package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import scala.compiletime.testing.typeCheckErrors

/** Layout queries address types by numeric ID, so these modules spell their IDs numerically. */
class TypeLayoutSuite extends munit.FunSuite {
  private def module(declarations: String)(using munit.Location): Module = assemble(s"$openCL\n$declarations")

  private def program(declarations: String)(using munit.Location): Program = load(module(declarations)).value

  for (base <- Seq(0, 1000001))
    test(s"scalar leaves preserve nested aggregates, empty structures and errors with type base $base") {
      def id(offset: Int): String = s"%${base + offset}"
      val layout                  = program(s"""
        ${id(1)} = OpTypeInt 32 0
        ${id(2)} = OpTypeVector ${id(1)} 3
        ${id(3)} = OpConstant ${id(1)} 2
        ${id(4)} = OpTypeArray ${id(2)} ${id(3)}
        ${id(5)} = OpTypePointer CrossWorkgroup ${id(4)}
        ${id(6)} = OpTypeStruct ${id(2)} ${id(4)} ${id(5)}
        ${id(7)} = OpTypeStruct
        ${id(8)} = OpTypeVoid
      """)
      assertEquals(layout.scalarLeafCount(base + 6), Right[Fault, Int >= 0](10))
      assertEquals(layout.scalarLeafCount(base + 7), Right[Fault, Int >= 0](0))
      assert(layout.scalarLeafCount(base + 8).isLeft)
      assert(layout.scalarLeafCount(base + 10).isLeft)
      assert(layout.scalarLeafCount(-1).isLeft)
    }

  test("pointer leaf traversal rejects overflowing array and structure offsets") {
    val layout = program("""
      %1 = OpTypeInt 32 0
      %2 = OpTypePointer CrossWorkgroup %1
      %3 = OpConstant %1 2
      %4 = OpTypeArray %2 %3
      %5 = OpTypeStruct %1 %4
    """)
    val largest: Either[Fault, Vec[(Long >= 0, Int)]] = layout.pointerLeafOffsets(4, Long.MaxValue - 8L)
    assertEquals(largest.map(_.map(_._1.raw)), Right(Vec(Long.MaxValue - 8L, Long.MaxValue)))
    assertEquals(layout.pointerLeafOffsets(5, 16L).map(_.map(_._1.raw)), Right(Vec(24L, 32L)))
    assert(layout.pointerLeafOffsets(4, Long.MaxValue).isLeft)
    assert(layout.pointerLeafOffsets(5, Long.MaxValue - 8L).isLeft)
    assert(layout.pointerLeafOffsets(5, Long.MaxValue).isLeft)
  }

  test("matrix storage offsets follow row and column major layouts") {
    val layout = program("""
      %1 = OpTypeFloat 32
      %2 = OpTypeVector %1 3
      %3 = OpTypeMatrix %2 2
    """)
    for (
      (rowMajor, expected) <- Seq(
        true  -> Vector(0L, 16L, 32L, 4L, 20L, 36L),
        false -> Vector(0L, 4L, 8L, 16L, 20L, 24L)
      )
    ) {
      val storage                          = Some(VMMemory.MatrixLayout(16, rowMajor))
      val first: Vector[(Long >= 0, Int)]  = layout.matrixStorageLeaves(3, storage)
      val second: Vector[(Long >= 0, Int)] = layout.matrixStorageLeaves(3, storage)
      assertEquals(first.map(_._1.raw), expected)
      assertEquals(second, first)
    }
  }

  test("large array leaf counts are rejected before constant construction") {
    val declarations = """
      %1 = OpTypeInt 64 0
      %2 = OpTypeVector %1 4
      %3 = OpConstant %1 0x4000000000000000
      %4 = OpTypeArray %2 %3
    """
    assert(program(declarations).scalarLeafCount(4).isLeft)
    assert(load(module(s"$declarations\n%5 = OpConstantNull %4")).isLeft)
  }

