package net.kurobako.gemstone.isa.amdgpu

import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

/** GFX8 and GFX9 scalar stores and atomics, the conditional branch stack, VSKIP, the ADDTID forms and CDNA's DS and
  * VMEM data in accumulation registers. No compiler emits the scalar stores, atomics, forks, joins or S_SETVSKIP, so
  * their programs are hand-written and assembled by `llvm-mc -triple=amdgcn -mcpu=gfx906` (gfx90a for the accumulation
  * registers, LLVM 22), expected values following the GCN3 and Vega ISA documents; a prologue in Gemstone's assembly
  * sets up the allocations' addresses.
  */
class AMDGPUScalarStoreSuite extends Suite {
  private val gfx906 = Target("gfx906")
  private val gfx90a = Target("gfx90a")

  // GFX9 32_UINT, X Y Z W selects
  private val Word3 = 0xfacL | (4L << 15) | (4L << 12)

  /** `lanes` lanes of `program`: the input of ascending dwords from 0x1000 and a 1 KiB output, both after the run. */
  private def run(target: Target, lanes: Int, program: Vec[Byte])(using munit.Location): (Vec[Int], Vec[Int]) = {
    val memory = RegionMemory()
    val input  = AllocationId(1L)
    val output = AllocationId(2L)
    memory.allocate(input, 4096)
    memory.allocate(output, 1024)
    (0 until 1024).foreach(index => memory.putInt(input, (index * 4).unsafe[Int >= 0], 0x1000 + index))
    val source = codeKernel(target = target, groupSegmentSize = 4096, code = program)
    val task = KernelTask.validated("kernel", Dimensions(lanes.unsafe[Int > 0], 1, 1), Dimensions.One, Vec.empty).value
    assertEquals(executeKernel(compileKernel(source), task, memory), Right(()))
    def read(id: AllocationId, count: Int) =
      Vec.tabulate(count)(index => memory.getInt(id, (index * 4).unsafe[Int >= 0]))
    (read(input, 1024), read(output, 256))
  }

  private def assemble(target: Target, text: String)(using munit.Location): Vec[Byte] =
    asm.Assembler.encode(asm.Parser.parse(text).value, TargetMode(target, 64)).value

  private def address(allocation: Long): Long = RegionMemory.Base + (allocation - 1L) * RegionMemory.Stride

  /** Moves of a buffer descriptor of the input of `records` bytes into s[8:11], whose first pair is also the input's
    * address, and the output's address into s[14:15].
    */
  private def prologue(target: Target, records: Long = 16L): Vec[Byte] = {
    val base = address(1L)
    val out  = address(2L)
    assemble(
      target,
      f"""s_mov_b32 s8, 0x${base & 0xffffffffL}%x
         |s_mov_b32 s9, 0x${base >>> 32}%x
         |s_mov_b32 s10, 0x$records%x
         |s_mov_b32 s11, 0x$Word3%x
         |s_mov_b32 s14, 0x${out & 0xffffffffL}%x
         |s_mov_b32 s15, 0x${out >>> 32}%x
         |""".stripMargin
    )
  }

  test("scalar stores write SGPRs to global memory, and through a descriptor up to the end of its buffer") {
    // s_mov_b32 s20..s23, 0x11..0x44; s_mov_b32 s12, 0x30
    // s_store_dword s20, s[14:15], 0x0; s_store_dwordx2 s[20:21], s[14:15], s12
    // s_store_dwordx4 s[20:23], s[14:15], 0x40; s_buffer_store_dwordx4 s[20:23], s[8:11], 0x8
    // s_dcache_wb; s_endpgm
    val code = words(
      0xbe940091, 0xbe9500a2, 0xbe9600b3, 0xbe9700ff, 0x00000044, 0xbe8c00b0, 0xc0420507, 0x00000000,
      0xc0440507, 0x0000000c, 0xc04a0507, 0x00000040, 0xc06a0504, 0x00000008, 0xc0840000, 0x00000000,
      0xbf810000
    )
    val stored = Vec(0x11, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0x11, 0x22, 0, 0, 0x11, 0x22, 0x33, 0x44)
    // a 16-byte buffer holds the first two of the four dwords from byte 8
    val clipped = Vec(0x1000, 0x1001, 0x11, 0x22, 0x1004)
    Vec(gfx906, Target("gfx803")).foreach { target =>
      val (input, output) = run(target, 1, prologue(target) ++ code)
      assertEquals(output.take(20), stored)
      assertEquals(input.take(5), clipped)
    }
  }

