package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.spirv.v1_6.execution.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class MatrixSuite extends munit.FunSuite {
  private def ref(id: Int): Operand    = operand(OperandKind.IdRef, id)
  private def literal(n: Int): Operand = operand(OperandKind.LiteralInteger, n)

  for {
    width <- Seq[Int in (16, 32, 64)](16, 32, 64)
    rawWidth   = width.raw
    widthBytes = width.widen[Int in (8, 16, 32, 64)].toMemoryValueByteWidth
    rows      <- 2 to 4
    columns   <- 2 to 4
    operation <- Seq(
      "transpose",
      "scale",
      "multiply",
      "matrix_vector",
      "vector_matrix",
      "outer",
      "indexed",
      "array_extract",
      "array_access",
      "determinant",
      "inverse",
      "determinant_swapped",
      "inverse_swapped",
      "determinant_negative_zero"
    )
    if operation != "determinant_negative_zero" || rows == 2 && columns == 2
    if !operation.startsWith("determinant") && !operation.startsWith("inverse") || rows == columns
  } test(s"$operation on $rawWidth-bit $rows by $columns matrices") {
    // Reference data is row-major; the shader record serializer explicitly writes columns.
    val special = operation.startsWith("determinant") || operation.startsWith("inverse")
    val base    = if (special) Vector.tabulate(rows, columns)((r, c) => if (r == c) 2.0 else if (r < c) 1.0 else 0.0)
    else Vector.tabulate(rows, columns)((r, c) => (r * columns + c + 1).toDouble)
    val a = if (operation == "determinant_negative_zero") Vector(Vector(0.0, 2.0), Vector(0.0, -3.0))
    else if (operation.endsWith("swapped")) base.updated(0, base(1)).updated(1, base(0)) else base
    val b            = Vector.tabulate(columns, rows)((r, c) => (r + c * 2 + 1).toDouble)
    val rowVector    = Vector.tabulate(rows)(r => (r + 1).toDouble)
    val columnVector = Vector.tabulate(columns)(c => (c + 2).toDouble)
    def flatten(matrix: Vector[Vector[Double]]): Vector[Double] = matrix.transpose.flatten
    val (leftType, rightType, outType, left, right, expected)   = operation match {
      case "determinant_negative_zero" =>
        // Scala Native zero-initializes all-zero literal arrays, losing the sign in Vector(-0.0).
        (5, 2, 2, flatten(a), Vector(0.0), Vector(java.lang.Double.longBitsToDouble(Long.MinValue)))
      case "determinant" | "determinant_swapped" =>
        (5, 2, 2, flatten(a), Vector(0.0), Vector(Math.pow(2.0, rows) * (if (operation.endsWith("swapped")) -1 else 1)))
      case "inverse" | "inverse_swapped" =>
        val inverse =
          Vector.tabulate(rows, columns)((r, c) => if (r == c) 0.5 else if (r < c) -Math.pow(0.5, c - r + 1) else 0.0)
        val output =
          if (operation.endsWith("swapped")) inverse.map(row => row.updated(0, row(1)).updated(1, row(0))) else inverse
        (5, 2, 5, flatten(a), Vector(0.0), flatten(output))
      case "array_extract" | "array_access" =>
        (8, 3, 5, Vector.fill(rows * columns)(-1.0) ++ flatten(a), rowVector, flatten(a))
      case "indexed" =>
        val changed = a.updated(rows - 1, a.last.updated(columns - 1, rowVector.last))
        (5, 3, 5, flatten(a), rowVector, flatten(changed.zip(rowVector).map((row, x) => row.updated(0, x))))
      case "transpose" => (5, 2, 6, flatten(a), Vector(0.0), flatten(a.transpose))
      case "scale"     => (5, 2, 5, flatten(a), Vector(-2.0), flatten(a.map(_.map(_ * -2))))
      case "multiply"  =>
        val product = a.map(row => b.transpose.map(column => row.zip(column).map((x, y) => x * y).sum))
        (5, 6, 7, flatten(a), flatten(b), flatten(product))
      case "matrix_vector" =>
        (5, 4, 3, flatten(a), columnVector, a.map(row => row.zip(columnVector).map((x, y) => x * y).sum))
      case "vector_matrix" =>
        (3, 5, 4, rowVector, flatten(a), a.transpose.map(column => column.zip(rowVector).map((x, y) => x * y).sum))
      case _ =>
        (3, 4, 5, rowVector, columnVector, flatten(rowVector.map(x => columnVector.map(x * _))))
    }
    val calculation: Vec[Instruction] = operation match {
      case "determinant_negative_zero" | "determinant" | "determinant_swapped" | "inverse" | "inverse_swapped" =>
        Vec(Instruction.ExtInst(
          ref(outType),
          ref(35),
          ref(39),
          operand(OperandKind.LiteralExtInstInteger, if (operation.startsWith("determinant")) 33 else 34),
          Vec(ref(33))
        ))
      case "array_extract" => Vec(Instruction.CompositeExtract(ref(5), ref(35), ref(33), Vec(literal(1))))
      case "array_access"  => Vec(
          Instruction.AccessChain(ref(9), ref(36), ref(20), Vec(ref(18))),
          Instruction.Load(ref(5), ref(35), ref(36), None)
        )
      case "indexed" => Vec(
          Instruction.Store(ref(22), ref(33), None),
          Instruction.AccessChain(ref(14), ref(36), ref(22), Vec(ref(16), ref(17))),
          Instruction.CompositeExtract(ref(2), ref(37), ref(34), Vec(literal(rows - 1))),
          Instruction.Store(ref(36), ref(37), None),
          Instruction.Load(ref(5), ref(38), ref(22), None),
          Instruction.CompositeInsert(ref(5), ref(35), ref(34), ref(38), Vec(literal(0)))
        )
      case "transpose"     => Vec(Instruction.Transpose(ref(outType), ref(35), ref(33)))
      case "scale"         => Vec(Instruction.MatrixTimesScalar(ref(outType), ref(35), ref(33), ref(34)))
      case "multiply"      => Vec(Instruction.MatrixTimesMatrix(ref(outType), ref(35), ref(33), ref(34)))
      case "matrix_vector" => Vec(Instruction.MatrixTimesVector(ref(outType), ref(35), ref(33), ref(34)))
      case "vector_matrix" => Vec(Instruction.VectorTimesMatrix(ref(outType), ref(35), ref(33), ref(34)))
      case _               => Vec(Instruction.OuterProduct(ref(outType), ref(35), ref(33), ref(34)))
    }
    val module = Module(
      Header(ISA.version, Word(0), Word(40), Word(0)),
      Vec(
        Instruction.ExtInstImport(ref(39), stringOperand("GLSL.std.450")),
        Instruction.TypeVoid(ref(1)),
        Instruction.TypeFloat(ref(2), literal(rawWidth), None),
        Instruction.TypeVector(ref(3), ref(2), literal(rows)),
        Instruction.TypeVector(ref(4), ref(2), literal(columns)),
        Instruction.TypeMatrix(ref(5), ref(3), literal(columns)),
        Instruction.TypeMatrix(ref(6), ref(4), literal(rows)),
        Instruction.TypeMatrix(ref(7), ref(3), literal(rows)),
        Instruction.TypeInt(ref(15), literal(32), literal(0)),
        Instruction.Constant(ref(15), ref(16), literal(columns - 1)),
        Instruction.Constant(ref(15), ref(17), literal(rows - 1)),
        Instruction.Constant(ref(15), ref(18), literal(1)),
        Instruction.Constant(ref(15), ref(19), literal(2)),
        Instruction.TypeArray(ref(8), ref(5), ref(19)),
        Instruction.TypePointer(ref(9), operand(OperandKind.StorageClass, StorageClass.Input), ref(5)),
        Instruction.TypePointer(ref(14), operand(OperandKind.StorageClass, StorageClass.Output), ref(2)),
        Instruction.TypePointer(ref(10), operand(OperandKind.StorageClass, StorageClass.Input), ref(leftType)),
        Instruction.TypePointer(ref(11), operand(OperandKind.StorageClass, StorageClass.Input), ref(rightType)),
        Instruction.TypePointer(ref(12), operand(OperandKind.StorageClass, StorageClass.Output), ref(outType)),
        Instruction.TypeFunction(ref(13), ref(1), Vec.empty),
        Instruction.Variable(ref(10), ref(20), operand(OperandKind.StorageClass, StorageClass.Input), None),
        Instruction.Variable(ref(11), ref(21), operand(OperandKind.StorageClass, StorageClass.Input), None),
        Instruction.Variable(ref(12), ref(22), operand(OperandKind.StorageClass, StorageClass.Output), None),
        Instruction.EntryPoint(
          operand(OperandKind.ExecutionModel, ExecutionModel.Vertex),
          ref(30),
          stringOperand("main"),
          Vec(ref(20), ref(21), ref(22))
        ),
        Instruction.Function(ref(1), ref(30), operand(OperandKind.FunctionControl, FunctionControl.None), ref(13)),
        Instruction.Label(ref(32)),
        Instruction.Load(ref(leftType), ref(33), ref(20), None),
        Instruction.Load(ref(rightType), ref(34), ref(21), None)
      ) ++ calculation ++ Vec(
        Instruction.Store(ref(22), ref(35), None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    def encoded(data: Vector[Double]): Vec[Byte] = Vec.from(data.flatMap { value =>
      val bits = rawWidth match {
        case 16 => IEEE754.floatToBinary16(value.toFloat).raw.toLong
        case 32 => java.lang.Float.floatToRawIntBits(value.toFloat).toLong
        case _  => java.lang.Double.doubleToRawLongBits(value)
      }
      LittleEndian.toVec(bits, widthBytes.widen[Int in 0 ~ 8])
    })
    val first  = encoded(left)
    val second = encoded(right)
    val output = encoded(expected)
    val region = AllocationId(1L)
    val memory = ArrayMemory(region, first.size + second.size + output.size)
    assertEquals(memory.write(region, 0L, first ++ second), Right(()))
    val task = KernelTask.validated(
      "main",
      Dimensions.One,
      Dimensions.One,
      Vec(
        KernelTask.Argument.Memory(region, 0L),
        KernelTask.Argument.Memory(region, first.nonNegativeSize.toLong.widen[Long >= 0]),
        KernelTask.Argument.Memory(region, (first.nonNegativeSize + second.nonNegativeSize).toLong.widen[Long >= 0])
      )
    ).toOption.get
    assertEquals(executeUnchecked(module, task, memory), Right(()))
    val actual = memory.bytes.drop(first.size + second.size)
    if (operation.startsWith("inverse")) {
      val byteWidth = widthBytes.widen[Int in 0 ~ 8]
      for (index <- expected.indices) {
        val bits = LittleEndian.decode(actual, index * byteWidth.raw, byteWidth)
        if (expected(index) == 0.0)
          assertEquals(bits & (if (rawWidth == 64) Long.MaxValue else (1L << (rawWidth - 1)) - 1), 0L)
        else assertEquals(
          actual.slice(index * byteWidth.raw, (index + 1) * byteWidth.raw),
          output.slice(index * byteWidth.raw, (index + 1) * byteWidth.raw)
        )
      }
    } else assertEquals(actual, output)
  }
}
