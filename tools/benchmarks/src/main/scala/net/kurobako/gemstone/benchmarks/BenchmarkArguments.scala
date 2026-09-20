package net.kurobako.gemstone.benchmarks

import com.monovore.decline.Command
import com.monovore.decline.Opts
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

object BenchmarkArguments {
  def parse[A](name: BenchmarkName, arguments: Seq[String], options: Opts[A]): A =
    Command(name.value, s"Options for the ${name.value} benchmark")(options).parse(arguments)
      .fold(help => throw IllegalArgumentException(help.toString), value => value)

  def iterations(name: BenchmarkName, arguments: Seq[String], default: Int :| Positive): Int :| Positive =
    parse(
      name,
      arguments,
      Opts.option[Int]("iterations", help = "Measured iterations").withDefault(default)
        .validate("iterations must be positive")(_ > 0).map(_.refineUnsafe[Positive])
    )
}
