package net.kurobako.gemstone.benchmarks

import cats.syntax.all.*
import com.monovore.decline.Command
import com.monovore.decline.Opts
import net.kurobako.gemstone.*
import net.kurobako.gemstone.benchmarks.BenchmarkArguments.*
import net.kurobako.gemstone.constraint.*
import scala.util.Using

object BenchmarkRunner {
  private sealed trait Action
  private case object ListBenchmarks                              extends Action
  private case class Run(options: BenchmarkOptions, jfr: Boolean) extends Action

  private val command = Command("benchmarks", "Run Gemstone microbenchmarks")(
    Opts.subcommands(
      Command("list", "List available benchmark cases")(Opts(ListBenchmarks)),
      Command("run", "Run one benchmark")((
        Opts.argument[String]("case")
          .validate("benchmark name must be non-empty and contain no NUL")(value =>
            !value.contains('\u0000')
          )
          .refined[BenchmarkName]("benchmark name must be non-empty and contain no NUL")
          .mapValidated { name =>
            if (PlatformBenchmarks.names.contains(name)) name.validNel
            else s"unknown benchmark: $name".invalidNel
          },
        Opts.option[Int]("warmup", help = "Warmup iterations").withDefault(1)
          .refined[Int >= 0]("warmups must be non-negative"),
        Opts.option[Int]("samples", help = "Measured samples").withDefault(5)
          .refined[Int > 0]("samples must be positive"),
        Opts.flag("no-jfr", help = "Disable the default JVM flight recording").orFalse,
        Opts.arguments[String]("case-argument").orEmpty
      ).mapN { (name, warmups, samples, noJfr, rest) =>
        Run(options = BenchmarkOptions(warmups, samples, BenchmarkInvocation(name, rest.toVector)), jfr = !noJfr)
      })
    )
  )

  // Warmup and sample counters are deliberately excluded from measured case execution.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def main(arguments: Array[String]): Unit = parse(arguments.toSeq) match {
    case ListBenchmarks    => PlatformBenchmarks.names.toVector.foreach(name => Console.out.println(name.raw))
    case Run(options, jfr) => Using.resource(PlatformMetrics.startRunRecording(options.invocation.name, jfr)) { _ =>
        var warmup = 0
        while warmup < options.warmups.raw do {
          val _ = PlatformBenchmarks.run(options.invocation)
          warmup += 1
        }
        var sample: Int >= 0 = 0
        while sample.raw < options.samples.raw do {
          val started  = platform.epochMillis
          val measured = PlatformBenchmarks.run(options.invocation)
          Console.out.println(ujson.write(result(options, sample, measured, started)))
          sample += 1
        }
      }
  }

  private def parse(arguments: Seq[String]): Action =
    command.parse(arguments).fold(help => throw IllegalArgumentException(help.toString), value => value)

  def parse(name: BenchmarkName, arguments: Vector[String]): BenchmarkOptions =
    parse("run" +: name.raw +: arguments) match {
      case Run(options, _) => options
      case _               => throw IllegalArgumentException("expected benchmark run")
    }

  def result(
      options: BenchmarkOptions,
      sample: Int >= 0,
      measurement: BenchmarkSample,
      startedEpochMillis: Long = platform.epochMillis
  ): ujson.Obj = ujson.Obj(
    "schema"               -> "gemstone-benchmark-v2",
    "case"                 -> options.invocation.name.raw,
    "arguments"            -> ujson.Arr.from(options.invocation.arguments),
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
