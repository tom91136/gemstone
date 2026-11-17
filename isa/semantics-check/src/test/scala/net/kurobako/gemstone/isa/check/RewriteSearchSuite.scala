package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.explorer.*
import net.kurobako.gemstone.flow.Window
import net.kurobako.gemstone.semantics.render.Bitwuzla
import net.kurobako.gemstone.testing.*

class RewriteSearchSuite extends Suite {
  private val target = "gfx906"
  private val kernel = WindowFixtures.kernel

  private val search   = RewriteSearch.Config(seed = 1L, budget = 3, temperature = 1.0, states = 2, limit = 8)
  private val sentence = RewriteSearch.Config(seed = 7L, budget = 6, temperature = 1.0, states = 4, limit = 4)

  private def installed(): Unit =
    assume(Bitwuzla.executable.isDefined, "no bitwuzla binary on GEMSTONE_BITWUZLA_EXE or PATH")

  private def windows(image: Vec[Byte], size: Int): Vec[Window] =
    AMDGPUWindows.of(image, kernel, target, size).toOption.get

  private def assembly(image: Vec[Byte]): Vec[String] = AMDGPUModel.staticCode(image, kernel).toOption.get._3

  private def searchOf(image: Vec[Byte], window: Window, config: RewriteSearch.Config = search) =
    RewriteSearch.amdgpu(image, kernel, target, window, config) match {
      case Right(report) => report
      case Left(reason)  => fail(s"the window ${window.first}..${window.last} is not searchable: $reason")
    }

  private val dead: Vec[Byte] = WindowFixtures.patched(
    """s_mov_b32 s0, 0
      |v_add_u32 v2, v1, v1
      |v_add_u32 v3, v1, v4
      |v_mov_b32 v5, v3
      |s_endpgm
      |""".stripMargin
  )

  private val overwritten: Vec[Byte] = WindowFixtures.patched(
    """v_add_u32 v2, v1, v4
      |v_mov_b32 v5, v2
      |s_endpgm
      |""".stripMargin
  )

  private lazy val deadWindow: Window =
    windows(dead, 3).find(_.first == 0).getOrElse(fail("no window at 0"))

  private lazy val overwrittenWindow: Window =
    windows(overwritten, 1).find(window => window.first == 0 && window.liveOut.contains("v2"))
      .getOrElse(fail("no window at 0 with v2 live out"))

  test("a fixed seed and budget prove a rewrite of a dead result with fewer model cycles") {
    installed()
    val text   = assembly(dead)
    val report = searchOf(dead, deadWindow)
    assert(report.rewrites.nonEmpty, report)
    report.rewrites.foreach { rewrite =>
      assert(rewrite.after.cycles < rewrite.before.cycles, rewrite)
      assert(rewrite.evidence.instructions.nonEmpty, rewrite)
    }
    val removed = Vec(text(0), text(2))
    assert(report.rewrites.exists(_.rewrite == removed), s"no rewrite deletes ${text(1)}: ${report.rewrites}")
  }

  test("a cheaper candidate that changes a live-out value is never reported") {
    installed()
    val report = searchOf(overwritten, overwrittenWindow)
    assertEquals(report.rewrites.size, 0, report)
    assert(report.wrong > 0, report)
  }

  test("proof rejects a candidate that changes a live-out value") {
    installed()
    val candidate = Vec("v_sub_u32 v2, v1, v4", "v_mov_b32 v5, v2")
    RewriteSearch.prove(overwritten, kernel, target, overwrittenWindow, candidate) match {
      case Right(Proof.Result.Rejected(_, _)) => ()
      case other                              => fail(s"expected a refutation, got $other")
    }
  }

  test("the same seed and budget give the same report") {
    installed()
    def rows(report: RewriteSearch.Report): Vec[(Vec[String], Vec[String], Double, String)] =
      report.rewrites.map(rewrite => (rewrite.original, rewrite.rewrite, rewrite.after.cycles, rewrite.source.kind))
    val first  = searchOf(dead, deadWindow, sentence)
    val second = searchOf(dead, deadWindow, sentence)
    assertEquals(first.evaluated, second.evaluated)
    assertEquals(first.candidates, second.candidates)
    assertEquals(first.wrong, second.wrong)
    assertEquals(rows(first), rows(second))
  }

  private def fixture(name: String, image: Vec[Byte], size: Int): Unit =
    test(s"the $name fixture reports only proved rewrites with fewer model cycles") {
      installed()
      windows(image, size).foreach { window =>
        RewriteSearch.amdgpu(image, kernel, target, window, RewriteSearch.Config.default) match {
          case Right(report) =>
            report.rewrites.foreach { rewrite =>
              assert(rewrite.after.cycles < rewrite.before.cycles, rewrite)
              assert(rewrite.evidence.instructions.nonEmpty, rewrite)
            }
            println(
              s"[rewrite] $name ${window.first}..${window.last} " +
                s"live-out=${window.liveOut.toVec.sorted.mkString(",")} candidates=${report.candidates} " +
                s"wrong=${report.wrong} rewrites=${report.rewrites.size}"
            )
            report.rewrites.foreach { rewrite =>
              println(
                f"[rewrite]   ${rewrite.after.cycles}%.2f from ${rewrite.before.cycles}%.2f " +
                  s"(${rewrite.source.kind}) ${rewrite.rewrite.mkString("; ")}"
              )
            }
          case Left(reason) =>
            println(s"[rewrite] $name ${window.first}..${window.last} skipped: $reason")
        }
      }
    }

  fixture("loop", WindowFixtures.loop, 8)
  fixture("state", WindowFixtures.state, 8)
  fixture("copy", WindowFixtures.vectorCopy, 8)
}
