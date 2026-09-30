package net.kurobako.gemstone.isa.spirv.v1_6

import cats.syntax.all.*
import java.util.concurrent.locks.ReentrantLock
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.concurrent.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import scala.collection.mutable

private object VMMemory {
  enum Region {
    case External(id: AllocationId)
    case Absolute(address: GuestAddress)
    case Internal(memory: InternalMemory)
    case Opaque(bits: Long)
    case Null
  }

  class InternalMemory(val bytes: Array[Byte]) {
    val lock: ReentrantLock = ReentrantLock()
  }

  case class Pointer(
      region: Region,
      offset: Long,
      pointee: Int,
      storageClass: Int,
      slots: Option[mutable.LongMap[(Int, Pointer)]] = None
  )

  private val VirtualAddressSpan = 0x0000010000000000L

  /** One sparse dirty page in SPIR-V's buffered external-memory view. */
  class DirtyPage {
    val bytes: Array[Byte] = Array.ofDim[Byte](4096)
    private val dirty      = Array.ofDim[Long](64)

    def clear(): Unit = java.util.Arrays.fill(dirty, 0L)

    def update(offset: Int, value: Byte): Unit = {
      bytes(offset) = value
      dirty(offset >>> 6) |= 1L << (offset & 63)
    }

    def contains(offset: Int): Boolean = (dirty(offset >>> 6) & (1L << (offset & 63))) != 0L

    @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.return"))
    def nextDirtyByte(from: Int): Int = {
      var word = from >>> 6
      if (word >= dirty.length) return bytes.length
      var remaining = dirty(word) & (-1L << (from & 63))
      while (remaining == 0L && word + 1 < dirty.length) {
        word += 1
        remaining = dirty(word)
      }
      (word << 6) + java.lang.Long.numberOfTrailingZeros(remaining)
    }

