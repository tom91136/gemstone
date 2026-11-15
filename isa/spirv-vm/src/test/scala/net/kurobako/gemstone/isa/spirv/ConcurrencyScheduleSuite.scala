package net.kurobako.gemstone.isa.spirv

import fixtures.*
import java.util.concurrent.atomic.AtomicInteger
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*

class ConcurrencyScheduleSuite extends Suite {
  import ConcurrencyScheduleKernels.*

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
      val program = load(spinModule()).value
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

  private class FirstSetter extends LaunchObserver {
    private var setterSeen       = false
    var waiterBeforeSetter: Long = 0L

    override def instruction(at: Fault.Location, state: ArchitecturalState): Either[Fault, Unit] = {
      if (!setterSeen) {
        if (at.workgroup.exists(_.x.raw == 1)) setterSeen = true
        else waiterBeforeSetter += 1
      }
      Right(())
    }
  }

  test("a seeded sweep bounds a workgroup to the same slice whatever its lane count") {
    val slice = 64 * 1024
    for {
      localSize <- Vector(64, 1024)
      seed      <- 0L to 7L
    } {
      val memory   = RegionMemory()
      val region   = memory.allocate(8L)
      val program  = load(spinModule(localSize)).value
      val observer = FirstSetter()
      val result   = execute(
        program,
        task(
          "spin",
          Vec(buffer(region, 0L, Some(8L))),
          local = testDimensions(localSize.refine[Int > 0].value),
          groups = testDimensions(2)
        ),
        memory,
        control = TaskControl(TaskControl.Limits(2000000L), Some(observer)),
        schedule = ConcurrencySchedule.Random(seed)
      )
      assertEquals(result, Right(()))
      assert(
        observer.waiterBeforeSetter <= slice,
        s"local size $localSize, seed $seed: waiter ran ${observer.waiterBeforeSetter} instructions " +
          s"before the setter, over the $slice instruction workgroup slice"
      )
      assertEquals(word(memory, region, 4), localSize.toLong)
    }
  }

  private class CountingRandom(seed: Long) extends ConcurrencySchedule.Random(seed) {
    val launchSchedules: AtomicInteger          = AtomicInteger()
    override def forLaunch: ConcurrencySchedule = {
      launchSchedules.incrementAndGet()
      super.forLaunch
    }
  }

  test("the launch order schedule is made once per launch, not once per sweep") {
    val memory   = RegionMemory()
    val region   = memory.allocate(8L)
    val program  = load(spinModule()).value
    val schedule = CountingRandom(7L)
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
        schedule = schedule
      ),
      Right(())
    )
    assertEquals(word(memory, region, 4), 1L)
    assertEquals(schedule.launchSchedules.get(), 1)
  }
}
