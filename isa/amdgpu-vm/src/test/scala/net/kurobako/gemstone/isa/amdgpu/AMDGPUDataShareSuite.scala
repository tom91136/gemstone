package net.kurobako.gemstone.isa.amdgpu

import EncodingConstants.*
import VMIR.*
import fixtures.*
import laneRuns.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.bits.*

/** The rarer LDS operations, the append counters `__builtin_amdgcn_ds_append` and `_ds_consume` emit (`s_mov_b32 m0, 4`
  * then `ds_consume v2`), and GDS and GWS as HSA queues provide them: no GDS segment, and GWS resources only for
  * cooperative dispatches.
  */
class AMDGPUDataShareSuite extends Suite {
  import InstructionSource.*

  /** One lane with v2 to v7 from `inputs`: LDS address 0 starts as v4 and 4 and 8 as v6 and v7, or 0 to 8 as v[4:5]
    * when `wide`; then `code`, after which v8 holds LDS address 0, v9 address 4 and v10 address 8.
    */
  private def local(target: Target, code: Vec[Int], inputs: Vec[Long], result: Int, wide: Boolean = false)(using
      munit.Location
  ): Long = laneResults(
    target,
    Vec(inputs),
    (if (wide) Vec(Instruction.Store(4, MemoryAddress.Local(3, 0), 8, high = false))
     else
       Vec(
         Instruction.Store(4, MemoryAddress.Local(3, 0), 4, high = false),
         Instruction.Store(6, MemoryAddress.Local(3, 4), 4, high = false),
         Instruction.Store(7, MemoryAddress.Local(3, 8), 4, high = false)
       )) ++ program(target, octets(code*)) ++ Vec(
      Instruction.Load(8, MemoryAddress.Local(3, 0), 4, signed = false, LoadPlacement.Full),
      Instruction.Load(9, MemoryAddress.Local(3, 4), 4, signed = false, LoadPlacement.Full),
      Instruction.Load(10, MemoryAddress.Local(3, 8), 4, signed = false, LoadPlacement.Full)
    ),
    result.unsafe[VectorRegisterIndex],
    groupSegmentSize = 64
  ).head

  /** The old value `code` returns in v4 and LDS address 0 afterwards. */
  private def exchanged(target: Target, code: Vec[Int], inputs: Long*)(using munit.Location): (Long, Long) =
    (local(target, code, Vec.from(inputs), 4), local(target, code, Vec.from(inputs), 8))

  // /opt/rocm/llvm/bin/llvm-mc -show-encoding; v3 holds LDS address 0
  private val gcn = Seq(
    Target("gfx906") -> Map(
      "rsub"    -> Vec(0x00, 0x00, 0x44, 0xd8, 0x03, 0x02, 0x00, 0x04),
      "rsubNo"  -> Vec(0x00, 0x00, 0x04, 0xd8, 0x03, 0x02, 0x00, 0x00),
      "mskor"   -> Vec(0x00, 0x00, 0x58, 0xd8, 0x03, 0x02, 0x05, 0x04),
      "wrap"    -> Vec(0x00, 0x00, 0x68, 0xd8, 0x03, 0x02, 0x05, 0x04),
      "cmpst"   -> Vec(0x00, 0x00, 0x62, 0xd8, 0x03, 0x02, 0x05, 0x04),
      "condx"   -> Vec(0x00, 0x00, 0xfc, 0xd8, 0x03, 0x06, 0x00, 0x04),
      "wrxchg2" -> Vec(0x01, 0x02, 0x5c, 0xd8, 0x03, 0x02, 0x05, 0x04)
    ),
    Target("gfx1030") -> Map(
      "rsub"    -> Vec(0x00, 0x00, 0x88, 0xd8, 0x03, 0x02, 0x00, 0x04),
      "rsubNo"  -> Vec(0x00, 0x00, 0x08, 0xd8, 0x03, 0x02, 0x00, 0x00),
      "mskor"   -> Vec(0x00, 0x00, 0xb0, 0xd8, 0x03, 0x02, 0x05, 0x04),
      "wrap"    -> Vec(0x00, 0x00, 0xd0, 0xd8, 0x03, 0x02, 0x05, 0x04),
      "cmpst"   -> Vec(0x00, 0x00, 0xc4, 0xd8, 0x03, 0x02, 0x05, 0x04),
      "condx"   -> Vec(0x00, 0x00, 0xf8, 0xd9, 0x03, 0x06, 0x00, 0x04),
      "wrxchg2" -> Vec(0x01, 0x02, 0xb8, 0xd8, 0x03, 0x02, 0x05, 0x04)
    )
  )

