package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*

class MatrixSuite extends munit.FunSuite {
  private val operations =
    Seq(
      "transpose",
      "scale",
      "multiply",
      "matrix_vector",
      "vector_matrix",
      "outer",
      "indexed",
      "array_extract",
      "array_access"
    )
  private val shapes = Seq((2, 4), (3, 2), (4, 3), (3, 3))

  // the remaining matrix shapes, once each
  private val shapeRows = Seq(
    ("multiply", 32, (2, 2)),
    ("transpose", 16, (2, 3)),
    ("matrix_vector", 64, (3, 4)),
    ("indexed", 32, (4, 2)),
    ("outer", 64, (4, 4))
  )

  for ((operation, width, (rows, columns)) <- pairwise(operations, Seq(16, 32, 64), shapes) ++ shapeRows)
    test(s"$operation on $width-bit $rows by $columns matrices") {
      val a                     = Vector.tabulate(rows, columns)((r, c) => (r * columns + c + 1).toDouble)
      val b                     = Vector.tabulate(columns, rows)((r, c) => (r + c * 2 + 1).toDouble)
      val x                     = Vector.tabulate(rows)(r => (r + 1).toDouble)
      val y                     = Vector.tabulate(columns)(c => (c + 2).toDouble)
      val matrix                = Input("%matrix", flatten(a))
      val column                = Input("%column", x)
      val row                   = Input("%row", y)
      val matrices              = Input("%matrices", Vector.fill(rows * columns)(-1.0) ++ flatten(a))
      def scalar(value: Double) = Input("%float", Vector(value))
      def dot(p: Seq[Double], q: Seq[Double]): Double = p.zip(q).map(_ * _).sum
      def binary(opcode: String, out: String)         = Seq(s"%result = $opcode $out %left %right")
      val (left, right, out, expected, body)          = operation match {
        case "transpose" =>
          (matrix, scalar(0), "%transposed", flatten(a.transpose), Seq("%result = OpTranspose %transposed %left"))
        case "scale" =>
          (matrix, scalar(-2), "%matrix", flatten(a.map(_.map(_ * -2))), binary("OpMatrixTimesScalar", "%matrix"))
        case "multiply" =>
          val product = a.map(r => b.transpose.map(dot(r, _)))
          val right   = Input("%transposed", flatten(b))
          (matrix, right, "%square", flatten(product), binary("OpMatrixTimesMatrix", "%square"))
        case "matrix_vector" => (matrix, row, "%column", a.map(dot(_, y)), binary("OpMatrixTimesVector", "%column"))
        case "vector_matrix" =>
          (column, matrix, "%row", a.transpose.map(dot(_, x)), binary("OpVectorTimesMatrix", "%row"))
        case "outer" =>
          (column, row, "%matrix", flatten(x.map(p => y.map(p * _))), binary("OpOuterProduct", "%matrix"))
        case "indexed" =>
          // Matrices are vectors of boxed doubles, so updating a cell boxes it.
          @scala.annotation.nowarn("msg=baumkuchen:loop-boxing")
          val changed = a.updated(rows - 1, a.last.updated(columns - 1, x.last))
          @scala.annotation.nowarn("msg=baumkuchen:loop-boxing")
          val stored = changed.zip(x).map(_.updated(0, _))
          (
            matrix,
            column,
            "%matrix",
            flatten(stored),
            Seq(
              "OpStore %out_var %left",
              "%cell = OpAccessChain %float_output_ptr %out_var %last_column %last_row",
              s"%last = OpCompositeExtract %float %right ${rows - 1}",
              "OpStore %cell %last",
              "%reloaded = OpLoad %matrix %out_var",
              "%result = OpCompositeInsert %matrix %right %reloaded 0"
            )
          )
        case "array_extract" =>
          (matrices, column, "%matrix", flatten(a), Seq("%result = OpCompositeExtract %matrix %left 1"))
        case _ => (
            matrices,
            column,
            "%matrix",
            flatten(a),
            Seq(
              "%element = OpAccessChain %matrix_input_ptr %left_var %one",
              "%result = OpLoad %matrix %element"
            )
          )
      }
      assertEquals(shader(width, rows, columns, left, right, out, expected.size, body*), encode(width, expected))
    }

