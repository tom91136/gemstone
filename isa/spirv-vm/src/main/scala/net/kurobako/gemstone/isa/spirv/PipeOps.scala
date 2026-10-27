package net.kurobako.gemstone.isa.spirv

import Op.*
import VMCollectives.Kind as CollectiveKind
import VMCollectives.Payload as CollectivePayload
import VMCollectives.Request as CollectiveRequest
import VMInvocation.*
import VMMemory.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*

/** OpenCL pipes in the `PipeLayout` ABI. Reads of memory always precede a claim, so a lane suspended on a pending read
  * retries without having taken packets.
  */
private object PipeOps {
  import Operand.*

  /** Pipe built-ins report failure with a negative status. */
  private val Failed = 0xffffffffL

  private val Index = 0xffffffffL

  /** Runs the pipe operation `instruction` names. */
  def execute(execution: InvocationState, instruction: Instruction): Flow[Unit] = instruction match {
    case Instruction.WritePipe(t, d, pipe, pointer, _, _) => write(execution, ResultIds(t, d), id(pipe), id(pointer))
    case Instruction.ReadPipe(t, d, pipe, pointer, _, _)  => read(execution, ResultIds(t, d), id(pipe), id(pointer))
    case Instruction.ReservedWritePipe(t, d, pipe, reserved, index, pointer, _, _) =>
      reservedTransfer(execution, ResultIds(t, d), id(pipe), id(reserved), id(index), id(pointer), end = PipeEnd.Write)
    case Instruction.ReservedReadPipe(t, d, pipe, reserved, index, pointer, _, _) =>
      reservedTransfer(execution, ResultIds(t, d), id(pipe), id(reserved), id(index), id(pointer), end = PipeEnd.Read)
    case Instruction.ReserveWritePipePackets(t, d, pipe, count, _, _) =>
      reserve(execution, ResultIds(t, d), id(pipe), id(count), end = PipeEnd.Write)
    case Instruction.ReserveReadPipePackets(t, d, pipe, count, _, _) =>
      reserve(execution, ResultIds(t, d), id(pipe), id(count), end = PipeEnd.Read)
    case Instruction.GroupReserveWritePipePackets(t, d, scope, pipe, count, _, _) =>
      groupReserve(execution, ResultIds(t, d), id(scope), id(pipe), id(count), end = PipeEnd.Write)
    case Instruction.GroupReserveReadPipePackets(t, d, scope, pipe, count, _, _) =>
      groupReserve(execution, ResultIds(t, d), id(scope), id(pipe), id(count), end = PipeEnd.Read)
    case Instruction.IsValidReserveId(t, d, reserved) =>
      advancing(execution)(
        scalar(execution.values, id(reserved)).map(value =>
          execution.values.updateScalar(id(d), id(t), if (value < 0L) 1L else 0L)
        )
      )
    case Instruction.GetNumPipePackets(t, d, pipe, _, _) =>
      query(execution, ResultIds(t, d), id(pipe))((memory, pointer) =>
        indices(memory, pointer).map((read, write) => (write - read) & Index)
      )
    case Instruction.CreatePipeFromPipeStorage(t, d, storage) =>
      advancing(execution)(transfer(execution.values, id(storage), id(d), id(t)))
    case Instruction.GetMaxPipePackets(t, d, pipe, _, _) =>
      query(execution, ResultIds(t, d), id(pipe))((memory, pointer) => header(memory, pointer, PipeLayout.Capacity))
    case _ => Left(Fault.Unsupported(s"${instruction.opcode} is not a pipe operation"))
  }

  private def advancing(execution: InvocationState)(outcome: => Flow[Unit]): Flow[Unit] =
    outcome.map(_ => execution.pc += 1)

  /** A reservation of `count` packets from `start`, with the sign bit marking it valid. */
  private def reservation(start: Long, count: Long): Long = Long.MinValue | ((count & 0x7fffffffL) << 32) |
    (start & Index)

  private def at(pipe: Pointer, offset: Long): Pointer =
    pipe.copy(address = pipe.address + offset, storageClass = StorageClass.CrossWorkgroup)

