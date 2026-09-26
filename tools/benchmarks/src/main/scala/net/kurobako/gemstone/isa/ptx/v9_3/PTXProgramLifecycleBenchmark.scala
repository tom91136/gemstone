package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.{NonNegativeInt, NonNegativeLong, PlatformTime, PositiveInt, Vec}
import net.kurobako.gemstone.benchmarks.BenchmarkArguments
import net.kurobako.gemstone.benchmarks.BenchmarkName
import net.kurobako.gemstone.benchmarks.PlatformMetrics
import net.kurobako.gemstone.isa.KernelName
import net.kurobako.gemstone.memory.GuestAddress
import net.kurobako.gemstone.vm.*

import cats.syntax.all.*
import com.monovore.decline.Opts

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
      iterations: PositiveInt,
      images: PositiveInt,
      entries: PositiveInt,
      instructions: NonNegativeInt,
      source: Option[Path],
      kernel: KernelName,
      recording: Option[Path]
  )

  private val machine = VirtualMachine(Observer.discard, Scheduler.Sequential)

  private object EmptyMemory extends Memory with AddressSpace {
    override def read(
        region: MemoryRegion,
        offset: NonNegativeLong,
        size: NonNegativeInt
    ): Either[Fault, Vec[Byte]] =
      Left(Fault.Unsupported("benchmark kernel attempted a memory read"))

    override def write(
        region: MemoryRegion,
        offset: NonNegativeLong,
        bytes: Vec[Byte]
    ): Either[Fault, Unit] =
      Left(Fault.Unsupported("benchmark kernel attempted a memory write"))

    override def address(region: MemoryRegion, offset: NonNegativeLong): Either[Fault, GuestAddress] =
      Left(Fault.Unsupported("benchmark kernel attempted address translation"))

    override def locate(address: GuestAddress, size: NonNegativeInt): Either[Fault, AddressSpace.Location] =
      Left(Fault.Unsupported("benchmark kernel attempted address lookup"))
  }

  private val name    = BenchmarkName("ptx-program-lifecycle")
  private val options = (
    Opts.option[String]("operation", help = "Operation to measure: decode or launch").withDefault("decode")
      .validate("operation must be decode or launch")(value => value == "decode" || value == "launch")
      .map(value => if (value == "launch") Operation.Launch else Operation.Decode),
    Opts.option[Int]("iterations", help = "Measured lifecycle operations").withDefault(20)
      .validate("iterations must be positive")(_ > 0).map(PositiveInt.unsafe),
    Opts.option[Int]("images", help = "Distinct images rotated by decode operations").withDefault(8)
      .validate("images must be positive")(_ > 0).map(PositiveInt.unsafe),
    Opts.option[Int]("entries", help = "Entries in the generated PTX fixture").withDefault(256)
      .validate("entries must be positive")(_ > 0).map(PositiveInt.unsafe),
    Opts.option[Int]("instructions", help = "Arithmetic instructions generated per entry").withDefault(0)
      .validate("instructions must be non-negative")(_ >= 0).map(NonNegativeInt.unsafe),
    Opts.option[String]("source", help = "Optional PTX source file").map(Path.of(_)).orNone,
    Opts.option[String]("kernel", help = "Parameterless entry used by launch operations").withDefault("benchmark")
      .validate("kernel must be non-empty and contain no NUL")(value => value.nonEmpty && !value.contains('\u0000'))
      .map(KernelName.unsafe),
    Opts.option[String]("recording", help = "JFR output path").map(Path.of(_)).orNone
  ).mapN(Options(_, _, _, _, _, _, _, _))

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def run(arguments: Array[String]): Unit = {
    val parsed = BenchmarkArguments.parse(name, arguments.toSeq, options)
    val source = parsed.source.fold(generatedSource(parsed.entries, parsed.instructions))(Files.readString(_))
    val images = Vector.tabulate(parsed.images.raw)(index => s"$source\n// lifecycle benchmark image $index\n")

    Using.resource(PlatformMetrics.startRecording(parsed.recording)) { _ =>
      val allocatedBefore = PlatformMetrics.allocatedBytes
      val started         = PlatformTime.monotonicNanos
      val checksum        = parsed.operation match {
        case Operation.Decode =>
          var iteration = 0
          var total     = 0L
          while (iteration < parsed.iterations.raw) {
            val program = decode(images(iteration % images.size))
            total += machine.symbols(program)
              .fold(error => throw IllegalStateException(error.message), _.kernels.size.toLong)
            iteration += 1
          }
          total
        case Operation.Launch =>
          val program  = decode(images.head)
          val metadata = machine.kernel(program, parsed.kernel)
            .fold(error => throw IllegalArgumentException(error.message), value => value)
          if (metadata.explicitParameterCount.raw != 0)
            throw IllegalArgumentException(
              s"benchmark launch kernel '${parsed.kernel.raw}' has ${metadata.explicitParameterCount} parameters"
            )
          val task = KernelTask.validated(
            kernelName = parsed.kernel,
            groupSize = LaunchGeometry.Dimensions.One,
            groupCount = LaunchGeometry.Dimensions.One,
            arguments = Vec.empty
          ).fold(error => throw IllegalArgumentException(error), value => value)
          var iteration = 0
          while (iteration < parsed.iterations.raw) {
            machine.execute(program, task, EmptyMemory, TaskControl.default)
              .fold(error => throw IllegalStateException(error.message), _ => ())
            iteration += 1
          }
          parsed.iterations.raw.toLong
      }
      val elapsed   = math.max(0L, PlatformTime.monotonicNanos - started)
      val allocated = for {
        before <- allocatedBefore
        after  <- PlatformMetrics.allocatedBytes
        if after >= before
      } yield (after - before).raw
      Console.out.println(
        s"operation=${parsed.operation.toString.toLowerCase(java.util.Locale.ROOT)} iterations=${parsed.iterations} " +
          s"images=${parsed.images} entries=${parsed.entries} instructions=${parsed.instructions} " +
          s"sourceBytes=${source.getBytes(StandardCharsets.UTF_8).length} " +
          s"nsPerOperation=${elapsed.toDouble / parsed.iterations.raw} " +
          s"bytesPerOperation=${allocated.fold(Double.NaN)(_.toDouble / parsed.iterations.raw)} checksum=$checksum"
      )
    }
  }

  private def decode(source: String): machine.LoadedProgram = {
    val program = Parser.decode(source).fold(error => throw IllegalArgumentException(error.message), identity)
    machine.load(program).fold(error => throw IllegalArgumentException(error.message), identity)
  }

  private def generatedSource(entries: PositiveInt, instructions: NonNegativeInt): String = {
    val body =
      if (instructions.raw == 0) "ret;"
      else {
        val arithmetic = (0 until instructions.raw).map(index => s"add.u32 %r1, %r0, ${index + 1};").mkString("\n")
        s".reg .b32 %r<2>;\nmov.u32 %r0, %tid.x;\n$arithmetic\nret;"
      }
    val source = StringBuilder(".version 8.0\n.target sm_80\n.address_size 64\n")
    source.append(s".visible .entry benchmark() { $body }\n")
    (1 until entries.raw).foreach(index => source.append(s".visible .entry unused_$index() { $body }\n"))
    source.result()
  }
}