  test("scalar atomics update global memory and buffers, returning the old value under GLC") {
    // s_atomic_add s20 (5), 0x0 glc; s_atomic_cmpswap s[22:23] (7, 0x1001), 0x4 glc
    // s_atomic_inc s24 (0x1002), 0x8 glc; s_atomic_umax_x2 s[26:27] (0x2000 << 32), 0x10 glc
    // s_buffer_atomic_sub s28 (1), 0x40 glc past the 16-byte buffer; s_buffer_atomic_xor s29 (0xff), 0xc glc
    // s_atomic_dec s30 (3), 0x18; s_atomic_cmpswap_x2 s[32:35] (0xbb_000000aa, 0x1009_00001008), 0x20 glc
    // then s20, s22, s24, s26, s27, s28, s29, s30, s32 and s33 to the output
    val code = words(
      0xbe940085, 0xbe960087, 0xbe9700ff, 0x00001001, 0xbe9800ff, 0x00001002, 0xbe9a0080, 0xbe9b00ff,
      0x00002000, 0xbe9c0081, 0xbe9d00ff, 0x000000ff, 0xbe9e0083, 0xbea000ff, 0x000000aa, 0xbea100ff,
      0x000000bb, 0xbea200ff, 0x00001008, 0xbea300ff, 0x00001009, 0xc20b0504, 0x00000000, 0xc2070584,
      0x00000004, 0xc22f0604, 0x00000008, 0xc29f0684, 0x00000010, 0xc10f0704, 0x00000040, 0xc12b0744,
      0x0000000c, 0xc2320784, 0x00000018, 0xc2870804, 0x00000020, 0xbf8cc07f, 0x7e020280, 0x7e040214,
      0x7e060216, 0x7e080218, 0x7e0a021a, 0xdc7c8000, 0x000e0201, 0x7e04021b, 0x7e06021c, 0x7e08021d,
      0x7e0a021e, 0xdc7c8010, 0x000e0201, 0x7e040220, 0x7e060221, 0xdc748020, 0x000e0201, 0xbf810000
    )
    val (input, output) = run(gfx906, 1, prologue(gfx906) ++ code)
    assertEquals(output.take(10), Vec(0x1000, 0x1001, 0x1002, 0x1004, 0x1005, 0, 0x1003, 3, 0x1008, 0x1009))
    // inc wraps to zero at its operand; umax keeps the larger pair; dec wraps to its operand above it
    assertEquals(input.take(10), Vec(0x1005, 7, 0, 0x10fc, 0, 0x2000, 3, 0x1007, 0xaa, 0xbb))
    assertEquals(input(16), 0x1010)
  }

  /** The two sides of a fork in each lane's dword, then EXEC after the join and CSP. */
  private def forked(code: Vec[Byte])(using munit.Location): (Vec[Int], Vec[Int]) = {
    val (_, output) = run(gfx906, 4, prologue(gfx906) ++ code)
    (output.take(4), output.slice(16, 19))
  }

