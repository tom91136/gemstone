package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import scala.collection.mutable
import scala.util.Using

class ConcurrencyScheduleSuite extends Suite {

  private val orderModule = spv"""
    $openCL
    OpEntryPoint Kernel %main "order" %builtin
    OpExecutionMode %main LocalSize 4 1 1
    OpDecorate %builtin BuiltIn GlobalInvocationId
    %void = OpTypeVoid
    %uint = OpTypeInt 32 0
    %ulong = OpTypeInt 64 0
    %id_vector = OpTypeVector %ulong 3
    %id_vector_ptr = OpTypePointer Input %id_vector
    %uint_ptr = OpTypePointer CrossWorkgroup %uint
    %fn = OpTypeFunction %void %uint_ptr
    %workgroup = OpConstant %uint 2
    %semantics = OpConstant %uint 256
    %builtin = OpVariable %id_vector_ptr Input
    %main = OpFunction %void None %fn
    %out = OpFunctionParameter %uint_ptr
    %entry = OpLabel
    %ids = OpLoad %id_vector %builtin
    %lane = OpCompositeExtract %ulong %ids 0
    %value = OpUConvert %uint %lane
    OpControlBarrier %workgroup %workgroup %semantics
    OpStore %out %value
    OpReturn
    OpFunctionEnd
  """

  private def runWithSchedule(schedule: ConcurrencySchedule): Int = {
    val memory  = RegionMemory()
    val region  = memory.allocate(4L)
    val program = load(orderModule).value
    assertEquals(
      execute(
        program,
        task(
          "order",
          Vec(KernelTask.Argument.Memory(region, 0L, Some(4L))),
          local = testDimensions(4)
        ),
        memory,
        schedule = schedule
      ),
      Right(())
    )
    LittleEndian.decodeInt32(memory.read(region, 0L, 4).value, 0)
  }

  test("invocation order in workgroup changes with seed and reproduces under same seed and fixed default") {
    val fixed    = runWithSchedule(ConcurrencySchedule.Fixed)
    val resSeed2 = runWithSchedule(ConcurrencySchedule.Random(2L))
    val repeated = ConcurrencySchedule.Random(3L)
    val resSeed3 = runWithSchedule(repeated)
    val repeat   = runWithSchedule(repeated)

    assertEquals(resSeed3, repeat)
    assertNotEquals(resSeed2, resSeed3)
    assertEquals(fixed, 3)
  }

  private val workgroupOrderModule = spv"""
    $openCL
    OpEntryPoint Kernel %main "order" %workgroup
    OpExecutionMode %main LocalSize 1 1 1
    OpDecorate %workgroup BuiltIn WorkgroupId
    %void = OpTypeVoid
    %bool = OpTypeBool
    %uint = OpTypeInt 32 0
    %ulong = OpTypeInt 64 0
    %id_vector = OpTypeVector %uint 3
    %id_vector_ptr = OpTypePointer Input %id_vector
    %uint_ptr = OpTypePointer CrossWorkgroup %uint
    %fn = OpTypeFunction %void %uint_ptr
    %three = OpConstant %uint 3
    %workgroup = OpVariable %id_vector_ptr Input
    %main = OpFunction %void None %fn
    %out = OpFunctionParameter %uint_ptr
    %entry = OpLabel
    %ids = OpLoad %id_vector %workgroup
    %id = OpCompositeExtract %uint %ids 0
    %value = OpLoad %uint %out
    %scaled = OpIMul %uint %value %three
    %next = OpIAdd %uint %scaled %id
    OpStore %out %next
    OpReturn
    OpFunctionEnd
  """

