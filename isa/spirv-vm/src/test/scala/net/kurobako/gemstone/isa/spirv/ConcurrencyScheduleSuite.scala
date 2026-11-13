package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*

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
}