  for ((target, code) <- gcn) {
    test(s"$target ds_rsub_rtn_u32 and ds_rsub_u32 subtract memory from the data") {
      assertEquals(exchanged(target, code("rsub"), 25L, 0L, 10L), (10L, 15L))
      assertEquals(local(target, code("rsubNo"), Vec(25L, 0L, 10L), 8), 15L)
    }
    test(s"$target ds_mskor_rtn_b32 clears the mask's bits and sets the data's") {
      assertEquals(exchanged(target, code("mskor"), 0x0000ffffL, 0L, 0xff00ff00L, 0x1234L), (0xff00ff00L, 0xff001234L))
    }
    test(s"$target ds_wrap_rtn_b32 subtracts the first data at or above it and adds the second below it") {
      assertEquals(exchanged(target, code("wrap"), 5L, 0L, 7L, 100L), (7L, 2L))
      assertEquals(exchanged(target, code("wrap"), 5L, 0L, 3L, 100L), (3L, 103L))
    }
    test(s"$target ds_cmpst_rtn_f32 compares as floats, so -0 matches +0 and NaN matches nothing") {
      assertEquals(exchanged(target, code("cmpst"), single(-0.0f), 0L, single(0.0f), single(1.5f)), (0L, single(1.5f)))
      assertEquals(
        exchanged(target, code("cmpst"), 0x7fc00000L, 0L, 0x7fc00000L, single(1.5f)),
        (0x7fc00000L, 0x7fc00000L)
      )
    }
    test(s"$target ds_condxchg32_rtn_b64 writes the dwords whose data has bit 31 set") {
      val inputs = Vec(0L, 0L, 0x11111111L, 0x22222222L, 0x80000005L, 9L)
      assertEquals(local(target, code("condx"), inputs, 4, wide = true), 0x11111111L)
      assertEquals(local(target, code("condx"), inputs, 8, wide = true), 5L)
      assertEquals(local(target, code("condx"), inputs, 9, wide = true), 0x22222222L)
    }
    test(s"$target ds_wrxchg2_rtn_b32 exchanges two dwords at their offsets") {
      val inputs = Vec(0x11L, 0L, 0L, 0x55L, 0x66L, 0x77L)
      assertEquals(local(target, code("wrxchg2"), inputs, 4), 0x66L)
      assertEquals(local(target, code("wrxchg2"), inputs, 5), 0x77L)
      assertEquals(local(target, code("wrxchg2"), inputs, 9), 0x11L)
      assertEquals(local(target, code("wrxchg2"), inputs, 10), 0x55L)
    }
  }

  test("gfx1100 ds_cmpstore_rtn_f32 takes the new value first and ds_storexchg_2addr_rtn_b32 exchanges two dwords") {
    val target = Target("gfx1100")
    assertEquals(
      exchanged(target, Vec(0x00, 0x00, 0xc4, 0xd8, 0x03, 0x02, 0x05, 0x04), single(1.5f), 0L, 0L, single(-0.0f)),
      (0L, single(1.5f))
    )
    val inputs = Vec(0x11L, 0L, 0L, 0x55L, 0x66L, 0x77L)
    assertEquals(local(target, Vec(0x01, 0x02, 0xb8, 0xd8, 0x03, 0x02, 0x05, 0x04), inputs, 4), 0x66L)
  }

