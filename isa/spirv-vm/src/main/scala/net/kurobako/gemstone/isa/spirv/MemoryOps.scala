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
import net.kurobako.gemstone.vm.*

/** Variables, loads, stores, copies, access chains, pointer conversions and kernel argument binding. */
private object MemoryOps {
  import Operand.*

  def lower(using site: Site): PartialFunction[Instruction, Op] = {
    case Instruction.Variable(resultType, dest, storageClass, initialiser) =>
      Variable(id(dest), id(resultType), id(storageClass), initialiser.map(id(_)))
    case instruction @ Instruction.Load(resultType, dest, pointer, _) =>
      load(instruction, ResultIds(resultType, dest), id(pointer))
    case instruction @ Instruction.Store(pointer, source, _)     => store(instruction, id(pointer), id(source))
    case Instruction.CopyMemory(target, source, _, _)            => CopyMemory(id(target), id(source))
    case Instruction.CopyMemorySized(target, source, size, _, _) => CopyMemorySized(id(target), id(source), id(size))
    case Instruction.UntypedVariableKHR(_, dest, storageClass, Some(_), initialiser) =>
      Variable(id(dest), site.program.valueType(id(dest)), id(storageClass), initialiser.map(id(_)))
    case Instruction.UntypedVariableKHR(dest = dest) =>
      Fail(Fault.InvalidRequest(s"OpUntypedVariableKHR %${id(dest)} in a function declares no data type"))
    case Instruction.UntypedAccessChainKHR(t, d, baseType, base, indexes) =>
      accessChain(t, d, base, None, indexes, Some(id(baseType)))
    case Instruction.UntypedInBoundsAccessChainKHR(t, d, baseType, base, indexes) =>
      accessChain(t, d, base, None, indexes, Some(id(baseType)))
    case Instruction.UntypedPtrAccessChainKHR(t, d, baseType, base, element, indexes) =>
      accessChain(t, d, base, Some(element), indexes, Some(id(baseType)))
    case Instruction.UntypedInBoundsPtrAccessChainKHR(t, d, baseType, base, element, indexes) =>
      accessChain(t, d, base, Some(element), indexes, Some(id(baseType)))
    case Instruction.UntypedArrayLengthKHR(t, d, structure, pointer, member) =>
      arrayLength(ResultIds(t, d), id(pointer), unsigned(member).toInt, Some(id(structure)))
    case Instruction.UntypedPrefetchKHR(pointerType = pointer) => prefetch(id(pointer))
    case Instruction.VariableLengthArrayINTEL(t, _, _) if site.program.runtimeType(id(t)).exists {
          case RuntimeType.Pointer(_, pointee) => site.program.untyped(pointee)
          case _                               => false
        } =>
      Fail(Fault.Unsupported(
        "OpVariableLengthArrayINTEL of an untyped pointer has no element type; OpUntypedVariableLengthArrayINTEL names it"
      ))
    case Instruction.VariableLengthArrayINTEL(t, d, length) =>
      VariableLengthArray(id(d), id(t), site.program.pointerPointee(id(t)), id(length))
    case Instruction.UntypedVariableLengthArrayINTEL(_, d, element, length) =>
      VariableLengthArray(id(d), site.program.valueType(id(d)), id(element), id(length))
    case Instruction.SaveMemoryINTEL(t, d)                    => SaveMemory(id(d), id(t))
    case Instruction.RestoreMemoryINTEL(pointer)              => RestoreMemory(id(pointer))
    case Instruction.AccessChain(t, d, base, indexes)         => accessChain(t, d, base, None, indexes)
    case Instruction.InBoundsAccessChain(t, d, base, indexes) =>
      accessChain(t, d, base, None, indexes)
    case Instruction.PtrAccessChain(t, d, base, element, indexes) =>
      accessChain(t, d, base, Some(element), indexes)
    case Instruction.InBoundsPtrAccessChain(t, d, base, element, indexes) =>
      accessChain(t, d, base, Some(element), indexes)
    case instruction @ Instruction.PtrEqual(resultType = _)    => staged(site, instruction, CoreFamily.Pointer)
    case instruction @ Instruction.PtrNotEqual(resultType = _) => staged(site, instruction, CoreFamily.Pointer)
    case instruction @ Instruction.PtrDiff(resultType = t)     =>
      site.program.integerShape(id(t)) match {
        case Some((_, 1)) => staged(site, instruction, CoreFamily.Pointer)
        case _            => Fail(Fault.InvalidRequest("OpPtrDiff requires a scalar integer result"))
      }
    case Instruction.PtrCastToGeneric(t, d, a) => castToGeneric(ResultIds(t, d), id(a))
    case Instruction.GenericCastToPtr(t, d, a) =>
      castFromGeneric("OpGenericCastToPtr", ResultIds(t, d), id(a), None)
    case Instruction.GenericCastToPtrExplicit(t, d, a, storage) =>
      castFromGeneric("OpGenericCastToPtrExplicit", ResultIds(t, d), id(a), Some(id(storage)))
    case Instruction.GenericPtrMemSemantics(t, d, a)      => genericSemantics(ResultIds(t, d), id(a))
    case Instruction.ConvertPtrToU(t, d, a)               => pointerToInteger(ResultIds(t, d), id(a))
    case Instruction.ConvertUToPtr(t, d, a)               => integerToPointer(ResultIds(t, d), id(a))
    case Instruction.ArrayLength(t, d, structure, member) =>
      arrayLength(ResultIds(t, d), id(structure), unsigned(member).toInt)
    case Instruction.SizeOf(t, d, pointer)        => sizeOf(ResultIds(t, d), pointer)
    case Instruction.LifetimeStart(pointer, size) => lifetime("OpLifetimeStart", pointer, size)
    case Instruction.LifetimeStop(pointer, size)  => lifetime("OpLifetimeStop", pointer, size)
    case Instruction.GroupAsyncCopy(t, d, scope, destination, source, count, stride, _) =>
      GroupAsyncCopy(id(d), id(t), id(scope), id(destination), id(source), id(count), id(stride), -1)
    case Instruction.GroupWaitEvents(scope, count, events) => GroupWaitEvents(id(scope), id(count), id(events))
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
      GroupAsyncCopy(id(d), id(t), id(scope), id(destination), id(source), id(count), id(stride), id(element))
  }

