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

  def workers(default: Int): Opts[Int > 0] = positive("workers", "Host execution workers", default)

  /** The first choice is the default. */
  def choice[A](name: String, first: (String, A), rest: (String, A)*): Opts[A] = {
    val choices = first +: rest
    val names   = choices.map(_._1).mkString(", ")
    Opts.option[String](name, s"One of $names").withDefault(first._1).mapValidated(value =>
      choices.collectFirst { case (`value`, selected) => selected }.toValidNel(s"$name must be one of $names")
    )
  }
}
