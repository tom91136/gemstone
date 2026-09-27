// Generated from the pinned Khronos SPIR-V core grammar. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.Constraint.NonEmptyString

import net.kurobako.gemstone.Constraint.{NonNegative, Positive}
import net.kurobako.gemstone.{NonEmptyVec}
import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.Vec.*
import net.kurobako.gemstone.isa.spirv.v1_6.OperandDecoder.DecodedOperands

@javax.annotation.processing.Generated(Array("sbt codegen"))
enum Instruction(val definition: Instruction.Definition, operandValues: Operand*) {
  case Nop() extends Instruction(Instruction.Definition.Nop)
  case Undef(resultType: Operand, dest: Operand) extends Instruction(Instruction.Definition.Undef, resultType, dest)
  case SourceContinued(continuedSrc: Operand) extends Instruction(Instruction.Definition.SourceContinued, continuedSrc)
  case Source(sourceLanguage: Operand, version: Operand, file: Option[Operand], src: Option[Operand])
      extends Instruction(Instruction.Definition.Source, (Vec(sourceLanguage, version) ++ file.toVec ++ src.toVec)*)
  case SourceExtension(extensionValue: Operand)
      extends Instruction(Instruction.Definition.SourceExtension, extensionValue)
  case Name(target: Operand, name: Operand) extends Instruction(Instruction.Definition.Name, target, name)
  case MemberName(dataType: Operand, member: Operand, name: Operand)
      extends Instruction(Instruction.Definition.MemberName, dataType, member, name)
  case String(dest: Operand, string: Operand) extends Instruction(Instruction.Definition.String, dest, string)
  case Line(file: Operand, line: Operand, column: Operand)
      extends Instruction(Instruction.Definition.Line, file, line, column)
  case Extension(name: Operand) extends Instruction(Instruction.Definition.Extension, name)
  case ExtInstImport(dest: Operand, name: Operand) extends Instruction(Instruction.Definition.ExtInstImport, dest, name)
  case ExtInst(resultType: Operand, dest: Operand, set: Operand, inst: Operand, srcs: Vec[Operand])
      extends Instruction(Instruction.Definition.ExtInst, (Vec(resultType, dest, set, inst) ++ srcs)*)
  case MemoryModel(addressingModel: Operand, memoryModel: Operand)
      extends Instruction(Instruction.Definition.MemoryModel, addressingModel, memoryModel)
  case EntryPoint(executionModel: Operand, entryPoint: Operand, name: Operand, interface: Vec[Operand])
      extends Instruction(Instruction.Definition.EntryPoint, (Vec(executionModel, entryPoint, name) ++ interface)*)
  case ExecutionMode(entryPoint: Operand, mode: Operand)
      extends Instruction(Instruction.Definition.ExecutionMode, entryPoint, mode)
  case Capability(capability: Operand) extends Instruction(Instruction.Definition.Capability, capability)
  case TypeVoid(dest: Operand) extends Instruction(Instruction.Definition.TypeVoid, dest)
  case TypeBool(dest: Operand) extends Instruction(Instruction.Definition.TypeBool, dest)
  case TypeInt(dest: Operand, width: Operand, signedness: Operand)
      extends Instruction(Instruction.Definition.TypeInt, dest, width, signedness)
  case TypeFloat(dest: Operand, width: Operand, floatingPointEncoding: Option[Operand])
      extends Instruction(Instruction.Definition.TypeFloat, (Vec(dest, width) ++ floatingPointEncoding.toVec)*)
  case TypeVector(dest: Operand, componentType: Operand, componentCount: Operand)
      extends Instruction(Instruction.Definition.TypeVector, dest, componentType, componentCount)
  case TypeMatrix(dest: Operand, columnType: Operand, columnCount: Operand)
      extends Instruction(Instruction.Definition.TypeMatrix, dest, columnType, columnCount)
  case TypeImage(
      dest: Operand,
      sampledType: Operand,
      dim: Operand,
      depth: Operand,
      arrayed: Operand,
      mS: Operand,
      sampled: Operand,
      imageFormat: Operand,
      accessQualifier: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.TypeImage,
        (Vec(dest, sampledType, dim, depth, arrayed, mS, sampled, imageFormat) ++ accessQualifier.toVec)*
      )
  case TypeSampler(dest: Operand) extends Instruction(Instruction.Definition.TypeSampler, dest)
  case TypeSampledImage(dest: Operand, imageType: Operand)
      extends Instruction(Instruction.Definition.TypeSampledImage, dest, imageType)
  case TypeArray(dest: Operand, elementType: Operand, length: Operand)
      extends Instruction(Instruction.Definition.TypeArray, dest, elementType, length)
  case TypeRuntimeArray(dest: Operand, elementType: Operand)
      extends Instruction(Instruction.Definition.TypeRuntimeArray, dest, elementType)
  case TypeStruct(dest: Operand, member0TypeMember1Type: Vec[Operand])
      extends Instruction(Instruction.Definition.TypeStruct, (Vec(dest) ++ member0TypeMember1Type)*)
  case TypeOpaque(dest: Operand, theNameOfTheOpaqueType: Operand)
      extends Instruction(Instruction.Definition.TypeOpaque, dest, theNameOfTheOpaqueType)
  case TypePointer(dest: Operand, storageClass: Operand, dataType: Operand)
      extends Instruction(Instruction.Definition.TypePointer, dest, storageClass, dataType)
  case TypeFunction(dest: Operand, returnType: Operand, parameter0TypeParameter1Type: Vec[Operand])
      extends Instruction(Instruction.Definition.TypeFunction, (Vec(dest, returnType) ++ parameter0TypeParameter1Type)*)
  case TypeEvent(dest: Operand) extends Instruction(Instruction.Definition.TypeEvent, dest)
  case TypeDeviceEvent(dest: Operand) extends Instruction(Instruction.Definition.TypeDeviceEvent, dest)
  case TypeReserveId(dest: Operand) extends Instruction(Instruction.Definition.TypeReserveId, dest)
  case TypeQueue(dest: Operand) extends Instruction(Instruction.Definition.TypeQueue, dest)
  case TypePipe(dest: Operand, qualifier: Operand) extends Instruction(Instruction.Definition.TypePipe, dest, qualifier)
  case TypeForwardPointer(pointerType: Operand, storageClass: Operand)
      extends Instruction(Instruction.Definition.TypeForwardPointer, pointerType, storageClass)
  case ConstantTrue(resultType: Operand, dest: Operand)
      extends Instruction(Instruction.Definition.ConstantTrue, resultType, dest)
  case ConstantFalse(resultType: Operand, dest: Operand)
      extends Instruction(Instruction.Definition.ConstantFalse, resultType, dest)
  case Constant(resultType: Operand, dest: Operand, value: Operand)
      extends Instruction(Instruction.Definition.Constant, resultType, dest, value)
  case ConstantComposite(resultType: Operand, dest: Operand, constituents: Vec[Operand])
      extends Instruction(Instruction.Definition.ConstantComposite, (Vec(resultType, dest) ++ constituents)*)
  case ConstantSampler(
      resultType: Operand,
      dest: Operand,
      samplerAddressingMode: Operand,
      param: Operand,
      samplerFilterMode: Operand
  ) extends Instruction(
        Instruction.Definition.ConstantSampler,
        resultType,
        dest,
        samplerAddressingMode,
        param,
        samplerFilterMode
      )
  case ConstantNull(resultType: Operand, dest: Operand)
      extends Instruction(Instruction.Definition.ConstantNull, resultType, dest)
  case SpecConstantTrue(resultType: Operand, dest: Operand)
      extends Instruction(Instruction.Definition.SpecConstantTrue, resultType, dest)
  case SpecConstantFalse(resultType: Operand, dest: Operand)
      extends Instruction(Instruction.Definition.SpecConstantFalse, resultType, dest)
  case SpecConstant(resultType: Operand, dest: Operand, value: Operand)
      extends Instruction(Instruction.Definition.SpecConstant, resultType, dest, value)
  case SpecConstantComposite(resultType: Operand, dest: Operand, constituents: Vec[Operand])
      extends Instruction(Instruction.Definition.SpecConstantComposite, (Vec(resultType, dest) ++ constituents)*)
  case SpecConstantOp(resultType: Operand, dest: Operand, instructionOpcode: Operand, srcs: Vec[Operand])
      extends Instruction(Instruction.Definition.SpecConstantOp, (Vec(resultType, dest, instructionOpcode) ++ srcs)*)
  case Function(resultType: Operand, dest: Operand, functionControl: Operand, functionType: Operand)
      extends Instruction(Instruction.Definition.Function, resultType, dest, functionControl, functionType)
  case FunctionParameter(resultType: Operand, dest: Operand)
      extends Instruction(Instruction.Definition.FunctionParameter, resultType, dest)
  case FunctionEnd() extends Instruction(Instruction.Definition.FunctionEnd)
  case FunctionCall(resultType: Operand, dest: Operand, function: Operand, argument0Argument1: Vec[Operand])
      extends Instruction(Instruction.Definition.FunctionCall, (Vec(resultType, dest, function) ++ argument0Argument1)*)
  case Variable(resultType: Operand, dest: Operand, storageClass: Operand, initializer: Option[Operand])
      extends Instruction(Instruction.Definition.Variable, (Vec(resultType, dest, storageClass) ++ initializer.toVec)*)
  case ImageTexelPointer(resultType: Operand, dest: Operand, image: Operand, coordinate: Operand, sample: Operand)
      extends Instruction(Instruction.Definition.ImageTexelPointer, resultType, dest, image, coordinate, sample)
  case Load(resultType: Operand, dest: Operand, pointer: Operand, mods: Option[Operand])
      extends Instruction(Instruction.Definition.Load, (Vec(resultType, dest, pointer) ++ mods.toVec)*)
  case Store(pointer: Operand, src: Operand, mods: Option[Operand])
      extends Instruction(Instruction.Definition.Store, (Vec(pointer, src) ++ mods.toVec)*)
  case CopyMemory(target: Operand, src: Operand, modsA: Option[Operand], modsB: Option[Operand])
      extends Instruction(Instruction.Definition.CopyMemory, (Vec(target, src) ++ modsA.toVec ++ modsB.toVec)*)
  case CopyMemorySized(target: Operand, src: Operand, size: Operand, modsA: Option[Operand], modsB: Option[Operand])
      extends Instruction(
        Instruction.Definition.CopyMemorySized,
        (Vec(target, src, size) ++ modsA.toVec ++ modsB.toVec)*
      )
  case AccessChain(resultType: Operand, dest: Operand, base: Operand, indexes: Vec[Operand])
      extends Instruction(Instruction.Definition.AccessChain, (Vec(resultType, dest, base) ++ indexes)*)
  case InBoundsAccessChain(resultType: Operand, dest: Operand, base: Operand, indexes: Vec[Operand])
      extends Instruction(Instruction.Definition.InBoundsAccessChain, (Vec(resultType, dest, base) ++ indexes)*)
  case PtrAccessChain(resultType: Operand, dest: Operand, base: Operand, element: Operand, indexes: Vec[Operand])
      extends Instruction(Instruction.Definition.PtrAccessChain, (Vec(resultType, dest, base, element) ++ indexes)*)
  case ArrayLength(resultType: Operand, dest: Operand, structure: Operand, arrayMember: Operand)
      extends Instruction(Instruction.Definition.ArrayLength, resultType, dest, structure, arrayMember)
  case GenericPtrMemSemantics(resultType: Operand, dest: Operand, pointer: Operand)
      extends Instruction(Instruction.Definition.GenericPtrMemSemantics, resultType, dest, pointer)
  case InBoundsPtrAccessChain(
      resultType: Operand,
      dest: Operand,
      base: Operand,
      element: Operand,
      indexes: Vec[Operand]
  ) extends Instruction(
        Instruction.Definition.InBoundsPtrAccessChain,
        (Vec(resultType, dest, base, element) ++ indexes)*
      )
  case Decorate(target: Operand, decoration: Operand)
      extends Instruction(Instruction.Definition.Decorate, target, decoration)
  case MemberDecorate(structureType: Operand, member: Operand, decoration: Operand)
      extends Instruction(Instruction.Definition.MemberDecorate, structureType, member, decoration)
  case DecorationGroup(dest: Operand) extends Instruction(Instruction.Definition.DecorationGroup, dest)
  case GroupDecorate(decorationGroup: Operand, targets: Vec[Operand])
      extends Instruction(Instruction.Definition.GroupDecorate, (Vec(decorationGroup) ++ targets)*)
  case GroupMemberDecorate(decorationGroup: Operand, targets: Vec[Operand])
      extends Instruction(Instruction.Definition.GroupMemberDecorate, (Vec(decorationGroup) ++ targets)*)
  case VectorExtractDynamic(resultType: Operand, dest: Operand, vector: Operand, index: Operand)
      extends Instruction(Instruction.Definition.VectorExtractDynamic, resultType, dest, vector, index)
  case VectorInsertDynamic(resultType: Operand, dest: Operand, vector: Operand, component: Operand, index: Operand)
      extends Instruction(Instruction.Definition.VectorInsertDynamic, resultType, dest, vector, component, index)
  case VectorShuffle(resultType: Operand, dest: Operand, vector1: Operand, vector2: Operand, components: Vec[Operand])
      extends Instruction(
        Instruction.Definition.VectorShuffle,
        (Vec(resultType, dest, vector1, vector2) ++ components)*
      )
  case CompositeConstruct(resultType: Operand, dest: Operand, constituents: Vec[Operand])
      extends Instruction(Instruction.Definition.CompositeConstruct, (Vec(resultType, dest) ++ constituents)*)
  case CompositeExtract(resultType: Operand, dest: Operand, composite: Operand, indexes: Vec[Operand])
      extends Instruction(Instruction.Definition.CompositeExtract, (Vec(resultType, dest, composite) ++ indexes)*)
  case CompositeInsert(resultType: Operand, dest: Operand, src: Operand, composite: Operand, indexes: Vec[Operand])
      extends Instruction(Instruction.Definition.CompositeInsert, (Vec(resultType, dest, src, composite) ++ indexes)*)
  case CopyObject(resultType: Operand, dest: Operand, src: Operand)
      extends Instruction(Instruction.Definition.CopyObject, resultType, dest, src)
  case Transpose(resultType: Operand, dest: Operand, matrix: Operand)
      extends Instruction(Instruction.Definition.Transpose, resultType, dest, matrix)
  case SampledImage(resultType: Operand, dest: Operand, image: Operand, sampler: Operand)
      extends Instruction(Instruction.Definition.SampledImage, resultType, dest, image, sampler)
  case ImageSampleImplicitLod(
      resultType: Operand,
      dest: Operand,
      sampledImage: Operand,
      coordinate: Operand,
      mods: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.ImageSampleImplicitLod,
        (Vec(resultType, dest, sampledImage, coordinate) ++ mods.toVec)*
      )
  case ImageSampleExplicitLod(
      resultType: Operand,
      dest: Operand,
      sampledImage: Operand,
      coordinate: Operand,
      mods: Operand
  ) extends Instruction(Instruction.Definition.ImageSampleExplicitLod, resultType, dest, sampledImage, coordinate, mods)
  case ImageSampleDrefImplicitLod(
      resultType: Operand,
      dest: Operand,
      sampledImage: Operand,
      coordinate: Operand,
      dref: Operand,
      mods: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.ImageSampleDrefImplicitLod,
        (Vec(resultType, dest, sampledImage, coordinate, dref) ++ mods.toVec)*
      )
  case ImageSampleDrefExplicitLod(
      resultType: Operand,
      dest: Operand,
      sampledImage: Operand,
      coordinate: Operand,
      dref: Operand,
      mods: Operand
  ) extends Instruction(
        Instruction.Definition.ImageSampleDrefExplicitLod,
        resultType,
        dest,
        sampledImage,
        coordinate,
        dref,
        mods
      )
  case ImageSampleProjImplicitLod(
      resultType: Operand,
      dest: Operand,
      sampledImage: Operand,
      coordinate: Operand,
      mods: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.ImageSampleProjImplicitLod,
        (Vec(resultType, dest, sampledImage, coordinate) ++ mods.toVec)*
      )
  case ImageSampleProjExplicitLod(
      resultType: Operand,
      dest: Operand,
      sampledImage: Operand,
      coordinate: Operand,
      mods: Operand
  ) extends Instruction(
        Instruction.Definition.ImageSampleProjExplicitLod,
        resultType,
        dest,
        sampledImage,
        coordinate,
        mods
      )
  case ImageSampleProjDrefImplicitLod(
      resultType: Operand,
      dest: Operand,
      sampledImage: Operand,
      coordinate: Operand,
      dref: Operand,
      mods: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.ImageSampleProjDrefImplicitLod,
        (Vec(resultType, dest, sampledImage, coordinate, dref) ++ mods.toVec)*
      )
  case ImageSampleProjDrefExplicitLod(
      resultType: Operand,
      dest: Operand,
      sampledImage: Operand,
      coordinate: Operand,
      dref: Operand,
      mods: Operand
  ) extends Instruction(
        Instruction.Definition.ImageSampleProjDrefExplicitLod,
        resultType,
        dest,
        sampledImage,
        coordinate,
        dref,
        mods
      )
  case ImageFetch(resultType: Operand, dest: Operand, image: Operand, coordinate: Operand, mods: Option[Operand])
      extends Instruction(Instruction.Definition.ImageFetch, (Vec(resultType, dest, image, coordinate) ++ mods.toVec)*)
  case ImageGather(
      resultType: Operand,
      dest: Operand,
      sampledImage: Operand,
      coordinate: Operand,
      component: Operand,
      mods: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.ImageGather,
        (Vec(resultType, dest, sampledImage, coordinate, component) ++ mods.toVec)*
      )
  case ImageDrefGather(
      resultType: Operand,
      dest: Operand,
      sampledImage: Operand,
      coordinate: Operand,
      dref: Operand,
      mods: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.ImageDrefGather,
        (Vec(resultType, dest, sampledImage, coordinate, dref) ++ mods.toVec)*
      )
  case ImageRead(resultType: Operand, dest: Operand, image: Operand, coordinate: Operand, mods: Option[Operand])
      extends Instruction(Instruction.Definition.ImageRead, (Vec(resultType, dest, image, coordinate) ++ mods.toVec)*)
  case ImageWrite(image: Operand, coordinate: Operand, texel: Operand, mods: Option[Operand])
      extends Instruction(Instruction.Definition.ImageWrite, (Vec(image, coordinate, texel) ++ mods.toVec)*)
  case Image(resultType: Operand, dest: Operand, sampledImage: Operand)
      extends Instruction(Instruction.Definition.Image, resultType, dest, sampledImage)
  case ImageQueryFormat(resultType: Operand, dest: Operand, image: Operand)
      extends Instruction(Instruction.Definition.ImageQueryFormat, resultType, dest, image)
  case ImageQueryOrder(resultType: Operand, dest: Operand, image: Operand)
      extends Instruction(Instruction.Definition.ImageQueryOrder, resultType, dest, image)
  case ImageQuerySizeLod(resultType: Operand, dest: Operand, image: Operand, levelOfDetail: Operand)
      extends Instruction(Instruction.Definition.ImageQuerySizeLod, resultType, dest, image, levelOfDetail)
  case ImageQuerySize(resultType: Operand, dest: Operand, image: Operand)
      extends Instruction(Instruction.Definition.ImageQuerySize, resultType, dest, image)
  case ImageQueryLod(resultType: Operand, dest: Operand, sampledImage: Operand, coordinate: Operand)
      extends Instruction(Instruction.Definition.ImageQueryLod, resultType, dest, sampledImage, coordinate)
  case ImageQueryLevels(resultType: Operand, dest: Operand, image: Operand)
      extends Instruction(Instruction.Definition.ImageQueryLevels, resultType, dest, image)
  case ImageQuerySamples(resultType: Operand, dest: Operand, image: Operand)
      extends Instruction(Instruction.Definition.ImageQuerySamples, resultType, dest, image)
  case ConvertFToU(resultType: Operand, dest: Operand, floatValue: Operand)
      extends Instruction(Instruction.Definition.ConvertFToU, resultType, dest, floatValue)
  case ConvertFToS(resultType: Operand, dest: Operand, floatValue: Operand)
      extends Instruction(Instruction.Definition.ConvertFToS, resultType, dest, floatValue)
  case ConvertSToF(resultType: Operand, dest: Operand, signedValue: Operand)
      extends Instruction(Instruction.Definition.ConvertSToF, resultType, dest, signedValue)
  case ConvertUToF(resultType: Operand, dest: Operand, unsignedValue: Operand)
      extends Instruction(Instruction.Definition.ConvertUToF, resultType, dest, unsignedValue)
  case UConvert(resultType: Operand, dest: Operand, unsignedValue: Operand)
      extends Instruction(Instruction.Definition.UConvert, resultType, dest, unsignedValue)
  case SConvert(resultType: Operand, dest: Operand, signedValue: Operand)
      extends Instruction(Instruction.Definition.SConvert, resultType, dest, signedValue)
  case FConvert(resultType: Operand, dest: Operand, floatValue: Operand)
      extends Instruction(Instruction.Definition.FConvert, resultType, dest, floatValue)
  case QuantizeToF16(resultType: Operand, dest: Operand, value: Operand)
      extends Instruction(Instruction.Definition.QuantizeToF16, resultType, dest, value)
  case ConvertPtrToU(resultType: Operand, dest: Operand, pointer: Operand)
      extends Instruction(Instruction.Definition.ConvertPtrToU, resultType, dest, pointer)
  case SatConvertSToU(resultType: Operand, dest: Operand, signedValue: Operand)
      extends Instruction(Instruction.Definition.SatConvertSToU, resultType, dest, signedValue)
  case SatConvertUToS(resultType: Operand, dest: Operand, unsignedValue: Operand)
      extends Instruction(Instruction.Definition.SatConvertUToS, resultType, dest, unsignedValue)
  case ConvertUToPtr(resultType: Operand, dest: Operand, integerValue: Operand)
      extends Instruction(Instruction.Definition.ConvertUToPtr, resultType, dest, integerValue)
  case PtrCastToGeneric(resultType: Operand, dest: Operand, pointer: Operand)
      extends Instruction(Instruction.Definition.PtrCastToGeneric, resultType, dest, pointer)
  case GenericCastToPtr(resultType: Operand, dest: Operand, pointer: Operand)
      extends Instruction(Instruction.Definition.GenericCastToPtr, resultType, dest, pointer)
  case GenericCastToPtrExplicit(resultType: Operand, dest: Operand, pointer: Operand, storage: Operand)
      extends Instruction(Instruction.Definition.GenericCastToPtrExplicit, resultType, dest, pointer, storage)
  case Bitcast(resultType: Operand, dest: Operand, src: Operand)
      extends Instruction(Instruction.Definition.Bitcast, resultType, dest, src)
  case SNegate(resultType: Operand, dest: Operand, src: Operand)
      extends Instruction(Instruction.Definition.SNegate, resultType, dest, src)
  case FNegate(resultType: Operand, dest: Operand, src: Operand)
      extends Instruction(Instruction.Definition.FNegate, resultType, dest, src)
  case IAdd(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.IAdd, resultType, dest, srcA, srcB)
  case FAdd(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.FAdd, resultType, dest, srcA, srcB)
  case ISub(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.ISub, resultType, dest, srcA, srcB)
  case FSub(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.FSub, resultType, dest, srcA, srcB)
  case IMul(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.IMul, resultType, dest, srcA, srcB)
  case FMul(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.FMul, resultType, dest, srcA, srcB)
  case UDiv(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.UDiv, resultType, dest, srcA, srcB)
  case SDiv(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.SDiv, resultType, dest, srcA, srcB)
  case FDiv(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.FDiv, resultType, dest, srcA, srcB)
  case UMod(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.UMod, resultType, dest, srcA, srcB)
  case SRem(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.SRem, resultType, dest, srcA, srcB)
  case SMod(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.SMod, resultType, dest, srcA, srcB)
  case FRem(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.FRem, resultType, dest, srcA, srcB)
  case FMod(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.FMod, resultType, dest, srcA, srcB)
  case VectorTimesScalar(resultType: Operand, dest: Operand, vector: Operand, scalar: Operand)
      extends Instruction(Instruction.Definition.VectorTimesScalar, resultType, dest, vector, scalar)
  case MatrixTimesScalar(resultType: Operand, dest: Operand, matrix: Operand, scalar: Operand)
      extends Instruction(Instruction.Definition.MatrixTimesScalar, resultType, dest, matrix, scalar)
  case VectorTimesMatrix(resultType: Operand, dest: Operand, vector: Operand, matrix: Operand)
      extends Instruction(Instruction.Definition.VectorTimesMatrix, resultType, dest, vector, matrix)
  case MatrixTimesVector(resultType: Operand, dest: Operand, matrix: Operand, vector: Operand)
      extends Instruction(Instruction.Definition.MatrixTimesVector, resultType, dest, matrix, vector)
  case MatrixTimesMatrix(resultType: Operand, dest: Operand, leftMatrix: Operand, rightMatrix: Operand)
      extends Instruction(Instruction.Definition.MatrixTimesMatrix, resultType, dest, leftMatrix, rightMatrix)
  case OuterProduct(resultType: Operand, dest: Operand, vector1: Operand, vector2: Operand)
      extends Instruction(Instruction.Definition.OuterProduct, resultType, dest, vector1, vector2)
  case Dot(resultType: Operand, dest: Operand, vector1: Operand, vector2: Operand)
      extends Instruction(Instruction.Definition.Dot, resultType, dest, vector1, vector2)
  case IAddCarry(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.IAddCarry, resultType, dest, srcA, srcB)
  case ISubBorrow(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.ISubBorrow, resultType, dest, srcA, srcB)
  case UMulExtended(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.UMulExtended, resultType, dest, srcA, srcB)
  case SMulExtended(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.SMulExtended, resultType, dest, srcA, srcB)
  case Any(resultType: Operand, dest: Operand, vector: Operand)
      extends Instruction(Instruction.Definition.Any, resultType, dest, vector)
  case All(resultType: Operand, dest: Operand, vector: Operand)
      extends Instruction(Instruction.Definition.All, resultType, dest, vector)
  case IsNan(resultType: Operand, dest: Operand, x: Operand)
      extends Instruction(Instruction.Definition.IsNan, resultType, dest, x)
  case IsInf(resultType: Operand, dest: Operand, x: Operand)
      extends Instruction(Instruction.Definition.IsInf, resultType, dest, x)
  case IsFinite(resultType: Operand, dest: Operand, x: Operand)
      extends Instruction(Instruction.Definition.IsFinite, resultType, dest, x)
  case IsNormal(resultType: Operand, dest: Operand, x: Operand)
      extends Instruction(Instruction.Definition.IsNormal, resultType, dest, x)
  case SignBitSet(resultType: Operand, dest: Operand, x: Operand)
      extends Instruction(Instruction.Definition.SignBitSet, resultType, dest, x)
  case LessOrGreater(resultType: Operand, dest: Operand, x: Operand, y: Operand)
      extends Instruction(Instruction.Definition.LessOrGreater, resultType, dest, x, y)
  case Ordered(resultType: Operand, dest: Operand, x: Operand, y: Operand)
      extends Instruction(Instruction.Definition.Ordered, resultType, dest, x, y)
  case Unordered(resultType: Operand, dest: Operand, x: Operand, y: Operand)
      extends Instruction(Instruction.Definition.Unordered, resultType, dest, x, y)
  case LogicalEqual(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.LogicalEqual, resultType, dest, srcA, srcB)
  case LogicalNotEqual(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.LogicalNotEqual, resultType, dest, srcA, srcB)
  case LogicalOr(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.LogicalOr, resultType, dest, srcA, srcB)
  case LogicalAnd(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.LogicalAnd, resultType, dest, srcA, srcB)
  case LogicalNot(resultType: Operand, dest: Operand, src: Operand)
      extends Instruction(Instruction.Definition.LogicalNot, resultType, dest, src)
  case Select(resultType: Operand, dest: Operand, pred: Operand, object1: Operand, object2: Operand)
      extends Instruction(Instruction.Definition.Select, resultType, dest, pred, object1, object2)
  case IEqual(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.IEqual, resultType, dest, srcA, srcB)
  case INotEqual(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.INotEqual, resultType, dest, srcA, srcB)
  case UGreaterThan(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.UGreaterThan, resultType, dest, srcA, srcB)
  case SGreaterThan(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.SGreaterThan, resultType, dest, srcA, srcB)
  case UGreaterThanEqual(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.UGreaterThanEqual, resultType, dest, srcA, srcB)
  case SGreaterThanEqual(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.SGreaterThanEqual, resultType, dest, srcA, srcB)
  case ULessThan(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.ULessThan, resultType, dest, srcA, srcB)
  case SLessThan(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.SLessThan, resultType, dest, srcA, srcB)
  case ULessThanEqual(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.ULessThanEqual, resultType, dest, srcA, srcB)
  case SLessThanEqual(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.SLessThanEqual, resultType, dest, srcA, srcB)
  case FOrdEqual(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.FOrdEqual, resultType, dest, srcA, srcB)
  case FUnordEqual(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.FUnordEqual, resultType, dest, srcA, srcB)
  case FOrdNotEqual(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.FOrdNotEqual, resultType, dest, srcA, srcB)
  case FUnordNotEqual(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.FUnordNotEqual, resultType, dest, srcA, srcB)
  case FOrdLessThan(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.FOrdLessThan, resultType, dest, srcA, srcB)
  case FUnordLessThan(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.FUnordLessThan, resultType, dest, srcA, srcB)
  case FOrdGreaterThan(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.FOrdGreaterThan, resultType, dest, srcA, srcB)
  case FUnordGreaterThan(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.FUnordGreaterThan, resultType, dest, srcA, srcB)
  case FOrdLessThanEqual(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.FOrdLessThanEqual, resultType, dest, srcA, srcB)
  case FUnordLessThanEqual(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.FUnordLessThanEqual, resultType, dest, srcA, srcB)
  case FOrdGreaterThanEqual(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.FOrdGreaterThanEqual, resultType, dest, srcA, srcB)
  case FUnordGreaterThanEqual(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.FUnordGreaterThanEqual, resultType, dest, srcA, srcB)
  case ShiftRightLogical(resultType: Operand, dest: Operand, base: Operand, shift: Operand)
      extends Instruction(Instruction.Definition.ShiftRightLogical, resultType, dest, base, shift)
  case ShiftRightArithmetic(resultType: Operand, dest: Operand, base: Operand, shift: Operand)
      extends Instruction(Instruction.Definition.ShiftRightArithmetic, resultType, dest, base, shift)
  case ShiftLeftLogical(resultType: Operand, dest: Operand, base: Operand, shift: Operand)
      extends Instruction(Instruction.Definition.ShiftLeftLogical, resultType, dest, base, shift)
  case BitwiseOr(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.BitwiseOr, resultType, dest, srcA, srcB)
  case BitwiseXor(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.BitwiseXor, resultType, dest, srcA, srcB)
  case BitwiseAnd(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.BitwiseAnd, resultType, dest, srcA, srcB)
  case Not(resultType: Operand, dest: Operand, src: Operand)
      extends Instruction(Instruction.Definition.Not, resultType, dest, src)
  case BitFieldInsert(
      resultType: Operand,
      dest: Operand,
      base: Operand,
      insert: Operand,
      offset: Operand,
      count: Operand
  ) extends Instruction(Instruction.Definition.BitFieldInsert, resultType, dest, base, insert, offset, count)
  case BitFieldSExtract(resultType: Operand, dest: Operand, base: Operand, offset: Operand, count: Operand)
      extends Instruction(Instruction.Definition.BitFieldSExtract, resultType, dest, base, offset, count)
  case BitFieldUExtract(resultType: Operand, dest: Operand, base: Operand, offset: Operand, count: Operand)
      extends Instruction(Instruction.Definition.BitFieldUExtract, resultType, dest, base, offset, count)
  case BitReverse(resultType: Operand, dest: Operand, base: Operand)
      extends Instruction(Instruction.Definition.BitReverse, resultType, dest, base)
  case BitCount(resultType: Operand, dest: Operand, base: Operand)
      extends Instruction(Instruction.Definition.BitCount, resultType, dest, base)
  case DPdx(resultType: Operand, dest: Operand, p: Operand)
      extends Instruction(Instruction.Definition.DPdx, resultType, dest, p)
  case DPdy(resultType: Operand, dest: Operand, p: Operand)
      extends Instruction(Instruction.Definition.DPdy, resultType, dest, p)
  case Fwidth(resultType: Operand, dest: Operand, p: Operand)
      extends Instruction(Instruction.Definition.Fwidth, resultType, dest, p)
  case DPdxFine(resultType: Operand, dest: Operand, p: Operand)
      extends Instruction(Instruction.Definition.DPdxFine, resultType, dest, p)
  case DPdyFine(resultType: Operand, dest: Operand, p: Operand)
      extends Instruction(Instruction.Definition.DPdyFine, resultType, dest, p)
  case FwidthFine(resultType: Operand, dest: Operand, p: Operand)
      extends Instruction(Instruction.Definition.FwidthFine, resultType, dest, p)
  case DPdxCoarse(resultType: Operand, dest: Operand, p: Operand)
      extends Instruction(Instruction.Definition.DPdxCoarse, resultType, dest, p)
  case DPdyCoarse(resultType: Operand, dest: Operand, p: Operand)
      extends Instruction(Instruction.Definition.DPdyCoarse, resultType, dest, p)
  case FwidthCoarse(resultType: Operand, dest: Operand, p: Operand)
      extends Instruction(Instruction.Definition.FwidthCoarse, resultType, dest, p)
  case EmitVertex() extends Instruction(Instruction.Definition.EmitVertex)
  case EndPrimitive() extends Instruction(Instruction.Definition.EndPrimitive)
  case EmitStreamVertex(stream: Operand) extends Instruction(Instruction.Definition.EmitStreamVertex, stream)
  case EndStreamPrimitive(stream: Operand) extends Instruction(Instruction.Definition.EndStreamPrimitive, stream)
  case ControlBarrier(execution: Operand, memory: Operand, semantics: Operand)
      extends Instruction(Instruction.Definition.ControlBarrier, execution, memory, semantics)
  case MemoryBarrier(memory: Operand, semantics: Operand)
      extends Instruction(Instruction.Definition.MemoryBarrier, memory, semantics)
  case AtomicLoad(resultType: Operand, dest: Operand, pointer: Operand, memory: Operand, semantics: Operand)
      extends Instruction(Instruction.Definition.AtomicLoad, resultType, dest, pointer, memory, semantics)
  case AtomicStore(pointer: Operand, memory: Operand, semantics: Operand, value: Operand)
      extends Instruction(Instruction.Definition.AtomicStore, pointer, memory, semantics, value)
  case AtomicExchange(
      resultType: Operand,
      dest: Operand,
      pointer: Operand,
      memory: Operand,
      semantics: Operand,
      value: Operand
  ) extends Instruction(Instruction.Definition.AtomicExchange, resultType, dest, pointer, memory, semantics, value)
  case AtomicCompareExchange(
      resultType: Operand,
      dest: Operand,
      pointer: Operand,
      memory: Operand,
      equal: Operand,
      unequal: Operand,
      value: Operand,
      comparator: Operand
  ) extends Instruction(
        Instruction.Definition.AtomicCompareExchange,
        resultType,
        dest,
        pointer,
        memory,
        equal,
        unequal,
        value,
        comparator
      )
  case AtomicCompareExchangeWeak(
      resultType: Operand,
      dest: Operand,
      pointer: Operand,
      memory: Operand,
      equal: Operand,
      unequal: Operand,
      value: Operand,
      comparator: Operand
  ) extends Instruction(
        Instruction.Definition.AtomicCompareExchangeWeak,
        resultType,
        dest,
        pointer,
        memory,
        equal,
        unequal,
        value,
        comparator
      )
  case AtomicIIncrement(resultType: Operand, dest: Operand, pointer: Operand, memory: Operand, semantics: Operand)
      extends Instruction(Instruction.Definition.AtomicIIncrement, resultType, dest, pointer, memory, semantics)
  case AtomicIDecrement(resultType: Operand, dest: Operand, pointer: Operand, memory: Operand, semantics: Operand)
      extends Instruction(Instruction.Definition.AtomicIDecrement, resultType, dest, pointer, memory, semantics)
  case AtomicIAdd(
      resultType: Operand,
      dest: Operand,
      pointer: Operand,
      memory: Operand,
      semantics: Operand,
      value: Operand
  ) extends Instruction(Instruction.Definition.AtomicIAdd, resultType, dest, pointer, memory, semantics, value)
  case AtomicISub(
      resultType: Operand,
      dest: Operand,
      pointer: Operand,
      memory: Operand,
      semantics: Operand,
      value: Operand
  ) extends Instruction(Instruction.Definition.AtomicISub, resultType, dest, pointer, memory, semantics, value)
  case AtomicSMin(
      resultType: Operand,
      dest: Operand,
      pointer: Operand,
      memory: Operand,
      semantics: Operand,
      value: Operand
  ) extends Instruction(Instruction.Definition.AtomicSMin, resultType, dest, pointer, memory, semantics, value)
  case AtomicUMin(
      resultType: Operand,
      dest: Operand,
      pointer: Operand,
      memory: Operand,
      semantics: Operand,
      value: Operand
  ) extends Instruction(Instruction.Definition.AtomicUMin, resultType, dest, pointer, memory, semantics, value)
  case AtomicSMax(
      resultType: Operand,
      dest: Operand,
      pointer: Operand,
      memory: Operand,
      semantics: Operand,
      value: Operand
  ) extends Instruction(Instruction.Definition.AtomicSMax, resultType, dest, pointer, memory, semantics, value)
  case AtomicUMax(
      resultType: Operand,
      dest: Operand,
      pointer: Operand,
      memory: Operand,
      semantics: Operand,
      value: Operand
  ) extends Instruction(Instruction.Definition.AtomicUMax, resultType, dest, pointer, memory, semantics, value)
  case AtomicAnd(
      resultType: Operand,
      dest: Operand,
      pointer: Operand,
      memory: Operand,
      semantics: Operand,
      value: Operand
  ) extends Instruction(Instruction.Definition.AtomicAnd, resultType, dest, pointer, memory, semantics, value)
  case AtomicOr(
      resultType: Operand,
      dest: Operand,
      pointer: Operand,
      memory: Operand,
      semantics: Operand,
      value: Operand
  ) extends Instruction(Instruction.Definition.AtomicOr, resultType, dest, pointer, memory, semantics, value)
  case AtomicXor(
      resultType: Operand,
      dest: Operand,
      pointer: Operand,
      memory: Operand,
      semantics: Operand,
      value: Operand
  ) extends Instruction(Instruction.Definition.AtomicXor, resultType, dest, pointer, memory, semantics, value)
  case Phi(resultType: Operand, dest: Operand, variableParent: Vec[Operand])
      extends Instruction(Instruction.Definition.Phi, (Vec(resultType, dest) ++ variableParent)*)
  case LoopMerge(mergeBlock: Operand, continueTarget: Operand, loopControl: Operand)
      extends Instruction(Instruction.Definition.LoopMerge, mergeBlock, continueTarget, loopControl)
  case SelectionMerge(mergeBlock: Operand, selectionControl: Operand)
      extends Instruction(Instruction.Definition.SelectionMerge, mergeBlock, selectionControl)
  case Label(dest: Operand) extends Instruction(Instruction.Definition.Label, dest)
  case Branch(targetLabel: Operand) extends Instruction(Instruction.Definition.Branch, targetLabel)
  case BranchConditional(pred: Operand, trueLabel: Operand, falseLabel: Operand, branchWeights: Vec[Operand])
      extends Instruction(
        Instruction.Definition.BranchConditional,
        (Vec(pred, trueLabel, falseLabel) ++ branchWeights)*
      )
  case Switch(selector: Operand, default: Operand, target: Vec[Operand])
      extends Instruction(Instruction.Definition.Switch, (Vec(selector, default) ++ target)*)
  case Kill() extends Instruction(Instruction.Definition.Kill)
  case Return() extends Instruction(Instruction.Definition.Return)
  case ReturnValue(value: Operand) extends Instruction(Instruction.Definition.ReturnValue, value)
  case Unreachable() extends Instruction(Instruction.Definition.Unreachable)
  case LifetimeStart(pointer: Operand, size: Operand)
      extends Instruction(Instruction.Definition.LifetimeStart, pointer, size)
  case LifetimeStop(pointer: Operand, size: Operand)
      extends Instruction(Instruction.Definition.LifetimeStop, pointer, size)
  case GroupAsyncCopy(
      resultType: Operand,
      dest: Operand,
      execution: Operand,
      destination: Operand,
      src: Operand,
      numElements: Operand,
      stride: Operand,
      event: Operand
  ) extends Instruction(
        Instruction.Definition.GroupAsyncCopy,
        resultType,
        dest,
        execution,
        destination,
        src,
        numElements,
        stride,
        event
      )
  case GroupWaitEvents(execution: Operand, numEvents: Operand, eventsList: Operand)
      extends Instruction(Instruction.Definition.GroupWaitEvents, execution, numEvents, eventsList)
  case GroupAll(resultType: Operand, dest: Operand, execution: Operand, pred: Operand)
      extends Instruction(Instruction.Definition.GroupAll, resultType, dest, execution, pred)
  case GroupAny(resultType: Operand, dest: Operand, execution: Operand, pred: Operand)
      extends Instruction(Instruction.Definition.GroupAny, resultType, dest, execution, pred)
  case GroupBroadcast(resultType: Operand, dest: Operand, execution: Operand, value: Operand, localId: Operand)
      extends Instruction(Instruction.Definition.GroupBroadcast, resultType, dest, execution, value, localId)
  case GroupIAdd(resultType: Operand, dest: Operand, execution: Operand, operation: Operand, x: Operand)
      extends Instruction(Instruction.Definition.GroupIAdd, resultType, dest, execution, operation, x)
  case GroupFAdd(resultType: Operand, dest: Operand, execution: Operand, operation: Operand, x: Operand)
      extends Instruction(Instruction.Definition.GroupFAdd, resultType, dest, execution, operation, x)
  case GroupFMin(resultType: Operand, dest: Operand, execution: Operand, operation: Operand, x: Operand)
      extends Instruction(Instruction.Definition.GroupFMin, resultType, dest, execution, operation, x)
  case GroupUMin(resultType: Operand, dest: Operand, execution: Operand, operation: Operand, x: Operand)
      extends Instruction(Instruction.Definition.GroupUMin, resultType, dest, execution, operation, x)
  case GroupSMin(resultType: Operand, dest: Operand, execution: Operand, operation: Operand, x: Operand)
      extends Instruction(Instruction.Definition.GroupSMin, resultType, dest, execution, operation, x)
  case GroupFMax(resultType: Operand, dest: Operand, execution: Operand, operation: Operand, x: Operand)
      extends Instruction(Instruction.Definition.GroupFMax, resultType, dest, execution, operation, x)
  case GroupUMax(resultType: Operand, dest: Operand, execution: Operand, operation: Operand, x: Operand)
      extends Instruction(Instruction.Definition.GroupUMax, resultType, dest, execution, operation, x)
  case GroupSMax(resultType: Operand, dest: Operand, execution: Operand, operation: Operand, x: Operand)
      extends Instruction(Instruction.Definition.GroupSMax, resultType, dest, execution, operation, x)
  case ReadPipe(
      resultType: Operand,
      dest: Operand,
      pipe: Operand,
      pointer: Operand,
      packetSize: Operand,
      packetAlignment: Operand
  ) extends Instruction(Instruction.Definition.ReadPipe, resultType, dest, pipe, pointer, packetSize, packetAlignment)
  case WritePipe(
      resultType: Operand,
      dest: Operand,
      pipe: Operand,
      pointer: Operand,
      packetSize: Operand,
      packetAlignment: Operand
  ) extends Instruction(Instruction.Definition.WritePipe, resultType, dest, pipe, pointer, packetSize, packetAlignment)
  case ReservedReadPipe(
      resultType: Operand,
      dest: Operand,
      pipe: Operand,
      reserveId: Operand,
      index: Operand,
      pointer: Operand,
      packetSize: Operand,
      packetAlignment: Operand
  ) extends Instruction(
        Instruction.Definition.ReservedReadPipe,
        resultType,
        dest,
        pipe,
        reserveId,
        index,
        pointer,
        packetSize,
        packetAlignment
      )
  case ReservedWritePipe(
      resultType: Operand,
      dest: Operand,
      pipe: Operand,
      reserveId: Operand,
      index: Operand,
      pointer: Operand,
      packetSize: Operand,
      packetAlignment: Operand
  ) extends Instruction(
        Instruction.Definition.ReservedWritePipe,
        resultType,
        dest,
        pipe,
        reserveId,
        index,
        pointer,
        packetSize,
        packetAlignment
      )
  case ReserveReadPipePackets(
      resultType: Operand,
      dest: Operand,
      pipe: Operand,
      numPackets: Operand,
      packetSize: Operand,
      packetAlignment: Operand
  ) extends Instruction(
        Instruction.Definition.ReserveReadPipePackets,
        resultType,
        dest,
        pipe,
        numPackets,
        packetSize,
        packetAlignment
      )
  case ReserveWritePipePackets(
      resultType: Operand,
      dest: Operand,
      pipe: Operand,
      numPackets: Operand,
      packetSize: Operand,
      packetAlignment: Operand
  ) extends Instruction(
        Instruction.Definition.ReserveWritePipePackets,
        resultType,
        dest,
        pipe,
        numPackets,
        packetSize,
        packetAlignment
      )
  case CommitReadPipe(pipe: Operand, reserveId: Operand, packetSize: Operand, packetAlignment: Operand)
      extends Instruction(Instruction.Definition.CommitReadPipe, pipe, reserveId, packetSize, packetAlignment)
  case CommitWritePipe(pipe: Operand, reserveId: Operand, packetSize: Operand, packetAlignment: Operand)
      extends Instruction(Instruction.Definition.CommitWritePipe, pipe, reserveId, packetSize, packetAlignment)
  case IsValidReserveId(resultType: Operand, dest: Operand, reserveId: Operand)
      extends Instruction(Instruction.Definition.IsValidReserveId, resultType, dest, reserveId)
  case GetNumPipePackets(
      resultType: Operand,
      dest: Operand,
      pipe: Operand,
      packetSize: Operand,
      packetAlignment: Operand
  ) extends Instruction(Instruction.Definition.GetNumPipePackets, resultType, dest, pipe, packetSize, packetAlignment)
  case GetMaxPipePackets(
      resultType: Operand,
      dest: Operand,
      pipe: Operand,
      packetSize: Operand,
      packetAlignment: Operand
  ) extends Instruction(Instruction.Definition.GetMaxPipePackets, resultType, dest, pipe, packetSize, packetAlignment)
  case GroupReserveReadPipePackets(
      resultType: Operand,
      dest: Operand,
      execution: Operand,
      pipe: Operand,
      numPackets: Operand,
      packetSize: Operand,
      packetAlignment: Operand
  ) extends Instruction(
        Instruction.Definition.GroupReserveReadPipePackets,
        resultType,
        dest,
        execution,
        pipe,
        numPackets,
        packetSize,
        packetAlignment
      )
  case GroupReserveWritePipePackets(
      resultType: Operand,
      dest: Operand,
      execution: Operand,
      pipe: Operand,
      numPackets: Operand,
      packetSize: Operand,
      packetAlignment: Operand
  ) extends Instruction(
        Instruction.Definition.GroupReserveWritePipePackets,
        resultType,
        dest,
        execution,
        pipe,
        numPackets,
        packetSize,
        packetAlignment
      )
  case GroupCommitReadPipe(
      execution: Operand,
      pipe: Operand,
      reserveId: Operand,
      packetSize: Operand,
      packetAlignment: Operand
  ) extends Instruction(
        Instruction.Definition.GroupCommitReadPipe,
        execution,
        pipe,
        reserveId,
        packetSize,
        packetAlignment
      )
  case GroupCommitWritePipe(
      execution: Operand,
      pipe: Operand,
      reserveId: Operand,
      packetSize: Operand,
      packetAlignment: Operand
  ) extends Instruction(
        Instruction.Definition.GroupCommitWritePipe,
        execution,
        pipe,
        reserveId,
        packetSize,
        packetAlignment
      )
  case EnqueueMarker(
      resultType: Operand,
      dest: Operand,
      queue: Operand,
      numEvents: Operand,
      waitEvents: Operand,
      retEvent: Operand
  ) extends Instruction(Instruction.Definition.EnqueueMarker, resultType, dest, queue, numEvents, waitEvents, retEvent)
  case EnqueueKernel(
      resultType: Operand,
      dest: Operand,
      queue: Operand,
      flags: Operand,
      nDRange: Operand,
      numEvents: Operand,
      waitEvents: Operand,
      retEvent: Operand,
      invoke: Operand,
      param: Operand,
      paramSize: Operand,
      paramAlign: Operand,
      localSize: Vec[Operand]
  ) extends Instruction(
        Instruction.Definition.EnqueueKernel,
        (Vec(
          resultType,
          dest,
          queue,
          flags,
          nDRange,
          numEvents,
          waitEvents,
          retEvent,
          invoke,
          param,
          paramSize,
          paramAlign
        ) ++ localSize)*
      )
  case GetKernelNDrangeSubGroupCount(
      resultType: Operand,
      dest: Operand,
      nDRange: Operand,
      invoke: Operand,
      param: Operand,
      paramSize: Operand,
      paramAlign: Operand
  ) extends Instruction(
        Instruction.Definition.GetKernelNDrangeSubGroupCount,
        resultType,
        dest,
        nDRange,
        invoke,
        param,
        paramSize,
        paramAlign
      )
  case GetKernelNDrangeMaxSubGroupSize(
      resultType: Operand,
      dest: Operand,
      nDRange: Operand,
      invoke: Operand,
      param: Operand,
      paramSize: Operand,
      paramAlign: Operand
  ) extends Instruction(
        Instruction.Definition.GetKernelNDrangeMaxSubGroupSize,
        resultType,
        dest,
        nDRange,
        invoke,
        param,
        paramSize,
        paramAlign
      )
  case GetKernelWorkGroupSize(
      resultType: Operand,
      dest: Operand,
      invoke: Operand,
      param: Operand,
      paramSize: Operand,
      paramAlign: Operand
  ) extends Instruction(
        Instruction.Definition.GetKernelWorkGroupSize,
        resultType,
        dest,
        invoke,
        param,
        paramSize,
        paramAlign
      )
  case GetKernelPreferredWorkGroupSizeMultiple(
      resultType: Operand,
      dest: Operand,
      invoke: Operand,
      param: Operand,
      paramSize: Operand,
      paramAlign: Operand
  ) extends Instruction(
        Instruction.Definition.GetKernelPreferredWorkGroupSizeMultiple,
        resultType,
        dest,
        invoke,
        param,
        paramSize,
        paramAlign
      )
  case RetainEvent(event: Operand) extends Instruction(Instruction.Definition.RetainEvent, event)
  case ReleaseEvent(event: Operand) extends Instruction(Instruction.Definition.ReleaseEvent, event)
  case CreateUserEvent(resultType: Operand, dest: Operand)
      extends Instruction(Instruction.Definition.CreateUserEvent, resultType, dest)
  case IsValidEvent(resultType: Operand, dest: Operand, event: Operand)
      extends Instruction(Instruction.Definition.IsValidEvent, resultType, dest, event)
  case SetUserEventStatus(event: Operand, status: Operand)
      extends Instruction(Instruction.Definition.SetUserEventStatus, event, status)
  case CaptureEventProfilingInfo(event: Operand, profilingInfo: Operand, value: Operand)
      extends Instruction(Instruction.Definition.CaptureEventProfilingInfo, event, profilingInfo, value)
  case GetDefaultQueue(resultType: Operand, dest: Operand)
      extends Instruction(Instruction.Definition.GetDefaultQueue, resultType, dest)
  case BuildNDRange(
      resultType: Operand,
      dest: Operand,
      globalWorkSize: Operand,
      localWorkSize: Operand,
      globalWorkOffset: Operand
  ) extends Instruction(
        Instruction.Definition.BuildNDRange,
        resultType,
        dest,
        globalWorkSize,
        localWorkSize,
        globalWorkOffset
      )
  case ImageSparseSampleImplicitLod(
      resultType: Operand,
      dest: Operand,
      sampledImage: Operand,
      coordinate: Operand,
      mods: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.ImageSparseSampleImplicitLod,
        (Vec(resultType, dest, sampledImage, coordinate) ++ mods.toVec)*
      )
  case ImageSparseSampleExplicitLod(
      resultType: Operand,
      dest: Operand,
      sampledImage: Operand,
      coordinate: Operand,
      mods: Operand
  ) extends Instruction(
        Instruction.Definition.ImageSparseSampleExplicitLod,
        resultType,
        dest,
        sampledImage,
        coordinate,
        mods
      )
  case ImageSparseSampleDrefImplicitLod(
      resultType: Operand,
      dest: Operand,
      sampledImage: Operand,
      coordinate: Operand,
      dref: Operand,
      mods: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.ImageSparseSampleDrefImplicitLod,
        (Vec(resultType, dest, sampledImage, coordinate, dref) ++ mods.toVec)*
      )
  case ImageSparseSampleDrefExplicitLod(
      resultType: Operand,
      dest: Operand,
      sampledImage: Operand,
      coordinate: Operand,
      dref: Operand,
      mods: Operand
  ) extends Instruction(
        Instruction.Definition.ImageSparseSampleDrefExplicitLod,
        resultType,
        dest,
        sampledImage,
        coordinate,
        dref,
        mods
      )
  case ImageSparseSampleProjImplicitLod(
      resultType: Operand,
      dest: Operand,
      sampledImage: Operand,
      coordinate: Operand,
      mods: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.ImageSparseSampleProjImplicitLod,
        (Vec(resultType, dest, sampledImage, coordinate) ++ mods.toVec)*
      )
  case ImageSparseSampleProjExplicitLod(
      resultType: Operand,
      dest: Operand,
      sampledImage: Operand,
      coordinate: Operand,
      mods: Operand
  ) extends Instruction(
        Instruction.Definition.ImageSparseSampleProjExplicitLod,
        resultType,
        dest,
        sampledImage,
        coordinate,
        mods
      )
  case ImageSparseSampleProjDrefImplicitLod(
      resultType: Operand,
      dest: Operand,
      sampledImage: Operand,
      coordinate: Operand,
      dref: Operand,
      mods: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.ImageSparseSampleProjDrefImplicitLod,
        (Vec(resultType, dest, sampledImage, coordinate, dref) ++ mods.toVec)*
      )
  case ImageSparseSampleProjDrefExplicitLod(
      resultType: Operand,
      dest: Operand,
      sampledImage: Operand,
      coordinate: Operand,
      dref: Operand,
      mods: Operand
  ) extends Instruction(
        Instruction.Definition.ImageSparseSampleProjDrefExplicitLod,
        resultType,
        dest,
        sampledImage,
        coordinate,
        dref,
        mods
      )
  case ImageSparseFetch(resultType: Operand, dest: Operand, image: Operand, coordinate: Operand, mods: Option[Operand])
      extends Instruction(
        Instruction.Definition.ImageSparseFetch,
        (Vec(resultType, dest, image, coordinate) ++ mods.toVec)*
      )
  case ImageSparseGather(
      resultType: Operand,
      dest: Operand,
      sampledImage: Operand,
      coordinate: Operand,
      component: Operand,
      mods: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.ImageSparseGather,
        (Vec(resultType, dest, sampledImage, coordinate, component) ++ mods.toVec)*
      )
  case ImageSparseDrefGather(
      resultType: Operand,
      dest: Operand,
      sampledImage: Operand,
      coordinate: Operand,
      dref: Operand,
      mods: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.ImageSparseDrefGather,
        (Vec(resultType, dest, sampledImage, coordinate, dref) ++ mods.toVec)*
      )
  case ImageSparseTexelsResident(resultType: Operand, dest: Operand, residentCode: Operand)
      extends Instruction(Instruction.Definition.ImageSparseTexelsResident, resultType, dest, residentCode)
  case NoLine() extends Instruction(Instruction.Definition.NoLine)
  case AtomicFlagTestAndSet(resultType: Operand, dest: Operand, pointer: Operand, memory: Operand, semantics: Operand)
      extends Instruction(Instruction.Definition.AtomicFlagTestAndSet, resultType, dest, pointer, memory, semantics)
  case AtomicFlagClear(pointer: Operand, memory: Operand, semantics: Operand)
      extends Instruction(Instruction.Definition.AtomicFlagClear, pointer, memory, semantics)
  case ImageSparseRead(resultType: Operand, dest: Operand, image: Operand, coordinate: Operand, mods: Option[Operand])
      extends Instruction(
        Instruction.Definition.ImageSparseRead,
        (Vec(resultType, dest, image, coordinate) ++ mods.toVec)*
      )
  case SizeOf(resultType: Operand, dest: Operand, pointer: Operand)
      extends Instruction(Instruction.Definition.SizeOf, resultType, dest, pointer)
  case TypePipeStorage(dest: Operand) extends Instruction(Instruction.Definition.TypePipeStorage, dest)
  case ConstantPipeStorage(
      resultType: Operand,
      dest: Operand,
      packetSize: Operand,
      packetAlignment: Operand,
      capacity: Operand
  ) extends Instruction(
        Instruction.Definition.ConstantPipeStorage,
        resultType,
        dest,
        packetSize,
        packetAlignment,
        capacity
      )
  case CreatePipeFromPipeStorage(resultType: Operand, dest: Operand, pipeStorage: Operand)
      extends Instruction(Instruction.Definition.CreatePipeFromPipeStorage, resultType, dest, pipeStorage)
  case GetKernelLocalSizeForSubgroupCount(
      resultType: Operand,
      dest: Operand,
      subgroupCount: Operand,
      invoke: Operand,
      param: Operand,
      paramSize: Operand,
      paramAlign: Operand
  ) extends Instruction(
        Instruction.Definition.GetKernelLocalSizeForSubgroupCount,
        resultType,
        dest,
        subgroupCount,
        invoke,
        param,
        paramSize,
        paramAlign
      )
  case GetKernelMaxNumSubgroups(
      resultType: Operand,
      dest: Operand,
      invoke: Operand,
      param: Operand,
      paramSize: Operand,
      paramAlign: Operand
  ) extends Instruction(
        Instruction.Definition.GetKernelMaxNumSubgroups,
        resultType,
        dest,
        invoke,
        param,
        paramSize,
        paramAlign
      )
  case TypeNamedBarrier(dest: Operand) extends Instruction(Instruction.Definition.TypeNamedBarrier, dest)
  case NamedBarrierInitialize(resultType: Operand, dest: Operand, subgroupCount: Operand)
      extends Instruction(Instruction.Definition.NamedBarrierInitialize, resultType, dest, subgroupCount)
  case MemoryNamedBarrier(namedBarrier: Operand, memory: Operand, semantics: Operand)
      extends Instruction(Instruction.Definition.MemoryNamedBarrier, namedBarrier, memory, semantics)
  case ModuleProcessed(process: Operand) extends Instruction(Instruction.Definition.ModuleProcessed, process)
  case ExecutionModeId(entryPoint: Operand, mode: Operand)
      extends Instruction(Instruction.Definition.ExecutionModeId, entryPoint, mode)
  case DecorateId(target: Operand, decoration: Operand)
      extends Instruction(Instruction.Definition.DecorateId, target, decoration)
  case GroupNonUniformElect(resultType: Operand, dest: Operand, execution: Operand)
      extends Instruction(Instruction.Definition.GroupNonUniformElect, resultType, dest, execution)
  case GroupNonUniformAll(resultType: Operand, dest: Operand, execution: Operand, pred: Operand)
      extends Instruction(Instruction.Definition.GroupNonUniformAll, resultType, dest, execution, pred)
  case GroupNonUniformAny(resultType: Operand, dest: Operand, execution: Operand, pred: Operand)
      extends Instruction(Instruction.Definition.GroupNonUniformAny, resultType, dest, execution, pred)
  case GroupNonUniformAllEqual(resultType: Operand, dest: Operand, execution: Operand, value: Operand)
      extends Instruction(Instruction.Definition.GroupNonUniformAllEqual, resultType, dest, execution, value)
  case GroupNonUniformBroadcast(
      resultType: Operand,
      dest: Operand,
      execution: Operand,
      value: Operand,
      invocationId: Operand
  ) extends Instruction(
        Instruction.Definition.GroupNonUniformBroadcast,
        resultType,
        dest,
        execution,
        value,
        invocationId
      )
  case GroupNonUniformBroadcastFirst(resultType: Operand, dest: Operand, execution: Operand, value: Operand)
      extends Instruction(Instruction.Definition.GroupNonUniformBroadcastFirst, resultType, dest, execution, value)
  case GroupNonUniformBallot(resultType: Operand, dest: Operand, execution: Operand, pred: Operand)
      extends Instruction(Instruction.Definition.GroupNonUniformBallot, resultType, dest, execution, pred)
  case GroupNonUniformInverseBallot(resultType: Operand, dest: Operand, execution: Operand, value: Operand)
      extends Instruction(Instruction.Definition.GroupNonUniformInverseBallot, resultType, dest, execution, value)
  case GroupNonUniformBallotBitExtract(
      resultType: Operand,
      dest: Operand,
      execution: Operand,
      value: Operand,
      index: Operand
  ) extends Instruction(
        Instruction.Definition.GroupNonUniformBallotBitExtract,
        resultType,
        dest,
        execution,
        value,
        index
      )
  case GroupNonUniformBallotBitCount(
      resultType: Operand,
      dest: Operand,
      execution: Operand,
      operation: Operand,
      value: Operand
  ) extends Instruction(
        Instruction.Definition.GroupNonUniformBallotBitCount,
        resultType,
        dest,
        execution,
        operation,
        value
      )
  case GroupNonUniformBallotFindLSB(resultType: Operand, dest: Operand, execution: Operand, value: Operand)
      extends Instruction(Instruction.Definition.GroupNonUniformBallotFindLSB, resultType, dest, execution, value)
  case GroupNonUniformBallotFindMSB(resultType: Operand, dest: Operand, execution: Operand, value: Operand)
      extends Instruction(Instruction.Definition.GroupNonUniformBallotFindMSB, resultType, dest, execution, value)
  case GroupNonUniformShuffle(
      resultType: Operand,
      dest: Operand,
      execution: Operand,
      value: Operand,
      invocationId: Operand
  ) extends Instruction(Instruction.Definition.GroupNonUniformShuffle, resultType, dest, execution, value, invocationId)
  case GroupNonUniformShuffleXor(resultType: Operand, dest: Operand, execution: Operand, value: Operand, mask: Operand)
      extends Instruction(Instruction.Definition.GroupNonUniformShuffleXor, resultType, dest, execution, value, mask)
  case GroupNonUniformShuffleUp(resultType: Operand, dest: Operand, execution: Operand, value: Operand, delta: Operand)
      extends Instruction(Instruction.Definition.GroupNonUniformShuffleUp, resultType, dest, execution, value, delta)
  case GroupNonUniformShuffleDown(
      resultType: Operand,
      dest: Operand,
      execution: Operand,
      value: Operand,
      delta: Operand
  ) extends Instruction(Instruction.Definition.GroupNonUniformShuffleDown, resultType, dest, execution, value, delta)
  case GroupNonUniformIAdd(
      resultType: Operand,
      dest: Operand,
      execution: Operand,
      operation: Operand,
      value: Operand,
      clusterSize: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.GroupNonUniformIAdd,
        (Vec(resultType, dest, execution, operation, value) ++ clusterSize.toVec)*
      )
  case GroupNonUniformFAdd(
      resultType: Operand,
      dest: Operand,
      execution: Operand,
      operation: Operand,
      value: Operand,
      clusterSize: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.GroupNonUniformFAdd,
        (Vec(resultType, dest, execution, operation, value) ++ clusterSize.toVec)*
      )
  case GroupNonUniformIMul(
      resultType: Operand,
      dest: Operand,
      execution: Operand,
      operation: Operand,
      value: Operand,
      clusterSize: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.GroupNonUniformIMul,
        (Vec(resultType, dest, execution, operation, value) ++ clusterSize.toVec)*
      )
  case GroupNonUniformFMul(
      resultType: Operand,
      dest: Operand,
      execution: Operand,
      operation: Operand,
      value: Operand,
      clusterSize: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.GroupNonUniformFMul,
        (Vec(resultType, dest, execution, operation, value) ++ clusterSize.toVec)*
      )
  case GroupNonUniformSMin(
      resultType: Operand,
      dest: Operand,
      execution: Operand,
      operation: Operand,
      value: Operand,
      clusterSize: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.GroupNonUniformSMin,
        (Vec(resultType, dest, execution, operation, value) ++ clusterSize.toVec)*
      )
  case GroupNonUniformUMin(
      resultType: Operand,
      dest: Operand,
      execution: Operand,
      operation: Operand,
      value: Operand,
      clusterSize: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.GroupNonUniformUMin,
        (Vec(resultType, dest, execution, operation, value) ++ clusterSize.toVec)*
      )
  case GroupNonUniformFMin(
      resultType: Operand,
      dest: Operand,
      execution: Operand,
      operation: Operand,
      value: Operand,
      clusterSize: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.GroupNonUniformFMin,
        (Vec(resultType, dest, execution, operation, value) ++ clusterSize.toVec)*
      )
  case GroupNonUniformSMax(
      resultType: Operand,
      dest: Operand,
      execution: Operand,
      operation: Operand,
      value: Operand,
      clusterSize: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.GroupNonUniformSMax,
        (Vec(resultType, dest, execution, operation, value) ++ clusterSize.toVec)*
      )
  case GroupNonUniformUMax(
      resultType: Operand,
      dest: Operand,
      execution: Operand,
      operation: Operand,
      value: Operand,
      clusterSize: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.GroupNonUniformUMax,
        (Vec(resultType, dest, execution, operation, value) ++ clusterSize.toVec)*
      )
  case GroupNonUniformFMax(
      resultType: Operand,
      dest: Operand,
      execution: Operand,
      operation: Operand,
      value: Operand,
      clusterSize: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.GroupNonUniformFMax,
        (Vec(resultType, dest, execution, operation, value) ++ clusterSize.toVec)*
      )
  case GroupNonUniformBitwiseAnd(
      resultType: Operand,
      dest: Operand,
      execution: Operand,
      operation: Operand,
      value: Operand,
      clusterSize: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.GroupNonUniformBitwiseAnd,
        (Vec(resultType, dest, execution, operation, value) ++ clusterSize.toVec)*
      )
  case GroupNonUniformBitwiseOr(
      resultType: Operand,
      dest: Operand,
      execution: Operand,
      operation: Operand,
      value: Operand,
      clusterSize: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.GroupNonUniformBitwiseOr,
        (Vec(resultType, dest, execution, operation, value) ++ clusterSize.toVec)*
      )
  case GroupNonUniformBitwiseXor(
      resultType: Operand,
      dest: Operand,
      execution: Operand,
      operation: Operand,
      value: Operand,
      clusterSize: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.GroupNonUniformBitwiseXor,
        (Vec(resultType, dest, execution, operation, value) ++ clusterSize.toVec)*
      )
  case GroupNonUniformLogicalAnd(
      resultType: Operand,
      dest: Operand,
      execution: Operand,
      operation: Operand,
      value: Operand,
      clusterSize: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.GroupNonUniformLogicalAnd,
        (Vec(resultType, dest, execution, operation, value) ++ clusterSize.toVec)*
      )
  case GroupNonUniformLogicalOr(
      resultType: Operand,
      dest: Operand,
      execution: Operand,
      operation: Operand,
      value: Operand,
      clusterSize: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.GroupNonUniformLogicalOr,
        (Vec(resultType, dest, execution, operation, value) ++ clusterSize.toVec)*
      )
  case GroupNonUniformLogicalXor(
      resultType: Operand,
      dest: Operand,
      execution: Operand,
      operation: Operand,
      value: Operand,
      clusterSize: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.GroupNonUniformLogicalXor,
        (Vec(resultType, dest, execution, operation, value) ++ clusterSize.toVec)*
      )
  case GroupNonUniformQuadBroadcast(
      resultType: Operand,
      dest: Operand,
      execution: Operand,
      value: Operand,
      index: Operand
  ) extends Instruction(Instruction.Definition.GroupNonUniformQuadBroadcast, resultType, dest, execution, value, index)
  case GroupNonUniformQuadSwap(
      resultType: Operand,
      dest: Operand,
      execution: Operand,
      value: Operand,
      direction: Operand
  ) extends Instruction(Instruction.Definition.GroupNonUniformQuadSwap, resultType, dest, execution, value, direction)
  case CopyLogical(resultType: Operand, dest: Operand, src: Operand)
      extends Instruction(Instruction.Definition.CopyLogical, resultType, dest, src)
  case PtrEqual(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.PtrEqual, resultType, dest, srcA, srcB)
  case PtrNotEqual(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.PtrNotEqual, resultType, dest, srcA, srcB)
  case PtrDiff(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.PtrDiff, resultType, dest, srcA, srcB)
  case ColorAttachmentReadEXT(resultType: Operand, dest: Operand, attachment: Operand, sample: Option[Operand])
      extends Instruction(
        Instruction.Definition.ColorAttachmentReadEXT,
        (Vec(resultType, dest, attachment) ++ sample.toVec)*
      )
  case DepthAttachmentReadEXT(resultType: Operand, dest: Operand, sample: Option[Operand])
      extends Instruction(Instruction.Definition.DepthAttachmentReadEXT, (Vec(resultType, dest) ++ sample.toVec)*)
  case StencilAttachmentReadEXT(resultType: Operand, dest: Operand, sample: Option[Operand])
      extends Instruction(Instruction.Definition.StencilAttachmentReadEXT, (Vec(resultType, dest) ++ sample.toVec)*)
  case TypeTensorARM(dest: Operand, elementType: Operand, rank: Option[Operand], shape: Option[Operand])
      extends Instruction(Instruction.Definition.TypeTensorARM, (Vec(dest, elementType) ++ rank.toVec ++ shape.toVec)*)
  case TensorReadARM(
      resultType: Operand,
      dest: Operand,
      tensor: Operand,
      coordinates: Operand,
      tensorOperands: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.TensorReadARM,
        (Vec(resultType, dest, tensor, coordinates) ++ tensorOperands.toVec)*
      )
  case TensorWriteARM(tensor: Operand, coordinates: Operand, src: Operand, tensorOperands: Option[Operand])
      extends Instruction(
        Instruction.Definition.TensorWriteARM,
        (Vec(tensor, coordinates, src) ++ tensorOperands.toVec)*
      )
  case TensorQuerySizeARM(resultType: Operand, dest: Operand, tensor: Operand, dimension: Operand)
      extends Instruction(Instruction.Definition.TensorQuerySizeARM, resultType, dest, tensor, dimension)
  case GraphConstantARM(resultType: Operand, dest: Operand, graphConstantID: Operand)
      extends Instruction(Instruction.Definition.GraphConstantARM, resultType, dest, graphConstantID)
  case GraphEntryPointARM(graph: Operand, name: Operand, interface: Vec[Operand])
      extends Instruction(Instruction.Definition.GraphEntryPointARM, (Vec(graph, name) ++ interface)*)
  case GraphARM(resultType: Operand, dest: Operand)
      extends Instruction(Instruction.Definition.GraphARM, resultType, dest)
  case GraphInputARM(resultType: Operand, dest: Operand, inputIndex: Operand, elementIndex: Vec[Operand])
      extends Instruction(Instruction.Definition.GraphInputARM, (Vec(resultType, dest, inputIndex) ++ elementIndex)*)
  case GraphSetOutputARM(value: Operand, outputIndex: Operand, elementIndex: Vec[Operand])
      extends Instruction(Instruction.Definition.GraphSetOutputARM, (Vec(value, outputIndex) ++ elementIndex)*)
  case GraphEndARM() extends Instruction(Instruction.Definition.GraphEndARM)
  case TypeGraphARM(dest: Operand, numInputs: Operand, inOutTypes: Vec[Operand])
      extends Instruction(Instruction.Definition.TypeGraphARM, (Vec(dest, numInputs) ++ inOutTypes)*)
  case BitcastExtractEXT(resultType: Operand, dest: Operand, base: Operand, offset: Operand)
      extends Instruction(Instruction.Definition.BitcastExtractEXT, resultType, dest, base, offset)
  case TerminateInvocation() extends Instruction(Instruction.Definition.TerminateInvocation)
  case TypeUntypedPointerKHR(dest: Operand, storageClass: Operand)
      extends Instruction(Instruction.Definition.TypeUntypedPointerKHR, dest, storageClass)
  case UntypedVariableKHR(
      resultType: Operand,
      dest: Operand,
      storageClass: Operand,
      dataType: Option[Operand],
      initializer: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.UntypedVariableKHR,
        (Vec(resultType, dest, storageClass) ++ dataType.toVec ++ initializer.toVec)*
      )
  case UntypedAccessChainKHR(
      resultType: Operand,
      dest: Operand,
      baseType: Operand,
      base: Operand,
      indexes: Vec[Operand]
  ) extends Instruction(
        Instruction.Definition.UntypedAccessChainKHR,
        (Vec(resultType, dest, baseType, base) ++ indexes)*
      )
  case UntypedInBoundsAccessChainKHR(
      resultType: Operand,
      dest: Operand,
      baseType: Operand,
      base: Operand,
      indexes: Vec[Operand]
  ) extends Instruction(
        Instruction.Definition.UntypedInBoundsAccessChainKHR,
        (Vec(resultType, dest, baseType, base) ++ indexes)*
      )
  case SubgroupBallotKHR(resultType: Operand, dest: Operand, pred: Operand)
      extends Instruction(Instruction.Definition.SubgroupBallotKHR, resultType, dest, pred)
  case SubgroupFirstInvocationKHR(resultType: Operand, dest: Operand, value: Operand)
      extends Instruction(Instruction.Definition.SubgroupFirstInvocationKHR, resultType, dest, value)
  case UntypedPtrAccessChainKHR(
      resultType: Operand,
      dest: Operand,
      baseType: Operand,
      base: Operand,
      element: Operand,
      indexes: Vec[Operand]
  ) extends Instruction(
        Instruction.Definition.UntypedPtrAccessChainKHR,
        (Vec(resultType, dest, baseType, base, element) ++ indexes)*
      )
  case UntypedInBoundsPtrAccessChainKHR(
      resultType: Operand,
      dest: Operand,
      baseType: Operand,
      base: Operand,
      element: Operand,
      indexes: Vec[Operand]
  ) extends Instruction(
        Instruction.Definition.UntypedInBoundsPtrAccessChainKHR,
        (Vec(resultType, dest, baseType, base, element) ++ indexes)*
      )
  case UntypedArrayLengthKHR(
      resultType: Operand,
      dest: Operand,
      structure: Operand,
      pointer: Operand,
      arrayMember: Operand
  ) extends Instruction(Instruction.Definition.UntypedArrayLengthKHR, resultType, dest, structure, pointer, arrayMember)
  case UntypedPrefetchKHR(
      pointerType: Operand,
      numBytes: Operand,
      rW: Option[Operand],
      locality: Option[Operand],
      cacheType: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.UntypedPrefetchKHR,
        (Vec(pointerType, numBytes) ++ rW.toVec ++ locality.toVec ++ cacheType.toVec)*
      )
  case FmaKHR(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand)
      extends Instruction(Instruction.Definition.FmaKHR, resultType, dest, srcA, srcB, srcC)
  case SubgroupAllKHR(resultType: Operand, dest: Operand, pred: Operand)
      extends Instruction(Instruction.Definition.SubgroupAllKHR, resultType, dest, pred)
  case SubgroupAnyKHR(resultType: Operand, dest: Operand, pred: Operand)
      extends Instruction(Instruction.Definition.SubgroupAnyKHR, resultType, dest, pred)
  case SubgroupAllEqualKHR(resultType: Operand, dest: Operand, pred: Operand)
      extends Instruction(Instruction.Definition.SubgroupAllEqualKHR, resultType, dest, pred)
  case GroupNonUniformRotateKHR(
      resultType: Operand,
      dest: Operand,
      execution: Operand,
      value: Operand,
      delta: Operand,
      clusterSize: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.GroupNonUniformRotateKHR,
        (Vec(resultType, dest, execution, value, delta) ++ clusterSize.toVec)*
      )
  case SubgroupReadInvocationKHR(resultType: Operand, dest: Operand, value: Operand, index: Operand)
      extends Instruction(Instruction.Definition.SubgroupReadInvocationKHR, resultType, dest, value, index)
  case ExtInstWithForwardRefsKHR(resultType: Operand, dest: Operand, set: Operand, inst: Operand, srcs: Vec[Operand])
      extends Instruction(Instruction.Definition.ExtInstWithForwardRefsKHR, (Vec(resultType, dest, set, inst) ++ srcs)*)
  case UntypedGroupAsyncCopyKHR(
      resultType: Operand,
      dest: Operand,
      execution: Operand,
      destination: Operand,
      src: Operand,
      elementNumBytes: Operand,
      numElements: Operand,
      stride: Operand,
      event: Operand,
      modsA: Option[Operand],
      modsB: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.UntypedGroupAsyncCopyKHR,
        (Vec(resultType, dest, execution, destination, src, elementNumBytes, numElements, stride, event) ++
          modsA.toVec ++ modsB.toVec)*
      )
  case TraceRayKHR(
      accel: Operand,
      rayFlags: Operand,
      cullMask: Operand,
      sBTOffset: Operand,
      sBTStride: Operand,
      missIndex: Operand,
      rayOrigin: Operand,
      rayTmin: Operand,
      rayDirection: Operand,
      rayTmax: Operand,
      payload: Operand
  ) extends Instruction(
        Instruction.Definition.TraceRayKHR,
        accel,
        rayFlags,
        cullMask,
        sBTOffset,
        sBTStride,
        missIndex,
        rayOrigin,
        rayTmin,
        rayDirection,
        rayTmax,
        payload
      )
  case ExecuteCallableKHR(sBTIndex: Operand, callableData: Operand)
      extends Instruction(Instruction.Definition.ExecuteCallableKHR, sBTIndex, callableData)
  case ConvertUToAccelerationStructureKHR(resultType: Operand, dest: Operand, accel: Operand)
      extends Instruction(Instruction.Definition.ConvertUToAccelerationStructureKHR, resultType, dest, accel)
  case IgnoreIntersectionKHR() extends Instruction(Instruction.Definition.IgnoreIntersectionKHR)
  case TerminateRayKHR() extends Instruction(Instruction.Definition.TerminateRayKHR)
  case SDot(resultType: Operand, dest: Operand, vector1: Operand, vector2: Operand, packedVectorFormat: Option[Operand])
      extends Instruction(
        Instruction.Definition.SDot,
        (Vec(resultType, dest, vector1, vector2) ++ packedVectorFormat.toVec)*
      )
  case UDot(resultType: Operand, dest: Operand, vector1: Operand, vector2: Operand, packedVectorFormat: Option[Operand])
      extends Instruction(
        Instruction.Definition.UDot,
        (Vec(resultType, dest, vector1, vector2) ++ packedVectorFormat.toVec)*
      )
  case SUDot(
      resultType: Operand,
      dest: Operand,
      vector1: Operand,
      vector2: Operand,
      packedVectorFormat: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.SUDot,
        (Vec(resultType, dest, vector1, vector2) ++ packedVectorFormat.toVec)*
      )
  case SDotAccSat(
      resultType: Operand,
      dest: Operand,
      vector1: Operand,
      vector2: Operand,
      accumulator: Operand,
      packedVectorFormat: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.SDotAccSat,
        (Vec(resultType, dest, vector1, vector2, accumulator) ++ packedVectorFormat.toVec)*
      )
  case UDotAccSat(
      resultType: Operand,
      dest: Operand,
      vector1: Operand,
      vector2: Operand,
      accumulator: Operand,
      packedVectorFormat: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.UDotAccSat,
        (Vec(resultType, dest, vector1, vector2, accumulator) ++ packedVectorFormat.toVec)*
      )
  case SUDotAccSat(
      resultType: Operand,
      dest: Operand,
      vector1: Operand,
      vector2: Operand,
      accumulator: Operand,
      packedVectorFormat: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.SUDotAccSat,
        (Vec(resultType, dest, vector1, vector2, accumulator) ++ packedVectorFormat.toVec)*
      )
  case TypeCooperativeMatrixKHR(
      dest: Operand,
      componentType: Operand,
      scope: Operand,
      rows: Operand,
      columns: Operand,
      use: Operand
  ) extends Instruction(Instruction.Definition.TypeCooperativeMatrixKHR, dest, componentType, scope, rows, columns, use)
  case CooperativeMatrixLoadKHR(
      resultType: Operand,
      dest: Operand,
      pointer: Operand,
      memoryLayout: Operand,
      stride: Option[Operand],
      mods: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.CooperativeMatrixLoadKHR,
        (Vec(resultType, dest, pointer, memoryLayout) ++ stride.toVec ++ mods.toVec)*
      )
  case CooperativeMatrixStoreKHR(
      pointer: Operand,
      src: Operand,
      memoryLayout: Operand,
      stride: Option[Operand],
      mods: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.CooperativeMatrixStoreKHR,
        (Vec(pointer, src, memoryLayout) ++ stride.toVec ++ mods.toVec)*
      )
  case CooperativeMatrixMulAddKHR(
      resultType: Operand,
      dest: Operand,
      a: Operand,
      b: Operand,
      c: Operand,
      cooperativeMatrixOperands: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.CooperativeMatrixMulAddKHR,
        (Vec(resultType, dest, a, b, c) ++ cooperativeMatrixOperands.toVec)*
      )
  case CooperativeMatrixLengthKHR(resultType: Operand, dest: Operand, dataType: Operand)
      extends Instruction(Instruction.Definition.CooperativeMatrixLengthKHR, resultType, dest, dataType)
  case ConstantCompositeReplicateEXT(resultType: Operand, dest: Operand, value: Operand)
      extends Instruction(Instruction.Definition.ConstantCompositeReplicateEXT, resultType, dest, value)
  case SpecConstantCompositeReplicateEXT(resultType: Operand, dest: Operand, value: Operand)
      extends Instruction(Instruction.Definition.SpecConstantCompositeReplicateEXT, resultType, dest, value)
  case CompositeConstructReplicateEXT(resultType: Operand, dest: Operand, value: Operand)
      extends Instruction(Instruction.Definition.CompositeConstructReplicateEXT, resultType, dest, value)
  case TypeRayQueryKHR(dest: Operand) extends Instruction(Instruction.Definition.TypeRayQueryKHR, dest)
  case RayQueryInitializeKHR(
      rayQuery: Operand,
      accel: Operand,
      rayFlags: Operand,
      cullMask: Operand,
      rayOrigin: Operand,
      rayTMin: Operand,
      rayDirection: Operand,
      rayTMax: Operand
  ) extends Instruction(
        Instruction.Definition.RayQueryInitializeKHR,
        rayQuery,
        accel,
        rayFlags,
        cullMask,
        rayOrigin,
        rayTMin,
        rayDirection,
        rayTMax
      )
  case RayQueryTerminateKHR(rayQuery: Operand)
      extends Instruction(Instruction.Definition.RayQueryTerminateKHR, rayQuery)
  case RayQueryGenerateIntersectionKHR(rayQuery: Operand, hitT: Operand)
      extends Instruction(Instruction.Definition.RayQueryGenerateIntersectionKHR, rayQuery, hitT)
  case RayQueryConfirmIntersectionKHR(rayQuery: Operand)
      extends Instruction(Instruction.Definition.RayQueryConfirmIntersectionKHR, rayQuery)
  case RayQueryProceedKHR(resultType: Operand, dest: Operand, rayQuery: Operand)
      extends Instruction(Instruction.Definition.RayQueryProceedKHR, resultType, dest, rayQuery)
  case RayQueryGetIntersectionTypeKHR(
      resultType: Operand,
      dest: Operand,
      rayQuery: Operand,
      intersection: Operand
  ) extends Instruction(Instruction.Definition.RayQueryGetIntersectionTypeKHR, resultType, dest, rayQuery, intersection)
  case ImageSampleWeightedQCOM(
      resultType: Operand,
      dest: Operand,
      texture: Operand,
      coordinates: Operand,
      weights: Operand
  ) extends Instruction(Instruction.Definition.ImageSampleWeightedQCOM, resultType, dest, texture, coordinates, weights)
  case ImageBoxFilterQCOM(resultType: Operand, dest: Operand, texture: Operand, coordinates: Operand, boxSize: Operand)
      extends Instruction(Instruction.Definition.ImageBoxFilterQCOM, resultType, dest, texture, coordinates, boxSize)
  case ImageBlockMatchSSDQCOM(
      resultType: Operand,
      dest: Operand,
      target: Operand,
      targetCoordinates: Operand,
      reference: Operand,
      referenceCoordinates: Operand,
      blockSize: Operand
  ) extends Instruction(
        Instruction.Definition.ImageBlockMatchSSDQCOM,
        resultType,
        dest,
        target,
        targetCoordinates,
        reference,
        referenceCoordinates,
        blockSize
      )
  case ImageBlockMatchSADQCOM(
      resultType: Operand,
      dest: Operand,
      target: Operand,
      targetCoordinates: Operand,
      reference: Operand,
      referenceCoordinates: Operand,
      blockSize: Operand
  ) extends Instruction(
        Instruction.Definition.ImageBlockMatchSADQCOM,
        resultType,
        dest,
        target,
        targetCoordinates,
        reference,
        referenceCoordinates,
        blockSize
      )
  case BitCastArrayQCOM(resultType: Operand, dest: Operand, srcArray: Operand)
      extends Instruction(Instruction.Definition.BitCastArrayQCOM, resultType, dest, srcArray)
  case ImageBlockMatchWindowSSDQCOM(
      resultType: Operand,
      dest: Operand,
      targetSampledImage: Operand,
      targetCoordinates: Operand,
      referenceSampledImage: Operand,
      referenceCoordinates: Operand,
      blockSize: Operand
  ) extends Instruction(
        Instruction.Definition.ImageBlockMatchWindowSSDQCOM,
        resultType,
        dest,
        targetSampledImage,
        targetCoordinates,
        referenceSampledImage,
        referenceCoordinates,
        blockSize
      )
  case ImageBlockMatchWindowSADQCOM(
      resultType: Operand,
      dest: Operand,
      targetSampledImage: Operand,
      targetCoordinates: Operand,
      referenceSampledImage: Operand,
      referenceCoordinates: Operand,
      blockSize: Operand
  ) extends Instruction(
        Instruction.Definition.ImageBlockMatchWindowSADQCOM,
        resultType,
        dest,
        targetSampledImage,
        targetCoordinates,
        referenceSampledImage,
        referenceCoordinates,
        blockSize
      )
  case ImageBlockMatchGatherSSDQCOM(
      resultType: Operand,
      dest: Operand,
      targetSampledImage: Operand,
      targetCoordinates: Operand,
      referenceSampledImage: Operand,
      referenceCoordinates: Operand,
      blockSize: Operand
  ) extends Instruction(
        Instruction.Definition.ImageBlockMatchGatherSSDQCOM,
        resultType,
        dest,
        targetSampledImage,
        targetCoordinates,
        referenceSampledImage,
        referenceCoordinates,
        blockSize
      )
  case ImageBlockMatchGatherSADQCOM(
      resultType: Operand,
      dest: Operand,
      targetSampledImage: Operand,
      targetCoordinates: Operand,
      referenceSampledImage: Operand,
      referenceCoordinates: Operand,
      blockSize: Operand
  ) extends Instruction(
        Instruction.Definition.ImageBlockMatchGatherSADQCOM,
        resultType,
        dest,
        targetSampledImage,
        targetCoordinates,
        referenceSampledImage,
        referenceCoordinates,
        blockSize
      )
  case CompositeConstructCoopMatQCOM(resultType: Operand, dest: Operand, srcArray: Operand)
      extends Instruction(Instruction.Definition.CompositeConstructCoopMatQCOM, resultType, dest, srcArray)
  case CompositeExtractCoopMatQCOM(resultType: Operand, dest: Operand, srcCooperativeMatrix: Operand)
      extends Instruction(Instruction.Definition.CompositeExtractCoopMatQCOM, resultType, dest, srcCooperativeMatrix)
  case ExtractSubArrayQCOM(resultType: Operand, dest: Operand, srcArray: Operand, index: Operand)
      extends Instruction(Instruction.Definition.ExtractSubArrayQCOM, resultType, dest, srcArray, index)
  case ImageGatherQCOM(
      resultType: Operand,
      dest: Operand,
      sampledImage: Operand,
      coordinate: Operand,
      component: Operand,
      mode: Operand,
      mods: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.ImageGatherQCOM,
        (Vec(resultType, dest, sampledImage, coordinate, component, mode) ++ mods.toVec)*
      )
  case GroupIAddNonUniformAMD(resultType: Operand, dest: Operand, execution: Operand, operation: Operand, x: Operand)
      extends Instruction(Instruction.Definition.GroupIAddNonUniformAMD, resultType, dest, execution, operation, x)
  case GroupFAddNonUniformAMD(resultType: Operand, dest: Operand, execution: Operand, operation: Operand, x: Operand)
      extends Instruction(Instruction.Definition.GroupFAddNonUniformAMD, resultType, dest, execution, operation, x)
  case GroupFMinNonUniformAMD(resultType: Operand, dest: Operand, execution: Operand, operation: Operand, x: Operand)
      extends Instruction(Instruction.Definition.GroupFMinNonUniformAMD, resultType, dest, execution, operation, x)
  case GroupUMinNonUniformAMD(resultType: Operand, dest: Operand, execution: Operand, operation: Operand, x: Operand)
      extends Instruction(Instruction.Definition.GroupUMinNonUniformAMD, resultType, dest, execution, operation, x)
  case GroupSMinNonUniformAMD(resultType: Operand, dest: Operand, execution: Operand, operation: Operand, x: Operand)
      extends Instruction(Instruction.Definition.GroupSMinNonUniformAMD, resultType, dest, execution, operation, x)
  case GroupFMaxNonUniformAMD(resultType: Operand, dest: Operand, execution: Operand, operation: Operand, x: Operand)
      extends Instruction(Instruction.Definition.GroupFMaxNonUniformAMD, resultType, dest, execution, operation, x)
  case GroupUMaxNonUniformAMD(resultType: Operand, dest: Operand, execution: Operand, operation: Operand, x: Operand)
      extends Instruction(Instruction.Definition.GroupUMaxNonUniformAMD, resultType, dest, execution, operation, x)
  case GroupSMaxNonUniformAMD(resultType: Operand, dest: Operand, execution: Operand, operation: Operand, x: Operand)
      extends Instruction(Instruction.Definition.GroupSMaxNonUniformAMD, resultType, dest, execution, operation, x)
  case FragmentMaskFetchAMD(resultType: Operand, dest: Operand, image: Operand, coordinate: Operand)
      extends Instruction(Instruction.Definition.FragmentMaskFetchAMD, resultType, dest, image, coordinate)
  case FragmentFetchAMD(resultType: Operand, dest: Operand, image: Operand, coordinate: Operand, fragmentIndex: Operand)
      extends Instruction(Instruction.Definition.FragmentFetchAMD, resultType, dest, image, coordinate, fragmentIndex)
  case ReadClockKHR(resultType: Operand, dest: Operand, scope: Operand)
      extends Instruction(Instruction.Definition.ReadClockKHR, resultType, dest, scope)
  case AllocateNodePayloadsAMDX(
      resultType: Operand,
      dest: Operand,
      visibility: Operand,
      payloadCount: Operand,
      nodeIndex: Operand
  ) extends Instruction(
        Instruction.Definition.AllocateNodePayloadsAMDX,
        resultType,
        dest,
        visibility,
        payloadCount,
        nodeIndex
      )
  case EnqueueNodePayloadsAMDX(payloadArray: Operand)
      extends Instruction(Instruction.Definition.EnqueueNodePayloadsAMDX, payloadArray)
  case TypeNodePayloadArrayAMDX(dest: Operand, payloadType: Operand)
      extends Instruction(Instruction.Definition.TypeNodePayloadArrayAMDX, dest, payloadType)
  case FinishWritingNodePayloadAMDX(resultType: Operand, dest: Operand, payload: Operand)
      extends Instruction(Instruction.Definition.FinishWritingNodePayloadAMDX, resultType, dest, payload)
  case NodePayloadArrayLengthAMDX(resultType: Operand, dest: Operand, payloadArray: Operand)
      extends Instruction(Instruction.Definition.NodePayloadArrayLengthAMDX, resultType, dest, payloadArray)
  case IsNodePayloadValidAMDX(resultType: Operand, dest: Operand, payloadType: Operand, nodeIndex: Operand)
      extends Instruction(Instruction.Definition.IsNodePayloadValidAMDX, resultType, dest, payloadType, nodeIndex)
  case ConstantStringAMDX(dest: Operand, literalString: Operand)
      extends Instruction(Instruction.Definition.ConstantStringAMDX, dest, literalString)
  case SpecConstantStringAMDX(dest: Operand, literalString: Operand)
      extends Instruction(Instruction.Definition.SpecConstantStringAMDX, dest, literalString)
  case GroupNonUniformQuadAllKHR(resultType: Operand, dest: Operand, pred: Operand)
      extends Instruction(Instruction.Definition.GroupNonUniformQuadAllKHR, resultType, dest, pred)
  case GroupNonUniformQuadAnyKHR(resultType: Operand, dest: Operand, pred: Operand)
      extends Instruction(Instruction.Definition.GroupNonUniformQuadAnyKHR, resultType, dest, pred)
  case TypeBufferEXT(dest: Operand, storageClass: Operand)
      extends Instruction(Instruction.Definition.TypeBufferEXT, dest, storageClass)
  case BufferPointerEXT(resultType: Operand, dest: Operand, buffer: Operand)
      extends Instruction(Instruction.Definition.BufferPointerEXT, resultType, dest, buffer)
  case AbortKHR(messageType: Operand, message: Operand)
      extends Instruction(Instruction.Definition.AbortKHR, messageType, message)
  case UntypedImageTexelPointerEXT(
      resultType: Operand,
      dest: Operand,
      imageType: Operand,
      image: Operand,
      coordinate: Operand,
      sample: Operand
  ) extends Instruction(
        Instruction.Definition.UntypedImageTexelPointerEXT,
        resultType,
        dest,
        imageType,
        image,
        coordinate,
        sample
      )
  case MemberDecorateIdEXT(structureType: Operand, member: Operand, decoration: Operand)
      extends Instruction(Instruction.Definition.MemberDecorateIdEXT, structureType, member, decoration)
  case ConstantSizeOfEXT(resultType: Operand, dest: Operand, dataType: Operand)
      extends Instruction(Instruction.Definition.ConstantSizeOfEXT, resultType, dest, dataType)
  case ConstantDataKHR(resultType: Operand, dest: Operand, data: Vec[Operand])
      extends Instruction(Instruction.Definition.ConstantDataKHR, (Vec(resultType, dest) ++ data)*)
  case SpecConstantDataKHR(resultType: Operand, dest: Operand, data: Vec[Operand])
      extends Instruction(Instruction.Definition.SpecConstantDataKHR, (Vec(resultType, dest) ++ data)*)
  case PoisonKHR(resultType: Operand, dest: Operand)
      extends Instruction(Instruction.Definition.PoisonKHR, resultType, dest)
  case FreezeKHR(resultType: Operand, dest: Operand, value: Operand)
      extends Instruction(Instruction.Definition.FreezeKHR, resultType, dest, value)
  case HitObjectRecordHitMotionNV(
      hitObject: Operand,
      accelerationStructure: Operand,
      instanceId: Operand,
      primitiveId: Operand,
      geometryIndex: Operand,
      hitKind: Operand,
      sBTRecordOffset: Operand,
      sBTRecordStride: Operand,
      origin: Operand,
      tMin: Operand,
      direction: Operand,
      tMax: Operand,
      currentTime: Operand,
      hitObjectAttributes: Operand
  ) extends Instruction(
        Instruction.Definition.HitObjectRecordHitMotionNV,
        hitObject,
        accelerationStructure,
        instanceId,
        primitiveId,
        geometryIndex,
        hitKind,
        sBTRecordOffset,
        sBTRecordStride,
        origin,
        tMin,
        direction,
        tMax,
        currentTime,
        hitObjectAttributes
      )
  case HitObjectRecordHitWithIndexMotionNV(
      hitObject: Operand,
      accelerationStructure: Operand,
      instanceId: Operand,
      primitiveId: Operand,
      geometryIndex: Operand,
      hitKind: Operand,
      sBTRecordIndex: Operand,
      origin: Operand,
      tMin: Operand,
      direction: Operand,
      tMax: Operand,
      currentTime: Operand,
      hitObjectAttributes: Operand
  ) extends Instruction(
        Instruction.Definition.HitObjectRecordHitWithIndexMotionNV,
        hitObject,
        accelerationStructure,
        instanceId,
        primitiveId,
        geometryIndex,
        hitKind,
        sBTRecordIndex,
        origin,
        tMin,
        direction,
        tMax,
        currentTime,
        hitObjectAttributes
      )
  case HitObjectRecordMissMotionNV(
      hitObject: Operand,
      sBTIndex: Operand,
      origin: Operand,
      tMin: Operand,
      direction: Operand,
      tMax: Operand,
      currentTime: Operand
  ) extends Instruction(
        Instruction.Definition.HitObjectRecordMissMotionNV,
        hitObject,
        sBTIndex,
        origin,
        tMin,
        direction,
        tMax,
        currentTime
      )
  case HitObjectGetWorldToObjectNV(resultType: Operand, dest: Operand, hitObject: Operand)
      extends Instruction(Instruction.Definition.HitObjectGetWorldToObjectNV, resultType, dest, hitObject)
  case HitObjectGetObjectToWorldNV(resultType: Operand, dest: Operand, hitObject: Operand)
      extends Instruction(Instruction.Definition.HitObjectGetObjectToWorldNV, resultType, dest, hitObject)
  case HitObjectGetObjectRayDirectionNV(resultType: Operand, dest: Operand, hitObject: Operand)
      extends Instruction(Instruction.Definition.HitObjectGetObjectRayDirectionNV, resultType, dest, hitObject)
  case HitObjectGetObjectRayOriginNV(resultType: Operand, dest: Operand, hitObject: Operand)
      extends Instruction(Instruction.Definition.HitObjectGetObjectRayOriginNV, resultType, dest, hitObject)
  case HitObjectTraceRayMotionNV(
      hitObject: Operand,
      accelerationStructure: Operand,
      rayFlags: Operand,
      cullmask: Operand,
      sBTRecordOffset: Operand,
      sBTRecordStride: Operand,
      missIndex: Operand,
      origin: Operand,
      tMin: Operand,
      direction: Operand,
      tMax: Operand,
      time: Operand,
      payload: Operand
  ) extends Instruction(
        Instruction.Definition.HitObjectTraceRayMotionNV,
        hitObject,
        accelerationStructure,
        rayFlags,
        cullmask,
        sBTRecordOffset,
        sBTRecordStride,
        missIndex,
        origin,
        tMin,
        direction,
        tMax,
        time,
        payload
      )
  case HitObjectGetShaderRecordBufferHandleNV(resultType: Operand, dest: Operand, hitObject: Operand)
      extends Instruction(Instruction.Definition.HitObjectGetShaderRecordBufferHandleNV, resultType, dest, hitObject)
  case HitObjectGetShaderBindingTableRecordIndexNV(
      resultType: Operand,
      dest: Operand,
      hitObject: Operand
  ) extends Instruction(Instruction.Definition.HitObjectGetShaderBindingTableRecordIndexNV, resultType, dest, hitObject)
  case HitObjectRecordEmptyNV(hitObject: Operand)
      extends Instruction(Instruction.Definition.HitObjectRecordEmptyNV, hitObject)
  case HitObjectTraceRayNV(
      hitObject: Operand,
      accelerationStructure: Operand,
      rayFlags: Operand,
      cullmask: Operand,
      sBTRecordOffset: Operand,
      sBTRecordStride: Operand,
      missIndex: Operand,
      origin: Operand,
      tMin: Operand,
      direction: Operand,
      tMax: Operand,
      payload: Operand
  ) extends Instruction(
        Instruction.Definition.HitObjectTraceRayNV,
        hitObject,
        accelerationStructure,
        rayFlags,
        cullmask,
        sBTRecordOffset,
        sBTRecordStride,
        missIndex,
        origin,
        tMin,
        direction,
        tMax,
        payload
      )
  case HitObjectRecordHitNV(
      hitObject: Operand,
      accelerationStructure: Operand,
      instanceId: Operand,
      primitiveId: Operand,
      geometryIndex: Operand,
      hitKind: Operand,
      sBTRecordOffset: Operand,
      sBTRecordStride: Operand,
      origin: Operand,
      tMin: Operand,
      direction: Operand,
      tMax: Operand,
      hitObjectAttributes: Operand
  ) extends Instruction(
        Instruction.Definition.HitObjectRecordHitNV,
        hitObject,
        accelerationStructure,
        instanceId,
        primitiveId,
        geometryIndex,
        hitKind,
        sBTRecordOffset,
        sBTRecordStride,
        origin,
        tMin,
        direction,
        tMax,
        hitObjectAttributes
      )
  case HitObjectRecordHitWithIndexNV(
      hitObject: Operand,
      accelerationStructure: Operand,
      instanceId: Operand,
      primitiveId: Operand,
      geometryIndex: Operand,
      hitKind: Operand,
      sBTRecordIndex: Operand,
      origin: Operand,
      tMin: Operand,
      direction: Operand,
      tMax: Operand,
      hitObjectAttributes: Operand
  ) extends Instruction(
        Instruction.Definition.HitObjectRecordHitWithIndexNV,
        hitObject,
        accelerationStructure,
        instanceId,
        primitiveId,
        geometryIndex,
        hitKind,
        sBTRecordIndex,
        origin,
        tMin,
        direction,
        tMax,
        hitObjectAttributes
      )
  case HitObjectRecordMissNV(
      hitObject: Operand,
      sBTIndex: Operand,
      origin: Operand,
      tMin: Operand,
      direction: Operand,
      tMax: Operand
  ) extends Instruction(
        Instruction.Definition.HitObjectRecordMissNV,
        hitObject,
        sBTIndex,
        origin,
        tMin,
        direction,
        tMax
      )
  case HitObjectExecuteShaderNV(hitObject: Operand, payload: Operand)
      extends Instruction(Instruction.Definition.HitObjectExecuteShaderNV, hitObject, payload)
  case HitObjectGetCurrentTimeNV(resultType: Operand, dest: Operand, hitObject: Operand)
      extends Instruction(Instruction.Definition.HitObjectGetCurrentTimeNV, resultType, dest, hitObject)
  case HitObjectGetAttributesNV(hitObject: Operand, hitObjectAttribute: Operand)
      extends Instruction(Instruction.Definition.HitObjectGetAttributesNV, hitObject, hitObjectAttribute)
  case HitObjectGetHitKindNV(resultType: Operand, dest: Operand, hitObject: Operand)
      extends Instruction(Instruction.Definition.HitObjectGetHitKindNV, resultType, dest, hitObject)
  case HitObjectGetPrimitiveIndexNV(resultType: Operand, dest: Operand, hitObject: Operand)
      extends Instruction(Instruction.Definition.HitObjectGetPrimitiveIndexNV, resultType, dest, hitObject)
  case HitObjectGetGeometryIndexNV(resultType: Operand, dest: Operand, hitObject: Operand)
      extends Instruction(Instruction.Definition.HitObjectGetGeometryIndexNV, resultType, dest, hitObject)
  case HitObjectGetInstanceIdNV(resultType: Operand, dest: Operand, hitObject: Operand)
      extends Instruction(Instruction.Definition.HitObjectGetInstanceIdNV, resultType, dest, hitObject)
  case HitObjectGetInstanceCustomIndexNV(resultType: Operand, dest: Operand, hitObject: Operand)
      extends Instruction(Instruction.Definition.HitObjectGetInstanceCustomIndexNV, resultType, dest, hitObject)
  case HitObjectGetWorldRayDirectionNV(resultType: Operand, dest: Operand, hitObject: Operand)
      extends Instruction(Instruction.Definition.HitObjectGetWorldRayDirectionNV, resultType, dest, hitObject)
  case HitObjectGetWorldRayOriginNV(resultType: Operand, dest: Operand, hitObject: Operand)
      extends Instruction(Instruction.Definition.HitObjectGetWorldRayOriginNV, resultType, dest, hitObject)
  case HitObjectGetRayTMaxNV(resultType: Operand, dest: Operand, hitObject: Operand)
      extends Instruction(Instruction.Definition.HitObjectGetRayTMaxNV, resultType, dest, hitObject)
  case HitObjectGetRayTMinNV(resultType: Operand, dest: Operand, hitObject: Operand)
      extends Instruction(Instruction.Definition.HitObjectGetRayTMinNV, resultType, dest, hitObject)
  case HitObjectIsEmptyNV(resultType: Operand, dest: Operand, hitObject: Operand)
      extends Instruction(Instruction.Definition.HitObjectIsEmptyNV, resultType, dest, hitObject)
  case HitObjectIsHitNV(resultType: Operand, dest: Operand, hitObject: Operand)
      extends Instruction(Instruction.Definition.HitObjectIsHitNV, resultType, dest, hitObject)
  case HitObjectIsMissNV(resultType: Operand, dest: Operand, hitObject: Operand)
      extends Instruction(Instruction.Definition.HitObjectIsMissNV, resultType, dest, hitObject)
  case ReorderThreadWithHitObjectNV(hitObject: Operand, hint: Option[Operand], bits: Option[Operand])
      extends Instruction(
        Instruction.Definition.ReorderThreadWithHitObjectNV,
        (Vec(hitObject) ++ hint.toVec ++ bits.toVec)*
      )
  case ReorderThreadWithHintNV(hint: Operand, bits: Operand)
      extends Instruction(Instruction.Definition.ReorderThreadWithHintNV, hint, bits)
  case TypeHitObjectNV(dest: Operand) extends Instruction(Instruction.Definition.TypeHitObjectNV, dest)
  case ImageSampleFootprintNV(
      resultType: Operand,
      dest: Operand,
      sampledImage: Operand,
      coordinate: Operand,
      granularity: Operand,
      coarse: Operand,
      mods: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.ImageSampleFootprintNV,
        (Vec(resultType, dest, sampledImage, coordinate, granularity, coarse) ++ mods.toVec)*
      )
  case TypeVectorIdEXT(dest: Operand, componentType: Operand, componentCount: Operand)
      extends Instruction(Instruction.Definition.TypeVectorIdEXT, dest, componentType, componentCount)
  case CooperativeVectorMatrixMulNV(
      resultType: Operand,
      dest: Operand,
      input: Operand,
      inputInterpretation: Operand,
      matrix: Operand,
      matrixOffset: Operand,
      matrixInterpretation: Operand,
      m: Operand,
      k: Operand,
      memoryLayout: Operand,
      transpose: Operand,
      matrixStride: Option[Operand],
      cooperativeMatrixOperands: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.CooperativeVectorMatrixMulNV,
        (Vec(
          resultType,
          dest,
          input,
          inputInterpretation,
          matrix,
          matrixOffset,
          matrixInterpretation,
          m,
          k,
          memoryLayout,
          transpose
        ) ++ matrixStride.toVec ++ cooperativeMatrixOperands.toVec)*
      )
  case CooperativeVectorOuterProductAccumulateNV(
      pointer: Operand,
      offset: Operand,
      a: Operand,
      b: Operand,
      memoryLayout: Operand,
      matrixInterpretation: Operand,
      matrixStride: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.CooperativeVectorOuterProductAccumulateNV,
        (Vec(pointer, offset, a, b, memoryLayout, matrixInterpretation) ++ matrixStride.toVec)*
      )
  case CooperativeVectorReduceSumAccumulateNV(pointer: Operand, offset: Operand, v: Operand)
      extends Instruction(Instruction.Definition.CooperativeVectorReduceSumAccumulateNV, pointer, offset, v)
  case CooperativeVectorMatrixMulAddNV(
      resultType: Operand,
      dest: Operand,
      input: Operand,
      inputInterpretation: Operand,
      matrix: Operand,
      matrixOffset: Operand,
      matrixInterpretation: Operand,
      bias: Operand,
      biasOffset: Operand,
      biasInterpretation: Operand,
      m: Operand,
      k: Operand,
      memoryLayout: Operand,
      transpose: Operand,
      matrixStride: Option[Operand],
      cooperativeMatrixOperands: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.CooperativeVectorMatrixMulAddNV,
        (Vec(
          resultType,
          dest,
          input,
          inputInterpretation,
          matrix,
          matrixOffset,
          matrixInterpretation,
          bias,
          biasOffset,
          biasInterpretation,
          m,
          k,
          memoryLayout,
          transpose
        ) ++ matrixStride.toVec ++ cooperativeMatrixOperands.toVec)*
      )
  case CooperativeMatrixConvertNV(resultType: Operand, dest: Operand, matrix: Operand)
      extends Instruction(Instruction.Definition.CooperativeMatrixConvertNV, resultType, dest, matrix)
  case EmitMeshTasksEXT(groupCountX: Operand, groupCountY: Operand, groupCountZ: Operand, payload: Option[Operand])
      extends Instruction(
        Instruction.Definition.EmitMeshTasksEXT,
        (Vec(groupCountX, groupCountY, groupCountZ) ++ payload.toVec)*
      )
  case SetMeshOutputsEXT(vertexCount: Operand, primitiveCount: Operand)
      extends Instruction(Instruction.Definition.SetMeshOutputsEXT, vertexCount, primitiveCount)
  case GroupNonUniformPartitionEXT(resultType: Operand, dest: Operand, value: Operand)
      extends Instruction(Instruction.Definition.GroupNonUniformPartitionEXT, resultType, dest, value)
  case WritePackedPrimitiveIndices4x8NV(indexOffset: Operand, packedIndices: Operand)
      extends Instruction(Instruction.Definition.WritePackedPrimitiveIndices4x8NV, indexOffset, packedIndices)
  case FetchMicroTriangleVertexPositionNV(
      resultType: Operand,
      dest: Operand,
      accel: Operand,
      instanceId: Operand,
      geometryIndex: Operand,
      primitiveIndex: Operand,
      barycentric: Operand
  ) extends Instruction(
        Instruction.Definition.FetchMicroTriangleVertexPositionNV,
        resultType,
        dest,
        accel,
        instanceId,
        geometryIndex,
        primitiveIndex,
        barycentric
      )
  case FetchMicroTriangleVertexBarycentricNV(
      resultType: Operand,
      dest: Operand,
      accel: Operand,
      instanceId: Operand,
      geometryIndex: Operand,
      primitiveIndex: Operand,
      barycentric: Operand
  ) extends Instruction(
        Instruction.Definition.FetchMicroTriangleVertexBarycentricNV,
        resultType,
        dest,
        accel,
        instanceId,
        geometryIndex,
        primitiveIndex,
        barycentric
      )
  case CooperativeVectorLoadNV(
      resultType: Operand,
      dest: Operand,
      pointer: Operand,
      offset: Operand,
      mods: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.CooperativeVectorLoadNV,
        (Vec(resultType, dest, pointer, offset) ++ mods.toVec)*
      )
  case CooperativeVectorStoreNV(pointer: Operand, offset: Operand, src: Operand, mods: Option[Operand])
      extends Instruction(Instruction.Definition.CooperativeVectorStoreNV, (Vec(pointer, offset, src) ++ mods.toVec)*)
  case HitObjectRecordFromQueryEXT(
      hitObject: Operand,
      rayQuery: Operand,
      sBTRecordIndex: Operand,
      hitObjectAttributes: Operand,
      hitKind: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.HitObjectRecordFromQueryEXT,
        (Vec(hitObject, rayQuery, sBTRecordIndex, hitObjectAttributes) ++ hitKind.toVec)*
      )
  case HitObjectRecordMissEXT(
      hitObject: Operand,
      rayFlags: Operand,
      missIndex: Operand,
      rayOrigin: Operand,
      rayTmin: Operand,
      rayDirection: Operand,
      rayTmax: Operand
  ) extends Instruction(
        Instruction.Definition.HitObjectRecordMissEXT,
        hitObject,
        rayFlags,
        missIndex,
        rayOrigin,
        rayTmin,
        rayDirection,
        rayTmax
      )
  case HitObjectRecordMissMotionEXT(
      hitObject: Operand,
      rayFlags: Operand,
      missIndex: Operand,
      rayOrigin: Operand,
      rayTmin: Operand,
      rayDirection: Operand,
      rayTmax: Operand,
      currentTime: Operand
  ) extends Instruction(
        Instruction.Definition.HitObjectRecordMissMotionEXT,
        hitObject,
        rayFlags,
        missIndex,
        rayOrigin,
        rayTmin,
        rayDirection,
        rayTmax,
        currentTime
      )
  case HitObjectGetIntersectionTriangleVertexPositionsEXT(resultType: Operand, dest: Operand, hitObject: Operand)
      extends Instruction(
        Instruction.Definition.HitObjectGetIntersectionTriangleVertexPositionsEXT,
        resultType,
        dest,
        hitObject
      )
  case HitObjectGetRayFlagsEXT(resultType: Operand, dest: Operand, hitObject: Operand)
      extends Instruction(Instruction.Definition.HitObjectGetRayFlagsEXT, resultType, dest, hitObject)
  case HitObjectSetShaderBindingTableRecordIndexEXT(
      hitObject: Operand,
      sBTRecordIndex: Operand
  ) extends Instruction(Instruction.Definition.HitObjectSetShaderBindingTableRecordIndexEXT, hitObject, sBTRecordIndex)
  case HitObjectReorderExecuteShaderEXT(
      hitObject: Operand,
      payload: Operand,
      hint: Option[Operand],
      bits: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.HitObjectReorderExecuteShaderEXT,
        (Vec(hitObject, payload) ++ hint.toVec ++ bits.toVec)*
      )
  case HitObjectTraceReorderExecuteEXT(
      hitObject: Operand,
      accelerationStructure: Operand,
      rayFlags: Operand,
      cullMask: Operand,
      sBTOffset: Operand,
      sBTStride: Operand,
      missIndex: Operand,
      rayOrigin: Operand,
      rayTmin: Operand,
      rayDirection: Operand,
      rayTmax: Operand,
      payload: Operand,
      hint: Option[Operand],
      bits: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.HitObjectTraceReorderExecuteEXT,
        (Vec(
          hitObject,
          accelerationStructure,
          rayFlags,
          cullMask,
          sBTOffset,
          sBTStride,
          missIndex,
          rayOrigin,
          rayTmin,
          rayDirection,
          rayTmax,
          payload
        ) ++ hint.toVec ++ bits.toVec)*
      )
  case HitObjectTraceMotionReorderExecuteEXT(
      hitObject: Operand,
      accelerationStructure: Operand,
      rayFlags: Operand,
      cullMask: Operand,
      sBTOffset: Operand,
      sBTStride: Operand,
      missIndex: Operand,
      rayOrigin: Operand,
      rayTmin: Operand,
      rayDirection: Operand,
      rayTmax: Operand,
      currentTime: Operand,
      payload: Operand,
      hint: Option[Operand],
      bits: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.HitObjectTraceMotionReorderExecuteEXT,
        (Vec(
          hitObject,
          accelerationStructure,
          rayFlags,
          cullMask,
          sBTOffset,
          sBTStride,
          missIndex,
          rayOrigin,
          rayTmin,
          rayDirection,
          rayTmax,
          currentTime,
          payload
        ) ++ hint.toVec ++ bits.toVec)*
      )
  case TypeHitObjectEXT(dest: Operand) extends Instruction(Instruction.Definition.TypeHitObjectEXT, dest)
  case ReorderThreadWithHintEXT(hint: Operand, bits: Operand)
      extends Instruction(Instruction.Definition.ReorderThreadWithHintEXT, hint, bits)
  case ReorderThreadWithHitObjectEXT(hitObject: Operand, hint: Option[Operand], bits: Option[Operand])
      extends Instruction(
        Instruction.Definition.ReorderThreadWithHitObjectEXT,
        (Vec(hitObject) ++ hint.toVec ++ bits.toVec)*
      )
  case HitObjectTraceRayEXT(
      hitObject: Operand,
      accelerationStructure: Operand,
      rayFlags: Operand,
      cullMask: Operand,
      sBTOffset: Operand,
      sBTStride: Operand,
      missIndex: Operand,
      rayOrigin: Operand,
      rayTmin: Operand,
      rayDirection: Operand,
      rayTmax: Operand,
      payload: Operand
  ) extends Instruction(
        Instruction.Definition.HitObjectTraceRayEXT,
        hitObject,
        accelerationStructure,
        rayFlags,
        cullMask,
        sBTOffset,
        sBTStride,
        missIndex,
        rayOrigin,
        rayTmin,
        rayDirection,
        rayTmax,
        payload
      )
  case HitObjectTraceRayMotionEXT(
      hitObject: Operand,
      accelerationStructure: Operand,
      rayFlags: Operand,
      cullMask: Operand,
      sBTOffset: Operand,
      sBTStride: Operand,
      missIndex: Operand,
      rayOrigin: Operand,
      rayTmin: Operand,
      rayDirection: Operand,
      rayTmax: Operand,
      currentTime: Operand,
      payload: Operand
  ) extends Instruction(
        Instruction.Definition.HitObjectTraceRayMotionEXT,
        hitObject,
        accelerationStructure,
        rayFlags,
        cullMask,
        sBTOffset,
        sBTStride,
        missIndex,
        rayOrigin,
        rayTmin,
        rayDirection,
        rayTmax,
        currentTime,
        payload
      )
  case HitObjectRecordEmptyEXT(hitObject: Operand)
      extends Instruction(Instruction.Definition.HitObjectRecordEmptyEXT, hitObject)
  case HitObjectExecuteShaderEXT(hitObject: Operand, payload: Operand)
      extends Instruction(Instruction.Definition.HitObjectExecuteShaderEXT, hitObject, payload)
  case HitObjectGetCurrentTimeEXT(resultType: Operand, dest: Operand, hitObject: Operand)
      extends Instruction(Instruction.Definition.HitObjectGetCurrentTimeEXT, resultType, dest, hitObject)
  case HitObjectGetAttributesEXT(hitObject: Operand, hitObjectAttribute: Operand)
      extends Instruction(Instruction.Definition.HitObjectGetAttributesEXT, hitObject, hitObjectAttribute)
  case HitObjectGetHitKindEXT(resultType: Operand, dest: Operand, hitObject: Operand)
      extends Instruction(Instruction.Definition.HitObjectGetHitKindEXT, resultType, dest, hitObject)
  case HitObjectGetPrimitiveIndexEXT(resultType: Operand, dest: Operand, hitObject: Operand)
      extends Instruction(Instruction.Definition.HitObjectGetPrimitiveIndexEXT, resultType, dest, hitObject)
  case HitObjectGetGeometryIndexEXT(resultType: Operand, dest: Operand, hitObject: Operand)
      extends Instruction(Instruction.Definition.HitObjectGetGeometryIndexEXT, resultType, dest, hitObject)
  case HitObjectGetInstanceIdEXT(resultType: Operand, dest: Operand, hitObject: Operand)
      extends Instruction(Instruction.Definition.HitObjectGetInstanceIdEXT, resultType, dest, hitObject)
  case HitObjectGetInstanceCustomIndexEXT(resultType: Operand, dest: Operand, hitObject: Operand)
      extends Instruction(Instruction.Definition.HitObjectGetInstanceCustomIndexEXT, resultType, dest, hitObject)
  case HitObjectGetObjectRayOriginEXT(resultType: Operand, dest: Operand, hitObject: Operand)
      extends Instruction(Instruction.Definition.HitObjectGetObjectRayOriginEXT, resultType, dest, hitObject)
  case HitObjectGetObjectRayDirectionEXT(resultType: Operand, dest: Operand, hitObject: Operand)
      extends Instruction(Instruction.Definition.HitObjectGetObjectRayDirectionEXT, resultType, dest, hitObject)
  case HitObjectGetWorldRayDirectionEXT(resultType: Operand, dest: Operand, hitObject: Operand)
      extends Instruction(Instruction.Definition.HitObjectGetWorldRayDirectionEXT, resultType, dest, hitObject)
  case HitObjectGetWorldRayOriginEXT(resultType: Operand, dest: Operand, hitObject: Operand)
      extends Instruction(Instruction.Definition.HitObjectGetWorldRayOriginEXT, resultType, dest, hitObject)
  case HitObjectGetObjectToWorldEXT(resultType: Operand, dest: Operand, hitObject: Operand)
      extends Instruction(Instruction.Definition.HitObjectGetObjectToWorldEXT, resultType, dest, hitObject)
  case HitObjectGetWorldToObjectEXT(resultType: Operand, dest: Operand, hitObject: Operand)
      extends Instruction(Instruction.Definition.HitObjectGetWorldToObjectEXT, resultType, dest, hitObject)
  case HitObjectGetRayTMaxEXT(resultType: Operand, dest: Operand, hitObject: Operand)
      extends Instruction(Instruction.Definition.HitObjectGetRayTMaxEXT, resultType, dest, hitObject)
  case ReportIntersectionKHR(resultType: Operand, dest: Operand, hit: Operand, hitKind: Operand)
      extends Instruction(Instruction.Definition.ReportIntersectionKHR, resultType, dest, hit, hitKind)
  case IgnoreIntersectionNV() extends Instruction(Instruction.Definition.IgnoreIntersectionNV)
  case TerminateRayNV() extends Instruction(Instruction.Definition.TerminateRayNV)
  case TraceNV(
      accel: Operand,
      rayFlags: Operand,
      cullMask: Operand,
      sBTOffset: Operand,
      sBTStride: Operand,
      missIndex: Operand,
      rayOrigin: Operand,
      rayTmin: Operand,
      rayDirection: Operand,
      rayTmax: Operand,
      payloadId: Operand
  ) extends Instruction(
        Instruction.Definition.TraceNV,
        accel,
        rayFlags,
        cullMask,
        sBTOffset,
        sBTStride,
        missIndex,
        rayOrigin,
        rayTmin,
        rayDirection,
        rayTmax,
        payloadId
      )
  case TraceMotionNV(
      accel: Operand,
      rayFlags: Operand,
      cullMask: Operand,
      sBTOffset: Operand,
      sBTStride: Operand,
      missIndex: Operand,
      rayOrigin: Operand,
      rayTmin: Operand,
      rayDirection: Operand,
      rayTmax: Operand,
      time: Operand,
      payloadId: Operand
  ) extends Instruction(
        Instruction.Definition.TraceMotionNV,
        accel,
        rayFlags,
        cullMask,
        sBTOffset,
        sBTStride,
        missIndex,
        rayOrigin,
        rayTmin,
        rayDirection,
        rayTmax,
        time,
        payloadId
      )
  case TraceRayMotionNV(
      accel: Operand,
      rayFlags: Operand,
      cullMask: Operand,
      sBTOffset: Operand,
      sBTStride: Operand,
      missIndex: Operand,
      rayOrigin: Operand,
      rayTmin: Operand,
      rayDirection: Operand,
      rayTmax: Operand,
      time: Operand,
      payload: Operand
  ) extends Instruction(
        Instruction.Definition.TraceRayMotionNV,
        accel,
        rayFlags,
        cullMask,
        sBTOffset,
        sBTStride,
        missIndex,
        rayOrigin,
        rayTmin,
        rayDirection,
        rayTmax,
        time,
        payload
      )
  case RayQueryGetIntersectionTriangleVertexPositionsKHR(
      resultType: Operand,
      dest: Operand,
      rayQuery: Operand,
      intersection: Operand
  ) extends Instruction(
        Instruction.Definition.RayQueryGetIntersectionTriangleVertexPositionsKHR,
        resultType,
        dest,
        rayQuery,
        intersection
      )
  case TypeAccelerationStructureKHR(dest: Operand)
      extends Instruction(Instruction.Definition.TypeAccelerationStructureKHR, dest)
  case ExecuteCallableNV(sBTIndex: Operand, callableDataId: Operand)
      extends Instruction(Instruction.Definition.ExecuteCallableNV, sBTIndex, callableDataId)
  case RayQueryGetIntersectionClusterIdNV(resultType: Operand, dest: Operand, rayQuery: Operand, intersection: Operand)
      extends Instruction(
        Instruction.Definition.RayQueryGetIntersectionClusterIdNV,
        resultType,
        dest,
        rayQuery,
        intersection
      )
  case HitObjectGetClusterIdNV(resultType: Operand, dest: Operand, hitObject: Operand)
      extends Instruction(Instruction.Definition.HitObjectGetClusterIdNV, resultType, dest, hitObject)
  case HitObjectGetRayTMinEXT(resultType: Operand, dest: Operand, hitObject: Operand)
      extends Instruction(Instruction.Definition.HitObjectGetRayTMinEXT, resultType, dest, hitObject)
  case HitObjectGetShaderBindingTableRecordIndexEXT(resultType: Operand, dest: Operand, hitObject: Operand)
      extends Instruction(
        Instruction.Definition.HitObjectGetShaderBindingTableRecordIndexEXT,
        resultType,
        dest,
        hitObject
      )
  case HitObjectGetShaderRecordBufferHandleEXT(resultType: Operand, dest: Operand, hitObject: Operand)
      extends Instruction(Instruction.Definition.HitObjectGetShaderRecordBufferHandleEXT, resultType, dest, hitObject)
  case HitObjectIsEmptyEXT(resultType: Operand, dest: Operand, hitObject: Operand)
      extends Instruction(Instruction.Definition.HitObjectIsEmptyEXT, resultType, dest, hitObject)
  case HitObjectIsHitEXT(resultType: Operand, dest: Operand, hitObject: Operand)
      extends Instruction(Instruction.Definition.HitObjectIsHitEXT, resultType, dest, hitObject)
  case HitObjectIsMissEXT(resultType: Operand, dest: Operand, hitObject: Operand)
      extends Instruction(Instruction.Definition.HitObjectIsMissEXT, resultType, dest, hitObject)
  case TypeCooperativeMatrixNV(
      dest: Operand,
      componentType: Operand,
      execution: Operand,
      rows: Operand,
      columns: Operand
  ) extends Instruction(Instruction.Definition.TypeCooperativeMatrixNV, dest, componentType, execution, rows, columns)
  case CooperativeMatrixLoadNV(
      resultType: Operand,
      dest: Operand,
      pointer: Operand,
      stride: Operand,
      columnMajor: Operand,
      mods: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.CooperativeMatrixLoadNV,
        (Vec(resultType, dest, pointer, stride, columnMajor) ++ mods.toVec)*
      )
  case CooperativeMatrixStoreNV(
      pointer: Operand,
      src: Operand,
      stride: Operand,
      columnMajor: Operand,
      mods: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.CooperativeMatrixStoreNV,
        (Vec(pointer, src, stride, columnMajor) ++ mods.toVec)*
      )
  case CooperativeMatrixMulAddNV(resultType: Operand, dest: Operand, a: Operand, b: Operand, c: Operand)
      extends Instruction(Instruction.Definition.CooperativeMatrixMulAddNV, resultType, dest, a, b, c)
  case CooperativeMatrixLengthNV(resultType: Operand, dest: Operand, dataType: Operand)
      extends Instruction(Instruction.Definition.CooperativeMatrixLengthNV, resultType, dest, dataType)
  case BeginInvocationInterlockEXT() extends Instruction(Instruction.Definition.BeginInvocationInterlockEXT)
  case EndInvocationInterlockEXT() extends Instruction(Instruction.Definition.EndInvocationInterlockEXT)
  case CooperativeMatrixReduceNV(
      resultType: Operand,
      dest: Operand,
      matrix: Operand,
      reduce: Operand,
      combineFunc: Operand
  ) extends Instruction(Instruction.Definition.CooperativeMatrixReduceNV, resultType, dest, matrix, reduce, combineFunc)
  case CooperativeMatrixLoadTensorNV(
      resultType: Operand,
      dest: Operand,
      pointer: Operand,
      src: Operand,
      tensorLayout: Operand,
      mods: Operand,
      tensorAddressingOperands: Operand
  ) extends Instruction(
        Instruction.Definition.CooperativeMatrixLoadTensorNV,
        resultType,
        dest,
        pointer,
        src,
        tensorLayout,
        mods,
        tensorAddressingOperands
      )
  case CooperativeMatrixStoreTensorNV(
      pointer: Operand,
      src: Operand,
      tensorLayout: Operand,
      mods: Operand,
      tensorAddressingOperands: Operand
  ) extends Instruction(
        Instruction.Definition.CooperativeMatrixStoreTensorNV,
        pointer,
        src,
        tensorLayout,
        mods,
        tensorAddressingOperands
      )
  case CooperativeMatrixPerElementOpNV(
      resultType: Operand,
      dest: Operand,
      matrix: Operand,
      func: Operand,
      srcs: Vec[Operand]
  ) extends Instruction(
        Instruction.Definition.CooperativeMatrixPerElementOpNV,
        (Vec(resultType, dest, matrix, func) ++ srcs)*
      )
  case TypeTensorLayoutNV(dest: Operand, dim: Operand, clampMode: Operand)
      extends Instruction(Instruction.Definition.TypeTensorLayoutNV, dest, dim, clampMode)
  case TypeTensorViewNV(dest: Operand, dim: Operand, hasDimensions: Operand, p: Vec[Operand])
      extends Instruction(Instruction.Definition.TypeTensorViewNV, (Vec(dest, dim, hasDimensions) ++ p)*)
  case CreateTensorLayoutNV(resultType: Operand, dest: Operand)
      extends Instruction(Instruction.Definition.CreateTensorLayoutNV, resultType, dest)
  case TensorLayoutSetDimensionNV(resultType: Operand, dest: Operand, tensorLayout: Operand, dim: Vec[Operand])
      extends Instruction(
        Instruction.Definition.TensorLayoutSetDimensionNV,
        (Vec(resultType, dest, tensorLayout) ++ dim)*
      )
  case TensorLayoutSetStrideNV(resultType: Operand, dest: Operand, tensorLayout: Operand, stride: Vec[Operand])
      extends Instruction(
        Instruction.Definition.TensorLayoutSetStrideNV,
        (Vec(resultType, dest, tensorLayout) ++ stride)*
      )
  case TensorLayoutSliceNV(resultType: Operand, dest: Operand, tensorLayout: Operand, srcs: Vec[Operand])
      extends Instruction(Instruction.Definition.TensorLayoutSliceNV, (Vec(resultType, dest, tensorLayout) ++ srcs)*)
  case TensorLayoutSetClampValueNV(resultType: Operand, dest: Operand, tensorLayout: Operand, value: Operand)
      extends Instruction(Instruction.Definition.TensorLayoutSetClampValueNV, resultType, dest, tensorLayout, value)
  case CreateTensorViewNV(resultType: Operand, dest: Operand)
      extends Instruction(Instruction.Definition.CreateTensorViewNV, resultType, dest)
  case TensorViewSetDimensionNV(resultType: Operand, dest: Operand, tensorView: Operand, dim: Vec[Operand])
      extends Instruction(Instruction.Definition.TensorViewSetDimensionNV, (Vec(resultType, dest, tensorView) ++ dim)*)
  case TensorViewSetStrideNV(resultType: Operand, dest: Operand, tensorView: Operand, stride: Vec[Operand])
      extends Instruction(Instruction.Definition.TensorViewSetStrideNV, (Vec(resultType, dest, tensorView) ++ stride)*)
  case DemoteToHelperInvocation() extends Instruction(Instruction.Definition.DemoteToHelperInvocation)
  case IsHelperInvocationEXT(resultType: Operand, dest: Operand)
      extends Instruction(Instruction.Definition.IsHelperInvocationEXT, resultType, dest)
  case TensorViewSetClipNV(
      resultType: Operand,
      dest: Operand,
      tensorView: Operand,
      clipRowOffset: Operand,
      clipRowSpan: Operand,
      clipColOffset: Operand,
      clipColSpan: Operand
  ) extends Instruction(
        Instruction.Definition.TensorViewSetClipNV,
        resultType,
        dest,
        tensorView,
        clipRowOffset,
        clipRowSpan,
        clipColOffset,
        clipColSpan
      )
  case TensorLayoutSetBlockSizeNV(resultType: Operand, dest: Operand, tensorLayout: Operand, blockSize: Vec[Operand])
      extends Instruction(
        Instruction.Definition.TensorLayoutSetBlockSizeNV,
        (Vec(resultType, dest, tensorLayout) ++ blockSize)*
      )
  case CooperativeMatrixTransposeNV(resultType: Operand, dest: Operand, matrix: Operand)
      extends Instruction(Instruction.Definition.CooperativeMatrixTransposeNV, resultType, dest, matrix)
  case ConvertUToImageNV(resultType: Operand, dest: Operand, src: Operand)
      extends Instruction(Instruction.Definition.ConvertUToImageNV, resultType, dest, src)
  case ConvertUToSamplerNV(resultType: Operand, dest: Operand, src: Operand)
      extends Instruction(Instruction.Definition.ConvertUToSamplerNV, resultType, dest, src)
  case ConvertImageToUNV(resultType: Operand, dest: Operand, src: Operand)
      extends Instruction(Instruction.Definition.ConvertImageToUNV, resultType, dest, src)
  case ConvertSamplerToUNV(resultType: Operand, dest: Operand, src: Operand)
      extends Instruction(Instruction.Definition.ConvertSamplerToUNV, resultType, dest, src)
  case ConvertUToSampledImageNV(resultType: Operand, dest: Operand, src: Operand)
      extends Instruction(Instruction.Definition.ConvertUToSampledImageNV, resultType, dest, src)
  case ConvertSampledImageToUNV(resultType: Operand, dest: Operand, src: Operand)
      extends Instruction(Instruction.Definition.ConvertSampledImageToUNV, resultType, dest, src)
  case SamplerImageAddressingModeNV(bitWidth: Operand)
      extends Instruction(Instruction.Definition.SamplerImageAddressingModeNV, bitWidth)
  case RawAccessChainNV(
      resultType: Operand,
      dest: Operand,
      base: Operand,
      byteStride: Operand,
      elementIndex: Operand,
      byteOffset: Operand,
      rawAccessChainOperands: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.RawAccessChainNV,
        (Vec(resultType, dest, base, byteStride, elementIndex, byteOffset) ++ rawAccessChainOperands.toVec)*
      )
  case RayQueryGetIntersectionSpherePositionNV(
      resultType: Operand,
      dest: Operand,
      rayQuery: Operand,
      intersection: Operand
  ) extends Instruction(
        Instruction.Definition.RayQueryGetIntersectionSpherePositionNV,
        resultType,
        dest,
        rayQuery,
        intersection
      )
  case RayQueryGetIntersectionSphereRadiusNV(
      resultType: Operand,
      dest: Operand,
      rayQuery: Operand,
      intersection: Operand
  ) extends Instruction(
        Instruction.Definition.RayQueryGetIntersectionSphereRadiusNV,
        resultType,
        dest,
        rayQuery,
        intersection
      )
  case RayQueryGetIntersectionLSSPositionsNV(
      resultType: Operand,
      dest: Operand,
      rayQuery: Operand,
      intersection: Operand
  ) extends Instruction(
        Instruction.Definition.RayQueryGetIntersectionLSSPositionsNV,
        resultType,
        dest,
        rayQuery,
        intersection
      )
  case RayQueryGetIntersectionLSSRadiiNV(resultType: Operand, dest: Operand, rayQuery: Operand, intersection: Operand)
      extends Instruction(
        Instruction.Definition.RayQueryGetIntersectionLSSRadiiNV,
        resultType,
        dest,
        rayQuery,
        intersection
      )
  case RayQueryGetIntersectionLSSHitValueNV(
      resultType: Operand,
      dest: Operand,
      rayQuery: Operand,
      intersection: Operand
  ) extends Instruction(
        Instruction.Definition.RayQueryGetIntersectionLSSHitValueNV,
        resultType,
        dest,
        rayQuery,
        intersection
      )
  case HitObjectGetSpherePositionNV(resultType: Operand, dest: Operand, hitObject: Operand)
      extends Instruction(Instruction.Definition.HitObjectGetSpherePositionNV, resultType, dest, hitObject)
  case HitObjectGetSphereRadiusNV(resultType: Operand, dest: Operand, hitObject: Operand)
      extends Instruction(Instruction.Definition.HitObjectGetSphereRadiusNV, resultType, dest, hitObject)
  case HitObjectGetLSSPositionsNV(resultType: Operand, dest: Operand, hitObject: Operand)
      extends Instruction(Instruction.Definition.HitObjectGetLSSPositionsNV, resultType, dest, hitObject)
  case HitObjectGetLSSRadiiNV(resultType: Operand, dest: Operand, hitObject: Operand)
      extends Instruction(Instruction.Definition.HitObjectGetLSSRadiiNV, resultType, dest, hitObject)
  case HitObjectIsSphereHitNV(resultType: Operand, dest: Operand, hitObject: Operand)
      extends Instruction(Instruction.Definition.HitObjectIsSphereHitNV, resultType, dest, hitObject)
  case HitObjectIsLSSHitNV(resultType: Operand, dest: Operand, hitObject: Operand)
      extends Instruction(Instruction.Definition.HitObjectIsLSSHitNV, resultType, dest, hitObject)
  case RayQueryIsSphereHitNV(resultType: Operand, dest: Operand, rayQuery: Operand, intersection: Operand)
      extends Instruction(Instruction.Definition.RayQueryIsSphereHitNV, resultType, dest, rayQuery, intersection)
  case RayQueryIsLSSHitNV(resultType: Operand, dest: Operand, rayQuery: Operand, intersection: Operand)
      extends Instruction(Instruction.Definition.RayQueryIsLSSHitNV, resultType, dest, rayQuery, intersection)
  case SubgroupShuffleINTEL(resultType: Operand, dest: Operand, data: Operand, invocationId: Operand)
      extends Instruction(Instruction.Definition.SubgroupShuffleINTEL, resultType, dest, data, invocationId)
  case SubgroupShuffleDownINTEL(resultType: Operand, dest: Operand, current: Operand, next: Operand, delta: Operand)
      extends Instruction(Instruction.Definition.SubgroupShuffleDownINTEL, resultType, dest, current, next, delta)
  case SubgroupShuffleUpINTEL(resultType: Operand, dest: Operand, previous: Operand, current: Operand, delta: Operand)
      extends Instruction(Instruction.Definition.SubgroupShuffleUpINTEL, resultType, dest, previous, current, delta)
  case SubgroupShuffleXorINTEL(resultType: Operand, dest: Operand, data: Operand, value: Operand)
      extends Instruction(Instruction.Definition.SubgroupShuffleXorINTEL, resultType, dest, data, value)
  case SubgroupBlockReadINTEL(resultType: Operand, dest: Operand, ptr: Operand)
      extends Instruction(Instruction.Definition.SubgroupBlockReadINTEL, resultType, dest, ptr)
  case SubgroupBlockWriteINTEL(ptr: Operand, data: Operand)
      extends Instruction(Instruction.Definition.SubgroupBlockWriteINTEL, ptr, data)
  case SubgroupImageBlockReadINTEL(resultType: Operand, dest: Operand, image: Operand, coordinate: Operand)
      extends Instruction(Instruction.Definition.SubgroupImageBlockReadINTEL, resultType, dest, image, coordinate)
  case SubgroupImageBlockWriteINTEL(image: Operand, coordinate: Operand, data: Operand)
      extends Instruction(Instruction.Definition.SubgroupImageBlockWriteINTEL, image, coordinate, data)
  case SubgroupImageMediaBlockReadINTEL(
      resultType: Operand,
      dest: Operand,
      image: Operand,
      coordinate: Operand,
      width: Operand,
      height: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupImageMediaBlockReadINTEL,
        resultType,
        dest,
        image,
        coordinate,
        width,
        height
      )
  case SubgroupImageMediaBlockWriteINTEL(
      image: Operand,
      coordinate: Operand,
      width: Operand,
      height: Operand,
      data: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupImageMediaBlockWriteINTEL,
        image,
        coordinate,
        width,
        height,
        data
      )
  case UCountLeadingZerosINTEL(resultType: Operand, dest: Operand, src: Operand)
      extends Instruction(Instruction.Definition.UCountLeadingZerosINTEL, resultType, dest, src)
  case UCountTrailingZerosINTEL(resultType: Operand, dest: Operand, src: Operand)
      extends Instruction(Instruction.Definition.UCountTrailingZerosINTEL, resultType, dest, src)
  case AbsISubINTEL(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.AbsISubINTEL, resultType, dest, srcA, srcB)
  case AbsUSubINTEL(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.AbsUSubINTEL, resultType, dest, srcA, srcB)
  case IAddSatINTEL(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.IAddSatINTEL, resultType, dest, srcA, srcB)
  case UAddSatINTEL(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.UAddSatINTEL, resultType, dest, srcA, srcB)
  case IAverageINTEL(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.IAverageINTEL, resultType, dest, srcA, srcB)
  case UAverageINTEL(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.UAverageINTEL, resultType, dest, srcA, srcB)
  case IAverageRoundedINTEL(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.IAverageRoundedINTEL, resultType, dest, srcA, srcB)
  case UAverageRoundedINTEL(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.UAverageRoundedINTEL, resultType, dest, srcA, srcB)
  case ISubSatINTEL(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.ISubSatINTEL, resultType, dest, srcA, srcB)
  case USubSatINTEL(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.USubSatINTEL, resultType, dest, srcA, srcB)
  case IMul32x16INTEL(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.IMul32x16INTEL, resultType, dest, srcA, srcB)
  case UMul32x16INTEL(resultType: Operand, dest: Operand, srcA: Operand, srcB: Operand)
      extends Instruction(Instruction.Definition.UMul32x16INTEL, resultType, dest, srcA, srcB)
  case ConstantFunctionPointerINTEL(resultType: Operand, dest: Operand, function: Operand)
      extends Instruction(Instruction.Definition.ConstantFunctionPointerINTEL, resultType, dest, function)
  case FunctionPointerCallINTEL(resultType: Operand, dest: Operand, srcs: Vec[Operand])
      extends Instruction(Instruction.Definition.FunctionPointerCallINTEL, (Vec(resultType, dest) ++ srcs)*)
  case AsmTargetINTEL(dest: Operand, asmTarget: Operand)
      extends Instruction(Instruction.Definition.AsmTargetINTEL, dest, asmTarget)
  case AsmINTEL(
      resultType: Operand,
      dest: Operand,
      asmType: Operand,
      target: Operand,
      asmInsts: Operand,
      constraints: Operand
  ) extends Instruction(Instruction.Definition.AsmINTEL, resultType, dest, asmType, target, asmInsts, constraints)
  case AsmCallINTEL(resultType: Operand, dest: Operand, asm: Operand, argument: Vec[Operand])
      extends Instruction(Instruction.Definition.AsmCallINTEL, (Vec(resultType, dest, asm) ++ argument)*)
  case AtomicFMinEXT(
      resultType: Operand,
      dest: Operand,
      pointer: Operand,
      memory: Operand,
      semantics: Operand,
      value: Operand
  ) extends Instruction(Instruction.Definition.AtomicFMinEXT, resultType, dest, pointer, memory, semantics, value)
  case AtomicFMaxEXT(
      resultType: Operand,
      dest: Operand,
      pointer: Operand,
      memory: Operand,
      semantics: Operand,
      value: Operand
  ) extends Instruction(Instruction.Definition.AtomicFMaxEXT, resultType, dest, pointer, memory, semantics, value)
  case AssumeTrueKHR(pred: Operand) extends Instruction(Instruction.Definition.AssumeTrueKHR, pred)
  case ExpectKHR(resultType: Operand, dest: Operand, value: Operand, expectedValue: Operand)
      extends Instruction(Instruction.Definition.ExpectKHR, resultType, dest, value, expectedValue)
  case DecorateString(target: Operand, decoration: Operand)
      extends Instruction(Instruction.Definition.DecorateString, target, decoration)
  case MemberDecorateString(structType: Operand, member: Operand, decoration: Operand)
      extends Instruction(Instruction.Definition.MemberDecorateString, structType, member, decoration)
  case VmeImageINTEL(resultType: Operand, dest: Operand, imageType: Operand, sampler: Operand)
      extends Instruction(Instruction.Definition.VmeImageINTEL, resultType, dest, imageType, sampler)
  case TypeVmeImageINTEL(dest: Operand, imageType: Operand)
      extends Instruction(Instruction.Definition.TypeVmeImageINTEL, dest, imageType)
  case TypeAvcImePayloadINTEL(dest: Operand) extends Instruction(Instruction.Definition.TypeAvcImePayloadINTEL, dest)
  case TypeAvcRefPayloadINTEL(dest: Operand) extends Instruction(Instruction.Definition.TypeAvcRefPayloadINTEL, dest)
  case TypeAvcSicPayloadINTEL(dest: Operand) extends Instruction(Instruction.Definition.TypeAvcSicPayloadINTEL, dest)
  case TypeAvcMcePayloadINTEL(dest: Operand) extends Instruction(Instruction.Definition.TypeAvcMcePayloadINTEL, dest)
  case TypeAvcMceResultINTEL(dest: Operand) extends Instruction(Instruction.Definition.TypeAvcMceResultINTEL, dest)
  case TypeAvcImeResultINTEL(dest: Operand) extends Instruction(Instruction.Definition.TypeAvcImeResultINTEL, dest)
  case TypeAvcImeResultSingleReferenceStreamoutINTEL(dest: Operand)
      extends Instruction(Instruction.Definition.TypeAvcImeResultSingleReferenceStreamoutINTEL, dest)
  case TypeAvcImeResultDualReferenceStreamoutINTEL(dest: Operand)
      extends Instruction(Instruction.Definition.TypeAvcImeResultDualReferenceStreamoutINTEL, dest)
  case TypeAvcImeSingleReferenceStreaminINTEL(dest: Operand)
      extends Instruction(Instruction.Definition.TypeAvcImeSingleReferenceStreaminINTEL, dest)
  case TypeAvcImeDualReferenceStreaminINTEL(dest: Operand)
      extends Instruction(Instruction.Definition.TypeAvcImeDualReferenceStreaminINTEL, dest)
  case TypeAvcRefResultINTEL(dest: Operand) extends Instruction(Instruction.Definition.TypeAvcRefResultINTEL, dest)
  case TypeAvcSicResultINTEL(dest: Operand) extends Instruction(Instruction.Definition.TypeAvcSicResultINTEL, dest)
  case SubgroupAvcMceGetDefaultInterBaseMultiReferencePenaltyINTEL(
      resultType: Operand,
      dest: Operand,
      sliceType: Operand,
      qp: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcMceGetDefaultInterBaseMultiReferencePenaltyINTEL,
        resultType,
        dest,
        sliceType,
        qp
      )
  case SubgroupAvcMceSetInterBaseMultiReferencePenaltyINTEL(
      resultType: Operand,
      dest: Operand,
      referenceBasePenalty: Operand,
      payload: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcMceSetInterBaseMultiReferencePenaltyINTEL,
        resultType,
        dest,
        referenceBasePenalty,
        payload
      )
  case SubgroupAvcMceGetDefaultInterShapePenaltyINTEL(
      resultType: Operand,
      dest: Operand,
      sliceType: Operand,
      qp: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcMceGetDefaultInterShapePenaltyINTEL,
        resultType,
        dest,
        sliceType,
        qp
      )
  case SubgroupAvcMceSetInterShapePenaltyINTEL(
      resultType: Operand,
      dest: Operand,
      packedShapePenalty: Operand,
      payload: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcMceSetInterShapePenaltyINTEL,
        resultType,
        dest,
        packedShapePenalty,
        payload
      )
  case SubgroupAvcMceGetDefaultInterDirectionPenaltyINTEL(
      resultType: Operand,
      dest: Operand,
      sliceType: Operand,
      qp: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcMceGetDefaultInterDirectionPenaltyINTEL,
        resultType,
        dest,
        sliceType,
        qp
      )
  case SubgroupAvcMceSetInterDirectionPenaltyINTEL(
      resultType: Operand,
      dest: Operand,
      directionCost: Operand,
      payload: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcMceSetInterDirectionPenaltyINTEL,
        resultType,
        dest,
        directionCost,
        payload
      )
  case SubgroupAvcMceGetDefaultIntraLumaShapePenaltyINTEL(
      resultType: Operand,
      dest: Operand,
      sliceType: Operand,
      qp: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcMceGetDefaultIntraLumaShapePenaltyINTEL,
        resultType,
        dest,
        sliceType,
        qp
      )
  case SubgroupAvcMceGetDefaultInterMotionVectorCostTableINTEL(
      resultType: Operand,
      dest: Operand,
      sliceType: Operand,
      qp: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcMceGetDefaultInterMotionVectorCostTableINTEL,
        resultType,
        dest,
        sliceType,
        qp
      )
  case SubgroupAvcMceGetDefaultHighPenaltyCostTableINTEL(resultType: Operand, dest: Operand)
      extends Instruction(Instruction.Definition.SubgroupAvcMceGetDefaultHighPenaltyCostTableINTEL, resultType, dest)
  case SubgroupAvcMceGetDefaultMediumPenaltyCostTableINTEL(resultType: Operand, dest: Operand)
      extends Instruction(Instruction.Definition.SubgroupAvcMceGetDefaultMediumPenaltyCostTableINTEL, resultType, dest)
  case SubgroupAvcMceGetDefaultLowPenaltyCostTableINTEL(resultType: Operand, dest: Operand)
      extends Instruction(Instruction.Definition.SubgroupAvcMceGetDefaultLowPenaltyCostTableINTEL, resultType, dest)
  case SubgroupAvcMceSetMotionVectorCostFunctionINTEL(
      resultType: Operand,
      dest: Operand,
      packedCostCenterDelta: Operand,
      packedCostTable: Operand,
      costPrecision: Operand,
      payload: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcMceSetMotionVectorCostFunctionINTEL,
        resultType,
        dest,
        packedCostCenterDelta,
        packedCostTable,
        costPrecision,
        payload
      )
  case SubgroupAvcMceGetDefaultIntraLumaModePenaltyINTEL(
      resultType: Operand,
      dest: Operand,
      sliceType: Operand,
      qp: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcMceGetDefaultIntraLumaModePenaltyINTEL,
        resultType,
        dest,
        sliceType,
        qp
      )
  case SubgroupAvcMceGetDefaultNonDcLumaIntraPenaltyINTEL(resultType: Operand, dest: Operand)
      extends Instruction(Instruction.Definition.SubgroupAvcMceGetDefaultNonDcLumaIntraPenaltyINTEL, resultType, dest)
  case SubgroupAvcMceGetDefaultIntraChromaModeBasePenaltyINTEL(resultType: Operand, dest: Operand) extends Instruction(
        Instruction.Definition.SubgroupAvcMceGetDefaultIntraChromaModeBasePenaltyINTEL,
        resultType,
        dest
      )
  case SubgroupAvcMceSetAcOnlyHaarINTEL(resultType: Operand, dest: Operand, payload: Operand)
      extends Instruction(Instruction.Definition.SubgroupAvcMceSetAcOnlyHaarINTEL, resultType, dest, payload)
  case SubgroupAvcMceSetSourceInterlacedFieldPolarityINTEL(
      resultType: Operand,
      dest: Operand,
      srcFieldPolarity: Operand,
      payload: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcMceSetSourceInterlacedFieldPolarityINTEL,
        resultType,
        dest,
        srcFieldPolarity,
        payload
      )
  case SubgroupAvcMceSetSingleReferenceInterlacedFieldPolarityINTEL(
      resultType: Operand,
      dest: Operand,
      referenceFieldPolarity: Operand,
      payload: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcMceSetSingleReferenceInterlacedFieldPolarityINTEL,
        resultType,
        dest,
        referenceFieldPolarity,
        payload
      )
  case SubgroupAvcMceSetDualReferenceInterlacedFieldPolaritiesINTEL(
      resultType: Operand,
      dest: Operand,
      forwardReferenceFieldPolarity: Operand,
      backwardReferenceFieldPolarity: Operand,
      payload: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcMceSetDualReferenceInterlacedFieldPolaritiesINTEL,
        resultType,
        dest,
        forwardReferenceFieldPolarity,
        backwardReferenceFieldPolarity,
        payload
      )
  case SubgroupAvcMceConvertToImePayloadINTEL(resultType: Operand, dest: Operand, payload: Operand)
      extends Instruction(Instruction.Definition.SubgroupAvcMceConvertToImePayloadINTEL, resultType, dest, payload)
  case SubgroupAvcMceConvertToImeResultINTEL(resultType: Operand, dest: Operand, payload: Operand)
      extends Instruction(Instruction.Definition.SubgroupAvcMceConvertToImeResultINTEL, resultType, dest, payload)
  case SubgroupAvcMceConvertToRefPayloadINTEL(resultType: Operand, dest: Operand, payload: Operand)
      extends Instruction(Instruction.Definition.SubgroupAvcMceConvertToRefPayloadINTEL, resultType, dest, payload)
  case SubgroupAvcMceConvertToRefResultINTEL(resultType: Operand, dest: Operand, payload: Operand)
      extends Instruction(Instruction.Definition.SubgroupAvcMceConvertToRefResultINTEL, resultType, dest, payload)
  case SubgroupAvcMceConvertToSicPayloadINTEL(resultType: Operand, dest: Operand, payload: Operand)
      extends Instruction(Instruction.Definition.SubgroupAvcMceConvertToSicPayloadINTEL, resultType, dest, payload)
  case SubgroupAvcMceConvertToSicResultINTEL(resultType: Operand, dest: Operand, payload: Operand)
      extends Instruction(Instruction.Definition.SubgroupAvcMceConvertToSicResultINTEL, resultType, dest, payload)
  case SubgroupAvcMceGetMotionVectorsINTEL(resultType: Operand, dest: Operand, payload: Operand)
      extends Instruction(Instruction.Definition.SubgroupAvcMceGetMotionVectorsINTEL, resultType, dest, payload)
  case SubgroupAvcMceGetInterDistortionsINTEL(resultType: Operand, dest: Operand, payload: Operand)
      extends Instruction(Instruction.Definition.SubgroupAvcMceGetInterDistortionsINTEL, resultType, dest, payload)
  case SubgroupAvcMceGetBestInterDistortionsINTEL(resultType: Operand, dest: Operand, payload: Operand)
      extends Instruction(Instruction.Definition.SubgroupAvcMceGetBestInterDistortionsINTEL, resultType, dest, payload)
  case SubgroupAvcMceGetInterMajorShapeINTEL(resultType: Operand, dest: Operand, payload: Operand)
      extends Instruction(Instruction.Definition.SubgroupAvcMceGetInterMajorShapeINTEL, resultType, dest, payload)
  case SubgroupAvcMceGetInterMinorShapeINTEL(resultType: Operand, dest: Operand, payload: Operand)
      extends Instruction(Instruction.Definition.SubgroupAvcMceGetInterMinorShapeINTEL, resultType, dest, payload)
  case SubgroupAvcMceGetInterDirectionsINTEL(resultType: Operand, dest: Operand, payload: Operand)
      extends Instruction(Instruction.Definition.SubgroupAvcMceGetInterDirectionsINTEL, resultType, dest, payload)
  case SubgroupAvcMceGetInterMotionVectorCountINTEL(
      resultType: Operand,
      dest: Operand,
      payload: Operand
  ) extends Instruction(Instruction.Definition.SubgroupAvcMceGetInterMotionVectorCountINTEL, resultType, dest, payload)
  case SubgroupAvcMceGetInterReferenceIdsINTEL(resultType: Operand, dest: Operand, payload: Operand)
      extends Instruction(Instruction.Definition.SubgroupAvcMceGetInterReferenceIdsINTEL, resultType, dest, payload)
  case SubgroupAvcMceGetInterReferenceInterlacedFieldPolaritiesINTEL(
      resultType: Operand,
      dest: Operand,
      packedReferenceIds: Operand,
      packedReferenceParameterFieldPolarities: Operand,
      payload: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcMceGetInterReferenceInterlacedFieldPolaritiesINTEL,
        resultType,
        dest,
        packedReferenceIds,
        packedReferenceParameterFieldPolarities,
        payload
      )
  case SubgroupAvcImeInitializeINTEL(
      resultType: Operand,
      dest: Operand,
      srcCoord: Operand,
      partitionMask: Operand,
      sADAdjustment: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcImeInitializeINTEL,
        resultType,
        dest,
        srcCoord,
        partitionMask,
        sADAdjustment
      )
  case SubgroupAvcImeSetSingleReferenceINTEL(
      resultType: Operand,
      dest: Operand,
      refOffset: Operand,
      searchWindowConfig: Operand,
      payload: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcImeSetSingleReferenceINTEL,
        resultType,
        dest,
        refOffset,
        searchWindowConfig,
        payload
      )
  case SubgroupAvcImeSetDualReferenceINTEL(
      resultType: Operand,
      dest: Operand,
      fwdRefOffset: Operand,
      bwdRefOffset: Operand,
      searchWindowConfig: Operand,
      payload: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcImeSetDualReferenceINTEL,
        resultType,
        dest,
        fwdRefOffset,
        bwdRefOffset,
        searchWindowConfig,
        payload
      )
  case SubgroupAvcImeRefWindowSizeINTEL(
      resultType: Operand,
      dest: Operand,
      searchWindowConfig: Operand,
      dualRef: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcImeRefWindowSizeINTEL,
        resultType,
        dest,
        searchWindowConfig,
        dualRef
      )
  case SubgroupAvcImeAdjustRefOffsetINTEL(
      resultType: Operand,
      dest: Operand,
      refOffset: Operand,
      srcCoord: Operand,
      refWindowSize: Operand,
      imageSize: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcImeAdjustRefOffsetINTEL,
        resultType,
        dest,
        refOffset,
        srcCoord,
        refWindowSize,
        imageSize
      )
  case SubgroupAvcImeConvertToMcePayloadINTEL(resultType: Operand, dest: Operand, payload: Operand)
      extends Instruction(Instruction.Definition.SubgroupAvcImeConvertToMcePayloadINTEL, resultType, dest, payload)
  case SubgroupAvcImeSetMaxMotionVectorCountINTEL(
      resultType: Operand,
      dest: Operand,
      maxMotionVectorCount: Operand,
      payload: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcImeSetMaxMotionVectorCountINTEL,
        resultType,
        dest,
        maxMotionVectorCount,
        payload
      )
  case SubgroupAvcImeSetUnidirectionalMixDisableINTEL(resultType: Operand, dest: Operand, payload: Operand)
      extends Instruction(
        Instruction.Definition.SubgroupAvcImeSetUnidirectionalMixDisableINTEL,
        resultType,
        dest,
        payload
      )
  case SubgroupAvcImeSetEarlySearchTerminationThresholdINTEL(
      resultType: Operand,
      dest: Operand,
      threshold: Operand,
      payload: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcImeSetEarlySearchTerminationThresholdINTEL,
        resultType,
        dest,
        threshold,
        payload
      )
  case SubgroupAvcImeSetWeightedSadINTEL(
      resultType: Operand,
      dest: Operand,
      packedSadWeights: Operand,
      payload: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcImeSetWeightedSadINTEL,
        resultType,
        dest,
        packedSadWeights,
        payload
      )
  case SubgroupAvcImeEvaluateWithSingleReferenceINTEL(
      resultType: Operand,
      dest: Operand,
      srcImage: Operand,
      refImage: Operand,
      payload: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcImeEvaluateWithSingleReferenceINTEL,
        resultType,
        dest,
        srcImage,
        refImage,
        payload
      )
  case SubgroupAvcImeEvaluateWithDualReferenceINTEL(
      resultType: Operand,
      dest: Operand,
      srcImage: Operand,
      fwdRefImage: Operand,
      bwdRefImage: Operand,
      payload: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcImeEvaluateWithDualReferenceINTEL,
        resultType,
        dest,
        srcImage,
        fwdRefImage,
        bwdRefImage,
        payload
      )
  case SubgroupAvcImeEvaluateWithSingleReferenceStreaminINTEL(
      resultType: Operand,
      dest: Operand,
      srcImage: Operand,
      refImage: Operand,
      payload: Operand,
      streaminComponents: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcImeEvaluateWithSingleReferenceStreaminINTEL,
        resultType,
        dest,
        srcImage,
        refImage,
        payload,
        streaminComponents
      )
  case SubgroupAvcImeEvaluateWithDualReferenceStreaminINTEL(
      resultType: Operand,
      dest: Operand,
      srcImage: Operand,
      fwdRefImage: Operand,
      bwdRefImage: Operand,
      payload: Operand,
      streaminComponents: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcImeEvaluateWithDualReferenceStreaminINTEL,
        resultType,
        dest,
        srcImage,
        fwdRefImage,
        bwdRefImage,
        payload,
        streaminComponents
      )
  case SubgroupAvcImeEvaluateWithSingleReferenceStreamoutINTEL(
      resultType: Operand,
      dest: Operand,
      srcImage: Operand,
      refImage: Operand,
      payload: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcImeEvaluateWithSingleReferenceStreamoutINTEL,
        resultType,
        dest,
        srcImage,
        refImage,
        payload
      )
  case SubgroupAvcImeEvaluateWithDualReferenceStreamoutINTEL(
      resultType: Operand,
      dest: Operand,
      srcImage: Operand,
      fwdRefImage: Operand,
      bwdRefImage: Operand,
      payload: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcImeEvaluateWithDualReferenceStreamoutINTEL,
        resultType,
        dest,
        srcImage,
        fwdRefImage,
        bwdRefImage,
        payload
      )
  case SubgroupAvcImeEvaluateWithSingleReferenceStreaminoutINTEL(
      resultType: Operand,
      dest: Operand,
      srcImage: Operand,
      refImage: Operand,
      payload: Operand,
      streaminComponents: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcImeEvaluateWithSingleReferenceStreaminoutINTEL,
        resultType,
        dest,
        srcImage,
        refImage,
        payload,
        streaminComponents
      )
  case SubgroupAvcImeEvaluateWithDualReferenceStreaminoutINTEL(
      resultType: Operand,
      dest: Operand,
      srcImage: Operand,
      fwdRefImage: Operand,
      bwdRefImage: Operand,
      payload: Operand,
      streaminComponents: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcImeEvaluateWithDualReferenceStreaminoutINTEL,
        resultType,
        dest,
        srcImage,
        fwdRefImage,
        bwdRefImage,
        payload,
        streaminComponents
      )
  case SubgroupAvcImeConvertToMceResultINTEL(resultType: Operand, dest: Operand, payload: Operand)
      extends Instruction(Instruction.Definition.SubgroupAvcImeConvertToMceResultINTEL, resultType, dest, payload)
  case SubgroupAvcImeGetSingleReferenceStreaminINTEL(
      resultType: Operand,
      dest: Operand,
      payload: Operand
  ) extends Instruction(Instruction.Definition.SubgroupAvcImeGetSingleReferenceStreaminINTEL, resultType, dest, payload)
  case SubgroupAvcImeGetDualReferenceStreaminINTEL(resultType: Operand, dest: Operand, payload: Operand)
      extends Instruction(Instruction.Definition.SubgroupAvcImeGetDualReferenceStreaminINTEL, resultType, dest, payload)
  case SubgroupAvcImeStripSingleReferenceStreamoutINTEL(resultType: Operand, dest: Operand, payload: Operand)
      extends Instruction(
        Instruction.Definition.SubgroupAvcImeStripSingleReferenceStreamoutINTEL,
        resultType,
        dest,
        payload
      )
  case SubgroupAvcImeStripDualReferenceStreamoutINTEL(resultType: Operand, dest: Operand, payload: Operand)
      extends Instruction(
        Instruction.Definition.SubgroupAvcImeStripDualReferenceStreamoutINTEL,
        resultType,
        dest,
        payload
      )
  case SubgroupAvcImeGetStreamoutSingleReferenceMajorShapeMotionVectorsINTEL(
      resultType: Operand,
      dest: Operand,
      payload: Operand,
      majorShape: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcImeGetStreamoutSingleReferenceMajorShapeMotionVectorsINTEL,
        resultType,
        dest,
        payload,
        majorShape
      )
  case SubgroupAvcImeGetStreamoutSingleReferenceMajorShapeDistortionsINTEL(
      resultType: Operand,
      dest: Operand,
      payload: Operand,
      majorShape: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcImeGetStreamoutSingleReferenceMajorShapeDistortionsINTEL,
        resultType,
        dest,
        payload,
        majorShape
      )
  case SubgroupAvcImeGetStreamoutSingleReferenceMajorShapeReferenceIdsINTEL(
      resultType: Operand,
      dest: Operand,
      payload: Operand,
      majorShape: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcImeGetStreamoutSingleReferenceMajorShapeReferenceIdsINTEL,
        resultType,
        dest,
        payload,
        majorShape
      )
  case SubgroupAvcImeGetStreamoutDualReferenceMajorShapeMotionVectorsINTEL(
      resultType: Operand,
      dest: Operand,
      payload: Operand,
      majorShape: Operand,
      direction: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcImeGetStreamoutDualReferenceMajorShapeMotionVectorsINTEL,
        resultType,
        dest,
        payload,
        majorShape,
        direction
      )
  case SubgroupAvcImeGetStreamoutDualReferenceMajorShapeDistortionsINTEL(
      resultType: Operand,
      dest: Operand,
      payload: Operand,
      majorShape: Operand,
      direction: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcImeGetStreamoutDualReferenceMajorShapeDistortionsINTEL,
        resultType,
        dest,
        payload,
        majorShape,
        direction
      )
  case SubgroupAvcImeGetStreamoutDualReferenceMajorShapeReferenceIdsINTEL(
      resultType: Operand,
      dest: Operand,
      payload: Operand,
      majorShape: Operand,
      direction: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcImeGetStreamoutDualReferenceMajorShapeReferenceIdsINTEL,
        resultType,
        dest,
        payload,
        majorShape,
        direction
      )
  case SubgroupAvcImeGetBorderReachedINTEL(resultType: Operand, dest: Operand, imageSelect: Operand, payload: Operand)
      extends Instruction(
        Instruction.Definition.SubgroupAvcImeGetBorderReachedINTEL,
        resultType,
        dest,
        imageSelect,
        payload
      )
  case SubgroupAvcImeGetTruncatedSearchIndicationINTEL(resultType: Operand, dest: Operand, payload: Operand)
      extends Instruction(
        Instruction.Definition.SubgroupAvcImeGetTruncatedSearchIndicationINTEL,
        resultType,
        dest,
        payload
      )
  case SubgroupAvcImeGetUnidirectionalEarlySearchTerminationINTEL(resultType: Operand, dest: Operand, payload: Operand)
      extends Instruction(
        Instruction.Definition.SubgroupAvcImeGetUnidirectionalEarlySearchTerminationINTEL,
        resultType,
        dest,
        payload
      )
  case SubgroupAvcImeGetWeightingPatternMinimumMotionVectorINTEL(resultType: Operand, dest: Operand, payload: Operand)
      extends Instruction(
        Instruction.Definition.SubgroupAvcImeGetWeightingPatternMinimumMotionVectorINTEL,
        resultType,
        dest,
        payload
      )
  case SubgroupAvcImeGetWeightingPatternMinimumDistortionINTEL(resultType: Operand, dest: Operand, payload: Operand)
      extends Instruction(
        Instruction.Definition.SubgroupAvcImeGetWeightingPatternMinimumDistortionINTEL,
        resultType,
        dest,
        payload
      )
  case SubgroupAvcFmeInitializeINTEL(
      resultType: Operand,
      dest: Operand,
      srcCoord: Operand,
      motionVectors: Operand,
      majorShapes: Operand,
      minorShapes: Operand,
      direction: Operand,
      pixelResolution: Operand,
      sadAdjustment: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcFmeInitializeINTEL,
        resultType,
        dest,
        srcCoord,
        motionVectors,
        majorShapes,
        minorShapes,
        direction,
        pixelResolution,
        sadAdjustment
      )
  case SubgroupAvcBmeInitializeINTEL(
      resultType: Operand,
      dest: Operand,
      srcCoord: Operand,
      motionVectors: Operand,
      majorShapes: Operand,
      minorShapes: Operand,
      direction: Operand,
      pixelResolution: Operand,
      bidirectionalWeight: Operand,
      sadAdjustment: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcBmeInitializeINTEL,
        resultType,
        dest,
        srcCoord,
        motionVectors,
        majorShapes,
        minorShapes,
        direction,
        pixelResolution,
        bidirectionalWeight,
        sadAdjustment
      )
  case SubgroupAvcRefConvertToMcePayloadINTEL(resultType: Operand, dest: Operand, payload: Operand)
      extends Instruction(Instruction.Definition.SubgroupAvcRefConvertToMcePayloadINTEL, resultType, dest, payload)
  case SubgroupAvcRefSetBidirectionalMixDisableINTEL(
      resultType: Operand,
      dest: Operand,
      payload: Operand
  ) extends Instruction(Instruction.Definition.SubgroupAvcRefSetBidirectionalMixDisableINTEL, resultType, dest, payload)
  case SubgroupAvcRefSetBilinearFilterEnableINTEL(resultType: Operand, dest: Operand, payload: Operand)
      extends Instruction(Instruction.Definition.SubgroupAvcRefSetBilinearFilterEnableINTEL, resultType, dest, payload)
  case SubgroupAvcRefEvaluateWithSingleReferenceINTEL(
      resultType: Operand,
      dest: Operand,
      srcImage: Operand,
      refImage: Operand,
      payload: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcRefEvaluateWithSingleReferenceINTEL,
        resultType,
        dest,
        srcImage,
        refImage,
        payload
      )
  case SubgroupAvcRefEvaluateWithDualReferenceINTEL(
      resultType: Operand,
      dest: Operand,
      srcImage: Operand,
      fwdRefImage: Operand,
      bwdRefImage: Operand,
      payload: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcRefEvaluateWithDualReferenceINTEL,
        resultType,
        dest,
        srcImage,
        fwdRefImage,
        bwdRefImage,
        payload
      )
  case SubgroupAvcRefEvaluateWithMultiReferenceINTEL(
      resultType: Operand,
      dest: Operand,
      srcImage: Operand,
      packedReferenceIds: Operand,
      payload: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcRefEvaluateWithMultiReferenceINTEL,
        resultType,
        dest,
        srcImage,
        packedReferenceIds,
        payload
      )
  case SubgroupAvcRefEvaluateWithMultiReferenceInterlacedINTEL(
      resultType: Operand,
      dest: Operand,
      srcImage: Operand,
      packedReferenceIds: Operand,
      packedReferenceFieldPolarities: Operand,
      payload: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcRefEvaluateWithMultiReferenceInterlacedINTEL,
        resultType,
        dest,
        srcImage,
        packedReferenceIds,
        packedReferenceFieldPolarities,
        payload
      )
  case SubgroupAvcRefConvertToMceResultINTEL(resultType: Operand, dest: Operand, payload: Operand)
      extends Instruction(Instruction.Definition.SubgroupAvcRefConvertToMceResultINTEL, resultType, dest, payload)
  case SubgroupAvcSicInitializeINTEL(resultType: Operand, dest: Operand, srcCoord: Operand)
      extends Instruction(Instruction.Definition.SubgroupAvcSicInitializeINTEL, resultType, dest, srcCoord)
  case SubgroupAvcSicConfigureSkcINTEL(
      resultType: Operand,
      dest: Operand,
      skipBlockPartitionType: Operand,
      skipMotionVectorMask: Operand,
      motionVectors: Operand,
      bidirectionalWeight: Operand,
      sadAdjustment: Operand,
      payload: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcSicConfigureSkcINTEL,
        resultType,
        dest,
        skipBlockPartitionType,
        skipMotionVectorMask,
        motionVectors,
        bidirectionalWeight,
        sadAdjustment,
        payload
      )
  case SubgroupAvcSicConfigureIpeLumaINTEL(
      resultType: Operand,
      dest: Operand,
      lumaIntraPartitionMask: Operand,
      intraNeighbourAvailabilty: Operand,
      leftEdgeLumaPixels: Operand,
      upperLeftCornerLumaPixel: Operand,
      upperEdgeLumaPixels: Operand,
      upperRightEdgeLumaPixels: Operand,
      sadAdjustment: Operand,
      payload: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcSicConfigureIpeLumaINTEL,
        resultType,
        dest,
        lumaIntraPartitionMask,
        intraNeighbourAvailabilty,
        leftEdgeLumaPixels,
        upperLeftCornerLumaPixel,
        upperEdgeLumaPixels,
        upperRightEdgeLumaPixels,
        sadAdjustment,
        payload
      )
  case SubgroupAvcSicConfigureIpeLumaChromaINTEL(
      resultType: Operand,
      dest: Operand,
      lumaIntraPartitionMask: Operand,
      intraNeighbourAvailabilty: Operand,
      leftEdgeLumaPixels: Operand,
      upperLeftCornerLumaPixel: Operand,
      upperEdgeLumaPixels: Operand,
      upperRightEdgeLumaPixels: Operand,
      leftEdgeChromaPixels: Operand,
      upperLeftCornerChromaPixel: Operand,
      upperEdgeChromaPixels: Operand,
      sadAdjustment: Operand,
      payload: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcSicConfigureIpeLumaChromaINTEL,
        resultType,
        dest,
        lumaIntraPartitionMask,
        intraNeighbourAvailabilty,
        leftEdgeLumaPixels,
        upperLeftCornerLumaPixel,
        upperEdgeLumaPixels,
        upperRightEdgeLumaPixels,
        leftEdgeChromaPixels,
        upperLeftCornerChromaPixel,
        upperEdgeChromaPixels,
        sadAdjustment,
        payload
      )
  case SubgroupAvcSicGetMotionVectorMaskINTEL(
      resultType: Operand,
      dest: Operand,
      skipBlockPartitionType: Operand,
      direction: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcSicGetMotionVectorMaskINTEL,
        resultType,
        dest,
        skipBlockPartitionType,
        direction
      )
  case SubgroupAvcSicConvertToMcePayloadINTEL(resultType: Operand, dest: Operand, payload: Operand)
      extends Instruction(Instruction.Definition.SubgroupAvcSicConvertToMcePayloadINTEL, resultType, dest, payload)
  case SubgroupAvcSicSetIntraLumaShapePenaltyINTEL(
      resultType: Operand,
      dest: Operand,
      packedShapePenalty: Operand,
      payload: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcSicSetIntraLumaShapePenaltyINTEL,
        resultType,
        dest,
        packedShapePenalty,
        payload
      )
  case SubgroupAvcSicSetIntraLumaModeCostFunctionINTEL(
      resultType: Operand,
      dest: Operand,
      lumaModePenalty: Operand,
      lumaPackedNeighborModes: Operand,
      lumaPackedNonDcPenalty: Operand,
      payload: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcSicSetIntraLumaModeCostFunctionINTEL,
        resultType,
        dest,
        lumaModePenalty,
        lumaPackedNeighborModes,
        lumaPackedNonDcPenalty,
        payload
      )
  case SubgroupAvcSicSetIntraChromaModeCostFunctionINTEL(
      resultType: Operand,
      dest: Operand,
      chromaModeBasePenalty: Operand,
      payload: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcSicSetIntraChromaModeCostFunctionINTEL,
        resultType,
        dest,
        chromaModeBasePenalty,
        payload
      )
  case SubgroupAvcSicSetBilinearFilterEnableINTEL(resultType: Operand, dest: Operand, payload: Operand)
      extends Instruction(Instruction.Definition.SubgroupAvcSicSetBilinearFilterEnableINTEL, resultType, dest, payload)
  case SubgroupAvcSicSetSkcForwardTransformEnableINTEL(
      resultType: Operand,
      dest: Operand,
      packedSadCoefficients: Operand,
      payload: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcSicSetSkcForwardTransformEnableINTEL,
        resultType,
        dest,
        packedSadCoefficients,
        payload
      )
  case SubgroupAvcSicSetBlockBasedRawSkipSadINTEL(
      resultType: Operand,
      dest: Operand,
      blockBasedSkipType: Operand,
      payload: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcSicSetBlockBasedRawSkipSadINTEL,
        resultType,
        dest,
        blockBasedSkipType,
        payload
      )
  case SubgroupAvcSicEvaluateIpeINTEL(resultType: Operand, dest: Operand, srcImage: Operand, payload: Operand)
      extends Instruction(Instruction.Definition.SubgroupAvcSicEvaluateIpeINTEL, resultType, dest, srcImage, payload)
  case SubgroupAvcSicEvaluateWithSingleReferenceINTEL(
      resultType: Operand,
      dest: Operand,
      srcImage: Operand,
      refImage: Operand,
      payload: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcSicEvaluateWithSingleReferenceINTEL,
        resultType,
        dest,
        srcImage,
        refImage,
        payload
      )
  case SubgroupAvcSicEvaluateWithDualReferenceINTEL(
      resultType: Operand,
      dest: Operand,
      srcImage: Operand,
      fwdRefImage: Operand,
      bwdRefImage: Operand,
      payload: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcSicEvaluateWithDualReferenceINTEL,
        resultType,
        dest,
        srcImage,
        fwdRefImage,
        bwdRefImage,
        payload
      )
  case SubgroupAvcSicEvaluateWithMultiReferenceINTEL(
      resultType: Operand,
      dest: Operand,
      srcImage: Operand,
      packedReferenceIds: Operand,
      payload: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcSicEvaluateWithMultiReferenceINTEL,
        resultType,
        dest,
        srcImage,
        packedReferenceIds,
        payload
      )
  case SubgroupAvcSicEvaluateWithMultiReferenceInterlacedINTEL(
      resultType: Operand,
      dest: Operand,
      srcImage: Operand,
      packedReferenceIds: Operand,
      packedReferenceFieldPolarities: Operand,
      payload: Operand
  ) extends Instruction(
        Instruction.Definition.SubgroupAvcSicEvaluateWithMultiReferenceInterlacedINTEL,
        resultType,
        dest,
        srcImage,
        packedReferenceIds,
        packedReferenceFieldPolarities,
        payload
      )
  case SubgroupAvcSicConvertToMceResultINTEL(resultType: Operand, dest: Operand, payload: Operand)
      extends Instruction(Instruction.Definition.SubgroupAvcSicConvertToMceResultINTEL, resultType, dest, payload)
  case SubgroupAvcSicGetIpeLumaShapeINTEL(resultType: Operand, dest: Operand, payload: Operand)
      extends Instruction(Instruction.Definition.SubgroupAvcSicGetIpeLumaShapeINTEL, resultType, dest, payload)
  case SubgroupAvcSicGetBestIpeLumaDistortionINTEL(resultType: Operand, dest: Operand, payload: Operand)
      extends Instruction(Instruction.Definition.SubgroupAvcSicGetBestIpeLumaDistortionINTEL, resultType, dest, payload)
  case SubgroupAvcSicGetBestIpeChromaDistortionINTEL(
      resultType: Operand,
      dest: Operand,
      payload: Operand
  ) extends Instruction(Instruction.Definition.SubgroupAvcSicGetBestIpeChromaDistortionINTEL, resultType, dest, payload)
  case SubgroupAvcSicGetPackedIpeLumaModesINTEL(resultType: Operand, dest: Operand, payload: Operand)
      extends Instruction(Instruction.Definition.SubgroupAvcSicGetPackedIpeLumaModesINTEL, resultType, dest, payload)
  case SubgroupAvcSicGetIpeChromaModeINTEL(resultType: Operand, dest: Operand, payload: Operand)
      extends Instruction(Instruction.Definition.SubgroupAvcSicGetIpeChromaModeINTEL, resultType, dest, payload)
  case SubgroupAvcSicGetPackedSkcLumaCountThresholdINTEL(resultType: Operand, dest: Operand, payload: Operand)
      extends Instruction(
        Instruction.Definition.SubgroupAvcSicGetPackedSkcLumaCountThresholdINTEL,
        resultType,
        dest,
        payload
      )
  case SubgroupAvcSicGetPackedSkcLumaSumThresholdINTEL(resultType: Operand, dest: Operand, payload: Operand)
      extends Instruction(
        Instruction.Definition.SubgroupAvcSicGetPackedSkcLumaSumThresholdINTEL,
        resultType,
        dest,
        payload
      )
  case SubgroupAvcSicGetInterRawSadsINTEL(resultType: Operand, dest: Operand, payload: Operand)
      extends Instruction(Instruction.Definition.SubgroupAvcSicGetInterRawSadsINTEL, resultType, dest, payload)
  case VariableLengthArrayINTEL(resultType: Operand, dest: Operand, length: Operand)
      extends Instruction(Instruction.Definition.VariableLengthArrayINTEL, resultType, dest, length)
  case SaveMemoryINTEL(resultType: Operand, dest: Operand)
      extends Instruction(Instruction.Definition.SaveMemoryINTEL, resultType, dest)
  case RestoreMemoryINTEL(ptr: Operand) extends Instruction(Instruction.Definition.RestoreMemoryINTEL, ptr)
  case ArbitraryFloatSinCosPiALTERA(
      resultType: Operand,
      dest: Operand,
      a: Operand,
      ma: Operand,
      mResult: Operand,
      subnormal: Operand,
      rounding: Operand,
      roundingAccuracy: Operand
  ) extends Instruction(
        Instruction.Definition.ArbitraryFloatSinCosPiALTERA,
        resultType,
        dest,
        a,
        ma,
        mResult,
        subnormal,
        rounding,
        roundingAccuracy
      )
  case ArbitraryFloatCastALTERA(
      resultType: Operand,
      dest: Operand,
      a: Operand,
      ma: Operand,
      mresult: Operand,
      subnormal: Operand,
      rounding: Operand,
      accuracy: Operand
  ) extends Instruction(
        Instruction.Definition.ArbitraryFloatCastALTERA,
        resultType,
        dest,
        a,
        ma,
        mresult,
        subnormal,
        rounding,
        accuracy
      )
  case ArbitraryFloatCastFromIntALTERA(
      resultType: Operand,
      dest: Operand,
      a: Operand,
      mresult: Operand,
      fromSign: Operand,
      subnormal: Operand,
      rounding: Operand,
      accuracy: Operand
  ) extends Instruction(
        Instruction.Definition.ArbitraryFloatCastFromIntALTERA,
        resultType,
        dest,
        a,
        mresult,
        fromSign,
        subnormal,
        rounding,
        accuracy
      )
  case ArbitraryFloatCastToIntALTERA(
      resultType: Operand,
      dest: Operand,
      a: Operand,
      ma: Operand,
      toSign: Operand,
      subnormal: Operand,
      rounding: Operand,
      accuracy: Operand
  ) extends Instruction(
        Instruction.Definition.ArbitraryFloatCastToIntALTERA,
        resultType,
        dest,
        a,
        ma,
        toSign,
        subnormal,
        rounding,
        accuracy
      )
  case ArbitraryFloatAddALTERA(
      resultType: Operand,
      dest: Operand,
      a: Operand,
      ma: Operand,
      b: Operand,
      mb: Operand,
      mResult: Operand,
      subnormal: Operand,
      rounding: Operand,
      accuracy: Operand
  ) extends Instruction(
        Instruction.Definition.ArbitraryFloatAddALTERA,
        resultType,
        dest,
        a,
        ma,
        b,
        mb,
        mResult,
        subnormal,
        rounding,
        accuracy
      )
  case ArbitraryFloatSubALTERA(
      resultType: Operand,
      dest: Operand,
      a: Operand,
      ma: Operand,
      b: Operand,
      mb: Operand,
      mresult: Operand,
      subnormal: Operand,
      rounding: Operand,
      accuracy: Operand
  ) extends Instruction(
        Instruction.Definition.ArbitraryFloatSubALTERA,
        resultType,
        dest,
        a,
        ma,
        b,
        mb,
        mresult,
        subnormal,
        rounding,
        accuracy
      )
  case ArbitraryFloatMulALTERA(
      resultType: Operand,
      dest: Operand,
      a: Operand,
      ma: Operand,
      b: Operand,
      mb: Operand,
      mresult: Operand,
      subnormal: Operand,
      rounding: Operand,
      accuracy: Operand
  ) extends Instruction(
        Instruction.Definition.ArbitraryFloatMulALTERA,
        resultType,
        dest,
        a,
        ma,
        b,
        mb,
        mresult,
        subnormal,
        rounding,
        accuracy
      )
  case ArbitraryFloatDivALTERA(
      resultType: Operand,
      dest: Operand,
      a: Operand,
      ma: Operand,
      b: Operand,
      mb: Operand,
      mresult: Operand,
      subnormal: Operand,
      rounding: Operand,
      accuracy: Operand
  ) extends Instruction(
        Instruction.Definition.ArbitraryFloatDivALTERA,
        resultType,
        dest,
        a,
        ma,
        b,
        mb,
        mresult,
        subnormal,
        rounding,
        accuracy
      )
  case ArbitraryFloatGTALTERA(resultType: Operand, dest: Operand, a: Operand, ma: Operand, b: Operand, mb: Operand)
      extends Instruction(Instruction.Definition.ArbitraryFloatGTALTERA, resultType, dest, a, ma, b, mb)
  case ArbitraryFloatGEALTERA(resultType: Operand, dest: Operand, a: Operand, ma: Operand, b: Operand, mb: Operand)
      extends Instruction(Instruction.Definition.ArbitraryFloatGEALTERA, resultType, dest, a, ma, b, mb)
  case ArbitraryFloatLTALTERA(resultType: Operand, dest: Operand, a: Operand, ma: Operand, b: Operand, mb: Operand)
      extends Instruction(Instruction.Definition.ArbitraryFloatLTALTERA, resultType, dest, a, ma, b, mb)
  case ArbitraryFloatLEALTERA(resultType: Operand, dest: Operand, a: Operand, ma: Operand, b: Operand, mb: Operand)
      extends Instruction(Instruction.Definition.ArbitraryFloatLEALTERA, resultType, dest, a, ma, b, mb)
  case ArbitraryFloatEQALTERA(resultType: Operand, dest: Operand, a: Operand, ma: Operand, b: Operand, mb: Operand)
      extends Instruction(Instruction.Definition.ArbitraryFloatEQALTERA, resultType, dest, a, ma, b, mb)
  case ArbitraryFloatRecipALTERA(
      resultType: Operand,
      dest: Operand,
      a: Operand,
      ma: Operand,
      mresult: Operand,
      subnormal: Operand,
      rounding: Operand,
      accuracy: Operand
  ) extends Instruction(
        Instruction.Definition.ArbitraryFloatRecipALTERA,
        resultType,
        dest,
        a,
        ma,
        mresult,
        subnormal,
        rounding,
        accuracy
      )
  case ArbitraryFloatRSqrtALTERA(
      resultType: Operand,
      dest: Operand,
      a: Operand,
      ma: Operand,
      mresult: Operand,
      subnormal: Operand,
      rounding: Operand,
      accuracy: Operand
  ) extends Instruction(
        Instruction.Definition.ArbitraryFloatRSqrtALTERA,
        resultType,
        dest,
        a,
        ma,
        mresult,
        subnormal,
        rounding,
        accuracy
      )
  case ArbitraryFloatCbrtALTERA(
      resultType: Operand,
      dest: Operand,
      a: Operand,
      ma: Operand,
      mresult: Operand,
      subnormal: Operand,
      rounding: Operand,
      accuracy: Operand
  ) extends Instruction(
        Instruction.Definition.ArbitraryFloatCbrtALTERA,
        resultType,
        dest,
        a,
        ma,
        mresult,
        subnormal,
        rounding,
        accuracy
      )
  case ArbitraryFloatHypotALTERA(
      resultType: Operand,
      dest: Operand,
      a: Operand,
      ma: Operand,
      b: Operand,
      mb: Operand,
      mresult: Operand,
      subnormal: Operand,
      rounding: Operand,
      accuracy: Operand
  ) extends Instruction(
        Instruction.Definition.ArbitraryFloatHypotALTERA,
        resultType,
        dest,
        a,
        ma,
        b,
        mb,
        mresult,
        subnormal,
        rounding,
        accuracy
      )
  case ArbitraryFloatSqrtALTERA(
      resultType: Operand,
      dest: Operand,
      a: Operand,
      ma: Operand,
      mresult: Operand,
      subnormal: Operand,
      rounding: Operand,
      accuracy: Operand
  ) extends Instruction(
        Instruction.Definition.ArbitraryFloatSqrtALTERA,
        resultType,
        dest,
        a,
        ma,
        mresult,
        subnormal,
        rounding,
        accuracy
      )
  case ArbitraryFloatLogINTEL(
      resultType: Operand,
      dest: Operand,
      a: Operand,
      ma: Operand,
      mresult: Operand,
      subnormal: Operand,
      rounding: Operand,
      accuracy: Operand
  ) extends Instruction(
        Instruction.Definition.ArbitraryFloatLogINTEL,
        resultType,
        dest,
        a,
        ma,
        mresult,
        subnormal,
        rounding,
        accuracy
      )
  case ArbitraryFloatLog2INTEL(
      resultType: Operand,
      dest: Operand,
      a: Operand,
      ma: Operand,
      mresult: Operand,
      subnormal: Operand,
      rounding: Operand,
      accuracy: Operand
  ) extends Instruction(
        Instruction.Definition.ArbitraryFloatLog2INTEL,
        resultType,
        dest,
        a,
        ma,
        mresult,
        subnormal,
        rounding,
        accuracy
      )
  case ArbitraryFloatLog10INTEL(
      resultType: Operand,
      dest: Operand,
      a: Operand,
      ma: Operand,
      mresult: Operand,
      subnormal: Operand,
      rounding: Operand,
      accuracy: Operand
  ) extends Instruction(
        Instruction.Definition.ArbitraryFloatLog10INTEL,
        resultType,
        dest,
        a,
        ma,
        mresult,
        subnormal,
        rounding,
        accuracy
      )
  case ArbitraryFloatLog1pINTEL(
      resultType: Operand,
      dest: Operand,
      a: Operand,
      ma: Operand,
      mresult: Operand,
      subnormal: Operand,
      rounding: Operand,
      accuracy: Operand
  ) extends Instruction(
        Instruction.Definition.ArbitraryFloatLog1pINTEL,
        resultType,
        dest,
        a,
        ma,
        mresult,
        subnormal,
        rounding,
        accuracy
      )
  case ArbitraryFloatExpINTEL(
      resultType: Operand,
      dest: Operand,
      a: Operand,
      ma: Operand,
      mresult: Operand,
      subnormal: Operand,
      rounding: Operand,
      accuracy: Operand
  ) extends Instruction(
        Instruction.Definition.ArbitraryFloatExpINTEL,
        resultType,
        dest,
        a,
        ma,
        mresult,
        subnormal,
        rounding,
        accuracy
      )
  case ArbitraryFloatExp2INTEL(
      resultType: Operand,
      dest: Operand,
      a: Operand,
      ma: Operand,
      mresult: Operand,
      subnormal: Operand,
      rounding: Operand,
      accuracy: Operand
  ) extends Instruction(
        Instruction.Definition.ArbitraryFloatExp2INTEL,
        resultType,
        dest,
        a,
        ma,
        mresult,
        subnormal,
        rounding,
        accuracy
      )
  case ArbitraryFloatExp10INTEL(
      resultType: Operand,
      dest: Operand,
      a: Operand,
      ma: Operand,
      mresult: Operand,
      subnormal: Operand,
      rounding: Operand,
      accuracy: Operand
  ) extends Instruction(
        Instruction.Definition.ArbitraryFloatExp10INTEL,
        resultType,
        dest,
        a,
        ma,
        mresult,
        subnormal,
        rounding,
        accuracy
      )
  case ArbitraryFloatExpm1INTEL(
      resultType: Operand,
      dest: Operand,
      a: Operand,
      ma: Operand,
      mresult: Operand,
      subnormal: Operand,
      rounding: Operand,
      accuracy: Operand
  ) extends Instruction(
        Instruction.Definition.ArbitraryFloatExpm1INTEL,
        resultType,
        dest,
        a,
        ma,
        mresult,
        subnormal,
        rounding,
        accuracy
      )
  case ArbitraryFloatSinINTEL(
      resultType: Operand,
      dest: Operand,
      a: Operand,
      ma: Operand,
      mresult: Operand,
      subnormal: Operand,
      rounding: Operand,
      accuracy: Operand
  ) extends Instruction(
        Instruction.Definition.ArbitraryFloatSinINTEL,
        resultType,
        dest,
        a,
        ma,
        mresult,
        subnormal,
        rounding,
        accuracy
      )
  case ArbitraryFloatCosINTEL(
      resultType: Operand,
      dest: Operand,
      a: Operand,
      ma: Operand,
      mresult: Operand,
      subnormal: Operand,
      rounding: Operand,
      accuracy: Operand
  ) extends Instruction(
        Instruction.Definition.ArbitraryFloatCosINTEL,
        resultType,
        dest,
        a,
        ma,
        mresult,
        subnormal,
        rounding,
        accuracy
      )
  case ArbitraryFloatSinCosINTEL(
      resultType: Operand,
      dest: Operand,
      a: Operand,
      ma: Operand,
      mresult: Operand,
      subnormal: Operand,
      rounding: Operand,
      accuracy: Operand
  ) extends Instruction(
        Instruction.Definition.ArbitraryFloatSinCosINTEL,
        resultType,
        dest,
        a,
        ma,
        mresult,
        subnormal,
        rounding,
        accuracy
      )
  case ArbitraryFloatSinPiINTEL(
      resultType: Operand,
      dest: Operand,
      a: Operand,
      ma: Operand,
      mresult: Operand,
      subnormal: Operand,
      rounding: Operand,
      accuracy: Operand
  ) extends Instruction(
        Instruction.Definition.ArbitraryFloatSinPiINTEL,
        resultType,
        dest,
        a,
        ma,
        mresult,
        subnormal,
        rounding,
        accuracy
      )
  case ArbitraryFloatCosPiINTEL(
      resultType: Operand,
      dest: Operand,
      a: Operand,
      ma: Operand,
      mresult: Operand,
      subnormal: Operand,
      rounding: Operand,
      accuracy: Operand
  ) extends Instruction(
        Instruction.Definition.ArbitraryFloatCosPiINTEL,
        resultType,
        dest,
        a,
        ma,
        mresult,
        subnormal,
        rounding,
        accuracy
      )
  case ArbitraryFloatASinINTEL(
      resultType: Operand,
      dest: Operand,
      a: Operand,
      ma: Operand,
      mresult: Operand,
      subnormal: Operand,
      rounding: Operand,
      accuracy: Operand
  ) extends Instruction(
        Instruction.Definition.ArbitraryFloatASinINTEL,
        resultType,
        dest,
        a,
        ma,
        mresult,
        subnormal,
        rounding,
        accuracy
      )
  case ArbitraryFloatASinPiINTEL(
      resultType: Operand,
      dest: Operand,
      a: Operand,
      ma: Operand,
      mresult: Operand,
      subnormal: Operand,
      rounding: Operand,
      accuracy: Operand
  ) extends Instruction(
        Instruction.Definition.ArbitraryFloatASinPiINTEL,
        resultType,
        dest,
        a,
        ma,
        mresult,
        subnormal,
        rounding,
        accuracy
      )
  case ArbitraryFloatACosINTEL(
      resultType: Operand,
      dest: Operand,
      a: Operand,
      m1: Operand,
      mout: Operand,
      enableSubnormals: Operand,
      roundingMode: Operand,
      roundingAccuracy: Operand
  ) extends Instruction(
        Instruction.Definition.ArbitraryFloatACosINTEL,
        resultType,
        dest,
        a,
        m1,
        mout,
        enableSubnormals,
        roundingMode,
        roundingAccuracy
      )
  case ArbitraryFloatACosPiINTEL(
      resultType: Operand,
      dest: Operand,
      a: Operand,
      ma: Operand,
      mresult: Operand,
      subnormal: Operand,
      rounding: Operand,
      accuracy: Operand
  ) extends Instruction(
        Instruction.Definition.ArbitraryFloatACosPiINTEL,
        resultType,
        dest,
        a,
        ma,
        mresult,
        subnormal,
        rounding,
        accuracy
      )
  case ArbitraryFloatATanINTEL(
      resultType: Operand,
      dest: Operand,
      a: Operand,
      ma: Operand,
      mresult: Operand,
      subnormal: Operand,
      rounding: Operand,
      accuracy: Operand
  ) extends Instruction(
        Instruction.Definition.ArbitraryFloatATanINTEL,
        resultType,
        dest,
        a,
        ma,
        mresult,
        subnormal,
        rounding,
        accuracy
      )
  case ArbitraryFloatATanPiINTEL(
      resultType: Operand,
      dest: Operand,
      a: Operand,
      ma: Operand,
      mresult: Operand,
      subnormal: Operand,
      rounding: Operand,
      accuracy: Operand
  ) extends Instruction(
        Instruction.Definition.ArbitraryFloatATanPiINTEL,
        resultType,
        dest,
        a,
        ma,
        mresult,
        subnormal,
        rounding,
        accuracy
      )
  case ArbitraryFloatATan2INTEL(
      resultType: Operand,
      dest: Operand,
      a: Operand,
      ma: Operand,
      b: Operand,
      mb: Operand,
      mresult: Operand,
      subnormal: Operand,
      rounding: Operand,
      accuracy: Operand
  ) extends Instruction(
        Instruction.Definition.ArbitraryFloatATan2INTEL,
        resultType,
        dest,
        a,
        ma,
        b,
        mb,
        mresult,
        subnormal,
        rounding,
        accuracy
      )
  case ArbitraryFloatPowINTEL(
      resultType: Operand,
      dest: Operand,
      a: Operand,
      ma: Operand,
      b: Operand,
      mb: Operand,
      mresult: Operand,
      subnormal: Operand,
      rounding: Operand,
      accuracy: Operand
  ) extends Instruction(
        Instruction.Definition.ArbitraryFloatPowINTEL,
        resultType,
        dest,
        a,
        ma,
        b,
        mb,
        mresult,
        subnormal,
        rounding,
        accuracy
      )
  case ArbitraryFloatPowRINTEL(
      resultType: Operand,
      dest: Operand,
      a: Operand,
      ma: Operand,
      b: Operand,
      mb: Operand,
      mresult: Operand,
      subnormal: Operand,
      rounding: Operand,
      accuracy: Operand
  ) extends Instruction(
        Instruction.Definition.ArbitraryFloatPowRINTEL,
        resultType,
        dest,
        a,
        ma,
        b,
        mb,
        mresult,
        subnormal,
        rounding,
        accuracy
      )
  case ArbitraryFloatPowNINTEL(
      resultType: Operand,
      dest: Operand,
      a: Operand,
      ma: Operand,
      b: Operand,
      signOfB: Operand,
      mresult: Operand,
      subnormal: Operand,
      rounding: Operand,
      accuracy: Operand
  ) extends Instruction(
        Instruction.Definition.ArbitraryFloatPowNINTEL,
        resultType,
        dest,
        a,
        ma,
        b,
        signOfB,
        mresult,
        subnormal,
        rounding,
        accuracy
      )
  case LoopControlINTEL(loopControlParameters: Vec[Operand])
      extends Instruction(Instruction.Definition.LoopControlINTEL, (loopControlParameters)*)
  case AliasDomainDeclINTEL(dest: Operand, name: Option[Operand])
      extends Instruction(Instruction.Definition.AliasDomainDeclINTEL, (Vec(dest) ++ name.toVec)*)
  case AliasScopeDeclINTEL(dest: Operand, aliasDomain: Operand, name: Option[Operand])
      extends Instruction(Instruction.Definition.AliasScopeDeclINTEL, (Vec(dest, aliasDomain) ++ name.toVec)*)
  case AliasScopeListDeclINTEL(dest: Operand, aliasScope1AliasScope2: Vec[Operand])
      extends Instruction(Instruction.Definition.AliasScopeListDeclINTEL, (Vec(dest) ++ aliasScope1AliasScope2)*)
  case FixedSqrtALTERA(
      resultType: Operand,
      dest: Operand,
      input: Operand,
      s: Operand,
      i: Operand,
      rI: Operand,
      q: Operand,
      o: Operand
  ) extends Instruction(Instruction.Definition.FixedSqrtALTERA, resultType, dest, input, s, i, rI, q, o)
  case FixedRecipALTERA(
      resultType: Operand,
      dest: Operand,
      input: Operand,
      s: Operand,
      i: Operand,
      rI: Operand,
      q: Operand,
      o: Operand
  ) extends Instruction(Instruction.Definition.FixedRecipALTERA, resultType, dest, input, s, i, rI, q, o)
  case FixedRsqrtALTERA(
      resultType: Operand,
      dest: Operand,
      input: Operand,
      s: Operand,
      i: Operand,
      rI: Operand,
      q: Operand,
      o: Operand
  ) extends Instruction(Instruction.Definition.FixedRsqrtALTERA, resultType, dest, input, s, i, rI, q, o)
  case FixedSinALTERA(
      resultType: Operand,
      dest: Operand,
      input: Operand,
      s: Operand,
      i: Operand,
      rI: Operand,
      q: Operand,
      o: Operand
  ) extends Instruction(Instruction.Definition.FixedSinALTERA, resultType, dest, input, s, i, rI, q, o)
  case FixedCosALTERA(
      resultType: Operand,
      dest: Operand,
      input: Operand,
      s: Operand,
      i: Operand,
      rI: Operand,
      q: Operand,
      o: Operand
  ) extends Instruction(Instruction.Definition.FixedCosALTERA, resultType, dest, input, s, i, rI, q, o)
  case FixedSinCosALTERA(
      resultType: Operand,
      dest: Operand,
      input: Operand,
      s: Operand,
      i: Operand,
      rI: Operand,
      q: Operand,
      o: Operand
  ) extends Instruction(Instruction.Definition.FixedSinCosALTERA, resultType, dest, input, s, i, rI, q, o)
  case FixedSinPiALTERA(
      resultType: Operand,
      dest: Operand,
      input: Operand,
      s: Operand,
      i: Operand,
      rI: Operand,
      q: Operand,
      o: Operand
  ) extends Instruction(Instruction.Definition.FixedSinPiALTERA, resultType, dest, input, s, i, rI, q, o)
  case FixedCosPiALTERA(
      resultType: Operand,
      dest: Operand,
      input: Operand,
      s: Operand,
      i: Operand,
      rI: Operand,
      q: Operand,
      o: Operand
  ) extends Instruction(Instruction.Definition.FixedCosPiALTERA, resultType, dest, input, s, i, rI, q, o)
  case FixedSinCosPiALTERA(
      resultType: Operand,
      dest: Operand,
      input: Operand,
      s: Operand,
      i: Operand,
      rI: Operand,
      q: Operand,
      o: Operand
  ) extends Instruction(Instruction.Definition.FixedSinCosPiALTERA, resultType, dest, input, s, i, rI, q, o)
  case FixedLogALTERA(
      resultType: Operand,
      dest: Operand,
      input: Operand,
      s: Operand,
      i: Operand,
      rI: Operand,
      q: Operand,
      o: Operand
  ) extends Instruction(Instruction.Definition.FixedLogALTERA, resultType, dest, input, s, i, rI, q, o)
  case FixedExpALTERA(
      resultType: Operand,
      dest: Operand,
      input: Operand,
      s: Operand,
      i: Operand,
      rI: Operand,
      q: Operand,
      o: Operand
  ) extends Instruction(Instruction.Definition.FixedExpALTERA, resultType, dest, input, s, i, rI, q, o)
  case PtrCastToCrossWorkgroupALTERA(resultType: Operand, dest: Operand, pointer: Operand)
      extends Instruction(Instruction.Definition.PtrCastToCrossWorkgroupALTERA, resultType, dest, pointer)
  case CrossWorkgroupCastToPtrALTERA(resultType: Operand, dest: Operand, pointer: Operand)
      extends Instruction(Instruction.Definition.CrossWorkgroupCastToPtrALTERA, resultType, dest, pointer)
  case ReadPipeBlockingALTERA(pipe: Operand, pointer: Operand, packetSize: Operand, packetAlignment: Operand)
      extends Instruction(Instruction.Definition.ReadPipeBlockingALTERA, pipe, pointer, packetSize, packetAlignment)
  case WritePipeBlockingALTERA(pipe: Operand, pointer: Operand, packetSize: Operand, packetAlignment: Operand)
      extends Instruction(Instruction.Definition.WritePipeBlockingALTERA, pipe, pointer, packetSize, packetAlignment)
  case FPGARegALTERA(resultType: Operand, dest: Operand, input: Operand)
      extends Instruction(Instruction.Definition.FPGARegALTERA, resultType, dest, input)
  case RayQueryGetRayTMinKHR(resultType: Operand, dest: Operand, rayQuery: Operand)
      extends Instruction(Instruction.Definition.RayQueryGetRayTMinKHR, resultType, dest, rayQuery)
  case RayQueryGetRayFlagsKHR(resultType: Operand, dest: Operand, rayQuery: Operand)
      extends Instruction(Instruction.Definition.RayQueryGetRayFlagsKHR, resultType, dest, rayQuery)
  case RayQueryGetIntersectionTKHR(resultType: Operand, dest: Operand, rayQuery: Operand, intersection: Operand)
      extends Instruction(Instruction.Definition.RayQueryGetIntersectionTKHR, resultType, dest, rayQuery, intersection)
  case RayQueryGetIntersectionInstanceCustomIndexKHR(
      resultType: Operand,
      dest: Operand,
      rayQuery: Operand,
      intersection: Operand
  ) extends Instruction(
        Instruction.Definition.RayQueryGetIntersectionInstanceCustomIndexKHR,
        resultType,
        dest,
        rayQuery,
        intersection
      )
  case RayQueryGetIntersectionInstanceIdKHR(
      resultType: Operand,
      dest: Operand,
      rayQuery: Operand,
      intersection: Operand
  ) extends Instruction(
        Instruction.Definition.RayQueryGetIntersectionInstanceIdKHR,
        resultType,
        dest,
        rayQuery,
        intersection
      )
  case RayQueryGetIntersectionInstanceShaderBindingTableRecordOffsetKHR(
      resultType: Operand,
      dest: Operand,
      rayQuery: Operand,
      intersection: Operand
  ) extends Instruction(
        Instruction.Definition.RayQueryGetIntersectionInstanceShaderBindingTableRecordOffsetKHR,
        resultType,
        dest,
        rayQuery,
        intersection
      )
  case RayQueryGetIntersectionGeometryIndexKHR(
      resultType: Operand,
      dest: Operand,
      rayQuery: Operand,
      intersection: Operand
  ) extends Instruction(
        Instruction.Definition.RayQueryGetIntersectionGeometryIndexKHR,
        resultType,
        dest,
        rayQuery,
        intersection
      )
  case RayQueryGetIntersectionPrimitiveIndexKHR(
      resultType: Operand,
      dest: Operand,
      rayQuery: Operand,
      intersection: Operand
  ) extends Instruction(
        Instruction.Definition.RayQueryGetIntersectionPrimitiveIndexKHR,
        resultType,
        dest,
        rayQuery,
        intersection
      )
  case RayQueryGetIntersectionBarycentricsKHR(
      resultType: Operand,
      dest: Operand,
      rayQuery: Operand,
      intersection: Operand
  ) extends Instruction(
        Instruction.Definition.RayQueryGetIntersectionBarycentricsKHR,
        resultType,
        dest,
        rayQuery,
        intersection
      )
  case RayQueryGetIntersectionFrontFaceKHR(resultType: Operand, dest: Operand, rayQuery: Operand, intersection: Operand)
      extends Instruction(
        Instruction.Definition.RayQueryGetIntersectionFrontFaceKHR,
        resultType,
        dest,
        rayQuery,
        intersection
      )
  case RayQueryGetIntersectionCandidateAABBOpaqueKHR(resultType: Operand, dest: Operand, rayQuery: Operand)
      extends Instruction(
        Instruction.Definition.RayQueryGetIntersectionCandidateAABBOpaqueKHR,
        resultType,
        dest,
        rayQuery
      )
  case RayQueryGetIntersectionObjectRayDirectionKHR(
      resultType: Operand,
      dest: Operand,
      rayQuery: Operand,
      intersection: Operand
  ) extends Instruction(
        Instruction.Definition.RayQueryGetIntersectionObjectRayDirectionKHR,
        resultType,
        dest,
        rayQuery,
        intersection
      )
  case RayQueryGetIntersectionObjectRayOriginKHR(
      resultType: Operand,
      dest: Operand,
      rayQuery: Operand,
      intersection: Operand
  ) extends Instruction(
        Instruction.Definition.RayQueryGetIntersectionObjectRayOriginKHR,
        resultType,
        dest,
        rayQuery,
        intersection
      )
  case RayQueryGetWorldRayDirectionKHR(resultType: Operand, dest: Operand, rayQuery: Operand)
      extends Instruction(Instruction.Definition.RayQueryGetWorldRayDirectionKHR, resultType, dest, rayQuery)
  case RayQueryGetWorldRayOriginKHR(resultType: Operand, dest: Operand, rayQuery: Operand)
      extends Instruction(Instruction.Definition.RayQueryGetWorldRayOriginKHR, resultType, dest, rayQuery)
  case RayQueryGetIntersectionObjectToWorldKHR(
      resultType: Operand,
      dest: Operand,
      rayQuery: Operand,
      intersection: Operand
  ) extends Instruction(
        Instruction.Definition.RayQueryGetIntersectionObjectToWorldKHR,
        resultType,
        dest,
        rayQuery,
        intersection
      )
  case RayQueryGetIntersectionWorldToObjectKHR(
      resultType: Operand,
      dest: Operand,
      rayQuery: Operand,
      intersection: Operand
  ) extends Instruction(
        Instruction.Definition.RayQueryGetIntersectionWorldToObjectKHR,
        resultType,
        dest,
        rayQuery,
        intersection
      )
  case AtomicFAddEXT(
      resultType: Operand,
      dest: Operand,
      pointer: Operand,
      memory: Operand,
      semantics: Operand,
      value: Operand
  ) extends Instruction(Instruction.Definition.AtomicFAddEXT, resultType, dest, pointer, memory, semantics, value)
  case TypeBufferSurfaceINTEL(dest: Operand, accessQualifier: Operand)
      extends Instruction(Instruction.Definition.TypeBufferSurfaceINTEL, dest, accessQualifier)
  case TypeStructContinuedINTEL(member0TypeMember1Type: Vec[Operand])
      extends Instruction(Instruction.Definition.TypeStructContinuedINTEL, (member0TypeMember1Type)*)
  case ConstantCompositeContinuedINTEL(constituents: Vec[Operand])
      extends Instruction(Instruction.Definition.ConstantCompositeContinuedINTEL, (constituents)*)
  case SpecConstantCompositeContinuedINTEL(constituents: Vec[Operand])
      extends Instruction(Instruction.Definition.SpecConstantCompositeContinuedINTEL, (constituents)*)
  case CompositeConstructContinuedINTEL(constituents: Vec[Operand])
      extends Instruction(Instruction.Definition.CompositeConstructContinuedINTEL, (constituents)*)
  case ConvertFToBF16INTEL(resultType: Operand, dest: Operand, floatValue: Operand)
      extends Instruction(Instruction.Definition.ConvertFToBF16INTEL, resultType, dest, floatValue)
  case ConvertBF16ToFINTEL(resultType: Operand, dest: Operand, bFloat16Value: Operand)
      extends Instruction(Instruction.Definition.ConvertBF16ToFINTEL, resultType, dest, bFloat16Value)
  case ControlBarrierArriveEXT(execution: Operand, memory: Operand, semantics: Operand)
      extends Instruction(Instruction.Definition.ControlBarrierArriveEXT, execution, memory, semantics)
  case ControlBarrierWaitEXT(execution: Operand, memory: Operand, semantics: Operand)
      extends Instruction(Instruction.Definition.ControlBarrierWaitEXT, execution, memory, semantics)
  case ArithmeticFenceEXT(resultType: Operand, dest: Operand, target: Operand)
      extends Instruction(Instruction.Definition.ArithmeticFenceEXT, resultType, dest, target)
  case TaskSequenceCreateALTERA(
      resultType: Operand,
      dest: Operand,
      function: Operand,
      pipelined: Operand,
      useStallEnableClusters: Operand,
      getCapacity: Operand,
      asyncCapacity: Operand
  ) extends Instruction(
        Instruction.Definition.TaskSequenceCreateALTERA,
        resultType,
        dest,
        function,
        pipelined,
        useStallEnableClusters,
        getCapacity,
        asyncCapacity
      )
  case TaskSequenceAsyncALTERA(sequence: Operand, arguments: Vec[Operand])
      extends Instruction(Instruction.Definition.TaskSequenceAsyncALTERA, (Vec(sequence) ++ arguments)*)
  case TaskSequenceGetALTERA(resultType: Operand, dest: Operand, sequence: Operand)
      extends Instruction(Instruction.Definition.TaskSequenceGetALTERA, resultType, dest, sequence)
  case TaskSequenceReleaseALTERA(sequence: Operand)
      extends Instruction(Instruction.Definition.TaskSequenceReleaseALTERA, sequence)
  case TypeTaskSequenceALTERA(dest: Operand) extends Instruction(Instruction.Definition.TypeTaskSequenceALTERA, dest)
  case SubgroupBlockPrefetchINTEL(ptr: Operand, numBytes: Operand, mods: Option[Operand])
      extends Instruction(Instruction.Definition.SubgroupBlockPrefetchINTEL, (Vec(ptr, numBytes) ++ mods.toVec)*)
  case Subgroup2DBlockLoadINTEL(
      elementSize: Operand,
      blockWidth: Operand,
      blockHeight: Operand,
      blockCount: Operand,
      srcBasePointer: Operand,
      memoryWidth: Operand,
      memoryHeight: Operand,
      memoryPitch: Operand,
      coordinate: Operand,
      dstPointer: Operand
  ) extends Instruction(
        Instruction.Definition.Subgroup2DBlockLoadINTEL,
        elementSize,
        blockWidth,
        blockHeight,
        blockCount,
        srcBasePointer,
        memoryWidth,
        memoryHeight,
        memoryPitch,
        coordinate,
        dstPointer
      )
  case Subgroup2DBlockLoadTransformINTEL(
      elementSize: Operand,
      blockWidth: Operand,
      blockHeight: Operand,
      blockCount: Operand,
      srcBasePointer: Operand,
      memoryWidth: Operand,
      memoryHeight: Operand,
      memoryPitch: Operand,
      coordinate: Operand,
      dstPointer: Operand
  ) extends Instruction(
        Instruction.Definition.Subgroup2DBlockLoadTransformINTEL,
        elementSize,
        blockWidth,
        blockHeight,
        blockCount,
        srcBasePointer,
        memoryWidth,
        memoryHeight,
        memoryPitch,
        coordinate,
        dstPointer
      )
  case Subgroup2DBlockLoadTransposeINTEL(
      elementSize: Operand,
      blockWidth: Operand,
      blockHeight: Operand,
      blockCount: Operand,
      srcBasePointer: Operand,
      memoryWidth: Operand,
      memoryHeight: Operand,
      memoryPitch: Operand,
      coordinate: Operand,
      dstPointer: Operand
  ) extends Instruction(
        Instruction.Definition.Subgroup2DBlockLoadTransposeINTEL,
        elementSize,
        blockWidth,
        blockHeight,
        blockCount,
        srcBasePointer,
        memoryWidth,
        memoryHeight,
        memoryPitch,
        coordinate,
        dstPointer
      )
  case Subgroup2DBlockPrefetchINTEL(
      elementSize: Operand,
      blockWidth: Operand,
      blockHeight: Operand,
      blockCount: Operand,
      srcBasePointer: Operand,
      memoryWidth: Operand,
      memoryHeight: Operand,
      memoryPitch: Operand,
      coordinate: Operand
  ) extends Instruction(
        Instruction.Definition.Subgroup2DBlockPrefetchINTEL,
        elementSize,
        blockWidth,
        blockHeight,
        blockCount,
        srcBasePointer,
        memoryWidth,
        memoryHeight,
        memoryPitch,
        coordinate
      )
  case Subgroup2DBlockStoreINTEL(
      elementSize: Operand,
      blockWidth: Operand,
      blockHeight: Operand,
      blockCount: Operand,
      srcPointer: Operand,
      dstBasePointer: Operand,
      memoryWidth: Operand,
      memoryHeight: Operand,
      memoryPitch: Operand,
      coordinate: Operand
  ) extends Instruction(
        Instruction.Definition.Subgroup2DBlockStoreINTEL,
        elementSize,
        blockWidth,
        blockHeight,
        blockCount,
        srcPointer,
        dstBasePointer,
        memoryWidth,
        memoryHeight,
        memoryPitch,
        coordinate
      )
  case SubgroupMatrixMultiplyAccumulateINTEL(
      resultType: Operand,
      dest: Operand,
      kDim: Operand,
      matrixA: Operand,
      matrixB: Operand,
      matrixC: Operand,
      matrixMultiplyAccumulateOperands: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.SubgroupMatrixMultiplyAccumulateINTEL,
        (Vec(resultType, dest, kDim, matrixA, matrixB, matrixC) ++ matrixMultiplyAccumulateOperands.toVec)*
      )
  case BitwiseFunctionINTEL(resultType: Operand, dest: Operand, a: Operand, b: Operand, c: Operand, lUTIndex: Operand)
      extends Instruction(Instruction.Definition.BitwiseFunctionINTEL, resultType, dest, a, b, c, lUTIndex)
  case UntypedVariableLengthArrayINTEL(resultType: Operand, dest: Operand, elementType: Operand, length: Operand)
      extends Instruction(Instruction.Definition.UntypedVariableLengthArrayINTEL, resultType, dest, elementType, length)
  case ConditionalExtensionINTEL(pred: Operand, name: Operand)
      extends Instruction(Instruction.Definition.ConditionalExtensionINTEL, pred, name)
  case ConditionalEntryPointINTEL(
      pred: Operand,
      executionModel: Operand,
      entryPoint: Operand,
      name: Operand,
      interface: Vec[Operand]
  ) extends Instruction(
        Instruction.Definition.ConditionalEntryPointINTEL,
        (Vec(pred, executionModel, entryPoint, name) ++ interface)*
      )
  case ConditionalCapabilityINTEL(pred: Operand, capability: Operand)
      extends Instruction(Instruction.Definition.ConditionalCapabilityINTEL, pred, capability)
  case SpecConstantTargetINTEL(resultType: Operand, dest: Operand, target: Operand, features: Vec[Operand])
      extends Instruction(Instruction.Definition.SpecConstantTargetINTEL, (Vec(resultType, dest, target) ++ features)*)
  case SpecConstantArchitectureINTEL(
      resultType: Operand,
      dest: Operand,
      category: Operand,
      family: Operand,
      instructionOpcode: Operand,
      architecture: Operand
  ) extends Instruction(
        Instruction.Definition.SpecConstantArchitectureINTEL,
        resultType,
        dest,
        category,
        family,
        instructionOpcode,
        architecture
      )
  case SpecConstantCapabilitiesINTEL(resultType: Operand, dest: Operand, capabilities: Vec[Operand])
      extends Instruction(
        Instruction.Definition.SpecConstantCapabilitiesINTEL,
        (Vec(resultType, dest) ++ capabilities)*
      )
  case ConditionalCopyObjectINTEL(
      resultType: Operand,
      dest: Operand,
      condition0Operand0Condition1Operand1: Vec[Operand]
  ) extends Instruction(
        Instruction.Definition.ConditionalCopyObjectINTEL,
        (Vec(resultType, dest) ++ condition0Operand0Condition1Operand1)*
      )
  case PredicatedLoadINTEL(
      resultType: Operand,
      dest: Operand,
      pointer: Operand,
      pred: Operand,
      defaultValue: Operand,
      mods: Option[Operand]
  ) extends Instruction(
        Instruction.Definition.PredicatedLoadINTEL,
        (Vec(resultType, dest, pointer, pred, defaultValue) ++ mods.toVec)*
      )
  case PredicatedStoreINTEL(pointer: Operand, src: Operand, pred: Operand, mods: Option[Operand])
      extends Instruction(Instruction.Definition.PredicatedStoreINTEL, (Vec(pointer, src, pred) ++ mods.toVec)*)
  case GroupIMulKHR(resultType: Operand, dest: Operand, execution: Operand, operation: Operand, x: Operand)
      extends Instruction(Instruction.Definition.GroupIMulKHR, resultType, dest, execution, operation, x)
  case GroupFMulKHR(resultType: Operand, dest: Operand, execution: Operand, operation: Operand, x: Operand)
      extends Instruction(Instruction.Definition.GroupFMulKHR, resultType, dest, execution, operation, x)
  case GroupBitwiseAndKHR(resultType: Operand, dest: Operand, execution: Operand, operation: Operand, x: Operand)
      extends Instruction(Instruction.Definition.GroupBitwiseAndKHR, resultType, dest, execution, operation, x)
  case GroupBitwiseOrKHR(resultType: Operand, dest: Operand, execution: Operand, operation: Operand, x: Operand)
      extends Instruction(Instruction.Definition.GroupBitwiseOrKHR, resultType, dest, execution, operation, x)
  case GroupBitwiseXorKHR(resultType: Operand, dest: Operand, execution: Operand, operation: Operand, x: Operand)
      extends Instruction(Instruction.Definition.GroupBitwiseXorKHR, resultType, dest, execution, operation, x)
  case GroupLogicalAndKHR(resultType: Operand, dest: Operand, execution: Operand, operation: Operand, x: Operand)
      extends Instruction(Instruction.Definition.GroupLogicalAndKHR, resultType, dest, execution, operation, x)
  case GroupLogicalOrKHR(resultType: Operand, dest: Operand, execution: Operand, operation: Operand, x: Operand)
      extends Instruction(Instruction.Definition.GroupLogicalOrKHR, resultType, dest, execution, operation, x)
  case GroupLogicalXorKHR(resultType: Operand, dest: Operand, execution: Operand, operation: Operand, x: Operand)
      extends Instruction(Instruction.Definition.GroupLogicalXorKHR, resultType, dest, execution, operation, x)
  case RoundFToTF32INTEL(resultType: Operand, dest: Operand, floatValue: Operand)
      extends Instruction(Instruction.Definition.RoundFToTF32INTEL, resultType, dest, floatValue)
  case MaskedGatherINTEL(
      resultType: Operand,
      dest: Operand,
      ptrVector: Operand,
      alignment: Operand,
      mask: Operand,
      fillEmpty: Operand
  ) extends Instruction(
        Instruction.Definition.MaskedGatherINTEL,
        resultType,
        dest,
        ptrVector,
        alignment,
        mask,
        fillEmpty
      )
  case MaskedScatterINTEL(inputVector: Operand, ptrVector: Operand, alignment: Operand, mask: Operand)
      extends Instruction(Instruction.Definition.MaskedScatterINTEL, inputVector, ptrVector, alignment, mask)
  case ConvertHandleToImageINTEL(resultType: Operand, dest: Operand, src: Operand)
      extends Instruction(Instruction.Definition.ConvertHandleToImageINTEL, resultType, dest, src)
  case ConvertHandleToSamplerINTEL(resultType: Operand, dest: Operand, src: Operand)
      extends Instruction(Instruction.Definition.ConvertHandleToSamplerINTEL, resultType, dest, src)
  case ConvertHandleToSampledImageINTEL(resultType: Operand, dest: Operand, src: Operand)
      extends Instruction(Instruction.Definition.ConvertHandleToSampledImageINTEL, resultType, dest, src)
  case FDot2MixAcc32VALVE(resultType: Operand, dest: Operand, vector1: Operand, vector2: Operand, accumulator: Operand)
      extends Instruction(Instruction.Definition.FDot2MixAcc32VALVE, resultType, dest, vector1, vector2, accumulator)
  case FDot2MixAcc16VALVE(resultType: Operand, dest: Operand, vector1: Operand, vector2: Operand, accumulator: Operand)
      extends Instruction(Instruction.Definition.FDot2MixAcc16VALVE, resultType, dest, vector1, vector2, accumulator)
  case FDot4MixAcc32VALVE(resultType: Operand, dest: Operand, vector1: Operand, vector2: Operand, accumulator: Operand)
      extends Instruction(Instruction.Definition.FDot4MixAcc32VALVE, resultType, dest, vector1, vector2, accumulator)

  final val operands: Vec[Operand] = operandValues.toVec
  final def opcode: NonEmptyString = definition.opcode
  final def opcodeNumber: SPIRVOpcodeNumber = definition.opcodeNumber
  final def decoder: Instruction.Decoder = definition.decoder
  final def meta: Metadata = definition.meta
}

@javax.annotation.processing.Generated(Array("sbt codegen"))
object Instruction {
  type Decoder = RawInstruction => Either[InstructionDecodeError, Instruction]

  enum Definition(
      val opcode: NonEmptyString,
      val opcodeNumber: SPIRVOpcodeNumber,
      decode: Definition => Decoder,
      val meta: Metadata
  ) {
    case Nop extends Definition(
          NonEmptyString("OpNop"),
          SPIRVOpcodeNumber(0),
          definition => raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.Nop()),
          InstructionMetadata0.m0
        )
    case Undef extends Definition(
          NonEmptyString("OpUndef"),
          SPIRVOpcodeNumber(1),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.Undef(values.required(0), values.required(1))
              ),
          InstructionMetadata0.m1
        )
    case SourceContinued extends Definition(
          NonEmptyString("OpSourceContinued"),
          SPIRVOpcodeNumber(2),
          definition =>
            raw =>
              Instruction.decodeDefinition(definition, raw, values => Instruction.SourceContinued(values.required(0))),
          InstructionMetadata0.m2
        )
    case Source extends Definition(
          NonEmptyString("OpSource"),
          SPIRVOpcodeNumber(3),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.Source(values.required(0), values.required(1), values.optional(2), values.optional(3))
              ),
          InstructionMetadata0.m3
        )
    case SourceExtension extends Definition(
          NonEmptyString("OpSourceExtension"),
          SPIRVOpcodeNumber(4),
          definition =>
            raw =>
              Instruction.decodeDefinition(definition, raw, values => Instruction.SourceExtension(values.required(0))),
          InstructionMetadata0.m4
        )
    case Name extends Definition(
          NonEmptyString("OpName"),
          SPIRVOpcodeNumber(5),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.Name(values.required(0), values.required(1))
              ),
          InstructionMetadata0.m5
        )
    case MemberName extends Definition(
          NonEmptyString("OpMemberName"),
          SPIRVOpcodeNumber(6),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.MemberName(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m6
        )
    case String extends Definition(
          NonEmptyString("OpString"),
          SPIRVOpcodeNumber(7),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.String(values.required(0), values.required(1))
              ),
          InstructionMetadata0.m7
        )
    case Line extends Definition(
          NonEmptyString("OpLine"),
          SPIRVOpcodeNumber(8),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.Line(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m8
        )
    case Extension extends Definition(
          NonEmptyString("OpExtension"),
          SPIRVOpcodeNumber(10),
          definition =>
            raw => Instruction.decodeDefinition(definition, raw, values => Instruction.Extension(values.required(0))),
          InstructionMetadata0.m9
        )
    case ExtInstImport extends Definition(
          NonEmptyString("OpExtInstImport"),
          SPIRVOpcodeNumber(11),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ExtInstImport(values.required(0), values.required(1))
              ),
          InstructionMetadata0.m10
        )
    case ExtInst extends Definition(
          NonEmptyString("OpExtInst"),
          SPIRVOpcodeNumber(12),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ExtInst(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.repeated(4)
                  )
              ),
          InstructionMetadata0.m11
        )
    case MemoryModel extends Definition(
          NonEmptyString("OpMemoryModel"),
          SPIRVOpcodeNumber(14),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.MemoryModel(values.required(0), values.required(1))
              ),
          InstructionMetadata0.m12
        )
    case EntryPoint extends Definition(
          NonEmptyString("OpEntryPoint"),
          SPIRVOpcodeNumber(15),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.EntryPoint(values.required(0), values.required(1), values.required(2), values.repeated(3))
              ),
          InstructionMetadata0.m13
        )
    case ExecutionMode extends Definition(
          NonEmptyString("OpExecutionMode"),
          SPIRVOpcodeNumber(16),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ExecutionMode(values.required(0), values.required(1))
              ),
          InstructionMetadata0.m14
        )
    case Capability extends Definition(
          NonEmptyString("OpCapability"),
          SPIRVOpcodeNumber(17),
          definition =>
            raw => Instruction.decodeDefinition(definition, raw, values => Instruction.Capability(values.required(0))),
          InstructionMetadata0.m15
        )
    case TypeVoid extends Definition(
          NonEmptyString("OpTypeVoid"),
          SPIRVOpcodeNumber(19),
          definition =>
            raw => Instruction.decodeDefinition(definition, raw, values => Instruction.TypeVoid(values.required(0))),
          InstructionMetadata0.m16
        )
    case TypeBool extends Definition(
          NonEmptyString("OpTypeBool"),
          SPIRVOpcodeNumber(20),
          definition =>
            raw => Instruction.decodeDefinition(definition, raw, values => Instruction.TypeBool(values.required(0))),
          InstructionMetadata0.m17
        )
    case TypeInt extends Definition(
          NonEmptyString("OpTypeInt"),
          SPIRVOpcodeNumber(21),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeInt(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m18
        )
    case TypeFloat extends Definition(
          NonEmptyString("OpTypeFloat"),
          SPIRVOpcodeNumber(22),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeFloat(values.required(0), values.required(1), values.optional(2))
              ),
          InstructionMetadata0.m19
        )
    case TypeVector extends Definition(
          NonEmptyString("OpTypeVector"),
          SPIRVOpcodeNumber(23),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeVector(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m20
        )
    case TypeMatrix extends Definition(
          NonEmptyString("OpTypeMatrix"),
          SPIRVOpcodeNumber(24),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeMatrix(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m21
        )
    case TypeImage extends Definition(
          NonEmptyString("OpTypeImage"),
          SPIRVOpcodeNumber(25),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.TypeImage(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7),
                    values.optional(8)
                  )
              ),
          InstructionMetadata0.m22
        )
    case TypeSampler extends Definition(
          NonEmptyString("OpTypeSampler"),
          SPIRVOpcodeNumber(26),
          definition =>
            raw => Instruction.decodeDefinition(definition, raw, values => Instruction.TypeSampler(values.required(0))),
          InstructionMetadata0.m23
        )
    case TypeSampledImage extends Definition(
          NonEmptyString("OpTypeSampledImage"),
          SPIRVOpcodeNumber(27),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeSampledImage(values.required(0), values.required(1))
              ),
          InstructionMetadata0.m24
        )
    case TypeArray extends Definition(
          NonEmptyString("OpTypeArray"),
          SPIRVOpcodeNumber(28),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeArray(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m25
        )
    case TypeRuntimeArray extends Definition(
          NonEmptyString("OpTypeRuntimeArray"),
          SPIRVOpcodeNumber(29),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeRuntimeArray(values.required(0), values.required(1))
              ),
          InstructionMetadata0.m26
        )
    case TypeStruct extends Definition(
          NonEmptyString("OpTypeStruct"),
          SPIRVOpcodeNumber(30),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeStruct(values.required(0), values.repeated(1))
              ),
          InstructionMetadata0.m27
        )
    case TypeOpaque extends Definition(
          NonEmptyString("OpTypeOpaque"),
          SPIRVOpcodeNumber(31),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeOpaque(values.required(0), values.required(1))
              ),
          InstructionMetadata0.m28
        )
    case TypePointer extends Definition(
          NonEmptyString("OpTypePointer"),
          SPIRVOpcodeNumber(32),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypePointer(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m29
        )
    case TypeFunction extends Definition(
          NonEmptyString("OpTypeFunction"),
          SPIRVOpcodeNumber(33),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeFunction(values.required(0), values.required(1), values.repeated(2))
              ),
          InstructionMetadata0.m30
        )
    case TypeEvent extends Definition(
          NonEmptyString("OpTypeEvent"),
          SPIRVOpcodeNumber(34),
          definition =>
            raw => Instruction.decodeDefinition(definition, raw, values => Instruction.TypeEvent(values.required(0))),
          InstructionMetadata0.m31
        )
    case TypeDeviceEvent extends Definition(
          NonEmptyString("OpTypeDeviceEvent"),
          SPIRVOpcodeNumber(35),
          definition =>
            raw =>
              Instruction.decodeDefinition(definition, raw, values => Instruction.TypeDeviceEvent(values.required(0))),
          InstructionMetadata0.m32
        )
    case TypeReserveId extends Definition(
          NonEmptyString("OpTypeReserveId"),
          SPIRVOpcodeNumber(36),
          definition =>
            raw =>
              Instruction.decodeDefinition(definition, raw, values => Instruction.TypeReserveId(values.required(0))),
          InstructionMetadata0.m33
        )
    case TypeQueue extends Definition(
          NonEmptyString("OpTypeQueue"),
          SPIRVOpcodeNumber(37),
          definition =>
            raw => Instruction.decodeDefinition(definition, raw, values => Instruction.TypeQueue(values.required(0))),
          InstructionMetadata0.m34
        )
    case TypePipe extends Definition(
          NonEmptyString("OpTypePipe"),
          SPIRVOpcodeNumber(38),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypePipe(values.required(0), values.required(1))
              ),
          InstructionMetadata0.m35
        )
    case TypeForwardPointer extends Definition(
          NonEmptyString("OpTypeForwardPointer"),
          SPIRVOpcodeNumber(39),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeForwardPointer(values.required(0), values.required(1))
              ),
          InstructionMetadata0.m36
        )
    case ConstantTrue extends Definition(
          NonEmptyString("OpConstantTrue"),
          SPIRVOpcodeNumber(41),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ConstantTrue(values.required(0), values.required(1))
              ),
          InstructionMetadata0.m37
        )
    case ConstantFalse extends Definition(
          NonEmptyString("OpConstantFalse"),
          SPIRVOpcodeNumber(42),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ConstantFalse(values.required(0), values.required(1))
              ),
          InstructionMetadata0.m38
        )
    case Constant extends Definition(
          NonEmptyString("OpConstant"),
          SPIRVOpcodeNumber(43),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.Constant(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m39
        )
    case ConstantComposite extends Definition(
          NonEmptyString("OpConstantComposite"),
          SPIRVOpcodeNumber(44),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ConstantComposite(values.required(0), values.required(1), values.repeated(2))
              ),
          InstructionMetadata0.m40
        )
    case ConstantSampler extends Definition(
          NonEmptyString("OpConstantSampler"),
          SPIRVOpcodeNumber(45),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ConstantSampler(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata0.m41
        )
    case ConstantNull extends Definition(
          NonEmptyString("OpConstantNull"),
          SPIRVOpcodeNumber(46),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ConstantNull(values.required(0), values.required(1))
              ),
          InstructionMetadata0.m42
        )
    case SpecConstantTrue extends Definition(
          NonEmptyString("OpSpecConstantTrue"),
          SPIRVOpcodeNumber(48),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.SpecConstantTrue(values.required(0), values.required(1))
              ),
          InstructionMetadata0.m43
        )
    case SpecConstantFalse extends Definition(
          NonEmptyString("OpSpecConstantFalse"),
          SPIRVOpcodeNumber(49),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.SpecConstantFalse(values.required(0), values.required(1))
              ),
          InstructionMetadata0.m44
        )
    case SpecConstant extends Definition(
          NonEmptyString("OpSpecConstant"),
          SPIRVOpcodeNumber(50),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.SpecConstant(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m45
        )
    case SpecConstantComposite extends Definition(
          NonEmptyString("OpSpecConstantComposite"),
          SPIRVOpcodeNumber(51),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.SpecConstantComposite(values.required(0), values.required(1), values.repeated(2))
              ),
          InstructionMetadata0.m46
        )
    case SpecConstantOp extends Definition(
          NonEmptyString("OpSpecConstantOp"),
          SPIRVOpcodeNumber(52),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SpecConstantOp(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.repeated(3)
                  )
              ),
          InstructionMetadata0.m47
        )
    case Function extends Definition(
          NonEmptyString("OpFunction"),
          SPIRVOpcodeNumber(54),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.Function(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          InstructionMetadata0.m48
        )
    case FunctionParameter extends Definition(
          NonEmptyString("OpFunctionParameter"),
          SPIRVOpcodeNumber(55),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.FunctionParameter(values.required(0), values.required(1))
              ),
          InstructionMetadata0.m49
        )
    case FunctionEnd extends Definition(
          NonEmptyString("OpFunctionEnd"),
          SPIRVOpcodeNumber(56),
          definition => raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.FunctionEnd()),
          InstructionMetadata0.m50
        )
    case FunctionCall extends Definition(
          NonEmptyString("OpFunctionCall"),
          SPIRVOpcodeNumber(57),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FunctionCall(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.repeated(3)
                  )
              ),
          InstructionMetadata0.m51
        )
    case Variable extends Definition(
          NonEmptyString("OpVariable"),
          SPIRVOpcodeNumber(59),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.Variable(values.required(0), values.required(1), values.required(2), values.optional(3))
              ),
          InstructionMetadata0.m52
        )
    case ImageTexelPointer extends Definition(
          NonEmptyString("OpImageTexelPointer"),
          SPIRVOpcodeNumber(60),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageTexelPointer(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata0.m53
        )
    case Load extends Definition(
          NonEmptyString("OpLoad"),
          SPIRVOpcodeNumber(61),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.Load(values.required(0), values.required(1), values.required(2), values.optional(3))
              ),
          InstructionMetadata0.m54
        )
    case Store extends Definition(
          NonEmptyString("OpStore"),
          SPIRVOpcodeNumber(62),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.Store(values.required(0), values.required(1), values.optional(2))
              ),
          InstructionMetadata0.m55
        )
    case CopyMemory extends Definition(
          NonEmptyString("OpCopyMemory"),
          SPIRVOpcodeNumber(63),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CopyMemory(values.required(0), values.required(1), values.optional(2), values.optional(3))
              ),
          InstructionMetadata0.m56
        )
    case CopyMemorySized extends Definition(
          NonEmptyString("OpCopyMemorySized"),
          SPIRVOpcodeNumber(64),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CopyMemorySized(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.optional(3),
                    values.optional(4)
                  )
              ),
          InstructionMetadata0.m57
        )
    case AccessChain extends Definition(
          NonEmptyString("OpAccessChain"),
          SPIRVOpcodeNumber(65),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.AccessChain(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.repeated(3)
                  )
              ),
          InstructionMetadata0.m58
        )
    case InBoundsAccessChain extends Definition(
          NonEmptyString("OpInBoundsAccessChain"),
          SPIRVOpcodeNumber(66),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.InBoundsAccessChain(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.repeated(3)
                  )
              ),
          InstructionMetadata0.m59
        )
    case PtrAccessChain extends Definition(
          NonEmptyString("OpPtrAccessChain"),
          SPIRVOpcodeNumber(67),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.PtrAccessChain(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.repeated(4)
                  )
              ),
          InstructionMetadata0.m60
        )
    case ArrayLength extends Definition(
          NonEmptyString("OpArrayLength"),
          SPIRVOpcodeNumber(68),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArrayLength(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata0.m61
        )
    case GenericPtrMemSemantics extends Definition(
          NonEmptyString("OpGenericPtrMemSemantics"),
          SPIRVOpcodeNumber(69),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.GenericPtrMemSemantics(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m62
        )
    case InBoundsPtrAccessChain extends Definition(
          NonEmptyString("OpInBoundsPtrAccessChain"),
          SPIRVOpcodeNumber(70),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.InBoundsPtrAccessChain(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.repeated(4)
                  )
              ),
          InstructionMetadata0.m63
        )
    case Decorate extends Definition(
          NonEmptyString("OpDecorate"),
          SPIRVOpcodeNumber(71),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.Decorate(values.required(0), values.required(1))
              ),
          InstructionMetadata0.m64
        )
    case MemberDecorate extends Definition(
          NonEmptyString("OpMemberDecorate"),
          SPIRVOpcodeNumber(72),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.MemberDecorate(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m65
        )
    case DecorationGroup extends Definition(
          NonEmptyString("OpDecorationGroup"),
          SPIRVOpcodeNumber(73),
          definition =>
            raw =>
              Instruction.decodeDefinition(definition, raw, values => Instruction.DecorationGroup(values.required(0))),
          InstructionMetadata0.m66
        )
    case GroupDecorate extends Definition(
          NonEmptyString("OpGroupDecorate"),
          SPIRVOpcodeNumber(74),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.GroupDecorate(values.required(0), values.repeated(1))
              ),
          InstructionMetadata0.m67
        )
    case GroupMemberDecorate extends Definition(
          NonEmptyString("OpGroupMemberDecorate"),
          SPIRVOpcodeNumber(75),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.GroupMemberDecorate(values.required(0), values.repeated(1))
              ),
          InstructionMetadata0.m68
        )
    case VectorExtractDynamic extends Definition(
          NonEmptyString("OpVectorExtractDynamic"),
          SPIRVOpcodeNumber(77),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.VectorExtractDynamic(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata0.m69
        )
    case VectorInsertDynamic extends Definition(
          NonEmptyString("OpVectorInsertDynamic"),
          SPIRVOpcodeNumber(78),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.VectorInsertDynamic(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata0.m70
        )
    case VectorShuffle extends Definition(
          NonEmptyString("OpVectorShuffle"),
          SPIRVOpcodeNumber(79),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.VectorShuffle(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.repeated(4)
                  )
              ),
          InstructionMetadata0.m71
        )
    case CompositeConstruct extends Definition(
          NonEmptyString("OpCompositeConstruct"),
          SPIRVOpcodeNumber(80),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.CompositeConstruct(values.required(0), values.required(1), values.repeated(2))
              ),
          InstructionMetadata0.m72
        )
    case CompositeExtract extends Definition(
          NonEmptyString("OpCompositeExtract"),
          SPIRVOpcodeNumber(81),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CompositeExtract(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.repeated(3)
                  )
              ),
          InstructionMetadata0.m73
        )
    case CompositeInsert extends Definition(
          NonEmptyString("OpCompositeInsert"),
          SPIRVOpcodeNumber(82),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CompositeInsert(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.repeated(4)
                  )
              ),
          InstructionMetadata0.m74
        )
    case CopyObject extends Definition(
          NonEmptyString("OpCopyObject"),
          SPIRVOpcodeNumber(83),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.CopyObject(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m75
        )
    case Transpose extends Definition(
          NonEmptyString("OpTranspose"),
          SPIRVOpcodeNumber(84),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.Transpose(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m76
        )
    case SampledImage extends Definition(
          NonEmptyString("OpSampledImage"),
          SPIRVOpcodeNumber(86),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SampledImage(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata0.m77
        )
    case ImageSampleImplicitLod extends Definition(
          NonEmptyString("OpImageSampleImplicitLod"),
          SPIRVOpcodeNumber(87),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageSampleImplicitLod(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.optional(4)
                  )
              ),
          InstructionMetadata0.m78
        )
    case ImageSampleExplicitLod extends Definition(
          NonEmptyString("OpImageSampleExplicitLod"),
          SPIRVOpcodeNumber(88),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageSampleExplicitLod(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata0.m79
        )
    case ImageSampleDrefImplicitLod extends Definition(
          NonEmptyString("OpImageSampleDrefImplicitLod"),
          SPIRVOpcodeNumber(89),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageSampleDrefImplicitLod(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.optional(5)
                  )
              ),
          InstructionMetadata0.m80
        )
    case ImageSampleDrefExplicitLod extends Definition(
          NonEmptyString("OpImageSampleDrefExplicitLod"),
          SPIRVOpcodeNumber(90),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageSampleDrefExplicitLod(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata0.m81
        )
    case ImageSampleProjImplicitLod extends Definition(
          NonEmptyString("OpImageSampleProjImplicitLod"),
          SPIRVOpcodeNumber(91),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageSampleProjImplicitLod(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.optional(4)
                  )
              ),
          InstructionMetadata0.m82
        )
    case ImageSampleProjExplicitLod extends Definition(
          NonEmptyString("OpImageSampleProjExplicitLod"),
          SPIRVOpcodeNumber(92),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageSampleProjExplicitLod(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata0.m83
        )
    case ImageSampleProjDrefImplicitLod extends Definition(
          NonEmptyString("OpImageSampleProjDrefImplicitLod"),
          SPIRVOpcodeNumber(93),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageSampleProjDrefImplicitLod(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.optional(5)
                  )
              ),
          InstructionMetadata0.m84
        )
    case ImageSampleProjDrefExplicitLod extends Definition(
          NonEmptyString("OpImageSampleProjDrefExplicitLod"),
          SPIRVOpcodeNumber(94),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageSampleProjDrefExplicitLod(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata0.m85
        )
    case ImageFetch extends Definition(
          NonEmptyString("OpImageFetch"),
          SPIRVOpcodeNumber(95),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageFetch(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.optional(4)
                  )
              ),
          InstructionMetadata0.m86
        )
    case ImageGather extends Definition(
          NonEmptyString("OpImageGather"),
          SPIRVOpcodeNumber(96),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageGather(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.optional(5)
                  )
              ),
          InstructionMetadata0.m87
        )
    case ImageDrefGather extends Definition(
          NonEmptyString("OpImageDrefGather"),
          SPIRVOpcodeNumber(97),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageDrefGather(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.optional(5)
                  )
              ),
          InstructionMetadata0.m88
        )
    case ImageRead extends Definition(
          NonEmptyString("OpImageRead"),
          SPIRVOpcodeNumber(98),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageRead(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.optional(4)
                  )
              ),
          InstructionMetadata0.m89
        )
    case ImageWrite extends Definition(
          NonEmptyString("OpImageWrite"),
          SPIRVOpcodeNumber(99),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageWrite(values.required(0), values.required(1), values.required(2), values.optional(3))
              ),
          InstructionMetadata0.m90
        )
    case Image extends Definition(
          NonEmptyString("OpImage"),
          SPIRVOpcodeNumber(100),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.Image(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m91
        )
    case ImageQueryFormat extends Definition(
          NonEmptyString("OpImageQueryFormat"),
          SPIRVOpcodeNumber(101),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ImageQueryFormat(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m92
        )
    case ImageQueryOrder extends Definition(
          NonEmptyString("OpImageQueryOrder"),
          SPIRVOpcodeNumber(102),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ImageQueryOrder(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m93
        )
    case ImageQuerySizeLod extends Definition(
          NonEmptyString("OpImageQuerySizeLod"),
          SPIRVOpcodeNumber(103),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageQuerySizeLod(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata0.m94
        )
    case ImageQuerySize extends Definition(
          NonEmptyString("OpImageQuerySize"),
          SPIRVOpcodeNumber(104),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ImageQuerySize(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m95
        )
    case ImageQueryLod extends Definition(
          NonEmptyString("OpImageQueryLod"),
          SPIRVOpcodeNumber(105),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageQueryLod(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata0.m96
        )
    case ImageQueryLevels extends Definition(
          NonEmptyString("OpImageQueryLevels"),
          SPIRVOpcodeNumber(106),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ImageQueryLevels(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m97
        )
    case ImageQuerySamples extends Definition(
          NonEmptyString("OpImageQuerySamples"),
          SPIRVOpcodeNumber(107),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ImageQuerySamples(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m98
        )
    case ConvertFToU extends Definition(
          NonEmptyString("OpConvertFToU"),
          SPIRVOpcodeNumber(109),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ConvertFToU(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m99
        )
    case ConvertFToS extends Definition(
          NonEmptyString("OpConvertFToS"),
          SPIRVOpcodeNumber(110),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ConvertFToS(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m100
        )
    case ConvertSToF extends Definition(
          NonEmptyString("OpConvertSToF"),
          SPIRVOpcodeNumber(111),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ConvertSToF(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m101
        )
    case ConvertUToF extends Definition(
          NonEmptyString("OpConvertUToF"),
          SPIRVOpcodeNumber(112),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ConvertUToF(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m102
        )
    case UConvert extends Definition(
          NonEmptyString("OpUConvert"),
          SPIRVOpcodeNumber(113),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.UConvert(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m103
        )
    case SConvert extends Definition(
          NonEmptyString("OpSConvert"),
          SPIRVOpcodeNumber(114),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.SConvert(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m104
        )
    case FConvert extends Definition(
          NonEmptyString("OpFConvert"),
          SPIRVOpcodeNumber(115),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.FConvert(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m105
        )
    case QuantizeToF16 extends Definition(
          NonEmptyString("OpQuantizeToF16"),
          SPIRVOpcodeNumber(116),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.QuantizeToF16(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m106
        )
    case ConvertPtrToU extends Definition(
          NonEmptyString("OpConvertPtrToU"),
          SPIRVOpcodeNumber(117),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ConvertPtrToU(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m107
        )
    case SatConvertSToU extends Definition(
          NonEmptyString("OpSatConvertSToU"),
          SPIRVOpcodeNumber(118),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.SatConvertSToU(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m108
        )
    case SatConvertUToS extends Definition(
          NonEmptyString("OpSatConvertUToS"),
          SPIRVOpcodeNumber(119),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.SatConvertUToS(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m109
        )
    case ConvertUToPtr extends Definition(
          NonEmptyString("OpConvertUToPtr"),
          SPIRVOpcodeNumber(120),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ConvertUToPtr(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m110
        )
    case PtrCastToGeneric extends Definition(
          NonEmptyString("OpPtrCastToGeneric"),
          SPIRVOpcodeNumber(121),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.PtrCastToGeneric(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m111
        )
    case GenericCastToPtr extends Definition(
          NonEmptyString("OpGenericCastToPtr"),
          SPIRVOpcodeNumber(122),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.GenericCastToPtr(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m112
        )
    case GenericCastToPtrExplicit extends Definition(
          NonEmptyString("OpGenericCastToPtrExplicit"),
          SPIRVOpcodeNumber(123),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GenericCastToPtrExplicit(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata0.m113
        )
    case Bitcast extends Definition(
          NonEmptyString("OpBitcast"),
          SPIRVOpcodeNumber(124),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.Bitcast(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m114
        )
    case SNegate extends Definition(
          NonEmptyString("OpSNegate"),
          SPIRVOpcodeNumber(126),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.SNegate(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m115
        )
    case FNegate extends Definition(
          NonEmptyString("OpFNegate"),
          SPIRVOpcodeNumber(127),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.FNegate(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m116
        )
    case IAdd extends Definition(
          NonEmptyString("OpIAdd"),
          SPIRVOpcodeNumber(128),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.IAdd(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          InstructionMetadata0.m117
        )
    case FAdd extends Definition(
          NonEmptyString("OpFAdd"),
          SPIRVOpcodeNumber(129),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FAdd(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          InstructionMetadata0.m118
        )
    case ISub extends Definition(
          NonEmptyString("OpISub"),
          SPIRVOpcodeNumber(130),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ISub(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          InstructionMetadata0.m119
        )
    case FSub extends Definition(
          NonEmptyString("OpFSub"),
          SPIRVOpcodeNumber(131),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FSub(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          InstructionMetadata0.m120
        )
    case IMul extends Definition(
          NonEmptyString("OpIMul"),
          SPIRVOpcodeNumber(132),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.IMul(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          InstructionMetadata0.m121
        )
    case FMul extends Definition(
          NonEmptyString("OpFMul"),
          SPIRVOpcodeNumber(133),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FMul(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          InstructionMetadata0.m122
        )
    case UDiv extends Definition(
          NonEmptyString("OpUDiv"),
          SPIRVOpcodeNumber(134),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.UDiv(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          InstructionMetadata0.m123
        )
    case SDiv extends Definition(
          NonEmptyString("OpSDiv"),
          SPIRVOpcodeNumber(135),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SDiv(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          InstructionMetadata0.m124
        )
    case FDiv extends Definition(
          NonEmptyString("OpFDiv"),
          SPIRVOpcodeNumber(136),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FDiv(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          InstructionMetadata0.m125
        )
    case UMod extends Definition(
          NonEmptyString("OpUMod"),
          SPIRVOpcodeNumber(137),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.UMod(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          InstructionMetadata0.m126
        )
    case SRem extends Definition(
          NonEmptyString("OpSRem"),
          SPIRVOpcodeNumber(138),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SRem(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          InstructionMetadata0.m127
        )
    case SMod extends Definition(
          NonEmptyString("OpSMod"),
          SPIRVOpcodeNumber(139),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SMod(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          InstructionMetadata0.m128
        )
    case FRem extends Definition(
          NonEmptyString("OpFRem"),
          SPIRVOpcodeNumber(140),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FRem(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          InstructionMetadata0.m129
        )
    case FMod extends Definition(
          NonEmptyString("OpFMod"),
          SPIRVOpcodeNumber(141),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FMod(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          InstructionMetadata0.m130
        )
    case VectorTimesScalar extends Definition(
          NonEmptyString("OpVectorTimesScalar"),
          SPIRVOpcodeNumber(142),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.VectorTimesScalar(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata0.m131
        )
    case MatrixTimesScalar extends Definition(
          NonEmptyString("OpMatrixTimesScalar"),
          SPIRVOpcodeNumber(143),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.MatrixTimesScalar(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata0.m132
        )
    case VectorTimesMatrix extends Definition(
          NonEmptyString("OpVectorTimesMatrix"),
          SPIRVOpcodeNumber(144),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.VectorTimesMatrix(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata0.m133
        )
    case MatrixTimesVector extends Definition(
          NonEmptyString("OpMatrixTimesVector"),
          SPIRVOpcodeNumber(145),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.MatrixTimesVector(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata0.m134
        )
    case MatrixTimesMatrix extends Definition(
          NonEmptyString("OpMatrixTimesMatrix"),
          SPIRVOpcodeNumber(146),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.MatrixTimesMatrix(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata0.m135
        )
    case OuterProduct extends Definition(
          NonEmptyString("OpOuterProduct"),
          SPIRVOpcodeNumber(147),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.OuterProduct(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata0.m136
        )
    case Dot extends Definition(
          NonEmptyString("OpDot"),
          SPIRVOpcodeNumber(148),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.Dot(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          InstructionMetadata0.m137
        )
    case IAddCarry extends Definition(
          NonEmptyString("OpIAddCarry"),
          SPIRVOpcodeNumber(149),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.IAddCarry(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          InstructionMetadata0.m138
        )
    case ISubBorrow extends Definition(
          NonEmptyString("OpISubBorrow"),
          SPIRVOpcodeNumber(150),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ISubBorrow(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          InstructionMetadata0.m139
        )
    case UMulExtended extends Definition(
          NonEmptyString("OpUMulExtended"),
          SPIRVOpcodeNumber(151),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.UMulExtended(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata0.m140
        )
    case SMulExtended extends Definition(
          NonEmptyString("OpSMulExtended"),
          SPIRVOpcodeNumber(152),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SMulExtended(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata0.m141
        )
    case Any extends Definition(
          NonEmptyString("OpAny"),
          SPIRVOpcodeNumber(154),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.Any(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m142
        )
    case All extends Definition(
          NonEmptyString("OpAll"),
          SPIRVOpcodeNumber(155),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.All(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m143
        )
    case IsNan extends Definition(
          NonEmptyString("OpIsNan"),
          SPIRVOpcodeNumber(156),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.IsNan(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m144
        )
    case IsInf extends Definition(
          NonEmptyString("OpIsInf"),
          SPIRVOpcodeNumber(157),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.IsInf(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m145
        )
    case IsFinite extends Definition(
          NonEmptyString("OpIsFinite"),
          SPIRVOpcodeNumber(158),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.IsFinite(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m146
        )
    case IsNormal extends Definition(
          NonEmptyString("OpIsNormal"),
          SPIRVOpcodeNumber(159),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.IsNormal(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m147
        )
    case SignBitSet extends Definition(
          NonEmptyString("OpSignBitSet"),
          SPIRVOpcodeNumber(160),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.SignBitSet(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m148
        )
    case LessOrGreater extends Definition(
          NonEmptyString("OpLessOrGreater"),
          SPIRVOpcodeNumber(161),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.LessOrGreater(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata0.m149
        )
    case Ordered extends Definition(
          NonEmptyString("OpOrdered"),
          SPIRVOpcodeNumber(162),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.Ordered(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          InstructionMetadata0.m150
        )
    case Unordered extends Definition(
          NonEmptyString("OpUnordered"),
          SPIRVOpcodeNumber(163),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.Unordered(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          InstructionMetadata0.m151
        )
    case LogicalEqual extends Definition(
          NonEmptyString("OpLogicalEqual"),
          SPIRVOpcodeNumber(164),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.LogicalEqual(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata0.m152
        )
    case LogicalNotEqual extends Definition(
          NonEmptyString("OpLogicalNotEqual"),
          SPIRVOpcodeNumber(165),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.LogicalNotEqual(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata0.m153
        )
    case LogicalOr extends Definition(
          NonEmptyString("OpLogicalOr"),
          SPIRVOpcodeNumber(166),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.LogicalOr(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          InstructionMetadata0.m154
        )
    case LogicalAnd extends Definition(
          NonEmptyString("OpLogicalAnd"),
          SPIRVOpcodeNumber(167),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.LogicalAnd(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          InstructionMetadata0.m155
        )
    case LogicalNot extends Definition(
          NonEmptyString("OpLogicalNot"),
          SPIRVOpcodeNumber(168),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.LogicalNot(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m156
        )
    case Select extends Definition(
          NonEmptyString("OpSelect"),
          SPIRVOpcodeNumber(169),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.Select(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata0.m157
        )
    case IEqual extends Definition(
          NonEmptyString("OpIEqual"),
          SPIRVOpcodeNumber(170),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.IEqual(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          InstructionMetadata0.m158
        )
    case INotEqual extends Definition(
          NonEmptyString("OpINotEqual"),
          SPIRVOpcodeNumber(171),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.INotEqual(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          InstructionMetadata0.m159
        )
    case UGreaterThan extends Definition(
          NonEmptyString("OpUGreaterThan"),
          SPIRVOpcodeNumber(172),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.UGreaterThan(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata0.m160
        )
    case SGreaterThan extends Definition(
          NonEmptyString("OpSGreaterThan"),
          SPIRVOpcodeNumber(173),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SGreaterThan(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata0.m161
        )
    case UGreaterThanEqual extends Definition(
          NonEmptyString("OpUGreaterThanEqual"),
          SPIRVOpcodeNumber(174),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.UGreaterThanEqual(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata0.m162
        )
    case SGreaterThanEqual extends Definition(
          NonEmptyString("OpSGreaterThanEqual"),
          SPIRVOpcodeNumber(175),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SGreaterThanEqual(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata0.m163
        )
    case ULessThan extends Definition(
          NonEmptyString("OpULessThan"),
          SPIRVOpcodeNumber(176),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ULessThan(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          InstructionMetadata0.m164
        )
    case SLessThan extends Definition(
          NonEmptyString("OpSLessThan"),
          SPIRVOpcodeNumber(177),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SLessThan(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          InstructionMetadata0.m165
        )
    case ULessThanEqual extends Definition(
          NonEmptyString("OpULessThanEqual"),
          SPIRVOpcodeNumber(178),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ULessThanEqual(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata0.m166
        )
    case SLessThanEqual extends Definition(
          NonEmptyString("OpSLessThanEqual"),
          SPIRVOpcodeNumber(179),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SLessThanEqual(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata0.m167
        )
    case FOrdEqual extends Definition(
          NonEmptyString("OpFOrdEqual"),
          SPIRVOpcodeNumber(180),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FOrdEqual(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          InstructionMetadata0.m168
        )
    case FUnordEqual extends Definition(
          NonEmptyString("OpFUnordEqual"),
          SPIRVOpcodeNumber(181),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FUnordEqual(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata0.m169
        )
    case FOrdNotEqual extends Definition(
          NonEmptyString("OpFOrdNotEqual"),
          SPIRVOpcodeNumber(182),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FOrdNotEqual(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata0.m170
        )
    case FUnordNotEqual extends Definition(
          NonEmptyString("OpFUnordNotEqual"),
          SPIRVOpcodeNumber(183),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FUnordNotEqual(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata0.m171
        )
    case FOrdLessThan extends Definition(
          NonEmptyString("OpFOrdLessThan"),
          SPIRVOpcodeNumber(184),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FOrdLessThan(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata0.m172
        )
    case FUnordLessThan extends Definition(
          NonEmptyString("OpFUnordLessThan"),
          SPIRVOpcodeNumber(185),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FUnordLessThan(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata0.m173
        )
    case FOrdGreaterThan extends Definition(
          NonEmptyString("OpFOrdGreaterThan"),
          SPIRVOpcodeNumber(186),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FOrdGreaterThan(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata0.m174
        )
    case FUnordGreaterThan extends Definition(
          NonEmptyString("OpFUnordGreaterThan"),
          SPIRVOpcodeNumber(187),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FUnordGreaterThan(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata0.m175
        )
    case FOrdLessThanEqual extends Definition(
          NonEmptyString("OpFOrdLessThanEqual"),
          SPIRVOpcodeNumber(188),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FOrdLessThanEqual(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata0.m176
        )
    case FUnordLessThanEqual extends Definition(
          NonEmptyString("OpFUnordLessThanEqual"),
          SPIRVOpcodeNumber(189),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FUnordLessThanEqual(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata0.m177
        )
    case FOrdGreaterThanEqual extends Definition(
          NonEmptyString("OpFOrdGreaterThanEqual"),
          SPIRVOpcodeNumber(190),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FOrdGreaterThanEqual(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata0.m178
        )
    case FUnordGreaterThanEqual extends Definition(
          NonEmptyString("OpFUnordGreaterThanEqual"),
          SPIRVOpcodeNumber(191),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FUnordGreaterThanEqual(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata0.m179
        )
    case ShiftRightLogical extends Definition(
          NonEmptyString("OpShiftRightLogical"),
          SPIRVOpcodeNumber(194),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ShiftRightLogical(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata0.m180
        )
    case ShiftRightArithmetic extends Definition(
          NonEmptyString("OpShiftRightArithmetic"),
          SPIRVOpcodeNumber(195),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ShiftRightArithmetic(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata0.m181
        )
    case ShiftLeftLogical extends Definition(
          NonEmptyString("OpShiftLeftLogical"),
          SPIRVOpcodeNumber(196),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ShiftLeftLogical(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata0.m182
        )
    case BitwiseOr extends Definition(
          NonEmptyString("OpBitwiseOr"),
          SPIRVOpcodeNumber(197),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.BitwiseOr(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          InstructionMetadata0.m183
        )
    case BitwiseXor extends Definition(
          NonEmptyString("OpBitwiseXor"),
          SPIRVOpcodeNumber(198),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.BitwiseXor(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          InstructionMetadata0.m184
        )
    case BitwiseAnd extends Definition(
          NonEmptyString("OpBitwiseAnd"),
          SPIRVOpcodeNumber(199),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.BitwiseAnd(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          InstructionMetadata0.m185
        )
    case Not extends Definition(
          NonEmptyString("OpNot"),
          SPIRVOpcodeNumber(200),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.Not(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m186
        )
    case BitFieldInsert extends Definition(
          NonEmptyString("OpBitFieldInsert"),
          SPIRVOpcodeNumber(201),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.BitFieldInsert(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata0.m187
        )
    case BitFieldSExtract extends Definition(
          NonEmptyString("OpBitFieldSExtract"),
          SPIRVOpcodeNumber(202),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.BitFieldSExtract(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata0.m188
        )
    case BitFieldUExtract extends Definition(
          NonEmptyString("OpBitFieldUExtract"),
          SPIRVOpcodeNumber(203),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.BitFieldUExtract(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata0.m189
        )
    case BitReverse extends Definition(
          NonEmptyString("OpBitReverse"),
          SPIRVOpcodeNumber(204),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.BitReverse(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m190
        )
    case BitCount extends Definition(
          NonEmptyString("OpBitCount"),
          SPIRVOpcodeNumber(205),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.BitCount(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m191
        )
    case DPdx extends Definition(
          NonEmptyString("OpDPdx"),
          SPIRVOpcodeNumber(207),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.DPdx(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m192
        )
    case DPdy extends Definition(
          NonEmptyString("OpDPdy"),
          SPIRVOpcodeNumber(208),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.DPdy(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m193
        )
    case Fwidth extends Definition(
          NonEmptyString("OpFwidth"),
          SPIRVOpcodeNumber(209),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.Fwidth(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m194
        )
    case DPdxFine extends Definition(
          NonEmptyString("OpDPdxFine"),
          SPIRVOpcodeNumber(210),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.DPdxFine(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m195
        )
    case DPdyFine extends Definition(
          NonEmptyString("OpDPdyFine"),
          SPIRVOpcodeNumber(211),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.DPdyFine(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m196
        )
    case FwidthFine extends Definition(
          NonEmptyString("OpFwidthFine"),
          SPIRVOpcodeNumber(212),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.FwidthFine(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m197
        )
    case DPdxCoarse extends Definition(
          NonEmptyString("OpDPdxCoarse"),
          SPIRVOpcodeNumber(213),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.DPdxCoarse(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m198
        )
    case DPdyCoarse extends Definition(
          NonEmptyString("OpDPdyCoarse"),
          SPIRVOpcodeNumber(214),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.DPdyCoarse(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata0.m199
        )
    case FwidthCoarse extends Definition(
          NonEmptyString("OpFwidthCoarse"),
          SPIRVOpcodeNumber(215),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.FwidthCoarse(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata1.m0
        )
    case EmitVertex extends Definition(
          NonEmptyString("OpEmitVertex"),
          SPIRVOpcodeNumber(218),
          definition => raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.EmitVertex()),
          InstructionMetadata1.m1
        )
    case EndPrimitive extends Definition(
          NonEmptyString("OpEndPrimitive"),
          SPIRVOpcodeNumber(219),
          definition => raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.EndPrimitive()),
          InstructionMetadata1.m2
        )
    case EmitStreamVertex extends Definition(
          NonEmptyString("OpEmitStreamVertex"),
          SPIRVOpcodeNumber(220),
          definition =>
            raw =>
              Instruction.decodeDefinition(definition, raw, values => Instruction.EmitStreamVertex(values.required(0))),
          InstructionMetadata1.m3
        )
    case EndStreamPrimitive extends Definition(
          NonEmptyString("OpEndStreamPrimitive"),
          SPIRVOpcodeNumber(221),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.EndStreamPrimitive(values.required(0))
              ),
          InstructionMetadata1.m4
        )
    case ControlBarrier extends Definition(
          NonEmptyString("OpControlBarrier"),
          SPIRVOpcodeNumber(224),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ControlBarrier(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata1.m5
        )
    case MemoryBarrier extends Definition(
          NonEmptyString("OpMemoryBarrier"),
          SPIRVOpcodeNumber(225),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.MemoryBarrier(values.required(0), values.required(1))
              ),
          InstructionMetadata1.m6
        )
    case AtomicLoad extends Definition(
          NonEmptyString("OpAtomicLoad"),
          SPIRVOpcodeNumber(227),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.AtomicLoad(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata1.m7
        )
    case AtomicStore extends Definition(
          NonEmptyString("OpAtomicStore"),
          SPIRVOpcodeNumber(228),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.AtomicStore(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata1.m8
        )
    case AtomicExchange extends Definition(
          NonEmptyString("OpAtomicExchange"),
          SPIRVOpcodeNumber(229),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.AtomicExchange(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata1.m9
        )
    case AtomicCompareExchange extends Definition(
          NonEmptyString("OpAtomicCompareExchange"),
          SPIRVOpcodeNumber(230),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.AtomicCompareExchange(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7)
                  )
              ),
          InstructionMetadata1.m10
        )
    case AtomicCompareExchangeWeak extends Definition(
          NonEmptyString("OpAtomicCompareExchangeWeak"),
          SPIRVOpcodeNumber(231),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.AtomicCompareExchangeWeak(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7)
                  )
              ),
          InstructionMetadata1.m11
        )
    case AtomicIIncrement extends Definition(
          NonEmptyString("OpAtomicIIncrement"),
          SPIRVOpcodeNumber(232),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.AtomicIIncrement(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata1.m12
        )
    case AtomicIDecrement extends Definition(
          NonEmptyString("OpAtomicIDecrement"),
          SPIRVOpcodeNumber(233),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.AtomicIDecrement(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata1.m13
        )
    case AtomicIAdd extends Definition(
          NonEmptyString("OpAtomicIAdd"),
          SPIRVOpcodeNumber(234),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.AtomicIAdd(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata1.m14
        )
    case AtomicISub extends Definition(
          NonEmptyString("OpAtomicISub"),
          SPIRVOpcodeNumber(235),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.AtomicISub(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata1.m15
        )
    case AtomicSMin extends Definition(
          NonEmptyString("OpAtomicSMin"),
          SPIRVOpcodeNumber(236),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.AtomicSMin(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata1.m16
        )
    case AtomicUMin extends Definition(
          NonEmptyString("OpAtomicUMin"),
          SPIRVOpcodeNumber(237),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.AtomicUMin(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata1.m17
        )
    case AtomicSMax extends Definition(
          NonEmptyString("OpAtomicSMax"),
          SPIRVOpcodeNumber(238),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.AtomicSMax(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata1.m18
        )
    case AtomicUMax extends Definition(
          NonEmptyString("OpAtomicUMax"),
          SPIRVOpcodeNumber(239),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.AtomicUMax(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata1.m19
        )
    case AtomicAnd extends Definition(
          NonEmptyString("OpAtomicAnd"),
          SPIRVOpcodeNumber(240),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.AtomicAnd(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata1.m20
        )
    case AtomicOr extends Definition(
          NonEmptyString("OpAtomicOr"),
          SPIRVOpcodeNumber(241),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.AtomicOr(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata1.m21
        )
    case AtomicXor extends Definition(
          NonEmptyString("OpAtomicXor"),
          SPIRVOpcodeNumber(242),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.AtomicXor(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata1.m22
        )
    case Phi extends Definition(
          NonEmptyString("OpPhi"),
          SPIRVOpcodeNumber(245),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.Phi(values.required(0), values.required(1), values.repeated(2))
              ),
          InstructionMetadata1.m23
        )
    case LoopMerge extends Definition(
          NonEmptyString("OpLoopMerge"),
          SPIRVOpcodeNumber(246),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.LoopMerge(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata1.m24
        )
    case SelectionMerge extends Definition(
          NonEmptyString("OpSelectionMerge"),
          SPIRVOpcodeNumber(247),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.SelectionMerge(values.required(0), values.required(1))
              ),
          InstructionMetadata1.m25
        )
    case Label extends Definition(
          NonEmptyString("OpLabel"),
          SPIRVOpcodeNumber(248),
          definition =>
            raw => Instruction.decodeDefinition(definition, raw, values => Instruction.Label(values.required(0))),
          InstructionMetadata1.m26
        )
    case Branch extends Definition(
          NonEmptyString("OpBranch"),
          SPIRVOpcodeNumber(249),
          definition =>
            raw => Instruction.decodeDefinition(definition, raw, values => Instruction.Branch(values.required(0))),
          InstructionMetadata1.m27
        )
    case BranchConditional extends Definition(
          NonEmptyString("OpBranchConditional"),
          SPIRVOpcodeNumber(250),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.BranchConditional(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.repeated(3)
                  )
              ),
          InstructionMetadata1.m28
        )
    case Switch extends Definition(
          NonEmptyString("OpSwitch"),
          SPIRVOpcodeNumber(251),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.Switch(values.required(0), values.required(1), values.repeated(2))
              ),
          InstructionMetadata1.m29
        )
    case Kill extends Definition(
          NonEmptyString("OpKill"),
          SPIRVOpcodeNumber(252),
          definition => raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.Kill()),
          InstructionMetadata1.m30
        )
    case Return extends Definition(
          NonEmptyString("OpReturn"),
          SPIRVOpcodeNumber(253),
          definition => raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.Return()),
          InstructionMetadata1.m31
        )
    case ReturnValue extends Definition(
          NonEmptyString("OpReturnValue"),
          SPIRVOpcodeNumber(254),
          definition =>
            raw => Instruction.decodeDefinition(definition, raw, values => Instruction.ReturnValue(values.required(0))),
          InstructionMetadata1.m32
        )
    case Unreachable extends Definition(
          NonEmptyString("OpUnreachable"),
          SPIRVOpcodeNumber(255),
          definition => raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.Unreachable()),
          InstructionMetadata1.m33
        )
    case LifetimeStart extends Definition(
          NonEmptyString("OpLifetimeStart"),
          SPIRVOpcodeNumber(256),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.LifetimeStart(values.required(0), values.required(1))
              ),
          InstructionMetadata1.m34
        )
    case LifetimeStop extends Definition(
          NonEmptyString("OpLifetimeStop"),
          SPIRVOpcodeNumber(257),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.LifetimeStop(values.required(0), values.required(1))
              ),
          InstructionMetadata1.m35
        )
    case GroupAsyncCopy extends Definition(
          NonEmptyString("OpGroupAsyncCopy"),
          SPIRVOpcodeNumber(259),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupAsyncCopy(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7)
                  )
              ),
          InstructionMetadata1.m36
        )
    case GroupWaitEvents extends Definition(
          NonEmptyString("OpGroupWaitEvents"),
          SPIRVOpcodeNumber(260),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.GroupWaitEvents(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata1.m37
        )
    case GroupAll extends Definition(
          NonEmptyString("OpGroupAll"),
          SPIRVOpcodeNumber(261),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupAll(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          InstructionMetadata1.m38
        )
    case GroupAny extends Definition(
          NonEmptyString("OpGroupAny"),
          SPIRVOpcodeNumber(262),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupAny(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          InstructionMetadata1.m39
        )
    case GroupBroadcast extends Definition(
          NonEmptyString("OpGroupBroadcast"),
          SPIRVOpcodeNumber(263),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupBroadcast(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata1.m40
        )
    case GroupIAdd extends Definition(
          NonEmptyString("OpGroupIAdd"),
          SPIRVOpcodeNumber(264),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupIAdd(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata1.m41
        )
    case GroupFAdd extends Definition(
          NonEmptyString("OpGroupFAdd"),
          SPIRVOpcodeNumber(265),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupFAdd(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata1.m42
        )
    case GroupFMin extends Definition(
          NonEmptyString("OpGroupFMin"),
          SPIRVOpcodeNumber(266),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupFMin(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata1.m43
        )
    case GroupUMin extends Definition(
          NonEmptyString("OpGroupUMin"),
          SPIRVOpcodeNumber(267),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupUMin(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata1.m44
        )
    case GroupSMin extends Definition(
          NonEmptyString("OpGroupSMin"),
          SPIRVOpcodeNumber(268),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupSMin(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata1.m45
        )
    case GroupFMax extends Definition(
          NonEmptyString("OpGroupFMax"),
          SPIRVOpcodeNumber(269),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupFMax(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata1.m46
        )
    case GroupUMax extends Definition(
          NonEmptyString("OpGroupUMax"),
          SPIRVOpcodeNumber(270),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupUMax(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata1.m47
        )
    case GroupSMax extends Definition(
          NonEmptyString("OpGroupSMax"),
          SPIRVOpcodeNumber(271),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupSMax(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata1.m48
        )
    case ReadPipe extends Definition(
          NonEmptyString("OpReadPipe"),
          SPIRVOpcodeNumber(274),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ReadPipe(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata1.m49
        )
    case WritePipe extends Definition(
          NonEmptyString("OpWritePipe"),
          SPIRVOpcodeNumber(275),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.WritePipe(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata1.m50
        )
    case ReservedReadPipe extends Definition(
          NonEmptyString("OpReservedReadPipe"),
          SPIRVOpcodeNumber(276),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ReservedReadPipe(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7)
                  )
              ),
          InstructionMetadata1.m51
        )
    case ReservedWritePipe extends Definition(
          NonEmptyString("OpReservedWritePipe"),
          SPIRVOpcodeNumber(277),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ReservedWritePipe(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7)
                  )
              ),
          InstructionMetadata1.m52
        )
    case ReserveReadPipePackets extends Definition(
          NonEmptyString("OpReserveReadPipePackets"),
          SPIRVOpcodeNumber(278),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ReserveReadPipePackets(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata1.m53
        )
    case ReserveWritePipePackets extends Definition(
          NonEmptyString("OpReserveWritePipePackets"),
          SPIRVOpcodeNumber(279),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ReserveWritePipePackets(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata1.m54
        )
    case CommitReadPipe extends Definition(
          NonEmptyString("OpCommitReadPipe"),
          SPIRVOpcodeNumber(280),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CommitReadPipe(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata1.m55
        )
    case CommitWritePipe extends Definition(
          NonEmptyString("OpCommitWritePipe"),
          SPIRVOpcodeNumber(281),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CommitWritePipe(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata1.m56
        )
    case IsValidReserveId extends Definition(
          NonEmptyString("OpIsValidReserveId"),
          SPIRVOpcodeNumber(282),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.IsValidReserveId(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata1.m57
        )
    case GetNumPipePackets extends Definition(
          NonEmptyString("OpGetNumPipePackets"),
          SPIRVOpcodeNumber(283),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GetNumPipePackets(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata1.m58
        )
    case GetMaxPipePackets extends Definition(
          NonEmptyString("OpGetMaxPipePackets"),
          SPIRVOpcodeNumber(284),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GetMaxPipePackets(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata1.m59
        )
    case GroupReserveReadPipePackets extends Definition(
          NonEmptyString("OpGroupReserveReadPipePackets"),
          SPIRVOpcodeNumber(285),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupReserveReadPipePackets(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6)
                  )
              ),
          InstructionMetadata1.m60
        )
    case GroupReserveWritePipePackets extends Definition(
          NonEmptyString("OpGroupReserveWritePipePackets"),
          SPIRVOpcodeNumber(286),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupReserveWritePipePackets(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6)
                  )
              ),
          InstructionMetadata1.m61
        )
    case GroupCommitReadPipe extends Definition(
          NonEmptyString("OpGroupCommitReadPipe"),
          SPIRVOpcodeNumber(287),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupCommitReadPipe(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata1.m62
        )
    case GroupCommitWritePipe extends Definition(
          NonEmptyString("OpGroupCommitWritePipe"),
          SPIRVOpcodeNumber(288),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupCommitWritePipe(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata1.m63
        )
    case EnqueueMarker extends Definition(
          NonEmptyString("OpEnqueueMarker"),
          SPIRVOpcodeNumber(291),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.EnqueueMarker(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata1.m64
        )
    case EnqueueKernel extends Definition(
          NonEmptyString("OpEnqueueKernel"),
          SPIRVOpcodeNumber(292),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.EnqueueKernel(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7),
                    values.required(8),
                    values.required(9),
                    values.required(10),
                    values.required(11),
                    values.repeated(12)
                  )
              ),
          InstructionMetadata1.m65
        )
    case GetKernelNDrangeSubGroupCount extends Definition(
          NonEmptyString("OpGetKernelNDrangeSubGroupCount"),
          SPIRVOpcodeNumber(293),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GetKernelNDrangeSubGroupCount(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6)
                  )
              ),
          InstructionMetadata1.m66
        )
    case GetKernelNDrangeMaxSubGroupSize extends Definition(
          NonEmptyString("OpGetKernelNDrangeMaxSubGroupSize"),
          SPIRVOpcodeNumber(294),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GetKernelNDrangeMaxSubGroupSize(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6)
                  )
              ),
          InstructionMetadata1.m67
        )
    case GetKernelWorkGroupSize extends Definition(
          NonEmptyString("OpGetKernelWorkGroupSize"),
          SPIRVOpcodeNumber(295),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GetKernelWorkGroupSize(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata1.m68
        )
    case GetKernelPreferredWorkGroupSizeMultiple extends Definition(
          NonEmptyString("OpGetKernelPreferredWorkGroupSizeMultiple"),
          SPIRVOpcodeNumber(296),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GetKernelPreferredWorkGroupSizeMultiple(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata1.m69
        )
    case RetainEvent extends Definition(
          NonEmptyString("OpRetainEvent"),
          SPIRVOpcodeNumber(297),
          definition =>
            raw => Instruction.decodeDefinition(definition, raw, values => Instruction.RetainEvent(values.required(0))),
          InstructionMetadata1.m70
        )
    case ReleaseEvent extends Definition(
          NonEmptyString("OpReleaseEvent"),
          SPIRVOpcodeNumber(298),
          definition =>
            raw =>
              Instruction.decodeDefinition(definition, raw, values => Instruction.ReleaseEvent(values.required(0))),
          InstructionMetadata1.m71
        )
    case CreateUserEvent extends Definition(
          NonEmptyString("OpCreateUserEvent"),
          SPIRVOpcodeNumber(299),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.CreateUserEvent(values.required(0), values.required(1))
              ),
          InstructionMetadata1.m72
        )
    case IsValidEvent extends Definition(
          NonEmptyString("OpIsValidEvent"),
          SPIRVOpcodeNumber(300),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.IsValidEvent(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata1.m73
        )
    case SetUserEventStatus extends Definition(
          NonEmptyString("OpSetUserEventStatus"),
          SPIRVOpcodeNumber(301),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.SetUserEventStatus(values.required(0), values.required(1))
              ),
          InstructionMetadata1.m74
        )
    case CaptureEventProfilingInfo extends Definition(
          NonEmptyString("OpCaptureEventProfilingInfo"),
          SPIRVOpcodeNumber(302),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CaptureEventProfilingInfo(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata1.m75
        )
    case GetDefaultQueue extends Definition(
          NonEmptyString("OpGetDefaultQueue"),
          SPIRVOpcodeNumber(303),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.GetDefaultQueue(values.required(0), values.required(1))
              ),
          InstructionMetadata1.m76
        )
    case BuildNDRange extends Definition(
          NonEmptyString("OpBuildNDRange"),
          SPIRVOpcodeNumber(304),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.BuildNDRange(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata1.m77
        )
    case ImageSparseSampleImplicitLod extends Definition(
          NonEmptyString("OpImageSparseSampleImplicitLod"),
          SPIRVOpcodeNumber(305),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageSparseSampleImplicitLod(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.optional(4)
                  )
              ),
          InstructionMetadata1.m78
        )
    case ImageSparseSampleExplicitLod extends Definition(
          NonEmptyString("OpImageSparseSampleExplicitLod"),
          SPIRVOpcodeNumber(306),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageSparseSampleExplicitLod(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata1.m79
        )
    case ImageSparseSampleDrefImplicitLod extends Definition(
          NonEmptyString("OpImageSparseSampleDrefImplicitLod"),
          SPIRVOpcodeNumber(307),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageSparseSampleDrefImplicitLod(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.optional(5)
                  )
              ),
          InstructionMetadata1.m80
        )
    case ImageSparseSampleDrefExplicitLod extends Definition(
          NonEmptyString("OpImageSparseSampleDrefExplicitLod"),
          SPIRVOpcodeNumber(308),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageSparseSampleDrefExplicitLod(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata1.m81
        )
    case ImageSparseSampleProjImplicitLod extends Definition(
          NonEmptyString("OpImageSparseSampleProjImplicitLod"),
          SPIRVOpcodeNumber(309),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageSparseSampleProjImplicitLod(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.optional(4)
                  )
              ),
          InstructionMetadata1.m82
        )
    case ImageSparseSampleProjExplicitLod extends Definition(
          NonEmptyString("OpImageSparseSampleProjExplicitLod"),
          SPIRVOpcodeNumber(310),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageSparseSampleProjExplicitLod(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata1.m83
        )
    case ImageSparseSampleProjDrefImplicitLod extends Definition(
          NonEmptyString("OpImageSparseSampleProjDrefImplicitLod"),
          SPIRVOpcodeNumber(311),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageSparseSampleProjDrefImplicitLod(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.optional(5)
                  )
              ),
          InstructionMetadata1.m84
        )
    case ImageSparseSampleProjDrefExplicitLod extends Definition(
          NonEmptyString("OpImageSparseSampleProjDrefExplicitLod"),
          SPIRVOpcodeNumber(312),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageSparseSampleProjDrefExplicitLod(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata1.m85
        )
    case ImageSparseFetch extends Definition(
          NonEmptyString("OpImageSparseFetch"),
          SPIRVOpcodeNumber(313),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageSparseFetch(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.optional(4)
                  )
              ),
          InstructionMetadata1.m86
        )
    case ImageSparseGather extends Definition(
          NonEmptyString("OpImageSparseGather"),
          SPIRVOpcodeNumber(314),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageSparseGather(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.optional(5)
                  )
              ),
          InstructionMetadata1.m87
        )
    case ImageSparseDrefGather extends Definition(
          NonEmptyString("OpImageSparseDrefGather"),
          SPIRVOpcodeNumber(315),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageSparseDrefGather(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.optional(5)
                  )
              ),
          InstructionMetadata1.m88
        )
    case ImageSparseTexelsResident extends Definition(
          NonEmptyString("OpImageSparseTexelsResident"),
          SPIRVOpcodeNumber(316),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageSparseTexelsResident(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata1.m89
        )
    case NoLine extends Definition(
          NonEmptyString("OpNoLine"),
          SPIRVOpcodeNumber(317),
          definition => raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.NoLine()),
          InstructionMetadata1.m90
        )
    case AtomicFlagTestAndSet extends Definition(
          NonEmptyString("OpAtomicFlagTestAndSet"),
          SPIRVOpcodeNumber(318),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.AtomicFlagTestAndSet(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata1.m91
        )
    case AtomicFlagClear extends Definition(
          NonEmptyString("OpAtomicFlagClear"),
          SPIRVOpcodeNumber(319),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.AtomicFlagClear(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata1.m92
        )
    case ImageSparseRead extends Definition(
          NonEmptyString("OpImageSparseRead"),
          SPIRVOpcodeNumber(320),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageSparseRead(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.optional(4)
                  )
              ),
          InstructionMetadata1.m93
        )
    case SizeOf extends Definition(
          NonEmptyString("OpSizeOf"),
          SPIRVOpcodeNumber(321),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.SizeOf(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata1.m94
        )
    case TypePipeStorage extends Definition(
          NonEmptyString("OpTypePipeStorage"),
          SPIRVOpcodeNumber(322),
          definition =>
            raw =>
              Instruction.decodeDefinition(definition, raw, values => Instruction.TypePipeStorage(values.required(0))),
          InstructionMetadata1.m95
        )
    case ConstantPipeStorage extends Definition(
          NonEmptyString("OpConstantPipeStorage"),
          SPIRVOpcodeNumber(323),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ConstantPipeStorage(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata1.m96
        )
    case CreatePipeFromPipeStorage extends Definition(
          NonEmptyString("OpCreatePipeFromPipeStorage"),
          SPIRVOpcodeNumber(324),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CreatePipeFromPipeStorage(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata1.m97
        )
    case GetKernelLocalSizeForSubgroupCount extends Definition(
          NonEmptyString("OpGetKernelLocalSizeForSubgroupCount"),
          SPIRVOpcodeNumber(325),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GetKernelLocalSizeForSubgroupCount(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6)
                  )
              ),
          InstructionMetadata1.m98
        )
    case GetKernelMaxNumSubgroups extends Definition(
          NonEmptyString("OpGetKernelMaxNumSubgroups"),
          SPIRVOpcodeNumber(326),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GetKernelMaxNumSubgroups(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata1.m99
        )
    case TypeNamedBarrier extends Definition(
          NonEmptyString("OpTypeNamedBarrier"),
          SPIRVOpcodeNumber(327),
          definition =>
            raw =>
              Instruction.decodeDefinition(definition, raw, values => Instruction.TypeNamedBarrier(values.required(0))),
          InstructionMetadata1.m100
        )
    case NamedBarrierInitialize extends Definition(
          NonEmptyString("OpNamedBarrierInitialize"),
          SPIRVOpcodeNumber(328),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.NamedBarrierInitialize(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata1.m101
        )
    case MemoryNamedBarrier extends Definition(
          NonEmptyString("OpMemoryNamedBarrier"),
          SPIRVOpcodeNumber(329),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.MemoryNamedBarrier(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata1.m102
        )
    case ModuleProcessed extends Definition(
          NonEmptyString("OpModuleProcessed"),
          SPIRVOpcodeNumber(330),
          definition =>
            raw =>
              Instruction.decodeDefinition(definition, raw, values => Instruction.ModuleProcessed(values.required(0))),
          InstructionMetadata1.m103
        )
    case ExecutionModeId extends Definition(
          NonEmptyString("OpExecutionModeId"),
          SPIRVOpcodeNumber(331),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ExecutionModeId(values.required(0), values.required(1))
              ),
          InstructionMetadata1.m104
        )
    case DecorateId extends Definition(
          NonEmptyString("OpDecorateId"),
          SPIRVOpcodeNumber(332),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.DecorateId(values.required(0), values.required(1))
              ),
          InstructionMetadata1.m105
        )
    case GroupNonUniformElect extends Definition(
          NonEmptyString("OpGroupNonUniformElect"),
          SPIRVOpcodeNumber(333),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.GroupNonUniformElect(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata1.m106
        )
    case GroupNonUniformAll extends Definition(
          NonEmptyString("OpGroupNonUniformAll"),
          SPIRVOpcodeNumber(334),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformAll(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata1.m107
        )
    case GroupNonUniformAny extends Definition(
          NonEmptyString("OpGroupNonUniformAny"),
          SPIRVOpcodeNumber(335),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformAny(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata1.m108
        )
    case GroupNonUniformAllEqual extends Definition(
          NonEmptyString("OpGroupNonUniformAllEqual"),
          SPIRVOpcodeNumber(336),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformAllEqual(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata1.m109
        )
    case GroupNonUniformBroadcast extends Definition(
          NonEmptyString("OpGroupNonUniformBroadcast"),
          SPIRVOpcodeNumber(337),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformBroadcast(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata1.m110
        )
    case GroupNonUniformBroadcastFirst extends Definition(
          NonEmptyString("OpGroupNonUniformBroadcastFirst"),
          SPIRVOpcodeNumber(338),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformBroadcastFirst(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata1.m111
        )
    case GroupNonUniformBallot extends Definition(
          NonEmptyString("OpGroupNonUniformBallot"),
          SPIRVOpcodeNumber(339),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformBallot(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata1.m112
        )
    case GroupNonUniformInverseBallot extends Definition(
          NonEmptyString("OpGroupNonUniformInverseBallot"),
          SPIRVOpcodeNumber(340),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformInverseBallot(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata1.m113
        )
    case GroupNonUniformBallotBitExtract extends Definition(
          NonEmptyString("OpGroupNonUniformBallotBitExtract"),
          SPIRVOpcodeNumber(341),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformBallotBitExtract(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata1.m114
        )
    case GroupNonUniformBallotBitCount extends Definition(
          NonEmptyString("OpGroupNonUniformBallotBitCount"),
          SPIRVOpcodeNumber(342),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformBallotBitCount(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata1.m115
        )
    case GroupNonUniformBallotFindLSB extends Definition(
          NonEmptyString("OpGroupNonUniformBallotFindLSB"),
          SPIRVOpcodeNumber(343),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformBallotFindLSB(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata1.m116
        )
    case GroupNonUniformBallotFindMSB extends Definition(
          NonEmptyString("OpGroupNonUniformBallotFindMSB"),
          SPIRVOpcodeNumber(344),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformBallotFindMSB(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata1.m117
        )
    case GroupNonUniformShuffle extends Definition(
          NonEmptyString("OpGroupNonUniformShuffle"),
          SPIRVOpcodeNumber(345),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformShuffle(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata1.m118
        )
    case GroupNonUniformShuffleXor extends Definition(
          NonEmptyString("OpGroupNonUniformShuffleXor"),
          SPIRVOpcodeNumber(346),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformShuffleXor(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata1.m119
        )
    case GroupNonUniformShuffleUp extends Definition(
          NonEmptyString("OpGroupNonUniformShuffleUp"),
          SPIRVOpcodeNumber(347),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformShuffleUp(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata1.m120
        )
    case GroupNonUniformShuffleDown extends Definition(
          NonEmptyString("OpGroupNonUniformShuffleDown"),
          SPIRVOpcodeNumber(348),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformShuffleDown(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata1.m121
        )
    case GroupNonUniformIAdd extends Definition(
          NonEmptyString("OpGroupNonUniformIAdd"),
          SPIRVOpcodeNumber(349),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformIAdd(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.optional(5)
                  )
              ),
          InstructionMetadata1.m122
        )
    case GroupNonUniformFAdd extends Definition(
          NonEmptyString("OpGroupNonUniformFAdd"),
          SPIRVOpcodeNumber(350),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformFAdd(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.optional(5)
                  )
              ),
          InstructionMetadata1.m123
        )
    case GroupNonUniformIMul extends Definition(
          NonEmptyString("OpGroupNonUniformIMul"),
          SPIRVOpcodeNumber(351),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformIMul(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.optional(5)
                  )
              ),
          InstructionMetadata1.m124
        )
    case GroupNonUniformFMul extends Definition(
          NonEmptyString("OpGroupNonUniformFMul"),
          SPIRVOpcodeNumber(352),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformFMul(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.optional(5)
                  )
              ),
          InstructionMetadata1.m125
        )
    case GroupNonUniformSMin extends Definition(
          NonEmptyString("OpGroupNonUniformSMin"),
          SPIRVOpcodeNumber(353),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformSMin(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.optional(5)
                  )
              ),
          InstructionMetadata1.m126
        )
    case GroupNonUniformUMin extends Definition(
          NonEmptyString("OpGroupNonUniformUMin"),
          SPIRVOpcodeNumber(354),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformUMin(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.optional(5)
                  )
              ),
          InstructionMetadata1.m127
        )
    case GroupNonUniformFMin extends Definition(
          NonEmptyString("OpGroupNonUniformFMin"),
          SPIRVOpcodeNumber(355),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformFMin(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.optional(5)
                  )
              ),
          InstructionMetadata1.m128
        )
    case GroupNonUniformSMax extends Definition(
          NonEmptyString("OpGroupNonUniformSMax"),
          SPIRVOpcodeNumber(356),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformSMax(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.optional(5)
                  )
              ),
          InstructionMetadata1.m129
        )
    case GroupNonUniformUMax extends Definition(
          NonEmptyString("OpGroupNonUniformUMax"),
          SPIRVOpcodeNumber(357),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformUMax(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.optional(5)
                  )
              ),
          InstructionMetadata1.m130
        )
    case GroupNonUniformFMax extends Definition(
          NonEmptyString("OpGroupNonUniformFMax"),
          SPIRVOpcodeNumber(358),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformFMax(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.optional(5)
                  )
              ),
          InstructionMetadata1.m131
        )
    case GroupNonUniformBitwiseAnd extends Definition(
          NonEmptyString("OpGroupNonUniformBitwiseAnd"),
          SPIRVOpcodeNumber(359),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformBitwiseAnd(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.optional(5)
                  )
              ),
          InstructionMetadata1.m132
        )
    case GroupNonUniformBitwiseOr extends Definition(
          NonEmptyString("OpGroupNonUniformBitwiseOr"),
          SPIRVOpcodeNumber(360),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformBitwiseOr(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.optional(5)
                  )
              ),
          InstructionMetadata1.m133
        )
    case GroupNonUniformBitwiseXor extends Definition(
          NonEmptyString("OpGroupNonUniformBitwiseXor"),
          SPIRVOpcodeNumber(361),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformBitwiseXor(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.optional(5)
                  )
              ),
          InstructionMetadata1.m134
        )
    case GroupNonUniformLogicalAnd extends Definition(
          NonEmptyString("OpGroupNonUniformLogicalAnd"),
          SPIRVOpcodeNumber(362),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformLogicalAnd(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.optional(5)
                  )
              ),
          InstructionMetadata1.m135
        )
    case GroupNonUniformLogicalOr extends Definition(
          NonEmptyString("OpGroupNonUniformLogicalOr"),
          SPIRVOpcodeNumber(363),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformLogicalOr(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.optional(5)
                  )
              ),
          InstructionMetadata1.m136
        )
    case GroupNonUniformLogicalXor extends Definition(
          NonEmptyString("OpGroupNonUniformLogicalXor"),
          SPIRVOpcodeNumber(364),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformLogicalXor(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.optional(5)
                  )
              ),
          InstructionMetadata1.m137
        )
    case GroupNonUniformQuadBroadcast extends Definition(
          NonEmptyString("OpGroupNonUniformQuadBroadcast"),
          SPIRVOpcodeNumber(365),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformQuadBroadcast(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata1.m138
        )
    case GroupNonUniformQuadSwap extends Definition(
          NonEmptyString("OpGroupNonUniformQuadSwap"),
          SPIRVOpcodeNumber(366),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformQuadSwap(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata1.m139
        )
    case CopyLogical extends Definition(
          NonEmptyString("OpCopyLogical"),
          SPIRVOpcodeNumber(400),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.CopyLogical(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata1.m140
        )
    case PtrEqual extends Definition(
          NonEmptyString("OpPtrEqual"),
          SPIRVOpcodeNumber(401),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.PtrEqual(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          InstructionMetadata1.m141
        )
    case PtrNotEqual extends Definition(
          NonEmptyString("OpPtrNotEqual"),
          SPIRVOpcodeNumber(402),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.PtrNotEqual(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata1.m142
        )
    case PtrDiff extends Definition(
          NonEmptyString("OpPtrDiff"),
          SPIRVOpcodeNumber(403),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.PtrDiff(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          InstructionMetadata1.m143
        )
    case ColorAttachmentReadEXT extends Definition(
          NonEmptyString("OpColorAttachmentReadEXT"),
          SPIRVOpcodeNumber(4160),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ColorAttachmentReadEXT(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.optional(3)
                  )
              ),
          InstructionMetadata1.m144
        )
    case DepthAttachmentReadEXT extends Definition(
          NonEmptyString("OpDepthAttachmentReadEXT"),
          SPIRVOpcodeNumber(4161),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.DepthAttachmentReadEXT(values.required(0), values.required(1), values.optional(2))
              ),
          InstructionMetadata1.m145
        )
    case StencilAttachmentReadEXT extends Definition(
          NonEmptyString("OpStencilAttachmentReadEXT"),
          SPIRVOpcodeNumber(4162),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.StencilAttachmentReadEXT(values.required(0), values.required(1), values.optional(2))
              ),
          InstructionMetadata1.m146
        )
    case TypeTensorARM extends Definition(
          NonEmptyString("OpTypeTensorARM"),
          SPIRVOpcodeNumber(4163),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.TypeTensorARM(
                    values.required(0),
                    values.required(1),
                    values.optional(2),
                    values.optional(3)
                  )
              ),
          InstructionMetadata1.m147
        )
    case TensorReadARM extends Definition(
          NonEmptyString("OpTensorReadARM"),
          SPIRVOpcodeNumber(4164),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.TensorReadARM(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.optional(4)
                  )
              ),
          InstructionMetadata1.m148
        )
    case TensorWriteARM extends Definition(
          NonEmptyString("OpTensorWriteARM"),
          SPIRVOpcodeNumber(4165),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.TensorWriteARM(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.optional(3)
                  )
              ),
          InstructionMetadata1.m149
        )
    case TensorQuerySizeARM extends Definition(
          NonEmptyString("OpTensorQuerySizeARM"),
          SPIRVOpcodeNumber(4166),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.TensorQuerySizeARM(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata1.m150
        )
    case GraphConstantARM extends Definition(
          NonEmptyString("OpGraphConstantARM"),
          SPIRVOpcodeNumber(4181),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.GraphConstantARM(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata1.m151
        )
    case GraphEntryPointARM extends Definition(
          NonEmptyString("OpGraphEntryPointARM"),
          SPIRVOpcodeNumber(4182),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.GraphEntryPointARM(values.required(0), values.required(1), values.repeated(2))
              ),
          InstructionMetadata1.m152
        )
    case GraphARM extends Definition(
          NonEmptyString("OpGraphARM"),
          SPIRVOpcodeNumber(4183),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.GraphARM(values.required(0), values.required(1))
              ),
          InstructionMetadata1.m153
        )
    case GraphInputARM extends Definition(
          NonEmptyString("OpGraphInputARM"),
          SPIRVOpcodeNumber(4184),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GraphInputARM(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.repeated(3)
                  )
              ),
          InstructionMetadata1.m154
        )
    case GraphSetOutputARM extends Definition(
          NonEmptyString("OpGraphSetOutputARM"),
          SPIRVOpcodeNumber(4185),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.GraphSetOutputARM(values.required(0), values.required(1), values.repeated(2))
              ),
          InstructionMetadata1.m155
        )
    case GraphEndARM extends Definition(
          NonEmptyString("OpGraphEndARM"),
          SPIRVOpcodeNumber(4186),
          definition => raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.GraphEndARM()),
          InstructionMetadata1.m156
        )
    case TypeGraphARM extends Definition(
          NonEmptyString("OpTypeGraphARM"),
          SPIRVOpcodeNumber(4190),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeGraphARM(values.required(0), values.required(1), values.repeated(2))
              ),
          InstructionMetadata1.m157
        )
    case BitcastExtractEXT extends Definition(
          NonEmptyString("OpBitcastExtractEXT"),
          SPIRVOpcodeNumber(4195),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.BitcastExtractEXT(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata1.m158
        )
    case TerminateInvocation extends Definition(
          NonEmptyString("OpTerminateInvocation"),
          SPIRVOpcodeNumber(4416),
          definition => raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.TerminateInvocation()),
          InstructionMetadata1.m159
        )
    case TypeUntypedPointerKHR extends Definition(
          NonEmptyString("OpTypeUntypedPointerKHR"),
          SPIRVOpcodeNumber(4417),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeUntypedPointerKHR(values.required(0), values.required(1))
              ),
          InstructionMetadata1.m160
        )
    case UntypedVariableKHR extends Definition(
          NonEmptyString("OpUntypedVariableKHR"),
          SPIRVOpcodeNumber(4418),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.UntypedVariableKHR(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.optional(3),
                    values.optional(4)
                  )
              ),
          InstructionMetadata1.m161
        )
    case UntypedAccessChainKHR extends Definition(
          NonEmptyString("OpUntypedAccessChainKHR"),
          SPIRVOpcodeNumber(4419),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.UntypedAccessChainKHR(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.repeated(4)
                  )
              ),
          InstructionMetadata1.m162
        )
    case UntypedInBoundsAccessChainKHR extends Definition(
          NonEmptyString("OpUntypedInBoundsAccessChainKHR"),
          SPIRVOpcodeNumber(4420),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.UntypedInBoundsAccessChainKHR(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.repeated(4)
                  )
              ),
          InstructionMetadata1.m163
        )
    case SubgroupBallotKHR extends Definition(
          NonEmptyString("OpSubgroupBallotKHR"),
          SPIRVOpcodeNumber(4421),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.SubgroupBallotKHR(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata1.m164
        )
    case SubgroupFirstInvocationKHR extends Definition(
          NonEmptyString("OpSubgroupFirstInvocationKHR"),
          SPIRVOpcodeNumber(4422),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupFirstInvocationKHR(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata1.m165
        )
    case UntypedPtrAccessChainKHR extends Definition(
          NonEmptyString("OpUntypedPtrAccessChainKHR"),
          SPIRVOpcodeNumber(4423),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.UntypedPtrAccessChainKHR(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.repeated(5)
                  )
              ),
          InstructionMetadata1.m166
        )
    case UntypedInBoundsPtrAccessChainKHR extends Definition(
          NonEmptyString("OpUntypedInBoundsPtrAccessChainKHR"),
          SPIRVOpcodeNumber(4424),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.UntypedInBoundsPtrAccessChainKHR(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.repeated(5)
                  )
              ),
          InstructionMetadata1.m167
        )
    case UntypedArrayLengthKHR extends Definition(
          NonEmptyString("OpUntypedArrayLengthKHR"),
          SPIRVOpcodeNumber(4425),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.UntypedArrayLengthKHR(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata1.m168
        )
    case UntypedPrefetchKHR extends Definition(
          NonEmptyString("OpUntypedPrefetchKHR"),
          SPIRVOpcodeNumber(4426),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.UntypedPrefetchKHR(
                    values.required(0),
                    values.required(1),
                    values.optional(2),
                    values.optional(3),
                    values.optional(4)
                  )
              ),
          InstructionMetadata1.m169
        )
    case FmaKHR extends Definition(
          NonEmptyString("OpFmaKHR"),
          SPIRVOpcodeNumber(4427),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FmaKHR(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata1.m170
        )
    case SubgroupAllKHR extends Definition(
          NonEmptyString("OpSubgroupAllKHR"),
          SPIRVOpcodeNumber(4428),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.SubgroupAllKHR(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata1.m171
        )
    case SubgroupAnyKHR extends Definition(
          NonEmptyString("OpSubgroupAnyKHR"),
          SPIRVOpcodeNumber(4429),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.SubgroupAnyKHR(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata1.m172
        )
    case SubgroupAllEqualKHR extends Definition(
          NonEmptyString("OpSubgroupAllEqualKHR"),
          SPIRVOpcodeNumber(4430),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.SubgroupAllEqualKHR(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata1.m173
        )
    case GroupNonUniformRotateKHR extends Definition(
          NonEmptyString("OpGroupNonUniformRotateKHR"),
          SPIRVOpcodeNumber(4431),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformRotateKHR(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.optional(5)
                  )
              ),
          InstructionMetadata1.m174
        )
    case SubgroupReadInvocationKHR extends Definition(
          NonEmptyString("OpSubgroupReadInvocationKHR"),
          SPIRVOpcodeNumber(4432),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupReadInvocationKHR(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata1.m175
        )
    case ExtInstWithForwardRefsKHR extends Definition(
          NonEmptyString("OpExtInstWithForwardRefsKHR"),
          SPIRVOpcodeNumber(4433),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ExtInstWithForwardRefsKHR(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.repeated(4)
                  )
              ),
          InstructionMetadata1.m176
        )
    case UntypedGroupAsyncCopyKHR extends Definition(
          NonEmptyString("OpUntypedGroupAsyncCopyKHR"),
          SPIRVOpcodeNumber(4434),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.UntypedGroupAsyncCopyKHR(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7),
                    values.required(8),
                    values.optional(9),
                    values.optional(10)
                  )
              ),
          InstructionMetadata1.m177
        )
    case TraceRayKHR extends Definition(
          NonEmptyString("OpTraceRayKHR"),
          SPIRVOpcodeNumber(4445),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.TraceRayKHR(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7),
                    values.required(8),
                    values.required(9),
                    values.required(10)
                  )
              ),
          InstructionMetadata1.m178
        )
    case ExecuteCallableKHR extends Definition(
          NonEmptyString("OpExecuteCallableKHR"),
          SPIRVOpcodeNumber(4446),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ExecuteCallableKHR(values.required(0), values.required(1))
              ),
          InstructionMetadata1.m179
        )
    case ConvertUToAccelerationStructureKHR extends Definition(
          NonEmptyString("OpConvertUToAccelerationStructureKHR"),
          SPIRVOpcodeNumber(4447),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ConvertUToAccelerationStructureKHR(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata1.m180
        )
    case IgnoreIntersectionKHR extends Definition(
          NonEmptyString("OpIgnoreIntersectionKHR"),
          SPIRVOpcodeNumber(4448),
          definition => raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.IgnoreIntersectionKHR()),
          InstructionMetadata1.m181
        )
    case TerminateRayKHR extends Definition(
          NonEmptyString("OpTerminateRayKHR"),
          SPIRVOpcodeNumber(4449),
          definition => raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.TerminateRayKHR()),
          InstructionMetadata1.m182
        )
    case SDot extends Definition(
          NonEmptyString("OpSDot"),
          SPIRVOpcodeNumber(4450),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SDot(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.optional(4)
                  )
              ),
          InstructionMetadata1.m183
        )
    case UDot extends Definition(
          NonEmptyString("OpUDot"),
          SPIRVOpcodeNumber(4451),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.UDot(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.optional(4)
                  )
              ),
          InstructionMetadata1.m184
        )
    case SUDot extends Definition(
          NonEmptyString("OpSUDot"),
          SPIRVOpcodeNumber(4452),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SUDot(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.optional(4)
                  )
              ),
          InstructionMetadata1.m185
        )
    case SDotAccSat extends Definition(
          NonEmptyString("OpSDotAccSat"),
          SPIRVOpcodeNumber(4453),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SDotAccSat(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.optional(5)
                  )
              ),
          InstructionMetadata1.m186
        )
    case UDotAccSat extends Definition(
          NonEmptyString("OpUDotAccSat"),
          SPIRVOpcodeNumber(4454),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.UDotAccSat(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.optional(5)
                  )
              ),
          InstructionMetadata1.m187
        )
    case SUDotAccSat extends Definition(
          NonEmptyString("OpSUDotAccSat"),
          SPIRVOpcodeNumber(4455),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SUDotAccSat(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.optional(5)
                  )
              ),
          InstructionMetadata1.m188
        )
    case TypeCooperativeMatrixKHR extends Definition(
          NonEmptyString("OpTypeCooperativeMatrixKHR"),
          SPIRVOpcodeNumber(4456),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.TypeCooperativeMatrixKHR(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata1.m189
        )
    case CooperativeMatrixLoadKHR extends Definition(
          NonEmptyString("OpCooperativeMatrixLoadKHR"),
          SPIRVOpcodeNumber(4457),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CooperativeMatrixLoadKHR(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.optional(4),
                    values.optional(5)
                  )
              ),
          InstructionMetadata1.m190
        )
    case CooperativeMatrixStoreKHR extends Definition(
          NonEmptyString("OpCooperativeMatrixStoreKHR"),
          SPIRVOpcodeNumber(4458),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CooperativeMatrixStoreKHR(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.optional(3),
                    values.optional(4)
                  )
              ),
          InstructionMetadata1.m191
        )
    case CooperativeMatrixMulAddKHR extends Definition(
          NonEmptyString("OpCooperativeMatrixMulAddKHR"),
          SPIRVOpcodeNumber(4459),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CooperativeMatrixMulAddKHR(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.optional(5)
                  )
              ),
          InstructionMetadata1.m192
        )
    case CooperativeMatrixLengthKHR extends Definition(
          NonEmptyString("OpCooperativeMatrixLengthKHR"),
          SPIRVOpcodeNumber(4460),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CooperativeMatrixLengthKHR(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata1.m193
        )
    case ConstantCompositeReplicateEXT extends Definition(
          NonEmptyString("OpConstantCompositeReplicateEXT"),
          SPIRVOpcodeNumber(4461),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ConstantCompositeReplicateEXT(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata1.m194
        )
    case SpecConstantCompositeReplicateEXT extends Definition(
          NonEmptyString("OpSpecConstantCompositeReplicateEXT"),
          SPIRVOpcodeNumber(4462),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SpecConstantCompositeReplicateEXT(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata1.m195
        )
    case CompositeConstructReplicateEXT extends Definition(
          NonEmptyString("OpCompositeConstructReplicateEXT"),
          SPIRVOpcodeNumber(4463),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CompositeConstructReplicateEXT(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata1.m196
        )
    case TypeRayQueryKHR extends Definition(
          NonEmptyString("OpTypeRayQueryKHR"),
          SPIRVOpcodeNumber(4472),
          definition =>
            raw =>
              Instruction.decodeDefinition(definition, raw, values => Instruction.TypeRayQueryKHR(values.required(0))),
          InstructionMetadata1.m197
        )
    case RayQueryInitializeKHR extends Definition(
          NonEmptyString("OpRayQueryInitializeKHR"),
          SPIRVOpcodeNumber(4473),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.RayQueryInitializeKHR(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7)
                  )
              ),
          InstructionMetadata1.m198
        )
    case RayQueryTerminateKHR extends Definition(
          NonEmptyString("OpRayQueryTerminateKHR"),
          SPIRVOpcodeNumber(4474),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.RayQueryTerminateKHR(values.required(0))
              ),
          InstructionMetadata1.m199
        )
    case RayQueryGenerateIntersectionKHR extends Definition(
          NonEmptyString("OpRayQueryGenerateIntersectionKHR"),
          SPIRVOpcodeNumber(4475),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.RayQueryGenerateIntersectionKHR(values.required(0), values.required(1))
              ),
          InstructionMetadata2.m0
        )
    case RayQueryConfirmIntersectionKHR extends Definition(
          NonEmptyString("OpRayQueryConfirmIntersectionKHR"),
          SPIRVOpcodeNumber(4476),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.RayQueryConfirmIntersectionKHR(values.required(0))
              ),
          InstructionMetadata2.m1
        )
    case RayQueryProceedKHR extends Definition(
          NonEmptyString("OpRayQueryProceedKHR"),
          SPIRVOpcodeNumber(4477),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.RayQueryProceedKHR(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m2
        )
    case RayQueryGetIntersectionTypeKHR extends Definition(
          NonEmptyString("OpRayQueryGetIntersectionTypeKHR"),
          SPIRVOpcodeNumber(4479),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.RayQueryGetIntersectionTypeKHR(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata2.m3
        )
    case ImageSampleWeightedQCOM extends Definition(
          NonEmptyString("OpImageSampleWeightedQCOM"),
          SPIRVOpcodeNumber(4480),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageSampleWeightedQCOM(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata2.m4
        )
    case ImageBoxFilterQCOM extends Definition(
          NonEmptyString("OpImageBoxFilterQCOM"),
          SPIRVOpcodeNumber(4481),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageBoxFilterQCOM(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata2.m5
        )
    case ImageBlockMatchSSDQCOM extends Definition(
          NonEmptyString("OpImageBlockMatchSSDQCOM"),
          SPIRVOpcodeNumber(4482),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageBlockMatchSSDQCOM(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6)
                  )
              ),
          InstructionMetadata2.m6
        )
    case ImageBlockMatchSADQCOM extends Definition(
          NonEmptyString("OpImageBlockMatchSADQCOM"),
          SPIRVOpcodeNumber(4483),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageBlockMatchSADQCOM(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6)
                  )
              ),
          InstructionMetadata2.m7
        )
    case BitCastArrayQCOM extends Definition(
          NonEmptyString("OpBitCastArrayQCOM"),
          SPIRVOpcodeNumber(4497),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.BitCastArrayQCOM(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m8
        )
    case ImageBlockMatchWindowSSDQCOM extends Definition(
          NonEmptyString("OpImageBlockMatchWindowSSDQCOM"),
          SPIRVOpcodeNumber(4500),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageBlockMatchWindowSSDQCOM(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6)
                  )
              ),
          InstructionMetadata2.m9
        )
    case ImageBlockMatchWindowSADQCOM extends Definition(
          NonEmptyString("OpImageBlockMatchWindowSADQCOM"),
          SPIRVOpcodeNumber(4501),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageBlockMatchWindowSADQCOM(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6)
                  )
              ),
          InstructionMetadata2.m10
        )
    case ImageBlockMatchGatherSSDQCOM extends Definition(
          NonEmptyString("OpImageBlockMatchGatherSSDQCOM"),
          SPIRVOpcodeNumber(4502),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageBlockMatchGatherSSDQCOM(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6)
                  )
              ),
          InstructionMetadata2.m11
        )
    case ImageBlockMatchGatherSADQCOM extends Definition(
          NonEmptyString("OpImageBlockMatchGatherSADQCOM"),
          SPIRVOpcodeNumber(4503),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageBlockMatchGatherSADQCOM(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6)
                  )
              ),
          InstructionMetadata2.m12
        )
    case CompositeConstructCoopMatQCOM extends Definition(
          NonEmptyString("OpCompositeConstructCoopMatQCOM"),
          SPIRVOpcodeNumber(4540),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CompositeConstructCoopMatQCOM(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m13
        )
    case CompositeExtractCoopMatQCOM extends Definition(
          NonEmptyString("OpCompositeExtractCoopMatQCOM"),
          SPIRVOpcodeNumber(4541),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CompositeExtractCoopMatQCOM(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m14
        )
    case ExtractSubArrayQCOM extends Definition(
          NonEmptyString("OpExtractSubArrayQCOM"),
          SPIRVOpcodeNumber(4542),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ExtractSubArrayQCOM(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata2.m15
        )
    case ImageGatherQCOM extends Definition(
          NonEmptyString("OpImageGatherQCOM"),
          SPIRVOpcodeNumber(4545),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageGatherQCOM(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.optional(6)
                  )
              ),
          InstructionMetadata2.m16
        )
    case GroupIAddNonUniformAMD extends Definition(
          NonEmptyString("OpGroupIAddNonUniformAMD"),
          SPIRVOpcodeNumber(5000),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupIAddNonUniformAMD(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata2.m17
        )
    case GroupFAddNonUniformAMD extends Definition(
          NonEmptyString("OpGroupFAddNonUniformAMD"),
          SPIRVOpcodeNumber(5001),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupFAddNonUniformAMD(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata2.m18
        )
    case GroupFMinNonUniformAMD extends Definition(
          NonEmptyString("OpGroupFMinNonUniformAMD"),
          SPIRVOpcodeNumber(5002),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupFMinNonUniformAMD(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata2.m19
        )
    case GroupUMinNonUniformAMD extends Definition(
          NonEmptyString("OpGroupUMinNonUniformAMD"),
          SPIRVOpcodeNumber(5003),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupUMinNonUniformAMD(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata2.m20
        )
    case GroupSMinNonUniformAMD extends Definition(
          NonEmptyString("OpGroupSMinNonUniformAMD"),
          SPIRVOpcodeNumber(5004),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupSMinNonUniformAMD(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata2.m21
        )
    case GroupFMaxNonUniformAMD extends Definition(
          NonEmptyString("OpGroupFMaxNonUniformAMD"),
          SPIRVOpcodeNumber(5005),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupFMaxNonUniformAMD(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata2.m22
        )
    case GroupUMaxNonUniformAMD extends Definition(
          NonEmptyString("OpGroupUMaxNonUniformAMD"),
          SPIRVOpcodeNumber(5006),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupUMaxNonUniformAMD(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata2.m23
        )
    case GroupSMaxNonUniformAMD extends Definition(
          NonEmptyString("OpGroupSMaxNonUniformAMD"),
          SPIRVOpcodeNumber(5007),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupSMaxNonUniformAMD(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata2.m24
        )
    case FragmentMaskFetchAMD extends Definition(
          NonEmptyString("OpFragmentMaskFetchAMD"),
          SPIRVOpcodeNumber(5011),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FragmentMaskFetchAMD(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata2.m25
        )
    case FragmentFetchAMD extends Definition(
          NonEmptyString("OpFragmentFetchAMD"),
          SPIRVOpcodeNumber(5012),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FragmentFetchAMD(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata2.m26
        )
    case ReadClockKHR extends Definition(
          NonEmptyString("OpReadClockKHR"),
          SPIRVOpcodeNumber(5056),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ReadClockKHR(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m27
        )
    case AllocateNodePayloadsAMDX extends Definition(
          NonEmptyString("OpAllocateNodePayloadsAMDX"),
          SPIRVOpcodeNumber(5074),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.AllocateNodePayloadsAMDX(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata2.m28
        )
    case EnqueueNodePayloadsAMDX extends Definition(
          NonEmptyString("OpEnqueueNodePayloadsAMDX"),
          SPIRVOpcodeNumber(5075),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.EnqueueNodePayloadsAMDX(values.required(0))
              ),
          InstructionMetadata2.m29
        )
    case TypeNodePayloadArrayAMDX extends Definition(
          NonEmptyString("OpTypeNodePayloadArrayAMDX"),
          SPIRVOpcodeNumber(5076),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeNodePayloadArrayAMDX(values.required(0), values.required(1))
              ),
          InstructionMetadata2.m30
        )
    case FinishWritingNodePayloadAMDX extends Definition(
          NonEmptyString("OpFinishWritingNodePayloadAMDX"),
          SPIRVOpcodeNumber(5078),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FinishWritingNodePayloadAMDX(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m31
        )
    case NodePayloadArrayLengthAMDX extends Definition(
          NonEmptyString("OpNodePayloadArrayLengthAMDX"),
          SPIRVOpcodeNumber(5090),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.NodePayloadArrayLengthAMDX(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m32
        )
    case IsNodePayloadValidAMDX extends Definition(
          NonEmptyString("OpIsNodePayloadValidAMDX"),
          SPIRVOpcodeNumber(5101),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.IsNodePayloadValidAMDX(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata2.m33
        )
    case ConstantStringAMDX extends Definition(
          NonEmptyString("OpConstantStringAMDX"),
          SPIRVOpcodeNumber(5103),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ConstantStringAMDX(values.required(0), values.required(1))
              ),
          InstructionMetadata2.m34
        )
    case SpecConstantStringAMDX extends Definition(
          NonEmptyString("OpSpecConstantStringAMDX"),
          SPIRVOpcodeNumber(5104),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.SpecConstantStringAMDX(values.required(0), values.required(1))
              ),
          InstructionMetadata2.m35
        )
    case GroupNonUniformQuadAllKHR extends Definition(
          NonEmptyString("OpGroupNonUniformQuadAllKHR"),
          SPIRVOpcodeNumber(5110),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformQuadAllKHR(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m36
        )
    case GroupNonUniformQuadAnyKHR extends Definition(
          NonEmptyString("OpGroupNonUniformQuadAnyKHR"),
          SPIRVOpcodeNumber(5111),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformQuadAnyKHR(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m37
        )
    case TypeBufferEXT extends Definition(
          NonEmptyString("OpTypeBufferEXT"),
          SPIRVOpcodeNumber(5115),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeBufferEXT(values.required(0), values.required(1))
              ),
          InstructionMetadata2.m38
        )
    case BufferPointerEXT extends Definition(
          NonEmptyString("OpBufferPointerEXT"),
          SPIRVOpcodeNumber(5119),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.BufferPointerEXT(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m39
        )
    case AbortKHR extends Definition(
          NonEmptyString("OpAbortKHR"),
          SPIRVOpcodeNumber(5121),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.AbortKHR(values.required(0), values.required(1))
              ),
          InstructionMetadata2.m40
        )
    case UntypedImageTexelPointerEXT extends Definition(
          NonEmptyString("OpUntypedImageTexelPointerEXT"),
          SPIRVOpcodeNumber(5126),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.UntypedImageTexelPointerEXT(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata2.m41
        )
    case MemberDecorateIdEXT extends Definition(
          NonEmptyString("OpMemberDecorateIdEXT"),
          SPIRVOpcodeNumber(5127),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.MemberDecorateIdEXT(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m42
        )
    case ConstantSizeOfEXT extends Definition(
          NonEmptyString("OpConstantSizeOfEXT"),
          SPIRVOpcodeNumber(5129),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ConstantSizeOfEXT(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m43
        )
    case ConstantDataKHR extends Definition(
          NonEmptyString("OpConstantDataKHR"),
          SPIRVOpcodeNumber(5147),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ConstantDataKHR(values.required(0), values.required(1), values.repeated(2))
              ),
          InstructionMetadata2.m44
        )
    case SpecConstantDataKHR extends Definition(
          NonEmptyString("OpSpecConstantDataKHR"),
          SPIRVOpcodeNumber(5148),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.SpecConstantDataKHR(values.required(0), values.required(1), values.repeated(2))
              ),
          InstructionMetadata2.m45
        )
    case PoisonKHR extends Definition(
          NonEmptyString("OpPoisonKHR"),
          SPIRVOpcodeNumber(5158),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.PoisonKHR(values.required(0), values.required(1))
              ),
          InstructionMetadata2.m46
        )
    case FreezeKHR extends Definition(
          NonEmptyString("OpFreezeKHR"),
          SPIRVOpcodeNumber(5159),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.FreezeKHR(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m47
        )
    case HitObjectRecordHitMotionNV extends Definition(
          NonEmptyString("OpHitObjectRecordHitMotionNV"),
          SPIRVOpcodeNumber(5249),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectRecordHitMotionNV(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7),
                    values.required(8),
                    values.required(9),
                    values.required(10),
                    values.required(11),
                    values.required(12),
                    values.required(13)
                  )
              ),
          InstructionMetadata2.m48
        )
    case HitObjectRecordHitWithIndexMotionNV extends Definition(
          NonEmptyString("OpHitObjectRecordHitWithIndexMotionNV"),
          SPIRVOpcodeNumber(5250),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectRecordHitWithIndexMotionNV(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7),
                    values.required(8),
                    values.required(9),
                    values.required(10),
                    values.required(11),
                    values.required(12)
                  )
              ),
          InstructionMetadata2.m49
        )
    case HitObjectRecordMissMotionNV extends Definition(
          NonEmptyString("OpHitObjectRecordMissMotionNV"),
          SPIRVOpcodeNumber(5251),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectRecordMissMotionNV(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6)
                  )
              ),
          InstructionMetadata2.m50
        )
    case HitObjectGetWorldToObjectNV extends Definition(
          NonEmptyString("OpHitObjectGetWorldToObjectNV"),
          SPIRVOpcodeNumber(5252),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetWorldToObjectNV(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m51
        )
    case HitObjectGetObjectToWorldNV extends Definition(
          NonEmptyString("OpHitObjectGetObjectToWorldNV"),
          SPIRVOpcodeNumber(5253),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetObjectToWorldNV(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m52
        )
    case HitObjectGetObjectRayDirectionNV extends Definition(
          NonEmptyString("OpHitObjectGetObjectRayDirectionNV"),
          SPIRVOpcodeNumber(5254),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetObjectRayDirectionNV(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata2.m53
        )
    case HitObjectGetObjectRayOriginNV extends Definition(
          NonEmptyString("OpHitObjectGetObjectRayOriginNV"),
          SPIRVOpcodeNumber(5255),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetObjectRayOriginNV(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m54
        )
    case HitObjectTraceRayMotionNV extends Definition(
          NonEmptyString("OpHitObjectTraceRayMotionNV"),
          SPIRVOpcodeNumber(5256),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectTraceRayMotionNV(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7),
                    values.required(8),
                    values.required(9),
                    values.required(10),
                    values.required(11),
                    values.required(12)
                  )
              ),
          InstructionMetadata2.m55
        )
    case HitObjectGetShaderRecordBufferHandleNV extends Definition(
          NonEmptyString("OpHitObjectGetShaderRecordBufferHandleNV"),
          SPIRVOpcodeNumber(5257),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetShaderRecordBufferHandleNV(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata2.m56
        )
    case HitObjectGetShaderBindingTableRecordIndexNV extends Definition(
          NonEmptyString("OpHitObjectGetShaderBindingTableRecordIndexNV"),
          SPIRVOpcodeNumber(5258),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetShaderBindingTableRecordIndexNV(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata2.m57
        )
    case HitObjectRecordEmptyNV extends Definition(
          NonEmptyString("OpHitObjectRecordEmptyNV"),
          SPIRVOpcodeNumber(5259),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.HitObjectRecordEmptyNV(values.required(0))
              ),
          InstructionMetadata2.m58
        )
    case HitObjectTraceRayNV extends Definition(
          NonEmptyString("OpHitObjectTraceRayNV"),
          SPIRVOpcodeNumber(5260),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectTraceRayNV(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7),
                    values.required(8),
                    values.required(9),
                    values.required(10),
                    values.required(11)
                  )
              ),
          InstructionMetadata2.m59
        )
    case HitObjectRecordHitNV extends Definition(
          NonEmptyString("OpHitObjectRecordHitNV"),
          SPIRVOpcodeNumber(5261),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectRecordHitNV(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7),
                    values.required(8),
                    values.required(9),
                    values.required(10),
                    values.required(11),
                    values.required(12)
                  )
              ),
          InstructionMetadata2.m60
        )
    case HitObjectRecordHitWithIndexNV extends Definition(
          NonEmptyString("OpHitObjectRecordHitWithIndexNV"),
          SPIRVOpcodeNumber(5262),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectRecordHitWithIndexNV(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7),
                    values.required(8),
                    values.required(9),
                    values.required(10),
                    values.required(11)
                  )
              ),
          InstructionMetadata2.m61
        )
    case HitObjectRecordMissNV extends Definition(
          NonEmptyString("OpHitObjectRecordMissNV"),
          SPIRVOpcodeNumber(5263),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectRecordMissNV(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata2.m62
        )
    case HitObjectExecuteShaderNV extends Definition(
          NonEmptyString("OpHitObjectExecuteShaderNV"),
          SPIRVOpcodeNumber(5264),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.HitObjectExecuteShaderNV(values.required(0), values.required(1))
              ),
          InstructionMetadata2.m63
        )
    case HitObjectGetCurrentTimeNV extends Definition(
          NonEmptyString("OpHitObjectGetCurrentTimeNV"),
          SPIRVOpcodeNumber(5265),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetCurrentTimeNV(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m64
        )
    case HitObjectGetAttributesNV extends Definition(
          NonEmptyString("OpHitObjectGetAttributesNV"),
          SPIRVOpcodeNumber(5266),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.HitObjectGetAttributesNV(values.required(0), values.required(1))
              ),
          InstructionMetadata2.m65
        )
    case HitObjectGetHitKindNV extends Definition(
          NonEmptyString("OpHitObjectGetHitKindNV"),
          SPIRVOpcodeNumber(5267),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.HitObjectGetHitKindNV(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m66
        )
    case HitObjectGetPrimitiveIndexNV extends Definition(
          NonEmptyString("OpHitObjectGetPrimitiveIndexNV"),
          SPIRVOpcodeNumber(5268),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetPrimitiveIndexNV(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m67
        )
    case HitObjectGetGeometryIndexNV extends Definition(
          NonEmptyString("OpHitObjectGetGeometryIndexNV"),
          SPIRVOpcodeNumber(5269),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetGeometryIndexNV(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m68
        )
    case HitObjectGetInstanceIdNV extends Definition(
          NonEmptyString("OpHitObjectGetInstanceIdNV"),
          SPIRVOpcodeNumber(5270),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetInstanceIdNV(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m69
        )
    case HitObjectGetInstanceCustomIndexNV extends Definition(
          NonEmptyString("OpHitObjectGetInstanceCustomIndexNV"),
          SPIRVOpcodeNumber(5271),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetInstanceCustomIndexNV(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata2.m70
        )
    case HitObjectGetWorldRayDirectionNV extends Definition(
          NonEmptyString("OpHitObjectGetWorldRayDirectionNV"),
          SPIRVOpcodeNumber(5272),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetWorldRayDirectionNV(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata2.m71
        )
    case HitObjectGetWorldRayOriginNV extends Definition(
          NonEmptyString("OpHitObjectGetWorldRayOriginNV"),
          SPIRVOpcodeNumber(5273),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetWorldRayOriginNV(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m72
        )
    case HitObjectGetRayTMaxNV extends Definition(
          NonEmptyString("OpHitObjectGetRayTMaxNV"),
          SPIRVOpcodeNumber(5274),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.HitObjectGetRayTMaxNV(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m73
        )
    case HitObjectGetRayTMinNV extends Definition(
          NonEmptyString("OpHitObjectGetRayTMinNV"),
          SPIRVOpcodeNumber(5275),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.HitObjectGetRayTMinNV(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m74
        )
    case HitObjectIsEmptyNV extends Definition(
          NonEmptyString("OpHitObjectIsEmptyNV"),
          SPIRVOpcodeNumber(5276),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.HitObjectIsEmptyNV(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m75
        )
    case HitObjectIsHitNV extends Definition(
          NonEmptyString("OpHitObjectIsHitNV"),
          SPIRVOpcodeNumber(5277),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.HitObjectIsHitNV(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m76
        )
    case HitObjectIsMissNV extends Definition(
          NonEmptyString("OpHitObjectIsMissNV"),
          SPIRVOpcodeNumber(5278),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.HitObjectIsMissNV(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m77
        )
    case ReorderThreadWithHitObjectNV extends Definition(
          NonEmptyString("OpReorderThreadWithHitObjectNV"),
          SPIRVOpcodeNumber(5279),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ReorderThreadWithHitObjectNV(values.required(0), values.optional(1), values.optional(2))
              ),
          InstructionMetadata2.m78
        )
    case ReorderThreadWithHintNV extends Definition(
          NonEmptyString("OpReorderThreadWithHintNV"),
          SPIRVOpcodeNumber(5280),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ReorderThreadWithHintNV(values.required(0), values.required(1))
              ),
          InstructionMetadata2.m79
        )
    case TypeHitObjectNV extends Definition(
          NonEmptyString("OpTypeHitObjectNV"),
          SPIRVOpcodeNumber(5281),
          definition =>
            raw =>
              Instruction.decodeDefinition(definition, raw, values => Instruction.TypeHitObjectNV(values.required(0))),
          InstructionMetadata2.m80
        )
    case ImageSampleFootprintNV extends Definition(
          NonEmptyString("OpImageSampleFootprintNV"),
          SPIRVOpcodeNumber(5283),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageSampleFootprintNV(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.optional(6)
                  )
              ),
          InstructionMetadata2.m81
        )
    case TypeVectorIdEXT extends Definition(
          NonEmptyString("OpTypeVectorIdEXT"),
          SPIRVOpcodeNumber(5288),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeVectorIdEXT(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m82
        )
    case CooperativeVectorMatrixMulNV extends Definition(
          NonEmptyString("OpCooperativeVectorMatrixMulNV"),
          SPIRVOpcodeNumber(5289),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CooperativeVectorMatrixMulNV(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7),
                    values.required(8),
                    values.required(9),
                    values.required(10),
                    values.optional(11),
                    values.optional(12)
                  )
              ),
          InstructionMetadata2.m83
        )
    case CooperativeVectorOuterProductAccumulateNV extends Definition(
          NonEmptyString("OpCooperativeVectorOuterProductAccumulateNV"),
          SPIRVOpcodeNumber(5290),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CooperativeVectorOuterProductAccumulateNV(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.optional(6)
                  )
              ),
          InstructionMetadata2.m84
        )
    case CooperativeVectorReduceSumAccumulateNV extends Definition(
          NonEmptyString("OpCooperativeVectorReduceSumAccumulateNV"),
          SPIRVOpcodeNumber(5291),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CooperativeVectorReduceSumAccumulateNV(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata2.m85
        )
    case CooperativeVectorMatrixMulAddNV extends Definition(
          NonEmptyString("OpCooperativeVectorMatrixMulAddNV"),
          SPIRVOpcodeNumber(5292),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CooperativeVectorMatrixMulAddNV(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7),
                    values.required(8),
                    values.required(9),
                    values.required(10),
                    values.required(11),
                    values.required(12),
                    values.required(13),
                    values.optional(14),
                    values.optional(15)
                  )
              ),
          InstructionMetadata2.m86
        )
    case CooperativeMatrixConvertNV extends Definition(
          NonEmptyString("OpCooperativeMatrixConvertNV"),
          SPIRVOpcodeNumber(5293),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CooperativeMatrixConvertNV(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m87
        )
    case EmitMeshTasksEXT extends Definition(
          NonEmptyString("OpEmitMeshTasksEXT"),
          SPIRVOpcodeNumber(5294),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.EmitMeshTasksEXT(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.optional(3)
                  )
              ),
          InstructionMetadata2.m88
        )
    case SetMeshOutputsEXT extends Definition(
          NonEmptyString("OpSetMeshOutputsEXT"),
          SPIRVOpcodeNumber(5295),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.SetMeshOutputsEXT(values.required(0), values.required(1))
              ),
          InstructionMetadata2.m89
        )
    case GroupNonUniformPartitionEXT extends Definition(
          NonEmptyString("OpGroupNonUniformPartitionEXT"),
          SPIRVOpcodeNumber(5296),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformPartitionEXT(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m90
        )
    case WritePackedPrimitiveIndices4x8NV extends Definition(
          NonEmptyString("OpWritePackedPrimitiveIndices4x8NV"),
          SPIRVOpcodeNumber(5299),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.WritePackedPrimitiveIndices4x8NV(values.required(0), values.required(1))
              ),
          InstructionMetadata2.m91
        )
    case FetchMicroTriangleVertexPositionNV extends Definition(
          NonEmptyString("OpFetchMicroTriangleVertexPositionNV"),
          SPIRVOpcodeNumber(5300),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FetchMicroTriangleVertexPositionNV(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6)
                  )
              ),
          InstructionMetadata2.m92
        )
    case FetchMicroTriangleVertexBarycentricNV extends Definition(
          NonEmptyString("OpFetchMicroTriangleVertexBarycentricNV"),
          SPIRVOpcodeNumber(5301),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FetchMicroTriangleVertexBarycentricNV(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6)
                  )
              ),
          InstructionMetadata2.m93
        )
    case CooperativeVectorLoadNV extends Definition(
          NonEmptyString("OpCooperativeVectorLoadNV"),
          SPIRVOpcodeNumber(5302),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CooperativeVectorLoadNV(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.optional(4)
                  )
              ),
          InstructionMetadata2.m94
        )
    case CooperativeVectorStoreNV extends Definition(
          NonEmptyString("OpCooperativeVectorStoreNV"),
          SPIRVOpcodeNumber(5303),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CooperativeVectorStoreNV(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.optional(3)
                  )
              ),
          InstructionMetadata2.m95
        )
    case HitObjectRecordFromQueryEXT extends Definition(
          NonEmptyString("OpHitObjectRecordFromQueryEXT"),
          SPIRVOpcodeNumber(5304),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectRecordFromQueryEXT(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.optional(4)
                  )
              ),
          InstructionMetadata2.m96
        )
    case HitObjectRecordMissEXT extends Definition(
          NonEmptyString("OpHitObjectRecordMissEXT"),
          SPIRVOpcodeNumber(5305),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectRecordMissEXT(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6)
                  )
              ),
          InstructionMetadata2.m97
        )
    case HitObjectRecordMissMotionEXT extends Definition(
          NonEmptyString("OpHitObjectRecordMissMotionEXT"),
          SPIRVOpcodeNumber(5306),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectRecordMissMotionEXT(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7)
                  )
              ),
          InstructionMetadata2.m98
        )
    case HitObjectGetIntersectionTriangleVertexPositionsEXT extends Definition(
          NonEmptyString("OpHitObjectGetIntersectionTriangleVertexPositionsEXT"),
          SPIRVOpcodeNumber(5307),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetIntersectionTriangleVertexPositionsEXT(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata2.m99
        )
    case HitObjectGetRayFlagsEXT extends Definition(
          NonEmptyString("OpHitObjectGetRayFlagsEXT"),
          SPIRVOpcodeNumber(5308),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetRayFlagsEXT(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m100
        )
    case HitObjectSetShaderBindingTableRecordIndexEXT extends Definition(
          NonEmptyString("OpHitObjectSetShaderBindingTableRecordIndexEXT"),
          SPIRVOpcodeNumber(5309),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectSetShaderBindingTableRecordIndexEXT(values.required(0), values.required(1))
              ),
          InstructionMetadata2.m101
        )
    case HitObjectReorderExecuteShaderEXT extends Definition(
          NonEmptyString("OpHitObjectReorderExecuteShaderEXT"),
          SPIRVOpcodeNumber(5310),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectReorderExecuteShaderEXT(
                    values.required(0),
                    values.required(1),
                    values.optional(2),
                    values.optional(3)
                  )
              ),
          InstructionMetadata2.m102
        )
    case HitObjectTraceReorderExecuteEXT extends Definition(
          NonEmptyString("OpHitObjectTraceReorderExecuteEXT"),
          SPIRVOpcodeNumber(5311),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectTraceReorderExecuteEXT(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7),
                    values.required(8),
                    values.required(9),
                    values.required(10),
                    values.required(11),
                    values.optional(12),
                    values.optional(13)
                  )
              ),
          InstructionMetadata2.m103
        )
    case HitObjectTraceMotionReorderExecuteEXT extends Definition(
          NonEmptyString("OpHitObjectTraceMotionReorderExecuteEXT"),
          SPIRVOpcodeNumber(5312),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectTraceMotionReorderExecuteEXT(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7),
                    values.required(8),
                    values.required(9),
                    values.required(10),
                    values.required(11),
                    values.required(12),
                    values.optional(13),
                    values.optional(14)
                  )
              ),
          InstructionMetadata2.m104
        )
    case TypeHitObjectEXT extends Definition(
          NonEmptyString("OpTypeHitObjectEXT"),
          SPIRVOpcodeNumber(5313),
          definition =>
            raw =>
              Instruction.decodeDefinition(definition, raw, values => Instruction.TypeHitObjectEXT(values.required(0))),
          InstructionMetadata2.m105
        )
    case ReorderThreadWithHintEXT extends Definition(
          NonEmptyString("OpReorderThreadWithHintEXT"),
          SPIRVOpcodeNumber(5314),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ReorderThreadWithHintEXT(values.required(0), values.required(1))
              ),
          InstructionMetadata2.m106
        )
    case ReorderThreadWithHitObjectEXT extends Definition(
          NonEmptyString("OpReorderThreadWithHitObjectEXT"),
          SPIRVOpcodeNumber(5315),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ReorderThreadWithHitObjectEXT(values.required(0), values.optional(1), values.optional(2))
              ),
          InstructionMetadata2.m107
        )
    case HitObjectTraceRayEXT extends Definition(
          NonEmptyString("OpHitObjectTraceRayEXT"),
          SPIRVOpcodeNumber(5316),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectTraceRayEXT(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7),
                    values.required(8),
                    values.required(9),
                    values.required(10),
                    values.required(11)
                  )
              ),
          InstructionMetadata2.m108
        )
    case HitObjectTraceRayMotionEXT extends Definition(
          NonEmptyString("OpHitObjectTraceRayMotionEXT"),
          SPIRVOpcodeNumber(5317),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectTraceRayMotionEXT(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7),
                    values.required(8),
                    values.required(9),
                    values.required(10),
                    values.required(11),
                    values.required(12)
                  )
              ),
          InstructionMetadata2.m109
        )
    case HitObjectRecordEmptyEXT extends Definition(
          NonEmptyString("OpHitObjectRecordEmptyEXT"),
          SPIRVOpcodeNumber(5318),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.HitObjectRecordEmptyEXT(values.required(0))
              ),
          InstructionMetadata2.m110
        )
    case HitObjectExecuteShaderEXT extends Definition(
          NonEmptyString("OpHitObjectExecuteShaderEXT"),
          SPIRVOpcodeNumber(5319),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.HitObjectExecuteShaderEXT(values.required(0), values.required(1))
              ),
          InstructionMetadata2.m111
        )
    case HitObjectGetCurrentTimeEXT extends Definition(
          NonEmptyString("OpHitObjectGetCurrentTimeEXT"),
          SPIRVOpcodeNumber(5320),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetCurrentTimeEXT(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m112
        )
    case HitObjectGetAttributesEXT extends Definition(
          NonEmptyString("OpHitObjectGetAttributesEXT"),
          SPIRVOpcodeNumber(5321),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.HitObjectGetAttributesEXT(values.required(0), values.required(1))
              ),
          InstructionMetadata2.m113
        )
    case HitObjectGetHitKindEXT extends Definition(
          NonEmptyString("OpHitObjectGetHitKindEXT"),
          SPIRVOpcodeNumber(5322),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.HitObjectGetHitKindEXT(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m114
        )
    case HitObjectGetPrimitiveIndexEXT extends Definition(
          NonEmptyString("OpHitObjectGetPrimitiveIndexEXT"),
          SPIRVOpcodeNumber(5323),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetPrimitiveIndexEXT(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m115
        )
    case HitObjectGetGeometryIndexEXT extends Definition(
          NonEmptyString("OpHitObjectGetGeometryIndexEXT"),
          SPIRVOpcodeNumber(5324),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetGeometryIndexEXT(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m116
        )
    case HitObjectGetInstanceIdEXT extends Definition(
          NonEmptyString("OpHitObjectGetInstanceIdEXT"),
          SPIRVOpcodeNumber(5325),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetInstanceIdEXT(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m117
        )
    case HitObjectGetInstanceCustomIndexEXT extends Definition(
          NonEmptyString("OpHitObjectGetInstanceCustomIndexEXT"),
          SPIRVOpcodeNumber(5326),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetInstanceCustomIndexEXT(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata2.m118
        )
    case HitObjectGetObjectRayOriginEXT extends Definition(
          NonEmptyString("OpHitObjectGetObjectRayOriginEXT"),
          SPIRVOpcodeNumber(5327),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetObjectRayOriginEXT(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m119
        )
    case HitObjectGetObjectRayDirectionEXT extends Definition(
          NonEmptyString("OpHitObjectGetObjectRayDirectionEXT"),
          SPIRVOpcodeNumber(5328),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetObjectRayDirectionEXT(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata2.m120
        )
    case HitObjectGetWorldRayDirectionEXT extends Definition(
          NonEmptyString("OpHitObjectGetWorldRayDirectionEXT"),
          SPIRVOpcodeNumber(5329),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetWorldRayDirectionEXT(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata2.m121
        )
    case HitObjectGetWorldRayOriginEXT extends Definition(
          NonEmptyString("OpHitObjectGetWorldRayOriginEXT"),
          SPIRVOpcodeNumber(5330),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetWorldRayOriginEXT(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m122
        )
    case HitObjectGetObjectToWorldEXT extends Definition(
          NonEmptyString("OpHitObjectGetObjectToWorldEXT"),
          SPIRVOpcodeNumber(5331),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetObjectToWorldEXT(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m123
        )
    case HitObjectGetWorldToObjectEXT extends Definition(
          NonEmptyString("OpHitObjectGetWorldToObjectEXT"),
          SPIRVOpcodeNumber(5332),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetWorldToObjectEXT(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m124
        )
    case HitObjectGetRayTMaxEXT extends Definition(
          NonEmptyString("OpHitObjectGetRayTMaxEXT"),
          SPIRVOpcodeNumber(5333),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.HitObjectGetRayTMaxEXT(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m125
        )
    case ReportIntersectionKHR extends Definition(
          NonEmptyString("OpReportIntersectionKHR"),
          SPIRVOpcodeNumber(5334),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ReportIntersectionKHR(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata2.m126
        )
    case IgnoreIntersectionNV extends Definition(
          NonEmptyString("OpIgnoreIntersectionNV"),
          SPIRVOpcodeNumber(5335),
          definition => raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.IgnoreIntersectionNV()),
          InstructionMetadata2.m127
        )
    case TerminateRayNV extends Definition(
          NonEmptyString("OpTerminateRayNV"),
          SPIRVOpcodeNumber(5336),
          definition => raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.TerminateRayNV()),
          InstructionMetadata2.m128
        )
    case TraceNV extends Definition(
          NonEmptyString("OpTraceNV"),
          SPIRVOpcodeNumber(5337),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.TraceNV(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7),
                    values.required(8),
                    values.required(9),
                    values.required(10)
                  )
              ),
          InstructionMetadata2.m129
        )
    case TraceMotionNV extends Definition(
          NonEmptyString("OpTraceMotionNV"),
          SPIRVOpcodeNumber(5338),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.TraceMotionNV(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7),
                    values.required(8),
                    values.required(9),
                    values.required(10),
                    values.required(11)
                  )
              ),
          InstructionMetadata2.m130
        )
    case TraceRayMotionNV extends Definition(
          NonEmptyString("OpTraceRayMotionNV"),
          SPIRVOpcodeNumber(5339),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.TraceRayMotionNV(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7),
                    values.required(8),
                    values.required(9),
                    values.required(10),
                    values.required(11)
                  )
              ),
          InstructionMetadata2.m131
        )
    case RayQueryGetIntersectionTriangleVertexPositionsKHR extends Definition(
          NonEmptyString("OpRayQueryGetIntersectionTriangleVertexPositionsKHR"),
          SPIRVOpcodeNumber(5340),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.RayQueryGetIntersectionTriangleVertexPositionsKHR(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata2.m132
        )
    case TypeAccelerationStructureKHR extends Definition(
          NonEmptyString("OpTypeAccelerationStructureKHR"),
          SPIRVOpcodeNumber(5341),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeAccelerationStructureKHR(values.required(0))
              ),
          InstructionMetadata2.m133
        )
    case ExecuteCallableNV extends Definition(
          NonEmptyString("OpExecuteCallableNV"),
          SPIRVOpcodeNumber(5344),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ExecuteCallableNV(values.required(0), values.required(1))
              ),
          InstructionMetadata2.m134
        )
    case RayQueryGetIntersectionClusterIdNV extends Definition(
          NonEmptyString("OpRayQueryGetIntersectionClusterIdNV"),
          SPIRVOpcodeNumber(5345),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.RayQueryGetIntersectionClusterIdNV(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata2.m135
        )
    case HitObjectGetClusterIdNV extends Definition(
          NonEmptyString("OpHitObjectGetClusterIdNV"),
          SPIRVOpcodeNumber(5346),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetClusterIdNV(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m136
        )
    case HitObjectGetRayTMinEXT extends Definition(
          NonEmptyString("OpHitObjectGetRayTMinEXT"),
          SPIRVOpcodeNumber(5347),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.HitObjectGetRayTMinEXT(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m137
        )
    case HitObjectGetShaderBindingTableRecordIndexEXT extends Definition(
          NonEmptyString("OpHitObjectGetShaderBindingTableRecordIndexEXT"),
          SPIRVOpcodeNumber(5348),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetShaderBindingTableRecordIndexEXT(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata2.m138
        )
    case HitObjectGetShaderRecordBufferHandleEXT extends Definition(
          NonEmptyString("OpHitObjectGetShaderRecordBufferHandleEXT"),
          SPIRVOpcodeNumber(5349),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetShaderRecordBufferHandleEXT(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata2.m139
        )
    case HitObjectIsEmptyEXT extends Definition(
          NonEmptyString("OpHitObjectIsEmptyEXT"),
          SPIRVOpcodeNumber(5350),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.HitObjectIsEmptyEXT(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m140
        )
    case HitObjectIsHitEXT extends Definition(
          NonEmptyString("OpHitObjectIsHitEXT"),
          SPIRVOpcodeNumber(5351),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.HitObjectIsHitEXT(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m141
        )
    case HitObjectIsMissEXT extends Definition(
          NonEmptyString("OpHitObjectIsMissEXT"),
          SPIRVOpcodeNumber(5352),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.HitObjectIsMissEXT(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m142
        )
    case TypeCooperativeMatrixNV extends Definition(
          NonEmptyString("OpTypeCooperativeMatrixNV"),
          SPIRVOpcodeNumber(5358),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.TypeCooperativeMatrixNV(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata2.m143
        )
    case CooperativeMatrixLoadNV extends Definition(
          NonEmptyString("OpCooperativeMatrixLoadNV"),
          SPIRVOpcodeNumber(5359),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CooperativeMatrixLoadNV(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.optional(5)
                  )
              ),
          InstructionMetadata2.m144
        )
    case CooperativeMatrixStoreNV extends Definition(
          NonEmptyString("OpCooperativeMatrixStoreNV"),
          SPIRVOpcodeNumber(5360),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CooperativeMatrixStoreNV(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.optional(4)
                  )
              ),
          InstructionMetadata2.m145
        )
    case CooperativeMatrixMulAddNV extends Definition(
          NonEmptyString("OpCooperativeMatrixMulAddNV"),
          SPIRVOpcodeNumber(5361),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CooperativeMatrixMulAddNV(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata2.m146
        )
    case CooperativeMatrixLengthNV extends Definition(
          NonEmptyString("OpCooperativeMatrixLengthNV"),
          SPIRVOpcodeNumber(5362),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CooperativeMatrixLengthNV(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m147
        )
    case BeginInvocationInterlockEXT extends Definition(
          NonEmptyString("OpBeginInvocationInterlockEXT"),
          SPIRVOpcodeNumber(5364),
          definition =>
            raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.BeginInvocationInterlockEXT()),
          InstructionMetadata2.m148
        )
    case EndInvocationInterlockEXT extends Definition(
          NonEmptyString("OpEndInvocationInterlockEXT"),
          SPIRVOpcodeNumber(5365),
          definition =>
            raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.EndInvocationInterlockEXT()),
          InstructionMetadata2.m149
        )
    case CooperativeMatrixReduceNV extends Definition(
          NonEmptyString("OpCooperativeMatrixReduceNV"),
          SPIRVOpcodeNumber(5366),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CooperativeMatrixReduceNV(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata2.m150
        )
    case CooperativeMatrixLoadTensorNV extends Definition(
          NonEmptyString("OpCooperativeMatrixLoadTensorNV"),
          SPIRVOpcodeNumber(5367),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CooperativeMatrixLoadTensorNV(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6)
                  )
              ),
          InstructionMetadata2.m151
        )
    case CooperativeMatrixStoreTensorNV extends Definition(
          NonEmptyString("OpCooperativeMatrixStoreTensorNV"),
          SPIRVOpcodeNumber(5368),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CooperativeMatrixStoreTensorNV(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata2.m152
        )
    case CooperativeMatrixPerElementOpNV extends Definition(
          NonEmptyString("OpCooperativeMatrixPerElementOpNV"),
          SPIRVOpcodeNumber(5369),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CooperativeMatrixPerElementOpNV(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.repeated(4)
                  )
              ),
          InstructionMetadata2.m153
        )
    case TypeTensorLayoutNV extends Definition(
          NonEmptyString("OpTypeTensorLayoutNV"),
          SPIRVOpcodeNumber(5370),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeTensorLayoutNV(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m154
        )
    case TypeTensorViewNV extends Definition(
          NonEmptyString("OpTypeTensorViewNV"),
          SPIRVOpcodeNumber(5371),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.TypeTensorViewNV(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.repeated(3)
                  )
              ),
          InstructionMetadata2.m155
        )
    case CreateTensorLayoutNV extends Definition(
          NonEmptyString("OpCreateTensorLayoutNV"),
          SPIRVOpcodeNumber(5372),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.CreateTensorLayoutNV(values.required(0), values.required(1))
              ),
          InstructionMetadata2.m156
        )
    case TensorLayoutSetDimensionNV extends Definition(
          NonEmptyString("OpTensorLayoutSetDimensionNV"),
          SPIRVOpcodeNumber(5373),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.TensorLayoutSetDimensionNV(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.repeated(3)
                  )
              ),
          InstructionMetadata2.m157
        )
    case TensorLayoutSetStrideNV extends Definition(
          NonEmptyString("OpTensorLayoutSetStrideNV"),
          SPIRVOpcodeNumber(5374),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.TensorLayoutSetStrideNV(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.repeated(3)
                  )
              ),
          InstructionMetadata2.m158
        )
    case TensorLayoutSliceNV extends Definition(
          NonEmptyString("OpTensorLayoutSliceNV"),
          SPIRVOpcodeNumber(5375),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.TensorLayoutSliceNV(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.repeated(3)
                  )
              ),
          InstructionMetadata2.m159
        )
    case TensorLayoutSetClampValueNV extends Definition(
          NonEmptyString("OpTensorLayoutSetClampValueNV"),
          SPIRVOpcodeNumber(5376),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.TensorLayoutSetClampValueNV(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata2.m160
        )
    case CreateTensorViewNV extends Definition(
          NonEmptyString("OpCreateTensorViewNV"),
          SPIRVOpcodeNumber(5377),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.CreateTensorViewNV(values.required(0), values.required(1))
              ),
          InstructionMetadata2.m161
        )
    case TensorViewSetDimensionNV extends Definition(
          NonEmptyString("OpTensorViewSetDimensionNV"),
          SPIRVOpcodeNumber(5378),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.TensorViewSetDimensionNV(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.repeated(3)
                  )
              ),
          InstructionMetadata2.m162
        )
    case TensorViewSetStrideNV extends Definition(
          NonEmptyString("OpTensorViewSetStrideNV"),
          SPIRVOpcodeNumber(5379),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.TensorViewSetStrideNV(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.repeated(3)
                  )
              ),
          InstructionMetadata2.m163
        )
    case DemoteToHelperInvocation extends Definition(
          NonEmptyString("OpDemoteToHelperInvocation"),
          SPIRVOpcodeNumber(5380),
          definition =>
            raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.DemoteToHelperInvocation()),
          InstructionMetadata2.m164
        )
    case IsHelperInvocationEXT extends Definition(
          NonEmptyString("OpIsHelperInvocationEXT"),
          SPIRVOpcodeNumber(5381),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.IsHelperInvocationEXT(values.required(0), values.required(1))
              ),
          InstructionMetadata2.m165
        )
    case TensorViewSetClipNV extends Definition(
          NonEmptyString("OpTensorViewSetClipNV"),
          SPIRVOpcodeNumber(5382),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.TensorViewSetClipNV(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6)
                  )
              ),
          InstructionMetadata2.m166
        )
    case TensorLayoutSetBlockSizeNV extends Definition(
          NonEmptyString("OpTensorLayoutSetBlockSizeNV"),
          SPIRVOpcodeNumber(5384),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.TensorLayoutSetBlockSizeNV(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.repeated(3)
                  )
              ),
          InstructionMetadata2.m167
        )
    case CooperativeMatrixTransposeNV extends Definition(
          NonEmptyString("OpCooperativeMatrixTransposeNV"),
          SPIRVOpcodeNumber(5390),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CooperativeMatrixTransposeNV(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m168
        )
    case ConvertUToImageNV extends Definition(
          NonEmptyString("OpConvertUToImageNV"),
          SPIRVOpcodeNumber(5391),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ConvertUToImageNV(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m169
        )
    case ConvertUToSamplerNV extends Definition(
          NonEmptyString("OpConvertUToSamplerNV"),
          SPIRVOpcodeNumber(5392),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ConvertUToSamplerNV(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m170
        )
    case ConvertImageToUNV extends Definition(
          NonEmptyString("OpConvertImageToUNV"),
          SPIRVOpcodeNumber(5393),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ConvertImageToUNV(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m171
        )
    case ConvertSamplerToUNV extends Definition(
          NonEmptyString("OpConvertSamplerToUNV"),
          SPIRVOpcodeNumber(5394),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ConvertSamplerToUNV(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m172
        )
    case ConvertUToSampledImageNV extends Definition(
          NonEmptyString("OpConvertUToSampledImageNV"),
          SPIRVOpcodeNumber(5395),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ConvertUToSampledImageNV(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m173
        )
    case ConvertSampledImageToUNV extends Definition(
          NonEmptyString("OpConvertSampledImageToUNV"),
          SPIRVOpcodeNumber(5396),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ConvertSampledImageToUNV(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m174
        )
    case SamplerImageAddressingModeNV extends Definition(
          NonEmptyString("OpSamplerImageAddressingModeNV"),
          SPIRVOpcodeNumber(5397),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.SamplerImageAddressingModeNV(values.required(0))
              ),
          InstructionMetadata2.m175
        )
    case RawAccessChainNV extends Definition(
          NonEmptyString("OpRawAccessChainNV"),
          SPIRVOpcodeNumber(5398),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.RawAccessChainNV(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.optional(6)
                  )
              ),
          InstructionMetadata2.m176
        )
    case RayQueryGetIntersectionSpherePositionNV extends Definition(
          NonEmptyString("OpRayQueryGetIntersectionSpherePositionNV"),
          SPIRVOpcodeNumber(5427),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.RayQueryGetIntersectionSpherePositionNV(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata2.m177
        )
    case RayQueryGetIntersectionSphereRadiusNV extends Definition(
          NonEmptyString("OpRayQueryGetIntersectionSphereRadiusNV"),
          SPIRVOpcodeNumber(5428),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.RayQueryGetIntersectionSphereRadiusNV(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata2.m178
        )
    case RayQueryGetIntersectionLSSPositionsNV extends Definition(
          NonEmptyString("OpRayQueryGetIntersectionLSSPositionsNV"),
          SPIRVOpcodeNumber(5429),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.RayQueryGetIntersectionLSSPositionsNV(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata2.m179
        )
    case RayQueryGetIntersectionLSSRadiiNV extends Definition(
          NonEmptyString("OpRayQueryGetIntersectionLSSRadiiNV"),
          SPIRVOpcodeNumber(5430),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.RayQueryGetIntersectionLSSRadiiNV(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata2.m180
        )
    case RayQueryGetIntersectionLSSHitValueNV extends Definition(
          NonEmptyString("OpRayQueryGetIntersectionLSSHitValueNV"),
          SPIRVOpcodeNumber(5431),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.RayQueryGetIntersectionLSSHitValueNV(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata2.m181
        )
    case HitObjectGetSpherePositionNV extends Definition(
          NonEmptyString("OpHitObjectGetSpherePositionNV"),
          SPIRVOpcodeNumber(5432),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetSpherePositionNV(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m182
        )
    case HitObjectGetSphereRadiusNV extends Definition(
          NonEmptyString("OpHitObjectGetSphereRadiusNV"),
          SPIRVOpcodeNumber(5433),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetSphereRadiusNV(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m183
        )
    case HitObjectGetLSSPositionsNV extends Definition(
          NonEmptyString("OpHitObjectGetLSSPositionsNV"),
          SPIRVOpcodeNumber(5434),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetLSSPositionsNV(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m184
        )
    case HitObjectGetLSSRadiiNV extends Definition(
          NonEmptyString("OpHitObjectGetLSSRadiiNV"),
          SPIRVOpcodeNumber(5435),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.HitObjectGetLSSRadiiNV(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m185
        )
    case HitObjectIsSphereHitNV extends Definition(
          NonEmptyString("OpHitObjectIsSphereHitNV"),
          SPIRVOpcodeNumber(5436),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.HitObjectIsSphereHitNV(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m186
        )
    case HitObjectIsLSSHitNV extends Definition(
          NonEmptyString("OpHitObjectIsLSSHitNV"),
          SPIRVOpcodeNumber(5437),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.HitObjectIsLSSHitNV(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m187
        )
    case RayQueryIsSphereHitNV extends Definition(
          NonEmptyString("OpRayQueryIsSphereHitNV"),
          SPIRVOpcodeNumber(5438),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.RayQueryIsSphereHitNV(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata2.m188
        )
    case RayQueryIsLSSHitNV extends Definition(
          NonEmptyString("OpRayQueryIsLSSHitNV"),
          SPIRVOpcodeNumber(5439),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.RayQueryIsLSSHitNV(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata2.m189
        )
    case SubgroupShuffleINTEL extends Definition(
          NonEmptyString("OpSubgroupShuffleINTEL"),
          SPIRVOpcodeNumber(5571),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupShuffleINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata2.m190
        )
    case SubgroupShuffleDownINTEL extends Definition(
          NonEmptyString("OpSubgroupShuffleDownINTEL"),
          SPIRVOpcodeNumber(5572),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupShuffleDownINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata2.m191
        )
    case SubgroupShuffleUpINTEL extends Definition(
          NonEmptyString("OpSubgroupShuffleUpINTEL"),
          SPIRVOpcodeNumber(5573),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupShuffleUpINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata2.m192
        )
    case SubgroupShuffleXorINTEL extends Definition(
          NonEmptyString("OpSubgroupShuffleXorINTEL"),
          SPIRVOpcodeNumber(5574),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupShuffleXorINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata2.m193
        )
    case SubgroupBlockReadINTEL extends Definition(
          NonEmptyString("OpSubgroupBlockReadINTEL"),
          SPIRVOpcodeNumber(5575),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.SubgroupBlockReadINTEL(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m194
        )
    case SubgroupBlockWriteINTEL extends Definition(
          NonEmptyString("OpSubgroupBlockWriteINTEL"),
          SPIRVOpcodeNumber(5576),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.SubgroupBlockWriteINTEL(values.required(0), values.required(1))
              ),
          InstructionMetadata2.m195
        )
    case SubgroupImageBlockReadINTEL extends Definition(
          NonEmptyString("OpSubgroupImageBlockReadINTEL"),
          SPIRVOpcodeNumber(5577),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupImageBlockReadINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata2.m196
        )
    case SubgroupImageBlockWriteINTEL extends Definition(
          NonEmptyString("OpSubgroupImageBlockWriteINTEL"),
          SPIRVOpcodeNumber(5578),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupImageBlockWriteINTEL(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata2.m197
        )
    case SubgroupImageMediaBlockReadINTEL extends Definition(
          NonEmptyString("OpSubgroupImageMediaBlockReadINTEL"),
          SPIRVOpcodeNumber(5580),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupImageMediaBlockReadINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata2.m198
        )
    case SubgroupImageMediaBlockWriteINTEL extends Definition(
          NonEmptyString("OpSubgroupImageMediaBlockWriteINTEL"),
          SPIRVOpcodeNumber(5581),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupImageMediaBlockWriteINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata2.m199
        )
    case UCountLeadingZerosINTEL extends Definition(
          NonEmptyString("OpUCountLeadingZerosINTEL"),
          SPIRVOpcodeNumber(5585),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.UCountLeadingZerosINTEL(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata3.m0
        )
    case UCountTrailingZerosINTEL extends Definition(
          NonEmptyString("OpUCountTrailingZerosINTEL"),
          SPIRVOpcodeNumber(5586),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.UCountTrailingZerosINTEL(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata3.m1
        )
    case AbsISubINTEL extends Definition(
          NonEmptyString("OpAbsISubINTEL"),
          SPIRVOpcodeNumber(5587),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.AbsISubINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata3.m2
        )
    case AbsUSubINTEL extends Definition(
          NonEmptyString("OpAbsUSubINTEL"),
          SPIRVOpcodeNumber(5588),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.AbsUSubINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata3.m3
        )
    case IAddSatINTEL extends Definition(
          NonEmptyString("OpIAddSatINTEL"),
          SPIRVOpcodeNumber(5589),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.IAddSatINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata3.m4
        )
    case UAddSatINTEL extends Definition(
          NonEmptyString("OpUAddSatINTEL"),
          SPIRVOpcodeNumber(5590),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.UAddSatINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata3.m5
        )
    case IAverageINTEL extends Definition(
          NonEmptyString("OpIAverageINTEL"),
          SPIRVOpcodeNumber(5591),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.IAverageINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata3.m6
        )
    case UAverageINTEL extends Definition(
          NonEmptyString("OpUAverageINTEL"),
          SPIRVOpcodeNumber(5592),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.UAverageINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata3.m7
        )
    case IAverageRoundedINTEL extends Definition(
          NonEmptyString("OpIAverageRoundedINTEL"),
          SPIRVOpcodeNumber(5593),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.IAverageRoundedINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata3.m8
        )
    case UAverageRoundedINTEL extends Definition(
          NonEmptyString("OpUAverageRoundedINTEL"),
          SPIRVOpcodeNumber(5594),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.UAverageRoundedINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata3.m9
        )
    case ISubSatINTEL extends Definition(
          NonEmptyString("OpISubSatINTEL"),
          SPIRVOpcodeNumber(5595),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ISubSatINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata3.m10
        )
    case USubSatINTEL extends Definition(
          NonEmptyString("OpUSubSatINTEL"),
          SPIRVOpcodeNumber(5596),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.USubSatINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata3.m11
        )
    case IMul32x16INTEL extends Definition(
          NonEmptyString("OpIMul32x16INTEL"),
          SPIRVOpcodeNumber(5597),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.IMul32x16INTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata3.m12
        )
    case UMul32x16INTEL extends Definition(
          NonEmptyString("OpUMul32x16INTEL"),
          SPIRVOpcodeNumber(5598),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.UMul32x16INTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata3.m13
        )
    case ConstantFunctionPointerINTEL extends Definition(
          NonEmptyString("OpConstantFunctionPointerINTEL"),
          SPIRVOpcodeNumber(5600),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ConstantFunctionPointerINTEL(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata3.m14
        )
    case FunctionPointerCallINTEL extends Definition(
          NonEmptyString("OpFunctionPointerCallINTEL"),
          SPIRVOpcodeNumber(5601),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FunctionPointerCallINTEL(values.required(0), values.required(1), values.repeated(2))
              ),
          InstructionMetadata3.m15
        )
    case AsmTargetINTEL extends Definition(
          NonEmptyString("OpAsmTargetINTEL"),
          SPIRVOpcodeNumber(5609),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.AsmTargetINTEL(values.required(0), values.required(1))
              ),
          InstructionMetadata3.m16
        )
    case AsmINTEL extends Definition(
          NonEmptyString("OpAsmINTEL"),
          SPIRVOpcodeNumber(5610),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.AsmINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata3.m17
        )
    case AsmCallINTEL extends Definition(
          NonEmptyString("OpAsmCallINTEL"),
          SPIRVOpcodeNumber(5611),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.AsmCallINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.repeated(3)
                  )
              ),
          InstructionMetadata3.m18
        )
    case AtomicFMinEXT extends Definition(
          NonEmptyString("OpAtomicFMinEXT"),
          SPIRVOpcodeNumber(5614),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.AtomicFMinEXT(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata3.m19
        )
    case AtomicFMaxEXT extends Definition(
          NonEmptyString("OpAtomicFMaxEXT"),
          SPIRVOpcodeNumber(5615),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.AtomicFMaxEXT(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata3.m20
        )
    case AssumeTrueKHR extends Definition(
          NonEmptyString("OpAssumeTrueKHR"),
          SPIRVOpcodeNumber(5630),
          definition =>
            raw =>
              Instruction.decodeDefinition(definition, raw, values => Instruction.AssumeTrueKHR(values.required(0))),
          InstructionMetadata3.m21
        )
    case ExpectKHR extends Definition(
          NonEmptyString("OpExpectKHR"),
          SPIRVOpcodeNumber(5631),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ExpectKHR(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          InstructionMetadata3.m22
        )
    case DecorateString extends Definition(
          NonEmptyString("OpDecorateString"),
          SPIRVOpcodeNumber(5632),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.DecorateString(values.required(0), values.required(1))
              ),
          InstructionMetadata3.m23
        )
    case MemberDecorateString extends Definition(
          NonEmptyString("OpMemberDecorateString"),
          SPIRVOpcodeNumber(5633),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.MemberDecorateString(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata3.m24
        )
    case VmeImageINTEL extends Definition(
          NonEmptyString("OpVmeImageINTEL"),
          SPIRVOpcodeNumber(5699),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.VmeImageINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata3.m25
        )
    case TypeVmeImageINTEL extends Definition(
          NonEmptyString("OpTypeVmeImageINTEL"),
          SPIRVOpcodeNumber(5700),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeVmeImageINTEL(values.required(0), values.required(1))
              ),
          InstructionMetadata3.m26
        )
    case TypeAvcImePayloadINTEL extends Definition(
          NonEmptyString("OpTypeAvcImePayloadINTEL"),
          SPIRVOpcodeNumber(5701),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeAvcImePayloadINTEL(values.required(0))
              ),
          InstructionMetadata3.m27
        )
    case TypeAvcRefPayloadINTEL extends Definition(
          NonEmptyString("OpTypeAvcRefPayloadINTEL"),
          SPIRVOpcodeNumber(5702),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeAvcRefPayloadINTEL(values.required(0))
              ),
          InstructionMetadata3.m28
        )
    case TypeAvcSicPayloadINTEL extends Definition(
          NonEmptyString("OpTypeAvcSicPayloadINTEL"),
          SPIRVOpcodeNumber(5703),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeAvcSicPayloadINTEL(values.required(0))
              ),
          InstructionMetadata3.m29
        )
    case TypeAvcMcePayloadINTEL extends Definition(
          NonEmptyString("OpTypeAvcMcePayloadINTEL"),
          SPIRVOpcodeNumber(5704),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeAvcMcePayloadINTEL(values.required(0))
              ),
          InstructionMetadata3.m30
        )
    case TypeAvcMceResultINTEL extends Definition(
          NonEmptyString("OpTypeAvcMceResultINTEL"),
          SPIRVOpcodeNumber(5705),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeAvcMceResultINTEL(values.required(0))
              ),
          InstructionMetadata3.m31
        )
    case TypeAvcImeResultINTEL extends Definition(
          NonEmptyString("OpTypeAvcImeResultINTEL"),
          SPIRVOpcodeNumber(5706),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeAvcImeResultINTEL(values.required(0))
              ),
          InstructionMetadata3.m32
        )
    case TypeAvcImeResultSingleReferenceStreamoutINTEL extends Definition(
          NonEmptyString("OpTypeAvcImeResultSingleReferenceStreamoutINTEL"),
          SPIRVOpcodeNumber(5707),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeAvcImeResultSingleReferenceStreamoutINTEL(values.required(0))
              ),
          InstructionMetadata3.m33
        )
    case TypeAvcImeResultDualReferenceStreamoutINTEL extends Definition(
          NonEmptyString("OpTypeAvcImeResultDualReferenceStreamoutINTEL"),
          SPIRVOpcodeNumber(5708),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeAvcImeResultDualReferenceStreamoutINTEL(values.required(0))
              ),
          InstructionMetadata3.m34
        )
    case TypeAvcImeSingleReferenceStreaminINTEL extends Definition(
          NonEmptyString("OpTypeAvcImeSingleReferenceStreaminINTEL"),
          SPIRVOpcodeNumber(5709),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeAvcImeSingleReferenceStreaminINTEL(values.required(0))
              ),
          InstructionMetadata3.m35
        )
    case TypeAvcImeDualReferenceStreaminINTEL extends Definition(
          NonEmptyString("OpTypeAvcImeDualReferenceStreaminINTEL"),
          SPIRVOpcodeNumber(5710),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeAvcImeDualReferenceStreaminINTEL(values.required(0))
              ),
          InstructionMetadata3.m36
        )
    case TypeAvcRefResultINTEL extends Definition(
          NonEmptyString("OpTypeAvcRefResultINTEL"),
          SPIRVOpcodeNumber(5711),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeAvcRefResultINTEL(values.required(0))
              ),
          InstructionMetadata3.m37
        )
    case TypeAvcSicResultINTEL extends Definition(
          NonEmptyString("OpTypeAvcSicResultINTEL"),
          SPIRVOpcodeNumber(5712),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeAvcSicResultINTEL(values.required(0))
              ),
          InstructionMetadata3.m38
        )
    case SubgroupAvcMceGetDefaultInterBaseMultiReferencePenaltyINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcMceGetDefaultInterBaseMultiReferencePenaltyINTEL"),
          SPIRVOpcodeNumber(5713),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcMceGetDefaultInterBaseMultiReferencePenaltyINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata3.m39
        )
    case SubgroupAvcMceSetInterBaseMultiReferencePenaltyINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcMceSetInterBaseMultiReferencePenaltyINTEL"),
          SPIRVOpcodeNumber(5714),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcMceSetInterBaseMultiReferencePenaltyINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata3.m40
        )
    case SubgroupAvcMceGetDefaultInterShapePenaltyINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcMceGetDefaultInterShapePenaltyINTEL"),
          SPIRVOpcodeNumber(5715),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcMceGetDefaultInterShapePenaltyINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata3.m41
        )
    case SubgroupAvcMceSetInterShapePenaltyINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcMceSetInterShapePenaltyINTEL"),
          SPIRVOpcodeNumber(5716),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcMceSetInterShapePenaltyINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata3.m42
        )
    case SubgroupAvcMceGetDefaultInterDirectionPenaltyINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcMceGetDefaultInterDirectionPenaltyINTEL"),
          SPIRVOpcodeNumber(5717),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcMceGetDefaultInterDirectionPenaltyINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata3.m43
        )
    case SubgroupAvcMceSetInterDirectionPenaltyINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcMceSetInterDirectionPenaltyINTEL"),
          SPIRVOpcodeNumber(5718),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcMceSetInterDirectionPenaltyINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata3.m44
        )
    case SubgroupAvcMceGetDefaultIntraLumaShapePenaltyINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcMceGetDefaultIntraLumaShapePenaltyINTEL"),
          SPIRVOpcodeNumber(5719),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcMceGetDefaultIntraLumaShapePenaltyINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata3.m45
        )
    case SubgroupAvcMceGetDefaultInterMotionVectorCostTableINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcMceGetDefaultInterMotionVectorCostTableINTEL"),
          SPIRVOpcodeNumber(5720),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcMceGetDefaultInterMotionVectorCostTableINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata3.m46
        )
    case SubgroupAvcMceGetDefaultHighPenaltyCostTableINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcMceGetDefaultHighPenaltyCostTableINTEL"),
          SPIRVOpcodeNumber(5721),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcMceGetDefaultHighPenaltyCostTableINTEL(values.required(0), values.required(1))
              ),
          InstructionMetadata3.m47
        )
    case SubgroupAvcMceGetDefaultMediumPenaltyCostTableINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcMceGetDefaultMediumPenaltyCostTableINTEL"),
          SPIRVOpcodeNumber(5722),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcMceGetDefaultMediumPenaltyCostTableINTEL(
                    values.required(0),
                    values.required(1)
                  )
              ),
          InstructionMetadata3.m48
        )
    case SubgroupAvcMceGetDefaultLowPenaltyCostTableINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcMceGetDefaultLowPenaltyCostTableINTEL"),
          SPIRVOpcodeNumber(5723),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcMceGetDefaultLowPenaltyCostTableINTEL(values.required(0), values.required(1))
              ),
          InstructionMetadata3.m49
        )
    case SubgroupAvcMceSetMotionVectorCostFunctionINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcMceSetMotionVectorCostFunctionINTEL"),
          SPIRVOpcodeNumber(5724),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcMceSetMotionVectorCostFunctionINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata3.m50
        )
    case SubgroupAvcMceGetDefaultIntraLumaModePenaltyINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcMceGetDefaultIntraLumaModePenaltyINTEL"),
          SPIRVOpcodeNumber(5725),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcMceGetDefaultIntraLumaModePenaltyINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata3.m51
        )
    case SubgroupAvcMceGetDefaultNonDcLumaIntraPenaltyINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcMceGetDefaultNonDcLumaIntraPenaltyINTEL"),
          SPIRVOpcodeNumber(5726),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcMceGetDefaultNonDcLumaIntraPenaltyINTEL(values.required(0), values.required(1))
              ),
          InstructionMetadata3.m52
        )
    case SubgroupAvcMceGetDefaultIntraChromaModeBasePenaltyINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcMceGetDefaultIntraChromaModeBasePenaltyINTEL"),
          SPIRVOpcodeNumber(5727),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcMceGetDefaultIntraChromaModeBasePenaltyINTEL(
                    values.required(0),
                    values.required(1)
                  )
              ),
          InstructionMetadata3.m53
        )
    case SubgroupAvcMceSetAcOnlyHaarINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcMceSetAcOnlyHaarINTEL"),
          SPIRVOpcodeNumber(5728),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcMceSetAcOnlyHaarINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata3.m54
        )
    case SubgroupAvcMceSetSourceInterlacedFieldPolarityINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcMceSetSourceInterlacedFieldPolarityINTEL"),
          SPIRVOpcodeNumber(5729),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcMceSetSourceInterlacedFieldPolarityINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata3.m55
        )
    case SubgroupAvcMceSetSingleReferenceInterlacedFieldPolarityINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcMceSetSingleReferenceInterlacedFieldPolarityINTEL"),
          SPIRVOpcodeNumber(5730),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcMceSetSingleReferenceInterlacedFieldPolarityINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata3.m56
        )
    case SubgroupAvcMceSetDualReferenceInterlacedFieldPolaritiesINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcMceSetDualReferenceInterlacedFieldPolaritiesINTEL"),
          SPIRVOpcodeNumber(5731),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcMceSetDualReferenceInterlacedFieldPolaritiesINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata3.m57
        )
    case SubgroupAvcMceConvertToImePayloadINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcMceConvertToImePayloadINTEL"),
          SPIRVOpcodeNumber(5732),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcMceConvertToImePayloadINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata3.m58
        )
    case SubgroupAvcMceConvertToImeResultINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcMceConvertToImeResultINTEL"),
          SPIRVOpcodeNumber(5733),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcMceConvertToImeResultINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata3.m59
        )
    case SubgroupAvcMceConvertToRefPayloadINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcMceConvertToRefPayloadINTEL"),
          SPIRVOpcodeNumber(5734),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcMceConvertToRefPayloadINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata3.m60
        )
    case SubgroupAvcMceConvertToRefResultINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcMceConvertToRefResultINTEL"),
          SPIRVOpcodeNumber(5735),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcMceConvertToRefResultINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata3.m61
        )
    case SubgroupAvcMceConvertToSicPayloadINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcMceConvertToSicPayloadINTEL"),
          SPIRVOpcodeNumber(5736),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcMceConvertToSicPayloadINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata3.m62
        )
    case SubgroupAvcMceConvertToSicResultINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcMceConvertToSicResultINTEL"),
          SPIRVOpcodeNumber(5737),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcMceConvertToSicResultINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata3.m63
        )
    case SubgroupAvcMceGetMotionVectorsINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcMceGetMotionVectorsINTEL"),
          SPIRVOpcodeNumber(5738),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcMceGetMotionVectorsINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata3.m64
        )
    case SubgroupAvcMceGetInterDistortionsINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcMceGetInterDistortionsINTEL"),
          SPIRVOpcodeNumber(5739),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcMceGetInterDistortionsINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata3.m65
        )
    case SubgroupAvcMceGetBestInterDistortionsINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcMceGetBestInterDistortionsINTEL"),
          SPIRVOpcodeNumber(5740),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcMceGetBestInterDistortionsINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata3.m66
        )
    case SubgroupAvcMceGetInterMajorShapeINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcMceGetInterMajorShapeINTEL"),
          SPIRVOpcodeNumber(5741),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcMceGetInterMajorShapeINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata3.m67
        )
    case SubgroupAvcMceGetInterMinorShapeINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcMceGetInterMinorShapeINTEL"),
          SPIRVOpcodeNumber(5742),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcMceGetInterMinorShapeINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata3.m68
        )
    case SubgroupAvcMceGetInterDirectionsINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcMceGetInterDirectionsINTEL"),
          SPIRVOpcodeNumber(5743),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcMceGetInterDirectionsINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata3.m69
        )
    case SubgroupAvcMceGetInterMotionVectorCountINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcMceGetInterMotionVectorCountINTEL"),
          SPIRVOpcodeNumber(5744),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcMceGetInterMotionVectorCountINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata3.m70
        )
    case SubgroupAvcMceGetInterReferenceIdsINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcMceGetInterReferenceIdsINTEL"),
          SPIRVOpcodeNumber(5745),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcMceGetInterReferenceIdsINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata3.m71
        )
    case SubgroupAvcMceGetInterReferenceInterlacedFieldPolaritiesINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcMceGetInterReferenceInterlacedFieldPolaritiesINTEL"),
          SPIRVOpcodeNumber(5746),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcMceGetInterReferenceInterlacedFieldPolaritiesINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata3.m72
        )
    case SubgroupAvcImeInitializeINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcImeInitializeINTEL"),
          SPIRVOpcodeNumber(5747),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcImeInitializeINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata3.m73
        )
    case SubgroupAvcImeSetSingleReferenceINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcImeSetSingleReferenceINTEL"),
          SPIRVOpcodeNumber(5748),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcImeSetSingleReferenceINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata3.m74
        )
    case SubgroupAvcImeSetDualReferenceINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcImeSetDualReferenceINTEL"),
          SPIRVOpcodeNumber(5749),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcImeSetDualReferenceINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata3.m75
        )
    case SubgroupAvcImeRefWindowSizeINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcImeRefWindowSizeINTEL"),
          SPIRVOpcodeNumber(5750),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcImeRefWindowSizeINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata3.m76
        )
    case SubgroupAvcImeAdjustRefOffsetINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcImeAdjustRefOffsetINTEL"),
          SPIRVOpcodeNumber(5751),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcImeAdjustRefOffsetINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata3.m77
        )
    case SubgroupAvcImeConvertToMcePayloadINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcImeConvertToMcePayloadINTEL"),
          SPIRVOpcodeNumber(5752),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcImeConvertToMcePayloadINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata3.m78
        )
    case SubgroupAvcImeSetMaxMotionVectorCountINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcImeSetMaxMotionVectorCountINTEL"),
          SPIRVOpcodeNumber(5753),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcImeSetMaxMotionVectorCountINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata3.m79
        )
    case SubgroupAvcImeSetUnidirectionalMixDisableINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcImeSetUnidirectionalMixDisableINTEL"),
          SPIRVOpcodeNumber(5754),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcImeSetUnidirectionalMixDisableINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata3.m80
        )
    case SubgroupAvcImeSetEarlySearchTerminationThresholdINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcImeSetEarlySearchTerminationThresholdINTEL"),
          SPIRVOpcodeNumber(5755),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcImeSetEarlySearchTerminationThresholdINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata3.m81
        )
    case SubgroupAvcImeSetWeightedSadINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcImeSetWeightedSadINTEL"),
          SPIRVOpcodeNumber(5756),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcImeSetWeightedSadINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata3.m82
        )
    case SubgroupAvcImeEvaluateWithSingleReferenceINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcImeEvaluateWithSingleReferenceINTEL"),
          SPIRVOpcodeNumber(5757),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcImeEvaluateWithSingleReferenceINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata3.m83
        )
    case SubgroupAvcImeEvaluateWithDualReferenceINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcImeEvaluateWithDualReferenceINTEL"),
          SPIRVOpcodeNumber(5758),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcImeEvaluateWithDualReferenceINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata3.m84
        )
    case SubgroupAvcImeEvaluateWithSingleReferenceStreaminINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcImeEvaluateWithSingleReferenceStreaminINTEL"),
          SPIRVOpcodeNumber(5759),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcImeEvaluateWithSingleReferenceStreaminINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata3.m85
        )
    case SubgroupAvcImeEvaluateWithDualReferenceStreaminINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcImeEvaluateWithDualReferenceStreaminINTEL"),
          SPIRVOpcodeNumber(5760),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcImeEvaluateWithDualReferenceStreaminINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6)
                  )
              ),
          InstructionMetadata3.m86
        )
    case SubgroupAvcImeEvaluateWithSingleReferenceStreamoutINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcImeEvaluateWithSingleReferenceStreamoutINTEL"),
          SPIRVOpcodeNumber(5761),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcImeEvaluateWithSingleReferenceStreamoutINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata3.m87
        )
    case SubgroupAvcImeEvaluateWithDualReferenceStreamoutINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcImeEvaluateWithDualReferenceStreamoutINTEL"),
          SPIRVOpcodeNumber(5762),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcImeEvaluateWithDualReferenceStreamoutINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata3.m88
        )
    case SubgroupAvcImeEvaluateWithSingleReferenceStreaminoutINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcImeEvaluateWithSingleReferenceStreaminoutINTEL"),
          SPIRVOpcodeNumber(5763),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcImeEvaluateWithSingleReferenceStreaminoutINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata3.m89
        )
    case SubgroupAvcImeEvaluateWithDualReferenceStreaminoutINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcImeEvaluateWithDualReferenceStreaminoutINTEL"),
          SPIRVOpcodeNumber(5764),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcImeEvaluateWithDualReferenceStreaminoutINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6)
                  )
              ),
          InstructionMetadata3.m90
        )
    case SubgroupAvcImeConvertToMceResultINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcImeConvertToMceResultINTEL"),
          SPIRVOpcodeNumber(5765),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcImeConvertToMceResultINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata3.m91
        )
    case SubgroupAvcImeGetSingleReferenceStreaminINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcImeGetSingleReferenceStreaminINTEL"),
          SPIRVOpcodeNumber(5766),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcImeGetSingleReferenceStreaminINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata3.m92
        )
    case SubgroupAvcImeGetDualReferenceStreaminINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcImeGetDualReferenceStreaminINTEL"),
          SPIRVOpcodeNumber(5767),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcImeGetDualReferenceStreaminINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata3.m93
        )
    case SubgroupAvcImeStripSingleReferenceStreamoutINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcImeStripSingleReferenceStreamoutINTEL"),
          SPIRVOpcodeNumber(5768),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcImeStripSingleReferenceStreamoutINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata3.m94
        )
    case SubgroupAvcImeStripDualReferenceStreamoutINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcImeStripDualReferenceStreamoutINTEL"),
          SPIRVOpcodeNumber(5769),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcImeStripDualReferenceStreamoutINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata3.m95
        )
    case SubgroupAvcImeGetStreamoutSingleReferenceMajorShapeMotionVectorsINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcImeGetStreamoutSingleReferenceMajorShapeMotionVectorsINTEL"),
          SPIRVOpcodeNumber(5770),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcImeGetStreamoutSingleReferenceMajorShapeMotionVectorsINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata3.m96
        )
    case SubgroupAvcImeGetStreamoutSingleReferenceMajorShapeDistortionsINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcImeGetStreamoutSingleReferenceMajorShapeDistortionsINTEL"),
          SPIRVOpcodeNumber(5771),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcImeGetStreamoutSingleReferenceMajorShapeDistortionsINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata3.m97
        )
    case SubgroupAvcImeGetStreamoutSingleReferenceMajorShapeReferenceIdsINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcImeGetStreamoutSingleReferenceMajorShapeReferenceIdsINTEL"),
          SPIRVOpcodeNumber(5772),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcImeGetStreamoutSingleReferenceMajorShapeReferenceIdsINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata3.m98
        )
    case SubgroupAvcImeGetStreamoutDualReferenceMajorShapeMotionVectorsINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcImeGetStreamoutDualReferenceMajorShapeMotionVectorsINTEL"),
          SPIRVOpcodeNumber(5773),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcImeGetStreamoutDualReferenceMajorShapeMotionVectorsINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata3.m99
        )
    case SubgroupAvcImeGetStreamoutDualReferenceMajorShapeDistortionsINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcImeGetStreamoutDualReferenceMajorShapeDistortionsINTEL"),
          SPIRVOpcodeNumber(5774),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcImeGetStreamoutDualReferenceMajorShapeDistortionsINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata3.m100
        )
    case SubgroupAvcImeGetStreamoutDualReferenceMajorShapeReferenceIdsINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcImeGetStreamoutDualReferenceMajorShapeReferenceIdsINTEL"),
          SPIRVOpcodeNumber(5775),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcImeGetStreamoutDualReferenceMajorShapeReferenceIdsINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata3.m101
        )
    case SubgroupAvcImeGetBorderReachedINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcImeGetBorderReachedINTEL"),
          SPIRVOpcodeNumber(5776),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcImeGetBorderReachedINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata3.m102
        )
    case SubgroupAvcImeGetTruncatedSearchIndicationINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcImeGetTruncatedSearchIndicationINTEL"),
          SPIRVOpcodeNumber(5777),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcImeGetTruncatedSearchIndicationINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata3.m103
        )
    case SubgroupAvcImeGetUnidirectionalEarlySearchTerminationINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcImeGetUnidirectionalEarlySearchTerminationINTEL"),
          SPIRVOpcodeNumber(5778),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcImeGetUnidirectionalEarlySearchTerminationINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata3.m104
        )
    case SubgroupAvcImeGetWeightingPatternMinimumMotionVectorINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcImeGetWeightingPatternMinimumMotionVectorINTEL"),
          SPIRVOpcodeNumber(5779),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcImeGetWeightingPatternMinimumMotionVectorINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata3.m105
        )
    case SubgroupAvcImeGetWeightingPatternMinimumDistortionINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcImeGetWeightingPatternMinimumDistortionINTEL"),
          SPIRVOpcodeNumber(5780),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcImeGetWeightingPatternMinimumDistortionINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata3.m106
        )
    case SubgroupAvcFmeInitializeINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcFmeInitializeINTEL"),
          SPIRVOpcodeNumber(5781),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcFmeInitializeINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7),
                    values.required(8)
                  )
              ),
          InstructionMetadata3.m107
        )
    case SubgroupAvcBmeInitializeINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcBmeInitializeINTEL"),
          SPIRVOpcodeNumber(5782),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcBmeInitializeINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7),
                    values.required(8),
                    values.required(9)
                  )
              ),
          InstructionMetadata3.m108
        )
    case SubgroupAvcRefConvertToMcePayloadINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcRefConvertToMcePayloadINTEL"),
          SPIRVOpcodeNumber(5783),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcRefConvertToMcePayloadINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata3.m109
        )
    case SubgroupAvcRefSetBidirectionalMixDisableINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcRefSetBidirectionalMixDisableINTEL"),
          SPIRVOpcodeNumber(5784),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcRefSetBidirectionalMixDisableINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata3.m110
        )
    case SubgroupAvcRefSetBilinearFilterEnableINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcRefSetBilinearFilterEnableINTEL"),
          SPIRVOpcodeNumber(5785),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcRefSetBilinearFilterEnableINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata3.m111
        )
    case SubgroupAvcRefEvaluateWithSingleReferenceINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcRefEvaluateWithSingleReferenceINTEL"),
          SPIRVOpcodeNumber(5786),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcRefEvaluateWithSingleReferenceINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata3.m112
        )
    case SubgroupAvcRefEvaluateWithDualReferenceINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcRefEvaluateWithDualReferenceINTEL"),
          SPIRVOpcodeNumber(5787),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcRefEvaluateWithDualReferenceINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata3.m113
        )
    case SubgroupAvcRefEvaluateWithMultiReferenceINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcRefEvaluateWithMultiReferenceINTEL"),
          SPIRVOpcodeNumber(5788),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcRefEvaluateWithMultiReferenceINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata3.m114
        )
    case SubgroupAvcRefEvaluateWithMultiReferenceInterlacedINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcRefEvaluateWithMultiReferenceInterlacedINTEL"),
          SPIRVOpcodeNumber(5789),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcRefEvaluateWithMultiReferenceInterlacedINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata3.m115
        )
    case SubgroupAvcRefConvertToMceResultINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcRefConvertToMceResultINTEL"),
          SPIRVOpcodeNumber(5790),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcRefConvertToMceResultINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata3.m116
        )
    case SubgroupAvcSicInitializeINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcSicInitializeINTEL"),
          SPIRVOpcodeNumber(5791),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcSicInitializeINTEL(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata3.m117
        )
    case SubgroupAvcSicConfigureSkcINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcSicConfigureSkcINTEL"),
          SPIRVOpcodeNumber(5792),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcSicConfigureSkcINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7)
                  )
              ),
          InstructionMetadata3.m118
        )
    case SubgroupAvcSicConfigureIpeLumaINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcSicConfigureIpeLumaINTEL"),
          SPIRVOpcodeNumber(5793),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcSicConfigureIpeLumaINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7),
                    values.required(8),
                    values.required(9)
                  )
              ),
          InstructionMetadata3.m119
        )
    case SubgroupAvcSicConfigureIpeLumaChromaINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcSicConfigureIpeLumaChromaINTEL"),
          SPIRVOpcodeNumber(5794),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcSicConfigureIpeLumaChromaINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7),
                    values.required(8),
                    values.required(9),
                    values.required(10),
                    values.required(11),
                    values.required(12)
                  )
              ),
          InstructionMetadata3.m120
        )
    case SubgroupAvcSicGetMotionVectorMaskINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcSicGetMotionVectorMaskINTEL"),
          SPIRVOpcodeNumber(5795),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcSicGetMotionVectorMaskINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata3.m121
        )
    case SubgroupAvcSicConvertToMcePayloadINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcSicConvertToMcePayloadINTEL"),
          SPIRVOpcodeNumber(5796),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcSicConvertToMcePayloadINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata3.m122
        )
    case SubgroupAvcSicSetIntraLumaShapePenaltyINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcSicSetIntraLumaShapePenaltyINTEL"),
          SPIRVOpcodeNumber(5797),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcSicSetIntraLumaShapePenaltyINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata3.m123
        )
    case SubgroupAvcSicSetIntraLumaModeCostFunctionINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcSicSetIntraLumaModeCostFunctionINTEL"),
          SPIRVOpcodeNumber(5798),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcSicSetIntraLumaModeCostFunctionINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata3.m124
        )
    case SubgroupAvcSicSetIntraChromaModeCostFunctionINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcSicSetIntraChromaModeCostFunctionINTEL"),
          SPIRVOpcodeNumber(5799),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcSicSetIntraChromaModeCostFunctionINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata3.m125
        )
    case SubgroupAvcSicSetBilinearFilterEnableINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcSicSetBilinearFilterEnableINTEL"),
          SPIRVOpcodeNumber(5800),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcSicSetBilinearFilterEnableINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata3.m126
        )
    case SubgroupAvcSicSetSkcForwardTransformEnableINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcSicSetSkcForwardTransformEnableINTEL"),
          SPIRVOpcodeNumber(5801),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcSicSetSkcForwardTransformEnableINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata3.m127
        )
    case SubgroupAvcSicSetBlockBasedRawSkipSadINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcSicSetBlockBasedRawSkipSadINTEL"),
          SPIRVOpcodeNumber(5802),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcSicSetBlockBasedRawSkipSadINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata3.m128
        )
    case SubgroupAvcSicEvaluateIpeINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcSicEvaluateIpeINTEL"),
          SPIRVOpcodeNumber(5803),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcSicEvaluateIpeINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata3.m129
        )
    case SubgroupAvcSicEvaluateWithSingleReferenceINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcSicEvaluateWithSingleReferenceINTEL"),
          SPIRVOpcodeNumber(5804),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcSicEvaluateWithSingleReferenceINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata3.m130
        )
    case SubgroupAvcSicEvaluateWithDualReferenceINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcSicEvaluateWithDualReferenceINTEL"),
          SPIRVOpcodeNumber(5805),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcSicEvaluateWithDualReferenceINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata3.m131
        )
    case SubgroupAvcSicEvaluateWithMultiReferenceINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcSicEvaluateWithMultiReferenceINTEL"),
          SPIRVOpcodeNumber(5806),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcSicEvaluateWithMultiReferenceINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata3.m132
        )
    case SubgroupAvcSicEvaluateWithMultiReferenceInterlacedINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcSicEvaluateWithMultiReferenceInterlacedINTEL"),
          SPIRVOpcodeNumber(5807),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcSicEvaluateWithMultiReferenceInterlacedINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata3.m133
        )
    case SubgroupAvcSicConvertToMceResultINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcSicConvertToMceResultINTEL"),
          SPIRVOpcodeNumber(5808),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcSicConvertToMceResultINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata3.m134
        )
    case SubgroupAvcSicGetIpeLumaShapeINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcSicGetIpeLumaShapeINTEL"),
          SPIRVOpcodeNumber(5809),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcSicGetIpeLumaShapeINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata3.m135
        )
    case SubgroupAvcSicGetBestIpeLumaDistortionINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcSicGetBestIpeLumaDistortionINTEL"),
          SPIRVOpcodeNumber(5810),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcSicGetBestIpeLumaDistortionINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata3.m136
        )
    case SubgroupAvcSicGetBestIpeChromaDistortionINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcSicGetBestIpeChromaDistortionINTEL"),
          SPIRVOpcodeNumber(5811),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcSicGetBestIpeChromaDistortionINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata3.m137
        )
    case SubgroupAvcSicGetPackedIpeLumaModesINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcSicGetPackedIpeLumaModesINTEL"),
          SPIRVOpcodeNumber(5812),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcSicGetPackedIpeLumaModesINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata3.m138
        )
    case SubgroupAvcSicGetIpeChromaModeINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcSicGetIpeChromaModeINTEL"),
          SPIRVOpcodeNumber(5813),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcSicGetIpeChromaModeINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata3.m139
        )
    case SubgroupAvcSicGetPackedSkcLumaCountThresholdINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcSicGetPackedSkcLumaCountThresholdINTEL"),
          SPIRVOpcodeNumber(5814),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcSicGetPackedSkcLumaCountThresholdINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata3.m140
        )
    case SubgroupAvcSicGetPackedSkcLumaSumThresholdINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcSicGetPackedSkcLumaSumThresholdINTEL"),
          SPIRVOpcodeNumber(5815),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcSicGetPackedSkcLumaSumThresholdINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata3.m141
        )
    case SubgroupAvcSicGetInterRawSadsINTEL extends Definition(
          NonEmptyString("OpSubgroupAvcSicGetInterRawSadsINTEL"),
          SPIRVOpcodeNumber(5816),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcSicGetInterRawSadsINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata3.m142
        )
    case VariableLengthArrayINTEL extends Definition(
          NonEmptyString("OpVariableLengthArrayINTEL"),
          SPIRVOpcodeNumber(5818),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.VariableLengthArrayINTEL(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata3.m143
        )
    case SaveMemoryINTEL extends Definition(
          NonEmptyString("OpSaveMemoryINTEL"),
          SPIRVOpcodeNumber(5819),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.SaveMemoryINTEL(values.required(0), values.required(1))
              ),
          InstructionMetadata3.m144
        )
    case RestoreMemoryINTEL extends Definition(
          NonEmptyString("OpRestoreMemoryINTEL"),
          SPIRVOpcodeNumber(5820),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.RestoreMemoryINTEL(values.required(0))
              ),
          InstructionMetadata3.m145
        )
    case ArbitraryFloatSinCosPiALTERA extends Definition(
          NonEmptyString("OpArbitraryFloatSinCosPiALTERA"),
          SPIRVOpcodeNumber(5840),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatSinCosPiALTERA(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7)
                  )
              ),
          InstructionMetadata3.m146
        )
    case ArbitraryFloatCastALTERA extends Definition(
          NonEmptyString("OpArbitraryFloatCastALTERA"),
          SPIRVOpcodeNumber(5841),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatCastALTERA(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7)
                  )
              ),
          InstructionMetadata3.m147
        )
    case ArbitraryFloatCastFromIntALTERA extends Definition(
          NonEmptyString("OpArbitraryFloatCastFromIntALTERA"),
          SPIRVOpcodeNumber(5842),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatCastFromIntALTERA(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7)
                  )
              ),
          InstructionMetadata3.m148
        )
    case ArbitraryFloatCastToIntALTERA extends Definition(
          NonEmptyString("OpArbitraryFloatCastToIntALTERA"),
          SPIRVOpcodeNumber(5843),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatCastToIntALTERA(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7)
                  )
              ),
          InstructionMetadata3.m149
        )
    case ArbitraryFloatAddALTERA extends Definition(
          NonEmptyString("OpArbitraryFloatAddALTERA"),
          SPIRVOpcodeNumber(5846),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatAddALTERA(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7),
                    values.required(8),
                    values.required(9)
                  )
              ),
          InstructionMetadata3.m150
        )
    case ArbitraryFloatSubALTERA extends Definition(
          NonEmptyString("OpArbitraryFloatSubALTERA"),
          SPIRVOpcodeNumber(5847),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatSubALTERA(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7),
                    values.required(8),
                    values.required(9)
                  )
              ),
          InstructionMetadata3.m151
        )
    case ArbitraryFloatMulALTERA extends Definition(
          NonEmptyString("OpArbitraryFloatMulALTERA"),
          SPIRVOpcodeNumber(5848),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatMulALTERA(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7),
                    values.required(8),
                    values.required(9)
                  )
              ),
          InstructionMetadata3.m152
        )
    case ArbitraryFloatDivALTERA extends Definition(
          NonEmptyString("OpArbitraryFloatDivALTERA"),
          SPIRVOpcodeNumber(5849),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatDivALTERA(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7),
                    values.required(8),
                    values.required(9)
                  )
              ),
          InstructionMetadata3.m153
        )
    case ArbitraryFloatGTALTERA extends Definition(
          NonEmptyString("OpArbitraryFloatGTALTERA"),
          SPIRVOpcodeNumber(5850),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatGTALTERA(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata3.m154
        )
    case ArbitraryFloatGEALTERA extends Definition(
          NonEmptyString("OpArbitraryFloatGEALTERA"),
          SPIRVOpcodeNumber(5851),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatGEALTERA(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata3.m155
        )
    case ArbitraryFloatLTALTERA extends Definition(
          NonEmptyString("OpArbitraryFloatLTALTERA"),
          SPIRVOpcodeNumber(5852),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatLTALTERA(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata3.m156
        )
    case ArbitraryFloatLEALTERA extends Definition(
          NonEmptyString("OpArbitraryFloatLEALTERA"),
          SPIRVOpcodeNumber(5853),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatLEALTERA(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata3.m157
        )
    case ArbitraryFloatEQALTERA extends Definition(
          NonEmptyString("OpArbitraryFloatEQALTERA"),
          SPIRVOpcodeNumber(5854),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatEQALTERA(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata3.m158
        )
    case ArbitraryFloatRecipALTERA extends Definition(
          NonEmptyString("OpArbitraryFloatRecipALTERA"),
          SPIRVOpcodeNumber(5855),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatRecipALTERA(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7)
                  )
              ),
          InstructionMetadata3.m159
        )
    case ArbitraryFloatRSqrtALTERA extends Definition(
          NonEmptyString("OpArbitraryFloatRSqrtALTERA"),
          SPIRVOpcodeNumber(5856),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatRSqrtALTERA(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7)
                  )
              ),
          InstructionMetadata3.m160
        )
    case ArbitraryFloatCbrtALTERA extends Definition(
          NonEmptyString("OpArbitraryFloatCbrtALTERA"),
          SPIRVOpcodeNumber(5857),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatCbrtALTERA(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7)
                  )
              ),
          InstructionMetadata3.m161
        )
    case ArbitraryFloatHypotALTERA extends Definition(
          NonEmptyString("OpArbitraryFloatHypotALTERA"),
          SPIRVOpcodeNumber(5858),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatHypotALTERA(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7),
                    values.required(8),
                    values.required(9)
                  )
              ),
          InstructionMetadata3.m162
        )
    case ArbitraryFloatSqrtALTERA extends Definition(
          NonEmptyString("OpArbitraryFloatSqrtALTERA"),
          SPIRVOpcodeNumber(5859),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatSqrtALTERA(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7)
                  )
              ),
          InstructionMetadata3.m163
        )
    case ArbitraryFloatLogINTEL extends Definition(
          NonEmptyString("OpArbitraryFloatLogINTEL"),
          SPIRVOpcodeNumber(5860),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatLogINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7)
                  )
              ),
          InstructionMetadata3.m164
        )
    case ArbitraryFloatLog2INTEL extends Definition(
          NonEmptyString("OpArbitraryFloatLog2INTEL"),
          SPIRVOpcodeNumber(5861),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatLog2INTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7)
                  )
              ),
          InstructionMetadata3.m165
        )
    case ArbitraryFloatLog10INTEL extends Definition(
          NonEmptyString("OpArbitraryFloatLog10INTEL"),
          SPIRVOpcodeNumber(5862),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatLog10INTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7)
                  )
              ),
          InstructionMetadata3.m166
        )
    case ArbitraryFloatLog1pINTEL extends Definition(
          NonEmptyString("OpArbitraryFloatLog1pINTEL"),
          SPIRVOpcodeNumber(5863),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatLog1pINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7)
                  )
              ),
          InstructionMetadata3.m167
        )
    case ArbitraryFloatExpINTEL extends Definition(
          NonEmptyString("OpArbitraryFloatExpINTEL"),
          SPIRVOpcodeNumber(5864),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatExpINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7)
                  )
              ),
          InstructionMetadata3.m168
        )
    case ArbitraryFloatExp2INTEL extends Definition(
          NonEmptyString("OpArbitraryFloatExp2INTEL"),
          SPIRVOpcodeNumber(5865),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatExp2INTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7)
                  )
              ),
          InstructionMetadata3.m169
        )
    case ArbitraryFloatExp10INTEL extends Definition(
          NonEmptyString("OpArbitraryFloatExp10INTEL"),
          SPIRVOpcodeNumber(5866),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatExp10INTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7)
                  )
              ),
          InstructionMetadata3.m170
        )
    case ArbitraryFloatExpm1INTEL extends Definition(
          NonEmptyString("OpArbitraryFloatExpm1INTEL"),
          SPIRVOpcodeNumber(5867),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatExpm1INTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7)
                  )
              ),
          InstructionMetadata3.m171
        )
    case ArbitraryFloatSinINTEL extends Definition(
          NonEmptyString("OpArbitraryFloatSinINTEL"),
          SPIRVOpcodeNumber(5868),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatSinINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7)
                  )
              ),
          InstructionMetadata3.m172
        )
    case ArbitraryFloatCosINTEL extends Definition(
          NonEmptyString("OpArbitraryFloatCosINTEL"),
          SPIRVOpcodeNumber(5869),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatCosINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7)
                  )
              ),
          InstructionMetadata3.m173
        )
    case ArbitraryFloatSinCosINTEL extends Definition(
          NonEmptyString("OpArbitraryFloatSinCosINTEL"),
          SPIRVOpcodeNumber(5870),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatSinCosINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7)
                  )
              ),
          InstructionMetadata3.m174
        )
    case ArbitraryFloatSinPiINTEL extends Definition(
          NonEmptyString("OpArbitraryFloatSinPiINTEL"),
          SPIRVOpcodeNumber(5871),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatSinPiINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7)
                  )
              ),
          InstructionMetadata3.m175
        )
    case ArbitraryFloatCosPiINTEL extends Definition(
          NonEmptyString("OpArbitraryFloatCosPiINTEL"),
          SPIRVOpcodeNumber(5872),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatCosPiINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7)
                  )
              ),
          InstructionMetadata3.m176
        )
    case ArbitraryFloatASinINTEL extends Definition(
          NonEmptyString("OpArbitraryFloatASinINTEL"),
          SPIRVOpcodeNumber(5873),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatASinINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7)
                  )
              ),
          InstructionMetadata3.m177
        )
    case ArbitraryFloatASinPiINTEL extends Definition(
          NonEmptyString("OpArbitraryFloatASinPiINTEL"),
          SPIRVOpcodeNumber(5874),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatASinPiINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7)
                  )
              ),
          InstructionMetadata3.m178
        )
    case ArbitraryFloatACosINTEL extends Definition(
          NonEmptyString("OpArbitraryFloatACosINTEL"),
          SPIRVOpcodeNumber(5875),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatACosINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7)
                  )
              ),
          InstructionMetadata3.m179
        )
    case ArbitraryFloatACosPiINTEL extends Definition(
          NonEmptyString("OpArbitraryFloatACosPiINTEL"),
          SPIRVOpcodeNumber(5876),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatACosPiINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7)
                  )
              ),
          InstructionMetadata3.m180
        )
    case ArbitraryFloatATanINTEL extends Definition(
          NonEmptyString("OpArbitraryFloatATanINTEL"),
          SPIRVOpcodeNumber(5877),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatATanINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7)
                  )
              ),
          InstructionMetadata3.m181
        )
    case ArbitraryFloatATanPiINTEL extends Definition(
          NonEmptyString("OpArbitraryFloatATanPiINTEL"),
          SPIRVOpcodeNumber(5878),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatATanPiINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7)
                  )
              ),
          InstructionMetadata3.m182
        )
    case ArbitraryFloatATan2INTEL extends Definition(
          NonEmptyString("OpArbitraryFloatATan2INTEL"),
          SPIRVOpcodeNumber(5879),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatATan2INTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7),
                    values.required(8),
                    values.required(9)
                  )
              ),
          InstructionMetadata3.m183
        )
    case ArbitraryFloatPowINTEL extends Definition(
          NonEmptyString("OpArbitraryFloatPowINTEL"),
          SPIRVOpcodeNumber(5880),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatPowINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7),
                    values.required(8),
                    values.required(9)
                  )
              ),
          InstructionMetadata3.m184
        )
    case ArbitraryFloatPowRINTEL extends Definition(
          NonEmptyString("OpArbitraryFloatPowRINTEL"),
          SPIRVOpcodeNumber(5881),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatPowRINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7),
                    values.required(8),
                    values.required(9)
                  )
              ),
          InstructionMetadata3.m185
        )
    case ArbitraryFloatPowNINTEL extends Definition(
          NonEmptyString("OpArbitraryFloatPowNINTEL"),
          SPIRVOpcodeNumber(5882),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatPowNINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7),
                    values.required(8),
                    values.required(9)
                  )
              ),
          InstructionMetadata3.m186
        )
    case LoopControlINTEL extends Definition(
          NonEmptyString("OpLoopControlINTEL"),
          SPIRVOpcodeNumber(5887),
          definition =>
            raw =>
              Instruction.decodeDefinition(definition, raw, values => Instruction.LoopControlINTEL(values.repeated(0))),
          InstructionMetadata3.m187
        )
    case AliasDomainDeclINTEL extends Definition(
          NonEmptyString("OpAliasDomainDeclINTEL"),
          SPIRVOpcodeNumber(5911),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.AliasDomainDeclINTEL(values.required(0), values.optional(1))
              ),
          InstructionMetadata3.m188
        )
    case AliasScopeDeclINTEL extends Definition(
          NonEmptyString("OpAliasScopeDeclINTEL"),
          SPIRVOpcodeNumber(5912),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.AliasScopeDeclINTEL(values.required(0), values.required(1), values.optional(2))
              ),
          InstructionMetadata3.m189
        )
    case AliasScopeListDeclINTEL extends Definition(
          NonEmptyString("OpAliasScopeListDeclINTEL"),
          SPIRVOpcodeNumber(5913),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.AliasScopeListDeclINTEL(values.required(0), values.repeated(1))
              ),
          InstructionMetadata3.m190
        )
    case FixedSqrtALTERA extends Definition(
          NonEmptyString("OpFixedSqrtALTERA"),
          SPIRVOpcodeNumber(5923),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FixedSqrtALTERA(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7)
                  )
              ),
          InstructionMetadata3.m191
        )
    case FixedRecipALTERA extends Definition(
          NonEmptyString("OpFixedRecipALTERA"),
          SPIRVOpcodeNumber(5924),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FixedRecipALTERA(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7)
                  )
              ),
          InstructionMetadata3.m192
        )
    case FixedRsqrtALTERA extends Definition(
          NonEmptyString("OpFixedRsqrtALTERA"),
          SPIRVOpcodeNumber(5925),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FixedRsqrtALTERA(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7)
                  )
              ),
          InstructionMetadata3.m193
        )
    case FixedSinALTERA extends Definition(
          NonEmptyString("OpFixedSinALTERA"),
          SPIRVOpcodeNumber(5926),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FixedSinALTERA(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7)
                  )
              ),
          InstructionMetadata3.m194
        )
    case FixedCosALTERA extends Definition(
          NonEmptyString("OpFixedCosALTERA"),
          SPIRVOpcodeNumber(5927),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FixedCosALTERA(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7)
                  )
              ),
          InstructionMetadata3.m195
        )
    case FixedSinCosALTERA extends Definition(
          NonEmptyString("OpFixedSinCosALTERA"),
          SPIRVOpcodeNumber(5928),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FixedSinCosALTERA(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7)
                  )
              ),
          InstructionMetadata3.m196
        )
    case FixedSinPiALTERA extends Definition(
          NonEmptyString("OpFixedSinPiALTERA"),
          SPIRVOpcodeNumber(5929),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FixedSinPiALTERA(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7)
                  )
              ),
          InstructionMetadata3.m197
        )
    case FixedCosPiALTERA extends Definition(
          NonEmptyString("OpFixedCosPiALTERA"),
          SPIRVOpcodeNumber(5930),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FixedCosPiALTERA(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7)
                  )
              ),
          InstructionMetadata3.m198
        )
    case FixedSinCosPiALTERA extends Definition(
          NonEmptyString("OpFixedSinCosPiALTERA"),
          SPIRVOpcodeNumber(5931),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FixedSinCosPiALTERA(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7)
                  )
              ),
          InstructionMetadata3.m199
        )
    case FixedLogALTERA extends Definition(
          NonEmptyString("OpFixedLogALTERA"),
          SPIRVOpcodeNumber(5932),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FixedLogALTERA(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7)
                  )
              ),
          InstructionMetadata4.m0
        )
    case FixedExpALTERA extends Definition(
          NonEmptyString("OpFixedExpALTERA"),
          SPIRVOpcodeNumber(5933),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FixedExpALTERA(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7)
                  )
              ),
          InstructionMetadata4.m1
        )
    case PtrCastToCrossWorkgroupALTERA extends Definition(
          NonEmptyString("OpPtrCastToCrossWorkgroupALTERA"),
          SPIRVOpcodeNumber(5934),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.PtrCastToCrossWorkgroupALTERA(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata4.m2
        )
    case CrossWorkgroupCastToPtrALTERA extends Definition(
          NonEmptyString("OpCrossWorkgroupCastToPtrALTERA"),
          SPIRVOpcodeNumber(5938),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CrossWorkgroupCastToPtrALTERA(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata4.m3
        )
    case ReadPipeBlockingALTERA extends Definition(
          NonEmptyString("OpReadPipeBlockingALTERA"),
          SPIRVOpcodeNumber(5946),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ReadPipeBlockingALTERA(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata4.m4
        )
    case WritePipeBlockingALTERA extends Definition(
          NonEmptyString("OpWritePipeBlockingALTERA"),
          SPIRVOpcodeNumber(5947),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.WritePipeBlockingALTERA(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata4.m5
        )
    case FPGARegALTERA extends Definition(
          NonEmptyString("OpFPGARegALTERA"),
          SPIRVOpcodeNumber(5949),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.FPGARegALTERA(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata4.m6
        )
    case RayQueryGetRayTMinKHR extends Definition(
          NonEmptyString("OpRayQueryGetRayTMinKHR"),
          SPIRVOpcodeNumber(6016),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.RayQueryGetRayTMinKHR(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata4.m7
        )
    case RayQueryGetRayFlagsKHR extends Definition(
          NonEmptyString("OpRayQueryGetRayFlagsKHR"),
          SPIRVOpcodeNumber(6017),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.RayQueryGetRayFlagsKHR(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata4.m8
        )
    case RayQueryGetIntersectionTKHR extends Definition(
          NonEmptyString("OpRayQueryGetIntersectionTKHR"),
          SPIRVOpcodeNumber(6018),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.RayQueryGetIntersectionTKHR(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata4.m9
        )
    case RayQueryGetIntersectionInstanceCustomIndexKHR extends Definition(
          NonEmptyString("OpRayQueryGetIntersectionInstanceCustomIndexKHR"),
          SPIRVOpcodeNumber(6019),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.RayQueryGetIntersectionInstanceCustomIndexKHR(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata4.m10
        )
    case RayQueryGetIntersectionInstanceIdKHR extends Definition(
          NonEmptyString("OpRayQueryGetIntersectionInstanceIdKHR"),
          SPIRVOpcodeNumber(6020),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.RayQueryGetIntersectionInstanceIdKHR(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata4.m11
        )
    case RayQueryGetIntersectionInstanceShaderBindingTableRecordOffsetKHR extends Definition(
          NonEmptyString("OpRayQueryGetIntersectionInstanceShaderBindingTableRecordOffsetKHR"),
          SPIRVOpcodeNumber(6021),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.RayQueryGetIntersectionInstanceShaderBindingTableRecordOffsetKHR(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata4.m12
        )
    case RayQueryGetIntersectionGeometryIndexKHR extends Definition(
          NonEmptyString("OpRayQueryGetIntersectionGeometryIndexKHR"),
          SPIRVOpcodeNumber(6022),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.RayQueryGetIntersectionGeometryIndexKHR(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata4.m13
        )
    case RayQueryGetIntersectionPrimitiveIndexKHR extends Definition(
          NonEmptyString("OpRayQueryGetIntersectionPrimitiveIndexKHR"),
          SPIRVOpcodeNumber(6023),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.RayQueryGetIntersectionPrimitiveIndexKHR(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata4.m14
        )
    case RayQueryGetIntersectionBarycentricsKHR extends Definition(
          NonEmptyString("OpRayQueryGetIntersectionBarycentricsKHR"),
          SPIRVOpcodeNumber(6024),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.RayQueryGetIntersectionBarycentricsKHR(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata4.m15
        )
    case RayQueryGetIntersectionFrontFaceKHR extends Definition(
          NonEmptyString("OpRayQueryGetIntersectionFrontFaceKHR"),
          SPIRVOpcodeNumber(6025),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.RayQueryGetIntersectionFrontFaceKHR(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata4.m16
        )
    case RayQueryGetIntersectionCandidateAABBOpaqueKHR extends Definition(
          NonEmptyString("OpRayQueryGetIntersectionCandidateAABBOpaqueKHR"),
          SPIRVOpcodeNumber(6026),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.RayQueryGetIntersectionCandidateAABBOpaqueKHR(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata4.m17
        )
    case RayQueryGetIntersectionObjectRayDirectionKHR extends Definition(
          NonEmptyString("OpRayQueryGetIntersectionObjectRayDirectionKHR"),
          SPIRVOpcodeNumber(6027),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.RayQueryGetIntersectionObjectRayDirectionKHR(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata4.m18
        )
    case RayQueryGetIntersectionObjectRayOriginKHR extends Definition(
          NonEmptyString("OpRayQueryGetIntersectionObjectRayOriginKHR"),
          SPIRVOpcodeNumber(6028),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.RayQueryGetIntersectionObjectRayOriginKHR(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata4.m19
        )
    case RayQueryGetWorldRayDirectionKHR extends Definition(
          NonEmptyString("OpRayQueryGetWorldRayDirectionKHR"),
          SPIRVOpcodeNumber(6029),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.RayQueryGetWorldRayDirectionKHR(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata4.m20
        )
    case RayQueryGetWorldRayOriginKHR extends Definition(
          NonEmptyString("OpRayQueryGetWorldRayOriginKHR"),
          SPIRVOpcodeNumber(6030),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.RayQueryGetWorldRayOriginKHR(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata4.m21
        )
    case RayQueryGetIntersectionObjectToWorldKHR extends Definition(
          NonEmptyString("OpRayQueryGetIntersectionObjectToWorldKHR"),
          SPIRVOpcodeNumber(6031),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.RayQueryGetIntersectionObjectToWorldKHR(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata4.m22
        )
    case RayQueryGetIntersectionWorldToObjectKHR extends Definition(
          NonEmptyString("OpRayQueryGetIntersectionWorldToObjectKHR"),
          SPIRVOpcodeNumber(6032),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.RayQueryGetIntersectionWorldToObjectKHR(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata4.m23
        )
    case AtomicFAddEXT extends Definition(
          NonEmptyString("OpAtomicFAddEXT"),
          SPIRVOpcodeNumber(6035),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.AtomicFAddEXT(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata4.m24
        )
    case TypeBufferSurfaceINTEL extends Definition(
          NonEmptyString("OpTypeBufferSurfaceINTEL"),
          SPIRVOpcodeNumber(6086),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeBufferSurfaceINTEL(values.required(0), values.required(1))
              ),
          InstructionMetadata4.m25
        )
    case TypeStructContinuedINTEL extends Definition(
          NonEmptyString("OpTypeStructContinuedINTEL"),
          SPIRVOpcodeNumber(6090),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeStructContinuedINTEL(values.repeated(0))
              ),
          InstructionMetadata4.m26
        )
    case ConstantCompositeContinuedINTEL extends Definition(
          NonEmptyString("OpConstantCompositeContinuedINTEL"),
          SPIRVOpcodeNumber(6091),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ConstantCompositeContinuedINTEL(values.repeated(0))
              ),
          InstructionMetadata4.m27
        )
    case SpecConstantCompositeContinuedINTEL extends Definition(
          NonEmptyString("OpSpecConstantCompositeContinuedINTEL"),
          SPIRVOpcodeNumber(6092),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.SpecConstantCompositeContinuedINTEL(values.repeated(0))
              ),
          InstructionMetadata4.m28
        )
    case CompositeConstructContinuedINTEL extends Definition(
          NonEmptyString("OpCompositeConstructContinuedINTEL"),
          SPIRVOpcodeNumber(6096),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.CompositeConstructContinuedINTEL(values.repeated(0))
              ),
          InstructionMetadata4.m29
        )
    case ConvertFToBF16INTEL extends Definition(
          NonEmptyString("OpConvertFToBF16INTEL"),
          SPIRVOpcodeNumber(6116),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ConvertFToBF16INTEL(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata4.m30
        )
    case ConvertBF16ToFINTEL extends Definition(
          NonEmptyString("OpConvertBF16ToFINTEL"),
          SPIRVOpcodeNumber(6117),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ConvertBF16ToFINTEL(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata4.m31
        )
    case ControlBarrierArriveEXT extends Definition(
          NonEmptyString("OpControlBarrierArriveEXT"),
          SPIRVOpcodeNumber(6142),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ControlBarrierArriveEXT(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata4.m32
        )
    case ControlBarrierWaitEXT extends Definition(
          NonEmptyString("OpControlBarrierWaitEXT"),
          SPIRVOpcodeNumber(6143),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ControlBarrierWaitEXT(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata4.m33
        )
    case ArithmeticFenceEXT extends Definition(
          NonEmptyString("OpArithmeticFenceEXT"),
          SPIRVOpcodeNumber(6145),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ArithmeticFenceEXT(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata4.m34
        )
    case TaskSequenceCreateALTERA extends Definition(
          NonEmptyString("OpTaskSequenceCreateALTERA"),
          SPIRVOpcodeNumber(6163),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.TaskSequenceCreateALTERA(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6)
                  )
              ),
          InstructionMetadata4.m35
        )
    case TaskSequenceAsyncALTERA extends Definition(
          NonEmptyString("OpTaskSequenceAsyncALTERA"),
          SPIRVOpcodeNumber(6164),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TaskSequenceAsyncALTERA(values.required(0), values.repeated(1))
              ),
          InstructionMetadata4.m36
        )
    case TaskSequenceGetALTERA extends Definition(
          NonEmptyString("OpTaskSequenceGetALTERA"),
          SPIRVOpcodeNumber(6165),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TaskSequenceGetALTERA(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata4.m37
        )
    case TaskSequenceReleaseALTERA extends Definition(
          NonEmptyString("OpTaskSequenceReleaseALTERA"),
          SPIRVOpcodeNumber(6166),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TaskSequenceReleaseALTERA(values.required(0))
              ),
          InstructionMetadata4.m38
        )
    case TypeTaskSequenceALTERA extends Definition(
          NonEmptyString("OpTypeTaskSequenceALTERA"),
          SPIRVOpcodeNumber(6199),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeTaskSequenceALTERA(values.required(0))
              ),
          InstructionMetadata4.m39
        )
    case SubgroupBlockPrefetchINTEL extends Definition(
          NonEmptyString("OpSubgroupBlockPrefetchINTEL"),
          SPIRVOpcodeNumber(6221),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupBlockPrefetchINTEL(values.required(0), values.required(1), values.optional(2))
              ),
          InstructionMetadata4.m40
        )
    case Subgroup2DBlockLoadINTEL extends Definition(
          NonEmptyString("OpSubgroup2DBlockLoadINTEL"),
          SPIRVOpcodeNumber(6231),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.Subgroup2DBlockLoadINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7),
                    values.required(8),
                    values.required(9)
                  )
              ),
          InstructionMetadata4.m41
        )
    case Subgroup2DBlockLoadTransformINTEL extends Definition(
          NonEmptyString("OpSubgroup2DBlockLoadTransformINTEL"),
          SPIRVOpcodeNumber(6232),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.Subgroup2DBlockLoadTransformINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7),
                    values.required(8),
                    values.required(9)
                  )
              ),
          InstructionMetadata4.m42
        )
    case Subgroup2DBlockLoadTransposeINTEL extends Definition(
          NonEmptyString("OpSubgroup2DBlockLoadTransposeINTEL"),
          SPIRVOpcodeNumber(6233),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.Subgroup2DBlockLoadTransposeINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7),
                    values.required(8),
                    values.required(9)
                  )
              ),
          InstructionMetadata4.m43
        )
    case Subgroup2DBlockPrefetchINTEL extends Definition(
          NonEmptyString("OpSubgroup2DBlockPrefetchINTEL"),
          SPIRVOpcodeNumber(6234),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.Subgroup2DBlockPrefetchINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7),
                    values.required(8)
                  )
              ),
          InstructionMetadata4.m44
        )
    case Subgroup2DBlockStoreINTEL extends Definition(
          NonEmptyString("OpSubgroup2DBlockStoreINTEL"),
          SPIRVOpcodeNumber(6235),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.Subgroup2DBlockStoreINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.required(6),
                    values.required(7),
                    values.required(8),
                    values.required(9)
                  )
              ),
          InstructionMetadata4.m45
        )
    case SubgroupMatrixMultiplyAccumulateINTEL extends Definition(
          NonEmptyString("OpSubgroupMatrixMultiplyAccumulateINTEL"),
          SPIRVOpcodeNumber(6237),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupMatrixMultiplyAccumulateINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5),
                    values.optional(6)
                  )
              ),
          InstructionMetadata4.m46
        )
    case BitwiseFunctionINTEL extends Definition(
          NonEmptyString("OpBitwiseFunctionINTEL"),
          SPIRVOpcodeNumber(6242),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.BitwiseFunctionINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata4.m47
        )
    case UntypedVariableLengthArrayINTEL extends Definition(
          NonEmptyString("OpUntypedVariableLengthArrayINTEL"),
          SPIRVOpcodeNumber(6244),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.UntypedVariableLengthArrayINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata4.m48
        )
    case ConditionalExtensionINTEL extends Definition(
          NonEmptyString("OpConditionalExtensionINTEL"),
          SPIRVOpcodeNumber(6248),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ConditionalExtensionINTEL(values.required(0), values.required(1))
              ),
          InstructionMetadata4.m49
        )
    case ConditionalEntryPointINTEL extends Definition(
          NonEmptyString("OpConditionalEntryPointINTEL"),
          SPIRVOpcodeNumber(6249),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ConditionalEntryPointINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.repeated(4)
                  )
              ),
          InstructionMetadata4.m50
        )
    case ConditionalCapabilityINTEL extends Definition(
          NonEmptyString("OpConditionalCapabilityINTEL"),
          SPIRVOpcodeNumber(6250),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ConditionalCapabilityINTEL(values.required(0), values.required(1))
              ),
          InstructionMetadata4.m51
        )
    case SpecConstantTargetINTEL extends Definition(
          NonEmptyString("OpSpecConstantTargetINTEL"),
          SPIRVOpcodeNumber(6251),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SpecConstantTargetINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.repeated(3)
                  )
              ),
          InstructionMetadata4.m52
        )
    case SpecConstantArchitectureINTEL extends Definition(
          NonEmptyString("OpSpecConstantArchitectureINTEL"),
          SPIRVOpcodeNumber(6252),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SpecConstantArchitectureINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata4.m53
        )
    case SpecConstantCapabilitiesINTEL extends Definition(
          NonEmptyString("OpSpecConstantCapabilitiesINTEL"),
          SPIRVOpcodeNumber(6253),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SpecConstantCapabilitiesINTEL(values.required(0), values.required(1), values.repeated(2))
              ),
          InstructionMetadata4.m54
        )
    case ConditionalCopyObjectINTEL extends Definition(
          NonEmptyString("OpConditionalCopyObjectINTEL"),
          SPIRVOpcodeNumber(6254),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ConditionalCopyObjectINTEL(values.required(0), values.required(1), values.repeated(2))
              ),
          InstructionMetadata4.m55
        )
    case PredicatedLoadINTEL extends Definition(
          NonEmptyString("OpPredicatedLoadINTEL"),
          SPIRVOpcodeNumber(6258),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.PredicatedLoadINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.optional(5)
                  )
              ),
          InstructionMetadata4.m56
        )
    case PredicatedStoreINTEL extends Definition(
          NonEmptyString("OpPredicatedStoreINTEL"),
          SPIRVOpcodeNumber(6259),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.PredicatedStoreINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.optional(3)
                  )
              ),
          InstructionMetadata4.m57
        )
    case GroupIMulKHR extends Definition(
          NonEmptyString("OpGroupIMulKHR"),
          SPIRVOpcodeNumber(6401),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupIMulKHR(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata4.m58
        )
    case GroupFMulKHR extends Definition(
          NonEmptyString("OpGroupFMulKHR"),
          SPIRVOpcodeNumber(6402),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupFMulKHR(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata4.m59
        )
    case GroupBitwiseAndKHR extends Definition(
          NonEmptyString("OpGroupBitwiseAndKHR"),
          SPIRVOpcodeNumber(6403),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupBitwiseAndKHR(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata4.m60
        )
    case GroupBitwiseOrKHR extends Definition(
          NonEmptyString("OpGroupBitwiseOrKHR"),
          SPIRVOpcodeNumber(6404),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupBitwiseOrKHR(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata4.m61
        )
    case GroupBitwiseXorKHR extends Definition(
          NonEmptyString("OpGroupBitwiseXorKHR"),
          SPIRVOpcodeNumber(6405),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupBitwiseXorKHR(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata4.m62
        )
    case GroupLogicalAndKHR extends Definition(
          NonEmptyString("OpGroupLogicalAndKHR"),
          SPIRVOpcodeNumber(6406),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupLogicalAndKHR(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata4.m63
        )
    case GroupLogicalOrKHR extends Definition(
          NonEmptyString("OpGroupLogicalOrKHR"),
          SPIRVOpcodeNumber(6407),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupLogicalOrKHR(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata4.m64
        )
    case GroupLogicalXorKHR extends Definition(
          NonEmptyString("OpGroupLogicalXorKHR"),
          SPIRVOpcodeNumber(6408),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupLogicalXorKHR(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata4.m65
        )
    case RoundFToTF32INTEL extends Definition(
          NonEmptyString("OpRoundFToTF32INTEL"),
          SPIRVOpcodeNumber(6426),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.RoundFToTF32INTEL(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata4.m66
        )
    case MaskedGatherINTEL extends Definition(
          NonEmptyString("OpMaskedGatherINTEL"),
          SPIRVOpcodeNumber(6428),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.MaskedGatherINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4),
                    values.required(5)
                  )
              ),
          InstructionMetadata4.m67
        )
    case MaskedScatterINTEL extends Definition(
          NonEmptyString("OpMaskedScatterINTEL"),
          SPIRVOpcodeNumber(6429),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.MaskedScatterINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3)
                  )
              ),
          InstructionMetadata4.m68
        )
    case ConvertHandleToImageINTEL extends Definition(
          NonEmptyString("OpConvertHandleToImageINTEL"),
          SPIRVOpcodeNumber(6529),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ConvertHandleToImageINTEL(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata4.m69
        )
    case ConvertHandleToSamplerINTEL extends Definition(
          NonEmptyString("OpConvertHandleToSamplerINTEL"),
          SPIRVOpcodeNumber(6530),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ConvertHandleToSamplerINTEL(values.required(0), values.required(1), values.required(2))
              ),
          InstructionMetadata4.m70
        )
    case ConvertHandleToSampledImageINTEL extends Definition(
          NonEmptyString("OpConvertHandleToSampledImageINTEL"),
          SPIRVOpcodeNumber(6531),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ConvertHandleToSampledImageINTEL(
                    values.required(0),
                    values.required(1),
                    values.required(2)
                  )
              ),
          InstructionMetadata4.m71
        )
    case FDot2MixAcc32VALVE extends Definition(
          NonEmptyString("OpFDot2MixAcc32VALVE"),
          SPIRVOpcodeNumber(6916),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FDot2MixAcc32VALVE(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata4.m72
        )
    case FDot2MixAcc16VALVE extends Definition(
          NonEmptyString("OpFDot2MixAcc16VALVE"),
          SPIRVOpcodeNumber(6917),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FDot2MixAcc16VALVE(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata4.m73
        )
    case FDot4MixAcc32VALVE extends Definition(
          NonEmptyString("OpFDot4MixAcc32VALVE"),
          SPIRVOpcodeNumber(6918),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FDot4MixAcc32VALVE(
                    values.required(0),
                    values.required(1),
                    values.required(2),
                    values.required(3),
                    values.required(4)
                  )
              ),
          InstructionMetadata4.m74
        )

    final lazy val decoder: Decoder = decode(this)
  }

  val version: SPIRVVersion = SPIRVVersion(
    Positive.unsafe(1),
    NonNegative.unsafe(6)
  )
  val grammarRevision: NonNegative[Int] = NonNegative.unsafe(7)
  val definitions: Vec[Definition] = Definition.values.toVec

  private def operandKinds0: Vec[OperandKindInfo] = Vec(
    OperandKindInfo(
      NonEmptyString("ImageOperands"),
      NonEmptyString("BitEnum"),
      Vec(
        OperandEnumerant(0L, Vec()),
        OperandEnumerant(1L, Vec(NonEmptyString("IdRef"))),
        OperandEnumerant(2L, Vec(NonEmptyString("IdRef"))),
        OperandEnumerant(4L, Vec(NonEmptyString("IdRef"), NonEmptyString("IdRef"))),
        OperandEnumerant(8L, Vec(NonEmptyString("IdRef"))),
        OperandEnumerant(16L, Vec(NonEmptyString("IdRef"))),
        OperandEnumerant(32L, Vec(NonEmptyString("IdRef"))),
        OperandEnumerant(64L, Vec(NonEmptyString("IdRef"))),
        OperandEnumerant(128L, Vec(NonEmptyString("IdRef"))),
        OperandEnumerant(256L, Vec(NonEmptyString("IdScope"))),
        OperandEnumerant(512L, Vec(NonEmptyString("IdScope"))),
        OperandEnumerant(1024L, Vec()),
        OperandEnumerant(2048L, Vec()),
        OperandEnumerant(4096L, Vec()),
        OperandEnumerant(8192L, Vec()),
        OperandEnumerant(16384L, Vec()),
        OperandEnumerant(65536L, Vec(NonEmptyString("IdRef")))
      )
    ),
    OperandKindInfo(
      NonEmptyString("FPFastMathMode"),
      NonEmptyString("BitEnum"),
      Vec(
        OperandEnumerant(0L, Vec()),
        OperandEnumerant(1L, Vec()),
        OperandEnumerant(2L, Vec()),
        OperandEnumerant(4L, Vec()),
        OperandEnumerant(8L, Vec()),
        OperandEnumerant(16L, Vec()),
        OperandEnumerant(65536L, Vec()),
        OperandEnumerant(131072L, Vec()),
        OperandEnumerant(262144L, Vec())
      )
    ),
    OperandKindInfo(
      NonEmptyString("SelectionControl"),
      NonEmptyString("BitEnum"),
      Vec(OperandEnumerant(0L, Vec()), OperandEnumerant(1L, Vec()), OperandEnumerant(2L, Vec()))
    ),
    OperandKindInfo(
      NonEmptyString("LoopControl"),
      NonEmptyString("BitEnum"),
      Vec(
        OperandEnumerant(0L, Vec()),
        OperandEnumerant(1L, Vec()),
        OperandEnumerant(2L, Vec()),
        OperandEnumerant(4L, Vec()),
        OperandEnumerant(8L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(16L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(32L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(64L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(128L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(256L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(65536L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(131072L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(262144L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(524288L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(1048576L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(2097152L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(4194304L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(8388608L, Vec()),
        OperandEnumerant(16777216L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(33554432L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(268435456L, Vec(NonEmptyString("LiteralInteger")))
      )
    ),
    OperandKindInfo(
      NonEmptyString("FunctionControl"),
      NonEmptyString("BitEnum"),
      Vec(
        OperandEnumerant(0L, Vec()),
        OperandEnumerant(1L, Vec()),
        OperandEnumerant(2L, Vec()),
        OperandEnumerant(4L, Vec()),
        OperandEnumerant(8L, Vec()),
        OperandEnumerant(65536L, Vec())
      )
    ),
    OperandKindInfo(
      NonEmptyString("MemorySemantics"),
      NonEmptyString("BitEnum"),
      Vec(
        OperandEnumerant(0L, Vec()),
        OperandEnumerant(2L, Vec()),
        OperandEnumerant(4L, Vec()),
        OperandEnumerant(8L, Vec()),
        OperandEnumerant(16L, Vec()),
        OperandEnumerant(64L, Vec()),
        OperandEnumerant(128L, Vec()),
        OperandEnumerant(256L, Vec()),
        OperandEnumerant(512L, Vec()),
        OperandEnumerant(1024L, Vec()),
        OperandEnumerant(2048L, Vec()),
        OperandEnumerant(4096L, Vec()),
        OperandEnumerant(8192L, Vec()),
        OperandEnumerant(16384L, Vec()),
        OperandEnumerant(32768L, Vec())
      )
    ),
    OperandKindInfo(
      NonEmptyString("MemoryAccess"),
      NonEmptyString("BitEnum"),
      Vec(
        OperandEnumerant(0L, Vec()),
        OperandEnumerant(1L, Vec()),
        OperandEnumerant(2L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(4L, Vec()),
        OperandEnumerant(8L, Vec(NonEmptyString("IdScope"))),
        OperandEnumerant(16L, Vec(NonEmptyString("IdScope"))),
        OperandEnumerant(32L, Vec()),
        OperandEnumerant(65536L, Vec(NonEmptyString("IdRef"))),
        OperandEnumerant(131072L, Vec(NonEmptyString("IdRef")))
      )
    ),
    OperandKindInfo(
      NonEmptyString("KernelProfilingInfo"),
      NonEmptyString("BitEnum"),
      Vec(OperandEnumerant(0L, Vec()), OperandEnumerant(1L, Vec()))
    )
  )

  private def operandKinds1: Vec[OperandKindInfo] = Vec(
    OperandKindInfo(
      NonEmptyString("RayFlags"),
      NonEmptyString("BitEnum"),
      Vec(
        OperandEnumerant(0L, Vec()),
        OperandEnumerant(1L, Vec()),
        OperandEnumerant(2L, Vec()),
        OperandEnumerant(4L, Vec()),
        OperandEnumerant(8L, Vec()),
        OperandEnumerant(16L, Vec()),
        OperandEnumerant(32L, Vec()),
        OperandEnumerant(64L, Vec()),
        OperandEnumerant(128L, Vec()),
        OperandEnumerant(256L, Vec()),
        OperandEnumerant(512L, Vec()),
        OperandEnumerant(1024L, Vec())
      )
    ),
    OperandKindInfo(
      NonEmptyString("FragmentShadingRate"),
      NonEmptyString("BitEnum"),
      Vec(
        OperandEnumerant(1L, Vec()),
        OperandEnumerant(2L, Vec()),
        OperandEnumerant(4L, Vec()),
        OperandEnumerant(8L, Vec())
      )
    ),
    OperandKindInfo(
      NonEmptyString("RawAccessChainOperands"),
      NonEmptyString("BitEnum"),
      Vec(OperandEnumerant(0L, Vec()), OperandEnumerant(1L, Vec()), OperandEnumerant(2L, Vec()))
    ),
    OperandKindInfo(
      NonEmptyString("SourceLanguage"),
      NonEmptyString("ValueEnum"),
      Vec(
        OperandEnumerant(0L, Vec()),
        OperandEnumerant(1L, Vec()),
        OperandEnumerant(2L, Vec()),
        OperandEnumerant(3L, Vec()),
        OperandEnumerant(4L, Vec()),
        OperandEnumerant(5L, Vec()),
        OperandEnumerant(6L, Vec()),
        OperandEnumerant(7L, Vec()),
        OperandEnumerant(8L, Vec()),
        OperandEnumerant(9L, Vec()),
        OperandEnumerant(10L, Vec()),
        OperandEnumerant(11L, Vec()),
        OperandEnumerant(12L, Vec()),
        OperandEnumerant(13L, Vec()),
        OperandEnumerant(14L, Vec()),
        OperandEnumerant(15L, Vec())
      )
    ),
    OperandKindInfo(
      NonEmptyString("ExecutionModel"),
      NonEmptyString("ValueEnum"),
      Vec(
        OperandEnumerant(0L, Vec()),
        OperandEnumerant(1L, Vec()),
        OperandEnumerant(2L, Vec()),
        OperandEnumerant(3L, Vec()),
        OperandEnumerant(4L, Vec()),
        OperandEnumerant(5L, Vec()),
        OperandEnumerant(6L, Vec()),
        OperandEnumerant(5267L, Vec()),
        OperandEnumerant(5268L, Vec()),
        OperandEnumerant(5313L, Vec()),
        OperandEnumerant(5314L, Vec()),
        OperandEnumerant(5315L, Vec()),
        OperandEnumerant(5316L, Vec()),
        OperandEnumerant(5317L, Vec()),
        OperandEnumerant(5318L, Vec()),
        OperandEnumerant(5364L, Vec()),
        OperandEnumerant(5365L, Vec())
      )
    ),
    OperandKindInfo(
      NonEmptyString("AddressingModel"),
      NonEmptyString("ValueEnum"),
      Vec(
        OperandEnumerant(0L, Vec()),
        OperandEnumerant(1L, Vec()),
        OperandEnumerant(2L, Vec()),
        OperandEnumerant(5348L, Vec())
      )
    ),
    OperandKindInfo(
      NonEmptyString("MemoryModel"),
      NonEmptyString("ValueEnum"),
      Vec(
        OperandEnumerant(0L, Vec()),
        OperandEnumerant(1L, Vec()),
        OperandEnumerant(2L, Vec()),
        OperandEnumerant(3L, Vec())
      )
    ),
    OperandKindInfo(
      NonEmptyString("ExecutionMode"),
      NonEmptyString("ValueEnum"),
      Vec(
        OperandEnumerant(0L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(1L, Vec()),
        OperandEnumerant(2L, Vec()),
        OperandEnumerant(3L, Vec()),
        OperandEnumerant(4L, Vec()),
        OperandEnumerant(5L, Vec()),
        OperandEnumerant(6L, Vec()),
        OperandEnumerant(7L, Vec()),
        OperandEnumerant(8L, Vec()),
        OperandEnumerant(9L, Vec()),
        OperandEnumerant(10L, Vec()),
        OperandEnumerant(11L, Vec()),
        OperandEnumerant(12L, Vec()),
        OperandEnumerant(14L, Vec()),
        OperandEnumerant(15L, Vec()),
        OperandEnumerant(16L, Vec()),
        OperandEnumerant(
          17L,
          Vec(NonEmptyString("LiteralInteger"), NonEmptyString("LiteralInteger"), NonEmptyString("LiteralInteger"))
        ),
        OperandEnumerant(
          18L,
          Vec(NonEmptyString("LiteralInteger"), NonEmptyString("LiteralInteger"), NonEmptyString("LiteralInteger"))
        ),
        OperandEnumerant(19L, Vec()),
        OperandEnumerant(20L, Vec()),
        OperandEnumerant(21L, Vec()),
        OperandEnumerant(22L, Vec()),
        OperandEnumerant(23L, Vec()),
        OperandEnumerant(24L, Vec()),
        OperandEnumerant(25L, Vec()),
        OperandEnumerant(26L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(27L, Vec()),
        OperandEnumerant(28L, Vec()),
        OperandEnumerant(29L, Vec()),
        OperandEnumerant(30L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(31L, Vec()),
        OperandEnumerant(33L, Vec()),
        OperandEnumerant(34L, Vec()),
        OperandEnumerant(35L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(36L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(37L, Vec(NonEmptyString("IdRef"))),
        OperandEnumerant(38L, Vec(NonEmptyString("IdRef"), NonEmptyString("IdRef"), NonEmptyString("IdRef"))),
        OperandEnumerant(39L, Vec(NonEmptyString("IdRef"), NonEmptyString("IdRef"), NonEmptyString("IdRef"))),
        OperandEnumerant(4169L, Vec()),
        OperandEnumerant(4170L, Vec()),
        OperandEnumerant(4171L, Vec()),
        OperandEnumerant(4421L, Vec()),
        OperandEnumerant(4446L, Vec()),
        OperandEnumerant(4459L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(4460L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(4461L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(4462L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(4463L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(4489L, Vec()),
        OperandEnumerant(
          4490L,
          Vec(NonEmptyString("LiteralInteger"), NonEmptyString("LiteralInteger"), NonEmptyString("LiteralInteger"))
        ),
        OperandEnumerant(5017L, Vec()),
        OperandEnumerant(5027L, Vec()),
        OperandEnumerant(5069L, Vec()),
        OperandEnumerant(5070L, Vec(NonEmptyString("IdRef"))),
        OperandEnumerant(5071L, Vec(NonEmptyString("IdRef"))),
        OperandEnumerant(5072L, Vec(NonEmptyString("IdRef"), NonEmptyString("IdRef"), NonEmptyString("IdRef"))),
        OperandEnumerant(5073L, Vec(NonEmptyString("IdRef"))),
        OperandEnumerant(5077L, Vec(NonEmptyString("IdRef"), NonEmptyString("IdRef"), NonEmptyString("IdRef"))),
        OperandEnumerant(5079L, Vec()),
        OperandEnumerant(5080L, Vec()),
        OperandEnumerant(5081L, Vec()),
        OperandEnumerant(5082L, Vec()),
        OperandEnumerant(5083L, Vec()),
        OperandEnumerant(5084L, Vec()),
        OperandEnumerant(5088L, Vec()),
        OperandEnumerant(5089L, Vec()),
        OperandEnumerant(5102L, Vec(NonEmptyString("IdRef"), NonEmptyString("IdRef"))),
        OperandEnumerant(5157L, Vec()),
        OperandEnumerant(5269L, Vec()),
        OperandEnumerant(5270L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(5289L, Vec()),
        OperandEnumerant(5290L, Vec()),
        OperandEnumerant(5298L, Vec()),
        OperandEnumerant(5366L, Vec()),
        OperandEnumerant(5367L, Vec()),
        OperandEnumerant(5368L, Vec()),
        OperandEnumerant(5369L, Vec()),
        OperandEnumerant(5370L, Vec()),
        OperandEnumerant(5371L, Vec()),
        OperandEnumerant(5427L, Vec()),
        OperandEnumerant(5618L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(5620L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(5621L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(5622L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(5623L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(
          5893L,
          Vec(NonEmptyString("LiteralInteger"), NonEmptyString("LiteralInteger"), NonEmptyString("LiteralInteger"))
        ),
        OperandEnumerant(5894L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(5895L, Vec()),
        OperandEnumerant(5896L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(5903L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(6023L, Vec()),
        OperandEnumerant(6028L, Vec(NonEmptyString("IdRef"), NonEmptyString("IdRef"))),
        OperandEnumerant(6031L, Vec(NonEmptyString("IdRef"))),
        OperandEnumerant(6154L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(6160L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(6417L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(6461L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(6462L, Vec(NonEmptyString("IdRef"))),
        OperandEnumerant(6463L, Vec(NonEmptyString("NamedMaximumNumberOfRegisters")))
      )
    )
  )

  private def operandKinds2: Vec[OperandKindInfo] = Vec(
    OperandKindInfo(
      NonEmptyString("StorageClass"),
      NonEmptyString("ValueEnum"),
      Vec(
        OperandEnumerant(0L, Vec()),
        OperandEnumerant(1L, Vec()),
        OperandEnumerant(2L, Vec()),
        OperandEnumerant(3L, Vec()),
        OperandEnumerant(4L, Vec()),
        OperandEnumerant(5L, Vec()),
        OperandEnumerant(6L, Vec()),
        OperandEnumerant(7L, Vec()),
        OperandEnumerant(8L, Vec()),
        OperandEnumerant(9L, Vec()),
        OperandEnumerant(10L, Vec()),
        OperandEnumerant(11L, Vec()),
        OperandEnumerant(12L, Vec()),
        OperandEnumerant(4172L, Vec()),
        OperandEnumerant(4491L, Vec()),
        OperandEnumerant(5068L, Vec()),
        OperandEnumerant(5328L, Vec()),
        OperandEnumerant(5329L, Vec()),
        OperandEnumerant(5338L, Vec()),
        OperandEnumerant(5339L, Vec()),
        OperandEnumerant(5342L, Vec()),
        OperandEnumerant(5343L, Vec()),
        OperandEnumerant(5349L, Vec()),
        OperandEnumerant(5385L, Vec()),
        OperandEnumerant(5402L, Vec()),
        OperandEnumerant(5411L, Vec()),
        OperandEnumerant(5605L, Vec()),
        OperandEnumerant(5936L, Vec()),
        OperandEnumerant(5937L, Vec())
      )
    ),
    OperandKindInfo(
      NonEmptyString("Dim"),
      NonEmptyString("ValueEnum"),
      Vec(
        OperandEnumerant(0L, Vec()),
        OperandEnumerant(1L, Vec()),
        OperandEnumerant(2L, Vec()),
        OperandEnumerant(3L, Vec()),
        OperandEnumerant(4L, Vec()),
        OperandEnumerant(5L, Vec()),
        OperandEnumerant(6L, Vec()),
        OperandEnumerant(4173L, Vec())
      )
    ),
    OperandKindInfo(
      NonEmptyString("SamplerAddressingMode"),
      NonEmptyString("ValueEnum"),
      Vec(
        OperandEnumerant(0L, Vec()),
        OperandEnumerant(1L, Vec()),
        OperandEnumerant(2L, Vec()),
        OperandEnumerant(3L, Vec()),
        OperandEnumerant(4L, Vec())
      )
    ),
    OperandKindInfo(
      NonEmptyString("SamplerFilterMode"),
      NonEmptyString("ValueEnum"),
      Vec(OperandEnumerant(0L, Vec()), OperandEnumerant(1L, Vec()))
    ),
    OperandKindInfo(
      NonEmptyString("ImageFormat"),
      NonEmptyString("ValueEnum"),
      Vec(
        OperandEnumerant(0L, Vec()),
        OperandEnumerant(1L, Vec()),
        OperandEnumerant(2L, Vec()),
        OperandEnumerant(3L, Vec()),
        OperandEnumerant(4L, Vec()),
        OperandEnumerant(5L, Vec()),
        OperandEnumerant(6L, Vec()),
        OperandEnumerant(7L, Vec()),
        OperandEnumerant(8L, Vec()),
        OperandEnumerant(9L, Vec()),
        OperandEnumerant(10L, Vec()),
        OperandEnumerant(11L, Vec()),
        OperandEnumerant(12L, Vec()),
        OperandEnumerant(13L, Vec()),
        OperandEnumerant(14L, Vec()),
        OperandEnumerant(15L, Vec()),
        OperandEnumerant(16L, Vec()),
        OperandEnumerant(17L, Vec()),
        OperandEnumerant(18L, Vec()),
        OperandEnumerant(19L, Vec()),
        OperandEnumerant(20L, Vec()),
        OperandEnumerant(21L, Vec()),
        OperandEnumerant(22L, Vec()),
        OperandEnumerant(23L, Vec()),
        OperandEnumerant(24L, Vec()),
        OperandEnumerant(25L, Vec()),
        OperandEnumerant(26L, Vec()),
        OperandEnumerant(27L, Vec()),
        OperandEnumerant(28L, Vec()),
        OperandEnumerant(29L, Vec()),
        OperandEnumerant(30L, Vec()),
        OperandEnumerant(31L, Vec()),
        OperandEnumerant(32L, Vec()),
        OperandEnumerant(33L, Vec()),
        OperandEnumerant(34L, Vec()),
        OperandEnumerant(35L, Vec()),
        OperandEnumerant(36L, Vec()),
        OperandEnumerant(37L, Vec()),
        OperandEnumerant(38L, Vec()),
        OperandEnumerant(39L, Vec()),
        OperandEnumerant(40L, Vec()),
        OperandEnumerant(41L, Vec())
      )
    ),
    OperandKindInfo(
      NonEmptyString("ImageChannelOrder"),
      NonEmptyString("ValueEnum"),
      Vec(
        OperandEnumerant(0L, Vec()),
        OperandEnumerant(1L, Vec()),
        OperandEnumerant(2L, Vec()),
        OperandEnumerant(3L, Vec()),
        OperandEnumerant(4L, Vec()),
        OperandEnumerant(5L, Vec()),
        OperandEnumerant(6L, Vec()),
        OperandEnumerant(7L, Vec()),
        OperandEnumerant(8L, Vec()),
        OperandEnumerant(9L, Vec()),
        OperandEnumerant(10L, Vec()),
        OperandEnumerant(11L, Vec()),
        OperandEnumerant(12L, Vec()),
        OperandEnumerant(13L, Vec()),
        OperandEnumerant(14L, Vec()),
        OperandEnumerant(15L, Vec()),
        OperandEnumerant(16L, Vec()),
        OperandEnumerant(17L, Vec()),
        OperandEnumerant(18L, Vec()),
        OperandEnumerant(19L, Vec())
      )
    ),
    OperandKindInfo(
      NonEmptyString("ImageChannelDataType"),
      NonEmptyString("ValueEnum"),
      Vec(
        OperandEnumerant(0L, Vec()),
        OperandEnumerant(1L, Vec()),
        OperandEnumerant(2L, Vec()),
        OperandEnumerant(3L, Vec()),
        OperandEnumerant(4L, Vec()),
        OperandEnumerant(5L, Vec()),
        OperandEnumerant(6L, Vec()),
        OperandEnumerant(7L, Vec()),
        OperandEnumerant(8L, Vec()),
        OperandEnumerant(9L, Vec()),
        OperandEnumerant(10L, Vec()),
        OperandEnumerant(11L, Vec()),
        OperandEnumerant(12L, Vec()),
        OperandEnumerant(13L, Vec()),
        OperandEnumerant(14L, Vec()),
        OperandEnumerant(15L, Vec()),
        OperandEnumerant(16L, Vec()),
        OperandEnumerant(17L, Vec()),
        OperandEnumerant(19L, Vec()),
        OperandEnumerant(20L, Vec()),
        OperandEnumerant(21L, Vec()),
        OperandEnumerant(22L, Vec()),
        OperandEnumerant(23L, Vec()),
        OperandEnumerant(24L, Vec()),
        OperandEnumerant(25L, Vec()),
        OperandEnumerant(26L, Vec())
      )
    ),
    OperandKindInfo(
      NonEmptyString("FPRoundingMode"),
      NonEmptyString("ValueEnum"),
      Vec(
        OperandEnumerant(0L, Vec()),
        OperandEnumerant(1L, Vec()),
        OperandEnumerant(2L, Vec()),
        OperandEnumerant(3L, Vec())
      )
    )
  )

  private def operandKinds3: Vec[OperandKindInfo] = Vec(
    OperandKindInfo(
      NonEmptyString("FPDenormMode"),
      NonEmptyString("ValueEnum"),
      Vec(OperandEnumerant(0L, Vec()), OperandEnumerant(1L, Vec()))
    ),
    OperandKindInfo(
      NonEmptyString("QuantizationModes"),
      NonEmptyString("ValueEnum"),
      Vec(
        OperandEnumerant(0L, Vec()),
        OperandEnumerant(1L, Vec()),
        OperandEnumerant(2L, Vec()),
        OperandEnumerant(3L, Vec()),
        OperandEnumerant(4L, Vec()),
        OperandEnumerant(5L, Vec()),
        OperandEnumerant(6L, Vec()),
        OperandEnumerant(7L, Vec())
      )
    ),
    OperandKindInfo(
      NonEmptyString("FPOperationMode"),
      NonEmptyString("ValueEnum"),
      Vec(OperandEnumerant(0L, Vec()), OperandEnumerant(1L, Vec()))
    ),
    OperandKindInfo(
      NonEmptyString("OverflowModes"),
      NonEmptyString("ValueEnum"),
      Vec(
        OperandEnumerant(0L, Vec()),
        OperandEnumerant(1L, Vec()),
        OperandEnumerant(2L, Vec()),
        OperandEnumerant(3L, Vec())
      )
    ),
    OperandKindInfo(
      NonEmptyString("LinkageType"),
      NonEmptyString("ValueEnum"),
      Vec(
        OperandEnumerant(0L, Vec()),
        OperandEnumerant(1L, Vec()),
        OperandEnumerant(2L, Vec()),
        OperandEnumerant(3L, Vec())
      )
    ),
    OperandKindInfo(
      NonEmptyString("AccessQualifier"),
      NonEmptyString("ValueEnum"),
      Vec(OperandEnumerant(0L, Vec()), OperandEnumerant(1L, Vec()), OperandEnumerant(2L, Vec()))
    ),
    OperandKindInfo(
      NonEmptyString("HostAccessQualifier"),
      NonEmptyString("ValueEnum"),
      Vec(
        OperandEnumerant(0L, Vec()),
        OperandEnumerant(1L, Vec()),
        OperandEnumerant(2L, Vec()),
        OperandEnumerant(3L, Vec())
      )
    ),
    OperandKindInfo(
      NonEmptyString("FunctionParameterAttribute"),
      NonEmptyString("ValueEnum"),
      Vec(
        OperandEnumerant(0L, Vec()),
        OperandEnumerant(1L, Vec()),
        OperandEnumerant(2L, Vec()),
        OperandEnumerant(3L, Vec()),
        OperandEnumerant(4L, Vec()),
        OperandEnumerant(5L, Vec()),
        OperandEnumerant(6L, Vec()),
        OperandEnumerant(7L, Vec()),
        OperandEnumerant(5940L, Vec())
      )
    )
  )

  private def operandKinds4: Vec[OperandKindInfo] = Vec(
    OperandKindInfo(
      NonEmptyString("Decoration"),
      NonEmptyString("ValueEnum"),
      Vec(
        OperandEnumerant(0L, Vec()),
        OperandEnumerant(1L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(2L, Vec()),
        OperandEnumerant(3L, Vec()),
        OperandEnumerant(4L, Vec()),
        OperandEnumerant(5L, Vec()),
        OperandEnumerant(6L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(7L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(8L, Vec()),
        OperandEnumerant(9L, Vec()),
        OperandEnumerant(10L, Vec()),
        OperandEnumerant(11L, Vec(NonEmptyString("BuiltIn"))),
        OperandEnumerant(13L, Vec()),
        OperandEnumerant(14L, Vec()),
        OperandEnumerant(15L, Vec()),
        OperandEnumerant(16L, Vec()),
        OperandEnumerant(17L, Vec()),
        OperandEnumerant(18L, Vec()),
        OperandEnumerant(19L, Vec()),
        OperandEnumerant(20L, Vec()),
        OperandEnumerant(21L, Vec()),
        OperandEnumerant(22L, Vec()),
        OperandEnumerant(23L, Vec()),
        OperandEnumerant(24L, Vec()),
        OperandEnumerant(25L, Vec()),
        OperandEnumerant(26L, Vec()),
        OperandEnumerant(27L, Vec(NonEmptyString("IdScope"))),
        OperandEnumerant(28L, Vec()),
        OperandEnumerant(29L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(30L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(31L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(32L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(33L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(34L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(35L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(36L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(37L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(38L, Vec(NonEmptyString("FunctionParameterAttribute"))),
        OperandEnumerant(39L, Vec(NonEmptyString("FPRoundingMode"))),
        OperandEnumerant(40L, Vec(NonEmptyString("FPFastMathMode"))),
        OperandEnumerant(41L, Vec(NonEmptyString("LiteralString"), NonEmptyString("LinkageType"))),
        OperandEnumerant(42L, Vec()),
        OperandEnumerant(43L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(44L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(45L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(46L, Vec(NonEmptyString("IdRef"))),
        OperandEnumerant(47L, Vec(NonEmptyString("IdRef"))),
        OperandEnumerant(4216L, Vec()),
        OperandEnumerant(4469L, Vec()),
        OperandEnumerant(4470L, Vec()),
        OperandEnumerant(4487L, Vec()),
        OperandEnumerant(4488L, Vec()),
        OperandEnumerant(4499L, Vec()),
        OperandEnumerant(4999L, Vec()),
        OperandEnumerant(5019L, Vec(NonEmptyString("IdRef"))),
        OperandEnumerant(5020L, Vec(NonEmptyString("IdRef"))),
        OperandEnumerant(5078L, Vec()),
        OperandEnumerant(5091L, Vec(NonEmptyString("IdRef"))),
        OperandEnumerant(5098L, Vec(NonEmptyString("IdRef"))),
        OperandEnumerant(5099L, Vec()),
        OperandEnumerant(5100L, Vec(NonEmptyString("IdRef"))),
        OperandEnumerant(5105L, Vec()),
        OperandEnumerant(5124L, Vec(NonEmptyString("IdRef"))),
        OperandEnumerant(5125L, Vec(NonEmptyString("IdRef"))),
        OperandEnumerant(5145L, Vec()),
        OperandEnumerant(5248L, Vec()),
        OperandEnumerant(5250L, Vec()),
        OperandEnumerant(5252L, Vec()),
        OperandEnumerant(5256L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(5271L, Vec()),
        OperandEnumerant(5272L, Vec()),
        OperandEnumerant(5273L, Vec()),
        OperandEnumerant(5285L, Vec()),
        OperandEnumerant(5300L, Vec()),
        OperandEnumerant(5355L, Vec()),
        OperandEnumerant(5356L, Vec()),
        OperandEnumerant(5358L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(5386L, Vec()),
        OperandEnumerant(5389L, Vec()),
        OperandEnumerant(5397L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(5398L, Vec()),
        OperandEnumerant(5399L, Vec()),
        OperandEnumerant(5400L, Vec()),
        OperandEnumerant(5401L, Vec()),
        OperandEnumerant(5599L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(5602L, Vec()),
        OperandEnumerant(5607L, Vec(NonEmptyString("LiteralString"))),
        OperandEnumerant(5608L, Vec()),
        OperandEnumerant(5624L, Vec()),
        OperandEnumerant(5625L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(5626L, Vec()),
        OperandEnumerant(5627L, Vec()),
        OperandEnumerant(5628L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(5634L, Vec(NonEmptyString("IdRef"))),
        OperandEnumerant(5635L, Vec(NonEmptyString("LiteralString"))),
        OperandEnumerant(5636L, Vec(NonEmptyString("LiteralString"))),
        OperandEnumerant(5822L, Vec(NonEmptyString("LiteralInteger"), NonEmptyString("FPRoundingMode"))),
        OperandEnumerant(5823L, Vec(NonEmptyString("LiteralInteger"), NonEmptyString("FPDenormMode"))),
        OperandEnumerant(5825L, Vec()),
        OperandEnumerant(5826L, Vec(NonEmptyString("LiteralString"))),
        OperandEnumerant(5827L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(5828L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(5829L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(5830L, Vec()),
        OperandEnumerant(5831L, Vec()),
        OperandEnumerant(5832L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(5833L, Vec()),
        OperandEnumerant(5834L, Vec(NonEmptyString("LiteralString"), NonEmptyString("LiteralString"))),
        OperandEnumerant(5835L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(5836L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(5883L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(5884L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(5885L, Vec()),
        OperandEnumerant(5899L, Vec()),
        OperandEnumerant(5900L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(5901L, Vec()),
        OperandEnumerant(5902L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(5905L, Vec()),
        OperandEnumerant(5907L, Vec()),
        OperandEnumerant(5909L, Vec(NonEmptyString("LiteralInteger"), NonEmptyString("LiteralInteger"))),
        OperandEnumerant(5914L, Vec(NonEmptyString("IdRef"))),
        OperandEnumerant(5915L, Vec(NonEmptyString("IdRef"))),
        OperandEnumerant(5917L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(5918L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(5919L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(5921L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(5944L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(6080L, Vec(NonEmptyString("LiteralInteger"), NonEmptyString("FPOperationMode"))),
        OperandEnumerant(6085L, Vec()),
        OperandEnumerant(6087L, Vec()),
        OperandEnumerant(6140L, Vec()),
        OperandEnumerant(6151L, Vec()),
        OperandEnumerant(6170L, Vec(NonEmptyString("LiteralFloat"))),
        OperandEnumerant(6172L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(
          6173L,
          Vec(NonEmptyString("LiteralInteger"), NonEmptyString("LiteralInteger"), NonEmptyString("LiteralInteger"))
        ),
        OperandEnumerant(6175L, Vec()),
        OperandEnumerant(6176L, Vec()),
        OperandEnumerant(6177L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(6178L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(6179L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(6180L, Vec(NonEmptyString("AccessQualifier"))),
        OperandEnumerant(6181L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(6182L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(6183L, Vec()),
        OperandEnumerant(6188L, Vec(NonEmptyString("HostAccessQualifier"), NonEmptyString("LiteralString"))),
        OperandEnumerant(6190L, Vec(NonEmptyString("InitializationModeQualifier"))),
        OperandEnumerant(6191L, Vec(NonEmptyString("LiteralInteger"))),
        OperandEnumerant(6247L, Vec(NonEmptyString("IdRef"))),
        OperandEnumerant(6442L, Vec(NonEmptyString("LiteralInteger"), NonEmptyString("LoadCacheControl"))),
        OperandEnumerant(6443L, Vec(NonEmptyString("LiteralInteger"), NonEmptyString("StoreCacheControl"))),
        OperandEnumerant(7040L, Vec(NonEmptyString("LiteralInteger")))
      )
    ),
    OperandKindInfo(
      NonEmptyString("BuiltIn"),
      NonEmptyString("ValueEnum"),
      Vec(
        OperandEnumerant(0L, Vec()),
        OperandEnumerant(1L, Vec()),
        OperandEnumerant(3L, Vec()),
        OperandEnumerant(4L, Vec()),
        OperandEnumerant(5L, Vec()),
        OperandEnumerant(6L, Vec()),
        OperandEnumerant(7L, Vec()),
        OperandEnumerant(8L, Vec()),
        OperandEnumerant(9L, Vec()),
        OperandEnumerant(10L, Vec()),
        OperandEnumerant(11L, Vec()),
        OperandEnumerant(12L, Vec()),
        OperandEnumerant(13L, Vec()),
        OperandEnumerant(14L, Vec()),
        OperandEnumerant(15L, Vec()),
        OperandEnumerant(16L, Vec()),
        OperandEnumerant(17L, Vec()),
        OperandEnumerant(18L, Vec()),
        OperandEnumerant(19L, Vec()),
        OperandEnumerant(20L, Vec()),
        OperandEnumerant(22L, Vec()),
        OperandEnumerant(23L, Vec()),
        OperandEnumerant(24L, Vec()),
        OperandEnumerant(25L, Vec()),
        OperandEnumerant(26L, Vec()),
        OperandEnumerant(27L, Vec()),
        OperandEnumerant(28L, Vec()),
        OperandEnumerant(29L, Vec()),
        OperandEnumerant(30L, Vec()),
        OperandEnumerant(31L, Vec()),
        OperandEnumerant(32L, Vec()),
        OperandEnumerant(33L, Vec()),
        OperandEnumerant(34L, Vec()),
        OperandEnumerant(36L, Vec()),
        OperandEnumerant(37L, Vec()),
        OperandEnumerant(38L, Vec()),
        OperandEnumerant(39L, Vec()),
        OperandEnumerant(40L, Vec()),
        OperandEnumerant(41L, Vec()),
        OperandEnumerant(42L, Vec()),
        OperandEnumerant(43L, Vec()),
        OperandEnumerant(4160L, Vec()),
        OperandEnumerant(4161L, Vec()),
        OperandEnumerant(4162L, Vec()),
        OperandEnumerant(4163L, Vec()),
        OperandEnumerant(4164L, Vec()),
        OperandEnumerant(4416L, Vec()),
        OperandEnumerant(4417L, Vec()),
        OperandEnumerant(4418L, Vec()),
        OperandEnumerant(4419L, Vec()),
        OperandEnumerant(4420L, Vec()),
        OperandEnumerant(4424L, Vec()),
        OperandEnumerant(4425L, Vec()),
        OperandEnumerant(4426L, Vec()),
        OperandEnumerant(4432L, Vec()),
        OperandEnumerant(4438L, Vec()),
        OperandEnumerant(4440L, Vec()),
        OperandEnumerant(4444L, Vec()),
        OperandEnumerant(4492L, Vec()),
        OperandEnumerant(4493L, Vec()),
        OperandEnumerant(4494L, Vec()),
        OperandEnumerant(4992L, Vec()),
        OperandEnumerant(4993L, Vec()),
        OperandEnumerant(4994L, Vec()),
        OperandEnumerant(4995L, Vec()),
        OperandEnumerant(4996L, Vec()),
        OperandEnumerant(4997L, Vec()),
        OperandEnumerant(4998L, Vec()),
        OperandEnumerant(5014L, Vec()),
        OperandEnumerant(5021L, Vec()),
        OperandEnumerant(5073L, Vec()),
        OperandEnumerant(5122L, Vec()),
        OperandEnumerant(5123L, Vec()),
        OperandEnumerant(5253L, Vec()),
        OperandEnumerant(5257L, Vec()),
        OperandEnumerant(5258L, Vec()),
        OperandEnumerant(5261L, Vec()),
        OperandEnumerant(5262L, Vec()),
        OperandEnumerant(5264L, Vec()),
        OperandEnumerant(5274L, Vec()),
        OperandEnumerant(5275L, Vec()),
        OperandEnumerant(5276L, Vec()),
        OperandEnumerant(5277L, Vec()),
        OperandEnumerant(5278L, Vec()),
        OperandEnumerant(5279L, Vec()),
        OperandEnumerant(5280L, Vec()),
        OperandEnumerant(5281L, Vec()),
        OperandEnumerant(5286L, Vec()),
        OperandEnumerant(5287L, Vec()),
        OperandEnumerant(5292L, Vec()),
        OperandEnumerant(5293L, Vec()),
        OperandEnumerant(5294L, Vec()),
        OperandEnumerant(5295L, Vec()),
        OperandEnumerant(5296L, Vec()),
        OperandEnumerant(5299L, Vec()),
        OperandEnumerant(5319L, Vec()),
        OperandEnumerant(5320L, Vec()),
        OperandEnumerant(5321L, Vec()),
        OperandEnumerant(5322L, Vec()),
        OperandEnumerant(5323L, Vec()),
        OperandEnumerant(5324L, Vec()),
        OperandEnumerant(5325L, Vec()),
        OperandEnumerant(5326L, Vec()),
        OperandEnumerant(5327L, Vec()),
        OperandEnumerant(5330L, Vec()),
        OperandEnumerant(5331L, Vec()),
        OperandEnumerant(5332L, Vec()),
        OperandEnumerant(5333L, Vec()),
        OperandEnumerant(5334L, Vec()),
        OperandEnumerant(5335L, Vec()),
        OperandEnumerant(5337L, Vec()),
        OperandEnumerant(5344L, Vec()),
        OperandEnumerant(5351L, Vec()),
        OperandEnumerant(5352L, Vec()),
        OperandEnumerant(5359L, Vec()),
        OperandEnumerant(5360L, Vec()),
        OperandEnumerant(5361L, Vec()),
        OperandEnumerant(5374L, Vec()),
        OperandEnumerant(5375L, Vec()),
        OperandEnumerant(5376L, Vec()),
        OperandEnumerant(5377L, Vec()),
        OperandEnumerant(5396L, Vec()),
        OperandEnumerant(5405L, Vec()),
        OperandEnumerant(5406L, Vec()),
        OperandEnumerant(5420L, Vec()),
        OperandEnumerant(5421L, Vec()),
        OperandEnumerant(5436L, Vec()),
        OperandEnumerant(6021L, Vec())
      )
    ),
    OperandKindInfo(
      NonEmptyString("Scope"),
      NonEmptyString("ValueEnum"),
      Vec(
        OperandEnumerant(0L, Vec()),
        OperandEnumerant(1L, Vec()),
        OperandEnumerant(2L, Vec()),
        OperandEnumerant(3L, Vec()),
        OperandEnumerant(4L, Vec()),
        OperandEnumerant(5L, Vec()),
        OperandEnumerant(6L, Vec())
      )
    ),
    OperandKindInfo(
      NonEmptyString("GroupOperation"),
      NonEmptyString("ValueEnum"),
      Vec(
        OperandEnumerant(0L, Vec()),
        OperandEnumerant(1L, Vec()),
        OperandEnumerant(2L, Vec()),
        OperandEnumerant(3L, Vec()),
        OperandEnumerant(6L, Vec()),
        OperandEnumerant(7L, Vec()),
        OperandEnumerant(8L, Vec())
      )
    ),
    OperandKindInfo(
      NonEmptyString("KernelEnqueueFlags"),
      NonEmptyString("ValueEnum"),
      Vec(OperandEnumerant(0L, Vec()), OperandEnumerant(1L, Vec()), OperandEnumerant(2L, Vec()))
    ),
    OperandKindInfo(
      NonEmptyString("Capability"),
      NonEmptyString("ValueEnum"),
      Vec(
        OperandEnumerant(0L, Vec()),
        OperandEnumerant(1L, Vec()),
        OperandEnumerant(2L, Vec()),
        OperandEnumerant(3L, Vec()),
        OperandEnumerant(4L, Vec()),
        OperandEnumerant(5L, Vec()),
        OperandEnumerant(6L, Vec()),
        OperandEnumerant(7L, Vec()),
        OperandEnumerant(8L, Vec()),
        OperandEnumerant(9L, Vec()),
        OperandEnumerant(10L, Vec()),
        OperandEnumerant(11L, Vec()),
        OperandEnumerant(12L, Vec()),
        OperandEnumerant(13L, Vec()),
        OperandEnumerant(14L, Vec()),
        OperandEnumerant(15L, Vec()),
        OperandEnumerant(17L, Vec()),
        OperandEnumerant(18L, Vec()),
        OperandEnumerant(19L, Vec()),
        OperandEnumerant(20L, Vec()),
        OperandEnumerant(21L, Vec()),
        OperandEnumerant(22L, Vec()),
        OperandEnumerant(23L, Vec()),
        OperandEnumerant(24L, Vec()),
        OperandEnumerant(25L, Vec()),
        OperandEnumerant(27L, Vec()),
        OperandEnumerant(28L, Vec()),
        OperandEnumerant(29L, Vec()),
        OperandEnumerant(30L, Vec()),
        OperandEnumerant(31L, Vec()),
        OperandEnumerant(32L, Vec()),
        OperandEnumerant(33L, Vec()),
        OperandEnumerant(34L, Vec()),
        OperandEnumerant(35L, Vec()),
        OperandEnumerant(36L, Vec()),
        OperandEnumerant(37L, Vec()),
        OperandEnumerant(38L, Vec()),
        OperandEnumerant(39L, Vec()),
        OperandEnumerant(40L, Vec()),
        OperandEnumerant(41L, Vec()),
        OperandEnumerant(42L, Vec()),
        OperandEnumerant(43L, Vec()),
        OperandEnumerant(44L, Vec()),
        OperandEnumerant(45L, Vec()),
        OperandEnumerant(46L, Vec()),
        OperandEnumerant(47L, Vec()),
        OperandEnumerant(48L, Vec()),
        OperandEnumerant(49L, Vec()),
        OperandEnumerant(50L, Vec()),
        OperandEnumerant(51L, Vec()),
        OperandEnumerant(52L, Vec()),
        OperandEnumerant(53L, Vec()),
        OperandEnumerant(54L, Vec()),
        OperandEnumerant(55L, Vec()),
        OperandEnumerant(56L, Vec()),
        OperandEnumerant(57L, Vec()),
        OperandEnumerant(58L, Vec()),
        OperandEnumerant(59L, Vec()),
        OperandEnumerant(60L, Vec()),
        OperandEnumerant(61L, Vec()),
        OperandEnumerant(62L, Vec()),
        OperandEnumerant(63L, Vec()),
        OperandEnumerant(64L, Vec()),
        OperandEnumerant(65L, Vec()),
        OperandEnumerant(66L, Vec()),
        OperandEnumerant(67L, Vec()),
        OperandEnumerant(68L, Vec()),
        OperandEnumerant(69L, Vec()),
        OperandEnumerant(70L, Vec()),
        OperandEnumerant(71L, Vec()),
        OperandEnumerant(4165L, Vec()),
        OperandEnumerant(4166L, Vec()),
        OperandEnumerant(4167L, Vec()),
        OperandEnumerant(4168L, Vec()),
        OperandEnumerant(4174L, Vec()),
        OperandEnumerant(4175L, Vec()),
        OperandEnumerant(4176L, Vec()),
        OperandEnumerant(4191L, Vec()),
        OperandEnumerant(4201L, Vec()),
        OperandEnumerant(4212L, Vec()),
        OperandEnumerant(4213L, Vec()),
        OperandEnumerant(4228L, Vec()),
        OperandEnumerant(4229L, Vec()),
        OperandEnumerant(4230L, Vec()),
        OperandEnumerant(4231L, Vec()),
        OperandEnumerant(4232L, Vec()),
        OperandEnumerant(4422L, Vec()),
        OperandEnumerant(4423L, Vec()),
        OperandEnumerant(4427L, Vec()),
        OperandEnumerant(4428L, Vec()),
        OperandEnumerant(4429L, Vec()),
        OperandEnumerant(4430L, Vec()),
        OperandEnumerant(4431L, Vec()),
        OperandEnumerant(4433L, Vec()),
        OperandEnumerant(4434L, Vec()),
        OperandEnumerant(4435L, Vec()),
        OperandEnumerant(4436L, Vec()),
        OperandEnumerant(4437L, Vec()),
        OperandEnumerant(4439L, Vec()),
        OperandEnumerant(4441L, Vec()),
        OperandEnumerant(4442L, Vec()),
        OperandEnumerant(4445L, Vec()),
        OperandEnumerant(4447L, Vec()),
        OperandEnumerant(4448L, Vec()),
        OperandEnumerant(4449L, Vec()),
        OperandEnumerant(4450L, Vec()),
        OperandEnumerant(4464L, Vec()),
        OperandEnumerant(4465L, Vec()),
        OperandEnumerant(4466L, Vec()),
        OperandEnumerant(4467L, Vec()),
        OperandEnumerant(4468L, Vec()),
        OperandEnumerant(4471L, Vec()),
        OperandEnumerant(4472L, Vec()),
        OperandEnumerant(4473L, Vec()),
        OperandEnumerant(4478L, Vec()),
        OperandEnumerant(4479L, Vec()),
        OperandEnumerant(4484L, Vec()),
        OperandEnumerant(4485L, Vec()),
        OperandEnumerant(4486L, Vec()),
        OperandEnumerant(4495L, Vec()),
        OperandEnumerant(4496L, Vec()),
        OperandEnumerant(4498L, Vec()),
        OperandEnumerant(4539L, Vec()),
        OperandEnumerant(4543L, Vec()),
        OperandEnumerant(4544L, Vec()),
        OperandEnumerant(5008L, Vec()),
        OperandEnumerant(5009L, Vec()),
        OperandEnumerant(5010L, Vec()),
        OperandEnumerant(5013L, Vec()),
        OperandEnumerant(5015L, Vec()),
        OperandEnumerant(5016L, Vec()),
        OperandEnumerant(5055L, Vec()),
        OperandEnumerant(5067L, Vec()),
        OperandEnumerant(5087L, Vec()),
        OperandEnumerant(5112L, Vec()),
        OperandEnumerant(5114L, Vec()),
        OperandEnumerant(5116L, Vec()),
        OperandEnumerant(5117L, Vec()),
        OperandEnumerant(5118L, Vec()),
        OperandEnumerant(5120L, Vec()),
        OperandEnumerant(5128L, Vec()),
        OperandEnumerant(5146L, Vec()),
        OperandEnumerant(5156L, Vec()),
        OperandEnumerant(5181L, Vec()),
        OperandEnumerant(5249L, Vec()),
        OperandEnumerant(5251L, Vec()),
        OperandEnumerant(5254L, Vec()),
        OperandEnumerant(5255L, Vec()),
        OperandEnumerant(5259L, Vec()),
        OperandEnumerant(5260L, Vec()),
        OperandEnumerant(5265L, Vec()),
        OperandEnumerant(5266L, Vec()),
        OperandEnumerant(5282L, Vec()),
        OperandEnumerant(5283L, Vec()),
        OperandEnumerant(5284L, Vec()),
        OperandEnumerant(5288L, Vec()),
        OperandEnumerant(5291L, Vec()),
        OperandEnumerant(5297L, Vec()),
        OperandEnumerant(5301L, Vec()),
        OperandEnumerant(5302L, Vec()),
        OperandEnumerant(5303L, Vec()),
        OperandEnumerant(5304L, Vec()),
        OperandEnumerant(5305L, Vec()),
        OperandEnumerant(5306L, Vec()),
        OperandEnumerant(5307L, Vec()),
        OperandEnumerant(5308L, Vec()),
        OperandEnumerant(5309L, Vec()),
        OperandEnumerant(5310L, Vec()),
        OperandEnumerant(5311L, Vec()),
        OperandEnumerant(5312L, Vec()),
        OperandEnumerant(5336L, Vec()),
        OperandEnumerant(5340L, Vec()),
        OperandEnumerant(5341L, Vec()),
        OperandEnumerant(5345L, Vec()),
        OperandEnumerant(5346L, Vec()),
        OperandEnumerant(5347L, Vec()),
        OperandEnumerant(5350L, Vec()),
        OperandEnumerant(5353L, Vec()),
        OperandEnumerant(5357L, Vec()),
        OperandEnumerant(5363L, Vec()),
        OperandEnumerant(5372L, Vec()),
        OperandEnumerant(5373L, Vec()),
        OperandEnumerant(5378L, Vec()),
        OperandEnumerant(5379L, Vec()),
        OperandEnumerant(5380L, Vec()),
        OperandEnumerant(5381L, Vec()),
        OperandEnumerant(5383L, Vec()),
        OperandEnumerant(5388L, Vec()),
        OperandEnumerant(5390L, Vec()),
        OperandEnumerant(5391L, Vec()),
        OperandEnumerant(5394L, Vec()),
        OperandEnumerant(5404L, Vec()),
        OperandEnumerant(5409L, Vec()),
        OperandEnumerant(5414L, Vec()),
        OperandEnumerant(5418L, Vec()),
        OperandEnumerant(5419L, Vec()),
        OperandEnumerant(5423L, Vec()),
        OperandEnumerant(5425L, Vec()),
        OperandEnumerant(5426L, Vec()),
        OperandEnumerant(5430L, Vec()),
        OperandEnumerant(5431L, Vec()),
        OperandEnumerant(5432L, Vec()),
        OperandEnumerant(5433L, Vec()),
        OperandEnumerant(5434L, Vec()),
        OperandEnumerant(5435L, Vec()),
        OperandEnumerant(5437L, Vec()),
        OperandEnumerant(5439L, Vec()),
        OperandEnumerant(5447L, Vec()),
        OperandEnumerant(5568L, Vec()),
        OperandEnumerant(5569L, Vec()),
        OperandEnumerant(5570L, Vec()),
        OperandEnumerant(5579L, Vec()),
        OperandEnumerant(5582L, Vec()),
        OperandEnumerant(5583L, Vec()),
        OperandEnumerant(5584L, Vec()),
        OperandEnumerant(5603L, Vec()),
        OperandEnumerant(5604L, Vec()),
        OperandEnumerant(5606L, Vec()),
        OperandEnumerant(5612L, Vec()),
        OperandEnumerant(5613L, Vec()),
        OperandEnumerant(5616L, Vec()),
        OperandEnumerant(5617L, Vec()),
        OperandEnumerant(5619L, Vec()),
        OperandEnumerant(5629L, Vec()),
        OperandEnumerant(5696L, Vec()),
        OperandEnumerant(5697L, Vec()),
        OperandEnumerant(5698L, Vec()),
        OperandEnumerant(5817L, Vec()),
        OperandEnumerant(5821L, Vec()),
        OperandEnumerant(5824L, Vec()),
        OperandEnumerant(5837L, Vec()),
        OperandEnumerant(5844L, Vec()),
        OperandEnumerant(5845L, Vec()),
        OperandEnumerant(5886L, Vec()),
        OperandEnumerant(5888L, Vec()),
        OperandEnumerant(5892L, Vec()),
        OperandEnumerant(5897L, Vec()),
        OperandEnumerant(5898L, Vec()),
        OperandEnumerant(5904L, Vec()),
        OperandEnumerant(5906L, Vec()),
        OperandEnumerant(5908L, Vec()),
        OperandEnumerant(5910L, Vec()),
        OperandEnumerant(5916L, Vec()),
        OperandEnumerant(5920L, Vec()),
        OperandEnumerant(5922L, Vec()),
        OperandEnumerant(5935L, Vec()),
        OperandEnumerant(5939L, Vec()),
        OperandEnumerant(5943L, Vec()),
        OperandEnumerant(5945L, Vec()),
        OperandEnumerant(5948L, Vec()),
        OperandEnumerant(6016L, Vec()),
        OperandEnumerant(6017L, Vec()),
        OperandEnumerant(6018L, Vec()),
        OperandEnumerant(6019L, Vec()),
        OperandEnumerant(6020L, Vec()),
        OperandEnumerant(6022L, Vec()),
        OperandEnumerant(6024L, Vec()),
        OperandEnumerant(6025L, Vec()),
        OperandEnumerant(6026L, Vec()),
        OperandEnumerant(6029L, Vec()),
        OperandEnumerant(6030L, Vec()),
        OperandEnumerant(6032L, Vec()),
        OperandEnumerant(6033L, Vec()),
        OperandEnumerant(6034L, Vec()),
        OperandEnumerant(6089L, Vec()),
        OperandEnumerant(6094L, Vec()),
        OperandEnumerant(6095L, Vec()),
        OperandEnumerant(6114L, Vec()),
        OperandEnumerant(6115L, Vec()),
        OperandEnumerant(6141L, Vec()),
        OperandEnumerant(6144L, Vec()),
        OperandEnumerant(6150L, Vec()),
        OperandEnumerant(6161L, Vec()),
        OperandEnumerant(6162L, Vec()),
        OperandEnumerant(6169L, Vec()),
        OperandEnumerant(6171L, Vec()),
        OperandEnumerant(6174L, Vec()),
        OperandEnumerant(6187L, Vec()),
        OperandEnumerant(6189L, Vec()),
        OperandEnumerant(6220L, Vec()),
        OperandEnumerant(6228L, Vec()),
        OperandEnumerant(6229L, Vec()),
        OperandEnumerant(6230L, Vec()),
        OperandEnumerant(6236L, Vec()),
        OperandEnumerant(6241L, Vec()),
        OperandEnumerant(6243L, Vec()),
        OperandEnumerant(6245L, Vec()),
        OperandEnumerant(6246L, Vec()),
        OperandEnumerant(6257L, Vec()),
        OperandEnumerant(6265L, Vec()),
        OperandEnumerant(6400L, Vec()),
        OperandEnumerant(6425L, Vec()),
        OperandEnumerant(6427L, Vec()),
        OperandEnumerant(6441L, Vec()),
        OperandEnumerant(6460L, Vec()),
        OperandEnumerant(6528L, Vec()),
        OperandEnumerant(6912L, Vec()),
        OperandEnumerant(6913L, Vec()),
        OperandEnumerant(6914L, Vec()),
        OperandEnumerant(6915L, Vec()),
        OperandEnumerant(7041L, Vec())
      )
    ),
    OperandKindInfo(
      NonEmptyString("RayQueryIntersection"),
      NonEmptyString("ValueEnum"),
      Vec(OperandEnumerant(0L, Vec()), OperandEnumerant(1L, Vec()))
    ),
    OperandKindInfo(
      NonEmptyString("RayQueryCommittedIntersectionType"),
      NonEmptyString("ValueEnum"),
      Vec(OperandEnumerant(0L, Vec()), OperandEnumerant(1L, Vec()), OperandEnumerant(2L, Vec()))
    )
  )

  private def operandKinds5: Vec[OperandKindInfo] = Vec(
    OperandKindInfo(
      NonEmptyString("RayQueryCandidateIntersectionType"),
      NonEmptyString("ValueEnum"),
      Vec(OperandEnumerant(0L, Vec()), OperandEnumerant(1L, Vec()))
    ),
    OperandKindInfo(
      NonEmptyString("PackedVectorFormat"),
      NonEmptyString("ValueEnum"),
      Vec(OperandEnumerant(0L, Vec()))
    ),
    OperandKindInfo(
      NonEmptyString("CooperativeMatrixOperands"),
      NonEmptyString("BitEnum"),
      Vec(
        OperandEnumerant(0L, Vec()),
        OperandEnumerant(1L, Vec()),
        OperandEnumerant(2L, Vec()),
        OperandEnumerant(4L, Vec()),
        OperandEnumerant(8L, Vec()),
        OperandEnumerant(16L, Vec())
      )
    ),
    OperandKindInfo(
      NonEmptyString("CooperativeMatrixLayout"),
      NonEmptyString("ValueEnum"),
      Vec(
        OperandEnumerant(0L, Vec()),
        OperandEnumerant(1L, Vec()),
        OperandEnumerant(4202L, Vec()),
        OperandEnumerant(4203L, Vec())
      )
    ),
    OperandKindInfo(
      NonEmptyString("CooperativeMatrixUse"),
      NonEmptyString("ValueEnum"),
      Vec(OperandEnumerant(0L, Vec()), OperandEnumerant(1L, Vec()), OperandEnumerant(2L, Vec()))
    ),
    OperandKindInfo(
      NonEmptyString("CooperativeMatrixReduce"),
      NonEmptyString("BitEnum"),
      Vec(OperandEnumerant(1L, Vec()), OperandEnumerant(2L, Vec()), OperandEnumerant(4L, Vec()))
    ),
    OperandKindInfo(
      NonEmptyString("TensorClampMode"),
      NonEmptyString("ValueEnum"),
      Vec(
        OperandEnumerant(0L, Vec()),
        OperandEnumerant(1L, Vec()),
        OperandEnumerant(2L, Vec()),
        OperandEnumerant(3L, Vec()),
        OperandEnumerant(4L, Vec())
      )
    ),
    OperandKindInfo(
      NonEmptyString("TensorAddressingOperands"),
      NonEmptyString("BitEnum"),
      Vec(
        OperandEnumerant(0L, Vec()),
        OperandEnumerant(1L, Vec(NonEmptyString("IdRef"))),
        OperandEnumerant(2L, Vec(NonEmptyString("IdRef"))),
        OperandEnumerant(4L, Vec(NonEmptyString("IdRef")))
      )
    )
  )

  private def operandKinds6: Vec[OperandKindInfo] = Vec(
    OperandKindInfo(
      NonEmptyString("InitializationModeQualifier"),
      NonEmptyString("ValueEnum"),
      Vec(OperandEnumerant(0L, Vec()), OperandEnumerant(1L, Vec()))
    ),
    OperandKindInfo(
      NonEmptyString("LoadCacheControl"),
      NonEmptyString("ValueEnum"),
      Vec(
        OperandEnumerant(0L, Vec()),
        OperandEnumerant(1L, Vec()),
        OperandEnumerant(2L, Vec()),
        OperandEnumerant(3L, Vec()),
        OperandEnumerant(4L, Vec())
      )
    ),
    OperandKindInfo(
      NonEmptyString("StoreCacheControl"),
      NonEmptyString("ValueEnum"),
      Vec(
        OperandEnumerant(0L, Vec()),
        OperandEnumerant(1L, Vec()),
        OperandEnumerant(2L, Vec()),
        OperandEnumerant(3L, Vec())
      )
    ),
    OperandKindInfo(
      NonEmptyString("NamedMaximumNumberOfRegisters"),
      NonEmptyString("ValueEnum"),
      Vec(OperandEnumerant(0L, Vec()))
    ),
    OperandKindInfo(
      NonEmptyString("MatrixMultiplyAccumulateOperands"),
      NonEmptyString("BitEnum"),
      Vec(
        OperandEnumerant(0L, Vec()),
        OperandEnumerant(1L, Vec()),
        OperandEnumerant(2L, Vec()),
        OperandEnumerant(4L, Vec()),
        OperandEnumerant(8L, Vec()),
        OperandEnumerant(16L, Vec()),
        OperandEnumerant(32L, Vec()),
        OperandEnumerant(64L, Vec()),
        OperandEnumerant(128L, Vec()),
        OperandEnumerant(256L, Vec()),
        OperandEnumerant(512L, Vec()),
        OperandEnumerant(1024L, Vec()),
        OperandEnumerant(2048L, Vec()),
        OperandEnumerant(4096L, Vec()),
        OperandEnumerant(8192L, Vec())
      )
    ),
    OperandKindInfo(
      NonEmptyString("FPEncoding"),
      NonEmptyString("ValueEnum"),
      Vec(
        OperandEnumerant(0L, Vec()),
        OperandEnumerant(4214L, Vec()),
        OperandEnumerant(4215L, Vec()),
        OperandEnumerant(4223L, Vec()),
        OperandEnumerant(4224L, Vec()),
        OperandEnumerant(4225L, Vec()),
        OperandEnumerant(4226L, Vec()),
        OperandEnumerant(4227L, Vec())
      )
    ),
    OperandKindInfo(
      NonEmptyString("CooperativeVectorMatrixLayout"),
      NonEmptyString("ValueEnum"),
      Vec(
        OperandEnumerant(0L, Vec()),
        OperandEnumerant(1L, Vec()),
        OperandEnumerant(2L, Vec()),
        OperandEnumerant(3L, Vec())
      )
    ),
    OperandKindInfo(
      NonEmptyString("ComponentType"),
      NonEmptyString("ValueEnum"),
      Vec(
        OperandEnumerant(0L, Vec()),
        OperandEnumerant(1L, Vec()),
        OperandEnumerant(2L, Vec()),
        OperandEnumerant(3L, Vec()),
        OperandEnumerant(4L, Vec()),
        OperandEnumerant(5L, Vec()),
        OperandEnumerant(6L, Vec()),
        OperandEnumerant(7L, Vec()),
        OperandEnumerant(8L, Vec()),
        OperandEnumerant(9L, Vec()),
        OperandEnumerant(10L, Vec()),
        OperandEnumerant(1000491000L, Vec()),
        OperandEnumerant(1000491001L, Vec()),
        OperandEnumerant(1000491002L, Vec()),
        OperandEnumerant(1000491003L, Vec())
      )
    )
  )

  private def operandKinds7: Vec[OperandKindInfo] = Vec(
    OperandKindInfo(
      NonEmptyString("GatherModes"),
      NonEmptyString("ValueEnum"),
      Vec(
        OperandEnumerant(0L, Vec()),
        OperandEnumerant(1L, Vec()),
        OperandEnumerant(2L, Vec()),
        OperandEnumerant(3L, Vec())
      )
    ),
    OperandKindInfo(
      NonEmptyString("TensorOperands"),
      NonEmptyString("BitEnum"),
      Vec(
        OperandEnumerant(0L, Vec()),
        OperandEnumerant(1L, Vec()),
        OperandEnumerant(2L, Vec(NonEmptyString("IdRef"))),
        OperandEnumerant(4L, Vec(NonEmptyString("IdRef"))),
        OperandEnumerant(8L, Vec(NonEmptyString("IdRef"))),
        OperandEnumerant(16L, Vec())
      )
    )
  )

  private val operandKinds: Map[NonEmptyString, OperandKindInfo] =
    Vec(
      operandKinds0,
      operandKinds1,
      operandKinds2,
      operandKinds3,
      operandKinds4,
      operandKinds5,
      operandKinds6,
      operandKinds7
    ).flatten.view.map(value => value.kind -> value).toMap

  private val byNumber: Map[SPIRVOpcodeNumber, Vec[Definition]] =
    definitions.groupMap(_.opcodeNumber)(value => value)
  private val byOpcode: Map[NonEmptyString, Vec[Definition]] =
    definitions.groupMap(_.opcode)(value => value)

  def definitionsFor(opcodeNumber: SPIRVOpcodeNumber): Vec[Definition] =
    byNumber.getOrElse(opcodeNumber, Vec.empty[Definition])
  def definitionsFor(opcode: NonEmptyString): Vec[Definition] =
    byOpcode.getOrElse(opcode, Vec.empty[Definition])

  def decode(raw: RawInstruction): Either[InstructionDecodeError, Instruction] = {
    val candidates = definitionsFor(raw.opcodeNumber)
    candidates match {
      case Vec() => Left(InstructionDecodeError.UnknownOpcode(raw.opcodeNumber))
      case Vec(definition) => definition.decoder(raw)
      case definitions =>
        val matches = definitions.flatMap(definition =>
          definition.decoder(raw).toOption.map(instruction => definition -> instruction)
        )
        matches match {
          case Vec((_, instruction)) => Right(instruction)
          case Vec() =>
            Left(InstructionDecodeError.NoMatchingDefinition(
              raw.opcodeNumber,
              NonEmptyVec.unsafeFromVec(definitions.map(_.opcode))
            ))
          case values =>
            Left(InstructionDecodeError.AmbiguousDefinition(
              raw.opcodeNumber,
              NonEmptyVec.unsafeFromVec(values.map(_._1.opcode))
            ))
        }
    }
  }

  def encode(instruction: Instruction): RawInstruction =
    RawInstruction(instruction.definition.opcodeNumber, instruction.operands.flatMap(_.words.toVector))

  def minimumOperandWords(definition: Definition): Vec[Word] =
    OperandDecoder.minimumWords(definition.meta.operands, operandKinds)

  private[v1_6] def decodeDefinition(
      definition: Definition,
      raw: RawInstruction,
      construct: DecodedOperands => Instruction
  ): Either[InstructionDecodeError, Instruction] =
    if (raw.opcodeNumber != definition.opcodeNumber)
      Left(InstructionDecodeError.DecoderOpcodeMismatch(definition.opcodeNumber, raw.opcodeNumber))
    else
      OperandDecoder
        .decode(definition, raw.operandWords, definition.meta.operands, operandKinds)
        .map(construct)
}
