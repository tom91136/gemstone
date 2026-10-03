package net.kurobako.gemstone.isa.spirv.v1_6.graphics

import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.graphics.GraphicsProtocol.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import scala.collection.mutable

private object GraphicsAccess {
  case class Failure(fault: Fault) extends RuntimeException(fault.message)
  def get[A](value: Either[Fault, A]): A = value.fold(error => throw Failure(error), identity)
  def scoped[A](memory: Memory, control: TaskControl)(execute: GraphicsAccess => Either[Fault, A]): Either[Fault, A] = {
    val access = GraphicsAccess(memory, control)
    try execute(access)
    catch {
      case Failure(fault)                  => Left(fault)
      case error: IllegalArgumentException => Left(Fault.InvalidTask(throwables.detail(error)))
      case error: ArithmeticException      => Left(Fault.InvalidTask(throwables.detail(error)))
    } finally access.releaseScratch()
  }

}

/** Allocation-bounded host access and draw-local invocation storage. The same memory adapter is passed to shaders and
  * fixed-function operations, preserving aliases and completion ordering.
  */
private class GraphicsAccess(val backing: Memory, val control: TaskControl) extends Memory
    with AddressSpace {
  private val local = mutable.ArrayBuffer.empty[Array[Byte]]
  // RPC allocation aliases are positive. Keep shader interface addresses in the
  // negative half, away from both native aliases and VM exception tokens at Long.MinValue.
  private val ScratchBase                = Long.MinValue / 2
  private val ScratchStride              = 0x100000000L
  private def addressSpace: AddressSpace = backing match {
    case value: AddressSpace => value
    case _ => throw GraphicsAccess.Failure(Fault.Memory("graphics execution requires an address space"))
  }
  def allocate(bytes: Array[Byte]): (AllocationId, Long > 0) = {
    require(bytes.nonEmpty, "empty graphics scratch allocation")
    require(local.size.toLong < -ScratchBase / ScratchStride, "graphics scratch address space exhausted")
    val candidate = Long.MaxValue - local.size.toLong
    val region    = candidate.unsafe[AllocationId]
    require(addressSpace.address(region, 0L).isLeft, "graphics scratch allocation collides with backing storage")
    local += bytes
    region -> bytes.length.toLong.unsafe[Long > 0]
  }
  def releaseScratch(): Unit                   = local.clear()
  private def index(region: AllocationId): Int = {
    val slot = Long.MaxValue - region.raw
    if (slot < local.size) slot.toInt else -1
  }
  private def localRange(
      region: AllocationId,
      offset: Long >= 0,
      size: Int >= 0
  ): Either[Fault, (Array[Byte], Int >= 0)] = {
    val slot = index(region)
    if (slot < 0 || slot >= local.size) Left(Fault.Memory("unknown graphics scratch region"))
    else ByteRange.start(local(slot).length, offset.raw, size.raw)
      .toRight(Fault.Memory("graphics scratch access exceeds allocation"))
      .map(start => local(slot) -> start)
  }
  override def readDeferred(region: AllocationId, offset: Long >= 0, size: Int >= 0): Memory.ReadResult[Vec[Byte]] =
    if (index(region) < 0) backing.readDeferred(region, offset, size) else read(region, offset, size)
  override def readValueDeferred(
      region: AllocationId,
      offset: Long >= 0,
      size: Int in 1 ~ 8
  ): Memory.ReadResult[Long] =
    if (index(region) < 0) backing.readValueDeferred(region, offset, size) else readValue(region, offset, size)

  override def read(region: AllocationId, offset: Long >= 0, size: Int >= 0): Either[Fault, Vec[Byte]] =
    if (index(region) < 0) backing.read(region, offset, size)
    else localRange(region, offset, size).map((bytes, start) => Vec.from(bytes.slice(start.raw, (start + size).raw)))
  override def write(region: AllocationId, offset: Long >= 0, bytes: Vec[Byte]): Either[Fault, Unit] =
    if (index(region) < 0) backing.write(region, offset, bytes)
    else localRange(region, offset, bytes.nonNegativeSize).map { (target, start) =>
      val _ = bytes.copyToArray(target, start.raw)
    }
  override def readValue(
      region: AllocationId,
      offset: Long >= 0,
      size: Int in 1 ~ 8
  ): Either[Fault, Long] =
    if (index(region) < 0) backing.readValue(region, offset, size)
    else localRange(region, offset, size).map((bytes, start) =>
      LittleEndian.decode(bytes, start.raw, size.scalarByteWidth)
    )
  override def writeValue(
      region: AllocationId,
      offset: Long >= 0,
      value: Long,
      size: Int in 1 ~ 8
  ): Either[Fault, Unit] =
    if (index(region) < 0) backing.writeValue(region, offset, value, size)
    else localRange(region, offset, size).map((bytes, start) =>
      LittleEndian.write(value, bytes, start.raw, size.scalarByteWidth)
    )
  override def atomicModifyValue(
      region: AllocationId,
      offset: Long >= 0,
      size: Int in 1 ~ 8
  )(update: Long => Long): Either[Fault, Long] =
    if (index(region) < 0) backing.atomicModifyValue(region, offset, size)(update)
    else Left(Fault.Unsupported("atomic access to shader interface records"))
  override def hasNativePointers(region: AllocationId): Boolean =
    (index(region) >= 0) || backing.hasNativePointers(region)
  override def address(region: AllocationId, offset: Long >= 0): Either[Fault, GuestAddress] =
    if (index(region) < 0) addressSpace.address(region, offset)
    else localRange(region, offset, 0).map(_ =>
      GuestAddress(ScratchBase + index(region).toLong * ScratchStride + offset.raw)
    )
  private def isScratch(address: GuestAddress): Boolean = address.raw >= ScratchBase && address.raw < 0L
  override def read(address: GuestAddress, size: Int >= 0): Either[Fault, Vec[Byte]] =
    if (isScratch(address)) super.read(address, size) else addressSpace.read(address, size)
  override def readDeferred(address: GuestAddress, size: Int >= 0): Memory.ReadResult[Vec[Byte]] =
    if (isScratch(address)) super.readDeferred(address, size) else addressSpace.readDeferred(address, size)
  override def readValue(address: GuestAddress, size: Int in 1 ~ 8): Either[Fault, Long] =
    if (isScratch(address)) super.readValue(address, size) else addressSpace.readValue(address, size)
  override def write(address: GuestAddress, bytes: Vec[Byte]): Either[Fault, Unit] =
    if (isScratch(address)) super.write(address, bytes) else addressSpace.write(address, bytes)
  override def writeValue(address: GuestAddress, value: Long, size: Int in 1 ~ 8): Either[Fault, Unit] =
    if (isScratch(address)) super.writeValue(address, value, size) else addressSpace.writeValue(address, value, size)
  override def atomicModifyValue(
      address: GuestAddress,
      size: Int in 1 ~ 8
  )(update: Long => Long): Either[Fault, Long] =
    if (isScratch(address)) super.atomicModifyValue(address, size)(update)
    else addressSpace.atomicModifyValue(address, size)(update)
  override def locate(address: GuestAddress, size: Int >= 0): Either[Fault, AddressSpace.Location] =
    if (!isScratch(address)) addressSpace.locate(address, size)
    else {
      val relative = address.raw - ScratchBase
      val slot     = relative / ScratchStride
      if (slot < 0 || slot >= local.size) addressSpace.locate(address, size)
      else {
        val region = (Long.MaxValue - slot).unsafe[AllocationId]
        val offset = (relative % ScratchStride).unsafe[Long >= 0]
        localRange(region, offset, size).map(_ => AddressSpace.Location.Within(region, offset))
      }
    }
  override def prepareReads(ranges: Vec[(GuestAddress, Int >= 0)]): Either[Fault, Unit] =
    addressSpace.prepareReads(ranges.filter { case (address, _) => !isScratch(address) })
  override def defersHostReads: Boolean               = backing.defersHostReads
  override def flushHostReads(): Either[Fault, Unit]  = backing.flushHostReads()
  override def flushHostWrites(): Either[Fault, Unit] = backing.flushHostWrites()
  override def invalidateHostReads(): Unit            = backing.invalidateHostReads()
  override def fenceHostMemory(): Either[Fault, Unit] = backing.fenceHostMemory()

  def prepare(ranges: IterableOnce[GraphicsRange]): Unit = {
    val reads = Vec.from(ranges).distinct.map { range =>
      GraphicsAccess.get(address(range.allocation, range.offset)) -> range.size.toIntExact.widen[Int >= 0]
    }
    GraphicsAccess.get(prepareReads(reads))
  }

  private def checked(range: GraphicsRange, offset: Long, length: Int >= 0): Long >= 0 = {
    require(
      offset >= 0 && offset <= range.size.raw && length.toLong.raw <= range.size.raw - offset,
      "graphics access exceeds its view"
    )
    range.offset + offset.unsafe[Long >= 0]
  }
  def bytes(range: GraphicsRange, offset: Long, length: Int): Array[Byte] = {
    val size = length.unsafe[Int >= 0]
    GraphicsAccess.get(read(range.allocation, checked(range, offset, size), size)).toArray
  }
  def store(range: GraphicsRange, offset: Long, bytes: Array[Byte]): Unit =
    GraphicsAccess.get(write(range.allocation, checked(range, offset, bytes.length.unsafe[Int >= 0]), Vec.from(bytes)))
  def word(range: GraphicsRange, offset: Long, width: Int in 1 ~ 8): Long =
    GraphicsAccess.get(readValue(
      range.allocation,
      checked(range, offset, width),
      width
    ))
  def putWord(range: GraphicsRange, offset: Long, width: Int in 1 ~ 8, value: Long): Unit =
    GraphicsAccess.get(writeValue(
      range.allocation,
      checked(range, offset, width),
      value,
      width
    ))
  def checkpoint(): Unit = GraphicsAccess.get(control.checkpoint())
}
