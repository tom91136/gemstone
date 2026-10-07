package net.kurobako.gemstone.isa.amdgpu

import cats.syntax.all.*
import com.monovore.decline.Opts
import java.nio.file.Files
import java.nio.file.Path
import net.kurobako.gemstone.benchmarks.*
import net.kurobako.gemstone.benchmarks.BenchmarkArguments.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

/** Vector-copy kernel, one dword per lane in 64-lane groups: the embedded gfx900 ROCr sample by default, or a
  * `vector_copy(source, destination, unused)` kernel from a compiled code object.
  */
object AMDGPUVectorCopyBenchmark {
  val benchmark: Case = (
    iterations(200),
    positive("groups", "Workgroups per dispatch", 1),
    Opts.option[String]("image", "AMDGPU ELF code object containing vector_copy").map(Path.of(_)).orNone
  ).mapN((iterations, groups, image) => () => run(iterations, groups, image))

  private val Words = Vec[Long](
    0xc0020242L, 0x00000004L, 0xc0020283L, 0x00000018L,
    0xc00a0003L, 0x00000000L, 0xbea10080L, 0xbf8cc07fL,
    0x8604ff09L, 0x0000ffffL, 0x92080408L, 0x810a080aL,
    0x6800000aL, 0x2202009fL, 0xd28f0000L, 0x00020082L,
    0x7e060201L, 0x32040000L, 0x38060303L, 0xdc508000L,
    0x027f0002L, 0x7e060203L, 0x32000002L, 0x38020303L,
    0xbf8c0f70L, 0xdc708000L, 0x007f0200L, 0xbf810000L
  )

  private def kernel(image: Option[Path]): CodeObjectKernel = image.fold {
    val code = Words.flatMap(word => LittleEndian.toVec(word, 4))
    CodeObjectKernel("vector_copy", Target("gfx900"), 80, 0, 0, 0x80, 0x0b, code, code.nonNegativeSize)
  }(path =>
    ELF.index(Vec.unsafeWrapArray(Files.readAllBytes(path))).flatMap(_.kernel("vector_copy"))
      .fold(error => throw IllegalArgumentException(error.message), identity)
  )

  private def run(iterations: Int > 0, groups: Int > 0, image: Option[Path]): BenchmarkSample = {
    val elements = Math.multiplyExact(groups.raw, 64)
    val prepared =
      VMCompiler().compile(kernel(image)).fold(failure => throw RuntimeException(failure.message), identity)
    val memory = BenchmarkMemory(elements * 4, elements * 4, elements * 4)
    val input  = memory.ids(0)
    val output = memory.ids(1)
    val guard  = memory.array(memory.ids(2))
    (0 until elements).foreach(index => memory.putInt(input, index * 4, index * 97 - 11))
    java.util.Arrays.fill(guard, 0x5a.toByte)
    val hidden   = Vec.fill(prepared.source.kernargSize.raw - memory.ids.size * 8)(0.toByte)
    val dispatch = KernelTask.validated(
      "vector_copy",
      Dimensions(64, 1, 1),
      Dimensions(groups, 1, 1),
      memory.ids.map(KernelTask.Argument.Memory(_, 0L)) ++
        hidden.refine[NonEmpty[Vec[Byte]]].toOption.map(KernelTask.Argument.Scalar(_))
    ).fold(message => throw IllegalArgumentException(message), identity)
    val sample = Measurement.repeat(iterations) {
      VMInterpreter.execute(prepared, dispatch, memory, TaskControl(), Workgroups.Sequential, Vec.empty, _ => Right(()))
        .fold(failure => throw RuntimeException(failure.message), _ => ())
    }
    (0 until elements).foreach { index =>
      val observed = memory.getInt(output, index * 4)
      if (observed != index * 97 - 11) throw IllegalStateException(s"copy mismatch at $index: $observed")
    }
    if (!guard.forall(_ == 0x5a.toByte)) throw IllegalStateException("vector copy modified the guard allocation")
    sample
  }
}
