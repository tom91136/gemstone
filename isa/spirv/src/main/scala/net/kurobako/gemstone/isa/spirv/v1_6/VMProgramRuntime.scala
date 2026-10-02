package net.kurobako.gemstone.isa.spirv.v1_6

import VMIR.*
import VMMemory.*
import cats.syntax.all.*
import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Bits.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import scala.collection.concurrent.TrieMap

private class VMProgramRuntime(val ir: VMIR.Program) extends LoadedProgram {
  export ir.*
  val usesNonUniformCollectives: Boolean = ir.module.instructions.exists(VMCollectives.isNonUniform)

  val layout: VMTypeLayout = VMTypeLayout(ir)

  // Zero identifies direct VM execution; published namespaces are bound once.
  private val functionPointerNamespace = java.util.concurrent.atomic.AtomicLong(0L)

  def ownsFunctionPointer(token: Long): Boolean = (token >>> 32) == functionPointerNamespace.get()

  val constants: Map[Int, VMValue] = ir.constantData.view.mapValues(VMValue.fromConstant).toMap

  @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
  private val scalarIntegerInstructions = {
    val compiled = Array.ofDim[VMScalarIntegerInstruction | Null](module.instructions.size)
    var pc       = 0
    while (pc < module.instructions.size) {
      val instruction = module.instructions(pc) match {
        case Instruction.IAdd(resultType, destination, left, right) =>
          compileScalarInteger(VMScalarIntegerInstruction.Add, resultType, destination, left, right)
        case Instruction.ISub(resultType, destination, left, right) =>
          compileScalarInteger(VMScalarIntegerInstruction.Subtract, resultType, destination, left, right)
        case Instruction.IMul(resultType, destination, left, right) =>
          compileScalarInteger(VMScalarIntegerInstruction.Multiply, resultType, destination, left, right)
        case _ => null
      }
      compiled(pc) = instruction
      pc += 1
    }
    compiled
  }

  def scalarIntegerInstruction(pc: Int): VMScalarIntegerInstruction | Null =
    scalarIntegerInstructions(pc)

  private def compileScalarInteger(
      operation: Byte,
      resultType: Operand,
      destination: Operand,
      left: Operand,
      right: Operand
  ): VMScalarIntegerInstruction | Null = {
    val typeId = Operand.id(resultType)
    layout.runtimeType(typeId) match {
      case Some(RuntimeType.Integer(width, _)) =>
        val leftId        = Operand.id(left)
        val rightId       = Operand.id(right)
        val leftConstant  = scalarConstant(leftId)
        val rightConstant = scalarConstant(rightId)
        VMScalarIntegerInstruction(
          operation,
          typeId,
          Operand.id(destination),
          leftId,
          rightId,
          leftConstant.isDefined,
          rightConstant.isDefined,
          leftConstant.getOrElse(0L),
          rightConstant.getOrElse(0L),
          if (width.raw == 64) -1L else (1L << width.raw) - 1L
        )
      case _ => null // scalafix:ok DisableSyntax.null
    }
  }

  private def scalarConstant(id: Int): Option[Long] =
    constantData.get(id) match {
      case Some(Constant.Bits(_, values)) if values.size == 1 => Some(values.head)
      case _                                                  => None
    }

  case class GlobalBinding(region: AllocationId, offset: Long >= 0, memory: Memory)
  private val boundGlobals: TrieMap[Int, GlobalBinding]          = TrieMap.empty
  def global(name: NonEmpty[String]): Either[Fault, GlobalImage] =
    globalImage(name).flatMap { case (_, bytes) =>
      GlobalImage.validated(bytes.length.toLong.unsafe[Long >= 0], Vec.from(bytes)).left.map(Fault.InvalidTask(_))
    }

  def bindGlobal(
      name: NonEmpty[String],
      region: AllocationId,
      offset: Long >= 0,
      regionSize: Long > 0,
      memory: Memory
  ): Either[Fault, Unit] =
    globalImage(name).flatMap { case (id, initial) =>
      if (offset.raw > regionSize.raw || initial.length.toLong > (regionSize.raw - offset.raw))
        Left(Fault.Memory(s"SPIR-V global '$name' exceeds the bound allocation"))
      else memory.write(region, offset, Vec.from(initial)).map { _ =>
        val _ = boundGlobals.put(id, GlobalBinding(region, offset, memory))
        ()
      }
    }

