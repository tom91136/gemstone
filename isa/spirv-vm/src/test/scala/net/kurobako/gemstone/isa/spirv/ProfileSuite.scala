package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.profile.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import scala.collection.mutable

class ProfileSuite extends Suite {
  private type Access = (lane: Int, space: Space, kind: LaunchObserver.AccessKind, address: Long, bytes: Int)
  private type Async  = (lane: Int, kind: LaunchObserver.AsyncKind, target: Long, bytes: Long)

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private class Facts extends ProfileStream.Visitor {
    var instructions: Vec[ProfileStream.Instruction]                            = Vec.empty
    val issues: mutable.ArrayBuffer[(text: String, issue: ProfileStream.Issue)] = mutable.ArrayBuffer.empty
    val accesses: mutable.ArrayBuffer[(text: String, access: Access)]           = mutable.ArrayBuffer.empty
    val asyncs: mutable.ArrayBuffer[(text: String, step: Async)]                = mutable.ArrayBuffer.empty
    private var current                                                         = ""

    override def issue(issue: ProfileStream.Issue): Unit = {
      current = instructions(issue.instruction.raw).text
      issues += ((current, issue))
    }

    override def access(
        lane: Int >= 0,
        space: Space,
        kind: LaunchObserver.AccessKind,
        address: Long,
        bytes: Int >= 0
    ): Unit =
      accesses += ((current, (lane.raw, space, kind, address, bytes.raw)))

    override def asynchronous(lane: Int >= 0, kind: LaunchObserver.AsyncKind, target: Long, bytes: Long): Unit =
      asyncs += ((current, (lane.raw, kind, target, bytes)))

    def accessed(opcode: String): Vector[Access] = accesses.toVector.collect {
      case (text, access) if text.contains(opcode) => access
    }

    def stepped(opcode: String): Vector[Async] = asyncs.toVector.collect {
      case (text, step) if text.contains(opcode) => step
    }
  }

  private def record(
      module: Module,
      entry: NonEmpty[String],
      memory: RegionMemory,
      arguments: Vec[KernelTask.Argument],
      local: Dimensions
  )(
      using munit.Location
  ): Facts = {
    val recorder = ProfileRecorder()
    val result   = load(module).flatMap(program =>
      execute(
        program,
        task(entry, arguments, local = local),
        memory,
        TaskControl(observer = Some(recorder))
      )
    )
    assertEquals(result, Right(()))
    val stream = recorder.stream
    val facts  = Facts()
    facts.instructions = stream.instructions
    stream.foreach(facts)
    facts
  }

  private def base(memory: RegionMemory, region: AllocationId): Long = memory.address(region, 0L).value.raw

  private def kernel(entry: String, types: String, body: String, locals: String = ""): Module = spv"""
    $openCL
    OpEntryPoint Kernel %main "$entry" %lid
    OpDecorate %lid BuiltIn LocalInvocationIndex
    %void = OpTypeVoid
    %bool = OpTypeBool
    %uint = OpTypeInt 32 0
    %uint_ptr = OpTypePointer CrossWorkgroup %uint
    %shared_ptr = OpTypePointer Workgroup %uint
    %lid_ptr = OpTypePointer Input %uint
    %lid = OpVariable %lid_ptr Input
    %fn = OpTypeFunction %void %uint_ptr %uint_ptr
    %zero = OpConstant %uint 0
    %one = OpConstant %uint 1
    %four = OpConstant %uint 4
    %thirty_one = OpConstant %uint 31
    %workgroup = OpConstant %uint ${Scope.Workgroup}
    %semantics = OpConstant %uint ${MemorySemantics.WorkgroupMemory | MemorySemantics.AcquireRelease}
    $types
    %main = OpFunction %void None %fn
    %in = OpFunctionParameter %uint_ptr
    %out = OpFunctionParameter %uint_ptr
    %entry = OpLabel
    $locals
    %lane = OpLoad %uint %lid
    $body
    OpReturn
    OpFunctionEnd
  """

  private inline val Lanes = 32
  private val Group        = Dimensions(Lanes, 1, 1)

  private def copy(stride: Int): Module = kernel(
    "copy",
    s"%stride = OpConstant %uint $stride",
    """%index = OpIMul %uint %lane %stride
      |%source = OpPtrAccessChain %uint_ptr %in %index
      |%value = OpLoad %uint %source
      |%target = OpPtrAccessChain %uint_ptr %out %lane
      |OpStore %target %value""".stripMargin
  )

