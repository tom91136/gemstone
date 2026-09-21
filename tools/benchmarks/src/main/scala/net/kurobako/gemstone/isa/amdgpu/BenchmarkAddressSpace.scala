package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.memory.GuestAddress
import net.kurobako.gemstone.vm.{AddressSpace, Fault, Memory, MemoryRegion}

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

private trait BenchmarkAddressSpace extends AddressSpace { self: Memory =>
  protected def regionSizes: Iterable[(MemoryRegion, Long)]

  private def base(region: MemoryRegion): Long =
    0x400000000000L + (region.value - 1L) * 0x000100000000L

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

  final override def locate(
      address: GuestAddress,
      size: Int :| Positive0
  ): Either[Fault, AddressSpace.Location] =
    regionSizes.view.collectFirst(Function.unlift { case (region, regionSize) =>
      val relative = address.value - base(region)
      Option.when(relative >= 0L && relative <= regionSize - size.toLong)(
        AddressSpace.Location(region, relative.refineUnsafe[Positive0])
      )
    }).toRight(Fault.Memory("benchmark address"))
}