  for {
    (operation, width, size, swapped) <-
      pairwise(Seq("Determinant", "MatrixInverse"), Seq(16, 32, 64), Seq(2, 3, 4), Seq(false, true))
  } test(s"$operation on $width-bit $size by $size matrices${if (swapped) " with swapped rows" else ""}") {
    val base = Vector.tabulate(size, size)((r, c) => if (r == c) 2.0 else if (r < c) 1.0 else 0.0)
    val a    = if (swapped) base.updated(0, base(1)).updated(1, base(0)) else base
    if (operation == "Determinant") {
      val expected = Math.pow(2.0, size) * (if (swapped) -1 else 1)
      assertEquals(determinant(width, size, flatten(a)), encode(width, Vector(expected)))
    } else {
      val inverse =
        Vector.tabulate(size, size)((r, c) => if (r == c) 0.5 else if (r < c) -Math.pow(0.5, c - r + 1) else 0.0)
      // Matrices are vectors of boxed doubles, so swapping columns boxes the cells.
      @scala.annotation.nowarn("msg=baumkuchen:loop-boxing")
      val expected = flatten(if (swapped) inverse.map(row => row.updated(0, row(1)).updated(1, row(0))) else inverse)
      val actual   = shader(
        width,
        size,
        size,
        Input("%matrix", flatten(a)),
        Input("%float", Vector(0.0)),
        "%matrix",
        expected.size,
        "%result = OpExtInst %matrix %glsl MatrixInverse %left"
      )
      val bytes = width / 8
      expected.indices.foreach { index =>
        val lane = actual.slice(index * bytes, (index + 1) * bytes)
        if (expected(index) == 0.0) {
          val magnitude = if (width == 64) Long.MaxValue else (1L << (width - 1)) - 1
          assertEquals(LittleEndian.decode(lane, 0, bytes.refine[Int in 0 ~ 8].value) & magnitude, 0L)
        } else assertEquals(lane, encode(width, Vector(expected(index))))
      }
    }
  }

  for (width <- Seq(16, 32, 64)) test(s"Determinant of a $width-bit singular matrix keeps the negative zero") {
    // Vector(-0.0) loses its sign on Scala Native, which zero-fills all-zero literal arrays
    val negativeZero = java.lang.Double.longBitsToDouble(Long.MinValue)
    assertEquals(determinant(width, 2, Vector(0.0, 0.0, 2.0, -3.0)), encode(width, Vector(negativeZero)))
  }

  test("OpVectorTimesScalar multiplies each floating lane") {
    val left  = Input("%column", Vector(2.0, -3.0))
    val right = Input("%float", Vector(-2.0))
    val body  = "%result = OpVectorTimesScalar %column %left %right"
    assertEquals(
      shader(width = 32, rows = 2, columns = 2, left, right, "%column", 2, body),
      encode(32, Vector(-4.0, 6.0))
    )
  }

  private val storage = pairwise(
    Seq("structure", "array", "matrix", "column", "scalar", "copy", "initialiser"),
    shapes,
    Seq("ColMajor", "RowMajor"),
    Seq("ColMajor", "RowMajor")
  )

  // every mode converts between layouts in both directions
  private val conversions = Seq(
    ("copy", (2, 2), "ColMajor", "RowMajor"),
    ("copy", (4, 4), "RowMajor", "ColMajor"),
    ("initialiser", (2, 3), "RowMajor", "ColMajor"),
    ("initialiser", (3, 4), "ColMajor", "RowMajor"),
    ("column", (4, 2), "ColMajor", "RowMajor"),
    ("column", (3, 3), "RowMajor", "ColMajor"),
    ("scalar", (4, 4), "ColMajor", "RowMajor"),
    ("scalar", (2, 2), "RowMajor", "ColMajor"),
    ("structure", (2, 3), "RowMajor", "ColMajor"),
    ("array", (3, 4), "ColMajor", "RowMajor"),
    ("matrix", (4, 2), "RowMajor", "ColMajor")
  )

