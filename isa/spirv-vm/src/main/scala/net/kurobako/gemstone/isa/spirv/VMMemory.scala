package net.kurobako.gemstone.isa.spirv

import VMProgram.*
import cats.syntax.all.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.concurrent.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import scala.collection.mutable

/** Flat SPIR-V addresses. Guest memory uses address-space addresses; dispatch-owned storage (unbound program globals,
  * workgroup and private bytes) sits in apertures above every guest address, selected by the top byte.
  */
private object VMMemory {

  /** A typed address. Bounds, descriptor arrays, samplers and matrix layouts are descriptor and type metadata. */
  case class Pointer(
      address: Long,
      pointee: Int,
      storageClass: Int,
      bounds: Option[(Long, Long)] = None,
      descriptors: Option[Vector[Pointer]] = None,
      sampler: Option[Pointer] = None,
      matrixLayout: Option[MatrixLayout] = None
  )

  private inline val ApertureShift = 56
  private inline val OffsetMask    = (1L << ApertureShift) - 1L
  private inline val Alignment     = 16
  inline val GlobalAperture        = 0x81L
  inline val WorkgroupAperture     = 0x82L
  inline val PrivateAperture       = 0x83L

  def apertureAddress(aperture: Long, offset: Int): Long = (aperture << ApertureShift) | offset.toLong

  def aperture(address: Long): Long = address >>> ApertureShift

  def offsetOf(address: Long): Long >= 0 = (address & OffsetMask).unsafe[Long >= 0]

  /** The storage class an address belongs to, as generic casts observe it. */
  def storageClassOf(address: Long): Int = aperture(address) match {
    case WorkgroupAperture => StorageClass.Workgroup
    case PrivateAperture   => StorageClass.Function
    case _                 => StorageClass.CrossWorkgroup
  }

  private def aligned(offset: Int, alignment: Int): Int = {
    val boundary = math.max(Alignment, alignment)
    (offset + boundary - 1) & -boundary
  }

  /** Lays out the dispatch-owned variables of one aperture into an initial image. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  class Layout(aperture: Long) {
    private val image   = mutable.ArrayBuilder.ofByte()
    private val patches = mutable.ArrayBuffer.empty[(Int, Array[Byte])]
    private var size    = 0

    def append(bytes: Vec[Byte], alignment: Int): Either[Fault, Long] = {
      val start = aligned(size, alignment)
      if (start < size || bytes.length > Int.MaxValue - alignment - start)
        Left(Fault.InvalidRequest(s"SPIR-V storage of ${start.toLong + bytes.length} bytes exceeds its aperture"))
      else {
        (size until start).foreach(_ => image += 0)
        image.addAll(bytes)
        size = start + bytes.length
        Right(apertureAddress(aperture, start))
      }
    }

    def reserve(bytes: Long, alignment: Int): Either[Fault, Long] =
      if (bytes < 0L || bytes > Int.MaxValue - Alignment - size)
        Left(Fault.InvalidRequest(s"invalid SPIR-V storage size $bytes"))
      else append(Vec.unsafeWrapArray(Array.ofDim[Byte](bytes.toInt)), alignment)

    /** Overwrites bytes already appended at `address` once the image is produced. */
    def patch(address: Long, bytes: Array[Byte]): Unit = patches += (offsetOf(address).raw.toInt -> bytes)