  test("gfx1201 ds_sub_clamp_rtn_u32, ds_cond_sub_rtn_u32 and the num float extrema") {
    val target = Target("gfx1201")
    val clamp  = Vec(0x00, 0x00, 0xa4, 0xda, 0x03, 0x02, 0x00, 0x04)
    val cond   = Vec(0x00, 0x00, 0xa0, 0xda, 0x03, 0x02, 0x00, 0x04)
    assertEquals(exchanged(target, clamp, 5L, 0L, 3L), (3L, 0L))
    assertEquals(exchanged(target, clamp, 5L, 0L, 8L), (8L, 3L))
    assertEquals(exchanged(target, cond, 5L, 0L, 3L), (3L, 3L))
    assertEquals(exchanged(target, cond, 5L, 0L, 8L), (8L, 3L))
    val minimum = Vec(0x00, 0x00, 0xc8, 0xd8, 0x03, 0x02, 0x00, 0x04)
    assertEquals(exchanged(target, minimum, single(1.0f), 0L, 0x7fc00000L), (0x7fc00000L, single(1.0f)))
    val maximum = Vec(0x00, 0x00, 0x4c, 0xd8, 0x03, 0x02, 0x00, 0x00)
    assertEquals(local(target, maximum, Vec(single(2.0f), 0L, single(-1.0f)), 8), single(2.0f))
  }

  /** Four lanes run `code` with M0 at 4 and LDS address 16 holding 40; each lane's v4 and then LDS address 16. */
  private def counted(target: Target, code: Vec[Int])(using munit.Location): (Vec[Long], Long) = {
    def run(result: Int): Vec[Long] =
      laneResults(
        target,
        Vec.fill(4)(Vec(40L, 0L, 0xdeadL)),
        Vec(
          Instruction.SMove(Fixed.M0, Constant(4L)),
          Instruction.Store(2, MemoryAddress.Local(3, 16), 4, high = false)
        ) ++ program(target, octets(code*)) :+
          Instruction.Load(5, MemoryAddress.Local(3, 16), 4, signed = false, LoadPlacement.Full),
        result.unsafe[VectorRegisterIndex],
        groupSegmentSize = 64
      )
    (run(4), run(5).head)
  }

  for (
    (target, append, consume) <- Seq(
      (
        Target("gfx906"),
        Vec(0x10, 0x00, 0x7c, 0xd9, 0x00, 0x00, 0x00, 0x04),
        Vec(0x10, 0x00, 0x7a, 0xd9, 0x00, 0x00, 0x00, 0x04)
      ),
      (
        Target("gfx1030"),
        Vec(0x10, 0x00, 0xf8, 0xd8, 0x00, 0x00, 0x00, 0x04),
        Vec(0x10, 0x00, 0xf4, 0xd8, 0x00, 0x00, 0x00, 0x04)
      ),
      (
        Target("gfx1100"),
        Vec(0x10, 0x00, 0xf8, 0xd8, 0x00, 0x00, 0x00, 0x04),
        Vec(0x10, 0x00, 0xf4, 0xd8, 0x00, 0x00, 0x00, 0x04)
      )
    )
  )
    // a gfx1036 moves the dword at the offset whatever M0 holds (s_mov_b32 m0 with 0, 4, 0x40000 and 0xffff, then
    // ds_append v0 offset:8, all count LDS address 8)
    test(s"$target ds_append and ds_consume count the active lanes at the offset and return the old count") {
      assertEquals(counted(target, append), (Vec.fill(4)(40L), 44L))
      assertEquals(counted(target, consume), (Vec.fill(4)(40L), 36L))
    }

