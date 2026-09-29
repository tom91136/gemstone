package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*

private trait BenchmarkAddressSpace extends AddressSpace {
  protected def regionSizes: Iterable[(AllocationId, Long)]

  private val AddressBase   = 0x400000000000L
  private val AddressStride = 0x000100000000L

  private def base(region: AllocationId): Long =
    AddressBase + (region.toLong - 1L) * AddressStride

  final override def address(
      region: AllocationId,
      offset: Long >= 0
  ): Either[Fault, GuestAddress] =
    regionSizes.find(_._1 == region).toRight(Fault.Memory("benchmark memory region")).flatMap { case (_, size) =>
      Either.cond(
        offset.raw <= size,
        GuestAddress(base(region) + offset.raw),
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
      size: Int >= 0
  ): Either[Fault, AddressSpace.Location] = {
    val regions                              = regionSizes
    var result: AddressSpace.Location | Null = null
    regions match {
      case indexed: IndexedSeq[(AllocationId, Long)] @unchecked =>
        var index = 0
        while (index < indexed.length && result == null) {
          val (region, regionSize) = indexed(index)
          val relative             = address.raw - base(region)
          if (relative >= 0L && relative <= regionSize - size.raw.toLong)
            result = AddressSpace.Location.Within(region, relative.unsafe[Long >= 0])
          index += 1
        }
      case _ =>
        val entries = regions.iterator
        while (entries.hasNext && result == null) {
          val (region, regionSize) = entries.next()
          val relative             = address.raw - base(region)
          if (relative >= 0L && relative <= regionSize - size.raw.toLong)
            result = AddressSpace.Location.Within(region, relative.unsafe[Long >= 0])
        }
    }
    if (result == null) Left(Fault.Memory("benchmark address")) else Right(result.nn)
  }

  final override def read(
      address: GuestAddress,
      size: Int >= 0
  ): Either[Fault, Vec[Byte]] = regionSizes match {
    case indexed: IndexedSeq[(AllocationId, Long)] @unchecked =>
      val index = mappedIndex(address, size, indexed)
      if (index < 0) Left(Fault.Memory("benchmark address"))
      else {
        val (region, _) = indexed(index)
        (address.raw - base(region)).refine[Long >= 0].left.map(error =>
          Fault.Memory(s"invalid benchmark offset: ${error}")
        ) match {
          case Left(fault)   => Left(fault)
          case Right(offset) => read(region, offset, size)
        }
      }
    case _ => super.read(address, size)
  }

  final override def write(address: GuestAddress, bytes: Vec[Byte]): Either[Fault, Unit] =
    regionSizes match {
      case indexed: IndexedSeq[(AllocationId, Long)] @unchecked =>
        val size  = bytes.nonNegativeSize
        val index = mappedIndex(address, size, indexed)
        if (index < 0) Left(Fault.Memory("benchmark address"))
        else {
          val (region, _) = indexed(index)
          (address.raw - base(region)).refine[Long >= 0].left.map(error =>
            Fault.Memory(s"invalid benchmark offset: ${error}")
          ) match {
            case Left(fault)   => Left(fault)
            case Right(offset) => write(region, offset, bytes)
          }
        }
      case _ => super.write(address, bytes)
    }

  final override def readValue(
      address: GuestAddress,
      size: Int where Between[1, 8]
  ): Either[Fault, Long] = regionSizes match {
    case indexed: IndexedSeq[(AllocationId, Long)] @unchecked =>
      val index = mappedIndex(address, size.nonNegative, indexed)
      if (index < 0) Left(Fault.Memory("benchmark address"))
      else {
        val (region, _) = indexed(index)
        (address.raw - base(region)).refine[Long >= 0].left.map(error =>
          Fault.Memory(s"invalid benchmark offset: ${error}")
        ) match {
          case Left(fault)   => Left(fault)
          case Right(offset) => readValue(region, offset, size)
        }
      }
    case _ => super.readValue(address, size)
  }

  final override def writeValue(
      address: GuestAddress,
      value: Long,
      size: Int where Between[1, 8]
  ): Either[Fault, Unit] = regionSizes match {
    case indexed: IndexedSeq[(AllocationId, Long)] @unchecked =>
      val index = mappedIndex(address, size.nonNegative, indexed)
      if (index < 0) Left(Fault.Memory("benchmark address"))
      else {
        val (region, _) = indexed(index)
        (address.raw - base(region)).refine[Long >= 0].left.map(error =>
          Fault.Memory(s"invalid benchmark offset: ${error}")
        ) match {
          case Left(fault)   => Left(fault)
          case Right(offset) => writeValue(region, offset, value, size)
        }
      }
    case _ => super.writeValue(address, value, size)
  }

  final override def atomicModifyValue(
      address: GuestAddress,
      size: Int where Between[1, 8]
  )(update: Long => Long): Either[Fault, Long] = regionSizes match {
    case indexed: IndexedSeq[(AllocationId, Long)] @unchecked =>
      val index = mappedIndex(address, size.nonNegative, indexed)
      if (index < 0) Left(Fault.Memory("benchmark address"))
      else {
        val (region, _) = indexed(index)
        (address.raw - base(region)).refine[Long >= 0].left.map(error =>
          Fault.Memory(s"invalid benchmark offset: ${error}")
        ) match {
          case Left(fault)   => Left(fault)
          case Right(offset) => atomicModifyValue(region, offset, size)(update)
        }
      }
    case _ => super.atomicModifyValue(address, size)(update)
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def mappedIndex(
      address: GuestAddress,
      size: Int >= 0,
      regions: IndexedSeq[(AllocationId, Long)]
  ): Int = {
    val relative = address.raw - AddressBase
    var found    = -1
    if (relative >= 0L) {
      val candidate = (relative / AddressStride).toInt
      if (candidate >= 0 && candidate < regions.length) {
        val (region, regionSize) = regions(candidate)
        val offset               = relative - candidate.toLong * AddressStride
        if (region.toLong == candidate.toLong + 1L && offset <= regionSize - size.raw.toLong) found = candidate
      }
    }
    var index = 0
    while (index < regions.length && found < 0) {
      val (region, regionSize) = regions(index)
      val relative             = address.raw - base(region)
      if (relative >= 0L && relative <= regionSize - size.raw.toLong) found = index
      index += 1
    }
    found
  }

}
