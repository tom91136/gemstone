package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*

class ForwardProgressSuite extends Suite {
  // Lane 0 of the first subgroup spins until lane 32, in the second subgroup, publishes a flag; with a barrier, the rest
  // of the first subgroup waits at a subgroup barrier meanwhile.
  private def spin(barrier: Boolean): Module = spv"""
    $openCL
    OpEntryPoint Kernel %main "spin" %lid
    OpDecorate %lid BuiltIn LocalInvocationIndex
    %void = OpTypeVoid
    %bool = OpTypeBool
    %uint = OpTypeInt 32 0
    %uint_ptr = OpTypePointer CrossWorkgroup %uint
    %flag_ptr = OpTypePointer Workgroup %uint
    %lid_ptr = OpTypePointer Input %uint
    %lid = OpVariable %lid_ptr Input
    %flag = OpVariable %flag_ptr Workgroup
    %fn = OpTypeFunction %void %uint_ptr
    %uint_0 = OpConstant %uint 0
    %uint_1 = OpConstant %uint 1
    %uint_32 = OpConstant %uint 32
    %subgroup = OpConstant %uint 3
    %workgroup = OpConstant %uint 2
    %relaxed = OpConstant %uint 0
    %acquire_release = OpConstant %uint 0x108
    %main = OpFunction %void None %fn
    %out = OpFunctionParameter %uint_ptr
    %entry = OpLabel
    %lane = OpLoad %uint %lid
    %waiter = OpIEqual %bool %lane %uint_0
    %not_waiter = OpLogicalNot %bool %waiter
    %publisher = OpIEqual %bool %lane %uint_32
    OpSelectionMerge %published None
    OpBranchConditional %publisher %publish %published
    %publish = OpLabel
    OpAtomicStore %flag %workgroup %acquire_release %uint_1
    OpBranch %published
    %published = OpLabel
    OpBranch %loop
    %loop = OpLabel
    OpLoopMerge %done %continue None
    OpBranch %body
    %body = OpLabel
    %seen = OpAtomicLoad %uint %flag %workgroup %acquire_release
    %ready = OpIEqual %bool %seen %uint_1
    %leave = OpLogicalOr %bool %ready %not_waiter
    OpBranchConditional %leave %done %continue
    %continue = OpLabel
    OpBranch %loop
    %done = OpLabel
    ${if (barrier) "OpControlBarrier %subgroup %subgroup %relaxed" else ""}
    %slot = OpPtrAccessChain %uint_ptr %out %lane
    %result = OpAtomicLoad %uint %flag %workgroup %acquire_release
    OpStore %slot %result
    OpReturn
    OpFunctionEnd
  """

  for (barrier <- booleans)
    test(
      s"a spinning subgroup lets another subgroup make progress${if (barrier) " across subgroup barriers" else ""}"
    ) {
      val memory  = RegionMemory()
      val out     = memory.allocate(256L)
      val control = TaskControl(TaskControl.Limits(5000000L.unsafe[Long > 0]))
      val result  =
        load(spin(barrier)).flatMap(execute(_, task("spin", Vec(buffer(out)), testDimensions(64)), memory, control))
      assertEquals(result, Right(()))
      assertEquals(word(memory, out, 0), 1L)
    }
}
