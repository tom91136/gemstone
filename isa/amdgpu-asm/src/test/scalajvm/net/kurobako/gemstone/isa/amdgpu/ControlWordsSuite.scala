package net.kurobako.gemstone.isa.amdgpu

import cats.syntax.all.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.amdgpu.asm.Binary
import net.kurobako.gemstone.isa.amdgpu.asm.Disassembler
import net.kurobako.gemstone.isa.amdgpu.asm.Instruction
import net.kurobako.gemstone.isa.amdgpu.asm.Operand
import net.kurobako.gemstone.isa.amdgpu.asm.Parser
import net.kurobako.gemstone.isa.amdgpu.asm.Printer
import net.kurobako.gemstone.isa.amdgpu.asm.Program
import net.kurobako.gemstone.isa.amdgpu.asm.RegisterFile
import net.kurobako.gemstone.testing.*
import scala.io.Source

class ControlWordsSuite extends Suite {
  private case class Compiled(kernel: String, processor: String, code: Vec[Byte], statements: Vec[Program.Statement]) {
    def name: String = s"$kernel.$processor"

    def target: Target = Target.all.find(_.raw == processor).getOrElse(fail(s"no target $processor"))
  }

  private def resource(name: String): Array[Byte] =
    Option(getClass.getResourceAsStream(s"controlwords/$name")).fold(fail(s"missing resource $name"))(stream =>
      try stream.readAllBytes()
      finally stream.close()
    )

  private def compiled(kernel: String, processor: String): Compiled = {
    val target  = Target.all.find(_.raw == processor).getOrElse(fail(s"no target $processor"))
    val outcome = for {
      image   <- CodeObject.decode(Vec.unsafeWrapArray(resource(s"$kernel.$processor.o"))).left.map(_.message.raw)
      code    <- ELF.index(image.image).flatMap(_.kernelCode(kernel)).left.map(_.message.raw)
      decoded <- Disassembler(TargetMode(target, TargetDefaults.waveSize(target))).decode(code).left.map(_.toString)
    } yield Compiled(kernel, processor, code, decoded.statements.filter(isInstruction))
    outcome.fold(error => fail(s"$kernel.$processor: $error"), identity)
  }

  private def isInstruction(statement: Program.Statement): Boolean = statement match {
    case Program.Statement.Instruction(_) | Program.Statement.InstructionPair(_, _) => true
    case _                                                                          => false
  }

  private lazy val all: Vector[Compiled] =
    Source.fromString(new String(resource("manifest.tsv"), "UTF-8")).getLines().filter(_.nonEmpty).map { line =>
      val fields = line.split('\t')
      compiled(fields(0), fields(1))
    }.toVector

  private def encode(target: Target, statements: Vec[Program.Statement]): Either[String, Vec[Byte]] = {
    val mode = TargetMode(target, TargetDefaults.waveSize(target))
    statements.toVector
      .traverse(statement => Binary.encodeStatement(statement, mode).left.map(_.toString))
      .map(parts => Vec.from(parts.flatMap(_.toVector)))
  }

  private def count(statements: Vec[Program.Statement], mnemonic: String): Int =
    statements.count {
      case Program.Statement.Instruction(value) => value.opcode.raw == mnemonic
      case _                                    => false
    }

  private def program(source: String): Vec[Program.Statement] =
    Parser.parse(source).fold(error => fail(error.toString), _.statements)

  private def nopImmediates(target: Target, statements: Vec[Program.Statement]): Vector[BigInt] =
    ControlWords.regenerate(statements, target).fold(error => fail(error), identity).toVector.flatMap {
      case Program.Statement.Instruction(value) if value.opcode.raw == "s_nop" =>
        value.operands.toVector.collect { case Operand.IntegerLiteral(immediate, _) => immediate }
      case _ => Vector.empty
    }

  private def mismatch(block: Compiled): Option[String] = {
    val outcome = for {
      regenerated <- ControlWords.regenerate(block.statements, block.target)
      bytes       <- encode(block.target, regenerated)
    } yield (regenerated, bytes)
    outcome match {
      case Left(error)                                         => Some(s"${block.name}: $error")
      case Right((_, bytes)) if bytes.sameElements(block.code) => None
      case Right((regenerated, _))                             =>
        Some(
          s"${block.name}:\nLLVM\n${Printer.render(Program(block.statements))}\nregenerated\n${Printer.render(Program(regenerated))}"
        )
    }
  }

  test("every compiled block regenerates to LLVM's bytes") {
    val failures = all.flatMap(mismatch)
    assertEquals(failures, Vector.empty[String], s"${all.size - failures.size} of ${all.size} blocks match")
  }

  test("every hazard rule fires on some compiled block") {
    val fired = all.flatMap(block =>
      ControlWords.hazards(block.statements, block.target).fold(error => fail(error), identity)
    ).toSet
    assertEquals(
      ControlWords.Hazard.values.toVector.filterNot(fired.contains),
      Vector.empty[ControlWords.Hazard],
      "hazard rules that fire on no compiled block"
    )
  }

