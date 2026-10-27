package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*
import net.kurobako.gemstone.semantics.render.*

/** SPV_INTEL_subgroups, SPV_INTEL_media_block_io and SPV_INTEL_2d_block_io transfers, as requests to a
  * [[SPIRVMachine]]. An element outside the image level or the 2D region reads zero and is not written. A fault from an
  * access the request makes stops it.
  */
private object IntelBlockSemantics {

  /** Which way a 2D block moves: packed loads, plain loads, transposed loads or stores. */
  enum Block2D derives CanEqual {
    case Plain, Transform, Transpose, Store
  }

  def power(value: UInt[64]): UInt[64] = fromRaw[UInt[64]](powerOf(toRaw(value)))

  def transformPacking(size: UInt[64]): UInt[64] = fromRaw[UInt[64]](4L / toRaw(size))

  def texelBytes(format: UInt[64]): UInt[64] =
    fromRaw[UInt[64]](ImageTexelCodec.texelBytes(toRaw(format).toInt).toLong)

  private def powerOf(value: Long): Long = if (value <= 1L) 1L else java.lang.Long.highestOneBit(value - 1L) << 1

  private inline def partialAdvance[V, D, F[_]](
      inline algebra: Algebra[V],
      inline machine: SPIRVMachine[V, D, F]
  )(lanes: Int, dest: Int, resultType: Int): F[Unit] =
    machine.eachLeaf(Array.empty[Int], lanes, dest, resultType)(index =>
      machine.partial(algebra.literal(index.toLong))
    )(machine.advance)

  private inline def mediaBounds[V, D, F[_]](
      inline algebra: Algebra[V],
      inline machine: SPIRVMachine[V, D, F]
  )(coordinate: Int, bytes: Int in 1 ~ 8, columns: V): (V, V, V, V) = (
    algebra.signExtend(machine.leaf(coordinate, 0), 32),
    algebra.signExtend(machine.leaf(coordinate, 1), 32),
    algebra.literal(bytes.raw.toLong),
    algebra.primitive(columns)(power(_: UInt[64]))
  )

  /** Partial result `lane` of `count` is the `bytes`-byte texel `offset(lane)` bytes into the bound level where
    * `inside(lane)` holds, and zero elsewhere, then `next`.
    */
  inline def levelLoad[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      count: Int,
      bytes: Int in 1 ~ 8
  )(inline inside: V => V)(inline offset: V => V)(inline next: => F[Unit]): F[Unit] =
    machine.repeat(algebra.literal(count.toLong))(lane =>
      machine.when(inside(lane))(
        machine.loadLevel(offset(lane), algebra.literal(bytes.raw.toLong))(value =>
          machine.setPartial(lane, value)(machine.done)
        )
      )(machine.done)
    )(next)

  /** Leaf `lane` of `source` is written `bytes` bytes into the bound level at `offset(lane)` where `inside(lane)`
    * holds, then `next`.
    */
  inline def levelStore[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      count: Int,
      bytes: Int in 1 ~ 8,
      source: Int
  )(inline inside: V => V)(inline offset: V => V)(inline next: => F[Unit]): F[Unit] =
    machine.repeat(algebra.literal(count.toLong))(lane =>
      machine.when(inside(lane))(
        machine.storeLevel(offset(lane), algebra.literal(bytes.raw.toLong), machine.leafAt(Array(source), 1, lane))(
          machine.done
        )
      )(machine.done)
    )(next)

  /** The byte offset of the texel at `column` of `row`, in rows `stride` texels apart of `texel` bytes each. */
  inline def texelOffset[V](inline algebra: Algebra[V])(row: V, column: V, stride: V, texel: V): V =
    algebra.add(algebra.multiply(algebra.multiply(row, stride), texel), column)

  /** Whether the `size`-byte element at `column` of `row` lies within a level of `width` by `height` texels of `texel`
    * bytes.
    */
  inline def texelInside[V](inline algebra: Algebra[V])(column: V, row: V, size: V, width: V, height: V, texel: V): V =
    algebra.and(
      algebra.and(
        algebra.lessOrEqual(algebra.literal(0L), column, true),
        algebra.lessOrEqual(algebra.add(column, size), algebra.multiply(width, texel), true)
      ),
      algebra.and(algebra.lessOrEqual(algebra.literal(0L), row, true), algebra.lessThan(row, height, true))
    )

