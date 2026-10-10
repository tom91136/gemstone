package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*

class NonUniformSuite extends munit.FunSuite {
  // two complete subgroups and one partial subgroup
  private final val Count = 69
  private val Operations  = Seq("Reduce", "InclusiveScan", "ExclusiveScan")
  private val Branches    = Seq("uniform", "divergent", "return")

  private val integer = pairwise(
    Seq("IAdd", "IMul", "SMin", "SMax", "UMin", "UMax", "BitwiseAnd", "BitwiseOr", "BitwiseXor"),
    Operations,
    Branches,
    Seq(8, 16, 32, 64),
    Seq(1, 2, 4),
    Seq(false, true)
  )

  // deferred reads need strides that are not multiples of 8; min/max identities at the widths pairwise skips
  private val integerExtra = Seq(
    ("IAdd", "Reduce", "uniform", 32, 1, true),
    ("IAdd", "ExclusiveScan", "uniform", 32, 1, true),
    ("IAdd", "InclusiveScan", "return", 32, 1, true),
    ("IAdd", "ExclusiveScan", "return", 32, 1, true),
    ("SMin", "ExclusiveScan", "uniform", 64, 1, false),
    ("SMax", "ExclusiveScan", "uniform", 8, 1, false)
  )

  for ((kind, operation, branch, width, lanes, deferred) <- integer ++ integerExtra.filterNot(integer.toSet))
    test(
      s"OpGroupNonUniform$kind $operation reconverges after a $branch branch at width $width, lanes $lanes" +
        (if (deferred) " with deferred reads" else "")
    ) {
      val bits                                       = (BigInt(1) << width) - 1
      def input(thread: Int, component: Int): BigInt =
        if (kind == "IMul") BigInt(if ((thread + component) % 2 == 0) -1 else 1) & bits
        else if (thread % 5 == 0) ((BigInt(1) << (width - 1)) + component) & bits
        else BigInt(thread % 7 + component - 3) & bits
      def signed(x: BigInt): BigInt = if (x.testBit(width - 1)) x - (BigInt(1) << width) else x
      val identity                  = kind match {
        case "IMul"                => BigInt(1)
        case "SMin"                => (BigInt(1) << (width - 1)) - 1
        case "SMax"                => BigInt(1) << (width - 1)
        case "UMin" | "BitwiseAnd" => bits
        case _                     => BigInt(0)
      }
      def combine(a: BigInt, b: BigInt): BigInt =
        (kind match {
          case "IAdd"       => a + b
          case "IMul"       => a * b
          case "SMin"       => if (signed(a) < signed(b)) a else b
          case "SMax"       => if (signed(a) > signed(b)) a else b
          case "UMin"       => a.min(b)
          case "UMax"       => a.max(b)
          case "BitwiseAnd" => a & b
          case "BitwiseOr"  => a | b
          case _            => a ^ b
        }) & bits
      val module =
        kernel(s"OpTypeInt $width 0", lanes, branch, 0, s"OpGroupNonUniform$kind %value %subgroup $operation %x")
      val outputs = dispatch(module, width, lanes, (thread, component) => input(thread, component).toLong, deferred)
      check(outputs, width, lanes, branch, 0) { (thread, masked) =>
        val selected = scanned(operation, thread, participants(thread, masked, 0))
        Some(Seq.tabulate(lanes)(c => selected.foldLeft(identity)((a, t) => combine(a, input(t, c))).toLong))
      }
    }

  private val floating = pairwise(
    Seq("FAdd", "FMul", "FMin", "FMax"),
    Operations,
    Branches,
    Seq[Int in (16, 32, 64)](16, 32, 64),
    Seq(1, 2, 4)
  )

  private val floatingIdentities = Seq[(String, String, String, Int in (16, 32, 64), Int)](
    ("FMin", "ExclusiveScan", "uniform", 64, 1),
    ("FMax", "ExclusiveScan", "uniform", 16, 1)
  )

