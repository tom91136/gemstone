// Generated from the pinned Khronos SPIR-V core grammar. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.spirv

import javax.annotation.processing.Generated

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.isa.spirv.OperandDecoder.*

@Generated(Array("sbt codegen"))
object Instruction {
  type Decoder = RawInstruction => Either[InstructionDecodeError, Instruction]

  enum Definition(
      val opcode: NonEmpty[java.lang.String],
      val opcodeNumber: SPIRVOpcodeNumber,
      decode: Definition => Decoder,
      operands: java.lang.String
  ) derives CanEqual {
    case Nop extends Definition(
          "OpNop",
          SPIRVOpcodeNumber(0),
          definition => raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.Nop()),
          ""
        )
    case Undef extends Definition(
          "OpUndef",
          SPIRVOpcodeNumber(1),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.Undef(values.required(0), values.required(1))
              ),
          "IdResultType IdResult"
        )
    case SourceContinued extends Definition(
          "OpSourceContinued",
          SPIRVOpcodeNumber(2),
          definition =>
            raw =>
              Instruction.decodeDefinition(definition, raw, values => Instruction.SourceContinued(values.required(0))),
          "LiteralString"
        )
    case Source extends Definition(
          "OpSource",
          SPIRVOpcodeNumber(3),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.Source(values.required(0), values.required(1), values.optional(2), values.optional(3))
              ),
          "SourceLanguage LiteralInteger IdRef? LiteralString?"
        )
    case SourceExtension extends Definition(
          "OpSourceExtension",
          SPIRVOpcodeNumber(4),
          definition =>
            raw =>
              Instruction.decodeDefinition(definition, raw, values => Instruction.SourceExtension(values.required(0))),
          "LiteralString"
        )
    case Name extends Definition(
          "OpName",
          SPIRVOpcodeNumber(5),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.Name(values.required(0), values.required(1))
              ),
          "IdRef LiteralString"
        )
    case MemberName extends Definition(
          "OpMemberName",
          SPIRVOpcodeNumber(6),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.MemberName(values.required(0), values.required(1), values.required(2))
              ),
          "IdRef LiteralInteger LiteralString"
        )
    case String extends Definition(
          "OpString",
          SPIRVOpcodeNumber(7),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.String(values.required(0), values.required(1))
              ),
          "IdResult LiteralString"
        )
    case Line extends Definition(
          "OpLine",
          SPIRVOpcodeNumber(8),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.Line(values.required(0), values.required(1), values.required(2))
              ),
          "IdRef LiteralInteger LiteralInteger"
        )
    case Extension extends Definition(
          "OpExtension",
          SPIRVOpcodeNumber(10),
          definition =>
            raw => Instruction.decodeDefinition(definition, raw, values => Instruction.Extension(values.required(0))),
          "LiteralString"
        )
    case ExtInstImport extends Definition(
          "OpExtInstImport",
          SPIRVOpcodeNumber(11),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ExtInstImport(values.required(0), values.required(1))
              ),
          "IdResult LiteralString"
        )
    case ExtInst extends Definition(
          "OpExtInst",
          SPIRVOpcodeNumber(12),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ExtInst(
                    resultType = values.required(0),
                    dest = values.required(1),
                    set = values.required(2),
                    inst = values.required(3),
                    srcs = values.repeated(4)
                  )
              ),
          "IdResultType IdResult IdRef LiteralExtInstInteger IdRef*"
        )
    case MemoryModel extends Definition(
          "OpMemoryModel",
          SPIRVOpcodeNumber(14),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.MemoryModel(values.required(0), values.required(1))
              ),
          "AddressingModel MemoryModel"
        )
    case EntryPoint extends Definition(
          "OpEntryPoint",
          SPIRVOpcodeNumber(15),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.EntryPoint(values.required(0), values.required(1), values.required(2), values.repeated(3))
              ),
          "ExecutionModel IdRef LiteralString IdRef*"
        )
    case ExecutionMode extends Definition(
          "OpExecutionMode",
          SPIRVOpcodeNumber(16),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ExecutionMode(values.required(0), values.required(1))
              ),
          "IdRef ExecutionMode"
        )
    case Capability extends Definition(
          "OpCapability",
          SPIRVOpcodeNumber(17),
          definition =>
            raw => Instruction.decodeDefinition(definition, raw, values => Instruction.Capability(values.required(0))),
          "Capability"
        )
    case TypeVoid extends Definition(
          "OpTypeVoid",
          SPIRVOpcodeNumber(19),
          definition =>
            raw => Instruction.decodeDefinition(definition, raw, values => Instruction.TypeVoid(values.required(0))),
          "IdResult"
        )
    case TypeBool extends Definition(
          "OpTypeBool",
          SPIRVOpcodeNumber(20),
          definition =>
            raw => Instruction.decodeDefinition(definition, raw, values => Instruction.TypeBool(values.required(0))),
          "IdResult"
        )
    case TypeInt extends Definition(
          "OpTypeInt",
          SPIRVOpcodeNumber(21),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeInt(values.required(0), values.required(1), values.required(2))
              ),
          "IdResult LiteralInteger LiteralInteger"
        )
    case TypeFloat extends Definition(
          "OpTypeFloat",
          SPIRVOpcodeNumber(22),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeFloat(values.required(0), values.required(1), values.optional(2))
              ),
          "IdResult LiteralInteger FPEncoding?"
        )
    case TypeVector extends Definition(
          "OpTypeVector",
          SPIRVOpcodeNumber(23),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeVector(values.required(0), values.required(1), values.required(2))
              ),
          "IdResult IdRef LiteralInteger"
        )
    case TypeMatrix extends Definition(
          "OpTypeMatrix",
          SPIRVOpcodeNumber(24),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeMatrix(values.required(0), values.required(1), values.required(2))
              ),
          "IdResult IdRef LiteralInteger"
        )
    case TypeImage extends Definition(
          "OpTypeImage",
          SPIRVOpcodeNumber(25),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.TypeImage(
                    dest = values.required(0),
                    sampledType = values.required(1),
                    dim = values.required(2),
                    depth = values.required(3),
                    arrayed = values.required(4),
                    mS = values.required(5),
                    sampled = values.required(6),
                    imageFormat = values.required(7),
                    accessQualifier = values.optional(8)
                  )
              ),
          "IdResult IdRef Dim LiteralInteger LiteralInteger LiteralInteger LiteralInteger ImageFormat AccessQualifier?"
        )
    case TypeSampler extends Definition(
          "OpTypeSampler",
          SPIRVOpcodeNumber(26),
          definition =>
            raw => Instruction.decodeDefinition(definition, raw, values => Instruction.TypeSampler(values.required(0))),
          "IdResult"
        )
    case TypeSampledImage extends Definition(
          "OpTypeSampledImage",
          SPIRVOpcodeNumber(27),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeSampledImage(values.required(0), values.required(1))
              ),
          "IdResult IdRef"
        )
    case TypeArray extends Definition(
          "OpTypeArray",
          SPIRVOpcodeNumber(28),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeArray(values.required(0), values.required(1), values.required(2))
              ),
          "IdResult IdRef IdRef"
        )
    case TypeRuntimeArray extends Definition(
          "OpTypeRuntimeArray",
          SPIRVOpcodeNumber(29),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeRuntimeArray(values.required(0), values.required(1))
              ),
          "IdResult IdRef"
        )
    case TypeStruct extends Definition(
          "OpTypeStruct",
          SPIRVOpcodeNumber(30),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeStruct(values.required(0), values.repeated(1))
              ),
          "IdResult IdRef*"
        )
    case TypeOpaque extends Definition(
          "OpTypeOpaque",
          SPIRVOpcodeNumber(31),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeOpaque(values.required(0), values.required(1))
              ),
          "IdResult LiteralString"
        )
    case TypePointer extends Definition(
          "OpTypePointer",
          SPIRVOpcodeNumber(32),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypePointer(values.required(0), values.required(1), values.required(2))
              ),
          "IdResult StorageClass IdRef"
        )
    case TypeFunction extends Definition(
          "OpTypeFunction",
          SPIRVOpcodeNumber(33),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeFunction(values.required(0), values.required(1), values.repeated(2))
              ),
          "IdResult IdRef IdRef*"
        )
    case TypeEvent extends Definition(
          "OpTypeEvent",
          SPIRVOpcodeNumber(34),
          definition =>
            raw => Instruction.decodeDefinition(definition, raw, values => Instruction.TypeEvent(values.required(0))),
          "IdResult"
        )
    case TypeDeviceEvent extends Definition(
          "OpTypeDeviceEvent",
          SPIRVOpcodeNumber(35),
          definition =>
            raw =>
              Instruction.decodeDefinition(definition, raw, values => Instruction.TypeDeviceEvent(values.required(0))),
          "IdResult"
        )
    case TypeReserveId extends Definition(
          "OpTypeReserveId",
          SPIRVOpcodeNumber(36),
          definition =>
            raw =>
              Instruction.decodeDefinition(definition, raw, values => Instruction.TypeReserveId(values.required(0))),
          "IdResult"
        )
    case TypeQueue extends Definition(
          "OpTypeQueue",
          SPIRVOpcodeNumber(37),
          definition =>
            raw => Instruction.decodeDefinition(definition, raw, values => Instruction.TypeQueue(values.required(0))),
          "IdResult"
        )
    case TypePipe extends Definition(
          "OpTypePipe",
          SPIRVOpcodeNumber(38),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypePipe(values.required(0), values.required(1))
              ),
          "IdResult AccessQualifier"
        )
    case TypeForwardPointer extends Definition(
          "OpTypeForwardPointer",
          SPIRVOpcodeNumber(39),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeForwardPointer(values.required(0), values.required(1))
              ),
          "IdRef StorageClass"
        )
    case ConstantTrue extends Definition(
          "OpConstantTrue",
          SPIRVOpcodeNumber(41),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ConstantTrue(values.required(0), values.required(1))
              ),
          "IdResultType IdResult"
        )
    case ConstantFalse extends Definition(
          "OpConstantFalse",
          SPIRVOpcodeNumber(42),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ConstantFalse(values.required(0), values.required(1))
              ),
          "IdResultType IdResult"
        )
    case Constant extends Definition(
          "OpConstant",
          SPIRVOpcodeNumber(43),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.Constant(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult LiteralContextDependentNumber"
        )
    case ConstantComposite extends Definition(
          "OpConstantComposite",
          SPIRVOpcodeNumber(44),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ConstantComposite(values.required(0), values.required(1), values.repeated(2))
              ),
          "IdResultType IdResult IdRef*"
        )
    case ConstantSampler extends Definition(
          "OpConstantSampler",
          SPIRVOpcodeNumber(45),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ConstantSampler(
                    resultType = values.required(0),
                    dest = values.required(1),
                    samplerAddressingMode = values.required(2),
                    param = values.required(3),
                    samplerFilterMode = values.required(4)
                  )
              ),
          "IdResultType IdResult SamplerAddressingMode LiteralInteger SamplerFilterMode"
        )
    case ConstantNull extends Definition(
          "OpConstantNull",
          SPIRVOpcodeNumber(46),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ConstantNull(values.required(0), values.required(1))
              ),
          "IdResultType IdResult"
        )
    case SpecConstantTrue extends Definition(
          "OpSpecConstantTrue",
          SPIRVOpcodeNumber(48),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.SpecConstantTrue(values.required(0), values.required(1))
              ),
          "IdResultType IdResult"
        )
    case SpecConstantFalse extends Definition(
          "OpSpecConstantFalse",
          SPIRVOpcodeNumber(49),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.SpecConstantFalse(values.required(0), values.required(1))
              ),
          "IdResultType IdResult"
        )
    case SpecConstant extends Definition(
          "OpSpecConstant",
          SPIRVOpcodeNumber(50),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.SpecConstant(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult LiteralContextDependentNumber"
        )
    case SpecConstantComposite extends Definition(
          "OpSpecConstantComposite",
          SPIRVOpcodeNumber(51),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.SpecConstantComposite(values.required(0), values.required(1), values.repeated(2))
              ),
          "IdResultType IdResult IdRef*"
        )
    case SpecConstantOp extends Definition(
          "OpSpecConstantOp",
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
          "IdResultType IdResult LiteralSpecConstantOpInteger LiteralInteger*"
        )
    case Function extends Definition(
          "OpFunction",
          SPIRVOpcodeNumber(54),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.Function(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          "IdResultType IdResult FunctionControl IdRef"
        )
    case FunctionParameter extends Definition(
          "OpFunctionParameter",
          SPIRVOpcodeNumber(55),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.FunctionParameter(values.required(0), values.required(1))
              ),
          "IdResultType IdResult"
        )
    case FunctionEnd extends Definition(
          "OpFunctionEnd",
          SPIRVOpcodeNumber(56),
          definition => raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.FunctionEnd()),
          ""
        )
    case FunctionCall extends Definition(
          "OpFunctionCall",
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
          "IdResultType IdResult IdRef IdRef*"
        )
    case Variable extends Definition(
          "OpVariable",
          SPIRVOpcodeNumber(59),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.Variable(values.required(0), values.required(1), values.required(2), values.optional(3))
              ),
          "IdResultType IdResult StorageClass IdRef?"
        )
    case ImageTexelPointer extends Definition(
          "OpImageTexelPointer",
          SPIRVOpcodeNumber(60),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageTexelPointer(
                    resultType = values.required(0),
                    dest = values.required(1),
                    image = values.required(2),
                    coordinate = values.required(3),
                    sample = values.required(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef"
        )
    case Load extends Definition(
          "OpLoad",
          SPIRVOpcodeNumber(61),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.Load(values.required(0), values.required(1), values.required(2), values.optional(3))
              ),
          "IdResultType IdResult IdRef MemoryAccess?"
        )
    case Store extends Definition(
          "OpStore",
          SPIRVOpcodeNumber(62),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.Store(values.required(0), values.required(1), values.optional(2))
              ),
          "IdRef IdRef MemoryAccess?"
        )
    case CopyMemory extends Definition(
          "OpCopyMemory",
          SPIRVOpcodeNumber(63),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CopyMemory(values.required(0), values.required(1), values.optional(2), values.optional(3))
              ),
          "IdRef IdRef MemoryAccess? MemoryAccess?"
        )
    case CopyMemorySized extends Definition(
          "OpCopyMemorySized",
          SPIRVOpcodeNumber(64),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CopyMemorySized(
                    target = values.required(0),
                    src = values.required(1),
                    size = values.required(2),
                    modsA = values.optional(3),
                    modsB = values.optional(4)
                  )
              ),
          "IdRef IdRef IdRef MemoryAccess? MemoryAccess?"
        )
    case AccessChain extends Definition(
          "OpAccessChain",
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
          "IdResultType IdResult IdRef IdRef*"
        )
    case InBoundsAccessChain extends Definition(
          "OpInBoundsAccessChain",
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
          "IdResultType IdResult IdRef IdRef*"
        )
    case PtrAccessChain extends Definition(
          "OpPtrAccessChain",
          SPIRVOpcodeNumber(67),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.PtrAccessChain(
                    resultType = values.required(0),
                    dest = values.required(1),
                    base = values.required(2),
                    element = values.required(3),
                    indexes = values.repeated(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef*"
        )
    case ArrayLength extends Definition(
          "OpArrayLength",
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
          "IdResultType IdResult IdRef LiteralInteger"
        )
    case GenericPtrMemSemantics extends Definition(
          "OpGenericPtrMemSemantics",
          SPIRVOpcodeNumber(69),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.GenericPtrMemSemantics(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case InBoundsPtrAccessChain extends Definition(
          "OpInBoundsPtrAccessChain",
          SPIRVOpcodeNumber(70),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.InBoundsPtrAccessChain(
                    resultType = values.required(0),
                    dest = values.required(1),
                    base = values.required(2),
                    element = values.required(3),
                    indexes = values.repeated(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef*"
        )
    case Decorate extends Definition(
          "OpDecorate",
          SPIRVOpcodeNumber(71),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.Decorate(values.required(0), values.required(1))
              ),
          "IdRef Decoration"
        )
    case MemberDecorate extends Definition(
          "OpMemberDecorate",
          SPIRVOpcodeNumber(72),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.MemberDecorate(values.required(0), values.required(1), values.required(2))
              ),
          "IdRef LiteralInteger Decoration"
        )
    case DecorationGroup extends Definition(
          "OpDecorationGroup",
          SPIRVOpcodeNumber(73),
          definition =>
            raw =>
              Instruction.decodeDefinition(definition, raw, values => Instruction.DecorationGroup(values.required(0))),
          "IdResult"
        )
    case GroupDecorate extends Definition(
          "OpGroupDecorate",
          SPIRVOpcodeNumber(74),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.GroupDecorate(values.required(0), values.repeated(1))
              ),
          "IdRef IdRef*"
        )
    case GroupMemberDecorate extends Definition(
          "OpGroupMemberDecorate",
          SPIRVOpcodeNumber(75),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.GroupMemberDecorate(values.required(0), values.repeated(1))
              ),
          "IdRef PairIdRefLiteralInteger*"
        )
    case VectorExtractDynamic extends Definition(
          "OpVectorExtractDynamic",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case VectorInsertDynamic extends Definition(
          "OpVectorInsertDynamic",
          SPIRVOpcodeNumber(78),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.VectorInsertDynamic(
                    resultType = values.required(0),
                    dest = values.required(1),
                    vector = values.required(2),
                    component = values.required(3),
                    index = values.required(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef"
        )
    case VectorShuffle extends Definition(
          "OpVectorShuffle",
          SPIRVOpcodeNumber(79),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.VectorShuffle(
                    resultType = values.required(0),
                    dest = values.required(1),
                    vector1 = values.required(2),
                    vector2 = values.required(3),
                    components = values.repeated(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef LiteralInteger*"
        )
    case CompositeConstruct extends Definition(
          "OpCompositeConstruct",
          SPIRVOpcodeNumber(80),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.CompositeConstruct(values.required(0), values.required(1), values.repeated(2))
              ),
          "IdResultType IdResult IdRef*"
        )
    case CompositeExtract extends Definition(
          "OpCompositeExtract",
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
          "IdResultType IdResult IdRef LiteralInteger*"
        )
    case CompositeInsert extends Definition(
          "OpCompositeInsert",
          SPIRVOpcodeNumber(82),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CompositeInsert(
                    resultType = values.required(0),
                    dest = values.required(1),
                    src = values.required(2),
                    composite = values.required(3),
                    indexes = values.repeated(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef LiteralInteger*"
        )
    case CopyObject extends Definition(
          "OpCopyObject",
          SPIRVOpcodeNumber(83),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.CopyObject(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case Transpose extends Definition(
          "OpTranspose",
          SPIRVOpcodeNumber(84),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.Transpose(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case SampledImage extends Definition(
          "OpSampledImage",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case ImageSampleImplicitLod extends Definition(
          "OpImageSampleImplicitLod",
          SPIRVOpcodeNumber(87),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageSampleImplicitLod(
                    resultType = values.required(0),
                    dest = values.required(1),
                    sampledImage = values.required(2),
                    coordinate = values.required(3),
                    mods = values.optional(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef ImageOperands?"
        )
    case ImageSampleExplicitLod extends Definition(
          "OpImageSampleExplicitLod",
          SPIRVOpcodeNumber(88),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageSampleExplicitLod(
                    resultType = values.required(0),
                    dest = values.required(1),
                    sampledImage = values.required(2),
                    coordinate = values.required(3),
                    mods = values.required(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef ImageOperands"
        )
    case ImageSampleDrefImplicitLod extends Definition(
          "OpImageSampleDrefImplicitLod",
          SPIRVOpcodeNumber(89),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageSampleDrefImplicitLod(
                    resultType = values.required(0),
                    dest = values.required(1),
                    sampledImage = values.required(2),
                    coordinate = values.required(3),
                    dref = values.required(4),
                    mods = values.optional(5)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef ImageOperands?"
        )
    case ImageSampleDrefExplicitLod extends Definition(
          "OpImageSampleDrefExplicitLod",
          SPIRVOpcodeNumber(90),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageSampleDrefExplicitLod(
                    resultType = values.required(0),
                    dest = values.required(1),
                    sampledImage = values.required(2),
                    coordinate = values.required(3),
                    dref = values.required(4),
                    mods = values.required(5)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef ImageOperands"
        )
    case ImageSampleProjImplicitLod extends Definition(
          "OpImageSampleProjImplicitLod",
          SPIRVOpcodeNumber(91),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageSampleProjImplicitLod(
                    resultType = values.required(0),
                    dest = values.required(1),
                    sampledImage = values.required(2),
                    coordinate = values.required(3),
                    mods = values.optional(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef ImageOperands?"
        )
    case ImageSampleProjExplicitLod extends Definition(
          "OpImageSampleProjExplicitLod",
          SPIRVOpcodeNumber(92),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageSampleProjExplicitLod(
                    resultType = values.required(0),
                    dest = values.required(1),
                    sampledImage = values.required(2),
                    coordinate = values.required(3),
                    mods = values.required(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef ImageOperands"
        )
    case ImageSampleProjDrefImplicitLod extends Definition(
          "OpImageSampleProjDrefImplicitLod",
          SPIRVOpcodeNumber(93),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageSampleProjDrefImplicitLod(
                    resultType = values.required(0),
                    dest = values.required(1),
                    sampledImage = values.required(2),
                    coordinate = values.required(3),
                    dref = values.required(4),
                    mods = values.optional(5)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef ImageOperands?"
        )
    case ImageSampleProjDrefExplicitLod extends Definition(
          "OpImageSampleProjDrefExplicitLod",
          SPIRVOpcodeNumber(94),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageSampleProjDrefExplicitLod(
                    resultType = values.required(0),
                    dest = values.required(1),
                    sampledImage = values.required(2),
                    coordinate = values.required(3),
                    dref = values.required(4),
                    mods = values.required(5)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef ImageOperands"
        )
    case ImageFetch extends Definition(
          "OpImageFetch",
          SPIRVOpcodeNumber(95),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageFetch(
                    resultType = values.required(0),
                    dest = values.required(1),
                    image = values.required(2),
                    coordinate = values.required(3),
                    mods = values.optional(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef ImageOperands?"
        )
    case ImageGather extends Definition(
          "OpImageGather",
          SPIRVOpcodeNumber(96),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageGather(
                    resultType = values.required(0),
                    dest = values.required(1),
                    sampledImage = values.required(2),
                    coordinate = values.required(3),
                    component = values.required(4),
                    mods = values.optional(5)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef ImageOperands?"
        )
    case ImageDrefGather extends Definition(
          "OpImageDrefGather",
          SPIRVOpcodeNumber(97),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageDrefGather(
                    resultType = values.required(0),
                    dest = values.required(1),
                    sampledImage = values.required(2),
                    coordinate = values.required(3),
                    dref = values.required(4),
                    mods = values.optional(5)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef ImageOperands?"
        )
    case ImageRead extends Definition(
          "OpImageRead",
          SPIRVOpcodeNumber(98),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageRead(
                    resultType = values.required(0),
                    dest = values.required(1),
                    image = values.required(2),
                    coordinate = values.required(3),
                    mods = values.optional(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef ImageOperands?"
        )
    case ImageWrite extends Definition(
          "OpImageWrite",
          SPIRVOpcodeNumber(99),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageWrite(values.required(0), values.required(1), values.required(2), values.optional(3))
              ),
          "IdRef IdRef IdRef ImageOperands?"
        )
    case Image extends Definition(
          "OpImage",
          SPIRVOpcodeNumber(100),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.Image(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case ImageQueryFormat extends Definition(
          "OpImageQueryFormat",
          SPIRVOpcodeNumber(101),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ImageQueryFormat(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case ImageQueryOrder extends Definition(
          "OpImageQueryOrder",
          SPIRVOpcodeNumber(102),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ImageQueryOrder(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case ImageQuerySizeLod extends Definition(
          "OpImageQuerySizeLod",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case ImageQuerySize extends Definition(
          "OpImageQuerySize",
          SPIRVOpcodeNumber(104),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ImageQuerySize(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case ImageQueryLod extends Definition(
          "OpImageQueryLod",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case ImageQueryLevels extends Definition(
          "OpImageQueryLevels",
          SPIRVOpcodeNumber(106),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ImageQueryLevels(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case ImageQuerySamples extends Definition(
          "OpImageQuerySamples",
          SPIRVOpcodeNumber(107),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ImageQuerySamples(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case ConvertFToU extends Definition(
          "OpConvertFToU",
          SPIRVOpcodeNumber(109),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ConvertFToU(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case ConvertFToS extends Definition(
          "OpConvertFToS",
          SPIRVOpcodeNumber(110),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ConvertFToS(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case ConvertSToF extends Definition(
          "OpConvertSToF",
          SPIRVOpcodeNumber(111),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ConvertSToF(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case ConvertUToF extends Definition(
          "OpConvertUToF",
          SPIRVOpcodeNumber(112),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ConvertUToF(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case UConvert extends Definition(
          "OpUConvert",
          SPIRVOpcodeNumber(113),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.UConvert(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case SConvert extends Definition(
          "OpSConvert",
          SPIRVOpcodeNumber(114),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.SConvert(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case FConvert extends Definition(
          "OpFConvert",
          SPIRVOpcodeNumber(115),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.FConvert(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case QuantizeToF16 extends Definition(
          "OpQuantizeToF16",
          SPIRVOpcodeNumber(116),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.QuantizeToF16(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case ConvertPtrToU extends Definition(
          "OpConvertPtrToU",
          SPIRVOpcodeNumber(117),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ConvertPtrToU(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case SatConvertSToU extends Definition(
          "OpSatConvertSToU",
          SPIRVOpcodeNumber(118),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.SatConvertSToU(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case SatConvertUToS extends Definition(
          "OpSatConvertUToS",
          SPIRVOpcodeNumber(119),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.SatConvertUToS(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case ConvertUToPtr extends Definition(
          "OpConvertUToPtr",
          SPIRVOpcodeNumber(120),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ConvertUToPtr(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case PtrCastToGeneric extends Definition(
          "OpPtrCastToGeneric",
          SPIRVOpcodeNumber(121),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.PtrCastToGeneric(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case GenericCastToPtr extends Definition(
          "OpGenericCastToPtr",
          SPIRVOpcodeNumber(122),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.GenericCastToPtr(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case GenericCastToPtrExplicit extends Definition(
          "OpGenericCastToPtrExplicit",
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
          "IdResultType IdResult IdRef StorageClass"
        )
    case Bitcast extends Definition(
          "OpBitcast",
          SPIRVOpcodeNumber(124),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.Bitcast(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case SNegate extends Definition(
          "OpSNegate",
          SPIRVOpcodeNumber(126),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.SNegate(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case FNegate extends Definition(
          "OpFNegate",
          SPIRVOpcodeNumber(127),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.FNegate(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case IAdd extends Definition(
          "OpIAdd",
          SPIRVOpcodeNumber(128),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.IAdd(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          "IdResultType IdResult IdRef IdRef"
        )
    case FAdd extends Definition(
          "OpFAdd",
          SPIRVOpcodeNumber(129),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FAdd(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          "IdResultType IdResult IdRef IdRef"
        )
    case ISub extends Definition(
          "OpISub",
          SPIRVOpcodeNumber(130),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ISub(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          "IdResultType IdResult IdRef IdRef"
        )
    case FSub extends Definition(
          "OpFSub",
          SPIRVOpcodeNumber(131),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FSub(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          "IdResultType IdResult IdRef IdRef"
        )
    case IMul extends Definition(
          "OpIMul",
          SPIRVOpcodeNumber(132),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.IMul(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          "IdResultType IdResult IdRef IdRef"
        )
    case FMul extends Definition(
          "OpFMul",
          SPIRVOpcodeNumber(133),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FMul(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          "IdResultType IdResult IdRef IdRef"
        )
    case UDiv extends Definition(
          "OpUDiv",
          SPIRVOpcodeNumber(134),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.UDiv(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          "IdResultType IdResult IdRef IdRef"
        )
    case SDiv extends Definition(
          "OpSDiv",
          SPIRVOpcodeNumber(135),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SDiv(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          "IdResultType IdResult IdRef IdRef"
        )
    case FDiv extends Definition(
          "OpFDiv",
          SPIRVOpcodeNumber(136),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FDiv(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          "IdResultType IdResult IdRef IdRef"
        )
    case UMod extends Definition(
          "OpUMod",
          SPIRVOpcodeNumber(137),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.UMod(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          "IdResultType IdResult IdRef IdRef"
        )
    case SRem extends Definition(
          "OpSRem",
          SPIRVOpcodeNumber(138),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SRem(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          "IdResultType IdResult IdRef IdRef"
        )
    case SMod extends Definition(
          "OpSMod",
          SPIRVOpcodeNumber(139),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SMod(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          "IdResultType IdResult IdRef IdRef"
        )
    case FRem extends Definition(
          "OpFRem",
          SPIRVOpcodeNumber(140),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FRem(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          "IdResultType IdResult IdRef IdRef"
        )
    case FMod extends Definition(
          "OpFMod",
          SPIRVOpcodeNumber(141),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FMod(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          "IdResultType IdResult IdRef IdRef"
        )
    case VectorTimesScalar extends Definition(
          "OpVectorTimesScalar",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case MatrixTimesScalar extends Definition(
          "OpMatrixTimesScalar",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case VectorTimesMatrix extends Definition(
          "OpVectorTimesMatrix",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case MatrixTimesVector extends Definition(
          "OpMatrixTimesVector",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case MatrixTimesMatrix extends Definition(
          "OpMatrixTimesMatrix",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case OuterProduct extends Definition(
          "OpOuterProduct",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case Dot extends Definition(
          "OpDot",
          SPIRVOpcodeNumber(148),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.Dot(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          "IdResultType IdResult IdRef IdRef"
        )
    case IAddCarry extends Definition(
          "OpIAddCarry",
          SPIRVOpcodeNumber(149),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.IAddCarry(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          "IdResultType IdResult IdRef IdRef"
        )
    case ISubBorrow extends Definition(
          "OpISubBorrow",
          SPIRVOpcodeNumber(150),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ISubBorrow(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          "IdResultType IdResult IdRef IdRef"
        )
    case UMulExtended extends Definition(
          "OpUMulExtended",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case SMulExtended extends Definition(
          "OpSMulExtended",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case Any extends Definition(
          "OpAny",
          SPIRVOpcodeNumber(154),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.Any(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case All extends Definition(
          "OpAll",
          SPIRVOpcodeNumber(155),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.All(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case IsNan extends Definition(
          "OpIsNan",
          SPIRVOpcodeNumber(156),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.IsNan(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case IsInf extends Definition(
          "OpIsInf",
          SPIRVOpcodeNumber(157),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.IsInf(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case IsFinite extends Definition(
          "OpIsFinite",
          SPIRVOpcodeNumber(158),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.IsFinite(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case IsNormal extends Definition(
          "OpIsNormal",
          SPIRVOpcodeNumber(159),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.IsNormal(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case SignBitSet extends Definition(
          "OpSignBitSet",
          SPIRVOpcodeNumber(160),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.SignBitSet(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case LessOrGreater extends Definition(
          "OpLessOrGreater",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case Ordered extends Definition(
          "OpOrdered",
          SPIRVOpcodeNumber(162),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.Ordered(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          "IdResultType IdResult IdRef IdRef"
        )
    case Unordered extends Definition(
          "OpUnordered",
          SPIRVOpcodeNumber(163),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.Unordered(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          "IdResultType IdResult IdRef IdRef"
        )
    case LogicalEqual extends Definition(
          "OpLogicalEqual",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case LogicalNotEqual extends Definition(
          "OpLogicalNotEqual",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case LogicalOr extends Definition(
          "OpLogicalOr",
          SPIRVOpcodeNumber(166),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.LogicalOr(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          "IdResultType IdResult IdRef IdRef"
        )
    case LogicalAnd extends Definition(
          "OpLogicalAnd",
          SPIRVOpcodeNumber(167),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.LogicalAnd(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          "IdResultType IdResult IdRef IdRef"
        )
    case LogicalNot extends Definition(
          "OpLogicalNot",
          SPIRVOpcodeNumber(168),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.LogicalNot(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case Select extends Definition(
          "OpSelect",
          SPIRVOpcodeNumber(169),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.Select(
                    resultType = values.required(0),
                    dest = values.required(1),
                    pred = values.required(2),
                    object1 = values.required(3),
                    object2 = values.required(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef"
        )
    case IEqual extends Definition(
          "OpIEqual",
          SPIRVOpcodeNumber(170),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.IEqual(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          "IdResultType IdResult IdRef IdRef"
        )
    case INotEqual extends Definition(
          "OpINotEqual",
          SPIRVOpcodeNumber(171),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.INotEqual(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          "IdResultType IdResult IdRef IdRef"
        )
    case UGreaterThan extends Definition(
          "OpUGreaterThan",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case SGreaterThan extends Definition(
          "OpSGreaterThan",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case UGreaterThanEqual extends Definition(
          "OpUGreaterThanEqual",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case SGreaterThanEqual extends Definition(
          "OpSGreaterThanEqual",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case ULessThan extends Definition(
          "OpULessThan",
          SPIRVOpcodeNumber(176),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ULessThan(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          "IdResultType IdResult IdRef IdRef"
        )
    case SLessThan extends Definition(
          "OpSLessThan",
          SPIRVOpcodeNumber(177),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SLessThan(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          "IdResultType IdResult IdRef IdRef"
        )
    case ULessThanEqual extends Definition(
          "OpULessThanEqual",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case SLessThanEqual extends Definition(
          "OpSLessThanEqual",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case FOrdEqual extends Definition(
          "OpFOrdEqual",
          SPIRVOpcodeNumber(180),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FOrdEqual(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          "IdResultType IdResult IdRef IdRef"
        )
    case FUnordEqual extends Definition(
          "OpFUnordEqual",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case FOrdNotEqual extends Definition(
          "OpFOrdNotEqual",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case FUnordNotEqual extends Definition(
          "OpFUnordNotEqual",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case FOrdLessThan extends Definition(
          "OpFOrdLessThan",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case FUnordLessThan extends Definition(
          "OpFUnordLessThan",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case FOrdGreaterThan extends Definition(
          "OpFOrdGreaterThan",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case FUnordGreaterThan extends Definition(
          "OpFUnordGreaterThan",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case FOrdLessThanEqual extends Definition(
          "OpFOrdLessThanEqual",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case FUnordLessThanEqual extends Definition(
          "OpFUnordLessThanEqual",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case FOrdGreaterThanEqual extends Definition(
          "OpFOrdGreaterThanEqual",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case FUnordGreaterThanEqual extends Definition(
          "OpFUnordGreaterThanEqual",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case ShiftRightLogical extends Definition(
          "OpShiftRightLogical",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case ShiftRightArithmetic extends Definition(
          "OpShiftRightArithmetic",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case ShiftLeftLogical extends Definition(
          "OpShiftLeftLogical",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case BitwiseOr extends Definition(
          "OpBitwiseOr",
          SPIRVOpcodeNumber(197),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.BitwiseOr(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          "IdResultType IdResult IdRef IdRef"
        )
    case BitwiseXor extends Definition(
          "OpBitwiseXor",
          SPIRVOpcodeNumber(198),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.BitwiseXor(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          "IdResultType IdResult IdRef IdRef"
        )
    case BitwiseAnd extends Definition(
          "OpBitwiseAnd",
          SPIRVOpcodeNumber(199),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.BitwiseAnd(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          "IdResultType IdResult IdRef IdRef"
        )
    case Not extends Definition(
          "OpNot",
          SPIRVOpcodeNumber(200),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.Not(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case BitFieldInsert extends Definition(
          "OpBitFieldInsert",
          SPIRVOpcodeNumber(201),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.BitFieldInsert(
                    resultType = values.required(0),
                    dest = values.required(1),
                    base = values.required(2),
                    insert = values.required(3),
                    offset = values.required(4),
                    count = values.required(5)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef"
        )
    case BitFieldSExtract extends Definition(
          "OpBitFieldSExtract",
          SPIRVOpcodeNumber(202),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.BitFieldSExtract(
                    resultType = values.required(0),
                    dest = values.required(1),
                    base = values.required(2),
                    offset = values.required(3),
                    count = values.required(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef"
        )
    case BitFieldUExtract extends Definition(
          "OpBitFieldUExtract",
          SPIRVOpcodeNumber(203),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.BitFieldUExtract(
                    resultType = values.required(0),
                    dest = values.required(1),
                    base = values.required(2),
                    offset = values.required(3),
                    count = values.required(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef"
        )
    case BitReverse extends Definition(
          "OpBitReverse",
          SPIRVOpcodeNumber(204),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.BitReverse(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case BitCount extends Definition(
          "OpBitCount",
          SPIRVOpcodeNumber(205),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.BitCount(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case DPdx extends Definition(
          "OpDPdx",
          SPIRVOpcodeNumber(207),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.DPdx(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case DPdy extends Definition(
          "OpDPdy",
          SPIRVOpcodeNumber(208),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.DPdy(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case Fwidth extends Definition(
          "OpFwidth",
          SPIRVOpcodeNumber(209),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.Fwidth(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case DPdxFine extends Definition(
          "OpDPdxFine",
          SPIRVOpcodeNumber(210),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.DPdxFine(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case DPdyFine extends Definition(
          "OpDPdyFine",
          SPIRVOpcodeNumber(211),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.DPdyFine(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case FwidthFine extends Definition(
          "OpFwidthFine",
          SPIRVOpcodeNumber(212),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.FwidthFine(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case DPdxCoarse extends Definition(
          "OpDPdxCoarse",
          SPIRVOpcodeNumber(213),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.DPdxCoarse(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case DPdyCoarse extends Definition(
          "OpDPdyCoarse",
          SPIRVOpcodeNumber(214),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.DPdyCoarse(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case FwidthCoarse extends Definition(
          "OpFwidthCoarse",
          SPIRVOpcodeNumber(215),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.FwidthCoarse(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case EmitVertex extends Definition(
          "OpEmitVertex",
          SPIRVOpcodeNumber(218),
          definition => raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.EmitVertex()),
          ""
        )
    case EndPrimitive extends Definition(
          "OpEndPrimitive",
          SPIRVOpcodeNumber(219),
          definition => raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.EndPrimitive()),
          ""
        )
    case EmitStreamVertex extends Definition(
          "OpEmitStreamVertex",
          SPIRVOpcodeNumber(220),
          definition =>
            raw =>
              Instruction.decodeDefinition(definition, raw, values => Instruction.EmitStreamVertex(values.required(0))),
          "IdRef"
        )
    case EndStreamPrimitive extends Definition(
          "OpEndStreamPrimitive",
          SPIRVOpcodeNumber(221),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.EndStreamPrimitive(values.required(0))
              ),
          "IdRef"
        )
    case ControlBarrier extends Definition(
          "OpControlBarrier",
          SPIRVOpcodeNumber(224),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ControlBarrier(values.required(0), values.required(1), values.required(2))
              ),
          "IdScope IdScope IdMemorySemantics"
        )
    case MemoryBarrier extends Definition(
          "OpMemoryBarrier",
          SPIRVOpcodeNumber(225),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.MemoryBarrier(values.required(0), values.required(1))
              ),
          "IdScope IdMemorySemantics"
        )
    case AtomicLoad extends Definition(
          "OpAtomicLoad",
          SPIRVOpcodeNumber(227),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.AtomicLoad(
                    resultType = values.required(0),
                    dest = values.required(1),
                    pointer = values.required(2),
                    memory = values.required(3),
                    semantics = values.required(4)
                  )
              ),
          "IdResultType IdResult IdRef IdScope IdMemorySemantics"
        )
    case AtomicStore extends Definition(
          "OpAtomicStore",
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
          "IdRef IdScope IdMemorySemantics IdRef"
        )
    case AtomicExchange extends Definition(
          "OpAtomicExchange",
          SPIRVOpcodeNumber(229),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.AtomicExchange(
                    resultType = values.required(0),
                    dest = values.required(1),
                    pointer = values.required(2),
                    memory = values.required(3),
                    semantics = values.required(4),
                    value = values.required(5)
                  )
              ),
          "IdResultType IdResult IdRef IdScope IdMemorySemantics IdRef"
        )
    case AtomicCompareExchange extends Definition(
          "OpAtomicCompareExchange",
          SPIRVOpcodeNumber(230),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.AtomicCompareExchange(
                    resultType = values.required(0),
                    dest = values.required(1),
                    pointer = values.required(2),
                    memory = values.required(3),
                    equal = values.required(4),
                    unequal = values.required(5),
                    value = values.required(6),
                    comparator = values.required(7)
                  )
              ),
          "IdResultType IdResult IdRef IdScope IdMemorySemantics IdMemorySemantics IdRef IdRef"
        )
    case AtomicCompareExchangeWeak extends Definition(
          "OpAtomicCompareExchangeWeak",
          SPIRVOpcodeNumber(231),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.AtomicCompareExchangeWeak(
                    resultType = values.required(0),
                    dest = values.required(1),
                    pointer = values.required(2),
                    memory = values.required(3),
                    equal = values.required(4),
                    unequal = values.required(5),
                    value = values.required(6),
                    comparator = values.required(7)
                  )
              ),
          "IdResultType IdResult IdRef IdScope IdMemorySemantics IdMemorySemantics IdRef IdRef"
        )
    case AtomicIIncrement extends Definition(
          "OpAtomicIIncrement",
          SPIRVOpcodeNumber(232),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.AtomicIIncrement(
                    resultType = values.required(0),
                    dest = values.required(1),
                    pointer = values.required(2),
                    memory = values.required(3),
                    semantics = values.required(4)
                  )
              ),
          "IdResultType IdResult IdRef IdScope IdMemorySemantics"
        )
    case AtomicIDecrement extends Definition(
          "OpAtomicIDecrement",
          SPIRVOpcodeNumber(233),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.AtomicIDecrement(
                    resultType = values.required(0),
                    dest = values.required(1),
                    pointer = values.required(2),
                    memory = values.required(3),
                    semantics = values.required(4)
                  )
              ),
          "IdResultType IdResult IdRef IdScope IdMemorySemantics"
        )
    case AtomicIAdd extends Definition(
          "OpAtomicIAdd",
          SPIRVOpcodeNumber(234),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.AtomicIAdd(
                    resultType = values.required(0),
                    dest = values.required(1),
                    pointer = values.required(2),
                    memory = values.required(3),
                    semantics = values.required(4),
                    value = values.required(5)
                  )
              ),
          "IdResultType IdResult IdRef IdScope IdMemorySemantics IdRef"
        )
    case AtomicISub extends Definition(
          "OpAtomicISub",
          SPIRVOpcodeNumber(235),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.AtomicISub(
                    resultType = values.required(0),
                    dest = values.required(1),
                    pointer = values.required(2),
                    memory = values.required(3),
                    semantics = values.required(4),
                    value = values.required(5)
                  )
              ),
          "IdResultType IdResult IdRef IdScope IdMemorySemantics IdRef"
        )
    case AtomicSMin extends Definition(
          "OpAtomicSMin",
          SPIRVOpcodeNumber(236),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.AtomicSMin(
                    resultType = values.required(0),
                    dest = values.required(1),
                    pointer = values.required(2),
                    memory = values.required(3),
                    semantics = values.required(4),
                    value = values.required(5)
                  )
              ),
          "IdResultType IdResult IdRef IdScope IdMemorySemantics IdRef"
        )
    case AtomicUMin extends Definition(
          "OpAtomicUMin",
          SPIRVOpcodeNumber(237),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.AtomicUMin(
                    resultType = values.required(0),
                    dest = values.required(1),
                    pointer = values.required(2),
                    memory = values.required(3),
                    semantics = values.required(4),
                    value = values.required(5)
                  )
              ),
          "IdResultType IdResult IdRef IdScope IdMemorySemantics IdRef"
        )
    case AtomicSMax extends Definition(
          "OpAtomicSMax",
          SPIRVOpcodeNumber(238),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.AtomicSMax(
                    resultType = values.required(0),
                    dest = values.required(1),
                    pointer = values.required(2),
                    memory = values.required(3),
                    semantics = values.required(4),
                    value = values.required(5)
                  )
              ),
          "IdResultType IdResult IdRef IdScope IdMemorySemantics IdRef"
        )
    case AtomicUMax extends Definition(
          "OpAtomicUMax",
          SPIRVOpcodeNumber(239),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.AtomicUMax(
                    resultType = values.required(0),
                    dest = values.required(1),
                    pointer = values.required(2),
                    memory = values.required(3),
                    semantics = values.required(4),
                    value = values.required(5)
                  )
              ),
          "IdResultType IdResult IdRef IdScope IdMemorySemantics IdRef"
        )
    case AtomicAnd extends Definition(
          "OpAtomicAnd",
          SPIRVOpcodeNumber(240),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.AtomicAnd(
                    resultType = values.required(0),
                    dest = values.required(1),
                    pointer = values.required(2),
                    memory = values.required(3),
                    semantics = values.required(4),
                    value = values.required(5)
                  )
              ),
          "IdResultType IdResult IdRef IdScope IdMemorySemantics IdRef"
        )
    case AtomicOr extends Definition(
          "OpAtomicOr",
          SPIRVOpcodeNumber(241),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.AtomicOr(
                    resultType = values.required(0),
                    dest = values.required(1),
                    pointer = values.required(2),
                    memory = values.required(3),
                    semantics = values.required(4),
                    value = values.required(5)
                  )
              ),
          "IdResultType IdResult IdRef IdScope IdMemorySemantics IdRef"
        )
    case AtomicXor extends Definition(
          "OpAtomicXor",
          SPIRVOpcodeNumber(242),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.AtomicXor(
                    resultType = values.required(0),
                    dest = values.required(1),
                    pointer = values.required(2),
                    memory = values.required(3),
                    semantics = values.required(4),
                    value = values.required(5)
                  )
              ),
          "IdResultType IdResult IdRef IdScope IdMemorySemantics IdRef"
        )
    case Phi extends Definition(
          "OpPhi",
          SPIRVOpcodeNumber(245),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.Phi(values.required(0), values.required(1), values.repeated(2))
              ),
          "IdResultType IdResult PairIdRefIdRef*"
        )
    case LoopMerge extends Definition(
          "OpLoopMerge",
          SPIRVOpcodeNumber(246),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.LoopMerge(values.required(0), values.required(1), values.required(2))
              ),
          "IdRef IdRef LoopControl"
        )
    case SelectionMerge extends Definition(
          "OpSelectionMerge",
          SPIRVOpcodeNumber(247),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.SelectionMerge(values.required(0), values.required(1))
              ),
          "IdRef SelectionControl"
        )
    case Label extends Definition(
          "OpLabel",
          SPIRVOpcodeNumber(248),
          definition =>
            raw => Instruction.decodeDefinition(definition, raw, values => Instruction.Label(values.required(0))),
          "IdResult"
        )
    case Branch extends Definition(
          "OpBranch",
          SPIRVOpcodeNumber(249),
          definition =>
            raw => Instruction.decodeDefinition(definition, raw, values => Instruction.Branch(values.required(0))),
          "IdRef"
        )
    case BranchConditional extends Definition(
          "OpBranchConditional",
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
          "IdRef IdRef IdRef LiteralInteger*"
        )
    case Switch extends Definition(
          "OpSwitch",
          SPIRVOpcodeNumber(251),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.Switch(values.required(0), values.required(1), values.repeated(2))
              ),
          "IdRef IdRef PairLiteralIntegerIdRef*"
        )
    case Kill extends Definition(
          "OpKill",
          SPIRVOpcodeNumber(252),
          definition => raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.Kill()),
          ""
        )
    case Return extends Definition(
          "OpReturn",
          SPIRVOpcodeNumber(253),
          definition => raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.Return()),
          ""
        )
    case ReturnValue extends Definition(
          "OpReturnValue",
          SPIRVOpcodeNumber(254),
          definition =>
            raw => Instruction.decodeDefinition(definition, raw, values => Instruction.ReturnValue(values.required(0))),
          "IdRef"
        )
    case Unreachable extends Definition(
          "OpUnreachable",
          SPIRVOpcodeNumber(255),
          definition => raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.Unreachable()),
          ""
        )
    case LifetimeStart extends Definition(
          "OpLifetimeStart",
          SPIRVOpcodeNumber(256),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.LifetimeStart(values.required(0), values.required(1))
              ),
          "IdRef LiteralInteger"
        )
    case LifetimeStop extends Definition(
          "OpLifetimeStop",
          SPIRVOpcodeNumber(257),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.LifetimeStop(values.required(0), values.required(1))
              ),
          "IdRef LiteralInteger"
        )
    case GroupAsyncCopy extends Definition(
          "OpGroupAsyncCopy",
          SPIRVOpcodeNumber(259),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupAsyncCopy(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    destination = values.required(3),
                    src = values.required(4),
                    numElements = values.required(5),
                    stride = values.required(6),
                    event = values.required(7)
                  )
              ),
          "IdResultType IdResult IdScope IdRef IdRef IdRef IdRef IdRef"
        )
    case GroupWaitEvents extends Definition(
          "OpGroupWaitEvents",
          SPIRVOpcodeNumber(260),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.GroupWaitEvents(values.required(0), values.required(1), values.required(2))
              ),
          "IdScope IdRef IdRef"
        )
    case GroupAll extends Definition(
          "OpGroupAll",
          SPIRVOpcodeNumber(261),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupAll(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          "IdResultType IdResult IdScope IdRef"
        )
    case GroupAny extends Definition(
          "OpGroupAny",
          SPIRVOpcodeNumber(262),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupAny(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          "IdResultType IdResult IdScope IdRef"
        )
    case GroupBroadcast extends Definition(
          "OpGroupBroadcast",
          SPIRVOpcodeNumber(263),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupBroadcast(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    value = values.required(3),
                    localId = values.required(4)
                  )
              ),
          "IdResultType IdResult IdScope IdRef IdRef"
        )
    case GroupIAdd extends Definition(
          "OpGroupIAdd",
          SPIRVOpcodeNumber(264),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupIAdd(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    operation = values.required(3),
                    x = values.required(4)
                  )
              ),
          "IdResultType IdResult IdScope GroupOperation IdRef"
        )
    case GroupFAdd extends Definition(
          "OpGroupFAdd",
          SPIRVOpcodeNumber(265),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupFAdd(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    operation = values.required(3),
                    x = values.required(4)
                  )
              ),
          "IdResultType IdResult IdScope GroupOperation IdRef"
        )
    case GroupFMin extends Definition(
          "OpGroupFMin",
          SPIRVOpcodeNumber(266),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupFMin(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    operation = values.required(3),
                    x = values.required(4)
                  )
              ),
          "IdResultType IdResult IdScope GroupOperation IdRef"
        )
    case GroupUMin extends Definition(
          "OpGroupUMin",
          SPIRVOpcodeNumber(267),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupUMin(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    operation = values.required(3),
                    x = values.required(4)
                  )
              ),
          "IdResultType IdResult IdScope GroupOperation IdRef"
        )
    case GroupSMin extends Definition(
          "OpGroupSMin",
          SPIRVOpcodeNumber(268),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupSMin(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    operation = values.required(3),
                    x = values.required(4)
                  )
              ),
          "IdResultType IdResult IdScope GroupOperation IdRef"
        )
    case GroupFMax extends Definition(
          "OpGroupFMax",
          SPIRVOpcodeNumber(269),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupFMax(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    operation = values.required(3),
                    x = values.required(4)
                  )
              ),
          "IdResultType IdResult IdScope GroupOperation IdRef"
        )
    case GroupUMax extends Definition(
          "OpGroupUMax",
          SPIRVOpcodeNumber(270),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupUMax(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    operation = values.required(3),
                    x = values.required(4)
                  )
              ),
          "IdResultType IdResult IdScope GroupOperation IdRef"
        )
    case GroupSMax extends Definition(
          "OpGroupSMax",
          SPIRVOpcodeNumber(271),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupSMax(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    operation = values.required(3),
                    x = values.required(4)
                  )
              ),
          "IdResultType IdResult IdScope GroupOperation IdRef"
        )
    case ReadPipe extends Definition(
          "OpReadPipe",
          SPIRVOpcodeNumber(274),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ReadPipe(
                    resultType = values.required(0),
                    dest = values.required(1),
                    pipe = values.required(2),
                    pointer = values.required(3),
                    packetSize = values.required(4),
                    packetAlignment = values.required(5)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef"
        )
    case WritePipe extends Definition(
          "OpWritePipe",
          SPIRVOpcodeNumber(275),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.WritePipe(
                    resultType = values.required(0),
                    dest = values.required(1),
                    pipe = values.required(2),
                    pointer = values.required(3),
                    packetSize = values.required(4),
                    packetAlignment = values.required(5)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef"
        )
    case ReservedReadPipe extends Definition(
          "OpReservedReadPipe",
          SPIRVOpcodeNumber(276),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ReservedReadPipe(
                    resultType = values.required(0),
                    dest = values.required(1),
                    pipe = values.required(2),
                    reserveId = values.required(3),
                    index = values.required(4),
                    pointer = values.required(5),
                    packetSize = values.required(6),
                    packetAlignment = values.required(7)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef IdRef IdRef"
        )
    case ReservedWritePipe extends Definition(
          "OpReservedWritePipe",
          SPIRVOpcodeNumber(277),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ReservedWritePipe(
                    resultType = values.required(0),
                    dest = values.required(1),
                    pipe = values.required(2),
                    reserveId = values.required(3),
                    index = values.required(4),
                    pointer = values.required(5),
                    packetSize = values.required(6),
                    packetAlignment = values.required(7)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef IdRef IdRef"
        )
    case ReserveReadPipePackets extends Definition(
          "OpReserveReadPipePackets",
          SPIRVOpcodeNumber(278),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ReserveReadPipePackets(
                    resultType = values.required(0),
                    dest = values.required(1),
                    pipe = values.required(2),
                    numPackets = values.required(3),
                    packetSize = values.required(4),
                    packetAlignment = values.required(5)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef"
        )
    case ReserveWritePipePackets extends Definition(
          "OpReserveWritePipePackets",
          SPIRVOpcodeNumber(279),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ReserveWritePipePackets(
                    resultType = values.required(0),
                    dest = values.required(1),
                    pipe = values.required(2),
                    numPackets = values.required(3),
                    packetSize = values.required(4),
                    packetAlignment = values.required(5)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef"
        )
    case CommitReadPipe extends Definition(
          "OpCommitReadPipe",
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
          "IdRef IdRef IdRef IdRef"
        )
    case CommitWritePipe extends Definition(
          "OpCommitWritePipe",
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
          "IdRef IdRef IdRef IdRef"
        )
    case IsValidReserveId extends Definition(
          "OpIsValidReserveId",
          SPIRVOpcodeNumber(282),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.IsValidReserveId(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case GetNumPipePackets extends Definition(
          "OpGetNumPipePackets",
          SPIRVOpcodeNumber(283),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GetNumPipePackets(
                    resultType = values.required(0),
                    dest = values.required(1),
                    pipe = values.required(2),
                    packetSize = values.required(3),
                    packetAlignment = values.required(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef"
        )
    case GetMaxPipePackets extends Definition(
          "OpGetMaxPipePackets",
          SPIRVOpcodeNumber(284),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GetMaxPipePackets(
                    resultType = values.required(0),
                    dest = values.required(1),
                    pipe = values.required(2),
                    packetSize = values.required(3),
                    packetAlignment = values.required(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef"
        )
    case GroupReserveReadPipePackets extends Definition(
          "OpGroupReserveReadPipePackets",
          SPIRVOpcodeNumber(285),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupReserveReadPipePackets(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    pipe = values.required(3),
                    numPackets = values.required(4),
                    packetSize = values.required(5),
                    packetAlignment = values.required(6)
                  )
              ),
          "IdResultType IdResult IdScope IdRef IdRef IdRef IdRef"
        )
    case GroupReserveWritePipePackets extends Definition(
          "OpGroupReserveWritePipePackets",
          SPIRVOpcodeNumber(286),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupReserveWritePipePackets(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    pipe = values.required(3),
                    numPackets = values.required(4),
                    packetSize = values.required(5),
                    packetAlignment = values.required(6)
                  )
              ),
          "IdResultType IdResult IdScope IdRef IdRef IdRef IdRef"
        )
    case GroupCommitReadPipe extends Definition(
          "OpGroupCommitReadPipe",
          SPIRVOpcodeNumber(287),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupCommitReadPipe(
                    execution = values.required(0),
                    pipe = values.required(1),
                    reserveId = values.required(2),
                    packetSize = values.required(3),
                    packetAlignment = values.required(4)
                  )
              ),
          "IdScope IdRef IdRef IdRef IdRef"
        )
    case GroupCommitWritePipe extends Definition(
          "OpGroupCommitWritePipe",
          SPIRVOpcodeNumber(288),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupCommitWritePipe(
                    execution = values.required(0),
                    pipe = values.required(1),
                    reserveId = values.required(2),
                    packetSize = values.required(3),
                    packetAlignment = values.required(4)
                  )
              ),
          "IdScope IdRef IdRef IdRef IdRef"
        )
    case EnqueueMarker extends Definition(
          "OpEnqueueMarker",
          SPIRVOpcodeNumber(291),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.EnqueueMarker(
                    resultType = values.required(0),
                    dest = values.required(1),
                    queue = values.required(2),
                    numEvents = values.required(3),
                    waitEvents = values.required(4),
                    retEvent = values.required(5)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef"
        )
    case EnqueueKernel extends Definition(
          "OpEnqueueKernel",
          SPIRVOpcodeNumber(292),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.EnqueueKernel(
                    resultType = values.required(0),
                    dest = values.required(1),
                    queue = values.required(2),
                    flags = values.required(3),
                    nDRange = values.required(4),
                    numEvents = values.required(5),
                    waitEvents = values.required(6),
                    retEvent = values.required(7),
                    invoke = values.required(8),
                    param = values.required(9),
                    paramSize = values.required(10),
                    paramAlign = values.required(11),
                    localSize = values.repeated(12)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef*"
        )
    case GetKernelNDrangeSubGroupCount extends Definition(
          "OpGetKernelNDrangeSubGroupCount",
          SPIRVOpcodeNumber(293),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GetKernelNDrangeSubGroupCount(
                    resultType = values.required(0),
                    dest = values.required(1),
                    nDRange = values.required(2),
                    invoke = values.required(3),
                    param = values.required(4),
                    paramSize = values.required(5),
                    paramAlign = values.required(6)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef IdRef"
        )
    case GetKernelNDrangeMaxSubGroupSize extends Definition(
          "OpGetKernelNDrangeMaxSubGroupSize",
          SPIRVOpcodeNumber(294),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GetKernelNDrangeMaxSubGroupSize(
                    resultType = values.required(0),
                    dest = values.required(1),
                    nDRange = values.required(2),
                    invoke = values.required(3),
                    param = values.required(4),
                    paramSize = values.required(5),
                    paramAlign = values.required(6)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef IdRef"
        )
    case GetKernelWorkGroupSize extends Definition(
          "OpGetKernelWorkGroupSize",
          SPIRVOpcodeNumber(295),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GetKernelWorkGroupSize(
                    resultType = values.required(0),
                    dest = values.required(1),
                    invoke = values.required(2),
                    param = values.required(3),
                    paramSize = values.required(4),
                    paramAlign = values.required(5)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef"
        )
    case GetKernelPreferredWorkGroupSizeMultiple extends Definition(
          "OpGetKernelPreferredWorkGroupSizeMultiple",
          SPIRVOpcodeNumber(296),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GetKernelPreferredWorkGroupSizeMultiple(
                    resultType = values.required(0),
                    dest = values.required(1),
                    invoke = values.required(2),
                    param = values.required(3),
                    paramSize = values.required(4),
                    paramAlign = values.required(5)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef"
        )
    case RetainEvent extends Definition(
          "OpRetainEvent",
          SPIRVOpcodeNumber(297),
          definition =>
            raw => Instruction.decodeDefinition(definition, raw, values => Instruction.RetainEvent(values.required(0))),
          "IdRef"
        )
    case ReleaseEvent extends Definition(
          "OpReleaseEvent",
          SPIRVOpcodeNumber(298),
          definition =>
            raw =>
              Instruction.decodeDefinition(definition, raw, values => Instruction.ReleaseEvent(values.required(0))),
          "IdRef"
        )
    case CreateUserEvent extends Definition(
          "OpCreateUserEvent",
          SPIRVOpcodeNumber(299),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.CreateUserEvent(values.required(0), values.required(1))
              ),
          "IdResultType IdResult"
        )
    case IsValidEvent extends Definition(
          "OpIsValidEvent",
          SPIRVOpcodeNumber(300),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.IsValidEvent(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case SetUserEventStatus extends Definition(
          "OpSetUserEventStatus",
          SPIRVOpcodeNumber(301),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.SetUserEventStatus(values.required(0), values.required(1))
              ),
          "IdRef IdRef"
        )
    case CaptureEventProfilingInfo extends Definition(
          "OpCaptureEventProfilingInfo",
          SPIRVOpcodeNumber(302),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CaptureEventProfilingInfo(values.required(0), values.required(1), values.required(2))
              ),
          "IdRef IdRef IdRef"
        )
    case GetDefaultQueue extends Definition(
          "OpGetDefaultQueue",
          SPIRVOpcodeNumber(303),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.GetDefaultQueue(values.required(0), values.required(1))
              ),
          "IdResultType IdResult"
        )
    case BuildNDRange extends Definition(
          "OpBuildNDRange",
          SPIRVOpcodeNumber(304),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.BuildNDRange(
                    resultType = values.required(0),
                    dest = values.required(1),
                    globalWorkSize = values.required(2),
                    localWorkSize = values.required(3),
                    globalWorkOffset = values.required(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef"
        )
    case ImageSparseSampleImplicitLod extends Definition(
          "OpImageSparseSampleImplicitLod",
          SPIRVOpcodeNumber(305),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageSparseSampleImplicitLod(
                    resultType = values.required(0),
                    dest = values.required(1),
                    sampledImage = values.required(2),
                    coordinate = values.required(3),
                    mods = values.optional(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef ImageOperands?"
        )
    case ImageSparseSampleExplicitLod extends Definition(
          "OpImageSparseSampleExplicitLod",
          SPIRVOpcodeNumber(306),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageSparseSampleExplicitLod(
                    resultType = values.required(0),
                    dest = values.required(1),
                    sampledImage = values.required(2),
                    coordinate = values.required(3),
                    mods = values.required(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef ImageOperands"
        )
    case ImageSparseSampleDrefImplicitLod extends Definition(
          "OpImageSparseSampleDrefImplicitLod",
          SPIRVOpcodeNumber(307),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageSparseSampleDrefImplicitLod(
                    resultType = values.required(0),
                    dest = values.required(1),
                    sampledImage = values.required(2),
                    coordinate = values.required(3),
                    dref = values.required(4),
                    mods = values.optional(5)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef ImageOperands?"
        )
    case ImageSparseSampleDrefExplicitLod extends Definition(
          "OpImageSparseSampleDrefExplicitLod",
          SPIRVOpcodeNumber(308),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageSparseSampleDrefExplicitLod(
                    resultType = values.required(0),
                    dest = values.required(1),
                    sampledImage = values.required(2),
                    coordinate = values.required(3),
                    dref = values.required(4),
                    mods = values.required(5)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef ImageOperands"
        )
    case ImageSparseSampleProjImplicitLod extends Definition(
          "OpImageSparseSampleProjImplicitLod",
          SPIRVOpcodeNumber(309),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageSparseSampleProjImplicitLod(
                    resultType = values.required(0),
                    dest = values.required(1),
                    sampledImage = values.required(2),
                    coordinate = values.required(3),
                    mods = values.optional(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef ImageOperands?"
        )
    case ImageSparseSampleProjExplicitLod extends Definition(
          "OpImageSparseSampleProjExplicitLod",
          SPIRVOpcodeNumber(310),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageSparseSampleProjExplicitLod(
                    resultType = values.required(0),
                    dest = values.required(1),
                    sampledImage = values.required(2),
                    coordinate = values.required(3),
                    mods = values.required(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef ImageOperands"
        )
    case ImageSparseSampleProjDrefImplicitLod extends Definition(
          "OpImageSparseSampleProjDrefImplicitLod",
          SPIRVOpcodeNumber(311),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageSparseSampleProjDrefImplicitLod(
                    resultType = values.required(0),
                    dest = values.required(1),
                    sampledImage = values.required(2),
                    coordinate = values.required(3),
                    dref = values.required(4),
                    mods = values.optional(5)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef ImageOperands?"
        )
    case ImageSparseSampleProjDrefExplicitLod extends Definition(
          "OpImageSparseSampleProjDrefExplicitLod",
          SPIRVOpcodeNumber(312),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageSparseSampleProjDrefExplicitLod(
                    resultType = values.required(0),
                    dest = values.required(1),
                    sampledImage = values.required(2),
                    coordinate = values.required(3),
                    dref = values.required(4),
                    mods = values.required(5)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef ImageOperands"
        )
    case ImageSparseFetch extends Definition(
          "OpImageSparseFetch",
          SPIRVOpcodeNumber(313),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageSparseFetch(
                    resultType = values.required(0),
                    dest = values.required(1),
                    image = values.required(2),
                    coordinate = values.required(3),
                    mods = values.optional(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef ImageOperands?"
        )
    case ImageSparseGather extends Definition(
          "OpImageSparseGather",
          SPIRVOpcodeNumber(314),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageSparseGather(
                    resultType = values.required(0),
                    dest = values.required(1),
                    sampledImage = values.required(2),
                    coordinate = values.required(3),
                    component = values.required(4),
                    mods = values.optional(5)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef ImageOperands?"
        )
    case ImageSparseDrefGather extends Definition(
          "OpImageSparseDrefGather",
          SPIRVOpcodeNumber(315),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageSparseDrefGather(
                    resultType = values.required(0),
                    dest = values.required(1),
                    sampledImage = values.required(2),
                    coordinate = values.required(3),
                    dref = values.required(4),
                    mods = values.optional(5)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef ImageOperands?"
        )
    case ImageSparseTexelsResident extends Definition(
          "OpImageSparseTexelsResident",
          SPIRVOpcodeNumber(316),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageSparseTexelsResident(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case NoLine extends Definition(
          "OpNoLine",
          SPIRVOpcodeNumber(317),
          definition => raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.NoLine()),
          ""
        )
    case AtomicFlagTestAndSet extends Definition(
          "OpAtomicFlagTestAndSet",
          SPIRVOpcodeNumber(318),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.AtomicFlagTestAndSet(
                    resultType = values.required(0),
                    dest = values.required(1),
                    pointer = values.required(2),
                    memory = values.required(3),
                    semantics = values.required(4)
                  )
              ),
          "IdResultType IdResult IdRef IdScope IdMemorySemantics"
        )
    case AtomicFlagClear extends Definition(
          "OpAtomicFlagClear",
          SPIRVOpcodeNumber(319),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.AtomicFlagClear(values.required(0), values.required(1), values.required(2))
              ),
          "IdRef IdScope IdMemorySemantics"
        )
    case ImageSparseRead extends Definition(
          "OpImageSparseRead",
          SPIRVOpcodeNumber(320),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageSparseRead(
                    resultType = values.required(0),
                    dest = values.required(1),
                    image = values.required(2),
                    coordinate = values.required(3),
                    mods = values.optional(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef ImageOperands?"
        )
    case SizeOf extends Definition(
          "OpSizeOf",
          SPIRVOpcodeNumber(321),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.SizeOf(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case TypePipeStorage extends Definition(
          "OpTypePipeStorage",
          SPIRVOpcodeNumber(322),
          definition =>
            raw =>
              Instruction.decodeDefinition(definition, raw, values => Instruction.TypePipeStorage(values.required(0))),
          "IdResult"
        )
    case ConstantPipeStorage extends Definition(
          "OpConstantPipeStorage",
          SPIRVOpcodeNumber(323),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ConstantPipeStorage(
                    resultType = values.required(0),
                    dest = values.required(1),
                    packetSize = values.required(2),
                    packetAlignment = values.required(3),
                    capacity = values.required(4)
                  )
              ),
          "IdResultType IdResult LiteralInteger LiteralInteger LiteralInteger"
        )
    case CreatePipeFromPipeStorage extends Definition(
          "OpCreatePipeFromPipeStorage",
          SPIRVOpcodeNumber(324),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CreatePipeFromPipeStorage(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case GetKernelLocalSizeForSubgroupCount extends Definition(
          "OpGetKernelLocalSizeForSubgroupCount",
          SPIRVOpcodeNumber(325),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GetKernelLocalSizeForSubgroupCount(
                    resultType = values.required(0),
                    dest = values.required(1),
                    subgroupCount = values.required(2),
                    invoke = values.required(3),
                    param = values.required(4),
                    paramSize = values.required(5),
                    paramAlign = values.required(6)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef IdRef"
        )
    case GetKernelMaxNumSubgroups extends Definition(
          "OpGetKernelMaxNumSubgroups",
          SPIRVOpcodeNumber(326),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GetKernelMaxNumSubgroups(
                    resultType = values.required(0),
                    dest = values.required(1),
                    invoke = values.required(2),
                    param = values.required(3),
                    paramSize = values.required(4),
                    paramAlign = values.required(5)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef"
        )
    case TypeNamedBarrier extends Definition(
          "OpTypeNamedBarrier",
          SPIRVOpcodeNumber(327),
          definition =>
            raw =>
              Instruction.decodeDefinition(definition, raw, values => Instruction.TypeNamedBarrier(values.required(0))),
          "IdResult"
        )
    case NamedBarrierInitialize extends Definition(
          "OpNamedBarrierInitialize",
          SPIRVOpcodeNumber(328),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.NamedBarrierInitialize(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case MemoryNamedBarrier extends Definition(
          "OpMemoryNamedBarrier",
          SPIRVOpcodeNumber(329),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.MemoryNamedBarrier(values.required(0), values.required(1), values.required(2))
              ),
          "IdRef IdScope IdMemorySemantics"
        )
    case ModuleProcessed extends Definition(
          "OpModuleProcessed",
          SPIRVOpcodeNumber(330),
          definition =>
            raw =>
              Instruction.decodeDefinition(definition, raw, values => Instruction.ModuleProcessed(values.required(0))),
          "LiteralString"
        )
    case ExecutionModeId extends Definition(
          "OpExecutionModeId",
          SPIRVOpcodeNumber(331),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ExecutionModeId(values.required(0), values.required(1))
              ),
          "IdRef ExecutionMode"
        )
    case DecorateId extends Definition(
          "OpDecorateId",
          SPIRVOpcodeNumber(332),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.DecorateId(values.required(0), values.required(1))
              ),
          "IdRef Decoration"
        )
    case GroupNonUniformElect extends Definition(
          "OpGroupNonUniformElect",
          SPIRVOpcodeNumber(333),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.GroupNonUniformElect(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdScope"
        )
    case GroupNonUniformAll extends Definition(
          "OpGroupNonUniformAll",
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
          "IdResultType IdResult IdScope IdRef"
        )
    case GroupNonUniformAny extends Definition(
          "OpGroupNonUniformAny",
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
          "IdResultType IdResult IdScope IdRef"
        )
    case GroupNonUniformAllEqual extends Definition(
          "OpGroupNonUniformAllEqual",
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
          "IdResultType IdResult IdScope IdRef"
        )
    case GroupNonUniformBroadcast extends Definition(
          "OpGroupNonUniformBroadcast",
          SPIRVOpcodeNumber(337),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformBroadcast(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    value = values.required(3),
                    invocationId = values.required(4)
                  )
              ),
          "IdResultType IdResult IdScope IdRef IdRef"
        )
    case GroupNonUniformBroadcastFirst extends Definition(
          "OpGroupNonUniformBroadcastFirst",
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
          "IdResultType IdResult IdScope IdRef"
        )
    case GroupNonUniformBallot extends Definition(
          "OpGroupNonUniformBallot",
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
          "IdResultType IdResult IdScope IdRef"
        )
    case GroupNonUniformInverseBallot extends Definition(
          "OpGroupNonUniformInverseBallot",
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
          "IdResultType IdResult IdScope IdRef"
        )
    case GroupNonUniformBallotBitExtract extends Definition(
          "OpGroupNonUniformBallotBitExtract",
          SPIRVOpcodeNumber(341),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformBallotBitExtract(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    value = values.required(3),
                    index = values.required(4)
                  )
              ),
          "IdResultType IdResult IdScope IdRef IdRef"
        )
    case GroupNonUniformBallotBitCount extends Definition(
          "OpGroupNonUniformBallotBitCount",
          SPIRVOpcodeNumber(342),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformBallotBitCount(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    operation = values.required(3),
                    value = values.required(4)
                  )
              ),
          "IdResultType IdResult IdScope GroupOperation IdRef"
        )
    case GroupNonUniformBallotFindLSB extends Definition(
          "OpGroupNonUniformBallotFindLSB",
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
          "IdResultType IdResult IdScope IdRef"
        )
    case GroupNonUniformBallotFindMSB extends Definition(
          "OpGroupNonUniformBallotFindMSB",
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
          "IdResultType IdResult IdScope IdRef"
        )
    case GroupNonUniformShuffle extends Definition(
          "OpGroupNonUniformShuffle",
          SPIRVOpcodeNumber(345),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformShuffle(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    value = values.required(3),
                    invocationId = values.required(4)
                  )
              ),
          "IdResultType IdResult IdScope IdRef IdRef"
        )
    case GroupNonUniformShuffleXor extends Definition(
          "OpGroupNonUniformShuffleXor",
          SPIRVOpcodeNumber(346),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformShuffleXor(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    value = values.required(3),
                    mask = values.required(4)
                  )
              ),
          "IdResultType IdResult IdScope IdRef IdRef"
        )
    case GroupNonUniformShuffleUp extends Definition(
          "OpGroupNonUniformShuffleUp",
          SPIRVOpcodeNumber(347),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformShuffleUp(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    value = values.required(3),
                    delta = values.required(4)
                  )
              ),
          "IdResultType IdResult IdScope IdRef IdRef"
        )
    case GroupNonUniformShuffleDown extends Definition(
          "OpGroupNonUniformShuffleDown",
          SPIRVOpcodeNumber(348),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformShuffleDown(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    value = values.required(3),
                    delta = values.required(4)
                  )
              ),
          "IdResultType IdResult IdScope IdRef IdRef"
        )
    case GroupNonUniformIAdd extends Definition(
          "OpGroupNonUniformIAdd",
          SPIRVOpcodeNumber(349),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformIAdd(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    operation = values.required(3),
                    value = values.required(4),
                    clusterSize = values.optional(5)
                  )
              ),
          "IdResultType IdResult IdScope GroupOperation IdRef IdRef?"
        )
    case GroupNonUniformFAdd extends Definition(
          "OpGroupNonUniformFAdd",
          SPIRVOpcodeNumber(350),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformFAdd(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    operation = values.required(3),
                    value = values.required(4),
                    clusterSize = values.optional(5)
                  )
              ),
          "IdResultType IdResult IdScope GroupOperation IdRef IdRef?"
        )
    case GroupNonUniformIMul extends Definition(
          "OpGroupNonUniformIMul",
          SPIRVOpcodeNumber(351),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformIMul(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    operation = values.required(3),
                    value = values.required(4),
                    clusterSize = values.optional(5)
                  )
              ),
          "IdResultType IdResult IdScope GroupOperation IdRef IdRef?"
        )
    case GroupNonUniformFMul extends Definition(
          "OpGroupNonUniformFMul",
          SPIRVOpcodeNumber(352),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformFMul(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    operation = values.required(3),
                    value = values.required(4),
                    clusterSize = values.optional(5)
                  )
              ),
          "IdResultType IdResult IdScope GroupOperation IdRef IdRef?"
        )
    case GroupNonUniformSMin extends Definition(
          "OpGroupNonUniformSMin",
          SPIRVOpcodeNumber(353),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformSMin(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    operation = values.required(3),
                    value = values.required(4),
                    clusterSize = values.optional(5)
                  )
              ),
          "IdResultType IdResult IdScope GroupOperation IdRef IdRef?"
        )
    case GroupNonUniformUMin extends Definition(
          "OpGroupNonUniformUMin",
          SPIRVOpcodeNumber(354),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformUMin(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    operation = values.required(3),
                    value = values.required(4),
                    clusterSize = values.optional(5)
                  )
              ),
          "IdResultType IdResult IdScope GroupOperation IdRef IdRef?"
        )
    case GroupNonUniformFMin extends Definition(
          "OpGroupNonUniformFMin",
          SPIRVOpcodeNumber(355),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformFMin(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    operation = values.required(3),
                    value = values.required(4),
                    clusterSize = values.optional(5)
                  )
              ),
          "IdResultType IdResult IdScope GroupOperation IdRef IdRef?"
        )
    case GroupNonUniformSMax extends Definition(
          "OpGroupNonUniformSMax",
          SPIRVOpcodeNumber(356),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformSMax(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    operation = values.required(3),
                    value = values.required(4),
                    clusterSize = values.optional(5)
                  )
              ),
          "IdResultType IdResult IdScope GroupOperation IdRef IdRef?"
        )
    case GroupNonUniformUMax extends Definition(
          "OpGroupNonUniformUMax",
          SPIRVOpcodeNumber(357),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformUMax(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    operation = values.required(3),
                    value = values.required(4),
                    clusterSize = values.optional(5)
                  )
              ),
          "IdResultType IdResult IdScope GroupOperation IdRef IdRef?"
        )
    case GroupNonUniformFMax extends Definition(
          "OpGroupNonUniformFMax",
          SPIRVOpcodeNumber(358),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformFMax(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    operation = values.required(3),
                    value = values.required(4),
                    clusterSize = values.optional(5)
                  )
              ),
          "IdResultType IdResult IdScope GroupOperation IdRef IdRef?"
        )
    case GroupNonUniformBitwiseAnd extends Definition(
          "OpGroupNonUniformBitwiseAnd",
          SPIRVOpcodeNumber(359),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformBitwiseAnd(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    operation = values.required(3),
                    value = values.required(4),
                    clusterSize = values.optional(5)
                  )
              ),
          "IdResultType IdResult IdScope GroupOperation IdRef IdRef?"
        )
    case GroupNonUniformBitwiseOr extends Definition(
          "OpGroupNonUniformBitwiseOr",
          SPIRVOpcodeNumber(360),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformBitwiseOr(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    operation = values.required(3),
                    value = values.required(4),
                    clusterSize = values.optional(5)
                  )
              ),
          "IdResultType IdResult IdScope GroupOperation IdRef IdRef?"
        )
    case GroupNonUniformBitwiseXor extends Definition(
          "OpGroupNonUniformBitwiseXor",
          SPIRVOpcodeNumber(361),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformBitwiseXor(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    operation = values.required(3),
                    value = values.required(4),
                    clusterSize = values.optional(5)
                  )
              ),
          "IdResultType IdResult IdScope GroupOperation IdRef IdRef?"
        )
    case GroupNonUniformLogicalAnd extends Definition(
          "OpGroupNonUniformLogicalAnd",
          SPIRVOpcodeNumber(362),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformLogicalAnd(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    operation = values.required(3),
                    value = values.required(4),
                    clusterSize = values.optional(5)
                  )
              ),
          "IdResultType IdResult IdScope GroupOperation IdRef IdRef?"
        )
    case GroupNonUniformLogicalOr extends Definition(
          "OpGroupNonUniformLogicalOr",
          SPIRVOpcodeNumber(363),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformLogicalOr(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    operation = values.required(3),
                    value = values.required(4),
                    clusterSize = values.optional(5)
                  )
              ),
          "IdResultType IdResult IdScope GroupOperation IdRef IdRef?"
        )
    case GroupNonUniformLogicalXor extends Definition(
          "OpGroupNonUniformLogicalXor",
          SPIRVOpcodeNumber(364),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformLogicalXor(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    operation = values.required(3),
                    value = values.required(4),
                    clusterSize = values.optional(5)
                  )
              ),
          "IdResultType IdResult IdScope GroupOperation IdRef IdRef?"
        )
    case GroupNonUniformQuadBroadcast extends Definition(
          "OpGroupNonUniformQuadBroadcast",
          SPIRVOpcodeNumber(365),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformQuadBroadcast(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    value = values.required(3),
                    index = values.required(4)
                  )
              ),
          "IdResultType IdResult IdScope IdRef IdRef"
        )
    case GroupNonUniformQuadSwap extends Definition(
          "OpGroupNonUniformQuadSwap",
          SPIRVOpcodeNumber(366),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformQuadSwap(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    value = values.required(3),
                    direction = values.required(4)
                  )
              ),
          "IdResultType IdResult IdScope IdRef IdRef"
        )
    case CopyLogical extends Definition(
          "OpCopyLogical",
          SPIRVOpcodeNumber(400),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.CopyLogical(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case PtrEqual extends Definition(
          "OpPtrEqual",
          SPIRVOpcodeNumber(401),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.PtrEqual(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          "IdResultType IdResult IdRef IdRef"
        )
    case PtrNotEqual extends Definition(
          "OpPtrNotEqual",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case PtrDiff extends Definition(
          "OpPtrDiff",
          SPIRVOpcodeNumber(403),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.PtrDiff(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          "IdResultType IdResult IdRef IdRef"
        )
    case ColorAttachmentReadEXT extends Definition(
          "OpColorAttachmentReadEXT",
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
          "IdResultType IdResult IdRef IdRef?"
        )
    case DepthAttachmentReadEXT extends Definition(
          "OpDepthAttachmentReadEXT",
          SPIRVOpcodeNumber(4161),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.DepthAttachmentReadEXT(values.required(0), values.required(1), values.optional(2))
              ),
          "IdResultType IdResult IdRef?"
        )
    case StencilAttachmentReadEXT extends Definition(
          "OpStencilAttachmentReadEXT",
          SPIRVOpcodeNumber(4162),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.StencilAttachmentReadEXT(values.required(0), values.required(1), values.optional(2))
              ),
          "IdResultType IdResult IdRef?"
        )
    case TypeTensorARM extends Definition(
          "OpTypeTensorARM",
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
          "IdResult IdRef IdRef? IdRef?"
        )
    case TensorReadARM extends Definition(
          "OpTensorReadARM",
          SPIRVOpcodeNumber(4164),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.TensorReadARM(
                    resultType = values.required(0),
                    dest = values.required(1),
                    tensor = values.required(2),
                    coordinates = values.required(3),
                    tensorOperands = values.optional(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef TensorOperands?"
        )
    case TensorWriteARM extends Definition(
          "OpTensorWriteARM",
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
          "IdRef IdRef IdRef TensorOperands?"
        )
    case TensorQuerySizeARM extends Definition(
          "OpTensorQuerySizeARM",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case GraphConstantARM extends Definition(
          "OpGraphConstantARM",
          SPIRVOpcodeNumber(4181),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.GraphConstantARM(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult LiteralInteger"
        )
    case GraphEntryPointARM extends Definition(
          "OpGraphEntryPointARM",
          SPIRVOpcodeNumber(4182),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.GraphEntryPointARM(values.required(0), values.required(1), values.repeated(2))
              ),
          "IdRef LiteralString IdRef*"
        )
    case GraphARM extends Definition(
          "OpGraphARM",
          SPIRVOpcodeNumber(4183),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.GraphARM(values.required(0), values.required(1))
              ),
          "IdResultType IdResult"
        )
    case GraphInputARM extends Definition(
          "OpGraphInputARM",
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
          "IdResultType IdResult IdRef IdRef*"
        )
    case GraphSetOutputARM extends Definition(
          "OpGraphSetOutputARM",
          SPIRVOpcodeNumber(4185),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.GraphSetOutputARM(values.required(0), values.required(1), values.repeated(2))
              ),
          "IdRef IdRef IdRef*"
        )
    case GraphEndARM extends Definition(
          "OpGraphEndARM",
          SPIRVOpcodeNumber(4186),
          definition => raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.GraphEndARM()),
          ""
        )
    case TypeGraphARM extends Definition(
          "OpTypeGraphARM",
          SPIRVOpcodeNumber(4190),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeGraphARM(values.required(0), values.required(1), values.repeated(2))
              ),
          "IdResult LiteralInteger IdRef*"
        )
    case BitcastExtractEXT extends Definition(
          "OpBitcastExtractEXT",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case TerminateInvocation extends Definition(
          "OpTerminateInvocation",
          SPIRVOpcodeNumber(4416),
          definition => raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.TerminateInvocation()),
          ""
        )
    case TypeUntypedPointerKHR extends Definition(
          "OpTypeUntypedPointerKHR",
          SPIRVOpcodeNumber(4417),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeUntypedPointerKHR(values.required(0), values.required(1))
              ),
          "IdResult StorageClass"
        )
    case UntypedVariableKHR extends Definition(
          "OpUntypedVariableKHR",
          SPIRVOpcodeNumber(4418),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.UntypedVariableKHR(
                    resultType = values.required(0),
                    dest = values.required(1),
                    storageClass = values.required(2),
                    dataType = values.optional(3),
                    initializer = values.optional(4)
                  )
              ),
          "IdResultType IdResult StorageClass IdRef? IdRef?"
        )
    case UntypedAccessChainKHR extends Definition(
          "OpUntypedAccessChainKHR",
          SPIRVOpcodeNumber(4419),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.UntypedAccessChainKHR(
                    resultType = values.required(0),
                    dest = values.required(1),
                    baseType = values.required(2),
                    base = values.required(3),
                    indexes = values.repeated(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef*"
        )
    case UntypedInBoundsAccessChainKHR extends Definition(
          "OpUntypedInBoundsAccessChainKHR",
          SPIRVOpcodeNumber(4420),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.UntypedInBoundsAccessChainKHR(
                    resultType = values.required(0),
                    dest = values.required(1),
                    baseType = values.required(2),
                    base = values.required(3),
                    indexes = values.repeated(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef*"
        )
    case SubgroupBallotKHR extends Definition(
          "OpSubgroupBallotKHR",
          SPIRVOpcodeNumber(4421),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.SubgroupBallotKHR(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case SubgroupFirstInvocationKHR extends Definition(
          "OpSubgroupFirstInvocationKHR",
          SPIRVOpcodeNumber(4422),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupFirstInvocationKHR(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case UntypedPtrAccessChainKHR extends Definition(
          "OpUntypedPtrAccessChainKHR",
          SPIRVOpcodeNumber(4423),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.UntypedPtrAccessChainKHR(
                    resultType = values.required(0),
                    dest = values.required(1),
                    baseType = values.required(2),
                    base = values.required(3),
                    element = values.required(4),
                    indexes = values.repeated(5)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef*"
        )
    case UntypedInBoundsPtrAccessChainKHR extends Definition(
          "OpUntypedInBoundsPtrAccessChainKHR",
          SPIRVOpcodeNumber(4424),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.UntypedInBoundsPtrAccessChainKHR(
                    resultType = values.required(0),
                    dest = values.required(1),
                    baseType = values.required(2),
                    base = values.required(3),
                    element = values.required(4),
                    indexes = values.repeated(5)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef*"
        )
    case UntypedArrayLengthKHR extends Definition(
          "OpUntypedArrayLengthKHR",
          SPIRVOpcodeNumber(4425),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.UntypedArrayLengthKHR(
                    resultType = values.required(0),
                    dest = values.required(1),
                    structure = values.required(2),
                    pointer = values.required(3),
                    arrayMember = values.required(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef LiteralInteger"
        )
    case UntypedPrefetchKHR extends Definition(
          "OpUntypedPrefetchKHR",
          SPIRVOpcodeNumber(4426),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.UntypedPrefetchKHR(
                    pointerType = values.required(0),
                    numBytes = values.required(1),
                    rW = values.optional(2),
                    locality = values.optional(3),
                    cacheType = values.optional(4)
                  )
              ),
          "IdRef IdRef IdRef? IdRef? IdRef?"
        )
    case FmaKHR extends Definition(
          "OpFmaKHR",
          SPIRVOpcodeNumber(4427),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FmaKHR(
                    resultType = values.required(0),
                    dest = values.required(1),
                    srcA = values.required(2),
                    srcB = values.required(3),
                    srcC = values.required(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef"
        )
    case SubgroupAllKHR extends Definition(
          "OpSubgroupAllKHR",
          SPIRVOpcodeNumber(4428),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.SubgroupAllKHR(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case SubgroupAnyKHR extends Definition(
          "OpSubgroupAnyKHR",
          SPIRVOpcodeNumber(4429),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.SubgroupAnyKHR(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case SubgroupAllEqualKHR extends Definition(
          "OpSubgroupAllEqualKHR",
          SPIRVOpcodeNumber(4430),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.SubgroupAllEqualKHR(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case GroupNonUniformRotateKHR extends Definition(
          "OpGroupNonUniformRotateKHR",
          SPIRVOpcodeNumber(4431),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformRotateKHR(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    value = values.required(3),
                    delta = values.required(4),
                    clusterSize = values.optional(5)
                  )
              ),
          "IdResultType IdResult IdScope IdRef IdRef IdRef?"
        )
    case SubgroupReadInvocationKHR extends Definition(
          "OpSubgroupReadInvocationKHR",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case ExtInstWithForwardRefsKHR extends Definition(
          "OpExtInstWithForwardRefsKHR",
          SPIRVOpcodeNumber(4433),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ExtInstWithForwardRefsKHR(
                    resultType = values.required(0),
                    dest = values.required(1),
                    set = values.required(2),
                    inst = values.required(3),
                    srcs = values.repeated(4)
                  )
              ),
          "IdResultType IdResult IdRef LiteralExtInstInteger IdRef*"
        )
    case UntypedGroupAsyncCopyKHR extends Definition(
          "OpUntypedGroupAsyncCopyKHR",
          SPIRVOpcodeNumber(4434),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.UntypedGroupAsyncCopyKHR(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    destination = values.required(3),
                    src = values.required(4),
                    elementNumBytes = values.required(5),
                    numElements = values.required(6),
                    stride = values.required(7),
                    event = values.required(8),
                    modsA = values.optional(9),
                    modsB = values.optional(10)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef IdRef IdRef IdRef MemoryAccess? MemoryAccess?"
        )
    case TraceRayKHR extends Definition(
          "OpTraceRayKHR",
          SPIRVOpcodeNumber(4445),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.TraceRayKHR(
                    accel = values.required(0),
                    rayFlags = values.required(1),
                    cullMask = values.required(2),
                    sBTOffset = values.required(3),
                    sBTStride = values.required(4),
                    missIndex = values.required(5),
                    rayOrigin = values.required(6),
                    rayTmin = values.required(7),
                    rayDirection = values.required(8),
                    rayTmax = values.required(9),
                    payload = values.required(10)
                  )
              ),
          "IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef"
        )
    case ExecuteCallableKHR extends Definition(
          "OpExecuteCallableKHR",
          SPIRVOpcodeNumber(4446),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ExecuteCallableKHR(values.required(0), values.required(1))
              ),
          "IdRef IdRef"
        )
    case ConvertUToAccelerationStructureKHR extends Definition(
          "OpConvertUToAccelerationStructureKHR",
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
          "IdResultType IdResult IdRef"
        )
    case IgnoreIntersectionKHR extends Definition(
          "OpIgnoreIntersectionKHR",
          SPIRVOpcodeNumber(4448),
          definition => raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.IgnoreIntersectionKHR()),
          ""
        )
    case TerminateRayKHR extends Definition(
          "OpTerminateRayKHR",
          SPIRVOpcodeNumber(4449),
          definition => raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.TerminateRayKHR()),
          ""
        )
    case SDot extends Definition(
          "OpSDot",
          SPIRVOpcodeNumber(4450),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SDot(
                    resultType = values.required(0),
                    dest = values.required(1),
                    vector1 = values.required(2),
                    vector2 = values.required(3),
                    packedVectorFormat = values.optional(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef PackedVectorFormat?"
        )
    case UDot extends Definition(
          "OpUDot",
          SPIRVOpcodeNumber(4451),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.UDot(
                    resultType = values.required(0),
                    dest = values.required(1),
                    vector1 = values.required(2),
                    vector2 = values.required(3),
                    packedVectorFormat = values.optional(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef PackedVectorFormat?"
        )
    case SUDot extends Definition(
          "OpSUDot",
          SPIRVOpcodeNumber(4452),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SUDot(
                    resultType = values.required(0),
                    dest = values.required(1),
                    vector1 = values.required(2),
                    vector2 = values.required(3),
                    packedVectorFormat = values.optional(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef PackedVectorFormat?"
        )
    case SDotAccSat extends Definition(
          "OpSDotAccSat",
          SPIRVOpcodeNumber(4453),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SDotAccSat(
                    resultType = values.required(0),
                    dest = values.required(1),
                    vector1 = values.required(2),
                    vector2 = values.required(3),
                    accumulator = values.required(4),
                    packedVectorFormat = values.optional(5)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef PackedVectorFormat?"
        )
    case UDotAccSat extends Definition(
          "OpUDotAccSat",
          SPIRVOpcodeNumber(4454),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.UDotAccSat(
                    resultType = values.required(0),
                    dest = values.required(1),
                    vector1 = values.required(2),
                    vector2 = values.required(3),
                    accumulator = values.required(4),
                    packedVectorFormat = values.optional(5)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef PackedVectorFormat?"
        )
    case SUDotAccSat extends Definition(
          "OpSUDotAccSat",
          SPIRVOpcodeNumber(4455),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SUDotAccSat(
                    resultType = values.required(0),
                    dest = values.required(1),
                    vector1 = values.required(2),
                    vector2 = values.required(3),
                    accumulator = values.required(4),
                    packedVectorFormat = values.optional(5)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef PackedVectorFormat?"
        )
    case TypeCooperativeMatrixKHR extends Definition(
          "OpTypeCooperativeMatrixKHR",
          SPIRVOpcodeNumber(4456),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.TypeCooperativeMatrixKHR(
                    dest = values.required(0),
                    componentType = values.required(1),
                    scope = values.required(2),
                    rows = values.required(3),
                    columns = values.required(4),
                    use = values.required(5)
                  )
              ),
          "IdResult IdRef IdScope IdRef IdRef IdRef"
        )
    case CooperativeMatrixLoadKHR extends Definition(
          "OpCooperativeMatrixLoadKHR",
          SPIRVOpcodeNumber(4457),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CooperativeMatrixLoadKHR(
                    resultType = values.required(0),
                    dest = values.required(1),
                    pointer = values.required(2),
                    memoryLayout = values.required(3),
                    stride = values.optional(4),
                    mods = values.optional(5)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef? MemoryAccess?"
        )
    case CooperativeMatrixStoreKHR extends Definition(
          "OpCooperativeMatrixStoreKHR",
          SPIRVOpcodeNumber(4458),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CooperativeMatrixStoreKHR(
                    pointer = values.required(0),
                    src = values.required(1),
                    memoryLayout = values.required(2),
                    stride = values.optional(3),
                    mods = values.optional(4)
                  )
              ),
          "IdRef IdRef IdRef IdRef? MemoryAccess?"
        )
    case CooperativeMatrixMulAddKHR extends Definition(
          "OpCooperativeMatrixMulAddKHR",
          SPIRVOpcodeNumber(4459),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CooperativeMatrixMulAddKHR(
                    resultType = values.required(0),
                    dest = values.required(1),
                    a = values.required(2),
                    b = values.required(3),
                    c = values.required(4),
                    cooperativeMatrixOperands = values.optional(5)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef CooperativeMatrixOperands?"
        )
    case CooperativeMatrixLengthKHR extends Definition(
          "OpCooperativeMatrixLengthKHR",
          SPIRVOpcodeNumber(4460),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CooperativeMatrixLengthKHR(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case ConstantCompositeReplicateEXT extends Definition(
          "OpConstantCompositeReplicateEXT",
          SPIRVOpcodeNumber(4461),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ConstantCompositeReplicateEXT(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case SpecConstantCompositeReplicateEXT extends Definition(
          "OpSpecConstantCompositeReplicateEXT",
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
          "IdResultType IdResult IdRef"
        )
    case CompositeConstructReplicateEXT extends Definition(
          "OpCompositeConstructReplicateEXT",
          SPIRVOpcodeNumber(4463),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CompositeConstructReplicateEXT(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case TypeRayQueryKHR extends Definition(
          "OpTypeRayQueryKHR",
          SPIRVOpcodeNumber(4472),
          definition =>
            raw =>
              Instruction.decodeDefinition(definition, raw, values => Instruction.TypeRayQueryKHR(values.required(0))),
          "IdResult"
        )
    case RayQueryInitializeKHR extends Definition(
          "OpRayQueryInitializeKHR",
          SPIRVOpcodeNumber(4473),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.RayQueryInitializeKHR(
                    rayQuery = values.required(0),
                    accel = values.required(1),
                    rayFlags = values.required(2),
                    cullMask = values.required(3),
                    rayOrigin = values.required(4),
                    rayTMin = values.required(5),
                    rayDirection = values.required(6),
                    rayTMax = values.required(7)
                  )
              ),
          "IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef"
        )
    case RayQueryTerminateKHR extends Definition(
          "OpRayQueryTerminateKHR",
          SPIRVOpcodeNumber(4474),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.RayQueryTerminateKHR(values.required(0))
              ),
          "IdRef"
        )
    case RayQueryGenerateIntersectionKHR extends Definition(
          "OpRayQueryGenerateIntersectionKHR",
          SPIRVOpcodeNumber(4475),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.RayQueryGenerateIntersectionKHR(values.required(0), values.required(1))
              ),
          "IdRef IdRef"
        )
    case RayQueryConfirmIntersectionKHR extends Definition(
          "OpRayQueryConfirmIntersectionKHR",
          SPIRVOpcodeNumber(4476),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.RayQueryConfirmIntersectionKHR(values.required(0))
              ),
          "IdRef"
        )
    case RayQueryProceedKHR extends Definition(
          "OpRayQueryProceedKHR",
          SPIRVOpcodeNumber(4477),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.RayQueryProceedKHR(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case RayQueryGetIntersectionTypeKHR extends Definition(
          "OpRayQueryGetIntersectionTypeKHR",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case ImageSampleWeightedQCOM extends Definition(
          "OpImageSampleWeightedQCOM",
          SPIRVOpcodeNumber(4480),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageSampleWeightedQCOM(
                    resultType = values.required(0),
                    dest = values.required(1),
                    texture = values.required(2),
                    coordinates = values.required(3),
                    weights = values.required(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef"
        )
    case ImageBoxFilterQCOM extends Definition(
          "OpImageBoxFilterQCOM",
          SPIRVOpcodeNumber(4481),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageBoxFilterQCOM(
                    resultType = values.required(0),
                    dest = values.required(1),
                    texture = values.required(2),
                    coordinates = values.required(3),
                    boxSize = values.required(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef"
        )
    case ImageBlockMatchSSDQCOM extends Definition(
          "OpImageBlockMatchSSDQCOM",
          SPIRVOpcodeNumber(4482),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageBlockMatchSSDQCOM(
                    resultType = values.required(0),
                    dest = values.required(1),
                    target = values.required(2),
                    targetCoordinates = values.required(3),
                    reference = values.required(4),
                    referenceCoordinates = values.required(5),
                    blockSize = values.required(6)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef IdRef"
        )
    case ImageBlockMatchSADQCOM extends Definition(
          "OpImageBlockMatchSADQCOM",
          SPIRVOpcodeNumber(4483),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageBlockMatchSADQCOM(
                    resultType = values.required(0),
                    dest = values.required(1),
                    target = values.required(2),
                    targetCoordinates = values.required(3),
                    reference = values.required(4),
                    referenceCoordinates = values.required(5),
                    blockSize = values.required(6)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef IdRef"
        )
    case BitCastArrayQCOM extends Definition(
          "OpBitCastArrayQCOM",
          SPIRVOpcodeNumber(4497),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.BitCastArrayQCOM(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case ImageBlockMatchWindowSSDQCOM extends Definition(
          "OpImageBlockMatchWindowSSDQCOM",
          SPIRVOpcodeNumber(4500),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageBlockMatchWindowSSDQCOM(
                    resultType = values.required(0),
                    dest = values.required(1),
                    targetSampledImage = values.required(2),
                    targetCoordinates = values.required(3),
                    referenceSampledImage = values.required(4),
                    referenceCoordinates = values.required(5),
                    blockSize = values.required(6)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef IdRef"
        )
    case ImageBlockMatchWindowSADQCOM extends Definition(
          "OpImageBlockMatchWindowSADQCOM",
          SPIRVOpcodeNumber(4501),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageBlockMatchWindowSADQCOM(
                    resultType = values.required(0),
                    dest = values.required(1),
                    targetSampledImage = values.required(2),
                    targetCoordinates = values.required(3),
                    referenceSampledImage = values.required(4),
                    referenceCoordinates = values.required(5),
                    blockSize = values.required(6)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef IdRef"
        )
    case ImageBlockMatchGatherSSDQCOM extends Definition(
          "OpImageBlockMatchGatherSSDQCOM",
          SPIRVOpcodeNumber(4502),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageBlockMatchGatherSSDQCOM(
                    resultType = values.required(0),
                    dest = values.required(1),
                    targetSampledImage = values.required(2),
                    targetCoordinates = values.required(3),
                    referenceSampledImage = values.required(4),
                    referenceCoordinates = values.required(5),
                    blockSize = values.required(6)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef IdRef"
        )
    case ImageBlockMatchGatherSADQCOM extends Definition(
          "OpImageBlockMatchGatherSADQCOM",
          SPIRVOpcodeNumber(4503),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageBlockMatchGatherSADQCOM(
                    resultType = values.required(0),
                    dest = values.required(1),
                    targetSampledImage = values.required(2),
                    targetCoordinates = values.required(3),
                    referenceSampledImage = values.required(4),
                    referenceCoordinates = values.required(5),
                    blockSize = values.required(6)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef IdRef"
        )
    case CompositeConstructCoopMatQCOM extends Definition(
          "OpCompositeConstructCoopMatQCOM",
          SPIRVOpcodeNumber(4540),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CompositeConstructCoopMatQCOM(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case CompositeExtractCoopMatQCOM extends Definition(
          "OpCompositeExtractCoopMatQCOM",
          SPIRVOpcodeNumber(4541),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CompositeExtractCoopMatQCOM(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case ExtractSubArrayQCOM extends Definition(
          "OpExtractSubArrayQCOM",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case ImageGatherQCOM extends Definition(
          "OpImageGatherQCOM",
          SPIRVOpcodeNumber(4545),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageGatherQCOM(
                    resultType = values.required(0),
                    dest = values.required(1),
                    sampledImage = values.required(2),
                    coordinate = values.required(3),
                    component = values.required(4),
                    mode = values.required(5),
                    mods = values.optional(6)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef ImageOperands?"
        )
    case GroupIAddNonUniformAMD extends Definition(
          "OpGroupIAddNonUniformAMD",
          SPIRVOpcodeNumber(5000),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupIAddNonUniformAMD(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    operation = values.required(3),
                    x = values.required(4)
                  )
              ),
          "IdResultType IdResult IdScope GroupOperation IdRef"
        )
    case GroupFAddNonUniformAMD extends Definition(
          "OpGroupFAddNonUniformAMD",
          SPIRVOpcodeNumber(5001),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupFAddNonUniformAMD(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    operation = values.required(3),
                    x = values.required(4)
                  )
              ),
          "IdResultType IdResult IdScope GroupOperation IdRef"
        )
    case GroupFMinNonUniformAMD extends Definition(
          "OpGroupFMinNonUniformAMD",
          SPIRVOpcodeNumber(5002),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupFMinNonUniformAMD(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    operation = values.required(3),
                    x = values.required(4)
                  )
              ),
          "IdResultType IdResult IdScope GroupOperation IdRef"
        )
    case GroupUMinNonUniformAMD extends Definition(
          "OpGroupUMinNonUniformAMD",
          SPIRVOpcodeNumber(5003),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupUMinNonUniformAMD(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    operation = values.required(3),
                    x = values.required(4)
                  )
              ),
          "IdResultType IdResult IdScope GroupOperation IdRef"
        )
    case GroupSMinNonUniformAMD extends Definition(
          "OpGroupSMinNonUniformAMD",
          SPIRVOpcodeNumber(5004),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupSMinNonUniformAMD(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    operation = values.required(3),
                    x = values.required(4)
                  )
              ),
          "IdResultType IdResult IdScope GroupOperation IdRef"
        )
    case GroupFMaxNonUniformAMD extends Definition(
          "OpGroupFMaxNonUniformAMD",
          SPIRVOpcodeNumber(5005),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupFMaxNonUniformAMD(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    operation = values.required(3),
                    x = values.required(4)
                  )
              ),
          "IdResultType IdResult IdScope GroupOperation IdRef"
        )
    case GroupUMaxNonUniformAMD extends Definition(
          "OpGroupUMaxNonUniformAMD",
          SPIRVOpcodeNumber(5006),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupUMaxNonUniformAMD(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    operation = values.required(3),
                    x = values.required(4)
                  )
              ),
          "IdResultType IdResult IdScope GroupOperation IdRef"
        )
    case GroupSMaxNonUniformAMD extends Definition(
          "OpGroupSMaxNonUniformAMD",
          SPIRVOpcodeNumber(5007),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupSMaxNonUniformAMD(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    operation = values.required(3),
                    x = values.required(4)
                  )
              ),
          "IdResultType IdResult IdScope GroupOperation IdRef"
        )
    case FragmentMaskFetchAMD extends Definition(
          "OpFragmentMaskFetchAMD",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case FragmentFetchAMD extends Definition(
          "OpFragmentFetchAMD",
          SPIRVOpcodeNumber(5012),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FragmentFetchAMD(
                    resultType = values.required(0),
                    dest = values.required(1),
                    image = values.required(2),
                    coordinate = values.required(3),
                    fragmentIndex = values.required(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef"
        )
    case ReadClockKHR extends Definition(
          "OpReadClockKHR",
          SPIRVOpcodeNumber(5056),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ReadClockKHR(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdScope"
        )
    case AllocateNodePayloadsAMDX extends Definition(
          "OpAllocateNodePayloadsAMDX",
          SPIRVOpcodeNumber(5074),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.AllocateNodePayloadsAMDX(
                    resultType = values.required(0),
                    dest = values.required(1),
                    visibility = values.required(2),
                    payloadCount = values.required(3),
                    nodeIndex = values.required(4)
                  )
              ),
          "IdResultType IdResult IdScope IdRef IdRef"
        )
    case EnqueueNodePayloadsAMDX extends Definition(
          "OpEnqueueNodePayloadsAMDX",
          SPIRVOpcodeNumber(5075),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.EnqueueNodePayloadsAMDX(values.required(0))
              ),
          "IdRef"
        )
    case TypeNodePayloadArrayAMDX extends Definition(
          "OpTypeNodePayloadArrayAMDX",
          SPIRVOpcodeNumber(5076),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeNodePayloadArrayAMDX(values.required(0), values.required(1))
              ),
          "IdResult IdRef"
        )
    case FinishWritingNodePayloadAMDX extends Definition(
          "OpFinishWritingNodePayloadAMDX",
          SPIRVOpcodeNumber(5078),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FinishWritingNodePayloadAMDX(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case NodePayloadArrayLengthAMDX extends Definition(
          "OpNodePayloadArrayLengthAMDX",
          SPIRVOpcodeNumber(5090),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.NodePayloadArrayLengthAMDX(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case IsNodePayloadValidAMDX extends Definition(
          "OpIsNodePayloadValidAMDX",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case ConstantStringAMDX extends Definition(
          "OpConstantStringAMDX",
          SPIRVOpcodeNumber(5103),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ConstantStringAMDX(values.required(0), values.required(1))
              ),
          "IdResult LiteralString"
        )
    case SpecConstantStringAMDX extends Definition(
          "OpSpecConstantStringAMDX",
          SPIRVOpcodeNumber(5104),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.SpecConstantStringAMDX(values.required(0), values.required(1))
              ),
          "IdResult LiteralString"
        )
    case GroupNonUniformQuadAllKHR extends Definition(
          "OpGroupNonUniformQuadAllKHR",
          SPIRVOpcodeNumber(5110),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformQuadAllKHR(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case GroupNonUniformQuadAnyKHR extends Definition(
          "OpGroupNonUniformQuadAnyKHR",
          SPIRVOpcodeNumber(5111),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformQuadAnyKHR(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case TypeBufferEXT extends Definition(
          "OpTypeBufferEXT",
          SPIRVOpcodeNumber(5115),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeBufferEXT(values.required(0), values.required(1))
              ),
          "IdResult StorageClass"
        )
    case BufferPointerEXT extends Definition(
          "OpBufferPointerEXT",
          SPIRVOpcodeNumber(5119),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.BufferPointerEXT(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case AbortKHR extends Definition(
          "OpAbortKHR",
          SPIRVOpcodeNumber(5121),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.AbortKHR(values.required(0), values.required(1))
              ),
          "IdRef IdRef"
        )
    case UntypedImageTexelPointerEXT extends Definition(
          "OpUntypedImageTexelPointerEXT",
          SPIRVOpcodeNumber(5126),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.UntypedImageTexelPointerEXT(
                    resultType = values.required(0),
                    dest = values.required(1),
                    imageType = values.required(2),
                    image = values.required(3),
                    coordinate = values.required(4),
                    sample = values.required(5)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef"
        )
    case MemberDecorateIdEXT extends Definition(
          "OpMemberDecorateIdEXT",
          SPIRVOpcodeNumber(5127),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.MemberDecorateIdEXT(values.required(0), values.required(1), values.required(2))
              ),
          "IdRef LiteralInteger Decoration"
        )
    case ConstantSizeOfEXT extends Definition(
          "OpConstantSizeOfEXT",
          SPIRVOpcodeNumber(5129),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ConstantSizeOfEXT(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case ConstantDataKHR extends Definition(
          "OpConstantDataKHR",
          SPIRVOpcodeNumber(5147),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ConstantDataKHR(values.required(0), values.required(1), values.repeated(2))
              ),
          "IdResultType IdResult LiteralInteger*"
        )
    case SpecConstantDataKHR extends Definition(
          "OpSpecConstantDataKHR",
          SPIRVOpcodeNumber(5148),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.SpecConstantDataKHR(values.required(0), values.required(1), values.repeated(2))
              ),
          "IdResultType IdResult LiteralInteger*"
        )
    case PoisonKHR extends Definition(
          "OpPoisonKHR",
          SPIRVOpcodeNumber(5158),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.PoisonKHR(values.required(0), values.required(1))
              ),
          "IdResultType IdResult"
        )
    case FreezeKHR extends Definition(
          "OpFreezeKHR",
          SPIRVOpcodeNumber(5159),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.FreezeKHR(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case HitObjectRecordHitMotionNV extends Definition(
          "OpHitObjectRecordHitMotionNV",
          SPIRVOpcodeNumber(5249),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectRecordHitMotionNV(
                    hitObject = values.required(0),
                    accelerationStructure = values.required(1),
                    instanceId = values.required(2),
                    primitiveId = values.required(3),
                    geometryIndex = values.required(4),
                    hitKind = values.required(5),
                    sBTRecordOffset = values.required(6),
                    sBTRecordStride = values.required(7),
                    origin = values.required(8),
                    tMin = values.required(9),
                    direction = values.required(10),
                    tMax = values.required(11),
                    currentTime = values.required(12),
                    hitObjectAttributes = values.required(13)
                  )
              ),
          "IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef"
        )
    case HitObjectRecordHitWithIndexMotionNV extends Definition(
          "OpHitObjectRecordHitWithIndexMotionNV",
          SPIRVOpcodeNumber(5250),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectRecordHitWithIndexMotionNV(
                    hitObject = values.required(0),
                    accelerationStructure = values.required(1),
                    instanceId = values.required(2),
                    primitiveId = values.required(3),
                    geometryIndex = values.required(4),
                    hitKind = values.required(5),
                    sBTRecordIndex = values.required(6),
                    origin = values.required(7),
                    tMin = values.required(8),
                    direction = values.required(9),
                    tMax = values.required(10),
                    currentTime = values.required(11),
                    hitObjectAttributes = values.required(12)
                  )
              ),
          "IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef"
        )
    case HitObjectRecordMissMotionNV extends Definition(
          "OpHitObjectRecordMissMotionNV",
          SPIRVOpcodeNumber(5251),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectRecordMissMotionNV(
                    hitObject = values.required(0),
                    sBTIndex = values.required(1),
                    origin = values.required(2),
                    tMin = values.required(3),
                    direction = values.required(4),
                    tMax = values.required(5),
                    currentTime = values.required(6)
                  )
              ),
          "IdRef IdRef IdRef IdRef IdRef IdRef IdRef"
        )
    case HitObjectGetWorldToObjectNV extends Definition(
          "OpHitObjectGetWorldToObjectNV",
          SPIRVOpcodeNumber(5252),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetWorldToObjectNV(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case HitObjectGetObjectToWorldNV extends Definition(
          "OpHitObjectGetObjectToWorldNV",
          SPIRVOpcodeNumber(5253),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetObjectToWorldNV(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case HitObjectGetObjectRayDirectionNV extends Definition(
          "OpHitObjectGetObjectRayDirectionNV",
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
          "IdResultType IdResult IdRef"
        )
    case HitObjectGetObjectRayOriginNV extends Definition(
          "OpHitObjectGetObjectRayOriginNV",
          SPIRVOpcodeNumber(5255),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetObjectRayOriginNV(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case HitObjectTraceRayMotionNV extends Definition(
          "OpHitObjectTraceRayMotionNV",
          SPIRVOpcodeNumber(5256),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectTraceRayMotionNV(
                    hitObject = values.required(0),
                    accelerationStructure = values.required(1),
                    rayFlags = values.required(2),
                    cullmask = values.required(3),
                    sBTRecordOffset = values.required(4),
                    sBTRecordStride = values.required(5),
                    missIndex = values.required(6),
                    origin = values.required(7),
                    tMin = values.required(8),
                    direction = values.required(9),
                    tMax = values.required(10),
                    time = values.required(11),
                    payload = values.required(12)
                  )
              ),
          "IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef"
        )
    case HitObjectGetShaderRecordBufferHandleNV extends Definition(
          "OpHitObjectGetShaderRecordBufferHandleNV",
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
          "IdResultType IdResult IdRef"
        )
    case HitObjectGetShaderBindingTableRecordIndexNV extends Definition(
          "OpHitObjectGetShaderBindingTableRecordIndexNV",
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
          "IdResultType IdResult IdRef"
        )
    case HitObjectRecordEmptyNV extends Definition(
          "OpHitObjectRecordEmptyNV",
          SPIRVOpcodeNumber(5259),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.HitObjectRecordEmptyNV(values.required(0))
              ),
          "IdRef"
        )
    case HitObjectTraceRayNV extends Definition(
          "OpHitObjectTraceRayNV",
          SPIRVOpcodeNumber(5260),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectTraceRayNV(
                    hitObject = values.required(0),
                    accelerationStructure = values.required(1),
                    rayFlags = values.required(2),
                    cullmask = values.required(3),
                    sBTRecordOffset = values.required(4),
                    sBTRecordStride = values.required(5),
                    missIndex = values.required(6),
                    origin = values.required(7),
                    tMin = values.required(8),
                    direction = values.required(9),
                    tMax = values.required(10),
                    payload = values.required(11)
                  )
              ),
          "IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef"
        )
    case HitObjectRecordHitNV extends Definition(
          "OpHitObjectRecordHitNV",
          SPIRVOpcodeNumber(5261),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectRecordHitNV(
                    hitObject = values.required(0),
                    accelerationStructure = values.required(1),
                    instanceId = values.required(2),
                    primitiveId = values.required(3),
                    geometryIndex = values.required(4),
                    hitKind = values.required(5),
                    sBTRecordOffset = values.required(6),
                    sBTRecordStride = values.required(7),
                    origin = values.required(8),
                    tMin = values.required(9),
                    direction = values.required(10),
                    tMax = values.required(11),
                    hitObjectAttributes = values.required(12)
                  )
              ),
          "IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef"
        )
    case HitObjectRecordHitWithIndexNV extends Definition(
          "OpHitObjectRecordHitWithIndexNV",
          SPIRVOpcodeNumber(5262),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectRecordHitWithIndexNV(
                    hitObject = values.required(0),
                    accelerationStructure = values.required(1),
                    instanceId = values.required(2),
                    primitiveId = values.required(3),
                    geometryIndex = values.required(4),
                    hitKind = values.required(5),
                    sBTRecordIndex = values.required(6),
                    origin = values.required(7),
                    tMin = values.required(8),
                    direction = values.required(9),
                    tMax = values.required(10),
                    hitObjectAttributes = values.required(11)
                  )
              ),
          "IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef"
        )
    case HitObjectRecordMissNV extends Definition(
          "OpHitObjectRecordMissNV",
          SPIRVOpcodeNumber(5263),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectRecordMissNV(
                    hitObject = values.required(0),
                    sBTIndex = values.required(1),
                    origin = values.required(2),
                    tMin = values.required(3),
                    direction = values.required(4),
                    tMax = values.required(5)
                  )
              ),
          "IdRef IdRef IdRef IdRef IdRef IdRef"
        )
    case HitObjectExecuteShaderNV extends Definition(
          "OpHitObjectExecuteShaderNV",
          SPIRVOpcodeNumber(5264),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.HitObjectExecuteShaderNV(values.required(0), values.required(1))
              ),
          "IdRef IdRef"
        )
    case HitObjectGetCurrentTimeNV extends Definition(
          "OpHitObjectGetCurrentTimeNV",
          SPIRVOpcodeNumber(5265),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetCurrentTimeNV(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case HitObjectGetAttributesNV extends Definition(
          "OpHitObjectGetAttributesNV",
          SPIRVOpcodeNumber(5266),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.HitObjectGetAttributesNV(values.required(0), values.required(1))
              ),
          "IdRef IdRef"
        )
    case HitObjectGetHitKindNV extends Definition(
          "OpHitObjectGetHitKindNV",
          SPIRVOpcodeNumber(5267),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.HitObjectGetHitKindNV(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case HitObjectGetPrimitiveIndexNV extends Definition(
          "OpHitObjectGetPrimitiveIndexNV",
          SPIRVOpcodeNumber(5268),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetPrimitiveIndexNV(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case HitObjectGetGeometryIndexNV extends Definition(
          "OpHitObjectGetGeometryIndexNV",
          SPIRVOpcodeNumber(5269),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetGeometryIndexNV(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case HitObjectGetInstanceIdNV extends Definition(
          "OpHitObjectGetInstanceIdNV",
          SPIRVOpcodeNumber(5270),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetInstanceIdNV(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case HitObjectGetInstanceCustomIndexNV extends Definition(
          "OpHitObjectGetInstanceCustomIndexNV",
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
          "IdResultType IdResult IdRef"
        )
    case HitObjectGetWorldRayDirectionNV extends Definition(
          "OpHitObjectGetWorldRayDirectionNV",
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
          "IdResultType IdResult IdRef"
        )
    case HitObjectGetWorldRayOriginNV extends Definition(
          "OpHitObjectGetWorldRayOriginNV",
          SPIRVOpcodeNumber(5273),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetWorldRayOriginNV(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case HitObjectGetRayTMaxNV extends Definition(
          "OpHitObjectGetRayTMaxNV",
          SPIRVOpcodeNumber(5274),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.HitObjectGetRayTMaxNV(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case HitObjectGetRayTMinNV extends Definition(
          "OpHitObjectGetRayTMinNV",
          SPIRVOpcodeNumber(5275),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.HitObjectGetRayTMinNV(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case HitObjectIsEmptyNV extends Definition(
          "OpHitObjectIsEmptyNV",
          SPIRVOpcodeNumber(5276),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.HitObjectIsEmptyNV(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case HitObjectIsHitNV extends Definition(
          "OpHitObjectIsHitNV",
          SPIRVOpcodeNumber(5277),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.HitObjectIsHitNV(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case HitObjectIsMissNV extends Definition(
          "OpHitObjectIsMissNV",
          SPIRVOpcodeNumber(5278),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.HitObjectIsMissNV(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case ReorderThreadWithHitObjectNV extends Definition(
          "OpReorderThreadWithHitObjectNV",
          SPIRVOpcodeNumber(5279),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ReorderThreadWithHitObjectNV(values.required(0), values.optional(1), values.optional(2))
              ),
          "IdRef IdRef? IdRef?"
        )
    case ReorderThreadWithHintNV extends Definition(
          "OpReorderThreadWithHintNV",
          SPIRVOpcodeNumber(5280),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ReorderThreadWithHintNV(values.required(0), values.required(1))
              ),
          "IdRef IdRef"
        )
    case TypeHitObjectNV extends Definition(
          "OpTypeHitObjectNV",
          SPIRVOpcodeNumber(5281),
          definition =>
            raw =>
              Instruction.decodeDefinition(definition, raw, values => Instruction.TypeHitObjectNV(values.required(0))),
          "IdResult"
        )
    case ImageSampleFootprintNV extends Definition(
          "OpImageSampleFootprintNV",
          SPIRVOpcodeNumber(5283),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ImageSampleFootprintNV(
                    resultType = values.required(0),
                    dest = values.required(1),
                    sampledImage = values.required(2),
                    coordinate = values.required(3),
                    granularity = values.required(4),
                    coarse = values.required(5),
                    mods = values.optional(6)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef ImageOperands?"
        )
    case TypeVectorIdEXT extends Definition(
          "OpTypeVectorIdEXT",
          SPIRVOpcodeNumber(5288),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeVectorIdEXT(values.required(0), values.required(1), values.required(2))
              ),
          "IdResult IdRef IdRef"
        )
    case CooperativeVectorMatrixMulNV extends Definition(
          "OpCooperativeVectorMatrixMulNV",
          SPIRVOpcodeNumber(5289),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CooperativeVectorMatrixMulNV(
                    resultType = values.required(0),
                    dest = values.required(1),
                    input = values.required(2),
                    inputInterpretation = values.required(3),
                    matrix = values.required(4),
                    matrixOffset = values.required(5),
                    matrixInterpretation = values.required(6),
                    m = values.required(7),
                    k = values.required(8),
                    memoryLayout = values.required(9),
                    transpose = values.required(10),
                    matrixStride = values.optional(11),
                    cooperativeMatrixOperands = values.optional(12)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef? CooperativeMatrixOperands?"
        )
    case CooperativeVectorOuterProductAccumulateNV extends Definition(
          "OpCooperativeVectorOuterProductAccumulateNV",
          SPIRVOpcodeNumber(5290),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CooperativeVectorOuterProductAccumulateNV(
                    pointer = values.required(0),
                    offset = values.required(1),
                    a = values.required(2),
                    b = values.required(3),
                    memoryLayout = values.required(4),
                    matrixInterpretation = values.required(5),
                    matrixStride = values.optional(6)
                  )
              ),
          "IdRef IdRef IdRef IdRef IdRef IdRef IdRef?"
        )
    case CooperativeVectorReduceSumAccumulateNV extends Definition(
          "OpCooperativeVectorReduceSumAccumulateNV",
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
          "IdRef IdRef IdRef"
        )
    case CooperativeVectorMatrixMulAddNV extends Definition(
          "OpCooperativeVectorMatrixMulAddNV",
          SPIRVOpcodeNumber(5292),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CooperativeVectorMatrixMulAddNV(
                    resultType = values.required(0),
                    dest = values.required(1),
                    input = values.required(2),
                    inputInterpretation = values.required(3),
                    matrix = values.required(4),
                    matrixOffset = values.required(5),
                    matrixInterpretation = values.required(6),
                    bias = values.required(7),
                    biasOffset = values.required(8),
                    biasInterpretation = values.required(9),
                    m = values.required(10),
                    k = values.required(11),
                    memoryLayout = values.required(12),
                    transpose = values.required(13),
                    matrixStride = values.optional(14),
                    cooperativeMatrixOperands = values.optional(15)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef? CooperativeMatrixOperands?"
        )
    case CooperativeMatrixConvertNV extends Definition(
          "OpCooperativeMatrixConvertNV",
          SPIRVOpcodeNumber(5293),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CooperativeMatrixConvertNV(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case EmitMeshTasksEXT extends Definition(
          "OpEmitMeshTasksEXT",
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
          "IdRef IdRef IdRef IdRef?"
        )
    case SetMeshOutputsEXT extends Definition(
          "OpSetMeshOutputsEXT",
          SPIRVOpcodeNumber(5295),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.SetMeshOutputsEXT(values.required(0), values.required(1))
              ),
          "IdRef IdRef"
        )
    case GroupNonUniformPartitionEXT extends Definition(
          "OpGroupNonUniformPartitionEXT",
          SPIRVOpcodeNumber(5296),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupNonUniformPartitionEXT(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case WritePackedPrimitiveIndices4x8NV extends Definition(
          "OpWritePackedPrimitiveIndices4x8NV",
          SPIRVOpcodeNumber(5299),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.WritePackedPrimitiveIndices4x8NV(values.required(0), values.required(1))
              ),
          "IdRef IdRef"
        )
    case FetchMicroTriangleVertexPositionNV extends Definition(
          "OpFetchMicroTriangleVertexPositionNV",
          SPIRVOpcodeNumber(5300),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FetchMicroTriangleVertexPositionNV(
                    resultType = values.required(0),
                    dest = values.required(1),
                    accel = values.required(2),
                    instanceId = values.required(3),
                    geometryIndex = values.required(4),
                    primitiveIndex = values.required(5),
                    barycentric = values.required(6)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef IdRef"
        )
    case FetchMicroTriangleVertexBarycentricNV extends Definition(
          "OpFetchMicroTriangleVertexBarycentricNV",
          SPIRVOpcodeNumber(5301),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FetchMicroTriangleVertexBarycentricNV(
                    resultType = values.required(0),
                    dest = values.required(1),
                    accel = values.required(2),
                    instanceId = values.required(3),
                    geometryIndex = values.required(4),
                    primitiveIndex = values.required(5),
                    barycentric = values.required(6)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef IdRef"
        )
    case CooperativeVectorLoadNV extends Definition(
          "OpCooperativeVectorLoadNV",
          SPIRVOpcodeNumber(5302),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CooperativeVectorLoadNV(
                    resultType = values.required(0),
                    dest = values.required(1),
                    pointer = values.required(2),
                    offset = values.required(3),
                    mods = values.optional(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef MemoryAccess?"
        )
    case CooperativeVectorStoreNV extends Definition(
          "OpCooperativeVectorStoreNV",
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
          "IdRef IdRef IdRef MemoryAccess?"
        )
    case HitObjectRecordFromQueryEXT extends Definition(
          "OpHitObjectRecordFromQueryEXT",
          SPIRVOpcodeNumber(5304),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectRecordFromQueryEXT(
                    hitObject = values.required(0),
                    rayQuery = values.required(1),
                    sBTRecordIndex = values.required(2),
                    hitObjectAttributes = values.required(3),
                    hitKind = values.optional(4)
                  )
              ),
          "IdRef IdRef IdRef IdRef IdRef?"
        )
    case HitObjectRecordMissEXT extends Definition(
          "OpHitObjectRecordMissEXT",
          SPIRVOpcodeNumber(5305),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectRecordMissEXT(
                    hitObject = values.required(0),
                    rayFlags = values.required(1),
                    missIndex = values.required(2),
                    rayOrigin = values.required(3),
                    rayTmin = values.required(4),
                    rayDirection = values.required(5),
                    rayTmax = values.required(6)
                  )
              ),
          "IdRef IdRef IdRef IdRef IdRef IdRef IdRef"
        )
    case HitObjectRecordMissMotionEXT extends Definition(
          "OpHitObjectRecordMissMotionEXT",
          SPIRVOpcodeNumber(5306),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectRecordMissMotionEXT(
                    hitObject = values.required(0),
                    rayFlags = values.required(1),
                    missIndex = values.required(2),
                    rayOrigin = values.required(3),
                    rayTmin = values.required(4),
                    rayDirection = values.required(5),
                    rayTmax = values.required(6),
                    currentTime = values.required(7)
                  )
              ),
          "IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef"
        )
    case HitObjectGetIntersectionTriangleVertexPositionsEXT extends Definition(
          "OpHitObjectGetIntersectionTriangleVertexPositionsEXT",
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
          "IdResultType IdResult IdRef"
        )
    case HitObjectGetRayFlagsEXT extends Definition(
          "OpHitObjectGetRayFlagsEXT",
          SPIRVOpcodeNumber(5308),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetRayFlagsEXT(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case HitObjectSetShaderBindingTableRecordIndexEXT extends Definition(
          "OpHitObjectSetShaderBindingTableRecordIndexEXT",
          SPIRVOpcodeNumber(5309),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectSetShaderBindingTableRecordIndexEXT(values.required(0), values.required(1))
              ),
          "IdRef IdRef"
        )
    case HitObjectReorderExecuteShaderEXT extends Definition(
          "OpHitObjectReorderExecuteShaderEXT",
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
          "IdRef IdRef IdRef? IdRef?"
        )
    case HitObjectTraceReorderExecuteEXT extends Definition(
          "OpHitObjectTraceReorderExecuteEXT",
          SPIRVOpcodeNumber(5311),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectTraceReorderExecuteEXT(
                    hitObject = values.required(0),
                    accelerationStructure = values.required(1),
                    rayFlags = values.required(2),
                    cullMask = values.required(3),
                    sBTOffset = values.required(4),
                    sBTStride = values.required(5),
                    missIndex = values.required(6),
                    rayOrigin = values.required(7),
                    rayTmin = values.required(8),
                    rayDirection = values.required(9),
                    rayTmax = values.required(10),
                    payload = values.required(11),
                    hint = values.optional(12),
                    bits = values.optional(13)
                  )
              ),
          "IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef? IdRef?"
        )
    case HitObjectTraceMotionReorderExecuteEXT extends Definition(
          "OpHitObjectTraceMotionReorderExecuteEXT",
          SPIRVOpcodeNumber(5312),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectTraceMotionReorderExecuteEXT(
                    hitObject = values.required(0),
                    accelerationStructure = values.required(1),
                    rayFlags = values.required(2),
                    cullMask = values.required(3),
                    sBTOffset = values.required(4),
                    sBTStride = values.required(5),
                    missIndex = values.required(6),
                    rayOrigin = values.required(7),
                    rayTmin = values.required(8),
                    rayDirection = values.required(9),
                    rayTmax = values.required(10),
                    currentTime = values.required(11),
                    payload = values.required(12),
                    hint = values.optional(13),
                    bits = values.optional(14)
                  )
              ),
          "IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef? IdRef?"
        )
    case TypeHitObjectEXT extends Definition(
          "OpTypeHitObjectEXT",
          SPIRVOpcodeNumber(5313),
          definition =>
            raw =>
              Instruction.decodeDefinition(definition, raw, values => Instruction.TypeHitObjectEXT(values.required(0))),
          "IdResult"
        )
    case ReorderThreadWithHintEXT extends Definition(
          "OpReorderThreadWithHintEXT",
          SPIRVOpcodeNumber(5314),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ReorderThreadWithHintEXT(values.required(0), values.required(1))
              ),
          "IdRef IdRef"
        )
    case ReorderThreadWithHitObjectEXT extends Definition(
          "OpReorderThreadWithHitObjectEXT",
          SPIRVOpcodeNumber(5315),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ReorderThreadWithHitObjectEXT(values.required(0), values.optional(1), values.optional(2))
              ),
          "IdRef IdRef? IdRef?"
        )
    case HitObjectTraceRayEXT extends Definition(
          "OpHitObjectTraceRayEXT",
          SPIRVOpcodeNumber(5316),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectTraceRayEXT(
                    hitObject = values.required(0),
                    accelerationStructure = values.required(1),
                    rayFlags = values.required(2),
                    cullMask = values.required(3),
                    sBTOffset = values.required(4),
                    sBTStride = values.required(5),
                    missIndex = values.required(6),
                    rayOrigin = values.required(7),
                    rayTmin = values.required(8),
                    rayDirection = values.required(9),
                    rayTmax = values.required(10),
                    payload = values.required(11)
                  )
              ),
          "IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef"
        )
    case HitObjectTraceRayMotionEXT extends Definition(
          "OpHitObjectTraceRayMotionEXT",
          SPIRVOpcodeNumber(5317),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectTraceRayMotionEXT(
                    hitObject = values.required(0),
                    accelerationStructure = values.required(1),
                    rayFlags = values.required(2),
                    cullMask = values.required(3),
                    sBTOffset = values.required(4),
                    sBTStride = values.required(5),
                    missIndex = values.required(6),
                    rayOrigin = values.required(7),
                    rayTmin = values.required(8),
                    rayDirection = values.required(9),
                    rayTmax = values.required(10),
                    currentTime = values.required(11),
                    payload = values.required(12)
                  )
              ),
          "IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef"
        )
    case HitObjectRecordEmptyEXT extends Definition(
          "OpHitObjectRecordEmptyEXT",
          SPIRVOpcodeNumber(5318),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.HitObjectRecordEmptyEXT(values.required(0))
              ),
          "IdRef"
        )
    case HitObjectExecuteShaderEXT extends Definition(
          "OpHitObjectExecuteShaderEXT",
          SPIRVOpcodeNumber(5319),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.HitObjectExecuteShaderEXT(values.required(0), values.required(1))
              ),
          "IdRef IdRef"
        )
    case HitObjectGetCurrentTimeEXT extends Definition(
          "OpHitObjectGetCurrentTimeEXT",
          SPIRVOpcodeNumber(5320),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetCurrentTimeEXT(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case HitObjectGetAttributesEXT extends Definition(
          "OpHitObjectGetAttributesEXT",
          SPIRVOpcodeNumber(5321),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.HitObjectGetAttributesEXT(values.required(0), values.required(1))
              ),
          "IdRef IdRef"
        )
    case HitObjectGetHitKindEXT extends Definition(
          "OpHitObjectGetHitKindEXT",
          SPIRVOpcodeNumber(5322),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.HitObjectGetHitKindEXT(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case HitObjectGetPrimitiveIndexEXT extends Definition(
          "OpHitObjectGetPrimitiveIndexEXT",
          SPIRVOpcodeNumber(5323),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetPrimitiveIndexEXT(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case HitObjectGetGeometryIndexEXT extends Definition(
          "OpHitObjectGetGeometryIndexEXT",
          SPIRVOpcodeNumber(5324),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetGeometryIndexEXT(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case HitObjectGetInstanceIdEXT extends Definition(
          "OpHitObjectGetInstanceIdEXT",
          SPIRVOpcodeNumber(5325),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetInstanceIdEXT(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case HitObjectGetInstanceCustomIndexEXT extends Definition(
          "OpHitObjectGetInstanceCustomIndexEXT",
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
          "IdResultType IdResult IdRef"
        )
    case HitObjectGetObjectRayOriginEXT extends Definition(
          "OpHitObjectGetObjectRayOriginEXT",
          SPIRVOpcodeNumber(5327),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetObjectRayOriginEXT(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case HitObjectGetObjectRayDirectionEXT extends Definition(
          "OpHitObjectGetObjectRayDirectionEXT",
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
          "IdResultType IdResult IdRef"
        )
    case HitObjectGetWorldRayDirectionEXT extends Definition(
          "OpHitObjectGetWorldRayDirectionEXT",
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
          "IdResultType IdResult IdRef"
        )
    case HitObjectGetWorldRayOriginEXT extends Definition(
          "OpHitObjectGetWorldRayOriginEXT",
          SPIRVOpcodeNumber(5330),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetWorldRayOriginEXT(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case HitObjectGetObjectToWorldEXT extends Definition(
          "OpHitObjectGetObjectToWorldEXT",
          SPIRVOpcodeNumber(5331),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetObjectToWorldEXT(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case HitObjectGetWorldToObjectEXT extends Definition(
          "OpHitObjectGetWorldToObjectEXT",
          SPIRVOpcodeNumber(5332),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetWorldToObjectEXT(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case HitObjectGetRayTMaxEXT extends Definition(
          "OpHitObjectGetRayTMaxEXT",
          SPIRVOpcodeNumber(5333),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.HitObjectGetRayTMaxEXT(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case ReportIntersectionKHR extends Definition(
          "OpReportIntersectionKHR",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case IgnoreIntersectionNV extends Definition(
          "OpIgnoreIntersectionNV",
          SPIRVOpcodeNumber(5335),
          definition => raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.IgnoreIntersectionNV()),
          ""
        )
    case TerminateRayNV extends Definition(
          "OpTerminateRayNV",
          SPIRVOpcodeNumber(5336),
          definition => raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.TerminateRayNV()),
          ""
        )
    case TraceNV extends Definition(
          "OpTraceNV",
          SPIRVOpcodeNumber(5337),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.TraceNV(
                    accel = values.required(0),
                    rayFlags = values.required(1),
                    cullMask = values.required(2),
                    sBTOffset = values.required(3),
                    sBTStride = values.required(4),
                    missIndex = values.required(5),
                    rayOrigin = values.required(6),
                    rayTmin = values.required(7),
                    rayDirection = values.required(8),
                    rayTmax = values.required(9),
                    payloadId = values.required(10)
                  )
              ),
          "IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef"
        )
    case TraceMotionNV extends Definition(
          "OpTraceMotionNV",
          SPIRVOpcodeNumber(5338),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.TraceMotionNV(
                    accel = values.required(0),
                    rayFlags = values.required(1),
                    cullMask = values.required(2),
                    sBTOffset = values.required(3),
                    sBTStride = values.required(4),
                    missIndex = values.required(5),
                    rayOrigin = values.required(6),
                    rayTmin = values.required(7),
                    rayDirection = values.required(8),
                    rayTmax = values.required(9),
                    time = values.required(10),
                    payloadId = values.required(11)
                  )
              ),
          "IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef"
        )
    case TraceRayMotionNV extends Definition(
          "OpTraceRayMotionNV",
          SPIRVOpcodeNumber(5339),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.TraceRayMotionNV(
                    accel = values.required(0),
                    rayFlags = values.required(1),
                    cullMask = values.required(2),
                    sBTOffset = values.required(3),
                    sBTStride = values.required(4),
                    missIndex = values.required(5),
                    rayOrigin = values.required(6),
                    rayTmin = values.required(7),
                    rayDirection = values.required(8),
                    rayTmax = values.required(9),
                    time = values.required(10),
                    payload = values.required(11)
                  )
              ),
          "IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef"
        )
    case RayQueryGetIntersectionTriangleVertexPositionsKHR extends Definition(
          "OpRayQueryGetIntersectionTriangleVertexPositionsKHR",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case TypeAccelerationStructureKHR extends Definition(
          "OpTypeAccelerationStructureKHR",
          SPIRVOpcodeNumber(5341),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeAccelerationStructureKHR(values.required(0))
              ),
          "IdResult"
        )
    case ExecuteCallableNV extends Definition(
          "OpExecuteCallableNV",
          SPIRVOpcodeNumber(5344),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ExecuteCallableNV(values.required(0), values.required(1))
              ),
          "IdRef IdRef"
        )
    case RayQueryGetIntersectionClusterIdNV extends Definition(
          "OpRayQueryGetIntersectionClusterIdNV",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case HitObjectGetClusterIdNV extends Definition(
          "OpHitObjectGetClusterIdNV",
          SPIRVOpcodeNumber(5346),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetClusterIdNV(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case HitObjectGetRayTMinEXT extends Definition(
          "OpHitObjectGetRayTMinEXT",
          SPIRVOpcodeNumber(5347),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.HitObjectGetRayTMinEXT(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case HitObjectGetShaderBindingTableRecordIndexEXT extends Definition(
          "OpHitObjectGetShaderBindingTableRecordIndexEXT",
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
          "IdResultType IdResult IdRef"
        )
    case HitObjectGetShaderRecordBufferHandleEXT extends Definition(
          "OpHitObjectGetShaderRecordBufferHandleEXT",
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
          "IdResultType IdResult IdRef"
        )
    case HitObjectIsEmptyEXT extends Definition(
          "OpHitObjectIsEmptyEXT",
          SPIRVOpcodeNumber(5350),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.HitObjectIsEmptyEXT(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case HitObjectIsHitEXT extends Definition(
          "OpHitObjectIsHitEXT",
          SPIRVOpcodeNumber(5351),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.HitObjectIsHitEXT(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case HitObjectIsMissEXT extends Definition(
          "OpHitObjectIsMissEXT",
          SPIRVOpcodeNumber(5352),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.HitObjectIsMissEXT(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case TypeCooperativeMatrixNV extends Definition(
          "OpTypeCooperativeMatrixNV",
          SPIRVOpcodeNumber(5358),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.TypeCooperativeMatrixNV(
                    dest = values.required(0),
                    componentType = values.required(1),
                    execution = values.required(2),
                    rows = values.required(3),
                    columns = values.required(4)
                  )
              ),
          "IdResult IdRef IdScope IdRef IdRef"
        )
    case CooperativeMatrixLoadNV extends Definition(
          "OpCooperativeMatrixLoadNV",
          SPIRVOpcodeNumber(5359),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CooperativeMatrixLoadNV(
                    resultType = values.required(0),
                    dest = values.required(1),
                    pointer = values.required(2),
                    stride = values.required(3),
                    columnMajor = values.required(4),
                    mods = values.optional(5)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef MemoryAccess?"
        )
    case CooperativeMatrixStoreNV extends Definition(
          "OpCooperativeMatrixStoreNV",
          SPIRVOpcodeNumber(5360),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CooperativeMatrixStoreNV(
                    pointer = values.required(0),
                    src = values.required(1),
                    stride = values.required(2),
                    columnMajor = values.required(3),
                    mods = values.optional(4)
                  )
              ),
          "IdRef IdRef IdRef IdRef MemoryAccess?"
        )
    case CooperativeMatrixMulAddNV extends Definition(
          "OpCooperativeMatrixMulAddNV",
          SPIRVOpcodeNumber(5361),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CooperativeMatrixMulAddNV(
                    resultType = values.required(0),
                    dest = values.required(1),
                    a = values.required(2),
                    b = values.required(3),
                    c = values.required(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef"
        )
    case CooperativeMatrixLengthNV extends Definition(
          "OpCooperativeMatrixLengthNV",
          SPIRVOpcodeNumber(5362),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CooperativeMatrixLengthNV(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case BeginInvocationInterlockEXT extends Definition(
          "OpBeginInvocationInterlockEXT",
          SPIRVOpcodeNumber(5364),
          definition =>
            raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.BeginInvocationInterlockEXT()),
          ""
        )
    case EndInvocationInterlockEXT extends Definition(
          "OpEndInvocationInterlockEXT",
          SPIRVOpcodeNumber(5365),
          definition =>
            raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.EndInvocationInterlockEXT()),
          ""
        )
    case CooperativeMatrixReduceNV extends Definition(
          "OpCooperativeMatrixReduceNV",
          SPIRVOpcodeNumber(5366),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CooperativeMatrixReduceNV(
                    resultType = values.required(0),
                    dest = values.required(1),
                    matrix = values.required(2),
                    reduce = values.required(3),
                    combineFunc = values.required(4)
                  )
              ),
          "IdResultType IdResult IdRef CooperativeMatrixReduce IdRef"
        )
    case CooperativeMatrixLoadTensorNV extends Definition(
          "OpCooperativeMatrixLoadTensorNV",
          SPIRVOpcodeNumber(5367),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CooperativeMatrixLoadTensorNV(
                    resultType = values.required(0),
                    dest = values.required(1),
                    pointer = values.required(2),
                    src = values.required(3),
                    tensorLayout = values.required(4),
                    mods = values.required(5),
                    tensorAddressingOperands = values.required(6)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef MemoryAccess TensorAddressingOperands"
        )
    case CooperativeMatrixStoreTensorNV extends Definition(
          "OpCooperativeMatrixStoreTensorNV",
          SPIRVOpcodeNumber(5368),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CooperativeMatrixStoreTensorNV(
                    pointer = values.required(0),
                    src = values.required(1),
                    tensorLayout = values.required(2),
                    mods = values.required(3),
                    tensorAddressingOperands = values.required(4)
                  )
              ),
          "IdRef IdRef IdRef MemoryAccess TensorAddressingOperands"
        )
    case CooperativeMatrixPerElementOpNV extends Definition(
          "OpCooperativeMatrixPerElementOpNV",
          SPIRVOpcodeNumber(5369),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CooperativeMatrixPerElementOpNV(
                    resultType = values.required(0),
                    dest = values.required(1),
                    matrix = values.required(2),
                    func = values.required(3),
                    srcs = values.repeated(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef*"
        )
    case TypeTensorLayoutNV extends Definition(
          "OpTypeTensorLayoutNV",
          SPIRVOpcodeNumber(5370),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeTensorLayoutNV(values.required(0), values.required(1), values.required(2))
              ),
          "IdResult IdRef IdRef"
        )
    case TypeTensorViewNV extends Definition(
          "OpTypeTensorViewNV",
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
          "IdResult IdRef IdRef IdRef*"
        )
    case CreateTensorLayoutNV extends Definition(
          "OpCreateTensorLayoutNV",
          SPIRVOpcodeNumber(5372),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.CreateTensorLayoutNV(values.required(0), values.required(1))
              ),
          "IdResultType IdResult"
        )
    case TensorLayoutSetDimensionNV extends Definition(
          "OpTensorLayoutSetDimensionNV",
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
          "IdResultType IdResult IdRef IdRef*"
        )
    case TensorLayoutSetStrideNV extends Definition(
          "OpTensorLayoutSetStrideNV",
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
          "IdResultType IdResult IdRef IdRef*"
        )
    case TensorLayoutSliceNV extends Definition(
          "OpTensorLayoutSliceNV",
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
          "IdResultType IdResult IdRef IdRef*"
        )
    case TensorLayoutSetClampValueNV extends Definition(
          "OpTensorLayoutSetClampValueNV",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case CreateTensorViewNV extends Definition(
          "OpCreateTensorViewNV",
          SPIRVOpcodeNumber(5377),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.CreateTensorViewNV(values.required(0), values.required(1))
              ),
          "IdResultType IdResult"
        )
    case TensorViewSetDimensionNV extends Definition(
          "OpTensorViewSetDimensionNV",
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
          "IdResultType IdResult IdRef IdRef*"
        )
    case TensorViewSetStrideNV extends Definition(
          "OpTensorViewSetStrideNV",
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
          "IdResultType IdResult IdRef IdRef*"
        )
    case DemoteToHelperInvocation extends Definition(
          "OpDemoteToHelperInvocation",
          SPIRVOpcodeNumber(5380),
          definition =>
            raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.DemoteToHelperInvocation()),
          ""
        )
    case IsHelperInvocationEXT extends Definition(
          "OpIsHelperInvocationEXT",
          SPIRVOpcodeNumber(5381),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.IsHelperInvocationEXT(values.required(0), values.required(1))
              ),
          "IdResultType IdResult"
        )
    case TensorViewSetClipNV extends Definition(
          "OpTensorViewSetClipNV",
          SPIRVOpcodeNumber(5382),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.TensorViewSetClipNV(
                    resultType = values.required(0),
                    dest = values.required(1),
                    tensorView = values.required(2),
                    clipRowOffset = values.required(3),
                    clipRowSpan = values.required(4),
                    clipColOffset = values.required(5),
                    clipColSpan = values.required(6)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef IdRef"
        )
    case TensorLayoutSetBlockSizeNV extends Definition(
          "OpTensorLayoutSetBlockSizeNV",
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
          "IdResultType IdResult IdRef IdRef*"
        )
    case CooperativeMatrixTransposeNV extends Definition(
          "OpCooperativeMatrixTransposeNV",
          SPIRVOpcodeNumber(5390),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CooperativeMatrixTransposeNV(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case ConvertUToImageNV extends Definition(
          "OpConvertUToImageNV",
          SPIRVOpcodeNumber(5391),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ConvertUToImageNV(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case ConvertUToSamplerNV extends Definition(
          "OpConvertUToSamplerNV",
          SPIRVOpcodeNumber(5392),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ConvertUToSamplerNV(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case ConvertImageToUNV extends Definition(
          "OpConvertImageToUNV",
          SPIRVOpcodeNumber(5393),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ConvertImageToUNV(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case ConvertSamplerToUNV extends Definition(
          "OpConvertSamplerToUNV",
          SPIRVOpcodeNumber(5394),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ConvertSamplerToUNV(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case ConvertUToSampledImageNV extends Definition(
          "OpConvertUToSampledImageNV",
          SPIRVOpcodeNumber(5395),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ConvertUToSampledImageNV(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case ConvertSampledImageToUNV extends Definition(
          "OpConvertSampledImageToUNV",
          SPIRVOpcodeNumber(5396),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ConvertSampledImageToUNV(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case SamplerImageAddressingModeNV extends Definition(
          "OpSamplerImageAddressingModeNV",
          SPIRVOpcodeNumber(5397),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.SamplerImageAddressingModeNV(values.required(0))
              ),
          "LiteralInteger"
        )
    case RawAccessChainNV extends Definition(
          "OpRawAccessChainNV",
          SPIRVOpcodeNumber(5398),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.RawAccessChainNV(
                    resultType = values.required(0),
                    dest = values.required(1),
                    base = values.required(2),
                    byteStride = values.required(3),
                    elementIndex = values.required(4),
                    byteOffset = values.required(5),
                    rawAccessChainOperands = values.optional(6)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef RawAccessChainOperands?"
        )
    case RayQueryGetIntersectionSpherePositionNV extends Definition(
          "OpRayQueryGetIntersectionSpherePositionNV",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case RayQueryGetIntersectionSphereRadiusNV extends Definition(
          "OpRayQueryGetIntersectionSphereRadiusNV",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case RayQueryGetIntersectionLSSPositionsNV extends Definition(
          "OpRayQueryGetIntersectionLSSPositionsNV",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case RayQueryGetIntersectionLSSRadiiNV extends Definition(
          "OpRayQueryGetIntersectionLSSRadiiNV",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case RayQueryGetIntersectionLSSHitValueNV extends Definition(
          "OpRayQueryGetIntersectionLSSHitValueNV",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case HitObjectGetSpherePositionNV extends Definition(
          "OpHitObjectGetSpherePositionNV",
          SPIRVOpcodeNumber(5432),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetSpherePositionNV(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case HitObjectGetSphereRadiusNV extends Definition(
          "OpHitObjectGetSphereRadiusNV",
          SPIRVOpcodeNumber(5433),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetSphereRadiusNV(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case HitObjectGetLSSPositionsNV extends Definition(
          "OpHitObjectGetLSSPositionsNV",
          SPIRVOpcodeNumber(5434),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.HitObjectGetLSSPositionsNV(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case HitObjectGetLSSRadiiNV extends Definition(
          "OpHitObjectGetLSSRadiiNV",
          SPIRVOpcodeNumber(5435),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.HitObjectGetLSSRadiiNV(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case HitObjectIsSphereHitNV extends Definition(
          "OpHitObjectIsSphereHitNV",
          SPIRVOpcodeNumber(5436),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.HitObjectIsSphereHitNV(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case HitObjectIsLSSHitNV extends Definition(
          "OpHitObjectIsLSSHitNV",
          SPIRVOpcodeNumber(5437),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.HitObjectIsLSSHitNV(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case RayQueryIsSphereHitNV extends Definition(
          "OpRayQueryIsSphereHitNV",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case RayQueryIsLSSHitNV extends Definition(
          "OpRayQueryIsLSSHitNV",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case SubgroupShuffleINTEL extends Definition(
          "OpSubgroupShuffleINTEL",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case SubgroupShuffleDownINTEL extends Definition(
          "OpSubgroupShuffleDownINTEL",
          SPIRVOpcodeNumber(5572),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupShuffleDownINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    current = values.required(2),
                    next = values.required(3),
                    delta = values.required(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef"
        )
    case SubgroupShuffleUpINTEL extends Definition(
          "OpSubgroupShuffleUpINTEL",
          SPIRVOpcodeNumber(5573),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupShuffleUpINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    previous = values.required(2),
                    current = values.required(3),
                    delta = values.required(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef"
        )
    case SubgroupShuffleXorINTEL extends Definition(
          "OpSubgroupShuffleXorINTEL",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case SubgroupBlockReadINTEL extends Definition(
          "OpSubgroupBlockReadINTEL",
          SPIRVOpcodeNumber(5575),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.SubgroupBlockReadINTEL(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case SubgroupBlockWriteINTEL extends Definition(
          "OpSubgroupBlockWriteINTEL",
          SPIRVOpcodeNumber(5576),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.SubgroupBlockWriteINTEL(values.required(0), values.required(1))
              ),
          "IdRef IdRef"
        )
    case SubgroupImageBlockReadINTEL extends Definition(
          "OpSubgroupImageBlockReadINTEL",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case SubgroupImageBlockWriteINTEL extends Definition(
          "OpSubgroupImageBlockWriteINTEL",
          SPIRVOpcodeNumber(5578),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupImageBlockWriteINTEL(values.required(0), values.required(1), values.required(2))
              ),
          "IdRef IdRef IdRef"
        )
    case SubgroupImageMediaBlockReadINTEL extends Definition(
          "OpSubgroupImageMediaBlockReadINTEL",
          SPIRVOpcodeNumber(5580),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupImageMediaBlockReadINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    image = values.required(2),
                    coordinate = values.required(3),
                    width = values.required(4),
                    height = values.required(5)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef"
        )
    case SubgroupImageMediaBlockWriteINTEL extends Definition(
          "OpSubgroupImageMediaBlockWriteINTEL",
          SPIRVOpcodeNumber(5581),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupImageMediaBlockWriteINTEL(
                    image = values.required(0),
                    coordinate = values.required(1),
                    width = values.required(2),
                    height = values.required(3),
                    data = values.required(4)
                  )
              ),
          "IdRef IdRef IdRef IdRef IdRef"
        )
    case UCountLeadingZerosINTEL extends Definition(
          "OpUCountLeadingZerosINTEL",
          SPIRVOpcodeNumber(5585),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.UCountLeadingZerosINTEL(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case UCountTrailingZerosINTEL extends Definition(
          "OpUCountTrailingZerosINTEL",
          SPIRVOpcodeNumber(5586),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.UCountTrailingZerosINTEL(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case AbsISubINTEL extends Definition(
          "OpAbsISubINTEL",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case AbsUSubINTEL extends Definition(
          "OpAbsUSubINTEL",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case IAddSatINTEL extends Definition(
          "OpIAddSatINTEL",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case UAddSatINTEL extends Definition(
          "OpUAddSatINTEL",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case IAverageINTEL extends Definition(
          "OpIAverageINTEL",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case UAverageINTEL extends Definition(
          "OpUAverageINTEL",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case IAverageRoundedINTEL extends Definition(
          "OpIAverageRoundedINTEL",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case UAverageRoundedINTEL extends Definition(
          "OpUAverageRoundedINTEL",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case ISubSatINTEL extends Definition(
          "OpISubSatINTEL",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case USubSatINTEL extends Definition(
          "OpUSubSatINTEL",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case IMul32x16INTEL extends Definition(
          "OpIMul32x16INTEL",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case UMul32x16INTEL extends Definition(
          "OpUMul32x16INTEL",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case ConstantFunctionPointerINTEL extends Definition(
          "OpConstantFunctionPointerINTEL",
          SPIRVOpcodeNumber(5600),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ConstantFunctionPointerINTEL(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case FunctionPointerCallINTEL extends Definition(
          "OpFunctionPointerCallINTEL",
          SPIRVOpcodeNumber(5601),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FunctionPointerCallINTEL(values.required(0), values.required(1), values.repeated(2))
              ),
          "IdResultType IdResult IdRef*"
        )
    case AsmTargetINTEL extends Definition(
          "OpAsmTargetINTEL",
          SPIRVOpcodeNumber(5609),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.AsmTargetINTEL(values.required(0), values.required(1))
              ),
          "IdResult LiteralString"
        )
    case AsmINTEL extends Definition(
          "OpAsmINTEL",
          SPIRVOpcodeNumber(5610),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.AsmINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    asmType = values.required(2),
                    target = values.required(3),
                    asmInsts = values.required(4),
                    constraints = values.required(5)
                  )
              ),
          "IdResultType IdResult IdRef IdRef LiteralString LiteralString"
        )
    case AsmCallINTEL extends Definition(
          "OpAsmCallINTEL",
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
          "IdResultType IdResult IdRef IdRef*"
        )
    case AtomicFMinEXT extends Definition(
          "OpAtomicFMinEXT",
          SPIRVOpcodeNumber(5614),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.AtomicFMinEXT(
                    resultType = values.required(0),
                    dest = values.required(1),
                    pointer = values.required(2),
                    memory = values.required(3),
                    semantics = values.required(4),
                    value = values.required(5)
                  )
              ),
          "IdResultType IdResult IdRef IdScope IdMemorySemantics IdRef"
        )
    case AtomicFMaxEXT extends Definition(
          "OpAtomicFMaxEXT",
          SPIRVOpcodeNumber(5615),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.AtomicFMaxEXT(
                    resultType = values.required(0),
                    dest = values.required(1),
                    pointer = values.required(2),
                    memory = values.required(3),
                    semantics = values.required(4),
                    value = values.required(5)
                  )
              ),
          "IdResultType IdResult IdRef IdScope IdMemorySemantics IdRef"
        )
    case AssumeTrueKHR extends Definition(
          "OpAssumeTrueKHR",
          SPIRVOpcodeNumber(5630),
          definition =>
            raw =>
              Instruction.decodeDefinition(definition, raw, values => Instruction.AssumeTrueKHR(values.required(0))),
          "IdRef"
        )
    case ExpectKHR extends Definition(
          "OpExpectKHR",
          SPIRVOpcodeNumber(5631),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ExpectKHR(values.required(0), values.required(1), values.required(2), values.required(3))
              ),
          "IdResultType IdResult IdRef IdRef"
        )
    case DecorateString extends Definition(
          "OpDecorateString",
          SPIRVOpcodeNumber(5632),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.DecorateString(values.required(0), values.required(1))
              ),
          "IdRef Decoration"
        )
    case MemberDecorateString extends Definition(
          "OpMemberDecorateString",
          SPIRVOpcodeNumber(5633),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.MemberDecorateString(values.required(0), values.required(1), values.required(2))
              ),
          "IdRef LiteralInteger Decoration"
        )
    case VmeImageINTEL extends Definition(
          "OpVmeImageINTEL",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case TypeVmeImageINTEL extends Definition(
          "OpTypeVmeImageINTEL",
          SPIRVOpcodeNumber(5700),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeVmeImageINTEL(values.required(0), values.required(1))
              ),
          "IdResult IdRef"
        )
    case TypeAvcImePayloadINTEL extends Definition(
          "OpTypeAvcImePayloadINTEL",
          SPIRVOpcodeNumber(5701),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeAvcImePayloadINTEL(values.required(0))
              ),
          "IdResult"
        )
    case TypeAvcRefPayloadINTEL extends Definition(
          "OpTypeAvcRefPayloadINTEL",
          SPIRVOpcodeNumber(5702),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeAvcRefPayloadINTEL(values.required(0))
              ),
          "IdResult"
        )
    case TypeAvcSicPayloadINTEL extends Definition(
          "OpTypeAvcSicPayloadINTEL",
          SPIRVOpcodeNumber(5703),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeAvcSicPayloadINTEL(values.required(0))
              ),
          "IdResult"
        )
    case TypeAvcMcePayloadINTEL extends Definition(
          "OpTypeAvcMcePayloadINTEL",
          SPIRVOpcodeNumber(5704),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeAvcMcePayloadINTEL(values.required(0))
              ),
          "IdResult"
        )
    case TypeAvcMceResultINTEL extends Definition(
          "OpTypeAvcMceResultINTEL",
          SPIRVOpcodeNumber(5705),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeAvcMceResultINTEL(values.required(0))
              ),
          "IdResult"
        )
    case TypeAvcImeResultINTEL extends Definition(
          "OpTypeAvcImeResultINTEL",
          SPIRVOpcodeNumber(5706),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeAvcImeResultINTEL(values.required(0))
              ),
          "IdResult"
        )
    case TypeAvcImeResultSingleReferenceStreamoutINTEL extends Definition(
          "OpTypeAvcImeResultSingleReferenceStreamoutINTEL",
          SPIRVOpcodeNumber(5707),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeAvcImeResultSingleReferenceStreamoutINTEL(values.required(0))
              ),
          "IdResult"
        )
    case TypeAvcImeResultDualReferenceStreamoutINTEL extends Definition(
          "OpTypeAvcImeResultDualReferenceStreamoutINTEL",
          SPIRVOpcodeNumber(5708),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeAvcImeResultDualReferenceStreamoutINTEL(values.required(0))
              ),
          "IdResult"
        )
    case TypeAvcImeSingleReferenceStreaminINTEL extends Definition(
          "OpTypeAvcImeSingleReferenceStreaminINTEL",
          SPIRVOpcodeNumber(5709),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeAvcImeSingleReferenceStreaminINTEL(values.required(0))
              ),
          "IdResult"
        )
    case TypeAvcImeDualReferenceStreaminINTEL extends Definition(
          "OpTypeAvcImeDualReferenceStreaminINTEL",
          SPIRVOpcodeNumber(5710),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeAvcImeDualReferenceStreaminINTEL(values.required(0))
              ),
          "IdResult"
        )
    case TypeAvcRefResultINTEL extends Definition(
          "OpTypeAvcRefResultINTEL",
          SPIRVOpcodeNumber(5711),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeAvcRefResultINTEL(values.required(0))
              ),
          "IdResult"
        )
    case TypeAvcSicResultINTEL extends Definition(
          "OpTypeAvcSicResultINTEL",
          SPIRVOpcodeNumber(5712),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeAvcSicResultINTEL(values.required(0))
              ),
          "IdResult"
        )
    case SubgroupAvcMceGetDefaultInterBaseMultiReferencePenaltyINTEL extends Definition(
          "OpSubgroupAvcMceGetDefaultInterBaseMultiReferencePenaltyINTEL",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case SubgroupAvcMceSetInterBaseMultiReferencePenaltyINTEL extends Definition(
          "OpSubgroupAvcMceSetInterBaseMultiReferencePenaltyINTEL",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case SubgroupAvcMceGetDefaultInterShapePenaltyINTEL extends Definition(
          "OpSubgroupAvcMceGetDefaultInterShapePenaltyINTEL",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case SubgroupAvcMceSetInterShapePenaltyINTEL extends Definition(
          "OpSubgroupAvcMceSetInterShapePenaltyINTEL",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case SubgroupAvcMceGetDefaultInterDirectionPenaltyINTEL extends Definition(
          "OpSubgroupAvcMceGetDefaultInterDirectionPenaltyINTEL",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case SubgroupAvcMceSetInterDirectionPenaltyINTEL extends Definition(
          "OpSubgroupAvcMceSetInterDirectionPenaltyINTEL",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case SubgroupAvcMceGetDefaultIntraLumaShapePenaltyINTEL extends Definition(
          "OpSubgroupAvcMceGetDefaultIntraLumaShapePenaltyINTEL",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case SubgroupAvcMceGetDefaultInterMotionVectorCostTableINTEL extends Definition(
          "OpSubgroupAvcMceGetDefaultInterMotionVectorCostTableINTEL",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case SubgroupAvcMceGetDefaultHighPenaltyCostTableINTEL extends Definition(
          "OpSubgroupAvcMceGetDefaultHighPenaltyCostTableINTEL",
          SPIRVOpcodeNumber(5721),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcMceGetDefaultHighPenaltyCostTableINTEL(values.required(0), values.required(1))
              ),
          "IdResultType IdResult"
        )
    case SubgroupAvcMceGetDefaultMediumPenaltyCostTableINTEL extends Definition(
          "OpSubgroupAvcMceGetDefaultMediumPenaltyCostTableINTEL",
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
          "IdResultType IdResult"
        )
    case SubgroupAvcMceGetDefaultLowPenaltyCostTableINTEL extends Definition(
          "OpSubgroupAvcMceGetDefaultLowPenaltyCostTableINTEL",
          SPIRVOpcodeNumber(5723),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcMceGetDefaultLowPenaltyCostTableINTEL(values.required(0), values.required(1))
              ),
          "IdResultType IdResult"
        )
    case SubgroupAvcMceSetMotionVectorCostFunctionINTEL extends Definition(
          "OpSubgroupAvcMceSetMotionVectorCostFunctionINTEL",
          SPIRVOpcodeNumber(5724),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcMceSetMotionVectorCostFunctionINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    packedCostCenterDelta = values.required(2),
                    packedCostTable = values.required(3),
                    costPrecision = values.required(4),
                    payload = values.required(5)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef"
        )
    case SubgroupAvcMceGetDefaultIntraLumaModePenaltyINTEL extends Definition(
          "OpSubgroupAvcMceGetDefaultIntraLumaModePenaltyINTEL",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case SubgroupAvcMceGetDefaultNonDcLumaIntraPenaltyINTEL extends Definition(
          "OpSubgroupAvcMceGetDefaultNonDcLumaIntraPenaltyINTEL",
          SPIRVOpcodeNumber(5726),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcMceGetDefaultNonDcLumaIntraPenaltyINTEL(values.required(0), values.required(1))
              ),
          "IdResultType IdResult"
        )
    case SubgroupAvcMceGetDefaultIntraChromaModeBasePenaltyINTEL extends Definition(
          "OpSubgroupAvcMceGetDefaultIntraChromaModeBasePenaltyINTEL",
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
          "IdResultType IdResult"
        )
    case SubgroupAvcMceSetAcOnlyHaarINTEL extends Definition(
          "OpSubgroupAvcMceSetAcOnlyHaarINTEL",
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
          "IdResultType IdResult IdRef"
        )
    case SubgroupAvcMceSetSourceInterlacedFieldPolarityINTEL extends Definition(
          "OpSubgroupAvcMceSetSourceInterlacedFieldPolarityINTEL",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case SubgroupAvcMceSetSingleReferenceInterlacedFieldPolarityINTEL extends Definition(
          "OpSubgroupAvcMceSetSingleReferenceInterlacedFieldPolarityINTEL",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case SubgroupAvcMceSetDualReferenceInterlacedFieldPolaritiesINTEL extends Definition(
          "OpSubgroupAvcMceSetDualReferenceInterlacedFieldPolaritiesINTEL",
          SPIRVOpcodeNumber(5731),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcMceSetDualReferenceInterlacedFieldPolaritiesINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    forwardReferenceFieldPolarity = values.required(2),
                    backwardReferenceFieldPolarity = values.required(3),
                    payload = values.required(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef"
        )
    case SubgroupAvcMceConvertToImePayloadINTEL extends Definition(
          "OpSubgroupAvcMceConvertToImePayloadINTEL",
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
          "IdResultType IdResult IdRef"
        )
    case SubgroupAvcMceConvertToImeResultINTEL extends Definition(
          "OpSubgroupAvcMceConvertToImeResultINTEL",
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
          "IdResultType IdResult IdRef"
        )
    case SubgroupAvcMceConvertToRefPayloadINTEL extends Definition(
          "OpSubgroupAvcMceConvertToRefPayloadINTEL",
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
          "IdResultType IdResult IdRef"
        )
    case SubgroupAvcMceConvertToRefResultINTEL extends Definition(
          "OpSubgroupAvcMceConvertToRefResultINTEL",
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
          "IdResultType IdResult IdRef"
        )
    case SubgroupAvcMceConvertToSicPayloadINTEL extends Definition(
          "OpSubgroupAvcMceConvertToSicPayloadINTEL",
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
          "IdResultType IdResult IdRef"
        )
    case SubgroupAvcMceConvertToSicResultINTEL extends Definition(
          "OpSubgroupAvcMceConvertToSicResultINTEL",
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
          "IdResultType IdResult IdRef"
        )
    case SubgroupAvcMceGetMotionVectorsINTEL extends Definition(
          "OpSubgroupAvcMceGetMotionVectorsINTEL",
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
          "IdResultType IdResult IdRef"
        )
    case SubgroupAvcMceGetInterDistortionsINTEL extends Definition(
          "OpSubgroupAvcMceGetInterDistortionsINTEL",
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
          "IdResultType IdResult IdRef"
        )
    case SubgroupAvcMceGetBestInterDistortionsINTEL extends Definition(
          "OpSubgroupAvcMceGetBestInterDistortionsINTEL",
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
          "IdResultType IdResult IdRef"
        )
    case SubgroupAvcMceGetInterMajorShapeINTEL extends Definition(
          "OpSubgroupAvcMceGetInterMajorShapeINTEL",
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
          "IdResultType IdResult IdRef"
        )
    case SubgroupAvcMceGetInterMinorShapeINTEL extends Definition(
          "OpSubgroupAvcMceGetInterMinorShapeINTEL",
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
          "IdResultType IdResult IdRef"
        )
    case SubgroupAvcMceGetInterDirectionsINTEL extends Definition(
          "OpSubgroupAvcMceGetInterDirectionsINTEL",
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
          "IdResultType IdResult IdRef"
        )
    case SubgroupAvcMceGetInterMotionVectorCountINTEL extends Definition(
          "OpSubgroupAvcMceGetInterMotionVectorCountINTEL",
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
          "IdResultType IdResult IdRef"
        )
    case SubgroupAvcMceGetInterReferenceIdsINTEL extends Definition(
          "OpSubgroupAvcMceGetInterReferenceIdsINTEL",
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
          "IdResultType IdResult IdRef"
        )
    case SubgroupAvcMceGetInterReferenceInterlacedFieldPolaritiesINTEL extends Definition(
          "OpSubgroupAvcMceGetInterReferenceInterlacedFieldPolaritiesINTEL",
          SPIRVOpcodeNumber(5746),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcMceGetInterReferenceInterlacedFieldPolaritiesINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    packedReferenceIds = values.required(2),
                    packedReferenceParameterFieldPolarities = values.required(3),
                    payload = values.required(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef"
        )
    case SubgroupAvcImeInitializeINTEL extends Definition(
          "OpSubgroupAvcImeInitializeINTEL",
          SPIRVOpcodeNumber(5747),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcImeInitializeINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    srcCoord = values.required(2),
                    partitionMask = values.required(3),
                    sADAdjustment = values.required(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef"
        )
    case SubgroupAvcImeSetSingleReferenceINTEL extends Definition(
          "OpSubgroupAvcImeSetSingleReferenceINTEL",
          SPIRVOpcodeNumber(5748),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcImeSetSingleReferenceINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    refOffset = values.required(2),
                    searchWindowConfig = values.required(3),
                    payload = values.required(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef"
        )
    case SubgroupAvcImeSetDualReferenceINTEL extends Definition(
          "OpSubgroupAvcImeSetDualReferenceINTEL",
          SPIRVOpcodeNumber(5749),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcImeSetDualReferenceINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    fwdRefOffset = values.required(2),
                    bwdRefOffset = values.required(3),
                    searchWindowConfig = values.required(4),
                    payload = values.required(5)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef"
        )
    case SubgroupAvcImeRefWindowSizeINTEL extends Definition(
          "OpSubgroupAvcImeRefWindowSizeINTEL",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case SubgroupAvcImeAdjustRefOffsetINTEL extends Definition(
          "OpSubgroupAvcImeAdjustRefOffsetINTEL",
          SPIRVOpcodeNumber(5751),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcImeAdjustRefOffsetINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    refOffset = values.required(2),
                    srcCoord = values.required(3),
                    refWindowSize = values.required(4),
                    imageSize = values.required(5)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef"
        )
    case SubgroupAvcImeConvertToMcePayloadINTEL extends Definition(
          "OpSubgroupAvcImeConvertToMcePayloadINTEL",
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
          "IdResultType IdResult IdRef"
        )
    case SubgroupAvcImeSetMaxMotionVectorCountINTEL extends Definition(
          "OpSubgroupAvcImeSetMaxMotionVectorCountINTEL",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case SubgroupAvcImeSetUnidirectionalMixDisableINTEL extends Definition(
          "OpSubgroupAvcImeSetUnidirectionalMixDisableINTEL",
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
          "IdResultType IdResult IdRef"
        )
    case SubgroupAvcImeSetEarlySearchTerminationThresholdINTEL extends Definition(
          "OpSubgroupAvcImeSetEarlySearchTerminationThresholdINTEL",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case SubgroupAvcImeSetWeightedSadINTEL extends Definition(
          "OpSubgroupAvcImeSetWeightedSadINTEL",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case SubgroupAvcImeEvaluateWithSingleReferenceINTEL extends Definition(
          "OpSubgroupAvcImeEvaluateWithSingleReferenceINTEL",
          SPIRVOpcodeNumber(5757),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcImeEvaluateWithSingleReferenceINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    srcImage = values.required(2),
                    refImage = values.required(3),
                    payload = values.required(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef"
        )
    case SubgroupAvcImeEvaluateWithDualReferenceINTEL extends Definition(
          "OpSubgroupAvcImeEvaluateWithDualReferenceINTEL",
          SPIRVOpcodeNumber(5758),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcImeEvaluateWithDualReferenceINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    srcImage = values.required(2),
                    fwdRefImage = values.required(3),
                    bwdRefImage = values.required(4),
                    payload = values.required(5)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef"
        )
    case SubgroupAvcImeEvaluateWithSingleReferenceStreaminINTEL extends Definition(
          "OpSubgroupAvcImeEvaluateWithSingleReferenceStreaminINTEL",
          SPIRVOpcodeNumber(5759),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcImeEvaluateWithSingleReferenceStreaminINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    srcImage = values.required(2),
                    refImage = values.required(3),
                    payload = values.required(4),
                    streaminComponents = values.required(5)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef"
        )
    case SubgroupAvcImeEvaluateWithDualReferenceStreaminINTEL extends Definition(
          "OpSubgroupAvcImeEvaluateWithDualReferenceStreaminINTEL",
          SPIRVOpcodeNumber(5760),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcImeEvaluateWithDualReferenceStreaminINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    srcImage = values.required(2),
                    fwdRefImage = values.required(3),
                    bwdRefImage = values.required(4),
                    payload = values.required(5),
                    streaminComponents = values.required(6)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef IdRef"
        )
    case SubgroupAvcImeEvaluateWithSingleReferenceStreamoutINTEL extends Definition(
          "OpSubgroupAvcImeEvaluateWithSingleReferenceStreamoutINTEL",
          SPIRVOpcodeNumber(5761),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcImeEvaluateWithSingleReferenceStreamoutINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    srcImage = values.required(2),
                    refImage = values.required(3),
                    payload = values.required(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef"
        )
    case SubgroupAvcImeEvaluateWithDualReferenceStreamoutINTEL extends Definition(
          "OpSubgroupAvcImeEvaluateWithDualReferenceStreamoutINTEL",
          SPIRVOpcodeNumber(5762),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcImeEvaluateWithDualReferenceStreamoutINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    srcImage = values.required(2),
                    fwdRefImage = values.required(3),
                    bwdRefImage = values.required(4),
                    payload = values.required(5)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef"
        )
    case SubgroupAvcImeEvaluateWithSingleReferenceStreaminoutINTEL extends Definition(
          "OpSubgroupAvcImeEvaluateWithSingleReferenceStreaminoutINTEL",
          SPIRVOpcodeNumber(5763),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcImeEvaluateWithSingleReferenceStreaminoutINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    srcImage = values.required(2),
                    refImage = values.required(3),
                    payload = values.required(4),
                    streaminComponents = values.required(5)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef"
        )
    case SubgroupAvcImeEvaluateWithDualReferenceStreaminoutINTEL extends Definition(
          "OpSubgroupAvcImeEvaluateWithDualReferenceStreaminoutINTEL",
          SPIRVOpcodeNumber(5764),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcImeEvaluateWithDualReferenceStreaminoutINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    srcImage = values.required(2),
                    fwdRefImage = values.required(3),
                    bwdRefImage = values.required(4),
                    payload = values.required(5),
                    streaminComponents = values.required(6)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef IdRef"
        )
    case SubgroupAvcImeConvertToMceResultINTEL extends Definition(
          "OpSubgroupAvcImeConvertToMceResultINTEL",
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
          "IdResultType IdResult IdRef"
        )
    case SubgroupAvcImeGetSingleReferenceStreaminINTEL extends Definition(
          "OpSubgroupAvcImeGetSingleReferenceStreaminINTEL",
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
          "IdResultType IdResult IdRef"
        )
    case SubgroupAvcImeGetDualReferenceStreaminINTEL extends Definition(
          "OpSubgroupAvcImeGetDualReferenceStreaminINTEL",
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
          "IdResultType IdResult IdRef"
        )
    case SubgroupAvcImeStripSingleReferenceStreamoutINTEL extends Definition(
          "OpSubgroupAvcImeStripSingleReferenceStreamoutINTEL",
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
          "IdResultType IdResult IdRef"
        )
    case SubgroupAvcImeStripDualReferenceStreamoutINTEL extends Definition(
          "OpSubgroupAvcImeStripDualReferenceStreamoutINTEL",
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
          "IdResultType IdResult IdRef"
        )
    case SubgroupAvcImeGetStreamoutSingleReferenceMajorShapeMotionVectorsINTEL extends Definition(
          "OpSubgroupAvcImeGetStreamoutSingleReferenceMajorShapeMotionVectorsINTEL",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case SubgroupAvcImeGetStreamoutSingleReferenceMajorShapeDistortionsINTEL extends Definition(
          "OpSubgroupAvcImeGetStreamoutSingleReferenceMajorShapeDistortionsINTEL",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case SubgroupAvcImeGetStreamoutSingleReferenceMajorShapeReferenceIdsINTEL extends Definition(
          "OpSubgroupAvcImeGetStreamoutSingleReferenceMajorShapeReferenceIdsINTEL",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case SubgroupAvcImeGetStreamoutDualReferenceMajorShapeMotionVectorsINTEL extends Definition(
          "OpSubgroupAvcImeGetStreamoutDualReferenceMajorShapeMotionVectorsINTEL",
          SPIRVOpcodeNumber(5773),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcImeGetStreamoutDualReferenceMajorShapeMotionVectorsINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    payload = values.required(2),
                    majorShape = values.required(3),
                    direction = values.required(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef"
        )
    case SubgroupAvcImeGetStreamoutDualReferenceMajorShapeDistortionsINTEL extends Definition(
          "OpSubgroupAvcImeGetStreamoutDualReferenceMajorShapeDistortionsINTEL",
          SPIRVOpcodeNumber(5774),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcImeGetStreamoutDualReferenceMajorShapeDistortionsINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    payload = values.required(2),
                    majorShape = values.required(3),
                    direction = values.required(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef"
        )
    case SubgroupAvcImeGetStreamoutDualReferenceMajorShapeReferenceIdsINTEL extends Definition(
          "OpSubgroupAvcImeGetStreamoutDualReferenceMajorShapeReferenceIdsINTEL",
          SPIRVOpcodeNumber(5775),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcImeGetStreamoutDualReferenceMajorShapeReferenceIdsINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    payload = values.required(2),
                    majorShape = values.required(3),
                    direction = values.required(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef"
        )
    case SubgroupAvcImeGetBorderReachedINTEL extends Definition(
          "OpSubgroupAvcImeGetBorderReachedINTEL",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case SubgroupAvcImeGetTruncatedSearchIndicationINTEL extends Definition(
          "OpSubgroupAvcImeGetTruncatedSearchIndicationINTEL",
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
          "IdResultType IdResult IdRef"
        )
    case SubgroupAvcImeGetUnidirectionalEarlySearchTerminationINTEL extends Definition(
          "OpSubgroupAvcImeGetUnidirectionalEarlySearchTerminationINTEL",
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
          "IdResultType IdResult IdRef"
        )
    case SubgroupAvcImeGetWeightingPatternMinimumMotionVectorINTEL extends Definition(
          "OpSubgroupAvcImeGetWeightingPatternMinimumMotionVectorINTEL",
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
          "IdResultType IdResult IdRef"
        )
    case SubgroupAvcImeGetWeightingPatternMinimumDistortionINTEL extends Definition(
          "OpSubgroupAvcImeGetWeightingPatternMinimumDistortionINTEL",
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
          "IdResultType IdResult IdRef"
        )
    case SubgroupAvcFmeInitializeINTEL extends Definition(
          "OpSubgroupAvcFmeInitializeINTEL",
          SPIRVOpcodeNumber(5781),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcFmeInitializeINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    srcCoord = values.required(2),
                    motionVectors = values.required(3),
                    majorShapes = values.required(4),
                    minorShapes = values.required(5),
                    direction = values.required(6),
                    pixelResolution = values.required(7),
                    sadAdjustment = values.required(8)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef IdRef IdRef IdRef"
        )
    case SubgroupAvcBmeInitializeINTEL extends Definition(
          "OpSubgroupAvcBmeInitializeINTEL",
          SPIRVOpcodeNumber(5782),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcBmeInitializeINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    srcCoord = values.required(2),
                    motionVectors = values.required(3),
                    majorShapes = values.required(4),
                    minorShapes = values.required(5),
                    direction = values.required(6),
                    pixelResolution = values.required(7),
                    bidirectionalWeight = values.required(8),
                    sadAdjustment = values.required(9)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef"
        )
    case SubgroupAvcRefConvertToMcePayloadINTEL extends Definition(
          "OpSubgroupAvcRefConvertToMcePayloadINTEL",
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
          "IdResultType IdResult IdRef"
        )
    case SubgroupAvcRefSetBidirectionalMixDisableINTEL extends Definition(
          "OpSubgroupAvcRefSetBidirectionalMixDisableINTEL",
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
          "IdResultType IdResult IdRef"
        )
    case SubgroupAvcRefSetBilinearFilterEnableINTEL extends Definition(
          "OpSubgroupAvcRefSetBilinearFilterEnableINTEL",
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
          "IdResultType IdResult IdRef"
        )
    case SubgroupAvcRefEvaluateWithSingleReferenceINTEL extends Definition(
          "OpSubgroupAvcRefEvaluateWithSingleReferenceINTEL",
          SPIRVOpcodeNumber(5786),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcRefEvaluateWithSingleReferenceINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    srcImage = values.required(2),
                    refImage = values.required(3),
                    payload = values.required(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef"
        )
    case SubgroupAvcRefEvaluateWithDualReferenceINTEL extends Definition(
          "OpSubgroupAvcRefEvaluateWithDualReferenceINTEL",
          SPIRVOpcodeNumber(5787),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcRefEvaluateWithDualReferenceINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    srcImage = values.required(2),
                    fwdRefImage = values.required(3),
                    bwdRefImage = values.required(4),
                    payload = values.required(5)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef"
        )
    case SubgroupAvcRefEvaluateWithMultiReferenceINTEL extends Definition(
          "OpSubgroupAvcRefEvaluateWithMultiReferenceINTEL",
          SPIRVOpcodeNumber(5788),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcRefEvaluateWithMultiReferenceINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    srcImage = values.required(2),
                    packedReferenceIds = values.required(3),
                    payload = values.required(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef"
        )
    case SubgroupAvcRefEvaluateWithMultiReferenceInterlacedINTEL extends Definition(
          "OpSubgroupAvcRefEvaluateWithMultiReferenceInterlacedINTEL",
          SPIRVOpcodeNumber(5789),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcRefEvaluateWithMultiReferenceInterlacedINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    srcImage = values.required(2),
                    packedReferenceIds = values.required(3),
                    packedReferenceFieldPolarities = values.required(4),
                    payload = values.required(5)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef"
        )
    case SubgroupAvcRefConvertToMceResultINTEL extends Definition(
          "OpSubgroupAvcRefConvertToMceResultINTEL",
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
          "IdResultType IdResult IdRef"
        )
    case SubgroupAvcSicInitializeINTEL extends Definition(
          "OpSubgroupAvcSicInitializeINTEL",
          SPIRVOpcodeNumber(5791),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcSicInitializeINTEL(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case SubgroupAvcSicConfigureSkcINTEL extends Definition(
          "OpSubgroupAvcSicConfigureSkcINTEL",
          SPIRVOpcodeNumber(5792),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcSicConfigureSkcINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    skipBlockPartitionType = values.required(2),
                    skipMotionVectorMask = values.required(3),
                    motionVectors = values.required(4),
                    bidirectionalWeight = values.required(5),
                    sadAdjustment = values.required(6),
                    payload = values.required(7)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef IdRef IdRef"
        )
    case SubgroupAvcSicConfigureIpeLumaINTEL extends Definition(
          "OpSubgroupAvcSicConfigureIpeLumaINTEL",
          SPIRVOpcodeNumber(5793),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcSicConfigureIpeLumaINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    lumaIntraPartitionMask = values.required(2),
                    intraNeighbourAvailabilty = values.required(3),
                    leftEdgeLumaPixels = values.required(4),
                    upperLeftCornerLumaPixel = values.required(5),
                    upperEdgeLumaPixels = values.required(6),
                    upperRightEdgeLumaPixels = values.required(7),
                    sadAdjustment = values.required(8),
                    payload = values.required(9)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef"
        )
    case SubgroupAvcSicConfigureIpeLumaChromaINTEL extends Definition(
          "OpSubgroupAvcSicConfigureIpeLumaChromaINTEL",
          SPIRVOpcodeNumber(5794),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcSicConfigureIpeLumaChromaINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    lumaIntraPartitionMask = values.required(2),
                    intraNeighbourAvailabilty = values.required(3),
                    leftEdgeLumaPixels = values.required(4),
                    upperLeftCornerLumaPixel = values.required(5),
                    upperEdgeLumaPixels = values.required(6),
                    upperRightEdgeLumaPixels = values.required(7),
                    leftEdgeChromaPixels = values.required(8),
                    upperLeftCornerChromaPixel = values.required(9),
                    upperEdgeChromaPixels = values.required(10),
                    sadAdjustment = values.required(11),
                    payload = values.required(12)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef"
        )
    case SubgroupAvcSicGetMotionVectorMaskINTEL extends Definition(
          "OpSubgroupAvcSicGetMotionVectorMaskINTEL",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case SubgroupAvcSicConvertToMcePayloadINTEL extends Definition(
          "OpSubgroupAvcSicConvertToMcePayloadINTEL",
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
          "IdResultType IdResult IdRef"
        )
    case SubgroupAvcSicSetIntraLumaShapePenaltyINTEL extends Definition(
          "OpSubgroupAvcSicSetIntraLumaShapePenaltyINTEL",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case SubgroupAvcSicSetIntraLumaModeCostFunctionINTEL extends Definition(
          "OpSubgroupAvcSicSetIntraLumaModeCostFunctionINTEL",
          SPIRVOpcodeNumber(5798),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcSicSetIntraLumaModeCostFunctionINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    lumaModePenalty = values.required(2),
                    lumaPackedNeighborModes = values.required(3),
                    lumaPackedNonDcPenalty = values.required(4),
                    payload = values.required(5)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef"
        )
    case SubgroupAvcSicSetIntraChromaModeCostFunctionINTEL extends Definition(
          "OpSubgroupAvcSicSetIntraChromaModeCostFunctionINTEL",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case SubgroupAvcSicSetBilinearFilterEnableINTEL extends Definition(
          "OpSubgroupAvcSicSetBilinearFilterEnableINTEL",
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
          "IdResultType IdResult IdRef"
        )
    case SubgroupAvcSicSetSkcForwardTransformEnableINTEL extends Definition(
          "OpSubgroupAvcSicSetSkcForwardTransformEnableINTEL",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case SubgroupAvcSicSetBlockBasedRawSkipSadINTEL extends Definition(
          "OpSubgroupAvcSicSetBlockBasedRawSkipSadINTEL",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case SubgroupAvcSicEvaluateIpeINTEL extends Definition(
          "OpSubgroupAvcSicEvaluateIpeINTEL",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case SubgroupAvcSicEvaluateWithSingleReferenceINTEL extends Definition(
          "OpSubgroupAvcSicEvaluateWithSingleReferenceINTEL",
          SPIRVOpcodeNumber(5804),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcSicEvaluateWithSingleReferenceINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    srcImage = values.required(2),
                    refImage = values.required(3),
                    payload = values.required(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef"
        )
    case SubgroupAvcSicEvaluateWithDualReferenceINTEL extends Definition(
          "OpSubgroupAvcSicEvaluateWithDualReferenceINTEL",
          SPIRVOpcodeNumber(5805),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcSicEvaluateWithDualReferenceINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    srcImage = values.required(2),
                    fwdRefImage = values.required(3),
                    bwdRefImage = values.required(4),
                    payload = values.required(5)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef"
        )
    case SubgroupAvcSicEvaluateWithMultiReferenceINTEL extends Definition(
          "OpSubgroupAvcSicEvaluateWithMultiReferenceINTEL",
          SPIRVOpcodeNumber(5806),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcSicEvaluateWithMultiReferenceINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    srcImage = values.required(2),
                    packedReferenceIds = values.required(3),
                    payload = values.required(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef"
        )
    case SubgroupAvcSicEvaluateWithMultiReferenceInterlacedINTEL extends Definition(
          "OpSubgroupAvcSicEvaluateWithMultiReferenceInterlacedINTEL",
          SPIRVOpcodeNumber(5807),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupAvcSicEvaluateWithMultiReferenceInterlacedINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    srcImage = values.required(2),
                    packedReferenceIds = values.required(3),
                    packedReferenceFieldPolarities = values.required(4),
                    payload = values.required(5)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef"
        )
    case SubgroupAvcSicConvertToMceResultINTEL extends Definition(
          "OpSubgroupAvcSicConvertToMceResultINTEL",
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
          "IdResultType IdResult IdRef"
        )
    case SubgroupAvcSicGetIpeLumaShapeINTEL extends Definition(
          "OpSubgroupAvcSicGetIpeLumaShapeINTEL",
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
          "IdResultType IdResult IdRef"
        )
    case SubgroupAvcSicGetBestIpeLumaDistortionINTEL extends Definition(
          "OpSubgroupAvcSicGetBestIpeLumaDistortionINTEL",
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
          "IdResultType IdResult IdRef"
        )
    case SubgroupAvcSicGetBestIpeChromaDistortionINTEL extends Definition(
          "OpSubgroupAvcSicGetBestIpeChromaDistortionINTEL",
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
          "IdResultType IdResult IdRef"
        )
    case SubgroupAvcSicGetPackedIpeLumaModesINTEL extends Definition(
          "OpSubgroupAvcSicGetPackedIpeLumaModesINTEL",
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
          "IdResultType IdResult IdRef"
        )
    case SubgroupAvcSicGetIpeChromaModeINTEL extends Definition(
          "OpSubgroupAvcSicGetIpeChromaModeINTEL",
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
          "IdResultType IdResult IdRef"
        )
    case SubgroupAvcSicGetPackedSkcLumaCountThresholdINTEL extends Definition(
          "OpSubgroupAvcSicGetPackedSkcLumaCountThresholdINTEL",
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
          "IdResultType IdResult IdRef"
        )
    case SubgroupAvcSicGetPackedSkcLumaSumThresholdINTEL extends Definition(
          "OpSubgroupAvcSicGetPackedSkcLumaSumThresholdINTEL",
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
          "IdResultType IdResult IdRef"
        )
    case SubgroupAvcSicGetInterRawSadsINTEL extends Definition(
          "OpSubgroupAvcSicGetInterRawSadsINTEL",
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
          "IdResultType IdResult IdRef"
        )
    case VariableLengthArrayINTEL extends Definition(
          "OpVariableLengthArrayINTEL",
          SPIRVOpcodeNumber(5818),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.VariableLengthArrayINTEL(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case SaveMemoryINTEL extends Definition(
          "OpSaveMemoryINTEL",
          SPIRVOpcodeNumber(5819),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.SaveMemoryINTEL(values.required(0), values.required(1))
              ),
          "IdResultType IdResult"
        )
    case RestoreMemoryINTEL extends Definition(
          "OpRestoreMemoryINTEL",
          SPIRVOpcodeNumber(5820),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.RestoreMemoryINTEL(values.required(0))
              ),
          "IdRef"
        )
    case ArbitraryFloatSinCosPiALTERA extends Definition(
          "OpArbitraryFloatSinCosPiALTERA",
          SPIRVOpcodeNumber(5840),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatSinCosPiALTERA(
                    resultType = values.required(0),
                    dest = values.required(1),
                    a = values.required(2),
                    ma = values.required(3),
                    mResult = values.required(4),
                    subnormal = values.required(5),
                    rounding = values.required(6),
                    roundingAccuracy = values.required(7)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger LiteralInteger LiteralInteger LiteralInteger LiteralInteger"
        )
    case ArbitraryFloatCastALTERA extends Definition(
          "OpArbitraryFloatCastALTERA",
          SPIRVOpcodeNumber(5841),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatCastALTERA(
                    resultType = values.required(0),
                    dest = values.required(1),
                    a = values.required(2),
                    ma = values.required(3),
                    mresult = values.required(4),
                    subnormal = values.required(5),
                    rounding = values.required(6),
                    accuracy = values.required(7)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger LiteralInteger LiteralInteger LiteralInteger LiteralInteger"
        )
    case ArbitraryFloatCastFromIntALTERA extends Definition(
          "OpArbitraryFloatCastFromIntALTERA",
          SPIRVOpcodeNumber(5842),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatCastFromIntALTERA(
                    resultType = values.required(0),
                    dest = values.required(1),
                    a = values.required(2),
                    mresult = values.required(3),
                    fromSign = values.required(4),
                    subnormal = values.required(5),
                    rounding = values.required(6),
                    accuracy = values.required(7)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger LiteralInteger LiteralInteger LiteralInteger LiteralInteger"
        )
    case ArbitraryFloatCastToIntALTERA extends Definition(
          "OpArbitraryFloatCastToIntALTERA",
          SPIRVOpcodeNumber(5843),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatCastToIntALTERA(
                    resultType = values.required(0),
                    dest = values.required(1),
                    a = values.required(2),
                    ma = values.required(3),
                    toSign = values.required(4),
                    subnormal = values.required(5),
                    rounding = values.required(6),
                    accuracy = values.required(7)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger LiteralInteger LiteralInteger LiteralInteger LiteralInteger"
        )
    case ArbitraryFloatAddALTERA extends Definition(
          "OpArbitraryFloatAddALTERA",
          SPIRVOpcodeNumber(5846),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatAddALTERA(
                    resultType = values.required(0),
                    dest = values.required(1),
                    a = values.required(2),
                    ma = values.required(3),
                    b = values.required(4),
                    mb = values.required(5),
                    mResult = values.required(6),
                    subnormal = values.required(7),
                    rounding = values.required(8),
                    accuracy = values.required(9)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger IdRef LiteralInteger LiteralInteger LiteralInteger LiteralInteger LiteralInteger"
        )
    case ArbitraryFloatSubALTERA extends Definition(
          "OpArbitraryFloatSubALTERA",
          SPIRVOpcodeNumber(5847),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatSubALTERA(
                    resultType = values.required(0),
                    dest = values.required(1),
                    a = values.required(2),
                    ma = values.required(3),
                    b = values.required(4),
                    mb = values.required(5),
                    mresult = values.required(6),
                    subnormal = values.required(7),
                    rounding = values.required(8),
                    accuracy = values.required(9)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger IdRef LiteralInteger LiteralInteger LiteralInteger LiteralInteger LiteralInteger"
        )
    case ArbitraryFloatMulALTERA extends Definition(
          "OpArbitraryFloatMulALTERA",
          SPIRVOpcodeNumber(5848),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatMulALTERA(
                    resultType = values.required(0),
                    dest = values.required(1),
                    a = values.required(2),
                    ma = values.required(3),
                    b = values.required(4),
                    mb = values.required(5),
                    mresult = values.required(6),
                    subnormal = values.required(7),
                    rounding = values.required(8),
                    accuracy = values.required(9)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger IdRef LiteralInteger LiteralInteger LiteralInteger LiteralInteger LiteralInteger"
        )
    case ArbitraryFloatDivALTERA extends Definition(
          "OpArbitraryFloatDivALTERA",
          SPIRVOpcodeNumber(5849),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatDivALTERA(
                    resultType = values.required(0),
                    dest = values.required(1),
                    a = values.required(2),
                    ma = values.required(3),
                    b = values.required(4),
                    mb = values.required(5),
                    mresult = values.required(6),
                    subnormal = values.required(7),
                    rounding = values.required(8),
                    accuracy = values.required(9)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger IdRef LiteralInteger LiteralInteger LiteralInteger LiteralInteger LiteralInteger"
        )
    case ArbitraryFloatGTALTERA extends Definition(
          "OpArbitraryFloatGTALTERA",
          SPIRVOpcodeNumber(5850),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatGTALTERA(
                    resultType = values.required(0),
                    dest = values.required(1),
                    a = values.required(2),
                    ma = values.required(3),
                    b = values.required(4),
                    mb = values.required(5)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger IdRef LiteralInteger"
        )
    case ArbitraryFloatGEALTERA extends Definition(
          "OpArbitraryFloatGEALTERA",
          SPIRVOpcodeNumber(5851),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatGEALTERA(
                    resultType = values.required(0),
                    dest = values.required(1),
                    a = values.required(2),
                    ma = values.required(3),
                    b = values.required(4),
                    mb = values.required(5)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger IdRef LiteralInteger"
        )
    case ArbitraryFloatLTALTERA extends Definition(
          "OpArbitraryFloatLTALTERA",
          SPIRVOpcodeNumber(5852),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatLTALTERA(
                    resultType = values.required(0),
                    dest = values.required(1),
                    a = values.required(2),
                    ma = values.required(3),
                    b = values.required(4),
                    mb = values.required(5)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger IdRef LiteralInteger"
        )
    case ArbitraryFloatLEALTERA extends Definition(
          "OpArbitraryFloatLEALTERA",
          SPIRVOpcodeNumber(5853),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatLEALTERA(
                    resultType = values.required(0),
                    dest = values.required(1),
                    a = values.required(2),
                    ma = values.required(3),
                    b = values.required(4),
                    mb = values.required(5)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger IdRef LiteralInteger"
        )
    case ArbitraryFloatEQALTERA extends Definition(
          "OpArbitraryFloatEQALTERA",
          SPIRVOpcodeNumber(5854),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatEQALTERA(
                    resultType = values.required(0),
                    dest = values.required(1),
                    a = values.required(2),
                    ma = values.required(3),
                    b = values.required(4),
                    mb = values.required(5)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger IdRef LiteralInteger"
        )
    case ArbitraryFloatRecipALTERA extends Definition(
          "OpArbitraryFloatRecipALTERA",
          SPIRVOpcodeNumber(5855),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatRecipALTERA(
                    resultType = values.required(0),
                    dest = values.required(1),
                    a = values.required(2),
                    ma = values.required(3),
                    mresult = values.required(4),
                    subnormal = values.required(5),
                    rounding = values.required(6),
                    accuracy = values.required(7)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger LiteralInteger LiteralInteger LiteralInteger LiteralInteger"
        )
    case ArbitraryFloatRSqrtALTERA extends Definition(
          "OpArbitraryFloatRSqrtALTERA",
          SPIRVOpcodeNumber(5856),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatRSqrtALTERA(
                    resultType = values.required(0),
                    dest = values.required(1),
                    a = values.required(2),
                    ma = values.required(3),
                    mresult = values.required(4),
                    subnormal = values.required(5),
                    rounding = values.required(6),
                    accuracy = values.required(7)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger LiteralInteger LiteralInteger LiteralInteger LiteralInteger"
        )
    case ArbitraryFloatCbrtALTERA extends Definition(
          "OpArbitraryFloatCbrtALTERA",
          SPIRVOpcodeNumber(5857),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatCbrtALTERA(
                    resultType = values.required(0),
                    dest = values.required(1),
                    a = values.required(2),
                    ma = values.required(3),
                    mresult = values.required(4),
                    subnormal = values.required(5),
                    rounding = values.required(6),
                    accuracy = values.required(7)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger LiteralInteger LiteralInteger LiteralInteger LiteralInteger"
        )
    case ArbitraryFloatHypotALTERA extends Definition(
          "OpArbitraryFloatHypotALTERA",
          SPIRVOpcodeNumber(5858),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatHypotALTERA(
                    resultType = values.required(0),
                    dest = values.required(1),
                    a = values.required(2),
                    ma = values.required(3),
                    b = values.required(4),
                    mb = values.required(5),
                    mresult = values.required(6),
                    subnormal = values.required(7),
                    rounding = values.required(8),
                    accuracy = values.required(9)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger IdRef LiteralInteger LiteralInteger LiteralInteger LiteralInteger LiteralInteger"
        )
    case ArbitraryFloatSqrtALTERA extends Definition(
          "OpArbitraryFloatSqrtALTERA",
          SPIRVOpcodeNumber(5859),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatSqrtALTERA(
                    resultType = values.required(0),
                    dest = values.required(1),
                    a = values.required(2),
                    ma = values.required(3),
                    mresult = values.required(4),
                    subnormal = values.required(5),
                    rounding = values.required(6),
                    accuracy = values.required(7)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger LiteralInteger LiteralInteger LiteralInteger LiteralInteger"
        )
    case ArbitraryFloatLogINTEL extends Definition(
          "OpArbitraryFloatLogINTEL",
          SPIRVOpcodeNumber(5860),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatLogINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    a = values.required(2),
                    ma = values.required(3),
                    mresult = values.required(4),
                    subnormal = values.required(5),
                    rounding = values.required(6),
                    accuracy = values.required(7)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger LiteralInteger LiteralInteger LiteralInteger LiteralInteger"
        )
    case ArbitraryFloatLog2INTEL extends Definition(
          "OpArbitraryFloatLog2INTEL",
          SPIRVOpcodeNumber(5861),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatLog2INTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    a = values.required(2),
                    ma = values.required(3),
                    mresult = values.required(4),
                    subnormal = values.required(5),
                    rounding = values.required(6),
                    accuracy = values.required(7)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger LiteralInteger LiteralInteger LiteralInteger LiteralInteger"
        )
    case ArbitraryFloatLog10INTEL extends Definition(
          "OpArbitraryFloatLog10INTEL",
          SPIRVOpcodeNumber(5862),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatLog10INTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    a = values.required(2),
                    ma = values.required(3),
                    mresult = values.required(4),
                    subnormal = values.required(5),
                    rounding = values.required(6),
                    accuracy = values.required(7)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger LiteralInteger LiteralInteger LiteralInteger LiteralInteger"
        )
    case ArbitraryFloatLog1pINTEL extends Definition(
          "OpArbitraryFloatLog1pINTEL",
          SPIRVOpcodeNumber(5863),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatLog1pINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    a = values.required(2),
                    ma = values.required(3),
                    mresult = values.required(4),
                    subnormal = values.required(5),
                    rounding = values.required(6),
                    accuracy = values.required(7)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger LiteralInteger LiteralInteger LiteralInteger LiteralInteger"
        )
    case ArbitraryFloatExpINTEL extends Definition(
          "OpArbitraryFloatExpINTEL",
          SPIRVOpcodeNumber(5864),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatExpINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    a = values.required(2),
                    ma = values.required(3),
                    mresult = values.required(4),
                    subnormal = values.required(5),
                    rounding = values.required(6),
                    accuracy = values.required(7)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger LiteralInteger LiteralInteger LiteralInteger LiteralInteger"
        )
    case ArbitraryFloatExp2INTEL extends Definition(
          "OpArbitraryFloatExp2INTEL",
          SPIRVOpcodeNumber(5865),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatExp2INTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    a = values.required(2),
                    ma = values.required(3),
                    mresult = values.required(4),
                    subnormal = values.required(5),
                    rounding = values.required(6),
                    accuracy = values.required(7)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger LiteralInteger LiteralInteger LiteralInteger LiteralInteger"
        )
    case ArbitraryFloatExp10INTEL extends Definition(
          "OpArbitraryFloatExp10INTEL",
          SPIRVOpcodeNumber(5866),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatExp10INTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    a = values.required(2),
                    ma = values.required(3),
                    mresult = values.required(4),
                    subnormal = values.required(5),
                    rounding = values.required(6),
                    accuracy = values.required(7)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger LiteralInteger LiteralInteger LiteralInteger LiteralInteger"
        )
    case ArbitraryFloatExpm1INTEL extends Definition(
          "OpArbitraryFloatExpm1INTEL",
          SPIRVOpcodeNumber(5867),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatExpm1INTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    a = values.required(2),
                    ma = values.required(3),
                    mresult = values.required(4),
                    subnormal = values.required(5),
                    rounding = values.required(6),
                    accuracy = values.required(7)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger LiteralInteger LiteralInteger LiteralInteger LiteralInteger"
        )
    case ArbitraryFloatSinINTEL extends Definition(
          "OpArbitraryFloatSinINTEL",
          SPIRVOpcodeNumber(5868),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatSinINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    a = values.required(2),
                    ma = values.required(3),
                    mresult = values.required(4),
                    subnormal = values.required(5),
                    rounding = values.required(6),
                    accuracy = values.required(7)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger LiteralInteger LiteralInteger LiteralInteger LiteralInteger"
        )
    case ArbitraryFloatCosINTEL extends Definition(
          "OpArbitraryFloatCosINTEL",
          SPIRVOpcodeNumber(5869),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatCosINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    a = values.required(2),
                    ma = values.required(3),
                    mresult = values.required(4),
                    subnormal = values.required(5),
                    rounding = values.required(6),
                    accuracy = values.required(7)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger LiteralInteger LiteralInteger LiteralInteger LiteralInteger"
        )
    case ArbitraryFloatSinCosINTEL extends Definition(
          "OpArbitraryFloatSinCosINTEL",
          SPIRVOpcodeNumber(5870),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatSinCosINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    a = values.required(2),
                    ma = values.required(3),
                    mresult = values.required(4),
                    subnormal = values.required(5),
                    rounding = values.required(6),
                    accuracy = values.required(7)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger LiteralInteger LiteralInteger LiteralInteger LiteralInteger"
        )
    case ArbitraryFloatSinPiINTEL extends Definition(
          "OpArbitraryFloatSinPiINTEL",
          SPIRVOpcodeNumber(5871),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatSinPiINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    a = values.required(2),
                    ma = values.required(3),
                    mresult = values.required(4),
                    subnormal = values.required(5),
                    rounding = values.required(6),
                    accuracy = values.required(7)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger LiteralInteger LiteralInteger LiteralInteger LiteralInteger"
        )
    case ArbitraryFloatCosPiINTEL extends Definition(
          "OpArbitraryFloatCosPiINTEL",
          SPIRVOpcodeNumber(5872),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatCosPiINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    a = values.required(2),
                    ma = values.required(3),
                    mresult = values.required(4),
                    subnormal = values.required(5),
                    rounding = values.required(6),
                    accuracy = values.required(7)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger LiteralInteger LiteralInteger LiteralInteger LiteralInteger"
        )
    case ArbitraryFloatASinINTEL extends Definition(
          "OpArbitraryFloatASinINTEL",
          SPIRVOpcodeNumber(5873),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatASinINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    a = values.required(2),
                    ma = values.required(3),
                    mresult = values.required(4),
                    subnormal = values.required(5),
                    rounding = values.required(6),
                    accuracy = values.required(7)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger LiteralInteger LiteralInteger LiteralInteger LiteralInteger"
        )
    case ArbitraryFloatASinPiINTEL extends Definition(
          "OpArbitraryFloatASinPiINTEL",
          SPIRVOpcodeNumber(5874),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatASinPiINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    a = values.required(2),
                    ma = values.required(3),
                    mresult = values.required(4),
                    subnormal = values.required(5),
                    rounding = values.required(6),
                    accuracy = values.required(7)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger LiteralInteger LiteralInteger LiteralInteger LiteralInteger"
        )
    case ArbitraryFloatACosINTEL extends Definition(
          "OpArbitraryFloatACosINTEL",
          SPIRVOpcodeNumber(5875),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatACosINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    a = values.required(2),
                    m1 = values.required(3),
                    mout = values.required(4),
                    enableSubnormals = values.required(5),
                    roundingMode = values.required(6),
                    roundingAccuracy = values.required(7)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger LiteralInteger LiteralInteger LiteralInteger LiteralInteger"
        )
    case ArbitraryFloatACosPiINTEL extends Definition(
          "OpArbitraryFloatACosPiINTEL",
          SPIRVOpcodeNumber(5876),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatACosPiINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    a = values.required(2),
                    ma = values.required(3),
                    mresult = values.required(4),
                    subnormal = values.required(5),
                    rounding = values.required(6),
                    accuracy = values.required(7)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger LiteralInteger LiteralInteger LiteralInteger LiteralInteger"
        )
    case ArbitraryFloatATanINTEL extends Definition(
          "OpArbitraryFloatATanINTEL",
          SPIRVOpcodeNumber(5877),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatATanINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    a = values.required(2),
                    ma = values.required(3),
                    mresult = values.required(4),
                    subnormal = values.required(5),
                    rounding = values.required(6),
                    accuracy = values.required(7)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger LiteralInteger LiteralInteger LiteralInteger LiteralInteger"
        )
    case ArbitraryFloatATanPiINTEL extends Definition(
          "OpArbitraryFloatATanPiINTEL",
          SPIRVOpcodeNumber(5878),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatATanPiINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    a = values.required(2),
                    ma = values.required(3),
                    mresult = values.required(4),
                    subnormal = values.required(5),
                    rounding = values.required(6),
                    accuracy = values.required(7)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger LiteralInteger LiteralInteger LiteralInteger LiteralInteger"
        )
    case ArbitraryFloatATan2INTEL extends Definition(
          "OpArbitraryFloatATan2INTEL",
          SPIRVOpcodeNumber(5879),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatATan2INTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    a = values.required(2),
                    ma = values.required(3),
                    b = values.required(4),
                    mb = values.required(5),
                    mresult = values.required(6),
                    subnormal = values.required(7),
                    rounding = values.required(8),
                    accuracy = values.required(9)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger IdRef LiteralInteger LiteralInteger LiteralInteger LiteralInteger LiteralInteger"
        )
    case ArbitraryFloatPowINTEL extends Definition(
          "OpArbitraryFloatPowINTEL",
          SPIRVOpcodeNumber(5880),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatPowINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    a = values.required(2),
                    ma = values.required(3),
                    b = values.required(4),
                    mb = values.required(5),
                    mresult = values.required(6),
                    subnormal = values.required(7),
                    rounding = values.required(8),
                    accuracy = values.required(9)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger IdRef LiteralInteger LiteralInteger LiteralInteger LiteralInteger LiteralInteger"
        )
    case ArbitraryFloatPowRINTEL extends Definition(
          "OpArbitraryFloatPowRINTEL",
          SPIRVOpcodeNumber(5881),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatPowRINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    a = values.required(2),
                    ma = values.required(3),
                    b = values.required(4),
                    mb = values.required(5),
                    mresult = values.required(6),
                    subnormal = values.required(7),
                    rounding = values.required(8),
                    accuracy = values.required(9)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger IdRef LiteralInteger LiteralInteger LiteralInteger LiteralInteger LiteralInteger"
        )
    case ArbitraryFloatPowNINTEL extends Definition(
          "OpArbitraryFloatPowNINTEL",
          SPIRVOpcodeNumber(5882),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ArbitraryFloatPowNINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    a = values.required(2),
                    ma = values.required(3),
                    b = values.required(4),
                    signOfB = values.required(5),
                    mresult = values.required(6),
                    subnormal = values.required(7),
                    rounding = values.required(8),
                    accuracy = values.required(9)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger IdRef LiteralInteger LiteralInteger LiteralInteger LiteralInteger LiteralInteger"
        )
    case LoopControlINTEL extends Definition(
          "OpLoopControlINTEL",
          SPIRVOpcodeNumber(5887),
          definition =>
            raw =>
              Instruction.decodeDefinition(definition, raw, values => Instruction.LoopControlINTEL(values.repeated(0))),
          "LiteralInteger*"
        )
    case AliasDomainDeclINTEL extends Definition(
          "OpAliasDomainDeclINTEL",
          SPIRVOpcodeNumber(5911),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.AliasDomainDeclINTEL(values.required(0), values.optional(1))
              ),
          "IdResult IdRef?"
        )
    case AliasScopeDeclINTEL extends Definition(
          "OpAliasScopeDeclINTEL",
          SPIRVOpcodeNumber(5912),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.AliasScopeDeclINTEL(values.required(0), values.required(1), values.optional(2))
              ),
          "IdResult IdRef IdRef?"
        )
    case AliasScopeListDeclINTEL extends Definition(
          "OpAliasScopeListDeclINTEL",
          SPIRVOpcodeNumber(5913),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.AliasScopeListDeclINTEL(values.required(0), values.repeated(1))
              ),
          "IdResult IdRef*"
        )
    case FixedSqrtALTERA extends Definition(
          "OpFixedSqrtALTERA",
          SPIRVOpcodeNumber(5923),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FixedSqrtALTERA(
                    resultType = values.required(0),
                    dest = values.required(1),
                    input = values.required(2),
                    s = values.required(3),
                    i = values.required(4),
                    rI = values.required(5),
                    q = values.required(6),
                    o = values.required(7)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger LiteralInteger LiteralInteger LiteralInteger LiteralInteger"
        )
    case FixedRecipALTERA extends Definition(
          "OpFixedRecipALTERA",
          SPIRVOpcodeNumber(5924),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FixedRecipALTERA(
                    resultType = values.required(0),
                    dest = values.required(1),
                    input = values.required(2),
                    s = values.required(3),
                    i = values.required(4),
                    rI = values.required(5),
                    q = values.required(6),
                    o = values.required(7)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger LiteralInteger LiteralInteger LiteralInteger LiteralInteger"
        )
    case FixedRsqrtALTERA extends Definition(
          "OpFixedRsqrtALTERA",
          SPIRVOpcodeNumber(5925),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FixedRsqrtALTERA(
                    resultType = values.required(0),
                    dest = values.required(1),
                    input = values.required(2),
                    s = values.required(3),
                    i = values.required(4),
                    rI = values.required(5),
                    q = values.required(6),
                    o = values.required(7)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger LiteralInteger LiteralInteger LiteralInteger LiteralInteger"
        )
    case FixedSinALTERA extends Definition(
          "OpFixedSinALTERA",
          SPIRVOpcodeNumber(5926),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FixedSinALTERA(
                    resultType = values.required(0),
                    dest = values.required(1),
                    input = values.required(2),
                    s = values.required(3),
                    i = values.required(4),
                    rI = values.required(5),
                    q = values.required(6),
                    o = values.required(7)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger LiteralInteger LiteralInteger LiteralInteger LiteralInteger"
        )
    case FixedCosALTERA extends Definition(
          "OpFixedCosALTERA",
          SPIRVOpcodeNumber(5927),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FixedCosALTERA(
                    resultType = values.required(0),
                    dest = values.required(1),
                    input = values.required(2),
                    s = values.required(3),
                    i = values.required(4),
                    rI = values.required(5),
                    q = values.required(6),
                    o = values.required(7)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger LiteralInteger LiteralInteger LiteralInteger LiteralInteger"
        )
    case FixedSinCosALTERA extends Definition(
          "OpFixedSinCosALTERA",
          SPIRVOpcodeNumber(5928),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FixedSinCosALTERA(
                    resultType = values.required(0),
                    dest = values.required(1),
                    input = values.required(2),
                    s = values.required(3),
                    i = values.required(4),
                    rI = values.required(5),
                    q = values.required(6),
                    o = values.required(7)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger LiteralInteger LiteralInteger LiteralInteger LiteralInteger"
        )
    case FixedSinPiALTERA extends Definition(
          "OpFixedSinPiALTERA",
          SPIRVOpcodeNumber(5929),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FixedSinPiALTERA(
                    resultType = values.required(0),
                    dest = values.required(1),
                    input = values.required(2),
                    s = values.required(3),
                    i = values.required(4),
                    rI = values.required(5),
                    q = values.required(6),
                    o = values.required(7)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger LiteralInteger LiteralInteger LiteralInteger LiteralInteger"
        )
    case FixedCosPiALTERA extends Definition(
          "OpFixedCosPiALTERA",
          SPIRVOpcodeNumber(5930),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FixedCosPiALTERA(
                    resultType = values.required(0),
                    dest = values.required(1),
                    input = values.required(2),
                    s = values.required(3),
                    i = values.required(4),
                    rI = values.required(5),
                    q = values.required(6),
                    o = values.required(7)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger LiteralInteger LiteralInteger LiteralInteger LiteralInteger"
        )
    case FixedSinCosPiALTERA extends Definition(
          "OpFixedSinCosPiALTERA",
          SPIRVOpcodeNumber(5931),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FixedSinCosPiALTERA(
                    resultType = values.required(0),
                    dest = values.required(1),
                    input = values.required(2),
                    s = values.required(3),
                    i = values.required(4),
                    rI = values.required(5),
                    q = values.required(6),
                    o = values.required(7)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger LiteralInteger LiteralInteger LiteralInteger LiteralInteger"
        )
    case FixedLogALTERA extends Definition(
          "OpFixedLogALTERA",
          SPIRVOpcodeNumber(5932),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FixedLogALTERA(
                    resultType = values.required(0),
                    dest = values.required(1),
                    input = values.required(2),
                    s = values.required(3),
                    i = values.required(4),
                    rI = values.required(5),
                    q = values.required(6),
                    o = values.required(7)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger LiteralInteger LiteralInteger LiteralInteger LiteralInteger"
        )
    case FixedExpALTERA extends Definition(
          "OpFixedExpALTERA",
          SPIRVOpcodeNumber(5933),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FixedExpALTERA(
                    resultType = values.required(0),
                    dest = values.required(1),
                    input = values.required(2),
                    s = values.required(3),
                    i = values.required(4),
                    rI = values.required(5),
                    q = values.required(6),
                    o = values.required(7)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger LiteralInteger LiteralInteger LiteralInteger LiteralInteger"
        )
    case PtrCastToCrossWorkgroupALTERA extends Definition(
          "OpPtrCastToCrossWorkgroupALTERA",
          SPIRVOpcodeNumber(5934),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.PtrCastToCrossWorkgroupALTERA(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case CrossWorkgroupCastToPtrALTERA extends Definition(
          "OpCrossWorkgroupCastToPtrALTERA",
          SPIRVOpcodeNumber(5938),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.CrossWorkgroupCastToPtrALTERA(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case ReadPipeBlockingALTERA extends Definition(
          "OpReadPipeBlockingALTERA",
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
          "IdRef IdRef IdRef IdRef"
        )
    case WritePipeBlockingALTERA extends Definition(
          "OpWritePipeBlockingALTERA",
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
          "IdRef IdRef IdRef IdRef"
        )
    case FPGARegALTERA extends Definition(
          "OpFPGARegALTERA",
          SPIRVOpcodeNumber(5949),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.FPGARegALTERA(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case RayQueryGetRayTMinKHR extends Definition(
          "OpRayQueryGetRayTMinKHR",
          SPIRVOpcodeNumber(6016),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.RayQueryGetRayTMinKHR(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case RayQueryGetRayFlagsKHR extends Definition(
          "OpRayQueryGetRayFlagsKHR",
          SPIRVOpcodeNumber(6017),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.RayQueryGetRayFlagsKHR(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case RayQueryGetIntersectionTKHR extends Definition(
          "OpRayQueryGetIntersectionTKHR",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case RayQueryGetIntersectionInstanceCustomIndexKHR extends Definition(
          "OpRayQueryGetIntersectionInstanceCustomIndexKHR",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case RayQueryGetIntersectionInstanceIdKHR extends Definition(
          "OpRayQueryGetIntersectionInstanceIdKHR",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case RayQueryGetIntersectionInstanceShaderBindingTableRecordOffsetKHR extends Definition(
          "OpRayQueryGetIntersectionInstanceShaderBindingTableRecordOffsetKHR",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case RayQueryGetIntersectionGeometryIndexKHR extends Definition(
          "OpRayQueryGetIntersectionGeometryIndexKHR",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case RayQueryGetIntersectionPrimitiveIndexKHR extends Definition(
          "OpRayQueryGetIntersectionPrimitiveIndexKHR",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case RayQueryGetIntersectionBarycentricsKHR extends Definition(
          "OpRayQueryGetIntersectionBarycentricsKHR",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case RayQueryGetIntersectionFrontFaceKHR extends Definition(
          "OpRayQueryGetIntersectionFrontFaceKHR",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case RayQueryGetIntersectionCandidateAABBOpaqueKHR extends Definition(
          "OpRayQueryGetIntersectionCandidateAABBOpaqueKHR",
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
          "IdResultType IdResult IdRef"
        )
    case RayQueryGetIntersectionObjectRayDirectionKHR extends Definition(
          "OpRayQueryGetIntersectionObjectRayDirectionKHR",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case RayQueryGetIntersectionObjectRayOriginKHR extends Definition(
          "OpRayQueryGetIntersectionObjectRayOriginKHR",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case RayQueryGetWorldRayDirectionKHR extends Definition(
          "OpRayQueryGetWorldRayDirectionKHR",
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
          "IdResultType IdResult IdRef"
        )
    case RayQueryGetWorldRayOriginKHR extends Definition(
          "OpRayQueryGetWorldRayOriginKHR",
          SPIRVOpcodeNumber(6030),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.RayQueryGetWorldRayOriginKHR(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case RayQueryGetIntersectionObjectToWorldKHR extends Definition(
          "OpRayQueryGetIntersectionObjectToWorldKHR",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case RayQueryGetIntersectionWorldToObjectKHR extends Definition(
          "OpRayQueryGetIntersectionWorldToObjectKHR",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case AtomicFAddEXT extends Definition(
          "OpAtomicFAddEXT",
          SPIRVOpcodeNumber(6035),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.AtomicFAddEXT(
                    resultType = values.required(0),
                    dest = values.required(1),
                    pointer = values.required(2),
                    memory = values.required(3),
                    semantics = values.required(4),
                    value = values.required(5)
                  )
              ),
          "IdResultType IdResult IdRef IdScope IdMemorySemantics IdRef"
        )
    case TypeBufferSurfaceINTEL extends Definition(
          "OpTypeBufferSurfaceINTEL",
          SPIRVOpcodeNumber(6086),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeBufferSurfaceINTEL(values.required(0), values.required(1))
              ),
          "IdResult AccessQualifier"
        )
    case TypeStructContinuedINTEL extends Definition(
          "OpTypeStructContinuedINTEL",
          SPIRVOpcodeNumber(6090),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeStructContinuedINTEL(values.repeated(0))
              ),
          "IdRef*"
        )
    case ConstantCompositeContinuedINTEL extends Definition(
          "OpConstantCompositeContinuedINTEL",
          SPIRVOpcodeNumber(6091),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ConstantCompositeContinuedINTEL(values.repeated(0))
              ),
          "IdRef*"
        )
    case SpecConstantCompositeContinuedINTEL extends Definition(
          "OpSpecConstantCompositeContinuedINTEL",
          SPIRVOpcodeNumber(6092),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.SpecConstantCompositeContinuedINTEL(values.repeated(0))
              ),
          "IdRef*"
        )
    case CompositeConstructContinuedINTEL extends Definition(
          "OpCompositeConstructContinuedINTEL",
          SPIRVOpcodeNumber(6096),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.CompositeConstructContinuedINTEL(values.repeated(0))
              ),
          "IdRef*"
        )
    case ConvertFToBF16INTEL extends Definition(
          "OpConvertFToBF16INTEL",
          SPIRVOpcodeNumber(6116),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ConvertFToBF16INTEL(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case ConvertBF16ToFINTEL extends Definition(
          "OpConvertBF16ToFINTEL",
          SPIRVOpcodeNumber(6117),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ConvertBF16ToFINTEL(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case ControlBarrierArriveEXT extends Definition(
          "OpControlBarrierArriveEXT",
          SPIRVOpcodeNumber(6142),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ControlBarrierArriveEXT(values.required(0), values.required(1), values.required(2))
              ),
          "IdScope IdScope IdMemorySemantics"
        )
    case ControlBarrierWaitEXT extends Definition(
          "OpControlBarrierWaitEXT",
          SPIRVOpcodeNumber(6143),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ControlBarrierWaitEXT(values.required(0), values.required(1), values.required(2))
              ),
          "IdScope IdScope IdMemorySemantics"
        )
    case ArithmeticFenceEXT extends Definition(
          "OpArithmeticFenceEXT",
          SPIRVOpcodeNumber(6145),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ArithmeticFenceEXT(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case TaskSequenceCreateALTERA extends Definition(
          "OpTaskSequenceCreateALTERA",
          SPIRVOpcodeNumber(6163),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.TaskSequenceCreateALTERA(
                    resultType = values.required(0),
                    dest = values.required(1),
                    function = values.required(2),
                    pipelined = values.required(3),
                    useStallEnableClusters = values.required(4),
                    getCapacity = values.required(5),
                    asyncCapacity = values.required(6)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger LiteralInteger LiteralInteger LiteralInteger"
        )
    case TaskSequenceAsyncALTERA extends Definition(
          "OpTaskSequenceAsyncALTERA",
          SPIRVOpcodeNumber(6164),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TaskSequenceAsyncALTERA(values.required(0), values.repeated(1))
              ),
          "IdRef IdRef*"
        )
    case TaskSequenceGetALTERA extends Definition(
          "OpTaskSequenceGetALTERA",
          SPIRVOpcodeNumber(6165),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TaskSequenceGetALTERA(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case TaskSequenceReleaseALTERA extends Definition(
          "OpTaskSequenceReleaseALTERA",
          SPIRVOpcodeNumber(6166),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TaskSequenceReleaseALTERA(values.required(0))
              ),
          "IdRef"
        )
    case TypeTaskSequenceALTERA extends Definition(
          "OpTypeTaskSequenceALTERA",
          SPIRVOpcodeNumber(6199),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.TypeTaskSequenceALTERA(values.required(0))
              ),
          "IdResult"
        )
    case SubgroupBlockPrefetchINTEL extends Definition(
          "OpSubgroupBlockPrefetchINTEL",
          SPIRVOpcodeNumber(6221),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupBlockPrefetchINTEL(values.required(0), values.required(1), values.optional(2))
              ),
          "IdRef IdRef MemoryAccess?"
        )
    case Subgroup2DBlockLoadINTEL extends Definition(
          "OpSubgroup2DBlockLoadINTEL",
          SPIRVOpcodeNumber(6231),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.Subgroup2DBlockLoadINTEL(
                    elementSize = values.required(0),
                    blockWidth = values.required(1),
                    blockHeight = values.required(2),
                    blockCount = values.required(3),
                    srcBasePointer = values.required(4),
                    memoryWidth = values.required(5),
                    memoryHeight = values.required(6),
                    memoryPitch = values.required(7),
                    coordinate = values.required(8),
                    dstPointer = values.required(9)
                  )
              ),
          "IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef"
        )
    case Subgroup2DBlockLoadTransformINTEL extends Definition(
          "OpSubgroup2DBlockLoadTransformINTEL",
          SPIRVOpcodeNumber(6232),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.Subgroup2DBlockLoadTransformINTEL(
                    elementSize = values.required(0),
                    blockWidth = values.required(1),
                    blockHeight = values.required(2),
                    blockCount = values.required(3),
                    srcBasePointer = values.required(4),
                    memoryWidth = values.required(5),
                    memoryHeight = values.required(6),
                    memoryPitch = values.required(7),
                    coordinate = values.required(8),
                    dstPointer = values.required(9)
                  )
              ),
          "IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef"
        )
    case Subgroup2DBlockLoadTransposeINTEL extends Definition(
          "OpSubgroup2DBlockLoadTransposeINTEL",
          SPIRVOpcodeNumber(6233),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.Subgroup2DBlockLoadTransposeINTEL(
                    elementSize = values.required(0),
                    blockWidth = values.required(1),
                    blockHeight = values.required(2),
                    blockCount = values.required(3),
                    srcBasePointer = values.required(4),
                    memoryWidth = values.required(5),
                    memoryHeight = values.required(6),
                    memoryPitch = values.required(7),
                    coordinate = values.required(8),
                    dstPointer = values.required(9)
                  )
              ),
          "IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef"
        )
    case Subgroup2DBlockPrefetchINTEL extends Definition(
          "OpSubgroup2DBlockPrefetchINTEL",
          SPIRVOpcodeNumber(6234),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.Subgroup2DBlockPrefetchINTEL(
                    elementSize = values.required(0),
                    blockWidth = values.required(1),
                    blockHeight = values.required(2),
                    blockCount = values.required(3),
                    srcBasePointer = values.required(4),
                    memoryWidth = values.required(5),
                    memoryHeight = values.required(6),
                    memoryPitch = values.required(7),
                    coordinate = values.required(8)
                  )
              ),
          "IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef"
        )
    case Subgroup2DBlockStoreINTEL extends Definition(
          "OpSubgroup2DBlockStoreINTEL",
          SPIRVOpcodeNumber(6235),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.Subgroup2DBlockStoreINTEL(
                    elementSize = values.required(0),
                    blockWidth = values.required(1),
                    blockHeight = values.required(2),
                    blockCount = values.required(3),
                    srcPointer = values.required(4),
                    dstBasePointer = values.required(5),
                    memoryWidth = values.required(6),
                    memoryHeight = values.required(7),
                    memoryPitch = values.required(8),
                    coordinate = values.required(9)
                  )
              ),
          "IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef IdRef"
        )
    case SubgroupMatrixMultiplyAccumulateINTEL extends Definition(
          "OpSubgroupMatrixMultiplyAccumulateINTEL",
          SPIRVOpcodeNumber(6237),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SubgroupMatrixMultiplyAccumulateINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    kDim = values.required(2),
                    matrixA = values.required(3),
                    matrixB = values.required(4),
                    matrixC = values.required(5),
                    matrixMultiplyAccumulateOperands = values.optional(6)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef MatrixMultiplyAccumulateOperands?"
        )
    case BitwiseFunctionINTEL extends Definition(
          "OpBitwiseFunctionINTEL",
          SPIRVOpcodeNumber(6242),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.BitwiseFunctionINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    a = values.required(2),
                    b = values.required(3),
                    c = values.required(4),
                    lUTIndex = values.required(5)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef IdRef"
        )
    case UntypedVariableLengthArrayINTEL extends Definition(
          "OpUntypedVariableLengthArrayINTEL",
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
          "IdResultType IdResult IdRef IdRef"
        )
    case ConditionalExtensionINTEL extends Definition(
          "OpConditionalExtensionINTEL",
          SPIRVOpcodeNumber(6248),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ConditionalExtensionINTEL(values.required(0), values.required(1))
              ),
          "IdRef LiteralString"
        )
    case ConditionalEntryPointINTEL extends Definition(
          "OpConditionalEntryPointINTEL",
          SPIRVOpcodeNumber(6249),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ConditionalEntryPointINTEL(
                    pred = values.required(0),
                    executionModel = values.required(1),
                    entryPoint = values.required(2),
                    name = values.required(3),
                    interface = values.repeated(4)
                  )
              ),
          "IdRef ExecutionModel IdRef LiteralString IdRef*"
        )
    case ConditionalCapabilityINTEL extends Definition(
          "OpConditionalCapabilityINTEL",
          SPIRVOpcodeNumber(6250),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.ConditionalCapabilityINTEL(values.required(0), values.required(1))
              ),
          "IdRef Capability"
        )
    case SpecConstantTargetINTEL extends Definition(
          "OpSpecConstantTargetINTEL",
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
          "IdResultType IdResult LiteralInteger LiteralInteger*"
        )
    case SpecConstantArchitectureINTEL extends Definition(
          "OpSpecConstantArchitectureINTEL",
          SPIRVOpcodeNumber(6252),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SpecConstantArchitectureINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    category = values.required(2),
                    family = values.required(3),
                    instructionOpcode = values.required(4),
                    architecture = values.required(5)
                  )
              ),
          "IdResultType IdResult LiteralInteger LiteralInteger LiteralInteger LiteralInteger"
        )
    case SpecConstantCapabilitiesINTEL extends Definition(
          "OpSpecConstantCapabilitiesINTEL",
          SPIRVOpcodeNumber(6253),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.SpecConstantCapabilitiesINTEL(values.required(0), values.required(1), values.repeated(2))
              ),
          "IdResultType IdResult Capability*"
        )
    case ConditionalCopyObjectINTEL extends Definition(
          "OpConditionalCopyObjectINTEL",
          SPIRVOpcodeNumber(6254),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ConditionalCopyObjectINTEL(values.required(0), values.required(1), values.repeated(2))
              ),
          "IdResultType IdResult IdRef*"
        )
    case PredicatedLoadINTEL extends Definition(
          "OpPredicatedLoadINTEL",
          SPIRVOpcodeNumber(6258),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.PredicatedLoadINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    pointer = values.required(2),
                    pred = values.required(3),
                    defaultValue = values.required(4),
                    mods = values.optional(5)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef MemoryAccess?"
        )
    case PredicatedStoreINTEL extends Definition(
          "OpPredicatedStoreINTEL",
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
          "IdRef IdRef IdRef MemoryAccess?"
        )
    case GroupIMulKHR extends Definition(
          "OpGroupIMulKHR",
          SPIRVOpcodeNumber(6401),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupIMulKHR(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    operation = values.required(3),
                    x = values.required(4)
                  )
              ),
          "IdResultType IdResult IdScope GroupOperation IdRef"
        )
    case GroupFMulKHR extends Definition(
          "OpGroupFMulKHR",
          SPIRVOpcodeNumber(6402),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupFMulKHR(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    operation = values.required(3),
                    x = values.required(4)
                  )
              ),
          "IdResultType IdResult IdScope GroupOperation IdRef"
        )
    case GroupBitwiseAndKHR extends Definition(
          "OpGroupBitwiseAndKHR",
          SPIRVOpcodeNumber(6403),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupBitwiseAndKHR(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    operation = values.required(3),
                    x = values.required(4)
                  )
              ),
          "IdResultType IdResult IdScope GroupOperation IdRef"
        )
    case GroupBitwiseOrKHR extends Definition(
          "OpGroupBitwiseOrKHR",
          SPIRVOpcodeNumber(6404),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupBitwiseOrKHR(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    operation = values.required(3),
                    x = values.required(4)
                  )
              ),
          "IdResultType IdResult IdScope GroupOperation IdRef"
        )
    case GroupBitwiseXorKHR extends Definition(
          "OpGroupBitwiseXorKHR",
          SPIRVOpcodeNumber(6405),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupBitwiseXorKHR(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    operation = values.required(3),
                    x = values.required(4)
                  )
              ),
          "IdResultType IdResult IdScope GroupOperation IdRef"
        )
    case GroupLogicalAndKHR extends Definition(
          "OpGroupLogicalAndKHR",
          SPIRVOpcodeNumber(6406),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupLogicalAndKHR(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    operation = values.required(3),
                    x = values.required(4)
                  )
              ),
          "IdResultType IdResult IdScope GroupOperation IdRef"
        )
    case GroupLogicalOrKHR extends Definition(
          "OpGroupLogicalOrKHR",
          SPIRVOpcodeNumber(6407),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupLogicalOrKHR(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    operation = values.required(3),
                    x = values.required(4)
                  )
              ),
          "IdResultType IdResult IdScope GroupOperation IdRef"
        )
    case GroupLogicalXorKHR extends Definition(
          "OpGroupLogicalXorKHR",
          SPIRVOpcodeNumber(6408),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.GroupLogicalXorKHR(
                    resultType = values.required(0),
                    dest = values.required(1),
                    execution = values.required(2),
                    operation = values.required(3),
                    x = values.required(4)
                  )
              ),
          "IdResultType IdResult IdScope GroupOperation IdRef"
        )
    case RoundFToTF32INTEL extends Definition(
          "OpRoundFToTF32INTEL",
          SPIRVOpcodeNumber(6426),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values => Instruction.RoundFToTF32INTEL(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case MaskedGatherINTEL extends Definition(
          "OpMaskedGatherINTEL",
          SPIRVOpcodeNumber(6428),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.MaskedGatherINTEL(
                    resultType = values.required(0),
                    dest = values.required(1),
                    ptrVector = values.required(2),
                    alignment = values.required(3),
                    mask = values.required(4),
                    fillEmpty = values.required(5)
                  )
              ),
          "IdResultType IdResult IdRef LiteralInteger IdRef IdRef"
        )
    case MaskedScatterINTEL extends Definition(
          "OpMaskedScatterINTEL",
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
          "IdRef IdRef LiteralInteger IdRef"
        )
    case ConvertHandleToImageINTEL extends Definition(
          "OpConvertHandleToImageINTEL",
          SPIRVOpcodeNumber(6529),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ConvertHandleToImageINTEL(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case ConvertHandleToSamplerINTEL extends Definition(
          "OpConvertHandleToSamplerINTEL",
          SPIRVOpcodeNumber(6530),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.ConvertHandleToSamplerINTEL(values.required(0), values.required(1), values.required(2))
              ),
          "IdResultType IdResult IdRef"
        )
    case ConvertHandleToSampledImageINTEL extends Definition(
          "OpConvertHandleToSampledImageINTEL",
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
          "IdResultType IdResult IdRef"
        )
    case FDot2MixAcc32VALVE extends Definition(
          "OpFDot2MixAcc32VALVE",
          SPIRVOpcodeNumber(6916),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FDot2MixAcc32VALVE(
                    resultType = values.required(0),
                    dest = values.required(1),
                    vector1 = values.required(2),
                    vector2 = values.required(3),
                    accumulator = values.required(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef"
        )
    case FDot2MixAcc16VALVE extends Definition(
          "OpFDot2MixAcc16VALVE",
          SPIRVOpcodeNumber(6917),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FDot2MixAcc16VALVE(
                    resultType = values.required(0),
                    dest = values.required(1),
                    vector1 = values.required(2),
                    vector2 = values.required(3),
                    accumulator = values.required(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef"
        )
    case FDot4MixAcc32VALVE extends Definition(
          "OpFDot4MixAcc32VALVE",
          SPIRVOpcodeNumber(6918),
          definition =>
            raw =>
              Instruction.decodeDefinition(
                definition,
                raw,
                values =>
                  Instruction.FDot4MixAcc32VALVE(
                    resultType = values.required(0),
                    dest = values.required(1),
                    vector1 = values.required(2),
                    vector2 = values.required(3),
                    accumulator = values.required(4)
                  )
              ),
          "IdResultType IdResult IdRef IdRef IdRef"
        )

    val shapes: Vec[OperandShape] = OperandShape.parse(operands)
    lazy val decoder: Decoder = decode(this)
  }

  val version: Version = Version(
    1,
    6
  )
  val definitions: Vec[Definition] = Definition.values.toVec

  val operandKinds: Map[OperandKind, OperandKindInfo] =
    Vec(
      "ImageOperands\tNone 0  \tBias 1 IdRef \tLod 2 IdRef \tGrad 4 IdRef,IdRef \tConstOffset 8 IdRef \tOffset 16 IdRef \tConstOffsets 32 IdRef \tSample 64 IdRef \tMinLod 128 IdRef \tMakeTexelAvailable 256 IdScope MakeTexelAvailableKHR\tMakeTexelVisible 512 IdScope MakeTexelVisibleKHR\tNonPrivateTexel 1024  NonPrivateTexelKHR\tVolatileTexel 2048  VolatileTexelKHR\tSignExtend 4096  \tZeroExtend 8192  \tNontemporal 16384  \tOffsets 65536 IdRef \nFPFastMathMode\tNone 0  \tNotNaN 1  \tNotInf 2  \tNSZ 4  \tAllowRecip 8  \tFast 16  \tAllowContract 65536  AllowContractFastINTEL\tAllowReassoc 131072  AllowReassocINTEL\tAllowTransform 262144  \nSelectionControl\tNone 0  \tFlatten 1  \tDontFlatten 2  \nLoopControl\tNone 0  \tUnroll 1  \tDontUnroll 2  \tDependencyInfinite 4  \tDependencyLength 8 LiteralInteger \tMinIterations 16 LiteralInteger \tMaxIterations 32 LiteralInteger \tIterationMultiple 64 LiteralInteger \tPeelCount 128 LiteralInteger \tPartialCount 256 LiteralInteger \tInitiationIntervalALTERA 65536 LiteralInteger InitiationIntervalINTEL\tMaxConcurrencyALTERA 131072 LiteralInteger MaxConcurrencyINTEL\tDependencyArrayALTERA 262144 LiteralInteger DependencyArrayINTEL\tPipelineEnableALTERA 524288 LiteralInteger PipelineEnableINTEL\tLoopCoalesceALTERA 1048576 LiteralInteger LoopCoalesceINTEL\tMaxInterleavingALTERA 2097152 LiteralInteger MaxInterleavingINTEL\tSpeculatedIterationsALTERA 4194304 LiteralInteger SpeculatedIterationsINTEL\tNoFusionALTERA 8388608  NoFusionINTEL\tLoopCountALTERA 16777216 LiteralInteger LoopCountINTEL\tMaxReinvocationDelayALTERA 33554432 LiteralInteger MaxReinvocationDelayINTEL\tMultipleWaitQueuesQCOM 268435456 LiteralInteger \nFunctionControl\tNone 0  \tInline 1  \tDontInline 2  \tPure 4  \tConst 8  \tOptNoneEXT 65536  OptNoneINTEL\nMemorySemantics\tRelaxed 0  None\tAcquire 2  \tRelease 4  \tAcquireRelease 8  \tSequentiallyConsistent 16  \tUniformMemory 64  \tSubgroupMemory 128  \tWorkgroupMemory 256  \tCrossWorkgroupMemory 512  \tAtomicCounterMemory 1024  \tImageMemory 2048  \tOutputMemory 4096  OutputMemoryKHR\tMakeAvailable 8192  MakeAvailableKHR\tMakeVisible 16384  MakeVisibleKHR\tVolatile 32768  \nMemoryAccess\tNone 0  \tVolatile 1  \tAligned 2 LiteralInteger \tNontemporal 4  \tMakePointerAvailable 8 IdScope MakePointerAvailableKHR\tMakePointerVisible 16 IdScope MakePointerVisibleKHR\tNonPrivatePointer 32  NonPrivatePointerKHR\tAliasScopeINTELMask 65536 IdRef \tNoAliasINTELMask 131072 IdRef \nKernelProfilingInfo\tNone 0  \tCmdExecTime 1  \nRayFlags\tNoneKHR 0  \tOpaqueKHR 1  \tNoOpaqueKHR 2  \tTerminateOnFirstHitKHR 4  \tSkipClosestHitShaderKHR 8  \tCullBackFacingTrianglesKHR 16  \tCullFrontFacingTrianglesKHR 32  \tCullOpaqueKHR 64  \tCullNoOpaqueKHR 128  \tSkipTrianglesKHR 256  SkipBuiltinPrimitivesNV\tSkipAABBsKHR 512  \tForceOpacityMicromap2StateKHR 1024  ForceOpacityMicromap2StateEXT\nFragmentShadingRate\tVertical2Pixels 1  \tVertical4Pixels 2  \tHorizontal2Pixels 4  \tHorizontal4Pixels 8  \nRawAccessChainOperands\tNone 0  \tRobustnessPerComponentNV 1  \tRobustnessPerElementNV 2  \nSourceLanguage\tUnknown 0  \tESSL 1  \tGLSL 2  \tOpenCL_C 3  \tOpenCL_CPP 4  \tHLSL 5  \tCPP_for_OpenCL 6  \tSYCL 7  \tHERO_C 8  \tNZSL 9  \tWGSL 10  \tSlang 11  \tZig 12  \tRust 13  \tPred 14  \tApilaJai 15  \nExecutionModel\tVertex 0  \tTessellationControl 1  \tTessellationEvaluation 2  \tGeometry 3  \tFragment 4  \tGLCompute 5  \tKernel 6  \tTaskNV 5267  \tMeshNV 5268  \tRayGenerationKHR 5313  RayGenerationNV\tIntersectionKHR 5314  IntersectionNV\tAnyHitKHR 5315  AnyHitNV\tClosestHitKHR 5316  ClosestHitNV\tMissKHR 5317  MissNV\tCallableKHR 5318  CallableNV\tTaskEXT 5364  \tMeshEXT 5365  \nAddressingModel\tLogical 0  \tPhysical32 1  \tPhysical64 2  \tPhysicalStorageBuffer64 5348  PhysicalStorageBuffer64EXT\nMemoryModel\tSimple 0  \tGLSL450 1  \tOpenCL 2  \tVulkan 3  VulkanKHR\nExecutionMode\tInvocations 0 LiteralInteger \tSpacingEqual 1  \tSpacingFractionalEven 2  \tSpacingFractionalOdd 3  \tVertexOrderCw 4  \tVertexOrderCcw 5  \tPixelCenterInteger 6  \tOriginUpperLeft 7  \tOriginLowerLeft 8  \tEarlyFragmentTests 9  \tPointMode 10  \tXfb 11  \tDepthReplacing 12  \tDepthGreater 14  \tDepthLess 15  \tDepthUnchanged 16  \tLocalSize 17 LiteralInteger,LiteralInteger,LiteralInteger \tLocalSizeHint 18 LiteralInteger,LiteralInteger,LiteralInteger \tInputPoints 19  \tInputLines 20  \tInputLinesAdjacency 21  \tTriangles 22  \tInputTrianglesAdjacency 23  \tQuads 24  \tIsolines 25  \tOutputVertices 26 LiteralInteger \tOutputPoints 27  \tOutputLineStrip 28  \tOutputTriangleStrip 29  \tVecTypeHint 30 LiteralInteger \tContractionOff 31  \tInitializer 33  \tFinalizer 34  \tSubgroupSize 35 LiteralInteger \tSubgroupsPerWorkgroup 36 LiteralInteger \tSubgroupsPerWorkgroupId 37 IdRef \tLocalSizeId 38 IdRef,IdRef,IdRef \tLocalSizeHintId 39 IdRef,IdRef,IdRef \tNonCoherentColorAttachmentReadEXT 4169  \tNonCoherentDepthAttachmentReadEXT 4170  \tNonCoherentStencilAttachmentReadEXT 4171  \tSubgroupUniformControlFlowKHR 4421  \tPostDepthCoverage 4446  \tDenormPreserve 4459 LiteralInteger \tDenormFlushToZero 4460 LiteralInteger \tSignedZeroInfNanPreserve 4461 LiteralInteger \tRoundingModeRTE 4462 LiteralInteger \tRoundingModeRTZ 4463 LiteralInteger \tNonCoherentTileAttachmentReadQCOM 4489  \tTileShadingRateQCOM 4490 LiteralInteger,LiteralInteger,LiteralInteger \tEarlyAndLateFragmentTestsAMD 5017  \tStencilRefReplacingEXT 5027  \tCoalescingAMDX 5069  \tIsApiEntryAMDX 5070 IdRef \tMaxNodeRecursionAMDX 5071 IdRef \tStaticNumWorkgroupsAMDX 5072 IdRef,IdRef,IdRef \tShaderIndexAMDX 5073 IdRef \tMaxNumWorkgroupsAMDX 5077 IdRef,IdRef,IdRef \tStencilRefUnchangedFrontAMD 5079  \tStencilRefGreaterFrontAMD 5080  \tStencilRefLessFrontAMD 5081  \tStencilRefUnchangedBackAMD 5082  \tStencilRefGreaterBackAMD 5083  \tStencilRefLessBackAMD 5084  \tQuadDerivativesKHR 5088  \tRequireFullQuadsKHR 5089  \tSharesInputWithAMDX 5102 IdRef,IdRef \tArithmeticPoisonKHR 5157  \tOutputLinesEXT 5269  OutputLinesNV\tOutputPrimitivesEXT 5270 LiteralInteger OutputPrimitivesNV\tDerivativeGroupQuadsKHR 5289  DerivativeGroupQuadsNV\tDerivativeGroupLinearKHR 5290  DerivativeGroupLinearNV\tOutputTrianglesEXT 5298  OutputTrianglesNV\tPixelInterlockOrderedEXT 5366  \tPixelInterlockUnorderedEXT 5367  \tSampleInterlockOrderedEXT 5368  \tSampleInterlockUnorderedEXT 5369  \tShadingRateInterlockOrderedEXT 5370  \tShadingRateInterlockUnorderedEXT 5371  \tShader64BitIndexingEXT 5427  \tSharedLocalMemorySizeINTEL 5618 LiteralInteger \tRoundingModeRTPINTEL 5620 LiteralInteger \tRoundingModeRTNINTEL 5621 LiteralInteger \tFloatingPointModeALTINTEL 5622 LiteralInteger \tFloatingPointModeIEEEINTEL 5623 LiteralInteger \tMaxWorkgroupSizeINTEL 5893 LiteralInteger,LiteralInteger,LiteralInteger \tMaxWorkDimINTEL 5894 LiteralInteger \tNoGlobalOffsetINTEL 5895  \tNumSIMDWorkitemsINTEL 5896 LiteralInteger \tSchedulerTargetFmaxMhzINTEL 5903 LiteralInteger \tMaximallyReconvergesKHR 6023  \tFPFastMathDefault 6028 IdRef,IdRef \tOpacityMicromapIdKHR 6031 IdRef \tStreamingInterfaceINTEL 6154 LiteralInteger \tRegisterMapInterfaceINTEL 6160 LiteralInteger \tNamedBarrierCountINTEL 6417 LiteralInteger \tMaximumRegistersINTEL 6461 LiteralInteger \tMaximumRegistersIdINTEL 6462 IdRef \tNamedMaximumRegistersINTEL 6463 NamedMaximumNumberOfRegisters \nStorageClass\tUniformConstant 0  \tInput 1  \tUniform 2  \tOutput 3  \tWorkgroup 4  \tCrossWorkgroup 5  \tPrivate 6  \tFunction 7  \tGeneric 8  \tPushConstant 9  \tAtomicCounter 10  \tImage 11  \tStorageBuffer 12  \tTileImageEXT 4172  \tTileAttachmentQCOM 4491  \tNodePayloadAMDX 5068  \tCallableDataKHR 5328  CallableDataNV\tIncomingCallableDataKHR 5329  IncomingCallableDataNV\tRayPayloadKHR 5338  RayPayloadNV\tHitAttributeKHR 5339  HitAttributeNV\tIncomingRayPayloadKHR 5342  IncomingRayPayloadNV\tShaderRecordBufferKHR 5343  ShaderRecordBufferNV\tPhysicalStorageBuffer 5349  PhysicalStorageBufferEXT\tHitObjectAttributeNV 5385  \tTaskPayloadWorkgroupEXT 5402  \tHitObjectAttributeEXT 5411  \tCodeSectionINTEL 5605  \tDeviceOnlyALTERA 5936  DeviceOnlyINTEL\tHostOnlyALTERA 5937  HostOnlyINTEL\nDim\t1D 0  \t2D 1  \t3D 2  \tCube 3  \tRect 4  \tBuffer 5  \tSubpassData 6  \tTileImageDataEXT 4173  \nSamplerAddressingMode\tNone 0  \tClampToEdge 1  \tClamp 2  \tRepeat 3  \tRepeatMirrored 4  \n",
      "SamplerFilterMode\tNearest 0  \tLinear 1  \nImageFormat\tUnknown 0  \tRgba32f 1  \tRgba16f 2  \tR32f 3  \tRgba8 4  \tRgba8Snorm 5  \tRg32f 6  \tRg16f 7  \tR11fG11fB10f 8  \tR16f 9  \tRgba16 10  \tRgb10A2 11  \tRg16 12  \tRg8 13  \tR16 14  \tR8 15  \tRgba16Snorm 16  \tRg16Snorm 17  \tRg8Snorm 18  \tR16Snorm 19  \tR8Snorm 20  \tRgba32i 21  \tRgba16i 22  \tRgba8i 23  \tR32i 24  \tRg32i 25  \tRg16i 26  \tRg8i 27  \tR16i 28  \tR8i 29  \tRgba32ui 30  \tRgba16ui 31  \tRgba8ui 32  \tR32ui 33  \tRgb10a2ui 34  \tRg32ui 35  \tRg16ui 36  \tRg8ui 37  \tR16ui 38  \tR8ui 39  \tR64ui 40  \tR64i 41  \nImageChannelOrder\tR 0  \tA 1  \tRG 2  \tRA 3  \tRGB 4  \tRGBA 5  \tBGRA 6  \tARGB 7  \tIntensity 8  \tLuminance 9  \tRx 10  \tRGx 11  \tRGBx 12  \tDepth 13  \tDepthStencil 14  \tsRGB 15  \tsRGBx 16  \tsRGBA 17  \tsBGRA 18  \tABGR 19  \nImageChannelDataType\tSnormInt8 0  \tSnormInt16 1  \tUnormInt8 2  \tUnormInt16 3  \tUnormShort565 4  \tUnormShort555 5  \tUnormInt101010 6  \tSignedInt8 7  \tSignedInt16 8  \tSignedInt32 9  \tUnsignedInt8 10  \tUnsignedInt16 11  \tUnsignedInt32 12  \tHalfFloat 13  \tFloat 14  \tUnormInt24 15  \tUnormInt101010_2 16  \tUnormInt10X6EXT 17  \tUnsignedIntRaw10EXT 19  \tUnsignedIntRaw12EXT 20  \tUnormInt2_101010EXT 21  \tUnsignedInt10X6EXT 22  \tUnsignedInt12X4EXT 23  \tUnsignedInt14X2EXT 24  \tUnormInt12X4EXT 25  \tUnormInt14X2EXT 26  \nFPRoundingMode\tRTE 0  \tRTZ 1  \tRTP 2  \tRTN 3  \nFPDenormMode\tPreserve 0  \tFlushToZero 1  \nQuantizationModes\tTRN 0  \tTRN_ZERO 1  \tRND 2  \tRND_ZERO 3  \tRND_INF 4  \tRND_MIN_INF 5  \tRND_CONV 6  \tRND_CONV_ODD 7  \nFPOperationMode\tIEEE 0  \tALT 1  \nOverflowModes\tWRAP 0  \tSAT 1  \tSAT_ZERO 2  \tSAT_SYM 3  \nLinkageType\tExport 0  \tImport 1  \tLinkOnceODR 2  \tWeakAMD 3  \nAccessQualifier\tReadOnly 0  \tWriteOnly 1  \tReadWrite 2  \nHostAccessQualifier\tNoneINTEL 0  \tReadINTEL 1  \tWriteINTEL 2  \tReadWriteINTEL 3  \nFunctionParameterAttribute\tZext 0  \tSext 1  \tByVal 2  \tSret 3  \tNoAlias 4  \tNoCapture 5  \tNoWrite 6  \tNoReadWrite 7  \tRuntimeAlignedALTERA 5940  RuntimeAlignedINTEL\nDecoration\tRelaxedPrecision 0  \tSpecId 1 LiteralInteger \tBlock 2  \tBufferBlock 3  \tRowMajor 4  \tColMajor 5  \tArrayStride 6 LiteralInteger \tMatrixStride 7 LiteralInteger \tGLSLShared 8  \tGLSLPacked 9  \tCPacked 10  \tBuiltIn 11 BuiltIn \tNoPerspective 13  \tFlat 14  \tPatch 15  \tCentroid 16  \tSample 17  \tInvariant 18  \tRestrict 19  \tAliased 20  \tVolatile 21  \tConstant 22  \tCoherent 23  \tNonWritable 24  \tNonReadable 25  \tUniform 26  \tUniformId 27 IdScope \tSaturatedConversion 28  \tStream 29 LiteralInteger \tLocation 30 LiteralInteger \tComponent 31 LiteralInteger \tIndex 32 LiteralInteger \tBinding 33 LiteralInteger \tDescriptorSet 34 LiteralInteger \tOffset 35 LiteralInteger \tXfbBuffer 36 LiteralInteger \tXfbStride 37 LiteralInteger \tFuncParamAttr 38 FunctionParameterAttribute \tFPRoundingMode 39 FPRoundingMode \tFPFastMathMode 40 FPFastMathMode \tLinkageAttributes 41 LiteralString,LinkageType \tNoContraction 42  \tInputAttachmentIndex 43 LiteralInteger \tAlignment 44 LiteralInteger \tMaxByteOffset 45 LiteralInteger \tAlignmentId 46 IdRef \tMaxByteOffsetId 47 IdRef \tSaturatedToLargestFloat8NormalConversionEXT 4216  \tNoSignedWrap 4469  \tNoUnsignedWrap 4470  \tWeightTextureQCOM 4487  \tBlockMatchTextureQCOM 4488  \tBlockMatchSamplerQCOM 4499  \tExplicitInterpAMD 4999  \tNodeSharesPayloadLimitsWithAMDX 5019 IdRef \tNodeMaxPayloadsAMDX 5020 IdRef \tTrackFinishWritingAMDX 5078  \tPayloadNodeNameAMDX 5091 IdRef \tPayloadNodeBaseIndexAMDX 5098 IdRef \tPayloadNodeSparseArrayAMDX 5099  \tPayloadNodeArraySizeAMDX 5100 IdRef \tPayloadDispatchIndirectAMDX 5105  \tArrayStrideIdEXT 5124 IdRef \tOffsetIdEXT 5125 IdRef \tUTFEncodedKHR 5145  \tOverrideCoverageNV 5248  \tPassthroughNV 5250  \tViewportRelativeNV 5252  \tSecondaryViewportRelativeNV 5256 LiteralInteger \tPerPrimitiveEXT 5271  PerPrimitiveNV\tPerViewNV 5272  \tPerTaskNV 5273  \tPerVertexKHR 5285  PerVertexNV\tNonUniform 5300  NonUniformEXT\tRestrictPointer 5355  RestrictPointerEXT\tAliasedPointer 5356  AliasedPointerEXT\tMemberOffsetNV 5358 LiteralInteger \tHitObjectShaderRecordBufferNV 5386  \tHitObjectShaderRecordBufferEXT 5389  \tBankNV 5397 LiteralInteger \tBindlessSamplerNV 5398  \tBindlessImageNV 5399  \tBoundSamplerNV 5400  \tBoundImageNV 5401  \tSIMTCallINTEL 5599 LiteralInteger \tReferencedIndirectlyINTEL 5602  \tClobberINTEL 5607 LiteralString \tSideEffectsINTEL 5608  \tVectorComputeVariableINTEL 5624  \tFuncParamIOKindINTEL 5625 LiteralInteger \tVectorComputeFunctionINTEL 5626  \tStackCallINTEL 5627  \tGlobalVariableOffsetINTEL 5628 LiteralInteger \tCounterBuffer 5634 IdRef HlslCounterBufferGOOGLE\tUserSemantic 5635 LiteralString HlslSemanticGOOGLE\tUserTypeGOOGLE 5636 LiteralString \tFunctionRoundingModeINTEL 5822 LiteralInteger,FPRoundingMode \tFunctionDenormModeINTEL 5823 LiteralInteger,FPDenormMode \tRegisterALTERA 5825  RegisterINTEL\tMemoryALTERA 5826 LiteralString MemoryINTEL\tNumbanksALTERA 5827 LiteralInteger NumbanksINTEL\tBankwidthALTERA 5828 LiteralInteger BankwidthINTEL\tMaxPrivateCopiesALTERA 5829 LiteralInteger MaxPrivateCopiesINTEL\tSinglepumpALTERA 5830  SinglepumpINTEL\tDoublepumpALTERA 5831  DoublepumpINTEL\tMaxReplicatesALTERA 5832 LiteralInteger MaxReplicatesINTEL\tSimpleDualPortALTERA 5833  SimpleDualPortINTEL\tMergeALTERA 5834 LiteralString,LiteralString MergeINTEL\tBankBitsALTERA 5835 LiteralInteger BankBitsINTEL\tForcePow2DepthALTERA 5836 LiteralInteger ForcePow2DepthINTEL\tStridesizeALTERA 5883 LiteralInteger StridesizeINTEL\tWordsizeALTERA 5884 LiteralInteger WordsizeINTEL\tTrueDualPortALTERA 5885  TrueDualPortINTEL\tBurstCoalesceALTERA 5899  BurstCoalesceINTEL\tCacheSizeALTERA 5900 LiteralInteger CacheSizeINTEL\tDontStaticallyCoalesceALTERA 5901  DontStaticallyCoalesceINTEL\tPrefetchALTERA 5902 LiteralInteger PrefetchINTEL\tStallEnableALTERA 5905  StallEnableINTEL\tFuseLoopsInFunctionALTERA 5907  FuseLoopsInFunctionINTEL\tMathOpDSPModeALTERA 5909 LiteralInteger,LiteralInteger MathOpDSPModeINTEL\tAliasScopeINTEL 5914 IdRef \tNoAliasINTEL 5915 IdRef \tInitiationIntervalALTERA 5917 LiteralInteger InitiationIntervalINTEL\tMaxConcurrencyALTERA 5918 LiteralInteger MaxConcurrencyINTEL\tPipelineEnableALTERA 5919 LiteralInteger PipelineEnableINTEL\tBufferLocationALTERA 5921 LiteralInteger BufferLocationINTEL\tIOPipeStorageALTERA 5944 LiteralInteger IOPipeStorageINTEL\tFunctionFloatingPointModeINTEL 6080 LiteralInteger,FPOperationMode \tSingleElementVectorINTEL 6085  \tVectorComputeCallableFunctionINTEL 6087  \tMediaBlockIOINTEL 6140  \tStallFreeALTERA 6151  StallFreeINTEL\tFPMaxErrorDecorationINTEL 6170 LiteralFloat \tLatencyControlLabelALTERA 6172 LiteralInteger LatencyControlLabelINTEL\tLatencyControlConstraintALTERA 6173 LiteralInteger,LiteralInteger,LiteralInteger LatencyControlConstraintINTEL\tConduitKernelArgumentALTERA 6175  ConduitKernelArgumentINTEL\tRegisterMapKernelArgumentALTERA 6176  RegisterMapKernelArgumentINTEL\tMMHostInterfaceAddressWidthALTERA 6177 LiteralInteger MMHostInterfaceAddressWidthINTEL\tMMHostInterfaceDataWidthALTERA 6178 LiteralInteger MMHostInterfaceDataWidthINTEL\tMMHostInterfaceLatencyALTERA 6179 LiteralInteger MMHostInterfaceLatencyINTEL\tMMHostInterfaceReadWriteModeALTERA 6180 AccessQualifier MMHostInterfaceReadWriteModeINTEL\tMMHostInterfaceMaxBurstALTERA 6181 LiteralInteger MMHostInterfaceMaxBurstINTEL\tMMHostInterfaceWaitRequestALTERA 6182 LiteralInteger MMHostInterfaceWaitRequestINTEL\tStableKernelArgumentALTERA 6183  StableKernelArgumentINTEL\tHostAccessINTEL 6188 HostAccessQualifier,LiteralString \tInitModeALTERA 6190 InitializationModeQualifier InitModeINTEL\tImplementInRegisterMapALTERA 6191 LiteralInteger ImplementInRegisterMapINTEL\tConditionalINTEL 6247 IdRef \tCacheControlLoadINTEL 6442 LiteralInteger,LoadCacheControl \tCacheControlStoreINTEL 6443 LiteralInteger,StoreCacheControl \tIntrinsicSAMSUNG 7040 LiteralInteger \n",
      "BuiltIn\tPosition 0  \tPointSize 1  \tClipDistance 3  \tCullDistance 4  \tVertexId 5  \tInstanceId 6  \tPrimitiveId 7  \tInvocationId 8  \tLayer 9  \tViewportIndex 10  \tTessLevelOuter 11  \tTessLevelInner 12  \tTessCoord 13  \tPatchVertices 14  \tFragCoord 15  \tPointCoord 16  \tFrontFacing 17  \tSampleId 18  \tSamplePosition 19  \tSampleMask 20  \tFragDepth 22  \tHelperInvocation 23  \tNumWorkgroups 24  \tWorkgroupSize 25  \tWorkgroupId 26  \tLocalInvocationId 27  \tGlobalInvocationId 28  \tLocalInvocationIndex 29  \tWorkDim 30  \tGlobalSize 31  \tEnqueuedWorkgroupSize 32  \tGlobalOffset 33  \tGlobalLinearId 34  \tWorkgroupLinearId 35  \tSubgroupSize 36  \tSubgroupMaxSize 37  \tNumSubgroups 38  \tNumEnqueuedSubgroups 39  \tSubgroupId 40  \tSubgroupLocalInvocationId 41  \tVertexIndex 42  \tInstanceIndex 43  \tCoreIDARM 4160  \tCoreCountARM 4161  \tCoreMaxIDARM 4162  \tWarpIDARM 4163  \tWarpMaxIDARM 4164  \tSubgroupEqMask 4416  SubgroupEqMaskKHR\tSubgroupGeMask 4417  SubgroupGeMaskKHR\tSubgroupGtMask 4418  SubgroupGtMaskKHR\tSubgroupLeMask 4419  SubgroupLeMaskKHR\tSubgroupLtMask 4420  SubgroupLtMaskKHR\tBaseVertex 4424  \tBaseInstance 4425  \tDrawIndex 4426  \tPrimitiveShadingRateKHR 4432  \tDeviceIndex 4438  \tViewIndex 4440  \tShadingRateKHR 4444  \tTileOffsetQCOM 4492  \tTileDimensionQCOM 4493  \tTileApronSizeQCOM 4494  \tBaryCoordNoPerspAMD 4992  \tBaryCoordNoPerspCentroidAMD 4993  \tBaryCoordNoPerspSampleAMD 4994  \tBaryCoordSmoothAMD 4995  \tBaryCoordSmoothCentroidAMD 4996  \tBaryCoordSmoothSampleAMD 4997  \tBaryCoordPullModelAMD 4998  \tFragStencilRefEXT 5014  \tRemainingRecursionLevelsAMDX 5021  \tShaderIndexAMDX 5073  \tSamplerHeapEXT 5122  \tResourceHeapEXT 5123  \tViewportMaskNV 5253  \tSecondaryPositionNV 5257  \tSecondaryViewportMaskNV 5258  \tPositionPerViewNV 5261  \tViewportMaskPerViewNV 5262  \tFullyCoveredEXT 5264  \tTaskCountNV 5274  \tPrimitiveCountNV 5275  \tPrimitiveIndicesNV 5276  \tClipDistancePerViewNV 5277  \tCullDistancePerViewNV 5278  \tLayerPerViewNV 5279  \tMeshViewCountNV 5280  \tMeshViewIndicesNV 5281  \tBaryCoordKHR 5286  BaryCoordNV\tBaryCoordNoPerspKHR 5287  BaryCoordNoPerspNV\tFragSizeEXT 5292  FragmentSizeNV\tFragInvocationCountEXT 5293  InvocationsPerPixelNV\tPrimitivePointIndicesEXT 5294  \tPrimitiveLineIndicesEXT 5295  \tPrimitiveTriangleIndicesEXT 5296  \tCullPrimitiveEXT 5299  \tLaunchIdKHR 5319  LaunchIdNV\tLaunchSizeKHR 5320  LaunchSizeNV\tWorldRayOriginKHR 5321  WorldRayOriginNV\tWorldRayDirectionKHR 5322  WorldRayDirectionNV\tObjectRayOriginKHR 5323  ObjectRayOriginNV\tObjectRayDirectionKHR 5324  ObjectRayDirectionNV\tRayTminKHR 5325  RayTminNV\tRayTmaxKHR 5326  RayTmaxNV\tInstanceCustomIndexKHR 5327  InstanceCustomIndexNV\tObjectToWorldKHR 5330  ObjectToWorldNV\tWorldToObjectKHR 5331  WorldToObjectNV\tHitTNV 5332  \tHitKindKHR 5333  HitKindNV\tCurrentRayTimeNV 5334  \tHitTriangleVertexPositionsKHR 5335  \tHitMicroTriangleVertexPositionsNV 5337  \tHitMicroTriangleVertexBarycentricsNV 5344  \tIncomingRayFlagsKHR 5351  IncomingRayFlagsNV\tRayGeometryIndexKHR 5352  \tHitIsSphereNV 5359  \tHitIsLSSNV 5360  \tHitSpherePositionNV 5361  \tWarpsPerSMNV 5374  \tSMCountNV 5375  \tWarpIDNV 5376  \tSMIDNV 5377  \tHitLSSPositionsNV 5396  \tHitKindFrontFacingMicroTriangleNV 5405  \tHitKindBackFacingMicroTriangleNV 5406  \tHitSphereRadiusNV 5420  \tHitLSSRadiiNV 5421  \tClusterIDNV 5436  \tCullMaskKHR 6021  \nScope\tCrossDevice 0  \tDevice 1  \tWorkgroup 2  \tSubgroup 3  \tInvocation 4  \tQueueFamily 5  QueueFamilyKHR\tShaderCallKHR 6  \nGroupOperation\tReduce 0  \tInclusiveScan 1  \tExclusiveScan 2  \tClusteredReduce 3  \tPartitionedReduceEXT 6  PartitionedReduceNV\tPartitionedInclusiveScanEXT 7  PartitionedInclusiveScanNV\tPartitionedExclusiveScanEXT 8  PartitionedExclusiveScanNV\nKernelEnqueueFlags\tNoWait 0  \tWaitKernel 1  \tWaitWorkGroup 2  \n",
      "Capability\tMatrix 0  \tShader 1  \tGeometry 2  \tTessellation 3  \tAddresses 4  \tLinkage 5  \tKernel 6  \tVector16 7  \tFloat16Buffer 8  \tFloat16 9  \tFloat64 10  \tInt64 11  \tInt64Atomics 12  \tImageBasic 13  \tImageReadWrite 14  \tImageMipmap 15  \tPipes 17  \tGroups 18  \tDeviceEnqueue 19  \tLiteralSampler 20  \tAtomicStorage 21  \tInt16 22  \tTessellationPointSize 23  \tGeometryPointSize 24  \tImageGatherExtended 25  \tStorageImageMultisample 27  \tUniformBufferArrayDynamicIndexing 28  \tSampledImageArrayDynamicIndexing 29  \tStorageBufferArrayDynamicIndexing 30  \tStorageImageArrayDynamicIndexing 31  \tClipDistance 32  \tCullDistance 33  \tImageCubeArray 34  \tSampleRateShading 35  \tImageRect 36  \tSampledRect 37  \tGenericPointer 38  \tInt8 39  \tInputAttachment 40  \tSparseResidency 41  \tMinLod 42  \tSampled1D 43  \tImage1D 44  \tSampledCubeArray 45  \tSampledBuffer 46  \tImageBuffer 47  \tImageMSArray 48  \tStorageImageExtendedFormats 49  \tImageQuery 50  \tDerivativeControl 51  \tInterpolationFunction 52  \tTransformFeedback 53  \tGeometryStreams 54  \tStorageImageReadWithoutFormat 55  \tStorageImageWriteWithoutFormat 56  \tMultiViewport 57  \tSubgroupDispatch 58  \tNamedBarrier 59  \tPipeStorage 60  \tGroupNonUniform 61  \tGroupNonUniformVote 62  \tGroupNonUniformArithmetic 63  \tGroupNonUniformBallot 64  \tGroupNonUniformShuffle 65  \tGroupNonUniformShuffleRelative 66  \tGroupNonUniformClustered 67  \tGroupNonUniformQuad 68  \tShaderLayer 69  \tShaderViewportIndex 70  \tUniformDecoration 71  \tCoreBuiltinsARM 4165  \tTileImageColorReadAccessEXT 4166  \tTileImageDepthReadAccessEXT 4167  \tTileImageStencilReadAccessEXT 4168  \tTensorsARM 4174  \tStorageTensorArrayDynamicIndexingARM 4175  \tStorageTensorArrayNonUniformIndexingARM 4176  \tGraphARM 4191  \tCooperativeMatrixLayoutsARM 4201  \tFloat8EXT 4212  \tFloat8CooperativeMatrixEXT 4213  \tFloat6EXT 4228  \tFloat4EXT 4229  \tFloat8UnsignedE8M0EXT 4230  \tMXInt8EXT 4231  \tBitcastExtractEXT 4232  \tFragmentShadingRateKHR 4422  \tSubgroupBallotKHR 4423  \tDrawParameters 4427  \tWorkgroupMemoryExplicitLayoutKHR 4428  \tWorkgroupMemoryExplicitLayout8BitAccessKHR 4429  \tWorkgroupMemoryExplicitLayout16BitAccessKHR 4430  \tSubgroupVoteKHR 4431  \tStorageBuffer16BitAccess 4433  StorageUniformBufferBlock16\tUniformAndStorageBuffer16BitAccess 4434  StorageUniform16\tStoragePushConstant16 4435  \tStorageInputOutput16 4436  \tDeviceGroup 4437  \tMultiView 4439  \tVariablePointersStorageBuffer 4441  \tVariablePointers 4442  \tAtomicStorageOps 4445  \tSampleMaskPostDepthCoverage 4447  \tStorageBuffer8BitAccess 4448  \tUniformAndStorageBuffer8BitAccess 4449  \tStoragePushConstant8 4450  \tDenormPreserve 4464  \tDenormFlushToZero 4465  \tSignedZeroInfNanPreserve 4466  \tRoundingModeRTE 4467  \tRoundingModeRTZ 4468  \tRayQueryProvisionalKHR 4471  \tRayQueryKHR 4472  \tUntypedPointersKHR 4473  \tRayTraversalPrimitiveCullingKHR 4478  \tRayTracingKHR 4479  \tTextureSampleWeightedQCOM 4484  \tTextureBoxFilterQCOM 4485  \tTextureBlockMatchQCOM 4486  \tTileShadingQCOM 4495  \tCooperativeMatrixConversionQCOM 4496  \tTextureBlockMatch2QCOM 4498  \tMultipleWaitQueuesQCOM 4539  \tImageGatherLinearQCOM 4543  \tImageGatherExtendedModesQCOM 4544  \tFloat16ImageAMD 5008  \tImageGatherBiasLodAMD 5009  \tFragmentMaskAMD 5010  \tStencilExportEXT 5013  \tImageReadWriteLodAMD 5015  \tInt64ImageEXT 5016  \tShaderClockKHR 5055  \tShaderEnqueueAMDX 5067  \tQuadControlKHR 5087  \tInt4TypeINTEL 5112  \tInt4CooperativeMatrixINTEL 5114  \tBFloat16TypeKHR 5116  \tBFloat16DotProductKHR 5117  \tBFloat16CooperativeMatrixKHR 5118  \tAbortKHR 5120  \tDescriptorHeapEXT 5128  \tConstantDataKHR 5146  \tPoisonFreezeKHR 5156  \tWeakLinkageAMD 5181  \tSampleMaskOverrideCoverageNV 5249  \tGeometryShaderPassthroughNV 5251  \tShaderViewportIndexLayerEXT 5254  ShaderViewportIndexLayerNV\tShaderViewportMaskNV 5255  \tShaderStereoViewNV 5259  \tPerViewAttributesNV 5260  \tFragmentFullyCoveredEXT 5265  \tMeshShadingNV 5266  \tImageFootprintNV 5282  \tMeshShadingEXT 5283  \tFragmentBarycentricKHR 5284  FragmentBarycentricNV\tComputeDerivativeGroupQuadsKHR 5288  ComputeDerivativeGroupQuadsNV\tFragmentDensityEXT 5291  ShadingRateNV\tGroupNonUniformPartitionedEXT 5297  GroupNonUniformPartitionedNV\tShaderNonUniform 5301  ShaderNonUniformEXT\tRuntimeDescriptorArray 5302  RuntimeDescriptorArrayEXT\tInputAttachmentArrayDynamicIndexing 5303  InputAttachmentArrayDynamicIndexingEXT\tUniformTexelBufferArrayDynamicIndexing 5304  UniformTexelBufferArrayDynamicIndexingEXT\tStorageTexelBufferArrayDynamicIndexing 5305  StorageTexelBufferArrayDynamicIndexingEXT\tUniformBufferArrayNonUniformIndexing 5306  UniformBufferArrayNonUniformIndexingEXT\tSampledImageArrayNonUniformIndexing 5307  SampledImageArrayNonUniformIndexingEXT\tStorageBufferArrayNonUniformIndexing 5308  StorageBufferArrayNonUniformIndexingEXT\tStorageImageArrayNonUniformIndexing 5309  StorageImageArrayNonUniformIndexingEXT\tInputAttachmentArrayNonUniformIndexing 5310  InputAttachmentArrayNonUniformIndexingEXT\tUniformTexelBufferArrayNonUniformIndexing 5311  UniformTexelBufferArrayNonUniformIndexingEXT\tStorageTexelBufferArrayNonUniformIndexing 5312  StorageTexelBufferArrayNonUniformIndexingEXT\tRayTracingPositionFetchKHR 5336  \tRayTracingNV 5340  \tRayTracingMotionBlurNV 5341  \tVulkanMemoryModel 5345  VulkanMemoryModelKHR\tVulkanMemoryModelDeviceScope 5346  VulkanMemoryModelDeviceScopeKHR\tPhysicalStorageBufferAddresses 5347  PhysicalStorageBufferAddressesEXT\tComputeDerivativeGroupLinearKHR 5350  ComputeDerivativeGroupLinearNV\tRayTracingProvisionalKHR 5353  \tCooperativeMatrixNV 5357  \tFragmentShaderSampleInterlockEXT 5363  \tFragmentShaderShadingRateInterlockEXT 5372  \tShaderSMBuiltinsNV 5373  \tFragmentShaderPixelInterlockEXT 5378  \tDemoteToHelperInvocation 5379  DemoteToHelperInvocationEXT\tDisplacementMicromapNV 5380  \tRayTracingOpacityMicromapKHR 5381  RayTracingOpacityMicromapEXT\tShaderInvocationReorderNV 5383  \tShaderInvocationReorderEXT 5388  \tBindlessTextureNV 5390  \tRayQueryPositionFetchKHR 5391  \tCooperativeVectorNV 5394  \tAtomicFloat16VectorNV 5404  \tRayTracingDisplacementMicromapNV 5409  \tRawAccessChainsNV 5414  \tRayTracingSpheresGeometryNV 5418  \tRayTracingLinearSweptSpheresGeometryNV 5419  \tPushConstantBanksNV 5423  \tLongVectorEXT 5425  \tShader64BitIndexingEXT 5426  \tCooperativeMatrixReductionsNV 5430  \tCooperativeMatrixConversionsNV 5431  \tCooperativeMatrixPerElementOperationsNV 5432  \tCooperativeMatrixTensorAddressingNV 5433  \tCooperativeMatrixBlockLoadsNV 5434  \tCooperativeVectorTrainingNV 5435  \tRayTracingClusterAccelerationStructureNV 5437  \tTensorAddressingNV 5439  \tCooperativeMatrixDecodeVectorNV 5447  \tSubgroupShuffleINTEL 5568  \tSubgroupBufferBlockIOINTEL 5569  \tSubgroupImageBlockIOINTEL 5570  \tSubgroupImageMediaBlockIOINTEL 5579  \tRoundToInfinityINTEL 5582  \tFloatingPointModeINTEL 5583  \tIntegerFunctions2INTEL 5584  \tFunctionPointersINTEL 5603  \tIndirectReferencesINTEL 5604  \tAsmINTEL 5606  \tAtomicFloat32MinMaxEXT 5612  \tAtomicFloat64MinMaxEXT 5613  \tAtomicFloat16MinMaxEXT 5616  \tVectorComputeINTEL 5617  \tVectorAnyINTEL 5619  \tExpectAssumeKHR 5629  \tSubgroupAvcMotionEstimationINTEL 5696  \tSubgroupAvcMotionEstimationIntraINTEL 5697  \tSubgroupAvcMotionEstimationChromaINTEL 5698  \tVariableLengthArrayINTEL 5817  \tFunctionFloatControlINTEL 5821  \tFPGAMemoryAttributesALTERA 5824  FPGAMemoryAttributesINTEL\tFPFastMathModeINTEL 5837  \tArbitraryPrecisionIntegersALTERA 5844  ArbitraryPrecisionIntegersINTEL\tArbitraryPrecisionFloatingPointALTERA 5845  ArbitraryPrecisionFloatingPointINTEL\tUnstructuredLoopControlsINTEL 5886  \tFPGALoopControlsALTERA 5888  FPGALoopControlsINTEL\tKernelAttributesINTEL 5892  \tFPGAKernelAttributesINTEL 5897  \tFPGAMemoryAccessesALTERA 5898  FPGAMemoryAccessesINTEL\tFPGAClusterAttributesALTERA 5904  FPGAClusterAttributesINTEL\tLoopFuseALTERA 5906  LoopFuseINTEL\tFPGADSPControlALTERA 5908  FPGADSPControlINTEL\tMemoryAccessAliasingINTEL 5910  \tFPGAInvocationPipeliningAttributesALTERA 5916  FPGAInvocationPipeliningAttributesINTEL\tFPGABufferLocationALTERA 5920  FPGABufferLocationINTEL\tArbitraryPrecisionFixedPointALTERA 5922  ArbitraryPrecisionFixedPointINTEL\tUSMStorageClassesALTERA 5935  USMStorageClassesINTEL\tRuntimeAlignedAttributeALTERA 5939  RuntimeAlignedAttributeINTEL\tIOPipesALTERA 5943  IOPipesINTEL\tBlockingPipesALTERA 5945  BlockingPipesINTEL\tFPGARegALTERA 5948  FPGARegINTEL\tDotProductInputAll 6016  DotProductInputAllKHR\tDotProductInput4x8Bit 6017  DotProductInput4x8BitKHR\tDotProductInput4x8BitPacked 6018  DotProductInput4x8BitPackedKHR\tDotProduct 6019  DotProductKHR\tRayCullMaskKHR 6020  \tCooperativeMatrixKHR 6022  \tReplicatedCompositesEXT 6024  \tBitInstructions 6025  \tGroupNonUniformRotateKHR 6026  \tFloatControls2 6029  \tFMAKHR 6030  \tRayTracingOpacityMicromapExecutionModeKHR 6032  \tAtomicFloat32AddEXT 6033  \tAtomicFloat64AddEXT 6034  \tLongCompositesINTEL 6089  \tOptNoneEXT 6094  OptNoneINTEL\tAtomicFloat16AddEXT 6095  \tDebugInfoModuleINTEL 6114  \tBFloat16ConversionINTEL 6115  \tSplitBarrierEXT 6141  SplitBarrierINTEL\tArithmeticFenceEXT 6144  \tFPGAClusterAttributesV2ALTERA 6150  FPGAClusterAttributesV2INTEL\tFPGAKernelAttributesv2INTEL 6161  \tTaskSequenceALTERA 6162  TaskSequenceINTEL\tFPMaxErrorINTEL 6169  \tFPGALatencyControlALTERA 6171  FPGALatencyControlINTEL\tFPGAArgumentInterfacesALTERA 6174  FPGAArgumentInterfacesINTEL\tGlobalVariableHostAccessINTEL 6187  \tGlobalVariableFPGADecorationsALTERA 6189  GlobalVariableFPGADecorationsINTEL\tSubgroupBufferPrefetchINTEL 6220  \tSubgroup2DBlockIOINTEL 6228  \tSubgroup2DBlockTransformINTEL 6229  \tSubgroup2DBlockTransposeINTEL 6230  \tSubgroupMatrixMultiplyAccumulateINTEL 6236  \tTernaryBitwiseFunctionINTEL 6241  \tUntypedVariableLengthArrayINTEL 6243  \tSpecConditionalINTEL 6245  \tFunctionVariantsINTEL 6246  \tPredicatedIOINTEL 6257  \tRoundedDivideSqrtINTEL 6265  \tGroupUniformArithmeticKHR 6400  \tTensorFloat32RoundingINTEL 6425  \tMaskedGatherScatterINTEL 6427  \tCacheControlsINTEL 6441  \tRegisterLimitsINTEL 6460  \tBindlessImagesINTEL 6528  \tDotProductFloat16AccFloat32VALVE 6912  \tDotProductFloat16AccFloat16VALVE 6913  \tDotProductBFloat16AccVALVE 6914  \tDotProductFloat8AccFloat32VALVE 6915  \tIntrinsicSAMSUNG 7041  \n",
      "RayQueryIntersection\tRayQueryCandidateIntersectionKHR 0  \tRayQueryCommittedIntersectionKHR 1  \nRayQueryCommittedIntersectionType\tRayQueryCommittedIntersectionNoneKHR 0  \tRayQueryCommittedIntersectionTriangleKHR 1  \tRayQueryCommittedIntersectionGeneratedKHR 2  \nRayQueryCandidateIntersectionType\tRayQueryCandidateIntersectionTriangleKHR 0  \tRayQueryCandidateIntersectionAABBKHR 1  \nPackedVectorFormat\tPackedVectorFormat4x8Bit 0  PackedVectorFormat4x8BitKHR\nCooperativeMatrixOperands\tNoneKHR 0  \tMatrixASignedComponentsKHR 1  \tMatrixBSignedComponentsKHR 2  \tMatrixCSignedComponentsKHR 4  \tMatrixResultSignedComponentsKHR 8  \tSaturatingAccumulationKHR 16  \nCooperativeMatrixLayout\tRowMajorKHR 0  \tColumnMajorKHR 1  \tRowBlockedInterleavedARM 4202  \tColumnBlockedInterleavedARM 4203  \nCooperativeMatrixUse\tMatrixAKHR 0  \tMatrixBKHR 1  \tMatrixAccumulatorKHR 2  \nCooperativeMatrixReduce\tRow 1  \tColumn 2  \t2x2 4  \nTensorClampMode\tUndefined 0  \tConstant 1  \tClampToEdge 2  \tRepeat 3  \tRepeatMirrored 4  \nTensorAddressingOperands\tNone 0  \tTensorView 1 IdRef \tDecodeFunc 2 IdRef \tDecodeVectorFunc 4 IdRef \nInitializationModeQualifier\tInitOnDeviceReprogramALTERA 0  InitOnDeviceReprogramINTEL\tInitOnDeviceResetALTERA 1  InitOnDeviceResetINTEL\nLoadCacheControl\tUncachedINTEL 0  \tCachedINTEL 1  \tStreamingINTEL 2  \tInvalidateAfterReadINTEL 3  \tConstCachedINTEL 4  \nStoreCacheControl\tUncachedINTEL 0  \tWriteThroughINTEL 1  \tWriteBackINTEL 2  \tStreamingINTEL 3  \nNamedMaximumNumberOfRegisters\tAutoINTEL 0  \nMatrixMultiplyAccumulateOperands\tNone 0  \tMatrixASignedComponentsINTEL 1  \tMatrixBSignedComponentsINTEL 2  \tMatrixCBFloat16INTEL 4  \tMatrixResultBFloat16INTEL 8  \tMatrixAPackedInt8INTEL 16  \tMatrixBPackedInt8INTEL 32  \tMatrixAPackedInt4INTEL 64  \tMatrixBPackedInt4INTEL 128  \tMatrixATF32INTEL 256  \tMatrixBTF32INTEL 512  \tMatrixAPackedFloat16INTEL 1024  \tMatrixBPackedFloat16INTEL 2048  \tMatrixAPackedBFloat16INTEL 4096  \tMatrixBPackedBFloat16INTEL 8192  \nFPEncoding\tBFloat16KHR 0  \tFloat8E4M3EXT 4214  \tFloat8E5M2EXT 4215  \tFloat6E2M3EXT 4223  \tFloat6E3M2EXT 4224  \tFloat4E2M1EXT 4225  \tFloat8UnsignedE8M0EXT 4226  \tMXInt8EXT 4227  \nCooperativeVectorMatrixLayout\tRowMajorNV 0  \tColumnMajorNV 1  \tInferencingOptimalNV 2  \tTrainingOptimalNV 3  \nComponentType\tFloat16NV 0  \tFloat32NV 1  \tFloat64NV 2  \tSignedInt8NV 3  \tSignedInt16NV 4  \tSignedInt32NV 5  \tSignedInt64NV 6  \tUnsignedInt8NV 7  \tUnsignedInt16NV 8  \tUnsignedInt32NV 9  \tUnsignedInt64NV 10  \tSignedInt8PackedNV 1000491000  \tUnsignedInt8PackedNV 1000491001  \tFloatE4M3NV 1000491002  \tFloatE5M2NV 1000491003  \nGatherModes\tGather4x1QCOM 0  \tGatherDQCOM 1  \tGatherH2QCOM 2  \tGatherV2QCOM 3  \nTensorOperands\tNoneARM 0  \tNontemporalARM 1  \tOutOfBoundsValueARM 2 IdRef \tMakeElementAvailableARM 4 IdRef \tMakeElementVisibleARM 8 IdRef \tNonPrivateElementARM 16  \n"
    ).flatMap(_.split('\n').toVec).view.map(row => OperandKindInfo.parse(row)).map(value => value.kind -> value).toMap

  private val aliases: Map[java.lang.String, Definition] = Map(
    "OpSDotKHR" -> Definition.SDot,
    "OpUDotKHR" -> Definition.UDot,
    "OpSUDotKHR" -> Definition.SUDot,
    "OpSDotAccSatKHR" -> Definition.SDotAccSat,
    "OpUDotAccSatKHR" -> Definition.UDotAccSat,
    "OpSUDotAccSatKHR" -> Definition.SUDotAccSat,
    "OpTypeCooperativeVectorNV" -> Definition.TypeVectorIdEXT,
    "OpGroupNonUniformPartitionNV" -> Definition.GroupNonUniformPartitionEXT,
    "OpReportIntersectionNV" -> Definition.ReportIntersectionKHR,
    "OpTypeAccelerationStructureNV" -> Definition.TypeAccelerationStructureKHR,
    "OpRayQueryGetClusterIdNV" -> Definition.RayQueryGetIntersectionClusterIdNV,
    "OpDemoteToHelperInvocationEXT" -> Definition.DemoteToHelperInvocation,
    "OpDecorateStringGOOGLE" -> Definition.DecorateString,
    "OpMemberDecorateStringGOOGLE" -> Definition.MemberDecorateString,
    "OpArbitraryFloatSinCosPiINTEL" -> Definition.ArbitraryFloatSinCosPiALTERA,
    "OpArbitraryFloatCastINTEL" -> Definition.ArbitraryFloatCastALTERA,
    "OpArbitraryFloatCastFromIntINTEL" -> Definition.ArbitraryFloatCastFromIntALTERA,
    "OpArbitraryFloatCastToIntINTEL" -> Definition.ArbitraryFloatCastToIntALTERA,
    "OpArbitraryFloatAddINTEL" -> Definition.ArbitraryFloatAddALTERA,
    "OpArbitraryFloatSubINTEL" -> Definition.ArbitraryFloatSubALTERA,
    "OpArbitraryFloatMulINTEL" -> Definition.ArbitraryFloatMulALTERA,
    "OpArbitraryFloatDivINTEL" -> Definition.ArbitraryFloatDivALTERA,
    "OpArbitraryFloatGTINTEL" -> Definition.ArbitraryFloatGTALTERA,
    "OpArbitraryFloatGEINTEL" -> Definition.ArbitraryFloatGEALTERA,
    "OpArbitraryFloatLTINTEL" -> Definition.ArbitraryFloatLTALTERA,
    "OpArbitraryFloatLEINTEL" -> Definition.ArbitraryFloatLEALTERA,
    "OpArbitraryFloatEQINTEL" -> Definition.ArbitraryFloatEQALTERA,
    "OpArbitraryFloatRecipINTEL" -> Definition.ArbitraryFloatRecipALTERA,
    "OpArbitraryFloatRSqrtINTEL" -> Definition.ArbitraryFloatRSqrtALTERA,
    "OpArbitraryFloatCbrtINTEL" -> Definition.ArbitraryFloatCbrtALTERA,
    "OpArbitraryFloatHypotINTEL" -> Definition.ArbitraryFloatHypotALTERA,
    "OpArbitraryFloatSqrtINTEL" -> Definition.ArbitraryFloatSqrtALTERA,
    "OpFixedSqrtINTEL" -> Definition.FixedSqrtALTERA,
    "OpFixedRecipINTEL" -> Definition.FixedRecipALTERA,
    "OpFixedRsqrtINTEL" -> Definition.FixedRsqrtALTERA,
    "OpFixedSinINTEL" -> Definition.FixedSinALTERA,
    "OpFixedCosINTEL" -> Definition.FixedCosALTERA,
    "OpFixedSinCosINTEL" -> Definition.FixedSinCosALTERA,
    "OpFixedSinPiINTEL" -> Definition.FixedSinPiALTERA,
    "OpFixedCosPiINTEL" -> Definition.FixedCosPiALTERA,
    "OpFixedSinCosPiINTEL" -> Definition.FixedSinCosPiALTERA,
    "OpFixedLogINTEL" -> Definition.FixedLogALTERA,
    "OpFixedExpINTEL" -> Definition.FixedExpALTERA,
    "OpPtrCastToCrossWorkgroupINTEL" -> Definition.PtrCastToCrossWorkgroupALTERA,
    "OpCrossWorkgroupCastToPtrINTEL" -> Definition.CrossWorkgroupCastToPtrALTERA,
    "OpReadPipeBlockingINTEL" -> Definition.ReadPipeBlockingALTERA,
    "OpWritePipeBlockingINTEL" -> Definition.WritePipeBlockingALTERA,
    "OpFPGARegINTEL" -> Definition.FPGARegALTERA,
    "OpControlBarrierArriveINTEL" -> Definition.ControlBarrierArriveEXT,
    "OpControlBarrierWaitINTEL" -> Definition.ControlBarrierWaitEXT,
    "OpTaskSequenceCreateINTEL" -> Definition.TaskSequenceCreateALTERA,
    "OpTaskSequenceAsyncINTEL" -> Definition.TaskSequenceAsyncALTERA,
    "OpTaskSequenceGetINTEL" -> Definition.TaskSequenceGetALTERA,
    "OpTaskSequenceReleaseINTEL" -> Definition.TaskSequenceReleaseALTERA,
    "OpTypeTaskSequenceINTEL" -> Definition.TypeTaskSequenceALTERA
  )
  private val byNumber: Map[SPIRVOpcodeNumber, Vec[Definition]] =
    definitions.groupMap(_.opcodeNumber)(value => value)
  private val byOpcode: Map[NonEmpty[java.lang.String], Vec[Definition]] =
    definitions.groupMap(_.opcode)(value => value)

  def definitionsFor(opcodeNumber: SPIRVOpcodeNumber): Vec[Definition] =
    byNumber.getOrElse(opcodeNumber, Vec.empty[Definition])
  def definitionsFor(opcode: NonEmpty[java.lang.String]): Vec[Definition] =
    byOpcode.getOrElse(opcode, aliases.get(opcode).toVec)

  def decode(raw: RawInstruction): Either[InstructionDecodeError, Instruction] = {
    val candidates = definitionsFor(raw.opcodeNumber)
    candidates match {
      case Vec() => Left(InstructionDecodeError.UnknownOpcode(raw.opcodeNumber))
      case Vec(definition) => definition.decoder(raw)
      case _ =>
        val matches = candidates.flatMap(definition =>
          definition.decoder(raw).toOption.map(instruction => definition -> instruction)
        )
        matches match {
          case Vec((_, instruction)) => Right(instruction)
          case Vec() =>
            Left(InstructionDecodeError.NoMatchingDefinition(
              raw.opcodeNumber,
              Vec.nonEmpty(candidates.head.opcode, candidates.tail.map(_.opcode)*)
            ))
          case values =>
            Left(InstructionDecodeError.AmbiguousDefinition(
              raw.opcodeNumber,
              Vec.nonEmpty(values.head._1.opcode, values.tail.map(_._1.opcode)*)
            ))
        }
    }
  }

  def encode(instruction: Instruction): RawInstruction =
    RawInstruction(instruction.definition.opcodeNumber, instruction.operands.flatMap(_.words))

  private def decodeDefinition(
      definition: Definition,
      raw: RawInstruction,
      construct: DecodedOperands => Instruction
  ): Either[InstructionDecodeError, Instruction] =
    if (raw.opcodeNumber != definition.opcodeNumber)
      Left(InstructionDecodeError.DecoderOpcodeMismatch(definition.opcodeNumber, raw.opcodeNumber))
    else {
      val decoded = OperandDecoder.decode(definition.opcode, raw.operandWords, definition.shapes, operandKinds)
      // The grammar uses 32-bit switch literals; retain 64-bit cases until the selector type is resolved.
      val operands = if (definition == Definition.Switch)
        decoded.orElse(OperandDecoder.decodeSwitch64(definition.opcode, raw.operandWords).orElse(decoded))
      else decoded
      operands.map(construct)
    }
}

@Generated(Array("sbt codegen"))
enum Instruction(val definition: Instruction.Definition, operandValues: Operand*) derives CanEqual {
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

  def operands: Vec[Operand] = operandValues.toVec
  final def opcode: NonEmpty[java.lang.String] = definition.opcode
  final def opcodeNumber: SPIRVOpcodeNumber = definition.opcodeNumber
  final def decoder: Instruction.Decoder = definition.decoder
}