  private def copyFacts(stride: Int)(using munit.Location): (Facts, Long, Long) = {
    val memory = RegionMemory()
    val in     = memory.allocate((4L * Lanes * stride).refine[Long > 0].value)
    val out    = memory.allocate((4L * Lanes).refine[Long > 0].value)
    val facts  = record(copy(stride), "copy", memory, Vec(buffer(in), buffer(out)), Group)
    (facts, base(memory, in), base(memory, out))
  }

  test("a coalesced global load reports one consecutive address per lane") {
    val (facts, in, out) = copyFacts(1)
    assertEquals(
      facts.accessed("OpLoad").map(a => (a.lane, a.space, a.kind, a.address - in, a.bytes)),
      Vector.tabulate(Lanes)(lane => (lane, Space.Global, LaunchObserver.AccessKind.Read, 4L * lane, 4))
    )
    assertEquals(
      facts.accessed("OpStore").map(a => (a.lane, a.space, a.kind, a.address - out, a.bytes)),
      Vector.tabulate(Lanes)(lane => (lane, Space.Global, LaunchObserver.AccessKind.Write, 4L * lane, 4))
    )
    assertEquals(facts.accessed("OpLoad").filter(_.space != Space.Global), Vector.empty)
  }

  test("a strided global load reports the stride in each lane's address") {
    val (facts, in, _) = copyFacts(4)
    assertEquals(
      facts.accessed("OpLoad").map(a => (a.lane, a.address - in)),
      Vector.tabulate(Lanes)(lane => (lane, 16L * lane))
    )
  }

  test("each issue names its invocation, the subgroup size and the instruction's text") {
    val (facts, _, _) = copyFacts(1)
    val stores        = facts.issues.filter(_.text.contains("OpStore"))
    assertEquals(stores.map(_.issue.lanes.map(_.raw)).toVector, Vector.tabulate(Lanes)(lane => Vec(lane)))
    assertEquals(stores.map(_.issue.unit.raw).toVector, Vector.tabulate(Lanes)(identity))
    assert(facts.issues.forall(_.issue.subgroupSize == 32))
    assert(stores.forall(_.text.startsWith("OpStore")), stores.headOption.map(_.text).toString)
  }

  test("workgroup stores and loads report offsets within the workgroup's shared window") {
    val module = kernel(
      "shared",
      """%thirty_two = OpConstant %uint 32
        |%tile_t = OpTypeArray %uint %thirty_two
        |%tile_ptr = OpTypePointer Workgroup %tile_t
        |%tile = OpVariable %tile_ptr Workgroup""".stripMargin,
      """%slot = OpAccessChain %shared_ptr %tile %lane
        |OpStore %slot %lane
        |OpControlBarrier %workgroup %workgroup %semantics
        |%reversed = OpISub %uint %thirty_one %lane
        |%other = OpAccessChain %shared_ptr %tile %reversed
        |%value = OpLoad %uint %other
        |%target = OpPtrAccessChain %uint_ptr %out %lane
        |OpStore %target %value""".stripMargin
    )
    val memory = RegionMemory()
    val in     = memory.allocate(4L)
    val out    = memory.allocate(128L)
    val facts  = record(module, "shared", memory, Vec(buffer(in), buffer(out)), Group)
    assertEquals(
      facts.accessed("OpStore").collect {
        case a if a.space == Space.Shared => (a.lane, a.space, a.kind, a.address, a.bytes)
      },
      Vector.tabulate(Lanes)(lane => (lane, Space.Shared, LaunchObserver.AccessKind.Write, 4L * lane, 4))
    )
    assertEquals(
      facts.accessed("OpLoad").map(a => (a.lane, a.space, a.kind, a.address, a.bytes)),
      Vector.tabulate(Lanes)(lane => (lane, Space.Shared, LaunchObserver.AccessKind.Read, 4L * (Lanes - 1 - lane), 4))
    )
  }

  test("a function variable reports local accesses") {
    val module = kernel(
      "local",
      "%function_ptr = OpTypePointer Function %uint",
      """OpStore %scratch %lane
        |%value = OpLoad %uint %scratch
        |%target = OpPtrAccessChain %uint_ptr %out %lane
        |OpStore %target %value""".stripMargin,
      "%scratch = OpVariable %function_ptr Function"
    )
    val memory = RegionMemory()
    val in     = memory.allocate(4L)
    val out    = memory.allocate(128L)
    val facts  = record(module, "local", memory, Vec(buffer(in), buffer(out)), Group)
    val writes = facts.accessed("OpStore").filter(_.space == Space.Local)
    val reads  = facts.accessed("OpLoad")
    assertEquals(
      writes.map(a => (a.lane, a.space, a.kind, a.bytes)),
      Vector.tabulate(Lanes)((_, Space.Local, LaunchObserver.AccessKind.Write, 4))
    )
    assertEquals(
      reads.map(a => (a.lane, a.space, a.kind, a.bytes)),
      Vector.tabulate(Lanes)((_, Space.Local, LaunchObserver.AccessKind.Read, 4))
    )
    assertEquals(reads.map(_.address), writes.map(_.address))
  }