  def writeGlobal(
      name: NonEmpty[String],
      offset: Long >= 0,
      bytes: Vec[Byte]
  ): Either[Fault, Unit] =
    globalImage(name).flatMap { case (id, initial) =>
      boundGlobals.get(id).toRight(Fault.Memory(s"SPIR-V global '$name' is not bound")).flatMap { binding =>
        if (offset.raw > initial.length || bytes.length > initial.length - offset.raw)
          Left(Fault.Memory(s"SPIR-V global '$name' write exceeds its storage"))
        else binding.memory.write(binding.region, binding.offset + offset, bytes)
      }
    }

  def symbols: Either[Fault, ProgramSymbols] =
    Right(ProgramSymbols(kernelNames.map(name => KernelSymbol(name, name)), Vec.empty))

  def bindFunctionPointerNamespace(id: Long): Either[Fault, Unit] =
    if (id <= 0L || id > 0xffffffffL)
      Left(Fault.InvalidTask("function pointer namespace must fit a positive 32-bit identity"))
    else if (functionPointerNamespace.compareAndSet(0L, id) || functionPointerNamespace.get() == id) Right(())
    else Left(Fault.InvalidTask("function pointer namespace is already bound"))

  def execute(
      task: KernelTask,
      memory: Memory,
      control: TaskControl,
      workgroups: Workgroups,
      handler: Event => Either[Fault, Unit],
      measurement: ExecutionMetrics => Unit
  ): Either[Fault, Unit] =
    VMInterpreter.execute(this, task, memory, control, workgroups, handler, measurement)

  def kernelNames: Vec[NonEmpty[String]] =
    Vec.from(this.entryPoints.keys.toSeq.sorted)

  private def globalImage(name: String): Either[Fault, (Int, Array[Byte])] =
    this.functionNames.find { case (id, candidate) =>
      candidate == name && this.globals.contains(id)
    }.map(_._1).toRight(Fault.Memory(s"SPIR-V global '$name' is not present")).flatMap { id =>
      materializeGlobals(storageClass => storageClass != 4 && storageClass != 6, useBindings = false)
        .flatMap(_.get(id) match {
          case Some(VMValue.Address(_, Pointer(Region.Internal(storage), _, _, _, _, _, _, _, _))) =>
            Right(id -> storage.bytes)
          case _ => Left(Fault.Memory(s"SPIR-V global '$name' has no storage"))
        })
    }

  def kernel(name: NonEmpty[String]): Either[Fault, KernelMetadata] =
    if (this.vulkanEntries.contains(name)) vulkanKernelMetadata(name)
    else {
      val compiled = this
      compiled.entryPoints.get(name).orElse(
        compiled.functionNames.collectFirst {
          case (id, candidate) if candidate == name && compiled.functions.contains(id) => id
        }
      ).toRight(
        Fault.InvalidTask(s"SPIR-V entry point $name does not exist")
      ).flatMap { functionId =>
        compiled.functions.get(functionId).toRight(
          Fault.InvalidTask(s"SPIR-V entry point $name has no function body")
        ).flatMap { function =>
          function.parameters.foldLeft[Either[Fault, (Vec[KernelMetadata.Parameter], Int)]](
            Right((Vec.empty[KernelMetadata.Parameter], 0))
          ) { case (result, (parameterId, parameterType)) =>
            result.flatMap { case (parameters, currentOffset) =>
              parameterMetadata(parameterId, parameterType).flatMap { parameter =>
                val offset = alignUp(currentOffset, math.min(8, compiled.layout.typeAlignment(parameterType)))
                offset.refine[Int >= 0]
                  .leftMap(_ =>
                    Fault.InvalidTask(s"negative SPIR-V parameter offset $offset")
                  )
                  .map(refinedOffset =>
                    parameters.appended(parameter.copy(offset =
                      refinedOffset
                    )) -> Math.addExact(offset, parameter.width.raw)
                  )
              }
            }
          }.flatMap { case (parameters, size) =>
            for {
              refinedSize <- size.refine[Int >= 0]
                .leftMap(_ =>
                  Fault.InvalidTask(s"negative SPIR-V argument block size $size")
                )
              entryPointId <-
                functionId.refine[Int >= 0].leftMap(_ => Fault.InvalidTask(s"negative SPIR-V function ID $functionId"))
              metadata <- KernelMetadata.validated(
                refinedSize,
                parameters.nonNegativeSize,
                parameters,
                requiredWorkGroupSize = workGroupSize(functionId),
                workGroupSizeHint = workGroupSizeHint(functionId),
                entryPointId = Some(entryPointId)
              )
                .leftMap(detail => Fault.InvalidTask(detail))
            } yield metadata
          }
        }
      }
    }

