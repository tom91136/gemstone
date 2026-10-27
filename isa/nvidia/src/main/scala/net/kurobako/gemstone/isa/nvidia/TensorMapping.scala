package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*

/** How the box of a tensor copy maps onto global memory and shared memory, over the lane algebra. Execution evaluates
  * these bodies on `Long` values to enumerate the runs of a transfer, and rendered definitions print them over the
  * fields the tensor map holds.
  *
  * A copy visits rows of box elements. Each row has a base address and says whether it lies inside the tensor, and each
  * element of it follows from its coordinate along dimension 0. A tile row is a combination of coordinates along the
  * other dimensions, and an im2col row is a pixel that the bounding box walks to.
  */
@SuppressWarnings(Array("scalafix:DisableSyntax.var"))
private[isa] object TensorMapping {

  /** The fields of a tensor map that [[field]] reads. `Dimension`, `Box` and `Traversal` take the dimension, and
    * `Stride` the dimension from 1 whose byte stride it gives; `Lower` and `Upper` take the spatial dimension of the
    * im2col corners.
    */
  enum Field derives CanEqual {
    case Address, Rank, Mode, Element, Tf32, Interleave, Swizzle, Atomicity, FillMode, Dimension, Stride, Box,
      Traversal,
      Channels, Pixels, Lower, Upper
  }

  /** The shared-memory offset a byte offset within a box lands at under a descriptor's swizzle and atomicity, given the
    * shared address the box starts at.
    */
  private inline def swizzled(swizzle: Int, atomicity: Int, start: Long, offset: Long): Long = {
    val address = start + offset
    val row     = address >>> 7
    val bits    = swizzle match {
      case 1 | 4 => MatrixDescriptor.swizzled(address, 32)
      case 2     => MatrixDescriptor.swizzled(address, 64)
      case 3     =>
        atomicity match {
          case 1 => address ^ ((row & 3L) << 5)
          case 2 => address ^ ((row & 3L) << 5) ^ ((row & 1L) << 3)
          case 3 => address ^ ((row & 1L) << 6)
          case _ => MatrixDescriptor.swizzled(address, 128)
        }
      case _ => address
    }
    bits - start
  }

  /** The shared offset of the chunk at `offset` of a box that starts at `start`, under the swizzle and its atomicity.
    */
  def swizzledOffset(swizzle: UInt[64], atomicity: UInt[64], start: UInt[64], offset: UInt[64]): UInt[64] =
    fromRaw[UInt[64]](swizzled(
      toRaw(swizzle).toInt,
      toRaw(atomicity).toInt,
      toRaw(start),
      toRaw(offset)
    ))

  extension [V](inline algebra: Algebra[V]) {

    /** The elements a box of `box` along a dimension holds when `traversal` thins it. */
    inline def elements(box: V, traversal: V): V =
      algebra.divide(
        algebra.subtract(algebra.add(box, traversal), algebra.literal(1L)),
        traversal,
        signed = true
      )

    /** `value` counted in units of `values` elements. */
    inline def counted(value: V, values: V): V =
      algebra.divide(value, values, signed = true)

    /** 1 where `coordinate` lies along a dimension of extent `dimension`. */
    inline def within(coordinate: V, dimension: V): V =
      algebra.and(
        algebra.lessOrEqual(algebra.literal(0L), coordinate, signed = true),
        algebra.lessThan(coordinate, dimension, signed = true)
      )

    /** 1 where an element at `coordinate` along dimension 0 lies inside the tensor, given whether its row does. */
    inline def inside(row: V, coordinate: V, dimension: V): V =
      algebra.and(row, algebra.within(coordinate, dimension))

    /** The coordinate along dimension 0 of element `column` of a row. */
    inline def coordinate(start: V, column: V, step: V): V =
      algebra.add(start, algebra.multiply(column, step))

    inline def address(base: V, coordinate: V, global: V): V =
      algebra.add(base, algebra.multiply(coordinate, global))

    /** The offset in the box of element `column` of row `row`, whose rows hold `columns` elements of `local` bytes. */
    inline def offset(row: V, columns: V, column: V, local: V): V =
      algebra.multiply(algebra.add(algebra.multiply(row, columns), column), local)

    /** The bytes a box chunk moves, 16 or 8 for the 8-byte-atomic 128-byte swizzle. */
    inline def chunk(swizzle: V, atomicity: V): V =
      algebra.select(
        algebra.and(
          algebra.equal(swizzle, algebra.literal(3L)),
          algebra.equal(atomicity, algebra.literal(2L))
        ),
        algebra.literal(8L),
        algebra.literal(16L)
      )

    /** The chunks of a box of `bytes`. */
    inline def chunks(bytes: V, chunk: V): V =
      algebra.divide(algebra.subtract(algebra.add(bytes, chunk), algebra.literal(1L)), chunk, signed = true)

    /** The bytes of the chunk at `at` of a box of `bytes`. */
    inline def length(bytes: V, at: V, chunk: V): V =
      algebra.minimum(chunk, algebra.subtract(bytes, at), signed = true)

    private inline def span(highest: V, low: V, interval: V): V =
      algebra.maximum(
        algebra.literal(1L),
        algebra.add(algebra.divide(algebra.subtract(highest, low), interval, signed = true), algebra.literal(1L)),
        signed = true
      )

    private inline def wraps(steps: V, first: V, period: V): V =
      algebra.select(
        algebra.lessThan(steps, first, signed = true),
        algebra.literal(0L),
        algebra.add(algebra.literal(1L), algebra.divide(algebra.subtract(steps, first), period, signed = true))
      )
  }

  /** The rows of a tile: four for a gather, otherwise the box elements of the dimensions from 1. */
  inline def rows[V](inline algebra: Algebra[V], gather: Boolean, rank: Int, inline count: Int => V): V =
    if (gather) algebra.literal(4L)
    else {
      var result = algebra.literal(1L)
      var axis   = 1
      while (axis < rank) {
        result = algebra.multiply(result, count(axis))
        axis += 1
      }
      result
    }

  /** `next` over the base address of tile row `row` and whether it lies inside the tensor. A gather row is the row of
    * its dimension 1 coordinate that the row index selects, and any other the box elements the row index gives along
    * dimensions 1 up, the first fastest.
    */
  inline def tileRow[V, R](
      inline algebra: Algebra[V],
      gather: Boolean,
      rank: Int,
      row: V,
      address: V,
      inline coordinate: Int => V,
      inline dimension: Int => V,
      inline stride: Int => V,
      inline traversal: Int => V,
      inline count: Int => V,
      inline bind: V => V
  )(inline next: (V, V) => R): R = {
    var base   = address
    var inside = algebra.literal(1L)
    if (gather) {
      val picked = bind(algebra.select(
        algebra.equal(row, algebra.literal(0L)),
        coordinate(1),
        algebra.select(
          algebra.equal(row, algebra.literal(1L)),
          coordinate(2),
          algebra.select(algebra.equal(row, algebra.literal(2L)), coordinate(3), coordinate(4))
        )
      ))
      inside = algebra.within(picked, dimension(1))
      base = algebra.add(base, algebra.multiply(picked, stride(1)))
    } else {
      var weight = algebra.literal(1L)
      var axis   = 1
      while (axis < rank) {
        val index = algebra.remainder(algebra.divide(row, weight, signed = true), count(axis), signed = true)
        val moved = bind(algebra.add(coordinate(axis), algebra.multiply(index, traversal(axis))))
        inside = if (axis == 1) algebra.within(moved, dimension(axis))
        else
          algebra.and(inside, algebra.within(moved, dimension(axis)))
        base = algebra.add(base, algebra.multiply(moved, stride(axis)))
        weight = algebra.multiply(weight, count(axis))
        axis += 1
      }
    }
    next(bind(base), bind(inside))
  }

  /** `next` over the pixels an im2col box walks, the pixels that follow them as their halo, and the start shift of the
    * first spatial dimension, given the pixels `mapped` the tensor map holds and the two offsets of the copy, which the
    * wide modes take as the halo and the shift.
    */
  inline def shape[V, R](inline algebra: Algebra[V], wide: V, wide128: V, mapped: V, first: V, second: V)(
      inline next: (V, V, V) => R
  ): R = {
    val halo = algebra.select(wide, first, algebra.literal(0L))
    next(
      algebra.select(wide128, algebra.literal(128L), mapped),
      algebra.select(wide128, algebra.multiply(halo, algebra.literal(4L)), halo),
      algebra.select(wide, second, algebra.literal(0L))
    )
  }

  /** `next` over the base address of im2col pixel `pixel` and whether it lies inside the tensor. The bounding box
    * advances along the first spatial dimension, or along all of them but the wide modes, wrapping each to its lower
    * corner past its upper and carrying to the next, and the last carry moves to the next image. The pixel `pixel` is
    * the step the box has walked to, except that the halo of `.w::128` interleaves with the pixels in blocks of 32.
    */
  inline def pixelRow[V, R](
      inline algebra: Algebra[V],
      rank: Int,
      wide: V,
      wide128: V,
      pixel: V,
      pixels: V,
      shift: V,
      address: V,
      inline coordinate: Int => V,
      inline dimension: Int => V,
      inline stride: Int => V,
      inline traversal: Int => V,
      inline lower: Int => V,
      inline upper: Int => V,
      inline offset: Int => V,
      inline bind: V => V
  )(inline next: (V, V) => R): R = {
    val spatial = rank - 2
    val beyond  = algebra.subtract(pixel, pixels)
    val walk    = bind(
      algebra.select(
        algebra.and(algebra.lessOrEqual(pixels, pixel, signed = true), wide128),
        algebra.add(
          algebra.multiply(
            algebra.literal(32L),
            algebra.add(algebra.remainder(beyond, algebra.literal(4L), signed = true), algebra.literal(1L))
          ),
          algebra.divide(beyond, algebra.literal(4L), signed = true)
        ),
        pixel
      )
    )
    var steps  = walk
    var carry  = walk
    var base   = address
    var inside = algebra.literal(1L)
    var axis   = 0
    while (axis < spatial) {
      val interval = traversal(axis + 1)
      val begin    = if (axis == 0) bind(algebra.add(coordinate(1), shift)) else coordinate(axis + 1)
      val lowest   = if (axis == 0) bind(algebra.add(lower(0), shift)) else lower(axis)
      val reach    = algebra.subtract(dimension(axis + 1), algebra.literal(1L))
      val highest  = bind(algebra.add(reach, if (axis == 0) algebra.add(upper(0), shift) else upper(axis)))
      val first    = bind(algebra.span(highest, begin, interval))
      val period   = bind(algebra.span(highest, lowest, interval))
      val advanced = if (axis == 0) steps else bind(algebra.select(wide, algebra.literal(0L), steps))
      val position = bind(
        algebra.select(
          algebra.lessThan(advanced, first, signed = true),
          algebra.add(begin, algebra.multiply(advanced, interval)),
          algebra.add(
            lowest,
            algebra.multiply(algebra.remainder(algebra.subtract(advanced, first), period, signed = true), interval)
          )
        )
      )
      val carried = bind(algebra.wraps(advanced, first, period))
      if (axis == 0) carry = carried
      steps = carried
      val adjust = bind(algebra.select(wide, algebra.literal(0L), offset(axis)))
      val placed = bind(algebra.add(position, adjust))
      inside =
        if (axis == 0) algebra.within(placed, dimension(axis + 1))
        else algebra.and(inside, algebra.within(placed, dimension(axis + 1)))
      base = algebra.add(base, algebra.multiply(placed, stride(axis + 1)))
      axis += 1
    }
    val moved = if (spatial > 1) bind(algebra.select(wide, carry, steps)) else steps
    val image = bind(algebra.add(coordinate(rank - 1), moved))
    next(
      bind(algebra.add(base, algebra.multiply(image, stride(rank - 1)))),
      bind(
        if (spatial == 0) algebra.within(image, dimension(rank - 1))
        else algebra.and(inside, algebra.within(image, dimension(rank - 1)))
      )
    )
  }

  /** The shared offset of the chunk at `at` of a box that starts at `start`. */
  inline def placed[V](inline algebra: Algebra[V], swizzle: V, atomicity: V, start: V, at: V): V =
    algebra.add(
      start,
      algebra.primitive(swizzle, atomicity, start, at)((a: UInt[64], b: UInt[64], c: UInt[64], d: UInt[64]) =>
        swizzledOffset(a, b, c, d)
      )
    )
}