  test("an atomic reports an atomic access at the same address for every lane") {
    val module = kernel("atomic", "", "%old = OpAtomicIAdd %uint %out %workgroup %semantics %one")
    val memory = RegionMemory()
    val in     = memory.allocate(4L)
    val out    = memory.allocate(4L)
    val facts  = record(module, "atomic", memory, Vec(buffer(in), buffer(out)), Group)
    assertEquals(
      facts.accessed("OpAtomicIAdd"),
      Vector.tabulate(Lanes)(lane => (lane, Space.Global, LaunchObserver.AccessKind.Atomic, base(memory, out), 4))
    )
    assertEquals(word(memory, out, 0), Lanes.toLong)
  }

  test("a divergent branch issues its body only for the lanes that take it") {
    val module = kernel(
      "divergent",
      "",
      """%odd = OpBitwiseAnd %uint %lane %one
        |%taken = OpINotEqual %bool %odd %zero
        |OpSelectionMerge %merge None
        |OpBranchConditional %taken %then %merge
        |%then = OpLabel
        |%target = OpPtrAccessChain %uint_ptr %out %lane
        |OpStore %target %lane
        |OpBranch %merge
        |%merge = OpLabel""".stripMargin
    )
    val memory = RegionMemory()
    val in     = memory.allocate(4L)
    val out    = memory.allocate(128L)
    val facts  = record(module, "divergent", memory, Vec(buffer(in), buffer(out)), Group)
    val odd    = Vector.tabulate(Lanes / 2)(index => 2 * index + 1)
    assertEquals(
      facts.issues.toVector.collect { case (text, issue) if text.contains("OpStore") => issue.lanes.map(_.raw) },
      odd.map(Vec(_))
    )
    assertEquals(
      facts.accessed("OpStore").map(a => (a.lane, a.address - base(memory, out))),
      odd.map(lane => (lane, 4L * lane))
    )
    assertEquals(facts.issues.toVector.count(_.text.contains("OpBranchConditional")), Lanes)
  }

  test("a group async copy reports a transfer and commit from the copying lane and a wait from every lane") {
    val count  = 4
    val module = spv"""
      $openCL
      OpEntryPoint Kernel %main "async"
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %uint_count = OpConstant %uint $count
      %event_t = OpTypeEvent
      %event_fn_ptr = OpTypePointer Function %event_t
      %uint_ptr = OpTypePointer CrossWorkgroup %uint
      %shared_ptr = OpTypePointer Workgroup %uint
      %tile_t = OpTypeArray %uint %uint_count
      %tile_ptr = OpTypePointer Workgroup %tile_t
      %fn = OpTypeFunction %void %uint_ptr
      %workgroup = OpConstant %uint ${Scope.Workgroup}
      %uint_0 = OpConstant %uint 0
      %uint_1 = OpConstant %uint 1
      %no_event = OpConstantNull %event_t
      %tile = OpVariable %tile_ptr Workgroup
      %main = OpFunction %void None %fn
      %source = OpFunctionParameter %uint_ptr
      %entry = OpLabel
      %events = OpVariable %event_fn_ptr Function
      %tile_base = OpInBoundsAccessChain %shared_ptr %tile %uint_0
      %loaded = OpGroupAsyncCopy %event_t %workgroup %tile_base %source %uint_count %uint_1 %no_event
      OpStore %events %loaded
      OpGroupWaitEvents %workgroup %uint_1 %events
      OpReturn
      OpFunctionEnd
    """
    val memory = RegionMemory()
    val source = memory.allocate(16L)
    val facts  = record(module, "async", memory, Vec(buffer(source)), Dimensions(4, 1, 1))
    assertEquals(
      facts.accessed("OpGroupAsyncCopy"),
      Vector[Access](
        (0, Space.Global, LaunchObserver.AccessKind.Read, base(memory, source), 4 * count),
        (0, Space.Shared, LaunchObserver.AccessKind.Write, 0L, 4 * count)
      )
    )
    assertEquals(
      facts.stepped("OpGroupAsyncCopy"),
      Vector[Async](
        (0, LaunchObserver.AsyncKind.Transfer, LaunchObserver.DefaultTarget, 4L * count),
        (0, LaunchObserver.AsyncKind.Commit, LaunchObserver.DefaultTarget, 0L)
      )
    )
    assertEquals(
      facts.stepped("OpGroupWaitEvents"),
      Vector.tabulate[Async](count)(lane =>
        (lane, LaunchObserver.AsyncKind.WaitGroups, LaunchObserver.DefaultTarget, 0L)
      )
    )
  }

