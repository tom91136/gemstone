package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.*

class DeviceEnqueueSuite extends munit.FunSuite {
  private val header =
    s"""OpCapability DeviceEnqueue
       |OpCapability GenericPointer
       |$openCL""".stripMargin

  private val types =
    """%void = OpTypeVoid
      |%uint = OpTypeInt 32 0
      |%ulong = OpTypeInt 64 0
      |%uchar = OpTypeInt 8 0
      |%uint_0 = OpConstant %uint 0
      |%uint_1 = OpConstant %uint 1
      |%uint_3 = OpConstant %uint 3
      |%uint_8 = OpConstant %uint 8
      |%uint_16 = OpConstant %uint 16
      |%uint_7 = OpConstant %uint 7
      |%uint_41 = OpConstant %uint 41
      |%uint_100 = OpConstant %uint 100
      |%ulong_0 = OpConstant %ulong 0
      |%ulong_1 = OpConstant %ulong 1
      |%ulong_4 = OpConstant %ulong 4
      |%ulong_8 = OpConstant %ulong 8
      |%ulong_9 = OpConstant %ulong 9
      |%queue_t = OpTypeQueue
      |%event_t = OpTypeDeviceEvent
      |%sizes = OpTypeArray %ulong %uint_3
      |%ndrange_t = OpTypeStruct %uint %sizes %sizes %sizes
      |%uint_ptr = OpTypePointer CrossWorkgroup %uint
      |%ndrange_ptr = OpTypePointer Function %ndrange_t
      |%event_ptr = OpTypePointer Function %event_t
      |%generic_event_ptr = OpTypePointer Generic %event_t
      |%generic_bytes = OpTypePointer Generic %uchar
      |%no_event = OpConstantNull %generic_event_ptr
      |%v3ulong = OpTypeVector %ulong 3
      |%v3ulong_ptr = OpTypePointer Input %v3ulong
      |%global_ids = OpVariable %v3ulong_ptr Input
      |%block_fn = OpTypeFunction %void %generic_bytes
      |""".stripMargin

  test("enqueued blocks run over their ND-range once the dispatch completes, with the captured literal") {
    val module = spv"""
      $header
      OpEntryPoint Kernel %parent "parent"
      OpEntryPoint Kernel %child "child" %global_ids
      OpDecorate %global_ids BuiltIn GlobalInvocationId
      $types
      %literal_t = OpTypeStruct %uint_ptr %uint
      %literal_ptr = OpTypePointer Function %literal_t
      %generic_literal = OpTypePointer Generic %literal_t
      %generic_out = OpTypePointer Generic %uint_ptr
      %generic_uint = OpTypePointer Generic %uint
      %out_slot = OpTypePointer Function %uint_ptr
      %value_slot = OpTypePointer Function %uint
      %parent_fn = OpTypeFunction %void %uint_ptr
      %parent = OpFunction %void None %parent_fn
      %out = OpFunctionParameter %uint_ptr
      %entry = OpLabel
      %range = OpVariable %ndrange_ptr Function
      %literal = OpVariable %literal_ptr Function
      %queue = OpGetDefaultQueue %queue_t
      %built = OpBuildNDRange %ndrange_t %ulong_4 %ulong_0 %ulong_0
      OpStore %range %built
      %captured_out = OpAccessChain %out_slot %literal %uint_0
      OpStore %captured_out %out
      %captured_value = OpAccessChain %value_slot %literal %uint_1
      OpStore %captured_value %uint_7
      %generic = OpPtrCastToGeneric %generic_literal %literal
      %bytes = OpBitcast %generic_bytes %generic
      %status = OpEnqueueKernel %uint %queue %uint_0 %range %uint_0 %no_event %no_event %child %bytes %uint_16 %uint_8
      %status_slot = OpPtrAccessChain %uint_ptr %out %ulong_9
      OpStore %status_slot %status
      %late = OpPtrAccessChain %uint_ptr %out %ulong_8
      OpStore %late %uint_100
      OpReturn
      OpFunctionEnd
      %child = OpFunction %void None %block_fn
      %block = OpFunctionParameter %generic_bytes
      %child_entry = OpLabel
      %view = OpBitcast %generic_literal %block
      %out_field = OpInBoundsPtrAccessChain %generic_out %view %ulong_0 %uint_0
      %target = OpLoad %uint_ptr %out_field
      %value_field = OpInBoundsPtrAccessChain %generic_uint %view %ulong_0 %uint_1
      %value = OpLoad %uint %value_field
      %ids = OpLoad %v3ulong %global_ids
      %id = OpCompositeExtract %ulong %ids 0
      %id_32 = OpUConvert %uint %id
      %late_slot = OpPtrAccessChain %uint_ptr %target %ulong_8
      %late_value = OpLoad %uint %late_slot
      %partial = OpIAdd %uint %value %id_32
      %sum = OpIAdd %uint %partial %late_value
      %slot = OpPtrAccessChain %uint_ptr %target %id
      OpStore %slot %sum
      OpReturn
      OpFunctionEnd
    """
    val memory = RegionMemory()
    val out    = memory.allocate(40L)
    writeWords(memory, out, 0, Seq.fill(10)(0xffffffffL)*)
    assertEquals(run(module, "parent", memory, Vec(buffer(out))), Right(()))
    assertEquals(words(memory, out, 0, 4), Vec(107L, 108L, 109L, 110L))
    assertEquals(word(memory, out, 36), 0L)
  }

