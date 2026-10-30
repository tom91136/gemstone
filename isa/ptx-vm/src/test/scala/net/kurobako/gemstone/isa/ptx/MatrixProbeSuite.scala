package net.kurobako.gemstone.isa.ptx

import execution.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*
import net.kurobako.gemstone.testing.Suite

/** The sm_89 matrix probes, run from their PTX, against the words an RTX 4060 Ti stores. */
class MatrixProbeSuite extends Suite {
  MatrixProbes.probes.foreach { probe =>
    probe.measured.foreach(measured =>
      test(s"${probe.name} matches the measured words") {
        val input    = MatrixProbes.inputs(probe)
        val expected = MatrixProbes.words(measured)
        val memory   = DeviceMemory(65536)
        val out      = memory.start
        val in       = memory.start.offset(32768L)
        input.zipWithIndex.foreach((word, index) =>
          assertEquals(memory.writeInt32(in.offset(index * 4L), word.toInt), Right(()))
        )
        val arguments = Map(
          ("out": NonEmpty[String]) -> Argument.fromLong(out.raw),
          ("in": NonEmpty[String])  -> Argument.fromLong(in.raw)
        )
        assertEquals(execute(compileKernel(probe.ptx), 1, 32, arguments, memory), Right(()))
        val words =
          expected.indices.map(index => memory.readInt32(out.offset(index * 4L)).toOption.get.toLong & 0xffffffffL)
        assertEquals(words.map(_.toHexString).toVector, expected.map(_.toHexString))
      }
    )
  }
}