  test("a launch reports the Workgroup variables it declares as shared bytes") {
    val module = kernel(
      "tile",
      """%thirty_two = OpConstant %uint 32
        |%tile_t = OpTypeArray %uint %thirty_two
        |%tile_ptr = OpTypePointer Workgroup %tile_t
        |%tile = OpVariable %tile_ptr Workgroup""".stripMargin,
      """%slot = OpAccessChain %shared_ptr %tile %lane
        |OpStore %slot %lane""".stripMargin
    )
    val memory   = RegionMemory()
    val in       = memory.allocate(4L)
    val out      = memory.allocate(128L)
    val recorder = ProfileRecorder()
    val result   = load(module).flatMap(program =>
      execute(
        program,
        task("tile", Vec(buffer(in), buffer(out)), local = Group),
        memory,
        TaskControl(observer = Some(recorder))
      )
    )
    assertEquals(result, Right(()))
    assertEquals(recorder.stream.resources, Some(KernelResources(Lanes, Lanes, None, 128L)))
    assert(Profiler.run(recorder.stream, Architecture.sm89).occupancy.isDefined)
  }

  private def pipeFacts(types: String, body: String)(using munit.Location): Facts = {
    val memory = RegionMemory()
    val in     = memory.allocate(4L)
    val out    = memory.allocate(4L)
    record(kernel("pipes", types, body), "pipes", memory, Vec(buffer(in), buffer(out)), Group)
  }

  private def pipeOf(facts: Facts, opcode: String)(using munit.Location): Option[Pipe] = {
    val found = facts.instructions.toVector.filter(_.text.contains(opcode))
    assertEquals(found.size, 1)
    found.head.pipe
  }

  test("float multiply and add report the fma pipe at 32 bits") {
    val facts = pipeFacts(
      "%float = OpTypeFloat 32",
      """%f = OpConvertUToF %float %lane
        |%product = OpFMul %float %f %f
        |%sum = OpFAdd %float %product %f""".stripMargin
    )
    assertEquals(pipeOf(facts, "OpFMul"), Some(Pipe.Fma))
    assertEquals(pipeOf(facts, "OpFAdd"), Some(Pipe.Fma))
  }

  test("float add at 64 bits reports the fp64 pipe") {
    val facts = pipeFacts(
      "%double = OpTypeFloat 64",
      """%f = OpConvertUToF %double %lane
        |%sum = OpFAdd %double %f %f""".stripMargin
    )
    assertEquals(pipeOf(facts, "OpFAdd"), Some(Pipe.Fp64))
  }

  test("float conversion to 64 bits reports the fp64 pipe") {
    val facts = pipeFacts(
      """%float = OpTypeFloat 32
        |%double = OpTypeFloat 64""".stripMargin,
      """%f = OpConvertUToF %float %lane
        |%d = OpFConvert %double %f""".stripMargin
    )
    assertEquals(pipeOf(facts, "OpFConvert"), Some(Pipe.Fp64))
  }

  test("float to integer conversion from 64 bits reports the fp64 pipe") {
    val facts = pipeFacts(
      """%float = OpTypeFloat 32
        |%double = OpTypeFloat 64""".stripMargin,
      """%f = OpConvertUToF %double %lane
        |%s = OpConvertFToS %uint %f
        |%g = OpConvertUToF %float %lane
        |%u = OpConvertFToU %uint %g""".stripMargin
    )
    assertEquals(pipeOf(facts, "OpConvertFToS"), Some(Pipe.Fp64))
    assertEquals(pipeOf(facts, "OpConvertFToU"), Some(Pipe.Fma))
  }

  test("integer multiply reports the fma pipe and integer add the alu pipe") {
    val facts = pipeFacts(
      "",
      """%product = OpIMul %uint %lane %four
        |%sum = OpIAdd %uint %product %one""".stripMargin
    )
    assertEquals(pipeOf(facts, "OpIMul"), Some(Pipe.Fma))
    assertEquals(pipeOf(facts, "OpIAdd"), Some(Pipe.Alu))
  }

  test("an opencl exp2 reports the mufu pipe at 32 bits") {
    val facts = pipeFacts(
      "%float = OpTypeFloat 32",
      """%f = OpConvertUToF %float %lane
        |%e = OpExtInst %float %opencl exp2 %f""".stripMargin
    )
    assertEquals(pipeOf(facts, "OpExtInst"), Some(Pipe.Mufu))
  }

