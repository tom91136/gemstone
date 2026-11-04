package net.kurobako.gemstone.explorer

import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.profile.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import scala.util.Using

/** A launch's profile lands on the listing lines of the instructions that issued. */
class HeatSuite extends Suite {

  private def bytes(text: String): Vec[Byte] = Hex.parse(text).toOption.get

  private val stridedShared: String =
    """.version 8.0
      |.target sm_80
      |.address_size 64
      |.visible .entry kernel(.param .u64 .ptr .global out) {
      |  .reg .b32 %r<4>;
      |  .shared .align 4 .b8 words[4224];
      |  mov.u32 %r0, %tid.x;
      |  shl.b32 %r1, %r0, 7;
      |  mov.u32 %r2, words;
      |  add.s32 %r3, %r2, %r1;
      |  st.shared.u32 [%r3], %r0;
      |  ret;
      |}
      |""".stripMargin

  private def profiled[A](
      language: Language,
      image: Vec[Byte],
      kernel: Option[String],
      groups: Dimensions,
      lanes: Dimensions
  )(check: (ProgramRun.Loaded, ProgramRun.Run, Vec[Option[LineHeat]]) => A): A =
    Using.resource(ProgramRun.load(ProgramListing.Image(language, image, None)).toOption.get) { program =>
      val name = kernel.fold(program.kernels.head)(_.unsafe[NonEmpty[String]])
      val run  = ProgramRun.run(program, name, lanes, groups).toOption.get
      assertEquals(run.failure, None)
      check(program, run, Heat.of(program.listing.toOption.get, run.profile))
    }

  private def ran(program: ProgramRun.Loaded, run: ProgramRun.Run): Set[ProgramLine] = {
    val listing = program.listing.toOption.get
    run.trace.steps.flatMap(step => listing.find(step.location)).toSet
  }

  private def landsOnIssued(program: ProgramRun.Loaded, run: ProgramRun.Run, heats: Vec[Option[LineHeat]]): Unit = {
    val heated = program.lines.zip(heats).collect { case (line, Some(_)) => line }.toSet
    assert(heated.nonEmpty, "no line carries a profile")
    assertEquals(heated, ran(program, run))
    assert(heats.flatten.forall(_.issues > 0L))
  }

  private def lineWith(program: ProgramRun.Loaded, heats: Vec[Option[LineHeat]], text: String): LineHeat =
    program.lines.zip(heats).collectFirst { case (line, Some(heat)) if line.text.contains(text) => heat }.get

  test("a PTX store lands on its line with the sectors of its lanes") {
    profiled(
      Language.PTX,
      Vec.from(Examples.ptx.getBytes(StandardCharsets.UTF_8)),
      Some("kernel"),
      Dimensions(4, 1, 1),
      Dimensions(32, 1, 1)
    ) { (program, run, heats) =>
      landsOnIssued(program, run, heats)
      val store = lineWith(program, heats, "st.global")
      assertEquals(store.global.requests, 4L)
      assert(store.global.sectors > 0L)
      assertEquals(store.excessSectors, 0L)
      assertEquals(store.idleLanes, 0L)
      assertEquals(lineWith(program, heats, "ld.param").global.requests, 0L)
    }
  }

  test("a PTX shared store strided by 32 words conflicts on its line") {
    profiled(
      Language.PTX,
      Vec.from(stridedShared.getBytes(StandardCharsets.UTF_8)),
      Some("kernel"),
      Dimensions(1, 1, 1),
      Dimensions(32, 1, 1)
    ) { (program, run, heats) =>
      landsOnIssued(program, run, heats)
      val store = lineWith(program, heats, "st.shared")
      assertEquals(store.shared.requests, 1L)
      assertEquals(store.shared.wavefronts, 32L)
      assertEquals(store.bankConflicts, 31L)
      assert(store.cyclesLost > 0.0)
      assertEquals(lineWith(program, heats, "shl").bankConflicts, 0L)
    }
  }

  test("the SASS sample carries a profile on the lines that issued") {
    profiled(Language.SASS, bytes(SassExample.cubin), None, Dimensions(4, 1, 1), Dimensions(8, 8, 1)) {
      (program, run, heats) => landsOnIssued(program, run, heats)
    }
  }

  test("the AMDGPU sample carries a profile on the lines that issued") {
    profiled(
      Language.AMDGPU,
      bytes(AmdgpuExample.codeObject),
      Some("probe"),
      Dimensions(4, 1, 1),
      Dimensions(8, 8, 1)
    ) {
      (program, run, heats) =>
        assertEquals(run.architecture, Architecture.gfx1036)
        landsOnIssued(program, run, heats)
    }
  }

