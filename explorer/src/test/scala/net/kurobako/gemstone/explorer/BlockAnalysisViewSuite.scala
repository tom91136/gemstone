package net.kurobako.gemstone.explorer

import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.*
import net.kurobako.gemstone.profile.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import scala.util.Using

class BlockAnalysisViewSuite extends Suite {

  private def bytes(text: String): Vec[Byte] = Hex.parse(text).toOption.get

  private val branched: String =
    """s_mov_b32 s0, 0
      |loop:
      |s_add_u32 s0, s0, 1
      |s_cmp_lt_u32 s0, 4
      |s_cbranch_scc1 loop
      |s_cmp_eq_u32 s0, 4
      |s_cbranch_scc1 skip
      |s_add_u32 s1, s1, 1
      |skip:
      |s_endpgm
      |""".stripMargin

  private def branchedImage: Vec[Byte] = {
    val code = AMDGPUModel.assemble(branched, Some("gfx900")).map(bytes(_)).toOption.get
    CodeObject.decode(bytes(AmdgpuExample.codeObject))
      .flatMap(_.replaceKernel(AmdgpuExample.kernel.unsafe[NonEmpty[String]], code))
      .toOption.get.image
  }

  private def modelOf(
      language: Language,
      image: Vec[Byte],
      kernel: String,
      group: Dimensions,
      count: Dimensions,
      architecture: Architecture
  ): BlockAnalysisView.Model =
    Using.resource(ProgramRun.load(ProgramListing.Image(language, image, None)).toOption.get) { program =>
      val run = ProgramRun.run(program, kernel.unsafe[NonEmpty[String]], group, count).toOption.get
      assertEquals(run.failure, None)
      BlockAnalysisView.model(run.stream, architecture)
    }

  private def branchedModel: BlockAnalysisView.Model =
    modelOf(
      Language.AMDGPU,
      branchedImage,
      AmdgpuExample.kernel,
      Dimensions(8, 1, 1),
      Dimensions(1, 1, 1),
      Architecture.gfx1036
    )

  private def regions(model: BlockAnalysisView.Model): Vec[BlockAnalysisView.Region] = model match {
    case BlockAnalysisView.Model.Analysed(regions)   => regions
    case BlockAnalysisView.Model.Unavailable(reason) => fail(reason)
  }

  test("a loop hands its timeline, pressure and bottleneck to the view") {
    val loop  = regions(branchedModel).find(_.kind == BlockAnalysis.Kind.Loop).get
    val timed = loop.state match {
      case BlockAnalysisView.State.Executed(summary) => summary
      case other                                     => fail(s"loop not executed: $other")
    }
    assert(timed.cyclesPerIteration > 0.0)
    assertEquals(timed.timeline.size, loop.members.size)
    assert(timed.pressure.nonEmpty)
    assert(timed.bottleneck.nonEmpty)
    assert(timed.source.detail.nonEmpty)
  }

  test("a block no launch reached is listed as not executed, without a summary") {
    val skipped = regions(branchedModel).filter(_.state == BlockAnalysisView.State.NotExecuted)
    assertEquals(skipped.size, 1)
    assertEquals(skipped.head.kind, BlockAnalysis.Kind.Block)
    assert(skipped.head.members.nonEmpty)
  }

  test("a profile without control flow graphs is unavailable") {
    val model = modelOf(
      Language.PTX,
      Vec.from(Examples.ptx.getBytes(StandardCharsets.UTF_8)),
      "kernel",
      Dimensions(32, 1, 1),
      Dimensions(4, 1, 1),
      Architecture.sm89
    )
    model match {
      case BlockAnalysisView.Model.Unavailable(reason) => assert(reason.contains("no control flow graphs"), reason)
      case other                                       => fail(other.toString)
    }
  }
}