  test("s_cbranch_i_fork runs the fewer lanes first, deferring the others to the join on the SGPR stack") {
    // s_getreg_b32 s40, hwreg(HW_REG_MODE, 29, 3); v_cmp_gt_u32 vcc, 3, v0; v_mov_b32 v2, 0
    // s_cbranch_i_fork vcc, 2; v_mov_b32 v2, 1; s_branch 1; v_mov_b32 v2, 2; s_cbranch_join s40
    // s_mov_b64 s[42:43], exec; s_getreg_b32 s41, hwreg(HW_REG_MODE, 29, 3); s_mov_b64 exec, -1
    // v2 to the output by lane, then s42, s43 and s41 from byte 64
    val code = words(
      0xb8a81741, 0x7d980083, 0x7e040280, 0xb86a0002, 0x7e040281, 0xbf820001, 0x7e040282, 0xbe802e28,
      0xbeaa017e, 0xb8a91741, 0xbefe01c1, 0x24060082, 0xdc708000, 0x000e0203, 0x7e08022a, 0x7e0a022b,
      0x7e0c0229, 0x7e0e0280, 0xdc788040, 0x000e0407, 0xbf810000
    )
    // lane 3 alone fails, so it runs first; the join then resumes the three passing lanes at the target
    assertEquals(forked(code), (Vec(2, 2, 2, 1), Vec(7, 0, 0)))
  }

  test("s_cbranch_g_fork branches with the fewer passing lanes and joins the others where it fell through") {
    // as above with v_cmp_gt_u32 vcc, 1, v0 and the target in s[44:45] from s_getpc_b64 plus 20:
    // s_cbranch_g_fork vcc, s[44:45]
    val code = words(
      0xb8a81741, 0x7d980081, 0x7e040280, 0xbeac1c00, 0x802c942c, 0x822d802d, 0x94802c6a, 0x7e040281,
      0xbf820001, 0x7e040282, 0xbe802e28, 0xbeaa017e, 0xb8a91741, 0xbefe01c1, 0x24060082, 0xdc708000,
      0x000e0203, 0x7e08022a, 0x7e0a022b, 0x7e0c0229, 0x7e0e0280, 0xdc788040, 0x000e0407, 0xbf810000
    )
    // a second join at the same CSP continues with the lanes the first one resumed
    assertEquals(forked(code), (Vec(2, 1, 1, 1), Vec(0xe, 0, 0)))
  }

  test("s_setvskip skips vector instructions while VSKIP is set, scalar ones still running") {
    // v_mov_b32 v2, 1; s_mov_b32 s41, 1; s_setvskip s41, 0; v_mov_b32 v2, 2; s_mov_b32 s20, 7
    // s_getreg_b32 s21, hwreg(HW_REG_MODE, 28, 1); s_setvskip 0, 0; v_mov_b32 v3, s20; v_mov_b32 v4, s21
    // v[2:4] to the output at 16 bytes a lane
    val code = words(
      0x7e040281, 0xbea90081, 0xbf108029, 0x7e040282, 0xbe940087, 0xb8950701, 0xbf108080, 0x7e060214,
      0x7e080215, 0x240a0084, 0xdc788000, 0x000e0205, 0xbf810000
    )
    val (_, output) = run(gfx906, 2, prologue(gfx906) ++ code)
    assertEquals(output.take(7), Vec(1, 7, 1, 0, 1, 7, 1))
  }

  test("s_setvskip sets VSKIP from the bit its operand names, and a clear lets vector instructions run") {
    val code = assemble(
      gfx906,
      """v_mov_b32 v2, 1
        |s_mov_b32 s41, 0x20
        |s_setvskip s41, 5
        |v_mov_b32 v2, 2
        |s_getreg_b32 s21, hwreg(HW_REG_MODE, 28, 1)
        |s_setvskip s41, 4
        |s_getreg_b32 s22, hwreg(HW_REG_MODE, 28, 1)
        |v_mov_b32 v3, 3
        |v_mov_b32 v4, s21
        |v_mov_b32 v5, s22
        |v_lshlrev_b32 v6, 4, v0
        |global_store_dwordx4 v6, v[2:5], s[14:15]
        |s_endpgm""".stripMargin
    )
    val (_, output) = run(gfx906, 2, prologue(gfx906) ++ code)
    assertEquals(output.take(8), Vec(1, 3, 1, 0, 1, 3, 1, 0))
  }

