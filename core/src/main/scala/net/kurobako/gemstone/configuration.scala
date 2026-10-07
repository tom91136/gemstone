package net.kurobako.gemstone

import java.nio.file.Path
import net.kurobako.gemstone.constraint.*

/** Text grammar for project-owned process configuration. No process-global reads. */
object configuration {
  def positiveLong(value: String): Option[Long > 0] =
    Option.when(value.nonEmpty && value.forall(c => c >= '0' && c <= '9'))(value)
      .flatMap(_.toLongOption).flatMap(_.refine[Long > 0].toOption)

  /** The JVM rejects NUL in `Path.of`, Scala Native does not. */
  def path(name: String, value: String): Either[String, Path] =
    Either.cond(!value.contains('\u0000'), Path.of(value), s"$name must not contain a NUL character")

  def traceEnabled(value: String, scope: String, category: String = ""): Boolean = {
    val qualified = if (category.isEmpty) scope else s"$scope.$category"
    value.split(',').exists { token =>
      val trimmed = token.dropWhile(c => c == ' ' || c == '\t').reverse.dropWhile(c => c == ' ' || c == '\t').reverse
      trimmed == "all" || trimmed == scope || trimmed == qualified
    }
  }
}
