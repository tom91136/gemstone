package net.kurobako.gemstone.benchmarks

import cats.syntax.all.*
import com.monovore.decline.Opts
import net.kurobako.gemstone.constraint.*

object BenchmarkArguments {

  /** Case arguments parse into the measured action, so invalid arguments fail before any measurement. */
  type Case = Opts[() => BenchmarkSample]

  def positive(name: String, help: String, default: Int): Opts[Int > 0] =
    Opts.option[Int](name, help).withDefault(default)
      .mapValidated(_.refine[Int > 0].leftMap(_ => s"$name must be positive").toValidatedNel)

  def nonNegative(name: String, help: String, default: Int): Opts[Int >= 0] =
    Opts.option[Int](name, help).withDefault(default)
      .mapValidated(_.refine[Int >= 0].leftMap(_ => s"$name must be non-negative").toValidatedNel)

  def iterations(default: Int): Opts[Int > 0] = positive("iterations", "Measured operations per sample", default)

  val workers: Opts[Int > 0] = positive("workers", "Host execution workers", 1)

  /** The first choice is the default. */
  def choice[A](name: String, first: (name: String, value: A), rest: (name: String, value: A)*): Opts[A] = {
    val choices = first +: rest
    val names   = choices.map(_.name).mkString(", ")
    Opts.option[String](name, s"One of $names").withDefault(first.name).mapValidated(value =>
      choices.collectFirst { case (`value`, selected) => selected }.toValidNel(s"$name must be one of $names")
    )
  }
}
