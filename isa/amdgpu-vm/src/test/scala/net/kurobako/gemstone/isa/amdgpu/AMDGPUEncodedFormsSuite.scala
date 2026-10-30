package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import fixtures.*
import laneRuns.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*

/** Encodings of operations the VM runs in other forms: VOP3 and DPP forms of the dot products, the packed FMA and the
  * mixed-precision FMA, bfloat16 dot products, packed 16-bit integer conversions, the DX9 multiply-adds, M0-relative
  * pair moves, variable lane permutes, accumulator moves, and wave control by S_CALL_B64, S_SETKILL, S_SETHALT and
  * MSG_INTERRUPT. hipcc emits the plain dot forms (`-O3 --offload-arch=gfx90a`: `v_dot2c_f32_f16_e32`,
  * `v_dot4c_i32_i8_e32`), the variable permutes for `__builtin_amdgcn_permlane16_var` on gfx1201 and MSG_INTERRUPT for
  * printf's hostcall (`s_sendmsg sendmsg(MSG_INTERRUPT)`); the others come from LLVM's assembler. Bytes from ROCm 7.1.1
  * `llvm-mc -triple=amdgcn -mcpu=<target> -show-encoding`, except `v_accvgpr_mov_b32_e64 a5, a2` and
  * `v_pk_fmac_f16_e64 v5, v2, v3`, which LLVM does not accept and the catalogue encodes.
  */
class AMDGPUEncodedFormsSuite extends Suite {
  private val gfx906  = Target("gfx906")
  private val gfx90a  = Target("gfx90a")
  private val gfx950  = Target("gfx950")
  private val gfx1030 = Target("gfx1030")
  private val gfx1100 = Target("gfx1100")

  /** Each lane's `result` after `code`, lane `i` holding `inputs(i)` in v2 onwards. */
  private def run(target: Target, code: Vec[Int], inputs: Vec[Vec[Long]], result: Int = 5): Vec[Long] =
    encoded(target, code, inputs, result, wave32 = target.isa.isRDNA)

  private def one(target: Target, code: Vec[Int], inputs: Long*): Long = run(target, code, Vec(Vec.from(inputs))).head

  private val halves = 0x40003c00L
  private val single = 0x3f800000L

  test("gfx90a VOP3 dot products accumulate into the destination") {
    assertEquals(
      one(gfx90a, Vec(0x05, 0x00, 0x37, 0xd1, 0x02, 0x07, 0x02, 0x00), halves, 0x44004200L, 0L, single),
      0x41400000L
    )
    assertEquals(
      one(gfx90a, Vec(0x05, 0x00, 0x39, 0xd1, 0x02, 0x07, 0x02, 0x00), 0x01020304L, 0x01010101L, 0L, 5L),
      15L
    )
    assertEquals(one(gfx90a, Vec(0x05, 0x00, 0x38, 0xd1, 0x02, 0x07, 0x02, 0x00), 0x20003L, 0x40005L, 0L, 1L), 24L)
    assertEquals(
      one(gfx90a, Vec(0x05, 0x00, 0x3a, 0xd1, 0x02, 0x07, 0x02, 0x00), 0x11111111L, 0x22222222L, 0L, 0L),
      16L
    )
  }

  test("v_dot2c_f32_f16_dpp routes source 0") {
    val lanes = Vec(Vec(halves, 0x3c003c00L, 0L, 0L), Vec(0x3c003c00L, 0x3c003c00L, 0L, 0L))
    assertEquals(run(gfx90a, Vec(0xfa, 0x06, 0x0a, 0x6e, 0x02, 0xb1, 0x00, 0xff), lanes), Vec(0x40000000L, 0x40400000L))
    assertEquals(
      run(gfx1030, Vec(0xfa, 0x06, 0x0a, 0x04, 0x02, 0xb1, 0x00, 0xff), lanes),
      Vec(0x40000000L, 0x40400000L)
    )
  }

