package net.kurobako.gemstone

import scala.util.Using
import upickle.default.Reader
import upickle.default.read

object resources {

  /** Generated JSON packaged next to `owner`; a missing resource is a build defect, not an input error. */
  def json[A: Reader](owner: Class[?], name: String): A =
    Using.resource(
      Option(owner.getResourceAsStream(name))
        .getOrElse(throw IllegalStateException(s"missing resource $name for ${owner.getName}"))
    )(read[A](_))
}
