package net.kurobako.gemstone.isa.spirv

import Op.*
import VMInvocation.*
import VMMemory.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*

/** SPV_INTEL_subgroups block reads and writes. Lane j of a subgroup accesses element j; vector component k accesses the
  * element k subgroup widths further on in a buffer, or the same byte column k rows down in an image. The media block
  * and 2D block forms of SPV_INTEL_media_block_io and SPV_INTEL_2d_block_io assign elements by their extensions'
  * mappings, so each lane reaches its own elements without the others; prefetches only check their pointers.
  */
private object IntelBlockOps {
  import Operand.*

  def lower(site: Site): PartialFunction[Instruction, Op] = {
    case Instruction.SubgroupBlockReadINTEL(t, d, pointer) =>
      shaped(site, id(t))((width, lanes) =>
        step((execution, memory) => bufferRead(execution, memory, id(t), id(d), id(pointer), width, lanes))
      )
    case Instruction.SubgroupBlockWriteINTEL(pointer, data) =>
      shaped(site, site.program.valueType(id(data)))((width, lanes) =>
        step((execution, memory) => bufferWrite(execution, memory, id(pointer), id(data), width, lanes))
      )
    case Instruction.SubgroupImageBlockReadINTEL(t, d, image, coordinate) =>
      shaped(site, id(t))((width, lanes) =>
        step((execution, memory) => imageRead(execution, memory, id(t), id(d), id(image), id(coordinate), width, lanes))
      )
    case Instruction.SubgroupImageBlockWriteINTEL(image, coordinate, data) =>
      shaped(site, site.program.valueType(id(data)))((width, lanes) =>
        step((execution, memory) => imageWrite(execution, memory, id(image), id(coordinate), id(data), width, lanes))
      )
    case Instruction.SubgroupImageMediaBlockReadINTEL(t, d, image, coordinate, width, height) =>
      shaped(site, id(t))((bytes, lanes) =>
        step((execution, memory) =>
          mediaRead(
            execution,
            memory,
            resultType = id(t),
            dest = id(d),
            image = id(image),
            coordinate = id(coordinate),
            width = id(width),
            height = id(height),
            bytes = bytes,
            lanes = lanes
          )
        )
      )
    case Instruction.SubgroupImageMediaBlockWriteINTEL(image, coordinate, width, height, data) =>
      shaped(site, site.program.valueType(id(data)))((bytes, lanes) =>
        step((execution, memory) =>
          mediaWrite(
            execution,
            memory,
            image = id(image),
            coordinate = id(coordinate),
            width = id(width),
            height = id(height),
            data = id(data),
            bytes = bytes,
            lanes = lanes
          )
        )
      )
    case Instruction.SubgroupBlockPrefetchINTEL(pointer, _, _)              => prefetch(id(pointer))
    case Instruction.Subgroup2DBlockPrefetchINTEL(srcBasePointer = pointer) => prefetch(id(pointer))
    case Instruction.Subgroup2DBlockLoadINTEL(size, width, height, count, source, rowBytes, rows, pitch, at, local) =>
      block2D(Block2D.Plain, Vec(size, width, height, count, source, rowBytes, rows, pitch, at, local))
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
      block2D(
        Block2D.Transform,
        Vec(size, width, height, count, source, rowBytes, rows, pitch, at, local)
      )
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
      block2D(
        Block2D.Transpose,
        Vec(size, width, height, count, source, rowBytes, rows, pitch, at, local)
      )
    case Instruction.Subgroup2DBlockStoreINTEL(size, width, height, count, local, target, rowBytes, rows, pitch, at) =>
      block2D(Block2D.Store, Vec(size, width, height, count, target, rowBytes, rows, pitch, at, local))
  }

  private def power(value: Long): Long = if (value <= 1L) 1L else java.lang.Long.highestOneBit(value - 1L) << 1

  /** The block rows and columns, padded, that SPV_INTEL_2d_block_io assigns to invocations, and where each element of
    * the padded block lies in the unpadded block before any transform.
    */
  private enum Block2D derives CanEqual {
    case Plain, Transform, Transpose, Store
  }