  private def header(memory: MemoryState, pipe: Pointer, offset: Int): Either[Fault, Long] =
    memory.atomicReadScalar(at(pipe, offset), 4)

  private def indices(memory: MemoryState, pipe: Pointer): Either[Fault, (Long, Long)] =
    memory.atomicReadScalar(at(pipe, PipeLayout.ReadIndex), 8).map(word => (word & Index, word >>> 32))

  private def slot(pipe: Pointer, capacity: Long, packetBytes: Long, index: Long): Pointer =
    at(pipe, PipeLayout.Packets + (index % capacity) * packetBytes)

  /** Claims `count` packets at the write or read end and returns the first claimed index, if they fit. */
  def claim(memory: MemoryState, pipe: Pointer, count: Long, end: PipeEnd): Either[Fault, Option[Long]] =
    header(memory, pipe, PipeLayout.Capacity).flatMap { capacity =>
      val writing                   = end == PipeEnd.Write
      def fits(word: Long): Boolean = {
        val used = ((word >>> 32) - (word & Index)) & Index
        count > 0L && (if (writing) used + count <= capacity else count <= used)
      }
      memory.atomicModifyScalar(at(pipe, PipeLayout.ReadIndex), 8) { word =>
        if (!fits(word)) word
        else if (writing) ((((word >>> 32) + count) & Index) << 32) | (word & Index)
        else (word & ~Index) | (((word & Index) + count) & Index)
      }.map(word => Option.when(fits(word))(if (writing) word >>> 32 else word & Index))
    }

  /** A reservation shared by a group, made once at its rendezvous. */
  def reserveOnce(memory: MemoryState, pipe: Pointer, count: Long, end: PipeEnd): Either[Fault, Long] =
    claim(memory, pipe, count, end).map(_.fold(0L)(reservation(_, count)))

  private def readBytes(memory: MemoryState, pointer: Pointer, count: Int): AddressSpace.ReadResult[Array[Byte]] = {
    val bytes                                                   = Array.ofDim[Byte](count)
    def loop(offset: Int): AddressSpace.ReadResult[Array[Byte]] =
      if (offset >= count) Right(bytes)
      else {
        val width = math.min(8, count - offset)
        memory.readScalar(pointer.copy(address = pointer.address + offset), width.unsafe[Int in 1 ~ 8]).flatMap {
          value =>
            LittleEndian.write(value, bytes, offset, width.unsafe[Int in 0 ~ 8])
            loop(offset + width)
        }
      }
    loop(0)
  }

  private def writeBytes(memory: MemoryState, pointer: Pointer, bytes: Array[Byte]): Either[Fault, Unit] = {
    def loop(offset: Int): Either[Fault, Unit] =
      if (offset >= bytes.length) Right(())
      else {
        val width = math.min(8, bytes.length - offset)
        memory.writeScalar(
          pointer.copy(address = pointer.address + offset),
          LittleEndian.decode(bytes, offset, width.unsafe[Int in 0 ~ 8]),
          width.unsafe[Int in 1 ~ 8]
        ).flatMap(_ => loop(offset + width))
      }
    loop(0)
  }

  private def packetBytes(memory: MemoryState, pipe: Pointer): Either[Fault, Int] =
    header(memory, pipe, PipeLayout.PacketBytes).flatMap(bytes =>
      Either.cond(bytes > 0L && bytes <= Int.MaxValue, bytes.toInt, Fault.InvalidRequest("pipe packet size is invalid"))
    )

  private def write(execution: InvocationState, ids: ResultIds, pipe: Int, pointer: Int): Flow[Unit] =
    advancing(execution) {
      val memory = execution.memory
      val values = execution.values
      for {
        target   <- address(values, pipe)
        source   <- address(values, pointer)
        size     <- packetBytes(memory, target)
        capacity <- header(memory, target, PipeLayout.Capacity)
        packet   <- readBytes(memory, source, size)
        claimed  <- claim(memory, target, 1L, PipeEnd.Write)
        _        <- claimed.fold[Either[Fault, Unit]](Right(()))(index =>
          writeBytes(memory, slot(target, capacity, size, index), packet)
        )
      } yield values.updateScalar(ids.dest, ids.resultType, if (claimed.isDefined) 0L else Failed)
    }

