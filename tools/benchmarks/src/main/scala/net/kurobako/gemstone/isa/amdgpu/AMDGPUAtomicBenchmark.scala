package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.benchmarks.*
import net.kurobako.gemstone.benchmarks.BenchmarkArguments.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

/** Checked one-lane global atomic add. The instruction image is the fixture of the AMDGPU atomic conformance unit
  * tests.
  */
object AMDGPUAtomicBenchmark {
  val benchmark: Case = iterations(20000).map(count => () => run(count))

  private val Image =
    "000204f4000000fa000308f4080000fa7fc08cbfff02007e78563412ff02027e103254768002047e0c02087e0e020a7e0e020c7e0f020e7e0080c8dc02040800703f8cbf0202047e830404349004044a008074dc02000800000081bf"

  private def run(iterations: Int > 0): BenchmarkSample = {
    val code   = Vec.from(Image.grouped(2).map(Integer.parseInt(_, 16).toByte))
    val kernel = VMCompiler().compile(CodeObjectKernel(
      "atomic",
      Target("gfx1030"),
      24,
      0,
      0,
      0x80,
      0x08,
      code,
      code.nonNegativeSize
    ))
      .fold(error => throw IllegalStateException(error.message), identity)
    val memory = BenchmarkMemory(24)
    val target = memory.ids(0)
    memory.putInt(target, 0, 7)
    val dispatch = KernelTask.validated(
      "atomic",
      Dimensions.One,
      Dimensions.One,
      Vec(
        KernelTask.Argument.Memory(target, 0L),
        KernelTask.Argument.Scalar(LittleEndian.toVec(9L, 8).unsafe),
        KernelTask.Argument.Scalar(LittleEndian.toVec(7L, 8).unsafe)
      )
    ).fold(error => throw IllegalArgumentException(error), identity)
    val sample = Measurement.repeat(iterations) {
      VMInterpreter.execute(kernel, dispatch, memory, TaskControl(), Workgroups.Sequential, Vec.empty, _ => Right(()))
        .fold(error => throw IllegalStateException(error.message), _ => ())
    }
    val observed = memory.getInt(target, 0).toLong & 0xffffffffL
    val expected = (7L + iterations.raw.toLong * 9L) & 0xffffffffL
    if (observed != expected) throw IllegalStateException(s"atomic benchmark result $observed != $expected")
    sample
  }
}
