package net.kurobako.gemstone.benchmarks

import net.kurobako.gemstone.Constraint.Positive

import com.monovore.decline.Command
import com.monovore.decline.Opts

object BenchmarkArguments {
  def parse[A](name: BenchmarkName, arguments: Seq[String], options: Opts[A]): A =
    Command(name.raw, s"Options for the ${name.raw} benchmark")(options).parse(arguments)
      .fold(help => throw IllegalArgumentException(help.toString), value => value)

  def iterations(name: BenchmarkName, arguments: Seq[String], default: Positive[Int]): Positive[Int] =
    parse(
      name,
      arguments,
      Opts.option[Int]("iterations", help = "Measured iterations").withDefault(default.raw)
        .validate("iterations must be positive")(_ > 0).map(Positive.unsafe)
    )
}
