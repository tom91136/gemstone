package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.explorer.*
import net.kurobako.gemstone.flow.Window
import net.kurobako.gemstone.profile.PipelineSimulator
import net.kurobako.gemstone.testing.*

class AMDGPUAnchorSuite extends Suite {

  private val target  = WindowFixtures.calibrated
  private val kernel  = "window"
  private val oneWave = AMDGPUBlockCost.Launch.Stated(32, 65536L, Some(PipelineSimulator.Placement(Vec(1, 0, 0, 0))))

  private case class Code(image: Vec[Byte], name: String = kernel) {
    val (_, _, text)      = AMDGPUModel.staticCode(image, name).fold(fail(_), identity)
    val offsets: Vec[Int] = AMDGPUModel.staticTiming(image, name).fold(fail(_), identity).map(_.offset)
    private val cost      = AMDGPUBlockCost.of(image, name, target, oneWave).fold(fail(_), identity)

    def at(prefix: String): Int = {
      val index = text.indexWhere(_.startsWith(prefix))
      assert(index >= 0, s"no $prefix in $text")
      index
    }

    private def grown(first: Int, nops: Int): Vec[String] =
      Vec(text(first)) ++ Vec.fill(nops)("s_nop 0") ++ Vec(text(first + 1))

    def grow(first: Int, nops: Int = 1): Either[String, (Vec[Int], Vec[String])] =
      cost.laidOut(Window(0, first, first + 1, Set.empty, Set.empty), grown(first, nops))

    def laid(first: Int): (Vec[Int], Vec[String]) = grow(first).fold(fail(_), identity)

    def assertRefused(first: Int, nops: Int = 1): Unit = {
      val result = grow(first, nops)
      assert(result.left.exists(_.contains("relative to the program counter")), result)
    }
  }

  private def code(source: String): Code = Code(WindowFixtures.assembled(source.stripMargin))

  private def literal(text: String): Long = {
    val value = text.split("[, ]+").last.stripPrefix("lit(").stripSuffix(")")
    if (value.startsWith("0x")) java.lang.Long.parseLong(value.drop(2), 16) else value.toLong
  }

  private def reached(offsets: Vec[Int], text: Vec[String]): Long = {
    val at = text.indexWhere(_.startsWith("s_getpc_b64"))
    offsets(at) + 4L + ((literal(text(at + 2)) << 32) | (literal(text(at + 1)) & 0xffffffffL))
  }

  private val outside =
    """v_mov_b32 v1, 0
      |v_add_nc_u32 v2, v1, v1
      |s_getpc_b64 s[0:1]
      |s_add_u32 s0, s0, 0xfffff000
      |s_addc_u32 s1, s1, -1
      |v_add_nc_u32 v5, v1, v4
      |v_add_nc_u32 v6, v1, v4
      |s_endpgm
      |""".stripMargin

  private def data = Code(WindowFixtures.assembled(outside))

  test("an address outside the kernel survives a rewrite before or after its anchor") {
    val original = data
    Vec(0, original.at("v_add_nc_u32 v5")).foreach { first =>
      val (offsets, text) = original.laid(first)
      assertEquals(reached(offsets, text), reached(original.offsets, original.text), text)
    }
  }

  test("an address outside the kernel survives the move of a kernel that outgrows its place") {
    val original = Code(WindowFixtures.assembled(outside, "ctl_f4_tail"), "ctl_f4_tail")
    Vec(0, original.at("v_add_nc_u32 v5")).foreach { first =>
      val (offsets, text) = original.laid(first)
      assertNotEquals(offsets.head, original.offsets.head)
      assertEquals(reached(offsets, text), reached(original.offsets, original.text), text)
    }
  }

  test("the labels of a subvector loop keep reaching their targets when its body grows") {
    val original = code(
      """v_mov_b32 v1, 0
        |v_add_nc_u32 v2, v1, v1
        |s_subvector_loop_begin s2, 3
        |v_add_nc_u32 v5, v1, v4
        |v_add_nc_u32 v6, v1, v4
        |s_subvector_loop_end s2, -3
        |v_add_nc_u32 v7, v1, v4
        |s_endpgm
        |"""
    )
    def targets(offsets: Vec[Int], text: Vec[String]): Vec[String] =
      text.zipWithIndex.collect { case (line, at) if line.startsWith("s_subvector_loop") => at }.map { at =>
        val reached = offsets(at) + 4 + 4 * literal(text(at)).toShort
        text(offsets.indexWhere(_ == reached))
      }
    val (offsets, text) = original.laid(original.at("v_add_nc_u32 v5"))
    assertEquals(targets(offsets, text), targets(original.offsets, original.text), text)
  }

  test("a window holding part of an anchor sequence is refused") {
    val original = data
    original.assertRefused(original.at("s_add_u32"))
  }

  test("a rewrite that reverses the sign of an address displacement is refused") {
    val original = code(
      """v_mov_b32 v1, 0
        |v_add_nc_u32 v2, v1, v1
        |s_getpc_b64 s[0:1]
        |s_add_u32 s0, s0, 16
        |s_addc_u32 s1, s1, 0
        |s_endpgm
        |"""
    )
    original.assertRefused(0, 8)
  }

  private def transfers(link: String) = code(
    s"""v_mov_b32 v1, 0
       |v_add_nc_u32 v2, v1, v1
       |s_getpc_b64 s[0:1]
       |s_add_u32 s0, s0, 0xfffff000
       |s_addc_u32 s1, s1, -1
       |$link
       |v_add_nc_u32 v5, v1, v4
       |v_add_nc_u32 v6, v1, v4
       |s_endpgm
       |"""
  )

  test("an address in the kernel is refused") {
    val original = code(
      """v_mov_b32 v1, 0
        |v_add_nc_u32 v2, v1, v1
        |s_getpc_b64 s[0:1]
        |s_add_u32 s0, s0, 16
        |s_addc_u32 s1, s1, 0
        |v_add_nc_u32 v5, v1, v4
        |v_add_nc_u32 v6, v1, v4
        |s_endpgm
        |"""
    )
    original.assertRefused(0)
  }

  test("a call through a register is refused") {
    transfers("s_swappc_b64 s[30:31], s[0:1]").assertRefused(0)
  }

  test("a jump through a register is refused") {
    transfers("s_setpc_b64 s[0:1]").assertRefused(0)
  }

  test("a relative call is refused") {
    transfers("s_call_b64 s[30:31], 1").assertRefused(0)
  }

  test("a jump to an address the kernel does not form refuses a size-changing rewrite") {
    val original = code(
      """v_mov_b32 v1, 0
        |v_add_nc_u32 v2, v1, v1
        |s_setpc_b64 s[2:3]
        |s_endpgm
        |"""
    )
    original.assertRefused(0)
  }
}