  for ((kind, operation, branch, width, lanes) <- floating ++ floatingIdentities.filterNot(floating.toSet))
    test(s"OpGroupNonUniform$kind $operation reconverges after a $branch branch at width $width, lanes $lanes") {
      def input(thread: Int, component: Int): Double =
        if (kind == "FMul") if ((thread + component) % 2 == 0) -1.0 else 1.0
        else (thread % 5 + component + 1).toDouble
      val identity = kind match {
        case "FAdd" => 0.0
        case "FMul" => 1.0
        case "FMin" => Double.PositiveInfinity
        case _      => Double.NegativeInfinity
      }
      def combine(a: Double, b: Double): Double = kind match {
        case "FAdd" => a + b
        case "FMul" => a * b
        case "FMin" => math.min(a, b)
        case _      => math.max(a, b)
      }
      val module =
        kernel(s"OpTypeFloat $width", lanes, branch, 0, s"OpGroupNonUniform$kind %value %subgroup $operation %x")
      val outputs =
        dispatch(module, width.raw, lanes, (thread, component) => IEEE754.fromDouble(input(thread, component), width))
      check(outputs, width.raw, lanes, branch, 0) { (thread, masked) =>
        val selected = scanned(operation, thread, participants(thread, masked, 0))
        // The oracle reduces in the subgroup's order, so it folds over boxed doubles.
        @scala.annotation.nowarn("msg=baumkuchen:loop-boxing")
        val reduced = Seq.tabulate(lanes)(c =>
          IEEE754.fromDouble(selected.foldLeft(identity)((a, t) => combine(a, input(t, c))), width)
        )
        Some(reduced)
      }
    }

  for {
    (kind, branch, width, lanes) <- pairwise(
      Seq("Broadcast", "BroadcastFirst", "Shuffle", "ShuffleXor", "ShuffleUp", "ShuffleDown"),
      Branches,
      Seq(8, 16, 32, 64),
      Seq(1, 2, 3, 4)
    )
  } test(s"OpGroupNonUniform$kind reconverges after a $branch branch at width $width, lanes $lanes") {
    val mask                                     = if (width == 64) -1L else (1L << width) - 1
    def input(thread: Int, component: Int): Long = (thread * 37L + component * 13L) & mask
    val operand                                  = kind match {
      case "Broadcast"      => "%broadcast_lane"
      case "BroadcastFirst" => ""
      case "Shuffle"        => "%shuffle_lane"
      case "ShuffleXor"     => "%two"
      case _                => "%one"
    }
    val module =
      kernel(s"OpTypeInt $width 0", lanes, branch, 1, s"OpGroupNonUniform$kind %value %subgroup %x $operand")
    val outputs = dispatch(module, width, lanes, (thread, component) => input(thread, component))
    check(outputs, width, lanes, branch, 1) { (thread, masked) =>
      val start      = thread / 32 * 32
      val local      = thread - start
      val sourceLane = kind match {
        case "Broadcast"      => ((thread / 32 & 1) << 1) + 1
        case "BroadcastFirst" => if (masked) 1 else 0
        case "Shuffle"        => (local & 2) + 1
        case "ShuffleXor"     => local ^ 2
        case "ShuffleUp"      => local - 1
        case _                => local + 1
      }
      val source = start + sourceLane
      Option.when(sourceLane >= 0 && sourceLane < 32 && source < Count && (!masked || source % 2 == 1))(
        Seq.tabulate(lanes)(input(source, _))
      )
    }
  }

  /** Applies `collective` to `%x` twice: inside a branch that only invocations of parity `active` take, then after the
    * merge, storing the results to the first and second halves of the output.
    */
  private def kernel(element: String, lanes: Int, branch: String, active: Int, collective: String): Module = {
    val value =
      if (lanes == 1) s"%value = $element" else s"%element = $element\n%value = OpTypeVector %element $lanes"
    val split = if (branch == "uniform") ""
    else """
      %parity = OpBitwiseAnd %uint %index %one
      %selected = OpIEqual %bool %parity %active
      OpSelectionMerge %merge None
      OpBranchConditional %selected %taken %skipped
      %taken = OpLabel
    """
    val merge = if (branch == "uniform") ""
    else s"""
      OpBranch %merge
      %skipped = OpLabel
      ${if (branch == "return") "OpReturn" else "OpBranch %merge"}
      %merge = OpLabel
    """
    spv"""
      $openCL
      OpEntryPoint Kernel %main "collective" %gid
      OpDecorate %gid BuiltIn GlobalInvocationId
      %void = OpTypeVoid
      %bool = OpTypeBool
      %uint = OpTypeInt 32 0
      %uint3 = OpTypeVector %uint 3
      %gid_ptr = OpTypePointer Input %uint3
      %gid = OpVariable %gid_ptr Input
      $value
      %value_ptr = OpTypePointer CrossWorkgroup %value
      %fn = OpTypeFunction %void %value_ptr %value_ptr
      %subgroup = OpConstant %uint 3
      %one = OpConstant %uint 1
      %two = OpConstant %uint 2
      %subgroup_size = OpConstant %uint 32
      %active = OpConstant %uint $active
      %count = OpConstant %uint $Count
      %main = OpFunction %void None %fn
      %in = OpFunctionParameter %value_ptr
      %out = OpFunctionParameter %value_ptr
      %entry = OpLabel
      %ids = OpLoad %uint3 %gid
      %index = OpCompositeExtract %uint %ids 0
      %in_ptr = OpPtrAccessChain %value_ptr %in %index
      %first_ptr = OpPtrAccessChain %value_ptr %out %index
      %x = OpLoad %value %in_ptr
      %subgroup_id = OpUDiv %uint %index %subgroup_size
      %subgroup_parity = OpBitwiseAnd %uint %subgroup_id %one
      %subgroup_offset = OpShiftLeftLogical %uint %subgroup_parity %one
      %broadcast_lane = OpIAdd %uint %subgroup_offset %one
      %pair = OpBitwiseAnd %uint %index %two
      %shuffle_lane = OpIAdd %uint %pair %one
      $split
      %first = $collective
      OpStore %first_ptr %first
      $merge
      %second_index = OpIAdd %uint %index %count
      %second_ptr = OpPtrAccessChain %value_ptr %out %second_index
      %second = $collective
      OpStore %second_ptr %second
      OpReturn
      OpFunctionEnd
    """
  }

