package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.vm.AddressSpace
import net.kurobako.gemstone.memory.GuestAddress

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.binary.{ByteRange, LittleEndian}
import net.kurobako.gemstone.concurrent.withLock
import net.kurobako.gemstone.memory.MemoryValueByteWidth
import net.kurobako.gemstone.vm.*

import cats.syntax.all.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import java.util.concurrent.locks.ReentrantLock
import scala.collection.mutable

private[v1_6] object VMMemory {
  enum Region {
    case External(id: MemoryRegion)
    case Internal(memory: InternalMemory)
    case Opaque(bits: Long)
    case Null
  }

  final class InternalMemory(val bytes: Array[Byte]) {
    val lock: ReentrantLock = ReentrantLock()
  }

  final case class Pointer(
      region: Region,
      offset: Long,
      pointee: Int,
      storageClass: Int,
      slots: Option[mutable.LongMap[(Int, Pointer)]] = None
  )

  private val VirtualAddressSpan = 0x0000010000000000L

  /** One sparse dirty page in SPIR-V's buffered external-memory view. */
  final class DirtyPage {
    val bytes         = Array.ofDim[Byte](4096)
    private val dirty = Array.ofDim[Long](64)

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
  final class MemoryState(
      memory: Memory,
      relocations: Vec[KernelTask.PointerRelocation],
      observer: Observer[Event],
      observation: ObservationContext
  ) {
    def flushHostReads(): Either[Fault, Unit] = memory.flushHostReads()
    def fence(): Either[Fault, Unit]          = flush().flatMap(_ => memory.fenceHostMemory())
    private val writes                        = mutable.HashMap.empty[MemoryRegion, mutable.LongMap[DirtyPage]]
    private val externalVirtualAddresses      = mutable.HashMap.empty[MemoryRegion, Long]
    private val internalVirtualAddresses      = java.util.IdentityHashMap[Array[Byte], java.lang.Long]()
    private val virtualPointers               = mutable.LongMap.empty[Pointer]
    private val exceptionalAddresses          = mutable.HashMap.empty[(Region, Long), Long]
    private val exceptionalPointers           = mutable.LongMap.empty[Pointer]
    private val pointerRelocations: mutable.HashMap[(MemoryRegion, Long), (MemoryRegion, Long)] =
      mutable.HashMap.from(relocations.view.collect {
        case KernelTask.PointerRelocation(
              KernelTask.RelocationSource.Memory(source),
              sourceOffset,
              target,
              targetOffset
            ) =>
          (source -> (sourceOffset: Long)) -> (target -> (targetOffset: Long))
      })
    private val argumentRelocations = {
      val result = mutable.HashMap.empty[Int, mutable.LongMap[(MemoryRegion, Long)]]
      relocations.foreach {
        case KernelTask.PointerRelocation(
              KernelTask.RelocationSource.Argument(argument),
              sourceOffset,
              target,
              targetOffset
            ) =>
          result
            .getOrElseUpdate(argument, mutable.LongMap.empty)
            .update(sourceOffset, target -> targetOffset)
        case _ => ()
      }
      result
    }
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var nextVirtualAddress = 0x0000100000000000L
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var nextExceptionalAddress = Long.MinValue

    /** IdentityHashMap uses null for a missing Java entry; confine it to this interop pattern. */
    @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.return"))
    def pointerInteger(pointer: Pointer): Either[Fault, Long] = {
      (memory, pointer.region) match {
        case (addresses: AddressSpace, Region.External(id)) if pointer.offset >= 0 =>
          return addresses.address(id, pointer.offset.refineUnsafe[Positive0]).map(_.value)
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
        case Region.Opaque(bits) =>
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

    def argumentRelocation(index: Int, offset: Long): Option[(MemoryRegion, Long)] =
      argumentRelocations.get(index).flatMap(_.get(offset))

    def nativePointerStorage(pointer: Pointer): Boolean = pointer.region match {
      case Region.External(region) => memory.hasNativePointers(region)
      case _                       => false
    }

    def allocationRelocation(source: MemoryRegion, offset: Long): Option[(MemoryRegion, Long)] =
      if (memory.hasNativePointers(source)) None else pointerRelocations.get(source -> offset)

    def recordPointerRelocation(pointer: Pointer, destination: Pointer): Either[Fault, Unit] =
      destination.region match {
        case Region.External(destinationAllocation)
            if destinationAllocation.value != 0 && !memory.hasNativePointers(destinationAllocation) =>
          destination.offset.refineEither[Positive0] match {
            case Left(_) => Left(Fault.InvalidTask(
                s"negative SPIR-V pointer relocation destination offset ${destination.offset}"
              ))
            case Right(sourceOffset) => pointer.region match {
                case Region.External(pointerAllocation) if pointerAllocation.value != 0 =>
                  pointer.offset.refineEither[Positive0] match {
                    case Left(_) => Left(Fault.InvalidTask(
                        s"negative SPIR-V pointer relocation target offset ${pointer.offset}"
                      ))
                    case Right(targetOffset) =>
                      pointerRelocations.update(
                        destinationAllocation -> destination.offset,
                        pointerAllocation     -> pointer.offset
                      )
                      observe(
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

    def observe(event: Event): Either[Fault, Unit] =
      observer.observe(observation, event).leftMap(error => Fault.Observer(error.message))

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
                case addresses: AddressSpace => addresses.locate(GuestAddress(address), 0).map(location =>
                    Pointer(Region.External(location.region), location.offset, pointee, storageClass)
                  ).orElse(Right(Pointer(Region.Opaque(address), 0L, pointee, storageClass)))
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

    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    def read(pointer: Pointer, size: Int :| Positive0): Either[Fault, Array[Byte]] =
      pointer.region match {
        case Region.Internal(internal) =>
          checkedRange(internal.bytes.length, pointer.offset, size).map(start =>
            java.util.Arrays.copyOfRange(internal.bytes, start, start + size)
          )
        case Region.External(id) =>
          pointer.offset.refineEither[Positive0]
            .leftMap(_ =>
              Fault.InvalidTask(s"invalid external read offset ${pointer.offset}")
            )
            .flatMap(offset => memory.read(id, offset, size)) match {
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
        case region @ (Region.Null | Region.Opaque(_)) => Left(regionFailure(region, "read through"))
      }

    def readScalar(pointer: Pointer, size: MemoryValueByteWidth): Either[Fault, Long] = {
      val relocation =
        if (size.value != 8) None
        else
          pointer.region match {
            case Region.External(source) => allocationRelocation(source, pointer.offset)
            case Region.Internal(_)      => None
            case Region.Opaque(_)        => None
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
        size: MemoryValueByteWidth
    ): Either[Fault, Long] = pointer.region match {
      case Region.Internal(internal) =>
        checkedRange(internal.bytes.length, pointer.offset, size.value)
          .map(start => LittleEndian.decode(internal.bytes, start, size.scalarByteWidth))
      case Region.External(id) =>
        pointer.offset.refineEither[Positive0]
          .leftMap(_ =>
            Fault.InvalidTask(
              s"invalid external scalar read offset ${pointer.offset}"
            )
          )
          .flatMap(offset => memory.readValue(id, offset, size)) match {
          case Left(error)   => Left(error)
          case Right(source) =>
            var value = source
            writes.get(id).foreach { pages =>
              var index = 0
              while (index < size.value) {
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
              pending.getOrElseUpdate(absolute >>> 12, DirtyPage()).update(pageOffset, bytes(index))
              index += 1
            }
            Right(())
          }
        case Region.Null =>
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

    def writeScalar(pointer: Pointer, value: Long, size: MemoryValueByteWidth): Either[Fault, Unit] =
      pointer.region match {
        case Region.Internal(internal) =>
          checkedRange(internal.bytes.length, pointer.offset, size.value)
            .map(start =>
              LittleEndian.write(value, internal.bytes, start, size.scalarByteWidth)
            )
        case Region.External(id) =>
          if (pointer.offset < 0L || pointer.offset > Long.MaxValue - size.value.toLong)
            Left(
              Fault.InvalidTask(s"invalid external write offset ${pointer.offset}")
            )
          else {
            val pending = writes.getOrElseUpdate(id, mutable.LongMap.empty)
            LittleEndian.write(value, size.scalarByteWidth) { (index, byte) =>
              val absolute   = pointer.offset + index.toLong
              val pageOffset = (absolute & 4095L).toInt
              pending
                .getOrElseUpdate(absolute >>> 12, DirtyPage())
                .update(pageOffset, byte)
            }
            Right(())
          }
        case Region.Null =>
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
                    MemoryValueByteWidth.Qword
                  ).toOption.flatMap(raw => integerPointer(raw, template.pointee, template.storageClass).toOption)
                }
                copied.foreach(pointer => target.update(destinationOffset, pointerType -> pointer))
              }
            }
          case None => ()
        }
        Right(())
      }

    def atomicReadScalar(pointer: Pointer, size: MemoryValueByteWidth): Either[Fault, Long] =
      pointer.region match {
        case Region.Internal(internal) => internal.lock.withLock(readExternalScalar(pointer, size))
        case Region.External(id)       =>
          pointer.offset.refineEither[Positive0]
            .leftMap(_ =>
              Fault.InvalidTask(
                s"invalid external atomic read offset ${pointer.offset}"
              )
            )
            .flatMap(offset => flush().flatMap(_ => memory.atomicModifyValue(id, offset, size)(value => value)))
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
        size: MemoryValueByteWidth
    ): Either[Fault, Unit] =
      atomicModifyScalar(pointer, size)(_ => value).map(_ => ())

    def atomicModifyScalar(
        pointer: Pointer,
        size: MemoryValueByteWidth
    )(update: Long => Long): Either[Fault, Long] = pointer.region match {
      case Region.Internal(internal) => internal.lock.withLock {
          checkedRange(internal.bytes.length, pointer.offset, size.value) match {
            case Left(error)  => Left(error)
            case Right(start) =>
              val scalarSize = size.scalarByteWidth
              val previous   = LittleEndian.decode(internal.bytes, start, scalarSize)
              LittleEndian.write(update(previous), internal.bytes, start, scalarSize)
              Right(previous)
          }
        }
      case Region.External(id) =>
        pointer.offset.refineEither[Positive0]
          .leftMap(_ =>
            Fault.InvalidTask(s"invalid external atomic offset ${pointer.offset}")
          )
          .flatMap(offset => flush().flatMap(_ => memory.atomicModifyValue(id, offset, size)(update)))
      case region @ (Region.Null | Region.Opaque(_)) => Left(regionFailure(region, "atomically modify"))
    }

    @SuppressWarnings(Array("scalafix:DisableSyntax.return", "scalafix:DisableSyntax.var"))
    def flush(): Either[Fault, Unit] = {
      val allocations = writes.view.toArray.sortBy(_._1.value)
      var allocation  = 0
      while (allocation < allocations.length) {
        val (id, pending) = allocations(allocation)
        val pages         = pending.view.toArray.sortBy(_._1)
        var pageIndex     = 0
        while (pageIndex < pages.length) {
          val (number, page) = pages(pageIndex)
          var offset         = 0
          while (offset < page.bytes.length) {
            offset = page.nextDirtyByte(offset)
            if (offset < page.bytes.length) {
              val start = offset
              offset = page.nextCleanByte(start)
              memory.write(
                id,
                ((number << 12) + start.toLong).refineUnsafe[Positive0],
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