  /** Binds the level of `image`, whose request writes its width, height, row stride and format after `lanes` partial
    * results, and passes the width, height, stride and the texel bytes of the format to `next`.
    */
  inline def levelBinding[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      image: Int,
      lanes: Int
  )(inline next: (V, V, V, V) => F[Unit]): F[Unit] =
    machine.partials(lanes + 7)(
      machine.level(image, algebra.literal(0L), lanes)(
        next(
          machine.partial(algebra.literal(lanes.toLong)),
          machine.partial(algebra.literal(lanes + 1L)),
          machine.partial(algebra.literal(lanes + 5L)),
          algebra.primitive(machine.partial(algebra.literal(lanes + 6L)))(texelBytes(_: UInt[64]))
        )
      )
    )

  inline def imageRead[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      resultType: Int,
      dest: Int,
      image: Int,
      coordinate: Int,
      bytes: Int in 1 ~ 8,
      lanes: Int
  ): F[Unit] = machine.numeric(coordinate)(
    levelBinding(algebra, machine)(image, lanes) { (width, height, stride, texel) =>
      val column = algebra.signExtend(machine.leaf(coordinate, 0), 32)
      val row    = algebra.signExtend(machine.leaf(coordinate, 1), 32)
      val size   = algebra.literal(bytes.raw.toLong)
      val origin = algebra.add(column, algebra.multiply(machine.subgroupLocal, size))
      levelLoad(algebra, machine)(lanes, bytes)(lane =>
        texelInside(algebra)(origin, algebra.add(row, lane), size, width, height, texel)
      )(lane => texelOffset(algebra)(algebra.add(row, lane), origin, stride, texel))(
        partialAdvance(algebra, machine)(lanes, dest, resultType)
      )
    }
  )

  inline def imageWrite[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      image: Int,
      coordinate: Int,
      data: Int,
      bytes: Int in 1 ~ 8,
      lanes: Int
  ): F[Unit] = machine.numeric(coordinate)(
    machine.numeric(data)(
      levelBinding(algebra, machine)(image, 0) { (width, height, stride, texel) =>
        val column = algebra.signExtend(machine.leaf(coordinate, 0), 32)
        val row    = algebra.signExtend(machine.leaf(coordinate, 1), 32)
        val size   = algebra.literal(bytes.raw.toLong)
        val origin = algebra.add(column, algebra.multiply(machine.subgroupLocal, size))
        levelStore(algebra, machine)(lanes, bytes, data)(lane =>
          texelInside(algebra)(origin, algebra.add(row, lane), size, width, height, texel)
        )(lane => texelOffset(algebra)(algebra.add(row, lane), origin, stride, texel))(machine.advance)
      }
    )
  )