  /** The position within the padded block, row-major, of value `k` of invocation `lane`: rows go to whole sets of lanes
    * while the padded width is at most the subgroup size, and runs of each row to each lane beyond it.
    */
  private def assigned(lane: Int, k: Int, columns: Long, subgroup: Int): (Long, Long) =
    if (columns <= subgroup) {
      val at = k.toLong * subgroup + lane
      (at / columns, at % columns)
    } else {
      val each = columns / subgroup
      (k / each, lane * each + k % each)
    }

  /** SPV_INTEL_2d_block_io's loads into, or store from, the invocation's own storage at `local`; an element outside the
    * memory region reads zero and is not written.
    */
  /** Over element size, block width, height and count, the region's base, width in bytes, height and pitch, the
    * coordinate and the lane's storage.
    */
  private def block2D(kind: Block2D, operands: Vec[Operand]): Op = {
    val ids = operands.map(id(_))
    step { (execution, memory) =>
      val values = execution.values
      for {
        size     <- scalar(values, ids(0))
        width    <- scalar(values, ids(1))
        height   <- scalar(values, ids(2))
        count    <- scalar(values, ids(3))
        region   <- address(values, ids(4))
        rowBytes <- scalar(values, ids(5))
        rows     <- scalar(values, ids(6))
        pitch    <- scalar(values, ids(7))
        at       <- bits(values, ids(8))
        target   <- address(values, ids(9))
        bytes    <- Either.cond(
          (size == 1L || size == 2L || size == 4L || size == 8L) && width > 0 && height > 0 && count > 0 &&
            at.length == 2 &&
            (kind != Block2D.Transform || size <= 2L),
          size.toInt.unsafe[Int in 1 ~ 8],
          Fault.InvalidRequest(s"2D block of $width x $height x $count elements of $size bytes is invalid")
        )
        _ <- Block2DTransfer(
          kind,
          region,
          at(0).toInt.toLong,
          at(1).toInt.toLong,
          bytes,
          width,
          height,
          rowBytes,
          rows,
          pitch
        )
          .run(memory, target, count, execution.invocation.subgroupLocal, execution.invocation.maxSubgroupSize)
      } yield ()
    }
  }

  /** The elements of a 2D block at column `x` and row `y` of a region of `rows` rows of `rowBytes` bytes, `pitch` bytes
    * apart; an element outside the region reads zero and is not written.
    */
  private class Block2DTransfer(
      kind: Block2D,
      region: Pointer,
      x: Long,
      y: Long,
      bytes: Int in 1 ~ 8,
      width: Long,
      height: Long,
      rowBytes: Long,
      rows: Long,
      pitch: Long
  ) {
    private val size = bytes.toLong

    private def inside(block: Long, row: Long, column: Long): Boolean = {
      val (r, c) = if (kind == Block2D.Transpose) (column, row) else (row, column)
      val byte   = (x + block * width + c) * size
      c < width && r < height && byte >= 0 && byte + size <= rowBytes && y + r >= 0 && y + r < rows
    }

    private def element(block: Long, row: Long, column: Long): Pointer = {
      val (r, c) = if (kind == Block2D.Transpose) (column, row) else (row, column)
      region.copy(address = region.address + (y + r) * pitch + (x + block * width + c) * size)
    }

    /** Loads into, or stores from, the lane's storage at `target`. */
    @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
    def run(
        memory: MemoryState,
        target: Pointer,
        count: Long,
        lane: Int,
        subgroup: Int
    ): AddressSpace.ReadResult[Unit] = {
      val store = kind == Block2D.Store
      // a transform packs 4 / size rows of one column into each 32-bit value
      val packing                   = if (kind == Block2D.Transform) 4 / bytes else 1
      val valueBytes: Int in 1 ~ 8  = if (kind == Block2D.Transform) 4 else bytes
      val (blockRows, blockColumns) = kind match {
        case Block2D.Transpose             => (width, power(height))
        case Block2D.Transform             => ((height + packing - 1) / packing, power(width))
        case Block2D.Plain | Block2D.Store => (height, power(width))
      }
      val perBlock = ((blockRows * blockColumns + subgroup - 1) / subgroup).toInt
      var failure: Fault | AddressSpace.Pending.type | Null = null
      var block                                             = 0L
      while (block < count && (failure eq null)) {
        var k = 0
        while (k < perBlock && (failure eq null)) {
          val (row, column) = assigned(lane, k, blockColumns, subgroup)
          if (row < blockRows) {
            val slot = target.copy(address = target.address + (block * perBlock + k) * valueBytes)
            if (store) memory.readScalar(slot, valueBytes) match {
              case Left(error)  => failure = error
              case Right(value) =>
                if (inside(block, row, column)) memory.writeScalar(element(block, row, column), value, bytes) match {
                  case Left(error) => failure = error
                  case Right(())   => ()
                }
            }
            else {
              var packed = 0L
              var part   = 0
              while (part < packing && (failure eq null)) {
                val at = row * packing + part
                if (inside(block, at, column)) memory.readScalar(element(block, at, column), bytes) match {
                  case Left(error)  => failure = error
                  case Right(value) => packed |= value << (part * size * 8)
                }
                part += 1
              }
              if (failure eq null) memory.writeScalar(slot, packed, valueBytes) match {
                case Left(error) => failure = error
                case Right(())   => ()
              }
            }
          }
          k += 1
        }
        block += 1
      }
      if (failure ne null) Left(failure) else Right(())
    }
  }