  test("bfloat16 dot products widen their halves exactly") {
    val (a, b) = (0x40003f80L, 0x40403f80L)
    assertEquals(one(gfx950, Vec(0x02, 0x07, 0x0a, 0x2c), a, b, 0L, 0L), 0x40e00000L)
    assertEquals(one(gfx950, Vec(0x05, 0x40, 0x9a, 0xd3, 0x02, 0x07, 0x12, 0x1c), a, b, single), 0x41000000L)
    assertEquals(one(gfx1100, Vec(0x05, 0x40, 0x1a, 0xcc, 0x02, 0x07, 0x12, 0x1c), a, b, single), 0x41000000L)
  }

  test("gfx1100 packs binary32 values into saturated 16-bit integers") {
    assertEquals(one(gfx1100, Vec(0x05, 0x00, 0x06, 0xd7, 0x02, 0x07, 0x02, 0x00), 0xc71c4000L, 0x40fccccdL), 0x78000L)
    assertEquals(
      one(gfx1100, Vec(0x05, 0x00, 0x07, 0xd7, 0x02, 0x07, 0x02, 0x00), 0xc0a00000L, 0x4788b800L),
      0xffff0000L
    )
  }

  test("gfx1100 VOP3P DPP routes the mixed-precision FMA's and the dot product's source 0") {
    val mixed = Vec(Vec(0x40000000L, single, single), Vec(0x40400000L, single, single))
    assertEquals(
      run(gfx1100, Vec(0x05, 0x00, 0x20, 0xcc, 0xfa, 0x06, 0x12, 0x04, 0x02, 0xb1, 0x00, 0xff), mixed),
      Vec(0x40800000L, 0x40400000L)
    )
    // v_fma_mix_f32_e64_dpp v0, v1, v2, v3 dpp8:[7,6,5,4,3,2,1,0]
    assert(VMCompiler.decodeInstruction(
      octets(0x00, 0x00, 0x20, 0xcc, 0xe9, 0x04, 0x0e, 0x04, 0x01, 0x77, 0x39, 0x05),
      gfx1100
    ).exists {
      case Instruction.VDPP(
            operation = Instruction.VFmaMixF32(srcA = InstructionSource.DPP),
            control = DPPControl.DPP8(_)
          ) =>
        true
      case _ => false
    })
    val dot = Vec(Vec(halves, 0x3c003c00L, 0L), Vec(0x3c003c00L, 0x3c003c00L, 0L))
    assertEquals(
      run(gfx1100, Vec(0x05, 0x40, 0x13, 0xcc, 0xfa, 0x06, 0x12, 0x1c, 0x02, 0xb1, 0x00, 0xff), dot),
      Vec(0x40000000L, 0x40400000L)
    )
  }

  test("gfx1201 v_permlane16_var_b32 and v_permlanex16_var_b32 select each lane's source by its own VGPR") {
    val gfx1201                         = Target("gfx1201")
    val lanes                           = Vec.tabulate(32)(lane => Vec(lane * 10L, 15L - lane % 16, 0L, 0L))
    def permuted(code: Int*): Vec[Long] = run(gfx1201, Vec.from(code), lanes)
    assertEquals(
      permuted(0x05, 0x00, 0x0f, 0xd7, 0x02, 0x07, 0x02, 0x00),
      Vec.tabulate(32)(lane => ((lane | 15) - lane % 16) * 10L)
    )
    assertEquals(
      permuted(0x05, 0x00, 0x10, 0xd7, 0x02, 0x07, 0x02, 0x00),
      Vec.tabulate(32)(lane => (((lane ^ 16) | 15) - lane % 16) * 10L)
    )
  }

  test("the DX9 multiply-adds take a zero factor's product as +0") {
    val infinity = 0x7f800000L
    assertEquals(one(gfx1030, Vec(0x05, 0x00, 0x40, 0xd5, 0x02, 0x07, 0x12, 0x04), 0L, infinity, single), single)
    assertEquals(one(gfx1030, Vec(0x02, 0x07, 0x0a, 0x0c), 0L, 0x7fc00000L, 0L, 0x40000000L), 0x40000000L)
    assertEquals(one(gfx906, Vec(0x05, 0x00, 0xc0, 0xd1, 0x02, 0x07, 0x12, 0x04), 0L, infinity, single), single)
  }

  test("gfx906 v_exp_legacy_f32 writes +0 whatever its source") {
    assertEquals(one(gfx906, Vec(0x02, 0x97, 0x0a, 0x7e), 0x40400000L), 0L)
  }

