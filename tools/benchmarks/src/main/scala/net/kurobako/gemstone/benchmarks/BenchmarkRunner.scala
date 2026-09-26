package net.kurobako.gemstone.benchmarks

import cats.syntax.all.*
import com.monovore.decline.Command
import com.monovore.decline.Opts
import net.kurobako.gemstone.NonNegativeInt
import net.kurobako.gemstone.NonNegativeLong
import net.kurobako.gemstone.PlatformTime
import net.kurobako.gemstone.PositiveInt

object BenchmarkRunner {
  private sealed trait Action
  private case object ListBenchmarks                      extends Action
  private final case class Run(options: BenchmarkOptions) extends Action

  private val command = Command("benchmarks", "Run Gemstone microbenchmarks")(
    Opts.subcommands(
      Command("list", "List available benchmark cases")(Opts(ListBenchmarks)),
      Command("run", "Run one benchmark")((
        Opts.argument[String]("case")
          .validate("benchmark name must be non-empty and contain no NUL")(value =>
            value.nonEmpty && !value.contains('\u0000')
          )
          .map(value => BenchmarkName.unsafe(value)),
        Opts.option[Int]("warmup", help = "Warmup iterations").withDefault(1)
          .validate("warmups must be non-negative")(_ >= 0).map(NonNegativeInt.unsafe),
        Opts.option[Int]("samples", help = "Measured samples").withDefault(5)
          .validate("samples must be positive")(_ > 0).map(PositiveInt.unsafe),
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
      if (!PlatformBenchmarks.names.exists(_ == options.invocation.name))
        throw IllegalArgumentException(s"unknown benchmark: ${options.invocation.name}")
      var warmup = 0
      while warmup < options.warmups.raw do {
        PlatformBenchmarks.run(options.invocation)
        warmup += 1
      }
      var sample = 0
      while sample < options.samples.raw do {
        val started = PlatformTime.monotonicNanos
        PlatformBenchmarks.run(options.invocation)
        val elapsed = NonNegativeLong.unsafe(math.max(0L, PlatformTime.monotonicNanos - started))
        Console.out.println(ujson.write(result(options, NonNegativeInt.unsafe(sample), elapsed)))
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
      sample: NonNegativeInt,
      elapsedNanos: NonNegativeLong
  ): ujson.Obj = ujson.Obj(
    "schema"               -> "gemstone-benchmark-v1",
    "case"                 -> options.invocation.name.raw,
    "arguments"            -> ujson.Arr.from(options.invocation.arguments),
    "platform"             -> PlatformMetrics.platform,
    "started_epoch_millis" -> PlatformTime.epochMillis,
    "sample"               -> sample.raw,
    "warmups"              -> options.warmups.raw,
    "operation_count"      -> 1L,
    "elapsed_nanos"        -> elapsedNanos.raw,
    "correct"              -> true
  )
}
