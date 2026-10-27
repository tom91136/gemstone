package net.kurobako.gemstone.isa.amdgpu

import VMDispatchMemory.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.concurrent.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import scala.runtime.LongRef

private object VMDispatchMemory {

  /** A code-object global of `size` bytes at `address`, backed by bound external storage. */
  case class GlobalMapping(address: GuestAddress, size: Int >= 0, binding: GlobalBinding) derives CanEqual {
    def contains(value: Long): Boolean               = value >= address.raw && value - address.raw < size
    def overlaps(start: Long, length: Long): Boolean = start < address.raw + size && address.raw < start + length
    def offsetOf(value: Long): Long >= 0             = binding.offset + (value - address.raw).unsafe[Long >= 0]
  }

  val KernargAddress  = 0x100000000000L
  val DispatchAddress = 0x200000000000L
  val QueueAddress    = 0x300000000000L
  // ROCr amd_queue_t ABI: aperture upper words follow the public queue, capabilities and write index.
  private val QueueApertureAddress = QueueAddress + 64L
  private val QueueApertures       = LittleEndian.toArray(VMAddressSpace.LocalBase >>> 32, 4) ++
    LittleEndian.toArray(VMAddressSpace.PrivateBase >>> 32, 4)

  private def annotate[A](access: String, address: Long, size: Int, result: Either[Fault, A]): Either[Fault, A] =
    result match {
      case Left(fault) => Left(annotated(access, address, size, fault))
      case other       => other
    }

  private def annotated(access: String, address: Long, size: Int, fault: Fault): Fault = fault match {
    case Fault.Memory(message) => Fault.Memory(s"AMDGPU $access address=0x${address.toHexString} size=$size: $message")
    case other                 => other
  }
}

