package net.kurobako.gemstone.text

import net.kurobako.gemstone.constraint.*
import scala.util.control.NoStackTrace

object SourceCursor {
  case class Mark(offset: Int >= 0, position: SourcePosition)

  case class Failure(mark: Mark, expectation: NonEmpty[String]) extends RuntimeException with NoStackTrace {
    def error: SourceError          = SourceError(mark.position, s"expected $expectation")
    override def getMessage: String = error.toString
  }
}

/** Character scanning for text frontends. Grammar and token interpretation belong to callers. Positions use UTF-16
  * offsets and columns, with LF advancing the line.
  */
@SuppressWarnings(Array("scalafix:DisableSyntax.var"))
class SourceCursor(source: String) {
  private var currentOffset: Int >= 0 = 0
  private var currentLine: Int > 0    = 1
  private var currentColumn: Int > 0  = 1

  def offset: Int >= 0         = currentOffset
  def position: SourcePosition = SourcePosition(currentLine, currentColumn)
  def mark: SourceCursor.Mark  = SourceCursor.Mark(currentOffset, position)
  def atEnd: Boolean           = currentOffset.raw == source.length

  def has(ahead: Int >= 0 = 0): Boolean = ahead.raw < source.length - currentOffset.raw
  def char(ahead: Int >= 0 = 0): Char   = {
    if (!has(ahead)) fail("source character")
    source.charAt(currentOffset.raw + ahead.raw)
  }
  def at(value: Char): Boolean           = !atEnd && source.charAt(currentOffset.raw) == value
  def startsWith(value: String): Boolean = source.startsWith(value, currentOffset.raw)

  def advance(): Unit = {
    val value = char()
    currentOffset += 1
    if (value == '\n') { currentLine += 1; currentColumn = 1 }
    else currentColumn += 1
  }

  def consume(value: Char): Boolean =
    if (at(value)) { advance(); true }
    else false

  def consume(value: String): Boolean =
    if (startsWith(value)) {
      val end = currentOffset.raw + value.length
      while (currentOffset.raw < end) advance()
      true
    } else false

  def skipWhile(predicate: Char => Boolean): Unit =
    while (!atEnd && predicate(source.charAt(currentOffset.raw))) advance()

  def skipWhitespace(): Unit = skipWhile(_.isWhitespace)

  def restore(saved: SourceCursor.Mark): Unit = {
    require(saved.offset.raw <= source.length, "source position is outside the input")
    currentOffset = saved.offset
    currentLine = saved.position.line
    currentColumn = saved.position.column
  }

  def slice(start: Int >= 0): String = {
    require(start <= currentOffset, "source span starts after the cursor")
    source.substring(start.raw, currentOffset.raw)
  }

  def fail(expectation: NonEmpty[String]): Nothing = throw SourceCursor.Failure(mark, expectation)
}
