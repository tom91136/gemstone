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

/** One 64-lane group copying 16 bytes per lane with `global_load_dwordx4`/`global_store_dwordx4`; reads return owned
  * snapshots, matching ExecutionSession read ownership.
  */
object AMDGPUDwordx4CopyBenchmark {
  val benchmark: Case = iterations(2000).map(count => () => run(count))

  private val Words = Vec[Long](
    0xc00a0100L, 0x0L, 0xbf8cc07fL, 0x24000084L, 0xdc5c8000L, 0x1040000L, 0xbf8c0f70L, 0xdc7c8000L, 0x60100L,
    0xbf810000L
  )

  private def run(iterations: Int > 0): BenchmarkSample = {
    val code   = Words.flatMap(word => LittleEndian.toVec(word, 4))
    val kernel = VMCompiler().compile(CodeObjectKernel(
      "kernel",
      Target("gfx900"),
      16,
      0,
      0,
      0x80,
      0x08,
      code,
      code.nonNegativeSize
    ))
      .fold(error => throw IllegalStateException(error.message), identity)
    val memory = BenchmarkMemory(1024, 1024)
    val input  = memory.ids(0)
    val output = memory.ids(1)
    memory.array(input).indices.foreach(index => memory.array(input)(index) = (index * 17).toByte)
    val dispatch = KernelTask.validated(
      "kernel",
      Dimensions(64, 1, 1),
      Dimensions.One,
      Vec(KernelTask.Argument.Memory(input, 0L), KernelTask.Argument.Memory(output, 0L))
    ).fold(error => throw IllegalStateException(error), identity)
    val sample = Measurement.repeat(iterations) {
      VMInterpreter.execute(kernel, dispatch, memory, TaskControl(), Workgroups.Sequential, Vec.empty, _ => Right(()))
        .fold(error => throw IllegalStateException(error.toString), _ => ())
    }
    if (!memory.array(input).sameElements(memory.array(output)))
      throw IllegalStateException("dwordx4 copy mismatch")
    sample
  }
}