  /** The position of the subgroup's value for component `lane` of a media block: component `lane` of the block is the
    * `lane * subgroupSize + subgroupLocal`th element, laid out row-major over rows of `padded` elements.
    */
  inline def mediaAt[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(lane: V): V =
    algebra.add(algebra.multiply(lane, machine.subgroupSize), machine.subgroupLocal)

  inline def mediaInside[V](inline algebra: Algebra[V])(
      at: V,
      padded: V,
      columns: V,
      rows: V,
      column: V,
      row: V,
      size: V,
      width: V,
      height: V,
      texel: V
  ): V = {
    val r = algebra.divide(at, padded, true)
    val c = algebra.remainder(at, padded, true)
    algebra.and(
      algebra.and(algebra.lessThan(r, rows, true), algebra.lessThan(c, columns, true)),
      texelInside(algebra)(
        algebra.add(column, algebra.multiply(c, size)),
        algebra.add(row, r),
        size,
        width,
        height,
        texel
      )
    )
  }

  inline def mediaOffset[V](inline algebra: Algebra[V])(
      at: V,
      padded: V,
      column: V,
      row: V,
      size: V,
      stride: V,
      texel: V
  ): V = {
    val r = algebra.divide(at, padded, true)
    val c = algebra.remainder(at, padded, true)
    texelOffset(algebra)(algebra.add(row, r), algebra.add(column, algebra.multiply(c, size)), stride, texel)
  }

  inline def mediaRead[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      resultType: Int,
      dest: Int,
      image: Int,
      coordinate: Int,
      width: Int,
      height: Int,
      bytes: Int in 1 ~ 8,
      lanes: Int
  ): F[Unit] = machine.numeric(coordinate)(
    machine.scalar(width)(columns =>
      machine.scalar(height)(rows =>
        levelBinding(algebra, machine)(image, lanes) { (levelWidth, levelHeight, stride, texel) =>
          val (column, row, size, padded) = mediaBounds(algebra, machine)(coordinate, bytes, columns)
          levelLoad(algebra, machine)(lanes, bytes)(lane =>
            mediaInside(algebra)(
              mediaAt(algebra, machine)(lane),
              padded,
              columns,
              rows,
              column,
              row,
              size,
              levelWidth,
              levelHeight,
              texel
            )
          )(lane => mediaOffset(algebra)(mediaAt(algebra, machine)(lane), padded, column, row, size, stride, texel))(
            partialAdvance(algebra, machine)(lanes, dest, resultType)
          )
        }
      )
    )
  )

  inline def mediaWrite[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      image: Int,
      coordinate: Int,
      width: Int,
      height: Int,
      data: Int,
      bytes: Int in 1 ~ 8,
      lanes: Int
  ): F[Unit] = machine.numeric(coordinate)(
    machine.numeric(data)(
      machine.scalar(width)(columns =>
        machine.scalar(height)(rows =>
          levelBinding(algebra, machine)(image, 0) { (levelWidth, levelHeight, stride, texel) =>
            val (column, row, size, padded) = mediaBounds(algebra, machine)(coordinate, bytes, columns)
            levelStore(algebra, machine)(lanes, bytes, data)(lane =>
              mediaInside(algebra)(
                mediaAt(algebra, machine)(lane),
                padded,
                columns,
                rows,
                column,
                row,
                size,
                levelWidth,
                levelHeight,
                texel
              )
            )(lane => mediaOffset(algebra)(mediaAt(algebra, machine)(lane), padded, column, row, size, stride, texel))(
              machine.advance
            )
          }
        )
      )
    )
  )

  /** The row and column of the element `k` of invocation `lane` in a padded block `columns` wide. A block of at most
    * `subgroup` columns gives each row to a whole set of invocations, and a wider one gives each invocation a run of
    * each row.
    */
  inline def blockRow[V](inline algebra: Algebra[V])(lane: V, k: V, columns: V, subgroup: V): V =
    algebra.select(
      algebra.lessOrEqual(columns, subgroup, true),
      algebra.divide(algebra.add(algebra.multiply(k, subgroup), lane), columns, true),
      algebra.divide(k, algebra.divide(columns, subgroup, true), true)
    )

  inline def blockColumn[V](inline algebra: Algebra[V])(lane: V, k: V, columns: V, subgroup: V): V =
    algebra.select(
      algebra.lessOrEqual(columns, subgroup, true),
      algebra.remainder(algebra.add(algebra.multiply(k, subgroup), lane), columns, true),
      algebra.add(
        algebra.multiply(lane, algebra.divide(columns, subgroup, true)),
        algebra.remainder(k, algebra.divide(columns, subgroup, true), true)
      )
    )

  /** The byte offset of the element at `row` and `column` of block `block` from the region's address, where the region
    * starts at column `x` and row `y`, is `width` elements wide and `pitch` bytes per row.
    */
  inline def blockElement[V](inline algebra: Algebra[V])(transposed: Boolean)(
      block: V,
      row: V,
      column: V,
      x: V,
      y: V,
      width: V,
      size: V,
      pitch: V
  ): V = {
    val r = if (transposed) column else row
    val c = if (transposed) row else column
    algebra.add(
      algebra.multiply(algebra.add(y, r), pitch),
      algebra.multiply(algebra.add(x, algebra.add(algebra.multiply(block, width), c)), size)
    )
  }

  /** Whether the element at `row` and `column` of block `block` lies in the region of `rows` rows of `rowBytes` bytes.
    */
  inline def blockInside[V](inline algebra: Algebra[V])(transposed: Boolean)(
      block: V,
      row: V,
      column: V,
      x: V,
      y: V,
      width: V,
      height: V,
      size: V,
      rowBytes: V,
      rows: V
  ): V = {
    val r    = if (transposed) column else row
    val c    = if (transposed) row else column
    val byte = algebra.multiply(algebra.add(x, algebra.add(algebra.multiply(block, width), c)), size)
    algebra.and(
      algebra.and(algebra.lessThan(c, width, true), algebra.lessThan(r, height, true)),
      algebra.and(
        algebra.and(
          algebra.lessOrEqual(algebra.literal(0L), byte, true),
          algebra.lessOrEqual(algebra.add(byte, size), rowBytes, true)
        ),
        algebra.and(
          algebra.lessOrEqual(algebra.literal(0L), algebra.add(y, r), true),
          algebra.lessThan(algebra.add(y, r), rows, true)
        )
      )
    )
  }

  /** A 2D block load or store of SPV_INTEL_2d_block_io, between the region at `region` and the invocation's storage at
    * `local`. Each invocation moves the elements that [[blockRow]] and [[blockColumn]] assign it. A transform packs
    * `4 / size` rows of one column into each 32-bit word.
    */
  inline def block2D[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      kind: Block2D,
      size: Int,
      width: Int,
      height: Int,
      count: Int,
      region: Int,
      rowBytes: Int,
      rows: Int,
      pitch: Int,
      at: Int,
      local: Int
  ): F[Unit] = machine.scalar(size)(bytes =>
    machine.scalar(width)(w =>
      machine.scalar(height)(h =>
        machine.scalar(count)(blocks =>
          machine.scalar(rowBytes)(rb =>
            machine.scalar(rows)(rs =>
              machine.scalar(pitch)(p =>
                machine.numeric(at)(
                  machine.partials(1) {
                    val x          = algebra.signExtend(machine.leaf(at, 0), 32)
                    val y          = algebra.signExtend(machine.leaf(at, 1), 32)
                    val subgroup   = machine.subgroupSize
                    val lane       = machine.subgroupLocal
                    val transposed = kind == Block2D.Transpose
                    val packed     = kind == Block2D.Transform
                    val store      = kind == Block2D.Store
                    val packing    =
                      if (packed) algebra.primitive(bytes)(transformPacking(_: UInt[64])) else algebra.literal(1L)
                    val valueBytes = if (packed) algebra.literal(4L) else bytes
                    val blockRows  = kind match {
                      case Block2D.Transpose => w
                      case Block2D.Transform =>
                        algebra.divide(algebra.add(h, algebra.subtract(packing, algebra.literal(1L))), packing, false)
                      case _ => h
                    }
                    val blockColumns = algebra.primitive(if (transposed) h else w)(power(_: UInt[64]))
                    val perBlock     = algebra.divide(
                      algebra.add(
                        algebra.multiply(blockRows, blockColumns),
                        algebra.subtract(subgroup, algebra.literal(1L))
                      ),
                      subgroup,
                      false
                    )
                    machine.repeat(blocks)(block =>
                      machine.repeat(perBlock) { k =>
                        val row    = blockRow(algebra)(lane, k, blockColumns, subgroup)
                        val column = blockColumn(algebra)(lane, k, blockColumns, subgroup)
                        val slot   = algebra.multiply(algebra.add(algebra.multiply(block, perBlock), k), valueBytes)
                        machine.when(algebra.lessThan(row, blockRows, true))(
                          if (store)
                            machine.loadElement(local, slot, valueBytes)(v =>
                              machine.when(
                                blockInside(algebra)(transposed)(block, row, column, x, y, w, h, bytes, rb, rs)
                              )(
                                machine.storeElement(
                                  region,
                                  blockElement(algebra)(transposed)(block, row, column, x, y, w, bytes, p),
                                  bytes,
                                  v
                                )(machine.done)
                              )(machine.done)
                            )
                          else
                            machine.setPartial(algebra.literal(0L), algebra.literal(0L))(
                              machine.repeat(algebra.literal(if (packed) 4L else 1L)) { part =>
                                val rowAt = algebra.add(algebra.multiply(row, packing), part)
                                machine.when(algebra.lessThan(part, packing, true))(
                                  machine.when(
                                    blockInside(algebra)(transposed)(block, rowAt, column, x, y, w, h, bytes, rb, rs)
                                  )(
                                    machine.loadElement(
                                      region,
                                      blockElement(algebra)(transposed)(block, rowAt, column, x, y, w, bytes, p),
                                      bytes
                                    )(v =>
                                      machine.setPartial(
                                        algebra.literal(0L),
                                        algebra.or(
                                          machine.partial(algebra.literal(0L)),
                                          algebra.shiftLeft(
                                            v,
                                            algebra.multiply(algebra.multiply(part, bytes), algebra.literal(8L))
                                          )
                                        )
                                      )(machine.done)
                                    )
                                  )(machine.done)
                                )(machine.done)
                              }(
                                machine.storeElement(local, slot, valueBytes, machine.partial(algebra.literal(0L)))(
                                  machine.done
                                )
                              )
                            )
                        )(machine.done)
                      }(machine.done)
                    )(machine.advance)
                  }
                )
              )
            )
          )
        )
      )
    )
  )

  def render(instruction: Instruction, machine: SPIRVMachineRender, bytes: Int): Option[Term.Steps] = {
    def element = bytes.unsafe[Int in 1 ~ 8]
    instruction match {
      case Instruction.SubgroupImageBlockReadINTEL(t, d, image, coordinate) =>
        Some(renderImageRead(machine, Operand.id(t), Operand.id(d), Operand.id(image), Operand.id(coordinate), element))
      case Instruction.SubgroupImageBlockWriteINTEL(image, coordinate, data) =>
        Some(renderImageWrite(machine, Operand.id(image), Operand.id(coordinate), Operand.id(data), element))
      case Instruction.SubgroupImageMediaBlockReadINTEL(t, d, image, coordinate, width, height) =>
        Some(renderMediaRead(
          machine,
          Vec(
            Operand.id(t),
            Operand.id(d),
            Operand.id(image),
            Operand.id(coordinate),
            Operand.id(width),
            Operand.id(height)
          ),
          element
        ))
      case Instruction.SubgroupImageMediaBlockWriteINTEL(image, coordinate, width, height, data) =>
        Some(renderMediaWrite(
          machine,
          Vec(Operand.id(image), Operand.id(coordinate), Operand.id(width), Operand.id(height), Operand.id(data)),
          element
        ))
      case Instruction.Subgroup2DBlockLoadINTEL(size, width, height, count, source, rowBytes, rows, pitch, at, local) =>
        Some(renderBlock2D(
          machine,
          Block2D.Plain,
          Vec(size, width, height, count, source, rowBytes, rows, pitch, at, local)
        ))
      case Instruction.Subgroup2DBlockLoadTransformINTEL(
            size,
            width,
            height,
            count,
            source,
            rowBytes,
            rows,
            pitch,
            at,
            local
          ) =>
        Some(renderBlock2D(
          machine,
          Block2D.Transform,
          Vec(size, width, height, count, source, rowBytes, rows, pitch, at, local)
        ))
      case Instruction.Subgroup2DBlockLoadTransposeINTEL(
            size,
            width,
            height,
            count,
            source,
            rowBytes,
            rows,
            pitch,
            at,
            local
          ) =>
        Some(renderBlock2D(
          machine,
          Block2D.Transpose,
          Vec(size, width, height, count, source, rowBytes, rows, pitch, at, local)
        ))
      case Instruction.Subgroup2DBlockStoreINTEL(
            size,
            width,
            height,
            count,
            local,
            target,
            rowBytes,
            rows,
            pitch,
            at
          ) =>
        Some(renderBlock2D(
          machine,
          Block2D.Store,
          Vec(size, width, height, count, target, rowBytes, rows, pitch, at, local)
        ))
      case _ => None
    }
  }

  // each form renders in its own method to keep every method under the JIT's size limit
  private def renderImageRead(
      machine: SPIRVMachineRender,
      t: Int,
      d: Int,
      image: Int,
      coordinate: Int,
      element: Int in 1 ~ 8
  ): Term.Steps = imageRead(Render, machine)(t, d, image, coordinate, element, machine.leafCount(d))

  private def renderImageWrite(
      machine: SPIRVMachineRender,
      image: Int,
      coordinate: Int,
      data: Int,
      element: Int in 1 ~ 8
  ): Term.Steps = imageWrite(Render, machine)(image, coordinate, data, element, machine.leafCount(data))

  private def renderMediaRead(machine: SPIRVMachineRender, ids: Vec[Int], element: Int in 1 ~ 8): Term.Steps =
    mediaRead(Render, machine)(ids(0), ids(1), ids(2), ids(3), ids(4), ids(5), element, machine.leafCount(ids(1)))

  private def renderMediaWrite(machine: SPIRVMachineRender, ids: Vec[Int], element: Int in 1 ~ 8): Term.Steps =
    mediaWrite(Render, machine)(ids(0), ids(1), ids(2), ids(3), ids(4), element, machine.leafCount(ids(4)))

  private def renderBlock2D(machine: SPIRVMachineRender, mode: Block2D, operands: Vec[Operand]): Term.Steps = {
    val ids = operands.map(Operand.id)
    block2D(Render, machine)(mode, ids(0), ids(1), ids(2), ids(3), ids(4), ids(5), ids(6), ids(7), ids(8), ids(9))
  }
}