  test("ds_write_addtid_b32 and ds_read_addtid_b32 address M0[15:0] plus the offset plus four bytes a lane") {
    // s_mov_b32 m0, 0x10; v_add_u32 v1, 100, v0; ds_write_addtid_b32 v1 offset:4
    // v6 = v0 * 4 + 20; ds_read_b32 v2, v6; ds_read_addtid_b32 v3 offset:8; v[2:3] to the output by lane
    val code = words(
      0xbefc0090, 0x680200ff, 0x00000064, 0xd83a0004, 0x00000100, 0x240c0082, 0x680c0c94, 0xbf8cc07f,
      0xd86c0000, 0x02000006, 0xd96c0008, 0x03000000, 0xbf8cc07f, 0x240a0083, 0xdc748000, 0x000e0205,
      0xbf810000
    )
    val (_, output) = run(gfx906, 4, prologue(gfx906) ++ code)
    assertEquals(output.take(8), Vec(100, 101, 101, 102, 102, 103, 103, 0))
  }

  test("CDNA2 DS and VMEM accesses with the ACC bit move their data and results through AGPRs") {
    // v1 = lane * 4, a2 = 10 + lane, a3 = 20 + lane; ds_write2_b32 v1, a2, a3 offset1:16
    // ds_add_rtn_u32 a4, v1, a3; ds_read2_b32 a[6:7], v1 offset1:16; ds_cmpst_rtn_b32 a5, v1, a2, a3
    // v9 = ((lane + 1) & 3) * 4; ds_bpermute_b32 a8, v9, a2; ds_swizzle_b32 a9, v2 offset:swizzle(BROADCAST,4,0)
    // global_atomic_add a10, v1, a2, s[14:15] offset:128 glc; a11 = v2; buffer_atomic_add a11, v1, s[8:11], 0 offen glc
    // a4..a11 to the output at 32 bytes a lane
    val code = words(
      0x24020082, 0x6804008a, 0xd3d94002, 0x18000102, 0x68060094, 0xd3d94003, 0x18000103, 0xda1c1000,
      0x00030201, 0xbf8cc07f, 0xda400000, 0x04000301, 0xbf8cc07f, 0xda6e1000, 0x06000001, 0xda600000,
      0x05030201, 0x68120081, 0x26121283, 0x24121282, 0xda7e0000, 0x08000209, 0xda7a001c, 0x09000002,
      0xdd098080, 0x0a8e0201, 0xd3d9400b, 0x18000102, 0xe1085000, 0x80820b01, 0xbf8c0070, 0xd3d84014,
      0x18000104, 0xd3d84015, 0x18000105, 0xd3d84016, 0x18000106, 0xd3d84017, 0x18000107, 0xd3d84018,
      0x18000108, 0xd3d84019, 0x18000109, 0xd3d8401a, 0x1800010a, 0xd3d8401b, 0x1800010b, 0x243c0085,
      0xdc7c8000, 0x000e141e, 0xdc7c8010, 0x000e181e, 0xbf810000
    )
    val (input, output) = run(gfx90a, 4, prologue(gfx90a, 64L) ++ code)
    (0 until 4).foreach { lane =>
      assertEquals(
        output.slice(lane * 8, lane * 8 + 8),
        Vec(10 + lane, 30 + 2 * lane, 30 + 2 * lane, 20 + lane, 10 + (lane + 1) % 4, 10, 0, 0x1000 + lane),
        s"lane $lane"
      )
      assertEquals(output(32 + lane), 10 + lane)
      assertEquals(input(lane), 0x1000 + 2 * lane + 10)
    }
  }

  private def rejection(target: Target, code: Int*): String =
    VMCompiler.decodeInstruction(Vec.from(code.map(_.toByte)), target).fold(_.raw, other => s"decoded $other")

