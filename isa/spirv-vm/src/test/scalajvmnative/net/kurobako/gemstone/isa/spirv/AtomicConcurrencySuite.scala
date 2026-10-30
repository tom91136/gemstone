package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*

class AtomicConcurrencySuite extends Suite {

  /** The first arriving workgroup publishes `seed` behind a release flag; later ones copy it if the flag is set. */
  private val publication = spv"""
    $openCL
    OpEntryPoint Kernel %main "publish" %workgroup_id
    OpDecorate %workgroup_id BuiltIn WorkgroupId
    %void = OpTypeVoid
    %bool = OpTypeBool
    %uint = OpTypeInt 32 0
    %ulong = OpTypeInt 64 0
    %ulong3 = OpTypeVector %ulong 3
    %ulong3_ptr = OpTypePointer Input %ulong3
    %uint_ptr = OpTypePointer CrossWorkgroup %uint
    %fn = OpTypeFunction %void %uint_ptr %uint_ptr %uint_ptr %uint_ptr %uint
    %zero = OpConstant %uint 0
    %one = OpConstant %uint 1
    %two = OpConstant %uint 2
    %device = OpConstant %uint ${Scope.Device}
    %release = OpConstant %uint ${MemorySemantics.CrossWorkgroupMemory | MemorySemantics.Release}
    %acquire = OpConstant %uint ${MemorySemantics.CrossWorkgroupMemory | MemorySemantics.Acquire}
    %acquire_release = OpConstant %uint ${MemorySemantics.CrossWorkgroupMemory | MemorySemantics.AcquireRelease}
    %workgroup_id = OpVariable %ulong3_ptr Input
    %main = OpFunction %void None %fn
    %records = OpFunctionParameter %uint_ptr
    %counter = OpFunctionParameter %uint_ptr
    %payload = OpFunctionParameter %uint_ptr
    %flag = OpFunctionParameter %uint_ptr
    %seed = OpFunctionParameter %uint
    %entry = OpLabel
    %ids = OpLoad %ulong3 %workgroup_id
    %id = OpCompositeExtract %ulong %ids 0
    %group = OpUConvert %uint %id
    %record = OpIMul %uint %group %two
    %ticket_out = OpPtrAccessChain %uint_ptr %records %record
    %observed_index = OpIAdd %uint %record %one
    %observed_out = OpPtrAccessChain %uint_ptr %records %observed_index
    %ticket = OpAtomicIAdd %uint %counter %device %acquire_release %one
    OpStore %ticket_out %ticket
    %first = OpIEqual %bool %ticket %zero
    OpSelectionMerge %done None
    OpBranchConditional %first %produce %consume
    %produce = OpLabel
    OpStore %payload %seed
    OpAtomicStore %flag %device %release %one
    OpStore %observed_out %seed
    OpBranch %done
    %consume = OpLabel
    %ready_flag = OpAtomicLoad %uint %flag %device %acquire
    %ready = OpINotEqual %bool %ready_flag %zero
    OpSelectionMerge %consumed None
    OpBranchConditional %ready %copy %skip
    %copy = OpLabel
    %published = OpLoad %uint %payload
    OpStore %observed_out %published
    OpBranch %consumed
    %skip = OpLabel
    OpStore %observed_out %zero
    OpBranch %consumed
    %consumed = OpLabel
    OpBranch %done
    %done = OpLabel
    OpReturn
    OpFunctionEnd
  """

  test("device-scope atomic election publishes a payload across workgroups without waiting for all groups") {
    val seeds = Vec(17L, 91L)
    for {
      groups   <- Vec[Int > 0](1, 2, 3, 5, 9)
      parallel <- booleans
    } {
      val size   = 12 + groups.raw * 8
      val memory = RegionMemory()
      val region = memory.allocate(size.toLong.refine[Long > 0].value)
      for (seed <- seeds) {
        assertEquals(memory.write(region, 0L, Vec.fill(size)(0.toByte)), Right(()))
        val schedule = Interleaving()
        val program  = load(publication, if (parallel) schedule else Workgroups.Sequential).value
        val launch   = task(
          "publish",
          Vec(buffer(region, 12L), buffer(region), buffer(region, 4L), buffer(region, 8L), scalar(seed, 4)),
          groups = testDimensions(groups)
        )
        assertEquals(execute(program, launch, schedule.InterleavedMemory(memory)), Right(()))
        val published = words(memory, region, 0, size / 4)
        assertEquals(published.take(3), Vec(groups.toLong, seed, 1L))
        val records = published.drop(3).grouped(2).toVector
        assertEquals(records.map(_.head).sorted, (0L until groups.toLong).toVector)
        assert(records.forall(record => record(1) == 0L || record(1) == seed))
        assert(records.exists(_(1) == seed))
        if (parallel) assertEquals(schedule.calls.get(), if (groups > 1) 1 else 0)
        if (groups > 1 && !parallel) {
          val consumers = records.filter(_.head != 0L)
          assert(consumers.nonEmpty)
          assert(consumers.forall(_(1) == seed))
        }
        // a completed producer launch makes the flag visible without co-resident workgroups
        assertEquals(execute(program, launch, schedule.InterleavedMemory(memory)), Right(()))
        val consumed = words(memory, region, 0, size / 4)
        assertEquals(consumed.take(3), Vec(groups.toLong * 2, seed, 1L))
        val observed = consumed.drop(3).grouped(2).toVector
        assertEquals(observed.map(_.head).sorted, (groups.toLong until groups.toLong * 2).toVector)
        assert(observed.forall(_(1) == seed))
      }
    }
  }
}
