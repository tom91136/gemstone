package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*

class FunctionVariableConcurrencySuite extends Suite {
  private val module = spv"""
    $openCL
    OpEntryPoint Kernel %main "groups" %global_ids
    OpDecorate %global_ids BuiltIn GlobalInvocationId
    %void = OpTypeVoid
    %uint = OpTypeInt 32 0
    %ulong = OpTypeInt 64 0
    %ulong3 = OpTypeVector %ulong 3
    %ulong3_ptr = OpTypePointer Input %ulong3
    %uint_ptr = OpTypePointer CrossWorkgroup %uint
    %local_ptr = OpTypePointer Function %uint
    %workgroup = OpConstant %uint 2
    %semantics = OpConstant %uint 264
    %fn = OpTypeFunction %void %uint_ptr
    %global_ids = OpVariable %ulong3_ptr Input
    %main = OpFunction %void None %fn
    %out = OpFunctionParameter %uint_ptr
    %entry = OpLabel
    %local = OpVariable %local_ptr Function
    %global_vector = OpLoad %ulong3 %global_ids
    %global_x = OpCompositeExtract %ulong %global_vector 0
    %global = OpUConvert %uint %global_x
    OpStore %local %global
    OpControlBarrier %workgroup %workgroup %semantics
    %loaded = OpLoad %uint %local
    %slot = OpPtrAccessChain %uint_ptr %out %global_x
    OpStore %slot %loaded
    OpReturn
    OpFunctionEnd
  """

  private val groupCounts = Vec[Int > 0](1, 2, 3)

  for {
    parallel <- booleans
    groups   <- groupCounts
  } test(s"function variables round trip on every workgroup parallel=$parallel groups=$groups") {
    val local    = 4
    val count    = local * groups.raw
    val memory   = RegionMemory()
    val region   = memory.allocate(((count + 1) * 4).toLong.refine[Long > 0].value)
    val schedule = Interleaving()
    val program  = load(module, if (parallel) schedule else Workgroups.Sequential).value
    val launch   =
      task("groups", Vec(buffer(region, 4L)), testDimensions(local.refine[Int > 0].value), testDimensions(groups))
    assertEquals(execute(program, launch, schedule.InterleavedMemory(memory)), Right(()))
    assertEquals(words(memory, region, 4, count), Vec.tabulate(count)(_.toLong))
  }
}
