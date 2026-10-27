package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.spirv.asm.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class SPIRVSandboxSuite extends munit.FunSuite {

  private val branchModule =
    """OpCapability Addresses
      |OpCapability Kernel
      |OpMemoryModel Physical64 OpenCL
      |OpEntryPoint Kernel %main "branch"
      |%void = OpTypeVoid
      |%uint = OpTypeInt 32 0
      |%bool = OpTypeBool
      |%uint_ptr = OpTypePointer CrossWorkgroup %uint
      |%fn = OpTypeFunction %void %uint_ptr
      |%helper_fn = OpTypeFunction %uint %uint
      |%false = OpConstantFalse %bool
      |%true = OpConstantTrue %bool
      |%zero = OpConstant %uint 0
      |%one = OpConstant %uint 1
      |%ten = OpConstant %uint 10
      |%main = OpFunction %void None %fn
      |%out = OpFunctionParameter %uint_ptr
      |%entry = OpLabel
      |OpSelectionMerge %merge None
      |OpBranchConditional %true %then %else
      |%then = OpLabel
      |OpBranch %merge
      |%else = OpLabel
      |OpBranch %merge
      |%merge = OpLabel
      |%value = OpPhi %uint %ten %then %one %else
      |%called = OpFunctionCall %uint %helper %value
      |OpStore %out %called
      |OpReturn
      |OpFunctionEnd
      |%helper = OpFunction %uint None %helper_fn
      |%x = OpFunctionParameter %uint
      |%helper_entry = OpLabel
      |%sum = OpIAdd %uint %x %ten
      |OpReturnValue %sum
      |OpFunctionEnd
      |""".stripMargin

  private val barrierModule =
    """OpCapability Addresses
      |OpCapability Kernel
      |OpMemoryModel Physical64 OpenCL
      |OpEntryPoint Kernel %main "barrier"
      |OpExecutionMode %main LocalSize 4 1 1
      |%void = OpTypeVoid
      |%uint = OpTypeInt 32 0
      |%uint_ptr = OpTypePointer CrossWorkgroup %uint
      |%fn = OpTypeFunction %void %uint_ptr
      |%workgroup = OpConstant %uint 2
      |%semantics = OpConstant %uint 256
      |%one = OpConstant %uint 1
      |%main = OpFunction %void None %fn
      |%out = OpFunctionParameter %uint_ptr
      |%entry = OpLabel
      |OpControlBarrier %workgroup %workgroup %semantics
      |OpStore %out %one
      |OpReturn
      |OpFunctionEnd
      |""".stripMargin

  private val killModule =
    """OpCapability Shader
      |OpMemoryModel Logical GLSL450
      |OpEntryPoint Fragment %main "main"
      |OpExecutionMode %main OriginUpperLeft
      |%void = OpTypeVoid
      |%fn = OpTypeFunction %void
      |%main = OpFunction %void None %fn
      |%entry = OpLabel
      |OpKill
      |OpFunctionEnd
      |""".stripMargin

  private def fault(message: String): Fault =
    Fault.InvalidRequest(message.refine[NonEmpty[String]].fold(_ => "SPIR-V sandbox error", identity))

  private def run(source: String, entry: String, group: Dimensions = Dimensions.One) =
    SPIRVSandbox.run(source, entry, allocations = Vec(Vec.fill(16)(0.toByte)), group = group)
      .fold(fault => fail(s"$entry did not run: ${fault.description.raw}"), identity)

  test("one invocation traces labels, a phi, a call and a return") {
    val result = run(branchModule, "branch")
    assert(result.complete)
    assertEquals(result.failure, None)

    assert(result.steps.map(_.label).distinct.size >= 5, s"labels: ${result.steps.map(_.label)}")
    assert(result.steps.exists(step => step.predecessor >= 0), "no edge recorded a predecessor")
    assertEquals(result.steps.map(_.callDepth).max, 1)
    assertEquals(result.steps.map(_.callDepth).min, 0)
    assert(result.steps.exists(step => step.callDepth == 1), "the helper never ran")
    assert(result.values.exists(_._2 == InvocationView.Value.Bits(Vec(20L))), s"values: ${result.values}")

    assertEquals(LittleEndian.decode(result.bytes(0), 0, 4), 20L)
  }

  test("OpSwitch follows the matching arm") {
    val module =
      """OpCapability Addresses
        |OpCapability Kernel
        |OpMemoryModel Physical64 OpenCL
        |OpEntryPoint Kernel %main "switch"
        |%void = OpTypeVoid
        |%uint = OpTypeInt 32 0
        |%uint_ptr = OpTypePointer CrossWorkgroup %uint
        |%fn = OpTypeFunction %void %uint_ptr
        |%seven = OpConstant %uint 7
        |%main = OpFunction %void None %fn
        |%out = OpFunctionParameter %uint_ptr
        |%entry = OpLabel
        |OpSwitch %seven %default 7 %case
        |%default = OpLabel
        |OpStore %out %seven
        |OpReturn
        |%case = OpLabel
        |OpStore %out %seven
        |OpReturn
        |OpFunctionEnd
        |""".stripMargin
    val result = run(module, "switch")
    assert(result.complete)
    assertEquals(LittleEndian.decode(result.bytes(0), 0, 4), 7L)
  }

  test("four invocations park and resume at OpControlBarrier") {
    val result = run(barrierModule, "barrier", Dimensions(4, 1, 1))
    assert(result.complete)
    assertEquals(result.failure, None)
    assertEquals(result.blocked.size, 4)
    assertEquals(result.resumed.size, 4)
    assert(result.transitions.exists(event => event.kind == MachineEvent.Kind.Retired))
    assertEquals(result.steps.map(_.ordinal).distinct.size, 4)
    assertEquals(LittleEndian.decode(result.bytes(0), 0, 4), 1L)
  }

  test("a fragment invocation can execute OpKill") {
    val result = SPIRVSandbox.run(killModule, "main", environment = Validation.Environment.VulkanGraphics)
      .fold(fault => fail(s"OpKill did not run: ${fault.description.raw}"), identity)
    assert(result.complete)
    assert(result.steps.nonEmpty)
  }

  private def direct(source: String, entry: String, group: Dimensions = Dimensions.One): Either[Fault, Vec[Byte]] =
    for {
      module  <- Parser.parse(source).left.map(error => fault(error.diagnostic.raw))
      image   <- ISA.encode(module).left.map(error => fault(error.message.raw))
      program <- SPIRVBackend(Validation.Environment.OpenCLCompute).load(image)
      memory = ArrayMemory()
      region = memory.allocate(16L)
      kernel <- entry.refine[NonEmpty[String]].left.map(message => fault(message))
      task   <- KernelTask.validated(
        kernel,
        group,
        Dimensions.One,
        Vec(KernelTask.Argument.Memory(region, 0L, Some(16L)))
      ).left.map(detail => fault(detail.raw))
      _ <- program.execute(task, TaskContext(memory, TaskControl(), _ => Right(())))
    } yield memory.bytes(region)

  test("the observed sandbox matches an unobserved VM launch") {
    val branch       = run(branchModule, "branch")
    val directBranch = direct(branchModule, "branch")
      .fold(fault => fail(fault.description.raw), identity)
    assertEquals(LittleEndian.decode(branch.bytes(0), 0, 4), 20L)
    assertEquals(LittleEndian.decode(directBranch, 0, 4), 20L)

    val barrier       = run(barrierModule, "barrier", Dimensions(4, 1, 1))
    val directBarrier = direct(barrierModule, "barrier", Dimensions(4, 1, 1))
      .fold(fault => fail(fault.description.raw), identity)
    assertEquals(barrier.blocked.size, 4)
    assertEquals(LittleEndian.decode(barrier.bytes(0), 0, 4), 1L)
    assertEquals(LittleEndian.decode(directBarrier, 0, 4), 1L)
  }

}
