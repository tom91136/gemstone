package net.kurobako.gemstone.isa.nvidia

import cats.syntax.all.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Bits.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.memory.*
import scala.collection.mutable

object VMBlockState {
  import VMIR.*

  @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
  private class Lane(registerChunkCount: VMRegisterFile.ChunkCount) {
    val registers: VMRegisterFile                                  = VMRegisterFile(registerChunkCount)
    val predicates: VMPredicateFile                                = VMPredicateFile()
    val parameters: mutable.Map[NonEmpty[String], Long]            = mutable.Map.empty
    val parameterBytes: mutable.Map[NonEmpty[String], Array[Byte]] = mutable.Map.empty
    private var codes: VMConditionCodes | Null                     = null
    private var returns: mutable.ArrayBuffer[Int] | Null           = null
    private var localBytes: mutable.LongMap[Byte] | Null           = null

    def conditionCodes: VMConditionCodes = codes match {
      case null =>
        val allocated = VMConditionCodes()
        codes = allocated
        allocated
      case existing => existing
    }

    def sassReturns: mutable.ArrayBuffer[Int] = returns match {
      case null =>
        val allocated = mutable.ArrayBuffer.empty[Int]
        returns = allocated
        allocated
      case existing => existing
    }

    def local: mutable.LongMap[Byte] = localBytes match {
      case null =>
        val allocated = mutable.LongMap.empty[Byte]
        localBytes = allocated
        allocated
      case existing => existing
    }

    def clear(): Unit = {
      registers.clear()
      predicates.clear()
      parameters.clear()
      parameterBytes.clear()
      codes match {
        case null     => ()
        case existing => existing.clear()
      }
      returns match {
        case null     => ()
        case existing => existing.clear()
      }
      localBytes match {
        case null     => ()
        case existing => existing.clear()
      }
    }
  }

  /** The write semantics shared by block execution and device-function calls. */
  object WriteBuffer {

    private def validateMemoryRange(
        memory: AddressMemory,
        pc: Pc,
        lane: WorkgroupLane,
        address: GuestAddress,
        size: Int >= 0
    ): Either[VMFault, Unit] =
      memory.validateRange(address, size).leftMap(error =>
        VMFault.fromMemory(error, s"pc $pc, lane $lane: ")
      )

    def validateWrite(memory: AddressMemory, pc: Pc)(write: VMWrite): Either[VMFault, Unit] =
      write match {
        case VMWrite.MemoryVector(lane, address, width, values) =>
          val size = values.nonNegativeSize * width.byteCount
          validateMemoryRange(memory, pc, lane, address, size)
        case VMWrite.Memory(lane, address, width, _) =>
          validateMemoryRange(memory, pc, lane, address, width.byteCount)
        case VMWrite.SystemMemory(lane, address, width, _) =>
          validateMemoryRange(memory, pc, lane, address, width.byteCount)
        case VMWrite.AtomicMemoryAdd(lane, _, address, width, _) =>
          validateMemoryRange(memory, pc, lane, address, width.byteCount)
        case VMWrite.AtomicMemoryCompareExchange(lane, _, address, width, _, _) =>
          validateMemoryRange(memory, pc, lane, address, width.byteCount)
        case VMWrite.AtomicMemoryExchange(lane, _, address, width, _) =>
          validateMemoryRange(memory, pc, lane, address, width.byteCount)
        case VMWrite.AtomicMemoryBinary(lane, _, address, width, _, _) =>
          validateMemoryRange(memory, pc, lane, address, width.byteCount)
        case VMWrite.AtomicMemoryAddFloat(lane, _, address, width, _) =>
          validateMemoryRange(memory, pc, lane, address, width.byteCount)
        case _ => Right(())
      }

