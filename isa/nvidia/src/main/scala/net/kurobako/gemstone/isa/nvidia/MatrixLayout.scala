package net.kurobako.gemstone.isa.nvidia

import VMIR.Matrix.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*

/** The lane and fragment layout of `ldmatrix`, as the primitives its semantics calls and the functions rendered
  * semantics calls in their place. A tile is a shape and whether it is transposed, an element the register and part of
  * a lane's fragment, and a selector the form of a stored row and the column to take from it.
  */
private[isa] object MatrixLayout {
  private val tiles: Array[Tile] =
    TileShape.values.flatMap(shape => Array(false, true).map(Tile(shape, 1, _, Packing.Bytes)))

  def code(tile: Tile): Long = 2L * tile.shape.ordinal + (if (tile.transpose) 1L else 0L)

  /** 0 for a row of 16-bit elements, otherwise 1 plus the packing of its byte elements. */
  def form(tile: Tile): Long = if (tile.shape.bytes == 2) 0L else 1L + tile.packing.ordinal

  private def coordinate(tile: Long, lane: Long, element: Long) = {
    val shape                 = tiles(tile.toInt)
    val (matrix, row, column) = shape.coordinate(WarpLane.values(lane.toInt), (element >> 2).toInt, (element & 3).toInt)
    (matrix, shape.stored(row, column))
  }

  /** The lane that holds the address of the row a fragment element comes from. */
  private def rowOf(tile: Long, lane: Long, element: Long): Long = {
    val (matrix, stored) = coordinate(tile, lane, element)
    matrix.toLong * tiles(tile.toInt).addresses + stored.row
  }

  /** The column of that row the fragment element is. */
  private def columnOf(tile: Long, lane: Long, element: Long): Long = coordinate(tile, lane, element)._2.column.toLong

  private def byte(low: Long, high: Long, index: Long): Long =
    ((if (index < 8L) low else high) >>> (8L * (index & 7L))) & 0xffL

  /** The element a selector names in the 16 bytes `low` and `high` hold, as a fragment holds it. */
  private def elementOf(selector: Long, low: Long, high: Long): Long = {
    val column = selector & 0xffL
    (selector >> 8) match {
      case 0L => ((if (column < 4L) low else high) >>> (16L * (column & 3L))) & 0xffffL
      case 1L => byte(low, high, column)
      case 2L =>
        val bit = column * 6L
        val at  = bit >> 3
        ((byte(low, high, at) | (byte(low, high, math.min(at + 1L, 15L)) << 8)) >>> (bit & 7L)) & 0x3fL
      case _ => ((byte(low, high, column >> 1) >>> ((column & 1L) * 4L)) & 0xfL) << 2
    }
  }

  val Row: Primitive[Primitive.Ternary] = Primitive("net.kurobako.gemstone.isa.nvidia.MatrixLayout.row", rowOf(_, _, _))
  val Column: Primitive[Primitive.Ternary] =
    Primitive("net.kurobako.gemstone.isa.nvidia.MatrixLayout.column", columnOf(_, _, _))
  val Element: Primitive[Primitive.Ternary] =
    Primitive("net.kurobako.gemstone.isa.nvidia.MatrixLayout.element", elementOf(_, _, _))

  def row(tile: UInt[64], lane: UInt[64], element: UInt[64]): UInt[64] =
    fromRaw[UInt[64]](rowOf(toRaw(tile), toRaw(lane), toRaw(element)))

  def column(tile: UInt[64], lane: UInt[64], element: UInt[64]): UInt[64] =
    fromRaw[UInt[64]](columnOf(toRaw(tile), toRaw(lane), toRaw(element)))

  def element(selector: UInt[64], low: UInt[64], high: UInt[64]): UInt[64] =
    fromRaw[UInt[64]](elementOf(toRaw(selector), toRaw(low), toRaw(high)))

  /** The rows above the columns of a WMMA fragment, as the layout primitives take its shape. */
  def fragmentCode(fragment: Fragment): Long = (fragment.rows.toLong << 32) | fragment.columns.toLong

  /** The row and column of the fragment element `element` a lane holds, which the WMMA layout deals out cyclically. */
  private def fragmentCoordinate(code: Long, lane: Long, element: Long): (Long, Long) = {
    val columns = code & 0xffffffffL
    val linear  = (element * 32L + lane) % ((code >>> 32) * columns)
    (linear / columns, linear % columns)
  }

  val FragmentRow: Primitive[Primitive.Ternary] =
    Primitive("net.kurobako.gemstone.isa.nvidia.MatrixLayout.fragmentRow", fragmentCoordinate(_, _, _)._1)
  val FragmentColumn: Primitive[Primitive.Ternary] =
    Primitive("net.kurobako.gemstone.isa.nvidia.MatrixLayout.fragmentColumn", fragmentCoordinate(_, _, _)._2)

  def fragmentRow(code: UInt[64], lane: UInt[64], element: UInt[64]): UInt[64] =
    fromRaw[UInt[64]](fragmentCoordinate(toRaw(code), toRaw(lane), toRaw(element))._1)

  def fragmentColumn(code: UInt[64], lane: UInt[64], element: UInt[64]): UInt[64] =
    fromRaw[UInt[64]](fragmentCoordinate(toRaw(code), toRaw(lane), toRaw(element))._2)
}
