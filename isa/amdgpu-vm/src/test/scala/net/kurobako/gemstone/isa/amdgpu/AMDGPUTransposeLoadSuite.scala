package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import laneRuns.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

/** The transposing loads hipcc emits for `__builtin_amdgcn_ds_read_tr16_b64_v4i16`, `ds_read_tr8_b64_v2i32`,
  * `ds_read_tr4_b64_v2i32` and `ds_read_tr6_b96_v3i32` on gfx950 and `__builtin_amdgcn_global_load_tr_b128_v8i16` and
  * `global_load_tr_b64_v2i32` on wave32 GFX12 (ROCm 7.1.1 `hipcc -O2 --offload-arch=gfx1201`: `global_load_tr_b128
  * v[1:4], v2, s[6:7]`, `global_load_tr_b64 v[5:6], v1, s[4:5]`). The expected lane maps restate CK's and Triton's for
  * CDNA4 (`load_tile_transpose.hpp`, `TargetFeatures::queryLDSTransLoadParams`) and TensileLite's for GFX12. Bytes from
  * LLVM 22 `llvm-mc -triple=amdgcn -mcpu=<target> -show-encoding`.
  */
class AMDGPUTransposeLoadSuite extends munit.FunSuite {
  private val gfx950  = Target("gfx950")
  private val gfx1201 = Target("gfx1201")

  /** Byte `index` lane `lane` reads. */
  private def byte(lane: Int, index: Int): Long = ((37 * lane + 11 * index + 5) & 0xff).toLong

  private def dword(lane: Int, index: Int): Long = (0 until 4).map(offset =>
    byte(lane, 4 * index + offset) << (8 * offset)
  ).sum

  /** Bits `bits` from bit `at` of the bytes lane `lane` reads. */
  private def field(lane: Int, at: Int, bits: Int): Long = {
    val window = (0 until 4).map(offset => byte(lane, at / 8 + offset) << (8 * offset)).sum
    (window >>> (at % 8)) & ((1L << bits) - 1L)
  }

  @FunctionalInterface
  private trait Origin { def apply(lane: Int, element: Int): (Int, Int) }

  /** Each lane's dwords of `elements` elements of `bits`, element `e` from where `origin(lane, e)` says. */
  private def expected(lanes: Int, bits: Int, elements: Int)(origin: Origin): Vec[Vec[Long]] =
    Vec.tabulate(lanes) { lane =>
      val packed = (0 until elements).map { element =>
        val (source, index) = origin(lane, element)
        BigInt(field(source, index * bits, bits)) << (element * bits)
      }.sum
      Vec.tabulate((bits * elements + 31) / 32)(word => ((packed >> (32 * word)) & 0xffffffffL).toLong)
    }

  /** The dwords from v8 each lane of a gfx950 wave holds after it writes its bytes to LDS at 16 times its index and
    * `read` reads them back.
    */
  private def local(write: Vec[Int], read: Vec[Int], dwords: Int): Vec[Vec[Long]] = {
    val inputs = Vec.tabulate(64)(lane => Vec(16L * lane, 0L, dword(lane, 0), dword(lane, 1), dword(lane, 2)))
    val code   = program(gfx950, octets((write ++ read)*))
    val words  = Vec.tabulate(dwords)(index =>
      laneResults(
        gfx950,
        inputs,
        code,
        (8 + index).unsafe[VectorRegisterIndex],
        wave32 = false,
        groupSegmentSize = 1024
      )
    )
    Vec.tabulate(64)(lane => words.map(_(lane)))
  }

  private val pair    = Vec(0x00, 0x00, 0x9a, 0xd8, 0x02, 0x04, 0x00, 0x00)
  private val triple  = Vec(0x00, 0x00, 0xbc, 0xd9, 0x02, 0x04, 0x00, 0x00)
  private val group16 = (lane: Int) => lane & ~15