  test("gfx1030 v_movrelsd_2_b32 and v_swaprel_b32 offset source and destination by M0's two fields") {
    def moved(code: Int*): (Long, Long) = {
      val operations =
        Instruction.SMove(m0Register(gfx1030.isa), InstructionSource.Constant(0x10001L)) +:
          program(gfx1030, octets(code*))
      def at(register: Int): Long =
        laneResults(gfx1030, Vec(Vec(0L, 33L, 0L, 0L, 66L)), operations, register.unsafe[VectorRegisterIndex]).head
      (at(3), at(6))
    }
    assertEquals(moved(0x02, 0x91, 0x0a, 0x7e), (33L, 33L))
    assertEquals(moved(0x02, 0xd1, 0x0a, 0x7e), (66L, 33L))
  }

  test("gfx90a moves between accumulation registers and accumulates packed halves in VOP3") {
    val write = octets(0x02, 0x40, 0xd9, 0xd3, 0x02, 0x01, 0x00, 0x18)
    val read  = octets(0x05, 0x40, 0xd8, 0xd3, 0x05, 0x01, 0x00, 0x18)
    val moved = program(gfx90a, write ++ octets(0x05, 0x00, 0x92, 0xd1, 0x02, 0x01, 0x00, 0x00) ++ read)
    assertEquals(laneResults(gfx90a, Vec(Vec(77L)), moved, 5, wave32 = false).head, 77L)
    assertEquals(
      one(gfx90a, Vec(0x05, 0x00, 0x3c, 0xd1, 0x02, 0x07, 0x02, 0x00), halves, halves, 0L, 0x3c003c00L),
      0x45004000L
    )
  }

  /** `code` and then `s_endpgm` on one lane of gfx1030, with v5 stored to the lane's memory afterwards. */
  private def control(code: Int*): (Either[Fault, Unit], Long) = {
    val memory     = RegionMemory()
    val allocation = AllocationId(1L)
    memory.allocate(allocation, 4)
    val address    = memory.address(allocation, 0L).fold(error => fail(error.toString), _.raw)
    val operations = Vec(
      Instruction.SMove64(6, InstructionSource.Constant(address)),
      Instruction.VMove(5, InstructionSource.Constant(0L)),
      Instruction.VMove(1, InstructionSource.Constant(0L))
    ) ++ program(gfx1030, octets(code*)).map {
      case Instruction.Call(dest, target) => Instruction.Call(dest, target + 3)
      case other                          => other
    } ++ Vec(
      Instruction.Store(5, MemoryAddress.Global(1, Some(6), 0), 4, false),
      Instruction.End
    )
    val outcome = runIR(operations, memory, codeKernel(target = gfx1030))
    (outcome, memory.getInt(allocation, 0.unsafe[Int >= 0]).toLong & 0xffffffffL)
  }

  test("s_call_b64 saves the return address and branches; s_setkill ends the wave") {
    // s_getpc_b64 s[12:13]; s_call_b64 s[10:11], 1; v_mov_b32 v5, 7; v_mov_b32 v6, 9; s_sub_u32 s10, s10, s12;
    // v_mov_b32 v5, s10
    val called = control(
      0x00, 0x1f, 0x8c, 0xbe, 0x01, 0x00, 0x0a, 0xbb, 0x87, 0x02, 0x0a, 0x7e, 0x89, 0x02, 0x0c, 0x7e, 0x0a, 0x0c, 0x8a,
      0x80, 0x0a, 0x02, 0x0a, 0x7e
    )
    assertEquals(called, (Right(()), 4L))
    assertEquals(control(0x01, 0x00, 0x8b, 0xbf, 0x87, 0x02, 0x0a, 0x7e), (Right(()), 0L))
  }

  test("s_sethalt 1 and MSG_INTERRUPT fault, as nothing resumes or answers the wave") {
    assert(control(0x01, 0x00, 0x8d, 0xbf)._1.left.exists(_.toString.contains("halts the wave")))
    assert(control(0x01, 0x00, 0x90, 0xbf)._1.left.exists(_.toString.contains("MSG_INTERRUPT")))
  }
}
