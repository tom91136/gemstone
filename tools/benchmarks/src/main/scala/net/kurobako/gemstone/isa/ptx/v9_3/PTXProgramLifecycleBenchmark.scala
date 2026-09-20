package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.{PlatformTime, Vec}
import net.kurobako.gemstone.benchmarks.BenchmarkArguments
import net.kurobako.gemstone.benchmarks.BenchmarkName
import net.kurobako.gemstone.benchmarks.PlatformMetrics
import net.kurobako.gemstone.execution.*

import cats.syntax.all.*
import com.monovore.decline.Opts
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import scala.util.Using

/** Exercises the PTX program lifecycle that CUDA conformance spends most of its setup time in. */
object PTXProgramLifecycleBenchmark {
  private enum Operation {
    case Decode, Launch
  }

  private final case class Options(
      operation: Operation,
      iterations: Int :| Positive,
      images: Int :| Positive,
      entries: Int :| Positive,
      instructions: Int :| Positive0,
      source: Option[Path],
      kernel: KernelName,
      recording: Option[Path]
  )

  private object EmptyMemory extends ExecutionMemory {
    override def read(
        id: AllocationId,
        offset: Long :| Positive0,
        size: Int :| Positive0
    ): Either[ExecutionFailure, Vec[Byte]] =
      Left(ExecutionFailure.Unsupported("benchmark kernel attempted a memory read"))

    override def write(
        id: AllocationId,
        offset: Long :| Positive0,
        bytes: Vec[Byte]
    ): Either[ExecutionFailure, Unit] =
      Left(ExecutionFailure.Unsupported("benchmark kernel attempted a memory write"))

    override def recordPointerRelocation(relocation: PointerRelocation): Unit = ()
  }

  private val name    = BenchmarkName("ptx-program-lifecycle")
  private val options = (
    Opts.option[String]("operation", help = "Operation to measure: decode or launch").withDefault("decode")
      .validate("operation must be decode or launch")(value => value == "decode" || value == "launch")
      .map(value => if (value == "launch") Operation.Launch else Operation.Decode),
    Opts.option[Int]("iterations", help = "Measured lifecycle operations").withDefault(20)
      .validate("iterations must be positive")(_ > 0).map(_.refineUnsafe[Positive]),
    Opts.option[Int]("images", help = "Distinct images rotated by decode operations").withDefault(8)
      .validate("images must be positive")(_ > 0).map(_.refineUnsafe[Positive]),
    Opts.option[Int]("entries", help = "Entries in the generated PTX fixture").withDefault(256)
      .validate("entries must be positive")(_ > 0).map(_.refineUnsafe[Positive]),
    Opts.option[Int]("instructions", help = "Arithmetic instructions generated per entry").withDefault(0)
      .validate("instructions must be non-negative")(_ >= 0).map(_.refineUnsafe[Positive0]),
    Opts.option[String]("source", help = "Optional PTX source file").map(Path.of(_)).orNone,
    Opts.option[String]("kernel", help = "Parameterless entry used by launch operations").withDefault("benchmark")
      .validate("kernel must be non-empty and contain no NUL")(value => value.nonEmpty && !value.contains('\u0000'))
      .map(KernelName.applyUnsafe),
    Opts.option[String]("recording", help = "JFR output path").map(Path.of(_)).orNone
  ).mapN(Options(_, _, _, _, _, _, _, _))

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def run(arguments: Array[String]): Unit = {
    val parsed = BenchmarkArguments.parse(name, arguments.toSeq, options)
    val source = parsed.source.fold(generatedSource(parsed.entries, parsed.instructions))(Files.readString(_))
    val images = Vector.tabulate(parsed.images)(index =>
      Vec.from(s"$source\n// lifecycle benchmark image $index\n".getBytes(StandardCharsets.UTF_8))
    )

    Using.resource(PlatformMetrics.startRecording(parsed.recording)) { _ =>
      val allocatedBefore = PlatformMetrics.allocatedBytes
      val started         = PlatformTime.monotonicNanos
      val checksum        = parsed.operation match {
        case Operation.Decode =>
          var iteration = 0
          var total     = 0L
          while (iteration < parsed.iterations) {
            val program = decode(images(iteration % images.size))
            total += program.catalog.fold(error => throw IllegalStateException(error.message), _.kernels.size.toLong)
            iteration += 1
          }
          total
        case Operation.Launch =>
          val program  = decode(images.head)
          val metadata = program.kernel(parsed.kernel)
            .fold(error => throw IllegalArgumentException(error.message), value => value)
          if (metadata.explicitParameterCount != 0)
            throw IllegalArgumentException(
              s"benchmark launch kernel '${parsed.kernel.value}' has ${metadata.explicitParameterCount} parameters"
            )
          val dispatch = KernelDispatch.validated(
            program = ProgramId(1),
            kernelName = parsed.kernel,
            groupSize = Dimensions.One,
            groupCount = Dimensions.One,
            arguments = Vec.empty
          ).fold(error => throw IllegalArgumentException(error), value => value)
          var iteration = 0
          while (iteration < parsed.iterations) {
            program.launch(dispatch, EmptyMemory, ExecutionControl.default)
              .fold(error => throw IllegalStateException(error.message), _ => ())
            iteration += 1
          }
          parsed.iterations.toLong
      }
      val elapsed   = math.max(0L, PlatformTime.monotonicNanos - started)
      val allocated = for {
        before <- allocatedBefore
        after  <- PlatformMetrics.allocatedBytes
        if after >= before
      } yield after - before
      Console.out.println(
        s"operation=${parsed.operation.toString.toLowerCase} iterations=${parsed.iterations} " +
          s"images=${parsed.images} entries=${parsed.entries} instructions=${parsed.instructions} " +
          s"sourceBytes=${source.getBytes(StandardCharsets.UTF_8).length} " +
          s"nsPerOperation=${elapsed.toDouble / parsed.iterations} " +
          s"bytesPerOperation=${allocated.fold(Double.NaN)(_.toDouble / parsed.iterations)} checksum=$checksum"
      )
    }
  }

  private def decode(image: Vec[Byte]): ProgramInstance =
    ModuleDecoder.decode(image).fold(error => throw IllegalArgumentException(error.message), value => value)

  private def generatedSource(entries: Int :| Positive, instructions: Int :| Positive0): String = {
    val body =
      if (instructions == 0) "ret;"
      else {
        val arithmetic = (0 until instructions).map(index => s"add.u32 %r1, %r0, ${index + 1};").mkString("\n")
        s".reg .b32 %r<2>;\nmov.u32 %r0, %tid.x;\n$arithmetic\nret;"
      }
    val source = StringBuilder(".version 8.0\n.target sm_80\n.address_size 64\n")
    source.append(s".visible .entry benchmark() { $body }\n")
    (1 until entries).foreach(index => source.append(s".visible .entry unused_$index() { $body }\n"))
    source.result()
  }
}
