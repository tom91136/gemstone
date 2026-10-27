package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*

class CooperativeMatrixCallSuite extends munit.FunSuite {

  private def assembled(declarations: String, body: String, functions: String): Module = spv"""
    OpCapability CooperativeMatrixKHR
    OpCapability CooperativeMatrixPerElementOperationsNV
    OpCapability CooperativeMatrixReductionsNV
    OpExtension "SPV_KHR_cooperative_matrix"
    OpExtension "SPV_NV_cooperative_matrix2"
    $openCL
    OpEntryPoint Kernel %main "main"
    %void = OpTypeVoid
    %uint = OpTypeInt 32 0
    %uint_ptr = OpTypePointer CrossWorkgroup %uint
    %local_ptr = OpTypePointer Workgroup %uint
    %scope = OpConstant %uint 3
    %use = OpConstant %uint 2
    %row_major = OpConstant %uint 0
    %zero = OpConstant %uint 0
    %one = OpConstant %uint 1
    %two = OpConstant %uint 2
    %ten = OpConstant %uint 10
    %hundred = OpConstant %uint 100
    %thousand = OpConstant %uint 1000
    %entry_fn = OpTypeFunction %void %uint_ptr %uint_ptr %uint_ptr
    %element_fn = OpTypeFunction %uint %uint %uint %uint
    %extra_fn = OpTypeFunction %uint %uint %uint %uint %uint %uint
    %combine_fn = OpTypeFunction %uint %uint %uint
    $declarations
    %main = OpFunction %void None %entry_fn
    %first = OpFunctionParameter %uint_ptr
    %second = OpFunctionParameter %uint_ptr
    %out = OpFunctionParameter %uint_ptr
    %entry = OpLabel
    $body
    OpReturn
    OpFunctionEnd
    $functions
  """

  private def declared(rows: Int, columns: Int, name: String = "matrix"): String =
    s"""%rows_$name = OpConstant %uint $rows
       |%columns_$name = OpConstant %uint $columns
       |%$name = OpTypeCooperativeMatrixKHR %uint %scope %rows_$name %columns_$name %use
       |%stride_$name = OpConstant %uint $columns""".stripMargin

  private val rowAndColumn =
    """%row_part = OpIMul %uint %row %hundred
      |%column_part = OpIMul %uint %column %thousand
      |%partial = OpIAdd %uint %value %row_part
      |%sum = OpIAdd %uint %partial %column_part
      |OpReturnValue %sum""".stripMargin

  private def elementFunction(name: String, body: String): String =
    s"""%$name = OpFunction %uint None %element_fn
       |%${name}_row = OpFunctionParameter %uint
       |%${name}_column = OpFunctionParameter %uint
       |%${name}_value = OpFunctionParameter %uint
       |%${name}_entry = OpLabel
       |${body.replaceAll("%(row|column|value)\\b", s"%${name}_$$1")}
       |OpFunctionEnd""".stripMargin

  private val combine =
    """%combine = OpFunction %uint None %combine_fn
      |%left = OpFunctionParameter %uint
      |%right = OpFunctionParameter %uint
      |%combine_entry = OpLabel
      |%shifted = OpIMul %uint %left %ten
      |%folded = OpIAdd %uint %shifted %right
      |OpReturnValue %folded
      |OpFunctionEnd""".stripMargin

  private def execute(
      module: Module,
      inputs: Seq[Seq[Long]],
      outputs: Int,
      local: Int = 1
  ): (Either[Fault, Unit], Vec[Long]) = {
    val memory  = RegionMemory()
    val buffers = inputs.map { values =>
      val region = memory.allocate((4L * values.size).refine[Long > 0].value)
      writeWords(memory, region, 0, values*)
      region
    }
    val out     = memory.allocate((4L * outputs).refine[Long > 0].value)
    val padding = Seq.fill(2 - inputs.size)(out)
    val result  = run(
      module,
      "main",
      memory,
      Vec.from((buffers ++ padding :+ out).map(buffer(_))),
      local = testDimensions(local.refine[Int > 0].value)
    )
    (result, words(memory, out, 0, outputs))
  }

  private val inputWords = Seq.tabulate(6)(index => 10L * (index + 1))

  test("a per-element function receives the row, column and component of each element") {
    val module = assembled(
      declared(rows = 2, columns = 3),
      """%loaded = OpCooperativeMatrixLoadKHR %matrix %first %row_major %stride_matrix
        |%mapped = OpCooperativeMatrixPerElementOpNV %matrix %loaded %element
        |OpCooperativeMatrixStoreKHR %out %mapped %row_major %stride_matrix""".stripMargin,
      elementFunction("element", rowAndColumn)
    )
    val expected = Seq.tabulate(2 * 3)(lane => inputWords(lane) + 100L * (lane / 3) + 1000L * (lane % 3))
    assertEquals(execute(module, Seq(inputWords), 6), (Right(()), Vec.from(expected)))
  }

