package net.kurobako.gemstone.vm

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*

object Memory {

  /** A deferred read has not failed; the owner must flush staged reads before retrying. */
  case object Pending
  type ReadResult[+A] = Either[Fault | Pending.type, A]
}

trait Memory {
  import Memory.*

  @scala.annotation.tailrec
  final def awaitRead[A](read: => ReadResult[A]): Either[Fault, A] = read match {
    case Left(Pending) => flushHostReads() match {
        case Left(fault) => Left(fault)
        case Right(())   => awaitRead(read)
      }
    case Left(fault: Fault) => Left(fault)
    case Right(value)       => Right(value)
  }

  def readDeferred(region: AllocationId, offset: Long >= 0, size: Int >= 0): ReadResult[Vec[Byte]] =
    read(region, offset, size)

  def readValueDeferred(region: AllocationId, offset: Long >= 0, size: Int where Between[1, 8]): ReadResult[Long] =
    readValue(region, offset, size)

  /** Pointer bytes use canonical addresses shared by all dispatches. Relocation metadata must not cache or replay them.
    */
  def hasNativePointers(region: AllocationId): Boolean = false

  /** A full fence; remote backing owners acknowledge publication before returning. */
  def fenceHostMemory(): Either[Fault, Unit] = flushHostWrites().map(_ => invalidateHostReads())
  def flushHostWrites(): Either[Fault, Unit] = Right(())
  def invalidateHostReads(): Unit            = ()
  def defersHostReads: Boolean               = false
  def flushHostReads(): Either[Fault, Unit]  = Right(())

  def read(
      region: AllocationId,
      offset: Long >= 0,
      size: Int >= 0
  ): Either[Fault, Vec[Byte]]

  def write(
      region: AllocationId,
      offset: Long >= 0,
      bytes: Vec[Byte]
  ): Either[Fault, Unit]

  def readValue(
      region: AllocationId,
      offset: Long >= 0,
      size: Int where Between[1, 8]
  ): Either[Fault, Long] =
    read(region, offset, size.nonNegative)
      .map(bytes => LittleEndian.decode(bytes, 0, size.scalarByteWidth))

  def writeValue(
      region: AllocationId,
      offset: Long >= 0,
      value: Long,
      size: Int where Between[1, 8]
  ): Either[Fault, Unit] =
    write(region, offset, LittleEndian.toVec(value, size.scalarByteWidth))

  def atomicModifyValue(
      @scala.annotation.unused region: AllocationId,
      @scala.annotation.unused offset: Long >= 0,
      @scala.annotation.unused size: Int where Between[1, 8]
  )(@scala.annotation.unused update: Long => Long): Either[Fault, Long] =
    Left(Fault.Unsupported("memory does not support atomic modification"))
}
