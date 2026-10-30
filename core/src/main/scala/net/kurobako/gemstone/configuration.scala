package net.kurobako.gemstone

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.string.*

/** Text grammar for project-owned process configuration. No process-global reads. */
object configuration {
  def positiveLong(value: String): Option[Long > 0] =
    Option.when(value.nonEmpty && value.forall(c => c >= '0' && c <= '9'))(value)
      .flatMap(_.toLongOption).flatMap(_.refine[Long > 0].toOption)

  /** A positive decimal with at most three fractional digits, matching the native shims and CMake. */
  def scale(value: String): Option[Double] =
    Option.when(value.matches("[0-9]+(\\.[0-9]{1,3})?"))(value.toDouble).filter(_ > 0)

  /** CMake truth values, matching the native shims. */
  def boolean(value: String): Option[Boolean] = value.toLowerCaseRoot match {
    case "1" | "true" | "on" | "y" | "yes"  => Some(true)
    case "0" | "false" | "off" | "n" | "no" => Some(false)
    case _                                  => None
  }

  /** Empty values count as unset. */
  def lookup(values: Map[String, String], name: String): Option[String] = values.get(name).filter(_.nonEmpty)

  def enabled(values: Map[String, String], name: String): Either[String, Boolean] =
    lookup(values, name).fold(Right(false))(value =>
      boolean(value).toRight(s"$name must be one of 1 true on y yes 0 false off n no, not '$value'")
    )

  def traceEnabled(value: String, scope: String, category: String = ""): Boolean = {
    val qualified = if (category.isEmpty) scope else s"$scope.$category"
    value.split(',').exists { token =>
      val trimmed = token.dropWhile(c => c == ' ' || c == '\t').reverse.dropWhile(c => c == ' ' || c == '\t').reverse
      trimmed == "all" || trimmed == scope || trimmed == qualified
    }
  }
}
