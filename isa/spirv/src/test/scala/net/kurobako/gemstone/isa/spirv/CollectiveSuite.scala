package net.kurobako.gemstone.isa.spirv

import fixtures.*
import java.util.concurrent.atomic.AtomicInteger
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class CollectiveSuite extends munit.FunSuite {
  private val Operations = Seq("Reduce", "InclusiveScan", "ExclusiveScan")

  private case class GroupCase(
      name: String,
      value: String,
      body: String,
      width: Int,
      input: Seq[Long],
      expected: Seq[Long],
      local: Dimensions = testDimensions(8),
      groups: Dimensions = Dimensions.One
  )

  private val ramp = Seq.tabulate(16)(_.toLong)

  private def anyMinimum(scope: String): String =
    s"""
      %predicate = OpULessThan %bool %x %three_long
      %any = OpGroupAny %bool $scope %predicate
      %minimum = OpGroupUMin %ulong %workgroup Reduce %x
      %result = OpSelect %ulong %any %minimum %zero_long
    """

  // wider accumulation would round 1 + 2 * 4e-8 up to the next binary32 value
  private def rounding(opcode: String, input: Seq[Float], combine: (Float, Float) => Float): GroupCase = GroupCase(
    s"$opcode InclusiveScan rounds every step to binary32",
    "%float",
    s"%result = $opcode %float %workgroup InclusiveScan %x",
    32,
    input.map(floatBits),
    input.tail.scanLeft(input.head)(combine).map(floatBits)
  )

  private val groupCases = {
    val input  = Seq(7L, -3, 9, -8, 4, 1, -2, 6)
    val minMax = for {
      (opcode, value, prefix) <- Seq(
        ("GroupSMin", "%int", Seq(7L, -3, -3, -8, -8, -8, -8, -8)),
        ("GroupSMax", "%int", Seq(7L, 7, 9, 9, 9, 9, 9, 9)),
        ("GroupUMin", "%uint", Seq(7L, 7, 7, 7, 4, 1, 1, 1)),
        ("GroupUMax", "%uint", Seq(7L, -3, -3, -3, -3, -3, -2, -2)),
        ("GroupFMin", "%float", Seq(7L, -3, -3, -8, -8, -8, -8, -8)),
        ("GroupFMax", "%float", Seq(7L, 7, 9, 9, 9, 9, 9, 9))
      )
      operation <- Seq("Reduce", "InclusiveScan")
    } yield {
      def bits(values: Seq[Long]) = if (value == "%float") values.map(v => floatBits(v.toFloat)) else values
      val expected                = if (operation == "Reduce") Seq.fill(8)(prefix.last) else prefix
      GroupCase(
        s"Op$opcode $operation",
        value,
        s"%result = Op$opcode $value %workgroup $operation %x",
        32,
        bits(input),
        bits(expected)
      )
    }
    val iAdd      = (operation: String) => s"%result = OpGroupIAdd %ulong %workgroup $operation %x"
    val broadcast = (scope: String) =>
      s"%scan = OpGroupIAdd %ulong %workgroup InclusiveScan %x\n%result = OpGroupBroadcast %ulong $scope %scan %three"
    minMax ++ Seq(
      GroupCase("OpGroupIAdd Reduce", "%ulong", iAdd("Reduce"), 64, ramp.take(8), Seq.fill(8)(28L)),
      GroupCase(
        "OpGroupIAdd InclusiveScan",
        "%ulong",
        iAdd("InclusiveScan"),
        64,
        ramp.take(8),
        Seq(0L, 1, 3, 6, 10, 15, 21, 28)
      ),
      GroupCase(
        "OpGroupIAdd ExclusiveScan",
        "%ulong",
        iAdd("ExclusiveScan"),
        64,
        ramp.take(8),
        Seq(0L, 0, 1, 3, 6, 10, 15, 21)
      ),
      GroupCase(
        "OpGroupIMulKHR Reduce of a constant",
        "%ulong",
        "%result = OpGroupIMulKHR %ulong %workgroup Reduce %three_long",
        64,
        ramp.take(8),
        Seq.fill(8)(6561L)
      ),
      GroupCase(
        "OpGroupFAdd InclusiveScan of a constant",
        "%float",
        "%result = OpGroupFAdd %float %workgroup InclusiveScan %three_float",
        32,
        Seq.fill(8)(0L),
        (1 to 8).map(index => floatBits(3f * index))
      ),
      GroupCase(
        "OpGroupFMulKHR InclusiveScan of a constant",
        "%float",
        "%result = OpGroupFMulKHR %float %workgroup InclusiveScan %three_float",
        32,
        Seq.fill(8)(0L),
        (1 to 8).map(index => floatBits(math.pow(3.0, index).toFloat))
      ),
      rounding("OpGroupFAdd", 1f +: Seq.fill(7)(4e-8f), _ + _),
      rounding("OpGroupFMulKHR", Seq.fill(8)(1.1f), _ * _),
      GroupCase(
        "OpGroupBroadcast over Workgroup of a scan",
        "%ulong",
        broadcast("%workgroup"),
        64,
        ramp.take(8),
        Seq.fill(8)(6L)
      ),
      GroupCase(
        "OpGroupBroadcast over Subgroup of a scan",
        "%ulong",
        broadcast("%subgroup"),
        64,
        ramp,
        Seq.fill(16)(6L),
        local = testDimensions(16)
      ),
      GroupCase(
        "OpGroupAny over Subgroup and OpGroupUMin in single-invocation workgroups",
        "%ulong",
        anyMinimum("%subgroup"),
        64,
        ramp.take(8),
        Seq(0L, 1, 2, 0, 0, 0, 0, 0),
        testDimensions(1),
        testDimensions(8)
      ),
      GroupCase(
        "OpGroupAny over Workgroup and OpGroupUMin in single-invocation workgroups",
        "%ulong",
        anyMinimum("%workgroup"),
        64,
        ramp.take(8),
        Seq(0L, 1, 2, 0, 0, 0, 0, 0),
        testDimensions(1),
        testDimensions(8)
      ),
      GroupCase(
        "OpGroupAny and OpGroupUMin after two control barriers",
        "%ulong",
        "OpControlBarrier %workgroup %workgroup %workgroup\nOpControlBarrier %workgroup %workgroup %workgroup\n" +
          anyMinimum("%subgroup"),
        64,
        ramp.take(8),
        Seq.fill(8)(0L)
      )
    )
  }

  for (row <- groupCases) test(s"${row.name} writes the result of every invocation") {
    val output = dispatch(
      kernel(row.value, row.body),
      row.local,
      row.groups,
      littleEndian(row.input, row.width),
      row.expected.size * row.width / 8
    )
    assertEquals(output, littleEndian(row.expected, row.width))
  }

  for {
    logical  <- Seq(false, true)
    operator <- Seq("And", "Or", "Xor")
    vector   <- Seq(false, true)
  } test(
    s"OpGroup${if (logical) "Logical" else "Bitwise"}${operator}KHR ${if (vector) "vector" else "scalar"} " +
      "reductions and scans"
  ) {
    for {
      width     <- if (logical) Seq(64) else Seq(8, 16, 32, 64)
      subgroup  <- Seq(false, true)
      operation <- Operations
    } {
      val lanes    = if (vector) 2 else 1
      val module   = khr(operator, logical, vector, width, if (subgroup) "%subgroup" else "%workgroup", operation)
      val output   = dispatch(module, testDimensions(40), testDimensions(2), Vec.empty, 80 * lanes * 8)
      val mask     = if (width == 64) -1L else (1L << width) - 1L
      val identity = if (operator != "And") 0L else if (logical) 1L else mask
      def combine(a: Long, b: Long): Long = operator match {
        case "And" => a & b
        case "Or"  => a | b
        case _     => a ^ b
      }
      val expected = (0 until 80).flatMap { global =>
        val start = global / 40 * 40 + (if (subgroup) global % 40 / 32 * 32 else 0)
        val end   = math.min(global / 40 * 40 + 40, start + (if (subgroup) 32 else 40))
        val until = operation match {
          case "Reduce"        => end
          case "InclusiveScan" => global + 1
          case _               => global
        }
        Seq.tabulate(lanes) { lane =>
          (start until until).foldLeft(identity) { (acc, index) =>
            val first = if (logical) { if (index % 4 != 3) 1L else 0L }
            else ((1L << (width - 1)) + index) & mask
            combine(acc, if (lane == 0) first else if (logical) first ^ 1L else ~first & mask)
          }
        }
      }
      assertEquals(output, littleEndian(expected, 64), s"width $width, subgroup $subgroup, $operation")
    }
  }

  test("group operations reject unsupported scopes and operations and mistyped operands") {
    def failure(module: Module): Fault = outcome(module, testDimensions(2), Dimensions.One, Vec.empty, 16)._1.leftValue
    def unsupported(module: Module): Boolean = failure(module) match {
      case Fault.Unsupported(_) => true
      case _                    => false
    }
    def invalid(module: Module): Boolean = failure(module) match {
      case Fault.InvalidRequest(_) => true
      case _                       => false
    }
    for (logical <- Seq(false, true)) {
      assert(unsupported(khr("And", logical, false, 32, "%device", "Reduce")))
      assert(unsupported(khr("And", logical, false, 32, "%workgroup", "ClusteredReduce")))
      assert(invalid(khr("And", logical, false, 32, "%workgroup", "Reduce", mistyped = true)))
    }
    failure(kernel("%ulong", anyMinimum("%device"))) match {
      case Fault.Unsupported(message) =>
        assertEquals(message, "OpGroupAny execution scope 1 is not Subgroup or Workgroup")
      case result => fail(s"expected an unsupported collective scope, found $result")
    }
    val malformed = anyMinimum("%subgroup") + "\n%bad = OpLogicalNotEqual %ulong %any %true"
    failure(kernel("%ulong", malformed, "%true = OpConstantTrue %bool")) match {
      case Fault.InvalidRequest(message) =>
        assertEquals(message, "OpLogicalNotEqual requires matching boolean scalar or vector operand and result types")
      case result => fail(s"expected a malformed logical operation, found $result")
    }
  }

  for {
    xor    <- Seq(false, true)
    vector <- Seq(false, true)
  } test(
    s"OpSubgroupShuffle${if (xor) "Xor" else ""}INTEL ${if (vector) "vector" else "scalar"} " +
      "handles lane selectors and partial subgroups"
  ) {
    for {
      mask    <- Seq(0, 1, 3, 32, Int.MinValue)
      varying <- Seq(false, true)
    } {
      val lanes  = if (vector) 2 else 1
      val data   = if (vector) "%pair" else "%payload"
      val module = kernel(
        if (vector) "%ulong2" else "%ulong",
        s"""
          %payload = OpIAdd %ulong %high %index
          ${if (vector) "%pair = OpCompositeConstruct %ulong2 %index %payload" else ""}
          %lane = OpBitwiseAnd %uint %index32 %three
          %result = OpSubgroupShuffle${if (xor) "Xor" else ""}INTEL ${if (vector) "%ulong2" else "%ulong"} $data ${
            if (varying) "%lane" else "%mask"
          }
        """,
        s"%mask = OpConstant %uint 0x${mask.toHexString}"
      )
      val output = dispatch(module, testDimensions(40), testDimensions(2), Vec.empty, 80 * lanes * 8)
      for (global <- 0 until 80) {
        val local         = global % 40
        val subgroupStart = local / 32 * 32
        val subgroupSize  = math.min(32, 40 - subgroupStart)
        val selector      = if (varying) global & 3 else mask
        val source        = if (xor) (local % 32) ^ selector else selector
        // an out-of-range source lane has an unspecified value
        if (source >= 0 && source < subgroupSize) {
          val sourceGlobal = (global / 40 * 40 + subgroupStart + source).toLong
          val expected     = (if (vector) Seq(sourceGlobal) else Seq.empty) :+ (0x1234567800000000L + sourceGlobal)
          assertEquals(
            output.slice(global * lanes * 8, (global + 1) * lanes * 8),
            littleEndian(expected, 64),
            s"mask $mask, varying $varying, invocation $global"
          )
        }
      }
    }
  }

  // SPV_INTEL_subgroups permits Delta to differ between invocations
  for {
    count                <- Seq(8, 64)
    (operation, varying) <- Seq("" -> false, "Up" -> false, "Down" -> false, "Up" -> true, "Down" -> true)
  } test(
    s"OpSubgroupShuffle${operation}INTEL with ${if (varying) "varying" else "uniform"} delta routes every lane in " +
      s"$count invocations"
  ) {
    val size    = math.min(count, 32)
    val delta   = if (varying) "%lane" else "%one"
    val shuffle = operation match {
      case "Up"   => s"OpSubgroupShuffleUpINTEL %uint %other %current $delta"
      case "Down" => s"OpSubgroupShuffleDownINTEL %uint %current %other $delta"
      case _      => "OpSubgroupShuffleINTEL %uint %current %reversed"
    }
    val module = kernel(
      "%uint",
      s"""
        %lane = OpUMod %uint %local_index %size
        %current = OpIAdd %uint %local_index %first_base
        %other = OpIAdd %uint %local_index %second_base
        %last = OpISub %uint %size %one
        %reversed = OpISub %uint %last %lane
        %result = $shuffle
      """,
      s"""
        %size = OpConstant %uint $size
        %first_base = OpConstant %uint 1000
        %second_base = OpConstant %uint 2000
      """
    )
    val output   = dispatch(module, testDimensions(count.refine[Int > 0].value), Dimensions.One, Vec.empty, count * 4)
    val expected = Seq.tabulate(count) { index =>
      val group = index / size * size
      val lane  = index % size
      val delta = if (varying) lane else 1
      operation match {
        case "Up" if lane < delta           => 2000 + group + size + lane - delta
        case "Up"                           => 1000 + group + lane - delta
        case "Down" if lane + delta >= size => 2000 + group + lane + delta - size
        case "Down"                         => 1000 + group + lane + delta
        case _                              => 1000 + group + size - 1 - lane
      }
    }
    assertEquals(output, littleEndian(expected.map(_.toLong), 32))
  }

  test("control barriers accept every memory scope and propagate memory fence failures") {
    for {
      scope       <- 0 to 6
      execution   <- Seq("%workgroup", "%subgroup")
      rejectFence <- Seq(false, true)
    } {
      val module = kernel(
        "%ulong",
        s"OpControlBarrier $execution %memory %semantics\n%result = OpIAdd %ulong %high %index",
        s"%memory = OpConstant %uint $scope\n%semantics = OpConstant %uint 0x308"
      )
      val fences  = AtomicInteger()
      val refusal = Fault.Backend("test fence failure")
      class FencedMemory(memory: RegionMemory) extends ForwardingMemory(memory) {
        override def fenceHostMemory(): Either[Fault, Unit] = {
          val _ = fences.incrementAndGet()
          if (rejectFence) Left(refusal) else Right(())
        }
      }
      val (result, output) = outcome(module, testDimensions(40), testDimensions(2), Vec.empty, 80 * 8, FencedMemory(_))
      val external         = scope == 0 || scope == 1 || scope >= 5
      if (rejectFence && external) {
        assertEquals(result, Left(refusal))
        assertEquals(output, Vec.fill(80 * 8)(0.toByte))
      } else {
        assertEquals(result, Right(()), s"memory scope $scope, execution scope $execution")
        assertEquals(fences.get() > 0, external)
        assertEquals(output.take(8), littleEndian(Seq(0x1234567800000000L), 64))
      }
    }
  }

  test("OpGroupBroadcast selects both coordinates of a two-dimensional local id") {
    val module = kernel(
      "%ulong",
      """
        %y = OpCompositeExtract %ulong %ids 1
        %row = OpIMul %ulong %y %four
        %linear = OpIAdd %ulong %row %index
        %payload = OpIAdd %ulong %high %linear
        %result = OpGroupBroadcast %ulong %workgroup %payload %coordinate
      """,
      """
        %four = OpConstant %ulong 4
        %two_long = OpConstant %ulong 2
        %coordinate = OpConstantComposite %ulong2 %two_long %one_long
      """,
      slot = "%linear"
    )
    val output = dispatch(module, testDimensions(4, 3), Dimensions.One, Vec.empty, 12 * 8)
    assertEquals(output, littleEndian(Seq.fill(12)(0x1234567800000006L), 64))
  }

  test("OpGroupNonUniformBallot collects the predicate of every invocation") {
    val module = kernel(
      "%uint",
      """
        %predicate = OpULessThan %bool %index %three_long
        %ballot = OpGroupNonUniformBallot %uint4 %subgroup %predicate
        %result = OpCompositeExtract %uint %ballot 0
      """,
      "%uint4 = OpTypeVector %uint 4"
    )
    assertEquals(
      dispatch(module, testDimensions(8), Dimensions.One, Vec.empty, 8 * 4),
      littleEndian(Seq.fill(8)(7L), 32)
    )
  }

  test("OpenCL ballot counts and rotations span the maximum subgroup size in a partial subgroup") {
    val module = kernel(
      "%uint",
      """
        %count = OpGroupNonUniformBallotBitCount %uint %subgroup Reduce %everyone
        %lane = OpUConvert %uint %index
        %rotated = OpGroupNonUniformRotateKHR %uint %subgroup %lane %delta
        %scaled = OpIMul %uint %count %thousand
        %result = OpIAdd %uint %scaled %rotated
      """,
      """
        %uint4 = OpTypeVector %uint 4
        %all = OpConstant %uint 0xffffffff
        %everyone = OpConstantComposite %uint4 %all %all %all %all
        %delta = OpConstant %uint 30
        %thousand = OpConstant %uint 1000
      """
    )
    // The 8-lane partial subgroup counts 32 ballot bits; lanes rotating past it keep their own value.
    val expected = (0 until 40).map { lane =>
      val (base, local) = (lane / 32 * 32, lane % 32)
      val source        = (local + 30) % 32
      32000L + (if (lane < 32 || source < 8) base + source else lane).toLong
    }
    assertEquals(dispatch(module, testDimensions(40), Dimensions.One, Vec.empty, 40 * 4), littleEndian(expected, 32))
  }

  test("clustered reductions combine each aligned cluster of the subgroup separately") {
    val module = kernel(
      "%uint",
      """
        %lane = OpUConvert %uint %index
        %result = OpGroupNonUniformIAdd %uint %subgroup ClusteredReduce %lane %cluster
      """,
      "%cluster = OpConstant %uint 4"
    )
    assertEquals(
      dispatch(module, testDimensions(8), Dimensions.One, Vec.empty, 8 * 4),
      littleEndian(Seq(6L, 6L, 6L, 6L, 22L, 22L, 22L, 22L), 32)
    )
  }

  for (
    (
      cluster,
      expected
    ) <- Seq(None -> Seq(1L, 2L, 3L, 4L, 5L, 6L, 7L, 0L), Some(4) -> Seq(1L, 2L, 3L, 0L, 5L, 6L, 7L, 4L))
  )
    test(s"OpGroupNonUniformRotateKHR reads the lane delta ahead within ${cluster.fold("the subgroup")(size =>
        s"clusters of $size"
      )}") {
      val module = kernel(
        "%uint",
        s"""
          %lane = OpUConvert %uint %index
          %result = OpGroupNonUniformRotateKHR %uint %subgroup %lane %delta${cluster.fold("")(_ => " %cluster")}
        """,
        s"%delta = OpConstant %uint 1\n%cluster = OpConstant %uint ${cluster.getOrElse(1)}"
      )
      assertEquals(dispatch(module, testDimensions(8), Dimensions.One, Vec.empty, 8 * 4), littleEndian(expected, 32))
    }

  test("only functions that reach a collective are marked collective") {
    val program = load(spv"""
      $openCL
      OpEntryPoint Kernel %plain "plain"
      OpEntryPoint Kernel %collective "collective"
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %fn = OpTypeFunction %void
      %workgroup = OpConstant %uint 2
      %plain = OpFunction %void None %fn
      %plain_entry = OpLabel
      OpReturn
      OpFunctionEnd
      %collective = OpFunction %void None %fn
      %collective_entry = OpLabel
      %call = OpFunctionCall %void %helper
      OpReturn
      OpFunctionEnd
      %helper = OpFunction %void None %fn
      %helper_entry = OpLabel
      OpControlBarrier %workgroup %workgroup %workgroup
      OpReturn
      OpFunctionEnd
    """).value
    assertEquals(program.collectiveFunctions, program.functions.keySet - program.entryPoints("plain"))
    class DeferringMemory(deferred: Boolean) extends ForwardingMemory(RejectingMemory()) {
      override def defersHostReads: Boolean = deferred
    }
    for {
      name     <- Seq[NonEmpty[String]]("plain", "collective")
      deferred <- Seq(false, true)
    } {
      val launch = task(name, local = testDimensions(8), groups = testDimensions(2))
      assertEquals(execute(program, launch, DeferringMemory(deferred)), Right(()), s"$name, deferred $deferred")
    }
  }

  /** `body` computes `%result` of type `value` from `%index`, the global invocation id, `%local_index`, and `%x`, the
    * input at `%index`; the kernel stores `%result` at `slot`.
    */
  private def kernel(value: String, body: String, declarations: String = "", slot: String = "%index"): Module = spv"""
    $openCL
    OpEntryPoint Kernel %main "collective" %gid %lid
    OpDecorate %gid BuiltIn GlobalInvocationId
    OpDecorate %lid BuiltIn LocalInvocationIndex
    %void = OpTypeVoid
    %bool = OpTypeBool
    %uint = OpTypeInt 32 0
    %int = OpTypeInt 32 1
    %ulong = OpTypeInt 64 0
    %float = OpTypeFloat 32
    %ulong2 = OpTypeVector %ulong 2
    %ulong3 = OpTypeVector %ulong 3
    %gid_ptr = OpTypePointer Input %ulong3
    %gid = OpVariable %gid_ptr Input
    %lid_ptr = OpTypePointer Input %uint
    %lid = OpVariable %lid_ptr Input
    %device = OpConstant %uint 1
    %workgroup = OpConstant %uint 2
    %subgroup = OpConstant %uint 3
    %one = OpConstant %uint 1
    %three = OpConstant %uint 3
    %zero_long = OpConstant %ulong 0
    %one_long = OpConstant %ulong 1
    %three_long = OpConstant %ulong 3
    %high = OpConstant %ulong 0x1234567800000000
    %three_float = OpConstant %float 3.0
    $declarations
    %value_ptr = OpTypePointer CrossWorkgroup $value
    %fn = OpTypeFunction %void %value_ptr %value_ptr
    %main = OpFunction %void None %fn
    %in = OpFunctionParameter %value_ptr
    %out = OpFunctionParameter %value_ptr
    %entry = OpLabel
    %ids = OpLoad %ulong3 %gid
    %index = OpCompositeExtract %ulong %ids 0
    %index32 = OpUConvert %uint %index
    %local_index = OpLoad %uint %lid
    %in_ptr = OpPtrAccessChain %value_ptr %in %index
    %x = OpLoad $value %in_ptr
    $body
    %out_ptr = OpPtrAccessChain %value_ptr %out $slot
    OpStore %out_ptr %result
    OpReturn
    OpFunctionEnd
  """

  /** KHR group operation over `width`-bit payloads `0x80.. + index`, or over `index % 4 != 3` when `logical`; vector
    * forms add the complement as a second lane. `mistyped` passes an operand of the wrong type.
    */
  private def khr(
      operator: String,
      logical: Boolean,
      vector: Boolean,
      width: Int,
      scope: String,
      operation: String,
      mistyped: Boolean = false
  ): Module = {
    val element  = if (logical) "%bool" else "%element"
    val dataType = if (vector) "%pair_type" else element
    val operand  = if (!mistyped) { if (vector) "%pair" else "%scalar" }
    else if (logical) "%payload"
    else "%pair"
    val value  = if (vector) "%ulong2" else "%ulong"
    val widen  = if (width == 64) "OpCopyObject" else "OpUConvert"
    val result =
      if (!logical) s"%result = $widen $value %group"
      else if (vector) "%result = OpSelect %ulong2 %group %ones %zeros"
      else "%result = OpSelect %ulong %group %one_long %zero_long"
    kernel(
      value,
      s"""
        %payload = OpIAdd %ulong %sign %index
        %lane = OpBitwiseAnd %uint %index32 %three
        ${if (logical) "%scalar = OpINotEqual %bool %lane %three" else s"%scalar = $widen %element %payload"}
        %inverse = ${if (logical) "OpLogicalNot" else "OpNot"} $element %scalar
        %pair = OpCompositeConstruct %pair_type %scalar %inverse
        %group = OpGroup${if (logical) "Logical" else "Bitwise"}${operator}KHR $dataType $scope $operation $operand
        $result
      """,
      s"""
        ${if (logical) "" else s"%element = OpTypeInt $width 0"}
        %pair_type = OpTypeVector $element 2
        %sign = OpConstant %ulong 0x${(1L << (width - 1)).toHexString}
        %zeros = OpConstantComposite %ulong2 %zero_long %zero_long
        %ones = OpConstantComposite %ulong2 %one_long %one_long
      """
    )
  }

  /** Runs `module` with `input` as `%in`, or with the output as `%in` when `input` is empty. */
  private def outcome(
      module: Module,
      local: Dimensions,
      groups: Dimensions,
      input: Vec[Byte],
      size: Int,
      space: RegionMemory => AddressSpace = identity
  ): (Either[Fault, Unit], Vec[Byte]) = {
    val memory = RegionMemory()
    val out    = memory.allocate(size.toLong.refine[Long > 0].value)
    val in     = if (input.isEmpty) out else memory.allocate(input)
    (run(module, "collective", space(memory), Vec(buffer(in), buffer(out)), local, groups), memory.bytes(out))
  }

  private def dispatch(module: Module, local: Dimensions, groups: Dimensions, input: Vec[Byte], size: Int)
      : Vec[Byte] = {
    val (result, output) = outcome(module, local, groups, input, size)
    assertEquals(result, Right(()))
    output
  }
}
