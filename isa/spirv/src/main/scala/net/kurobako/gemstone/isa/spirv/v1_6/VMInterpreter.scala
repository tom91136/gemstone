package net.kurobako.gemstone.isa.spirv.v1_6

import VMCollectives.{Kind as CollectiveKind, Payload as CollectivePayload, Request as CollectiveRequest, *}
import VMIR.*
import VMInvocation.*
import VMMemory.*
import VMShaderInterface.*
import cats.syntax.all.*
import java.nio.charset.StandardCharsets
import java.util.concurrent.locks.ReentrantLock
import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Bits.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.concurrent.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import scala.annotation.tailrec
import scala.collection.mutable
import scala.util.control.NonFatal

/** Typed SPIR-V kernel execution over process-neutral allocation identities.
  *
  * This interpreter deliberately operates on the generated instruction enum. It does not translate pointers into host
  * addresses: external memory is always accessed through [[Memory]], while Function and local storage use
  * invocation/work-group-owned byte buffers.
  */
private object VMInterpreter {
  import Operand.*
  private type Program = VMProgramRuntime
  private val LegacyWorkgroupLinearId = 35

  private def isPhi(instruction: Instruction): Boolean = instruction match {
    case Instruction.Phi(_, _, _) => true
    case _                        => false
  }

  private enum FloatingOperation(val opcode: String) {
    case Add       extends FloatingOperation("OpFAdd")
    case Subtract  extends FloatingOperation("OpFSub")
    case Multiply  extends FloatingOperation("OpFMul")
    case Divide    extends FloatingOperation("OpFDiv")
    case Remainder extends FloatingOperation("OpFRem")
    case Modulo    extends FloatingOperation("OpFMod")
    case Minimum   extends FloatingOperation("OpenCL.std fmin")
    case Maximum   extends FloatingOperation("OpenCL.std fmax")
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
    ExtendedInstruction.OpenClStd.Acos,
    ExtendedInstruction.OpenClStd.Acosh,
    ExtendedInstruction.OpenClStd.Acospi,
    ExtendedInstruction.OpenClStd.Asin,
    ExtendedInstruction.OpenClStd.Asinh,
    ExtendedInstruction.OpenClStd.Asinpi,
    ExtendedInstruction.OpenClStd.Atan,
    ExtendedInstruction.OpenClStd.Atanh,
    ExtendedInstruction.OpenClStd.Atanpi,
    ExtendedInstruction.OpenClStd.Cbrt,
    ExtendedInstruction.OpenClStd.Ceil,
    ExtendedInstruction.OpenClStd.Cos,
    ExtendedInstruction.OpenClStd.Cosh,
    ExtendedInstruction.OpenClStd.Cospi,
    ExtendedInstruction.OpenClStd.Erf,
    ExtendedInstruction.OpenClStd.Erfc,
    ExtendedInstruction.OpenClStd.Tgamma,
    ExtendedInstruction.OpenClStd.Lgamma,
    ExtendedInstruction.OpenClStd.Exp,
    ExtendedInstruction.OpenClStd.Exp2,
    ExtendedInstruction.OpenClStd.Exp10,
    ExtendedInstruction.OpenClStd.Expm1,
    ExtendedInstruction.OpenClStd.Fabs,
    ExtendedInstruction.OpenClStd.Floor,
    ExtendedInstruction.OpenClStd.Log,
    ExtendedInstruction.OpenClStd.Log2,
    ExtendedInstruction.OpenClStd.Log10,
    ExtendedInstruction.OpenClStd.Log1p,
    ExtendedInstruction.OpenClStd.Logb,
    ExtendedInstruction.OpenClStd.Rint,
    ExtendedInstruction.OpenClStd.Round,
    ExtendedInstruction.OpenClStd.Rsqrt,
    ExtendedInstruction.OpenClStd.Sin,
    ExtendedInstruction.OpenClStd.Sinh,
    ExtendedInstruction.OpenClStd.Sinpi,
    ExtendedInstruction.OpenClStd.Sqrt,
    ExtendedInstruction.OpenClStd.Tan,
    ExtendedInstruction.OpenClStd.Tanh,
    ExtendedInstruction.OpenClStd.Tanpi,
    ExtendedInstruction.OpenClStd.Trunc,
    ExtendedInstruction.OpenClStd.NativeCos,
    ExtendedInstruction.OpenClStd.NativeExp,
    ExtendedInstruction.OpenClStd.NativeExp2,
    ExtendedInstruction.OpenClStd.NativeExp10,
    ExtendedInstruction.OpenClStd.NativeLog,
    ExtendedInstruction.OpenClStd.NativeLog2,
    ExtendedInstruction.OpenClStd.NativeLog10,
    ExtendedInstruction.OpenClStd.NativeRecip,
    ExtendedInstruction.OpenClStd.NativeRsqrt,
    ExtendedInstruction.OpenClStd.NativeSin,
    ExtendedInstruction.OpenClStd.NativeSqrt,
    ExtendedInstruction.OpenClStd.NativeTan
  )
  private val OpenCLBinaryFloatingOpcodes = Set(
    ExtendedInstruction.OpenClStd.Atan2,
    ExtendedInstruction.OpenClStd.Atan2pi,
    ExtendedInstruction.OpenClStd.Fdim,
    ExtendedInstruction.OpenClStd.Fmod,
    ExtendedInstruction.OpenClStd.Remainder,
    ExtendedInstruction.OpenClStd.Hypot,
    ExtendedInstruction.OpenClStd.NativeDivide,
    ExtendedInstruction.OpenClStd.NativePowr,
    ExtendedInstruction.OpenClStd.Powr
  )

  def execute(
      program: Program,
      dispatch: KernelTask,
      memory: Memory,
      control: TaskControl,
      workgroups: Workgroups,
      handler: Event => Either[Fault, Unit],
      measurement: ExecutionMetrics => Unit = _ => ()
  ): Either[Fault, Unit] = {
    val metrics = VMMetrics()
    val started = platform.monotonicNanos
    val result  = try executeValidated(program, dispatch, memory, control, workgroups, handler, metrics)
    catch {
      case NonFatal(error) =>
        Left(
          Fault.InvalidTask(
            s"malformed SPIR-V execution state: ${Option(error.getMessage).getOrElse(error.getClass.getSimpleName)}"
          )
        )
    }
    metrics.recordElapsed(math.max(0L, platform.monotonicNanos - started))
    measurement(metrics.snapshot)
    result
  }