  test("DS_READ_B64_TR_B16 gives each lane of 16 the halves of its column from four rows") {
    assertEquals(
      local(pair, Vec(0x00, 0x00, 0xc6, 0xd9, 0x02, 0x00, 0x00, 0x08), 2),
      expected(lanes = 64, bits = 16, elements = 4)((lane, e) => (group16(lane) + 4 * e + ((lane & 15) >> 2), lane & 3))
    )
  }

  test("DS_READ_B64_TR_B8 gives each lane of 16 the bytes of its column from eight rows") {
    assertEquals(
      local(pair, Vec(0x00, 0x00, 0xc4, 0xd9, 0x02, 0x00, 0x00, 0x08), 2),
      expected(lanes = 64, bits = 8, elements = 8)((lane, e) => (group16(lane) + 2 * e + ((lane & 15) >> 3), lane & 7))
    )
  }

  test("DS_READ_B64_TR_B4 and DS_READ_B96_TR_B6 transpose 16 lanes of 16 elements") {
    assertEquals(
      local(pair, Vec(0x00, 0x00, 0xc0, 0xd9, 0x02, 0x00, 0x00, 0x08), 2),
      expected(lanes = 64, bits = 4, elements = 16)((lane, e) => (group16(lane) + e, lane & 15))
    )
    assertEquals(
      local(triple, Vec(0x00, 0x00, 0xc2, 0xd9, 0x02, 0x00, 0x00, 0x08), 3),
      expected(lanes = 64, bits = 6, elements = 16)((lane, e) => (group16(lane) + e, lane & 15))
    )
  }

  /** The dwords from v6 each lane of a gfx1201 wave32 holds after `code` reads its bytes from its memory. */
  private def global(code: Vec[Int], dwords: Int): Vec[Vec[Long]] = {
    val inputs = Vec.tabulate(32)(lane => Vec(0L, dword(lane, 0), dword(lane, 1), dword(lane, 2), dword(lane, 3)))
    val words  = Vec.tabulate(dwords)(index =>
      laneResults(gfx1201, inputs, program(gfx1201, octets(code*)), (6 + index).unsafe[VectorRegisterIndex])
    )
    Vec.tabulate(32)(lane => words.map(_(lane)))
  }

  test("GLOBAL_LOAD_TR_B128 transposes the halves of each eight lanes and _B64 the bytes of two blocks of four rows") {
    assertEquals(
      global(Vec(0x06, 0xc0, 0x15, 0xee, 0x06, 0x00, 0x00, 0x00, 0x01, 0x04, 0x00, 0x00), 4),
      expected(lanes = 32, bits = 16, elements = 8)((lane, e) => ((lane & ~7) + e, lane & 7))
    )
    assertEquals(
      global(Vec(0x06, 0x00, 0x16, 0xee, 0x06, 0x00, 0x00, 0x00, 0x01, 0x04, 0x00, 0x00), 2),
      expected(lanes = 32, bits = 8, elements = 8)((lane, e) =>
        (group16(lane) + 8 * (e >> 2) + 4 * ((lane >> 3) & 1) + (e & 3), lane & 7)
      )
    )
  }

  test("GLOBAL_LOAD_LDS_DWORDX4 stores each lane's 16 bytes into the LDS from M0 and the offset at its index") {
    val code = Vec(
      0x80, 0x00, 0xfc, 0xbe, 0x08, 0x80, 0xf4, 0xdd, 0x01, 0x00, 0x06, 0x00, 0x00, 0x00, 0xfe, 0xd9, 0x02, 0x00, 0x00,
      0x08
    )
    val inputs = Vec.tabulate(64)(lane => Vec(8L + 16 * lane, 0L) ++ Vec.tabulate(4)(dword(lane, _)))
    val words  = Vec.tabulate(4)(index =>
      laneResults(
        gfx950,
        inputs,
        program(gfx950, octets(code*)),
        (8 + index).unsafe[VectorRegisterIndex],
        wave32 = false,
        groupSegmentSize = 2048
      )
    )
    assertEquals(
      Vec.tabulate(64)(lane => words.map(_(lane))),
      Vec.tabulate(64)(lane => Vec.tabulate(4)(dword(lane, _)))
    )
  }
}
