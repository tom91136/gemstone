package net.kurobako.gemstone.isa.ptx

import Program.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.ptx.asm.*

/** Verifies the generated PTX instruction sum type and its colocated decoders. */
class InstructionSuite extends munit.FunSuite {
  test("typed modifiers preserve duplicate types, fixed suffix order and vendor syntax") {
    def instruction(source: String): Instruction = Parser.parseStatement(source) match {
      case Right(Statement.Instruction(value)) => value
      case other                               => fail(s"unexpected parse result: $other")
    }
    val conversion = instruction("cvt.rn.f32.f32 %d, %a;")
    assertEquals(conversion.mods, Vec(Modifier.Rn, Modifier.F32, Modifier.F32))
    val load = instruction("wmma.load.a.sync.aligned.row.m16n16k16.global.f16 {%r0}, [%p], 16;")
    assertEquals(
      load.fixedModifiers ++ load.mods,
      Vec(
        Modifier.Load,
        Modifier.A,
        Modifier.Sync,
        Modifier.Aligned,
        Modifier.Row,
        Modifier.M16n16k16,
        Modifier.Global,
        Modifier.F16
      )
    )
    val vendor = instruction("mov.vendor_extension.u32 %d, %a;")
    assertEquals(
      vendor.mods,
      Vec(
        Modifier.Unrecognised("vendor_extension".refine[NonEmpty[String]].fold(error => fail(error.raw), identity)),
        Modifier.U32
      )
    )
    assertEquals(instruction(Printer.render(vendor)), vendor)
    assert(Parser.parseStatement("mov..u32 %d, %a;").isLeft)
  }

  test("every specified opcode decodes directly to its generated instruction case") {
    val opcodes = Instruction.definitions.map(_.opcode).distinct.sorted

    assert(Instruction.definitions.nonEmpty)
    assertEquals(opcodes.size, 209)
    opcodes.foreach { opcode =>
      val forms =
        Instruction.definitions.filter(_.opcode == opcode).flatMap(_.meta.entries).flatMap(_.forms)
      def symbols(count: Int) = Vec.tabulate(count)(index => Operand.Symbol(s"operand$index"))
      val operands            = forms.map(_.operands.size).distinct.sorted(using Ordering.Int).map(symbols(_))
        .find(Instruction.decode(opcode, _).isRight).getOrElse(Vec.empty[Operand])
      val instruction = Instruction.decode(opcode, operands).fold(error => fail(s"$opcode: $error"), value => value)
      assertEquals(instruction.opcode, opcode)
      assert(instruction.operands.sameElements(operands))
      assertEquals(instruction.meta, instruction.definition.meta)
      val decoded = instruction.decoder(instruction.operands, instruction.mods, instruction.pred).toOption.get
      assertEquals(decoded.definition, instruction.definition)
      assert(decoded.operands.sameElements(instruction.operands))
    }
  }

  test("unknown opcodes and invalid arities fail to decode") {
    assertEquals(
      Instruction.decode("gemstone_unknown", Vec.empty),
      Left(Instruction.DecodeError.UnknownOpcode("gemstone_unknown"))
    )
    Instruction.decode("add", Vec(Operand.Symbol("destination"))) match {
      case Left(Instruction.DecodeError.InvalidOperandCount("add", expected, 1)) =>
        assertEquals(expected, Vec(3))
      case value => fail(s"unexpected result: $value")
    }
  }
}
