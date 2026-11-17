package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.explorer.*
import net.kurobako.gemstone.isa.amdgpu.*
import net.kurobako.gemstone.testing.*

class AMDGPUWindowsSuite extends Suite {
  private val kernel = "vector_copy"

  private val source =
    """s_mov_b32 s0, 0
      |v_mov_b32 v1, 0
      |loop:
      |v_add_f32 v1, v1, v2
      |v_mul_f32 v3, v1, v1
      |s_add_u32 s0, s0, 1
      |s_cmp_lt_u32 s0, 4
      |s_cbranch_scc1 loop
      |v_add_u32 v4, v3, v1
      |ds_add_u32 v4, v3
      |v_sub_u32 v5, v4, v3
      |global_store_dword v[6:7], v5, off
      |s_endpgm
      |""".stripMargin

  private lazy val image: Vec[Byte] = {
    val code = AMDGPUModel.assemble(source, Some("gfx906")).map(hex => Hex.parse(hex).toOption.get).toOption.get
    CodeObject.decode(Hex.parse(codeObjects.vectorCopyGfx906).toOption.get)
      .flatMap(_.replaceKernel(kernel.unsafe[NonEmpty[String]], code))
      .toOption.get.image
  }

  private def windows(code: Vec[Byte], size: Int): Vec[(Int, Int, Set[String], Set[String])] =
    AMDGPUWindows.of(code, kernel, "gfx906", size).toOption.get.map(w => (w.first, w.last, w.liveIn, w.liveOut))

  test("a counted loop keeps its accumulator, its address pair and its counter live around the back edge") {
    val (graph, uses, _) = AMDGPUModel.staticCode(image, kernel).toOption.get
    val live             = net.kurobako.gemstone.flow.Liveness.of(graph, uses)
    assertEquals(live.liveIn(2), Set("s0", "v1", "v2", "v6", "v7"))
    assertEquals(live.liveOut(6), Set("s0", "v1", "v2", "v3", "v6", "v7"))
    assertEquals(live.liveIn(0), Set("v2", "v6", "v7"))
  }

  test("windows end at a rejected instruction and at a block boundary, each with its live sets") {
    assertEquals(
      windows(image, 8),
      Vec(
        (0, 1, Set("v2", "v6", "v7"), Set("s0", "v1", "v2", "v6", "v7")),
        (2, 5, Set("s0", "v1", "v2", "v6", "v7"), Set("s0", "v1", "v2", "v3", "v6", "v7")),
        (7, 7, Set("v1", "v3", "v6", "v7"), Set("v3", "v4", "v6", "v7")),
        (9, 9, Set("v3", "v4", "v6", "v7"), Set("v5", "v6", "v7"))
      )
    )
  }

  test("a run longer than the size gives every window of that size") {
    assertEquals(windows(image, 2).map(w => (w._1, w._2)), Vec((0, 1), (2, 3), (3, 4), (4, 5), (7, 7), (9, 9)))
  }

  test("the LLVM-compiled vector copy has windows between its loads, carries and waits") {
    assertEquals(
      windows(Hex.parse(codeObjects.vectorCopyGfx906).toOption.get, 8),
      Vec(
        (
          3,
          3,
          Set("s0", "s1", "s2", "s3", "s8", "s9", "s10", "v0"),
          Set("s0", "s1", "s2", "s3", "s8", "s9", "s10", "v0")
        ),
        (5, 11, Set("s0", "s1", "s2", "s3", "s8", "s9", "s10", "v0"), Set("s0", "s2", "s3", "v0", "v1", "v3")),
        (15, 15, Set("s2", "s3", "v0", "v1", "v2"), Set("s2", "v0", "v1", "v2", "v3"))
      )
    )
  }
}
