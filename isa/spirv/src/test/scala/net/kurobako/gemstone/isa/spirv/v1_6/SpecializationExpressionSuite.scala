package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.spirv.v1_6.execution.*

class SpecializationExpressionSuite extends munit.FunSuite {
  private def ref(n: Int): Operand = operand("IdRef", n)
  private def lit(n: Int): Operand = operand("LiteralInteger", n)
  private val types                = Vec(
    Instruction.TypeBool(ref(1)),
    Instruction.TypeInt(ref(2), lit(32), lit(1)),
    Instruction.TypeInt(ref(3), lit(64), lit(1)),
    Instruction.TypeFloat(ref(4), lit(32), None),
    Instruction.TypeFloat(ref(5), lit(64), None),
    Instruction.TypeVector(ref(6), ref(2), lit(2)),
    Instruction.TypeVector(ref(7), ref(1), lit(2)),
    Instruction.TypeStruct(ref(8), Vec(ref(2), ref(6)))
  )
  private def constant(t: Int, id: Int, bits: Long): Instruction =
    Instruction.SpecConstant(
      ref(t),
      ref(id),
      if (t == 3 || t == 5) operand("LiteralContextDependentNumber", bits.toInt, (bits >>> 32).toInt)
      else operand("LiteralContextDependentNumber", bits.toInt)
    )
  private def evaluate(declarations: Vec[Instruction], t: Int, opcode: Int, args: Int*): Vec[Long] = {
    val module = Module(
      Header(ISA.version, Word(0), Word(100), Word(0)),
      types ++ declarations ++ Vec(
        Instruction.SpecConstantOp(
          ref(t),
          ref(99),
          operand("LiteralSpecConstantOpInteger", opcode),
          Vec.from(args.map(ref(_)))
        )
      ),
      Endianness.Little
    )
    VMCompiler.compileUnchecked(module).fold(e => fail(e.message), identity).constantData(99) match {
      case VMIR.Constant.Bits(_, lanes) => lanes
      case other                        => fail(s"unexpected constant $other")
    }
  }
  test("specialization comparisons respect opcode signedness at both widths") {
    for (t <- Seq(2, 3)) {
      val values = Vec(constant(t, 10, -1), constant(t, 11, 1))
      for (
        (op, expected) <- Seq(
          170 -> 0L,
          171 -> 1L,
          172 -> 1L,
          173 -> 0L,
          174 -> 1L,
          175 -> 0L,
          176 -> 0L,
          177 -> 1L,
          178 -> 0L,
          179 -> 1L
        )
      )
        assertEquals(evaluate(values, 1, op, 10, 11), Vec(expected), s"type=$t opcode=$op")
    }
  }
  test("boolean specialization expressions and vector select") {
    val values = Vec(Instruction.SpecConstantTrue(ref(1), ref(10)), Instruction.SpecConstantFalse(ref(1), ref(11)))
    for ((op, expected) <- Seq(164 -> 0L, 165 -> 1L, 166 -> 1L, 167 -> 0L))
      assertEquals(evaluate(values, 1, op, 10, 11), Vec(expected))
    assertEquals(evaluate(values, 1, 168, 11), Vec(1L))
    val vectors = values ++ Vec(
      constant(2, 12, 9),
      constant(2, 13, 7),
      Instruction.SpecConstantComposite(ref(7), ref(14), Vec(ref(10), ref(11))),
      Instruction.SpecConstantComposite(ref(6), ref(15), Vec(ref(12), ref(13))),
      Instruction.SpecConstantComposite(ref(6), ref(16), Vec(ref(13), ref(12)))
    )
    assertEquals(evaluate(vectors, 6, 169, 14, 15, 16), Vec(9L, 9L))
  }
  test("specialization conversions extend the source sign and convert floating values") {
    assertEquals(evaluate(Vec(constant(2, 10, 0xffffffffL)), 3, 114, 10), Vec(-1L))
    assertEquals(evaluate(Vec(constant(2, 10, 0xffffffffL)), 3, 113, 10), Vec(0xffffffffL))
    assertEquals(evaluate(Vec(constant(3, 10, 0x100000003L)), 2, 114, 10), Vec(3L))
    assertEquals(
      evaluate(Vec(constant(4, 10, 0x3fc00000L)), 5, 115, 10),
      Vec(java.lang.Double.doubleToRawLongBits(1.5))
    )
    assertEquals(
      evaluate(Vec(constant(5, 10, java.lang.Double.doubleToRawLongBits(-2.5))), 4, 115, 10),
      Vec(0xc0200000L)
    )
  }
  test("specialization extract and insert follow nested composite types") {
    val values = Vec(
      constant(2, 10, 9),
      constant(2, 11, 7),
      Instruction.SpecConstantComposite(ref(6), ref(12), Vec(ref(10), ref(11))),
      Instruction.SpecConstantComposite(ref(8), ref(13), Vec(ref(11), ref(12)))
    )
    assertEquals(evaluate(values, 6, 81, 13, 1), Vec(9L, 7L))
    assertEquals(evaluate(values, 2, 81, 13, 1, 0), Vec(9L))
    assertEquals(evaluate(values, 8, 82, 11, 13, 1, 0), Vec(7L, 7L, 7L))
    assertEquals(evaluate(values, 6, 79, 12, 12, 3, 0), Vec(7L, 9L))
  }
  test("quantization preserves normal half values and flushes all smaller magnitudes") {
    for (
      (input, expected) <- Seq(
        0x3f800001L -> 0x3f800000L,
        0x38800000L -> 0x38800000L,
        0x387fffffL -> 0L,
        0xb87fffffL -> 0x80000000L,
        0x80000000L -> 0x80000000L,
        0x7f800000L -> 0x7f800000L,
        0xff800000L -> 0xff800000L,
        0x47800000L -> 0x7f800000L
      )
    )
      assertEquals(evaluate(Vec(constant(4, 10, input)), 4, 116, 10), Vec(expected))
    val nan = evaluate(Vec(constant(4, 10, 0x7fc00001L)), 4, 116, 10).head
    assert(java.lang.Float.intBitsToFloat(nan.toInt).isNaN)
  }
  test("decoration groups apply variable and member metadata before compilation") {
    val declarations = Vec(
      Instruction.Decorate(ref(20), operand("Decoration", Decoration.DescriptorSet, 3)),
      Instruction.Decorate(ref(21), operand("Decoration", Decoration.Offset, 16)),
      Instruction.DecorationGroup(ref(20)),
      Instruction.DecorationGroup(ref(21)),
      Instruction.GroupDecorate(ref(20), Vec(ref(30))),
      Instruction.GroupMemberDecorate(ref(21), Vec(operand("PairIdRefLiteralInteger", 8, 1))),
      Instruction.Decorate(ref(30), operand("Decoration", Decoration.Binding, 7)),
      Instruction.TypePointer(ref(29), operand("StorageClass", StorageClass.Uniform), ref(8)),
      Instruction.Variable(ref(29), ref(30), operand("StorageClass", StorageClass.Uniform), None)
    )
    val module   = Module(Header(ISA.version, Word(0), Word(40), Word(0)), types ++ declarations, Endianness.Little)
    val compiled = VMCompiler.compileUnchecked(ISA.decode(ISA.encode(module).toOption.get).toOption.get).fold(
      e => fail(e.message),
      identity
    )
    assertEquals((compiled.descriptorBindings(30)._1.raw, compiled.descriptorBindings(30)._2.raw), (3, 7))
    assertEquals(compiled.memberOffsets((8, 1)), 16)
  }

}