  test("no s_delay_alu is regenerated on a target without the delay instruction") {
    all.filter(!_.target.isa.isGFX11OrLater).foreach { block =>
      val regenerated = ControlWords.regenerate(block.statements, block.target).fold(error => fail(error), identity)
      assertEquals(count(regenerated, "s_delay_alu"), 0, block.name)
    }
  }

  test("a dual-issue pair reports the scheduling record it lacks") {
    def vector(index: Int) = Operand.Register(RegisterFile.VectorRegister, index, index)
    val add                = Instruction.decode(
      "v_add_f32",
      Vec(vector(0), vector(1), vector(2))
    ).fold(error => fail(error.toString), identity)
    val target = Target.all.find(_.raw == "gfx1100").getOrElse(fail("no gfx1100"))
    assert(ControlWords.regenerate(Vec(Program.Statement.InstructionPair(add, add)), target).isLeft)
  }

  test("the older hazard rules add no s_nop on a target with the no-data-dependency model") {
    def vector(index: Int)                            = Operand.Register(RegisterFile.VectorRegister, index, index)
    def pair(first: Int)                              = Operand.Register(RegisterFile.VectorRegister, first, first + 1)
    def scalar(first: Int, last: Int)                 = Operand.Register(RegisterFile.Scalar, first, last)
    def statement(opcode: String, operands: Operand*) = Program.Statement.Instruction(
      Instruction.decode(opcode, Vec.from(operands)).fold(error => fail(error.toString), identity)
    )
    def compare(target: Target) = statement(
      "v_cmpx_eq_u32",
      (Option.when(target.isa.isGFX9OrEarlier)(Operand.SpecialRegister("vcc")).toVector ++
        Vector(vector(0), vector(1)))*
    )
    val sequences = Vector[(String, Target => Vector[Program.Statement])](
      "vmem reads an sgpr a valu wrote" ->
        (_ =>
          Vector(
            statement("v_readfirstlane_b32", scalar(4, 4), vector(0)),
            statement("global_load_dword", vector(1), vector(0), scalar(4, 5))
          )
        ),
      "valu overwrites a wide store" ->
        (_ =>
          Vector(
            statement("global_store_dwordx2", vector(0), pair(2), scalar(0, 1)),
            statement("v_mov_b32", vector(3), vector(2))
          )
        ),
      "vmem reads exec a valu wrote" ->
        (target =>
          Vector(
            compare(target),
            statement("global_load_dword", vector(2), vector(3), scalar(4, 5))
          )
        )
    )
    val gfx906  = Target.all.find(_.raw == "gfx906").getOrElse(fail("no gfx906"))
    val gfx1036 = Target.all.find(_.raw == "gfx1036").getOrElse(fail("no gfx1036"))
    sequences.foreach { (name, block) =>
      def nops(target: Target): Int =
        count(ControlWords.regenerate(Vec.from(block(target)), target).fold(error => fail(error), identity), "s_nop")
      assertEquals(nops(gfx1036), 0, name)
      assert(nops(gfx906) > 0, name)
    }
  }

  test("an FP atomic before s_denorm_mode owes three wait states on GFX10") {
    val gfx1036 = Target("gfx1036")
    val atomic  = "global_atomic_fmin v[0:1], v2, off"
    Vector(
      ""                                 -> Vector(BigInt(2)),
      "s_mov_b32 s0, 0"                  -> Vector(BigInt(1)),
      "s_mov_b32 s0, 0\ns_mov_b32 s1, 0" -> Vector(BigInt(0))
    ).foreach { (fillers, expected) =>
      val source = Vector(atomic, fillers, "s_denorm_mode 0").filter(_.nonEmpty).mkString("\n")
      assertEquals(nopImmediates(gfx1036, program(source)), expected, fillers)
    }
  }

  test("three SALU, a VALU or a waitcnt between an FP atomic and s_denorm_mode ends the hazard") {
    val gfx1036 = Target("gfx1036")
    val atomic  = "global_atomic_fmin v[0:1], v2, off"
    Vector(
      "s_mov_b32 s0, 0\ns_mov_b32 s1, 0\ns_mov_b32 s2, 0",
      "v_mov_b32 v3, 0",
      "s_waitcnt 0"
    ).foreach { filler =>
      assertEquals(nopImmediates(gfx1036, program(s"$atomic\n$filler\ns_denorm_mode 0")), Vector.empty[BigInt], filler)
    }
  }

  test("the FP atomic hazard is absent before GFX10") {
    val block = compiled("fp_atomic_window", "gfx1100")
    assertEquals(nopImmediates(block.target, block.statements), Vector.empty[BigInt], block.name)
  }

  test("a returning FP atomic printed without its destination has no encoding") {
    val gfx1036 = Target("gfx1036")
    assert(ControlWords.regenerate(program("global_atomic_fmin v4, v[0:1], v2, off glc"), gfx1036).isRight)
    assertEquals(
      ControlWords.regenerate(program("global_atomic_fmin v[0:1], v2, off glc"), gfx1036).left.toOption,
      Some("global_atomic_fmin has no encoding for gfx1036")
    )
  }
}
