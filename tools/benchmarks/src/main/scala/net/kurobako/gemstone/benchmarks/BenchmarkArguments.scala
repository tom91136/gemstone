package net.kurobako.gemstone.benchmarks

import com.monovore.decline.Command
import com.monovore.decline.Opts
import net.kurobako.gemstone.constraint.*

object BenchmarkArguments {
  def parse[A](name: BenchmarkName, arguments: Seq[String], options: Opts[A]): A =
    Command(name.raw, s"Options for the $name benchmark")(options).parse(arguments)
      .fold(help => throw IllegalArgumentException(help.toString), value => value)

  def iterations(name: BenchmarkName, arguments: Seq[String], default: Int > 0): Int > 0 =
    parse(
      name,
      arguments,
      Opts.option[Int]("iterations", help = "Measured iterations").withDefault(default.raw)
        .validate("iterations must be positive")(_ > 0).map(_.unsafe[Int > 0])
    )
}
