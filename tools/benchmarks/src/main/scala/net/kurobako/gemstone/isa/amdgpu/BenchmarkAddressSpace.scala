package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.memory.GuestAddress
import net.kurobako.gemstone.memory.MemoryValueByteWidth
import net.kurobako.gemstone.vm.{AddressSpace, Fault, Memory, MemoryRegion}

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

@SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
private trait BenchmarkAddressSpace extends AddressSpace { self: Memory =>
  protected def regionSizes: Iterable[(MemoryRegion, Long)]

  private val AddressBase   = 0x400000000000L
  private val AddressStride = 0x000100000000L

  private def base(region: MemoryRegion): Long =
    AddressBase + (region.value - 1L) * AddressStride

  final override def address(
      region: MemoryRegion,
      offset: Long :| Positive0
  ): Either[Fault, GuestAddress] =
    regionSizes.find(_._1 == region).toRight(Fault.Memory("benchmark memory region")).flatMap { case (_, size) =>
      Either.cond(
        offset <= size,
        GuestAddress(base(region) + offset),
        Fault.InvalidTask("benchmark address range")
      )
    }

  @SuppressWarnings(Array(
    "scalafix:DisableSyntax.null",
    "scalafix:DisableSyntax.var",
    "scalafix:GemstoneNoIterator.no-iterator"
  ))
  final override def locate(
      address: GuestAddress,
      size: Int :| Positive0
  ): Either[Fault, AddressSpace.Location] = {
    val regions                              = regionSizes
    var result: AddressSpace.Location | Null = null
    regions match {
      case indexed: IndexedSeq[(MemoryRegion, Long)] @unchecked =>
        var index = 0
        while (index < indexed.length && result == null) {
          val (region, regionSize) = indexed(index)
          val relative             = address.value - base(region)
          if (relative >= 0L && relative <= regionSize - size.toLong)
            result = AddressSpace.Location(region, relative.refineUnsafe[Positive0])
          index += 1
        }
      case _ =>
        val entries = regions.iterator
        while (entries.hasNext && result == null) {
          val (region, regionSize) = entries.next()
          val relative             = address.value - base(region)
          if (relative >= 0L && relative <= regionSize - size.toLong)
            result = AddressSpace.Location(region, relative.refineUnsafe[Positive0])
        }
    }
    if (result == null) Left(Fault.Memory("benchmark address")) else Right(result.nn)
  }

  final override def read(
      memory: Memory,
      address: GuestAddress,
      size: Int :| Positive0
  ): Either[Fault, Vec[Byte]] = regionSizes match {
    case indexed: IndexedSeq[(MemoryRegion, Long)] @unchecked =>
      val index = mappedIndex(address, size, indexed)
      if (index < 0) Left(Fault.Memory("benchmark address"))
      else {
        val (region, _) = indexed(index)
        memory.read(region, (address.value - base(region)).assume[Positive0], size)
      }
    case _ => super.read(memory, address, size)
  }

  final override def write(memory: Memory, address: GuestAddress, bytes: Vec[Byte]): Either[Fault, Unit] =
    regionSizes match {
      case indexed: IndexedSeq[(MemoryRegion, Long)] @unchecked =>
        val size  = bytes.length.assume[Positive0]
        val index = mappedIndex(address, size, indexed)
        if (index < 0) Left(Fault.Memory("benchmark address"))
        else {
          val (region, _) = indexed(index)
          memory.write(region, (address.value - base(region)).assume[Positive0], bytes)
        }
      case _ => super.write(memory, address, bytes)
    }

  final override def readValue(
      memory: Memory,
      address: GuestAddress,
      size: MemoryValueByteWidth
  ): Either[Fault, Long] = regionSizes match {
    case indexed: IndexedSeq[(MemoryRegion, Long)] @unchecked =>
      val index = mappedIndex(address, size.nonNegative, indexed)
      if (index < 0) Left(Fault.Memory("benchmark address"))
      else {
        val (region, _) = indexed(index)
        memory.readValue(region, (address.value - base(region)).assume[Positive0], size)
      }
    case _ => super.readValue(memory, address, size)
  }

  final override def writeValue(
      memory: Memory,
      address: GuestAddress,
      value: Long,
      size: MemoryValueByteWidth
  ): Either[Fault, Unit] = regionSizes match {
    case indexed: IndexedSeq[(MemoryRegion, Long)] @unchecked =>
      val index = mappedIndex(address, size.nonNegative, indexed)
      if (index < 0) Left(Fault.Memory("benchmark address"))
      else {
        val (region, _) = indexed(index)
        memory.writeValue(region, (address.value - base(region)).assume[Positive0], value, size)
      }
    case _ => super.writeValue(memory, address, value, size)
  }

  final override def atomicModifyValue(
      memory: Memory,
      address: GuestAddress,
      size: MemoryValueByteWidth
  )(update: Long => Long): Either[Fault, Long] = regionSizes match {
    case indexed: IndexedSeq[(MemoryRegion, Long)] @unchecked =>
      val index = mappedIndex(address, size.nonNegative, indexed)
      if (index < 0) Left(Fault.Memory("benchmark address"))
      else {
        val (region, _) = indexed(index)
        memory.atomicModifyValue(region, (address.value - base(region)).assume[Positive0], size)(update)
      }
    case _ => super.atomicModifyValue(memory, address, size)(update)
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def mappedIndex(
      address: GuestAddress,
      size: Int :| Positive0,
      regions: IndexedSeq[(MemoryRegion, Long)]
  ): Int = {
    val relative = address.value - AddressBase
    var found    = -1
    if (relative >= 0L) {
      val candidate = (relative / AddressStride).toInt
      if (candidate >= 0 && candidate < regions.length) {
        val (region, regionSize) = regions(candidate)
        val offset               = relative - candidate.toLong * AddressStride
        if (region.value == candidate.toLong + 1L && offset <= regionSize - size.toLong) found = candidate
      }
    }
    var index = 0
    while (index < regions.length && found < 0) {
      val (region, regionSize) = regions(index)
      val relative             = address.value - base(region)
      if (relative >= 0L && relative <= regionSize - size.toLong) found = index
      index += 1
    }
    found
  }

}
