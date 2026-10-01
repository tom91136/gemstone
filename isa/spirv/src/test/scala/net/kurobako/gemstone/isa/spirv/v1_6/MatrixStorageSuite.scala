package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.spirv.v1_6.execution.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class MatrixStorageSuite extends munit.FunSuite {
  private def ref(n: Int): Operand = operand("IdRef", n)
  private def lit(n: Int): Operand = operand("LiteralInteger", n)

  for {
    rows            <- 2 to 4
    columns         <- 2 to 4
    sourceRows      <- Seq(false, true)
    destinationRows <- Seq(false, true)
    mode            <- Seq("structure", "array", "matrix", "column", "scalar", "copy", "initializer")
  } test(s"$mode maps ${rows}x$columns matrix storage, row-major $sourceRows to $destinationRows, preserving padding") {
    val size     = 16 + 2 * 128 + 16
    val first    = Array.fill[Byte](size)(0x5a.toByte)
    val expected = Array.fill[Byte](size)(0x6b.toByte)
    def offset(matrix: Int, row: Int, column: Int, rowMajor: Boolean, stride: Int): Int =
      16 + matrix * 128 + (if (rowMajor) row * stride + column * 4 else column * stride + row * 4)
    def put(bytes: Array[Byte], offset: Int, value: Float): Unit =
      LittleEndian.write(java.lang.Float.floatToRawIntBits(value).toLong, bytes, offset, 4)
    put(first, 0, 13.0f)
    if (mode == "structure") put(expected, 0, 13.0f)
    if (mode == "initializer") put(expected, 0, 1.0f)
    for {
      m <- 0 until 2
      r <- 0 until rows
      c <- 0 until columns
    } {
      val value = (1 + m * 100 + r * columns + c).toFloat
      put(first, offset(m, r, c, sourceRows, 16), value)
      val selected = mode match {
        case "structure" | "array" | "initializer" => true
        case "matrix" | "copy"                     => m == 1
        case "column"                              => m == 1 && c == 1
        case _                                     => m == 1 && c == 1 && r == rows - 1
      }
      if (selected) put(expected, offset(m, r, c, destinationRows, 32), if (mode == "initializer") 1.0f else value)
    }
    val (pointerType, valueType, indexes) = mode match {
      case "structure" | "initializer" => (12, 10, Vec.empty[Operand])
      case "array"                     => (14, 8, Vec(ref(17)))
      case "matrix" | "copy"           => (23, 5, Vec(ref(17), ref(17)))
      case "column"                    => (24, 4, Vec(ref(17), ref(17), ref(17)))
      case _                           => (25, 2, Vec(ref(17), ref(17), ref(17), ref(19)))
    }
    val body = if (mode == "structure" || mode == "initializer") Vec(
      Instruction.Load(ref(10), ref(35), ref(if (mode == "initializer") 47 else 20), None),
      Instruction.CopyLogical(ref(11), ref(36), ref(35)),
      Instruction.Store(ref(21), ref(36), None)
    )
    else Vec(
      Instruction.AccessChain(ref(pointerType), ref(32), ref(20), indexes),
      Instruction.AccessChain(ref(pointerType), ref(33), ref(21), indexes)
    ) ++
      (if (mode == "copy") Vec(Instruction.CopyMemory(ref(33), ref(32), None, None))
       else Vec(
         Instruction.Load(ref(valueType), ref(35), ref(32), None),
         Instruction.Store(ref(33), ref(35), None)
       ))
    val module = Module(
      Header(ISA.version, Word(0), Word(64), Word(0)),
      Vec(
        Instruction.EntryPoint(
          operand("ExecutionModel", ExecutionModel.GLCompute),
          ref(30),
          stringOperand("main"),
          Vec(ref(20), ref(21))
        ),
        Instruction.Decorate(ref(20), operand("Decoration", Decoration.Binding, 0)),
        Instruction.Decorate(ref(20), operand("Decoration", Decoration.DescriptorSet, 0)),
        Instruction.Decorate(ref(21), operand("Decoration", Decoration.Binding, 1)),
        Instruction.Decorate(ref(21), operand("Decoration", Decoration.DescriptorSet, 0)),
        Instruction.Decorate(ref(10), operand("Decoration", Decoration.Block)),
        Instruction.Decorate(ref(11), operand("Decoration", Decoration.Block)),
        Instruction.Decorate(ref(8), operand("Decoration", Decoration.ArrayStride, 128)),
        Instruction.MemberDecorate(ref(10), lit(0), operand("Decoration", Decoration.Offset, 0)),
        Instruction.MemberDecorate(ref(11), lit(0), operand("Decoration", Decoration.Offset, 0)),
        Instruction.MemberDecorate(ref(10), lit(1), operand("Decoration", Decoration.Offset, 16)),
        Instruction.MemberDecorate(ref(11), lit(1), operand("Decoration", Decoration.Offset, 16)),
        Instruction.MemberDecorate(ref(10), lit(1), operand("Decoration", Decoration.MatrixStride, 16)),
        Instruction.MemberDecorate(ref(11), lit(1), operand("Decoration", Decoration.MatrixStride, 32)),
        Instruction.MemberDecorate(ref(10), lit(1), operand("Decoration", if (sourceRows) 4 else 5)),
        Instruction.MemberDecorate(ref(11), lit(1), operand("Decoration", if (destinationRows) 4 else 5)),
        Instruction.TypeVoid(ref(1)),
        Instruction.TypeFloat(ref(2), lit(32), None),
        Instruction.TypeInt(ref(15), lit(32), lit(0)),
        Instruction.Constant(ref(15), ref(16), lit(0)),
        Instruction.Constant(ref(15), ref(17), lit(1)),
        Instruction.Constant(ref(15), ref(18), lit(2)),
        Instruction.Constant(ref(15), ref(19), lit(rows - 1)),
        Instruction.TypeVector(ref(4), ref(2), lit(rows)),
        Instruction.TypeMatrix(ref(5), ref(4), lit(columns)),
        Instruction.TypeArray(ref(8), ref(5), ref(18)),
        Instruction.TypeStruct(ref(10), Vec(ref(2), ref(8))),
        Instruction.TypeStruct(ref(11), Vec(ref(2), ref(8))),
        Instruction.TypePointer(ref(12), operand("StorageClass", StorageClass.StorageBuffer), ref(10)),
        Instruction.TypePointer(ref(13), operand("StorageClass", StorageClass.StorageBuffer), ref(11)),
        Instruction.TypePointer(ref(14), operand("StorageClass", StorageClass.StorageBuffer), ref(8)),
        Instruction.TypePointer(ref(23), operand("StorageClass", StorageClass.StorageBuffer), ref(5)),
        Instruction.TypePointer(ref(24), operand("StorageClass", StorageClass.StorageBuffer), ref(4)),
        Instruction.TypePointer(ref(25), operand("StorageClass", StorageClass.StorageBuffer), ref(2)),
        Instruction.TypePointer(ref(46), operand("StorageClass", StorageClass.Function), ref(10)),
        Instruction.Constant(ref(2), ref(41), operand("LiteralContextDependentNumber", 0x3f800000)),
        Instruction.ConstantComposite(ref(4), ref(42), Vec.fill(rows)(ref(41))),
        Instruction.ConstantComposite(ref(5), ref(43), Vec.fill(columns)(ref(42))),
        Instruction.ConstantComposite(ref(8), ref(44), Vec(ref(43), ref(43))),
        Instruction.ConstantComposite(ref(10), ref(45), Vec(ref(41), ref(44))),
        Instruction.TypeFunction(ref(22), ref(1), Vec.empty),
        Instruction.Variable(ref(12), ref(20), operand("StorageClass", StorageClass.StorageBuffer), None),
        Instruction.Variable(ref(13), ref(21), operand("StorageClass", StorageClass.StorageBuffer), None),
        Instruction.Function(ref(1), ref(30), operand("FunctionControl", FunctionControl.None), ref(22)),
        Instruction.Label(ref(31))
      ) ++
        (if (mode == "initializer")
           Vec(Instruction.Variable(ref(46), ref(47), operand("StorageClass", StorageClass.Function), Some(ref(45))))
         else Vec.empty[Instruction]) ++ body ++ Vec(Instruction.Return(), Instruction.FunctionEnd()),
      Endianness.Little
    )
    val region = AllocationId(1L)
    val memory = ArrayMemory(region, size * 2)
    assertEquals(
      memory.write(
        region,
        0L,
        Vec.from(first ++ Array.fill[Byte](size)(0x6b.toByte))
      ),
      Right(())
    )
    val task = KernelTask.validated(
      "main",
      Dimensions.One,
      Dimensions.One,
      Vec(
        KernelTask.Argument.Memory(region, 0L),
        KernelTask.Argument.Memory(region, size.toLong.unsafe[Long >= 0])
      )
    ).toOption.get
    assertEquals(executeUnchecked(module, task, memory), Right(()))
    assertEquals(memory.bytes.take(size), Vec.from(first))
    assertEquals(memory.bytes.drop(size), Vec.from(expected))
  }
}
