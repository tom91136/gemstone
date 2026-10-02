package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
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

  test("target aliases share decoder data without erasing exact target identity") {
    val sm90  = Metadata.catalogue.targets.find(_.name == "SM90").getOrElse(fail("missing SM90"))
    val sm90a = Metadata.catalogue.targets.find(_.name == "SM90a").getOrElse(fail("missing SM90a"))
    assertNotEquals(sm90, sm90a)
    assertEquals(sm90.encodingKey, sm90a.encodingKey)
    assertEquals(Metadata.catalogue.targets.size, 36)
    assertEquals(Metadata.catalogue.targets.map(_.encodingKey).distinct.size, 16)
    assertEquals(Metadata.catalogue.targets.find(_.name == "SM999"), None)
  }

  test("definitions are available without constructing an instruction") {
    val definitions = Instruction.Definition.forMnemonic("MOV")
    assert(definitions.nonEmpty)
    assert(definitions.forall(_.mnemonic == "MOV"))
    assert(Instruction.Definition.forMnemonic("NO_SUCH_INSTRUCTION").isEmpty)
    assertEquals(Instruction.Definition.all.map(_.name).distinct.size, Instruction.Definition.all.size)
    val registerForm = Instruction.MovRegRegDefinition
    assertEquals(
      registerForm.slots.map(_.kind),
      Vec(Instruction.OperandKind.Register, Instruction.OperandKind.Register)
    )
    Vec("SM35", "SM89").foreach { target =>
      val database = NVDisasm.forTarget(target).getOrElse(fail(s"missing $target metadata"))
      assert(database.forms.exists(form =>
        registerForm.forms.contains(form.name) && form.opcodes.exists(_.name == "MOV")
      ))
    }
  }

  test("decoder expansion shares assignments across targets and expressions across assignments") {
    val sm80 = NVDisasm.forTarget("SM80").getOrElse(fail("missing SM80 decoder"))
    val sm89 = NVDisasm.forTarget("SM89").getOrElse(fail("missing SM89 decoder"))
    assertNotEquals(sm80.sha256, sm89.sha256)
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

  test("every definition constructs its own closed instruction case") {
    def sample(kind: Instruction.OperandKind): Operand = kind match {
      case Instruction.OperandKind.Register  => Operand.Register(RegisterBank.R, 1)
      case Instruction.OperandKind.Predicate => Operand.Predicate(Some(0), uniform = false, negated = false)
      case Instruction.OperandKind.Immediate => Operand.Integer(1)
      case Instruction.OperandKind.Address   =>
        Operand.Address(Some(Operand.Register(RegisterBank.R, 1)), None, 0, 1, None, None)
      case Instruction.OperandKind.ConstantBank    => Operand.ConstantBank(0, Operand.Integer(0))
      case Instruction.OperandKind.SpecialRegister => Operand.SpecialRegister(SpecialRegister.SR_TID_X)
      case Instruction.OperandKind.Group           =>
        Operand.Group(Vec(Operand.Register(RegisterBank.R, 1), Operand.Register(RegisterBank.R, 2)))
      case Instruction.OperandKind.Symbol => Operand.Symbol("CC")
    }
    Instruction.Definition.all.foreach { definition =>
      val operands    = definition.slots.map(slot => sample(slot.kind))
      val instruction = definition.decode(Vec.empty, operands, None).getOrElse(fail(definition.name.raw))
      assert(instruction.definition eq definition)
      assertEquals(instruction.operands, operands)
      assertEquals(instruction.mnemonic, definition.mnemonic)
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
