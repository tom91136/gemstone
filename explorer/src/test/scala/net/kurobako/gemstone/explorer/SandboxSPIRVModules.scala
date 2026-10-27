package net.kurobako.gemstone.explorer

/** SPIR-V assembly fixtures for the sandbox tests. They are shared by the JVM and Scala.js suites. */
object SandboxSPIRVModules {

  /** One invocation: a conditional branch, a phi, a call and a return writing 20 to the output. */
  val branch: String =
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

  /** Four invocations park at OpControlBarrier, resume, and each store 1. */
  val barrier: String =
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

  /** A minimal single-invocation module used by the Scala.js smoke test. */
  val store: String =
    """OpCapability Kernel
      |OpMemoryModel Physical64 OpenCL
      |OpEntryPoint Kernel %main "store"
      |%void = OpTypeVoid
      |%uint = OpTypeInt 32 0
      |%uint_ptr = OpTypePointer CrossWorkgroup %uint
      |%fn = OpTypeFunction %void %uint_ptr
      |%value = OpConstant %uint 42
      |%main = OpFunction %void None %fn
      |%out = OpFunctionParameter %uint_ptr
      |%entry = OpLabel
      |OpStore %out %value
      |OpReturn
      |OpFunctionEnd
      |""".stripMargin
}
