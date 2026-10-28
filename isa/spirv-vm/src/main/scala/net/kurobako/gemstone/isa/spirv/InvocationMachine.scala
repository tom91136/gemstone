package net.kurobako.gemstone.isa.spirv

import Op.*
import VMCollectives.Kind as CollectiveKind
import VMCollectives.Payload as CollectivePayload
import VMCollectives.Request as CollectiveRequest
import VMInvocation.*
import VMMemory.*
import VMProgram.*
import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.binary.Bits.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.platform.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.vm.*
import scala.annotation.tailrec
import scala.collection.mutable

private object InvocationMachine {
  val unreachableFault: Fault = Fault.InvalidRequest("OpUnreachable was reached")

  private def unsupported(what: String): Fault = Fault.Unsupported(s"SPIR-V $what is not a machine operation yet")

  val origin: Long = epochMillis * 1000000L - monotonicNanos

  inline def jump(execution: InvocationState, block: Block): Flow[Unit] = block.fault match {
    case Some(fault) => Left(fault)
    case None        =>
      execution.predecessor = execution.currentLabel
      execution.pc = block.pc
      Done
  }

  inline def enter(execution: InvocationState, label: Int): Flow[Unit] = {
    execution.currentLabel = label
    execution.pc += 1
    if (execution.fragmentQuad || execution.subgroupReconvergence) {
      val regions = execution.reconvergenceRegions
      val merge   = regions.indexWhere(_.merge == label)
      if (merge >= 0) {
        regions.remove(merge + 1, regions.size - merge - 1)
        execution.reconvergenceJoin = Some(ReconvergenceJoin.Merge(label))
      } else {
        // A continue may bypass nested selection merges on its way to the loop header.
        val header = regions.indexWhere(region => region.loop && region.header == label)
        if (header >= 0) regions.remove(header + 1, regions.size - header - 1)
      }
    }
    Done
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def edge(labels: Array[Int], predecessor: Int): Int = {
    var index = labels.length - 1
    while (index >= 0 && labels(index) != predecessor) index -= 1
    index
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.null"))
  inline def phi(execution: InvocationState, group: Phis): Flow[Unit] = {
    val values                = execution.values
    val destinations          = group.destinations
    val bits                  = execution.phiBits(destinations.length)
    val staged                = execution.phiValues(destinations.length)
    var index                 = 0
    var missing: Fault | Null = null
    while (index < destinations.length && missing == null) {
      val incoming = edge(group.parents(index), execution.predecessor)
      if (incoming >= 0 && group.scalar(index)) bits(index) = values.scalars(group.sources(index)(incoming))
      else (if (incoming < 0) None else values.get(group.sources(index)(incoming))) match {
        case Some(value) => staged(index) = retag(value, group.types(index))
        case None        =>
          missing = Fault.InvalidRequest(
            s"OpPhi %${destinations(index)} has no incoming value for label %${execution.predecessor}"
          )
      }
      index += 1
    }
    if (missing != null) Left(missing)
    else {
      index = 0
      while (index < destinations.length) {
        if (group.scalar(index)) values.scalars(destinations(index)) = bits(index)
        else { values.update(destinations(index), staged(index).nn); staged(index) = null }
        index += 1
      }
      execution.pc = group.next
      Done
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  inline def switch(execution: InvocationState, value: Long, cases: Cases): Flow[Unit] = {
    var target = cases.default
    var index  = 0
    while (index < cases.literals.length) {
      if ((cases.literals(index) & cases.mask) == (value & cases.mask)) target = cases.blocks(index)
      index += 1
    }
    jump(execution, target)
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  inline def call(execution: InvocationState, callee: Callee): Flow[Unit] =
    if (execution.calls.size >= 128) Left(Fault.Backend("SPIR-V function call depth exceeds 128 frames"))
    else {
      val function = callee.function
      var index    = 0
      var failure  = Option.empty[Fault]
      while (index < callee.arguments.length && failure.isEmpty) {
        val (parameterId, parameterType) = function.parameters(index)
        transfer(execution.values, callee.arguments(index), parameterId, parameterType) match {
          case Left(error) => failure = Some(error)
          case Right(())   => ()
        }
        index += 1
      }
      failure.toLeft {
        execution.calls.append(
          CallFrame(
            execution.currentFunction,
            execution.pc + 1,
            execution.currentLabel,
            execution.predecessor,
            callee.resultType,
            callee.destination,
            execution.storage.top
          )
        )
        execution.currentFunction = function
        execution.pc = function.start + 1
        execution.currentLabel = -1
        execution.predecessor = -1
      }
    }

  /** The loop `sweep` runs in: the one started at this position and call depth, with the value its last call returned
    * to the first parameter recorded, or a new one.
    */
  private def sweepLoop(execution: InvocationState, sweep: Sweep): CallLoop = execution.loops match {
    case head :: _ if head.pc == execution.pc && head.depth == execution.calls.size =>
      head.results(head.call) = execution.values.leaf(sweep.function.parameters.head._1, 0)
      head.call += 1
      head
    case loops =>
      val started = CallLoop(execution.pc, execution.calls.size, Array.ofDim[Long](sweep.calls))
      execution.loops = started :: loops
      started
  }

  private def enterSweep(execution: InvocationState, sweep: Sweep): Unit = {
    val function = sweep.function
    execution.calls.append(
      CallFrame(
        execution.currentFunction,
        execution.pc,
        execution.currentLabel,
        execution.predecessor,
        sweep.resultType,
        function.parameters.head._1,
        execution.storage.top
      )
    )
    execution.currentFunction = function
    execution.pc = function.start + 1
    execution.currentLabel = -1
    execution.predecessor = -1
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  inline def sweep(execution: InvocationState, sweep: Sweep)(inline argument: (Int, Long, Int) => Long)(
      inline next: (Int => Long) => Flow[Unit]
  ): Flow[Unit] = {
    val loop = sweepLoop(execution, sweep)
    if (loop.call >= sweep.calls) {
      execution.loops = execution.loops.tail
      next(loop.results(_))
    } else if (execution.calls.size >= 128) Left(Fault.Backend("SPIR-V function call depth exceeds 128 frames"))
    else {
      val previous = if (loop.call == 0) 0L else loop.results(loop.call - 1)
      var index    = 0
      var failure  = Option.empty[Fault]
      while (index < sweep.fixed.length && failure.isEmpty) {
        val (parameterId, parameterType) = sweep.function.parameters(index)
        if (sweep.fixed(index) >= 0)
          transfer(execution.values, sweep.fixed(index), parameterId, parameterType) match {
            case Left(error) => failure = Some(error)
            case Right(())   => ()
          }
        else execution.values.updateScalar(parameterId, parameterType, argument(loop.call, previous, index))
        index += 1
      }
      failure.toLeft(enterSweep(execution, sweep))
    }
  }

  def callIndirect(execution: InvocationState, call: Indirect): Flow[Unit] =
    execution.values.get(call.pointer) match {
      case Some(VMValue.Bits(_, Array(token)))
          if execution.values.program.ownsFunctionPointer(token) && (token & 0xffffffffL) > 0 &&
            (token & 0xffffffffL) <= Int.MaxValue &&
            execution.values.program.functions.contains((token & 0xffffffffL).toInt) =>
        call.callee((token & 0xffffffffL).toInt).run(execution, execution.memory)
      case Some(_) => Left(Fault.InvalidRequest("OpFunctionPointerCallINTEL has an invalid function pointer"))
      case None    => Left(undefined(call.pointer))
    }

  private def finishFragmentReturn(execution: InvocationState): Unit = {
    execution.reconvergedReturn = false
    if (execution.fragmentQuad || execution.subgroupReconvergence)
      execution.reconvergenceRegions.filterInPlace(_.function != execution.currentFunction.id)
  }

  /** Lanes that reconverge pause at the return until their neighbours reach it. */
  private def pauseReturn(execution: InvocationState): Boolean =
    if ((execution.fragmentQuad || execution.subgroupReconvergence) && !execution.reconvergedReturn) {
      finishFragmentReturn(execution)
      execution.reconvergenceJoin = Some(ReconvergenceJoin.FunctionReturn(execution.currentFunction.id))
      true
    } else false

  private def resume(execution: InvocationState, frame: CallFrame): Unit = {
    execution.storage.top = frame.stack
    execution.currentFunction = frame.function
    execution.pc = frame.pc
    execution.currentLabel = frame.currentLabel
    execution.predecessor = frame.predecessor
  }

  def returnControl(execution: InvocationState): Flow[Unit] =
    if (pauseReturn(execution)) Done
    else {
      finishFragmentReturn(execution)
      execution.calls.removeLastOption() match {
        case None =>
          execution.complete()
          Done
        case Some(frame) =>
          if (execution.values.program.runtimeType(frame.resultType).contains(RuntimeType.Void)) {
            resume(execution, frame)
            Done
          } else
            Left(Fault.InvalidRequest(
              s"SPIR-V function %${execution.currentFunction.id} returned no value to %${frame.destination}"
            ))
      }
    }

  def returnValue(execution: InvocationState, value: Int): Flow[Unit] =
    if (pauseReturn(execution)) Done
    else {
      finishFragmentReturn(execution)
      execution.values.get(value) match {
        case None           => Left(undefined(value))
        case Some(returned) =>
          execution.calls.removeLastOption() match {
            case None        => Left(Fault.InvalidRequest("SPIR-V entry point returned a value"))
            case Some(frame) =>
              execution.values.update(frame.destination, retag(returned, frame.resultType))
              resume(execution, frame)
              Done
          }
      }
    }

  def discard(execution: InvocationState): Flow[Unit] =
    if (!execution.fragmentInvocation) Left(Fault.InvalidRequest("OpKill requires a fragment invocation"))
    else {
      val cleared = execution.sampleMask match {
        case Some(pointer) =>
          execution.memory.write(pointer, Array.ofDim[Byte](execution.values.program.typeSize(pointer.pointee)))
        case None => Right(())
      }
      cleared.map { _ =>
        execution.fragmentKilled = true
        execution.complete()
        execution.calls.clear()
      }
    }

  def abort(execution: InvocationState, message: Int): Flow[Unit] = {
    val lanes =
      Option(execution.values.lanes(message)).fold("")(_.map(java.lang.Long.toHexString(_)).mkString(" "))
    Left(Fault.Backend(s"OpAbortKHR terminated the invocation with message [$lanes]"))
  }

  def nowhere(what: String): Flow[Nothing] = Left(unsupported(what))

  /** InvalidRequest faults from a memory access name the instruction that made it. */
  def located(error: Fault | AddressSpace.Pending.type, execution: InvocationState, instruction: Instruction)
      : Fault | AddressSpace.Pending.type = error match {
    case fault: Fault => locatedFault(fault, execution, instruction)
    case pending      => pending
  }

  def locatedFault(error: Fault, execution: InvocationState, instruction: Instruction): Fault = error match {
    case Fault.InvalidRequest(message) =>
      Fault.InvalidRequest(s"$message at function %${execution.currentFunction.id}, pc ${execution.pc} ($instruction)")
    case _ => error
  }

  def mappedMatrixStorage(program: VMProgram, pointer: Pointer): Boolean =
    pointer.matrixLayout.nonEmpty || program.hasMatrixStorage(pointer.pointee) ||
      // Composite accesses operate on members, preserving padding and bounding each scalar element.
      (program.vulkan && program.runtimeType(pointer.pointee).exists {
        case RuntimeType.Structure(_) | RuntimeType.Array(_, _) => true
        case _                                                  => false
      })

  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.return"))
  def loadMatrixStorage(
      program: VMProgram,
      memory: MemoryState,
      pointer: Pointer
  ): AddressSpace.ReadResult[VMValue.Bits] = {
    val leaves = program.matrixStorageLeaves(pointer.pointee, pointer.matrixLayout)
    val lanes  = Array.ofDim[Long](leaves.size)
    var index  = 0
    while (index < leaves.size) {
      val (offset, scalar) = leaves(index)
      val size             = program.scalarMemoryWidth(scalar) match {
        case Left(error)  => return Left(error)
        case Right(value) => value
      }
      memory.readScalar(
        pointer.copy(address = pointer.address + offset, pointee = scalar, matrixLayout = None),
        size
      ) match {
        case Left(error)  => return Left(error)
        case Right(value) => lanes(index) = value
      }
      index += 1
    }
    Right(VMValue.Bits(pointer.pointee, lanes))
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.return"))
  def storeMatrixStorage(
      program: VMProgram,
      memory: MemoryState,
      pointer: Pointer,
      value: VMValue.Bits
  ): Either[Fault, Unit] = {
    val leaves = program.matrixStorageLeaves(pointer.pointee, pointer.matrixLayout)
    if (leaves.size != value.lanes.length)
      return Left(Fault.InvalidRequest("matrix storage scalar count does not match"))
    var index = 0
    while (index < leaves.size) {
      val (offset, scalar) = leaves(index)
      val size             = program.scalarMemoryWidth(scalar) match {
        case Left(error)  => return Left(error)
        case Right(width) => width
      }
      memory.writeScalar(
        pointer.copy(address = pointer.address + offset, pointee = scalar, matrixLayout = None),
        value.lanes(index),
        size
      ) match {
        case Left(error) => return Left(error)
        case Right(())   => ()
      }
      index += 1
    }
    Right(())
  }

  def loadPointer(
      program: VMProgram,
      memory: MemoryState,
      source: Pointer,
      resultType: Int
  ): AddressSpace.ReadResult[VMValue] =
    program.runtimeType(resultType) match {
      case Some(RuntimeType.Pointer(resultStorage, resultPointee)) =>
        memory.readPointer(source, resultPointee, resultStorage).map(VMValue.Address(resultType, _))
      case value => Left(Fault.InvalidRequest(s"SPIR-V pointer load result type %$resultType is invalid: $value"))
    }

  def storePointer(
      program: VMProgram,
      memory: MemoryState,
      target: Pointer,
      sourceType: Int,
      source: Pointer
  ): Either[Fault, Unit] =
    (program.runtimeType(target.pointee), program.runtimeType(sourceType)) match {
      case (
            Some(RuntimeType.Pointer(targetStorage, targetPointee)),
            Some(RuntimeType.Pointer(sourceStorage, sourcePointee))
          )
          if target.pointee == sourceType && targetStorage == sourceStorage && targetPointee == sourcePointee &&
            (source.pointee == sourcePointee || program.untyped(sourcePointee)) =>
        memory.writePointer(target, source)
      case (_, Some(RuntimeType.Pointer(_, _))) if program.untyped(target.pointee) =>
        memory.writePointer(target, source)
      case (targetType, sourceValue) =>
        Left(Fault.InvalidRequest(
          s"SPIR-V pointer store type mismatch: target pointee %${target.pointee} is $targetType, source %$sourceType is $sourceValue"
        ))
    }

  inline def loadScalar(execution: InvocationState, pointer: Int, size: Int in 1 ~ 8, target: Int, typeId: Int)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = address(execution.values, pointer) match {
    case Left(error)   => Left(error)
    case Right(source) =>
      if (execution.memory.readLocalScalar(source, size, execution.values.scalars, target)) next
      else execution.memory.readScalar(source, size) match {
        case Left(error)   => Left(error)
        case Right(loaded) =>
          execution.values.updateScalar(target, typeId, loaded)
          next
      }
  }

  inline def loadAddress(
      execution: InvocationState,
      pointer: Int,
      target: Int,
      typeId: Int,
      instruction: Instruction
  )(
      inline next: => Flow[Unit]
  ): Flow[Unit] = address(execution.values, pointer) match {
    case Left(error)   => Left(error)
    case Right(source) => loadPointer(execution.values.program, execution.memory, source, typeId) match {
        case Left(error)  => Left(located(error, execution, instruction))
        case Right(value) =>
          execution.values.update(target, value)
          next
      }
  }

  inline def loadValue(execution: InvocationState, pointer: Int, target: Int, typeId: Int, size: Int >= 0)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = {
    val program = execution.values.program
    address(execution.values, pointer) match {
      case Left(error)   => Left(error)
      case Right(source) =>
        val loaded =
          if (mappedMatrixStorage(program, source))
            loadMatrixStorage(program, execution.memory, source).map(retag(_, typeId))
          else execution.memory.read(source, size).flatMap(program.decode(typeId, _))
        loaded match {
          case Left(error)  => Left(error)
          case Right(value) =>
            execution.values.update(target, value)
            next
        }
    }
  }

  inline def loadHandle(execution: InvocationState, pointer: Int, target: Int, typeId: Int, pointee: Int)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = address(execution.values, pointer) match {
    case Left(error)   => Left(error)
    case Right(source) =>
      val handle = if (pointee >= 0) source.copy(pointee = pointee) else source
      execution.values.update(target, VMValue.Address(typeId, handle))
      next
  }

  inline def loadSampled(execution: InvocationState, pointer: Int, target: Int, typeId: Int, imageType: Int)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = address(execution.values, pointer) match {
    case Left(error)   => Left(error)
    case Right(source) => ImageOps.sampledImage(execution.memory, source, imageType) match {
        case Left(error)  => Left(error)
        case Right(image) =>
          execution.values.update(target, VMValue.Address(typeId, image))
          next
      }
  }

  inline def storeScalar(
      execution: InvocationState,
      pointer: Int,
      source: Int,
      size: Int in 1 ~ 8,
      instruction: Instruction
  )(
      inline next: => Flow[Unit]
  ): Flow[Unit] = address(execution.values, pointer) match {
    case Left(error)   => Left(error)
    case Right(target) => execution.memory.writeScalar(target, execution.values.scalars(source), size) match {
        case Left(error) => Left(locatedFault(error, execution, instruction))
        case Right(())   => next
      }
  }

  inline def storeValue(execution: InvocationState, pointer: Int, source: Int, instruction: Instruction)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = {
    val values  = execution.values
    val program = values.program
    address(values, pointer) match {
      case Left(error)   => Left(error)
      case Right(target) =>
        val stored = values.get(source) match {
          case Some(VMValue.Address(sourceType, value)) =>
            storePointer(program, execution.memory, target, sourceType, value)
          case Some(value: VMValue.Bits) if mappedMatrixStorage(program, target) =>
            storeMatrixStorage(program, execution.memory, target, value)
          case Some(value) => program.encode(value).flatMap(
              execution.memory.write(target, _).left.map(locatedFault(_, execution, instruction))
            )
          case None => Left(undefined(source))
        }
        stored match {
          case Left(error) => Left(error)
          case Right(())   => next
        }
    }
  }

  inline def copyMemory(
      execution: InvocationState,
      target: Int,
      origin: Int
  )(inline next: => Flow[Unit]): Flow[Unit] = {
    val values  = execution.values
    val program = values.program
    address(values, target) match {
      case Left(error)        => Left(error)
      case Right(destination) => address(values, origin) match {
          case Left(error)   => Left(error)
          case Right(source) =>
            val copied =
              if (mappedMatrixStorage(program, destination) || mappedMatrixStorage(program, source))
                loadMatrixStorage(program, execution.memory, source).flatMap(
                  storeMatrixStorage(program, execution.memory, destination, _)
                )
              else execution.memory.copy(destination, source, program.typeSize(source.pointee))
            copied match {
              case Left(error) => Left(error)
              case Right(())   => next
            }
        }
    }
  }

  inline def copyMemorySized(execution: InvocationState, target: Int, origin: Int, size: Int)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = {
    val values = execution.values
    address(values, target) match {
      case Left(error)        => Left(error)
      case Right(destination) => address(values, origin) match {
          case Left(error)   => Left(error)
          case Right(source) => scalar(values, size).flatMap { rawSize =>
              rawSize.refine[Long in 0L ~ 2147483647L].left.map(_ =>
                Fault.InvalidRequest(s"OpCopyMemorySized byte count $rawSize is outside the supported range")
              )
            } match {
              case Left(error)      => Left(error)
              case Right(byteCount) =>
                execution.memory.copy(destination, source, byteCount.toIntExact.widen[Int >= 0]) match {
                  case Left(error) => Left(error)
                  case Right(())   => next
                }
            }
        }
    }
  }

  // Group copies advance byte ranges directly rather than constructing intermediate slices.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def asyncCopy(
      execution: InvocationState,
      target: Int,
      typeId: Int,
      scopeId: Int,
      destinationId: Int,
      sourceId: Int,
      countId: Int,
      strideId: Int,
      elementId: Int
  ): Flow[Unit] = {
    val values  = execution.values
    val program = values.program
    val memory  = execution.memory
    for {
      element     <- if (elementId < 0) Right(None) else scalar(values, elementId).map(Some(_))
      scope       <- scalar(values, scopeId)
      destination <- address(values, destinationId)
      source      <- address(values, sourceId)
      count       <- scalar(values, countId)
      strideValue <- scalar(values, strideId)
      _           <- Either.cond(
        scope == Scope.Workgroup,
        (),
        Fault.Unsupported(s"OpGroupAsyncCopy execution scope $scope is not Workgroup")
      )
      _ <- Either.cond(
        count >= 0L && strideValue > 0L,
        (),
        Fault.InvalidRequest(s"OpGroupAsyncCopy has invalid count/stride $count/$strideValue")
      )
      _ <- Either.cond(
        (destination.storageClass == StorageClass.Workgroup && source.storageClass == StorageClass.CrossWorkgroup) ||
          (destination.storageClass == StorageClass.CrossWorkgroup && source.storageClass == StorageClass.Workgroup),
        (),
        Fault.InvalidRequest(
          s"OpGroupAsyncCopy requires Workgroup/CrossWorkgroup pointers, got ${destination.storageClass}/${source.storageClass}"
        )
      )
      _ <- {
        val elementSize = element.fold(program.typeSize(destination.pointee).raw)(bytes =>
          if (bytes > 0L && bytes <= Int.MaxValue) bytes.toInt else 0
        )
        val invocation = execution.invocation
        if (elementSize <= 0) Left(Fault.InvalidRequest("OpGroupAsyncCopy element type has no data size"))
        else if (invocation.local(0) != 0L || invocation.local(1) != 0L || invocation.local(2) != 0L)
          Right(values.updateScalar(target, typeId, 0L))
        else
          try {
            val strideBytes = Math.multiplyExact(strideValue, elementSize.toLong)
            val packedBytes = Math.toIntExact(Math.multiplyExact(count, elementSize.toLong))
            val sourceBytes =
              if (destination.storageClass == StorageClass.Workgroup) {
                val sourceSpan =
                  if (count == 0L) 0
                  else Math.toIntExact(Math.addExact(Math.multiplyExact(count - 1L, strideBytes), elementSize.toLong))
                memory.read(source, sourceSpan.unsafe[Int >= 0]).map { strided =>
                  if (strideValue == 1L) strided
                  else {
                    val packed = Array.ofDim[Byte](packedBytes)
                    var index  = 0L
                    while (index < count) {
                      Array.copy(
                        strided,
                        Math.toIntExact(Math.multiplyExact(index, strideBytes)),
                        packed,
                        Math.toIntExact(Math.multiplyExact(index, elementSize.toLong)),
                        elementSize
                      )
                      index += 1L
                    }
                    packed
                  }
                }
              } else memory.read(source, packedBytes.unsafe[Int >= 0])
            sourceBytes.flatMap { packed =>
              if (destination.storageClass == StorageClass.CrossWorkgroup && strideValue != 1L) {
                @tailrec
                def writeElement(index: Long): Either[Fault, Unit] =
                  if (index == count) Right(())
                  else
                    memory.write(
                      destination.copy(address = destination.address + Math.multiplyExact(index, strideBytes)),
                      packed.slice(
                        Math.toIntExact(Math.multiplyExact(index, elementSize.toLong)),
                        Math.toIntExact(Math.multiplyExact(index + 1L, elementSize.toLong))
                      )
                    ) match {
                      case failure @ Left(_) => failure
                      case Right(())         => writeElement(index + 1L)
                    }
                writeElement(0L)
              } else memory.write(destination, packed)
            }.map(_ => values.updateScalar(target, typeId, 0L))
          } catch {
            case _: ArithmeticException | _: IllegalArgumentException =>
              Left(Fault.InvalidRequest("OpGroupAsyncCopy byte range is too large"))
          }
      }
    } yield ()
  }

  inline def groupAsyncCopy(
      execution: InvocationState,
      target: Int,
      typeId: Int,
      scope: Int,
      destination: Int,
      source: Int,
      count: Int,
      stride: Int,
      element: Int
  )(inline next: => Flow[Unit]): Flow[Unit] =
    asyncCopy(
      execution,
      target = target,
      typeId = typeId,
      scopeId = scope,
      destinationId = destination,
      sourceId = source,
      countId = count,
      strideId = stride,
      elementId = element
    ) match {
      case Left(error) => Left(error)
      case Right(())   => next
    }

  inline def waitEvents(execution: InvocationState, scopeId: Int, countId: Int, events: Int)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = {
    val values = execution.values
    (for {
      scope <- scalar(values, scopeId)
      count <- scalar(values, countId)
      _     <- address(values, events)
      _     <- Either.cond(
        scope == Scope.Workgroup,
        (),
        Fault.Unsupported(s"OpGroupWaitEvents execution scope $scope is not Workgroup")
      )
      _ <- Either.cond(count >= 0L, (), Fault.InvalidRequest(s"OpGroupWaitEvents has invalid event count $count"))
    } yield ()) match {
      case Left(error) => Left(error)
      case Right(())   => next
    }
  }

  inline def storageClass(space: Space): Int = space match {
    case Space.Global   => StorageClass.CrossWorkgroup
    case Space.Shared   => StorageClass.Workgroup
    case Space.Local    => StorageClass.Function
    case Space.Constant => StorageClass.UniformConstant
    case Space.Generic  => StorageClass.Generic
  }

  inline def spaced(space: Space, address: Long): Pointer = Pointer(address, 0, storageClass(space))

  def namedAccess(error: Fault | AddressSpace.Pending.type, what: String): Fault | AddressSpace.Pending.type =
    error match {
      case Fault.InvalidRequest(message) => Fault.InvalidRequest(s"$what: $message")
      case other                         => other
    }

  inline def load[A](execution: InvocationState, space: Space, address: Long, width: ScalarBitWidth)(
      inline next: Long => Flow[A]
  ): Flow[A] = execution.memory.readScalar(spaced(space, address), width.toScalarByteWidth) match {
    case Left(error)   => Left(error)
    case Right(loaded) => next(loaded)
  }

  inline def store[A](execution: InvocationState, space: Space, address: Long, value: Long, width: ScalarBitWidth)(
      inline next: => Flow[A]
  ): Flow[A] = execution.memory.writeScalar(spaced(space, address), value, width.toScalarByteWidth) match {
    case Left(error) => Left(error)
    case Right(())   => next
  }

  inline def loadBytes[A](execution: InvocationState, space: Space, address: Long, length: Long, what: String)(
      inline next: Array[Byte] => Flow[A]
  ): Flow[A] = length.refine[Long in 0L ~ 2147483647L] match {
    case Left(_)      => Left(Fault.InvalidRequest(s"$what byte count $length is outside the supported range"))
    case Right(count) => execution.memory.read(spaced(space, address), count.toIntExact.widen[Int >= 0]) match {
        case Left(error)  => Left(namedAccess(error, what))
        case Right(bytes) => next(bytes)
      }
  }

  inline def storeBytes[A](execution: InvocationState, space: Space, address: Long, data: Array[Byte], what: String)(
      inline next: => Flow[A]
  ): Flow[A] = execution.memory.write(spaced(space, address), data) match {
    case Left(error) => Left(namedAccess(error, what))
    case Right(())   => next
  }

  inline def variable(
      execution: InvocationState,
      target: Int,
      typeId: Int,
      storageClass: Int,
      initialiser: Option[Int]
  )(inline next: => Flow[Unit]): Flow[Unit] = {
    val program = execution.values.program
    val pointee = program.pointerPointee(typeId)
    val size    = program.typeSize(pointee).raw
    val bounded = program.vulkan && (storageClass == StorageClass.Private || storageClass == StorageClass.Function)
    execution.storage.allocate(size, program.typeAlignment(pointee)) match {
      case Left(error)    => Left(error)
      case Right(address) =>
        val pointer =
          Pointer(address, pointee, storageClass, bounds = Option.when(bounded)(address -> (address + size)))
        val stored: Either[Fault, Unit] = initialiser.flatMap(execution.values.get) match {
          case Some(VMValue.Address(_, value)) => execution.memory.writePointer(pointer, value)
          case Some(value)                     =>
            program.encode(value).flatMap(encoded => execution.memory.write(pointer, encoded.take(size)))
          case None => Right(())
        }
        stored match {
          case Left(error) => Left(error)
          case Right(())   =>
            execution.values.update(target, VMValue.Address(typeId, pointer))
            next
        }
    }
  }

  inline def variableLengthArray(
      execution: InvocationState,
      target: Int,
      typeId: Int,
      element: Int,
      length: Long
  )(inline next: => Flow[Unit]): Flow[Unit] = {
    val program = execution.values.program
    val size    = program.typeSize(element).toLong
    val bytes   = length * size
    if (length < 0L || bytes > Int.MaxValue.toLong)
      Left(Fault.InvalidRequest(s"OpVariableLengthArrayINTEL length $length is outside private storage"))
    else execution.storage.allocate(bytes.toInt, program.typeAlignment(element)) match {
      case Left(error)    => Left(error)
      case Right(address) =>
        execution.values.update(target, VMValue.Address(typeId, Pointer(address, element, StorageClass.Function)))
        next
    }
  }

  inline def saveMemory(
      execution: InvocationState,
      target: Int,
      typeId: Int
  )(inline next: => Flow[Unit]): Flow[Unit] = {
    val top     = apertureAddress(PrivateAperture, execution.storage.top)
    val pointee = execution.values.program.pointerPointee(typeId)
    execution.values.update(target, VMValue.Address(typeId, Pointer(top, pointee, StorageClass.Function)))
    next
  }

  inline def restoreMemory(execution: InvocationState, pointer: Int)(inline next: => Flow[Unit]): Flow[Unit] =
    address(execution.values, pointer) match {
      case Left(error)  => Left(error)
      case Right(saved) =>
        val offset = offsetOf(saved.address).raw
        if (aperture(saved.address) == PrivateAperture && offset <= execution.storage.top) {
          execution.storage.top = offset.toInt
          next
        } else Left(Fault.InvalidRequest("OpRestoreMemoryINTEL pointer is not a saved stack position below the top"))
    }

  inline def memorySemantics(execution: InvocationState, source: Int, target: Int, typeId: Int)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = {
    val values  = execution.values
    val program = values.program
    values.get(source) match {
      case Some(VMValue.Address(sourceType, pointer)) if program.runtimeType(sourceType).exists {
            case RuntimeType.Pointer(StorageClass.Generic, _) => true
            case _                                            => false
          } =>
        val semantics = storageClassOf(pointer.address) match {
          case StorageClass.Workgroup      => MemorySemantics.WorkgroupMemory
          case StorageClass.CrossWorkgroup => MemorySemantics.CrossWorkgroupMemory
          case _                           => 0
        }
        values.updateScalar(target, typeId, semantics.toLong)
        next
      case None    => Left(undefined(source))
      case Some(_) => Left(Fault.InvalidRequest("OpGenericPtrMemSemantics source is not a Generic pointer"))
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  inline def extract(execution: InvocationState, source: Int, offset: Int, count: Int, target: Int, typeId: Int)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = {
    val values = execution.values
    val lanes  = values.lanes(source)
    if (lanes == null) Left(undefined(source))
    else if (offset >= 0 && offset + count <= lanes.length) {
      if (count == 1) values.updateScalar(target, typeId, lanes(offset))
      else values.update(target, VMValue.Bits(typeId, lanes.slice(offset, offset + count)))
      next
    } else Left(Fault.InvalidRequest(s"OpCompositeExtract range $offset+$count exceeds ${lanes.length} scalar leaves"))
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
  inline def construct(execution: InvocationState, parts: Array[Int], leaves: Int, target: Int, typeId: Int)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = {
    val values = execution.values
    val result = Array.ofDim[Long](leaves)
    var cursor = 0
    var index  = 0
    var absent = -1
    while (index < parts.length && absent < 0) {
      val part = parts(index)
      if (values.program.scalarSlot(part)) {
        result(cursor) = values.scalars(part)
        cursor += 1
      } else values.lanes(part) match {
        case null  => absent = part
        case lanes =>
          Array.copy(lanes, 0, result, cursor, lanes.length)
          cursor += lanes.length
      }
      index += 1
    }
    if (absent >= 0) Left(undefined(absent))
    else { values.update(target, VMValue.Bits(typeId, result)); next }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  inline def insert(
      execution: InvocationState,
      source: Int,
      composite: Int,
      offset: Int,
      count: Int,
      target: Int,
      typeId: Int
  )(inline next: => Flow[Unit]): Flow[Unit] = {
    val values   = execution.values
    val inserted = values.lanes(source)
    val lanes    = values.lanes(composite)
    if (inserted == null || lanes == null) Left(Fault.Unsupported("composite insertion requires scalar data"))
    else if (inserted.length == count && offset + count <= lanes.length) {
      val result = lanes.clone()
      Array.copy(inserted, 0, result, offset, count)
      values.update(target, VMValue.Bits(typeId, result))
      next
    } else Left(Fault.InvalidRequest(
      s"OpCompositeInsert source has ${inserted.length} leaves, expected $count at offset $offset"
    ))
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
  inline def shuffle(
      execution: InvocationState,
      left: Int,
      right: Int,
      selection: Array[Long],
      target: Int,
      typeId: Int
  )(
      inline next: => Flow[Unit]
  ): Flow[Unit] = {
    val values = execution.values
    val first  = values.lanes(left)
    val second = values.lanes(right)
    if (first == null || second == null) Left(Fault.InvalidRequest("OpVectorShuffle operands must be numeric vectors"))
    else {
      val result = Array.ofDim[Long](selection.length)
      var index  = 0
      while (index < selection.length) {
        val selected = selection(index)
        result(index) =
          if (selected == 0xffffffffL) 0L
          else if (selected < first.length) first(selected.toInt)
          else second(selected.toInt - first.length)
        index += 1
      }
      values.update(target, VMValue.Bits(typeId, result))
      next
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  inline def transpose(execution: InvocationState, source: Int, rows: Int, columns: Int, target: Int, typeId: Int)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = {
    val values = execution.values
    val lanes  = values.lanes(source)
    if (lanes == null) Left(undefined(source))
    else if (lanes.length != rows * columns)
      Left(Fault.InvalidRequest("matrix Transpose operand/result shapes do not match"))
    else {
      values.update(
        target,
        VMValue.Bits(typeId, Array.tabulate(lanes.length)(lane => lanes(Matrices.transposed(lane, rows, columns))))
      )
      next
    }
  }

  def ownLeaf(execution: InvocationState, source: Int, index: Int, rows: Int, columns: Int, use: Int): Long = {
    val lane  = execution.invocation.subgroupLocal
    val lines = if (use == 1) columns else rows
    if (lane >= lines) 0L
    else if (use == 1) execution.values.leaf(source, index * columns + lane)
    else execution.values.leaf(source, lane * columns + index)
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  inline def eachLeaf(execution: InvocationState, sources: Array[Int], count: Int, target: Int, typeId: Int)(
      inline body: Int => Long
  )(inline next: => Flow[Unit]): Flow[Unit] = {
    val values = execution.values
    val absent = values.absent(sources)
    if (absent >= 0) Left(undefined(absent))
    else {
      val result = Array.ofDim[Long](count)
      var index  = 0
      while (index < count) {
        result(index) = body(index)
        index += 1
      }
      values.update(target, VMValue.Bits(typeId, result))
      next
    }
  }

  def strideBytes(program: VMProgram, pointer: Int, width: Int in 1 ~ 8): Long = {
    val pointee = program.runtimeType(program.valueType(pointer)) match {
      case Some(RuntimeType.Pointer(_, element)) if program.vulkan => program.typeSize(element).toLong
      case _                                                       => width.toLong
    }
    if (pointee > 0L) pointee else width.toLong
  }

  inline def pointerAt(values: InvocationValues, pointer: Int)(inline next: Pointer => Flow[Unit]): Flow[Unit] =
    values.objects(pointer) match {
      case VMValue.Address(_, found) => next(found)
      case _                         => address(values, pointer) match {
          case Left(error)  => Left(error)
          case Right(found) => next(found)
        }
    }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  inline def loadLeaves(
      execution: InvocationState,
      pointer: Int,
      count: Int,
      width: Int in 1 ~ 8,
      target: Int,
      typeId: Int
  )(
      inline offset: Int => Long
  )(inline next: => Flow[Unit]): Flow[Unit] = {
    val values = execution.values
    var result = Done
    pointerAt(values, pointer) { base =>
      val lanes = Array.ofDim[Long](count)
      var index = 0
      while (index < count && result.isRight) {
        execution.memory.readScalar(base.copy(address = base.address + offset(index)), width) match {
          case Left(error)  => result = Left(error)
          case Right(value) => lanes(index) = value
        }
        index += 1
      }
      if (result.isRight) values.update(target, VMValue.Bits(typeId, lanes))
      result
    } match {
      case Left(error) => Left(error)
      case _           => next
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  inline def storeLeaves(execution: InvocationState, pointer: Int, source: Int, count: Int, width: Int in 1 ~ 8)(
      inline offset: Int => Long
  )(inline next: => Flow[Unit]): Flow[Unit] = {
    val values = execution.values
    var result = Done
    pointerAt(values, pointer) { base =>
      bits(values, source) match {
        case Left(error)  => Left(error)
        case Right(lanes) =>
          var index = 0
          while (index < count && result.isRight) {
            result =
              execution.memory.writeScalar(base.copy(address = base.address + offset(index)), lanes(index), width)
            index += 1
          }
          result
      }
    } match {
      case Left(error) => Left(error)
      case _           => next
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  inline def loadElement(execution: InvocationState, pointer: Int, offset: Long, bytes: Long)(
      inline next: Long => Flow[Unit]
  ): Flow[Unit] = {
    var loaded = 0L
    pointerAt(execution.values, pointer) { base =>
      execution.memory.readScalar(base.copy(address = base.address + offset), bytes.toInt.unsafe[Int in 1 ~ 8]) match {
        case Left(error)  => Left(error)
        case Right(value) =>
          loaded = value
          Done
      }
    } match {
      case Left(error) => Left(error)
      case _           => next(loaded)
    }
  }

  inline def storeElement(execution: InvocationState, pointer: Int, offset: Long, bytes: Long, value: Long)(
      inline next: => Flow[Unit]
  ): Flow[Unit] =
    pointerAt(execution.values, pointer) { base =>
      execution.memory.writeScalar(base.copy(address = base.address + offset), value, bytes.toInt.unsafe[Int in 1 ~ 8])
    } match {
      case Left(error) => Left(error)
      case _           => next
    }

  private def absolute(address: Long, pointee: Int): Pointer = Pointer(address, pointee, StorageClass.UniformConstant)

  inline def loadAbsolute(execution: InvocationState, address: Long, offset: Long, bytes: Long, pointee: Int)(
      inline next: Long => Flow[Unit]
  ): Flow[Unit] =
    execution.memory.readScalar(absolute(address + offset, pointee), bytes.toInt.unsafe[Int in 1 ~ 8]) match {
      case Left(error)  => Left(error)
      case Right(value) => next(value)
    }

  inline def storeAbsolute(
      execution: InvocationState,
      address: Long,
      offset: Long,
      bytes: Long,
      value: Long,
      pointee: Int
  )(inline next: => Flow[Unit]): Flow[Unit] =
    execution.memory.writeScalar(absolute(address + offset, pointee), value, bytes.toInt.unsafe[Int in 1 ~ 8]) match {
      case Left(error) => Left(error)
      case _           => next
    }

  inline def loadLevel(execution: InvocationState, offset: Long, bytes: Long)(inline next: Long => Flow[Unit])
      : Flow[Unit] = {
    val base = execution.texels.get.pixels
    execution.memory.readScalar(base.copy(address = base.address + offset), bytes.toInt.unsafe[Int in 1 ~ 8]) match {
      case Left(error)  => Left(error)
      case Right(value) => next(value)
    }
  }

  inline def storeLevel(execution: InvocationState, offset: Long, bytes: Long, value: Long)(inline next: => Flow[Unit])
      : Flow[Unit] = {
    val base = execution.texels.get.pixels
    execution.memory.writeScalar(
      base.copy(address = base.address + offset),
      value,
      bytes.toInt.unsafe[Int in 1 ~ 8]
    ) match {
      case Left(error) => Left(error)
      case _           => next
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  inline def repeat(count: Long)(inline body: Long => Flow[Unit])(inline next: => Flow[Unit]): Flow[Unit] = {
    var result = Done
    var index  = 0L
    while (index < count && result.isRight) {
      result = body(index)
      index += 1L
    }
    result match {
      case Left(error) => Left(error)
      case _           => next
    }
  }

  inline def printf(execution: InvocationState, format: Int, arguments: Array[Int], target: Int, typeId: Int)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = emitPrintf(execution, format, arguments, target, typeId) match {
    case Left(error) => Left(error)
    case Right(())   => next
  }

  inline def emitPrintf(
      execution: InvocationState,
      format: Int,
      arguments: Array[Int],
      target: Int,
      typeId: Int
  ): Flow[Unit] = {
    val values = execution.values
    val memory = execution.memory
    for {
      formatPointer <- values.get(format) match {
        case Some(VMValue.Address(_, pointer)) => Right(pointer)
        case Some(_) => Left(Fault.InvalidRequest("OpenCL.std printf format is not a pointer"))
        case None    => Left(undefined(format))
      }
      text        <- readCString(memory, formatPointer)
      conversions <- printfConversions(text)
      _           <- Either.cond(
        conversions.size <= arguments.length,
        (),
        Fault.InvalidRequest(
          s"OpenCL.std printf format expects at least ${conversions.size} arguments, received ${arguments.length}"
        )
      )
      printed <- conversions.indices.foldLeft[Flow[Vector[Event.PrintArgument]]](Right(Vector.empty)) {
        (result, index) =>
          result.flatMap { found =>
            val source = arguments(index)
            values.get(source) match {
              case Some(VMValue.Bits(argumentType, lanes)) =>
                values.program.integerShape(argumentType) match {
                  case Some((width, _)) => Right(found :+ Event.PrintArgument.Integer(Vec.from(lanes), width))
                  case None             => values.program.floatingShape(argumentType) match {
                      case Some((width, _)) => Right(found :+ Event.PrintArgument.Floating(Vec.from(lanes), width))
                      case None             =>
                        Left(Fault.InvalidRequest(
                          s"OpenCL.std printf argument ${index + 1} is not an integer or floating-point value"
                        ))
                    }
                }
              case Some(VMValue.Address(_, pointer)) if conversions(index) == 's' =>
                readCString(memory, pointer).map(argument => found :+ Event.PrintArgument.Text(argument))
              case Some(VMValue.Address(_, pointer)) => Right(found :+ Event.PrintArgument.Pointer(pointer.address))
              case None                              => Left(undefined(source))
            }
          }
      }
      _ <- memory.handler(Event.Print(text, Vec.from(printed)))
      _ <- values.program.integerShape(typeId) match {
        case Some((_, 1)) => Right(values.updateScalar(target, typeId, 0L))
        case _            => Left(Fault.InvalidRequest("OpenCL.std printf result is not an integer scalar"))
      }
    } yield ()
  }

  // C strings read byte by byte so a terminator ends the read before any later fault.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return", "scalafix:DisableSyntax.var"))
  def readCString(memory: MemoryState, pointer: Pointer): AddressSpace.ReadResult[String] = {
    val bytes = mutable.ArrayBuffer.empty[Byte]
    var index = 0L
    while (index < 1024L * 1024L) {
      memory.read(pointer.copy(address = pointer.address + index), 1) match {
        case Left(error)                   => return Left(error)
        case Right(value) if value(0) == 0 => return Right(String(bytes.toArray, StandardCharsets.UTF_8))
        case Right(value)                  => bytes += value(0)
      }
      index += 1L
    }
    Left(Fault.InvalidRequest("OpenCL.std printf string exceeds 1 MiB"))
  }

  def dynamic(what: String): Either[Fault, Nothing] =
    Left(Fault.Unsupported(s"OpenCL.std printf dynamic $what is not implemented"))

  // Conversion scanning advances a cursor through flags, width, precision and length modifiers.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return", "scalafix:DisableSyntax.var"))
  def printfConversions(format: String): Either[Fault, Vec[Char]] = {
    val result = mutable.ArrayBuffer.empty[Char]
    var index  = 0
    while (index < format.length)
      if (format(index) != '%') index += 1
      else if (index + 1 < format.length && format(index + 1) == '%') index += 2
      else {
        index += 1
        while (index < format.length && "-+ #0".contains(format(index))) index += 1
        if (index < format.length && format(index) == '*')
          return dynamic("field widths")
        while (index < format.length && format(index).isDigit) index += 1
        if (index < format.length && format(index) == '.') {
          index += 1
          if (index < format.length && format(index) == '*')
            return dynamic("precision")
          while (index < format.length && format(index).isDigit) index += 1
        }
        if (index < format.length && format(index) == 'v') {
          index += 1
          while (index < format.length && format(index).isDigit) index += 1
        }
        while (index < format.length && (format(index) == 'h' || format(index) == 'l')) index += 1
        if (index >= format.length)
          return Left(Fault.InvalidRequest("OpenCL.std printf format ends within a conversion"))
        result += format(index).toLower
        index += 1
      }
    Right(Vec.from(result))
  }

  inline def loadVector(
      execution: InvocationState,
      offset: Int,
      pointer: Int,
      count: Int,
      size: Int in 1 ~ 8,
      target: Int,
      typeId: Int
  )(inline next: => Flow[Unit]): Flow[Unit] =
    loadElements(execution, offset, pointer, count, size, target, typeId) match {
      case Left(error) => Left(error)
      case Right(())   => next
    }

  inline def loadElements(
      execution: InvocationState,
      offset: Int,
      pointer: Int,
      count: Int,
      size: Int in 1 ~ 8,
      target: Int,
      typeId: Int
  ): Flow[Unit] = {
    val values = execution.values
    for {
      index     <- scalar(values, offset)
      source    <- address(values, pointer)
      component <- values.program.runtimeType(typeId) match {
        case Some(RuntimeType.VectorType(component, _)) => Right(component)
        case _ => Left(Fault.InvalidRequest("OpenCL.std vloadn result type is not a vector"))
      }
      stride = count.toLong * size
      _ <- Either.cond(
        !displacementOverflows(source.address, index, stride),
        (),
        displacementFault(source.address, index, stride)
      )
      base = source.address + index * stride
      data = Array.ofDim[Long](count)
      _ <- (0 until count).foldLeft[Flow[Unit]](Done) { (result, lane) =>
        result.flatMap(_ =>
          execution.memory.readScalar(source.copy(address = base + lane.toLong * size, pointee = component), size)
            .map(value => data(lane) = value)
        )
      }
    } yield values.update(target, VMValue.Bits(typeId, data))
  }

  inline def storeVector(
      execution: InvocationState,
      data: Int,
      offset: Int,
      pointer: Int,
      count: Int,
      size: Int in 1 ~ 8
  )(inline next: => Flow[Unit]): Flow[Unit] = storeElements(execution, data, offset, pointer, count, size) match {
    case Left(error) => Left(error)
    case Right(())   => next
  }

  inline def storeElements(
      execution: InvocationState,
      data: Int,
      offset: Int,
      pointer: Int,
      count: Int,
      size: Int in 1 ~ 8
  ): Flow[Unit] = {
    val values = execution.values
    for {
      lanes     <- Option(values.lanes(data)).toRight(undefined(data))
      component <- values.get(data) match {
        case Some(VMValue.Bits(dataType, _)) => values.program.runtimeType(dataType) match {
            case Some(RuntimeType.VectorType(component, _)) => Right(component)
            case _ => Left(Fault.InvalidRequest("OpenCL.std vstoren source is not a vector"))
          }
        case _ => Left(Fault.InvalidRequest("OpenCL.std vstoren source is not numeric"))
      }
      index  <- scalar(values, offset)
      target <- address(values, pointer)
      _      <- Either.cond(
        lanes.length == count,
        (),
        Fault.InvalidRequest(s"OpenCL.std vstoren has ${lanes.length} lanes, expected $count")
      )
      stride = count.toLong * size
      _ <- Either.cond(
        !displacementOverflows(target.address, index, stride),
        (),
        displacementFault(target.address, index, stride)
      )
      base = target.address + index * stride
      _ <- (0 until count).foldLeft[Flow[Unit]](Done) { (result, lane) =>
        result.flatMap(_ =>
          execution.memory.writeScalar(
            target.copy(address = base + lane.toLong * size, pointee = component),
            lanes(lane),
            size
          )
        )
      }
    } yield ()
  }

  inline def extractDynamic(execution: InvocationState, vector: Int, index: Int, target: Int, typeId: Int)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = {
    val values = execution.values
    (values.get(vector), values.get(index)) match {
      case (Some(VMValue.Bits(_, lanes)), Some(VMValue.Bits(_, Array(component)))) =>
        val selected = component.toInt
        if (selected < 0 || selected >= lanes.length)
          Left(Fault.InvalidRequest(s"OpVectorExtractDynamic index $selected is out of range"))
        else { values.updateScalar(target, typeId, lanes(selected)); next }
      case (None, _) => Left(undefined(vector))
      case (_, None) => Left(undefined(index))
      case _         => Left(Fault.Unsupported("OpVectorExtractDynamic requires a scalar index"))
    }
  }

  inline def insertDynamic(
      execution: InvocationState,
      vector: Int,
      component: Int,
      index: Int,
      target: Int,
      typeId: Int
  )(
      inline next: => Flow[Unit]
  ): Flow[Unit] = {
    val values = execution.values
    (values.get(vector), values.get(component), scalar(values, index)) match {
      case (Some(VMValue.Bits(_, lanes)), Some(VMValue.Bits(_, Array(value))), Right(selected)) =>
        val position = selected.toInt
        if (position < 0 || position >= lanes.length)
          Left(Fault.InvalidRequest(s"OpVectorInsertDynamic index $position is out of range"))
        else {
          val result = lanes.clone()
          result(position) = value
          values.update(target, VMValue.Bits(typeId, result))
          next
        }
      case (None, _, _)        => Left(undefined(vector))
      case (_, None, _)        => Left(undefined(component))
      case (_, _, Left(error)) => Left(error)
      case _                   => Left(Fault.InvalidRequest("OpVectorInsertDynamic requires scalar component"))
    }
  }

  // a handle carries no range, so the descriptor may span the largest a view has: every level, swizzle and pitch
  private val DescriptorBytes: Long = ImageDescriptor.Layout.LevelTable + 15L * ImageDescriptor.Layout.LevelBytes +
    ImageDescriptor.Layout.SwizzleBytes + ImageDescriptor.Layout.PitchBytes

  def handled(address: Long, typeId: Int): Pointer =
    Pointer(address, typeId, StorageClass.UniformConstant, Some(address -> (address + DescriptorBytes)))

  inline def convertHandle(execution: InvocationState, source: Int, target: Int, typeId: Int)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = scalar(execution.values, source) match {
    case Left(error)    => Left(error)
    case Right(address) =>
      execution.values.update(target, VMValue.Address(typeId, handled(address, typeId)))
      next
  }

  inline def rawAccessChain(
      execution: InvocationState,
      base: Int,
      stride: Int,
      element: Int,
      offset: Int,
      pointeeType: Int,
      target: Int,
      typeId: Int
  )(inline next: => Flow[Unit]): Flow[Unit] =
    rawAddress(execution, base, stride, element, offset, pointeeType, target, typeId) match {
      case Left(error) => Left(error)
      case Right(())   => next
    }

  def rawAddress(
      execution: InvocationState,
      base: Int,
      stride: Int,
      element: Int,
      offset: Int,
      pointeeType: Int,
      target: Int,
      typeId: Int
  ): Flow[Unit] = {
    val values = execution.values
    for {
      start   <- address(values, base)
      spacing <- scalar(values, stride)
      index   <- scalar(values, element)
      bytes   <- scalar(values, offset)
      moved = start.copy(address = start.address + spacing * index + bytes, pointee = pointeeType, matrixLayout = None)
    } yield values.update(target, VMValue.Address(typeId, moved))
  }

  inline def predicatedLoad(
      execution: InvocationState,
      predicate: Int,
      pointer: Int,
      default: Int,
      target: Int,
      typeId: Int,
      size: Int >= 0
  )(inline next: => Flow[Unit]): Flow[Unit] =
    predicatedRead(execution, predicate, pointer, default, target, typeId, size) match {
      case Left(error) => Left(error)
      case Right(())   => next
    }

  def predicatedRead(
      execution: InvocationState,
      predicate: Int,
      pointer: Int,
      default: Int,
      target: Int,
      typeId: Int,
      size: Int >= 0
  ): Flow[Unit] = {
    val values = execution.values
    scalar(values, predicate).flatMap(enabled =>
      if (enabled == 0L) transfer(values, default, target, typeId)
      else address(values, pointer).flatMap(execution.memory.read(_, size)).flatMap(values.program.decode(typeId, _))
        .map(values.update(target, _))
    )
  }

  inline def predicatedStore(execution: InvocationState, predicate: Int, pointer: Int, source: Int)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = predicatedWrite(execution, predicate, pointer, source) match {
    case Left(error) => Left(error)
    case Right(())   => next
  }

  def predicatedWrite(execution: InvocationState, predicate: Int, pointer: Int, source: Int): Flow[Unit] = {
    val values = execution.values
    scalar(values, predicate).flatMap(enabled =>
      if (enabled == 0L) Right(())
      else for {
        target <- address(values, pointer)
        value  <- values.get(source).toRight(undefined(source))
        bytes  <- values.program.encode(value)
        _      <- execution.memory.write(target, bytes)
      } yield ()
    )
  }

  inline def maskedGather(
      execution: InvocationState,
      pointers: Int,
      mask: Int,
      fill: Int,
      target: Int,
      typeId: Int,
      width: Int in 1 ~ 8
  )(inline next: => Flow[Unit]): Flow[Unit] =
    gatherLanes(execution, pointers, mask, fill, target, typeId, width) match {
      case Left(error) => Left(error)
      case Right(())   => next
    }

  def gatherLanes(
      execution: InvocationState,
      pointers: Int,
      mask: Int,
      fill: Int,
      target: Int,
      typeId: Int,
      width: Int in 1 ~ 8
  ): Flow[Unit] = {
    val values = execution.values
    for {
      element   <- vectorElement(values.program, typeId)
      storage   <- vectorStorage(values.program, pointers)
      addresses <- bits(values, pointers)
      selected  <- bits(values, mask)
      fills     <- bits(values, fill)
      result    <- gatherElements(execution.memory, addresses, selected, fills, element, width, storage)
    } yield values.update(target, VMValue.Bits(typeId, result))
  }

  private def vectorElement(program: VMProgram, vectorType: Int): Either[Fault, Int] =
    program.runtimeType(vectorType) match {
      case Some(RuntimeType.VectorType(element, _)) => Right(element)
      case _ => Left(Fault.InvalidRequest("SPIR-V masked access needs vector data"))
    }

  private def vectorStorage(program: VMProgram, pointers: Int): Either[Fault, Int] =
    program.runtimeType(program.valueType(pointers)) match {
      case Some(RuntimeType.VectorType(pointer, _)) => program.runtimeType(pointer) match {
          case Some(RuntimeType.Pointer(storage, _)) => Right(storage)
          case _ => Left(Fault.InvalidRequest("SPIR-V masked access needs a vector of pointers"))
        }
      case _ => Left(Fault.InvalidRequest("SPIR-V masked access needs a vector of pointers"))
    }

  @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
  private def gatherElements(
      memory: MemoryState,
      addresses: Array[Long],
      selected: Array[Long],
      fills: Array[Long],
      element: Int,
      width: Int in 1 ~ 8,
      storage: Int
  ): AddressSpace.ReadResult[Array[Long]] = {
    val result                                            = Array.ofDim[Long](addresses.length)
    var failure: Fault | AddressSpace.Pending.type | Null = null
    var lane                                              = 0
    while (lane < result.length && (failure eq null)) {
      if (selected(lane) == 0L) result(lane) = fills(lane % fills.length)
      else
        memory.readScalar(Pointer(addresses(lane), element, storage), width) match {
          case Left(error)  => failure = error
          case Right(value) => result(lane) = value
        }
      lane += 1
    }
    if (failure ne null) Left(failure) else Right(result)
  }

  inline def maskedScatter(
      execution: InvocationState,
      data: Int,
      pointers: Int,
      mask: Int,
      width: Int in 1 ~ 8
  )(inline next: => Flow[Unit]): Flow[Unit] = scatterLanes(execution, data, pointers, mask, width) match {
    case Left(error) => Left(error)
    case Right(())   => next
  }

  def scatterLanes(
      execution: InvocationState,
      data: Int,
      pointers: Int,
      mask: Int,
      width: Int in 1 ~ 8
  ): Flow[Unit] = {
    val values = execution.values
    for {
      element   <- vectorElement(values.program, values.program.valueType(data))
      storage   <- vectorStorage(values.program, pointers)
      lanes     <- bits(values, data)
      addresses <- bits(values, pointers)
      selected  <- bits(values, mask)
      _         <- scatterElements(execution.memory, lanes, addresses, selected, element, width, storage)
    } yield ()
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
  private def scatterElements(
      memory: MemoryState,
      data: Array[Long],
      addresses: Array[Long],
      selected: Array[Long],
      element: Int,
      width: Int in 1 ~ 8,
      storage: Int
  ): Either[Fault, Unit] = {
    var failure: Fault | Null = null
    var lane                  = 0
    while (lane < addresses.length && failure == null) {
      if (selected(lane) != 0L)
        memory.writeScalar(Pointer(addresses(lane), element, storage), data(lane), width) match {
          case Left(error) => failure = error
          case Right(())   => ()
        }
      lane += 1
    }
    if (failure != null) Left(failure) else Right(())
  }

  inline def convertSampledHandle(execution: InvocationState, source: Int, target: Int, typeId: Int)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = sampledHandle(execution, source, target, typeId) match {
    case Left(error) => Left(error)
    case Right(())   => next
  }

  def sampledHandle(execution: InvocationState, source: Int, target: Int, typeId: Int): Flow[Unit] = {
    val values = execution.values
    for {
      address <- scalar(values, source)
      image   <- values.program.runtimeType(typeId) match {
        case Some(RuntimeType.SampledImage(image)) => Right(image)
        case _ => Left(Fault.InvalidRequest("OpConvertHandleToSampledImageINTEL requires a sampled image result"))
      }
      sampled <- ImageOps.sampledImage(execution.memory, handled(address, typeId), image)
    } yield values.update(target, VMValue.Address(typeId, sampled))
  }

  inline def subArray(execution: InvocationState, source: Int, index: Int, target: Int, typeId: Int)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = subArrayLeaves(execution, source, index, target, typeId) match {
    case Left(error) => Left(error)
    case Right(())   => next
  }

  def subArrayLeaves(execution: InvocationState, source: Int, index: Int, target: Int, typeId: Int): Flow[Unit] = {
    val values = execution.values
    for {
      count <- values.program.scalarLeafCount(typeId)
      lanes <- bits(values, source)
      start <- scalar(values, index).map(_.toInt)
      _     <- Either.cond(
        start >= 0 && start + count <= lanes.length,
        (),
        Fault.InvalidRequest(s"OpExtractSubArrayQCOM range $start + $count exceeds ${lanes.length} elements")
      )
    } yield values.update(target, VMValue.Bits(typeId, lanes.slice(start, start + count)))
  }

  inline def copyValue(execution: InvocationState, source: Int, target: Int, typeId: Int)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = transfer(execution.values, source, target, typeId) match {
    case Left(error) => Left(error)
    case Right(())   => next
  }

  inline def conditionalCopy(execution: InvocationState, pairs: Array[Int], target: Int, typeId: Int)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = conditionalOperand(execution.values, pairs) match {
    case Left(error)    => Left(error)
    case Right(operand) => copyValue(execution, operand, target, typeId)(next)
  }

  inline def prefetch(execution: InvocationState, pointer: Int)(inline next: => Flow[Unit]): Flow[Unit] =
    address(execution.values, pointer) match {
      case Left(error) => Left(error)
      case Right(_)    => next
    }

  inline def pointerToInteger(execution: InvocationState, pointer: Int, mask: Long, target: Int, typeId: Int)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = address(execution.values, pointer) match {
    case Left(error)  => Left(error)
    case Right(found) =>
      execution.values.updateScalar(target, typeId, found.address & mask)
      next
  }

  inline def pointersToIntegers(execution: InvocationState, source: Int, mask: Long, target: Int, typeId: Int)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = bits(execution.values, source) match {
    case Left(error)      => Left(error)
    case Right(addresses) =>
      execution.values.update(target, VMValue.Bits(typeId, addresses.map(_ & mask)))
      next
  }

  inline def integerToPointer(
      execution: InvocationState,
      source: Int,
      pointee: Int,
      storageClass: Int,
      function: Boolean,
      target: Int,
      typeId: Int
  )(inline next: => Flow[Unit]): Flow[Unit] = {
    val values = execution.values
    values.get(source) match {
      case Some(VMValue.Bits(integerType, Array(rawAddress)))
          if values.program.integerShape(integerType).exists(_._2 == 1) =>
        if (function) values.update(target, VMValue.Bits(typeId, Array(rawAddress)))
        else values.update(
          target,
          VMValue.Address(
            typeId,
            Pointer(
              rawAddress & mask(values.program.scalarWidth(integerType).widen[Int in 1 ~ 64]),
              pointee,
              storageClass
            )
          )
        )
        next
      case Some(_) => Left(Fault.InvalidRequest("OpConvertUToPtr requires a scalar integer operand"))
      case None    => Left(undefined(source))
    }
  }

  inline def integersToPointers(execution: InvocationState, source: Int, lanes: Int, target: Int, typeId: Int)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = {
    val values = execution.values
    values.get(source) match {
      case Some(VMValue.Bits(integerType, found)) if found.length == lanes =>
        val bits = mask(values.program.scalarWidth(integerType).widen[Int in 1 ~ 64])
        values.update(target, VMValue.Bits(typeId, found.map(_ & bits)))
        next
      case Some(_) => Left(Fault.InvalidRequest("OpConvertUToPtr requires an integer vector of its lane count"))
      case None    => Left(undefined(source))
    }
  }

  inline def castToGeneric(execution: InvocationState, source: Int, cast: PointerCast, target: Int, typeId: Int)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = {
    val program = cast.program
    val values  = execution.values
    values.get(source) match {
      case Some(VMValue.Address(sourceType, pointer)) =>
        program.runtimeType(sourceType) match {
          case Some(RuntimeType.Pointer(storageClass, sourcePointee))
              if Addresses.castable.contains(storageClass) && sourcePointee == cast.pointee &&
                (pointer.pointee == cast.pointee || program.untyped(cast.pointee)) =>
            values.update(target, VMValue.Address(typeId, pointer.copy(storageClass = StorageClass.Generic)))
            next
          case Some(RuntimeType.Pointer(storageClass, sourcePointee)) =>
            Left(Fault.InvalidRequest(
              s"OpPtrCastToGeneric cannot cast storage class $storageClass pointee %$sourcePointee to %${cast.pointee}"
            ))
          case _ => Left(Fault.InvalidRequest("OpPtrCastToGeneric source type is not a pointer"))
        }
      case None    => Left(undefined(source))
      case Some(_) => Left(Fault.InvalidRequest("OpPtrCastToGeneric source is not a pointer"))
    }
  }

  inline def castFromGeneric(execution: InvocationState, source: Int, cast: PointerCast, target: Int, typeId: Int)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = {
    val program = cast.program
    val values  = execution.values
    values.get(source) match {
      case Some(VMValue.Address(sourceType, pointer)) =>
        program.runtimeType(sourceType) match {
          case Some(RuntimeType.Pointer(StorageClass.Generic, sourcePointee))
              if sourcePointee == cast.pointee && (pointer.pointee == cast.pointee || program.untyped(cast.pointee)) =>
            val converted =
              if (Addresses.keeps(pointer.address, cast.storageClass, cast.explicit))
                pointer.copy(
                  pointee = if (program.untyped(cast.pointee)) pointer.pointee else cast.pointee,
                  storageClass = cast.storageClass
                )
              else Pointer(0L, cast.pointee, cast.storageClass)
            values.update(target, VMValue.Address(typeId, converted))
            next
          case Some(RuntimeType.Pointer(sourceStorage, sourcePointee)) =>
            Left(Fault.InvalidRequest(
              s"${cast.opcode} cannot cast storage class $sourceStorage pointee %$sourcePointee" +
                (if (!cast.explicit) s" to storage class ${cast.storageClass} pointee %${cast.pointee}" else "")
            ))
          case _ => Left(Fault.InvalidRequest(s"${cast.opcode} source type is not a pointer"))
        }
      case None    => Left(undefined(source))
      case Some(_) => Left(Fault.InvalidRequest(s"${cast.opcode} source is not a pointer"))
    }
  }

  inline def arrayLength(
      execution: InvocationState,
      structure: Int,
      member: Int,
      declared: Option[Int],
      target: Int,
      typeId: Int
  )(
      inline next: => Flow[Unit]
  ): Flow[Unit] = {
    val values  = execution.values
    val program = values.program
    val result  = values.get(structure) match {
      case Some(VMValue.Address(_, located)) =>
        val pointer = declared.fold(located)(declaredType => located.copy(pointee = declaredType))
        program.runtimeType(pointer.pointee) match {
          case Some(RuntimeType.Structure(members)) =>
            if (member < 0 || member >= members.size)
              Left(Fault.InvalidRequest("OpArrayLength member is outside its structure"))
            else program.runtimeType(members(member)) match {
              case Some(RuntimeType.Array(element, count)) if count == 0L =>
                val stride = program.arrayStrides.get(members(member)).fold(program.typeSize(element).raw)(_.raw)
                pointer.bounds.toRight(Fault.InvalidRequest("OpArrayLength requires a bounded descriptor")).flatMap {
                  case (_, end) =>
                    val offset = pointer.address + program.structMemberOffset(pointer.pointee, members, member)
                    if (stride <= 0 || offset > end)
                      Left(Fault.InvalidRequest("OpArrayLength array is outside its descriptor"))
                    else Right((end - offset) / stride)
                }
              case _ => Left(Fault.InvalidRequest("OpArrayLength requires a runtime array member"))
            }
          case _ => Left(Fault.InvalidRequest("OpArrayLength requires a structure pointer"))
        }
      case _ => Left(Fault.InvalidRequest("OpArrayLength requires a pointer"))
    }
    result match {
      case Left(error)   => Left(error)
      case Right(length) =>
        values.updateScalar(target, typeId, length)
        next
    }
  }

  private inline def indexAt(values: InvocationValues, id: Int, inline fail: Fault => Unit): Long =
    if (values.program.scalarSlot(id)) values.scalars(id)
    else scalar(values, id) match {
      case Right(value) => value
      case Left(error)  =>
        fail(error)
        0L
    }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.null"))
  inline def accessChain(
      execution: InvocationState,
      base: Int,
      element: Int,
      indexes: Array[Int],
      chain: AccessChain,
      target: Int,
      typeId: Int
  )(inline next: => Flow[Unit]): Flow[Unit] = {
    val values = execution.values
    address(values, base) match {
      case Left(error)  => Left(error)
      case Right(start) =>
        var unreadable: Fault | Null = null
        AccessLayout.walk[Long, Flow[Unit]](
          chain = chain,
          start = start,
          element = element >= 0,
          count = indexes.length,
          origin = start.address,
          address = location => location,
          number = position => indexAt(values, indexes(position), error => unreadable = error),
          index =
            position => indexAt(values, if (position < 0) element else indexes(position), error => unreadable = error),
          pending = () => unreadable,
          plus = (offset, delta) => offset + delta,
          scaled = (offset, selected, stride) => offset + selected * stride,
          failed = error => Left(error),
          reached = (offset, reachedType, layout, source) => {
            values.update(
              target,
              VMValue.Address(
                typeId,
                source.copy(
                  address = offset,
                  pointee = if (chain.walked) reachedType else chain.pointee,
                  matrixLayout = layout
                )
              )
            )
            next
          }
        )
    }
  }

  private inline def flagTarget(
      values: InvocationValues,
      pointer: Int,
      scope: Int,
      semantics: Int,
      opcode: String
  ): Either[Fault, Pointer] = address(values, pointer) match {
    case Left(error)     => Left(error)
    case Right(location) =>
      if (
        !values.program.integerShape(location.pointee).exists { case (width, count) => width == 32 && count == 1 } &&
        !StagedOps.untypedPointer(values.program, pointer)
      )
        Left(Fault.InvalidRequest(s"$opcode requires a pointer to a 32-bit integer flag"))
      else if (!StagedOps.atomicStorage.contains(location.storageClass))
        Left(Fault.InvalidRequest(s"$opcode pointer storage class does not support atomic access"))
      else memoryControls(values, scope, semantics, opcode) match {
        case Left(error) => Left(error)
        case Right(_)    => Right(location)
      }
  }

  inline def atomicLoad(
      execution: InvocationState,
      pointer: Int,
      size: Int in 1 ~ 8,
      target: Int,
      typeId: Int,
      scope: Int,
      semantics: Int
  )(inline next: => Flow[Unit]): Flow[Unit] = {
    val values = execution.values
    StagedOps.target(values, pointer, typeId, "OpAtomicLoad") match {
      case Left(error)     => Left(error)
      case Right(location) => memoryControls(values, scope, semantics, "OpAtomicLoad") match {
          case Left(error) => Left(error)
          case Right(_)    => execution.memory.atomicReadScalar(location, size) match {
              case Left(error)   => Left(error)
              case Right(result) =>
                values.updateScalar(target, typeId, result & mask((size.raw * 8).unsafe[Int in 1 ~ 64]))
                next
            }
        }
    }
  }

  inline def atomicStore(
      execution: InvocationState,
      pointer: Int,
      source: Int,
      size: Int in 1 ~ 8,
      scope: Int,
      semantics: Int
  )(inline next: => Flow[Unit]): Flow[Unit] = {
    val values = execution.values
    StagedOps.target(values, pointer, values.program.valueType(source), "OpAtomicStore") match {
      case Left(error)     => Left(error)
      case Right(location) => memoryControls(values, scope, semantics, "OpAtomicStore") match {
          case Left(error) => Left(error)
          case Right(_)    =>
            val value = values.scalars(source) & mask((size.raw * 8).unsafe[Int in 1 ~ 64])
            execution.memory.atomicWriteScalar(location, value, size) match {
              case Left(error) => Left(error)
              case Right(())   => next
            }
        }
    }
  }

  inline def flagTestAndSet(
      execution: InvocationState,
      pointer: Int,
      target: Int,
      typeId: Int,
      scope: Int,
      semantics: Int
  )(inline next: => Flow[Unit]): Flow[Unit] = {
    val values = execution.values
    flagTarget(values, pointer, scope, semantics, "OpAtomicFlagTestAndSet") match {
      case Left(error)     => Left(error)
      case Right(location) => execution.memory.atomicModifyScalar(location, 4)(_ => 1L) match {
          case Left(error)     => Left(error)
          case Right(previous) =>
            values.updateScalar(target, typeId, if (previous == 0L) 0L else 1L)
            next
        }
    }
  }

  inline def flagClear(execution: InvocationState, pointer: Int, scope: Int, semantics: Int)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = {
    val values = execution.values
    flagTarget(values, pointer, scope, semantics, "OpAtomicFlagClear") match {
      case Left(error)     => Left(error)
      case Right(location) => scalar(values, semantics) match {
          case Left(error)  => Left(error)
          case Right(order) =>
            if ((order & 0xaL) != 0L)
              Left(Fault.InvalidRequest("OpAtomicFlagClear cannot use Acquire or AcquireRelease memory semantics"))
            else execution.memory.atomicWriteScalar(location, 0L, 4) match {
              case Left(error) => Left(error)
              case Right(())   => next
            }
        }
    }
  }

  /** Whether `semantics` names at most one of the Acquire, Release, AcquireRelease and SequentiallyConsistent orders.
    */
  inline def multipleMemoryOrders(semantics: Long): Boolean = {
    val order = semantics & 0x1eL
    (order & (order - 1L)) != 0L
  }

  inline def controlBarrier(execution: InvocationState, scope: Int, memory: Int, semantics: Int): Flow[Unit] = {
    val values = execution.values
    scalar(values, scope) match {
      case Left(error)  => Left(error)
      case Right(level) =>
        if (!isWorkgroupOrSubgroup(level)) Left(scopeFault("OpControlBarrier", level))
        else memoryControls(values, memory, semantics, "OpControlBarrier") match {
          case Left(error) => Left(error)
          case Right(_)    => scalar(values, memory) match {
              case Left(error)        => Left(error)
              case Right(memoryScope) => scalar(values, semantics) match {
                  case Left(error)  => Left(error)
                  case Right(flags) =>
                    if (multipleMemoryOrders(flags))
                      Left(
                        Fault.InvalidRequest(f"OpControlBarrier memory semantics 0x$flags%x are invalid or unsupported")
                      )
                    else suspend(
                      execution,
                      CollectiveRequest(
                        CollectiveKind.Barrier,
                        _,
                        _,
                        level.toInt,
                        CollectivePayload.Barrier(memoryScope.toInt, flags)
                      )
                    )
                }
            }
        }
    }
  }

  inline def arriveBarrier(execution: InvocationState, scope: Int, memory: Int, semantics: Int)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = {
    val values = execution.values
    scalar(values, scope) match {
      case Left(error)  => Left(error)
      case Right(level) =>
        if (!isWorkgroupOrSubgroup(level)) Left(scopeFault("OpControlBarrierArriveEXT", level))
        else memoryControls(values, memory, semantics, "OpControlBarrierArriveEXT") match {
          case Left(error) => Left(error)
          case Right(_)    => next
        }
    }
  }

  inline def fence(execution: InvocationState, scope: Int, semantics: Int)(inline next: => Flow[Unit]): Flow[Unit] =
    memoryControls(execution.values, scope, semantics, "OpMemoryBarrier") match {
      case Left(error) => Left(error)
      case Right(_)    => execution.memory.fence() match {
          case Left(error) => Left(error)
          case Right(())   => next
        }
    }

  inline def namedBarrier(execution: InvocationState, barrier: Int, memory: Int, semantics: Int): Flow[Unit] = {
    val values = execution.values
    scalar(values, barrier) match {
      case Left(error)  => Left(error)
      case Right(count) =>
        val subgroups = execution.invocation.numSubgroups
        if (count < subgroups)
          Left(Fault.Unsupported(s"OpMemoryNamedBarrier over $count of the workgroup's $subgroups subgroups"))
        else memoryControls(values, memory, semantics, "OpMemoryNamedBarrier") match {
          case Left(error) => Left(error)
          case Right(_)    => scalar(values, memory) match {
              case Left(error)        => Left(error)
              case Right(memoryScope) => scalar(values, semantics) match {
                  case Left(error)  => Left(error)
                  case Right(flags) => suspend(
                      execution,
                      CollectiveRequest(
                        CollectiveKind.Barrier,
                        _,
                        _,
                        Scope.Workgroup,
                        CollectivePayload.Barrier(memoryScope.toInt, flags)
                      )
                    )
                }
            }
        }
    }
  }

  inline def initializeBarrier(execution: InvocationState, target: Int, typeId: Int, count: Int)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = scalar(execution.values, count) match {
    case Left(error)  => Left(error)
    case Right(value) =>
      execution.values.updateScalar(target, typeId, value)
      next
  }
}

/** The machine operations on an invocation: control requests move its position, labels and call frames, and effects
  * stop at the first fault. A value class, so running an instruction allocates no machine.
  */
private class InvocationMachine(val execution: InvocationState) extends AnyVal
    with SPIRVMachine[Long, Array[Byte], Flow] {

  inline def pc: Long = execution.pc.toLong

  inline def advance: Flow[Unit] = {
    execution.pc += 1
    Done
  }

  inline def done: Flow[Unit] = Done

  inline def uniform(operand: Int): Long = read((), operand)

  inline def jump(target: Block): Flow[Unit] = InvocationMachine.jump(execution, target)

  inline def branch(condition: Long, taken: Block): Flow[Unit] =
    if (condition != 0L) InvocationMachine.jump(execution, taken) else advance

  inline def branch(condition: Long, taken: Block, otherwise: Block): Flow[Unit] =
    InvocationMachine.jump(execution, if (condition != 0L) taken else otherwise)

  inline def jumpTo(target: Long): Flow[Unit] = InvocationMachine.nowhere("computed jump")

  inline def jumpEach(inline target: Unit => Long): Flow[Unit] = jumpTo(target(()))

  inline def branchEach(inline condition: Unit => Long, taken: Block): Flow[Unit] = branch(condition(()), taken)

  inline def exit: Flow[Unit] = {
    execution.complete()
    Done
  }

  inline def park(barrier: Long, count: Long, inline blocking: Boolean): Flow[Unit] =
    InvocationMachine.nowhere("barrier")

  inline def everyLane[A](inline body: Unit => Flow[Unit])(inline next: => Flow[A]): Flow[A] = body(()) match {
    case Left(fault) => Left(fault)
    case _           => next
  }

  inline def read(lane: Unit, operand: Int): Long = execution.values.scalars(operand)

  inline def write[A](lane: Unit, target: Int, value: Long)(inline next: => Flow[A]): Flow[A] = {
    execution.values.scalars(target) = value
    next
  }

  inline def load[A](lane: Unit, inline space: Space, address: Long, width: ScalarBitWidth)(
      inline next: Long => Flow[A]
  ): Flow[A] = InvocationMachine.load(execution, space, address, width)(next)

  inline def store[A](lane: Unit, inline space: Space, address: Long, value: Long, width: ScalarBitWidth)(
      inline next: => Flow[A]
  ): Flow[A] = InvocationMachine.store(execution, space, address, value, width)(next)

  inline def loadBytes[A](lane: Unit, inline space: Space, address: Long, length: Long, what: String)(
      inline next: Array[Byte] => Flow[A]
  ): Flow[A] = InvocationMachine.loadBytes(execution, space, address, length, what)(next)

  inline def storeBytes[A](lane: Unit, inline space: Space, address: Long, data: Array[Byte], what: String)(
      inline next: => Flow[A]
  ): Flow[A] = InvocationMachine.storeBytes(execution, space, address, data, what)(next)

  inline def variable(target: Int, typeId: Int, storageClass: Int, initialiser: Option[Int])(
      inline next: => Flow[Unit]
  ): Flow[Unit] = InvocationMachine.variable(execution, target, typeId, storageClass, initialiser)(next)

  inline def variableLengthArray(target: Int, typeId: Int, element: Int, length: Long)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = InvocationMachine.variableLengthArray(execution, target, typeId, element, length)(next)

  inline def saveMemory(target: Int, typeId: Int)(inline next: => Flow[Unit]): Flow[Unit] =
    InvocationMachine.saveMemory(execution, target, typeId)(next)

  inline def restoreMemory(pointer: Int)(inline next: => Flow[Unit]): Flow[Unit] =
    InvocationMachine.restoreMemory(execution, pointer)(next)

  inline def memorySemantics(source: Int, target: Int, typeId: Int)(inline next: => Flow[Unit]): Flow[Unit] =
    InvocationMachine.memorySemantics(execution, source, target, typeId)(next)

  inline def compute(primitive: Primitive[Primitive.Unary], first: Long): Long = primitive.function(first)

  inline def compute(primitive: Primitive[Primitive.Binary], first: Long, second: Long): Long =
    primitive.function(first, second)

  inline def compute(primitive: Primitive[Primitive.Ternary], first: Long, second: Long, third: Long): Long =
    primitive.function(first, second, third)

  inline def require[A](condition: Long, label: String)(inline fault: => Fault)(inline next: => Flow[A]): Flow[A] =
    if (condition != 0L) next else Left(fault)

  inline def scalar(operand: Int)(inline next: Long => Flow[Unit]): Flow[Unit] = {
    def continue(value: Long): Flow[Unit] = next(value)
    val values                            = execution.values
    if (values.program.scalarSlot(operand)) continue(values.scalars(operand))
    else Op.scalar(values, operand) match {
      case Left(fault)  => Left(fault)
      case Right(value) => continue(value)
    }
  }

  inline def predecessor: Long = execution.predecessor.toLong

  inline def enter(label: Int): Flow[Unit] = InvocationMachine.enter(execution, label)

  inline def phi(group: Phis): Flow[Unit] = InvocationMachine.phi(execution, group)

  inline def switch(selector: Long, cases: Cases): Flow[Unit] = InvocationMachine.switch(execution, selector, cases)

  inline def call(callee: Callee): Flow[Unit] = InvocationMachine.call(execution, callee)

  inline def callIndirect(call: Indirect): Flow[Unit] = InvocationMachine.callIndirect(execution, call)

  inline def sweep(sweep: Sweep)(inline argument: (Int, Long, Int) => Long)(
      inline next: (Int => Long) => Flow[Unit]
  ): Flow[Unit] = InvocationMachine.sweep(execution, sweep)(argument)(next)

  inline def returnControl: Flow[Unit] = InvocationMachine.returnControl(execution)

  inline def returnValue(value: Int): Flow[Unit] = InvocationMachine.returnValue(execution, value)

  inline def kill: Flow[Unit] = InvocationMachine.discard(execution)

  inline def terminate: Flow[Unit] = InvocationMachine.discard(execution)

  inline def unreachable: Flow[Unit] = Left(InvocationMachine.unreachableFault)

  inline def abort(message: Int): Flow[Unit] = InvocationMachine.abort(execution, message)

  inline def loadScalar(pointer: Int, size: Int in 1 ~ 8, target: Int, typeId: Int)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = InvocationMachine.loadScalar(execution, pointer, size, target, typeId)(next)

  inline def loadAddress(pointer: Int, target: Int, typeId: Int, instruction: Instruction)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = InvocationMachine.loadAddress(execution, pointer, target, typeId, instruction)(next)

  inline def loadValue(pointer: Int, target: Int, typeId: Int, size: Int >= 0)(inline next: => Flow[Unit]): Flow[Unit] =
    InvocationMachine.loadValue(execution, pointer, target, typeId, size)(next)

  inline def loadHandle(pointer: Int, target: Int, typeId: Int, pointee: Int)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = InvocationMachine.loadHandle(execution, pointer, target, typeId, pointee)(next)

  inline def loadSampled(
      pointer: Int,
      target: Int,
      typeId: Int,
      imageType: Int
  )(inline next: => Flow[Unit]): Flow[Unit] =
    InvocationMachine.loadSampled(execution, pointer, target, typeId, imageType)(next)

  inline def storeScalar(pointer: Int, source: Int, size: Int in 1 ~ 8, instruction: Instruction)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = InvocationMachine.storeScalar(execution, pointer, source, size, instruction)(next)

  inline def storeValue(pointer: Int, source: Int, instruction: Instruction)(inline next: => Flow[Unit]): Flow[Unit] =
    InvocationMachine.storeValue(execution, pointer, source, instruction)(next)

  inline def copyMemory(target: Int, origin: Int)(inline next: => Flow[Unit]): Flow[Unit] =
    InvocationMachine.copyMemory(execution, target, origin)(next)

  inline def copyMemorySized(target: Int, origin: Int, size: Int)(inline next: => Flow[Unit]): Flow[Unit] =
    InvocationMachine.copyMemorySized(execution, target, origin, size)(next)

  inline def groupAsyncCopy(
      target: Int,
      typeId: Int,
      scope: Int,
      destination: Int,
      source: Int,
      count: Int,
      stride: Int,
      element: Int
  )(inline next: => Flow[Unit]): Flow[Unit] =
    InvocationMachine.groupAsyncCopy(
      execution,
      target,
      typeId,
      scope,
      destination,
      source,
      count,
      stride,
      element
    )(next)

  inline def waitEvents(scope: Int, count: Int, events: Int)(inline next: => Flow[Unit]): Flow[Unit] =
    InvocationMachine.waitEvents(execution, scope, count, events)(next)

  inline def atomicLoad(pointer: Int, size: Int in 1 ~ 8, target: Int, typeId: Int, scope: Int, semantics: Int)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = InvocationMachine.atomicLoad(execution, pointer, size, target, typeId, scope, semantics)(next)

  inline def atomicStore(pointer: Int, source: Int, size: Int in 1 ~ 8, scope: Int, semantics: Int)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = InvocationMachine.atomicStore(execution, pointer, source, size, scope, semantics)(next)

  inline def flagTestAndSet(pointer: Int, target: Int, typeId: Int, scope: Int, semantics: Int)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = InvocationMachine.flagTestAndSet(execution, pointer, target, typeId, scope, semantics)(next)

  inline def flagClear(pointer: Int, scope: Int, semantics: Int)(inline next: => Flow[Unit]): Flow[Unit] =
    InvocationMachine.flagClear(execution, pointer, scope, semantics)(next)

  inline def controlBarrier(scope: Int, memory: Int, semantics: Int): Flow[Unit] =
    InvocationMachine.controlBarrier(execution, scope, memory, semantics)

  inline def arriveBarrier(scope: Int, memory: Int, semantics: Int)(inline next: => Flow[Unit]): Flow[Unit] =
    InvocationMachine.arriveBarrier(execution, scope, memory, semantics)(next)

  inline def fence(scope: Int, semantics: Int)(inline next: => Flow[Unit]): Flow[Unit] =
    InvocationMachine.fence(execution, scope, semantics)(next)

  inline def namedBarrier(barrier: Int, memory: Int, semantics: Int): Flow[Unit] =
    InvocationMachine.namedBarrier(execution, barrier, memory, semantics)

  inline def initializeBarrier(target: Int, typeId: Int, count: Int)(inline next: => Flow[Unit]): Flow[Unit] =
    InvocationMachine.initializeBarrier(execution, target, typeId, count)(next)

  inline def collective(collective: Collective): Flow[Unit] = CollectiveMachine.run(execution, collective)

  inline def extract(source: Int, offset: Int, count: Int, target: Int, typeId: Int)(inline next: => Flow[Unit])
      : Flow[Unit] = InvocationMachine.extract(execution, source, offset, count, target, typeId)(next)

  inline def construct(
      parts: Array[Int],
      leaves: Int,
      target: Int,
      typeId: Int
  )(inline next: => Flow[Unit]): Flow[Unit] =
    InvocationMachine.construct(execution, parts, leaves, target, typeId)(next)

  inline def insert(source: Int, composite: Int, offset: Int, count: Int, target: Int, typeId: Int)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = InvocationMachine.insert(execution, source, composite, offset, count, target, typeId)(next)

  inline def shuffle(left: Int, right: Int, selection: Array[Long], target: Int, typeId: Int)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = InvocationMachine.shuffle(execution, left, right, selection, target, typeId)(next)

  inline def transpose(source: Int, rows: Int, columns: Int, target: Int, typeId: Int)(inline next: => Flow[Unit])
      : Flow[Unit] = InvocationMachine.transpose(execution, source, rows, columns, target, typeId)(next)

  inline def eachLeaf(sources: Array[Int], count: Int, target: Int, typeId: Int)(inline body: Int => Long)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = InvocationMachine.eachLeaf(execution, sources, count, target, typeId)(body)(next)

  inline def strideBytes(pointer: Int, width: Int in 1 ~ 8): Long =
    InvocationMachine.strideBytes(execution.values.program, pointer, width)

  inline def subgroupLocal: Long = execution.invocation.subgroupLocal.toLong

  inline def subgroupSize: Long = execution.invocation.maxSubgroupSize.toLong

  inline def loadLeaves(pointer: Int, count: Int, width: Int in 1 ~ 8, target: Int, typeId: Int)(
      inline offset: Int => Long
  )(inline next: => Flow[Unit]): Flow[Unit] =
    InvocationMachine.loadLeaves(execution, pointer, count, width, target, typeId)(offset)(next)

  inline def storeLeaves(pointer: Int, source: Int, count: Int, width: Int in 1 ~ 8)(inline offset: Int => Long)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = InvocationMachine.storeLeaves(execution, pointer, source, count, width)(offset)(next)

  inline def loadElement(pointer: Int, offset: Long, bytes: Long)(inline next: Long => Flow[Unit]): Flow[Unit] =
    InvocationMachine.loadElement(execution, pointer, offset, bytes)(next)

  inline def storeElement(
      pointer: Int,
      offset: Long,
      bytes: Long,
      value: Long
  )(inline next: => Flow[Unit]): Flow[Unit] =
    InvocationMachine.storeElement(execution, pointer, offset, bytes, value)(next)

  inline def loadAbsolute(address: Long, offset: Long, bytes: Long, pointee: Int)(
      inline next: Long => Flow[Unit]
  ): Flow[Unit] = InvocationMachine.loadAbsolute(execution, address, offset, bytes, pointee)(next)

  inline def storeAbsolute(address: Long, offset: Long, bytes: Long, value: Long, pointee: Int)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = InvocationMachine.storeAbsolute(execution, address, offset, bytes, value, pointee)(next)

  inline def loadLevel(offset: Long, bytes: Long)(inline next: Long => Flow[Unit]): Flow[Unit] =
    InvocationMachine.loadLevel(execution, offset, bytes)(next)

  inline def storeLevel(offset: Long, bytes: Long, value: Long)(inline next: => Flow[Unit]): Flow[Unit] =
    InvocationMachine.storeLevel(execution, offset, bytes, value)(next)

  inline def repeat(count: Long)(inline body: Long => Flow[Unit])(inline next: => Flow[Unit]): Flow[Unit] =
    InvocationMachine.repeat(count)(body)(next)

  inline def partials(count: Int)(inline next: => Flow[Unit]): Flow[Unit] = {
    execution.partials = Array.ofDim[Long](count)
    next
  }

  inline def setPartial(lane: Long, value: Long)(inline next: => Flow[Unit]): Flow[Unit] = {
    execution.partials(lane.toInt) = value
    next
  }

  inline def partial(lane: Long): Long = execution.partials(lane.toInt)

  inline def vulkan: Long = if (execution.values.program.vulkan) 1L else 0L

  inline def integral(operand: Int): Long = ImageMachine.integral(execution, operand)

  inline def count(operand: Int): Long = ImageMachine.count(execution, operand)

  inline def numeric(operand: Int)(inline next: => Flow[Unit]): Flow[Unit] =
    ImageMachine.numeric(execution, operand) match {
      case Left(error) => Left(error)
      case _           => next
    }

  inline def view(image: Int, query: Boolean)(inline next: (Long, Long, Long) => Flow[Unit]): Flow[Unit] =
    ImageMachine.view(execution, image, query) match {
      case Left(error)  => Left(error)
      case Right(found) => next(found & 0xffL, (found >> 8) & 1L, (found >> 16) & 1L)
    }

  inline def sampler(image: Int, missing: NonEmpty[String])(inline next: => Flow[Unit]): Flow[Unit] =
    ImageMachine.samplerPresent(execution, image, missing) match {
      case Left(error) => Left(error)
      case _           => next
    }

  inline def samplerWords(image: Int, into: Int)(inline next: => Flow[Unit]): Flow[Unit] =
    ImageMachine.samplerWords(execution, image, into) match {
      case Left(error) => Left(error)
      case _           => next
    }

  inline def samplerWord(image: Int, word: Int)(inline next: Long => Flow[Unit]): Flow[Unit] =
    ImageMachine.samplerWord(execution, image, word) match {
      case Left(error)  => Left(error)
      case Right(found) => next(found)
    }

  inline def formatWord(image: Int)(inline next: Long => Flow[Unit]): Flow[Unit] =
    ImageMachine.formatWord(execution, image) match {
      case Left(error)  => Left(error)
      case Right(found) => next(found)
    }

  inline def levelCount(image: Int)(inline next: Long => Flow[Unit]): Flow[Unit] =
    ImageMachine.levelCount(execution, image) match {
      case Left(error)  => Left(error)
      case Right(found) => next(found)
    }

  inline def level(image: Int, level: Long, into: Int)(inline next: => Flow[Unit]): Flow[Unit] =
    ImageMachine.level(execution, image, level, into) match {
      case Left(error) => Left(error)
      case _           => next
    }

  inline def texel(layer: Long, x: Long, row: Long, into: Int)(inline next: => Flow[Unit]): Flow[Unit] =
    ImageMachine.texel(execution, layer, x, row, into) match {
      case Left(error) => Left(error)
      case _           => next
    }

  inline def swizzle(image: Int, from: Long)(inline next: => Flow[Unit]): Flow[Unit] =
    ImageMachine.swizzle(execution, image, from) match {
      case Left(error) => Left(error)
      case _           => next
    }

  inline def gradients(from: Int, count: Long, resultType: Int, dest: Int, into: Int)(
      inline next: Long => Flow[Unit]
  ): Flow[Unit] = ImageMachine.gradients(execution, from, count, resultType, dest, into) match {
    case Left(error) => Left(error)
    case Right(-1L)  => Done
    case Right(flag) => next(flag)
  }

  inline def lodResult(typeId: Int)(inline next: => Flow[Unit]): Flow[Unit] =
    ImageMachine.lodResult(execution, typeId) match {
      case Left(error) => Left(error)
      case _           => next
    }

  inline def when(condition: Long)(inline body: => Flow[Unit])(inline next: => Flow[Unit]): Flow[Unit] =
    (if (condition != 0L) body else Done) match {
      case Left(error) => Left(error)
      case _           => next
    }

  inline def leaf(source: Int, index: Int): Long = execution.values.leaf(source, index)

  inline def leafAt(sources: Array[Int], values: Int, index: Long): Long =
    execution.values.leafAt(sources, values, index.toInt)

  inline def ownLeaf(source: Int, index: Int, rows: Int, columns: Int, use: Int): Long =
    InvocationMachine.ownLeaf(execution, source, index, rows, columns, use)

  inline def printf(
      format: Int,
      arguments: Array[Int],
      target: Int,
      typeId: Int
  )(inline next: => Flow[Unit]): Flow[Unit] =
    InvocationMachine.printf(execution, format, arguments, target, typeId)(next)

  inline def loadVector(offset: Int, pointer: Int, count: Int, size: Int in 1 ~ 8, target: Int, typeId: Int)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = InvocationMachine.loadVector(execution, offset, pointer, count, size, target, typeId)(next)

  inline def storeVector(data: Int, offset: Int, pointer: Int, count: Int, size: Int in 1 ~ 8)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = InvocationMachine.storeVector(execution, data, offset, pointer, count, size)(next)

  inline def convertHandle(source: Int, target: Int, typeId: Int)(inline next: => Flow[Unit]): Flow[Unit] =
    InvocationMachine.convertHandle(execution, source, target, typeId)(next)

  inline def rawAccessChain(
      base: Int,
      stride: Int,
      element: Int,
      offset: Int,
      pointeeType: Int,
      target: Int,
      typeId: Int
  )(inline next: => Flow[Unit]): Flow[Unit] =
    InvocationMachine.rawAccessChain(execution, base, stride, element, offset, pointeeType, target, typeId)(next)

  inline def predicatedLoad(predicate: Int, pointer: Int, default: Int, target: Int, typeId: Int, size: Int >= 0)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = InvocationMachine.predicatedLoad(execution, predicate, pointer, default, target, typeId, size)(next)

  inline def predicatedStore(predicate: Int, pointer: Int, source: Int)(inline next: => Flow[Unit]): Flow[Unit] =
    InvocationMachine.predicatedStore(execution, predicate, pointer, source)(next)

  inline def maskedGather(pointers: Int, mask: Int, fill: Int, target: Int, typeId: Int, width: Int in 1 ~ 8)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = InvocationMachine.maskedGather(execution, pointers, mask, fill, target, typeId, width)(next)

  inline def maskedScatter(
      data: Int,
      pointers: Int,
      mask: Int,
      width: Int in 1 ~ 8
  )(inline next: => Flow[Unit]): Flow[Unit] =
    InvocationMachine.maskedScatter(execution, data, pointers, mask, width)(next)

  inline def convertSampledHandle(source: Int, target: Int, typeId: Int)(inline next: => Flow[Unit]): Flow[Unit] =
    InvocationMachine.convertSampledHandle(execution, source, target, typeId)(next)

  inline def subArray(source: Int, index: Int, target: Int, typeId: Int)(inline next: => Flow[Unit]): Flow[Unit] =
    InvocationMachine.subArray(execution, source, index, target, typeId)(next)

  inline def extractDynamic(vector: Int, index: Int, target: Int, typeId: Int)(inline next: => Flow[Unit]): Flow[Unit] =
    InvocationMachine.extractDynamic(execution, vector, index, target, typeId)(next)

  inline def insertDynamic(vector: Int, component: Int, index: Int, target: Int, typeId: Int)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = InvocationMachine.insertDynamic(execution, vector, component, index, target, typeId)(next)

  inline def readClock(scope: Int, target: Int, typeId: Int, halves: Boolean)(inline next: => Flow[Unit]): Flow[Unit] =
    scalar(scope) { value =>
      val ticks =
        if (value == Scope.Device || value == Scope.CrossDevice)
          Right(InvocationMachine.origin + monotonicNanos)
        else if (value == Scope.Workgroup || value == Scope.Subgroup) Right(execution.issued)
        else Left(Fault.InvalidRequest(s"OpReadClockKHR has invalid scope $value"))
      ticks match {
        case Left(fault) => Left(fault)
        case Right(time) =>
          if (halves) execution.values.update(target, VMValue.Bits(typeId, Array(time & 0xffffffffL, time >>> 32)))
          else execution.values.updateScalar(target, typeId, time)
          next
      }
    }

  inline def zero(target: Int, typeId: Int, opcode: String)(inline next: => Flow[Unit]): Flow[Unit] =
    zeroValue(execution.values.program.types, typeId, target, opcode) match {
      case Left(fault)     => Left(fault)
      case Right(constant) =>
        execution.values.update(target, constant.value)
        next
    }

  inline def copyValue(source: Int, target: Int, typeId: Int)(inline next: => Flow[Unit]): Flow[Unit] =
    InvocationMachine.copyValue(execution, source, target, typeId)(next)

  inline def conditionalCopy(pairs: Array[Int], target: Int, typeId: Int)(inline next: => Flow[Unit]): Flow[Unit] =
    InvocationMachine.conditionalCopy(execution, pairs, target, typeId)(next)

  inline def prefetch(pointer: Int)(inline next: => Flow[Unit]): Flow[Unit] =
    InvocationMachine.prefetch(execution, pointer)(next)

  inline def accessChain(base: Int, element: Int, indexes: Array[Int], chain: AccessChain, target: Int, typeId: Int)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = InvocationMachine.accessChain(execution, base, element, indexes, chain, target, typeId)(next)

  inline def pointerToInteger(
      pointer: Int,
      mask: Long,
      target: Int,
      typeId: Int
  )(inline next: => Flow[Unit]): Flow[Unit] =
    InvocationMachine.pointerToInteger(execution, pointer, mask, target, typeId)(next)

  inline def pointersToIntegers(source: Int, mask: Long, target: Int, typeId: Int)(inline next: => Flow[Unit])
      : Flow[Unit] = InvocationMachine.pointersToIntegers(execution, source, mask, target, typeId)(next)

  inline def integerToPointer(
      source: Int,
      pointee: Int,
      storageClass: Int,
      function: Boolean,
      target: Int,
      typeId: Int
  )(inline next: => Flow[Unit]): Flow[Unit] =
    InvocationMachine.integerToPointer(execution, source, pointee, storageClass, function, target, typeId)(next)

  inline def integersToPointers(source: Int, lanes: Int, target: Int, typeId: Int)(inline next: => Flow[Unit])
      : Flow[Unit] = InvocationMachine.integersToPointers(execution, source, lanes, target, typeId)(next)

  inline def castToGeneric(source: Int, cast: PointerCast, target: Int, typeId: Int)(inline next: => Flow[Unit])
      : Flow[Unit] = InvocationMachine.castToGeneric(execution, source, cast, target, typeId)(next)

  inline def castFromGeneric(source: Int, cast: PointerCast, target: Int, typeId: Int)(inline next: => Flow[Unit])
      : Flow[Unit] = InvocationMachine.castFromGeneric(execution, source, cast, target, typeId)(next)

  inline def arrayLength(structure: Int, member: Int, declared: Option[Int], target: Int, typeId: Int)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = InvocationMachine.arrayLength(execution, structure, member, declared, target, typeId)(next)

  inline def image(instruction: Instruction): Flow[Unit] = ImageOps.execute(execution, instruction)

  inline def scalarValue(target: Int, typeId: Int, value: Long)(inline next: => Flow[Unit]): Flow[Unit] = {
    execution.values.updateScalar(target, typeId, value)
    next
  }

  inline def cooperativeVector(instruction: Instruction): Flow[Unit] =
    CooperativeVectorOps.execute(execution, instruction)

  inline def pipe(instruction: Instruction): Flow[Unit] = PipeOps.execute(execution, instruction)

  inline def deviceEnqueue(instruction: Instruction): Flow[Unit] = DeviceEnqueueOps.execute(execution, instruction)

  inline def tensorAddressing(instruction: Instruction): Flow[Unit] =
    TensorAddressingOps.execute(execution, instruction)
}