  for ((mode, (rows, columns), source, destination) <- storage ++ conversions.filterNot(storage.toSet))
    test(s"$mode maps ${rows}x$columns matrix storage from $source to $destination, preserving padding") {
      val size     = 16 + 2 * 128 + 16
      val first    = Array.fill[Byte](size)(0x5a.toByte)
      val expected = Array.fill[Byte](size)(0x6b.toByte)
      def offset(matrix: Int, row: Int, column: Int, major: String, stride: Int): Int =
        16 + matrix * 128 + (if (major == "RowMajor") row * stride + column * 4 else column * stride + row * 4)
      def put(bytes: Array[Byte], offset: Int, value: Float): Unit =
        LittleEndian.write(floatBits(value), bytes, offset, 4)
      put(first, 0, 13.0f)
      if (mode == "structure") put(expected, 0, 13.0f)
      if (mode == "initialiser") put(expected, 0, 1.0f)
      for {
        m <- 0 until 2
        r <- 0 until rows
        c <- 0 until columns
      } {
        val value = (1 + m * 100 + r * columns + c).toFloat
        put(first, offset(m, r, c, source, 16), value)
        val selected = mode match {
          case "structure" | "array" | "initialiser" => true
          case "matrix" | "copy"                     => m == 1
          case "column"                              => m == 1 && c == 1
          case _                                     => m == 1 && c == 1 && r == rows - 1
        }
        if (selected) put(expected, offset(m, r, c, destination, 32), if (mode == "initialiser") 1.0f else value)
      }
      val (pointer, value, indexes) = mode match {
        case "array"           => ("%matrices_ptr", "%matrices", "%one")
        case "matrix" | "copy" => ("%matrix_ptr", "%matrix", "%one %one")
        case "column"          => ("%column_ptr", "%column", "%one %one %one")
        case _                 => ("%float_ptr", "%float", "%one %one %one %last_row")
      }
      val body = mode match {
        case "structure" | "initialiser" =>
          s"""%value = OpLoad %source_block ${if (mode == "initialiser") "%initial" else "%source"}
           |%copy = OpCopyLogical %destination_block %value
           |OpStore %destination %copy""".stripMargin
        case "copy" =>
          s"""%from = OpAccessChain $pointer %source $indexes
           |%to = OpAccessChain $pointer %destination $indexes
           |OpCopyMemory %to %from""".stripMargin
        case _ =>
          s"""%from = OpAccessChain $pointer %source $indexes
           |%to = OpAccessChain $pointer %destination $indexes
           |%value = OpLoad $value %from
           |OpStore %to %value""".stripMargin
      }
      val module = spv"""
      $vulkan
      OpEntryPoint GLCompute %main "main" %source %destination
      OpDecorate %source Binding 0
      OpDecorate %source DescriptorSet 0
      OpDecorate %destination Binding 1
      OpDecorate %destination DescriptorSet 0
      OpDecorate %source_block Block
      OpDecorate %destination_block Block
      OpDecorate %matrices ArrayStride 128
      OpMemberDecorate %source_block 0 Offset 0
      OpMemberDecorate %destination_block 0 Offset 0
      OpMemberDecorate %source_block 1 Offset 16
      OpMemberDecorate %destination_block 1 Offset 16
      OpMemberDecorate %source_block 1 MatrixStride 16
      OpMemberDecorate %destination_block 1 MatrixStride 32
      OpMemberDecorate %source_block 1 $source
      OpMemberDecorate %destination_block 1 $destination
      %void = OpTypeVoid
      %float = OpTypeFloat 32
      %uint = OpTypeInt 32 0
      %one = OpConstant %uint 1
      %two = OpConstant %uint 2
      %last_row = OpConstant %uint ${rows - 1}
      %column = OpTypeVector %float $rows
      %matrix = OpTypeMatrix %column $columns
      %matrices = OpTypeArray %matrix %two
      %source_block = OpTypeStruct %float %matrices
      %destination_block = OpTypeStruct %float %matrices
      %source_ptr = OpTypePointer StorageBuffer %source_block
      %destination_ptr = OpTypePointer StorageBuffer %destination_block
      %matrices_ptr = OpTypePointer StorageBuffer %matrices
      %matrix_ptr = OpTypePointer StorageBuffer %matrix
      %column_ptr = OpTypePointer StorageBuffer %column
      %float_ptr = OpTypePointer StorageBuffer %float
      %function_block_ptr = OpTypePointer Function %source_block
      %float_one = OpConstant %float 1
      %column_ones = OpConstantComposite %column ${Seq.fill(rows)("%float_one").mkString(" ")}
      %matrix_ones = OpConstantComposite %matrix ${Seq.fill(columns)("%column_ones").mkString(" ")}
      %matrices_ones = OpConstantComposite %matrices %matrix_ones %matrix_ones
      %block_ones = OpConstantComposite %source_block %float_one %matrices_ones
      %fn = OpTypeFunction %void
      %source = OpVariable %source_ptr StorageBuffer
      %destination = OpVariable %destination_ptr StorageBuffer
      %main = OpFunction %void None %fn
      %entry = OpLabel
      ${if (mode == "initialiser") "%initial = OpVariable %function_block_ptr Function %block_ones" else ""}
      $body
      OpReturn
      OpFunctionEnd
    """
      val memory = RegionMemory()
      val from   = memory.allocate(Vec.from(first))
      val to     = memory.allocate(Vec.from(Array.fill[Byte](size)(0x6b.toByte)))
      assertEquals(run(module, "main", memory, Vec(buffer(from), buffer(to))), Right(()))
      assertEquals(memory.bytes(from), Vec.from(first))
      assertEquals(memory.bytes(to), Vec.from(expected))
    }