  /** SPV_INTEL_variable_length_array: `length` elements of `element` in function storage, released as the function's
    * variables are or by OpRestoreMemoryINTEL.
    */
  private class VariableLengthArray(target: Int, typeId: Int, element: Int, length: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      VariableSemantics.variableLengthArray(InvocationMachine(execution))(target, typeId, element, length)
  }

  private class Variable(target: Int, typeId: Int, storageClass: Int, initialiser: Option[Int]) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      VariableSemantics.variable(InvocationMachine(execution))(target, typeId, storageClass, initialiser)
  }

  private class SaveMemory(target: Int, typeId: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      VariableSemantics.saveMemory(InvocationMachine(execution))(target, typeId)
  }

  private class RestoreMemory(pointer: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      VariableSemantics.restoreMemory(InvocationMachine(execution))(pointer)
  }

  private def load(instruction: Instruction, ids: ResultIds, pointer: Int)(using site: Site): Op = {
    val program = site.program
    program.runtimeType(ids.resultType) match {
      case Some(RuntimeType.MultisampledTexture(_) | RuntimeType.InputAttachment(
            _,
            _
          ) | RuntimeType.StorageImage(_, _, _, _, _, _) |
          RuntimeType.Texture(_, _, _) | RuntimeType.CubeTexture(_) | RuntimeType.Sampler | RuntimeType.Tensor(_, _)) =>
        LoadHandle(pointer, ids.dest, ids.resultType, -1)
      // GLSL samplerBuffer types use OpTypeSampledImage, but Vulkan uniform texel buffers carry a view without a sampler.
      case Some(RuntimeType.SampledImage(imageType)) if program.runtimeType(imageType).exists {
            case RuntimeType.Texture(texelBuffer = true) => true
            case _                                       => false
          } =>
        LoadHandle(pointer, ids.dest, ids.resultType, imageType)
      case Some(RuntimeType.SampledImage(imageType)) => LoadSampled(pointer, ids.dest, ids.resultType, imageType)
      case Some(RuntimeType.Pointer(_, _))           => LoadAddress(pointer, ids.dest, ids.resultType, instruction)
      case Some(RuntimeType.Bool | RuntimeType.Integer(_, _) | RuntimeType.Floating(_) | RuntimeType.Float8(_)) =>
        program.scalarMemoryWidth(ids.resultType) match {
          case Left(error) => Fail(error)
          case Right(size) => ScalarLoad(ids.dest, ids.resultType, pointer, size)
        }
      case _ => LoadValue(pointer, ids.dest, ids.resultType, program.typeSize(ids.resultType))
    }
  }

  private class LoadHandle(pointer: Int, target: Int, typeId: Int, pointee: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      MemoryAccessSemantics.handleLoad(InvocationMachine(execution))(pointer, target, typeId, pointee)
  }

  private class LoadSampled(pointer: Int, target: Int, typeId: Int, imageType: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      MemoryAccessSemantics.sampledLoad(InvocationMachine(execution))(pointer, target, typeId, imageType)
  }

  private class LoadAddress(pointer: Int, target: Int, typeId: Int, instruction: Instruction) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      MemoryAccessSemantics.addressLoad(InvocationMachine(execution))(pointer, target, typeId, instruction)
  }

  private class LoadValue(pointer: Int, target: Int, typeId: Int, size: Int >= 0) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      MemoryAccessSemantics.valueLoad(InvocationMachine(execution))(pointer, target, typeId, size)
  }

  private class ScalarLoad(dest: Int, resultType: Int, pointer: Int, size: Int in 1 ~ 8) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      MemoryAccessSemantics.scalarLoad(InvocationMachine(execution))(pointer, size, dest, resultType)
  }

  private def store(instruction: Instruction, pointer: Int, source: Int)(using site: Site): Op = {
    val program = site.program
    if (program.scalarSlot(source))
      program.scalarMemoryWidth(program.valueType(source)) match {
        case Left(error) => Fail(error)
        case Right(size) => ScalarStore(instruction, pointer, source, size)
      }
    else StoreValue(instruction, pointer, source)
  }

  private class ScalarStore(instruction: Instruction, pointer: Int, source: Int, size: Int in 1 ~ 8) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      MemoryAccessSemantics.scalarStore(InvocationMachine(execution))(pointer, source, size, instruction)
  }

  private class StoreValue(instruction: Instruction, pointer: Int, source: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      MemoryAccessSemantics.valueStore(InvocationMachine(execution))(pointer, source, instruction)
  }

  private class CopyMemory(target: Int, origin: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      MemoryAccessSemantics.copy(InvocationMachine(execution))(target, origin)
  }

  private class CopyMemorySized(target: Int, origin: Int, size: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      MemoryAccessSemantics.copySized(InvocationMachine(execution))(target, origin, size)
  }

  private class GroupAsyncCopy(
      target: Int,
      typeId: Int,
      scope: Int,
      destination: Int,
      source: Int,
      count: Int,
      stride: Int,
      element: Int
  ) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      MemoryAccessSemantics.groupAsyncCopy(InvocationMachine(execution))(
        target,
        typeId,
        scope,
        destination,
        source,
        count,
        stride,
        element
      )
  }

  private class GroupWaitEvents(scope: Int, count: Int, events: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      MemoryAccessSemantics.waitEvents(InvocationMachine(execution))(scope, count, events)
  }

  private def accessChain(
      resultTypeOperand: Operand,
      destOperand: Operand,
      baseOperand: Operand,
      elementOperand: Option[Operand],
      indexOperands: Vec[Operand],
      baseType: Option[Int] = None
  )(using site: Site): Op = {
    val resultType = id(resultTypeOperand)
    val base       = id(baseOperand)
    val program    = site.program
    AccessChainOp(
      base,
      elementOperand.fold(-1)(id(_)),
      indexOperands.map(id(_)).toArray,
      AccessChain(
        program,
        baseType,
        program.pointerPointee(resultType),
        program.arrayStrides.get(program.valueType(base)).map(_.raw.toLong)
      ),
      id(destOperand),
      resultType
    )
  }

  private class AccessChainOp(
      base: Int,
      element: Int,
      indexes: Array[Int],
      chain: AccessChain,
      dest: Int,
      resultType: Int
  ) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      PointerAccessSemantics.accessChain(InvocationMachine(execution))(base, element, indexes, chain, dest, resultType)
  }

  private def castToGeneric(ids: ResultIds, source: Int)(using site: Site): Op = {
    val program = site.program
    program.runtimeType(ids.resultType) match {
      case Some(RuntimeType.Pointer(StorageClass.Generic, pointee)) =>
        CastToGeneric(
          source,
          PointerCast("OpPtrCastToGeneric", program, pointee, StorageClass.Generic, explicit = false),
          ids.dest,
          ids.resultType
        )
      case Some(RuntimeType.Pointer(storageClass, _)) =>
        Fail(
          Fault.InvalidRequest(s"OpPtrCastToGeneric result pointer has storage class $storageClass instead of Generic")
        )
      case _ => Fail(Fault.InvalidRequest(s"OpPtrCastToGeneric result type %${ids.resultType} is undefined"))
    }
  }

  private class CastToGeneric(source: Int, cast: PointerCast, dest: Int, resultType: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      PointerAccessSemantics.castToGeneric(InvocationMachine(execution))(source, cast, dest, resultType)
  }

  // A generic pointer into another storage class casts to null.
  private def castFromGeneric(
      opcode: String,
      ids: ResultIds,
      source: Int,
      storage: Option[Int]
  )(using site: Site): Op = {
    val program = site.program
    program.runtimeType(ids.resultType) match {
      case Some(RuntimeType.Pointer(targetStorage, targetPointee))
          if Addresses.castable.contains(targetStorage) && storage.forall(_ == targetStorage) =>
        CastFromGeneric(
          source,
          PointerCast(opcode, program, targetPointee, targetStorage, storage.nonEmpty),
          ids.dest,
          ids.resultType
        )
      case Some(RuntimeType.Pointer(targetStorage, _)) =>
        Fail(Fault.InvalidRequest(storage match {
          case None           => s"$opcode result pointer has invalid storage class $targetStorage"
          case Some(explicit) =>
            s"$opcode requires Workgroup, CrossWorkgroup, or Function storage matching result type; " +
              s"got storage $explicit and result storage $targetStorage"
        }))
      case _ => Fail(Fault.InvalidRequest(s"$opcode result type %${ids.resultType} is undefined"))
    }
  }

  private class CastFromGeneric(source: Int, cast: PointerCast, dest: Int, resultType: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      PointerAccessSemantics.castFromGeneric(InvocationMachine(execution))(source, cast, dest, resultType)
  }

  private class GenericSemantics(source: Int, target: Int, typeId: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      VariableSemantics.memorySemantics(InvocationMachine(execution))(source, target, typeId)
  }

  private def genericSemantics(ids: ResultIds, source: Int)(using site: Site): Op =
    site.program.integerShape(ids.resultType) match {
      case Some((_, 1)) => GenericSemantics(source, ids.dest, ids.resultType)
      case _            => Fail(Fault.InvalidRequest("OpGenericPtrMemSemantics requires a scalar integer result"))
    }

  private def pointerToInteger(ids: ResultIds, pointer: Int)(using site: Site): Op =
    site.program.integerShape(ids.resultType) match {
      case Some((width, 1)) => PointerToInteger(pointer, mask(width), ids.dest, ids.resultType)
      // SPV_INTEL_masked_gather_scatter's vectors of pointers hold their addresses as lanes
      case Some((width, lanes)) if site.program.runtimeType(site.program.valueType(pointer)).exists {
            case RuntimeType.VectorType(_, count) => count.raw == lanes
            case _                                => false
          } =>
        PointersToIntegers(pointer, mask(width), ids.dest, ids.resultType)
      case _ => Fail(Fault.InvalidRequest("OpConvertPtrToU requires a scalar integer result"))
    }

  private class PointerToInteger(pointer: Int, bits: Long, dest: Int, resultType: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      PointerAccessSemantics.pointerToInteger(InvocationMachine(execution))(pointer, bits, dest, resultType)
  }

  private class PointersToIntegers(source: Int, bits: Long, dest: Int, resultType: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      PointerAccessSemantics.pointersToIntegers(InvocationMachine(execution))(source, bits, dest, resultType)
  }

  private def integerToPointer(ids: ResultIds, source: Int)(using site: Site): Op = {
    val program = site.program
    program.runtimeType(ids.resultType) match {
      case Some(RuntimeType.Pointer(storageClass, pointee)) =>
        val function = program.runtimeType(pointee).exists {
          case RuntimeType.Function => true
          case _                    => false
        }
        IntegerToPointer(source, pointee, storageClass, function, ids.dest, ids.resultType)
      case Some(RuntimeType.VectorType(component, count)) if program.runtimeType(component).exists(isPointer) =>
        IntegersToPointers(source, count.raw, ids.dest, ids.resultType)
      case Some(_) => Fail(Fault.InvalidRequest("OpConvertUToPtr requires a pointer result type"))
      case None    => Fail(Fault.InvalidRequest(s"OpConvertUToPtr result type %${ids.resultType} is undefined"))
    }
  }

  private class IntegerToPointer(
      source: Int,
      pointee: Int,
      storageClass: Int,
      function: Boolean,
      dest: Int,
      resultType: Int
  ) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      PointerAccessSemantics.integerToPointer(InvocationMachine(execution))(
        source,
        pointee,
        storageClass,
        function,
        dest,
        resultType
      )
  }

  private class IntegersToPointers(source: Int, lanes: Int, dest: Int, resultType: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      PointerAccessSemantics.integersToPointers(InvocationMachine(execution))(source, lanes, dest, resultType)
  }

  /** OpSizeOf: the size of the pointee type, as its arrays and structures lay it out in memory. */
  private def sizeOf(ids: ResultIds, pointer: Operand)(using site: Site): Op = typed(site, pointer) match {
    case Typed.Undefined(value)  => Fail(undefined(value))
    case Typed.Data(value, _)    => Fail(Fault.InvalidRequest(s"OpSizeOf operand %$value is not a pointer"))
    case Typed.Opaque(_, typeId) => site.program.runtimeType(typeId) match {
        case Some(RuntimeType.Pointer(_, pointee)) =>
          val size = site.program.typeSize(pointee).toLong
          step((execution, _) => Right(execution.values.updateScalar(ids.dest, ids.resultType, size)))
        case _ => Fail(Fault.InvalidRequest(s"OpSizeOf operand %${id(pointer)} is not a pointer"))
      }
  }

  private def arrayLength(ids: ResultIds, structure: Int, member: Int, declared: Option[Int] = None): Op =
    ArrayLength(structure, member, declared, ids.dest, ids.resultType)

  private class ArrayLength(structure: Int, member: Int, declared: Option[Int], dest: Int, resultType: Int)
      extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      PointerAccessSemantics.arrayLength(InvocationMachine(execution))(structure, member, declared, dest, resultType)
  }

  /** Lifetime markers carry no effect, but a malformed marker indicates a corrupt module. */
  private def lifetime(opcode: String, pointerOperand: Operand, sizeOperand: Operand)(using site: Site): Op =
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
          RuntimeType.StorageImage(_, _, _, _, _, _) | RuntimeType.Texture(_, _, _) | RuntimeType.Sampler |
          RuntimeType.SampledImage(_) | RuntimeType.Pipe
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
