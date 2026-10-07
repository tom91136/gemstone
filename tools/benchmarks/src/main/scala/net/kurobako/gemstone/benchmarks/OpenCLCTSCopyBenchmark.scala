package net.kurobako.gemstone.benchmarks

import cats.syntax.all.*
import net.kurobako.gemstone.benchmarks.BenchmarkArguments.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.isa.spirv.*
import net.kurobako.gemstone.vm.*
import scala.util.Using

/** Bounded OpenCL CTS async-copy paths, measured after SPIR-V loading. */
object OpenCLCTSCopyBenchmark {
  private enum Direction {
    case GlobalToLocal, LocalToGlobal
  }

  private case class Options(
      direction: Direction,
      groups: Int > 0,
      localSize: Int > 0,
      stride: Int > 0,
      iterations: Int > 0,
      workers: Int > 0
  )

  val benchmark: Case = (
    choice("direction", "global-to-local" -> Direction.GlobalToLocal, "local-to-global" -> Direction.LocalToGlobal),
    positive("groups", "Workgroups per dispatch", 16),
    positive("local-size", "Work items per group", 8),
    positive("stride", "Element stride (1 is contiguous)", 2),
    iterations(3),
    workers(1)
  ).mapN(Options(_, _, _, _, _, _)).map(options => () => run(options))

  private val Unwritten = 0x5a.toByte

  private def run(options: Options): BenchmarkSample = {
    val copied = Math.multiplyExact(Math.multiplyExact(options.groups.raw, options.localSize.raw), 3)
    val bytes  = Math.multiplyExact(Math.multiplyExact(copied, options.stride.raw), 4)
    val memory = BenchmarkMemory(bytes, bytes)
    val input  = memory.ids(0)
    val output = memory.ids(1)
    val _      = Array.tabulate[Byte](bytes)(index => (index * 37 + 11).toByte).copyToArray(memory.array(input))
    java.util.Arrays.fill(memory.array(output), Unwritten)
    val kernel: NonEmpty[String] = (options.direction, options.stride.raw) match {
      case (Direction.GlobalToLocal, 1) => "global_to_local_contiguous"
      case (Direction.LocalToGlobal, 1) => "local_to_global_contiguous"
      case (Direction.GlobalToLocal, _) => "global_to_local"
      case (Direction.LocalToGlobal, _) => "local_to_global"
    }
    val task = KernelTask.validated(
      kernel,
      LaunchGeometry.Dimensions(options.localSize, 1, 1),
      LaunchGeometry.Dimensions(options.groups, 1, 1),
      Vec(
        KernelTask.Argument.Memory(input, 0L),
        KernelTask.Argument.Memory(output, 0L),
        KernelTask.Argument.Local(options.localSize.toLong.widen[Long > 0] * 3L * 4L),
        KernelTask.Argument.Scalar(LittleEndian.toVec(options.stride.raw.toLong, 4).unsafe)
      )
    ).fold(error => throw IllegalArgumentException(error), identity)
    val image  = Vec.unsafeWrapArray(BenchmarkResources.bytes("opencl-cts-async-copy.spv", "opencl"))
    val sample = Using.resource(WorkerPool(options.workers, minGroups = 1L, minWorkItems = 1L)) { workgroups =>
      val program = SPIRVBackend(Validation.Environment.OpenCLCompute, workgroups = workgroups).load(image)
        .fold(error => throw IllegalArgumentException(error.message), identity)
      Measurement.repeat(options.iterations) {
        program.execute(task, memory, TaskControl(), _ => Right(()))
          .fold(error => throw IllegalStateException(error.message), _ => ())
      }
    }
    val source      = memory.array(input)
    val destination = memory.array(output)
    destination.indices.foreach { index =>
      val element  = index / 4
      val expected = if (element % options.stride.raw == 0 && element / options.stride.raw < copied) source(index)
      else Unwritten
      if (destination(index) != expected)
        throw IllegalStateException(s"async copy mismatch at byte $index: ${destination(index)} != $expected")
    }
    sample
  }
}
