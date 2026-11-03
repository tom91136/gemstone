package net.kurobako.gemstone.benchmarks

import cats.syntax.all.*
import com.monovore.decline.Command
import com.monovore.decline.Opts
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.atomic.AtomicBoolean
import net.kurobako.gemstone.*
import net.kurobako.gemstone.benchmarks.BenchmarkArguments.*
import net.kurobako.gemstone.benchmarks.PlatformBenchmarks.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.profile.*
import net.kurobako.gemstone.toolchain.*
import net.kurobako.gemstone.vm.*
import scala.util.Using

object BenchmarkRunner {
  case class Options(
      warmups: Int >= 0,
      samples: Int > 0,
      name: String,
      arguments: Vector[String],
      jfr: Boolean,
      observe: Boolean
  )

  case class ProfileOptions(
      architecture: Architecture,
      stream: Option[Path],
      replay: Option[Path],
      json: Option[Path]
  )

  enum Action derives CanEqual {
    case List
    case Run(options: Options, benchmark: LaunchControl => BenchmarkSample)
    case Profile(options: ProfileOptions, benchmark: LaunchControl => BenchmarkSample)
  }

  private val Top = 10

  val command: Command[Action] = Command("benchmarks", "Run Gemstone microbenchmarks")(
    Opts.subcommands(
      Command("list", "List available benchmark cases")(Opts(Action.List)),
      Command("run", "Run one benchmark")((
        Opts.argument[String]("case"),
        nonNegative("warmup", "Warmup iterations", 1),
        positive("samples", "Measured samples", 5),
        Opts.flag("no-jfr", help = "Disable the default JVM flight recording").orFalse,
        Opts.flag("observe", help = "Attach a fresh ProfileRecorder to every launch").orFalse,
        Opts.arguments[String]("case-argument").orEmpty
      ).tupled.mapValidated { (name, warmups, samples, noJfr, observe, arguments) =>
        PlatformBenchmarks.parse(name, arguments)
          .map(run =>
            Action.Run(Options(warmups, samples, name, arguments.toVector, !noJfr, observe), run)
          ).toValidatedNel
      }),
      Command("profile", "Profile one launch of a benchmark case")((
        Opts.argument[String]("case"),
        Opts.option[String]("architecture", help = "Architecture: sm_89, gfx1030 or gfx1036").orNone,
        Opts.option[String]("stream", help = "Write the profile stream to this file").map(Path.of(_)).orNone,
        Opts.option[String](
          "replay",
          help = "Read a profile stream instead of running the case"
        ).map(Path.of(_)).orNone,
        Opts.option[String]("json", help = "Write the profile as JSON lines to this file").map(Path.of(_)).orNone,
        Opts.arguments[String]("case-argument").orEmpty
      ).tupled.mapValidated { (name, architecture, stream, replay, json, arguments) =>
        selectProfile(name, architecture, stream, replay, json, arguments.toVector).toValidatedNel
      })
    )
  )

  def main(arguments: Array[String]): Unit = {
    workarounds.install()
    command.parse(arguments.toSeq) match {
      case Left(help) =>
        Console.err.println(help)
        if (help.errors.nonEmpty) sys.exit(2)
      case Right(Action.List) => PlatformBenchmarks.cases.foreach(entry => Console.out.println(entry.name))
      case Right(Action.Run(options, benchmark)) =>
        Using.resource(PlatformMetrics.startRecording(options.name, options.jfr)) { _ =>
          val measure = () => benchmark(launch(options.observe))
          (0 until options.warmups).foreach(_ => measure())
          Vec.indices(options.samples.widen[Int >= 0]).foreach { sample =>
            val started = platform.epochMillis
            Console.out.println(ujson.write(result(options, sample, measure(), started)))
          }
        }
      case Right(Action.Profile(options, benchmark)) =>
        stream(options, benchmark) match {
          case Left(error) =>
            Console.err.println(error)
            sys.exit(1)
          case Right(recorded) =>
            options.stream.foreach(path =>
              Using.resource(DataOutputStream(BufferedOutputStream(Files.newOutputStream(path))))(recorded.write)
            )
            val profile = Profiler.run(recorded, options.architecture)
            Console.out.println(ProfileReport.summary(profile, Top))
            options.json.foreach(path =>
              Files.writeString(path, ProfileReport.jsonLines(profile).map(_ + "\n").mkString)
            )
        }
    }
  }

