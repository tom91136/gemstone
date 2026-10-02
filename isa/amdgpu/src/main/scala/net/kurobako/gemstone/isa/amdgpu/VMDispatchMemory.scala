package net.kurobako.gemstone.isa.amdgpu

import ELF.*
import VMDispatchMemory.*
import cats.syntax.all.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.concurrent.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*

private object VMDispatchMemory {
  case class GlobalBinding(
      address: Long,
      size: Int >= 0,
      allocation: AllocationId,
      offset: Long >= 0,
      allocationSize: Long > 0
  )

  val KernargAddress  = 0x100000000000L
  val DispatchAddress = 0x200000000000L
}

/** One dispatch's memory routing; global bytes and their locks remain owned by the loaded program. */
private class VMDispatchMemory(
    kernel: CodeObjectKernel,
    kernarg: Array[Byte],
    dispatch: Array[Byte],
    allocations: Array[GlobalBinding],
    memory: AddressSpace
) {
  import VMDispatchMemory.*
  private val readOnlyAddress   = kernel.readOnlyAddress
  private val readOnly          = kernel.readOnly
  private val globalMemory      = kernel.globalMemory
  private val globalMemoryLocks = kernel.globalMemoryLocks

  def flushHostWrites(): Either[Fault, Unit]                                   = memory.flushHostWrites()
  def invalidateHostReads(): Unit                                              = memory.invalidateHostReads()
  def prepareReads(ranges: Vec[(GuestAddress, Int >= 0)]): Either[Fault, Unit] = memory.prepareReads(ranges)
  def isImmutable(address: Long, size: Int): Boolean                           =
    ByteRange.contains(kernarg.length, address - KernargAddress, size.toLong) ||
      ByteRange.contains(dispatch.length, address - DispatchAddress, size.toLong) ||
      ByteRange.contains(readOnly.length, address - readOnlyAddress, size.toLong)
  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  def isExternal(address: Long, size: Int): Boolean =
    aliasedRegion(address) == null && !isImmutable(address, size) &&
      globalRegionIndex(address, size) < 0

  // These memory-instruction helpers consume the allocation-free null sentinel returned by aliasedRegion.
  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  def readBytes(
      address: Long,
      size: Int >= 0
  ): Either[Fault, Vec[Byte]] = {
    val binding = aliasedRegion(address)
    if (binding != null)
      memory.read(
        binding.allocation,
        (binding.offset.raw + address - binding.address).unsafe[Long >= 0],
        size
      )
    else localRead(address, size.raw, KernargAddress, kernarg)
      .orElse(localRead(address, size.raw, DispatchAddress, dispatch))
      .orElse(localRead(address, size.raw, readOnlyAddress, readOnly))
      .orElse(globalRead(address, size.raw)) match {
      case Some(bytes) => Right(Vec.unsafeWrapArray(bytes))
      case None        =>
        memory.read(GuestAddress(address), size).leftMap {
          case Fault.Memory(message) =>
            Fault.Memory(s"AMDGPU memory read address=0x${address.toHexString} size=$size: $message")
          case other => other
        }
    }

  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  def readValue(
      address: Long,
      size: Int where Between[1, 8]
  ): Either[Fault, Long] = {
    val scalarSize = size
    val binding    = aliasedRegion(address)
    if (binding != null)
      memory.readValue(
        binding.allocation,
        (binding.offset.raw + address - binding.address).unsafe[Long >= 0],
        size
      )
    else {
      val kernargOffset = address - KernargAddress
      if (ByteRange.contains(kernarg.length, kernargOffset, size.raw.toLong))
        Right(LittleEndian.decode(kernarg, kernargOffset.toInt, scalarSize))
      else {
        val dispatchOffset = address - DispatchAddress
        if (ByteRange.contains(dispatch.length, dispatchOffset, size.raw.toLong))
          Right(LittleEndian.decode(dispatch, dispatchOffset.toInt, scalarSize))
        else {
          val readOnlyOffset = address - readOnlyAddress
          if (ByteRange.contains(readOnly.length, readOnlyOffset, size.raw.toLong))
            Right(LittleEndian.decode(readOnly, readOnlyOffset.toInt, scalarSize))
          else {
            val globalIndex = globalRegionIndex(address, size.raw)
            if (globalIndex >= 0) {
              val (base, bytes) = globalMemory(globalIndex)
              Right(LittleEndian.decode(bytes, (address - base).toInt, scalarSize))
            } else {
              memory.readValue(GuestAddress(address), size).leftMap {
                case Fault.Memory(message) =>
                  Fault.Memory(s"AMDGPU memory read address=0x${address.toHexString} size=$size: $message")
                case other => other
              }
            }
          }
        }
      }
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  def writeBytes(
      address: Long,
      bytes: Array[Byte]
  ): Either[Fault, Unit] = {
    val binding = aliasedRegion(address)
    if (binding != null)
      memory.write(
        binding.allocation,
        (binding.offset.raw + address - binding.address).unsafe[Long >= 0],
        Vec.unsafeWrapArray(bytes)
      )
    else globalWrite(address, bytes) match {
      case Some(_) => Right(())
      case None    =>
        memory.write(GuestAddress(address), Vec.unsafeWrapArray(bytes)).leftMap {
          case Fault.Memory(message) => Fault.Memory(
              s"AMDGPU memory write address=0x${address.toHexString} size=${bytes.length}: $message"
            )
          case other => other
        }
    }

  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  def writeValue(
      address: Long,
      value: Long,
      size: Int where Between[1, 8]
  ): Either[Fault, Unit] = {
    val scalarSize = size
    val binding    = aliasedRegion(address)
    if (binding != null)
      memory.writeValue(
        binding.allocation,
        (binding.offset.raw + address - binding.address).unsafe[Long >= 0],
        value,
        size
      )
    else {
      val globalIndex = globalRegionIndex(address, size.raw)
      if (globalIndex >= 0) {
        val (base, bytes) = globalMemory(globalIndex)
        LittleEndian.write(value, bytes, (address - base).toInt, scalarSize)
        Right(())
      } else {
        memory.writeValue(GuestAddress(address), value, size).leftMap {
          case Fault.Memory(message) =>
            Fault.Memory(s"AMDGPU memory write address=0x${address.toHexString} size=$size: $message")
          case other => other
        }
      }
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  def atomicModifyValue(
      address: Long,
      size: Int where Between[1, 8]
  )(update: Long => Long): Either[Fault, Long] = {
    val scalarSize = size
    val binding    = aliasedRegion(address)
    if (binding != null)
      memory.atomicModifyValue(
        binding.allocation,
        (binding.offset.raw + address - binding.address).unsafe[Long >= 0],
        size
      )(update)
    else {
      val globalIndex = globalRegionIndex(address, size.raw)
      if (globalIndex >= 0) {
        val (base, bytes) = globalMemory(globalIndex)
        globalMemoryLocks(globalIndex).withLock {
          val offset   = (address - base).toInt
          val previous = LittleEndian.decode(bytes, offset, scalarSize)
          LittleEndian.write(update(previous), bytes, offset, scalarSize)
          Right(previous)
        }
      } else {
        memory.atomicModifyValue(GuestAddress(address), size)(update).left.map {
          case Fault.Memory(message) =>
            Fault.Memory(s"AMDGPU atomic address=0x${address.toHexString} size=$size: $message")
          case other => other
        }
      }
    }
  }

  private def globalRead(address: Long, size: Int): Option[Array[Byte]] = {
    val index = globalRegionIndex(address, size)
    if (index < 0) None
    else {
      val (base, bytes) = globalMemory(index)
      val offset        = address - base
      Some(java.util.Arrays.copyOfRange(bytes, offset.toInt, offset.toInt + size))
    }
  }

  private def globalWrite(address: Long, value: Array[Byte]): Option[Unit] = {
    val index = globalRegionIndex(address, value.length)
    if (index < 0) None
    else {
      val (base, bytes) = globalMemory(index)
      val offset        = address - base
      Array.copy(value, 0, bytes, offset.toInt, value.length)
      Some(())
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def globalRegionIndex(address: Long, size: Int): Int = {
    var index = 0
    var found = -1
    while (index < globalMemory.length && found < 0) {
      val (base, bytes) = globalMemory(index)
      val offset        = address - base
      if (address >= base && offset <= bytes.length.toLong - size.toLong) found = index
      index += 1
    }
    found
  }

  // Null is the allocation-free miss sentinel in this memory-instruction hot path.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.null"))
  private def aliasedRegion(address: Long): GlobalBinding | Null = {
    var index                        = 0
    var result: GlobalBinding | Null = null
    while (index < allocations.length && result == null) {
      val region = allocations(index)
      if (address >= region.address && address - region.address < region.size.raw) result = region
      index += 1
    }
    result
  }

  private def localRead(address: Long, size: Int, base: Long, bytes: Array[Byte]): Option[Array[Byte]] = {
    val offset = address - base
    Option.when(ByteRange.contains(bytes.length, offset, size.toLong))(
      java.util.Arrays.copyOfRange(bytes, offset.toInt, offset.toInt + size)
    )
  }

}