    def applyWrite(state: VMBlockState, memory: AddressMemory)(write: VMWrite): Unit = write match {
      case VMWrite.Register(lane, name, value)      => state.registers(lane)(name) = value
      case VMWrite.Carry(lane, value)               => state.carry(lane.raw) = value
      case VMWrite.ConditionFlag(lane, flag, value) => state.conditionCodes(lane).write(flag, value)
      case VMWrite.AddThreeCarry(lane, value)       => state.conditionCodes(lane).writeThreeInputCarry(value)
      case VMWrite.Predicate(lane, name, value)     => state.predicates(lane)(name) = value
      case VMWrite.Parameter(lane, name, value)     => state.parameters(lane)(name) = value
      case VMWrite.ParameterBytes(lane, name, offset, width, value) =>
        state.writeParameterBytes(lane, name, offset, width, value)
      case VMWrite.ParameterBuffer(address, width, value) =>
        state.parameterBufferRange(address, width.byteCount.widen[Int >= 0]).foreach { case (bytes, offset) =>
          LittleEndian.write(value, bytes, offset.raw, width.byteCount)
        }
      case VMWrite.Shared(_, address, width, value)        => state.writeShared(address, width, value)
      case VMWrite.Local(lane, address, width, value)      => state.writeLocal(lane, address, width, value)
      case VMWrite.MemoryVector(_, address, width, values) =>
        memory.writeBytes(address, values.flatMap(value => LittleEndian.toVec(value, width.byteCount)))
          .left.foreach(error => throw IllegalStateException(error.message))
      case VMWrite.Memory(_, address, width, value) =>
        memory.writeLittleEndian(address, value, width.byteCount).left.foreach(error =>
          throw IllegalStateException(error.message)
        )
      case VMWrite.SystemMemory(_, address, width, value) =>
        memory.writeSystemLittleEndian(address, value, width.byteCount).left.foreach(error =>
          throw IllegalStateException(error.message)
        )
      case VMWrite.AtomicSharedAdd(lane, destination, address, width, value) =>
        val previous = state.readShared(address, width)
        state.registers(lane)(destination) = Bits.truncate(previous, width)
        state.writeShared(address, width, previous + value)
      case VMWrite.AtomicSharedCompareExchange(lane, destination, address, width, comparator, value) =>
        val previous = Bits.truncate(state.readShared(address, width), width)
        state.registers(lane)(destination) = previous
        if (previous == Bits.truncate(comparator, width)) state.writeShared(address, width, value)
      case VMWrite.AtomicSharedExchange(lane, destination, address, width, value) =>
        val previous = state.readShared(address, width)
        state.registers(lane)(destination) = Bits.truncate(previous, width)
        state.writeShared(address, width, value)
      case VMWrite.AtomicSharedBinary(lane, destination, address, width, value, operation) =>
        val previous = state.readShared(address, width)
        state.registers(lane)(destination) = Bits.truncate(previous, width)
        state.writeShared(address, width, atomicBinaryResult(operation, previous, value, width))
      case VMWrite.AtomicMemoryAdd(lane, destination, address, width, value) =>
        val previous = memory.atomicModifyLittleEndian(address, width.byteCount)(_ + value).fold(
          error => throw IllegalStateException(error.message),
          value => value
        )
        state.registers(lane)(destination) = Bits.truncate(previous, width)
      case VMWrite.AtomicMemoryCompareExchange(lane, destination, address, width, comparator, value) =>
        val previous = memory.atomicModifyLittleEndian(address, width.byteCount) { current =>
          if (Bits.truncate(current, width) == Bits.truncate(comparator, width)) value else current
        }.fold(error => throw IllegalStateException(error.message), value => value)
        state.registers(lane)(destination) = Bits.truncate(previous, width)
      case VMWrite.AtomicMemoryExchange(lane, destination, address, width, value) =>
        val previous = memory.atomicModifyLittleEndian(address, width.byteCount)(_ => value).fold(
          error => throw IllegalStateException(error.message),
          value => value
        )
        state.registers(lane)(destination) = Bits.truncate(previous, width)
      case VMWrite.AtomicMemoryBinary(lane, destination, address, width, value, operation) =>
        val previous = memory.atomicModifyLittleEndian(address, width.byteCount) { current =>
          atomicBinaryResult(operation, current, value, width)
        }.fold(error => throw IllegalStateException(error.message), value => value)
        state.registers(lane)(destination) = Bits.truncate(previous, width)
      case VMWrite.AtomicMemoryAddFloat(lane, destination, address, width, value) =>
        val previous = memory.atomicModifyLittleEndian(address, width.byteCount) { current =>
          if (width.raw == 32)
            IEEE754.floatToBinary32(
              IEEE754.binary32ToFloat(Binary32.low(current)) +
                IEEE754.binary32ToFloat(Binary32.low(value))
            ).toLong
          else IEEE754.doubleToBinary64(IEEE754.binary64ToDouble(current) + IEEE754.binary64ToDouble(value))
        }.fold(error => throw IllegalStateException(error.message), value => value)
        state.registers(lane)(destination) = Bits.truncate(previous, width)
    }