  private def executeValidated(
      program: Program,
      dispatch: KernelTask,
      memory: Memory,
      control: TaskControl,
      workgroups: Workgroups,
      handler: Event => Either[Fault, Unit],
      metrics: VMMetrics
  ): Either[Fault, Unit] =
    program.entryPoints.get(dispatch.kernelName).toRight(
      Fault.InvalidTask(
        s"SPIR-V entry point ${dispatch.kernelName} does not exist"
      )
    ).flatMap { functionId =>
      program.functions.get(functionId).toRight(
        Fault.InvalidTask(
          s"SPIR-V entry point ${dispatch.kernelName} has no function body"
        )
      ).flatMap { function =>
        Either.cond(
          (if (program.vulkanEntries.contains(dispatch.kernelName))
             argumentVariables(program, dispatch.kernelName).size
           else function.parameters.size) == dispatch.arguments.size,
          (),
          Fault.InvalidTask(
            s"SPIR-V entry point ${dispatch.kernelName} expects ${
                if (program.vulkanEntries.contains(dispatch.kernelName)) argumentVariables(program, dispatch.kernelName).size
                else function.parameters.size
              } arguments, " +
              s"received ${dispatch.arguments.size}"
          )
        ).flatMap(_ => executeDispatch(program, function, dispatch, memory, control, workgroups, handler, metrics))
      }
    }

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
      workgroups: Workgroups,
      handler: Event => Either[Fault, Unit],
      metrics: VMMetrics
  ): Either[Fault, Unit] = {
    val initialGlobals = program.materializeGlobals(storageClass =>
      storageClass != StorageClass.Workgroup && storageClass != StorageClass.Private
    ) match {
      case Left(error)  => return Left(error)
      case Right(value) => value
    }
    val globalValues = if (!program.vulkanEntries.contains(dispatch.kernelName)) initialGlobals
    else {
      argumentVariables(
        program,
        dispatch.kernelName
      ).zip(dispatch.arguments).foldLeft[Either[Fault, Map[Int, RuntimeValue]]](Right(initialGlobals)) {
        case (result, (variable, KernelTask.Argument.Memory(allocation, offset, range))) => result.flatMap { values =>
            program.globals.get(variable).toRight(Fault.InvalidTask(s"SPIR-V descriptor %$variable has no variable"))
              .map { global =>
                val pointee = program.layout.pointerPointee(global.pointerType)
                val leaf    = Pointer(
                  Region.External(allocation),
                  offset.raw,
                  pointee,
                  global.storageClass,
                  bounds = range.map(size => offset.raw -> Math.addExact(offset.raw, size.raw))
                )
                val pointer = program.layout.runtimeType(pointee) match {
                  case Some(RuntimeType.Array(member, _)) if program.descriptorBindings.contains(variable) =>
                    val previous = values.get(variable).collect {
                      case RuntimeValue.Address(_, pointer) => pointer.descriptors.getOrElse(Vector.empty)
                    }.getOrElse(Vector.empty)
                    leaf.copy(descriptors = Some(previous :+ leaf.copy(pointee = member)))
                  case _ => leaf
                }
                values.updated(variable, RuntimeValue.Address(global.pointerType, pointer))
              }
          }
        case (_, (variable, _)) => Left(Fault.InvalidTask(s"SPIR-V descriptor %$variable requires a memory argument"))
      } match {
        case Left(error)  => return Left(error)
        case Right(value) => value
      }
    }
    val stageInterfaces   = stageInterfaceVariables(program, dispatch.kernelName).toSet
    val stageInitializers = stageInterfaces.toVector.flatMap { variable =>
      program.globals.get(variable).filter(_.storageClass == StorageClass.Output).flatMap(_.initializer).map {
        initializer =>
          variable -> program.encode(program.constants(initializer))
      }
    }
    val fragment = program.module.instructions.exists {
      case Instruction.EntryPoint(model, entry, _, _) => id(model) == 4 && id(entry) == function.id
      case _                                          => false
    }
    val helperVariable =
      stageInterfaces.find(variable => program.builtins.get(variable).contains(BuiltIn.HelperInvocation))
    val maskVariable = stageInterfaces.find(variable =>
      program.builtins.get(variable).contains(BuiltIn.SampleMask) && program.globals.get(
        variable
      ).exists(_.storageClass == StorageClass.Output)
    )
    val groupSize  = dispatch.geometry.groupSize
    val totalLocal = groupSize.volumeInt match {
      case Left(message) => return Left(Fault.InvalidTask(message))
      case Right(value)  => value
    }
    val subgroup = program.subgroupSizes.getOrElse(
      function.id,
      if (program.vulkanEntries.nonEmpty) 32 else math.max(1, math.min(32, totalLocal.raw))
    )
    val enqueuedSubgroups = (totalLocal.raw.toLong + subgroup.toLong - 1L) / subgroup.toLong
    val hasCollectives    = program.collectiveFunctions.contains(function.id) || memory.defersHostReads
    val baseScalars = BaseScalarValues(program.constants, globalValues, program.module.header.bound.toUnsigned.toLong)
    metrics.recordDispatch(hasCollectives)
    val groupCount = dispatch.geometry.groupCount.volume match {
      case Left(message) => return Left(Fault.InvalidTask(message))
      case Right(value)  => value
    }
    // The workgroups parallelizes launches large enough to amortise queue traffic and claims a range of groups per
    // dequeue, so queue overhead stays bounded while medium grids leave the calling thread.
    // Positive launch volumes imply the workgroups's non-negative task-count requirement.
    val taskCount: Long >= 0 = groupCount
    val workItems: Long >= 0 = totalLocal.toLong
    val parallelWork         = workgroups.shouldParallel(taskCount, workItems)
    val state                = MemoryState(memory, dispatch.pointerRelocations, handler)
    val flushLock            = ReentrantLock()
    val reusableValues       =
      if (hasCollectives || parallelWork) None
      else
        Some(
          InvocationValues(
            program.constants,
            globalValues,
            program.module.header.bound.toUnsigned.toLong,
            baseScalars,
            metrics
          )
        )
    val reusableExecution = Array[InvocationState | Null](null)
    // Collective invocations must keep separate register files while a workgroup is suspended,
    // but a completed workgroup no longer needs them. Reuse each local lane's file in the
    // next workgroup when dispatch is sequential.
    val collectiveValues =
      if (hasCollectives && !parallelWork && groupCount.raw > 1L)
        Array.fill[Option[InvocationValues]](totalLocal.raw)(None)
      else Array.empty[Option[InvocationValues]]

    def runGroup(
        groupIndex: Long >= 0,
        reuseInvocationState: Boolean,
        groupMetrics: VMMetrics,
        reuseValues: Option[InvocationValues],
        reuseExecution: Option[Array[InvocationState | Null]],
        groupState: MemoryState
    ): Either[Fault, Unit] = {
      val group = dispatch.geometry.groupCoordinates(groupIndex.raw) match {
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

      val workgroupValues = program.materializeGlobals(_ == 4) match {
        case Left(error)  => return Left(error)
        case Right(value) => value
      }
      val shared =
        if (dispatch.arguments.isEmpty) Map.empty[Int, Region]
        else
          dispatch.arguments.zipWithIndex.collect { case (KernelTask.Argument.Local(size), argumentIndex) =>
            argumentIndex -> Region.Internal(InternalMemory(Array.ofDim[Byte](checkedSize(size.raw))))
          }.toMap
      val reusableValues = if (hasCollectives) None
      else reuseValues.orElse(
        Some(
          InvocationValues(
            program.constants,
            globalValues,
            program.module.header.bound.toUnsigned.toLong,
            baseScalars,
            groupMetrics
          )
        )
      )
      var reusableExecution: InvocationState | Null = reuseExecution match {
        case Some(slot) => slot(0)
        case None       => null
      }
      val executions  = if (hasCollectives) mutable.ArrayBuffer.empty[InvocationState] else null
      var linearLocal = 0
      while (linearLocal < activeLocalCount.raw) {
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
          val privateValues = program.materializeGlobals(_ == 6) match {
            case Left(error)  => return Left(error)
            case Right(value) => value
          }
          val subgroupIndex = linearLocal / subgroup
          val subgroupSize  = if (program.vulkanEntries.nonEmpty) subgroup
          else math.min(subgroup, activeLocalCount.raw - subgroupIndex * subgroup)
          val numSubgroups = (activeLocalCount.raw.toLong + subgroup.toLong - 1L) / subgroup.toLong
          val invocation   = reusableExecution match {
            case null => InvocationBuiltins(
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
          // Stage arguments are arrays of invocation records. Vertex inputs and
          // interpolated fragment inputs use the same typed memory operations as
          // descriptors; each invocation receives its own input/output slice.
          val stageValues = stageInterfaces.view.flatMap { variable =>
            globalValues.get(variable).collect { case RuntimeValue.Address(pointerType, pointer) =>
              variable -> RuntimeValue.Address(
                pointerType,
                pointer.copy(
                  offset = Math.addExact(
                    pointer.offset,
                    Math.multiplyExact(global.x.raw, program.layout.typeSize(pointer.pointee).toLong)
                  )
                )
              )
            }
          }.toMap
          // Output initializers apply to each invocation's record, including
          // invocations that never execute an explicit store to that output.
          var initializerIndex = 0
          while (initializerIndex < stageInitializers.size) {
            val (variable, bytes) = stageInitializers(initializerIndex)
            stageValues.get(variable) match {
              case Some(RuntimeValue.Address(_, pointer)) => bytes.flatMap(groupState.write(pointer, _)) match {
                  case Left(error) => return Left(error)
                  case Right(())   => ()
                }
              case _ => return Left(Fault.InvalidTask(s"SPIR-V output %$variable has no invocation record"))
            }
            initializerIndex += 1
          }
          prepareInvocation(
            program,
            function,
            dispatch.arguments,
            shared,
            invocation,
            globalValues ++ workgroupValues ++ privateValues ++ stageValues,
            if (collectiveValues.nonEmpty) collectiveValues(linearLocal) else reusableValues,
            reusableExecution,
            groupState,
            baseScalars,
            groupMetrics,
            collectiveValues.nonEmpty
          ) match {
            case Left(error)  => return Left(error)
            case Right(value) =>
              value.fragmentInvocation = fragment
              value.sampleMask =
                maskVariable.flatMap(stageValues.get).collect { case RuntimeValue.Address(_, pointer) => pointer }
              value.fragmentQuad = fragment && groupSize.x.raw == 4 && groupSize.y.raw == 1 && groupSize.z.raw == 1
              value.subgroupReconvergence = program.usesNonUniformCollectives && !fragment
              value.helperInvocation = helperVariable.flatMap(stageValues.get) match {
                case Some(RuntimeValue.Address(_, pointer)) =>
                  val flag = memory.awaitRead(groupState.readScalar(pointer, 1))
                  flag match {
                    case Left(error)  => return Left(error)
                    case Right(value) => value != 0
                  }
                case _ => false
              }
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

    val runResult = workgroups.forEach(taskCount, parallel = parallelWork) { groupIndex =>
      if (parallelWork) {
        val groupMetrics = VMMetrics()
        val groupState   = MemoryState(memory, dispatch.pointerRelocations, handler)
        runGroup(
          groupIndex = groupIndex,
          reuseInvocationState = false,
          groupMetrics = groupMetrics,
          reuseValues = None,
          reuseExecution = None,
          groupState = groupState
        ) match {
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
          groupIndex = groupIndex,
          reuseInvocationState = true,
          groupMetrics = metrics,
          reuseValues = reusableValues,
          reuseExecution = Some(reusableExecution),
          groupState = state
        )
      }
    }
    runResult match {
      case Left(error) => return Left(error)
      case Right(())   => ()
    }
    if (parallelWork) Right(()) else state.flush()
  }

  // InvocationBuiltins setup fills the machine frame sequentially and fails fast on invalid parameters.
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
      invocation: InvocationBuiltins,
      globalValues: Map[Int, RuntimeValue],
      reusableValues: Option[InvocationValues],
      reusableExecution: InvocationState | Null,
      memory: MemoryState,
      baseScalars: BaseScalarValues,
      metrics: VMMetrics,
      trackTouched: Boolean
  ): Either[Fault, InvocationState] = {
    val values = reusableValues match {
      case Some(value) =>
        value.clear()
        globalValues.foreach { case (id, global) => value.update(id, global) }
        value
      case None =>
        InvocationValues(
          program.constants,
          globalValues,
          program.module.header.bound.toUnsigned.toLong,
          baseScalars,
          metrics,
          trackTouched
        )
    }
    val entry      = program.entryPoints.view.collectFirst { case (name, entryId) if entryId == function.id => name }
    val shader     = entry.exists(program.vulkanEntries.contains)
    val interfaces = entry.flatMap(program.ir.entryPointInterfaces.get).getOrElse(Set.empty)
    val globals    = program.globals.view.filter { case (variable, global) =>
      !shader || (global.storageClass != StorageClass.Input && global.storageClass != StorageClass.Output) || interfaces.contains(
        variable
      )
    }.toVector
    var globalIndex = 0
    while (globalIndex < globals.size) {
      val (variable, global) = globals(globalIndex)
      program.builtins.get(variable) match {
        case Some(_) if globalValues.get(variable).exists {
              case RuntimeValue.Address(_, pointer) => pointer.region match {
                  case Region.External(_) => true
                  case _                  => false
                }
              case _ => false
            } => ()
        case Some(builtin) =>
          val pointee = program.layout.pointerPointee(global.pointerType)
          val value   = builtinValue(program, pointee, builtin, invocation) match {
            case Left(error)   => return Left(error)
            case Right(result) => result
          }
          val bytes = program.encode(value) match {
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
        InvocationState(
          invocation,
          values,
          function,
          function.start + 1,
          -1,
          -1,
          0L,
          mutable.ArrayDeque.empty[CallFrame]
        )
      case value =>
        value.reset(invocation, function)
        value
    }
    Right(execution)
  }

  private val InstructionCompleted: Either[Fault, Unit] = Right(())

  // Keep the loop and semantic families below HotSpot's huge-method threshold.
  // InvocationBuiltins state is explicit so calls, returns and collective suspension share one path.
  // The instruction loop exits as soon as the invocation completes, suspends, or faults.
  @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.return"))
  private def resumeInvocation(
      program: Program,
      execution: InvocationState,
      memory: MemoryState,
      control: TaskControl,
      metrics: VMMetrics
  ): Either[Fault, Unit] = {
    memory.helperInvocation = execution.helperInvocation
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
          case Left(Memory.Pending) =>
            execution.awaitMemory()
            return InstructionCompleted
          case Left(error: Fault) => return Left(error)
          case Right(_)           => ()
        }
      if (
        execution.completed || execution.suspended.nonEmpty || execution.quadRequest.nonEmpty || execution.fragmentJoin.nonEmpty
      )
        return InstructionCompleted
    }
    Left(Fault.InvalidTask(
      s"SPIR-V function %${execution.currentFunction.id} " +
        s"(${program.functionNames.get(execution.currentFunction.id).fold("unnamed")(_)}) " +
        "reached OpFunctionEnd without OpReturn"
    ))
  }

  private def executeScalarInteger(
      execution: InvocationState,
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
      execution: InvocationState,
      memory: MemoryState,
      instruction: Instruction
  ): Memory.ReadResult[Unit] = instruction match {
    case Instruction.Kill() =>
      if (!execution.fragmentInvocation) Left(Fault.InvalidTask("OpKill requires a fragment invocation"))
      else {
        val cleared = execution.sampleMask match {
          case Some(pointer) => memory.write(pointer, Array.ofDim[Byte](program.layout.typeSize(pointer.pointee)))
          case None          => Right(())
        }
        cleared.map { _ =>
          execution.fragmentKilled = true
          execution.complete()
          execution.calls.clear()
        }
      }
    case _: Instruction.Nop | _: Instruction.Line | _: Instruction.NoLine =>
      execution.pc += 1
      Right(())
    case _: Instruction.FunctionParameter | _: Instruction.Label | _: Instruction.Phi =>
      executePhi(program, execution, instruction)
    case _: Instruction.Variable | _: Instruction.Load | _: Instruction.Store | _: Instruction.CopyMemory | _: Instruction.CopyMemorySized | _: Instruction.GroupAsyncCopy | _: Instruction.GroupWaitEvents =>
      executeMemory(program, execution, memory, instruction)
    case _: Instruction.DPdx | _: Instruction.DPdy | _: Instruction.Fwidth |
        _: Instruction.DPdxFine | _: Instruction.DPdyFine | _: Instruction.FwidthFine |
        _: Instruction.DPdxCoarse | _: Instruction.DPdyCoarse | _: Instruction.FwidthCoarse =>
      executeDerivative(program, execution, instruction)
    case _: Instruction.ImageQueryLod | _: Instruction.ImageQuerySamples | _: Instruction.SampledImage | _: Instruction.Image | _: Instruction.ImageFetch | _: Instruction.ImageQuerySizeLod | _: Instruction.ImageQueryLevels | _: Instruction.ImageSampleImplicitLod | _: Instruction.ImageSampleExplicitLod | _: Instruction.ImageSampleDrefImplicitLod | _: Instruction.ImageSampleDrefExplicitLod |
        _: Instruction.ImageSampleProjImplicitLod | _: Instruction.ImageSampleProjExplicitLod |
        _: Instruction.ImageSampleProjDrefImplicitLod | _: Instruction.ImageSampleProjDrefExplicitLod |
        _: Instruction.ImageGather | _: Instruction.ImageDrefGather =>
      executeSampling(program, execution, memory, instruction)
    case _: Instruction.ImageRead | _: Instruction.ImageWrite | _: Instruction.ImageTexelPointer | _: Instruction.ImageQuerySize =>
      executeImage(program, execution, memory, instruction)
    case _: Instruction.BitFieldUExtract | _: Instruction.BitFieldSExtract | _: Instruction.BitFieldInsert | _: Instruction.BitReverse =>
      executeBitField(program, execution, instruction)
    case _: Instruction.ArrayLength | _: Instruction.Bitcast | _: Instruction.BitCount | _: Instruction.PtrCastToGeneric | _: Instruction.GenericCastToPtr | _: Instruction.GenericCastToPtrExplicit | _: Instruction.PtrAccessChain | _: Instruction.InBoundsPtrAccessChain | _: Instruction.AccessChain | _: Instruction.InBoundsAccessChain | _: Instruction.CompositeExtract | _: Instruction.CompositeConstruct | _: Instruction.CompositeInsert | _: Instruction.VectorExtractDynamic | _: Instruction.VectorInsertDynamic =>
      executeComposite(program, execution, memory, instruction)
    case _: Instruction.VectorShuffle | _: Instruction.ExtInst =>
      executeExtended(program, execution, memory, instruction)
    case _: Instruction.CopyLogical | _: Instruction.UConvert | _: Instruction.SConvert | _: Instruction.ConvertPtrToU | _: Instruction.ConvertUToPtr | _: Instruction.ConvertSToF | _: Instruction.ConvertUToF | _: Instruction.ConvertFToS | _: Instruction.ConvertFToU | _: Instruction.FConvert | _: Instruction.QuantizeToF16 | _: Instruction.Undef | _: Instruction.CopyObject | _: Instruction.LifetimeStart | _: Instruction.LifetimeStop =>
      executeConversion(program, execution, memory, instruction)
    case _: Instruction.IAddCarry | _: Instruction.ISubBorrow | _: Instruction.UMulExtended | _: Instruction.SMulExtended =>
      val (t, d, a, b, operation) = instruction match {
        case Instruction.IAddCarry(t, d, a, b)    => (t, d, a, b, 0)
        case Instruction.ISubBorrow(t, d, a, b)   => (t, d, a, b, 1)
        case Instruction.UMulExtended(t, d, a, b) => (t, d, a, b, 2)
        case Instruction.SMulExtended(t, d, a, b) => (t, d, a, b, 3)
        case _ => throw IllegalStateException("extended integer instruction dispatch mismatch")
      }
      integerExtended(program, execution.values, t, d, a, b, operation).map(_ => execution.pc += 1)
    case _: Instruction.IAdd | _: Instruction.ISub | _: Instruction.IMul | _: Instruction.UDiv | _: Instruction.UMod | _: Instruction.SDiv | _: Instruction.SRem | _: Instruction.SMod | _: Instruction.SNegate =>
      executeInteger(program, execution, instruction)
    case _: Instruction.AtomicStore | _: Instruction.AtomicLoad | _: Instruction.AtomicExchange | _: Instruction.AtomicCompareExchange | _: Instruction.AtomicCompareExchangeWeak | _: Instruction.AtomicFlagTestAndSet | _: Instruction.AtomicFlagClear | _: Instruction.AtomicFAddEXT | _: Instruction.AtomicFMinEXT | _: Instruction.AtomicFMaxEXT | _: Instruction.AtomicIAdd | _: Instruction.AtomicUMin | _: Instruction.AtomicIIncrement | _: Instruction.AtomicIDecrement | _: Instruction.AtomicISub | _: Instruction.AtomicSMin | _: Instruction.AtomicSMax | _: Instruction.AtomicUMax | _: Instruction.AtomicAnd | _: Instruction.AtomicOr | _: Instruction.AtomicXor | _: Instruction.FAdd | _: Instruction.FSub | _: Instruction.FMul | _: Instruction.Dot | _: Instruction.FDiv | _: Instruction.FRem | _: Instruction.FMod | _: Instruction.VectorTimesScalar =>
      executeAtomic(program, execution, memory, instruction)
    case _: Instruction.Transpose | _: Instruction.MatrixTimesScalar | _: Instruction.VectorTimesMatrix |
        _: Instruction.MatrixTimesVector | _: Instruction.MatrixTimesMatrix | _: Instruction.OuterProduct =>
      val (resultType, dest, operation, sources) = instruction match {
        case Instruction.Transpose(t, d, a)            => (t, d, MatrixOperation.Transpose, Vec(a))
        case Instruction.MatrixTimesScalar(t, d, a, b) => (t, d, MatrixOperation.Scale, Vec(a, b))
        case Instruction.VectorTimesMatrix(t, d, a, b) => (t, d, MatrixOperation.VectorMatrix, Vec(a, b))
        case Instruction.MatrixTimesVector(t, d, a, b) => (t, d, MatrixOperation.MatrixVector, Vec(a, b))
        case Instruction.MatrixTimesMatrix(t, d, a, b) => (t, d, MatrixOperation.Multiply, Vec(a, b))
        case Instruction.OuterProduct(t, d, a, b)      => (t, d, MatrixOperation.OuterProduct, Vec(a, b))
        case _ => throw IllegalStateException("matrix instruction dispatch mismatch")
      }
      floatingMatrix(program, execution.values, resultType, dest, operation, sources).map(_ => execution.pc += 1)
    case _: Instruction.FNegate | _: Instruction.ShiftLeftLogical =>
      executeFloatingAndShift(program, execution, instruction)
    case _: Instruction.ShiftRightLogical | _: Instruction.ShiftRightArithmetic | _: Instruction.BitwiseOr | _: Instruction.BitwiseXor | _: Instruction.BitwiseAnd | _: Instruction.Not | _: Instruction.IEqual | _: Instruction.INotEqual | _: Instruction.LogicalEqual | _: Instruction.LogicalNotEqual | _: Instruction.LogicalOr | _: Instruction.LogicalAnd | _: Instruction.LogicalNot | _: Instruction.Any | _: Instruction.All | _: Instruction.FOrdEqual | _: Instruction.FOrdNotEqual | _: Instruction.FUnordEqual | _: Instruction.FUnordNotEqual | _: Instruction.FUnordLessThan | _: Instruction.FUnordGreaterThan | _: Instruction.FUnordLessThanEqual | _: Instruction.FUnordGreaterThanEqual | _: Instruction.Unordered | _: Instruction.Ordered | _: Instruction.FOrdLessThanEqual | _: Instruction.FOrdLessThan | _: Instruction.FOrdGreaterThan | _: Instruction.FOrdGreaterThanEqual | _: Instruction.IsInf | _: Instruction.IsNan | _: Instruction.IsFinite =>
      executeArithmetic(program, execution, instruction)
    case _: Instruction.IsNormal | _: Instruction.SignBitSet | _: Instruction.ULessThan | _: Instruction.UGreaterThan | _: Instruction.ULessThanEqual | _: Instruction.UGreaterThanEqual | _: Instruction.SGreaterThan | _: Instruction.SGreaterThanEqual | _: Instruction.SLessThan | _: Instruction.SLessThanEqual | _: Instruction.Select =>
      executeComparison(program, execution, instruction)
    case _: Instruction.ControlBarrier | _: Instruction.MemoryBarrier | _: Instruction.GroupAny | _: Instruction.GroupAll | _: Instruction.GroupUMin | _: Instruction.GroupUMax | _: Instruction.GroupSMin | _: Instruction.GroupSMax | _: Instruction.GroupIAdd | _: Instruction.GroupIMulKHR | _: Instruction.GroupBitwiseAndKHR | _: Instruction.GroupBitwiseOrKHR | _: Instruction.GroupBitwiseXorKHR | _: Instruction.GroupLogicalAndKHR | _: Instruction.GroupLogicalOrKHR | _: Instruction.GroupLogicalXorKHR | _: Instruction.GroupFAdd | _: Instruction.GroupFMin | _: Instruction.GroupFMax | _: Instruction.GroupFMulKHR | _: Instruction.SubgroupShuffleINTEL | _: Instruction.SubgroupShuffleXorINTEL | _: Instruction.SubgroupShuffleUpINTEL | _: Instruction.SubgroupShuffleDownINTEL | _: Instruction.GroupBroadcast =>
      executeCollective(program, execution, memory, instruction)
    case value if VMCollectives.isNonUniform(value) =>
      executeNonUniform(program, execution, value)
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
      execution: InvocationState,
      instruction: Instruction
  ): Either[Fault, Unit] = {
    val values = execution.values
    instruction match {
      case Instruction.FunctionParameter(_, _) => execution.pc += 1
      case Instruction.Label(label)            =>
        execution.currentLabel = id(label)
        execution.pc += 1
        if (execution.fragmentQuad || execution.subgroupReconvergence) {
          val merge = execution.fragmentRegions.indexWhere(_.merge == id(label))
          if (merge >= 0) {
            execution.fragmentRegions.remove(merge + 1, execution.fragmentRegions.size - merge - 1)
            execution.fragmentJoin = Some(FragmentJoin.Merge(id(label)))
          } else {
            // A continue may bypass nested selection merges on its way to the loop header.
            val header = execution.fragmentRegions.indexWhere(r => r.loop && r.header == id(label))
            if (header >= 0) execution.fragmentRegions.remove(header + 1, execution.fragmentRegions.size - header - 1)
          }
        }
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
      execution: InvocationState,
      memory: MemoryState,
      instruction: Instruction
  ): Memory.ReadResult[Unit] = {
    val values = execution.values
    instruction match {
      case Instruction.Variable(resultType, dest, storageClass, initializer) =>
        val pointee = program.layout.pointerPointee(id(resultType))
        val buffer  = Array.ofDim[Byte](program.layout.typeSize(pointee))
        val slots   = mutable.LongMap.empty[(Int, Pointer)]
        program.layout.pointerLeafOffsets(pointee, 0L).toOption.foreach(_.foreach { case (offset, pointerType) =>
          program.layout.runtimeType(pointerType) match {
            case Some(RuntimeType.Pointer(pointerStorage, pointerPointee)) =>
              slots.update(offset, pointerType -> Pointer(Region.Null, 0L, pointerPointee, pointerStorage))
            case _ => ()
          }
        })
        initializer.flatMap(value => values.get(id(value))) match {
          case Some(value) => program.encode(value) match {
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
              Option.when(slots.nonEmpty)(slots),
              bounds = Option.when(
                program.vulkanEntries.nonEmpty && (id(storageClass) == StorageClass.Private || id(
                  storageClass
                ) == StorageClass.Function)
              )(0L -> buffer.length.toLong)
            )
          )
        )
        execution.pc += 1
      case Instruction.Load(resultType, dest, pointer, _) =>
        address(values, pointer) match {
          case Left(error)   => return Left(error)
          case Right(source) =>
            program.layout.runtimeType(id(resultType)) match {
              case Some(RuntimeType.MultisampledTexture(_, _)) | Some(RuntimeType.InputAttachment(
                    _,
                    _
                  )) | Some(RuntimeType.StorageImage(_, _, _, _, _, _)) | Some(RuntimeType.Texture(
                    _,
                    _,
                    _,
                    _
                  )) | Some(RuntimeType.CubeTexture(_, _)) | Some(RuntimeType.Sampler) =>
                values.update(id(dest), RuntimeValue.Address(id(resultType), source))
                execution.pc += 1
              case Some(RuntimeType.SampledImage(imageType))
                  if program.layout.runtimeType(imageType).exists {
                    case RuntimeType.Texture(_, _, true, _) => true
                    case _                                  => false
                  } =>
                // GLSL samplerBuffer types use OpTypeSampledImage, but Vulkan
                // uniform texel buffers carry an image view without a sampler.
                values.update(id(dest), RuntimeValue.Address(id(resultType), source.copy(pointee = imageType)))
                execution.pc += 1
              case Some(RuntimeType.SampledImage(imageType)) =>
                val loaded = for {
                  size  <- memory.readScalar(source.copy(offset = source.offset + 8), 8)
                  image <- memory.descriptorRelocatedPointer(
                    source,
                    imageType,
                    0
                  ).toRight(Fault.InvalidTask("sampled-image descriptor is missing its image relocation"))
                  _ <- Either.cond(
                    size >= 16 && size <= Int.MaxValue && image.offset >= 0 && image.offset <= Long.MaxValue - size,
                    (),
                    Fault.InvalidTask("invalid sampled-image descriptor bounds")
                  )
                } yield image.copy(
                  bounds = Some(image.offset -> (image.offset + size)),
                  sampler = Some(source.copy(offset = source.offset + 16))
                )
                loaded match {
                  case Left(error)  => return Left(error)
                  case Right(image) =>
                    values.update(id(dest), RuntimeValue.Address(id(resultType), image)); execution.pc += 1
                }
              case Some(RuntimeType.Pointer(_, _)) =>
                loadPointer(program, memory, source, id(resultType)) match {
                  case Left(error) =>
                    return Left(withExecutionContext(error, execution.currentFunction, execution.pc, instruction))
                  case Right(value) =>
                    values.update(id(dest), value)
                    execution.pc += 1
                }
              case Some(RuntimeType.Bool) | Some(RuntimeType.Integer(_, _)) | Some(RuntimeType.Floating(_)) =>
                program.layout.scalarMemoryWidth(id(resultType)) match {
                  case Left(error) => return Left(error)
                  case Right(size) => memory.readScalar(source, size) match {
                      case Left(error)   => return Left(error)
                      case Right(loaded) =>
                        values.updateScalar(id(dest), id(resultType), loaded)
                        execution.pc += 1
                    }
                }
              case _ if mappedMatrixStorage(program, source) =>
                loadMatrixStorage(program, memory, source) match {
                  case Left(error)  => return Left(error)
                  case Right(value) => values.update(id(dest), retag(value, id(resultType))); execution.pc += 1
                }
              case _ => program.layout.nonNegativeTypeSize(id(resultType)) match {
                  case Left(error) => return Left(error)
                  case Right(size) => memory.read(source, size) match {
                      case Left(error)  => return Left(error)
                      case Right(bytes) =>
                        program.decode(id(resultType), bytes) match {
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
            program.layout.scalarMemoryWidth(sourceType) match {
              case Left(error) => return Left(error)
              case Right(size) =>
                memory.writeScalar(target, values.scalarBits(id(source)), size) match {
                  case Left(error) =>
                    return Left(withExecutionContext(error, execution.currentFunction, execution.pc, instruction))
                  case Right(()) =>
                    if (size.raw == 8)
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
              case Some(value: RuntimeValue.Bits) if mappedMatrixStorage(program, target) =>
                storeMatrixStorage(program, memory, target, value) match {
                  case Left(error) => return Left(error)
                  case Right(())   => execution.pc += 1
                }
              case Some(value) => program.encode(value) match {
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
          case (Right(destination), Right(origin))
              if mappedMatrixStorage(program, destination) || mappedMatrixStorage(program, origin) =>
            loadMatrixStorage(program, memory, origin).flatMap(value =>
              storeMatrixStorage(program, memory, destination, value)
            ) match {
              case Left(error) => return Left(error)
              case Right(())   => execution.pc += 1
            }
          case (Right(destination), Right(origin)) =>
            program.layout.nonNegativeTypeSize(origin.pointee) match {
              case Left(error) => return Left(error)
              case Right(size) => memory.read(origin, size) match {
                  case Left(error)  => return Left(error)
                  case Right(bytes) =>
                    memory.write(destination, bytes).flatMap(_ =>
                      memory.copySlots(destination, origin, size.raw)
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
            val byteCount = rawSize.toInt.unsafe[Int >= 0]
            memory.read(origin, byteCount) match {
              case Left(error)  => return Left(error)
              case Right(bytes) =>
                memory.write(destination, bytes).flatMap(_ =>
                  memory.copySlots(destination, origin, byteCount.raw.toLong)
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

  // Vulkan storage-image ABI: width, height, depth, SPIR-V ImageFormat (four LE u32s), then texels.
  // Image objects retain an address so descriptor aliases and image atomics share the memory model.
  private def imageFloatComponents(format: Int): Int = format match {
    case ImageDescriptor.Format.DecodedRgb32f => 3 // Decoded RGB-only compressed view; alpha defaults to one.
    case ImageFormat.Rgba32f                  => 4
    case ImageFormat.Rg32f                    => 2
    case ImageFormat.R32f | ImageDescriptor.Format.D32SfloatS8Depth => 1
    case _                                                          => 0
  }
  private def imageIsInteger(format: Int): Boolean =
    format == ImageDescriptor.Format.S8Uint || format == ImageDescriptor.Format.D32SfloatS8Stencil || IntegerImageFormat(
      format
    ).isDefined
  private def imageWordComponents(format: Int): Int = imageFloatComponents(format)

  private def readIntegerTexel(
      memory: MemoryState,
      pointer: Pointer,
      layout: IntegerImageFormat.Layout,
      valid: Boolean = true
  ): Memory.ReadResult[Array[Long]] =
    (0 until layout.components).foldLeft[Memory.ReadResult[Array[Long]]](Right(Array(
      0L,
      0L,
      0L,
      if (valid) 1L else 0L
    ))) {
      (result, lane) =>
        for {
          data <- result
          word <- memory.readScalar(
            pointer.copy(offset = pointer.offset + layout.offset(lane), pointee = pointer.pointee),
            layout.componentBytes
          )
        } yield { data(lane) = layout.decode(word, lane); data }
    }

  private def readNormalizedTexel(
      memory: MemoryState,
      pointer: Pointer,
      layout: NormalizedImageFormat.Layout,
      valid: Boolean = true
  ): Memory.ReadResult[Array[Double]] =
    (0 until layout.components).foldLeft[Memory.ReadResult[Array[Double]]](Right(Array(
      0.0,
      0.0,
      0.0,
      if (valid) 1.0 else 0.0
    ))) {
      (result, lane) =>
        for {
          data <- result
          word <-
            memory.readScalar(pointer.copy(offset = pointer.offset + layout.channel(lane)), 1)
        } yield { data(lane) = layout.decode(word, lane); data }
    }

  private def readNarrowTexel(
      memory: MemoryState,
      pointer: Pointer,
      layout: NarrowImageFormat.Layout,
      valid: Boolean = true
  ): Memory.ReadResult[Array[Double]] =
    (0 until layout.components).foldLeft[Memory.ReadResult[Array[Double]]](Right(Array(
      0.0,
      0.0,
      0.0,
      if (valid) 1.0 else 0.0
    ))) {
      (result, lane) =>
        for {
          data <- result
          word <- memory.readScalar(
            pointer.copy(offset = pointer.offset + layout.offset(lane), pointee = pointer.pointee),
            layout.elementBytes
          )
        } yield { data(lane) = layout.decode(word, lane); data }
    }

  private case class ImageLevel(
      pixels: Pointer,
      width: Long,
      height: Long,
      format: Int,
      samples: Int = 1,
      layers: Long = 1,
      depth: Long = 1
  )

  private def imageLevels(memory: MemoryState, image: Pointer): Memory.ReadResult[Int] =
    for {
      format <-
        memory.readScalar(image.copy(offset = image.offset + ImageDescriptor.Layout.Format), 4)
      levels <- if ((format & ImageDescriptor.Flags.LevelTable) == 0) Right(1L)
      else memory.readScalar(image.copy(offset = image.offset + ImageDescriptor.Layout.Levels), 4)
      _ <- Either.cond(levels >= 1 && levels <= 15, (), Fault.InvalidTask("invalid image level count"))
    } yield levels.toInt

  // Swizzles apply after filtering and missing-component/border replacement.
  // Bit 29 adds four VkComponentSwizzle words following the V2 level table.
  private def swizzleImage(
      memory: MemoryState,
      image: Pointer,
      values: Array[Double]
  ): Memory.ReadResult[Array[Double]] =
    memory.readScalar(
      image.copy(offset = image.offset + ImageDescriptor.Layout.Format),
      4
    ).flatMap { format =>
      if ((format & ImageDescriptor.Flags.Swizzle) == 0) Right(values)
      else for {
        _ <- Either.cond(
          (format & ImageDescriptor.Flags.LevelTable) != 0,
          (),
          Fault.InvalidTask("swizzle requires a V2 image view")
        )
        levels <- imageLevels(memory, image)
        _      <- Either.cond(
          image.bounds.exists { case (begin, end) =>
            image.offset >= begin && ImageDescriptor.Layout.LevelTable + ImageDescriptor.Layout.SwizzleBytes + levels * ImageDescriptor.Layout.LevelBytes <= end - image.offset
          },
          (),
          Fault.InvalidTask("image swizzle exceeds descriptor")
        )
        result <-
          (0 until 4).foldLeft[Memory.ReadResult[Array[Double]]](Right(Array.ofDim[Double](4))) { (result, lane) =>
            for {
              out       <- result
              component <- memory.readScalar(
                image.copy(offset =
                  image.offset + ImageDescriptor.Layout.LevelTable + levels * ImageDescriptor.Layout.LevelBytes + lane * 4
                ),
                4
              )
              _ <- Either.cond(
                component <= ImageDescriptor.Swizzle.A,
                (),
                Fault.InvalidTask("invalid image component swizzle")
              )
            } yield {
              out(lane) = if (component == ImageDescriptor.Swizzle.Identity) values(lane)
              else if (component == ImageDescriptor.Swizzle.Zero) 0.0
              else if (component == ImageDescriptor.Swizzle.One) 1.0
              else values(component.toInt - ImageDescriptor.Swizzle.R);
              out
            }
          }
      } yield result
    }

  // V2 views carry a level table and a relocated pointer to shared texels. V1
  // descriptors retain the inline single-level layout used by portable VM clients.
  // Format bit 30 marks a volume: header word 2 is base-view depth, shifted by
  // the view-relative mip. Without that bit, V2 word 2 is the constant layer count.
  private def imageLevel(
      memory: MemoryState,
      image: Pointer,
      level: Int,
      expectedFormat: Int = -1,
      texelBuffer: Boolean = false
  ): Memory.ReadResult[ImageLevel] =
    for {
      rawFormat <-
        memory.readScalar(image.copy(offset = image.offset + ImageDescriptor.Layout.Format), 4)
      format = (rawFormat & ImageDescriptor.Flags.FormatMask).toInt
      volume = (rawFormat & ImageDescriptor.Flags.Volume) != 0
      levels <- imageLevels(memory, image)
      _      <- Either.cond(
        level >= 0 && level < levels && ImageTexelCodec.texelBytes(
          format
        ) != 0 && (expectedFormat < 0 || format == expectedFormat),
        (),
        Fault.InvalidTask(s"invalid image level $level of $levels or format $format (expected $expectedFormat)")
      )
      data <- if ((rawFormat & ImageDescriptor.Flags.LevelTable) != 0) {
        val table = image.offset + ImageDescriptor.Layout.LevelTable + level * ImageDescriptor.Layout.LevelBytes
        for {
          _ <- Either.cond(
            image.bounds.exists { case (begin, end) =>
              image.offset >= begin && ImageDescriptor.Layout.LevelTable + levels * ImageDescriptor.Layout.LevelBytes <= end - image.offset
            },
            (),
            Fault.InvalidTask("image level table exceeds descriptor")
          )
          size <- memory.readScalar(
            image.copy(offset = image.offset + ImageDescriptor.Layout.TexelBytes),
            8
          )
          base <- memory.descriptorRelocatedPointer(
            image.copy(offset = image.offset + ImageDescriptor.Layout.Texels),
            image.pointee,
            image.storageClass
          ).toRight(Fault.InvalidTask("image view is missing its texel relocation"))
          width  <- memory.readScalar(image.copy(offset = table), 4)
          height <-
            memory.readScalar(image.copy(offset = table + ImageDescriptor.Layout.LevelHeight), 4)
          offset <-
            memory.readScalar(image.copy(offset = table + ImageDescriptor.Layout.LevelOffset), 8)
          slices <- memory.readScalar(
            image.copy(offset = image.offset + ImageDescriptor.Layout.Slices),
            4
          )
          rawSamples <- memory.readScalar(
            image.copy(offset = image.offset + ImageDescriptor.Layout.Samples),
            4
          )
          layers          = if (volume) 1L else slices
          depth           = if (volume) Math.max(1L, slices >> level) else 1L
          samples         = if (rawSamples == 0L) 1 else rawSamples.toInt
          bytes           = width * height * ImageTexelCodec.texelBytes(format) * samples * layers * depth
          validDimensions = width > 0 && height > 0 && (
            if (texelBuffer) !volume && layers == 1 && width <= 0xffffffffL && height == 1
            else if (volume) samples == 1 && slices >= 1 && slices <= 2048 && width <= 2048 && height <= 2048
            else width <= 16384 && height <= 16384
          )
          _ <- Either.cond(
            validDimensions && (samples == 1 || samples == 4) && layers >= 1 && layers <= 2048 &&
              size > 0 && size <= Int.MaxValue && offset >= 0 && offset <= size && bytes <= size - offset &&
              base.offset >= 0 && base.offset <= Long.MaxValue - size,
            (),
            Fault.InvalidTask("image level exceeds texel allocation")
          )
        } yield ImageLevel(
          base.copy(
            offset = base.offset + offset,
            bounds = Some((base.offset + offset) -> (base.offset + offset + bytes))
          ),
          width,
          height,
          format,
          samples,
          layers,
          depth
        )
      } else for {
        width  <- memory.readScalar(image, 4)
        height <- memory.readScalar(image.copy(offset = image.offset + 4), 4)
        depth  <-
          memory.readScalar(image.copy(offset = image.offset + ImageDescriptor.Layout.Slices), 4)
        bytes           = width * height * depth * ImageTexelCodec.texelBytes(format)
        validDimensions = width > 0 && height > 0 && (
          if (texelBuffer) !volume && width <= 0xffffffffL && height == 1 && depth == 1
          else if (volume) depth >= 1 && depth <= 2048 && width <= 2048 && height <= 2048
          else width <= 16384 && height <= 16384 && depth == 1
        )
        _ <- Either.cond(
          validDimensions && image.bounds.exists {
            case (begin, end) => image.offset >= begin && 16 + bytes <= end - image.offset
          },
          (),
          Fault.InvalidTask("invalid storage-image descriptor")
        )
      } yield ImageLevel(
        image.copy(
          offset = image.offset + ImageDescriptor.Layout.Texels,
          bounds = Some(
            (image.offset + ImageDescriptor.Layout.Texels) -> (image.offset + ImageDescriptor.Layout.Texels + bytes)
          )
        ),
        width,
        height,
        format,
        depth = depth
      )
    } yield data

  private def imageTexel(
      program: Program,
      memory: MemoryState,
      values: InvocationValues,
      image: Operand,
      coordinate: Operand
  ): Memory.ReadResult[(Pointer, Int)] =
    for {
      source    <- address(values, image)
      imageType <- program.layout.runtimeType(source.pointee) match {
        case Some(RuntimeType.StorageImage(sampledType, format, dimensions, texelBuffer, arrayed, cube)) =>
          Right((sampledType, format, dimensions, texelBuffer, arrayed || cube))
        case _ => Left(Fault.InvalidTask("image operand does not identify a storage image"))
      }
      data   <- imageLevel(memory, source, 0, if (program.vulkanEntries.nonEmpty) -1 else imageType._2, imageType._4)
      coords <- bits(values, coordinate)
      _      <- Either.cond(
        coords.length == imageType._3 + (if (imageType._5) 1 else 0),
        (),
        Fault.InvalidTask("image coordinate component count does not match dimensionality")
      )
    } yield {
      val width      = data.width
      val height     = data.height
      val x          = coords(0).toInt.toLong
      val y          = if (imageType._3 == 1) 0L else coords(1).toInt.toLong
      val z          = if (imageType._3 == 3) coords(2).toInt.toLong else 0L
      val layer      = if (imageType._5) coords(imageType._3).toInt.toLong else 0L
      val begin      = data.pixels.offset
      val texelBytes = ImageTexelCodec.texelBytes(data.format)
      val end        = begin + width * height * data.layers * data.depth * texelBytes
      val offset     =
        if (
          x < 0 || y < 0 || x >= width || y >= height || z < 0 || z >= data.depth || layer < 0 || layer >= data.layers
        ) end
        else begin + (((layer * data.depth + z) * height + y) * width + x) * texelBytes
      (
        data.pixels.copy(
          offset = offset,
          pointee = imageType._1,
          storageClass = StorageClass.Image,
          bounds = Some(begin -> end)
        ),
        data.format
      )
    }

  // Sampler ABI: min/mag filters, U/V address modes, unnormalized, compare enable/op,
  // border color, float bias/min/max LOD, mipmap mode, then W address mode.
  // The trailing W word is only required for volume sampling; older 2D clients retain 48-byte packets.
  private def sampleTexel(
      memory: MemoryState,
      image: Pointer,
      format: Int,
      width: Long,
      x: Long,
      y: Long
  ): Memory.ReadResult[Array[Double]] = {
    val pointer =
      image.copy(offset =
        image.offset + (y * width + x) * ImageTexelCodec.texelBytes(
          format
        ) + (if (format == ImageDescriptor.Format.D32SfloatS8Stencil) 4 else 0)
      )
    val components = imageWordComponents(format)
    if (IntegerImageFormat(format).isDefined) {
      val layout = IntegerImageFormat(format).get
      readIntegerTexel(memory, pointer, layout).map(_.map(v => if (layout.signed) v.toInt.toDouble else v.toDouble))
    } else if (NarrowImageFormat(format).isDefined) {
      readNarrowTexel(memory, pointer, NarrowImageFormat(format).get)
    } else if (NormalizedImageFormat(format).isDefined) {
      readNormalizedTexel(memory, pointer, NormalizedImageFormat(format).get)
    } else if (components != 0) {
      (0 until components).foldLeft[Memory.ReadResult[Array[Double]]](Right(Array(0.0, 0.0, 0.0, 1.0))) {
        (result, lane) =>
          for {
            data <- result
            word <- memory.readScalar(pointer.copy(offset = pointer.offset + lane * 4), 4)
          } yield {
            data(lane) =
              if (imageIsInteger(format)) word.toDouble else IEEE754.binary32ToFloat(Binary32.low(word)).toDouble;
            data
          }
      }
    } else memory.readScalar(
      pointer,
      if (format == ImageDescriptor.Format.D16Unorm) 2
      else if (format == ImageDescriptor.Format.S8Uint || format == ImageDescriptor.Format.D32SfloatS8Stencil)
        1
      else 4
    ).map {
      word =>
        if (format == ImageDescriptor.Format.D16Unorm) Array(word.toDouble / 65535.0, 0.0, 0.0, 1.0)
        else if (
          format == ImageFormat.R32ui || format == ImageDescriptor.Format.S8Uint || format == ImageDescriptor.Format.D32SfloatS8Stencil
        ) Array(word.toDouble, 0.0, 0.0, 1.0)
        else Array.tabulate(4) { lane =>
          val component = if (format == ImageDescriptor.Format.Bgra8Unorm && lane < 3) 2 - lane else lane
          val value     = ((word >>> (component * 8)) & 255).toDouble
          if (format == ImageFormat.Rgba8ui) value else value / 255.0
        }
    }
  }
  private def sampleAddress(value: Long, size: Long, mode: Int): Option[Long] = mode match {
    case ImageDescriptor.AddressMode.Repeat         => Some(Math.floorMod(value, size))
    case ImageDescriptor.AddressMode.MirroredRepeat =>
      val wrapped = Math.floorMod(value, 2 * size); Some(if (wrapped >= size) 2 * size - 1 - wrapped else wrapped)
    case ImageDescriptor.AddressMode.ClampToEdge       => Some(Math.max(0L, Math.min(size - 1, value)))
    case ImageDescriptor.AddressMode.ClampToBorder     => Option.when(value >= 0 && value < size)(value)
    case ImageDescriptor.AddressMode.MirrorClampToEdge =>
      Some(Math.max(0L, Math.min(size - 1, if (value < 0) -value - 1 else value)))
    case _ => None
  }
  private def sampleCompare(op: Int, reference: Double, depth: Double): Double = {
    val pass = op match {
      case ImageDescriptor.CompareOp.Never          => false
      case ImageDescriptor.CompareOp.Less           => reference < depth
      case ImageDescriptor.CompareOp.Equal          => reference == depth
      case ImageDescriptor.CompareOp.LessOrEqual    => reference <= depth
      case ImageDescriptor.CompareOp.Greater        => reference > depth
      case ImageDescriptor.CompareOp.NotEqual       => reference != depth
      case ImageDescriptor.CompareOp.GreaterOrEqual => reference >= depth
      case ImageDescriptor.CompareOp.Always         => true
      case _                                        => false
    }
    if (pass) 1.0 else 0.0
  }
  private val imageZeroOffset    = Array(0L, 0L, 0L)
  private val imageGatherOffsets = Array((0L, 1L), (1L, 1L), (1L, 0L), (0L, 0L))

  // ConstOffset is measured in texels at the selected level, and never moves
  // the array layer. These bounds match VkPhysicalDeviceLimits.
  private def imageConstOffset(
      values: InvocationValues,
      operand: Int,
      dimensions: Int
  ): Memory.ReadResult[Array[Long]] =
    values.get(operand) match {
      case Some(RuntimeValue.Bits(_, lanes)) if lanes.length == dimensions =>
        val offset = lanes.map(_.toInt.toLong)
        Either.cond(
          offset.forall(v => v >= -8 && v <= 7),
          offset,
          Fault.InvalidTask("image ConstOffset exceeds device limits")
        )
      case _ => Left(Fault.InvalidTask("image ConstOffset dimensionality does not match"))
    }

  private def imageGradientLod(
      level: ImageLevel,
      direction: Array[Double],
      dx: Array[Double],
      dy: Array[Double],
      dimensions: Int,
      projection: Option[CubeMap.Projection],
      unnormalized: Boolean
  ): Double = {
    val width    = if (unnormalized) 1.0 else level.width.toDouble
    val height   = if (unnormalized) 1.0 else level.height.toDouble
    val (gx, gy) = projection match {
      case Some(p) =>
        val x = CubeMap.gradient(p.face, direction, dx); val y = CubeMap.gradient(p.face, direction, dy)
        (Array(x._1, x._2), Array(y._1, y._2))
      case None => (dx, dy)
    }
    val rho = if (dimensions == 1) Math.max(Math.abs(gx(0) * width), Math.abs(gy(0) * width))
    else if (dimensions == 3 && projection.isEmpty) {
      val depth = if (unnormalized) 1.0 else level.depth.toDouble
      Math.max(
        Math.hypot(Math.hypot(gx(0) * width, gx(1) * height), gx(2) * depth),
        Math.hypot(Math.hypot(gy(0) * width, gy(1) * height), gy(2) * depth)
      )
    } else Math.max(Math.hypot(gx(0) * width, gx(1) * height), Math.hypot(gy(0) * width, gy(1) * height))
    Math.log(rho) / Math.log(2.0)
  }

  private def sampleImage(
      memory: MemoryState,
      values: InvocationValues,
      image: Pointer,
      coords: Array[Long],
      dref: Option[Double],
      explicit: Boolean,
      mods: Option[Operand],
      gradients: Option[(Array[Double], Array[Double])],
      dimensions: Int,
      arrayed: Boolean,
      cube: Boolean,
      gather: Option[Int],
      vulkan: Boolean
  ): Memory.ReadResult[Array[Long]] = {
    def float(word: Long): Double = IEEE754.binary32ToFloat(Binary32.low(word)).toDouble
    val volume                    = dimensions == 3 && !cube
    for {
      sampler <- image.sampler.toRight(Fault.InvalidTask("sample operation has no sampler"))
      words   <-
        (0 until (if (volume) 13 else 12)).foldLeft[Memory.ReadResult[Array[Long]]](Right(Array.ofDim[Long](if (volume)
          13
        else 12))) { (result, index) =>
          for {
            data <- result
            word <- memory.readScalar(sampler.copy(offset = sampler.offset + index * 4), 4)
          } yield { data(index) = word; data }
        }
      formatWord <- memory.readScalar(image.copy(offset = image.offset + 12), 4)
      format = (formatWord & ImageDescriptor.Flags.FormatMask).toInt
      levels <- imageLevels(memory, image)
      _      <- Either.cond(
        coords.length >= dimensions + (if (arrayed) 1 else 0) && coords.take(dimensions + (if (arrayed) 1
                                                                                           else 0)).forall(w =>
          java.lang.Double.isFinite(float(w))
        ),
        (),
        Fault.InvalidTask("invalid sample coordinate dimensionality")
      )
      direction  = coords.take(dimensions).map(float(_))
      projection = if (cube) Some(CubeMap.project(direction(0), direction(1), direction(2))) else None
      _ <- Either.cond(
        !cube || words(ImageDescriptor.SamplerWord.Unnormalized) == 0,
        (),
        Fault.InvalidTask("cube sampling requires normalized coordinates")
      )
      operands    = mods.map(_.words.toVector.map(_.raw).toArray).getOrElse(Array(0))
      mask        = operands.headOption.getOrElse(-1)
      lodMask     = mask & (ImageOperands.Bias | ImageOperands.Lod | ImageOperands.Grad)
      offsetIndex = 1 + (if (lodMask == ImageOperands.Grad) 2 else if (lodMask != 0) 1 else 0)
      _ <- Either.cond(
        if (gather.isDefined)
          mask == 0 || mask == ImageOperands.ConstOffset || mask == ImageOperands.Offset || mask == ImageOperands.ConstOffsets
        else
          (mask & ~(ImageOperands.Bias | ImageOperands.Lod | ImageOperands.Grad | ImageOperands.ConstOffset)) == 0 && (lodMask == 0 || lodMask == ImageOperands.Bias || lodMask == ImageOperands.Lod || lodMask == ImageOperands.Grad),
        (),
        Fault.Unsupported("unsupported sample/gather image operands")
      )
      _ <- Either.cond(
        operands.length == offsetIndex + (if (
                                            (mask & (ImageOperands.ConstOffset | ImageOperands.Offset | ImageOperands.ConstOffsets)) != 0
                                          ) 1
                                          else 0),
        (),
        Fault.InvalidTask("invalid sample operand count")
      )
      offset <- if ((mask & (ImageOperands.ConstOffset | ImageOperands.Offset)) == 0) Right(imageZeroOffset)
      else if (cube) Left(Fault.InvalidTask("cube sampling does not allow ConstOffset"))
      else imageConstOffset(values, operands(offsetIndex), dimensions)
      gatherOffsets <- if ((mask & ImageOperands.ConstOffsets) == 0) Right(imageGatherOffsets)
      else if (cube) Left(Fault.InvalidTask("cube gather does not allow ConstOffsets"))
      else values.get(operands(offsetIndex)) match {
        case Some(RuntimeValue.Bits(_, lanes)) if lanes.length == 8 =>
          val offsets = lanes.map(_.toInt.toLong)
          Either.cond(
            offsets.forall(v => v >= -8 && v <= 7),
            Array.tabulate(4)(i => (offsets(i * 2), offsets(i * 2 + 1))),
            Fault.InvalidTask("gather ConstOffsets exceeds device limits")
          )
        case _ => Left(Fault.InvalidTask("gather ConstOffsets requires four two-dimensional offsets"))
      }
      lod <- if (lodMask == ImageOperands.Bias || lodMask == ImageOperands.Lod) {
        values.get(operands(1)) match {
          case Some(RuntimeValue.Bits(_, lanes)) if lanes.length == 1 => Right(float(lanes(0)))
          case _ => Left(Fault.InvalidTask("sample LOD is not scalar"))
        }
      } else Right(0.0)
      resolvedGradients <- if (lodMask == ImageOperands.Grad) {
        (values.get(operands(1)), values.get(operands(2))) match {
          case (Some(RuntimeValue.Bits(_, dx)), Some(RuntimeValue.Bits(_, dy))) =>
            Right(Some((dx.take(dimensions).map(float(_)), dy.take(dimensions).map(float(_)))))
          case _ => Left(Fault.InvalidTask("sample gradients are not floating values"))
        }
      } else Right(gradients.map((dx, dy) => (dx.take(dimensions), dy.take(dimensions))))
      _ <- Either.cond(
        gather.isDefined || !explicit || lodMask == ImageOperands.Lod || lodMask == ImageOperands.Grad,
        (),
        Fault.Unsupported("explicit sampling requires Lod or Grad")
      )
      _ <- Either.cond(
        gather.isDefined || explicit || gradients.nonEmpty || words(ImageDescriptor.SamplerWord.MinFilter) == words(
          ImageDescriptor.SamplerWord.MagFilter
        ) || float(words(ImageDescriptor.SamplerWord.MaxLod)) <= 0 || float(
          words(ImageDescriptor.SamplerWord.MinLod)
        ) > 0,
        (),
        Fault.Unsupported("implicit LOD with distinct min/mag filters needs fragment derivatives")
      )
      baseLod <- if (lodMask == ImageOperands.Lod) Right(lod)
      else resolvedGradients match {
        case None           => Right(0.0)
        case Some((dx, dy)) => imageLevel(memory, image, 0).flatMap { level =>
            Either.cond(
              dx.length == dimensions && dy.length == dimensions,
              (),
              Fault.InvalidTask("sample gradient dimensionality does not match")
            ).map { _ =>
              imageGradientLod(
                level,
                direction,
                dx,
                dy,
                dimensions,
                projection,
                words(ImageDescriptor.SamplerWord.Unnormalized) != 0
              )
            }
          }
      }
      // Sampler bias applies to both explicit and implicit LOD. Shader Bias is
      // combined with it before the device's maxSamplerLodBias clamp.
      bias = Math.max(
        -16.0,
        Math.min(
          16.0,
          float(words(ImageDescriptor.SamplerWord.MipLodBias)) + (if (lodMask == ImageOperands.Bias) lod else 0.0)
        )
      )
      lambda = Math.max(
        float(words(ImageDescriptor.SamplerWord.MinLod)),
        Math.min(float(words(ImageDescriptor.SamplerWord.MaxLod)), baseLod + bias)
      )
      filter = if (gather.isDefined) ImageDescriptor.Filter.Linear
      else if (lambda > 0) words(ImageDescriptor.SamplerWord.MinFilter).toInt
      else words(ImageDescriptor.SamplerWord.MagFilter).toInt
      _ <- Either.cond(
        filter == ImageDescriptor.Filter.Nearest || filter == ImageDescriptor.Filter.Linear,
        (),
        Fault.Unsupported("sample filter is not nearest or linear")
      )
      _ <- Either.cond(
        gather.isDefined || filter == ImageDescriptor.Filter.Nearest || !imageIsInteger(format),
        (),
        Fault.InvalidTask("integer images require nearest filtering")
      )
      reference = dref.getOrElse(0.0)
      _ <-
        Either.cond(
          vulkan || dref.isDefined == (words(ImageDescriptor.SamplerWord.CompareEnable) != 0),
          (),
          Fault.InvalidTask("depth comparison and sampler disagree")
        )
      _ <- Either.cond(
        gather.isDefined || explicit || gradients.nonEmpty || levels == 1 || float(
          words(ImageDescriptor.SamplerWord.MinLod)
        ) == float(
          words(ImageDescriptor.SamplerWord.MaxLod)
        ) || float(words(ImageDescriptor.SamplerWord.MaxLod)) <= 0,
        (),
        Fault.Unsupported("implicit mip selection needs fragment derivatives")
      )
      result <- {
        def atLevel(level: Int): Memory.ReadResult[Array[Double]] = imageLevel(memory, image, level).flatMap { data =>
          val layerCount = if (cube) data.layers / 6 else data.layers
          val layer      =
            if (arrayed) Math.max(0L, Math.min(layerCount - 1, Math.rint(float(coords(dimensions))).toLong)) else 0L
          def layerPixels(index: Long): Pointer =
            data.pixels.copy(offset =
              data.pixels.offset + index * data.width * data.height * ImageTexelCodec.texelBytes(format)
            )
          val sx =
            projection.fold(float(coords(0)))(_.s) * (if (words(ImageDescriptor.SamplerWord.Unnormalized) != 0) 1.0
                                                      else data.width.toDouble) + offset(0)
          val sy = if (dimensions == 1) 0.5
          else projection.fold(float(coords(1)))(_.t) * (if (words(ImageDescriptor.SamplerWord.Unnormalized) != 0) 1.0
                                                         else data.height.toDouble) + offset(1)
          val sz = if (volume) float(coords(2)) * (if (words(ImageDescriptor.SamplerWord.Unnormalized) != 0) 1.0
                                                   else data.depth.toDouble) + offset(2)
          else 0.5
          val components =
            if (
              format == ImageDescriptor.Format.D16Unorm || format == ImageDescriptor.Format.S8Uint || format == ImageDescriptor.Format.D32SfloatS8Stencil
            ) 1
            else IntegerImageFormat(
              format
            ).fold(NormalizedImageFormat(format).fold(
              NarrowImageFormat(format).fold(imageWordComponents(format))(_.components)
            )(_.components))(_.components) match {
              case 0     => 4
              case count => count
            }
          val border = Array.tabulate(4) { lane =>
            if (lane >= components) { if (lane == 3) 1.0 else 0.0 }
            else if (
              words(
                ImageDescriptor.SamplerWord.BorderColor
              ) >= ImageDescriptor.BorderColor.FloatOpaqueWhite || (lane == 3 && words(
                ImageDescriptor.SamplerWord.BorderColor
              ) >= ImageDescriptor.BorderColor.FloatOpaqueBlack)
            ) 1.0
            else 0.0
          }
          def compare(data: Array[Double]): Array[Double] =
            if (dref.isDefined) Array(sampleCompare(
              words(ImageDescriptor.SamplerWord.CompareOp).toInt,
              if (format == ImageDescriptor.Format.D16Unorm) Math.max(0.0, Math.min(1.0, reference)) else reference,
              data(0)
            ))
            else data
          def texel(x: Long, y: Long, z: Long): Memory.ReadResult[Array[Double]] = projection match {
            case Some(p) =>
              if (data.width != data.height || data.layers < 6 || data.layers % 6 != 0)
                Left(Fault.InvalidTask("cube image requires square faces in groups of six"))
              else {
                val neighbors = CubeMap.taps(p.face, x, y, data.width, filter == ImageDescriptor.Filter.Linear)
                // Cube corners have no unique neighbor. Selecting the incident
                // face is permitted and preserves integer and Dref gather values.
                val taps = if (gather.isDefined) neighbors.take(1) else neighbors
                taps.foldLeft[Memory.ReadResult[Array[Double]]](Right(Array.fill(if (dref.isDefined) 1 else 4)(0.0))) {
                  (result, tap) =>
                    for {
                      sum   <- result
                      value <- sampleTexel(memory, layerPixels(layer * 6 + tap.face), format, data.width, tap.x, tap.y)
                    } yield { val v = compare(value); Array.tabulate(sum.length)(i => sum(i) + v(i) / taps.size) }
                }
              }
            case None =>
              val fetched =
                (
                  sampleAddress(x, data.width, words(ImageDescriptor.SamplerWord.AddressU).toInt),
                  sampleAddress(y, data.height, words(ImageDescriptor.SamplerWord.AddressV).toInt),
                  if (volume) sampleAddress(z, data.depth, words(ImageDescriptor.SamplerWord.AddressW).toInt)
                  else Some(0L)
                ) match {
                  case (Some(ix), Some(iy), Some(iz)) =>
                    sampleTexel(memory, layerPixels(layer), format, data.width, ix, iz * data.height + iy)
                  case _ => Right(border)
                }
              fetched.map(compare(_))
          }
          if (gather.isDefined) {
            val x = Math.floor(sx - 0.5).toLong; val y = Math.floor(sy - 0.5).toLong
            gatherOffsets.foldLeft[Memory.ReadResult[Array[Double]]](Right(Array.empty[Double])) { (result, delta) =>
              for {
                accumulated <- result
                value       <- texel(x + delta._1, y + delta._2, 0)
                mapped      <- if (dref.isEmpty) swizzleImage(memory, image, value) else Right(value)
              } yield accumulated :+ mapped(gather.get)
            }
          } else if (filter == ImageDescriptor.Filter.Nearest)
            texel(Math.floor(sx).toLong, Math.floor(sy).toLong, Math.floor(sz).toLong)
          else {
            val x  = Math.floor(sx - 0.5).toLong; val y = Math.floor(sy - 0.5).toLong
            val fx = sx - 0.5 - x; val fy               = sy - 0.5 - y
            def plane(z: Long): Memory.ReadResult[Array[Double]] = for {
              a <- texel(x, y, z)
              b <- texel(x + 1, y, z)
              c <- texel(x, y + 1, z)
              d <- texel(x + 1, y + 1, z)
            } yield Array.tabulate(a.length)(i =>
              (1 - fy) * ((1 - fx) * a(i) + fx * b(i)) + fy * ((1 - fx) * c(i) + fx * d(i))
            )
            if (!volume) plane(0)
            else {
              val z  = Math.floor(sz - 0.5).toLong
              val fz = sz - 0.5 - z
              for {
                a <- plane(z)
                b <- plane(z + 1)
              } yield Array.tabulate(a.length)(i => (1 - fz) * a(i) + fz * b(i))
            }
          }
        }
        val selected = Math.max(0.0, Math.min(levels - 1.0, lambda))
        if (gather.isDefined) atLevel(0)
        else if (words(ImageDescriptor.SamplerWord.MipmapMode) == ImageDescriptor.MipmapMode.Nearest)
          atLevel(Math.floor(selected + 0.5).toInt)
        else {
          val low      = Math.floor(selected).toInt
          val fraction = selected - low
          for {
            a <- atLevel(low)
            b <- atLevel(Math.min(low + 1, levels - 1))
          } yield Array.tabulate(a.length)(i => if (fraction == 0) a(i) else a(i) * (1 - fraction) + b(i) * fraction)
        }
      }
      swizzled <- if (dref.isEmpty && gather.isEmpty) swizzleImage(memory, image, result) else Right(result)
    } yield swizzled.map(value =>
      if (imageIsInteger(format) && dref.isEmpty) value.toLong & 0xffffffffL
      else IEEE754.floatToBinary32(value.toFloat).raw.toLong & 0xffffffffL
    )
  }
  private def executeDerivative(
      program: Program,
      execution: InvocationState,
      instruction: Instruction
  ): Memory.ReadResult[Unit] = {
    val (resultType, destination, operand, operation) = instruction match {
      case Instruction.DPdx(t, d, v)         => (t, d, v, 0)
      case Instruction.DPdy(t, d, v)         => (t, d, v, 1)
      case Instruction.Fwidth(t, d, v)       => (t, d, v, 2)
      case Instruction.DPdxFine(t, d, v)     => (t, d, v, 0)
      case Instruction.DPdyFine(t, d, v)     => (t, d, v, 1)
      case Instruction.FwidthFine(t, d, v)   => (t, d, v, 2)
      case Instruction.DPdxCoarse(t, d, v)   => (t, d, v, 3)
      case Instruction.DPdyCoarse(t, d, v)   => (t, d, v, 4)
      case Instruction.FwidthCoarse(t, d, v) => (t, d, v, 5)
      case _                                 => throw IllegalArgumentException("not a derivative instruction")
    }
    for {
      _ <- Either.cond(
        execution.fragmentQuad,
        (),
        Fault.InvalidTask("fragment derivatives require an aligned four-lane quad")
      )
      shape <-
        floatingShape(program, id(resultType)).toRight(Fault.InvalidTask("derivative result is not floating point"))
      _    <- Either.cond(shape._1.raw == 32, (), Fault.Unsupported("derivatives currently require 32-bit components"))
      data <- bits(execution.values, operand)
      _    <- Either.cond(data.length == shape._2, (), Fault.InvalidTask("derivative operand shape mismatch"))
    } yield execution.quadRequest = Some(QuadRequest(
      execution.currentFunction.id,
      execution.pc,
      id(resultType),
      id(destination),
      data,
      operation
    ))
  }

  private def executeSampling(
      program: Program,
      execution: InvocationState,
      memory: MemoryState,
      instruction: Instruction
  ): Memory.ReadResult[Unit] = {
    val values = execution.values
    def sample(
        resultType: Operand,
        dest: Operand,
        source: Operand,
        coordinate: Operand,
        dref: Option[Operand],
        explicit: Boolean,
        mods: Option[Operand],
        projective: Boolean = false,
        gatherComponent: Option[Operand] = None,
        gatherDepth: Boolean = false
    ): Memory.ReadResult[Unit] = for {
      image      <- address(values, source)
      dimensions <- program.layout.runtimeType(image.pointee) match {
        case Some(RuntimeType.Texture(_, dimensions, false, arrayed)) => Right((dimensions, arrayed, false))
        case Some(RuntimeType.CubeTexture(_, arrayed))                => Right((3, arrayed, true))
        case _ => Left(Fault.InvalidTask("sampling requires an image view, not a texel buffer"))
      }
      raw <- bits(values, coordinate)
      _   <- Either.cond(
        !projective || (!dimensions._2 && !dimensions._3 && raw.length > dimensions._1),
        (),
        Fault.InvalidTask("projective sampling requires non-array 1D, 2D or 3D coordinates and q")
      )
      q = if (projective) IEEE754.binary32ToFloat(Binary32.low(raw(dimensions._1))) else 1.0f
      // Implicit derivatives are taken after projection. Explicit Grad values
      // already describe the projected coordinates and pass through unchanged.
      coords = if (projective) raw.take(dimensions._1).map(w =>
        IEEE754.floatToBinary32(IEEE754.binary32ToFloat(Binary32.low(w)) / q).raw.toLong & 0xffffffffL
      )
      else raw
      reference <- dref match {
        case Some(value) =>
          scalar(values, value).map(w => Some((IEEE754.binary32ToFloat(Binary32.low(w)) / q).toDouble))
        case None => Right(None)
      }
      gather <- if (gatherDepth) Right(Some(0))
      else gatherComponent match {
        case None            => Right(None)
        case Some(component) => scalar(values, component).flatMap(c =>
            Either.cond(c >= 0 && c <= 3, Some(c.toInt), Fault.InvalidTask("gather component must be in [0,3]"))
          )
      }
      _ <- Either.cond(
        gather.isEmpty || dimensions._1 == 2 || dimensions._3,
        (),
        Fault.InvalidTask("gather requires a 2D or cube image")
      )
      _ <- if (gather.isEmpty && !explicit && execution.fragmentQuad && execution.gradients.isEmpty) {
        execution.quadRequest = Some(QuadRequest(
          execution.currentFunction.id,
          execution.pc,
          id(resultType),
          id(dest),
          coords,
          6
        ))
        Right(())
      } else sampleImage(
        memory,
        values,
        image,
        coords,
        reference,
        explicit,
        mods,
        execution.gradients,
        dimensions._1,
        dimensions._2,
        dimensions._3,
        gather,
        program.vulkanEntries.nonEmpty
      ).map { result =>
        execution.gradients = None
        values.update(id(dest), RuntimeValue.Bits(id(resultType), result))
      }
    } yield ()
    val result = instruction match {
      case Instruction.SampledImage(resultType, dest, source, sampler) =>
        for {
          image <- address(values, source)
          state <- address(values, sampler)
        } yield values.update(id(dest), RuntimeValue.Address(id(resultType), image.copy(sampler = Some(state))))
      case Instruction.Image(resultType, dest, source) => address(values, source).map(image =>
          values.update(id(dest), RuntimeValue.Address(id(resultType), image.copy(sampler = None)))
        )
      case Instruction.ImageSampleImplicitLod(t, d, i, c, m)        => sample(t, d, i, c, None, false, m)
      case Instruction.ImageSampleExplicitLod(t, d, i, c, m)        => sample(t, d, i, c, None, true, Some(m))
      case Instruction.ImageSampleDrefImplicitLod(t, d, i, c, r, m) => sample(t, d, i, c, Some(r), false, m)
      case Instruction.ImageSampleDrefExplicitLod(t, d, i, c, r, m) => sample(t, d, i, c, Some(r), true, Some(m))
      case Instruction.ImageSampleProjImplicitLod(t, d, i, c, m)    =>
        sample(t, d, i, c, None, false, m, projective = true)
      case Instruction.ImageSampleProjExplicitLod(t, d, i, c, m) =>
        sample(t, d, i, c, None, true, Some(m), projective = true)
      case Instruction.ImageSampleProjDrefImplicitLod(t, d, i, c, r, m) =>
        sample(t, d, i, c, Some(r), false, m, projective = true)
      case Instruction.ImageSampleProjDrefExplicitLod(t, d, i, c, r, m) =>
        sample(t, d, i, c, Some(r), true, Some(m), projective = true)
      case Instruction.ImageGather(t, d, i, c, component, m) =>
        sample(t, d, i, c, None, false, m, gatherComponent = Some(component))
      case Instruction.ImageDrefGather(t, d, i, c, r, m) => sample(t, d, i, c, Some(r), false, m, gatherDepth = true)
      case Instruction.ImageQueryLod(t, d, i, c)         =>
        for {
          coords     <- bits(values, c)
          image      <- address(values, i)
          dimensions <- program.layout.runtimeType(image.pointee) match {
            case Some(RuntimeType.Texture(_, n, false, _)) => Right((n, false))
            case Some(RuntimeType.CubeTexture(_, _))       => Right((3, true))
            case _ => Left(Fault.InvalidTask("LOD query requires a sampled 1D, 2D, 3D or cube image"))
          }
          _ <- Either.cond(
            coords.length >= dimensions._1,
            (),
            Fault.InvalidTask("LOD query coordinate shape does not match image")
          )
          _ <- if (execution.fragmentQuad && execution.gradients.isEmpty) {
            execution.quadRequest =
              Some(QuadRequest(execution.currentFunction.id, execution.pc, id(t), id(d), coords, 6))
            Right(())
          } else for {
            sampler     <- image.sampler.toRight(Fault.InvalidTask("LOD query has no sampler"))
            level       <- imageLevel(memory, image, 0)
            levels      <- imageLevels(memory, image)
            biasBits    <- memory.readScalar(sampler.copy(offset = sampler.offset + 32), 4)
            minBits     <- memory.readScalar(sampler.copy(offset = sampler.offset + 36), 4)
            maxBits     <- memory.readScalar(sampler.copy(offset = sampler.offset + 40), 4)
            mipmap      <- memory.readScalar(sampler.copy(offset = sampler.offset + 44), 4)
            resultShape <- floatingShape(program, id(t)).filter((w, n) => w.raw == 32 && n == 2)
              .toRight(Fault.InvalidTask("LOD query result must be float32 vec2"))
          } yield {
            def float(bits: Long): Double = IEEE754.binary32ToFloat(Binary32.low(bits)).toDouble
            val direction                 = coords.take(dimensions._1).map(float(_))
            val projection                =
              if (dimensions._2) Some(CubeMap.project(direction(0), direction(1), direction(2))) else None
            val base = execution.gradients.fold(Double.NegativeInfinity) { case (dx, dy) =>
              imageGradientLod(level, direction, dx, dy, dimensions._1, projection, false)
            }
            val lod     = base + Math.max(-16.0, Math.min(16.0, float(biasBits)))
            val clamped = Math.max(0.0, Math.min(levels - 1.0, Math.max(float(minBits), Math.min(float(maxBits), lod))))
            val selected = if (mipmap == ImageDescriptor.MipmapMode.Nearest)
              Math.max(0.0, Math.ceil(clamped + 0.5) - 1.0)
            else clamped
            values.update(
              id(d),
              RuntimeValue.Bits(id(t), Array(selected, lod).map(v => doubleToFloating(resultShape._1, v)))
            )
            execution.gradients = None
          }
        } yield ()
      case Instruction.ImageQueryLevels(t, d, i) =>
        for {
          image  <- address(values, i)
          levels <- imageLevels(memory, image)
        } yield values.update(id(d), RuntimeValue.Bits(id(t), Array(levels.toLong)))
      case Instruction.ImageQuerySizeLod(t, d, i, lod) =>
        for {
          level      <- scalar(values, lod)
          image      <- address(values, i)
          dimensions <- program.layout.runtimeType(image.pointee) match {
            case Some(RuntimeType.Texture(_, dimensions, false, arrayed)) => Right((dimensions, arrayed, false))
            case Some(RuntimeType.CubeTexture(_, arrayed))                => Right((2, arrayed, true))
            case _ => Left(Fault.InvalidTask("image mip size query requires a sampled image"))
          }
          levels <- imageLevels(memory, image)
          bounded = if (program.vulkanEntries.nonEmpty) Math.max(0, Math.min(levels - 1, level.toInt)) else level.toInt
          data <- imageLevel(memory, image, bounded)
        } yield values.update(
          id(d),
          RuntimeValue.Bits(
            id(t),
            (if (dimensions._1 == 1) Array(data.width)
             else if (dimensions._1 == 3) Array(data.width, data.height, data.depth)
             else Array(data.width, data.height)) ++ (if (dimensions._2)
                                                        Array(if (dimensions._3) data.layers / 6 else data.layers)
                                                      else Array.empty[Long])
          )
        )
      case Instruction.ImageQuerySamples(t, d, i) =>
        for {
          image <- address(values, i)
          data  <- imageLevel(memory, image, 0)
        } yield values.update(id(d), RuntimeValue.Bits(id(t), Array(data.samples.toLong)))
      case Instruction.ImageFetch(t, d, i, c, mods) =>
        for {
          image     <- address(values, i)
          imageType <- program.layout.runtimeType(image.pointee) match {
            case Some(RuntimeType.Texture(_, dimensions, buffer, arrayed)) =>
              Right((dimensions, buffer, false, arrayed))
            case Some(RuntimeType.MultisampledTexture(_, arrayed)) => Right((2, false, true, arrayed))
            case _ => Left(Fault.InvalidTask("texel fetch requires a sampled image"))
          }
          operands      = mods.map(_.words.toVector.map(_.raw).toArray).getOrElse(Array(0))
          mask          = operands.headOption.getOrElse(-1)
          selectionMask = if (imageType._3) ImageOperands.Sample else ImageOperands.Lod
          hasSelection  = (mask & selectionMask) != 0
          hasOffset     = (mask & ImageOperands.ConstOffset) != 0
          _ <- Either.cond(
            (mask & ~(selectionMask | (ImageOperands.SignExtend | ImageOperands.ZeroExtend) | (if (
                                                                                                 imageType._2 || imageType._3
                                                                                               ) 0
                                                                                               else
                                                                                                 ImageOperands.ConstOffset))) == 0 &&
              (mask & (ImageOperands.SignExtend | ImageOperands.ZeroExtend)) != (ImageOperands.SignExtend | ImageOperands.ZeroExtend) &&
              (!imageType._3 || hasSelection) && (!imageType._2 || (mask & ~(ImageOperands.SignExtend | ImageOperands.ZeroExtend)) == 0),
            (),
            Fault.Unsupported("texel fetch requires matching Lod or Sample with optional ConstOffset")
          )
          _ <- Either.cond(
            operands.length == 1 + (if (hasSelection) 1 else 0) + (if (hasOffset) 1 else 0),
            (),
            Fault.InvalidTask("invalid texel fetch operand count")
          )
          operand <- if (!hasSelection) Right(0)
          else values.get(operands(1)) match {
            case Some(RuntimeValue.Bits(_, lanes)) if lanes.length == 1 => Right(lanes(0).toInt)
            case _ => Left(Fault.InvalidTask("texel fetch LOD or sample is not scalar"))
          }
          offset <- if (!hasOffset) Right(imageZeroOffset)
          else imageConstOffset(values, operands(if (hasSelection) 2 else 1), imageType._1)
          data <- imageLevel(memory, image, if (imageType._3) 0 else operand, texelBuffer = imageType._2)
          _    <- Either.cond(
            imageType._3 || data.samples == 1,
            (),
            Fault.InvalidTask("multisample image needs a multisampled texture type")
          )
          coords <- bits(values, c)
          _      <- Either.cond(
            coords.length == imageType._1 + (if (imageType._4) 1 else 0),
            (),
            Fault.InvalidTask("texel fetch coordinate dimensionality does not match")
          )
          x      = coords(0).toInt.toLong + offset(0)
          y      = if (imageType._1 == 1) 0L else coords(1).toInt.toLong + offset(1)
          z      = if (imageType._1 == 3) coords(2).toInt.toLong + offset(2) else 0L
          sample = if (imageType._3) operand else 0
          layer  = if (imageType._4) coords(imageType._1).toInt.toLong else 0L
          texel <-
            if (
              x < 0 || y < 0 || x >= data.width || y >= data.height || z < 0 || z >= data.depth || sample < 0 || sample >= data.samples || layer < 0 || layer >= data.layers
            )
              Right(Array.fill(4)(0.0))
            else sampleTexel(
              memory,
              data.pixels,
              data.format,
              data.width * data.samples,
              x * data.samples + sample,
              (layer * data.depth + z) * data.height + y
            )
          swizzled <- swizzleImage(memory, image, texel)
        } yield values.update(
          id(d),
          RuntimeValue.Bits(
            id(t),
            swizzled.map(v =>
              if (imageIsInteger(data.format)) v.toLong & 0xffffffffL
              else IEEE754.floatToBinary32(v.toFloat).raw.toLong & 0xffffffffL
            )
          )
        )
      case _ => Left(Fault.Unsupported("sampled image instruction is not implemented"))
    }
    result.map(_ => if (execution.quadRequest.isEmpty) execution.pc += 1)
  }

  private def readInputAttachment(
      program: Program,
      memory: MemoryState,
      values: InvocationValues,
      image: Operand,
      coordinate: Operand,
      mods: Option[Operand]
  ): Memory.ReadResult[Array[Long]] = for {
    source    <- address(values, image)
    imageType <- program.layout.runtimeType(source.pointee) match {
      case Some(RuntimeType.InputAttachment(component, multisampled)) => Right(component -> multisampled)
      case _ => Left(Fault.InvalidTask("subpass read requires an input attachment"))
    }
    data <- imageLevel(memory, source, 0)
    _    <-
      Either.cond(imageType._2 == (data.samples > 1), (), Fault.InvalidTask("input attachment sample count mismatch"))
    coord <- bits(values, coordinate)
    _ <- Either.cond(coord.length == 2, (), Fault.InvalidTask("input attachment coordinates require two components"))
    position <- program.builtins.view.collectFirst {
      case (variable, 15) if values.get(variable).exists { case RuntimeValue.Address(_, _) => true; case _ => false } =>
        variable
    }.flatMap(v => values.get(v)) match {
      case Some(RuntimeValue.Address(_, pointer)) => Right(pointer)
      case _ => Left(Fault.InvalidTask("input attachment requires a fragment position record"))
    }
    px     <- memory.readScalar(position, 4)
    py     <- memory.readScalar(position.copy(offset = position.offset + 4), 4)
    sample <- mods match {
      case Some(m)
          if imageType._2 && m.words.length == 2 && (m.words.head.raw & ~(ImageOperands.SignExtend | ImageOperands.ZeroExtend)) == ImageOperands.Sample &&
            (m.words.head.raw & (ImageOperands.SignExtend | ImageOperands.ZeroExtend)) != (ImageOperands.SignExtend | ImageOperands.ZeroExtend) =>
        values.get(m.words.apply(1).raw) match {
          case Some(RuntimeValue.Bits(_, lanes)) if lanes.length == 1 => Right(lanes(0).toInt)
          case _ => Left(Fault.InvalidTask("input attachment sample index is not scalar"))
        }
      case None if !imageType._2 => Right(0)
      case Some(m)
          if !imageType._2 && m.words.length == 1 &&
            (m.words.head.raw == 0 || m.words.head.raw == ImageOperands.SignExtend || m.words.head.raw == ImageOperands.ZeroExtend) =>
        Right(0)
      case _ => Left(Fault.Unsupported("input attachment operands require a sample index only for multisampled images"))
    }
    x = Math.floor(IEEE754.binary32ToFloat(Binary32.low(px)).toDouble).toLong + coord(0).toInt
    y = Math.floor(IEEE754.binary32ToFloat(Binary32.low(py)).toDouble).toLong + coord(1).toInt
    texel <- if (x < 0 || y < 0 || x >= data.width || y >= data.height || sample < 0 || sample >= data.samples)
      Right(Array.fill(4)(0.0))
    else sampleTexel(memory, data.pixels, data.format, data.width * data.samples, x * data.samples + sample, y)
  } yield
    if (imageIsInteger(data.format)) texel.map(_.toLong & 0xffffffffL)
    else texel.map(x => IEEE754.floatToBinary32(x.toFloat).raw.toLong & 0xffffffffL)

  private def executeImage(
      program: Program,
      execution: InvocationState,
      memory: MemoryState,
      instruction: Instruction
  ): Memory.ReadResult[Unit] = {
    val values = execution.values
    val result = instruction match {
      case Instruction.ImageRead(resultType, dest, image, coordinate, mods)
          if address(values, image).toOption.exists(p =>
            program.layout.runtimeType(p.pointee).exists {
              case RuntimeType.InputAttachment(_, _) => true; case _ => false
            }
          ) =>
        readInputAttachment(program, memory, values, image, coordinate, mods)
          .map(lanes => values.update(id(dest), RuntimeValue.Bits(id(resultType), lanes)))

      case Instruction.ImageRead(resultType, dest, image, coordinate, mods) =>
        for {
          _ <- Either.cond(
            mods.forall(m =>
              m.words.length == 1 && (unsigned(m) == 0L || unsigned(m) == ImageOperands.SignExtend || unsigned(
                m
              ) == ImageOperands.ZeroExtend)
            ),
            (),
            Fault.Unsupported("unsupported storage image read operands")
          )
          texel <- imageTexel(program, memory, values, image, coordinate)
          lanes <- {
            val (pointer, format) = texel
            val valid             = pointer.bounds.exists { case (_, end) => pointer.offset < end }
            val components        = imageWordComponents(format)
            if (IntegerImageFormat(format).isDefined)
              readIntegerTexel(memory, pointer, IntegerImageFormat(format).get, valid)
            else if (NarrowImageFormat(format).isDefined)
              readNarrowTexel(memory, pointer, NarrowImageFormat(format).get, valid).map(_.map(value =>
                IEEE754.floatToBinary32(value.toFloat).raw.toLong & 0xffffffffL
              ))
            else if (NormalizedImageFormat(format).isDefined)
              readNormalizedTexel(memory, pointer, NormalizedImageFormat(format).get, valid).map(_.map(value =>
                IEEE754.floatToBinary32(value.toFloat).raw.toLong & 0xffffffffL
              ))
            else if (components != 0) {
              val initial = Array(0L, 0L, 0L, if (valid) if (imageIsInteger(format)) 1L else 0x3f800000L else 0L)
              (0 until components).foldLeft[Memory.ReadResult[Array[Long]]](Right(initial)) { (result, lane) =>
                for {
                  data <- result
                  word <- memory.readScalar(pointer.copy(offset = pointer.offset + lane * 4), 4)
                } yield { data(lane) = word; data }
              }
            } else memory.readScalar(pointer, 4).map { value =>
              if (format == ImageFormat.Rgba8) Array.tabulate(4)(lane =>
                IEEE754.floatToBinary32(((value >>> (lane * 8)) & 255L).toFloat / 255.0f).raw.toLong
              )
              else if (format == ImageFormat.Rgba8ui) Array.tabulate(4)(lane => (value >>> (lane * 8)) & 255L)
              else Array(value, 0L, 0L, if (valid) 1L else 0L)
            }
          }
        } yield values.update(id(dest), RuntimeValue.Bits(id(resultType), lanes))
      case Instruction.ImageWrite(image, coordinate, texel, mods) =>
        for {
          _ <- Either.cond(
            mods.forall(m =>
              m.words.length == 1 && (unsigned(m) == 0L || unsigned(m) == ImageOperands.SignExtend || unsigned(
                m
              ) == ImageOperands.ZeroExtend)
            ),
            (),
            Fault.Unsupported("unsupported storage image write operands")
          )
          target <- imageTexel(program, memory, values, image, coordinate)
          lanes  <- bits(values, texel)
          _      <- Either.cond(lanes.length == 4, (), Fault.InvalidTask("image texel requires four components"))
          _      <- if (IntegerImageFormat(target._2).isDefined) {
            val layout = IntegerImageFormat(target._2).get
            if (layout.packed) memory.writeScalar(target._1, layout.encodePacked(lanes), 4)
            else (0 until layout.components).foldLeft[Memory.ReadResult[Unit]](Right(())) { (result, lane) =>
              result.flatMap(_ =>
                memory.writeScalar(
                  target._1.copy(offset = target._1.offset + lane * layout.componentBytes.raw),
                  layout.encode(lanes(lane)),
                  layout.componentBytes
                )
              )
            }
          } else if (NarrowImageFormat(target._2).isDefined) {
            val layout = NarrowImageFormat(target._2).get
            val floats = lanes.map(v => IEEE754.binary32ToFloat(Binary32.low(v)).toDouble)
            if (layout.encoding != ImageFormats.NarrowEncoding.Half)
              memory.writeScalar(
                target._1,
                layout.encodePacked(floats),
                layout.texelBytes
              )
            else (0 until layout.components).foldLeft[Memory.ReadResult[Unit]](Right(())) { (result, lane) =>
              result.flatMap(_ =>
                memory.writeScalar(
                  target._1.copy(offset = target._1.offset + layout.offset(lane), pointee = target._1.pointee),
                  layout.encodeLane(floats(lane), lane),
                  2
                )
              )
            }
          } else if (NormalizedImageFormat(target._2).isDefined) {
            val layout = NormalizedImageFormat(target._2).get
            (0 until layout.components).foldLeft[Memory.ReadResult[Unit]](Right(())) { (result, lane) =>
              result.flatMap(_ =>
                memory.writeScalar(
                  target._1.copy(offset = target._1.offset + layout.channel(lane)),
                  layout.encode(IEEE754.binary32ToFloat(Binary32.low(lanes(lane))).toDouble, lane),
                  1
                )
              )
            }
          } else if (imageWordComponents(target._2) != 0)
            (0 until imageWordComponents(target._2)).foldLeft[Memory.ReadResult[Unit]](Right(())) { (result, lane) =>
              result.flatMap(_ =>
                memory.writeScalar(
                  target._1.copy(offset = target._1.offset + lane * 4),
                  lanes(lane),
                  4
                )
              )
            }
          else memory.writeScalar(target._1, lanes(0), 4)
        } yield ()
      case Instruction.ImageTexelPointer(resultType, dest, image, coordinate, sample) =>
        for {
          sampleIndex <- scalar(values, sample)
          _           <- Either.cond(sampleIndex == 0, (), Fault.InvalidTask("storage image has only sample zero"))
          target      <- imageTexel(program, memory, values, image, coordinate)
          _           <- Either.cond(
            target._2 == 33 || target._2 == 24,
            (),
            Fault.Unsupported("image atomics require R32ui or R32i")
          )
        } yield values.update(id(dest), RuntimeValue.Address(id(resultType), target._1))
      case Instruction.ImageQuerySize(resultType, dest, image) =>
        for {
          pointer   <- address(values, image)
          imageType <- program.layout.runtimeType(pointer.pointee) match {
            case Some(RuntimeType.StorageImage(_, format, dimensions, buffer, arrayed, cube)) =>
              Right((format, dimensions, buffer, arrayed, cube))
            case Some(RuntimeType.Texture(_, 1, true, false))      => Right((-1, 1, true, false, false))
            case Some(RuntimeType.MultisampledTexture(_, arrayed)) => Right((-1, 2, false, arrayed, false))
            case _ => Left(Fault.InvalidTask("image size query requires a storage image or texel buffer"))
          }
          shape <-
            imageLevel(memory, pointer, 0, if (program.vulkanEntries.nonEmpty) -1 else imageType._1, imageType._3)
        } yield values.update(
          id(dest),
          RuntimeValue.Bits(
            id(resultType),
            (if (imageType._2 == 1) Array(shape.width)
             else if (imageType._2 == 3) Array(shape.width, shape.height, shape.depth)
             else Array(shape.width, shape.height)) ++ (if (imageType._4)
                                                          Array(if (imageType._5) shape.layers / 6 else shape.layers)
                                                        else Array.empty[Long])
          )
        )
      case _ => Left(Fault.Unsupported(s"${instruction.opcode} image execution is not implemented"))
    }
    result.map(_ => execution.pc += 1)
  }

  // Composite operations scan variable operands and retain the first validation failure.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return", "scalafix:DisableSyntax.var"))
  private def executeComposite(
      program: Program,
      execution: InvocationState,
      memory: MemoryState,
      instruction: Instruction
  ): Either[Fault, Unit] = {
    val values = execution.values
    instruction match {
      case Instruction.Bitcast(resultType, dest, source) =>
        values.get(id(source)) match {
          case Some(RuntimeValue.Address(_, pointer)) =>
            val pointee = program.layout.pointerPointee(id(resultType))
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
                val pointee = program.layout.pointerPointee(id(resultType))
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
        (program.layout.runtimeType(id(resultType)), values.get(id(source))) match {
          case (Some(RuntimeType.Pointer(8, pointee)), Some(RuntimeValue.Address(sourceType, pointer))) =>
            program.layout.runtimeType(sourceType) match {
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
      case Instruction.ArrayLength(resultType, dest, structure, arrayMember) =>
        val result = values.get(id(structure)) match {
          case Some(RuntimeValue.Address(_, pointer)) => program.layout.runtimeType(pointer.pointee) match {
              case Some(RuntimeType.Structure(members)) =>
                val member = unsigned(arrayMember).toInt
                if (member < 0 || member >= members.size)
                  Left(Fault.InvalidTask("OpArrayLength member is outside its structure"))
                else program.layout.runtimeType(members(member)) match {
                  case Some(RuntimeType.Array(element, 0)) =>
                    val stride = program.arrayStrides.getOrElse(members(member), program.layout.typeSize(element))
                    pointer.bounds.toRight(Fault.InvalidTask("OpArrayLength requires a bounded descriptor")).flatMap {
                      case (_, end) =>
                        val offset = Math.addExact(
                          pointer.offset,
                          program.layout.structMemberOffset(pointer.pointee, members, member).toLong
                        )
                        if (stride <= 0 || offset > end)
                          Left(Fault.InvalidTask("OpArrayLength array is outside its descriptor"))
                        else Right((end - offset) / stride)
                    }
                  case _ => Left(Fault.InvalidTask("OpArrayLength requires a runtime array member"))
                }
              case _ => Left(Fault.InvalidTask("OpArrayLength requires a structure pointer"))
            }
          case _ => Left(Fault.InvalidTask("OpArrayLength requires a pointer"))
        }
        result match {
          case Left(error)   => return Left(error)
          case Right(length) => values.updateScalar(id(dest), id(resultType), length); execution.pc += 1
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
        program.layout.scalarLeafCount(id(resultType)) match {
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
      execution: InvocationState,
      memory: MemoryState,
      instruction: Instruction
  ): Memory.ReadResult[Unit] = {
    val values = execution.values
    instruction match {
      case Instruction.VectorShuffle(resultType, dest, vector1, vector2, components) =>
        (values.get(id(vector1)), values.get(id(vector2))) match {
          case (Some(RuntimeValue.Bits(_, left)), Some(RuntimeValue.Bits(_, right))) =>
            program.layout.scalarLeafCount(id(resultType)) match {
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
      execution: InvocationState,
      memory: MemoryState,
      instruction: Instruction
  ): Either[Fault, Unit] = {
    val values = execution.values
    instruction match {
      case Instruction.UConvert(resultType, dest, source) =>
        val sourceId = id(source)
        if (values.scalarType(sourceId) >= 0) {
          val width = program.layout.scalarWidth(id(resultType))
          values.updateScalar(id(dest), id(resultType), values.scalarBits(sourceId) & mask(width))
          execution.pc += 1
        } else
          values.get(sourceId) match {
            case Some(RuntimeValue.Bits(_, lanes)) =>
              val width = program.layout.scalarWidth(id(resultType))
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
        (program.layout.runtimeType(id(resultType)), values.get(id(integerValue))) match {
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
            memory.integerPointer(
              rawAddress & mask(program.layout.scalarWidth(integerType)),
              pointee,
              storageClass
            ) match {
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
      case Instruction.QuantizeToF16(resultType, dest, source) =>
        values.get(id(source)) match {
          case Some(RuntimeValue.Bits(t, lanes))
              if t == id(resultType) &&
                floatingShape(program, t).exists { case (width, count) =>
                  width.raw == 32 && count == lanes.length
                } =>
            values.update(id(dest), RuntimeValue.Bits(t, lanes.map(quantizeToF16)))
            execution.pc += 1
          case Some(_) =>
            return Left(Fault.InvalidTask("OpQuantizeToF16 requires matching float32 scalar or vector types"))
          case None => return Left(undefined(source))
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
      case Instruction.CopyLogical(resultType, dest, source) =>
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
  private def executeBitField(
      program: Program,
      execution: InvocationState,
      instruction: Instruction
  ): Either[Fault, Unit] = {
    val values                                         = execution.values
    val (t, d, base, insert, offset, count, operation) = instruction match {
      case Instruction.BitFieldUExtract(t, d, b, o, c)  => (t, d, b, None, Some(o), Some(c), 0)
      case Instruction.BitFieldSExtract(t, d, b, o, c)  => (t, d, b, None, Some(o), Some(c), 1)
      case Instruction.BitFieldInsert(t, d, b, i, o, c) => (t, d, b, Some(i), Some(o), Some(c), 2)
      case Instruction.BitReverse(t, d, b)              => (t, d, b, None, None, None, 3)
      case _                                            => return Left(Fault.Unsupported("unknown bit-field operation"))
    }
    for {
      shape  <- integerShape(program, id(t)).toRight(Fault.InvalidTask("bit-field result requires integer components"))
      source <- values.get(id(base)) match {
        case Some(RuntimeValue.Bits(bt, lanes))
            if integerShape(program, bt).contains(shape) && lanes.length == shape._2 => Right(lanes)
        case _ => Left(Fault.InvalidTask("bit-field base shape does not match result"))
      }
      added <- insert match {
        case Some(i) => values.get(id(i)) match {
            case Some(RuntimeValue.Bits(it, lanes))
                if integerShape(program, it).contains(shape) && lanes.length == shape._2 => Right(lanes)
            case _ => Left(Fault.InvalidTask("bit-field inserted value shape does not match result"))
          }
        case None => Right(Array.empty[Long])
      }
      off <- offset.fold[Either[Fault, Long]](Right(0L))(scalar(values, _))
      len <- count.fold[Either[Fault, Long]](Right(0L))(scalar(values, _))
      validRange = off >= 0 && len >= 0 && off <= shape._1.raw && len <= shape._1.raw - off
      _ <- Either.cond(
        validRange || program.vulkanEntries.nonEmpty,
        (),
        Fault.InvalidTask("bit-field range exceeds component width")
      )
    } yield {
      val fieldMask = if (!validRange) 0L else if (len == 64) -1L else (1L << len.toInt) - 1L
      val result    = source.indices.map { lane =>
        val word = source(lane) & mask(shape._1)
        // Invalid shader ranges have undefined results. Keep the operation local to its
        // result instead of losing the device, including when that result is discarded.
        val value = if (!validRange) { if (operation == 2) word else 0L }
        else if (operation == 3) java.lang.Long.reverse(word) >>> (64 - shape._1.raw)
        else if (len == 0) { if (operation == 2) word else 0L }
        else if (operation == 2) (word & ~(fieldMask << off.toInt)) | ((added(lane) & fieldMask) << off.toInt)
        else {
          val extracted = (word >>> off.toInt) & fieldMask
          if (operation == 1) (extracted << (64 - len.toInt)) >> (64 - len.toInt) else extracted
        }
        value & mask(shape._1)
      }.toArray
      values.update(id(d), RuntimeValue.Bits(id(t), result))
      execution.pc += 1
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.return"))
  private def executeInteger(
      program: Program,
      execution: InvocationState,
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
      case Instruction.SMod(resultType, dest, left, right) =>
        signedIntegerArithmetic(
          program,
          values,
          resultType,
          dest,
          left,
          right,
          SignedIntegerOperation.Modulo
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
      execution: InvocationState,
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
      case Instruction.FRem(resultType, dest, left, right) =>
        floatingBinary(program, values, resultType, dest, left, right, FloatingOperation.Remainder) match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.FMod(resultType, dest, left, right) =>
        floatingBinary(program, values, resultType, dest, left, right, FloatingOperation.Modulo) match {
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
      execution: InvocationState,
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
            if (java.lang.Long.compareUnsigned(amount, width.raw.toLong) >= 0) 0L
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
      execution: InvocationState,
      instruction: Instruction
  ): Either[Fault, Unit] = {
    val values = execution.values
    instruction match {
      case Instruction.ShiftRightLogical(resultType, dest, base, shift) =>
        integerShift(program, values, resultType, dest, base, shift, "OpShiftRightLogical") {
          (value, amount, width) =>
            if (java.lang.Long.compareUnsigned(amount, width.raw.toLong) >= 0) 0L
            else (value & mask(width)) >>> amount.toInt
        } match {
          case Left(error) => return Left(error)
          case Right(())   => execution.pc += 1
        }
      case Instruction.ShiftRightArithmetic(resultType, dest, base, shift) =>
        integerShift(program, values, resultType, dest, base, shift, "OpShiftRightArithmetic") {
          (value, amount, width) =>
            val signed = signExtend(value, width)
            if (java.lang.Long.compareUnsigned(amount, width.raw.toLong) >= 0) {
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
      execution: InvocationState,
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
            (booleanLanes(program, conditionType), program.layout.scalarLeafCount(id(resultType))) match {
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

  private def executeNonUniform(
      program: Program,
      execution: InvocationState,
      instruction: Instruction
  ): Either[Fault, Unit] = {
    val values   = execution.values
    val function = execution.currentFunction.id
    val pc       = execution.pc
    def valueRequest(
        kind: CollectiveKind,
        t: Operand,
        d: Operand,
        scope: Operand,
        source: Operand,
        operation: Int = 0,
        predicateResult: Boolean = false
    ): Either[Fault, CollectiveRequest] =
      (scalar(values, scope), values.get(id(source))) match {
        case (Right(3L), Some(value @ RuntimeValue.Bits(sourceType, _)))
            if (predicateResult && booleanLanes(program, id(t)).contains(1)) || sourceType == id(t) =>
          Right(CollectiveRequest(
            kind,
            function,
            pc,
            3,
            CollectivePayload.Value(id(t), id(d), value),
            operation,
            nonUniform = true
          ))
        case (Left(error), _) => Left(error)
        case (_, None)        => Left(undefined(source))
        case _                =>
          Left(Fault.InvalidTask("subgroup operation requires Subgroup scope and compatible value/result types"))
      }
    def arithmetic(
        kind: CollectiveKind,
        t: Operand,
        d: Operand,
        scope: Operand,
        operation: Operand,
        source: Operand,
        cluster: Option[Operand]
    ): Either[Fault, CollectiveRequest] = {
      val logical =
        kind == CollectiveKind.LogicalAnd || kind == CollectiveKind.LogicalOr || kind == CollectiveKind.LogicalXor
      if (cluster.nonEmpty || unsigned(operation) > 2L)
        Left(Fault.Unsupported("clustered subgroup reductions are not implemented"))
      else if (if (logical) booleanLanes(program, id(t)).isEmpty else integerShape(program, id(t)).isEmpty)
        Left(Fault.InvalidTask("subgroup arithmetic operand type is incompatible with its operation"))
      else valueRequest(kind, t, d, scope, source, id(operation))
    }
    def shuffle(
        t: Operand,
        d: Operand,
        scope: Operand,
        source: Operand,
        lane: Operand,
        mode: Int
    ): Either[Fault, CollectiveRequest] =
      for {
        request <- valueRequest(CollectiveKind.Shuffle, t, d, scope, source)
        index   <- scalar(values, lane)
        _       <- Either.cond(
          integerShape(
            program,
            values.get(id(lane)).collect { case RuntimeValue.Bits(t, _) => t }.getOrElse(-1)
          ).contains(32 -> 1),
          (),
          Fault.InvalidTask("subgroup shuffle lane must be a 32-bit scalar integer")
        )
      } yield {
        val local    = execution.invocation.subgroupLocal.toLong
        val selected = mode match {
          case 1 => local ^ index
          case 2 => local - index
          case 3 => local + index
          case _ => index
        }
        val data = values.get(id(source)).get
        request.copy(payload =
          CollectivePayload.Shuffle(
            id(t),
            id(d),
            data,
            RuntimeValue.Bits(
              values.get(id(lane)).collect { case RuntimeValue.Bits(t, _) => t }.getOrElse(-1),
              Array(selected)
            )
          )
        )
      }
    instruction match {
      case _: Instruction.GroupNonUniformFAdd | _: Instruction.GroupNonUniformFMul |
          _: Instruction.GroupNonUniformFMin | _: Instruction.GroupNonUniformFMax =>
        executeNonUniformFloating(program, execution, instruction)
      case _: Instruction.GroupNonUniformInverseBallot | _: Instruction.GroupNonUniformBallotBitExtract |
          _: Instruction.GroupNonUniformBallotBitCount | _: Instruction.GroupNonUniformBallotFindLSB |
          _: Instruction.GroupNonUniformBallotFindMSB => executeNonUniformBallot(program, execution, instruction)
      case _ =>
        val request = instruction match {
          case Instruction.GroupNonUniformElect(t, d, scope) =>
            scalar(values, scope).flatMap { value =>
              if (value != 3L || !booleanLanes(program, id(t)).contains(1))
                Left(Fault.InvalidTask("subgroup election requires Subgroup scope and boolean result"))
              else Right(CollectiveRequest(
                CollectiveKind.Elect,
                function,
                pc,
                3,
                CollectivePayload.Value(id(t), id(d), RuntimeValue.Bits(id(t), Array(1L))),
                nonUniform = true
              ))
            }
          case Instruction.GroupNonUniformAll(t, d, scope, value) =>
            collectiveBoolean(
              program,
              values,
              function,
              pc,
              t,
              d,
              scope,
              value,
              CollectiveKind.All,
              "OpGroupNonUniformAll"
            )
          case Instruction.GroupNonUniformAny(t, d, scope, value) =>
            collectiveBoolean(
              program,
              values,
              function,
              pc,
              t,
              d,
              scope,
              value,
              CollectiveKind.Any,
              "OpGroupNonUniformAny"
            )
          case Instruction.GroupNonUniformAllEqual(t, d, scope, value) =>
            valueRequest(CollectiveKind.AllEqual, t, d, scope, value, predicateResult = true)
          case Instruction.GroupNonUniformBroadcastFirst(t, d, scope, value) =>
            valueRequest(CollectiveKind.BroadcastFirst, t, d, scope, value)
          case Instruction.GroupNonUniformBroadcast(t, d, scope, value, lane)   => shuffle(t, d, scope, value, lane, 0)
          case Instruction.GroupNonUniformShuffle(t, d, scope, value, lane)     => shuffle(t, d, scope, value, lane, 0)
          case Instruction.GroupNonUniformShuffleXor(t, d, scope, value, lane)  => shuffle(t, d, scope, value, lane, 1)
          case Instruction.GroupNonUniformShuffleUp(t, d, scope, value, lane)   => shuffle(t, d, scope, value, lane, 2)
          case Instruction.GroupNonUniformShuffleDown(t, d, scope, value, lane) => shuffle(t, d, scope, value, lane, 3)
          case Instruction.GroupNonUniformBallot(t, d, scope, value)            =>
            collectiveBallot(program, values, function, pc, t, d, scope, value)
          case Instruction.GroupNonUniformIAdd(t, d, scope, operation, value, cluster) =>
            arithmetic(CollectiveKind.IntegerAdd, t, d, scope, operation, value, cluster)
          case Instruction.GroupNonUniformIMul(t, d, scope, operation, value, cluster) =>
            arithmetic(CollectiveKind.IntegerMultiply, t, d, scope, operation, value, cluster)
          case Instruction.GroupNonUniformSMin(t, d, scope, operation, value, cluster) =>
            arithmetic(CollectiveKind.SignedMin, t, d, scope, operation, value, cluster)
          case Instruction.GroupNonUniformUMin(t, d, scope, operation, value, cluster) =>
            arithmetic(CollectiveKind.UnsignedMin, t, d, scope, operation, value, cluster)
          case Instruction.GroupNonUniformSMax(t, d, scope, operation, value, cluster) =>
            arithmetic(CollectiveKind.SignedMax, t, d, scope, operation, value, cluster)
          case Instruction.GroupNonUniformUMax(t, d, scope, operation, value, cluster) =>
            arithmetic(CollectiveKind.UnsignedMax, t, d, scope, operation, value, cluster)
          case Instruction.GroupNonUniformBitwiseAnd(t, d, scope, operation, value, cluster) =>
            arithmetic(CollectiveKind.BitwiseAnd, t, d, scope, operation, value, cluster)
          case Instruction.GroupNonUniformBitwiseOr(t, d, scope, operation, value, cluster) =>
            arithmetic(CollectiveKind.BitwiseOr, t, d, scope, operation, value, cluster)
          case Instruction.GroupNonUniformBitwiseXor(t, d, scope, operation, value, cluster) =>
            arithmetic(CollectiveKind.BitwiseXor, t, d, scope, operation, value, cluster)
          case Instruction.GroupNonUniformLogicalAnd(t, d, scope, operation, value, cluster) =>
            arithmetic(CollectiveKind.LogicalAnd, t, d, scope, operation, value, cluster)
          case Instruction.GroupNonUniformLogicalOr(t, d, scope, operation, value, cluster) =>
            arithmetic(CollectiveKind.LogicalOr, t, d, scope, operation, value, cluster)
          case Instruction.GroupNonUniformLogicalXor(t, d, scope, operation, value, cluster) =>
            arithmetic(CollectiveKind.LogicalXor, t, d, scope, operation, value, cluster)
          case _ => Left(Fault.Unsupported(s"${instruction.opcode} subgroup execution is not implemented"))
        }
        request.flatMap { pending =>
          if (pending.scope != 3) Left(Fault.InvalidTask("nonuniform operations require Subgroup scope"))
          else {
            suspend(
              execution,
              execution.currentFunction,
              pc,
              execution.currentLabel,
              execution.predecessor,
              execution.instructionCount,
              pending.copy(nonUniform = true)
            )
            Right(())
          }
        }
    }
  }

  private def executeNonUniformBallot(
      program: Program,
      execution: InvocationState,
      instruction: Instruction
  ): Either[Fault, Unit] = {
    val (t, d, scope, source, mode, index) = instruction match {
      case Instruction.GroupNonUniformInverseBallot(t, d, scope, source)         => (t, d, scope, source, -1, None)
      case Instruction.GroupNonUniformBallotBitExtract(t, d, scope, source, bit) => (t, d, scope, source, -1, Some(bit))
      case Instruction.GroupNonUniformBallotBitCount(t, d, scope, operation, source) =>
        (t, d, scope, source, id(operation), None)
      case Instruction.GroupNonUniformBallotFindLSB(t, d, scope, source) => (t, d, scope, source, 3, None)
      case Instruction.GroupNonUniformBallotFindMSB(t, d, scope, source) => (t, d, scope, source, 4, None)
      case _ => throw IllegalStateException("subgroup ballot dispatch mismatch")
    }
    val bit =
      index.fold[Either[Fault, Long]](Right(execution.invocation.subgroupLocal.toLong))(scalar(execution.values, _))
    (scalar(execution.values, scope), execution.values.get(id(source)), bit) match {
      case (Right(3L), Some(RuntimeValue.Bits(sourceType, words)), Right(local))
          if integerShape(program, sourceType).contains(32 -> 4) && words.length == 4 &&
            (if (mode == -1) booleanLanes(program, id(t)).contains(1)
             else integerShape(program, id(t)).contains(32 -> 1)) =>
        def set(bit: Int): Boolean = (words(bit / 32) & (1L << (bit % 32))) != 0
        val result                 = mode match {
          case -1 => if (local >= 0 && local < 128 && set(local.toInt)) 1L else 0L
          case 0  => (0 until execution.invocation.subgroupSize).count(set(_)).toLong
          case 1  => (0 to execution.invocation.subgroupLocal).count(set(_)).toLong
          case 2  => (0 until execution.invocation.subgroupLocal).count(set(_)).toLong
          case 3  => (0 until execution.invocation.subgroupSize).find(set(_)).fold(0xffffffffL)(_.toLong)
          case 4  => (0 until execution.invocation.subgroupSize).reverse.find(set(_)).fold(0xffffffffL)(_.toLong)
          case _  => -1L
        }
        if (result < 0) Left(Fault.InvalidTask("invalid subgroup ballot operation"))
        else {
          execution.values.update(id(d), RuntimeValue.Bits(id(t), Array(result)))
          execution.pc += 1
          Right(())
        }
      case (Left(error), _, _) => Left(error)
      case (_, _, Left(error)) => Left(error)
      case (_, None, _)        => Left(undefined(source))
      case _ => Left(Fault.InvalidTask("subgroup ballot requires Subgroup scope, uint4 value and compatible result"))
    }
  }

  private def executeNonUniformFloating(
      program: Program,
      execution: InvocationState,
      instruction: Instruction
  ): Either[Fault, Unit] = {
    val (resultType, destination, scope, operation, value, cluster, kind) = instruction match {
      case Instruction.GroupNonUniformFAdd(t, d, s, o, v, c) => (t, d, s, o, v, c, CollectiveKind.FloatingAdd)
      case Instruction.GroupNonUniformFMul(t, d, s, o, v, c) => (t, d, s, o, v, c, CollectiveKind.FloatingMultiply)
      case Instruction.GroupNonUniformFMin(t, d, s, o, v, c) => (t, d, s, o, v, c, CollectiveKind.FloatingMin)
      case Instruction.GroupNonUniformFMax(t, d, s, o, v, c) => (t, d, s, o, v, c, CollectiveKind.FloatingMax)
      case _ => throw IllegalStateException("nonuniform floating dispatch mismatch")
    }
    val values   = execution.values
    val function = execution.currentFunction.id
    val pc       = execution.pc
    val request  = if (cluster.nonEmpty) Left(Fault.Unsupported("clustered subgroup reductions are not implemented"))
    else kind match {
      case CollectiveKind.FloatingAdd =>
        collectiveFloatingAdd(program, values, function, pc, resultType, destination, scope, operation, value)
      case CollectiveKind.FloatingMultiply =>
        collectiveFloatingMultiply(program, values, function, pc, resultType, destination, scope, operation, value)
      case _ =>
        collectiveFloatingExtremum(
          program,
          values,
          function,
          pc,
          resultType,
          destination,
          scope,
          operation,
          value,
          kind,
          instruction.opcode.toString
        )
    }
    request.flatMap { pending =>
      if (pending.scope != 3) Left(Fault.InvalidTask("nonuniform floating operations require Subgroup scope"))
      else {
        suspend(
          execution,
          execution.currentFunction,
          pc,
          execution.currentLabel,
          execution.predecessor,
          execution.instructionCount,
          pending.copy(nonUniform = true)
        )
        Right(())
      }
    }
  }

  // Collective dispatch returns as soon as it emits a rendezvous or encounters an invalid operand.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return"))
  private def executeCollective(
      program: Program,
      execution: InvocationState,
      memory: MemoryState,
      instruction: Instruction
  ): Either[Fault, Unit] = {
    val values = execution.values
    def suspendTypedOperation(
        resultType: Operand,
        destination: Operand,
        scope: Operand,
        operation: Operand,
        source: Operand,
        kind: CollectiveKind,
        opcode: String,
        logical: Boolean
    ): Either[Fault, Unit] =
      collectiveTypedOperation(
        program,
        values,
        execution.currentFunction.id,
        execution.pc,
        resultType,
        destination,
        scope,
        operation,
        source,
        kind,
        opcode,
        logical
      ).map { request =>
        suspend(
          execution,
          execution.currentFunction,
          execution.pc,
          execution.currentLabel,
          execution.predecessor,
          execution.instructionCount,
          request
        )
      }
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
        collectiveTypedOperation(
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
        collectiveTypedOperation(
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
        collectiveTypedOperation(
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
        collectiveTypedOperation(
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
      case Instruction.GroupBitwiseAndKHR(resultType, dest, executionScope, operation, value) =>
        return suspendTypedOperation(
          resultType,
          dest,
          executionScope,
          operation,
          value,
          CollectiveKind.BitwiseAnd,
          "OpGroupBitwiseAndKHR",
          false
        )
      case Instruction.GroupBitwiseOrKHR(resultType, dest, executionScope, operation, value) =>
        return suspendTypedOperation(
          resultType,
          dest,
          executionScope,
          operation,
          value,
          CollectiveKind.BitwiseOr,
          "OpGroupBitwiseOrKHR",
          false
        )
      case Instruction.GroupBitwiseXorKHR(resultType, dest, executionScope, operation, value) =>
        return suspendTypedOperation(
          resultType,
          dest,
          executionScope,
          operation,
          value,
          CollectiveKind.BitwiseXor,
          "OpGroupBitwiseXorKHR",
          false
        )
      case Instruction.GroupLogicalAndKHR(resultType, dest, executionScope, operation, value) =>
        return suspendTypedOperation(
          resultType,
          dest,
          executionScope,
          operation,
          value,
          CollectiveKind.LogicalAnd,
          "OpGroupLogicalAndKHR",
          true
        )
      case Instruction.GroupLogicalOrKHR(resultType, dest, executionScope, operation, value) =>
        return suspendTypedOperation(
          resultType,
          dest,
          executionScope,
          operation,
          value,
          CollectiveKind.LogicalOr,
          "OpGroupLogicalOrKHR",
          true
        )
      case Instruction.GroupLogicalXorKHR(resultType, dest, executionScope, operation, value) =>
        return suspendTypedOperation(
          resultType,
          dest,
          executionScope,
          operation,
          value,
          CollectiveKind.LogicalXor,
          "OpGroupLogicalXorKHR",
          true
        )
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
      case Instruction.SubgroupShuffleXorINTEL(resultType, dest, data, invocationId) =>
        collectiveShuffle(
          values,
          execution.currentFunction.id,
          execution.pc,
          resultType,
          dest,
          data,
          invocationId,
          execution.invocation.subgroupLocal
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
      case other =>
        return Left(Fault.Unsupported(s"${other.opcode} execution is not implemented"))
    }
    InstructionCompleted
  }

  // Builtin and function-call argument decoding is indexed and fails fast.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return", "scalafix:DisableSyntax.var"))
  private def executeCall(
      program: Program,
      execution: InvocationState,
      memory: MemoryState,
      instruction: Instruction
  ): Memory.ReadResult[Unit] = {
    val values = execution.values
    instruction match {
      case Instruction.FunctionPointerCallINTEL(resultType, dest, sources) if sources.nonEmpty =>
        return values.get(id(sources.head)) match {
          case Some(RuntimeValue.Bits(_, Array(token)))
              if program.ownsFunctionPointer(token) &&
                (token & 0xffffffffL) > 0 && (token & 0xffffffffL) <= Int.MaxValue &&
                program.functions.contains((token & 0xffffffffL).toInt) =>
            val called = sources.head.copy(words = Vec.nonEmpty(Word((token & 0xffffffffL).toInt)))
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
                    val source = program.functionNames.get(id(called)) match {
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
                        s"OpenCL ${program.functionNames.get(id(called)).fold("builtin")(_)} result type " +
                          s"%${id(resultType)} is not an integer scalar"
                      )
                    )
                }
              case Right(dimension) =>
                return Left(
                  Fault.InvalidTask(
                    s"OpenCL ${program.functionNames.get(id(called)).fold("builtin")(_)} dimension $dimension is invalid"
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
                    val source = program.functionNames.get(id(called)) match {
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
                        s"OpenCL ${program.functionNames.get(id(called)).fold("builtin")(_)} result type is not an integer scalar"
                      )
                    )
                }
              case Right(dimension) =>
                return Left(
                  Fault.InvalidTask(
                    s"OpenCL ${program.functionNames.get(id(called)).fold("builtin")(_)} dimension $dimension is invalid"
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
                execution.pc += 1
              case _ =>
                return Left(
                  Fault.InvalidTask(
                    s"OpenCL ${program.functionNames.get(id(called)).fold("builtin")(_)} result type " +
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
                  if (bits == 0L) sourceWidth.raw.toLong
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
                memory.readScalar(pointer, 4) match {
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
                memory.readScalar(pointer, 2) match {
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
            (values.get(id(arguments.head)), program.layout.runtimeType(id(resultType))) match {
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
                    val (exponentMask, fractionMask) = width.raw match {
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
                val scope = program.functionNames.get(id(called)) match {
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
      execution: InvocationState,
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
            val selectorMask = mask(program.layout.scalarWidth(values.scalarType(id(selector))))
            var target       = default
            var index        = 0
            while (index < targets.size) {
              val pair    = targets(index)
              val literal = pair.words match {
                case Vec(low, _) =>
                  if (pair.kind != OperandKind.PairLiteralIntegerIdRef)
                    return Left(Fault.InvalidTask("OpSwitch target is not a literal/label pair"))
                  low.toUnsigned.toLong
                case Vec(low, high, _) =>
                  if (pair.kind != OperandKind.PairLiteralIntegerIdRef)
                    return Left(Fault.InvalidTask("OpSwitch target is not a literal/label pair"))
                  low.toUnsigned.toLong | (high.toUnsigned.toLong << 32)
                case _ =>
                  return Left(Fault.InvalidTask("OpSwitch has an incomplete literal/label pair"))
              }
              if (pair.words.length != 2 && pair.words.length != 3)
                return Left(Fault.InvalidTask("OpSwitch has an incomplete literal/label pair"))
              val label = pair.words.last
              if ((literal & selectorMask) == (value & selectorMask))
                target = Operand(OperandKind.IdRef, Vec.nonEmpty(label))
              index += 1
            }
            execution.predecessor = execution.currentLabel
            label(execution.currentFunction, target) match {
              case Left(error) => return Left(error)
              case Right(pc)   => execution.pc = pc
            }
        }
      case _: Instruction.ReturnValue | _: Instruction.Return
          if (execution.fragmentQuad || execution.subgroupReconvergence) && !execution.fragmentReturnReady =>
        finishFragmentReturn(execution)
        execution.fragmentJoin = Some(FragmentJoin.FunctionReturn(execution.currentFunction.id))
      case Instruction.ReturnValue(value) =>
        finishFragmentReturn(execution)
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
      case Instruction.Return() =>
        finishFragmentReturn(execution)
        execution.calls.removeLastOption() match {
          case None =>
            execution.complete()
            return Right(())
          case Some(frame) => program.layout.runtimeType(frame.resultType) match {
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
      case Instruction.LoopMerge(merge, _, _) =>
        enterFragmentRegion(execution, id(merge), loop = true)
        execution.pc += 1
      case Instruction.SelectionMerge(merge, _) =>
        enterFragmentRegion(execution, id(merge), loop = false)
        execution.pc += 1
      case Instruction.AssumeTrueKHR(_) => execution.pc += 1
      case other                        =>
        return Left(Fault.Unsupported(s"${other.opcode} execution is not implemented"))
    }
    InstructionCompleted
  }

  private def enterFragmentRegion(execution: InvocationState, merge: Int, loop: Boolean): Unit =
    if (
      (execution.fragmentQuad || execution.subgroupReconvergence) && !execution.fragmentRegions.exists(_.merge == merge)
    )
      execution.fragmentRegions += FragmentRegion(execution.currentFunction.id, execution.currentLabel, merge, loop)

  private def finishFragmentReturn(execution: InvocationState): Unit = {
    execution.fragmentReturnReady = false
    if (execution.fragmentQuad || execution.subgroupReconvergence)
      execution.fragmentRegions.filterInPlace(_.function != execution.currentFunction.id)
  }

  // Pause at structured merges and function returns so a lane cannot evaluate a
  // later, uniform derivative while its neighbors are still in divergent code.
  private def resolveFragmentJoins(executions: mutable.ArrayBuffer[InvocationState]): Int = {
    val ready = executions.flatMap(_.fragmentJoin).distinct.filter { join =>
      executions.forall { execution =>
        val participates = join match {
          case FragmentJoin.Merge(label)             => execution.fragmentRegions.exists(_.merge == label)
          case FragmentJoin.FunctionReturn(function) =>
            execution.currentFunction.id == function || execution.calls.exists(_.function.id == function)
        }
        !participates || execution.completed || execution.fragmentKilled || execution.fragmentJoin.contains(join)
      }
    }
    executions.foreach { execution =>
      execution.fragmentJoin.filter(ready.contains).foreach { join =>
        join match {
          case FragmentJoin.Merge(label) =>
            val index = execution.fragmentRegions.indexWhere(_.merge == label)
            execution.fragmentRegions.remove(index, execution.fragmentRegions.size - index)
          case FragmentJoin.FunctionReturn(_) => execution.fragmentReturnReady = true
        }
        execution.fragmentJoin = None
      }
    }
    ready.size
  }

  private def suspend(
      execution: InvocationState,
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
    execution.suspend(request)
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
      executions: mutable.ArrayBuffer[InvocationState],
      memory: MemoryState,
      control: TaskControl,
      metrics: VMMetrics
  ): Either[Fault, Unit] = {
    while (executions.exists(!_.completed)) {
      var index = 0
      while (index < executions.size) {
        val execution = executions(index)
        if (
          !execution.completed && execution.suspended.isEmpty && execution.quadRequest.isEmpty && execution.fragmentJoin.isEmpty
        )
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
          case Right(())   => executions.foreach(_.memoryReady())
        }
      }
      // A deferred read is a transport suspension, not subgroup divergence.
      // Resume those lanes before choosing the active set at a collective.
      if (!hadPendingMemory) resolveCollectives(program, executions, memory) match {
        case Left(error)                                                      => return Left(error)
        case Right(0) if !hadPendingMemory && executions.exists(!_.completed) =>
          val waiting = executions.view
            .filter(!_.completed)
            .map(execution =>
              execution.suspended match {
                case Some(request) =>
                  s"local=${execution.invocation.localVector.mkString("(", ",", ")")}:" +
                    s"${request.kind}@%${request.function}:${request.pc}/scope=${request.scope}"
                case None => s"local=${execution.invocation.localVector.mkString("(", ",", ")")}:running"
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
      executions: mutable.ArrayBuffer[InvocationState],
      memory: MemoryState
  ): Either[Fault, Int] = {
    var resolved = resolveQuad(executions) match {
      case Left(error)  => return Left(error)
      case Right(count) => count
    }
    val subgroups = executions.view.map(_.invocation.subgroupIndex).toSet.toArray.sorted
    var index     = 0
    while (index < subgroups.length) {
      val subgroup     = subgroups(index)
      val participants = Vec.from(executions.view.filter(_.invocation.subgroupIndex == subgroup))
      resolved += resolveFragmentJoins(mutable.ArrayBuffer.from(participants))
      val requests   = participants.flatMap(_.suspended)
      val nonUniform = requests.filter(r => r.scope == 3 && r.nonUniform)
      val sites      = nonUniform.foldLeft(Vector.empty[CollectiveRequest]) { (sites, request) =>
        if (sites.exists(sameCollective(_, request))) sites else sites :+ request
      }
      var siteIndex = 0
      while (siteIndex < sites.size) {
        val site   = sites(siteIndex)
        val active = participants.filter(_.suspended.exists(sameCollective(_, site)))
        resolveCollective(program, active, site, memory) match {
          case Left(error) => return Left(error)
          case Right(())   => resolved += 1
        }
        siteIndex += 1
      }
      if (
        nonUniform.isEmpty &&
        requests.nonEmpty && requests.head.scope == 3 &&
        participants.forall(execution =>
          !execution.completed && execution.suspended.exists(sameCollective(_, requests.head))
        )
      )
        resolveCollective(program, participants, requests.head, memory) match {
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
        resolveCollective(program, Vec.from(executions), request, memory) match {
          case Left(error) => return Left(error)
          case Right(())   => resolved += 1
        }
      case _ => ()
    }
    Right(resolved)
  }

  private def resolveQuad(executions: mutable.ArrayBuffer[InvocationState]): Either[Fault, Int] = {
    val requests = executions.flatMap(_.quadRequest)
    if (requests.isEmpty) Right(0)
    else if (executions.size != 4) Left(Fault.InvalidTask("fragment derivatives require four lanes"))
    else {
      val laneRequests                                                = executions.map(_.quadRequest).toVector
      def value(lane: Int, component: Int, site: QuadRequest): Double =
        // Non-participating neighbors have undefined operands at this dynamic
        // site. Choose zero without executing their paths or reviving killed lanes.
        laneRequests(lane).filter(r =>
          r.function == site.function && r.pc == site.pc &&
            r.operation == site.operation && r.operand.length == site.operand.length
        ).fold(0.0)(r =>
          IEEE754.binary32ToFloat(Binary32.low(r.operand(component))).toDouble
        )
      executions.zipWithIndex.foreach { (execution, lane) =>
        execution.quadRequest.foreach { request =>
          val coarse = request.operation >= 3 && request.operation <= 5
          val left   = if (coarse) 0 else lane & 2
          val top    = if (coarse) 0 else lane & 1
          val dx = Array.tabulate(request.operand.length)(i => value(left + 1, i, request) - value(left, i, request))
          val dy = Array.tabulate(request.operand.length)(i => value(top + 2, i, request) - value(top, i, request))
          if (request.operation == 6) execution.gradients = Some((dx, dy))
          else {
            val data = Array.tabulate(dx.length) { i =>
              val result = request.operation % 3 match {
                case 0 => dx(i)
                case 1 => dy(i)
                case _ => Math.abs(dx(i)) + Math.abs(dy(i))
              }
              IEEE754.floatToBinary32(result.toFloat).raw.toLong & 0xffffffffL
            }
            execution.values.update(request.destination, RuntimeValue.Bits(request.resultType, data))
            execution.pc += 1
          }
          execution.quadRequest = None
        }
      }
      Right(1)
    }
  }

  private def sameCollective(left: CollectiveRequest, right: CollectiveRequest): Boolean =
    left.kind == right.kind && left.function == right.function && left.pc == right.pc &&
      left.scope == right.scope && left.operation == right.operation && left.selector == right.selector &&
      left.nonUniform == right.nonUniform &&
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
              CollectivePayload.ShuffleUp(leftType, _, _, _, _),
              CollectivePayload.ShuffleUp(rightType, _, _, _, _)
            ) =>
          leftType == rightType
        case (
              CollectivePayload.ShuffleDown(leftType, _, _, _, _),
              CollectivePayload.ShuffleDown(rightType, _, _, _, _)
            ) =>
          leftType == rightType
        case _ => false
      })

  // Per-lane reduction and ballot loops write directly into the collective result buffer.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def resolveCollective(
      program: Program,
      participants: Vec[InvocationState],
      expected: CollectiveRequest,
      memory: MemoryState
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
      case CollectivePayload.Barrier(memoryScope, semantics) =>
        // Group participants share a mirror; broader scopes must also publish to
        // and refresh host memory. Fence once at rendezvous, before lanes resume.
        val fence = if (semantics == 0L) Right(())
        else if (memoryScope == 2 || memoryScope == 3 || memoryScope == 4) memory.flush()
        else memory.fence()
        fence.map { _ =>
          participants.foreach { execution =>
            val request = execution.suspended.get
            execution.pc = request.pc + 1
            execution.resume()
          }
        }
      case CollectivePayload.Shuffle(resultType, _, _, _) =>
        val shuffleRequests = requests.collect {
          case CollectiveRequest(_, _, _, _, CollectivePayload.Shuffle(_, destination, data, invocationId), _, _, _) =>
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
                          case Some(CollectiveRequest(_, _, _, _, CollectivePayload.Shuffle(_, _, data, _), _, _, _)) =>
                            Right(existing :+ retag(data, resultType))
                          case _ => Left(Fault.InvalidTask("subgroup shuffle source is not suspended"))
                        }
                      // SYCL selection/permutation permits an out-of-range source with
                      // an unspecified result. Retain this lane's input in that case.
                      case None => Right(existing :+ retag(request._2._2, resultType))
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
                execution.resume()
              }
              Right(())
          }
        }
      case CollectivePayload.ShuffleUp(resultType, _, _, _, _) =>
        val shuffleRequests = requests.collect {
          case CollectiveRequest(
                _,
                _,
                _,
                _,
                CollectivePayload.ShuffleUp(_, destination, previous, current, requestDelta),
                _,
                _,
                _
              ) =>
            (destination, previous, current, requestDelta)
        }
        if (shuffleRequests.size != requests.size)
          Left(Fault.InvalidTask(
            "subgroup shuffle-up participants have incompatible payloads"
          ))
        else {
          val results =
            participants.zip(shuffleRequests).foldLeft[Either[Fault, Vec[RuntimeValue]]](Right(Vec.empty)) {
              case (Right(existing), (execution, (_, _, _, delta))) =>
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
                execution.resume()
              }
              Right(())
          }
        }
      case CollectivePayload.ShuffleDown(resultType, _, _, _, _) =>
        val shuffleRequests = requests.collect {
          case CollectiveRequest(
                _,
                _,
                _,
                _,
                CollectivePayload.ShuffleDown(_, destination, current, next, requestDelta),
                _,
                _,
                _
              ) =>
            (destination, current, next, requestDelta)
        }
        if (shuffleRequests.size != requests.size)
          Left(Fault.InvalidTask(
            "subgroup shuffle-down participants have incompatible payloads"
          ))
        else {
          val results =
            participants.zip(shuffleRequests).foldLeft[Either[Fault, Vec[RuntimeValue]]](Right(Vec.empty)) {
              case (Right(existing), (execution, (_, _, _, delta))) =>
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
                execution.resume()
              }
              Right(())
          }
        }
      case CollectivePayload.Value(resultType, _, _) =>
        val requestValues = requests.collect {
          case CollectiveRequest(_, _, _, _, CollectivePayload.Value(_, _, value), _, _, _) =>
            value
        }
        if (requestValues.size != requests.size)
          Left(
            Fault.InvalidTask("collective participants have incompatible payloads")
          )
        else {
          val results = expected.kind match {
            case CollectiveKind.Elect =>
              Right(Vec.from(participants.indices.map(i =>
                RuntimeValue.Bits(resultType, Array(if (i == 0) 1L else 0L))
              )))
            case CollectiveKind.BroadcastFirst => Right(Vec.fill(participants.size)(requestValues.head))
            case CollectiveKind.AllEqual       =>
              val inputs = requestValues.collect { case RuntimeValue.Bits(t, lanes) => (t, lanes) }
              val equal  = inputs.size == requestValues.size && inputs.tail.forall { case (t, lanes) =>
                val first = inputs.head._2
                lanes.length == first.length && lanes.indices.forall { lane =>
                  floatingShape(program, t) match {
                    case Some((width, _)) =>
                      floatingToDouble(width, lanes(lane)) == floatingToDouble(width, first(lane))
                    case None => lanes(lane) == first(lane)
                  }
                }
              }
              Right(Vec.fill(participants.size)(RuntimeValue.Bits(resultType, Array(if (equal) 1L else 0L))))
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
                val width: Int where Between[1, 64] = integerShape(program, resultType).map(_._1).getOrElse(64)
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
                      if (maximum) 1L << (width.raw - 1) else (1L << (width.raw - 1)) - 1L
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
                val width: Int where Between[1, 64] = integerShape(program, resultType).map(_._1).getOrElse(64)
                if (expected.operation == 0) {
                  val reduced = Array.tabulate(inputs.head.length) { lane =>
                    inputs.view.foldLeft(0L)((sum, input) => (sum + input(lane)) & mask(width))
                  }
                  Right(Vec.fill(participants.size)(RuntimeValue.Bits(resultType, reduced)))
                } else {
                  Right(scan(inputs, 0L, resultType)((left, right) => (left + right) & mask(width)))
                }
              }
            case CollectiveKind.BitwiseAnd | CollectiveKind.BitwiseOr | CollectiveKind.BitwiseXor |
                CollectiveKind.LogicalAnd | CollectiveKind.LogicalOr | CollectiveKind.LogicalXor =>
              val inputs = requestValues.collect { case RuntimeValue.Bits(_, lanes) => lanes }
              if (inputs.size != requests.size || inputs.isEmpty)
                Left(Fault.InvalidTask(s"${expected.kind} participants do not all provide matching values"))
              else {
                val logical    = booleanLanes(program, resultType).nonEmpty
                val resultMask = if (logical) 1L else mask(integerShape(program, resultType).get._1)
                val identity   = expected.kind match {
                  case CollectiveKind.BitwiseAnd | CollectiveKind.LogicalAnd => resultMask
                  case _                                                     => 0L
                }
                def combine(left: Long, right: Long): Long = {
                  val normalized = if (logical) { if (right != 0L) 1L else 0L }
                  else right
                  (expected.kind match {
                    case CollectiveKind.BitwiseAnd | CollectiveKind.LogicalAnd => left & normalized
                    case CollectiveKind.BitwiseOr | CollectiveKind.LogicalOr   => left | normalized
                    case _                                                     => left ^ normalized
                  }) & resultMask
                }
                if (expected.operation == 0) {
                  val reduced = Array.tabulate(inputs.head.length) { lane =>
                    inputs.view.foldLeft(identity)((acc, input) => combine(acc, input(lane)))
                  }
                  Right(Vec.fill(participants.size)(RuntimeValue.Bits(resultType, reduced)))
                } else Right(scan(inputs, identity, resultType)(combine(_, _)))
              }
            case CollectiveKind.IntegerMultiply =>
              val inputs = requestValues.collect { case RuntimeValue.Bits(_, lanes) => lanes }
              if (inputs.size != requests.size || inputs.isEmpty)
                Left(Fault.InvalidTask(
                  "OpGroupIMulKHR participants do not all provide integer values"
                ))
              else {
                val width: Int where Between[1, 64] = integerShape(program, resultType).map(_._1).getOrElse(64)
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
                val width: OneOf[(16, 32, 64)] = floatingShape(program, resultType).map(_._1).getOrElse(64)
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
                val width: OneOf[(16, 32, 64)]              = floatingShape(program, resultType).map(_._1).getOrElse(64)
                val maximum                                 = expected.kind == CollectiveKind.FloatingMax
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
                val width: OneOf[(16, 32, 64)] = floatingShape(program, resultType).map(_._1).getOrElse(64)
                val identity                   = floatingOneBits(width)
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
                case (_, Vec(x, y)) =>
                  participants.indexWhere(execution =>
                    execution.invocation.localX == x && execution.invocation.localY == y &&
                      execution.invocation.localZ == 0
                  )
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
                execution.resume()
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
          if integerShape(program, id(resultType)).contains(32 -> 4) &&
            booleanLanes(program, predType).contains(1) =>
        Right(
          CollectiveRequest(
            CollectiveKind.Ballot,
            function,
            pc,
            3,
            CollectivePayload.Value(id(resultType), id(destination), value),
            nonUniform = true
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
      case (Right(executionScope @ (2L | 3L)), Right(memoryScope), Right(memorySemantics))
          if memoryScope >= 0L && memoryScope <= 6L && (memorySemantics & ~0xffdeL) == 0L &&
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
      case (Right(_), Right(scope), _) if scope < 0L || scope > 6L =>
        Left(Fault.InvalidTask(
          s"OpControlBarrier has invalid memory scope $scope"
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
      invocation: InvocationBuiltins,
      resultType: Operand,
      resultDestination: Operand,
      executionScope: Operand,
      destinationId: Operand,
      sourceId: Operand,
      numElements: Operand,
      stride: Operand
  ): Memory.ReadResult[Unit] =
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
        (destination.storageClass == StorageClass.Workgroup && source.storageClass == StorageClass.CrossWorkgroup) ||
          (destination.storageClass == StorageClass.CrossWorkgroup && source.storageClass == StorageClass.Workgroup),
        (),
        Fault.InvalidTask(
          s"OpGroupAsyncCopy requires Workgroup/CrossWorkgroup pointers, got ${destination.storageClass}/${source.storageClass}"
        )
      )
      _ <- {
        val elementSize = program.layout.typeSize(destination.pointee)
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

  private def collectiveTypedOperation(
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
      opcode: String,
      logical: Boolean = false
  ): Either[Fault, CollectiveRequest] =
    (scalar(values, executionScope), values.get(id(source))) match {
      case (Right(scope @ (2L | 3L)), Some(value @ RuntimeValue.Bits(sourceType, lanes)))
          if Vec(0L, 1L, 2L).contains(unsigned(operation)) &&
            (if (logical) booleanLanes(program, id(resultType)) == booleanLanes(program, sourceType) &&
             booleanLanes(program, sourceType).contains(lanes.length)
             else integerShape(program, id(resultType)) == integerShape(program, sourceType) &&
             integerShape(program, sourceType).exists(_._2 == lanes.length)) =>
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
          s"$opcode requires matching ${if (logical) "boolean" else "integer"} scalar or vector types"
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
      invocationId: Operand,
      sourceXor: Int = 0
  ): Either[Fault, CollectiveRequest] =
    (values.get(id(data)), values.get(id(invocationId))) match {
      case (
            Some(dataValue @ RuntimeValue.Bits(dataType, dataLanes)),
            Some(RuntimeValue.Bits(selectorType, Array(selector)))
          )
          if dataType == id(resultType) && dataLanes.nonEmpty =>
        Right(
          CollectiveRequest(
            CollectiveKind.Shuffle,
            function,
            pc,
            3,
            CollectivePayload.Shuffle(
              id(resultType),
              id(destination),
              dataValue,
              RuntimeValue.Bits(selectorType, Array(selector ^ sourceXor.toLong))
            )
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
            count == lanes.length && Vec(1, 2, 3).contains(count)
          } =>
        val width: Int where Between[1, 64] = integerShape(program, localIdType).map(_._1).getOrElse(64)
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
            "OpGroupBroadcast requires matching result/source bit types and a scalar, two-lane or three-lane integer local id"
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
    program.layout.runtimeType(parameterType) match {
      case Some(RuntimeType.Pointer(storageClass, pointee)) => argument match {
          case KernelTask.Argument.Memory(allocation, offset, _) =>
            program.layout.pointerLeafOffsets(pointee, 0L) match {
              case Right(leaves) if leaves.isEmpty =>
                Right(RuntimeValue.Address(
                  parameterType,
                  Pointer(Region.External(allocation), offset.raw, pointee, storageClass)
                ))
              case Right(_) => bindExternalAggregatePointerArgument(
                  program,
                  parameterType,
                  storageClass,
                  pointee,
                  allocation,
                  offset.raw,
                  memory
                )
              case Left(error) => Left(error)
            }
          case KernelTask.Argument.Scalar(bytes)
              if !byValue && bytes.toArray.forall(_ == 0) &&
                !program.layout.runtimeType(pointee).exists {
                  case RuntimeType.Structure(_) | RuntimeType.Array(_, _) | RuntimeType.MatrixType(_, _) => true
                  case _                                                                                 => false
                } =>
            Right(RuntimeValue.Address(parameterType, Pointer(Region.Null, 0L, pointee, storageClass)))
          case KernelTask.Argument.Scalar(bytes)
              // A ByVal aggregate can contain a pointer in opaque byte-array storage at offset zero.
              // That relocation belongs inside the private copy, not to the parameter pointer itself.
              if !byValue && memory.argumentRelocation(argumentIndex, 0L).isDefined &&
                (program.layout.pointerLeafOffsets(pointee, 0L).toOption.exists(_.isEmpty) ||
                  program.layout.typeSize(pointee) > bytes.size) =>
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
              if !byValue && bytes.size == 8 && (storageClass == StorageClass.CrossWorkgroup || storageClass == StorageClass.Generic) =>
            memory.integerPointer(
              LittleEndian.decode(bytes, 0, 8),
              pointee,
              storageClass
            )
              .map(pointer => RuntimeValue.Address(parameterType, pointer))
          case KernelTask.Argument.Scalar(bytes) =>
            memory.relocateArgument(argumentIndex, bytes) match {
              case Left(error)      => Left(error)
              case Right(relocated) =>
                val size   = math.max(program.layout.typeSize(pointee), relocated.length)
                val buffer = Array.ofDim[Byte](size)
                Array.copy(relocated, 0, buffer, 0, relocated.length)
                val slots = mutable.LongMap.empty[(Int, Pointer)]
                program.layout.pointerLeafOffsets(pointee, 0L) match {
                  case Right(leaves) => leaves.foreach { case (offset, pointerType) =>
                      program.layout.runtimeType(pointerType) match {
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
            val region = shared.getOrElse(Region.Internal(InternalMemory(Array.ofDim[Byte](checkedSize(size.raw)))))
            Right(RuntimeValue.Address(parameterType, Pointer(region, 0L, pointee, storageClass)))
        }
      case Some(_) => argument match {
          case KernelTask.Argument.Scalar(bytes) =>
            memory.relocateArgument(argumentIndex, bytes).flatMap(decoded => program.decode(parameterType, decoded))
          case value => value match {
              case KernelTask.Argument.Local(size) =>
                // Some DPC++ Level Zero kernels lower a local pointer parameter to a
                // 64-bit physical address and convert it back with OpConvertUToPtr.
                // Register the per-workgroup region in the same virtual address map
                // used by ordinary pointer conversions.
                val region =
                  shared.getOrElse(Region.Internal(InternalMemory(Array.ofDim[Byte](checkedSize(size.raw)))))
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
      allocation: AllocationId,
      allocationOffset: Long,
      memory: MemoryState
  ): Either[Fault, RuntimeValue] =
    program.layout.pointerLeafOffsets(pointee, 0L) match {
      case Left(error)   => Left(error)
      case Right(leaves) =>
        val slots = mutable.LongMap.empty[(Int, Pointer)]
        leaves.foreach { case (slotOffset, pointerType) =>
          program.layout.runtimeType(pointerType) match {
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

  def pointerLeafOffsetsForTest(
      module: Module,
      typeId: Int
  ): Either[Fault, Vec[(Long, Int)]] =
    VMCompiler.compileUnchecked(module)
      .map(VMProgramRuntime(_))
      .flatMap(program => program.layout.pointerLeafOffsets(typeId, 0L))

  private def loadPointer(
      program: Program,
      memory: MemoryState,
      source: Pointer,
      resultType: Int
  ): Memory.ReadResult[RuntimeValue] = program.layout.runtimeType(resultType) match {
    case Some(RuntimeType.Pointer(resultStorage, resultPointee)) =>
      val stored = if (memory.nativePointerStorage(source)) None
      else source.slots.flatMap(_.get(source.offset)).orElse(
        memory.relocatedPointer(source, resultPointee, resultStorage).map(resultType -> _)
      )
      val resolved = stored match {
        case Some(pointer) => Right(pointer)
        case None          => memory.readScalar(source, 8)
            .flatMap(raw => memory.integerPointer(raw, resultPointee, resultStorage))
            .map(resultType -> _)
      }
      resolved.flatMap { case (slotType, pointer) =>
        program.layout.runtimeType(slotType) match {
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
                  s"expected %$resultType (${program.layout.runtimeType(resultType)}), " +
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
    (program.layout.runtimeType(target.pointee), program.layout.runtimeType(sourceType)) match {
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
              _       <- memory.writeScalar(target, address, 8)
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
    var source = address(values, base) match {
      case Left(error)  => return Left(error)
      case Right(value) => value
    }
    val initial = element match {
      case Some(value) =>
        scalar(values, value).map(index =>
          (
            memory.elementOffset(source, index, program.layout.storageTypeSize(source.pointee, source.matrixLayout)),
            source.pointee
          )
        )
      case None => Right((source.offset, source.pointee))
    }
    val (baseOffset, baseType) = initial match {
      case Left(error)  => return Left(error)
      case Right(value) => value
    }
    var matrixLayout = source.matrixLayout
    var offset       = baseOffset
    var typeId       = baseType
    var position     = 0
    while (position < indexes.size) {
      val index = scalar(values, indexes(position)) match {
        case Left(error)  => return Left(error)
        case Right(value) => value
      }
      program.layout.runtimeType(typeId) match {
        case Some(RuntimeType.Structure(members)) =>
          if (index < 0L || index >= members.size.toLong)
            return Left(
              Fault.InvalidTask(s"structure member index $index is out of range")
            )
          offset += program.layout.structMemberOffset(typeId, members, index.toInt)
          matrixLayout = program.layout.memberMatrixLayouts.get(typeId -> index.toInt)
          typeId = members(index.toInt)
        case Some(RuntimeType.Array(member, _)) =>
          source.descriptors match {
            case Some(descriptors) =>
              if (index < 0 || index >= descriptors.size || element.nonEmpty)
                return Left(Fault.InvalidTask(s"descriptor array index $index is out of range"))
              source = descriptors(index.toInt)
              offset = source.offset
            case None =>
              offset += index * program.arrayStrides.getOrElse(
                typeId,
                program.layout.storageTypeSize(member, matrixLayout)
              )
          }
          typeId = member
        case Some(RuntimeType.MatrixType(column, _)) =>
          val stride = if (matrixLayout.exists(_.rowMajor)) program.layout.runtimeType(column) match {
            case Some(RuntimeType.VectorType(component, _)) => program.layout.typeSize(component)
            case _ => return Left(Fault.InvalidTask("invalid matrix column type"))
          }
          else matrixLayout.map(_.stride).getOrElse(program.layout.typeSize(column))
          offset += index * stride
          matrixLayout = matrixLayout.filter(_.rowMajor)
          typeId = column
        case Some(RuntimeType.VectorType(component, _)) =>
          offset += index * matrixLayout.filter(_.rowMajor).map(_.stride).getOrElse(program.layout.typeSize(component))
          matrixLayout = None
          typeId = component
        case Some(value) =>
          return Left(Fault.InvalidTask(s"cannot index through SPIR-V type $value"))
        case None =>
          return Left(Fault.InvalidTask(s"SPIR-V type %$typeId is undefined"))
      }
      position += 1
    }
    val outputType = id(resultType)
    val pointee    = program.layout.pointerPointee(outputType)
    values.update(
      id(destination),
      RuntimeValue.Address(
        outputType,
        source.copy(offset = offset, pointee = pointee, matrixLayout = matrixLayout)
      )
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
    (program.layout.runtimeType(outputType), values.get(id(source))) match {
      case (
            Some(RuntimeType.Pointer(targetStorage, targetPointee)),
            Some(RuntimeValue.Address(sourceType, pointer))
          ) if Vec(4, 5, 7).contains(targetStorage) =>
        program.layout.runtimeType(sourceType) match {
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
    (program.layout.runtimeType(outputType), values.get(id(source))) match {
      case (
            Some(RuntimeType.Pointer(resultStorage, targetPointee)),
            Some(RuntimeValue.Address(sourceType, pointer))
          ) if resultStorage == targetStorage && Vec(4, 5, 7).contains(targetStorage) =>
        program.layout.runtimeType(sourceType) match {
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
      program.layout.runtimeType(current) match {
        case Some(RuntimeType.VectorType(component, count)) if selected < count.toLong =>
          program.layout.scalarLeafCount(component) match {
            case Left(error)   => return Left(error)
            case Right(leaves) => offset += Math.toIntExact(selected * leaves.toLong)
          }
          current = component
        case Some(RuntimeType.MatrixType(column, count)) if selected < count.toLong =>
          program.layout.scalarLeafCount(column) match {
            case Left(error)   => return Left(error)
            case Right(leaves) => offset += Math.toIntExact(selected * leaves.toLong)
          }
          current = column
        case Some(RuntimeType.Array(element, count)) if selected < count =>
          program.layout.scalarLeafCount(element) match {
            case Left(error)   => return Left(error)
            case Right(leaves) => offset += Math.toIntExact(selected * leaves.toLong)
          }
          current = element
        case Some(RuntimeType.Structure(members)) if selected < members.size.toLong =>
          var member = 0
          while (member < selected.toInt) {
            program.layout.scalarLeafCount(members(member)) match {
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
    program.layout.scalarLeafCount(current).map(leaves => offset -> leaves)
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
    val value  = bits.toInt
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
    val nearest = IEEE754.doubleToBinary16(value)
    // The core conversion already rounds to nearest even, including overflow.
    // Directed modes only need to move to an adjacent representable value when
    // the nearest result lies on the wrong side of the original value.
    if (value.isNaN || value.isInfinite) nearest
    else {
      val rounded = IEEE754.binary16ToFloat(nearest).toDouble
      mode match {
        case FPRoundingMode.RTZ if value > 0.0 && rounded > value => nextBinary16(nearest, upward = false)
        case FPRoundingMode.RTZ if value < 0.0 && rounded < value => nextBinary16(nearest, upward = true)
        case FPRoundingMode.RTP if rounded < value                => nextBinary16(nearest, upward = true)
        case FPRoundingMode.RTN if rounded > value                => nextBinary16(nearest, upward = false)
        case _                                                    => nearest
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
  ): Memory.ReadResult[Unit] = {
    val vector = sources.size == 3
    if ((!vector && sources.size != 2) || (vector && sources.size != 3))
      return Left(Fault.InvalidTask("OpenCL.std half load has an invalid operand count"))
    (floatingShape(program, id(resultType)), scalar(values, sources(0)), address(values, sources(1))) match {
      case (Some((width, count)), Right(offset), Right(pointer))
          if width.raw == 32 && (!vector || unsigned(sources(2)) == count.toLong) =>
        val stride = if (aligned && count == 3) 4 else count
        val base   = Math.addExact(pointer.offset, Math.multiplyExact(offset, stride.toLong * 2L))
        val result = Array.ofDim[Long](count)
        var lane   = 0
        while (lane < count) {
          memory.readScalar(
            pointer.copy(offset = Math.addExact(base, lane.toLong * 2L)),
            2
          ) match {
            case Left(error) => return Left(error)
            case Right(half) =>
              result(lane) = IEEE754.floatToBinary32(IEEE754.binary16ToFloat(Binary16.low(half))).toLong
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
          case Some((width, count)) if width.raw == 32 || width.raw == 64 =>
            val stride = if (aligned && count == 3) 4 else count
            val base   = Math.addExact(pointer.offset, Math.multiplyExact(offset, stride.toLong * 2L))
            val mode   = if (rounded) id(sources(3)) else 0
            var lane   = 0
            while (lane < count) {
              val source = floatingToDouble(width, lanes(lane))
              memory.writeScalar(
                pointer.copy(offset = Math.addExact(base, lane.toLong * 2L)),
                doubleToBinary16(source, mode).toLong,
                2
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
  ): Memory.ReadResult[Unit] =
    if (sources.size != 3)
      Left(Fault.InvalidTask(
        s"OpenCL.std vloadn expects 3 operands, received ${sources.size}"
      ))
    else
      (program.layout.runtimeType(id(resultType)), scalar(values, sources(0)), address(values, sources(1))) match {
        case (_, Left(error), _) => Left(error)
        case (_, _, Left(error)) => Left(error)
        case (Some(RuntimeType.VectorType(component, count)), Right(offset), Right(pointer))
            if unsigned(sources(2)) == count.toLong =>
          val elementSize = program.layout.scalarMemoryWidth(component) match {
            case Left(error) => return Left(error)
            case Right(size) => size
          }
          val base = Math.addExact(
            pointer.offset,
            Math.multiplyExact(Math.multiplyExact(offset, count.toLong), elementSize.raw.toLong)
          )
          val lanes = Array.ofDim[Long](count)
          var index = 0
          while (index < count) {
            memory.readScalar(
              pointer.copy(
                offset = Math.addExact(base, Math.multiplyExact(index.toLong, elementSize.raw.toLong)),
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
          program.layout.runtimeType(sourceType) match {
            case Some(RuntimeType.VectorType(component, count)) if lanes.length == count =>
              val elementSize = program.layout.scalarMemoryWidth(component) match {
                case Left(error) => return Left(error)
                case Right(size) => size
              }
              val base = Math.addExact(
                pointer.offset,
                Math.multiplyExact(Math.multiplyExact(offset, count.toLong), elementSize.raw.toLong)
              )
              var index = 0
              while (index < count) {
                memory.writeScalar(
                  pointer.copy(
                    offset = Math.addExact(base, Math.multiplyExact(index.toLong, elementSize.raw.toLong)),
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
              width.raw match {
                case 16 =>
                  val from = floatingToDouble(width, leftLanes(index))
                  val to   = floatingToDouble(width, rightLanes(index))
                  if (from.isNaN || to.isNaN) doubleToFloating(width, Double.NaN)
                  else if (from == to) rightLanes(index)
                  else if (from == 0.0) if (to < 0.0) 0x8001L else 1L
                  else leftLanes(index) + (if ((to > from) == (from > 0.0)) 1L else -1L)
                case 32 =>
                  IEEE754.floatToBinary32(
                    Math.nextAfter(
                      IEEE754.binary32ToFloat(Binary32.low(leftLanes(index))),
                      IEEE754.binary32ToFloat(Binary32.low(rightLanes(index)))
                    )
                  ).toLong
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

  @SuppressWarnings(Array("scalafix:DisableSyntax.return", "scalafix:DisableSyntax.var"))
  private def openCLIntegerArithmetic(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      opcode: Int,
      sources: Vec[Operand]
  ): Either[Fault, Unit] = {
    import ExtendedInstruction.OpenClStd.*
    val arity = opcode match {
      case `SClamp` | `UClamp` | `SMadHi` | `UMadHi` | `SMadSat` | `UMadSat` | `SMad24` | `UMad24` => 3
      case _                                                                                       => 2
    }
    val widening     = opcode == SUpsample || opcode == UUpsample
    val smallProduct = opcode == SMul24 || opcode == UMul24 || opcode == SMad24 || opcode == UMad24
    if (sources.size != arity)
      return Left(
        Fault.InvalidTask(s"OpenCL.std integer opcode $opcode expects $arity operands, received ${sources.size}")
      )
    integerShape(program, id(resultType)) match {
      case Some((width, count))
          if (width.raw == 8 || width.raw == 16 || width.raw == 32 || width.raw == 64) &&
            (!widening || width.raw != 8) && (!smallProduct || width.raw == 32) =>
        val inputWidth = if (widening) (width.raw / 2).refine[Int where Between[1, 64]].toOption.get else width
        val inputs     = Array.ofDim[Array[Long]](arity)
        var index      = 0
        while (index < arity) {
          values.get(id(sources(index))) match {
            case Some(RuntimeValue.Bits(sourceType, lanes))
                if integerShape(program, sourceType).contains(inputWidth -> count) && lanes.length == count =>
              inputs(index) = lanes
            case None => return Left(undefined(sources(index)))
            case _    => return Left(
                Fault.InvalidTask(s"OpenCL.std integer opcode $opcode requires matching integer scalar/vector types")
              )
          }
          index += 1
        }
        val signed = opcode match {
          case `SAbsDiff` | `SAddSat` | `SRhadd` | `SClamp` | `SMadHi` | `SMadSat` |
              `SMulHi` | `SSubSat` | `SUpsample` | `SMad24` | `SMul24` => true
          case _ => false
        }
        // Preserve wide intermediates until saturation or high-half extraction, including 64-bit products.
        val modulus                                = BigInt(1) << inputWidth.raw
        val maximum                                = if (signed) (modulus >> 1) - 1 else modulus - 1
        val minimum                                = if (signed) -(modulus >> 1) else BigInt(0)
        val productWidth: Int where Between[1, 64] = if (smallProduct) 24 else inputWidth
        val productMask                            = (BigInt(1) << productWidth.raw) - 1
        def integer(raw: Long): BigInt             =
          if (signed) BigInt(signExtend(raw, inputWidth)) else BigInt(raw) & (modulus - 1)
        def factor(raw: Long): BigInt =
          if (signed) BigInt(signExtend(raw, productWidth)) else BigInt(raw) & productMask
        val result = Array.tabulate(count) { lane =>
          val left       = factor(inputs(0)(lane))
          val right      = factor(inputs(1)(lane))
          val third      = if (arity == 3) integer(inputs(2)(lane)) else BigInt(0)
          val calculated = opcode match {
            case `SAbsDiff` | `UAbsDiff` => (left - right).abs
            case `SAddSat` | `UAddSat`   => (left + right).max(minimum).min(maximum)
            case `SSubSat` | `USubSat`   => (left - right).max(minimum).min(maximum)
            case `SClamp` | `UClamp`     => left.max(right).min(third)
            case `UHadd`                 => (left + right) >> 1
            case `SRhadd` | `URhadd`     => (left + right + 1) >> 1
            case `SMulHi` | `UMulHi`     => (left * right) >> width.raw
            case `SMadHi` | `UMadHi`     => ((left * right) >> width.raw) + third
            case `SMadSat` | `UMadSat`   => (left * right + third).max(minimum).min(maximum)
            case `Rotate`                =>
              val shift = right.toInt & (width.raw - 1)
              (left << shift) | (left >> (width.raw - shift))
            case `SUpsample` | `UUpsample` => (left << inputWidth.raw) | (right & (modulus - 1))
            case `SMul24` | `UMul24`       => left * right
            case `SMad24` | `UMad24`       => left * right + third
          }
          calculated.toLong & mask(width)
        }
        values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
        Right(())
      case _ =>
        Left(Fault.InvalidTask(s"OpenCL.std integer opcode $opcode requires an 8, 16, 32 or 64-bit integer result"))
    }
  }

  private def openCLAbs(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand,
      signed: Boolean
  ): Either[Fault, Unit] = values.get(id(source)) match {
    case Some(RuntimeValue.Bits(sourceType, lanes)) =>
      (integerShape(program, sourceType), integerShape(program, id(resultType))) match {
        case (Some((sourceWidth, sourceLanes)), Some((resultWidth, resultLanes)))
            if sourceWidth == resultWidth && sourceLanes == resultLanes && lanes.length == sourceLanes =>
          val result = lanes.map { raw =>
            val value = if (signed) signExtend(raw, sourceWidth) else raw
            (if (signed && value < 0L) -value else value) & mask(resultWidth)
          }
          values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
          Right(())
        case _ =>
          Left(Fault.InvalidTask(
            "OpenCL.std abs requires matching integer scalar/vector types"
          ))
      }
    case Some(_) => Left(Fault.InvalidTask("OpenCL.std abs operand is a pointer"))
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
              sourceWidth.raw.toLong
            else
              (java.lang.Long.numberOfLeadingZeros(value) - (64 - sourceWidth.raw)).toLong
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
            if (value == 0L) sourceWidth.raw.toLong else java.lang.Long.numberOfTrailingZeros(value).toLong
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
  ): Memory.ReadResult[Unit] = {
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
              arguments += Event.PrintArgument.Integer(Vec.from(lanes), width.raw)
            case None => floatingShape(program, typeId) match {
                case Some((width, _)) =>
                  arguments += Event.PrintArgument.Floating(Vec.from(lanes), width.raw)
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
    memory.handler(Event.Print(format, Vec.from(arguments))).flatMap { _ =>
      integerShape(program, id(resultType)) match {
        case Some((width, 1)) =>
          values.update(id(destination), RuntimeValue.Bits(id(resultType), Array(0L & mask(width))))
          Right(())
        case _ => Left(Fault.InvalidTask("OpenCL.std printf result is not an integer scalar"))
      }
    }
  }

  private def signedIntegerShape(program: Program, typeId: Int): Option[(Int where Between[1, 64], Int, Boolean)] =
    program.layout.runtimeType(typeId) match {
      case Some(RuntimeType.Integer(width, signed))       => Some((width, 1, signed))
      case Some(RuntimeType.VectorType(component, count)) => program.layout.runtimeType(component) match {
          case Some(RuntimeType.Integer(width, signed)) => Some((width, count, signed))
          case _                                        => None
        }
      case _ => None
    }

  @SuppressWarnings(Array("scalafix:DisableSyntax.return", "scalafix:DisableSyntax.var"))
  private def readCString(memory: MemoryState, pointer: Pointer): Memory.ReadResult[String] = {
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
  ): Memory.ReadResult[Unit] = {
    val setId         = id(set)
    val encodedOpcode = id(instruction)
    // The half_* family permits reduced precision; the native implementation
    // supplies at least that accuracy, including the binary divide/powr forms.
    val opcode =
      if (
        program.extendedInstructionSets.get(setId).contains("OpenCL.std") &&
        encodedOpcode >= ExtendedInstruction.OpenClStd.HalfCos && encodedOpcode <= ExtendedInstruction.OpenClStd.HalfTan
      )
        encodedOpcode + ExtendedInstruction.OpenClStd.NativeCos - ExtendedInstruction.OpenClStd.HalfCos
      else encodedOpcode
    program.extendedInstructionSets.get(setId) match {
      case None =>
        Left(Fault.InvalidTask(s"OpExtInst references undefined import %$setId"))
      case Some("GLSL.std.450")
          if opcode >= ExtendedInstruction.GlslStd450.Round && opcode <= ExtendedInstruction.GlslStd450.InverseSqrt && opcode != ExtendedInstruction.GlslStd450.SAbs && opcode != ExtendedInstruction.GlslStd450.SSign && opcode != ExtendedInstruction.GlslStd450.Atan2 && opcode != ExtendedInstruction.GlslStd450.Pow =>
        glslUnaryFloating(program, values, resultType, destination, opcode, sources)
      case Some("GLSL.std.450")
          if opcode == ExtendedInstruction.GlslStd450.FMin || opcode == ExtendedInstruction.GlslStd450.FMax || opcode == ExtendedInstruction.GlslStd450.FClamp || opcode == ExtendedInstruction.GlslStd450.NMin || opcode == ExtendedInstruction.GlslStd450.NMax || opcode == ExtendedInstruction.GlslStd450.NClamp =>
        glslNumericMinMax(program, values, resultType, destination, opcode, sources)
      case Some("GLSL.std.450")
          if opcode == ExtendedInstruction.GlslStd450.SAbs || opcode == ExtendedInstruction.GlslStd450.SSign || opcode == ExtendedInstruction.GlslStd450.UMin || opcode == ExtendedInstruction.GlslStd450.SMin || opcode == ExtendedInstruction.GlslStd450.UMax || opcode == ExtendedInstruction.GlslStd450.SMax ||
            opcode == ExtendedInstruction.GlslStd450.UClamp || opcode == ExtendedInstruction.GlslStd450.SClamp || opcode == ExtendedInstruction.GlslStd450.FindILsb || opcode == ExtendedInstruction.GlslStd450.FindSMsb || opcode == ExtendedInstruction.GlslStd450.FindUMsb =>
        glslInteger(program, values, resultType, destination, opcode, sources)
      case Some("GLSL.std.450")
          if opcode == ExtendedInstruction.GlslStd450.Determinant || opcode == ExtendedInstruction.GlslStd450.MatrixInverse =>
        glslMatrix(program, values, resultType, destination, opcode, sources)
      case Some("GLSL.std.450")
          if opcode == ExtendedInstruction.GlslStd450.Length || opcode == ExtendedInstruction.GlslStd450.Distance =>
        glslLengthDistance(program, values, resultType, destination, opcode, sources)
      case Some("GLSL.std.450")
          if opcode >= ExtendedInstruction.GlslStd450.PackSnorm4x8 && opcode <= ExtendedInstruction.GlslStd450.UnpackDouble2x32 =>
        glslPack(program, values, resultType, destination, opcode, sources)
      case Some("GLSL.std.450")
          if opcode == ExtendedInstruction.GlslStd450.Modf || opcode == ExtendedInstruction.GlslStd450.ModfStruct || opcode == ExtendedInstruction.GlslStd450.Frexp || opcode == ExtendedInstruction.GlslStd450.FrexpStruct =>
        glslDecompose(program, values, memory, resultType, destination, opcode, sources)
      case Some("GLSL.std.450") if opcode == ExtendedInstruction.GlslStd450.Atan2 =>
        if (sources.size != 2) Left(Fault.InvalidTask("GLSL Atan2 expects two operands"))
        else openCLAtan2OrHypot(program, values, resultType, destination, sources(0), sources(1), false)
      case Some("GLSL.std.450")
          if opcode == ExtendedInstruction.GlslStd450.FMix || opcode == ExtendedInstruction.GlslStd450.Step || opcode == ExtendedInstruction.GlslStd450.SmoothStep =>
        val arity = if (opcode == ExtendedInstruction.GlslStd450.Step) 2 else 3
        if (sources.size != arity) Left(Fault.InvalidTask(s"GLSL opcode $opcode expects $arity operands"))
        else
          openCLFloatingElementwise(program, values, resultType, destination, sources, opcode.toString, "GLSL.std.450") {
            a =>
              opcode match {
                case ExtendedInstruction.GlslStd450.FMix => a(0) * (1.0 - a(2)) + a(1) * a(2)
                case ExtendedInstruction.GlslStd450.Step => if (a(1) < a(0)) 0.0 else 1.0
                case _                                   =>
                  val t = Math.max(0.0, Math.min(1.0, (a(2) - a(0)) / (a(1) - a(0))))
                  t * t * (3.0 - 2.0 * t)
              }
          }
      case Some("GLSL.std.450") if opcode == ExtendedInstruction.GlslStd450.Fma =>
        if (sources.size != 3) Left(Fault.InvalidTask("GLSL Fma expects three operands"))
        else openCLMad(program, values, resultType, destination, sources(0), sources(1), sources(2), true)
      case Some("GLSL.std.450") if opcode == ExtendedInstruction.GlslStd450.Ldexp =>
        if (sources.size != 2) Left(Fault.InvalidTask("GLSL Ldexp expects two operands"))
        else openCLLdexp(program, values, resultType, destination, sources(0), sources(1), glsl = true)
      case Some("GLSL.std.450")
          if opcode == ExtendedInstruction.GlslStd450.Cross || opcode == ExtendedInstruction.GlslStd450.Normalize =>
        openCLGeometric(
          program,
          values,
          resultType,
          destination,
          if (opcode == ExtendedInstruction.GlslStd450.Cross) ExtendedInstruction.OpenClStd.Cross
          else ExtendedInstruction.OpenClStd.Normalize,
          sources
        )
      case Some("GLSL.std.450")
          if opcode >= ExtendedInstruction.GlslStd450.FaceForward && opcode <= ExtendedInstruction.GlslStd450.Refract =>
        glslGeometric(program, values, resultType, destination, opcode, sources)
      case Some("GLSL.std.450") if opcode == ExtendedInstruction.GlslStd450.Pow =>
        if (sources.size != 2)
          Left(Fault.InvalidTask("GLSL.std.450 Pow expects two operands"))
        else openCLPow(program, values, resultType, destination, sources(0), sources(1))
      case Some("OpenCL.std") if opcode == ExtendedInstruction.OpenClStd.Copysign =>
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
                  val signMask = width.raw match {
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
          if opcode == ExtendedInstruction.OpenClStd.Fma || opcode == ExtendedInstruction.OpenClStd.Mad =>
        val name = if (opcode == ExtendedInstruction.OpenClStd.Fma) "fma" else "mad"
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
          opcode == ExtendedInstruction.OpenClStd.Fma
        )
      case Some("OpenCL.std")
          if opcode == ExtendedInstruction.OpenClStd.Maxmag || opcode == ExtendedInstruction.OpenClStd.Minmag =>
        if (sources.size != 2)
          Left(Fault.InvalidTask(
            s"OpenCL.std maxmag expects 2 operands, received ${sources.size}"
          ))
        else openCLMagnitude(
          program,
          values,
          resultType,
          destination,
          sources(0),
          sources(1),
          opcode == ExtendedInstruction.OpenClStd.Maxmag
        )
      case Some("OpenCL.std") if opcode == ExtendedInstruction.OpenClStd.Pow =>
        if (sources.size != 2)
          Left(Fault.InvalidTask(
            s"OpenCL.std pow expects 2 operands, received ${sources.size}"
          ))
        else openCLPow(program, values, resultType, destination, sources(0), sources(1))
      case Some("OpenCL.std")
          if opcode >= ExtendedInstruction.OpenClStd.SMax && opcode <= ExtendedInstruction.OpenClStd.UMin =>
        val name = opcode match {
          case ExtendedInstruction.OpenClStd.SMax => "s_max"
          case ExtendedInstruction.OpenClStd.UMax => "u_max"
          case ExtendedInstruction.OpenClStd.SMin => "s_min"
          case _                                  => "u_min"
        }
        val width: Int where Between[1, 64] = integerShape(program, id(resultType)).map(_._1).getOrElse(64)
        if (sources.size != 2)
          Left(Fault.InvalidTask(
            s"OpenCL.std $name expects 2 operands, received ${sources.size}"
          ))
        else
          integerBitwise(program, values, resultType, destination, sources(0), sources(1), s"OpenCL.std $name") {
            (left, right) =>
              opcode match {
                case ExtendedInstruction.OpenClStd.SMax =>
                  if (signExtend(left, width) >= signExtend(right, width)) left else right
                case ExtendedInstruction.OpenClStd.UMax =>
                  if (java.lang.Long.compareUnsigned(left, right) >= 0) left else right
                case ExtendedInstruction.OpenClStd.SMin =>
                  if (signExtend(left, width) <= signExtend(right, width)) left else right
                case _ => if (java.lang.Long.compareUnsigned(left, right) <= 0) left else right
              }
          }
      case Some("OpenCL.std")
          if opcode == ExtendedInstruction.OpenClStd.Sincos ||
            opcode == ExtendedInstruction.OpenClStd.Fract || opcode == ExtendedInstruction.OpenClStd.Modf ||
            opcode == ExtendedInstruction.OpenClStd.Frexp || opcode == ExtendedInstruction.OpenClStd.LgammaR =>
        if (sources.size != 2)
          Left(Fault.InvalidTask(
            s"OpenCL.std pointer math opcode $opcode expects 2 operands, received ${sources.size}"
          ))
        else openCLPointerMath(program, values, memory, resultType, destination, sources(0), sources(1), opcode)
      case Some("OpenCL.std") if opcode == ExtendedInstruction.OpenClStd.Ilogb =>
        if (sources.size != 1) Left(Fault.InvalidTask("OpenCL.std ilogb requires one operand"))
        else openCLIlogb(program, values, resultType, destination, sources.head)
      case Some("OpenCL.std") if opcode == ExtendedInstruction.OpenClStd.Remquo =>
        if (sources.size != 3) Left(Fault.InvalidTask("OpenCL.std remquo requires three operands"))
        else openCLRemquo(program, values, memory, resultType, destination, sources)
      case Some("OpenCL.std") if opcode == ExtendedInstruction.OpenClStd.Printf =>
        openCLPrintf(program, values, memory, resultType, destination, sources)
      case Some("OpenCL.std")
          if opcode == ExtendedInstruction.OpenClStd.Fmax || opcode == ExtendedInstruction.OpenClStd.Fmin =>
        val name = if (opcode == ExtendedInstruction.OpenClStd.Fmax) "fmax" else "fmin"
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
            if (opcode == ExtendedInstruction.OpenClStd.Fmax) FloatingOperation.Maximum else FloatingOperation.Minimum
          )
      case Some("OpenCL.std") if opcode == ExtendedInstruction.OpenClStd.Ldexp =>
        if (sources.size != 2)
          Left(Fault.InvalidTask(
            s"OpenCL.std ldexp expects 2 operands, received ${sources.size}"
          ))
        else openCLLdexp(program, values, resultType, destination, sources(0), sources(1))
      case Some("OpenCL.std")
          if OpenCLUnaryFloatingOpcodes.contains(opcode) =>
        val name = opcode match {
          case ExtendedInstruction.OpenClStd.Acos        => "acos"
          case ExtendedInstruction.OpenClStd.Acosh       => "acosh"
          case ExtendedInstruction.OpenClStd.Acospi      => "acospi"
          case ExtendedInstruction.OpenClStd.Asin        => "asin"
          case ExtendedInstruction.OpenClStd.Asinh       => "asinh"
          case ExtendedInstruction.OpenClStd.Asinpi      => "asinpi"
          case ExtendedInstruction.OpenClStd.Atan        => "atan"
          case ExtendedInstruction.OpenClStd.Atanh       => "atanh"
          case ExtendedInstruction.OpenClStd.Atanpi      => "atanpi"
          case ExtendedInstruction.OpenClStd.Cbrt        => "cbrt"
          case ExtendedInstruction.OpenClStd.Ceil        => "ceil"
          case ExtendedInstruction.OpenClStd.Cos         => "cos"
          case ExtendedInstruction.OpenClStd.Cosh        => "cosh"
          case ExtendedInstruction.OpenClStd.Cospi       => "cospi"
          case ExtendedInstruction.OpenClStd.Erf         => "erf"
          case ExtendedInstruction.OpenClStd.Erfc        => "erfc"
          case ExtendedInstruction.OpenClStd.Tgamma      => "tgamma"
          case ExtendedInstruction.OpenClStd.Lgamma      => "lgamma"
          case ExtendedInstruction.OpenClStd.Exp         => "exp"
          case ExtendedInstruction.OpenClStd.Exp2        => "exp2"
          case ExtendedInstruction.OpenClStd.Exp10       => "exp10"
          case ExtendedInstruction.OpenClStd.Expm1       => "expm1"
          case ExtendedInstruction.OpenClStd.Fabs        => "fabs"
          case ExtendedInstruction.OpenClStd.Floor       => "floor"
          case ExtendedInstruction.OpenClStd.Log         => "log"
          case ExtendedInstruction.OpenClStd.Log2        => "log2"
          case ExtendedInstruction.OpenClStd.Log10       => "log10"
          case ExtendedInstruction.OpenClStd.Log1p       => "log1p"
          case ExtendedInstruction.OpenClStd.Logb        => "logb"
          case ExtendedInstruction.OpenClStd.Rint        => "rint"
          case ExtendedInstruction.OpenClStd.Round       => "round"
          case ExtendedInstruction.OpenClStd.Rsqrt       => "rsqrt"
          case ExtendedInstruction.OpenClStd.Sin         => "sin"
          case ExtendedInstruction.OpenClStd.Sinh        => "sinh"
          case ExtendedInstruction.OpenClStd.Sinpi       => "sinpi"
          case ExtendedInstruction.OpenClStd.Sqrt        => "sqrt"
          case ExtendedInstruction.OpenClStd.Tan         => "tan"
          case ExtendedInstruction.OpenClStd.Tanh        => "tanh"
          case ExtendedInstruction.OpenClStd.Tanpi       => "tanpi"
          case ExtendedInstruction.OpenClStd.Trunc       => "trunc"
          case ExtendedInstruction.OpenClStd.NativeCos   => "native_cos"
          case ExtendedInstruction.OpenClStd.NativeExp   => "native_exp"
          case ExtendedInstruction.OpenClStd.NativeExp2  => "native_exp2"
          case ExtendedInstruction.OpenClStd.NativeExp10 => "native_exp10"
          case ExtendedInstruction.OpenClStd.NativeLog   => "native_log"
          case ExtendedInstruction.OpenClStd.NativeLog2  => "native_log2"
          case ExtendedInstruction.OpenClStd.NativeLog10 => "native_log10"
          case ExtendedInstruction.OpenClStd.NativeRecip => "native_recip"
          case ExtendedInstruction.OpenClStd.NativeRsqrt => "native_rsqrt"
          case ExtendedInstruction.OpenClStd.NativeSin   => "native_sin"
          case ExtendedInstruction.OpenClStd.NativeSqrt  => "native_sqrt"
          case ExtendedInstruction.OpenClStd.NativeTan   => "native_tan"
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
                      case ExtendedInstruction.OpenClStd.Acos   => Math.acos(value)
                      case ExtendedInstruction.OpenClStd.Acosh  => OpenCLMath.acosh(value)
                      case ExtendedInstruction.OpenClStd.Acospi => Math.acos(value) / Math.PI
                      case ExtendedInstruction.OpenClStd.Asin   => Math.asin(value)
                      case ExtendedInstruction.OpenClStd.Asinh  => OpenCLMath.asinh(value)
                      case ExtendedInstruction.OpenClStd.Asinpi => Math.asin(value) / Math.PI
                      case ExtendedInstruction.OpenClStd.Atan   => Math.atan(value)
                      case ExtendedInstruction.OpenClStd.Atanh  => OpenCLMath.atanh(value)
                      case ExtendedInstruction.OpenClStd.Atanpi => Math.atan(value) / Math.PI
                      case ExtendedInstruction.OpenClStd.Cbrt   => Math.cbrt(value)
                      case ExtendedInstruction.OpenClStd.Ceil   => Math.ceil(value)
                      case ExtendedInstruction.OpenClStd.Cos    => Math.cos(value)
                      case ExtendedInstruction.OpenClStd.Cosh   => Math.cosh(value)
                      case ExtendedInstruction.OpenClStd.Cospi  => openCLCosPi(value)
                      case ExtendedInstruction.OpenClStd.Erf    => OpenCLMath.erf(value)
                      case ExtendedInstruction.OpenClStd.Erfc   => OpenCLMath.erfc(value)
                      case ExtendedInstruction.OpenClStd.Tgamma => OpenCLMath.tgamma(value)
                      case ExtendedInstruction.OpenClStd.Lgamma => OpenCLMath.lgamma(value)
                      case ExtendedInstruction.OpenClStd.Exp    => Math.exp(value)
                      case ExtendedInstruction.OpenClStd.Exp2   => Math.pow(2.0, value)
                      case ExtendedInstruction.OpenClStd.Exp10  => Math.pow(10.0, value)
                      case ExtendedInstruction.OpenClStd.Expm1  => Math.expm1(value)
                      case ExtendedInstruction.OpenClStd.Fabs   => Math.abs(value)
                      case ExtendedInstruction.OpenClStd.Floor  => Math.floor(value)
                      case ExtendedInstruction.OpenClStd.Log    => Math.log(value)
                      case ExtendedInstruction.OpenClStd.Log2   => Math.log(value) / Math.log(2.0)
                      case ExtendedInstruction.OpenClStd.Log10  => Math.log10(value)
                      case ExtendedInstruction.OpenClStd.Log1p  => Math.log1p(value)
                      case ExtendedInstruction.OpenClStd.Logb   =>
                        if (value.isNaN) Double.NaN
                        else if (value == 0.0) Double.NegativeInfinity
                        else if (value.isInfinite) Double.PositiveInfinity
                        else openCLExponent(value).toDouble
                      case ExtendedInstruction.OpenClStd.Rint  => Math.rint(value)
                      case ExtendedInstruction.OpenClStd.Round =>
                        if (value < 0.0) Math.ceil(value - 0.5) else Math.floor(value + 0.5)
                      case ExtendedInstruction.OpenClStd.Rsqrt => 1.0 / Math.sqrt(value)
                      case ExtendedInstruction.OpenClStd.Sin   => Math.sin(value)
                      case ExtendedInstruction.OpenClStd.Sinh  => Math.sinh(value)
                      case ExtendedInstruction.OpenClStd.Sinpi => openCLSinPi(value)
                      case ExtendedInstruction.OpenClStd.Sqrt  => Math.sqrt(value)
                      case ExtendedInstruction.OpenClStd.Tan   => Math.tan(value)
                      case ExtendedInstruction.OpenClStd.Tanh  => Math.tanh(value)
                      case ExtendedInstruction.OpenClStd.Tanpi => openCLTanPi(value)
                      case ExtendedInstruction.OpenClStd.Trunc =>
                        if (value < 0.0) Math.ceil(value) else Math.floor(value)
                      case ExtendedInstruction.OpenClStd.NativeCos   => Math.cos(value)
                      case ExtendedInstruction.OpenClStd.NativeExp   => Math.exp(value)
                      case ExtendedInstruction.OpenClStd.NativeExp2  => Math.pow(2.0, value)
                      case ExtendedInstruction.OpenClStd.NativeExp10 => Math.pow(10.0, value)
                      case ExtendedInstruction.OpenClStd.NativeLog   => Math.log(value)
                      case ExtendedInstruction.OpenClStd.NativeLog2  => Math.log(value) / Math.log(2.0)
                      case ExtendedInstruction.OpenClStd.NativeLog10 => Math.log10(value)
                      case ExtendedInstruction.OpenClStd.NativeRecip => 1.0 / value
                      case ExtendedInstruction.OpenClStd.NativeRsqrt => 1.0 / Math.sqrt(value)
                      case ExtendedInstruction.OpenClStd.NativeSin   => Math.sin(value)
                      case ExtendedInstruction.OpenClStd.NativeSqrt  => Math.sqrt(value)
                      case ExtendedInstruction.OpenClStd.NativeTan   => Math.tan(value)
                    }
                    resultWidth.raw match {
                      case 16 if opcode == ExtendedInstruction.OpenClStd.Fabs => raw & 0x7fffL
                      case 32 if opcode == ExtendedInstruction.OpenClStd.Fabs => raw & 0x7fffffffL
                      case 64 if opcode == ExtendedInstruction.OpenClStd.Fabs => raw & 0x7fffffffffffffffL
                      case 16                                                 =>
                        IEEE754.doubleToBinary16(
                          evaluate(IEEE754.binary16ToFloat(Binary16.low(raw)).toDouble)
                        ).raw
                      case 32 =>
                        IEEE754.floatToBinary32(
                          evaluate(IEEE754.binary32ToFloat(Binary32.low(raw)).toDouble).toFloat
                        ).toLong
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
          case ExtendedInstruction.OpenClStd.Atan2        => "atan2"
          case ExtendedInstruction.OpenClStd.Atan2pi      => "atan2pi"
          case ExtendedInstruction.OpenClStd.Fdim         => "fdim"
          case ExtendedInstruction.OpenClStd.Fmod         => "fmod"
          case ExtendedInstruction.OpenClStd.Remainder    => "remainder"
          case ExtendedInstruction.OpenClStd.Hypot        => "hypot"
          case ExtendedInstruction.OpenClStd.NativeDivide => "native_divide"
          case ExtendedInstruction.OpenClStd.NativePowr   => "native_powr"
          case _                                          => "powr"
        }
        if (sources.size != 2)
          Left(Fault.InvalidTask(
            s"OpenCL.std $name expects 2 operands, received ${sources.size}"
          ))
        else if (opcode == ExtendedInstruction.OpenClStd.Atan2 || opcode == ExtendedInstruction.OpenClStd.Hypot)
          openCLAtan2OrHypot(
            program,
            values,
            resultType,
            destination,
            sources(0),
            sources(1),
            opcode == ExtendedInstruction.OpenClStd.Hypot
          )
        else
          openCLFloatingElementwise(program, values, resultType, destination, sources, name) { operands =>
            opcode match {
              case ExtendedInstruction.OpenClStd.Atan2pi      => Math.atan2(operands(0), operands(1)) / Math.PI
              case ExtendedInstruction.OpenClStd.Fdim         => Math.max(operands(0) - operands(1), 0.0)
              case ExtendedInstruction.OpenClStd.NativeDivide => operands(0) / operands(1)
              case ExtendedInstruction.OpenClStd.Fmod         => operands(0) % operands(1)
              case ExtendedInstruction.OpenClStd.Remainder    => Math.IEEEremainder(operands(0), operands(1))
              case _                                          => openCLPowr(operands(0), operands(1))
            }
          }
      case Some("OpenCL.std") if opcode == ExtendedInstruction.OpenClStd.Nan =>
        if (sources.size != 1)
          Left(Fault.InvalidTask(
            s"OpenCL.std nan expects 1 operand, received ${sources.size}"
          ))
        else openCLNan(program, values, resultType, destination, sources.head)
      case Some("OpenCL.std")
          if opcode == ExtendedInstruction.OpenClStd.Pown || opcode == ExtendedInstruction.OpenClStd.Rootn =>
        val name = if (opcode == ExtendedInstruction.OpenClStd.Pown) "pown" else "rootn"
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
          opcode == ExtendedInstruction.OpenClStd.Rootn
        )
      case Some("OpenCL.std") if opcode == ExtendedInstruction.OpenClStd.Nextafter =>
        if (sources.size != 2)
          Left(Fault.InvalidTask(
            s"OpenCL.std nextafter expects 2 operands, received ${sources.size}"
          ))
        else openCLNextAfter(program, values, resultType, destination, sources(0), sources(1))
      case Some("OpenCL.std")
          if (opcode >= ExtendedInstruction.OpenClStd.SAbsDiff && opcode <= ExtendedInstruction.OpenClStd.UClamp &&
            opcode != ExtendedInstruction.OpenClStd.SHadd) ||
            (opcode >= ExtendedInstruction.OpenClStd.SMadHi && opcode <= ExtendedInstruction.OpenClStd.SMadSat) ||
            (opcode >= ExtendedInstruction.OpenClStd.SMulHi && opcode <= ExtendedInstruction.OpenClStd.SUpsample) ||
            (opcode >= ExtendedInstruction.OpenClStd.SMad24 && opcode <= ExtendedInstruction.OpenClStd.UMul24) ||
            (opcode >= ExtendedInstruction.OpenClStd.UAbsDiff && opcode <= ExtendedInstruction.OpenClStd.UMadHi) =>
        openCLIntegerArithmetic(program, values, resultType, destination, opcode, sources)
      case Some("OpenCL.std") if opcode == ExtendedInstruction.OpenClStd.SHadd =>
        if (sources.size != 2)
          Left(Fault.InvalidTask(
            s"OpenCL.std hadd expects 2 operands, received ${sources.size}"
          ))
        else openCLSignedHalfAdd(program, values, resultType, destination, sources(0), sources(1))
      case Some("OpenCL.std")
          if opcode == ExtendedInstruction.OpenClStd.SAbs || opcode == ExtendedInstruction.OpenClStd.UAbs =>
        if (sources.size != 1)
          Left(Fault.InvalidTask(
            s"OpenCL.std abs expects 1 operand, received ${sources.size}"
          ))
        else openCLAbs(
          program,
          values,
          resultType,
          destination,
          sources.head,
          opcode == ExtendedInstruction.OpenClStd.SAbs
        )
      case Some("OpenCL.std") if opcode == ExtendedInstruction.OpenClStd.Clz =>
        if (sources.size != 1)
          Left(Fault.InvalidTask(
            s"OpenCL.std clz expects 1 operand, received ${sources.size}"
          ))
        else openCLCountLeadingZeros(program, values, resultType, destination, sources.head)
      case Some("OpenCL.std") if opcode == ExtendedInstruction.OpenClStd.Ctz =>
        if (sources.size != 1)
          Left(Fault.InvalidTask(
            s"OpenCL.std ctz expects 1 operand, received ${sources.size}"
          ))
        else openCLCountTrailingZeros(program, values, resultType, destination, sources.head)
      case Some("OpenCL.std") if opcode == ExtendedInstruction.OpenClStd.Popcount =>
        if (sources.size != 1)
          Left(Fault.InvalidTask(
            s"OpenCL.std popcount expects 1 operand, received ${sources.size}"
          ))
        else openCLPopulationCount(program, values, resultType, destination, sources.head)
      case Some("OpenCL.std")
          if opcode >= ExtendedInstruction.OpenClStd.Fclamp && opcode <= ExtendedInstruction.OpenClStd.Sign =>
        openCLCommon(program, values, resultType, destination, opcode, sources)
      case Some("OpenCL.std")
          if opcode >= ExtendedInstruction.OpenClStd.Cross && opcode <= ExtendedInstruction.OpenClStd.FastNormalize =>
        openCLGeometric(program, values, resultType, destination, opcode, sources)
      case Some("OpenCL.std")
          if opcode == ExtendedInstruction.OpenClStd.VloadHalf || opcode == ExtendedInstruction.OpenClStd.VloadHalfn || opcode == ExtendedInstruction.OpenClStd.VloadaHalfn =>
        openCLHalfLoad(
          program,
          values,
          memory,
          resultType,
          destination,
          sources,
          aligned = opcode == ExtendedInstruction.OpenClStd.VloadaHalfn
        )
      case Some("OpenCL.std")
          if opcode >= ExtendedInstruction.OpenClStd.VstoreHalf && opcode <= ExtendedInstruction.OpenClStd.VstoreaHalfnR =>
        openCLHalfStore(
          program,
          values,
          memory,
          sources,
          aligned = opcode >= ExtendedInstruction.OpenClStd.VstoreaHalfn,
          rounded =
            opcode == ExtendedInstruction.OpenClStd.VstoreHalfR || opcode == ExtendedInstruction.OpenClStd.VstoreHalfnR || opcode == ExtendedInstruction.OpenClStd.VstoreaHalfnR
        )
      case Some("OpenCL.std") if opcode == ExtendedInstruction.OpenClStd.Vloadn =>
        openCLVectorLoad(program, values, memory, resultType, destination, sources)
      case Some("OpenCL.std") if opcode == ExtendedInstruction.OpenClStd.Vstoren =>
        if (sources.size != 3)
          Left(Fault.InvalidTask(
            s"OpenCL.std vstoren expects 3 operands, received ${sources.size}"
          ))
        else
          openCLVectorStore(program, values, memory, sources.head, Vec(sources(1), sources(2)))
      case Some("OpenCL.std")
          if opcode == ExtendedInstruction.OpenClStd.Shuffle || opcode == ExtendedInstruction.OpenClStd.Shuffle2 =>
        openCLShuffle(
          program,
          values,
          resultType,
          destination,
          sources,
          opcode == ExtendedInstruction.OpenClStd.Shuffle2
        )
      case Some("OpenCL.std") if opcode == ExtendedInstruction.OpenClStd.Bitselect =>
        openCLBitselect(program, values, resultType, destination, sources)
      case Some("OpenCL.std") if opcode == ExtendedInstruction.OpenClStd.Select =>
        openCLSelect(program, values, resultType, destination, sources)
      case Some("OpenCL.std") if opcode == ExtendedInstruction.OpenClStd.Prefetch =>
        if (sources.isEmpty)
          Left(Fault.InvalidTask("OpenCL.std prefetch is missing its pointer operand"))
        else address(values, sources.head).map(_ => ())
      case Some(name) =>
        Left(Fault.Unsupported(
          s"OpExtInst $name opcode $opcode execution is not implemented"
        ))
    }
  }

  private def glslLengthDistance(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      opcode: Int,
      sources: Vec[Operand]
  ): Either[Fault, Unit] =
    if (sources.size != (if (opcode == ExtendedInstruction.GlslStd450.Length) 1 else 2))
      Left(Fault.InvalidTask("GLSL Length/Distance operand count is invalid"))
    else for {
      resultShape <- floatingShape(
        program,
        id(resultType)
      ).toRight(Fault.InvalidTask("GLSL Length/Distance result must be floating point"))
      operands <- sources.traverse { source =>
        values.get(id(source)) match {
          case Some(RuntimeValue.Bits(typeId, lanes)) => floatingShape(
              program,
              typeId
            ).toRight(Fault.InvalidTask("GLSL Length/Distance operand must be floating point")).map(shape =>
              (shape, lanes)
            )
          case _ => Left(Fault.InvalidTask("GLSL Length/Distance operand is missing"))
        }
      }
      _ <- Either.cond(
        resultShape._2 == 1 && operands.forall { case (shape, lanes) =>
          shape == operands.head._1 && shape._1 == resultShape._1 && lanes.length == shape._2
        },
        (),
        Fault.InvalidTask("GLSL Length/Distance types do not match")
      )
    } yield {
      val width  = resultShape._1
      val length = operands.head._2.indices.foldLeft(0.0) { (length, lane) =>
        val a     = floatingToDouble(width, operands(0)._2(lane))
        val value = if (opcode == ExtendedInstruction.GlslStd450.Length) a
        else floatingToDouble(width, doubleToFloating(width, a - floatingToDouble(width, operands(1)._2(lane))))
        Math.hypot(length, value)
      }
      values.update(id(destination), RuntimeValue.Bits(id(resultType), Array(doubleToFloating(width, length))))
    }

  private def glslMatrix(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      opcode: Int,
      sources: Vec[Operand]
  ): Either[Fault, Unit] =
    if (sources.size != 1) Left(Fault.InvalidTask("GLSL matrix operation requires one operand"))
    else values.get(id(sources.head)) match {
      case Some(RuntimeValue.Bits(typeId, lanes)) => matrixShape(program, typeId) match {
          case Some(shape @ (width, rows, columns))
              if rows == columns && lanes.length == rows * columns &&
                (if (opcode == ExtendedInstruction.GlslStd450.Determinant)
                   floatingShape(program, id(resultType)).contains(width -> 1)
                 else matrixShape(program, id(resultType)).contains(shape)) =>
            val matrix = Vector.tabulate(rows, columns)((r, c) => floatingToDouble(width, lanes(c * rows + r)))
            def minor(m: Vector[Vector[Double]], row: Int, column: Int): Vector[Vector[Double]] =
              m.zipWithIndex.collect {
                case (values, r) if r != row =>
                  values.zipWithIndex.collect { case (value, c) if c != column => value }
              }
            def determinant(m: Vector[Vector[Double]]): Double =
              if (m.size == 1) m.head.head
              else (1 until m.size).foldLeft(m.head.head * determinant(minor(m, 0, 0))) { (sum, column) =>
                val term = m.head(column) * determinant(minor(m, 0, column))
                if (column % 2 == 0) sum + term else sum - term
              }
            val det    = determinant(matrix)
            val result = if (opcode == ExtendedInstruction.GlslStd450.Determinant) Array(doubleToFloating(width, det))
            else Array.tabulate(rows * columns) { lane =>
              val r        = lane % rows
              val c        = lane / rows
              val cofactor = determinant(minor(matrix, c, r))
              doubleToFloating(width, (if ((r + c) % 2 == 0) cofactor else -cofactor) / det)
            }
            values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
            Right(())
          case _ => Left(Fault.InvalidTask("GLSL determinant/inverse requires matching square matrix types"))
        }
      case None => Left(undefined(sources.head))
      case _    => Left(Fault.InvalidTask("GLSL determinant/inverse operand is not matrix data"))
    }

  private def glslInteger(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      opcode: Int,
      sources: Vec[Operand]
  ): Either[Fault, Unit] = {
    val unary =
      opcode == ExtendedInstruction.GlslStd450.SAbs || opcode == ExtendedInstruction.GlslStd450.SSign || opcode >= ExtendedInstruction.GlslStd450.FindILsb
    val required = if (unary) 1
    else if (opcode == ExtendedInstruction.GlslStd450.UClamp || opcode == ExtendedInstruction.GlslStd450.SClamp) 3
    else 2
    if (sources.size != required) Left(Fault.InvalidTask(s"GLSL integer opcode $opcode has the wrong operand count"))
    else integerShape(program, id(resultType)).toRight(
      Fault.InvalidTask(s"GLSL integer opcode $opcode requires an integer type")
    ).flatMap { case shape @ (width, count) =>
      if (opcode >= ExtendedInstruction.GlslStd450.FindILsb && width.raw != 32)
        Left(Fault.InvalidTask("GLSL integer bit searches require 32-bit components"))
      else sources.traverse { source =>
        values.get(id(source)) match {
          case Some(RuntimeValue.Bits(typeId, lanes))
              if integerShape(program, typeId).contains(shape) && lanes.length == count => Right(lanes)
          case None => Left(undefined(source))
          case _    => Left(Fault.InvalidTask(s"GLSL integer opcode $opcode operand types do not match"))
        }
      }.map { operands =>
        // The instruction selects signedness independently of the operand type's signedness.
        val signed =
          opcode == ExtendedInstruction.GlslStd450.SMin || opcode == ExtendedInstruction.GlslStd450.SMax || opcode == ExtendedInstruction.GlslStd450.SClamp
        def less(a: Long, b: Long): Boolean =
          if (signed) signExtend(a, width) < signExtend(b, width)
          else java.lang.Long.compareUnsigned(a, b) < 0
        def minimum(a: Long, b: Long): Long = if (less(b, a)) b else a
        def maximum(a: Long, b: Long): Long = if (less(a, b)) b else a
        val result                          = Array.tabulate(count) { lane =>
          val a     = operands(0)(lane) & mask(width)
          val b     = if (unary) 0L else operands(1)(lane) & mask(width)
          val value = opcode match {
            case ExtendedInstruction.GlslStd450.SAbs     => if (signExtend(a, width) < 0) -a else a
            case ExtendedInstruction.GlslStd450.SSign    => java.lang.Long.signum(signExtend(a, width)).toLong
            case ExtendedInstruction.GlslStd450.FindILsb =>
              if (a == 0) -1L else java.lang.Long.numberOfTrailingZeros(a).toLong
            case ExtendedInstruction.GlslStd450.FindSMsb =>
              val bits = if (signExtend(a, width) < 0) ~a & mask(width) else a
              (63 - java.lang.Long.numberOfLeadingZeros(bits)).toLong
            case ExtendedInstruction.GlslStd450.FindUMsb => (63 - java.lang.Long.numberOfLeadingZeros(a)).toLong
            case ExtendedInstruction.GlslStd450.UMin | ExtendedInstruction.GlslStd450.SMin => minimum(a, b)
            case ExtendedInstruction.GlslStd450.UMax | ExtendedInstruction.GlslStd450.SMax => maximum(a, b)
            case _ => minimum(maximum(a, b), operands(2)(lane) & mask(width))
          }
          value & mask(width)
        }
        values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
      }
    }
  }

  private def glslNumericMinMax(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      opcode: Int,
      sources: Vec[Operand]
  ): Either[Fault, Unit] = {
    val required =
      if (opcode == ExtendedInstruction.GlslStd450.FClamp || opcode == ExtendedInstruction.GlslStd450.NClamp) 3 else 2
    if (sources.size != required) Left(Fault.InvalidTask("GLSL numeric min/max has the wrong operand count"))
    else floatingShape(
      program,
      id(resultType)
    ).toRight(Fault.InvalidTask("GLSL numeric min/max requires a floating type")).flatMap {
      case shape @ (width, count) =>
        sources.traverse { source =>
          values.get(id(source)) match {
            case Some(RuntimeValue.Bits(typeId, lanes))
                if floatingShape(program, typeId).contains(shape) && lanes.length == count => Right(lanes)
            case None => Left(undefined(source))
            case _    => Left(Fault.InvalidTask("GLSL numeric min/max operand types do not match"))
          }
        }.map { operands =>
          // FMin/FMax permit this same numeric operand preference for NaNs as NMin/NMax.
          def minimum(a: Double, b: Double): Double = if (a.isNaN) b else if (b.isNaN) a else Math.min(a, b)
          def maximum(a: Double, b: Double): Double = if (a.isNaN) b else if (b.isNaN) a else Math.max(a, b)
          val result                                = Array.tabulate(count) { lane =>
            val a     = floatingToDouble(width, operands(0)(lane))
            val b     = floatingToDouble(width, operands(1)(lane))
            val value = opcode match {
              case ExtendedInstruction.GlslStd450.FMin | ExtendedInstruction.GlslStd450.NMin => minimum(a, b)
              case ExtendedInstruction.GlslStd450.FMax | ExtendedInstruction.GlslStd450.NMax => maximum(a, b)
              case _ => minimum(maximum(a, b), floatingToDouble(width, operands(2)(lane)))
            }
            doubleToFloating(width, value)
          }
          values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
        }
    }
  }

  private def glslPack(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      opcode: Int,
      sources: Vec[Operand]
  ): Either[Fault, Unit] =
    if (sources.size != 1) Left(Fault.InvalidTask("GLSL packing expects one operand"))
    else values.get(id(sources.head)) match {
      case Some(RuntimeValue.Bits(sourceType, lanes)) =>
        val sourceFloat = floatingShape(program, sourceType).map((w, n) => w.raw -> n)
        val resultFloat = floatingShape(program, id(resultType)).map((w, n) => w.raw -> n)
        val sourceInt   = signedIntegerShape(program, sourceType).map((w, n, signed) => (w.raw, n, signed))
        val resultInt   = signedIntegerShape(program, id(resultType)).map((w, n, signed) => (w.raw, n, signed))
        val count       =
          if (
            opcode == ExtendedInstruction.GlslStd450.PackSnorm4x8 || opcode == ExtendedInstruction.GlslStd450.PackUnorm4x8 || opcode == ExtendedInstruction.GlslStd450.UnpackSnorm4x8 || opcode == ExtendedInstruction.GlslStd450.UnpackUnorm4x8
          ) 4
          else 2
        val result =
          if (
            opcode <= ExtendedInstruction.GlslStd450.PackHalf2x16 && sourceFloat.contains(
              32 -> count
            ) && resultInt.contains((
              32,
              1,
              false
            )) && lanes.length == count
          ) {
            val bits   = if (count == 4) 8 else 16
            val signed =
              opcode == ExtendedInstruction.GlslStd450.PackSnorm4x8 || opcode == ExtendedInstruction.GlslStd450.PackSnorm2x16
            val scale  = if (signed) (1 << (bits - 1)) - 1 else (1 << bits) - 1
            val packed = lanes.zipWithIndex.foldLeft(0L) { case (acc, (raw, lane)) =>
              val value = IEEE754.binary32ToFloat(Binary32.low(raw))
              val word  = if (opcode == ExtendedInstruction.GlslStd450.PackHalf2x16)
                IEEE754.floatToBinary16(value).raw.toLong
              else Math.round(Math.max(if (signed) -1.0 else 0.0, Math.min(1.0, value.toDouble)) * scale)
              acc | ((word & ((1L << bits) - 1)) << (bits * lane))
            }
            Some(Array(packed))
          } else if (
            opcode == ExtendedInstruction.GlslStd450.PackDouble2x32 && sourceInt.contains((
              32,
              2,
              false
            )) && resultFloat.contains(64 -> 1) && lanes.length == 2
          )
            Some(Array((lanes(0) & 0xffffffffL) | (lanes(1) << 32)))
          else if (
            opcode == ExtendedInstruction.GlslStd450.UnpackDouble2x32 && sourceFloat.contains(
              64 -> 1
            ) && resultInt.contains((32, 2, false)) && lanes.length == 1
          )
            Some(Array(lanes(0) & 0xffffffffL, lanes(0) >>> 32))
          else if (
            opcode >= ExtendedInstruction.GlslStd450.UnpackSnorm2x16 && opcode <= ExtendedInstruction.GlslStd450.UnpackUnorm4x8 && sourceInt.contains(
              (
                32,
                1,
                false
              )
            ) && resultFloat.contains(32 -> count) && lanes.length == 1
          ) {
            val bits   = if (count == 4) 8 else 16
            val signed =
              opcode == ExtendedInstruction.GlslStd450.UnpackSnorm2x16 || opcode == ExtendedInstruction.GlslStd450.UnpackSnorm4x8
            Some(Array.tabulate(count) { lane =>
              val word  = (lanes(0) >>> (bits * lane)) & ((1L << bits) - 1)
              val value = if (opcode == ExtendedInstruction.GlslStd450.UnpackHalf2x16)
                IEEE754.binary16ToFloat(Binary16.low(word))
              else if (signed) Math.max(
                -1.0,
                ((word ^ (1L << (bits - 1))) - (1L << (bits - 1))).toDouble / ((1 << (bits - 1)) - 1)
              ).toFloat
              else (word.toDouble / ((1 << bits) - 1)).toFloat
              IEEE754.floatToBinary32(value).raw.toLong
            })
          } else None
        result match {
          case Some(bits) => values.update(id(destination), RuntimeValue.Bits(id(resultType), bits)); Right(())
          case None       => Left(Fault.InvalidTask(s"GLSL packing opcode $opcode has invalid operand or result types"))
        }
      case Some(_) => Left(Fault.InvalidTask("GLSL packing operand is a pointer"))
      case None    => Left(undefined(sources.head))
    }

  private def glslDecompose(
      program: Program,
      values: InvocationValues,
      memory: MemoryState,
      resultType: Operand,
      destination: Operand,
      opcode: Int,
      sources: Vec[Operand]
  ): Either[Fault, Unit] = {
    val pointerForm = opcode == ExtendedInstruction.GlslStd450.Modf || opcode == ExtendedInstruction.GlslStd450.Frexp
    val modf = opcode == ExtendedInstruction.GlslStd450.Modf || opcode == ExtendedInstruction.GlslStd450.ModfStruct
    if (sources.size != (if (pointerForm) 2 else 1))
      Left(Fault.InvalidTask("GLSL decomposition has an incorrect operand count"))
    else values.get(id(sources.head)) match {
      case Some(RuntimeValue.Bits(sourceType, lanes)) =>
        val output = if (pointerForm) values.get(id(sources(1))) match {
          case Some(RuntimeValue.Address(_, pointer)) if id(resultType) == sourceType =>
            Some((pointer.pointee, Some(pointer)))
          case _ => None
        }
        else program.layout.runtimeType(id(resultType)) match {
          case Some(RuntimeType.Structure(members)) if members.size == 2 && members(0) == sourceType =>
            Some((members(1), None))
          case _ => None
        }
        (floatingShape(program, sourceType), output) match {
          case (Some((width, count)), Some((outputType, pointer)))
              if lanes.length == count &&
                (if (modf) outputType == sourceType
                 else integerShape(program, outputType).exists((_, n) => n == count)) =>
            val fractional = Array.ofDim[Long](count)
            val integral   = Array.ofDim[Long](count)
            for (lane <- 0 until count) {
              val value = floatingToDouble(width, lanes(lane))
              if (modf) {
                val integer  = if (value < 0.0) Math.ceil(value) else Math.floor(value)
                val fraction =
                  if (value.isInfinity) Math.copySign(0.0, value) else Math.copySign(value - integer, value)
                fractional(lane) = doubleToFloating(width, fraction)
                integral(lane) = doubleToFloating(width, integer)
              } else {
                val exponent = if (value == 0.0 || !value.isFinite) 0
                else {
                  val initial = Math.getExponent(value)
                  (if (initial == -1023) Math.getExponent(Math.scalb(value, 54)) - 54 else initial) + 1
                }
                fractional(lane) = doubleToFloating(width, Math.scalb(value, -exponent))
                integral(lane) = exponent.toLong & mask(integerShape(program, outputType).get._1)
              }
            }
            val written = pointer match {
              case Some(address) =>
                program.encode(RuntimeValue.Bits(outputType, integral)).flatMap(memory.write(address, _))
              case None => Right(())
            }
            written.map { _ =>
              values.update(
                id(destination),
                RuntimeValue.Bits(id(resultType), if (pointerForm) fractional else fractional ++ integral)
              )
            }
          case _ => Left(Fault.InvalidTask("GLSL decomposition has incompatible operand or result types"))
        }
      case None => Left(undefined(sources.head))
      case _    => Left(Fault.InvalidTask("GLSL decomposition requires floating-point input"))
    }
  }

  private def glslGeometric(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      opcode: Int,
      sources: Vec[Operand]
  ): Either[Fault, Unit] = {
    val arity = if (opcode == ExtendedInstruction.GlslStd450.Reflect) 2 else 3
    floatingShape(program, id(resultType)) match {
      case Some((width, count)) if sources.size == arity =>
        sources.zipWithIndex.traverse { (source, index) =>
          val expected = if (opcode == ExtendedInstruction.GlslStd450.Refract && index == 2) 1 else count
          values.get(id(source)) match {
            case Some(RuntimeValue.Bits(t, lanes))
                if floatingShape(program, t).contains(width -> expected) && lanes.length == expected =>
              Right(lanes.map(floatingToDouble(width, _)))
            case None => Left(undefined(source))
            case _    => Left(Fault.InvalidTask("GLSL geometric operand types do not match"))
          }
        }.map { operands =>
          val dot = if (opcode == ExtendedInstruction.GlslStd450.FaceForward)
            operands(2).zip(operands(1)).map(_ * _).sum
          else operands(0).zip(operands(1)).map(_ * _).sum
          val result = Array.tabulate(count) { lane =>
            val value = opcode match {
              case ExtendedInstruction.GlslStd450.FaceForward =>
                if (dot < 0.0) operands(0)(lane) else -operands(0)(lane)
              case ExtendedInstruction.GlslStd450.Reflect => operands(0)(lane) - 2.0 * dot * operands(1)(lane)
              case _                                      =>
                val eta = operands(2)(0)
                val k   = 1.0 - eta * eta * (1.0 - dot * dot)
                if (k < 0.0) 0.0 else eta * operands(0)(lane) - (eta * dot + Math.sqrt(k)) * operands(1)(lane)
            }
            doubleToFloating(width, value)
          }
          values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
        }
      case _ => Left(Fault.InvalidTask("GLSL geometric result or operand count is invalid"))
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
              if (opcode == ExtendedInstruction.GlslStd450.FAbs) resultWidth.raw match {
                case 16 => raw & 0x7fffL
                case 32 => raw & 0x7fffffffL
                case 64 => raw & 0x7fffffffffffffffL
              }
              else {
                val value    = floatingToDouble(sourceWidth, raw)
                val computed = opcode match {
                  case ExtendedInstruction.GlslStd450.Round | ExtendedInstruction.GlslStd450.RoundEven =>
                    Math.rint(value)
                  case ExtendedInstruction.GlslStd450.Trunc   => if (value < 0) Math.ceil(value) else Math.floor(value)
                  case ExtendedInstruction.GlslStd450.FSign   => if (value < 0) -1.0 else if (value > 0) 1.0 else 0.0
                  case ExtendedInstruction.GlslStd450.Floor   => Math.floor(value)
                  case ExtendedInstruction.GlslStd450.Ceil    => Math.ceil(value)
                  case ExtendedInstruction.GlslStd450.Fract   => value - Math.floor(value)
                  case ExtendedInstruction.GlslStd450.Radians => value * (Math.PI / 180.0)
                  case ExtendedInstruction.GlslStd450.Degrees => value * (180.0 / Math.PI)
                  case ExtendedInstruction.GlslStd450.Sin     => Math.sin(value)
                  case ExtendedInstruction.GlslStd450.Cos     => Math.cos(value)
                  case ExtendedInstruction.GlslStd450.Tan     => Math.tan(value)
                  case ExtendedInstruction.GlslStd450.Asin    => Math.asin(value)
                  case ExtendedInstruction.GlslStd450.Acos    => Math.acos(value)
                  case ExtendedInstruction.GlslStd450.Atan    => Math.atan(value)
                  case ExtendedInstruction.GlslStd450.Sinh | ExtendedInstruction.GlslStd450.Cosh =>
                    // GLSL defines these through exp at the operand precision.
                    // Preserve intermediate overflow, including values where exp(x)/2 would otherwise fit.
                    val positive = floatingToDouble(sourceWidth, doubleToFloating(sourceWidth, Math.exp(value)))
                    val negative = floatingToDouble(sourceWidth, doubleToFloating(sourceWidth, Math.exp(-value)))
                    val combined =
                      if (opcode == ExtendedInstruction.GlslStd450.Sinh) positive - negative else positive + negative
                    floatingToDouble(sourceWidth, doubleToFloating(sourceWidth, combined)) / 2.0
                  case ExtendedInstruction.GlslStd450.Tanh  => Math.tanh(value)
                  case ExtendedInstruction.GlslStd450.Asinh =>
                    val a = Math.abs(value)
                    Math.copySign(
                      if (a > 1e150) Math.log(a) + Math.log(2.0)
                      else Math.log1p(a + a * a / (1.0 + Math.sqrt(1.0 + a * a))),
                      value
                    )
                  case ExtendedInstruction.GlslStd450.Acosh => if (value > 1e150) Math.log(value) + Math.log(2.0)
                    else Math.log(value + Math.sqrt(value - 1.0) * Math.sqrt(value + 1.0))
                  case ExtendedInstruction.GlslStd450.Atanh       => 0.5 * Math.log1p(2.0 * value / (1.0 - value))
                  case ExtendedInstruction.GlslStd450.Exp         => Math.exp(value)
                  case ExtendedInstruction.GlslStd450.Log         => Math.log(value)
                  case ExtendedInstruction.GlslStd450.Exp2        => Math.pow(2.0, value)
                  case ExtendedInstruction.GlslStd450.Log2        => Math.log(value) / Math.log(2.0)
                  case ExtendedInstruction.GlslStd450.Sqrt        => Math.sqrt(value)
                  case ExtendedInstruction.GlslStd450.InverseSqrt => 1.0 / Math.sqrt(value)
                }
                resultWidth.raw match {
                  case 16 => IEEE754.floatToBinary16(computed.toFloat).toLong
                  case 32 => IEEE754.floatToBinary32(computed.toFloat).toLong
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

  private def floatingToDouble(width: OneOf[(16, 32, 64)], bits: Long): Double = IEEE754.toDouble(bits, width)

  private def doubleToFloating(width: OneOf[(16, 32, 64)], value: Double): Long = IEEE754.fromDouble(value, width)

  private def openCLSinPi(value: Double): Double =
    if (!value.isFinite) Double.NaN
    else if (value == Math.rint(value)) Math.copySign(0.0, value)
    else {
      val reduced   = Math.IEEEremainder(value, 2.0)
      val x         = Math.abs(reduced)
      val magnitude = if (x <= 0.25) Math.sin(Math.PI * x)
      else if (x <= 0.75) Math.cos(Math.PI * (0.5 - x))
      else Math.sin(Math.PI * (1.0 - x))
      Math.copySign(magnitude, reduced)
    }

  private def openCLCosPi(value: Double): Double =
    if (!value.isFinite) Double.NaN
    else {
      val x = Math.abs(Math.IEEEremainder(value, 2.0))
      if (x == 0.5) 0.0
      else if (x <= 0.25) Math.cos(Math.PI * x)
      else if (x <= 0.75) Math.sin(Math.PI * (0.5 - x))
      else -Math.cos(Math.PI * (1.0 - x))
    }

  private def openCLTanPi(value: Double): Double =
    if (!value.isFinite) Double.NaN
    else {
      val period  = Math.IEEEremainder(value, 2.0)
      val reduced = Math.IEEEremainder(value, 1.0)
      val x       = Math.abs(reduced)
      if (x == 0.0) Math.copySign(0.0, if (Math.abs(period) == 1.0) -value else value)
      else if (x == 0.5) Math.copySign(Double.PositiveInfinity, period)
      else Math.copySign(if (x <= 0.25) Math.tan(Math.PI * x) else 1.0 / Math.tan(Math.PI * (0.5 - x)), reduced)
    }

  private def openCLPowr(base: Double, exponent: Double): Double =
    if (
      base.isNaN || exponent.isNaN || base < 0.0 || (base == 1.0 && exponent.isInfinity) ||
      ((base == 0.0 || base == Double.PositiveInfinity) && exponent == 0.0)
    ) Double.NaN
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
      name: String,
      instructionSet: String = "OpenCL.std"
  )(operation: Array[Double] => Double): Either[Fault, Unit] =
    floatingShape(program, id(resultType)) match {
      case None =>
        Left(Fault.InvalidTask(s"$instructionSet $name result is not floating-point"))
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
                      s"$instructionSet $name operand ${sourceIndex + 1} does not match result shape ($width,$count)"
                    )
                  )
              }
            case Some(_) =>
              return Left(Fault.InvalidTask(
                s"$instructionSet $name operand ${sourceIndex + 1} is a pointer"
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
      case ExtendedInstruction.OpenClStd.Fclamp     => "clamp"      -> 3
      case ExtendedInstruction.OpenClStd.Degrees    => "degrees"    -> 1
      case ExtendedInstruction.OpenClStd.FmaxCommon => "fmax"       -> 2
      case ExtendedInstruction.OpenClStd.FminCommon => "fmin"       -> 2
      case ExtendedInstruction.OpenClStd.Mix        => "mix"        -> 3
      case ExtendedInstruction.OpenClStd.Radians    => "radians"    -> 1
      case ExtendedInstruction.OpenClStd.Step       => "step"       -> 2
      case ExtendedInstruction.OpenClStd.Smoothstep => "smoothstep" -> 3
      case _                                        => "sign"       -> 1
    }
    if (sources.size != arity)
      Left(Fault.InvalidTask(
        s"OpenCL.std $name expects $arity operands, received ${sources.size}"
      ))
    else
      openCLFloatingElementwise(program, values, resultType, destination, sources, name) { operands =>
        opcode match {
          case ExtendedInstruction.OpenClStd.Fclamp =>
            floatingMinimum(floatingMaximum(operands(0), operands(1)), operands(2))
          case ExtendedInstruction.OpenClStd.Degrees    => operands(0) * (180.0 / Math.PI)
          case ExtendedInstruction.OpenClStd.FmaxCommon => floatingMaximum(operands(0), operands(1))
          case ExtendedInstruction.OpenClStd.FminCommon => floatingMinimum(operands(0), operands(1))
          case ExtendedInstruction.OpenClStd.Mix        => operands(0) + (operands(1) - operands(0)) * operands(2)
          case ExtendedInstruction.OpenClStd.Radians    => operands(0) * (Math.PI / 180.0)
          case ExtendedInstruction.OpenClStd.Step       => if (operands(1) < operands(0)) 0.0 else 1.0
          case ExtendedInstruction.OpenClStd.Smoothstep =>
            val width                        = floatingShape(program, id(resultType)).get._1
            def round(value: Double): Double = floatingToDouble(width, doubleToFloating(width, value))
            val ratio = round(round(operands(2) - operands(0)) / round(operands(1) - operands(0)))
            val t     = floatingMinimum(floatingMaximum(ratio, 0.0), 1.0)
            round(t * t) * round(3.0 - round(2.0 * t))
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
      case ExtendedInstruction.OpenClStd.Cross        => "cross"
      case ExtendedInstruction.OpenClStd.Distance     => "distance"
      case ExtendedInstruction.OpenClStd.Length       => "length"
      case ExtendedInstruction.OpenClStd.Normalize    => "normalize"
      case ExtendedInstruction.OpenClStd.FastDistance => "fast_distance"
      case ExtendedInstruction.OpenClStd.FastLength   => "fast_length"
      case _                                          => "fast_normalize"
    }
    val arity =
      if (
        opcode == ExtendedInstruction.OpenClStd.Cross || opcode == ExtendedInstruction.OpenClStd.Distance || opcode == ExtendedInstruction.OpenClStd.FastDistance
      ) 2
      else 1
    if (sources.size != arity)
      return Left(
        Fault.InvalidTask(
          s"OpenCL.std $name expects $arity operands, received ${sources.size}"
        )
      )
    val operands = Array.ofDim[Array[Long]](arity)
    var width    = Option.empty[OneOf[(16, 32, 64)]]
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
      case ExtendedInstruction.OpenClStd.Cross =>
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
      case ExtendedInstruction.OpenClStd.Distance | ExtendedInstruction.OpenClStd.FastDistance =>
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
      case ExtendedInstruction.OpenClStd.Length | ExtendedInstruction.OpenClStd.FastLength =>
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
            val sign   = 1L << (conditionWidth.raw - 1)
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
            width.raw match {
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
            val negative = width.raw match {
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

  // Pointer-output builtins use the same typed write path for every address space.
  private def openCLPointerMath(
      program: Program,
      values: InvocationValues,
      memory: MemoryState,
      resultType: Operand,
      destination: Operand,
      source: Operand,
      output: Operand,
      opcode: Int
  ): Either[Fault, Unit] =
    (values.get(id(source)), address(values, output)) match {
      case (Some(RuntimeValue.Bits(sourceType, sourceLanes)), Right(pointer)) =>
        (floatingShape(program, sourceType), floatingShape(program, id(resultType))) match {
          case (Some((width, count)), Some(resultShape))
              if resultShape == (width, count) && sourceLanes.length == count =>
            val integerOutput =
              opcode == ExtendedInstruction.OpenClStd.Frexp || opcode == ExtendedInstruction.OpenClStd.LgammaR
            val validPointer = if (integerOutput)
              integerShape(program, pointer.pointee).contains((32, count))
            else floatingShape(program, pointer.pointee).contains((width, count))
            if (!validPointer) Left(Fault.InvalidTask("OpenCL.std pointer math output type does not match input"))
            else {
              val paired = sourceLanes.map { raw =>
                val x                = floatingToDouble(width, raw)
                val (result, stored) = opcode match {
                  case ExtendedInstruction.OpenClStd.Sincos  => (Math.sin(x), doubleToFloating(width, Math.cos(x)))
                  case ExtendedInstruction.OpenClStd.LgammaR =>
                    (OpenCLMath.lgamma(x), OpenCLMath.gammaSign(x).toLong & 0xffffffffL)
                  case ExtendedInstruction.OpenClStd.Frexp =>
                    val exponent = if (x == 0.0 || !x.isFinite) 0 else openCLExponent(x) + 1
                    (Math.scalb(x, -exponent), exponent.toLong & 0xffffffffL)
                  case ExtendedInstruction.OpenClStd.Modf =>
                    val integral = if (x < 0.0) Math.ceil(x) else Math.floor(x)
                    val fraction = if (x.isInfinite) Math.copySign(0.0, x) else Math.copySign(x - integral, x)
                    (fraction, doubleToFloating(width, integral))
                  case _ =>
                    val integral = Math.floor(x)
                    val maximum  = width.raw match {
                      case 16 => 1.0 - Math.scalb(1.0, -11)
                      case 32 => Math.nextDown(1.0f).toDouble
                      case 64 => Math.nextDown(1.0)
                    }
                    val fraction = if (x.isNaN) x
                    else if (x == 0.0 || x.isInfinite) Math.copySign(0.0, x)
                    else Math.min(x - integral, maximum)
                    (fraction, doubleToFloating(width, integral))
                }
                (doubleToFloating(width, result), stored)
              }
              val bytes = Array.ofDim[Byte](program.layout.typeSize(pointer.pointee))
              val _     = program.encodeBits(pointer.pointee, paired.map(_._2), 0, bytes, 0)
              memory.write(pointer, bytes).map { _ =>
                values.update(id(destination), RuntimeValue.Bits(id(resultType), paired.map(_._1)))
              }
            }
          case _ =>
            Left(Fault.InvalidTask("OpenCL.std pointer math requires matching floating-point scalar/vector types"))
        }
      case (None, _)        => Left(undefined(source))
      case (_, Left(error)) => Left(error)
      case _                => Left(Fault.InvalidTask("OpenCL.std pointer math source is not floating-point"))
    }

  private def openCLIlogb(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      source: Operand
  ): Either[Fault, Unit] = values.get(id(source)) match {
    case Some(RuntimeValue.Bits(sourceType, lanes)) =>
      (floatingShape(program, sourceType), integerShape(program, id(resultType))) match {
        case (Some((width, count)), Some((resultWidth, resultCount)))
            if resultWidth.raw == 32 && count == resultCount =>
          val result = lanes.map { raw =>
            val x        = floatingToDouble(width, raw)
            val exponent = if (x == 0.0) Int.MinValue else if (!x.isFinite) Int.MaxValue else openCLExponent(x)
            exponent.toLong & 0xffffffffL
          }
          values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
          Right(())
        case _ => Left(Fault.InvalidTask("OpenCL.std ilogb requires floating input and matching i32 result lanes"))
      }
    case None => Left(undefined(source))
    case _    => Left(Fault.InvalidTask("OpenCL.std ilogb input is not floating-point"))
  }

  private def openCLRemquo(
      program: Program,
      values: InvocationValues,
      memory: MemoryState,
      resultType: Operand,
      destination: Operand,
      sources: Vec[Operand]
  ): Either[Fault, Unit] =
    (values.get(id(sources(0))), values.get(id(sources(1))), address(values, sources(2))) match {
      case (Some(RuntimeValue.Bits(leftType, left)), Some(RuntimeValue.Bits(rightType, right)), Right(pointer)) =>
        floatingShape(program, id(resultType)) match {
          case Some(shape @ (width, count))
              if floatingShape(program, leftType).contains(shape) &&
                floatingShape(program, rightType).contains(shape) &&
                integerShape(program, pointer.pointee).contains((32, count)) =>
            val remainders = Array.tabulate(count)(lane =>
              doubleToFloating(
                width,
                Math.IEEEremainder(floatingToDouble(width, left(lane)), floatingToDouble(width, right(lane)))
              )
            )
            val quotients = Array.tabulate(count) { lane =>
              val x = floatingToDouble(width, left(lane))
              val y = floatingToDouble(width, right(lane))
              if (!x.isFinite || y.isNaN || y == 0.0 || y.isInfinite) 0L
              else {
                // Exact binary inputs avoid quotient rounding errors and overflow in x/y.
                val quotient = java.math.BigDecimal(x).divide(
                  java.math.BigDecimal(y),
                  0,
                  java.math.RoundingMode.HALF_EVEN
                ).toBigInteger
                (quotient.abs.intValue & 0x7f).toLong * quotient.signum
              }
            }
            val bytes = Array.ofDim[Byte](program.layout.typeSize(pointer.pointee))
            val _     = program.encodeBits(pointer.pointee, quotients, 0, bytes, 0)
            memory.write(pointer, bytes).map { _ =>
              values.update(id(destination), RuntimeValue.Bits(id(resultType), remainders))
            }
          case _ =>
            Left(Fault.InvalidTask("OpenCL.std remquo requires matching floating operands and i32 pointer lanes"))
        }
      case (None, _, _)        => Left(undefined(sources(0)))
      case (_, None, _)        => Left(undefined(sources(1)))
      case (_, _, Left(error)) => Left(error)
      case _                   => Left(Fault.InvalidTask("OpenCL.std remquo operands are not floating-point"))
    }

  private def openCLExponent(value: Double): Int = {
    val exponent = Math.getExponent(value)
    if (exponent == java.lang.Double.MIN_EXPONENT - 1) Math.getExponent(Math.scalb(value, 54)) - 54
    else exponent
  }

  private def openCLMagnitude(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      left: Operand,
      right: Operand,
      maximum: Boolean
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
              if (Math.abs(leftValue) > Math.abs(rightValue)) { if (maximum) leftValue else rightValue }
              else if (Math.abs(rightValue) > Math.abs(leftValue)) { if (maximum) rightValue else leftValue }
              else if (maximum) floatingMaximum(leftValue, rightValue)
              else floatingMinimum(leftValue, rightValue)
            doubleToFloating(width, selected)
          }
          values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
          Right(())
        case _ =>
          Left(Fault.InvalidTask(
            "OpenCL.std magnitude selection requires matching floating-point types"
          ))
      }
    case (None, _) => Left(undefined(left))
    case (_, None) => Left(undefined(right))
    case _         => Left(Fault.InvalidTask("OpenCL.std magnitude selection operands are not floating-point"))
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
      exponent: Operand,
      glsl: Boolean = false
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
            if width == resultWidth && count == resultCount && (glsl || exponentWidth.raw == 32) && exponentCount == count &&
              sourceLanes.length == count && exponentLanes.length == count =>
          val result = Array.tabulate(count) { index =>
            val exponentValue = signExtend(exponentLanes(index), exponentWidth)
            val scale         = Math.max(Int.MinValue.toLong, Math.min(Int.MaxValue.toLong, exponentValue)).toInt
            doubleToFloating(width, Math.scalb(floatingToDouble(width, sourceLanes(index)), scale))
          }
          values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
          Right(())
        case _ =>
          Left(Fault.InvalidTask(
            if (glsl) "GLSL Ldexp requires matching floating values and integer exponents"
            else "OpenCL.std ldexp requires matching floating values and i32 exponents"
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
      val width      = program.layout.scalarWidth(id(resultType))
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
          val width = program.layout.scalarWidth(id(resultType))
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
  )(operation: (Long, Long, Int where Between[1, 64]) => Long): Either[Fault, Unit] =
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
      invocation: InvocationBuiltins
  ): Either[Fault, RuntimeValue] = {
    val lanes: Either[Fault, Array[Long]] = builtin match {
      case BuiltIn.NumWorkgroups                                 => Right(invocation.numWorkgroupsVector)
      case BuiltIn.WorkgroupSize                                 => Right(invocation.workgroupSizeVector)
      case BuiltIn.WorkgroupId if program.vulkanEntries.nonEmpty =>
        Right(Array(
          invocation.groupX + invocation.globalOffsetX / invocation.enqueuedWorkgroupSizeX,
          invocation.groupY + invocation.globalOffsetY / invocation.enqueuedWorkgroupSizeY,
          invocation.groupZ + invocation.globalOffsetZ / invocation.enqueuedWorkgroupSizeZ
        ))
      case BuiltIn.WorkgroupId          => Right(invocation.groupVector)
      case BuiltIn.LocalInvocationId    => Right(invocation.localVector)
      case BuiltIn.GlobalInvocationId   => Right(invocation.globalVector)
      case BuiltIn.LocalInvocationIndex => Right(Array(
          (invocation.localZ * invocation.workgroupSizeY + invocation.localY) * invocation.workgroupSizeX + invocation.localX
        ))
      case BuiltIn.WorkDim               => Right(Array(invocation.workDim.toLong))
      case BuiltIn.GlobalSize            => Right(invocation.globalSizeVector)
      case BuiltIn.EnqueuedWorkgroupSize => Right(invocation.enqueuedWorkgroupSizeVector)
      case BuiltIn.GlobalOffset          => Right(invocation.globalOffsetVector)
      case BuiltIn.GlobalLinearId        => Right(
          Array(
            (invocation.globalX - invocation.globalOffsetX) + invocation.globalSizeX *
              ((invocation.globalY - invocation.globalOffsetY) +
                invocation.globalSizeY * (invocation.globalZ - invocation.globalOffsetZ))
          )
        )
      case LegacyWorkgroupLinearId => Right(
          Array(
            invocation.groupX + invocation.numWorkgroupsX *
              (invocation.groupY + invocation.numWorkgroupsY * invocation.groupZ)
          )
        )
      case maskKind @ (BuiltIn.SubgroupEqMask | BuiltIn.SubgroupGeMask | BuiltIn.SubgroupGtMask | BuiltIn.SubgroupLeMask | BuiltIn.SubgroupLtMask) =>
        Right(Array.tabulate(4) { word =>
          (0 until 32).foldLeft(0L) { (bits, bit) =>
            val lane     = word * 32 + bit
            val selected = maskKind match {
              case BuiltIn.SubgroupEqMask => lane == invocation.subgroupLocal
              case BuiltIn.SubgroupGeMask => lane >= invocation.subgroupLocal
              case BuiltIn.SubgroupGtMask => lane > invocation.subgroupLocal
              case BuiltIn.SubgroupLeMask => lane <= invocation.subgroupLocal
              case _                      => lane < invocation.subgroupLocal
            }
            if (lane < invocation.subgroupSize && selected) bits | (1L << bit) else bits
          }
        })
      // The Vulkan runtime exposes one physical device in each device group.
      case BuiltIn.DeviceIndex if program.vulkanEntries.nonEmpty => Right(Array(0L))
      case BuiltIn.SubgroupSize                                  => Right(Array(invocation.subgroupSize.toLong))
      case BuiltIn.SubgroupMaxSize                               => Right(Array(invocation.maxSubgroupSize.toLong))
      case BuiltIn.NumSubgroups                                  => Right(Array(invocation.numSubgroups))
      case BuiltIn.NumEnqueuedSubgroups                          => Right(Array(invocation.enqueuedSubgroups))
      case BuiltIn.SubgroupId                                    => Right(Array(invocation.subgroupIndex.toLong))
      case BuiltIn.SubgroupLocalInvocationId                     => Right(Array(invocation.subgroupLocal.toLong))
      case value => Left(Fault.Unsupported(s"SPIR-V BuiltIn $value is not implemented"))
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

  private def mappedMatrixStorage(program: Program, pointer: Pointer): Boolean =
    pointer.matrixLayout.nonEmpty || program.layout.hasMatrixStorage(pointer.pointee) ||
      (program.vulkanEntries.nonEmpty && program.layout.runtimeType(pointer.pointee).exists {
        // Composite memory accesses operate on members, preserving padding and
        // independently applying descriptor bounds to each scalar element.
        case RuntimeType.Structure(_) | RuntimeType.Array(_, _) => true
        case _                                                  => false
      })

  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.return"))
  private def loadMatrixStorage(
      program: Program,
      memory: MemoryState,
      pointer: Pointer
  ): Memory.ReadResult[RuntimeValue.Bits] = {
    val leaves = program.layout.matrixStorageLeaves(pointer.pointee, pointer.matrixLayout)
    val lanes  = Array.ofDim[Long](leaves.size)
    var index  = 0
    while (index < leaves.size) {
      val (offset, scalar) = leaves(index)
      val size             = program.layout.scalarMemoryWidth(scalar) match {
        case Left(error)  => return Left(error)
        case Right(value) => value
      }
      memory.readScalar(
        pointer.copy(offset = Math.addExact(pointer.offset, offset), pointee = scalar, matrixLayout = None),
        size
      ) match {
        case Left(error)  => return Left(error)
        case Right(value) => lanes(index) = value
      }
      index += 1
    }
    Right(RuntimeValue.Bits(pointer.pointee, lanes))
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.return"))
  private def storeMatrixStorage(
      program: Program,
      memory: MemoryState,
      pointer: Pointer,
      value: RuntimeValue.Bits
  ): Either[Fault, Unit] = {
    val leaves = program.layout.matrixStorageLeaves(pointer.pointee, pointer.matrixLayout)
    if (leaves.size != value.lanes.length) return Left(Fault.InvalidTask("matrix storage scalar count does not match"))
    var index = 0
    while (index < leaves.size) {
      val (offset, scalar) = leaves(index)
      val size             = program.layout.scalarMemoryWidth(scalar) match {
        case Left(error)  => return Left(error)
        case Right(value) => value
      }
      memory.writeScalar(
        pointer.copy(offset = Math.addExact(pointer.offset, offset), pointee = scalar, matrixLayout = None),
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
      case (Vec(pointerId), Vec(sizeWord)) => values.get(pointerId.raw) match {
          case Some(RuntimeValue.Address(pointerType, pointer)) => program.layout.runtimeType(pointerType) match {
              case Some(RuntimeType.Pointer(_, pointee)) if pointee == pointer.pointee =>
                val size = sizeWord.toUnsigned.toLong
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
                  s"$opcode target %$pointerId does not have pointer type"
                ))
              case None =>
                Left(Fault.InvalidTask(
                  s"$opcode target %$pointerId has undefined pointer type"
                ))
            }
          case Some(_) => Left(Fault.InvalidTask(
              s"$opcode target %$pointerId is not a pointer"
            ))
          case None => Left(
              Fault.InvalidTask(s"$opcode target %$pointerId is undefined")
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
            sourceWidth.raw * sourceCount == resultWidth.raw * resultCount =>
        val result     = Array.ofDim[Long](resultCount)
        var resultLane = 0
        while (resultLane < resultCount) {
          var bits = 0L
          var bit  = 0
          while (bit < resultWidth.raw) {
            val sourceBit    = resultLane * resultWidth.raw + bit
            val sourceLane   = sourceBit / sourceWidth.raw
            val sourceOffset = sourceBit % sourceWidth.raw
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

  private def numericShape(program: Program, typeId: Int): Option[(Int where Between[1, 64], Int)] =
    integerShape(program, typeId)
      .orElse(floatingShape(program, typeId).map((width, count) => width.bitWidth -> count))
      .orElse {
        program.layout.runtimeType(typeId) match {
          case Some(RuntimeType.Bool) => Some((8, 1))
          case Some(RuntimeType.VectorType(component, count))
              if program.layout.runtimeType(component).contains(RuntimeType.Bool) => Some((8, count))
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
        case Vec(value, parent) => if (parent.raw == predecessor) selected = value.raw
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
            val unsigned = sourceWidth.raw match {
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
            val (signMask, exponentMask, fractionMask) = width.raw match {
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
          val signMask = width.raw match {
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
            width.raw match {
              case 16 =>
                val a = IEEE754.binary16ToFloat(Binary16.low(leftLanes(index)))
                val b = IEEE754.binary16ToFloat(Binary16.low(rightLanes(index)))
                IEEE754.floatToBinary16(
                  operation match {
                    case FloatingOperation.Add       => a + b
                    case FloatingOperation.Subtract  => a - b
                    case FloatingOperation.Multiply  => a * b
                    case FloatingOperation.Divide    => a / b
                    case FloatingOperation.Remainder => a % b
                    case FloatingOperation.Modulo    =>
                      val remainder = a % b
                      if (remainder != 0 && (remainder < 0) != (b < 0)) remainder + b else remainder
                    case FloatingOperation.Minimum => floatingMinimum(a, b)
                    case FloatingOperation.Maximum => floatingMaximum(a, b)
                  }
                ).toLong
              case 32 =>
                val a = IEEE754.binary32ToFloat(Binary32.low(leftLanes(index)))
                val b = IEEE754.binary32ToFloat(Binary32.low(rightLanes(index)))
                IEEE754.floatToBinary32(
                  operation match {
                    case FloatingOperation.Add       => a + b
                    case FloatingOperation.Subtract  => a - b
                    case FloatingOperation.Multiply  => a * b
                    case FloatingOperation.Divide    => a / b
                    case FloatingOperation.Remainder => a % b
                    case FloatingOperation.Modulo    =>
                      val remainder = a % b
                      if (remainder != 0 && (remainder < 0) != (b < 0)) remainder + b else remainder
                    case FloatingOperation.Minimum => floatingMinimum(a, b)
                    case FloatingOperation.Maximum => floatingMaximum(a, b)
                  }
                ).toLong
              case 64 =>
                val a = IEEE754.binary64ToDouble(leftLanes(index))
                val b = IEEE754.binary64ToDouble(rightLanes(index))
                IEEE754.doubleToBinary64(
                  operation match {
                    case FloatingOperation.Add       => a + b
                    case FloatingOperation.Subtract  => a - b
                    case FloatingOperation.Multiply  => a * b
                    case FloatingOperation.Divide    => a / b
                    case FloatingOperation.Remainder => a % b
                    case FloatingOperation.Modulo    =>
                      val remainder = a % b
                      if (remainder != 0 && (remainder < 0) != (b < 0)) remainder + b else remainder
                    case FloatingOperation.Minimum => floatingMinimum(a, b)
                    case FloatingOperation.Maximum => floatingMaximum(a, b)
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
          val rounding  = program.ir.roundingModes.getOrElse(id(destination), FPRoundingMode.RTE)
          val converted = lanes.map { raw =>
            val value = floatingToDouble(sourceWidth, raw)
            val bits  = doubleToFloating(resultWidth, value)
            // The nearest encoding differs from a directed result by at most
            // one representable value, including overflow and subnormal edges.
            // Equal infinities, signed zeros and NaNs need no adjustment.
            if (rounding == FPRoundingMode.RTE || resultWidth.raw >= sourceWidth.raw) bits
            else {
              val nearest = floatingToDouble(resultWidth, bits)
              rounding match {
                case FPRoundingMode.RTZ if math.abs(nearest) > math.abs(value) => bits - 1L // RTZ
                case FPRoundingMode.RTP if nearest < value => bits + (if (value < 0.0) -1L else 1L) // RTP
                case FPRoundingMode.RTN if nearest > value => bits + (if (value > 0.0) -1L else 1L) // RTN
                case _                                     => bits
              }
            }
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
  private def roundedIntegral(value: Double, mode: Option[Int]): Double = mode match {
    case Some(FPRoundingMode.RTE) => math.rint(value)
    case Some(FPRoundingMode.RTP) => math.ceil(value)
    case Some(FPRoundingMode.RTN) => math.floor(value)
    case _                        => if (value < 0.0) math.ceil(value) else math.floor(value)
  }

  private def roundedToLong(value: Double, mode: Option[Int]): Long = roundedIntegral(value, mode).toLong

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
              if (program.saturatedConversions.contains(id(destination)) || program.vulkanEntries.nonEmpty) {
                val minimum = if (resultWidth.raw == 64) Long.MinValue else -(1L << (resultWidth.raw - 1))
                val maximum = if (resultWidth.raw == 64) Long.MaxValue else (1L << (resultWidth.raw - 1)) - 1L
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
            val floating = roundedIntegral(floatingToDouble(sourceWidth, lanes(index)), mode)
            val result   = if (saturate && (floating.isNaN || floating <= 0.0)) Right(0L)
            else if (saturate && floating >= Math.scalb(1.0, resultWidth.raw)) Right(mask(resultWidth))
            else floatingToUnsigned(IEEE754.doubleToBinary64(floating), 64, resultWidth)
            result match {
              case Left(_) if program.vulkanEntries.nonEmpty =>
                converted(index) = if (floating.isNaN || floating <= 0.0) 0L else mask(resultWidth)
              case Left(detail) =>
                return Left(Fault.InvalidTask(
                  s"OpConvertFToU source lane $index cannot be represented as an unsigned $resultWidth-bit integer: $detail"
                ))
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
      sourceWidth: OneOf[(16, 32, 64)],
      resultWidth: Int where Between[1, 64]
  ): Either[String, Long] = {
    val (exponentBits, fractionBits, bias) = sourceWidth.raw match {
      case 16 => (5, 10, 15)
      case 32 => (8, 23, 127)
      case 64 => (11, 52, 1023)
    }
    val bits            = raw & mask(sourceWidth.bitWidth)
    val fractionMask    = (1L << fractionBits) - 1L
    val exponentMask    = (1L << exponentBits) - 1L
    val fraction        = bits & fractionMask
    val encodedExponent = (bits >>> fractionBits) & exponentMask
    val negative        = ((bits >>> (sourceWidth.raw - 1)) & 1L) != 0L
    val magnitudeIsZero = encodedExponent == 0L && fraction == 0L

    if (encodedExponent == exponentMask) Left("NaN and infinity are not integers")
    else if (encodedExponent < bias) Right(0L) // Truncation of either sign below one is representable.
    else if (negative && !magnitudeIsZero) Left("negative values are outside the unsigned range")
    else {
      val exponent    = encodedExponent.toInt - bias
      val significand = BigInt((1L << fractionBits) | fraction)
      val integer     =
        if (exponent >= fractionBits) significand << (exponent - fractionBits)
        else significand >> (fractionBits - exponent)
      if (integer.bitLength > resultWidth.raw) Left("value exceeds the result width")
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
        val leftType                      = values.scalarType(leftId)
        val rightType                     = values.scalarType(rightId)
        def matches(typeId: Int): Boolean =
          typeId == resultId || integerShape(program, typeId).contains(width -> count)
        if (count == 1 && matches(leftType) && matches(rightType)) {
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
                if matches(leftRuntimeType) && matches(rightRuntimeType) &&
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
      case Some((width, count))
          if count == 1 &&
            (values.scalarType(sourceId) == resultId || integerShape(
              program,
              values.scalarType(sourceId)
            ).contains(width -> count)) =>
        values.updateScalar(id(destination), resultId, ~values.scalarBits(sourceId) & mask(width))
        Right(())
      case Some((width, count)) =>
        values.get(sourceId) match {
          case Some(RuntimeValue.Bits(runtimeType, lanes))
              if (runtimeType == resultId || integerShape(
                program,
                runtimeType
              ).contains(width -> count)) && lanes.length == count =>
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

  private def integerExtended(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      left: Operand,
      right: Operand,
      operation: Int
  ): Either[Fault, Unit] = {
    val member = program.layout.runtimeType(id(resultType)) match {
      case Some(RuntimeType.Structure(members)) if members.size == 2 && members(0) == members(1) => Some(members(0))
      case _                                                                                     => None
    }
    (member.flatMap(signedIntegerShape(program, _)), values.get(id(left)), values.get(id(right))) match {
      case (Some((width, count, signed)), Some(RuntimeValue.Bits(aType, a)), Some(RuntimeValue.Bits(bType, b)))
          if member.contains(
            aType
          ) && aType == bType && a.length == count && b.length == count && (operation == 3 || !signed) =>
        val result   = Array.ofDim[Long](count * 2)
        val wordMask = mask(width)
        for (lane <- 0 until count) {
          val x    = a(lane) & wordMask
          val y    = b(lane) & wordMask
          val low  = (if (operation == 0) x + y else if (operation == 1) x - y else x * y) & wordMask
          val high = operation match {
            case 0                   => if (java.lang.Long.compareUnsigned(low, x) < 0) 1L else 0L
            case 1                   => if (java.lang.Long.compareUnsigned(x, y) < 0) 1L else 0L
            case _ if width.raw < 64 =>
              if (operation == 3) (signExtend(x, width) * signExtend(y, width)) >> width.raw
              else (x * y) >>> width.raw
            case _ =>
              // Four 32-bit products give the high half without host-specific intrinsics.
              val x0       = x & 0xffffffffL
              val y0       = y & 0xffffffffL
              val x1       = x >>> 32
              val y1       = y >>> 32
              val t        = x1 * y0 + ((x0 * y0) >>> 32)
              val middle   = (t & 0xffffffffL) + x0 * y1
              val unsigned = x1 * y1 + (t >>> 32) + (middle >>> 32)
              if (operation == 3) unsigned - (if (x < 0) y else 0L) - (if (y < 0) x else 0L)
              else unsigned
          }
          result(lane) = low
          result(count + lane) = high & wordMask
        }
        values.update(id(destination), RuntimeValue.Bits(id(resultType), result))
        Right(())
      case (_, None, _) => Left(undefined(left))
      case (_, _, None) => Left(undefined(right))
      case _            => Left(
          Fault.InvalidTask("extended integer arithmetic requires a two-member integer structure and matching operands")
        )
    }
  }

  private def integerBitCount(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      base: Operand
  ): Either[Fault, Unit] = values.get(id(base)) match {
    case Some(RuntimeValue.Bits(baseType, lanes)) =>
      (integerShape(program, id(resultType)), integerShape(program, baseType)) match {
        case (Some((resultWidth, count)), Some((baseWidth, baseCount)))
            if count == baseCount && lanes.length == count =>
          values.update(
            id(destination),
            RuntimeValue.Bits(id(resultType), lanes.map(value => bitCount(value, baseWidth).toLong & mask(resultWidth)))
          )
          Right(())
        case _ =>
          Left(Fault.InvalidTask("OpBitCount requires integer operands and results with equal component counts"))
      }
    case Some(_) => Left(Fault.InvalidTask("OpBitCount base is not an integer value"))
    case None    => Left(undefined(base))
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

  private enum MatrixOperation {
    case Transpose, Scale, VectorMatrix, MatrixVector, Multiply, OuterProduct
  }

  private def matrixShape(program: Program, typeId: Int): Option[(Int in (16, 32, 64), Int, Int)] =
    program.layout.runtimeType(typeId) match {
      case Some(RuntimeType.MatrixType(column, columns)) =>
        floatingShape(program, column).map { case (width, rows) => (width, rows, columns) }
      case _ => None
    }

  private def floatingMatrix(
      program: Program,
      values: InvocationValues,
      resultType: Operand,
      destination: Operand,
      operation: MatrixOperation,
      sources: Vec[Operand]
  ): Either[Fault, Unit] = {
    val required = if (operation == MatrixOperation.Transpose) 1 else 2
    if (sources.size != required) Left(Fault.InvalidTask(s"matrix $operation has the wrong operand count"))
    else sources.traverse { source =>
      values.get(id(source)) match {
        case Some(RuntimeValue.Bits(typeId, lanes)) => Right((typeId, lanes))
        case None                                   => Left(undefined(source))
        case _ => Left(Fault.InvalidTask(s"matrix $operation requires data operands"))
      }
    }.flatMap { operands =>
      val resultId     = id(resultType)
      val leftType     = operands(0)._1
      val left         = operands(0)._2
      val rightType    = if (required == 2) operands(1)._1 else leftType
      val right        = if (required == 2) operands(1)._2 else left
      val resultMatrix = matrixShape(program, resultId)
      val leftMatrix   = matrixShape(program, leftType)
      val rightMatrix  = matrixShape(program, rightType)
      val resultVector = floatingShape(program, resultId)
      val leftVector   = floatingShape(program, leftType)
      val rightVector  = floatingShape(program, rightType)
      // Every matrix is a sequence of columns. Storage padding is not part of a value's lanes.
      def product(
          width: Int in (16, 32, 64),
          rows: Int,
          columns: Int,
          inner: Int,
          a: (Int, Int) => Long,
          b: (Int, Int) => Long
      ): Array[Long] =
        Array.tabulate(rows * columns) { lane =>
          val row    = lane % rows
          val column = lane / rows
          (0 until inner).foldLeft(doubleToFloating(width, 0.0)) { (sum, k) =>
            floatingAddBits(width, sum, floatingMultiplyBits(width, a(row, k), b(k, column)))
          }
        }
      val result = operation match {
        case MatrixOperation.Transpose => (leftMatrix, resultMatrix) match {
            case (Some((width, rows, columns)), Some((outWidth, outRows, outColumns)))
                if width == outWidth && rows == outColumns && columns == outRows && left.length == rows * columns =>
              Some(Array.tabulate(left.length)(lane => left((lane % outRows) * rows + lane / outRows)))
            case _ => None
          }
        case MatrixOperation.Scale => (leftMatrix, rightVector) match {
            case (Some(shape @ (width, rows, columns)), Some((scalarWidth, 1)))
                if resultMatrix.contains(
                  shape
                ) && width == scalarWidth && left.length == rows * columns && right.length == 1 =>
              Some(left.map(floatingMultiplyBits(width, _, right(0))))
            case _ => None
          }
        case MatrixOperation.Multiply => (leftMatrix, rightMatrix, resultMatrix) match {
            case (
                  Some((width, rows, inner)),
                  Some((rightWidth, rightRows, columns)),
                  Some((outWidth, outRows, outColumns))
                )
                if width == rightWidth && width == outWidth && inner == rightRows && outRows == rows && outColumns == columns &&
                  left.length == rows * inner && right.length == inner * columns =>
              Some(product(width, rows, columns, inner, (r, k) => left(k * rows + r), (k, c) => right(c * inner + k)))
            case _ => None
          }
        case MatrixOperation.MatrixVector => (leftMatrix, rightVector, resultVector) match {
            case (Some((width, rows, columns)), Some((rightWidth, count)), Some((outWidth, outCount)))
                if width == rightWidth && width == outWidth && columns == count && rows == outCount &&
                  left.length == rows * columns && right.length == count =>
              Some(product(width, rows, 1, columns, (r, k) => left(k * rows + r), (k, _) => right(k)))
            case _ => None
          }
        case MatrixOperation.VectorMatrix => (leftVector, rightMatrix, resultVector) match {
            case (Some((width, count)), Some((rightWidth, rows, columns)), Some((outWidth, outCount)))
                if width == rightWidth && width == outWidth && rows == count && columns == outCount &&
                  left.length == count && right.length == rows * columns =>
              Some(product(width, 1, columns, rows, (_, k) => left(k), (k, c) => right(c * rows + k)))
            case _ => None
          }
        case MatrixOperation.OuterProduct => (leftVector, rightVector, resultMatrix) match {
            case (Some((width, rows)), Some((rightWidth, columns)), Some((outWidth, outRows, outColumns)))
                if width == rightWidth && width == outWidth && rows == outRows && columns == outColumns &&
                  left.length == rows && right.length == columns =>
              Some(Array.tabulate(rows * columns)(lane =>
                floatingMultiplyBits(width, left(lane % rows), right(lane / rows))
              ))
            case _ => None
          }
      }
      result.toRight(Fault.InvalidTask(s"matrix $operation operand/result shapes do not match")).map { lanes =>
        values.update(id(destination), RuntimeValue.Bits(resultId, lanes))
      }
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
          val result = if (all) !lanes.contains(0L) else lanes.exists(_ != 0L)
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
    case Modulo    extends SignedIntegerOperation("OpSMod")
  }

  private enum UnsignedIntegerOperation(val opcode: String) {
    case Divide    extends UnsignedIntegerOperation("OpUDiv")
    case Remainder extends UnsignedIntegerOperation("OpUMod")
  }

  // OpenCL integer division by zero yields an unspecified value without trapping.
  // Choose zero for that lane and continue computing the remaining lanes.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
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
              result(index) =
                if (divisor == 0L) 0L
                else
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

  private def atomicWidth(program: Program, typeId: Int, opcode: String): Either[Fault, Int in (32, 64)] =
    Option.when(typeId >= 0)(typeId).flatMap(atomicScalarShape(program, _)) match {
      case Some((width, 1)) if width.raw == 32 || width.raw == 64 => Right(width.raw.unsafe[Int in (32, 64)])
      case _ => Left(Fault.InvalidTask(s"$opcode requires a 32- or 64-bit scalar type"))
    }

  // Uniform includes legacy BufferBlock storage buffers, alongside StorageBuffer.
  private def atomicStorage(storage: Int): Boolean = storage match {
    case StorageClass.Uniform | StorageClass.Workgroup | StorageClass.CrossWorkgroup | StorageClass.Generic |
        StorageClass.AtomicCounter | StorageClass.Image | StorageClass.StorageBuffer | StorageClass.PhysicalStorageBuffer =>
      true
    case _ => false
  }

  private def atomicTarget(
      values: InvocationValues,
      pointer: Operand,
      typeId: Int,
      opcode: String
  ): Either[Fault, Pointer] = for {
    target <- address(values, pointer)
    _      <- Either.cond(
      target.pointee == typeId,
      (),
      Fault.InvalidTask(s"$opcode pointer pointee %${target.pointee} does not match type %$typeId")
    )
    _ <- Either.cond(
      atomicStorage(target.storageClass),
      (),
      Fault.InvalidTask(s"$opcode pointer storage class ${target.storageClass} does not support atomic access")
    )
  } yield target

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
    for {
      width  <- atomicWidth(program, valueType, opcode)
      target <- atomicTarget(values, pointer, valueType, opcode)
      _      <- validateAtomicControls(values, scope, semantics, opcode)
      _      <- memory.atomicWriteScalar(target, values.scalarBits(valueId) & mask(width), width.toMemoryValueByteWidth)
    } yield ()
  }

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
    for {
      width  <- atomicWidth(program, resultTypeId, opcode)
      target <- atomicTarget(values, pointer, resultTypeId, opcode)
      _      <- validateAtomicControls(values, scope, semantics, opcode)
      result <- memory.atomicReadScalar(target, width.toMemoryValueByteWidth)
    } yield values.updateScalar(id(destination), resultTypeId, result & mask(width))
  }

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
    for {
      width <- atomicWidth(program, resultTypeId, opcode)
      _     <- Either.cond(
        values.scalarType(valueId) == resultTypeId,
        (),
        Fault.InvalidTask(s"$opcode requires matching result and value types")
      )
      target   <- atomicTarget(values, pointer, resultTypeId, opcode)
      _        <- validateAtomicControls(values, scope, semantics, opcode)
      previous <-
        memory.atomicModifyScalar(target, width.toMemoryValueByteWidth)(_ => values.scalarBits(valueId) & mask(width))
    } yield values.updateScalar(id(destination), resultTypeId, previous & mask(width))
  }

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
    for {
      width <- atomicWidth(program, resultTypeId, opcode)
      _     <- Either.cond(
        values.scalarType(valueId) == resultTypeId && values.scalarType(comparatorId) == resultTypeId,
        (),
        Fault.InvalidTask(s"$opcode requires matching result, value, and comparator types")
      )
      target   <- atomicTarget(values, pointer, resultTypeId, opcode)
      _        <- validateAtomicControls(values, scope, equal, opcode)
      _        <- validateAtomicControls(values, scope, unequal, opcode)
      previous <- memory.atomicModifyScalar(target, width.toMemoryValueByteWidth) { previous =>
        if ((previous & mask(width)) == (values.scalarBits(comparatorId) & mask(width)))
          values.scalarBits(valueId) & mask(width)
        else previous
      }
    } yield values.updateScalar(id(destination), resultTypeId, previous & mask(width))
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
      previous <- memory.atomicModifyScalar(target, 4)(_ => 1L)
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
      _ <- memory.atomicWriteScalar(target, 0L, 4)
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
        integerShape(program, target.pointee).exists { case (width, count) => width.raw == 32 && count == 1 },
        (),
        Fault.InvalidTask(s"$opcode requires a pointer to a 32-bit integer flag")
      )
      _ <- Either.cond(
        atomicStorage(target.storageClass),
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

  private def atomicScalarShape(program: Program, typeId: Int): Option[(Int where Between[1, 64], Int)] =
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
      case Some((width, 1)) if width.raw == 32 || width.raw == 64 => (width.raw.unsafe[Int in (32, 64)], 1)
      case _                                                      =>
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
    val byteSize = width.toMemoryValueByteWidth
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
          if (width.raw == 64 && java.lang.Long.compareUnsigned(previous, operand) > 0) operand
          else if (width.raw < 64 && previous > operand) operand
          else previous
        case AtomicIntegerOperation.SignedMaximum =>
          if (signExtend(previous, width) < signExtend(operand, width)) operand else previous
        case AtomicIntegerOperation.UnsignedMaximum =>
          if (width.raw == 64 && java.lang.Long.compareUnsigned(previous, operand) < 0) operand
          else if (width.raw < 64 && previous < operand) operand
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
    val target = atomicTarget(values, pointer, resultTypeId, opcode) match {
      case Left(error)  => return Left(error)
      case Right(value) => value
    }
    validateAtomicControls(values, scope, semantics, opcode).flatMap { _ =>
      val width    = shape._1
      val bitWidth = width.bitWidth
      val byteSize = width.toMemoryValueByteWidth
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
    val target = atomicTarget(values, pointer, resultTypeId, opcode) match {
      case Left(error)  => return Left(error)
      case Right(value) => value
    }
    validateAtomicControls(values, scope, semantics, opcode).flatMap { _ =>
      val width    = shape._1
      val bitWidth = width.bitWidth
      val byteSize = width.toMemoryValueByteWidth
      val operand  = values.scalarBits(valueId)
      memory.atomicModifyScalar(target, byteSize) { previous =>
        val updated =
          if (maximum) floatingMaximumBits(width, previous, operand)
          else floatingMinimumBits(width, previous, operand)
        updated & mask(bitWidth)
      }.map(previous => values.updateScalar(id(destination), resultTypeId, previous & mask(bitWidth)))
    }
  }

  // Exceptional integer results do not abort other lanes; scalar and vector paths agree.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
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
          val divisor  = signExtend(values.scalarBits(rightId), width)
          val dividend = signExtend(values.scalarBits(leftId), width)
          val result   = if (divisor == 0L) 0L
          else operation match {
            case SignedIntegerOperation.Divide    => dividend / divisor
            case SignedIntegerOperation.Remainder => dividend % divisor
            case SignedIntegerOperation.Modulo    =>
              val remainder = dividend % divisor
              if (remainder != 0 && (remainder < 0) != (divisor < 0)) remainder + divisor else remainder
          }
          values.updateScalar(id(destination), id(resultType), result & mask(width))
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
              val divisor  = signExtend(rightValue, width)
              val dividend = signExtend(leftValue, width)
              val result   = if (divisor == 0L) 0L
              else operation match {
                case SignedIntegerOperation.Divide    => dividend / divisor
                case SignedIntegerOperation.Remainder => dividend % divisor
                case SignedIntegerOperation.Modulo    =>
                  val remainder = dividend % divisor
                  if (remainder != 0 && (remainder < 0) != (divisor < 0)) remainder + divisor else remainder
              }
              values.update(id(destination), RuntimeValue.Bits(id(resultType), Array(result & mask(width))))
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
                val divisor  = signExtend(rightLanes(index), width)
                val dividend = signExtend(leftLanes(index), width)
                result(index) =
                  (if (divisor == 0L) 0L
                   else operation match {
                     case SignedIntegerOperation.Divide    => dividend / divisor
                     case SignedIntegerOperation.Remainder => dividend % divisor
                     case SignedIntegerOperation.Modulo    =>
                       val remainder = dividend % divisor
                       if (remainder != 0 && (remainder < 0) != (divisor < 0)) remainder + divisor else remainder
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

  private def integerShape(program: Program, typeId: Int): Option[(Int where Between[1, 64], Int)] =
    program.layout.runtimeType(typeId) match {
      case Some(RuntimeType.Integer(width, _))            => Some((width, 1))
      case Some(RuntimeType.VectorType(component, count)) => program.layout.runtimeType(component) match {
          case Some(RuntimeType.Integer(width, _)) => Some((width, count))
          case _                                   => None
        }
      case _ => None
    }

  private def floatingShape(program: Program, typeId: Int): Option[(OneOf[(16, 32, 64)], Int)] =
    program.layout.runtimeType(typeId) match {
      case Some(RuntimeType.Floating(width))              => Some((width, 1))
      case Some(RuntimeType.VectorType(component, count)) => program.layout.runtimeType(component) match {
          case Some(RuntimeType.Floating(width)) => Some((width, count))
          case _                                 => None
        }
      case _ => None
    }

  def floatingAddBits(width: OneOf[(16, 32, 64)], left: Long, right: Long): Long = width.raw match {
    case 16 =>
      IEEE754.floatToBinary16(
        IEEE754.binary16ToFloat(Binary16.low(left)) +
          IEEE754.binary16ToFloat(Binary16.low(right))
      ).toLong
    case 32 =>
      IEEE754.floatToBinary32(
        IEEE754.binary32ToFloat(Binary32.low(left)) +
          IEEE754.binary32ToFloat(Binary32.low(right))
      ).toLong
    case 64 =>
      IEEE754.doubleToBinary64(IEEE754.binary64ToDouble(left) + IEEE754.binary64ToDouble(right))
  }

  private def floatingOneBits(width: OneOf[(16, 32, 64)]): Long = width.raw match {
    case 16 => IEEE754.floatToBinary16(1.0f).toLong
    case 32 => IEEE754.floatToBinary32(1.0f).toLong
    case 64 => IEEE754.doubleToBinary64(1.0)
  }

  private def floatingInfinityBits(width: OneOf[(16, 32, 64)], negative: Boolean): Long = width.raw match {
    case 16 => if (negative) 0xfc00L else 0x7c00L
    case 32 => if (negative) 0xff800000L else 0x7f800000L
    case 64 => if (negative) 0xfff0000000000000L else 0x7ff0000000000000L
  }

  def floatingMultiplyBits(
      width: OneOf[(16, 32, 64)],
      left: Long,
      right: Long
  ): Long = width.raw match {
    case 16 =>
      IEEE754.floatToBinary16(
        IEEE754.binary16ToFloat(Binary16.low(left)) *
          IEEE754.binary16ToFloat(Binary16.low(right))
      ).toLong
    case 32 =>
      IEEE754.floatToBinary32(
        IEEE754.binary32ToFloat(Binary32.low(left)) *
          IEEE754.binary32ToFloat(Binary32.low(right))
      ).toLong
    case 64 =>
      IEEE754.doubleToBinary64(IEEE754.binary64ToDouble(left) * IEEE754.binary64ToDouble(right))
  }

  private def floatingFmaBits(width: OneOf[(16, 32, 64)], left: Long, right: Long, addend: Long): Long =
    width.raw match {
      case 16 =>
        IEEE754.floatToBinary16(
          Math.fma(
            IEEE754.binary16ToFloat(Binary16.low(left)).toDouble,
            IEEE754.binary16ToFloat(Binary16.low(right)).toDouble,
            IEEE754.binary16ToFloat(Binary16.low(addend)).toDouble
          ).toFloat
        ).toLong
      case 32 =>
        IEEE754.floatToBinary32(
          Math.fma(
            IEEE754.binary32ToFloat(Binary32.low(left)),
            IEEE754.binary32ToFloat(Binary32.low(right)),
            IEEE754.binary32ToFloat(Binary32.low(addend))
          )
        ).toLong
      case 64 =>
        IEEE754.doubleToBinary64(
          Math.fma(IEEE754.binary64ToDouble(left), IEEE754.binary64ToDouble(right), IEEE754.binary64ToDouble(addend))
        )
    }

  def floatingMinimumBits(
      width: OneOf[(16, 32, 64)],
      left: Long,
      right: Long
  ): Long = width.raw match {
    case 16 =>
      IEEE754.floatToBinary16(
        floatingMinimum(
          IEEE754.binary16ToFloat(Binary16.low(left)),
          IEEE754.binary16ToFloat(Binary16.low(right))
        )
      ).toLong
    case 32 =>
      IEEE754.floatToBinary32(
        floatingMinimum(
          IEEE754.binary32ToFloat(Binary32.low(left)),
          IEEE754.binary32ToFloat(Binary32.low(right))
        )
      ).toLong
    case 64 =>
      IEEE754.doubleToBinary64(floatingMinimum(IEEE754.binary64ToDouble(left), IEEE754.binary64ToDouble(right)))
  }

  def floatingMaximumBits(
      width: OneOf[(16, 32, 64)],
      left: Long,
      right: Long
  ): Long = width.raw match {
    case 16 =>
      IEEE754.floatToBinary16(
        floatingMaximum(
          IEEE754.binary16ToFloat(Binary16.low(left)),
          IEEE754.binary16ToFloat(Binary16.low(right))
        )
      ).toLong
    case 32 =>
      IEEE754.floatToBinary32(
        floatingMaximum(
          IEEE754.binary32ToFloat(Binary32.low(left)),
          IEEE754.binary32ToFloat(Binary32.low(right))
        )
      ).toLong
    case 64 =>
      IEEE754.doubleToBinary64(floatingMaximum(IEEE754.binary64ToDouble(left), IEEE754.binary64ToDouble(right)))
  }

  private def floatingMinimum(left: Float, right: Float): Float =
    if (left.isNaN) right
    else if (right.isNaN) left
    else if (left == right) {
      if (
        left == 0.0f &&
        ((IEEE754.floatToBinary32(left).toInt | IEEE754.floatToBinary32(right).toInt) < 0)
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
        ((IEEE754.floatToBinary32(left).toInt & IEEE754.floatToBinary32(right).toInt) >= 0)
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

  private def booleanLanes(program: Program, typeId: Int): Option[Int] = program.layout.runtimeType(typeId) match {
    case Some(RuntimeType.Bool) => Some(1)
    case Some(RuntimeType.VectorType(component, count))
        if program.layout.runtimeType(component).contains(RuntimeType.Bool) =>
      Some(count)
    case _ => None
  }

  private def label(function: Function, operand: Operand): Either[Fault, Int] =
    function.labels.get(id(operand)) match {
      case Some(value) => Right(value)
      case None        => Left(Fault.InvalidTask(s"SPIR-V label %${id(operand)} is absent in function %${function.id}"))
    }

  private def withExecutionContext(
      error: Fault | Memory.Pending.type,
      function: Function,
      pc: Int,
      instruction: Instruction
  ): Fault | Memory.Pending.type = error match {
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
