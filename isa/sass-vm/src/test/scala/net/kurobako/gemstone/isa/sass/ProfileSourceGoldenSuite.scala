package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.Harness.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.profile.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*

/** Warp issues per SASS instruction of the probe kernels on an RTX 4060 Ti (sm_89, driver 615.78.08, CUDA 13.3, ncu
  * 2026.1.0.0), one warp per launch: the `Instructions Executed` column of the source page collected with the
  * `SourceCounters` and `WarpStateStats` sections, keyed by SASS byte offset. Only instructions that issue are listed.
  */
class ProfileSourceGoldenSuite extends Suite {
  private val kernels = Vec(
    ("ld_stride", 2, "0:1 10:1 20:1 30:1 40:1 50:1 60:1 70:1 80:1 90:1"),
    ("shared_stride", 32, "0:1 10:1 20:1 30:1 40:1 50:1 60:1 70:1 80:1 90:1 a0:1 b0:1"),
    (
      "divergent",
      1,
      """0:1 10:1 20:1 30:1 40:1 50:1 60:1 70:1 80:1 90:1 a0:1 b0:1 c0:1 d0:1 e0:1 f0:1
        |100:1 110:1 120:1 130:1 140:1 150:1 160:1 170:1 180:1 190:1 1a0:1 1b0:1 1c0:1 1d0:1
        |1e0:1 2d0:1 2e0:1 2f0:1 380:1 390:1 3a0:1 3b0:1 3c0:1 3d0:1 3e0:1 3f0:1 400:1 410:1
        |420:1 430:1 440:1 4e0:1 4f0:1 500:2 510:1 520:1 530:1 540:1""".stripMargin
    )
  )

  kernels.foreach { (name, arg, table) =>
    test(s"hardware: $name $arg") {
      assertEquals(issues(observe(name, arg)), offsets(table))
    }
  }

  private def offsets(table: String): Set[(Long, Long)] =
    table.trim.split("\\s+").map { token =>
      val parts = token.split(':')
      (java.lang.Long.parseLong(parts(0), 16), parts(1).toLong)
    }.toSet

  private def issues(profile: Profile): Set[(Long, Long)] =
    profile.instructions.collect {
      case (instruction, counters) if counters.issues > 0L && instruction.address.nonEmpty =>
        (instruction.address.get.raw, counters.issues)
    }.toSet

  private def pointers(kernel: String): Int = kernel match {
    case "shared_stride" => 1
    case _               => 2
  }

  private def shared(kernel: String): Int >= 0 = kernel match {
    case "shared_stride" => 4224
    case _               => 0
  }

  private def listing(kernel: String): String =
    Fixture.listing(kernel.split('_').zipWithIndex.map((part, index) =>
      if (index == 0) part else part.capitalize
    ).mkString + "Sm89")

  private def observe(name: String, arg: Int): Profile = {
    val kernel  = Harness.load(89, listing(name), name, shared(name))
    val memory  = Memory()
    val buffers = Vec.tabulate(pointers(name))(_ => memory.allocate(Vec.fill(65536)(0.toByte)))
    val values  = Vec.from(
      buffers.view.map(buffer => KernelTask.Argument.Memory(buffer, 0L)) :+
        KernelTask.Argument.Scalar(LittleEndian.toVec(arg.toLong, 4).refine[NonEmpty[Vec[Byte]]].value)
    )
    val task = KernelTask.validated(
      kernel.name,
      LaunchGeometry.Dimensions(32, 1, 1),
      LaunchGeometry.Dimensions(1, 1, 1),
      values
    ).value
    val sizes    = Seq.fill(pointers(name))(8) :+ 4
    val compiled = kernel.compile(sizes*).value
    val recorder = ProfileRecorder()
    memory.run(compiled, task, TaskControl(observer = Some(recorder))).left.foreach(fault => fail(fault.message))
    Profiler.run(recorder.stream, Architecture.sm89)
  }
}
