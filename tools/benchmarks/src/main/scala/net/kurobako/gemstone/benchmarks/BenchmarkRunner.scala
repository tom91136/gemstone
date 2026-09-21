package net.kurobako.gemstone.benchmarks

import cats.syntax.all.*
import com.monovore.decline.Command
import com.monovore.decline.Opts
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import net.kurobako.gemstone.PlatformTime

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
          .map(value => BenchmarkName.applyUnsafe(value)),
        Opts.option[Int]("warmup", help = "Warmup iterations").withDefault(1)
          .validate("warmups must be non-negative")(_ >= 0).map(_.refineUnsafe[Positive0]),
        Opts.option[Int]("samples", help = "Measured samples").withDefault(5)
          .validate("samples must be positive")(_ > 0).map(_.refineUnsafe[Positive]),
        Opts.arguments[String]("case-argument").orEmpty
      ).mapN { (name, warmups, samples, rest) =>
        Run(BenchmarkOptions(warmups, samples, BenchmarkInvocation(name, rest.toVector)))
      })
    )
  )

  // Warmup and sample counters are deliberately excluded from measured case execution.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def main(arguments: Array[String]): Unit = parse(arguments.toSeq) match {
    case ListBenchmarks => PlatformBenchmarks.names.foreach(name => Console.out.println(name.value))
    case Run(options)   =>
      if (!PlatformBenchmarks.names.contains(options.invocation.name))
        throw IllegalArgumentException(s"unknown benchmark: ${options.invocation.name}")
      var warmup = 0
      while warmup < options.warmups do {
        PlatformBenchmarks.run(options.invocation)
        warmup += 1
      }
      var sample = 0
      while sample < options.samples do {
        val started = PlatformTime.monotonicNanos
        PlatformBenchmarks.run(options.invocation)
        val elapsed = math.max(0L, PlatformTime.monotonicNanos - started).refineUnsafe[Positive0]
        Console.out.println(ujson.write(result(options, sample.refineUnsafe[Positive0], elapsed)))
        sample += 1
      }
  }

  private def parse(arguments: Seq[String]): Action =
    command.parse(arguments).fold(help => throw IllegalArgumentException(help.toString), value => value)

  def parse(name: BenchmarkName, arguments: Vector[String]): BenchmarkOptions =
    parse("run" +: name.value +: arguments) match {
      case Run(options) => options
      case _            => throw IllegalArgumentException("expected benchmark run")
    }

  def result(
      options: BenchmarkOptions,
      sample: Int :| Positive0,
      elapsedNanos: Long :| Positive0
  ): ujson.Obj = ujson.Obj(
    "schema"               -> "gemstone-benchmark-v1",
    "case"                 -> options.invocation.name.value,
    "arguments"            -> ujson.Arr.from(options.invocation.arguments),
    "platform"             -> PlatformMetrics.platform,
    "started_epoch_millis" -> PlatformTime.epochMillis,
    "sample"               -> sample,
    "warmups"              -> options.warmups,
    "operation_count"      -> 1L,
    "elapsed_nanos"        -> elapsedNanos,
    "correct"              -> true
  )
}
