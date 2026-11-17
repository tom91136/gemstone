package net.kurobako.gemstone.isa.amdgpu

import cats.syntax.all.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.amdgpu.asm.Binary
import net.kurobako.gemstone.isa.amdgpu.asm.Disassembler
import net.kurobako.gemstone.isa.amdgpu.asm.Instruction
import net.kurobako.gemstone.isa.amdgpu.asm.Operand
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

  test("every target's blocks contain the control word it regenerates") {
    all.groupBy(_.processor).foreach { (processor, blocks) =>
      val word = if (blocks.head.target.isa.isGFX11OrLater) "s_delay_alu" else "s_nop"
      assert(blocks.exists(block => count(block.statements, word) > 0), s"$processor has no $word")
    }
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
}
