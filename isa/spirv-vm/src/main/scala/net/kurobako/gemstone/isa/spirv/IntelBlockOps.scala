package net.kurobako.gemstone.isa.spirv

import MemoryOps.*
import Op.*
import VMInvocation.*
import VMMemory.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.vm.*

/** SPV_INTEL_subgroups block reads and writes. Lane j of a subgroup accesses element j; vector component k accesses the
  * element k subgroup widths further on in a buffer. The image, media and 2D block forms of SPV_INTEL_subgroups,
  * SPV_INTEL_media_block_io and SPV_INTEL_2d_block_io are requests in [[IntelBlockSemantics]]; prefetches only check
  * their pointers.
  */
private object IntelBlockOps {
  import IntelBlockSemantics.*
  import Operand.*

  def lower(site: Site): PartialFunction[Instruction, Op] = {
    case Instruction.SubgroupBlockReadINTEL(t, d, pointer) =>
      shaped(site, id(t))((width, lanes) => BlockRead(id(pointer), width, lanes, id(d), id(t)))
    case Instruction.SubgroupBlockWriteINTEL(pointer, data) =>
      shaped(site, site.program.valueType(id(data)))((width, lanes) => BlockWrite(id(pointer), id(data), width, lanes))
    case Instruction.SubgroupImageBlockReadINTEL(t, d, image, coordinate) =>
      shaped(site, id(t))((width, lanes) => ImageBlockRead(id(t), id(d), id(image), id(coordinate), width, lanes))
    case Instruction.SubgroupImageBlockWriteINTEL(image, coordinate, data) =>
      shaped(site, site.program.valueType(id(data)))((width, lanes) =>
        ImageBlockWrite(id(image), id(coordinate), id(data), width, lanes)
      )
    case Instruction.SubgroupImageMediaBlockReadINTEL(t, d, image, coordinate, width, height) =>
      shaped(site, id(t))((bytes, lanes) =>
        MediaBlockRead(
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
    case Instruction.SubgroupImageMediaBlockWriteINTEL(image, coordinate, width, height, data) =>
      shaped(site, site.program.valueType(id(data)))((bytes, lanes) =>
        MediaBlockWrite(
          image = id(image),
          coordinate = id(coordinate),
          width = id(width),
          height = id(height),
          data = id(data),
          bytes = bytes,
          lanes = lanes
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

  private def block2D(kind: Block2D, operands: Vec[Operand]): Op = Block2DOp(kind, operands.map(id(_)))

  private inline def shaped(site: Site, typeId: Int)(inline op: (Int in 1 ~ 8, Int) => Op): Op =
    site.program.integerShape(typeId) match {
      case Some((bits, lanes)) if bits % 8 == 0 => op((bits / 8).unsafe[Int in 1 ~ 8], lanes)
      case _ => Fail(Fault.InvalidRequest(s"Intel subgroup block data %$typeId is not an integer scalar or vector"))
    }

  private class BlockRead(pointer: Int, width: Int in 1 ~ 8, lanes: Int, target: Int, typeId: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      MemoryAccessSemantics.blockLoad(Execute, InvocationMachine(execution))(pointer, width, lanes, target, typeId)
  }

  private class BlockWrite(pointer: Int, data: Int, width: Int in 1 ~ 8, lanes: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      MemoryAccessSemantics.blockStore(Execute, InvocationMachine(execution))(pointer, data, width, lanes)
  }

  /** The image is a level the VM can bind, and its coordinate is two-dimensional, as the image block forms check them.
    */
  private def imageBlock(execution: InvocationState, memory: MemoryState, image: Int, coordinate: Int)
      : AddressSpace.ReadResult[Unit] =
    for {
      pointer <- address(execution.values, image)
      _       <- ImageOps.imageLevel(memory, pointer, 0)
      at      <- bits(execution.values, coordinate)
      _ <- Either.cond(at.length == 2, (), Fault.InvalidRequest("Intel image block coordinate must be two-dimensional"))
    } yield ()

  private class ImageBlockRead(resultType: Int, dest: Int, image: Int, coordinate: Int, width: Int in 1 ~ 8, lanes: Int)
      extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      imageBlock(execution, memory, image, coordinate).flatMap(_ =>
        IntelBlockSemantics.imageRead(Execute, InvocationMachine(execution))(
          resultType,
          dest,
          image,
          coordinate,
          width,
          lanes
        )
      )
  }

  private class ImageBlockWrite(image: Int, coordinate: Int, data: Int, width: Int in 1 ~ 8, lanes: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      imageBlock(execution, memory, image, coordinate).flatMap(_ =>
        IntelBlockSemantics.imageWrite(Execute, InvocationMachine(execution))(image, coordinate, data, width, lanes)
      )
  }

  private class MediaBlockRead(
      resultType: Int,
      dest: Int,
      image: Int,
      coordinate: Int,
      width: Int,
      height: Int,
      bytes: Int in 1 ~ 8,
      lanes: Int
  ) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      imageBlock(execution, memory, image, coordinate).flatMap(_ =>
        IntelBlockSemantics.mediaRead(Execute, InvocationMachine(execution))(
          resultType,
          dest,
          image,
          coordinate,
          width,
          height,
          bytes,
          lanes
        )
      )
  }

  private class MediaBlockWrite(
      image: Int,
      coordinate: Int,
      width: Int,
      height: Int,
      data: Int,
      bytes: Int in 1 ~ 8,
      lanes: Int
  ) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      imageBlock(execution, memory, image, coordinate).flatMap(_ =>
        IntelBlockSemantics.mediaWrite(Execute, InvocationMachine(execution))(
          image,
          coordinate,
          width,
          height,
          data,
          bytes,
          lanes
        )
      )
  }

  /** `operands` are the element size, block width, height and count, the region, the row bytes, rows and pitch of the
    * region, the block origin and the invocation's storage, in that order.
    */
  private class Block2DOp(kind: Block2D, operands: Vec[Int]) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = {
      val values = execution.values
      for {
        size   <- scalar(values, operands(0))
        width  <- scalar(values, operands(1))
        height <- scalar(values, operands(2))
        count  <- scalar(values, operands(3))
        _      <- address(values, operands(4))
        _      <- scalar(values, operands(5))
        _      <- scalar(values, operands(6))
        _      <- scalar(values, operands(7))
        at     <- bits(values, operands(8))
        _      <- address(values, operands(9))
        _      <- Either.cond(
          (size == 1L || size == 2L || size == 4L || size == 8L) && width > 0 && height > 0 && count > 0 &&
            at.length == 2 &&
            (kind != Block2D.Transform || size <= 2L),
          (),
          Fault.InvalidRequest(s"2D block of $width x $height x $count elements of $size bytes is invalid")
        )
        _ <- IntelBlockSemantics.block2D(Execute, InvocationMachine(execution))(
          kind = kind,
          size = operands(0),
          width = operands(1),
          height = operands(2),
          count = operands(3),
          region = operands(4),
          rowBytes = operands(5),
          rows = operands(6),
          pitch = operands(7),
          at = operands(8),
          local = operands(9)
        )
      } yield ()
    }
  }
}