  // Reads the oldest packet, then claims exactly that index; a lost race reads the next one.
  private def read(execution: InvocationState, ids: ResultIds, pipe: Int, pointer: Int): Flow[Unit] =
    advancing(execution) {
      val memory = execution.memory
      val values = execution.values
      def attempt(source: Pointer, target: Pointer, size: Int, capacity: Long): AddressSpace.ReadResult[Boolean] =
        indices(memory, source).flatMap { (oldest, newest) =>
          if (newest == oldest) Right(false)
          else readBytes(memory, slot(source, capacity, size, oldest), size).flatMap { packet =>
            memory.atomicModifyScalar(at(source, PipeLayout.ReadIndex), 8)(word =>
              if ((word & Index) == oldest) (word & ~Index) | ((oldest + 1L) & Index) else word
            ).flatMap(word =>
              if ((word & Index) == oldest) writeBytes(memory, target, packet).map(_ => true)
              else attempt(source, target, size, capacity)
            )
          }
        }
      for {
        source   <- address(values, pipe)
        target   <- address(values, pointer)
        size     <- packetBytes(memory, source)
        capacity <- header(memory, source, PipeLayout.Capacity)
        read     <- attempt(source, target, size, capacity)
      } yield values.updateScalar(ids.dest, ids.resultType, if (read) 0L else Failed)
    }

  private def reservedTransfer(
      execution: InvocationState,
      ids: ResultIds,
      pipe: Int,
      reserved: Int,
      index: Int,
      pointer: Int,
      end: PipeEnd
  ): Flow[Unit] =
    advancing(execution) {
      val memory = execution.memory
      val values = execution.values
      for {
        handle   <- address(values, pipe)
        packet   <- address(values, pointer)
        id       <- scalar(values, reserved)
        offset   <- scalar(values, index)
        size     <- packetBytes(memory, handle)
        capacity <- header(memory, handle, PipeLayout.Capacity)
        valid  = id < 0L && (offset & Index) < ((id >>> 32) & 0x7fffffffL)
        target = slot(handle, capacity, size, ((id & Index) + (offset & Index)) & Index)
        _ <-
          if (!valid) Right(())
          else if (end == PipeEnd.Write) readBytes(memory, packet, size).flatMap(writeBytes(memory, target, _))
          else readBytes(memory, target, size).flatMap(writeBytes(memory, packet, _))
      } yield values.updateScalar(ids.dest, ids.resultType, if (valid) 0L else Failed)
    }

  private def reserve(execution: InvocationState, ids: ResultIds, pipe: Int, count: Int, end: PipeEnd)
      : Flow[Unit] =
    advancing(execution) {
      val memory = execution.memory
      val values = execution.values
      for {
        handle   <- address(values, pipe)
        packets  <- scalar(values, count)
        reserved <- reserveOnce(memory, handle, packets & Index, end)
      } yield values.updateScalar(ids.dest, ids.resultType, reserved)
    }

  private def groupReserve(
      execution: InvocationState,
      ids: ResultIds,
      scope: Int,
      pipe: Int,
      count: Int,
      end: PipeEnd
  ): Flow[Unit] = {
    val values = execution.values
    for {
      group   <- scalar(values, scope)
      _       <- Either.cond(isWorkgroupOrSubgroup(group), (), scopeFault("pipe reservation", group))
      handle  <- address(values, pipe)
      packets <- scalar(values, count)
      _       <- suspend(
        execution,
        CollectiveRequest(
          CollectiveKind.PipeReservation,
          _,
          _,
          group.toInt,
          CollectivePayload.PipeReservation(ids.resultType, ids.dest, handle, packets & 0xffffffffL, end)
        )
      )
    } yield ()
  }

  private def query(execution: InvocationState, ids: ResultIds, pipe: Int)(
      read: (MemoryState, Pointer) => Either[Fault, Long]
  ): Flow[Unit] = advancing(execution)(
    address(execution.values, pipe).flatMap(read(execution.memory, _))
      .map(execution.values.updateScalar(ids.dest, ids.resultType, _))
  )
}
