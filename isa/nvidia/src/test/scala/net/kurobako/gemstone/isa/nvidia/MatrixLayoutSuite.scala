package net.kurobako.gemstone.isa.nvidia

import VMIR.Matrix.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.Suite

class MatrixLayoutSuite extends Suite {
  private def tile(shape: TileShape, transpose: Boolean, packing: Packing = Packing.Bytes) =
    Tile(shape, 1, transpose, packing)

  private def place(tile: Tile, lane: Int, register: Int, part: Int): (row: Long, column: Long) = {
    val element = 4L * register + part
    (
      MatrixLayout.Row.function(MatrixLayout.code(tile), lane.toLong, element),
      MatrixLayout.Column.function(MatrixLayout.code(tile), lane.toLong, element)
    )
  }

  test("a lane takes two consecutive 16-bit elements of the row its quad names, from the lane that holds that row") {
    val plain = tile(TileShape.M8N8, transpose = false)
    assertEquals(place(plain, lane = 5, register = 1, part = 0), (9L, 2L))
    assertEquals(place(plain, lane = 5, register = 1, part = 1), (9L, 3L))
    assertEquals(place(plain, lane = 31, register = 3, part = 1), (31L, 7L))
  }

  test("a transposed matrix takes the elements of a column from consecutive rows") {
    val transposed = tile(TileShape.M8N8, transpose = true)
    assertEquals(place(transposed, lane = 5, register = 1, part = 0), (10L, 1L))
    assertEquals(place(transposed, lane = 5, register = 1, part = 1), (11L, 1L))
  }

  test("a 16x16 byte matrix has two registers per matrix, the second from the rows eight on") {
    val wide = tile(TileShape.M16N16, transpose = false)
    assertEquals(place(wide, lane = 5, register = 0, part = 2), (1L, 6L))
    assertEquals(place(wide, lane = 5, register = 1, part = 2), (9L, 6L))
    assertEquals(place(wide, lane = 5, register = 2, part = 0), (17L, 4L))
  }

  private val rows = Vec(
    (0x0807060504030201L, 0x100f0e0d0c0b0a09L),
    (0x8182838485868788L, 0x7071727374757677L),
    (-1L, 0L),
    (0x123456789abcdef0L, 0xfedcba9876543210L)
  )

  private def bytes(low: Long, high: Long): Array[Byte] =
    Array.tabulate(16)(index => ((if (index < 8) low else high) >>> (8 * (index % 8))).toByte)

  test("an element of a 16-bit row is the little-endian halfword at its column") {
    for {
      (low, high) <- rows
      column      <- 0 until 8
    } {
      val memory   = bytes(low, high)
      val expected = (memory(2 * column) & 0xffL) | ((memory(2 * column + 1) & 0xffL) << 8)
      assertEquals(MatrixLayout.Element.function(column, low, high), expected)
    }
  }

  test("an element of a byte row is the byte its packing places at its column") {
    for {
      packing     <- Packing.values.toSeq
      (low, high) <- rows
      column      <- 0 until 16
    } {
      val memory   = bytes(low, high)
      val expected = packing.unpack(memory)(column) & 0xffL
      val selector = (1L + packing.ordinal << 8) | column
      assertEquals(MatrixLayout.Element.function(selector, low, high), expected, s"$packing column $column")
    }
  }
}
