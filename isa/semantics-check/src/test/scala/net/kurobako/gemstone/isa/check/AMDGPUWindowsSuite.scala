package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.explorer.*
import net.kurobako.gemstone.isa.amdgpu.*
import net.kurobako.gemstone.testing.*

class AMDGPUWindowsSuite extends Suite {
  private val kernel = "vector_copy"

  private val exec    = Set("exec_lo", "exec_hi")
  private val flat    = exec + "m0"
  private val vcc     = Set("vcc_lo", "vcc_hi")
  private val copied  = Set("s0", "s1", "s2", "s3", "s8", "s9", "s10", "v0", "v1", "v2", "v3")
  private val vectors = Set("v1", "v2", "v3", "v4", "v5", "v6", "v7")

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
    val live             = AMDGPUWindows.liveness(graph, uses)
    assertEquals(live.liveIn(2), vectors ++ flat + "s0")
    assertEquals(live.liveOut(6), vectors ++ flat + "s0")
    assertEquals(live.liveIn(0), vectors ++ flat)
  }

  test("windows end at a rejected instruction and at a block boundary, each with its live sets") {
    assertEquals(
      windows(image, 8),
      Vec(
        (0, 1, vectors ++ flat, vectors ++ flat + "s0"),
        (2, 5, vectors ++ flat + "s0", vectors ++ flat + "s0" + "scc"),
        (7, 7, Set("v1", "v3", "v4", "v5", "v6", "v7") ++ flat, Set("v3", "v4", "v5", "v6", "v7") ++ flat),
        (9, 9, Set("v3", "v4", "v5", "v6", "v7") ++ flat, Set("v5", "v6", "v7") ++ flat)
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
        (3, 3, copied ++ flat, copied ++ flat),
        (5, 11, copied ++ flat, Set("s0", "s2", "s3", "v0", "v1", "v2", "v3") ++ flat),
        (15, 15, Set("s2", "s3", "v0", "v1", "v2", "v3") ++ flat, Set("s2", "v0", "v1", "v2", "v3") ++ flat)
      )
    )
  }

  private val stateSource =
    """s_mov_b64 vcc, s[2:3]
      |v_mul_f32 v3, v1, v2
      |v_cndmask_b32 v4, v3, v1, vcc
      |v_cmp_lt_f32 vcc, v3, v4
      |v_add_co_u32 v5, vcc, v3, v4
      |v_add_u32 v7, v5, v3
      |s_mov_b64 exec, s[4:5]
      |v_add_u32 v6, v5, v4
      |s_cmp_lt_u32 s0, 4
      |s_cselect_b32 s1, 1, 0
      |s_endpgm
      |""".stripMargin

  private lazy val stateImage: Vec[Byte] = {
    val code = AMDGPUModel.assemble(stateSource, Some("gfx906")).map(hex => Hex.parse(hex).toOption.get).toOption.get
    CodeObject.decode(Hex.parse(codeObjects.vectorCopyGfx906).toOption.get)
      .flatMap(_.replaceKernel(kernel.unsafe[NonEmpty[String]], code))
      .toOption.get.image
  }

  test("a window ends with the condition code its branch reads") {
    val found = windows(image, 8)
    assert(found.exists(w => w._2 == 5 && w._4.contains("scc")), found)
  }

  test("implicit and special registers are read and written as the catalogue and the encoding say") {
    val (_, uses, _) = AMDGPUModel.staticCode(stateImage, kernel).toOption.get
    assertEquals(uses(0).writes.toSet, vcc)
    assertEquals(uses(2).reads.toSet, Set("v3", "v1") ++ vcc ++ exec)
    assertEquals(uses(3).writes.toSet, vcc)
    assertEquals(uses(4).writes.toSet, Set("v5") ++ vcc)
    assertEquals(uses(6).writes.toSet, exec)
    assertEquals(uses(8).writes.toSet, Set("scc"))
    assertEquals(uses(9).reads.toSet, Set("scc"))
  }

  test("windows carry VCC to the instruction that reads it and stop at a write of EXEC") {
    assertEquals(
      windows(stateImage, 8),
      Vec(
        (
          0,
          1,
          Set("s0", "s2", "s3", "s4", "s5", "v1", "v2", "v3", "v4", "v5") ++ exec,
          Set("s0", "s4", "s5", "v1", "v3", "v4", "v5") ++ exec ++ vcc
        ),
        (5, 5, Set("s0", "s4", "s5", "v3", "v4", "v5") ++ exec, Set("s0", "s4", "s5", "v4", "v5")),
        (7, 9, Set("s0", "v4", "v5") ++ exec, Set.empty[String])
      )
    )
  }
}
