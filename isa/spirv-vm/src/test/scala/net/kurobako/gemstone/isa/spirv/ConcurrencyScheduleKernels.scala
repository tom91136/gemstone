package net.kurobako.gemstone.isa.spirv

import fixtures.*

object ConcurrencyScheduleKernels {

  def spinModule(localSize: Int = 1): Module = spv"""
    $openCL
    OpEntryPoint Kernel %main "spin" %workgroup
    OpExecutionMode %main LocalSize $localSize 1 1
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
}
