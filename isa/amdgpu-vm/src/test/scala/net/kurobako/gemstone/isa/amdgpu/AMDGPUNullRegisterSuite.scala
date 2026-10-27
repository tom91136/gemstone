package net.kurobako.gemstone.isa.amdgpu

import laneRuns.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.render.*

/** M0 and the null register, which RDNA3 onwards encodes as s125 and s124 and RDNA1 and RDNA2 as s124 and s125; each
  * with its LLVM bytes from `llvm-mc -show-encoding`: `s_mov_b32 m0, 17`, `s_mov_b64 null, s[0:1]`,
  * `s_mov_b32 null, s1`, `s_mov_b32 s2, m0`, `s_mov_b64 s[4:5], null` and `s_add_u32 s3, null, m0`.
  */
class AMDGPUNullRegisterSuite extends munit.FunSuite {
  private val rdna2 = Vec(
    Vec(0x91, 0x03, 0xfc, 0xbe),
    Vec(0x00, 0x04, 0xfd, 0xbe),
    Vec(0x01, 0x03, 0xfd, 0xbe),
    Vec(0x7c, 0x03, 0x82, 0xbe),
    Vec(0x7d, 0x04, 0x84, 0xbe),
    Vec(0x7d, 0x7c, 0x03, 0x80)
  )
  private val rdna3 = Vec(
    Vec(0x91, 0x00, 0xfd, 0xbe),
    Vec(0x00, 0x01, 0xfc, 0xbe),
    Vec(0x01, 0x00, 0xfc, 0xbe),
    Vec(0x7d, 0x00, 0x82, 0xbe),
    Vec(0x7c, 0x01, 0x84, 0xbe),
    Vec(0x7c, 0x7d, 0x03, 0x80)
  )

  for ((target, code) <- Seq((Target("gfx1030"), rdna2), (Target("gfx1100"), rdna3), (Target("gfx1201"), rdna3)))
    test(s"$target writes to the null register are dropped, and it reads zero beside M0") {
      def result(register: Int): Long = scalarResult(
        target,
        Vec(0x0bL, 0x0cL, 0L, 0L, -1L, -1L),
        program(target, octets(code.flatten*)),
        register.unsafe[VMIR.ScalarRegisterIndex]
      )
      assertEquals(result(2), 17L)
      assertEquals(result(3), 17L)
      assertEquals(result(4), 0L)
      assertEquals(result(5), 0L)
    }

  for (target <- Seq(Target("gfx1030"), Target("gfx1100"), Target("gfx1201")))
    test(s"$target a MODE read into the null register is dropped and the register reads zero") {
      val register = VMIR.nullRegister(target.isa).getOrElse(fail("no null register"))
      val result   = scalarResult(
        target,
        Vec.empty,
        Vec(
          VMIR.Instruction.SSetMode(0, 4, VMIR.InstructionSource.Constant(0xfL)),
          VMIR.Instruction.SGetMode(register, 0, 4)
        ),
        register
      )
      assertEquals(result, 0L)
    }

  for ((target, m0, absent) <- Seq((Target("gfx1030"), "s124", "s125"), (Target("gfx1100"), "s125", "s124")))
    test(s"$target definitions name M0 and read the null register as zero") {
      val defined =
        InstructionSemantics.describe(target, "s_add_u32 s3, null, m0", Map.empty).toOption.map(ScalaBackend.show(_))
      assertEquals(
        defined,
        Some(
          "def s_add_u32(m0: UInt[32]): (s3: UInt[32], scc: Bool) =\n  (\n    s3 = UInt[32](0) + m0,\n" +
            "    scc = UInt[32](0).to[UInt[64]] + m0.to[UInt[64]] > 0xffffffffL\n  )"
        )
      )
      assert(!defined.exists(_.contains(m0)) && !defined.exists(_.contains(absent)), defined)
    }
}
