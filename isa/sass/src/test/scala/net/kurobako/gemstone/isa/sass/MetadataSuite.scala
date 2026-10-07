package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.Targets.*
import net.kurobako.gemstone.isa.sass.asm.*

class MetadataSuite extends munit.FunSuite {
  test("modifier decoding preserves order, repetitions and unknown vendor spellings") {
    val instruction = Parser.parse("F2F.F32.F32 R0, R1;").fold(fail(_), identity)
    assertEquals(instruction.modifiers, Vec(Instruction.Modifier.F32, Instruction.Modifier.F32))
    assertEquals(Instruction.Modifier.fromName("F32"), Right(Instruction.Modifier.F32))
    assert(Instruction.Modifier.fromName("").isLeft)
    val vendor = Parser.parse("MOV.FUTURE R0, R1;").fold(fail(_), identity)
    assertEquals(vendor.modifiers, Vec(Instruction.Modifier.Vendor("FUTURE")))
    assertEquals(Printer.print(vendor), Right("MOV.FUTURE R0, R1;"))
  }

  test("targets sharing a description share decoder data") {
    assert(decoder(86) eq decoder(88))
    assert(decoder(89) ne decoder(86))
    assert(Target.parse("SM999").isLeft)
  }

  test("definitions are available without constructing an instruction") {
    val definitions = Instruction.Definition.forMnemonic("MOV")
    assert(definitions.nonEmpty)
    assert(definitions.forall(_.mnemonic == "MOV"))
    assert(Instruction.Definition.forMnemonic("NO_SUCH_INSTRUCTION").isEmpty)
    assertEquals(Instruction.Definition.all.map(_.name).distinct.size, Instruction.Definition.all.size)
    val registerForm = Instruction.MovRegRegDefinition
    Vec(35, 89).foreach { sm =>
      assert(decoder(sm).forms.exists(form =>
        registerForm.forms.contains(form.name) && form.opcodes.exists(_.name == "MOV")
      ))
    }
  }

  test("decoder expansion shares assignments across targets and expressions across assignments") {
    val sm80 = decoder(80)
    val sm89 = decoder(89)
    assertNotEquals(sm80.digest, sm89.digest)
    val fields      = sm80.forms.flatMap(_.fields)
    val otherFields = sm89.forms.flatMap(_.fields).map(field => field -> field).toMap
    val shared      = fields.find(otherFields.contains(_)).getOrElse(fail("no common assignment"))
    assert(shared eq otherFields(shared))

    val group = fields.groupBy(_.expression).values.find(_.distinct.size > 1)
      .getOrElse(fail("no expression used by distinct assignments"))
    val first  = group.head
    val second = group.find(_ != first).getOrElse(fail("missing distinct assignment"))
    assert(first.expression eq second.expression)
  }

  test("every definition decodes its own operand shape and no earlier definition shadows it") {
    val register = Operand.Register(RegisterBank.R, 1)
    val kinds    = Map[String, Operand](
      "Reg"     -> register,
      "Pred"    -> Operand.Predicate(Some(0), uniform = false, negated = false),
      "Imm"     -> Operand.Integer(1),
      "Address" -> Operand.Address(Some(Operand.Register(RegisterBank.R, 1)), None, 0, 1, None, None),
      "Const"   -> Operand.ConstantBank(0, Operand.Integer(0)),
      "Special" -> Operand.SpecialRegister(SpecialRegister.SR_TID_X),
      "Group"   -> Operand.Group(Vec(register, Operand.Register(RegisterBank.R, 2))),
      "Symbol"  -> Operand.Symbol("CC")
    )
    val shape = s"(.*?)(NoOperands|(?:${kinds.keys.mkString("|")})+)".r
    Instruction.Definition.all.foreach { definition =>
      val name: String = definition.name.raw
      val operands     = name match {
        case shape(prefix: String, suffix: String)
            if prefix.filter(_.isLetterOrDigit).equalsIgnoreCase(definition.mnemonic.raw.filter(_.isLetterOrDigit)) =>
          if (suffix == "NoOperands") Vec.empty[Operand]
          else Vec.from(s"(${kinds.keys.mkString("|")})".r.findAllIn(suffix)).map(kinds)
        case _ => fail(s"$name does not name its operand shape")
      }
      val instruction = definition.decode(Vec.empty, operands, None).getOrElse(fail(definition.name.raw))
      assert(instruction.definition eq definition, definition)
      assertEquals(instruction.operands, operands, definition)
      assertEquals(Instruction.decode(definition.mnemonic.raw, Vec.empty, operands, None), Right(instruction))
    }
  }

  test("MOV carries named checked operands and its shared definition") {
    val dst = InstructionOperand.Register.from(Operand.Register(RegisterBank.R, 1)).fold(fail(_), identity)
    val src = InstructionOperand.Register.from(Operand.Register(RegisterBank.R, 2)).fold(fail(_), identity)
    val instruction: Instruction.MovRegReg = Instruction.MovRegReg(dst, src)
    assertEquals(instruction.dst, dst)
    assertEquals(instruction.operands, Vec[Operand](dst, src))
    assert(instruction.definition eq Instruction.MovRegRegDefinition)
    assertEquals(Instruction.decode("MOV", Vec.empty, instruction.operands, None), Right(instruction))
    assert(Instruction.decode("MOV", Vec.empty, Vec.empty, None).isLeft)
    assert(Instruction.decode(
      "MOV",
      Vec.empty,
      Vec(Operand.Register(RegisterBank.R, 1), Operand.Predicate(None, false, false)),
      None
    ).isLeft)
  }
}