  test("a per-element function is called in column order") {
    val module = assembled(
      declared(rows = 2, columns = 3) + "\n%counter = OpVariable %local_ptr Workgroup",
      """OpStore %counter %zero
        |%loaded = OpCooperativeMatrixLoadKHR %matrix %first %row_major %stride_matrix
        |%mapped = OpCooperativeMatrixPerElementOpNV %matrix %loaded %element
        |OpCooperativeMatrixStoreKHR %out %mapped %row_major %stride_matrix""".stripMargin,
      elementFunction(
        "element",
        """%old = OpLoad %uint %counter
          |%new = OpIAdd %uint %old %one
          |OpStore %counter %new
          |OpReturnValue %old""".stripMargin
      )
    )
    val expected = Seq.tabulate(2 * 3)(lane => (lane % 3) * 2L + lane / 3)
    assertEquals(execute(module, Seq(inputWords), 6), (Right(()), Vec.from(expected)))
  }

  test("a per-element function takes further matrices component by component and other operands whole") {
    val module = assembled(
      declared(rows = 2, columns = 3),
      """%loaded = OpCooperativeMatrixLoadKHR %matrix %first %row_major %stride_matrix
        |%scaled = OpCooperativeMatrixLoadKHR %matrix %second %row_major %stride_matrix
        |%mapped = OpCooperativeMatrixPerElementOpNV %matrix %loaded %element %scaled %thousand
        |OpCooperativeMatrixStoreKHR %out %mapped %row_major %stride_matrix""".stripMargin,
      """%element = OpFunction %uint None %extra_fn
         |%row = OpFunctionParameter %uint
         |%column = OpFunctionParameter %uint
         |%value = OpFunctionParameter %uint
         |%beside = OpFunctionParameter %uint
         |%whole = OpFunctionParameter %uint
         |%element_entry = OpLabel
         |%product = OpIMul %uint %beside %whole
         |%sum = OpIAdd %uint %value %product
         |OpReturnValue %sum
         |OpFunctionEnd""".stripMargin
    )
    val beside = Seq(1L, 2, 3, 4, 5, 6)
    assertEquals(
      execute(module, Seq(inputWords, beside), 6)._2,
      Vec.from(inputWords.zip(beside).map((v, b) => v + b * 1000L))
    )
  }

  test("a per-element function can call another function") {
    val module = assembled(
      declared(rows = 2, columns = 3) + "\n%twice_fn = OpTypeFunction %uint %uint",
      """%loaded = OpCooperativeMatrixLoadKHR %matrix %first %row_major %stride_matrix
        |%mapped = OpCooperativeMatrixPerElementOpNV %matrix %loaded %element
        |OpCooperativeMatrixStoreKHR %out %mapped %row_major %stride_matrix""".stripMargin,
      elementFunction(
        "element",
        """%doubled = OpFunctionCall %uint %twice %value
          |%shifted = OpFunctionCall %uint %twice %doubled
          |%sum = OpIAdd %uint %shifted %row
          |OpReturnValue %sum""".stripMargin
      ) +
        """
          |%twice = OpFunction %uint None %twice_fn
          |%number = OpFunctionParameter %uint
          |%twice_entry = OpLabel
          |%product = OpIMul %uint %number %two
          |OpReturnValue %product
          |OpFunctionEnd""".stripMargin
    )
    val expected = Seq.tabulate(6)(lane => inputWords(lane) * 4 + lane / 3)
    assertEquals(execute(module, Seq(inputWords), 6), (Right(()), Vec.from(expected)))
  }

  test("a per-element function can apply another per-element function to a matrix of its own") {
    val module = assembled(
      declared(rows = 2, columns = 2),
      """%loaded = OpCooperativeMatrixLoadKHR %matrix %first %row_major %stride_matrix
        |%mapped = OpCooperativeMatrixPerElementOpNV %matrix %loaded %outer
        |OpCooperativeMatrixStoreKHR %out %mapped %row_major %stride_matrix""".stripMargin,
      elementFunction(
        "outer",
        """%filled = OpCompositeConstruct %matrix %value
          |%inner_mapped = OpCooperativeMatrixPerElementOpNV %matrix %filled %inner
          |%last = OpCompositeExtract %uint %inner_mapped 3
          |OpReturnValue %last""".stripMargin
      ) + "\n" + elementFunction("inner", rowAndColumn)
    )
    val values = Seq(7L, 8, 9, 10)
    assertEquals(execute(module, Seq(values), 4), (Right(()), Vec.from(values.map(_ + 1100L))))
  }

  test("every invocation of a workgroup runs its own sequence of calls") {
    val module = assembled(
      declared(rows = 2, columns = 3),
      """%loaded = OpCooperativeMatrixLoadKHR %matrix %first %row_major %stride_matrix
        |%mapped = OpCooperativeMatrixPerElementOpNV %matrix %loaded %element
        |OpCooperativeMatrixStoreKHR %out %mapped %row_major %stride_matrix""".stripMargin,
      elementFunction("element", rowAndColumn)
    )
    val expected = Seq.tabulate(6)(lane => inputWords(lane) + 100L * (lane / 3) + 1000L * (lane % 3))
    assertEquals(execute(module, Seq(inputWords), 6, local = 4), (Right(()), Vec.from(expected)))
  }