  private inline def shaped(site: Site, typeId: Int)(inline op: (Int in 1 ~ 8, Int) => Op): Op =
    site.program.integerShape(typeId) match {
      case Some((bits, lanes)) if bits % 8 == 0 => op((bits / 8).unsafe[Int in 1 ~ 8], lanes)
      case _ => Fail(Fault.InvalidRequest(s"Intel subgroup block data %$typeId is not an integer scalar or vector"))
    }

  private inline def lanesFrom(
      inline component: Int => AddressSpace.ReadResult[Long],
      lanes: Int
  ): AddressSpace.ReadResult[Array[Long]] = {
    val result                                                 = Array.ofDim[Long](lanes)
    def loop(index: Int): AddressSpace.ReadResult[Array[Long]] =
      if (index == lanes) Right(result)
      else component(index).flatMap { value =>
        result(index) = value
        loop(index + 1)
      }
    loop(0)
  }

  private inline def eachLane(lanes: Int)(inline write: Int => Either[Fault, Unit])
      : Either[Fault, Unit] = (0 until lanes).foldLeft[Either[Fault, Unit]](Right(()))((result, index) =>
    result.flatMap(_ => write(index))
  )

  private def element(execution: InvocationState, pointer: Pointer, width: Int, component: Int): Pointer = {
    val invocation = execution.invocation
    pointer.copy(address =
      pointer.address + (component.toLong * invocation.maxSubgroupSize + invocation.subgroupLocal) * width
    )
  }

  private def bufferRead(
      execution: InvocationState,
      memory: MemoryState,
      resultType: Int,
      dest: Int,
      pointer: Int,
      width: Int in 1 ~ 8,
      lanes: Int
  ): AddressSpace.ReadResult[Unit] =
    address(execution.values, pointer).flatMap(base =>
      lanesFrom(component => memory.readScalar(element(execution, base, width, component), width), lanes)
    ).map(result => execution.values.update(dest, VMValue.Bits(resultType, result)))

  private def bufferWrite(
      execution: InvocationState,
      memory: MemoryState,
      pointer: Int,
      data: Int,
      width: Int in 1 ~ 8,
      lanes: Int
  ): Either[Fault, Unit] =
    for {
      base   <- address(execution.values, pointer)
      values <- bits(execution.values, data)
      _      <- eachLane(lanes)(component =>
        memory.writeScalar(element(execution, base, width, component), values(component), width)
      )
    } yield ()

  /** The address of a lane's bytes in component `row` of a 2D image block, if they lie within the image. */
  private def texel(
      execution: InvocationState,
      level: ImageOps.ImageLevel,
      column: Long,
      row: Long,
      width: Int
  ): Option[Pointer] = {
    val texelBytes = ImageTexelCodec.texelBytes(level.format).toLong
    val byte       = column + execution.invocation.subgroupLocal.toLong * width
    Option.when(byte >= 0 && byte + width <= level.width * texelBytes && row >= 0 && row < level.height)(
      level.pixels.copy(address = level.pixels.address + row * level.stride * texelBytes + byte)
    )
  }

