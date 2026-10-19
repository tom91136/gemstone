package net.kurobako.gemstone

import java.nio.file.Path

/** Paths in project-owned process configuration, where a file system exists. */
object paths {

  /** The JVM rejects NUL in `Path.of`, Scala Native does not. */
  def parse(name: String, value: String): Either[String, Path] =
    Either.cond(!value.contains('\u0000'), Path.of(value), s"$name must not contain a NUL character")
}
