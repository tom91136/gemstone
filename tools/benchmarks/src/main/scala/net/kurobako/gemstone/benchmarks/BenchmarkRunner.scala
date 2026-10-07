package net.kurobako.gemstone.benchmarks

import cats.syntax.all.*
import com.monovore.decline.Command
import com.monovore.decline.Opts
import net.kurobako.gemstone.*
import net.kurobako.gemstone.benchmarks.BenchmarkArguments.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import scala.util.Using

object BenchmarkRunner {
  case class Options(warmups: Int >= 0, samples: Int > 0, name: String, arguments: Vector[String], jfr: Boolean)

  enum Action {
    case List
    case Run(options: Options, measure: () => BenchmarkSample)
  }

  val command: Command[Action] = Command("benchmarks", "Run Gemstone microbenchmarks")(
    Opts.subcommands(
      Command("list", "List available benchmark cases")(Opts(Action.List)),
      Command("run", "Run one benchmark")((
        Opts.argument[String]("case"),
        nonNegative("warmup", "Warmup iterations", 1),
        positive("samples", "Measured samples", 5),
        Opts.flag("no-jfr", help = "Disable the default JVM flight recording").orFalse,
        Opts.arguments[String]("case-argument").orEmpty
      ).tupled.mapValidated { (name, warmups, samples, noJfr, arguments) =>
        PlatformBenchmarks.parse(name, arguments)
          .map(Action.Run(Options(warmups, samples, name, arguments.toVector, !noJfr), _)).toValidatedNel
      })
    )
  )

  def main(arguments: Array[String]): Unit = command.parse(arguments.toSeq) match {
    case Left(help) =>
      Console.err.println(help)
      if (help.errors.nonEmpty) sys.exit(2)
    case Right(Action.List)                  => PlatformBenchmarks.cases.foreach((name, _) => Console.out.println(name))
    case Right(Action.Run(options, measure)) =>
      Using.resource(PlatformMetrics.startRecording(options.name, options.jfr)) { _ =>
        (0 until options.warmups.raw).foreach(_ => measure())
        Vec.indices(options.samples.widen[Int >= 0]).foreach { sample =>
          val started = platform.epochMillis
          Console.out.println(ujson.write(result(options, sample, measure(), started)))
        }
      }
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
    "operation_count"      -> measurement.operations.raw.toString,
    "elapsed_nanos"        -> measurement.elapsedNanos.raw.toString,
    "allocated_bytes"      ->
      measurement.allocatedBytes.fold[ujson.Value](ujson.Null)(bytes => ujson.Str(bytes.raw.toString)),
    "allocation_scope" ->
      (measurement.allocationScope match {
        case BenchmarkSample.AllocationScope.CallingThread => "calling-thread"
        case BenchmarkSample.AllocationScope.Workers       => "workers"
        case BenchmarkSample.AllocationScope.Unavailable   => "unavailable"
      }),
    "correct" -> true
  )
}
