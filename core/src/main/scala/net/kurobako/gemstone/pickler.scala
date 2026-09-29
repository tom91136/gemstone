package net.kurobako.gemstone

import net.kurobako.gemstone.constraint.*
import upickle.default.ReadWriter
import upickle.default.readwriter

/** Import `pickler.given` when deriving codecs for refined values. */
object pickler {
  given ReadWriter[NonEmpty[String]] = readwriter[String].bimap(identity, _.unsafe[NonEmpty[String]])
}