  test("the SPIR-V sample carries a profile on the lines that issued") {
    profiled(Language.SPIRV, bytes(SpirvExample.module), Some("mandelbrot"), Dimensions(4, 1, 1), Dimensions(8, 8, 1)) {
      (program, run, heats) =>
        assertEquals(run.architecture, Architecture.sm89)
        landsOnIssued(program, run, heats)
    }
  }

  private val listing = Listing(
    Vec(
      ProgramLine(Some("a"), 0L, "ld", "ld"),
      ProgramLine(Some("a"), 1L, "st", "st"),
      ProgramLine(Some("b"), 0L, "mov", "mov"),
      ProgramLine(Some("b"), 1L, "ret", "ret")
    )
  )

  private def instruction(function: String, address: Long): ProfileStream.Instruction = ProfileStream.Instruction(
    "k".unsafe[NonEmpty[String]],
    Some(function.unsafe[NonEmpty[String]]),
    Some(address.unsafe[Long >= 0]),
    "",
    RegisterUse.empty
  )

  private def counters(issues: Long, active: Long, sectors: Long, ideal: Long): Profile.Counters =
    Profile.Counters(
      issues = issues,
      activeLanes = active,
      subgroupLanes = issues * 32L,
      divergent = 0L,
      reconverging = 0L,
      memory = Map(
        (Space.Global, LaunchObserver.AccessKind.Read) ->
          Profile.Memory.zero.copy(requests = issues, sectors = sectors, idealSectors = ideal)
      )
    )

  private val profile = Profile(
    Architecture.sm89,
    None,
    None,
    Profile.Counters.zero,
    Vec(
      instruction("a", 1L) -> counters(issues = 2L, active = 32L, sectors = 12L, ideal = 8L),
      instruction("b", 1L) -> counters(issues = 4L, active = 128L, sectors = 4L, ideal = 4L),
      instruction("b", 0L) -> Profile.Counters.zero
    )
  )

  test("counters map to the line of their function and address, and other lines carry none") {
    val heats = Heat.of(listing, profile)
    assertEquals(heats.map(_.map(_.issues)), Vec(None, Some(2L), None, Some(4L)))
    assertEquals(heats(1).get.excessSectors, 4L)
    assertEquals(heats(1).get.idleLanes, 32L)
    assertEquals(heats(3).get.excessSectors, 0L)
    assert(heats(1).get.cyclesLost > heats(3).get.cyclesLost)
  }

  test("each metric reads its own count and the maximum is over the lines that carry heat") {
    val heats = Heat.of(listing, profile)
    assertEquals(Heat.maximum(heats, Heat.Metric.Issues), 4.0)
    assertEquals(Heat.maximum(heats, Heat.Metric.IdleLanes), 32.0)
    assertEquals(Heat.maximum(heats, Heat.Metric.ExcessSectors), 4.0)
    assertEquals(Heat.maximum(heats, Heat.Metric.BankConflicts), 0.0)
    assertEquals(Heat.maximum(Vec.empty[Option[LineHeat]], Heat.Metric.Issues), 0.0)
  }

  test("zero is unshaded, any positive value shades, and the maximum takes the darkest shade") {
    assertEquals(Heat.level(value = 0.0, maximum = 10.0), 0)
    assertEquals(Heat.level(value = 5.0, maximum = 0.0), 0)
    assertEquals(Heat.level(value = 0.001, maximum = 10.0), 1)
    assertEquals(Heat.level(value = 2.0, maximum = 10.0), 1)
    assertEquals(Heat.level(value = 2.5, maximum = 10.0), 2)
    assertEquals(Heat.level(value = 8.0, maximum = 10.0), 4)
    assertEquals(Heat.level(value = 8.1, maximum = 10.0), 5)
    assertEquals(Heat.level(value = 10.0, maximum = 10.0), 5)
    assertEquals(Heat.level(value = 20.0, maximum = 10.0), 5)
  }

  test("the legend bounds are the top of each shade") {
    val bounds = Heat.bounds(10.0)
    assertEquals(bounds, Vec(2.0, 4.0, 6.0, 8.0, 10.0))
    bounds.zipWithIndex.foreach((bound, index) => assertEquals(Heat.level(value = bound, maximum = 10.0), index + 1))
  }
}
