package net.kurobako.gemstone.isa.spirv

import LaneOps.*
import Op.*
import OpBuilder.*
import VMInvocation.*
import VMMemory.*
import VMProgram.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Bits.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.vm.*
import scala.annotation.tailrec

/** Variables, loads, stores, copies, access chains, pointer conversions and kernel argument binding. */
private object MemoryOps {
  import Operand.*

  def lower(site: Site): PartialFunction[Instruction, Op] = {
    case Instruction.Variable(resultType, dest, storageClass, initialiser) =>
      variable(site, id(resultType), id(dest), id(storageClass), initialiser.map(id(_)))
    case instruction @ Instruction.Load(resultType, dest, pointer, _) =>
      load(site, instruction, id(resultType), id(dest), id(pointer))
    case instruction @ Instruction.Store(pointer, source, _)     => store(site, instruction, id(pointer), id(source))
    case Instruction.CopyMemory(target, source, _, _)            => copyMemory(site, id(target), id(source))
    case Instruction.CopyMemorySized(target, source, size, _, _) => copyMemorySized(id(target), id(source), id(size))
    case Instruction.UntypedVariableKHR(_, dest, storageClass, Some(_), initialiser) =>
      variable(site, site.program.valueType(id(dest)), id(dest), id(storageClass), initialiser.map(id(_)))
    case Instruction.UntypedVariableKHR(dest = dest) =>
      Fail(Fault.InvalidRequest(s"OpUntypedVariableKHR %${id(dest)} in a function declares no data type"))
    case Instruction.UntypedAccessChainKHR(t, d, baseType, base, indexes) =>
      accessChain(site, t, d, base, None, indexes, Some(id(baseType)))
    case Instruction.UntypedInBoundsAccessChainKHR(t, d, baseType, base, indexes) =>
      accessChain(site, t, d, base, None, indexes, Some(id(baseType)))
    case Instruction.UntypedPtrAccessChainKHR(t, d, baseType, base, element, indexes) =>
      accessChain(site, t, d, base, Some(element), indexes, Some(id(baseType)))
    case Instruction.UntypedInBoundsPtrAccessChainKHR(t, d, baseType, base, element, indexes) =>
      accessChain(site, t, d, base, Some(element), indexes, Some(id(baseType)))
    case Instruction.UntypedArrayLengthKHR(t, d, structure, pointer, member) =>
      arrayLength(id(t), id(d), id(pointer), unsigned(member).toInt, Some(id(structure)))
    case Instruction.UntypedPrefetchKHR(pointerType = pointer) => prefetch(id(pointer))
    case Instruction.VariableLengthArrayINTEL(t, _, _) if site.program.runtimeType(id(t)).exists {
          case RuntimeType.Pointer(_, pointee) => site.program.untyped(pointee)
          case _                               => false
        } =>
      Fail(Fault.Unsupported(
        "OpVariableLengthArrayINTEL of an untyped pointer has no element type; OpUntypedVariableLengthArrayINTEL names it"
      ))
    case Instruction.VariableLengthArrayINTEL(t, d, length) =>
      variableLengthArray(site, id(t), id(d), site.program.pointerPointee(id(t)), id(length))
    case Instruction.UntypedVariableLengthArrayINTEL(_, d, element, length) =>
      variableLengthArray(site, site.program.valueType(id(d)), id(d), id(element), id(length))
    case Instruction.SaveMemoryINTEL(t, d) =>
      step { (execution, _) =>
        val top = apertureAddress(PrivateAperture, execution.storage.top)
        Right(
          execution.values.update(
            id(d),
            VMValue.Address(id(t), Pointer(top, site.program.pointerPointee(id(t)), StorageClass.Function))
          )
        )
      }
    case Instruction.RestoreMemoryINTEL(pointer) =>
      step { (execution, _) =>
        address(execution.values, id(pointer)).flatMap { saved =>
          val offset = offsetOf(saved.address).raw
          Either.cond(
            aperture(saved.address) == PrivateAperture && offset <= execution.storage.top,
            execution.storage.top = offset.toInt,
            Fault.InvalidRequest("OpRestoreMemoryINTEL pointer is not a saved stack position below the top")
          )
        }
      }
    case Instruction.AccessChain(t, d, base, indexes)         => accessChain(site, t, d, base, None, indexes)
    case Instruction.InBoundsAccessChain(t, d, base, indexes) =>
      accessChain(site, t, d, base, None, indexes)
    case Instruction.PtrAccessChain(t, d, base, element, indexes) =>
      accessChain(site, t, d, base, Some(element), indexes)
    case Instruction.InBoundsPtrAccessChain(t, d, base, element, indexes) =>
      accessChain(site, t, d, base, Some(element), indexes)
    case instruction @ Instruction.PtrEqual(resultType = _)    => staged(site, instruction, CoreFamily.Pointer)
    case instruction @ Instruction.PtrNotEqual(resultType = _) => staged(site, instruction, CoreFamily.Pointer)
    case instruction @ Instruction.PtrDiff(resultType = t)     =>
      site.program.integerShape(id(t)) match {
        case Some((_, 1)) => staged(site, instruction, CoreFamily.Pointer)
        case _            => Fail(Fault.InvalidRequest("OpPtrDiff requires a scalar integer result"))
      }
    case Instruction.PtrCastToGeneric(t, d, a) => castToGeneric(site, id(t), id(d), id(a))
    case Instruction.GenericCastToPtr(t, d, a) =>
      castFromGeneric(site, "OpGenericCastToPtr", id(t), id(d), id(a), None)
    case Instruction.GenericCastToPtrExplicit(t, d, a, storage) =>
      castFromGeneric(site, "OpGenericCastToPtrExplicit", id(t), id(d), id(a), Some(id(storage)))
    case Instruction.GenericPtrMemSemantics(t, d, a)      => genericSemantics(site, id(t), id(d), id(a))
    case Instruction.ConvertPtrToU(t, d, a)               => pointerToInteger(site, id(t), id(d), id(a))
    case Instruction.ConvertUToPtr(t, d, a)               => integerToPointer(site, id(t), id(d), id(a))
    case Instruction.ArrayLength(t, d, structure, member) =>
      arrayLength(id(t), id(d), id(structure), unsigned(member).toInt)
    case Instruction.SizeOf(t, d, pointer)        => sizeOf(site, id(t), id(d), pointer)
    case Instruction.LifetimeStart(pointer, size) => lifetime(site, "OpLifetimeStart", pointer, size)
    case Instruction.LifetimeStop(pointer, size)  => lifetime(site, "OpLifetimeStop", pointer, size)
    case Instruction.GroupAsyncCopy(t, d, scope, destination, source, count, stride, _) =>
      groupAsyncCopy(
        resultType = id(t),
        dest = id(d),
        scopeId = id(scope),
        destinationId = id(destination),
        sourceId = id(source),
        countId = id(count),
        strideId = id(stride)
      )
    case Instruction.GroupWaitEvents(scope, count, events) => groupWaitEvents(id(scope), id(count), id(events))
    case Instruction.UntypedGroupAsyncCopyKHR(
          resultType = t,
          dest = d,
          execution = scope,
          destination = destination,
          src = source,
          elementNumBytes = element,
          numElements = count,
          stride = stride
        ) =>
      groupAsyncCopy(
        resultType = id(t),
        dest = id(d),
        scopeId = id(scope),
        destinationId = id(destination),
        sourceId = id(source),
        countId = id(count),
        strideId = id(stride),
        elementId = Some(id(element))
      )
  }