  private def reduction(
      mode: String,
      source: (rows: Int, columns: Int),
      result: (rows: Int, columns: Int),
      functions: String = combine
  ): Module = assembled(
    declared(rows = source.rows, columns = source.columns, name = "source") + "\n" +
      declared(rows = result.rows, columns = result.columns, name = "target"),
    s"""%loaded = OpCooperativeMatrixLoadKHR %source %first %row_major %stride_source
       |%reduced = OpCooperativeMatrixReduceNV %target %loaded $mode %combine
       |OpCooperativeMatrixStoreKHR %out %reduced %row_major %stride_target""".stripMargin,
    functions
  )

  private def fold(lanes: Seq[Int]): Long = lanes.map(_.toLong).reduceLeft(_ * 10 + _)

  private val numbers = Seq.tabulate(16)(_ + 1L)

  for {
    (mode, source, result, groups) <-
      Seq[(String, (rows: Int, columns: Int), (rows: Int, columns: Int), Seq[Seq[Int]])](
        ("Row", (2, 3), (2, 2), Seq(Seq(1, 2, 3), Seq(1, 2, 3).map(_ + 3))),
        ("Column", (2, 3), (3, 3), Seq(Seq(1, 4), Seq(2, 5), Seq(3, 6))),
        ("Row|Column", (2, 3), (2, 2), Seq(Seq(1, 2, 3, 4, 5, 6))),
        ("2x2", (4, 4), (2, 2), Seq(Seq(1, 5, 2, 6), Seq(3, 7, 4, 8), Seq(9, 13, 10, 14), Seq(11, 15, 12, 16)))
      )
  }
    test(s"a $mode reduction folds each group of components through the function in order") {
      val expected = Seq.tabulate(result.rows * result.columns) { lane =>
        val group = mode match {
          case "Row"    => lane / result.columns
          case "Column" => lane % result.columns
          case "2x2"    => lane
          case _        => 0
        }
        fold(groups(group))
      }
      assertEquals(
        execute(reduction(mode, source, result), Seq(numbers.take(source.rows * source.columns)), expected.size),
        (Right(()), Vec.from(expected))
      )
    }

  test("a reduction over single components returns them") {
    assertEquals(
      execute(reduction("Row", (3, 1), (3, 2)), Seq(Seq(4L, 5, 6)), 6),
      (Right(()), Vec(4L, 4, 5, 5, 6, 6))
    )
    assertEquals(
      execute(reduction("Column", (1, 3), (2, 3)), Seq(Seq(4L, 5, 6)), 6),
      (Right(()), Vec(4L, 5, 6, 4, 5, 6))
    )
  }

  test("a reduction function can call another function") {
    val functions =
      """%combine = OpFunction %uint None %combine_fn
        |%left = OpFunctionParameter %uint
        |%right = OpFunctionParameter %uint
        |%combine_entry = OpLabel
        |%folded = OpFunctionCall %uint %shift %left %right
        |OpReturnValue %folded
        |OpFunctionEnd
        |%shift = OpFunction %uint None %combine_fn
        |%high = OpFunctionParameter %uint
        |%low = OpFunctionParameter %uint
        |%shift_entry = OpLabel
        |%shifted = OpIMul %uint %high %ten
        |%joined = OpIAdd %uint %shifted %low
        |OpReturnValue %joined
        |OpFunctionEnd""".stripMargin
    assertEquals(
      execute(reduction("Row", (2, 3), (2, 3), functions), Seq(numbers.take(6)), 6),
      (Right(()), Vec(123L, 123, 123, 456, 456, 456))
    )
  }

  test(
    "a reduction by a mode that names no reduction, or by a function of another shape, fails when the module loads"
  ) {
    val failing = Seq(
      reduction("Row|2x2", (4, 4), (2, 2)),
      reduction("Row", (2, 3), (3, 3)),
      reduction("Column", (2, 3), (2, 2)),
      reduction("2x2", (4, 4), (2, 3)),
      reduction(
        "Row",
        (2, 3),
        (2, 3),
        combine.replace("%combine_fn", "%element_fn").replace(
          "%right = OpFunctionParameter %uint",
          "%right = OpFunctionParameter %uint\n%extra = OpFunctionParameter %uint"
        )
      )
    )
    failing.foreach { module =>
      val at      = module.instructions.indexWhere(_.opcode.raw == "OpCooperativeMatrixReduceNV")
      val failure = SPIRVLowering.failures(module, environment(module)).map(_(at))
      assert(
        failure.exists(_.exists {
          case Fault.InvalidRequest(message) => message.raw.contains("OpCooperativeMatrixReduceNV"); case _ => false
        }),
        failure.toString
      )
    }
  }
}
