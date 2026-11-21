package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.explorer.*
import net.kurobako.gemstone.flow.ControlFlowGraph
import net.kurobako.gemstone.flow.Window
import net.kurobako.gemstone.flow.Windows
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
    assert(at(0x60) && at(0x510), "a predicate written beside a constant-bank read is admitted")
    assert(!at(0x30), "a uniform register is written through the warp")
    assert(at(0x50) && at(0xb0) && at(0x530), "loads and stores of global memory are admitted")
    assert(!at(0x70), "a convergence barrier has no rendering")
    assert(!at(0x2c0), "a branch writes no register")
    assert(at(0x200) && at(0x210) && at(0x220), "the loop's arithmetic is admitted")
    assert(at(0x260), "the comparison is admitted")
  }

  test("the kernel's windows run through its constant-bank reads and its global loads and stores") {
    val (graph, uses, instructions) = coded(loopKernel, Vec(24))
    val windows                     = SASSWindows.of(graph, uses, instructions, "sm_89", 8)
    val spans                       = windows.map(window => (loop(window.first), loop(window.last)))
    val gained                      = Vec((0x00, 0x00), (0x40, 0x60), (0x90, 0xc0), (0x510, 0x530))
    assertEquals(
      spans.filterNot(gained.contains),
      Vec(
        (0x20, 0x20),
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
    assertEquals(spans.size, 23)
    assertEquals(
      spans.groupMapReduce((first, last) => (last - first) / 16 + 1)(_ => 1)(_ + _),
      Map(1 -> 7, 2 -> 5, 3 -> 5, 4 -> 2, 5 -> 2, 6 -> 1, 8 -> 1)
    )
    val loaded = windows.find(window => loop(window.first) == 0x40).getOrElse(fail("no window at 0x40"))
    assert(loaded.liveOut.contains("R5"), "what the load reads is live to the store at the kernel's end")
    val stored = windows.find(window => loop(window.last) == 0x530).getOrElse(fail("no window ends at 0x530"))
    assert(stored.liveIn.contains("R5"), stored.liveIn)
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
        (0, 2, Set("R0", "R1", "R3"), Set("R2")),
        (1, 3, Set("R0", "R2", "R3"), Set.empty[String])
      )
    )
    assert(SASSForms.of("sm_89", instructions(2).operation).isRight, "the guarded write is admitted")
    assert(windows.last.liveIn.contains("R2"), "the guarded write kept R2 live")
    assertEquals(uses(2).reads.toSet, Set("P0", "R2", "R3"))
  }

  test("a compiled division's guarded corrections sit inside its windows") {
    val kernel                      = Harness.load(89, Fixture.listing("divideSm89"), "divide_probe")
    val (graph, uses, instructions) = coded(kernel, Vec(8, 4, 4))
    val kept                        = SASSWindows.admitted("sm_89", instructions)
    val guarded                     = Vec.from(instructions.indices).filter(instructions(_).guard.nonEmpty)
    assertEquals(guarded.map(loop), Vec(0x120, 0x130, 0x180, 0x1b0, 0x1c0))
    assert(
      guarded.forall(kept),
      guarded.filterNot(kept).map(index => SASSForms.of("sm_89", instructions(index).operation))
    )
    def covered(windows: Vec[Window]): Int = windows.flatMap(window => window.first to window.last).distinct.size
    val without                            = Windows.of(
      graph,
      SASSWindows.liveness(graph, uses),
      index => !kept(index) || instructions(index).guard.nonEmpty,
      8
    )
    assertEquals((without.size, covered(without)), (11, 26))
    val windows = SASSWindows.of(graph, uses, instructions, "sm_89", 8)
    assertEquals((windows.size, covered(windows), instructions.size), (23, 31, 34))
    assertEquals(
      windows.map(window => (loop(window.first), loop(window.last))).filter((first, _) => first >= 0x100),
      Vec.tabulate(10)(index => (0x100 + 16 * index, 0x170 + 16 * index))
    )
    val corrected = windows.find(window => loop(window.first) == 0x110).getOrElse(fail("no window at 0x110"))
    assertEquals(corrected.liveIn, Set("R0", "R2", "R3", "R5", "R7"))
    assert(corrected.liveOut.contains("R3") && corrected.liveOut.contains("P1"), corrected.liveOut)
  }

  test("the cubin path gives the same windows as its decoded instructions") {
    val (graph, uses, instructions) = SASSModel.staticCode(Fixture.copyAddCubin, "copy_add").toOption.get
    assertEquals(
      SASSWindows.of(Fixture.copyAddCubin, "copy_add", "sm_89").toOption.get,
      SASSWindows.of(graph, uses, instructions, "sm_89", 8)
    )
  }
}
