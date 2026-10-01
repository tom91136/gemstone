package net.kurobako.gemstone.benchmarks

import cats.syntax.all.*
import com.monovore.decline.Command
import com.monovore.decline.Opts
import net.kurobako.gemstone.*
import net.kurobako.gemstone.benchmarks.BenchmarkArguments.*
import net.kurobako.gemstone.constraint.*

object BenchmarkRunner {
  private sealed trait Action
  private case object ListBenchmarks                extends Action
  private case class Run(options: BenchmarkOptions) extends Action

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
        Opts.arguments[String]("case-argument").orEmpty
      ).mapN { (name, warmups, samples, rest) =>
        Run(BenchmarkOptions(warmups, samples, BenchmarkInvocation(name, rest.toVector)))
      })
    )
  )

  // Warmup and sample counters are deliberately excluded from measured case execution.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def main(arguments: Array[String]): Unit = parse(arguments.toSeq) match {
    case ListBenchmarks => PlatformBenchmarks.names.toVector.foreach(name => Console.out.println(name.raw))
    case Run(options)   =>
      var warmup = 0
      while warmup < options.warmups.raw do {
        PlatformBenchmarks.run(options.invocation)
        warmup += 1
      }
      var sample = 0
      while sample < options.samples.raw do {
        val started = platform.monotonicNanos
        PlatformBenchmarks.run(options.invocation)
        val elapsed = math.max(0L, platform.monotonicNanos - started).unsafe[Long >= 0]
        Console.out.println(ujson.write(result(options, sample.unsafe[Int >= 0], elapsed)))
        sample += 1
      }
  }

  private def parse(arguments: Seq[String]): Action =
    command.parse(arguments).fold(help => throw IllegalArgumentException(help.toString), value => value)

  def parse(name: BenchmarkName, arguments: Vector[String]): BenchmarkOptions =
    parse("run" +: name.raw +: arguments) match {
      case Run(options) => options
      case _            => throw IllegalArgumentException("expected benchmark run")
    }

  def result(
      options: BenchmarkOptions,
      sample: Int >= 0,
      elapsedNanos: Long >= 0
  ): ujson.Obj = ujson.Obj(
    "schema"               -> "gemstone-benchmark-v1",
    "case"                 -> options.invocation.name.raw,
    "arguments"            -> ujson.Arr.from(options.invocation.arguments),
    "platform"             -> PlatformMetrics.platform,
    "started_epoch_millis" -> platform.epochMillis,
    "sample"               -> sample.raw,
    "warmups"              -> options.warmups.raw,
    "operation_count"      -> 1L,
    "elapsed_nanos"        -> elapsedNanos.raw,
    "correct"              -> true
  )
}
