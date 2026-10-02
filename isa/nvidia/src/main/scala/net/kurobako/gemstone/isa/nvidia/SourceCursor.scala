package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.constraint.*
import scala.util.control.NoStackTrace

object SourceCursor {
  case class Position(offset: Int >= 0, line: Int > 0, column: Int > 0)

  case class Failure(position: Position, expectation: NonEmpty[String]) extends RuntimeException with NoStackTrace {
    override def getMessage: String =
      s"${position.line.raw}:${position.column.raw} (offset ${position.offset.raw}): expected $expectation"
  }
}

/** Character scanning shared by the NVIDIA text frontends. Grammar and token interpretation belong to callers.
  * Positions use UTF-16 offsets and columns, with LF advancing the line, matching the PTX source diagnostics.
  */
@SuppressWarnings(Array("scalafix:DisableSyntax.var"))
class SourceCursor(source: String) {
  private var currentOffset: Int >= 0 = 0
  private var currentLine: Int > 0    = 1
  private var currentColumn: Int > 0  = 1

  def offset: Int >= 0                = currentOffset
  def line: Int > 0                   = currentLine
  def column: Int > 0                 = currentColumn
  def atEnd: Boolean                  = currentOffset.raw == source.length
  def position: SourceCursor.Position = SourceCursor.Position(currentOffset, currentLine, currentColumn)

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

  /** Restore a position previously captured from this cursor. */
  def restore(saved: SourceCursor.Position): Unit = {
    require(saved.offset.raw <= source.length, "source position is outside the input")
    currentOffset = saved.offset
    currentLine = saved.line
    currentColumn = saved.column
  }

  def slice(start: Int >= 0): String = {
    require(start <= currentOffset, "source span starts after the cursor")
    source.substring(start.raw, currentOffset.raw)
  }

  def fail(expectation: NonEmpty[String]): Nothing = throw SourceCursor.Failure(position, expectation)
}