    def result(): Array[Byte] = {
      val bytes = image.result()
      patches.foreach((offset, data) => Array.copy(data, 0, bytes, offset, data.length))
      bytes
    }
  }

  /** Bump-allocated private storage of one invocation. A function's variables are released when it returns. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  class Arena {
    var bytes: Array[Byte] = Array.ofDim[Byte](256)
    var top: Int           = 0

    /** Pointers stored here with descriptor metadata, valid while the stored address bits are unchanged. */
    val pointers: mutable.LongMap[Pointer] = mutable.LongMap.empty

    def reset(image: Array[Byte]): Unit = {
      reserve(image.length)
      Array.copy(image, 0, bytes, 0, image.length)
      top = image.length
      // Clearing an empty LongMap still walks and reallocates its tables.
      if (pointers.nonEmpty) pointers.clear()
    }

    def allocate(size: Int, alignment: Int): Either[Fault, Long] = {
      val start = aligned(top, alignment)
      if (size < 0 || size > Int.MaxValue - alignment - start)
        Left(Fault.InvalidRequest(s"SPIR-V private storage of ${start.toLong + size} bytes exceeds its aperture"))
      else {
        reserve(start + size)
        java.util.Arrays.fill(bytes, start, start + size, 0.toByte)
        top = start + size
        Right(apertureAddress(PrivateAperture, start))
      }
    }

    private def reserve(size: Int): Unit =
      if (size > bytes.length)
        bytes = java.util.Arrays.copyOf(bytes, math.max(size, math.min(Int.MaxValue - Alignment, bytes.length * 2)))
  }

  /** Storage and relocation state shared by every workgroup of one dispatch. */
  class Storage(
      val memory: AddressSpace,
      relocations: Vec[KernelTask.PointerRelocation],
      val globals: Array[Byte],
      val handler: Event => Either[Fault, Unit]
  ) {
    val lock: ReentrantLock      = ReentrantLock()
    val deviceQueue: DeviceQueue = DeviceQueue()

    // Client pointers stored in allocations without native pointer bytes; a pointer store replaces its entry.
    val pointers: ConcurrentHashMap[(AllocationId, Long), (AllocationId, Long)] = {
      val table = ConcurrentHashMap[(AllocationId, Long), (AllocationId, Long)]()
      relocations.foreach {
        case KernelTask.PointerRelocation(KernelTask.RelocationSource.Memory(source), offset, target, targetOffset) =>
          val _ = table.put(source -> offset.raw, target -> targetOffset.raw)
        case _ => ()
      }
      table
    }
  }

  // The executing invocation's storage and helper status are rebound before each resume.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.null"))
  class MemoryState(shared: Storage) {
    private val memory                                = shared.memory
    def handler: Event => Either[Fault, Unit]         = shared.handler
    def deviceQueue: DeviceQueue                      = shared.deviceQueue
    def flushHostReads(): Either[Fault, Unit]         = memory.flushHostReads()
    def fence(): Either[Fault, Unit]                  = memory.fenceHostMemory()
    var helperInvocation: Boolean                     = false
    var invocation: Arena                             = Arena()
    var workgroup: Array[Byte]                        = Array.emptyByteArray
    private val descriptors                           = mutable.HashMap.empty[(Long, Int), Long]
    private def suppressed(pointer: Pointer): Boolean =
      helperInvocation && pointer.storageClass != StorageClass.Output && storage(pointer.address) == null

    private def storage(address: Long): Array[Byte] | Null = aperture(address) match {
      case PrivateAperture   => invocation.bytes
      case WorkgroupAperture => workgroup
      case GlobalAperture    => shared.globals
      case _                 => null
    }

    private def extent(bytes: Array[Byte], address: Long): Int =
      if (aperture(address) == PrivateAperture) invocation.top else bytes.length

    /** The byte offset of `size` bytes at `address` within `bytes`, or -1 when they do not fit. */
    private def offsetIn(bytes: Array[Byte], address: Long, size: Int): Int = {
      val offset = address & OffsetMask
      val limit  = extent(bytes, address)
      if (offset <= limit && size.toLong <= limit - offset) offset.toInt else -1
    }

    private def outOfRange(bytes: Array[Byte], address: Long, size: Int): Fault = Fault.InvalidRequest(
      s"SPIR-V access at 0x${java.lang.Long.toHexString(address)} + $size exceeds ${extent(bytes, address)} bytes"
    )

    private def start(bytes: Array[Byte], address: Long, size: Int): Either[Fault, Int] = {
      val offset = offsetIn(bytes, address, size)
      if (offset >= 0) Right(offset) else Left(outOfRange(bytes, address, size))
    }

    private def guest(address: Long, size: Int >= 0, operation: String): Either[Fault, AddressSpace.Location] =
      if (address == 0L) Left(Fault.InvalidRequest(s"cannot $operation a null SPIR-V pointer"))
      else memory.locate(GuestAddress(address), size)

    private def outsideBounds(pointer: Pointer, size: Long): Boolean = pointer.bounds match {
      case Some((begin, end)) => pointer.address < begin || pointer.address > end || size > end - pointer.address
      case None               => false
    }

    private def relocation(id: AllocationId, offset: Long): Option[(AllocationId, Long)] =
      if (shared.pointers.isEmpty || memory.hasNativePointers(id)) None
      else Option(shared.pointers.get(id -> offset))

    private def addressOf(target: (AllocationId, Long)): Either[Fault, Long] =
      target._2.refine[Long >= 0]
        .left.map(_ => Fault.InvalidRequest(s"negative SPIR-V pointer relocation offset ${target._2}"))
        .flatMap(memory.address(target._1, _)).map(_.raw)

    /** Whether `size` bytes at `pointer` lie inside dispatch-owned storage, or the pointer names guest memory. */
    def contains(pointer: Pointer, size: Long): Boolean = {
      val bytes = storage(pointer.address)
      bytes == null || (size <= Int.MaxValue && start(bytes, pointer.address, size.toInt).isRight)
    }

    def read(pointer: Pointer, size: Int >= 0): AddressSpace.ReadResult[Array[Byte]] =
      if (outsideBounds(pointer, size.raw)) Right(Array.ofDim[Byte](size.raw))
      else {
        val bytes = storage(pointer.address)
        if (bytes != null)
          start(bytes, pointer.address, size.raw).map(at => java.util.Arrays.copyOfRange(bytes, at, at + size.raw))
        else
          guest(pointer.address, size, "read through") match {
            case Left(fault)                                     => Left(fault)
            case Right(AddressSpace.Location.Within(id, offset)) => memory.readDeferred(id, offset, size).map(_.toArray)
            case Right(AddressSpace.Location.Absolute(address))  => memory.readDeferred(address, size).map(_.toArray)
          }
      }

    /** Reads a scalar from dispatch-owned storage into `registers(slot)`; false leaves it to [[readScalar]]. */
    def readLocalScalar(pointer: Pointer, size: Int in 1 ~ 8, registers: Array[Long], slot: Int): Boolean =
      !outsideBounds(pointer, size.raw) && {
        val bytes = storage(pointer.address)
        bytes != null && {
          val at = offsetIn(bytes, pointer.address, size.raw)
          at >= 0 && {
            registers(slot) = LittleEndian.decode(bytes, at, size.widen[Int in 0 ~ 8])
            true
          }
        }
      }

    def readScalar(pointer: Pointer, size: Int in 1 ~ 8): AddressSpace.ReadResult[Long] =
      if (outsideBounds(pointer, size.raw)) Right(0L) else load(pointer.address, size)

    private def load(address: Long, size: Int in 1 ~ 8): AddressSpace.ReadResult[Long] = {
      val bytes = storage(address)
      if (bytes != null) {
        val at = offsetIn(bytes, address, size.raw)
        if (at >= 0) Right(LittleEndian.decode(bytes, at, size.widen[Int in 0 ~ 8]))
        else Left(outOfRange(bytes, address, size.raw))
      } else
        guest(address, size.widen[Int >= 0], "read through") match {
          case Left(fault)                                     => Left(fault)
          case Right(AddressSpace.Location.Within(id, offset)) =>
            (if (size.raw == 8) relocation(id, offset.raw) else None) match {
              case Some(target) => addressOf(target)
              case None         => memory.readValueDeferred(id, offset, size)
            }
          case Right(AddressSpace.Location.Absolute(target)) =>
            memory.readDeferred(target, size.widen[Int >= 0]).map(LittleEndian.decode(_, 0, size.widen[Int in 0 ~ 8]))
        }
    }

    /** A word of an image or sampler record at `offset` within it. Records are immutable facade metadata while a
      * dispatch runs, so each word is read once.
      */
    def descriptor(record: Pointer, offset: Long, size: Int in 1 ~ 8): AddressSpace.ReadResult[Long] = {
      val word = record.copy(address = record.address + offset)
      if (outsideBounds(word, size.raw)) Right(0L)
      else {
        val key = (word.address, size.raw)
        descriptors.get(key) match {
          case Some(value) => Right(value)
          case None        => load(word.address, size).map { value =>
              descriptors.update(key, value)
              value
            }
        }
      }
    }

    // Image and sampler records are immutable facade metadata, even when their bytes use host backing. Their declared
    // relocations remain authoritative; ordinary shader pointer loads continue to read live native pointer bits.
    def descriptorPointer(record: Pointer, pointee: Int, storageClass: Int): Either[Fault, Option[Pointer]] =
      guest(record.address, 8, "read through").flatMap {
        case AddressSpace.Location.Within(id, offset) =>
          Option(shared.pointers.get(id -> offset.raw)) match {
            case Some(target) => addressOf(target).map(address => Some(Pointer(address, pointee, storageClass)))
            case None         => Right(None)
          }
        case AddressSpace.Location.Absolute(_) => Right(None)
      }

    def write(pointer: Pointer, bytes: Array[Byte]): Either[Fault, Unit] =
      if (suppressed(pointer) || outsideBounds(pointer, bytes.length)) Right(())
      else {
        val target = storage(pointer.address)
        if (target != null)
          start(target, pointer.address, bytes.length).map(at => Array.copy(bytes, 0, target, at, bytes.length))
        else if (pointer.address == 0L) Left(nullWrite(pointer))
        else {
          val data = Vec.from(bytes)
          forgetRelocations(pointer.address, data.nonNegativeSize)
          memory.write(GuestAddress(pointer.address), data)
        }
      }

    def writeScalar(pointer: Pointer, value: Long, size: Int in 1 ~ 8): Either[Fault, Unit] =
      if (suppressed(pointer) || outsideBounds(pointer, size.raw)) AddressSpace.Ready
      else {
        val target = storage(pointer.address)
        if (target != null) {
          val at = offsetIn(target, pointer.address, size.raw)
          if (at < 0) Left(outOfRange(target, pointer.address, size.raw))
          else {
            LittleEndian.write(value, target, at, size.widen[Int in 0 ~ 8])
            AddressSpace.Ready
          }
        } else if (pointer.address == 0L) Left(nullWrite(pointer))
        else {
          forgetRelocations(pointer.address, size.widen)
          memory.writeValue(GuestAddress(pointer.address), value, size)
        }
      }

    private def nullWrite(pointer: Pointer): Fault = Fault.InvalidRequest(
      s"cannot write through a null SPIR-V pointer (pointee=%${pointer.pointee}, storage=${pointer.storageClass})"
    )

    /** Stores `source` at `target` and reports the stored pointer when the target allocation has no native pointers.
      * Private storage also keeps the descriptor metadata that the address bits cannot carry.
      */
    def writePointer(target: Pointer, source: Pointer): Either[Fault, Unit] =
      writeScalar(target, source.address, 8).flatMap { _ =>
        if (suppressed(target) || outsideBounds(target, 8)) Right(())
        else if (aperture(target.address) == PrivateAperture) {
          if (
            source.bounds.isEmpty && source.descriptors.isEmpty && source.sampler.isEmpty && source.matrixLayout.isEmpty
          )
            invocation.pointers -= target.address
          else invocation.pointers.update(target.address, source)
          Right(())
        } else if (storage(target.address) != null) Right(())
        else
          guest(target.address, 8, "write through").flatMap {
            case AddressSpace.Location.Within(destination, offset) if !memory.hasNativePointers(destination) =>
              val located =
                if (storage(source.address) != null || source.address == 0L) None
                else memory.locate(GuestAddress(source.address), 0).toOption
              located match {
                case Some(AddressSpace.Location.Within(allocation, targetOffset)) =>
                  shared.handler(Event.PointerRelocation(destination, offset, allocation, targetOffset))
                case _ => Right(())
              }
            case _ => Right(())
          }
      }

    def readPointer(source: Pointer, pointee: Int, storageClass: Int): AddressSpace.ReadResult[Pointer] =
      readScalar(source, 8).map { address =>
        val described =
          if (aperture(source.address) != PrivateAperture || outsideBounds(source, 8)) None
          else invocation.pointers.get(source.address).filter(_.address == address)
        described.fold(Pointer(address, pointee, storageClass))(_.copy(pointee = pointee, storageClass = storageClass))
      }

    /** Copies `size` bytes. Pointers keep their private descriptor metadata, and client pointers in allocations without
      * native pointers arrive relocated.
      */
    def copy(destination: Pointer, origin: Pointer, size: Int >= 0): AddressSpace.ReadResult[Unit] =
      read(origin, size).flatMap(write(destination, _)).flatMap { _ =>
        val span = size.raw.toLong - 8L
        if (
          span < 0L || suppressed(destination) || outsideBounds(destination, size.raw) ||
          outsideBounds(origin, size.raw)
        )
          Right(())
        else {
          def moved(at: Long): Pointer = destination.copy(address = destination.address + at)
          if (aperture(destination.address) == PrivateAperture)
            invocation.pointers.keys.view.filter(key =>
              key >= destination.address && key - destination.address <= span
            ).toVector.foreach(invocation.pointers -= _)
          if (aperture(origin.address) == PrivateAperture) {
            val described = invocation.pointers.view.filter { case (key, pointer) =>
              key >= origin.address && key - origin.address <= span && load(key, 8).contains(pointer.address)
            }.toVector
            described.traverse_ { case (key, pointer) => writePointer(moved(key - origin.address), pointer) }
          } else if (storage(origin.address) != null || origin.address == 0L || shared.pointers.isEmpty) Right(())
          else
            memory.locate(GuestAddress(origin.address), size) match {
              case Right(AddressSpace.Location.Within(id, offset)) if !memory.hasNativePointers(id) =>
                relocations(id, offset.raw, span).traverse_ { case (at, target) =>
                  addressOf(target).flatMap(address =>
                    writePointer(moved(at), Pointer(address, 0, StorageClass.CrossWorkgroup))
                  )
                }
              case _ => Right(())
            }
        }
      }

    /** Client relocations no longer describe a pointer slot that a plain write overlaps, even in part. */
    private def forgetRelocations(address: Long, size: Int >= 0): Unit =
      if (!shared.pointers.isEmpty && size.raw > 0)
        memory.locate(GuestAddress(address), size) match {
          case Right(AddressSpace.Location.Within(id, offset)) if !memory.hasNativePointers(id) =>
            val first = offset.raw - 7L
            relocations(id, first, size.raw + 6L).foreach { case (at, _) => shared.pointers.remove(id -> (first + at)) }
          case _ => ()
        }

    /** Relocated client pointers whose 8 bytes start within `span` bytes of `offset`, keyed by distance from it. */
    private def relocations(id: AllocationId, offset: Long, span: Long): Vector[(Long, (AllocationId, Long))] =
      if (span < shared.pointers.size.toLong)
        Vector.range(0L, span + 1L).flatMap(at => Option(shared.pointers.get(id -> (offset + at))).map(at -> _))
      else {
        val found = Vector.newBuilder[(Long, (AllocationId, Long))]
        shared.pointers.forEach { case ((source, at), target) =>
          if (source == id && at >= offset && at - offset <= span) found += (at - offset) -> target
        }
        found.result()
      }

    def atomicReadScalar(pointer: Pointer, size: Int in 1 ~ 8): Either[Fault, Long] =
      atomicModifyScalar(pointer, size, write = false)(identity)

    def atomicWriteScalar(pointer: Pointer, value: Long, size: Int in 1 ~ 8): Either[Fault, Unit] =
      atomicModifyScalar(pointer, size)(_ => value).map(_ => ())

    def atomicModifyScalar(pointer: Pointer, size: Int in 1 ~ 8, write: Boolean = true)(
        update: Long => Long
    ): Either[Fault, Long] =
      if ((write && suppressed(pointer)) || outsideBounds(pointer, size.raw)) Right(0L)
      else {
        val target = storage(pointer.address)
        if (target != null) {
          def modify(): Either[Fault, Long] = start(target, pointer.address, size.raw).map { at =>
            val width    = size.widen[Int in 0 ~ 8]
            val previous = LittleEndian.decode(target, at, width)
            if (write) LittleEndian.write(update(previous), target, at, width)
            previous
          }
          if (aperture(pointer.address) == GlobalAperture) shared.lock.withLock(modify()) else modify()
        } else
          guest(pointer.address, size.widen[Int >= 0], "atomically access").flatMap { _ =>
            if (write) forgetRelocations(pointer.address, size.widen)
            memory.atomicModifyValue(GuestAddress(pointer.address), size)(update)
          }
      }
  }
}
