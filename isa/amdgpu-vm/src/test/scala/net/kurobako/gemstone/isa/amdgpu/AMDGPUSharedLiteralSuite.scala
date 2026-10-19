package net.kurobako.gemstone.isa.amdgpu

import laneRuns.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.semantics.render.*

/** S_FMAAK_F32 and S_FMAMK_F32 carry one literal dword, which fills K and a literal source alike; bytes from ROCm 7.1.1
  * `llvm-mc -triple=amdgcn -mcpu=gfx1201 -show-encoding` for `s_fmaak_f32 s0, 0x40490fdb, s1, 0x40490fdb` and
  * `s_fmamk_f32 s0, 0x40490fdb, 0x40490fdb, s1`.
  */
class AMDGPUSharedLiteralSuite extends munit.FunSuite {
  private val target = Target("gfx1201")

  test("one literal fills K and the literal source") {
    def result(code: Int*): Long =
      scalarResult(target, Vec(0L, 0x3f800000L), program(target, octets(code*)))
    assertEquals(result(0xff, 0x01, 0x80, 0xa2, 0xdb, 0x0f, 0x49, 0x40), 0x40c90fdbL)
    assertEquals(result(0xff, 0x01, 0x00, 0xa3, 0xdb, 0x0f, 0x49, 0x40), 0x412de9e7L)
  }

  test("definitions read the shared literal as the one operand it binds") {
    val names                                 = Map("s0" -> "SDST", "s1" -> "SSRC1", "0x1234" -> "LITERAL")
    def defined(text: String): Option[String] =
      InstructionSemantics.describe(target, text, names, Vec("LITERAL", "SSRC1")).toOption.map(ScalaBackend.show(_))
    assertEquals(
      defined("s_fmaak_f32 s0, 0x1234, s1, 0x1234"),
      Some(
        "def s_fmaak_f32(LITERAL: UInt[32], SSRC1: UInt[32]): (SDST: UInt[32]) =\n" +
          "  (SDST = Floats.fma(LITERAL.asFloat, SSRC1.asFloat, LITERAL.asFloat).bits)"
      )
    )
    assert(defined("s_fmamk_f32 s0, 0x1234, 0x1234, s1").exists(!_.contains("4660")))
  }
}
