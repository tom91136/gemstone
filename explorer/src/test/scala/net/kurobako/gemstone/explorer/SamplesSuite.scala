package net.kurobako.gemstone.explorer

import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import scala.util.Using

/** The examples the program page starts from must load, list and run on the real VM. */
class SamplesSuite extends munit.FunSuite {

  private def bytes(text: String): Vec[Byte] = Hex.parse(text).toOption.get

  private def verify(
      language: Language,
      image: Vec[Byte],
      target: Option[String],
      kernel: Option[String],
      groups: Dimensions,
      lanes: Dimensions
  ): Unit = Using.resource(ProgramRun.load(ProgramListing.Image(language, image, target)).toOption.get) { program =>
    val name = kernel.fold(program.kernels.head)(_.unsafe[NonEmpty[String]])
    val run  = ProgramRun.run(program, name, lanes, groups).toOption.get
    assert(program.lines.nonEmpty, "the sample has no listing")
    assert(run.trace.issues > 0, "the sample issued nothing")
    assertEquals(run.failure, None, s"the sample stopped: ${run.failure.map(_.description.raw)}")
  }

  test("the PTX sample runs") {
    verify(
      Language.PTX,
      Vec.from(Examples.ptx.getBytes(StandardCharsets.UTF_8)),
      None,
      Some("kernel"),
      Dimensions(4, 1, 1),
      Dimensions(8, 1, 1)
    )
  }

  test("the Mandelbrot sample renders an image") {
    val image = Vec.from(Examples.mandelbrot.getBytes(StandardCharsets.UTF_8))
    Using.resource(ProgramRun.load(ProgramListing.Image(Language.PTX, image, None)).toOption.get) { program =>
      val run = ProgramRun
        .run(program, "mandelbrot".unsafe[NonEmpty[String]], Dimensions(8, 8, 1), Dimensions(4, 1, 1))
        .toOption
        .get
      assertEquals(run.failure, None)
      assert(run.trace.issues > 0)
      val allocation = run.allocations.head
      val words      = Vec.tabulate(256)(index => run.memory.getInt(allocation, (index * 4).unsafe[Int >= 0]))
      assert(words.exists(_ > 0), "no pixel escaped")
      assert(words.contains(0), "every pixel escaped")
    }
  }

  test("the SASS sample loads with the architecture its cubin names and runs") {
    val image = ProgramListing.Image(Language.SASS, bytes(SassExample.cubin), None)
    Using.resource(ProgramRun.load(image).toOption.get) { program =>
      assertEquals(program.image.target, Some("sm_50"))
      val run = ProgramRun.run(
        program,
        program.kernels.head,
        Dimensions(8, 8, 1),
        Dimensions(4, 1, 1)
      ).toOption.get
      assert(program.lines.nonEmpty)
      assert(run.trace.issues > 0)
      assertEquals(run.failure, None, s"the sample stopped: ${run.failure.map(_.description.raw)}")
    }
  }

  test("the AMDGPU sample loads with the architecture its code object names and runs") {
    val image = ProgramListing.Image(Language.AMDGPU, bytes(AmdgpuExample.codeObject), None)
    Using.resource(ProgramRun.load(image).toOption.get) { program =>
      assertEquals(program.image.target, Some("gfx900"))
      val run =
        ProgramRun.run(program, "probe".unsafe[NonEmpty[String]], Dimensions(8, 8, 1), Dimensions(4, 1, 1)).toOption.get
      assert(program.lines.nonEmpty)
      assert(run.trace.issues > 0)
      assertEquals(run.failure, None, s"the sample stopped: ${run.failure.map(_.description.raw)}")
    }
  }

  test("the SPIR-V sample loads and runs") {
    verify(
      Language.SPIRV,
      bytes(SpirvExample.module),
      None,
      Some("mandelbrot"),
      Dimensions(8, 8, 1),
      Dimensions(4, 1, 1)
    )
  }
}
