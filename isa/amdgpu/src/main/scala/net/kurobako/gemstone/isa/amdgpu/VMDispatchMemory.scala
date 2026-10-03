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
  ) {
    def offsetOf(value: Long): Long >= 0 = offset + (value - address).unsafe[Long >= 0]
  }

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
  def isImmutable(address: Long, size: Int >= 0): Boolean                      =
    ByteRange.contains(kernarg.length, address - KernargAddress, size.toLong.raw) ||
      ByteRange.contains(dispatch.length, address - DispatchAddress, size.toLong.raw) ||
      ByteRange.contains(readOnly.length, address - readOnlyAddress, size.toLong.raw)
  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  def isExternal(address: Long, size: Int >= 0): Boolean =
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
        binding.offsetOf(address),
        size
      )
    else localRead(address, size, KernargAddress, kernarg)
      .orElse(localRead(address, size, DispatchAddress, dispatch))
      .orElse(localRead(address, size, readOnlyAddress, readOnly))
      .orElse(globalRead(address, size)) match {
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
      size: Int in 1 ~ 8
  ): Either[Fault, Long] = {
    val scalarSize = size
    val binding    = aliasedRegion(address)
    if (binding != null)
      memory.readValue(
        binding.allocation,
        binding.offsetOf(address),
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
            val globalIndex = globalRegionIndex(address, size)
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
        binding.offsetOf(address),
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
      size: Int in 1 ~ 8
  ): Either[Fault, Unit] = {
    val scalarSize = size
    val binding    = aliasedRegion(address)
    if (binding != null)
      memory.writeValue(
        binding.allocation,
        binding.offsetOf(address),
        value,
        size
      )
    else {
      val globalIndex = globalRegionIndex(address, size)
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
      size: Int in (4, 8)
  )(update: Long => Long): Either[Fault, Long] = {
    val scalarSize = size
    val binding    = aliasedRegion(address)
    if (binding != null)
      memory.atomicModifyValue(
        binding.allocation,
        binding.offsetOf(address),
        size
      )(update)
    else {
      val globalIndex = globalRegionIndex(address, size)
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

  private def globalRead(address: Long, size: Int >= 0): Option[Array[Byte]] = {
    val index = globalRegionIndex(address, size)
    if (index < 0) None
    else {
      val (base, bytes) = globalMemory(index)
      val offset        = address - base
      Some(java.util.Arrays.copyOfRange(bytes, offset.toInt, offset.toInt + size.raw))
    }
  }

  private def globalWrite(address: Long, value: Array[Byte]): Option[Unit] = {
    val index = globalRegionIndex(address, value.length.unsafe[Int >= 0])
    if (index < 0) None
    else {
      val (base, bytes) = globalMemory(index)
      val offset        = address - base
      Array.copy(value, 0, bytes, offset.toInt, value.length)
      Some(())
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def globalRegionIndex(address: Long, size: Int >= 0): Int = {
    var index = 0
    var found = -1
    while (index < globalMemory.length && found < 0) {
      val (base, bytes) = globalMemory(index)
      val offset        = address - base
      if (address >= base && offset <= bytes.length.toLong - size.toLong.raw) found = index
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

  private def localRead(address: Long, size: Int >= 0, base: Long, bytes: Array[Byte]): Option[Array[Byte]] = {
    val offset = address - base
    Option.when(ByteRange.contains(bytes.length, offset, size.toLong.raw))(
      java.util.Arrays.copyOfRange(bytes, offset.toInt, offset.toInt + size.raw)
    )
  }

}
