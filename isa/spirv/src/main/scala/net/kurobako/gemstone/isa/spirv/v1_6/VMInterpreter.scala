package net.kurobako.gemstone.isa.spirv.v1_6

import VMIR.*
import VMCollectives.{
  Kind as CollectiveKind,
  Payload as CollectivePayload,
  Request as CollectiveRequest,
  openCLBroadcastScope
}
import VMInvocation.*
import VMMemory.*

import net.kurobako.gemstone.{PlatformTime, Vec}

import net.kurobako.gemstone.binary.Bits.bitCount
import net.kurobako.gemstone.binary.Bits.mask
import net.kurobako.gemstone.binary.Bits.signExtend
import net.kurobako.gemstone.binary.Binary16
import net.kurobako.gemstone.binary.Binary32
import net.kurobako.gemstone.binary.BitWidth
import net.kurobako.gemstone.binary.FloatingPointWidth
import net.kurobako.gemstone.binary.IEEE754
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.binary.ScalarByteWidth
import net.kurobako.gemstone.concurrent.withLock
import net.kurobako.gemstone.isa.{KernelMetadata, KernelName}
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.memory.MemoryValueByteWidth
import cats.syntax.all.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import scala.collection.mutable
import scala.annotation.tailrec
import scala.util.control.NonFatal
import java.nio.charset.StandardCharsets
import java.util.concurrent.locks.ReentrantLock

/** Typed SPIR-V kernel execution over process-neutral allocation identities.
  *
  * This interpreter deliberately operates on the generated instruction enum. It does not translate pointers into host
  * addresses: external memory is always accessed through [[Memory]], while Function and local storage use
  * invocation/work-group-owned byte buffers.
  */
