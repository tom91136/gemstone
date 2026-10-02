package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.constraint.*

class SourceCursorSuite extends munit.FunSuite {
  test("lookahead and failed consumption preserve the cursor") {
    val cursor = SourceCursor("abc")
    assert(cursor.has(2))
    assert(!cursor.has(3))
    assertEquals(cursor.char(2), 'c')
    assert(!cursor.consume("abd"))
    assertEquals(cursor.position, SourceCursor.Position(0, 1, 1))
    assert(cursor.consume("ab"))
    assert(cursor.consume('c'))
    assert(cursor.atEnd)
    assert(!cursor.consume('c'))
    val failure = intercept[SourceCursor.Failure](cursor.advance())
    assertEquals(failure.position, SourceCursor.Position(3, 1, 4))
  }

  test("checkpoints restore offsets and line information across CRLF") {
    val cursor = SourceCursor("a\r\nb\nc")
    assert(cursor.consume('a'))
    val mark = cursor.position
    cursor.skipWhitespace()
    assertEquals(cursor.position, SourceCursor.Position(3, 2, 1))
    assert(cursor.consume("b\n"))
    assertEquals(cursor.position, SourceCursor.Position(5, 3, 1))
    cursor.restore(mark)
    assertEquals(cursor.slice(0), "a")
    cursor.skipWhitespace()
    assertEquals(cursor.position, SourceCursor.Position(3, 2, 1))
    val _ = intercept[IllegalArgumentException](cursor.restore(SourceCursor.Position(100, 1, 1)))
    assertEquals(cursor.position, SourceCursor.Position(3, 2, 1))
  }

  test("scanning stops at delimiters and uses UTF-16 source positions") {
    val cursor = SourceCursor("alpha.\ud83d\ude00")
    cursor.skipWhile(_.isLetter)
    assertEquals(cursor.slice(0), "alpha")
    assert(cursor.consume('.'))
    val start = cursor.offset
    cursor.skipWhile(_ => true)
    assertEquals(cursor.slice(start), "\ud83d\ude00")
    assertEquals(cursor.offset.raw, 8)
    assertEquals(cursor.column.raw, 9)
    intercept[IllegalArgumentException](cursor.slice(9))
  }

  test("failures carry an expectation and the current position") {
    val cursor = SourceCursor("\n ")
    cursor.skipWhitespace()
    val failure = intercept[SourceCursor.Failure](cursor.fail("operand"))
    assertEquals(failure.position, SourceCursor.Position(2, 2, 2))
    assertEquals(failure.expectation.raw, "operand")
  }
}