    @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.return"))
    def nextCleanByte(from: Int): Int = {
      var word = from >>> 6
      if (word >= dirty.length) return bytes.length
      var remaining = ~dirty(word) & (-1L << (from & 63))
      while (remaining == 0L && word + 1 < dirty.length) {
        word += 1
        remaining = ~dirty(word)
      }
      (word << 6) + java.lang.Long.numberOfTrailingZeros(remaining)
    }
  }
  // Mirrored writes, virtual pointers and relocation bookkeeping are confined behind this final memory-state holder.
  class MemoryState(
      memory: Memory,
      relocations: Vec[KernelTask.PointerRelocation],
      val handler: Event => Either[Fault, Unit]
  ) {
    def flushHostReads(): Either[Fault, Unit] = memory.flushHostReads()
    def fence(): Either[Fault, Unit]          = flush().flatMap(_ => memory.fenceHostMemory())
    private val writes                        = mutable.HashMap.empty[AllocationId, mutable.LongMap[DirtyPage]]
    private val externalVirtualAddresses      = mutable.HashMap.empty[AllocationId, Long]
    private val internalVirtualAddresses      = java.util.IdentityHashMap[Array[Byte], java.lang.Long]()
    private val virtualPointers               = mutable.LongMap.empty[Pointer]
    private val exceptionalAddresses          = mutable.HashMap.empty[(Region, Long), Long]
    private val exceptionalPointers           = mutable.LongMap.empty[Pointer]
    private val pointerRelocations: mutable.HashMap[(AllocationId, Long), (AllocationId, Long)] =
      mutable.HashMap.from(relocations.view.collect {
        case KernelTask.PointerRelocation(
              KernelTask.RelocationSource.Memory(source),
              sourceOffset,
              target,
              targetOffset
            ) =>
          (source -> sourceOffset.raw) -> (target -> targetOffset.raw)
      })
    private val argumentRelocations = {
      val result = mutable.HashMap.empty[Int, mutable.LongMap[(AllocationId, Long)]]
      relocations.foreach {
        case KernelTask.PointerRelocation(
              KernelTask.RelocationSource.Argument(argument),
              sourceOffset,
              target,
              targetOffset
            ) =>
          result
            .getOrElseUpdate(argument.raw, mutable.LongMap.empty)
            .update(sourceOffset.raw, target -> targetOffset.raw)
        case _ => ()
      }
      result
    }
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var nextVirtualAddress = 0x0000100000000000L
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var nextExceptionalAddress = Long.MinValue

    // Atomic loops frequently flush a scalar store. Retain at most one cleared
    // page instead of allocating another 4 KiB buffer for each iteration.
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var recycledPage = Option.empty[DirtyPage]

    private def acquireDirtyPage(): DirtyPage = recycledPage match {
      case Some(page) =>
        recycledPage = None
        page
      case None => DirtyPage()
    }

    /** IdentityHashMap uses null for a missing Java entry; confine it to this interop pattern. */
    @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.return"))
    def pointerInteger(pointer: Pointer): Either[Fault, Long] = {
      (memory, pointer.region) match {
        case (addresses: AddressSpace, Region.External(id)) if pointer.offset >= 0 =>
          return addresses.address(id, pointer.offset.unsafe[Long >= 0]).map(_.raw)
        case _ => ()
      }
      val base = pointer.region match {
        case Region.External(id)     => externalVirtualAddresses.getOrElseUpdate(id, allocateVirtualAddress(pointer))
        case Region.Internal(memory) =>
          internalVirtualAddresses.get(memory.bytes) match {
            case null =>
              val allocated = allocateVirtualAddress(pointer)
              internalVirtualAddresses.put(memory.bytes, allocated)
              allocated
            case existing => existing.longValue()
          }
        case Region.Absolute(address) => return Right(address.offset(pointer.offset).raw)
        case Region.Opaque(bits)      =>
          return Either
            .catchOnly[ArithmeticException](Math.addExact(bits, pointer.offset))
            .leftMap(_ => Fault.InvalidTask("opaque SPIR-V pointer arithmetic overflow"))
        case Region.Null => return Right(0L)
      }
      if (pointer.offset >= 0L && pointer.offset < VirtualAddressSpan) Right(base + pointer.offset)
      else
        Right(exceptionalAddresses.getOrElseUpdate(
          pointer.region -> pointer.offset, {
            // An out-of-range pointer may be formed and compared without being
            // dereferenced. Keep its integer identity outside the normal regions.
            // The mapping lives for the task and is not reclaimed.
            val address = nextExceptionalAddress
            nextExceptionalAddress = Math.addExact(nextExceptionalAddress, 1L)
            exceptionalPointers.update(address, pointer)
            address
          }
        ))
    }

    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    def relocateArgument(index: Int, bytes: Vec[Byte]): Either[Fault, Array[Byte]] = {
      val relocated   = bytes.toArray
      val relocations = argumentRelocations.get(index).toList.view.flatMap(_.view).toArray
      var current     = 0
      var failure     = Option.empty[Fault]
      while (current < relocations.length && failure.isEmpty) {
        val (sourceOffset, (target, targetOffset)) = relocations(current)
        if (sourceOffset > relocated.length.toLong - 8L)
          failure = Some(Fault.InvalidTask(
            s"pointer relocation source offset $sourceOffset exceeds argument $index"
          ))
        else
          pointerInteger(Pointer(Region.External(target), targetOffset, 0, 8)) match {
            case Left(error)    => failure = Some(error)
            case Right(address) =>
              var byte = 0
              while (byte < 8) {
                relocated(sourceOffset.toInt + byte) = (address >>> (byte * 8)).toByte
                byte += 1
              }
          }
        current += 1
      }
      failure.toLeft(relocated)
    }

    def argumentRelocation(index: Int, offset: Long): Option[(AllocationId, Long)] =
      argumentRelocations.get(index).flatMap(_.get(offset))

    def nativePointerStorage(pointer: Pointer): Boolean = pointer.region match {
      case Region.External(region) => memory.hasNativePointers(region)
      case Region.Absolute(_)      => true
      case _                       => false
    }

    def allocationRelocation(source: AllocationId, offset: Long): Option[(AllocationId, Long)] =
      if (memory.hasNativePointers(source)) None else pointerRelocations.get(source -> offset)

    def recordPointerRelocation(pointer: Pointer, destination: Pointer): Either[Fault, Unit] =
      destination.region match {
        case Region.External(destinationAllocation)
            if !memory.hasNativePointers(destinationAllocation) =>
          destination.offset.refine[Long >= 0] match {
            case Left(_) => Left(Fault.InvalidTask(
                s"negative SPIR-V pointer relocation destination offset ${destination.offset}"
              ))
            case Right(sourceOffset) => pointer.region match {
                case Region.External(pointerAllocation) =>
                  pointer.offset.refine[Long >= 0] match {
                    case Left(_) => Left(Fault.InvalidTask(
                        s"negative SPIR-V pointer relocation target offset ${pointer.offset}"
                      ))
                    case Right(targetOffset) =>
                      pointerRelocations.update(
                        destinationAllocation -> destination.offset,
                        pointerAllocation     -> pointer.offset
                      )
                      handler(
                        Event.PointerRelocation(
                          destinationAllocation,
                          sourceOffset,
                          pointerAllocation,
                          targetOffset
                        )
                      )
                  }
                case _ =>
                  val _ = pointerRelocations.remove(destinationAllocation -> destination.offset)
                  Right(())
              }
          }
        case _ => Right(())
      }

    def relocatedPointer(source: Pointer, pointee: Int, storageClass: Int): Option[Pointer] =
      source.region match {
        case Region.External(allocation) =>
          allocationRelocation(allocation, source.offset).map { case (target, targetOffset) =>
            Pointer(Region.External(target), targetOffset, pointee, storageClass)
          }
        case _ => None
      }

    def elementOffset(source: Pointer, index: Long, elementSize: Int): Long =
      Math.addExact(source.offset, Math.multiplyExact(index, elementSize.toLong))

    def integerPointer(address: Long, pointee: Int, storageClass: Int): Either[Fault, Pointer] =
      if (address == 0L) Right(Pointer(Region.Null, 0L, pointee, storageClass))
      else exceptionalPointers.get(address) match {
        case Some(origin) =>
          Either.cond(
            storageClass == 8 || storageClass == origin.storageClass,
            origin.copy(pointee = pointee),
            Fault.InvalidTask(
              s"SPIR-V virtual address 0x${java.lang.Long.toHexString(address)} belongs to storage class " +
                s"${origin.storageClass}, not $storageClass"
            )
          )
        case None =>
          val base   = address & ~(VirtualAddressSpan - 1L)
          val offset = address - base
          virtualPointers.get(base) match {
            case Some(origin) if offset >= 0L && offset < VirtualAddressSpan =>
              Either.cond(
                storageClass == 8 || storageClass == origin.storageClass,
                origin.copy(offset = offset, pointee = pointee),
                Fault.InvalidTask(
                  s"SPIR-V virtual address 0x${java.lang.Long.toHexString(address)} belongs to storage class " +
                    s"${origin.storageClass}, not $storageClass"
                )
              )
            case _ => memory match {
                case addresses: AddressSpace =>
                  addresses.locate(GuestAddress(address), 0).map {
                    case AddressSpace.Location.Within(allocation, offset) =>
                      Pointer(Region.External(allocation), offset.raw, pointee, storageClass)
                    case AddressSpace.Location.Absolute(value) =>
                      Pointer(Region.Absolute(value), 0L, pointee, storageClass)
                  }.orElse(Right(Pointer(Region.Opaque(address), 0L, pointee, storageClass)))
                case _ => Right(Pointer(Region.Opaque(address), 0L, pointee, storageClass))
              }
          }
      }

    private def allocateVirtualAddress(pointer: Pointer): Long = {
      val address = nextVirtualAddress
      nextVirtualAddress = Math.addExact(nextVirtualAddress, VirtualAddressSpan)
      virtualPointers.update(address, pointer.copy(offset = 0L))
      address
    }

    private def addressed[A, E >: Fault](operation: AddressSpace => Either[E, A]): Either[E, A] =
      memory match {
        case addresses: AddressSpace => flush().flatMap(_ => operation(addresses))
        case _ => Left(Fault.Unsupported("absolute SPIR-V pointers require an address-space capable memory"))
      }

    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    def read(pointer: Pointer, size: Int >= 0): Memory.ReadResult[Array[Byte]] =
      pointer.region match {
        case Region.Internal(internal) =>
          checkedRange(internal.bytes.length, pointer.offset, size.raw).map(start =>
            java.util.Arrays.copyOfRange(internal.bytes, start, start + size.raw)
          )
        case Region.External(id) =>
          pointer.offset.refine[Long >= 0]
            .leftMap(_ =>
              Fault.InvalidTask(s"invalid external read offset ${pointer.offset}")
            )
            .flatMap(offset => memory.readDeferred(id, offset, size)) match {
            case Left(error)   => Left(error)
            case Right(source) =>
              val result = source.toArray
              writes.get(id).foreach { pending =>
                var index = 0
                while (index < result.length) {
                  val absolute   = pointer.offset + index.toLong
                  val pageOffset = (absolute & 4095L).toInt
                  pending.get(absolute >>> 12).foreach { page =>
                    if (page.contains(pageOffset)) result(index) = page.bytes(pageOffset)
                  }
                  index += 1
                }
              }
              Right(result)
          }
        case Region.Absolute(address) =>
          addressed(_.readDeferred(address.offset(pointer.offset), size)).map(_.toArray)
        case region @ (Region.Null | Region.Opaque(_)) => Left(regionFailure(region, "read through"))
      }

    def readScalar(pointer: Pointer, size: Int where Between[1, 8]): Memory.ReadResult[Long] = {
      val relocation =
        if (size.raw != 8) None
        else
          pointer.region match {
            case Region.External(source) => allocationRelocation(source, pointer.offset)
            case Region.Internal(_)      => None
            case Region.Opaque(_)        => None
            case Region.Absolute(_)      => None
            case Region.Null             => None
          }
      relocation match {
        case Some((target, targetOffset)) =>
          pointerInteger(Pointer(Region.External(target), targetOffset, 0, 8))
        case None => readExternalScalar(pointer, size)
      }
    }

    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private def readExternalScalar(
        pointer: Pointer,
        size: Int where Between[1, 8]
    ): Memory.ReadResult[Long] = pointer.region match {
      case Region.Internal(internal) =>
        checkedRange(internal.bytes.length, pointer.offset, size.raw)
          .map(start => LittleEndian.decode(internal.bytes, start, size.scalarByteWidth))
      case Region.External(id) =>
        pointer.offset.refine[Long >= 0]
          .leftMap(_ =>
            Fault.InvalidTask(
              s"invalid external scalar read offset ${pointer.offset}"
            )
          )
          .flatMap(offset => memory.readValueDeferred(id, offset, size)) match {
          case Left(error)   => Left(error)
          case Right(source) =>
            var value = source
            writes.get(id).foreach { pages =>
              var index = 0
              while (index < size.raw) {
                val absolute   = pointer.offset + index.toLong
                val pageOffset = (absolute & 4095L).toInt
                pages.get(absolute >>> 12) match {
                  case Some(page) if page.contains(pageOffset) =>
                    val shift = index * 8
                    value = (value & ~(0xffL << shift)) | ((page.bytes(pageOffset).toLong & 0xffL) << shift)
                  case _ => ()
                }
                index += 1
              }
            }
            Right(value)
        }
      case Region.Absolute(address) =>
        addressed(_.readDeferred(address.offset(pointer.offset), size.nonNegative)).map(bytes =>
          LittleEndian.decode(bytes, 0, size.scalarByteWidth)
        )
      case region @ (Region.Null | Region.Opaque(_)) => Left(regionFailure(region, "read scalar through"))
    }

    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    def write(pointer: Pointer, bytes: Array[Byte]): Either[Fault, Unit] =
      pointer.region match {
        case Region.Internal(internal) =>
          checkedRange(internal.bytes.length, pointer.offset, bytes.length) match {
            case Left(error)  => Left(error)
            case Right(start) =>
              Array.copy(bytes, 0, internal.bytes, start, bytes.length)
              Right(())
          }
        case Region.External(id) =>
          if (pointer.offset < 0L || pointer.offset > Long.MaxValue - bytes.length.toLong)
            Left(
              Fault.InvalidTask(s"invalid external write offset ${pointer.offset}")
            )
          else {
            val pending = writes.getOrElseUpdate(id, mutable.LongMap.empty)
            var index   = 0
            while (index < bytes.length) {
              val absolute   = pointer.offset + index.toLong
              val pageOffset = (absolute & 4095L).toInt
              pending.getOrElseUpdate(absolute >>> 12, acquireDirtyPage()).update(pageOffset, bytes(index))
              index += 1
            }
            Right(())
          }
        case Region.Absolute(address) => addressed(_.write(address.offset(pointer.offset), Vec.from(bytes)))
        case Region.Null              =>
          Left(
            Fault.InvalidTask(
              s"cannot write through a null SPIR-V pointer (offset=${pointer.offset}, pointee=%${pointer.pointee}, " +
                s"storage=${pointer.storageClass})"
            )
          )
        case Region.Opaque(bits) =>
          Left(Fault.InvalidTask(
            s"cannot write through opaque SPIR-V pointer 0x${java.lang.Long.toHexString(bits)}"
          ))
      }

    def writeScalar(pointer: Pointer, value: Long, size: Int where Between[1, 8]): Either[Fault, Unit] =
      pointer.region match {
        case Region.Internal(internal) =>
          checkedRange(internal.bytes.length, pointer.offset, size.raw)
            .map(start =>
              LittleEndian.write(value, internal.bytes, start, size.scalarByteWidth)
            )
        case Region.External(id) =>
          if (pointer.offset < 0L || pointer.offset > Long.MaxValue - size.raw.toLong)
            Left(
              Fault.InvalidTask(s"invalid external write offset ${pointer.offset}")
            )
          else {
            val pending = writes.getOrElseUpdate(id, mutable.LongMap.empty)
            LittleEndian.write(value, size.scalarByteWidth) { (index, byte) =>
              val absolute   = pointer.offset + index.toLong
              val pageOffset = (absolute & 4095L).toInt
              pending
                .getOrElseUpdate(absolute >>> 12, acquireDirtyPage())
                .update(pageOffset, byte)
            }
            Right(())
          }
        case Region.Absolute(address) => addressed(_.writeValue(address.offset(pointer.offset), value, size))
        case Region.Null              =>
          Left(
            Fault.InvalidTask(
              s"cannot write through a null SPIR-V pointer (offset=${pointer.offset}, pointee=%${pointer.pointee}, " +
                s"storage=${pointer.storageClass})"
            )
          )
        case Region.Opaque(bits) =>
          Left(Fault.InvalidTask(
            s"cannot write scalar through opaque SPIR-V pointer 0x${java.lang.Long.toHexString(bits)}"
          ))
      }

    def copySlots(destination: Pointer, origin: Pointer, size: Long): Either[Fault, Unit] =
      if (size < 0L || origin.offset > Long.MaxValue - size || destination.offset > Long.MaxValue - size)
        Left(Fault.InvalidTask(s"invalid aggregate copy size $size"))
      else {
        destination.slots match {
          case Some(target) =>
            val end = destination.offset + size
            Vec.from(target).foreach { case (destinationOffset, (pointerType, template)) =>
              if (destinationOffset >= destination.offset && destinationOffset < end) {
                val sourceOffset = origin.offset + destinationOffset - destination.offset
                val typed        = origin.slots.flatMap(_.get(sourceOffset)).map(_._2)
                val copied       = typed.filter(_.region != Region.Null).orElse {
                  readScalar(
                    Pointer(origin.region, sourceOffset, template.pointee, template.storageClass),
                    8
                  ).toOption.flatMap(raw => integerPointer(raw, template.pointee, template.storageClass).toOption)
                }
                copied.foreach(pointer => target.update(destinationOffset, pointerType -> pointer))
              }
            }
          case None => ()
        }
        Right(())
      }

    def atomicReadScalar(pointer: Pointer, size: Int where Between[1, 8]): Either[Fault, Long] =
      pointer.region match {
        case Region.Internal(internal) => internal.lock.withLock(memory.awaitRead(readExternalScalar(pointer, size)))
        case Region.External(id)       =>
          pointer.offset.refine[Long >= 0]
            .leftMap(_ =>
              Fault.InvalidTask(
                s"invalid external atomic read offset ${pointer.offset}"
              )
            )
            .flatMap(offset => flush().flatMap(_ => memory.atomicModifyValue(id, offset, size)(value => value)))
        case Region.Absolute(address) =>
          addressed(_.atomicModifyValue(address.offset(pointer.offset), size)(identity))
        case region @ (Region.Null | Region.Opaque(_)) => Left(regionFailure(region, "atomically read"))
      }

    private def regionFailure(region: Region, operation: String): Fault = region match {
      case Region.Null         => Fault.InvalidTask(s"cannot $operation a null SPIR-V pointer")
      case Region.Opaque(bits) =>
        Fault.InvalidTask(s"cannot $operation opaque SPIR-V pointer 0x${java.lang.Long.toHexString(bits)}")
      case _ => Fault.InvalidTask(s"cannot $operation SPIR-V pointer")
    }

    def atomicWriteScalar(
        pointer: Pointer,
        value: Long,
        size: Int where Between[1, 8]
    ): Either[Fault, Unit] =
      atomicModifyScalar(pointer, size)(_ => value).map(_ => ())

    def atomicModifyScalar(
        pointer: Pointer,
        size: Int where Between[1, 8]
    )(update: Long => Long): Either[Fault, Long] = pointer.region match {
      case Region.Internal(internal) => internal.lock.withLock {
          checkedRange(internal.bytes.length, pointer.offset, size.raw) match {
            case Left(error)  => Left(error)
            case Right(start) =>
              val scalarSize = size.scalarByteWidth
              val previous   = LittleEndian.decode(internal.bytes, start, scalarSize)
              LittleEndian.write(update(previous), internal.bytes, start, scalarSize)
              Right(previous)
          }
        }
      case Region.External(id) =>
        pointer.offset.refine[Long >= 0]
          .leftMap(_ =>
            Fault.InvalidTask(s"invalid external atomic offset ${pointer.offset}")
          )
          .flatMap(offset => flush().flatMap(_ => memory.atomicModifyValue(id, offset, size)(update)))
      case Region.Absolute(address) =>
        addressed(_.atomicModifyValue(address.offset(pointer.offset), size)(update))
      case region @ (Region.Null | Region.Opaque(_)) => Left(regionFailure(region, "atomically modify"))
    }

    @SuppressWarnings(Array("scalafix:DisableSyntax.return", "scalafix:DisableSyntax.var"))
    def flush(): Either[Fault, Unit] = {
      if (writes.isEmpty) return Right(())
      val allocations = writes.view.toArray.sortBy(_._1.toLong)
      var recycle     = Option.empty[DirtyPage]
      var allocation  = 0
      while (allocation < allocations.length) {
        val (id, pending) = allocations(allocation)
        val pages         = pending.view.toArray.sortBy(_._1)
        var pageIndex     = 0
        while (pageIndex < pages.length) {
          val (number, page) = pages(pageIndex)
          if (recycle.isEmpty) recycle = Some(page)
          var offset = 0
          while (offset < page.bytes.length) {
            offset = page.nextDirtyByte(offset)
            if (offset < page.bytes.length) {
              val start = offset
              offset = page.nextCleanByte(start)
              memory.write(
                id,
                ((number << 12) + start.toLong).unsafe[Long >= 0],
                Vec.from(java.util.Arrays.copyOfRange(page.bytes, start, offset))
              ) match {
                case Left(error) => return Left(error)
                case Right(())   => ()
              }
            }
          }
          pageIndex += 1
        }
        allocation += 1
      }
      // Do not clear any page until every write succeeds: failed flushes must
      // retain their pending bytes, including pages already sent to memory.
      recycle.foreach { page =>
        page.clear()
        recycledPage = Some(page)
      }
      writes.clear()
      Right(())
    }

    private def checkedRange(
        length: Int,
        offset: Long,
        size: Int
    ): Either[Fault, Int] =
      ByteRange
        .start(length, offset, size)
        .toRight(
          Fault.InvalidTask(s"internal byte range $offset + $size exceeds $length")
        )
  }
}