  def descriptorVariables(name: NonEmpty[String]): Vec[Int] = VMShaderInterface.argumentVariables(this, name)
  private def vulkanKernelMetadata(name: NonEmpty[String]): Either[Fault, KernelMetadata] =
    VMShaderInterface.metadata(this, name)

  def workGroupSize(functionId: Int): Option[LaunchGeometry.Dimensions] =
    this.builtinWorkGroupSize.orElse(this.workGroupSizes.get(functionId))

  def workGroupSizeHint(functionId: Int): Option[LaunchGeometry.Dimensions] =
    this.workGroupSizeHints.get(functionId)

  private def parameterMetadata(
      parameterId: Int,
      parameterType: Int
  ): Either[Fault, KernelMetadata.Parameter] =
    this.layout.runtimeType(parameterType) match {
      case Some(RuntimeType.Pointer(_, pointee)) if this.byValueParameters.contains(parameterId) =>
        this.layout.pointerLeafOffsets(pointee, 0L).flatMap(pointers =>
          parameterMetadata(this.layout.typeSize(pointee), KernelMetadata.ParameterKind.Scalar, pointers.nonEmpty)
        )
      case Some(RuntimeType.Pointer(storageClass, _)) if storageClass == 4 =>
        Right(KernelMetadata.Parameter(
          8,
          0,
          KernelMetadata.ParameterKind.Local,
          containsPointers = false
        ))
      case Some(RuntimeType.Pointer(_, _)) =>
        Right(KernelMetadata.Parameter(
          8,
          0,
          KernelMetadata.ParameterKind.Allocation,
          containsPointers = false
        ))
      case Some(_) => this.layout.pointerLeafOffsets(parameterType, 0L).flatMap(pointers =>
          parameterMetadata(this.layout.typeSize(parameterType), KernelMetadata.ParameterKind.Scalar, pointers.nonEmpty)
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
    width.refine[Int > 0]
      .leftMap(_ =>
        Fault.InvalidTask(s"SPIR-V parameter width must be positive: $width")
      )
      .map(refinedWidth => KernelMetadata.Parameter(refinedWidth, 0, kind, containsPointers))

  // Global initialization exits before publishing a partially materialized image.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return", "scalafix:DisableSyntax.var"))
  def materializeGlobals(
      includeStorageClass: Int => Boolean,
      useBindings: Boolean = true
  ): Either[Fault, Map[Int, VMValue]] = {
    val values      = Map.newBuilder[Int, VMValue]
    val globals     = this.globals.view.toVector
    var globalIndex = 0
    while (globalIndex < globals.size) {
      val (variable, global) = globals(globalIndex)
      if (!this.builtins.contains(variable) && includeStorageClass(global.storageClass)) {
        val pointee = this.layout.pointerPointee(global.pointerType)
        val bytes   = global.initializer match {
          case None              => Array.ofDim[Byte](this.layout.typeSize(pointee))
          case Some(initializer) => this.constants.get(initializer) match {
              case None =>
                return Left(
                  Fault.InvalidTask(
                    s"SPIR-V global variable %$variable has undefined initializer %$initializer"
                  )
                )
              case Some(value) =>
                val encoded = encode(value) match {
                  case Left(error) => return Left(error)
                  case Right(data) => data
                }
                if (encoded.length != this.layout.typeSize(pointee))
                  return Left(
                    Fault.InvalidTask(
                      s"SPIR-V global variable %$variable initializer has ${encoded.length} bytes, " +
                        s"expected ${this.layout.typeSize(pointee)}"
                    )
                  )
                encoded
            }
        }
        val pointer = (if (useBindings) this.boundGlobals.get(variable) else None) match {
          case Some(binding) =>
            Pointer(Region.External(binding.region), binding.offset.raw, pointee, global.storageClass)
          case None => Pointer(
              Region.Internal(InternalMemory(bytes)),
              0L,
              pointee,
              global.storageClass,
              bounds = Option.when(vulkanEntries.nonEmpty && global.storageClass == StorageClass.Private)(0L ->
                bytes.length.toLong)
            )
        }
        values += variable -> VMValue.Address(global.pointerType, pointer)
      }
      globalIndex += 1
    }
    Right(values.result())
  }

  def decode(typeId: Int, bytes: Array[Byte]): Either[Fault, VMValue] =
    this.layout.scalarLeafCount(typeId).flatMap { count =>
      try {
        val lanes = Array.ofDim[Long](count)
        val used  = if (layout.hasMatrixStorage(typeId)) {
          val leaves = layout.matrixStorageLeaves(typeId, None)
          leaves.zipWithIndex.foreach { case ((offset, scalar), lane) =>
            lanes(lane) =
              LittleEndian.decode(
                bytes,
                Math.toIntExact(offset),
                this.layout.typeSize(scalar).unsafe[Int where Between[0, 8]]
              )
          }
          leaves.size
        } else decodeBits(typeId, bytes, 0, lanes, 0)
        if (used != lanes.length)
          Left(Fault.InvalidTask(
            s"SPIR-V value of type %$typeId decoded $used scalar leaves, expected ${lanes.length}"
          ))
        else Right(VMValue.Bits(typeId, lanes))
      } catch {
        case error: IllegalArgumentException => Left(Fault.InvalidTask(throwables.detail(error)))
      }
    }

  // Dense scalar/vector decoding writes output lanes in place.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def decodeBits(
      typeId: Int,
      input: Array[Byte],
      byteStart: Int,
      lanes: Array[Long],
      laneStart: Int
  ): Int = this.layout.runtimeType(typeId) match {
    case Some(RuntimeType.Bool) | Some(RuntimeType.Integer(_, _)) | Some(RuntimeType.Floating(_)) |
        Some(RuntimeType.Event) =>
      if (laneStart >= lanes.length)
        throw IllegalArgumentException(s"SPIR-V value of type %$typeId has too many scalar leaves")
      val width     = this.layout.scalarWidth(typeId)
      val byteCount = width.toScalarByteWidth
      val offset    = Math.min(byteStart, input.length)
      val available = Math.min(byteCount.raw, input.length - offset).unsafe[Int where Between[0, 8]]
      lanes(laneStart) = LittleEndian.decode(input, offset, available) & mask(width)
      laneStart + 1
    case Some(RuntimeType.Pointer(_, _)) =>
      if (laneStart >= lanes.length)
        throw IllegalArgumentException(s"SPIR-V value of type %$typeId has too many pointer leaves")
      val offset    = Math.min(byteStart, input.length)
      val available = Math.min(8, input.length - offset).unsafe[Int where Between[0, 8]]
      lanes(laneStart) = LittleEndian.decode(input, offset, available)
      laneStart + 1
    case Some(RuntimeType.VectorType(component, count)) =>
      var lane  = laneStart
      var index = 0
      while (index < count) {
        lane = decodeBits(component, input, byteStart + index * this.layout.typeSize(component), lanes, lane)
        index += 1
      }
      lane
    case Some(RuntimeType.MatrixType(column, count)) =>
      var lane  = laneStart
      var index = 0
      while (index < count) {
        lane = decodeBits(column, input, byteStart + index * this.layout.typeSize(column), lanes, lane)
        index += 1
      }
      lane
    case Some(RuntimeType.Array(element, count)) =>
      val stride = this.arrayStrides.getOrElse(typeId, this.layout.typeSize(element))
      var lane   = laneStart
      var index  = 0L
      while (index < count) {
        lane = decodeBits(element, input, byteStart + Math.toIntExact(index * stride.toLong), lanes, lane)
        index += 1L
      }
      lane
    case Some(RuntimeType.Structure(members)) =>
      var lane  = laneStart
      var index = 0
      while (index < members.size) {
        lane = decodeBits(
          members(index),
          input,
          byteStart + this.layout.structMemberOffset(typeId, members, index),
          lanes,
          lane
        )
        index += 1
      }
      lane
    case value => throw IllegalArgumentException(s"SPIR-V type %$typeId cannot be decoded as data: $value")
  }

  def encode(value: VMValue): Either[Fault, Array[Byte]] = value match {
    case VMValue.Bits(typeId, lanes) =>
      try {
        val bytes = Array.ofDim[Byte](this.layout.typeSize(typeId))
        val used  = if (layout.hasMatrixStorage(typeId)) {
          val leaves = layout.matrixStorageLeaves(typeId, None)
          if (leaves.size != lanes.length)
            throw IllegalArgumentException("matrix storage initializer scalar count does not match")
          leaves.zipWithIndex.foreach { case ((offset, scalar), lane) =>
            LittleEndian.write(
              lanes(lane),
              bytes,
              Math.toIntExact(offset),
              this.layout.typeSize(scalar).unsafe[Int where Between[0, 8]]
            )
          }
          leaves.size
        } else encodeBits(typeId, lanes, 0, bytes, 0)
        if (used != lanes.length)
          Left(Fault.InvalidTask(
            s"SPIR-V value of type %$typeId has ${lanes.length} scalar leaves, expected $used"
          ))
        else Right(bytes)
      } catch {
        case error: IllegalArgumentException => Left(Fault.InvalidTask(throwables.detail(error)))
      }
    case VMValue.Address(_, pointer) =>
      Left(Fault.InvalidTask(s"cannot encode runtime pointer $pointer as process-neutral bytes"))
  }

  // Dense scalar/vector encoding writes output lanes in place.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def encodeBits(
      typeId: Int,
      lanes: Array[Long],
      laneStart: Int,
      output: Array[Byte],
      byteStart: Int
  ): Int = this.layout.runtimeType(typeId) match {
    case Some(RuntimeType.Bool) | Some(RuntimeType.Integer(_, _)) | Some(RuntimeType.Floating(_)) |
        Some(RuntimeType.Event) =>
      if (laneStart >= lanes.length)
        throw IllegalArgumentException(s"SPIR-V value of type %$typeId has too few scalar leaves")
      val width = this.layout.scalarWidth(typeId)
      LittleEndian.write(
        lanes(laneStart),
        output,
        byteStart,
        width.toScalarByteWidth
      )
      laneStart + 1
    case Some(RuntimeType.Pointer(_, _)) =>
      if (laneStart >= lanes.length)
        throw IllegalArgumentException(s"SPIR-V value of type %$typeId has too few pointer leaves")
      LittleEndian.write(lanes(laneStart), output, byteStart, 8)
      laneStart + 1
    case Some(RuntimeType.VectorType(component, count)) =>
      var lane  = laneStart
      var index = 0
      while (index < count) {
        lane = encodeBits(component, lanes, lane, output, byteStart + index * this.layout.typeSize(component))
        index += 1
      }
      lane
    case Some(RuntimeType.MatrixType(column, count)) =>
      var lane  = laneStart
      var index = 0
      while (index < count) {
        lane = encodeBits(column, lanes, lane, output, byteStart + index * this.layout.typeSize(column))
        index += 1
      }
      lane
    case Some(RuntimeType.Array(element, count)) =>
      val stride = this.arrayStrides.getOrElse(typeId, this.layout.typeSize(element))
      var lane   = laneStart
      var index  = 0L
      while (index < count) {
        lane = encodeBits(
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
          members(index),
          lanes,
          lane,
          output,
          byteStart + this.layout.structMemberOffset(typeId, members, index)
        )
        index += 1
      }
      lane
    case value => throw IllegalArgumentException(s"SPIR-V type %$typeId cannot be encoded as data: $value")
  }

  private def alignUp(value: Int, alignment: Int): Int = layout.alignUp(value, alignment)
}