  test("leaf-count products preserve zero and the largest supported result") {
    def leaves(count: Long >= 0): Either[Fault, Int >= 0] = VMProgram.scalarLeafCount(
      Map(
        1 -> VMProgram.RuntimeType.Integer(64, false),
        2 -> VMProgram.RuntimeType.VectorType(1, 4),
        3 -> VMProgram.RuntimeType.Array(2, count)
      ),
      3
    )
    assertEquals(leaves(0L).map(_.raw), Right(0))
    assertEquals(leaves(1L).map(_.raw), Right(4))
    // largest array length whose four scalar leaves fit in an Int
    assertEquals(leaves(536870911L).map(_.raw), Right(Int.MaxValue - 3))
    assert(leaves(536870912L).isLeft)
    assert(leaves(Long.MaxValue).isLeft)
    assertEquals(
      VMProgram.scalarLeafCount(
        Map(1 -> VMProgram.RuntimeType.Structure(Vec.empty), 2 -> VMProgram.RuntimeType.Array(1, Long.MaxValue)),
        2
      )
        .map(_.raw),
      Right(0)
    )
  }

  test("nested offsets are checked before decoding bytes") {
    val layout = program("""
      %1 = OpTypeInt 64 0
      %2 = OpTypeStruct %1
      %3 = OpTypeStruct %2
      %4 = OpTypeStruct %3
      OpMemberDecorate %2 0 Offset 16
      OpMemberDecorate %3 0 Offset 2147483640
      OpMemberDecorate %4 0 Offset 2147483640
    """)
    assert(layout.decode(4, LittleEndian.toArray(42L, 8)).isLeft)
    assert(layout.encode(VMValue.Bits(4, Array(42L))).isLeft)
  }

  test("structure size covers every explicit member offset") {
    val layout = program("""
      %1 = OpTypeInt 64 0
      %2 = OpTypeStruct %1 %1
      OpMemberDecorate %2 0 Offset 16
      OpMemberDecorate %2 1 Offset 0
    """)
    assertEquals(layout.typeSize(2).raw, 24)
    val encoded = layout.encode(VMValue.Bits(2, Array(1L, 2L))).value
    assertEquals(encoded.length, 24)
    layout.decode(2, encoded).value match {
      case VMValue.Bits(_, lanes) => assertEquals(lanes.toSeq, Seq(1L, 2L))
      case other                  => fail(s"expected structure bits, got $other")
    }
  }

  test("implicit members follow previous explicit offsets in both sizing and encoding") {
    val layout = program("""
      %1 = OpTypeInt 64 0
      %2 = OpTypeStruct %1 %1
      OpMemberDecorate %2 0 Offset 16
    """)
    assertEquals(layout.typeSize(2).raw, 32)
    assertEquals(layout.structMemberOffset(2, Vec(1, 1), 1).raw, 24)
    assertEquals(LittleEndian.decode(layout.encode(VMValue.Bits(2, Array(1L, 2L))).value, 24, 8), 2L)
  }

  test("aggregate decoding returns a fault on offset overflow") {
    val layout = program("""
      %1 = OpTypeInt 64 0
      %3 = OpConstant %1 3
      %2 = OpTypeArray %1 %3
      OpDecorate %2 ArrayStride 2147483647
    """)
    assert(layout.decode(2, Array.empty[Byte]).isLeft)
  }

  test("aggregate encoding returns a fault on size overflow") {
    val layout = program("""
      %1 = OpTypeInt 64 0
      %3 = OpConstant %1 0x40000000
      %2 = OpTypeArray %1 %3
    """)
    assert(layout.encode(VMValue.Bits(2, Array.empty[Long])).isLeft)
  }

