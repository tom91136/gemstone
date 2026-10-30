package net.kurobako.gemstone.isa.amdgpu

import EncodingConstants.*
import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

/** GFX12.5 named barriers, as LLVM's AMDGPUExecutionSynchronization describes them: `s_barrier_init` sets a barrier's
  * expected count from the upper half of M0, `s_barrier_join` makes it the one a wave's `s_barrier_wait` waits on and
  * `s_barrier_leave` drops it. llc 22 lowers `llvm.amdgcn.s.barrier.init`, `.join`, `.signal.var`, `.wait` and `.leave`
  * on a `target("amdgcn.named.barrier", 0)` global to these forms with the barrier ID in M0's low bits and the count
  * from bit 16. The kernels are assembled by `llvm-mc -mcpu=gfx1250`, LLVM 22.
  */
class AMDGPUNamedBarrierSuite extends Suite {
  private val gfx1250 = Target("gfx1250")

  /** Each lane's v2 after `code`, which stores it through s[6:7] at four times its lane, on `lanes` lanes. */
  private def run(code: Vec[Int], lanes: Int)(using munit.Location): Either[Fault, Vec[Int]] = {
    val memory = RegionMemory()
    val output = AllocationId(1L)
    memory.allocate(output, lanes * 4)
    val base    = RegionMemory.Base
    val program = Vec(0xbe8600ff, base.toInt, 0xbe8700ff, (base >>> 32).toInt) ++ code
    val source  = codeKernel(
      target = gfx1250,
      groupSegmentSize = 4,
      kernelCodeProperties = KernelCodeProperty.ENABLE_WAVEFRONT_SIZE32,
      code = words(program*)
    )
    val task = KernelTask.validated("kernel", Dimensions(lanes.unsafe[Int > 0], 1, 1), Dimensions.One, Vec.empty).value
    executeKernel(compileKernel(source), task, memory).map(_ =>
      Vec.tabulate(lanes)(lane => memory.getInt(output, (lane * 4).unsafe[Int >= 0]))
    )
  }

  // the store and end of every kernel: v_lshlrev_b32 v1, 2, v0; global_store_b32 v1, v2, s[6:7]; s_endpgm
  private val Store = Vec(0x30020082, 0xee068006, 0x01000000, 0x00000001, 0xbfb00000)

  // Wave 0 initialises barrier 1 for two waves; after the workgroup barrier, waves 0 and 1 join it, wave 1 writes LDS
  // and both signal and wait, then read LDS; wave 2 never joins. Wave 0 runs first, so it reads 7 only if its wait
  // holds it until wave 1 has signalled.
  //   v_readfirstlane_b32 s8, v0; s_mov_b32 m0, 0x20001; s_cmp_lg_u32 s8, 0; s_cbranch_scc1 1; s_barrier_init m0
  //   s_barrier_signal -1; s_barrier_wait -1; v_mov_b32 v2, 0; s_cmp_eq_u32 s8, 64; s_cbranch_scc1 16
  //   s_barrier_join m0; s_cmp_eq_u32 s8, 32; s_cbranch_scc0 5; v_mov_b32 v3, 0; v_mov_b32 v4, 7
  //   ds_store_b32 v3, v4; s_wait_dscnt 0; s_barrier_signal m0; s_barrier_wait 1; v_mov_b32 v3, 0
  //   ds_load_b32 v2, v3; s_wait_dscnt 0; s_barrier_leave; s_cselect_b32 s9, 1, 0
  private val Joined = Vec(
    0x7e100500, 0xbefd00ff, 0x00020001, 0xbf078008, 0xbfa20001, 0xbe80517d, 0xbe804ec1, 0xbf94ffff, 0x7e040280,
    0xbf06c008, 0xbfa20010, 0xbe80527d, 0xbf06a008, 0xbfa10005, 0x7e060280, 0x7e080287, 0xd8340000, 0x00000403,
    0xbfc60000, 0xbe804e7d, 0xbf940001, 0x7e060280, 0xd8d80000, 0x02000003, 0xbfc60000, 0xbf950000, 0x98098081
  )

  test("a joined wave's s_barrier_wait holds it until the barrier's expected count of waves signal") {
    assertEquals(run(Joined ++ Store, 96), Right(Vec.fill(64)(7) ++ Vec.fill(32)(0)))
  }

  // the same with an expected count of three, which only two waves ever signal
  test("a named barrier its waves cannot complete deadlocks") {
    assert(run(Joined.updated(2, 0x00030001) ++ Store, 96).left.exists(_.message.contains("no wave can release")))
  }

  // RDNA4 has the workgroup barriers -1 and -2 only; named barriers 1 to 16 start with GFX12.5
  test("gfx1201 does not lower named barrier operations") {
    Vec(Vec(0x7d, 0x51, 0x80, 0xbe), Vec(0x7d, 0x52, 0x80, 0xbe), Vec(0x00, 0x00, 0x95, 0xbf)).foreach(code =>
      assert(VMCompiler.decodeInstruction(Vec.from(code.map(_.toByte)), Target("gfx1201")).isLeft, code)
    )
  }
}
