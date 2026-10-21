package net.kurobako.gemstone.isa.ptx

import cats.syntax.all.*
import com.monovore.decline.Opts
import java.nio.file.Files
import java.nio.file.Path
import net.kurobako.gemstone.benchmarks.*
import net.kurobako.gemstone.benchmarks.BenchmarkArguments.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*
import scala.collection.mutable

/** Exercises the PTX program lifecycle that CUDA conformance spends most of its setup time in. */
object PTXProgramLifecycleBenchmark {
  private enum Operation derives CanEqual {
    case Decode, Launch
  }

  private case class Options(
      operation: Operation,
      iterations: Int > 0,
      images: Int > 0,
      entries: Int > 0,
      instructions: Int >= 0,
      source: Option[Path],
      kernel: NonEmpty[String]
  )

  val benchmark: Case = (
    choice("operation", "decode" -> Operation.Decode, "launch" -> Operation.Launch),
    iterations(20),
    positive("images", "Distinct images rotated by decode operations", 8),
    positive("entries", "Entries in the generated PTX fixture", 256),
    nonNegative("instructions", "Arithmetic instructions generated per entry", 0),
    Opts.option[String]("source", help = "Optional PTX source file").map(Path.of(_)).orNone,
    Opts.option[String]("kernel", help = "Parameterless entry used by launch operations").withDefault("benchmark")
      .mapValidated(_.refine[NonEmpty[String]].leftMap(_ => "kernel must be non-empty").toValidatedNel)
  ).mapN(Options(_, _, _, _, _, _, _)).map(options => () => run(options))

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def run(options: Options): BenchmarkSample = {
    val source = options.source.fold(generatedSource(options.entries, options.instructions))(Files.readString(_))
    val images = Vector.tabulate(options.images)(index => s"$source\n// lifecycle benchmark image $index\n")
    options.operation match {
      case Operation.Decode =>
        val measurement = Measurement {
          var iteration = 0
          var total     = 0L
          while (iteration < options.iterations) {
            val program = decode(images(iteration % images.size))
            total += program.symbols.fold(error => throw IllegalStateException(error.message), _.kernels.size.toLong)
            iteration += 1
          }
          total
        }
        require(measurement.value > 0L, "benchmark program has no kernels")
        measurement.sample(options.iterations.widen[Long > 0])
      case Operation.Launch =>
        val program  = decode(images.head)
        val metadata =
          program.kernel(options.kernel).fold(error => throw IllegalArgumentException(error.message), identity)
        if (metadata.explicitParameterCount != 0)
          throw IllegalArgumentException(
            s"benchmark launch kernel '${options.kernel}' has ${metadata.explicitParameterCount} parameters"
          )
        val task = KernelTask.validated(
          kernelName = options.kernel,
          groupSize = LaunchGeometry.Dimensions.One,
          groupCount = LaunchGeometry.Dimensions.One,
          arguments = Vec.empty
        ).fold(error => throw IllegalArgumentException(error), identity)
        val memory = BenchmarkMemory()
        Measurement.repeat(options.iterations) {
          program.execute(task, TaskContext(memory, TaskControl(), _ => Right(())))
            .fold(error => throw IllegalStateException(error.message), _ => ())
        }
    }
  }

  private def decode(source: String): LoadedProgram = {
    val program = Parser.parse(source).fold(error => throw IllegalArgumentException(error.message), identity)
    LoadedProgram.load(program).fold(error => throw IllegalArgumentException(error.message), identity)
  }

  private def generatedSource(entries: Int > 0, instructions: Int >= 0): String = {
    val body =
      if (instructions == 0) "ret;"
      else {
        val arithmetic = (0 until instructions).map(index => s"add.u32 %r1, %r0, ${index + 1};").mkString("\n")
        s".reg .b32 %r<2>;\nmov.u32 %r0, %tid.x;\n$arithmetic\nret;"
      }
    val source = mutable.StringBuilder(".version 8.0\n.target sm_80\n.address_size 64\n")
    source.append(s".visible .entry benchmark() { $body }\n")
    (1 until entries).foreach(index => source.append(s".visible .entry unused_$index() { $body }\n"))
    source.result()
  }
}
