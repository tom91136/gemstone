package net.kurobako.gemstone.isa.spirv

import Op.*
import VMInvocation.*
import VMLowering.*
import VMMemory.*
import cats.syntax.all.*
import net.kurobako.gemstone.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*

/** OpenCL device-side enqueue: queues, ND-ranges, events and block kernel queries. */
private object DeviceEnqueueOps {
  import DeviceQueue.*
  import Operand.*

  /** `get_default_queue` names the default device queue, which every device queue shares. */
  private val DefaultQueue = 1L

  private val MaxWorkGroupSize         = environment.OpenclMaxWorkGroupSizeDefault.raw
  private val SubgroupSize             = 32L
  private val ProfilingCommandExecTime = 1L

  def lower(site: Site): PartialFunction[Instruction, Op] = {
    case Instruction.GetDefaultQueue(t, d) =>
      step((execution, _) => Right(execution.values.updateScalar(id(d), id(t), DefaultQueue)))
    case Instruction.BuildNDRange(t, d, global, local, offset) =>
      step { (execution, _) =>
        val values = execution.values
        for {
          sizes   <- bits(values, id(global))
          groups  <- bits(values, id(local))
          origins <- bits(values, id(offset))
        } yield {
          def padded(lanes: Array[Long]): Array[Long] = Array.tabulate(3)(i => if (i < lanes.length) lanes(i) else 0L)
          val lanes = Array(sizes.length.toLong) ++ padded(origins) ++ padded(sizes) ++ padded(groups)
          values.update(id(d), VMValue.Bits(id(t), lanes))
        }
      }
    case instruction: Instruction.EnqueueKernel                    => enqueueKernel(site.program, instruction)
    case Instruction.EnqueueMarker(t, d, queue, count, waits, ret) =>
      step { (execution, memory) =>
        val values = execution.values
        for {
          target   <- scalar(values, id(queue))
          number   <- scalar(values, id(count))
          list     <- address(values, id(waits))
          returned <- address(values, id(ret))
          handles  <- waitList(memory, number, list)
          status   <-
            if (target == 0L) Right(InvalidQueue)
            else handles.filter(_.nonEmpty).fold(Right(InvalidEventWaitList))(waitsOn =>
              finishEnqueue(
                memory,
                returned,
                memory.deviceQueue.enqueue(waitsOn, returned.address != 0L)(
                  Command.Marker(_, waitsOn)
                )
              )
            )
        } yield values.updateScalar(id(d), id(t), status.toLong & 0xffffffffL)
      }
    case Instruction.RetainEvent(event) =>
      step((execution, memory) => scalar(execution.values, id(event)).map(memory.deviceQueue.retain))
    case Instruction.ReleaseEvent(event) =>
      step((execution, memory) => scalar(execution.values, id(event)).map(memory.deviceQueue.release))
    case Instruction.CreateUserEvent(t, d) =>
      step((execution, memory) => Right(execution.values.updateScalar(id(d), id(t), memory.deviceQueue.userEvent())))
    case Instruction.IsValidEvent(t, d, event) =>
      step((execution, memory) =>
        scalar(execution.values, id(event)).map(handle =>
          execution.values.updateScalar(id(d), id(t), if (memory.deviceQueue.valid(handle)) 1L else 0L)
        )
      )
    case Instruction.SetUserEventStatus(event, status) =>
      step { (execution, memory) =>
        for {
          handle <- scalar(execution.values, id(event))
          value  <- scalar(execution.values, id(status))
          _      <- memory.deviceQueue.setStatus(handle, value.toInt, memory.write)
        } yield ()
      }
    case Instruction.CaptureEventProfilingInfo(event, info, value) =>
      step { (execution, memory) =>
        for {
          handle  <- scalar(execution.values, id(event))
          kind    <- scalar(execution.values, id(info))
          pointer <- address(execution.values, id(value))
          _       <-
            if (kind == ProfilingCommandExecTime) memory.deviceQueue.capture(handle, pointer, memory.write)
            else Left(Fault.InvalidRequest(s"capture_event_profiling_info does not support profiling info $kind"))
        } yield ()
      }
    case Instruction.GetKernelWorkGroupSize(t, d, _, _, _, _) =>
      step((execution, _) => Right(execution.values.updateScalar(id(d), id(t), MaxWorkGroupSize)))
    case Instruction.GetKernelPreferredWorkGroupSizeMultiple(t, d, _, _, _, _) =>
      step((execution, _) => Right(execution.values.updateScalar(id(d), id(t), SubgroupSize)))
    case Instruction.GetKernelMaxNumSubgroups(t, d, _, _, _, _) =>
      step((execution, _) =>
        Right(execution.values.updateScalar(id(d), id(t), (MaxWorkGroupSize + SubgroupSize - 1L) / SubgroupSize))
      )
    case Instruction.GetKernelLocalSizeForSubgroupCount(t, d, count, _, _, _, _) =>
      step((execution, _) =>
        scalar(execution.values, id(count)).map { subgroups =>
          val size = (subgroups & 0xffffffffL) * SubgroupSize
          execution.values.updateScalar(id(d), id(t), if (size <= MaxWorkGroupSize) size else 0L)
        }
      )
    case Instruction.GetKernelNDrangeSubGroupCount(t, d, range, _, _, _, _) =>
      step((execution, memory) =>
        ndRange(site.program, execution.values, memory, id(range)).map { ndrange =>
          val volume = groupShape(ndrange).product
          execution.values.updateScalar(id(d), id(t), (volume + SubgroupSize - 1L) / SubgroupSize)
        }
      )
    case Instruction.GetKernelNDrangeMaxSubGroupSize(t, d, range, _, _, _, _) =>
      step((execution, memory) =>
        ndRange(site.program, execution.values, memory, id(range)).map(ndrange =>
          execution.values.updateScalar(id(d), id(t), math.min(SubgroupSize, groupShape(ndrange).product))
        )
      )
  }

