package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.*

class FunctionVariableSuite extends munit.FunSuite {
  private def check(name: String, module: Module, expected: Long*) = test(name) {
    val memory = RegionMemory()
    val out    = memory.allocate(16L)
    assertEquals(run(module, "round_trip", memory, Vec(buffer(out))), Right(()))
    assertEquals(words(memory, out, offset = 0, count = expected.size), Vec.from(expected))
  }

  check(
    "an initialised Function variable reads back",
    spv"""
      $openCL
      OpEntryPoint Kernel %main "round_trip"
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %out_ptr = OpTypePointer CrossWorkgroup %uint
      %local_ptr = OpTypePointer Function %uint
      %fn = OpTypeFunction %void %out_ptr
      %value = OpConstant %uint 77
      %main = OpFunction %void None %fn
      %out = OpFunctionParameter %out_ptr
      %entry = OpLabel
      %local = OpVariable %local_ptr Function %value
      %loaded = OpLoad %uint %local
      OpStore %out %loaded
      OpReturn
      OpFunctionEnd
    """,
    77L
  )

  check(
    "a Function variable in a callee round trips",
    spv"""
      $openCL
      OpEntryPoint Kernel %main "round_trip"
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %out_ptr = OpTypePointer CrossWorkgroup %uint
      %local_ptr = OpTypePointer Function %uint
      %fn = OpTypeFunction %void %out_ptr
      %helper_fn = OpTypeFunction %uint
      %value = OpConstant %uint 77
      %helper = OpFunction %uint None %helper_fn
      %h_entry = OpLabel
      %hlocal = OpVariable %local_ptr Function
      OpStore %hlocal %value
      %hloaded = OpLoad %uint %hlocal
      OpReturnValue %hloaded
      OpFunctionEnd
      %main = OpFunction %void None %fn
      %out = OpFunctionParameter %out_ptr
      %entry = OpLabel
      %r = OpFunctionCall %uint %helper
      OpStore %out %r
      OpReturn
      OpFunctionEnd
    """,
    77L
  )

  check(
    "a callee stores through a Function pointer argument",
    spv"""
      $openCL
      OpEntryPoint Kernel %main "round_trip"
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %out_ptr = OpTypePointer CrossWorkgroup %uint
      %local_ptr = OpTypePointer Function %uint
      %fn = OpTypeFunction %void %out_ptr
      %helper_fn = OpTypeFunction %void %local_ptr
      %value = OpConstant %uint 77
      %helper = OpFunction %void None %helper_fn
      %p = OpFunctionParameter %local_ptr
      %h_entry = OpLabel
      OpStore %p %value
      OpReturn
      OpFunctionEnd
      %main = OpFunction %void None %fn
      %out = OpFunctionParameter %out_ptr
      %entry = OpLabel
      %local = OpVariable %local_ptr Function
      %r = OpFunctionCall %void %helper %local
      %loaded = OpLoad %uint %local
      OpStore %out %loaded
      OpReturn
      OpFunctionEnd
    """,
    77L
  )

  check(
    "a Function variable of a structure round trips through an access chain",
    spv"""
      $openCL
      OpEntryPoint Kernel %main "round_trip"
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %uint_0 = OpConstant %uint 0
      %uint_1 = OpConstant %uint 1
      %uint_4 = OpConstant %uint 4
      %arr = OpTypeArray %uint %uint_4
      %s = OpTypeStruct %uint %arr
      %out_ptr = OpTypePointer CrossWorkgroup %uint
      %s_ptr = OpTypePointer Function %s
      %local_ptr = OpTypePointer Function %uint
      %fn = OpTypeFunction %void %out_ptr
      %value = OpConstant %uint 77
      %main = OpFunction %void None %fn
      %out = OpFunctionParameter %out_ptr
      %entry = OpLabel
      %local = OpVariable %s_ptr Function
      %e = OpAccessChain %local_ptr %local %uint_1 %uint_1
      OpStore %e %value
      %loaded = OpLoad %uint %e
      OpStore %out %loaded
      OpReturn
      OpFunctionEnd
    """,
    77L
  )

  check(
    "a pointer held in a Function variable round trips",
    spv"""
      $openCL
      OpEntryPoint Kernel %main "round_trip"
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %out_ptr = OpTypePointer CrossWorkgroup %uint
      %local_ptr = OpTypePointer Function %uint
      %holder_ptr = OpTypePointer Function %local_ptr
      %fn = OpTypeFunction %void %out_ptr
      %value = OpConstant %uint 77
      %main = OpFunction %void None %fn
      %out = OpFunctionParameter %out_ptr
      %entry = OpLabel
      %local = OpVariable %local_ptr Function
      %holder = OpVariable %holder_ptr Function
      OpStore %local %value
      OpStore %holder %local
      %back = OpLoad %local_ptr %holder
      %loaded = OpLoad %uint %back
      OpStore %out %loaded
      OpReturn
      OpFunctionEnd
    """,
    77L
  )
}
