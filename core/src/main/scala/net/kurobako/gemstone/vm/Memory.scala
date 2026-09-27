package net.kurobako.gemstone.vm

import net.kurobako.gemstone.Constraint.IntBetween

import net.kurobako.gemstone.Constraint.NonNegative

import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.binary.LittleEndian
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
      offset: NonNegative[Long],
      size: NonNegative[Int]
  ): Either[Fault, Vec[Byte]]

  def write(
      region: MemoryRegion,
      offset: NonNegative[Long],
      bytes: Vec[Byte]
  ): Either[Fault, Unit]

  def readValue(
      region: MemoryRegion,
      offset: NonNegative[Long],
      size: IntBetween[1, 8]
  ): Either[Fault, Long] =
    read(region, offset, size.nonNegative)
      .map(bytes => LittleEndian.decode(bytes, 0, size.scalarByteWidth))

  def writeValue(
      region: MemoryRegion,
      offset: NonNegative[Long],
      value: Long,
      size: IntBetween[1, 8]
  ): Either[Fault, Unit] =
    write(region, offset, LittleEndian.toVec(value, size.scalarByteWidth))

  @nowarn("msg=unused explicit parameter")
  def atomicModifyValue(
      region: MemoryRegion,
      offset: NonNegative[Long],
      size: IntBetween[1, 8]
  )(update: Long => Long): Either[Fault, Long] =
    Left(Fault.Unsupported("memory does not support atomic modification"))
}