/** One dispatch's memory routing; global bytes and their locks remain owned by the loaded program. */
private class VMDispatchMemory(
    kernel: CodeObjectKernel,
    kernarg: Array[Byte],
    dispatch: Array[Byte],
    allocations: Array[GlobalMapping],
    val memory: AddressSpace,
    val kernargAddress: Long
) {
  private val readOnlyAddress   = kernel.readOnlyAddress
  private val globalMemory      = kernel.globalMemory
  private val globalMemoryLocks = kernel.globalMemoryLocks
  // Dispatch-constant images, read-only for the kernel.
  private val immutableBases  = Array(KernargAddress, DispatchAddress, QueueApertureAddress, readOnlyAddress)
  private val immutableImages = Array(kernarg, dispatch, QueueApertures, kernel.readOnly)
  private val localBases      = immutableBases ++ globalMemory.map(_._1)
  private val localImages     = immutableImages ++ globalMemory.map(_._2)
  // Heap and stack addresses lie outside the ELF image; reject them before scanning its symbols and sections.
  private val aliasStart = allocations.view.map(_.address.raw).minOption.getOrElse(Long.MaxValue)
  private val aliasEnd   = allocations.view.map(region =>
    if (region.address.raw > Long.MaxValue - region.size) Long.MaxValue else region.address.raw + region.size
  ).maxOption.getOrElse(Long.MinValue)
  private val globalStart = globalMemory.view.map(_._1).minOption.getOrElse(Long.MaxValue)
  private val globalEnd   = globalMemory.view.map { case (base, bytes) =>
    if (base > Long.MaxValue - bytes.length) Long.MaxValue else base + bytes.length
  }.maxOption.getOrElse(Long.MinValue)

  def isMutable(address: Long, size: Int >= 0): Boolean = immutableIndex(address, size) < 0

  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  def isExternal(address: Long, size: Int >= 0): Boolean =
    aliasedRegion(address) == null && isMutable(address, size) && globalRegionIndex(address, size) < 0

  // These memory-instruction helpers consume the allocation-free null sentinel returned by aliasedRegion.
  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  def readBytes(address: Long, size: Int >= 0): Either[Fault, Vec[Byte]] = {
    val binding = aliasedRegion(address)
    if (binding != null) memory.read(binding.binding.region, binding.offsetOf(address), size)
    else {
      val image = localImage(address, size)
      if (image < 0) annotate("memory read", address, size, memory.read(GuestAddress(address), size))
      else {
        val offset = (address - localBases(image)).toInt
        Right(Vec.unsafeWrapArray(java.util.Arrays.copyOfRange(localImages(image), offset, offset + size)))
      }
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  def readValueInto(address: Long, size: Int in 1 ~ 8, into: LongRef): Fault | Null = {
    val binding = aliasedRegion(address)
    if (binding != null) memory.readValueInto(binding.binding.region, binding.offsetOf(address), size, into)
    else {
      val image = localImage(address, size)
      if (image < 0) memory.readValueInto(GuestAddress(address), size, into) match {
        case null         => null
        case fault: Fault => annotated("memory read", address, size, fault)
      }
      else {
        into.elem = LittleEndian.decode(localImages(image), (address - localBases(image)).toInt, size)
        null
      }
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  def writeBytes(address: Long, bytes: Array[Byte]): Either[Fault, Unit] = {
    val binding = aliasedRegion(address)
    if (binding != null) memory.write(binding.binding.region, binding.offsetOf(address), Vec.unsafeWrapArray(bytes))
    else {
      val index = globalRegionIndex(address, bytes.length)
      if (index < 0)
        annotate("memory write", address, bytes.length, memory.write(GuestAddress(address), Vec.unsafeWrapArray(bytes)))
      else {
        val (base, image) = globalMemory(index)
        Array.copy(bytes, 0, image, (address - base).toInt, bytes.length)
        Right(())
      }
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  def writeValue(address: Long, value: Long, size: Int in 1 ~ 8): Either[Fault, Unit] = {
    val binding = aliasedRegion(address)
    if (binding != null) memory.writeValue(binding.binding.region, binding.offsetOf(address), value, size)
    else {
      val index = globalRegionIndex(address, size)
      if (index < 0) annotate("memory write", address, size, memory.writeValue(GuestAddress(address), value, size))
      else {
        val (base, bytes) = globalMemory(index)
        LittleEndian.write(value, bytes, (address - base).toInt, size)
        Right(())
      }
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  def atomicModifyValue(address: Long, size: Int in (4, 8))(update: Long => Long): Either[Fault, Long] = {
    val binding = aliasedRegion(address)
    if (binding != null) memory.atomicModifyValue(binding.binding.region, binding.offsetOf(address), size)(update)
    else {
      val index = globalRegionIndex(address, size)
      if (index < 0)
        annotate("atomic", address, size, memory.atomicModifyValue(GuestAddress(address), size)(update))
      else {
        val (base, bytes) = globalMemory(index)
        globalMemoryLocks(index).withLock {
          val offset   = (address - base).toInt
          val previous = LittleEndian.decode(bytes, offset, size)
          LittleEndian.write(update(previous), bytes, offset, size)
          Right(previous)
        }
      }
    }
  }

  // -1 is the miss sentinel for the dispatch-local and code-object images, indexing localBases and localImages.
  private def localImage(address: Long, size: Int): Int = {
    val local = immutableIndex(address, size)
    if (local >= 0) local
    else {
      val global = globalRegionIndex(address, size)
      if (global >= 0) immutableImages.length + global else -1
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def immutableIndex(address: Long, size: Int): Int = {
    var index = 0
    var found = -1
    while (index < immutableImages.length && found < 0) {
      if (ByteRange.contains(immutableImages(index).length, address - immutableBases(index), size.toLong)) found = index
      index += 1
    }
    found
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def globalRegionIndex(address: Long, size: Int): Int = {
    var index = 0
    var found = -1
    while (address >= globalStart && address <= globalEnd && index < globalMemory.length && found < 0) {
      val (base, bytes) = globalMemory(index)
      if (address >= base && address - base <= bytes.length.toLong - size) found = index
      index += 1
    }
    found
  }

  // Null is the allocation-free miss sentinel in this memory-instruction hot path.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.null"))
  private def aliasedRegion(address: Long): GlobalMapping | Null = {
    var index                        = 0
    var result: GlobalMapping | Null = null
    while (address >= aliasStart && address <= aliasEnd && index < allocations.length && result == null) {
      val region = allocations(index)
      if (region.contains(address)) result = region
      index += 1
    }
    result
  }
}
