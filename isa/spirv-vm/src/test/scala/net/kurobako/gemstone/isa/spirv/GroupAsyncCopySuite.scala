package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*

class GroupAsyncCopySuite extends munit.FunSuite {
  private final val Count = 4
  private val sentinel    = 0xffffffffL

  /** Copies `Count` elements spaced `stride` apart into workgroup memory, then back out at the same spacing. */
  private def kernel(stride: Int): Module = spv"""
    $openCL
    OpEntryPoint Kernel %main "roundtrip"
    %void = OpTypeVoid
    %uint = OpTypeInt 32 0
    %uint_count = OpConstant %uint $Count
    %event_t = OpTypeEvent
    %event_fn_ptr = OpTypePointer Function %event_t
    %uint_ptr = OpTypePointer CrossWorkgroup %uint
    %shared_ptr = OpTypePointer Workgroup %uint
    %tile_t = OpTypeArray %uint %uint_count
    %tile_ptr = OpTypePointer Workgroup %tile_t
    %fn = OpTypeFunction %void %uint_ptr %uint_ptr
    %workgroup = OpConstant %uint ${Scope.Workgroup}
    %uint_0 = OpConstant %uint 0
    %uint_stride = OpConstant %uint $stride
    %no_event = OpConstantNull %event_t
    %semantics = OpConstant %uint ${MemorySemantics.WorkgroupMemory | MemorySemantics.CrossWorkgroupMemory |
      MemorySemantics.AcquireRelease}
    %tile = OpVariable %tile_ptr Workgroup
    %main = OpFunction %void None %fn
    %source = OpFunctionParameter %uint_ptr
    %target = OpFunctionParameter %uint_ptr
    %entry = OpLabel
    %events = OpVariable %event_fn_ptr Function
    %tile_base = OpInBoundsAccessChain %shared_ptr %tile %uint_0
    %loaded = OpGroupAsyncCopy %event_t %workgroup %tile_base %source %uint_count %uint_stride %no_event
    OpStore %events %loaded
    OpGroupWaitEvents %workgroup %uint_count %events
    OpControlBarrier %workgroup %workgroup %semantics
    %stored = OpGroupAsyncCopy %event_t %workgroup %target %tile_base %uint_count %uint_stride %loaded
    OpStore %events %stored
    OpGroupWaitEvents %workgroup %uint_count %events
    OpReturn
    OpFunctionEnd
  """

  for (stride <- Seq(1, 3))
    test(s"OpGroupAsyncCopy round-trips $Count elements spaced $stride apart through workgroup memory") {
      val total  = Count * stride
      val memory = RegionMemory()
      val source = memory.allocate((4L * total).refine[Long > 0].value)
      val target = memory.allocate((4L * total).refine[Long > 0].value)
      writeWords(memory, source, 0, Seq.tabulate(total)(index => 0x100L + index)*)
      writeWords(memory, target, 0, Seq.fill(total)(sentinel)*)
      assertEquals(
        run(kernel(stride), "roundtrip", memory, Vec(buffer(source), buffer(target)), local = testDimensions(Count)),
        Right(())
      )
      assertEquals(
        words(memory, target, offset = 0, count = total),
        Vec.tabulate(total)(index => if (index % stride == 0) 0x100L + index else sentinel),
        s"stride $stride"
      )
    }
}
