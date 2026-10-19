package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.*

/** SPV_KHR_shader_clock: glslang emits OpReadClockKHR for clockARB and clockRealtimeEXT, and icpx for SYCL's clock. */
class ClockSuite extends munit.FunSuite {
  private val module = spv"""
    OpCapability ShaderClockKHR
    $openCL
    OpEntryPoint Kernel %main "clock"
    %void = OpTypeVoid
    %uint = OpTypeInt 32 0
    %ulong = OpTypeInt 64 0
    %uint2 = OpTypeVector %uint 2
    %out_ptr = OpTypePointer CrossWorkgroup %ulong
    %fn = OpTypeFunction %void %out_ptr
    %device = OpConstant %uint 1
    %subgroup = OpConstant %uint 3
    %one = OpConstant %ulong 1
    %two = OpConstant %ulong 2
    %three = OpConstant %ulong 3
    %four = OpConstant %ulong 4
    %main = OpFunction %void None %fn
    %out = OpFunctionParameter %out_ptr
    %entry = OpLabel
    %issued_before = OpReadClockKHR %ulong %subgroup
    %time_before = OpReadClockKHR %ulong %device
    %pair = OpReadClockKHR %uint2 %subgroup
    %issued_after = OpReadClockKHR %ulong %subgroup
    %time_after = OpReadClockKHR %ulong %device
    %pair_low = OpCompositeExtract %uint %pair 0
    %pair_wide = OpUConvert %ulong %pair_low
    OpStore %out %issued_before
    %slot1 = OpPtrAccessChain %out_ptr %out %one
    OpStore %slot1 %issued_after
    %slot2 = OpPtrAccessChain %out_ptr %out %two
    OpStore %slot2 %time_before
    %slot3 = OpPtrAccessChain %out_ptr %out %three
    OpStore %slot3 %time_after
    %slot4 = OpPtrAccessChain %out_ptr %out %four
    OpStore %slot4 %pair_wide
    OpReturn
    OpFunctionEnd
  """

  // Subgroup and workgroup clocks count the instructions the invocation issued; device clocks are nanoseconds since
  // the Unix epoch on a clock that never runs backwards
  test("OpReadClockKHR counts issued instructions within a subgroup and reads wall time at device scope") {
    val memory  = RegionMemory()
    val out     = memory.allocate(40L)
    val started = platform.epochMillis * 1000000L
    assertEquals(run(module, "clock", memory, Vec(buffer(out))), Right(()))
    val ended  = platform.epochMillis * 1000000L + 1000000L
    val values = Vec.tabulate(5)(slot => LittleEndian.decode(memory.bytes(out), slot * 8, 8))
    assert(values(1) > values(0) && values(4) > values(0) && values(1) > values(4), values)
    assert(values(2) >= started - 1000000L && values(2) <= values(3) && values(3) <= ended, values)
  }
}