private[v1_6] object VMInterpreter {
  import Operand.{id, unsigned}
  private type Program = VMLoadedProgram

  private def isPhi(instruction: Instruction): Boolean = instruction match {
    case Instruction.Phi(_, _, _) => true
    case _                        => false
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

  private val OpenCLUnaryFloatingOpcodes = Set(
    ExtendedInstruction.OpenClStd.acos,
    ExtendedInstruction.OpenClStd.acosh,
    ExtendedInstruction.OpenClStd.acospi,
    ExtendedInstruction.OpenClStd.asin,
    ExtendedInstruction.OpenClStd.asinh,
    ExtendedInstruction.OpenClStd.asinpi,
    ExtendedInstruction.OpenClStd.atan,
    ExtendedInstruction.OpenClStd.atanh,
    ExtendedInstruction.OpenClStd.atanpi,
    ExtendedInstruction.OpenClStd.cbrt,
    ExtendedInstruction.OpenClStd.ceil,
    ExtendedInstruction.OpenClStd.cos,
    ExtendedInstruction.OpenClStd.cosh,
    ExtendedInstruction.OpenClStd.cospi,
    ExtendedInstruction.OpenClStd.exp,
    ExtendedInstruction.OpenClStd.exp2,
    ExtendedInstruction.OpenClStd.exp10,
    ExtendedInstruction.OpenClStd.expm1,
    ExtendedInstruction.OpenClStd.fabs,
    ExtendedInstruction.OpenClStd.floor,
    ExtendedInstruction.OpenClStd.log,
    ExtendedInstruction.OpenClStd.log2,
    ExtendedInstruction.OpenClStd.log10,
    ExtendedInstruction.OpenClStd.log1p,
    ExtendedInstruction.OpenClStd.logb,
    ExtendedInstruction.OpenClStd.rint,
    ExtendedInstruction.OpenClStd.round,
    ExtendedInstruction.OpenClStd.rsqrt,
    ExtendedInstruction.OpenClStd.sin,
    ExtendedInstruction.OpenClStd.sinh,
    ExtendedInstruction.OpenClStd.sinpi,
    ExtendedInstruction.OpenClStd.sqrt,
    ExtendedInstruction.OpenClStd.tan,
    ExtendedInstruction.OpenClStd.tanh,
    ExtendedInstruction.OpenClStd.tanpi,
    ExtendedInstruction.OpenClStd.trunc,
    ExtendedInstruction.OpenClStd.native_cos,
    ExtendedInstruction.OpenClStd.native_exp,
    ExtendedInstruction.OpenClStd.native_exp2,
    ExtendedInstruction.OpenClStd.native_exp10,
    ExtendedInstruction.OpenClStd.native_log,
    ExtendedInstruction.OpenClStd.native_log2,
    ExtendedInstruction.OpenClStd.native_log10,
    ExtendedInstruction.OpenClStd.native_recip,
    ExtendedInstruction.OpenClStd.native_rsqrt,
    ExtendedInstruction.OpenClStd.native_sin,
    ExtendedInstruction.OpenClStd.native_sqrt,
    ExtendedInstruction.OpenClStd.native_tan
  )
  private val OpenCLBinaryFloatingOpcodes = Set(
    ExtendedInstruction.OpenClStd.atan2,
    ExtendedInstruction.OpenClStd.atan2pi,
    ExtendedInstruction.OpenClStd.fdim,
    ExtendedInstruction.OpenClStd.fmod,
    ExtendedInstruction.OpenClStd.hypot,
    ExtendedInstruction.OpenClStd.powr
  )

  def kernelNames(program: Program): Vec[KernelName] =
    Vec.from(program.entryPoints.keys.toSeq.sortBy(_.value))

  def globalImage(program: Program, name: String): Either[Fault, (Int, Array[Byte])] =
    program.functionNames.find { case (id, candidate) =>
      candidate.value == name && program.globals.contains(id)
    }.map(_._1).toRight(Fault.Memory(s"SPIR-V global '$name' is not present")).flatMap { id =>
      materializeGlobals(program, storageClass => storageClass != 4 && storageClass != 6, useBindings = false)
        .flatMap(_.get(id) match {
          case Some(RuntimeValue.Address(_, Pointer(Region.Internal(storage), _, _, _, _))) =>
            Right(id -> storage.bytes)
          case _ => Left(Fault.Memory(s"SPIR-V global '$name' has no storage"))
        })
    }

  def kernelMetadata(program: Program, name: KernelName): Either[Fault, KernelMetadata] =
    if (program.vulkanEntries.contains(name)) vulkanKernelMetadata(program, name)
    else {
      val compiled = program
      compiled.entryPoints.get(name).toRight(
        Fault.InvalidTask(s"SPIR-V entry point $name does not exist")
      ).flatMap { functionId =>
        compiled.functions.get(functionId).toRight(
          Fault.InvalidTask(s"SPIR-V entry point $name has no function body")
        ).flatMap { function =>
          function.parameters.foldLeft[Either[Fault, (Vec[KernelMetadata.Parameter], Int)]](
            Right((Vec.empty[KernelMetadata.Parameter], 0))
          ) { case (result, (parameterId, parameterType)) =>
            result.flatMap { case (parameters, currentOffset) =>
              parameterMetadata(compiled, parameterId, parameterType).flatMap { parameter =>
                val offset = alignUp(currentOffset, math.min(8, typeAlignment(compiled, parameterType)))
                offset.refineEither[Positive0]
                  .leftMap(_ =>
                    Fault.InvalidTask(s"negative SPIR-V parameter offset $offset")
                  )
                  .map(refinedOffset =>
                    parameters.appended(parameter.copy(offset =
                      refinedOffset
                    )) -> Math.addExact(offset, parameter.width)
                  )
              }
            }
          }.flatMap { case (parameters, size) =>
            for {
              refinedSize <- size.refineEither[Positive0]
                .leftMap(_ =>
                  Fault.InvalidTask(s"negative SPIR-V argument block size $size")
                )
              parameterCount <- parameters.size.refineEither[Positive0]
                .leftMap(_ => Fault.InvalidTask("negative SPIR-V parameter count"))
              metadata <- KernelMetadata.validated(refinedSize, parameterCount, parameters)
                .leftMap(detail => Fault.InvalidTask(detail))
            } yield metadata
          }
        }
      }
    }

  private[v1_6] def descriptorVariables(program: Program, name: KernelName): Vec[Int] = {
    val declared = program.ir.entryPointInterfaces.getOrElse(name, Set.empty)
    val scoped   = program.descriptorBindings.filter { case (variable, _) => declared.contains(variable) }
    // Before SPIR-V 1.4, descriptor globals were not required in OpEntryPoint's
    // interface. Retain compatibility with those modules when none are listed.
    val bindings = if (scoped.nonEmpty) scoped else program.descriptorBindings
    Vec.from(bindings.toSeq.sortBy { case (variable, (set, binding)) =>
      (set, binding, variable)
    }.map(_._1))
  }

  private def vulkanKernelMetadata(program: Program, name: KernelName): Either[Fault, KernelMetadata] =
    program.entryPoints.get(name).flatMap(program.functions.get) match {
      case None    => Left(Fault.InvalidTask(s"SPIR-V entry point $name has no function body"))
      case Some(_) =>
        val functionId  = program.entryPoints(name)
        val descriptors = descriptorVariables(program, name)
        val parameters  = descriptors.indices.map(index =>
          KernelMetadata.Parameter(
            8.refineUnsafe[Positive],
            (index * 8).refineUnsafe[Positive0],
            KernelMetadata.ParameterKind.Memory,
            containsPointers = false
          )
        )
        KernelMetadata.validated(
          (descriptors.size * 8).refineUnsafe[Positive0],
          descriptors.size.refineUnsafe[Positive0],
          Vec.from(parameters),
          requiredWorkGroupSize = workGroupSize(program, functionId),
          workGroupSizeHint = workGroupSizeHint(program, functionId),
          bindingIndices =
            Vec.from(descriptors.map(variable => program.descriptorBindings(variable)._2.refineUnsafe[Positive0]))
        ).leftMap(detail => Fault.InvalidTask(detail))
    }

  private def workGroupSize(program: Program, functionId: Int): Option[KernelMetadata.WorkGroupSize] =
    program.workGroupSizes.get(functionId).map(toWorkGroupSize(_, _, _)).orElse(builtinWorkGroupSize(program))

  private def builtinWorkGroupSize(program: Program): Option[KernelMetadata.WorkGroupSize] =
    program.builtins.collectFirst { case (variable, 25) => variable }.flatMap { variable =>
      program.constantData.get(variable).collect {
        case Constant.Bits(_, lanes) if lanes.size == 3 =>
          toWorkGroupSize(lanes(0).toInt, lanes(1).toInt, lanes(2).toInt)
      }
    }

  private def toWorkGroupSize(x: Int, y: Int, z: Int): KernelMetadata.WorkGroupSize =
    KernelMetadata.WorkGroupSize(x.refineUnsafe[Positive], y.refineUnsafe[Positive], z.refineUnsafe[Positive])

  private def workGroupSizeHint(program: Program, functionId: Int): Option[KernelMetadata.WorkGroupSize] =
    program.workGroupSizeHints.get(functionId).map { case (x, y, z) =>
      KernelMetadata.WorkGroupSize(x.refineUnsafe[Positive], y.refineUnsafe[Positive], z.refineUnsafe[Positive])
    }

  private def parameterMetadata(
      program: Program,
      parameterId: Int,
      parameterType: Int
  ): Either[Fault, KernelMetadata.Parameter] =
    program.runtimeType(parameterType) match {
      case Some(RuntimeType.Pointer(_, pointee)) if program.byValueParameters.contains(parameterId) =>
        pointerLeafOffsets(program, pointee, 0L).flatMap(pointers =>
          parameterMetadata(typeSize(program, pointee), KernelMetadata.ParameterKind.Scalar, pointers.nonEmpty)
        )
      case Some(RuntimeType.Pointer(storageClass, _)) if storageClass == 4 =>
        Right(KernelMetadata.Parameter(8, 0, KernelMetadata.ParameterKind.Local, containsPointers = false))
      case Some(RuntimeType.Pointer(_, _)) =>
        Right(KernelMetadata.Parameter(8, 0, KernelMetadata.ParameterKind.Memory, containsPointers = false))
      case Some(_) => pointerLeafOffsets(program, parameterType, 0L).flatMap(pointers =>
          parameterMetadata(typeSize(program, parameterType), KernelMetadata.ParameterKind.Scalar, pointers.nonEmpty)
        )
      case None => Left(
          Fault.InvalidTask(s"SPIR-V parameter type %$parameterType is undefined")
        )
    }

  private def parameterMetadata(
      width: Int,
      kind: KernelMetadata.ParameterKind,
      containsPointers: Boolean
  ): Either[Fault, KernelMetadata.Parameter] =
    width.refineEither[Positive]
      .leftMap(_ =>
        Fault.InvalidTask(s"SPIR-V parameter width must be positive: $width")
      )
      .map(refinedWidth => KernelMetadata.Parameter(refinedWidth, 0, kind, containsPointers))

  def execute(
      program: Program,
      dispatch: KernelTask,
      memory: Memory,
      control: TaskControl,
      scheduler: Scheduler,
      observer: Observer[Event]
  ): Either[Fault, Unit] =
    executeMeasured(program, dispatch, memory, control, scheduler, observer)

  private def executeMeasured(
      program: Program,
      dispatch: KernelTask,
      memory: Memory,
      control: TaskControl,
      scheduler: Scheduler,
      observer: Observer[Event]
  ): Either[Fault, Unit] = {
    val metrics = Performance.Metrics()
    val started = PlatformTime.monotonicNanos
    val result  = try executeValidated(program, dispatch, memory, control, scheduler, observer, metrics)
    catch {
      case NonFatal(error) =>
        Left(
          Fault.InvalidTask(
            s"malformed SPIR-V execution state: ${Option(error.getMessage).getOrElse(error.getClass.getSimpleName)}"
          )
        )
    }
    metrics.recordElapsed(math.max(0L, PlatformTime.monotonicNanos - started))
    result.flatMap(_ =>
      observer.observe(control.observation, Performance.Observation(metrics.snapshot))
        .leftMap(error => Fault.Observer(error.message))
    )
  }

  private def executeValidated(
      program: Program,
      dispatch: KernelTask,
      memory: Memory,
      control: TaskControl,
      scheduler: Scheduler,
      observer: Observer[Event],
      metrics: Performance.Metrics
  ): Either[Fault, Unit] =
    program.entryPoints.get(dispatch.kernelName).toRight(
      Fault.InvalidTask(
        s"SPIR-V entry point ${dispatch.kernelName.value} does not exist"
      )
    ).flatMap { functionId =>
      program.functions.get(functionId).toRight(
        Fault.InvalidTask(
          s"SPIR-V entry point ${dispatch.kernelName.value} has no function body"
        )
      ).flatMap { function =>
        Either.cond(
          (if (program.vulkanEntries.contains(dispatch.kernelName))
             descriptorVariables(program, dispatch.kernelName).size
           else function.parameters.size) == dispatch.arguments.size,
          (),
          Fault.InvalidTask(
            s"SPIR-V entry point ${dispatch.kernelName.value} expects ${
                if (program.vulkanEntries.contains(dispatch.kernelName)) descriptorVariables(program, dispatch.kernelName).size
                else function.parameters.size
              } arguments, " +
              s"received ${dispatch.arguments.size}"
          )
        ).flatMap(_ => executeDispatch(program, function, dispatch, memory, control, scheduler, observer, metrics))
      }
    }

  private def scalarLeafCount(program: Program, typeId: Int): Either[Fault, Int] =
    program.scalarLeafCountCache.getOrElseUpdate(typeId, VMIR.scalarLeafCount(program.types, typeId))

  // Work-item scheduling reuses nullable invocation slots and mutable loop state to keep the dispatch hot path lean.
  @SuppressWarnings(Array(
    "scalafix:DisableSyntax.null",
    "scalafix:DisableSyntax.var",
    "scalafix:DisableSyntax.return"
  ))
  private def executeDispatch(
      program: Program,
      function: Function,
      dispatch: KernelTask,
      memory: Memory,
      control: TaskControl,
      scheduler: Scheduler,
      observer: Observer[Event],
      metrics: Performance.Metrics
  ): Either[Fault, Unit] = {
    val initialGlobals = materializeGlobals(program, storageClass => storageClass != 4 && storageClass != 6) match {
      case Left(error)  => return Left(error)
      case Right(value) => value
    }
    val globalValues = if (!program.vulkanEntries.contains(dispatch.kernelName)) initialGlobals
    else {
      descriptorVariables(
        program,
        dispatch.kernelName
      ).zip(dispatch.arguments).foldLeft[Either[Fault, Map[Int, RuntimeValue]]](Right(initialGlobals)) {
        case (result, (variable, KernelTask.Argument.Memory(allocation, offset))) => result.flatMap { values =>
            program.globals.get(variable).toRight(Fault.InvalidTask(s"SPIR-V descriptor %$variable has no variable"))
              .map { global =>
                val pointee = pointerPointee(program, global.pointerType)
                values.updated(
                  variable,
                  RuntimeValue.Address(
                    global.pointerType,
                    Pointer(Region.External(allocation), offset, pointee, global.storageClass)
                  )
                )
              }
          }
        case (_, (variable, _)) => Left(Fault.InvalidTask(s"SPIR-V descriptor %$variable requires a memory argument"))
      } match {
        case Left(error)  => return Left(error)
        case Right(value) => value
      }
    }
    val groupSize  = dispatch.geometry.groupSize
    val totalLocal = groupSize.volumeInt match {
      case Left(message) => return Left(Fault.InvalidTask(message))
      case Right(value)  => value
    }
    val subgroup          = program.subgroupSizes.getOrElse(function.id, math.max(1, math.min(32, totalLocal)))
    val enqueuedSubgroups = (totalLocal.toLong + subgroup.toLong - 1L) / subgroup.toLong
    val hasCollectives    = program.collectiveFunctions.contains(function.id) || memory.defersHostReads
    val baseScalars       = BaseScalarValues(program.constants, globalValues, program.module.header.bound.toUnsigned)
    metrics.recordDispatch(hasCollectives)
    val groupCount = dispatch.geometry.groupCount.volume match {
      case Left(message) => return Left(Fault.InvalidTask(message))
      case Right(value)  => value
    }
    // The scheduler parallelizes launches large enough to amortise queue traffic and claims a range of groups per
    // dequeue, so queue overhead stays bounded while medium grids leave the calling thread.
    // Positive launch volumes imply the scheduler's non-negative task-count requirement.
    val taskCount: Long :| Positive0 = groupCount.refineUnsafe[Positive0]
    val workItems: Long :| Positive0 = totalLocal.toLong.refineUnsafe[Positive0]
    val parallelWork                 = scheduler.shouldParallel(taskCount, workItems)
    val state                        = MemoryState(memory, dispatch.pointerRelocations, observer, control.observation)
    val flushLock                    = ReentrantLock()
    val reusableValues               =
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
    // Collective invocations must keep separate register files while a workgroup is suspended,
    // but a completed workgroup no longer needs them. Reuse each local lane's file in the
    // next workgroup when dispatch is sequential.
    val collectiveValues =
      if (hasCollectives && !parallelWork && groupCount > 1L) Array.fill[Option[InvocationValues]](totalLocal)(None)
      else Array.empty[Option[InvocationValues]]

    def runGroup(
        groupIndex: Long :| Positive0,
        reuseInvocationState: Boolean,
        groupMetrics: Performance.Metrics,
        reuseValues: Option[InvocationValues],
        reuseExecution: Option[Array[InvocationExecution | Null]],
        groupState: MemoryState
    ): Either[Fault, Unit] = {
      val group = dispatch.geometry.groupCoordinates(groupIndex) match {
        case Left(message) => return Left(Fault.InvalidTask(message))
        case Right(value)  => value
      }
      val activeGroupSize = dispatch.geometry.activeGroupSize(group) match {
        case Left(message) => return Left(Fault.InvalidTask(message))
        case Right(value)  => value
      }
      val activeLocalCount = activeGroupSize.volumeInt match {
        case Left(message) => return Left(Fault.InvalidTask(message))
        case Right(value)  => value
      }

      val workgroupValues = materializeGlobals(program, _ == 4) match {
        case Left(error)  => return Left(error)
        case Right(value) => value
      }
      val shared =
        if (dispatch.arguments.isEmpty) Map.empty[Int, Region]
        else
          dispatch.arguments.zipWithIndex.collect { case (KernelTask.Argument.Local(size), argumentIndex) =>
            argumentIndex -> Region.Internal(InternalMemory(Array.ofDim[Byte](checkedSize(size))))
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
      val executions  = if (hasCollectives) mutable.ArrayBuffer.empty[InvocationExecution] else null
      var linearLocal = 0
      while (linearLocal < activeLocalCount) {
        // The loop lower bound proves the linear local index is non-negative.
        val local = activeGroupSize.coordinates(linearLocal.toLong) match {
          case Left(message) => return Left(Fault.InvalidTask(message))
          case Right(value)  => value
        }
        val global = dispatch.geometry.globalCoordinatesLong(group, local) match {
          case Left(message) => return Left(Fault.InvalidTask(message))
          case Right(value)  => value
        }
        if (dispatch.geometry.containsGlobal(global)) {
          groupMetrics.recordInvocation()
          val privateValues = materializeGlobals(program, _ == 6) match {
            case Left(error)  => return Left(error)
            case Right(value) => value
          }
          val subgroupIndex = linearLocal / subgroup
          val subgroupSize  = math.min(subgroup, activeLocalCount - subgroupIndex * subgroup)
          val numSubgroups  = (activeLocalCount.toLong + subgroup.toLong - 1L) / subgroup.toLong
          val invocation    = reusableExecution match {
            case null => Invocation(
                groupX = group.x.toLong,
                groupY = group.y.toLong,
                groupZ = group.z.toLong,
                localX = local.x.toLong,
                localY = local.y.toLong,
                localZ = local.z.toLong,
                globalX = global.x,
                globalY = global.y,
                globalZ = global.z,
                globalSizeX = dispatch.geometry.globalExtent.x.toLong,
                globalSizeY = dispatch.geometry.globalExtent.y.toLong,
                globalSizeZ = dispatch.geometry.globalExtent.z.toLong,
                globalOffsetX = dispatch.geometry.globalOffset.x.toLong,
                globalOffsetY = dispatch.geometry.globalOffset.y.toLong,
                globalOffsetZ = dispatch.geometry.globalOffset.z.toLong,
                workDim = dispatch.geometry.workDim,
                numWorkgroupsX = dispatch.geometry.groupCount.x.toLong,
                numWorkgroupsY = dispatch.geometry.groupCount.y.toLong,
                numWorkgroupsZ = dispatch.geometry.groupCount.z.toLong,
                workgroupSizeX = activeGroupSize.x.toLong,
                workgroupSizeY = activeGroupSize.y.toLong,
                workgroupSizeZ = activeGroupSize.z.toLong,
                enqueuedWorkgroupSizeX = groupSize.x.toLong,
                enqueuedWorkgroupSizeY = groupSize.y.toLong,
                enqueuedWorkgroupSizeZ = groupSize.z.toLong,
                subgroupSize = subgroupSize,
                maxSubgroupSize = subgroup,
                numSubgroups = numSubgroups,
                enqueuedSubgroups = enqueuedSubgroups,
                subgroupIndex = subgroupIndex,
                subgroupLocal = linearLocal % subgroup
              )
            case execution =>
              execution.invocation.reset(
                group,
                local,
                global,
                dispatch.geometry,
                activeGroupSize,
                groupSize,
                subgroupSize,
                subgroup,
                numSubgroups,
                enqueuedSubgroups,
                subgroupIndex,
                linearLocal % subgroup
              )
              execution.invocation
          }
          prepareInvocation(
            program,
            function,
            dispatch.arguments,
            shared,
            invocation,
            globalValues ++ workgroupValues ++ privateValues,
            if (collectiveValues.nonEmpty) collectiveValues(linearLocal) else reusableValues,
            reusableExecution,
            groupState,
            baseScalars,
            groupMetrics,
            collectiveValues.nonEmpty
          ) match {
            case Left(error)  => return Left(error)
            case Right(value) =>
              if (collectiveValues.nonEmpty) collectiveValues(linearLocal) = Some(value.values)
              if (hasCollectives) {
                val _ = executions.nn += value
              } else {
                if (reuseInvocationState) {
                  reusableExecution = value
                  reuseExecution match {
                    case Some(slot) => slot(0) = value
                    case None       => ()
                  }
                }
                resumeInvocation(program, value, groupState, control, groupMetrics) match {
                  case Left(error)                  => return Left(error)
                  case Right(()) if value.completed => ()
                  case Right(())                    =>
                    return Left(
                      Fault.InvalidTask(
                        "SPIR-V program suspended at a collective absent from its static instruction set"
                      )
                    )
                }
              }
          }
        }
        linearLocal += 1
      }
      if (hasCollectives)
        runWorkgroup(program, executions.nn, groupState, control, groupMetrics)
      else Right(())
    }

    val runResult = scheduler.forEach(taskCount, parallel = parallelWork) { groupIndex =>
      if (parallelWork) {
        val groupMetrics = Performance.Metrics()
        val groupState   = MemoryState(memory, dispatch.pointerRelocations, observer, control.observation)
        runGroup(groupIndex, reuseInvocationState = false, groupMetrics, None, None, groupState) match {
          case Left(error) => Left(error)
          case Right(_)    => flushLock.withLock(groupState.flush()) match {
              case Left(error) => Left(error)
              case Right(_)    =>
                metrics.merge(groupMetrics)
                Right(())
            }
        }
      } else {
        runGroup(
          groupIndex,
          reuseInvocationState = true,
          metrics,
          reusableValues,
          Some(reusableExecution),
          state
        )
      }
    }
    runResult match {
      case Left(error) => return Left(error)
      case Right(())   => ()
    }
    if (parallelWork) Right(()) else state.flush()
  }

  // Invocation setup fills the machine frame sequentially and fails fast on invalid parameters.
  @SuppressWarnings(
    Array(
      "scalafix:DisableSyntax.var",
      "scalafix:DisableSyntax.return",
      "scalafix:DisableSyntax.null"
    )
  )
  private def prepareInvocation(
      program: Program,
      function: Function,
      arguments: Vec[KernelTask.Argument],
      shared: Map[Int, Region],
      invocation: Invocation,
      globalValues: Map[Int, RuntimeValue],
      reusableValues: Option[InvocationValues],
      reusableExecution: InvocationExecution | Null,
      memory: MemoryState,
      baseScalars: BaseScalarValues,
      metrics: Performance.Metrics,
      trackTouched: Boolean
  ): Either[Fault, InvocationExecution] = {
    val values = reusableValues match {
      case Some(value) =>
        value.clear()
        globalValues.foreach { case (id, global) => value.update(id, global) }
        value
      case None =>
        InvocationValues(
          program.constants,
          globalValues,
          program.module.header.bound.toUnsigned,
          baseScalars,
          metrics,
          trackTouched
        )
    }
    val globals     = program.globals.view.toVector
    var globalIndex = 0
    while (globalIndex < globals.size) {
      val (variable, global) = globals(globalIndex)
      program.builtins.get(variable) match {
        case Some(builtin) =>
          val pointee = pointerPointee(program, global.pointerType)
          val value   = builtinValue(program, pointee, builtin, invocation) match {
            case Left(error)   => return Left(error)
            case Right(result) => result
          }
          val bytes = encode(program, value) match {
            case Left(error) => return Left(error)
            case Right(data) => data
          }
          values.update(
            variable,
            RuntimeValue.Address(
              global.pointerType,
              Pointer(Region.Internal(InternalMemory(bytes)), 0L, pointee, global.storageClass)
            )
          )
        case None => ()
      }
      globalIndex += 1
    }
    var parameterIndex = 0
    while (parameterIndex < function.parameters.size) {
      val (parameterId, parameterType) = function.parameters(parameterIndex)
      bindArgument(
        program,
        parameterType,
        program.byValueParameters.contains(parameterId),
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

  private val InstructionCompleted: Either[Fault, Unit] = Right(())

  // Keep the loop and semantic families below HotSpot's huge-method threshold.
  // Invocation state is explicit so calls, returns and collective suspension share one path.
  // The instruction loop exits as soon as the invocation completes, suspends, or faults.
  @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.return"))
  private def resumeInvocation(
      program: Program,
      execution: InvocationExecution,
      memory: MemoryState,
      control: TaskControl,
      metrics: Performance.Metrics
  ): Either[Fault, Unit] = {
    while (execution.pc < execution.currentFunction.end) {
      execution.instructionCount += 1L
      metrics.recordInstruction()
      control.checkpoint() match {
        case Left(fault) => return Left(fault)
        case Right(_)    => ()
      }
      val scalarInteger = program.scalarIntegerInstruction(execution.pc)
      if (scalarInteger == null || !executeScalarInteger(execution, scalarInteger.nn))
        executeInstruction(program, execution, memory, program.module.instructions(execution.pc)) match {
          case Left(Fault.PendingMemory) =>
            execution.memoryPending = true
            return InstructionCompleted
          case Left(error) => return Left(error)
          case Right(_)    => ()
        }
      if (execution.completed || execution.suspended.nonEmpty) return InstructionCompleted
    }
    Left(Fault.InvalidTask(
      s"SPIR-V function %${execution.currentFunction.id} " +
        s"(${program.functionNames.get(execution.currentFunction.id).fold("unnamed")(_.value)}) " +
        "reached OpFunctionEnd without OpReturn"
    ))
  }

  private def executeScalarInteger(
      execution: InvocationExecution,
      instruction: VMScalarIntegerInstruction
  ): Boolean = {
    val values = execution.values
    val left = if (instruction.leftConstant) Some(instruction.leftConstantBits) else values.readScalar(instruction.left)
    val right =
      if (instruction.rightConstant) Some(instruction.rightConstantBits) else values.readScalar(instruction.right)
    (left, right) match {
      case (Some(leftValue), Some(rightValue)) =>
        val result = instruction.operation match {
          case VMScalarIntegerInstruction.Add      => leftValue + rightValue
          case VMScalarIntegerInstruction.Subtract => leftValue - rightValue
          case VMScalarIntegerInstruction.Multiply => leftValue * rightValue
        }
        values.updateScalar(
          instruction.destination,
          instruction.resultType,
          result & instruction.resultMask
        )
        execution.pc += 1
        true
      case _ => false
    }
  }

  private def executeInstruction(
      program: Program,
      execution: InvocationExecution,
      memory: MemoryState,
      instruction: Instruction
  ): Either[Fault, Unit] = instruction match {
    case _: Instruction.Line | _: Instruction.NoLine =>
      execution.pc += 1
      Right(())
    case _: Instruction.FunctionParameter | _: Instruction.Label | _: Instruction.Phi =>
      executePhi(program, execution, instruction)
    case _: Instruction.Variable | _: Instruction.Load | _: Instruction.Store | _: Instruction.CopyMemory | _: Instruction.CopyMemorySized | _: Instruction.GroupAsyncCopy | _: Instruction.GroupWaitEvents =>
      executeMemory(program, execution, memory, instruction)
    case _: Instruction.Bitcast | _: Instruction.BitCount | _: Instruction.PtrCastToGeneric | _: Instruction.GenericCastToPtr | _: Instruction.GenericCastToPtrExplicit | _: Instruction.PtrAccessChain | _: Instruction.InBoundsPtrAccessChain | _: Instruction.AccessChain | _: Instruction.InBoundsAccessChain | _: Instruction.CompositeExtract | _: Instruction.CompositeConstruct | _: Instruction.CompositeInsert | _: Instruction.VectorExtractDynamic | _: Instruction.VectorInsertDynamic =>
      executeComposite(program, execution, memory, instruction)
    case _: Instruction.VectorShuffle | _: Instruction.ExtInst =>
      executeExtended(program, execution, memory, instruction)
    case _: Instruction.UConvert | _: Instruction.SConvert | _: Instruction.ConvertPtrToU | _: Instruction.ConvertUToPtr | _: Instruction.ConvertSToF | _: Instruction.ConvertUToF | _: Instruction.ConvertFToS | _: Instruction.ConvertFToU | _: Instruction.FConvert | _: Instruction.Undef | _: Instruction.CopyObject | _: Instruction.LifetimeStart | _: Instruction.LifetimeStop =>
      executeConversion(program, execution, memory, instruction)
    case _: Instruction.IAdd | _: Instruction.ISub | _: Instruction.IMul | _: Instruction.UDiv | _: Instruction.UMod | _: Instruction.SDiv | _: Instruction.SRem | _: Instruction.SNegate =>
      executeInteger(program, execution, instruction)
    case _: Instruction.AtomicStore | _: Instruction.AtomicLoad | _: Instruction.AtomicExchange | _: Instruction.AtomicCompareExchange | _: Instruction.AtomicCompareExchangeWeak | _: Instruction.AtomicFlagTestAndSet | _: Instruction.AtomicFlagClear | _: Instruction.AtomicFAddEXT | _: Instruction.AtomicFMinEXT | _: Instruction.AtomicFMaxEXT | _: Instruction.AtomicIAdd | _: Instruction.AtomicUMin | _: Instruction.AtomicIIncrement | _: Instruction.AtomicIDecrement | _: Instruction.AtomicISub | _: Instruction.AtomicSMin | _: Instruction.AtomicSMax | _: Instruction.AtomicUMax | _: Instruction.AtomicAnd | _: Instruction.AtomicOr | _: Instruction.AtomicXor | _: Instruction.FAdd | _: Instruction.FSub | _: Instruction.FMul | _: Instruction.Dot | _: Instruction.FDiv | _: Instruction.VectorTimesScalar =>
      executeAtomic(program, execution, memory, instruction)
    case _: Instruction.FNegate | _: Instruction.ShiftLeftLogical =>
      executeFloatingAndShift(program, execution, instruction)
    case _: Instruction.ShiftRightLogical | _: Instruction.ShiftRightArithmetic | _: Instruction.BitwiseOr | _: Instruction.BitwiseXor | _: Instruction.BitwiseAnd | _: Instruction.Not | _: Instruction.IEqual | _: Instruction.INotEqual | _: Instruction.LogicalEqual | _: Instruction.LogicalNotEqual | _: Instruction.LogicalOr | _: Instruction.LogicalAnd | _: Instruction.LogicalNot | _: Instruction.Any | _: Instruction.All | _: Instruction.FOrdEqual | _: Instruction.FOrdNotEqual | _: Instruction.FUnordEqual | _: Instruction.FUnordNotEqual | _: Instruction.FUnordLessThan | _: Instruction.FUnordGreaterThan | _: Instruction.FUnordLessThanEqual | _: Instruction.FUnordGreaterThanEqual | _: Instruction.Unordered | _: Instruction.Ordered | _: Instruction.FOrdLessThanEqual | _: Instruction.FOrdLessThan | _: Instruction.FOrdGreaterThan | _: Instruction.FOrdGreaterThanEqual | _: Instruction.IsInf | _: Instruction.IsNan | _: Instruction.IsFinite =>
      executeArithmetic(program, execution, instruction)
    case _: Instruction.IsNormal | _: Instruction.SignBitSet | _: Instruction.ULessThan | _: Instruction.UGreaterThan | _: Instruction.ULessThanEqual | _: Instruction.UGreaterThanEqual | _: Instruction.SGreaterThan | _: Instruction.SGreaterThanEqual | _: Instruction.SLessThan | _: Instruction.SLessThanEqual | _: Instruction.Select =>
      executeComparison(program, execution, instruction)
    case _: Instruction.ControlBarrier | _: Instruction.MemoryBarrier | _: Instruction.GroupAny | _: Instruction.GroupAll | _: Instruction.GroupUMin | _: Instruction.GroupUMax | _: Instruction.GroupSMin | _: Instruction.GroupSMax | _: Instruction.GroupIAdd | _: Instruction.GroupIMulKHR | _: Instruction.GroupFAdd | _: Instruction.GroupFMin | _: Instruction.GroupFMax | _: Instruction.GroupFMulKHR | _: Instruction.SubgroupShuffleINTEL | _: Instruction.SubgroupShuffleUpINTEL | _: Instruction.SubgroupShuffleDownINTEL | _: Instruction.GroupBroadcast | _: Instruction.GroupNonUniformBallot =>
      executeCollective(program, execution, memory, instruction)
    case _: Instruction.FunctionCall | _: Instruction.FunctionPointerCallINTEL =>
      executeCall(program, execution, memory, instruction)
    case _: Instruction.Branch | _: Instruction.BranchConditional | _: Instruction.Switch | _: Instruction.ReturnValue | _: Instruction.Return | _: Instruction.AssumeTrueKHR | _: Instruction.LoopMerge | _: Instruction.SelectionMerge =>
      executeBranch(program, execution, instruction)
    case other =>
      Left(Fault.Unsupported(s"${other.opcode} execution is not implemented"))
  }

  // Phi writes are staged while predecessors are scanned and fail before partial commit.
  @SuppressWarnings(
    Array(
      "scalafix:DisableSyntax.var",
      "scalafix:DisableSyntax.return"
    )
  )
  private def executePhi(
      program: Program,
      execution: InvocationExecution,
      instruction: Instruction
  ): Either[Fault, Unit] = {
    val values = execution.values
    instruction match {
      case Instruction.FunctionParameter(_, _) => execution.pc += 1
      case Instruction.Label(label)            =>
        execution.currentLabel = id(label)
        execution.pc += 1
      case Instruction.Phi(_, _, _) =>
        val updates = mutable.ArrayBuffer.empty[(Int, RuntimeValue)]
        var phiPc   = execution.pc
        while (phiPc < execution.currentFunction.end && isPhi(program.module.instructions(phiPc))) {
          program.module.instructions(phiPc) match {
            case Instruction.Phi(resultType, dest, incoming) =>
              phiIncoming(incoming, execution.predecessor) match {
                case Left(error)          => return Left(error)
                case Right(Some(valueId)) =>
                  values.get(valueId.toLong) match {
                    case Some(value) => updates += id(dest) -> retag(value, id(resultType))
                    case None        =>
                      return Left(
                        Fault.InvalidTask(
                          s"OpPhi %${id(dest)} has no incoming value for label %${execution.predecessor}"
                        )
                      )
                  }
                case Right(None) =>
                  return Left(
                    Fault.InvalidTask(
                      s"OpPhi %${id(dest)} has no incoming value for label %${execution.predecessor}"
                    )
                  )
              }
            case _ => ()
          }
          phiPc += 1
        }
        updates.foreach { case (destination, value) => values.update(destination, value) }
        execution.pc = phiPc
      case other =>
        return Left(Fault.Unsupported(s"${other.opcode} execution is not implemented"))
    }
    InstructionCompleted
  }

  // Memory execution fails fast and uses invocation-owned pointer slots for aggregate relocation.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return"))
  private def executeMemory(
      program: Program,
      execution: InvocationExecution,
      memory: MemoryState,
      instruction: Instruction
  ): Either[Fault, Unit] = {
    val values = execution.values
    instruction match {
      case Instruction.Variable(resultType, dest, storageClass, initializer) =>
        val pointee = pointerPointee(program, id(resultType))
        val buffer  = Array.ofDim[Byte](typeSize(program, pointee))
        val slots   = mutable.LongMap.empty[(Int, Pointer)]
        pointerLeafOffsets(program, pointee, 0L).toOption.foreach(_.foreach { case (offset, pointerType) =>
          program.runtimeType(pointerType) match {
            case Some(RuntimeType.Pointer(pointerStorage, pointerPointee)) =>
              slots.update(offset, pointerType -> Pointer(Region.Null, 0L, pointerPointee, pointerStorage))
            case _ => ()
          }
        })
        initializer.flatMap(value => values.get(id(value))) match {
          case Some(value) => encode(program, value) match {
              case Left(error)    => return Left(error)
              case Right(encoded) =>
                Array.copy(encoded, 0, buffer, 0, math.min(buffer.length, encoded.length))
            }
          case None => ()
        }
        values.update(
          id(dest),
          RuntimeValue.Address(
            id(resultType),
            Pointer(
              Region.Internal(InternalMemory(buffer)),
              0L,
              pointee,
              id(storageClass),
              Option.when(slots.nonEmpty)(slots)
            )
          )
        )
        execution.pc += 1
      case Instruction.Load(resultType, dest, pointer, _) =>
        address(values, pointer) match {
          case Left(error)   => return Left(error)
          case Right(source) =>
            program.runtimeType(id(resultType)) match {
              case Some(RuntimeType.Pointer(_, _)) =>
                loadPointer(program, memory, source, id(resultType)) match {
                  case Left(error) =>
                    return Left(withExecutionContext(error, execution.currentFunction, execution.pc, instruction))
                  case Right(value) =>
                    values.update(id(dest), value)
                    execution.pc += 1
                }
              case Some(RuntimeType.Bool) | Some(RuntimeType.Integer(_, _)) | Some(RuntimeType.Floating(_)) =>
                scalarMemoryWidth(program, id(resultType)) match {
                  case Left(error) => return Left(error)
                  case Right(size) => memory.readScalar(source, size) match {
                      case Left(error)   => return Left(error)
                      case Right(loaded) =>
                        values.updateScalar(id(dest), id(resultType), loaded)
                        execution.pc += 1
                    }
                }
              case _ => nonNegativeTypeSize(program, id(resultType)) match {
                  case Left(error) => return Left(error)
                  case Right(size) => memory.read(source, size) match {
                      case Left(error)  => return Left(error)
                      case Right(bytes) =>
                        decode(program, id(resultType), bytes) match {
                          case Left(error)  => return Left(error)
                          case Right(value) => values.update(id(dest), value)
                        }
                        execution.pc += 1
                    }
                }
            }
        }
      case Instruction.Store(pointer, source, _) =>
        address(values, pointer) match {
          case Right(target) if values.scalarType(id(source)) >= 0 =>
            val sourceType = values.scalarType(id(source))
            scalarMemoryWidth(program, sourceType) match {
              case Left(error) => return Left(error)
              case Right(size) =>
                memory.writeScalar(target, values.scalarBits(id(source)), size) match {
                  case Left(error) =>
                    return Left(withExecutionContext(error, execution.currentFunction, execution.pc, instruction))
                  case Right(()) =>
                    if (size.value == 8)
                      target.slots.foreach { slots =>
                        slots.get(target.offset).foreach { case (pointerType, template) =>
                          memory.integerPointer(
                            values.scalarBits(id(source)),
                            template.pointee,
                            template.storageClass
                          ).toOption.foreach { pointer =>
                            slots.update(target.offset, pointerType -> pointer)
                          }
                        }
                      }
                    execution.pc += 1
                }
            }
          case Right(target) => values.get(id(source)) match {
              case Some(RuntimeValue.Address(sourceType, value)) =>
                storePointer(program, memory, target, sourceType, value) match {
                  case Left(error) => return Left(error)
                  case Right(())   => execution.pc += 1
                }
              case Some(value) => encode(program, value) match {
                  case Left(error)    => return Left(error)
                  case Right(encoded) => memory.write(target, encoded) match {
                      case Left(error) =>
                        return Left(withExecutionContext(error, execution.currentFunction, execution.pc, instruction))
                      case Right(()) => execution.pc += 1
                    }
                }
              case None => return Left(undefined(source))
            }
          case Left(error) => return Left(error)
        }
      case Instruction.CopyMemory(target, source, _, _) =>
        (address(values, target), address(values, source)) match {
          case (Right(destination), Right(origin)) =>
            nonNegativeTypeSize(program, origin.pointee) match {
              case Left(error) => return Left(error)
              case Right(size) => memory.read(origin, size) match {
                  case Left(error)  => return Left(error)
                  case Right(bytes) =>
                    memory.write(destination, bytes).flatMap(_ =>
                      memory.copySlots(destination, origin, size.toLong)
                    ) match {
                      case Left(error) => return Left(error)
                      case Right(())   => execution.pc += 1
                    }
                }
            }
          case (Left(error), _) => return Left(error)
          case (_, Left(error)) => return Left(error)
        }
      case Instruction.CopyMemorySized(target, source, size, _, _) =>
        (address(values, target), address(values, source), scalar(values, size)) match {
          case (Right(destination), Right(origin), Right(rawSize))
              if rawSize >= 0L && rawSize <= Int.MaxValue.toLong =>
            val byteCount = rawSize.toInt.refineUnsafe[Positive0]
            memory.read(origin, byteCount) match {
              case Left(error)  => return Left(error)
              case Right(bytes) =>
                memory.write(destination, bytes).flatMap(_ =>
                  memory.copySlots(destination, origin, byteCount.toLong)
                ) match {
                  case Left(error) => return Left(error)
                  case Right(())   => execution.pc += 1
                }
            }
          case (Right(_), Right(_), Right(rawSize)) =>
            return Left(Fault.InvalidTask(
              s"OpCopyMemorySized byte count $rawSize is outside the supported range"
            ))
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
          case Right(())   => execution.pc += 1
        }
      case Instruction.GroupWaitEvents(executionScope, numEvents, eventsList) =>
        (for {
          scope <- scalar(values, executionScope)
          count <- scalar(values, numEvents)
          _     <- address(values, eventsList)
          _     <- Either.cond(
            scope == 2L,
            (),
            Fault.Unsupported(
              s"OpGroupWaitEvents execution scope $scope is not Workgroup"
            )
          )
          _ <- Either.cond(
            count >= 0L,
            (),
            Fault.InvalidTask(s"OpGroupWaitEvents has invalid event count $count")
          )
        } yield ()) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case other =>
        return Left(Fault.Unsupported(s"${other.opcode} execution is not implemented"))
    }
    InstructionCompleted
  }

  // Composite operations scan variable operands and retain the first validation failure.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return", "scalafix:DisableSyntax.var"))
  private def executeComposite(
      program: Program,
      execution: InvocationExecution,
      memory: MemoryState,
      instruction: Instruction
  ): Either[Fault, Unit] = {
    val values = execution.values
    instruction match {
      case Instruction.Bitcast(resultType, dest, source) =>
        values.get(id(source)) match {
          case Some(RuntimeValue.Address(_, pointer)) =>
            val pointee = pointerPointee(program, id(resultType))
            val result  = if (pointer.pointee == pointee) pointer else pointer.copy(pointee = pointee)
            values.update(id(dest), RuntimeValue.Address(id(resultType), result))
            execution.pc += 1
          case Some(value) =>
            value match {
              case RuntimeValue.Bits(sourceType, lanes) =>
                bitcast(program, sourceType, id(resultType), lanes) match {
                  case Left(error)   => return Left(error)
                  case Right(result) =>
                    values.update(id(dest), RuntimeValue.Bits(id(resultType), result))
                    execution.pc += 1
                }
              case RuntimeValue.Address(_, pointer) =>
                val pointee = pointerPointee(program, id(resultType))
                val result  = if (pointer.pointee == pointee) pointer else pointer.copy(pointee = pointee)
                values.update(id(dest), RuntimeValue.Address(id(resultType), result))
                execution.pc += 1
            }
          case None => return Left(undefined(source))
        }
      case Instruction.BitCount(resultType, dest, base) =>
        integerBitCount(program, values, resultType, dest, base) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.PtrCastToGeneric(resultType, dest, source) =>
        (program.runtimeType(id(resultType)), values.get(id(source))) match {
          case (Some(RuntimeType.Pointer(8, pointee)), Some(RuntimeValue.Address(sourceType, pointer))) =>
            program.runtimeType(sourceType) match {
              case Some(RuntimeType.Pointer(storageClass, sourcePointee))
                  if Vec(4, 5, 7).contains(storageClass) && sourcePointee == pointee &&
                    pointer.pointee == pointee && (pointer.region == Region.Null || pointer.storageClass == storageClass) =>
                values.update(id(dest), RuntimeValue.Address(id(resultType), pointer))
                execution.pc += 1
              case Some(RuntimeType.Pointer(storageClass, sourcePointee)) =>
                return Left(
                  Fault.InvalidTask(
                    s"OpPtrCastToGeneric cannot cast storage class $storageClass pointee %$sourcePointee to %$pointee"
                  )
                )
              case _ =>
                return Left(Fault.InvalidTask("OpPtrCastToGeneric source type is not a pointer"))
            }
          case (Some(RuntimeType.Pointer(storageClass, _)), _) =>
            return Left(
              Fault.InvalidTask(
                s"OpPtrCastToGeneric result pointer has storage class $storageClass instead of Generic"
              )
            )
          case (None, _) =>
            return Left(
              Fault.InvalidTask(
                s"OpPtrCastToGeneric result type %${id(resultType)} is undefined"
              )
            )
          case (_, None) => return Left(undefined(source))
          case _         => return Left(Fault.InvalidTask("OpPtrCastToGeneric source is not a pointer"))
        }
      case Instruction.GenericCastToPtr(resultType, dest, source) =>
        genericCastToPointer(program, values, resultType, dest, source) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.GenericCastToPtrExplicit(resultType, dest, source, storage) =>
        genericCastToPointerExplicit(program, values, resultType, dest, source, storage) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.PtrAccessChain(resultType, dest, base, element, indexes) =>
        accessChain(program, values, memory, resultType, dest, base, Some(element), indexes) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.InBoundsPtrAccessChain(resultType, dest, base, element, indexes) =>
        accessChain(program, values, memory, resultType, dest, base, Some(element), indexes) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.AccessChain(resultType, dest, base, indexes) =>
        accessChain(program, values, memory, resultType, dest, base, None, indexes) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.InBoundsAccessChain(resultType, dest, base, indexes) =>
        accessChain(program, values, memory, resultType, dest, base, None, indexes) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.CompositeExtract(resultType, dest, composite, indexes) =>
        compositeExtract(program, values, resultType, dest, composite, indexes) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.CompositeConstruct(resultType, dest, constituents) =>
        val lanes = Array.newBuilder[Long]
        var index = 0
        while (index < constituents.size) {
          values.get(id(constituents(index))) match {
            case Some(RuntimeValue.Bits(_, value)) => lanes ++= value
            case Some(_)                           =>
              return Left(
                Fault.InvalidTask(
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
            execution.pc += 1
          case Right(expected) =>
            return Left(
              Fault.InvalidTask(
                s"OpCompositeConstruct %${id(dest)} has ${result.length} scalar leaves, expected $expected"
              )
            )
          case Left(error) => return Left(error)
        }
      case Instruction.CompositeInsert(resultType, dest, source, composite, indexes) =>
        compositeInsert(program, values, resultType, dest, source, composite, indexes) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.VectorExtractDynamic(resultType, dest, vector, index) =>
        (values.get(id(vector)), values.get(id(index))) match {
          case (Some(RuntimeValue.Bits(_, lanes)), Some(RuntimeValue.Bits(_, Array(component)))) =>
            val selected = component.toInt
            if (selected < 0 || selected >= lanes.length)
              return Left(Fault.InvalidTask(
                s"OpVectorExtractDynamic index $selected is out of range"
              ))
            values.update(id(dest), RuntimeValue.Bits(id(resultType), Array(lanes(selected))))
            execution.pc += 1
          case (None, _) => return Left(undefined(vector))
          case (_, None) => return Left(undefined(index))
          case _         => return Left(Fault.Unsupported("OpVectorExtractDynamic requires a scalar index"))
        }
      case Instruction.VectorInsertDynamic(resultType, dest, vector, component, index) =>
        (values.get(id(vector)), values.get(id(component)), scalar(values, index)) match {
          case (Some(RuntimeValue.Bits(_, lanes)), Some(RuntimeValue.Bits(_, Array(value))), Right(selected)) =>
            val position = selected.toInt
            if (position < 0 || position >= lanes.length)
              return Left(Fault.InvalidTask(
                s"OpVectorInsertDynamic index $position is out of range"
              ))
            val result = lanes.clone()
            result(position) = value
            values.update(id(dest), RuntimeValue.Bits(id(resultType), result))
            execution.pc += 1
          case (None, _, _)        => return Left(undefined(vector))
          case (_, None, _)        => return Left(undefined(component))
          case (_, _, Left(error)) => return Left(error)
          case _                   => return Left(Fault.InvalidTask("OpVectorInsertDynamic requires scalar component"))
        }
      case other =>
        return Left(Fault.Unsupported(s"${other.opcode} execution is not implemented"))
    }
    InstructionCompleted
  }

  // Extended vector operations use indexed hot loops and fail on the first invalid lane or operand.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return", "scalafix:DisableSyntax.var"))
  private def executeExtended(
      program: Program,
      execution: InvocationExecution,
      memory: MemoryState,
      instruction: Instruction
  ): Either[Fault, Unit] = {
    val values = execution.values
    instruction match {
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
                        Fault.InvalidTask(
                          s"OpVectorShuffle component $selected is outside ${combined.length} lanes"
                        )
                      )
                  index += 1
                }
                values.update(id(dest), RuntimeValue.Bits(id(resultType), result))
                execution.pc += 1
              case Right(expected) =>
                return Left(
                  Fault.InvalidTask(
                    s"OpVectorShuffle result has ${components.size} components, expected $expected scalar leaves"
                  )
                )
              case Left(error) => return Left(error)
            }
          case (None, _) => return Left(undefined(vector1))
          case (_, None) => return Left(undefined(vector2))
          case _         => return Left(
              Fault.InvalidTask("OpVectorShuffle operands must be numeric vectors")
            )
        }
      case Instruction.ExtInst(resultType, dest, set, inst, sources) =>
        extendedInstruction(program, values, memory, resultType, dest, set, inst, sources) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case other =>
        return Left(Fault.Unsupported(s"${other.opcode} execution is not implemented"))
    }
    InstructionCompleted
  }

  // Conversion dispatch exits immediately when operand or type validation fails.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return"))
  private def executeConversion(
      program: Program,
      execution: InvocationExecution,
      memory: MemoryState,
      instruction: Instruction
  ): Either[Fault, Unit] = {
    val values = execution.values
    instruction match {
      case Instruction.UConvert(resultType, dest, source) =>
        val sourceId = id(source)
        if (values.scalarType(sourceId) >= 0) {
          val width = scalarWidth(program, id(resultType))
          values.updateScalar(id(dest), id(resultType), values.scalarBits(sourceId) & mask(width))
          execution.pc += 1
        } else
          values.get(sourceId) match {
            case Some(RuntimeValue.Bits(_, lanes)) =>
              val width = scalarWidth(program, id(resultType))
              values.update(id(dest), RuntimeValue.Bits(id(resultType), lanes.map(_ & mask(width))))
              execution.pc += 1
            case Some(_) => return Left(Fault.InvalidTask("OpUConvert cannot convert a pointer"))
            case None    => return Left(undefined(source))
          }
      case Instruction.SConvert(resultType, dest, source) =>
        signedConvert(program, values, resultType, dest, source) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.ConvertPtrToU(resultType, dest, pointer) =>
        val resultWidth = integerShape(program, id(resultType)) match {
          case Some((width, 1)) => width
          case _                =>
            return Left(Fault.InvalidTask("OpConvertPtrToU requires a scalar integer result"))
        }
        address(values, pointer).flatMap(memory.pointerInteger(_)) match {
          case Left(error)  => return Left(error)
          case Right(value) =>
            values.update(id(dest), RuntimeValue.Bits(id(resultType), Array(value & mask(resultWidth))))
            execution.pc += 1
        }
      case Instruction.ConvertUToPtr(resultType, dest, integerValue) =>
        (program.runtimeType(id(resultType)), values.get(id(integerValue))) match {
          case (
                Some(RuntimeType.Pointer(_, pointee)),
                Some(RuntimeValue.Bits(integerType, Array(rawAddress)))
              )
              if program.types.get(pointee).exists {
                case RuntimeType.Function(_, _) => true
                case _                          => false
              } &&
                integerShape(program, integerType).exists(_._2 == 1) =>
            values.update(id(dest), RuntimeValue.Bits(id(resultType), Array(rawAddress)))
            execution.pc += 1
          case (
                Some(RuntimeType.Pointer(storageClass, pointee)),
                Some(RuntimeValue.Bits(integerType, Array(rawAddress)))
              ) if integerShape(program, integerType).exists(_._2 == 1) =>
            memory.integerPointer(rawAddress & mask(scalarWidth(program, integerType)), pointee, storageClass) match {
              case Left(error) =>
                return Left(
                  Fault.InvalidTask(
                    s"${error.message} at OpConvertUToPtr function %${execution.currentFunction.id}, execution.pc ${execution.pc}, " +
                      s"source %${id(integerValue)}, result %${id(dest)}"
                  )
                )
              case Right(pointer) =>
                values.update(id(dest), RuntimeValue.Address(id(resultType), pointer))
                execution.pc += 1
            }
          case (Some(RuntimeType.Pointer(_, _)), Some(_)) =>
            return Left(Fault.InvalidTask("OpConvertUToPtr requires a scalar integer operand"))
          case (_, None)    => return Left(undefined(integerValue))
          case (Some(_), _) =>
            return Left(Fault.InvalidTask("OpConvertUToPtr requires a pointer result type"))
          case (None, _) =>
            return Left(
              Fault.InvalidTask(
                s"OpConvertUToPtr result type %${id(resultType)} is undefined"
              )
            )
        }
      case Instruction.ConvertSToF(resultType, dest, source) =>
        convertSignedToFloat(program, values, resultType, dest, source) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.ConvertUToF(resultType, dest, source) =>
        convertUnsignedToFloat(program, values, resultType, dest, source) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.ConvertFToS(resultType, dest, source) =>
        convertFloatToSigned(program, values, resultType, dest, source) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.ConvertFToU(resultType, dest, source) =>
        convertFloatToUnsigned(program, values, resultType, dest, source) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.FConvert(resultType, dest, source) =>
        convertFloating(program, values, resultType, dest, source) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.Undef(resultType, dest) =>
        zeroValue(program.types, resultType, dest, "OpUndef") match {
          case Left(error)  => return Left(error)
          case Right(value) =>
            values.update(id(dest), VMInvocation.runtimeValue(value))
            execution.pc += 1
        }
      case Instruction.CopyObject(resultType, dest, source) =>
        values.get(id(source)) match {
          case Some(value) =>
            values.update(id(dest), retag(value, id(resultType)))
            execution.pc += 1
          case None => return Left(undefined(source))
        }
      case Instruction.LifetimeStart(pointer, size) =>
        validateLifetime(program, values, "OpLifetimeStart", pointer, size) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.LifetimeStop(pointer, size) =>
        validateLifetime(program, values, "OpLifetimeStop", pointer, size) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case other =>
        return Left(Fault.Unsupported(s"${other.opcode} execution is not implemented"))
    }
    InstructionCompleted
  }

  // Integer dispatch exits immediately when operand or type validation fails.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return"))
  private def executeInteger(
      program: Program,
      execution: InvocationExecution,
      instruction: Instruction
  ): Either[Fault, Unit] = {
    val values = execution.values
    instruction match {
      case Instruction.IAdd(resultType, dest, left, right) =>
        binary(program, values, resultType, dest, left, right)(_ + _) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.ISub(resultType, dest, left, right) =>
        binary(program, values, resultType, dest, left, right)(_ - _) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.IMul(resultType, dest, left, right) =>
        binary(program, values, resultType, dest, left, right)(_ * _) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.SNegate(resultType, dest, source) =>
        signedNegate(program, values, resultType, dest, source) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
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
          case Right(())   => execution.pc += 1
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
          case Right(())   => execution.pc += 1
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
          case Right(())   => execution.pc += 1
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
          case Right(())   => execution.pc += 1
        }
      case other =>
        return Left(Fault.Unsupported(s"${other.opcode} execution is not implemented"))
    }
    InstructionCompleted
  }

  // Atomic dispatch validates and exits before mutating memory on any failure.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return"))
  private def executeAtomic(
      program: Program,
      execution: InvocationExecution,
      memory: MemoryState,
      instruction: Instruction
  ): Either[Fault, Unit] = {
    val values = execution.values
    instruction match {
      case Instruction.AtomicStore(pointer, scope, semantics, value) =>
        atomicStore(program, values, memory, pointer, scope, semantics, value) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.AtomicLoad(resultType, dest, pointer, scope, semantics) =>
        atomicLoad(program, values, memory, resultType, dest, pointer, scope, semantics) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.AtomicExchange(resultType, dest, pointer, scope, semantics, value) =>
        atomicExchange(program, values, memory, resultType, dest, pointer, scope, semantics, value) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
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
          case Right(())   => execution.pc += 1
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
          case Right(())   => execution.pc += 1
        }
      case Instruction.AtomicFlagTestAndSet(resultType, dest, pointer, scope, semantics) =>
        atomicFlagTestAndSet(program, values, memory, resultType, dest, pointer, scope, semantics) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.AtomicFlagClear(pointer, scope, semantics) =>
        atomicFlagClear(program, values, memory, pointer, scope, semantics) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
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
          case Right(())   => execution.pc += 1
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
          case Right(())   => execution.pc += 1
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
          case Right(())   => execution.pc += 1
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
          case Right(())   => execution.pc += 1
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
          case Right(())   => execution.pc += 1
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
          case Right(())   => execution.pc += 1
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
          case Right(())   => execution.pc += 1
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
          case Right(())   => execution.pc += 1
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
          case Right(())   => execution.pc += 1
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
          case Right(())   => execution.pc += 1
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
          case Right(())   => execution.pc += 1
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
          case Right(())   => execution.pc += 1
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
          case Right(())   => execution.pc += 1
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
          case Right(())   => execution.pc += 1
        }
      case Instruction.FAdd(resultType, dest, left, right) =>
        floatingBinary(program, values, resultType, dest, left, right, FloatingOperation.Add) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.FSub(resultType, dest, left, right) =>
        floatingBinary(program, values, resultType, dest, left, right, FloatingOperation.Subtract) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.FMul(resultType, dest, left, right) =>
        floatingBinary(program, values, resultType, dest, left, right, FloatingOperation.Multiply) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.VectorTimesScalar(resultType, dest, vector, scalar) =>
        floatingVectorTimesScalar(program, values, resultType, dest, vector, scalar) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.Dot(resultType, dest, left, right) =>
        floatingDot(program, values, resultType, dest, left, right) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.FDiv(resultType, dest, left, right) =>
        floatingBinary(program, values, resultType, dest, left, right, FloatingOperation.Divide) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case other =>
        return Left(Fault.Unsupported(s"${other.opcode} execution is not implemented"))
    }
    InstructionCompleted
  }

  // The XOR atomic specialization shares the same fail-before-write semantics.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return"))
  private def executeFloatingAndShift(
      program: Program,
      execution: InvocationExecution,
      instruction: Instruction
  ): Either[Fault, Unit] = {
    val values = execution.values
    instruction match {
      case Instruction.FNegate(resultType, dest, source) =>
        floatingNegate(program, values, resultType, dest, source) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.ShiftLeftLogical(resultType, dest, base, shift) =>
        integerShift(program, values, resultType, dest, base, shift, "OpShiftLeftLogical") {
          (value, amount, width) =>
            if (java.lang.Long.compareUnsigned(amount, width.value.toLong) >= 0) 0L
            else value << amount.toInt
        } match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case other =>
        return Left(Fault.Unsupported(s"${other.opcode} execution is not implemented"))
    }
    InstructionCompleted
  }

  // Arithmetic dispatch exits immediately on undefined or incompatible operands.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return"))
  private def executeArithmetic(
      program: Program,
      execution: InvocationExecution,
      instruction: Instruction
  ): Either[Fault, Unit] = {
    val values = execution.values
    instruction match {
      case Instruction.ShiftRightLogical(resultType, dest, base, shift) =>
        integerShift(program, values, resultType, dest, base, shift, "OpShiftRightLogical") {
          (value, amount, width) =>
            if (java.lang.Long.compareUnsigned(amount, width.value.toLong) >= 0) 0L
            else (value & mask(width)) >>> amount.toInt
        } match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.ShiftRightArithmetic(resultType, dest, base, shift) =>
        integerShift(program, values, resultType, dest, base, shift, "OpShiftRightArithmetic") {
          (value, amount, width) =>
            val signed = signExtend(value, width)
            if (java.lang.Long.compareUnsigned(amount, width.value.toLong) >= 0) {
              if (signed < 0L) -1L else 0L
            } else signed >> amount.toInt
        } match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.BitwiseOr(resultType, dest, left, right) =>
        integerBitwise(program, values, resultType, dest, left, right, "OpBitwiseOr")(_ | _) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.BitwiseXor(resultType, dest, left, right) =>
        integerBitwise(program, values, resultType, dest, left, right, "OpBitwiseXor")(_ ^ _) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.BitwiseAnd(resultType, dest, left, right) =>
        integerBitwise(program, values, resultType, dest, left, right, "OpBitwiseAnd")(_ & _) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.Not(resultType, dest, source) =>
        integerNot(program, values, resultType, dest, source) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.IEqual(resultType, dest, left, right) =>
        integerEquality(program, values, resultType, dest, left, right, "OpIEqual")(_ == _) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.INotEqual(resultType, dest, left, right) =>
        integerEquality(program, values, resultType, dest, left, right, "OpINotEqual")(_ != _) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.LogicalEqual(resultType, dest, left, right) =>
        logicalBinary(program, values, resultType, dest, left, right, "OpLogicalEqual")(_ == _) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.LogicalNotEqual(resultType, dest, left, right) =>
        logicalBinary(program, values, resultType, dest, left, right, "OpLogicalNotEqual")(_ != _) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.LogicalOr(resultType, dest, left, right) =>
        logicalBinary(program, values, resultType, dest, left, right, "OpLogicalOr")(_ || _) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.LogicalAnd(resultType, dest, left, right) =>
        logicalBinary(program, values, resultType, dest, left, right, "OpLogicalAnd")(_ && _) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.LogicalNot(resultType, dest, source) =>
        logicalNot(program, values, resultType, dest, source) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.Any(resultType, dest, source) =>
        logicalReduction(program, values, resultType, dest, source, all = false) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.All(resultType, dest, source) =>
        logicalReduction(program, values, resultType, dest, source, all = true) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.FOrdEqual(resultType, dest, left, right) =>
        floatingCompare(program, values, resultType, dest, left, right, FloatingComparison.OrderedEqual) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.FOrdNotEqual(resultType, dest, left, right) =>
        floatingCompare(program, values, resultType, dest, left, right, FloatingComparison.OrderedNotEqual) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.FUnordEqual(resultType, dest, left, right) =>
        floatingCompare(program, values, resultType, dest, left, right, FloatingComparison.UnorderedEqual) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.FUnordNotEqual(resultType, dest, left, right) =>
        floatingCompare(program, values, resultType, dest, left, right, FloatingComparison.UnorderedNotEqual) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.FUnordLessThan(resultType, dest, left, right) =>
        floatingCompare(program, values, resultType, dest, left, right, FloatingComparison.UnorderedLessThan) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
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
          case Right(())   => execution.pc += 1
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
          case Right(())   => execution.pc += 1
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
          case Right(())   => execution.pc += 1
        }
      case Instruction.Unordered(resultType, dest, left, right) =>
        floatingCompare(program, values, resultType, dest, left, right, FloatingComparison.Unordered) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.Ordered(resultType, dest, left, right) =>
        floatingCompare(program, values, resultType, dest, left, right, FloatingComparison.Ordered) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
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
          case Right(())   => execution.pc += 1
        }
      case Instruction.FOrdLessThan(resultType, dest, left, right) =>
        floatingCompare(program, values, resultType, dest, left, right, FloatingComparison.OrderedLessThan) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.FOrdGreaterThan(resultType, dest, left, right) =>
        floatingCompare(program, values, resultType, dest, left, right, FloatingComparison.OrderedGreaterThan) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
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
          case Right(())   => execution.pc += 1
        }
      case Instruction.IsInf(resultType, dest, value) =>
        floatingClassify(program, values, resultType, dest, value, FloatingClassification.Infinite) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.IsNan(resultType, dest, value) =>
        floatingClassify(program, values, resultType, dest, value, FloatingClassification.NaN) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.IsFinite(resultType, dest, value) =>
        floatingClassify(program, values, resultType, dest, value, FloatingClassification.Finite) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case other =>
        return Left(Fault.Unsupported(s"${other.opcode} execution is not implemented"))
    }
    InstructionCompleted
  }

  // Comparison dispatch exits immediately on undefined or incompatible operands.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return"))
  private def executeComparison(
      program: Program,
      execution: InvocationExecution,
      instruction: Instruction
  ): Either[Fault, Unit] = {
    val values = execution.values
    instruction match {
      case Instruction.IsNormal(resultType, dest, value) =>
        floatingClassify(program, values, resultType, dest, value, FloatingClassification.Normal) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.SignBitSet(resultType, dest, value) =>
        floatingClassify(program, values, resultType, dest, value, FloatingClassification.Signed) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.ULessThan(resultType, dest, left, right) =>
        compare(values, resultType, dest, left, right)(java.lang.Long.compareUnsigned(_, _) < 0) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.UGreaterThan(resultType, dest, left, right) =>
        compare(values, resultType, dest, left, right)(java.lang.Long.compareUnsigned(_, _) > 0) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.ULessThanEqual(resultType, dest, left, right) =>
        compare(values, resultType, dest, left, right)(java.lang.Long.compareUnsigned(_, _) <= 0) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.UGreaterThanEqual(resultType, dest, left, right) =>
        compare(values, resultType, dest, left, right)(java.lang.Long.compareUnsigned(_, _) >= 0) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.SGreaterThan(resultType, dest, left, right) =>
        signedCompare(program, values, resultType, dest, left, right, "OpSGreaterThan")(_ > _) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.SGreaterThanEqual(resultType, dest, left, right) =>
        signedCompare(program, values, resultType, dest, left, right, "OpSGreaterThanEqual")(_ >= _) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.SLessThan(resultType, dest, left, right) =>
        signedCompare(program, values, resultType, dest, left, right, "OpSLessThan")(_ < _) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.SLessThanEqual(resultType, dest, left, right) =>
        signedCompare(program, values, resultType, dest, left, right, "OpSLessThanEqual")(_ <= _) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
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
                execution.pc += 1
              case (None, _) =>
                return Left(Fault.InvalidTask(
                  s"OpSelect condition %${id(condition)} is not boolean"
                ))
              case (_, Left(error)) => return Left(error)
              case _                =>
                return Left(
                  Fault.InvalidTask(
                    s"OpSelect condition/value shapes are incompatible: condition ${conditionLanes.length}, " +
                      s"true ${trueLanes.length}, false ${falseLanes.length}"
                  )
                )
            }
          case (Some(RuntimeValue.Bits(conditionType, conditionLanes)), Some(yes), Some(no))
              if booleanLanes(program, conditionType).contains(1) && conditionLanes.length == 1 =>
            values.update(id(dest), retag(if (conditionLanes(0) != 0L) yes else no, id(resultType)))
            execution.pc += 1
          case (None, _, _) =>
            return Left(Fault.InvalidTask(
              s"OpSelect condition %${id(condition)} is not boolean"
            ))
          case (_, None, _) => return Left(undefined(whenTrue))
          case (_, _, None) => return Left(undefined(whenFalse))
          case _            =>
            return Left(
              Fault.InvalidTask(s"OpSelect condition %${id(condition)} is invalid")
            )
        }
      case other =>
        return Left(Fault.Unsupported(s"${other.opcode} execution is not implemented"))
    }
    InstructionCompleted
  }

  // Collective dispatch returns as soon as it emits a rendezvous or encounters an invalid operand.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return"))
  private def executeCollective(
      program: Program,
      execution: InvocationExecution,
      memory: MemoryState,
      instruction: Instruction
  ): Either[Fault, Unit] = {
    val values = execution.values
    instruction match {
      case Instruction.ControlBarrier(executionScope, memoryScope, semantics) =>
        controlBarrier(
          values,
          execution.currentFunction.id,
          execution.pc,
          executionScope,
          memoryScope,
          semantics
        ) match {
          case Left(error)    => return Left(error)
          case Right(request) =>
            suspend(
              execution,
              execution.currentFunction,
              execution.pc,
              execution.currentLabel,
              execution.predecessor,
              execution.instructionCount,
              request
            )
            return Right(())
        }
      case Instruction.MemoryBarrier(memoryScope, semantics) =>
        validateAtomicControls(values, memoryScope, semantics, "OpMemoryBarrier").flatMap(_ => memory.fence()) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.GroupAny(resultType, dest, executionScope, pred) =>
        collectiveAny(
          program,
          values,
          execution.currentFunction.id,
          execution.pc,
          resultType,
          dest,
          executionScope,
          pred
        ) match {
          case Left(error)    => return Left(error)
          case Right(request) =>
            suspend(
              execution,
              execution.currentFunction,
              execution.pc,
              execution.currentLabel,
              execution.predecessor,
              execution.instructionCount,
              request
            )
            return Right(())
        }
      case Instruction.GroupAll(resultType, dest, executionScope, pred) =>
        collectiveBoolean(
          program,
          values,
          execution.currentFunction.id,
          execution.pc,
          resultType,
          dest,
          executionScope,
          pred,
          CollectiveKind.All,
          "OpGroupAll"
        ) match {
          case Left(error)    => return Left(error)
          case Right(request) =>
            suspend(
              execution,
              execution.currentFunction,
              execution.pc,
              execution.currentLabel,
              execution.predecessor,
              execution.instructionCount,
              request
            )
            return Right(())
        }
      case Instruction.GroupUMin(resultType, dest, executionScope, operation, value) =>
        collectiveIntegerExtremum(
          program,
          values,
          execution.currentFunction.id,
          execution.pc,
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
            suspend(
              execution,
              execution.currentFunction,
              execution.pc,
              execution.currentLabel,
              execution.predecessor,
              execution.instructionCount,
              request
            )
            return Right(())
        }
      case Instruction.GroupUMax(resultType, dest, executionScope, operation, value) =>
        collectiveIntegerExtremum(
          program,
          values,
          execution.currentFunction.id,
          execution.pc,
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
            suspend(
              execution,
              execution.currentFunction,
              execution.pc,
              execution.currentLabel,
              execution.predecessor,
              execution.instructionCount,
              request
            )
            return Right(())
        }
      case Instruction.GroupSMin(resultType, dest, executionScope, operation, value) =>
        collectiveIntegerExtremum(
          program,
          values,
          execution.currentFunction.id,
          execution.pc,
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
            suspend(
              execution,
              execution.currentFunction,
              execution.pc,
              execution.currentLabel,
              execution.predecessor,
              execution.instructionCount,
              request
            )
            return Right(())
        }
      case Instruction.GroupSMax(resultType, dest, executionScope, operation, value) =>
        collectiveIntegerExtremum(
          program,
          values,
          execution.currentFunction.id,
          execution.pc,
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
            suspend(
              execution,
              execution.currentFunction,
              execution.pc,
              execution.currentLabel,
              execution.predecessor,
              execution.instructionCount,
              request
            )
            return Right(())
        }
      case Instruction.GroupIAdd(resultType, dest, executionScope, operation, value) =>
        collectiveIntegerAdd(
          program,
          values,
          execution.currentFunction.id,
          execution.pc,
          resultType,
          dest,
          executionScope,
          operation,
          value
        ) match {
          case Left(error)    => return Left(error)
          case Right(request) =>
            suspend(
              execution,
              execution.currentFunction,
              execution.pc,
              execution.currentLabel,
              execution.predecessor,
              execution.instructionCount,
              request
            )
            return Right(())
        }
      case Instruction.GroupIMulKHR(resultType, dest, executionScope, operation, value) =>
        collectiveIntegerMultiply(
          program,
          values,
          execution.currentFunction.id,
          execution.pc,
          resultType,
          dest,
          executionScope,
          operation,
          value
        ) match {
          case Left(error)    => return Left(error)
          case Right(request) =>
            suspend(
              execution,
              execution.currentFunction,
              execution.pc,
              execution.currentLabel,
              execution.predecessor,
              execution.instructionCount,
              request
            )
            return Right(())
        }
      case Instruction.GroupFAdd(resultType, dest, executionScope, operation, value) =>
        collectiveFloatingAdd(
          program,
          values,
          execution.currentFunction.id,
          execution.pc,
          resultType,
          dest,
          executionScope,
          operation,
          value
        ) match {
          case Left(error)    => return Left(error)
          case Right(request) =>
            suspend(
              execution,
              execution.currentFunction,
              execution.pc,
              execution.currentLabel,
              execution.predecessor,
              execution.instructionCount,
              request
            )
            return Right(())
        }
      case Instruction.GroupFMin(resultType, dest, executionScope, operation, value) =>
        collectiveFloatingExtremum(
          program,
          values,
          execution.currentFunction.id,
          execution.pc,
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
            suspend(
              execution,
              execution.currentFunction,
              execution.pc,
              execution.currentLabel,
              execution.predecessor,
              execution.instructionCount,
              request
            )
            return Right(())
        }
      case Instruction.GroupFMax(resultType, dest, executionScope, operation, value) =>
        collectiveFloatingExtremum(
          program,
          values,
          execution.currentFunction.id,
          execution.pc,
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
            suspend(
              execution,
              execution.currentFunction,
              execution.pc,
              execution.currentLabel,
              execution.predecessor,
              execution.instructionCount,
              request
            )
            return Right(())
        }
      case Instruction.GroupFMulKHR(resultType, dest, executionScope, operation, value) =>
        collectiveFloatingMultiply(
          program,
          values,
          execution.currentFunction.id,
          execution.pc,
          resultType,
          dest,
          executionScope,
          operation,
          value
        ) match {
          case Left(error)    => return Left(error)
          case Right(request) =>
            suspend(
              execution,
              execution.currentFunction,
              execution.pc,
              execution.currentLabel,
              execution.predecessor,
              execution.instructionCount,
              request
            )
            return Right(())
        }
      case Instruction.SubgroupShuffleINTEL(resultType, dest, data, invocationId) =>
        collectiveShuffle(
          values,
          execution.currentFunction.id,
          execution.pc,
          resultType,
          dest,
          data,
          invocationId
        ) match {
          case Left(error)    => return Left(error)
          case Right(request) =>
            suspend(
              execution,
              execution.currentFunction,
              execution.pc,
              execution.currentLabel,
              execution.predecessor,
              execution.instructionCount,
              request
            )
            return Right(())
        }
      case Instruction.SubgroupShuffleUpINTEL(resultType, dest, previous, current, delta) =>
        collectiveShuffleUp(
          values,
          execution.currentFunction.id,
          execution.pc,
          resultType,
          dest,
          previous,
          current,
          delta
        ) match {
          case Left(error)    => return Left(error)
          case Right(request) =>
            suspend(
              execution,
              execution.currentFunction,
              execution.pc,
              execution.currentLabel,
              execution.predecessor,
              execution.instructionCount,
              request
            )
            return Right(())
        }
      case Instruction.SubgroupShuffleDownINTEL(resultType, dest, current, next, delta) =>
        collectiveShuffleDown(
          values,
          execution.currentFunction.id,
          execution.pc,
          resultType,
          dest,
          current,
          next,
          delta
        ) match {
          case Left(error)    => return Left(error)
          case Right(request) =>
            suspend(
              execution,
              execution.currentFunction,
              execution.pc,
              execution.currentLabel,
              execution.predecessor,
              execution.instructionCount,
              request
            )
            return Right(())
        }
      case Instruction.GroupBroadcast(resultType, dest, executionScope, value, localId) =>
        collectiveBroadcast(
          program,
          values,
          execution.currentFunction.id,
          execution.pc,
          resultType,
          dest,
          executionScope,
          value,
          localId
        ) match {
          case Left(error)    => return Left(error)
          case Right(request) =>
            suspend(
              execution,
              execution.currentFunction,
              execution.pc,
              execution.currentLabel,
              execution.predecessor,
              execution.instructionCount,
              request
            )
            return Right(())
        }
      case Instruction.GroupNonUniformBallot(resultType, dest, executionScope, pred) =>
        collectiveBallot(
          program,
          values,
          execution.currentFunction.id,
          execution.pc,
          resultType,
          dest,
          executionScope,
          pred
        ) match {
          case Left(error)    => return Left(error)
          case Right(request) =>
            suspend(
              execution,
              execution.currentFunction,
              execution.pc,
              execution.currentLabel,
              execution.predecessor,
              execution.instructionCount,
              request
            )
            return Right(())
        }
      case other =>
        return Left(Fault.Unsupported(s"${other.opcode} execution is not implemented"))
    }
    InstructionCompleted
  }

  // Builtin and function-call argument decoding is indexed and fails fast.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return", "scalafix:DisableSyntax.var"))
  private def executeCall(
      program: Program,
      execution: InvocationExecution,
      memory: MemoryState,
      instruction: Instruction
  ): Either[Fault, Unit] = {
    val values = execution.values
    instruction match {
      case Instruction.FunctionPointerCallINTEL(resultType, dest, sources) if sources.nonEmpty =>
        return values.get(id(sources.head)) match {
          case Some(RuntimeValue.Bits(_, Array(token)))
              if (token >>> 32) == program.programId &&
                (token & 0xffffffffL) > 0 && (token & 0xffffffffL) <= Int.MaxValue &&
                program.functions.contains((token & 0xffffffffL).toInt) =>
            val called = sources.head.copy(words = Vec(Word((token & 0xffffffffL).toInt)).refineUnsafe[MinLength[1]])
            executeCall(program, execution, memory, Instruction.FunctionCall(resultType, dest, called, sources.tail))
          case Some(_) => Left(Fault.InvalidTask("OpFunctionPointerCallINTEL has an invalid function pointer"))
          case None    => Left(undefined(sources.head))
        }
      case Instruction.FunctionCall(resultType, dest, called, arguments) =>
        val callee = program.functions.get(id(called)) match {
          case Some(value) => value
          case None        =>
            return Left(
              Fault.InvalidTask(
                s"OpFunctionCall references undefined function %${id(called)}"
              )
            )
        }
        if (arguments.size != callee.parameters.size)
          return Left(
            Fault.InvalidTask(
              s"OpFunctionCall %${id(called)} expects ${callee.parameters.size} arguments, received ${arguments.size}"
            )
          )
        program.functionNames.get(id(called)).map(_.value) match {
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
                    execution.pc += 1
                  case _ =>
                    return Left(
                      Fault.InvalidTask(
                        s"OpenCL get_local_id result type %${id(resultType)} is not an integer scalar"
                      )
                    )
                }
              case Right(dimension) =>
                return Left(Fault.InvalidTask(
                  s"OpenCL get_local_id dimension $dimension is invalid"
                ))
            }
          case Some("_Z12get_group_idj") | Some("_Z14get_local_sizej") | Some("_Z23get_enqueued_local_sizej") =>
            scalar(values, arguments.head) match {
              case Left(error)                                           => return Left(error)
              case Right(dimension) if dimension >= 0L && dimension < 3L =>
                integerShape(program, id(resultType)) match {
                  case Some((width, 1)) =>
                    val source = program.functionNames.get(id(called)).map(_.value) match {
                      case Some("_Z12get_group_idj")            => execution.invocation.group(dimension.toInt)
                      case Some("_Z14get_local_sizej")          => execution.invocation.workgroupSize(dimension.toInt)
                      case Some("_Z23get_enqueued_local_sizej") =>
                        execution.invocation.enqueuedWorkgroupSize(dimension.toInt)
                      case _ => 0L
                    }
                    values.update(id(dest), RuntimeValue.Bits(id(resultType), Array(source & mask(width))))
                    execution.pc += 1
                  case _ =>
                    return Left(
                      Fault.InvalidTask(
                        s"OpenCL ${program.functionNames.get(id(called)).fold("builtin")(_.value)} result type " +
                          s"%${id(resultType)} is not an integer scalar"
                      )
                    )
                }
              case Right(dimension) =>
                return Left(
                  Fault.InvalidTask(
                    s"OpenCL ${program.functionNames.get(id(called)).fold("builtin")(_.value)} dimension $dimension is invalid"
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
                    execution.pc += 1
                  case _ =>
                    return Left(
                      Fault.InvalidTask(
                        "OpenCL get_global_id result type is not an integer scalar"
                      )
                    )
                }
              case Right(dimension) =>
                return Left(Fault.InvalidTask(
                  s"OpenCL get_global_id dimension $dimension is invalid"
                ))
            }
          case Some("_Z15get_global_sizej") | Some("_Z17get_global_offsetj") | Some("_Z14get_num_groupsj") =>
            scalar(values, arguments.head) match {
              case Left(error)                                           => return Left(error)
              case Right(dimension) if dimension >= 0L && dimension < 3L =>
                integerShape(program, id(resultType)) match {
                  case Some((width, 1)) =>
                    val source = program.functionNames.get(id(called)).map(_.value) match {
                      case Some("_Z15get_global_sizej")   => execution.invocation.globalSize(dimension.toInt)
                      case Some("_Z17get_global_offsetj") => execution.invocation.globalOffset(dimension.toInt)
                      case Some("_Z14get_num_groupsj")    => execution.invocation.numWorkgroups(dimension.toInt)
                      case _                              => 0L
                    }
                    values.update(id(dest), RuntimeValue.Bits(id(resultType), Array(source & mask(width))))
                    execution.pc += 1
                  case _ =>
                    return Left(
                      Fault.InvalidTask(
                        s"OpenCL ${program.functionNames.get(id(called)).fold("builtin")(_.value)} result type is not an integer scalar"
                      )
                    )
                }
              case Right(dimension) =>
                return Left(
                  Fault.InvalidTask(
                    s"OpenCL ${program.functionNames.get(id(called)).fold("builtin")(_.value)} dimension $dimension is invalid"
                  )
                )
            }
          case Some("_Z18get_sub_group_sizev") | Some("_Z22get_sub_group_local_idv") |
              Some("_Z16get_sub_group_idv") | Some("_Z18get_num_sub_groupsv") =>
            integerShape(program, id(resultType)) match {
              case Some((width, 1)) =>
                val source = program.functionNames.get(id(called)).map(_.value) match {
                  case Some("_Z18get_sub_group_sizev")     => execution.invocation.subgroupSize.toLong
                  case Some("_Z22get_sub_group_local_idv") => execution.invocation.subgroupLocal.toLong
                  case Some("_Z16get_sub_group_idv")       => execution.invocation.subgroupIndex.toLong
                  case Some("_Z18get_num_sub_groupsv")     => execution.invocation.numSubgroups
                  case _                                   => 0L
                }
                values.update(id(dest), RuntimeValue.Bits(id(resultType), Array(source & mask(width))))
                execution.pc += 1
              case _ =>
                return Left(
                  Fault.InvalidTask(
                    s"OpenCL ${program.functionNames.get(id(called)).fold("builtin")(_.value)} result type " +
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
                execution.pc += 1
              case (Left(error), _, _) => return Left(error)
              case (_, Left(error), _) => return Left(error)
              case (_, _, _)           =>
                return Left(
                  Fault.InvalidTask("OpenCL min requires integer scalar arguments and result")
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
                val count =
                  if (bits == 0L) sourceWidth.value.toLong
                  else java.lang.Long.numberOfTrailingZeros(bits).toLong
                values.update(id(dest), RuntimeValue.Bits(id(resultType), Array(count & mask(resultWidth))))
                execution.pc += 1
              case (Left(error), _, _) => return Left(error)
              case _                   =>
                return Left(
                  Fault.InvalidTask("OpenCL ctz requires integer scalar arguments and result")
                )
            }
          case Some(name) if name.startsWith("_Z26intel_sub_group_shuffle_up") =>
            if (arguments.size != 3)
              return Left(
                Fault.InvalidTask(
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
                  execution.currentFunction,
                  execution.pc,
                  execution.currentLabel,
                  execution.predecessor,
                  execution.instructionCount,
                  CollectiveRequest(
                    CollectiveKind.ShuffleUp,
                    execution.currentFunction.id,
                    execution.pc,
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
                return Left(Fault.InvalidTask("OpenCL subgroup shuffle-up delta must be non-negative"))
              case (Some(_), Some(_), Right(_)) =>
                return Left(Fault.InvalidTask(
                  "OpenCL subgroup shuffle-up requires matching result and operand types"
                ))
              case (None, _, _)        => return Left(undefined(arguments(0)))
              case (_, None, _)        => return Left(undefined(arguments(1)))
              case (_, _, Left(error)) => return Left(error)
            }
          case Some("__devicelib_ConvertFToBF16INTEL") =>
            if (arguments.size != 1)
              return Left(
                Fault.InvalidTask(
                  s"__devicelib_ConvertFToBF16INTEL expects one pointer argument, received ${arguments.size}"
                )
              )
            values.get(id(arguments.head)) match {
              case Some(RuntimeValue.Address(_, pointer)) =>
                memory.readScalar(pointer, MemoryValueByteWidth.Dword) match {
                  case Left(error) => return Left(error)
                  case Right(raw)  =>
                    values.update(
                      id(dest),
                      RuntimeValue.Bits(id(resultType), Array(float32ToBfloat16Bits(raw)))
                    )
                    execution.pc += 1
                }
              case Some(_) =>
                return Left(
                  Fault.InvalidTask("__devicelib_ConvertFToBF16INTEL argument is not a pointer")
                )
              case None => return Left(undefined(arguments.head))
            }
          case Some("__devicelib_ConvertBF16ToFINTEL") =>
            if (arguments.size != 1)
              return Left(
                Fault.InvalidTask(
                  s"__devicelib_ConvertBF16ToFINTEL expects one pointer argument, received ${arguments.size}"
                )
              )
            values.get(id(arguments.head)) match {
              case Some(RuntimeValue.Address(_, pointer)) =>
                memory.readScalar(pointer, MemoryValueByteWidth.Word) match {
                  case Left(error) => return Left(error)
                  case Right(raw)  =>
                    values.update(
                      id(dest),
                      RuntimeValue.Bits(id(resultType), Array(bfloat16ToFloat32Bits(raw)))
                    )
                    execution.pc += 1
                }
              case Some(_) =>
                return Left(
                  Fault.InvalidTask("__devicelib_ConvertBF16ToFINTEL argument is not a pointer")
                )
              case None => return Left(undefined(arguments.head))
            }
          case Some("__to_global") =>
            (values.get(id(arguments.head)), program.runtimeType(id(resultType))) match {
              case (Some(RuntimeValue.Address(_, pointer)), Some(RuntimeType.Pointer(storageClass, pointee))) =>
                values.update(
                  id(dest),
                  RuntimeValue.Address(
                    id(resultType),
                    pointer.copy(pointee = pointee, storageClass = storageClass)
                  )
                )
                execution.pc += 1
              case (Some(RuntimeValue.Address(_, _)), _) =>
                return Left(Fault.InvalidTask("OpenCL __to_global result is not a pointer"))
              case (Some(_), _) =>
                return Left(Fault.InvalidTask("OpenCL __to_global source is not a pointer"))
              case (None, _) => return Left(undefined(arguments.head))
            }
          case Some("_Z5isnanf") =>
            values.get(id(arguments.head)) match {
              case Some(RuntimeValue.Bits(sourceType, Array(raw))) =>
                (floatingShape(program, sourceType), integerShape(program, id(resultType))) match {
                  case (Some((width, 1)), Some((resultWidth, 1))) =>
                    val (exponentMask, fractionMask) = width.value match {
                      case 16 => (0x7c00L, 0x03ffL)
                      case 32 => (0x7f800000L, 0x007fffffL)
                      case 64 => (0x7ff0000000000000L, 0x000fffffffffffffL)
                    }
                    val bits  = raw & mask(width.bitWidth)
                    val isNan = (bits & exponentMask) == exponentMask && (bits & fractionMask) != 0L
                    values.update(
                      id(dest),
                      RuntimeValue.Bits(id(resultType), Array((if (isNan) 1L else 0L) & mask(resultWidth)))
                    )
                    execution.pc += 1
                  case (None, _) =>
                    return Left(Fault.InvalidTask("OpenCL isnan source is not a floating-point scalar"))
                  case (_, None) =>
                    return Left(Fault.InvalidTask("OpenCL isnan result is not an integer scalar"))
                  case _ =>
                    return Left(
                      Fault.InvalidTask(
                        "OpenCL isnan requires scalar source and result types"
                      )
                    )
                }
              case Some(_) =>
                return Left(Fault.InvalidTask("OpenCL isnan source is not numeric"))
              case None => return Left(undefined(arguments.head))
            }
          case Some("_Z7barrierj") =>
            scalar(values, arguments.head) match {
              case Left(error)  => return Left(error)
              case Right(flags) =>
                suspend(
                  execution,
                  execution.currentFunction,
                  execution.pc,
                  execution.currentLabel,
                  execution.predecessor,
                  execution.instructionCount,
                  CollectiveRequest(
                    CollectiveKind.Barrier,
                    execution.currentFunction.id,
                    execution.pc,
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
                  execution.currentFunction,
                  execution.pc,
                  execution.currentLabel,
                  execution.predecessor,
                  execution.instructionCount,
                  CollectiveRequest(
                    CollectiveKind.IntegerAdd,
                    execution.currentFunction.id,
                    execution.pc,
                    2,
                    CollectivePayload.Value(id(resultType), id(dest), value),
                    operation = 2
                  )
                )
                return Right(())
              case Some(_) =>
                return Left(
                  Fault.InvalidTask(
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
                val scope = program.functionNames.get(id(called)).map(_.value) match {
                  case Some("_Z28sub_group_scan_exclusive_addi") => 3
                  case _                                         => 2
                }
                suspend(
                  execution,
                  execution.currentFunction,
                  execution.pc,
                  execution.currentLabel,
                  execution.predecessor,
                  execution.instructionCount,
                  CollectiveRequest(
                    CollectiveKind.IntegerAdd,
                    execution.currentFunction.id,
                    execution.pc,
                    scope,
                    CollectivePayload.Value(id(resultType), id(dest), value),
                    operation = 2
                  )
                )
                return Right(())
              case Some(_) =>
                return Left(
                  Fault.InvalidTask(
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
                  execution.currentFunction,
                  execution.pc,
                  execution.currentLabel,
                  execution.predecessor,
                  execution.instructionCount,
                  CollectiveRequest(
                    CollectiveKind.Broadcast,
                    execution.currentFunction.id,
                    execution.pc,
                    scope,
                    CollectivePayload.Value(id(resultType), id(dest), value),
                    selector = Vec(localId & mask(width))
                  )
                )
                return Right(())
              case (None, _, _)        => return Left(undefined(arguments(0)))
              case (_, Left(error), _) => return Left(error)
              case _                   =>
                return Left(
                  Fault.InvalidTask(
                    "OpenCL broadcast requires matching integer scalar arguments and result"
                  )
                )
            }
          case _ =>
            if (execution.calls.size >= 128)
              return Left(Fault.Backend("SPIR-V function call depth exceeds 128 frames"))
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
            execution.calls.append(
              CallFrame(
                execution.currentFunction,
                execution.pc + 1,
                execution.currentLabel,
                execution.predecessor,
                id(resultType),
                id(dest)
              )
            )
            execution.currentFunction = callee
            execution.pc = callee.start + 1
            execution.currentLabel = -1
            execution.predecessor = -1
        }
      case other =>
        return Left(Fault.Unsupported(s"${other.opcode} execution is not implemented"))
    }
    InstructionCompleted
  }

  // Branch-table decoding advances a cursor and exits on malformed control flow.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return", "scalafix:DisableSyntax.var"))
  private def executeBranch(
      program: Program,
      execution: InvocationExecution,
      instruction: Instruction
  ): Either[Fault, Unit] = {
    val values = execution.values
    instruction match {
      case Instruction.Branch(target) =>
        execution.predecessor = execution.currentLabel
        label(execution.currentFunction, target) match {
          case Left(error) => return Left(error)
          case Right(pc)   => execution.pc = pc
        }
      case Instruction.BranchConditional(condition, whenTrue, whenFalse, _) =>
        scalar(values, condition) match {
          case Left(error) => return Left(error)
          case Right(test) =>
            execution.predecessor = execution.currentLabel
            label(execution.currentFunction, if (test != 0L) whenTrue else whenFalse) match {
              case Left(error) => return Left(error)
              case Right(pc)   => execution.pc = pc
            }
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
                case Vec(low, _) =>
                  if (pair.kind.value != "PairLiteralIntegerIdRef")
                    return Left(Fault.InvalidTask("OpSwitch target is not a literal/label pair"))
                  low.toUnsigned
                case Vec(low, high, _) =>
                  if (pair.kind.value != "PairLiteralIntegerIdRef")
                    return Left(Fault.InvalidTask("OpSwitch target is not a literal/label pair"))
                  low.toUnsigned | (high.toUnsigned << 32)
                case _ =>
                  return Left(Fault.InvalidTask("OpSwitch has an incomplete literal/label pair"))
              }
              if (pair.words.size != 2 && pair.words.size != 3)
                return Left(Fault.InvalidTask("OpSwitch has an incomplete literal/label pair"))
              val label = pair.words.last
              if (literal == value)
                target = Operand(SPIRVOperandKind("IdRef"), Vec.nonEmpty(label))
              index += 1
            }
            execution.predecessor = execution.currentLabel
            label(execution.currentFunction, target) match {
              case Left(error) => return Left(error)
              case Right(pc)   => execution.pc = pc
            }
        }
      case Instruction.ReturnValue(value) =>
        val returned = values.get(id(value)) match {
          case Some(result) => result
          case None         => return Left(undefined(value))
        }
        execution.calls.removeLastOption() match {
          case None        => return Left(Fault.InvalidTask("SPIR-V entry point returned a value"))
          case Some(frame) =>
            values.update(frame.destination, retag(returned, frame.resultType))
            execution.currentFunction = frame.function
            execution.pc = frame.pc
            execution.currentLabel = frame.currentLabel
            execution.predecessor = frame.predecessor
        }
      case Instruction.Return() => execution.calls.removeLastOption() match {
          case None =>
            execution.completed = true
            return Right(())
          case Some(frame) => program.runtimeType(frame.resultType) match {
              case Some(RuntimeType.Void) =>
                execution.currentFunction = frame.function
                execution.pc = frame.pc
                execution.currentLabel = frame.currentLabel
                execution.predecessor = frame.predecessor
              case _ =>
                return Left(
                  Fault.InvalidTask(
                    s"SPIR-V function %${execution.currentFunction.id} returned no value to %${frame.destination}"
                  )
                )
            }
        }
      case Instruction.AssumeTrueKHR(_) | Instruction.LoopMerge(_, _, _) | Instruction.SelectionMerge(_, _) =>
        execution.pc += 1
      case other =>
        return Left(Fault.Unsupported(s"${other.opcode} execution is not implemented"))
    }
    InstructionCompleted
  }

  private def suspend(
      execution: InvocationExecution,
      function: Function,
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

  // Workgroup progress mutates invocation machines in place and stops at the first fault or deadlock.
  @SuppressWarnings(
    Array(
      "scalafix:DisableSyntax.var",
      "scalafix:DisableSyntax.return"
    )
  )
  private def runWorkgroup(
      program: Program,
      executions: mutable.ArrayBuffer[InvocationExecution],
      memory: MemoryState,
      control: TaskControl,
      metrics: Performance.Metrics
  ): Either[Fault, Unit] = {
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
      val hadPendingMemory = executions.exists(_.memoryPending)
      if (hadPendingMemory) {
        memory.flushHostReads() match {
          case Left(error) => return Left(error)
          case Right(())   => executions.foreach(_.memoryPending = false)
        }
      }
      resolveCollectives(program, executions) match {
        case Left(error)                                                      => return Left(error)
        case Right(0) if !hadPendingMemory && executions.exists(!_.completed) =>
          val waiting = executions.view
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
            Fault.InvalidTask(
              s"SPIR-V collective participation diverged within a workgroup: $waiting"
            )
          )
        case Right(_) => ()
      }
    }
    Right(())
  }

  // Collective resolution scans the mutable invocation set until no suspended operation remains.
  @SuppressWarnings(
    Array(
      "scalafix:DisableSyntax.var",
      "scalafix:DisableSyntax.return"
    )
  )
  private def resolveCollectives(
      program: Program,
      executions: mutable.ArrayBuffer[InvocationExecution]
  ): Either[Fault, Int] = {
    var resolved  = 0
    val subgroups = executions.view.map(_.invocation.subgroupIndex).toSet.toArray.sorted
    var index     = 0
    while (index < subgroups.length) {
      val subgroup     = subgroups(index)
      val participants = Vec.from(executions.view.filter(_.invocation.subgroupIndex == subgroup))
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
        resolveCollective(program, Vec.from(executions), request) match {
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

  // Per-lane reduction and ballot loops write directly into the collective result buffer.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def resolveCollective(
      program: Program,
      participants: Vec[InvocationExecution],
      expected: CollectiveRequest
  ): Either[Fault, Unit] = {
    val requests = participants.map(_.suspended.get)
    def scan(
        inputs: Vec[Array[Long]],
        initial: Long,
        resultType: Int
    )(combine: (Long, Long) => Long): Vec[RuntimeValue] = {
      val accumulated = Array.fill(inputs.head.length)(initial)
      val outputs     = Vec.newBuilder[RuntimeValue]
      inputs.foreach { input =>
        val before = accumulated.clone()
        var lane   = 0
        while (lane < accumulated.length) {
          accumulated(lane) = combine(accumulated(lane), input(lane))
          lane += 1
        }
        outputs += RuntimeValue.Bits(resultType, if (expected.operation == 1) accumulated.clone() else before)
      }
      outputs.result()
    }
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
          Left(Fault.InvalidTask(
            "subgroup shuffle participants have incompatible payloads"
          ))
        else {
          val results =
            participants.zip(shuffleRequests).foldLeft[Either[Fault, Vec[RuntimeValue]]](Right(Vec.empty)) {
              case (Right(existing), request) =>
                val invocationId = request._2._3
                invocationId match {
                  case RuntimeValue.Bits(_, Array(sourceLocal)) =>
                    participants.find(_.invocation.subgroupLocal.toLong == sourceLocal) match {
                      case Some(sourceExecution) =>
                        sourceExecution.suspended match {
                          case Some(CollectiveRequest(_, _, _, _, CollectivePayload.Shuffle(_, _, data, _), _, _)) =>
                            Right(existing :+ retag(data, resultType))
                          case _ => Left(Fault.InvalidTask("subgroup shuffle source is not suspended"))
                        }
                      case None =>
                        Left(Fault.InvalidTask(
                          s"subgroup shuffle source lane $sourceLocal is absent"
                        ))
                    }
                  case _ => Left(Fault.InvalidTask("subgroup shuffle invocation id must be scalar"))
                }
              case (Left(error), _) => Left(error)
            }
          results match {
            case Left(error)   => Left(error)
            case Right(values) =>
              participants.zip(values).foreach { case (execution, value) =>
                execution.suspended.get.payload match {
                  case CollectivePayload.Shuffle(requestType, destination, _, _) =>
                    execution.values.update(destination, retag(value, requestType))
                  case _ => ()
                }
                execution.pc = execution.suspended.get.pc + 1
                execution.suspended = None
              }
              Right(())
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
          Left(Fault.InvalidTask(
            "subgroup shuffle-up participants have incompatible payloads"
          ))
        else {
          val results =
            participants.zip(shuffleRequests).foldLeft[Either[Fault, Vec[RuntimeValue]]](Right(Vec.empty)) {
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
                      case _ => Left(Fault.InvalidTask("subgroup shuffle-up source is not suspended"))
                    }
                  case None =>
                    Left(Fault.InvalidTask(
                      s"subgroup shuffle-up source lane $sourceLocal is absent"
                    ))
                }
                value.map(result => existing :+ retag(result, resultType))
              case (Left(error), _) => Left(error)
            }
          results match {
            case Left(error)   => Left(error)
            case Right(values) =>
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
              Right(())
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
          Left(Fault.InvalidTask(
            "subgroup shuffle-down participants have incompatible payloads"
          ))
        else {
          val results =
            participants.zip(shuffleRequests).foldLeft[Either[Fault, Vec[RuntimeValue]]](Right(Vec.empty)) {
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
                      case _ => Left(Fault.InvalidTask("subgroup shuffle-down source is not suspended"))
                    }
                  case None =>
                    Left(Fault.InvalidTask(
                      s"subgroup shuffle-down source lane $sourceLocal is absent"
                    ))
                }
                value.map(result => existing :+ retag(result, resultType))
              case (Left(error), _) => Left(error)
            }
          results match {
            case Left(error)   => Left(error)
            case Right(values) =>
              participants.zip(values).foreach { case (execution, value) =>
                execution.suspended.get.payload match {
                  case CollectivePayload.ShuffleDown(requestType, destination, _, _, _) =>
                    execution.values.update(destination, retag(value, requestType))
                  case _ => ()
                }
                execution.pc = execution.suspended.get.pc + 1
                execution.suspended = None
              }
              Right(())
          }
        }
      case CollectivePayload.Value(resultType, _, _) =>
        val requestValues = requests.collect {
          case CollectiveRequest(_, _, _, _, CollectivePayload.Value(_, _, value), _, _) =>
            value
        }
        if (requestValues.size != requests.size)
          Left(
            Fault.InvalidTask("collective participants have incompatible payloads")
          )
        else {
          val results = expected.kind match {
            case CollectiveKind.Any =>
              val any = requestValues.exists {
                case RuntimeValue.Bits(_, Array(value)) => value != 0L
                case _                                  => false
              }
              Right(Vec.fill(participants.size)(RuntimeValue.Bits(resultType, Array(if (any) 1L else 0L))))
            case CollectiveKind.All =>
              val all = requestValues.forall {
                case RuntimeValue.Bits(_, Array(value)) => value != 0L
                case _                                  => false
              }
              Right(Vec.fill(participants.size)(RuntimeValue.Bits(resultType, Array(if (all) 1L else 0L))))
            case CollectiveKind.UnsignedMin | CollectiveKind.UnsignedMax | CollectiveKind.SignedMin |
                CollectiveKind.SignedMax =>
              val inputs = requestValues.collect { case RuntimeValue.Bits(_, lanes) => lanes }
              if (inputs.size != requests.size || inputs.isEmpty)
                Left(
                  Fault.InvalidTask(
                    s"${expected.kind} participants do not all provide integer values"
                  )
                )
              else {
                val width   = integerShape(program, resultType).map(_._1).getOrElse(BitWidth(64))
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
                    inputs.view.map(_(lane) & mask(width)).reduce(extremum(_, _))
                  }
                  Right(Vec.fill(participants.size)(RuntimeValue.Bits(resultType, lanes)))
                } else {
                  val identity =
                    if (signed) {
                      if (maximum) 1L << (width.value - 1) else (1L << (width.value - 1)) - 1L
                    } else if (maximum) 0L
                    else mask(width)
                  Right(scan(inputs, identity & mask(width), resultType)((left, right) =>
                    extremum(left, right & mask(width))
                  ))
                }
              }
            case CollectiveKind.IntegerAdd =>
              val inputs = requestValues.collect { case RuntimeValue.Bits(_, lanes) => lanes }
              if (inputs.size != requests.size || inputs.isEmpty)
                Left(Fault.InvalidTask(
                  "OpGroupIAdd participants do not all provide integer values"
                ))
              else {
                val width = integerShape(program, resultType).map(_._1).getOrElse(BitWidth(64))
                if (expected.operation == 0) {
                  val reduced = Array.tabulate(inputs.head.length) { lane =>
                    inputs.view.foldLeft(0L)((sum, input) => (sum + input(lane)) & mask(width))
                  }
                  Right(Vec.fill(participants.size)(RuntimeValue.Bits(resultType, reduced)))
                } else {
                  Right(scan(inputs, 0L, resultType)((left, right) => (left + right) & mask(width)))
                }
              }
            case CollectiveKind.IntegerMultiply =>
              val inputs = requestValues.collect { case RuntimeValue.Bits(_, lanes) => lanes }
              if (inputs.size != requests.size || inputs.isEmpty)
                Left(Fault.InvalidTask(
                  "OpGroupIMulKHR participants do not all provide integer values"
                ))
              else {
                val width = integerShape(program, resultType).map(_._1).getOrElse(BitWidth(64))
                if (expected.operation == 0) {
                  val reduced = Array.tabulate(inputs.head.length) { lane =>
                    inputs.view.foldLeft(1L)((product, input) => (product * input(lane)) & mask(width))
                  }
                  Right(Vec.fill(participants.size)(RuntimeValue.Bits(resultType, reduced)))
                } else {
                  Right(scan(inputs, 1L, resultType)((left, right) => (left * right) & mask(width)))
                }
              }
            case CollectiveKind.FloatingAdd =>
              val inputs = requestValues.collect { case RuntimeValue.Bits(_, lanes) => lanes }
              if (inputs.size != requests.size || inputs.isEmpty)
                Left(Fault.InvalidTask(
                  "OpGroupFAdd participants do not all provide floating values"
                ))
              else {
                val width = floatingShape(program, resultType).map(_._1).getOrElse(FloatingPointWidth(64))
                if (expected.operation == 0) {
                  val reduced = Array.tabulate(inputs.head.length) { lane =>
                    inputs.view.foldLeft(0L)((sum, input) => floatingAddBits(width, sum, input(lane)))
                  }
                  Right(Vec.fill(participants.size)(RuntimeValue.Bits(resultType, reduced)))
                } else {
                  Right(scan(inputs, 0L, resultType)((left, right) => floatingAddBits(width, left, right)))
                }
              }
            case CollectiveKind.FloatingMin | CollectiveKind.FloatingMax =>
              val inputs = requestValues.collect { case RuntimeValue.Bits(_, lanes) => lanes }
              if (inputs.size != requests.size || inputs.isEmpty)
                Left(Fault.InvalidTask(
                  s"${expected.kind} participants do not all provide values"
                ))
              else {
                val width   = floatingShape(program, resultType).map(_._1).getOrElse(FloatingPointWidth(64))
                val maximum = expected.kind == CollectiveKind.FloatingMax
                def extremum(left: Long, right: Long): Long =
                  if (maximum) floatingMaximumBits(width, left, right)
                  else floatingMinimumBits(width, left, right)
                if (expected.operation == 0) {
                  val reduced = Array.tabulate(inputs.head.length) { lane =>
                    inputs.view.map(_(lane)).reduce(extremum(_, _))
                  }
                  Right(Vec.fill(participants.size)(RuntimeValue.Bits(resultType, reduced)))
                } else {
                  val identity = floatingInfinityBits(width, negative = maximum)
                  Right(scan(inputs, identity, resultType)(extremum(_, _)))
                }
              }
            case CollectiveKind.FloatingMultiply =>
              val inputs = requestValues.collect { case RuntimeValue.Bits(_, lanes) => lanes }
              if (inputs.size != requests.size || inputs.isEmpty)
                Left(Fault.InvalidTask(
                  "OpGroupFMulKHR participants do not all provide floating values"
                ))
              else {
                val width    = floatingShape(program, resultType).map(_._1).getOrElse(FloatingPointWidth(64))
                val identity = floatingOneBits(width)
                if (expected.operation == 0) {
                  val reduced = Array.tabulate(inputs.head.length) { lane =>
                    inputs.view.foldLeft(identity) { (product, input) =>
                      floatingMultiplyBits(width, product, input(lane))
                    }
                  }
                  Right(Vec.fill(participants.size)(RuntimeValue.Bits(resultType, reduced)))
                } else {
                  Right(scan(inputs, identity, resultType)((left, right) => floatingMultiplyBits(width, left, right)))
                }
              }
            case CollectiveKind.ShuffleUp =>
              Left(Fault.InvalidTask("subgroup shuffle-up payload was resolved as a value collective"))
            case CollectiveKind.ShuffleDown =>
              Left(Fault.InvalidTask("subgroup shuffle-down payload was resolved as a value collective"))
            case CollectiveKind.Shuffle =>
              Left(Fault.InvalidTask("subgroup shuffle payload was resolved as a value collective"))
            case CollectiveKind.Broadcast =>
              val selected = (expected.scope, expected.selector) match {
                case (3, Vec(x)) =>
                  participants.indexWhere(_.invocation.subgroupLocal.toLong == x)
                case (_, Vec(x)) =>
                  participants.indexWhere(_.invocation.localX == x)
                case (_, Vec(x, y, z)) =>
                  participants.indexWhere(execution =>
                    execution.invocation.localX == x && execution.invocation.localY == y &&
                      execution.invocation.localZ == z
                  )
                case _ => -1
              }
              if (selected < 0)
                Left(
                  Fault.InvalidTask(
                    s"OpGroupBroadcast local id ${expected.selector.mkString("(", ",", ")")} is outside the workgroup"
                  )
                )
              else Right(Vec.fill(participants.size)(requestValues(selected)))
            case CollectiveKind.Ballot =>
              val ballot = Array.fill(4)(0L)
              val valid  = participants.zip(requestValues).forall {
                case (execution, RuntimeValue.Bits(_, Array(predicate))) =>
                  val local = execution.invocation.subgroupLocal
                  if (predicate != 0L) ballot(local / 32) |= 1L << (local % 32)
                  true
                case _ => false
              }
              if (!valid)
                Left(Fault.InvalidTask(
                  "OpGroupNonUniformBallot participants do not all provide scalar boolean predicates"
                ))
              else Right(Vec.fill(participants.size)(RuntimeValue.Bits(resultType, ballot)))
            case CollectiveKind.Barrier =>
              Left(Fault.InvalidTask("barrier rendezvous unexpectedly carried a value"))
          }
          results match {
            case Left(error)   => Left(error)
            case Right(values) =>
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
              Right(())
          }
        }
    }
  }

  private def collectiveBallot(
      program: Program,
      values: InvocationValues,
      function: Int,
      pc: Int,
      resultType: Operand,
      destination: Operand,
      executionScope: Operand,
      pred: Operand
  ): Either[Fault, CollectiveRequest] =
    (scalar(values, executionScope), values.get(id(pred))) match {
      case (Right(3L), Some(value @ RuntimeValue.Bits(predType, Array(_))))
          if integerShape(program, id(resultType)).contains(BitWidth(32) -> 4) &&
            booleanLanes(program, predType).contains(1) =>
        Right(
          CollectiveRequest(
            CollectiveKind.Ballot,
            function,
            pc,
            3,
            CollectivePayload.Value(id(resultType), id(destination), value)
          )
        )
      case (Right(scope), _) if scope != 3L =>
        Left(Fault.Unsupported(
          s"OpGroupNonUniformBallot execution scope $scope is not Subgroup"
        ))
      case (Right(_), Some(_)) =>
        Left(Fault.InvalidTask(
          "OpGroupNonUniformBallot requires a four-lane 32-bit integer result and scalar boolean predicate"
        ))
      case (Left(error), _) => Left(error)
      case (_, None)        => Left(undefined(pred))
    }

  private def controlBarrier(
      values: InvocationValues,
      function: Int,
      pc: Int,
      executionScope: Operand,
      memoryScope: Operand,
      semantics: Operand
  ): Either[Fault, CollectiveRequest] =
    (scalar(values, executionScope), scalar(values, memoryScope), scalar(values, semantics)) match {
      case (Right(executionScope @ (2L | 3L)), Right(memoryScope @ (2L | 3L)), Right(memorySemantics))
          if (memorySemantics & ~0xffdeL) == 0L &&
            Vec(0L, 2L, 4L, 8L, 16L).contains(memorySemantics & 0x1eL) =>
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
        Left(Fault.Unsupported(
          s"OpControlBarrier execution scope $scope is not Subgroup or Workgroup"
        ))
      case (Right(_), Right(scope), _) if scope != 2L && scope != 3L =>
        Left(Fault.Unsupported(
          s"OpControlBarrier memory scope $scope is not Subgroup or Workgroup"
        ))
      case (Right(_), Right(_), Right(memorySemantics)) =>
        Left(
          Fault.InvalidTask(
            f"OpControlBarrier memory semantics 0x$memorySemantics%x are invalid or unsupported"
          )
        )
      case (_, _, Left(error)) => Left(error)
      case (Left(error), _, _) => Left(error)
      case (_, Left(error), _) => Left(error)
    }

  // Group copies advance byte ranges directly rather than constructing intermediate slices.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def groupAsyncCopy(
      program: Program,
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
  ): Either[Fault, Unit] =
    for {
      scope       <- scalar(values, executionScope)
      destination <- address(values, destinationId)
      source      <- address(values, sourceId)
      count       <- scalar(values, numElements)
      strideValue <- scalar(values, stride)
      _           <- Either.cond(
        scope == 2L,
        (),
        Fault.Unsupported(s"OpGroupAsyncCopy execution scope $scope is not Workgroup")
      )
      _ <- Either.cond(
        count >= 0L && strideValue > 0L,
        (),
        Fault.InvalidTask(
          s"OpGroupAsyncCopy has invalid count/stride $count/$strideValue"
        )
      )
      _ <- Either.cond(
        (destination.storageClass == 4 && source.storageClass == 5) ||
          (destination.storageClass == 5 && source.storageClass == 4),
        (),
        Fault.InvalidTask(
          s"OpGroupAsyncCopy requires Workgroup/CrossWorkgroup pointers, got ${destination.storageClass}/${source.storageClass}"
        )
      )
      _ <- {
        val elementSize = typeSize(program, destination.pointee)
        if (elementSize <= 0)
          Left(Fault.InvalidTask("OpGroupAsyncCopy element type has no data size"))
        else if (invocation.localX != 0L || invocation.localY != 0L || invocation.localZ != 0L) {
          values.update(id(resultDestination), RuntimeValue.Bits(id(resultType), Array(0L)))
          Right(())
        } else {
          try {
            val strideBytes = Math.multiplyExact(strideValue, elementSize.toLong)
            val packedBytes = Math.toIntExact(Math.multiplyExact(count, elementSize.toLong))
            val sourceBytes =
              if (destination.storageClass == 4) {
                val sourceSpan =
                  if (count == 0L) 0
                  else Math.toIntExact(Math.addExact(Math.multiplyExact(count - 1L, strideBytes), elementSize.toLong))
                memory.read(source, sourceSpan.refineUnsafe[Positive0]).map { strided =>
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
              } else memory.read(source, packedBytes.refineUnsafe[Positive0])
            sourceBytes.flatMap { packed =>
              if (destination.storageClass == 5 && strideValue != 1L) {
                @tailrec
                def writeElement(index: Long): Either[Fault, Unit] =
                  if (index == count) Right(())
                  else {
                    val target = destination.copy(offset =
                      Math.addExact(destination.offset, Math.multiplyExact(index, strideBytes))
                    )
                    memory
                      .write(
                        target,
                        packed.slice(
                          Math.toIntExact(Math.multiplyExact(index, elementSize.toLong)),
                          Math.toIntExact(Math.multiplyExact(index + 1L, elementSize.toLong))
                        )
                      ) match {
                      case failure @ Left(_) => failure
                      case Right(())         => writeElement(index + 1L)
                    }
                  }
                writeElement(0L)
              } else memory.write(destination, packed)
            }.map { _ =>
              values.update(id(resultDestination), RuntimeValue.Bits(id(resultType), Array(0L)))
            }
          } catch {
            case _: ArithmeticException | _: IllegalArgumentException =>
              Left(Fault.InvalidTask("OpGroupAsyncCopy byte range is too large"))
          }
        }
      }
    } yield ()

  private def collectiveAny(
      program: Program,
      values: InvocationValues,
      function: Int,
      pc: Int,
      resultType: Operand,
      destination: Operand,
      executionScope: Operand,
      pred: Operand
  ): Either[Fault, CollectiveRequest] = collectiveBoolean(
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
      program: Program,
      values: InvocationValues,
      function: Int,
      pc: Int,
      resultType: Operand,
      destination: Operand,
      executionScope: Operand,
      pred: Operand,
      kind: CollectiveKind,
      opcode: String
  ): Either[Fault, CollectiveRequest] =
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
        Left(Fault.Unsupported(
          s"$opcode execution scope $scope is not Subgroup or Workgroup"
        ))
      case (Right(_), Some(_)) =>
        Left(Fault.InvalidTask(
          s"$opcode requires scalar boolean predicate and result types"
        ))
      case (Left(error), _) => Left(error)
      case (_, None)        => Left(undefined(pred))
    }

  private def collectiveIntegerExtremum(
      program: Program,
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
  ): Either[Fault, CollectiveRequest] =
    (scalar(values, executionScope), values.get(id(source))) match {
      case (Right(scope @ (2L | 3L)), Some(value @ RuntimeValue.Bits(sourceType, lanes)))
          if Vec(0L, 1L, 2L).contains(unsigned(operation)) &&
            integerShape(program, id(resultType)) == integerShape(program, sourceType) &&
            integerShape(program, sourceType).exists(_._2 == lanes.length) =>
        Right(
          CollectiveRequest(
            kind,
            function,
            pc,
            scope.toInt,
            CollectivePayload.Value(id(resultType), id(destination), value),
            id(operation)
          )
        )
      case (Right(_), _) if !Vec(0L, 1L, 2L).contains(unsigned(operation)) =>
        Left(
          Fault.Unsupported(
            s"$opcode group operation ${unsigned(operation)} is not Reduce, InclusiveScan, or ExclusiveScan"
          )
        )
      case (Right(scope), _) if scope != 2L && scope != 3L =>
        Left(Fault.Unsupported(
          s"$opcode execution scope $scope is not Subgroup or Workgroup"
        ))
      case (Right(_), Some(_)) =>
        Left(Fault.InvalidTask(
          s"$opcode requires matching integer scalar or vector types"
        ))
      case (Left(error), _) => Left(error)
      case (_, None)        => Left(undefined(source))
    }

  private def collectiveIntegerAdd(
      program: Program,
      values: InvocationValues,
      function: Int,
      pc: Int,
      resultType: Operand,
      destination: Operand,
      executionScope: Operand,
      operation: Operand,
      source: Operand
  ): Either[Fault, CollectiveRequest] =
    (scalar(values, executionScope), values.get(id(source))) match {
      case (Right(scope @ (2L | 3L)), Some(value @ RuntimeValue.Bits(sourceType, lanes)))
          if Vec(0L, 1L, 2L).contains(unsigned(operation)) &&
            integerShape(program, id(resultType)) == integerShape(program, sourceType) &&
            integerShape(program, sourceType).exists(_._2 == lanes.length) =>
        Right(
          CollectiveRequest(
            CollectiveKind.IntegerAdd,
            function,
            pc,
            scope.toInt,
            CollectivePayload.Value(id(resultType), id(destination), value),
            id(operation)
          )
        )
      case (Right(_), _) if !Vec(0L, 1L, 2L).contains(unsigned(operation)) =>
        Left(
          Fault.Unsupported(
            s"OpGroupIAdd group operation ${unsigned(operation)} is not Reduce, InclusiveScan, or ExclusiveScan"
          )
        )
      case (Right(scope), _) if scope != 2L && scope != 3L =>
        Left(Fault.Unsupported(
          s"OpGroupIAdd execution scope $scope is not Subgroup or Workgroup"
        ))
      case (Right(_), Some(_)) =>
        Left(Fault.InvalidTask(
          "OpGroupIAdd requires matching integer scalar or vector types"
        ))
      case (Left(error), _) => Left(error)
      case (_, None)        => Left(undefined(source))
    }

  private def collectiveIntegerMultiply(
      program: Program,
      values: InvocationValues,
      function: Int,
      pc: Int,
      resultType: Operand,
      destination: Operand,
      executionScope: Operand,
      operation: Operand,
      source: Operand
  ): Either[Fault, CollectiveRequest] =
    (scalar(values, executionScope), values.get(id(source))) match {
      case (Right(scope @ (2L | 3L)), Some(value @ RuntimeValue.Bits(sourceType, lanes)))
          if Vec(0L, 1L, 2L).contains(unsigned(operation)) &&
            integerShape(program, id(resultType)) == integerShape(program, sourceType) &&
            integerShape(program, sourceType).exists(_._2 == lanes.length) =>
        Right(
          CollectiveRequest(
            CollectiveKind.IntegerMultiply,
            function,
            pc,
            scope.toInt,
            CollectivePayload.Value(id(resultType), id(destination), value),
            id(operation)
          )
        )
      case (Right(_), _) if !Vec(0L, 1L, 2L).contains(unsigned(operation)) =>
        Left(
          Fault.Unsupported(
            s"OpGroupIMulKHR group operation ${unsigned(operation)} is not Reduce, InclusiveScan, or ExclusiveScan"
          )
        )
      case (Right(scope), _) if scope != 2L && scope != 3L =>
        Left(Fault.Unsupported(
          s"OpGroupIMulKHR execution scope $scope is not Subgroup or Workgroup"
        ))
      case (Right(_), Some(_)) =>
        Left(Fault.InvalidTask(
          "OpGroupIMulKHR requires matching integer scalar or vector types"
        ))
      case (Left(error), _) => Left(error)
      case (_, None)        => Left(undefined(source))
    }

  private def collectiveFloatingAdd(
      program: Program,
      values: InvocationValues,
      function: Int,
      pc: Int,
      resultType: Operand,
      destination: Operand,
      executionScope: Operand,
      operation: Operand,
      source: Operand
  ): Either[Fault, CollectiveRequest] =
    (scalar(values, executionScope), values.get(id(source))) match {
      case (Right(scope @ (2L | 3L)), Some(value @ RuntimeValue.Bits(sourceType, lanes)))
          if Vec(0L, 1L, 2L).contains(unsigned(operation)) &&
            floatingShape(program, id(resultType)) == floatingShape(program, sourceType) &&
            floatingShape(program, sourceType).exists(_._2 == lanes.length) =>
        Right(
          CollectiveRequest(
            CollectiveKind.FloatingAdd,
            function,
            pc,
            scope.toInt,
            CollectivePayload.Value(id(resultType), id(destination), value),
            id(operation)
          )
        )
      case (Right(_), _) if !Vec(0L, 1L, 2L).contains(unsigned(operation)) =>
        Left(
          Fault.Unsupported(
            s"OpGroupFAdd group operation ${unsigned(operation)} is not Reduce, InclusiveScan, or ExclusiveScan"
          )
        )
      case (Right(scope), _) if scope != 2L && scope != 3L =>
        Left(Fault.Unsupported(
          s"OpGroupFAdd execution scope $scope is not Subgroup or Workgroup"
        ))
      case (Right(_), Some(_)) =>
        Left(Fault.InvalidTask(
          "OpGroupFAdd requires matching floating scalar or vector types"
        ))
      case (Left(error), _) => Left(error)
      case (_, None)        => Left(undefined(source))
    }

  private def collectiveFloatingMultiply(
      program: Program,
      values: InvocationValues,
      function: Int,
      pc: Int,
      resultType: Operand,
      destination: Operand,
      executionScope: Operand,
      operation: Operand,
      source: Operand
  ): Either[Fault, CollectiveRequest] =
    (scalar(values, executionScope), values.get(id(source))) match {
      case (Right(scope @ (2L | 3L)), Some(value @ RuntimeValue.Bits(sourceType, lanes)))
          if Vec(0L, 1L, 2L).contains(unsigned(operation)) &&
            floatingShape(program, id(resultType)) == floatingShape(program, sourceType) &&
            floatingShape(program, sourceType).exists(_._2 == lanes.length) =>
        Right(
          CollectiveRequest(
            CollectiveKind.FloatingMultiply,
            function,
            pc,
            scope.toInt,
            CollectivePayload.Value(id(resultType), id(destination), value),
            id(operation)
          )
        )
      case (Right(_), _) if !Vec(0L, 1L, 2L).contains(unsigned(operation)) =>
        Left(
          Fault.Unsupported(
            s"OpGroupFMulKHR group operation ${unsigned(operation)} is not Reduce, InclusiveScan, or ExclusiveScan"
          )
        )
      case (Right(scope), _) if scope != 2L && scope != 3L =>
        Left(Fault.Unsupported(
          s"OpGroupFMulKHR execution scope $scope is not Subgroup or Workgroup"
        ))
      case (Right(_), Some(_)) =>
        Left(Fault.InvalidTask(
          "OpGroupFMulKHR requires matching floating scalar or vector types"
        ))
      case (Left(error), _) => Left(error)
      case (_, None)        => Left(undefined(source))
    }

  private def collectiveFloatingExtremum(
      program: Program,
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
  ): Either[Fault, CollectiveRequest] =
    (scalar(values, executionScope), values.get(id(source))) match {
      case (Right(scope @ (2L | 3L)), Some(value @ RuntimeValue.Bits(sourceType, lanes)))
          if Vec(0L, 1L, 2L).contains(unsigned(operation)) &&
            floatingShape(program, id(resultType)) == floatingShape(program, sourceType) &&
            floatingShape(program, sourceType).exists(_._2 == lanes.length) =>
        Right(
          CollectiveRequest(
            kind,
            function,
            pc,
            scope.toInt,
            CollectivePayload.Value(id(resultType), id(destination), value),
            id(operation)
          )
        )
      case (Right(_), _) if !Vec(0L, 1L, 2L).contains(unsigned(operation)) =>
        Left(
          Fault.Unsupported(
            s"$opcode group operation ${unsigned(operation)} is not Reduce, InclusiveScan, or ExclusiveScan"
          )
        )
      case (Right(scope), _) if scope != 2L && scope != 3L =>
        Left(Fault.Unsupported(
          s"$opcode execution scope $scope is not Subgroup or Workgroup"
        ))
      case (Right(_), Some(_)) =>
        Left(Fault.InvalidTask(
          s"$opcode requires matching floating scalar or vector types"
        ))
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
  ): Either[Fault, CollectiveRequest] =
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
        Left(Fault.InvalidTask("OpSubgroupShuffleUpINTEL delta must be non-negative"))
      case (Some(_), Some(_), Right(_)) =>
        Left(Fault.InvalidTask(
          "OpSubgroupShuffleUpINTEL requires matching result and operand types"
        ))
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
  ): Either[Fault, CollectiveRequest] =
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
        Left(Fault.InvalidTask("OpSubgroupShuffleINTEL invocation id must be scalar"))
      case (Some(_), Some(_)) =>
        Left(Fault.InvalidTask(
          "OpSubgroupShuffleINTEL requires matching data and result types"
        ))
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
  ): Either[Fault, CollectiveRequest] =
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
        Left(Fault.InvalidTask("OpSubgroupShuffleDownINTEL delta must be non-negative"))
      case (Some(_), Some(_), Right(_)) =>
        Left(Fault.InvalidTask(
          "OpSubgroupShuffleDownINTEL requires matching result and operand types"
        ))
      case (_, _, Left(error)) => Left(error)
      case (None, _, _)        => Left(undefined(current))
      case (_, None, _)        => Left(undefined(next))
    }

  private def collectiveBroadcast(
      program: Program,
      values: InvocationValues,
      function: Int,
      pc: Int,
      resultType: Operand,
      destination: Operand,
      executionScope: Operand,
      source: Operand,
      localId: Operand
  ): Either[Fault, CollectiveRequest] =
    (scalar(values, executionScope), values.get(id(source)), values.get(id(localId))) match {
      case (
            Right(scope @ (2L | 3L)),
            Some(value @ RuntimeValue.Bits(sourceType, _)),
            Some(RuntimeValue.Bits(localIdType, lanes))
          )
          if sourceType == id(resultType) && integerShape(program, localIdType).exists { case (_, count) =>
            count == lanes.length && Vec(1, 3).contains(count)
          } =>
        val width = integerShape(program, localIdType).map(_._1).getOrElse(BitWidth(64))
        Right(
          CollectiveRequest(
            CollectiveKind.Broadcast,
            function,
            pc,
            scope.toInt,
            CollectivePayload.Value(id(resultType), id(destination), value),
            selector = Vec.from(lanes.map(_ & mask(width)))
          )
        )
      case (Right(scope), _, _) if scope != 2L && scope != 3L =>
        Left(Fault.Unsupported(
          s"OpGroupBroadcast execution scope $scope is not Subgroup or Workgroup"
        ))
      case (Right(_), None, _) => Left(undefined(source))
      case (Right(_), _, None) => Left(undefined(localId))
      case (Right(_), _, _)    =>
        Left(
          Fault.InvalidTask(
            "OpGroupBroadcast requires matching result/source bit types and a scalar or three-lane integer local id"
          )
        )
      case (Left(error), _, _) => Left(error)
    }

  // Aggregate argument pointer slots are invocation-owned relocation state.
  private def bindArgument(
      program: Program,
      parameterType: Int,
      byValue: Boolean,
      argument: KernelTask.Argument,
      shared: Option[Region],
      argumentIndex: Int,
      memory: MemoryState
  ): Either[Fault, RuntimeValue] =
    program.runtimeType(parameterType) match {
      case Some(RuntimeType.Pointer(storageClass, pointee)) => argument match {
          case KernelTask.Argument.Memory(allocation, offset) =>
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
          case KernelTask.Argument.Scalar(bytes)
              if !byValue && bytes.toArray.forall(_ == 0) &&
                !program.runtimeType(pointee).exists {
                  case RuntimeType.Structure(_) | RuntimeType.Array(_, _) => true
                  case _                                                  => false
                } =>
            Right(RuntimeValue.Address(parameterType, Pointer(Region.Null, 0L, pointee, storageClass)))
          case KernelTask.Argument.Scalar(bytes)
              // A ByVal aggregate can contain a pointer in opaque byte-array storage at offset zero.
              // That relocation belongs inside the private copy, not to the parameter pointer itself.
              if !byValue && memory.argumentRelocation(argumentIndex, 0L).isDefined &&
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
              case None => Left(Fault.InvalidTask("missing SVM pointer relocation"))
            }
          case KernelTask.Argument.Scalar(bytes)
              if !byValue && bytes.size == 8 && (storageClass == 5 || storageClass == 8) =>
            memory.integerPointer(
              LittleEndian.decode(bytes, 0, MemoryValueByteWidth.Qword.scalarByteWidth),
              pointee,
              storageClass
            )
              .map(pointer => RuntimeValue.Address(parameterType, pointer))
          case KernelTask.Argument.Scalar(bytes) =>
            memory.relocateArgument(argumentIndex, bytes) match {
              case Left(error)      => Left(error)
              case Right(relocated) =>
                val size   = math.max(typeSize(program, pointee), relocated.length)
                val buffer = Array.ofDim[Byte](size)
                Array.copy(relocated, 0, buffer, 0, relocated.length)
                val slots = mutable.LongMap.empty[(Int, Pointer)]
                pointerLeafOffsets(program, pointee, 0L) match {
                  case Right(leaves) => leaves.foreach { case (offset, pointerType) =>
                      program.runtimeType(pointerType) match {
                        case Some(RuntimeType.Pointer(pointerStorage, pointerPointee)) =>
                          memory.argumentRelocation(argumentIndex, offset).foreach { (target, targetOffset) =>
                            val pointer = Pointer(Region.External(target), targetOffset, pointerPointee, pointerStorage)
                            slots.update(offset, pointerType -> pointer)
                          }
                        case _ => ()
                      }
                    }
                  case Left(_) => ()
                }
                Right(RuntimeValue.Address(
                  parameterType,
                  Pointer(
                    Region.Internal(InternalMemory(buffer)),
                    0L,
                    pointee,
                    storageClass,
                    Option.when(slots.nonEmpty)(slots)
                  )
                ))
            }
          case KernelTask.Argument.Local(size) =>
            val region = shared.getOrElse(Region.Internal(InternalMemory(Array.ofDim[Byte](checkedSize(size)))))
            Right(RuntimeValue.Address(parameterType, Pointer(region, 0L, pointee, storageClass)))
        }
      case Some(_) => argument match {
          case KernelTask.Argument.Scalar(bytes) =>
            memory.relocateArgument(argumentIndex, bytes).flatMap(decoded => decode(program, parameterType, decoded))
          case value => value match {
              case KernelTask.Argument.Local(size) =>
                // Some DPC++ Level Zero kernels lower a local pointer parameter to a
                // 64-bit physical address and convert it back with OpConvertUToPtr.
                // Register the per-workgroup region in the same virtual address map
                // used by ordinary pointer conversions.
                val region = shared.getOrElse(Region.Internal(InternalMemory(Array.ofDim[Byte](checkedSize(size)))))
                memory.pointerInteger(Pointer(region, 0L, 0, 4)).map(address =>
                  RuntimeValue.Bits(parameterType, Array(address))
                )
              case other => Left(
                  Fault.InvalidTask(s"scalar SPIR-V parameter cannot bind $other")
                )
            }
        }
      case None => Left(
          Fault.InvalidTask(s"SPIR-V parameter type %$parameterType is undefined")
        )
    }

  // External aggregate pointer slots are invocation-owned relocation state.
  private def bindExternalAggregatePointerArgument(
      program: Program,
      parameterType: Int,
      storageClass: Int,
      pointee: Int,
      allocation: MemoryRegion,
      allocationOffset: Long,
      memory: MemoryState
  ): Either[Fault, RuntimeValue] =
    pointerLeafOffsets(program, pointee, 0L) match {
      case Left(error)   => Left(error)
      case Right(leaves) =>
        val slots = mutable.LongMap.empty[(Int, Pointer)]
        leaves.foreach { case (slotOffset, pointerType) =>
          program.runtimeType(pointerType) match {
            case Some(RuntimeType.Pointer(pointerStorage, pointerPointee)) =>
              // Missing relocation metadata says nothing about the stored pointer.
              // Leave the slot absent so loadPointer reads the registered native bytes.
              memory.allocationRelocation(allocation, allocationOffset + slotOffset).foreach { (target, targetOffset) =>
                val pointer = Pointer(Region.External(target), targetOffset, pointerPointee, pointerStorage)
                slots.update(allocationOffset + slotOffset, pointerType -> pointer)
              }
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
  ): Either[Fault, Vec[(Long, Int)]] =
    VMCompiler.compileUnchecked(module)
      .map(VMLoadedProgram(_))
      .flatMap(program => pointerLeafOffsets(program, typeId, 0L))

  private def pointerLeafOffsets(
      program: Program,
      typeId: Int,
      baseOffset: Long
  ): Either[Fault, Vec[(Long, Int)]] = program.runtimeType(typeId) match {
    case Some(RuntimeType.Pointer(_, _))      => Right(Vec(baseOffset -> typeId))
    case Some(RuntimeType.Structure(members)) =>
      members.zipWithIndex.foldLeft[Either[Fault, Vec[(Long, Int)]]](Right(Vec.empty)) {
        case (result, (member, index)) =>
          result.flatMap(existing =>
            pointerLeafOffsets(program, member, baseOffset + structMemberOffset(program, typeId, members, index)).map(
              existing ++ _
            )
          )
      }
    case Some(RuntimeType.Array(element, count)) =>
      if (count < 0L || count > Int.MaxValue.toLong)
        Left(Fault.InvalidTask(
          s"SPIR-V aggregate pointer array type %$typeId has invalid size $count"
        ))
      else
        (0 until count.toInt).foldLeft[Either[Fault, Vec[(Long, Int)]]](Right(Vec.empty)) {
          case (result, index) =>
            result.flatMap { existing =>
              val stride = program.arrayStrides.getOrElse(typeId, typeSize(program, element))
              pointerLeafOffsets(program, element, baseOffset + index.toLong * stride).map(existing ++ _)
            }
        }
    case Some(_) => Right(Vec.empty)
    case None    =>
      Left(Fault.InvalidTask(s"SPIR-V aggregate member type %$typeId is undefined"))
  }

  private def loadPointer(
      program: Program,
      memory: MemoryState,
      source: Pointer,
      resultType: Int
  ): Either[Fault, RuntimeValue] = program.runtimeType(resultType) match {
    case Some(RuntimeType.Pointer(resultStorage, resultPointee)) =>
      val stored = if (memory.nativePointerStorage(source)) None
      else source.slots.flatMap(_.get(source.offset)).orElse(
        memory.relocatedPointer(source, resultPointee, resultStorage).map(resultType -> _)
      )
      val resolved = stored match {
        case Some(pointer) => Right(pointer)
        case None          => memory.readScalar(source, MemoryValueByteWidth.Qword)
            .flatMap(raw => memory.integerPointer(raw, resultPointee, resultStorage))
            .map(resultType -> _)
      }
      resolved.flatMap { case (slotType, pointer) =>
        program.runtimeType(slotType) match {
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
              Fault.InvalidTask(
                s"SPIR-V pointer slot at byte ${source.offset} has type %$slotType ($value), " +
                  s"expected %$resultType (${program.runtimeType(resultType)}), " +
                  s"base pointee %${source.pointee}, storage ${source.storageClass}, " +
                  s"slots ${source.slots.map(slots => Vec.from(slots.keys).sorted.mkString("[", ",", "]")).getOrElse("none")}"
              )
            )
        }

      }
    case value =>
      Left(Fault.InvalidTask(
        s"SPIR-V pointer load result type %$resultType is invalid: $value"
      ))
  }

  private def storePointer(
      program: Program,
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
            source.pointee == sourcePointee &&
            (source.region == Region.Null || sourceStorage == 8 || source.storageClass == sourceStorage) =>
        (target.region, source.region) match {
          case (Region.External(_), Region.Internal(_)) =>
            Left(Fault.Unsupported("SPIR-V cannot persist a pointer to dispatch-local storage"))
          case _ =>
            val stored = for {
              address <- memory.pointerInteger(source)
              _       <- memory.writeScalar(target, address, MemoryValueByteWidth.Qword)
              _       <- memory.recordPointerRelocation(source, target)
            } yield ()
            stored match {
              case Left(error) => Left(error)
              case Right(())   =>
                target.slots.foreach(_.update(target.offset, sourceType -> source))
                Right(())
            }
        }
      case (targetType, sourceValue) =>
        Left(
          Fault.InvalidTask(
            s"SPIR-V pointer store type mismatch: target pointee %${target.pointee} is $targetType, " +
              s"source %$sourceType is $sourceValue"
          )
        )
    }

  // Global initialization exits before publishing a partially materialized image.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return", "scalafix:DisableSyntax.var"))
  private def materializeGlobals(
      program: Program,
      includeStorageClass: Int => Boolean,
      useBindings: Boolean = true
  ): Either[Fault, Map[Int, RuntimeValue]] = {
    val values      = Map.newBuilder[Int, RuntimeValue]
    val globals     = program.globals.view.toVector
    var globalIndex = 0
    while (globalIndex < globals.size) {
      val (variable, global) = globals(globalIndex)
      if (!program.builtins.contains(variable) && includeStorageClass(global.storageClass)) {
        val pointee = pointerPointee(program, global.pointerType)
        val bytes   = global.initializer match {
          case None              => Array.ofDim[Byte](typeSize(program, pointee))
          case Some(initializer) => program.constants.get(initializer) match {
              case None =>
                return Left(
                  Fault.InvalidTask(
                    s"SPIR-V global variable %$variable has undefined initializer %$initializer"
                  )
                )
              case Some(value) =>
                val encoded = encode(program, value) match {
                  case Left(error) => return Left(error)
                  case Right(data) => data
                }
                if (encoded.length != typeSize(program, pointee))
                  return Left(
                    Fault.InvalidTask(
                      s"SPIR-V global variable %$variable initializer has ${encoded.length} bytes, " +
                        s"expected ${typeSize(program, pointee)}"
                    )
                  )
                encoded
            }
        }
        val pointer = (if (useBindings) program.boundGlobals.get(variable) else None) match {
          case Some(binding) => Pointer(Region.External(binding.region), binding.offset, pointee, global.storageClass)
          case None          => Pointer(Region.Internal(InternalMemory(bytes)), 0L, pointee, global.storageClass)
        }
        values += variable -> RuntimeValue.Address(global.pointerType, pointer)
      }
      globalIndex += 1
    }
    Right(values.result())
  }

  // Access-chain layout walks nested types with a cursor and fails at the first invalid index.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return", "scalafix:DisableSyntax.var"))
  private def accessChain(
      program: Program,
      values: InvocationValues,
      memory: MemoryState,
      resultType: Operand,
      destination: Operand,
      base: Operand,
      element: Option[Operand],
      indexes: Vec[Operand]
  ): Either[Fault, Unit] = {
    val source = address(values, base) match {
      case Left(error)  => return Left(error)
      case Right(value) => value
    }
    val initial = element match {
      case Some(value) =>
        scalar(values, value).map(index =>
          (memory.elementOffset(source, index, typeSize(program, source.pointee)), source.pointee)
        )
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
      program.runtimeType(typeId) match {
        case Some(RuntimeType.Structure(members)) =>
          if (index < 0L || index >= members.size.toLong)
            return Left(
              Fault.InvalidTask(s"structure member index $index is out of range")
            )
          offset += structMemberOffset(program, typeId, members, index.toInt)
          typeId = members(index.toInt)
        case Some(RuntimeType.Array(member, _)) =>
          offset += index * program.arrayStrides.getOrElse(typeId, typeSize(program, member))
          typeId = member
        case Some(RuntimeType.VectorType(component, _)) =>
          offset += index * typeSize(program, component)
          typeId = component
        case Some(value) =>
          return Left(Fault.InvalidTask(s"cannot index through SPIR-V type $value"))
        case None =>
          return Left(Fault.InvalidTask(s"SPIR-V type %$typeId is undefined"))
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
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand
  ): Either[Fault, Unit] = {
    val outputType = id(resultType)
    (program.runtimeType(outputType), values.get(id(source))) match {
      case (
            Some(RuntimeType.Pointer(targetStorage, targetPointee)),
            Some(RuntimeValue.Address(sourceType, pointer))
          ) if Vec(4, 5, 7).contains(targetStorage) =>
        program.runtimeType(sourceType) match {
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
              Fault.InvalidTask(
                s"OpGenericCastToPtr cannot cast storage class $sourceStorage pointee %$sourcePointee " +
                  s"to storage class $targetStorage pointee %$targetPointee"
              )
            )
          case _ => Left(Fault.InvalidTask("OpGenericCastToPtr source type is not a pointer"))
        }
      case (Some(RuntimeType.Pointer(targetStorage, _)), _) =>
        Left(
          Fault.InvalidTask(
            s"OpGenericCastToPtr result pointer has invalid storage class $targetStorage"
          )
        )
      case (None, _) =>
        Left(Fault.InvalidTask(
          s"OpGenericCastToPtr result type %$outputType is undefined"
        ))
      case (_, None) => Left(undefined(source))
      case _         => Left(Fault.InvalidTask("OpGenericCastToPtr source is not a pointer"))
    }
  }

  private def genericCastToPointerExplicit(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand,
      storage: Operand
  ): Either[Fault, Unit] = {
    val outputType    = id(resultType)
    val targetStorage = id(storage)
    (program.runtimeType(outputType), values.get(id(source))) match {
      case (
            Some(RuntimeType.Pointer(resultStorage, targetPointee)),
            Some(RuntimeValue.Address(sourceType, pointer))
          ) if resultStorage == targetStorage && Vec(4, 5, 7).contains(targetStorage) =>
        program.runtimeType(sourceType) match {
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
              Fault.InvalidTask(
                s"OpGenericCastToPtrExplicit cannot cast storage class $sourceStorage pointee %$sourcePointee"
              )
            )
          case _ => Left(Fault.InvalidTask("OpGenericCastToPtrExplicit source is not a pointer"))
        }
      case (Some(RuntimeType.Pointer(resultStorage, _)), _)
          if resultStorage != targetStorage || !Vec(4, 5, 7).contains(targetStorage) =>
        Left(
          Fault.InvalidTask(
            "OpGenericCastToPtrExplicit requires Workgroup, CrossWorkgroup, or Function storage matching result type; " +
              s"got storage $targetStorage and result storage $resultStorage"
          )
        )
      case (Some(RuntimeType.Pointer(_, _)), None) => Left(undefined(source))
      case (None, _)                               =>
        Left(Fault.InvalidTask(
          s"OpGenericCastToPtrExplicit result type %$outputType is undefined"
        ))
      case _ => Left(Fault.InvalidTask("OpGenericCastToPtrExplicit source is not a pointer"))
    }
  }

  // Composite layout walks nested members with a cursor and fails on invalid type structure.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return", "scalafix:DisableSyntax.var"))
  private def compositeLeafRange(
      program: Program,
      typeId: Int,
      indexes: Vec[Operand]
  ): Either[Fault, (Int, Int)] = {
    var current = typeId
    var offset  = 0
    var index   = 0
    while (index < indexes.size) {
      val selected = unsigned(indexes(index))
      program.runtimeType(current) match {
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
            Fault.InvalidTask(
              s"OpComposite index $selected is invalid for type %$current ($value)"
            )
          )
        case None =>
          return Left(Fault.InvalidTask(s"OpComposite type %$current is undefined"))
      }
      index += 1
    }
    scalarLeafCount(program, current).map(leaves => offset -> leaves)
  }

  private def compositeExtract(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      composite: Operand,
      indexes: Vec[Operand]
  ): Either[Fault, Unit] = values.get(id(composite)) match {
    case Some(RuntimeValue.Bits(compositeType, lanes)) =>
      compositeLeafRange(program, compositeType, indexes) match {
        case Right((offset, count)) if offset >= 0 && offset + count <= lanes.length =>
          values.update(id(destination), RuntimeValue.Bits(id(resultType), lanes.slice(offset, offset + count)))
          Right(())
        case Right((offset, count)) =>
          Left(
            Fault.InvalidTask(
              s"OpCompositeExtract range $offset+$count exceeds ${lanes.length} scalar leaves"
            )
          )
        case Left(error) => Left(error)
      }
    case Some(_) => Left(Fault.Unsupported("composite extraction requires scalar data"))
    case None    => Left(undefined(composite))
  }

  private def compositeInsert(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand,
      composite: Operand,
      indexes: Vec[Operand]
  ): Either[Fault, Unit] =
    (values.get(id(source)), values.get(id(composite))) match {
      case (Some(RuntimeValue.Bits(_, sourceLanes)), Some(RuntimeValue.Bits(compositeType, compositeLanes))) =>
        compositeLeafRange(program, compositeType, indexes) match {
          case Right((offset, count)) if sourceLanes.length == count && offset + count <= compositeLanes.length =>
            val result = compositeLanes.clone()
            Array.copy(sourceLanes, 0, result, offset, count)
            values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
            Right(())
          case Right((offset, count)) =>
            Left(
              Fault.InvalidTask(
                s"OpCompositeInsert source has ${sourceLanes.length} leaves, expected $count at offset $offset"
              )
            )
          case Left(error) => Left(error)
        }
      case (None, _) => Left(undefined(source))
      case (_, None) => Left(undefined(composite))
      case _         => Left(Fault.Unsupported("composite insertion requires scalar data"))
    }

  private def nextBinary16(bits: Binary16, upward: Boolean): Binary16 = {
    val value  = bits.value.toInt
    val result = if ((value & 0x7fff) > 0x7c00) value.toLong
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
    Binary16.low(result)
  }

  private def doubleToBinary16(value: Double, mode: Int): Binary16 = {
    val approximate = IEEE754.doubleToBinary16(value)
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
            else if ((lower.value & 1L) == 0L) lower else upper
        }
      }
    }
  }

  // Vector half loads are a lane-wise hot loop with fail-fast memory access.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return", "scalafix:DisableSyntax.var"))
  private def openCLHalfLoad(
      program: Program,
      values: InvocationValues,
      memory: MemoryState,
      resultType: Operand,
      destination: Operand,
      sources: Vec[Operand],
      aligned: Boolean
  ): Either[Fault, Unit] = {
    val vector = sources.size == 3
    if ((!vector && sources.size != 2) || (vector && sources.size != 3))
      return Left(Fault.InvalidTask("OpenCL.std half load has an invalid operand count"))
    (floatingShape(program, id(resultType)), scalar(values, sources(0)), address(values, sources(1))) match {
      case (Some((width, count)), Right(offset), Right(pointer))
          if width.value == 32 && (!vector || unsigned(sources(2)) == count.toLong) =>
        val stride = if (aligned && count == 3) 4 else count
        val base   = Math.addExact(pointer.offset, Math.multiplyExact(offset, stride.toLong * 2L))
        val result = Array.ofDim[Long](count)
        var lane   = 0
        while (lane < count) {
          memory.readScalar(
            pointer.copy(offset = Math.addExact(base, lane.toLong * 2L)),
            MemoryValueByteWidth.Word
          ) match {
            case Left(error) => return Left(error)
            case Right(half) =>
              result(lane) = IEEE754.floatToBinary32(IEEE754.binary16ToFloat(Binary16.low(half))).value
          }
          lane += 1
        }
        values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
        Right(())
      case (Some(_), Left(error), _) => Left(error)
      case (Some(_), _, Left(error)) => Left(error)
      case _ => Left(Fault.InvalidTask("OpenCL.std half load requires a float result and half pointer"))
    }
  }

  // Vector half stores are a lane-wise hot loop with fail-fast memory access.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return", "scalafix:DisableSyntax.var"))
  private def openCLHalfStore(
      program: Program,
      values: InvocationValues,
      memory: MemoryState,
      sources: Vec[Operand],
      aligned: Boolean,
      rounded: Boolean
  ): Either[Fault, Unit] = {
    val expected = if (rounded) 4 else 3
    if (sources.size != expected)
      return Left(
        Fault.InvalidTask(s"OpenCL.std half store expects $expected operands")
      )
    (values.get(id(sources(0))), scalar(values, sources(1)), address(values, sources(2))) match {
      case (Some(RuntimeValue.Bits(sourceType, lanes)), Right(offset), Right(pointer)) =>
        floatingShape(program, sourceType) match {
          case Some((width, count)) if width.value == 32 || width.value == 64 =>
            val stride = if (aligned && count == 3) 4 else count
            val base   = Math.addExact(pointer.offset, Math.multiplyExact(offset, stride.toLong * 2L))
            val mode   = if (rounded) id(sources(3)) else 0
            var lane   = 0
            while (lane < count) {
              val source = floatingToDouble(width, lanes(lane))
              memory.writeScalar(
                pointer.copy(offset = Math.addExact(base, lane.toLong * 2L)),
                doubleToBinary16(source, mode).value,
                MemoryValueByteWidth.Word
              ) match {
                case Left(error) => return Left(error)
                case Right(())   => ()
              }
              lane += 1
            }
            Right(())
          case _ => Left(Fault.InvalidTask("OpenCL.std half store source is not float or double"))
        }
      case (None, _, _)              => Left(undefined(sources(0)))
      case (Some(_), Left(error), _) => Left(error)
      case (Some(_), _, Left(error)) => Left(error)
      case _                         =>
        Left(Fault.InvalidTask("OpenCL.std half store operands are not value types"))
    }
  }

  // Vector loads walk components directly and stop on the first memory fault.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return", "scalafix:DisableSyntax.var"))
  private def openCLVectorLoad(
      program: Program,
      values: InvocationValues,
      memory: MemoryState,
      resultType: Operand,
      destination: Operand,
      sources: Vec[Operand]
  ): Either[Fault, Unit] =
    if (sources.size != 3)
      Left(Fault.InvalidTask(
        s"OpenCL.std vloadn expects 3 operands, received ${sources.size}"
      ))
    else
      (program.runtimeType(id(resultType)), scalar(values, sources(0)), address(values, sources(1))) match {
        case (_, Left(error), _) => Left(error)
        case (_, _, Left(error)) => Left(error)
        case (Some(RuntimeType.VectorType(component, count)), Right(offset), Right(pointer))
            if unsigned(sources(2)) == count.toLong =>
          val elementSize = scalarMemoryWidth(program, component) match {
            case Left(error) => return Left(error)
            case Right(size) => size
          }
          val base = Math.addExact(
            pointer.offset,
            Math.multiplyExact(Math.multiplyExact(offset, count.toLong), elementSize.value.toLong)
          )
          val lanes = Array.ofDim[Long](count)
          var index = 0
          while (index < count) {
            memory.readScalar(
              pointer.copy(
                offset = Math.addExact(base, Math.multiplyExact(index.toLong, elementSize.value.toLong)),
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
            Fault.InvalidTask(
              s"OpenCL.std vloadn literal vector width ${unsigned(sources(2))} does not match $count"
            )
          )
        case (Some(_), _, _) =>
          Left(Fault.InvalidTask("OpenCL.std vloadn result type is not a vector"))
        case (None, _, _) =>
          Left(Fault.InvalidTask(
            s"OpenCL.std vloadn result type %${id(resultType)} is undefined"
          ))
      }

  // Vector stores walk components directly and stop on the first memory fault.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return", "scalafix:DisableSyntax.var"))
  private def openCLVectorStore(
      program: Program,
      values: InvocationValues,
      memory: MemoryState,
      source: Operand,
      sources: Vec[Operand]
  ): Either[Fault, Unit] =
    if (sources.size != 2)
      Left(Fault.InvalidTask(
        s"OpenCL.std vstoren expects 2 operands, received ${sources.size}"
      ))
    else
      (values.get(id(source)), scalar(values, sources(0)), address(values, sources(1))) match {
        case (Some(RuntimeValue.Bits(sourceType, lanes)), Right(offset), Right(pointer)) =>
          program.runtimeType(sourceType) match {
            case Some(RuntimeType.VectorType(component, count)) if lanes.length == count =>
              val elementSize = scalarMemoryWidth(program, component) match {
                case Left(error) => return Left(error)
                case Right(size) => size
              }
              val base = Math.addExact(
                pointer.offset,
                Math.multiplyExact(Math.multiplyExact(offset, count.toLong), elementSize.value.toLong)
              )
              var index = 0
              while (index < count) {
                memory.writeScalar(
                  pointer.copy(
                    offset = Math.addExact(base, Math.multiplyExact(index.toLong, elementSize.value.toLong)),
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
              Left(Fault.InvalidTask(
                s"OpenCL.std vstoren has ${lanes.length} lanes, expected $count"
              ))
            case _ => Left(Fault.InvalidTask("OpenCL.std vstoren source is not a vector"))
          }
        case (None, _, _)              => Left(undefined(source))
        case (Some(_), Left(error), _) => Left(error)
        case (Some(_), _, Left(error)) => Left(error)
        case (Some(_), _, _)           => Left(Fault.InvalidTask("OpenCL.std vstoren source is not numeric"))
      }

  private def openCLNextAfter(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      left: Operand,
      right: Operand
  ): Either[Fault, Unit] =
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
              width.value match {
                case 16 =>
                  IEEE754.floatToBinary16(
                    Math.nextAfter(
                      IEEE754.binary16ToFloat(Binary16.low(leftLanes(index))),
                      IEEE754.binary16ToFloat(Binary16.low(rightLanes(index)))
                    )
                  ).value
                case 32 =>
                  IEEE754.floatToBinary32(
                    Math.nextAfter(
                      IEEE754.binary32ToFloat(Binary32.low(leftLanes(index))),
                      IEEE754.binary32ToFloat(Binary32.low(rightLanes(index)))
                    )
                  ).value
                case 64 =>
                  IEEE754.doubleToBinary64(
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
              Fault.InvalidTask(
                "OpenCL.std nextafter requires matching floating-point scalar/vector operand and result types"
              )
            )
        }
      case (None, _) => Left(undefined(left))
      case (_, None) => Left(undefined(right))
      case _         => Left(Fault.InvalidTask("OpenCL.std nextafter operands are not floating-point"))
    }

  private def openCLSignedHalfAdd(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      left: Operand,
      right: Operand
  ): Either[Fault, Unit] =
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
              Fault.InvalidTask(
                "OpenCL.std hadd requires matching integer scalar/vector operand and result types"
              )
            )
        }
      case (None, _) => Left(undefined(left))
      case (_, None) => Left(undefined(right))
      case _         =>
        Left(Fault.InvalidTask("OpenCL.std hadd operands are not integer values"))
    }

  private def openCLSignedAbs(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand
  ): Either[Fault, Unit] = values.get(id(source)) match {
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
          Left(Fault.InvalidTask(
            "OpenCL.std s_abs requires matching integer scalar/vector types"
          ))
      }
    case Some(_) => Left(Fault.InvalidTask("OpenCL.std s_abs operand is a pointer"))
    case None    => Left(undefined(source))
  }

  private def openCLCountLeadingZeros(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand
  ): Either[Fault, Unit] = values.get(id(source)) match {
    case Some(RuntimeValue.Bits(sourceType, lanes)) =>
      (integerShape(program, sourceType), integerShape(program, id(resultType))) match {
        case (Some((sourceWidth, sourceLanes)), Some((resultWidth, resultLanes)))
            if sourceWidth == resultWidth && sourceLanes == resultLanes && lanes.length == sourceLanes =>
          val result = lanes.map { raw =>
            val value = raw & mask(sourceWidth)
            if (value == 0L)
              sourceWidth.value.toLong
            else
              (java.lang.Long.numberOfLeadingZeros(value) - (64 - sourceWidth.value)).toLong
          }
          values.update(id(destination), RuntimeValue.Bits(id(resultType), result.map(_ & mask(resultWidth))))
          Right(())
        case _ =>
          Left(Fault.InvalidTask(
            "OpenCL.std clz requires matching integer scalar/vector types"
          ))
      }
    case Some(_) => Left(Fault.InvalidTask("OpenCL.std clz operand is a pointer"))
    case None    => Left(undefined(source))
  }

  private def openCLCountTrailingZeros(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand
  ): Either[Fault, Unit] = values.get(id(source)) match {
    case Some(RuntimeValue.Bits(sourceType, lanes)) =>
      (integerShape(program, sourceType), integerShape(program, id(resultType))) match {
        case (Some((sourceWidth, sourceLanes)), Some((resultWidth, resultLanes)))
            if sourceWidth == resultWidth && sourceLanes == resultLanes && lanes.length == sourceLanes =>
          val result = lanes.map { raw =>
            val value = raw & mask(sourceWidth)
            if (value == 0L) sourceWidth.value.toLong else java.lang.Long.numberOfTrailingZeros(value).toLong
          }
          values.update(id(destination), RuntimeValue.Bits(id(resultType), result.map(_ & mask(resultWidth))))
          Right(())
        case _ =>
          Left(Fault.InvalidTask(
            "OpenCL.std ctz requires matching integer scalar/vector types"
          ))
      }
    case Some(_) => Left(Fault.InvalidTask("OpenCL.std ctz operand is a pointer"))
    case None    => Left(undefined(source))
  }

  private def openCLPopulationCount(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand
  ): Either[Fault, Unit] = values.get(id(source)) match {
    case Some(RuntimeValue.Bits(sourceType, lanes)) =>
      (integerShape(program, sourceType), integerShape(program, id(resultType))) match {
        case (Some((sourceWidth, sourceLanes)), Some((resultWidth, resultLanes)))
            if sourceWidth == resultWidth && sourceLanes == resultLanes && lanes.length == sourceLanes =>
          val result = lanes.map(raw => bitCount(raw, sourceWidth).toLong & mask(resultWidth))
          values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
          Right(())
        case _ =>
          Left(Fault.InvalidTask(
            "OpenCL.std popcount requires matching integer scalar/vector types"
          ))
      }
    case Some(_) => Left(Fault.InvalidTask("OpenCL.std popcount operand is a pointer"))
    case None    => Left(undefined(source))
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.return", "scalafix:DisableSyntax.var"))
  private def openCLPrintf(
      program: Program,
      values: InvocationValues,
      memory: MemoryState,
      resultType: Operand,
      destination: Operand,
      sources: Vec[Operand]
  ): Either[Fault, Unit] = {
    if (sources.isEmpty)
      return Left(Fault.InvalidTask("OpenCL.std printf requires a format string"))
    val format = values.get(id(sources.head)) match {
      case Some(RuntimeValue.Address(_, pointer)) => readCString(memory, pointer) match {
          case Left(error) => return Left(error)
          case Right(text) => text
        }
      case Some(_) => return Left(Fault.InvalidTask("OpenCL.std printf format is not a pointer"))
      case None    => return Left(undefined(sources.head))
    }
    val conversions = printfConversions(format) match {
      case Left(error)  => return Left(error)
      case Right(value) => value
    }
    if (conversions.size > sources.size - 1)
      return Left(
        Fault.InvalidTask(
          s"OpenCL.std printf format expects at least ${conversions.size} arguments, received ${sources.size - 1}"
        )
      )
    val arguments = mutable.ArrayBuffer.empty[Event.PrintArgument]
    var index     = 0
    while (index < conversions.size) {
      val source = sources(index + 1)
      values.get(id(source)) match {
        case Some(RuntimeValue.Bits(typeId, lanes)) =>
          signedIntegerShape(program, typeId) match {
            case Some((width, _, _)) =>
              arguments += Event.PrintArgument.Integer(Vec.from(lanes), width.value)
            case None => floatingShape(program, typeId) match {
                case Some((width, _)) =>
                  arguments += Event.PrintArgument.Floating(Vec.from(lanes), width.value)
                case None =>
                  return Left(
                    Fault.InvalidTask(
                      s"OpenCL.std printf argument ${index + 1} is not an integer or floating-point value"
                    )
                  )
              }
          }
        case Some(RuntimeValue.Address(_, pointer)) if conversions(index) == 's' =>
          readCString(memory, pointer) match {
            case Left(error) => return Left(error)
            case Right(text) => arguments += Event.PrintArgument.Text(text)
          }
        case Some(RuntimeValue.Address(_, pointer)) =>
          memory.pointerInteger(pointer) match {
            case Left(error)  => return Left(error)
            case Right(value) => arguments += Event.PrintArgument.Pointer(value)
          }
        case None => return Left(undefined(source))
      }
      index += 1
    }
    memory.observe(Event.Print(format, Vec.from(arguments))).flatMap { _ =>
      integerShape(program, id(resultType)) match {
        case Some((width, 1)) =>
          values.update(id(destination), RuntimeValue.Bits(id(resultType), Array(0L & mask(width))))
          Right(())
        case _ => Left(Fault.InvalidTask("OpenCL.std printf result is not an integer scalar"))
      }
    }
  }

  private def signedIntegerShape(program: Program, typeId: Int): Option[(BitWidth, Int, Boolean)] =
    program.runtimeType(typeId) match {
      case Some(RuntimeType.Integer(width, signed))       => Some((width, 1, signed))
      case Some(RuntimeType.VectorType(component, count)) => program.runtimeType(component) match {
          case Some(RuntimeType.Integer(width, signed)) => Some((width, count, signed))
          case _                                        => None
        }
      case _ => None
    }

  @SuppressWarnings(Array("scalafix:DisableSyntax.return", "scalafix:DisableSyntax.var"))
  private def readCString(memory: MemoryState, pointer: Pointer): Either[Fault, String] = {
    val bytes = mutable.ArrayBuffer.empty[Byte]
    var index = 0L
    while (index < 1024L * 1024L) {
      val current =
        try pointer.copy(offset = Math.addExact(pointer.offset, index))
        catch {
          case _: ArithmeticException =>
            return Left(Fault.InvalidTask("OpenCL.std printf string pointer overflows"))
        }
      memory.read(current, 1) match {
        case Left(error)                   => return Left(error)
        case Right(value) if value(0) == 0 =>
          return Right(String(bytes.toArray, StandardCharsets.UTF_8))
        case Right(value) => bytes += value(0)
      }
      index += 1L
    }
    Left(Fault.InvalidTask("OpenCL.std printf string exceeds 1 MiB"))
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.return", "scalafix:DisableSyntax.var"))
  private def printfConversions(format: String): Either[Fault, Vec[Char]] = {
    val result = mutable.ArrayBuffer.empty[Char]
    var index  = 0
    while (index < format.length)
      if (format(index) != '%') index += 1
      else if (index + 1 < format.length && format(index + 1) == '%') index += 2
      else {
        index += 1
        while (index < format.length && "-+ #0".contains(format(index))) index += 1
        if (index < format.length && format(index) == '*')
          return Left(Fault.Unsupported("OpenCL.std printf dynamic field widths are not implemented"))
        while (index < format.length && format(index).isDigit) index += 1
        if (index < format.length && format(index) == '.') {
          index += 1
          if (index < format.length && format(index) == '*')
            return Left(Fault.Unsupported("OpenCL.std printf dynamic precision is not implemented"))
          while (index < format.length && format(index).isDigit) index += 1
        }
        if (index < format.length && format(index) == 'v') {
          index += 1
          while (index < format.length && format(index).isDigit) index += 1
        }
        while (index < format.length && (format(index) == 'h' || format(index) == 'l')) index += 1
        if (index >= format.length)
          return Left(Fault.InvalidTask("OpenCL.std printf format ends within a conversion"))
        result += format(index).toLower
        index += 1
      }
    Right(Vec.from(result))
  }

  private def extendedInstruction(
      program: Program,
      values: InvocationValues,
      memory: MemoryState,
      resultType: Operand,
      destination: Operand,
      set: Operand,
      instruction: Operand,
      sources: Vec[Operand]
  ): Either[Fault, Unit] = {
    val setId  = id(set)
    val opcode = id(instruction)
    program.extendedInstructionSets.get(setId).map(_.value) match {
      case None =>
        Left(Fault.InvalidTask(s"OpExtInst references undefined import %$setId"))
      case Some("GLSL.std.450")
          if opcode == ExtendedInstruction.GlslStd450.FAbs || opcode == ExtendedInstruction.GlslStd450.Sin || opcode == ExtendedInstruction.GlslStd450.Cos || opcode == ExtendedInstruction.GlslStd450.Sqrt =>
        glslUnaryFloating(program, values, resultType, destination, opcode, sources)
      case Some("GLSL.std.450") if opcode == ExtendedInstruction.GlslStd450.Pow =>
        if (sources.size != 2)
          Left(Fault.InvalidTask("GLSL.std.450 Pow expects two operands"))
        else openCLPow(program, values, resultType, destination, sources(0), sources(1))
      case Some("OpenCL.std") if opcode == ExtendedInstruction.OpenClStd.copysign =>
        if (sources.size != 2)
          Left(Fault.InvalidTask(
            s"OpenCL.std copysign expects 2 operands, received ${sources.size}"
          ))
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
                  val signMask = width.value match {
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
                    Fault.InvalidTask(
                      "OpenCL.std copysign requires matching floating-point scalar/vector operand and result types"
                    )
                  )
              }
            case (None, _) => Left(undefined(sources(0)))
            case (_, None) => Left(undefined(sources(1)))
            case _         => Left(Fault.InvalidTask("OpenCL.std copysign operands are not floating-point"))
          }
      case Some("OpenCL.std")
          if opcode == ExtendedInstruction.OpenClStd.fma || opcode == ExtendedInstruction.OpenClStd.mad =>
        val name = if (opcode == ExtendedInstruction.OpenClStd.fma) "fma" else "mad"
        if (sources.size != 3)
          Left(Fault.InvalidTask(
            s"OpenCL.std $name expects 3 operands, received ${sources.size}"
          ))
        else openCLMad(
          program,
          values,
          resultType,
          destination,
          sources(0),
          sources(1),
          sources(2),
          opcode == ExtendedInstruction.OpenClStd.fma
        )
      case Some("OpenCL.std") if opcode == ExtendedInstruction.OpenClStd.maxmag =>
        if (sources.size != 2)
          Left(Fault.InvalidTask(
            s"OpenCL.std maxmag expects 2 operands, received ${sources.size}"
          ))
        else openCLMaxMag(program, values, resultType, destination, sources(0), sources(1))
      case Some("OpenCL.std") if opcode == ExtendedInstruction.OpenClStd.pow =>
        if (sources.size != 2)
          Left(Fault.InvalidTask(
            s"OpenCL.std pow expects 2 operands, received ${sources.size}"
          ))
        else openCLPow(program, values, resultType, destination, sources(0), sources(1))
      case Some("OpenCL.std")
          if opcode >= ExtendedInstruction.OpenClStd.s_max && opcode <= ExtendedInstruction.OpenClStd.u_min =>
        val name = opcode match {
          case ExtendedInstruction.OpenClStd.s_max => "s_max"
          case ExtendedInstruction.OpenClStd.u_max => "u_max"
          case ExtendedInstruction.OpenClStd.s_min => "s_min"
          case _                                   => "u_min"
        }
        val width = integerShape(program, id(resultType)).map(_._1).getOrElse(BitWidth(64))
        if (sources.size != 2)
          Left(Fault.InvalidTask(
            s"OpenCL.std $name expects 2 operands, received ${sources.size}"
          ))
        else
          integerBitwise(program, values, resultType, destination, sources(0), sources(1), s"OpenCL.std $name") {
            (left, right) =>
              opcode match {
                case ExtendedInstruction.OpenClStd.s_max =>
                  if (signExtend(left, width) >= signExtend(right, width)) left else right
                case ExtendedInstruction.OpenClStd.u_max =>
                  if (java.lang.Long.compareUnsigned(left, right) >= 0) left else right
                case ExtendedInstruction.OpenClStd.s_min =>
                  if (signExtend(left, width) <= signExtend(right, width)) left else right
                case _ => if (java.lang.Long.compareUnsigned(left, right) <= 0) left else right
              }
          }
      case Some("OpenCL.std") if opcode == ExtendedInstruction.OpenClStd.sincos =>
        if (sources.size != 2)
          Left(Fault.InvalidTask(
            s"OpenCL.std sincos expects 2 operands, received ${sources.size}"
          ))
        else openCLSinCos(program, values, memory, resultType, destination, sources(0), sources(1))
      case Some("OpenCL.std") if opcode == ExtendedInstruction.OpenClStd.printf =>
        openCLPrintf(program, values, memory, resultType, destination, sources)
      case Some("OpenCL.std")
          if opcode == ExtendedInstruction.OpenClStd.fmax || opcode == ExtendedInstruction.OpenClStd.fmin =>
        val name = if (opcode == ExtendedInstruction.OpenClStd.fmax) "fmax" else "fmin"
        if (sources.size != 2)
          Left(Fault.InvalidTask(
            s"OpenCL.std $name expects 2 operands, received ${sources.size}"
          ))
        else
          floatingBinary(
            program,
            values,
            resultType,
            destination,
            sources(0),
            sources(1),
            if (opcode == ExtendedInstruction.OpenClStd.fmax) FloatingOperation.Maximum else FloatingOperation.Minimum
          )
      case Some("OpenCL.std") if opcode == ExtendedInstruction.OpenClStd.ldexp =>
        if (sources.size != 2)
          Left(Fault.InvalidTask(
            s"OpenCL.std ldexp expects 2 operands, received ${sources.size}"
          ))
        else openCLLdexp(program, values, resultType, destination, sources(0), sources(1))
      case Some("OpenCL.std")
          if OpenCLUnaryFloatingOpcodes.contains(opcode) =>
        val name = opcode match {
          case ExtendedInstruction.OpenClStd.acos         => "acos"
          case ExtendedInstruction.OpenClStd.acosh        => "acosh"
          case ExtendedInstruction.OpenClStd.acospi       => "acospi"
          case ExtendedInstruction.OpenClStd.asin         => "asin"
          case ExtendedInstruction.OpenClStd.asinh        => "asinh"
          case ExtendedInstruction.OpenClStd.asinpi       => "asinpi"
          case ExtendedInstruction.OpenClStd.atan         => "atan"
          case ExtendedInstruction.OpenClStd.atanh        => "atanh"
          case ExtendedInstruction.OpenClStd.atanpi       => "atanpi"
          case ExtendedInstruction.OpenClStd.cbrt         => "cbrt"
          case ExtendedInstruction.OpenClStd.ceil         => "ceil"
          case ExtendedInstruction.OpenClStd.cos          => "cos"
          case ExtendedInstruction.OpenClStd.cosh         => "cosh"
          case ExtendedInstruction.OpenClStd.cospi        => "cospi"
          case ExtendedInstruction.OpenClStd.exp          => "exp"
          case ExtendedInstruction.OpenClStd.exp2         => "exp2"
          case ExtendedInstruction.OpenClStd.exp10        => "exp10"
          case ExtendedInstruction.OpenClStd.expm1        => "expm1"
          case ExtendedInstruction.OpenClStd.fabs         => "fabs"
          case ExtendedInstruction.OpenClStd.floor        => "floor"
          case ExtendedInstruction.OpenClStd.log          => "log"
          case ExtendedInstruction.OpenClStd.log2         => "log2"
          case ExtendedInstruction.OpenClStd.log10        => "log10"
          case ExtendedInstruction.OpenClStd.log1p        => "log1p"
          case ExtendedInstruction.OpenClStd.logb         => "logb"
          case ExtendedInstruction.OpenClStd.rint         => "rint"
          case ExtendedInstruction.OpenClStd.round        => "round"
          case ExtendedInstruction.OpenClStd.rsqrt        => "rsqrt"
          case ExtendedInstruction.OpenClStd.sin          => "sin"
          case ExtendedInstruction.OpenClStd.sinh         => "sinh"
          case ExtendedInstruction.OpenClStd.sinpi        => "sinpi"
          case ExtendedInstruction.OpenClStd.sqrt         => "sqrt"
          case ExtendedInstruction.OpenClStd.tan          => "tan"
          case ExtendedInstruction.OpenClStd.tanh         => "tanh"
          case ExtendedInstruction.OpenClStd.tanpi        => "tanpi"
          case ExtendedInstruction.OpenClStd.trunc        => "trunc"
          case ExtendedInstruction.OpenClStd.native_cos   => "native_cos"
          case ExtendedInstruction.OpenClStd.native_exp   => "native_exp"
          case ExtendedInstruction.OpenClStd.native_exp2  => "native_exp2"
          case ExtendedInstruction.OpenClStd.native_exp10 => "native_exp10"
          case ExtendedInstruction.OpenClStd.native_log   => "native_log"
          case ExtendedInstruction.OpenClStd.native_log2  => "native_log2"
          case ExtendedInstruction.OpenClStd.native_log10 => "native_log10"
          case ExtendedInstruction.OpenClStd.native_recip => "native_recip"
          case ExtendedInstruction.OpenClStd.native_rsqrt => "native_rsqrt"
          case ExtendedInstruction.OpenClStd.native_sin   => "native_sin"
          case ExtendedInstruction.OpenClStd.native_sqrt  => "native_sqrt"
          case ExtendedInstruction.OpenClStd.native_tan   => "native_tan"
        }
        if (sources.size != 1)
          Left(Fault.InvalidTask(
            s"OpenCL.std $name expects 1 operand, received ${sources.size}"
          ))
        else
          values.get(id(sources.head)) match {
            case Some(RuntimeValue.Bits(sourceType, lanes)) =>
              (floatingShape(program, sourceType), floatingShape(program, id(resultType))) match {
                case (Some((sourceWidth, sourceLanes)), Some((resultWidth, resultLanes)))
                    if sourceWidth == resultWidth && sourceLanes == resultLanes =>
                  val result = lanes.map { raw =>
                    def evaluate(value: Double): Double = opcode match {
                      case ExtendedInstruction.OpenClStd.acos   => Math.acos(value)
                      case ExtendedInstruction.OpenClStd.acosh  => Math.log(value + Math.sqrt(value * value - 1.0))
                      case ExtendedInstruction.OpenClStd.acospi => Math.acos(value) / Math.PI
                      case ExtendedInstruction.OpenClStd.asin   => Math.asin(value)
                      case ExtendedInstruction.OpenClStd.asinh  => Math.log(value + Math.sqrt(value * value + 1.0))
                      case ExtendedInstruction.OpenClStd.asinpi => Math.asin(value) / Math.PI
                      case ExtendedInstruction.OpenClStd.atan   => Math.atan(value)
                      case ExtendedInstruction.OpenClStd.atanh  => 0.5 * Math.log((1.0 + value) / (1.0 - value))
                      case ExtendedInstruction.OpenClStd.atanpi => Math.atan(value) / Math.PI
                      case ExtendedInstruction.OpenClStd.cbrt   => Math.cbrt(value)
                      case ExtendedInstruction.OpenClStd.ceil   => Math.ceil(value)
                      case ExtendedInstruction.OpenClStd.cos    => Math.cos(value)
                      case ExtendedInstruction.OpenClStd.cosh   => Math.cosh(value)
                      case ExtendedInstruction.OpenClStd.cospi  => openCLCosPi(value)
                      case ExtendedInstruction.OpenClStd.exp    => Math.exp(value)
                      case ExtendedInstruction.OpenClStd.exp2   => Math.pow(2.0, value)
                      case ExtendedInstruction.OpenClStd.exp10  => Math.pow(10.0, value)
                      case ExtendedInstruction.OpenClStd.expm1  => Math.expm1(value)
                      case ExtendedInstruction.OpenClStd.fabs   => Math.abs(value)
                      case ExtendedInstruction.OpenClStd.floor  => Math.floor(value)
                      case ExtendedInstruction.OpenClStd.log    => Math.log(value)
                      case ExtendedInstruction.OpenClStd.log2   => Math.log(value) / Math.log(2.0)
                      case ExtendedInstruction.OpenClStd.log10  => Math.log10(value)
                      case ExtendedInstruction.OpenClStd.log1p  => Math.log1p(value)
                      case ExtendedInstruction.OpenClStd.logb   =>
                        if (value.isNaN) Double.NaN
                        else if (value == 0.0) Double.NegativeInfinity
                        else if (value.isInfinite) Double.PositiveInfinity
                        else Math.getExponent(value).toDouble
                      case ExtendedInstruction.OpenClStd.rint  => Math.rint(value)
                      case ExtendedInstruction.OpenClStd.round =>
                        if (value < 0.0) Math.ceil(value - 0.5) else Math.floor(value + 0.5)
                      case ExtendedInstruction.OpenClStd.rsqrt => 1.0 / Math.sqrt(value)
                      case ExtendedInstruction.OpenClStd.sin   => Math.sin(value)
                      case ExtendedInstruction.OpenClStd.sinh  => Math.sinh(value)
                      case ExtendedInstruction.OpenClStd.sinpi => openCLSinPi(value)
                      case ExtendedInstruction.OpenClStd.sqrt  => Math.sqrt(value)
                      case ExtendedInstruction.OpenClStd.tan   => Math.tan(value)
                      case ExtendedInstruction.OpenClStd.tanh  => Math.tanh(value)
                      case ExtendedInstruction.OpenClStd.tanpi => openCLTanPi(value)
                      case ExtendedInstruction.OpenClStd.trunc =>
                        if (value < 0.0) Math.ceil(value) else Math.floor(value)
                      case ExtendedInstruction.OpenClStd.native_cos   => Math.cos(value)
                      case ExtendedInstruction.OpenClStd.native_exp   => Math.exp(value)
                      case ExtendedInstruction.OpenClStd.native_exp2  => Math.pow(2.0, value)
                      case ExtendedInstruction.OpenClStd.native_exp10 => Math.pow(10.0, value)
                      case ExtendedInstruction.OpenClStd.native_log   => Math.log(value)
                      case ExtendedInstruction.OpenClStd.native_log2  => Math.log(value) / Math.log(2.0)
                      case ExtendedInstruction.OpenClStd.native_log10 => Math.log10(value)
                      case ExtendedInstruction.OpenClStd.native_recip => 1.0 / value
                      case ExtendedInstruction.OpenClStd.native_rsqrt => 1.0 / Math.sqrt(value)
                      case ExtendedInstruction.OpenClStd.native_sin   => Math.sin(value)
                      case ExtendedInstruction.OpenClStd.native_sqrt  => Math.sqrt(value)
                      case ExtendedInstruction.OpenClStd.native_tan   => Math.tan(value)
                    }
                    resultWidth.value match {
                      case 16 if opcode == ExtendedInstruction.OpenClStd.fabs => raw & 0x7fffL
                      case 32 if opcode == ExtendedInstruction.OpenClStd.fabs => raw & 0x7fffffffL
                      case 64 if opcode == ExtendedInstruction.OpenClStd.fabs => raw & 0x7fffffffffffffffL
                      case 16                                                 =>
                        IEEE754.floatToBinary16(
                          evaluate(IEEE754.binary16ToFloat(Binary16.low(raw)).toDouble).toFloat
                        ).value
                      case 32 =>
                        IEEE754.floatToBinary32(
                          evaluate(IEEE754.binary32ToFloat(Binary32.low(raw)).toDouble).toFloat
                        ).value
                      case 64 => IEEE754.doubleToBinary64(evaluate(IEEE754.binary64ToDouble(raw)))
                    }
                  }
                  values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
                  Right(())
                case _ =>
                  Left(
                    Fault.InvalidTask(
                      s"OpenCL.std $name requires matching floating-point scalar/vector operand and result types"
                    )
                  )
              }
            case Some(_) => Left(
                Fault.InvalidTask(s"OpenCL.std $name operand is not floating-point")
              )
            case None => Left(undefined(sources.head))
          }
      case Some("OpenCL.std") if OpenCLBinaryFloatingOpcodes.contains(opcode) =>
        val name = opcode match {
          case ExtendedInstruction.OpenClStd.atan2   => "atan2"
          case ExtendedInstruction.OpenClStd.atan2pi => "atan2pi"
          case ExtendedInstruction.OpenClStd.fdim    => "fdim"
          case ExtendedInstruction.OpenClStd.fmod    => "fmod"
          case ExtendedInstruction.OpenClStd.hypot   => "hypot"
          case _                                     => "powr"
        }
        if (sources.size != 2)
          Left(Fault.InvalidTask(
            s"OpenCL.std $name expects 2 operands, received ${sources.size}"
          ))
        else if (opcode == ExtendedInstruction.OpenClStd.atan2 || opcode == ExtendedInstruction.OpenClStd.hypot)
          openCLAtan2OrHypot(
            program,
            values,
            resultType,
            destination,
            sources(0),
            sources(1),
            opcode == ExtendedInstruction.OpenClStd.hypot
          )
        else
          openCLFloatingElementwise(program, values, resultType, destination, sources, name) { operands =>
            opcode match {
              case ExtendedInstruction.OpenClStd.atan2pi => Math.atan2(operands(0), operands(1)) / Math.PI
              case ExtendedInstruction.OpenClStd.fdim    => Math.max(operands(0) - operands(1), 0.0)
              case ExtendedInstruction.OpenClStd.fmod    => operands(0) % operands(1)
              case _                                     => openCLPowr(operands(0), operands(1))
            }
          }
      case Some("OpenCL.std") if opcode == ExtendedInstruction.OpenClStd.nan =>
        if (sources.size != 1)
          Left(Fault.InvalidTask(
            s"OpenCL.std nan expects 1 operand, received ${sources.size}"
          ))
        else openCLNan(program, values, resultType, destination, sources.head)
      case Some("OpenCL.std")
          if opcode == ExtendedInstruction.OpenClStd.pown || opcode == ExtendedInstruction.OpenClStd.rootn =>
        val name = if (opcode == ExtendedInstruction.OpenClStd.pown) "pown" else "rootn"
        if (sources.size != 2)
          Left(Fault.InvalidTask(
            s"OpenCL.std $name expects 2 operands, received ${sources.size}"
          ))
        else openCLIntegerPower(
          program,
          values,
          resultType,
          destination,
          sources(0),
          sources(1),
          opcode == ExtendedInstruction.OpenClStd.rootn
        )
      case Some("OpenCL.std") if opcode == ExtendedInstruction.OpenClStd.nextafter =>
        if (sources.size != 2)
          Left(Fault.InvalidTask(
            s"OpenCL.std nextafter expects 2 operands, received ${sources.size}"
          ))
        else openCLNextAfter(program, values, resultType, destination, sources(0), sources(1))
      case Some("OpenCL.std") if opcode == ExtendedInstruction.OpenClStd.s_hadd =>
        if (sources.size != 2)
          Left(Fault.InvalidTask(
            s"OpenCL.std hadd expects 2 operands, received ${sources.size}"
          ))
        else openCLSignedHalfAdd(program, values, resultType, destination, sources(0), sources(1))
      case Some("OpenCL.std") if opcode == ExtendedInstruction.OpenClStd.s_abs =>
        if (sources.size != 1)
          Left(Fault.InvalidTask(
            s"OpenCL.std s_abs expects 1 operand, received ${sources.size}"
          ))
        else openCLSignedAbs(program, values, resultType, destination, sources.head)
      case Some("OpenCL.std") if opcode == ExtendedInstruction.OpenClStd.clz =>
        if (sources.size != 1)
          Left(Fault.InvalidTask(
            s"OpenCL.std clz expects 1 operand, received ${sources.size}"
          ))
        else openCLCountLeadingZeros(program, values, resultType, destination, sources.head)
      case Some("OpenCL.std") if opcode == ExtendedInstruction.OpenClStd.ctz =>
        if (sources.size != 1)
          Left(Fault.InvalidTask(
            s"OpenCL.std ctz expects 1 operand, received ${sources.size}"
          ))
        else openCLCountTrailingZeros(program, values, resultType, destination, sources.head)
      case Some("OpenCL.std") if opcode == ExtendedInstruction.OpenClStd.popcount =>
        if (sources.size != 1)
          Left(Fault.InvalidTask(
            s"OpenCL.std popcount expects 1 operand, received ${sources.size}"
          ))
        else openCLPopulationCount(program, values, resultType, destination, sources.head)
      case Some("OpenCL.std")
          if opcode >= ExtendedInstruction.OpenClStd.fclamp && opcode <= ExtendedInstruction.OpenClStd.sign =>
        openCLCommon(program, values, resultType, destination, opcode, sources)
      case Some("OpenCL.std")
          if opcode >= ExtendedInstruction.OpenClStd.cross && opcode <= ExtendedInstruction.OpenClStd.fast_normalize =>
        openCLGeometric(program, values, resultType, destination, opcode, sources)
      case Some("OpenCL.std")
          if opcode == ExtendedInstruction.OpenClStd.vload_half || opcode == ExtendedInstruction.OpenClStd.vload_halfn || opcode == ExtendedInstruction.OpenClStd.vloada_halfn =>
        openCLHalfLoad(
          program,
          values,
          memory,
          resultType,
          destination,
          sources,
          aligned = opcode == ExtendedInstruction.OpenClStd.vloada_halfn
        )
      case Some("OpenCL.std")
          if opcode >= ExtendedInstruction.OpenClStd.vstore_half && opcode <= ExtendedInstruction.OpenClStd.vstorea_halfn_r =>
        openCLHalfStore(
          program,
          values,
          memory,
          sources,
          aligned = opcode >= ExtendedInstruction.OpenClStd.vstorea_halfn,
          rounded =
            opcode == ExtendedInstruction.OpenClStd.vstore_half_r || opcode == ExtendedInstruction.OpenClStd.vstore_halfn_r || opcode == ExtendedInstruction.OpenClStd.vstorea_halfn_r
        )
      case Some("OpenCL.std") if opcode == ExtendedInstruction.OpenClStd.vloadn =>
        openCLVectorLoad(program, values, memory, resultType, destination, sources)
      case Some("OpenCL.std") if opcode == ExtendedInstruction.OpenClStd.vstoren =>
        if (sources.size != 3)
          Left(Fault.InvalidTask(
            s"OpenCL.std vstoren expects 3 operands, received ${sources.size}"
          ))
        else
          openCLVectorStore(program, values, memory, sources.head, Vec(sources(1), sources(2)))
      case Some("OpenCL.std")
          if opcode == ExtendedInstruction.OpenClStd.shuffle || opcode == ExtendedInstruction.OpenClStd.shuffle2 =>
        openCLShuffle(
          program,
          values,
          resultType,
          destination,
          sources,
          opcode == ExtendedInstruction.OpenClStd.shuffle2
        )
      case Some("OpenCL.std") if opcode == ExtendedInstruction.OpenClStd.bitselect =>
        openCLBitselect(program, values, resultType, destination, sources)
      case Some("OpenCL.std") if opcode == ExtendedInstruction.OpenClStd.select =>
        openCLSelect(program, values, resultType, destination, sources)
      case Some("OpenCL.std") if opcode == ExtendedInstruction.OpenClStd.prefetch =>
        if (sources.isEmpty)
          Left(Fault.InvalidTask("OpenCL.std prefetch is missing its pointer operand"))
        else address(values, sources.head).map(_ => ())
      case Some(name) =>
        Left(Fault.Unsupported(
          s"OpExtInst $name opcode $opcode execution is not implemented"
        ))
    }
  }

  private def glslUnaryFloating(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      opcode: Int,
      sources: Vec[Operand]
  ): Either[Fault, Unit] =
    if (sources.size != 1) Left(Fault.InvalidTask(s"GLSL.std.450 opcode $opcode expects one operand"))
    else values.get(id(sources.head)) match {
      case Some(RuntimeValue.Bits(sourceType, lanes)) =>
        (floatingShape(program, sourceType), floatingShape(program, id(resultType))) match {
          case (Some((sourceWidth, sourceLanes)), Some((resultWidth, resultLanes)))
              if sourceWidth == resultWidth && sourceLanes == resultLanes =>
            val result = lanes.map { raw =>
              if (opcode == ExtendedInstruction.GlslStd450.FAbs) resultWidth.value match {
                case 16 => raw & 0x7fffL
                case 32 => raw & 0x7fffffffL
                case 64 => raw & 0x7fffffffffffffffL
              }
              else {
                val value    = floatingToDouble(sourceWidth, raw)
                val computed = opcode match {
                  case ExtendedInstruction.GlslStd450.Sin  => Math.sin(value)
                  case ExtendedInstruction.GlslStd450.Cos  => Math.cos(value)
                  case ExtendedInstruction.GlslStd450.Sqrt => Math.sqrt(value)
                }
                resultWidth.value match {
                  case 16 => IEEE754.floatToBinary16(computed.toFloat).value
                  case 32 => IEEE754.floatToBinary32(computed.toFloat).value
                  case 64 => IEEE754.doubleToBinary64(computed)
                }
              }
            }
            values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
            Right(())
          case _ => Left(Fault.InvalidTask("GLSL.std.450 unary floating types do not match"))
        }
      case Some(_) => Left(Fault.InvalidTask("GLSL.std.450 unary operand is not floating-point"))
      case None    => Left(undefined(sources.head))
    }

  private def floatingToDouble(width: FloatingPointWidth, bits: Long): Double = IEEE754.toDouble(bits, width)

  private def doubleToFloating(width: FloatingPointWidth, value: Double): Long = IEEE754.fromDouble(value, width)

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

  // Elementwise floating builtins reuse indexed component buffers and fail fast on type mismatches.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return", "scalafix:DisableSyntax.var"))
  private def openCLFloatingElementwise(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      sources: Vec[Operand],
      name: String
  )(operation: Array[Double] => Double): Either[Fault, Unit] =
    floatingShape(program, id(resultType)) match {
      case None =>
        Left(Fault.InvalidTask(s"OpenCL.std $name result is not floating-point"))
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
                    Fault.InvalidTask(
                      s"OpenCL.std $name operand ${sourceIndex + 1} does not match result shape ($width,$count)"
                    )
                  )
              }
            case Some(_) =>
              return Left(Fault.InvalidTask(
                s"OpenCL.std $name operand ${sourceIndex + 1} is a pointer"
              ))
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
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      opcode: Int,
      sources: Vec[Operand]
  ): Either[Fault, Unit] = {
    val (name, arity) = opcode match {
      case ExtendedInstruction.OpenClStd.fclamp      => "clamp"      -> 3
      case ExtendedInstruction.OpenClStd.degrees     => "degrees"    -> 1
      case ExtendedInstruction.OpenClStd.fmax_common => "fmax"       -> 2
      case ExtendedInstruction.OpenClStd.fmin_common => "fmin"       -> 2
      case ExtendedInstruction.OpenClStd.mix         => "mix"        -> 3
      case ExtendedInstruction.OpenClStd.radians     => "radians"    -> 1
      case ExtendedInstruction.OpenClStd.step        => "step"       -> 2
      case ExtendedInstruction.OpenClStd.smoothstep  => "smoothstep" -> 3
      case _                                         => "sign"       -> 1
    }
    if (sources.size != arity)
      Left(Fault.InvalidTask(
        s"OpenCL.std $name expects $arity operands, received ${sources.size}"
      ))
    else
      openCLFloatingElementwise(program, values, resultType, destination, sources, name) { operands =>
        opcode match {
          case ExtendedInstruction.OpenClStd.fclamp =>
            floatingMinimum(floatingMaximum(operands(0), operands(1)), operands(2))
          case ExtendedInstruction.OpenClStd.degrees     => operands(0) * (180.0 / Math.PI)
          case ExtendedInstruction.OpenClStd.fmax_common => floatingMaximum(operands(0), operands(1))
          case ExtendedInstruction.OpenClStd.fmin_common => floatingMinimum(operands(0), operands(1))
          case ExtendedInstruction.OpenClStd.mix         => operands(0) + (operands(1) - operands(0)) * operands(2)
          case ExtendedInstruction.OpenClStd.radians     => operands(0) * (Math.PI / 180.0)
          case ExtendedInstruction.OpenClStd.step        => if (operands(1) < operands(0)) 0.0 else 1.0
          case ExtendedInstruction.OpenClStd.smoothstep  =>
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

  // Geometric builtins accumulate lane values directly and fail fast on incompatible shapes.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return", "scalafix:DisableSyntax.var"))
  private def openCLGeometric(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      opcode: Int,
      sources: Vec[Operand]
  ): Either[Fault, Unit] = {
    val name = opcode match {
      case ExtendedInstruction.OpenClStd.cross         => "cross"
      case ExtendedInstruction.OpenClStd.distance      => "distance"
      case ExtendedInstruction.OpenClStd.length        => "length"
      case ExtendedInstruction.OpenClStd.normalize     => "normalize"
      case ExtendedInstruction.OpenClStd.fast_distance => "fast_distance"
      case ExtendedInstruction.OpenClStd.fast_length   => "fast_length"
      case _                                           => "fast_normalize"
    }
    val arity =
      if (
        opcode == ExtendedInstruction.OpenClStd.cross || opcode == ExtendedInstruction.OpenClStd.distance || opcode == ExtendedInstruction.OpenClStd.fast_distance
      ) 2
      else 1
    if (sources.size != arity)
      return Left(
        Fault.InvalidTask(
          s"OpenCL.std $name expects $arity operands, received ${sources.size}"
        )
      )
    val operands = Array.ofDim[Array[Long]](arity)
    var width    = Option.empty[FloatingPointWidth]
    var count    = 0
    var index    = 0
    while (index < arity) {
      values.get(id(sources(index))) match {
        case Some(RuntimeValue.Bits(sourceType, lanes)) => floatingShape(program, sourceType) match {
            case Some((sourceWidth, sourceCount))
                if width.forall(_ == sourceWidth) && (index == 0 || sourceCount == count) =>
              width = Some(sourceWidth)
              count = sourceCount
              operands(index) = lanes
            case _ => return Left(Fault.InvalidTask(
                s"OpenCL.std $name operands have incompatible types"
              ))
          }
        case Some(_) =>
          return Left(Fault.InvalidTask(s"OpenCL.std $name operand is a pointer"))
        case None => return Left(undefined(sources(index)))
      }
      index += 1
    }
    val commonWidth = width match {
      case Some(value) => value
      case None        =>
        return Left(
          Fault.InvalidTask(s"OpenCL.std $name has no floating-point operands")
        )
    }
    def component(source: Int, lane: Int): Double = floatingToDouble(commonWidth, operands(source)(lane))
    def magnitude(source: Int): Double            = {
      var length = 0.0
      var lane   = 0
      while (lane < count) {
        length = Math.hypot(length, component(source, lane))
        lane += 1
      }
      length
    }
    opcode match {
      case ExtendedInstruction.OpenClStd.cross =>
        if (count != 3 && count != 4)
          Left(
            Fault.InvalidTask("OpenCL.std cross requires 3- or 4-component vectors")
          )
        else if (!floatingShape(program, id(resultType)).contains((commonWidth, count)))
          Left(Fault.InvalidTask(
            "OpenCL.std cross result type does not match its operands"
          ))
        else {
          val result = Array(
            doubleToFloating(commonWidth, component(0, 1) * component(1, 2) - component(0, 2) * component(1, 1)),
            doubleToFloating(commonWidth, component(0, 2) * component(1, 0) - component(0, 0) * component(1, 2)),
            doubleToFloating(commonWidth, component(0, 0) * component(1, 1) - component(0, 1) * component(1, 0))
          ) ++ Option.when(count == 4)(doubleToFloating(commonWidth, 0.0))
          values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
          Right(())
        }
      case ExtendedInstruction.OpenClStd.distance | ExtendedInstruction.OpenClStd.fast_distance =>
        if (!floatingShape(program, id(resultType)).contains((commonWidth, 1)))
          Left(Fault.InvalidTask(
            s"OpenCL.std $name result is not a matching floating scalar"
          ))
        else {
          var distance = 0.0
          var lane     = 0
          while (lane < count) {
            distance = Math.hypot(distance, component(0, lane) - component(1, lane))
            lane += 1
          }
          values.update(
            id(destination),
            RuntimeValue.Bits(id(resultType), Array(doubleToFloating(commonWidth, distance)))
          )
          Right(())
        }
      case ExtendedInstruction.OpenClStd.length | ExtendedInstruction.OpenClStd.fast_length =>
        if (!floatingShape(program, id(resultType)).contains((commonWidth, 1)))
          Left(Fault.InvalidTask(
            s"OpenCL.std $name result is not a matching floating scalar"
          ))
        else {
          values.update(
            id(destination),
            RuntimeValue.Bits(id(resultType), Array(doubleToFloating(commonWidth, magnitude(0))))
          )
          Right(())
        }
      case _ =>
        if (!floatingShape(program, id(resultType)).contains((commonWidth, count)))
          Left(Fault.InvalidTask(
            s"OpenCL.std $name result type does not match its operand"
          ))
        else {
          var scale = 0.0
          var lane  = 0
          while (lane < count) {
            scale = math.max(scale, math.abs(component(0, lane)))
            lane += 1
          }
          val scaledLength =
            if (scale == 0.0 || !scale.isFinite) magnitude(0)
            else {
              var length = 0.0
              lane = 0
              while (lane < count) {
                length = Math.hypot(length, component(0, lane) / scale)
                lane += 1
              }
              length
            }
          val result = Array.tabulate(count) { lane =>
            val normalized =
              if (scale == 0.0 || !scale.isFinite) component(0, lane) / scaledLength
              else (component(0, lane) / scale) / scaledLength
            doubleToFloating(commonWidth, normalized)
          }
          values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
          Right(())
        }
    }
  }

  // Shuffle validation exits before writing a partially defined vector.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return"))
  private def openCLShuffle(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      sources: Vec[Operand],
      twoInputs: Boolean
  ): Either[Fault, Unit] = {
    val expected = if (twoInputs) 3 else 2
    if (sources.size != expected)
      return Left(
        Fault.InvalidTask(s"OpenCL.std shuffle expects $expected operands")
      )
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
          case _ => Left(Fault.InvalidTask("OpenCL.std shuffle operand types are incompatible"))
        }
      case (None, _, _) => Left(undefined(sources(0)))
      case (_, _, None) => Left(undefined(sources(maskIndex)))
      case _            =>
        Left(Fault.InvalidTask("OpenCL.std shuffle operands are not value types"))
    }
  }

  // Bitselect validation exits before writing a partially defined vector.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return"))
  private def openCLBitselect(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      sources: Vec[Operand]
  ): Either[Fault, Unit] = {
    if (sources.size != 3)
      return Left(Fault.InvalidTask(
        s"OpenCL.std bitselect expects 3 operands, received ${sources.size}"
      ))
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
          Left(Fault.InvalidTask(
            "OpenCL.std bitselect operands must have matching numeric shapes"
          ))
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
      case _ => Left(Fault.InvalidTask(
          "OpenCL.std bitselect operands are undefined or pointer values"
        ))
    }
  }

  // Select validation exits before writing a partially defined vector.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return"))
  private def openCLSelect(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      sources: Vec[Operand]
  ): Either[Fault, Unit] = {
    if (sources.size != 3)
      return Left(Fault.InvalidTask(
        s"OpenCL.std select expects 3 operands, received ${sources.size}"
      ))
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
            val sign   = 1L << (conditionWidth.value - 1)
            val result = Array.tabulate(count) { lane =>
              val condition = c(lane % conditionCount)
              val selected  =
                if (count == 1 && conditionCount == 1) condition != 0L
                else (condition & sign) != 0L
              if (selected) b(lane) else a(lane)
            }
            values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
            Right(())
          case _ => Left(Fault.InvalidTask("OpenCL.std select operand types are incompatible"))
        }
      case _ => Left(Fault.InvalidTask(
          "OpenCL.std select operands are undefined or pointer values"
        ))
    }
  }

  private def openCLNan(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand
  ): Either[Fault, Unit] = values.get(id(source)) match {
    case Some(RuntimeValue.Bits(sourceType, sourceLanes)) =>
      (floatingShape(program, id(resultType)), integerShape(program, sourceType)) match {
        case (Some((width, count)), Some((_, sourceCount))) if sourceCount == count || sourceCount == 1 =>
          val result = Array.tabulate(count) { lane =>
            val payload = sourceLanes(lane % sourceCount)
            width.value match {
              case 16 => 0x7e00L | (payload & 0x1ffL)
              case 32 => 0x7fc00000L | (payload & 0x3fffffL)
              case 64 => 0x7ff8000000000000L | (payload & 0x7ffffffffffffL)
            }
          }
          values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
          Right(())
        case _ => Left(Fault.InvalidTask("OpenCL.std nan requires a floating result and integer payload"))
      }
    case Some(_) => Left(Fault.InvalidTask("OpenCL.std nan payload is a pointer"))
    case None    => Left(undefined(source))
  }

  private def openCLIntegerPower(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand,
      exponent: Operand,
      root: Boolean
  ): Either[Fault, Unit] = (values.get(id(source)), values.get(id(exponent))) match {
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
            val negative = width.value match {
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
            Fault.InvalidTask(
              s"OpenCL.std ${if (root) "rootn" else "pown"} requires floating values and integer exponents"
            )
          )
      }
    case (None, _) => Left(undefined(source))
    case (_, None) => Left(undefined(exponent))
    case _         => Left(
        Fault.InvalidTask("OpenCL.std integer-power operands are not value types")
      )
  }

  private def openCLMad(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      left: Operand,
      right: Operand,
      addend: Operand,
      fused: Boolean
  ): Either[Fault, Unit] =
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
              if (fused) floatingFmaBits(width, leftLanes(index), rightLanes(index), addendLanes(index))
              else
                floatingAddBits(
                  width,
                  floatingMultiplyBits(width, leftLanes(index), rightLanes(index)),
                  addendLanes(index)
                )
            }
            values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
            Right(())
          case _ =>
            Left(Fault.InvalidTask(
              s"OpenCL.std ${if (fused) "fma" else "mad"} requires matching floating-point types"
            ))
        }
      case (None, _, _) => Left(undefined(left))
      case (_, None, _) => Left(undefined(right))
      case (_, _, None) => Left(undefined(addend))
      case _ => Left(Fault.InvalidTask(s"OpenCL.std ${if (fused) "fma" else "mad"} operands are not floating-point"))
    }

  // Sin/cos writes paired results through one indexed component loop.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def openCLSinCos(
      program: Program,
      values: InvocationValues,
      memory: MemoryState,
      resultType: Operand,
      destination: Operand,
      source: Operand,
      cosine: Operand
  ): Either[Fault, Unit] =
    (values.get(id(source)), address(values, cosine)) match {
      case (Some(RuntimeValue.Bits(sourceType, sourceLanes)), Right(cosinePointer)) =>
        (floatingShape(program, sourceType), floatingShape(program, id(resultType))) match {
          case (Some((width, count)), Some((resultWidth, resultCount)))
              if width == resultWidth && count == resultCount && sourceLanes.length == count =>
            val sineLanes   = Array.ofDim[Long](count)
            val cosineLanes = Array.ofDim[Long](count)
            var index       = 0
            while (index < count) {
              val value = floatingToDouble(width, sourceLanes(index))
              sineLanes(index) = doubleToFloating(width, Math.sin(value))
              cosineLanes(index) = doubleToFloating(width, Math.cos(value))
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
              Fault.InvalidTask(
                "OpenCL.std sincos requires matching floating-point scalar/vector types"
              )
            )
        }
      case (None, _)        => Left(undefined(source))
      case (_, Left(error)) => Left(error)
      case _                => Left(Fault.InvalidTask("OpenCL.std sincos source is not floating-point"))
    }

  private def openCLMaxMag(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      left: Operand,
      right: Operand
  ): Either[Fault, Unit] = (values.get(id(left)), values.get(id(right))) match {
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
            val leftValue  = floatingToDouble(width, leftLanes(index))
            val rightValue = floatingToDouble(width, rightLanes(index))
            val selected   =
              if (Math.abs(leftValue) > Math.abs(rightValue)) leftValue
              else if (Math.abs(rightValue) > Math.abs(leftValue)) rightValue
              else Math.max(leftValue, rightValue)
            doubleToFloating(width, selected)
          }
          values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
          Right(())
        case _ =>
          Left(Fault.InvalidTask(
            "OpenCL.std maxmag requires matching floating-point types"
          ))
      }
    case (None, _) => Left(undefined(left))
    case (_, None) => Left(undefined(right))
    case _         => Left(Fault.InvalidTask("OpenCL.std maxmag operands are not floating-point"))
  }

  private def openCLPow(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      left: Operand,
      right: Operand
  ): Either[Fault, Unit] = (values.get(id(left)), values.get(id(right))) match {
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
            doubleToFloating(
              width,
              Math.pow(floatingToDouble(width, leftLanes(index)), floatingToDouble(width, rightLanes(index)))
            )
          }
          values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
          Right(())
        case _ =>
          Left(Fault.InvalidTask(
            "OpenCL.std pow requires matching floating-point types"
          ))
      }
    case (None, _) => Left(undefined(left))
    case (_, None) => Left(undefined(right))
    case _         => Left(Fault.InvalidTask("OpenCL.std pow operands are not floating-point"))
  }

  private def openCLAtan2OrHypot(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      left: Operand,
      right: Operand,
      hypot: Boolean
  ): Either[Fault, Unit] = (values.get(id(left)), values.get(id(right))) match {
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
            val leftValue  = floatingToDouble(width, leftLanes(index))
            val rightValue = floatingToDouble(width, rightLanes(index))
            val value      = if (hypot) Math.hypot(leftValue, rightValue) else Math.atan2(leftValue, rightValue)
            doubleToFloating(width, value)
          }
          values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
          Right(())
        case _ =>
          Left(
            Fault.InvalidTask(
              s"OpenCL.std ${if (hypot) "hypot" else "atan2"} requires matching floating-point scalar/vector types"
            )
          )
      }
    case (None, _) => Left(undefined(left))
    case (_, None) => Left(undefined(right))
    case _         => Left(Fault.InvalidTask("OpenCL.std operands are not floating-point"))
  }

  private def openCLLdexp(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand,
      exponent: Operand
  ): Either[Fault, Unit] = (values.get(id(source)), values.get(id(exponent))) match {
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
            if width == resultWidth && count == resultCount && exponentWidth.value == 32 && exponentCount == count &&
              sourceLanes.length == count && exponentLanes.length == count =>
          val result = Array.tabulate(count) { index =>
            val scale = signExtend(exponentLanes(index), exponentWidth).toInt
            doubleToFloating(width, Math.scalb(floatingToDouble(width, sourceLanes(index)), scale))
          }
          values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
          Right(())
        case _ =>
          Left(Fault.InvalidTask(
            "OpenCL.std ldexp requires matching floating values and i32 exponents"
          ))
      }
    case (None, _) => Left(undefined(source))
    case (_, None) => Left(undefined(exponent))
    case _         =>
      Left(Fault.InvalidTask("OpenCL.std ldexp operands are not value types"))
  }

  private def binary(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      left: Operand,
      right: Operand
  )(operation: (Long, Long) => Long): Either[Fault, Unit] = {
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
      InstructionCompleted
    } else
      (bits(values, left), bits(values, right)) match {
        case (Right(a), Right(b)) =>
          val width = scalarWidth(program, id(resultType))
          val size  = math.max(a.length, b.length)
          val lanes = Array.tabulate(size)(index => operation(a(index % a.length), b(index % b.length)) & mask(width))
          values.update(id(destination), RuntimeValue.Bits(id(resultType), lanes))
          InstructionCompleted
        case (Left(error), _) => Left(error)
        case (_, Left(error)) => Left(error)
      }
  }

  private def integerShift(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      base: Operand,
      shift: Operand,
      opcode: String
  )(operation: (Long, Long, BitWidth) => Long): Either[Fault, Unit] =
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
              Fault.InvalidTask(
                s"$opcode requires an integer scalar/vector result and base of the same type, " +
                  "plus an integer shift operand with the same lane count"
              )
            )
        }
      case (None, _) => Left(undefined(base))
      case (_, None) => Left(undefined(shift))
      case _         =>
        Left(
          Fault.InvalidTask(
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
  )(predicate: (Long, Long) => Boolean): Either[Fault, Unit] = {
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
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      left: Operand,
      right: Operand,
      opcode: String
  )(predicate: (Long, Long) => Boolean): Either[Fault, Unit] = {
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
                  Fault.InvalidTask(
                    s"$opcode left operand %$leftId is not an integer scalar or vector"
                  )
                )
              case (_, None, _) =>
                Left(
                  Fault.InvalidTask(
                    s"$opcode right operand %$rightId is not an integer scalar or vector"
                  )
                )
              case (_, _, None) =>
                Left(
                  Fault.InvalidTask(
                    s"$opcode result type %${id(resultType)} is not a boolean scalar or vector"
                  )
                )
              case (Some(leftShape), Some(rightShape), Some(resultLanes)) =>
                Left(
                  Fault.InvalidTask(
                    s"$opcode type shapes do not match: left $leftShape, right $rightShape, result lanes $resultLanes"
                  )
                )
            }
          case (Some(RuntimeValue.Address(_, _)), _) =>
            Left(Fault.InvalidTask(s"$opcode left operand %$leftId is a pointer"))
          case (_, Some(RuntimeValue.Address(_, _))) =>
            Left(Fault.InvalidTask(s"$opcode right operand %$rightId is a pointer"))
          case (None, _) => Left(undefined(left))
          case (_, None) => Left(undefined(right))
        }
    }
  }

  private def builtinValue(
      program: Program,
      typeId: Int,
      builtin: Int,
      invocation: Invocation
  ): Either[Fault, RuntimeValue] = {
    val lanes: Either[Fault, Array[Long]] = builtin match {
      case BuiltIn.NumWorkgroups        => Right(invocation.numWorkgroups)
      case BuiltIn.WorkgroupSize        => Right(invocation.workgroupSize)
      case BuiltIn.WorkgroupId          => Right(invocation.group)
      case BuiltIn.LocalInvocationId    => Right(invocation.local)
      case BuiltIn.GlobalInvocationId   => Right(invocation.global)
      case BuiltIn.LocalInvocationIndex => Right(Array(
          (invocation.localZ * invocation.workgroupSizeY + invocation.localY) * invocation.workgroupSizeX + invocation.localX
        ))
      case BuiltIn.WorkDim               => Right(Array(invocation.workDim.toLong))
      case BuiltIn.GlobalSize            => Right(invocation.globalSize)
      case BuiltIn.EnqueuedWorkgroupSize => Right(invocation.enqueuedWorkgroupSize)
      case BuiltIn.GlobalOffset          => Right(invocation.globalOffset)
      case BuiltIn.GlobalLinearId        => Right(
          Array(
            (invocation.globalX - invocation.globalOffsetX) + invocation.globalSizeX *
              ((invocation.globalY - invocation.globalOffsetY) +
                invocation.globalSizeY * (invocation.globalZ - invocation.globalOffsetZ))
          )
        )
      case BuiltIn.SubgroupSize              => Right(Array(invocation.subgroupSize.toLong))
      case BuiltIn.SubgroupMaxSize           => Right(Array(invocation.maxSubgroupSize.toLong))
      case BuiltIn.NumSubgroups              => Right(Array(invocation.numSubgroups))
      case BuiltIn.NumEnqueuedSubgroups      => Right(Array(invocation.enqueuedSubgroups))
      case BuiltIn.SubgroupId                => Right(Array(invocation.subgroupIndex.toLong))
      case BuiltIn.SubgroupLocalInvocationId => Right(Array(invocation.subgroupLocal.toLong))
      case value                             => Left(Fault.Unsupported(s"SPIR-V BuiltIn $value is not implemented"))
    }
    lanes.flatMap(values =>
      integerShape(program, typeId) match {
        case Some((width, count)) if count == values.length =>
          Right(RuntimeValue.Bits(typeId, values.map(_ & mask(width))))
        case Some((_, count)) =>
          Left(
            Fault.InvalidTask(
              s"SPIR-V BuiltIn $builtin requires ${values.length} integer lanes, result type %$typeId has $count"
            )
          )
        case None =>
          Left(
            Fault.InvalidTask(
              s"SPIR-V BuiltIn $builtin result type %$typeId is not an integer scalar or vector"
            )
          )
      }
    )
  }

  private def decode(program: Program, typeId: Int, bytes: Array[Byte]): Either[Fault, RuntimeValue] =
    scalarLeafCount(program, typeId).flatMap { count =>
      try {
        val lanes = Array.ofDim[Long](count)
        val used  = decodeBits(program, typeId, bytes, 0, lanes, 0)
        if (used != lanes.length)
          Left(Fault.InvalidTask(
            s"SPIR-V value of type %$typeId decoded $used scalar leaves, expected ${lanes.length}"
          ))
        else Right(RuntimeValue.Bits(typeId, lanes))
      } catch {
        case error: IllegalArgumentException => Left(Fault.InvalidTask(error.getMessage))
      }
    }

  // Dense scalar/vector decoding writes output lanes in place.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def decodeBits(
      program: Program,
      typeId: Int,
      input: Array[Byte],
      byteStart: Int,
      lanes: Array[Long],
      laneStart: Int
  ): Int = program.runtimeType(typeId) match {
    case Some(RuntimeType.Bool) | Some(RuntimeType.Integer(_, _)) | Some(RuntimeType.Floating(_)) |
        Some(RuntimeType.Event) =>
      if (laneStart >= lanes.length)
        throw IllegalArgumentException(s"SPIR-V value of type %$typeId has too many scalar leaves")
      val width     = scalarWidth(program, typeId)
      val byteCount = ScalarByteWidth.applyUnsafe(width.value / 8)
      val offset    = Math.min(byteStart, input.length)
      val available = ScalarByteWidth.applyUnsafe(Math.min(byteCount.value, input.length - offset))
      lanes(laneStart) = LittleEndian.decode(input, offset, available) & mask(width)
      laneStart + 1
    case Some(RuntimeType.Pointer(_, _)) =>
      if (laneStart >= lanes.length)
        throw IllegalArgumentException(s"SPIR-V value of type %$typeId has too many pointer leaves")
      val offset    = Math.min(byteStart, input.length)
      val available = ScalarByteWidth.applyUnsafe(Math.min(8, input.length - offset))
      lanes(laneStart) = LittleEndian.decode(input, offset, available)
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

  private def encode(program: Program, value: RuntimeValue): Either[Fault, Array[Byte]] = value match {
    case RuntimeValue.Bits(typeId, lanes) =>
      try {
        val bytes = Array.ofDim[Byte](typeSize(program, typeId))
        val used  = encodeBits(program, typeId, lanes, 0, bytes, 0)
        if (used != lanes.length)
          Left(Fault.InvalidTask(
            s"SPIR-V value of type %$typeId has ${lanes.length} scalar leaves, expected $used"
          ))
        else Right(bytes)
      } catch {
        case error: IllegalArgumentException => Left(Fault.InvalidTask(error.getMessage))
      }
    case RuntimeValue.Address(_, pointer) =>
      Left(Fault.InvalidTask(s"cannot encode runtime pointer $pointer as process-neutral bytes"))
  }

  // Dense scalar/vector encoding writes output lanes in place.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def encodeBits(
      program: Program,
      typeId: Int,
      lanes: Array[Long],
      laneStart: Int,
      output: Array[Byte],
      byteStart: Int
  ): Int = program.runtimeType(typeId) match {
    case Some(RuntimeType.Bool) | Some(RuntimeType.Integer(_, _)) | Some(RuntimeType.Floating(_)) |
        Some(RuntimeType.Event) =>
      if (laneStart >= lanes.length)
        throw IllegalArgumentException(s"SPIR-V value of type %$typeId has too few scalar leaves")
      val width = scalarWidth(program, typeId)
      LittleEndian.write(
        lanes(laneStart),
        output,
        byteStart,
        ScalarByteWidth.applyUnsafe(width.value / 8)
      )
      laneStart + 1
    case Some(RuntimeType.Pointer(_, _)) =>
      if (laneStart >= lanes.length)
        throw IllegalArgumentException(s"SPIR-V value of type %$typeId has too few pointer leaves")
      LittleEndian.write(lanes(laneStart), output, byteStart, ScalarByteWidth.Qword)
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

  private def typeSize(program: Program, typeId: Int): Int =
    program.cachedTypeSize(typeId)(typeSizeUncached(program, typeId))

  private def nonNegativeTypeSize(
      program: Program,
      typeId: Int
  ): Either[Fault, Int :| Positive0] = {
    val size = typeSize(program, typeId)
    size.refineEither[Positive0]
      .leftMap(_ =>
        Fault.InvalidTask(s"SPIR-V type %$typeId has a negative byte size: $size")
      )
  }

  private def scalarMemoryWidth(
      program: Program,
      typeId: Int
  ): Either[Fault, MemoryValueByteWidth] = {
    val size = typeSize(program, typeId)
    size.refineEither[Interval.Closed[1, 8]]
      .leftMap(_ =>
        Fault.InvalidTask(
          s"SPIR-V scalar type %$typeId has invalid byte width $size"
        )
      )
      .map(size => MemoryValueByteWidth(size))
  }

  private def typeSizeUncached(program: Program, typeId: Int): Int = program.runtimeType(typeId) match {
    case Some(RuntimeType.Bool)                     => 1
    case Some(RuntimeType.Integer(width, _))        => width.value / 8
    case Some(RuntimeType.Floating(width))          => width.value / 8
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

  // Struct layout carries offset/alignment state and uses the packed-layout fast exit.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return", "scalafix:DisableSyntax.var"))
  private def structSize(program: Program, structureType: Int, members: Vec[Int]): Int = {
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

  // Struct member layout carries offset/alignment state through one ordered scan.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def structMemberOffset(
      program: Program,
      structureType: Int,
      members: Vec[Int],
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

  private def typeAlignment(program: Program, typeId: Int): Int =
    program.typeAlignmentCache.getOrElseUpdate(typeId, typeAlignmentUncached(program, typeId))

  private def typeAlignmentUncached(program: Program, typeId: Int): Int = program.runtimeType(typeId) match {
    case Some(RuntimeType.Structure(_)) if program.packedStructures.contains(typeId) => 1
    case Some(RuntimeType.Structure(members))           => members.map(typeAlignment(program, _)).maxOption.getOrElse(1)
    case Some(RuntimeType.Array(element, _))            => typeAlignment(program, element)
    case Some(RuntimeType.VectorType(component, count)) =>
      Math.multiplyExact(typeAlignment(program, component), if (count == 3) 4 else count)
    case Some(RuntimeType.Pointer(_, _)) => 8
    case _                               => math.max(1, typeSize(program, typeId))
  }

  private def scalarWidth(program: Program, typeId: Int): BitWidth = program.runtimeType(typeId) match {
    case Some(RuntimeType.Bool)                     => BitWidth(8)
    case Some(RuntimeType.Integer(width, _))        => width
    case Some(RuntimeType.Floating(width))          => width.bitWidth
    case Some(RuntimeType.Event)                    => BitWidth(64)
    case Some(RuntimeType.VectorType(component, _)) => scalarWidth(program, component)
    case value => throw IllegalArgumentException(s"SPIR-V type %$typeId is not scalar or vector: $value")
  }

  private def pointerPointee(program: Program, typeId: Int): Int = program.runtimeType(typeId) match {
    case Some(RuntimeType.Pointer(_, pointee)) => pointee
    case value => throw IllegalArgumentException(s"SPIR-V type %$typeId is not a pointer: $value")
  }

  private def address(values: InvocationValues, operand: Operand): Either[Fault, Pointer] =
    values.get(id(operand)) match {
      case Some(RuntimeValue.Address(_, value)) => Right(value)
      case Some(_)                              =>
        Left(Fault.InvalidTask(s"SPIR-V value %${id(operand)} is not a pointer"))
      case None => Left(undefined(operand))
    }

  /** Lifetime instructions are semantic markers, but accepting a malformed marker would hide a corrupt module. Resolve
    * the object identity and validate the literal byte range without reading external allocation contents.
    */
  private def validateLifetime(
      program: Program,
      values: InvocationValues,
      opcode: String,
      pointerOperand: Operand,
      sizeOperand: Operand
  ): Either[Fault, Unit] =
    (pointerOperand.words, sizeOperand.words) match {
      case (Vec(pointerId), Vec(sizeWord)) => values.get(pointerId.bits) match {
          case Some(RuntimeValue.Address(pointerType, pointer)) => program.runtimeType(pointerType) match {
              case Some(RuntimeType.Pointer(_, pointee)) if pointee == pointer.pointee =>
                val size = sizeWord.toUnsigned
                pointer.region match {
                  case Region.Internal(internal) if size != 0L =>
                    val end = pointer.offset + size
                    Either.cond(
                      pointer.offset >= 0L && end >= pointer.offset && end <= internal.bytes.length.toLong,
                      (),
                      Fault.InvalidTask(
                        s"$opcode byte range ${pointer.offset} + $size exceeds internal object of ${internal.bytes.length} bytes"
                      )
                    )
                  case _ => Right(())
                }
              case Some(_) =>
                Left(Fault.InvalidTask(
                  s"$opcode target %${pointerId.bits} does not have pointer type"
                ))
              case None =>
                Left(Fault.InvalidTask(
                  s"$opcode target %${pointerId.bits} has undefined pointer type"
                ))
            }
          case Some(_) => Left(Fault.InvalidTask(
              s"$opcode target %${pointerId.bits} is not a pointer"
            ))
          case None => Left(
              Fault.InvalidTask(s"$opcode target %${pointerId.bits} is undefined")
            )
        }
      case (pointerWords, _) if pointerWords.size != 1 =>
        Left(Fault.InvalidTask(
          s"$opcode pointer must contain one word, found ${pointerWords.size}"
        ))
      case (_, sizeWords) =>
        Left(Fault.InvalidTask(
          s"$opcode size must contain one word, found ${sizeWords.size}"
        ))
    }

  private def bits(values: InvocationValues, operand: Operand): Either[Fault, Array[Long]] =
    values.get(id(operand)) match {
      case Some(RuntimeValue.Bits(_, value)) => Right(value)
      case Some(_)                           =>
        Left(Fault.InvalidTask(s"SPIR-V value %${id(operand)} is not numeric"))
      case None => Left(undefined(operand))
    }

  private def scalar(values: InvocationValues, operand: Operand): Either[Fault, Long] =
    if (values.scalarType(id(operand)) >= 0) Right(values.scalarBits(id(operand)))
    else
      bits(values, operand).flatMap {
        case Array(value) => Right(value)
        case value        => Left(Fault.InvalidTask(
            s"SPIR-V value %${id(operand)} has ${value.length} lanes"
          ))
      }

  private def retag(value: RuntimeValue, typeId: Int): RuntimeValue = value match {
    case RuntimeValue.Bits(_, lanes)      => RuntimeValue.Bits(typeId, lanes.clone())
    case RuntimeValue.Address(_, pointer) => RuntimeValue.Address(typeId, pointer)
  }

  // Bitcast packs and unpacks lane bits without allocating intermediate collections.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def bitcast(
      program: Program,
      sourceType: Int,
      resultType: Int,
      sourceLanes: Array[Long]
  ): Either[Fault, Array[Long]] = {
    val sourceShape = numericShape(program, sourceType)
    val resultShape = numericShape(program, resultType)
    (sourceShape, resultShape) match {
      case (Some((sourceWidth, sourceCount)), Some((resultWidth, resultCount)))
          if sourceLanes.length == sourceCount &&
            sourceWidth.value * sourceCount == resultWidth.value * resultCount =>
        val result     = Array.ofDim[Long](resultCount)
        var resultLane = 0
        while (resultLane < resultCount) {
          var bits = 0L
          var bit  = 0
          while (bit < resultWidth.value) {
            val sourceBit    = resultLane * resultWidth.value + bit
            val sourceLane   = sourceBit / sourceWidth.value
            val sourceOffset = sourceBit % sourceWidth.value
            if (((sourceLanes(sourceLane) >>> sourceOffset) & 1L) != 0L) bits |= 1L << bit
            bit += 1
          }
          result(resultLane) = bits & mask(resultWidth)
          resultLane += 1
        }
        Right(result)
      case (Some((sourceWidth, sourceCount)), Some((resultWidth, resultCount))) =>
        Left(
          Fault.InvalidTask(
            s"OpBitcast source %$sourceType has $sourceCount x $sourceWidth bits and result %$resultType " +
              s"has $resultCount x $resultWidth bits (value has ${sourceLanes.length} lanes)"
          )
        )
      case (None, _) =>
        Left(Fault.InvalidTask(
          s"OpBitcast source type %$sourceType is not scalar/vector numeric data"
        ))
      case (_, None) =>
        Left(Fault.InvalidTask(
          s"OpBitcast result type %$resultType is not scalar/vector numeric data"
        ))
    }
  }

  private def numericShape(program: Program, typeId: Int): Option[(BitWidth, Int)] =
    integerShape(program, typeId)
      .orElse(floatingShape(program, typeId).map((width, count) => width.bitWidth -> count))
      .orElse {
        program.runtimeType(typeId) match {
          case Some(RuntimeType.Bool) => Some((BitWidth(8), 1))
          case Some(RuntimeType.VectorType(component, count))
              if program.runtimeType(component).contains(RuntimeType.Bool) => Some((BitWidth(8), count))
          case _ => None
        }
      }

  // Phi lookup scans predecessor pairs and exits at the matching edge or malformed operand.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return", "scalafix:DisableSyntax.var"))
  private def phiIncoming(
      incoming: Vec[Operand],
      predecessor: Int
  ): Either[Fault, Option[Int]] = {
    var selected = -1
    var index    = 0
    while (index < incoming.size) {
      val pair = incoming(index)
      pair.words match {
        case Vec(value, parent) => if (parent.bits == predecessor) selected = value.bits
        case words              =>
          return Left(
            Fault.InvalidTask(
              s"OpPhi ${pair.kind} operand must contain a value and parent label, found ${words.size} words"
            )
          )
      }
      index += 1
    }
    Right(if (selected < 0) None else Some(selected))
  }

  private def convertSignedToFloat(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand
  ): Either[Fault, Unit] = values.get(id(source)) match {
    case Some(RuntimeValue.Bits(sourceType, lanes)) =>
      (integerShape(program, sourceType), floatingShape(program, id(resultType))) match {
        case (Some((sourceWidth, sourceLanes)), Some((resultWidth, resultLanes))) if sourceLanes == resultLanes =>
          val converted = lanes.map { raw =>
            val signed = signExtend(raw, sourceWidth)
            doubleToFloating(resultWidth, signed.toDouble)
          }
          values.update(id(destination), RuntimeValue.Bits(id(resultType), converted))
          Right(())
        case (None, _) =>
          Left(
            Fault.InvalidTask(
              s"OpConvertSToF source %${id(source)} is not an integer scalar or vector"
            )
          )
        case (_, None) =>
          Left(
            Fault.InvalidTask(
              s"OpConvertSToF result type %${id(resultType)} is not a floating-point scalar or vector"
            )
          )
        case (Some((_, sourceLanes)), Some((_, resultLanes))) =>
          Left(
            Fault.InvalidTask(
              s"OpConvertSToF cannot convert $sourceLanes source lanes to $resultLanes result lanes"
            )
          )
      }
    case Some(_) =>
      Left(Fault.InvalidTask(s"OpConvertSToF source %${id(source)} is a pointer"))
    case None => Left(undefined(source))
  }

  private def convertUnsignedToFloat(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand
  ): Either[Fault, Unit] = values.get(id(source)) match {
    case Some(RuntimeValue.Bits(sourceType, lanes)) =>
      (integerShape(program, sourceType), floatingShape(program, id(resultType))) match {
        case (Some((sourceWidth, sourceLanes)), Some((resultWidth, resultLanes))) if sourceLanes == resultLanes =>
          val converted = lanes.map { raw =>
            val unsigned = sourceWidth.value match {
              case 64 if raw < 0L => (raw >>> 1).toDouble * 2.0 + (raw & 1L).toDouble
              case _              => (raw & mask(sourceWidth)).toDouble
            }
            doubleToFloating(resultWidth, unsigned)
          }
          values.update(id(destination), RuntimeValue.Bits(id(resultType), converted))
          Right(())
        case (None, _) =>
          Left(
            Fault.InvalidTask(
              s"OpConvertUToF source %${id(source)} is not an integer scalar or vector"
            )
          )
        case (_, None) =>
          Left(
            Fault.InvalidTask(
              s"OpConvertUToF result type %${id(resultType)} is not a floating-point scalar or vector"
            )
          )
        case (Some((_, sourceLanes)), Some((_, resultLanes))) =>
          Left(
            Fault.InvalidTask(
              s"OpConvertUToF cannot convert $sourceLanes source lanes to $resultLanes result lanes"
            )
          )
      }
    case Some(_) =>
      Left(Fault.InvalidTask(s"OpConvertUToF source %${id(source)} is a pointer"))
    case None => Left(undefined(source))
  }

  private def signedConvert(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand
  ): Either[Fault, Unit] = values.get(id(source)) match {
    case Some(RuntimeValue.Bits(sourceType, lanes)) =>
      (integerShape(program, sourceType), integerShape(program, id(resultType))) match {
        case (Some((sourceWidth, sourceLanes)), Some((resultWidth, resultLanes))) if sourceLanes == resultLanes =>
          values.update(
            id(destination),
            RuntimeValue.Bits(id(resultType), lanes.map(value => signExtend(value, sourceWidth) & mask(resultWidth)))
          )
          Right(())
        case (None, _) =>
          Left(Fault.InvalidTask(
            s"OpSConvert source %${id(source)} is not an integer scalar or vector"
          ))
        case (_, None) =>
          Left(
            Fault.InvalidTask(
              s"OpSConvert result type %${id(resultType)} is not an integer scalar or vector"
            )
          )
        case (Some((_, sourceLanes)), Some((_, resultLanes))) =>
          Left(
            Fault.InvalidTask(
              s"OpSConvert cannot convert $sourceLanes source lanes to $resultLanes result lanes"
            )
          )
      }
    case Some(_) =>
      Left(Fault.InvalidTask(s"OpSConvert source %${id(source)} is a pointer"))
    case None => Left(undefined(source))
  }

  private def signedNegate(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand
  ): Either[Fault, Unit] = values.get(id(source)) match {
    case Some(RuntimeValue.Bits(sourceType, lanes)) =>
      (integerShape(program, sourceType), integerShape(program, id(resultType))) match {
        case (Some(sourceShape @ (width, count)), Some(resultShape))
            if sourceShape == resultShape && lanes.length == count =>
          values.update(id(destination), RuntimeValue.Bits(id(resultType), lanes.map(raw => -raw & mask(width))))
          Right(())
        case _ => Left(Fault.InvalidTask("OpSNegate requires matching integer scalar/vector operand and result types"))
      }
    case Some(_) => Left(Fault.InvalidTask("OpSNegate source is a pointer"))
    case None    => Left(undefined(source))
  }

  private def floatingClassify(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand,
      classification: FloatingClassification
  ): Either[Fault, Unit] = values.get(id(source)) match {
    case Some(RuntimeValue.Bits(sourceType, lanes)) =>
      (floatingShape(program, sourceType), booleanLanes(program, id(resultType))) match {
        case (Some((width, sourceLanes)), Some(resultLanes)) if sourceLanes == resultLanes =>
          val result = lanes.map { raw =>
            val (signMask, exponentMask, fractionMask) = width.value match {
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
            Fault.InvalidTask(
              s"${classification.opcode} source %${id(source)} is not a floating-point scalar or vector"
            )
          )
        case (_, None) =>
          Left(
            Fault.InvalidTask(
              s"${classification.opcode} result type %${id(resultType)} is not a boolean scalar or vector"
            )
          )
        case (Some((_, sourceLanes)), Some(resultLanes)) =>
          Left(
            Fault.InvalidTask(
              s"${classification.opcode} cannot classify $sourceLanes lanes into $resultLanes lanes"
            )
          )
      }
    case Some(_) =>
      Left(Fault.InvalidTask(s"${classification.opcode} source is a pointer"))
    case None => Left(undefined(source))
  }

  private def floatingNegate(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand
  ): Either[Fault, Unit] = values.get(id(source)) match {
    case Some(RuntimeValue.Bits(sourceType, lanes)) =>
      (floatingShape(program, sourceType), floatingShape(program, id(resultType))) match {
        case (Some(sourceShape @ (width, _)), Some(resultShape)) if sourceShape == resultShape =>
          val signMask = width.value match {
            case 16 => 0x8000L
            case 32 => 0x80000000L
            case 64 => 0x8000000000000000L
          }
          values.update(id(destination), RuntimeValue.Bits(id(resultType), lanes.map(_ ^ signMask)))
          Right(())
        case _ =>
          Left(
            Fault.InvalidTask(
              "OpFNegate requires matching floating-point scalar/vector operand and result types"
            )
          )
      }
    case Some(_) => Left(Fault.InvalidTask("OpFNegate source is a pointer"))
    case None    => Left(undefined(source))
  }

  private def floatingVectorTimesScalar(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      vector: Operand,
      scalar: Operand
  ): Either[Fault, Unit] = (values.get(id(vector)), values.get(id(scalar))) match {
    case (Some(RuntimeValue.Bits(vectorType, vectorLanes)), Some(RuntimeValue.Bits(scalarType, Array(scalarRaw)))) =>
      (
        floatingShape(program, vectorType),
        floatingShape(program, scalarType),
        floatingShape(program, id(resultType))
      ) match {
        case (Some((width, count)), Some((scalarWidth, 1)), Some((resultWidth, resultCount)))
            if width == scalarWidth && width == resultWidth && count == resultCount && count > 1
              && vectorLanes.length == count =>
          val multiplier = floatingToDouble(width, scalarRaw)
          val result     = vectorLanes.map(raw => doubleToFloating(width, floatingToDouble(width, raw) * multiplier))
          values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
          Right(())
        case _ => Left(Fault.InvalidTask("OpVectorTimesScalar requires a floating vector and matching scalar"))
      }
    case (Some(_), Some(_)) => Left(Fault.InvalidTask("OpVectorTimesScalar requires a floating vector and scalar"))
    case (None, _)          => Left(undefined(vector))
    case (_, None)          => Left(undefined(scalar))
  }

  private def floatingBinary(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      left: Operand,
      right: Operand,
      operation: FloatingOperation
  ): Either[Fault, Unit] = (values.get(id(left)), values.get(id(right))) match {
    case (Some(RuntimeValue.Bits(leftType, leftLanes)), Some(RuntimeValue.Bits(rightType, rightLanes))) =>
      (
        floatingShape(program, id(resultType)),
        floatingShape(program, leftType),
        floatingShape(program, rightType)
      ) match {
        case (Some((width, count)), Some((leftWidth, leftCount)), Some((rightWidth, rightCount)))
            if width == leftWidth && width == rightWidth && count == leftCount && count == rightCount =>
          val result = Array.tabulate(count) { index =>
            width.value match {
              case 16 =>
                val a = IEEE754.binary16ToFloat(Binary16.low(leftLanes(index)))
                val b = IEEE754.binary16ToFloat(Binary16.low(rightLanes(index)))
                IEEE754.floatToBinary16(
                  operation match {
                    case FloatingOperation.Add      => a + b
                    case FloatingOperation.Subtract => a - b
                    case FloatingOperation.Multiply => a * b
                    case FloatingOperation.Divide   => a / b
                    case FloatingOperation.Minimum  => floatingMinimum(a, b)
                    case FloatingOperation.Maximum  => floatingMaximum(a, b)
                  }
                ).value
              case 32 =>
                val a = IEEE754.binary32ToFloat(Binary32.low(leftLanes(index)))
                val b = IEEE754.binary32ToFloat(Binary32.low(rightLanes(index)))
                IEEE754.floatToBinary32(
                  operation match {
                    case FloatingOperation.Add      => a + b
                    case FloatingOperation.Subtract => a - b
                    case FloatingOperation.Multiply => a * b
                    case FloatingOperation.Divide   => a / b
                    case FloatingOperation.Minimum  => floatingMinimum(a, b)
                    case FloatingOperation.Maximum  => floatingMaximum(a, b)
                  }
                ).value
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
            Fault.InvalidTask(
              s"${operation.opcode} result type %${id(resultType)} is not a floating-point scalar or vector"
            )
          )
        case (_, None, _) =>
          Left(Fault.InvalidTask(
            s"${operation.opcode} left operand %${id(left)} is not floating-point"
          ))
        case (_, _, None) =>
          Left(
            Fault.InvalidTask(
              s"${operation.opcode} right operand %${id(right)} is not floating-point"
            )
          )
        case (Some(result), Some(leftShape), Some(rightShape)) =>
          Left(
            Fault.InvalidTask(
              s"${operation.opcode} type shapes do not match: result $result, left $leftShape, right $rightShape"
            )
          )
      }
    case (Some(RuntimeValue.Address(_, _)), _) =>
      Left(Fault.InvalidTask(
        s"${operation.opcode} left operand %${id(left)} is a pointer"
      ))
    case (_, Some(RuntimeValue.Address(_, _))) =>
      Left(Fault.InvalidTask(
        s"${operation.opcode} right operand %${id(right)} is a pointer"
      ))
    case (None, _) => Left(undefined(left))
    case (_, None) => Left(undefined(right))
  }

  private def convertFloating(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand
  ): Either[Fault, Unit] = values.get(id(source)) match {
    case Some(RuntimeValue.Bits(sourceType, lanes)) =>
      (floatingShape(program, sourceType), floatingShape(program, id(resultType))) match {
        case (Some((sourceWidth, sourceLanes)), Some((resultWidth, resultLanes)))
            if sourceLanes == resultLanes && lanes.length == sourceLanes =>
          val converted = lanes.map { raw =>
            doubleToFloating(resultWidth, floatingToDouble(sourceWidth, raw))
          }
          values.update(id(destination), RuntimeValue.Bits(id(resultType), converted))
          Right(())
        case (None, _) =>
          Left(Fault.InvalidTask(
            s"OpFConvert source %${id(source)} is not a floating-point scalar or vector"
          ))
        case (_, None) =>
          Left(
            Fault.InvalidTask(
              s"OpFConvert result type %${id(resultType)} is not a floating-point scalar or vector"
            )
          )
        case (Some((_, sourceLanes)), Some((_, resultLanes))) =>
          Left(
            Fault.InvalidTask(
              s"OpFConvert cannot convert $sourceLanes source lanes to $resultLanes result lanes"
            )
          )
      }
    case Some(_) =>
      Left(Fault.InvalidTask(s"OpFConvert source %${id(source)} is a pointer"))
    case None => Left(undefined(source))
  }

  /** FPRoundingMode decoration values: RTE, RTZ, RTP, RTN. */
  private def roundedToLong(value: Double, mode: Option[Int]): Long = mode match {
    case Some(0) => math.rint(value).toLong
    case Some(1) => if (value < 0) math.ceil(value).toLong else math.floor(value).toLong
    case Some(2) => math.ceil(value).toLong
    case Some(3) => math.floor(value).toLong
    case _       => value.toLong
  }

  private def convertFloatToSigned(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand
  ): Either[Fault, Unit] = values.get(id(source)) match {
    case Some(RuntimeValue.Bits(sourceType, lanes)) =>
      (floatingShape(program, sourceType), integerShape(program, id(resultType))) match {
        case (Some((sourceWidth, sourceLanes)), Some((resultWidth, resultLanes))) if sourceLanes == resultLanes =>
          val mode      = program.ir.roundingModes.get(id(destination))
          val converted = lanes.map { raw =>
            val floating = floatingToDouble(sourceWidth, raw)
            val signed   =
              if (program.saturatedConversions.contains(id(destination))) {
                val minimum = if (resultWidth.value == 64) Long.MinValue else -(1L << (resultWidth.value - 1))
                val maximum = if (resultWidth.value == 64) Long.MaxValue else (1L << (resultWidth.value - 1)) - 1L
                if (floating.isNaN) 0L
                else if (floating <= minimum.toDouble) minimum
                else if (floating >= maximum.toDouble) maximum
                else roundedToLong(floating, mode)
              } else roundedToLong(floating, mode)
            signed & mask(resultWidth)
          }
          values.update(id(destination), RuntimeValue.Bits(id(resultType), converted))
          Right(())
        case (None, _) =>
          Left(
            Fault.InvalidTask(
              s"OpConvertFToS source %${id(source)} is not a floating-point scalar or vector"
            )
          )
        case (_, None) =>
          Left(
            Fault.InvalidTask(
              s"OpConvertFToS result type %${id(resultType)} is not an integer scalar or vector"
            )
          )
        case (Some((_, sourceLanes)), Some((_, resultLanes))) =>
          Left(
            Fault.InvalidTask(
              s"OpConvertFToS cannot convert $sourceLanes source lanes to $resultLanes result lanes"
            )
          )
      }
    case Some(_) =>
      Left(Fault.InvalidTask(s"OpConvertFToS source %${id(source)} is a pointer"))
    case None => Left(undefined(source))
  }

  // Unsigned conversion scans vector lanes and stops before publishing an invalid divisor/range result.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return", "scalafix:DisableSyntax.var"))
  private def convertFloatToUnsigned(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand
  ): Either[Fault, Unit] = values.get(id(source)) match {
    case Some(RuntimeValue.Bits(sourceType, lanes)) =>
      (floatingShape(program, sourceType), integerShape(program, id(resultType))) match {
        case (Some((sourceWidth, sourceLanes)), Some((resultWidth, resultLanes))) if sourceLanes == resultLanes =>
          val saturate  = program.saturatedConversions.contains(id(destination))
          val mode      = program.ir.roundingModes.get(id(destination))
          val converted = Array.ofDim[Long](lanes.length)
          var index     = 0
          while (index < lanes.length) {
            if (saturate) {
              val floating  = floatingToDouble(sourceWidth, lanes(index))
              val saturated =
                if (floating.isNaN || floating <= 0.0) 0L
                else if (resultWidth.value >= 64) roundedToLong(floating, mode)
                else {
                  val maximum = (1L << resultWidth.value) - 1L
                  if (floating >= maximum.toDouble) maximum else roundedToLong(floating, mode)
                }
              converted(index) = saturated & mask(resultWidth)
            } else
              floatingToUnsigned(lanes(index), sourceWidth, resultWidth) match {
                case Left(detail) =>
                  return Left(
                    Fault.InvalidTask(
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
            Fault.InvalidTask(
              s"OpConvertFToU source %${id(source)} is not a floating-point scalar or vector"
            )
          )
        case (_, None) =>
          Left(
            Fault.InvalidTask(
              s"OpConvertFToU result type %${id(resultType)} is not an integer scalar or vector"
            )
          )
        case (Some((_, sourceLanes)), Some((_, resultLanes))) =>
          Left(
            Fault.InvalidTask(
              s"OpConvertFToU cannot convert $sourceLanes source lanes to $resultLanes result lanes"
            )
          )
      }
    case Some(_) =>
      Left(Fault.InvalidTask(s"OpConvertFToU source %${id(source)} is a pointer"))
    case None => Left(undefined(source))
  }

  /** Converts an IEEE-754 bit pattern by truncating toward zero without first narrowing through a signed Long. */
  private def floatingToUnsigned(
      raw: Long,
      sourceWidth: FloatingPointWidth,
      resultWidth: BitWidth
  ): Either[String, Long] = {
    val (exponentBits, fractionBits, bias) = sourceWidth.value match {
      case 16 => (5, 10, 15)
      case 32 => (8, 23, 127)
      case 64 => (11, 52, 1023)
    }
    val bits            = raw & mask(sourceWidth.bitWidth)
    val fractionMask    = (1L << fractionBits) - 1L
    val exponentMask    = (1L << exponentBits) - 1L
    val fraction        = bits & fractionMask
    val encodedExponent = (bits >>> fractionBits) & exponentMask
    val negative        = ((bits >>> (sourceWidth.value - 1)) & 1L) != 0L
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
      if (integer.bitLength > resultWidth.value) Left("value exceeds the result width")
      else Right(integer.longValue & mask(resultWidth))
    }
  }

  private def integerEquality(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      left: Operand,
      right: Operand,
      opcode: String
  )(predicate: (Long, Long) => Boolean): Either[Fault, Unit] = {
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
                  Fault.InvalidTask(
                    s"$opcode left operand %$leftId is not an integer scalar or vector"
                  )
                )
              case (_, None, _) =>
                Left(
                  Fault.InvalidTask(
                    s"$opcode right operand %$rightId is not an integer scalar or vector"
                  )
                )
              case (_, _, None) =>
                Left(
                  Fault.InvalidTask(
                    s"$opcode result type %${id(resultType)} is not a boolean scalar or vector"
                  )
                )
              case (Some(leftShape), Some(rightShape), Some(resultLanes)) =>
                Left(
                  Fault.InvalidTask(
                    s"$opcode type shapes do not match: left $leftShape, right $rightShape, result lanes $resultLanes"
                  )
                )
            }
          case (Some(RuntimeValue.Address(_, _)), _) =>
            Left(Fault.InvalidTask(s"$opcode left operand %$leftId is a pointer"))
          case (_, Some(RuntimeValue.Address(_, _))) =>
            Left(Fault.InvalidTask(s"$opcode right operand %$rightId is a pointer"))
          case (None, _) => Left(undefined(left))
          case (_, None) => Left(undefined(right))
        }
    }
  }

  private def integerBitwise(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      left: Operand,
      right: Operand,
      opcode: String
  )(operation: (Long, Long) => Long): Either[Fault, Unit] = {
    val resultId = id(resultType)
    val leftId   = id(left)
    val rightId  = id(right)
    integerShape(program, resultId) match {
      case None =>
        Left(Fault.InvalidTask(
          s"$opcode result type %$resultId is not an integer scalar or vector"
        ))
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
                Fault.InvalidTask(
                  s"$opcode requires matching integer scalar or vector operand and result types"
                )
              )
          }
    }
  }

  private def integerNot(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand
  ): Either[Fault, Unit] = {
    val resultId = id(resultType)
    val sourceId = id(source)
    integerShape(program, resultId) match {
      case None =>
        Left(Fault.InvalidTask(
          s"OpNot result type %$resultId is not an integer scalar or vector"
        ))
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
              Fault.InvalidTask(
                "OpNot requires matching integer scalar or vector operand and result types"
              )
            )
          case None => Left(undefined(source))
        }
    }
  }

  private def integerBitCount(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      base: Operand
  ): Either[Fault, Unit] = {
    val resultId = id(resultType)
    val baseId   = id(base)
    integerShape(program, resultId) match {
      case None =>
        Left(Fault.InvalidTask(
          s"OpBitCount result type %$resultId is not an integer scalar or vector"
        ))
      case Some((width, count)) =>
        val operation: Long => Long = value =>
          bitCount(value, width).toLong
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
              Left(Fault.InvalidTask("OpBitCount requires a matching integer scalar or vector base"))
            case None => Left(undefined(base))
          }
    }
  }

  private def logicalBinary(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      left: Operand,
      right: Operand,
      opcode: String
  )(predicate: (Boolean, Boolean) => Boolean): Either[Fault, Unit] = {
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
                Fault.InvalidTask(
                  s"$opcode requires matching boolean scalar or vector operand and result types"
                )
              )
          }
        case (Some(_), Some(_)) =>
          Left(
            Fault.InvalidTask(
              s"$opcode requires matching boolean scalar or vector operand and result types"
            )
          )
        case (None, _) => Left(undefined(left))
        case (_, None) => Left(undefined(right))
      }
  }

  // Dot products accumulate directly across vector lanes.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def floatingDot(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      left: Operand,
      right: Operand
  ): Either[Fault, Unit] = (values.get(id(left)), values.get(id(right))) match {
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
        case _ => Left(Fault.InvalidTask("OpDot requires matching floating vectors and a scalar result"))
      }
    case (None, _) => Left(undefined(left))
    case (_, None) => Left(undefined(right))
    case _         =>
      Left(Fault.InvalidTask("OpDot operands are not floating-point vectors"))
  }

  private def logicalReduction(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand,
      all: Boolean
  ): Either[Fault, Unit] = values.get(id(source)) match {
    case Some(RuntimeValue.Bits(sourceType, lanes)) =>
      (booleanLanes(program, sourceType), booleanLanes(program, id(resultType))) match {
        case (Some(count), Some(1)) if lanes.length == count =>
          val result = if (all) lanes.forall(_ != 0L) else lanes.exists(_ != 0L)
          values.updateScalar(id(destination), id(resultType), if (result) 1L else 0L)
          Right(())
        case _ => Left(Fault.InvalidTask(
            s"Op${if (all) "All" else "Any"} requires a boolean vector"
          ))
      }
    case Some(_) => Left(Fault.InvalidTask(
        s"Op${if (all) "All" else "Any"} source is a pointer"
      ))
    case None => Left(undefined(source))
  }

  private def logicalNot(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand
  ): Either[Fault, Unit] = {
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
          Left(Fault.InvalidTask(
            "OpLogicalNot requires matching boolean operand and result types"
          ))
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

  // Vector arithmetic uses indexed lanes and exits before division by zero.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return", "scalafix:DisableSyntax.var"))
  private def unsignedIntegerArithmetic(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      left: Operand,
      right: Operand,
      operation: UnsignedIntegerOperation
  ): Either[Fault, Unit] = {
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
                return Left(Fault.InvalidTask(
                  s"${operation.opcode} divisor lane $index is zero"
                ))
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
              Fault.InvalidTask(
                s"${operation.opcode} result type %${id(resultType)} is not an integer scalar or vector"
              )
            )
          case (_, None, _) =>
            Left(
              Fault.InvalidTask(
                s"${operation.opcode} left operand %${id(left)} is not an integer scalar or vector"
              )
            )
          case (_, _, None) =>
            Left(
              Fault.InvalidTask(
                s"${operation.opcode} right operand %${id(right)} is not an integer scalar or vector"
              )
            )
          case (Some(resultShape), Some(leftShape), Some(rightShape)) =>
            Left(
              Fault.InvalidTask(
                s"${operation.opcode} type shapes do not match: result $resultShape, left $leftShape, right $rightShape"
              )
            )
        }
      case (Some(RuntimeValue.Address(_, _)), _) =>
        Left(Fault.InvalidTask(
          s"${operation.opcode} left operand %${id(left)} is a pointer"
        ))
      case (_, Some(RuntimeValue.Address(_, _))) =>
        Left(Fault.InvalidTask(
          s"${operation.opcode} right operand %${id(right)} is a pointer"
        ))
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

  // Atomic validation exits before any invalid memory update.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return"))
  private def atomicStore(
      program: Program,
      values: InvocationValues,
      memory: MemoryState,
      pointer: Operand,
      scope: Operand,
      semantics: Operand,
      value: Operand
  ): Either[Fault, Unit] = {
    val opcode    = "OpAtomicStore"
    val valueId   = id(value)
    val valueType = values.scalarType(valueId)
    val shape     = Option.when(valueType >= 0)(valueType).flatMap(atomicScalarShape(program, _)) match {
      case Some(result @ (width, 1)) if width.value == 32 || width.value == 64 => result
      case _                                                                   =>
        return Left(
          Fault.InvalidTask(s"$opcode requires a 32- or 64-bit scalar value")
        )
    }
    val target = address(values, pointer) match {
      case Left(error)  => return Left(error)
      case Right(value) => value
    }
    if (target.pointee != valueType)
      return Left(
        Fault.InvalidTask(
          s"$opcode pointer pointee %${target.pointee} does not match value type %$valueType"
        )
      )
    if (!Vec(4, 5, 8, 10, 11, 12, 5349).contains(target.storageClass))
      return Left(
        Fault.InvalidTask(
          s"$opcode pointer storage class ${target.storageClass} does not support atomic access"
        )
      )
    validateAtomicControls(values, scope, semantics, opcode).flatMap { _ =>
      val width = shape._1
      memory.atomicWriteScalar(
        target,
        values.scalarBits(valueId) & mask(width),
        MemoryValueByteWidth.applyUnsafe(width.value / 8)
      )
    }
  }

  // Atomic validation exits before any invalid memory access.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return"))
  private def atomicLoad(
      program: Program,
      values: InvocationValues,
      memory: MemoryState,
      resultType: Operand,
      destination: Operand,
      pointer: Operand,
      scope: Operand,
      semantics: Operand
  ): Either[Fault, Unit] = {
    val opcode       = "OpAtomicLoad"
    val resultTypeId = id(resultType)
    val shape        = atomicScalarShape(program, resultTypeId) match {
      case Some(result @ (width, 1)) if width.value == 32 || width.value == 64 => result
      case _                                                                   =>
        return Left(
          Fault.InvalidTask(s"$opcode requires a 32- or 64-bit scalar result")
        )
    }
    val target = address(values, pointer) match {
      case Left(error)  => return Left(error)
      case Right(value) => value
    }
    if (target.pointee != resultTypeId)
      return Left(
        Fault.InvalidTask(
          s"$opcode pointer pointee %${target.pointee} does not match result type %$resultTypeId"
        )
      )
    if (!Vec(4, 5, 8, 10, 11, 12, 5349).contains(target.storageClass))
      return Left(
        Fault.InvalidTask(
          s"$opcode pointer storage class ${target.storageClass} does not support atomic access"
        )
      )
    validateAtomicControls(values, scope, semantics, opcode).flatMap { _ =>
      memory.atomicReadScalar(target, MemoryValueByteWidth.applyUnsafe(shape._1.value / 8)).map(value =>
        values.updateScalar(id(destination), resultTypeId, value & mask(shape._1))
      )
    }
  }

  // Atomic validation exits before any invalid memory update.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return"))
  private def atomicExchange(
      program: Program,
      values: InvocationValues,
      memory: MemoryState,
      resultType: Operand,
      destination: Operand,
      pointer: Operand,
      scope: Operand,
      semantics: Operand,
      value: Operand
  ): Either[Fault, Unit] = {
    val opcode       = "OpAtomicExchange"
    val resultTypeId = id(resultType)
    val valueId      = id(value)
    val shape        = atomicScalarShape(program, resultTypeId) match {
      case Some(result @ (width, 1)) if width.value == 32 || width.value == 64 => result
      case _                                                                   =>
        return Left(
          Fault.InvalidTask(s"$opcode requires a 32- or 64-bit scalar result")
        )
    }
    if (values.scalarType(valueId) != resultTypeId)
      return Left(
        Fault.InvalidTask(s"$opcode requires matching result and value types")
      )
    val target = address(values, pointer) match {
      case Left(error)  => return Left(error)
      case Right(value) => value
    }
    if (target.pointee != resultTypeId)
      return Left(
        Fault.InvalidTask(s"$opcode pointer pointee does not match result type")
      )
    if (!Vec(4, 5, 8, 10, 11, 12, 5349).contains(target.storageClass))
      return Left(Fault.InvalidTask(
        s"$opcode pointer storage class does not support atomic access"
      ))
    validateAtomicControls(values, scope, semantics, opcode).flatMap { _ =>
      memory.atomicModifyScalar(target, MemoryValueByteWidth.applyUnsafe(shape._1.value / 8))(_ =>
        values.scalarBits(valueId) & mask(shape._1)
      ).map {
        previous => values.updateScalar(id(destination), resultTypeId, previous & mask(shape._1))
      }
    }
  }

  // Atomic validation exits before any invalid memory update.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return"))
  private def atomicCompareExchange(
      program: Program,
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
  ): Either[Fault, Unit] = {
    val opcode       = "OpAtomicCompareExchange"
    val resultTypeId = id(resultType)
    val valueId      = id(value)
    val comparatorId = id(comparator)
    val shape        = atomicScalarShape(program, resultTypeId) match {
      case Some(result @ (width, 1)) if width.value == 32 || width.value == 64 => result
      case _                                                                   =>
        return Left(
          Fault.InvalidTask(s"$opcode requires a 32- or 64-bit scalar result")
        )
    }
    if (values.scalarType(valueId) != resultTypeId || values.scalarType(comparatorId) != resultTypeId)
      return Left(Fault.InvalidTask(
        s"$opcode requires matching result, value, and comparator types"
      ))
    val target = address(values, pointer) match {
      case Left(error)  => return Left(error)
      case Right(value) => value
    }
    if (target.pointee != resultTypeId)
      return Left(
        Fault.InvalidTask(s"$opcode pointer pointee does not match result type")
      )
    if (!Vec(4, 5, 8, 10, 11, 12, 5349).contains(target.storageClass))
      return Left(Fault.InvalidTask(
        s"$opcode pointer storage class does not support atomic access"
      ))
    for {
      _      <- validateAtomicControls(values, scope, equal, opcode)
      _      <- validateAtomicControls(values, scope, unequal, opcode)
      result <- memory.atomicModifyScalar(target, MemoryValueByteWidth.applyUnsafe(shape._1.value / 8)) { previous =>
        if ((previous & mask(shape._1)) == (values.scalarBits(comparatorId) & mask(shape._1)))
          values.scalarBits(valueId) & mask(shape._1)
        else previous
      }
    } yield values.updateScalar(id(destination), resultTypeId, result & mask(shape._1))
  }

  private def atomicFlagTestAndSet(
      program: Program,
      values: InvocationValues,
      memory: MemoryState,
      resultType: Operand,
      destination: Operand,
      pointer: Operand,
      scope: Operand,
      semantics: Operand
  ): Either[Fault, Unit] = {
    val opcode = "OpAtomicFlagTestAndSet"
    for {
      _ <- Either.cond(
        program.types.get(id(resultType)).contains(RuntimeType.Bool),
        (),
        Fault.InvalidTask(s"$opcode requires a Boolean result")
      )
      target   <- atomicFlagTarget(program, values, pointer, scope, semantics, opcode)
      previous <- memory.atomicModifyScalar(target, MemoryValueByteWidth.applyUnsafe(4))(_ => 1L)
    } yield values.updateScalar(id(destination), id(resultType), if (previous == 0L) 0L else 1L)
  }

  private def atomicFlagClear(
      program: Program,
      values: InvocationValues,
      memory: MemoryState,
      pointer: Operand,
      scope: Operand,
      semantics: Operand
  ): Either[Fault, Unit] = {
    val opcode = "OpAtomicFlagClear"
    for {
      target <- atomicFlagTarget(program, values, pointer, scope, semantics, opcode)
      order  <- scalar(values, semantics)
      _      <- Either.cond(
        (order & 0xaL) == 0L,
        (),
        Fault.InvalidTask(s"$opcode cannot use Acquire or AcquireRelease memory semantics")
      )
      _ <- memory.atomicWriteScalar(target, 0L, MemoryValueByteWidth.applyUnsafe(4))
    } yield ()
  }

  private def atomicFlagTarget(
      program: Program,
      values: InvocationValues,
      pointer: Operand,
      scope: Operand,
      semantics: Operand,
      opcode: String
  ): Either[Fault, Pointer] =
    for {
      target <- address(values, pointer)
      _      <- Either.cond(
        integerShape(program, target.pointee).exists { case (width, count) => width.value == 32 && count == 1 },
        (),
        Fault.InvalidTask(s"$opcode requires a pointer to a 32-bit integer flag")
      )
      _ <- Either.cond(
        Vec(4, 5, 8, 10, 11, 12, 5349).contains(target.storageClass),
        (),
        Fault.InvalidTask(s"$opcode pointer storage class does not support atomic access")
      )
      _ <- validateAtomicControls(values, scope, semantics, opcode)
    } yield target

  private def validateAtomicControls(
      values: InvocationValues,
      scope: Operand,
      semantics: Operand,
      opcode: String
  ): Either[Fault, Unit] =
    for {
      memoryScope <- scalar(values, scope)
      _           <- Either.cond(
        memoryScope >= 0L && memoryScope <= 6L,
        (),
        Fault.InvalidTask(s"$opcode has invalid memory scope $memoryScope")
      )
      memorySemantics <- scalar(values, semantics)
      knownSemantics = 0xffdeL
      _ <- Either.cond(
        (memorySemantics & ~knownSemantics) == 0L,
        (),
        Fault.InvalidTask(
          s"$opcode has unknown memory-semantics bits 0x${java.lang.Long.toHexString(memorySemantics)}"
        )
      )
    } yield ()

  private def atomicScalarShape(program: Program, typeId: Int): Option[(BitWidth, Int)] =
    integerShape(program, typeId)
      .orElse(floatingShape(program, typeId).map((width, count) => width.bitWidth -> count))

  // Atomic validation exits before any invalid integer memory update.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return"))
  private def atomicInteger(
      program: Program,
      values: InvocationValues,
      memory: MemoryState,
      resultType: Operand,
      destination: Operand,
      pointer: Operand,
      scope: Operand,
      semantics: Operand,
      value: Option[Operand],
      operation: AtomicIntegerOperation
  ): Either[Fault, Unit] = {
    val resultTypeId = id(resultType)
    val valueId      = value.map(id(_))
    val shape        = integerShape(program, resultTypeId) match {
      case Some(result @ (width, 1)) if width.value == 32 || width.value == 64 => result
      case _                                                                   =>
        return Left(
          Fault.InvalidTask(
            s"${operation.opcode} requires a 32- or 64-bit scalar integer result"
          )
        )
    }
    valueId match {
      case Some(sourceId) if values.scalarType(sourceId) != resultTypeId =>
        return Left(
          Fault.InvalidTask(
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
        Fault.InvalidTask(
          s"${operation.opcode} pointer pointee %${target.pointee} does not match result type %$resultTypeId"
        )
      )
    validateAtomicControls(values, scope, semantics, operation.opcode) match {
      case Left(error) => return Left(error)
      case Right(())   => ()
    }
    val width    = shape._1
    val byteSize = MemoryValueByteWidth.applyUnsafe(width.value / 8)
    val operand  = valueId.fold(1L)(values.scalarBits(_)) & mask(width)
    memory.atomicModifyScalar(target, byteSize) { previous =>
      val updated = operation match {
        case AtomicIntegerOperation.Increment     => (previous + 1L) & mask(width)
        case AtomicIntegerOperation.Decrement     => (previous - 1L) & mask(width)
        case AtomicIntegerOperation.Add           => (previous + operand) & mask(width)
        case AtomicIntegerOperation.Subtract      => (previous - operand) & mask(width)
        case AtomicIntegerOperation.SignedMinimum =>
          if (signExtend(previous, width) > signExtend(operand, width)) operand else previous
        case AtomicIntegerOperation.UnsignedMinimum =>
          if (width.value == 64 && java.lang.Long.compareUnsigned(previous, operand) > 0) operand
          else if (width.value < 64 && previous > operand) operand
          else previous
        case AtomicIntegerOperation.SignedMaximum =>
          if (signExtend(previous, width) < signExtend(operand, width)) operand else previous
        case AtomicIntegerOperation.UnsignedMaximum =>
          if (width.value == 64 && java.lang.Long.compareUnsigned(previous, operand) < 0) operand
          else if (width.value < 64 && previous < operand) operand
          else previous
        case AtomicIntegerOperation.And => previous & operand
        case AtomicIntegerOperation.Or  => previous | operand
        case AtomicIntegerOperation.Xor => previous ^ operand
      }
      updated
    }.map(previous => values.updateScalar(id(destination), resultTypeId, previous & mask(width)))
  }

  // Atomic validation exits before any invalid floating memory update.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return"))
  private def atomicFloatingAdd(
      program: Program,
      values: InvocationValues,
      memory: MemoryState,
      resultType: Operand,
      destination: Operand,
      pointer: Operand,
      scope: Operand,
      semantics: Operand,
      value: Operand
  ): Either[Fault, Unit] = {
    val opcode       = "OpAtomicFAddEXT"
    val resultTypeId = id(resultType)
    val valueId      = id(value)
    val shape        = floatingShape(program, resultTypeId) match {
      case Some(result @ (_, 1)) => result
      case _                     =>
        return Left(
          Fault.InvalidTask(
            s"$opcode requires a 16-, 32-, or 64-bit scalar floating-point result"
          )
        )
    }
    if (values.scalarType(valueId) != resultTypeId)
      return Left(
        Fault.InvalidTask(s"$opcode requires matching result and value types")
      )
    val target = address(values, pointer) match {
      case Left(error)  => return Left(error)
      case Right(value) => value
    }
    if (target.pointee != resultTypeId)
      return Left(
        Fault.InvalidTask(s"$opcode pointer pointee does not match result type")
      )
    if (!Vec(4, 5, 8, 10, 11, 12, 5349).contains(target.storageClass))
      return Left(Fault.InvalidTask(
        s"$opcode pointer storage class does not support atomic access"
      ))
    validateAtomicControls(values, scope, semantics, opcode).flatMap { _ =>
      val width    = shape._1
      val bitWidth = width.bitWidth
      val byteSize = MemoryValueByteWidth.applyUnsafe(width.value / 8)
      memory.atomicModifyScalar(target, byteSize) { previous =>
        floatingAddBits(width, previous, values.scalarBits(valueId)) & mask(bitWidth)
      }.map(previous => values.updateScalar(id(destination), resultTypeId, previous & mask(bitWidth)))
    }
  }

  // Atomic validation exits before any invalid floating memory update.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return"))
  private def atomicFloatingMinMax(
      program: Program,
      values: InvocationValues,
      memory: MemoryState,
      resultType: Operand,
      destination: Operand,
      pointer: Operand,
      scope: Operand,
      semantics: Operand,
      value: Operand,
      maximum: Boolean
  ): Either[Fault, Unit] = {
    val opcode       = if (maximum) "OpAtomicFMaxEXT" else "OpAtomicFMinEXT"
    val resultTypeId = id(resultType)
    val valueId      = id(value)
    val shape        = floatingShape(program, resultTypeId) match {
      case Some(result @ (_, 1)) => result
      case _                     =>
        return Left(
          Fault.InvalidTask(
            s"$opcode requires a 16-, 32-, or 64-bit scalar floating-point result"
          )
        )
    }
    if (values.scalarType(valueId) != resultTypeId)
      return Left(
        Fault.InvalidTask(s"$opcode requires matching result and value types")
      )
    val target = address(values, pointer) match {
      case Left(error)  => return Left(error)
      case Right(value) => value
    }
    if (target.pointee != resultTypeId)
      return Left(
        Fault.InvalidTask(s"$opcode pointer pointee does not match result type")
      )
    if (!Vec(4, 5, 8, 10, 11, 12, 5349).contains(target.storageClass))
      return Left(Fault.InvalidTask(
        s"$opcode pointer storage class does not support atomic access"
      ))
    validateAtomicControls(values, scope, semantics, opcode).flatMap { _ =>
      val width    = shape._1
      val bitWidth = width.bitWidth
      val byteSize = MemoryValueByteWidth.applyUnsafe(width.value / 8)
      val operand  = values.scalarBits(valueId)
      memory.atomicModifyScalar(target, byteSize) { previous =>
        val updated =
          if (maximum) floatingMaximumBits(width, previous, operand)
          else floatingMinimumBits(width, previous, operand)
        updated & mask(bitWidth)
      }.map(previous => values.updateScalar(id(destination), resultTypeId, previous & mask(bitWidth)))
    }
  }

  // Vector arithmetic uses indexed lanes and exits before division overflow or zero.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return", "scalafix:DisableSyntax.var"))
  private def signedIntegerArithmetic(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      left: Operand,
      right: Operand,
      operation: SignedIntegerOperation
  ): Either[Fault, Unit] = {
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
            Left(Fault.InvalidTask(s"${operation.opcode} divisor lane 0 is zero"))
          else {
            val dividend = signExtend(values.scalarBits(leftId), width)
            val minimum  = if (width.value == 64) Long.MinValue else -(1L << (width.value - 1))
            if (dividend == minimum && divisor == -1L)
              Left(
                Fault.InvalidTask(
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
            Fault.InvalidTask(
              s"${operation.opcode} result type %${id(resultType)} is not an integer scalar or vector"
            )
          )
        case (_, None, _) =>
          Left(
            Fault.InvalidTask(
              s"${operation.opcode} left operand %${id(left)} is not an integer scalar or vector"
            )
          )
        case (_, _, None) =>
          Left(
            Fault.InvalidTask(
              s"${operation.opcode} right operand %${id(right)} is not an integer scalar or vector"
            )
          )
        case (Some(resultShape), Some(leftShape), Some(rightShape)) =>
          Left(
            Fault.InvalidTask(
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
                Left(
                  Fault.InvalidTask(s"${operation.opcode} divisor lane 0 is zero")
                )
              else {
                val dividend = signExtend(leftValue, width)
                val minimum  = if (width.value == 64) Long.MinValue else -(1L << (width.value - 1))
                if (dividend == minimum && divisor == -1L)
                  Left(
                    Fault.InvalidTask(
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
                Fault.InvalidTask(
                  s"${operation.opcode} result type %${id(resultType)} is not an integer scalar or vector"
                )
              )
            case (_, None, _) =>
              Left(
                Fault.InvalidTask(
                  s"${operation.opcode} left operand %${id(left)} is not an integer scalar or vector"
                )
              )
            case (_, _, None) =>
              Left(
                Fault.InvalidTask(
                  s"${operation.opcode} right operand %${id(right)} is not an integer scalar or vector"
                )
              )
            case (Some(resultShape), Some(leftShape), Some(rightShape)) =>
              Left(
                Fault.InvalidTask(
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
                  return Left(Fault.InvalidTask(
                    s"${operation.opcode} divisor lane $index is zero"
                  ))
                val dividend = signExtend(leftLanes(index), width)
                val minimum  = if (width.value == 64) Long.MinValue else -(1L << (width.value - 1))
                if (dividend == minimum && divisor == -1L)
                  return Left(
                    Fault.InvalidTask(
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
                Fault.InvalidTask(
                  s"${operation.opcode} result type %${id(resultType)} is not an integer scalar or vector"
                )
              )
            case (_, None, _) =>
              Left(
                Fault.InvalidTask(
                  s"${operation.opcode} left operand %${id(left)} is not an integer scalar or vector"
                )
              )
            case (_, _, None) =>
              Left(
                Fault.InvalidTask(
                  s"${operation.opcode} right operand %${id(right)} is not an integer scalar or vector"
                )
              )
            case (Some(resultShape), Some(leftShape), Some(rightShape)) =>
              Left(
                Fault.InvalidTask(
                  s"${operation.opcode} type shapes do not match: result $resultShape, left $leftShape, right $rightShape"
                )
              )
          }
        case (Some(RuntimeValue.Address(_, _)), _) =>
          Left(Fault.InvalidTask(
            s"${operation.opcode} left operand %${id(left)} is a pointer"
          ))
        case (_, Some(RuntimeValue.Address(_, _))) =>
          Left(Fault.InvalidTask(
            s"${operation.opcode} right operand %${id(right)} is a pointer"
          ))
        case (None, _) => Left(undefined(left))
        case (_, None) => Left(undefined(right))
      }
  }

  private def floatingCompare(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      left: Operand,
      right: Operand,
      operation: FloatingComparison
  ): Either[Fault, Unit] = (values.get(id(left)), values.get(id(right))) match {
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
            val a          = IEEE754.toDouble(leftLanes(index), width)
            val b          = IEEE754.toDouble(rightLanes(index), width)
            val comparison = operation match {
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
            if (comparison) 1L else 0L
          }
          values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
          Right(())
        case (None, _, _) =>
          Left(Fault.InvalidTask(
            s"${operation.opcode} left operand %${id(left)} is not floating-point"
          ))
        case (_, None, _) =>
          Left(
            Fault.InvalidTask(
              s"${operation.opcode} right operand %${id(right)} is not floating-point"
            )
          )
        case (_, _, None) =>
          Left(
            Fault.InvalidTask(
              s"${operation.opcode} result type %${id(resultType)} is not a boolean scalar or vector"
            )
          )
        case (Some(leftShape), Some(rightShape), Some(resultLanes)) =>
          Left(
            Fault.InvalidTask(
              s"${operation.opcode} %${id(destination)} operands %${id(left)}/%${id(right)} " +
                s"type shapes/lane counts do not match: left $leftShape/${leftLanes.length}, " +
                s"right $rightShape/${rightLanes.length}, result lanes $resultLanes"
            )
          )
      }
    case (Some(RuntimeValue.Address(_, _)), _) =>
      Left(Fault.InvalidTask(
        s"${operation.opcode} left operand %${id(left)} is a pointer"
      ))
    case (_, Some(RuntimeValue.Address(_, _))) =>
      Left(Fault.InvalidTask(
        s"${operation.opcode} right operand %${id(right)} is a pointer"
      ))
    case (None, _) => Left(undefined(left))
    case (_, None) => Left(undefined(right))
  }

  private def integerShape(program: Program, typeId: Int): Option[(BitWidth, Int)] =
    program.runtimeType(typeId) match {
      case Some(RuntimeType.Integer(width, _))            => Some((width, 1))
      case Some(RuntimeType.VectorType(component, count)) => program.runtimeType(component) match {
          case Some(RuntimeType.Integer(width, _)) => Some((width, count))
          case _                                   => None
        }
      case _ => None
    }

  private def floatingShape(program: Program, typeId: Int): Option[(FloatingPointWidth, Int)] =
    program.runtimeType(typeId) match {
      case Some(RuntimeType.Floating(width))              => Some((width, 1))
      case Some(RuntimeType.VectorType(component, count)) => program.runtimeType(component) match {
          case Some(RuntimeType.Floating(width)) => Some((width, count))
          case _                                 => None
        }
      case _ => None
    }

  private[v1_6] def floatingAddBits(width: FloatingPointWidth, left: Long, right: Long): Long = width.value match {
    case 16 =>
      IEEE754.floatToBinary16(
        IEEE754.binary16ToFloat(Binary16.low(left)) +
          IEEE754.binary16ToFloat(Binary16.low(right))
      ).value
    case 32 =>
      IEEE754.floatToBinary32(
        IEEE754.binary32ToFloat(Binary32.low(left)) +
          IEEE754.binary32ToFloat(Binary32.low(right))
      ).value
    case 64 =>
      IEEE754.doubleToBinary64(IEEE754.binary64ToDouble(left) + IEEE754.binary64ToDouble(right))
  }

  private def floatingOneBits(width: FloatingPointWidth): Long = width.value match {
    case 16 => IEEE754.floatToBinary16(1.0f).value
    case 32 => IEEE754.floatToBinary32(1.0f).value
    case 64 => IEEE754.doubleToBinary64(1.0)
  }

  private def floatingInfinityBits(width: FloatingPointWidth, negative: Boolean): Long = width.value match {
    case 16 => if (negative) 0xfc00L else 0x7c00L
    case 32 => if (negative) 0xff800000L else 0x7f800000L
    case 64 => if (negative) 0xfff0000000000000L else 0x7ff0000000000000L
  }

  private[v1_6] def floatingMultiplyBits(
      width: FloatingPointWidth,
      left: Long,
      right: Long
  ): Long = width.value match {
    case 16 =>
      IEEE754.floatToBinary16(
        IEEE754.binary16ToFloat(Binary16.low(left)) *
          IEEE754.binary16ToFloat(Binary16.low(right))
      ).value
    case 32 =>
      IEEE754.floatToBinary32(
        IEEE754.binary32ToFloat(Binary32.low(left)) *
          IEEE754.binary32ToFloat(Binary32.low(right))
      ).value
    case 64 =>
      IEEE754.doubleToBinary64(IEEE754.binary64ToDouble(left) * IEEE754.binary64ToDouble(right))
  }

  private def floatingFmaBits(width: FloatingPointWidth, left: Long, right: Long, addend: Long): Long =
    width.value match {
      case 16 =>
        IEEE754.floatToBinary16(
          Math.fma(
            IEEE754.binary16ToFloat(Binary16.low(left)).toDouble,
            IEEE754.binary16ToFloat(Binary16.low(right)).toDouble,
            IEEE754.binary16ToFloat(Binary16.low(addend)).toDouble
          ).toFloat
        ).value
      case 32 =>
        IEEE754.floatToBinary32(
          Math.fma(
            IEEE754.binary32ToFloat(Binary32.low(left)),
            IEEE754.binary32ToFloat(Binary32.low(right)),
            IEEE754.binary32ToFloat(Binary32.low(addend))
          )
        ).value
      case 64 =>
        IEEE754.doubleToBinary64(
          Math.fma(IEEE754.binary64ToDouble(left), IEEE754.binary64ToDouble(right), IEEE754.binary64ToDouble(addend))
        )
    }

  private[v1_6] def floatingMinimumBits(
      width: FloatingPointWidth,
      left: Long,
      right: Long
  ): Long = width.value match {
    case 16 =>
      IEEE754.floatToBinary16(
        floatingMinimum(
          IEEE754.binary16ToFloat(Binary16.low(left)),
          IEEE754.binary16ToFloat(Binary16.low(right))
        )
      ).value
    case 32 =>
      IEEE754.floatToBinary32(
        floatingMinimum(
          IEEE754.binary32ToFloat(Binary32.low(left)),
          IEEE754.binary32ToFloat(Binary32.low(right))
        )
      ).value
    case 64 =>
      IEEE754.doubleToBinary64(floatingMinimum(IEEE754.binary64ToDouble(left), IEEE754.binary64ToDouble(right)))
  }

  private[v1_6] def floatingMaximumBits(
      width: FloatingPointWidth,
      left: Long,
      right: Long
  ): Long = width.value match {
    case 16 =>
      IEEE754.floatToBinary16(
        floatingMaximum(
          IEEE754.binary16ToFloat(Binary16.low(left)),
          IEEE754.binary16ToFloat(Binary16.low(right))
        )
      ).value
    case 32 =>
      IEEE754.floatToBinary32(
        floatingMaximum(
          IEEE754.binary32ToFloat(Binary32.low(left)),
          IEEE754.binary32ToFloat(Binary32.low(right))
        )
      ).value
    case 64 =>
      IEEE754.doubleToBinary64(floatingMaximum(IEEE754.binary64ToDouble(left), IEEE754.binary64ToDouble(right)))
  }

  private def floatingMinimum(left: Float, right: Float): Float =
    if (left.isNaN) right
    else if (right.isNaN) left
    else if (left == right) {
      if (
        left == 0.0f &&
        ((IEEE754.floatToBinary32(left).value.toInt | IEEE754.floatToBinary32(right).value.toInt) < 0)
      )
        IEEE754.binary32ToFloat(Binary32(0x80000000L))
      else left
    } else if (left < right) left else right

  private def floatingMaximum(left: Float, right: Float): Float =
    if (left.isNaN) right
    else if (right.isNaN) left
    else if (left == right) {
      if (
        left == 0.0f &&
        ((IEEE754.floatToBinary32(left).value.toInt & IEEE754.floatToBinary32(right).value.toInt) >= 0)
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
        ((IEEE754.doubleToBinary64(left) | IEEE754.doubleToBinary64(right)) < 0L)
      )
        IEEE754.binary64ToDouble(0x8000000000000000L)
      else left
    } else if (left < right) left else right

  private def floatingMaximum(left: Double, right: Double): Double =
    if (left.isNaN) right
    else if (right.isNaN) left
    else if (left == right) {
      if (
        left == 0.0d &&
        ((IEEE754.doubleToBinary64(left) & IEEE754.doubleToBinary64(right)) >= 0L)
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

  private def booleanLanes(program: Program, typeId: Int): Option[Int] = program.runtimeType(typeId) match {
    case Some(RuntimeType.Bool) => Some(1)
    case Some(RuntimeType.VectorType(component, count)) if program.runtimeType(component).contains(RuntimeType.Bool) =>
      Some(count)
    case _ => None
  }

  private def label(function: Function, operand: Operand): Either[Fault, Int] =
    function.labels.get(id(operand)) match {
      case Some(value) => Right(value)
      case None        => Left(Fault.InvalidTask(s"SPIR-V label %${id(operand)} is absent in function %${function.id}"))
    }

  private def alignUp(value: Int, alignment: Int): Int =
    Math.multiplyExact((value + alignment - 1) / alignment, alignment)

  private def withExecutionContext(
      error: Fault,
      function: Function,
      pc: Int,
      instruction: Instruction
  ): Fault = error match {
    case Fault.InvalidTask(message) =>
      Fault.InvalidTask(
        s"$message at function %${function.id}, pc $pc ($instruction)"
      )
    case _ => error
  }

  private def checkedSize(size: Long): Int = {
    if (size < 0L || size > Int.MaxValue.toLong) throw IllegalArgumentException(s"invalid local size $size")
    size.toInt
  }
  private def undefined(operand: Operand): Fault.InvalidTask =
    Fault.InvalidTask(s"SPIR-V value %${id(operand)} is undefined")
}