  test("pre-GFX11 messages: scheduling ones run as waits, MSG_HALT_WAVES halts, the others are rejected") {
    // llvm-mc: s_sendmsg sendmsg(MSG_SAVEWAVE), sendmsg(MSG_SYSMSG, SYSMSG_OP_TTRACE_PC), sendmsg(MSG_HALT_WAVES),
    // sendmsg(MSG_GS_DONE, GS_OP_NOP) and sendmsg(0, 0, 0)
    assertEquals(decoded(gfx906, 0x04, 0x00, 0x90, 0xbf), VMIR.Instruction.Wait)
    assertEquals(decoded(gfx906, 0x4f, 0x00, 0x90, 0xbf), VMIR.Instruction.Wait)
    assertEquals(
      decoded(gfx906, 0x06, 0x00, 0x90, 0xbf),
      VMIR.Instruction.Halt("s_sendmsg MSG_HALT_WAVES halts the waves, which nothing resumes")
    )
    assert(rejection(gfx906, 0x03, 0x00, 0x90, 0xbf).contains("geometry pipeline"))
    assert(rejection(gfx906, 0x00, 0x00, 0x90, 0xbf).contains("names no message"))
    // MSG_HALT_WAVES arrived with GFX9
    assert(rejection(Target("gfx803"), 0x06, 0x00, 0x90, 0xbf).contains("names no message"))
  }

  test("s_endpgm_saved ends the wave; s_endpgm_ordered_ps_done is a pixel shader's") {
    assertEquals(decoded(gfx906, 0x00, 0x00, 0x9b, 0xbf), VMIR.Instruction.End)
    assert(rejection(gfx906, 0x00, 0x00, 0x9e, 0xbf).contains("pixel shader"))
  }

  test("GFX8's v_fma_f16 and v_mad_i16 are GFX9's legacy forms, which decode as such") {
    // llvm-mc gfx803: v_fma_f16 v0, v1, v2, v3 and v_mad_i16 v0, v1, v2, v3; gfx906: v_fma_legacy_f16 v0, v1, v2, v3
    assert(decoded(
      Target("gfx803"),
      0x00,
      0x00,
      0xee,
      0xd1,
      0x01,
      0x05,
      0x0e,
      0x04
    ).toString.contains("FusedMultiplyAddF16"))
    assert(decoded(
      Target("gfx803"),
      0x00,
      0x00,
      0xec,
      0xd1,
      0x01,
      0x05,
      0x0e,
      0x04
    ).toString.contains("MultiplyAddI16"))
    assert(decoded(gfx906, 0x00, 0x00, 0xee, 0xd1, 0x01, 0x05, 0x0e, 0x04).toString.contains("FusedMultiplyAddF16"))
  }

  test("gfx942 v_mov_b64_e64 moves a register pair") {
    // llvm-mc gfx942: v_mov_b64_e64 v[0:1], v[2:3]
    assertEquals(
      decoded(Target("gfx942"), 0x00, 0x00, 0x78, 0xd1, 0x02, 0x01, 0x00, 0x00),
      VMIR.Instruction.VMove64(0, VMIR.InstructionSource.VectorRegister(2))
    )
  }

  test("v_accvgpr_mov_b32 DPP routes its source between lanes of the accumulation registers") {
    val program = assemble(
      gfx90a,
      """v_add_u32 v2, 10, v0
        |v_accvgpr_write_b32 a2, v2
        |v_accvgpr_mov_b32_dpp a3, a2 quad_perm:[1,0,3,2] row_mask:0xf bank_mask:0xf
        |v_accvgpr_read_b32 v3, a3
        |v_lshlrev_b32 v4, 2, v0
        |global_store_dword v4, v3, s[14:15]
        |s_endpgm
        |""".stripMargin
    )
    val (_, output) = run(gfx90a, 4, prologue(gfx90a) ++ program)
    assertEquals(output.take(4), Vec(11, 10, 13, 12))
  }
}