  private def imageBlock(execution: InvocationState, memory: MemoryState, image: Int, coordinate: Int)
      : AddressSpace.ReadResult[(ImageOps.ImageLevel, Long, Long)] =
    for {
      pointer <- address(execution.values, image)
      level   <- ImageOps.imageLevel(memory, pointer, 0)
      at      <- bits(execution.values, coordinate)
      _ <- Either.cond(at.length == 2, (), Fault.InvalidRequest("Intel image block coordinate must be two-dimensional"))
    } yield (level, at(0).toInt.toLong, at(1).toInt.toLong)

  private def imageRead(
      execution: InvocationState,
      memory: MemoryState,
      resultType: Int,
      dest: Int,
      image: Int,
      coordinate: Int,
      width: Int in 1 ~ 8,
      lanes: Int
  ): AddressSpace.ReadResult[Unit] =
    imageBlock(execution, memory, image, coordinate).flatMap { (level, column, row) =>
      lanesFrom(
        component =>
          texel(execution, level, column, row + component, width).fold[AddressSpace.ReadResult[Long]](Right(0L))(
            memory.readScalar(_, width)
          ),
        lanes
      )
    }.map(result => execution.values.update(dest, VMValue.Bits(resultType, result)))

  /** The byte column and row of each component of a lane's media block value: the region's rows, each padded to a power
    * of two of components, run row-major through the subgroup's values, components a subgroup apart.
    */
  private def media(
      execution: InvocationState,
      memory: MemoryState,
      image: Int,
      coordinate: Int,
      width: Int,
      height: Int,
      bytes: Int,
      lanes: Int
  ): AddressSpace.ReadResult[Vec[Option[Pointer]]] =
    for {
      block   <- imageBlock(execution, memory, image, coordinate)
      columns <- scalar(execution.values, width)
      rows    <- scalar(execution.values, height)
    } yield {
      val (level, column, row) = block
      val padded               = power(columns)
      val texelBytes           = ImageTexelCodec.texelBytes(level.format).toLong
      val invocation           = execution.invocation
      Vec.tabulate(lanes) { component =>
        val at     = component.toLong * invocation.maxSubgroupSize + invocation.subgroupLocal
        val (r, c) = (at / padded, at % padded)
        val byte   = column + c * bytes
        val y      = row + r
        Option.when(
          r < rows && c < columns && byte >= 0 && byte + bytes <= level.width * texelBytes && y >= 0 &&
            y < level.height
        )(level.pixels.copy(address = level.pixels.address + y * level.stride * texelBytes + byte))
      }
    }

  private def mediaRead(
      execution: InvocationState,
      memory: MemoryState,
      resultType: Int,
      dest: Int,
      image: Int,
      coordinate: Int,
      width: Int,
      height: Int,
      bytes: Int in 1 ~ 8,
      lanes: Int
  ): AddressSpace.ReadResult[Unit] =
    media(execution, memory, image, coordinate, width, height, bytes, lanes).flatMap(places =>
      lanesFrom(
        component => places(component).fold[AddressSpace.ReadResult[Long]](Right(0L))(memory.readScalar(_, bytes)),
        lanes
      )
    ).map(result => execution.values.update(dest, VMValue.Bits(resultType, result)))

  private def mediaWrite(
      execution: InvocationState,
      memory: MemoryState,
      image: Int,
      coordinate: Int,
      width: Int,
      height: Int,
      data: Int,
      bytes: Int in 1 ~ 8,
      lanes: Int
  ): AddressSpace.ReadResult[Unit] =
    for {
      places <- media(execution, memory, image, coordinate, width, height, bytes, lanes)
      values <- bits(execution.values, data)
      _      <- eachLane(lanes)(component =>
        places(component).fold[Either[Fault, Unit]](Right(()))(memory.writeScalar(_, values(component), bytes))
      )
    } yield ()

  private def imageWrite(
      execution: InvocationState,
      memory: MemoryState,
      image: Int,
      coordinate: Int,
      data: Int,
      width: Int in 1 ~ 8,
      lanes: Int
  ): AddressSpace.ReadResult[Unit] =
    for {
      block  <- imageBlock(execution, memory, image, coordinate)
      values <- bits(execution.values, data)
      (level, column, row) = block
      _ <- eachLane(lanes)(component =>
        texel(execution, level, column, row + component, width).fold[Either[Fault, Unit]](Right(()))(
          memory.writeScalar(_, values(component), width)
        )
      )
    } yield ()
}