  def launch(observe: Boolean): LaunchControl =
    if (observe) () => TaskControl(observer = Some(ProfileRecorder())) else () => TaskControl()

  def record(benchmark: LaunchControl => BenchmarkSample): Either[String, ProfileStream] = {
    val recorder = ProfileRecorder()
    val claimed  = AtomicBoolean(false)
    val _        = benchmark(() =>
      if (claimed.compareAndSet(false, true)) TaskControl(observer = Some(recorder)) else TaskControl()
    )
    val recorded = recorder.stream
    Either.cond(recorded.instructions.nonEmpty, recorded, "case launched no kernel")
  }

  private def selectProfile(
      name: String,
      architecture: Option[String],
      stream: Option[Path],
      replay: Option[Path],
      json: Option[Path],
      arguments: Vector[String]
  ): Either[String, Action] =
    for {
      _         <- Either.cond(stream.isEmpty || replay.isEmpty, (), "--stream and --replay are exclusive")
      entry     <- PlatformBenchmarks.find(name)
      resolved  <- architectureFor(entry, architecture)
      benchmark <- PlatformBenchmarks.parse(name, arguments)
    } yield Action.Profile(ProfileOptions(resolved, stream, replay, json), benchmark)

  private def architectureFor(
      entry: PlatformBenchmarks.Entry,
      requested: Option[String]
  ): Either[String, Architecture] =
    requested match {
      case Some(name) => Architecture.named(name).toRight(s"unknown architecture: $name")
      case None       =>
        entry.platform match {
          case Some(Platform.Nvidia)   => Right(Architecture.nvidiaDefault)
          case Some(Platform.Amdgpu)   => Right(Architecture.amdgpuDefault)
          case Some(Platform.Portable) =>
            Left(s"${entry.name} needs --architecture (${Architecture.all.map(_.name).mkString(", ")})")
          case None => Left(s"${entry.name} runs no VM")
        }
    }

  private def stream(
      options: ProfileOptions,
      benchmark: LaunchControl => BenchmarkSample
  ): Either[String, ProfileStream] =
    options.replay match {
      case Some(path) =>
        Using.resource(DataInputStream(BufferedInputStream(Files.newInputStream(path))))(ProfileStream.read(_))
      case None => record(benchmark)
    }

  def result(
      options: Options,
      sample: Int >= 0,
      measurement: BenchmarkSample,
      startedEpochMillis: Long = platform.epochMillis
  ): ujson.Obj = ujson.Obj(
    "schema"               -> "gemstone-benchmark-v2",
    "case"                 -> options.name,
    "arguments"            -> ujson.Arr.from(options.arguments),
    "platform"             -> PlatformMetrics.platform,
    "started_epoch_millis" -> startedEpochMillis.toString,
    "sample"               -> sample.raw,
    "warmups"              -> options.warmups.raw,
    "observed"             -> options.observe,
    "operation_count"      -> measurement.operations.toString,
    "elapsed_nanos"        -> measurement.elapsedNanos.toString,
    "allocated_bytes"      ->
      measurement.allocatedBytes.fold[ujson.Value](ujson.Null)(bytes => ujson.Str(bytes.toString)),
    "allocation_scope" ->
      (measurement.allocationScope match {
        case BenchmarkSample.AllocationScope.CallingThread => "calling-thread"
        case BenchmarkSample.AllocationScope.Workers       => "workers"
        case BenchmarkSample.AllocationScope.Unavailable   => "unavailable"
      }),
    "correct" -> true
  )
}