  /** InvalidRequest faults from a memory access name the instruction that made it. */
  private def located(error: Fault | AddressSpace.Pending.type, execution: InvocationState, instruction: Instruction)
      : Fault | AddressSpace.Pending.type = error match {
    case fault: Fault => locatedFault(fault, execution, instruction)
    case pending      => pending
  }

  private def locatedFault(error: Fault, execution: InvocationState, instruction: Instruction): Fault = error match {
    case Fault.InvalidRequest(message) =>
      Fault.InvalidRequest(s"$message at function %${execution.currentFunction.id}, pc ${execution.pc} ($instruction)")
    case _ => error
  }

  /** SPV_INTEL_variable_length_array: `length` elements of `element` in function storage, released as the function's
    * variables are or by OpRestoreMemoryINTEL.
    */
  private def variableLengthArray(site: Site, resultType: Int, dest: Int, element: Int, length: Int): Op = {
    val program = site.program
    val size    = program.typeSize(element).toLong
    step { (execution, _) =>
      scalar(execution.values, length).flatMap { count =>
        val bytes = count * size
        if (count < 0L || bytes > Int.MaxValue.toLong)
          Left(Fault.InvalidRequest(s"OpVariableLengthArrayINTEL length $count is outside private storage"))
        else
          execution.storage.allocate(bytes.toInt, program.typeAlignment(element)).map(address =>
            execution.values.update(dest, VMValue.Address(resultType, Pointer(address, element, StorageClass.Function)))
          )
      }
    }
  }