  private val spinModule = spv"""
    $openCL
    OpEntryPoint Kernel %main "spin" %workgroup
    OpExecutionMode %main LocalSize 1 1 1
    OpDecorate %workgroup BuiltIn WorkgroupId
    %void = OpTypeVoid
    %bool = OpTypeBool
    %uint = OpTypeInt 32 0
    %id_vector = OpTypeVector %uint 3
    %id_vector_ptr = OpTypePointer Input %id_vector
    %uint_ptr = OpTypePointer CrossWorkgroup %uint
    %fn = OpTypeFunction %void %uint_ptr
    %zero = OpConstant %uint 0
    %one = OpConstant %uint 1
    %workgroup = OpVariable %id_vector_ptr Input
    %main = OpFunction %void None %fn
    %out = OpFunctionParameter %uint_ptr
    %entry = OpLabel
    %ids = OpLoad %id_vector %workgroup
    %id = OpCompositeExtract %uint %ids 0
    %is_setter = OpIEqual %bool %id %one
    OpSelectionMerge %merge None
    OpBranchConditional %is_setter %set %wait
    %set = OpLabel
    OpStore %out %one
    OpBranch %merge
    %wait = OpLabel
    OpBranch %loop
    %loop = OpLabel
    OpLoopMerge %merge %continue None
    OpBranch %body
    %body = OpLabel
    %flag = OpLoad %uint %out
    %ready = OpIEqual %bool %flag %one
    OpBranchConditional %ready %merge %continue
    %continue = OpLabel
    OpBranch %loop
    %merge = OpLabel
    %is_waiter = OpIEqual %bool %id %zero
    OpSelectionMerge %done None
    OpBranchConditional %is_waiter %increment %done
    %increment = OpLabel
    %slot = OpPtrAccessChain %uint_ptr %out %one
    %count = OpLoad %uint %slot
    %next = OpIAdd %uint %count %one
    OpStore %slot %next
    OpBranch %done
    %done = OpLabel
    OpReturn
    OpFunctionEnd
  """

  private def workgroupOrder(schedule: ConcurrencySchedule): Int = {
    val memory  = RegionMemory()
    val region  = memory.allocate(4L)
    val program = load(workgroupOrderModule).value
    assertEquals(
      execute(
        program,
        task(
          "order",
          Vec(buffer(region, 0L, Some(4L))),
          local = testDimensions(1),
          groups = testDimensions(2)
        ),
        memory,
        schedule = schedule
      ),
      Right(())
    )
    word(memory, region, 0).toInt
  }

  test("workgroup order changes with seed and reproduces under the same seed") {
    val fixed    = workgroupOrder(ConcurrencySchedule.Fixed)
    val seed0    = workgroupOrder(ConcurrencySchedule.Random(0L))
    val repeated = ConcurrencySchedule.Random(1L)
    val seed1    = workgroupOrder(repeated)
    val again    = workgroupOrder(repeated)

    assertEquals(seed1, again)
    assertNotEquals(seed0, seed1)
    assert(fixed == seed0 || fixed == seed1)
  }

  test("a workgroup spinning on a flag another sets completes under several seeds") {
    for (seed <- 0L to 3L) {
      val memory  = RegionMemory()
      val region  = memory.allocate(8L)
      val program = load(spinModule).value
      assertEquals(
        execute(
          program,
          task(
            "spin",
            Vec(buffer(region, 0L, Some(8L))),
            local = testDimensions(1),
            groups = testDimensions(2)
          ),
          memory,
          schedule = ConcurrencySchedule.Random(seed)
        ),
        Right(())
      )
      assertEquals(word(memory, region, 4), 1L)
    }
  }

  test("ReplayDevice reproduces a seeded launch") {
    val image = ISA.encode(spinModule).value
    Using.resource(WorkerPool(1, minGroups = 1L, minWorkItems = 1L)) { pool =>
      val memory = ArrayMemory()
      val region = memory.allocate(8L)
      val replay = ReplayDevice(
        BackendRegistry(Vec(SPIRVBackend(Validation.Environment.OpenCLCompute, workgroups = pool))),
        memory,
        TaskControl.Limits(2000000L),
        ConcurrencySchedule.Random(7L)
      )
      val task = KernelTask.validated(
        "spin",
        testDimensions(1),
        testDimensions(2),
        Vec(buffer(region, 0L, Some(8L)))
      ).value
      val checkpoints = mutable.ArrayBuffer.empty[ReplayDevice.Checkpoint]
      val whole       = replay.launch(
        ProgramFormat.SPIRVOpenCLCompute,
        image,
        task,
        (checkpoint, _, _) => Right(checkpoints += checkpoint).map(_ => ())
      )
      assert(whole.isRight, whole)
      val finished = LittleEndian.decodeInt32(memory.bytes(region), 4)
      assertEquals(finished, 1)

      val resumed = replay.resume(checkpoints(10), (_, _, _) => Right(()))
      assert(resumed.isRight, resumed)
      assertEquals(LittleEndian.decodeInt32(memory.bytes(region), 4), finished)
    }
  }
}