  private def stride(width: Int, lanes: Int): Int = (if (lanes == 3) 4 else lanes) * width / 8

  /** Returns both output halves; the output starts filled with 0x55 so inactive invocations stay visible. */
  private def dispatch(
      module: Module,
      width: Int,
      lanes: Int,
      input: (Int, Int) => Long,
      deferred: Boolean = false
  ): Vec[Byte] = {
    val storageLanes = stride(width, lanes) * 8 / width
    val source       = littleEndian((0 until Count).flatMap(t => (0 until storageLanes).map(c => input(t, c))), width)
    val memory       = RegionMemory()
    val region       = memory.allocate(source ++ Vec.fill(source.size * 2)(0x55.toByte))
    val arguments    = Vec(buffer(region), buffer(region, source.size.toLong.refine[Long >= 0].value))
    val space        = if (deferred) DeferredMemory(memory) else memory
    assertEquals(run(module, "collective", space, arguments, local = testDimensions(Count)), Right(()))
    memory.bytes(region).drop(source.size)
  }

  /** `expected(thread, masked)` gives the lanes of an invocation that ran the collective, or None when unspecified. */
  private def check(outputs: Vec[Byte], width: Int, lanes: Int, branch: String, active: Int)(
      expected: (Int, Boolean) => Option[Seq[Long]]
  ): Unit = for {
    output <- 0 until 2
    thread <- 0 until Count
  } {
    val masked = branch != "uniform" && (output == 0 || branch == "return")
    val offset = (output * Count + thread) * stride(width, lanes)
    val actual = outputs.slice(offset, offset + lanes * width / 8)
    if (masked && thread % 2 != active) assertEquals(actual, Vec.fill(lanes * width / 8)(0x55.toByte))
    else expected(thread, masked).foreach(values =>
      assertEquals(actual, littleEndian(values, width), s"output $output, invocation $thread")
    )
  }

  private def participants(thread: Int, masked: Boolean, active: Int): Seq[Int] = {
    val start = thread / 32 * 32
    (start until math.min(start + 32, Count)).filter(t => !masked || t % 2 == active)
  }

  private def scanned(operation: String, thread: Int, participants: Seq[Int]): Seq[Int] = operation match {
    case "Reduce"        => participants
    case "InclusiveScan" => participants.filter(_ <= thread)
    case _               => participants.filter(_ < thread)
  }

  /** Defers every read that is not 8-byte aligned until the next host read flush. */
  private class DeferredMemory(memory: RegionMemory) extends ForwardingMemory(memory) {
    private val pending                                = scala.collection.mutable.Set.empty[Long]
    private val ready                                  = scala.collection.mutable.Set.empty[Long]
    override def defersHostReads: Boolean              = true
    override def flushHostReads(): Either[Fault, Unit] = {
      val _ = ready.addAll(pending)
      pending.clear()
      Right(())
    }
    override def read(id: AllocationId, offset: Long >= 0, size: Int >= 0): Either[Fault, Vec[Byte]] =
      awaitRead(readDeferred(id, offset, size))
    override def readValueDeferred(id: AllocationId, offset: Long >= 0, size: Int in 1 ~ 8)
        : AddressSpace.ReadResult[Long] =
      readDeferred(id, offset, size.widen[Int >= 0])
        .map(bytes => LittleEndian.decode(bytes, 0, size.widen[Int in 0 ~ 8]))
    override def readDeferred(id: AllocationId, offset: Long >= 0, size: Int >= 0): AddressSpace.ReadResult[Vec[Byte]] =
      if (offset.raw % 8L == 0L || ready.contains(offset.raw)) memory.read(id, offset, size)
      else {
        val _ = pending.add(offset.raw)
        Left(AddressSpace.Pending)
      }
  }
}