  private def variable(site: Site, resultType: Int, dest: Int, storageClass: Int, initialiser: Option[Int]): Op = {
    val program = site.program
    val pointee = program.pointerPointee(resultType)
    val size    = program.typeSize(pointee).raw
    val bounded = program.vulkan && (storageClass == StorageClass.Private || storageClass == StorageClass.Function)
    step { (execution, memory) =>
      execution.storage.allocate(size, program.typeAlignment(pointee)).flatMap { address =>
        val pointer =
          Pointer(address, pointee, storageClass, bounds = Option.when(bounded)(address -> (address + size)))
        val stored = initialiser.flatMap(execution.values.get) match {
          case Some(VMValue.Address(_, value)) => memory.writePointer(pointer, value)
          case Some(value) => program.encode(value).flatMap(encoded => memory.write(pointer, encoded.take(size)))
          case None        => Right(())
        }
        stored.map(_ => execution.values.update(dest, VMValue.Address(resultType, pointer)))
      }
    }
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

  private def load(site: Site, instruction: Instruction, resultType: Int, dest: Int, pointer: Int): Op = {
    val program = site.program
    program.runtimeType(resultType) match {
      case Some(RuntimeType.MultisampledTexture(
            _,
            _
          ) | RuntimeType.InputAttachment(_, _) | _: RuntimeType.StorageImage |
          _: RuntimeType.Texture | RuntimeType.CubeTexture(_, _) | RuntimeType.Sampler) =>
        step((execution, _) =>
          address(execution.values, pointer).map(source =>
            execution.values.update(dest, VMValue.Address(resultType, source))
          )
        )
      // GLSL samplerBuffer types use OpTypeSampledImage, but Vulkan uniform texel buffers carry a view without a sampler.
      case Some(RuntimeType.SampledImage(imageType)) if program.runtimeType(imageType).exists {
            case RuntimeType.Texture(texelBuffer = true) => true
            case _                                       => false
          } =>
        step((execution, _) =>
          address(execution.values, pointer).map(source =>
            execution.values.update(dest, VMValue.Address(resultType, source.copy(pointee = imageType)))
          )
        )
      case Some(RuntimeType.SampledImage(imageType)) =>
        step((execution, memory) =>
          address(execution.values, pointer).flatMap(ImageOps.sampledImage(memory, _, imageType)).map(image =>
            execution.values.update(dest, VMValue.Address(resultType, image))
          )
        )
      case Some(RuntimeType.Pointer(_, _)) =>
        step((execution, memory) =>
          address(execution.values, pointer).flatMap(loadPointer(program, memory, _, resultType))
            .left.map(located(_, execution, instruction)).map(execution.values.update(dest, _))
        )
      case Some(RuntimeType.Bool | RuntimeType.Integer(_, _) | RuntimeType.Floating(_) | RuntimeType.Float8(_)) =>
        program.scalarMemoryWidth(resultType) match {
          case Left(error) => Fail(error)
          case Right(size) => ScalarLoad(dest, resultType, pointer, size)
        }
      case _ =>
        val size = program.typeSize(resultType)
        step { (execution, memory) =>
          address(execution.values, pointer).flatMap { source =>
            if (mappedMatrixStorage(program, source))
              loadMatrixStorage(program, memory, source).map(value =>
                execution.values.update(dest, retag(value, resultType))
              )
            else memory.read(
              source,
              size
            ).flatMap(bytes => program.decode(resultType, bytes)).map(execution.values.update(dest, _))
          }
        }
    }
  }

  private class ScalarLoad(dest: Int, resultType: Int, pointer: Int, size: Int in 1 ~ 8) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      address(execution.values, pointer) match {
        case Left(error)   => Left(error)
        case Right(target) =>
          if (memory.readLocalScalar(target, size, execution.values.scalars, dest)) {
            execution.pc += 1
            Done
          } else
            memory.readScalar(target, size) match {
              case Left(error)   => Left(error)
              case Right(loaded) =>
                execution.values.updateScalar(dest, resultType, loaded)
                execution.pc += 1
                Done
            }
      }
  }

  private def store(site: Site, instruction: Instruction, pointer: Int, source: Int): Op = {
    val program = site.program
    if (program.scalarSlot(source))
      program.scalarMemoryWidth(program.valueType(source)) match {
        case Left(error) => Fail(error)
        case Right(size) => ScalarStore(instruction, pointer, source, size)
      }
    else
      step { (execution, memory) =>
        val values = execution.values
        address(values, pointer).flatMap { target =>
          values.get(source) match {
            case Some(VMValue.Address(sourceType, value)) => storePointer(program, memory, target, sourceType, value)
            case Some(value: VMValue.Bits) if mappedMatrixStorage(program, target) =>
              storeMatrixStorage(program, memory, target, value)
            case Some(value) =>
              program.encode(value).flatMap(memory.write(target, _).left.map(locatedFault(_, execution, instruction)))
            case None => Left(undefined(source))
          }
        }
      }
  }

