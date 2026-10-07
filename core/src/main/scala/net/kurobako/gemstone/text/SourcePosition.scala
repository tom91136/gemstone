package net.kurobako.gemstone.text

import net.kurobako.gemstone.constraint.*

object SourcePosition {
  val Start: SourcePosition = SourcePosition(1, 1)

  /** Position of a UTF-16 offset, with LF advancing the line. */
  def at(source: String, offset: Int >= 0): SourcePosition = {
    val end = math.min(offset.raw, source.length)
    SourcePosition(
      (source.view.take(end).count(_ == '\n') + 1).unsafe[Int > 0],
      (end - source.lastIndexOf('\n', end - 1)).unsafe[Int > 0]
    )
  }
}

/** One-based line and UTF-16 column in a text source. */
case class SourcePosition(line: Int > 0, column: Int > 0) {
  override def toString: String = s"$line:$column"
}
