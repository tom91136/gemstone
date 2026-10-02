package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.collection.*

class OpcodePatternSuite extends munit.FunSuite {
  private def compileFence(opcode: String) =
    VMCompiler.compile(s""".version 8.0
.target sm_80
.address_size 64
.visible .entry empty() { $opcode; ret; }
""")

  test("fence opcode patterns accept only the supported orders and scopes") {
    Vec(
      "fence.cta",
      "fence.gpu",
      "fence.sys",
      "fence.cluster",
      "fence.sc.cta",
      "fence.acq_rel.gpu",
      "fence.acquire.sys",
      "fence.release.cluster"
    ).foreach(opcode => assert(compileFence(opcode).isRight, clues(opcode)))

    Vec("fence.relaxed.cta", "fence.sc.cluster.extra", "fence.unknown.gpu")
      .foreach(opcode => assert(compileFence(opcode).isLeft, clues(opcode)))
  }
  test("scalar memory opcode patterns preserve supported order and scope spellings") {
    val loads  = Vec("ld.relaxed.sys", "ld.relaxed.sys.global", "ld.acquire.cta.global", "ld.relaxed.gpu")
    val stores = Vec("st.relaxed.sys.global", "st.relaxed.sys", "st.release.sys.global", "st.relaxed.cta")
    for (opcode <- loads ++ stores) {
      val operands = if (opcode.startsWith("ld.")) "%value,[%address]" else "[%address],%value"
      val result   = VMCompiler.compile(s""".version 8.0
.target sm_80
.address_size 64
.visible .entry scalar() {
.reg .u64 %address;
.reg .u32 %value;
$opcode.u32 $operands;
ret;
}""")
      assert(result.isRight, clues(opcode, result))
    }
  }
}