  private class ScalarStore(instruction: Instruction, pointer: Int, source: Int, size: Int in 1 ~ 8) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = {
      val values = execution.values
      address(values, pointer) match {
        case Left(error)   => Left(error)
        case Right(target) =>
          val bits = values.scalars(source)
          memory.writeScalar(target, bits, size) match {
            case Left(error) => Left(located(error, execution, instruction))
            case Right(())   =>
              execution.pc += 1
              Done
          }
      }
    }
  }

  private def copyMemory(site: Site, target: Int, source: Int): Op = {
    val program = site.program
    step { (execution, memory) =>
      for {
        destination <- address(execution.values, target)
        origin      <- address(execution.values, source)
        _           <-
          if (mappedMatrixStorage(program, destination) || mappedMatrixStorage(program, origin))
            loadMatrixStorage(program, memory, origin).flatMap(storeMatrixStorage(program, memory, destination, _))
          else memory.copy(destination, origin, program.typeSize(origin.pointee))
      } yield ()
    }
  }

  private def copyMemorySized(target: Int, source: Int, size: Int): Op = step { (execution, memory) =>
    for {
      destination <- address(execution.values, target)
      origin      <- address(execution.values, source)
      rawSize     <- scalar(execution.values, size)
      byteCount   <- rawSize.refine[Long in 0L ~ 2147483647L]
        .left.map(_ => Fault.InvalidRequest(s"OpCopyMemorySized byte count $rawSize is outside the supported range"))
      _ <- memory.copy(destination, origin, byteCount.toIntExact.widen)
    } yield ()
  }

  // Access chains walk nested types with a cursor and fail at the first invalid index.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.null"))
  private def accessChain(
      site: Site,
      resultTypeOperand: Operand,
      destOperand: Operand,
      baseOperand: Operand,
      elementOperand: Option[Operand],
      indexOperands: Vec[Operand],
      baseType: Option[Int] = None
  ): Op = {
    val resultType = id(resultTypeOperand)
    val dest       = id(destOperand)
    val base       = id(baseOperand)
    val element    = elementOperand.map(id(_))
    val indexes    = indexOperands.map(id(_))
    val program    = site.program
    val pointee    = program.pointerPointee(resultType)
    val baseStride = program.arrayStrides.get(program.valueType(base))
    // an untyped chain walks its base type, and its result points at the type it reaches
    val walked = program.untyped(pointee)
    step { (execution, _) =>
      val values = execution.values
      address(values, base) match {
        case Left(error)  => Left(error)
        case Right(start) =>
          var source                = start
          var matrixLayout          = start.matrixLayout
          var offset                = start.address
          var typeId                = baseType.getOrElse(start.pointee)
          var position              = 0
          var failure: Fault | Null = null
          element match {
            case Some(value) =>
              scalar(values, value) match {
                case Left(error)  => failure = error
                case Right(first) =>
                  offset += first *
                    (baseStride match {
                      case Some(stride) => stride.raw
                      case None         => program.storageTypeSize(typeId, start.matrixLayout).raw
                    })
              }
            case None => ()
          }
          while (position < indexes.size && failure == null) {
            // Scalar index slots are read unboxed rather than through `scalar`, which wraps each index in an Either.
            val indexId = indexes(position)
            val index   =
              if (program.scalarSlot(indexId)) values.scalars(indexId)
              else scalar(values, indexId) match {
                case Right(value) => value
                case Left(error)  =>
                  failure = error
                  0L
              }
            if (failure == null) program.runtimeType(typeId) match {
              case Some(RuntimeType.Structure(members)) =>
                if (index < 0L || index >= members.size.toLong)
                  failure = Fault.InvalidRequest(s"structure member index $index is out of range")
                else {
                  offset += program.structMemberOffset(typeId, members, index.toInt)
                  matrixLayout = program.memberMatrixLayouts.get(typeId -> index.toInt)
                  typeId = members(index.toInt)
                }
              case Some(RuntimeType.Array(member, _)) =>
                source.descriptors match {
                  case Some(descriptors) =>
                    if (index < 0 || index >= descriptors.size || element.nonEmpty)
                      failure = Fault.InvalidRequest(s"descriptor array index $index is out of range")
                    else {
                      source = descriptors(index.toInt)
                      offset = source.address
                    }
                  case None =>
                    offset += index *
                      (program.arrayStrides.get(typeId) match {
                        case Some(stride) => stride.raw
                        case None         => program.storageTypeSize(member, matrixLayout).raw
                      })
                }
                typeId = member
              case Some(RuntimeType.MatrixType(column, _)) =>
                val stride =
                  if (matrixLayout.exists(_.rowMajor)) program.runtimeType(column) match {
                    case Some(RuntimeType.VectorType(component, _)) => program.typeSize(component).raw
                    case _                                          =>
                      failure = Fault.InvalidRequest("invalid matrix column type")
                      0
                  }
                  else matrixLayout.fold(program.typeSize(column).raw)(_.stride.raw)
                offset += index * stride
                matrixLayout = matrixLayout.filter(_.rowMajor)
                typeId = column
              case Some(RuntimeType.VectorType(component, _)) =>
                offset += index *
                  matrixLayout.filter(_.rowMajor).map(_.stride.raw).getOrElse(program.typeSize(component).raw)
                matrixLayout = None
                typeId = component
              case Some(value) => failure = Fault.InvalidRequest(s"cannot index through SPIR-V type $value")
              case None        => failure = Fault.InvalidRequest(s"SPIR-V type %$typeId is undefined")
            }
            position += 1
          }
          if (failure != null) Left(failure)
          else {
            values.update(
              dest,
              VMValue.Address(
                resultType,
                source.copy(address = offset, pointee = if (walked) typeId else pointee, matrixLayout = matrixLayout)
              )
            )
            Done
          }
      }
    }
  }

  private val castable = Set(StorageClass.Workgroup, StorageClass.CrossWorkgroup, StorageClass.Function)

  private def castToGeneric(site: Site, resultType: Int, dest: Int, source: Int): Op = {
    val program = site.program
    program.runtimeType(resultType) match {
      case Some(RuntimeType.Pointer(StorageClass.Generic, pointee)) =>
        step { (execution, _) =>
          execution.values.get(source) match {
            case Some(VMValue.Address(sourceType, pointer)) =>
              program.runtimeType(sourceType) match {
                case Some(RuntimeType.Pointer(storageClass, sourcePointee))
                    if castable.contains(storageClass) && sourcePointee == pointee &&
                      (pointer.pointee == pointee || program.untyped(pointee)) =>
                  Right(execution.values.update(
                    dest,
                    VMValue.Address(resultType, pointer.copy(storageClass = StorageClass.Generic))
                  ))
                case Some(RuntimeType.Pointer(storageClass, sourcePointee)) =>
                  Left(Fault.InvalidRequest(
                    s"OpPtrCastToGeneric cannot cast storage class $storageClass pointee %$sourcePointee to %$pointee"
                  ))
                case _ => Left(Fault.InvalidRequest("OpPtrCastToGeneric source type is not a pointer"))
              }
            case None    => Left(undefined(source))
            case Some(_) => Left(Fault.InvalidRequest("OpPtrCastToGeneric source is not a pointer"))
          }
        }
      case Some(RuntimeType.Pointer(storageClass, _)) =>
        Fail(
          Fault.InvalidRequest(s"OpPtrCastToGeneric result pointer has storage class $storageClass instead of Generic")
        )
      case _ => Fail(Fault.InvalidRequest(s"OpPtrCastToGeneric result type %$resultType is undefined"))
    }
  }

  // A generic pointer into another storage class casts to null.
  private def castFromGeneric(
      site: Site,
      opcode: String,
      resultType: Int,
      dest: Int,
      source: Int,
      storage: Option[Int]
  ): Op = {
    val program = site.program
    program.runtimeType(resultType) match {
      case Some(RuntimeType.Pointer(targetStorage, targetPointee))
          if castable.contains(targetStorage) && storage.forall(_ == targetStorage) =>
        step { (execution, _) =>
          execution.values.get(source) match {
            case Some(VMValue.Address(sourceType, pointer)) =>
              program.runtimeType(sourceType) match {
                case Some(RuntimeType.Pointer(StorageClass.Generic, sourcePointee))
                    if sourcePointee == targetPointee &&
                      (pointer.pointee == targetPointee || program.untyped(targetPointee)) =>
                  val named    = storageClassOf(pointer.address) == targetStorage
                  val retained = if (storage.isEmpty) pointer.address != 0L && named else pointer.address == 0L || named
                  val converted =
                    if (retained)
                      pointer.copy(
                        pointee = if (program.untyped(targetPointee)) pointer.pointee else targetPointee,
                        storageClass = targetStorage
                      )
                    else Pointer(0L, targetPointee, targetStorage)
                  Right(execution.values.update(dest, VMValue.Address(resultType, converted)))
                case Some(RuntimeType.Pointer(sourceStorage, sourcePointee)) =>
                  Left(Fault.InvalidRequest(
                    s"$opcode cannot cast storage class $sourceStorage pointee %$sourcePointee" +
                      (if (storage.isEmpty) s" to storage class $targetStorage pointee %$targetPointee" else "")
                  ))
                case _ => Left(Fault.InvalidRequest(s"$opcode source type is not a pointer"))
              }
            case None    => Left(undefined(source))
            case Some(_) => Left(Fault.InvalidRequest(s"$opcode source is not a pointer"))
          }
        }
      case Some(RuntimeType.Pointer(targetStorage, _)) =>
        Fail(Fault.InvalidRequest(storage match {
          case None           => s"$opcode result pointer has invalid storage class $targetStorage"
          case Some(explicit) =>
            s"$opcode requires Workgroup, CrossWorkgroup, or Function storage matching result type; " +
              s"got storage $explicit and result storage $targetStorage"
        }))
      case _ => Fail(Fault.InvalidRequest(s"$opcode result type %$resultType is undefined"))
    }
  }

  private def genericSemantics(site: Site, resultType: Int, dest: Int, source: Int): Op = {
    val program = site.program
    program.integerShape(resultType) match {
      case Some((_, 1)) =>
        step { (execution, _) =>
          execution.values.get(source) match {
            case Some(VMValue.Address(sourceType, pointer))
                if program.runtimeType(sourceType).exists {
                  case RuntimeType.Pointer(StorageClass.Generic, _) => true
                  case _                                            => false
                } =>
              val semantics = storageClassOf(pointer.address) match {
                case StorageClass.Workgroup      => MemorySemantics.WorkgroupMemory
                case StorageClass.CrossWorkgroup => MemorySemantics.CrossWorkgroupMemory
                case _                           => 0
              }
              Right(execution.values.updateScalar(dest, resultType, semantics.toLong))
            case None    => Left(undefined(source))
            case Some(_) => Left(Fault.InvalidRequest("OpGenericPtrMemSemantics source is not a Generic pointer"))
          }
        }
      case _ => Fail(Fault.InvalidRequest("OpGenericPtrMemSemantics requires a scalar integer result"))
    }
  }

  private def pointerToInteger(site: Site, resultType: Int, dest: Int, pointer: Int): Op =
    site.program.integerShape(resultType) match {
      case Some((width, 1)) =>
        val bits = mask(width)
        step((execution, _) =>
          address(execution.values, pointer).map(value =>
            execution.values.updateScalar(dest, resultType, value.address & bits)
          )
        )
      // SPV_INTEL_masked_gather_scatter's vectors of pointers hold their addresses as lanes
      case Some((width, lanes)) if site.program.runtimeType(site.program.valueType(pointer)).exists {
            case RuntimeType.VectorType(_, count) => count.raw == lanes
            case _                                => false
          } =>
        val bits = mask(width)
        step((execution, _) =>
          Op.bits(execution.values, pointer).map(addresses =>
            execution.values.update(dest, VMValue.Bits(resultType, addresses.map(_ & bits)))
          )
        )
      case _ => Fail(Fault.InvalidRequest("OpConvertPtrToU requires a scalar integer result"))
    }

  private def integerToPointer(site: Site, resultType: Int, dest: Int, source: Int): Op = {
    val program = site.program
    program.runtimeType(resultType) match {
      case Some(RuntimeType.Pointer(storageClass, pointee)) =>
        val function = program.runtimeType(pointee).exists {
          case RuntimeType.Function(_, _) => true
          case _                          => false
        }
        step { (execution, _) =>
          execution.values.get(source) match {
            case Some(VMValue.Bits(integerType, Array(rawAddress)))
                if program.integerShape(integerType).exists(_._2 == 1) =>
              if (function) Right(execution.values.update(dest, VMValue.Bits(resultType, Array(rawAddress))))
              else {
                val address = rawAddress & mask(program.scalarWidth(integerType))
                Right(execution.values.update(
                  dest,
                  VMValue.Address(resultType, Pointer(address, pointee, storageClass))
                ))
              }
            case Some(_) => Left(Fault.InvalidRequest("OpConvertUToPtr requires a scalar integer operand"))
            case None    => Left(undefined(source))
          }
        }
      case Some(RuntimeType.VectorType(component, count)) if program.runtimeType(component).exists(isPointer) =>
        step { (execution, _) =>
          execution.values.get(source) match {
            case Some(VMValue.Bits(integerType, lanes)) if lanes.length == count.raw =>
              val bits = mask(program.scalarWidth(integerType))
              Right(execution.values.update(dest, VMValue.Bits(resultType, lanes.map(_ & bits))))
            case Some(_) => Left(Fault.InvalidRequest("OpConvertUToPtr requires an integer vector of its lane count"))
            case None    => Left(undefined(source))
          }
        }
      case Some(_) => Fail(Fault.InvalidRequest("OpConvertUToPtr requires a pointer result type"))
      case None    => Fail(Fault.InvalidRequest(s"OpConvertUToPtr result type %$resultType is undefined"))
    }
  }

  /** OpSizeOf: the size of the pointee type, as its arrays and structures lay it out in memory. */
  private def sizeOf(site: Site, resultType: Int, dest: Int, pointer: Operand): Op = typed(site, pointer) match {
    case Typed.Undefined(value)  => Fail(undefined(value))
    case Typed.Data(value, _)    => Fail(Fault.InvalidRequest(s"OpSizeOf operand %$value is not a pointer"))
    case Typed.Opaque(_, typeId) => site.program.runtimeType(typeId) match {
        case Some(RuntimeType.Pointer(_, pointee)) =>
          val size = site.program.typeSize(pointee).toLong
          step((execution, _) => Right(execution.values.updateScalar(dest, resultType, size)))
        case _ => Fail(Fault.InvalidRequest(s"OpSizeOf operand %${id(pointer)} is not a pointer"))
      }
  }

  private def arrayLength(resultType: Int, dest: Int, structure: Int, member: Int, declared: Option[Int] = None): Op =
    step { (execution, _) =>
      val program = execution.values.program
      val result  = execution.values.get(structure) match {
        case Some(VMValue.Address(_, located)) =>
          val pointer = declared.fold(located)(typeId => located.copy(pointee = typeId))
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
      result.map(execution.values.updateScalar(dest, resultType, _))
    }

  /** Lifetime markers carry no effect, but a malformed marker indicates a corrupt module. */
  private def lifetime(site: Site, opcode: String, pointerOperand: Operand, sizeOperand: Operand): Op =
    (pointerOperand.words, sizeOperand.words) match {
      case (Vec(pointerId), Vec(sizeWord)) =>
        val size = sizeWord.toUnsigned.toLong
        step { (execution, memory) =>
          execution.values.get(pointerId.raw) match {
            case Some(VMValue.Address(pointerType, pointer)) => site.program.runtimeType(pointerType) match {
                case Some(RuntimeType.Pointer(_, pointee))
                    if pointee == pointer.pointee || site.program.untyped(pointee) =>
                  Either.cond(
                    memory.contains(pointer, size),
                    (),
                    Fault.InvalidRequest(
                      s"$opcode byte range 0x${java.lang.Long.toHexString(pointer.address)} + $size exceeds its storage"
                    )
                  )
                case Some(_) => Left(Fault.InvalidRequest(s"$opcode target %$pointerId does not have pointer type"))
                case None    => Left(Fault.InvalidRequest(s"$opcode target %$pointerId has undefined pointer type"))
              }
            case Some(_) => Left(Fault.InvalidRequest(s"$opcode target %$pointerId is not a pointer"))
            case None    => Left(Fault.InvalidRequest(s"$opcode target %$pointerId is undefined"))
          }
        }
      case (pointerWords, _) if pointerWords.size != 1 =>
        Fail(Fault.InvalidRequest(s"$opcode pointer must contain one word, found ${pointerWords.size}"))
      case (_, sizeWords) => Fail(Fault.InvalidRequest(s"$opcode size must contain one word, found ${sizeWords.size}"))
    }

  // Group copies advance byte ranges directly rather than constructing intermediate slices.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def groupAsyncCopy(
      resultType: Int,
      dest: Int,
      scopeId: Int,
      destinationId: Int,
      sourceId: Int,
      countId: Int,
      strideId: Int,
      elementId: Option[Int] = None
  )
      : Op = step { (execution, memory) =>
    val values  = execution.values
    val program = values.program
    for {
      element     <- elementId.fold[Either[Fault, Option[Long]]](Right(None))(scalar(values, _).map(Some(_)))
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
          Right(values.updateScalar(dest, resultType, 0L))
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
            }.map(_ => values.updateScalar(dest, resultType, 0L))
          } catch {
            case _: ArithmeticException | _: IllegalArgumentException =>
              Left(Fault.InvalidRequest("OpGroupAsyncCopy byte range is too large"))
          }
      }
    } yield ()
  }

  private def groupWaitEvents(scopeId: Int, countId: Int, events: Int): Op = step { (execution, _) =>
    val values = execution.values
    for {
      scope <- scalar(values, scopeId)
      count <- scalar(values, countId)
      _     <- address(values, events)
      _     <- Either.cond(
        scope == Scope.Workgroup,
        (),
        Fault.Unsupported(s"OpGroupWaitEvents execution scope $scope is not Workgroup")
      )
      _ <- Either.cond(count >= 0L, (), Fault.InvalidRequest(s"OpGroupWaitEvents has invalid event count $count"))
    } yield ()
  }

  /** Binds one kernel parameter from its relocated argument bytes. By-value copies join the private image, local
    * arguments name their reserved workgroup storage.
    */
  def bindArgument(
      program: VMProgram,
      parameterType: Int,
      byValue: Boolean,
      argument: KernelTask.Argument,
      bytes: Array[Byte],
      relocatedAtZero: Boolean,
      local: Option[Long],
      privateImage: Layout
  ): Either[Fault, VMValue] = program.runtimeType(parameterType) match {
    case Some(RuntimeType.Pointer(storageClass, pointee)) =>
      def pointer(address: Long): VMValue = VMValue.Address(parameterType, Pointer(address, pointee, storageClass))
      argument match {
        case KernelTask.Argument.Memory(_, _, _) => Right(pointer(LittleEndian.decode(bytes, 0, 8)))
        case KernelTask.Argument.Scalar(original)
            if !byValue && original.forall(_ == 0) && !program.runtimeType(pointee).exists {
              case RuntimeType.Structure(_) | RuntimeType.Array(_, _) | RuntimeType.MatrixType(_, _) => true
              case _                                                                                 => false
            } =>
          Right(pointer(0L))
        // A ByVal aggregate can contain a pointer in opaque byte-array storage at offset zero.
        // That relocation belongs inside the private copy, not to the parameter pointer itself.
        case KernelTask.Argument.Scalar(original)
            if !byValue && relocatedAtZero &&
              (program.pointerLeafOffsets(pointee, 0L).toOption.exists(_.isEmpty) ||
                program.typeSize(pointee) > original.size) =>
          Right(pointer(LittleEndian.decode(bytes, 0, 8)))
        case KernelTask.Argument.Scalar(original)
            if !byValue && original.size == 8 &&
              (storageClass == StorageClass.CrossWorkgroup || storageClass == StorageClass.Generic) =>
          Right(pointer(LittleEndian.decode(bytes, 0, 8)))
        case KernelTask.Argument.Scalar(_) =>
          val copy = Array.ofDim[Byte](math.max(program.typeSize(pointee), bytes.length))
          Array.copy(bytes, 0, copy, 0, bytes.length)
          privateImage.append(Vec.unsafeWrapArray(copy), program.typeAlignment(pointee)).map(pointer(_))
        case KernelTask.Argument.Local(_) =>
          local.toRight(Fault.InvalidRequest("SPIR-V local parameter has no workgroup storage")).map(pointer(_))
        case KernelTask.Argument.Descriptor =>
          Left(Fault.InvalidRequest("SPIR-V parameter has an unresolved descriptor"))
      }
    case Some(
          RuntimeType.StorageImage(_, _, _, _, _, _) | RuntimeType.Texture(_, _, _, _) | RuntimeType.Sampler |
          RuntimeType.SampledImage(_) | RuntimeType.Pipe(_)
        ) =>
      argument match {
        case KernelTask.Argument.Memory(_, _, range) =>
          val address = LittleEndian.decode(bytes, 0, 8)
          Right(VMValue.Address(
            parameterType,
            Pointer(
              address,
              parameterType,
              StorageClass.UniformConstant,
              range.map(size => address -> (address + size))
            )
          ))
        case other => Left(Fault.InvalidRequest(s"SPIR-V image or sampler parameter cannot bind $other"))
      }
    case Some(_) => argument match {
        case KernelTask.Argument.Scalar(_) => program.decode(parameterType, bytes)
        // Some DPC++ Level Zero kernels lower a local pointer parameter to a 64-bit address and convert it back with
        // OpConvertUToPtr.
        case KernelTask.Argument.Local(_) =>
          local.toRight(Fault.InvalidRequest("SPIR-V local parameter has no workgroup storage"))
            .map(address => VMValue.Bits(parameterType, Array(address)))
        case other => Left(Fault.InvalidRequest(s"scalar SPIR-V parameter cannot bind $other"))
      }
    case None => Left(Fault.InvalidRequest(s"SPIR-V parameter type %$parameterType is undefined"))
  }
}