    private def atomicBinaryResult(
        operation: IntegerOperation,
        current: Long,
        value: Long,
        width: Int where (Between[8, 64] & MultipleOf[8])
    ): Long = {
      val left   = Bits.truncate(current, width)
      val right  = Bits.truncate(value, width)
      val result = operation match {
        case IntegerOperation.Minimum(true) =>
          val shift = 64 - width.raw
          Math.min(left << shift >> shift, right << shift >> shift)
        case IntegerOperation.Maximum(true) =>
          val shift = 64 - width.raw
          Math.max(left << shift >> shift, right << shift >> shift)
        case IntegerOperation.Minimum(false) =>
          if (java.lang.Long.compareUnsigned(left, right) <= 0) left else right
        case IntegerOperation.Maximum(false) =>
          if (java.lang.Long.compareUnsigned(left, right) >= 0) left else right
        case IntegerOperation.BitwiseAnd => left & right
        case IntegerOperation.BitwiseOr  => left | right
        case IntegerOperation.BitwiseXor => left ^ right
        case other                       => throw IllegalStateException(s"unsupported atomic operation $other")
      }
      Bits.truncate(result, width)
    }

  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
  class WriteBuffer {
    private val ObjectWrite   = 0.toByte
    private val RegisterWrite = 1.toByte
    private val MemoryWrite   = 2.toByte
    private var kinds         = Array.ofDim[Byte](64)
    private var writes        = Array.ofDim[VMWrite | Null](64)
    private var lanes         = Array.ofDim[WorkgroupLane](64)
    private var registerNames = Array.ofDim[NonEmpty[String]](64)
    private var values        = Array.ofDim[Long](64)
    private var addresses     = Array.ofDim[Long](64)
    private var widths        = Array.ofDim[Int where (Between[8, 64] & MultipleOf[8])](64)
    private var pendingSize   = 0

    def stage(write: VMWrite): Unit = {
      ensureCapacity()
      kinds(pendingSize) = ObjectWrite
      writes(pendingSize) = write
      pendingSize += 1
    }

    def stageRegister(lane: WorkgroupLane, name: NonEmpty[String], value: Long): Unit = {
      ensureCapacity()
      kinds(pendingSize) = RegisterWrite
      writes(pendingSize) = null
      lanes(pendingSize) = lane
      registerNames(pendingSize) = name
      values(pendingSize) = value
      pendingSize += 1
    }

    def stageMemory(
        lane: WorkgroupLane,
        address: GuestAddress,
        width: Int where (Between[8, 64] & MultipleOf[8]),
        value: Long
    ): Unit = {
      val previous = pendingSize - 1
      // Coalesce identical non-atomic stores within a warp. Repeated unlock writes
      // must not clear a lock reacquired by another workgroup between lane writes.
      val duplicate = previous >= 0 && kinds(previous) == MemoryWrite &&
        (lanes(previous).raw >>> 5) ==
        (lane.raw >>> 5) && addresses(previous) == address.raw &&
        widths(previous) == width && values(previous) == value
      if (!duplicate) {
        ensureCapacity()
        kinds(pendingSize) = MemoryWrite
        writes(pendingSize) = null
        lanes(pendingSize) = lane
        addresses(pendingSize) = address.raw
        widths(pendingSize) = width
        values(pendingSize) = value
        pendingSize += 1
      }
    }