  test("integer storage retains byte alignment and round-trips every supported width") {
    for (bits <- 8 to 64 by 8) {
      val layout                = program(s"%1 = OpTypeInt $bits 0")
      val width: ScalarBitWidth = layout.scalarWidth(1)
      val bytes: Int in 1 ~ 8   = width.toScalarByteWidth
      assertEquals(bytes.raw, bits / 8)
      assertEquals(layout.scalarMemoryWidth(1).map(_.raw), Right(bits / 8))
      layout.runtimeType(1) match {
        case Some(VMProgram.RuntimeType.Integer(stored, _)) =>
          val aligned: Int where MultipleOf[8] = stored.widen[Int where MultipleOf[8]]
          assertEquals(aligned.raw, bits)
        case other => fail(s"expected integer type, got $other")
      }
      val expected = if (bits == 64) -1L else (1L << bits) - 1L
      val encoded  = layout.encode(VMValue.Bits(1, Array(expected))).value
      assertEquals(encoded.length, bytes.raw)
      layout.decode(1, encoded).value match {
        case VMValue.Bits(_, lanes) => assertEquals(lanes.toSeq, Seq(expected))
        case other                  => fail(s"expected integer bits, got $other")
      }
    }
    for (bits <- Seq(1, 7, 12, 65)) assert(load(module(s"%1 = OpTypeInt $bits 0")).isLeft, bits)
    // the assembler rejects a zero width
    val prologue  = module("")
    val zeroWidth = Instruction.TypeInt(
      operand(OperandKind.IdResult, 2),
      operand(OperandKind.LiteralInteger, 0),
      operand(OperandKind.LiteralInteger, 0)
    )
    assert(load(prologue.copy(
      header = prologue.header.copy(bound = Word(3)),
      instructions = prologue.instructions :+ zeroWidth
    )).isLeft)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      VMProgram.RuntimeType.Integer(12, false)
    """).exists(_.message.contains("does not satisfy")))
  }

  test("layout preserves vector padding, fixed arrays and the runtime-array zero sentinel") {
    val layout = program("""
      %1 = OpTypeInt 32 0
      %2 = OpTypeVector %1 3
      %3 = OpConstant %1 5
      %4 = OpTypeArray %1 %3
      %5 = OpTypeRuntimeArray %1
    """)
    assertEquals(Seq(1, 2, 4, 5).map(layout.typeSize(_).raw), Seq(4, 16, 20, 0))
    assertEquals(layout.typeAlignment(2).raw, 16)
  }

  test("negative fixed-array lengths are rejected before layout") {
    val result = load(module("""
      %1 = OpTypeInt 64 1
      %2 = OpConstant %1 -1
      %3 = OpTypeArray %1 %2
    """))
    assert(result.leftValue.message.contains("array length -1 is invalid"))
  }

  test("zero array strides are rejected at the decoration boundary") {
    assert(load(module("OpDecorate %target ArrayStride 0")).leftValue.message.contains("array stride"))
  }

  test("matrix decorations retain the checked stride through layout") {
    val layout = program("""
      %1 = OpTypeFloat 32
      %2 = OpTypeVector %1 2
      %3 = OpTypeMatrix %2 3
      %4 = OpTypeStruct %3
      OpMemberDecorate %4 0 MatrixStride 16
      OpMemberDecorate %4 0 RowMajor
    """)
    val matrix = layout.memberMatrixLayouts((4, 0))
    assertEquals(matrix.stride.raw, 16)
    assert(matrix.rowMajor)
    assertEquals(layout.typeSize(4).raw, 32)
    assert(load(module("OpMemberDecorate %target 0 MatrixStride 0")).leftValue.message.contains("matrix stride"))
  }

  for (index <- Seq(1, 2, -1))
    test(s"nested composite extraction preserves scalar and vector values and rejects invalid index $index") {
      val module = spv"""
        $openCL
        OpEntryPoint Kernel %main "extract"
        %uint = OpTypeInt 32 0
        %uint2 = OpTypeVector %uint 2
        %pair = OpTypeStruct %uint %uint2
        %uint_ptr = OpTypePointer CrossWorkgroup %uint
        %void = OpTypeVoid
        %fn = OpTypeFunction %void %uint_ptr
        %ten = OpConstant %uint 10
        %twenty = OpConstant %uint 20
        %thirty = OpConstant %uint 30
        %vector = OpConstantComposite %uint2 %twenty %thirty
        %composite = OpConstantComposite %pair %ten %vector
        %main = OpFunction %void None %fn
        %out = OpFunctionParameter %uint_ptr
        %entry = OpLabel
        %inner = OpCompositeExtract %uint2 %composite 1
        %lane = OpCompositeExtract %uint %inner $index
        %sum = OpIAdd %uint %lane %ten
        OpStore %out %sum
        OpReturn
        OpFunctionEnd
      """
      val memory = RegionMemory()
      val out    = memory.allocate(4L)
      val result = run(module, "extract", memory, Vec(buffer(out)))
      if (index == 1) {
        assertEquals(result, Right(()))
        assertEquals(word(memory, out, 0), 40L)
      } else {
        assert(result.leftValue.message.contains("OpComposite index"))
        assertEquals(word(memory, out, 0), 0L)
      }
    }
}
