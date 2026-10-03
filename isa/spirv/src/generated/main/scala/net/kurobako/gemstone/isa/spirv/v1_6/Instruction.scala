// Generated from the pinned Khronos SPIR-V core grammar. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.isa.spirv.v1_6.OperandDecoder.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
object Instruction {
  type Decoder = RawInstruction => Either[InstructionDecodeError, Instruction]

  enum Definition(
      val opcode: NonEmpty[java.lang.String],
      val opcodeNumber: SPIRVOpcodeNumber,
      decode: Definition => Decoder,
      val meta: Metadata
  ) {
    case Nop extends Definition(
          "OpNop",
          SPIRVOpcodeNumber(0),
          definition => raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.Nop()),
          InstructionMetadata0.m0
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
          InstructionMetadata0.m1
        )
    case SourceContinued extends Definition(
          "OpSourceContinued",
          SPIRVOpcodeNumber(2),
          definition =>
            raw =>
              Instruction.decodeDefinition(definition, raw, values => Instruction.SourceContinued(values.required(0))),
          InstructionMetadata0.m2
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
          InstructionMetadata0.m3
        )
    case SourceExtension extends Definition(
          "OpSourceExtension",
          SPIRVOpcodeNumber(4),
          definition =>
            raw =>
              Instruction.decodeDefinition(definition, raw, values => Instruction.SourceExtension(values.required(0))),
          InstructionMetadata0.m4
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
          InstructionMetadata0.m5
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
          InstructionMetadata0.m6
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
          InstructionMetadata0.m7
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
          InstructionMetadata0.m8
        )
    case Extension extends Definition(
          "OpExtension",
          SPIRVOpcodeNumber(10),
          definition =>
            raw => Instruction.decodeDefinition(definition, raw, values => Instruction.Extension(values.required(0))),
          InstructionMetadata0.m9
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
          InstructionMetadata0.m10
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
          "OpMemoryModel",
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
          InstructionMetadata0.m13
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
          InstructionMetadata0.m14
        )
    case Capability extends Definition(
          "OpCapability",
          SPIRVOpcodeNumber(17),
          definition =>
            raw => Instruction.decodeDefinition(definition, raw, values => Instruction.Capability(values.required(0))),
          InstructionMetadata0.m15
        )
    case TypeVoid extends Definition(
          "OpTypeVoid",
          SPIRVOpcodeNumber(19),
          definition =>
            raw => Instruction.decodeDefinition(definition, raw, values => Instruction.TypeVoid(values.required(0))),
          InstructionMetadata0.m16
        )
    case TypeBool extends Definition(
          "OpTypeBool",
          SPIRVOpcodeNumber(20),
          definition =>
            raw => Instruction.decodeDefinition(definition, raw, values => Instruction.TypeBool(values.required(0))),
          InstructionMetadata0.m17
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
          InstructionMetadata0.m18
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
          InstructionMetadata0.m19
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
          InstructionMetadata0.m20
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
          InstructionMetadata0.m21
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
          "OpTypeSampler",
          SPIRVOpcodeNumber(26),
          definition =>
            raw => Instruction.decodeDefinition(definition, raw, values => Instruction.TypeSampler(values.required(0))),
          InstructionMetadata0.m23
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
          InstructionMetadata0.m24
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
          InstructionMetadata0.m25
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
          InstructionMetadata0.m26
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
          InstructionMetadata0.m27
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
          InstructionMetadata0.m28
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
          InstructionMetadata0.m29
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
          InstructionMetadata0.m30
        )
    case TypeEvent extends Definition(
          "OpTypeEvent",
          SPIRVOpcodeNumber(34),
          definition =>
            raw => Instruction.decodeDefinition(definition, raw, values => Instruction.TypeEvent(values.required(0))),
          InstructionMetadata0.m31
        )
    case TypeDeviceEvent extends Definition(
          "OpTypeDeviceEvent",
          SPIRVOpcodeNumber(35),
          definition =>
            raw =>
              Instruction.decodeDefinition(definition, raw, values => Instruction.TypeDeviceEvent(values.required(0))),
          InstructionMetadata0.m32
        )
    case TypeReserveId extends Definition(
          "OpTypeReserveId",
          SPIRVOpcodeNumber(36),
          definition =>
            raw =>
              Instruction.decodeDefinition(definition, raw, values => Instruction.TypeReserveId(values.required(0))),
          InstructionMetadata0.m33
        )
    case TypeQueue extends Definition(
          "OpTypeQueue",
          SPIRVOpcodeNumber(37),
          definition =>
            raw => Instruction.decodeDefinition(definition, raw, values => Instruction.TypeQueue(values.required(0))),
          InstructionMetadata0.m34
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
          InstructionMetadata0.m35
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
          InstructionMetadata0.m36
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
          InstructionMetadata0.m37
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
          InstructionMetadata0.m38
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
          InstructionMetadata0.m39
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
          InstructionMetadata0.m40
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
          "OpConstantNull",
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
          "OpSpecConstantTrue",
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
          "OpSpecConstantFalse",
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
          "OpSpecConstant",
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
          "OpSpecConstantComposite",
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
          InstructionMetadata0.m47
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
          InstructionMetadata0.m48
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
          InstructionMetadata0.m49
        )
    case FunctionEnd extends Definition(
          "OpFunctionEnd",
          SPIRVOpcodeNumber(56),
          definition => raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.FunctionEnd()),
          InstructionMetadata0.m50
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
          InstructionMetadata0.m51
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
          InstructionMetadata0.m52
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
          InstructionMetadata0.m54
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
          InstructionMetadata0.m55
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
          InstructionMetadata0.m56
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
          InstructionMetadata0.m58
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
          InstructionMetadata0.m59
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
          InstructionMetadata0.m61
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
          InstructionMetadata0.m62
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
          "OpDecorate",
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
          "OpMemberDecorate",
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
          "OpDecorationGroup",
          SPIRVOpcodeNumber(73),
          definition =>
            raw =>
              Instruction.decodeDefinition(definition, raw, values => Instruction.DecorationGroup(values.required(0))),
          InstructionMetadata0.m66
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
          InstructionMetadata0.m67
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
          InstructionMetadata0.m68
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
          InstructionMetadata0.m69
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
          "OpVectorShuffle",
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
          "OpCompositeConstruct",
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
          InstructionMetadata0.m73
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
          "OpCopyObject",
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
          "OpTranspose",
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
          InstructionMetadata0.m77
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
          "OpImageSampleExplicitLod",
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
          "OpImageSampleDrefImplicitLod",
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
          "OpImageSampleDrefExplicitLod",
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
          "OpImageSampleProjImplicitLod",
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
          "OpImageSampleProjExplicitLod",
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
          "OpImageSampleProjDrefImplicitLod",
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
          "OpImageSampleProjDrefExplicitLod",
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
          "OpImageFetch",
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
          "OpImageGather",
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
          "OpImageDrefGather",
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
          "OpImageRead",
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
          InstructionMetadata0.m90
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
          InstructionMetadata0.m91
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
          InstructionMetadata0.m92
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
          InstructionMetadata0.m93
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
          InstructionMetadata0.m94
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
          InstructionMetadata0.m95
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
          InstructionMetadata0.m96
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
          InstructionMetadata0.m97
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
          InstructionMetadata0.m98
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
          InstructionMetadata0.m99
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
          InstructionMetadata0.m100
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
          InstructionMetadata0.m101
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
          InstructionMetadata0.m102
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
          InstructionMetadata0.m103
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
          InstructionMetadata0.m104
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
          InstructionMetadata0.m105
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
          InstructionMetadata0.m106
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
          InstructionMetadata0.m107
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
          InstructionMetadata0.m108
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
          InstructionMetadata0.m109
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
          InstructionMetadata0.m110
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
          InstructionMetadata0.m111
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
          InstructionMetadata0.m112
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
          InstructionMetadata0.m113
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
          InstructionMetadata0.m114
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
          InstructionMetadata0.m115
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
          InstructionMetadata0.m116
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
          InstructionMetadata0.m117
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
          InstructionMetadata0.m118
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
          InstructionMetadata0.m119
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
          InstructionMetadata0.m120
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
          InstructionMetadata0.m121
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
          InstructionMetadata0.m122
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
          InstructionMetadata0.m123
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
          InstructionMetadata0.m124
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
          InstructionMetadata0.m125
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
          InstructionMetadata0.m126
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
          InstructionMetadata0.m127
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
          InstructionMetadata0.m128
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
          InstructionMetadata0.m129
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
          InstructionMetadata0.m130
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
          InstructionMetadata0.m131
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
          InstructionMetadata0.m132
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
          InstructionMetadata0.m133
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
          InstructionMetadata0.m134
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
          InstructionMetadata0.m135
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
          InstructionMetadata0.m136
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
          InstructionMetadata0.m137
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
          InstructionMetadata0.m138
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
          InstructionMetadata0.m139
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
          InstructionMetadata0.m140
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
          InstructionMetadata0.m141
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
          InstructionMetadata0.m142
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
          InstructionMetadata0.m143
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
          InstructionMetadata0.m144
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
          InstructionMetadata0.m145
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
          InstructionMetadata0.m146
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
          InstructionMetadata0.m147
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
          InstructionMetadata0.m148
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
          InstructionMetadata0.m149
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
          InstructionMetadata0.m150
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
          InstructionMetadata0.m151
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
          InstructionMetadata0.m152
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
          InstructionMetadata0.m153
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
          InstructionMetadata0.m154
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
          InstructionMetadata0.m155
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
          InstructionMetadata0.m156
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
          InstructionMetadata0.m158
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
          InstructionMetadata0.m159
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
          InstructionMetadata0.m160
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
          InstructionMetadata0.m161
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
          InstructionMetadata0.m162
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
          InstructionMetadata0.m163
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
          InstructionMetadata0.m164
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
          InstructionMetadata0.m165
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
          InstructionMetadata0.m166
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
          InstructionMetadata0.m167
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
          InstructionMetadata0.m168
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
          InstructionMetadata0.m169
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
          InstructionMetadata0.m170
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
          InstructionMetadata0.m171
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
          InstructionMetadata0.m172
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
          InstructionMetadata0.m173
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
          InstructionMetadata0.m174
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
          InstructionMetadata0.m175
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
          InstructionMetadata0.m176
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
          InstructionMetadata0.m177
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
          InstructionMetadata0.m178
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
          InstructionMetadata0.m179
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
          InstructionMetadata0.m180
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
          InstructionMetadata0.m181
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
          InstructionMetadata0.m182
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
          InstructionMetadata0.m183
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
          InstructionMetadata0.m184
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
          InstructionMetadata0.m185
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
          InstructionMetadata0.m186
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
          "OpBitFieldSExtract",
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
          "OpBitFieldUExtract",
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
          "OpBitReverse",
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
          "OpBitCount",
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
          "OpDPdx",
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
          "OpDPdy",
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
          "OpFwidth",
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
          "OpDPdxFine",
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
          "OpDPdyFine",
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
          "OpFwidthFine",
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
          "OpDPdxCoarse",
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
          "OpDPdyCoarse",
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
          "OpFwidthCoarse",
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
          "OpEmitVertex",
          SPIRVOpcodeNumber(218),
          definition => raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.EmitVertex()),
          InstructionMetadata1.m1
        )
    case EndPrimitive extends Definition(
          "OpEndPrimitive",
          SPIRVOpcodeNumber(219),
          definition => raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.EndPrimitive()),
          InstructionMetadata1.m2
        )
    case EmitStreamVertex extends Definition(
          "OpEmitStreamVertex",
          SPIRVOpcodeNumber(220),
          definition =>
            raw =>
              Instruction.decodeDefinition(definition, raw, values => Instruction.EmitStreamVertex(values.required(0))),
          InstructionMetadata1.m3
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
          InstructionMetadata1.m4
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
          InstructionMetadata1.m5
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
          InstructionMetadata1.m6
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
          InstructionMetadata1.m8
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
          "OpAtomicCompareExchange",
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
          "OpAtomicCompareExchangeWeak",
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
          "OpAtomicIIncrement",
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
          "OpAtomicIDecrement",
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
          "OpAtomicIAdd",
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
          "OpAtomicISub",
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
          "OpAtomicSMin",
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
          "OpAtomicUMin",
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
          "OpAtomicSMax",
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
          "OpAtomicUMax",
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
          "OpAtomicAnd",
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
          "OpAtomicOr",
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
          "OpAtomicXor",
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
          "OpPhi",
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
          "OpLoopMerge",
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
          "OpSelectionMerge",
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
          "OpLabel",
          SPIRVOpcodeNumber(248),
          definition =>
            raw => Instruction.decodeDefinition(definition, raw, values => Instruction.Label(values.required(0))),
          InstructionMetadata1.m26
        )
    case Branch extends Definition(
          "OpBranch",
          SPIRVOpcodeNumber(249),
          definition =>
            raw => Instruction.decodeDefinition(definition, raw, values => Instruction.Branch(values.required(0))),
          InstructionMetadata1.m27
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
          InstructionMetadata1.m28
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
          InstructionMetadata1.m29
        )
    case Kill extends Definition(
          "OpKill",
          SPIRVOpcodeNumber(252),
          definition => raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.Kill()),
          InstructionMetadata1.m30
        )
    case Return extends Definition(
          "OpReturn",
          SPIRVOpcodeNumber(253),
          definition => raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.Return()),
          InstructionMetadata1.m31
        )
    case ReturnValue extends Definition(
          "OpReturnValue",
          SPIRVOpcodeNumber(254),
          definition =>
            raw => Instruction.decodeDefinition(definition, raw, values => Instruction.ReturnValue(values.required(0))),
          InstructionMetadata1.m32
        )
    case Unreachable extends Definition(
          "OpUnreachable",
          SPIRVOpcodeNumber(255),
          definition => raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.Unreachable()),
          InstructionMetadata1.m33
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
          InstructionMetadata1.m34
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
          InstructionMetadata1.m35
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
          "OpGroupWaitEvents",
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
          InstructionMetadata1.m38
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
          InstructionMetadata1.m39
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
          "OpGroupIAdd",
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
          "OpGroupFAdd",
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
          "OpGroupFMin",
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
          "OpGroupUMin",
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
          "OpGroupSMin",
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
          "OpGroupFMax",
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
          "OpGroupUMax",
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
          "OpGroupSMax",
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
          "OpReadPipe",
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
          "OpWritePipe",
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
          "OpReservedReadPipe",
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
          "OpReservedWritePipe",
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
          "OpReserveReadPipePackets",
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
          "OpReserveWritePipePackets",
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
          InstructionMetadata1.m55
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
          InstructionMetadata1.m56
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
          InstructionMetadata1.m57
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
          "OpGetMaxPipePackets",
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
          "OpGroupReserveReadPipePackets",
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
          "OpGroupReserveWritePipePackets",
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
          "OpGroupCommitReadPipe",
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
          "OpGroupCommitWritePipe",
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
          "OpEnqueueMarker",
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
          "OpEnqueueKernel",
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
          "OpGetKernelNDrangeSubGroupCount",
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
          "OpGetKernelNDrangeMaxSubGroupSize",
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
          "OpGetKernelWorkGroupSize",
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
          "OpGetKernelPreferredWorkGroupSizeMultiple",
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
          "OpRetainEvent",
          SPIRVOpcodeNumber(297),
          definition =>
            raw => Instruction.decodeDefinition(definition, raw, values => Instruction.RetainEvent(values.required(0))),
          InstructionMetadata1.m70
        )
    case ReleaseEvent extends Definition(
          "OpReleaseEvent",
          SPIRVOpcodeNumber(298),
          definition =>
            raw =>
              Instruction.decodeDefinition(definition, raw, values => Instruction.ReleaseEvent(values.required(0))),
          InstructionMetadata1.m71
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
          InstructionMetadata1.m72
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
          InstructionMetadata1.m73
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
          InstructionMetadata1.m74
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
          InstructionMetadata1.m75
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
          InstructionMetadata1.m76
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
          "OpImageSparseSampleImplicitLod",
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
          "OpImageSparseSampleExplicitLod",
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
          "OpImageSparseSampleDrefImplicitLod",
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
          "OpImageSparseSampleDrefExplicitLod",
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
          "OpImageSparseSampleProjImplicitLod",
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
          "OpImageSparseSampleProjExplicitLod",
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
          "OpImageSparseSampleProjDrefImplicitLod",
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
          "OpImageSparseSampleProjDrefExplicitLod",
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
          "OpImageSparseFetch",
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
          "OpImageSparseGather",
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
          "OpImageSparseDrefGather",
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
          InstructionMetadata1.m89
        )
    case NoLine extends Definition(
          "OpNoLine",
          SPIRVOpcodeNumber(317),
          definition => raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.NoLine()),
          InstructionMetadata1.m90
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
          "OpAtomicFlagClear",
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
          "OpImageSparseRead",
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
          "OpSizeOf",
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
          "OpTypePipeStorage",
          SPIRVOpcodeNumber(322),
          definition =>
            raw =>
              Instruction.decodeDefinition(definition, raw, values => Instruction.TypePipeStorage(values.required(0))),
          InstructionMetadata1.m95
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
          InstructionMetadata1.m97
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
          "OpGetKernelMaxNumSubgroups",
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
          "OpTypeNamedBarrier",
          SPIRVOpcodeNumber(327),
          definition =>
            raw =>
              Instruction.decodeDefinition(definition, raw, values => Instruction.TypeNamedBarrier(values.required(0))),
          InstructionMetadata1.m100
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
          InstructionMetadata1.m101
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
          InstructionMetadata1.m102
        )
    case ModuleProcessed extends Definition(
          "OpModuleProcessed",
          SPIRVOpcodeNumber(330),
          definition =>
            raw =>
              Instruction.decodeDefinition(definition, raw, values => Instruction.ModuleProcessed(values.required(0))),
          InstructionMetadata1.m103
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
          InstructionMetadata1.m104
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
          InstructionMetadata1.m105
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
          InstructionMetadata1.m106
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
          InstructionMetadata1.m107
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
          InstructionMetadata1.m108
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
          InstructionMetadata1.m109
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
          InstructionMetadata1.m111
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
          InstructionMetadata1.m112
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
          InstructionMetadata1.m113
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
          "OpGroupNonUniformBallotBitCount",
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
          InstructionMetadata1.m116
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
          InstructionMetadata1.m117
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
          "OpGroupNonUniformShuffleXor",
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
          "OpGroupNonUniformShuffleUp",
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
          "OpGroupNonUniformShuffleDown",
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
          "OpGroupNonUniformIAdd",
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
          "OpGroupNonUniformFAdd",
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
          "OpGroupNonUniformIMul",
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
          "OpGroupNonUniformFMul",
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
          "OpGroupNonUniformSMin",
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
          "OpGroupNonUniformUMin",
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
          "OpGroupNonUniformFMin",
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
          "OpGroupNonUniformSMax",
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
          "OpGroupNonUniformUMax",
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
          "OpGroupNonUniformFMax",
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
          "OpGroupNonUniformBitwiseAnd",
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
          "OpGroupNonUniformBitwiseOr",
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
          "OpGroupNonUniformBitwiseXor",
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
          "OpGroupNonUniformLogicalAnd",
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
          "OpGroupNonUniformLogicalOr",
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
          "OpGroupNonUniformLogicalXor",
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
          "OpGroupNonUniformQuadBroadcast",
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
          "OpGroupNonUniformQuadSwap",
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
          "OpCopyLogical",
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
          InstructionMetadata1.m141
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
          InstructionMetadata1.m142
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
          InstructionMetadata1.m143
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
          InstructionMetadata1.m144
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
          InstructionMetadata1.m145
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
          InstructionMetadata1.m146
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
          InstructionMetadata1.m147
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
          InstructionMetadata1.m149
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
          InstructionMetadata1.m150
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
          InstructionMetadata1.m151
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
          InstructionMetadata1.m152
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
          InstructionMetadata1.m153
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
          InstructionMetadata1.m154
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
          InstructionMetadata1.m155
        )
    case GraphEndARM extends Definition(
          "OpGraphEndARM",
          SPIRVOpcodeNumber(4186),
          definition => raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.GraphEndARM()),
          InstructionMetadata1.m156
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
          InstructionMetadata1.m157
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
          InstructionMetadata1.m158
        )
    case TerminateInvocation extends Definition(
          "OpTerminateInvocation",
          SPIRVOpcodeNumber(4416),
          definition => raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.TerminateInvocation()),
          InstructionMetadata1.m159
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
          InstructionMetadata1.m160
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
          "OpUntypedAccessChainKHR",
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
          "OpUntypedInBoundsAccessChainKHR",
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
          "OpSubgroupBallotKHR",
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
          InstructionMetadata1.m165
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
          "OpUntypedInBoundsPtrAccessChainKHR",
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
          "OpUntypedArrayLengthKHR",
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
          "OpUntypedPrefetchKHR",
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
          "OpFmaKHR",
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
          "OpSubgroupAllKHR",
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
          "OpSubgroupAnyKHR",
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
          "OpSubgroupAllEqualKHR",
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
          "OpGroupNonUniformRotateKHR",
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
          InstructionMetadata1.m175
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
          "OpUntypedGroupAsyncCopyKHR",
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
          "OpTraceRayKHR",
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
          "OpExecuteCallableKHR",
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
          InstructionMetadata1.m180
        )
    case IgnoreIntersectionKHR extends Definition(
          "OpIgnoreIntersectionKHR",
          SPIRVOpcodeNumber(4448),
          definition => raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.IgnoreIntersectionKHR()),
          InstructionMetadata1.m181
        )
    case TerminateRayKHR extends Definition(
          "OpTerminateRayKHR",
          SPIRVOpcodeNumber(4449),
          definition => raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.TerminateRayKHR()),
          InstructionMetadata1.m182
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
          "OpUDot",
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
          "OpSUDot",
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
          "OpSDotAccSat",
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
          "OpUDotAccSat",
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
          "OpSUDotAccSat",
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
          "OpTypeCooperativeMatrixKHR",
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
          "OpCooperativeMatrixLoadKHR",
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
          "OpCooperativeMatrixStoreKHR",
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
          "OpCooperativeMatrixMulAddKHR",
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
          InstructionMetadata1.m193
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
          InstructionMetadata1.m194
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
          InstructionMetadata1.m195
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
          InstructionMetadata1.m196
        )
    case TypeRayQueryKHR extends Definition(
          "OpTypeRayQueryKHR",
          SPIRVOpcodeNumber(4472),
          definition =>
            raw =>
              Instruction.decodeDefinition(definition, raw, values => Instruction.TypeRayQueryKHR(values.required(0))),
          InstructionMetadata1.m197
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
          "OpRayQueryTerminateKHR",
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
          "OpRayQueryGenerateIntersectionKHR",
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
          "OpRayQueryConfirmIntersectionKHR",
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
          "OpRayQueryProceedKHR",
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
          InstructionMetadata2.m3
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
          "OpImageBoxFilterQCOM",
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
          "OpImageBlockMatchSSDQCOM",
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
          "OpImageBlockMatchSADQCOM",
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
          "OpBitCastArrayQCOM",
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
          "OpImageBlockMatchWindowSSDQCOM",
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
          "OpImageBlockMatchWindowSADQCOM",
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
          "OpImageBlockMatchGatherSSDQCOM",
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
          "OpImageBlockMatchGatherSADQCOM",
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
          InstructionMetadata2.m13
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
          InstructionMetadata2.m14
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
          InstructionMetadata2.m15
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
          "OpGroupIAddNonUniformAMD",
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
          "OpGroupFAddNonUniformAMD",
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
          "OpGroupFMinNonUniformAMD",
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
          "OpGroupUMinNonUniformAMD",
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
          "OpGroupSMinNonUniformAMD",
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
          "OpGroupFMaxNonUniformAMD",
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
          "OpGroupUMaxNonUniformAMD",
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
          "OpGroupSMaxNonUniformAMD",
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
          InstructionMetadata2.m25
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
          "OpReadClockKHR",
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
          "OpAllocateNodePayloadsAMDX",
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
          "OpEnqueueNodePayloadsAMDX",
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
          "OpTypeNodePayloadArrayAMDX",
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
          InstructionMetadata2.m31
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
          InstructionMetadata2.m32
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
          InstructionMetadata2.m33
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
          InstructionMetadata2.m34
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
          InstructionMetadata2.m35
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
          InstructionMetadata2.m36
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
          InstructionMetadata2.m37
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
          InstructionMetadata2.m38
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
          InstructionMetadata2.m39
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
          InstructionMetadata2.m40
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
          "OpMemberDecorateIdEXT",
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
          "OpConstantSizeOfEXT",
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
          "OpConstantDataKHR",
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
          "OpSpecConstantDataKHR",
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
          "OpPoisonKHR",
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
          "OpFreezeKHR",
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
          "OpHitObjectRecordHitMotionNV",
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
          "OpHitObjectRecordHitWithIndexMotionNV",
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
          "OpHitObjectRecordMissMotionNV",
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
          InstructionMetadata2.m51
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
          InstructionMetadata2.m52
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
          InstructionMetadata2.m53
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
          InstructionMetadata2.m54
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
          InstructionMetadata2.m56
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
          InstructionMetadata2.m57
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
          InstructionMetadata2.m58
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
          "OpHitObjectRecordHitNV",
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
          "OpHitObjectRecordHitWithIndexNV",
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
          "OpHitObjectRecordMissNV",
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
          "OpHitObjectExecuteShaderNV",
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
          InstructionMetadata2.m64
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
          InstructionMetadata2.m65
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
          InstructionMetadata2.m66
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
          InstructionMetadata2.m67
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
          InstructionMetadata2.m68
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
          InstructionMetadata2.m69
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
          InstructionMetadata2.m70
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
          InstructionMetadata2.m71
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
          InstructionMetadata2.m72
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
          InstructionMetadata2.m73
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
          InstructionMetadata2.m74
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
          InstructionMetadata2.m75
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
          InstructionMetadata2.m76
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
          InstructionMetadata2.m77
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
          InstructionMetadata2.m78
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
          InstructionMetadata2.m79
        )
    case TypeHitObjectNV extends Definition(
          "OpTypeHitObjectNV",
          SPIRVOpcodeNumber(5281),
          definition =>
            raw =>
              Instruction.decodeDefinition(definition, raw, values => Instruction.TypeHitObjectNV(values.required(0))),
          InstructionMetadata2.m80
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
          "OpTypeVectorIdEXT",
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
          "OpCooperativeVectorMatrixMulNV",
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
          "OpCooperativeVectorOuterProductAccumulateNV",
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
          InstructionMetadata2.m85
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
          InstructionMetadata2.m87
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
          InstructionMetadata2.m88
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
          InstructionMetadata2.m89
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
          InstructionMetadata2.m90
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
          InstructionMetadata2.m91
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
          "OpFetchMicroTriangleVertexBarycentricNV",
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
          "OpCooperativeVectorLoadNV",
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
          InstructionMetadata2.m95
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
          "OpHitObjectRecordMissEXT",
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
          "OpHitObjectRecordMissMotionEXT",
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
          InstructionMetadata2.m99
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
          InstructionMetadata2.m100
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
          InstructionMetadata2.m101
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
          InstructionMetadata2.m102
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
          "OpHitObjectTraceMotionReorderExecuteEXT",
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
          "OpTypeHitObjectEXT",
          SPIRVOpcodeNumber(5313),
          definition =>
            raw =>
              Instruction.decodeDefinition(definition, raw, values => Instruction.TypeHitObjectEXT(values.required(0))),
          InstructionMetadata2.m105
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
          InstructionMetadata2.m106
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
          InstructionMetadata2.m107
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
          "OpHitObjectTraceRayMotionEXT",
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
          "OpHitObjectRecordEmptyEXT",
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
          "OpHitObjectExecuteShaderEXT",
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
          InstructionMetadata2.m112
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
          InstructionMetadata2.m113
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
          InstructionMetadata2.m114
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
          InstructionMetadata2.m115
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
          InstructionMetadata2.m116
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
          InstructionMetadata2.m117
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
          InstructionMetadata2.m118
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
          InstructionMetadata2.m119
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
          InstructionMetadata2.m120
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
          InstructionMetadata2.m121
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
          InstructionMetadata2.m122
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
          InstructionMetadata2.m123
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
          InstructionMetadata2.m124
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
          InstructionMetadata2.m125
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
          InstructionMetadata2.m126
        )
    case IgnoreIntersectionNV extends Definition(
          "OpIgnoreIntersectionNV",
          SPIRVOpcodeNumber(5335),
          definition => raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.IgnoreIntersectionNV()),
          InstructionMetadata2.m127
        )
    case TerminateRayNV extends Definition(
          "OpTerminateRayNV",
          SPIRVOpcodeNumber(5336),
          definition => raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.TerminateRayNV()),
          InstructionMetadata2.m128
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
          "OpTraceMotionNV",
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
          "OpTraceRayMotionNV",
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
          InstructionMetadata2.m132
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
          InstructionMetadata2.m133
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
          InstructionMetadata2.m134
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
          InstructionMetadata2.m135
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
          InstructionMetadata2.m136
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
          InstructionMetadata2.m137
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
          InstructionMetadata2.m138
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
          InstructionMetadata2.m139
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
          InstructionMetadata2.m140
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
          InstructionMetadata2.m141
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
          InstructionMetadata2.m142
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
          "OpCooperativeMatrixLoadNV",
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
          "OpCooperativeMatrixStoreNV",
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
          "OpCooperativeMatrixMulAddNV",
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
          InstructionMetadata2.m147
        )
    case BeginInvocationInterlockEXT extends Definition(
          "OpBeginInvocationInterlockEXT",
          SPIRVOpcodeNumber(5364),
          definition =>
            raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.BeginInvocationInterlockEXT()),
          InstructionMetadata2.m148
        )
    case EndInvocationInterlockEXT extends Definition(
          "OpEndInvocationInterlockEXT",
          SPIRVOpcodeNumber(5365),
          definition =>
            raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.EndInvocationInterlockEXT()),
          InstructionMetadata2.m149
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
          "OpCooperativeMatrixLoadTensorNV",
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
          "OpCooperativeMatrixStoreTensorNV",
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
          "OpCooperativeMatrixPerElementOpNV",
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
          "OpTypeTensorLayoutNV",
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
          InstructionMetadata2.m155
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
          InstructionMetadata2.m156
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
          InstructionMetadata2.m157
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
          InstructionMetadata2.m158
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
          InstructionMetadata2.m159
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
          InstructionMetadata2.m160
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
          InstructionMetadata2.m161
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
          InstructionMetadata2.m162
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
          InstructionMetadata2.m163
        )
    case DemoteToHelperInvocation extends Definition(
          "OpDemoteToHelperInvocation",
          SPIRVOpcodeNumber(5380),
          definition =>
            raw => Instruction.decodeDefinition(definition, raw, _ => Instruction.DemoteToHelperInvocation()),
          InstructionMetadata2.m164
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
          InstructionMetadata2.m165
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
          InstructionMetadata2.m167
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
          InstructionMetadata2.m168
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
          InstructionMetadata2.m169
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
          InstructionMetadata2.m170
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
          InstructionMetadata2.m171
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
          InstructionMetadata2.m172
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
          InstructionMetadata2.m173
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
          InstructionMetadata2.m174
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
          InstructionMetadata2.m175
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
          InstructionMetadata2.m177
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
          InstructionMetadata2.m178
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
          InstructionMetadata2.m179
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
          InstructionMetadata2.m180
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
          InstructionMetadata2.m181
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
          InstructionMetadata2.m182
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
          InstructionMetadata2.m183
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
          InstructionMetadata2.m184
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
          InstructionMetadata2.m185
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
          InstructionMetadata2.m186
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
          InstructionMetadata2.m187
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
          InstructionMetadata2.m188
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
          InstructionMetadata2.m189
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
          InstructionMetadata2.m190
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
          "OpSubgroupShuffleUpINTEL",
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
          InstructionMetadata2.m193
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
          InstructionMetadata2.m194
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
          InstructionMetadata2.m195
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
          InstructionMetadata2.m196
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
          InstructionMetadata2.m197
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
          "OpSubgroupImageMediaBlockWriteINTEL",
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
          InstructionMetadata3.m0
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
          InstructionMetadata3.m1
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
          InstructionMetadata3.m2
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
          InstructionMetadata3.m3
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
          InstructionMetadata3.m4
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
          InstructionMetadata3.m5
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
          InstructionMetadata3.m6
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
          InstructionMetadata3.m7
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
          InstructionMetadata3.m8
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
          InstructionMetadata3.m9
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
          InstructionMetadata3.m10
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
          InstructionMetadata3.m11
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
          InstructionMetadata3.m12
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
          InstructionMetadata3.m13
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
          InstructionMetadata3.m14
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
          InstructionMetadata3.m15
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
          InstructionMetadata3.m16
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
          InstructionMetadata3.m18
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
          "OpAtomicFMaxEXT",
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
          "OpAssumeTrueKHR",
          SPIRVOpcodeNumber(5630),
          definition =>
            raw =>
              Instruction.decodeDefinition(definition, raw, values => Instruction.AssumeTrueKHR(values.required(0))),
          InstructionMetadata3.m21
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
          InstructionMetadata3.m22
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
          InstructionMetadata3.m23
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
          InstructionMetadata3.m24
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
          InstructionMetadata3.m25
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
          InstructionMetadata3.m26
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
          InstructionMetadata3.m27
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
          InstructionMetadata3.m28
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
          InstructionMetadata3.m29
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
          InstructionMetadata3.m30
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
          InstructionMetadata3.m31
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
          InstructionMetadata3.m32
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
          InstructionMetadata3.m33
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
          InstructionMetadata3.m34
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
          InstructionMetadata3.m35
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
          InstructionMetadata3.m36
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
          InstructionMetadata3.m37
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
          InstructionMetadata3.m38
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
          InstructionMetadata3.m39
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
          InstructionMetadata3.m40
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
          InstructionMetadata3.m41
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
          InstructionMetadata3.m42
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
          InstructionMetadata3.m43
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
          InstructionMetadata3.m44
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
          InstructionMetadata3.m45
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
          InstructionMetadata3.m46
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
          InstructionMetadata3.m47
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
          InstructionMetadata3.m48
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
          InstructionMetadata3.m49
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
          InstructionMetadata3.m51
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
          InstructionMetadata3.m52
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
          InstructionMetadata3.m53
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
          InstructionMetadata3.m54
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
          InstructionMetadata3.m55
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
          InstructionMetadata3.m56
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
          InstructionMetadata3.m58
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
          InstructionMetadata3.m59
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
          InstructionMetadata3.m60
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
          InstructionMetadata3.m61
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
          InstructionMetadata3.m62
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
          InstructionMetadata3.m63
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
          InstructionMetadata3.m64
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
          InstructionMetadata3.m65
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
          InstructionMetadata3.m66
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
          InstructionMetadata3.m67
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
          InstructionMetadata3.m68
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
          InstructionMetadata3.m69
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
          InstructionMetadata3.m70
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
          InstructionMetadata3.m71
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
          "OpSubgroupAvcImeInitializeINTEL",
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
          "OpSubgroupAvcImeSetSingleReferenceINTEL",
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
          "OpSubgroupAvcImeSetDualReferenceINTEL",
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
          InstructionMetadata3.m76
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
          InstructionMetadata3.m78
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
          InstructionMetadata3.m79
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
          InstructionMetadata3.m80
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
          InstructionMetadata3.m81
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
          InstructionMetadata3.m82
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
          "OpSubgroupAvcImeEvaluateWithDualReferenceINTEL",
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
          "OpSubgroupAvcImeEvaluateWithSingleReferenceStreaminINTEL",
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
          "OpSubgroupAvcImeEvaluateWithDualReferenceStreaminINTEL",
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
          "OpSubgroupAvcImeEvaluateWithSingleReferenceStreamoutINTEL",
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
          "OpSubgroupAvcImeEvaluateWithDualReferenceStreamoutINTEL",
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
          "OpSubgroupAvcImeEvaluateWithSingleReferenceStreaminoutINTEL",
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
          "OpSubgroupAvcImeEvaluateWithDualReferenceStreaminoutINTEL",
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
          InstructionMetadata3.m91
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
          InstructionMetadata3.m92
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
          InstructionMetadata3.m93
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
          InstructionMetadata3.m94
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
          InstructionMetadata3.m95
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
          InstructionMetadata3.m96
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
          InstructionMetadata3.m97
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
          InstructionMetadata3.m98
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
          "OpSubgroupAvcImeGetStreamoutDualReferenceMajorShapeDistortionsINTEL",
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
          "OpSubgroupAvcImeGetStreamoutDualReferenceMajorShapeReferenceIdsINTEL",
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
          InstructionMetadata3.m102
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
          InstructionMetadata3.m103
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
          InstructionMetadata3.m104
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
          InstructionMetadata3.m105
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
          InstructionMetadata3.m106
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
          "OpSubgroupAvcBmeInitializeINTEL",
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
          InstructionMetadata3.m109
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
          InstructionMetadata3.m110
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
          InstructionMetadata3.m111
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
          "OpSubgroupAvcRefEvaluateWithDualReferenceINTEL",
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
          "OpSubgroupAvcRefEvaluateWithMultiReferenceINTEL",
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
          "OpSubgroupAvcRefEvaluateWithMultiReferenceInterlacedINTEL",
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
          InstructionMetadata3.m116
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
          InstructionMetadata3.m117
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
          "OpSubgroupAvcSicConfigureIpeLumaINTEL",
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
          "OpSubgroupAvcSicConfigureIpeLumaChromaINTEL",
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
          InstructionMetadata3.m121
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
          InstructionMetadata3.m122
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
          InstructionMetadata3.m123
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
          InstructionMetadata3.m125
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
          InstructionMetadata3.m126
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
          InstructionMetadata3.m127
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
          InstructionMetadata3.m128
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
          InstructionMetadata3.m129
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
          "OpSubgroupAvcSicEvaluateWithDualReferenceINTEL",
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
          "OpSubgroupAvcSicEvaluateWithMultiReferenceINTEL",
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
          "OpSubgroupAvcSicEvaluateWithMultiReferenceInterlacedINTEL",
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
          InstructionMetadata3.m134
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
          InstructionMetadata3.m135
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
          InstructionMetadata3.m136
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
          InstructionMetadata3.m137
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
          InstructionMetadata3.m138
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
          InstructionMetadata3.m139
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
          InstructionMetadata3.m140
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
          InstructionMetadata3.m141
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
          InstructionMetadata3.m142
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
          InstructionMetadata3.m143
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
          InstructionMetadata3.m144
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
          InstructionMetadata3.m145
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
          "OpArbitraryFloatCastALTERA",
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
          "OpArbitraryFloatCastFromIntALTERA",
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
          "OpArbitraryFloatCastToIntALTERA",
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
          "OpArbitraryFloatAddALTERA",
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
          "OpArbitraryFloatSubALTERA",
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
          "OpArbitraryFloatMulALTERA",
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
          "OpArbitraryFloatDivALTERA",
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
          "OpArbitraryFloatGTALTERA",
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
          "OpArbitraryFloatGEALTERA",
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
          "OpArbitraryFloatLTALTERA",
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
          "OpArbitraryFloatLEALTERA",
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
          "OpArbitraryFloatEQALTERA",
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
          "OpArbitraryFloatRecipALTERA",
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
          "OpArbitraryFloatRSqrtALTERA",
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
          "OpArbitraryFloatCbrtALTERA",
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
          "OpArbitraryFloatHypotALTERA",
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
          "OpArbitraryFloatSqrtALTERA",
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
          "OpArbitraryFloatLogINTEL",
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
          "OpArbitraryFloatLog2INTEL",
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
          "OpArbitraryFloatLog10INTEL",
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
          "OpArbitraryFloatLog1pINTEL",
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
          "OpArbitraryFloatExpINTEL",
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
          "OpArbitraryFloatExp2INTEL",
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
          "OpArbitraryFloatExp10INTEL",
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
          "OpArbitraryFloatExpm1INTEL",
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
          "OpArbitraryFloatSinINTEL",
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
          "OpArbitraryFloatCosINTEL",
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
          "OpArbitraryFloatSinCosINTEL",
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
          "OpArbitraryFloatSinPiINTEL",
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
          "OpArbitraryFloatCosPiINTEL",
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
          "OpArbitraryFloatASinINTEL",
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
          "OpArbitraryFloatASinPiINTEL",
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
          "OpArbitraryFloatACosINTEL",
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
          "OpArbitraryFloatACosPiINTEL",
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
          "OpArbitraryFloatATanINTEL",
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
          "OpArbitraryFloatATanPiINTEL",
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
          "OpArbitraryFloatATan2INTEL",
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
          "OpArbitraryFloatPowINTEL",
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
          "OpArbitraryFloatPowRINTEL",
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
          "OpArbitraryFloatPowNINTEL",
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
          "OpLoopControlINTEL",
          SPIRVOpcodeNumber(5887),
          definition =>
            raw =>
              Instruction.decodeDefinition(definition, raw, values => Instruction.LoopControlINTEL(values.repeated(0))),
          InstructionMetadata3.m187
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
          InstructionMetadata3.m188
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
          InstructionMetadata3.m189
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
          InstructionMetadata3.m190
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
          "OpFixedRecipALTERA",
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
          "OpFixedRsqrtALTERA",
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
          "OpFixedSinALTERA",
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
          "OpFixedCosALTERA",
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
          "OpFixedSinCosALTERA",
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
          "OpFixedSinPiALTERA",
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
          "OpFixedCosPiALTERA",
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
          "OpFixedSinCosPiALTERA",
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
          "OpFixedLogALTERA",
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
          "OpFixedExpALTERA",
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
          InstructionMetadata4.m2
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
          InstructionMetadata4.m3
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
          InstructionMetadata4.m4
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
          InstructionMetadata4.m5
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
          InstructionMetadata4.m6
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
          InstructionMetadata4.m7
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
          InstructionMetadata4.m8
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
          InstructionMetadata4.m9
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
          InstructionMetadata4.m10
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
          InstructionMetadata4.m11
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
          InstructionMetadata4.m12
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
          InstructionMetadata4.m13
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
          InstructionMetadata4.m14
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
          InstructionMetadata4.m15
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
          InstructionMetadata4.m16
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
          InstructionMetadata4.m17
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
          InstructionMetadata4.m18
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
          InstructionMetadata4.m19
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
          InstructionMetadata4.m20
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
          InstructionMetadata4.m21
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
          InstructionMetadata4.m22
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
          InstructionMetadata4.m23
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
          "OpTypeBufferSurfaceINTEL",
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
          "OpTypeStructContinuedINTEL",
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
          "OpConstantCompositeContinuedINTEL",
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
          "OpSpecConstantCompositeContinuedINTEL",
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
          "OpCompositeConstructContinuedINTEL",
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
          "OpConvertFToBF16INTEL",
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
          "OpConvertBF16ToFINTEL",
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
          InstructionMetadata4.m32
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
          InstructionMetadata4.m33
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
          InstructionMetadata4.m34
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
          "OpTaskSequenceAsyncALTERA",
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
          "OpTaskSequenceGetALTERA",
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
          "OpTaskSequenceReleaseALTERA",
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
          "OpTypeTaskSequenceALTERA",
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
          InstructionMetadata4.m40
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
          "OpSubgroup2DBlockLoadTransformINTEL",
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
          "OpSubgroup2DBlockLoadTransposeINTEL",
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
          "OpSubgroup2DBlockPrefetchINTEL",
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
          "OpSubgroup2DBlockStoreINTEL",
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
          "OpSubgroupMatrixMultiplyAccumulateINTEL",
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
          "OpBitwiseFunctionINTEL",
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
          InstructionMetadata4.m48
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
          InstructionMetadata4.m49
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
          "OpConditionalCapabilityINTEL",
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
          InstructionMetadata4.m52
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
          InstructionMetadata4.m54
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
          InstructionMetadata4.m55
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
          InstructionMetadata4.m57
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
          "OpGroupFMulKHR",
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
          "OpGroupBitwiseAndKHR",
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
          "OpGroupBitwiseOrKHR",
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
          "OpGroupBitwiseXorKHR",
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
          "OpGroupLogicalAndKHR",
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
          "OpGroupLogicalOrKHR",
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
          "OpGroupLogicalXorKHR",
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
          "OpRoundFToTF32INTEL",
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
          "OpMaskedGatherINTEL",
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
          InstructionMetadata4.m68
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
          InstructionMetadata4.m69
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
          InstructionMetadata4.m70
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
          InstructionMetadata4.m71
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
          "OpFDot2MixAcc16VALVE",
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
          "OpFDot4MixAcc32VALVE",
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

    lazy val decoder: Decoder = decode(this)
  }

  val version: Version = Version(
    1,
    6
  )
  val grammarRevision: Int >= 0 = 7
  val definitions: Vec[Definition] = Definition.values.toVec

  private def operandKinds0: Vec[OperandKindInfo] = Vec(
    OperandKindInfo(
      OperandKind.ImageOperands,
      Vec(
        OperandEnumerant("None", 0L, Vec(), Vec()),
        OperandEnumerant("Bias", 1L, Vec(OperandKind.IdRef), Vec()),
        OperandEnumerant("Lod", 2L, Vec(OperandKind.IdRef), Vec()),
        OperandEnumerant("Grad", 4L, Vec(OperandKind.IdRef, OperandKind.IdRef), Vec()),
        OperandEnumerant("ConstOffset", 8L, Vec(OperandKind.IdRef), Vec()),
        OperandEnumerant("Offset", 16L, Vec(OperandKind.IdRef), Vec()),
        OperandEnumerant("ConstOffsets", 32L, Vec(OperandKind.IdRef), Vec()),
        OperandEnumerant("Sample", 64L, Vec(OperandKind.IdRef), Vec()),
        OperandEnumerant("MinLod", 128L, Vec(OperandKind.IdRef), Vec()),
        OperandEnumerant("MakeTexelAvailable", 256L, Vec(OperandKind.IdScope), Vec("MakeTexelAvailableKHR")),
        OperandEnumerant("MakeTexelVisible", 512L, Vec(OperandKind.IdScope), Vec("MakeTexelVisibleKHR")),
        OperandEnumerant("NonPrivateTexel", 1024L, Vec(), Vec("NonPrivateTexelKHR")),
        OperandEnumerant("VolatileTexel", 2048L, Vec(), Vec("VolatileTexelKHR")),
        OperandEnumerant("SignExtend", 4096L, Vec(), Vec()),
        OperandEnumerant("ZeroExtend", 8192L, Vec(), Vec()),
        OperandEnumerant("Nontemporal", 16384L, Vec(), Vec()),
        OperandEnumerant("Offsets", 65536L, Vec(OperandKind.IdRef), Vec())
      )
    ),
    OperandKindInfo(
      OperandKind.FPFastMathMode,
      Vec(
        OperandEnumerant("None", 0L, Vec(), Vec()),
        OperandEnumerant("NotNaN", 1L, Vec(), Vec()),
        OperandEnumerant("NotInf", 2L, Vec(), Vec()),
        OperandEnumerant("NSZ", 4L, Vec(), Vec()),
        OperandEnumerant("AllowRecip", 8L, Vec(), Vec()),
        OperandEnumerant("Fast", 16L, Vec(), Vec()),
        OperandEnumerant("AllowContract", 65536L, Vec(), Vec("AllowContractFastINTEL")),
        OperandEnumerant("AllowReassoc", 131072L, Vec(), Vec("AllowReassocINTEL")),
        OperandEnumerant("AllowTransform", 262144L, Vec(), Vec())
      )
    ),
    OperandKindInfo(
      OperandKind.SelectionControl,
      Vec(
        OperandEnumerant("None", 0L, Vec(), Vec()),
        OperandEnumerant("Flatten", 1L, Vec(), Vec()),
        OperandEnumerant("DontFlatten", 2L, Vec(), Vec())
      )
    ),
    OperandKindInfo(
      OperandKind.LoopControl,
      Vec(
        OperandEnumerant("None", 0L, Vec(), Vec()),
        OperandEnumerant("Unroll", 1L, Vec(), Vec()),
        OperandEnumerant("DontUnroll", 2L, Vec(), Vec()),
        OperandEnumerant("DependencyInfinite", 4L, Vec(), Vec()),
        OperandEnumerant("DependencyLength", 8L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant("MinIterations", 16L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant("MaxIterations", 32L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant("IterationMultiple", 64L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant("PeelCount", 128L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant("PartialCount", 256L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant(
          "InitiationIntervalALTERA",
          65536L,
          Vec(OperandKind.LiteralInteger),
          Vec("InitiationIntervalINTEL")
        ),
        OperandEnumerant("MaxConcurrencyALTERA", 131072L, Vec(OperandKind.LiteralInteger), Vec("MaxConcurrencyINTEL")),
        OperandEnumerant(
          "DependencyArrayALTERA",
          262144L,
          Vec(OperandKind.LiteralInteger),
          Vec("DependencyArrayINTEL")
        ),
        OperandEnumerant("PipelineEnableALTERA", 524288L, Vec(OperandKind.LiteralInteger), Vec("PipelineEnableINTEL")),
        OperandEnumerant("LoopCoalesceALTERA", 1048576L, Vec(OperandKind.LiteralInteger), Vec("LoopCoalesceINTEL")),
        OperandEnumerant(
          "MaxInterleavingALTERA",
          2097152L,
          Vec(OperandKind.LiteralInteger),
          Vec("MaxInterleavingINTEL")
        ),
        OperandEnumerant(
          "SpeculatedIterationsALTERA",
          4194304L,
          Vec(OperandKind.LiteralInteger),
          Vec("SpeculatedIterationsINTEL")
        ),
        OperandEnumerant("NoFusionALTERA", 8388608L, Vec(), Vec("NoFusionINTEL")),
        OperandEnumerant("LoopCountALTERA", 16777216L, Vec(OperandKind.LiteralInteger), Vec("LoopCountINTEL")),
        OperandEnumerant(
          "MaxReinvocationDelayALTERA",
          33554432L,
          Vec(OperandKind.LiteralInteger),
          Vec("MaxReinvocationDelayINTEL")
        ),
        OperandEnumerant("MultipleWaitQueuesQCOM", 268435456L, Vec(OperandKind.LiteralInteger), Vec())
      )
    ),
    OperandKindInfo(
      OperandKind.FunctionControl,
      Vec(
        OperandEnumerant("None", 0L, Vec(), Vec()),
        OperandEnumerant("Inline", 1L, Vec(), Vec()),
        OperandEnumerant("DontInline", 2L, Vec(), Vec()),
        OperandEnumerant("Pure", 4L, Vec(), Vec()),
        OperandEnumerant("Const", 8L, Vec(), Vec()),
        OperandEnumerant("OptNoneEXT", 65536L, Vec(), Vec("OptNoneINTEL"))
      )
    ),
    OperandKindInfo(
      OperandKind.MemorySemantics,
      Vec(
        OperandEnumerant("Relaxed", 0L, Vec(), Vec("None")),
        OperandEnumerant("Acquire", 2L, Vec(), Vec()),
        OperandEnumerant("Release", 4L, Vec(), Vec()),
        OperandEnumerant("AcquireRelease", 8L, Vec(), Vec()),
        OperandEnumerant("SequentiallyConsistent", 16L, Vec(), Vec()),
        OperandEnumerant("UniformMemory", 64L, Vec(), Vec()),
        OperandEnumerant("SubgroupMemory", 128L, Vec(), Vec()),
        OperandEnumerant("WorkgroupMemory", 256L, Vec(), Vec()),
        OperandEnumerant("CrossWorkgroupMemory", 512L, Vec(), Vec()),
        OperandEnumerant("AtomicCounterMemory", 1024L, Vec(), Vec()),
        OperandEnumerant("ImageMemory", 2048L, Vec(), Vec()),
        OperandEnumerant("OutputMemory", 4096L, Vec(), Vec("OutputMemoryKHR")),
        OperandEnumerant("MakeAvailable", 8192L, Vec(), Vec("MakeAvailableKHR")),
        OperandEnumerant("MakeVisible", 16384L, Vec(), Vec("MakeVisibleKHR")),
        OperandEnumerant("Volatile", 32768L, Vec(), Vec())
      )
    ),
    OperandKindInfo(
      OperandKind.MemoryAccess,
      Vec(
        OperandEnumerant("None", 0L, Vec(), Vec()),
        OperandEnumerant("Volatile", 1L, Vec(), Vec()),
        OperandEnumerant("Aligned", 2L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant("Nontemporal", 4L, Vec(), Vec()),
        OperandEnumerant("MakePointerAvailable", 8L, Vec(OperandKind.IdScope), Vec("MakePointerAvailableKHR")),
        OperandEnumerant("MakePointerVisible", 16L, Vec(OperandKind.IdScope), Vec("MakePointerVisibleKHR")),
        OperandEnumerant("NonPrivatePointer", 32L, Vec(), Vec("NonPrivatePointerKHR")),
        OperandEnumerant("AliasScopeINTELMask", 65536L, Vec(OperandKind.IdRef), Vec()),
        OperandEnumerant("NoAliasINTELMask", 131072L, Vec(OperandKind.IdRef), Vec())
      )
    ),
    OperandKindInfo(
      OperandKind.KernelProfilingInfo,
      Vec(OperandEnumerant("None", 0L, Vec(), Vec()), OperandEnumerant("CmdExecTime", 1L, Vec(), Vec()))
    )
  )

  private def operandKinds1: Vec[OperandKindInfo] = Vec(
    OperandKindInfo(
      OperandKind.RayFlags,
      Vec(
        OperandEnumerant("NoneKHR", 0L, Vec(), Vec()),
        OperandEnumerant("OpaqueKHR", 1L, Vec(), Vec()),
        OperandEnumerant("NoOpaqueKHR", 2L, Vec(), Vec()),
        OperandEnumerant("TerminateOnFirstHitKHR", 4L, Vec(), Vec()),
        OperandEnumerant("SkipClosestHitShaderKHR", 8L, Vec(), Vec()),
        OperandEnumerant("CullBackFacingTrianglesKHR", 16L, Vec(), Vec()),
        OperandEnumerant("CullFrontFacingTrianglesKHR", 32L, Vec(), Vec()),
        OperandEnumerant("CullOpaqueKHR", 64L, Vec(), Vec()),
        OperandEnumerant("CullNoOpaqueKHR", 128L, Vec(), Vec()),
        OperandEnumerant("SkipTrianglesKHR", 256L, Vec(), Vec("SkipBuiltinPrimitivesNV")),
        OperandEnumerant("SkipAABBsKHR", 512L, Vec(), Vec()),
        OperandEnumerant("ForceOpacityMicromap2StateKHR", 1024L, Vec(), Vec("ForceOpacityMicromap2StateEXT"))
      )
    ),
    OperandKindInfo(
      OperandKind.FragmentShadingRate,
      Vec(
        OperandEnumerant("Vertical2Pixels", 1L, Vec(), Vec()),
        OperandEnumerant("Vertical4Pixels", 2L, Vec(), Vec()),
        OperandEnumerant("Horizontal2Pixels", 4L, Vec(), Vec()),
        OperandEnumerant("Horizontal4Pixels", 8L, Vec(), Vec())
      )
    ),
    OperandKindInfo(
      OperandKind.RawAccessChainOperands,
      Vec(
        OperandEnumerant("None", 0L, Vec(), Vec()),
        OperandEnumerant("RobustnessPerComponentNV", 1L, Vec(), Vec()),
        OperandEnumerant("RobustnessPerElementNV", 2L, Vec(), Vec())
      )
    ),
    OperandKindInfo(
      OperandKind.SourceLanguage,
      Vec(
        OperandEnumerant("Unknown", 0L, Vec(), Vec()),
        OperandEnumerant("ESSL", 1L, Vec(), Vec()),
        OperandEnumerant("GLSL", 2L, Vec(), Vec()),
        OperandEnumerant("OpenCL_C", 3L, Vec(), Vec()),
        OperandEnumerant("OpenCL_CPP", 4L, Vec(), Vec()),
        OperandEnumerant("HLSL", 5L, Vec(), Vec()),
        OperandEnumerant("CPP_for_OpenCL", 6L, Vec(), Vec()),
        OperandEnumerant("SYCL", 7L, Vec(), Vec()),
        OperandEnumerant("HERO_C", 8L, Vec(), Vec()),
        OperandEnumerant("NZSL", 9L, Vec(), Vec()),
        OperandEnumerant("WGSL", 10L, Vec(), Vec()),
        OperandEnumerant("Slang", 11L, Vec(), Vec()),
        OperandEnumerant("Zig", 12L, Vec(), Vec()),
        OperandEnumerant("Rust", 13L, Vec(), Vec()),
        OperandEnumerant("Pred", 14L, Vec(), Vec()),
        OperandEnumerant("ApilaJai", 15L, Vec(), Vec())
      )
    ),
    OperandKindInfo(
      OperandKind.ExecutionModel,
      Vec(
        OperandEnumerant("Vertex", 0L, Vec(), Vec()),
        OperandEnumerant("TessellationControl", 1L, Vec(), Vec()),
        OperandEnumerant("TessellationEvaluation", 2L, Vec(), Vec()),
        OperandEnumerant("Geometry", 3L, Vec(), Vec()),
        OperandEnumerant("Fragment", 4L, Vec(), Vec()),
        OperandEnumerant("GLCompute", 5L, Vec(), Vec()),
        OperandEnumerant("Kernel", 6L, Vec(), Vec()),
        OperandEnumerant("TaskNV", 5267L, Vec(), Vec()),
        OperandEnumerant("MeshNV", 5268L, Vec(), Vec()),
        OperandEnumerant("RayGenerationKHR", 5313L, Vec(), Vec("RayGenerationNV")),
        OperandEnumerant("IntersectionKHR", 5314L, Vec(), Vec("IntersectionNV")),
        OperandEnumerant("AnyHitKHR", 5315L, Vec(), Vec("AnyHitNV")),
        OperandEnumerant("ClosestHitKHR", 5316L, Vec(), Vec("ClosestHitNV")),
        OperandEnumerant("MissKHR", 5317L, Vec(), Vec("MissNV")),
        OperandEnumerant("CallableKHR", 5318L, Vec(), Vec("CallableNV")),
        OperandEnumerant("TaskEXT", 5364L, Vec(), Vec()),
        OperandEnumerant("MeshEXT", 5365L, Vec(), Vec())
      )
    ),
    OperandKindInfo(
      OperandKind.AddressingModel,
      Vec(
        OperandEnumerant("Logical", 0L, Vec(), Vec()),
        OperandEnumerant("Physical32", 1L, Vec(), Vec()),
        OperandEnumerant("Physical64", 2L, Vec(), Vec()),
        OperandEnumerant("PhysicalStorageBuffer64", 5348L, Vec(), Vec("PhysicalStorageBuffer64EXT"))
      )
    ),
    OperandKindInfo(
      OperandKind.MemoryModel,
      Vec(
        OperandEnumerant("Simple", 0L, Vec(), Vec()),
        OperandEnumerant("GLSL450", 1L, Vec(), Vec()),
        OperandEnumerant("OpenCL", 2L, Vec(), Vec()),
        OperandEnumerant("Vulkan", 3L, Vec(), Vec("VulkanKHR"))
      )
    ),
    OperandKindInfo(
      OperandKind.ExecutionMode,
      Vec(
        OperandEnumerant("Invocations", 0L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant("SpacingEqual", 1L, Vec(), Vec()),
        OperandEnumerant("SpacingFractionalEven", 2L, Vec(), Vec()),
        OperandEnumerant("SpacingFractionalOdd", 3L, Vec(), Vec()),
        OperandEnumerant("VertexOrderCw", 4L, Vec(), Vec()),
        OperandEnumerant("VertexOrderCcw", 5L, Vec(), Vec()),
        OperandEnumerant("PixelCenterInteger", 6L, Vec(), Vec()),
        OperandEnumerant("OriginUpperLeft", 7L, Vec(), Vec()),
        OperandEnumerant("OriginLowerLeft", 8L, Vec(), Vec()),
        OperandEnumerant("EarlyFragmentTests", 9L, Vec(), Vec()),
        OperandEnumerant("PointMode", 10L, Vec(), Vec()),
        OperandEnumerant("Xfb", 11L, Vec(), Vec()),
        OperandEnumerant("DepthReplacing", 12L, Vec(), Vec()),
        OperandEnumerant("DepthGreater", 14L, Vec(), Vec()),
        OperandEnumerant("DepthLess", 15L, Vec(), Vec()),
        OperandEnumerant("DepthUnchanged", 16L, Vec(), Vec()),
        OperandEnumerant(
          "LocalSize",
          17L,
          Vec(OperandKind.LiteralInteger, OperandKind.LiteralInteger, OperandKind.LiteralInteger),
          Vec()
        ),
        OperandEnumerant(
          "LocalSizeHint",
          18L,
          Vec(OperandKind.LiteralInteger, OperandKind.LiteralInteger, OperandKind.LiteralInteger),
          Vec()
        ),
        OperandEnumerant("InputPoints", 19L, Vec(), Vec()),
        OperandEnumerant("InputLines", 20L, Vec(), Vec()),
        OperandEnumerant("InputLinesAdjacency", 21L, Vec(), Vec()),
        OperandEnumerant("Triangles", 22L, Vec(), Vec()),
        OperandEnumerant("InputTrianglesAdjacency", 23L, Vec(), Vec()),
        OperandEnumerant("Quads", 24L, Vec(), Vec()),
        OperandEnumerant("Isolines", 25L, Vec(), Vec()),
        OperandEnumerant("OutputVertices", 26L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant("OutputPoints", 27L, Vec(), Vec()),
        OperandEnumerant("OutputLineStrip", 28L, Vec(), Vec()),
        OperandEnumerant("OutputTriangleStrip", 29L, Vec(), Vec()),
        OperandEnumerant("VecTypeHint", 30L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant("ContractionOff", 31L, Vec(), Vec()),
        OperandEnumerant("Initializer", 33L, Vec(), Vec()),
        OperandEnumerant("Finalizer", 34L, Vec(), Vec()),
        OperandEnumerant("SubgroupSize", 35L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant("SubgroupsPerWorkgroup", 36L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant("SubgroupsPerWorkgroupId", 37L, Vec(OperandKind.IdRef), Vec()),
        OperandEnumerant("LocalSizeId", 38L, Vec(OperandKind.IdRef, OperandKind.IdRef, OperandKind.IdRef), Vec()),
        OperandEnumerant("LocalSizeHintId", 39L, Vec(OperandKind.IdRef, OperandKind.IdRef, OperandKind.IdRef), Vec()),
        OperandEnumerant("NonCoherentColorAttachmentReadEXT", 4169L, Vec(), Vec()),
        OperandEnumerant("NonCoherentDepthAttachmentReadEXT", 4170L, Vec(), Vec()),
        OperandEnumerant("NonCoherentStencilAttachmentReadEXT", 4171L, Vec(), Vec()),
        OperandEnumerant("SubgroupUniformControlFlowKHR", 4421L, Vec(), Vec()),
        OperandEnumerant("PostDepthCoverage", 4446L, Vec(), Vec()),
        OperandEnumerant("DenormPreserve", 4459L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant("DenormFlushToZero", 4460L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant("SignedZeroInfNanPreserve", 4461L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant("RoundingModeRTE", 4462L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant("RoundingModeRTZ", 4463L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant("NonCoherentTileAttachmentReadQCOM", 4489L, Vec(), Vec()),
        OperandEnumerant(
          "TileShadingRateQCOM",
          4490L,
          Vec(OperandKind.LiteralInteger, OperandKind.LiteralInteger, OperandKind.LiteralInteger),
          Vec()
        ),
        OperandEnumerant("EarlyAndLateFragmentTestsAMD", 5017L, Vec(), Vec()),
        OperandEnumerant("StencilRefReplacingEXT", 5027L, Vec(), Vec()),
        OperandEnumerant("CoalescingAMDX", 5069L, Vec(), Vec()),
        OperandEnumerant("IsApiEntryAMDX", 5070L, Vec(OperandKind.IdRef), Vec()),
        OperandEnumerant("MaxNodeRecursionAMDX", 5071L, Vec(OperandKind.IdRef), Vec()),
        OperandEnumerant(
          "StaticNumWorkgroupsAMDX",
          5072L,
          Vec(OperandKind.IdRef, OperandKind.IdRef, OperandKind.IdRef),
          Vec()
        ),
        OperandEnumerant("ShaderIndexAMDX", 5073L, Vec(OperandKind.IdRef), Vec()),
        OperandEnumerant(
          "MaxNumWorkgroupsAMDX",
          5077L,
          Vec(OperandKind.IdRef, OperandKind.IdRef, OperandKind.IdRef),
          Vec()
        ),
        OperandEnumerant("StencilRefUnchangedFrontAMD", 5079L, Vec(), Vec()),
        OperandEnumerant("StencilRefGreaterFrontAMD", 5080L, Vec(), Vec()),
        OperandEnumerant("StencilRefLessFrontAMD", 5081L, Vec(), Vec()),
        OperandEnumerant("StencilRefUnchangedBackAMD", 5082L, Vec(), Vec()),
        OperandEnumerant("StencilRefGreaterBackAMD", 5083L, Vec(), Vec()),
        OperandEnumerant("StencilRefLessBackAMD", 5084L, Vec(), Vec()),
        OperandEnumerant("QuadDerivativesKHR", 5088L, Vec(), Vec()),
        OperandEnumerant("RequireFullQuadsKHR", 5089L, Vec(), Vec()),
        OperandEnumerant("SharesInputWithAMDX", 5102L, Vec(OperandKind.IdRef, OperandKind.IdRef), Vec()),
        OperandEnumerant("ArithmeticPoisonKHR", 5157L, Vec(), Vec()),
        OperandEnumerant("OutputLinesEXT", 5269L, Vec(), Vec("OutputLinesNV")),
        OperandEnumerant("OutputPrimitivesEXT", 5270L, Vec(OperandKind.LiteralInteger), Vec("OutputPrimitivesNV")),
        OperandEnumerant("DerivativeGroupQuadsKHR", 5289L, Vec(), Vec("DerivativeGroupQuadsNV")),
        OperandEnumerant("DerivativeGroupLinearKHR", 5290L, Vec(), Vec("DerivativeGroupLinearNV")),
        OperandEnumerant("OutputTrianglesEXT", 5298L, Vec(), Vec("OutputTrianglesNV")),
        OperandEnumerant("PixelInterlockOrderedEXT", 5366L, Vec(), Vec()),
        OperandEnumerant("PixelInterlockUnorderedEXT", 5367L, Vec(), Vec()),
        OperandEnumerant("SampleInterlockOrderedEXT", 5368L, Vec(), Vec()),
        OperandEnumerant("SampleInterlockUnorderedEXT", 5369L, Vec(), Vec()),
        OperandEnumerant("ShadingRateInterlockOrderedEXT", 5370L, Vec(), Vec()),
        OperandEnumerant("ShadingRateInterlockUnorderedEXT", 5371L, Vec(), Vec()),
        OperandEnumerant("Shader64BitIndexingEXT", 5427L, Vec(), Vec()),
        OperandEnumerant("SharedLocalMemorySizeINTEL", 5618L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant("RoundingModeRTPINTEL", 5620L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant("RoundingModeRTNINTEL", 5621L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant("FloatingPointModeALTINTEL", 5622L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant("FloatingPointModeIEEEINTEL", 5623L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant(
          "MaxWorkgroupSizeINTEL",
          5893L,
          Vec(OperandKind.LiteralInteger, OperandKind.LiteralInteger, OperandKind.LiteralInteger),
          Vec()
        ),
        OperandEnumerant("MaxWorkDimINTEL", 5894L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant("NoGlobalOffsetINTEL", 5895L, Vec(), Vec()),
        OperandEnumerant("NumSIMDWorkitemsINTEL", 5896L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant("SchedulerTargetFmaxMhzINTEL", 5903L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant("MaximallyReconvergesKHR", 6023L, Vec(), Vec()),
        OperandEnumerant("FPFastMathDefault", 6028L, Vec(OperandKind.IdRef, OperandKind.IdRef), Vec()),
        OperandEnumerant("OpacityMicromapIdKHR", 6031L, Vec(OperandKind.IdRef), Vec()),
        OperandEnumerant("StreamingInterfaceINTEL", 6154L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant("RegisterMapInterfaceINTEL", 6160L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant("NamedBarrierCountINTEL", 6417L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant("MaximumRegistersINTEL", 6461L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant("MaximumRegistersIdINTEL", 6462L, Vec(OperandKind.IdRef), Vec()),
        OperandEnumerant("NamedMaximumRegistersINTEL", 6463L, Vec(OperandKind.NamedMaximumNumberOfRegisters), Vec())
      )
    )
  )

  private def operandKinds2: Vec[OperandKindInfo] = Vec(
    OperandKindInfo(
      OperandKind.StorageClass,
      Vec(
        OperandEnumerant("UniformConstant", 0L, Vec(), Vec()),
        OperandEnumerant("Input", 1L, Vec(), Vec()),
        OperandEnumerant("Uniform", 2L, Vec(), Vec()),
        OperandEnumerant("Output", 3L, Vec(), Vec()),
        OperandEnumerant("Workgroup", 4L, Vec(), Vec()),
        OperandEnumerant("CrossWorkgroup", 5L, Vec(), Vec()),
        OperandEnumerant("Private", 6L, Vec(), Vec()),
        OperandEnumerant("Function", 7L, Vec(), Vec()),
        OperandEnumerant("Generic", 8L, Vec(), Vec()),
        OperandEnumerant("PushConstant", 9L, Vec(), Vec()),
        OperandEnumerant("AtomicCounter", 10L, Vec(), Vec()),
        OperandEnumerant("Image", 11L, Vec(), Vec()),
        OperandEnumerant("StorageBuffer", 12L, Vec(), Vec()),
        OperandEnumerant("TileImageEXT", 4172L, Vec(), Vec()),
        OperandEnumerant("TileAttachmentQCOM", 4491L, Vec(), Vec()),
        OperandEnumerant("NodePayloadAMDX", 5068L, Vec(), Vec()),
        OperandEnumerant("CallableDataKHR", 5328L, Vec(), Vec("CallableDataNV")),
        OperandEnumerant("IncomingCallableDataKHR", 5329L, Vec(), Vec("IncomingCallableDataNV")),
        OperandEnumerant("RayPayloadKHR", 5338L, Vec(), Vec("RayPayloadNV")),
        OperandEnumerant("HitAttributeKHR", 5339L, Vec(), Vec("HitAttributeNV")),
        OperandEnumerant("IncomingRayPayloadKHR", 5342L, Vec(), Vec("IncomingRayPayloadNV")),
        OperandEnumerant("ShaderRecordBufferKHR", 5343L, Vec(), Vec("ShaderRecordBufferNV")),
        OperandEnumerant("PhysicalStorageBuffer", 5349L, Vec(), Vec("PhysicalStorageBufferEXT")),
        OperandEnumerant("HitObjectAttributeNV", 5385L, Vec(), Vec()),
        OperandEnumerant("TaskPayloadWorkgroupEXT", 5402L, Vec(), Vec()),
        OperandEnumerant("HitObjectAttributeEXT", 5411L, Vec(), Vec()),
        OperandEnumerant("CodeSectionINTEL", 5605L, Vec(), Vec()),
        OperandEnumerant("DeviceOnlyALTERA", 5936L, Vec(), Vec("DeviceOnlyINTEL")),
        OperandEnumerant("HostOnlyALTERA", 5937L, Vec(), Vec("HostOnlyINTEL"))
      )
    ),
    OperandKindInfo(
      OperandKind.Dim,
      Vec(
        OperandEnumerant("1D", 0L, Vec(), Vec()),
        OperandEnumerant("2D", 1L, Vec(), Vec()),
        OperandEnumerant("3D", 2L, Vec(), Vec()),
        OperandEnumerant("Cube", 3L, Vec(), Vec()),
        OperandEnumerant("Rect", 4L, Vec(), Vec()),
        OperandEnumerant("Buffer", 5L, Vec(), Vec()),
        OperandEnumerant("SubpassData", 6L, Vec(), Vec()),
        OperandEnumerant("TileImageDataEXT", 4173L, Vec(), Vec())
      )
    ),
    OperandKindInfo(
      OperandKind.SamplerAddressingMode,
      Vec(
        OperandEnumerant("None", 0L, Vec(), Vec()),
        OperandEnumerant("ClampToEdge", 1L, Vec(), Vec()),
        OperandEnumerant("Clamp", 2L, Vec(), Vec()),
        OperandEnumerant("Repeat", 3L, Vec(), Vec()),
        OperandEnumerant("RepeatMirrored", 4L, Vec(), Vec())
      )
    ),
    OperandKindInfo(
      OperandKind.SamplerFilterMode,
      Vec(OperandEnumerant("Nearest", 0L, Vec(), Vec()), OperandEnumerant("Linear", 1L, Vec(), Vec()))
    ),
    OperandKindInfo(
      OperandKind.ImageFormat,
      Vec(
        OperandEnumerant("Unknown", 0L, Vec(), Vec()),
        OperandEnumerant("Rgba32f", 1L, Vec(), Vec()),
        OperandEnumerant("Rgba16f", 2L, Vec(), Vec()),
        OperandEnumerant("R32f", 3L, Vec(), Vec()),
        OperandEnumerant("Rgba8", 4L, Vec(), Vec()),
        OperandEnumerant("Rgba8Snorm", 5L, Vec(), Vec()),
        OperandEnumerant("Rg32f", 6L, Vec(), Vec()),
        OperandEnumerant("Rg16f", 7L, Vec(), Vec()),
        OperandEnumerant("R11fG11fB10f", 8L, Vec(), Vec()),
        OperandEnumerant("R16f", 9L, Vec(), Vec()),
        OperandEnumerant("Rgba16", 10L, Vec(), Vec()),
        OperandEnumerant("Rgb10A2", 11L, Vec(), Vec()),
        OperandEnumerant("Rg16", 12L, Vec(), Vec()),
        OperandEnumerant("Rg8", 13L, Vec(), Vec()),
        OperandEnumerant("R16", 14L, Vec(), Vec()),
        OperandEnumerant("R8", 15L, Vec(), Vec()),
        OperandEnumerant("Rgba16Snorm", 16L, Vec(), Vec()),
        OperandEnumerant("Rg16Snorm", 17L, Vec(), Vec()),
        OperandEnumerant("Rg8Snorm", 18L, Vec(), Vec()),
        OperandEnumerant("R16Snorm", 19L, Vec(), Vec()),
        OperandEnumerant("R8Snorm", 20L, Vec(), Vec()),
        OperandEnumerant("Rgba32i", 21L, Vec(), Vec()),
        OperandEnumerant("Rgba16i", 22L, Vec(), Vec()),
        OperandEnumerant("Rgba8i", 23L, Vec(), Vec()),
        OperandEnumerant("R32i", 24L, Vec(), Vec()),
        OperandEnumerant("Rg32i", 25L, Vec(), Vec()),
        OperandEnumerant("Rg16i", 26L, Vec(), Vec()),
        OperandEnumerant("Rg8i", 27L, Vec(), Vec()),
        OperandEnumerant("R16i", 28L, Vec(), Vec()),
        OperandEnumerant("R8i", 29L, Vec(), Vec()),
        OperandEnumerant("Rgba32ui", 30L, Vec(), Vec()),
        OperandEnumerant("Rgba16ui", 31L, Vec(), Vec()),
        OperandEnumerant("Rgba8ui", 32L, Vec(), Vec()),
        OperandEnumerant("R32ui", 33L, Vec(), Vec()),
        OperandEnumerant("Rgb10a2ui", 34L, Vec(), Vec()),
        OperandEnumerant("Rg32ui", 35L, Vec(), Vec()),
        OperandEnumerant("Rg16ui", 36L, Vec(), Vec()),
        OperandEnumerant("Rg8ui", 37L, Vec(), Vec()),
        OperandEnumerant("R16ui", 38L, Vec(), Vec()),
        OperandEnumerant("R8ui", 39L, Vec(), Vec()),
        OperandEnumerant("R64ui", 40L, Vec(), Vec()),
        OperandEnumerant("R64i", 41L, Vec(), Vec())
      )
    ),
    OperandKindInfo(
      OperandKind.ImageChannelOrder,
      Vec(
        OperandEnumerant("R", 0L, Vec(), Vec()),
        OperandEnumerant("A", 1L, Vec(), Vec()),
        OperandEnumerant("RG", 2L, Vec(), Vec()),
        OperandEnumerant("RA", 3L, Vec(), Vec()),
        OperandEnumerant("RGB", 4L, Vec(), Vec()),
        OperandEnumerant("RGBA", 5L, Vec(), Vec()),
        OperandEnumerant("BGRA", 6L, Vec(), Vec()),
        OperandEnumerant("ARGB", 7L, Vec(), Vec()),
        OperandEnumerant("Intensity", 8L, Vec(), Vec()),
        OperandEnumerant("Luminance", 9L, Vec(), Vec()),
        OperandEnumerant("Rx", 10L, Vec(), Vec()),
        OperandEnumerant("RGx", 11L, Vec(), Vec()),
        OperandEnumerant("RGBx", 12L, Vec(), Vec()),
        OperandEnumerant("Depth", 13L, Vec(), Vec()),
        OperandEnumerant("DepthStencil", 14L, Vec(), Vec()),
        OperandEnumerant("sRGB", 15L, Vec(), Vec()),
        OperandEnumerant("sRGBx", 16L, Vec(), Vec()),
        OperandEnumerant("sRGBA", 17L, Vec(), Vec()),
        OperandEnumerant("sBGRA", 18L, Vec(), Vec()),
        OperandEnumerant("ABGR", 19L, Vec(), Vec())
      )
    ),
    OperandKindInfo(
      OperandKind.ImageChannelDataType,
      Vec(
        OperandEnumerant("SnormInt8", 0L, Vec(), Vec()),
        OperandEnumerant("SnormInt16", 1L, Vec(), Vec()),
        OperandEnumerant("UnormInt8", 2L, Vec(), Vec()),
        OperandEnumerant("UnormInt16", 3L, Vec(), Vec()),
        OperandEnumerant("UnormShort565", 4L, Vec(), Vec()),
        OperandEnumerant("UnormShort555", 5L, Vec(), Vec()),
        OperandEnumerant("UnormInt101010", 6L, Vec(), Vec()),
        OperandEnumerant("SignedInt8", 7L, Vec(), Vec()),
        OperandEnumerant("SignedInt16", 8L, Vec(), Vec()),
        OperandEnumerant("SignedInt32", 9L, Vec(), Vec()),
        OperandEnumerant("UnsignedInt8", 10L, Vec(), Vec()),
        OperandEnumerant("UnsignedInt16", 11L, Vec(), Vec()),
        OperandEnumerant("UnsignedInt32", 12L, Vec(), Vec()),
        OperandEnumerant("HalfFloat", 13L, Vec(), Vec()),
        OperandEnumerant("Float", 14L, Vec(), Vec()),
        OperandEnumerant("UnormInt24", 15L, Vec(), Vec()),
        OperandEnumerant("UnormInt101010_2", 16L, Vec(), Vec()),
        OperandEnumerant("UnormInt10X6EXT", 17L, Vec(), Vec()),
        OperandEnumerant("UnsignedIntRaw10EXT", 19L, Vec(), Vec()),
        OperandEnumerant("UnsignedIntRaw12EXT", 20L, Vec(), Vec()),
        OperandEnumerant("UnormInt2_101010EXT", 21L, Vec(), Vec()),
        OperandEnumerant("UnsignedInt10X6EXT", 22L, Vec(), Vec()),
        OperandEnumerant("UnsignedInt12X4EXT", 23L, Vec(), Vec()),
        OperandEnumerant("UnsignedInt14X2EXT", 24L, Vec(), Vec()),
        OperandEnumerant("UnormInt12X4EXT", 25L, Vec(), Vec()),
        OperandEnumerant("UnormInt14X2EXT", 26L, Vec(), Vec())
      )
    ),
    OperandKindInfo(
      OperandKind.FPRoundingMode,
      Vec(
        OperandEnumerant("RTE", 0L, Vec(), Vec()),
        OperandEnumerant("RTZ", 1L, Vec(), Vec()),
        OperandEnumerant("RTP", 2L, Vec(), Vec()),
        OperandEnumerant("RTN", 3L, Vec(), Vec())
      )
    )
  )

  private def operandKinds3: Vec[OperandKindInfo] = Vec(
    OperandKindInfo(
      OperandKind.FPDenormMode,
      Vec(OperandEnumerant("Preserve", 0L, Vec(), Vec()), OperandEnumerant("FlushToZero", 1L, Vec(), Vec()))
    ),
    OperandKindInfo(
      OperandKind.QuantizationModes,
      Vec(
        OperandEnumerant("TRN", 0L, Vec(), Vec()),
        OperandEnumerant("TRN_ZERO", 1L, Vec(), Vec()),
        OperandEnumerant("RND", 2L, Vec(), Vec()),
        OperandEnumerant("RND_ZERO", 3L, Vec(), Vec()),
        OperandEnumerant("RND_INF", 4L, Vec(), Vec()),
        OperandEnumerant("RND_MIN_INF", 5L, Vec(), Vec()),
        OperandEnumerant("RND_CONV", 6L, Vec(), Vec()),
        OperandEnumerant("RND_CONV_ODD", 7L, Vec(), Vec())
      )
    ),
    OperandKindInfo(
      OperandKind.FPOperationMode,
      Vec(OperandEnumerant("IEEE", 0L, Vec(), Vec()), OperandEnumerant("ALT", 1L, Vec(), Vec()))
    ),
    OperandKindInfo(
      OperandKind.OverflowModes,
      Vec(
        OperandEnumerant("WRAP", 0L, Vec(), Vec()),
        OperandEnumerant("SAT", 1L, Vec(), Vec()),
        OperandEnumerant("SAT_ZERO", 2L, Vec(), Vec()),
        OperandEnumerant("SAT_SYM", 3L, Vec(), Vec())
      )
    ),
    OperandKindInfo(
      OperandKind.LinkageType,
      Vec(
        OperandEnumerant("Export", 0L, Vec(), Vec()),
        OperandEnumerant("Import", 1L, Vec(), Vec()),
        OperandEnumerant("LinkOnceODR", 2L, Vec(), Vec()),
        OperandEnumerant("WeakAMD", 3L, Vec(), Vec())
      )
    ),
    OperandKindInfo(
      OperandKind.AccessQualifier,
      Vec(
        OperandEnumerant("ReadOnly", 0L, Vec(), Vec()),
        OperandEnumerant("WriteOnly", 1L, Vec(), Vec()),
        OperandEnumerant("ReadWrite", 2L, Vec(), Vec())
      )
    ),
    OperandKindInfo(
      OperandKind.HostAccessQualifier,
      Vec(
        OperandEnumerant("NoneINTEL", 0L, Vec(), Vec()),
        OperandEnumerant("ReadINTEL", 1L, Vec(), Vec()),
        OperandEnumerant("WriteINTEL", 2L, Vec(), Vec()),
        OperandEnumerant("ReadWriteINTEL", 3L, Vec(), Vec())
      )
    ),
    OperandKindInfo(
      OperandKind.FunctionParameterAttribute,
      Vec(
        OperandEnumerant("Zext", 0L, Vec(), Vec()),
        OperandEnumerant("Sext", 1L, Vec(), Vec()),
        OperandEnumerant("ByVal", 2L, Vec(), Vec()),
        OperandEnumerant("Sret", 3L, Vec(), Vec()),
        OperandEnumerant("NoAlias", 4L, Vec(), Vec()),
        OperandEnumerant("NoCapture", 5L, Vec(), Vec()),
        OperandEnumerant("NoWrite", 6L, Vec(), Vec()),
        OperandEnumerant("NoReadWrite", 7L, Vec(), Vec()),
        OperandEnumerant("RuntimeAlignedALTERA", 5940L, Vec(), Vec("RuntimeAlignedINTEL"))
      )
    )
  )

  private def operandKinds4: Vec[OperandKindInfo] = Vec(
    OperandKindInfo(
      OperandKind.Decoration,
      Vec(
        OperandEnumerant("RelaxedPrecision", 0L, Vec(), Vec()),
        OperandEnumerant("SpecId", 1L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant("Block", 2L, Vec(), Vec()),
        OperandEnumerant("BufferBlock", 3L, Vec(), Vec()),
        OperandEnumerant("RowMajor", 4L, Vec(), Vec()),
        OperandEnumerant("ColMajor", 5L, Vec(), Vec()),
        OperandEnumerant("ArrayStride", 6L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant("MatrixStride", 7L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant("GLSLShared", 8L, Vec(), Vec()),
        OperandEnumerant("GLSLPacked", 9L, Vec(), Vec()),
        OperandEnumerant("CPacked", 10L, Vec(), Vec()),
        OperandEnumerant("BuiltIn", 11L, Vec(OperandKind.BuiltIn), Vec()),
        OperandEnumerant("NoPerspective", 13L, Vec(), Vec()),
        OperandEnumerant("Flat", 14L, Vec(), Vec()),
        OperandEnumerant("Patch", 15L, Vec(), Vec()),
        OperandEnumerant("Centroid", 16L, Vec(), Vec()),
        OperandEnumerant("Sample", 17L, Vec(), Vec()),
        OperandEnumerant("Invariant", 18L, Vec(), Vec()),
        OperandEnumerant("Restrict", 19L, Vec(), Vec()),
        OperandEnumerant("Aliased", 20L, Vec(), Vec()),
        OperandEnumerant("Volatile", 21L, Vec(), Vec()),
        OperandEnumerant("Constant", 22L, Vec(), Vec()),
        OperandEnumerant("Coherent", 23L, Vec(), Vec()),
        OperandEnumerant("NonWritable", 24L, Vec(), Vec()),
        OperandEnumerant("NonReadable", 25L, Vec(), Vec()),
        OperandEnumerant("Uniform", 26L, Vec(), Vec()),
        OperandEnumerant("UniformId", 27L, Vec(OperandKind.IdScope), Vec()),
        OperandEnumerant("SaturatedConversion", 28L, Vec(), Vec()),
        OperandEnumerant("Stream", 29L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant("Location", 30L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant("Component", 31L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant("Index", 32L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant("Binding", 33L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant("DescriptorSet", 34L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant("Offset", 35L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant("XfbBuffer", 36L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant("XfbStride", 37L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant("FuncParamAttr", 38L, Vec(OperandKind.FunctionParameterAttribute), Vec()),
        OperandEnumerant("FPRoundingMode", 39L, Vec(OperandKind.FPRoundingMode), Vec()),
        OperandEnumerant("FPFastMathMode", 40L, Vec(OperandKind.FPFastMathMode), Vec()),
        OperandEnumerant("LinkageAttributes", 41L, Vec(OperandKind.LiteralString, OperandKind.LinkageType), Vec()),
        OperandEnumerant("NoContraction", 42L, Vec(), Vec()),
        OperandEnumerant("InputAttachmentIndex", 43L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant("Alignment", 44L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant("MaxByteOffset", 45L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant("AlignmentId", 46L, Vec(OperandKind.IdRef), Vec()),
        OperandEnumerant("MaxByteOffsetId", 47L, Vec(OperandKind.IdRef), Vec()),
        OperandEnumerant("SaturatedToLargestFloat8NormalConversionEXT", 4216L, Vec(), Vec()),
        OperandEnumerant("NoSignedWrap", 4469L, Vec(), Vec()),
        OperandEnumerant("NoUnsignedWrap", 4470L, Vec(), Vec()),
        OperandEnumerant("WeightTextureQCOM", 4487L, Vec(), Vec()),
        OperandEnumerant("BlockMatchTextureQCOM", 4488L, Vec(), Vec()),
        OperandEnumerant("BlockMatchSamplerQCOM", 4499L, Vec(), Vec()),
        OperandEnumerant("ExplicitInterpAMD", 4999L, Vec(), Vec()),
        OperandEnumerant("NodeSharesPayloadLimitsWithAMDX", 5019L, Vec(OperandKind.IdRef), Vec()),
        OperandEnumerant("NodeMaxPayloadsAMDX", 5020L, Vec(OperandKind.IdRef), Vec()),
        OperandEnumerant("TrackFinishWritingAMDX", 5078L, Vec(), Vec()),
        OperandEnumerant("PayloadNodeNameAMDX", 5091L, Vec(OperandKind.IdRef), Vec()),
        OperandEnumerant("PayloadNodeBaseIndexAMDX", 5098L, Vec(OperandKind.IdRef), Vec()),
        OperandEnumerant("PayloadNodeSparseArrayAMDX", 5099L, Vec(), Vec()),
        OperandEnumerant("PayloadNodeArraySizeAMDX", 5100L, Vec(OperandKind.IdRef), Vec()),
        OperandEnumerant("PayloadDispatchIndirectAMDX", 5105L, Vec(), Vec()),
        OperandEnumerant("ArrayStrideIdEXT", 5124L, Vec(OperandKind.IdRef), Vec()),
        OperandEnumerant("OffsetIdEXT", 5125L, Vec(OperandKind.IdRef), Vec()),
        OperandEnumerant("UTFEncodedKHR", 5145L, Vec(), Vec()),
        OperandEnumerant("OverrideCoverageNV", 5248L, Vec(), Vec()),
        OperandEnumerant("PassthroughNV", 5250L, Vec(), Vec()),
        OperandEnumerant("ViewportRelativeNV", 5252L, Vec(), Vec()),
        OperandEnumerant("SecondaryViewportRelativeNV", 5256L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant("PerPrimitiveEXT", 5271L, Vec(), Vec("PerPrimitiveNV")),
        OperandEnumerant("PerViewNV", 5272L, Vec(), Vec()),
        OperandEnumerant("PerTaskNV", 5273L, Vec(), Vec()),
        OperandEnumerant("PerVertexKHR", 5285L, Vec(), Vec("PerVertexNV")),
        OperandEnumerant("NonUniform", 5300L, Vec(), Vec("NonUniformEXT")),
        OperandEnumerant("RestrictPointer", 5355L, Vec(), Vec("RestrictPointerEXT")),
        OperandEnumerant("AliasedPointer", 5356L, Vec(), Vec("AliasedPointerEXT")),
        OperandEnumerant("MemberOffsetNV", 5358L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant("HitObjectShaderRecordBufferNV", 5386L, Vec(), Vec()),
        OperandEnumerant("HitObjectShaderRecordBufferEXT", 5389L, Vec(), Vec()),
        OperandEnumerant("BankNV", 5397L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant("BindlessSamplerNV", 5398L, Vec(), Vec()),
        OperandEnumerant("BindlessImageNV", 5399L, Vec(), Vec()),
        OperandEnumerant("BoundSamplerNV", 5400L, Vec(), Vec()),
        OperandEnumerant("BoundImageNV", 5401L, Vec(), Vec()),
        OperandEnumerant("SIMTCallINTEL", 5599L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant("ReferencedIndirectlyINTEL", 5602L, Vec(), Vec()),
        OperandEnumerant("ClobberINTEL", 5607L, Vec(OperandKind.LiteralString), Vec()),
        OperandEnumerant("SideEffectsINTEL", 5608L, Vec(), Vec()),
        OperandEnumerant("VectorComputeVariableINTEL", 5624L, Vec(), Vec()),
        OperandEnumerant("FuncParamIOKindINTEL", 5625L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant("VectorComputeFunctionINTEL", 5626L, Vec(), Vec()),
        OperandEnumerant("StackCallINTEL", 5627L, Vec(), Vec()),
        OperandEnumerant("GlobalVariableOffsetINTEL", 5628L, Vec(OperandKind.LiteralInteger), Vec()),
        OperandEnumerant("CounterBuffer", 5634L, Vec(OperandKind.IdRef), Vec("HlslCounterBufferGOOGLE")),
        OperandEnumerant("UserSemantic", 5635L, Vec(OperandKind.LiteralString), Vec("HlslSemanticGOOGLE")),
        OperandEnumerant("UserTypeGOOGLE", 5636L, Vec(OperandKind.LiteralString), Vec()),
        OperandEnumerant(
          "FunctionRoundingModeINTEL",
          5822L,
          Vec(OperandKind.LiteralInteger, OperandKind.FPRoundingMode),
          Vec()
        ),
        OperandEnumerant(
          "FunctionDenormModeINTEL",
          5823L,
          Vec(OperandKind.LiteralInteger, OperandKind.FPDenormMode),
          Vec()
        ),
        OperandEnumerant("RegisterALTERA", 5825L, Vec(), Vec("RegisterINTEL")),
        OperandEnumerant("MemoryALTERA", 5826L, Vec(OperandKind.LiteralString), Vec("MemoryINTEL")),
        OperandEnumerant("NumbanksALTERA", 5827L, Vec(OperandKind.LiteralInteger), Vec("NumbanksINTEL")),
        OperandEnumerant("BankwidthALTERA", 5828L, Vec(OperandKind.LiteralInteger), Vec("BankwidthINTEL")),
        OperandEnumerant(
          "MaxPrivateCopiesALTERA",
          5829L,
          Vec(OperandKind.LiteralInteger),
          Vec("MaxPrivateCopiesINTEL")
        ),
        OperandEnumerant("SinglepumpALTERA", 5830L, Vec(), Vec("SinglepumpINTEL")),
        OperandEnumerant("DoublepumpALTERA", 5831L, Vec(), Vec("DoublepumpINTEL")),
        OperandEnumerant("MaxReplicatesALTERA", 5832L, Vec(OperandKind.LiteralInteger), Vec("MaxReplicatesINTEL")),
        OperandEnumerant("SimpleDualPortALTERA", 5833L, Vec(), Vec("SimpleDualPortINTEL")),
        OperandEnumerant(
          "MergeALTERA",
          5834L,
          Vec(OperandKind.LiteralString, OperandKind.LiteralString),
          Vec("MergeINTEL")
        ),
        OperandEnumerant("BankBitsALTERA", 5835L, Vec(OperandKind.LiteralInteger), Vec("BankBitsINTEL")),
        OperandEnumerant("ForcePow2DepthALTERA", 5836L, Vec(OperandKind.LiteralInteger), Vec("ForcePow2DepthINTEL")),
        OperandEnumerant("StridesizeALTERA", 5883L, Vec(OperandKind.LiteralInteger), Vec("StridesizeINTEL")),
        OperandEnumerant("WordsizeALTERA", 5884L, Vec(OperandKind.LiteralInteger), Vec("WordsizeINTEL")),
        OperandEnumerant("TrueDualPortALTERA", 5885L, Vec(), Vec("TrueDualPortINTEL")),
        OperandEnumerant("BurstCoalesceALTERA", 5899L, Vec(), Vec("BurstCoalesceINTEL")),
        OperandEnumerant("CacheSizeALTERA", 5900L, Vec(OperandKind.LiteralInteger), Vec("CacheSizeINTEL")),
        OperandEnumerant("DontStaticallyCoalesceALTERA", 5901L, Vec(), Vec("DontStaticallyCoalesceINTEL")),
        OperandEnumerant("PrefetchALTERA", 5902L, Vec(OperandKind.LiteralInteger), Vec("PrefetchINTEL")),
        OperandEnumerant("StallEnableALTERA", 5905L, Vec(), Vec("StallEnableINTEL")),
        OperandEnumerant("FuseLoopsInFunctionALTERA", 5907L, Vec(), Vec("FuseLoopsInFunctionINTEL")),
        OperandEnumerant(
          "MathOpDSPModeALTERA",
          5909L,
          Vec(OperandKind.LiteralInteger, OperandKind.LiteralInteger),
          Vec("MathOpDSPModeINTEL")
        ),
        OperandEnumerant("AliasScopeINTEL", 5914L, Vec(OperandKind.IdRef), Vec()),
        OperandEnumerant("NoAliasINTEL", 5915L, Vec(OperandKind.IdRef), Vec()),
        OperandEnumerant(
          "InitiationIntervalALTERA",
          5917L,
          Vec(OperandKind.LiteralInteger),
          Vec("InitiationIntervalINTEL")
        ),
        OperandEnumerant("MaxConcurrencyALTERA", 5918L, Vec(OperandKind.LiteralInteger), Vec("MaxConcurrencyINTEL")),
        OperandEnumerant("PipelineEnableALTERA", 5919L, Vec(OperandKind.LiteralInteger), Vec("PipelineEnableINTEL")),
        OperandEnumerant("BufferLocationALTERA", 5921L, Vec(OperandKind.LiteralInteger), Vec("BufferLocationINTEL")),
        OperandEnumerant("IOPipeStorageALTERA", 5944L, Vec(OperandKind.LiteralInteger), Vec("IOPipeStorageINTEL")),
        OperandEnumerant(
          "FunctionFloatingPointModeINTEL",
          6080L,
          Vec(OperandKind.LiteralInteger, OperandKind.FPOperationMode),
          Vec()
        ),
        OperandEnumerant("SingleElementVectorINTEL", 6085L, Vec(), Vec()),
        OperandEnumerant("VectorComputeCallableFunctionINTEL", 6087L, Vec(), Vec()),
        OperandEnumerant("MediaBlockIOINTEL", 6140L, Vec(), Vec()),
        OperandEnumerant("StallFreeALTERA", 6151L, Vec(), Vec("StallFreeINTEL")),
        OperandEnumerant("FPMaxErrorDecorationINTEL", 6170L, Vec(OperandKind.LiteralFloat), Vec()),
        OperandEnumerant(
          "LatencyControlLabelALTERA",
          6172L,
          Vec(OperandKind.LiteralInteger),
          Vec("LatencyControlLabelINTEL")
        ),
        OperandEnumerant(
          "LatencyControlConstraintALTERA",
          6173L,
          Vec(OperandKind.LiteralInteger, OperandKind.LiteralInteger, OperandKind.LiteralInteger),
          Vec("LatencyControlConstraintINTEL")
        ),
        OperandEnumerant("ConduitKernelArgumentALTERA", 6175L, Vec(), Vec("ConduitKernelArgumentINTEL")),
        OperandEnumerant("RegisterMapKernelArgumentALTERA", 6176L, Vec(), Vec("RegisterMapKernelArgumentINTEL")),
        OperandEnumerant(
          "MMHostInterfaceAddressWidthALTERA",
          6177L,
          Vec(OperandKind.LiteralInteger),
          Vec("MMHostInterfaceAddressWidthINTEL")
        ),
        OperandEnumerant(
          "MMHostInterfaceDataWidthALTERA",
          6178L,
          Vec(OperandKind.LiteralInteger),
          Vec("MMHostInterfaceDataWidthINTEL")
        ),
        OperandEnumerant(
          "MMHostInterfaceLatencyALTERA",
          6179L,
          Vec(OperandKind.LiteralInteger),
          Vec("MMHostInterfaceLatencyINTEL")
        ),
        OperandEnumerant(
          "MMHostInterfaceReadWriteModeALTERA",
          6180L,
          Vec(OperandKind.AccessQualifier),
          Vec("MMHostInterfaceReadWriteModeINTEL")
        ),
        OperandEnumerant(
          "MMHostInterfaceMaxBurstALTERA",
          6181L,
          Vec(OperandKind.LiteralInteger),
          Vec("MMHostInterfaceMaxBurstINTEL")
        ),
        OperandEnumerant(
          "MMHostInterfaceWaitRequestALTERA",
          6182L,
          Vec(OperandKind.LiteralInteger),
          Vec("MMHostInterfaceWaitRequestINTEL")
        ),
        OperandEnumerant("StableKernelArgumentALTERA", 6183L, Vec(), Vec("StableKernelArgumentINTEL")),
        OperandEnumerant(
          "HostAccessINTEL",
          6188L,
          Vec(OperandKind.HostAccessQualifier, OperandKind.LiteralString),
          Vec()
        ),
        OperandEnumerant("InitModeALTERA", 6190L, Vec(OperandKind.InitializationModeQualifier), Vec("InitModeINTEL")),
        OperandEnumerant(
          "ImplementInRegisterMapALTERA",
          6191L,
          Vec(OperandKind.LiteralInteger),
          Vec("ImplementInRegisterMapINTEL")
        ),
        OperandEnumerant("ConditionalINTEL", 6247L, Vec(OperandKind.IdRef), Vec()),
        OperandEnumerant(
          "CacheControlLoadINTEL",
          6442L,
          Vec(OperandKind.LiteralInteger, OperandKind.LoadCacheControl),
          Vec()
        ),
        OperandEnumerant(
          "CacheControlStoreINTEL",
          6443L,
          Vec(OperandKind.LiteralInteger, OperandKind.StoreCacheControl),
          Vec()
        ),
        OperandEnumerant("IntrinsicSAMSUNG", 7040L, Vec(OperandKind.LiteralInteger), Vec())
      )
    ),
    OperandKindInfo(
      OperandKind.BuiltIn,
      Vec(
        OperandEnumerant("Position", 0L, Vec(), Vec()),
        OperandEnumerant("PointSize", 1L, Vec(), Vec()),
        OperandEnumerant("ClipDistance", 3L, Vec(), Vec()),
        OperandEnumerant("CullDistance", 4L, Vec(), Vec()),
        OperandEnumerant("VertexId", 5L, Vec(), Vec()),
        OperandEnumerant("InstanceId", 6L, Vec(), Vec()),
        OperandEnumerant("PrimitiveId", 7L, Vec(), Vec()),
        OperandEnumerant("InvocationId", 8L, Vec(), Vec()),
        OperandEnumerant("Layer", 9L, Vec(), Vec()),
        OperandEnumerant("ViewportIndex", 10L, Vec(), Vec()),
        OperandEnumerant("TessLevelOuter", 11L, Vec(), Vec()),
        OperandEnumerant("TessLevelInner", 12L, Vec(), Vec()),
        OperandEnumerant("TessCoord", 13L, Vec(), Vec()),
        OperandEnumerant("PatchVertices", 14L, Vec(), Vec()),
        OperandEnumerant("FragCoord", 15L, Vec(), Vec()),
        OperandEnumerant("PointCoord", 16L, Vec(), Vec()),
        OperandEnumerant("FrontFacing", 17L, Vec(), Vec()),
        OperandEnumerant("SampleId", 18L, Vec(), Vec()),
        OperandEnumerant("SamplePosition", 19L, Vec(), Vec()),
        OperandEnumerant("SampleMask", 20L, Vec(), Vec()),
        OperandEnumerant("FragDepth", 22L, Vec(), Vec()),
        OperandEnumerant("HelperInvocation", 23L, Vec(), Vec()),
        OperandEnumerant("NumWorkgroups", 24L, Vec(), Vec()),
        OperandEnumerant("WorkgroupSize", 25L, Vec(), Vec()),
        OperandEnumerant("WorkgroupId", 26L, Vec(), Vec()),
        OperandEnumerant("LocalInvocationId", 27L, Vec(), Vec()),
        OperandEnumerant("GlobalInvocationId", 28L, Vec(), Vec()),
        OperandEnumerant("LocalInvocationIndex", 29L, Vec(), Vec()),
        OperandEnumerant("WorkDim", 30L, Vec(), Vec()),
        OperandEnumerant("GlobalSize", 31L, Vec(), Vec()),
        OperandEnumerant("EnqueuedWorkgroupSize", 32L, Vec(), Vec()),
        OperandEnumerant("GlobalOffset", 33L, Vec(), Vec()),
        OperandEnumerant("GlobalLinearId", 34L, Vec(), Vec()),
        OperandEnumerant("SubgroupSize", 36L, Vec(), Vec()),
        OperandEnumerant("SubgroupMaxSize", 37L, Vec(), Vec()),
        OperandEnumerant("NumSubgroups", 38L, Vec(), Vec()),
        OperandEnumerant("NumEnqueuedSubgroups", 39L, Vec(), Vec()),
        OperandEnumerant("SubgroupId", 40L, Vec(), Vec()),
        OperandEnumerant("SubgroupLocalInvocationId", 41L, Vec(), Vec()),
        OperandEnumerant("VertexIndex", 42L, Vec(), Vec()),
        OperandEnumerant("InstanceIndex", 43L, Vec(), Vec()),
        OperandEnumerant("CoreIDARM", 4160L, Vec(), Vec()),
        OperandEnumerant("CoreCountARM", 4161L, Vec(), Vec()),
        OperandEnumerant("CoreMaxIDARM", 4162L, Vec(), Vec()),
        OperandEnumerant("WarpIDARM", 4163L, Vec(), Vec()),
        OperandEnumerant("WarpMaxIDARM", 4164L, Vec(), Vec()),
        OperandEnumerant("SubgroupEqMask", 4416L, Vec(), Vec("SubgroupEqMaskKHR")),
        OperandEnumerant("SubgroupGeMask", 4417L, Vec(), Vec("SubgroupGeMaskKHR")),
        OperandEnumerant("SubgroupGtMask", 4418L, Vec(), Vec("SubgroupGtMaskKHR")),
        OperandEnumerant("SubgroupLeMask", 4419L, Vec(), Vec("SubgroupLeMaskKHR")),
        OperandEnumerant("SubgroupLtMask", 4420L, Vec(), Vec("SubgroupLtMaskKHR")),
        OperandEnumerant("BaseVertex", 4424L, Vec(), Vec()),
        OperandEnumerant("BaseInstance", 4425L, Vec(), Vec()),
        OperandEnumerant("DrawIndex", 4426L, Vec(), Vec()),
        OperandEnumerant("PrimitiveShadingRateKHR", 4432L, Vec(), Vec()),
        OperandEnumerant("DeviceIndex", 4438L, Vec(), Vec()),
        OperandEnumerant("ViewIndex", 4440L, Vec(), Vec()),
        OperandEnumerant("ShadingRateKHR", 4444L, Vec(), Vec()),
        OperandEnumerant("TileOffsetQCOM", 4492L, Vec(), Vec()),
        OperandEnumerant("TileDimensionQCOM", 4493L, Vec(), Vec()),
        OperandEnumerant("TileApronSizeQCOM", 4494L, Vec(), Vec()),
        OperandEnumerant("BaryCoordNoPerspAMD", 4992L, Vec(), Vec()),
        OperandEnumerant("BaryCoordNoPerspCentroidAMD", 4993L, Vec(), Vec()),
        OperandEnumerant("BaryCoordNoPerspSampleAMD", 4994L, Vec(), Vec()),
        OperandEnumerant("BaryCoordSmoothAMD", 4995L, Vec(), Vec()),
        OperandEnumerant("BaryCoordSmoothCentroidAMD", 4996L, Vec(), Vec()),
        OperandEnumerant("BaryCoordSmoothSampleAMD", 4997L, Vec(), Vec()),
        OperandEnumerant("BaryCoordPullModelAMD", 4998L, Vec(), Vec()),
        OperandEnumerant("FragStencilRefEXT", 5014L, Vec(), Vec()),
        OperandEnumerant("RemainingRecursionLevelsAMDX", 5021L, Vec(), Vec()),
        OperandEnumerant("ShaderIndexAMDX", 5073L, Vec(), Vec()),
        OperandEnumerant("SamplerHeapEXT", 5122L, Vec(), Vec()),
        OperandEnumerant("ResourceHeapEXT", 5123L, Vec(), Vec()),
        OperandEnumerant("ViewportMaskNV", 5253L, Vec(), Vec()),
        OperandEnumerant("SecondaryPositionNV", 5257L, Vec(), Vec()),
        OperandEnumerant("SecondaryViewportMaskNV", 5258L, Vec(), Vec()),
        OperandEnumerant("PositionPerViewNV", 5261L, Vec(), Vec()),
        OperandEnumerant("ViewportMaskPerViewNV", 5262L, Vec(), Vec()),
        OperandEnumerant("FullyCoveredEXT", 5264L, Vec(), Vec()),
        OperandEnumerant("TaskCountNV", 5274L, Vec(), Vec()),
        OperandEnumerant("PrimitiveCountNV", 5275L, Vec(), Vec()),
        OperandEnumerant("PrimitiveIndicesNV", 5276L, Vec(), Vec()),
        OperandEnumerant("ClipDistancePerViewNV", 5277L, Vec(), Vec()),
        OperandEnumerant("CullDistancePerViewNV", 5278L, Vec(), Vec()),
        OperandEnumerant("LayerPerViewNV", 5279L, Vec(), Vec()),
        OperandEnumerant("MeshViewCountNV", 5280L, Vec(), Vec()),
        OperandEnumerant("MeshViewIndicesNV", 5281L, Vec(), Vec()),
        OperandEnumerant("BaryCoordKHR", 5286L, Vec(), Vec("BaryCoordNV")),
        OperandEnumerant("BaryCoordNoPerspKHR", 5287L, Vec(), Vec("BaryCoordNoPerspNV")),
        OperandEnumerant("FragSizeEXT", 5292L, Vec(), Vec("FragmentSizeNV")),
        OperandEnumerant("FragInvocationCountEXT", 5293L, Vec(), Vec("InvocationsPerPixelNV")),
        OperandEnumerant("PrimitivePointIndicesEXT", 5294L, Vec(), Vec()),
        OperandEnumerant("PrimitiveLineIndicesEXT", 5295L, Vec(), Vec()),
        OperandEnumerant("PrimitiveTriangleIndicesEXT", 5296L, Vec(), Vec()),
        OperandEnumerant("CullPrimitiveEXT", 5299L, Vec(), Vec()),
        OperandEnumerant("LaunchIdKHR", 5319L, Vec(), Vec("LaunchIdNV")),
        OperandEnumerant("LaunchSizeKHR", 5320L, Vec(), Vec("LaunchSizeNV")),
        OperandEnumerant("WorldRayOriginKHR", 5321L, Vec(), Vec("WorldRayOriginNV")),
        OperandEnumerant("WorldRayDirectionKHR", 5322L, Vec(), Vec("WorldRayDirectionNV")),
        OperandEnumerant("ObjectRayOriginKHR", 5323L, Vec(), Vec("ObjectRayOriginNV")),
        OperandEnumerant("ObjectRayDirectionKHR", 5324L, Vec(), Vec("ObjectRayDirectionNV")),
        OperandEnumerant("RayTminKHR", 5325L, Vec(), Vec("RayTminNV")),
        OperandEnumerant("RayTmaxKHR", 5326L, Vec(), Vec("RayTmaxNV")),
        OperandEnumerant("InstanceCustomIndexKHR", 5327L, Vec(), Vec("InstanceCustomIndexNV")),
        OperandEnumerant("ObjectToWorldKHR", 5330L, Vec(), Vec("ObjectToWorldNV")),
        OperandEnumerant("WorldToObjectKHR", 5331L, Vec(), Vec("WorldToObjectNV")),
        OperandEnumerant("HitTNV", 5332L, Vec(), Vec()),
        OperandEnumerant("HitKindKHR", 5333L, Vec(), Vec("HitKindNV")),
        OperandEnumerant("CurrentRayTimeNV", 5334L, Vec(), Vec()),
        OperandEnumerant("HitTriangleVertexPositionsKHR", 5335L, Vec(), Vec()),
        OperandEnumerant("HitMicroTriangleVertexPositionsNV", 5337L, Vec(), Vec()),
        OperandEnumerant("HitMicroTriangleVertexBarycentricsNV", 5344L, Vec(), Vec()),
        OperandEnumerant("IncomingRayFlagsKHR", 5351L, Vec(), Vec("IncomingRayFlagsNV")),
        OperandEnumerant("RayGeometryIndexKHR", 5352L, Vec(), Vec()),
        OperandEnumerant("HitIsSphereNV", 5359L, Vec(), Vec()),
        OperandEnumerant("HitIsLSSNV", 5360L, Vec(), Vec()),
        OperandEnumerant("HitSpherePositionNV", 5361L, Vec(), Vec()),
        OperandEnumerant("WarpsPerSMNV", 5374L, Vec(), Vec()),
        OperandEnumerant("SMCountNV", 5375L, Vec(), Vec()),
        OperandEnumerant("WarpIDNV", 5376L, Vec(), Vec()),
        OperandEnumerant("SMIDNV", 5377L, Vec(), Vec()),
        OperandEnumerant("HitLSSPositionsNV", 5396L, Vec(), Vec()),
        OperandEnumerant("HitKindFrontFacingMicroTriangleNV", 5405L, Vec(), Vec()),
        OperandEnumerant("HitKindBackFacingMicroTriangleNV", 5406L, Vec(), Vec()),
        OperandEnumerant("HitSphereRadiusNV", 5420L, Vec(), Vec()),
        OperandEnumerant("HitLSSRadiiNV", 5421L, Vec(), Vec()),
        OperandEnumerant("ClusterIDNV", 5436L, Vec(), Vec()),
        OperandEnumerant("CullMaskKHR", 6021L, Vec(), Vec())
      )
    ),
    OperandKindInfo(
      OperandKind.Scope,
      Vec(
        OperandEnumerant("CrossDevice", 0L, Vec(), Vec()),
        OperandEnumerant("Device", 1L, Vec(), Vec()),
        OperandEnumerant("Workgroup", 2L, Vec(), Vec()),
        OperandEnumerant("Subgroup", 3L, Vec(), Vec()),
        OperandEnumerant("Invocation", 4L, Vec(), Vec()),
        OperandEnumerant("QueueFamily", 5L, Vec(), Vec("QueueFamilyKHR")),
        OperandEnumerant("ShaderCallKHR", 6L, Vec(), Vec())
      )
    ),
    OperandKindInfo(
      OperandKind.GroupOperation,
      Vec(
        OperandEnumerant("Reduce", 0L, Vec(), Vec()),
        OperandEnumerant("InclusiveScan", 1L, Vec(), Vec()),
        OperandEnumerant("ExclusiveScan", 2L, Vec(), Vec()),
        OperandEnumerant("ClusteredReduce", 3L, Vec(), Vec()),
        OperandEnumerant("PartitionedReduceEXT", 6L, Vec(), Vec("PartitionedReduceNV")),
        OperandEnumerant("PartitionedInclusiveScanEXT", 7L, Vec(), Vec("PartitionedInclusiveScanNV")),
        OperandEnumerant("PartitionedExclusiveScanEXT", 8L, Vec(), Vec("PartitionedExclusiveScanNV"))
      )
    ),
    OperandKindInfo(
      OperandKind.KernelEnqueueFlags,
      Vec(
        OperandEnumerant("NoWait", 0L, Vec(), Vec()),
        OperandEnumerant("WaitKernel", 1L, Vec(), Vec()),
        OperandEnumerant("WaitWorkGroup", 2L, Vec(), Vec())
      )
    ),
    OperandKindInfo(
      OperandKind.Capability,
      Vec(
        OperandEnumerant("Matrix", 0L, Vec(), Vec()),
        OperandEnumerant("Shader", 1L, Vec(), Vec()),
        OperandEnumerant("Geometry", 2L, Vec(), Vec()),
        OperandEnumerant("Tessellation", 3L, Vec(), Vec()),
        OperandEnumerant("Addresses", 4L, Vec(), Vec()),
        OperandEnumerant("Linkage", 5L, Vec(), Vec()),
        OperandEnumerant("Kernel", 6L, Vec(), Vec()),
        OperandEnumerant("Vector16", 7L, Vec(), Vec()),
        OperandEnumerant("Float16Buffer", 8L, Vec(), Vec()),
        OperandEnumerant("Float16", 9L, Vec(), Vec()),
        OperandEnumerant("Float64", 10L, Vec(), Vec()),
        OperandEnumerant("Int64", 11L, Vec(), Vec()),
        OperandEnumerant("Int64Atomics", 12L, Vec(), Vec()),
        OperandEnumerant("ImageBasic", 13L, Vec(), Vec()),
        OperandEnumerant("ImageReadWrite", 14L, Vec(), Vec()),
        OperandEnumerant("ImageMipmap", 15L, Vec(), Vec()),
        OperandEnumerant("Pipes", 17L, Vec(), Vec()),
        OperandEnumerant("Groups", 18L, Vec(), Vec()),
        OperandEnumerant("DeviceEnqueue", 19L, Vec(), Vec()),
        OperandEnumerant("LiteralSampler", 20L, Vec(), Vec()),
        OperandEnumerant("AtomicStorage", 21L, Vec(), Vec()),
        OperandEnumerant("Int16", 22L, Vec(), Vec()),
        OperandEnumerant("TessellationPointSize", 23L, Vec(), Vec()),
        OperandEnumerant("GeometryPointSize", 24L, Vec(), Vec()),
        OperandEnumerant("ImageGatherExtended", 25L, Vec(), Vec()),
        OperandEnumerant("StorageImageMultisample", 27L, Vec(), Vec()),
        OperandEnumerant("UniformBufferArrayDynamicIndexing", 28L, Vec(), Vec()),
        OperandEnumerant("SampledImageArrayDynamicIndexing", 29L, Vec(), Vec()),
        OperandEnumerant("StorageBufferArrayDynamicIndexing", 30L, Vec(), Vec()),
        OperandEnumerant("StorageImageArrayDynamicIndexing", 31L, Vec(), Vec()),
        OperandEnumerant("ClipDistance", 32L, Vec(), Vec()),
        OperandEnumerant("CullDistance", 33L, Vec(), Vec()),
        OperandEnumerant("ImageCubeArray", 34L, Vec(), Vec()),
        OperandEnumerant("SampleRateShading", 35L, Vec(), Vec()),
        OperandEnumerant("ImageRect", 36L, Vec(), Vec()),
        OperandEnumerant("SampledRect", 37L, Vec(), Vec()),
        OperandEnumerant("GenericPointer", 38L, Vec(), Vec()),
        OperandEnumerant("Int8", 39L, Vec(), Vec()),
        OperandEnumerant("InputAttachment", 40L, Vec(), Vec()),
        OperandEnumerant("SparseResidency", 41L, Vec(), Vec()),
        OperandEnumerant("MinLod", 42L, Vec(), Vec()),
        OperandEnumerant("Sampled1D", 43L, Vec(), Vec()),
        OperandEnumerant("Image1D", 44L, Vec(), Vec()),
        OperandEnumerant("SampledCubeArray", 45L, Vec(), Vec()),
        OperandEnumerant("SampledBuffer", 46L, Vec(), Vec()),
        OperandEnumerant("ImageBuffer", 47L, Vec(), Vec()),
        OperandEnumerant("ImageMSArray", 48L, Vec(), Vec()),
        OperandEnumerant("StorageImageExtendedFormats", 49L, Vec(), Vec()),
        OperandEnumerant("ImageQuery", 50L, Vec(), Vec()),
        OperandEnumerant("DerivativeControl", 51L, Vec(), Vec()),
        OperandEnumerant("InterpolationFunction", 52L, Vec(), Vec()),
        OperandEnumerant("TransformFeedback", 53L, Vec(), Vec()),
        OperandEnumerant("GeometryStreams", 54L, Vec(), Vec()),
        OperandEnumerant("StorageImageReadWithoutFormat", 55L, Vec(), Vec()),
        OperandEnumerant("StorageImageWriteWithoutFormat", 56L, Vec(), Vec()),
        OperandEnumerant("MultiViewport", 57L, Vec(), Vec()),
        OperandEnumerant("SubgroupDispatch", 58L, Vec(), Vec()),
        OperandEnumerant("NamedBarrier", 59L, Vec(), Vec()),
        OperandEnumerant("PipeStorage", 60L, Vec(), Vec()),
        OperandEnumerant("GroupNonUniform", 61L, Vec(), Vec()),
        OperandEnumerant("GroupNonUniformVote", 62L, Vec(), Vec()),
        OperandEnumerant("GroupNonUniformArithmetic", 63L, Vec(), Vec()),
        OperandEnumerant("GroupNonUniformBallot", 64L, Vec(), Vec()),
        OperandEnumerant("GroupNonUniformShuffle", 65L, Vec(), Vec()),
        OperandEnumerant("GroupNonUniformShuffleRelative", 66L, Vec(), Vec()),
        OperandEnumerant("GroupNonUniformClustered", 67L, Vec(), Vec()),
        OperandEnumerant("GroupNonUniformQuad", 68L, Vec(), Vec()),
        OperandEnumerant("ShaderLayer", 69L, Vec(), Vec()),
        OperandEnumerant("ShaderViewportIndex", 70L, Vec(), Vec()),
        OperandEnumerant("UniformDecoration", 71L, Vec(), Vec()),
        OperandEnumerant("CoreBuiltinsARM", 4165L, Vec(), Vec()),
        OperandEnumerant("TileImageColorReadAccessEXT", 4166L, Vec(), Vec()),
        OperandEnumerant("TileImageDepthReadAccessEXT", 4167L, Vec(), Vec()),
        OperandEnumerant("TileImageStencilReadAccessEXT", 4168L, Vec(), Vec()),
        OperandEnumerant("TensorsARM", 4174L, Vec(), Vec()),
        OperandEnumerant("StorageTensorArrayDynamicIndexingARM", 4175L, Vec(), Vec()),
        OperandEnumerant("StorageTensorArrayNonUniformIndexingARM", 4176L, Vec(), Vec()),
        OperandEnumerant("GraphARM", 4191L, Vec(), Vec()),
        OperandEnumerant("CooperativeMatrixLayoutsARM", 4201L, Vec(), Vec()),
        OperandEnumerant("Float8EXT", 4212L, Vec(), Vec()),
        OperandEnumerant("Float8CooperativeMatrixEXT", 4213L, Vec(), Vec()),
        OperandEnumerant("Float6EXT", 4228L, Vec(), Vec()),
        OperandEnumerant("Float4EXT", 4229L, Vec(), Vec()),
        OperandEnumerant("Float8UnsignedE8M0EXT", 4230L, Vec(), Vec()),
        OperandEnumerant("MXInt8EXT", 4231L, Vec(), Vec()),
        OperandEnumerant("BitcastExtractEXT", 4232L, Vec(), Vec()),
        OperandEnumerant("FragmentShadingRateKHR", 4422L, Vec(), Vec()),
        OperandEnumerant("SubgroupBallotKHR", 4423L, Vec(), Vec()),
        OperandEnumerant("DrawParameters", 4427L, Vec(), Vec()),
        OperandEnumerant("WorkgroupMemoryExplicitLayoutKHR", 4428L, Vec(), Vec()),
        OperandEnumerant("WorkgroupMemoryExplicitLayout8BitAccessKHR", 4429L, Vec(), Vec()),
        OperandEnumerant("WorkgroupMemoryExplicitLayout16BitAccessKHR", 4430L, Vec(), Vec()),
        OperandEnumerant("SubgroupVoteKHR", 4431L, Vec(), Vec()),
        OperandEnumerant("StorageBuffer16BitAccess", 4433L, Vec(), Vec("StorageUniformBufferBlock16")),
        OperandEnumerant("UniformAndStorageBuffer16BitAccess", 4434L, Vec(), Vec("StorageUniform16")),
        OperandEnumerant("StoragePushConstant16", 4435L, Vec(), Vec()),
        OperandEnumerant("StorageInputOutput16", 4436L, Vec(), Vec()),
        OperandEnumerant("DeviceGroup", 4437L, Vec(), Vec()),
        OperandEnumerant("MultiView", 4439L, Vec(), Vec()),
        OperandEnumerant("VariablePointersStorageBuffer", 4441L, Vec(), Vec()),
        OperandEnumerant("VariablePointers", 4442L, Vec(), Vec()),
        OperandEnumerant("AtomicStorageOps", 4445L, Vec(), Vec()),
        OperandEnumerant("SampleMaskPostDepthCoverage", 4447L, Vec(), Vec()),
        OperandEnumerant("StorageBuffer8BitAccess", 4448L, Vec(), Vec()),
        OperandEnumerant("UniformAndStorageBuffer8BitAccess", 4449L, Vec(), Vec()),
        OperandEnumerant("StoragePushConstant8", 4450L, Vec(), Vec()),
        OperandEnumerant("DenormPreserve", 4464L, Vec(), Vec()),
        OperandEnumerant("DenormFlushToZero", 4465L, Vec(), Vec()),
        OperandEnumerant("SignedZeroInfNanPreserve", 4466L, Vec(), Vec()),
        OperandEnumerant("RoundingModeRTE", 4467L, Vec(), Vec()),
        OperandEnumerant("RoundingModeRTZ", 4468L, Vec(), Vec()),
        OperandEnumerant("RayQueryProvisionalKHR", 4471L, Vec(), Vec()),
        OperandEnumerant("RayQueryKHR", 4472L, Vec(), Vec()),
        OperandEnumerant("UntypedPointersKHR", 4473L, Vec(), Vec()),
        OperandEnumerant("RayTraversalPrimitiveCullingKHR", 4478L, Vec(), Vec()),
        OperandEnumerant("RayTracingKHR", 4479L, Vec(), Vec()),
        OperandEnumerant("TextureSampleWeightedQCOM", 4484L, Vec(), Vec()),
        OperandEnumerant("TextureBoxFilterQCOM", 4485L, Vec(), Vec()),
        OperandEnumerant("TextureBlockMatchQCOM", 4486L, Vec(), Vec()),
        OperandEnumerant("TileShadingQCOM", 4495L, Vec(), Vec()),
        OperandEnumerant("CooperativeMatrixConversionQCOM", 4496L, Vec(), Vec()),
        OperandEnumerant("TextureBlockMatch2QCOM", 4498L, Vec(), Vec()),
        OperandEnumerant("MultipleWaitQueuesQCOM", 4539L, Vec(), Vec()),
        OperandEnumerant("ImageGatherLinearQCOM", 4543L, Vec(), Vec()),
        OperandEnumerant("ImageGatherExtendedModesQCOM", 4544L, Vec(), Vec()),
        OperandEnumerant("Float16ImageAMD", 5008L, Vec(), Vec()),
        OperandEnumerant("ImageGatherBiasLodAMD", 5009L, Vec(), Vec()),
        OperandEnumerant("FragmentMaskAMD", 5010L, Vec(), Vec()),
        OperandEnumerant("StencilExportEXT", 5013L, Vec(), Vec()),
        OperandEnumerant("ImageReadWriteLodAMD", 5015L, Vec(), Vec()),
        OperandEnumerant("Int64ImageEXT", 5016L, Vec(), Vec()),
        OperandEnumerant("ShaderClockKHR", 5055L, Vec(), Vec()),
        OperandEnumerant("ShaderEnqueueAMDX", 5067L, Vec(), Vec()),
        OperandEnumerant("QuadControlKHR", 5087L, Vec(), Vec()),
        OperandEnumerant("Int4TypeINTEL", 5112L, Vec(), Vec()),
        OperandEnumerant("Int4CooperativeMatrixINTEL", 5114L, Vec(), Vec()),
        OperandEnumerant("BFloat16TypeKHR", 5116L, Vec(), Vec()),
        OperandEnumerant("BFloat16DotProductKHR", 5117L, Vec(), Vec()),
        OperandEnumerant("BFloat16CooperativeMatrixKHR", 5118L, Vec(), Vec()),
        OperandEnumerant("AbortKHR", 5120L, Vec(), Vec()),
        OperandEnumerant("DescriptorHeapEXT", 5128L, Vec(), Vec()),
        OperandEnumerant("ConstantDataKHR", 5146L, Vec(), Vec()),
        OperandEnumerant("PoisonFreezeKHR", 5156L, Vec(), Vec()),
        OperandEnumerant("WeakLinkageAMD", 5181L, Vec(), Vec()),
        OperandEnumerant("SampleMaskOverrideCoverageNV", 5249L, Vec(), Vec()),
        OperandEnumerant("GeometryShaderPassthroughNV", 5251L, Vec(), Vec()),
        OperandEnumerant("ShaderViewportIndexLayerEXT", 5254L, Vec(), Vec("ShaderViewportIndexLayerNV")),
        OperandEnumerant("ShaderViewportMaskNV", 5255L, Vec(), Vec()),
        OperandEnumerant("ShaderStereoViewNV", 5259L, Vec(), Vec()),
        OperandEnumerant("PerViewAttributesNV", 5260L, Vec(), Vec()),
        OperandEnumerant("FragmentFullyCoveredEXT", 5265L, Vec(), Vec()),
        OperandEnumerant("MeshShadingNV", 5266L, Vec(), Vec()),
        OperandEnumerant("ImageFootprintNV", 5282L, Vec(), Vec()),
        OperandEnumerant("MeshShadingEXT", 5283L, Vec(), Vec()),
        OperandEnumerant("FragmentBarycentricKHR", 5284L, Vec(), Vec("FragmentBarycentricNV")),
        OperandEnumerant("ComputeDerivativeGroupQuadsKHR", 5288L, Vec(), Vec("ComputeDerivativeGroupQuadsNV")),
        OperandEnumerant("FragmentDensityEXT", 5291L, Vec(), Vec("ShadingRateNV")),
        OperandEnumerant("GroupNonUniformPartitionedEXT", 5297L, Vec(), Vec("GroupNonUniformPartitionedNV")),
        OperandEnumerant("ShaderNonUniform", 5301L, Vec(), Vec("ShaderNonUniformEXT")),
        OperandEnumerant("RuntimeDescriptorArray", 5302L, Vec(), Vec("RuntimeDescriptorArrayEXT")),
        OperandEnumerant(
          "InputAttachmentArrayDynamicIndexing",
          5303L,
          Vec(),
          Vec("InputAttachmentArrayDynamicIndexingEXT")
        ),
        OperandEnumerant(
          "UniformTexelBufferArrayDynamicIndexing",
          5304L,
          Vec(),
          Vec("UniformTexelBufferArrayDynamicIndexingEXT")
        ),
        OperandEnumerant(
          "StorageTexelBufferArrayDynamicIndexing",
          5305L,
          Vec(),
          Vec("StorageTexelBufferArrayDynamicIndexingEXT")
        ),
        OperandEnumerant(
          "UniformBufferArrayNonUniformIndexing",
          5306L,
          Vec(),
          Vec("UniformBufferArrayNonUniformIndexingEXT")
        ),
        OperandEnumerant(
          "SampledImageArrayNonUniformIndexing",
          5307L,
          Vec(),
          Vec("SampledImageArrayNonUniformIndexingEXT")
        ),
        OperandEnumerant(
          "StorageBufferArrayNonUniformIndexing",
          5308L,
          Vec(),
          Vec("StorageBufferArrayNonUniformIndexingEXT")
        ),
        OperandEnumerant(
          "StorageImageArrayNonUniformIndexing",
          5309L,
          Vec(),
          Vec("StorageImageArrayNonUniformIndexingEXT")
        ),
        OperandEnumerant(
          "InputAttachmentArrayNonUniformIndexing",
          5310L,
          Vec(),
          Vec("InputAttachmentArrayNonUniformIndexingEXT")
        ),
        OperandEnumerant(
          "UniformTexelBufferArrayNonUniformIndexing",
          5311L,
          Vec(),
          Vec("UniformTexelBufferArrayNonUniformIndexingEXT")
        ),
        OperandEnumerant(
          "StorageTexelBufferArrayNonUniformIndexing",
          5312L,
          Vec(),
          Vec("StorageTexelBufferArrayNonUniformIndexingEXT")
        ),
        OperandEnumerant("RayTracingPositionFetchKHR", 5336L, Vec(), Vec()),
        OperandEnumerant("RayTracingNV", 5340L, Vec(), Vec()),
        OperandEnumerant("RayTracingMotionBlurNV", 5341L, Vec(), Vec()),
        OperandEnumerant("VulkanMemoryModel", 5345L, Vec(), Vec("VulkanMemoryModelKHR")),
        OperandEnumerant("VulkanMemoryModelDeviceScope", 5346L, Vec(), Vec("VulkanMemoryModelDeviceScopeKHR")),
        OperandEnumerant("PhysicalStorageBufferAddresses", 5347L, Vec(), Vec("PhysicalStorageBufferAddressesEXT")),
        OperandEnumerant("ComputeDerivativeGroupLinearKHR", 5350L, Vec(), Vec("ComputeDerivativeGroupLinearNV")),
        OperandEnumerant("RayTracingProvisionalKHR", 5353L, Vec(), Vec()),
        OperandEnumerant("CooperativeMatrixNV", 5357L, Vec(), Vec()),
        OperandEnumerant("FragmentShaderSampleInterlockEXT", 5363L, Vec(), Vec()),
        OperandEnumerant("FragmentShaderShadingRateInterlockEXT", 5372L, Vec(), Vec()),
        OperandEnumerant("ShaderSMBuiltinsNV", 5373L, Vec(), Vec()),
        OperandEnumerant("FragmentShaderPixelInterlockEXT", 5378L, Vec(), Vec()),
        OperandEnumerant("DemoteToHelperInvocation", 5379L, Vec(), Vec("DemoteToHelperInvocationEXT")),
        OperandEnumerant("DisplacementMicromapNV", 5380L, Vec(), Vec()),
        OperandEnumerant("RayTracingOpacityMicromapKHR", 5381L, Vec(), Vec("RayTracingOpacityMicromapEXT")),
        OperandEnumerant("ShaderInvocationReorderNV", 5383L, Vec(), Vec()),
        OperandEnumerant("ShaderInvocationReorderEXT", 5388L, Vec(), Vec()),
        OperandEnumerant("BindlessTextureNV", 5390L, Vec(), Vec()),
        OperandEnumerant("RayQueryPositionFetchKHR", 5391L, Vec(), Vec()),
        OperandEnumerant("CooperativeVectorNV", 5394L, Vec(), Vec()),
        OperandEnumerant("AtomicFloat16VectorNV", 5404L, Vec(), Vec()),
        OperandEnumerant("RayTracingDisplacementMicromapNV", 5409L, Vec(), Vec()),
        OperandEnumerant("RawAccessChainsNV", 5414L, Vec(), Vec()),
        OperandEnumerant("RayTracingSpheresGeometryNV", 5418L, Vec(), Vec()),
        OperandEnumerant("RayTracingLinearSweptSpheresGeometryNV", 5419L, Vec(), Vec()),
        OperandEnumerant("PushConstantBanksNV", 5423L, Vec(), Vec()),
        OperandEnumerant("LongVectorEXT", 5425L, Vec(), Vec()),
        OperandEnumerant("Shader64BitIndexingEXT", 5426L, Vec(), Vec()),
        OperandEnumerant("CooperativeMatrixReductionsNV", 5430L, Vec(), Vec()),
        OperandEnumerant("CooperativeMatrixConversionsNV", 5431L, Vec(), Vec()),
        OperandEnumerant("CooperativeMatrixPerElementOperationsNV", 5432L, Vec(), Vec()),
        OperandEnumerant("CooperativeMatrixTensorAddressingNV", 5433L, Vec(), Vec()),
        OperandEnumerant("CooperativeMatrixBlockLoadsNV", 5434L, Vec(), Vec()),
        OperandEnumerant("CooperativeVectorTrainingNV", 5435L, Vec(), Vec()),
        OperandEnumerant("RayTracingClusterAccelerationStructureNV", 5437L, Vec(), Vec()),
        OperandEnumerant("TensorAddressingNV", 5439L, Vec(), Vec()),
        OperandEnumerant("CooperativeMatrixDecodeVectorNV", 5447L, Vec(), Vec()),
        OperandEnumerant("SubgroupShuffleINTEL", 5568L, Vec(), Vec()),
        OperandEnumerant("SubgroupBufferBlockIOINTEL", 5569L, Vec(), Vec()),
        OperandEnumerant("SubgroupImageBlockIOINTEL", 5570L, Vec(), Vec()),
        OperandEnumerant("SubgroupImageMediaBlockIOINTEL", 5579L, Vec(), Vec()),
        OperandEnumerant("RoundToInfinityINTEL", 5582L, Vec(), Vec()),
        OperandEnumerant("FloatingPointModeINTEL", 5583L, Vec(), Vec()),
        OperandEnumerant("IntegerFunctions2INTEL", 5584L, Vec(), Vec()),
        OperandEnumerant("FunctionPointersINTEL", 5603L, Vec(), Vec()),
        OperandEnumerant("IndirectReferencesINTEL", 5604L, Vec(), Vec()),
        OperandEnumerant("AsmINTEL", 5606L, Vec(), Vec()),
        OperandEnumerant("AtomicFloat32MinMaxEXT", 5612L, Vec(), Vec()),
        OperandEnumerant("AtomicFloat64MinMaxEXT", 5613L, Vec(), Vec()),
        OperandEnumerant("AtomicFloat16MinMaxEXT", 5616L, Vec(), Vec()),
        OperandEnumerant("VectorComputeINTEL", 5617L, Vec(), Vec()),
        OperandEnumerant("VectorAnyINTEL", 5619L, Vec(), Vec()),
        OperandEnumerant("ExpectAssumeKHR", 5629L, Vec(), Vec()),
        OperandEnumerant("SubgroupAvcMotionEstimationINTEL", 5696L, Vec(), Vec()),
        OperandEnumerant("SubgroupAvcMotionEstimationIntraINTEL", 5697L, Vec(), Vec()),
        OperandEnumerant("SubgroupAvcMotionEstimationChromaINTEL", 5698L, Vec(), Vec()),
        OperandEnumerant("VariableLengthArrayINTEL", 5817L, Vec(), Vec()),
        OperandEnumerant("FunctionFloatControlINTEL", 5821L, Vec(), Vec()),
        OperandEnumerant("FPGAMemoryAttributesALTERA", 5824L, Vec(), Vec("FPGAMemoryAttributesINTEL")),
        OperandEnumerant("FPFastMathModeINTEL", 5837L, Vec(), Vec()),
        OperandEnumerant("ArbitraryPrecisionIntegersALTERA", 5844L, Vec(), Vec("ArbitraryPrecisionIntegersINTEL")),
        OperandEnumerant(
          "ArbitraryPrecisionFloatingPointALTERA",
          5845L,
          Vec(),
          Vec("ArbitraryPrecisionFloatingPointINTEL")
        ),
        OperandEnumerant("UnstructuredLoopControlsINTEL", 5886L, Vec(), Vec()),
        OperandEnumerant("FPGALoopControlsALTERA", 5888L, Vec(), Vec("FPGALoopControlsINTEL")),
        OperandEnumerant("KernelAttributesINTEL", 5892L, Vec(), Vec()),
        OperandEnumerant("FPGAKernelAttributesINTEL", 5897L, Vec(), Vec()),
        OperandEnumerant("FPGAMemoryAccessesALTERA", 5898L, Vec(), Vec("FPGAMemoryAccessesINTEL")),
        OperandEnumerant("FPGAClusterAttributesALTERA", 5904L, Vec(), Vec("FPGAClusterAttributesINTEL")),
        OperandEnumerant("LoopFuseALTERA", 5906L, Vec(), Vec("LoopFuseINTEL")),
        OperandEnumerant("FPGADSPControlALTERA", 5908L, Vec(), Vec("FPGADSPControlINTEL")),
        OperandEnumerant("MemoryAccessAliasingINTEL", 5910L, Vec(), Vec()),
        OperandEnumerant(
          "FPGAInvocationPipeliningAttributesALTERA",
          5916L,
          Vec(),
          Vec("FPGAInvocationPipeliningAttributesINTEL")
        ),
        OperandEnumerant("FPGABufferLocationALTERA", 5920L, Vec(), Vec("FPGABufferLocationINTEL")),
        OperandEnumerant("ArbitraryPrecisionFixedPointALTERA", 5922L, Vec(), Vec("ArbitraryPrecisionFixedPointINTEL")),
        OperandEnumerant("USMStorageClassesALTERA", 5935L, Vec(), Vec("USMStorageClassesINTEL")),
        OperandEnumerant("RuntimeAlignedAttributeALTERA", 5939L, Vec(), Vec("RuntimeAlignedAttributeINTEL")),
        OperandEnumerant("IOPipesALTERA", 5943L, Vec(), Vec("IOPipesINTEL")),
        OperandEnumerant("BlockingPipesALTERA", 5945L, Vec(), Vec("BlockingPipesINTEL")),
        OperandEnumerant("FPGARegALTERA", 5948L, Vec(), Vec("FPGARegINTEL")),
        OperandEnumerant("DotProductInputAll", 6016L, Vec(), Vec("DotProductInputAllKHR")),
        OperandEnumerant("DotProductInput4x8Bit", 6017L, Vec(), Vec("DotProductInput4x8BitKHR")),
        OperandEnumerant("DotProductInput4x8BitPacked", 6018L, Vec(), Vec("DotProductInput4x8BitPackedKHR")),
        OperandEnumerant("DotProduct", 6019L, Vec(), Vec("DotProductKHR")),
        OperandEnumerant("RayCullMaskKHR", 6020L, Vec(), Vec()),
        OperandEnumerant("CooperativeMatrixKHR", 6022L, Vec(), Vec()),
        OperandEnumerant("ReplicatedCompositesEXT", 6024L, Vec(), Vec()),
        OperandEnumerant("BitInstructions", 6025L, Vec(), Vec()),
        OperandEnumerant("GroupNonUniformRotateKHR", 6026L, Vec(), Vec()),
        OperandEnumerant("FloatControls2", 6029L, Vec(), Vec()),
        OperandEnumerant("FMAKHR", 6030L, Vec(), Vec()),
        OperandEnumerant("RayTracingOpacityMicromapExecutionModeKHR", 6032L, Vec(), Vec()),
        OperandEnumerant("AtomicFloat32AddEXT", 6033L, Vec(), Vec()),
        OperandEnumerant("AtomicFloat64AddEXT", 6034L, Vec(), Vec()),
        OperandEnumerant("LongCompositesINTEL", 6089L, Vec(), Vec()),
        OperandEnumerant("OptNoneEXT", 6094L, Vec(), Vec("OptNoneINTEL")),
        OperandEnumerant("AtomicFloat16AddEXT", 6095L, Vec(), Vec()),
        OperandEnumerant("DebugInfoModuleINTEL", 6114L, Vec(), Vec()),
        OperandEnumerant("BFloat16ConversionINTEL", 6115L, Vec(), Vec()),
        OperandEnumerant("SplitBarrierEXT", 6141L, Vec(), Vec("SplitBarrierINTEL")),
        OperandEnumerant("ArithmeticFenceEXT", 6144L, Vec(), Vec()),
        OperandEnumerant("FPGAClusterAttributesV2ALTERA", 6150L, Vec(), Vec("FPGAClusterAttributesV2INTEL")),
        OperandEnumerant("FPGAKernelAttributesv2INTEL", 6161L, Vec(), Vec()),
        OperandEnumerant("TaskSequenceALTERA", 6162L, Vec(), Vec("TaskSequenceINTEL")),
        OperandEnumerant("FPMaxErrorINTEL", 6169L, Vec(), Vec()),
        OperandEnumerant("FPGALatencyControlALTERA", 6171L, Vec(), Vec("FPGALatencyControlINTEL")),
        OperandEnumerant("FPGAArgumentInterfacesALTERA", 6174L, Vec(), Vec("FPGAArgumentInterfacesINTEL")),
        OperandEnumerant("GlobalVariableHostAccessINTEL", 6187L, Vec(), Vec()),
        OperandEnumerant(
          "GlobalVariableFPGADecorationsALTERA",
          6189L,
          Vec(),
          Vec("GlobalVariableFPGADecorationsINTEL")
        ),
        OperandEnumerant("SubgroupBufferPrefetchINTEL", 6220L, Vec(), Vec()),
        OperandEnumerant("Subgroup2DBlockIOINTEL", 6228L, Vec(), Vec()),
        OperandEnumerant("Subgroup2DBlockTransformINTEL", 6229L, Vec(), Vec()),
        OperandEnumerant("Subgroup2DBlockTransposeINTEL", 6230L, Vec(), Vec()),
        OperandEnumerant("SubgroupMatrixMultiplyAccumulateINTEL", 6236L, Vec(), Vec()),
        OperandEnumerant("TernaryBitwiseFunctionINTEL", 6241L, Vec(), Vec()),
        OperandEnumerant("UntypedVariableLengthArrayINTEL", 6243L, Vec(), Vec()),
        OperandEnumerant("SpecConditionalINTEL", 6245L, Vec(), Vec()),
        OperandEnumerant("FunctionVariantsINTEL", 6246L, Vec(), Vec()),
        OperandEnumerant("PredicatedIOINTEL", 6257L, Vec(), Vec()),
        OperandEnumerant("RoundedDivideSqrtINTEL", 6265L, Vec(), Vec()),
        OperandEnumerant("GroupUniformArithmeticKHR", 6400L, Vec(), Vec()),
        OperandEnumerant("TensorFloat32RoundingINTEL", 6425L, Vec(), Vec()),
        OperandEnumerant("MaskedGatherScatterINTEL", 6427L, Vec(), Vec()),
        OperandEnumerant("CacheControlsINTEL", 6441L, Vec(), Vec()),
        OperandEnumerant("RegisterLimitsINTEL", 6460L, Vec(), Vec()),
        OperandEnumerant("BindlessImagesINTEL", 6528L, Vec(), Vec()),
        OperandEnumerant("DotProductFloat16AccFloat32VALVE", 6912L, Vec(), Vec()),
        OperandEnumerant("DotProductFloat16AccFloat16VALVE", 6913L, Vec(), Vec()),
        OperandEnumerant("DotProductBFloat16AccVALVE", 6914L, Vec(), Vec()),
        OperandEnumerant("DotProductFloat8AccFloat32VALVE", 6915L, Vec(), Vec()),
        OperandEnumerant("IntrinsicSAMSUNG", 7041L, Vec(), Vec())
      )
    ),
    OperandKindInfo(
      OperandKind.RayQueryIntersection,
      Vec(
        OperandEnumerant("RayQueryCandidateIntersectionKHR", 0L, Vec(), Vec()),
        OperandEnumerant("RayQueryCommittedIntersectionKHR", 1L, Vec(), Vec())
      )
    ),
    OperandKindInfo(
      OperandKind.RayQueryCommittedIntersectionType,
      Vec(
        OperandEnumerant("RayQueryCommittedIntersectionNoneKHR", 0L, Vec(), Vec()),
        OperandEnumerant("RayQueryCommittedIntersectionTriangleKHR", 1L, Vec(), Vec()),
        OperandEnumerant("RayQueryCommittedIntersectionGeneratedKHR", 2L, Vec(), Vec())
      )
    )
  )

  private def operandKinds5: Vec[OperandKindInfo] = Vec(
    OperandKindInfo(
      OperandKind.RayQueryCandidateIntersectionType,
      Vec(
        OperandEnumerant("RayQueryCandidateIntersectionTriangleKHR", 0L, Vec(), Vec()),
        OperandEnumerant("RayQueryCandidateIntersectionAABBKHR", 1L, Vec(), Vec())
      )
    ),
    OperandKindInfo(
      OperandKind.PackedVectorFormat,
      Vec(OperandEnumerant("PackedVectorFormat4x8Bit", 0L, Vec(), Vec("PackedVectorFormat4x8BitKHR")))
    ),
    OperandKindInfo(
      OperandKind.CooperativeMatrixOperands,
      Vec(
        OperandEnumerant("NoneKHR", 0L, Vec(), Vec()),
        OperandEnumerant("MatrixASignedComponentsKHR", 1L, Vec(), Vec()),
        OperandEnumerant("MatrixBSignedComponentsKHR", 2L, Vec(), Vec()),
        OperandEnumerant("MatrixCSignedComponentsKHR", 4L, Vec(), Vec()),
        OperandEnumerant("MatrixResultSignedComponentsKHR", 8L, Vec(), Vec()),
        OperandEnumerant("SaturatingAccumulationKHR", 16L, Vec(), Vec())
      )
    ),
    OperandKindInfo(
      OperandKind.CooperativeMatrixLayout,
      Vec(
        OperandEnumerant("RowMajorKHR", 0L, Vec(), Vec()),
        OperandEnumerant("ColumnMajorKHR", 1L, Vec(), Vec()),
        OperandEnumerant("RowBlockedInterleavedARM", 4202L, Vec(), Vec()),
        OperandEnumerant("ColumnBlockedInterleavedARM", 4203L, Vec(), Vec())
      )
    ),
    OperandKindInfo(
      OperandKind.CooperativeMatrixUse,
      Vec(
        OperandEnumerant("MatrixAKHR", 0L, Vec(), Vec()),
        OperandEnumerant("MatrixBKHR", 1L, Vec(), Vec()),
        OperandEnumerant("MatrixAccumulatorKHR", 2L, Vec(), Vec())
      )
    ),
    OperandKindInfo(
      OperandKind.CooperativeMatrixReduce,
      Vec(
        OperandEnumerant("Row", 1L, Vec(), Vec()),
        OperandEnumerant("Column", 2L, Vec(), Vec()),
        OperandEnumerant("2x2", 4L, Vec(), Vec())
      )
    ),
    OperandKindInfo(
      OperandKind.TensorClampMode,
      Vec(
        OperandEnumerant("Undefined", 0L, Vec(), Vec()),
        OperandEnumerant("Constant", 1L, Vec(), Vec()),
        OperandEnumerant("ClampToEdge", 2L, Vec(), Vec()),
        OperandEnumerant("Repeat", 3L, Vec(), Vec()),
        OperandEnumerant("RepeatMirrored", 4L, Vec(), Vec())
      )
    ),
    OperandKindInfo(
      OperandKind.TensorAddressingOperands,
      Vec(
        OperandEnumerant("None", 0L, Vec(), Vec()),
        OperandEnumerant("TensorView", 1L, Vec(OperandKind.IdRef), Vec()),
        OperandEnumerant("DecodeFunc", 2L, Vec(OperandKind.IdRef), Vec()),
        OperandEnumerant("DecodeVectorFunc", 4L, Vec(OperandKind.IdRef), Vec())
      )
    )
  )

  private def operandKinds6: Vec[OperandKindInfo] = Vec(
    OperandKindInfo(
      OperandKind.InitializationModeQualifier,
      Vec(
        OperandEnumerant("InitOnDeviceReprogramALTERA", 0L, Vec(), Vec("InitOnDeviceReprogramINTEL")),
        OperandEnumerant("InitOnDeviceResetALTERA", 1L, Vec(), Vec("InitOnDeviceResetINTEL"))
      )
    ),
    OperandKindInfo(
      OperandKind.LoadCacheControl,
      Vec(
        OperandEnumerant("UncachedINTEL", 0L, Vec(), Vec()),
        OperandEnumerant("CachedINTEL", 1L, Vec(), Vec()),
        OperandEnumerant("StreamingINTEL", 2L, Vec(), Vec()),
        OperandEnumerant("InvalidateAfterReadINTEL", 3L, Vec(), Vec()),
        OperandEnumerant("ConstCachedINTEL", 4L, Vec(), Vec())
      )
    ),
    OperandKindInfo(
      OperandKind.StoreCacheControl,
      Vec(
        OperandEnumerant("UncachedINTEL", 0L, Vec(), Vec()),
        OperandEnumerant("WriteThroughINTEL", 1L, Vec(), Vec()),
        OperandEnumerant("WriteBackINTEL", 2L, Vec(), Vec()),
        OperandEnumerant("StreamingINTEL", 3L, Vec(), Vec())
      )
    ),
    OperandKindInfo(OperandKind.NamedMaximumNumberOfRegisters, Vec(OperandEnumerant("AutoINTEL", 0L, Vec(), Vec()))),
    OperandKindInfo(
      OperandKind.MatrixMultiplyAccumulateOperands,
      Vec(
        OperandEnumerant("None", 0L, Vec(), Vec()),
        OperandEnumerant("MatrixASignedComponentsINTEL", 1L, Vec(), Vec()),
        OperandEnumerant("MatrixBSignedComponentsINTEL", 2L, Vec(), Vec()),
        OperandEnumerant("MatrixCBFloat16INTEL", 4L, Vec(), Vec()),
        OperandEnumerant("MatrixResultBFloat16INTEL", 8L, Vec(), Vec()),
        OperandEnumerant("MatrixAPackedInt8INTEL", 16L, Vec(), Vec()),
        OperandEnumerant("MatrixBPackedInt8INTEL", 32L, Vec(), Vec()),
        OperandEnumerant("MatrixAPackedInt4INTEL", 64L, Vec(), Vec()),
        OperandEnumerant("MatrixBPackedInt4INTEL", 128L, Vec(), Vec()),
        OperandEnumerant("MatrixATF32INTEL", 256L, Vec(), Vec()),
        OperandEnumerant("MatrixBTF32INTEL", 512L, Vec(), Vec()),
        OperandEnumerant("MatrixAPackedFloat16INTEL", 1024L, Vec(), Vec()),
        OperandEnumerant("MatrixBPackedFloat16INTEL", 2048L, Vec(), Vec()),
        OperandEnumerant("MatrixAPackedBFloat16INTEL", 4096L, Vec(), Vec()),
        OperandEnumerant("MatrixBPackedBFloat16INTEL", 8192L, Vec(), Vec())
      )
    ),
    OperandKindInfo(
      OperandKind.FPEncoding,
      Vec(
        OperandEnumerant("BFloat16KHR", 0L, Vec(), Vec()),
        OperandEnumerant("Float8E4M3EXT", 4214L, Vec(), Vec()),
        OperandEnumerant("Float8E5M2EXT", 4215L, Vec(), Vec()),
        OperandEnumerant("Float6E2M3EXT", 4223L, Vec(), Vec()),
        OperandEnumerant("Float6E3M2EXT", 4224L, Vec(), Vec()),
        OperandEnumerant("Float4E2M1EXT", 4225L, Vec(), Vec()),
        OperandEnumerant("Float8UnsignedE8M0EXT", 4226L, Vec(), Vec()),
        OperandEnumerant("MXInt8EXT", 4227L, Vec(), Vec())
      )
    ),
    OperandKindInfo(
      OperandKind.CooperativeVectorMatrixLayout,
      Vec(
        OperandEnumerant("RowMajorNV", 0L, Vec(), Vec()),
        OperandEnumerant("ColumnMajorNV", 1L, Vec(), Vec()),
        OperandEnumerant("InferencingOptimalNV", 2L, Vec(), Vec()),
        OperandEnumerant("TrainingOptimalNV", 3L, Vec(), Vec())
      )
    ),
    OperandKindInfo(
      OperandKind.ComponentType,
      Vec(
        OperandEnumerant("Float16NV", 0L, Vec(), Vec()),
        OperandEnumerant("Float32NV", 1L, Vec(), Vec()),
        OperandEnumerant("Float64NV", 2L, Vec(), Vec()),
        OperandEnumerant("SignedInt8NV", 3L, Vec(), Vec()),
        OperandEnumerant("SignedInt16NV", 4L, Vec(), Vec()),
        OperandEnumerant("SignedInt32NV", 5L, Vec(), Vec()),
        OperandEnumerant("SignedInt64NV", 6L, Vec(), Vec()),
        OperandEnumerant("UnsignedInt8NV", 7L, Vec(), Vec()),
        OperandEnumerant("UnsignedInt16NV", 8L, Vec(), Vec()),
        OperandEnumerant("UnsignedInt32NV", 9L, Vec(), Vec()),
        OperandEnumerant("UnsignedInt64NV", 10L, Vec(), Vec()),
        OperandEnumerant("SignedInt8PackedNV", 1000491000L, Vec(), Vec()),
        OperandEnumerant("UnsignedInt8PackedNV", 1000491001L, Vec(), Vec()),
        OperandEnumerant("FloatE4M3NV", 1000491002L, Vec(), Vec()),
        OperandEnumerant("FloatE5M2NV", 1000491003L, Vec(), Vec())
      )
    )
  )

  private def operandKinds7: Vec[OperandKindInfo] = Vec(
    OperandKindInfo(
      OperandKind.GatherModes,
      Vec(
        OperandEnumerant("Gather4x1QCOM", 0L, Vec(), Vec()),
        OperandEnumerant("GatherDQCOM", 1L, Vec(), Vec()),
        OperandEnumerant("GatherH2QCOM", 2L, Vec(), Vec()),
        OperandEnumerant("GatherV2QCOM", 3L, Vec(), Vec())
      )
    ),
    OperandKindInfo(
      OperandKind.TensorOperands,
      Vec(
        OperandEnumerant("NoneARM", 0L, Vec(), Vec()),
        OperandEnumerant("NontemporalARM", 1L, Vec(), Vec()),
        OperandEnumerant("OutOfBoundsValueARM", 2L, Vec(OperandKind.IdRef), Vec()),
        OperandEnumerant("MakeElementAvailableARM", 4L, Vec(OperandKind.IdRef), Vec()),
        OperandEnumerant("MakeElementVisibleARM", 8L, Vec(OperandKind.IdRef), Vec()),
        OperandEnumerant("NonPrivateElementARM", 16L, Vec(), Vec())
      )
    )
  )

  val operandKinds: Map[OperandKind, OperandKindInfo] =
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
      case definitions =>
        val matches = definitions.flatMap(definition =>
          definition.decoder(raw).toOption.map(instruction => definition -> instruction)
        )
        matches match {
          case Vec((_, instruction)) => Right(instruction)
          case Vec() =>
            Left(InstructionDecodeError.NoMatchingDefinition(
              raw.opcodeNumber,
              Vec.nonEmpty(definitions.head.opcode, definitions.tail.map(_.opcode)*)
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

  def minimumOperandWords(definition: Definition): Vec[Word] =
    OperandDecoder.minimumWords(definition.meta.operands, operandKinds)

  private def decodeDefinition(
      definition: Definition,
      raw: RawInstruction,
      construct: DecodedOperands => Instruction
  ): Either[InstructionDecodeError, Instruction] =
    if (raw.opcodeNumber != definition.opcodeNumber)
      Left(InstructionDecodeError.DecoderOpcodeMismatch(definition.opcodeNumber, raw.opcodeNumber))
    else {
      val decoded = OperandDecoder.decode(definition.opcode, raw.operandWords, definition.meta.operands, operandKinds)
      // The grammar uses 32-bit switch literals; retain 64-bit cases until the selector type is resolved.
      val operands = if (definition == Definition.Switch)
        decoded.orElse(OperandDecoder.decodeSwitch64(definition.opcode, raw.operandWords).orElse(decoded))
      else decoded
      operands.map(construct)
    }
}

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

  val operands: Vec[Operand] = operandValues.toVec
  final def opcode: NonEmpty[java.lang.String] = definition.opcode
  final def opcodeNumber: SPIRVOpcodeNumber = definition.opcodeNumber
  final def decoder: Instruction.Decoder = definition.decoder
  final def meta: Metadata = definition.meta
}
