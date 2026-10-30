package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class KernelLaunchConcurrencySuite extends Suite {

  /** Work-item value `(group + seed) * 1000 + local * 10 + global`; with `barrier`, the group term comes from the
    * work-item `producer` through Workgroup memory.
    */
  private def geometry(barrier: Boolean, producer: Int): Module = spv"""
    $openCL
    OpEntryPoint Kernel %main "groups" %global_ids %local_ids %group_ids
    OpDecorate %global_ids BuiltIn GlobalInvocationId
    OpDecorate %local_ids BuiltIn LocalInvocationId
    OpDecorate %group_ids BuiltIn WorkgroupId
    %void = OpTypeVoid
    %bool = OpTypeBool
    %uint = OpTypeInt 32 0
    %ulong = OpTypeInt 64 0
    %ulong3 = OpTypeVector %ulong 3
    %ulong3_ptr = OpTypePointer Input %ulong3
    %uint_ptr = OpTypePointer CrossWorkgroup %uint
    %shared_ptr = OpTypePointer Workgroup %uint
    %fn = OpTypeFunction %void %uint_ptr %uint %shared_ptr
    %producer = OpConstant %uint $producer
    %thousand = OpConstant %uint 1000
    %ten = OpConstant %uint 10
    %workgroup = OpConstant %uint ${Scope.Workgroup}
    %semantics = OpConstant %uint ${MemorySemantics.WorkgroupMemory | MemorySemantics.AcquireRelease}
    %global_ids = OpVariable %ulong3_ptr Input
    %local_ids = OpVariable %ulong3_ptr Input
    %group_ids = OpVariable %ulong3_ptr Input
    %main = OpFunction %void None %fn
    %out = OpFunctionParameter %uint_ptr
    %seed = OpFunctionParameter %uint
    %shared = OpFunctionParameter %shared_ptr
    %entry = OpLabel
    %global_vector = OpLoad %ulong3 %global_ids
    %local_vector = OpLoad %ulong3 %local_ids
    %group_vector = OpLoad %ulong3 %group_ids
    %global_x = OpCompositeExtract %ulong %global_vector 0
    %local_x = OpCompositeExtract %ulong %local_vector 0
    %group_x = OpCompositeExtract %ulong %group_vector 0
    %global = OpUConvert %uint %global_x
    %local = OpUConvert %uint %local_x
    %group = OpUConvert %uint %group_x
    %group_term = OpIAdd %uint %group %seed
    ${
      if (barrier) """%is_producer = OpIEqual %bool %local %producer
                     |OpSelectionMerge %exchanged None
                     |OpBranchConditional %is_producer %publish %exchanged
                     |%publish = OpLabel
                     |OpStore %shared %group_term
                     |OpBranch %exchanged
                     |%exchanged = OpLabel
                     |OpControlBarrier %workgroup %workgroup %semantics
                     |%shared_term = OpLoad %uint %shared""".stripMargin
      else "%shared_term = OpCopyObject %uint %group_term"
    }
    %group_scaled = OpIMul %uint %shared_term %thousand
    %local_scaled = OpIMul %uint %local %ten
    %partial = OpIAdd %uint %group_scaled %local_scaled
    %result = OpIAdd %uint %partial %global
    %slot = OpPtrAccessChain %uint_ptr %out %global_x
    OpStore %slot %result
    OpReturn
    OpFunctionEnd
  """

  test("cross-workgroup IDs, partial extents and workgroup barriers stay isolated across launches") {
    val locals = Vec[Int > 0](31, 32, 33)
    val seeds  = Vec(1, 9)
    for {
      groups   <- Vec[Int > 0](1, 2, 3, 5, 9)
      local    <- locals
      parallel <- booleans
      barrier  <- booleans
      producer <- if (barrier) Vec(2, local - 1) else Vec(2)
    } {
      val count            = (local.raw * groups - (if (barrier) 0 else 1)).refine[Int > 0].value
      val offset: Int >= 0 = 7
      val start            = Coordinates(offset, 0, 0)
      val memory           = RegionMemory()
      val region           = memory.allocate(((offset.raw + count.raw + 2) * 4).toLong.refine[Long > 0].value)
      val module           = geometry(barrier, producer)
      for (seed <- seeds) {
        val schedule = Interleaving()
        val program  = load(module, if (parallel) schedule else Workgroups.Sequential).value
        val partial  = task(
          "groups",
          Vec(buffer(region, 4L), scalar(seed.toLong, 4), KernelTask.Argument.Local(4L)),
          testDimensions(local),
          testDimensions(groups),
          globalSize = Some(testDimensions(count).toGlobalDimensions),
          globalOffset = start
        )
        assertEquals(
          execute(program, partial, schedule.InterleavedMemory(memory)),
          Right(()),
          s"groups=$groups local=$local parallel=$parallel barrier=$barrier"
        )
        assertEquals(schedule.calls.get(), if (parallel && groups > 1) 1 else 0)
        val expected = Vec.tabulate(count) { index =>
          (index / local + seed) * 1000L + (index % local) * 10L + offset + index
        }
        assertEquals(
          words(memory, region, 0, offset.raw + count.raw + 2),
          Vec.fill(offset.raw + 1)(0L) ++ expected ++ Vec(0L)
        )
      }
    }
  }
}
