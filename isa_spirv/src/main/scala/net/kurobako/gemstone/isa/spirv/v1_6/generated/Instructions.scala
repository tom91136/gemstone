// Generated from the pinned Khronos SPIR-V core grammar. Do not edit by hand.
package net.kurobako.gemstone.isa.spirv.v1_6

sealed trait KnownInstruction extends Product with Serializable {
  def arguments: InstructionArguments
  def opcode: Opcode
  def name: String
  final def syntax: Instruction = arguments.toSyntax(opcode)
}

enum InstructionDecodeError {
  case UnknownOpcode(opcode: String)
}

object KnownInstruction {
  private val ChunkCount: Int = 28

  def fromSyntax(value: Instruction): Either[InstructionDecodeError, KnownInstruction] = {
    val name = value.opcode.number.toString
    val arguments = InstructionArguments(value.operands)
    decode(name, arguments).toRight(InstructionDecodeError.UnknownOpcode(name))
  }

  private def decode(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    Math.floorMod(name.hashCode, ChunkCount) match {
      case 0 => decode0(name, arguments)
      case 1 => decode1(name, arguments)
      case 2 => decode2(name, arguments)
      case 3 => decode3(name, arguments)
      case 4 => decode4(name, arguments)
      case 5 => decode5(name, arguments)
      case 6 => decode6(name, arguments)
      case 7 => decode7(name, arguments)
      case 8 => decode8(name, arguments)
      case 9 => decode9(name, arguments)
      case 10 => decode10(name, arguments)
      case 11 => decode11(name, arguments)
      case 12 => decode12(name, arguments)
      case 13 => decode13(name, arguments)
      case 14 => decode14(name, arguments)
      case 15 => decode15(name, arguments)
      case 16 => decode16(name, arguments)
      case 17 => decode17(name, arguments)
      case 18 => decode18(name, arguments)
      case 19 => decode19(name, arguments)
      case 20 => decode20(name, arguments)
      case 21 => decode21(name, arguments)
      case 22 => decode22(name, arguments)
      case 23 => decode23(name, arguments)
      case 24 => decode24(name, arguments)
      case 25 => decode25(name, arguments)
      case 26 => decode26(name, arguments)
      case 27 => decode27(name, arguments)
    }

  final case class OpAbortKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5121)
    override val name: String = "OpAbortKHR"
  }

  final case class OpAbsISubINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5587)
    override val name: String = "OpAbsISubINTEL"
  }

  final case class OpAbsUSubINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5588)
    override val name: String = "OpAbsUSubINTEL"
  }

  final case class OpAccessChain(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(65)
    override val name: String = "OpAccessChain"
  }

  final case class OpAliasDomainDeclINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5911)
    override val name: String = "OpAliasDomainDeclINTEL"
  }

  final case class OpAliasScopeDeclINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5912)
    override val name: String = "OpAliasScopeDeclINTEL"
  }

  final case class OpAliasScopeListDeclINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5913)
    override val name: String = "OpAliasScopeListDeclINTEL"
  }

  final case class OpAll(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(155)
    override val name: String = "OpAll"
  }

  final case class OpAllocateNodePayloadsAMDX(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5074)
    override val name: String = "OpAllocateNodePayloadsAMDX"
  }

  final case class OpAny(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(154)
    override val name: String = "OpAny"
  }

  final case class OpArbitraryFloatACosINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5875)
    override val name: String = "OpArbitraryFloatACosINTEL"
  }

  final case class OpArbitraryFloatACosPiINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5876)
    override val name: String = "OpArbitraryFloatACosPiINTEL"
  }

  final case class OpArbitraryFloatASinINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5873)
    override val name: String = "OpArbitraryFloatASinINTEL"
  }

  final case class OpArbitraryFloatASinPiINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5874)
    override val name: String = "OpArbitraryFloatASinPiINTEL"
  }

  final case class OpArbitraryFloatATan2INTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5879)
    override val name: String = "OpArbitraryFloatATan2INTEL"
  }

  final case class OpArbitraryFloatATanINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5877)
    override val name: String = "OpArbitraryFloatATanINTEL"
  }

  final case class OpArbitraryFloatATanPiINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5878)
    override val name: String = "OpArbitraryFloatATanPiINTEL"
  }

  final case class OpArbitraryFloatAddALTERA(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5846)
    override val name: String = "OpArbitraryFloatAddALTERA"
  }

  final case class OpArbitraryFloatCastALTERA(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5841)
    override val name: String = "OpArbitraryFloatCastALTERA"
  }

  final case class OpArbitraryFloatCastFromIntALTERA(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5842)
    override val name: String = "OpArbitraryFloatCastFromIntALTERA"
  }

  final case class OpArbitraryFloatCastToIntALTERA(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5843)
    override val name: String = "OpArbitraryFloatCastToIntALTERA"
  }

  final case class OpArbitraryFloatCbrtALTERA(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5857)
    override val name: String = "OpArbitraryFloatCbrtALTERA"
  }

  final case class OpArbitraryFloatCosINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5869)
    override val name: String = "OpArbitraryFloatCosINTEL"
  }

  final case class OpArbitraryFloatCosPiINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5872)
    override val name: String = "OpArbitraryFloatCosPiINTEL"
  }

  final case class OpArbitraryFloatDivALTERA(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5849)
    override val name: String = "OpArbitraryFloatDivALTERA"
  }

  final case class OpArbitraryFloatEQALTERA(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5854)
    override val name: String = "OpArbitraryFloatEQALTERA"
  }

  final case class OpArbitraryFloatExp10INTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5866)
    override val name: String = "OpArbitraryFloatExp10INTEL"
  }

  final case class OpArbitraryFloatExp2INTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5865)
    override val name: String = "OpArbitraryFloatExp2INTEL"
  }

  final case class OpArbitraryFloatExpINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5864)
    override val name: String = "OpArbitraryFloatExpINTEL"
  }

  final case class OpArbitraryFloatExpm1INTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5867)
    override val name: String = "OpArbitraryFloatExpm1INTEL"
  }

  final case class OpArbitraryFloatGEALTERA(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5851)
    override val name: String = "OpArbitraryFloatGEALTERA"
  }

  final case class OpArbitraryFloatGTALTERA(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5850)
    override val name: String = "OpArbitraryFloatGTALTERA"
  }

  final case class OpArbitraryFloatHypotALTERA(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5858)
    override val name: String = "OpArbitraryFloatHypotALTERA"
  }

  final case class OpArbitraryFloatLEALTERA(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5853)
    override val name: String = "OpArbitraryFloatLEALTERA"
  }

  final case class OpArbitraryFloatLTALTERA(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5852)
    override val name: String = "OpArbitraryFloatLTALTERA"
  }

  final case class OpArbitraryFloatLog10INTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5862)
    override val name: String = "OpArbitraryFloatLog10INTEL"
  }

  final case class OpArbitraryFloatLog1pINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5863)
    override val name: String = "OpArbitraryFloatLog1pINTEL"
  }

  final case class OpArbitraryFloatLog2INTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5861)
    override val name: String = "OpArbitraryFloatLog2INTEL"
  }

  final case class OpArbitraryFloatLogINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5860)
    override val name: String = "OpArbitraryFloatLogINTEL"
  }

  final case class OpArbitraryFloatMulALTERA(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5848)
    override val name: String = "OpArbitraryFloatMulALTERA"
  }

  final case class OpArbitraryFloatPowINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5880)
    override val name: String = "OpArbitraryFloatPowINTEL"
  }

  final case class OpArbitraryFloatPowNINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5882)
    override val name: String = "OpArbitraryFloatPowNINTEL"
  }

  final case class OpArbitraryFloatPowRINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5881)
    override val name: String = "OpArbitraryFloatPowRINTEL"
  }

  final case class OpArbitraryFloatRSqrtALTERA(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5856)
    override val name: String = "OpArbitraryFloatRSqrtALTERA"
  }

  final case class OpArbitraryFloatRecipALTERA(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5855)
    override val name: String = "OpArbitraryFloatRecipALTERA"
  }

  final case class OpArbitraryFloatSinCosINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5870)
    override val name: String = "OpArbitraryFloatSinCosINTEL"
  }

  final case class OpArbitraryFloatSinCosPiALTERA(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5840)
    override val name: String = "OpArbitraryFloatSinCosPiALTERA"
  }

  final case class OpArbitraryFloatSinINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5868)
    override val name: String = "OpArbitraryFloatSinINTEL"
  }

  final case class OpArbitraryFloatSinPiINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5871)
    override val name: String = "OpArbitraryFloatSinPiINTEL"
  }

  final case class OpArbitraryFloatSqrtALTERA(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5859)
    override val name: String = "OpArbitraryFloatSqrtALTERA"
  }

  final case class OpArbitraryFloatSubALTERA(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5847)
    override val name: String = "OpArbitraryFloatSubALTERA"
  }

  final case class OpArithmeticFenceEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6145)
    override val name: String = "OpArithmeticFenceEXT"
  }

  final case class OpArrayLength(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(68)
    override val name: String = "OpArrayLength"
  }

  final case class OpAsmCallINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5611)
    override val name: String = "OpAsmCallINTEL"
  }

  final case class OpAsmINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5610)
    override val name: String = "OpAsmINTEL"
  }

  final case class OpAsmTargetINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5609)
    override val name: String = "OpAsmTargetINTEL"
  }

  final case class OpAssumeTrueKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5630)
    override val name: String = "OpAssumeTrueKHR"
  }

  final case class OpAtomicAnd(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(240)
    override val name: String = "OpAtomicAnd"
  }

  final case class OpAtomicCompareExchange(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(230)
    override val name: String = "OpAtomicCompareExchange"
  }

  final case class OpAtomicCompareExchangeWeak(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(231)
    override val name: String = "OpAtomicCompareExchangeWeak"
  }

  final case class OpAtomicExchange(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(229)
    override val name: String = "OpAtomicExchange"
  }

  final case class OpAtomicFAddEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6035)
    override val name: String = "OpAtomicFAddEXT"
  }

  final case class OpAtomicFMaxEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5615)
    override val name: String = "OpAtomicFMaxEXT"
  }

  final case class OpAtomicFMinEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5614)
    override val name: String = "OpAtomicFMinEXT"
  }

  final case class OpAtomicFlagClear(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(319)
    override val name: String = "OpAtomicFlagClear"
  }

  final case class OpAtomicFlagTestAndSet(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(318)
    override val name: String = "OpAtomicFlagTestAndSet"
  }

  final case class OpAtomicIAdd(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(234)
    override val name: String = "OpAtomicIAdd"
  }

  final case class OpAtomicIDecrement(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(233)
    override val name: String = "OpAtomicIDecrement"
  }

  final case class OpAtomicIIncrement(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(232)
    override val name: String = "OpAtomicIIncrement"
  }

  final case class OpAtomicISub(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(235)
    override val name: String = "OpAtomicISub"
  }

  final case class OpAtomicLoad(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(227)
    override val name: String = "OpAtomicLoad"
  }

  final case class OpAtomicOr(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(241)
    override val name: String = "OpAtomicOr"
  }

  final case class OpAtomicSMax(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(238)
    override val name: String = "OpAtomicSMax"
  }

  final case class OpAtomicSMin(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(236)
    override val name: String = "OpAtomicSMin"
  }

  final case class OpAtomicStore(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(228)
    override val name: String = "OpAtomicStore"
  }

  final case class OpAtomicUMax(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(239)
    override val name: String = "OpAtomicUMax"
  }

  final case class OpAtomicUMin(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(237)
    override val name: String = "OpAtomicUMin"
  }

  final case class OpAtomicXor(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(242)
    override val name: String = "OpAtomicXor"
  }

  final case class OpBeginInvocationInterlockEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5364)
    override val name: String = "OpBeginInvocationInterlockEXT"
  }

  final case class OpBitCastArrayQCOM(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4497)
    override val name: String = "OpBitCastArrayQCOM"
  }

  final case class OpBitCount(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(205)
    override val name: String = "OpBitCount"
  }

  final case class OpBitFieldInsert(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(201)
    override val name: String = "OpBitFieldInsert"
  }

  final case class OpBitFieldSExtract(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(202)
    override val name: String = "OpBitFieldSExtract"
  }

  final case class OpBitFieldUExtract(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(203)
    override val name: String = "OpBitFieldUExtract"
  }

  final case class OpBitReverse(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(204)
    override val name: String = "OpBitReverse"
  }

  final case class OpBitcast(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(124)
    override val name: String = "OpBitcast"
  }

  final case class OpBitcastExtractEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4195)
    override val name: String = "OpBitcastExtractEXT"
  }

  final case class OpBitwiseAnd(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(199)
    override val name: String = "OpBitwiseAnd"
  }

  final case class OpBitwiseFunctionINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6242)
    override val name: String = "OpBitwiseFunctionINTEL"
  }

  final case class OpBitwiseOr(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(197)
    override val name: String = "OpBitwiseOr"
  }

  final case class OpBitwiseXor(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(198)
    override val name: String = "OpBitwiseXor"
  }

  final case class OpBranch(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(249)
    override val name: String = "OpBranch"
  }

  final case class OpBranchConditional(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(250)
    override val name: String = "OpBranchConditional"
  }

  final case class OpBufferPointerEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5119)
    override val name: String = "OpBufferPointerEXT"
  }

  final case class OpBuildNDRange(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(304)
    override val name: String = "OpBuildNDRange"
  }

  final case class OpCapability(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(17)
    override val name: String = "OpCapability"
  }

  final case class OpCaptureEventProfilingInfo(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(302)
    override val name: String = "OpCaptureEventProfilingInfo"
  }

  final case class OpColorAttachmentReadEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4160)
    override val name: String = "OpColorAttachmentReadEXT"
  }

  final case class OpCommitReadPipe(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(280)
    override val name: String = "OpCommitReadPipe"
  }

  final case class OpCommitWritePipe(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(281)
    override val name: String = "OpCommitWritePipe"
  }

  final case class OpCompositeConstruct(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(80)
    override val name: String = "OpCompositeConstruct"
  }

  final case class OpCompositeConstructContinuedINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6096)
    override val name: String = "OpCompositeConstructContinuedINTEL"
  }

  final case class OpCompositeConstructCoopMatQCOM(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4540)
    override val name: String = "OpCompositeConstructCoopMatQCOM"
  }

  final case class OpCompositeConstructReplicateEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4463)
    override val name: String = "OpCompositeConstructReplicateEXT"
  }

  final case class OpCompositeExtract(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(81)
    override val name: String = "OpCompositeExtract"
  }

  final case class OpCompositeExtractCoopMatQCOM(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4541)
    override val name: String = "OpCompositeExtractCoopMatQCOM"
  }

  final case class OpCompositeInsert(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(82)
    override val name: String = "OpCompositeInsert"
  }

  final case class OpConditionalCapabilityINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6250)
    override val name: String = "OpConditionalCapabilityINTEL"
  }

  final case class OpConditionalCopyObjectINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6254)
    override val name: String = "OpConditionalCopyObjectINTEL"
  }

  final case class OpConditionalEntryPointINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6249)
    override val name: String = "OpConditionalEntryPointINTEL"
  }

  final case class OpConditionalExtensionINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6248)
    override val name: String = "OpConditionalExtensionINTEL"
  }

  final case class OpConstant(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(43)
    override val name: String = "OpConstant"
  }

  final case class OpConstantComposite(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(44)
    override val name: String = "OpConstantComposite"
  }

  final case class OpConstantCompositeContinuedINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6091)
    override val name: String = "OpConstantCompositeContinuedINTEL"
  }

  final case class OpConstantCompositeReplicateEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4461)
    override val name: String = "OpConstantCompositeReplicateEXT"
  }

  final case class OpConstantDataKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5147)
    override val name: String = "OpConstantDataKHR"
  }

  final case class OpConstantFalse(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(42)
    override val name: String = "OpConstantFalse"
  }

  final case class OpConstantFunctionPointerINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5600)
    override val name: String = "OpConstantFunctionPointerINTEL"
  }

  final case class OpConstantNull(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(46)
    override val name: String = "OpConstantNull"
  }

  final case class OpConstantPipeStorage(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(323)
    override val name: String = "OpConstantPipeStorage"
  }

  final case class OpConstantSampler(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(45)
    override val name: String = "OpConstantSampler"
  }

  final case class OpConstantSizeOfEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5129)
    override val name: String = "OpConstantSizeOfEXT"
  }

  final case class OpConstantStringAMDX(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5103)
    override val name: String = "OpConstantStringAMDX"
  }

  final case class OpConstantTrue(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(41)
    override val name: String = "OpConstantTrue"
  }

  final case class OpControlBarrier(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(224)
    override val name: String = "OpControlBarrier"
  }

  final case class OpControlBarrierArriveEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6142)
    override val name: String = "OpControlBarrierArriveEXT"
  }

  final case class OpControlBarrierWaitEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6143)
    override val name: String = "OpControlBarrierWaitEXT"
  }

  final case class OpConvertBF16ToFINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6117)
    override val name: String = "OpConvertBF16ToFINTEL"
  }

  final case class OpConvertFToBF16INTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6116)
    override val name: String = "OpConvertFToBF16INTEL"
  }

  final case class OpConvertFToS(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(110)
    override val name: String = "OpConvertFToS"
  }

  final case class OpConvertFToU(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(109)
    override val name: String = "OpConvertFToU"
  }

  final case class OpConvertHandleToImageINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6529)
    override val name: String = "OpConvertHandleToImageINTEL"
  }

  final case class OpConvertHandleToSampledImageINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6531)
    override val name: String = "OpConvertHandleToSampledImageINTEL"
  }

  final case class OpConvertHandleToSamplerINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6530)
    override val name: String = "OpConvertHandleToSamplerINTEL"
  }

  final case class OpConvertImageToUNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5393)
    override val name: String = "OpConvertImageToUNV"
  }

  final case class OpConvertPtrToU(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(117)
    override val name: String = "OpConvertPtrToU"
  }

  final case class OpConvertSToF(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(111)
    override val name: String = "OpConvertSToF"
  }

  final case class OpConvertSampledImageToUNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5396)
    override val name: String = "OpConvertSampledImageToUNV"
  }

  final case class OpConvertSamplerToUNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5394)
    override val name: String = "OpConvertSamplerToUNV"
  }

  final case class OpConvertUToAccelerationStructureKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4447)
    override val name: String = "OpConvertUToAccelerationStructureKHR"
  }

  final case class OpConvertUToF(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(112)
    override val name: String = "OpConvertUToF"
  }

  final case class OpConvertUToImageNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5391)
    override val name: String = "OpConvertUToImageNV"
  }

  final case class OpConvertUToPtr(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(120)
    override val name: String = "OpConvertUToPtr"
  }

  final case class OpConvertUToSampledImageNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5395)
    override val name: String = "OpConvertUToSampledImageNV"
  }

  final case class OpConvertUToSamplerNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5392)
    override val name: String = "OpConvertUToSamplerNV"
  }

  final case class OpCooperativeMatrixConvertNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5293)
    override val name: String = "OpCooperativeMatrixConvertNV"
  }

  final case class OpCooperativeMatrixLengthKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4460)
    override val name: String = "OpCooperativeMatrixLengthKHR"
  }

  final case class OpCooperativeMatrixLengthNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5362)
    override val name: String = "OpCooperativeMatrixLengthNV"
  }

  final case class OpCooperativeMatrixLoadKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4457)
    override val name: String = "OpCooperativeMatrixLoadKHR"
  }

  final case class OpCooperativeMatrixLoadNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5359)
    override val name: String = "OpCooperativeMatrixLoadNV"
  }

  final case class OpCooperativeMatrixLoadTensorNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5367)
    override val name: String = "OpCooperativeMatrixLoadTensorNV"
  }

  final case class OpCooperativeMatrixMulAddKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4459)
    override val name: String = "OpCooperativeMatrixMulAddKHR"
  }

  final case class OpCooperativeMatrixMulAddNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5361)
    override val name: String = "OpCooperativeMatrixMulAddNV"
  }

  final case class OpCooperativeMatrixPerElementOpNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5369)
    override val name: String = "OpCooperativeMatrixPerElementOpNV"
  }

  final case class OpCooperativeMatrixReduceNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5366)
    override val name: String = "OpCooperativeMatrixReduceNV"
  }

  final case class OpCooperativeMatrixStoreKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4458)
    override val name: String = "OpCooperativeMatrixStoreKHR"
  }

  final case class OpCooperativeMatrixStoreNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5360)
    override val name: String = "OpCooperativeMatrixStoreNV"
  }

  final case class OpCooperativeMatrixStoreTensorNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5368)
    override val name: String = "OpCooperativeMatrixStoreTensorNV"
  }

  final case class OpCooperativeMatrixTransposeNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5390)
    override val name: String = "OpCooperativeMatrixTransposeNV"
  }

  final case class OpCooperativeVectorLoadNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5302)
    override val name: String = "OpCooperativeVectorLoadNV"
  }

  final case class OpCooperativeVectorMatrixMulAddNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5292)
    override val name: String = "OpCooperativeVectorMatrixMulAddNV"
  }

  final case class OpCooperativeVectorMatrixMulNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5289)
    override val name: String = "OpCooperativeVectorMatrixMulNV"
  }

  final case class OpCooperativeVectorOuterProductAccumulateNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5290)
    override val name: String = "OpCooperativeVectorOuterProductAccumulateNV"
  }

  final case class OpCooperativeVectorReduceSumAccumulateNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5291)
    override val name: String = "OpCooperativeVectorReduceSumAccumulateNV"
  }

  final case class OpCooperativeVectorStoreNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5303)
    override val name: String = "OpCooperativeVectorStoreNV"
  }

  final case class OpCopyLogical(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(400)
    override val name: String = "OpCopyLogical"
  }

  final case class OpCopyMemory(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(63)
    override val name: String = "OpCopyMemory"
  }

  final case class OpCopyMemorySized(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(64)
    override val name: String = "OpCopyMemorySized"
  }

  final case class OpCopyObject(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(83)
    override val name: String = "OpCopyObject"
  }

  final case class OpCreatePipeFromPipeStorage(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(324)
    override val name: String = "OpCreatePipeFromPipeStorage"
  }

  final case class OpCreateTensorLayoutNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5372)
    override val name: String = "OpCreateTensorLayoutNV"
  }

  final case class OpCreateTensorViewNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5377)
    override val name: String = "OpCreateTensorViewNV"
  }

  final case class OpCreateUserEvent(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(299)
    override val name: String = "OpCreateUserEvent"
  }

  final case class OpCrossWorkgroupCastToPtrALTERA(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5938)
    override val name: String = "OpCrossWorkgroupCastToPtrALTERA"
  }

  final case class OpDPdx(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(207)
    override val name: String = "OpDPdx"
  }

  final case class OpDPdxCoarse(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(213)
    override val name: String = "OpDPdxCoarse"
  }

  final case class OpDPdxFine(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(210)
    override val name: String = "OpDPdxFine"
  }

  final case class OpDPdy(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(208)
    override val name: String = "OpDPdy"
  }

  final case class OpDPdyCoarse(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(214)
    override val name: String = "OpDPdyCoarse"
  }

  final case class OpDPdyFine(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(211)
    override val name: String = "OpDPdyFine"
  }

  final case class OpDecorate(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(71)
    override val name: String = "OpDecorate"
  }

  final case class OpDecorateId(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(332)
    override val name: String = "OpDecorateId"
  }

  final case class OpDecorateString(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5632)
    override val name: String = "OpDecorateString"
  }

  final case class OpDecorationGroup(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(73)
    override val name: String = "OpDecorationGroup"
  }

  final case class OpDemoteToHelperInvocation(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5380)
    override val name: String = "OpDemoteToHelperInvocation"
  }

  final case class OpDepthAttachmentReadEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4161)
    override val name: String = "OpDepthAttachmentReadEXT"
  }

  final case class OpDot(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(148)
    override val name: String = "OpDot"
  }

  final case class OpEmitMeshTasksEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5294)
    override val name: String = "OpEmitMeshTasksEXT"
  }

  final case class OpEmitStreamVertex(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(220)
    override val name: String = "OpEmitStreamVertex"
  }

  final case class OpEmitVertex(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(218)
    override val name: String = "OpEmitVertex"
  }

  final case class OpEndInvocationInterlockEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5365)
    override val name: String = "OpEndInvocationInterlockEXT"
  }

  final case class OpEndPrimitive(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(219)
    override val name: String = "OpEndPrimitive"
  }

  final case class OpEndStreamPrimitive(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(221)
    override val name: String = "OpEndStreamPrimitive"
  }

  final case class OpEnqueueKernel(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(292)
    override val name: String = "OpEnqueueKernel"
  }

  final case class OpEnqueueMarker(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(291)
    override val name: String = "OpEnqueueMarker"
  }

  final case class OpEnqueueNodePayloadsAMDX(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5075)
    override val name: String = "OpEnqueueNodePayloadsAMDX"
  }

  final case class OpEntryPoint(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(15)
    override val name: String = "OpEntryPoint"
  }

  final case class OpExecuteCallableKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4446)
    override val name: String = "OpExecuteCallableKHR"
  }

  final case class OpExecuteCallableNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5344)
    override val name: String = "OpExecuteCallableNV"
  }

  final case class OpExecutionMode(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(16)
    override val name: String = "OpExecutionMode"
  }

  final case class OpExecutionModeId(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(331)
    override val name: String = "OpExecutionModeId"
  }

  final case class OpExpectKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5631)
    override val name: String = "OpExpectKHR"
  }

  final case class OpExtInst(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(12)
    override val name: String = "OpExtInst"
  }

  final case class OpExtInstImport(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(11)
    override val name: String = "OpExtInstImport"
  }

  final case class OpExtInstWithForwardRefsKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4433)
    override val name: String = "OpExtInstWithForwardRefsKHR"
  }

  final case class OpExtension(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(10)
    override val name: String = "OpExtension"
  }

  final case class OpExtractSubArrayQCOM(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4542)
    override val name: String = "OpExtractSubArrayQCOM"
  }

  final case class OpFAdd(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(129)
    override val name: String = "OpFAdd"
  }

  final case class OpFConvert(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(115)
    override val name: String = "OpFConvert"
  }

  final case class OpFDiv(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(136)
    override val name: String = "OpFDiv"
  }

  final case class OpFDot2MixAcc16VALVE(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6917)
    override val name: String = "OpFDot2MixAcc16VALVE"
  }

  final case class OpFDot2MixAcc32VALVE(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6916)
    override val name: String = "OpFDot2MixAcc32VALVE"
  }

  final case class OpFDot4MixAcc32VALVE(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6918)
    override val name: String = "OpFDot4MixAcc32VALVE"
  }

  final case class OpFMod(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(141)
    override val name: String = "OpFMod"
  }

  final case class OpFMul(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(133)
    override val name: String = "OpFMul"
  }

  final case class OpFNegate(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(127)
    override val name: String = "OpFNegate"
  }

  final case class OpFOrdEqual(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(180)
    override val name: String = "OpFOrdEqual"
  }

  final case class OpFOrdGreaterThan(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(186)
    override val name: String = "OpFOrdGreaterThan"
  }

  final case class OpFOrdGreaterThanEqual(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(190)
    override val name: String = "OpFOrdGreaterThanEqual"
  }

  final case class OpFOrdLessThan(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(184)
    override val name: String = "OpFOrdLessThan"
  }

  final case class OpFOrdLessThanEqual(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(188)
    override val name: String = "OpFOrdLessThanEqual"
  }

  final case class OpFOrdNotEqual(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(182)
    override val name: String = "OpFOrdNotEqual"
  }

  final case class OpFPGARegALTERA(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5949)
    override val name: String = "OpFPGARegALTERA"
  }

  final case class OpFRem(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(140)
    override val name: String = "OpFRem"
  }

  final case class OpFSub(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(131)
    override val name: String = "OpFSub"
  }

  final case class OpFUnordEqual(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(181)
    override val name: String = "OpFUnordEqual"
  }

  final case class OpFUnordGreaterThan(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(187)
    override val name: String = "OpFUnordGreaterThan"
  }

  final case class OpFUnordGreaterThanEqual(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(191)
    override val name: String = "OpFUnordGreaterThanEqual"
  }

  final case class OpFUnordLessThan(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(185)
    override val name: String = "OpFUnordLessThan"
  }

  final case class OpFUnordLessThanEqual(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(189)
    override val name: String = "OpFUnordLessThanEqual"
  }

  final case class OpFUnordNotEqual(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(183)
    override val name: String = "OpFUnordNotEqual"
  }

  final case class OpFetchMicroTriangleVertexBarycentricNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5301)
    override val name: String = "OpFetchMicroTriangleVertexBarycentricNV"
  }

  final case class OpFetchMicroTriangleVertexPositionNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5300)
    override val name: String = "OpFetchMicroTriangleVertexPositionNV"
  }

  final case class OpFinishWritingNodePayloadAMDX(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5078)
    override val name: String = "OpFinishWritingNodePayloadAMDX"
  }

  final case class OpFixedCosALTERA(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5927)
    override val name: String = "OpFixedCosALTERA"
  }

  final case class OpFixedCosPiALTERA(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5930)
    override val name: String = "OpFixedCosPiALTERA"
  }

  final case class OpFixedExpALTERA(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5933)
    override val name: String = "OpFixedExpALTERA"
  }

  final case class OpFixedLogALTERA(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5932)
    override val name: String = "OpFixedLogALTERA"
  }

  final case class OpFixedRecipALTERA(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5924)
    override val name: String = "OpFixedRecipALTERA"
  }

  final case class OpFixedRsqrtALTERA(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5925)
    override val name: String = "OpFixedRsqrtALTERA"
  }

  final case class OpFixedSinALTERA(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5926)
    override val name: String = "OpFixedSinALTERA"
  }

  final case class OpFixedSinCosALTERA(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5928)
    override val name: String = "OpFixedSinCosALTERA"
  }

  final case class OpFixedSinCosPiALTERA(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5931)
    override val name: String = "OpFixedSinCosPiALTERA"
  }

  final case class OpFixedSinPiALTERA(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5929)
    override val name: String = "OpFixedSinPiALTERA"
  }

  final case class OpFixedSqrtALTERA(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5923)
    override val name: String = "OpFixedSqrtALTERA"
  }

  final case class OpFmaKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4427)
    override val name: String = "OpFmaKHR"
  }

  final case class OpFragmentFetchAMD(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5012)
    override val name: String = "OpFragmentFetchAMD"
  }

  final case class OpFragmentMaskFetchAMD(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5011)
    override val name: String = "OpFragmentMaskFetchAMD"
  }

  final case class OpFreezeKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5159)
    override val name: String = "OpFreezeKHR"
  }

  final case class OpFunction(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(54)
    override val name: String = "OpFunction"
  }

  final case class OpFunctionCall(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(57)
    override val name: String = "OpFunctionCall"
  }

  final case class OpFunctionEnd(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(56)
    override val name: String = "OpFunctionEnd"
  }

  final case class OpFunctionParameter(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(55)
    override val name: String = "OpFunctionParameter"
  }

  final case class OpFunctionPointerCallINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5601)
    override val name: String = "OpFunctionPointerCallINTEL"
  }

  final case class OpFwidth(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(209)
    override val name: String = "OpFwidth"
  }

  final case class OpFwidthCoarse(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(215)
    override val name: String = "OpFwidthCoarse"
  }

  final case class OpFwidthFine(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(212)
    override val name: String = "OpFwidthFine"
  }

  final case class OpGenericCastToPtr(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(122)
    override val name: String = "OpGenericCastToPtr"
  }

  final case class OpGenericCastToPtrExplicit(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(123)
    override val name: String = "OpGenericCastToPtrExplicit"
  }

  final case class OpGenericPtrMemSemantics(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(69)
    override val name: String = "OpGenericPtrMemSemantics"
  }

  final case class OpGetDefaultQueue(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(303)
    override val name: String = "OpGetDefaultQueue"
  }

  final case class OpGetKernelLocalSizeForSubgroupCount(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(325)
    override val name: String = "OpGetKernelLocalSizeForSubgroupCount"
  }

  final case class OpGetKernelMaxNumSubgroups(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(326)
    override val name: String = "OpGetKernelMaxNumSubgroups"
  }

  final case class OpGetKernelNDrangeMaxSubGroupSize(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(294)
    override val name: String = "OpGetKernelNDrangeMaxSubGroupSize"
  }

  final case class OpGetKernelNDrangeSubGroupCount(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(293)
    override val name: String = "OpGetKernelNDrangeSubGroupCount"
  }

  final case class OpGetKernelPreferredWorkGroupSizeMultiple(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(296)
    override val name: String = "OpGetKernelPreferredWorkGroupSizeMultiple"
  }

  final case class OpGetKernelWorkGroupSize(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(295)
    override val name: String = "OpGetKernelWorkGroupSize"
  }

  final case class OpGetMaxPipePackets(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(284)
    override val name: String = "OpGetMaxPipePackets"
  }

  final case class OpGetNumPipePackets(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(283)
    override val name: String = "OpGetNumPipePackets"
  }

  final case class OpGraphARM(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4183)
    override val name: String = "OpGraphARM"
  }

  final case class OpGraphConstantARM(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4181)
    override val name: String = "OpGraphConstantARM"
  }

  final case class OpGraphEndARM(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4186)
    override val name: String = "OpGraphEndARM"
  }

  final case class OpGraphEntryPointARM(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4182)
    override val name: String = "OpGraphEntryPointARM"
  }

  final case class OpGraphInputARM(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4184)
    override val name: String = "OpGraphInputARM"
  }

  final case class OpGraphSetOutputARM(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4185)
    override val name: String = "OpGraphSetOutputARM"
  }

  final case class OpGroupAll(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(261)
    override val name: String = "OpGroupAll"
  }

  final case class OpGroupAny(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(262)
    override val name: String = "OpGroupAny"
  }

  final case class OpGroupAsyncCopy(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(259)
    override val name: String = "OpGroupAsyncCopy"
  }

  final case class OpGroupBitwiseAndKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6403)
    override val name: String = "OpGroupBitwiseAndKHR"
  }

  final case class OpGroupBitwiseOrKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6404)
    override val name: String = "OpGroupBitwiseOrKHR"
  }

  final case class OpGroupBitwiseXorKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6405)
    override val name: String = "OpGroupBitwiseXorKHR"
  }

  final case class OpGroupBroadcast(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(263)
    override val name: String = "OpGroupBroadcast"
  }

  final case class OpGroupCommitReadPipe(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(287)
    override val name: String = "OpGroupCommitReadPipe"
  }

  final case class OpGroupCommitWritePipe(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(288)
    override val name: String = "OpGroupCommitWritePipe"
  }

  final case class OpGroupDecorate(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(74)
    override val name: String = "OpGroupDecorate"
  }

  final case class OpGroupFAdd(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(265)
    override val name: String = "OpGroupFAdd"
  }

  final case class OpGroupFAddNonUniformAMD(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5001)
    override val name: String = "OpGroupFAddNonUniformAMD"
  }

  final case class OpGroupFMax(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(269)
    override val name: String = "OpGroupFMax"
  }

  final case class OpGroupFMaxNonUniformAMD(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5005)
    override val name: String = "OpGroupFMaxNonUniformAMD"
  }

  final case class OpGroupFMin(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(266)
    override val name: String = "OpGroupFMin"
  }

  final case class OpGroupFMinNonUniformAMD(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5002)
    override val name: String = "OpGroupFMinNonUniformAMD"
  }

  final case class OpGroupFMulKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6402)
    override val name: String = "OpGroupFMulKHR"
  }

  final case class OpGroupIAdd(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(264)
    override val name: String = "OpGroupIAdd"
  }

  final case class OpGroupIAddNonUniformAMD(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5000)
    override val name: String = "OpGroupIAddNonUniformAMD"
  }

  final case class OpGroupIMulKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6401)
    override val name: String = "OpGroupIMulKHR"
  }

  final case class OpGroupLogicalAndKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6406)
    override val name: String = "OpGroupLogicalAndKHR"
  }

  final case class OpGroupLogicalOrKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6407)
    override val name: String = "OpGroupLogicalOrKHR"
  }

  final case class OpGroupLogicalXorKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6408)
    override val name: String = "OpGroupLogicalXorKHR"
  }

  final case class OpGroupMemberDecorate(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(75)
    override val name: String = "OpGroupMemberDecorate"
  }

  final case class OpGroupNonUniformAll(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(334)
    override val name: String = "OpGroupNonUniformAll"
  }

  final case class OpGroupNonUniformAllEqual(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(336)
    override val name: String = "OpGroupNonUniformAllEqual"
  }

  final case class OpGroupNonUniformAny(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(335)
    override val name: String = "OpGroupNonUniformAny"
  }

  final case class OpGroupNonUniformBallot(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(339)
    override val name: String = "OpGroupNonUniformBallot"
  }

  final case class OpGroupNonUniformBallotBitCount(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(342)
    override val name: String = "OpGroupNonUniformBallotBitCount"
  }

  final case class OpGroupNonUniformBallotBitExtract(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(341)
    override val name: String = "OpGroupNonUniformBallotBitExtract"
  }

  final case class OpGroupNonUniformBallotFindLSB(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(343)
    override val name: String = "OpGroupNonUniformBallotFindLSB"
  }

  final case class OpGroupNonUniformBallotFindMSB(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(344)
    override val name: String = "OpGroupNonUniformBallotFindMSB"
  }

  final case class OpGroupNonUniformBitwiseAnd(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(359)
    override val name: String = "OpGroupNonUniformBitwiseAnd"
  }

  final case class OpGroupNonUniformBitwiseOr(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(360)
    override val name: String = "OpGroupNonUniformBitwiseOr"
  }

  final case class OpGroupNonUniformBitwiseXor(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(361)
    override val name: String = "OpGroupNonUniformBitwiseXor"
  }

  final case class OpGroupNonUniformBroadcast(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(337)
    override val name: String = "OpGroupNonUniformBroadcast"
  }

  final case class OpGroupNonUniformBroadcastFirst(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(338)
    override val name: String = "OpGroupNonUniformBroadcastFirst"
  }

  final case class OpGroupNonUniformElect(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(333)
    override val name: String = "OpGroupNonUniformElect"
  }

  final case class OpGroupNonUniformFAdd(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(350)
    override val name: String = "OpGroupNonUniformFAdd"
  }

  final case class OpGroupNonUniformFMax(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(358)
    override val name: String = "OpGroupNonUniformFMax"
  }

  final case class OpGroupNonUniformFMin(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(355)
    override val name: String = "OpGroupNonUniformFMin"
  }

  final case class OpGroupNonUniformFMul(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(352)
    override val name: String = "OpGroupNonUniformFMul"
  }

  final case class OpGroupNonUniformIAdd(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(349)
    override val name: String = "OpGroupNonUniformIAdd"
  }

  final case class OpGroupNonUniformIMul(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(351)
    override val name: String = "OpGroupNonUniformIMul"
  }

  final case class OpGroupNonUniformInverseBallot(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(340)
    override val name: String = "OpGroupNonUniformInverseBallot"
  }

  final case class OpGroupNonUniformLogicalAnd(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(362)
    override val name: String = "OpGroupNonUniformLogicalAnd"
  }

  final case class OpGroupNonUniformLogicalOr(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(363)
    override val name: String = "OpGroupNonUniformLogicalOr"
  }

  final case class OpGroupNonUniformLogicalXor(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(364)
    override val name: String = "OpGroupNonUniformLogicalXor"
  }

  final case class OpGroupNonUniformPartitionEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5296)
    override val name: String = "OpGroupNonUniformPartitionEXT"
  }

  final case class OpGroupNonUniformQuadAllKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5110)
    override val name: String = "OpGroupNonUniformQuadAllKHR"
  }

  final case class OpGroupNonUniformQuadAnyKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5111)
    override val name: String = "OpGroupNonUniformQuadAnyKHR"
  }

  final case class OpGroupNonUniformQuadBroadcast(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(365)
    override val name: String = "OpGroupNonUniformQuadBroadcast"
  }

  final case class OpGroupNonUniformQuadSwap(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(366)
    override val name: String = "OpGroupNonUniformQuadSwap"
  }

  final case class OpGroupNonUniformRotateKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4431)
    override val name: String = "OpGroupNonUniformRotateKHR"
  }

  final case class OpGroupNonUniformSMax(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(356)
    override val name: String = "OpGroupNonUniformSMax"
  }

  final case class OpGroupNonUniformSMin(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(353)
    override val name: String = "OpGroupNonUniformSMin"
  }

  final case class OpGroupNonUniformShuffle(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(345)
    override val name: String = "OpGroupNonUniformShuffle"
  }

  final case class OpGroupNonUniformShuffleDown(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(348)
    override val name: String = "OpGroupNonUniformShuffleDown"
  }

  final case class OpGroupNonUniformShuffleUp(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(347)
    override val name: String = "OpGroupNonUniformShuffleUp"
  }

  final case class OpGroupNonUniformShuffleXor(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(346)
    override val name: String = "OpGroupNonUniformShuffleXor"
  }

  final case class OpGroupNonUniformUMax(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(357)
    override val name: String = "OpGroupNonUniformUMax"
  }

  final case class OpGroupNonUniformUMin(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(354)
    override val name: String = "OpGroupNonUniformUMin"
  }

  final case class OpGroupReserveReadPipePackets(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(285)
    override val name: String = "OpGroupReserveReadPipePackets"
  }

  final case class OpGroupReserveWritePipePackets(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(286)
    override val name: String = "OpGroupReserveWritePipePackets"
  }

  final case class OpGroupSMax(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(271)
    override val name: String = "OpGroupSMax"
  }

  final case class OpGroupSMaxNonUniformAMD(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5007)
    override val name: String = "OpGroupSMaxNonUniformAMD"
  }

  final case class OpGroupSMin(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(268)
    override val name: String = "OpGroupSMin"
  }

  final case class OpGroupSMinNonUniformAMD(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5004)
    override val name: String = "OpGroupSMinNonUniformAMD"
  }

  final case class OpGroupUMax(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(270)
    override val name: String = "OpGroupUMax"
  }

  final case class OpGroupUMaxNonUniformAMD(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5006)
    override val name: String = "OpGroupUMaxNonUniformAMD"
  }

  final case class OpGroupUMin(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(267)
    override val name: String = "OpGroupUMin"
  }

  final case class OpGroupUMinNonUniformAMD(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5003)
    override val name: String = "OpGroupUMinNonUniformAMD"
  }

  final case class OpGroupWaitEvents(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(260)
    override val name: String = "OpGroupWaitEvents"
  }

  final case class OpHitObjectExecuteShaderEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5319)
    override val name: String = "OpHitObjectExecuteShaderEXT"
  }

  final case class OpHitObjectExecuteShaderNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5264)
    override val name: String = "OpHitObjectExecuteShaderNV"
  }

  final case class OpHitObjectGetAttributesEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5321)
    override val name: String = "OpHitObjectGetAttributesEXT"
  }

  final case class OpHitObjectGetAttributesNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5266)
    override val name: String = "OpHitObjectGetAttributesNV"
  }

  final case class OpHitObjectGetClusterIdNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5346)
    override val name: String = "OpHitObjectGetClusterIdNV"
  }

  final case class OpHitObjectGetCurrentTimeEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5320)
    override val name: String = "OpHitObjectGetCurrentTimeEXT"
  }

  final case class OpHitObjectGetCurrentTimeNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5265)
    override val name: String = "OpHitObjectGetCurrentTimeNV"
  }

  final case class OpHitObjectGetGeometryIndexEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5324)
    override val name: String = "OpHitObjectGetGeometryIndexEXT"
  }

  final case class OpHitObjectGetGeometryIndexNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5269)
    override val name: String = "OpHitObjectGetGeometryIndexNV"
  }

  final case class OpHitObjectGetHitKindEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5322)
    override val name: String = "OpHitObjectGetHitKindEXT"
  }

  final case class OpHitObjectGetHitKindNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5267)
    override val name: String = "OpHitObjectGetHitKindNV"
  }

  final case class OpHitObjectGetInstanceCustomIndexEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5326)
    override val name: String = "OpHitObjectGetInstanceCustomIndexEXT"
  }

  final case class OpHitObjectGetInstanceCustomIndexNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5271)
    override val name: String = "OpHitObjectGetInstanceCustomIndexNV"
  }

  final case class OpHitObjectGetInstanceIdEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5325)
    override val name: String = "OpHitObjectGetInstanceIdEXT"
  }

  final case class OpHitObjectGetInstanceIdNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5270)
    override val name: String = "OpHitObjectGetInstanceIdNV"
  }

  final case class OpHitObjectGetIntersectionTriangleVertexPositionsEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5307)
    override val name: String = "OpHitObjectGetIntersectionTriangleVertexPositionsEXT"
  }

  final case class OpHitObjectGetLSSPositionsNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5434)
    override val name: String = "OpHitObjectGetLSSPositionsNV"
  }

  final case class OpHitObjectGetLSSRadiiNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5435)
    override val name: String = "OpHitObjectGetLSSRadiiNV"
  }

  final case class OpHitObjectGetObjectRayDirectionEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5328)
    override val name: String = "OpHitObjectGetObjectRayDirectionEXT"
  }

  final case class OpHitObjectGetObjectRayDirectionNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5254)
    override val name: String = "OpHitObjectGetObjectRayDirectionNV"
  }

  final case class OpHitObjectGetObjectRayOriginEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5327)
    override val name: String = "OpHitObjectGetObjectRayOriginEXT"
  }

  final case class OpHitObjectGetObjectRayOriginNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5255)
    override val name: String = "OpHitObjectGetObjectRayOriginNV"
  }

  final case class OpHitObjectGetObjectToWorldEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5331)
    override val name: String = "OpHitObjectGetObjectToWorldEXT"
  }

  final case class OpHitObjectGetObjectToWorldNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5253)
    override val name: String = "OpHitObjectGetObjectToWorldNV"
  }

  final case class OpHitObjectGetPrimitiveIndexEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5323)
    override val name: String = "OpHitObjectGetPrimitiveIndexEXT"
  }

  final case class OpHitObjectGetPrimitiveIndexNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5268)
    override val name: String = "OpHitObjectGetPrimitiveIndexNV"
  }

  final case class OpHitObjectGetRayFlagsEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5308)
    override val name: String = "OpHitObjectGetRayFlagsEXT"
  }

  final case class OpHitObjectGetRayTMaxEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5333)
    override val name: String = "OpHitObjectGetRayTMaxEXT"
  }

  final case class OpHitObjectGetRayTMaxNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5274)
    override val name: String = "OpHitObjectGetRayTMaxNV"
  }

  final case class OpHitObjectGetRayTMinEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5347)
    override val name: String = "OpHitObjectGetRayTMinEXT"
  }

  final case class OpHitObjectGetRayTMinNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5275)
    override val name: String = "OpHitObjectGetRayTMinNV"
  }

  final case class OpHitObjectGetShaderBindingTableRecordIndexEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5348)
    override val name: String = "OpHitObjectGetShaderBindingTableRecordIndexEXT"
  }

  final case class OpHitObjectGetShaderBindingTableRecordIndexNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5258)
    override val name: String = "OpHitObjectGetShaderBindingTableRecordIndexNV"
  }

  final case class OpHitObjectGetShaderRecordBufferHandleEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5349)
    override val name: String = "OpHitObjectGetShaderRecordBufferHandleEXT"
  }

  final case class OpHitObjectGetShaderRecordBufferHandleNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5257)
    override val name: String = "OpHitObjectGetShaderRecordBufferHandleNV"
  }

  final case class OpHitObjectGetSpherePositionNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5432)
    override val name: String = "OpHitObjectGetSpherePositionNV"
  }

  final case class OpHitObjectGetSphereRadiusNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5433)
    override val name: String = "OpHitObjectGetSphereRadiusNV"
  }

  final case class OpHitObjectGetWorldRayDirectionEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5329)
    override val name: String = "OpHitObjectGetWorldRayDirectionEXT"
  }

  final case class OpHitObjectGetWorldRayDirectionNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5272)
    override val name: String = "OpHitObjectGetWorldRayDirectionNV"
  }

  final case class OpHitObjectGetWorldRayOriginEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5330)
    override val name: String = "OpHitObjectGetWorldRayOriginEXT"
  }

  final case class OpHitObjectGetWorldRayOriginNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5273)
    override val name: String = "OpHitObjectGetWorldRayOriginNV"
  }

  final case class OpHitObjectGetWorldToObjectEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5332)
    override val name: String = "OpHitObjectGetWorldToObjectEXT"
  }

  final case class OpHitObjectGetWorldToObjectNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5252)
    override val name: String = "OpHitObjectGetWorldToObjectNV"
  }

  final case class OpHitObjectIsEmptyEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5350)
    override val name: String = "OpHitObjectIsEmptyEXT"
  }

  final case class OpHitObjectIsEmptyNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5276)
    override val name: String = "OpHitObjectIsEmptyNV"
  }

  final case class OpHitObjectIsHitEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5351)
    override val name: String = "OpHitObjectIsHitEXT"
  }

  final case class OpHitObjectIsHitNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5277)
    override val name: String = "OpHitObjectIsHitNV"
  }

  final case class OpHitObjectIsLSSHitNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5437)
    override val name: String = "OpHitObjectIsLSSHitNV"
  }

  final case class OpHitObjectIsMissEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5352)
    override val name: String = "OpHitObjectIsMissEXT"
  }

  final case class OpHitObjectIsMissNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5278)
    override val name: String = "OpHitObjectIsMissNV"
  }

  final case class OpHitObjectIsSphereHitNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5436)
    override val name: String = "OpHitObjectIsSphereHitNV"
  }

  final case class OpHitObjectRecordEmptyEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5318)
    override val name: String = "OpHitObjectRecordEmptyEXT"
  }

  final case class OpHitObjectRecordEmptyNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5259)
    override val name: String = "OpHitObjectRecordEmptyNV"
  }

  final case class OpHitObjectRecordFromQueryEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5304)
    override val name: String = "OpHitObjectRecordFromQueryEXT"
  }

  final case class OpHitObjectRecordHitMotionNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5249)
    override val name: String = "OpHitObjectRecordHitMotionNV"
  }

  final case class OpHitObjectRecordHitNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5261)
    override val name: String = "OpHitObjectRecordHitNV"
  }

  final case class OpHitObjectRecordHitWithIndexMotionNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5250)
    override val name: String = "OpHitObjectRecordHitWithIndexMotionNV"
  }

  final case class OpHitObjectRecordHitWithIndexNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5262)
    override val name: String = "OpHitObjectRecordHitWithIndexNV"
  }

  final case class OpHitObjectRecordMissEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5305)
    override val name: String = "OpHitObjectRecordMissEXT"
  }

  final case class OpHitObjectRecordMissMotionEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5306)
    override val name: String = "OpHitObjectRecordMissMotionEXT"
  }

  final case class OpHitObjectRecordMissMotionNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5251)
    override val name: String = "OpHitObjectRecordMissMotionNV"
  }

  final case class OpHitObjectRecordMissNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5263)
    override val name: String = "OpHitObjectRecordMissNV"
  }

  final case class OpHitObjectReorderExecuteShaderEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5310)
    override val name: String = "OpHitObjectReorderExecuteShaderEXT"
  }

  final case class OpHitObjectSetShaderBindingTableRecordIndexEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5309)
    override val name: String = "OpHitObjectSetShaderBindingTableRecordIndexEXT"
  }

  final case class OpHitObjectTraceMotionReorderExecuteEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5312)
    override val name: String = "OpHitObjectTraceMotionReorderExecuteEXT"
  }

  final case class OpHitObjectTraceRayEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5316)
    override val name: String = "OpHitObjectTraceRayEXT"
  }

  final case class OpHitObjectTraceRayMotionEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5317)
    override val name: String = "OpHitObjectTraceRayMotionEXT"
  }

  final case class OpHitObjectTraceRayMotionNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5256)
    override val name: String = "OpHitObjectTraceRayMotionNV"
  }

  final case class OpHitObjectTraceRayNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5260)
    override val name: String = "OpHitObjectTraceRayNV"
  }

  final case class OpHitObjectTraceReorderExecuteEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5311)
    override val name: String = "OpHitObjectTraceReorderExecuteEXT"
  }

  final case class OpIAdd(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(128)
    override val name: String = "OpIAdd"
  }

  final case class OpIAddCarry(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(149)
    override val name: String = "OpIAddCarry"
  }

  final case class OpIAddSatINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5589)
    override val name: String = "OpIAddSatINTEL"
  }

  final case class OpIAverageINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5591)
    override val name: String = "OpIAverageINTEL"
  }

  final case class OpIAverageRoundedINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5593)
    override val name: String = "OpIAverageRoundedINTEL"
  }

  final case class OpIEqual(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(170)
    override val name: String = "OpIEqual"
  }

  final case class OpIMul(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(132)
    override val name: String = "OpIMul"
  }

  final case class OpIMul32x16INTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5597)
    override val name: String = "OpIMul32x16INTEL"
  }

  final case class OpINotEqual(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(171)
    override val name: String = "OpINotEqual"
  }

  final case class OpISub(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(130)
    override val name: String = "OpISub"
  }

  final case class OpISubBorrow(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(150)
    override val name: String = "OpISubBorrow"
  }

  final case class OpISubSatINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5595)
    override val name: String = "OpISubSatINTEL"
  }

  final case class OpIgnoreIntersectionKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4448)
    override val name: String = "OpIgnoreIntersectionKHR"
  }

  final case class OpIgnoreIntersectionNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5335)
    override val name: String = "OpIgnoreIntersectionNV"
  }

  final case class OpImage(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(100)
    override val name: String = "OpImage"
  }

  final case class OpImageBlockMatchGatherSADQCOM(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4503)
    override val name: String = "OpImageBlockMatchGatherSADQCOM"
  }

  final case class OpImageBlockMatchGatherSSDQCOM(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4502)
    override val name: String = "OpImageBlockMatchGatherSSDQCOM"
  }

  final case class OpImageBlockMatchSADQCOM(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4483)
    override val name: String = "OpImageBlockMatchSADQCOM"
  }

  final case class OpImageBlockMatchSSDQCOM(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4482)
    override val name: String = "OpImageBlockMatchSSDQCOM"
  }

  final case class OpImageBlockMatchWindowSADQCOM(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4501)
    override val name: String = "OpImageBlockMatchWindowSADQCOM"
  }

  final case class OpImageBlockMatchWindowSSDQCOM(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4500)
    override val name: String = "OpImageBlockMatchWindowSSDQCOM"
  }

  final case class OpImageBoxFilterQCOM(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4481)
    override val name: String = "OpImageBoxFilterQCOM"
  }

  final case class OpImageDrefGather(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(97)
    override val name: String = "OpImageDrefGather"
  }

  final case class OpImageFetch(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(95)
    override val name: String = "OpImageFetch"
  }

  final case class OpImageGather(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(96)
    override val name: String = "OpImageGather"
  }

  final case class OpImageGatherQCOM(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4545)
    override val name: String = "OpImageGatherQCOM"
  }

  final case class OpImageQueryFormat(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(101)
    override val name: String = "OpImageQueryFormat"
  }

  final case class OpImageQueryLevels(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(106)
    override val name: String = "OpImageQueryLevels"
  }

  final case class OpImageQueryLod(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(105)
    override val name: String = "OpImageQueryLod"
  }

  final case class OpImageQueryOrder(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(102)
    override val name: String = "OpImageQueryOrder"
  }

  final case class OpImageQuerySamples(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(107)
    override val name: String = "OpImageQuerySamples"
  }

  final case class OpImageQuerySize(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(104)
    override val name: String = "OpImageQuerySize"
  }

  final case class OpImageQuerySizeLod(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(103)
    override val name: String = "OpImageQuerySizeLod"
  }

  final case class OpImageRead(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(98)
    override val name: String = "OpImageRead"
  }

  final case class OpImageSampleDrefExplicitLod(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(90)
    override val name: String = "OpImageSampleDrefExplicitLod"
  }

  final case class OpImageSampleDrefImplicitLod(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(89)
    override val name: String = "OpImageSampleDrefImplicitLod"
  }

  final case class OpImageSampleExplicitLod(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(88)
    override val name: String = "OpImageSampleExplicitLod"
  }

  final case class OpImageSampleFootprintNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5283)
    override val name: String = "OpImageSampleFootprintNV"
  }

  final case class OpImageSampleImplicitLod(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(87)
    override val name: String = "OpImageSampleImplicitLod"
  }

  final case class OpImageSampleProjDrefExplicitLod(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(94)
    override val name: String = "OpImageSampleProjDrefExplicitLod"
  }

  final case class OpImageSampleProjDrefImplicitLod(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(93)
    override val name: String = "OpImageSampleProjDrefImplicitLod"
  }

  final case class OpImageSampleProjExplicitLod(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(92)
    override val name: String = "OpImageSampleProjExplicitLod"
  }

  final case class OpImageSampleProjImplicitLod(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(91)
    override val name: String = "OpImageSampleProjImplicitLod"
  }

  final case class OpImageSampleWeightedQCOM(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4480)
    override val name: String = "OpImageSampleWeightedQCOM"
  }

  final case class OpImageSparseDrefGather(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(315)
    override val name: String = "OpImageSparseDrefGather"
  }

  final case class OpImageSparseFetch(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(313)
    override val name: String = "OpImageSparseFetch"
  }

  final case class OpImageSparseGather(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(314)
    override val name: String = "OpImageSparseGather"
  }

  final case class OpImageSparseRead(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(320)
    override val name: String = "OpImageSparseRead"
  }

  final case class OpImageSparseSampleDrefExplicitLod(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(308)
    override val name: String = "OpImageSparseSampleDrefExplicitLod"
  }

  final case class OpImageSparseSampleDrefImplicitLod(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(307)
    override val name: String = "OpImageSparseSampleDrefImplicitLod"
  }

  final case class OpImageSparseSampleExplicitLod(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(306)
    override val name: String = "OpImageSparseSampleExplicitLod"
  }

  final case class OpImageSparseSampleImplicitLod(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(305)
    override val name: String = "OpImageSparseSampleImplicitLod"
  }

  final case class OpImageSparseSampleProjDrefExplicitLod(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(312)
    override val name: String = "OpImageSparseSampleProjDrefExplicitLod"
  }

  final case class OpImageSparseSampleProjDrefImplicitLod(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(311)
    override val name: String = "OpImageSparseSampleProjDrefImplicitLod"
  }

  final case class OpImageSparseSampleProjExplicitLod(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(310)
    override val name: String = "OpImageSparseSampleProjExplicitLod"
  }

  final case class OpImageSparseSampleProjImplicitLod(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(309)
    override val name: String = "OpImageSparseSampleProjImplicitLod"
  }

  final case class OpImageSparseTexelsResident(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(316)
    override val name: String = "OpImageSparseTexelsResident"
  }

  final case class OpImageTexelPointer(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(60)
    override val name: String = "OpImageTexelPointer"
  }

  final case class OpImageWrite(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(99)
    override val name: String = "OpImageWrite"
  }

  final case class OpInBoundsAccessChain(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(66)
    override val name: String = "OpInBoundsAccessChain"
  }

  final case class OpInBoundsPtrAccessChain(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(70)
    override val name: String = "OpInBoundsPtrAccessChain"
  }

  final case class OpIsFinite(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(158)
    override val name: String = "OpIsFinite"
  }

  final case class OpIsHelperInvocationEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5381)
    override val name: String = "OpIsHelperInvocationEXT"
  }

  final case class OpIsInf(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(157)
    override val name: String = "OpIsInf"
  }

  final case class OpIsNan(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(156)
    override val name: String = "OpIsNan"
  }

  final case class OpIsNodePayloadValidAMDX(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5101)
    override val name: String = "OpIsNodePayloadValidAMDX"
  }

  final case class OpIsNormal(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(159)
    override val name: String = "OpIsNormal"
  }

  final case class OpIsValidEvent(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(300)
    override val name: String = "OpIsValidEvent"
  }

  final case class OpIsValidReserveId(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(282)
    override val name: String = "OpIsValidReserveId"
  }

  final case class OpKill(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(252)
    override val name: String = "OpKill"
  }

  final case class OpLabel(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(248)
    override val name: String = "OpLabel"
  }

  final case class OpLessOrGreater(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(161)
    override val name: String = "OpLessOrGreater"
  }

  final case class OpLifetimeStart(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(256)
    override val name: String = "OpLifetimeStart"
  }

  final case class OpLifetimeStop(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(257)
    override val name: String = "OpLifetimeStop"
  }

  final case class OpLine(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(8)
    override val name: String = "OpLine"
  }

  final case class OpLoad(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(61)
    override val name: String = "OpLoad"
  }

  final case class OpLogicalAnd(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(167)
    override val name: String = "OpLogicalAnd"
  }

  final case class OpLogicalEqual(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(164)
    override val name: String = "OpLogicalEqual"
  }

  final case class OpLogicalNot(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(168)
    override val name: String = "OpLogicalNot"
  }

  final case class OpLogicalNotEqual(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(165)
    override val name: String = "OpLogicalNotEqual"
  }

  final case class OpLogicalOr(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(166)
    override val name: String = "OpLogicalOr"
  }

  final case class OpLoopControlINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5887)
    override val name: String = "OpLoopControlINTEL"
  }

  final case class OpLoopMerge(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(246)
    override val name: String = "OpLoopMerge"
  }

  final case class OpMaskedGatherINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6428)
    override val name: String = "OpMaskedGatherINTEL"
  }

  final case class OpMaskedScatterINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6429)
    override val name: String = "OpMaskedScatterINTEL"
  }

  final case class OpMatrixTimesMatrix(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(146)
    override val name: String = "OpMatrixTimesMatrix"
  }

  final case class OpMatrixTimesScalar(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(143)
    override val name: String = "OpMatrixTimesScalar"
  }

  final case class OpMatrixTimesVector(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(145)
    override val name: String = "OpMatrixTimesVector"
  }

  final case class OpMemberDecorate(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(72)
    override val name: String = "OpMemberDecorate"
  }

  final case class OpMemberDecorateIdEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5127)
    override val name: String = "OpMemberDecorateIdEXT"
  }

  final case class OpMemberDecorateString(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5633)
    override val name: String = "OpMemberDecorateString"
  }

  final case class OpMemberName(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6)
    override val name: String = "OpMemberName"
  }

  final case class OpMemoryBarrier(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(225)
    override val name: String = "OpMemoryBarrier"
  }

  final case class OpMemoryModel(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(14)
    override val name: String = "OpMemoryModel"
  }

  final case class OpMemoryNamedBarrier(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(329)
    override val name: String = "OpMemoryNamedBarrier"
  }

  final case class OpModuleProcessed(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(330)
    override val name: String = "OpModuleProcessed"
  }

  final case class OpName(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5)
    override val name: String = "OpName"
  }

  final case class OpNamedBarrierInitialize(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(328)
    override val name: String = "OpNamedBarrierInitialize"
  }

  final case class OpNoLine(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(317)
    override val name: String = "OpNoLine"
  }

  final case class OpNodePayloadArrayLengthAMDX(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5090)
    override val name: String = "OpNodePayloadArrayLengthAMDX"
  }

  final case class OpNop(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(0)
    override val name: String = "OpNop"
  }

  final case class OpNot(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(200)
    override val name: String = "OpNot"
  }

  final case class OpOrdered(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(162)
    override val name: String = "OpOrdered"
  }

  final case class OpOuterProduct(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(147)
    override val name: String = "OpOuterProduct"
  }

  final case class OpPhi(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(245)
    override val name: String = "OpPhi"
  }

  final case class OpPoisonKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5158)
    override val name: String = "OpPoisonKHR"
  }

  final case class OpPredicatedLoadINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6258)
    override val name: String = "OpPredicatedLoadINTEL"
  }

  final case class OpPredicatedStoreINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6259)
    override val name: String = "OpPredicatedStoreINTEL"
  }

  final case class OpPtrAccessChain(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(67)
    override val name: String = "OpPtrAccessChain"
  }

  final case class OpPtrCastToCrossWorkgroupALTERA(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5934)
    override val name: String = "OpPtrCastToCrossWorkgroupALTERA"
  }

  final case class OpPtrCastToGeneric(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(121)
    override val name: String = "OpPtrCastToGeneric"
  }

  final case class OpPtrDiff(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(403)
    override val name: String = "OpPtrDiff"
  }

  final case class OpPtrEqual(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(401)
    override val name: String = "OpPtrEqual"
  }

  final case class OpPtrNotEqual(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(402)
    override val name: String = "OpPtrNotEqual"
  }

  final case class OpQuantizeToF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(116)
    override val name: String = "OpQuantizeToF16"
  }

  final case class OpRawAccessChainNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5398)
    override val name: String = "OpRawAccessChainNV"
  }

  final case class OpRayQueryConfirmIntersectionKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4476)
    override val name: String = "OpRayQueryConfirmIntersectionKHR"
  }

  final case class OpRayQueryGenerateIntersectionKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4475)
    override val name: String = "OpRayQueryGenerateIntersectionKHR"
  }

  final case class OpRayQueryGetIntersectionBarycentricsKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6024)
    override val name: String = "OpRayQueryGetIntersectionBarycentricsKHR"
  }

  final case class OpRayQueryGetIntersectionCandidateAABBOpaqueKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6026)
    override val name: String = "OpRayQueryGetIntersectionCandidateAABBOpaqueKHR"
  }

  final case class OpRayQueryGetIntersectionClusterIdNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5345)
    override val name: String = "OpRayQueryGetIntersectionClusterIdNV"
  }

  final case class OpRayQueryGetIntersectionFrontFaceKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6025)
    override val name: String = "OpRayQueryGetIntersectionFrontFaceKHR"
  }

  final case class OpRayQueryGetIntersectionGeometryIndexKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6022)
    override val name: String = "OpRayQueryGetIntersectionGeometryIndexKHR"
  }

  final case class OpRayQueryGetIntersectionInstanceCustomIndexKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6019)
    override val name: String = "OpRayQueryGetIntersectionInstanceCustomIndexKHR"
  }

  final case class OpRayQueryGetIntersectionInstanceIdKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6020)
    override val name: String = "OpRayQueryGetIntersectionInstanceIdKHR"
  }

  final case class OpRayQueryGetIntersectionInstanceShaderBindingTableRecordOffsetKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6021)
    override val name: String = "OpRayQueryGetIntersectionInstanceShaderBindingTableRecordOffsetKHR"
  }

  final case class OpRayQueryGetIntersectionLSSHitValueNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5431)
    override val name: String = "OpRayQueryGetIntersectionLSSHitValueNV"
  }

  final case class OpRayQueryGetIntersectionLSSPositionsNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5429)
    override val name: String = "OpRayQueryGetIntersectionLSSPositionsNV"
  }

  final case class OpRayQueryGetIntersectionLSSRadiiNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5430)
    override val name: String = "OpRayQueryGetIntersectionLSSRadiiNV"
  }

  final case class OpRayQueryGetIntersectionObjectRayDirectionKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6027)
    override val name: String = "OpRayQueryGetIntersectionObjectRayDirectionKHR"
  }

  final case class OpRayQueryGetIntersectionObjectRayOriginKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6028)
    override val name: String = "OpRayQueryGetIntersectionObjectRayOriginKHR"
  }

  final case class OpRayQueryGetIntersectionObjectToWorldKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6031)
    override val name: String = "OpRayQueryGetIntersectionObjectToWorldKHR"
  }

  final case class OpRayQueryGetIntersectionPrimitiveIndexKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6023)
    override val name: String = "OpRayQueryGetIntersectionPrimitiveIndexKHR"
  }

  final case class OpRayQueryGetIntersectionSpherePositionNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5427)
    override val name: String = "OpRayQueryGetIntersectionSpherePositionNV"
  }

  final case class OpRayQueryGetIntersectionSphereRadiusNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5428)
    override val name: String = "OpRayQueryGetIntersectionSphereRadiusNV"
  }

  final case class OpRayQueryGetIntersectionTKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6018)
    override val name: String = "OpRayQueryGetIntersectionTKHR"
  }

  final case class OpRayQueryGetIntersectionTriangleVertexPositionsKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5340)
    override val name: String = "OpRayQueryGetIntersectionTriangleVertexPositionsKHR"
  }

  final case class OpRayQueryGetIntersectionTypeKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4479)
    override val name: String = "OpRayQueryGetIntersectionTypeKHR"
  }

  final case class OpRayQueryGetIntersectionWorldToObjectKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6032)
    override val name: String = "OpRayQueryGetIntersectionWorldToObjectKHR"
  }

  final case class OpRayQueryGetRayFlagsKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6017)
    override val name: String = "OpRayQueryGetRayFlagsKHR"
  }

  final case class OpRayQueryGetRayTMinKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6016)
    override val name: String = "OpRayQueryGetRayTMinKHR"
  }

  final case class OpRayQueryGetWorldRayDirectionKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6029)
    override val name: String = "OpRayQueryGetWorldRayDirectionKHR"
  }

  final case class OpRayQueryGetWorldRayOriginKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6030)
    override val name: String = "OpRayQueryGetWorldRayOriginKHR"
  }

  final case class OpRayQueryInitializeKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4473)
    override val name: String = "OpRayQueryInitializeKHR"
  }

  final case class OpRayQueryIsLSSHitNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5439)
    override val name: String = "OpRayQueryIsLSSHitNV"
  }

  final case class OpRayQueryIsSphereHitNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5438)
    override val name: String = "OpRayQueryIsSphereHitNV"
  }

  final case class OpRayQueryProceedKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4477)
    override val name: String = "OpRayQueryProceedKHR"
  }

  final case class OpRayQueryTerminateKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4474)
    override val name: String = "OpRayQueryTerminateKHR"
  }

  final case class OpReadClockKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5056)
    override val name: String = "OpReadClockKHR"
  }

  final case class OpReadPipe(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(274)
    override val name: String = "OpReadPipe"
  }

  final case class OpReadPipeBlockingALTERA(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5946)
    override val name: String = "OpReadPipeBlockingALTERA"
  }

  final case class OpReleaseEvent(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(298)
    override val name: String = "OpReleaseEvent"
  }

  final case class OpReorderThreadWithHintEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5314)
    override val name: String = "OpReorderThreadWithHintEXT"
  }

  final case class OpReorderThreadWithHintNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5280)
    override val name: String = "OpReorderThreadWithHintNV"
  }

  final case class OpReorderThreadWithHitObjectEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5315)
    override val name: String = "OpReorderThreadWithHitObjectEXT"
  }

  final case class OpReorderThreadWithHitObjectNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5279)
    override val name: String = "OpReorderThreadWithHitObjectNV"
  }

  final case class OpReportIntersectionKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5334)
    override val name: String = "OpReportIntersectionKHR"
  }

  final case class OpReserveReadPipePackets(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(278)
    override val name: String = "OpReserveReadPipePackets"
  }

  final case class OpReserveWritePipePackets(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(279)
    override val name: String = "OpReserveWritePipePackets"
  }

  final case class OpReservedReadPipe(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(276)
    override val name: String = "OpReservedReadPipe"
  }

  final case class OpReservedWritePipe(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(277)
    override val name: String = "OpReservedWritePipe"
  }

  final case class OpRestoreMemoryINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5820)
    override val name: String = "OpRestoreMemoryINTEL"
  }

  final case class OpRetainEvent(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(297)
    override val name: String = "OpRetainEvent"
  }

  final case class OpReturn(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(253)
    override val name: String = "OpReturn"
  }

  final case class OpReturnValue(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(254)
    override val name: String = "OpReturnValue"
  }

  final case class OpRoundFToTF32INTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6426)
    override val name: String = "OpRoundFToTF32INTEL"
  }

  final case class OpSConvert(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(114)
    override val name: String = "OpSConvert"
  }

  final case class OpSDiv(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(135)
    override val name: String = "OpSDiv"
  }

  final case class OpSDot(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4450)
    override val name: String = "OpSDot"
  }

  final case class OpSDotAccSat(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4453)
    override val name: String = "OpSDotAccSat"
  }

  final case class OpSGreaterThan(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(173)
    override val name: String = "OpSGreaterThan"
  }

  final case class OpSGreaterThanEqual(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(175)
    override val name: String = "OpSGreaterThanEqual"
  }

  final case class OpSLessThan(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(177)
    override val name: String = "OpSLessThan"
  }

  final case class OpSLessThanEqual(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(179)
    override val name: String = "OpSLessThanEqual"
  }

  final case class OpSMod(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(139)
    override val name: String = "OpSMod"
  }

  final case class OpSMulExtended(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(152)
    override val name: String = "OpSMulExtended"
  }

  final case class OpSNegate(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(126)
    override val name: String = "OpSNegate"
  }

  final case class OpSRem(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(138)
    override val name: String = "OpSRem"
  }

  final case class OpSUDot(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4452)
    override val name: String = "OpSUDot"
  }

  final case class OpSUDotAccSat(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4455)
    override val name: String = "OpSUDotAccSat"
  }

  final case class OpSampledImage(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(86)
    override val name: String = "OpSampledImage"
  }

  final case class OpSamplerImageAddressingModeNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5397)
    override val name: String = "OpSamplerImageAddressingModeNV"
  }

  final case class OpSatConvertSToU(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(118)
    override val name: String = "OpSatConvertSToU"
  }

  final case class OpSatConvertUToS(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(119)
    override val name: String = "OpSatConvertUToS"
  }

  final case class OpSaveMemoryINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5819)
    override val name: String = "OpSaveMemoryINTEL"
  }

  final case class OpSelect(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(169)
    override val name: String = "OpSelect"
  }

  final case class OpSelectionMerge(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(247)
    override val name: String = "OpSelectionMerge"
  }

  final case class OpSetMeshOutputsEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5295)
    override val name: String = "OpSetMeshOutputsEXT"
  }

  final case class OpSetUserEventStatus(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(301)
    override val name: String = "OpSetUserEventStatus"
  }

  final case class OpShiftLeftLogical(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(196)
    override val name: String = "OpShiftLeftLogical"
  }

  final case class OpShiftRightArithmetic(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(195)
    override val name: String = "OpShiftRightArithmetic"
  }

  final case class OpShiftRightLogical(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(194)
    override val name: String = "OpShiftRightLogical"
  }

  final case class OpSignBitSet(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(160)
    override val name: String = "OpSignBitSet"
  }

  final case class OpSizeOf(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(321)
    override val name: String = "OpSizeOf"
  }

  final case class OpSource(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(3)
    override val name: String = "OpSource"
  }

  final case class OpSourceContinued(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(2)
    override val name: String = "OpSourceContinued"
  }

  final case class OpSourceExtension(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4)
    override val name: String = "OpSourceExtension"
  }

  final case class OpSpecConstant(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(50)
    override val name: String = "OpSpecConstant"
  }

  final case class OpSpecConstantArchitectureINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6252)
    override val name: String = "OpSpecConstantArchitectureINTEL"
  }

  final case class OpSpecConstantCapabilitiesINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6253)
    override val name: String = "OpSpecConstantCapabilitiesINTEL"
  }

  final case class OpSpecConstantComposite(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(51)
    override val name: String = "OpSpecConstantComposite"
  }

  final case class OpSpecConstantCompositeContinuedINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6092)
    override val name: String = "OpSpecConstantCompositeContinuedINTEL"
  }

  final case class OpSpecConstantCompositeReplicateEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4462)
    override val name: String = "OpSpecConstantCompositeReplicateEXT"
  }

  final case class OpSpecConstantDataKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5148)
    override val name: String = "OpSpecConstantDataKHR"
  }

  final case class OpSpecConstantFalse(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(49)
    override val name: String = "OpSpecConstantFalse"
  }

  final case class OpSpecConstantOp(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(52)
    override val name: String = "OpSpecConstantOp"
  }

  final case class OpSpecConstantStringAMDX(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5104)
    override val name: String = "OpSpecConstantStringAMDX"
  }

  final case class OpSpecConstantTargetINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6251)
    override val name: String = "OpSpecConstantTargetINTEL"
  }

  final case class OpSpecConstantTrue(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(48)
    override val name: String = "OpSpecConstantTrue"
  }

  final case class OpStencilAttachmentReadEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4162)
    override val name: String = "OpStencilAttachmentReadEXT"
  }

  final case class OpStore(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(62)
    override val name: String = "OpStore"
  }

  final case class OpString(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(7)
    override val name: String = "OpString"
  }

  final case class OpSubgroup2DBlockLoadINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6231)
    override val name: String = "OpSubgroup2DBlockLoadINTEL"
  }

  final case class OpSubgroup2DBlockLoadTransformINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6232)
    override val name: String = "OpSubgroup2DBlockLoadTransformINTEL"
  }

  final case class OpSubgroup2DBlockLoadTransposeINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6233)
    override val name: String = "OpSubgroup2DBlockLoadTransposeINTEL"
  }

  final case class OpSubgroup2DBlockPrefetchINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6234)
    override val name: String = "OpSubgroup2DBlockPrefetchINTEL"
  }

  final case class OpSubgroup2DBlockStoreINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6235)
    override val name: String = "OpSubgroup2DBlockStoreINTEL"
  }

  final case class OpSubgroupAllEqualKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4430)
    override val name: String = "OpSubgroupAllEqualKHR"
  }

  final case class OpSubgroupAllKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4428)
    override val name: String = "OpSubgroupAllKHR"
  }

  final case class OpSubgroupAnyKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4429)
    override val name: String = "OpSubgroupAnyKHR"
  }

  final case class OpSubgroupAvcBmeInitializeINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5782)
    override val name: String = "OpSubgroupAvcBmeInitializeINTEL"
  }

  final case class OpSubgroupAvcFmeInitializeINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5781)
    override val name: String = "OpSubgroupAvcFmeInitializeINTEL"
  }

  final case class OpSubgroupAvcImeAdjustRefOffsetINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5751)
    override val name: String = "OpSubgroupAvcImeAdjustRefOffsetINTEL"
  }

  final case class OpSubgroupAvcImeConvertToMcePayloadINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5752)
    override val name: String = "OpSubgroupAvcImeConvertToMcePayloadINTEL"
  }

  final case class OpSubgroupAvcImeConvertToMceResultINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5765)
    override val name: String = "OpSubgroupAvcImeConvertToMceResultINTEL"
  }

  final case class OpSubgroupAvcImeEvaluateWithDualReferenceINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5758)
    override val name: String = "OpSubgroupAvcImeEvaluateWithDualReferenceINTEL"
  }

  final case class OpSubgroupAvcImeEvaluateWithDualReferenceStreaminINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5760)
    override val name: String = "OpSubgroupAvcImeEvaluateWithDualReferenceStreaminINTEL"
  }

  final case class OpSubgroupAvcImeEvaluateWithDualReferenceStreaminoutINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5764)
    override val name: String = "OpSubgroupAvcImeEvaluateWithDualReferenceStreaminoutINTEL"
  }

  final case class OpSubgroupAvcImeEvaluateWithDualReferenceStreamoutINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5762)
    override val name: String = "OpSubgroupAvcImeEvaluateWithDualReferenceStreamoutINTEL"
  }

  final case class OpSubgroupAvcImeEvaluateWithSingleReferenceINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5757)
    override val name: String = "OpSubgroupAvcImeEvaluateWithSingleReferenceINTEL"
  }

  final case class OpSubgroupAvcImeEvaluateWithSingleReferenceStreaminINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5759)
    override val name: String = "OpSubgroupAvcImeEvaluateWithSingleReferenceStreaminINTEL"
  }

  final case class OpSubgroupAvcImeEvaluateWithSingleReferenceStreaminoutINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5763)
    override val name: String = "OpSubgroupAvcImeEvaluateWithSingleReferenceStreaminoutINTEL"
  }

  final case class OpSubgroupAvcImeEvaluateWithSingleReferenceStreamoutINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5761)
    override val name: String = "OpSubgroupAvcImeEvaluateWithSingleReferenceStreamoutINTEL"
  }

  final case class OpSubgroupAvcImeGetBorderReachedINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5776)
    override val name: String = "OpSubgroupAvcImeGetBorderReachedINTEL"
  }

  final case class OpSubgroupAvcImeGetDualReferenceStreaminINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5767)
    override val name: String = "OpSubgroupAvcImeGetDualReferenceStreaminINTEL"
  }

  final case class OpSubgroupAvcImeGetSingleReferenceStreaminINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5766)
    override val name: String = "OpSubgroupAvcImeGetSingleReferenceStreaminINTEL"
  }

  final case class OpSubgroupAvcImeGetStreamoutDualReferenceMajorShapeDistortionsINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5774)
    override val name: String = "OpSubgroupAvcImeGetStreamoutDualReferenceMajorShapeDistortionsINTEL"
  }

  final case class OpSubgroupAvcImeGetStreamoutDualReferenceMajorShapeMotionVectorsINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5773)
    override val name: String = "OpSubgroupAvcImeGetStreamoutDualReferenceMajorShapeMotionVectorsINTEL"
  }

  final case class OpSubgroupAvcImeGetStreamoutDualReferenceMajorShapeReferenceIdsINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5775)
    override val name: String = "OpSubgroupAvcImeGetStreamoutDualReferenceMajorShapeReferenceIdsINTEL"
  }

  final case class OpSubgroupAvcImeGetStreamoutSingleReferenceMajorShapeDistortionsINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5771)
    override val name: String = "OpSubgroupAvcImeGetStreamoutSingleReferenceMajorShapeDistortionsINTEL"
  }

  final case class OpSubgroupAvcImeGetStreamoutSingleReferenceMajorShapeMotionVectorsINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5770)
    override val name: String = "OpSubgroupAvcImeGetStreamoutSingleReferenceMajorShapeMotionVectorsINTEL"
  }

  final case class OpSubgroupAvcImeGetStreamoutSingleReferenceMajorShapeReferenceIdsINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5772)
    override val name: String = "OpSubgroupAvcImeGetStreamoutSingleReferenceMajorShapeReferenceIdsINTEL"
  }

  final case class OpSubgroupAvcImeGetTruncatedSearchIndicationINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5777)
    override val name: String = "OpSubgroupAvcImeGetTruncatedSearchIndicationINTEL"
  }

  final case class OpSubgroupAvcImeGetUnidirectionalEarlySearchTerminationINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5778)
    override val name: String = "OpSubgroupAvcImeGetUnidirectionalEarlySearchTerminationINTEL"
  }

  final case class OpSubgroupAvcImeGetWeightingPatternMinimumDistortionINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5780)
    override val name: String = "OpSubgroupAvcImeGetWeightingPatternMinimumDistortionINTEL"
  }

  final case class OpSubgroupAvcImeGetWeightingPatternMinimumMotionVectorINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5779)
    override val name: String = "OpSubgroupAvcImeGetWeightingPatternMinimumMotionVectorINTEL"
  }

  final case class OpSubgroupAvcImeInitializeINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5747)
    override val name: String = "OpSubgroupAvcImeInitializeINTEL"
  }

  final case class OpSubgroupAvcImeRefWindowSizeINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5750)
    override val name: String = "OpSubgroupAvcImeRefWindowSizeINTEL"
  }

  final case class OpSubgroupAvcImeSetDualReferenceINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5749)
    override val name: String = "OpSubgroupAvcImeSetDualReferenceINTEL"
  }

  final case class OpSubgroupAvcImeSetEarlySearchTerminationThresholdINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5755)
    override val name: String = "OpSubgroupAvcImeSetEarlySearchTerminationThresholdINTEL"
  }

  final case class OpSubgroupAvcImeSetMaxMotionVectorCountINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5753)
    override val name: String = "OpSubgroupAvcImeSetMaxMotionVectorCountINTEL"
  }

  final case class OpSubgroupAvcImeSetSingleReferenceINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5748)
    override val name: String = "OpSubgroupAvcImeSetSingleReferenceINTEL"
  }

  final case class OpSubgroupAvcImeSetUnidirectionalMixDisableINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5754)
    override val name: String = "OpSubgroupAvcImeSetUnidirectionalMixDisableINTEL"
  }

  final case class OpSubgroupAvcImeSetWeightedSadINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5756)
    override val name: String = "OpSubgroupAvcImeSetWeightedSadINTEL"
  }

  final case class OpSubgroupAvcImeStripDualReferenceStreamoutINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5769)
    override val name: String = "OpSubgroupAvcImeStripDualReferenceStreamoutINTEL"
  }

  final case class OpSubgroupAvcImeStripSingleReferenceStreamoutINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5768)
    override val name: String = "OpSubgroupAvcImeStripSingleReferenceStreamoutINTEL"
  }

  final case class OpSubgroupAvcMceConvertToImePayloadINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5732)
    override val name: String = "OpSubgroupAvcMceConvertToImePayloadINTEL"
  }

  final case class OpSubgroupAvcMceConvertToImeResultINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5733)
    override val name: String = "OpSubgroupAvcMceConvertToImeResultINTEL"
  }

  final case class OpSubgroupAvcMceConvertToRefPayloadINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5734)
    override val name: String = "OpSubgroupAvcMceConvertToRefPayloadINTEL"
  }

  final case class OpSubgroupAvcMceConvertToRefResultINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5735)
    override val name: String = "OpSubgroupAvcMceConvertToRefResultINTEL"
  }

  final case class OpSubgroupAvcMceConvertToSicPayloadINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5736)
    override val name: String = "OpSubgroupAvcMceConvertToSicPayloadINTEL"
  }

  final case class OpSubgroupAvcMceConvertToSicResultINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5737)
    override val name: String = "OpSubgroupAvcMceConvertToSicResultINTEL"
  }

  final case class OpSubgroupAvcMceGetBestInterDistortionsINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5740)
    override val name: String = "OpSubgroupAvcMceGetBestInterDistortionsINTEL"
  }

  final case class OpSubgroupAvcMceGetDefaultHighPenaltyCostTableINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5721)
    override val name: String = "OpSubgroupAvcMceGetDefaultHighPenaltyCostTableINTEL"
  }

  final case class OpSubgroupAvcMceGetDefaultInterBaseMultiReferencePenaltyINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5713)
    override val name: String = "OpSubgroupAvcMceGetDefaultInterBaseMultiReferencePenaltyINTEL"
  }

  final case class OpSubgroupAvcMceGetDefaultInterDirectionPenaltyINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5717)
    override val name: String = "OpSubgroupAvcMceGetDefaultInterDirectionPenaltyINTEL"
  }

  final case class OpSubgroupAvcMceGetDefaultInterMotionVectorCostTableINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5720)
    override val name: String = "OpSubgroupAvcMceGetDefaultInterMotionVectorCostTableINTEL"
  }

  final case class OpSubgroupAvcMceGetDefaultInterShapePenaltyINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5715)
    override val name: String = "OpSubgroupAvcMceGetDefaultInterShapePenaltyINTEL"
  }

  final case class OpSubgroupAvcMceGetDefaultIntraChromaModeBasePenaltyINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5727)
    override val name: String = "OpSubgroupAvcMceGetDefaultIntraChromaModeBasePenaltyINTEL"
  }

  final case class OpSubgroupAvcMceGetDefaultIntraLumaModePenaltyINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5725)
    override val name: String = "OpSubgroupAvcMceGetDefaultIntraLumaModePenaltyINTEL"
  }

  final case class OpSubgroupAvcMceGetDefaultIntraLumaShapePenaltyINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5719)
    override val name: String = "OpSubgroupAvcMceGetDefaultIntraLumaShapePenaltyINTEL"
  }

  final case class OpSubgroupAvcMceGetDefaultLowPenaltyCostTableINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5723)
    override val name: String = "OpSubgroupAvcMceGetDefaultLowPenaltyCostTableINTEL"
  }

  final case class OpSubgroupAvcMceGetDefaultMediumPenaltyCostTableINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5722)
    override val name: String = "OpSubgroupAvcMceGetDefaultMediumPenaltyCostTableINTEL"
  }

  final case class OpSubgroupAvcMceGetDefaultNonDcLumaIntraPenaltyINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5726)
    override val name: String = "OpSubgroupAvcMceGetDefaultNonDcLumaIntraPenaltyINTEL"
  }

  final case class OpSubgroupAvcMceGetInterDirectionsINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5743)
    override val name: String = "OpSubgroupAvcMceGetInterDirectionsINTEL"
  }

  final case class OpSubgroupAvcMceGetInterDistortionsINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5739)
    override val name: String = "OpSubgroupAvcMceGetInterDistortionsINTEL"
  }

  final case class OpSubgroupAvcMceGetInterMajorShapeINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5741)
    override val name: String = "OpSubgroupAvcMceGetInterMajorShapeINTEL"
  }

  final case class OpSubgroupAvcMceGetInterMinorShapeINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5742)
    override val name: String = "OpSubgroupAvcMceGetInterMinorShapeINTEL"
  }

  final case class OpSubgroupAvcMceGetInterMotionVectorCountINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5744)
    override val name: String = "OpSubgroupAvcMceGetInterMotionVectorCountINTEL"
  }

  final case class OpSubgroupAvcMceGetInterReferenceIdsINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5745)
    override val name: String = "OpSubgroupAvcMceGetInterReferenceIdsINTEL"
  }

  final case class OpSubgroupAvcMceGetInterReferenceInterlacedFieldPolaritiesINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5746)
    override val name: String = "OpSubgroupAvcMceGetInterReferenceInterlacedFieldPolaritiesINTEL"
  }

  final case class OpSubgroupAvcMceGetMotionVectorsINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5738)
    override val name: String = "OpSubgroupAvcMceGetMotionVectorsINTEL"
  }

  final case class OpSubgroupAvcMceSetAcOnlyHaarINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5728)
    override val name: String = "OpSubgroupAvcMceSetAcOnlyHaarINTEL"
  }

  final case class OpSubgroupAvcMceSetDualReferenceInterlacedFieldPolaritiesINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5731)
    override val name: String = "OpSubgroupAvcMceSetDualReferenceInterlacedFieldPolaritiesINTEL"
  }

  final case class OpSubgroupAvcMceSetInterBaseMultiReferencePenaltyINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5714)
    override val name: String = "OpSubgroupAvcMceSetInterBaseMultiReferencePenaltyINTEL"
  }

  final case class OpSubgroupAvcMceSetInterDirectionPenaltyINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5718)
    override val name: String = "OpSubgroupAvcMceSetInterDirectionPenaltyINTEL"
  }

  final case class OpSubgroupAvcMceSetInterShapePenaltyINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5716)
    override val name: String = "OpSubgroupAvcMceSetInterShapePenaltyINTEL"
  }

  final case class OpSubgroupAvcMceSetMotionVectorCostFunctionINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5724)
    override val name: String = "OpSubgroupAvcMceSetMotionVectorCostFunctionINTEL"
  }

  final case class OpSubgroupAvcMceSetSingleReferenceInterlacedFieldPolarityINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5730)
    override val name: String = "OpSubgroupAvcMceSetSingleReferenceInterlacedFieldPolarityINTEL"
  }

  final case class OpSubgroupAvcMceSetSourceInterlacedFieldPolarityINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5729)
    override val name: String = "OpSubgroupAvcMceSetSourceInterlacedFieldPolarityINTEL"
  }

  final case class OpSubgroupAvcRefConvertToMcePayloadINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5783)
    override val name: String = "OpSubgroupAvcRefConvertToMcePayloadINTEL"
  }

  final case class OpSubgroupAvcRefConvertToMceResultINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5790)
    override val name: String = "OpSubgroupAvcRefConvertToMceResultINTEL"
  }

  final case class OpSubgroupAvcRefEvaluateWithDualReferenceINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5787)
    override val name: String = "OpSubgroupAvcRefEvaluateWithDualReferenceINTEL"
  }

  final case class OpSubgroupAvcRefEvaluateWithMultiReferenceINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5788)
    override val name: String = "OpSubgroupAvcRefEvaluateWithMultiReferenceINTEL"
  }

  final case class OpSubgroupAvcRefEvaluateWithMultiReferenceInterlacedINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5789)
    override val name: String = "OpSubgroupAvcRefEvaluateWithMultiReferenceInterlacedINTEL"
  }

  final case class OpSubgroupAvcRefEvaluateWithSingleReferenceINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5786)
    override val name: String = "OpSubgroupAvcRefEvaluateWithSingleReferenceINTEL"
  }

  final case class OpSubgroupAvcRefSetBidirectionalMixDisableINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5784)
    override val name: String = "OpSubgroupAvcRefSetBidirectionalMixDisableINTEL"
  }

  final case class OpSubgroupAvcRefSetBilinearFilterEnableINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5785)
    override val name: String = "OpSubgroupAvcRefSetBilinearFilterEnableINTEL"
  }

  final case class OpSubgroupAvcSicConfigureIpeLumaChromaINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5794)
    override val name: String = "OpSubgroupAvcSicConfigureIpeLumaChromaINTEL"
  }

  final case class OpSubgroupAvcSicConfigureIpeLumaINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5793)
    override val name: String = "OpSubgroupAvcSicConfigureIpeLumaINTEL"
  }

  final case class OpSubgroupAvcSicConfigureSkcINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5792)
    override val name: String = "OpSubgroupAvcSicConfigureSkcINTEL"
  }

  final case class OpSubgroupAvcSicConvertToMcePayloadINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5796)
    override val name: String = "OpSubgroupAvcSicConvertToMcePayloadINTEL"
  }

  final case class OpSubgroupAvcSicConvertToMceResultINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5808)
    override val name: String = "OpSubgroupAvcSicConvertToMceResultINTEL"
  }

  final case class OpSubgroupAvcSicEvaluateIpeINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5803)
    override val name: String = "OpSubgroupAvcSicEvaluateIpeINTEL"
  }

  final case class OpSubgroupAvcSicEvaluateWithDualReferenceINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5805)
    override val name: String = "OpSubgroupAvcSicEvaluateWithDualReferenceINTEL"
  }

  final case class OpSubgroupAvcSicEvaluateWithMultiReferenceINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5806)
    override val name: String = "OpSubgroupAvcSicEvaluateWithMultiReferenceINTEL"
  }

  final case class OpSubgroupAvcSicEvaluateWithMultiReferenceInterlacedINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5807)
    override val name: String = "OpSubgroupAvcSicEvaluateWithMultiReferenceInterlacedINTEL"
  }

  final case class OpSubgroupAvcSicEvaluateWithSingleReferenceINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5804)
    override val name: String = "OpSubgroupAvcSicEvaluateWithSingleReferenceINTEL"
  }

  final case class OpSubgroupAvcSicGetBestIpeChromaDistortionINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5811)
    override val name: String = "OpSubgroupAvcSicGetBestIpeChromaDistortionINTEL"
  }

  final case class OpSubgroupAvcSicGetBestIpeLumaDistortionINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5810)
    override val name: String = "OpSubgroupAvcSicGetBestIpeLumaDistortionINTEL"
  }

  final case class OpSubgroupAvcSicGetInterRawSadsINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5816)
    override val name: String = "OpSubgroupAvcSicGetInterRawSadsINTEL"
  }

  final case class OpSubgroupAvcSicGetIpeChromaModeINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5813)
    override val name: String = "OpSubgroupAvcSicGetIpeChromaModeINTEL"
  }

  final case class OpSubgroupAvcSicGetIpeLumaShapeINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5809)
    override val name: String = "OpSubgroupAvcSicGetIpeLumaShapeINTEL"
  }

  final case class OpSubgroupAvcSicGetMotionVectorMaskINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5795)
    override val name: String = "OpSubgroupAvcSicGetMotionVectorMaskINTEL"
  }

  final case class OpSubgroupAvcSicGetPackedIpeLumaModesINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5812)
    override val name: String = "OpSubgroupAvcSicGetPackedIpeLumaModesINTEL"
  }

  final case class OpSubgroupAvcSicGetPackedSkcLumaCountThresholdINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5814)
    override val name: String = "OpSubgroupAvcSicGetPackedSkcLumaCountThresholdINTEL"
  }

  final case class OpSubgroupAvcSicGetPackedSkcLumaSumThresholdINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5815)
    override val name: String = "OpSubgroupAvcSicGetPackedSkcLumaSumThresholdINTEL"
  }

  final case class OpSubgroupAvcSicInitializeINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5791)
    override val name: String = "OpSubgroupAvcSicInitializeINTEL"
  }

  final case class OpSubgroupAvcSicSetBilinearFilterEnableINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5800)
    override val name: String = "OpSubgroupAvcSicSetBilinearFilterEnableINTEL"
  }

  final case class OpSubgroupAvcSicSetBlockBasedRawSkipSadINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5802)
    override val name: String = "OpSubgroupAvcSicSetBlockBasedRawSkipSadINTEL"
  }

  final case class OpSubgroupAvcSicSetIntraChromaModeCostFunctionINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5799)
    override val name: String = "OpSubgroupAvcSicSetIntraChromaModeCostFunctionINTEL"
  }

  final case class OpSubgroupAvcSicSetIntraLumaModeCostFunctionINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5798)
    override val name: String = "OpSubgroupAvcSicSetIntraLumaModeCostFunctionINTEL"
  }

  final case class OpSubgroupAvcSicSetIntraLumaShapePenaltyINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5797)
    override val name: String = "OpSubgroupAvcSicSetIntraLumaShapePenaltyINTEL"
  }

  final case class OpSubgroupAvcSicSetSkcForwardTransformEnableINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5801)
    override val name: String = "OpSubgroupAvcSicSetSkcForwardTransformEnableINTEL"
  }

  final case class OpSubgroupBallotKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4421)
    override val name: String = "OpSubgroupBallotKHR"
  }

  final case class OpSubgroupBlockPrefetchINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6221)
    override val name: String = "OpSubgroupBlockPrefetchINTEL"
  }

  final case class OpSubgroupBlockReadINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5575)
    override val name: String = "OpSubgroupBlockReadINTEL"
  }

  final case class OpSubgroupBlockWriteINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5576)
    override val name: String = "OpSubgroupBlockWriteINTEL"
  }

  final case class OpSubgroupFirstInvocationKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4422)
    override val name: String = "OpSubgroupFirstInvocationKHR"
  }

  final case class OpSubgroupImageBlockReadINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5577)
    override val name: String = "OpSubgroupImageBlockReadINTEL"
  }

  final case class OpSubgroupImageBlockWriteINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5578)
    override val name: String = "OpSubgroupImageBlockWriteINTEL"
  }

  final case class OpSubgroupImageMediaBlockReadINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5580)
    override val name: String = "OpSubgroupImageMediaBlockReadINTEL"
  }

  final case class OpSubgroupImageMediaBlockWriteINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5581)
    override val name: String = "OpSubgroupImageMediaBlockWriteINTEL"
  }

  final case class OpSubgroupMatrixMultiplyAccumulateINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6237)
    override val name: String = "OpSubgroupMatrixMultiplyAccumulateINTEL"
  }

  final case class OpSubgroupReadInvocationKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4432)
    override val name: String = "OpSubgroupReadInvocationKHR"
  }

  final case class OpSubgroupShuffleDownINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5572)
    override val name: String = "OpSubgroupShuffleDownINTEL"
  }

  final case class OpSubgroupShuffleINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5571)
    override val name: String = "OpSubgroupShuffleINTEL"
  }

  final case class OpSubgroupShuffleUpINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5573)
    override val name: String = "OpSubgroupShuffleUpINTEL"
  }

  final case class OpSubgroupShuffleXorINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5574)
    override val name: String = "OpSubgroupShuffleXorINTEL"
  }

  final case class OpSwitch(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(251)
    override val name: String = "OpSwitch"
  }

  final case class OpTaskSequenceAsyncALTERA(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6164)
    override val name: String = "OpTaskSequenceAsyncALTERA"
  }

  final case class OpTaskSequenceCreateALTERA(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6163)
    override val name: String = "OpTaskSequenceCreateALTERA"
  }

  final case class OpTaskSequenceGetALTERA(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6165)
    override val name: String = "OpTaskSequenceGetALTERA"
  }

  final case class OpTaskSequenceReleaseALTERA(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6166)
    override val name: String = "OpTaskSequenceReleaseALTERA"
  }

  final case class OpTensorLayoutSetBlockSizeNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5384)
    override val name: String = "OpTensorLayoutSetBlockSizeNV"
  }

  final case class OpTensorLayoutSetClampValueNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5376)
    override val name: String = "OpTensorLayoutSetClampValueNV"
  }

  final case class OpTensorLayoutSetDimensionNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5373)
    override val name: String = "OpTensorLayoutSetDimensionNV"
  }

  final case class OpTensorLayoutSetStrideNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5374)
    override val name: String = "OpTensorLayoutSetStrideNV"
  }

  final case class OpTensorLayoutSliceNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5375)
    override val name: String = "OpTensorLayoutSliceNV"
  }

  final case class OpTensorQuerySizeARM(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4166)
    override val name: String = "OpTensorQuerySizeARM"
  }

  final case class OpTensorReadARM(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4164)
    override val name: String = "OpTensorReadARM"
  }

  final case class OpTensorViewSetClipNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5382)
    override val name: String = "OpTensorViewSetClipNV"
  }

  final case class OpTensorViewSetDimensionNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5378)
    override val name: String = "OpTensorViewSetDimensionNV"
  }

  final case class OpTensorViewSetStrideNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5379)
    override val name: String = "OpTensorViewSetStrideNV"
  }

  final case class OpTensorWriteARM(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4165)
    override val name: String = "OpTensorWriteARM"
  }

  final case class OpTerminateInvocation(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4416)
    override val name: String = "OpTerminateInvocation"
  }

  final case class OpTerminateRayKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4449)
    override val name: String = "OpTerminateRayKHR"
  }

  final case class OpTerminateRayNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5336)
    override val name: String = "OpTerminateRayNV"
  }

  final case class OpTraceMotionNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5338)
    override val name: String = "OpTraceMotionNV"
  }

  final case class OpTraceNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5337)
    override val name: String = "OpTraceNV"
  }

  final case class OpTraceRayKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4445)
    override val name: String = "OpTraceRayKHR"
  }

  final case class OpTraceRayMotionNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5339)
    override val name: String = "OpTraceRayMotionNV"
  }

  final case class OpTranspose(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(84)
    override val name: String = "OpTranspose"
  }

  final case class OpTypeAccelerationStructureKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5341)
    override val name: String = "OpTypeAccelerationStructureKHR"
  }

  final case class OpTypeArray(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(28)
    override val name: String = "OpTypeArray"
  }

  final case class OpTypeAvcImeDualReferenceStreaminINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5710)
    override val name: String = "OpTypeAvcImeDualReferenceStreaminINTEL"
  }

  final case class OpTypeAvcImePayloadINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5701)
    override val name: String = "OpTypeAvcImePayloadINTEL"
  }

  final case class OpTypeAvcImeResultDualReferenceStreamoutINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5708)
    override val name: String = "OpTypeAvcImeResultDualReferenceStreamoutINTEL"
  }

  final case class OpTypeAvcImeResultINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5706)
    override val name: String = "OpTypeAvcImeResultINTEL"
  }

  final case class OpTypeAvcImeResultSingleReferenceStreamoutINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5707)
    override val name: String = "OpTypeAvcImeResultSingleReferenceStreamoutINTEL"
  }

  final case class OpTypeAvcImeSingleReferenceStreaminINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5709)
    override val name: String = "OpTypeAvcImeSingleReferenceStreaminINTEL"
  }

  final case class OpTypeAvcMcePayloadINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5704)
    override val name: String = "OpTypeAvcMcePayloadINTEL"
  }

  final case class OpTypeAvcMceResultINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5705)
    override val name: String = "OpTypeAvcMceResultINTEL"
  }

  final case class OpTypeAvcRefPayloadINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5702)
    override val name: String = "OpTypeAvcRefPayloadINTEL"
  }

  final case class OpTypeAvcRefResultINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5711)
    override val name: String = "OpTypeAvcRefResultINTEL"
  }

  final case class OpTypeAvcSicPayloadINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5703)
    override val name: String = "OpTypeAvcSicPayloadINTEL"
  }

  final case class OpTypeAvcSicResultINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5712)
    override val name: String = "OpTypeAvcSicResultINTEL"
  }

  final case class OpTypeBool(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(20)
    override val name: String = "OpTypeBool"
  }

  final case class OpTypeBufferEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5115)
    override val name: String = "OpTypeBufferEXT"
  }

  final case class OpTypeBufferSurfaceINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6086)
    override val name: String = "OpTypeBufferSurfaceINTEL"
  }

  final case class OpTypeCooperativeMatrixKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4456)
    override val name: String = "OpTypeCooperativeMatrixKHR"
  }

  final case class OpTypeCooperativeMatrixNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5358)
    override val name: String = "OpTypeCooperativeMatrixNV"
  }

  final case class OpTypeDeviceEvent(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(35)
    override val name: String = "OpTypeDeviceEvent"
  }

  final case class OpTypeEvent(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(34)
    override val name: String = "OpTypeEvent"
  }

  final case class OpTypeFloat(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(22)
    override val name: String = "OpTypeFloat"
  }

  final case class OpTypeForwardPointer(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(39)
    override val name: String = "OpTypeForwardPointer"
  }

  final case class OpTypeFunction(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(33)
    override val name: String = "OpTypeFunction"
  }

  final case class OpTypeGraphARM(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4190)
    override val name: String = "OpTypeGraphARM"
  }

  final case class OpTypeHitObjectEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5313)
    override val name: String = "OpTypeHitObjectEXT"
  }

  final case class OpTypeHitObjectNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5281)
    override val name: String = "OpTypeHitObjectNV"
  }

  final case class OpTypeImage(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(25)
    override val name: String = "OpTypeImage"
  }

  final case class OpTypeInt(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(21)
    override val name: String = "OpTypeInt"
  }

  final case class OpTypeMatrix(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(24)
    override val name: String = "OpTypeMatrix"
  }

  final case class OpTypeNamedBarrier(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(327)
    override val name: String = "OpTypeNamedBarrier"
  }

  final case class OpTypeNodePayloadArrayAMDX(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5076)
    override val name: String = "OpTypeNodePayloadArrayAMDX"
  }

  final case class OpTypeOpaque(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(31)
    override val name: String = "OpTypeOpaque"
  }

  final case class OpTypePipe(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(38)
    override val name: String = "OpTypePipe"
  }

  final case class OpTypePipeStorage(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(322)
    override val name: String = "OpTypePipeStorage"
  }

  final case class OpTypePointer(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(32)
    override val name: String = "OpTypePointer"
  }

  final case class OpTypeQueue(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(37)
    override val name: String = "OpTypeQueue"
  }

  final case class OpTypeRayQueryKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4472)
    override val name: String = "OpTypeRayQueryKHR"
  }

  final case class OpTypeReserveId(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(36)
    override val name: String = "OpTypeReserveId"
  }

  final case class OpTypeRuntimeArray(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(29)
    override val name: String = "OpTypeRuntimeArray"
  }

  final case class OpTypeSampledImage(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(27)
    override val name: String = "OpTypeSampledImage"
  }

  final case class OpTypeSampler(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(26)
    override val name: String = "OpTypeSampler"
  }

  final case class OpTypeStruct(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(30)
    override val name: String = "OpTypeStruct"
  }

  final case class OpTypeStructContinuedINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6090)
    override val name: String = "OpTypeStructContinuedINTEL"
  }

  final case class OpTypeTaskSequenceALTERA(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6199)
    override val name: String = "OpTypeTaskSequenceALTERA"
  }

  final case class OpTypeTensorARM(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4163)
    override val name: String = "OpTypeTensorARM"
  }

  final case class OpTypeTensorLayoutNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5370)
    override val name: String = "OpTypeTensorLayoutNV"
  }

  final case class OpTypeTensorViewNV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5371)
    override val name: String = "OpTypeTensorViewNV"
  }

  final case class OpTypeUntypedPointerKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4417)
    override val name: String = "OpTypeUntypedPointerKHR"
  }

  final case class OpTypeVector(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(23)
    override val name: String = "OpTypeVector"
  }

  final case class OpTypeVectorIdEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5288)
    override val name: String = "OpTypeVectorIdEXT"
  }

  final case class OpTypeVmeImageINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5700)
    override val name: String = "OpTypeVmeImageINTEL"
  }

  final case class OpTypeVoid(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(19)
    override val name: String = "OpTypeVoid"
  }

  final case class OpUAddSatINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5590)
    override val name: String = "OpUAddSatINTEL"
  }

  final case class OpUAverageINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5592)
    override val name: String = "OpUAverageINTEL"
  }

  final case class OpUAverageRoundedINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5594)
    override val name: String = "OpUAverageRoundedINTEL"
  }

  final case class OpUConvert(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(113)
    override val name: String = "OpUConvert"
  }

  final case class OpUCountLeadingZerosINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5585)
    override val name: String = "OpUCountLeadingZerosINTEL"
  }

  final case class OpUCountTrailingZerosINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5586)
    override val name: String = "OpUCountTrailingZerosINTEL"
  }

  final case class OpUDiv(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(134)
    override val name: String = "OpUDiv"
  }

  final case class OpUDot(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4451)
    override val name: String = "OpUDot"
  }

  final case class OpUDotAccSat(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4454)
    override val name: String = "OpUDotAccSat"
  }

  final case class OpUGreaterThan(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(172)
    override val name: String = "OpUGreaterThan"
  }

  final case class OpUGreaterThanEqual(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(174)
    override val name: String = "OpUGreaterThanEqual"
  }

  final case class OpULessThan(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(176)
    override val name: String = "OpULessThan"
  }

  final case class OpULessThanEqual(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(178)
    override val name: String = "OpULessThanEqual"
  }

  final case class OpUMod(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(137)
    override val name: String = "OpUMod"
  }

  final case class OpUMul32x16INTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5598)
    override val name: String = "OpUMul32x16INTEL"
  }

  final case class OpUMulExtended(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(151)
    override val name: String = "OpUMulExtended"
  }

  final case class OpUSubSatINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5596)
    override val name: String = "OpUSubSatINTEL"
  }

  final case class OpUndef(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(1)
    override val name: String = "OpUndef"
  }

  final case class OpUnordered(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(163)
    override val name: String = "OpUnordered"
  }

  final case class OpUnreachable(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(255)
    override val name: String = "OpUnreachable"
  }

  final case class OpUntypedAccessChainKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4419)
    override val name: String = "OpUntypedAccessChainKHR"
  }

  final case class OpUntypedArrayLengthKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4425)
    override val name: String = "OpUntypedArrayLengthKHR"
  }

  final case class OpUntypedGroupAsyncCopyKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4434)
    override val name: String = "OpUntypedGroupAsyncCopyKHR"
  }

  final case class OpUntypedImageTexelPointerEXT(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5126)
    override val name: String = "OpUntypedImageTexelPointerEXT"
  }

  final case class OpUntypedInBoundsAccessChainKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4420)
    override val name: String = "OpUntypedInBoundsAccessChainKHR"
  }

  final case class OpUntypedInBoundsPtrAccessChainKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4424)
    override val name: String = "OpUntypedInBoundsPtrAccessChainKHR"
  }

  final case class OpUntypedPrefetchKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4426)
    override val name: String = "OpUntypedPrefetchKHR"
  }

  final case class OpUntypedPtrAccessChainKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4423)
    override val name: String = "OpUntypedPtrAccessChainKHR"
  }

  final case class OpUntypedVariableKHR(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(4418)
    override val name: String = "OpUntypedVariableKHR"
  }

  final case class OpUntypedVariableLengthArrayINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(6244)
    override val name: String = "OpUntypedVariableLengthArrayINTEL"
  }

  final case class OpVariable(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(59)
    override val name: String = "OpVariable"
  }

  final case class OpVariableLengthArrayINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5818)
    override val name: String = "OpVariableLengthArrayINTEL"
  }

  final case class OpVectorExtractDynamic(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(77)
    override val name: String = "OpVectorExtractDynamic"
  }

  final case class OpVectorInsertDynamic(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(78)
    override val name: String = "OpVectorInsertDynamic"
  }

  final case class OpVectorShuffle(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(79)
    override val name: String = "OpVectorShuffle"
  }

  final case class OpVectorTimesMatrix(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(144)
    override val name: String = "OpVectorTimesMatrix"
  }

  final case class OpVectorTimesScalar(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(142)
    override val name: String = "OpVectorTimesScalar"
  }

  final case class OpVmeImageINTEL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5699)
    override val name: String = "OpVmeImageINTEL"
  }

  final case class OpWritePackedPrimitiveIndices4x8NV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5299)
    override val name: String = "OpWritePackedPrimitiveIndices4x8NV"
  }

  final case class OpWritePipe(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(275)
    override val name: String = "OpWritePipe"
  }

  final case class OpWritePipeBlockingALTERA(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe(5947)
    override val name: String = "OpWritePipeBlockingALTERA"
  }

  private def decode0(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "11" => Some(OpExtInstImport(arguments))
      case "118" => Some(OpSatConvertSToU(arguments))
      case "132" => Some(OpIMul(arguments))
      case "202" => Some(OpBitFieldSExtract(arguments))
      case "279" => Some(OpReserveWritePipePackets(arguments))
      case "286" => Some(OpGroupReserveWritePipePackets(arguments))
      case "293" => Some(OpGetKernelNDrangeSubGroupCount(arguments))
      case "349" => Some(OpGroupNonUniformIAdd(arguments))
      case "356" => Some(OpGroupNonUniformSMax(arguments))
      case "363" => Some(OpGroupNonUniformLogicalOr(arguments))
      case "4422" => Some(OpSubgroupFirstInvocationKHR(arguments))
      case "5115" => Some(OpTypeBufferEXT(arguments))
      case "5269" => Some(OpHitObjectGetGeometryIndexNV(arguments))
      case "5276" => Some(OpHitObjectIsEmptyNV(arguments))
      case "5283" => Some(OpImageSampleFootprintNV(arguments))
      case "5290" => Some(OpCooperativeVectorOuterProductAccumulateNV(arguments))
      case "5339" => Some(OpTraceRayMotionNV(arguments))
      case "5346" => Some(OpHitObjectGetClusterIdNV(arguments))
      case "5360" => Some(OpCooperativeMatrixStoreNV(arguments))
      case "5430" => Some(OpRayQueryGetIntersectionLSSRadiiNV(arguments))
      case "5577" => Some(OpSubgroupImageBlockReadINTEL(arguments))
      case "5591" => Some(OpIAverageINTEL(arguments))
      case "5717" => Some(OpSubgroupAvcMceGetDefaultInterDirectionPenaltyINTEL(arguments))
      case "5724" => Some(OpSubgroupAvcMceSetMotionVectorCostFunctionINTEL(arguments))
      case "5731" => Some(OpSubgroupAvcMceSetDualReferenceInterlacedFieldPolaritiesINTEL(arguments))
      case "5801" => Some(OpSubgroupAvcSicSetSkcForwardTransformEnableINTEL(arguments))
      case "5878" => Some(OpArbitraryFloatATanPiINTEL(arguments))
      case "6116" => Some(OpConvertFToBF16INTEL(arguments))
      case "8" => Some(OpLine(arguments))
      case "88" => Some(OpImageSampleExplicitLod(arguments))
      case "95" => Some(OpImageFetch(arguments))
      case _ => None
    }

  private def decode1(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "119" => Some(OpSatConvertUToS(arguments))
      case "12" => Some(OpExtInst(arguments))
      case "126" => Some(OpSNegate(arguments))
      case "133" => Some(OpFMul(arguments))
      case "140" => Some(OpFRem(arguments))
      case "203" => Some(OpBitFieldUExtract(arguments))
      case "210" => Some(OpDPdxFine(arguments))
      case "287" => Some(OpGroupCommitReadPipe(arguments))
      case "294" => Some(OpGetKernelNDrangeMaxSubGroupSize(arguments))
      case "357" => Some(OpGroupNonUniformUMax(arguments))
      case "364" => Some(OpGroupNonUniformLogicalXor(arguments))
      case "4416" => Some(OpTerminateInvocation(arguments))
      case "4423" => Some(OpUntypedPtrAccessChainKHR(arguments))
      case "4430" => Some(OpSubgroupAllEqualKHR(arguments))
      case "4500" => Some(OpImageBlockMatchWindowSSDQCOM(arguments))
      case "5277" => Some(OpHitObjectIsHitNV(arguments))
      case "5291" => Some(OpCooperativeVectorReduceSumAccumulateNV(arguments))
      case "5347" => Some(OpHitObjectGetRayTMinEXT(arguments))
      case "5361" => Some(OpCooperativeMatrixMulAddNV(arguments))
      case "5431" => Some(OpRayQueryGetIntersectionLSSHitValueNV(arguments))
      case "5578" => Some(OpSubgroupImageBlockWriteINTEL(arguments))
      case "5585" => Some(OpUCountLeadingZerosINTEL(arguments))
      case "5592" => Some(OpUAverageINTEL(arguments))
      case "5718" => Some(OpSubgroupAvcMceSetInterDirectionPenaltyINTEL(arguments))
      case "5725" => Some(OpSubgroupAvcMceGetDefaultIntraLumaModePenaltyINTEL(arguments))
      case "5732" => Some(OpSubgroupAvcMceConvertToImePayloadINTEL(arguments))
      case "5802" => Some(OpSubgroupAvcSicSetBlockBasedRawSkipSadINTEL(arguments))
      case "5879" => Some(OpArbitraryFloatATan2INTEL(arguments))
      case "5949" => Some(OpFPGARegALTERA(arguments))
      case "6117" => Some(OpConvertBF16ToFINTEL(arguments))
      case "89" => Some(OpImageSampleDrefImplicitLod(arguments))
      case "96" => Some(OpImageGather(arguments))
      case _ => None
    }

  private def decode2(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "127" => Some(OpFNegate(arguments))
      case "134" => Some(OpUDiv(arguments))
      case "141" => Some(OpFMod(arguments))
      case "20" => Some(OpTypeBool(arguments))
      case "204" => Some(OpBitReverse(arguments))
      case "211" => Some(OpDPdyFine(arguments))
      case "288" => Some(OpGroupCommitWritePipe(arguments))
      case "295" => Some(OpGetKernelWorkGroupSize(arguments))
      case "358" => Some(OpGroupNonUniformFMax(arguments))
      case "365" => Some(OpGroupNonUniformQuadBroadcast(arguments))
      case "4417" => Some(OpTypeUntypedPointerKHR(arguments))
      case "4424" => Some(OpUntypedInBoundsPtrAccessChainKHR(arguments))
      case "4431" => Some(OpGroupNonUniformRotateKHR(arguments))
      case "4501" => Some(OpImageBlockMatchWindowSADQCOM(arguments))
      case "5278" => Some(OpHitObjectIsMissNV(arguments))
      case "5292" => Some(OpCooperativeVectorMatrixMulAddNV(arguments))
      case "5348" => Some(OpHitObjectGetShaderBindingTableRecordIndexEXT(arguments))
      case "5362" => Some(OpCooperativeMatrixLengthNV(arguments))
      case "5432" => Some(OpHitObjectGetSpherePositionNV(arguments))
      case "5586" => Some(OpUCountTrailingZerosINTEL(arguments))
      case "5593" => Some(OpIAverageRoundedINTEL(arguments))
      case "5719" => Some(OpSubgroupAvcMceGetDefaultIntraLumaShapePenaltyINTEL(arguments))
      case "5726" => Some(OpSubgroupAvcMceGetDefaultNonDcLumaIntraPenaltyINTEL(arguments))
      case "5733" => Some(OpSubgroupAvcMceConvertToImeResultINTEL(arguments))
      case "5740" => Some(OpSubgroupAvcMceGetBestInterDistortionsINTEL(arguments))
      case "5803" => Some(OpSubgroupAvcSicEvaluateIpeINTEL(arguments))
      case "5810" => Some(OpSubgroupAvcSicGetBestIpeLumaDistortionINTEL(arguments))
      case "5887" => Some(OpLoopControlINTEL(arguments))
      case "6426" => Some(OpRoundFToTF32INTEL(arguments))
      case "97" => Some(OpImageDrefGather(arguments))
      case _ => None
    }

  private def decode3(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "128" => Some(OpIAdd(arguments))
      case "135" => Some(OpSDiv(arguments))
      case "14" => Some(OpMemoryModel(arguments))
      case "142" => Some(OpVectorTimesScalar(arguments))
      case "205" => Some(OpBitCount(arguments))
      case "21" => Some(OpTypeInt(arguments))
      case "212" => Some(OpFwidthFine(arguments))
      case "296" => Some(OpGetKernelPreferredWorkGroupSizeMultiple(arguments))
      case "359" => Some(OpGroupNonUniformBitwiseAnd(arguments))
      case "366" => Some(OpGroupNonUniformQuadSwap(arguments))
      case "4418" => Some(OpUntypedVariableKHR(arguments))
      case "4425" => Some(OpUntypedArrayLengthKHR(arguments))
      case "4432" => Some(OpSubgroupReadInvocationKHR(arguments))
      case "4502" => Some(OpImageBlockMatchGatherSSDQCOM(arguments))
      case "5279" => Some(OpReorderThreadWithHitObjectNV(arguments))
      case "5293" => Some(OpCooperativeMatrixConvertNV(arguments))
      case "5349" => Some(OpHitObjectGetShaderRecordBufferHandleEXT(arguments))
      case "5370" => Some(OpTypeTensorLayoutNV(arguments))
      case "5433" => Some(OpHitObjectGetSphereRadiusNV(arguments))
      case "5587" => Some(OpAbsISubINTEL(arguments))
      case "5594" => Some(OpUAverageRoundedINTEL(arguments))
      case "5727" => Some(OpSubgroupAvcMceGetDefaultIntraChromaModeBasePenaltyINTEL(arguments))
      case "5734" => Some(OpSubgroupAvcMceConvertToRefPayloadINTEL(arguments))
      case "5741" => Some(OpSubgroupAvcMceGetInterMajorShapeINTEL(arguments))
      case "5804" => Some(OpSubgroupAvcSicEvaluateWithSingleReferenceINTEL(arguments))
      case "5811" => Some(OpSubgroupAvcSicGetBestIpeChromaDistortionINTEL(arguments))
      case "98" => Some(OpImageRead(arguments))
      case _ => None
    }

  private def decode4(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "129" => Some(OpFAdd(arguments))
      case "136" => Some(OpFDiv(arguments))
      case "143" => Some(OpMatrixTimesScalar(arguments))
      case "15" => Some(OpEntryPoint(arguments))
      case "150" => Some(OpISubBorrow(arguments))
      case "213" => Some(OpDPdxCoarse(arguments))
      case "22" => Some(OpTypeFloat(arguments))
      case "220" => Some(OpEmitStreamVertex(arguments))
      case "297" => Some(OpRetainEvent(arguments))
      case "4419" => Some(OpUntypedAccessChainKHR(arguments))
      case "4426" => Some(OpUntypedPrefetchKHR(arguments))
      case "4433" => Some(OpExtInstWithForwardRefsKHR(arguments))
      case "4503" => Some(OpImageBlockMatchGatherSADQCOM(arguments))
      case "5056" => Some(OpReadClockKHR(arguments))
      case "5119" => Some(OpBufferPointerEXT(arguments))
      case "5126" => Some(OpUntypedImageTexelPointerEXT(arguments))
      case "5294" => Some(OpEmitMeshTasksEXT(arguments))
      case "5364" => Some(OpBeginInvocationInterlockEXT(arguments))
      case "5371" => Some(OpTypeTensorViewNV(arguments))
      case "5427" => Some(OpRayQueryGetIntersectionSpherePositionNV(arguments))
      case "5434" => Some(OpHitObjectGetLSSPositionsNV(arguments))
      case "5588" => Some(OpAbsUSubINTEL(arguments))
      case "5595" => Some(OpISubSatINTEL(arguments))
      case "5728" => Some(OpSubgroupAvcMceSetAcOnlyHaarINTEL(arguments))
      case "5735" => Some(OpSubgroupAvcMceConvertToRefResultINTEL(arguments))
      case "5742" => Some(OpSubgroupAvcMceGetInterMinorShapeINTEL(arguments))
      case "5805" => Some(OpSubgroupAvcSicEvaluateWithDualReferenceINTEL(arguments))
      case "5812" => Some(OpSubgroupAvcSicGetPackedIpeLumaModesINTEL(arguments))
      case "6428" => Some(OpMaskedGatherINTEL(arguments))
      case "99" => Some(OpImageWrite(arguments))
      case _ => None
    }

  private def decode5(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "137" => Some(OpUMod(arguments))
      case "144" => Some(OpVectorTimesMatrix(arguments))
      case "151" => Some(OpUMulExtended(arguments))
      case "16" => Some(OpExecutionMode(arguments))
      case "207" => Some(OpDPdx(arguments))
      case "214" => Some(OpDPdyCoarse(arguments))
      case "221" => Some(OpEndStreamPrimitive(arguments))
      case "23" => Some(OpTypeVector(arguments))
      case "298" => Some(OpReleaseEvent(arguments))
      case "30" => Some(OpTypeStruct(arguments))
      case "4427" => Some(OpFmaKHR(arguments))
      case "4434" => Some(OpUntypedGroupAsyncCopyKHR(arguments))
      case "5127" => Some(OpMemberDecorateIdEXT(arguments))
      case "5288" => Some(OpTypeVectorIdEXT(arguments))
      case "5295" => Some(OpSetMeshOutputsEXT(arguments))
      case "5358" => Some(OpTypeCooperativeMatrixNV(arguments))
      case "5365" => Some(OpEndInvocationInterlockEXT(arguments))
      case "5372" => Some(OpCreateTensorLayoutNV(arguments))
      case "5428" => Some(OpRayQueryGetIntersectionSphereRadiusNV(arguments))
      case "5435" => Some(OpHitObjectGetLSSRadiiNV(arguments))
      case "5589" => Some(OpIAddSatINTEL(arguments))
      case "5596" => Some(OpUSubSatINTEL(arguments))
      case "5729" => Some(OpSubgroupAvcMceSetSourceInterlacedFieldPolarityINTEL(arguments))
      case "5736" => Some(OpSubgroupAvcMceConvertToSicPayloadINTEL(arguments))
      case "5743" => Some(OpSubgroupAvcMceGetInterDirectionsINTEL(arguments))
      case "5750" => Some(OpSubgroupAvcImeRefWindowSizeINTEL(arguments))
      case "5806" => Some(OpSubgroupAvcSicEvaluateWithMultiReferenceINTEL(arguments))
      case "5813" => Some(OpSubgroupAvcSicGetIpeChromaModeINTEL(arguments))
      case "5820" => Some(OpRestoreMemoryINTEL(arguments))
      case "6142" => Some(OpControlBarrierArriveEXT(arguments))
      case "6429" => Some(OpMaskedScatterINTEL(arguments))
      case _ => None
    }

  private def decode6(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "138" => Some(OpSRem(arguments))
      case "145" => Some(OpMatrixTimesVector(arguments))
      case "152" => Some(OpSMulExtended(arguments))
      case "17" => Some(OpCapability(arguments))
      case "208" => Some(OpDPdy(arguments))
      case "215" => Some(OpFwidthCoarse(arguments))
      case "24" => Some(OpTypeMatrix(arguments))
      case "299" => Some(OpCreateUserEvent(arguments))
      case "31" => Some(OpTypeOpaque(arguments))
      case "4428" => Some(OpSubgroupAllKHR(arguments))
      case "5289" => Some(OpCooperativeVectorMatrixMulNV(arguments))
      case "5296" => Some(OpGroupNonUniformPartitionEXT(arguments))
      case "5359" => Some(OpCooperativeMatrixLoadNV(arguments))
      case "5366" => Some(OpCooperativeMatrixReduceNV(arguments))
      case "5373" => Some(OpTensorLayoutSetDimensionNV(arguments))
      case "5380" => Some(OpDemoteToHelperInvocation(arguments))
      case "5429" => Some(OpRayQueryGetIntersectionLSSPositionsNV(arguments))
      case "5436" => Some(OpHitObjectIsSphereHitNV(arguments))
      case "5597" => Some(OpIMul32x16INTEL(arguments))
      case "5737" => Some(OpSubgroupAvcMceConvertToSicResultINTEL(arguments))
      case "5744" => Some(OpSubgroupAvcMceGetInterMotionVectorCountINTEL(arguments))
      case "5751" => Some(OpSubgroupAvcImeAdjustRefOffsetINTEL(arguments))
      case "5807" => Some(OpSubgroupAvcSicEvaluateWithMultiReferenceInterlacedINTEL(arguments))
      case "5814" => Some(OpSubgroupAvcSicGetPackedSkcLumaCountThresholdINTEL(arguments))
      case "6143" => Some(OpControlBarrierWaitEXT(arguments))
      case _ => None
    }

  private def decode7(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "139" => Some(OpSMod(arguments))
      case "146" => Some(OpMatrixTimesMatrix(arguments))
      case "160" => Some(OpSignBitSet(arguments))
      case "209" => Some(OpFwidth(arguments))
      case "230" => Some(OpAtomicCompareExchange(arguments))
      case "25" => Some(OpTypeImage(arguments))
      case "300" => Some(OpIsValidEvent(arguments))
      case "32" => Some(OpTypePointer(arguments))
      case "4429" => Some(OpSubgroupAnyKHR(arguments))
      case "4450" => Some(OpSDot(arguments))
      case "5129" => Some(OpConstantSizeOfEXT(arguments))
      case "5367" => Some(OpCooperativeMatrixLoadTensorNV(arguments))
      case "5374" => Some(OpTensorLayoutSetStrideNV(arguments))
      case "5381" => Some(OpIsHelperInvocationEXT(arguments))
      case "5437" => Some(OpHitObjectIsLSSHitNV(arguments))
      case "5598" => Some(OpUMul32x16INTEL(arguments))
      case "5738" => Some(OpSubgroupAvcMceGetMotionVectorsINTEL(arguments))
      case "5745" => Some(OpSubgroupAvcMceGetInterReferenceIdsINTEL(arguments))
      case "5752" => Some(OpSubgroupAvcImeConvertToMcePayloadINTEL(arguments))
      case "5808" => Some(OpSubgroupAvcSicConvertToMceResultINTEL(arguments))
      case "5815" => Some(OpSubgroupAvcSicGetPackedSkcLumaSumThresholdINTEL(arguments))
      case "6221" => Some(OpSubgroupBlockPrefetchINTEL(arguments))
      case _ => None
    }

  private def decode8(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "147" => Some(OpOuterProduct(arguments))
      case "154" => Some(OpAny(arguments))
      case "161" => Some(OpLessOrGreater(arguments))
      case "19" => Some(OpTypeVoid(arguments))
      case "224" => Some(OpControlBarrier(arguments))
      case "231" => Some(OpAtomicCompareExchangeWeak(arguments))
      case "26" => Some(OpTypeSampler(arguments))
      case "301" => Some(OpSetUserEventStatus(arguments))
      case "33" => Some(OpTypeFunction(arguments))
      case "4451" => Some(OpUDot(arguments))
      case "5074" => Some(OpAllocateNodePayloadsAMDX(arguments))
      case "5368" => Some(OpCooperativeMatrixStoreTensorNV(arguments))
      case "5375" => Some(OpTensorLayoutSliceNV(arguments))
      case "5382" => Some(OpTensorViewSetClipNV(arguments))
      case "5438" => Some(OpRayQueryIsSphereHitNV(arguments))
      case "5739" => Some(OpSubgroupAvcMceGetInterDistortionsINTEL(arguments))
      case "5746" => Some(OpSubgroupAvcMceGetInterReferenceInterlacedFieldPolaritiesINTEL(arguments))
      case "5753" => Some(OpSubgroupAvcImeSetMaxMotionVectorCountINTEL(arguments))
      case "5760" => Some(OpSubgroupAvcImeEvaluateWithDualReferenceStreaminINTEL(arguments))
      case "5809" => Some(OpSubgroupAvcSicGetIpeLumaShapeINTEL(arguments))
      case "5816" => Some(OpSubgroupAvcSicGetInterRawSadsINTEL(arguments))
      case "6145" => Some(OpArithmeticFenceEXT(arguments))
      case "6530" => Some(OpConvertHandleToSamplerINTEL(arguments))
      case _ => None
    }

  private def decode9(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "148" => Some(OpDot(arguments))
      case "155" => Some(OpAll(arguments))
      case "162" => Some(OpOrdered(arguments))
      case "218" => Some(OpEmitVertex(arguments))
      case "225" => Some(OpMemoryBarrier(arguments))
      case "232" => Some(OpAtomicIIncrement(arguments))
      case "27" => Some(OpTypeSampledImage(arguments))
      case "302" => Some(OpCaptureEventProfilingInfo(arguments))
      case "34" => Some(OpTypeEvent(arguments))
      case "41" => Some(OpConstantTrue(arguments))
      case "4445" => Some(OpTraceRayKHR(arguments))
      case "4452" => Some(OpSUDot(arguments))
      case "5075" => Some(OpEnqueueNodePayloadsAMDX(arguments))
      case "5299" => Some(OpWritePackedPrimitiveIndices4x8NV(arguments))
      case "5369" => Some(OpCooperativeMatrixPerElementOpNV(arguments))
      case "5376" => Some(OpTensorLayoutSetClampValueNV(arguments))
      case "5390" => Some(OpCooperativeMatrixTransposeNV(arguments))
      case "5439" => Some(OpRayQueryIsLSSHitNV(arguments))
      case "5600" => Some(OpConstantFunctionPointerINTEL(arguments))
      case "5747" => Some(OpSubgroupAvcImeInitializeINTEL(arguments))
      case "5754" => Some(OpSubgroupAvcImeSetUnidirectionalMixDisableINTEL(arguments))
      case "5761" => Some(OpSubgroupAvcImeEvaluateWithSingleReferenceStreamoutINTEL(arguments))
      case "6090" => Some(OpTypeStructContinuedINTEL(arguments))
      case "6531" => Some(OpConvertHandleToSampledImageINTEL(arguments))
      case _ => None
    }

  private def decode10(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "149" => Some(OpIAddCarry(arguments))
      case "156" => Some(OpIsNan(arguments))
      case "163" => Some(OpUnordered(arguments))
      case "170" => Some(OpIEqual(arguments))
      case "219" => Some(OpEndPrimitive(arguments))
      case "233" => Some(OpAtomicIDecrement(arguments))
      case "240" => Some(OpAtomicAnd(arguments))
      case "28" => Some(OpTypeArray(arguments))
      case "303" => Some(OpGetDefaultQueue(arguments))
      case "310" => Some(OpImageSparseSampleProjExplicitLod(arguments))
      case "35" => Some(OpTypeDeviceEvent(arguments))
      case "42" => Some(OpConstantFalse(arguments))
      case "4446" => Some(OpExecuteCallableKHR(arguments))
      case "4453" => Some(OpSDotAccSat(arguments))
      case "4460" => Some(OpCooperativeMatrixLengthKHR(arguments))
      case "5076" => Some(OpTypeNodePayloadArrayAMDX(arguments))
      case "5090" => Some(OpNodePayloadArrayLengthAMDX(arguments))
      case "5300" => Some(OpFetchMicroTriangleVertexPositionNV(arguments))
      case "5377" => Some(OpCreateTensorViewNV(arguments))
      case "5384" => Some(OpTensorLayoutSetBlockSizeNV(arguments))
      case "5391" => Some(OpConvertUToImageNV(arguments))
      case "5601" => Some(OpFunctionPointerCallINTEL(arguments))
      case "5748" => Some(OpSubgroupAvcImeSetSingleReferenceINTEL(arguments))
      case "5755" => Some(OpSubgroupAvcImeSetEarlySearchTerminationThresholdINTEL(arguments))
      case "5762" => Some(OpSubgroupAvcImeEvaluateWithDualReferenceStreamoutINTEL(arguments))
      case "5818" => Some(OpVariableLengthArrayINTEL(arguments))
      case "6091" => Some(OpConstantCompositeContinuedINTEL(arguments))
      case "6231" => Some(OpSubgroup2DBlockLoadINTEL(arguments))
      case _ => None
    }

  private def decode11(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "157" => Some(OpIsInf(arguments))
      case "164" => Some(OpLogicalEqual(arguments))
      case "171" => Some(OpINotEqual(arguments))
      case "227" => Some(OpAtomicLoad(arguments))
      case "234" => Some(OpAtomicIAdd(arguments))
      case "241" => Some(OpAtomicOr(arguments))
      case "29" => Some(OpTypeRuntimeArray(arguments))
      case "304" => Some(OpBuildNDRange(arguments))
      case "311" => Some(OpImageSparseSampleProjDrefImplicitLod(arguments))
      case "36" => Some(OpTypeReserveId(arguments))
      case "4160" => Some(OpColorAttachmentReadEXT(arguments))
      case "43" => Some(OpConstant(arguments))
      case "4447" => Some(OpConvertUToAccelerationStructureKHR(arguments))
      case "4454" => Some(OpUDotAccSat(arguments))
      case "4461" => Some(OpConstantCompositeReplicateEXT(arguments))
      case "50" => Some(OpSpecConstant(arguments))
      case "5000" => Some(OpGroupIAddNonUniformAMD(arguments))
      case "5147" => Some(OpConstantDataKHR(arguments))
      case "5301" => Some(OpFetchMicroTriangleVertexBarycentricNV(arguments))
      case "5378" => Some(OpTensorViewSetDimensionNV(arguments))
      case "5392" => Some(OpConvertUToSamplerNV(arguments))
      case "5749" => Some(OpSubgroupAvcImeSetDualReferenceINTEL(arguments))
      case "5756" => Some(OpSubgroupAvcImeSetWeightedSadINTEL(arguments))
      case "5763" => Some(OpSubgroupAvcImeEvaluateWithSingleReferenceStreaminoutINTEL(arguments))
      case "5770" => Some(OpSubgroupAvcImeGetStreamoutSingleReferenceMajorShapeMotionVectorsINTEL(arguments))
      case "5819" => Some(OpSaveMemoryINTEL(arguments))
      case "5840" => Some(OpArbitraryFloatSinCosPiALTERA(arguments))
      case "6092" => Some(OpSpecConstantCompositeContinuedINTEL(arguments))
      case "6232" => Some(OpSubgroup2DBlockLoadTransformINTEL(arguments))
      case _ => None
    }

  private def decode12(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "158" => Some(OpIsFinite(arguments))
      case "165" => Some(OpLogicalNotEqual(arguments))
      case "172" => Some(OpUGreaterThan(arguments))
      case "228" => Some(OpAtomicStore(arguments))
      case "235" => Some(OpAtomicISub(arguments))
      case "242" => Some(OpAtomicXor(arguments))
      case "305" => Some(OpImageSparseSampleImplicitLod(arguments))
      case "312" => Some(OpImageSparseSampleProjDrefExplicitLod(arguments))
      case "37" => Some(OpTypeQueue(arguments))
      case "4161" => Some(OpDepthAttachmentReadEXT(arguments))
      case "44" => Some(OpConstantComposite(arguments))
      case "4448" => Some(OpIgnoreIntersectionKHR(arguments))
      case "4455" => Some(OpSUDotAccSat(arguments))
      case "4462" => Some(OpSpecConstantCompositeReplicateEXT(arguments))
      case "5001" => Some(OpGroupFAddNonUniformAMD(arguments))
      case "5078" => Some(OpFinishWritingNodePayloadAMDX(arguments))
      case "51" => Some(OpSpecConstantComposite(arguments))
      case "5148" => Some(OpSpecConstantDataKHR(arguments))
      case "5302" => Some(OpCooperativeVectorLoadNV(arguments))
      case "5379" => Some(OpTensorViewSetStrideNV(arguments))
      case "5393" => Some(OpConvertImageToUNV(arguments))
      case "5610" => Some(OpAsmINTEL(arguments))
      case "5757" => Some(OpSubgroupAvcImeEvaluateWithSingleReferenceINTEL(arguments))
      case "5764" => Some(OpSubgroupAvcImeEvaluateWithDualReferenceStreaminoutINTEL(arguments))
      case "5771" => Some(OpSubgroupAvcImeGetStreamoutSingleReferenceMajorShapeDistortionsINTEL(arguments))
      case "5841" => Some(OpArbitraryFloatCastALTERA(arguments))
      case "5911" => Some(OpAliasDomainDeclINTEL(arguments))
      case "6086" => Some(OpTypeBufferSurfaceINTEL(arguments))
      case "6163" => Some(OpTaskSequenceCreateALTERA(arguments))
      case "6233" => Some(OpSubgroup2DBlockLoadTransposeINTEL(arguments))
      case _ => None
    }

  private def decode13(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "159" => Some(OpIsNormal(arguments))
      case "166" => Some(OpLogicalOr(arguments))
      case "173" => Some(OpSGreaterThan(arguments))
      case "180" => Some(OpFOrdEqual(arguments))
      case "229" => Some(OpAtomicExchange(arguments))
      case "236" => Some(OpAtomicSMin(arguments))
      case "250" => Some(OpBranchConditional(arguments))
      case "306" => Some(OpImageSparseSampleExplicitLod(arguments))
      case "313" => Some(OpImageSparseFetch(arguments))
      case "320" => Some(OpImageSparseRead(arguments))
      case "38" => Some(OpTypePipe(arguments))
      case "4162" => Some(OpStencilAttachmentReadEXT(arguments))
      case "4449" => Some(OpTerminateRayKHR(arguments))
      case "4456" => Some(OpTypeCooperativeMatrixKHR(arguments))
      case "4463" => Some(OpCompositeConstructReplicateEXT(arguments))
      case "45" => Some(OpConstantSampler(arguments))
      case "4540" => Some(OpCompositeConstructCoopMatQCOM(arguments))
      case "5002" => Some(OpGroupFMinNonUniformAMD(arguments))
      case "52" => Some(OpSpecConstantOp(arguments))
      case "5303" => Some(OpCooperativeVectorStoreNV(arguments))
      case "5310" => Some(OpHitObjectReorderExecuteShaderEXT(arguments))
      case "5394" => Some(OpConvertSamplerToUNV(arguments))
      case "5611" => Some(OpAsmCallINTEL(arguments))
      case "5758" => Some(OpSubgroupAvcImeEvaluateWithDualReferenceINTEL(arguments))
      case "5765" => Some(OpSubgroupAvcImeConvertToMceResultINTEL(arguments))
      case "5772" => Some(OpSubgroupAvcImeGetStreamoutSingleReferenceMajorShapeReferenceIdsINTEL(arguments))
      case "5842" => Some(OpArbitraryFloatCastFromIntALTERA(arguments))
      case "5912" => Some(OpAliasScopeDeclINTEL(arguments))
      case "6164" => Some(OpTaskSequenceAsyncALTERA(arguments))
      case "6234" => Some(OpSubgroup2DBlockPrefetchINTEL(arguments))
      case _ => None
    }

  private def decode14(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "167" => Some(OpLogicalAnd(arguments))
      case "174" => Some(OpUGreaterThanEqual(arguments))
      case "181" => Some(OpFUnordEqual(arguments))
      case "237" => Some(OpAtomicUMin(arguments))
      case "251" => Some(OpSwitch(arguments))
      case "307" => Some(OpImageSparseSampleDrefImplicitLod(arguments))
      case "314" => Some(OpImageSparseGather(arguments))
      case "321" => Some(OpSizeOf(arguments))
      case "39" => Some(OpTypeForwardPointer(arguments))
      case "4163" => Some(OpTypeTensorARM(arguments))
      case "4457" => Some(OpCooperativeMatrixLoadKHR(arguments))
      case "4541" => Some(OpCompositeExtractCoopMatQCOM(arguments))
      case "46" => Some(OpConstantNull(arguments))
      case "5003" => Some(OpGroupUMinNonUniformAMD(arguments))
      case "5304" => Some(OpHitObjectRecordFromQueryEXT(arguments))
      case "5311" => Some(OpHitObjectTraceReorderExecuteEXT(arguments))
      case "5395" => Some(OpConvertUToSampledImageNV(arguments))
      case "5759" => Some(OpSubgroupAvcImeEvaluateWithSingleReferenceStreaminINTEL(arguments))
      case "5766" => Some(OpSubgroupAvcImeGetSingleReferenceStreaminINTEL(arguments))
      case "5773" => Some(OpSubgroupAvcImeGetStreamoutDualReferenceMajorShapeMotionVectorsINTEL(arguments))
      case "5780" => Some(OpSubgroupAvcImeGetWeightingPatternMinimumDistortionINTEL(arguments))
      case "5843" => Some(OpArbitraryFloatCastToIntALTERA(arguments))
      case "5850" => Some(OpArbitraryFloatGTALTERA(arguments))
      case "5913" => Some(OpAliasScopeListDeclINTEL(arguments))
      case "60" => Some(OpImageTexelPointer(arguments))
      case "6165" => Some(OpTaskSequenceGetALTERA(arguments))
      case "6235" => Some(OpSubgroup2DBlockStoreINTEL(arguments))
      case "6242" => Some(OpBitwiseFunctionINTEL(arguments))
      case "6529" => Some(OpConvertHandleToImageINTEL(arguments))
      case _ => None
    }

  private def decode15(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "168" => Some(OpLogicalNot(arguments))
      case "175" => Some(OpSGreaterThanEqual(arguments))
      case "182" => Some(OpFOrdNotEqual(arguments))
      case "238" => Some(OpAtomicSMax(arguments))
      case "245" => Some(OpPhi(arguments))
      case "252" => Some(OpKill(arguments))
      case "308" => Some(OpImageSparseSampleDrefExplicitLod(arguments))
      case "315" => Some(OpImageSparseDrefGather(arguments))
      case "322" => Some(OpTypePipeStorage(arguments))
      case "4164" => Some(OpTensorReadARM(arguments))
      case "4458" => Some(OpCooperativeMatrixStoreKHR(arguments))
      case "4472" => Some(OpTypeRayQueryKHR(arguments))
      case "4542" => Some(OpExtractSubArrayQCOM(arguments))
      case "5004" => Some(OpGroupSMinNonUniformAMD(arguments))
      case "5011" => Some(OpFragmentMaskFetchAMD(arguments))
      case "5158" => Some(OpPoisonKHR(arguments))
      case "5305" => Some(OpHitObjectRecordMissEXT(arguments))
      case "5312" => Some(OpHitObjectTraceMotionReorderExecuteEXT(arguments))
      case "5396" => Some(OpConvertSampledImageToUNV(arguments))
      case "54" => Some(OpFunction(arguments))
      case "5767" => Some(OpSubgroupAvcImeGetDualReferenceStreaminINTEL(arguments))
      case "5774" => Some(OpSubgroupAvcImeGetStreamoutDualReferenceMajorShapeDistortionsINTEL(arguments))
      case "5781" => Some(OpSubgroupAvcFmeInitializeINTEL(arguments))
      case "5851" => Some(OpArbitraryFloatGEALTERA(arguments))
      case "6096" => Some(OpCompositeConstructContinuedINTEL(arguments))
      case "61" => Some(OpLoad(arguments))
      case "6166" => Some(OpTaskSequenceReleaseALTERA(arguments))
      case "6250" => Some(OpConditionalCapabilityINTEL(arguments))
      case _ => None
    }

  private def decode16(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "169" => Some(OpSelect(arguments))
      case "176" => Some(OpULessThan(arguments))
      case "183" => Some(OpFUnordNotEqual(arguments))
      case "190" => Some(OpFOrdGreaterThanEqual(arguments))
      case "239" => Some(OpAtomicUMax(arguments))
      case "246" => Some(OpLoopMerge(arguments))
      case "253" => Some(OpReturn(arguments))
      case "260" => Some(OpGroupWaitEvents(arguments))
      case "309" => Some(OpImageSparseSampleProjImplicitLod(arguments))
      case "316" => Some(OpImageSparseTexelsResident(arguments))
      case "323" => Some(OpConstantPipeStorage(arguments))
      case "330" => Some(OpModuleProcessed(arguments))
      case "400" => Some(OpCopyLogical(arguments))
      case "4165" => Some(OpTensorWriteARM(arguments))
      case "4459" => Some(OpCooperativeMatrixMulAddKHR(arguments))
      case "4473" => Some(OpRayQueryInitializeKHR(arguments))
      case "4480" => Some(OpImageSampleWeightedQCOM(arguments))
      case "48" => Some(OpSpecConstantTrue(arguments))
      case "5005" => Some(OpGroupFMaxNonUniformAMD(arguments))
      case "5012" => Some(OpFragmentFetchAMD(arguments))
      case "5159" => Some(OpFreezeKHR(arguments))
      case "5250" => Some(OpHitObjectRecordHitWithIndexMotionNV(arguments))
      case "5306" => Some(OpHitObjectRecordMissMotionEXT(arguments))
      case "5313" => Some(OpTypeHitObjectEXT(arguments))
      case "5320" => Some(OpHitObjectGetCurrentTimeEXT(arguments))
      case "5397" => Some(OpSamplerImageAddressingModeNV(arguments))
      case "55" => Some(OpFunctionParameter(arguments))
      case "5614" => Some(OpAtomicFMinEXT(arguments))
      case "5768" => Some(OpSubgroupAvcImeStripSingleReferenceStreamoutINTEL(arguments))
      case "5775" => Some(OpSubgroupAvcImeGetStreamoutDualReferenceMajorShapeReferenceIdsINTEL(arguments))
      case "5782" => Some(OpSubgroupAvcBmeInitializeINTEL(arguments))
      case "5852" => Some(OpArbitraryFloatLTALTERA(arguments))
      case "6020" => Some(OpRayQueryGetIntersectionInstanceIdKHR(arguments))
      case "62" => Some(OpStore(arguments))
      case "6237" => Some(OpSubgroupMatrixMultiplyAccumulateINTEL(arguments))
      case "6244" => Some(OpUntypedVariableLengthArrayINTEL(arguments))
      case "6251" => Some(OpSpecConstantTargetINTEL(arguments))
      case "6916" => Some(OpFDot2MixAcc32VALVE(arguments))
      case _ => None
    }

  private def decode17(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "100" => Some(OpImage(arguments))
      case "177" => Some(OpSLessThan(arguments))
      case "184" => Some(OpFOrdLessThan(arguments))
      case "191" => Some(OpFUnordGreaterThanEqual(arguments))
      case "247" => Some(OpSelectionMerge(arguments))
      case "254" => Some(OpReturnValue(arguments))
      case "261" => Some(OpGroupAll(arguments))
      case "317" => Some(OpNoLine(arguments))
      case "324" => Some(OpCreatePipeFromPipeStorage(arguments))
      case "331" => Some(OpExecutionModeId(arguments))
      case "401" => Some(OpPtrEqual(arguments))
      case "4166" => Some(OpTensorQuerySizeARM(arguments))
      case "4474" => Some(OpRayQueryTerminateKHR(arguments))
      case "4481" => Some(OpImageBoxFilterQCOM(arguments))
      case "49" => Some(OpSpecConstantFalse(arguments))
      case "5006" => Some(OpGroupUMaxNonUniformAMD(arguments))
      case "5251" => Some(OpHitObjectRecordMissMotionNV(arguments))
      case "5307" => Some(OpHitObjectGetIntersectionTriangleVertexPositionsEXT(arguments))
      case "5314" => Some(OpReorderThreadWithHintEXT(arguments))
      case "5321" => Some(OpHitObjectGetAttributesEXT(arguments))
      case "5398" => Some(OpRawAccessChainNV(arguments))
      case "56" => Some(OpFunctionEnd(arguments))
      case "5615" => Some(OpAtomicFMaxEXT(arguments))
      case "5699" => Some(OpVmeImageINTEL(arguments))
      case "5769" => Some(OpSubgroupAvcImeStripDualReferenceStreamoutINTEL(arguments))
      case "5776" => Some(OpSubgroupAvcImeGetBorderReachedINTEL(arguments))
      case "5783" => Some(OpSubgroupAvcRefConvertToMcePayloadINTEL(arguments))
      case "5790" => Some(OpSubgroupAvcRefConvertToMceResultINTEL(arguments))
      case "5846" => Some(OpArbitraryFloatAddALTERA(arguments))
      case "5853" => Some(OpArbitraryFloatLEALTERA(arguments))
      case "5860" => Some(OpArbitraryFloatLogINTEL(arguments))
      case "5923" => Some(OpFixedSqrtALTERA(arguments))
      case "5930" => Some(OpFixedCosPiALTERA(arguments))
      case "6021" => Some(OpRayQueryGetIntersectionInstanceShaderBindingTableRecordOffsetKHR(arguments))
      case "6252" => Some(OpSpecConstantArchitectureINTEL(arguments))
      case "63" => Some(OpCopyMemory(arguments))
      case "6917" => Some(OpFDot2MixAcc16VALVE(arguments))
      case "70" => Some(OpInBoundsPtrAccessChain(arguments))
      case _ => None
    }

  private def decode18(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "101" => Some(OpImageQueryFormat(arguments))
      case "178" => Some(OpULessThanEqual(arguments))
      case "185" => Some(OpFUnordLessThan(arguments))
      case "248" => Some(OpLabel(arguments))
      case "255" => Some(OpUnreachable(arguments))
      case "262" => Some(OpGroupAny(arguments))
      case "318" => Some(OpAtomicFlagTestAndSet(arguments))
      case "325" => Some(OpGetKernelLocalSizeForSubgroupCount(arguments))
      case "332" => Some(OpDecorateId(arguments))
      case "402" => Some(OpPtrNotEqual(arguments))
      case "4181" => Some(OpGraphConstantARM(arguments))
      case "4475" => Some(OpRayQueryGenerateIntersectionKHR(arguments))
      case "4482" => Some(OpImageBlockMatchSSDQCOM(arguments))
      case "4545" => Some(OpImageGatherQCOM(arguments))
      case "5007" => Some(OpGroupSMaxNonUniformAMD(arguments))
      case "5252" => Some(OpHitObjectGetWorldToObjectNV(arguments))
      case "5308" => Some(OpHitObjectGetRayFlagsEXT(arguments))
      case "5315" => Some(OpReorderThreadWithHitObjectEXT(arguments))
      case "5322" => Some(OpHitObjectGetHitKindEXT(arguments))
      case "5609" => Some(OpAsmTargetINTEL(arguments))
      case "5630" => Some(OpAssumeTrueKHR(arguments))
      case "57" => Some(OpFunctionCall(arguments))
      case "5700" => Some(OpTypeVmeImageINTEL(arguments))
      case "5777" => Some(OpSubgroupAvcImeGetTruncatedSearchIndicationINTEL(arguments))
      case "5784" => Some(OpSubgroupAvcRefSetBidirectionalMixDisableINTEL(arguments))
      case "5791" => Some(OpSubgroupAvcSicInitializeINTEL(arguments))
      case "5847" => Some(OpArbitraryFloatSubALTERA(arguments))
      case "5854" => Some(OpArbitraryFloatEQALTERA(arguments))
      case "5861" => Some(OpArbitraryFloatLog2INTEL(arguments))
      case "5924" => Some(OpFixedRecipALTERA(arguments))
      case "5931" => Some(OpFixedSinCosPiALTERA(arguments))
      case "6022" => Some(OpRayQueryGetIntersectionGeometryIndexKHR(arguments))
      case "6253" => Some(OpSpecConstantCapabilitiesINTEL(arguments))
      case "64" => Some(OpCopyMemorySized(arguments))
      case "6918" => Some(OpFDot4MixAcc32VALVE(arguments))
      case "71" => Some(OpDecorate(arguments))
      case _ => None
    }

  private def decode19(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "102" => Some(OpImageQueryOrder(arguments))
      case "179" => Some(OpSLessThanEqual(arguments))
      case "186" => Some(OpFOrdGreaterThan(arguments))
      case "249" => Some(OpBranch(arguments))
      case "256" => Some(OpLifetimeStart(arguments))
      case "263" => Some(OpGroupBroadcast(arguments))
      case "270" => Some(OpGroupUMax(arguments))
      case "319" => Some(OpAtomicFlagClear(arguments))
      case "326" => Some(OpGetKernelMaxNumSubgroups(arguments))
      case "333" => Some(OpGroupNonUniformElect(arguments))
      case "340" => Some(OpGroupNonUniformInverseBallot(arguments))
      case "403" => Some(OpPtrDiff(arguments))
      case "4182" => Some(OpGraphEntryPointARM(arguments))
      case "4476" => Some(OpRayQueryConfirmIntersectionKHR(arguments))
      case "4483" => Some(OpImageBlockMatchSADQCOM(arguments))
      case "5253" => Some(OpHitObjectGetObjectToWorldNV(arguments))
      case "5260" => Some(OpHitObjectTraceRayNV(arguments))
      case "5309" => Some(OpHitObjectSetShaderBindingTableRecordIndexEXT(arguments))
      case "5316" => Some(OpHitObjectTraceRayEXT(arguments))
      case "5323" => Some(OpHitObjectGetPrimitiveIndexEXT(arguments))
      case "5330" => Some(OpHitObjectGetWorldRayOriginEXT(arguments))
      case "5631" => Some(OpExpectKHR(arguments))
      case "5701" => Some(OpTypeAvcImePayloadINTEL(arguments))
      case "5778" => Some(OpSubgroupAvcImeGetUnidirectionalEarlySearchTerminationINTEL(arguments))
      case "5785" => Some(OpSubgroupAvcRefSetBilinearFilterEnableINTEL(arguments))
      case "5792" => Some(OpSubgroupAvcSicConfigureSkcINTEL(arguments))
      case "5848" => Some(OpArbitraryFloatMulALTERA(arguments))
      case "5855" => Some(OpArbitraryFloatRecipALTERA(arguments))
      case "5862" => Some(OpArbitraryFloatLog10INTEL(arguments))
      case "5925" => Some(OpFixedRsqrtALTERA(arguments))
      case "5932" => Some(OpFixedLogALTERA(arguments))
      case "6016" => Some(OpRayQueryGetRayTMinKHR(arguments))
      case "6023" => Some(OpRayQueryGetIntersectionPrimitiveIndexKHR(arguments))
      case "6030" => Some(OpRayQueryGetWorldRayOriginKHR(arguments))
      case "6254" => Some(OpConditionalCopyObjectINTEL(arguments))
      case "6401" => Some(OpGroupIMulKHR(arguments))
      case "65" => Some(OpAccessChain(arguments))
      case "72" => Some(OpMemberDecorate(arguments))
      case _ => None
    }

  private def decode20(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "0" => Some(OpNop(arguments))
      case "103" => Some(OpImageQuerySizeLod(arguments))
      case "110" => Some(OpConvertFToS(arguments))
      case "187" => Some(OpFUnordGreaterThan(arguments))
      case "194" => Some(OpShiftRightLogical(arguments))
      case "257" => Some(OpLifetimeStop(arguments))
      case "264" => Some(OpGroupIAdd(arguments))
      case "271" => Some(OpGroupSMax(arguments))
      case "327" => Some(OpTypeNamedBarrier(arguments))
      case "334" => Some(OpGroupNonUniformAll(arguments))
      case "341" => Some(OpGroupNonUniformBallotBitExtract(arguments))
      case "4183" => Some(OpGraphARM(arguments))
      case "4190" => Some(OpTypeGraphARM(arguments))
      case "4477" => Some(OpRayQueryProceedKHR(arguments))
      case "5254" => Some(OpHitObjectGetObjectRayDirectionNV(arguments))
      case "5261" => Some(OpHitObjectRecordHitNV(arguments))
      case "5317" => Some(OpHitObjectTraceRayMotionEXT(arguments))
      case "5324" => Some(OpHitObjectGetGeometryIndexEXT(arguments))
      case "5331" => Some(OpHitObjectGetObjectToWorldEXT(arguments))
      case "5632" => Some(OpDecorateString(arguments))
      case "5702" => Some(OpTypeAvcRefPayloadINTEL(arguments))
      case "5779" => Some(OpSubgroupAvcImeGetWeightingPatternMinimumMotionVectorINTEL(arguments))
      case "5786" => Some(OpSubgroupAvcRefEvaluateWithSingleReferenceINTEL(arguments))
      case "5793" => Some(OpSubgroupAvcSicConfigureIpeLumaINTEL(arguments))
      case "5849" => Some(OpArbitraryFloatDivALTERA(arguments))
      case "5856" => Some(OpArbitraryFloatRSqrtALTERA(arguments))
      case "5863" => Some(OpArbitraryFloatLog1pINTEL(arguments))
      case "5870" => Some(OpArbitraryFloatSinCosINTEL(arguments))
      case "59" => Some(OpVariable(arguments))
      case "5926" => Some(OpFixedSinALTERA(arguments))
      case "5933" => Some(OpFixedExpALTERA(arguments))
      case "6017" => Some(OpRayQueryGetRayFlagsKHR(arguments))
      case "6024" => Some(OpRayQueryGetIntersectionBarycentricsKHR(arguments))
      case "6031" => Some(OpRayQueryGetIntersectionObjectToWorldKHR(arguments))
      case "6248" => Some(OpConditionalExtensionINTEL(arguments))
      case "6402" => Some(OpGroupFMulKHR(arguments))
      case "66" => Some(OpInBoundsAccessChain(arguments))
      case "73" => Some(OpDecorationGroup(arguments))
      case "80" => Some(OpCompositeConstruct(arguments))
      case _ => None
    }

  private def decode21(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "1" => Some(OpUndef(arguments))
      case "104" => Some(OpImageQuerySize(arguments))
      case "111" => Some(OpConvertSToF(arguments))
      case "188" => Some(OpFOrdLessThanEqual(arguments))
      case "195" => Some(OpShiftRightArithmetic(arguments))
      case "265" => Some(OpGroupFAdd(arguments))
      case "328" => Some(OpNamedBarrierInitialize(arguments))
      case "335" => Some(OpGroupNonUniformAny(arguments))
      case "342" => Some(OpGroupNonUniformBallotBitCount(arguments))
      case "4184" => Some(OpGraphInputARM(arguments))
      case "5101" => Some(OpIsNodePayloadValidAMDX(arguments))
      case "5255" => Some(OpHitObjectGetObjectRayOriginNV(arguments))
      case "5262" => Some(OpHitObjectRecordHitWithIndexNV(arguments))
      case "5318" => Some(OpHitObjectRecordEmptyEXT(arguments))
      case "5325" => Some(OpHitObjectGetInstanceIdEXT(arguments))
      case "5332" => Some(OpHitObjectGetWorldToObjectEXT(arguments))
      case "5633" => Some(OpMemberDecorateString(arguments))
      case "5703" => Some(OpTypeAvcSicPayloadINTEL(arguments))
      case "5710" => Some(OpTypeAvcImeDualReferenceStreaminINTEL(arguments))
      case "5787" => Some(OpSubgroupAvcRefEvaluateWithDualReferenceINTEL(arguments))
      case "5794" => Some(OpSubgroupAvcSicConfigureIpeLumaChromaINTEL(arguments))
      case "5857" => Some(OpArbitraryFloatCbrtALTERA(arguments))
      case "5864" => Some(OpArbitraryFloatExpINTEL(arguments))
      case "5871" => Some(OpArbitraryFloatSinPiINTEL(arguments))
      case "5927" => Some(OpFixedCosALTERA(arguments))
      case "5934" => Some(OpPtrCastToCrossWorkgroupALTERA(arguments))
      case "6018" => Some(OpRayQueryGetIntersectionTKHR(arguments))
      case "6025" => Some(OpRayQueryGetIntersectionFrontFaceKHR(arguments))
      case "6032" => Some(OpRayQueryGetIntersectionWorldToObjectKHR(arguments))
      case "6249" => Some(OpConditionalEntryPointINTEL(arguments))
      case "6403" => Some(OpGroupBitwiseAndKHR(arguments))
      case "67" => Some(OpPtrAccessChain(arguments))
      case "74" => Some(OpGroupDecorate(arguments))
      case "81" => Some(OpCompositeExtract(arguments))
      case _ => None
    }

  private def decode22(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "105" => Some(OpImageQueryLod(arguments))
      case "112" => Some(OpConvertUToF(arguments))
      case "189" => Some(OpFUnordLessThanEqual(arguments))
      case "196" => Some(OpShiftLeftLogical(arguments))
      case "2" => Some(OpSourceContinued(arguments))
      case "259" => Some(OpGroupAsyncCopy(arguments))
      case "266" => Some(OpGroupFMin(arguments))
      case "280" => Some(OpCommitReadPipe(arguments))
      case "329" => Some(OpMemoryNamedBarrier(arguments))
      case "336" => Some(OpGroupNonUniformAllEqual(arguments))
      case "343" => Some(OpGroupNonUniformBallotFindLSB(arguments))
      case "350" => Some(OpGroupNonUniformFAdd(arguments))
      case "4185" => Some(OpGraphSetOutputARM(arguments))
      case "4479" => Some(OpRayQueryGetIntersectionTypeKHR(arguments))
      case "5249" => Some(OpHitObjectRecordHitMotionNV(arguments))
      case "5256" => Some(OpHitObjectTraceRayMotionNV(arguments))
      case "5263" => Some(OpHitObjectRecordMissNV(arguments))
      case "5270" => Some(OpHitObjectGetInstanceIdNV(arguments))
      case "5319" => Some(OpHitObjectExecuteShaderEXT(arguments))
      case "5326" => Some(OpHitObjectGetInstanceCustomIndexEXT(arguments))
      case "5333" => Some(OpHitObjectGetRayTMaxEXT(arguments))
      case "5340" => Some(OpRayQueryGetIntersectionTriangleVertexPositionsKHR(arguments))
      case "5571" => Some(OpSubgroupShuffleINTEL(arguments))
      case "5704" => Some(OpTypeAvcMcePayloadINTEL(arguments))
      case "5711" => Some(OpTypeAvcRefResultINTEL(arguments))
      case "5788" => Some(OpSubgroupAvcRefEvaluateWithMultiReferenceINTEL(arguments))
      case "5795" => Some(OpSubgroupAvcSicGetMotionVectorMaskINTEL(arguments))
      case "5858" => Some(OpArbitraryFloatHypotALTERA(arguments))
      case "5865" => Some(OpArbitraryFloatExp2INTEL(arguments))
      case "5872" => Some(OpArbitraryFloatCosPiINTEL(arguments))
      case "5928" => Some(OpFixedSinCosALTERA(arguments))
      case "6019" => Some(OpRayQueryGetIntersectionInstanceCustomIndexKHR(arguments))
      case "6026" => Some(OpRayQueryGetIntersectionCandidateAABBOpaqueKHR(arguments))
      case "6404" => Some(OpGroupBitwiseOrKHR(arguments))
      case "68" => Some(OpArrayLength(arguments))
      case "75" => Some(OpGroupMemberDecorate(arguments))
      case "82" => Some(OpCompositeInsert(arguments))
      case _ => None
    }

  private def decode23(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "106" => Some(OpImageQueryLevels(arguments))
      case "113" => Some(OpUConvert(arguments))
      case "120" => Some(OpConvertUToPtr(arguments))
      case "197" => Some(OpBitwiseOr(arguments))
      case "267" => Some(OpGroupUMin(arguments))
      case "274" => Some(OpReadPipe(arguments))
      case "281" => Some(OpCommitWritePipe(arguments))
      case "3" => Some(OpSource(arguments))
      case "337" => Some(OpGroupNonUniformBroadcast(arguments))
      case "344" => Some(OpGroupNonUniformBallotFindMSB(arguments))
      case "351" => Some(OpGroupNonUniformIMul(arguments))
      case "4186" => Some(OpGraphEndARM(arguments))
      case "5103" => Some(OpConstantStringAMDX(arguments))
      case "5110" => Some(OpGroupNonUniformQuadAllKHR(arguments))
      case "5257" => Some(OpHitObjectGetShaderRecordBufferHandleNV(arguments))
      case "5264" => Some(OpHitObjectExecuteShaderNV(arguments))
      case "5271" => Some(OpHitObjectGetInstanceCustomIndexNV(arguments))
      case "5327" => Some(OpHitObjectGetObjectRayOriginEXT(arguments))
      case "5334" => Some(OpReportIntersectionKHR(arguments))
      case "5341" => Some(OpTypeAccelerationStructureKHR(arguments))
      case "5572" => Some(OpSubgroupShuffleDownINTEL(arguments))
      case "5705" => Some(OpTypeAvcMceResultINTEL(arguments))
      case "5712" => Some(OpTypeAvcSicResultINTEL(arguments))
      case "5789" => Some(OpSubgroupAvcRefEvaluateWithMultiReferenceInterlacedINTEL(arguments))
      case "5796" => Some(OpSubgroupAvcSicConvertToMcePayloadINTEL(arguments))
      case "5859" => Some(OpArbitraryFloatSqrtALTERA(arguments))
      case "5866" => Some(OpArbitraryFloatExp10INTEL(arguments))
      case "5873" => Some(OpArbitraryFloatASinINTEL(arguments))
      case "5880" => Some(OpArbitraryFloatPowINTEL(arguments))
      case "5929" => Some(OpFixedSinPiALTERA(arguments))
      case "6027" => Some(OpRayQueryGetIntersectionObjectRayDirectionKHR(arguments))
      case "6258" => Some(OpPredicatedLoadINTEL(arguments))
      case "6405" => Some(OpGroupBitwiseXorKHR(arguments))
      case "69" => Some(OpGenericPtrMemSemantics(arguments))
      case "83" => Some(OpCopyObject(arguments))
      case "90" => Some(OpImageSampleDrefExplicitLod(arguments))
      case _ => None
    }

  private def decode24(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "107" => Some(OpImageQuerySamples(arguments))
      case "114" => Some(OpSConvert(arguments))
      case "121" => Some(OpPtrCastToGeneric(arguments))
      case "198" => Some(OpBitwiseXor(arguments))
      case "268" => Some(OpGroupSMin(arguments))
      case "275" => Some(OpWritePipe(arguments))
      case "282" => Some(OpIsValidReserveId(arguments))
      case "338" => Some(OpGroupNonUniformBroadcastFirst(arguments))
      case "345" => Some(OpGroupNonUniformShuffle(arguments))
      case "352" => Some(OpGroupNonUniformFMul(arguments))
      case "4" => Some(OpSourceExtension(arguments))
      case "5104" => Some(OpSpecConstantStringAMDX(arguments))
      case "5111" => Some(OpGroupNonUniformQuadAnyKHR(arguments))
      case "5258" => Some(OpHitObjectGetShaderBindingTableRecordIndexNV(arguments))
      case "5265" => Some(OpHitObjectGetCurrentTimeNV(arguments))
      case "5272" => Some(OpHitObjectGetWorldRayDirectionNV(arguments))
      case "5328" => Some(OpHitObjectGetObjectRayDirectionEXT(arguments))
      case "5335" => Some(OpIgnoreIntersectionNV(arguments))
      case "5573" => Some(OpSubgroupShuffleUpINTEL(arguments))
      case "5580" => Some(OpSubgroupImageMediaBlockReadINTEL(arguments))
      case "5706" => Some(OpTypeAvcImeResultINTEL(arguments))
      case "5713" => Some(OpSubgroupAvcMceGetDefaultInterBaseMultiReferencePenaltyINTEL(arguments))
      case "5720" => Some(OpSubgroupAvcMceGetDefaultInterMotionVectorCostTableINTEL(arguments))
      case "5797" => Some(OpSubgroupAvcSicSetIntraLumaShapePenaltyINTEL(arguments))
      case "5867" => Some(OpArbitraryFloatExpm1INTEL(arguments))
      case "5874" => Some(OpArbitraryFloatASinPiINTEL(arguments))
      case "5881" => Some(OpArbitraryFloatPowRINTEL(arguments))
      case "6028" => Some(OpRayQueryGetIntersectionObjectRayOriginKHR(arguments))
      case "6035" => Some(OpAtomicFAddEXT(arguments))
      case "6259" => Some(OpPredicatedStoreINTEL(arguments))
      case "6406" => Some(OpGroupLogicalAndKHR(arguments))
      case "77" => Some(OpVectorExtractDynamic(arguments))
      case "84" => Some(OpTranspose(arguments))
      case "91" => Some(OpImageSampleProjImplicitLod(arguments))
      case _ => None
    }

  private def decode25(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "115" => Some(OpFConvert(arguments))
      case "122" => Some(OpGenericCastToPtr(arguments))
      case "199" => Some(OpBitwiseAnd(arguments))
      case "269" => Some(OpGroupFMax(arguments))
      case "276" => Some(OpReservedReadPipe(arguments))
      case "283" => Some(OpGetNumPipePackets(arguments))
      case "339" => Some(OpGroupNonUniformBallot(arguments))
      case "346" => Some(OpGroupNonUniformShuffleXor(arguments))
      case "353" => Some(OpGroupNonUniformSMin(arguments))
      case "360" => Some(OpGroupNonUniformBitwiseOr(arguments))
      case "4195" => Some(OpBitcastExtractEXT(arguments))
      case "5" => Some(OpName(arguments))
      case "5259" => Some(OpHitObjectRecordEmptyNV(arguments))
      case "5266" => Some(OpHitObjectGetAttributesNV(arguments))
      case "5273" => Some(OpHitObjectGetWorldRayOriginNV(arguments))
      case "5280" => Some(OpReorderThreadWithHintNV(arguments))
      case "5329" => Some(OpHitObjectGetWorldRayDirectionEXT(arguments))
      case "5336" => Some(OpTerminateRayNV(arguments))
      case "5350" => Some(OpHitObjectIsEmptyEXT(arguments))
      case "5574" => Some(OpSubgroupShuffleXorINTEL(arguments))
      case "5581" => Some(OpSubgroupImageMediaBlockWriteINTEL(arguments))
      case "5707" => Some(OpTypeAvcImeResultSingleReferenceStreamoutINTEL(arguments))
      case "5714" => Some(OpSubgroupAvcMceSetInterBaseMultiReferencePenaltyINTEL(arguments))
      case "5721" => Some(OpSubgroupAvcMceGetDefaultHighPenaltyCostTableINTEL(arguments))
      case "5798" => Some(OpSubgroupAvcSicSetIntraLumaModeCostFunctionINTEL(arguments))
      case "5868" => Some(OpArbitraryFloatSinINTEL(arguments))
      case "5875" => Some(OpArbitraryFloatACosINTEL(arguments))
      case "5882" => Some(OpArbitraryFloatPowNINTEL(arguments))
      case "5938" => Some(OpCrossWorkgroupCastToPtrALTERA(arguments))
      case "6029" => Some(OpRayQueryGetWorldRayDirectionKHR(arguments))
      case "6407" => Some(OpGroupLogicalOrKHR(arguments))
      case "78" => Some(OpVectorInsertDynamic(arguments))
      case "92" => Some(OpImageSampleProjExplicitLod(arguments))
      case _ => None
    }

  private def decode26(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "109" => Some(OpConvertFToU(arguments))
      case "116" => Some(OpQuantizeToF16(arguments))
      case "123" => Some(OpGenericCastToPtrExplicit(arguments))
      case "130" => Some(OpISub(arguments))
      case "200" => Some(OpNot(arguments))
      case "277" => Some(OpReservedWritePipe(arguments))
      case "284" => Some(OpGetMaxPipePackets(arguments))
      case "291" => Some(OpEnqueueMarker(arguments))
      case "347" => Some(OpGroupNonUniformShuffleUp(arguments))
      case "354" => Some(OpGroupNonUniformUMin(arguments))
      case "361" => Some(OpGroupNonUniformBitwiseXor(arguments))
      case "4420" => Some(OpUntypedInBoundsAccessChainKHR(arguments))
      case "4497" => Some(OpBitCastArrayQCOM(arguments))
      case "5267" => Some(OpHitObjectGetHitKindNV(arguments))
      case "5274" => Some(OpHitObjectGetRayTMaxNV(arguments))
      case "5281" => Some(OpTypeHitObjectNV(arguments))
      case "5337" => Some(OpTraceNV(arguments))
      case "5344" => Some(OpExecuteCallableNV(arguments))
      case "5351" => Some(OpHitObjectIsHitEXT(arguments))
      case "5575" => Some(OpSubgroupBlockReadINTEL(arguments))
      case "5708" => Some(OpTypeAvcImeResultDualReferenceStreamoutINTEL(arguments))
      case "5715" => Some(OpSubgroupAvcMceGetDefaultInterShapePenaltyINTEL(arguments))
      case "5722" => Some(OpSubgroupAvcMceGetDefaultMediumPenaltyCostTableINTEL(arguments))
      case "5799" => Some(OpSubgroupAvcSicSetIntraChromaModeCostFunctionINTEL(arguments))
      case "5869" => Some(OpArbitraryFloatCosINTEL(arguments))
      case "5876" => Some(OpArbitraryFloatACosPiINTEL(arguments))
      case "5946" => Some(OpReadPipeBlockingALTERA(arguments))
      case "6" => Some(OpMemberName(arguments))
      case "6408" => Some(OpGroupLogicalXorKHR(arguments))
      case "79" => Some(OpVectorShuffle(arguments))
      case "86" => Some(OpSampledImage(arguments))
      case "93" => Some(OpImageSampleProjDrefImplicitLod(arguments))
      case _ => None
    }

  private def decode27(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "10" => Some(OpExtension(arguments))
      case "117" => Some(OpConvertPtrToU(arguments))
      case "124" => Some(OpBitcast(arguments))
      case "131" => Some(OpFSub(arguments))
      case "201" => Some(OpBitFieldInsert(arguments))
      case "278" => Some(OpReserveReadPipePackets(arguments))
      case "285" => Some(OpGroupReserveReadPipePackets(arguments))
      case "292" => Some(OpEnqueueKernel(arguments))
      case "348" => Some(OpGroupNonUniformShuffleDown(arguments))
      case "355" => Some(OpGroupNonUniformFMin(arguments))
      case "362" => Some(OpGroupNonUniformLogicalAnd(arguments))
      case "4421" => Some(OpSubgroupBallotKHR(arguments))
      case "5121" => Some(OpAbortKHR(arguments))
      case "5268" => Some(OpHitObjectGetPrimitiveIndexNV(arguments))
      case "5275" => Some(OpHitObjectGetRayTMinNV(arguments))
      case "5338" => Some(OpTraceMotionNV(arguments))
      case "5345" => Some(OpRayQueryGetIntersectionClusterIdNV(arguments))
      case "5352" => Some(OpHitObjectIsMissEXT(arguments))
      case "5576" => Some(OpSubgroupBlockWriteINTEL(arguments))
      case "5590" => Some(OpUAddSatINTEL(arguments))
      case "5709" => Some(OpTypeAvcImeSingleReferenceStreaminINTEL(arguments))
      case "5716" => Some(OpSubgroupAvcMceSetInterShapePenaltyINTEL(arguments))
      case "5723" => Some(OpSubgroupAvcMceGetDefaultLowPenaltyCostTableINTEL(arguments))
      case "5730" => Some(OpSubgroupAvcMceSetSingleReferenceInterlacedFieldPolarityINTEL(arguments))
      case "5800" => Some(OpSubgroupAvcSicSetBilinearFilterEnableINTEL(arguments))
      case "5877" => Some(OpArbitraryFloatATanINTEL(arguments))
      case "5947" => Some(OpWritePipeBlockingALTERA(arguments))
      case "6199" => Some(OpTypeTaskSequenceALTERA(arguments))
      case "7" => Some(OpString(arguments))
      case "87" => Some(OpImageSampleImplicitLod(arguments))
      case "94" => Some(OpImageSampleProjDrefExplicitLod(arguments))
      case _ => None
    }
}
