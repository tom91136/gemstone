package net.kurobako.gemstone.isa.amdgpu

import cats.syntax.all.*
import net.kurobako.gemstone.benchmarks.*
import net.kurobako.gemstone.benchmarks.BenchmarkArguments.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

/** ALU-only companion to [[AMDGPUVectorCopyBenchmark]], isolating instruction and lane dispatch from guest memory. */
object AMDGPUALUBenchmark {
  private enum Mode derives CanEqual {
    case Move, Binary
  }

  val benchmark: Case = (
    iterations(50),
    nonNegative("instructions", "ALU instructions per dispatch", 512),
    choice("mode", "move" -> Mode.Move, "binary" -> Mode.Binary)
  ).mapN((iterations, instructions, mode) => () => run(iterations, instructions, mode))

  private def run(iterations: Int > 0, instructions: Int >= 0, mode: Mode): BenchmarkSample = {
    val instruction = mode match {
      case Mode.Move => 0x7e0002ebL
      // gfx9 VOP2 v_add_f32 v0, v1, v2
      case Mode.Binary => 0x02000501L
    }
    val words    = Vec.fill(instructions)(instruction) :+ 0xbf810000L
    val code     = words.flatMap(word => LittleEndian.toVec(word, 4))
    val kernel   = CodeObjectKernel("alu", Target("gfx900"), 0, 0, 0, 0, 0, code, code.nonNegativeSize)
    val prepared = VMCompiler().compile(kernel).fold(failure => throw RuntimeException(failure.message), identity)
    val dispatch = KernelTask.validated("alu", Dimensions(64, 1, 1), Dimensions.One, Vec.empty)
      .fold(message => throw IllegalArgumentException(message), identity)
    val memory = BenchmarkMemory()
    SequentialDispatch.repeat(iterations, prepared, dispatch, memory)
  }
}