  /** The work-group shape a launch uses: the requested one, or as much of the first dimension as a group holds. */
  def groupShape(range: NDRange): Vector[Long] =
    if (range.local.take(range.dimensions).exists(_ != 0L)) range.local.take(range.dimensions).map(math.max(1L, _))
    else math.min(range.global.head, MaxWorkGroupSize) +: Vector.fill(range.dimensions - 1)(1L)

  private def ndRange(program: VMProgram, values: InvocationValues, memory: MemoryState, operand: Int)
      : AddressSpace.ReadResult[NDRange] = {
    val lanes: AddressSpace.ReadResult[Array[Long]] = values.get(operand) match {
      case Some(VMValue.Address(_, pointer)) =>
        val pointee = program.pointerPointee(program.valueType(operand))
        memory.read(pointer, program.typeSize(pointee)).flatMap(bytes =>
          program.decode(pointee, bytes).flatMap {
            case VMValue.Bits(_, lanes) => Right(lanes)
            case _                      => Left(Fault.InvalidRequest("ndrange_t is not a structure of integers"))
          }
        )
      case Some(VMValue.Bits(_, lanes)) => Right(lanes)
      case None                         => Left(undefined(operand))
    }
    lanes.flatMap {
      case lanes if lanes.length == 10 =>
        Right(NDRange(
          lanes(0).toInt,
          lanes.slice(1, 4).toVector,
          lanes.slice(4, 7).toVector,
          lanes.slice(7, 10).toVector
        ))
      case lanes => Left(Fault.InvalidRequest(s"ndrange_t has ${lanes.length} members, expected 10"))
    }
  }

  private def waitList(memory: MemoryState, count: Long, list: Pointer): AddressSpace.ReadResult[Option[Vector[Long]]] =
    if ((count == 0L) != (list.address == 0L)) Right(None)
    else (0L until count).toVector.traverse(index =>
      memory.readScalar(list.copy(address = list.address + index * 8L), 8)
    ).map(Some(_))

  private def finishEnqueue(memory: MemoryState, returned: Pointer, result: Either[Int, Long]): Either[Fault, Int] =
    result match {
      case Left(status)                           => Right(status)
      case Right(handle) if returned.address != 0 => memory.writeScalar(returned, handle, 8).map(_ => Success)
      case Right(_)                               => Right(Success)
    }

  private def localSize(program: VMProgram, values: InvocationValues, memory: MemoryState, operand: Int)
      : AddressSpace.ReadResult[Long] = values.get(operand) match {
    case Some(VMValue.Address(_, pointer)) =>
      program.scalarMemoryWidth(program.pointerPointee(program.valueType(operand))).flatMap(memory.readScalar(
        pointer,
        _
      ))
    case Some(_) => scalar(values, operand).map(_ & 0xffffffffL)
    case None    => Left(undefined(operand))
  }

  private def validRange(range: NDRange): Boolean =
    range.dimensions >= 1 && range.dimensions <= 3 && range.global.take(range.dimensions).forall(_ > 0L)

  private def enqueueKernel(program: VMProgram, instruction: Instruction.EnqueueKernel): Op = {
    val Instruction.EnqueueKernel(t, d, queue, _, range, count, waits, ret, invoke, param, size, alignment, locals) =
      instruction
    step { (execution, memory) =>
      val values = execution.values
      for {
        target   <- scalar(values, id(queue))
        ndrange  <- ndRange(program, values, memory, id(range))
        number   <- scalar(values, id(count))
        list     <- address(values, id(waits))
        returned <- address(values, id(ret))
        literal  <- address(values, id(param))
        bytes    <- scalar(values, id(size))
        align    <- scalar(values, id(alignment))
        handles  <- waitList(memory, number, list)
        sizes    <- locals.toVector.traverse(operand => localSize(program, values, memory, id(operand)))
        captured <- memory.read(literal, bytes.toInt.unsafe[Int >= 0])
        status   <-
          if (target == 0L) Right(InvalidQueue)
          else if (!validRange(ndrange)) Right(InvalidNDRange)
          else if (sizes.contains(0L)) Right(InvalidArgSize)
          else handles.fold(Right(InvalidEventWaitList))(waitsOn =>
            finishEnqueue(
              memory,
              returned,
              memory.deviceQueue.enqueue(waitsOn, returned.address != 0L)(event =>
                Command.Kernel(event, waitsOn, id(invoke), ndrange, captured, align.toInt, sizes)
              )
            )
          )
      } yield values.updateScalar(id(d), id(t), status.toLong & 0xffffffffL)
    }
  }
}