  /** Reference data is row-major; shader interface records hold columns. */
  private def flatten(matrix: Vector[Vector[Double]]): Vector[Double] = matrix.transpose.flatten

  private def encode(width: Int, data: Vector[Double]): Vec[Byte] =
    littleEndian(data.map(floatingBits(_, width)), width)

  private def determinant(width: Int, size: Int, matrix: Vector[Double]): Vec[Byte] =
    shader(
      width,
      size,
      size,
      Input("%matrix", matrix),
      Input("%float", Vector(0.0)),
      "%float",
      1,
      "%result = OpExtInst %float %glsl Determinant %left"
    )

  private case class Input(typeName: String, data: Vector[Double])

  /** Vertex shader that loads `%left` and `%right` from its inputs, runs `body` and stores `%result` to its output. */
  private def shader(
      width: Int,
      rows: Int,
      columns: Int,
      left: Input,
      right: Input,
      out: String,
      outputs: Int,
      body: String*
  ): Vec[Byte] = {
    val module = spv"""
      ${vulkan.replace("OpCapability Shader", "OpCapability Shader\nOpCapability Float16\nOpCapability Float64")}
      OpEntryPoint Vertex %main "main" %left_var %right_var %out_var
      %void = OpTypeVoid
      %float = OpTypeFloat $width
      %column = OpTypeVector %float $rows
      %row = OpTypeVector %float $columns
      %matrix = OpTypeMatrix %column $columns
      %transposed = OpTypeMatrix %row $rows
      %square = OpTypeMatrix %column $rows
      %uint = OpTypeInt 32 0
      %last_column = OpConstant %uint ${columns - 1}
      %last_row = OpConstant %uint ${rows - 1}
      %one = OpConstant %uint 1
      %two = OpConstant %uint 2
      %matrices = OpTypeArray %matrix %two
      %matrix_input_ptr = OpTypePointer Input %matrix
      %float_output_ptr = OpTypePointer Output %float
      %left_ptr = OpTypePointer Input ${left.typeName}
      %right_ptr = OpTypePointer Input ${right.typeName}
      %out_ptr = OpTypePointer Output $out
      %fn = OpTypeFunction %void
      %left_var = OpVariable %left_ptr Input
      %right_var = OpVariable %right_ptr Input
      %out_var = OpVariable %out_ptr Output
      %main = OpFunction %void None %fn
      %entry = OpLabel
      %left = OpLoad ${left.typeName} %left_var
      %right = OpLoad ${right.typeName} %right_var
      ${body.mkString("\n")}
      OpStore %out_var %result
      OpReturn
      OpFunctionEnd
    """
    val memory = RegionMemory()
    val first  = memory.allocate(encode(width, left.data))
    val second = memory.allocate(encode(width, right.data))
    val output = memory.allocate((outputs * width / 8).toLong.refine[Long > 0].value)
    assertEquals(run(module, "main", memory, Vec(buffer(first), buffer(second), buffer(output))), Right(()))
    memory.bytes(output)
  }
}
