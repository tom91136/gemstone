package net.kurobako.gemstone

import net.kurobako.gemstone.constraint.*
import upickle.default.ReadWriter
import upickle.default.readwriter

/** Import `pickler.given` when deriving codecs for refined values. */
object pickler {
  // upickle type class over a refined String, neither of which can host it
  @SuppressWarnings(Array("scalafix:GivenPlacement"))
  given ReadWriter[NonEmpty[String]] = readwriter[String].bimap(identity, _.unsafe[NonEmpty[String]])
}
