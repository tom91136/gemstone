package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.explorer.*
import net.kurobako.gemstone.testing.*

class AMDGPUWindowsSuite extends Suite {
  private val kernel = WindowFixtures.kernel

  private val exec    = Set("exec_lo", "exec_hi")
  private val flat    = exec + "m0"
  private val vcc     = Set("vcc_lo", "vcc_hi")
  private val copied  = Set("s0", "s1", "s2", "s3", "s8", "s9", "s10", "v0", "v1", "v2", "v3")
  private val vectors = Set("v1", "v2", "v3", "v4", "v5", "v6", "v7")

  private lazy val image: Vec[Byte]      = WindowFixtures.loop
  private lazy val stateImage: Vec[Byte] = WindowFixtures.state

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
        (9, 10, Set("v3", "v4", "v5", "v6", "v7") ++ flat, Set.empty[String])
      )
    )
  }

  test("a run longer than the size gives every window of that size") {
    assertEquals(windows(image, 2).map(w => (w._1, w._2)), Vec((0, 1), (2, 3), (3, 4), (4, 5), (7, 7), (9, 10)))
  }

  test("the LLVM-compiled vector copy has windows through its loads and its store, between its waits") {
    val address = Set("s2", "s3", "v0", "v1", "v2", "v3")
    val found   = windows(WindowFixtures.vectorCopy, 8)
    assertEquals(
      found.map(w => (w._1, w._2)),
      Vec((0, 3), (5, 12), (6, 13), (7, 14), (8, 15), (9, 16), (10, 17), (19, 19))
    )
    assertEquals(found(1), (5, 12, copied ++ flat, address ++ flat ++ vcc))
    assertEquals(found(2), (6, 13, copied - "s9" + "s4" ++ flat, address ++ flat))
    val (_, _, assembly) = AMDGPUModel.staticCode(WindowFixtures.vectorCopy, kernel).toOption.get
    assert(assembly(14).startsWith("global_load_dword"), assembly(14))
    assert(assembly(18).startsWith("s_waitcnt"), assembly(18))
    assert(assembly(19).startsWith("global_store_dword"), assembly(19))
  }

  test("a compiled gfx1036 kernel has windows through its scalar loads and through its stores") {
    val found = AMDGPUWindows.of(WindowFixtures.compiled, "square_plus_one", WindowFixtures.calibrated, 8).toOption.get
    val spans = found.map(w => (w.first, w.last))
    assertEquals(
      spans,
      Vec(
        (0, 1),
        (3, 3),
        (5, 8),
        (10, 13),
        (14, 14),
        (16, 22),
        (24, 31),
        (25, 32),
        (26, 33),
        (35, 36),
        (37, 37),
        (39, 41),
        (43, 46),
        (47, 47),
        (49, 56)
      )
    )
    val (_, _, assembly) = AMDGPUModel.staticCode(WindowFixtures.compiled, "square_plus_one").toOption.get
    def holds(span: (Int, Int)): Vec[String] =
      assembly.slice(span._1, span._2 + 1).map(_.takeWhile(!_.isWhitespace)).filter(name =>
        name.startsWith("global_") || name.startsWith("s_load")
      )
    assertEquals(holds((16, 22)), Vec("s_load_dwordx8"))
    assertEquals(holds((26, 33)), Vec("global_store_dwordx4", "global_store_dwordx4"))
    assertEquals(holds((49, 56)), Vec("global_store_dword"))
    assert(spans.forall(span => !assembly.slice(span._1, span._2 + 1).exists(_.startsWith("s_waitcnt"))))
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

  test("windows run through the lanes that read and write VCC and stop at a write of EXEC") {
    assertEquals(
      windows(stateImage, 8),
      Vec(
        (
          0,
          5,
          Set("s0", "s2", "s3", "s4", "s5", "v1", "v2", "v3", "v4", "v5") ++ exec,
          Set("s0", "s4", "s5", "v4", "v5")
        ),
        (7, 9, Set("s0", "v4", "v5") ++ exec, Set.empty[String])
      )
    )
  }

  test("a gfx1036 kernel takes its windows from the checked cases of its ISA") {
    val kept  = Set("s5", "s6", "s17", "v3", "v4", "v5", "v6", "exec_lo")
    val found = AMDGPUWindows.of(WindowFixtures.compiled, "ctl_f4_dead", WindowFixtures.calibrated, 8)
    assertEquals(
      found.map(_.map(w => (w.first, w.last, w.liveIn, w.liveOut))),
      Right(Vec((0, 0, kept - "s17", kept), (15, 21, kept, kept + "scc")))
    )
  }
}
