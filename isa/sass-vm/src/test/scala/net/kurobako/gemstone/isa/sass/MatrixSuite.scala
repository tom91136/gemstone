package net.kurobako.gemstone.isa.sass

import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*
import net.kurobako.gemstone.isa.ptx.*
import net.kurobako.gemstone.isa.sass.Harness.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*

/** Matrix instructions as ptxas emits them, and the code it emits around them where an architecture has no tensor core
  * for a form, against the PTX they come from and, for sm_89, the words an RTX 4060 Ti stores.
  */
class MatrixSuite extends munit.FunSuite {
  private def fromPtx(probe: MatrixProbes.Probe, input: Vec[Long]): Vec[Long] = {
    val program = PTXBackend().load(Vec.from(probe.ptx.getBytes(StandardCharsets.UTF_8))).value
    val memory  = Memory()
    val out     = memory.allocate(Vec.fill(32 * probe.d * 4)(0.toByte))
    val in      = memory.allocate(words(input))
    val task    = KernelTask.validated(
      probe.name.unsafe[NonEmpty[String]],
      LaunchGeometry.Dimensions(32, 1, 1),
      LaunchGeometry.Dimensions.One,
      Vec(KernelTask.Argument.Memory(out, 0L), KernelTask.Argument.Memory(in, 0L))
    ).value
    program.execute(task, memory, TaskControl(), _ => Right(())).value
    memory.readWords(out, (32 * probe.d).unsafe[Int >= 0])
  }

  MatrixProbes.probes.foreach { probe =>
    test(s"${probe.name} matches its PTX${if (probe.measured.nonEmpty) " and the measured words" else ""}") {
      val input  = Vec.from(MatrixProbes.inputs(probe))
      val kernel = load(probe.sm, Fixture.listing(probe.name), probe.name, shared = 2048)
      val words  = kernel.launch(32)(Arg.zeros(32 * probe.d * 4), Arg.Buffer(Harness.words(input))).words(0)
      assertEquals(words.map(_.toHexString), fromPtx(probe, input).map(_.toHexString), "PTX")
      probe.measured.foreach(measured =>
        assertEquals(words.map(_.toHexString), Vec.from(MatrixProbes.words(measured)).map(_.toHexString), "measured")
      )
    }
  }
}