  for (
    (target, code) <- Seq(
      Target("gfx906") -> Map(
        "append"  -> Vec(0x00, 0x00, 0x7d, 0xd9, 0x00, 0x00, 0x00, 0x04),
        "ordered" -> Vec(0x04, 0x03, 0x7f, 0xd9, 0x02, 0x00, 0x00, 0x04),
        "add"     -> Vec(0x00, 0x00, 0x01, 0xd8, 0x03, 0x02, 0x00, 0x00),
        "read"    -> Vec(0x00, 0x00, 0x6d, 0xd8, 0x03, 0x00, 0x00, 0x04),
        "addRtn"  -> Vec(0x00, 0x00, 0x41, 0xd8, 0x03, 0x02, 0x00, 0x04),
        "barrier" -> Vec(0x00, 0x00, 0x3b, 0xd9, 0x02, 0x00, 0x00, 0x00),
        "init"    -> Vec(0x00, 0x00, 0x33, 0xd9, 0x02, 0x00, 0x00, 0x00)
      ),
      Target("gfx1030") -> Map(
        "append"  -> Vec(0x00, 0x00, 0xfa, 0xd8, 0x00, 0x00, 0x00, 0x04),
        "ordered" -> Vec(0x04, 0x03, 0xfe, 0xd8, 0x02, 0x00, 0x00, 0x04),
        "add"     -> Vec(0x00, 0x00, 0x02, 0xd8, 0x03, 0x02, 0x00, 0x00),
        "read"    -> Vec(0x00, 0x00, 0xda, 0xd8, 0x03, 0x00, 0x00, 0x04),
        "addRtn"  -> Vec(0x00, 0x00, 0x82, 0xd8, 0x03, 0x02, 0x00, 0x04),
        "barrier" -> Vec(0x00, 0x00, 0x76, 0xd8, 0x02, 0x00, 0x00, 0x00),
        "init"    -> Vec(0x00, 0x00, 0x66, 0xd8, 0x02, 0x00, 0x00, 0x00)
      )
    )
  ) {
    test(s"$target GDS forms reach an empty segment: reads and returns give zero and writes are dropped") {
      val empty = Vec(25L, 0L, 0xdeadL)
      Seq("append", "ordered", "read", "addRtn").foreach(name =>
        assertEquals(local(target, code(name), empty, 4), 0L, name)
      )
      assertEquals(local(target, code("add"), Vec(25L, 0L, 10L), 8), 10L)
    }
    test(s"$target GWS operations fault outside a cooperative dispatch") {
      Seq("barrier", "init").foreach { name =>
        val memory = RegionMemory()
        val result = runIR(program(target, octets(code(name)*)) :+ Instruction.End, memory, codeKernel(target = target))
        assert(
          result.left.exists(_.message.contains("GWS")),
          s"$name: $result"
        )
      }
    }
  }

  // ds_bpermute_b32 v4, v2, v3 from llvm-mc; the CDNA3 and RDNA3 pseudocode reads 0 from a source lane EXEC disables
  for (
    (target, code) <- Seq(
      Target("gfx906")  -> Vec(0x00, 0x00, 0x7e, 0xd8, 0x02, 0x03, 0x00, 0x04),
      Target("gfx942")  -> Vec(0x00, 0x00, 0x7e, 0xd8, 0x02, 0x03, 0x00, 0x04),
      Target("gfx1030") -> Vec(0x00, 0x00, 0xcc, 0xda, 0x02, 0x03, 0x00, 0x04),
      Target("gfx1100") -> Vec(0x00, 0x00, 0xcc, 0xda, 0x02, 0x03, 0x00, 0x04)
    )
  )
    test(s"$target ds_bpermute_b32 reads 0 from a source lane EXEC disables") {
      val inputs = Vec.tabulate(4)(lane => Vec(((lane + 1) % 4) * 4L, 10L + lane))
      assertEquals(
        laneResults(target, inputs, program(target, octets(code*)), 4, exec = 0xbL),
        Vec(11L, 0L, 0L, 10L)
      )
    }
}
