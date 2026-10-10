package net.kurobako.gemstone.isa.spirv

import Op.*
import VMInvocation.*
import VMLowering.*
import VMMemory.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*

/** SPV_INTEL_subgroups block reads and writes. Lane j of a subgroup accesses element j; vector component k accesses the
  * element k subgroup widths further on in a buffer, or the same byte column k rows down in an image.
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
  }

  private def shaped(site: Site, typeId: Int)(op: (Int in 1 ~ 8, Int) => Op): Op =
    site.program.integerShape(typeId) match {
      case Some((bits, lanes)) if bits.raw % 8 == 0 => op((bits.raw / 8).unsafe[Int in 1 ~ 8], lanes)
      case _ => Fail(Fault.InvalidRequest(s"Intel subgroup block data %$typeId is not an integer scalar or vector"))
    }

  private def lanesFrom(
      component: Int => AddressSpace.ReadResult[Long],
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

  private def eachLane(lanes: Int)(write: Int => Either[Fault, Unit])
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
      lanesFrom(component => memory.readScalar(element(execution, base, width.raw, component), width), lanes)
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
        memory.writeScalar(element(execution, base, width.raw, component), values(component), width)
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
          texel(execution, level, column, row + component, width.raw).fold[AddressSpace.ReadResult[Long]](Right(0L))(
            memory.readScalar(_, width)
          ),
        lanes
      )
    }.map(result => execution.values.update(dest, VMValue.Bits(resultType, result)))

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
        texel(execution, level, column, row + component, width.raw).fold[Either[Fault, Unit]](Right(()))(
          memory.writeScalar(_, values(component), width)
        )
      )
    } yield ()
}