  test("enqueued commands wait on user events that later commands complete") {
    val module = spv"""
      $header
      OpEntryPoint Kernel %parent "parent"
      OpEntryPoint Kernel %waiter "waiter"
      OpEntryPoint Kernel %opener "opener"
      $types
      %waiter_t = OpTypeStruct %uint_ptr
      %opener_t = OpTypeStruct %uint_ptr %event_t
      %waiter_ptr = OpTypePointer Function %waiter_t
      %opener_ptr = OpTypePointer Function %opener_t
      %generic_waiter = OpTypePointer Generic %waiter_t
      %generic_opener = OpTypePointer Generic %opener_t
      %generic_out = OpTypePointer Generic %uint_ptr
      %out_slot = OpTypePointer Function %uint_ptr
      %event_slot = OpTypePointer Function %event_t
      %parent_fn = OpTypeFunction %void %uint_ptr
      %parent = OpFunction %void None %parent_fn
      %out = OpFunctionParameter %uint_ptr
      %entry = OpLabel
      %range = OpVariable %ndrange_ptr Function
      %gate = OpVariable %event_ptr Function
      %first = OpVariable %waiter_ptr Function
      %second = OpVariable %opener_ptr Function
      %queue = OpGetDefaultQueue %queue_t
      %built = OpBuildNDRange %ndrange_t %ulong_1 %ulong_0 %ulong_0
      OpStore %range %built
      %event = OpCreateUserEvent %event_t
      OpStore %gate %event
      %waits = OpPtrCastToGeneric %generic_event_ptr %gate
      %first_out = OpAccessChain %out_slot %first %uint_0
      OpStore %first_out %out
      %first_generic = OpPtrCastToGeneric %generic_waiter %first
      %first_bytes = OpBitcast %generic_bytes %first_generic
      %waited = OpEnqueueKernel %uint %queue %uint_0 %range %uint_1 %waits %no_event %waiter %first_bytes %uint_8 %uint_8
      %second_out = OpAccessChain %out_slot %second %uint_0
      OpStore %second_out %out
      %second_event = OpAccessChain %event_slot %second %uint_1
      OpStore %second_event %event
      %second_generic = OpPtrCastToGeneric %generic_opener %second
      %second_bytes = OpBitcast %generic_bytes %second_generic
      %opened = OpEnqueueKernel %uint %queue %uint_0 %range %uint_0 %no_event %no_event %opener %second_bytes %uint_16 %uint_8
      OpReleaseEvent %event
      OpReturn
      OpFunctionEnd
      %waiter = OpFunction %void None %block_fn
      %waiter_block = OpFunctionParameter %generic_bytes
      %waiter_entry = OpLabel
      %waiter_view = OpBitcast %generic_waiter %waiter_block
      %waiter_field = OpInBoundsPtrAccessChain %generic_out %waiter_view %ulong_0 %uint_0
      %waiter_target = OpLoad %uint_ptr %waiter_field
      %opened_value = OpLoad %uint %waiter_target
      %next = OpIAdd %uint %opened_value %uint_1
      %next_slot = OpPtrAccessChain %uint_ptr %waiter_target %ulong_1
      OpStore %next_slot %next
      OpReturn
      OpFunctionEnd
      %opener = OpFunction %void None %block_fn
      %opener_block = OpFunctionParameter %generic_bytes
      %opener_entry = OpLabel
      %opener_view = OpBitcast %generic_opener %opener_block
      %opener_field = OpInBoundsPtrAccessChain %generic_out %opener_view %ulong_0 %uint_0
      %opener_target = OpLoad %uint_ptr %opener_field
      OpStore %opener_target %uint_41
      %gate_field = OpInBoundsPtrAccessChain %generic_event_ptr %opener_view %ulong_0 %uint_1
      %opened_gate = OpLoad %event_t %gate_field
      OpSetUserEventStatus %opened_gate %uint_0
      OpReturn
      OpFunctionEnd
    """
    val memory = RegionMemory()
    val out    = memory.allocate(8L)
    assertEquals(run(module, "parent", memory, Vec(buffer(out))), Right(()))
    assertEquals(words(memory, out, 0, 2), Vec(41L, 42L))
  }
}
