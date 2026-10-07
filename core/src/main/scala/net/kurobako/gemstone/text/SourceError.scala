package net.kurobako.gemstone.text

import net.kurobako.gemstone.constraint.*

/** Diagnostic for malformed text, located at the offending source position. */
case class SourceError(position: SourcePosition, message: NonEmpty[String]) {
  def diagnostic: NonEmpty[String] = s"$position: $message"

  override def toString: String = diagnostic
}
