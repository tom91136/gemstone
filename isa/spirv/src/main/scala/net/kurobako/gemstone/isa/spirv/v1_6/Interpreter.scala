package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.Col

import net.kurobako.gemstone.binary.Bits.mask
import net.kurobako.gemstone.binary.Bits.signExtend
import net.kurobako.gemstone.binary.ByteRange
import net.kurobako.gemstone.binary.IEEE754
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.execution.*
import net.kurobako.gemstone.isa.ValidationResult
import scala.collection.concurrent.TrieMap
import scala.collection.mutable // scalafix:ok GemstoneRuntimeCollections.mutable-collection; invocation machine state
import scala.util.control.NonFatal

/** Typed SPIR-V kernel execution over process-neutral allocation identities.
  *
  * This interpreter deliberately operates on the generated instruction enum. It does not translate pointers into host
  * addresses: external memory is always accessed through [[ExecutionMemory]], while Function and local storage use
  * invocation/work-group-owned byte buffers.
  */
@SuppressWarnings(
  Array(
    "scalafix:DisableSyntax.var",
    "scalafix:DisableSyntax.return",
    "scalafix:GemstoneRuntimeCollections.mutable-collection"
  )
)
object Interpreter {
  private val VirtualAddressSpan         = 0x0000010000000000L
  private val OpenCLUnaryFloatingOpcodes = Set(
    0, 1, 2, 3, 4, 5, 6, 8, 9, 11, 12, 14, 15, 16, 19, 20, 21, 22, 23, 25, 37, 38, 39, 40, 41,
    53, 55, 56, 57, 59, 60, 61, 62, 63, 64, 66, 81, 83, 84, 85, 86, 87, 88, 90, 91, 92, 93, 94
  )
  private val OpenCLBinaryFloatingOpcodes = Set(7, 10, 24, 29, 32, 50)

  final class PreparedProgram private[Interpreter] (private[Interpreter] val value: DispatchProgram)

  final case class PerformanceSnapshot(
      dispatches: Long,
      collectiveDispatches: Long,
      invocations: Long,
      instructions: Long,
      invocationValueInstances: Long,
      denseValueSlots: Long,
      estimatedDenseValueBytes: Long,
      elapsedNanos: Long
  )

  /** Opt-in counters for separating interpreter work from launcher and build-tool overhead. A collector may be reused
    * across dispatches; snapshots are cumulative.
    */
  final class PerformanceMetrics() {
    private var dispatchCount                = 0L
    private var collectiveDispatchCount      = 0L
    private var invocationCount              = 0L
    private var instructionCount             = 0L
    private var invocationValueInstanceCount = 0L
    private var denseValueSlotCount          = 0L
    private var estimatedDenseValueByteCount = 0L
    private var dispatchElapsedNanos         = 0L

    private[Interpreter] def recordDispatch(collective: Boolean): Unit = {
      dispatchCount += 1L
      if (collective) collectiveDispatchCount += 1L
    }

    private[Interpreter] def recordInvocation(): Unit = invocationCount += 1L

    private[Interpreter] def recordInstruction(): Unit = instructionCount += 1L

    private[Interpreter] def recordInvocationValues(denseSlots: Int, bytesPerSlot: Int): Unit = {
      invocationValueInstanceCount += 1L
      denseValueSlotCount += denseSlots.toLong
      estimatedDenseValueByteCount += denseSlots.toLong * bytesPerSlot
    }

    private[Interpreter] def recordElapsed(nanos: Long): Unit = dispatchElapsedNanos += nanos

    private[Interpreter] def merge(other: PerformanceMetrics): Unit = synchronized {
      invocationCount += other.invocationCount
      instructionCount += other.instructionCount
      invocationValueInstanceCount += other.invocationValueInstanceCount
      denseValueSlotCount += other.denseValueSlotCount
      estimatedDenseValueByteCount += other.estimatedDenseValueByteCount
      dispatchElapsedNanos += other.dispatchElapsedNanos
    }

    def snapshot: PerformanceSnapshot = PerformanceSnapshot(
      dispatchCount,
      collectiveDispatchCount,
      invocationCount,
      instructionCount,
      invocationValueInstanceCount,
      denseValueSlotCount,
      estimatedDenseValueByteCount,
      dispatchElapsedNanos
    )
  }

  private enum RuntimeType {
    case Void
    case Bool
    case Integer(width: Int, signed: Boolean)
    case Floating(width: Int)
    case VectorType(component: Int, count: Int)
    case Array(element: Int, count: Long)
    case Structure(members: Col[Int])
    case Pointer(storageClass: Int, pointee: Int)
    case Function(result: Int, parameters: Col[Int])
    case Event
  }

  private enum Region {
    case External(id: AllocationId)
    case Internal(bytes: Array[Byte])
    case Null
  }

  private final case class Pointer(
      region: Region,
      offset: Long,
      pointee: Int,
      storageClass: Int,
      slots: Option[mutable.LongMap[(Int, Pointer)]] = None
  )

  private enum RuntimeValue {
    case Bits(typeId: Int, lanes: Array[Long])
    case Address(typeId: Int, pointer: Pointer)
  }

  private final class BaseScalarValues(
      constants: Map[Int, RuntimeValue],
      globals: Map[Int, RuntimeValue],
      idBound: Long
  ) {
    private val denseLimit = 1000000L
    private val types      =
      if (idBound > 0L && idBound <= denseLimit) Array.fill(idBound.toInt)(-1)
      else Array.empty[Int]
    private val bits = Array.ofDim[Long](types.length)

    constants.iterator.concat(globals.iterator).foreach {
      case (key, RuntimeValue.Bits(typeId, Array(value))) if key >= 0 && key < types.length =>
        types(key) = typeId
        bits(key) = value
      case _ => ()
    }

    def scalarType(key: Int): Int = if (key >= 0 && key < types.length) types(key) else -1

    def scalarBits(key: Int): Long = bits(key)
  }

  /** Per-invocation writes over immutable module/dispatch values. A large oneDPL launch can contain hundreds of
    * thousands of invocations, so eagerly copying every SPIR-V constant into every lane dominates execution.
    */
  private final class InvocationValues(
      constants: Map[Int, RuntimeValue],
      globals: Map[Int, RuntimeValue],
      idBound: Long,
      baseScalars: BaseScalarValues,
      metrics: PerformanceMetrics | Null
  ) {
    private val denseLimit = 1000000L
    private val dense      =
      if (idBound > 0L && idBound <= denseLimit) Array.ofDim[RuntimeValue | Null](idBound.toInt)
      else Array.empty[RuntimeValue | Null]
    private val denseScalarTypes = Array.fill(dense.length)(-1)
    private val denseScalarBits  = Array.ofDim[Long](dense.length)
    private val touched          = mutable.ArrayBuffer.empty[Int]
    private val overflow         = mutable.LongMap.empty[RuntimeValue]

    if (metrics != null) metrics.recordInvocationValues(dense.length, 16)

    def get(key: Long): Option[RuntimeValue] = {
      val local =
        if (key >= 0L && key < dense.length.toLong) {
          val index = key.toInt
          if (denseScalarTypes(index) >= 0)
            Some(RuntimeValue.Bits(denseScalarTypes(index), Array(denseScalarBits(index))))
          else
            dense(index) match {
              case null  => None
              case value => Some(value)
            }
        } else overflow.get(key)
      local.orElse(globals.get(key.toInt)).orElse(constants.get(key.toInt))
    }

    def update(key: Long, value: RuntimeValue): Unit =
      if (key >= 0L && key < dense.length.toLong) {
        val index = key.toInt
        if (dense(index) == null && denseScalarTypes(index) < 0) touched += index
        value match {
          case RuntimeValue.Bits(typeId, Array(bits)) =>
            dense(index) = null
            denseScalarTypes(index) = typeId
            denseScalarBits(index) = bits
          case _ =>
            dense(index) = value
            denseScalarTypes(index) = -1
        }
      } else overflow.update(key, value)

    def scalarType(key: Int): Int =
      if (key >= 0 && key < dense.length) {
        val localType = denseScalarTypes(key)
        if (localType >= 0) localType
        else if (dense(key) != null) -1
        else {
          val baseType = baseScalars.scalarType(key)
          if (baseType >= 0) baseType else fallbackScalarType(key)
        }
      } else
        overflow.get(key.toLong) match {
          case Some(RuntimeValue.Bits(typeId, Array(_))) => typeId
          case Some(_)                                   => -1
          case None                                      => fallbackScalarType(key)
        }

    def scalarBits(key: Int): Long =
      if (key >= 0 && key < dense.length) {
        if (denseScalarTypes(key) >= 0) denseScalarBits(key)
        else if (baseScalars.scalarType(key) >= 0) baseScalars.scalarBits(key)
        else fallbackScalarBits(key)
      } else
        overflow.get(key.toLong) match {
          case Some(RuntimeValue.Bits(_, Array(value))) => value
          case Some(_) => throw IllegalStateException(s"SPIR-V value %$key is not scalar")
          case None    => fallbackScalarBits(key)
        }

    private def fallbackScalarType(key: Int): Int =
      globals.get(key).orElse(constants.get(key)) match {
        case Some(RuntimeValue.Bits(typeId, Array(_))) => typeId
        case _                                         => -1
      }

    private def fallbackScalarBits(key: Int): Long =
      globals.get(key).orElse(constants.get(key)) match {
        case Some(RuntimeValue.Bits(_, Array(value))) => value
        case _ => throw IllegalStateException(s"SPIR-V value %$key is not scalar")
      }

    def updateScalar(key: Int, typeId: Int, bits: Long): Unit =
      if (key >= 0 && key < dense.length) {
        if (dense(key) == null && denseScalarTypes(key) < 0) touched += key
        dense(key) = null
        denseScalarTypes(key) = typeId
        denseScalarBits(key) = bits
      } else overflow.update(key.toLong, RuntimeValue.Bits(typeId, Array(bits)))

    def clear(): Unit = {
      var index = 0
      while (index < touched.length) {
        dense(touched(index)) = null
        denseScalarTypes(touched(index)) = -1
        index += 1
      }
      touched.clear()
      overflow.clear()
    }
  }

  private enum FloatingOperation(val opcode: String) {
    case Add      extends FloatingOperation("OpFAdd")
    case Subtract extends FloatingOperation("OpFSub")
    case Multiply extends FloatingOperation("OpFMul")
    case Divide   extends FloatingOperation("OpFDiv")
    case Minimum  extends FloatingOperation("OpenCL.std fmin")
    case Maximum  extends FloatingOperation("OpenCL.std fmax")
  }

  private enum FloatingComparison(val opcode: String) {
    case OrderedEqual              extends FloatingComparison("OpFOrdEqual")
    case OrderedNotEqual           extends FloatingComparison("OpFOrdNotEqual")
    case Ordered                   extends FloatingComparison("OpOrdered")
    case UnorderedEqual            extends FloatingComparison("OpFUnordEqual")
    case UnorderedNotEqual         extends FloatingComparison("OpFUnordNotEqual")
    case UnorderedLessThan         extends FloatingComparison("OpFUnordLessThan")
    case UnorderedGreaterThan      extends FloatingComparison("OpFUnordGreaterThan")
    case UnorderedLessThanEqual    extends FloatingComparison("OpFUnordLessThanEqual")
    case UnorderedGreaterThanEqual extends FloatingComparison("OpFUnordGreaterThanEqual")
    case Unordered                 extends FloatingComparison("OpUnordered")
    case OrderedLessThan           extends FloatingComparison("OpFOrdLessThan")
    case OrderedLessThanEqual      extends FloatingComparison("OpFOrdLessThanEqual")
    case OrderedGreaterThan        extends FloatingComparison("OpFOrdGreaterThan")
    case OrderedGreaterThanEqual   extends FloatingComparison("OpFOrdGreaterThanEqual")
  }

  private enum FloatingClassification(val opcode: String) {
    case Infinite extends FloatingClassification("OpIsInf")
    case NaN      extends FloatingClassification("OpIsNan")
    case Finite   extends FloatingClassification("OpIsFinite")
    case Normal   extends FloatingClassification("OpIsNormal")
    case Signed   extends FloatingClassification("OpSignBitSet")
  }

  private final case class FunctionInfo(
      id: Int,
      parameters: Col[(Int, Int)],
      start: Int,
      end: Int,
      labels: Map[Int, Int]
  )

  private final case class CallFrame(
      function: FunctionInfo,
      pc: Int,
      currentLabel: Int,
      predecessor: Int,
      resultType: Int,
      destination: Int
  )

  private final case class GlobalInfo(pointerType: Int, storageClass: Int, initializer: Option[Int])

  private final case class DispatchProgram(
      module: Module,
      types: Map[Int, RuntimeType],
      constants: Map[Int, RuntimeValue],
      globals: Map[Int, GlobalInfo],
      builtins: Map[Int, Int],
      arrayStrides: Map[Int, Int],
      memberOffsets: Map[(Int, Int), Int],
      packedStructures: Set[Int],
      saturatedConversions: Set[Int],
      extendedInstructionSets: Map[Int, String],
      entryPoints: Map[String, Int],
      subgroupSizes: Map[Int, Int],
      functions: Map[Int, FunctionInfo],
      functionNames: Map[Int, String],
      collectiveFunctions: Set[Int]
  ) {
    val typeSizeCache: TrieMap[Int, Int]                                  = TrieMap.empty
    val typeAlignmentCache: TrieMap[Int, Int]                             = TrieMap.empty
    val scalarLeafCountCache: TrieMap[Int, Either[ExecutionFailure, Int]] = TrieMap.empty
  }

  private final class DirtyPage {
    val bytes         = Array.ofDim[Byte](4096)
    private val dirty = Array.ofDim[Long](64)

    def update(offset: Int, value: Byte): Unit = {
      bytes(offset) = value
      dirty(offset >>> 6) |= 1L << (offset & 63)
    }

    def contains(offset: Int): Boolean =
      (dirty(offset >>> 6) & (1L << (offset & 63))) != 0L
  }

  private final class MemoryState(
      memory: ExecutionMemory,
      relocations: Col[PointerRelocation],
      threadSafe: Boolean
  ) {
    private val lock = Object()

    private inline def withLock[A](inline operation: => A): A =
      if (threadSafe) lock.synchronized(operation) else operation
    private val writes                   = mutable.HashMap.empty[AllocationId, mutable.LongMap[DirtyPage]]
    private val externalVirtualAddresses = mutable.HashMap.empty[AllocationId, Long]
    private val internalVirtualAddresses = java.util.IdentityHashMap[Array[Byte], java.lang.Long]()
    private val virtualPointers          = mutable.LongMap.empty[Pointer]
    private val pointerRelocations       = mutable.HashMap.from(relocations.iterator.collect {
      case relocation if relocation.sourceAllocation.isDefined =>
        (relocation.sourceAllocation.get -> relocation.sourceOffset) -> (relocation.target -> relocation.targetOffset)
    })
    private val argumentRelocations = {
      val result = mutable.HashMap.empty[Int, mutable.LongMap[(AllocationId, Long)]]
      relocations.foreach { relocation =>
        relocation.sourceArgument.foreach { argument =>
          result
            .getOrElseUpdate(argument, mutable.LongMap.empty)
            .update(relocation.sourceOffset, relocation.target -> relocation.targetOffset)
        }
      }
      result
    }
    private var nextVirtualAddress = 0x0000100000000000L

    def pointerInteger(pointer: Pointer): Either[ExecutionFailure, Long] = withLock {
      val base = pointer.region match {
        case Region.External(id)    => externalVirtualAddresses.getOrElseUpdate(id, allocateVirtualAddress(pointer))
        case Region.Internal(bytes) =>
          val existing = internalVirtualAddresses.get(bytes)
          if (existing == null) {
            val allocated = allocateVirtualAddress(pointer)
            internalVirtualAddresses.put(bytes, allocated)
            allocated
          } else existing.longValue()
        case Region.Null => return Right(0L)
      }
      Either.cond(
        pointer.offset >= 0L && pointer.offset < VirtualAddressSpan,
        base + pointer.offset,
        ExecutionFailure.InvalidRequest(
          s"SPIR-V pointer offset ${pointer.offset} exceeds the virtual region span"
        )
      )
    }

    def relocateArgument(index: Int, bytes: Col[Byte]): Either[ExecutionFailure, Array[Byte]] = withLock {
      val relocated = bytes.toArray
      argumentRelocations.get(index).iterator.flatMap(_.iterator).foldLeft[Either[
        ExecutionFailure,
        Unit
      ]](Right(())) {
        case (result, (sourceOffset, (target, targetOffset))) =>
          result.flatMap { _ =>
            Either.cond[ExecutionFailure, Unit](
              sourceOffset <= relocated.length.toLong - 8L,
              (),
              ExecutionFailure.InvalidRequest(
                s"pointer relocation source offset $sourceOffset exceeds argument $index"
              )
            ).flatMap { _ =>
              pointerInteger(Pointer(Region.External(target), targetOffset, 0, 8)).map {
                address =>
                  var byte = 0
                  while (byte < 8) {
                    relocated(sourceOffset.toInt + byte) = (address >>> (byte * 8)).toByte
                    byte += 1
                  }
              }
            }
          }
      }.map(_ => relocated)
    }

    def argumentRelocation(index: Int, offset: Long): Option[(AllocationId, Long)] = withLock {
      argumentRelocations.get(index).flatMap(_.get(offset))
    }

    def allocationRelocation(source: AllocationId, offset: Long): Option[(AllocationId, Long)] = withLock {
      pointerRelocations.get(source -> offset)
    }

    def recordPointerRelocation(pointer: Pointer, destination: Pointer): Unit = withLock {
      (pointer.region, destination.region) match {
        case (_, Region.External(destinationAllocation)) =>
          val _ = pointerRelocations.remove(destinationAllocation -> destination.offset)
          pointer.region match {
            case Region.External(pointerAllocation) =>
              pointerRelocations.update(
                destinationAllocation -> destination.offset,
                pointerAllocation     -> pointer.offset
              )
              memory.recordPointerRelocation(
                PointerRelocation(
                  Some(destinationAllocation),
                  None,
                  destination.offset,
                  pointerAllocation,
                  pointer.offset
                )
              )
            case _ => ()
          }
        case _ => ()
      }
    }

    def relocatedPointer(source: Pointer, pointee: Int, storageClass: Int): Option[Pointer] = withLock {
      source.region match {
        case Region.External(allocation) =>
          allocationRelocation(allocation, source.offset).map { case (target, targetOffset) =>
            Pointer(Region.External(target), targetOffset, pointee, storageClass)
          }
        case _ => None
      }
    }

    def elementOffset(program: DispatchProgram, source: Pointer, index: Long): Long =
      Math.addExact(source.offset, Math.multiplyExact(index, typeSize(program, source.pointee).toLong))

    def integerPointer(address: Long, pointee: Int, storageClass: Int): Either[ExecutionFailure, Pointer] =
      withLock {
        if (address == 0L) Right(Pointer(Region.Null, 0L, pointee, storageClass))
        else {
          val base   = address & ~(VirtualAddressSpan - 1L)
          val offset = address - base
          virtualPointers.get(base) match {
            case Some(origin) if offset >= 0L && offset < VirtualAddressSpan =>
              Either.cond(
                storageClass == 8 || storageClass == origin.storageClass,
                origin.copy(offset = offset, pointee = pointee),
                ExecutionFailure.InvalidRequest(
                  s"SPIR-V virtual address 0x${java.lang.Long.toHexString(address)} belongs to storage class " +
                    s"${origin.storageClass}, not $storageClass"
                )
              )
            case _ =>
              Left(
                ExecutionFailure.InvalidRequest(
                  s"SPIR-V integer 0x${java.lang.Long.toHexString(address)} is not a dispatch-local virtual address"
                )
              )
          }
        }
      }

    private def allocateVirtualAddress(pointer: Pointer): Long = {
      val address = nextVirtualAddress
      nextVirtualAddress = Math.addExact(nextVirtualAddress, VirtualAddressSpan)
      virtualPointers.update(address, pointer.copy(offset = 0L))
      address
    }

    def read(pointer: Pointer, size: Int): Either[ExecutionFailure, Array[Byte]] = withLock {
      pointer.region match {
        case Region.Internal(bytes) =>
          checkedRange(bytes.length, pointer.offset, size).map(start =>
            java.util.Arrays.copyOfRange(bytes, start, start + size)
          )
        case Region.External(id) =>
          memory.read(id, pointer.offset, size) match {
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
        case Region.Null =>
          Left(ExecutionFailure.InvalidRequest("cannot read through a null SPIR-V pointer"))
      }
    }

    def readScalar(pointer: Pointer, size: Int): Either[ExecutionFailure, Long] = withLock {
      val relocation =
        if (size != 8) None
        else
          pointer.region match {
            case Region.External(source) => pointerRelocations.get(source -> pointer.offset)
            case Region.Internal(_)      => None
            case Region.Null             => None
          }
      relocation match {
        case Some((target, targetOffset)) =>
          pointerInteger(Pointer(Region.External(target), targetOffset, 0, 8))
        case None => readExternalScalar(pointer, size)
      }
    }

    private def readExternalScalar(pointer: Pointer, size: Int): Either[ExecutionFailure, Long] = pointer.region match {
      case Region.Internal(bytes) =>
        checkedRange(bytes.length, pointer.offset, size).map(start => LittleEndian.decode(bytes, start, size))
      case Region.External(id) =>
        memory.read(id, pointer.offset, size) match {
          case Left(error)   => Left(error)
          case Right(source) =>
            val pending = writes.get(id)
            val value   = LittleEndian.decode(source.length) { index =>
              val absolute   = pointer.offset + index.toLong
              val pageOffset = (absolute & 4095L).toInt
              pending match {
                case Some(pages) => pages.get(absolute >>> 12) match {
                    case Some(page) if page.contains(pageOffset) => page.bytes(pageOffset)
                    case _                                       => source(index)
                  }
                case None => source(index)
              }
            }
            Right(value)
        }
      case Region.Null =>
        Left(ExecutionFailure.InvalidRequest("cannot read scalar through a null SPIR-V pointer"))
    }

    def write(pointer: Pointer, bytes: Array[Byte]): Either[ExecutionFailure, Unit] = withLock {
      pointer.region match {
        case Region.Internal(target) =>
          checkedRange(target.length, pointer.offset, bytes.length).map { start =>
            System.arraycopy(bytes, 0, target, start, bytes.length)
          }
        case Region.External(id) =>
          if (pointer.offset < 0L || pointer.offset > Long.MaxValue - bytes.length.toLong)
            Left(ExecutionFailure.InvalidRequest(s"invalid external write offset ${pointer.offset}"))
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
            ExecutionFailure.InvalidRequest(
              s"cannot write through a null SPIR-V pointer (offset=${pointer.offset}, pointee=%${pointer.pointee}, " +
                s"storage=${pointer.storageClass})"
            )
          )
      }
    }

    def writeScalar(pointer: Pointer, value: Long, size: Int): Either[ExecutionFailure, Unit] = withLock {
      pointer.region match {
        case Region.Internal(target) =>
          checkedRange(target.length, pointer.offset, size).map(start => LittleEndian.write(value, target, start, size))
        case Region.External(id) =>
          if (pointer.offset < 0L || size < 0 || pointer.offset > Long.MaxValue - size.toLong)
            Left(ExecutionFailure.InvalidRequest(s"invalid external write offset ${pointer.offset}"))
          else {
            val pending = writes.getOrElseUpdate(id, mutable.LongMap.empty)
            LittleEndian.write(value, size) { (index, byte) =>
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
            ExecutionFailure.InvalidRequest(
              s"cannot write through a null SPIR-V pointer (offset=${pointer.offset}, pointee=%${pointer.pointee}, " +
                s"storage=${pointer.storageClass})"
            )
          )
      }
    }

    def copySlots(destination: Pointer, origin: Pointer, size: Long): Either[ExecutionFailure, Unit] = withLock {
      if (size < 0L || origin.offset > Long.MaxValue - size || destination.offset > Long.MaxValue - size)
        Left(ExecutionFailure.InvalidRequest(s"invalid aggregate copy size $size"))
      else {
        destination.slots match {
          case Some(target) =>
            val end = destination.offset + size
            Col.from(target).foreach { case (destinationOffset, (pointerType, template)) =>
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
    }

    /** Keep read-modify-write atomics indivisible when workgroups share a pool. */
    def atomic[A](operation: => A): A = if (threadSafe) lock.synchronized(operation) else operation

    def flush(): Either[ExecutionFailure, Unit] = withLock {
      val allocations = writes.iterator.toArray.sortBy(_._1.toLong)
      var allocation  = 0
      while (allocation < allocations.length) {
        val (id, pending) = allocations(allocation)
        val pages         = pending.iterator.toArray.sortBy(_._1)
        var pageIndex     = 0
        while (pageIndex < pages.length) {
          val (number, page) = pages(pageIndex)
          var offset         = 0
          while (offset < page.bytes.length) {
            while (offset < page.bytes.length && !page.contains(offset)) offset += 1
            if (offset < page.bytes.length) {
              val start = offset
              while (offset < page.bytes.length && page.contains(offset)) offset += 1
              memory.write(
                id,
                (number << 12) + start.toLong,
                Col.from(java.util.Arrays.copyOfRange(page.bytes, start, offset))
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
      Right(())
    }

    private def checkedRange(length: Int, offset: Long, size: Int): Either[ExecutionFailure, Int] =
      ByteRange
        .start(length, offset, size)
        .toRight(ExecutionFailure.InvalidRequest(s"internal byte range $offset + $size exceeds $length"))
  }

  def prepare(module: Module): Either[ExecutionFailure, PreparedProgram] =
    try compile(module).map(PreparedProgram(_))
    catch {
      case NonFatal(error) =>
        Left(
          ExecutionFailure.InvalidRequest(
            s"malformed SPIR-V execution state: ${Option(error.getMessage).getOrElse(error.getClass.getSimpleName)}"
          )
        )
    }

  /** Validate modules received from an external toolchain before compiling their execution state. Handwritten unit
    * fixtures may use the validation-free overload to exercise individual interpreter rules in isolation.
    */
  def prepare(module: Module, environment: Environment): Either[ExecutionFailure, PreparedProgram] =
    Validator.validate(module, environment) match {
      case ValidationResult.Valid           => prepare(module)
      case ValidationResult.Invalid(issues) =>
        Left(ExecutionFailure.InvalidRequest(issues.map(_.message).mkString("invalid SPIR-V module: ", "; ", "")))
      case ValidationResult.Unsupported(issues) =>
        Left(ExecutionFailure.Unsupported(issues.map(_.message).mkString("unsupported SPIR-V module: ", "; ", "")))
    }

  def run(program: PreparedProgram, dispatch: KernelDispatch, memory: ExecutionMemory): Either[ExecutionFailure, Unit] =
    run(program, dispatch, memory, ExecutionControl.default)

  def run(
      program: PreparedProgram,
      dispatch: KernelDispatch,
      memory: ExecutionMemory,
      metrics: PerformanceMetrics
  ): Either[ExecutionFailure, Unit] =
    run(program, dispatch, memory, ExecutionControl.default, metrics)

  def run(
      program: PreparedProgram,
      dispatch: KernelDispatch,
      memory: ExecutionMemory,
      control: ExecutionControl
  ): Either[ExecutionFailure, Unit] =
    runMeasured(program, dispatch, memory, control, null)

  def run(
      program: PreparedProgram,
      dispatch: KernelDispatch,
      memory: ExecutionMemory,
      control: ExecutionControl,
      metrics: PerformanceMetrics
  ): Either[ExecutionFailure, Unit] =
    runMeasured(program, dispatch, memory, control, metrics)

  private def runMeasured(
      program: PreparedProgram,
      dispatch: KernelDispatch,
      memory: ExecutionMemory,
      control: ExecutionControl,
      metrics: PerformanceMetrics | Null
  ): Either[ExecutionFailure, Unit] = {
    val started = if (metrics == null) 0L else System.nanoTime()
    try runValidated(program.value, dispatch, memory, control, metrics)
    catch {
      case NonFatal(error) =>
        Left(
          ExecutionFailure.InvalidRequest(
            s"malformed SPIR-V execution state: ${Option(error.getMessage).getOrElse(error.getClass.getSimpleName)}"
          )
        )
    } finally if (metrics != null) metrics.recordElapsed(System.nanoTime() - started)
  }

  def run(module: Module, dispatch: KernelDispatch, memory: ExecutionMemory): Either[ExecutionFailure, Unit] =
    prepare(module).flatMap(run(_, dispatch, memory))

  private def runValidated(
      program: DispatchProgram,
      dispatch: KernelDispatch,
      memory: ExecutionMemory,
      control: ExecutionControl,
      metrics: PerformanceMetrics | Null
  ): Either[ExecutionFailure, Unit] =
    program.entryPoints.get(dispatch.kernelName).toRight(
      ExecutionFailure.NotFound(s"SPIR-V entry point ${dispatch.kernelName} does not exist")
    ).flatMap { functionId =>
      program.functions.get(functionId).toRight(
        ExecutionFailure.InvalidRequest(s"SPIR-V entry point ${dispatch.kernelName} has no function body")
      ).flatMap { function =>
        Either.cond(
          function.parameters.size == dispatch.arguments.size,
          (),
          ExecutionFailure.InvalidRequest(
            s"SPIR-V entry point ${dispatch.kernelName} expects ${function.parameters.size} arguments, " +
              s"received ${dispatch.arguments.size}"
          )
        ).flatMap(_ => executeDispatch(program, function, dispatch, memory, control, metrics))
      }
    }

  private def compile(module: Module): Either[ExecutionFailure, DispatchProgram] = {
    val types                   = mutable.HashMap.empty[Int, RuntimeType]
    val constants               = mutable.HashMap.empty[Int, RuntimeValue]
    val globals                 = mutable.HashMap.empty[Int, GlobalInfo]
    val builtins                = mutable.HashMap.empty[Int, Int]
    val arrayStrides            = mutable.HashMap.empty[Int, Int]
    val memberOffsets           = mutable.HashMap.empty[(Int, Int), Int]
    val packedStructures        = mutable.HashSet.empty[Int]
    val saturatedConversions    = mutable.HashSet.empty[Int]
    val extendedInstructionSets = mutable.HashMap.empty[Int, String]
    val entryPoints             = mutable.HashMap.empty[String, Int]
    val subgroupSizes           = mutable.HashMap.empty[Int, Int]
    val functions               = mutable.HashMap.empty[Int, FunctionInfo]
    val functionNames           = mutable.HashMap.empty[Int, String]

    module.instructions.foreach {
      case Instruction.Name(target, name) => functionNames.update(id(target), literalString(name))
      case Instruction.Decorate(target, decoration)
          if decoration.words.size >= 2 && unsigned(decoration) == 41L =>
        // Optimized/linker-produced modules may omit OpName for imported
        // builtins but retain their LinkageAttributes decoration.
        functionNames.update(id(target), literalString(decoration.copy(words = decoration.words.drop(1))))
      case _ => ()
    }

    var index = 0
    while (index < module.instructions.size) {
      module.instructions(index) match {
        case Instruction.TypeVoid(dest)                   => types.update(id(dest), RuntimeType.Void)
        case Instruction.TypeBool(dest)                   => types.update(id(dest), RuntimeType.Bool)
        case Instruction.TypeInt(dest, width, signedness) =>
          val bits = unsigned(width).toInt
          if (bits < 8 || bits > 64 || bits % 8 != 0)
            return Left(ExecutionFailure.Unsupported(s"SPIR-V integer width $bits is unsupported"))
          types.update(id(dest), RuntimeType.Integer(bits, unsigned(signedness) != 0L))
        case Instruction.TypeFloat(dest, width, _) =>
          val bits = unsigned(width).toInt
          if (!Col(16, 32, 64).contains(bits))
            return Left(ExecutionFailure.Unsupported(s"SPIR-V floating-point width $bits is unsupported"))
          types.update(id(dest), RuntimeType.Floating(bits))
        case Instruction.TypeVector(dest, component, count) =>
          types.update(id(dest), RuntimeType.VectorType(id(component), unsigned(count).toInt))
        case Instruction.TypeArray(dest, element, length) =>
          val count = constants.get(id(length)).flatMap(scalarBits) match {
            case Some(value) => value
            case None        =>
              return Left(
                ExecutionFailure.InvalidRequest(s"SPIR-V array length %${id(length)} is not a scalar constant")
              )
          }
          types.update(id(dest), RuntimeType.Array(id(element), count))
        case Instruction.TypeStruct(dest, members) =>
          types.update(id(dest), RuntimeType.Structure(members.map(id)))
        case Instruction.TypePointer(dest, storageClass, dataType) =>
          types.update(id(dest), RuntimeType.Pointer(unsigned(storageClass).toInt, id(dataType)))
        case Instruction.TypeFunction(dest, result, parameters) =>
          types.update(id(dest), RuntimeType.Function(id(result), parameters.map(id)))
        case Instruction.TypeEvent(dest)                => types.update(id(dest), RuntimeType.Event)
        case Instruction.ConstantTrue(resultType, dest) =>
          constants.update(id(dest), RuntimeValue.Bits(id(resultType), Array(1L)))
        case Instruction.ConstantFalse(resultType, dest) =>
          constants.update(id(dest), RuntimeValue.Bits(id(resultType), Array(0L)))
        case Instruction.Constant(resultType, dest, value) =>
          constants.update(id(dest), RuntimeValue.Bits(id(resultType), Array(literalBits(value))))
        case Instruction.SpecConstant(resultType, dest, value) =>
          constants.update(id(dest), RuntimeValue.Bits(id(resultType), Array(literalBits(value))))
        case Instruction.ConstantComposite(resultType, dest, constituents) =>
          constantComposite(types, constants, resultType, dest, constituents) match {
            case Left(error)  => return Left(error)
            case Right(value) => constants.update(id(dest), value)
          }
        case Instruction.SpecConstantComposite(resultType, dest, constituents) =>
          constantComposite(types, constants, resultType, dest, constituents) match {
            case Left(error)  => return Left(error)
            case Right(value) => constants.update(id(dest), value)
          }
        case Instruction.ConstantNull(resultType, dest) =>
          zeroValue(types, resultType, dest, "OpConstantNull") match {
            case Left(error)  => return Left(error)
            case Right(value) => constants.update(id(dest), value)
          }
        case Instruction.Undef(resultType, dest) =>
          zeroValue(types, resultType, dest, "OpUndef") match {
            case Left(error)  => return Left(error)
            case Right(value) => constants.update(id(dest), value)
          }
        case Instruction.Variable(resultType, dest, storageClass, initializer) =>
          globals.update(
            id(dest),
            GlobalInfo(id(resultType), unsigned(storageClass).toInt, initializer.map(id))
          )
        case Instruction.Decorate(target, decoration) if decoration.words.size >= 2 && unsigned(decoration) == 11L =>
          builtins.update(id(target), decoration.words(1).toUnsigned.toInt)
        case Instruction.Decorate(target, decoration) if decoration.words.size >= 2 && unsigned(decoration) == 6L =>
          arrayStrides.update(id(target), Math.toIntExact(decoration.words(1).toUnsigned))
        case Instruction.Decorate(target, decoration) if unsigned(decoration) == 10L =>
          val _ = packedStructures.add(id(target))
        case Instruction.Decorate(target, decoration) if unsigned(decoration) == 28L =>
          val _ = saturatedConversions.add(id(target))
        case Instruction.MemberDecorate(structureType, member, decoration)
            if decoration.words.size >= 2 && unsigned(decoration) == 35L =>
          memberOffsets.update(
            id(structureType) -> Math.toIntExact(unsigned(member)),
            Math.toIntExact(decoration.words(1).toUnsigned)
          )
        case Instruction.ExtInstImport(dest, name) =>
          extendedInstructionSets.update(id(dest), literalString(name))
        case Instruction.EntryPoint(_, entryPoint, name, _) =>
          entryPoints.update(literalString(name), id(entryPoint))
        case Instruction.ExecutionMode(entryPoint, mode)
            if unsigned(mode) == 35L && mode.words.size >= 2 =>
          val size = mode.words(1).toUnsigned
          if (size == 0L || size > Int.MaxValue.toLong)
            return Left(ExecutionFailure.InvalidRequest(s"SPIR-V SubgroupSize $size is invalid"))
          subgroupSizes.update(id(entryPoint), size.toInt)
        case Instruction.Function(_, dest, _, _) =>
          val functionId = id(dest)
          val parameters = mutable.ArrayBuilder.make[(Int, Int)]
          val labels     = mutable.HashMap.empty[Int, Int]
          val start      = index
          index += 1
          while (
            index < module.instructions.size && !module.instructions(index).isInstanceOf[Instruction.FunctionEnd]
          ) {
            module.instructions(index) match {
              case Instruction.FunctionParameter(resultType, parameter) =>
                parameters += ((id(parameter), id(resultType)))
              case Instruction.Label(label) => labels.update(id(label), index)
              case _                        => ()
            }
            index += 1
          }
          if (index >= module.instructions.size)
            return Left(ExecutionFailure.InvalidRequest(s"SPIR-V function %$functionId has no OpFunctionEnd"))
          functions.update(
            functionId,
            FunctionInfo(functionId, Col.from(parameters.result()), start, index, labels.toMap)
          )
        case _ => ()
      }
      index += 1
    }
    Right(
      DispatchProgram(
        module,
        types.toMap,
        constants.toMap,
        globals.toMap,
        builtins.toMap,
        arrayStrides.toMap,
        memberOffsets.toMap,
        packedStructures.toSet,
        saturatedConversions.toSet,
        extendedInstructionSets.toMap,
        entryPoints.toMap,
        subgroupSizes.toMap,
        functions.toMap,
        functionNames.toMap,
        reachableCollectiveFunctions(module, functions.toMap, functionNames.toMap)
      )
    )
  }

  private def reachableCollectiveFunctions(
      module: Module,
      functions: Map[Int, FunctionInfo],
      functionNames: Map[Int, String]
  ): Set[Int] = {
    val result = mutable.HashSet.empty[Int]
    functions.valuesIterator.foreach { function =>
      var pc     = function.start + 1
      var direct = false
      while (pc < function.end && !direct) {
        direct = isDirectCollective(module.instructions(pc), functionNames)
        pc += 1
      }
      if (direct) result += function.id
    }

    var changed = true
    while (changed) {
      changed = false
      functions.valuesIterator.foreach { function =>
        if (!result.contains(function.id)) {
          var pc      = function.start + 1
          var reaches = false
          while (pc < function.end && !reaches) {
            module.instructions(pc) match {
              case Instruction.FunctionCall(_, _, called, _) => reaches = result.contains(id(called))
              case _                                         => ()
            }
            pc += 1
          }
          if (reaches) {
            result += function.id
            changed = true
          }
        }
      }
    }
    result.toSet
  }

  private def isDirectCollective(instruction: Instruction, functionNames: Map[Int, String]): Boolean =
    instruction match {
      case _: Instruction.GroupAny | _: Instruction.GroupAll | _: Instruction.GroupUMin | _: Instruction.GroupUMax |
          _: Instruction.GroupSMin | _: Instruction.GroupSMax | _: Instruction.GroupIAdd |
          _: Instruction.GroupIMulKHR | _: Instruction.GroupFAdd | _: Instruction.GroupFMin |
          _: Instruction.GroupFMax | _: Instruction.GroupFMulKHR |
          _: Instruction.SubgroupShuffleINTEL | _: Instruction.SubgroupShuffleUpINTEL | _: Instruction.SubgroupShuffleDownINTEL | _: Instruction.GroupBroadcast |
          _: Instruction.ControlBarrier => true
      case Instruction.FunctionCall(_, _, called, _) =>
        functionNames.get(id(called)).exists(isOpenCLCollective)
      case _ => false
    }

  private def openCLBroadcastScope(name: String): Option[Int] =
    if (name.startsWith("_Z19sub_group_broadcast")) Some(3)
    else if (name.startsWith("_Z20work_group_broadcast")) Some(2)
    else None

  private def isOpenCLCollective(name: String): Boolean = name match {
    case "_Z7barrierj" | "_Z29work_group_scan_exclusive_addi" | "_Z29work_group_scan_exclusive_addl" |
        "_Z28sub_group_scan_exclusive_addi" => true
    case value => openCLBroadcastScope(value).isDefined || value.startsWith("_Z26intel_sub_group_shuffle_up")
  }

  private def constantComposite(
      types: collection.Map[Int, RuntimeType],
      constants: collection.Map[Int, RuntimeValue],
      resultType: Operand,
      destination: Operand,
      constituents: Col[Operand]
  ): Either[ExecutionFailure, RuntimeValue] = {
    val lanes = Array.newBuilder[Long]
    var index = 0
    while (index < constituents.size) {
      val constituent = constituents(index)
      constants.get(id(constituent)) match {
        case Some(RuntimeValue.Bits(_, values)) => lanes ++= values
        case Some(_)                            =>
          return Left(
            ExecutionFailure.InvalidRequest(
              s"SPIR-V composite constant %${id(destination)} contains pointer %${id(constituent)}"
            )
          )
        case None => return Left(undefined(constituent))
      }
      index += 1
    }
    val result = lanes.result()
    scalarLeafCount(types, id(resultType)).flatMap(expected =>
      Either.cond(
        result.length == expected,
        RuntimeValue.Bits(id(resultType), result),
        ExecutionFailure.InvalidRequest(
          s"SPIR-V composite constant %${id(destination)} has ${result.length} scalar leaves, expected $expected"
        )
      )
    )
  }

  private def zeroValue(
      types: collection.Map[Int, RuntimeType],
      resultType: Operand,
      destination: Operand,
      opcode: String
  ): Either[ExecutionFailure, RuntimeValue] = {
    val typeId = id(resultType)
    types.get(typeId) match {
      case Some(RuntimeType.Pointer(storageClass, pointee)) =>
        Right(RuntimeValue.Address(typeId, Pointer(Region.Null, 0L, pointee, storageClass)))
      case _ =>
        scalarLeafCount(types, typeId) match {
          case Right(count)                                   => Right(RuntimeValue.Bits(typeId, Array.fill(count)(0L)))
          case Left(ExecutionFailure.InvalidRequest(message)) =>
            Left(
              ExecutionFailure.InvalidRequest(s"$opcode %${id(destination)} has invalid result type: $message")
            )
          case Left(error) => Left(error)
        }
    }
  }

  private def scalarLeafCount(
      types: collection.Map[Int, RuntimeType],
      typeId: Int
  ): Either[ExecutionFailure, Int] = types.get(typeId) match {
    case Some(RuntimeType.Bool) | Some(RuntimeType.Integer(_, _)) | Some(RuntimeType.Floating(_)) |
        Some(RuntimeType.Event) => Right(1)
    case Some(RuntimeType.VectorType(component, count)) =>
      scalarLeafCount(types, component).flatMap(value => checkedLeafCount(typeId, value.toLong * count.toLong))
    case Some(RuntimeType.Array(element, count)) =>
      scalarLeafCount(types, element).flatMap(value => checkedLeafCount(typeId, value.toLong * count))
    case Some(RuntimeType.Structure(members)) =>
      members.foldLeft[Either[ExecutionFailure, Int]](Right(0)) { (total, member) =>
        total.flatMap(left =>
          scalarLeafCount(types, member).flatMap(right => checkedLeafCount(typeId, left.toLong + right))
        )
      }
    case value =>
      Left(ExecutionFailure.InvalidRequest(s"SPIR-V type %$typeId has no scalar data leaves: $value"))
  }

  private def scalarLeafCount(program: DispatchProgram, typeId: Int): Either[ExecutionFailure, Int] =
    program.scalarLeafCountCache.getOrElseUpdate(typeId, scalarLeafCount(program.types, typeId))

  private def checkedLeafCount(typeId: Int, count: Long): Either[ExecutionFailure, Int] =
    Either.cond(
      count >= 0L && count <= Int.MaxValue.toLong,
      count.toInt,
      ExecutionFailure.InvalidRequest(s"SPIR-V type %$typeId has an invalid scalar leaf count: $count")
    )

  private def executeDispatch(
      program: DispatchProgram,
      function: FunctionInfo,
      dispatch: KernelDispatch,
      memory: ExecutionMemory,
      control: ExecutionControl,
      metrics: PerformanceMetrics | Null
  ): Either[ExecutionFailure, Unit] = {
    val globalValues = materializeGlobals(program, storageClass => storageClass != 4 && storageClass != 6) match {
      case Left(error)  => return Left(error)
      case Right(value) => value
    }
    val sx             = dispatch.geometry.groupSize.x
    val sy             = dispatch.geometry.groupSize.y
    val sz             = dispatch.geometry.groupSize.z
    val gx             = dispatch.geometry.groupCount.x
    val gy             = dispatch.geometry.groupCount.y
    val gz             = dispatch.geometry.groupCount.z
    val totalLocal     = Math.multiplyExact(Math.multiplyExact(sx, sy), sz)
    val subgroup       = program.subgroupSizes.getOrElse(function.id, math.max(1, math.min(32, totalLocal)))
    val hasCollectives = program.collectiveFunctions.contains(function.id)
    val hasAtomics     = program.module.instructions.exists {
      case _: Instruction.AtomicStore | _: Instruction.AtomicLoad | _: Instruction.AtomicExchange |
          _: Instruction.AtomicCompareExchange | _: Instruction.AtomicCompareExchangeWeak |
          _: Instruction.AtomicFAddEXT | _: Instruction.AtomicFMinEXT | _: Instruction.AtomicFMaxEXT |
          _: Instruction.AtomicIAdd | _: Instruction.AtomicUMin | _: Instruction.AtomicIIncrement |
          _: Instruction.AtomicIDecrement | _: Instruction.AtomicISub | _: Instruction.AtomicSMin |
          _: Instruction.AtomicSMax | _: Instruction.AtomicUMax | _: Instruction.AtomicAnd |
          _: Instruction.AtomicOr | _: Instruction.AtomicXor => true
      case _ => false
    }
    val baseScalars = BaseScalarValues(program.constants, globalValues, program.module.header.bound.toUnsigned)
    if (metrics != null) metrics.recordDispatch(hasCollectives)
    val groupCount = Math.multiplyExact(Math.multiplyExact(gx.toLong, gy.toLong), gz.toLong)
    // A large number of one-invocation groups is common in oneDPL. For those
    // launches, queueing work-group tasks costs more than the interpreter work
    // itself. Parallelize either genuinely large workgroups or a sufficiently
    // large number of groups; the pool still enforces the global worker cap.
    val parallelWork   = !hasAtomics && control.parallelism.shouldParallel(groupCount, totalLocal.toLong)
    val state          = MemoryState(memory, dispatch.pointerRelocations, threadSafe = false)
    val flushLock      = Object()
    val reusableValues =
      if (hasCollectives || parallelWork) None
      else
        Some(
          InvocationValues(
            program.constants,
            globalValues,
            program.module.header.bound.toUnsigned,
            baseScalars,
            metrics
          )
        )
    val reusableExecution = Array[InvocationExecution | Null](null)

    def runGroup(
        groupIndex: Long,
        reuseInvocationState: Boolean,
        groupMetrics: PerformanceMetrics | Null,
        reuseValues: Option[InvocationValues],
        reuseExecution: Option[Array[InvocationExecution | Null]],
        groupState: MemoryState
    ): Either[ExecutionFailure, Unit] = {
      val group           = dispatch.geometry.groupCoordinates(groupIndex)
      val groupX          = group.x
      val groupY          = group.y
      val groupZ          = group.z
      val workgroupValues = materializeGlobals(program, _ == 4) match {
        case Left(error)  => return Left(error)
        case Right(value) => value
      }
      val shared = dispatch.arguments.zipWithIndex.collect { case (KernelArgument.Local(size), argumentIndex) =>
        argumentIndex -> Region.Internal(Array.ofDim[Byte](checkedSize(size)))
      }.toMap
      val reusableValues = if (hasCollectives) None
      else reuseValues.orElse(
        Some(
          InvocationValues(
            program.constants,
            globalValues,
            program.module.header.bound.toUnsigned,
            baseScalars,
            groupMetrics
          )
        )
      )
      var reusableExecution: InvocationExecution | Null = reuseExecution match {
        case Some(slot) => slot(0)
        case None       => null
      }
      val executions = mutable.ArrayBuffer.empty[InvocationExecution]
      var localZ     = 0
      while (localZ < sz) {
        var localY = 0
        while (localY < sy) {
          var localX = 0
          while (localX < sx) {
            val linearLocal     = (localZ * sy + localY) * sx + localX
            val relativeGlobalX = groupX.toLong * sx + localX
            val relativeGlobalY = groupY.toLong * sy + localY
            val relativeGlobalZ = groupZ.toLong * sz + localZ
            if (
              relativeGlobalX < dispatch.geometry.globalSize.x && relativeGlobalY < dispatch.geometry.globalSize.y &&
              relativeGlobalZ < dispatch.geometry.globalSize.z
            ) {
              if (groupMetrics != null) groupMetrics.recordInvocation()
              val globalX       = Math.addExact(dispatch.geometry.globalOffset.x.toLong, relativeGlobalX)
              val globalY       = Math.addExact(dispatch.geometry.globalOffset.y.toLong, relativeGlobalY)
              val globalZ       = Math.addExact(dispatch.geometry.globalOffset.z.toLong, relativeGlobalZ)
              val privateValues = materializeGlobals(program, _ == 6) match {
                case Left(error)  => return Left(error)
                case Right(value) => value
              }
              val invocation = Invocation(
                groupX = groupX.toLong,
                groupY = groupY.toLong,
                groupZ = groupZ.toLong,
                localX = localX.toLong,
                localY = localY.toLong,
                localZ = localZ.toLong,
                globalX = globalX,
                globalY = globalY,
                globalZ = globalZ,
                globalSizeX = dispatch.geometry.globalSize.x.toLong,
                globalSizeY = dispatch.geometry.globalSize.y.toLong,
                globalSizeZ = dispatch.geometry.globalSize.z.toLong,
                globalOffsetX = dispatch.geometry.globalOffset.x.toLong,
                globalOffsetY = dispatch.geometry.globalOffset.y.toLong,
                globalOffsetZ = dispatch.geometry.globalOffset.z.toLong,
                workDim = dispatch.geometry.workDim,
                numWorkgroupsX = dispatch.geometry.groupCount.x.toLong,
                numWorkgroupsY = dispatch.geometry.groupCount.y.toLong,
                numWorkgroupsZ = dispatch.geometry.groupCount.z.toLong,
                workgroupSizeX = sx.toLong,
                workgroupSizeY = sy.toLong,
                workgroupSizeZ = sz.toLong,
                subgroupSize = subgroup,
                subgroupIndex = linearLocal / subgroup,
                subgroupLocal = linearLocal % subgroup
              )
              prepareInvocation(
                program,
                function,
                dispatch.arguments,
                shared,
                invocation,
                globalValues ++ workgroupValues ++ privateValues,
                reusableValues,
                reusableExecution,
                groupState,
                baseScalars,
                groupMetrics
              ) match {
                case Left(error)  => return Left(error)
                case Right(value) =>
                  if (hasCollectives) executions += value
                  else {
                    if (reuseInvocationState) {
                      reusableExecution = value
                      reuseExecution.foreach(_(0) = value)
                    }
                    resumeInvocation(program, value, groupState, control, groupMetrics) match {
                      case Left(error)                  => return Left(error)
                      case Right(()) if value.completed => ()
                      case Right(())                    =>
                        return Left(
                          ExecutionFailure.InvalidRequest(
                            "SPIR-V program suspended at a collective absent from its static instruction set"
                          )
                        )
                    }
                  }
              }
            }
            localX += 1
          }
          localY += 1
        }
        localZ += 1
      }
      if (hasCollectives)
        runWorkgroup(program, executions, groupState, control, groupMetrics)
      else Right(())
    }

    val runResult = if (!parallelWork) {
      var groupIndex = 0L
      var result     = Right(()): Either[ExecutionFailure, Unit]
      while (groupIndex < groupCount && result.isRight) {
        result = runGroup(
          groupIndex,
          reuseInvocationState = true,
          metrics,
          reusableValues,
          Some(reusableExecution),
          state
        )
        groupIndex += 1L
      }
      result
    } else {
      control.parallelism.parallelFor(groupCount) { groupIndex =>
        val groupMetrics = if (metrics == null) null else PerformanceMetrics()
        val groupState   = MemoryState(memory, dispatch.pointerRelocations, threadSafe = false)
        runGroup(groupIndex, reuseInvocationState = false, groupMetrics, None, None, groupState).flatMap { _ =>
          flushLock.synchronized(groupState.flush())
        }.map { _ =>
          if (metrics != null && groupMetrics != null) metrics.merge(groupMetrics)
        }
      }
    }
    runResult match {
      case Left(error) => return Left(error)
      case Right(())   => ()
    }
    if (parallelWork) Right(()) else state.flush()
  }

  private final case class Invocation(
      groupX: Long,
      groupY: Long,
      groupZ: Long,
      localX: Long,
      localY: Long,
      localZ: Long,
      globalX: Long,
      globalY: Long,
      globalZ: Long,
      globalSizeX: Long,
      globalSizeY: Long,
      globalSizeZ: Long,
      globalOffsetX: Long,
      globalOffsetY: Long,
      globalOffsetZ: Long,
      workDim: Int,
      numWorkgroupsX: Long,
      numWorkgroupsY: Long,
      numWorkgroupsZ: Long,
      workgroupSizeX: Long,
      workgroupSizeY: Long,
      workgroupSizeZ: Long,
      subgroupSize: Int,
      subgroupIndex: Int,
      subgroupLocal: Int
  ) {
    val group: Array[Long]         = Array(groupX, groupY, groupZ)
    val local: Array[Long]         = Array(localX, localY, localZ)
    val global: Array[Long]        = Array(globalX, globalY, globalZ)
    val globalSize: Array[Long]    = Array(globalSizeX, globalSizeY, globalSizeZ)
    val globalOffset: Array[Long]  = Array(globalOffsetX, globalOffsetY, globalOffsetZ)
    val workgroupSize: Array[Long] = Array(workgroupSizeX, workgroupSizeY, workgroupSizeZ)
    val numWorkgroups: Array[Long] = Array(numWorkgroupsX, numWorkgroupsY, numWorkgroupsZ)
    def numSubgroups: Long         =
      (workgroupSizeX * workgroupSizeY * workgroupSizeZ + subgroupSize.toLong - 1L) / subgroupSize.toLong
  }

  private enum CollectiveKind {
    case Any
    case All
    case UnsignedMin
    case UnsignedMax
    case SignedMin
    case SignedMax
    case IntegerAdd
    case IntegerMultiply
    case FloatingAdd
    case FloatingMin
    case FloatingMax
    case FloatingMultiply
    case Shuffle
    case ShuffleUp
    case ShuffleDown
    case Broadcast
    case Barrier
  }

  private enum CollectivePayload {
    case Value(resultType: Int, destination: Int, value: RuntimeValue)
    case Shuffle(resultType: Int, destination: Int, data: RuntimeValue, invocationId: RuntimeValue)
    case ShuffleUp(
        resultType: Int,
        destination: Int,
        previous: RuntimeValue,
        current: RuntimeValue,
        delta: Long
    )
    case ShuffleDown(
        resultType: Int,
        destination: Int,
        current: RuntimeValue,
        next: RuntimeValue,
        delta: Long
    )
    case Barrier(memoryScope: Int, semantics: Long)
  }

  private final case class CollectiveRequest(
      kind: CollectiveKind,
      function: Int,
      pc: Int,
      scope: Int,
      payload: CollectivePayload,
      operation: Int = 0,
      selector: Col[Long] = Col.empty
  )

  private final class InvocationExecution(
      var invocation: Invocation,
      val values: InvocationValues,
      var currentFunction: FunctionInfo,
      var pc: Int,
      var currentLabel: Int,
      var predecessor: Int,
      var instructionCount: Long,
      val calls: mutable.ArrayDeque[CallFrame],
      var suspended: Option[CollectiveRequest],
      var completed: Boolean
  ) {
    def reset(nextInvocation: Invocation, function: FunctionInfo): Unit = {
      invocation = nextInvocation
      currentFunction = function
      pc = function.start + 1
      currentLabel = -1
      predecessor = -1
      instructionCount = 0L
      calls.clear()
      suspended = None
      completed = false
    }
  }

  private def prepareInvocation(
      program: DispatchProgram,
      function: FunctionInfo,
      arguments: Col[KernelArgument],
      shared: Map[Int, Region],
      invocation: Invocation,
      globalValues: Map[Int, RuntimeValue],
      reusableValues: Option[InvocationValues],
      reusableExecution: InvocationExecution | Null,
      memory: MemoryState,
      baseScalars: BaseScalarValues,
      metrics: PerformanceMetrics | Null
  ): Either[ExecutionFailure, InvocationExecution] = {
    val values = reusableValues match {
      case Some(value) =>
        value.clear()
        value
      case None =>
        InvocationValues(
          program.constants,
          globalValues,
          program.module.header.bound.toUnsigned,
          baseScalars,
          metrics
        )
    }
    globalValues.foreach { case (id, value) => values.update(id, value) }
    val globalIterator = program.globals.iterator
    while (globalIterator.hasNext) {
      val (variable, global) = globalIterator.next()
      program.builtins.get(variable) match {
        case Some(builtin) =>
          val pointee = pointerPointee(program, global.pointerType)
          val value   = builtinValue(program, pointee, builtin, invocation) match {
            case Left(error)   => return Left(error)
            case Right(result) => result
          }
          val bytes = encode(program, value)
          values.update(
            variable,
            RuntimeValue.Address(
              global.pointerType,
              Pointer(Region.Internal(bytes), 0L, pointee, global.storageClass)
            )
          )
        case None => ()
      }
    }
    var parameterIndex = 0
    while (parameterIndex < function.parameters.size) {
      val (parameterId, parameterType) = function.parameters(parameterIndex)
      bindArgument(
        program,
        parameterType,
        arguments(parameterIndex),
        shared.get(parameterIndex),
        parameterIndex,
        memory
      ) match {
        case Left(error)  => return Left(error)
        case Right(value) => values.update(parameterId, value)
      }
      parameterIndex += 1
    }

    val execution = reusableExecution match {
      case null =>
        InvocationExecution(
          invocation,
          values,
          function,
          function.start + 1,
          -1,
          -1,
          0L,
          mutable.ArrayDeque.empty[CallFrame],
          None,
          false
        )
      case value =>
        value.reset(invocation, function)
        value
    }
    Right(execution)
  }

  private def resumeInvocation(
      program: DispatchProgram,
      execution: InvocationExecution,
      memory: MemoryState,
      control: ExecutionControl,
      metrics: PerformanceMetrics | Null
  ): Either[ExecutionFailure, Unit] = {
    val values           = execution.values
    var currentFunction  = execution.currentFunction
    var pc               = execution.pc
    var currentLabel     = execution.currentLabel
    var predecessor      = execution.predecessor
    var instructionCount = execution.instructionCount
    val calls            = execution.calls
    while (pc < currentFunction.end) {
      instructionCount += 1L
      if (metrics != null) metrics.recordInstruction()
      control.checkpoint() match {
        case Left(fault) => return Left(ExecutionFailure.Failed(fault.message))
        case Right(())   => ()
      }
      val instruction = program.module.instructions(pc)
      instruction match {
        case Instruction.FunctionParameter(_, _) => pc += 1
        case Instruction.Label(label)            =>
          currentLabel = id(label)
          pc += 1
        case Instruction.Phi(_, _, _) =>
          val updates = mutable.ArrayBuffer.empty[(Int, RuntimeValue)]
          var phiPc   = pc
          while (phiPc < currentFunction.end && program.module.instructions(phiPc).isInstanceOf[Instruction.Phi]) {
            program.module.instructions(phiPc) match {
              case Instruction.Phi(resultType, dest, incoming) =>
                phiIncoming(incoming, predecessor) match {
                  case Left(error)          => return Left(error)
                  case Right(Some(valueId)) =>
                    values.get(valueId.toLong) match {
                      case Some(value) => updates += id(dest) -> retag(value, id(resultType))
                      case None        =>
                        return Left(
                          ExecutionFailure.InvalidRequest(
                            s"OpPhi %${id(dest)} has no incoming value for label %$predecessor"
                          )
                        )
                    }
                  case Right(None) =>
                    return Left(
                      ExecutionFailure.InvalidRequest(
                        s"OpPhi %${id(dest)} has no incoming value for label %$predecessor"
                      )
                    )
                }
              case _ => ()
            }
            phiPc += 1
          }
          updates.foreach { case (destination, value) => values.update(destination, value) }
          pc = phiPc
        case Instruction.Variable(resultType, dest, storageClass, initializer) =>
          val pointee = pointerPointee(program, id(resultType))
          val buffer  = Array.ofDim[Byte](typeSize(program, pointee))
          val slots   = mutable.LongMap.empty[(Int, Pointer)]
          pointerLeafOffsets(program, pointee, 0L).toOption.foreach(_.foreach { case (offset, pointerType) =>
            program.types.get(pointerType) match {
              case Some(RuntimeType.Pointer(pointerStorage, pointerPointee)) =>
                slots.update(offset, pointerType -> Pointer(Region.Null, 0L, pointerPointee, pointerStorage))
              case _ => ()
            }
          })
          initializer.flatMap(value => values.get(id(value))).foreach(value =>
            System.arraycopy(
              encode(program, value),
              0,
              buffer,
              0,
              math.min(buffer.length, encode(program, value).length)
            )
          )
          values.update(
            id(dest),
            RuntimeValue.Address(
              id(resultType),
              Pointer(
                Region.Internal(buffer),
                0L,
                pointee,
                unsigned(storageClass).toInt,
                Option.when(slots.nonEmpty)(slots)
              )
            )
          )
          pc += 1
        case Instruction.Load(resultType, dest, pointer, _) =>
          address(values, pointer) match {
            case Left(error)   => return Left(error)
            case Right(source) =>
              program.types.get(id(resultType)) match {
                case Some(RuntimeType.Pointer(_, _)) =>
                  loadPointer(program, memory, source, id(resultType)) match {
                    case Left(error)  => return Left(error)
                    case Right(value) =>
                      values.update(id(dest), value)
                      pc += 1
                  }
                case Some(RuntimeType.Bool) | Some(RuntimeType.Integer(_, _)) | Some(RuntimeType.Floating(_)) =>
                  memory.readScalar(source, typeSize(program, id(resultType))) match {
                    case Left(error) => return Left(error)
                    case Right(bits) =>
                      values.updateScalar(id(dest), id(resultType), bits)
                      pc += 1
                  }
                case _ => memory.read(source, typeSize(program, id(resultType))) match {
                    case Left(error)  => return Left(error)
                    case Right(bytes) =>
                      values.update(id(dest), decode(program, id(resultType), bytes))
                      pc += 1
                  }
              }
          }
        case Instruction.Store(pointer, source, _) =>
          address(values, pointer) match {
            case Right(target) if values.scalarType(id(source)) >= 0 =>
              val sourceType = values.scalarType(id(source))
              memory.writeScalar(target, values.scalarBits(id(source)), typeSize(program, sourceType)) match {
                case Left(error) => return Left(withExecutionContext(error, currentFunction, pc, instruction))
                case Right(())   =>
                  if (typeSize(program, sourceType) == 8)
                    target.slots.flatMap(_.get(target.offset)).foreach { case (pointerType, template) =>
                      memory.integerPointer(
                        values.scalarBits(id(source)),
                        template.pointee,
                        template.storageClass
                      ).toOption.foreach {
                        pointer => target.slots.foreach(_.update(target.offset, pointerType -> pointer))
                      }
                    }
                  pc += 1
              }
            case Right(target) => values.get(id(source)) match {
                case Some(RuntimeValue.Address(sourceType, value)) =>
                  storePointer(program, memory, target, sourceType, value) match {
                    case Left(error) => return Left(error)
                    case Right(())   => pc += 1
                  }
                case Some(value) => memory.write(target, encode(program, value)) match {
                    case Left(error) => return Left(withExecutionContext(error, currentFunction, pc, instruction))
                    case Right(())   => pc += 1
                  }
                case None => return Left(undefined(source))
              }
            case Left(error) => return Left(error)
          }
        case Instruction.CopyMemory(target, source, _, _) =>
          (address(values, target), address(values, source)) match {
            case (Right(destination), Right(origin)) =>
              val size = typeSize(program, origin.pointee)
              memory.read(origin, size) match {
                case Left(error)  => return Left(error)
                case Right(bytes) =>
                  memory.write(destination, bytes).flatMap(_ =>
                    memory.copySlots(destination, origin, size.toLong)
                  ) match {
                    case Left(error) => return Left(error)
                    case Right(())   => pc += 1
                  }
              }
            case (Left(error), _) => return Left(error)
            case (_, Left(error)) => return Left(error)
          }
        case Instruction.CopyMemorySized(target, source, size, _, _) =>
          (address(values, target), address(values, source), scalar(values, size)) match {
            case (Right(destination), Right(origin), Right(rawSize)) if rawSize <= Int.MaxValue.toLong =>
              val byteCount = rawSize.toInt
              memory.read(origin, byteCount) match {
                case Left(error)  => return Left(error)
                case Right(bytes) =>
                  memory.write(destination, bytes).flatMap(_ =>
                    memory.copySlots(destination, origin, byteCount.toLong)
                  ) match {
                    case Left(error) => return Left(error)
                    case Right(())   => pc += 1
                  }
              }
            case (Right(_), Right(_), Right(rawSize)) =>
              return Left(ExecutionFailure.InvalidRequest(s"OpCopyMemorySized byte count $rawSize is too large"))
            case (Left(error), _, _) => return Left(error)
            case (_, Left(error), _) => return Left(error)
            case (_, _, Left(error)) => return Left(error)
          }
        case Instruction.GroupAsyncCopy(
              resultType,
              dest,
              executionScope,
              destination,
              source,
              numElements,
              stride,
              _
            ) =>
          groupAsyncCopy(
            program,
            values,
            memory,
            execution.invocation,
            resultType,
            dest,
            executionScope,
            destination,
            source,
            numElements,
            stride
          ) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.GroupWaitEvents(executionScope, numEvents, eventsList) =>
          (for {
            scope <- scalar(values, executionScope)
            count <- scalar(values, numEvents)
            _     <- address(values, eventsList)
            _     <- Either.cond(
              scope == 2L,
              (),
              ExecutionFailure.Unsupported(s"OpGroupWaitEvents execution scope $scope is not Workgroup")
            )
            _ <- Either.cond(
              count >= 0L,
              (),
              ExecutionFailure.InvalidRequest(s"OpGroupWaitEvents has invalid event count $count")
            )
          } yield ()) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.Bitcast(resultType, dest, source) =>
          values.get(id(source)) match {
            case Some(RuntimeValue.Address(_, pointer)) =>
              val pointee = pointerPointee(program, id(resultType))
              values.update(id(dest), RuntimeValue.Address(id(resultType), pointer.copy(pointee = pointee)))
              pc += 1
            case Some(value) =>
              value match {
                case RuntimeValue.Bits(sourceType, lanes) =>
                  bitcast(program, sourceType, id(resultType), lanes) match {
                    case Left(error)   => return Left(error)
                    case Right(result) =>
                      values.update(id(dest), RuntimeValue.Bits(id(resultType), result))
                      pc += 1
                  }
                case RuntimeValue.Address(_, pointer) =>
                  val pointee = pointerPointee(program, id(resultType))
                  values.update(id(dest), RuntimeValue.Address(id(resultType), pointer.copy(pointee = pointee)))
                  pc += 1
              }
            case None => return Left(undefined(source))
          }
        case Instruction.BitCount(resultType, dest, base) =>
          integerBitCount(program, values, resultType, dest, base) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.PtrCastToGeneric(resultType, dest, source) =>
          (program.types.get(id(resultType)), values.get(id(source))) match {
            case (Some(RuntimeType.Pointer(8, pointee)), Some(RuntimeValue.Address(sourceType, pointer))) =>
              program.types.get(sourceType) match {
                case Some(RuntimeType.Pointer(storageClass, sourcePointee))
                    if Col(4, 5, 7).contains(storageClass) && sourcePointee == pointee &&
                      pointer.pointee == pointee && (pointer.region == Region.Null || pointer.storageClass == storageClass) =>
                  values.update(id(dest), RuntimeValue.Address(id(resultType), pointer))
                  pc += 1
                case Some(RuntimeType.Pointer(storageClass, sourcePointee)) =>
                  return Left(
                    ExecutionFailure.InvalidRequest(
                      s"OpPtrCastToGeneric cannot cast storage class $storageClass pointee %$sourcePointee to %$pointee"
                    )
                  )
                case _ =>
                  return Left(ExecutionFailure.InvalidRequest("OpPtrCastToGeneric source type is not a pointer"))
              }
            case (Some(RuntimeType.Pointer(storageClass, _)), _) =>
              return Left(
                ExecutionFailure.InvalidRequest(
                  s"OpPtrCastToGeneric result pointer has storage class $storageClass instead of Generic"
                )
              )
            case (None, _) =>
              return Left(
                ExecutionFailure.InvalidRequest(s"OpPtrCastToGeneric result type %${id(resultType)} is undefined")
              )
            case (_, None) => return Left(undefined(source))
            case _         => return Left(ExecutionFailure.InvalidRequest("OpPtrCastToGeneric source is not a pointer"))
          }
        case Instruction.GenericCastToPtr(resultType, dest, source) =>
          genericCastToPointer(program, values, resultType, dest, source) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.GenericCastToPtrExplicit(resultType, dest, source, storage) =>
          genericCastToPointerExplicit(program, values, resultType, dest, source, storage) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.PtrAccessChain(resultType, dest, base, element, indexes) =>
          accessChain(program, values, memory, resultType, dest, base, Some(element), indexes) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.InBoundsPtrAccessChain(resultType, dest, base, element, indexes) =>
          accessChain(program, values, memory, resultType, dest, base, Some(element), indexes) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.AccessChain(resultType, dest, base, indexes) =>
          accessChain(program, values, memory, resultType, dest, base, None, indexes) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.InBoundsAccessChain(resultType, dest, base, indexes) =>
          accessChain(program, values, memory, resultType, dest, base, None, indexes) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.CompositeExtract(resultType, dest, composite, indexes) =>
          compositeExtract(program, values, resultType, dest, composite, indexes) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.CompositeConstruct(resultType, dest, constituents) =>
          val lanes = Array.newBuilder[Long]
          var index = 0
          while (index < constituents.size) {
            values.get(id(constituents(index))) match {
              case Some(RuntimeValue.Bits(_, value)) => lanes ++= value
              case Some(_)                           =>
                return Left(
                  ExecutionFailure.InvalidRequest(
                    s"OpCompositeConstruct constituent %${id(constituents(index))} is a pointer"
                  )
                )
              case None => return Left(undefined(constituents(index)))
            }
            index += 1
          }
          val result = lanes.result()
          scalarLeafCount(program, id(resultType)) match {
            case Right(expected) if result.length == expected =>
              values.update(id(dest), RuntimeValue.Bits(id(resultType), result))
              pc += 1
            case Right(expected) =>
              return Left(
                ExecutionFailure.InvalidRequest(
                  s"OpCompositeConstruct %${id(dest)} has ${result.length} scalar leaves, expected $expected"
                )
              )
            case Left(error) => return Left(error)
          }
        case Instruction.CompositeInsert(resultType, dest, source, composite, indexes) =>
          compositeInsert(program, values, resultType, dest, source, composite, indexes) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.VectorExtractDynamic(resultType, dest, vector, index) =>
          (values.get(id(vector)), values.get(id(index))) match {
            case (Some(RuntimeValue.Bits(_, lanes)), Some(RuntimeValue.Bits(_, Array(component)))) =>
              val selected = component.toInt
              if (selected < 0 || selected >= lanes.length)
                return Left(ExecutionFailure.InvalidRequest(s"OpVectorExtractDynamic index $selected is out of range"))
              values.update(id(dest), RuntimeValue.Bits(id(resultType), Array(lanes(selected))))
              pc += 1
            case (None, _) => return Left(undefined(vector))
            case (_, None) => return Left(undefined(index))
            case _ => return Left(ExecutionFailure.Unsupported("OpVectorExtractDynamic requires a scalar index"))
          }
        case Instruction.VectorInsertDynamic(resultType, dest, vector, component, index) =>
          (values.get(id(vector)), values.get(id(component)), scalar(values, index)) match {
            case (Some(RuntimeValue.Bits(_, lanes)), Some(RuntimeValue.Bits(_, Array(value))), Right(selected)) =>
              val position = selected.toInt
              if (position < 0 || position >= lanes.length)
                return Left(ExecutionFailure.InvalidRequest(s"OpVectorInsertDynamic index $position is out of range"))
              val result = lanes.clone()
              result(position) = value
              values.update(id(dest), RuntimeValue.Bits(id(resultType), result))
              pc += 1
            case (None, _, _)        => return Left(undefined(vector))
            case (_, None, _)        => return Left(undefined(component))
            case (_, _, Left(error)) => return Left(error)
            case _ => return Left(ExecutionFailure.InvalidRequest("OpVectorInsertDynamic requires scalar component"))
          }
        case Instruction.VectorShuffle(resultType, dest, vector1, vector2, components) =>
          (values.get(id(vector1)), values.get(id(vector2))) match {
            case (Some(RuntimeValue.Bits(_, left)), Some(RuntimeValue.Bits(_, right))) =>
              scalarLeafCount(program, id(resultType)) match {
                case Right(expected) if expected == components.size =>
                  val combined = left ++ right
                  val result   = Array.ofDim[Long](components.size)
                  var index    = 0
                  while (index < components.size) {
                    val selected = unsigned(components(index))
                    result(index) =
                      if (selected == 0xffffffffL) 0L
                      else if (selected < combined.length.toLong) combined(selected.toInt)
                      else
                        return Left(
                          ExecutionFailure.InvalidRequest(
                            s"OpVectorShuffle component $selected is outside ${combined.length} lanes"
                          )
                        )
                    index += 1
                  }
                  values.update(id(dest), RuntimeValue.Bits(id(resultType), result))
                  pc += 1
                case Right(expected) =>
                  return Left(
                    ExecutionFailure.InvalidRequest(
                      s"OpVectorShuffle result has ${components.size} components, expected $expected scalar leaves"
                    )
                  )
                case Left(error) => return Left(error)
              }
            case (None, _) => return Left(undefined(vector1))
            case (_, None) => return Left(undefined(vector2))
            case _ => return Left(ExecutionFailure.InvalidRequest("OpVectorShuffle operands must be numeric vectors"))
          }
        case Instruction.ExtInst(resultType, dest, set, inst, sources) =>
          extendedInstruction(program, values, memory, resultType, dest, set, inst, sources) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.UConvert(resultType, dest, source) =>
          val sourceId = id(source)
          if (values.scalarType(sourceId) >= 0) {
            val width = scalarWidth(program, id(resultType))
            values.updateScalar(id(dest), id(resultType), values.scalarBits(sourceId) & mask(width))
            pc += 1
          } else
            values.get(sourceId) match {
              case Some(RuntimeValue.Bits(_, lanes)) =>
                val width = scalarWidth(program, id(resultType))
                values.update(id(dest), RuntimeValue.Bits(id(resultType), lanes.map(_ & mask(width))))
                pc += 1
              case Some(_) => return Left(ExecutionFailure.InvalidRequest("OpUConvert cannot convert a pointer"))
              case None    => return Left(undefined(source))
            }
        case Instruction.SConvert(resultType, dest, source) =>
          signedConvert(program, values, resultType, dest, source) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.ConvertPtrToU(resultType, dest, pointer) =>
          val resultWidth = integerShape(program, id(resultType)) match {
            case Some((width, 1)) => width
            case _                =>
              return Left(ExecutionFailure.InvalidRequest("OpConvertPtrToU requires a scalar integer result"))
          }
          address(values, pointer).flatMap(memory.pointerInteger) match {
            case Left(error)  => return Left(error)
            case Right(value) =>
              values.update(id(dest), RuntimeValue.Bits(id(resultType), Array(value & mask(resultWidth))))
              pc += 1
          }
        case Instruction.ConvertUToPtr(resultType, dest, integerValue) =>
          (program.types.get(id(resultType)), values.get(id(integerValue))) match {
            case (
                  Some(RuntimeType.Pointer(storageClass, pointee)),
                  Some(RuntimeValue.Bits(integerType, Array(rawAddress)))
                ) if integerShape(program, integerType).exists(_._2 == 1) =>
              memory.integerPointer(rawAddress & mask(scalarWidth(program, integerType)), pointee, storageClass) match {
                case Left(error) =>
                  return Left(
                    ExecutionFailure.InvalidRequest(
                      s"${error.message} at OpConvertUToPtr function %${currentFunction.id}, pc $pc, " +
                        s"source %${id(integerValue)}, result %${id(dest)}"
                    )
                  )
                case Right(pointer) =>
                  values.update(id(dest), RuntimeValue.Address(id(resultType), pointer))
                  pc += 1
              }
            case (Some(RuntimeType.Pointer(_, _)), Some(_)) =>
              return Left(ExecutionFailure.InvalidRequest("OpConvertUToPtr requires a scalar integer operand"))
            case (_, None)    => return Left(undefined(integerValue))
            case (Some(_), _) =>
              return Left(ExecutionFailure.InvalidRequest("OpConvertUToPtr requires a pointer result type"))
            case (None, _) =>
              return Left(
                ExecutionFailure.InvalidRequest(s"OpConvertUToPtr result type %${id(resultType)} is undefined")
              )
          }
        case Instruction.ConvertSToF(resultType, dest, source) =>
          convertSignedToFloat(program, values, resultType, dest, source) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.ConvertUToF(resultType, dest, source) =>
          convertUnsignedToFloat(program, values, resultType, dest, source) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.ConvertFToS(resultType, dest, source) =>
          convertFloatToSigned(program, values, resultType, dest, source) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.ConvertFToU(resultType, dest, source) =>
          convertFloatToUnsigned(program, values, resultType, dest, source) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.FConvert(resultType, dest, source) =>
          convertFloating(program, values, resultType, dest, source) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.Undef(resultType, dest) =>
          zeroValue(program.types, resultType, dest, "OpUndef") match {
            case Left(error)  => return Left(error)
            case Right(value) =>
              values.update(id(dest), value)
              pc += 1
          }
        case Instruction.CopyObject(resultType, dest, source) =>
          values.get(id(source)) match {
            case Some(value) =>
              values.update(id(dest), retag(value, id(resultType)))
              pc += 1
            case None => return Left(undefined(source))
          }
        case Instruction.LifetimeStart(pointer, size) =>
          validateLifetime(program, values, "OpLifetimeStart", pointer, size) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.LifetimeStop(pointer, size) =>
          validateLifetime(program, values, "OpLifetimeStop", pointer, size) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.IAdd(resultType, dest, left, right) =>
          binary(program, values, resultType, dest, left, right)(_ + _) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.ISub(resultType, dest, left, right) =>
          binary(program, values, resultType, dest, left, right)(_ - _) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.IMul(resultType, dest, left, right) =>
          binary(program, values, resultType, dest, left, right)(_ * _) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.UDiv(resultType, dest, left, right) =>
          unsignedIntegerArithmetic(
            program,
            values,
            resultType,
            dest,
            left,
            right,
            UnsignedIntegerOperation.Divide
          ) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.UMod(resultType, dest, left, right) =>
          unsignedIntegerArithmetic(
            program,
            values,
            resultType,
            dest,
            left,
            right,
            UnsignedIntegerOperation.Remainder
          ) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.SDiv(resultType, dest, left, right) =>
          signedIntegerArithmetic(
            program,
            values,
            resultType,
            dest,
            left,
            right,
            SignedIntegerOperation.Divide
          ) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.SRem(resultType, dest, left, right) =>
          signedIntegerArithmetic(
            program,
            values,
            resultType,
            dest,
            left,
            right,
            SignedIntegerOperation.Remainder
          ) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.AtomicStore(pointer, scope, semantics, value) =>
          atomicStore(program, values, memory, pointer, scope, semantics, value) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.AtomicLoad(resultType, dest, pointer, scope, semantics) =>
          atomicLoad(program, values, memory, resultType, dest, pointer, scope, semantics) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.AtomicExchange(resultType, dest, pointer, scope, semantics, value) =>
          atomicExchange(program, values, memory, resultType, dest, pointer, scope, semantics, value) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.AtomicCompareExchange(resultType, dest, pointer, scope, equal, unequal, value, comparator) =>
          atomicCompareExchange(
            program,
            values,
            memory,
            resultType,
            dest,
            pointer,
            scope,
            equal,
            unequal,
            value,
            comparator
          ) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.AtomicCompareExchangeWeak(
              resultType,
              dest,
              pointer,
              scope,
              equal,
              unequal,
              value,
              comparator
            ) =>
          atomicCompareExchange(
            program,
            values,
            memory,
            resultType,
            dest,
            pointer,
            scope,
            equal,
            unequal,
            value,
            comparator
          ) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.AtomicFAddEXT(resultType, dest, pointer, memoryScope, semantics, value) =>
          atomicFloatingAdd(
            program,
            values,
            memory,
            resultType,
            dest,
            pointer,
            memoryScope,
            semantics,
            value
          ) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.AtomicFMinEXT(resultType, dest, pointer, memoryScope, semantics, value) =>
          atomicFloatingMinMax(
            program,
            values,
            memory,
            resultType,
            dest,
            pointer,
            memoryScope,
            semantics,
            value,
            maximum = false
          ) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.AtomicFMaxEXT(resultType, dest, pointer, memoryScope, semantics, value) =>
          atomicFloatingMinMax(
            program,
            values,
            memory,
            resultType,
            dest,
            pointer,
            memoryScope,
            semantics,
            value,
            maximum = true
          ) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.AtomicIAdd(resultType, dest, pointer, scope, semantics, value) =>
          atomicInteger(
            program,
            values,
            memory,
            resultType,
            dest,
            pointer,
            scope,
            semantics,
            Some(value),
            AtomicIntegerOperation.Add
          ) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.AtomicUMin(resultType, dest, pointer, scope, semantics, value) =>
          atomicInteger(
            program,
            values,
            memory,
            resultType,
            dest,
            pointer,
            scope,
            semantics,
            Some(value),
            AtomicIntegerOperation.UnsignedMinimum
          ) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.AtomicIIncrement(resultType, dest, pointer, scope, semantics) =>
          atomicInteger(
            program,
            values,
            memory,
            resultType,
            dest,
            pointer,
            scope,
            semantics,
            None,
            AtomicIntegerOperation.Increment
          ) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.AtomicIDecrement(resultType, dest, pointer, scope, semantics) =>
          atomicInteger(
            program,
            values,
            memory,
            resultType,
            dest,
            pointer,
            scope,
            semantics,
            None,
            AtomicIntegerOperation.Decrement
          ) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.AtomicISub(resultType, dest, pointer, scope, semantics, value) =>
          atomicInteger(
            program,
            values,
            memory,
            resultType,
            dest,
            pointer,
            scope,
            semantics,
            Some(value),
            AtomicIntegerOperation.Subtract
          ) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.AtomicSMin(resultType, dest, pointer, scope, semantics, value) =>
          atomicInteger(
            program,
            values,
            memory,
            resultType,
            dest,
            pointer,
            scope,
            semantics,
            Some(value),
            AtomicIntegerOperation.SignedMinimum
          ) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.AtomicSMax(resultType, dest, pointer, scope, semantics, value) =>
          atomicInteger(
            program,
            values,
            memory,
            resultType,
            dest,
            pointer,
            scope,
            semantics,
            Some(value),
            AtomicIntegerOperation.SignedMaximum
          ) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.AtomicUMax(resultType, dest, pointer, scope, semantics, value) =>
          atomicInteger(
            program,
            values,
            memory,
            resultType,
            dest,
            pointer,
            scope,
            semantics,
            Some(value),
            AtomicIntegerOperation.UnsignedMaximum
          ) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.AtomicAnd(resultType, dest, pointer, scope, semantics, value) =>
          atomicInteger(
            program,
            values,
            memory,
            resultType,
            dest,
            pointer,
            scope,
            semantics,
            Some(value),
            AtomicIntegerOperation.And
          ) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.AtomicOr(resultType, dest, pointer, scope, semantics, value) =>
          atomicInteger(
            program,
            values,
            memory,
            resultType,
            dest,
            pointer,
            scope,
            semantics,
            Some(value),
            AtomicIntegerOperation.Or
          ) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.AtomicXor(resultType, dest, pointer, scope, semantics, value) =>
          atomicInteger(
            program,
            values,
            memory,
            resultType,
            dest,
            pointer,
            scope,
            semantics,
            Some(value),
            AtomicIntegerOperation.Xor
          ) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.FAdd(resultType, dest, left, right) =>
          floatingBinary(program, values, resultType, dest, left, right, FloatingOperation.Add) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.FSub(resultType, dest, left, right) =>
          floatingBinary(program, values, resultType, dest, left, right, FloatingOperation.Subtract) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.FMul(resultType, dest, left, right) =>
          floatingBinary(program, values, resultType, dest, left, right, FloatingOperation.Multiply) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.Dot(resultType, dest, left, right) =>
          floatingDot(program, values, resultType, dest, left, right) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.FDiv(resultType, dest, left, right) =>
          floatingBinary(program, values, resultType, dest, left, right, FloatingOperation.Divide) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.FNegate(resultType, dest, source) =>
          floatingNegate(program, values, resultType, dest, source) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.ShiftLeftLogical(resultType, dest, base, shift) =>
          integerShift(program, values, resultType, dest, base, shift, "OpShiftLeftLogical") {
            (value, amount, width) =>
              if (java.lang.Long.compareUnsigned(amount, width.toLong) >= 0) 0L
              else value << amount.toInt
          } match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.ShiftRightLogical(resultType, dest, base, shift) =>
          integerShift(program, values, resultType, dest, base, shift, "OpShiftRightLogical") {
            (value, amount, width) =>
              if (java.lang.Long.compareUnsigned(amount, width.toLong) >= 0) 0L
              else (value & mask(width)) >>> amount.toInt
          } match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.ShiftRightArithmetic(resultType, dest, base, shift) =>
          integerShift(program, values, resultType, dest, base, shift, "OpShiftRightArithmetic") {
            (value, amount, width) =>
              val signed = signExtend(value, width)
              if (java.lang.Long.compareUnsigned(amount, width.toLong) >= 0) {
                if (signed < 0L) -1L else 0L
              } else signed >> amount.toInt
          } match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.BitwiseOr(resultType, dest, left, right) =>
          integerBitwise(program, values, resultType, dest, left, right, "OpBitwiseOr")(_ | _) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.BitwiseXor(resultType, dest, left, right) =>
          integerBitwise(program, values, resultType, dest, left, right, "OpBitwiseXor")(_ ^ _) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.BitwiseAnd(resultType, dest, left, right) =>
          integerBitwise(program, values, resultType, dest, left, right, "OpBitwiseAnd")(_ & _) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.Not(resultType, dest, source) =>
          integerNot(program, values, resultType, dest, source) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.IEqual(resultType, dest, left, right) =>
          integerEquality(program, values, resultType, dest, left, right, "OpIEqual")(_ == _) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.INotEqual(resultType, dest, left, right) =>
          integerEquality(program, values, resultType, dest, left, right, "OpINotEqual")(_ != _) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.LogicalEqual(resultType, dest, left, right) =>
          logicalBinary(program, values, resultType, dest, left, right, "OpLogicalEqual")(_ == _) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.LogicalNotEqual(resultType, dest, left, right) =>
          logicalBinary(program, values, resultType, dest, left, right, "OpLogicalNotEqual")(_ != _) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.LogicalOr(resultType, dest, left, right) =>
          logicalBinary(program, values, resultType, dest, left, right, "OpLogicalOr")(_ || _) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.LogicalAnd(resultType, dest, left, right) =>
          logicalBinary(program, values, resultType, dest, left, right, "OpLogicalAnd")(_ && _) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.LogicalNot(resultType, dest, source) =>
          logicalNot(program, values, resultType, dest, source) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.Any(resultType, dest, source) =>
          logicalReduction(program, values, resultType, dest, source, all = false) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.All(resultType, dest, source) =>
          logicalReduction(program, values, resultType, dest, source, all = true) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.FOrdEqual(resultType, dest, left, right) =>
          floatingCompare(program, values, resultType, dest, left, right, FloatingComparison.OrderedEqual) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.FOrdNotEqual(resultType, dest, left, right) =>
          floatingCompare(program, values, resultType, dest, left, right, FloatingComparison.OrderedNotEqual) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.FUnordEqual(resultType, dest, left, right) =>
          floatingCompare(program, values, resultType, dest, left, right, FloatingComparison.UnorderedEqual) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.FUnordNotEqual(resultType, dest, left, right) =>
          floatingCompare(program, values, resultType, dest, left, right, FloatingComparison.UnorderedNotEqual) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.FUnordLessThan(resultType, dest, left, right) =>
          floatingCompare(program, values, resultType, dest, left, right, FloatingComparison.UnorderedLessThan) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.FUnordGreaterThan(resultType, dest, left, right) =>
          floatingCompare(
            program,
            values,
            resultType,
            dest,
            left,
            right,
            FloatingComparison.UnorderedGreaterThan
          ) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.FUnordLessThanEqual(resultType, dest, left, right) =>
          floatingCompare(
            program,
            values,
            resultType,
            dest,
            left,
            right,
            FloatingComparison.UnorderedLessThanEqual
          ) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.FUnordGreaterThanEqual(resultType, dest, left, right) =>
          floatingCompare(
            program,
            values,
            resultType,
            dest,
            left,
            right,
            FloatingComparison.UnorderedGreaterThanEqual
          ) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.Unordered(resultType, dest, left, right) =>
          floatingCompare(program, values, resultType, dest, left, right, FloatingComparison.Unordered) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.Ordered(resultType, dest, left, right) =>
          floatingCompare(program, values, resultType, dest, left, right, FloatingComparison.Ordered) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.FOrdLessThanEqual(resultType, dest, left, right) =>
          floatingCompare(
            program,
            values,
            resultType,
            dest,
            left,
            right,
            FloatingComparison.OrderedLessThanEqual
          ) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.FOrdLessThan(resultType, dest, left, right) =>
          floatingCompare(program, values, resultType, dest, left, right, FloatingComparison.OrderedLessThan) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.FOrdGreaterThan(resultType, dest, left, right) =>
          floatingCompare(program, values, resultType, dest, left, right, FloatingComparison.OrderedGreaterThan) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.FOrdGreaterThanEqual(resultType, dest, left, right) =>
          floatingCompare(
            program,
            values,
            resultType,
            dest,
            left,
            right,
            FloatingComparison.OrderedGreaterThanEqual
          ) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.IsInf(resultType, dest, value) =>
          floatingClassify(program, values, resultType, dest, value, FloatingClassification.Infinite) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.IsNan(resultType, dest, value) =>
          floatingClassify(program, values, resultType, dest, value, FloatingClassification.NaN) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.IsFinite(resultType, dest, value) =>
          floatingClassify(program, values, resultType, dest, value, FloatingClassification.Finite) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.IsNormal(resultType, dest, value) =>
          floatingClassify(program, values, resultType, dest, value, FloatingClassification.Normal) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.SignBitSet(resultType, dest, value) =>
          floatingClassify(program, values, resultType, dest, value, FloatingClassification.Signed) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.ULessThan(resultType, dest, left, right) =>
          compare(values, resultType, dest, left, right)(java.lang.Long.compareUnsigned(_, _) < 0) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.UGreaterThan(resultType, dest, left, right) =>
          compare(values, resultType, dest, left, right)(java.lang.Long.compareUnsigned(_, _) > 0) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.ULessThanEqual(resultType, dest, left, right) =>
          compare(values, resultType, dest, left, right)(java.lang.Long.compareUnsigned(_, _) <= 0) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.UGreaterThanEqual(resultType, dest, left, right) =>
          compare(values, resultType, dest, left, right)(java.lang.Long.compareUnsigned(_, _) >= 0) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.SGreaterThan(resultType, dest, left, right) =>
          signedCompare(program, values, resultType, dest, left, right, "OpSGreaterThan")(_ > _) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.SGreaterThanEqual(resultType, dest, left, right) =>
          signedCompare(program, values, resultType, dest, left, right, "OpSGreaterThanEqual")(_ >= _) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.SLessThan(resultType, dest, left, right) =>
          signedCompare(program, values, resultType, dest, left, right, "OpSLessThan")(_ < _) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.SLessThanEqual(resultType, dest, left, right) =>
          signedCompare(program, values, resultType, dest, left, right, "OpSLessThanEqual")(_ <= _) match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.Select(resultType, dest, condition, whenTrue, whenFalse) =>
          val trueId  = id(whenTrue)
          val falseId = id(whenFalse)
          (values.get(id(condition)), values.get(trueId), values.get(falseId)) match {
            case (
                  Some(RuntimeValue.Bits(conditionType, conditionLanes)),
                  Some(RuntimeValue.Bits(_, trueLanes)),
                  Some(RuntimeValue.Bits(_, falseLanes))
                ) =>
              (booleanLanes(program, conditionType), scalarLeafCount(program, id(resultType))) match {
                case (Some(conditionCount), Right(resultCount))
                    if trueLanes.length == resultCount && falseLanes.length == resultCount &&
                      (conditionCount == 1 || conditionCount == resultCount) =>
                  val result = Array.tabulate(resultCount) { index =>
                    val selected = if (conditionLanes(if (conditionCount == 1) 0 else index) != 0L) trueLanes
                    else falseLanes
                    selected(index)
                  }
                  values.update(id(dest), RuntimeValue.Bits(id(resultType), result))
                  pc += 1
                case (None, _) =>
                  return Left(ExecutionFailure.InvalidRequest(s"OpSelect condition %${id(condition)} is not boolean"))
                case (_, Left(error)) => return Left(error)
                case _                =>
                  return Left(
                    ExecutionFailure.InvalidRequest(
                      s"OpSelect condition/value shapes are incompatible: condition ${conditionLanes.length}, " +
                        s"true ${trueLanes.length}, false ${falseLanes.length}"
                    )
                  )
              }
            case (Some(RuntimeValue.Bits(conditionType, conditionLanes)), Some(yes), Some(no))
                if booleanLanes(program, conditionType).contains(1) && conditionLanes.length == 1 =>
              values.update(id(dest), retag(if (conditionLanes(0) != 0L) yes else no, id(resultType)))
              pc += 1
            case (None, _, _) =>
              return Left(ExecutionFailure.InvalidRequest(s"OpSelect condition %${id(condition)} is not boolean"))
            case (_, None, _) => return Left(undefined(whenTrue))
            case (_, _, None) => return Left(undefined(whenFalse))
            case _            =>
              return Left(ExecutionFailure.InvalidRequest(s"OpSelect condition %${id(condition)} is invalid"))
          }
        case Instruction.ControlBarrier(executionScope, memoryScope, semantics) =>
          controlBarrier(values, currentFunction.id, pc, executionScope, memoryScope, semantics) match {
            case Left(error)    => return Left(error)
            case Right(request) =>
              suspend(execution, currentFunction, pc, currentLabel, predecessor, instructionCount, request)
              return Right(())
          }
        case Instruction.MemoryBarrier(memoryScope, semantics) =>
          validateAtomicControls(values, memoryScope, semantics, "OpMemoryBarrier") match {
            case Left(error) => return Left(error)
            case Right(())   => pc += 1
          }
        case Instruction.GroupAny(resultType, dest, executionScope, pred) =>
          collectiveAny(program, values, currentFunction.id, pc, resultType, dest, executionScope, pred) match {
            case Left(error)    => return Left(error)
            case Right(request) =>
              suspend(execution, currentFunction, pc, currentLabel, predecessor, instructionCount, request)
              return Right(())
          }
        case Instruction.GroupAll(resultType, dest, executionScope, pred) =>
          collectiveBoolean(
            program,
            values,
            currentFunction.id,
            pc,
            resultType,
            dest,
            executionScope,
            pred,
            CollectiveKind.All,
            "OpGroupAll"
          ) match {
            case Left(error)    => return Left(error)
            case Right(request) =>
              suspend(execution, currentFunction, pc, currentLabel, predecessor, instructionCount, request)
              return Right(())
          }
        case Instruction.GroupUMin(resultType, dest, executionScope, operation, value) =>
          collectiveIntegerExtremum(
            program,
            values,
            currentFunction.id,
            pc,
            resultType,
            dest,
            executionScope,
            operation,
            value,
            CollectiveKind.UnsignedMin,
            "OpGroupUMin"
          ) match {
            case Left(error)    => return Left(error)
            case Right(request) =>
              suspend(execution, currentFunction, pc, currentLabel, predecessor, instructionCount, request)
              return Right(())
          }
        case Instruction.GroupUMax(resultType, dest, executionScope, operation, value) =>
          collectiveIntegerExtremum(
            program,
            values,
            currentFunction.id,
            pc,
            resultType,
            dest,
            executionScope,
            operation,
            value,
            CollectiveKind.UnsignedMax,
            "OpGroupUMax"
          ) match {
            case Left(error)    => return Left(error)
            case Right(request) =>
              suspend(execution, currentFunction, pc, currentLabel, predecessor, instructionCount, request)
              return Right(())
          }
        case Instruction.GroupSMin(resultType, dest, executionScope, operation, value) =>
          collectiveIntegerExtremum(
            program,
            values,
            currentFunction.id,
            pc,
            resultType,
            dest,
            executionScope,
            operation,
            value,
            CollectiveKind.SignedMin,
            "OpGroupSMin"
          ) match {
            case Left(error)    => return Left(error)
            case Right(request) =>
              suspend(execution, currentFunction, pc, currentLabel, predecessor, instructionCount, request)
              return Right(())
          }
        case Instruction.GroupSMax(resultType, dest, executionScope, operation, value) =>
          collectiveIntegerExtremum(
            program,
            values,
            currentFunction.id,
            pc,
            resultType,
            dest,
            executionScope,
            operation,
            value,
            CollectiveKind.SignedMax,
            "OpGroupSMax"
          ) match {
            case Left(error)    => return Left(error)
            case Right(request) =>
              suspend(execution, currentFunction, pc, currentLabel, predecessor, instructionCount, request)
              return Right(())
          }
        case Instruction.GroupIAdd(resultType, dest, executionScope, operation, value) =>
          collectiveIntegerAdd(
            program,
            values,
            currentFunction.id,
            pc,
            resultType,
            dest,
            executionScope,
            operation,
            value
          ) match {
            case Left(error)    => return Left(error)
            case Right(request) =>
              suspend(execution, currentFunction, pc, currentLabel, predecessor, instructionCount, request)
              return Right(())
          }
        case Instruction.GroupIMulKHR(resultType, dest, executionScope, operation, value) =>
          collectiveIntegerMultiply(
            program,
            values,
            currentFunction.id,
            pc,
            resultType,
            dest,
            executionScope,
            operation,
            value
          ) match {
            case Left(error)    => return Left(error)
            case Right(request) =>
              suspend(execution, currentFunction, pc, currentLabel, predecessor, instructionCount, request)
              return Right(())
          }
        case Instruction.GroupFAdd(resultType, dest, executionScope, operation, value) =>
          collectiveFloatingAdd(
            program,
            values,
            currentFunction.id,
            pc,
            resultType,
            dest,
            executionScope,
            operation,
            value
          ) match {
            case Left(error)    => return Left(error)
            case Right(request) =>
              suspend(execution, currentFunction, pc, currentLabel, predecessor, instructionCount, request)
              return Right(())
          }
        case Instruction.GroupFMin(resultType, dest, executionScope, operation, value) =>
          collectiveFloatingExtremum(
            program,
            values,
            currentFunction.id,
            pc,
            resultType,
            dest,
            executionScope,
            operation,
            value,
            CollectiveKind.FloatingMin,
            "OpGroupFMin"
          ) match {
            case Left(error)    => return Left(error)
            case Right(request) =>
              suspend(execution, currentFunction, pc, currentLabel, predecessor, instructionCount, request)
              return Right(())
          }
        case Instruction.GroupFMax(resultType, dest, executionScope, operation, value) =>
          collectiveFloatingExtremum(
            program,
            values,
            currentFunction.id,
            pc,
            resultType,
            dest,
            executionScope,
            operation,
            value,
            CollectiveKind.FloatingMax,
            "OpGroupFMax"
          ) match {
            case Left(error)    => return Left(error)
            case Right(request) =>
              suspend(execution, currentFunction, pc, currentLabel, predecessor, instructionCount, request)
              return Right(())
          }
        case Instruction.GroupFMulKHR(resultType, dest, executionScope, operation, value) =>
          collectiveFloatingMultiply(
            program,
            values,
            currentFunction.id,
            pc,
            resultType,
            dest,
            executionScope,
            operation,
            value
          ) match {
            case Left(error)    => return Left(error)
            case Right(request) =>
              suspend(execution, currentFunction, pc, currentLabel, predecessor, instructionCount, request)
              return Right(())
          }
        case Instruction.SubgroupShuffleINTEL(resultType, dest, data, invocationId) =>
          collectiveShuffle(values, currentFunction.id, pc, resultType, dest, data, invocationId) match {
            case Left(error)    => return Left(error)
            case Right(request) =>
              suspend(execution, currentFunction, pc, currentLabel, predecessor, instructionCount, request)
              return Right(())
          }
        case Instruction.SubgroupShuffleUpINTEL(resultType, dest, previous, current, delta) =>
          collectiveShuffleUp(values, currentFunction.id, pc, resultType, dest, previous, current, delta) match {
            case Left(error)    => return Left(error)
            case Right(request) =>
              suspend(execution, currentFunction, pc, currentLabel, predecessor, instructionCount, request)
              return Right(())
          }
        case Instruction.SubgroupShuffleDownINTEL(resultType, dest, current, next, delta) =>
          collectiveShuffleDown(values, currentFunction.id, pc, resultType, dest, current, next, delta) match {
            case Left(error)    => return Left(error)
            case Right(request) =>
              suspend(execution, currentFunction, pc, currentLabel, predecessor, instructionCount, request)
              return Right(())
          }
        case Instruction.GroupBroadcast(resultType, dest, executionScope, value, localId) =>
          collectiveBroadcast(
            program,
            values,
            currentFunction.id,
            pc,
            resultType,
            dest,
            executionScope,
            value,
            localId
          ) match {
            case Left(error)    => return Left(error)
            case Right(request) =>
              suspend(execution, currentFunction, pc, currentLabel, predecessor, instructionCount, request)
              return Right(())
          }
        case Instruction.FunctionCall(resultType, dest, called, arguments) =>
          val callee = program.functions.get(id(called)) match {
            case Some(value) => value
            case None        =>
              return Left(
                ExecutionFailure.InvalidRequest(s"OpFunctionCall references undefined function %${id(called)}")
              )
          }
          if (arguments.size != callee.parameters.size)
            return Left(
              ExecutionFailure.InvalidRequest(
                s"OpFunctionCall %${id(called)} expects ${callee.parameters.size} arguments, received ${arguments.size}"
              )
            )
          program.functionNames.get(id(called)) match {
            case Some("_Z12get_local_idj") =>
              scalar(values, arguments.head) match {
                case Left(error)                                           => return Left(error)
                case Right(dimension) if dimension >= 0L && dimension < 3L =>
                  integerShape(program, id(resultType)) match {
                    case Some((width, 1)) =>
                      values.update(
                        id(dest),
                        RuntimeValue.Bits(
                          id(resultType),
                          Array(execution.invocation.local(dimension.toInt) & mask(width))
                        )
                      )
                      pc += 1
                    case _ =>
                      return Left(
                        ExecutionFailure.InvalidRequest(
                          s"OpenCL get_local_id result type %${id(resultType)} is not an integer scalar"
                        )
                      )
                  }
                case Right(dimension) =>
                  return Left(ExecutionFailure.InvalidRequest(s"OpenCL get_local_id dimension $dimension is invalid"))
              }
            case Some("_Z12get_group_idj") | Some("_Z14get_local_sizej") =>
              scalar(values, arguments.head) match {
                case Left(error)                                           => return Left(error)
                case Right(dimension) if dimension >= 0L && dimension < 3L =>
                  integerShape(program, id(resultType)) match {
                    case Some((width, 1)) =>
                      val source = program.functionNames.get(id(called)) match {
                        case Some("_Z12get_group_idj")   => execution.invocation.group(dimension.toInt)
                        case Some("_Z14get_local_sizej") => execution.invocation.workgroupSize(dimension.toInt)
                        case _                           => 0L
                      }
                      values.update(id(dest), RuntimeValue.Bits(id(resultType), Array(source & mask(width))))
                      pc += 1
                    case _ =>
                      return Left(
                        ExecutionFailure.InvalidRequest(
                          s"OpenCL ${program.functionNames.getOrElse(id(called), "builtin")} result type " +
                            s"%${id(resultType)} is not an integer scalar"
                        )
                      )
                  }
                case Right(dimension) =>
                  return Left(
                    ExecutionFailure.InvalidRequest(
                      s"OpenCL ${program.functionNames.getOrElse(id(called), "builtin")} dimension $dimension is invalid"
                    )
                  )
              }
            case Some("_Z13get_global_idj") =>
              scalar(values, arguments.head) match {
                case Left(error)                                           => return Left(error)
                case Right(dimension) if dimension >= 0L && dimension < 3L =>
                  integerShape(program, id(resultType)) match {
                    case Some((width, 1)) =>
                      values.update(
                        id(dest),
                        RuntimeValue.Bits(
                          id(resultType),
                          Array(execution.invocation.global(dimension.toInt) & mask(width))
                        )
                      )
                      pc += 1
                    case _ =>
                      return Left(
                        ExecutionFailure.InvalidRequest(
                          "OpenCL get_global_id result type is not an integer scalar"
                        )
                      )
                  }
                case Right(dimension) =>
                  return Left(ExecutionFailure.InvalidRequest(s"OpenCL get_global_id dimension $dimension is invalid"))
              }
            case Some("_Z15get_global_sizej") | Some("_Z17get_global_offsetj") | Some("_Z14get_num_groupsj") =>
              scalar(values, arguments.head) match {
                case Left(error)                                           => return Left(error)
                case Right(dimension) if dimension >= 0L && dimension < 3L =>
                  integerShape(program, id(resultType)) match {
                    case Some((width, 1)) =>
                      val source = program.functionNames.get(id(called)) match {
                        case Some("_Z15get_global_sizej")   => execution.invocation.globalSize(dimension.toInt)
                        case Some("_Z17get_global_offsetj") => execution.invocation.globalOffset(dimension.toInt)
                        case Some("_Z14get_num_groupsj")    => execution.invocation.numWorkgroups(dimension.toInt)
                        case _                              => 0L
                      }
                      values.update(id(dest), RuntimeValue.Bits(id(resultType), Array(source & mask(width))))
                      pc += 1
                    case _ =>
                      return Left(
                        ExecutionFailure.InvalidRequest(
                          s"OpenCL ${program.functionNames.getOrElse(id(called), "builtin")} result type is not an integer scalar"
                        )
                      )
                  }
                case Right(dimension) =>
                  return Left(
                    ExecutionFailure.InvalidRequest(
                      s"OpenCL ${program.functionNames.getOrElse(id(called), "builtin")} dimension $dimension is invalid"
                    )
                  )
              }
            case Some("_Z18get_sub_group_sizev") | Some("_Z22get_sub_group_local_idv") |
                Some("_Z16get_sub_group_idv") | Some("_Z18get_num_sub_groupsv") =>
              integerShape(program, id(resultType)) match {
                case Some((width, 1)) =>
                  val source = program.functionNames.get(id(called)) match {
                    case Some("_Z18get_sub_group_sizev")     => execution.invocation.subgroupSize.toLong
                    case Some("_Z22get_sub_group_local_idv") => execution.invocation.subgroupLocal.toLong
                    case Some("_Z16get_sub_group_idv")       => execution.invocation.subgroupIndex.toLong
                    case Some("_Z18get_num_sub_groupsv")     => execution.invocation.numSubgroups
                    case _                                   => 0L
                  }
                  values.update(id(dest), RuntimeValue.Bits(id(resultType), Array(source & mask(width))))
                  pc += 1
                case _ =>
                  return Left(
                    ExecutionFailure.InvalidRequest(
                      s"OpenCL ${program.functionNames.getOrElse(id(called), "builtin")} result type " +
                        s"%${id(resultType)} is not an integer scalar"
                    )
                  )
              }
            case Some("_Z3minmm") =>
              (
                scalar(values, arguments(0)),
                scalar(values, arguments(1)),
                integerShape(program, id(resultType))
              ) match {
                case (Right(left), Right(right), Some((width, 1))) =>
                  val result = if (java.lang.Long.compareUnsigned(left, right) <= 0) left else right
                  values.update(id(dest), RuntimeValue.Bits(id(resultType), Array(result & mask(width))))
                  pc += 1
                case (Left(error), _, _) => return Left(error)
                case (_, Left(error), _) => return Left(error)
                case (_, _, _)           =>
                  return Left(
                    ExecutionFailure.InvalidRequest("OpenCL min requires integer scalar arguments and result")
                  )
              }
            case Some("_Z3ctzi") =>
              val sourceType = values.scalarType(id(arguments.head))
              (
                scalar(values, arguments.head),
                integerShape(program, sourceType),
                integerShape(program, id(resultType))
              ) match {
                case (Right(value), Some((sourceWidth, 1)), Some((resultWidth, 1))) =>
                  val bits  = value & mask(sourceWidth)
                  val count = if (bits == 0L) sourceWidth.toLong else java.lang.Long.numberOfTrailingZeros(bits).toLong
                  values.update(id(dest), RuntimeValue.Bits(id(resultType), Array(count & mask(resultWidth))))
                  pc += 1
                case (Left(error), _, _) => return Left(error)
                case _                   =>
                  return Left(
                    ExecutionFailure.InvalidRequest("OpenCL ctz requires integer scalar arguments and result")
                  )
              }
            case Some(name) if name.startsWith("_Z26intel_sub_group_shuffle_up") =>
              if (arguments.size != 3)
                return Left(
                  ExecutionFailure.InvalidRequest(
                    s"OpenCL $name expects current, next, and delta arguments"
                  )
                )
              (values.get(id(arguments(0))), values.get(id(arguments(1))), scalar(values, arguments(2))) match {
                case (
                      previousValue @ Some(RuntimeValue.Bits(previousType, previousLanes)),
                      currentValue @ Some(RuntimeValue.Bits(currentType, currentLanes)),
                      Right(delta)
                    )
                    if previousType == id(resultType) && currentType == id(resultType) &&
                      previousLanes.length == currentLanes.length && delta >= 0L =>
                  suspend(
                    execution,
                    currentFunction,
                    pc,
                    currentLabel,
                    predecessor,
                    instructionCount,
                    CollectiveRequest(
                      CollectiveKind.ShuffleUp,
                      currentFunction.id,
                      pc,
                      3,
                      CollectivePayload.ShuffleUp(
                        id(resultType),
                        id(dest),
                        previousValue.get,
                        currentValue.get,
                        delta
                      )
                    )
                  )
                  return Right(())
                case (Some(_), Some(_), Right(delta)) if delta < 0L =>
                  return Left(ExecutionFailure.InvalidRequest("OpenCL subgroup shuffle-up delta must be non-negative"))
                case (Some(_), Some(_), Right(_)) =>
                  return Left(ExecutionFailure.InvalidRequest(
                    "OpenCL subgroup shuffle-up requires matching result and operand types"
                  ))
                case (None, _, _)        => return Left(undefined(arguments(0)))
                case (_, None, _)        => return Left(undefined(arguments(1)))
                case (_, _, Left(error)) => return Left(error)
              }
            case Some("__devicelib_ConvertFToBF16INTEL") =>
              if (arguments.size != 1)
                return Left(
                  ExecutionFailure.InvalidRequest(
                    s"__devicelib_ConvertFToBF16INTEL expects one pointer argument, received ${arguments.size}"
                  )
                )
              values.get(id(arguments.head)) match {
                case Some(RuntimeValue.Address(_, pointer)) =>
                  memory.readScalar(pointer, 4) match {
                    case Left(error) => return Left(error)
                    case Right(raw)  =>
                      values.update(
                        id(dest),
                        RuntimeValue.Bits(id(resultType), Array(float32ToBfloat16Bits(raw)))
                      )
                      pc += 1
                  }
                case Some(_) =>
                  return Left(
                    ExecutionFailure.InvalidRequest("__devicelib_ConvertFToBF16INTEL argument is not a pointer")
                  )
                case None => return Left(undefined(arguments.head))
              }
            case Some("__devicelib_ConvertBF16ToFINTEL") =>
              if (arguments.size != 1)
                return Left(
                  ExecutionFailure.InvalidRequest(
                    s"__devicelib_ConvertBF16ToFINTEL expects one pointer argument, received ${arguments.size}"
                  )
                )
              values.get(id(arguments.head)) match {
                case Some(RuntimeValue.Address(_, pointer)) =>
                  memory.readScalar(pointer, 2) match {
                    case Left(error) => return Left(error)
                    case Right(raw)  =>
                      values.update(
                        id(dest),
                        RuntimeValue.Bits(id(resultType), Array(bfloat16ToFloat32Bits(raw)))
                      )
                      pc += 1
                  }
                case Some(_) =>
                  return Left(
                    ExecutionFailure.InvalidRequest("__devicelib_ConvertBF16ToFINTEL argument is not a pointer")
                  )
                case None => return Left(undefined(arguments.head))
              }
            case Some("__to_global") =>
              (values.get(id(arguments.head)), program.types.get(id(resultType))) match {
                case (Some(RuntimeValue.Address(_, pointer)), Some(RuntimeType.Pointer(storageClass, pointee))) =>
                  values.update(
                    id(dest),
                    RuntimeValue.Address(
                      id(resultType),
                      pointer.copy(pointee = pointee, storageClass = storageClass)
                    )
                  )
                  pc += 1
                case (Some(RuntimeValue.Address(_, _)), _) =>
                  return Left(ExecutionFailure.InvalidRequest("OpenCL __to_global result is not a pointer"))
                case (Some(_), _) =>
                  return Left(ExecutionFailure.InvalidRequest("OpenCL __to_global source is not a pointer"))
                case (None, _) => return Left(undefined(arguments.head))
              }
            case Some("_Z5isnanf") =>
              values.get(id(arguments.head)) match {
                case Some(RuntimeValue.Bits(sourceType, Array(raw))) =>
                  (floatingShape(program, sourceType), integerShape(program, id(resultType))) match {
                    case (Some((width, 1)), Some((resultWidth, 1))) =>
                      val (exponentMask, fractionMask) = width match {
                        case 16 => (0x7c00L, 0x03ffL)
                        case 32 => (0x7f800000L, 0x007fffffL)
                        case 64 => (0x7ff0000000000000L, 0x000fffffffffffffL)
                      }
                      val bits  = raw & mask(width)
                      val isNan = (bits & exponentMask) == exponentMask && (bits & fractionMask) != 0L
                      values.update(
                        id(dest),
                        RuntimeValue.Bits(id(resultType), Array((if (isNan) 1L else 0L) & mask(resultWidth)))
                      )
                      pc += 1
                    case (None, _) =>
                      return Left(ExecutionFailure.InvalidRequest("OpenCL isnan source is not a floating-point scalar"))
                    case (_, None) =>
                      return Left(ExecutionFailure.InvalidRequest("OpenCL isnan result is not an integer scalar"))
                    case _ =>
                      return Left(
                        ExecutionFailure.InvalidRequest("OpenCL isnan requires scalar source and result types")
                      )
                  }
                case Some(_) =>
                  return Left(ExecutionFailure.InvalidRequest("OpenCL isnan source is not numeric"))
                case None => return Left(undefined(arguments.head))
              }
            case Some("_Z7barrierj") =>
              scalar(values, arguments.head) match {
                case Left(error)  => return Left(error)
                case Right(flags) =>
                  suspend(
                    execution,
                    currentFunction,
                    pc,
                    currentLabel,
                    predecessor,
                    instructionCount,
                    CollectiveRequest(
                      CollectiveKind.Barrier,
                      currentFunction.id,
                      pc,
                      2,
                      CollectivePayload.Barrier(2, flags)
                    )
                  )
                  return Right(())
              }
            case Some("_Z29work_group_scan_exclusive_addi") =>
              values.get(id(arguments.head)) match {
                case Some(value @ RuntimeValue.Bits(sourceType, lanes))
                    if lanes.length == 1 && integerShape(program, sourceType).isDefined &&
                      integerShape(program, sourceType) == integerShape(program, id(resultType)) =>
                  suspend(
                    execution,
                    currentFunction,
                    pc,
                    currentLabel,
                    predecessor,
                    instructionCount,
                    CollectiveRequest(
                      CollectiveKind.IntegerAdd,
                      currentFunction.id,
                      pc,
                      2,
                      CollectivePayload.Value(id(resultType), id(dest), value),
                      operation = 2
                    )
                  )
                  return Right(())
                case Some(_) =>
                  return Left(
                    ExecutionFailure.InvalidRequest(
                      "OpenCL work_group_scan_exclusive_add requires matching integer scalar types"
                    )
                  )
                case None => return Left(undefined(arguments.head))
              }
            case Some("_Z29work_group_scan_exclusive_addl") | Some("_Z28sub_group_scan_exclusive_addi") =>
              values.get(id(arguments.head)) match {
                case Some(value @ RuntimeValue.Bits(sourceType, lanes))
                    if lanes.length == 1 && integerShape(program, sourceType).isDefined &&
                      integerShape(program, sourceType) == integerShape(program, id(resultType)) =>
                  val scope = program.functionNames.get(id(called)) match {
                    case Some("_Z28sub_group_scan_exclusive_addi") => 3
                    case _                                         => 2
                  }
                  suspend(
                    execution,
                    currentFunction,
                    pc,
                    currentLabel,
                    predecessor,
                    instructionCount,
                    CollectiveRequest(
                      CollectiveKind.IntegerAdd,
                      currentFunction.id,
                      pc,
                      scope,
                      CollectivePayload.Value(id(resultType), id(dest), value),
                      operation = 2
                    )
                  )
                  return Right(())
                case Some(_) =>
                  return Left(
                    ExecutionFailure.InvalidRequest(
                      "OpenCL scan requires matching integer scalar types"
                    )
                  )
                case None => return Left(undefined(arguments.head))
              }
            case Some(name)
                if openCLBroadcastScope(name).isDefined =>
              (
                values.get(id(arguments(0))),
                scalar(values, arguments(1)),
                integerShape(program, id(resultType))
              ) match {
                case (Some(value @ RuntimeValue.Bits(sourceType, lanes)), Right(localId), Some((width, count)))
                    if lanes.length == count && integerShape(program, sourceType) == Some((width, count)) =>
                  val scope = openCLBroadcastScope(name).get
                  suspend(
                    execution,
                    currentFunction,
                    pc,
                    currentLabel,
                    predecessor,
                    instructionCount,
                    CollectiveRequest(
                      CollectiveKind.Broadcast,
                      currentFunction.id,
                      pc,
                      scope,
                      CollectivePayload.Value(id(resultType), id(dest), value),
                      selector = Col(localId & mask(width))
                    )
                  )
                  return Right(())
                case (None, _, _)        => return Left(undefined(arguments(0)))
                case (_, Left(error), _) => return Left(error)
                case _                   =>
                  return Left(
                    ExecutionFailure.InvalidRequest(
                      "OpenCL broadcast requires matching integer scalar arguments and result"
                    )
                  )
              }
            case _ =>
              if (calls.size >= 128)
                return Left(ExecutionFailure.Failed("SPIR-V function call depth exceeds 128 frames"))
              var argumentIndex = 0
              while (argumentIndex < arguments.size) {
                values.get(id(arguments(argumentIndex))) match {
                  case Some(value) =>
                    val (parameterId, parameterType) = callee.parameters(argumentIndex)
                    values.update(parameterId, retag(value, parameterType))
                  case None => return Left(undefined(arguments(argumentIndex)))
                }
                argumentIndex += 1
              }
              calls.append(
                CallFrame(currentFunction, pc + 1, currentLabel, predecessor, id(resultType), id(dest))
              )
              currentFunction = callee
              pc = callee.start + 1
              currentLabel = -1
              predecessor = -1
          }
        case Instruction.Branch(target) =>
          predecessor = currentLabel
          pc = label(currentFunction, target)
        case Instruction.BranchConditional(condition, whenTrue, whenFalse, _) =>
          scalar(values, condition) match {
            case Left(error) => return Left(error)
            case Right(test) =>
              predecessor = currentLabel
              pc = label(currentFunction, if (test != 0L) whenTrue else whenFalse)
          }
        case Instruction.Switch(selector, default, targets) =>
          scalar(values, selector) match {
            case Left(error)  => return Left(error)
            case Right(value) =>
              var target = default
              var index  = 0
              while (index < targets.size) {
                val pair    = targets(index)
                val literal = pair.words match {
                  case Col(low, _) =>
                    if (pair.kind != "PairLiteralIntegerIdRef")
                      return Left(ExecutionFailure.InvalidRequest("OpSwitch target is not a literal/label pair"))
                    low.toUnsigned
                  case Col(low, high, _) =>
                    if (pair.kind != "PairLiteralIntegerIdRef")
                      return Left(ExecutionFailure.InvalidRequest("OpSwitch target is not a literal/label pair"))
                    low.toUnsigned | (high.toUnsigned << 32)
                  case _ =>
                    return Left(ExecutionFailure.InvalidRequest("OpSwitch has an incomplete literal/label pair"))
                }
                if (pair.words.size != 2 && pair.words.size != 3)
                  return Left(ExecutionFailure.InvalidRequest("OpSwitch has an incomplete literal/label pair"))
                val label = pair.words.last
                if (literal == value)
                  target = Operand("IdRef", Col(label))
                index += 1
              }
              predecessor = currentLabel
              pc = label(currentFunction, target)
          }
        case Instruction.ReturnValue(value) =>
          val returned = values.get(id(value)) match {
            case Some(result) => result
            case None         => return Left(undefined(value))
          }
          calls.removeLastOption() match {
            case None        => return Left(ExecutionFailure.InvalidRequest("SPIR-V entry point returned a value"))
            case Some(frame) =>
              values.update(frame.destination, retag(returned, frame.resultType))
              currentFunction = frame.function
              pc = frame.pc
              currentLabel = frame.currentLabel
              predecessor = frame.predecessor
          }
        case Instruction.Return() => calls.removeLastOption() match {
            case None =>
              execution.completed = true
              return Right(())
            case Some(frame) => program.types.get(frame.resultType) match {
                case Some(RuntimeType.Void) =>
                  currentFunction = frame.function
                  pc = frame.pc
                  currentLabel = frame.currentLabel
                  predecessor = frame.predecessor
                case _ =>
                  return Left(
                    ExecutionFailure.InvalidRequest(
                      s"SPIR-V function %${currentFunction.id} returned no value to %${frame.destination}"
                    )
                  )
              }
          }
        case Instruction.AssumeTrueKHR(_) | Instruction.LoopMerge(_, _, _) | Instruction.SelectionMerge(_, _) =>
          pc += 1
        case instruction =>
          return Left(ExecutionFailure.Unsupported(s"${instruction.opcode} execution is not implemented"))
      }
    }
    Left(
      ExecutionFailure.InvalidRequest(
        s"SPIR-V function %${currentFunction.id} " +
          s"(${program.functionNames.getOrElse(currentFunction.id, "unnamed")}) " +
          "reached OpFunctionEnd without OpReturn"
      )
    )
  }

  private def suspend(
      execution: InvocationExecution,
      function: FunctionInfo,
      pc: Int,
      currentLabel: Int,
      predecessor: Int,
      instructionCount: Long,
      request: CollectiveRequest
  ): Unit = {
    execution.currentFunction = function
    execution.pc = pc
    execution.currentLabel = currentLabel
    execution.predecessor = predecessor
    execution.instructionCount = instructionCount
    execution.suspended = Some(request)
  }

  private def runWorkgroup(
      program: DispatchProgram,
      executions: mutable.ArrayBuffer[InvocationExecution],
      memory: MemoryState,
      control: ExecutionControl,
      metrics: PerformanceMetrics | Null
  ): Either[ExecutionFailure, Unit] = {
    while (executions.exists(!_.completed)) {
      var index = 0
      while (index < executions.size) {
        val execution = executions(index)
        if (!execution.completed && execution.suspended.isEmpty)
          resumeInvocation(program, execution, memory, control, metrics) match {
            case Left(error) => return Left(error)
            case Right(())   => ()
          }
        index += 1
      }
      resolveCollectives(program, executions) match {
        case Left(error)                                 => return Left(error)
        case Right(0) if executions.exists(!_.completed) =>
          val waiting = executions.iterator
            .filter(!_.completed)
            .map(execution =>
              execution.suspended match {
                case Some(request) =>
                  s"local=${execution.invocation.local.mkString("(", ",", ")")}:" +
                    s"${request.kind}@%${request.function}:${request.pc}/scope=${request.scope}"
                case None => s"local=${execution.invocation.local.mkString("(", ",", ")")}:running"
              }
            )
            .take(8)
            .mkString(", ")
          return Left(
            ExecutionFailure.InvalidRequest(
              s"SPIR-V collective participation diverged within a workgroup: $waiting"
            )
          )
        case Right(_) => ()
      }
    }
    Right(())
  }

  private def resolveCollectives(
      program: DispatchProgram,
      executions: mutable.ArrayBuffer[InvocationExecution]
  ): Either[ExecutionFailure, Int] = {
    var resolved  = 0
    val subgroups = executions.iterator.map(_.invocation.subgroupIndex).toSet.toArray.sorted
    var index     = 0
    while (index < subgroups.length) {
      val subgroup     = subgroups(index)
      val participants = Col.from(executions.iterator.filter(_.invocation.subgroupIndex == subgroup))
      val requests     = participants.flatMap(_.suspended)
      if (
        requests.nonEmpty && requests.head.scope == 3 &&
        participants.forall(execution =>
          !execution.completed && execution.suspended.exists(sameCollective(_, requests.head))
        )
      )
        resolveCollective(program, participants, requests.head) match {
          case Left(error) => return Left(error)
          case Right(())   => resolved += 1
        }
      index += 1
    }
    val requests = executions.flatMap(_.suspended)
    requests.find(_.scope == 2) match {
      case Some(request)
          if executions.forall(execution =>
            !execution.completed && execution.suspended.exists(sameCollective(_, request))
          ) =>
        resolveCollective(program, Col.from(executions), request) match {
          case Left(error) => return Left(error)
          case Right(())   => resolved += 1
        }
      case _ => ()
    }
    Right(resolved)
  }

  private def sameCollective(left: CollectiveRequest, right: CollectiveRequest): Boolean =
    left.kind == right.kind && left.function == right.function && left.pc == right.pc &&
      left.scope == right.scope && left.operation == right.operation && left.selector == right.selector &&
      ((left.payload, right.payload) match {
        case (CollectivePayload.Value(leftType, _, _), CollectivePayload.Value(rightType, _, _)) =>
          leftType == rightType
        case (
              CollectivePayload.Shuffle(leftType, _, _, _),
              CollectivePayload.Shuffle(rightType, _, _, _)
            ) =>
          leftType == rightType
        case (
              CollectivePayload.Barrier(leftMemoryScope, leftSemantics),
              CollectivePayload.Barrier(rightMemoryScope, rightSemantics)
            ) =>
          leftMemoryScope == rightMemoryScope && leftSemantics == rightSemantics
        case (
              CollectivePayload.ShuffleUp(leftType, _, _, _, leftDelta),
              CollectivePayload.ShuffleUp(rightType, _, _, _, rightDelta)
            ) =>
          leftType == rightType && leftDelta == rightDelta
        case (
              CollectivePayload.ShuffleDown(leftType, _, _, _, leftDelta),
              CollectivePayload.ShuffleDown(rightType, _, _, _, rightDelta)
            ) =>
          leftType == rightType && leftDelta == rightDelta
        case _ => false
      })

  private def resolveCollective(
      program: DispatchProgram,
      participants: Col[InvocationExecution],
      expected: CollectiveRequest
  ): Either[ExecutionFailure, Unit] = {
    val requests = participants.map(_.suspended.get)
    expected.payload match {
      case CollectivePayload.Barrier(_, _) =>
        participants.foreach { execution =>
          val request = execution.suspended.get
          execution.pc = request.pc + 1
          execution.suspended = None
        }
        Right(())
      case CollectivePayload.Shuffle(resultType, _, _, _) =>
        val shuffleRequests = requests.collect {
          case CollectiveRequest(_, _, _, _, CollectivePayload.Shuffle(_, destination, data, invocationId), _, _) =>
            (destination, data, invocationId)
        }
        if (shuffleRequests.size != requests.size)
          Left(ExecutionFailure.InvalidRequest("subgroup shuffle participants have incompatible payloads"))
        else {
          val results =
            participants.zip(shuffleRequests).foldLeft[Either[ExecutionFailure, Col[RuntimeValue]]](Right(Col.empty)) {
              case (Right(existing), request) =>
                val invocationId = request._2._3
                invocationId match {
                  case RuntimeValue.Bits(_, Array(sourceLocal)) =>
                    participants.find(_.invocation.subgroupLocal.toLong == sourceLocal) match {
                      case Some(sourceExecution) =>
                        sourceExecution.suspended match {
                          case Some(CollectiveRequest(_, _, _, _, CollectivePayload.Shuffle(_, _, data, _), _, _)) =>
                            Right(existing :+ retag(data, resultType))
                          case _ => Left(ExecutionFailure.InvalidRequest("subgroup shuffle source is not suspended"))
                        }
                      case None =>
                        Left(ExecutionFailure.InvalidRequest(s"subgroup shuffle source lane $sourceLocal is absent"))
                    }
                  case _ => Left(ExecutionFailure.InvalidRequest("subgroup shuffle invocation id must be scalar"))
                }
              case (Left(error), _) => Left(error)
            }
          results.map { values =>
            participants.zip(values).foreach { case (execution, value) =>
              execution.suspended.get.payload match {
                case CollectivePayload.Shuffle(requestType, destination, _, _) =>
                  execution.values.update(destination, retag(value, requestType))
                case _ => ()
              }
              execution.pc = execution.suspended.get.pc + 1
              execution.suspended = None
            }
          }
        }
      case CollectivePayload.ShuffleUp(resultType, _, _, _, delta) =>
        val shuffleRequests = requests.collect {
          case CollectiveRequest(
                _,
                _,
                _,
                _,
                CollectivePayload.ShuffleUp(_, destination, previous, current, requestDelta),
                _,
                _
              ) if requestDelta == delta =>
            (destination, previous, current)
        }
        if (shuffleRequests.size != requests.size)
          Left(ExecutionFailure.InvalidRequest("subgroup shuffle-up participants have incompatible payloads"))
        else {
          val results =
            participants.zip(shuffleRequests).foldLeft[Either[ExecutionFailure, Col[RuntimeValue]]](Right(Col.empty)) {
              case (Right(existing), (execution, _)) =>
                val unwrappedSource = execution.invocation.subgroupLocal.toLong - delta
                val usePrevious     = unwrappedSource < 0L
                val sourceLocal     =
                  if (usePrevious) unwrappedSource + execution.invocation.subgroupSize.toLong
                  else unwrappedSource
                val value = participants.find(_.invocation.subgroupLocal.toLong == sourceLocal) match {
                  case Some(sourceExecution) =>
                    sourceExecution.suspended match {
                      case Some(
                            CollectiveRequest(
                              _,
                              _,
                              _,
                              _,
                              CollectivePayload.ShuffleUp(_, _, previous, current, _),
                              _,
                              _
                            )
                          ) => Right(if (usePrevious) previous else current)
                      case _ => Left(ExecutionFailure.InvalidRequest("subgroup shuffle-up source is not suspended"))
                    }
                  case None =>
                    Left(ExecutionFailure.InvalidRequest(s"subgroup shuffle-up source lane $sourceLocal is absent"))
                }
                value.map(result => existing :+ retag(result, resultType))
              case (Left(error), _) => Left(error)
            }
          results.map { values =>
            participants.zip(values).foreach { case (execution, value) =>
              val request = execution.suspended.get
              request.payload match {
                case CollectivePayload.ShuffleUp(requestType, destination, _, _, _) =>
                  execution.values.update(destination, retag(value, requestType))
                case _ => ()
              }
              execution.pc = request.pc + 1
              execution.suspended = None
            }
          }
        }
      case CollectivePayload.ShuffleDown(resultType, _, _, _, delta) =>
        val shuffleRequests = requests.collect {
          case CollectiveRequest(
                _,
                _,
                _,
                _,
                CollectivePayload.ShuffleDown(_, destination, current, next, requestDelta),
                _,
                _
              ) if requestDelta == delta =>
            (destination, current, next)
        }
        if (shuffleRequests.size != requests.size)
          Left(ExecutionFailure.InvalidRequest("subgroup shuffle-down participants have incompatible payloads"))
        else {
          val results =
            participants.zip(shuffleRequests).foldLeft[Either[ExecutionFailure, Col[RuntimeValue]]](Right(Col.empty)) {
              case (Right(existing), (execution, _)) =>
                val unwrappedSource = execution.invocation.subgroupLocal.toLong + delta
                val useNext         = unwrappedSource >= execution.invocation.subgroupSize.toLong
                val sourceLocal = if (useNext) unwrappedSource - execution.invocation.subgroupSize else unwrappedSource
                val value       = participants.find(_.invocation.subgroupLocal.toLong == sourceLocal) match {
                  case Some(sourceExecution) =>
                    sourceExecution.suspended match {
                      case Some(CollectiveRequest(
                            _,
                            _,
                            _,
                            _,
                            CollectivePayload.ShuffleDown(_, _, current, next, _),
                            _,
                            _
                          )) => Right(if (useNext) next else current)
                      case _ => Left(ExecutionFailure.InvalidRequest("subgroup shuffle-down source is not suspended"))
                    }
                  case None =>
                    Left(ExecutionFailure.InvalidRequest(s"subgroup shuffle-down source lane $sourceLocal is absent"))
                }
                value.map(result => existing :+ retag(result, resultType))
              case (Left(error), _) => Left(error)
            }
          results.map { values =>
            participants.zip(values).foreach { case (execution, value) =>
              execution.suspended.get.payload match {
                case CollectivePayload.ShuffleDown(requestType, destination, _, _, _) =>
                  execution.values.update(destination, retag(value, requestType))
                case _ => ()
              }
              execution.pc = execution.suspended.get.pc + 1
              execution.suspended = None
            }
          }
        }
      case CollectivePayload.Value(resultType, _, _) =>
        val requestValues = requests.collect {
          case CollectiveRequest(_, _, _, _, CollectivePayload.Value(_, _, value), _, _) =>
            value
        }
        if (requestValues.size != requests.size)
          Left(ExecutionFailure.InvalidRequest("collective participants have incompatible payloads"))
        else {
          val results = expected.kind match {
            case CollectiveKind.Any =>
              val any = requestValues.exists {
                case RuntimeValue.Bits(_, Array(value)) => value != 0L
                case _                                  => false
              }
              Right(Col.fill(participants.size)(RuntimeValue.Bits(resultType, Array(if (any) 1L else 0L))))
            case CollectiveKind.All =>
              val all = requestValues.forall {
                case RuntimeValue.Bits(_, Array(value)) => value != 0L
                case _                                  => false
              }
              Right(Col.fill(participants.size)(RuntimeValue.Bits(resultType, Array(if (all) 1L else 0L))))
            case CollectiveKind.UnsignedMin | CollectiveKind.UnsignedMax | CollectiveKind.SignedMin |
                CollectiveKind.SignedMax =>
              val inputs = requestValues.collect { case RuntimeValue.Bits(_, lanes) => lanes }
              if (inputs.size != requests.size || inputs.isEmpty)
                Left(
                  ExecutionFailure.InvalidRequest(s"${expected.kind} participants do not all provide integer values")
                )
              else {
                val width   = integerShape(program, resultType).map(_._1).getOrElse(64)
                val signed  = expected.kind == CollectiveKind.SignedMin || expected.kind == CollectiveKind.SignedMax
                val maximum = expected.kind == CollectiveKind.UnsignedMax || expected.kind == CollectiveKind.SignedMax
                def extremum(left: Long, right: Long): Long = {
                  val comparison =
                    if (signed) java.lang.Long.compare(signExtend(left, width), signExtend(right, width))
                    else java.lang.Long.compareUnsigned(left, right)
                  if ((maximum && comparison >= 0) || (!maximum && comparison <= 0)) left else right
                }
                if (expected.operation == 0) {
                  val lanes = Array.tabulate(inputs.head.length) { lane =>
                    inputs.iterator.map(_(lane) & mask(width)).reduce(extremum)
                  }
                  Right(Col.fill(participants.size)(RuntimeValue.Bits(resultType, lanes)))
                } else {
                  val identity =
                    if (signed) {
                      if (maximum) 1L << (width - 1) else (1L << (width - 1)) - 1L
                    } else if (maximum) 0L
                    else mask(width)
                  val accumulated = Array.fill(inputs.head.length)(identity & mask(width))
                  val outputs     = inputs.map { input =>
                    val before = accumulated.clone()
                    var lane   = 0
                    while (lane < accumulated.length) {
                      accumulated(lane) = extremum(accumulated(lane), input(lane) & mask(width))
                      lane += 1
                    }
                    RuntimeValue.Bits(resultType, if (expected.operation == 1) accumulated.clone() else before)
                  }
                  Right(outputs)
                }
              }
            case CollectiveKind.IntegerAdd =>
              val inputs = requestValues.collect { case RuntimeValue.Bits(_, lanes) => lanes }
              if (inputs.size != requests.size || inputs.isEmpty)
                Left(ExecutionFailure.InvalidRequest("OpGroupIAdd participants do not all provide integer values"))
              else {
                val width = integerShape(program, resultType).map(_._1).getOrElse(64)
                if (expected.operation == 0) {
                  val reduced = Array.tabulate(inputs.head.length) { lane =>
                    inputs.iterator.foldLeft(0L)((sum, input) => (sum + input(lane)) & mask(width))
                  }
                  Right(Col.fill(participants.size)(RuntimeValue.Bits(resultType, reduced)))
                } else {
                  val accumulated = Array.fill(inputs.head.length)(0L)
                  val outputs     = inputs.map { input =>
                    val before = accumulated.clone()
                    var lane   = 0
                    while (lane < accumulated.length) {
                      accumulated(lane) = (accumulated(lane) + input(lane)) & mask(width)
                      lane += 1
                    }
                    RuntimeValue.Bits(
                      resultType,
                      if (expected.operation == 1) accumulated.clone() else before
                    )
                  }
                  Right(outputs)
                }
              }
            case CollectiveKind.IntegerMultiply =>
              val inputs = requestValues.collect { case RuntimeValue.Bits(_, lanes) => lanes }
              if (inputs.size != requests.size || inputs.isEmpty)
                Left(ExecutionFailure.InvalidRequest("OpGroupIMulKHR participants do not all provide integer values"))
              else {
                val width = integerShape(program, resultType).map(_._1).getOrElse(64)
                if (expected.operation == 0) {
                  val reduced = Array.tabulate(inputs.head.length) { lane =>
                    inputs.iterator.foldLeft(1L)((product, input) => (product * input(lane)) & mask(width))
                  }
                  Right(Col.fill(participants.size)(RuntimeValue.Bits(resultType, reduced)))
                } else {
                  val accumulated = Array.fill(inputs.head.length)(1L)
                  val outputs     = inputs.map { input =>
                    val before = accumulated.clone()
                    var lane   = 0
                    while (lane < accumulated.length) {
                      accumulated(lane) = (accumulated(lane) * input(lane)) & mask(width)
                      lane += 1
                    }
                    RuntimeValue.Bits(
                      resultType,
                      if (expected.operation == 1) accumulated.clone() else before
                    )
                  }
                  Right(outputs)
                }
              }
            case CollectiveKind.FloatingAdd =>
              val inputs = requestValues.collect { case RuntimeValue.Bits(_, lanes) => lanes }
              if (inputs.size != requests.size || inputs.isEmpty)
                Left(ExecutionFailure.InvalidRequest("OpGroupFAdd participants do not all provide floating values"))
              else {
                val width = floatingShape(program, resultType).map(_._1).getOrElse(64)
                if (expected.operation == 0) {
                  val reduced = Array.tabulate(inputs.head.length) { lane =>
                    inputs.iterator.foldLeft(0L)((sum, input) => floatingAddBits(width, sum, input(lane)))
                  }
                  Right(Col.fill(participants.size)(RuntimeValue.Bits(resultType, reduced)))
                } else {
                  val accumulated = Array.fill(inputs.head.length)(0L)
                  val outputs     = inputs.map { input =>
                    val before = accumulated.clone()
                    var lane   = 0
                    while (lane < accumulated.length) {
                      accumulated(lane) = floatingAddBits(width, accumulated(lane), input(lane))
                      lane += 1
                    }
                    RuntimeValue.Bits(
                      resultType,
                      if (expected.operation == 1) accumulated.clone() else before
                    )
                  }
                  Right(outputs)
                }
              }
            case CollectiveKind.FloatingMin | CollectiveKind.FloatingMax =>
              val inputs = requestValues.collect { case RuntimeValue.Bits(_, lanes) => lanes }
              if (inputs.size != requests.size || inputs.isEmpty)
                Left(ExecutionFailure.InvalidRequest(s"${expected.kind} participants do not all provide values"))
              else {
                val width                                   = floatingShape(program, resultType).map(_._1).getOrElse(64)
                val maximum                                 = expected.kind == CollectiveKind.FloatingMax
                def extremum(left: Long, right: Long): Long =
                  if (maximum) floatingMaximumBits(width, left, right)
                  else floatingMinimumBits(width, left, right)
                if (expected.operation == 0) {
                  val reduced = Array.tabulate(inputs.head.length) { lane =>
                    inputs.iterator.map(_(lane)).reduce(extremum)
                  }
                  Right(Col.fill(participants.size)(RuntimeValue.Bits(resultType, reduced)))
                } else {
                  val identity    = floatingInfinityBits(width, negative = maximum)
                  val accumulated = Array.fill(inputs.head.length)(identity)
                  val outputs     = inputs.map { input =>
                    val before = accumulated.clone()
                    var lane   = 0
                    while (lane < accumulated.length) {
                      accumulated(lane) = extremum(accumulated(lane), input(lane))
                      lane += 1
                    }
                    RuntimeValue.Bits(resultType, if (expected.operation == 1) accumulated.clone() else before)
                  }
                  Right(outputs)
                }
              }
            case CollectiveKind.FloatingMultiply =>
              val inputs = requestValues.collect { case RuntimeValue.Bits(_, lanes) => lanes }
              if (inputs.size != requests.size || inputs.isEmpty)
                Left(ExecutionFailure.InvalidRequest("OpGroupFMulKHR participants do not all provide floating values"))
              else {
                val width    = floatingShape(program, resultType).map(_._1).getOrElse(64)
                val identity = floatingOneBits(width)
                if (expected.operation == 0) {
                  val reduced = Array.tabulate(inputs.head.length) { lane =>
                    inputs.iterator.foldLeft(identity) { (product, input) =>
                      floatingMultiplyBits(width, product, input(lane))
                    }
                  }
                  Right(Col.fill(participants.size)(RuntimeValue.Bits(resultType, reduced)))
                } else {
                  val accumulated = Array.fill(inputs.head.length)(identity)
                  val outputs     = inputs.map { input =>
                    val before = accumulated.clone()
                    var lane   = 0
                    while (lane < accumulated.length) {
                      accumulated(lane) = floatingMultiplyBits(width, accumulated(lane), input(lane))
                      lane += 1
                    }
                    RuntimeValue.Bits(
                      resultType,
                      if (expected.operation == 1) accumulated.clone() else before
                    )
                  }
                  Right(outputs)
                }
              }
            case CollectiveKind.ShuffleUp =>
              Left(ExecutionFailure.InvalidRequest("subgroup shuffle-up payload was resolved as a value collective"))
            case CollectiveKind.ShuffleDown =>
              Left(ExecutionFailure.InvalidRequest("subgroup shuffle-down payload was resolved as a value collective"))
            case CollectiveKind.Shuffle =>
              Left(ExecutionFailure.InvalidRequest("subgroup shuffle payload was resolved as a value collective"))
            case CollectiveKind.Broadcast =>
              val selected = (expected.scope, expected.selector) match {
                case (3, Col(x)) =>
                  participants.indexWhere(_.invocation.subgroupLocal.toLong == x)
                case (_, Col(x)) =>
                  participants.indexWhere(_.invocation.localX == x)
                case (_, Col(x, y, z)) =>
                  participants.indexWhere(execution =>
                    execution.invocation.localX == x && execution.invocation.localY == y &&
                      execution.invocation.localZ == z
                  )
                case _ => -1
              }
              if (selected < 0)
                Left(
                  ExecutionFailure.InvalidRequest(
                    s"OpGroupBroadcast local id ${expected.selector.mkString("(", ",", ")")} is outside the workgroup"
                  )
                )
              else Right(Col.fill(participants.size)(requestValues(selected)))
            case CollectiveKind.Barrier =>
              Left(ExecutionFailure.InvalidRequest("barrier rendezvous unexpectedly carried a value"))
          }
          results.map { values =>
            participants.zip(values).foreach { case (execution, value) =>
              val request = execution.suspended.get
              request.payload match {
                case CollectivePayload.Value(requestType, destination, _) =>
                  execution.values.update(destination, retag(value, requestType))
                case CollectivePayload.Barrier(_, _)              => ()
                case CollectivePayload.Shuffle(_, _, _, _)        => ()
                case CollectivePayload.ShuffleUp(_, _, _, _, _)   => ()
                case CollectivePayload.ShuffleDown(_, _, _, _, _) => ()
              }
              execution.pc = request.pc + 1
              execution.suspended = None
            }
          }
        }
    }
  }

  private def controlBarrier(
      values: InvocationValues,
      function: Int,
      pc: Int,
      executionScope: Operand,
      memoryScope: Operand,
      semantics: Operand
  ): Either[ExecutionFailure, CollectiveRequest] =
    (scalar(values, executionScope), scalar(values, memoryScope), scalar(values, semantics)) match {
      case (Right(executionScope @ (2L | 3L)), Right(memoryScope @ (2L | 3L)), Right(memorySemantics))
          if (memorySemantics & ~0xffdeL) == 0L &&
            Col(0L, 2L, 4L, 8L, 16L).contains(memorySemantics & 0x1eL) =>
        Right(
          CollectiveRequest(
            CollectiveKind.Barrier,
            function,
            pc,
            executionScope.toInt,
            CollectivePayload.Barrier(memoryScope.toInt, memorySemantics)
          )
        )
      case (Right(scope), _, _) if scope != 2L && scope != 3L =>
        Left(ExecutionFailure.Unsupported(s"OpControlBarrier execution scope $scope is not Subgroup or Workgroup"))
      case (Right(_), Right(scope), _) if scope != 2L && scope != 3L =>
        Left(ExecutionFailure.Unsupported(s"OpControlBarrier memory scope $scope is not Subgroup or Workgroup"))
      case (Right(_), Right(_), Right(memorySemantics)) =>
        Left(
          ExecutionFailure.InvalidRequest(
            f"OpControlBarrier memory semantics 0x$memorySemantics%x are invalid or unsupported"
          )
        )
      case (_, _, Left(error)) => Left(error)
      case (Left(error), _, _) => Left(error)
      case (_, Left(error), _) => Left(error)
    }

  private def groupAsyncCopy(
      program: DispatchProgram,
      values: InvocationValues,
      memory: MemoryState,
      invocation: Invocation,
      resultType: Operand,
      resultDestination: Operand,
      executionScope: Operand,
      destinationId: Operand,
      sourceId: Operand,
      numElements: Operand,
      stride: Operand
  ): Either[ExecutionFailure, Unit] =
    for {
      scope        <- scalar(values, executionScope)
      destination  <- address(values, destinationId)
      source       <- address(values, sourceId)
      count        <- scalar(values, numElements)
      sourceStride <- scalar(values, stride)
      _            <- Either.cond(
        scope == 2L,
        (),
        ExecutionFailure.Unsupported(s"OpGroupAsyncCopy execution scope $scope is not Workgroup")
      )
      _ <- Either.cond(
        count >= 0L && sourceStride > 0L,
        (),
        ExecutionFailure.InvalidRequest(s"OpGroupAsyncCopy has invalid count/stride $count/$sourceStride")
      )
      _ <- {
        val elementSize = typeSize(program, destination.pointee)
        if (elementSize <= 0)
          Left(ExecutionFailure.InvalidRequest("OpGroupAsyncCopy element type has no data size"))
        else if (invocation.localX != 0L || invocation.localY != 0L || invocation.localZ != 0L) {
          values.update(id(resultDestination), RuntimeValue.Bits(id(resultType), Array(0L)))
          Right(())
        } else {
          try {
            val totalBytes = Math.toIntExact(Math.multiplyExact(count, elementSize.toLong))
            val sourceSpan =
              if (count == 0L) 0
              else Math.toIntExact(
                Math.addExact(
                  Math.multiplyExact(count - 1L, Math.multiplyExact(sourceStride, elementSize.toLong)),
                  elementSize.toLong
                )
              )
            memory.read(source, sourceSpan).flatMap { sourceBytes =>
              val packed =
                if (sourceStride == 1L) sourceBytes
                else {
                  val result = Array.ofDim[Byte](totalBytes)
                  var index  = 0L
                  while (index < count) {
                    val sourceOffset =
                      Math.toIntExact(Math.multiplyExact(index, Math.multiplyExact(sourceStride, elementSize.toLong)))
                    System.arraycopy(
                      sourceBytes,
                      sourceOffset,
                      result,
                      Math.toIntExact(index * elementSize.toLong),
                      elementSize
                    )
                    index += 1L
                  }
                  result
                }
              memory.write(destination, packed)
            }.map { _ =>
              values.update(id(resultDestination), RuntimeValue.Bits(id(resultType), Array(0L)))
            }
          } catch {
            case _: ArithmeticException | _: IllegalArgumentException =>
              Left(ExecutionFailure.InvalidRequest("OpGroupAsyncCopy byte range is too large"))
          }
        }
      }
    } yield ()

  private def collectiveAny(
      program: DispatchProgram,
      values: InvocationValues,
      function: Int,
      pc: Int,
      resultType: Operand,
      destination: Operand,
      executionScope: Operand,
      pred: Operand
  ): Either[ExecutionFailure, CollectiveRequest] = collectiveBoolean(
    program,
    values,
    function,
    pc,
    resultType,
    destination,
    executionScope,
    pred,
    CollectiveKind.Any,
    "OpGroupAny"
  )

  private def collectiveBoolean(
      program: DispatchProgram,
      values: InvocationValues,
      function: Int,
      pc: Int,
      resultType: Operand,
      destination: Operand,
      executionScope: Operand,
      pred: Operand,
      kind: CollectiveKind,
      opcode: String
  ): Either[ExecutionFailure, CollectiveRequest] =
    (scalar(values, executionScope), values.get(id(pred))) match {
      case (Right(scope @ (2L | 3L)), Some(value @ RuntimeValue.Bits(predType, Array(_))))
          if booleanLanes(program, id(resultType)).contains(1) && booleanLanes(program, predType).contains(1) =>
        Right(
          CollectiveRequest(
            kind,
            function,
            pc,
            scope.toInt,
            CollectivePayload.Value(id(resultType), id(destination), value)
          )
        )
      case (Right(scope), _) if scope != 2L && scope != 3L =>
        Left(ExecutionFailure.Unsupported(s"$opcode execution scope $scope is not Subgroup or Workgroup"))
      case (Right(_), Some(_)) =>
        Left(ExecutionFailure.InvalidRequest(s"$opcode requires scalar boolean predicate and result types"))
      case (Left(error), _) => Left(error)
      case (_, None)        => Left(undefined(pred))
    }

  private def collectiveIntegerExtremum(
      program: DispatchProgram,
      values: InvocationValues,
      function: Int,
      pc: Int,
      resultType: Operand,
      destination: Operand,
      executionScope: Operand,
      operation: Operand,
      source: Operand,
      kind: CollectiveKind,
      opcode: String
  ): Either[ExecutionFailure, CollectiveRequest] =
    (scalar(values, executionScope), values.get(id(source))) match {
      case (Right(scope @ (2L | 3L)), Some(value @ RuntimeValue.Bits(sourceType, lanes)))
          if Col(0L, 1L, 2L).contains(unsigned(operation)) &&
            integerShape(program, id(resultType)) == integerShape(program, sourceType) &&
            integerShape(program, sourceType).exists(_._2 == lanes.length) =>
        Right(
          CollectiveRequest(
            kind,
            function,
            pc,
            scope.toInt,
            CollectivePayload.Value(id(resultType), id(destination), value),
            unsigned(operation).toInt
          )
        )
      case (Right(_), _) if !Col(0L, 1L, 2L).contains(unsigned(operation)) =>
        Left(
          ExecutionFailure.Unsupported(
            s"$opcode group operation ${unsigned(operation)} is not Reduce, InclusiveScan, or ExclusiveScan"
          )
        )
      case (Right(scope), _) if scope != 2L && scope != 3L =>
        Left(ExecutionFailure.Unsupported(s"$opcode execution scope $scope is not Subgroup or Workgroup"))
      case (Right(_), Some(_)) =>
        Left(ExecutionFailure.InvalidRequest(s"$opcode requires matching integer scalar or vector types"))
      case (Left(error), _) => Left(error)
      case (_, None)        => Left(undefined(source))
    }

  private def collectiveIntegerAdd(
      program: DispatchProgram,
      values: InvocationValues,
      function: Int,
      pc: Int,
      resultType: Operand,
      destination: Operand,
      executionScope: Operand,
      operation: Operand,
      source: Operand
  ): Either[ExecutionFailure, CollectiveRequest] =
    (scalar(values, executionScope), values.get(id(source))) match {
      case (Right(scope @ (2L | 3L)), Some(value @ RuntimeValue.Bits(sourceType, lanes)))
          if Col(0L, 1L, 2L).contains(unsigned(operation)) &&
            integerShape(program, id(resultType)) == integerShape(program, sourceType) &&
            integerShape(program, sourceType).exists(_._2 == lanes.length) =>
        Right(
          CollectiveRequest(
            CollectiveKind.IntegerAdd,
            function,
            pc,
            scope.toInt,
            CollectivePayload.Value(id(resultType), id(destination), value),
            unsigned(operation).toInt
          )
        )
      case (Right(_), _) if !Col(0L, 1L, 2L).contains(unsigned(operation)) =>
        Left(
          ExecutionFailure.Unsupported(
            s"OpGroupIAdd group operation ${unsigned(operation)} is not Reduce, InclusiveScan, or ExclusiveScan"
          )
        )
      case (Right(scope), _) if scope != 2L && scope != 3L =>
        Left(ExecutionFailure.Unsupported(s"OpGroupIAdd execution scope $scope is not Subgroup or Workgroup"))
      case (Right(_), Some(_)) =>
        Left(ExecutionFailure.InvalidRequest("OpGroupIAdd requires matching integer scalar or vector types"))
      case (Left(error), _) => Left(error)
      case (_, None)        => Left(undefined(source))
    }

  private def collectiveIntegerMultiply(
      program: DispatchProgram,
      values: InvocationValues,
      function: Int,
      pc: Int,
      resultType: Operand,
      destination: Operand,
      executionScope: Operand,
      operation: Operand,
      source: Operand
  ): Either[ExecutionFailure, CollectiveRequest] =
    (scalar(values, executionScope), values.get(id(source))) match {
      case (Right(scope @ (2L | 3L)), Some(value @ RuntimeValue.Bits(sourceType, lanes)))
          if Col(0L, 1L, 2L).contains(unsigned(operation)) &&
            integerShape(program, id(resultType)) == integerShape(program, sourceType) &&
            integerShape(program, sourceType).exists(_._2 == lanes.length) =>
        Right(
          CollectiveRequest(
            CollectiveKind.IntegerMultiply,
            function,
            pc,
            scope.toInt,
            CollectivePayload.Value(id(resultType), id(destination), value),
            unsigned(operation).toInt
          )
        )
      case (Right(_), _) if !Col(0L, 1L, 2L).contains(unsigned(operation)) =>
        Left(
          ExecutionFailure.Unsupported(
            s"OpGroupIMulKHR group operation ${unsigned(operation)} is not Reduce, InclusiveScan, or ExclusiveScan"
          )
        )
      case (Right(scope), _) if scope != 2L && scope != 3L =>
        Left(ExecutionFailure.Unsupported(s"OpGroupIMulKHR execution scope $scope is not Subgroup or Workgroup"))
      case (Right(_), Some(_)) =>
        Left(ExecutionFailure.InvalidRequest("OpGroupIMulKHR requires matching integer scalar or vector types"))
      case (Left(error), _) => Left(error)
      case (_, None)        => Left(undefined(source))
    }

  private def collectiveFloatingAdd(
      program: DispatchProgram,
      values: InvocationValues,
      function: Int,
      pc: Int,
      resultType: Operand,
      destination: Operand,
      executionScope: Operand,
      operation: Operand,
      source: Operand
  ): Either[ExecutionFailure, CollectiveRequest] =
    (scalar(values, executionScope), values.get(id(source))) match {
      case (Right(scope @ (2L | 3L)), Some(value @ RuntimeValue.Bits(sourceType, lanes)))
          if Col(0L, 1L, 2L).contains(unsigned(operation)) &&
            floatingShape(program, id(resultType)) == floatingShape(program, sourceType) &&
            floatingShape(program, sourceType).exists(_._2 == lanes.length) =>
        Right(
          CollectiveRequest(
            CollectiveKind.FloatingAdd,
            function,
            pc,
            scope.toInt,
            CollectivePayload.Value(id(resultType), id(destination), value),
            unsigned(operation).toInt
          )
        )
      case (Right(_), _) if !Col(0L, 1L, 2L).contains(unsigned(operation)) =>
        Left(
          ExecutionFailure.Unsupported(
            s"OpGroupFAdd group operation ${unsigned(operation)} is not Reduce, InclusiveScan, or ExclusiveScan"
          )
        )
      case (Right(scope), _) if scope != 2L && scope != 3L =>
        Left(ExecutionFailure.Unsupported(s"OpGroupFAdd execution scope $scope is not Subgroup or Workgroup"))
      case (Right(_), Some(_)) =>
        Left(ExecutionFailure.InvalidRequest("OpGroupFAdd requires matching floating scalar or vector types"))
      case (Left(error), _) => Left(error)
      case (_, None)        => Left(undefined(source))
    }

  private def collectiveFloatingMultiply(
      program: DispatchProgram,
      values: InvocationValues,
      function: Int,
      pc: Int,
      resultType: Operand,
      destination: Operand,
      executionScope: Operand,
      operation: Operand,
      source: Operand
  ): Either[ExecutionFailure, CollectiveRequest] =
    (scalar(values, executionScope), values.get(id(source))) match {
      case (Right(scope @ (2L | 3L)), Some(value @ RuntimeValue.Bits(sourceType, lanes)))
          if Col(0L, 1L, 2L).contains(unsigned(operation)) &&
            floatingShape(program, id(resultType)) == floatingShape(program, sourceType) &&
            floatingShape(program, sourceType).exists(_._2 == lanes.length) =>
        Right(
          CollectiveRequest(
            CollectiveKind.FloatingMultiply,
            function,
            pc,
            scope.toInt,
            CollectivePayload.Value(id(resultType), id(destination), value),
            unsigned(operation).toInt
          )
        )
      case (Right(_), _) if !Col(0L, 1L, 2L).contains(unsigned(operation)) =>
        Left(
          ExecutionFailure.Unsupported(
            s"OpGroupFMulKHR group operation ${unsigned(operation)} is not Reduce, InclusiveScan, or ExclusiveScan"
          )
        )
      case (Right(scope), _) if scope != 2L && scope != 3L =>
        Left(ExecutionFailure.Unsupported(s"OpGroupFMulKHR execution scope $scope is not Subgroup or Workgroup"))
      case (Right(_), Some(_)) =>
        Left(ExecutionFailure.InvalidRequest("OpGroupFMulKHR requires matching floating scalar or vector types"))
      case (Left(error), _) => Left(error)
      case (_, None)        => Left(undefined(source))
    }

  private def collectiveFloatingExtremum(
      program: DispatchProgram,
      values: InvocationValues,
      function: Int,
      pc: Int,
      resultType: Operand,
      destination: Operand,
      executionScope: Operand,
      operation: Operand,
      source: Operand,
      kind: CollectiveKind,
      opcode: String
  ): Either[ExecutionFailure, CollectiveRequest] =
    (scalar(values, executionScope), values.get(id(source))) match {
      case (Right(scope @ (2L | 3L)), Some(value @ RuntimeValue.Bits(sourceType, lanes)))
          if Col(0L, 1L, 2L).contains(unsigned(operation)) &&
            floatingShape(program, id(resultType)) == floatingShape(program, sourceType) &&
            floatingShape(program, sourceType).exists(_._2 == lanes.length) =>
        Right(
          CollectiveRequest(
            kind,
            function,
            pc,
            scope.toInt,
            CollectivePayload.Value(id(resultType), id(destination), value),
            unsigned(operation).toInt
          )
        )
      case (Right(_), _) if !Col(0L, 1L, 2L).contains(unsigned(operation)) =>
        Left(
          ExecutionFailure.Unsupported(
            s"$opcode group operation ${unsigned(operation)} is not Reduce, InclusiveScan, or ExclusiveScan"
          )
        )
      case (Right(scope), _) if scope != 2L && scope != 3L =>
        Left(ExecutionFailure.Unsupported(s"$opcode execution scope $scope is not Subgroup or Workgroup"))
      case (Right(_), Some(_)) =>
        Left(ExecutionFailure.InvalidRequest(s"$opcode requires matching floating scalar or vector types"))
      case (Left(error), _) => Left(error)
      case (_, None)        => Left(undefined(source))
    }

  private def collectiveShuffleUp(
      values: InvocationValues,
      function: Int,
      pc: Int,
      resultType: Operand,
      destination: Operand,
      previous: Operand,
      current: Operand,
      delta: Operand
  ): Either[ExecutionFailure, CollectiveRequest] =
    (values.get(id(previous)), values.get(id(current)), scalar(values, delta)) match {
      case (
            Some(previousValue @ RuntimeValue.Bits(previousType, previousLanes)),
            Some(currentValue @ RuntimeValue.Bits(currentType, currentLanes)),
            Right(deltaValue)
          )
          if previousType == id(resultType) && currentType == id(resultType) &&
            previousLanes.length == currentLanes.length && deltaValue >= 0L =>
        Right(
          CollectiveRequest(
            CollectiveKind.ShuffleUp,
            function,
            pc,
            3,
            CollectivePayload.ShuffleUp(
              id(resultType),
              id(destination),
              previousValue,
              currentValue,
              deltaValue
            )
          )
        )
      case (Some(_), Some(_), Right(deltaValue)) if deltaValue < 0L =>
        Left(ExecutionFailure.InvalidRequest("OpSubgroupShuffleUpINTEL delta must be non-negative"))
      case (Some(_), Some(_), Right(_)) =>
        Left(ExecutionFailure.InvalidRequest("OpSubgroupShuffleUpINTEL requires matching result and operand types"))
      case (_, _, Left(error)) => Left(error)
      case (None, _, _)        => Left(undefined(previous))
      case (_, None, _)        => Left(undefined(current))
    }

  private def collectiveShuffle(
      values: InvocationValues,
      function: Int,
      pc: Int,
      resultType: Operand,
      destination: Operand,
      data: Operand,
      invocationId: Operand
  ): Either[ExecutionFailure, CollectiveRequest] =
    (values.get(id(data)), values.get(id(invocationId))) match {
      case (
            Some(dataValue @ RuntimeValue.Bits(dataType, dataLanes)),
            Some(invocationValue @ RuntimeValue.Bits(_, Array(_)))
          )
          if dataType == id(resultType) && dataLanes.nonEmpty =>
        Right(
          CollectiveRequest(
            CollectiveKind.Shuffle,
            function,
            pc,
            3,
            CollectivePayload.Shuffle(id(resultType), id(destination), dataValue, invocationValue)
          )
        )
      case (Some(_), Some(RuntimeValue.Bits(_, lanes))) if lanes.length != 1 =>
        Left(ExecutionFailure.InvalidRequest("OpSubgroupShuffleINTEL invocation id must be scalar"))
      case (Some(_), Some(_)) =>
        Left(ExecutionFailure.InvalidRequest("OpSubgroupShuffleINTEL requires matching data and result types"))
      case (None, _) => Left(undefined(data))
      case (_, None) => Left(undefined(invocationId))
    }

  private def collectiveShuffleDown(
      values: InvocationValues,
      function: Int,
      pc: Int,
      resultType: Operand,
      destination: Operand,
      current: Operand,
      next: Operand,
      delta: Operand
  ): Either[ExecutionFailure, CollectiveRequest] =
    (values.get(id(current)), values.get(id(next)), scalar(values, delta)) match {
      case (
            Some(currentValue @ RuntimeValue.Bits(currentType, currentLanes)),
            Some(nextValue @ RuntimeValue.Bits(nextType, nextLanes)),
            Right(deltaValue)
          )
          if currentType == id(resultType) && nextType == id(resultType) &&
            currentLanes.length == nextLanes.length && deltaValue >= 0L =>
        Right(
          CollectiveRequest(
            CollectiveKind.ShuffleDown,
            function,
            pc,
            3,
            CollectivePayload.ShuffleDown(
              id(resultType),
              id(destination),
              currentValue,
              nextValue,
              deltaValue
            )
          )
        )
      case (Some(_), Some(_), Right(deltaValue)) if deltaValue < 0L =>
        Left(ExecutionFailure.InvalidRequest("OpSubgroupShuffleDownINTEL delta must be non-negative"))
      case (Some(_), Some(_), Right(_)) =>
        Left(ExecutionFailure.InvalidRequest("OpSubgroupShuffleDownINTEL requires matching result and operand types"))
      case (_, _, Left(error)) => Left(error)
      case (None, _, _)        => Left(undefined(current))
      case (_, None, _)        => Left(undefined(next))
    }

  private def collectiveBroadcast(
      program: DispatchProgram,
      values: InvocationValues,
      function: Int,
      pc: Int,
      resultType: Operand,
      destination: Operand,
      executionScope: Operand,
      source: Operand,
      localId: Operand
  ): Either[ExecutionFailure, CollectiveRequest] =
    (scalar(values, executionScope), values.get(id(source)), values.get(id(localId))) match {
      case (
            Right(scope @ (2L | 3L)),
            Some(value @ RuntimeValue.Bits(sourceType, _)),
            Some(RuntimeValue.Bits(localIdType, lanes))
          )
          if sourceType == id(resultType) && integerShape(program, localIdType).exists { case (_, count) =>
            count == lanes.length && Col(1, 3).contains(count)
          } =>
        val width = integerShape(program, localIdType).map(_._1).getOrElse(64)
        Right(
          CollectiveRequest(
            CollectiveKind.Broadcast,
            function,
            pc,
            scope.toInt,
            CollectivePayload.Value(id(resultType), id(destination), value),
            selector = Col.from(lanes.map(_ & mask(width)))
          )
        )
      case (Right(scope), _, _) if scope != 2L && scope != 3L =>
        Left(ExecutionFailure.Unsupported(s"OpGroupBroadcast execution scope $scope is not Subgroup or Workgroup"))
      case (Right(_), None, _) => Left(undefined(source))
      case (Right(_), _, None) => Left(undefined(localId))
      case (Right(_), _, _)    =>
        Left(
          ExecutionFailure.InvalidRequest(
            "OpGroupBroadcast requires matching result/source bit types and a scalar or three-lane integer local id"
          )
        )
      case (Left(error), _, _) => Left(error)
    }

  private def bindArgument(
      program: DispatchProgram,
      parameterType: Int,
      argument: KernelArgument,
      shared: Option[Region],
      argumentIndex: Int,
      memory: MemoryState
  ): Either[ExecutionFailure, RuntimeValue] =
    program.types.get(parameterType) match {
      case Some(RuntimeType.Pointer(storageClass, pointee)) => argument match {
          case KernelArgument.Allocation(allocation, offset) =>
            pointerLeafOffsets(program, pointee, 0L) match {
              case Right(leaves) if leaves.isEmpty =>
                Right(RuntimeValue.Address(
                  parameterType,
                  Pointer(Region.External(allocation), offset, pointee, storageClass)
                ))
              case Right(_) => bindExternalAggregatePointerArgument(
                  program,
                  parameterType,
                  storageClass,
                  pointee,
                  allocation,
                  offset,
                  memory
                )
              case Left(error) => Left(error)
            }
          case KernelArgument.Scalar(bytes)
              if bytes.toArray.forall(_ == 0) &&
                !program.types.get(pointee).exists {
                  case RuntimeType.Structure(_) | RuntimeType.Array(_, _) => true
                  case _                                                  => false
                } =>
            Right(RuntimeValue.Address(parameterType, Pointer(Region.Null, 0L, pointee, storageClass)))
          case KernelArgument.Scalar(bytes)
              if memory.argumentRelocation(argumentIndex, 0L).isDefined &&
                (pointerLeafOffsets(program, pointee, 0L).toOption.exists(_.isEmpty) ||
                  typeSize(program, pointee) > bytes.size) =>
            memory.argumentRelocation(argumentIndex, 0L) match {
              case Some((target, targetOffset)) =>
                Right(
                  RuntimeValue.Address(
                    parameterType,
                    Pointer(Region.External(target), targetOffset, pointee, storageClass)
                  )
                )
              case None => Left(ExecutionFailure.InvalidRequest("missing SVM pointer relocation"))
            }
          case KernelArgument.Scalar(bytes) =>
            memory.relocateArgument(argumentIndex, bytes).map { relocated =>
              val size   = math.max(typeSize(program, pointee), relocated.length)
              val buffer = Array.ofDim[Byte](size)
              System.arraycopy(relocated, 0, buffer, 0, relocated.length)
              val slots = mutable.LongMap.empty[(Int, Pointer)]
              pointerLeafOffsets(program, pointee, 0L) match {
                case Right(leaves) => leaves.foreach { case (offset, pointerType) =>
                    program.types.get(pointerType) match {
                      case Some(RuntimeType.Pointer(pointerStorage, pointerPointee)) =>
                        val pointer = memory.argumentRelocation(argumentIndex, offset) match {
                          case Some((target, targetOffset)) =>
                            Pointer(Region.External(target), targetOffset, pointerPointee, pointerStorage)
                          case None => Pointer(Region.Null, 0L, pointerPointee, pointerStorage)
                        }
                        slots.update(offset, pointerType -> pointer)
                      case _ => ()
                    }
                  }
                case Left(_) => ()
              }
              RuntimeValue.Address(
                parameterType,
                Pointer(Region.Internal(buffer), 0L, pointee, storageClass, Option.when(slots.nonEmpty)(slots))
              )
            }
          case KernelArgument.Local(size) =>
            val region = shared.getOrElse(Region.Internal(Array.ofDim[Byte](checkedSize(size))))
            Right(RuntimeValue.Address(parameterType, Pointer(region, 0L, pointee, storageClass)))
        }
      case Some(_) => argument match {
          case KernelArgument.Scalar(bytes) =>
            memory.relocateArgument(argumentIndex, bytes).map(decoded => decode(program, parameterType, decoded))
          case value => value match {
              case KernelArgument.Local(size) =>
                // Some DPC++ Level Zero kernels lower a local pointer parameter to a
                // 64-bit physical address and convert it back with OpConvertUToPtr.
                // Register the per-workgroup region in the same virtual address map
                // used by ordinary pointer conversions.
                val region = shared.getOrElse(Region.Internal(Array.ofDim[Byte](checkedSize(size))))
                memory.pointerInteger(Pointer(region, 0L, 0, 4)).map(address =>
                  RuntimeValue.Bits(parameterType, Array(address))
                )
              case other => Left(ExecutionFailure.InvalidRequest(s"scalar SPIR-V parameter cannot bind $other"))
            }
        }
      case None => Left(ExecutionFailure.InvalidRequest(s"SPIR-V parameter type %$parameterType is undefined"))
    }

  private def bindExternalAggregatePointerArgument(
      program: DispatchProgram,
      parameterType: Int,
      storageClass: Int,
      pointee: Int,
      allocation: AllocationId,
      allocationOffset: Long,
      memory: MemoryState
  ): Either[ExecutionFailure, RuntimeValue] =
    pointerLeafOffsets(program, pointee, 0L) match {
      case Left(error)   => Left(error)
      case Right(leaves) =>
        val slots = mutable.LongMap.empty[(Int, Pointer)]
        leaves.foreach { case (slotOffset, pointerType) =>
          program.types.get(pointerType) match {
            case Some(RuntimeType.Pointer(pointerStorage, pointerPointee)) =>
              val pointer = memory.allocationRelocation(allocation, allocationOffset + slotOffset) match {
                case Some((target, targetOffset)) =>
                  Pointer(Region.External(target), targetOffset, pointerPointee, pointerStorage)
                case None => Pointer(Region.Null, 0L, pointerPointee, pointerStorage)
              }
              slots.update(allocationOffset + slotOffset, pointerType -> pointer)
            case _ => ()
          }
        }
        Right(
          RuntimeValue.Address(
            parameterType,
            Pointer(
              Region.External(allocation),
              allocationOffset,
              pointee,
              storageClass,
              Option.when(slots.nonEmpty)(slots)
            )
          )
        )
    }

  private[v1_6] def pointerLeafOffsetsForTest(
      module: Module,
      typeId: Int
  ): Either[ExecutionFailure, Col[(Long, Int)]] =
    prepare(module).flatMap(program => pointerLeafOffsets(program.value, typeId, 0L))

  private def pointerLeafOffsets(
      program: DispatchProgram,
      typeId: Int,
      baseOffset: Long
  ): Either[ExecutionFailure, Col[(Long, Int)]] = program.types.get(typeId) match {
    case Some(RuntimeType.Pointer(_, _))      => Right(Col(baseOffset -> typeId))
    case Some(RuntimeType.Structure(members)) =>
      members.zipWithIndex.foldLeft[Either[ExecutionFailure, Col[(Long, Int)]]](Right(Col.empty)) {
        case (result, (member, index)) =>
          result.flatMap(existing =>
            pointerLeafOffsets(program, member, baseOffset + structMemberOffset(program, typeId, members, index)).map(
              existing ++ _
            )
          )
      }
    case Some(RuntimeType.Array(element, count)) =>
      if (count < 0L || count > Int.MaxValue.toLong)
        Left(ExecutionFailure.InvalidRequest(s"SPIR-V aggregate pointer array type %$typeId has invalid size $count"))
      else
        (0 until count.toInt).foldLeft[Either[ExecutionFailure, Col[(Long, Int)]]](Right(Col.empty)) {
          case (result, index) =>
            result.flatMap { existing =>
              val stride = program.arrayStrides.getOrElse(typeId, typeSize(program, element))
              pointerLeafOffsets(program, element, baseOffset + index.toLong * stride).map(existing ++ _)
            }
        }
    case Some(_) => Right(Col.empty)
    case None    => Left(ExecutionFailure.InvalidRequest(s"SPIR-V aggregate member type %$typeId is undefined"))
  }

  private def loadPointer(
      program: DispatchProgram,
      memory: MemoryState,
      source: Pointer,
      resultType: Int
  ): Either[ExecutionFailure, RuntimeValue] = program.types.get(resultType) match {
    case Some(RuntimeType.Pointer(resultStorage, resultPointee)) =>
      source.slots.flatMap(_.get(source.offset)).orElse(
        memory.relocatedPointer(source, resultPointee, resultStorage).map(resultType -> _)
      ) match {
        case Some((slotType, pointer)) => program.types.get(slotType) match {
            case Some(RuntimeType.Pointer(slotStorage, _))
                if pointer.region == Region.Null ||
                  ((slotStorage == 8 || pointer.storageClass == slotStorage) &&
                    (resultStorage == 8 || pointer.storageClass == resultStorage)) =>
              // A pointer-valued aggregate may be accessed through an OpBitcast
              // (common in oneDPL's iterator wrappers). The slot records the
              // pointer's original static type, while the load supplies the
              // bitcast destination type. Preserve the address and retag the
              // pointer for the destination; Generic is compatible with any
              // concrete storage class.
              val result = pointer.copy(pointee = resultPointee, storageClass = resultStorage)
              Right(RuntimeValue.Address(resultType, result))
            case value =>
              Left(
                ExecutionFailure.InvalidRequest(
                  s"SPIR-V pointer slot at byte ${source.offset} has type %$slotType ($value), " +
                    s"expected %$resultType (${program.types.get(resultType)}), " +
                    s"base pointee %${source.pointee}, storage ${source.storageClass}, " +
                    s"slots ${source.slots.map(slots => Col.from(slots.keys).sorted.mkString("[", ",", "]")).getOrElse("none")}"
                )
              )
          }
        case None =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"SPIR-V pointer load at byte ${source.offset} does not reference a typed pointer slot"
            )
          )
      }
    case value =>
      Left(ExecutionFailure.InvalidRequest(s"SPIR-V pointer load result type %$resultType is invalid: $value"))
  }

  private def storePointer(
      program: DispatchProgram,
      memory: MemoryState,
      target: Pointer,
      sourceType: Int,
      source: Pointer
  ): Either[ExecutionFailure, Unit] =
    (program.types.get(target.pointee), program.types.get(sourceType), target.slots) match {
      case (
            Some(RuntimeType.Pointer(targetStorage, targetPointee)),
            Some(RuntimeType.Pointer(sourceStorage, sourcePointee)),
            Some(slots)
          )
          if target.pointee == sourceType && targetStorage == sourceStorage && targetPointee == sourcePointee &&
            source.pointee == sourcePointee &&
            (source.region == Region.Null || sourceStorage == 8 || source.storageClass == sourceStorage) =>
        (target.region, source.region) match {
          case (Region.External(_), Region.Internal(_)) =>
            Left(ExecutionFailure.Unsupported("SPIR-V cannot persist a pointer to dispatch-local storage"))
          case _ =>
            for {
              address <- memory.pointerInteger(source)
              _       <- memory.writeScalar(target, address, 8)
            } yield {
              slots.update(target.offset, sourceType -> source)
              memory.recordPointerRelocation(source, target)
            }
        }
      case (Some(RuntimeType.Pointer(_, _)), Some(RuntimeType.Pointer(_, _)), None) =>
        Left(
          ExecutionFailure.InvalidRequest(
            s"SPIR-V pointer store at byte ${target.offset} does not reference a typed pointer slot"
          )
        )
      case (targetType, sourceValue, _) =>
        Left(
          ExecutionFailure.InvalidRequest(
            s"SPIR-V pointer store type mismatch: target pointee %${target.pointee} is $targetType, " +
              s"source %$sourceType is $sourceValue"
          )
        )
    }

  private def materializeGlobals(
      program: DispatchProgram,
      includeStorageClass: Int => Boolean
  ): Either[ExecutionFailure, Map[Int, RuntimeValue]] = {
    val values  = Map.newBuilder[Int, RuntimeValue]
    val globals = program.globals.iterator
    while (globals.hasNext) {
      val (variable, global) = globals.next()
      if (!program.builtins.contains(variable) && includeStorageClass(global.storageClass)) {
        val pointee = pointerPointee(program, global.pointerType)
        val bytes   = global.initializer match {
          case None              => Array.ofDim[Byte](typeSize(program, pointee))
          case Some(initializer) => program.constants.get(initializer) match {
              case None =>
                return Left(
                  ExecutionFailure.InvalidRequest(
                    s"SPIR-V global variable %$variable has undefined initializer %$initializer"
                  )
                )
              case Some(value) =>
                val encoded = encode(program, value)
                if (encoded.length != typeSize(program, pointee))
                  return Left(
                    ExecutionFailure.InvalidRequest(
                      s"SPIR-V global variable %$variable initializer has ${encoded.length} bytes, " +
                        s"expected ${typeSize(program, pointee)}"
                    )
                  )
                encoded
            }
        }
        values += variable -> RuntimeValue.Address(
          global.pointerType,
          Pointer(Region.Internal(bytes), 0L, pointee, global.storageClass)
        )
      }
    }
    Right(values.result())
  }

  private def accessChain(
      program: DispatchProgram,
      values: InvocationValues,
      memory: MemoryState,
      resultType: Operand,
      destination: Operand,
      base: Operand,
      element: Option[Operand],
      indexes: Col[Operand]
  ): Either[ExecutionFailure, Unit] = {
    val source = address(values, base) match {
      case Left(error)  => return Left(error)
      case Right(value) => value
    }
    val initial = element match {
      case Some(value) =>
        scalar(values, value).map(index => (memory.elementOffset(program, source, index), source.pointee))
      case None => Right((source.offset, source.pointee))
    }
    val (baseOffset, baseType) = initial match {
      case Left(error)  => return Left(error)
      case Right(value) => value
    }
    var offset   = baseOffset
    var typeId   = baseType
    var position = 0
    while (position < indexes.size) {
      val index = scalar(values, indexes(position)) match {
        case Left(error)  => return Left(error)
        case Right(value) => value
      }
      program.types.get(typeId) match {
        case Some(RuntimeType.Structure(members)) =>
          if (index < 0L || index >= members.size.toLong)
            return Left(ExecutionFailure.InvalidRequest(s"structure member index $index is out of range"))
          offset += structMemberOffset(program, typeId, members, index.toInt)
          typeId = members(index.toInt)
        case Some(RuntimeType.Array(member, _)) =>
          offset += index * program.arrayStrides.getOrElse(typeId, typeSize(program, member))
          typeId = member
        case Some(RuntimeType.VectorType(component, _)) =>
          offset += index * typeSize(program, component)
          typeId = component
        case Some(value) => return Left(ExecutionFailure.InvalidRequest(s"cannot index through SPIR-V type $value"))
        case None        => return Left(ExecutionFailure.InvalidRequest(s"SPIR-V type %$typeId is undefined"))
      }
      position += 1
    }
    val outputType = id(resultType)
    val pointee    = pointerPointee(program, outputType)
    values.update(
      id(destination),
      RuntimeValue.Address(outputType, Pointer(source.region, offset, pointee, source.storageClass, source.slots))
    )
    Right(())
  }

  private def genericCastToPointer(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand
  ): Either[ExecutionFailure, Unit] = {
    val outputType = id(resultType)
    (program.types.get(outputType), values.get(id(source))) match {
      case (
            Some(RuntimeType.Pointer(targetStorage, targetPointee)),
            Some(RuntimeValue.Address(sourceType, pointer))
          ) if Col(4, 5, 7).contains(targetStorage) =>
        program.types.get(sourceType) match {
          case Some(RuntimeType.Pointer(8, sourcePointee))
              if sourcePointee == targetPointee && pointer.pointee == targetPointee =>
            val converted = pointer.region match {
              case Region.Null                                => Pointer(Region.Null, 0L, targetPointee, targetStorage)
              case _ if pointer.storageClass == targetStorage => pointer
              case _                                          => Pointer(Region.Null, 0L, targetPointee, targetStorage)
            }
            values.update(id(destination), RuntimeValue.Address(outputType, converted))
            Right(())
          case Some(RuntimeType.Pointer(sourceStorage, sourcePointee)) =>
            Left(
              ExecutionFailure.InvalidRequest(
                s"OpGenericCastToPtr cannot cast storage class $sourceStorage pointee %$sourcePointee " +
                  s"to storage class $targetStorage pointee %$targetPointee"
              )
            )
          case _ => Left(ExecutionFailure.InvalidRequest("OpGenericCastToPtr source type is not a pointer"))
        }
      case (Some(RuntimeType.Pointer(targetStorage, _)), _) =>
        Left(
          ExecutionFailure.InvalidRequest(
            s"OpGenericCastToPtr result pointer has invalid storage class $targetStorage"
          )
        )
      case (None, _) =>
        Left(ExecutionFailure.InvalidRequest(s"OpGenericCastToPtr result type %$outputType is undefined"))
      case (_, None) => Left(undefined(source))
      case _         => Left(ExecutionFailure.InvalidRequest("OpGenericCastToPtr source is not a pointer"))
    }
  }

  private def genericCastToPointerExplicit(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand,
      storage: Operand
  ): Either[ExecutionFailure, Unit] = {
    val outputType    = id(resultType)
    val targetStorage = unsigned(storage).toInt
    (program.types.get(outputType), values.get(id(source))) match {
      case (
            Some(RuntimeType.Pointer(resultStorage, targetPointee)),
            Some(RuntimeValue.Address(sourceType, pointer))
          ) if resultStorage == targetStorage && Col(4, 5, 7).contains(targetStorage) =>
        program.types.get(sourceType) match {
          case Some(RuntimeType.Pointer(8, sourcePointee))
              if sourcePointee == targetPointee && pointer.pointee == targetPointee =>
            val converted =
              if (pointer.region == Region.Null || pointer.storageClass == targetStorage)
                pointer.copy(pointee = targetPointee, storageClass = targetStorage)
              else Pointer(Region.Null, 0L, targetPointee, targetStorage)
            values.update(id(destination), RuntimeValue.Address(outputType, converted))
            Right(())
          case Some(RuntimeType.Pointer(sourceStorage, sourcePointee)) =>
            Left(
              ExecutionFailure.InvalidRequest(
                s"OpGenericCastToPtrExplicit cannot cast storage class $sourceStorage pointee %$sourcePointee"
              )
            )
          case _ => Left(ExecutionFailure.InvalidRequest("OpGenericCastToPtrExplicit source is not a pointer"))
        }
      case (Some(RuntimeType.Pointer(resultStorage, _)), _)
          if resultStorage != targetStorage || !Col(4, 5, 7).contains(targetStorage) =>
        Left(
          ExecutionFailure.InvalidRequest(
            s"OpGenericCastToPtrExplicit requires Workgroup, CrossWorkgroup, or Function storage matching result type; " +
              s"got storage $targetStorage and result storage $resultStorage"
          )
        )
      case (Some(RuntimeType.Pointer(_, _)), None) => Left(undefined(source))
      case (None, _)                               =>
        Left(ExecutionFailure.InvalidRequest(s"OpGenericCastToPtrExplicit result type %$outputType is undefined"))
      case _ => Left(ExecutionFailure.InvalidRequest("OpGenericCastToPtrExplicit source is not a pointer"))
    }
  }

  private def compositeLeafRange(
      program: DispatchProgram,
      typeId: Int,
      indexes: Col[Operand]
  ): Either[ExecutionFailure, (Int, Int)] = {
    var current = typeId
    var offset  = 0
    var index   = 0
    while (index < indexes.size) {
      val selected = unsigned(indexes(index))
      program.types.get(current) match {
        case Some(RuntimeType.VectorType(component, count)) if selected < count.toLong =>
          scalarLeafCount(program, component) match {
            case Left(error)   => return Left(error)
            case Right(leaves) => offset += Math.toIntExact(selected * leaves.toLong)
          }
          current = component
        case Some(RuntimeType.Array(element, count)) if selected < count =>
          scalarLeafCount(program, element) match {
            case Left(error)   => return Left(error)
            case Right(leaves) => offset += Math.toIntExact(selected * leaves.toLong)
          }
          current = element
        case Some(RuntimeType.Structure(members)) if selected < members.size.toLong =>
          var member = 0
          while (member < selected.toInt) {
            scalarLeafCount(program, members(member)) match {
              case Left(error)   => return Left(error)
              case Right(leaves) => offset = Math.addExact(offset, leaves)
            }
            member += 1
          }
          current = members(selected.toInt)
        case Some(value) =>
          return Left(
            ExecutionFailure.InvalidRequest(
              s"OpComposite index $selected is invalid for type %$current ($value)"
            )
          )
        case None => return Left(ExecutionFailure.InvalidRequest(s"OpComposite type %$current is undefined"))
      }
      index += 1
    }
    scalarLeafCount(program, current).map(leaves => offset -> leaves)
  }

  private def compositeExtract(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      composite: Operand,
      indexes: Col[Operand]
  ): Either[ExecutionFailure, Unit] = values.get(id(composite)) match {
    case Some(RuntimeValue.Bits(compositeType, lanes)) =>
      compositeLeafRange(program, compositeType, indexes) match {
        case Right((offset, count)) if offset >= 0 && offset + count <= lanes.length =>
          values.update(id(destination), RuntimeValue.Bits(id(resultType), lanes.slice(offset, offset + count)))
          Right(())
        case Right((offset, count)) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"OpCompositeExtract range $offset+$count exceeds ${lanes.length} scalar leaves"
            )
          )
        case Left(error) => Left(error)
      }
    case Some(_) => Left(ExecutionFailure.Unsupported("composite extraction requires scalar data"))
    case None    => Left(undefined(composite))
  }

  private def compositeInsert(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand,
      composite: Operand,
      indexes: Col[Operand]
  ): Either[ExecutionFailure, Unit] =
    (values.get(id(source)), values.get(id(composite))) match {
      case (Some(RuntimeValue.Bits(_, sourceLanes)), Some(RuntimeValue.Bits(compositeType, compositeLanes))) =>
        compositeLeafRange(program, compositeType, indexes) match {
          case Right((offset, count)) if sourceLanes.length == count && offset + count <= compositeLanes.length =>
            val result = compositeLanes.clone()
            System.arraycopy(sourceLanes, 0, result, offset, count)
            values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
            Right(())
          case Right((offset, count)) =>
            Left(
              ExecutionFailure.InvalidRequest(
                s"OpCompositeInsert source has ${sourceLanes.length} leaves, expected $count at offset $offset"
              )
            )
          case Left(error) => Left(error)
        }
      case (None, _) => Left(undefined(source))
      case (_, None) => Left(undefined(composite))
      case _         => Left(ExecutionFailure.Unsupported("composite insertion requires scalar data"))
    }

  private def nextBinary16(bits: Long, upward: Boolean): Long = {
    val value = bits.toInt & 0xffff
    if ((value & 0x7fff) > 0x7c00) value.toLong
    else if (upward) {
      if (value == 0x7c00) value.toLong
      else if ((value & 0x8000) != 0) (value - 1).toLong & 0xffffL
      else (value + 1).toLong & 0xffffL
    } else {
      if (value == 0xfc00) value.toLong
      else if ((value & 0x8000) != 0) (value + 1).toLong & 0xffffL
      else if (value == 0) 0x8001L
      else (value - 1).toLong
    }
  }

  private def doubleToBinary16(value: Double, mode: Int): Long = {
    val approximate = IEEE754.floatToBinary16(value.toFloat)
    if (value.isNaN || value.isInfinite) approximate
    else {
      val approximateValue = IEEE754.binary16ToFloat(approximate).toDouble
      if (approximateValue == value) approximate
      else {
        val (lower, upper) =
          if (approximateValue < value) approximate      -> nextBinary16(approximate, upward = true)
          else nextBinary16(approximate, upward = false) -> approximate
        mode match {
          case 1 => if (value < 0.0) upper else lower
          case 2 => upper
          case 3 => lower
          case _ =>
            val lowerDistance = value - IEEE754.binary16ToFloat(lower).toDouble
            val upperDistance = IEEE754.binary16ToFloat(upper).toDouble - value
            if (lowerDistance < upperDistance) lower
            else if (upperDistance < lowerDistance) upper
            else if ((lower & 1L) == 0L) lower else upper
        }
      }
    }
  }

  private def openCLHalfLoad(
      program: DispatchProgram,
      values: InvocationValues,
      memory: MemoryState,
      resultType: Operand,
      destination: Operand,
      sources: Col[Operand],
      aligned: Boolean
  ): Either[ExecutionFailure, Unit] = {
    val vector = sources.size == 3
    if ((!vector && sources.size != 2) || (vector && sources.size != 3))
      return Left(ExecutionFailure.InvalidRequest("OpenCL.std half load has an invalid operand count"))
    (floatingShape(program, id(resultType)), scalar(values, sources(0)), address(values, sources(1))) match {
      case (Some((32, count)), Right(offset), Right(pointer))
          if !vector || unsigned(sources(2)) == count.toLong =>
        val stride = if (aligned && count == 3) 4 else count
        val base   = Math.addExact(pointer.offset, Math.multiplyExact(offset, stride.toLong * 2L))
        val result = Array.ofDim[Long](count)
        var lane   = 0
        while (lane < count) {
          memory.readScalar(pointer.copy(offset = Math.addExact(base, lane.toLong * 2L)), 2) match {
            case Left(error) => return Left(error)
            case Right(half) => result(lane) = IEEE754.floatToBinary32(IEEE754.binary16ToFloat(half))
          }
          lane += 1
        }
        values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
        Right(())
      case (Some(_), Left(error), _) => Left(error)
      case (Some(_), _, Left(error)) => Left(error)
      case _ => Left(ExecutionFailure.InvalidRequest("OpenCL.std half load requires a float result and half pointer"))
    }
  }

  private def openCLHalfStore(
      program: DispatchProgram,
      values: InvocationValues,
      memory: MemoryState,
      sources: Col[Operand],
      aligned: Boolean,
      rounded: Boolean
  ): Either[ExecutionFailure, Unit] = {
    val expected = if (rounded) 4 else 3
    if (sources.size != expected)
      return Left(ExecutionFailure.InvalidRequest(s"OpenCL.std half store expects $expected operands"))
    (values.get(id(sources(0))), scalar(values, sources(1)), address(values, sources(2))) match {
      case (Some(RuntimeValue.Bits(sourceType, lanes)), Right(offset), Right(pointer)) =>
        floatingShape(program, sourceType) match {
          case Some((width, count)) if width == 32 || width == 64 =>
            val stride = if (aligned && count == 3) 4 else count
            val base   = Math.addExact(pointer.offset, Math.multiplyExact(offset, stride.toLong * 2L))
            val mode   = if (rounded) unsigned(sources(3)).toInt else 0
            var lane   = 0
            while (lane < count) {
              val source = floatingToDouble(width, lanes(lane))
              memory.writeScalar(
                pointer.copy(offset = Math.addExact(base, lane.toLong * 2L)),
                doubleToBinary16(source, mode),
                2
              ) match {
                case Left(error) => return Left(error)
                case Right(())   => ()
              }
              lane += 1
            }
            Right(())
          case _ => Left(ExecutionFailure.InvalidRequest("OpenCL.std half store source is not float or double"))
        }
      case (None, _, _)              => Left(undefined(sources(0)))
      case (Some(_), Left(error), _) => Left(error)
      case (Some(_), _, Left(error)) => Left(error)
      case _ => Left(ExecutionFailure.InvalidRequest("OpenCL.std half store operands are not value types"))
    }
  }

  private def openCLVectorLoad(
      program: DispatchProgram,
      values: InvocationValues,
      memory: MemoryState,
      resultType: Operand,
      destination: Operand,
      sources: Col[Operand]
  ): Either[ExecutionFailure, Unit] =
    if (sources.size != 3)
      Left(ExecutionFailure.InvalidRequest(s"OpenCL.std vloadn expects 3 operands, received ${sources.size}"))
    else
      (program.types.get(id(resultType)), scalar(values, sources(0)), address(values, sources(1))) match {
        case (_, Left(error), _) => Left(error)
        case (_, _, Left(error)) => Left(error)
        case (Some(RuntimeType.VectorType(component, count)), Right(offset), Right(pointer))
            if unsigned(sources(2)) == count.toLong =>
          val elementSize = typeSize(program, component)
          val base        = Math.addExact(
            pointer.offset,
            Math.multiplyExact(Math.multiplyExact(offset, count.toLong), elementSize.toLong)
          )
          val lanes = Array.ofDim[Long](count)
          var index = 0
          while (index < count) {
            memory.readScalar(
              pointer.copy(
                offset = Math.addExact(base, Math.multiplyExact(index.toLong, elementSize.toLong)),
                pointee = component
              ),
              elementSize
            ) match {
              case Left(error)  => return Left(error)
              case Right(value) => lanes(index) = value
            }
            index += 1
          }
          values.update(id(destination), RuntimeValue.Bits(id(resultType), lanes))
          Right(())
        case (Some(RuntimeType.VectorType(_, count)), Right(_), Right(_)) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"OpenCL.std vloadn literal vector width ${unsigned(sources(2))} does not match $count"
            )
          )
        case (Some(_), _, _) =>
          Left(ExecutionFailure.InvalidRequest("OpenCL.std vloadn result type is not a vector"))
        case (None, _, _) =>
          Left(ExecutionFailure.InvalidRequest(s"OpenCL.std vloadn result type %${id(resultType)} is undefined"))
      }

  private def openCLVectorStore(
      program: DispatchProgram,
      values: InvocationValues,
      memory: MemoryState,
      source: Operand,
      sources: Col[Operand]
  ): Either[ExecutionFailure, Unit] =
    if (sources.size != 2)
      Left(ExecutionFailure.InvalidRequest(s"OpenCL.std vstoren expects 2 operands, received ${sources.size}"))
    else
      (values.get(id(source)), scalar(values, sources(0)), address(values, sources(1))) match {
        case (Some(RuntimeValue.Bits(sourceType, lanes)), Right(offset), Right(pointer)) =>
          program.types.get(sourceType) match {
            case Some(RuntimeType.VectorType(component, count)) if lanes.length == count =>
              val elementSize = typeSize(program, component)
              val base        = Math.addExact(
                pointer.offset,
                Math.multiplyExact(Math.multiplyExact(offset, count.toLong), elementSize.toLong)
              )
              var index = 0
              while (index < count) {
                memory.writeScalar(
                  pointer.copy(
                    offset = Math.addExact(base, Math.multiplyExact(index.toLong, elementSize.toLong)),
                    pointee = component
                  ),
                  lanes(index),
                  elementSize
                ) match {
                  case Left(error) => return Left(error)
                  case Right(())   => ()
                }
                index += 1
              }
              Right(())
            case Some(RuntimeType.VectorType(_, count)) =>
              Left(ExecutionFailure.InvalidRequest(s"OpenCL.std vstoren has ${lanes.length} lanes, expected $count"))
            case _ => Left(ExecutionFailure.InvalidRequest("OpenCL.std vstoren source is not a vector"))
          }
        case (None, _, _)              => Left(undefined(source))
        case (Some(_), Left(error), _) => Left(error)
        case (Some(_), _, Left(error)) => Left(error)
        case (Some(_), _, _) => Left(ExecutionFailure.InvalidRequest("OpenCL.std vstoren source is not numeric"))
      }

  private def openCLNextAfter(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      left: Operand,
      right: Operand
  ): Either[ExecutionFailure, Unit] =
    (values.get(id(left)), values.get(id(right))) match {
      case (Some(RuntimeValue.Bits(leftType, leftLanes)), Some(RuntimeValue.Bits(rightType, rightLanes))) =>
        (
          floatingShape(program, leftType),
          floatingShape(program, rightType),
          floatingShape(program, id(resultType))
        ) match {
          case (Some((width, count)), Some((rightWidth, rightCount)), Some((resultWidth, resultCount)))
              if width == rightWidth && width == resultWidth && count == rightCount && count == resultCount =>
            val result = Array.tabulate(count) { index =>
              width match {
                case 16 =>
                  IEEE754.floatToBinary16(
                    Math.nextAfter(
                      IEEE754.binary16ToFloat(leftLanes(index)),
                      IEEE754.binary16ToFloat(rightLanes(index))
                    )
                  )
                case 32 =>
                  java.lang.Float.floatToRawIntBits(
                    Math.nextAfter(
                      IEEE754.binary32ToFloat(leftLanes(index)),
                      IEEE754.binary32ToFloat(rightLanes(index))
                    )
                  ).toLong & 0xffffffffL
                case 64 =>
                  java.lang.Double.doubleToRawLongBits(
                    Math.nextAfter(
                      IEEE754.binary64ToDouble(leftLanes(index)),
                      IEEE754.binary64ToDouble(rightLanes(index))
                    )
                  )
              }
            }
            values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
            Right(())
          case _ =>
            Left(
              ExecutionFailure.InvalidRequest(
                "OpenCL.std nextafter requires matching floating-point scalar/vector operand and result types"
              )
            )
        }
      case (None, _) => Left(undefined(left))
      case (_, None) => Left(undefined(right))
      case _         => Left(ExecutionFailure.InvalidRequest("OpenCL.std nextafter operands are not floating-point"))
    }

  private def openCLSignedHalfAdd(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      left: Operand,
      right: Operand
  ): Either[ExecutionFailure, Unit] =
    (values.get(id(left)), values.get(id(right))) match {
      case (Some(RuntimeValue.Bits(leftType, leftLanes)), Some(RuntimeValue.Bits(rightType, rightLanes))) =>
        (
          integerShape(program, leftType),
          integerShape(program, rightType),
          integerShape(program, id(resultType))
        ) match {
          case (Some((width, count)), Some((rightWidth, rightCount)), Some((resultWidth, resultCount)))
              if width == rightWidth && width == resultWidth && count == rightCount && count == resultCount =>
            val result = Array.tabulate(count) { index =>
              val leftValue  = signExtend(leftLanes(index), width)
              val rightValue = signExtend(rightLanes(index), width)
              ((leftValue & rightValue) + ((leftValue ^ rightValue) >> 1)) & mask(width)
            }
            values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
            Right(())
          case _ =>
            Left(
              ExecutionFailure.InvalidRequest(
                "OpenCL.std hadd requires matching integer scalar/vector operand and result types"
              )
            )
        }
      case (None, _) => Left(undefined(left))
      case (_, None) => Left(undefined(right))
      case _         => Left(ExecutionFailure.InvalidRequest("OpenCL.std hadd operands are not integer values"))
    }

  private def openCLSignedAbs(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand
  ): Either[ExecutionFailure, Unit] = values.get(id(source)) match {
    case Some(RuntimeValue.Bits(sourceType, lanes)) =>
      (integerShape(program, sourceType), integerShape(program, id(resultType))) match {
        case (Some((sourceWidth, sourceLanes)), Some((resultWidth, resultLanes)))
            if sourceWidth == resultWidth && sourceLanes == resultLanes && lanes.length == sourceLanes =>
          val result = lanes.map { raw =>
            val signed = signExtend(raw, sourceWidth)
            (if (signed < 0L) -signed else signed) & mask(resultWidth)
          }
          values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
          Right(())
        case _ =>
          Left(ExecutionFailure.InvalidRequest("OpenCL.std s_abs requires matching integer scalar/vector types"))
      }
    case Some(_) => Left(ExecutionFailure.InvalidRequest("OpenCL.std s_abs operand is a pointer"))
    case None    => Left(undefined(source))
  }

  private def openCLCountLeadingZeros(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand
  ): Either[ExecutionFailure, Unit] = values.get(id(source)) match {
    case Some(RuntimeValue.Bits(sourceType, lanes)) =>
      (integerShape(program, sourceType), integerShape(program, id(resultType))) match {
        case (Some((sourceWidth, sourceLanes)), Some((resultWidth, resultLanes)))
            if sourceWidth == resultWidth && sourceLanes == resultLanes && lanes.length == sourceLanes =>
          val result = lanes.map { raw =>
            val value = raw & mask(sourceWidth)
            if (value == 0L)
              sourceWidth.toLong
            else
              (java.lang.Long.numberOfLeadingZeros(value) - (64 - sourceWidth)).toLong
          }
          values.update(id(destination), RuntimeValue.Bits(id(resultType), result.map(_ & mask(resultWidth))))
          Right(())
        case _ =>
          Left(ExecutionFailure.InvalidRequest("OpenCL.std clz requires matching integer scalar/vector types"))
      }
    case Some(_) => Left(ExecutionFailure.InvalidRequest("OpenCL.std clz operand is a pointer"))
    case None    => Left(undefined(source))
  }

  private def openCLCountTrailingZeros(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand
  ): Either[ExecutionFailure, Unit] = values.get(id(source)) match {
    case Some(RuntimeValue.Bits(sourceType, lanes)) =>
      (integerShape(program, sourceType), integerShape(program, id(resultType))) match {
        case (Some((sourceWidth, sourceLanes)), Some((resultWidth, resultLanes)))
            if sourceWidth == resultWidth && sourceLanes == resultLanes && lanes.length == sourceLanes =>
          val result = lanes.map { raw =>
            val value = raw & mask(sourceWidth)
            if (value == 0L) sourceWidth.toLong else java.lang.Long.numberOfTrailingZeros(value).toLong
          }
          values.update(id(destination), RuntimeValue.Bits(id(resultType), result.map(_ & mask(resultWidth))))
          Right(())
        case _ =>
          Left(ExecutionFailure.InvalidRequest("OpenCL.std ctz requires matching integer scalar/vector types"))
      }
    case Some(_) => Left(ExecutionFailure.InvalidRequest("OpenCL.std ctz operand is a pointer"))
    case None    => Left(undefined(source))
  }

  private def extendedInstruction(
      program: DispatchProgram,
      values: InvocationValues,
      memory: MemoryState,
      resultType: Operand,
      destination: Operand,
      set: Operand,
      instruction: Operand,
      sources: Col[Operand]
  ): Either[ExecutionFailure, Unit] = {
    val setId  = id(set)
    val opcode = unsigned(instruction).toInt
    program.extendedInstructionSets.get(setId) match {
      case None => Left(ExecutionFailure.InvalidRequest(s"OpExtInst references undefined import %$setId"))
      case Some("OpenCL.std") if opcode == 13 =>
        if (sources.size != 2)
          Left(ExecutionFailure.InvalidRequest(s"OpenCL.std copysign expects 2 operands, received ${sources.size}"))
        else
          (values.get(id(sources(0))), values.get(id(sources(1)))) match {
            case (Some(RuntimeValue.Bits(leftType, left)), Some(RuntimeValue.Bits(rightType, right))) =>
              (
                floatingShape(program, leftType),
                floatingShape(program, rightType),
                floatingShape(program, id(resultType))
              ) match {
                case (Some(leftShape @ (width, lanes)), Some(rightShape), Some(resultShape))
                    if leftShape == rightShape && leftShape == resultShape =>
                  val signMask = width match {
                    case 16 => 0x8000L
                    case 32 => 0x80000000L
                    case 64 => 0x8000000000000000L
                  }
                  values.update(
                    id(destination),
                    RuntimeValue.Bits(
                      id(resultType),
                      Array.tabulate(lanes)(index => (left(index) & ~signMask) | (right(index) & signMask))
                    )
                  )
                  Right(())
                case _ =>
                  Left(
                    ExecutionFailure.InvalidRequest(
                      "OpenCL.std copysign requires matching floating-point scalar/vector operand and result types"
                    )
                  )
              }
            case (None, _) => Left(undefined(sources(0)))
            case (_, None) => Left(undefined(sources(1)))
            case _ => Left(ExecutionFailure.InvalidRequest("OpenCL.std copysign operands are not floating-point"))
          }
      case Some("OpenCL.std") if opcode == 42 =>
        if (sources.size != 3)
          Left(ExecutionFailure.InvalidRequest(s"OpenCL.std mad expects 3 operands, received ${sources.size}"))
        else openCLMad(program, values, resultType, destination, sources(0), sources(1), sources(2))
      case Some("OpenCL.std") if opcode == 43 =>
        if (sources.size != 2)
          Left(ExecutionFailure.InvalidRequest(s"OpenCL.std maxmag expects 2 operands, received ${sources.size}"))
        else openCLMaxMag(program, values, resultType, destination, sources(0), sources(1))
      case Some("OpenCL.std") if opcode == 48 =>
        if (sources.size != 2)
          Left(ExecutionFailure.InvalidRequest(s"OpenCL.std pow expects 2 operands, received ${sources.size}"))
        else openCLPow(program, values, resultType, destination, sources(0), sources(1))
      case Some("OpenCL.std") if opcode >= 156 && opcode <= 159 =>
        val name = opcode match {
          case 156 => "s_max"
          case 157 => "u_max"
          case 158 => "s_min"
          case _   => "u_min"
        }
        val width = integerShape(program, id(resultType)).map(_._1).getOrElse(64)
        if (sources.size != 2)
          Left(ExecutionFailure.InvalidRequest(s"OpenCL.std $name expects 2 operands, received ${sources.size}"))
        else
          integerBitwise(program, values, resultType, destination, sources(0), sources(1), s"OpenCL.std $name") {
            (left, right) =>
              opcode match {
                case 156 => if (signExtend(left, width) >= signExtend(right, width)) left else right
                case 157 => if (java.lang.Long.compareUnsigned(left, right) >= 0) left else right
                case 158 => if (signExtend(left, width) <= signExtend(right, width)) left else right
                case _   => if (java.lang.Long.compareUnsigned(left, right) <= 0) left else right
              }
          }
      case Some("OpenCL.std") if opcode == 58 =>
        if (sources.size != 2)
          Left(ExecutionFailure.InvalidRequest(s"OpenCL.std sincos expects 2 operands, received ${sources.size}"))
        else openCLSinCos(program, values, memory, resultType, destination, sources(0), sources(1))
      case Some("OpenCL.std") if opcode == 27 || opcode == 28 =>
        val name = if (opcode == 27) "fmax" else "fmin"
        if (sources.size != 2)
          Left(ExecutionFailure.InvalidRequest(s"OpenCL.std $name expects 2 operands, received ${sources.size}"))
        else
          floatingBinary(
            program,
            values,
            resultType,
            destination,
            sources(0),
            sources(1),
            if (opcode == 27) FloatingOperation.Maximum else FloatingOperation.Minimum
          )
      case Some("OpenCL.std") if opcode == 34 =>
        if (sources.size != 2)
          Left(ExecutionFailure.InvalidRequest(s"OpenCL.std ldexp expects 2 operands, received ${sources.size}"))
        else openCLLdexp(program, values, resultType, destination, sources(0), sources(1))
      case Some("OpenCL.std")
          if OpenCLUnaryFloatingOpcodes.contains(opcode) =>
        val name = opcode match {
          case 0  => "acos"
          case 1  => "acosh"
          case 2  => "acospi"
          case 3  => "asin"
          case 4  => "asinh"
          case 5  => "asinpi"
          case 6  => "atan"
          case 8  => "atanh"
          case 9  => "atanpi"
          case 11 => "cbrt"
          case 12 => "ceil"
          case 14 => "cos"
          case 15 => "cosh"
          case 16 => "cospi"
          case 19 => "exp"
          case 20 => "exp2"
          case 21 => "exp10"
          case 22 => "expm1"
          case 23 => "fabs"
          case 25 => "floor"
          case 37 => "log"
          case 38 => "log2"
          case 39 => "log10"
          case 40 => "log1p"
          case 41 => "logb"
          case 53 => "rint"
          case 55 => "round"
          case 56 => "rsqrt"
          case 57 => "sin"
          case 59 => "sinh"
          case 60 => "sinpi"
          case 61 => "sqrt"
          case 62 => "tan"
          case 63 => "tanh"
          case 64 => "tanpi"
          case 66 => "trunc"
          case 81 => "native_cos"
          case 83 => "native_exp"
          case 84 => "native_exp2"
          case 85 => "native_exp10"
          case 86 => "native_log"
          case 87 => "native_log2"
          case 88 => "native_log10"
          case 90 => "native_recip"
          case 91 => "native_rsqrt"
          case 92 => "native_sin"
          case 93 => "native_sqrt"
          case 94 => "native_tan"
        }
        if (sources.size != 1)
          Left(ExecutionFailure.InvalidRequest(s"OpenCL.std $name expects 1 operand, received ${sources.size}"))
        else
          values.get(id(sources.head)) match {
            case Some(RuntimeValue.Bits(sourceType, lanes)) =>
              (floatingShape(program, sourceType), floatingShape(program, id(resultType))) match {
                case (Some((sourceWidth, sourceLanes)), Some((resultWidth, resultLanes)))
                    if sourceWidth == resultWidth && sourceLanes == resultLanes =>
                  val result = lanes.map { raw =>
                    def evaluate(value: Double): Double = opcode match {
                      case 0  => Math.acos(value)
                      case 1  => Math.log(value + Math.sqrt(value * value - 1.0))
                      case 2  => Math.acos(value) / Math.PI
                      case 3  => Math.asin(value)
                      case 4  => Math.log(value + Math.sqrt(value * value + 1.0))
                      case 5  => Math.asin(value) / Math.PI
                      case 6  => Math.atan(value)
                      case 8  => 0.5 * Math.log((1.0 + value) / (1.0 - value))
                      case 9  => Math.atan(value) / Math.PI
                      case 11 => Math.cbrt(value)
                      case 12 => Math.ceil(value)
                      case 14 => Math.cos(value)
                      case 15 => Math.cosh(value)
                      case 16 => openCLCosPi(value)
                      case 19 => Math.exp(value)
                      case 20 => Math.pow(2.0, value)
                      case 21 => Math.pow(10.0, value)
                      case 22 => Math.expm1(value)
                      case 23 => Math.abs(value)
                      case 25 => Math.floor(value)
                      case 37 => Math.log(value)
                      case 38 => Math.log(value) / Math.log(2.0)
                      case 39 => Math.log10(value)
                      case 40 => Math.log1p(value)
                      case 41 =>
                        if (value.isNaN) Double.NaN
                        else if (value == 0.0) Double.NegativeInfinity
                        else if (value.isInfinite) Double.PositiveInfinity
                        else Math.getExponent(value).toDouble
                      case 53 => Math.rint(value)
                      case 55 => if (value < 0.0) Math.ceil(value - 0.5) else Math.floor(value + 0.5)
                      case 56 => 1.0 / Math.sqrt(value)
                      case 57 => Math.sin(value)
                      case 59 => Math.sinh(value)
                      case 60 => openCLSinPi(value)
                      case 61 => Math.sqrt(value)
                      case 62 => Math.tan(value)
                      case 63 => Math.tanh(value)
                      case 64 => openCLTanPi(value)
                      case 66 => if (value < 0.0) Math.ceil(value) else Math.floor(value)
                      case 81 => Math.cos(value)
                      case 83 => Math.exp(value)
                      case 84 => Math.pow(2.0, value)
                      case 85 => Math.pow(10.0, value)
                      case 86 => Math.log(value)
                      case 87 => Math.log(value) / Math.log(2.0)
                      case 88 => Math.log10(value)
                      case 90 => 1.0 / value
                      case 91 => 1.0 / Math.sqrt(value)
                      case 92 => Math.sin(value)
                      case 93 => Math.sqrt(value)
                      case 94 => Math.tan(value)
                    }
                    resultWidth match {
                      case 16 if opcode == 23 => raw & 0x7fffL
                      case 32 if opcode == 23 => raw & 0x7fffffffL
                      case 64 if opcode == 23 => raw & 0x7fffffffffffffffL
                      case 16 => IEEE754.floatToBinary16(evaluate(IEEE754.binary16ToFloat(raw).toDouble).toFloat)
                      case 32 =>
                        IEEE754.floatToBinary32(evaluate(IEEE754.binary32ToFloat(raw).toDouble).toFloat)
                      case 64 => IEEE754.doubleToBinary64(evaluate(IEEE754.binary64ToDouble(raw)))
                    }
                  }
                  values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
                  Right(())
                case _ =>
                  Left(
                    ExecutionFailure.InvalidRequest(
                      s"OpenCL.std $name requires matching floating-point scalar/vector operand and result types"
                    )
                  )
              }
            case Some(_) => Left(ExecutionFailure.InvalidRequest(s"OpenCL.std $name operand is not floating-point"))
            case None    => Left(undefined(sources.head))
          }
      case Some("OpenCL.std") if OpenCLBinaryFloatingOpcodes.contains(opcode) =>
        val name = opcode match {
          case 7  => "atan2"
          case 10 => "atan2pi"
          case 24 => "fdim"
          case 29 => "fmod"
          case 32 => "hypot"
          case _  => "powr"
        }
        if (sources.size != 2)
          Left(ExecutionFailure.InvalidRequest(s"OpenCL.std $name expects 2 operands, received ${sources.size}"))
        else if (opcode == 7 || opcode == 32)
          openCLAtan2OrHypot(program, values, resultType, destination, sources(0), sources(1), opcode == 32)
        else
          openCLFloatingElementwise(program, values, resultType, destination, sources, name) { operands =>
            opcode match {
              case 10 => Math.atan2(operands(0), operands(1)) / Math.PI
              case 24 => Math.max(operands(0) - operands(1), 0.0)
              case 29 => operands(0) % operands(1)
              case _  => openCLPowr(operands(0), operands(1))
            }
          }
      case Some("OpenCL.std") if opcode == 46 =>
        if (sources.size != 1)
          Left(ExecutionFailure.InvalidRequest(s"OpenCL.std nan expects 1 operand, received ${sources.size}"))
        else openCLNan(program, values, resultType, destination, sources.head)
      case Some("OpenCL.std") if opcode == 49 || opcode == 54 =>
        val name = if (opcode == 49) "pown" else "rootn"
        if (sources.size != 2)
          Left(ExecutionFailure.InvalidRequest(s"OpenCL.std $name expects 2 operands, received ${sources.size}"))
        else openCLIntegerPower(program, values, resultType, destination, sources(0), sources(1), opcode == 54)
      case Some("OpenCL.std") if opcode == 47 =>
        if (sources.size != 2)
          Left(ExecutionFailure.InvalidRequest(s"OpenCL.std nextafter expects 2 operands, received ${sources.size}"))
        else openCLNextAfter(program, values, resultType, destination, sources(0), sources(1))
      case Some("OpenCL.std") if opcode == 145 =>
        if (sources.size != 2)
          Left(ExecutionFailure.InvalidRequest(s"OpenCL.std hadd expects 2 operands, received ${sources.size}"))
        else openCLSignedHalfAdd(program, values, resultType, destination, sources(0), sources(1))
      case Some("OpenCL.std") if opcode == 141 =>
        if (sources.size != 1)
          Left(ExecutionFailure.InvalidRequest(s"OpenCL.std s_abs expects 1 operand, received ${sources.size}"))
        else openCLSignedAbs(program, values, resultType, destination, sources.head)
      case Some("OpenCL.std") if opcode == 151 =>
        if (sources.size != 1)
          Left(ExecutionFailure.InvalidRequest(s"OpenCL.std clz expects 1 operand, received ${sources.size}"))
        else openCLCountLeadingZeros(program, values, resultType, destination, sources.head)
      case Some("OpenCL.std") if opcode == 152 =>
        if (sources.size != 1)
          Left(ExecutionFailure.InvalidRequest(s"OpenCL.std ctz expects 1 operand, received ${sources.size}"))
        else openCLCountTrailingZeros(program, values, resultType, destination, sources.head)
      case Some("OpenCL.std") if opcode >= 95 && opcode <= 103 =>
        openCLCommon(program, values, resultType, destination, opcode, sources)
      case Some("OpenCL.std") if opcode >= 104 && opcode <= 110 =>
        openCLGeometric(program, values, resultType, destination, opcode, sources)
      case Some("OpenCL.std") if opcode == 173 || opcode == 174 || opcode == 179 =>
        openCLHalfLoad(program, values, memory, resultType, destination, sources, aligned = opcode == 179)
      case Some("OpenCL.std") if opcode >= 175 && opcode <= 181 =>
        openCLHalfStore(
          program,
          values,
          memory,
          sources,
          aligned = opcode >= 180,
          rounded = opcode == 176 || opcode == 178 || opcode == 181
        )
      case Some("OpenCL.std") if opcode == 171 =>
        openCLVectorLoad(program, values, memory, resultType, destination, sources)
      case Some("OpenCL.std") if opcode == 172 =>
        if (sources.size != 3)
          Left(ExecutionFailure.InvalidRequest(s"OpenCL.std vstoren expects 3 operands, received ${sources.size}"))
        else
          openCLVectorStore(program, values, memory, sources.head, Col(sources(1), sources(2)))
      case Some("OpenCL.std") if opcode == 182 || opcode == 183 =>
        openCLShuffle(program, values, resultType, destination, sources, opcode == 183)
      case Some("OpenCL.std") if opcode == 186 =>
        openCLBitselect(program, values, resultType, destination, sources)
      case Some("OpenCL.std") if opcode == 187 =>
        openCLSelect(program, values, resultType, destination, sources)
      case Some("OpenCL.std") if opcode == 185 =>
        if (sources.isEmpty)
          Left(ExecutionFailure.InvalidRequest("OpenCL.std prefetch is missing its pointer operand"))
        else address(values, sources.head).map(_ => ())
      case Some(name) =>
        Left(ExecutionFailure.Unsupported(s"OpExtInst $name opcode $opcode execution is not implemented"))
    }
  }

  private def floatingToDouble(width: Int, bits: Long): Double = width match {
    case 16 => IEEE754.binary16ToFloat(bits).toDouble
    case 32 => IEEE754.binary32ToFloat(bits).toDouble
    case 64 => IEEE754.binary64ToDouble(bits)
  }

  private def doubleToFloating(width: Int, value: Double): Long = width match {
    case 16 => IEEE754.floatToBinary16(value.toFloat)
    case 32 => IEEE754.floatToBinary32(value.toFloat)
    case 64 => IEEE754.doubleToBinary64(value)
  }

  private def openCLSinPi(value: Double): Double =
    if (!value.isFinite) Double.NaN
    else if (value == Math.rint(value)) Math.copySign(0.0, value)
    else Math.sin(Math.PI * value)

  private def openCLCosPi(value: Double): Double =
    if (!value.isFinite) Double.NaN
    else {
      val floor      = Math.floor(value)
      val fractional = value - floor
      if (fractional == 0.5) 0.0
      else if (fractional == 0.0) if ((floor.toLong & 1L) == 0L) 1.0 else -1.0
      else Math.cos(Math.PI * value)
    }

  private def openCLTanPi(value: Double): Double =
    if (!value.isFinite) Double.NaN
    else {
      val floor      = Math.floor(value)
      val fractional = value - floor
      if (fractional == 0.0) {
        val signSource = if ((Math.abs(floor.toLong) & 1L) == 0L) value else -value
        Math.copySign(0.0, signSource)
      } else if (fractional == 0.5) {
        if ((floor.toLong & 1L) == 0L) Double.PositiveInfinity else Double.NegativeInfinity
      } else Math.tan(Math.PI * value)
    }

  private def openCLPowr(base: Double, exponent: Double): Double =
    if (base < 0.0 || ((base == 0.0 || base == Double.PositiveInfinity) && exponent == 0.0)) Double.NaN
    else if (base == 0.0) {
      if (exponent < 0.0) Double.PositiveInfinity else 0.0
    } else Math.pow(base, exponent)

  private def openCLFloatingElementwise(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      sources: Col[Operand],
      name: String
  )(operation: Array[Double] => Double): Either[ExecutionFailure, Unit] =
    floatingShape(program, id(resultType)) match {
      case None => Left(ExecutionFailure.InvalidRequest(s"OpenCL.std $name result is not floating-point"))
      case Some((width, count)) =>
        val sourceLanes  = Array.ofDim[Array[Long]](sources.size)
        val sourceCounts = Array.ofDim[Int](sources.size)
        var sourceIndex  = 0
        while (sourceIndex < sources.size) {
          values.get(id(sources(sourceIndex))) match {
            case Some(RuntimeValue.Bits(sourceType, lanes)) => floatingShape(program, sourceType) match {
                case Some((sourceWidth, sourceCount))
                    if sourceWidth == width && (sourceCount == count || sourceCount == 1) =>
                  sourceLanes(sourceIndex) = lanes
                  sourceCounts(sourceIndex) = sourceCount
                case _ =>
                  return Left(
                    ExecutionFailure.InvalidRequest(
                      s"OpenCL.std $name operand ${sourceIndex + 1} does not match result shape ($width,$count)"
                    )
                  )
              }
            case Some(_) =>
              return Left(ExecutionFailure.InvalidRequest(s"OpenCL.std $name operand ${sourceIndex + 1} is a pointer"))
            case None => return Left(undefined(sources(sourceIndex)))
          }
          sourceIndex += 1
        }
        val arguments = Array.ofDim[Double](sources.size)
        val result    = Array.tabulate(count) { lane =>
          var index = 0
          while (index < sources.size) {
            arguments(index) = floatingToDouble(width, sourceLanes(index)(lane % sourceCounts(index)))
            index += 1
          }
          doubleToFloating(width, operation(arguments))
        }
        values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
        Right(())
    }

  private def openCLCommon(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      opcode: Int,
      sources: Col[Operand]
  ): Either[ExecutionFailure, Unit] = {
    val (name, arity) = opcode match {
      case 95  => "clamp"      -> 3
      case 96  => "degrees"    -> 1
      case 97  => "fmax"       -> 2
      case 98  => "fmin"       -> 2
      case 99  => "mix"        -> 3
      case 100 => "radians"    -> 1
      case 101 => "step"       -> 2
      case 102 => "smoothstep" -> 3
      case _   => "sign"       -> 1
    }
    if (sources.size != arity)
      Left(ExecutionFailure.InvalidRequest(s"OpenCL.std $name expects $arity operands, received ${sources.size}"))
    else
      openCLFloatingElementwise(program, values, resultType, destination, sources, name) { operands =>
        opcode match {
          case 95  => floatingMinimum(floatingMaximum(operands(0), operands(1)), operands(2))
          case 96  => operands(0) * (180.0 / Math.PI)
          case 97  => floatingMaximum(operands(0), operands(1))
          case 98  => floatingMinimum(operands(0), operands(1))
          case 99  => operands(0) + (operands(1) - operands(0)) * operands(2)
          case 100 => operands(0) * (Math.PI / 180.0)
          case 101 => if (operands(1) < operands(0)) 0.0 else 1.0
          case 102 =>
            val ratio = (operands(2) - operands(0)) / (operands(1) - operands(0))
            val t     = floatingMinimum(floatingMaximum(ratio, 0.0), 1.0)
            t * t * (3.0 - 2.0 * t)
          case _ =>
            if (operands(0) > 0.0) 1.0
            else if (operands(0) < 0.0) -1.0
            else 0.0
        }
      }
  }

  private def openCLGeometric(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      opcode: Int,
      sources: Col[Operand]
  ): Either[ExecutionFailure, Unit] = {
    val name = opcode match {
      case 104 => "cross"
      case 105 => "distance"
      case 106 => "length"
      case 107 => "normalize"
      case 108 => "fast_distance"
      case 109 => "fast_length"
      case _   => "fast_normalize"
    }
    val arity = if (opcode == 104 || opcode == 105 || opcode == 108) 2 else 1
    if (sources.size != arity)
      return Left(
        ExecutionFailure.InvalidRequest(s"OpenCL.std $name expects $arity operands, received ${sources.size}")
      )
    val operands = Array.ofDim[Array[Long]](arity)
    var width    = 0
    var count    = 0
    var index    = 0
    while (index < arity) {
      values.get(id(sources(index))) match {
        case Some(RuntimeValue.Bits(sourceType, lanes)) => floatingShape(program, sourceType) match {
            case Some((sourceWidth, sourceCount)) if index == 0 || (sourceWidth == width && sourceCount == count) =>
              width = sourceWidth
              count = sourceCount
              operands(index) = lanes
            case _ => return Left(ExecutionFailure.InvalidRequest(s"OpenCL.std $name operands have incompatible types"))
          }
        case Some(_) => return Left(ExecutionFailure.InvalidRequest(s"OpenCL.std $name operand is a pointer"))
        case None    => return Left(undefined(sources(index)))
      }
      index += 1
    }
    def component(source: Int, lane: Int): Double = floatingToDouble(width, operands(source)(lane))
    def magnitude(source: Int): Double            = {
      var sum  = 0.0
      var lane = 0
      while (lane < count) {
        val value = component(source, lane)
        sum += value * value
        lane += 1
      }
      Math.sqrt(sum)
    }
    opcode match {
      case 104 =>
        if (count != 3 && count != 4)
          Left(ExecutionFailure.InvalidRequest("OpenCL.std cross requires 3- or 4-component vectors"))
        else if (!floatingShape(program, id(resultType)).contains((width, count)))
          Left(ExecutionFailure.InvalidRequest("OpenCL.std cross result type does not match its operands"))
        else {
          val result = Array(
            doubleToFloating(width, component(0, 1) * component(1, 2) - component(0, 2) * component(1, 1)),
            doubleToFloating(width, component(0, 2) * component(1, 0) - component(0, 0) * component(1, 2)),
            doubleToFloating(width, component(0, 0) * component(1, 1) - component(0, 1) * component(1, 0))
          ) ++ Option.when(count == 4)(doubleToFloating(width, 0.0))
          values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
          Right(())
        }
      case 105 | 108 =>
        if (!floatingShape(program, id(resultType)).contains((width, 1)))
          Left(ExecutionFailure.InvalidRequest(s"OpenCL.std $name result is not a matching floating scalar"))
        else {
          var sum  = 0.0
          var lane = 0
          while (lane < count) {
            val difference = component(0, lane) - component(1, lane)
            sum += difference * difference
            lane += 1
          }
          values.update(
            id(destination),
            RuntimeValue.Bits(id(resultType), Array(doubleToFloating(width, Math.sqrt(sum))))
          )
          Right(())
        }
      case 106 | 109 =>
        if (!floatingShape(program, id(resultType)).contains((width, 1)))
          Left(ExecutionFailure.InvalidRequest(s"OpenCL.std $name result is not a matching floating scalar"))
        else {
          values.update(
            id(destination),
            RuntimeValue.Bits(id(resultType), Array(doubleToFloating(width, magnitude(0))))
          )
          Right(())
        }
      case _ =>
        if (!floatingShape(program, id(resultType)).contains((width, count)))
          Left(ExecutionFailure.InvalidRequest(s"OpenCL.std $name result type does not match its operand"))
        else {
          val length = magnitude(0)
          val result = Array.tabulate(count)(lane => doubleToFloating(width, component(0, lane) / length))
          values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
          Right(())
        }
    }
  }

  private def openCLShuffle(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      sources: Col[Operand],
      twoInputs: Boolean
  ): Either[ExecutionFailure, Unit] = {
    val expected = if (twoInputs) 3 else 2
    if (sources.size != expected)
      return Left(ExecutionFailure.InvalidRequest(s"OpenCL.std shuffle expects $expected operands"))
    val first     = values.get(id(sources(0)))
    val second    = Option.when(twoInputs)(values.get(id(sources(1)))).flatten
    val maskIndex = if (twoInputs) 2 else 1
    val selection = values.get(id(sources(maskIndex)))
    (first, second, selection) match {
      case (
            Some(RuntimeValue.Bits(firstType, firstLanes)),
            secondValue,
            Some(RuntimeValue.Bits(maskType, maskLanes))
          ) =>
        val secondLanes =
          secondValue.collect { case RuntimeValue.Bits(_, lanes) => lanes }.getOrElse(Array.emptyLongArray)
        val sourceShape = numericShape(program, firstType)
        val secondShape = secondValue.flatMap {
          case RuntimeValue.Bits(secondType, _) => numericShape(program, secondType)
          case _                                => None
        }
        (sourceShape, integerShape(program, maskType), numericShape(program, id(resultType))) match {
          case (Some((width, sourceCount)), Some((_, maskCount)), Some((resultWidth, resultCount)))
              if width == resultWidth && maskCount == resultCount &&
                (!twoInputs || secondShape.contains((width, sourceCount))) =>
            val combined = if (twoInputs) firstLanes ++ secondLanes else firstLanes
            val result   = Array.tabulate(resultCount) { lane =>
              combined(java.lang.Long.remainderUnsigned(maskLanes(lane), combined.length.toLong).toInt)
            }
            values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
            Right(())
          case _ => Left(ExecutionFailure.InvalidRequest("OpenCL.std shuffle operand types are incompatible"))
        }
      case (None, _, _) => Left(undefined(sources(0)))
      case (_, _, None) => Left(undefined(sources(maskIndex)))
      case _            => Left(ExecutionFailure.InvalidRequest("OpenCL.std shuffle operands are not value types"))
    }
  }

  private def openCLBitselect(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      sources: Col[Operand]
  ): Either[ExecutionFailure, Unit] = {
    if (sources.size != 3)
      return Left(ExecutionFailure.InvalidRequest(s"OpenCL.std bitselect expects 3 operands, received ${sources.size}"))
    (values.get(id(sources(0))), values.get(id(sources(1))), values.get(id(sources(2)))) match {
      case (
            Some(RuntimeValue.Bits(aType, a)),
            Some(RuntimeValue.Bits(bType, b)),
            Some(RuntimeValue.Bits(cType, c))
          ) =>
        val resultShape = numericShape(program, id(resultType))
        if (
          numericShape(program, aType) != resultShape || numericShape(program, bType) != resultShape ||
          numericShape(program, cType) != resultShape
        )
          Left(ExecutionFailure.InvalidRequest("OpenCL.std bitselect operands must have matching numeric shapes"))
        else {
          values.update(
            id(destination),
            RuntimeValue.Bits(
              id(resultType),
              Array.tabulate(a.length)(index => (a(index) & ~c(index)) | (b(index) & c(index)))
            )
          )
          Right(())
        }
      case _ => Left(ExecutionFailure.InvalidRequest("OpenCL.std bitselect operands are undefined or pointer values"))
    }
  }

  private def openCLSelect(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      sources: Col[Operand]
  ): Either[ExecutionFailure, Unit] = {
    if (sources.size != 3)
      return Left(ExecutionFailure.InvalidRequest(s"OpenCL.std select expects 3 operands, received ${sources.size}"))
    (values.get(id(sources(0))), values.get(id(sources(1))), values.get(id(sources(2)))) match {
      case (
            Some(RuntimeValue.Bits(aType, a)),
            Some(RuntimeValue.Bits(bType, b)),
            Some(RuntimeValue.Bits(cType, c))
          ) =>
        (
          numericShape(program, id(resultType)),
          numericShape(program, aType),
          numericShape(program, bType),
          integerShape(program, cType)
        ) match {
          case (Some(resultShape @ (_, count)), Some(aShape), Some(bShape), Some((conditionWidth, conditionCount)))
              if resultShape == aShape && resultShape == bShape && (conditionCount == count || conditionCount == 1) =>
            val sign   = 1L << (conditionWidth - 1)
            val result = Array.tabulate(count) { lane =>
              val condition = c(lane % conditionCount)
              val selected  =
                if (count == 1 && conditionCount == 1) condition != 0L
                else (condition & sign) != 0L
              if (selected) b(lane) else a(lane)
            }
            values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
            Right(())
          case _ => Left(ExecutionFailure.InvalidRequest("OpenCL.std select operand types are incompatible"))
        }
      case _ => Left(ExecutionFailure.InvalidRequest("OpenCL.std select operands are undefined or pointer values"))
    }
  }

  private def openCLNan(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand
  ): Either[ExecutionFailure, Unit] = values.get(id(source)) match {
    case Some(RuntimeValue.Bits(sourceType, sourceLanes)) =>
      (floatingShape(program, id(resultType)), integerShape(program, sourceType)) match {
        case (Some((width, count)), Some((_, sourceCount))) if sourceCount == count || sourceCount == 1 =>
          val result = Array.tabulate(count) { lane =>
            val payload = sourceLanes(lane % sourceCount)
            width match {
              case 16 => 0x7e00L | (payload & 0x1ffL)
              case 32 => 0x7fc00000L | (payload & 0x3fffffL)
              case 64 => 0x7ff8000000000000L | (payload & 0x7ffffffffffffL)
            }
          }
          values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
          Right(())
        case _ => Left(ExecutionFailure.InvalidRequest("OpenCL.std nan requires a floating result and integer payload"))
      }
    case Some(_) => Left(ExecutionFailure.InvalidRequest("OpenCL.std nan payload is a pointer"))
    case None    => Left(undefined(source))
  }

  private def openCLIntegerPower(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand,
      exponent: Operand,
      root: Boolean
  ): Either[ExecutionFailure, Unit] = (values.get(id(source)), values.get(id(exponent))) match {
    case (Some(RuntimeValue.Bits(sourceType, sourceLanes)), Some(RuntimeValue.Bits(exponentType, exponentLanes))) =>
      (
        floatingShape(program, id(resultType)),
        floatingShape(program, sourceType),
        integerShape(program, exponentType)
      ) match {
        case (Some((width, count)), Some((sourceWidth, sourceCount)), Some((exponentWidth, exponentCount)))
            if width == sourceWidth && count == sourceCount && (exponentCount == count || exponentCount == 1) =>
          val result = Array.tabulate(count) { lane =>
            val value    = floatingToDouble(width, sourceLanes(lane))
            val power    = signExtend(exponentLanes(lane % exponentCount), exponentWidth).toInt
            val negative = width match {
              case 16 => (sourceLanes(lane) & 0x8000L) != 0L
              case 32 => (sourceLanes(lane) & 0x80000000L) != 0L
              case 64 => sourceLanes(lane) < 0L
            }
            val evaluated =
              if (!root) Math.pow(value, power.toDouble)
              else if (power == 0) Double.NaN
              else if (value == 0.0 && negative && (power & 1) != 0)
                if (power < 0) Double.NegativeInfinity else -0.0
              else if (value < 0.0 && (power & 1) != 0) -Math.pow(-value, 1.0 / power.toDouble)
              else if (value < 0.0) Double.NaN
              else Math.pow(value, 1.0 / power.toDouble)
            doubleToFloating(width, evaluated)
          }
          values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
          Right(())
        case _ =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"OpenCL.std ${if (root) "rootn" else "pown"} requires floating values and integer exponents"
            )
          )
      }
    case (None, _) => Left(undefined(source))
    case (_, None) => Left(undefined(exponent))
    case _         => Left(ExecutionFailure.InvalidRequest("OpenCL.std integer-power operands are not value types"))
  }

  private def openCLMad(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      left: Operand,
      right: Operand,
      addend: Operand
  ): Either[ExecutionFailure, Unit] =
    (values.get(id(left)), values.get(id(right)), values.get(id(addend))) match {
      case (
            Some(RuntimeValue.Bits(leftType, leftLanes)),
            Some(RuntimeValue.Bits(rightType, rightLanes)),
            Some(RuntimeValue.Bits(addendType, addendLanes))
          ) =>
        (
          floatingShape(program, leftType),
          floatingShape(program, rightType),
          floatingShape(program, addendType),
          floatingShape(program, id(resultType))
        ) match {
          case (
                Some((width, lanes)),
                Some((rightWidth, rightLanesCount)),
                Some((addendWidth, addendLanesCount)),
                Some((resultWidth, resultLanes))
              )
              if width == rightWidth && width == addendWidth && width == resultWidth &&
                lanes == rightLanesCount && lanes == addendLanesCount && lanes == resultLanes &&
                leftLanes.length == lanes && rightLanes.length == lanes && addendLanes.length == lanes =>
            val result = Array.tabulate(lanes) { index =>
              floatingAddBits(
                width,
                floatingMultiplyBits(width, leftLanes(index), rightLanes(index)),
                addendLanes(index)
              )
            }
            values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
            Right(())
          case _ =>
            Left(ExecutionFailure.InvalidRequest("OpenCL.std mad requires matching floating-point types"))
        }
      case (None, _, _) => Left(undefined(left))
      case (_, None, _) => Left(undefined(right))
      case (_, _, None) => Left(undefined(addend))
      case _            => Left(ExecutionFailure.InvalidRequest("OpenCL.std mad operands are not floating-point"))
    }

  private def openCLSinCos(
      program: DispatchProgram,
      values: InvocationValues,
      memory: MemoryState,
      resultType: Operand,
      destination: Operand,
      source: Operand,
      cosine: Operand
  ): Either[ExecutionFailure, Unit] =
    (values.get(id(source)), address(values, cosine)) match {
      case (Some(RuntimeValue.Bits(sourceType, sourceLanes)), Right(cosinePointer)) =>
        (floatingShape(program, sourceType), floatingShape(program, id(resultType))) match {
          case (Some((width, count)), Some((resultWidth, resultCount)))
              if width == resultWidth && count == resultCount && sourceLanes.length == count =>
            val sineLanes   = Array.ofDim[Long](count)
            val cosineLanes = Array.ofDim[Long](count)
            var index       = 0
            while (index < count) {
              width match {
                case 16 =>
                  val value = IEEE754.binary16ToFloat(sourceLanes(index)).toDouble
                  sineLanes(index) = IEEE754.floatToBinary16(Math.sin(value).toFloat)
                  cosineLanes(index) = IEEE754.floatToBinary16(Math.cos(value).toFloat)
                case 32 =>
                  val value = IEEE754.binary32ToFloat(sourceLanes(index)).toDouble
                  sineLanes(index) = IEEE754.floatToBinary32(Math.sin(value).toFloat)
                  cosineLanes(index) = IEEE754.floatToBinary32(Math.cos(value).toFloat)
                case 64 =>
                  val value = IEEE754.binary64ToDouble(sourceLanes(index))
                  sineLanes(index) = IEEE754.doubleToBinary64(Math.sin(value))
                  cosineLanes(index) = IEEE754.doubleToBinary64(Math.cos(value))
              }
              index += 1
            }
            val cosineBytes = Array.ofDim[Byte](typeSize(program, id(resultType)))
            val _           = encodeBits(program, id(resultType), cosineLanes, 0, cosineBytes, 0)
            memory.write(cosinePointer, cosineBytes) match {
              case Left(error) => Left(error)
              case Right(())   =>
                values.update(id(destination), RuntimeValue.Bits(id(resultType), sineLanes))
                Right(())
            }
          case _ =>
            Left(
              ExecutionFailure.InvalidRequest("OpenCL.std sincos requires matching floating-point scalar/vector types")
            )
        }
      case (None, _)        => Left(undefined(source))
      case (_, Left(error)) => Left(error)
      case _                => Left(ExecutionFailure.InvalidRequest("OpenCL.std sincos source is not floating-point"))
    }

  private def openCLMaxMag(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      left: Operand,
      right: Operand
  ): Either[ExecutionFailure, Unit] = (values.get(id(left)), values.get(id(right))) match {
    case (Some(RuntimeValue.Bits(leftType, leftLanes)), Some(RuntimeValue.Bits(rightType, rightLanes))) =>
      (
        floatingShape(program, leftType),
        floatingShape(program, rightType),
        floatingShape(program, id(resultType))
      ) match {
        case (Some((width, lanes)), Some((rightWidth, rightLanesCount)), Some((resultWidth, resultLanes)))
            if width == rightWidth && width == resultWidth && lanes == rightLanesCount && lanes == resultLanes &&
              leftLanes.length == lanes && rightLanes.length == lanes =>
          val result = Array.tabulate(lanes) { index =>
            width match {
              case 16 =>
                val leftValue  = IEEE754.binary16ToFloat(leftLanes(index)).toDouble
                val rightValue = IEEE754.binary16ToFloat(rightLanes(index)).toDouble
                val selected   =
                  if (Math.abs(leftValue) > Math.abs(rightValue)) leftValue
                  else if (Math.abs(rightValue) > Math.abs(leftValue)) rightValue
                  else Math.max(leftValue, rightValue)
                IEEE754.floatToBinary16(selected.toFloat)
              case 32 =>
                val leftValue  = IEEE754.binary32ToFloat(leftLanes(index))
                val rightValue = IEEE754.binary32ToFloat(rightLanes(index))
                val selected   =
                  if (Math.abs(leftValue) > Math.abs(rightValue)) leftValue
                  else if (Math.abs(rightValue) > Math.abs(leftValue)) rightValue
                  else Math.max(leftValue, rightValue)
                IEEE754.floatToBinary32(selected)
              case 64 =>
                val leftValue  = IEEE754.binary64ToDouble(leftLanes(index))
                val rightValue = IEEE754.binary64ToDouble(rightLanes(index))
                val selected   =
                  if (Math.abs(leftValue) > Math.abs(rightValue)) leftValue
                  else if (Math.abs(rightValue) > Math.abs(leftValue)) rightValue
                  else Math.max(leftValue, rightValue)
                IEEE754.doubleToBinary64(selected)
            }
          }
          values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
          Right(())
        case _ =>
          Left(ExecutionFailure.InvalidRequest("OpenCL.std maxmag requires matching floating-point types"))
      }
    case (None, _) => Left(undefined(left))
    case (_, None) => Left(undefined(right))
    case _         => Left(ExecutionFailure.InvalidRequest("OpenCL.std maxmag operands are not floating-point"))
  }

  private def openCLPow(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      left: Operand,
      right: Operand
  ): Either[ExecutionFailure, Unit] = (values.get(id(left)), values.get(id(right))) match {
    case (Some(RuntimeValue.Bits(leftType, leftLanes)), Some(RuntimeValue.Bits(rightType, rightLanes))) =>
      (
        floatingShape(program, leftType),
        floatingShape(program, rightType),
        floatingShape(program, id(resultType))
      ) match {
        case (Some((width, lanes)), Some((rightWidth, rightLanesCount)), Some((resultWidth, resultLanes)))
            if width == rightWidth && width == resultWidth && lanes == rightLanesCount && lanes == resultLanes &&
              leftLanes.length == lanes && rightLanes.length == lanes =>
          val result = Array.tabulate(lanes) { index =>
            width match {
              case 16 =>
                IEEE754.floatToBinary16(
                  Math.pow(
                    IEEE754.binary16ToFloat(leftLanes(index)).toDouble,
                    IEEE754.binary16ToFloat(rightLanes(index)).toDouble
                  ).toFloat
                )
              case 32 =>
                IEEE754.floatToBinary32(
                  Math.pow(
                    IEEE754.binary32ToFloat(leftLanes(index)).toDouble,
                    IEEE754.binary32ToFloat(rightLanes(index)).toDouble
                  ).toFloat
                )
              case 64 =>
                IEEE754.doubleToBinary64(
                  Math.pow(
                    IEEE754.binary64ToDouble(leftLanes(index)),
                    IEEE754.binary64ToDouble(rightLanes(index))
                  )
                )
            }
          }
          values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
          Right(())
        case _ =>
          Left(ExecutionFailure.InvalidRequest("OpenCL.std pow requires matching floating-point types"))
      }
    case (None, _) => Left(undefined(left))
    case (_, None) => Left(undefined(right))
    case _         => Left(ExecutionFailure.InvalidRequest("OpenCL.std pow operands are not floating-point"))
  }

  private def openCLAtan2OrHypot(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      left: Operand,
      right: Operand,
      hypot: Boolean
  ): Either[ExecutionFailure, Unit] = (values.get(id(left)), values.get(id(right))) match {
    case (Some(RuntimeValue.Bits(leftType, leftLanes)), Some(RuntimeValue.Bits(rightType, rightLanes))) =>
      (
        floatingShape(program, leftType),
        floatingShape(program, rightType),
        floatingShape(program, id(resultType))
      ) match {
        case (
              Some((width, count)),
              Some((rightWidth, rightCount)),
              Some((resultWidth, resultCount))
            )
            if width == rightWidth && width == resultWidth && count == rightCount && count == resultCount &&
              leftLanes.length == count && rightLanes.length == count =>
          val result = Array.tabulate(count) { index =>
            val value = width match {
              case 16 =>
                val leftValue  = IEEE754.binary16ToFloat(leftLanes(index)).toDouble
                val rightValue = IEEE754.binary16ToFloat(rightLanes(index)).toDouble
                if (hypot) Math.hypot(leftValue, rightValue) else Math.atan2(leftValue, rightValue)
              case 32 =>
                val leftValue  = IEEE754.binary32ToFloat(leftLanes(index)).toDouble
                val rightValue = IEEE754.binary32ToFloat(rightLanes(index)).toDouble
                if (hypot) Math.hypot(leftValue, rightValue) else Math.atan2(leftValue, rightValue)
              case 64 =>
                val leftValue  = IEEE754.binary64ToDouble(leftLanes(index))
                val rightValue = IEEE754.binary64ToDouble(rightLanes(index))
                if (hypot) Math.hypot(leftValue, rightValue) else Math.atan2(leftValue, rightValue)
            }
            width match {
              case 16 => IEEE754.floatToBinary16(value.toFloat)
              case 32 => IEEE754.floatToBinary32(value.toFloat)
              case 64 => IEEE754.doubleToBinary64(value)
            }
          }
          values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
          Right(())
        case _ =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"OpenCL.std ${if (hypot) "hypot" else "atan2"} requires matching floating-point scalar/vector types"
            )
          )
      }
    case (None, _) => Left(undefined(left))
    case (_, None) => Left(undefined(right))
    case _         => Left(ExecutionFailure.InvalidRequest("OpenCL.std operands are not floating-point"))
  }

  private def openCLLdexp(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand,
      exponent: Operand
  ): Either[ExecutionFailure, Unit] = (values.get(id(source)), values.get(id(exponent))) match {
    case (Some(RuntimeValue.Bits(sourceType, sourceLanes)), Some(RuntimeValue.Bits(exponentType, exponentLanes))) =>
      (
        floatingShape(program, sourceType),
        floatingShape(program, id(resultType)),
        integerShape(program, exponentType)
      ) match {
        case (
              Some((width, count)),
              Some((resultWidth, resultCount)),
              Some((exponentWidth, exponentCount))
            )
            if width == resultWidth && count == resultCount && exponentWidth == 32 && exponentCount == count &&
              sourceLanes.length == count && exponentLanes.length == count =>
          val result = Array.tabulate(count) { index =>
            val scale = signExtend(exponentLanes(index), exponentWidth).toInt
            width match {
              case 16 =>
                IEEE754.floatToBinary16(
                  Math.scalb(IEEE754.binary16ToFloat(sourceLanes(index)), scale).toFloat
                )
              case 32 =>
                IEEE754.floatToBinary32(Math.scalb(IEEE754.binary32ToFloat(sourceLanes(index)), scale))
              case 64 =>
                IEEE754.doubleToBinary64(Math.scalb(IEEE754.binary64ToDouble(sourceLanes(index)), scale))
            }
          }
          values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
          Right(())
        case _ =>
          Left(ExecutionFailure.InvalidRequest("OpenCL.std ldexp requires matching floating values and i32 exponents"))
      }
    case (None, _) => Left(undefined(source))
    case (_, None) => Left(undefined(exponent))
    case _         => Left(ExecutionFailure.InvalidRequest("OpenCL.std ldexp operands are not value types"))
  }

  private def binary(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      left: Operand,
      right: Operand
  )(operation: (Long, Long) => Long): Either[ExecutionFailure, Unit] = {
    val leftId  = id(left)
    val rightId = id(right)
    if (values.scalarType(leftId) >= 0 && values.scalarType(rightId) >= 0) {
      val width      = scalarWidth(program, id(resultType))
      val leftValue  = values.scalarBits(leftId)
      val rightValue = values.scalarBits(rightId)
      val result     = operation(leftValue, rightValue) & mask(width)
      values.updateScalar(
        id(destination),
        id(resultType),
        result
      )
      Right(())
    } else
      (bits(values, left), bits(values, right)) match {
        case (Right(a), Right(b)) =>
          val width = scalarWidth(program, id(resultType))
          val size  = math.max(a.length, b.length)
          val lanes = Array.tabulate(size)(index => operation(a(index % a.length), b(index % b.length)) & mask(width))
          values.update(id(destination), RuntimeValue.Bits(id(resultType), lanes))
          Right(())
        case (Left(error), _) => Left(error)
        case (_, Left(error)) => Left(error)
      }
  }

  private def integerShift(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      base: Operand,
      shift: Operand,
      opcode: String
  )(operation: (Long, Long, Int) => Long): Either[ExecutionFailure, Unit] =
    (values.get(id(base)), values.get(id(shift))) match {
      case (
            Some(RuntimeValue.Bits(baseType, baseLanes)),
            Some(RuntimeValue.Bits(shiftType, shiftLanes))
          ) =>
        val resultShape = integerShape(program, id(resultType))
        val baseShape   = integerShape(program, baseType)
        val shiftShape  = integerShape(program, shiftType)
        (resultShape, baseShape, shiftShape) match {
          case (Some((width, count)), Some(baseDescriptor), Some((shiftWidth, shiftCount)))
              if baseDescriptor == (width, count) && shiftCount == count && baseLanes.length == count &&
                shiftLanes.length == count =>
            val lanes = Array.tabulate(count) { lane =>
              val amount = shiftLanes(lane) & mask(shiftWidth)
              operation(baseLanes(lane), amount, width) & mask(width)
            }
            values.update(id(destination), RuntimeValue.Bits(id(resultType), lanes))
            Right(())
          case _ =>
            Left(
              ExecutionFailure.InvalidRequest(
                s"$opcode requires an integer scalar/vector result and base of the same type, " +
                  "plus an integer shift operand with the same lane count"
              )
            )
        }
      case (None, _) => Left(undefined(base))
      case (_, None) => Left(undefined(shift))
      case _         =>
        Left(
          ExecutionFailure.InvalidRequest(
            s"$opcode requires integer scalar or vector operands"
          )
        )
    }

  private def compare(
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      left: Operand,
      right: Operand
  )(predicate: (Long, Long) => Boolean): Either[ExecutionFailure, Unit] = {
    val leftId  = id(left)
    val rightId = id(right)
    if (values.scalarType(leftId) >= 0 && values.scalarType(rightId) >= 0) {
      values.updateScalar(
        id(destination),
        id(resultType),
        if (predicate(values.scalarBits(leftId), values.scalarBits(rightId))) 1L else 0L
      )
      Right(())
    } else
      (bits(values, left), bits(values, right)) match {
        case (Right(a), Right(b)) =>
          val size  = math.max(a.length, b.length)
          val lanes =
            Array.tabulate(size)(index => if (predicate(a(index % a.length), b(index % b.length))) 1L else 0L)
          values.update(id(destination), RuntimeValue.Bits(id(resultType), lanes))
          Right(())
        case (Left(error), _) => Left(error)
        case (_, Left(error)) => Left(error)
      }
  }

  private def signedCompare(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      left: Operand,
      right: Operand,
      opcode: String
  )(predicate: (Long, Long) => Boolean): Either[ExecutionFailure, Unit] = {
    val leftId    = id(left)
    val rightId   = id(right)
    val leftType  = values.scalarType(leftId)
    val rightType = values.scalarType(rightId)
    (
      Option.when(leftType >= 0)(leftType).flatMap(integerShape(program, _)),
      Option.when(rightType >= 0)(rightType).flatMap(integerShape(program, _)),
      booleanLanes(program, id(resultType))
    ) match {
      case (Some((width, 1)), Some((rightWidth, 1)), Some(1)) if width == rightWidth =>
        val result =
          predicate(signExtend(values.scalarBits(leftId), width), signExtend(values.scalarBits(rightId), width))
        values.updateScalar(id(destination), id(resultType), if (result) 1L else 0L)
        Right(())
      case _ =>
        (values.get(leftId), values.get(rightId)) match {
          case (
                Some(RuntimeValue.Bits(leftRuntimeType, leftLanes)),
                Some(RuntimeValue.Bits(rightRuntimeType, rightLanes))
              ) =>
            (
              integerShape(program, leftRuntimeType),
              integerShape(program, rightRuntimeType),
              booleanLanes(program, id(resultType))
            ) match {
              case (Some((width, count)), Some((rightWidth, rightCount)), Some(resultCount))
                  if width == rightWidth && count == rightCount && count == resultCount &&
                    leftLanes.length == count && rightLanes.length == count =>
                val result = Array.tabulate(count)(index =>
                  if (predicate(signExtend(leftLanes(index), width), signExtend(rightLanes(index), width))) 1L else 0L
                )
                values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
                Right(())
              case (None, _, _) =>
                Left(
                  ExecutionFailure.InvalidRequest(
                    s"$opcode left operand %$leftId is not an integer scalar or vector"
                  )
                )
              case (_, None, _) =>
                Left(
                  ExecutionFailure.InvalidRequest(
                    s"$opcode right operand %$rightId is not an integer scalar or vector"
                  )
                )
              case (_, _, None) =>
                Left(
                  ExecutionFailure.InvalidRequest(
                    s"$opcode result type %${id(resultType)} is not a boolean scalar or vector"
                  )
                )
              case (Some(leftShape), Some(rightShape), Some(resultLanes)) =>
                Left(
                  ExecutionFailure.InvalidRequest(
                    s"$opcode type shapes do not match: left $leftShape, right $rightShape, result lanes $resultLanes"
                  )
                )
            }
          case (Some(RuntimeValue.Address(_, _)), _) =>
            Left(ExecutionFailure.InvalidRequest(s"$opcode left operand %$leftId is a pointer"))
          case (_, Some(RuntimeValue.Address(_, _))) =>
            Left(ExecutionFailure.InvalidRequest(s"$opcode right operand %$rightId is a pointer"))
          case (None, _) => Left(undefined(left))
          case (_, None) => Left(undefined(right))
        }
    }
  }

  private def builtinValue(
      program: DispatchProgram,
      typeId: Int,
      builtin: Int,
      invocation: Invocation
  ): Either[ExecutionFailure, RuntimeValue] = {
    val lanes: Either[ExecutionFailure, Array[Long]] = builtin match {
      case 24 => Right(invocation.numWorkgroups)
      case 25 => Right(invocation.workgroupSize)
      case 26 => Right(invocation.group)
      case 27 => Right(invocation.local)
      case 28 => Right(invocation.global)
      case 29 => Right(Array(
          (invocation.localZ * invocation.workgroupSizeY + invocation.localY) * invocation.workgroupSizeX + invocation.localX
        ))
      case 30 => Right(Array(invocation.workDim.toLong))
      case 31 => Right(invocation.globalSize)
      case 32 => Right(invocation.workgroupSize)
      case 33 => Right(invocation.globalOffset)
      case 34 => Right(
          Array(
            (invocation.globalX - invocation.globalOffsetX) + invocation.globalSizeX *
              ((invocation.globalY - invocation.globalOffsetY) +
                invocation.globalSizeY * (invocation.globalZ - invocation.globalOffsetZ))
          )
        )
      case 35 => Right(
          Array(
            invocation.groupX + invocation.numWorkgroupsX *
              (invocation.groupY + invocation.numWorkgroupsY * invocation.groupZ)
          )
        )
      case 36    => Right(Array(invocation.subgroupSize.toLong))
      case 37    => Right(Array(invocation.subgroupSize.toLong))
      case 38    => Right(Array(invocation.numSubgroups))
      case 39    => Right(Array(invocation.numSubgroups))
      case 40    => Right(Array(invocation.subgroupIndex.toLong))
      case 41    => Right(Array(invocation.subgroupLocal.toLong))
      case value => Left(ExecutionFailure.Unsupported(s"SPIR-V BuiltIn $value is not implemented"))
    }
    lanes.flatMap(values =>
      integerShape(program, typeId) match {
        case Some((width, count)) if count == values.length =>
          Right(RuntimeValue.Bits(typeId, values.map(_ & mask(width))))
        case Some((_, count)) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"SPIR-V BuiltIn $builtin requires ${values.length} integer lanes, result type %$typeId has $count"
            )
          )
        case None =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"SPIR-V BuiltIn $builtin result type %$typeId is not an integer scalar or vector"
            )
          )
      }
    )
  }

  private def decode(program: DispatchProgram, typeId: Int, bytes: Array[Byte]): RuntimeValue = {
    val count = scalarLeafCount(program, typeId).fold(
      error => throw IllegalArgumentException(error.message),
      identity
    )
    val lanes = Array.ofDim[Long](count)
    val used  = decodeBits(program, typeId, bytes, 0, lanes, 0)
    if (used != lanes.length)
      throw IllegalArgumentException(
        s"SPIR-V value of type %$typeId decoded $used scalar leaves, expected ${lanes.length}"
      )
    RuntimeValue.Bits(typeId, lanes)
  }

  private def decodeBits(
      program: DispatchProgram,
      typeId: Int,
      input: Array[Byte],
      byteStart: Int,
      lanes: Array[Long],
      laneStart: Int
  ): Int = program.types.get(typeId) match {
    case Some(RuntimeType.Bool) | Some(RuntimeType.Integer(_, _)) | Some(RuntimeType.Floating(_)) |
        Some(RuntimeType.Event) =>
      if (laneStart >= lanes.length)
        throw IllegalArgumentException(s"SPIR-V value of type %$typeId has too many scalar leaves")
      val width     = scalarWidth(program, typeId)
      val byteCount = width / 8
      val offset    = Math.min(byteStart, input.length)
      val available = Math.min(byteCount, input.length - offset)
      lanes(laneStart) = LittleEndian.decode(input, offset, available) & mask(width)
      laneStart + 1
    case Some(RuntimeType.VectorType(component, count)) =>
      var lane  = laneStart
      var index = 0
      while (index < count) {
        lane = decodeBits(program, component, input, byteStart + index * typeSize(program, component), lanes, lane)
        index += 1
      }
      lane
    case Some(RuntimeType.Array(element, count)) =>
      val stride = program.arrayStrides.getOrElse(typeId, typeSize(program, element))
      var lane   = laneStart
      var index  = 0L
      while (index < count) {
        lane = decodeBits(program, element, input, byteStart + Math.toIntExact(index * stride.toLong), lanes, lane)
        index += 1L
      }
      lane
    case Some(RuntimeType.Structure(members)) =>
      var lane  = laneStart
      var index = 0
      while (index < members.size) {
        lane = decodeBits(
          program,
          members(index),
          input,
          byteStart + structMemberOffset(program, typeId, members, index),
          lanes,
          lane
        )
        index += 1
      }
      lane
    case value => throw IllegalArgumentException(s"SPIR-V type %$typeId cannot be decoded as data: $value")
  }

  private def encode(program: DispatchProgram, value: RuntimeValue): Array[Byte] = value match {
    case RuntimeValue.Bits(typeId, lanes) =>
      val bytes = Array.ofDim[Byte](typeSize(program, typeId))
      val used  = encodeBits(program, typeId, lanes, 0, bytes, 0)
      if (used != lanes.length)
        throw IllegalArgumentException(
          s"SPIR-V value of type %$typeId has ${lanes.length} scalar leaves, expected $used"
        )
      bytes
    case RuntimeValue.Address(_, pointer) =>
      throw IllegalArgumentException(s"cannot encode runtime pointer $pointer as process-neutral bytes")
  }

  private def encodeBits(
      program: DispatchProgram,
      typeId: Int,
      lanes: Array[Long],
      laneStart: Int,
      output: Array[Byte],
      byteStart: Int
  ): Int = program.types.get(typeId) match {
    case Some(RuntimeType.Bool) | Some(RuntimeType.Integer(_, _)) | Some(RuntimeType.Floating(_)) |
        Some(RuntimeType.Event) =>
      if (laneStart >= lanes.length)
        throw IllegalArgumentException(s"SPIR-V value of type %$typeId has too few scalar leaves")
      val width = scalarWidth(program, typeId)
      LittleEndian.write(lanes(laneStart), output, byteStart, width / 8)
      laneStart + 1
    case Some(RuntimeType.VectorType(component, count)) =>
      var lane  = laneStart
      var index = 0
      while (index < count) {
        lane = encodeBits(program, component, lanes, lane, output, byteStart + index * typeSize(program, component))
        index += 1
      }
      lane
    case Some(RuntimeType.Array(element, count)) =>
      val stride = program.arrayStrides.getOrElse(typeId, typeSize(program, element))
      var lane   = laneStart
      var index  = 0L
      while (index < count) {
        lane = encodeBits(
          program,
          element,
          lanes,
          lane,
          output,
          byteStart + Math.toIntExact(index * stride.toLong)
        )
        index += 1L
      }
      lane
    case Some(RuntimeType.Structure(members)) =>
      var lane  = laneStart
      var index = 0
      while (index < members.size) {
        lane = encodeBits(
          program,
          members(index),
          lanes,
          lane,
          output,
          byteStart + structMemberOffset(program, typeId, members, index)
        )
        index += 1
      }
      lane
    case value => throw IllegalArgumentException(s"SPIR-V type %$typeId cannot be encoded as data: $value")
  }

  private def typeSize(program: DispatchProgram, typeId: Int): Int =
    program.typeSizeCache.getOrElseUpdate(typeId, typeSizeUncached(program, typeId))

  private def typeSizeUncached(program: DispatchProgram, typeId: Int): Int = program.types.get(typeId) match {
    case Some(RuntimeType.Bool)                     => 1
    case Some(RuntimeType.Integer(width, _))        => width / 8
    case Some(RuntimeType.Floating(width))          => width / 8
    case Some(RuntimeType.Event)                    => 8
    case Some(RuntimeType.VectorType(component, n)) =>
      Math.multiplyExact(typeSize(program, component), if (n == 3) 4 else n)
    case Some(RuntimeType.Array(element, n)) =>
      Math.toIntExact(Math.multiplyExact(program.arrayStrides.getOrElse(typeId, typeSize(program, element)).toLong, n))
    case Some(RuntimeType.Structure(members)) => structSize(program, typeId, members)
    case Some(RuntimeType.Pointer(_, _))      => 8
    case Some(RuntimeType.Void)               => 0
    case Some(RuntimeType.Function(_, _))     => 0
    case None                                 => throw IllegalArgumentException(s"SPIR-V type %$typeId is undefined")
  }

  private def structSize(program: DispatchProgram, structureType: Int, members: Col[Int]): Int = {
    if (program.packedStructures.contains(structureType))
      return members.foldLeft(0)((size, member) => Math.addExact(size, typeSize(program, member)))
    var offset = 0
    var align  = 1
    var index  = 0
    while (index < members.size) {
      val memberAlign = typeAlignment(program, members(index))
      offset = program.memberOffsets.getOrElse(structureType -> index, alignUp(offset, memberAlign))
      offset = Math.addExact(offset, typeSize(program, members(index)))
      align = math.max(align, memberAlign)
      index += 1
    }
    alignUp(offset, align)
  }

  private def structMemberOffset(
      program: DispatchProgram,
      structureType: Int,
      members: Col[Int],
      selected: Int
  ): Int =
    if (program.packedStructures.contains(structureType))
      members.take(selected).foldLeft(0)((size, member) => Math.addExact(size, typeSize(program, member)))
    else program.memberOffsets.get(structureType -> selected).getOrElse {
      var offset = 0
      var index  = 0
      while (index < selected) {
        offset = alignUp(offset, typeAlignment(program, members(index)))
        offset = Math.addExact(offset, typeSize(program, members(index)))
        index += 1
      }
      alignUp(offset, typeAlignment(program, members(selected)))
    }

  private def typeAlignment(program: DispatchProgram, typeId: Int): Int =
    program.typeAlignmentCache.getOrElseUpdate(typeId, typeAlignmentUncached(program, typeId))

  private def typeAlignmentUncached(program: DispatchProgram, typeId: Int): Int = program.types.get(typeId) match {
    case Some(RuntimeType.Structure(_)) if program.packedStructures.contains(typeId) => 1
    case Some(RuntimeType.Structure(members))           => members.map(typeAlignment(program, _)).maxOption.getOrElse(1)
    case Some(RuntimeType.Array(element, _))            => typeAlignment(program, element)
    case Some(RuntimeType.VectorType(component, count)) =>
      math.min(16, typeAlignment(program, component) * (if (count == 3) 4 else count))
    case Some(RuntimeType.Pointer(_, _)) => 8
    case _                               => math.max(1, typeSize(program, typeId))
  }

  private def scalarWidth(program: DispatchProgram, typeId: Int): Int = program.types.get(typeId) match {
    case Some(RuntimeType.Bool)                     => 8
    case Some(RuntimeType.Integer(width, _))        => width
    case Some(RuntimeType.Floating(width))          => width
    case Some(RuntimeType.Event)                    => 64
    case Some(RuntimeType.VectorType(component, _)) => scalarWidth(program, component)
    case value => throw IllegalArgumentException(s"SPIR-V type %$typeId is not scalar or vector: $value")
  }

  private def pointerPointee(program: DispatchProgram, typeId: Int): Int = program.types.get(typeId) match {
    case Some(RuntimeType.Pointer(_, pointee)) => pointee
    case value => throw IllegalArgumentException(s"SPIR-V type %$typeId is not a pointer: $value")
  }

  private def address(values: InvocationValues, operand: Operand): Either[ExecutionFailure, Pointer] =
    values.get(id(operand)) match {
      case Some(RuntimeValue.Address(_, value)) => Right(value)
      case Some(_) => Left(ExecutionFailure.InvalidRequest(s"SPIR-V value %${id(operand)} is not a pointer"))
      case None    => Left(undefined(operand))
    }

  /** Lifetime instructions are semantic markers, but accepting a malformed marker would hide a corrupt module. Resolve
    * the object identity and validate the literal byte range without reading external allocation contents.
    */
  private def validateLifetime(
      program: DispatchProgram,
      values: InvocationValues,
      opcode: String,
      pointerOperand: Operand,
      sizeOperand: Operand
  ): Either[ExecutionFailure, Unit] =
    (pointerOperand.words, sizeOperand.words) match {
      case (Col(pointerId), Col(sizeWord)) => values.get(pointerId.bits) match {
          case Some(RuntimeValue.Address(pointerType, pointer)) => program.types.get(pointerType) match {
              case Some(RuntimeType.Pointer(_, pointee)) if pointee == pointer.pointee =>
                val size = sizeWord.toUnsigned
                pointer.region match {
                  case Region.Internal(bytes) if size != 0L =>
                    val end = pointer.offset + size
                    Either.cond(
                      pointer.offset >= 0L && end >= pointer.offset && end <= bytes.length.toLong,
                      (),
                      ExecutionFailure.InvalidRequest(
                        s"$opcode byte range ${pointer.offset} + $size exceeds internal object of ${bytes.length} bytes"
                      )
                    )
                  case _ => Right(())
                }
              case Some(_) =>
                Left(ExecutionFailure.InvalidRequest(s"$opcode target %${pointerId.bits} does not have pointer type"))
              case None =>
                Left(ExecutionFailure.InvalidRequest(s"$opcode target %${pointerId.bits} has undefined pointer type"))
            }
          case Some(_) => Left(ExecutionFailure.InvalidRequest(s"$opcode target %${pointerId.bits} is not a pointer"))
          case None    => Left(ExecutionFailure.InvalidRequest(s"$opcode target %${pointerId.bits} is undefined"))
        }
      case (pointerWords, _) if pointerWords.size != 1 =>
        Left(ExecutionFailure.InvalidRequest(s"$opcode pointer must contain one word, found ${pointerWords.size}"))
      case (_, sizeWords) =>
        Left(ExecutionFailure.InvalidRequest(s"$opcode size must contain one word, found ${sizeWords.size}"))
    }

  private def bits(values: InvocationValues, operand: Operand): Either[ExecutionFailure, Array[Long]] =
    values.get(id(operand)) match {
      case Some(RuntimeValue.Bits(_, value)) => Right(value)
      case Some(_) => Left(ExecutionFailure.InvalidRequest(s"SPIR-V value %${id(operand)} is not numeric"))
      case None    => Left(undefined(operand))
    }

  private def scalar(values: InvocationValues, operand: Operand): Either[ExecutionFailure, Long] =
    if (values.scalarType(id(operand)) >= 0) Right(values.scalarBits(id(operand)))
    else
      bits(values, operand).flatMap {
        case Array(value) => Right(value)
        case value => Left(ExecutionFailure.InvalidRequest(s"SPIR-V value %${id(operand)} has ${value.length} lanes"))
      }

  private def scalarBits(value: RuntimeValue): Option[Long] = value match {
    case RuntimeValue.Bits(_, Array(result)) => Some(result)
    case _                                   => None
  }

  private def retag(value: RuntimeValue, typeId: Int): RuntimeValue = value match {
    case RuntimeValue.Bits(_, lanes)      => RuntimeValue.Bits(typeId, lanes.clone())
    case RuntimeValue.Address(_, pointer) => RuntimeValue.Address(typeId, pointer)
  }

  private def bitcast(
      program: DispatchProgram,
      sourceType: Int,
      resultType: Int,
      sourceLanes: Array[Long]
  ): Either[ExecutionFailure, Array[Long]] = {
    val sourceShape = numericShape(program, sourceType)
    val resultShape = numericShape(program, resultType)
    (sourceShape, resultShape) match {
      case (Some((sourceWidth, sourceCount)), Some((resultWidth, resultCount)))
          if sourceLanes.length == sourceCount && sourceWidth * sourceCount == resultWidth * resultCount =>
        val result     = Array.ofDim[Long](resultCount)
        var resultLane = 0
        while (resultLane < resultCount) {
          var bits = 0L
          var bit  = 0
          while (bit < resultWidth) {
            val sourceBit    = resultLane * resultWidth + bit
            val sourceLane   = sourceBit / sourceWidth
            val sourceOffset = sourceBit % sourceWidth
            if (((sourceLanes(sourceLane) >>> sourceOffset) & 1L) != 0L) bits |= 1L << bit
            bit += 1
          }
          result(resultLane) = bits & mask(resultWidth)
          resultLane += 1
        }
        Right(result)
      case (Some((sourceWidth, sourceCount)), Some((resultWidth, resultCount))) =>
        Left(
          ExecutionFailure.InvalidRequest(
            s"OpBitcast source %$sourceType has $sourceCount x $sourceWidth bits and result %$resultType " +
              s"has $resultCount x $resultWidth bits (value has ${sourceLanes.length} lanes)"
          )
        )
      case (None, _) =>
        Left(ExecutionFailure.InvalidRequest(s"OpBitcast source type %$sourceType is not scalar/vector numeric data"))
      case (_, None) =>
        Left(ExecutionFailure.InvalidRequest(s"OpBitcast result type %$resultType is not scalar/vector numeric data"))
    }
  }

  private def numericShape(program: DispatchProgram, typeId: Int): Option[(Int, Int)] =
    integerShape(program, typeId).orElse(floatingShape(program, typeId)).orElse {
      program.types.get(typeId) match {
        case Some(RuntimeType.Bool) => Some((8, 1))
        case Some(RuntimeType.VectorType(component, count))
            if program.types.get(component).contains(RuntimeType.Bool) => Some((8, count))
        case _ => None
      }
    }

  private def phiIncoming(
      incoming: Col[Operand],
      predecessor: Int
  ): Either[ExecutionFailure, Option[Int]] = {
    var selected = -1
    var index    = 0
    while (index < incoming.size) {
      val pair = incoming(index)
      pair.words match {
        case Col(value, parent) => if (parent.bits == predecessor) selected = value.bits
        case words              =>
          return Left(
            ExecutionFailure.InvalidRequest(
              s"OpPhi ${pair.kind} operand must contain a value and parent label, found ${words.size} words"
            )
          )
      }
      index += 1
    }
    Right(if (selected < 0) None else Some(selected))
  }

  private def convertSignedToFloat(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand
  ): Either[ExecutionFailure, Unit] = values.get(id(source)) match {
    case Some(RuntimeValue.Bits(sourceType, lanes)) =>
      (integerShape(program, sourceType), floatingShape(program, id(resultType))) match {
        case (Some((sourceWidth, sourceLanes)), Some((resultWidth, resultLanes))) if sourceLanes == resultLanes =>
          val converted = lanes.map { raw =>
            val signed = signExtend(raw, sourceWidth)
            resultWidth match {
              case 16 => IEEE754.floatToBinary16(signed.toFloat)
              case 32 => IEEE754.floatToBinary32(signed.toFloat)
              case 64 => IEEE754.doubleToBinary64(signed.toDouble)
            }
          }
          values.update(id(destination), RuntimeValue.Bits(id(resultType), converted))
          Right(())
        case (None, _) =>
          Left(
            ExecutionFailure.InvalidRequest(s"OpConvertSToF source %${id(source)} is not an integer scalar or vector")
          )
        case (_, None) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"OpConvertSToF result type %${id(resultType)} is not a floating-point scalar or vector"
            )
          )
        case (Some((_, sourceLanes)), Some((_, resultLanes))) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"OpConvertSToF cannot convert $sourceLanes source lanes to $resultLanes result lanes"
            )
          )
      }
    case Some(_) => Left(ExecutionFailure.InvalidRequest(s"OpConvertSToF source %${id(source)} is a pointer"))
    case None    => Left(undefined(source))
  }

  private def convertUnsignedToFloat(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand
  ): Either[ExecutionFailure, Unit] = values.get(id(source)) match {
    case Some(RuntimeValue.Bits(sourceType, lanes)) =>
      (integerShape(program, sourceType), floatingShape(program, id(resultType))) match {
        case (Some((sourceWidth, sourceLanes)), Some((resultWidth, resultLanes))) if sourceLanes == resultLanes =>
          val converted = lanes.map { raw =>
            val unsigned = sourceWidth match {
              case 64 if raw < 0L => (raw >>> 1).toDouble * 2.0 + (raw & 1L).toDouble
              case width          => (raw & mask(width)).toDouble
            }
            resultWidth match {
              case 16 => IEEE754.floatToBinary16(unsigned.toFloat)
              case 32 => IEEE754.floatToBinary32(unsigned.toFloat)
              case 64 => IEEE754.doubleToBinary64(unsigned)
            }
          }
          values.update(id(destination), RuntimeValue.Bits(id(resultType), converted))
          Right(())
        case (None, _) =>
          Left(
            ExecutionFailure.InvalidRequest(s"OpConvertUToF source %${id(source)} is not an integer scalar or vector")
          )
        case (_, None) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"OpConvertUToF result type %${id(resultType)} is not a floating-point scalar or vector"
            )
          )
        case (Some((_, sourceLanes)), Some((_, resultLanes))) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"OpConvertUToF cannot convert $sourceLanes source lanes to $resultLanes result lanes"
            )
          )
      }
    case Some(_) => Left(ExecutionFailure.InvalidRequest(s"OpConvertUToF source %${id(source)} is a pointer"))
    case None    => Left(undefined(source))
  }

  private def signedConvert(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand
  ): Either[ExecutionFailure, Unit] = values.get(id(source)) match {
    case Some(RuntimeValue.Bits(sourceType, lanes)) =>
      (integerShape(program, sourceType), integerShape(program, id(resultType))) match {
        case (Some((sourceWidth, sourceLanes)), Some((resultWidth, resultLanes))) if sourceLanes == resultLanes =>
          values.update(
            id(destination),
            RuntimeValue.Bits(id(resultType), lanes.map(value => signExtend(value, sourceWidth) & mask(resultWidth)))
          )
          Right(())
        case (None, _) =>
          Left(ExecutionFailure.InvalidRequest(s"OpSConvert source %${id(source)} is not an integer scalar or vector"))
        case (_, None) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"OpSConvert result type %${id(resultType)} is not an integer scalar or vector"
            )
          )
        case (Some((_, sourceLanes)), Some((_, resultLanes))) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"OpSConvert cannot convert $sourceLanes source lanes to $resultLanes result lanes"
            )
          )
      }
    case Some(_) => Left(ExecutionFailure.InvalidRequest(s"OpSConvert source %${id(source)} is a pointer"))
    case None    => Left(undefined(source))
  }

  private def floatingClassify(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand,
      classification: FloatingClassification
  ): Either[ExecutionFailure, Unit] = values.get(id(source)) match {
    case Some(RuntimeValue.Bits(sourceType, lanes)) =>
      (floatingShape(program, sourceType), booleanLanes(program, id(resultType))) match {
        case (Some((width, sourceLanes)), Some(resultLanes)) if sourceLanes == resultLanes =>
          val result = lanes.map { raw =>
            val (signMask, exponentMask, fractionMask) = width match {
              case 16 => (0x8000L, 0x7c00L, 0x03ffL)
              case 32 => (0x80000000L, 0x7f800000L, 0x007fffffL)
              case 64 => (0x8000000000000000L, 0x7ff0000000000000L, 0x000fffffffffffffL)
            }
            val exponentSet  = (raw & exponentMask) == exponentMask
            val exponentZero = (raw & exponentMask) == 0L
            val fractionSet  = (raw & fractionMask) != 0L
            val matches      = classification match {
              case FloatingClassification.Infinite => exponentSet && !fractionSet
              case FloatingClassification.NaN      => exponentSet && fractionSet
              case FloatingClassification.Finite   => !exponentSet
              case FloatingClassification.Normal   => !exponentSet && !exponentZero
              case FloatingClassification.Signed   => (raw & signMask) != 0L
            }
            if (matches) 1L else 0L
          }
          values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
          Right(())
        case (None, _) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"${classification.opcode} source %${id(source)} is not a floating-point scalar or vector"
            )
          )
        case (_, None) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"${classification.opcode} result type %${id(resultType)} is not a boolean scalar or vector"
            )
          )
        case (Some((_, sourceLanes)), Some(resultLanes)) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"${classification.opcode} cannot classify $sourceLanes lanes into $resultLanes lanes"
            )
          )
      }
    case Some(_) => Left(ExecutionFailure.InvalidRequest(s"${classification.opcode} source is a pointer"))
    case None    => Left(undefined(source))
  }

  private def floatingNegate(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand
  ): Either[ExecutionFailure, Unit] = values.get(id(source)) match {
    case Some(RuntimeValue.Bits(sourceType, lanes)) =>
      (floatingShape(program, sourceType), floatingShape(program, id(resultType))) match {
        case (Some(sourceShape @ (width, _)), Some(resultShape)) if sourceShape == resultShape =>
          val signMask = width match {
            case 16 => 0x8000L
            case 32 => 0x80000000L
            case 64 => 0x8000000000000000L
          }
          values.update(id(destination), RuntimeValue.Bits(id(resultType), lanes.map(_ ^ signMask)))
          Right(())
        case _ =>
          Left(
            ExecutionFailure.InvalidRequest(
              "OpFNegate requires matching floating-point scalar/vector operand and result types"
            )
          )
      }
    case Some(_) => Left(ExecutionFailure.InvalidRequest("OpFNegate source is a pointer"))
    case None    => Left(undefined(source))
  }

  private def floatingBinary(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      left: Operand,
      right: Operand,
      operation: FloatingOperation
  ): Either[ExecutionFailure, Unit] = (values.get(id(left)), values.get(id(right))) match {
    case (Some(RuntimeValue.Bits(leftType, leftLanes)), Some(RuntimeValue.Bits(rightType, rightLanes))) =>
      (
        floatingShape(program, id(resultType)),
        floatingShape(program, leftType),
        floatingShape(program, rightType)
      ) match {
        case (Some((width, count)), Some((leftWidth, leftCount)), Some((rightWidth, rightCount)))
            if width == leftWidth && width == rightWidth && count == leftCount && count == rightCount =>
          val result = Array.tabulate(count) { index =>
            width match {
              case 16 =>
                val a = IEEE754.binary16ToFloat(leftLanes(index))
                val b = IEEE754.binary16ToFloat(rightLanes(index))
                IEEE754.floatToBinary16(
                  operation match {
                    case FloatingOperation.Add      => a + b
                    case FloatingOperation.Subtract => a - b
                    case FloatingOperation.Multiply => a * b
                    case FloatingOperation.Divide   => a / b
                    case FloatingOperation.Minimum  => floatingMinimum(a, b)
                    case FloatingOperation.Maximum  => floatingMaximum(a, b)
                  }
                )
              case 32 =>
                val a = IEEE754.binary32ToFloat(leftLanes(index))
                val b = IEEE754.binary32ToFloat(rightLanes(index))
                IEEE754.floatToBinary32(
                  operation match {
                    case FloatingOperation.Add      => a + b
                    case FloatingOperation.Subtract => a - b
                    case FloatingOperation.Multiply => a * b
                    case FloatingOperation.Divide   => a / b
                    case FloatingOperation.Minimum  => floatingMinimum(a, b)
                    case FloatingOperation.Maximum  => floatingMaximum(a, b)
                  }
                )
              case 64 =>
                val a = IEEE754.binary64ToDouble(leftLanes(index))
                val b = IEEE754.binary64ToDouble(rightLanes(index))
                IEEE754.doubleToBinary64(
                  operation match {
                    case FloatingOperation.Add      => a + b
                    case FloatingOperation.Subtract => a - b
                    case FloatingOperation.Multiply => a * b
                    case FloatingOperation.Divide   => a / b
                    case FloatingOperation.Minimum  => floatingMinimum(a, b)
                    case FloatingOperation.Maximum  => floatingMaximum(a, b)
                  }
                )
            }
          }
          values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
          Right(())
        case (None, _, _) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"${operation.opcode} result type %${id(resultType)} is not a floating-point scalar or vector"
            )
          )
        case (_, None, _) =>
          Left(ExecutionFailure.InvalidRequest(s"${operation.opcode} left operand %${id(left)} is not floating-point"))
        case (_, _, None) =>
          Left(
            ExecutionFailure.InvalidRequest(s"${operation.opcode} right operand %${id(right)} is not floating-point")
          )
        case (Some(result), Some(leftShape), Some(rightShape)) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"${operation.opcode} type shapes do not match: result $result, left $leftShape, right $rightShape"
            )
          )
      }
    case (Some(RuntimeValue.Address(_, _)), _) =>
      Left(ExecutionFailure.InvalidRequest(s"${operation.opcode} left operand %${id(left)} is a pointer"))
    case (_, Some(RuntimeValue.Address(_, _))) =>
      Left(ExecutionFailure.InvalidRequest(s"${operation.opcode} right operand %${id(right)} is a pointer"))
    case (None, _) => Left(undefined(left))
    case (_, None) => Left(undefined(right))
  }

  private def convertFloating(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand
  ): Either[ExecutionFailure, Unit] = values.get(id(source)) match {
    case Some(RuntimeValue.Bits(sourceType, lanes)) =>
      (floatingShape(program, sourceType), floatingShape(program, id(resultType))) match {
        case (Some((sourceWidth, sourceLanes)), Some((resultWidth, resultLanes)))
            if sourceLanes == resultLanes && lanes.length == sourceLanes =>
          val converted = lanes.map { raw =>
            val value = sourceWidth match {
              case 16 => IEEE754.binary16ToFloat(raw).toDouble
              case 32 => IEEE754.binary32ToFloat(raw).toDouble
              case 64 => IEEE754.binary64ToDouble(raw)
            }
            resultWidth match {
              case 16 => IEEE754.floatToBinary16(value.toFloat).toLong
              case 32 => IEEE754.floatToBinary32(value.toFloat).toLong & 0xffffffffL
              case 64 => IEEE754.doubleToBinary64(value)
            }
          }
          values.update(id(destination), RuntimeValue.Bits(id(resultType), converted))
          Right(())
        case (None, _) =>
          Left(ExecutionFailure.InvalidRequest(
            s"OpFConvert source %${id(source)} is not a floating-point scalar or vector"
          ))
        case (_, None) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"OpFConvert result type %${id(resultType)} is not a floating-point scalar or vector"
            )
          )
        case (Some((_, sourceLanes)), Some((_, resultLanes))) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"OpFConvert cannot convert $sourceLanes source lanes to $resultLanes result lanes"
            )
          )
      }
    case Some(_) => Left(ExecutionFailure.InvalidRequest(s"OpFConvert source %${id(source)} is a pointer"))
    case None    => Left(undefined(source))
  }

  private def convertFloatToSigned(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand
  ): Either[ExecutionFailure, Unit] = values.get(id(source)) match {
    case Some(RuntimeValue.Bits(sourceType, lanes)) =>
      (floatingShape(program, sourceType), integerShape(program, id(resultType))) match {
        case (Some((sourceWidth, sourceLanes)), Some((resultWidth, resultLanes))) if sourceLanes == resultLanes =>
          val converted = lanes.map { raw =>
            val floating = sourceWidth match {
              case 16 => IEEE754.binary16ToFloat(raw).toDouble
              case 32 => IEEE754.binary32ToFloat(raw).toDouble
              case 64 => IEEE754.binary64ToDouble(raw)
            }
            val signed =
              if (program.saturatedConversions.contains(id(destination))) {
                val minimum = if (resultWidth == 64) Long.MinValue else -(1L << (resultWidth - 1))
                val maximum = if (resultWidth == 64) Long.MaxValue else (1L << (resultWidth - 1)) - 1L
                if (floating.isNaN) minimum
                else if (floating <= minimum.toDouble) minimum
                else if (floating >= maximum.toDouble) maximum
                else floating.toLong
              } else floating.toLong
            signed & mask(resultWidth)
          }
          values.update(id(destination), RuntimeValue.Bits(id(resultType), converted))
          Right(())
        case (None, _) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"OpConvertFToS source %${id(source)} is not a floating-point scalar or vector"
            )
          )
        case (_, None) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"OpConvertFToS result type %${id(resultType)} is not an integer scalar or vector"
            )
          )
        case (Some((_, sourceLanes)), Some((_, resultLanes))) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"OpConvertFToS cannot convert $sourceLanes source lanes to $resultLanes result lanes"
            )
          )
      }
    case Some(_) => Left(ExecutionFailure.InvalidRequest(s"OpConvertFToS source %${id(source)} is a pointer"))
    case None    => Left(undefined(source))
  }

  private def convertFloatToUnsigned(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand
  ): Either[ExecutionFailure, Unit] = values.get(id(source)) match {
    case Some(RuntimeValue.Bits(sourceType, lanes)) =>
      (floatingShape(program, sourceType), integerShape(program, id(resultType))) match {
        case (Some((sourceWidth, sourceLanes)), Some((resultWidth, resultLanes))) if sourceLanes == resultLanes =>
          val converted = Array.ofDim[Long](lanes.length)
          var index     = 0
          while (index < lanes.length) {
            floatingToUnsigned(lanes(index), sourceWidth, resultWidth) match {
              case Left(detail) =>
                return Left(
                  ExecutionFailure.InvalidRequest(
                    s"OpConvertFToU source lane $index cannot be represented as an unsigned $resultWidth-bit integer: $detail"
                  )
                )
              case Right(value) => converted(index) = value
            }
            index += 1
          }
          values.update(id(destination), RuntimeValue.Bits(id(resultType), converted))
          Right(())
        case (None, _) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"OpConvertFToU source %${id(source)} is not a floating-point scalar or vector"
            )
          )
        case (_, None) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"OpConvertFToU result type %${id(resultType)} is not an integer scalar or vector"
            )
          )
        case (Some((_, sourceLanes)), Some((_, resultLanes))) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"OpConvertFToU cannot convert $sourceLanes source lanes to $resultLanes result lanes"
            )
          )
      }
    case Some(_) => Left(ExecutionFailure.InvalidRequest(s"OpConvertFToU source %${id(source)} is a pointer"))
    case None    => Left(undefined(source))
  }

  /** Converts an IEEE-754 bit pattern by truncating toward zero without first narrowing through a signed Long. */
  private def floatingToUnsigned(raw: Long, sourceWidth: Int, resultWidth: Int): Either[String, Long] = {
    val (exponentBits, fractionBits, bias) = sourceWidth match {
      case 16 => (5, 10, 15)
      case 32 => (8, 23, 127)
      case 64 => (11, 52, 1023)
    }
    val bits            = raw & mask(sourceWidth)
    val fractionMask    = (1L << fractionBits) - 1L
    val exponentMask    = (1L << exponentBits) - 1L
    val fraction        = bits & fractionMask
    val encodedExponent = (bits >>> fractionBits) & exponentMask
    val negative        = ((bits >>> (sourceWidth - 1)) & 1L) != 0L
    val magnitudeIsZero = encodedExponent == 0L && fraction == 0L

    if (encodedExponent == exponentMask) Left("NaN and infinity are not integers")
    else if (negative && !magnitudeIsZero) Left("negative values are outside the unsigned range")
    else if (encodedExponent == 0L) Right(0L)
    else {
      val exponent    = encodedExponent.toInt - bias
      val significand = BigInt((1L << fractionBits) | fraction)
      val integer     =
        if (exponent >= fractionBits) significand << (exponent - fractionBits)
        else significand >> (fractionBits - exponent)
      if (integer.bitLength > resultWidth) Left("value exceeds the result width")
      else Right(integer.longValue & mask(resultWidth))
    }
  }

  private def integerEquality(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      left: Operand,
      right: Operand,
      opcode: String
  )(predicate: (Long, Long) => Boolean): Either[ExecutionFailure, Unit] = {
    val leftId    = id(left)
    val rightId   = id(right)
    val leftType  = values.scalarType(leftId)
    val rightType = values.scalarType(rightId)
    (
      Option.when(leftType >= 0)(leftType).flatMap(integerShape(program, _)),
      Option.when(rightType >= 0)(rightType).flatMap(integerShape(program, _)),
      booleanLanes(program, id(resultType))
    ) match {
      case (Some(leftShape @ (_, 1)), Some(rightShape), Some(1)) if leftShape == rightShape =>
        values.updateScalar(
          id(destination),
          id(resultType),
          if (predicate(values.scalarBits(leftId), values.scalarBits(rightId))) 1L else 0L
        )
        Right(())
      case _ => (values.get(leftId), values.get(rightId)) match {
          case (
                Some(RuntimeValue.Bits(leftRuntimeType, leftLanes)),
                Some(RuntimeValue.Bits(rightRuntimeType, rightLanes))
              ) =>
            (
              integerShape(program, leftRuntimeType),
              integerShape(program, rightRuntimeType),
              booleanLanes(program, id(resultType))
            ) match {
              case (Some(leftShape), Some(rightShape), Some(resultLanes))
                  if leftShape == rightShape && leftShape._2 == resultLanes &&
                    leftLanes.length == resultLanes && rightLanes.length == resultLanes =>
                values.update(
                  id(destination),
                  RuntimeValue.Bits(
                    id(resultType),
                    Array.tabulate(resultLanes)(index => if (predicate(leftLanes(index), rightLanes(index))) 1L else 0L)
                  )
                )
                Right(())
              case (None, _, _) =>
                Left(
                  ExecutionFailure.InvalidRequest(
                    s"$opcode left operand %$leftId is not an integer scalar or vector"
                  )
                )
              case (_, None, _) =>
                Left(
                  ExecutionFailure.InvalidRequest(
                    s"$opcode right operand %$rightId is not an integer scalar or vector"
                  )
                )
              case (_, _, None) =>
                Left(
                  ExecutionFailure.InvalidRequest(
                    s"$opcode result type %${id(resultType)} is not a boolean scalar or vector"
                  )
                )
              case (Some(leftShape), Some(rightShape), Some(resultLanes)) =>
                Left(
                  ExecutionFailure.InvalidRequest(
                    s"$opcode type shapes do not match: left $leftShape, right $rightShape, result lanes $resultLanes"
                  )
                )
            }
          case (Some(RuntimeValue.Address(_, _)), _) =>
            Left(ExecutionFailure.InvalidRequest(s"$opcode left operand %$leftId is a pointer"))
          case (_, Some(RuntimeValue.Address(_, _))) =>
            Left(ExecutionFailure.InvalidRequest(s"$opcode right operand %$rightId is a pointer"))
          case (None, _) => Left(undefined(left))
          case (_, None) => Left(undefined(right))
        }
    }
  }

  private def integerBitwise(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      left: Operand,
      right: Operand,
      opcode: String
  )(operation: (Long, Long) => Long): Either[ExecutionFailure, Unit] = {
    val resultId = id(resultType)
    val leftId   = id(left)
    val rightId  = id(right)
    integerShape(program, resultId) match {
      case None =>
        Left(ExecutionFailure.InvalidRequest(s"$opcode result type %$resultId is not an integer scalar or vector"))
      case Some((width, count)) =>
        val leftType  = values.scalarType(leftId)
        val rightType = values.scalarType(rightId)
        if (count == 1 && leftType == resultId && rightType == resultId) {
          values.updateScalar(
            id(destination),
            resultId,
            operation(values.scalarBits(leftId), values.scalarBits(rightId)) & mask(width)
          )
          Right(())
        } else
          (values.get(leftId), values.get(rightId)) match {
            case (
                  Some(RuntimeValue.Bits(leftRuntimeType, leftLanes)),
                  Some(RuntimeValue.Bits(rightRuntimeType, rightLanes))
                )
                if leftRuntimeType == resultId && rightRuntimeType == resultId &&
                  leftLanes.length == count && rightLanes.length == count =>
              values.update(
                id(destination),
                RuntimeValue.Bits(
                  resultId,
                  Array.tabulate(count)(index => operation(leftLanes(index), rightLanes(index)) & mask(width))
                )
              )
              Right(())
            case (None, _) => Left(undefined(left))
            case (_, None) => Left(undefined(right))
            case _         =>
              Left(
                ExecutionFailure.InvalidRequest(
                  s"$opcode requires matching integer scalar or vector operand and result types"
                )
              )
          }
    }
  }

  private def integerNot(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand
  ): Either[ExecutionFailure, Unit] = {
    val resultId = id(resultType)
    val sourceId = id(source)
    integerShape(program, resultId) match {
      case None =>
        Left(ExecutionFailure.InvalidRequest(s"OpNot result type %$resultId is not an integer scalar or vector"))
      case Some((width, count)) if count == 1 && values.scalarType(sourceId) == resultId =>
        values.updateScalar(id(destination), resultId, ~values.scalarBits(sourceId) & mask(width))
        Right(())
      case Some((width, count)) =>
        values.get(sourceId) match {
          case Some(RuntimeValue.Bits(runtimeType, lanes)) if runtimeType == resultId && lanes.length == count =>
            values.update(id(destination), RuntimeValue.Bits(resultId, lanes.map(value => ~value & mask(width))))
            Right(())
          case Some(_) =>
            Left(
              ExecutionFailure.InvalidRequest(
                "OpNot requires matching integer scalar or vector operand and result types"
              )
            )
          case None => Left(undefined(source))
        }
    }
  }

  private def integerBitCount(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      base: Operand
  ): Either[ExecutionFailure, Unit] = {
    val resultId = id(resultType)
    val baseId   = id(base)
    integerShape(program, resultId) match {
      case None =>
        Left(ExecutionFailure.InvalidRequest(s"OpBitCount result type %$resultId is not an integer scalar or vector"))
      case Some((width, count)) =>
        val operation: Long => Long = value =>
          if (width <= 32) java.lang.Integer.bitCount((value & mask(width)).toInt).toLong
          else java.lang.Long.bitCount(value & mask(width)).toLong
        if (count == 1 && values.scalarType(baseId) == resultId) {
          values.updateScalar(id(destination), resultId, operation(values.scalarBits(baseId)) & mask(width))
          Right(())
        } else
          values.get(baseId) match {
            case Some(RuntimeValue.Bits(baseType, lanes))
                if baseType == resultId && lanes.length == count =>
              values.update(
                id(destination),
                RuntimeValue.Bits(resultId, lanes.map(value => operation(value) & mask(width)))
              )
              Right(())
            case Some(_) =>
              Left(ExecutionFailure.InvalidRequest("OpBitCount requires a matching integer scalar or vector base"))
            case None => Left(undefined(base))
          }
    }
  }

  private def logicalBinary(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      left: Operand,
      right: Operand,
      opcode: String
  )(predicate: (Boolean, Boolean) => Boolean): Either[ExecutionFailure, Unit] = {
    val leftId    = id(left)
    val rightId   = id(right)
    val leftType  = values.scalarType(leftId)
    val rightType = values.scalarType(rightId)
    if (
      leftType >= 0 && rightType >= 0 && booleanLanes(program, leftType).contains(1) &&
      booleanLanes(program, rightType).contains(1) && booleanLanes(program, id(resultType)).contains(1)
    ) {
      val result = predicate(values.scalarBits(leftId) != 0L, values.scalarBits(rightId) != 0L)
      values.updateScalar(id(destination), id(resultType), if (result) 1L else 0L)
      Right(())
    } else
      (values.get(leftId), values.get(rightId)) match {
        case (
              Some(RuntimeValue.Bits(leftRuntimeType, leftLanes)),
              Some(RuntimeValue.Bits(rightRuntimeType, rightLanes))
            ) =>
          (
            booleanLanes(program, leftRuntimeType),
            booleanLanes(program, rightRuntimeType),
            booleanLanes(program, id(resultType))
          ) match {
            case (Some(leftCount), Some(rightCount), Some(resultCount))
                if leftCount == rightCount && leftCount == resultCount &&
                  leftLanes.length == leftCount && rightLanes.length == rightCount =>
              values.update(
                id(destination),
                RuntimeValue.Bits(
                  id(resultType),
                  Array.tabulate(resultCount)(index =>
                    if (predicate(leftLanes(index) != 0L, rightLanes(index) != 0L)) 1L else 0L
                  )
                )
              )
              Right(())
            case _ =>
              Left(
                ExecutionFailure.InvalidRequest(
                  s"$opcode requires matching boolean scalar or vector operand and result types"
                )
              )
          }
        case (Some(_), Some(_)) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"$opcode requires matching boolean scalar or vector operand and result types"
            )
          )
        case (None, _) => Left(undefined(left))
        case (_, None) => Left(undefined(right))
      }
  }

  private def floatingDot(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      left: Operand,
      right: Operand
  ): Either[ExecutionFailure, Unit] = (values.get(id(left)), values.get(id(right))) match {
    case (Some(RuntimeValue.Bits(leftType, leftLanes)), Some(RuntimeValue.Bits(rightType, rightLanes))) =>
      (
        floatingShape(program, leftType),
        floatingShape(program, rightType),
        floatingShape(program, id(resultType))
      ) match {
        case (Some((width, count)), Some((rightWidth, rightCount)), Some((resultWidth, 1)))
            if width == rightWidth && width == resultWidth && count == rightCount &&
              leftLanes.length == count && rightLanes.length == count =>
          var accumulated = doubleToFloating(width, 0.0)
          var lane        = 0
          while (lane < count) {
            accumulated = floatingAddBits(
              width,
              accumulated,
              floatingMultiplyBits(width, leftLanes(lane), rightLanes(lane))
            )
            lane += 1
          }
          values.update(id(destination), RuntimeValue.Bits(id(resultType), Array(accumulated)))
          Right(())
        case _ => Left(ExecutionFailure.InvalidRequest("OpDot requires matching floating vectors and a scalar result"))
      }
    case (None, _) => Left(undefined(left))
    case (_, None) => Left(undefined(right))
    case _         => Left(ExecutionFailure.InvalidRequest("OpDot operands are not floating-point vectors"))
  }

  private def logicalReduction(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand,
      all: Boolean
  ): Either[ExecutionFailure, Unit] = values.get(id(source)) match {
    case Some(RuntimeValue.Bits(sourceType, lanes)) =>
      (booleanLanes(program, sourceType), booleanLanes(program, id(resultType))) match {
        case (Some(count), Some(1)) if lanes.length == count =>
          val result = if (all) lanes.forall(_ != 0L) else lanes.exists(_ != 0L)
          values.updateScalar(id(destination), id(resultType), if (result) 1L else 0L)
          Right(())
        case _ => Left(ExecutionFailure.InvalidRequest(s"Op${if (all) "All" else "Any"} requires a boolean vector"))
      }
    case Some(_) => Left(ExecutionFailure.InvalidRequest(s"Op${if (all) "All" else "Any"} source is a pointer"))
    case None    => Left(undefined(source))
  }

  private def logicalNot(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand
  ): Either[ExecutionFailure, Unit] = {
    val sourceId   = id(source)
    val sourceType = values.scalarType(sourceId)
    if (
      sourceType >= 0 && booleanLanes(program, sourceType).contains(1) &&
      booleanLanes(program, id(resultType)).contains(1)
    ) {
      values.updateScalar(id(destination), id(resultType), if (values.scalarBits(sourceId) == 0L) 1L else 0L)
      Right(())
    } else
      values.get(sourceId) match {
        case Some(RuntimeValue.Bits(runtimeType, lanes))
            if booleanLanes(program, runtimeType).contains(lanes.length) &&
              booleanLanes(program, id(resultType)).contains(lanes.length) =>
          values.update(
            id(destination),
            RuntimeValue.Bits(id(resultType), lanes.map(value => if (value == 0L) 1L else 0L))
          )
          Right(())
        case Some(_) =>
          Left(ExecutionFailure.InvalidRequest("OpLogicalNot requires matching boolean operand and result types"))
        case None => Left(undefined(source))
      }
  }

  private enum SignedIntegerOperation(val opcode: String) {
    case Divide    extends SignedIntegerOperation("OpSDiv")
    case Remainder extends SignedIntegerOperation("OpSRem")
  }

  private enum UnsignedIntegerOperation(val opcode: String) {
    case Divide    extends UnsignedIntegerOperation("OpUDiv")
    case Remainder extends UnsignedIntegerOperation("OpUMod")
  }

  private def unsignedIntegerArithmetic(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      left: Operand,
      right: Operand,
      operation: UnsignedIntegerOperation
  ): Either[ExecutionFailure, Unit] = {
    val leftValue  = values.get(id(left))
    val rightValue = values.get(id(right))
    (leftValue, rightValue) match {
      case (Some(RuntimeValue.Bits(leftType, leftLanes)), Some(RuntimeValue.Bits(rightType, rightLanes))) =>
        (
          integerShape(program, id(resultType)),
          integerShape(program, leftType),
          integerShape(program, rightType)
        ) match {
          case (
                Some((width, count)),
                Some((leftWidth, leftCount)),
                Some((rightWidth, rightCount))
              )
              if width == leftWidth && width == rightWidth && count == leftCount && count == rightCount &&
                leftLanes.length == count && rightLanes.length == count =>
            val result = Array.ofDim[Long](count)
            var index  = 0
            while (index < count) {
              val dividend = leftLanes(index) & mask(width)
              val divisor  = rightLanes(index) & mask(width)
              if (divisor == 0L)
                return Left(ExecutionFailure.InvalidRequest(s"${operation.opcode} divisor lane $index is zero"))
              result(index) =
                (operation match {
                  case UnsignedIntegerOperation.Divide    => java.lang.Long.divideUnsigned(dividend, divisor)
                  case UnsignedIntegerOperation.Remainder => java.lang.Long.remainderUnsigned(dividend, divisor)
                }) & mask(width)
              index += 1
            }
            values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
            Right(())
          case (None, _, _) =>
            Left(
              ExecutionFailure.InvalidRequest(
                s"${operation.opcode} result type %${id(resultType)} is not an integer scalar or vector"
              )
            )
          case (_, None, _) =>
            Left(
              ExecutionFailure.InvalidRequest(
                s"${operation.opcode} left operand %${id(left)} is not an integer scalar or vector"
              )
            )
          case (_, _, None) =>
            Left(
              ExecutionFailure.InvalidRequest(
                s"${operation.opcode} right operand %${id(right)} is not an integer scalar or vector"
              )
            )
          case (Some(resultShape), Some(leftShape), Some(rightShape)) =>
            Left(
              ExecutionFailure.InvalidRequest(
                s"${operation.opcode} type shapes do not match: result $resultShape, left $leftShape, right $rightShape"
              )
            )
        }
      case (Some(RuntimeValue.Address(_, _)), _) =>
        Left(ExecutionFailure.InvalidRequest(s"${operation.opcode} left operand %${id(left)} is a pointer"))
      case (_, Some(RuntimeValue.Address(_, _))) =>
        Left(ExecutionFailure.InvalidRequest(s"${operation.opcode} right operand %${id(right)} is a pointer"))
      case (None, _) => Left(undefined(left))
      case (_, None) => Left(undefined(right))
    }
  }

  private enum AtomicIntegerOperation(val opcode: String) {
    case Increment       extends AtomicIntegerOperation("OpAtomicIIncrement")
    case Decrement       extends AtomicIntegerOperation("OpAtomicIDecrement")
    case Add             extends AtomicIntegerOperation("OpAtomicIAdd")
    case Subtract        extends AtomicIntegerOperation("OpAtomicISub")
    case SignedMinimum   extends AtomicIntegerOperation("OpAtomicSMin")
    case UnsignedMinimum extends AtomicIntegerOperation("OpAtomicUMin")
    case SignedMaximum   extends AtomicIntegerOperation("OpAtomicSMax")
    case UnsignedMaximum extends AtomicIntegerOperation("OpAtomicUMax")
    case And             extends AtomicIntegerOperation("OpAtomicAnd")
    case Or              extends AtomicIntegerOperation("OpAtomicOr")
    case Xor             extends AtomicIntegerOperation("OpAtomicXor")
  }

  private def atomicStore(
      program: DispatchProgram,
      values: InvocationValues,
      memory: MemoryState,
      pointer: Operand,
      scope: Operand,
      semantics: Operand,
      value: Operand
  ): Either[ExecutionFailure, Unit] = {
    val opcode    = "OpAtomicStore"
    val valueId   = id(value)
    val valueType = values.scalarType(valueId)
    val shape     = Option.when(valueType >= 0)(valueType).flatMap(atomicScalarShape(program, _)) match {
      case Some(result @ (width, 1)) if width == 32 || width == 64 => result
      case _                                                       =>
        return Left(
          ExecutionFailure.InvalidRequest(s"$opcode requires a 32- or 64-bit scalar value")
        )
    }
    val target = address(values, pointer) match {
      case Left(error)  => return Left(error)
      case Right(value) => value
    }
    if (target.pointee != valueType)
      return Left(
        ExecutionFailure.InvalidRequest(
          s"$opcode pointer pointee %${target.pointee} does not match value type %$valueType"
        )
      )
    if (!Col(4, 5, 8, 10, 11, 12, 5349).contains(target.storageClass))
      return Left(
        ExecutionFailure.InvalidRequest(
          s"$opcode pointer storage class ${target.storageClass} does not support atomic access"
        )
      )
    validateAtomicControls(values, scope, semantics, opcode).flatMap { _ =>
      val width = shape._1
      memory.writeScalar(target, values.scalarBits(valueId) & mask(width), width / 8)
    }
  }

  private def atomicLoad(
      program: DispatchProgram,
      values: InvocationValues,
      memory: MemoryState,
      resultType: Operand,
      destination: Operand,
      pointer: Operand,
      scope: Operand,
      semantics: Operand
  ): Either[ExecutionFailure, Unit] = {
    val opcode       = "OpAtomicLoad"
    val resultTypeId = id(resultType)
    val shape        = atomicScalarShape(program, resultTypeId) match {
      case Some(result @ (width, 1)) if width == 32 || width == 64 => result
      case _                                                       =>
        return Left(ExecutionFailure.InvalidRequest(s"$opcode requires a 32- or 64-bit scalar result"))
    }
    val target = address(values, pointer) match {
      case Left(error)  => return Left(error)
      case Right(value) => value
    }
    if (target.pointee != resultTypeId)
      return Left(
        ExecutionFailure.InvalidRequest(
          s"$opcode pointer pointee %${target.pointee} does not match result type %$resultTypeId"
        )
      )
    if (!Col(4, 5, 8, 10, 11, 12, 5349).contains(target.storageClass))
      return Left(
        ExecutionFailure.InvalidRequest(
          s"$opcode pointer storage class ${target.storageClass} does not support atomic access"
        )
      )
    validateAtomicControls(values, scope, semantics, opcode).flatMap { _ =>
      memory.readScalar(target, shape._1 / 8).map(value =>
        values.updateScalar(id(destination), resultTypeId, value & mask(shape._1))
      )
    }
  }

  private def atomicExchange(
      program: DispatchProgram,
      values: InvocationValues,
      memory: MemoryState,
      resultType: Operand,
      destination: Operand,
      pointer: Operand,
      scope: Operand,
      semantics: Operand,
      value: Operand
  ): Either[ExecutionFailure, Unit] = {
    val opcode       = "OpAtomicExchange"
    val resultTypeId = id(resultType)
    val valueId      = id(value)
    val shape        = atomicScalarShape(program, resultTypeId) match {
      case Some(result @ (width, 1)) if width == 32 || width == 64 => result
      case _                                                       =>
        return Left(ExecutionFailure.InvalidRequest(s"$opcode requires a 32- or 64-bit scalar result"))
    }
    if (values.scalarType(valueId) != resultTypeId)
      return Left(ExecutionFailure.InvalidRequest(s"$opcode requires matching result and value types"))
    val target = address(values, pointer) match {
      case Left(error)  => return Left(error)
      case Right(value) => value
    }
    if (target.pointee != resultTypeId)
      return Left(ExecutionFailure.InvalidRequest(s"$opcode pointer pointee does not match result type"))
    if (!Col(4, 5, 8, 10, 11, 12, 5349).contains(target.storageClass))
      return Left(ExecutionFailure.InvalidRequest(s"$opcode pointer storage class does not support atomic access"))
    validateAtomicControls(values, scope, semantics, opcode).flatMap { _ =>
      memory.atomic {
        memory.readScalar(target, shape._1 / 8).flatMap { previous =>
          memory.writeScalar(target, values.scalarBits(valueId) & mask(shape._1), shape._1 / 8).map { _ =>
            values.updateScalar(id(destination), resultTypeId, previous & mask(shape._1))
          }
        }
      }
    }
  }

  private def atomicCompareExchange(
      program: DispatchProgram,
      values: InvocationValues,
      memory: MemoryState,
      resultType: Operand,
      destination: Operand,
      pointer: Operand,
      scope: Operand,
      equal: Operand,
      unequal: Operand,
      value: Operand,
      comparator: Operand
  ): Either[ExecutionFailure, Unit] = {
    val opcode       = "OpAtomicCompareExchange"
    val resultTypeId = id(resultType)
    val valueId      = id(value)
    val comparatorId = id(comparator)
    val shape        = atomicScalarShape(program, resultTypeId) match {
      case Some(result @ (width, 1)) if width == 32 || width == 64 => result
      case _                                                       =>
        return Left(ExecutionFailure.InvalidRequest(s"$opcode requires a 32- or 64-bit scalar result"))
    }
    if (values.scalarType(valueId) != resultTypeId || values.scalarType(comparatorId) != resultTypeId)
      return Left(ExecutionFailure.InvalidRequest(s"$opcode requires matching result, value, and comparator types"))
    val target = address(values, pointer) match {
      case Left(error)  => return Left(error)
      case Right(value) => value
    }
    if (target.pointee != resultTypeId)
      return Left(ExecutionFailure.InvalidRequest(s"$opcode pointer pointee does not match result type"))
    if (!Col(4, 5, 8, 10, 11, 12, 5349).contains(target.storageClass))
      return Left(ExecutionFailure.InvalidRequest(s"$opcode pointer storage class does not support atomic access"))
    for {
      _      <- validateAtomicControls(values, scope, equal, opcode)
      _      <- validateAtomicControls(values, scope, unequal, opcode)
      result <- memory.atomic {
        for {
          previous <- memory.readScalar(target, shape._1 / 8)
          _        <- if ((previous & mask(shape._1)) == (values.scalarBits(comparatorId) & mask(shape._1)))
            memory.writeScalar(target, values.scalarBits(valueId) & mask(shape._1), shape._1 / 8)
          else Right(())
        } yield previous
      }
    } yield values.updateScalar(id(destination), resultTypeId, result & mask(shape._1))
  }

  private def validateAtomicControls(
      values: InvocationValues,
      scope: Operand,
      semantics: Operand,
      opcode: String
  ): Either[ExecutionFailure, Unit] =
    for {
      memoryScope <- scalar(values, scope)
      _           <- Either.cond(
        memoryScope >= 0L && memoryScope <= 6L,
        (),
        ExecutionFailure.InvalidRequest(s"$opcode has invalid memory scope $memoryScope")
      )
      memorySemantics <- scalar(values, semantics)
      knownSemantics = 0xffdeL
      _ <- Either.cond(
        (memorySemantics & ~knownSemantics) == 0L,
        (),
        ExecutionFailure.InvalidRequest(
          s"$opcode has unknown memory-semantics bits 0x${java.lang.Long.toHexString(memorySemantics)}"
        )
      )
    } yield ()

  private def atomicScalarShape(program: DispatchProgram, typeId: Int): Option[(Int, Int)] =
    integerShape(program, typeId).orElse(floatingShape(program, typeId))

  private def atomicInteger(
      program: DispatchProgram,
      values: InvocationValues,
      memory: MemoryState,
      resultType: Operand,
      destination: Operand,
      pointer: Operand,
      scope: Operand,
      semantics: Operand,
      value: Option[Operand],
      operation: AtomicIntegerOperation
  ): Either[ExecutionFailure, Unit] = {
    val resultTypeId = id(resultType)
    val valueId      = value.map(id)
    val shape        = integerShape(program, resultTypeId) match {
      case Some(result @ (width, 1)) if width == 32 || width == 64 => result
      case _                                                       =>
        return Left(
          ExecutionFailure.InvalidRequest(
            s"${operation.opcode} requires a 32- or 64-bit scalar integer result"
          )
        )
    }
    valueId match {
      case Some(sourceId) if values.scalarType(sourceId) != resultTypeId =>
        return Left(
          ExecutionFailure.InvalidRequest(
            s"${operation.opcode} requires matching result and value types"
          )
        )
      case _ => ()
    }
    val target = address(values, pointer) match {
      case Left(error)  => return Left(error)
      case Right(value) => value
    }
    if (target.pointee != resultTypeId)
      return Left(
        ExecutionFailure.InvalidRequest(
          s"${operation.opcode} pointer pointee %${target.pointee} does not match result type %$resultTypeId"
        )
      )
    validateAtomicControls(values, scope, semantics, operation.opcode) match {
      case Left(error) => return Left(error)
      case Right(())   => ()
    }
    val width    = shape._1
    val byteSize = width / 8
    memory.atomic {
      memory.readScalar(target, byteSize).flatMap { previous =>
        val operand = valueId.fold(1L)(values.scalarBits) & mask(width)
        val updated = operation match {
          case AtomicIntegerOperation.Increment     => (previous + 1L) & mask(width)
          case AtomicIntegerOperation.Decrement     => (previous - 1L) & mask(width)
          case AtomicIntegerOperation.Add           => (previous + operand) & mask(width)
          case AtomicIntegerOperation.Subtract      => (previous - operand) & mask(width)
          case AtomicIntegerOperation.SignedMinimum =>
            if (signExtend(previous, width) > signExtend(operand, width)) operand else previous
          case AtomicIntegerOperation.UnsignedMinimum =>
            if (width == 64 && java.lang.Long.compareUnsigned(previous, operand) > 0) operand
            else if (width < 64 && previous > operand) operand
            else previous
          case AtomicIntegerOperation.SignedMaximum =>
            if (signExtend(previous, width) < signExtend(operand, width)) operand else previous
          case AtomicIntegerOperation.UnsignedMaximum =>
            if (width == 64 && java.lang.Long.compareUnsigned(previous, operand) < 0) operand
            else if (width < 64 && previous < operand) operand
            else previous
          case AtomicIntegerOperation.And => previous & operand
          case AtomicIntegerOperation.Or  => previous | operand
          case AtomicIntegerOperation.Xor => previous ^ operand
        }
        memory.writeScalar(target, updated, byteSize).map { _ =>
          values.updateScalar(id(destination), resultTypeId, previous & mask(width))
        }
      }
    }
  }

  private def atomicFloatingAdd(
      program: DispatchProgram,
      values: InvocationValues,
      memory: MemoryState,
      resultType: Operand,
      destination: Operand,
      pointer: Operand,
      scope: Operand,
      semantics: Operand,
      value: Operand
  ): Either[ExecutionFailure, Unit] = {
    val opcode       = "OpAtomicFAddEXT"
    val resultTypeId = id(resultType)
    val valueId      = id(value)
    val shape        = floatingShape(program, resultTypeId) match {
      case Some(result @ (width, 1)) if width == 16 || width == 32 || width == 64 => result
      case _                                                                      =>
        return Left(
          ExecutionFailure.InvalidRequest(s"$opcode requires a 16-, 32-, or 64-bit scalar floating-point result")
        )
    }
    if (values.scalarType(valueId) != resultTypeId)
      return Left(ExecutionFailure.InvalidRequest(s"$opcode requires matching result and value types"))
    val target = address(values, pointer) match {
      case Left(error)  => return Left(error)
      case Right(value) => value
    }
    if (target.pointee != resultTypeId)
      return Left(ExecutionFailure.InvalidRequest(s"$opcode pointer pointee does not match result type"))
    if (!Col(4, 5, 8, 10, 11, 12, 5349).contains(target.storageClass))
      return Left(ExecutionFailure.InvalidRequest(s"$opcode pointer storage class does not support atomic access"))
    validateAtomicControls(values, scope, semantics, opcode).flatMap { _ =>
      val width    = shape._1
      val byteSize = width / 8
      memory.atomic {
        memory.readScalar(target, byteSize).flatMap { previous =>
          val updated = floatingAddBits(width, previous, values.scalarBits(valueId)) & mask(width)
          memory.writeScalar(target, updated, byteSize).map { _ =>
            values.updateScalar(id(destination), resultTypeId, previous & mask(width))
          }
        }
      }
    }
  }

  private def atomicFloatingMinMax(
      program: DispatchProgram,
      values: InvocationValues,
      memory: MemoryState,
      resultType: Operand,
      destination: Operand,
      pointer: Operand,
      scope: Operand,
      semantics: Operand,
      value: Operand,
      maximum: Boolean
  ): Either[ExecutionFailure, Unit] = {
    val opcode       = if (maximum) "OpAtomicFMaxEXT" else "OpAtomicFMinEXT"
    val resultTypeId = id(resultType)
    val valueId      = id(value)
    val shape        = floatingShape(program, resultTypeId) match {
      case Some(result @ (width, 1)) if width == 16 || width == 32 || width == 64 => result
      case _                                                                      =>
        return Left(
          ExecutionFailure.InvalidRequest(s"$opcode requires a 16-, 32-, or 64-bit scalar floating-point result")
        )
    }
    if (values.scalarType(valueId) != resultTypeId)
      return Left(ExecutionFailure.InvalidRequest(s"$opcode requires matching result and value types"))
    val target = address(values, pointer) match {
      case Left(error)  => return Left(error)
      case Right(value) => value
    }
    if (target.pointee != resultTypeId)
      return Left(ExecutionFailure.InvalidRequest(s"$opcode pointer pointee does not match result type"))
    if (!Col(4, 5, 8, 10, 11, 12, 5349).contains(target.storageClass))
      return Left(ExecutionFailure.InvalidRequest(s"$opcode pointer storage class does not support atomic access"))
    validateAtomicControls(values, scope, semantics, opcode).flatMap { _ =>
      val width    = shape._1
      val byteSize = width / 8
      memory.atomic {
        memory.readScalar(target, byteSize).flatMap { previous =>
          val operand = values.scalarBits(valueId)
          val updated =
            if (maximum) floatingMaximumBits(width, previous, operand)
            else floatingMinimumBits(width, previous, operand)
          memory.writeScalar(target, updated & mask(width), byteSize).map { _ =>
            values.updateScalar(id(destination), resultTypeId, previous & mask(width))
          }
        }
      }
    }
  }

  private def signedIntegerArithmetic(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      left: Operand,
      right: Operand,
      operation: SignedIntegerOperation
  ): Either[ExecutionFailure, Unit] = {
    val leftId          = id(left)
    val rightId         = id(right)
    val scalarLeftType  = values.scalarType(leftId)
    val scalarRightType = values.scalarType(rightId)
    if (scalarLeftType >= 0 && scalarRightType >= 0)
      (
        integerShape(program, id(resultType)),
        integerShape(program, scalarLeftType),
        integerShape(program, scalarRightType)
      ) match {
        case (Some((width, 1)), Some((leftWidth, 1)), Some((rightWidth, 1)))
            if width == leftWidth && width == rightWidth =>
          val divisor = signExtend(values.scalarBits(rightId), width)
          if (divisor == 0L)
            Left(ExecutionFailure.InvalidRequest(s"${operation.opcode} divisor lane 0 is zero"))
          else {
            val dividend = signExtend(values.scalarBits(leftId), width)
            val minimum  = if (width == 64) Long.MinValue else -(1L << (width - 1))
            if (dividend == minimum && divisor == -1L)
              Left(
                ExecutionFailure.InvalidRequest(
                  s"${operation.opcode} lane 0 has the undefined minimum-signed-value divided by -1 case"
                )
              )
            else {
              val result = operation match {
                case SignedIntegerOperation.Divide    => dividend / divisor
                case SignedIntegerOperation.Remainder => dividend % divisor
              }
              values.updateScalar(id(destination), id(resultType), result & mask(width))
              Right(())
            }
          }
        case (None, _, _) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"${operation.opcode} result type %${id(resultType)} is not an integer scalar or vector"
            )
          )
        case (_, None, _) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"${operation.opcode} left operand %${id(left)} is not an integer scalar or vector"
            )
          )
        case (_, _, None) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"${operation.opcode} right operand %${id(right)} is not an integer scalar or vector"
            )
          )
        case (Some(resultShape), Some(leftShape), Some(rightShape)) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"${operation.opcode} type shapes do not match: result $resultShape, left $leftShape, right $rightShape"
            )
          )
      }
    else
      (values.get(leftId), values.get(rightId)) match {
        case (
              Some(RuntimeValue.Bits(leftType, Array(leftValue))),
              Some(RuntimeValue.Bits(rightType, Array(rightValue)))
            ) =>
          (
            integerShape(program, id(resultType)),
            integerShape(program, leftType),
            integerShape(program, rightType)
          ) match {
            case (Some((width, 1)), Some((leftWidth, 1)), Some((rightWidth, 1)))
                if width == leftWidth && width == rightWidth =>
              val divisor = signExtend(rightValue, width)
              if (divisor == 0L)
                Left(ExecutionFailure.InvalidRequest(s"${operation.opcode} divisor lane 0 is zero"))
              else {
                val dividend = signExtend(leftValue, width)
                val minimum  = if (width == 64) Long.MinValue else -(1L << (width - 1))
                if (dividend == minimum && divisor == -1L)
                  Left(
                    ExecutionFailure.InvalidRequest(
                      s"${operation.opcode} lane 0 has the undefined minimum-signed-value divided by -1 case"
                    )
                  )
                else {
                  val result = operation match {
                    case SignedIntegerOperation.Divide    => dividend / divisor
                    case SignedIntegerOperation.Remainder => dividend % divisor
                  }
                  values.update(id(destination), RuntimeValue.Bits(id(resultType), Array(result & mask(width))))
                  Right(())
                }
              }
            case (None, _, _) =>
              Left(
                ExecutionFailure.InvalidRequest(
                  s"${operation.opcode} result type %${id(resultType)} is not an integer scalar or vector"
                )
              )
            case (_, None, _) =>
              Left(
                ExecutionFailure.InvalidRequest(
                  s"${operation.opcode} left operand %${id(left)} is not an integer scalar or vector"
                )
              )
            case (_, _, None) =>
              Left(
                ExecutionFailure.InvalidRequest(
                  s"${operation.opcode} right operand %${id(right)} is not an integer scalar or vector"
                )
              )
            case (Some(resultShape), Some(leftShape), Some(rightShape)) =>
              Left(
                ExecutionFailure.InvalidRequest(
                  s"${operation.opcode} type shapes do not match: result $resultShape, left $leftShape, right $rightShape"
                )
              )
          }
        case (Some(RuntimeValue.Bits(leftType, leftLanes)), Some(RuntimeValue.Bits(rightType, rightLanes))) =>
          (
            integerShape(program, id(resultType)),
            integerShape(program, leftType),
            integerShape(program, rightType)
          ) match {
            case (Some(resultShape), Some(leftShape), Some(rightShape))
                if resultShape == leftShape && resultShape == rightShape =>
              val (width, count) = resultShape
              val result         = Array.ofDim[Long](count)
              var index          = 0
              while (index < count) {
                val divisor = signExtend(rightLanes(index), width)
                if (divisor == 0L)
                  return Left(ExecutionFailure.InvalidRequest(s"${operation.opcode} divisor lane $index is zero"))
                val dividend = signExtend(leftLanes(index), width)
                val minimum  = if (width == 64) Long.MinValue else -(1L << (width - 1))
                if (dividend == minimum && divisor == -1L)
                  return Left(
                    ExecutionFailure.InvalidRequest(
                      s"${operation.opcode} lane $index has the undefined minimum-signed-value divided by -1 case"
                    )
                  )
                result(index) =
                  (operation match {
                    case SignedIntegerOperation.Divide    => dividend / divisor
                    case SignedIntegerOperation.Remainder => dividend % divisor
                  }) & mask(width)
                index += 1
              }
              values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
              Right(())
            case (None, _, _) =>
              Left(
                ExecutionFailure.InvalidRequest(
                  s"${operation.opcode} result type %${id(resultType)} is not an integer scalar or vector"
                )
              )
            case (_, None, _) =>
              Left(
                ExecutionFailure.InvalidRequest(
                  s"${operation.opcode} left operand %${id(left)} is not an integer scalar or vector"
                )
              )
            case (_, _, None) =>
              Left(
                ExecutionFailure.InvalidRequest(
                  s"${operation.opcode} right operand %${id(right)} is not an integer scalar or vector"
                )
              )
            case (Some(resultShape), Some(leftShape), Some(rightShape)) =>
              Left(
                ExecutionFailure.InvalidRequest(
                  s"${operation.opcode} type shapes do not match: result $resultShape, left $leftShape, right $rightShape"
                )
              )
          }
        case (Some(RuntimeValue.Address(_, _)), _) =>
          Left(ExecutionFailure.InvalidRequest(s"${operation.opcode} left operand %${id(left)} is a pointer"))
        case (_, Some(RuntimeValue.Address(_, _))) =>
          Left(ExecutionFailure.InvalidRequest(s"${operation.opcode} right operand %${id(right)} is a pointer"))
        case (None, _) => Left(undefined(left))
        case (_, None) => Left(undefined(right))
      }
  }

  private def floatingCompare(
      program: DispatchProgram,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      left: Operand,
      right: Operand,
      operation: FloatingComparison
  ): Either[ExecutionFailure, Unit] = (values.get(id(left)), values.get(id(right))) match {
    case (Some(RuntimeValue.Bits(leftType, leftLanes)), Some(RuntimeValue.Bits(rightType, rightLanes))) =>
      (
        floatingShape(program, leftType),
        floatingShape(program, rightType),
        booleanLanes(program, id(resultType))
      ) match {
        case (Some(leftShape), Some(rightShape), Some(resultLanes))
            if leftShape == rightShape && leftShape._2 == resultLanes &&
              leftLanes.length == resultLanes && rightLanes.length == resultLanes =>
          val width  = leftShape._1
          val result = Array.tabulate(resultLanes) { index =>
            val comparison = width match {
              case 16 =>
                val a = IEEE754.binary16ToFloat(leftLanes(index))
                val b = IEEE754.binary16ToFloat(rightLanes(index))
                operation match {
                  case FloatingComparison.OrderedEqual              => a == b
                  case FloatingComparison.OrderedNotEqual           => !a.isNaN && !b.isNaN && a != b
                  case FloatingComparison.Ordered                   => !a.isNaN && !b.isNaN
                  case FloatingComparison.UnorderedEqual            => a == b || a.isNaN || b.isNaN
                  case FloatingComparison.UnorderedNotEqual         => a != b
                  case FloatingComparison.UnorderedLessThan         => a < b || a.isNaN || b.isNaN
                  case FloatingComparison.UnorderedGreaterThan      => a > b || a.isNaN || b.isNaN
                  case FloatingComparison.UnorderedLessThanEqual    => a <= b || a.isNaN || b.isNaN
                  case FloatingComparison.UnorderedGreaterThanEqual => a >= b || a.isNaN || b.isNaN
                  case FloatingComparison.Unordered                 => a.isNaN || b.isNaN
                  case FloatingComparison.OrderedLessThan           => a < b
                  case FloatingComparison.OrderedLessThanEqual      => a <= b
                  case FloatingComparison.OrderedGreaterThan        => a > b
                  case FloatingComparison.OrderedGreaterThanEqual   => a >= b
                }
              case 32 =>
                val a = IEEE754.binary32ToFloat(leftLanes(index))
                val b = IEEE754.binary32ToFloat(rightLanes(index))
                operation match {
                  case FloatingComparison.OrderedEqual              => a == b
                  case FloatingComparison.OrderedNotEqual           => !a.isNaN && !b.isNaN && a != b
                  case FloatingComparison.Ordered                   => !a.isNaN && !b.isNaN
                  case FloatingComparison.UnorderedEqual            => a == b || a.isNaN || b.isNaN
                  case FloatingComparison.UnorderedNotEqual         => a != b
                  case FloatingComparison.UnorderedLessThan         => a < b || a.isNaN || b.isNaN
                  case FloatingComparison.UnorderedGreaterThan      => a > b || a.isNaN || b.isNaN
                  case FloatingComparison.UnorderedLessThanEqual    => a <= b || a.isNaN || b.isNaN
                  case FloatingComparison.UnorderedGreaterThanEqual => a >= b || a.isNaN || b.isNaN
                  case FloatingComparison.Unordered                 => a.isNaN || b.isNaN
                  case FloatingComparison.OrderedLessThan           => a < b
                  case FloatingComparison.OrderedLessThanEqual      => a <= b
                  case FloatingComparison.OrderedGreaterThan        => a > b
                  case FloatingComparison.OrderedGreaterThanEqual   => a >= b
                }
              case 64 =>
                val a = IEEE754.binary64ToDouble(leftLanes(index))
                val b = IEEE754.binary64ToDouble(rightLanes(index))
                operation match {
                  case FloatingComparison.OrderedEqual              => a == b
                  case FloatingComparison.OrderedNotEqual           => !a.isNaN && !b.isNaN && a != b
                  case FloatingComparison.Ordered                   => !a.isNaN && !b.isNaN
                  case FloatingComparison.UnorderedEqual            => a == b || a.isNaN || b.isNaN
                  case FloatingComparison.UnorderedNotEqual         => a != b
                  case FloatingComparison.UnorderedLessThan         => a < b || a.isNaN || b.isNaN
                  case FloatingComparison.UnorderedGreaterThan      => a > b || a.isNaN || b.isNaN
                  case FloatingComparison.UnorderedLessThanEqual    => a <= b || a.isNaN || b.isNaN
                  case FloatingComparison.UnorderedGreaterThanEqual => a >= b || a.isNaN || b.isNaN
                  case FloatingComparison.Unordered                 => a.isNaN || b.isNaN
                  case FloatingComparison.OrderedLessThan           => a < b
                  case FloatingComparison.OrderedLessThanEqual      => a <= b
                  case FloatingComparison.OrderedGreaterThan        => a > b
                  case FloatingComparison.OrderedGreaterThanEqual   => a >= b
                }
            }
            if (comparison) 1L else 0L
          }
          values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
          Right(())
        case (None, _, _) =>
          Left(ExecutionFailure.InvalidRequest(s"${operation.opcode} left operand %${id(left)} is not floating-point"))
        case (_, None, _) =>
          Left(
            ExecutionFailure.InvalidRequest(s"${operation.opcode} right operand %${id(right)} is not floating-point")
          )
        case (_, _, None) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"${operation.opcode} result type %${id(resultType)} is not a boolean scalar or vector"
            )
          )
        case (Some(leftShape), Some(rightShape), Some(resultLanes)) =>
          Left(
            ExecutionFailure.InvalidRequest(
              s"${operation.opcode} %${id(destination)} operands %${id(left)}/%${id(right)} " +
                s"type shapes/lane counts do not match: left $leftShape/${leftLanes.length}, " +
                s"right $rightShape/${rightLanes.length}, result lanes $resultLanes"
            )
          )
      }
    case (Some(RuntimeValue.Address(_, _)), _) =>
      Left(ExecutionFailure.InvalidRequest(s"${operation.opcode} left operand %${id(left)} is a pointer"))
    case (_, Some(RuntimeValue.Address(_, _))) =>
      Left(ExecutionFailure.InvalidRequest(s"${operation.opcode} right operand %${id(right)} is a pointer"))
    case (None, _) => Left(undefined(left))
    case (_, None) => Left(undefined(right))
  }

  private def integerShape(program: DispatchProgram, typeId: Int): Option[(Int, Int)] =
    program.types.get(typeId) match {
      case Some(RuntimeType.Integer(width, _))            => Some((width, 1))
      case Some(RuntimeType.VectorType(component, count)) => program.types.get(component) match {
          case Some(RuntimeType.Integer(width, _)) => Some((width, count))
          case _                                   => None
        }
      case _ => None
    }

  private def floatingShape(program: DispatchProgram, typeId: Int): Option[(Int, Int)] =
    program.types.get(typeId) match {
      case Some(RuntimeType.Floating(width))              => Some((width, 1))
      case Some(RuntimeType.VectorType(component, count)) => program.types.get(component) match {
          case Some(RuntimeType.Floating(width)) => Some((width, count))
          case _                                 => None
        }
      case _ => None
    }

  private[v1_6] def floatingAddBits(width: Int, left: Long, right: Long): Long = width match {
    case 16 =>
      IEEE754.floatToBinary16(IEEE754.binary16ToFloat(left) + IEEE754.binary16ToFloat(right))
    case 32 =>
      IEEE754.floatToBinary32(IEEE754.binary32ToFloat(left) + IEEE754.binary32ToFloat(right))
    case 64 =>
      IEEE754.doubleToBinary64(IEEE754.binary64ToDouble(left) + IEEE754.binary64ToDouble(right))
  }

  private def floatingOneBits(width: Int): Long = width match {
    case 16 => IEEE754.floatToBinary16(1.0f).toLong
    case 32 => IEEE754.floatToBinary32(1.0f).toLong & 0xffffffffL
    case 64 => IEEE754.doubleToBinary64(1.0)
  }

  private def floatingInfinityBits(width: Int, negative: Boolean): Long = width match {
    case 16 => if (negative) 0xfc00L else 0x7c00L
    case 32 => if (negative) 0xff800000L else 0x7f800000L
    case 64 => if (negative) 0xfff0000000000000L else 0x7ff0000000000000L
  }

  private[v1_6] def floatingMultiplyBits(width: Int, left: Long, right: Long): Long = width match {
    case 16 =>
      IEEE754.floatToBinary16(IEEE754.binary16ToFloat(left) * IEEE754.binary16ToFloat(right))
    case 32 =>
      IEEE754.floatToBinary32(IEEE754.binary32ToFloat(left) * IEEE754.binary32ToFloat(right))
    case 64 =>
      IEEE754.doubleToBinary64(IEEE754.binary64ToDouble(left) * IEEE754.binary64ToDouble(right))
  }

  private[v1_6] def floatingMinimumBits(width: Int, left: Long, right: Long): Long = width match {
    case 16 => IEEE754.floatToBinary16(floatingMinimum(IEEE754.binary16ToFloat(left), IEEE754.binary16ToFloat(right)))
    case 32 => IEEE754.floatToBinary32(floatingMinimum(IEEE754.binary32ToFloat(left), IEEE754.binary32ToFloat(right)))
    case 64 =>
      IEEE754.doubleToBinary64(floatingMinimum(IEEE754.binary64ToDouble(left), IEEE754.binary64ToDouble(right)))
  }

  private[v1_6] def floatingMaximumBits(width: Int, left: Long, right: Long): Long = width match {
    case 16 => IEEE754.floatToBinary16(floatingMaximum(IEEE754.binary16ToFloat(left), IEEE754.binary16ToFloat(right)))
    case 32 => IEEE754.floatToBinary32(floatingMaximum(IEEE754.binary32ToFloat(left), IEEE754.binary32ToFloat(right)))
    case 64 =>
      IEEE754.doubleToBinary64(floatingMaximum(IEEE754.binary64ToDouble(left), IEEE754.binary64ToDouble(right)))
  }

  private def floatingMinimum(left: Float, right: Float): Float =
    if (left.isNaN) right
    else if (right.isNaN) left
    else if (left == right) {
      if (
        left == 0.0f &&
        ((java.lang.Float.floatToRawIntBits(left) | java.lang.Float.floatToRawIntBits(right)) < 0)
      )
        java.lang.Float.intBitsToFloat(0x80000000)
      else left
    } else if (left < right) left else right

  private def floatingMaximum(left: Float, right: Float): Float =
    if (left.isNaN) right
    else if (right.isNaN) left
    else if (left == right) {
      if (
        left == 0.0f &&
        ((java.lang.Float.floatToRawIntBits(left) & java.lang.Float.floatToRawIntBits(right)) >= 0)
      )
        0.0f
      else left
    } else if (left > right) left else right

  private def floatingMinimum(left: Double, right: Double): Double =
    if (left.isNaN) right
    else if (right.isNaN) left
    else if (left == right) {
      if (
        left == 0.0d &&
        ((java.lang.Double.doubleToRawLongBits(left) | java.lang.Double.doubleToRawLongBits(right)) < 0L)
      )
        java.lang.Double.longBitsToDouble(0x8000000000000000L)
      else left
    } else if (left < right) left else right

  private def floatingMaximum(left: Double, right: Double): Double =
    if (left.isNaN) right
    else if (right.isNaN) left
    else if (left == right) {
      if (
        left == 0.0d &&
        ((java.lang.Double.doubleToRawLongBits(left) & java.lang.Double.doubleToRawLongBits(right)) >= 0L)
      )
        0.0d
      else left
    } else if (left > right) left else right

  /** Intel's bfloat16 device library uses the high sixteen bits of an IEEE-754 binary32 value. */
  private def float32ToBfloat16Bits(value: Long): Long = {
    val bits    = value.toInt
    val rounded = bits + (0x7fff + ((bits >>> 16) & 1))
    (rounded >>> 16).toLong & 0xffffL
  }

  private def bfloat16ToFloat32Bits(value: Long): Long = (value & 0xffffL) << 16

  private def booleanLanes(program: DispatchProgram, typeId: Int): Option[Int] = program.types.get(typeId) match {
    case Some(RuntimeType.Bool) => Some(1)
    case Some(RuntimeType.VectorType(component, count)) if program.types.get(component).contains(RuntimeType.Bool) =>
      Some(count)
    case _ => None
  }

  private def label(function: FunctionInfo, operand: Operand): Int =
    function.labels.getOrElse(
      id(operand),
      throw IllegalArgumentException(s"SPIR-V label %${id(operand)} is absent in function %${function.id}")
    )

  private def id(operand: Operand): Int        = operand.word.bits
  private def unsigned(operand: Operand): Long = operand.words.head.toUnsigned

  private def literalBits(operand: Operand): Long = {
    var result = 0L
    var index  = 0
    while (index < operand.words.size && index < 2) {
      result |= operand.words(index).toUnsigned << (index * 32)
      index += 1
    }
    result
  }

  private def literalString(operand: Operand): String = {
    val bytes = operand.words.flatMap { word =>
      LittleEndian.toCol(word.bits.toLong, 4)
    }.takeWhile(_ != 0)
    String(bytes.toArray, java.nio.charset.StandardCharsets.UTF_8)
  }

  private def alignUp(value: Int, alignment: Int): Int =
    Math.multiplyExact((value + alignment - 1) / alignment, alignment)

  private def withExecutionContext(
      error: ExecutionFailure,
      function: FunctionInfo,
      pc: Int,
      instruction: Instruction
  ): ExecutionFailure = error match {
    case ExecutionFailure.InvalidRequest(message) if message.startsWith("cannot write through a null SPIR-V pointer") =>
      ExecutionFailure.InvalidRequest(s"$message at function %${function.id}, pc $pc (${instruction.opcode})")
    case _ => error
  }

  private def checkedSize(size: Long): Int = {
    if (size < 0L || size > Int.MaxValue.toLong) throw IllegalArgumentException(s"invalid local size $size")
    size.toInt
  }
  private def undefined(operand: Operand): ExecutionFailure.InvalidRequest =
    ExecutionFailure.InvalidRequest(s"SPIR-V value %${id(operand)} is undefined")
}
