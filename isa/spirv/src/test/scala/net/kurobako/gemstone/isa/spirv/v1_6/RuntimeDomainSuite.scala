package net.kurobako.gemstone.isa.spirv.v1_6

import execution.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*
import scala.compiletime.testing.typeCheckErrors

class RuntimeDomainSuite extends munit.FunSuite {
  test("pointer leaf offsets retain nonnegative input and output contracts") {
    assertEquals(
      typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      import net.kurobako.gemstone.collection.*
      import net.kurobako.gemstone.vm.Fault
      def offsets(layout: VMTypeLayout): Either[Fault, Vec[(Long >= 0, Int)]] =
        layout.pointerLeafOffsets(1, 0L)
    """),
      Nil
    )
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      def invalid(layout: VMTypeLayout) = layout.pointerLeafOffsets(1, -1L)
    """).nonEmpty)
  }

  test("pointer leaf traversal rejects overflowing array and structure offsets") {
    val ir = VMCompiler.compileUnchecked(module(
      Instruction.TypeInt(ref(1), lit(32), lit(0)),
      Instruction.TypePointer(ref(2), operand(OperandKind.StorageClass, StorageClass.CrossWorkgroup), ref(1)),
      Instruction.Constant(ref(1), ref(3), operand(OperandKind.LiteralContextDependentNumber, 2)),
      Instruction.TypeArray(ref(4), ref(2), ref(3)),
      Instruction.TypeStruct(ref(5), Vec(ref(1), ref(4)))
    )).fold(error => fail(error.message), identity)
    val layout  = VMTypeLayout(ir)
    val largest = layout.pointerLeafOffsets(4, Long.MaxValue - 8L)
    assertEquals(largest.map(_.map(_._1.raw)), Right(Vec(Long.MaxValue - 8L, Long.MaxValue)))
    assertEquals(layout.pointerLeafOffsets(5, 16L).map(_.map(_._1.raw)), Right(Vec(24L, 32L)))
    assert(layout.pointerLeafOffsets(4, Long.MaxValue).isLeft)
    assert(layout.pointerLeafOffsets(5, Long.MaxValue - 8L).isLeft)
    assert(layout.pointerLeafOffsets(5, Long.MaxValue).isLeft)
  }

  test("matrix storage offsets preserve nonnegative evidence") {
    assertEquals(
      typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      def offsets(layout: VMTypeLayout): Vector[(Long >= 0, Int)] =
        layout.matrixStorageLeaves(1, None)
    """),
      Nil
    )
  }

  test("matrix offset refinements survive cached row and column layouts") {
    val ir = VMCompiler.compileUnchecked(module(
      Instruction.TypeFloat(ref(1), lit(32), None),
      Instruction.TypeVector(ref(2), ref(1), lit(3)),
      Instruction.TypeMatrix(ref(3), ref(2), lit(2))
    )).fold(error => fail(error.message), identity)
    val layout = VMTypeLayout(ir)
    for (
      (rowMajor, expected) <- Seq(
        true  -> Vector(0L, 16L, 32L, 4L, 20L, 36L),
        false -> Vector(0L, 4L, 8L, 16L, 20L, 24L)
      )
    ) {
      val storage                          = Some(VMIR.MatrixLayout(16, rowMajor))
      val first: Vector[(Long >= 0, Int)]  = layout.matrixStorageLeaves(3, storage)
      val cached: Vector[(Long >= 0, Int)] = layout.matrixStorageLeaves(3, storage)
      assertEquals(first.map(_._1.raw), expected)
      assertEquals(cached, first)
    }
  }

  test("leaf counts preserve nonnegative evidence through IR and layout") {
    assertEquals(
      typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      import net.kurobako.gemstone.vm.Fault
      val count: Either[Fault, Int >= 0] = VMIR.scalarLeafCount(Map.empty, 1)
      def cached(layout: VMTypeLayout): Either[Fault, Int >= 0] = layout.scalarLeafCount(1)
    """),
      Nil
    )
  }

  private def module(instructions: Instruction*): Module = Module(
    Header(ISA.version, Word(0), Word(32), Word(0)),
    Vec.from(instructions),
    Endianness.Little
  )
  private def ref(value: Int): Operand = operand(OperandKind.IdResult, value)
  private def lit(value: Int): Operand = operand(OperandKind.LiteralInteger, value)

  test("large array leaf counts are rejected before constant construction") {
    val instructions = Seq(
      Instruction.TypeInt(ref(1), lit(64), lit(0)),
      Instruction.TypeVector(ref(2), ref(1), lit(4)),
      Instruction.Constant(ref(1), ref(3), operand(OperandKind.LiteralContextDependentNumber, 0, 0x40000000)),
      Instruction.TypeArray(ref(4), ref(2), ref(3))
    )
    val ir = VMCompiler.compileUnchecked(module(instructions*)).fold(error => fail(error.message), identity)
    assert(VMIR.scalarLeafCount(ir.types, 4).isLeft)
    assert(VMCompiler.compileUnchecked(module((instructions :+ Instruction.ConstantNull(ref(4), ref(5)))*)).isLeft)
  }

  test("leaf-count products preserve zero and the largest supported result") {
    import VMIR.*
    def leaves(count: Long >= 0): Either[Fault, Int >= 0] = VMIR.scalarLeafCount(
      Map(
        1 -> RuntimeType.Integer(64, false),
        2 -> RuntimeType.VectorType(1, 4),
        3 -> RuntimeType.Array(2, count)
      ),
      3
    )
    assertEquals(leaves(0L).map(_.raw), Right(0))
    assertEquals(leaves(1L).map(_.raw), Right(4))
    // Largest array length whose four scalar leaves fit in an Int.
    assertEquals(leaves(536870911L).map(_.raw), Right(Int.MaxValue - 3))
    assert(leaves(536870912L).isLeft)
    assert(leaves(Long.MaxValue).isLeft)
    assertEquals(
      VMIR.scalarLeafCount(
        Map(
          1 -> RuntimeType.Structure(Vec.empty),
          2 -> RuntimeType.Array(1, Long.MaxValue)
        ),
        2
      ).map(_.raw),
      Right(0)
    )
  }

  test("nested offsets are checked before decoding bytes") {
    val ir = VMCompiler.compileUnchecked(module(
      Instruction.TypeInt(ref(1), lit(64), lit(0)),
      Instruction.TypeStruct(ref(2), Vec(ref(1))),
      Instruction.TypeStruct(ref(3), Vec(ref(2))),
      Instruction.TypeStruct(ref(4), Vec(ref(3))),
      Instruction.MemberDecorate(ref(2), lit(0), operand(OperandKind.Decoration, Decoration.Offset, 16)),
      Instruction.MemberDecorate(ref(3), lit(0), operand(OperandKind.Decoration, Decoration.Offset, Int.MaxValue - 7)),
      Instruction.MemberDecorate(ref(4), lit(0), operand(OperandKind.Decoration, Decoration.Offset, Int.MaxValue - 7))
    )).fold(error => fail(error.message), identity)
    val runtime = VMProgramRuntime(ir)
    assert(runtime.decode(4, LittleEndian.toArray(42L, 8)).isLeft)
    assert(runtime.encode(VMValue.Bits(4, Array(42L))).isLeft)
  }

  test("structure size covers every explicit member offset") {
    val ir = VMCompiler.compileUnchecked(module(
      Instruction.TypeInt(ref(1), lit(64), lit(0)),
      Instruction.TypeStruct(ref(2), Vec(ref(1), ref(1))),
      Instruction.MemberDecorate(ref(2), lit(0), operand(OperandKind.Decoration, Decoration.Offset, 16)),
      Instruction.MemberDecorate(ref(2), lit(1), operand(OperandKind.Decoration, Decoration.Offset, 0))
    )).fold(error => fail(error.message), identity)
    val layout = VMTypeLayout(ir)
    assertEquals(layout.typeSize(2).raw, 24)
    val runtime = VMProgramRuntime(ir)
    val encoded = runtime.encode(VMValue.Bits(2, Array(1L, 2L))).fold(error => fail(error.message), identity)
    assertEquals(encoded.length, 24)
    runtime.decode(2, encoded).fold(error => fail(error.message), identity) match {
      case VMValue.Bits(_, lanes) => assertEquals(lanes.toSeq, Seq(1L, 2L))
      case _                      => fail("expected structure bits")
    }
  }

  test("implicit members follow previous explicit offsets in both sizing and encoding") {
    val ir = VMCompiler.compileUnchecked(module(
      Instruction.TypeInt(ref(1), lit(64), lit(0)),
      Instruction.TypeStruct(ref(2), Vec(ref(1), ref(1))),
      Instruction.MemberDecorate(ref(2), lit(0), operand(OperandKind.Decoration, Decoration.Offset, 16))
    )).fold(error => fail(error.message), identity)
    val layout = VMTypeLayout(ir)
    assertEquals(layout.typeSize(2).raw, 32)
    assertEquals(layout.structMemberOffset(2, Vec(1, 1), 1).raw, 24)
    val runtime = VMProgramRuntime(ir)
    val bytes   = runtime.encode(VMValue.Bits(2, Array(1L, 2L))).fold(error => fail(error.message), identity)
    assertEquals(LittleEndian.decode(bytes, 24, 8), 2L)
  }

  test("aggregate decoding returns a fault on offset overflow") {
    val ir = VMCompiler.compileUnchecked(module(
      Instruction.TypeInt(ref(1), lit(64), lit(0)),
      Instruction.Constant(ref(1), ref(3), operand(OperandKind.LiteralContextDependentNumber, 3, 0)),
      Instruction.TypeArray(ref(2), ref(1), ref(3)),
      Instruction.Decorate(ref(2), operand(OperandKind.Decoration, Decoration.ArrayStride, Int.MaxValue))
    )).fold(error => fail(error.message), identity)
    assert(VMProgramRuntime(ir).decode(2, Array.empty[Byte]).isLeft)
  }

  test("aggregate encoding returns a fault on size overflow") {
    val ir = VMCompiler.compileUnchecked(module(
      Instruction.TypeInt(ref(1), lit(64), lit(0)),
      Instruction.Constant(ref(1), ref(3), operand(OperandKind.LiteralContextDependentNumber, 0x40000000, 0)),
      Instruction.TypeArray(ref(2), ref(1), ref(3))
    )).fold(error => fail(error.message), identity)
    assert(VMProgramRuntime(ir).encode(VMValue.Bits(2, Array.empty[Long])).isLeft)
  }

  test("integer storage retains byte alignment and round-trips every supported width") {
    for (bits <- 8 to 64 by 8) {
      val ir = VMCompiler.compileUnchecked(module(
        Instruction.TypeInt(ref(1), lit(bits), lit(0))
      )).fold(error => fail(error.message), identity)
      val layout                = VMTypeLayout(ir)
      val width: ScalarBitWidth = layout.scalarWidth(1)
      val bytes: Int in 1 ~ 8   = width.toScalarByteWidth
      assertEquals(bytes.raw, bits / 8)
      assertEquals(layout.scalarMemoryWidth(1).map(_.raw), Right(bits / 8))
      ir.types(1) match {
        case VMIR.RuntimeType.Integer(stored, _) =>
          val aligned: Int where MultipleOf[8] = stored.widen[Int where MultipleOf[8]]
          assertEquals(aligned.raw, bits)
        case _ => fail("expected integer type")
      }
      val runtime  = VMProgramRuntime(ir)
      val expected = if (bits == 64) -1L else (1L << bits) - 1L
      val encoded  = runtime.encode(VMValue.Bits(1, Array(expected))).fold(error => fail(error.message), identity)
      assertEquals(encoded.length, bytes.raw)
      runtime.decode(1, encoded).fold(error => fail(error.message), identity) match {
        case VMValue.Bits(_, lanes) => assertEquals(lanes.toSeq, Seq(expected))
        case _                      => fail("expected integer bits")
      }
    }
    for (bits <- Seq(0, 1, 7, 12, 65))
      assert(VMCompiler.compileUnchecked(module(
        Instruction.TypeInt(ref(1), lit(bits), lit(0))
      )).isLeft)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      VMIR.RuntimeType.Integer(12, false)
    """).exists(_.message.contains("does not satisfy")))
  }

  test("layout preserves vector padding, fixed arrays and the runtime-array zero sentinel") {
    val ir = VMCompiler.compileUnchecked(module(
      Instruction.TypeInt(ref(1), lit(32), lit(0)),
      Instruction.TypeVector(ref(2), ref(1), lit(3)),
      Instruction.Constant(ref(1), ref(3), operand(OperandKind.LiteralContextDependentNumber, 5)),
      Instruction.TypeArray(ref(4), ref(1), ref(3)),
      Instruction.TypeRuntimeArray(ref(5), ref(1))
    )).fold(error => fail(error.message), identity)
    val layout = VMTypeLayout(ir)
    assertEquals(layout.typeSize(1).raw, 4)
    assertEquals(layout.typeSize(2).raw, 16)
    assertEquals(layout.typeSize(4).raw, 20)
    assertEquals(layout.typeSize(5).raw, 0)
    assertEquals(layout.typeAlignment(2).raw, 16)
    assertEquals(layout.typeSize(2).raw, 16)
    assertEquals(layout.alignUp(17, 16).raw, 32)
    intercept[ArithmeticException](layout.alignUp(Int.MaxValue, 16))
  }

  test("negative fixed-array lengths are rejected before layout") {
    val result = VMCompiler.compileUnchecked(module(
      Instruction.TypeInt(ref(1), lit(64), lit(1)),
      Instruction.Constant(ref(1), ref(2), operand(OperandKind.LiteralContextDependentNumber, -1, -1)),
      Instruction.TypeArray(ref(3), ref(1), ref(2))
    ))
    assert(result.left.exists(_.message.contains("array length -1 is invalid")))
  }

  test("zero array strides are rejected at the decoration boundary") {
    val result = VMCompiler.compileUnchecked(module(
      Instruction.Decorate(ref(1), operand(OperandKind.Decoration, Decoration.ArrayStride, 0))
    ))
    assert(result.left.exists(_.message.contains("array stride")))
  }

  test("matrix decorations retain the checked stride through layout") {
    val ir = VMCompiler.compileUnchecked(module(
      Instruction.TypeFloat(ref(1), lit(32), None),
      Instruction.TypeVector(ref(2), ref(1), lit(2)),
      Instruction.TypeMatrix(ref(3), ref(2), lit(3)),
      Instruction.TypeStruct(ref(4), Vec(ref(3))),
      Instruction.MemberDecorate(ref(4), lit(0), operand(OperandKind.Decoration, Decoration.MatrixStride, 16)),
      Instruction.MemberDecorate(ref(4), lit(0), operand(OperandKind.Decoration, Decoration.RowMajor))
    )).fold(error => fail(error.message), identity)
    val layout = VMTypeLayout(ir)
    val matrix = layout.memberMatrixLayouts((4, 0))
    assertEquals(matrix.stride.raw, 16)
    assert(matrix.rowMajor)
    assertEquals(layout.typeSize(4).raw, 32)
    val invalid = VMCompiler.compileUnchecked(module(
      Instruction.MemberDecorate(ref(4), lit(0), operand(OperandKind.Decoration, Decoration.MatrixStride, 0))
    ))
    assert(invalid.left.exists(_.message.contains("matrix stride")))
  }

  test("metrics retain saturation and dimensions across merges") {
    val first = VMMetrics()
    first.recordDispatch(collective = true)
    first.recordInvocation()
    first.recordInstruction()
    first.recordInvocationValues(5, 16)
    first.recordElapsed(Long.MaxValue)
    val second = VMMetrics()
    second.recordElapsed(1)
    second.merge(first)
    val snapshot = second.snapshot
    assertEquals(snapshot.dispatches.raw, 1L)
    assertEquals(snapshot.collectiveDispatches.raw, 1L)
    assertEquals(snapshot.invocations.raw, 1L)
    assertEquals(snapshot.instructions.raw, 1L)
    assertEquals(snapshot.invocationValueInstances.raw, 1L)
    assertEquals(snapshot.denseValueSlots.raw, 5L)
    assertEquals(snapshot.estimatedDenseValueBytes.raw, 80L)
    assertEquals(snapshot.elapsedNanos.raw, Long.MaxValue)
  }
}
