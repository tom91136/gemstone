package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.explorer.*
import net.kurobako.gemstone.flow.ControlFlowGraph
import net.kurobako.gemstone.isa.sass.DecodedInstruction
import net.kurobako.gemstone.isa.sass.Harness
import net.kurobako.gemstone.isa.sass.LoadedProgram
import net.kurobako.gemstone.isa.sass.fixtures.Fixture
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.RegisterUse
import net.kurobako.gemstone.vm.Workgroups

class SASSWindowsSuite extends Suite {
  private lazy val loopKernel: Harness.Kernel = Harness.load(89, Fixture.listing("divergentSm89"), "divergent")

  private def coded(kernel: Harness.Kernel, parameters: Vec[Int])
      : (ControlFlowGraph, Vec[RegisterUse], Vec[DecodedInstruction]) = {
    val image   = kernel.module(parameters)
    val program = LoadedProgram(image, Map(kernel.name -> kernel.functions), Workgroups.Sequential, Harness.text)
    program.staticCode(kernel.name).fold(error => fail(error.message), identity)
  }

  private def loop(index: Int): Int = index * 16

  test("a window inside the loop carries the registers the rest of the iteration and exit read") {
    val (graph, uses, instructions) = coded(loopKernel, Vec(24))
    val windows                     = SASSWindows.of(graph, uses, instructions, "sm_89", 3)
    val found                       = windows.find(window => loop(window.first) == 0x200 && loop(window.last) == 0x220)
      .getOrElse(fail(windows.map(window => (loop(window.first), loop(window.last))).mkString))
    assertEquals(found.liveIn, Set("R0", "R2", "R3", "R4", "R5", "R6", "UR4", "P0"))
    assertEquals(found.liveOut, Set("R0", "R2", "R3", "R4", "R5", "R6", "R8", "UR4", "P0"))
  }

  test("the loop's run ends at its branch, which has no definition to prove") {
    val (graph, uses, instructions) = coded(loopKernel, Vec(24))
    val windows                     = SASSWindows.of(graph, uses, instructions, "sm_89", 3)
    val inLoop = windows.filter(window => loop(window.first) >= 0x200 && loop(window.last) <= 0x2b0)
    assertEquals(inLoop.map(window => (loop(window.first), loop(window.last))).last, (0x290, 0x2b0))
    assert(windows.forall(window => window.first != 44 && window.last != 44), "no window holds the branch")
  }

  test("constant-bank reads are admitted, and what has no form of its own ends a window") {
    val (_, _, instructions)      = coded(loopKernel, Vec(24))
    val kept                      = SASSWindows.admitted("sm_89", instructions)
    def at(address: Int): Boolean = kept(address / 16)
    assertEquals(instructions(0x40 / 16).mnemonic.raw, "IMAD", "the wide product reads the constant bank")
    assert(at(0x00) && at(0x40) && at(0x90) && at(0xa0) && at(0x520), "constant-bank reads are admitted")
    assert(!at(0x60) && !at(0x510), "no checked case writes a predicate beside a constant-bank read")
    assert(!at(0x30), "a uniform register is written through the warp")
    assert(!at(0x50), "a load is not a plain memory access")
    assert(!at(0x70), "a convergence barrier has no rendering")
    assert(!at(0x2c0), "a branch writes no register")
    assert(at(0x200) && at(0x210) && at(0x220), "the loop's arithmetic is admitted")
    assert(at(0x260), "the comparison is admitted")
  }

  test("the kernel gains the windows that hold its constant-bank reads and loses none") {
    val (graph, uses, instructions) = coded(loopKernel, Vec(24))
    val windows                     = SASSWindows.of(graph, uses, instructions, "sm_89", 8)
    val spans                       = windows.map(window => (loop(window.first), loop(window.last)))
    val gained                      = Vec((0x00, 0x00), (0x40, 0x40), (0x90, 0xa0), (0x520, 0x520))
    assertEquals(
      spans.filterNot(gained.contains),
      Vec(
        (0x20, 0x20),
        (0xc0, 0xc0),
        (0xe0, 0x130),
        (0x150, 0x150),
        (0x170, 0x190),
        (0x1b0, 0x1d0),
        (0x1f0, 0x1f0),
        (0x200, 0x220),
        (0x240, 0x2b0),
        (0x2d0, 0x2e0),
        (0x300, 0x310),
        (0x330, 0x370),
        (0x380, 0x380),
        (0x3a0, 0x3b0),
        (0x3d0, 0x400),
        (0x420, 0x420),
        (0x430, 0x430),
        (0x450, 0x490),
        (0x4b0, 0x4c0),
        (0x4e0, 0x4f0)
      )
    )
    assertEquals(spans.filter(gained.contains), gained)
    val pair = windows.find(window => loop(window.first) == 0x90).getOrElse(fail("no window at 0x90"))
    assertEquals(pair.liveOut.intersect(Set("R2", "R3")), Set("R2", "R3"))
  }

  test("a predicated write does not end the live range of what it may leave unchanged") {
    val kernel = Harness.program(
      89,
      """MOV R2, R1
        |ISETP.GT.U32.AND P0, PT, R0, 0x10, PT
        |@P0 MOV R2, R3
        |IADD3 R4, R2, 0x1, RZ
        |EXIT""".stripMargin
    )
    val (graph, uses, instructions) = coded(kernel, Vec.empty)
    val windows                     = SASSWindows.of(graph, uses, instructions, "sm_89", 3)
    assertEquals(
      windows.map(window => (window.first, window.last, window.liveIn, window.liveOut)),
      Vec(
        (0, 1, Set("R0", "R1", "R3"), Set("R2", "R3", "P0")),
        (3, 3, Set("R2"), Set.empty[String])
      )
    )
    assert(SASSForms.of("sm_89", instructions(2).operation).left.exists(_.contains("has no rendering")))
    assert(windows.head.liveOut.contains("R2"), "the guarded write kept R2 live")
    assertEquals(uses(2).reads.toSet, Set("P0", "R2", "R3"))
  }

  test("the cubin path gives the same windows as its decoded instructions") {
    val (graph, uses, instructions) = SASSModel.staticCode(Fixture.copyAddCubin, "copy_add").toOption.get
    assertEquals(
      SASSWindows.of(Fixture.copyAddCubin, "copy_add", "sm_89").toOption.get,
      SASSWindows.of(graph, uses, instructions, "sm_89", 8)
    )
  }
}