  test("loads and returns report no pipe") {
    val facts = pipeFacts("", "")
    assertEquals(pipeOf(facts, "OpLoad"), None)
    assertEquals(pipeOf(facts, "OpReturn"), None)
  }

  private val cooperativeMatrix = spv"""
    OpCapability CooperativeMatrixKHR
    OpExtension "SPV_KHR_cooperative_matrix"
    $openCL
    OpEntryPoint Kernel %main "main"
    %void = OpTypeVoid
    %uint = OpTypeInt 32 0
    %scope = OpConstant %uint 3
    %two = OpConstant %uint 2
    %one = OpConstant %uint 1
    %use_a = OpConstant %uint 0
    %use_b = OpConstant %uint 1
    %use_c = OpConstant %uint 2
    %row_major = OpConstant %uint 0
    %stride = OpConstant %uint 2
    %a_type = OpTypeCooperativeMatrixKHR %uint %scope %two %two %use_a
    %b_type = OpTypeCooperativeMatrixKHR %uint %scope %two %two %use_b
    %c_type = OpTypeCooperativeMatrixKHR %uint %scope %two %two %use_c
    %uint_ptr = OpTypePointer CrossWorkgroup %uint
    %fn = OpTypeFunction %void %uint_ptr %uint_ptr %uint_ptr
    %main = OpFunction %void None %fn
    %a_in = OpFunctionParameter %uint_ptr
    %b_in = OpFunctionParameter %uint_ptr
    %out = OpFunctionParameter %uint_ptr
    %entry = OpLabel
    %a = OpCooperativeMatrixLoadKHR %a_type %a_in %row_major %stride
    %b = OpCooperativeMatrixLoadKHR %b_type %b_in %row_major %stride
    %c = OpCompositeConstruct %c_type %one
    %product = OpCooperativeMatrixMulAddKHR %c_type %a %b %c
    OpCooperativeMatrixStoreKHR %out %product %row_major %stride
    OpReturn
    OpFunctionEnd
  """

  test("a cooperative matrix multiply-accumulate reports the tensor pipe") {
    val memory = RegionMemory()
    val a      = memory.allocate(16L)
    val b      = memory.allocate(16L)
    val out    = memory.allocate(16L)
    val facts  = record(cooperativeMatrix, "main", memory, Vec(buffer(a), buffer(b), buffer(out)), Dimensions.One)
    assertEquals(pipeOf(facts, "OpCooperativeMatrixMulAddKHR"), Some(Pipe.Tensor))
  }

  private val cooperativeMatrixNV = spv"""
    OpCapability CooperativeMatrixNV
    OpExtension "SPV_NV_cooperative_matrix"
    $openCL
    OpEntryPoint Kernel %main "main"
    %void = OpTypeVoid
    %bool = OpTypeBool
    %false = OpConstantFalse %bool
    %uint = OpTypeInt 32 0
    %scope = OpConstant %uint 3
    %two = OpConstant %uint 2
    %one = OpConstant %uint 1
    %matrix = OpTypeCooperativeMatrixNV %uint %scope %two %two
    %uint_ptr = OpTypePointer CrossWorkgroup %uint
    %fn = OpTypeFunction %void %uint_ptr %uint_ptr %uint_ptr
    %main = OpFunction %void None %fn
    %a_in = OpFunctionParameter %uint_ptr
    %b_in = OpFunctionParameter %uint_ptr
    %out = OpFunctionParameter %uint_ptr
    %entry = OpLabel
    %a = OpCooperativeMatrixLoadNV %matrix %a_in %two %false None
    %b = OpCooperativeMatrixLoadNV %matrix %b_in %two %false None
    %c = OpCompositeConstruct %matrix %one
    %product = OpCooperativeMatrixMulAddNV %matrix %a %b %c
    OpCooperativeMatrixStoreNV %out %product %two %false None
    OpReturn
    OpFunctionEnd
  """

  test("a cooperative matrix nv multiply-accumulate reports the tensor pipe") {
    val memory = RegionMemory()
    val a      = memory.allocate(16L)
    val b      = memory.allocate(16L)
    val out    = memory.allocate(16L)
    val facts  = record(cooperativeMatrixNV, "main", memory, Vec(buffer(a), buffer(b), buffer(out)), Dimensions.One)
    assertEquals(pipeOf(facts, "OpCooperativeMatrixMulAddNV"), Some(Pipe.Tensor))
  }
}
