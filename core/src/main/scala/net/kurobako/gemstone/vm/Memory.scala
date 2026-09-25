package net.kurobako.gemstone.vm

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.memory.MemoryValueByteWidth

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import scala.annotation.nowarn

trait Memory {

  /** Pointer bytes use canonical addresses shared by all dispatches. Relocation metadata must not cache or replay them.
    */
  def hasNativePointers(region: MemoryRegion): Boolean = false

  /** A full fence; remote backing owners acknowledge publication before returning. */
  def fenceHostMemory(): Either[Fault, Unit] = flushHostWrites().map(_ => invalidateHostReads())
  def flushHostWrites(): Either[Fault, Unit] = Right(())
  def invalidateHostReads(): Unit            = ()
  def defersHostReads: Boolean               = false
  def flushHostReads(): Either[Fault, Unit]  = Right(())

  def read(
      region: MemoryRegion,
      offset: Long :| Positive0,
      size: Int :| Positive0
  ): Either[Fault, Vec[Byte]]

  def write(
      region: MemoryRegion,
      offset: Long :| Positive0,
      bytes: Vec[Byte]
  ): Either[Fault, Unit]

  def readValue(
      region: MemoryRegion,
      offset: Long :| Positive0,
      size: MemoryValueByteWidth
  ): Either[Fault, Long] =
    read(region, offset, size.nonNegative)
      .map(bytes => LittleEndian.decode(bytes, 0, size.scalarByteWidth))

  def writeValue(
      region: MemoryRegion,
      offset: Long :| Positive0,
      value: Long,
      size: MemoryValueByteWidth
  ): Either[Fault, Unit] =
    write(region, offset, LittleEndian.toVec(value, size.scalarByteWidth))

  @nowarn("msg=unused explicit parameter")
  def atomicModifyValue(
      region: MemoryRegion,
      offset: Long :| Positive0,
      size: MemoryValueByteWidth
  )(update: Long => Long): Either[Fault, Long] =
    Left(Fault.Unsupported("memory does not support atomic modification"))
}