    def isEmpty: Boolean = pendingSize == 0
    def size: Int        = pendingSize

    def clear(): Unit = {
      var index = 0
      while (index < pendingSize) {
        writes(index) = null
        index += 1
      }
      pendingSize = 0
    }

    private def ensureCapacity(): Unit =
      if (pendingSize == kinds.length) {
        val capacity = Math.multiplyExact(kinds.length, 2)
        kinds = java.util.Arrays.copyOf(kinds, capacity)
        writes = java.util.Arrays.copyOf(writes, capacity)
        val nextLanes = Array.ofDim[WorkgroupLane](capacity)
        Array.copy(lanes, 0, nextLanes, 0, lanes.length)
        lanes = nextLanes
        val nextRegisterNames = Array.ofDim[NonEmpty[String]](capacity)
        Array.copy(registerNames, 0, nextRegisterNames, 0, registerNames.length)
        registerNames = nextRegisterNames
        values = java.util.Arrays.copyOf(values, capacity)
        addresses = java.util.Arrays.copyOf(addresses, capacity)
        val nextWidths = Array.ofDim[Int where (Between[8, 64] & MultipleOf[8])](capacity)
        Array.copy(widths, 0, nextWidths, 0, widths.length)
        widths = nextWidths
      }

    @SuppressWarnings(Array("scalafix:DisableSyntax.return"))
    def commit(state: VMBlockState, memory: AddressMemory, pc: Pc): Either[VMFault, Unit] = {
      var index = 0
      while (index < pendingSize) {
        if (kinds(index) == ObjectWrite)
          WriteBuffer.validateWrite(memory, pc)(writes(index).nn) match {
            case Left(fault) =>
              clear()
              return Left(fault)
            case Right(()) => ()
          }
        else if (kinds(index) == MemoryWrite)
          memory.validateRange(
            GuestAddress(addresses(index)),
            widths(index).byteCount
          ) match {
            case Left(error) =>
              clear()
              return Left(VMFault.fromMemory(error, s"pc $pc, lane ${lanes(index)}: "))
            case Right(()) => ()
          }
        index += 1
      }
      index = 0
      val systemWrites = Vec.from((0 until pendingSize).flatMap { position =>
        if (kinds(position) != ObjectWrite) None
        else
          writes(position).nn match {
            case VMWrite.SystemMemory(_, address, width, value) => Some((address, value, width.byteCount))
            case _                                              => None
          }
      })
      if (systemWrites.nonEmpty)
        memory.writeSystemBatch(systemWrites).left.foreach(error => throw IllegalStateException(error.message))
      index = 0
      while (index < pendingSize) {
        if (kinds(index) == RegisterWrite)
          state.registers(lanes(index))(registerNames(index)) = values(index)
        else if (kinds(index) == MemoryWrite)
          memory.writeLittleEndian(
            GuestAddress(addresses(index)),
            values(index),
            widths(index).byteCount
          ).left.foreach(error => throw IllegalStateException(error.message))
        else {
          writes(index).nn match {
            case VMWrite.SystemMemory(_, _, _, _) => ()
            case other                            => WriteBuffer.applyWrite(state, memory)(other)
          }
          writes(index) = null
        }
        index += 1
      }
      pendingSize = 0
      Right(())
    }
  }
}

/** Mutable storage for one executing NVIDIA workgroup. */
class VMBlockState(
    val laneCount: Int > 0,
    initialLive: VMLaneMask,
    val shared: mutable.LongMap[Byte] = mutable.LongMap.empty,
    val registerChunkCount: VMRegisterFile.ChunkCount = 96,
    val memoryBarriers: mutable.Map[Long, VMMemoryBarrier] = mutable.Map.empty
) {
  val invalidatedMemoryBarriers: mutable.Set[Long] = mutable.Set.empty
  private val lanes                                = Array.ofDim[VMBlockState.Lane | Null](laneCount.raw)

  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  private def lane(index: WorkgroupLane): VMBlockState.Lane = lanes(index.raw) match {
    case null =>
      val allocated = VMBlockState.Lane(registerChunkCount)
      lanes(index.raw) = allocated
      allocated
    case existing => existing
  }

  def registers(index: WorkgroupLane): VMRegisterFile                                  = lane(index).registers
  def predicates(index: WorkgroupLane): VMPredicateFile                                = lane(index).predicates
  def conditionCodes(index: WorkgroupLane): VMConditionCodes                           = lane(index).conditionCodes
  def parameters(index: WorkgroupLane): mutable.Map[NonEmpty[String], Long]            = lane(index).parameters
  def parameterBytes(index: WorkgroupLane): mutable.Map[NonEmpty[String], Array[Byte]] = lane(index).parameterBytes
  def sassReturns(index: WorkgroupLane): mutable.ArrayBuffer[Int]                      = lane(index).sassReturns
  private def local(index: WorkgroupLane): mutable.LongMap[Byte]                       = lane(index).local

  val convergence: mutable.Map[(Int, Int), Long] = mutable.Map.empty
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var nextConvergenceToken = 0

  def createConvergence(warp: Int, mask: Long): Int = {
    nextConvergenceToken = Math.incrementExact(nextConvergenceToken)
    convergence((warp, nextConvergenceToken)) = mask
    nextConvergenceToken
  }
  val carry: Array[Boolean]                                     = Array.fill(laneCount.raw)(false)
  val effects: VMBlockState.WriteBuffer                         = VMBlockState.WriteBuffer()
  private val buffers: mutable.Map[Long, Array[Byte]]           = mutable.Map.empty
  def parameterBuffers: scala.collection.Map[Long, Array[Byte]] = buffers
  val parameterAddresses: mutable.Map[NonEmpty[String], Long]   = mutable.Map.empty
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var nextParameterBuffer = 0x700000000L
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var firstParameterBuffer = Long.MaxValue
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  var live: VMLaneMask = initialLive

  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.null"))
  def reset(nextLive: VMLaneMask): Unit = {
    var lane = 0
    while (lane < laneCount.raw) {
      lanes(lane) match {
        case null  => ()
        case state => state.clear()
      }
      carry(lane) = false
      lane += 1
    }
    buffers.clear()
    parameterAddresses.clear()
    shared.clear()
    convergence.clear()
    nextConvergenceToken = 0
    memoryBarriers.clear()
    invalidatedMemoryBarriers.clear()
    effects.clear()
    nextParameterBuffer = 0x700000000L
    firstParameterBuffer = Long.MaxValue
    live = nextLive
  }

  def readShared(address: Long, width: Int where (Between[8, 64] & MultipleOf[8])): Long =
    LittleEndian.decode(byteCount(width))(index =>
      shared.getOrElse(VMAddressSpace.sharedAddress(address) + index.toLong, 0.toByte)
    )

  def writeShared(address: Long, width: Int where (Between[8, 64] & MultipleOf[8]), value: Long): Unit = {
    val base = VMAddressSpace.sharedAddress(address)
    val size = byteCount(width)
    if (memoryBarriers.nonEmpty || invalidatedMemoryBarriers.nonEmpty) {
      // A partial or same-value guest write replaces cached opaque state as well.
      val first = base & ~7L
      val last  = (base + size.raw - 1L) & ~7L
      val _     = memoryBarriers.remove(first)
      val _     = invalidatedMemoryBarriers.remove(first)
      if (last != first) {
        val _ = memoryBarriers.remove(last)
        val _ = invalidatedMemoryBarriers.remove(last)
      }
    }
    LittleEndian.write(value, size) { (index, byte) => shared(base + index.toLong) = byte }
  }

  def readLocal(lane: WorkgroupLane, address: Long, width: Int where (Between[8, 64] & MultipleOf[8])): Long = {
    val bytes = local(lane)
    LittleEndian.decode(byteCount(width))(index => bytes.getOrElse(address + index.toLong, 0.toByte))
  }

  def writeLocal(
      lane: WorkgroupLane,
      address: Long,
      width: Int where (Between[8, 64] & MultipleOf[8]),
      value: Long
  ): Unit = {
    val bytes = local(lane)
    LittleEndian.write(value, byteCount(width)) { (index, byte) =>
      bytes(address + index.toLong) = byte
    }
  }

  def writeParameterBytes(
      lane: WorkgroupLane,
      name: NonEmpty[String],
      offset: Int >= 0,
      width: Int where (Between[8, 64] & MultipleOf[8]),
      value: Long
  ): Unit = {
    val end        = Math.addExact(offset.raw, byteCount(width).raw)
    val parameters = parameterBytes(lane)
    val bytes      = parameters.get(name) match {
      case Some(existing) if existing.length >= end => existing
      case Some(existing)                           =>
        val grown = java.util.Arrays.copyOf(existing, end)
        parameters(name) = grown
        grown
      case None =>
        val allocated = Array.ofDim[Byte](end)
        parameters(name) = allocated
        allocated
    }
    LittleEndian.write(value, bytes, offset.raw, byteCount(width))
  }

  def readParameterBytes(
      lane: WorkgroupLane,
      name: NonEmpty[String],
      offset: Int >= 0,
      size: Int where Between[0, 8]
  ): Long = {
    val end = Math.addExact(offset.raw, size.raw)
    parameterBytes(lane).get(name) match {
      case Some(bytes) if end <= bytes.length => LittleEndian.decode(bytes, offset.raw, size)
      case Some(bytes)                        => LittleEndian.decode(size) { index =>
          if (offset.raw + index < bytes.length) bytes(offset.raw + index) else 0.toByte
        }
      case None => 0L
    }
  }

  def allocateParameterBuffer(size: Int >= 0): Long = {
    val base = nextParameterBuffer
    buffers(base) = Array.ofDim[Byte](Math.max(size.raw, 1))
    firstParameterBuffer = Math.min(firstParameterBuffer, base)
    nextParameterBuffer = Math.addExact(base, Math.max(size.raw, 1).toLong + 0x100L)
    base
  }

  def bindParameterBuffer(name: NonEmpty[String], bytes: Array[Byte]): Unit = {
    val base = allocateParameterBuffer(bytes.length.unsafe[Int >= 0])
    Array.copy(bytes, 0, buffers(base), 0, bytes.length)
    parameterAddresses(name) = base
  }

  def importParameterBuffers(imported: scala.collection.Map[Long, Array[Byte]]): Unit =
    imported.foreach { case (address, bytes) =>
      buffers(address) = bytes
      firstParameterBuffer = Math.min(firstParameterBuffer, address)
      nextParameterBuffer = Math.max(
        nextParameterBuffer,
        Math.addExact(address, Math.max(bytes.length, 1).toLong + 0x100L)
      )
    }

  // This hot lookup avoids the intermediate view and collectFirst closure for each virtual-memory access.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:GemstoneNoIterator.no-iterator"))
  def parameterBufferRange(address: Long, size: Int >= 0): Option[(Array[Byte], Int >= 0)] =
    if (address < firstParameterBuffer || address >= nextParameterBuffer) None
    else {
      val entries                                 = buffers.iterator
      var result: Option[(Array[Byte], Int >= 0)] = None
      while (entries.hasNext && result.isEmpty) {
        val (base, bytes) = entries.next()
        val offset        = address - base
        if (address >= base && offset >= 0L && offset <= bytes.length.toLong - size.raw.toLong)
          result = Some(bytes -> offset.toInt.unsafe[Int >= 0])
      }
      result
    }

  def readParameterBuffer(address: Long, size: Int in 0 ~ 8): Option[Long] =
    parameterBufferRange(address, size.widen[Int >= 0]).map { case (bytes, offset) =>
      LittleEndian.decode(bytes, offset.raw, size)
    }

  def readParameterBufferBytes(address: Long, size: Int >= 0): Option[Array[Byte]] =
    parameterBufferRange(address, size).map { case (bytes, offset) =>
      java.util.Arrays.copyOfRange(bytes, offset.raw, (offset + size).raw)
    }
}
