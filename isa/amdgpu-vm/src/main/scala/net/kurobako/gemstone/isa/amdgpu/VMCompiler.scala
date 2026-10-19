package net.kurobako.gemstone.isa.amdgpu

import BufferInstructionDefs.*
import DataShareInstructionDefs.*
import DecodedRegisters.*
import DualVectorInstructionDefs.*
import EncodingConstants.*
import FlatInstructionDefs.*
import PackedVectorInstructionDefs.*
import SOP1InstructionDefs.*
import SOP2InstructionDefs.*
import SOPCInstructionDefs.*
import SOPKInstructionDefs.*
import SOPPInstructionDefs.*
import ScalarMemoryInstructionDefs.*
import VMIR.*
import VOP1InstructionDefs.*
import VOP2InstructionDefs.*
import VOP3InstructionDefs.*
import cats.syntax.either.*
import cats.syntax.traverse.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*
import scala.collection.immutable.IntMap

// the digit in decodeVOP1/2/3 names an encoding format, not a width
@SuppressWarnings(Array("baumkuchen:numbered-family"))
private object VMCompiler {
  private def isAbsentScalarOffset(value: Long): Boolean =
    value == Fixed.NullSgpr || value == Fixed.ExecLo || value == Fixed.ExecHi

  /** GCN V_CMPX writes its mask to both EXEC and the VOPC destination; GFX10 onwards writes only EXEC. */
  private def comparisonDestination(
      comparison: VectorComparison,
      destination: ScalarRegisterIndex,
      target: Target
  ): ScalarRegisterIndex =
    nullRegister(target.isa).filter(_ => comparison.updateExec).getOrElse(destination)

  private def scalarUnaryOperation(opcode: Opcode): Option[ScalarUnaryOperation] = opcode match {
    case STruncF32                 => Some(ScalarUnaryOperation.TruncateF32)
    case SCvtU32F32                => Some(ScalarUnaryOperation.U32FromF32)
    case SCvtF32U32                => Some(ScalarUnaryOperation.F32FromU32)
    case SAbsI32                   => Some(ScalarUnaryOperation.AbsoluteI32)
    case SSextI32I8                => Some(ScalarUnaryOperation.SignExtendI8)
    case SSextI32I16               => Some(ScalarUnaryOperation.SignExtendI16)
    case SBcnt1I32B32              => Some(ScalarUnaryOperation.CountBits32)
    case SBcnt1I32B64              => Some(ScalarUnaryOperation.CountBits64)
    case SBrevB32                  => Some(ScalarUnaryOperation.ReverseBits32)
    case SNotB32                   => Some(ScalarUnaryOperation.Not32)
    case SNotB64                   => Some(ScalarUnaryOperation.Not64)
    case SFlbitI32 | SClsI32       => Some(ScalarUnaryOperation.FirstBitHighI32)
    case SFlbitI32I64 | SClsI32I64 => Some(ScalarUnaryOperation.FirstBitHighI64)
    case SFlbitI32B32 | SClzI32U32 => Some(ScalarUnaryOperation.FirstBitHighU32)
    case SFlbitI32B64 | SClzI32U64 => Some(ScalarUnaryOperation.FirstBitHighU64)
    case SBitset0B32               => Some(ScalarUnaryOperation.ClearBit32)
    case SBitset1B32               => Some(ScalarUnaryOperation.SetBit32)
    case SBitset0B64               => Some(ScalarUnaryOperation.ClearBit64)
    case SBitset1B64               => Some(ScalarUnaryOperation.SetBit64)
    case SFf0I32B32                => Some(ScalarUnaryOperation.FirstZeroLow32)
    case SFf0I32B64                => Some(ScalarUnaryOperation.FirstZeroLow64)
    case SFf1I32B32 | SCtzI32B32   => Some(ScalarUnaryOperation.FirstOneLow32)
    case SFf1I32B64 | SCtzI32B64   => Some(ScalarUnaryOperation.FirstOneLow64)
    case SBcnt0I32B32              => Some(ScalarUnaryOperation.CountZeros32)
    case SBcnt0I32B64              => Some(ScalarUnaryOperation.CountZeros64)
    case SBrevB64                  => Some(ScalarUnaryOperation.ReverseBits64)
    case SQuadmaskB32              => Some(ScalarUnaryOperation.QuadMask32)
    case SQuadmaskB64              => Some(ScalarUnaryOperation.QuadMask64)
    case SWqmB32                   => Some(ScalarUnaryOperation.WholeQuad32)
    case SWqmB64                   => Some(ScalarUnaryOperation.WholeQuad64)
    case SBitreplicateB64B32       => Some(ScalarUnaryOperation.BitReplicate)
    case _                         => None
  }

  def scalarBinaryOperation(opcode: Opcode): Option[ScalarBinaryOperation] = opcode match {
    case SAndB32                 => Some(ScalarBinaryOperation.And32)
    case SOrB32                  => Some(ScalarBinaryOperation.Or32)
    case SSubF32                 => Some(ScalarBinaryOperation.SubtractF32)
    case SAddF32                 => Some(ScalarBinaryOperation.AddF32)
    case SMulF32                 => Some(ScalarBinaryOperation.MultiplyF32)
    case SMulI32                 => Some(ScalarBinaryOperation.MultiplyI32)
    case SMulHiU32               => Some(ScalarBinaryOperation.MultiplyHighU32)
    case SMulHiI32               => Some(ScalarBinaryOperation.MultiplyHighI32)
    case SAddI32 | SAddCoI32     => Some(ScalarBinaryOperation.AddI32)
    case SSubI32 | SSubCoI32     => Some(ScalarBinaryOperation.SubtractI32)
    case SLshlB32                => Some(ScalarBinaryOperation.ShiftLeft32)
    case SLshl2AddU32            => Some(ScalarBinaryOperation.ShiftLeftTwoAndAddU32)
    case SLshl1AddU32            => Some(ScalarBinaryOperation.ShiftLeftOneAndAddU32)
    case SLshl3AddU32            => Some(ScalarBinaryOperation.ShiftLeftThreeAndAddU32)
    case SLshl4AddU32            => Some(ScalarBinaryOperation.ShiftLeftFourAndAddU32)
    case SNandB32                => Some(ScalarBinaryOperation.Nand32)
    case SXnorB32                => Some(ScalarBinaryOperation.Xnor32)
    case SBfmB32                 => Some(ScalarBinaryOperation.BitFieldMask32)
    case SAbsdiffI32             => Some(ScalarBinaryOperation.AbsoluteDifferenceI32)
    case SPackHlB32B16           => Some(ScalarBinaryOperation.PackHighLow16)
    case SAshrI32                => Some(ScalarBinaryOperation.ShiftRightArithmetic32)
    case SBfeU32                 => Some(ScalarBinaryOperation.BitFieldExtractU32)
    case SBfeI32                 => Some(ScalarBinaryOperation.BitFieldExtractI32)
    case SLshrB32                => Some(ScalarBinaryOperation.ShiftRight32)
    case SAndn2B32 | SAndNot1B32 => Some(ScalarBinaryOperation.AndNot32)
    case SOrn2B32 | SOrNot1B32   => Some(ScalarBinaryOperation.OrNot32)
    case SNorB32                 => Some(ScalarBinaryOperation.Nor32)
    case SPackLlB32B16           => Some(ScalarBinaryOperation.PackLowLow16)
    case SPackLhB32B16           => Some(ScalarBinaryOperation.PackLowHigh16)
    case SPackHhB32B16           => Some(ScalarBinaryOperation.PackHighHigh16)
    case SAddU32 | SAddCoU32     => Some(ScalarBinaryOperation.AddU32)
    case SAddcU32 | SAddCoCiU32  => Some(ScalarBinaryOperation.AddCarryU32)
    case SSubU32 | SSubCoU32     => Some(ScalarBinaryOperation.SubtractU32)
    case SSubbU32 | SSubCoCiU32  => Some(ScalarBinaryOperation.SubtractBorrowU32)
    case SXorB32                 => Some(ScalarBinaryOperation.Xor32)
    case _                       => None
  }

  private def scalarFloatOperation(opcode: Opcode): Option[ScalarFloatOperation] = opcode match {
    case SAddF16              => Some(ScalarFloatOperation.AddF16)
    case SSubF16              => Some(ScalarFloatOperation.SubtractF16)
    case SMulF16              => Some(ScalarFloatOperation.MultiplyF16)
    case SMinF16 | SMinNumF16 => Some(ScalarFloatOperation.MinimumF16)
    case SMaxF16 | SMaxNumF16 => Some(ScalarFloatOperation.MaximumF16)
    case SMinF32 | SMinNumF32 => Some(ScalarFloatOperation.MinimumF32)
    case SMaxF32 | SMaxNumF32 => Some(ScalarFloatOperation.MaximumF32)
    case SMinimumF16          => Some(ScalarFloatOperation.MinimumPropagateF16)
    case SMaximumF16          => Some(ScalarFloatOperation.MaximumPropagateF16)
    case SMinimumF32          => Some(ScalarFloatOperation.MinimumPropagateF32)
    case SMaximumF32          => Some(ScalarFloatOperation.MaximumPropagateF32)
    case SCvtPkRtzF16F32      => Some(ScalarFloatOperation.PackRoundTowardZeroF16)
    case _                    => None
  }

  private def scalarFloatUnaryOperation(opcode: Opcode): Option[ScalarFloatUnaryOperation] = opcode match {
    case SCvtF32I32   => Some(ScalarFloatUnaryOperation.F32FromI32)
    case SCvtI32F32   => Some(ScalarFloatUnaryOperation.I32FromF32)
    case SCvtF16F32   => Some(ScalarFloatUnaryOperation.F16FromF32)
    case SCvtF32F16   => Some(ScalarFloatUnaryOperation.F32FromF16)
    case SCvtHiF32F16 => Some(ScalarFloatUnaryOperation.F32FromHighF16)
    case SCeilF32     => Some(ScalarFloatUnaryOperation.CeilF32)
    case SFloorF32    => Some(ScalarFloatUnaryOperation.FloorF32)
    case SRndneF32    => Some(ScalarFloatUnaryOperation.RoundNearestEvenF32)
    case SCeilF16     => Some(ScalarFloatUnaryOperation.CeilF16)
    case SFloorF16    => Some(ScalarFloatUnaryOperation.FloorF16)
    case STruncF16    => Some(ScalarFloatUnaryOperation.TruncateF16)
    case SRndneF16    => Some(ScalarFloatUnaryOperation.RoundNearestEvenF16)
    case _            => None
  }

  def scalarMinMaxOperation(opcode: Opcode): Option[ScalarMinMaxOperation] = opcode match {
    case SMinU32 => Some(ScalarMinMaxOperation.MinimumU32)
    case SMaxU32 => Some(ScalarMinMaxOperation.MaximumU32)
    case SMinI32 => Some(ScalarMinMaxOperation.MinimumI32)
    case SMaxI32 => Some(ScalarMinMaxOperation.MaximumI32)
    case _       => None
  }

  private def scalarBinary64Operation(opcode: Opcode): Option[ScalarBinary64Operation] = opcode match {
    case SMulU64                 => Some(ScalarBinary64Operation.Multiply)
    case SAddNcU64               => Some(ScalarBinary64Operation.AddNoCarry)
    case SSubNcU64               => Some(ScalarBinary64Operation.SubtractNoCarry)
    case SAndB64                 => Some(ScalarBinary64Operation.And)
    case SAndn2B64 | SAndNot1B64 => Some(ScalarBinary64Operation.AndNot)
    case SOrB64                  => Some(ScalarBinary64Operation.Or)
    case SOrn2B64 | SOrNot1B64   => Some(ScalarBinary64Operation.OrNot)
    case SNorB64                 => Some(ScalarBinary64Operation.Nor)
    case SXorB64                 => Some(ScalarBinary64Operation.Xor)
    case SLshlB64                => Some(ScalarBinary64Operation.ShiftLeft)
    case SLshrB64                => Some(ScalarBinary64Operation.ShiftRight)
    case SAshrI64                => Some(ScalarBinary64Operation.ShiftRightArithmetic)
    case SBfeI64                 => Some(ScalarBinary64Operation.BitFieldExtractI64)
    case SBfeU64                 => Some(ScalarBinary64Operation.BitFieldExtractU64)
    case SNandB64                => Some(ScalarBinary64Operation.Nand)
    case SXnorB64                => Some(ScalarBinary64Operation.Xnor)
    case SBfmB64                 => Some(ScalarBinary64Operation.BitFieldMask)
    case _                       => None
  }

  def scalarCompareOperation(opcode: Opcode): Option[ScalarCompareOperation] = opcode match {
    case SCmpEqI32   => Some(ScalarCompareOperation.EqualI32)
    case SCmpLgI32   => Some(ScalarCompareOperation.NotEqualI32)
    case SCmpGtI32   => Some(ScalarCompareOperation.GreaterI32)
    case SCmpGeI32   => Some(ScalarCompareOperation.GreaterOrEqualI32)
    case SCmpLtI32   => Some(ScalarCompareOperation.LessI32)
    case SCmpLeI32   => Some(ScalarCompareOperation.LessOrEqualI32)
    case SCmpEqU32   => Some(ScalarCompareOperation.EqualU32)
    case SCmpLgU32   => Some(ScalarCompareOperation.NotEqualU32)
    case SCmpGtU32   => Some(ScalarCompareOperation.GreaterU32)
    case SCmpGeU32   => Some(ScalarCompareOperation.GreaterOrEqualU32)
    case SCmpLtU32   => Some(ScalarCompareOperation.LessU32)
    case SCmpLeU32   => Some(ScalarCompareOperation.LessOrEqualU32)
    case SCmpEqU64   => Some(ScalarCompareOperation.EqualU64)
    case SCmpLgU64   => Some(ScalarCompareOperation.NotEqualU64)
    case SBitcmp1B32 => Some(ScalarCompareOperation.BitSet32)
    case SBitcmp0B32 => Some(ScalarCompareOperation.BitClear32)
    case SBitcmp1B64 => Some(ScalarCompareOperation.BitSet64)
    case SBitcmp0B64 => Some(ScalarCompareOperation.BitClear64)
    case _           => None
  }

  private def scalarImmediateCompareOperation(opcode: Opcode): Option[(ScalarCompareOperation, Boolean)] =
    opcode match {
      case SCmpkEqI32 => Some(ScalarCompareOperation.EqualI32 -> true)
      case SCmpkLgI32 => Some(ScalarCompareOperation.NotEqualI32 -> true)
      case SCmpkGtI32 => Some(ScalarCompareOperation.GreaterI32 -> true)
      case SCmpkGeI32 => Some(ScalarCompareOperation.GreaterOrEqualI32 -> true)
      case SCmpkLeI32 => Some(ScalarCompareOperation.LessOrEqualI32 -> true)
      case SCmpkLtI32 => Some(ScalarCompareOperation.LessI32 -> true)
      case SCmpkEqU32 => Some(ScalarCompareOperation.EqualU32 -> false)
      case SCmpkLgU32 => Some(ScalarCompareOperation.NotEqualU32 -> false)
      case SCmpkGtU32 => Some(ScalarCompareOperation.GreaterU32 -> false)
      case SCmpkGeU32 => Some(ScalarCompareOperation.GreaterOrEqualU32 -> false)
      case SCmpkLtU32 => Some(ScalarCompareOperation.LessU32 -> false)
      case SCmpkLeU32 => Some(ScalarCompareOperation.LessOrEqualU32 -> false)
      case _          => None
    }

  private def vectorBitFindOperation(opcode: Opcode): Option[VectorBitFindOperation] = opcode match {
    case VFfblB32 | VCtzI32B32 => Some(VectorBitFindOperation.FirstBitLow32)
    case VFfbhI32 | VClsI32    => Some(VectorBitFindOperation.FirstBitHighI32)
    case VFfbhU32 | VClzI32U32 => Some(VectorBitFindOperation.FirstBitHighU32)
    case _                     => None
  }

  private def vectorConvertOperation(opcode: Opcode): Option[VectorConvertOperation] = opcode match {
    case VFrexpExpI32F64                   => Some(VectorConvertOperation.FrexpExponentF64)
    case VFrexpExpI32F32                   => Some(VectorConvertOperation.FrexpExponentF32)
    case VFrexpMantF32                     => Some(VectorConvertOperation.FrexpMantissaF32)
    case VFrexpMantF64                     => Some(VectorConvertOperation.FrexpMantissaF64)
    case VRcpIflagF32 | VRcpF32            => Some(VectorConvertOperation.ReciprocalF32)
    case VRcpLegacyF32                     => Some(VectorConvertOperation.ReciprocalLegacyF32)
    case VRcpClampF32                      => Some(VectorConvertOperation.ReciprocalClampF32)
    case VRcpClampF64                      => Some(VectorConvertOperation.ReciprocalClampF64)
    case VRsqClampF32                      => Some(VectorConvertOperation.ReciprocalSquareRootClampF32)
    case VRsqClampF64                      => Some(VectorConvertOperation.ReciprocalSquareRootClampF64)
    case VRsqLegacyF32                     => Some(VectorConvertOperation.ReciprocalSquareRootLegacyF32)
    case VLogClampF32                      => Some(VectorConvertOperation.LogarithmClampF32)
    case VRcpF64                           => Some(VectorConvertOperation.ReciprocalF64)
    case VTruncF32                         => Some(VectorConvertOperation.TruncateF32)
    case VFractF32                         => Some(VectorConvertOperation.FractionF32)
    case VFloorF32                         => Some(VectorConvertOperation.FloorF32)
    case VRndneF32                         => Some(VectorConvertOperation.RoundNearestEvenF32)
    case VTruncF64                         => Some(VectorConvertOperation.TruncateF64)
    case VFloorF64                         => Some(VectorConvertOperation.FloorF64)
    case VFractF64                         => Some(VectorConvertOperation.FractionF64)
    case VRndneF64                         => Some(VectorConvertOperation.RoundNearestEvenF64)
    case VRsqF32                           => Some(VectorConvertOperation.ReciprocalSquareRootF32)
    case VRsqF64                           => Some(VectorConvertOperation.ReciprocalSquareRootF64)
    case VSqrtF32                          => Some(VectorConvertOperation.SquareRootF32)
    case VExpF32 | VExpLegacyF32           => Some(VectorConvertOperation.ExponentBase2F32)
    case VSinF32                           => Some(VectorConvertOperation.SineF32)
    case VCosF32                           => Some(VectorConvertOperation.CosineF32)
    case VLogF32 | VLogLegacyF32           => Some(VectorConvertOperation.LogarithmBase2F32)
    case VCvtI32F32                        => Some(VectorConvertOperation.I32FromF32)
    case VCvtU32F32                        => Some(VectorConvertOperation.U32FromF32)
    case VCvtI32F64                        => Some(VectorConvertOperation.I32FromF64)
    case VCvtU32F64                        => Some(VectorConvertOperation.U32FromF64)
    case VCvtF32Ubyte0                     => Some(VectorConvertOperation.F32FromUByte0)
    case VCvtF32Ubyte1                     => Some(VectorConvertOperation.F32FromUByte1)
    case VCvtF32Ubyte2                     => Some(VectorConvertOperation.F32FromUByte2)
    case VCvtF32Ubyte3                     => Some(VectorConvertOperation.F32FromUByte3)
    case VCvtF32F64                        => Some(VectorConvertOperation.F32FromF64)
    case VCvtF32F16                        => Some(VectorConvertOperation.F32FromF16)
    case VCvtF16F32                        => Some(VectorConvertOperation.F16FromF32)
    case VCvtI16F16                        => Some(VectorConvertOperation.I16FromF16)
    case VCvtF64I32                        => Some(VectorConvertOperation.F64FromI32)
    case VCvtF64U32                        => Some(VectorConvertOperation.F64FromU32)
    case VCvtF64F32                        => Some(VectorConvertOperation.F64FromF32)
    case VCvtF32I32                        => Some(VectorConvertOperation.F32FromI32)
    case VCvtF32U32                        => Some(VectorConvertOperation.F32FromU32)
    case VCeilF32                          => Some(VectorConvertOperation.CeilF32)
    case VCeilF64                          => Some(VectorConvertOperation.CeilF64)
    case VSqrtF64                          => Some(VectorConvertOperation.SquareRootF64)
    case VCvtFlrI32F32 | VCvtFloorI32F32   => Some(VectorConvertOperation.FloorI32FromF32)
    case VCvtRpiI32F32 | VCvtNearestI32F32 => Some(VectorConvertOperation.NearestI32FromF32)
    case VCeilF16                          => Some(VectorConvertOperation.CeilF16)
    case VFloorF16                         => Some(VectorConvertOperation.FloorF16)
    case VTruncF16                         => Some(VectorConvertOperation.TruncateF16)
    case VRndneF16                         => Some(VectorConvertOperation.RoundNearestEvenF16)
    case VFractF16                         => Some(VectorConvertOperation.FractionF16)
    case VSqrtF16                          => Some(VectorConvertOperation.SquareRootF16)
    case VRcpF16                           => Some(VectorConvertOperation.ReciprocalF16)
    case VRsqF16                           => Some(VectorConvertOperation.ReciprocalSquareRootF16)
    case VExpF16                           => Some(VectorConvertOperation.ExponentBase2F16)
    case VLogF16                           => Some(VectorConvertOperation.LogarithmBase2F16)
    case VSinF16                           => Some(VectorConvertOperation.SineF16)
    case VCosF16                           => Some(VectorConvertOperation.CosineF16)
    case VFrexpMantF16                     => Some(VectorConvertOperation.FrexpMantissaF16)
    case VFrexpExpI16F16                   => Some(VectorConvertOperation.FrexpExponentF16)
    case VCvtF16I16                        => Some(VectorConvertOperation.F16FromI16)
    case VCvtF16U16                        => Some(VectorConvertOperation.F16FromU16)
    case VCvtU16F16                        => Some(VectorConvertOperation.U16FromF16)
    case VCvtOffF32I4                      => Some(VectorConvertOperation.OffsetF32FromI4)
    case VCvtI32I16                        => Some(VectorConvertOperation.I32FromI16)
    case VCvtU32U16                        => Some(VectorConvertOperation.U32FromU16)
    case VCvtF32Bf16                       => Some(VectorConvertOperation.F32FromBF16)
    case VCvtNormI16F16                    => Some(VectorConvertOperation.SignedNormFromF16)
    case VCvtNormU16F16                    => Some(VectorConvertOperation.UnsignedNormFromF16)
    case VSatPkU8I16                       => Some(VectorConvertOperation.SaturatePackU8FromI16)
    case VPrngB32                          => Some(VectorConvertOperation.PseudoRandom)
    case _                                 => None
  }

  /** How the VOP3 form of a conversion the VM runs generically takes modifiers: the sources whose ABS and NEG bits
    * apply, the width of the floating-point result OMOD and the clamp scale, and the OP_SEL bits of its binary16 source
    * and result halves.
    */
  private case class ConvertForm(modifiers: Int, output: Option[Int in (16, 32, 64)], halves: Int)

  private def convertForm(opcode: Opcode): Option[ConvertForm] = vectorConvertOperation(opcode).flatMap {
    case VectorConvertOperation.CeilF32 | VectorConvertOperation.TruncateF32 | VectorConvertOperation.FloorF32 |
        VectorConvertOperation.FractionF32 | VectorConvertOperation.RoundNearestEvenF32 |
        VectorConvertOperation.ReciprocalSquareRootF32 | VectorConvertOperation.SquareRootF32 |
        VectorConvertOperation.ExponentBase2F32 | VectorConvertOperation.LogarithmBase2F32 |
        VectorConvertOperation.SineF32 | VectorConvertOperation.CosineF32 | VectorConvertOperation.F32FromF64 |
        VectorConvertOperation.ReciprocalClampF32 | VectorConvertOperation.ReciprocalSquareRootClampF32 |
        VectorConvertOperation.LogarithmClampF32 | VectorConvertOperation.ReciprocalLegacyF32 |
        VectorConvertOperation.ReciprocalSquareRootLegacyF32 =>
      Some(ConvertForm(1, Some(32), 0))
    case VectorConvertOperation.F32FromI32 | VectorConvertOperation.F32FromU32 |
        VectorConvertOperation.F32FromUByte0 | VectorConvertOperation.F32FromUByte1 |
        VectorConvertOperation.F32FromUByte2 | VectorConvertOperation.F32FromUByte3 =>
      Some(ConvertForm(0, Some(32), 0))
    case VectorConvertOperation.CeilF64 | VectorConvertOperation.TruncateF64 | VectorConvertOperation.FloorF64 |
        VectorConvertOperation.FractionF64 | VectorConvertOperation.RoundNearestEvenF64 |
        VectorConvertOperation.ReciprocalF64 | VectorConvertOperation.ReciprocalSquareRootF64 |
        VectorConvertOperation.SquareRootF64 | VectorConvertOperation.ReciprocalClampF64 |
        VectorConvertOperation.ReciprocalSquareRootClampF64 =>
      Some(ConvertForm(1, Some(64), 0))
    case VectorConvertOperation.F64FromI32 | VectorConvertOperation.F64FromU32 => Some(ConvertForm(0, Some(64), 0))
    case VectorConvertOperation.I32FromF32 | VectorConvertOperation.U32FromF32 | VectorConvertOperation.I32FromF64 |
        VectorConvertOperation.U32FromF64 | VectorConvertOperation.FloorI32FromF32 |
        VectorConvertOperation.NearestI32FromF32 | VectorConvertOperation.FrexpExponentF32 |
        VectorConvertOperation.FrexpExponentF64 =>
      Some(ConvertForm(1, None, 0))
    case VectorConvertOperation.CeilF16 | VectorConvertOperation.FloorF16 | VectorConvertOperation.TruncateF16 |
        VectorConvertOperation.RoundNearestEvenF16 | VectorConvertOperation.FractionF16 |
        VectorConvertOperation.SquareRootF16 | VectorConvertOperation.ReciprocalF16 |
        VectorConvertOperation.ReciprocalSquareRootF16 | VectorConvertOperation.ExponentBase2F16 |
        VectorConvertOperation.LogarithmBase2F16 | VectorConvertOperation.SineF16 | VectorConvertOperation.CosineF16 |
        VectorConvertOperation.FrexpMantissaF16 =>
      Some(ConvertForm(1, Some(16), 9))
    case VectorConvertOperation.F16FromI16 | VectorConvertOperation.F16FromU16 => Some(ConvertForm(0, Some(16), 9))
    case VectorConvertOperation.FrexpExponentF16 | VectorConvertOperation.U16FromF16 => Some(ConvertForm(1, None, 9))
    case VectorConvertOperation.SignedNormFromF16 | VectorConvertOperation.UnsignedNormFromF16 =>
      Some(ConvertForm(1, None, 9))
    case VectorConvertOperation.OffsetF32FromI4                                => Some(ConvertForm(0, Some(32), 0))
    case VectorConvertOperation.I32FromI16 | VectorConvertOperation.U32FromU16 => Some(ConvertForm(0, None, 1))
    case VectorConvertOperation.F32FromBF16                                    => Some(ConvertForm(1, Some(32), 1))
    case VectorConvertOperation.SaturatePackU8FromI16                          => Some(ConvertForm(0, None, 8))
    case VectorConvertOperation.PseudoRandom                                   => Some(ConvertForm(0, None, 0))
    case _                                                                     => None
  }

  def vectorBinaryOperation(opcode: Opcode): Option[VectorBinaryOperation] = opcode match {
    case VAddU32 | VAddNcU32                   => Some(VectorBinaryOperation.AddU32)
    case VMulLoU32 | VMulLoI32                 => Some(VectorBinaryOperation.MultiplyLowU32)
    case VAddI32 | VAddNcI32                   => Some(VectorBinaryOperation.AddI32)
    case VSubI32 | VSubNcI32                   => Some(VectorBinaryOperation.SubtractI32)
    case VAddI16 | VAddNcI16                   => Some(VectorBinaryOperation.AddI16)
    case VSubI16 | VSubNcI16                   => Some(VectorBinaryOperation.SubtractI16)
    case VMulHiU32                             => Some(VectorBinaryOperation.MultiplyHighU32)
    case VMulHiI32                             => Some(VectorBinaryOperation.MultiplyHighI32)
    case VAshrrevI32                           => Some(VectorBinaryOperation.ShiftRightArithmeticI32)
    case VAshrrevI16                           => Some(VectorBinaryOperation.ShiftRightArithmeticI16)
    case VLshlrevB32                           => Some(VectorBinaryOperation.ShiftLeft32)
    case VLshrrevB32                           => Some(VectorBinaryOperation.ShiftRight32)
    case VLshlrevB16                           => Some(VectorBinaryOperation.ShiftLeft16)
    case VLshrrevB16                           => Some(VectorBinaryOperation.ShiftRight16)
    case VAddU16 | VAddNcU16                   => Some(VectorBinaryOperation.AddU16)
    case VAndB32                               => Some(VectorBinaryOperation.And32)
    case VOrB32                                => Some(VectorBinaryOperation.Or32)
    case VXorB32                               => Some(VectorBinaryOperation.Xor32)
    case VSubU16 | VSubNcU16                   => Some(VectorBinaryOperation.SubtractU16)
    case VSubrevU16                            => Some(VectorBinaryOperation.SubtractReverseU16)
    case VSubU32 | VSubNcU32                   => Some(VectorBinaryOperation.SubtractU32)
    case VSubrevU32 | VSubrevNcU32             => Some(VectorBinaryOperation.SubtractReverseU32)
    case VSubrevF32                            => Some(VectorBinaryOperation.SubtractReverseF32)
    case VMulU32U24                            => Some(VectorBinaryOperation.MultiplyU24)
    case VMulHiU32U24                          => Some(VectorBinaryOperation.MultiplyHighU24)
    case VMulI32I24                            => Some(VectorBinaryOperation.MultiplyI24)
    case VMulHiI32I24                          => Some(VectorBinaryOperation.MultiplyHighI24)
    case VMulLoU16                             => Some(VectorBinaryOperation.MultiplyLowU16)
    case VMinU32                               => Some(VectorBinaryOperation.MinimumU32)
    case VMaxU32                               => Some(VectorBinaryOperation.MaximumU32)
    case VMinI32                               => Some(VectorBinaryOperation.MinimumI32)
    case VMinI16                               => Some(VectorBinaryOperation.MinimumI16)
    case VMinU16                               => Some(VectorBinaryOperation.MinimumU16)
    case VXnorB32                              => Some(VectorBinaryOperation.Xnor32)
    case VCvtPkRtzF16F32_1 | VCvtPkrtzF16F32_2 => Some(VectorBinaryOperation.PackRoundTowardZeroF16)
    case VSubF16                               => Some(VectorBinaryOperation.SubtractF16)
    case VSubrevF16                            => Some(VectorBinaryOperation.SubtractReverseF16)
    case VLdexpF16                             => Some(VectorBinaryOperation.LoadExponentF16)
    case VMinF32 | VMinNumF32                  => Some(VectorBinaryOperation.MinimumF32)
    case VMinF16 | VMinNumF16                  => Some(VectorBinaryOperation.MinimumF16)
    case VMaxI32                               => Some(VectorBinaryOperation.MaximumI32)
    case VMaxI16                               => Some(VectorBinaryOperation.MaximumI16)
    case VMaxU16                               => Some(VectorBinaryOperation.MaximumU16)
    case VMaxF32 | VMaxNumF32                  => Some(VectorBinaryOperation.MaximumF32)
    case VMaxF16 | VMaxNumF16                  => Some(VectorBinaryOperation.MaximumF16)
    case VMinimumF32                           => Some(VectorBinaryOperation.MinimumPropagateF32)
    case VMaximumF32                           => Some(VectorBinaryOperation.MaximumPropagateF32)
    case VMinimumF16                           => Some(VectorBinaryOperation.MinimumPropagateF16)
    case VMaximumF16                           => Some(VectorBinaryOperation.MaximumPropagateF16)
    case VMinLegacyF32                         => Some(VectorBinaryOperation.MinimumLegacyF32)
    case VMaxLegacyF32                         => Some(VectorBinaryOperation.MaximumLegacyF32)
    case VLdexpF32                             => Some(VectorBinaryOperation.LoadExponentF32)
    case VAddF32                               => Some(VectorBinaryOperation.AddF32)
    case VMulF32                               => Some(VectorBinaryOperation.MultiplyF32)
    case VMulLegacyF32 | VMulDx9ZeroF32        => Some(VectorBinaryOperation.MultiplyLegacyF32)
    case VMacF32                               => Some(VectorBinaryOperation.MultiplyAccumulateF32)
    case VAddF16                               => Some(VectorBinaryOperation.AddF16)
    case VMulF16                               => Some(VectorBinaryOperation.MultiplyF16)
    case VFmacF16                              => Some(VectorBinaryOperation.FusedMultiplyAccumulateF16)
    case VSubF32                               => Some(VectorBinaryOperation.SubtractF32)
    case VAddCoU32                             => Some(VectorBinaryOperation.AddCarryU32)
    case VAddCoCiU32 | VAddcCoU32              => Some(VectorBinaryOperation.AddWithCarryU32)
    case VSubCoU32                             => Some(VectorBinaryOperation.SubtractCarryU32)
    case VSubCoCiU32 | VSubbCoU32              => Some(VectorBinaryOperation.SubtractWithBorrowU32)
    case _                                     => None
  }

  /** Integer operations on two dwords, which VOP3 runs as VOP2 does; carries write a mask and take their own forms. */
  private def integerBinary(operation: VectorBinaryOperation): Boolean = operation match {
    case VectorBinaryOperation.MultiplyHighU24 | VectorBinaryOperation.MultiplyHighI24 |
        VectorBinaryOperation.MultiplyU24 | VectorBinaryOperation.MultiplyI24 | VectorBinaryOperation.MinimumU32 |
        VectorBinaryOperation.MaximumU32 | VectorBinaryOperation.MinimumI32 | VectorBinaryOperation.MaximumI32 |
        VectorBinaryOperation.MinimumI16 | VectorBinaryOperation.MinimumU16 | VectorBinaryOperation.MaximumI16 |
        VectorBinaryOperation.MaximumU16 | VectorBinaryOperation.ShiftRight16 | VectorBinaryOperation.ShiftLeft16 |
        VectorBinaryOperation.ShiftRightArithmeticI16 | VectorBinaryOperation.MultiplyLowU16 |
        VectorBinaryOperation.Xnor32 | VectorBinaryOperation.AddI32 | VectorBinaryOperation.SubtractI32 |
        VectorBinaryOperation.AddI16 | VectorBinaryOperation.SubtractI16 =>
      true
    case _ => false
  }

  private def vectorSubDwordOperation(opcode: Opcode): Option[VectorSubDwordOperation] = opcode match {
    case VCndmaskB32                    => Some(VectorSubDwordOperation.ConditionalMask32)
    case VAddU32 | VAddNcU32            => Some(VectorSubDwordOperation.AddU32)
    case VAddU16 | VAddNcU16            => Some(VectorSubDwordOperation.AddU16)
    case VAddF16                        => Some(VectorSubDwordOperation.AddF16)
    case VAddCoU32                      => Some(VectorSubDwordOperation.AddCarryU32)
    case VAddCoCiU32 | VAddcCoU32       => Some(VectorSubDwordOperation.AddWithCarryU32)
    case VAndB32                        => Some(VectorSubDwordOperation.And32)
    case VOrB32                         => Some(VectorSubDwordOperation.Or32)
    case VXorB32                        => Some(VectorSubDwordOperation.Xor32)
    case VLshlrevB16                    => Some(VectorSubDwordOperation.ShiftLeft16)
    case VLshlrevB32                    => Some(VectorSubDwordOperation.ShiftLeft32)
    case VLshrrevB16                    => Some(VectorSubDwordOperation.ShiftRight16)
    case VLshrrevB32                    => Some(VectorSubDwordOperation.ShiftRight32)
    case VMaxI16                        => Some(VectorSubDwordOperation.MaximumI16)
    case VMaxF16                        => Some(VectorSubDwordOperation.MaximumF16)
    case VMinI16                        => Some(VectorSubDwordOperation.MinimumI16)
    case VMinU16                        => Some(VectorSubDwordOperation.MinimumU16)
    case VMinF16                        => Some(VectorSubDwordOperation.MinimumF16)
    case VMulLoU16                      => Some(VectorSubDwordOperation.MultiplyLowU16)
    case VMulF16                        => Some(VectorSubDwordOperation.MultiplyF16)
    case VMulU32U24                     => Some(VectorSubDwordOperation.MultiplyU24)
    case VMulI32I24                     => Some(VectorSubDwordOperation.MultiplyI24)
    case VSubU16 | VSubNcU16            => Some(VectorSubDwordOperation.SubtractU16)
    case VSubrevU16                     => Some(VectorSubDwordOperation.SubtractReverseU16)
    case VSubU32 | VSubNcU32            => Some(VectorSubDwordOperation.SubtractU32)
    case VSubCoU32                      => Some(VectorSubDwordOperation.SubtractCarryU32)
    case VSubCoCiU32 | VSubbCoU32       => Some(VectorSubDwordOperation.SubtractWithBorrowU32)
    case VSubrevU32 | VSubrevNcU32      => Some(VectorSubDwordOperation.SubtractReverseU32)
    case VSubrevCoU32                   => Some(VectorSubDwordOperation.SubtractReverseCarryU32)
    case VSubrevCoCiU32 | VSubbrevCoU32 => Some(VectorSubDwordOperation.SubtractReverseWithBorrowU32)
    case _                              => None
  }

  private def vectorBinary64Operation(opcode: Opcode): Option[VectorBinary64Operation] = opcode match {
    case VLdexpF64            => Some(VectorBinary64Operation.LoadExponent)
    case VAddF64              => Some(VectorBinary64Operation.Add)
    case VMulF64              => Some(VectorBinary64Operation.Multiply)
    case VMaxF64 | VMaxNumF64 => Some(VectorBinary64Operation.Maximum)
    case VMinF64 | VMinNumF64 => Some(VectorBinary64Operation.Minimum)
    case VMaximumF64          => Some(VectorBinary64Operation.MaximumPropagate)
    case VMinimumF64          => Some(VectorBinary64Operation.MinimumPropagate)
    case _                    => None
  }

  def vectorPackedBinaryOperation(opcode: Opcode): Option[VectorPackedBinaryOperation] =
    opcode match {
      case VPkAddU16                => Some(VectorPackedBinaryOperation.AddU16)
      case VPkAddF16                => Some(VectorPackedBinaryOperation.AddF16)
      case VPkMulLoU16              => Some(VectorPackedBinaryOperation.MultiplyLowU16)
      case VPkMulF16                => Some(VectorPackedBinaryOperation.MultiplyF16)
      case VPkMaxF16 | VPkMaxNumF16 => Some(VectorPackedBinaryOperation.MaximumF16)
      case VPkMinF16 | VPkMinNumF16 => Some(VectorPackedBinaryOperation.MinimumF16)
      case VPkMaximumF16            => Some(VectorPackedBinaryOperation.MaximumPropagateF16)
      case VPkMinimumF16            => Some(VectorPackedBinaryOperation.MinimumPropagateF16)
      case VPkMaxI16                => Some(VectorPackedBinaryOperation.MaximumI16)
      case VPkMaxU16                => Some(VectorPackedBinaryOperation.MaximumU16)
      case VPkSubI16                => Some(VectorPackedBinaryOperation.SubtractI16)
      case VPkLshrrevB16            => Some(VectorPackedBinaryOperation.ShiftRight16)
      case VPkAddI16                => Some(VectorPackedBinaryOperation.AddI16)
      case VPkSubU16                => Some(VectorPackedBinaryOperation.SubtractU16)
      case VPkMinI16                => Some(VectorPackedBinaryOperation.MinimumI16)
      case VPkMinU16                => Some(VectorPackedBinaryOperation.MinimumU16)
      case VPkLshlrevB16            => Some(VectorPackedBinaryOperation.ShiftLeft16)
      case VPkAshrrevI16            => Some(VectorPackedBinaryOperation.ShiftRightArithmetic16)
      case _                        => None
    }

  def vectorTernaryOperation(opcode: Opcode): Option[VectorTernaryOperation] = opcode match {
    case VAdd3U32                          => Some(VectorTernaryOperation.Add3U32)
    case VOr3B32                           => Some(VectorTernaryOperation.Or3B32)
    case VXor3B32                          => Some(VectorTernaryOperation.Xor3B32)
    case VMax3I32                          => Some(VectorTernaryOperation.Maximum3I32)
    case VMax3U32                          => Some(VectorTernaryOperation.Maximum3U32)
    case VMin3I32                          => Some(VectorTernaryOperation.Minimum3I32)
    case VMin3F32                          => Some(VectorTernaryOperation.Minimum3F32)
    case VMax3F32 | VMax3NumF32            => Some(VectorTernaryOperation.Maximum3F32)
    case VMed3F32 | VMed3NumF32            => Some(VectorTernaryOperation.Median3F32)
    case VMax3F16 | VMax3NumF16            => Some(VectorTernaryOperation.Maximum3F16)
    case VMin3F16 | VMin3NumF16            => Some(VectorTernaryOperation.Minimum3F16)
    case VMed3F16 | VMed3NumF16            => Some(VectorTernaryOperation.Median3F16)
    case VMaxminF32 | VMaxminNumF32        => Some(VectorTernaryOperation.MaximumMinimumF32)
    case VMinmaxF32 | VMinmaxNumF32        => Some(VectorTernaryOperation.MinimumMaximumF32)
    case VMaxminF16 | VMaxminNumF16        => Some(VectorTernaryOperation.MaximumMinimumF16)
    case VMinmaxF16 | VMinmaxNumF16        => Some(VectorTernaryOperation.MinimumMaximumF16)
    case VMin3NumF32                       => Some(VectorTernaryOperation.Minimum3F32)
    case VMinimum3F32                      => Some(VectorTernaryOperation.Minimum3PropagateF32)
    case VMaximum3F32                      => Some(VectorTernaryOperation.Maximum3PropagateF32)
    case VMinimum3F16                      => Some(VectorTernaryOperation.Minimum3PropagateF16)
    case VMaximum3F16                      => Some(VectorTernaryOperation.Maximum3PropagateF16)
    case VMaximumminimumF32                => Some(VectorTernaryOperation.MaximumMinimumPropagateF32)
    case VMinimummaximumF32                => Some(VectorTernaryOperation.MinimumMaximumPropagateF32)
    case VMaximumminimumF16                => Some(VectorTernaryOperation.MaximumMinimumPropagateF16)
    case VMinimummaximumF16                => Some(VectorTernaryOperation.MinimumMaximumPropagateF16)
    case VMaxminI32                        => Some(VectorTernaryOperation.MaximumMinimumI32)
    case VMinmaxI32                        => Some(VectorTernaryOperation.MinimumMaximumI32)
    case VMaxminU32                        => Some(VectorTernaryOperation.MaximumMinimumU32)
    case VMinmaxU32                        => Some(VectorTernaryOperation.MinimumMaximumU32)
    case VAlignbyteB32                     => Some(VectorTernaryOperation.AlignByte32)
    case VSadU8                            => Some(VectorTernaryOperation.SumAbsoluteDifferenceU8)
    case VSadHiU8                          => Some(VectorTernaryOperation.SumAbsoluteDifferenceHighU8)
    case VSadU16                           => Some(VectorTernaryOperation.SumAbsoluteDifferenceU16)
    case VSadU32                           => Some(VectorTernaryOperation.SumAbsoluteDifferenceU32)
    case VMsadU8                           => Some(VectorTernaryOperation.MaskedSumAbsoluteDifferenceU8)
    case VMin3U32                          => Some(VectorTernaryOperation.Minimum3U32)
    case VMed3I16                          => Some(VectorTernaryOperation.Median3I16)
    case VMed3I32                          => Some(VectorTernaryOperation.Median3I32)
    case VMed3U16                          => Some(VectorTernaryOperation.Median3U16)
    case VMed3U32                          => Some(VectorTernaryOperation.Median3U32)
    case VMax3I16                          => Some(VectorTernaryOperation.Maximum3I16)
    case VMax3U16                          => Some(VectorTernaryOperation.Maximum3U16)
    case VMin3I16                          => Some(VectorTernaryOperation.Minimum3I16)
    case VMin3U16                          => Some(VectorTernaryOperation.Minimum3U16)
    case VMadU16                           => Some(VectorTernaryOperation.MultiplyAddU16)
    case VFmaF16 | VFmaLegacyF16           => Some(VectorTernaryOperation.FusedMultiplyAddF16)
    case VDivFmasF32                       => Some(VectorTernaryOperation.DivisionFusedMultiplyAddF32)
    case VMadLegacyU16                     => Some(VectorTernaryOperation.MultiplyAddLegacyU16)
    case VMadU32U24                        => Some(VectorTernaryOperation.MultiplyAddU24)
    case VMadI32I24                        => Some(VectorTernaryOperation.MultiplyAddI24)
    case VAlignbitB32                      => Some(VectorTernaryOperation.AlignBit32)
    case VBfiB32                           => Some(VectorTernaryOperation.BitFieldInsert32)
    case VAndOrB32                         => Some(VectorTernaryOperation.AndOr32)
    case VBfeI32                           => Some(VectorTernaryOperation.BitFieldExtractI32)
    case VBfeU32                           => Some(VectorTernaryOperation.BitFieldExtractU32)
    case VXadU32                           => Some(VectorTernaryOperation.XorAddU32)
    case VAddLshlU32                       => Some(VectorTernaryOperation.AddShiftLeftU32)
    case VLshlAddU32                       => Some(VectorTernaryOperation.ShiftLeftAddU32)
    case VLshlOrB32                        => Some(VectorTernaryOperation.ShiftLeftOr32)
    case VPermB32                          => Some(VectorTernaryOperation.PermuteBytes32)
    case VMadU32U16                        => Some(VectorTernaryOperation.MultiplyAddU32U16)
    case VMadI32I16                        => Some(VectorTernaryOperation.MultiplyAddI32I16)
    case VMadI16 | VMadLegacyI16           => Some(VectorTernaryOperation.MultiplyAddI16)
    case VMadF16 | VMadLegacyF16 | VMacF16 => Some(VectorTernaryOperation.MultiplyAddF16)
    case VDivFixupF16 | VDivFixupLegacyF16 => Some(VectorTernaryOperation.DivisionFixupF16)
    case VAshrPkI8I32                      => Some(VectorTernaryOperation.ShiftRightPackI8)
    case VAshrPkU8I32                      => Some(VectorTernaryOperation.ShiftRightPackU8)
    case _                                 => None
  }

  private def vectorMediaOperation(opcode: Opcode): Option[VectorMediaOperation] = opcode match {
    case VLerpU8                                 => Some(VectorMediaOperation.LerpU8)
    case VCubeidF32                              => Some(VectorMediaOperation.CubeIdF32)
    case VCubescF32                              => Some(VectorMediaOperation.CubeScF32)
    case VCubetcF32                              => Some(VectorMediaOperation.CubeTcF32)
    case VCubemaF32                              => Some(VectorMediaOperation.CubeMaF32)
    case VCvtPkU8F32                             => Some(VectorMediaOperation.PackByteF32)
    case VCvtPkI16I32                            => Some(VectorMediaOperation.PackI16I32)
    case VCvtPkU16U32                            => Some(VectorMediaOperation.PackU16U32)
    case VCvtPknormI16F32_2 | VCvtPkNormI16F32_1 => Some(VectorMediaOperation.PackNormI16F32)
    case VCvtPknormU16F32_2 | VCvtPkNormU16F32_1 => Some(VectorMediaOperation.PackNormU16F32)
    case VCvtPknormI16F16_2 | VCvtPkNormI16F16_1 => Some(VectorMediaOperation.PackNormI16F16)
    case VCvtPknormU16F16_2 | VCvtPkNormU16F16_1 => Some(VectorMediaOperation.PackNormU16F16)
    case VBfmB32                                 => Some(VectorMediaOperation.BitFieldMask32)
    case VCvtPkF16F32                            => Some(VectorMediaOperation.PackF16F32)
    case VCvtPkBf16F32                           => Some(VectorMediaOperation.PackBF16F32)
    case VCvtPkI16F32                            => Some(VectorMediaOperation.PackI16F32)
    case VCvtPkU16F32                            => Some(VectorMediaOperation.PackU16F32)
    case _                                       => None
  }

  private def dsAtomicOperation(opcode: Opcode): Option[(AtomicOperation, Int in (4, 8))] = opcode match {
    case DsAddU32 | DsAddRtnU32             => Some((AtomicOperation.Add, 4))
    case DsSubU32 | DsSubRtnU32             => Some((AtomicOperation.Subtract, 4))
    case DsIncU32 | DsIncRtnU32             => Some((AtomicOperation.IncrementWrap, 4))
    case DsDecU32 | DsDecRtnU32             => Some((AtomicOperation.DecrementWrap, 4))
    case DsMinI32 | DsMinRtnI32             => Some((AtomicOperation.MinimumSigned, 4))
    case DsMaxI32 | DsMaxRtnI32             => Some((AtomicOperation.MaximumSigned, 4))
    case DsMinU32 | DsMinRtnU32             => Some((AtomicOperation.MinimumUnsigned, 4))
    case DsMaxU32 | DsMaxRtnU32             => Some((AtomicOperation.MaximumUnsigned, 4))
    case DsAndRtnB32                        => Some((AtomicOperation.And, 4))
    case DsOrRtnB32                         => Some((AtomicOperation.Or, 4))
    case DsXorRtnB32                        => Some((AtomicOperation.Xor, 4))
    case DsAddRtnF32                        => Some((AtomicOperation.FloatAdd, 4))
    case DsMinRtnF32                        => Some((AtomicOperation.FloatMinimum, 4))
    case DsMaxRtnF32                        => Some((AtomicOperation.FloatMaximum, 4))
    case DsAddRtnU64                        => Some((AtomicOperation.Add, 8))
    case DsSubRtnU64                        => Some((AtomicOperation.Subtract, 8))
    case DsIncRtnU64                        => Some((AtomicOperation.IncrementWrap, 8))
    case DsDecRtnU64                        => Some((AtomicOperation.DecrementWrap, 8))
    case DsMinRtnI64                        => Some((AtomicOperation.MinimumSigned, 8))
    case DsMaxRtnI64                        => Some((AtomicOperation.MaximumSigned, 8))
    case DsMinRtnU64                        => Some((AtomicOperation.MinimumUnsigned, 8))
    case DsMaxRtnU64                        => Some((AtomicOperation.MaximumUnsigned, 8))
    case DsAndRtnB64                        => Some((AtomicOperation.And, 8))
    case DsOrRtnB64                         => Some((AtomicOperation.Or, 8))
    case DsXorRtnB64                        => Some((AtomicOperation.Xor, 8))
    case DsAddRtnF64                        => Some((AtomicOperation.FloatAdd, 8))
    case DsMinRtnF64                        => Some((AtomicOperation.FloatMinimum, 8))
    case DsMaxRtnF64                        => Some((AtomicOperation.FloatMaximum, 8))
    case DsAndB32                           => Some((AtomicOperation.And, 4))
    case DsOrB32                            => Some((AtomicOperation.Or, 4))
    case DsXorB32                           => Some((AtomicOperation.Xor, 4))
    case DsAddF32                           => Some((AtomicOperation.FloatAdd, 4))
    case DsMinF32                           => Some((AtomicOperation.FloatMinimum, 4))
    case DsMaxF32                           => Some((AtomicOperation.FloatMaximum, 4))
    case DsAddU64                           => Some((AtomicOperation.Add, 8))
    case DsSubU64                           => Some((AtomicOperation.Subtract, 8))
    case DsIncU64                           => Some((AtomicOperation.IncrementWrap, 8))
    case DsDecU64                           => Some((AtomicOperation.DecrementWrap, 8))
    case DsMinI64                           => Some((AtomicOperation.MinimumSigned, 8))
    case DsMaxI64                           => Some((AtomicOperation.MaximumSigned, 8))
    case DsMinU64                           => Some((AtomicOperation.MinimumUnsigned, 8))
    case DsMaxU64                           => Some((AtomicOperation.MaximumUnsigned, 8))
    case DsAndB64                           => Some((AtomicOperation.And, 8))
    case DsOrB64                            => Some((AtomicOperation.Or, 8))
    case DsXorB64                           => Some((AtomicOperation.Xor, 8))
    case DsAddF64                           => Some((AtomicOperation.FloatAdd, 8))
    case DsMinF64                           => Some((AtomicOperation.FloatMinimum, 8))
    case DsMaxF64                           => Some((AtomicOperation.FloatMaximum, 8))
    case DsWrxchgRtnB32 | DsStorexchgRtnB32 => Some((AtomicOperation.Exchange, 4))
    case DsPkAddF16 | DsPkAddRtnF16         => Some((AtomicOperation.PackedHalfAdd, 4))
    case DsMinNumF32 | DsMinNumRtnF32       => Some((AtomicOperation.FloatMinimum, 4))
    case DsMaxNumF32 | DsMaxNumRtnF32       => Some((AtomicOperation.FloatMaximum, 4))
    case DsMinNumF64 | DsMinNumRtnF64       => Some((AtomicOperation.FloatMinimum, 8))
    case DsMaxNumF64 | DsMaxNumRtnF64       => Some((AtomicOperation.FloatMaximum, 8))
    case DsPkAddBf16 | DsPkAddRtnBf16       => Some((AtomicOperation.PackedBFloat16Add, 4))
    case DsWrxchgRtnB64 | DsStorexchgRtnB64 => Some((AtomicOperation.Exchange, 8))
    case _                                  => None
  }

  private def dsAtomicReturnsValue(opcode: Opcode): Boolean =
    opcode.mnemonic.raw.contains("_rtn_") || opcode.mnemonic.raw.endsWith("_rtn")

  def vectorComparison(opcode: Opcode): Option[VectorComparison] =
    InstructionDefs.ComparisonOpcodes.get(opcode).map { comparison =>
      val kind = comparison.kind match {
        case Opcode.ComparisonKind.Signed16     => VectorComparisonKind.Signed16
        case Opcode.ComparisonKind.Signed32     => VectorComparisonKind.Signed32
        case Opcode.ComparisonKind.Signed64     => VectorComparisonKind.Signed64
        case Opcode.ComparisonKind.Unsigned16   => VectorComparisonKind.Unsigned16
        case Opcode.ComparisonKind.Unsigned32   => VectorComparisonKind.Unsigned32
        case Opcode.ComparisonKind.Unsigned64   => VectorComparisonKind.Unsigned64
        case Opcode.ComparisonKind.Float16      => VectorComparisonKind.Float16
        case Opcode.ComparisonKind.Float32      => VectorComparisonKind.Float32
        case Opcode.ComparisonKind.Float64      => VectorComparisonKind.Float64
        case Opcode.ComparisonKind.ClassFloat32 => VectorComparisonKind.ClassFloat32
        case Opcode.ComparisonKind.ClassFloat64 => VectorComparisonKind.ClassFloat64
      }
      val relation = comparison.relation match {
        case Opcode.ComparisonRelation.Equal             => VectorComparisonRelation.Equal
        case Opcode.ComparisonRelation.OrderedNotEqual   => VectorComparisonRelation.OrderedNotEqual
        case Opcode.ComparisonRelation.NotEqual          => VectorComparisonRelation.NotEqual
        case Opcode.ComparisonRelation.Less              => VectorComparisonRelation.Less
        case Opcode.ComparisonRelation.LessOrEqual       => VectorComparisonRelation.LessOrEqual
        case Opcode.ComparisonRelation.Greater           => VectorComparisonRelation.Greater
        case Opcode.ComparisonRelation.GreaterOrEqual    => VectorComparisonRelation.GreaterOrEqual
        case Opcode.ComparisonRelation.NotLess           => VectorComparisonRelation.NotLess
        case Opcode.ComparisonRelation.NotLessOrEqual    => VectorComparisonRelation.NotLessOrEqual
        case Opcode.ComparisonRelation.NotGreater        => VectorComparisonRelation.NotGreater
        case Opcode.ComparisonRelation.NotGreaterOrEqual => VectorComparisonRelation.NotGreaterOrEqual
        case Opcode.ComparisonRelation.Ordered           => VectorComparisonRelation.Ordered
        case Opcode.ComparisonRelation.Unordered         => VectorComparisonRelation.Unordered
        case Opcode.ComparisonRelation.NotLessOrGreater  => VectorComparisonRelation.NotLessOrGreater
        case Opcode.ComparisonRelation.Class             => VectorComparisonRelation.Class
      }
      VectorComparison(kind, relation, comparison.updateExec)
    }.orElse(untabulatedComparison(opcode)).orElse(opcode.mnemonic.raw match {
      case Signalling(exec, relation) =>
        InstructionDefs.forOpcode(s"v_cmp${exec}_$relation").view.flatMap(vectorComparison(_)).headOption
      case _ => None
    })

  // the signalling compares differ from the quiet ones only in raising invalid on a quiet NaN, which the VM does not
  private val Signalling = "v_cmps(x?)_(.+)".r

  private val Untabulated = "v_cmp(x?)_(f|t|tru|class)_([fiu])(16|32|64)".r

  /** The compares the generated table leaves out: those with a constant result, and the binary16 class test. */
  private def untabulatedComparison(opcode: Opcode): Option[VectorComparison] = opcode.mnemonic.raw match {
    case Untabulated(exec, "class", "f", "16") =>
      Some(VectorComparison(VectorComparisonKind.ClassFloat16, VectorComparisonRelation.Class, exec.nonEmpty))
    case Untabulated(exec, constant, kind, width) if constant != "class" =>
      val relation = if (constant == "f") VectorComparisonRelation.Never else VectorComparisonRelation.Always
      ((kind, width) match {
        case ("f", "16") => Some(VectorComparisonKind.Float16)
        case ("f", "32") => Some(VectorComparisonKind.Float32)
        case ("f", "64") => Some(VectorComparisonKind.Float64)
        case ("i", "16") => Some(VectorComparisonKind.Signed16)
        case ("i", "32") => Some(VectorComparisonKind.Signed32)
        case ("i", "64") => Some(VectorComparisonKind.Signed64)
        case ("u", "16") => Some(VectorComparisonKind.Unsigned16)
        case ("u", "32") => Some(VectorComparisonKind.Unsigned32)
        case ("u", "64") => Some(VectorComparisonKind.Unsigned64)
        case _           => None
      }).map(VectorComparison(_, relation, exec.nonEmpty))
    case _ => None
  }

  /** The sources, bit `i` for source `i`, whose ABS and NEG bits the VOP3 IR applies; any other set bit is rejected. */
  private def vop3SourceModifierMask(opcode: Opcode): Int = opcode match {
    case VRcpF32 | VSRcpF32 | VFrexpMantF32 | VFrexpMantF64 | VCvtF64F32 | VLdexpF32 | VLdexpF64 | VTrigPreopF64 |
        VCvtF32F16 | VCvtF16F32 | VCvtI16F16 => 1
    case VCndmaskB32 | VAddF32 | VSubF32 | VSubrevF32 | VMulF32 | VMulLegacyF32 | VAddF64 | VMulF64 | VMaxF32 |
        VMinF32 | VMaxNumF32 | VMinNumF32 | VMaxF64 | VMinF64 | VMaxNumF64 | VMinNumF64 | VMacF32 | VFmacF32 |
        VMulDx9ZeroF32 |
        VFmacF64 | VPackB32F16 | VAddF16 | VMulF16 | VMinF16 | VMaxF16 | VFmacF16 | VMaxNumF16 | VMinNumF16 |
        VMinimumF32 | VMaximumF32 | VMinimumF16 | VMaximumF16 | VMinimumF64 | VMaximumF64 | VMinLegacyF32 |
        VMaxLegacyF32 | VMacLegacyF32 => 3
    // VOP3-encoded V_PK_FMA_F16 carries NEG_HI in the ABS bits.
    case VMadF32 | VFmaF32 | VFmaF64 | VFmaF16 | VMin3F32 | VDivFixupF32 | VDivFixupF64 | VDivFmasF32 |
        VDivFmasF64 | VDivScaleF32 | VDivScaleF64 | VPkFmaF16 | VMax3F32 | VMed3F32 | VMax3F16 | VMin3F16 | VMed3F16 |
        VMaxminF32 | VMinmaxF32 | VMaxminF16 | VMinmaxF16 | VCubeidF32 | VCubescF32 | VCubetcF32 | VCubemaF32 |
        VMax3NumF32 | VMin3NumF32 | VMed3NumF32 | VMaxminNumF32 | VMinmaxNumF32 | VMax3NumF16 | VMin3NumF16 |
        VMed3NumF16 | VMaxminNumF16 | VMinmaxNumF16 | VMinimum3F32 | VMaximum3F32 | VMinimum3F16 | VMaximum3F16 |
        VMaximumminimumF32 | VMinimummaximumF32 | VMaximumminimumF16 | VMinimummaximumF16 |
        VDivFixupF16 | VDivFixupLegacyF16 | VMadF16 | VMadLegacyF16 | VFmaLegacyF16 => 7
    case VMacF16 | VCvtPknormI16F32_2 | VCvtPkNormI16F32_1 | VCvtPknormU16F32_2 | VCvtPkNormU16F32_1 |
        VCvtPkF16F32 | VCvtPkBf16F32 |
        VCvtPknormI16F16_2 | VCvtPkNormI16F16_1 | VCvtPknormU16F16_2 | VCvtPkNormU16F16_1 => 3
    case VCvtPkU8F32 | VCvtPkaccumU8F32            => 1
    case VCvtPkFp8F32 | VCvtPkBf8F32               => 3
    case VCvtPkI16F32 | VCvtPkU16F32               => 3
    case VMadLegacyF32 | VFmaLegacyF32             => 7
    case VMullitF32                                => 7
    case VFmacLegacyF32                            => 3
    case VFmaDx9ZeroF32                            => 7
    case VFmacDx9ZeroF32                           => 3
    case VCvtSrFp8F32 | VCvtSrBf8F32               => 1
    case VCvtPkRtzF16F32_1 | VCvtPkrtzF16F32_2     => 3
    case VSubF16 | VSubrevF16                      => 3
    case VLdexpF16                                 => 1
    case VCvtSrF16F32 | VCvtSrBf16F32              => 1
    case other if scaledConversion(other).nonEmpty =>
      scaledConversion(other).fold(0) {
        case ScaledConversion.NarrowPair(side, _) => if (side == Minifloats.Wide.Single) 7 else 3
        case ScaledConversion.NarrowStochastic(side = _) | ScaledConversion.NarrowStochasticPair(Minifloats.Wide.Half) |
            ScaledConversion.NarrowStochasticPair(Minifloats.Wide.BFloat) => 5
        case ScaledConversion.NarrowStochasticPair(side = _)                             => 4
        case ScaledConversion.Widen(format = _) | ScaledConversion.WidenPair(format = _) => 3
        case _                                                                           => 0
      }
    case other =>
      vectorComparison(other).fold(convertForm(other).fold(0)(_.modifiers))(comparisonModifierMask(_))
  }

  /** gfx950's V_CVT_SCALEF32 conversions, by the layout and formats their mnemonics name; the packed conversions of 8
    * and 16 elements are gfx1250's.
    */
  private def scaledConversion(opcode: Opcode): Option[ScaledConversion] = {
    import ScaledConversion.*
    def narrow(name: String): Option[Minifloat] = name match {
      case "fp8" => Some(Minifloat.E4M3)
      case "bf8" => Some(Minifloat.E5M2)
      case "fp6" => Some(Minifloat.E2M3)
      case "bf6" => Some(Minifloat.E3M2)
      case "fp4" => Some(Minifloat.E2M1)
      case _     => None
    }
    def wide(name: String): Option[Minifloats.Wide] = name match {
      case "f32"  => Some(Minifloats.Wide.Single)
      case "f16"  => Some(Minifloats.Wide.Half)
      case "bf16" => Some(Minifloats.Wide.BFloat)
      case _      => None
    }
    val mnemonic                                                                  = opcode.mnemonic.raw
    def widening(from: String, to: String): Option[(Minifloat, Minifloats.Wide)]  = narrow(from).zip(wide(to))
    def narrowing(from: String, to: String): Option[(Minifloats.Wide, Minifloat)] = wide(from).zip(narrow(to))
    if (!mnemonic.startsWith("v_cvt_scalef32_")) None
    else mnemonic.stripPrefix("v_cvt_scalef32_").split('_').toVec match {
      case Vec(to, from)       => widening(from, to).map(Widen(_, _))
      case Vec("pk", to, from) =>
        widening(from, to).map(WidenPair(_, _)).orElse(narrowing(from, to).map(NarrowPair(_, _)))
      case Vec("sr", to, from)          => narrowing(from, to).map(NarrowStochastic(_, _))
      case Vec("sr", "pk", "fp4", from) => wide(from).map(NarrowStochasticPair(_))
      case Vec("pk32", to, from)        =>
        widening(from, to).map(WidenBlock(_, _))
          .orElse(narrowing(from, to).map(NarrowBlock(_, _, interleaved = false, stochastic = false)))
      case Vec("2xpk16", to, from) => narrowing(from, to).map(NarrowBlock(_, _, interleaved = true, stochastic = false))
      case Vec("sr", "pk32", to, from) =>
        narrowing(from, to).map(NarrowBlock(_, _, interleaved = false, stochastic = true))
      case _ => None
    }
  }

  /** Integer comparisons take no source modifiers and the class mask is an integer. */
  private def comparisonModifierMask(comparison: VectorComparison): Int = comparison.kind match {
    case VectorComparisonKind.Float16 | VectorComparisonKind.Float32 | VectorComparisonKind.Float64 => 3
    case VectorComparisonKind.ClassFloat16 | VectorComparisonKind.ClassFloat32 |
        VectorComparisonKind.ClassFloat64 => 1
    case _ => 0
  }

  /** The result width of the VOP3 opcodes whose OMOD the VM implements. */
  private def vop3OutputWidth(opcode: Opcode): Option[Int in (16, 32, 64)] = opcode match {
    case VRcpF32 | VFrexpMantF32 | VLdexpF32 | VAddF32 | VSubF32 | VSubrevF32 | VMulF32 | VMulLegacyF32 | VMaxF32 |
        VMulDx9ZeroF32 |
        VMinF32 | VMaxNumF32 | VMinNumF32 | VMin3F32 | VMadF32 | VFmaF32 | VMacF32 | VFmacF32 | VDivFixupF32 |
        VDivFmasF32 | VMax3F32 | VMed3F32 | VMaxminF32 | VMinmaxF32 | VCubeidF32 | VCubescF32 | VCubetcF32 |
        VCubemaF32 | VMax3NumF32 | VMin3NumF32 | VMed3NumF32 | VMaxminNumF32 | VMinmaxNumF32 | VMinimumF32 |
        VMaximumF32 | VMinimum3F32 | VMaximum3F32 | VMaximumminimumF32 | VMinimummaximumF32 => Some(32)
    case VMinimumF16 | VMaximumF16 | VMinimum3F16 | VMaximum3F16 | VMaximumminimumF16 | VMinimummaximumF16 => Some(16)
    case VMinimumF64 | VMaximumF64                                                                         => Some(64)
    case VMax3NumF16 | VMin3NumF16 | VMed3NumF16 | VMaxminNumF16 | VMinmaxNumF16 | VMaxNumF16 | VMinNumF16 => Some(16)
    case VDivFixupF16 | VDivFixupLegacyF16 | VMadF16 | VMadLegacyF16 | VFmaLegacyF16 | VMacF16             => Some(16)
    case VMax3F16 | VMin3F16 | VMed3F16 | VMaxminF16 | VMinmaxF16                                          => Some(16)
    case VFrexpMantF64 | VCvtF64F32 | VLdexpF64 | VAddF64 | VMulF64 | VMaxF64 | VMinF64 | VMaxNumF64 | VMinNumF64 |
        VFmaF64 | VFmacF64 | VDivFixupF64 | VDivFmasF64 | VTrigPreopF64 => Some(64)
    case VFmaF16 | VAddF16 | VMulF16 | VMinF16 | VMaxF16 | VFmacF16 | VCvtF16F32 | VSubF16 | VSubrevF16 | VLdexpF16 =>
      Some(16)
    case VCvtF32F16                                                 => Some(32)
    case VMadLegacyF32 | VFmaLegacyF32 | VFmacLegacyF32             => Some(32)
    case VMacLegacyF32 | VMullitF32 | VMinLegacyF32 | VMaxLegacyF32 => Some(32)
    case VFmaDx9ZeroF32 | VFmacDx9ZeroF32                           => Some(32)
    case other                                                      => convertForm(other).flatMap(_.output)
  }

  /** The OP_SEL bits each f16 VOP3 opcode defines, from GFX11 for those VOP1/VOP2 also encode; bit 3 selects the
    * destination half.
    */
  private def halfOpSelection(opcode: Opcode): Option[Int] = opcode match {
    case VMax3F16 | VMin3F16 | VMed3F16 | VMaxminF16 | VMinmaxF16                            => Some(0xf)
    case VMax3NumF16 | VMin3NumF16 | VMed3NumF16 | VMaxminNumF16 | VMinmaxNumF16             => Some(0xf)
    case VMaxNumF16 | VMinNumF16 | VAndB16 | VOrB16 | VXorB16                                => Some(0xb)
    case VMinimumF16 | VMaximumF16                                                           => Some(0xb)
    case VMinimum3F16 | VMaximum3F16 | VMaximumminimumF16 | VMinimummaximumF16               => Some(0xf)
    case VMovB16 | VNotB16 | VCvtI32I16 | VCvtU32U16 | VSwapB16                              => Some(0x9)
    case VCndmaskB16                                                                         => Some(0xb)
    case VDivFixupF16 | VDivFixupLegacyF16 | VMadF16 | VMadLegacyF16 | VFmaLegacyF16         => Some(0xf)
    case VMacF16                                                                             => Some(0xb)
    case VMadU32U16 | VMadI32I16 | VMadI16 | VMadLegacyI16                                   => Some(0x3)
    case VCvtPknormI16F16_2 | VCvtPkNormI16F16_1 | VCvtPknormU16F16_2 | VCvtPkNormU16F16_1   => Some(0x3)
    case VAddF16 | VMulF16 | VMinF16 | VMaxF16 | VFmacF16 | VSubF16 | VSubrevF16 | VLdexpF16 => Some(0xb)
    case VCvtF32F16                                                                          => Some(0x1)
    case VCvtF16F32                                                                          => Some(0x8)
    case VCvtI16F16                                                                          => Some(0x9)
    case VDot2F16F16 | VDot2Bf16Bf16                                                         => Some(0xc)
    case VAshrPkI8I32 | VAshrPkU8I32                                                         => Some(0x8)
    case other => convertForm(other).map(_.halves).filter(_ != 0)
  }

  private val NativeHalfOpcodes: Set[Opcode] = Set(
    VMax3F16,
    VMin3F16,
    VMed3F16,
    VMaxminF16,
    VMinmaxF16,
    VMax3NumF16,
    VMin3NumF16,
    VMed3NumF16,
    VMaxminNumF16,
    VMinmaxNumF16,
    VMaxNumF16,
    VMinNumF16,
    VMinimumF16,
    VMaximumF16,
    VMinimum3F16,
    VMaximum3F16,
    VMaximumminimumF16,
    VMinimummaximumF16,
    VAndB16,
    VOrB16,
    VXorB16,
    VMovB16,
    VNotB16,
    VCndmaskB16,
    VDivFixupF16,
    VMadF16,
    VMadU32U16,
    VMadI32I16,
    VMadI16,
    VCvtPknormI16F16_2,
    VCvtPknormU16F16_2
  )

  // f16 opcodes whose result fills only the low half of the destination.
  private lazy val HalfResultOpcodes: Set[Opcode] = Set(
    VDot2F16F16,
    VDot2Bf16Bf16,
    VAshrPkI8I32,
    VAshrPkU8I32,
    VAddF16,
    VMulF16,
    VMinF16,
    VMaxF16,
    VFmacF16,
    VCvtF16F32,
    VCvtI16F16,
    VSubF16,
    VSubrevF16,
    VLdexpF16,
    VMax3F16,
    VMin3F16,
    VMed3F16,
    VMaxminF16,
    VMinmaxF16,
    VMax3NumF16,
    VMin3NumF16,
    VMed3NumF16,
    VMaxminNumF16,
    VMinmaxNumF16,
    VMaxNumF16,
    VMinNumF16,
    VMinimumF16,
    VMaximumF16,
    VMinimum3F16,
    VMaximum3F16,
    VMaximumminimumF16,
    VMinimummaximumF16,
    VAndB16,
    VOrB16,
    VXorB16,
    VMovB16,
    VNotB16,
    VCndmaskB16,
    VDivFixupF16,
    VDivFixupLegacyF16,
    VMadF16,
    VMadLegacyF16,
    VFmaLegacyF16,
    VMacF16,
    VMadakF16,
    VMadmkF16,
    VFmaakF16,
    VFmamkF16
  )

  private def halfResultOpcode(opcode: Opcode): Boolean =
    HalfResultOpcodes.contains(opcode) || convertForm(opcode).exists(form => (form.halves & 8) != 0)

  /** GFX10 onwards keeps the high half of a 16-bit result's destination; earlier generations zero it. */
  private def preservesHighHalf(target: Target): Boolean =
    target.isa.isRDNA || target.isa == ISA.GFX11_7 || target.isa == ISA.CDNA5

  /** Only GFX11 onwards defines OP_SEL for the VOP3 forms of 16-bit VOP1/VOP2 opcodes. */
  private def halfOpSelectionDefined(target: Target): Boolean = target.isa match {
    case ISA.RDNA3 | ISA.RDNA3_5 | ISA.GFX11_7 | ISA.RDNA4 | ISA.CDNA5 => true
    case _                                                             => false
  }

  /** The 8-bit float format of a conversion: CDNA3's FNUZ formats, the OCP formats from gfx950 and GFX12 on. */
  private def eightBit(bf8: Boolean, target: Target): Minifloat = (target.isa == ISA.CDNA3, bf8) match {
    case (true, false)  => Minifloat.E4M3FNUZ
    case (true, true)   => Minifloat.E5M2FNUZ
    case (false, false) => Minifloat.E4M3
    case (false, true)  => Minifloat.E5M2
  }

  /** The widening of V_CVT_F32_FP8, V_CVT_F32_BF8 and their packed forms in the target's format. */
  private def eightBitConversion(opcode: Opcode, target: Target): Option[VectorConvertOperation] =
    (opcode, eightBit(opcode == VCvtF32Bf8 || opcode == VCvtPkF32Bf8, target)) match {
      case (VCvtF32Fp8 | VCvtF32Bf8, Minifloat.E4M3)         => Some(VectorConvertOperation.F32FromE4M3)
      case (VCvtF32Fp8 | VCvtF32Bf8, Minifloat.E5M2)         => Some(VectorConvertOperation.F32FromE5M2)
      case (VCvtF32Fp8 | VCvtF32Bf8, Minifloat.E4M3FNUZ)     => Some(VectorConvertOperation.F32FromE4M3Fnuz)
      case (VCvtF32Fp8 | VCvtF32Bf8, _)                      => Some(VectorConvertOperation.F32FromE5M2Fnuz)
      case (VCvtPkF32Fp8 | VCvtPkF32Bf8, Minifloat.E4M3)     => Some(VectorConvertOperation.PairF32FromE4M3)
      case (VCvtPkF32Fp8 | VCvtPkF32Bf8, Minifloat.E5M2)     => Some(VectorConvertOperation.PairF32FromE5M2)
      case (VCvtPkF32Fp8 | VCvtPkF32Bf8, Minifloat.E4M3FNUZ) => Some(VectorConvertOperation.PairF32FromE4M3Fnuz)
      case (VCvtPkF32Fp8 | VCvtPkF32Bf8, _)                  => Some(VectorConvertOperation.PairF32FromE5M2Fnuz)
      case _                                                 => None
    }

  /** The 16-bit operands of a 32-bit VOP1, VOP2 or VOPC encoding, bit `i` for source `i` and bit 3 for the destination,
    * whose VGPR numbers from 128 select the high half of the VGPR 128 below from GFX11 on.
    */
  private def trueHalves(machine: InstructionDecoder.Decoded, target: Target): Int =
    if (!halfOpSelectionDefined(target) || machine.format.isVOP3) 0
    else machine.definition match {
      case VFmaakF16 | VFmamkF16 | VMadakF16 | VMadmkF16 => 0xb
      case opcode                                        =>
        halfOpSelection(opcode).orElse(vectorComparison(opcode).collect {
          case VectorComparison(kind = VectorComparisonKind.Float16 | VectorComparisonKind.Signed16 |
              VectorComparisonKind.Unsigned16) => 0x3
          case VectorComparison(kind = VectorComparisonKind.ClassFloat16) => 0x1
        }).getOrElse(0)
    }

  /** A 32-bit encoding's source as the 16-bit operand bit `bit` of `halves` selects it. */
  private def trueHalf(source: InstructionSource, halves: Int, bit: Int): InstructionSource = source match {
    case InstructionSource.VectorRegister(index) if (halves & (1 << bit)) != 0 && index >= 128 =>
      InstructionSource.HighHalf(InstructionSource.VectorRegister((index - 128).unsafe[VectorRegisterIndex]))
    case other => other
  }

  /** The first source of a 32-bit encoding, its operand bit 0 selecting a half. */
  private def firstHalf(
      machine: InstructionDecoder.Decoded,
      target: Target
  ): Either[NonEmpty[String], InstructionSource] =
    operandSource(machine, machine(EncodingField.SRC0).toInt, machine.literal, target)
      .map(trueHalf(_, trueHalves(machine, target), 0))

  /** A 32-bit encoding's destination VGPR and whether a 16-bit result goes to its high half. */
  private def trueDestination(
      machine: InstructionDecoder.Decoded,
      target: Target
  ): Either[NonEmpty[String], (VectorRegisterIndex, Boolean)] =
    machine.vector(EncodingField.VDST).map(index =>
      if ((trueHalves(machine, target) & 8) != 0 && index >= 128)
        ((index - 128).unsafe[VectorRegisterIndex], true)
      else (index, false)
    )

  /** DPP's source 0, the VGPR it routes and whether the operation reads its high half. */
  private def routedHalf(
      machine: InstructionDecoder.Decoded,
      target: Target
  ): Either[NonEmpty[String], InstructionSource] =
    machine.vector(EncodingField.VSRC0).map(index =>
      if ((trueHalves(machine, target) & 1) != 0 && index >= 128) InstructionSource.HighHalf(InstructionSource.DPP)
      else InstructionSource.DPP
    )

  private def halfResult(
      opcode: Opcode,
      operation: Instruction,
      destination: VectorRegisterIndex,
      high: Boolean,
      target: Target
  ): Instruction =
    if (halfResultOpcode(opcode) && (high || preservesHighHalf(target)))
      Instruction.VHalfResult(operation, destination, high)
    else operation

  // Opcodes whose IR applies the VOP3 clamp bit itself.
  private lazy val ClampingVOP3Opcodes: Set[Opcode] = Set(
    VMadLegacyF32,
    VMacLegacyF32,
    VMullitF32,
    VMinLegacyF32,
    VMaxLegacyF32,
    VFmaLegacyF32,
    VFmacLegacyF32,
    VFmaDx9ZeroF32,
    VFmacDx9ZeroF32,
    VDot2cF32Bf16,
    VDot4cI32I8,
    VDot2cI32I16,
    VDot8cI32I4,
    VPkFmacF16,
    VSRcpF32,
    VRcpF32,
    VFrexpMantF32,
    VCvtF64F32,
    VCvtF32F16,
    VCvtF16F32,
    VCvtI16F16,
    VAddF16,
    VMulF16,
    VMinF16,
    VMaxF16,
    VAddF32,
    VSubF32,
    VSubrevF32,
    VMulF32,
    VMulLegacyF32,
    VMulDx9ZeroF32,
    VLdexpF32,
    VMaxF32,
    VMinF32,
    VMaxNumF32,
    VMinNumF32,
    VMinimumF32,
    VMaximumF32,
    VMinimumF16,
    VMaximumF16,
    VMadF32,
    VFmaF32,
    VMacF32,
    VFmacF32,
    VPkFmaF16,
    VPackB32F16,
    VAddU32,
    VAddNcU32,
    VSubU32,
    VSubNcU32,
    VSubrevU32,
    VSubrevNcU32,
    VAddU16,
    VAddNcU16,
    VSubU16,
    VSubrevU16,
    VSubNcU16,
    VAddCoU32,
    VAddCoCiU32,
    VAddcCoU32,
    VSubCoU32,
    VSubCoCiU32,
    VSubbCoU32,
    VSubrevCoU32,
    VSubrevCoCiU32,
    VSubbrevCoU32,
    VMadU16,
    VMadLegacyU16,
    VMadU32U24,
    VMadI32I24,
    VMadU64U32,
    VMadCoU64U32,
    VMadI64I32,
    VMadCoI64I32,
    VAddI32,
    VSubI32,
    VAddNcI32,
    VSubNcI32,
    VAddI16,
    VSubI16,
    VAddNcI16,
    VSubNcI16,
    VMadU32U16,
    VMadI32I16,
    VMadI16,
    VMadLegacyI16,
    VMadF16,
    VMadLegacyF16,
    VFmaLegacyF16,
    VMacF16,
    // Clamping cannot change these results.
    VMin3I16,
    VMin3U16,
    VMax3I16,
    VMax3U16,
    VMed3I16,
    VMed3U16,
    VAlignbitB32
  )

  /** The floating-point result width of VOP3 opcodes whose IR leaves the clamp to [[Instruction.VClampResult]]. */
  private def clampResultWidth(opcode: Opcode): Option[Int in (16, 32, 64)] = opcode match {
    case VDivScaleF32 => Some(32)
    case VDivScaleF64 => Some(64)
    case other        => vop3OutputWidth(other).filterNot(_ => ClampingVOP3Opcodes.contains(other))
  }

  // Integer and register-move VOP3P opcodes define no NEG_LO or NEG_HI.
  private lazy val UnmodifiedVOP3POpcodes: Set[Opcode] = Set(
    VPkMadU16,
    VPkMadI16,
    VAccvgprReadB32,
    VAccvgprRead,
    VAccvgprWriteB32,
    VAccvgprWrite,
    VDot4U32U8,
    VDot4I32I8,
    VDot2I32I16,
    VDot2U32U16,
    VDot8I32I4,
    VDot8U32U4,
    VDot2F32F16,
    VDot2F32Bf16,
    VDot4cI32I8,
    VDot2cI32I16,
    VDot8cI32I4,
    VDot2cF32F16
  )

  /** The elements a dot product that accumulates into its destination multiplies. */
  private def vectorBinaryDot(opcode: Opcode): Option[DotOperation] = opcode match {
    case VDot4cI32I8  => Some(DotOperation.SignedBytes)
    case VDot2cI32I16 => Some(DotOperation.SignedShorts)
    case VDot8cI32I4  => Some(DotOperation.SignedNibbles)
    case VDot2cF32F16 => Some(DotOperation.Halves)
    case _            => None
  }
  // These VOP3 encodings carry OP_SEL_HI in the OMOD bits.
  private lazy val PackedVOP3OpSelectHighOpcodes: Set[Opcode] = Set(
    VPkFmaF16,
    VPkAddU16,
    VPkAddF16,
    VPkMulLoU16,
    VPkMulF16,
    VPkMaxF16,
    VPkMinF16,
    VPkSubI16,
    VPkLshrrevB16
  )
  private case class Decoded(operation: Instruction, size: Int)
  case class DecodedProgram(
      operations: Vec[Instruction],
      instructionIndices: Map[Int, Int],
      instructionOffsets: Vec[Int]
  )

  /** Decode one instruction for decoder-focused tests without exposing the executable model publicly. */
  def decodeInstruction(
      code: Vec[Byte],
      target: Target
  ): Either[NonEmpty[String], Instruction] =
    Either.cond[NonEmpty[String], Unit](code.size >= 4, (), "truncated instruction at byte 0")
      .flatMap(_ => decode(Vec.toArray(code), 0, target, TargetDefaults.waveSize(target)).map(_.operation))

  private def selfContained(program: DecodedProgram): Boolean = {
    def constant(source: InstructionSource): Option[Long] = source match {
      case InstructionSource.Constant(value) => Some(value)
      case InstructionSource.Literal(value)  => Some(value)
      case _                                 => None
    }
    def relativeJump(index: Int, source: InstructionSource): Option[Int] =
      if (index < 4) None
      else (program.operations.slice(index - 4, index), source) match {
        // Prove the destination of the long-branch sequence PC + constant, on ordinary SGPRs only.
        case (
              Vec(
                Instruction.SGetPc(base, offset),
                Instruction.SBinary(ScalarBinaryOperation.AddI32, temporary, left, right),
                Instruction.SBinary(
                  ScalarBinaryOperation.AddU32,
                  low,
                  InstructionSource.Scalar(lowSource),
                  InstructionSource.Scalar(addend)
                ),
                Instruction.SBinary(
                  ScalarBinaryOperation.AddCarryU32,
                  high,
                  InstructionSource.Scalar(highSource),
                  zero
                )
              ),
              InstructionSource.Scalar(jump)
            )
            if base >= 0 && base < 105 && temporary >= 0 && temporary < 106 &&
              base == jump && low == base && lowSource == base && high.raw == base + 1 && highSource == high &&
              addend == temporary && temporary != base && temporary != high && constant(zero).contains(0L) =>
          for {
            a <- constant(left)
            b <- constant(right)
            target = offset.toLong + 4L + Bits.low32(a + b).toLong
            instruction <-
              Option.when(target <= Int.MaxValue.toLong)(target.toInt).flatMap(program.instructionIndices.get)
          } yield instruction
        case _ => None
      }
    val jumps = program.operations.zipWithIndex.collect {
      case (Instruction.SSetPc(source), index) => index -> relativeJump(index, source)
    }
    val destinations = program.operations.collect { case Instruction.Branch(_, target) => target } ++
      jumps.flatMap(_._2)
    !program.operations.exists {
      case Instruction.SSwapPc(_, _) | Instruction.BranchRelative(_, _) | Instruction.CallRelative(_, _) => true
      case _                                                                                             => false
    } && jumps.forall { case (index, target) =>
      target.nonEmpty && !destinations.exists(destination => destination > index - 4 && destination <= index)
    }
  }

  // Validate and coalesce linked text ranges in one ordered scan.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def linkedTextRanges(base: Long, code: Vec[Byte], sizes: Map[Long, Int]): Vec[(Int, Int)] = {
    val bytes  = Vec.toArray(code)
    val length = bytes.length
    val ranges = sizes.view.flatMap { case (address, size) =>
      val relative = address - base
      if (relative >= 0L && relative < length.toLong && size > 0) {
        val bounded = math.min(size.toLong, length.toLong - relative)
        Option.when(bounded > 0L)((relative.toInt, bounded.toInt))
      } else None
    }.toVec.sortBy(_._1)
    if (ranges.isEmpty) Vec.empty
    else {
      val normalised = Vec.newBuilder[(Int, Int)]
      var index      = 0
      while (index < ranges.size) {
        val (start, size) = ranges(index)
        val nextStart     = if (index + 1 < ranges.size) ranges(index + 1)._1 else length
        var end           = math.min(start + size, nextStart)
        // Inferred function sizes include the alignment gap to the next symbol; drop its zero words.
        while (
          end - start >= 4 && bytes(end - 4) == 0 && bytes(end - 3) == 0 && bytes(end - 2) == 0 && bytes(end - 1) == 0
        )
          end -= 4
        if (end > start) normalised += start -> (end - start)
        index += 1
      }
      normalised.result()
    }
  }

  // Decode variable-width instructions while retaining the first malformed range.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def decodeResolving(
      code: Vec[Byte],
      target: Target,
      waveSize: Int in (32, 64),
      functionRanges: Vec[(Int, Int)]
  )(external: (Instruction, NonEmpty[String]) => Either[NonEmpty[String], Instruction])
      : Either[NonEmpty[String], DecodedProgram] = {
    val bytes      = Vec.toArray(code)
    val ranges     = if (functionRanges.nonEmpty) functionRanges else Vec(0 -> bytes.length)
    val result     = Vec.newBuilder[(Int, Instruction)]
    var rangeIndex = 0
    var failure    = Option.empty[NonEmpty[String]]
    while (rangeIndex < ranges.length && failure.isEmpty) {
      val rangeStart = ranges(rangeIndex)._1
      val rangeEnd   = math.min(bytes.length, rangeStart + ranges(rangeIndex)._2)
      var offset     = rangeStart
      while (offset < rangeEnd && failure.isEmpty)
        if (offset > bytes.length - 4 || offset > rangeEnd - 4) {
          // Only an all-zero alignment tail may be skipped.
          val padding = (offset until rangeEnd).forall(bytes(_) == 0)
          if (padding) offset = rangeEnd
          else failure = Some(s"truncated instruction at byte $offset")
        } else
          decode(bytes, offset, target, waveSize) match {
            case Right(decoded) if offset + decoded.size <= rangeEnd =>
              result += offset -> decoded.operation
              offset += decoded.size
            case Right(_) =>
              failure = Some(s"instruction at byte $offset exceeds function range")
            case Left(_) if (offset until rangeEnd).forall(bytes(_) == 0) =>
              // An all-zero alignment gap after an inferred-size symbol is not code.
              offset = rangeEnd
            case Left(error) => failure = Some(s"byte $offset: $error")
          }
      rangeIndex += 1
    }
    failure.toLeft(Vec.from(result.result())).flatMap(indexed(_)).flatMap { decoded =>
      val indices = IntMap.from(decoded.view.zipWithIndex.map { case ((offset, _), index) => offset -> index })
      def indexAt(targetOffset: Long): Option[Int] =
        Option.when(targetOffset >= 0L && targetOffset <= Int.MaxValue.toLong)(targetOffset.toInt).flatMap(indices.get)
      inline def resolved(operation: Instruction, kind: String, offset: Int, displacement: Int)(
          inline internal: Int => Instruction
      ): Either[NonEmpty[String], Instruction] = {
        val targetOffset = offset.toLong + 4L + displacement.toLong * 4L
        indexAt(targetOffset) match {
          case Some(index) => Right(internal(index))
          case None        =>
            external(operation, s"$kind at byte $offset targets non-instruction byte $targetOffset")
        }
      }
      decoded.map { case (offset, operation) =>
        operation match {
          case Instruction.BranchRelative(condition, displacement) =>
            resolved(operation, "branch", offset, displacement)(Instruction.Branch(condition, _))
          case Instruction.SubvectorLoopRelative(begin, state, displacement) =>
            resolved(operation, "subvector loop", offset, displacement)(Instruction.SubvectorLoop(begin, state, _))
          case Instruction.ForkRelative(mask, displacement) =>
            resolved(operation, "fork", offset, displacement)(Instruction.Fork(mask, _))
          case Instruction.CallRelative(dest, displacement) =>
            resolved(operation, "call", offset, displacement)(Instruction.Call(dest, _))
          case other => Right(other)
        }
      }.sequence.map(operations => DecodedProgram(operations, indices, decoded.map(_._1)))
    }
  }

  def decodeProgram(
      code: Vec[Byte],
      target: Target,
      waveSize: Int in (32, 64),
      functionRanges: Vec[(Int, Int)] = Vec.empty
  ): Either[NonEmpty[String], DecodedProgram] =
    decodeResolving(code, target, waveSize, functionRanges)((_, error) => Left(error))

  private def decodeDeferringExternalBranches(
      code: Vec[Byte],
      target: Target,
      waveSize: Int in (32, 64),
      functionRanges: Vec[(Int, Int)]
  ): Either[NonEmpty[String], DecodedProgram] =
    decodeResolving(code, target, waveSize, functionRanges)((operation, _) => Right(operation))

  private val ScreenPartition: NonEmpty[String] =
    "v_screen_partition_4se_b32 reads the rasteriser's table of shader-engine masks, which is not modelled"

  /** Which instructions VSKIP skips in a program that can set it, by S_SETVSKIP or by writing MODE: the vector ones.
    */
  private def skippable(operations: Vec[Instruction]): Vec[Boolean] =
    if (
      !operations.exists {
        case Instruction.SetVectorSkip(_, _)       => true
        case Instruction.SSetMode(offset, size, _) => offset <= 28 && offset + size > 28
        case _                                     => false
      }
    ) Vec.empty
    else operations.map(!scalarIssued(_))

  /** Whether the scalar unit or the sequencer runs an instruction, which VSKIP does not skip. */
  private def scalarIssued(operation: Instruction): Boolean = operation match {
    case _: (Instruction.SLoad | Instruction.SMove | Instruction.SWriteExec | Instruction.SMoveRelative |
          Instruction.SMultiplyImmediate | Instruction.SMove64 | Instruction.SSetReg | Instruction.SSetMode |
          Instruction.SGetMode | Instruction.SGetPc | Instruction.SReadClock | Instruction.SReadWaveId |
          Instruction.SubvectorLoopRelative | Instruction.SubvectorLoop | Instruction.SSetPc |
          Instruction.SSwapPc |
          Instruction.SSaveExec | Instruction.SUnary | Instruction.SReciprocalF32 | Instruction.SFmaF32 |
          Instruction.SFmaF16 | Instruction.SFloat | Instruction.SFloatUnary | Instruction.SBinary |
          Instruction.SMinMax | Instruction.SBinary64 | Instruction.SGprIndex | Instruction.SCSelect |
          Instruction.SAddImmediate | Instruction.SFloatCompare | Instruction.SCmp | Instruction.SBufferLoad |
          Instruction.BranchRelative | Instruction.CallRelative | Instruction.Call | Instruction.Halt |
          Instruction.Branch | Instruction.BarrierSignal | Instruction.BarrierWait | Instruction.BarrierInit |
          Instruction.BarrierJoin | Instruction.Trap |
          Instruction.SStore | Instruction.SAtomic | Instruction.SCompareSwap | Instruction.ForkIndirect |
          Instruction.ForkRelative | Instruction.Fork | Instruction.Join | Instruction.SetVectorSkip) => true
    case Instruction.SGprIndexOff | Instruction.Barrier | Instruction.BarrierLeave | Instruction.Wait |
        Instruction.Sleep | Instruction.End |
        Instruction.CacheInvalidate | Instruction.CacheWritebackInvalidate => true
    case _ => false
  }

  /** GFX9 VGPR indexing mode offsets the operands M0[15:12] names by M0[7:0] from s_set_gpr_idx_on to
    * s_set_gpr_idx_off, which compilers emit around single moves in straight-line code; those lower to relative moves.
    */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def indexed(decoded: Vec[(Int, Instruction)]): Either[NonEmpty[String], Vec[(Int, Instruction)]] = {
    var mode = Option.empty[Int]
    decoded.traverse { (offset, instruction) =>
      val lowered = (mode, instruction) match {
        case (_, Instruction.SGprIndex(_, Some(bits), true)) =>
          mode = Some(bits)
          Right(instruction)
        case (Some(_), Instruction.SGprIndex(_, Some(bits), false)) =>
          mode = Some(bits)
          Right(instruction)
        case (_, Instruction.SGprIndexOff) =>
          mode = None
          Right(instruction)
        case (Some(bits), Instruction.VMove(dest, source @ InstructionSource.VectorRegister(_))) if bits == 1 =>
          Right(Instruction.VMoveRelative(dest, source, RelativeField.Low))
        case (Some(bits), Instruction.VMove(dest, source)) if bits == 8 =>
          Right(Instruction.VMoveRelativeDestination(dest, source, None, RelativeField.Low))
        case (_, Instruction.SGprIndex(index = _) | Instruction.Wait) => Right(instruction)
        case (Some(bits), other) if bits != 0                         =>
          Left[NonEmpty[String], Instruction](s"$other in VGPR indexing mode $bits is not implemented")
        case _ => Right(instruction)
      }
      lowered.map(offset -> _)
    }
  }

  extension (machine: InstructionDecoder.Decoded) {
    private def isSet(field: EncodingField): Boolean = machine(field) != 0
  }

  private def decode(
      bytes: Array[Byte],
      offset: Int,
      target: Target,
      waveSize: Int in (32, 64)
  ): Either[NonEmpty[String], Decoded] =
    InstructionDecoder.dual(bytes, offset, target)
      .map(_.flatMap(dual => decodeVOPD(dual, target).map(Decoded(_, dual.size))))
      .getOrElse(InstructionDecoder.decode(bytes, offset, target).flatMap(machine =>
        decodeMachine(machine, offset, target, waveSize).map(Decoded(_, machine.size))
      ))
      .flatMap(decoded => VMRegisterBounds.validate(decoded.operation, waveSize).map(_ => decoded))

  /** Instructions with no effect execution observes: trace and performance markers, sleeps and wake-ups for sleeps that
    * do not block, branches only a debugger's flags take, clearing a HALT or KILL bit that is never set, prefetches,
    * and cache maintenance with no scalar stores to write back. Invalidations make later loads read memory again.
    */
  private def hint(machine: InstructionDecoder.Decoded): Option[Instruction] = machine.definition match {
    case STtracedata | STtracedataImm | SIncperflevel | SDecperflevel | SWakeup | SWaitEvent | SWaitIdle | SIcacheInv |
        SCbranchCdbgsys | SCbranchCdbguser | SCbranchCdbgsysOrUser | SCbranchCdbgsysAndUser | SVersion |
        SWaitcntVmcnt | SWaitcntExpcnt | SDcacheWb | SDcacheWbVol | SDcacheDiscard | SDcacheDiscardX2 | SAtcProbe |
        SAtcProbeBuffer | SBufferPrefetchData | DsNop | VNop | VClrexcp | VPipeflush | SSleepVar | SPrefetchData |
        SPrefetchInst | SPrefetchDataPcRel | SPrefetchInstPcRel | SSingleuseVdst | SSetValuCoexecMode =>
      Some(Instruction.Wait)
    case SSethalt | SSetkill if (machine(EncodingField.SIMM16) & 1L) == 0L => Some(Instruction.Wait)
    case SSetkill                                                          => Some(Instruction.End)
    case SSethalt | SSendmsghalt                                           =>
      Some(Instruction.Halt(s"${machine.definition} halts the wave"))
    case SDcacheInv | SDcacheInvVol | SGl1Inv => Some(Instruction.CacheInvalidate)
    case _                                    => None
  }

  private def decodeMachine(
      machine: InstructionDecoder.Decoded,
      offset: Int,
      target: Target,
      waveSize: Int in (32, 64)
  ): Either[NonEmpty[String], Instruction] =
    hint(machine).fold(decodeFormat(machine, offset, target, waveSize))(Right(_))

  private def decodeFormat(
      machine: InstructionDecoder.Decoded,
      offset: Int,
      target: Target,
      waveSize: Int in (32, 64)
  ): Either[NonEmpty[String], Instruction] = machine.format match {
    case EncodingFormat.SMEM => decodeSMEM(machine, target)
    case EncodingFormat.SOP1 | EncodingFormat.SOP1_INST_LITERAL | EncodingFormat.SOP1_INST_LITERAL64 =>
      decodeSOP1(machine, offset, target)
    case EncodingFormat.SOPC | EncodingFormat.SOPC_INST_LITERAL | EncodingFormat.SOPC_INST_LITERAL64 =>
      decodeSOPC(machine, target)
    case EncodingFormat.SOPP                                    => decodeSOPP(machine, target)
    case EncodingFormat.SOPK | EncodingFormat.SOPK_INST_LITERAL => decodeSOPK(machine, target)
    case EncodingFormat.SOP2 | EncodingFormat.SOP2_INST_LITERAL | EncodingFormat.SOP2_INST_LITERAL64 =>
      decodeSOP2(machine, target)
    case EncodingFormat.VOP3P_MFMA                         => decodeMFMA(machine, target)
    case EncodingFormat.VOP3PX2 if target.isa == ISA.CDNA4 => decodeMFMA(machine, target)
    case format if format.base == EncodingFormat.VOP3P && machine.definition.mnemonic.raw == "v_mfma_ld_scale_b32" =>
      Left("v_mfma_ld_scale_b32 executes only as the scale prefix of v_mfma_scale_*, as one instruction")
    case format if format.base == EncodingFormat.VOP3P && matrix(machine) => decodeWaveMatrix(machine, target, waveSize)
    case format if format.base == EncodingFormat.VOP3P                    => decodeVOP3P(machine, target)
    case format if format.base == EncodingFormat.VOP3                     => decodeVOP3(machine, target)
    case EncodingFormat.DS | EncodingFormat.VDS => decodeDS(machine).map(zeroingHalves(_, target))
    case EncodingFormat.MUBUF | EncodingFormat.MTBUF | EncodingFormat.VBUFFER =>
      decodeBuffer(machine, target).map(zeroingHalves(_, target))
    case format if format.isImage                     => VMResourceDecoder.image(machine, target)
    case format if format.base == EncodingFormat.VOP1 => decodeVOP1(machine, target)
    case format if format.base == EncodingFormat.VOPC => decodeVOPC(machine, target)
    case format if format.base == EncodingFormat.VOP2 => decodeVOP2(machine, target)
    case EncodingFormat.FLAT | EncodingFormat.FLAT_GLBL | EncodingFormat.FLAT_GLOBAL | EncodingFormat.VGLOBAL |
        EncodingFormat.VFLAT | EncodingFormat.FLAT_SCRATCH | EncodingFormat.VSCRATCH =>
      decodeGlobalOrFlat(machine, target).map(zeroingHalves(_, target))
    case EncodingFormat.ILLEGAL => Right(Instruction.Halt("v_illegal raises an illegal instruction exception"))
    case format                 => Left(s"$format execution is not implemented")
  }

  /** ECC, which the CDNA parts enable, makes a 16-bit load write its whole register, zeroing the half it leaves. */
  private def zeroingHalves(instruction: Instruction, target: Target): Instruction = target.isa match {
    case ISA.CDNA1 | ISA.CDNA2 | ISA.CDNA3 | ISA.CDNA4 => instruction match {
        case load @ Instruction.Load(placement = LoadPlacement.Low) =>
          load.copy(placement = LoadPlacement.LowZeroingHigh)
        case load @ Instruction.Load(placement = LoadPlacement.High) =>
          load.copy(placement = LoadPlacement.HighZeroingLow)
        case other => other
      }
    case _ => instruction
  }

  /** V_PERMLANE16_SWAP and V_PERMLANE32_SWAP; the VOP3 form's OP_SEL bits 0 and 1 are FI and BOUND_CTRL. */
  private def permlaneSwap(
      opcode: Opcode,
      destination: VectorRegisterIndex,
      src: InstructionSource,
      selection: Int
  ): Either[NonEmpty[String], Instruction] = src match {
    case InstructionSource.VectorRegister(index) =>
      Right(Instruction.VPermlaneSwap(
        destination,
        index,
        if (opcode == VPermlane16SwapB32) 16 else 32,
        fetchInactive = (selection & 1) != 0,
        boundControl = (selection & 2) != 0
      ))
    case _ => Left(s"$opcode requires a vector-register source")
  }

  private def decodeVOPD(dual: InstructionDecoder.Dual, target: Target): Either[NonEmpty[String], Instruction] = {
    val three = dual.layout.format == EncodingFormat.VOPD3XY
    def register(what: String, value: Long): Either[NonEmpty[String], VectorRegisterIndex] =
      value.toInt.refine[VectorRegisterIndex]
        .leftMap[NonEmpty[String]](_ => s"dual-issue $what $value exceeds the vector register bank")
    def half(
        candidates: Vec[(Opcode, Metadata.Encoding)],
        destination: Long,
        source0: EncodingField,
        source1: EncodingField,
        source2: EncodingField,
        negate: EncodingField
    ): Either[NonEmpty[String], DualVector] =
      for {
        opcode    <- candidates.headOption.map(_._1).toRight[NonEmpty[String]]("missing dual-issue opcode")
        dest      <- register("destination", destination)
        second    <- register(source1.toString, dual(source1))
        left      <- vectorSource(dual(source0).toInt, dual.literal, target)
        operation <- opcode match {
          case VDualMovB32                                  => Right(DualVectorOperation.Move)
          case VDualAddF32                                  => Right(DualVectorOperation.AddF32)
          case VDualSubF32                                  => Right(DualVectorOperation.SubtractF32)
          case VDualSubrevF32                               => Right(DualVectorOperation.ReverseSubtractF32)
          case VDualMulF32                                  => Right(DualVectorOperation.MultiplyF32)
          case VDualFmacF32 | VDualFmaakF32 | VDualFmamkF32 => Right(DualVectorOperation.FmaF32)
          case VDualFmaF32                                  => Right(DualVectorOperation.FmaF32)
          case VDualMinF32 | VDualMinNumF32                 => Right(DualVectorOperation.MinimumF32)
          case VDualMaxF32 | VDualMaxNumF32                 => Right(DualVectorOperation.MaximumF32)
          case VDualCndmaskB32                              => Right(DualVectorOperation.ConditionalMask)
          case VDualAddNcU32                                => Right(DualVectorOperation.AddU32)
          case VDualLshlrevB32                              => Right(DualVectorOperation.ShiftLeft32)
          case VDualAndB32                                  => Right(DualVectorOperation.And32)
          case VDualMulDx9ZeroF32                           => Right(DualVectorOperation.MultiplyLegacyF32)
          case VDualDot2accF32F16                           => Right(DualVectorOperation.DotHalvesF32)
          case VDualDot2accF32Bf16                          => Right(DualVectorOperation.DotBFloat16F32)
          case VDualSubNcU32                                => Right(DualVectorOperation.SubtractU32)
          case VDualMinI32                                  => Right(DualVectorOperation.MinimumI32)
          case VDualMaxI32                                  => Right(DualVectorOperation.MaximumI32)
          case VDualLshrrevB32                              => Right(DualVectorOperation.ShiftRight32)
          case VDualAshrrevI32                              => Right(DualVectorOperation.ShiftRightArithmetic32)
          case VDualAddF64                                  => Right(DualVectorOperation.AddF64)
          case VDualMulF64                                  => Right(DualVectorOperation.MultiplyF64)
          case VDualFmaF64                                  => Right(DualVectorOperation.FmaF64)
          case VDualMinNumF64                               => Right(DualVectorOperation.MinimumF64)
          case VDualMaxNumF64                               => Right(DualVectorOperation.MaximumF64)
          case VDualBitop2B32                               => Right(DualVectorOperation.BitOperation2)
          case other => Left[NonEmpty[String], DualVectorOperation](s"dual-issue ${other.mnemonic} is not implemented")
        }
        third <- opcode match {
          // VOPD3 gives FMA and FMA_F64 a third source; FMAC accumulates into its destination
          case VDualFmaF32 | VDualFmaF64 if three => register(source2.toString, dual(source2)).map(Some(_))
          case _                                  => Right(None)
        }
        // a VOPD3 select names its lane mask, a single SGPR as dual issue runs in wave32
        condition <-
          if (three && opcode == VDualCndmaskB32)
            dual(source2).toInt.refine[ScalarRegisterIndex].map(mask => Some(InstructionSource.Scalar(mask)))
              .leftMap[NonEmpty[String]](_ => s"dual-issue $source2 is not a scalar register")
          else Right(None)
        embedded = InstructionSource.Literal(dual.literal.getOrElse(0L))
        right    = if (opcode == VDualFmamkF32) embedded else InstructionSource.VectorRegister(second)
        addend   =
          if (opcode == VDualFmaakF32) embedded
          else InstructionSource.VectorRegister(third.getOrElse(if (opcode == VDualFmamkF32) second else dest))
      } yield DualVector(
        operation,
        dest,
        left,
        right,
        addend,
        negate = if (three) dual(negate).toInt else 0,
        condition = condition,
        table = if (opcode == VDualBitop2B32) dual(source2).toInt else 0
      )
    for {
      left <- half(
        dual.x,
        dual(EncodingField.VDSTX),
        EncodingField.SRCX0,
        EncodingField.VSRCX1,
        EncodingField.VSRCX2,
        EncodingField.NEGX
      )
      right <- half(
        dual.y,
        dual.destinationY,
        EncodingField.SRCY0,
        EncodingField.VSRCY1,
        EncodingField.VSRCY2,
        EncodingField.NEGY
      )
    } yield Instruction.VDual(left, right)
  }

  private def decodeSMEM(machine: InstructionDecoder.Decoded, target: Target): Either[NonEmpty[String], Instruction] = {
    val opcode = machine.definition
    // RDNA3+ uses s124 as the null offset register; older RDNA uses s125.
    val nullOffset = if (target.isa.isGFX11OrLater) Fixed.M0
    else Fixed.NullSgpr
    val immediate = !machine.contains(EncodingField.IMM) || machine.isSet(EncodingField.IMM)
    // GFX6/GFX7 SMRD immediates and GFX7 trailing literals count dwords; an SGPR offset counts bytes. GFX8 byte
    // offsets are unsigned 20-bit; GFX9 onwards sign-extends the whole field.
    val offset = target.isa match {
      case ISA.GFX6 | ISA.GFX7 =>
        if (immediate) machine(EncodingField.OFFSET) * 4 else machine.literal.fold(0L)(Bits.low32(_).toLong * 4)
      case _ if !immediate => 0L
      case ISA.GFX8        => machine(EncodingField.OFFSET) & 0xfffffL
      case _               => machine.signed(EncodingField.OFFSET)
    }
    if (opcode == SGetWaveidInWorkgroup) machine.scalar(EncodingField.SDATA).map(Instruction.SReadWaveId(_))
    else if (opcode == SMemtime || opcode == SMemrealtime)
      machine.scalar(EncodingField.SDATA).map(Instruction.SReadClock(
        _,
        if (opcode == SMemtime) Clock.Shader else Clock.RealTime
      ))
    else {
      val scalarOffset =
        if (!machine.contains(EncodingField.IMM))
          if (machine(EncodingField.SOFFSET) != nullOffset.toLong) machine.scalar(EncodingField.SOFFSET).map(Some(_))
          else Right(None)
        else if (machine.isSet(EncodingField.SOFFSET_EN)) machine.scalar(EncodingField.SOFFSET).map(Some(_))
        else if (!immediate && machine.literal.isEmpty) machine.scalar(EncodingField.OFFSET).map(Some(_))
        else Right(None)
      if (opcode.mnemonic.raw.startsWith("s_buffer_load"))
        scalarOffset.flatMap(VMResourceDecoder.scalarBuffer(machine, offset, _))
      else decodeScalarWrite(machine, offset, scalarOffset).getOrElse(
        decodeScalarLoad(machine, opcode, offset, scalarOffset)
      )
    }
  }

  private val ScalarAtomics: Map[String, AtomicOperation] = Map(
    "swap" -> AtomicOperation.Exchange,
    "add"  -> AtomicOperation.Add,
    "sub"  -> AtomicOperation.Subtract,
    "smin" -> AtomicOperation.MinimumSigned,
    "umin" -> AtomicOperation.MinimumUnsigned,
    "smax" -> AtomicOperation.MaximumSigned,
    "umax" -> AtomicOperation.MaximumUnsigned,
    "and"  -> AtomicOperation.And,
    "or"   -> AtomicOperation.Or,
    "xor"  -> AtomicOperation.Xor,
    "inc"  -> AtomicOperation.IncrementWrap,
    "dec"  -> AtomicOperation.DecrementWrap
  )

  /** The scalar stores and atomics of GFX8 to RDNA1, to global memory or through a buffer descriptor; atomics return
    * the old value under GLC.
    */
  private def decodeScalarWrite(
      machine: InstructionDecoder.Decoded,
      offset: Long,
      scalarOffsets: Either[NonEmpty[String], Option[ScalarRegisterIndex]]
  ): Option[Either[NonEmpty[String], Instruction]] = {
    val name     = machine.definition.mnemonic.raw
    val buffer   = name.startsWith("s_buffer_")
    val access   = name.stripPrefix("s_buffer_").stripPrefix("s_")
    val wide     = access.endsWith("_x2")
    val bytes    = if (wide) 8: Int in (4, 8) else 4: Int in (4, 8)
    val returned = machine.isSet(if (machine.contains(EncodingField.GLC)) EncodingField.GLC else EncodingField.SC0)
    def address  = for {
      scalarOffset <- scalarOffsets
      base         <- machine.scalar(EncodingField.SBASE)
      data         <- machine.scalar(EncodingField.SDATA)
    } yield (data, ScalarAddress(base, offset, scalarOffset, buffer))
    access match {
      case scratch if scratch.startsWith("scratch_") =>
        Some(Left(
          s"${machine.definition} addresses the wave's scratch linearly from FLAT_SCRATCH, which the per-lane private " +
            "memory does not lay out"
        ))
      case "store_dword"                          => Some(address.map((data, at) => Instruction.SStore(data, 1, at)))
      case "store_dwordx2"                        => Some(address.map((data, at) => Instruction.SStore(data, 2, at)))
      case "store_dwordx4"                        => Some(address.map((data, at) => Instruction.SStore(data, 4, at)))
      case atomic if atomic.startsWith("atomic_") =>
        atomic.stripPrefix("atomic_").stripSuffix("_x2") match {
          case "cmpswap" => Some(address.map((data, at) => Instruction.SCompareSwap(data, at, bytes, returned)))
          case operation =>
            ScalarAtomics.get(operation).map(found =>
              address.map((data, at) => Instruction.SAtomic(found, data, at, bytes, returned))
            )
        }
      case _ => None
    }
  }

  private def decodeScalarLoad(
      machine: InstructionDecoder.Decoded,
      opcode: Opcode,
      offset: Long,
      scalarOffsets: Either[NonEmpty[String], Option[ScalarRegisterIndex]]
  ): Either[NonEmpty[String], Instruction] =
    for {
      count <- opcode match {
        case SLoadU8 | SLoadU16 | SLoadI8 | SLoadI16 | SLoadDword | SLoadB32 => Right(1: Int in (1, 2, 3, 4, 8, 16))
        case SLoadDwordx2 | SLoadB64                                         => Right(2: Int in (1, 2, 3, 4, 8, 16))
        case SLoadB96                                                        => Right(3: Int in (1, 2, 3, 4, 8, 16))
        case SLoadDwordx4 | SLoadB128                                        => Right(4: Int in (1, 2, 3, 4, 8, 16))
        case SLoadDwordx8 | SLoadB256                                        => Right(8: Int in (1, 2, 3, 4, 8, 16))
        case SLoadDwordx16 | SLoadB512                                       => Right(16: Int in (1, 2, 3, 4, 8, 16))
        case other => Left[NonEmpty[String], Nothing](s"$other SMEM execution is not implemented")
      }
      scalarOffset <- scalarOffsets
      data         <- machine.scalar(EncodingField.SDATA)
      base         <- machine.scalar(EncodingField.SBASE)
    } yield Instruction.SLoad(
      data,
      count,
      base,
      offset,
      scalarOffset,
      opcode match {
        case SLoadU8 | SLoadI8   => 1
        case SLoadU16 | SLoadI16 => 2
        case _                   => 4
      },
      opcode == SLoadI8 || opcode == SLoadI16
    )

  /** Named barrier objects, which LLVM's execution synchronization model gives GFX12.5 and not RDNA4. */
  private def namedBarriers(target: Target): Boolean = target.isa == ISA.CDNA5

  // GFX12 encodes M0 as scalar 125
  private val NamedBarrierM0: ScalarRegisterIndex = 125

  private def fromM0(machine: InstructionDecoder.Decoded): Boolean =
    machine(EncodingField.SSRC0).toInt == NamedBarrierM0.raw

  private def decodeSOP1(
      machine: InstructionDecoder.Decoded,
      offset: Int,
      target: Target
  ): Either[NonEmpty[String], Instruction] =
    for {
      src         <- scalarSource(machine(EncodingField.SSRC0).toInt, machine.literal, target)
      destination <- machine.scalar(EncodingField.SDST)
      operation   <- machine.definition match {
        case SBarrierSignal        => Right(Instruction.BarrierSignal(src, isFirst = false, fromM0(machine)))
        case SBarrierSignalIsfirst => Right(Instruction.BarrierSignal(src, isFirst = true, fromM0(machine)))
        case SBarrierInit if namedBarriers(target) =>
          Right(Instruction.BarrierInit(src, fromM0(machine), InstructionSource.Scalar(NamedBarrierM0)))
        case SBarrierJoin if namedBarriers(target)     => Right(Instruction.BarrierJoin(src, fromM0(machine)))
        case SGetBarrierState if namedBarriers(target) =>
          Left[NonEmpty[String], Instruction](
            "s_get_barrier_state is not implemented: no public document lays out the barrier state it returns"
          )
        case SWakeupBarrier =>
          Left[NonEmpty[String], Instruction](
            "s_wakeup_barrier is not implemented: no public document says what a wave woken from a barrier does"
          )
        case SMovB32  => Right(Instruction.SMove(destination, src))
        case SMovB64  => Right(Instruction.SMove64(destination, src))
        case SCmovB32 =>
          Right(Instruction.SCSelect(destination, src, InstructionSource.Scalar(destination), 32))
        case SCmovB64 =>
          Right(Instruction.SCSelect(destination, src, InstructionSource.Scalar(destination), 64))
        case SGetpcB64                           => Right(Instruction.SGetPc(destination, offset))
        case SSetGprIdxIdx                       => Right(Instruction.SGprIndex(Some(src), None, enable = false))
        case SAndn2WrexecB32 | SAndNot1WrexecB32 => Right(Instruction.SWriteExec(notExec = true, destination, src, 32))
        case SAndn2WrexecB64 | SAndNot1WrexecB64 => Right(Instruction.SWriteExec(notExec = true, destination, src, 64))
        case SAndn1WrexecB32 | SAndNot0WrexecB32 => Right(Instruction.SWriteExec(notExec = false, destination, src, 32))
        case SAndn1WrexecB64 | SAndNot0WrexecB64 => Right(Instruction.SWriteExec(notExec = false, destination, src, 64))
        case SMovrelsB32                         =>
          Right(Instruction.SMoveRelative(destination, src, 1, relativeSource = true, relativeDest = false))
        case SMovrelsB64 =>
          Right(Instruction.SMoveRelative(destination, src, 2, relativeSource = true, relativeDest = false))
        case SMovreldB32 =>
          Right(Instruction.SMoveRelative(destination, src, 1, relativeSource = false, relativeDest = true))
        case SMovreldB64 =>
          Right(Instruction.SMoveRelative(destination, src, 2, relativeSource = false, relativeDest = true))
        case SMovrelsd2B32 =>
          Right(Instruction.SMoveRelative(destination, src, 1, relativeSource = true, relativeDest = true))
        case SRfeB64      => Right(Instruction.SSetPc(src))
        case SCbranchJoin => Right(Instruction.Join(src))
        // the VM holds every VGPR, so a dynamic allocation always succeeds
        case SAllocVgpr =>
          Right(Instruction.SCmp(
            ScalarCompareOperation.EqualU32,
            InstructionSource.Constant(0L),
            InstructionSource.Constant(0L)
          ))
        case SSendmsgRtnB64 if realTime(machine(EncodingField.SSRC0)) =>
          Right(Instruction.SReadClock(destination, Clock.RealTime))
        case SSetpcB64       => Right(Instruction.SSetPc(src))
        case SSwappcB64      => Right(Instruction.SSwapPc(destination, src))
        case SAndSaveexecB32 =>
          Right(Instruction.SSaveExec(SaveExecOperation.And, destination, src, 32))
        case SAndSaveexecB64 =>
          Right(Instruction.SSaveExec(SaveExecOperation.And, destination, src, 64))
        case SAndn2SaveexecB32 | SAndNot1SaveexecB32 =>
          Right(Instruction.SSaveExec(SaveExecOperation.AndNot, destination, src, 32))
        case SAndn2SaveexecB64 | SAndNot1SaveexecB64 =>
          Right(Instruction.SSaveExec(SaveExecOperation.AndNot, destination, src, 64))
        case SOrSaveexecB32 =>
          Right(Instruction.SSaveExec(SaveExecOperation.Or, destination, src, 32))
        case SOrSaveexecB64 =>
          Right(Instruction.SSaveExec(SaveExecOperation.Or, destination, src, 64))
        case SXorSaveexecB32 =>
          Right(Instruction.SSaveExec(SaveExecOperation.Xor, destination, src, 32))
        case SXorSaveexecB64 =>
          Right(Instruction.SSaveExec(SaveExecOperation.Xor, destination, src, 64))
        case SOrn2SaveexecB32 | SOrNot1SaveexecB32 =>
          Right(Instruction.SSaveExec(SaveExecOperation.OrNot, destination, src, 32))
        case SOrn2SaveexecB64 | SOrNot1SaveexecB64 =>
          Right(Instruction.SSaveExec(SaveExecOperation.OrNot, destination, src, 64))
        case SNandSaveexecB32 => Right(Instruction.SSaveExec(SaveExecOperation.Nand, destination, src, 32))
        case SNandSaveexecB64 => Right(Instruction.SSaveExec(SaveExecOperation.Nand, destination, src, 64))
        case SNorSaveexecB32  => Right(Instruction.SSaveExec(SaveExecOperation.Nor, destination, src, 32))
        case SNorSaveexecB64  => Right(Instruction.SSaveExec(SaveExecOperation.Nor, destination, src, 64))
        case SXnorSaveexecB32 => Right(Instruction.SSaveExec(SaveExecOperation.Xnor, destination, src, 32))
        case SXnorSaveexecB64 => Right(Instruction.SSaveExec(SaveExecOperation.Xnor, destination, src, 64))
        case SAndn1SaveexecB32 | SAndNot0SaveexecB32 =>
          Right(Instruction.SSaveExec(SaveExecOperation.NotAnd, destination, src, 32))
        case SAndn1SaveexecB64 | SAndNot0SaveexecB64 =>
          Right(Instruction.SSaveExec(SaveExecOperation.NotAnd, destination, src, 64))
        case SOrn1SaveexecB32 | SOrNot0SaveexecB32 =>
          Right(Instruction.SSaveExec(SaveExecOperation.NotOr, destination, src, 32))
        case SOrn1SaveexecB64 | SOrNot0SaveexecB64 =>
          Right(Instruction.SSaveExec(SaveExecOperation.NotOr, destination, src, 64))
        case other =>
          scalarUnaryOperation(other)
            .map(operation => Instruction.SUnary(operation, destination, src))
            .orElse(scalarFloatUnaryOperation(other).map(Instruction.SFloatUnary(_, destination, half(other, src))))
            .toRight[NonEmpty[String]](s"$other SOP1 execution is not implemented")
      }
    } yield operation

  private def decodeSOP2(machine: InstructionDecoder.Decoded, target: Target): Either[NonEmpty[String], Instruction] = {
    val opcode = machine.definition
    // one literal dword fills K and any literal source
    def constant = machine.literal.map(InstructionSource.Literal(_))
      .toRight[NonEmpty[String]](s"$opcode is missing its literal operand")
    for {
      srcA        <- scalarSource(machine(EncodingField.SSRC0).toInt, machine.literal, target)
      srcB        <- scalarSource(machine(EncodingField.SSRC1).toInt, machine.literal, target)
      destination <- machine.scalar(EncodingField.SDST)
      operation   <- opcode match {
        case SFmacF32 => Right(Instruction.SFmaF32(destination, srcA, srcB, InstructionSource.Scalar(destination)))
        case SFmacF16 => Right(Instruction.SFmaF16(
            destination,
            half(opcode, srcA),
            half(opcode, srcB),
            InstructionSource.Scalar(destination)
          ))
        case SFmaakF32     => constant.map(Instruction.SFmaF32(destination, srcA, srcB, _))
        case SFmamkF32     => constant.map(Instruction.SFmaF32(destination, srcA, _, srcB))
        case SCselectB32   => Right(Instruction.SCSelect(destination, srcA, srcB, 32))
        case SCselectB64   => Right(Instruction.SCSelect(destination, srcA, srcB, 64))
        case SCbranchGFork => Right(Instruction.ForkIndirect(srcA, srcB))
        // a trap handler's return, which also restores the state the trap saved; kernels run no trap handler
        case SRfeRestoreB64 => Right(Instruction.SSetPc(srcA))
        case other          =>
          scalarBinary64Operation(other)
            .map(operation => Instruction.SBinary64(operation, destination, srcA, srcB))
            .orElse(
              scalarBinaryOperation(other)
                .map(operation => Instruction.SBinary(operation, destination, srcA, srcB))
            )
            .orElse(
              scalarMinMaxOperation(other)
                .map(operation => Instruction.SMinMax(operation, destination, srcA, srcB))
            )
            .orElse(
              scalarFloatOperation(other)
                .map(Instruction.SFloat(_, destination, half(other, srcA), half(other, srcB)))
            )
            .toRight[NonEmpty[String]](s"$other SOP2 execution is not implemented")
      }
    } yield operation
  }

  private def hardwareRegisterFields(target: Target): Option[Map[EncodingField, OperandEncoding.NamedField]] =
    OperandEncoding.Catalogue.forKind(target.isa, OperandKind.HWREG)
      .map(_.fields.map(field => field.field.name -> field).toMap)

  /** MSG_RTN_GET_REALTIME, which LLVM sends for the steady counter on every target from GFX11. */
  private val RealTimeMessage = 0x83L

  /** Whether a returning message asks for the real-time clock. */
  private def realTime(message: Long): Boolean = message == RealTimeMessage

  /** Whether a HWREG selector names exactly `bits` bits from `first` of the register `register`. */
  private def selects(encoded: Long, target: Target, register: String, first: Long, bits: Long): Boolean = (for {
    fields <- hardwareRegisterFields(target)
    id     <- fields.get(EncodingField.ID)
    name   <- id.names.get(register)
    offset <- fields.get(EncodingField.OFFSET)
    size   <- fields.get(EncodingField.SIZE)
  } yield id.field.get(encoded) == name.toLong && offset.field.get(encoded) == first &&
    size.field.get(encoded) == bits - 1L).getOrElse(false)

  /** Whether a HWREG selector reads the whole 20-bit shader cycle counter. */
  private def shaderCycles(encoded: Long, target: Target): Boolean =
    selects(encoded, target, "HW_REG_SHADER_CYCLES", 0L, 20L)

  /** Whether a HWREG selector reads TRAPSTS.MEM_VIOL alone, the flag LLVM tests to retry a GWS operation the hardware
    * declined before GFX10.
    */
  private def memoryViolation(encoded: Long, target: Target): Boolean =
    selects(encoded, target, "HW_REG_TRAPSTS", 8L, 1L)

  /** The MODE bits a HWREG selector names, clipped to MODE's 32 bits, or None when it names another register. */
  private def modeField(encoded: Long, target: Target): Option[(Int in 0 ~ 31, Int in 1 ~ 32)] = for {
    fields <- hardwareRegisterFields(target)
    id     <- fields.get(EncodingField.ID)
    // GFX12 renames the register HW_REG_WAVE_MODE.
    name <- id.names.get("HW_REG_MODE").orElse(id.names.get("HW_REG_WAVE_MODE"))
    if id.field.get(encoded) == name.toLong
    offset <- fields.get(EncodingField.OFFSET)
    size   <- fields.get(EncodingField.SIZE)
    first  <- offset.field.get(encoded).toInt.refine[Int in 0 ~ 31].toOption
    count  <- math.min(size.field.get(encoded).toInt + 1, 32 - first).refine[Int in 1 ~ 32].toOption
  } yield (first, count)

  private def decodeSOPK(machine: InstructionDecoder.Decoded, target: Target): Either[NonEmpty[String], Instruction] = {
    val immediate = machine.signed(EncodingField.SIMM16)
    machine.definition match {
      // Memory and LDS operations finish synchronously before the next instruction.
      case SWaitcntVscnt | SWaitcntLgkmcnt => Right(Instruction.Wait)
      case SMovkI32                        =>
        machine.scalar(EncodingField.SDST).map(Instruction.SMove(_, InstructionSource.Constant(immediate)))
      case SCmovkI32 =>
        machine.scalar(EncodingField.SDST).map(destination =>
          Instruction.SCSelect(
            destination,
            InstructionSource.Constant(immediate),
            InstructionSource.Scalar(destination),
            32
          )
        )
      case SMulkI32 =>
        machine.scalar(EncodingField.SDST).map(Instruction.SMultiplyImmediate(_, immediate.toInt))
      case SAddkI32 | SAddkCoI32 =>
        machine.scalar(EncodingField.SDST).map(Instruction.SAddImmediate(_, immediate.toInt))
      case SGetregB32 =>
        val encoded = machine(EncodingField.SIMM16)
        val value   = for {
          fields      <- hardwareRegisterFields(target)
          id          <- fields.get(EncodingField.ID)
          sharedBases <- id.names.get("HW_REG_SH_MEM_BASES")
          if id.field.get(encoded) == sharedBases.toLong
          offset <- fields.get(EncodingField.OFFSET)
          size   <- fields.get(EncodingField.SIZE)
        } yield (VMAddressSpace.HardwareBases >>> offset.field.get(encoded).toInt) &
          (-1L >>> (64 - size.field.get(encoded).toInt - 1))
        modeField(encoded, target) match {
          case Some((offset, size)) => machine.scalar(EncodingField.SDST).map(Instruction.SGetMode(_, offset, size))
          case None if shaderCycles(encoded, target) =>
            machine.scalar(EncodingField.SDST).map(Instruction.SReadClock(_, Clock.ShaderCycles))
          // a memory violation faults the launch and GWS operations always complete, so the flag stays clear
          case None if memoryViolation(encoded, target) =>
            machine.scalar(EncodingField.SDST).map(Instruction.SMove(_, InstructionSource.Constant(0)))
          case None =>
            for {
              value       <- value.toRight[NonEmpty[String]](s"unsupported hardware register selector $encoded")
              destination <- machine.scalar(EncodingField.SDST)
            } yield Instruction.SMove(destination, InstructionSource.Constant(value.toInt))
        }
      case setreg @ (SSetregB32 | SSetregImm32B32) =>
        val encoded = machine(EncodingField.SIMM16)
        modeField(encoded, target) match {
          case Some((offset, size)) =>
            val source =
              if (setreg == SSetregImm32B32)
                machine.literal.map(InstructionSource.Constant(_)).toRight[NonEmpty[String]](s"$setreg has no literal")
              else machine.scalar(EncodingField.SDST).map(InstructionSource.Scalar(_))
            source.map(Instruction.SSetMode(offset, size, _))
          case None => machine.scalar(EncodingField.SDST).map(Instruction.SSetReg(encoded.toInt, _))
        }
      case compare @ (SCmpkEqI32 | SCmpkLgI32 | SCmpkGtI32 | SCmpkGeI32 | SCmpkLtI32 | SCmpkLeI32 | SCmpkEqU32 |
          SCmpkLgU32 | SCmpkGtU32 | SCmpkGeU32 | SCmpkLtU32 | SCmpkLeU32) =>
        for {
          (operation, signed) <- scalarImmediateCompareOperation(compare)
            .toRight[NonEmpty[String]](s"$compare SOPK execution is not implemented")
          register <- machine.scalar(EncodingField.SDST)
        } yield Instruction.SCmp(
          operation,
          InstructionSource.Scalar(register),
          InstructionSource.Constant(if (signed) immediate else machine(EncodingField.SIMM16))
        )
      case SCallB64 => machine.scalar(EncodingField.SDST).map(Instruction.CallRelative(_, immediate.toInt))
      case begin @ (SSubvectorLoopBegin | SSubvectorLoopEnd) =>
        machine.scalar(EncodingField.SDST).map(
          Instruction.SubvectorLoopRelative(begin == SSubvectorLoopBegin, _, immediate.toInt)
        )
      case SCbranchIFork =>
        machine.scalar(EncodingField.SDST).map(mask =>
          Instruction.ForkRelative(InstructionSource.Scalar(mask), machine.signed(EncodingField.SIMM16).toInt)
        )
      case other => Left[NonEmpty[String], Nothing](s"$other SOPK execution is not implemented")
    }
  }

  private def decodeSOPC(machine: InstructionDecoder.Decoded, target: Target): Either[NonEmpty[String], Instruction] =
    for {
      srcA      <- scalarSource(machine(EncodingField.SSRC0).toInt, machine.literal, target)
      srcB      <- scalarSource(machine(EncodingField.SSRC1).toInt, machine.literal, target)
      operation <- machine.definition match {
        case SSetGprIdxOn =>
          Right(Instruction.SGprIndex(Some(srcA), Some(machine(EncodingField.SSRC1).toInt & 0xf), enable = true))
        case SSetvskip => Right(Instruction.SetVectorSkip(srcA, srcB))
        case other     =>
          scalarCompareOperation(other)
            .map(operation => Instruction.SCmp(operation, srcA, srcB))
            .orElse(vectorComparison(other).filter(operation =>
              operation.kind == VectorComparisonKind.Float16 || operation.kind == VectorComparisonKind.Float32
            ).map(operation => Instruction.SFloatCompare(operation, srcA, srcB)))
            .toRight[NonEmpty[String]](s"$other SOPC execution is not implemented")
      }
    } yield operation

  /** The messages before GFX11 other than MSG_INTERRUPT: wave saves and generation stalls only steer scheduling, and a
    * thread trace of the PC reaches no lane; the rest serve the graphics pipeline, a trap handler or the driver.
    */
  private val LegacyMessages: Set[ISA] = Set(
    ISA.GFX6,
    ISA.GFX7,
    ISA.GFX8,
    ISA.GFX9,
    ISA.CDNA1,
    ISA.CDNA2,
    ISA.CDNA3,
    ISA.CDNA4,
    ISA.RDNA1,
    ISA.RDNA2
  )

  private def legacyMessage(immediate: Int, target: Target): Either[NonEmpty[String], Instruction] = {

    val gfx9 = target.isa != ISA.GFX6 && target.isa != ISA.GFX7 && target.isa != ISA.GFX8
    (immediate & 0xf, (immediate >>> 4) & 7) match {
      case (4, _) if target.isa != ISA.GFX6 && target.isa != ISA.GFX7 => Right(Instruction.Wait)
      case (5, _) if gfx9                                             => Right(Instruction.Wait)
      case (6, _) if gfx9 => Right(Instruction.Halt("s_sendmsg MSG_HALT_WAVES halts the waves, which nothing resumes"))
      case (2 | 3, _)     =>
        Left("s_sendmsg MSG_GS and MSG_GS_DONE signal the geometry pipeline, which a compute dispatch has none of")
      case (7 | 9, _) if gfx9 =>
        Left("s_sendmsg MSG_ORDERED_PS_DONE and MSG_GS_ALLOC_REQ serve graphics shader stages")
      case (8, _) if gfx9 && !target.isa.isRDNA =>
        Left("s_sendmsg MSG_EARLY_PRIM_DEALLOC serves the geometry shader stage")
      case (10, _) if gfx9 =>
        Left("s_sendmsg MSG_GET_DOORBELL returns the queue's doorbell to a trap handler, which kernels do not run")
      case (11, _) if target.isa.isRDNA =>
        Left("s_sendmsg MSG_GET_DDID returns the debugger's dispatch ID to a trap handler, which kernels do not run")
      case (15, 4) => Right(Instruction.Wait)
      case (15, 1) =>
        Right(Instruction.Halt("s_sendmsg MSG_SYSMSG ECC_ERR_INTERRUPT raises an ECC error interrupt to the host"))
      case (15, operation) =>
        Left(s"s_sendmsg MSG_SYSMSG operation $operation reads registers for or acknowledges a host trap")
      case (message, _) => Left(s"s_sendmsg $message names no message on ${target.name}")
    }
  }

  private def decodeSOPP(machine: InstructionDecoder.Decoded, target: Target): Either[NonEmpty[String], Instruction] = {

    val immediate                                = machine(EncodingField.SIMM16).toInt
    def branch(condition: ScalarBranchCondition) =
      Right(Instruction.BranchRelative(condition, machine.signed(EncodingField.SIMM16).toInt))
    machine.definition match {
      // Operations complete synchronously; waits only flush staged host writes.
      case SWaitcnt | SWaitcntVscnt | SWaitcntDepctr | SDelayAlu | SWaitKmcnt | SWaitAlu |
          SWaitLoadcnt | SWaitStorecnt | SWaitDscnt | SWaitLoadcntDscnt | SWaitStorecntDscnt |
          SWaitSamplecnt | SWaitBvhcnt | SWaitExpcnt =>
        Right(Instruction.Wait)
      case SDenormMode =>
        Right(Instruction.SSetMode(ModeRegister.DenormalShift, 4, InstructionSource.Constant(immediate & 0xf)))
      case SRoundMode => Right(Instruction.SSetMode(0, 4, InstructionSource.Constant(immediate & 0xf)))
      case SSendmsg if immediate == 3 && target.isa.isGFX11OrLaterGraphics =>
        // MSG_DEALLOC_VGPRS releases hardware registers after their last use.
        Right(Instruction.Wait)
      // MSG_INTERRUPT asks the host to service a hostcall, such as printf's, and the wave waits for the answer
      case SSendmsg if (immediate & 0xf) == 1 =>
        Right(Instruction.Halt("s_sendmsg MSG_INTERRUPT waits for a hostcall service, which no host provides"))
      case SSendmsg if LegacyMessages(target.isa)          => legacyMessage(immediate, target)
      case SNop | SInstPrefetch | SSetInstPrefetchDistance => Right(Instruction.Wait)
      // Clause grouping has no observable effect.
      case SClause => Right(Instruction.Wait)
      // Wave priority only affects physical scheduling.
      case SSetprio     => Right(Instruction.Wait)
      case SBarrierWait => Right(Instruction.BarrierWait(machine.signed(EncodingField.SIMM16).toInt))
      case SBarrierLeave if namedBarriers(target) => Right(Instruction.BarrierLeave)
      case SBarrier                               => Right(Instruction.Barrier)
      case SEndpgm                                => Right(Instruction.End)
      // the end of a wave a context-switch trap handler saved, which then has nothing left to run
      case SEndpgmSaved         => Right(Instruction.End)
      case SEndpgmOrderedPsDone =>
        Left("s_endpgm_ordered_ps_done ends a pixel shader in primitive order, which a compute dispatch has none of")
      case SSendmsg if (immediate & 0xff) == 6 && target.isa != ISA.RDNA4 =>
        Right(Instruction.Halt("s_sendmsg MSG_HALT_WAVES halts the device's waves"))
      // the other messages address the graphics pipeline, wave launch or the system, none of which a compute
      // dispatch answers
      case SSendmsg => Right(Instruction.Wait)
      // Linked failure handlers decode without being called; the trap faults only when executed.
      case STrap => Right(Instruction.Trap(immediate))
      // s_code_end pads the end of linked GFX10+ text; it is not a terminator.
      case SCodeEnd       => Right(Instruction.Wait)
      case SBranch        => branch(ScalarBranchCondition.Always)
      case SCbranchScc0   => branch(ScalarBranchCondition.SccZero)
      case SCbranchScc1   => branch(ScalarBranchCondition.SccNonZero)
      case SCbranchVccz   => branch(ScalarBranchCondition.VccZero)
      case SCbranchVccnz  => branch(ScalarBranchCondition.VccNonZero)
      case SCbranchExecz  => branch(ScalarBranchCondition.ExecZero)
      case SCbranchExecnz => branch(ScalarBranchCondition.ExecNonZero)
      case SSleep         => Right(Instruction.Sleep)
      case SSetGprIdxMode => Right(Instruction.SGprIndex(None, Some(immediate & 0xf), enable = false))
      case SSetGprIdxOff  => Right(Instruction.SGprIndexOff)
      case other          => Left[NonEmpty[String], Nothing](s"$other SOPP execution is not implemented")
    }
  }

  private val SourceModifierFields =
    Vec(EncodingField.SRC0_NEG, EncodingField.SRC0_ABS, EncodingField.SRC1_NEG, EncodingField.SRC1_ABS)

  private val DPP8Selectors = Vec(
    EncodingField.LANE_SEL_0,
    EncodingField.LANE_SEL_1,
    EncodingField.LANE_SEL_2,
    EncodingField.LANE_SEL_3,
    EncodingField.LANE_SEL_4,
    EncodingField.LANE_SEL_5,
    EncodingField.LANE_SEL_6,
    EncodingField.LANE_SEL_7
  )

  private val UpperByteSelections = Vec(SubDwordSelection.Byte1, SubDwordSelection.Byte2, SubDwordSelection.Byte3)

  /** SDWA executes only opcodes the catalogue lists with an SDWA encoding. */
  private def withoutSourceModifiers(machine: InstructionDecoder.Decoded): Boolean =
    SourceModifierFields.forall(!machine.isSet(_))

  private def encodedModifiers(machine: InstructionDecoder.Decoded): SourceModifiers = SourceModifiers(
    machine(EncodingField.SRC0_ABS).toInt | (machine(EncodingField.SRC1_ABS).toInt << 1),
    machine(EncodingField.SRC0_NEG).toInt | (machine(EncodingField.SRC1_NEG).toInt << 1)
  )

  /** VOP1, VOP2 and VOPC DPP carry the ABS and NEG bits a VOP3 encoding of the opcode applies. */
  private def sourceModified(machine: InstructionDecoder.Decoded, operation: Instruction): Option[Instruction] = {
    val modifiers = encodedModifiers(machine)
    val applies   = ((modifiers.absolute | modifiers.negate) & ~vop3SourceModifierMask(machine.definition)) == 0
    if (modifiers == SourceModifiers.Unmodified) Some(operation)
    else Option.when(applies && !machine.format.isVOP3)(operation).collect {
      case binary @ Instruction.VBinary(modifiers = SourceModifiers.Unmodified)   => binary.copy(modifiers = modifiers)
      case convert @ Instruction.VConvert(modifiers = SourceModifiers.Unmodified) => convert.copy(modifiers = modifiers)
      case compare @ Instruction.VCompare(modifiers = SourceModifiers.Unmodified) => compare.copy(modifiers = modifiers)
      case select @ Instruction.VCndMask(modifiers = SourceModifiers.Unmodified)  => select.copy(modifiers = modifiers)
      case multiply @ Instruction.VMadF32(modifiers = SourceModifiers.Unmodified) =>
        multiply.copy(modifiers = modifiers)
    }
  }

  private def sdwaListed(machine: InstructionDecoder.Decoded): Either[NonEmpty[String], Unit] =
    Either.cond(machine.encoding.format.isSdwa, (), s"${machine.definition} SDWA execution is not implemented")

  /** Whether `operation` works on dwords lane by lane, so DPP can route its source 0; 32-bit DPP routes no pairs. */
  @annotation.tailrec
  private def routable(operation: Instruction): Boolean = operation match {
    case Instruction.VConvert(operation = conversion) =>
      !PairConversions(conversion) || WideningConversions(conversion)
    case Instruction.VCompare(operation = VectorComparison(kind = kind)) =>
      kind match {
        case VectorComparisonKind.Signed64 | VectorComparisonKind.Unsigned64 | VectorComparisonKind.Float64 |
            VectorComparisonKind.ClassFloat64 => false
        case _ => true
      }
    case Instruction.VClampResult(operation = inner)  => routable(inner)
    case Instruction.VOutputModified(ignored = inner) => routable(inner)
    case _: (Instruction.VMove | Instruction.VMoveRelative | Instruction.VMoveRelativeDestination |
          Instruction.VNot | Instruction.VBitReverse | Instruction.VBitFind |
          Instruction.VBinary | Instruction.VAddCarry | Instruction.VSubCarry | Instruction.VSubReverseCarry |
          Instruction.VMadF32 | Instruction.VCndMask | Instruction.VTernary | Instruction.VBitCount |
          Instruction.VDot | Instruction.VMedia | Instruction.VMaskBitCount | Instruction.VConvertNarrow |
          Instruction.VDot2F32F16 | Instruction.VPackedTernary | Instruction.VFmaMixF32 |
          Instruction.VFmaMixF16 | Instruction.VAccumulatorWrite | Instruction.VPackB32F16) => true
    case _ => false
  }

  private val WideningConversions: Set[VectorConvertOperation] = Set(
    VectorConvertOperation.F64FromI32,
    VectorConvertOperation.F64FromU32,
    VectorConvertOperation.F64FromF32
  )

  /** Whether `operation` reads source 0 as a register pair, which DPP routes whole. */
  @annotation.tailrec
  private def pairRouted(operation: Instruction): Boolean = operation match {
    case Instruction.VConvert(operation = conversion) =>
      PairConversions(conversion) && !WideningConversions(conversion)
    case Instruction.VClampResult(operation = inner)  => pairRouted(inner)
    case Instruction.VOutputModified(ignored = inner) => pairRouted(inner)
    case Instruction.VMove64(dest = _) | Instruction.VBinary64(dest = _) | Instruction.VMadF64(dest = _) => true
    case _                                                                                               => false
  }

  private val PairConversions: Set[VectorConvertOperation] = Set(
    VectorConvertOperation.F64FromI32,
    VectorConvertOperation.F64FromU32,
    VectorConvertOperation.F64FromF32,
    VectorConvertOperation.F32FromF64,
    VectorConvertOperation.I32FromF64,
    VectorConvertOperation.U32FromF64,
    VectorConvertOperation.FrexpExponentF64,
    VectorConvertOperation.FrexpMantissaF64,
    VectorConvertOperation.ReciprocalF64,
    VectorConvertOperation.ReciprocalSquareRootF64,
    VectorConvertOperation.ReciprocalClampF64,
    VectorConvertOperation.ReciprocalSquareRootClampF64,
    VectorConvertOperation.SquareRootF64,
    VectorConvertOperation.TruncateF64,
    VectorConvertOperation.FloorF64,
    VectorConvertOperation.CeilF64,
    VectorConvertOperation.FractionF64,
    VectorConvertOperation.RoundNearestEvenF64
  )

  /** DPP routes source 0 of a 32-bit per-lane operation; other operations read their sources as whole registers. */
  private def dataParallel(
      machine: InstructionDecoder.Decoded,
      operation: Instruction,
      target: Target
  ): Either[NonEmpty[String], Instruction] = {
    val opcode = machine.definition
    val octets = machine.format.isDpp8
    for {
      _ <- Either.cond[NonEmpty[String], Unit](
        routable(operation) || pairRouted(operation),
        (),
        s"$opcode DPP execution is not implemented"
      )
      modified <- sourceModified(machine, operation).toRight[NonEmpty[String]](
        s"$opcode DPP source modifiers are not implemented"
      )
      control <-
        if (octets) Right(DPPControl.DPP8(DPP8Selectors.zipWithIndex.foldLeft(0) { case (selectors, (field, lane)) =>
          selectors | (machine(field).toInt << (lane * 3))
        }))
        else {
          val control = machine(EncodingField.DPP_CTRL).toInt
          Either.cond[NonEmpty[String], DPPControl](
            DppControl.valid(control, target.isa),
            DPPControl.DPP16(
              control,
              machine(EncodingField.ROW_MASK).toInt,
              machine(EncodingField.BANK_MASK).toInt,
              machine.isSet(EncodingField.BOUND_CTRL)
            ),
            f"$opcode DPP control 0x$control%x is not valid for ${target.name}"
          )
        }
      routed <- machine.vector(EncodingField.VSRC0)
      source =
        if ((trueHalves(machine, target) & 1) != 0 && routed >= 128) (routed - 128).unsafe[VectorRegisterIndex]
        else routed
    } yield {
      // the source register the relative moves read is the one DPP routes, so the move itself takes the routed value
      val (inner, relative) = modified match {
        case Instruction.VMoveRelative(dest, src, field) => (Instruction.VMove(dest, src), Some(field))
        case move @ Instruction.VMoveRelativeDestination(sourceField = field) =>
          (move.copy(sourceField = None), field)
        case other => (other, None)
      }
      Instruction.VDPP(
        inner,
        source,
        control,
        if (octets) machine(EncodingField.SRC0) == DPP8.DPP8_FI_1 else machine.isSet(EncodingField.FI),
        pairRouted(operation),
        if (opcode == VAccvgprMovB32) RegisterBank.Accumulator else RegisterBank.Vector,
        relative
      )
    }
  }

  /** SDWA selects a scalar source operand, including inline constants, when its S0/S1 bit is set. */
  private def subDwordSource(
      machine: InstructionDecoder.Decoded,
      field: EncodingField,
      scalarSelect: EncodingField,
      target: Target
  ): Either[NonEmpty[String], InstructionSource] =
    if (machine.isSet(scalarSelect)) operandSource(machine, machine(field).toInt, None, target)
    else machine.vector(field).map(InstructionSource.VectorRegister(_))

  /** VSRC1 names an SGPR only when SDWA selects scalar source mode. */
  private def vectorSourceB(
      machine: InstructionDecoder.Decoded,
      target: Target
  ): Either[NonEmpty[String], InstructionSource] =
    if (machine.format.isSdwa) subDwordSource(machine, EncodingField.VSRC1, EncodingField.S1, target)
    else machine.vector(EncodingField.VSRC1).map(InstructionSource.VectorRegister(_))

  /** DPP wraps the operation; SDWA modifies, clamps and scales it and places its result; otherwise the result takes the
    * 16-bit destination-half write.
    */
  private def vectorResult(
      machine: InstructionDecoder.Decoded,
      target: Target
  )(operation: Instruction): Either[NonEmpty[String], Instruction] =
    (if (machine.format.isDpp) dataParallel(machine, operation, target) else Right(operation)).flatMap {
      case subDword @ Instruction.VBinarySubDword(operation = _) => Right(subDword)
      case other if machine.format.isSdwa                        => subDwordResult(machine, other)
      case other                                                 =>
        trueDestination(machine, target).map((destination, high) =>
          halfResult(machine.definition, other, destination, high, target)
        )
    }

  /** An SDWA source as its selection reads it. */
  private def selectedSource(
      machine: InstructionDecoder.Decoded,
      source: InstructionSource,
      selection: EncodingField,
      signExtend: EncodingField
  ): Either[NonEmpty[String], InstructionSource] = subDwordSelection(machine(selection).toInt).map {
    case SubDwordSelection.DWord => source
    case part                    => InstructionSource.Selected(source, part, machine.isSet(signExtend))
  }

  /** An SDWA operation on selected sources with its source modifiers, clamp and output scale, its result placed in the
    * destination selection.
    */
  private def subDwordResult(
      machine: InstructionDecoder.Decoded,
      operation: Instruction
  ): Either[NonEmpty[String], Instruction] = {
    val opcode = machine.definition
    for {
      modified <- sourceModified(machine, operation)
        .toRight[NonEmpty[String]](s"$opcode SDWA source modifiers are not implemented")
      dest    <- machine.vector(EncodingField.VDST)
      clamped <- if (machine.isSet(EncodingField.CLAMP)) clampedOperation(opcode, modified, dest) else Right(modified)
      scaled  <-
        if (!machine.isSet(EncodingField.OMOD)) Right(clamped)
        else vop3OutputWidth(opcode).toRight[NonEmpty[String]](s"$opcode output modifiers are not implemented").map(
          width =>
            Instruction.VOutputModified(
              clamped,
              modified,
              dest,
              machine(EncodingField.OMOD).toInt match {
                case EncodingConstants.OutputModifiers.MUL2 => OutputScale.Multiply2
                case EncodingConstants.OutputModifiers.MUL4 => OutputScale.Multiply4
                case _                                      => OutputScale.Divide2
              },
              width,
              machine.isSet(EncodingField.CLAMP)
            )
        )
      selection <- subDwordSelection(machine(EncodingField.DST_SEL).toInt)
      unused    <- subDwordDestinationUnused(machine(EncodingField.DST_UNUSED).toInt)
    } yield selection match {
      case SubDwordSelection.DWord => scaled
      case part                    =>
        Instruction.VSubDwordResult(
          scaled,
          dest,
          part,
          unused,
          PartialFunction.condOpt(operation) { case Instruction.VMoveRelativeDestination(field = field) => field }
        )
    }
  }

  /** `operation` with its result clamped: the operations whose IR applies the clamp take it, the others saturate their
    * floating-point result.
    */
  private def clampedOperation(
      opcode: Opcode,
      operation: Instruction,
      dest: VectorRegisterIndex
  ): Either[NonEmpty[String], Instruction] = operation match {
    case binary @ Instruction.VBinary(operation = _) if ClampingVOP3Opcodes.contains(opcode) =>
      Right(binary.copy(clamp = true))
    case convert @ Instruction.VConvert(operation = _) if convertForm(opcode).nonEmpty =>
      Right(convert.copy(clamp = true))
    case other =>
      clampResultWidth(opcode).map(Instruction.VClampResult(other, dest, _))
        .toRight[NonEmpty[String]](s"$opcode clamp is not implemented")
  }

  private def decodeVOP2(machine: InstructionDecoder.Decoded, target: Target): Either[NonEmpty[String], Instruction] = {
    val opcode  = machine.definition
    val hasDPP  = machine.format.isDpp
    val hasSDWA = machine.format.isSdwa
    val literal = machine.literal
    // SDWA forms the sub-dword IR covers without modifiers keep it; others select their sources and place the result
    val subDword = hasSDWA && vectorSubDwordOperation(opcode).nonEmpty && withoutSourceModifiers(machine) &&
      !machine.isSet(EncodingField.CLAMP) && !machine.isSet(EncodingField.OMOD)
    (for {
      _    <- if (hasSDWA) sdwaListed(machine) else Right(())
      rawA <-
        if (hasSDWA) subDwordSource(machine, EncodingField.VSRC0, EncodingField.S0, target)
        else if (hasDPP) routedHalf(machine, target)
        else firstHalf(machine, target)
      rawB <- vectorSourceB(machine, target).map(trueHalf(_, trueHalves(machine, target), 1))
      srcA <-
        if (hasSDWA && !subDword) selectedSource(machine, rawA, EncodingField.SRC0_SEL, EncodingField.SRC0_SEXT)
        else Right(rawA)
      srcB <-
        if (hasSDWA && !subDword) selectedSource(machine, rawB, EncodingField.SRC1_SEL, EncodingField.SRC1_SEXT)
        else Right(rawB)
      placed <- trueDestination(machine, target)
      dest = placed._1
      operation <- opcode match {
        case VFmacF16 if placed._2 =>
          Right(Instruction.VTernary(
            VectorTernaryOperation.FusedMultiplyAddF16,
            dest,
            srcA,
            srcB,
            InstructionSource.HighHalf(InstructionSource.VectorRegister(dest))
          ))
        case supported if subDword =>
          for {
            destSelection   <- subDwordSelection(machine(EncodingField.DST_SEL).toInt)
            destUnused      <- subDwordDestinationUnused(machine(EncodingField.DST_UNUSED).toInt)
            srcASelection   <- subDwordSelection(machine(EncodingField.SRC0_SEL).toInt)
            srcBSelection   <- subDwordSelection(machine(EncodingField.SRC1_SEL).toInt)
            binaryOperation <- vectorSubDwordOperation(supported)
              .toRight[NonEmpty[String]](s"$supported SDWA execution is not implemented")
          } yield Instruction.VBinarySubDword(
            operation = binaryOperation,
            dest = dest,
            srcA = srcA,
            srcB = srcB,
            destSelection = destSelection,
            destUnused = destUnused,
            srcASelection = srcASelection,
            srcBSelection = srcBSelection,
            srcASignExtend = machine.isSet(EncodingField.SRC0_SEXT),
            srcBSignExtend = machine.isSet(EncodingField.SRC1_SEXT)
          )
        case VFmacLegacyF32 | VFmacDx9ZeroF32 =>
          Right(Instruction.VMadF32(
            VectorMadF32Operation.FusedMultiplyAddLegacy,
            dest,
            srcA,
            srcB,
            InstructionSource.VectorRegister(dest),
            SourceModifiers.Unmodified,
            false
          ))
        case VFmacF32 =>
          Right(Instruction.VMadF32(
            VectorMadF32Operation.FusedMultiplyAdd,
            dest,
            srcA,
            srcB,
            InstructionSource.VectorRegister(dest),
            SourceModifiers.Unmodified,
            false
          ))
        case VLshlrevB64 =>
          Right(Instruction.VShiftLeft64(dest, srcA, srcB))
        case VLshlB32 =>
          Right(Instruction.VBinary(VectorBinaryOperation.ShiftLeft32, dest, srcA = srcB, srcB = srcA, clamp = false))
        case VLshrB32 =>
          Right(Instruction.VBinary(VectorBinaryOperation.ShiftRight32, dest, srcA = srcB, srcB = srcA, clamp = false))
        case VAshrI32 =>
          Right(Instruction.VBinary(
            VectorBinaryOperation.ShiftRightArithmeticI32,
            dest,
            srcA = srcB,
            srcB = srcA,
            clamp = false
          ))
        case VMbcntLoU32B32 => Right(Instruction.VMaskBitCount(dest, srcA, srcB, high = false))
        case VMbcntHiU32B32 => Right(Instruction.VMaskBitCount(dest, srcA, srcB, high = true))
        case VBcntU32B32    => Right(Instruction.VBitCount(dest = dest, src = srcA, base = srcB))
        case VBfmB32 | VCvtPkI16I32 | VCvtPkU16U32 | VCvtPknormI16F32_2 | VCvtPkNormI16F32_1 | VCvtPknormU16F32_2 |
            VCvtPkNormU16F32_1 =>
          vectorMediaOperation(opcode).map(
            Instruction.VMedia(_, dest, srcA, srcB, InstructionSource.Constant(0L), SourceModifiers.Unmodified)
          ).toRight[NonEmpty[String]](s"$opcode VOP2 execution is not implemented")
        // the byte SRC1 selects replaces the destination's
        case VCvtPkaccumU8F32 =>
          Right(Instruction.VMedia(
            VectorMediaOperation.PackByteF32,
            dest,
            srcA,
            srcB,
            InstructionSource.VectorRegister(dest),
            SourceModifiers.Unmodified
          ))
        case VMacLegacyF32 =>
          Right(Instruction.VMadF32(
            VectorMadF32Operation.MultiplyAddLegacy,
            dest,
            srcA,
            srcB,
            InstructionSource.VectorRegister(dest),
            SourceModifiers.Unmodified,
            false
          ))
        // GFX6 and GFX7 encode the lane moves in VOP2, VSRC1 naming the lane select as a source operand does
        case VReadlaneB32 =>
          for {
            lane        <- scalarSource(machine(EncodingField.VSRC1).toInt, None, target)
            destination <- machine.scalar(EncodingField.VDST)
          } yield Instruction.VReadLane(destination, srcA, lane)
        case VWritelaneB32 =>
          scalarSource(machine(EncodingField.VSRC1).toInt, None, target).map(Instruction.VWriteLane(dest, srcA, _))
        // each half accumulates into the same half of the destination
        case VPkFmacF16 =>
          Right(Instruction.VPackedTernary(
            dest = dest,
            srcA = srcA,
            srcB = srcB,
            srcC = InstructionSource.VectorRegister(dest),
            lowSelection = 0,
            highSelection = 7,
            negateLow = 0,
            negateHigh = 0,
            clamp = false
          ))
        case VFmacF64 =>
          Right(Instruction.VMadF64(
            dest,
            srcA,
            srcB,
            InstructionSource.VectorRegister(dest),
            SourceModifiers.Unmodified
          ))
        case VAddF64 | VMulF64 | VMaxF64 | VMaxNumF64 | VMinF64 | VMinNumF64 =>
          vectorBinary64Operation(opcode).map(operation =>
            Instruction.VBinary64(operation, dest, srcA, srcB, SourceModifiers.Unmodified)
          ).toRight[NonEmpty[String]](s"$opcode VOP2 execution is not implemented")
        case VMacF16 =>
          Right(Instruction.VTernary(
            VectorTernaryOperation.MultiplyAddF16,
            dest,
            srcA,
            srcB,
            InstructionSource.VectorRegister(dest)
          ))
        case VMadmkF16 | VFmamkF16 =>
          literal.map(value =>
            Instruction.VTernary(
              if (opcode == VFmamkF16) VectorTernaryOperation.FusedMultiplyAddF16
              else VectorTernaryOperation.MultiplyAddF16,
              dest,
              srcA,
              InstructionSource.Literal(value),
              srcB
            )
          ).toRight[NonEmpty[String]](s"$opcode is missing its literal operand")
        case VMadakF16 | VFmaakF16 =>
          literal.map(value =>
            Instruction.VTernary(
              if (opcode == VFmaakF16) VectorTernaryOperation.FusedMultiplyAddF16
              else VectorTernaryOperation.MultiplyAddF16,
              dest,
              srcA,
              srcB,
              InstructionSource.Literal(value)
            )
          ).toRight[NonEmpty[String]](s"$opcode is missing its literal operand")
        case VMadmkF32 | VFmamkF32 =>
          literal.map(value =>
            Instruction.VMadF32(
              if (opcode == VFmamkF32) VectorMadF32Operation.FusedMultiplyAdd else VectorMadF32Operation.MultiplyAdd,
              dest,
              srcA,
              InstructionSource.Literal(value),
              srcB,
              SourceModifiers.Unmodified,
              false
            )
          )
            .toRight[NonEmpty[String]](s"$opcode is missing its literal operand")
        case VMadakF32 | VFmaakF32 =>
          literal.map(value =>
            Instruction.VMadF32(
              if (opcode == VFmaakF32) VectorMadF32Operation.FusedMultiplyAdd else VectorMadF32Operation.MultiplyAdd,
              dest,
              srcA,
              srcB,
              InstructionSource.Literal(value),
              SourceModifiers.Unmodified,
              false
            )
          )
            .toRight[NonEmpty[String]](s"$opcode is missing its literal operand")
        case VDot2F32F16 =>
          Right(Instruction.VDot2F32F16(dest = dest, srcA = srcA, srcB = srcB, accumulate = false))
        case VDot2cF32F16 =>
          Right(Instruction.VDot2F32F16(dest = dest, srcA = srcA, srcB = srcB, accumulate = true))
        case VDot2accF32F16 =>
          Right(Instruction.VDot(DotOperation.Halves, dest, srcA, srcB, InstructionSource.VectorRegister(dest), false))
        case VDot2cF32Bf16 =>
          Right(Instruction.VDot(
            DotOperation.BFloat16Halves,
            dest,
            srcA,
            srcB,
            InstructionSource.VectorRegister(dest),
            false
          ))
        case VDot4cI32I8 =>
          Right(Instruction.VDot(
            DotOperation.SignedBytes,
            dest,
            srcA,
            srcB,
            InstructionSource.VectorRegister(dest),
            false
          ))
        case VDot2cI32I16 =>
          Right(Instruction.VDot(
            DotOperation.SignedShorts,
            dest,
            srcA,
            srcB,
            InstructionSource.VectorRegister(dest),
            false
          ))
        case VDot8cI32I4 =>
          Right(Instruction.VDot(
            DotOperation.SignedNibbles,
            dest,
            srcA,
            srcB,
            InstructionSource.VectorRegister(dest),
            false
          ))
        case VMovB32     => Right(Instruction.VMove(dest, srcA))
        case VNotB32     => Right(Instruction.VNot(dest, srcA))
        case VCndmaskB32 =>
          Right(Instruction.VCndMask(dest, srcA, srcB, InstructionSource.Scalar(Fixed.VccLo)))
        case VAddCoU32 =>
          Right(Instruction.VAddCarry(
            dest = dest,
            carryDest = Fixed.VccLo,
            srcA = srcA,
            srcB = srcB,
            carryIn = None
          ))
        case VAddCoCiU32 | VAddcCoU32 =>
          Right(Instruction.VAddCarry(
            dest = dest,
            carryDest = Fixed.VccLo,
            srcA = srcA,
            srcB = srcB,
            carryIn = Some(InstructionSource.Scalar(Fixed.VccLo))
          ))
        case VSubCoU32 =>
          Right(Instruction.VSubCarry(
            dest = dest,
            carryDest = Fixed.VccLo,
            srcA = srcA,
            srcB = srcB,
            carryIn = None
          ))
        case VSubCoCiU32 | VSubbCoU32 =>
          Right(Instruction.VSubCarry(
            dest = dest,
            carryDest = Fixed.VccLo,
            srcA = srcA,
            srcB = srcB,
            carryIn = Some(InstructionSource.Scalar(Fixed.VccLo))
          ))
        case VSubrevCoU32 =>
          Right(Instruction.VSubReverseCarry(
            dest = dest,
            carryDest = Fixed.VccLo,
            srcA = srcA,
            srcB = srcB,
            carryIn = None
          ))
        case VSubbrevCoU32 | VSubrevCoCiU32 =>
          Right(Instruction.VSubReverseCarry(
            dest = dest,
            carryDest = Fixed.VccLo,
            srcA = srcA,
            srcB = srcB,
            carryIn = Some(InstructionSource.Scalar(Fixed.VccLo))
          ))
        case other =>
          vectorBinaryOperation(other)
            .map(operation =>
              Instruction.VBinary(
                operation = operation,
                dest = dest,
                srcA = srcA,
                srcB = srcB,
                clamp = false
              )
            )
            .toRight[NonEmpty[String]](s"$other VOP2 execution is not implemented")
      }
    } yield operation).flatMap(vectorResult(machine, target))
  }

  /** The relative move `opcode` makes: RDNA offsets the registers by all of M0, V_MOVRELSD_2_B32 by M0's two fields,
    * and earlier targets by its low byte.
    */
  private def relativeMove(
      opcode: Opcode,
      destination: VectorRegisterIndex,
      src: InstructionSource,
      target: Target
  ): Instruction = {
    val field = if (target.isa.isRDNA) RelativeField.Whole else RelativeField.Low
    opcode match {
      case VMovrelsB32  => Instruction.VMoveRelative(destination, src, field)
      case VMovreldB32  => Instruction.VMoveRelativeDestination(destination, src, None, field)
      case VMovrelsdB32 => Instruction.VMoveRelativeDestination(destination, src, Some(field), field)
      case _            =>
        Instruction.VMoveRelativeDestination(destination, src, Some(RelativeField.Source), RelativeField.Destination)
    }
  }

  private def decodeVOP1(machine: InstructionDecoder.Decoded, target: Target): Either[NonEmpty[String], Instruction] = {
    val opcode  = machine.definition
    val hasDPP  = machine.format.isDpp
    val hasSDWA = machine.format.isSdwa
    (for {
      src <-
        if (hasSDWA) subDwordSource(machine, EncodingField.VSRC0, EncodingField.S0, target).flatMap(
          selectedSource(machine, _, EncodingField.SRC0_SEL, EncodingField.SRC0_SEXT)
        )
        else if (hasDPP) routedHalf(machine, target)
        else firstHalf(machine, target)
      destination <- trueDestination(machine, target).map(_._1)
      operation   <- opcode match {
        case VAccvgprMovB32 => src match {
            case InstructionSource.VectorRegister(index) => Right(Instruction.VAccumulatorMove(destination, index))
            // DPP routes the source from the accumulation registers into an accumulator write
            case InstructionSource.DPP => Right(Instruction.VAccumulatorWrite(destination, src))
            case _ => Left[NonEmpty[String], Instruction](s"$opcode requires an accumulator register")
          }
        case VMovB32        => Right(Instruction.VMove(destination, src))
        case VPermlane64B32 =>
          Right(Instruction.VPermuteLanes(
            destination,
            src,
            InstructionSource.Constant(0L),
            InstructionSource.Constant(0L),
            LanePermutation.HalfWave,
            fetchInactive = true,
            boundControl = false
          ))
        case VMovB64 if !hasSDWA => Right(Instruction.VMove64(destination, src))
        case VMovB64             =>
          Left[NonEmpty[String], Instruction](s"$opcode has no SDWA form: SDWA selects within a dword")
        case VCvtF32Fp8 | VCvtF32Bf8 | VCvtPkF32Fp8 | VCvtPkF32Bf8 =>
          eightBitConversion(opcode, target).map(Instruction.VConvert(_, destination, src))
            .toRight[NonEmpty[String]](s"$opcode VOP1 execution is not implemented")
        case VSwaprelB32 => src match {
            case InstructionSource.VectorRegister(index) => Right(Instruction.VSwapRelative(destination, index))
            case _ => Left[NonEmpty[String], Instruction](s"$opcode requires a vector-register source")
          }
        case VMovrelsB32 | VMovreldB32 | VMovrelsdB32 | VMovrelsd2B32 =>
          Right(relativeMove(opcode, destination, src, target))
        case VNotB32                                 => Right(Instruction.VNot(destination, src))
        case VMovB16                                 => Right(Instruction.VMove(destination, src))
        case VNotB16                                 => Right(Instruction.VNot(destination, src))
        case VSwapB32                                => Right(Instruction.VSwap(destination, src))
        case VPermlane16SwapB32 | VPermlane32SwapB32 => permlaneSwap(machine.definition, destination, src, 0)
        case VSwapB16                                =>
          for {
            (dest, high) <- trueDestination(machine, target)
            swapped      <- src match {
              case InstructionSource.VectorRegister(index) =>
                Right(Instruction.VSwapHalves(dest, high, index, srcHigh = false))
              case InstructionSource.HighHalf(InstructionSource.VectorRegister(index)) =>
                Right(Instruction.VSwapHalves(dest, high, index, srcHigh = true))
              case _ => Left[NonEmpty[String], Instruction](s"$opcode requires a vector-register source")
            }
          } yield swapped
        case VBfrevB32 =>
          Right(Instruction.VBitReverse(destination, src))
        case VReadfirstlaneB32      => machine.scalar(EncodingField.VDST).map(Instruction.VReadFirstLane(_, src))
        case VScreenPartition4seB32 => Left(ScreenPartition)
        case other                  =>
          vectorBitFindOperation(other)
            .map(operation => Instruction.VBitFind(operation, destination, src))
            .orElse(
              vectorConvertOperation(other)
                .map(operation => Instruction.VConvert(operation, destination, src))
            )
            .toRight[NonEmpty[String]](s"$other VOP1 execution is not implemented")
      }
    } yield operation).flatMap(vectorResult(machine, target))
  }

  private def decodeVOPC(machine: InstructionDecoder.Decoded, target: Target): Either[NonEmpty[String], Instruction] = {
    val opcode  = machine.definition
    val hasSDWA = machine.format.isSdwa
    (for {
      srcA <-
        if (hasSDWA) subDwordSource(machine, EncodingField.VSRC0, EncodingField.S0, target)
        else if (machine.format.isDpp) routedHalf(machine, target)
        else firstHalf(machine, target)
      srcB       <- vectorSourceB(machine, target).map(trueHalf(_, trueHalves(machine, target), 1))
      comparison <- vectorComparison(opcode).toRight[NonEmpty[String]](s"$opcode VOPC execution is not implemented")
      operation  <-
        if (hasSDWA) for {
          _           <- sdwaListed(machine)
          destination <-
            if (machine.isSet(EncodingField.SD)) machine.scalar(EncodingField.SDST)
            else Right(Fixed.VccLo: ScalarRegisterIndex)
          srcASelection <- subDwordSelection(machine(EncodingField.SRC0_SEL).toInt)
          srcBSelection <- subDwordSelection(machine(EncodingField.SRC1_SEL).toInt)
          modifiers = encodedModifiers(machine)
          _ <- Either.cond[NonEmpty[String], Unit](
            ((modifiers.absolute | modifiers.negate) & ~comparisonModifierMask(comparison)) == 0,
            (),
            s"$opcode SDWA source modifiers are not implemented"
          )
        } yield Instruction.VCompare(
          comparison,
          comparisonDestination(comparison, destination, target),
          srcA,
          srcB,
          modifiers,
          Some(SubDwordSources(
            srcASelection,
            srcBSelection,
            machine.isSet(EncodingField.SRC0_SEXT),
            machine.isSet(EncodingField.SRC1_SEXT)
          ))
        )
        else Right(Instruction.VCompare(comparison, comparisonDestination(comparison, Fixed.VccLo, target), srcA, srcB))
    } yield operation).flatMap(operation =>
      if (machine.format.isDpp) dataParallel(machine, operation, target) else Right(operation)
    )
  }

  private def decodeVOP3(machine: InstructionDecoder.Decoded, target: Target): Either[NonEmpty[String], Instruction] = {
    val opcode    = machine.definition
    val literal   = machine.literal
    val modifiers = SourceModifiers(machine(EncodingField.ABS).toInt, machine(EncodingField.NEG).toInt)
    // MAC reads its addend from the destination, which takes no modifiers.
    val sourceModifiersAB             = SourceModifiers(modifiers.absolute & 3, modifiers.negate & 3)
    def operand(field: EncodingField) =
      operandSource(machine, machine(field).toInt, literal, target).leftMap[NonEmpty[String]](error =>
        s"$error in $opcode"
      )
    val opSelection                                                  = machine(EncodingField.OP_SEL).toInt
    def half(source: InstructionSource, bit: Int): InstructionSource =
      if ((opSelection & (1 << bit)) != 0 && halfOpSelection(opcode).nonEmpty) InstructionSource.HighHalf(source)
      else source
    for {
      srcA <- if (machine.format.isDpp) Right(InstructionSource.DPP) else operand(EncodingField.SRC0)
      srcB <- operand(EncodingField.SRC1)
      srcC <- operand(EncodingField.SRC2)
      _    <- Either.cond[NonEmpty[String], Unit](
        ((modifiers.absolute | modifiers.negate) & ~vop3SourceModifierMask(opcode)) == 0,
        (),
        s"$opcode source modifiers are not implemented"
      )
      _ <- Either.cond[NonEmpty[String], Unit](
        halfOpSelection(opcode).forall(defined =>
          opSelection == 0 ||
            ((halfOpSelectionDefined(target) || NativeHalfOpcodes(opcode)) && (opSelection & ~defined) == 0)
        ),
        (),
        s"$opcode op_sel is not implemented"
      )
      destination      <- machine.vector(EncodingField.VDST)
      carryDestination <- machine.scalar(EncodingField.SDST)
      lower = (clamp: Boolean) => {
        def scalarDestination: Either[NonEmpty[String], ScalarRegisterIndex] =
          destination.refine[ScalarRegisterIndex]
            .left.map[NonEmpty[String]](_ => s"$opcode scalar destination $destination exceeds the scalar bank")
        def halvedBinary: Either[NonEmpty[String], Instruction] =
          vectorBinaryOperation(opcode).map(operation =>
            Instruction.VBinary(operation, destination, half(srcA, 0), half(srcB, 1), clamp, modifiers)
          ).toRight[NonEmpty[String]](s"$opcode VOP3 execution is not implemented")
        def halvedTernary: Either[NonEmpty[String], Instruction] =
          vectorTernaryOperation(opcode).map(operation =>
            Instruction.VTernary(
              operation,
              destination,
              half(srcA, 0),
              half(srcB, 1),
              half(srcC, 2),
              modifiers,
              clamp
            )
          ).toRight[NonEmpty[String]](s"$opcode VOP3 execution is not implemented")
        def halvedConvert: Either[NonEmpty[String], Instruction] =
          vectorConvertOperation(opcode).map(operation =>
            Instruction.VConvert(operation, destination, half(srcA, 0), modifiers, clamp)
          ).toRight[NonEmpty[String]](s"$opcode VOP3 execution is not implemented")
        opcode match {
          case VAddF16 | VMulF16 | VMinF16 | VMaxF16 | VMaxNumF16 | VMinNumF16 | VMinimumF16 | VMaximumF16 =>
            halvedBinary
          // The accumulator is the destination half that OP_SEL bit 3 names.
          case VFmacF16 =>
            Right(Instruction.VTernary(
              VectorTernaryOperation.FusedMultiplyAddF16,
              destination,
              half(srcA, 0),
              half(srcB, 1),
              half(InstructionSource.VectorRegister(destination), 3),
              sourceModifiersAB
            ))
          case VCvtF32F16 | VCvtF16F32 | VCvtI16F16 => halvedConvert
          case VLshlAddU64                          =>
            Right(Instruction.VShiftLeftAdd64(destination, srcA, srcB, srcC))
          case VLshlrevB64 =>
            Right(Instruction.VShiftLeft64(destination, srcA, srcB))
          case VLshrrevB64 =>
            Right(Instruction.VShiftRight64(destination, srcA, srcB))
          case VAshrrevI64 =>
            Right(Instruction.VShiftRightArithmetic64(destination, srcA, srcB))
          case VLshlB64 =>
            Right(Instruction.VShiftLeft64(destination, srcB, srcA))
          case VLshrB64 =>
            Right(Instruction.VShiftRight64(destination, srcB, srcA))
          case VAshrI64 =>
            Right(Instruction.VShiftRightArithmetic64(destination, srcB, srcA))
          case VLshlB32 | VLshrB32 | VAshrI32 =>
            Right(Instruction.VBinary(
              opcode match {
                case VLshlB32 => VectorBinaryOperation.ShiftLeft32
                case VLshrB32 => VectorBinaryOperation.ShiftRight32
                case _        => VectorBinaryOperation.ShiftRightArithmeticI32
              },
              destination,
              srcA = srcB,
              srcB = srcA,
              clamp = clamp
            ))
          case VCvtPkaccumU8F32 =>
            Right(Instruction.VMedia(
              VectorMediaOperation.PackByteF32,
              destination,
              srcA,
              srcB,
              InstructionSource.VectorRegister(destination),
              modifiers
            ))
          case VMacLegacyF32 | VMullitF32 =>
            Right(Instruction.VMadF32(
              if (opcode == VMullitF32) VectorMadF32Operation.MultiplyLighting
              else VectorMadF32Operation.MultiplyAddLegacy,
              destination,
              srcA,
              srcB,
              if (opcode == VMullitF32) srcC else InstructionSource.VectorRegister(destination),
              if (opcode == VMullitF32) modifiers else sourceModifiersAB,
              clamp
            ))
          case VSRcpF32 =>
            srcA match {
              case InstructionSource.VectorRegister(_) =>
                Left[NonEmpty[String], Instruction](s"$opcode requires a scalar source")
              case _ => scalarDestination.map(Instruction.SReciprocalF32(_, srcA, modifiers, clamp))
            }
          case VRcpF32 | VRcpIflagF32 =>
            Right(Instruction.VConvert(VectorConvertOperation.ReciprocalF32, destination, srcA, modifiers, clamp))
          case VReadfirstlaneB32 => machine.scalar(EncodingField.VDST).map(Instruction.VReadFirstLane(_, srcA))
          case VMadU32U16 | VMadI32I16 | VMadI16 | VMadLegacyI16 | VMadF16 | VMadLegacyF16 | VFmaLegacyF16 |
              VDivFixupF16 | VDivFixupLegacyF16 => halvedTernary
          // the accumulator is the destination half that OP_SEL bit 3 names
          case VMacF16 =>
            Right(Instruction.VTernary(
              VectorTernaryOperation.MultiplyAddF16,
              destination,
              half(srcA, 0),
              half(srcB, 1),
              half(InstructionSource.VectorRegister(destination), 3),
              sourceModifiersAB,
              clamp
            ))
          case VCubeidF32 | VCubescF32 | VCubetcF32 | VCubemaF32 | VLerpU8 | VCvtPkU8F32 =>
            vectorMediaOperation(opcode).map(Instruction.VMedia(_, destination, srcA, srcB, srcC, modifiers))
              .toRight[NonEmpty[String]](s"$opcode VOP3 execution is not implemented")
          case VCvtPkI16I32 | VCvtPkU16U32 | VCvtPknormI16F32_2 | VCvtPkNormI16F32_1 | VCvtPknormU16F32_2 |
              VCvtPkNormU16F32_1 | VBfmB32 | VCvtPknormI16F16_2 | VCvtPkNormI16F16_1 | VCvtPknormU16F16_2 |
              VCvtPkNormU16F16_1 | VCvtPkF16F32 | VCvtPkBf16F32 | VCvtPkI16F32 | VCvtPkU16F32 =>
            vectorMediaOperation(opcode).map(
              Instruction.VMedia(
                _,
                destination,
                half(srcA, 0),
                half(srcB, 1),
                InstructionSource.Constant(0L),
                modifiers
              )
            ).toRight[NonEmpty[String]](s"$opcode VOP3 execution is not implemented")
          case VQsadPkU16U8 | VMqsadPkU16U8 =>
            Right(Instruction.VQuadSad(destination, srcA, srcB, srcC, masked = opcode == VMqsadPkU16U8, wide = false))
          case VMqsadU32U8 => srcC match {
              case InstructionSource.VectorRegister(_) =>
                Right(Instruction.VQuadSad(destination, srcA, srcB, srcC, masked = true, wide = true))
              case _ => Left[NonEmpty[String], Instruction](s"$opcode accumulator is not a vector register quad")
            }
          case VPermlane16SwapB32 | VPermlane32SwapB32 => permlaneSwap(opcode, destination, srcA, opSelection)
          case VPermlane16VarB32 | VPermlanex16VarB32  =>
            Right(Instruction.VPermuteLanes(
              destination,
              srcA,
              srcB,
              InstructionSource.Constant(0L),
              if (opcode == VPermlanex16VarB32) LanePermutation.CrossRowVariable else LanePermutation.RowVariable,
              fetchInactive = (opSelection & 1) != 0,
              boundControl = (opSelection & 2) != 0
            ))
          case VPermlane16B32 | VPermlanex16B32 =>
            Right(Instruction.VPermuteLanes(
              destination,
              srcA,
              srcB,
              srcC,
              if (opcode == VPermlanex16B32) LanePermutation.CrossRow else LanePermutation.Row,
              fetchInactive = (opSelection & 1) != 0,
              boundControl = (opSelection & 2) != 0
            ))
          case VFrexpMantF32 =>
            Right(Instruction.VConvert(VectorConvertOperation.FrexpMantissaF32, destination, srcA, modifiers, clamp))
          case VCvtF64F32 =>
            Right(Instruction.VConvert(VectorConvertOperation.F64FromF32, destination, srcA, modifiers, clamp))
          case VFrexpMantF64 =>
            Right(Instruction.VConvert(VectorConvertOperation.FrexpMantissaF64, destination, srcA, modifiers))
          case VTrigPreopF64 =>
            Right(Instruction.VTrigPreopF64(destination, srcA, srcB, modifiers))
          case VBitop3B16 | VBitop3B32 =>
            Right(Instruction.VBitOp3(
              destination,
              srcA,
              srcB,
              srcC,
              machine(EncodingField.BITOP3).toInt,
              opcode == VBitop3B16
            ))
          case VFmaF64 | VFmacF64 =>
            Right(Instruction.VMadF64(
              destination,
              srcA,
              srcB,
              if (opcode == VFmacF64) InstructionSource.VectorRegister(destination) else srcC,
              modifiers
            ))
          case VDivFmasF64 =>
            Right(Instruction.VDivFmasF64(destination, srcA, srcB, srcC, modifiers))
          case VDivFixupF64 =>
            Right(Instruction.VDivFixupF64(
              dest = destination,
              quotient = srcA,
              denominator = srcB,
              numerator = srcC,
              absolute = machine(EncodingField.ABS).toInt,
              negate = machine(EncodingField.NEG).toInt
            ))
          case VDivScaleF64 =>
            Right(Instruction.VDivScaleF64(
              dest = destination,
              conditionDest = carryDestination,
              srcA = srcA,
              srcB = srcB,
              srcC = srcC,
              modifiers = modifiers
            ))
          case VAddF64 | VLdexpF64 | VMulF64 | VMaxF64 | VMaxNumF64 | VMinF64 | VMinNumF64 | VMinimumF64 | VMaximumF64 =>
            vectorBinary64Operation(opcode)
              .map(operation =>
                Instruction.VBinary64(operation, destination, srcA, srcB, modifiers)
              )
              .toRight[NonEmpty[String]](s"$opcode VOP3 execution is not implemented")
          case VMbcntLoU32B32 =>
            Right(Instruction.VMaskBitCount(destination, srcA, srcB, high = false))
          case VMbcntHiU32B32 =>
            Right(Instruction.VMaskBitCount(destination, srcA, srcB, high = true))
          case VReadlaneB32  => machine.scalar(EncodingField.VDST).map(Instruction.VReadLane(_, srcA, srcB))
          case VWritelaneB32 =>
            Right(Instruction.VWriteLane(destination, srcA, srcB))
          case VAddU32 | VAddNcU32 | VAddU16 | VAddNcU16 | VSubU16 | VSubrevU16 | VSubNcU16 | VMulLoU16 | VAshrrevI16 |
              VAshrrevI32 |
              VLshlrevB32 | VLshrrevB32 | VLshlrevB16 | VLshrrevB16 | VMulLoU32 | VMulHiU32 | VMulHiI32 |
              VSubU32 | VSubNcU32 | VSubrevU32 | VSubrevNcU32 | VMinI16 | VMinU16 | VMaxI16 | VMaxU16 |
              VAndB32 | VOrB32 | VXorB32 =>
            vectorBinaryOperation(opcode)
              .map(operation =>
                Instruction.VBinary(
                  operation = operation,
                  dest = destination,
                  srcA = srcA,
                  srcB = srcB,
                  clamp = clamp
                )
              )
              .toRight[NonEmpty[String]](s"$opcode VOP3 execution is not implemented")
          case VCndmaskB32 => srcC match {
              case InstructionSource.Scalar(_) =>
                Right(Instruction.VCndMask(destination, srcA, srcB, srcC, modifiers))
              case _ => Left[NonEmpty[String], Nothing](s"$opcode condition is not a scalar mask")
            }
          case VAddCoU32 =>
            Right(Instruction.VAddCarry(
              dest = destination,
              carryDest = carryDestination,
              srcA = srcA,
              srcB = srcB,
              carryIn = None,
              clamp = clamp
            ))
          case VAddCoCiU32 | VAddcCoU32 =>
            Right(Instruction.VAddCarry(
              dest = destination,
              carryDest = carryDestination,
              srcA = srcA,
              srcB = srcB,
              carryIn = Some(srcC),
              clamp = clamp
            ))
          case VSubCoU32 =>
            Right(Instruction.VSubCarry(
              dest = destination,
              carryDest = carryDestination,
              srcA = srcA,
              srcB = srcB,
              carryIn = None,
              clamp = clamp
            ))
          case VSubbCoU32 | VSubCoCiU32 =>
            Right(Instruction.VSubCarry(
              dest = destination,
              carryDest = carryDestination,
              srcA = srcA,
              srcB = srcB,
              carryIn = Some(srcC),
              clamp = clamp
            ))
          case VSubrevCoU32 =>
            Right(Instruction.VSubReverseCarry(
              dest = destination,
              carryDest = carryDestination,
              srcA = srcA,
              srcB = srcB,
              carryIn = None,
              clamp = clamp
            ))
          case VSubbrevCoU32 | VSubrevCoCiU32 =>
            Right(Instruction.VSubReverseCarry(
              dest = destination,
              carryDest = carryDestination,
              srcA = srcA,
              srcB = srcB,
              carryIn = Some(srcC),
              clamp = clamp
            ))
          case VMadF32 | VFmaF32 =>
            Right(Instruction.VMadF32(
              if (opcode == VFmaF32) VectorMadF32Operation.FusedMultiplyAdd
              else VectorMadF32Operation.MultiplyAdd,
              destination,
              srcA,
              srcB,
              srcC,
              modifiers,
              clamp
            ))
          case VMacF32 | VFmacF32 =>
            Right(Instruction.VMadF32(
              if (opcode == VFmacF32) VectorMadF32Operation.FusedMultiplyAdd
              else VectorMadF32Operation.MultiplyAdd,
              destination,
              srcA,
              srcB,
              InstructionSource.VectorRegister(destination),
              sourceModifiersAB,
              clamp
            ))
          case VDivFixupF32 =>
            Right(Instruction.VDivFixupF32(
              dest = destination,
              quotient = srcA,
              denominator = srcB,
              numerator = srcC,
              absolute = machine(EncodingField.ABS).toInt,
              negate = machine(EncodingField.NEG).toInt
            ))
          case VBcntU32B32 =>
            Right(Instruction.VBitCount(dest = destination, src = srcA, base = srcB))
          case VPkFmaF16 =>
            Right(Instruction.VPackedTernary(
              dest = destination,
              srcA = srcA,
              srcB = srcB,
              srcC = srcC,
              lowSelection = machine(EncodingField.OP_SEL).toInt & 7,
              highSelection = machine(EncodingField.OMOD).toInt |
                (((machine(EncodingField.OP_SEL).toInt >> 3) & 1) << 2),
              negateLow = machine(EncodingField.NEG).toInt,
              negateHigh = machine(EncodingField.ABS).toInt,
              clamp = clamp
            ))
          case VAdd3U32 | VOr3B32 | VXor3B32 | VFmaF16 | VDivFmasF32 | VMadLegacyU16 | VMadU16 | VMadU32U24 | VAlignbitB32 | VBfiB32 |
              VMed3I16 | VMed3I32 | VMed3U16 | VMed3U32 | VMin3F32 |
              VMax3I16 | VMax3U16 | VMin3I16 | VMin3U16 |
              VMax3I32 | VMax3U32 | VMin3I32 | VMin3U32 |
              VAndOrB32 | VBfeI32 | VBfeU32 | VXadU32 | VMadI32I24 | VAddLshlU32 | VLshlAddU32 | VLshlOrB32 | VPermB32 |
              VMax3F32 | VMed3F32 | VMaxminF32 | VMinmaxF32 | VMaxminI32 | VMinmaxI32 | VMaxminU32 | VMinmaxU32 |
              VMax3NumF32 | VMin3NumF32 | VMed3NumF32 | VMaxminNumF32 | VMinmaxNumF32 | VMinimum3F32 | VMaximum3F32 |
              VMaximumminimumF32 | VMinimummaximumF32 |
              VAlignbyteB32 | VSadU8 | VSadHiU8 | VSadU16 | VSadU32 | VMsadU8 | VAshrPkI8I32 | VAshrPkU8I32 =>
            vectorTernaryOperation(opcode)
              .map(operation => Instruction.VTernary(operation, destination, srcA, srcB, srcC, modifiers, clamp))
              .toRight[NonEmpty[String]](s"$opcode VOP3 execution is not implemented")
          case VMax3F16 | VMin3F16 | VMed3F16 | VMaxminF16 | VMinmaxF16 | VMax3NumF16 | VMin3NumF16 | VMed3NumF16 |
              VMaxminNumF16 | VMinmaxNumF16 | VMinimum3F16 | VMaximum3F16 | VMaximumminimumF16 | VMinimummaximumF16 =>
            halvedTernary
          case other if ExtendedVOP3Opcodes(other) || scaledConversion(other).nonEmpty =>
            extendedVOP3(machine, target, destination, srcA, srcB, srcC, modifiers, opSelection, clamp)
          case VCvtPkRtzF16F32_1 | VCvtPkrtzF16F32_2 =>
            Right(Instruction.VBinary(
              VectorBinaryOperation.PackRoundTowardZeroF16,
              destination,
              srcA,
              srcB,
              clamp = false,
              modifiers
            ))
          case VPackB32F16 =>
            Right(Instruction.VPackB32F16(
              destination,
              srcA,
              srcB,
              modifiers,
              machine(EncodingField.OP_SEL).toInt,
              clamp
            ))
          case packed @ (VPkAddU16 | VPkAddF16 | VPkMulLoU16 | VPkMulF16 | VPkMaxF16 | VPkMinF16 | VPkSubI16 |
              VPkLshrrevB16) =>
            vectorPackedBinaryOperation(packed)
              .toRight[NonEmpty[String]](s"$packed VOP3 execution is not implemented")
              .map(packedOperation =>
                Instruction.VPackedBinary(
                  operation = packedOperation,
                  dest = destination,
                  srcA = srcA,
                  srcB = srcB,
                  srcAHighForHigh = (machine(EncodingField.OMOD) & 1) != 0,
                  srcBHighForHigh = (machine(EncodingField.OMOD) & 2) != 0,
                  srcAHighForLow = (machine(EncodingField.OP_SEL) & 1) != 0,
                  srcBHighForLow = (machine(EncodingField.OP_SEL) & 2) != 0
                )
              )
          case VMadU64U32 | VMadCoU64U32 =>
            Right(Instruction.VMadU64U32(destination, carryDestination, srcA, srcB, srcC, clamp))
          case VMadI64I32 | VMadCoI64I32 =>
            Right(Instruction.VMadI64I32(destination, carryDestination, srcA, srcB, srcC, clamp))
          case VDivScaleF32 =>
            Right(Instruction.VDivScaleF32(
              dest = destination,
              conditionDest = carryDestination,
              srcA = srcA,
              srcB = srcB,
              srcC = srcC,
              modifiers = modifiers
            ))
          case VAddF32 | VSubF32 | VSubrevF32 | VMulF32 | VLdexpF32 | VMaxF32 | VMinF32 | VMinNumF32 | VMaxNumF32 |
              VMinimumF32 | VMaximumF32 | VMinLegacyF32 | VMaxLegacyF32 =>
            vectorBinaryOperation(opcode)
              .map(operation =>
                Instruction.VBinary(operation, destination, srcA, srcB, clamp, modifiers)
              )
              .toRight[NonEmpty[String]](s"$opcode VOP3 execution is not implemented")
          case VNotB32                    => Right(Instruction.VNot(destination, srcA))
          case VNotB16                    => Right(Instruction.VNot(destination, half(srcA, 0)))
          case VMovB16                    => Right(Instruction.VMove(destination, half(srcA, 0)))
          case VAndB16 | VOrB16 | VXorB16 =>
            Right(Instruction.VBinary(
              opcode match {
                case VAndB16 => VectorBinaryOperation.And32
                case VOrB16  => VectorBinaryOperation.Or32
                case _       => VectorBinaryOperation.Xor32
              },
              destination,
              half(srcA, 0),
              half(srcB, 1),
              clamp = false
            ))
          case VCndmaskB16 => srcC match {
              case InstructionSource.Scalar(_) =>
                Right(Instruction.VCndMask(destination, half(srcA, 0), half(srcB, 1), srcC))
              case _ => Left[NonEmpty[String], Nothing](s"$opcode condition is not a scalar mask")
            }
          case VSwapB32                                                 => Right(Instruction.VSwap(destination, srcA))
          case VMovrelsB32 | VMovreldB32 | VMovrelsdB32 | VMovrelsd2B32 =>
            Right(relativeMove(opcode, destination, srcA, target))
          case VMulLoI32 =>
            Right(Instruction.VBinary(VectorBinaryOperation.MultiplyLowU32, destination, srcA, srcB, clamp = false))
          case VMulLegacyF32 | VMulDx9ZeroF32 =>
            Right(Instruction.VBinary(
              VectorBinaryOperation.MultiplyLegacyF32,
              destination,
              srcA,
              srcB,
              clamp,
              modifiers
            ))
          case VSExpF32 | VSLogF32 | VSRsqF32 | VSSqrtF32 | VSExpF16 | VSLogF16 | VSRcpF16 | VSRsqF16 | VSSqrtF16 =>
            for {
              _ <- Either.cond[NonEmpty[String], Unit](
                modifiers == SourceModifiers.Unmodified && !clamp,
                (),
                s"$opcode modifiers are not implemented"
              )
              dest <- scalarDestination
            } yield Instruction.SFloatUnary(
              opcode match {
                case VSExpF32  => ScalarFloatUnaryOperation.ExponentBase2F32
                case VSLogF32  => ScalarFloatUnaryOperation.LogarithmBase2F32
                case VSRsqF32  => ScalarFloatUnaryOperation.ReciprocalSquareRootF32
                case VSSqrtF32 => ScalarFloatUnaryOperation.SquareRootF32
                case VSExpF16  => ScalarFloatUnaryOperation.ExponentBase2F16
                case VSLogF16  => ScalarFloatUnaryOperation.LogarithmBase2F16
                case VSRcpF16  => ScalarFloatUnaryOperation.ReciprocalF16
                case VSRsqF16  => ScalarFloatUnaryOperation.ReciprocalSquareRootF16
                case _         => ScalarFloatUnaryOperation.SquareRootF16
              },
              dest,
              srcA
            )
          case VBfrevB32                                       => Right(Instruction.VBitReverse(destination, srcA))
          case VMovB32                                         => Right(Instruction.VMove(destination, srcA))
          case VMovB64                                         => Right(Instruction.VMove64(destination, srcA))
          case VScreenPartition4seB32                          => Left(ScreenPartition)
          case VSubF16 | VSubrevF16 | VLdexpF16                => halvedBinary
          case other if convertForm(other).nonEmpty            => halvedConvert
          case other if vectorBitFindOperation(other).nonEmpty =>
            vectorBitFindOperation(other).map(Instruction.VBitFind(_, destination, srcA))
              .toRight[NonEmpty[String]](s"$other VOP3 execution is not implemented")
          case other if vectorBinaryOperation(other).exists(integerBinary(_)) =>
            if (opSelection != 0) Left[NonEmpty[String], Instruction](s"$other op_sel is not implemented")
            else vectorBinaryOperation(other).map(Instruction.VBinary(_, destination, srcA, srcB, clamp))
              .toRight[NonEmpty[String]](s"$other VOP3 execution is not implemented")
          case other =>
            for {
              comparison <-
                vectorComparison(other).toRight[NonEmpty[String]](s"$other VOP3 execution is not implemented")
              destination <- machine.scalar(EncodingField.VDST)
            } yield Instruction.VCompare(
              comparison,
              comparisonDestination(comparison, destination, target),
              srcA,
              srcB,
              modifiers
            )
        }
      }
      clamp   = machine.isSet(EncodingField.CLAMP)
      clamped = (clamp: Boolean) =>
        if (
          !clamp || ClampingVOP3Opcodes.contains(opcode) || vectorComparison(opcode).nonEmpty ||
          convertForm(opcode).nonEmpty
        ) lower(clamp)
        else clampResultWidth(opcode).toRight[NonEmpty[String]](s"$opcode clamp is not implemented")
          .flatMap(width => lower(false).map(Instruction.VClampResult(_, destination, width)))
      operation <-
        if (!machine.isSet(EncodingField.OMOD) || PackedVOP3OpSelectHighOpcodes.contains(opcode)) clamped(clamp)
        else for {
          width   <- vop3OutputWidth(opcode).toRight[NonEmpty[String]](s"$opcode output modifiers are not implemented")
          ignored <- clamped(clamp)
          applied <- lower(false)
        } yield Instruction.VOutputModified(
          ignored,
          applied,
          destination,
          machine(EncodingField.OMOD).toInt match {
            case EncodingConstants.OutputModifiers.MUL2 => OutputScale.Multiply2
            case EncodingConstants.OutputModifiers.MUL4 => OutputScale.Multiply4
            case _                                      => OutputScale.Divide2
          },
          width,
          clamp
        )
      routed <- if (machine.format.isDpp) dataParallel(machine, operation, target) else Right(operation)
    } yield halfResult(opcode, routed, destination, (opSelection & 8) != 0, target)
  }

  // the mixed-precision and dot forms route source 0 under DPP from GFX11 on
  private val DppRoutedOpcodes: Set[Opcode] = Set(
    VFmaMixF32,
    VFmaMixloF16,
    VFmaMixhiF16,
    VDot2F32F16,
    VDot2F32Bf16,
    VDot4F32Fp8Bf8,
    VDot4F32Bf8Fp8,
    VDot4F32Fp8Fp8,
    VDot4F32Bf8Bf8
  )

  /** The VOP3 opcodes [[extendedVOP3]] lowers. */
  private lazy val ExtendedVOP3Opcodes: Set[Opcode] = Set(
    VDot2cF32F16,
    VDot4cI32I8,
    VDot2cI32I16,
    VDot8cI32I4,
    VDot2cF32Bf16,
    VPkFmacF16,
    VAccvgprMovB32,
    VCvtF32Fp8,
    VCvtF32Bf8,
    VCvtPkF32Fp8,
    VCvtPkF32Bf8,
    VCvtPkFp8F32,
    VCvtPkBf8F32,
    VCvtSrFp8F32,
    VCvtSrBf8F32,
    VMadLegacyF32,
    VFmaLegacyF32,
    VFmacLegacyF32,
    VFmaDx9ZeroF32,
    VFmacDx9ZeroF32,
    VDot2F16F16,
    VDot2Bf16Bf16,
    VCvtSrF16F32,
    VCvtSrBf16F32
  )

  /** VOP3 forms of dot products, packed and legacy multiply-adds, accumulator moves, 8-bit float conversions, and the
    * stochastic and scaled conversions, of which those of more than two elements read whole vector register blocks.
    */
  private def extendedVOP3(
      machine: InstructionDecoder.Decoded,
      target: Target,
      destination: VectorRegisterIndex,
      srcA: InstructionSource,
      srcB: InstructionSource,
      srcC: InstructionSource,
      modifiers: SourceModifiers,
      opSelection: Int,
      clamp: Boolean
  ): Either[NonEmpty[String], Instruction] = {
    val opcode = machine.definition
    opcode match {
      // the VOP3 forms of the VOP2 dot products accumulate into the destination
      case VDot2cF32F16 if !clamp => Right(Instruction.VDot2F32F16(destination, srcA, srcB, accumulate = true))
      case VDot4cI32I8 | VDot2cI32I16 | VDot8cI32I4 | VDot2cF32Bf16 =>
        Right(Instruction.VDot(
          opcode match {
            case VDot4cI32I8   => DotOperation.SignedBytes
            case VDot2cI32I16  => DotOperation.SignedShorts
            case VDot2cF32Bf16 => DotOperation.BFloat16Halves
            case _             => DotOperation.SignedNibbles
          },
          destination,
          srcA,
          srcB,
          InstructionSource.VectorRegister(destination),
          clamp
        ))
      case VPkFmacF16 =>
        Right(Instruction.VPackedTernary(
          dest = destination,
          srcA = srcA,
          srcB = srcB,
          srcC = InstructionSource.VectorRegister(destination),
          lowSelection = 0,
          highSelection = 7,
          negateLow = 0,
          negateHigh = 0,
          clamp = clamp
        ))
      case VAccvgprMovB32 => srcA match {
          case InstructionSource.VectorRegister(index) => Right(Instruction.VAccumulatorMove(destination, index))
          case _ => Left[NonEmpty[String], Instruction](s"$opcode requires an accumulator register")
        }
      // the byte or word an 8-bit float conversion reads, which BYTE_SEL or OP_SEL names
      case VCvtF32Fp8 | VCvtF32Bf8 =>
        val byte = if (machine.contains(EncodingField.BYTE_SEL)) machine(EncodingField.BYTE_SEL).toInt else 0
        eightBitConversion(opcode, target).map(operation =>
          Instruction.VConvert(
            operation,
            destination,
            if (byte == 0) srcA
            else InstructionSource.Selected(
              srcA,
              UpperByteSelections(byte - 1),
              signExtend = false
            )
          )
        ).toRight[NonEmpty[String]](s"$opcode VOP3 execution is not implemented")
      case VCvtPkF32Fp8 | VCvtPkF32Bf8 =>
        eightBitConversion(opcode, target).map(operation =>
          Instruction.VConvert(
            operation,
            destination,
            if ((opSelection & 1) == 0) srcA
            else InstructionSource.Selected(srcA, SubDwordSelection.Word1, signExtend = false)
          )
        ).toRight[NonEmpty[String]](s"$opcode VOP3 execution is not implemented")
      case VCvtPkFp8F32 | VCvtPkBf8F32 =>
        Right(Instruction.VConvertNarrow(
          destination,
          srcA,
          srcB,
          eightBit(opcode == VCvtPkBf8F32, target),
          stochastic = false,
          ((opSelection >> 3) & 1).unsafe[Int in 0 ~ 3],
          modifiers
        ))
      case VCvtSrFp8F32 | VCvtSrBf8F32 =>
        Right(Instruction.VConvertNarrow(
          destination,
          srcA,
          srcB,
          eightBit(opcode == VCvtSrBf8F32, target),
          stochastic = true,
          ((opSelection >> 2) & 3).unsafe[Int in 0 ~ 3],
          modifiers
        ))
      case VMadLegacyF32 | VFmaLegacyF32 | VFmaDx9ZeroF32 =>
        Right(Instruction.VMadF32(
          if (opcode != VMadLegacyF32) VectorMadF32Operation.FusedMultiplyAddLegacy
          else VectorMadF32Operation.MultiplyAddLegacy,
          destination,
          srcA,
          srcB,
          srcC,
          modifiers,
          clamp
        ))
      case VFmacLegacyF32 | VFmacDx9ZeroF32 =>
        Right(Instruction.VMadF32(
          VectorMadF32Operation.FusedMultiplyAddLegacy,
          destination,
          srcA,
          srcB,
          InstructionSource.VectorRegister(destination),
          SourceModifiers(modifiers.absolute & 3, modifiers.negate & 3),
          clamp
        ))
      // the addend and the result are the halves OP_SEL bits 2 and 3 name
      case VDot2F16F16 | VDot2Bf16Bf16 =>
        Right(Instruction.VDot(
          if (opcode == VDot2F16F16) DotOperation.HalvesToHalf else DotOperation.BFloat16HalvesToBFloat16,
          destination,
          srcA,
          srcB,
          if ((opSelection & 4) != 0) InstructionSource.HighHalf(srcC) else srcC,
          clamp = false
        ))
      case VCvtSrF16F32 | VCvtSrBf16F32 =>
        Right(Instruction.VConvertStochasticHalf(
          destination,
          srcA,
          srcB,
          if (opcode == VCvtSrF16F32) Minifloats.Wide.Half else Minifloats.Wide.BFloat,
          (opSelection & 8) != 0,
          modifiers
        ))
      case other =>
        scaledConversion(other).toRight[NonEmpty[String]](s"$other VOP3 execution is not implemented").flatMap {
          conversion =>
            val dwords = conversion.dwords
            val blocks = Vec((srcA, dwords.a), (srcB, dwords.b), (srcC, dwords.c)).forall {
              case (InstructionSource.VectorRegister(_), _) => true
              case (_, count)                               => count <= 1
            }
            Either.cond[NonEmpty[String], Instruction](
              blocks,
              Instruction.VScaledConvert(conversion, destination, srcA, srcB, srcC, opSelection, modifiers),
              s"$other requires vector register blocks"
            )
        }
    }
  }

  /** A matrix operand's constant as the bits of each of its `format` elements. */
  private def matrixElements(
      value: InstructionSource,
      format: MatrixFormat
  ): Either[NonEmpty[String], InstructionSource] =
    value match {
      case InstructionSource.Floating(constant) =>
        format match {
          case MatrixFormat.F64 => Right(InstructionSource.Constant(IEEE754.doubleToBinary64(constant)))
          case MatrixFormat.F32 | MatrixFormat.XF32 =>
            Right(InstructionSource.Constant(IEEE754.floatToBinary32(constant.toFloat).toLong))
          case MatrixFormat.F16  => Right(InstructionSource.Constant(IEEE754.floatToBinary16(constant.toFloat).toLong))
          case MatrixFormat.BF16 =>
            Right(InstructionSource.Constant(IEEE754.floatToBinary32(constant.toFloat).toLong >>> 16))
          case other => Left(s"an inline float constant of $other elements is not implemented")
        }
      case other => Right(other)
    }

  /** Whether a VOP3P instruction is a gfx11 or gfx12 wave matrix product. */
  private def matrix(machine: InstructionDecoder.Decoded): Boolean = {
    val mnemonic = machine.definition.mnemonic.raw
    (mnemonic.startsWith("v_wmma_") || mnemonic.startsWith("v_swmmac_")) && !mnemonic.contains("_ld_scale")
  }

  /** MFMA and SMFMAC on the CDNA matrix core. CBSZ and ABID broadcast a block of A, or select a sparse A's index set;
    * the f8f6f4 forms take A's and B's formats from CBSZ and BLGP; the f64 forms negate A, B and C by BLGP's bits on
    * gfx940 and later; gfx950's scaled forms take E8M0 scales by ABID[0].
    */
  private def decodeMFMA(machine: InstructionDecoder.Decoded, target: Target): Either[NonEmpty[String], Instruction] = {
    val opcode         = machine.definition
    val mnemonic       = opcode.mnemonic.raw
    val blockBroadcast = machine(EncodingField.CBSZ).toInt
    val blockId        = machine(EncodingField.ABID).toInt
    val laneGroups     = machine(EncodingField.BLGP).toInt
    val selective      = mnemonic.endsWith("_f8f6f4")
    val clamp          = machine.contains(EncodingField.CLAMP) && machine.isSet(EncodingField.CLAMP)
    def fail(message: String): Either[NonEmpty[String], Unit]                    = Left(s"$opcode $message")
    def scale(field: EncodingField): Either[NonEmpty[String], InstructionSource] =
      vectorSource(machine(field).toInt, machine.literal, target)
    for {
      shape <- MatrixShape.of(mnemonic, target.isa, 64, blockBroadcast, laneGroups)
        .leftMap[NonEmpty[String]](message => s"$message: execution is not implemented")
      double = shape.c == MatrixFormat.F64
      _ <-
        if (selective || shape.sparse || double) Right(())
        else if ((1 << blockBroadcast) > shape.blocks || blockId >= (1 << blockBroadcast))
          fail(s"has invalid block broadcast CBSZ=$blockBroadcast ABID=$blockId")
        else Right(())
      _ <- if (double && laneGroups != 0 && target.isa == ISA.CDNA2) fail("negation is not defined on gfx90a")
      else Right(())
      _ <- if (clamp && !shape.c.integer) fail("clamp, which follows MODE.FP16_OVFL, is not implemented") else Right(())
      a <- vectorSource(machine(EncodingField.SRC0).toInt, None, target)
      b <- vectorSource(machine(EncodingField.SRC1).toInt, None, target)
      addend      <- vectorSource(machine(EncodingField.SRC2).toInt, None, target)
      c           <- if (shape.sparse) Right(addend) else matrixElements(addend, shape.c)
      destination <- machine.vector(EncodingField.VDST)
      scales      <-
        if (mnemonic.startsWith("v_mfma_scale_") && (blockId & 1) != 0)
          for {
            first  <- scale(EncodingField.SCALE_SRC0)
            second <- scale(EncodingField.SCALE_SRC1)
          } yield {
            val selection = machine(EncodingField.OP_SEL).toInt
            val high      = machine(EncodingField.OP_SEL_HI).toInt
            Some(MatrixScales(first, (selection & 1) | ((high & 1) << 1), second, ((selection >> 1) & 1) | (high & 2)))
          }
        else Right(None)
    } yield {
      val banks =
        machine(EncodingField.ACC).toInt |
          (if (!machine.contains(EncodingField.ACC_CD) || machine.isSet(EncodingField.ACC_CD)) 4 else 0)
      val broadcasting = !(selective || shape.sparse || double)
      Instruction.VMatrix(
        destination,
        a,
        b,
        c,
        shape,
        MatrixControls(
          banks = banks,
          blockBroadcast = if (broadcasting) blockBroadcast else 0,
          blockId = if (broadcasting) blockId else 0,
          laneGroups = if (broadcasting) laneGroups else 0,
          negateLow = if (double) laneGroups else 0,
          indexSet = if (shape.sparse && blockBroadcast == 0) blockId else 0,
          clamp = clamp,
          scales = scales
        )
      )
    }
  }

  /** WMMA and SWMMAC on gfx11 and gfx12. NEG and NEG_HI negate the low and high halves of A's and B's registers and
    * negate C or take its absolute value, or for integer elements make A and B signed; gfx11's OP_SEL[2] keeps a 16-bit
    * C/D in the high halves, and SWMMAC's OP_SEL selects the index set of its sparse A.
    */
  private def decodeWaveMatrix(
      machine: InstructionDecoder.Decoded,
      target: Target,
      waveSize: Int in (32, 64)
  ): Either[NonEmpty[String], Instruction] = {
    val opcode      = machine.definition
    val mnemonic    = opcode.mnemonic.raw
    val negateLow   = machine(EncodingField.NEG).toInt
    val negateHigh  = machine(EncodingField.NEG_HI).toInt
    val opSelection = machine(EncodingField.OP_SEL).toInt
    val clamp       = machine.isSet(EncodingField.CLAMP)
    for {
      shape <- MatrixShape.of(
        mnemonic,
        target.isa,
        waveSize,
        signedA = (negateLow & 1) != 0,
        signedB = (negateLow & 2) != 0
      ).leftMap[NonEmpty[String]](message => s"$message: execution is not implemented")
      integer = shape.a.integer
      _ <- Either.cond[NonEmpty[String], Unit](
        !clamp || shape.c.integer,
        (),
        s"$opcode clamp on floating-point results is not implemented"
      )
      a      <- vectorSource(machine(EncodingField.SRC0).toInt, None, target).flatMap(matrixElements(_, shape.a))
      b      <- vectorSource(machine(EncodingField.SRC1).toInt, None, target).flatMap(matrixElements(_, shape.b))
      addend <- operandSource(machine, machine(EncodingField.SRC2).toInt, machine.literal, target)
        .leftMap[NonEmpty[String]](error => s"$error in $opcode")
      c           <- if (shape.sparse) Right(addend) else matrixElements(addend, shape.c)
      destination <- machine.vector(EncodingField.VDST)
    } yield Instruction.VMatrix(
      destination,
      a,
      b,
      c,
      shape,
      MatrixControls(
        negateLow = if (integer) 0 else negateLow,
        negateHigh = if (integer) 0 else negateHigh,
        high = shape.halfRegisters && (opSelection & 4) != 0,
        indexSet = if (shape.sparse) opSelection & 3 else 0,
        clamp = clamp
      )
    )
  }

  /** Decode the packed 16-bit VOP3P encoding used by RDNA instructions such as `v_pk_lshrrev_b16`. */
  private def decodeVOP3P(
      machine: InstructionDecoder.Decoded,
      target: Target
  ): Either[NonEmpty[String], Instruction] = {
    val opcode          = machine.definition
    val opSelection     = machine(EncodingField.OP_SEL).toInt
    val opSelectionHigh = (machine(EncodingField.OP_SEL_HI) | (machine(EncodingField.OP_SEL_HI_2) << 2)).toInt
    val negateLow       = machine(EncodingField.NEG).toInt
    val negateHigh      = machine(EncodingField.NEG_HI).toInt
    val clamp           = machine.isSet(EncodingField.CLAMP)
    def operand(field: EncodingField) = operandSource(machine, machine(field).toInt, machine.literal, target)
      .leftMap[NonEmpty[String]](error => s"$error in $opcode")
    // A mixed-precision source that OP_SEL_HI converts from f16 reads an inline float constant as binary16.
    def mixSource(source: InstructionSource, index: Int): InstructionSource = source match {
      case InstructionSource.Floating(value) if (opSelectionHigh & (1 << index)) != 0 =>
        InstructionSource.Constant(Bits.low16(IEEE754.floatToBinary16(value.toFloat).toLong).toLong)
      case other => other
    }
    val routes = DppRoutedOpcodes(opcode)
    (for {
      _ <- Either.cond[NonEmpty[String], Unit](
        !machine.format.isDpp || routes,
        (),
        s"$opcode DPP execution is not implemented"
      )
      srcA <- if (machine.format.isDpp) Right(InstructionSource.DPP) else operand(EncodingField.SRC0)
      srcB <- operand(EncodingField.SRC1)
      srcC <- operand(EncodingField.SRC2)
      _    <- Either.cond[NonEmpty[String], Unit](
        (negateLow == 0 && negateHigh == 0) || !UnmodifiedVOP3POpcodes.contains(opcode),
        (),
        s"$opcode packed modifiers are not implemented"
      )
      destination <- machine.vector(EncodingField.VDST)
      operation   <- opcode match {
        case VAccvgprReadB32 | VAccvgprRead => srcA match {
            case InstructionSource.VectorRegister(index) => Right(Instruction.VAccumulatorRead(destination, index))
            case _ => Left[NonEmpty[String], Instruction](s"$opcode requires an accumulator register")
          }
        case VAccvgprWriteB32 | VAccvgprWrite  => Right(Instruction.VAccumulatorWrite(destination, srcA))
        case VPkAddF32 | VPkMulF32 | VPkFmaF32 =>
          val operation = opcode match {
            case VPkAddF32 => PackedF32Operation.Add
            case VPkMulF32 => PackedF32Operation.Multiply
            case _         => PackedF32Operation.Fma
          }
          Either.cond[NonEmpty[String], Instruction](
            !clamp,
            Instruction.VPackedF32(
              operation,
              destination,
              srcA,
              srcB,
              srcC,
              opSelection,
              opSelectionHigh,
              negateLow,
              negateHigh
            ),
            s"$opcode clamp modifier is not implemented"
          )
        case VPkMovB32 =>
          Either.cond[NonEmpty[String], Instruction](
            negateLow == 0 && negateHigh == 0 && !clamp,
            Instruction.VPackedMove32(destination, srcA, srcB, (opSelection & 1) != 0, (opSelection & 2) != 0),
            s"$opcode packed modifiers are not implemented"
          )
        case VPkFmaF16 | VPkMadU16 | VPkMadI16 | VPkMinimum3F16 | VPkMaximum3F16 =>
          Right(Instruction.VPackedTernary(
            dest = destination,
            srcA = srcA,
            srcB = srcB,
            srcC = srcC,
            lowSelection = opSelection,
            highSelection = opSelectionHigh,
            negateLow = negateLow,
            negateHigh = negateHigh,
            clamp = clamp,
            operation = opcode match {
              case VPkMadU16      => PackedTernaryOperation.MultiplyAddU16
              case VPkMadI16      => PackedTernaryOperation.MultiplyAddI16
              case VPkMinimum3F16 => PackedTernaryOperation.Minimum3PropagateF16
              case VPkMaximum3F16 => PackedTernaryOperation.Maximum3PropagateF16
              case _              => PackedTernaryOperation.FusedMultiplyAddF16
            }
          ))
        case VFmaMixloF16 | VFmaMixhiF16 | VMadMixloF16 | VMadMixhiF16 =>
          Right(Instruction.VFmaMixF16(
            dest = destination,
            srcA = mixSource(srcA, 0),
            srcB = mixSource(srcB, 1),
            srcC = mixSource(srcC, 2),
            opSelection = opSelection,
            opSelectionHi = opSelectionHigh,
            negate = negateLow,
            absolute = negateHigh,
            clamp = clamp,
            high = opcode == VFmaMixhiF16 || opcode == VMadMixhiF16,
            fused = opcode == VFmaMixloF16 || opcode == VFmaMixhiF16
          ))
        case VFmaMixF32 | VMadMixF32 =>
          Right(Instruction.VFmaMixF32(
            dest = destination,
            srcA = mixSource(srcA, 0),
            srcB = mixSource(srcB, 1),
            srcC = mixSource(srcC, 2),
            opSelection = opSelection,
            opSelectionHi = opSelectionHigh,
            negate = negateLow,
            absolute = negateHigh,
            clamp = clamp,
            fused = opcode == VFmaMixF32
          ))
        case VDot4U32U8 if !clamp =>
          Right(Instruction.VDot4U32U8(
            dest = destination,
            srcA = srcA,
            srcB = srcB,
            srcC = srcC
          ))
        case VDot4U32U8   => Right(Instruction.VDot(DotOperation.UnsignedBytes, destination, srcA, srcB, srcC, clamp))
        case VDot4I32I8   => Right(Instruction.VDot(DotOperation.SignedBytes, destination, srcA, srcB, srcC, clamp))
        case VDot2I32I16  => Right(Instruction.VDot(DotOperation.SignedShorts, destination, srcA, srcB, srcC, clamp))
        case VDot2U32U16  => Right(Instruction.VDot(DotOperation.UnsignedShorts, destination, srcA, srcB, srcC, clamp))
        case VDot8I32I4   => Right(Instruction.VDot(DotOperation.SignedNibbles, destination, srcA, srcB, srcC, clamp))
        case VDot8U32U4   => Right(Instruction.VDot(DotOperation.UnsignedNibbles, destination, srcA, srcB, srcC, clamp))
        case VDot2F32F16  => Right(Instruction.VDot(DotOperation.Halves, destination, srcA, srcB, srcC, clamp))
        case VDot2F32Bf16 =>
          Right(Instruction.VDot(DotOperation.BFloat16Halves, destination, srcA, srcB, srcC, clamp))
        case VDot4cI32I8 | VDot2cI32I16 | VDot8cI32I4 | VDot2cF32F16 =>
          vectorBinaryDot(opcode)
            .map(Instruction.VDot(_, destination, srcA, srcB, InstructionSource.VectorRegister(destination), clamp))
            .toRight[NonEmpty[String]](s"$opcode VOP3P execution is not implemented")
        // NEG_LO bits 0 and 1 make the elements of SRC0 and SRC1 signed
        case VDot4I32Iu8 | VDot8I32Iu4 =>
          val (signedA, signedB) = ((negateLow & 1) != 0, (negateLow & 2) != 0)
          Either.cond[NonEmpty[String], Instruction](
            negateHigh == 0 && (negateLow & 4) == 0,
            Instruction.VDot(
              if (opcode == VDot4I32Iu8) DotOperation.MixedBytes(signedA, signedB)
              else DotOperation.MixedNibbles(signedA, signedB),
              destination,
              srcA,
              srcB,
              srcC,
              clamp
            ),
            s"$opcode packed modifiers are not implemented"
          )
        // the addend alone takes modifiers: NEG_LO bit 2 negates it and NEG_HI bit 2 takes its magnitude
        case VDot4F32Fp8Bf8 | VDot4F32Bf8Fp8 | VDot4F32Fp8Fp8 | VDot4F32Bf8Bf8 =>
          val (formatA, formatB) = opcode match {
            case VDot4F32Fp8Bf8 => (Minifloat.E4M3, Minifloat.E5M2)
            case VDot4F32Bf8Fp8 => (Minifloat.E5M2, Minifloat.E4M3)
            case VDot4F32Fp8Fp8 => (Minifloat.E4M3, Minifloat.E4M3)
            case _              => (Minifloat.E5M2, Minifloat.E5M2)
          }
          Either.cond[NonEmpty[String], Instruction](
            ((negateLow | negateHigh) & 3) == 0 && !clamp,
            Instruction.VDot(
              DotOperation.EightBitFloats(formatA, formatB),
              destination,
              srcA,
              srcB,
              srcC,
              clamp,
              SourceModifiers(negateHigh & 4, negateLow & 4)
            ),
            s"$opcode packed modifiers are not implemented"
          )
        case packed @ (
              VPkAddU16 | VPkAddI16 | VPkAddF16 | VPkMulLoU16 | VPkMulF16 | VPkMaxF16 | VPkMinF16 | VPkMaxNumF16 |
              VPkMinNumF16 | VPkMaxI16 | VPkMinimumF16 | VPkMaximumF16 |
              VPkMaxU16 | VPkMinI16 | VPkMinU16 | VPkSubI16 | VPkSubU16 | VPkLshlrevB16 | VPkLshrrevB16 | VPkAshrrevI16
            ) =>
          for {
            _ <-
              Either.cond[NonEmpty[String], Unit](
                negateLow == 0 && negateHigh == 0,
                (),
                s"$packed packed modifiers are not implemented"
              )
            packedOperation <- vectorPackedBinaryOperation(packed)
              .toRight[NonEmpty[String]](s"$packed VOP3P execution is not implemented")
          } yield Instruction.VPackedBinary(
            operation = packedOperation,
            dest = destination,
            srcA = srcA,
            srcB = srcB,
            srcAHighForHigh = (opSelectionHigh & 1) != 0,
            srcBHighForHigh = (opSelectionHigh & 2) != 0,
            srcAHighForLow = (opSelection & 1) != 0,
            srcBHighForLow = (opSelection & 2) != 0,
            clamp = clamp
          )
        case other => Left[NonEmpty[String], Nothing](s"$other VOP3P execution is not implemented")
      }
    } yield operation).flatMap(operation =>
      if (machine.format.isDpp) dataParallel(machine, operation, target) else Right(operation)
    )
  }

  /** GWS operations take their data from the ADDR field; GDS forms reach the empty GDS segment of an HSA queue, which
    * reads and returns zero and drops writes.
    */
  private def decodeDS(machine: InstructionDecoder.Decoded): Either[NonEmpty[String], Instruction] = {
    val opcode                                   = machine.definition
    def sync(operation: GlobalWaveSyncOperation) = machine.vector(EncodingField.ADDR).map(data =>
      Instruction.GlobalWaveSync(
        opcode.mnemonic.raw,
        operation,
        data,
        machine(EncodingField.OFFSET0).toInt | (machine(EncodingField.OFFSET1).toInt << 8),
        if (machine.isSet(EncodingField.ACC)) RegisterBank.Accumulator else RegisterBank.Vector
      )
    )
    opcode match {
      case DsGwsInit           => sync(GlobalWaveSyncOperation.Init)
      case DsGwsBarrier        => sync(GlobalWaveSyncOperation.Barrier)
      case DsGwsSemaV          => sync(GlobalWaveSyncOperation.SemaphoreV)
      case DsGwsSemaBr         => sync(GlobalWaveSyncOperation.SemaphoreBulkRelease)
      case DsGwsSemaReleaseAll => sync(GlobalWaveSyncOperation.SemaphoreReleaseAll)
      case DsGwsSemaP          => sync(GlobalWaveSyncOperation.SemaphoreP)
      case DsOrderedCount      =>
        machine.vector(EncodingField.VDST).map(Instruction.VMove(_, InstructionSource.Constant(0L)))
      case _ if machine.isSet(EncodingField.GDS) => decodeLocal(machine).flatMap(globalShare(opcode, _))
      case _                                     => decodeLocal(machine).flatMap(accumulated(machine, _))
    }
  }

  /** CDNA's DS ACC bit moves the data and the result through the accumulation registers instead; addresses stay in
    * VGPRs.
    */
  private def accumulated(
      machine: InstructionDecoder.Decoded,
      local: Instruction
  ): Either[NonEmpty[String], Instruction] = {
    val bank = RegisterBank.Accumulator
    if (!machine.isSet(EncodingField.ACC)) Right(local)
    else local match {
      case load @ Instruction.Load(dest = _)             => Right(load.copy(bank = bank))
      case store @ Instruction.Store(src = _)            => Right(store.copy(bank = bank))
      case atomic @ Instruction.Atomic(dest = _)         => Right(atomic.copy(bank = bank))
      case exchange @ Instruction.CompareSwap(dest = _)  => Right(exchange.copy(bank = bank))
      case Instruction.AtomicExtended(operation, access) =>
        Right(Instruction.AtomicExtended(operation, access.copy(bank = bank)))
      case Instruction.ExchangePair(first, second) =>
        Right(Instruction.ExchangePair(first.copy(bank = bank), second.copy(bank = bank)))
      case pair @ Instruction.LoadPair(dest = _)        => Right(pair.copy(bank = bank))
      case pair @ Instruction.StorePair(address = _)    => Right(pair.copy(bank = bank))
      case permute @ Instruction.DSPermute(dest = _)    => Right(permute.copy(bank = bank))
      case permute @ Instruction.DSBPermute(dest = _)   => Right(permute.copy(bank = bank))
      case swizzle @ Instruction.DSSwizzle(dest = _)    => Right(swizzle.copy(bank = bank))
      case counter @ Instruction.LocalCounter(dest = _) => Right(counter.copy(bank = bank))
      case _ => Left(s"${machine.definition} with accumulation registers is not implemented")
    }
  }

  private def globalShare(opcode: Opcode, local: Instruction): Either[NonEmpty[String], Instruction] = {
    def zero(dest: VectorRegisterIndex, bytes: Int): Either[NonEmpty[String], Instruction] = bytes match {
      case 8           => Right(Instruction.VMove64(dest, InstructionSource.Constant(0L)))
      case b if b <= 4 => Right(Instruction.VMove(dest, InstructionSource.Constant(0L)))
      case _           => Left[NonEmpty[String], Instruction](s"$opcode GDS execution is not implemented")
    }
    local match {
      case Instruction.Load(dest = dest, bytes = bytes)                            => zero(dest, bytes)
      case Instruction.LoadPair(dest = dest, bytes = bytes)                        => zero(dest, bytes * 2)
      case Instruction.LocalCounter(dest = dest)                                   => zero(dest, 4)
      case Instruction.Atomic(dest = dest, bytes = bytes, returnValue = true)      => zero(dest, bytes)
      case Instruction.CompareSwap(dest = dest, bytes = bytes, returnValue = true) => zero(dest, bytes)
      case Instruction.AtomicExtended(_, Instruction.CompareSwap(dest = dest, bytes = bytes, returnValue = true)) =>
        zero(dest, bytes)
      case Instruction.Store(src = _) | Instruction.StorePair(address = _) | Instruction.Atomic(returnValue = false) |
          Instruction.CompareSwap(returnValue = false) | Instruction.AtomicExtended(_, _) => Right(Instruction.Wait)
      case _ => Left[NonEmpty[String], Instruction](s"$opcode GDS execution is not implemented")
    }
  }

  private def decodeLocal(machine: InstructionDecoder.Decoded): Either[NonEmpty[String], Instruction] = {

    val opcode   = machine.definition
    val offsetA  = machine(EncodingField.OFFSET0).toInt
    val offsetB  = machine(EncodingField.OFFSET1).toInt
    val dsOffset = offsetA | (offsetB << 8)
    def extended(
        operation: ExtendedAtomicOperation,
        bytes: Int in (4, 8),
        a: VectorRegisterIndex,
        b: VectorRegisterIndex
    )(
        dest: VectorRegisterIndex,
        address: VectorRegisterIndex
    ) = Instruction.AtomicExtended(
      operation,
      Instruction.CompareSwap(dest, MemoryAddress.Local(address, dsOffset), b, a, bytes, dsAtomicReturnsValue(opcode))
    )
    def exchanges(bytes: Int in (4, 8), scale: Int)(
        dest: VectorRegisterIndex,
        address: VectorRegisterIndex,
        a: VectorRegisterIndex,
        b: VectorRegisterIndex
    ) = Instruction.ExchangePair(
      Instruction.Atomic(AtomicOperation.Exchange, dest, MemoryAddress.Local(address, offsetA * scale), a, bytes, true),
      Instruction.Atomic(
        AtomicOperation.Exchange,
        dest.offsetBy(bytes.widen[Int >= 0] / 4),
        MemoryAddress.Local(address, offsetB * scale),
        b,
        bytes,
        true
      )
    )
    for {
      address <- machine.vector(EncodingField.ADDR)
      dataA   <- machine.vector(EncodingField.DATA0)
      dataB   <- machine.vector(EncodingField.DATA1)
      dest    <- machine.vector(EncodingField.VDST)
      local = MemoryAddress.Local(address, dsOffset)
      operation <- opcode match {
        case DsReadAddtidB32 | DsLoadAddtidB32 =>
          Right(Instruction.Load(dest, MemoryAddress.LaneLocal(dsOffset), 4, false, LoadPlacement.Full))
        case DsWriteAddtidB32 | DsStoreAddtidB32 =>
          Right(Instruction.Store(dataA, MemoryAddress.LaneLocal(dsOffset), 4, false))
        case DsReadB64TrB16           => Right(Instruction.LoadTranspose(dest, local, TransposeLayout.Rows16))
        case DsReadB64TrB8            => Right(Instruction.LoadTranspose(dest, local, TransposeLayout.Rows8))
        case DsReadB64TrB4            => Right(Instruction.LoadTranspose(dest, local, TransposeLayout.Rows4))
        case DsReadB96TrB6            => Right(Instruction.LoadTranspose(dest, local, TransposeLayout.Rows6))
        case DsPermuteB32             => Right(Instruction.DSPermute(dest, address, dataA, dsOffset))
        case DsAppend                 => Right(Instruction.LocalCounter(dest, dsOffset, decrement = false))
        case DsConsume                => Right(Instruction.LocalCounter(dest, dsOffset, decrement = true))
        case DsRsubU32 | DsRsubRtnU32 =>
          Right(extended(ExtendedAtomicOperation.ReverseSubtract, 4, dataA, dataA)(dest, address))
        case DsRsubU64 | DsRsubRtnU64 =>
          Right(extended(ExtendedAtomicOperation.ReverseSubtract, 8, dataA, dataA)(dest, address))
        case DsMskorB32 | DsMskorRtnB32 =>
          Right(extended(ExtendedAtomicOperation.MaskOr, 4, dataA, dataB)(dest, address))
        case DsMskorB64 | DsMskorRtnB64 =>
          Right(extended(ExtendedAtomicOperation.MaskOr, 8, dataA, dataB)(dest, address))
        case DsWrapRtnB32 => Right(extended(ExtendedAtomicOperation.Wrap, 4, dataA, dataB)(dest, address))
        // GCN compares with DATA0 and stores DATA1; GFX11 swaps them
        case DsCmpstF32 | DsCmpstRtnF32 =>
          Right(extended(ExtendedAtomicOperation.FloatCompareSwap, 4, dataA, dataB)(dest, address))
        case DsCmpstF64 | DsCmpstRtnF64 =>
          Right(extended(ExtendedAtomicOperation.FloatCompareSwap, 8, dataA, dataB)(dest, address))
        case DsCmpstoreF32 | DsCmpstoreRtnF32 =>
          Right(extended(ExtendedAtomicOperation.FloatCompareSwap, 4, dataB, dataA)(dest, address))
        case DsCmpstoreF64 | DsCmpstoreRtnF64 =>
          Right(extended(ExtendedAtomicOperation.FloatCompareSwap, 8, dataB, dataA)(dest, address))
        case DsSubClampU32 | DsSubClampRtnU32 =>
          Right(extended(ExtendedAtomicOperation.SubtractClamp, 4, dataA, dataA)(dest, address))
        case DsCondSubU32 | DsCondSubRtnU32 =>
          Right(extended(ExtendedAtomicOperation.ConditionalSubtract, 4, dataA, dataA)(dest, address))
        case DsCondxchg32RtnB64 =>
          Right(extended(ExtendedAtomicOperation.ConditionalExchange, 8, dataA, dataA)(dest, address))
        case DsWrxchg2RtnB32 | DsStorexchg2addrRtnB32             => Right(exchanges(4, 4)(dest, address, dataA, dataB))
        case DsWrxchg2RtnB64 | DsStorexchg2addrRtnB64             => Right(exchanges(8, 8)(dest, address, dataA, dataB))
        case DsWrxchg2st64RtnB32 | DsStorexchg2addrStride64RtnB32 =>
          Right(exchanges(4, 256)(dest, address, dataA, dataB))
        case DsWrxchg2st64RtnB64 | DsStorexchg2addrStride64RtnB64 =>
          Right(exchanges(8, 512)(dest, address, dataA, dataB))
        case DsBpermuteB32   => Right(Instruction.DSBPermute(dest, address, dataA, dsOffset))
        case DsBpermuteFiB32 => Right(Instruction.DSBPermute(dest, address, dataA, dsOffset, fetchInactive = true))
        // DS_SWIZZLE reads its source from the address field.
        case DsSwizzleB32 => Right(Instruction.DSSwizzle(dest, address, dsOffset))
        case atomic @ (
              DsAddU32 | DsSubU32 | DsIncU32 | DsDecU32 |
              DsMinI32 | DsMaxI32 | DsMinU32 | DsMaxU32 |
              DsAndRtnB32 | DsOrRtnB32 | DsXorRtnB32 |
              DsAndRtnB64 | DsOrRtnB64 | DsXorRtnB64 |
              DsAddRtnU32 | DsAddRtnU64 | DsSubRtnU32 | DsSubRtnU64 |
              DsIncRtnU32 | DsIncRtnU64 | DsDecRtnU32 | DsDecRtnU64 |
              DsMinRtnI32 | DsMinRtnI64 | DsMinRtnU32 | DsMinRtnU64 |
              DsMaxRtnI32 | DsMaxRtnI64 | DsMaxRtnU32 | DsMaxRtnU64 |
              DsAddRtnF32 | DsAddRtnF64 | DsMinRtnF32 | DsMinRtnF64 |
              DsMaxRtnF32 | DsMaxRtnF64 | DsAndB32 | DsOrB32 | DsXorB32 | DsAddF32 | DsMinF32 | DsMaxF32 |
              DsAddU64 | DsSubU64 | DsIncU64 | DsDecU64 | DsMinI64 | DsMaxI64 | DsMinU64 | DsMaxU64 | DsAndB64 |
              DsOrB64 | DsXorB64 | DsAddF64 | DsMinF64 | DsMaxF64 | DsWrxchgRtnB32 | DsStorexchgRtnB32 |
              DsWrxchgRtnB64 | DsStorexchgRtnB64 | DsPkAddF16 | DsPkAddRtnF16 | DsPkAddBf16 | DsPkAddRtnBf16 |
              DsMinNumF32 | DsMinNumRtnF32 | DsMaxNumF32 | DsMaxNumRtnF32 | DsMinNumF64 | DsMinNumRtnF64 | DsMaxNumF64 |
              DsMaxNumRtnF64
            ) =>
          dsAtomicOperation(atomic)
            .map((operation, bytes) =>
              Instruction.Atomic(operation, dest, local, dataA, bytes, dsAtomicReturnsValue(atomic))
            )
            .toRight[NonEmpty[String]](s"$atomic DS execution is not implemented")
        // GCN compares with DATA0 and stores DATA1; GFX11 swaps them
        case DsCmpstB32 | DsCmpstRtnB32 =>
          Right(Instruction.CompareSwap(dest, local, dataB, dataA, 4, dsAtomicReturnsValue(opcode)))
        case DsCmpstB64 | DsCmpstRtnB64 =>
          Right(Instruction.CompareSwap(dest, local, dataB, dataA, 8, dsAtomicReturnsValue(opcode)))
        case DsCmpstoreB32 | DsCmpstoreRtnB32 =>
          Right(Instruction.CompareSwap(dest, local, dataA, dataB, 4, dsAtomicReturnsValue(opcode)))
        case DsCmpstoreB64 | DsCmpstoreRtnB64 =>
          Right(Instruction.CompareSwap(dest, local, dataA, dataB, 8, dsAtomicReturnsValue(opcode)))
        case DsWriteB8 | DsStoreB8             => Right(Instruction.Store(dataA, local, 1, false))
        case DsWriteB16 | DsStoreB16           => Right(Instruction.Store(dataA, local, 2, false))
        case DsWriteB32 | DsStoreB32           => Right(Instruction.Store(dataA, local, 4, false))
        case DsWriteB64 | DsStoreB64           => Right(Instruction.Store(dataA, local, 8, false))
        case DsWriteB96 | DsStoreB96           => Right(Instruction.Store(dataA, local, 12, false))
        case DsWriteB128 | DsStoreB128         => Right(Instruction.Store(dataA, local, 16, false))
        case DsWriteB8D16Hi | DsStoreB8D16Hi   => Right(Instruction.Store(dataA, local, 1, true))
        case DsWriteB16D16Hi | DsStoreB16D16Hi => Right(Instruction.Store(dataA, local, 2, true))
        case DsWrite2B32 | DsStore2addrB32     =>
          Right(Instruction.StorePair(address, dataA, dataB, 4, offsetA * 4, offsetB * 4))
        case DsWrite2B64 | DsStore2addrB64 =>
          Right(Instruction.StorePair(address, dataA, dataB, 8, offsetA * 8, offsetB * 8))
        case DsWrite2st64B32 | DsStore2addrStride64B32 =>
          Right(Instruction.StorePair(address, dataA, dataB, 4, offsetA * 256, offsetB * 256))
        case DsWrite2st64B64 | DsStore2addrStride64B64 =>
          Right(Instruction.StorePair(address, dataA, dataB, 8, offsetA * 512, offsetB * 512))
        case DsReadU8 | DsLoadU8             => Right(Instruction.Load(dest, local, 1, false, LoadPlacement.Full))
        case DsReadU16 | DsLoadU16           => Right(Instruction.Load(dest, local, 2, false, LoadPlacement.Full))
        case DsReadI8 | DsLoadI8             => Right(Instruction.Load(dest, local, 1, true, LoadPlacement.Full))
        case DsReadI16 | DsLoadI16           => Right(Instruction.Load(dest, local, 2, true, LoadPlacement.Full))
        case DsReadU8D16 | DsLoadU8D16       => Right(Instruction.Load(dest, local, 1, false, LoadPlacement.Low))
        case DsReadU8D16Hi | DsLoadU8D16Hi   => Right(Instruction.Load(dest, local, 1, false, LoadPlacement.High))
        case DsReadU16D16 | DsLoadU16D16     => Right(Instruction.Load(dest, local, 2, false, LoadPlacement.Low))
        case DsReadU16D16Hi | DsLoadU16D16Hi => Right(Instruction.Load(dest, local, 2, false, LoadPlacement.High))
        case DsReadI8D16 | DsLoadI8D16       => Right(Instruction.Load(dest, local, 1, true, LoadPlacement.Low))
        case DsReadI8D16Hi | DsLoadI8D16Hi   => Right(Instruction.Load(dest, local, 1, true, LoadPlacement.High))
        case DsReadB32 | DsLoadB32           => Right(Instruction.Load(dest, local, 4, false, LoadPlacement.Full))
        case DsReadB64 | DsLoadB64           => Right(Instruction.Load(dest, local, 8, false, LoadPlacement.Full))
        case DsReadB96 | DsLoadB96           => Right(Instruction.Load(dest, local, 12, false, LoadPlacement.Full))
        case DsReadB128 | DsLoadB128         => Right(Instruction.Load(dest, local, 16, false, LoadPlacement.Full))
        case DsRead2B32 | DsLoad2addrB32     => Right(Instruction.LoadPair(dest, address, 4, offsetA * 4, offsetB * 4))
        case DsRead2B64 | DsLoad2addrB64     => Right(Instruction.LoadPair(dest, address, 8, offsetA * 8, offsetB * 8))
        case DsRead2st64B32 | DsLoad2addrStride64B32 =>
          Right(Instruction.LoadPair(dest, address, 4, offsetA * 256, offsetB * 256))
        case DsRead2st64B64 | DsLoad2addrStride64B64 =>
          Right(Instruction.LoadPair(dest, address, 8, offsetA * 512, offsetB * 512))
        case source2 if source2.mnemonic.raw.contains("_src2_") =>
          // the offset fields hold the displacement unless OFFSET1[7] takes it from ADDR[31:17]
          val displacement = Option.when((offsetB & 0x80) == 0)(
            (((offsetB & 0x40) << 9) | ((offsetB & 0x7f) << 8) | offsetA).toShort.toInt
          )
          val bytes = if (source2.mnemonic.raw.endsWith("64")) 8 else 4
          source2Operation(source2.mnemonic.raw)
            .map((operation, reverse) =>
              Instruction.LocalSource2(operation, address, displacement, bytes.unsafe[Int in (4, 8)], reverse)
            )
            .toRight[NonEmpty[String]](s"$source2 DS execution is not implemented")
        case other => Left[NonEmpty[String], Nothing](s"$other DS execution is not implemented")
      }
    } yield operation
  }

  private val Source2 = "ds_([a-z]+)_src2_([biuf])(32|64)".r

  /** The update a DS source-2 instruction makes, and whether it subtracts the old value from the other. */
  private def source2Operation(name: String): Option[(AtomicOperation, Boolean)] = name match {
    case Source2(operation, kind, _) =>
      (operation, kind) match {
        case ("add", "f") => Some((AtomicOperation.FloatAdd, false))
        case ("add", _)   => Some((AtomicOperation.Add, false))
        case ("sub", _)   => Some((AtomicOperation.Subtract, false))
        case ("rsub", _)  => Some((AtomicOperation.Subtract, true))
        case ("inc", _)   => Some((AtomicOperation.IncrementWrap, false))
        case ("dec", _)   => Some((AtomicOperation.DecrementWrap, false))
        case ("min", "i") => Some((AtomicOperation.MinimumSigned, false))
        case ("max", "i") => Some((AtomicOperation.MaximumSigned, false))
        case ("min", "u") => Some((AtomicOperation.MinimumUnsigned, false))
        case ("max", "u") => Some((AtomicOperation.MaximumUnsigned, false))
        case ("min", "f") => Some((AtomicOperation.FloatMinimum, false))
        case ("max", "f") => Some((AtomicOperation.FloatMaximum, false))
        case ("and", _)   => Some((AtomicOperation.And, false))
        case ("or", _)    => Some((AtomicOperation.Or, false))
        case ("xor", _)   => Some((AtomicOperation.Xor, false))
        case ("write", _) => Some((AtomicOperation.Exchange, false))
        case _            => None
      }
    case _ => None
  }

  private def decodeGlobalOrFlat(
      machine: InstructionDecoder.Decoded,
      target: Target
  ): Either[NonEmpty[String], Instruction] = {
    val wide        = machine.layout.bits > 64
    val returnValue =
      if (wide) (machine(EncodingField.TH) & 1L) != 0
      else machine.isSet(if (machine.contains(EncodingField.GLC)) EncodingField.GLC else EncodingField.SC0)
    val coherent = if (wide) machine(EncodingField.SCOPE) >= 2 else returnValue
    val segment  =
      if (machine.format == EncodingFormat.VSCRATCH || machine.format == EncodingFormat.FLAT_SCRATCH) 1L
      else if (machine.format == EncodingFormat.VGLOBAL) 2L
      else if (machine.format == EncodingFormat.VFLAT) 0L
      else machine(EncodingField.SEG)
    // Legacy FLAT offsets are unsigned; GLOBAL/SCRATCH and the wider RDNA4 encodings are signed.
    val memoryOffset =
      if (segment == 0 && !wide) machine(EncodingField.OFFSET).toInt else machine.signed(EncodingField.OFFSET).toInt
    for {
      vectorAddress <- machine.vector(if (wide) EncodingField.VADDR else EncodingField.ADDR)
      dataRegister  <- machine.vector(if (wide) EncodingField.VSRC else EncodingField.DATA)
      scalarAddress <- machine.scalar(EncodingField.SADDR)
      destination   <- machine.vector(EncodingField.VDST)
      // RDNA3 and later encode the null scalar address as 124.
      absentScalar = isAbsentScalarOffset(scalarAddress.toLong) ||
        (target.isa.isGFX11OrLater && scalarAddress == 124)
      scalar = Option.when(!absentScalar)(scalarAddress)
      address <-
        if (segment == 1) {
          // without SVE, a scalar address replaces the vector one
          val vector = Option.when(
            if (machine.contains(EncodingField.SVE)) machine.isSet(EncodingField.SVE) else absentScalar
          )(vectorAddress)
          Right(MemoryAddress.Scratch(vector, scalar, memoryOffset))
        } else if (segment == 2) Right(MemoryAddress.Global(vectorAddress, scalar, memoryOffset))
        else if (segment == 0 && (absentScalar || (!wide && scalarAddress == 0)))
          Right(MemoryAddress.Flat(vectorAddress, memoryOffset))
        // the wider encodings' flat access adds a 32-bit VGPR to a scalar base, as a global one does
        else if (segment == 0 && wide) Right(MemoryAddress.Flat(vectorAddress, memoryOffset, scalar))
        else
          Left[
            NonEmpty[String],
            Nothing
          ](s"VMEM scalar address s[$scalarAddress:${scalarAddress + 1}] is not implemented")
      // Only global and flat loads honour the coherence of their cache policy.
      ordered = coherent && segment != 1
      bank    = if (machine.isSet(EncodingField.ACC)) RegisterBank.Accumulator else RegisterBank.Vector
      operation <- machine.definition match {
        case GlobalInv                                   => Right(Instruction.CacheInvalidate)
        case GlobalWb                                    => Right(Instruction.Wait)
        case GlobalWbinv                                 => Right(Instruction.CacheWritebackInvalidate)
        case GlobalLoadDwordAddtid | GlobalLoadAddtidB32 =>
          Right(Instruction.Load(
            destination,
            MemoryAddress.LaneGlobal(scalar, memoryOffset),
            4,
            false,
            LoadPlacement.Full,
            bank,
            ordered
          ))
        case GlobalStoreDwordAddtid | GlobalStoreAddtidB32 =>
          Right(Instruction.Store(dataRegister, MemoryAddress.LaneGlobal(scalar, memoryOffset), 4, false, bank))
        // LDS DMA: each lane's element lands from M0 plus the offset, at its index times the element's dwords
        case GlobalLoadLdsDword | ScratchLoadLdsDword => Right(Instruction.LoadLocal(address, 4, false, memoryOffset))
        case GlobalLoadLdsDwordx3                     => Right(Instruction.LoadLocal(address, 12, false, memoryOffset))
        case GlobalLoadLdsDwordx4                     => Right(Instruction.LoadLocal(address, 16, false, memoryOffset))
        case GlobalLoadLdsUbyte | ScratchLoadLdsUbyte => Right(Instruction.LoadLocal(address, 1, false, memoryOffset))
        case GlobalLoadLdsSbyte | ScratchLoadLdsSbyte => Right(Instruction.LoadLocal(address, 1, true, memoryOffset))
        case GlobalLoadLdsUshort | ScratchLoadLdsUshort => Right(Instruction.LoadLocal(address, 2, false, memoryOffset))
        case GlobalLoadLdsSshort | ScratchLoadLdsSshort => Right(Instruction.LoadLocal(address, 2, true, memoryOffset))
        case GlobalLoadTrB128 => Right(Instruction.LoadTranspose(destination, address, TransposeLayout.Blocks16))
        case GlobalLoadTrB64 => Right(Instruction.LoadTranspose(destination, address, TransposeLayout.InterleavedBytes))
        case other           =>
          VectorMemoryAccess.of(other).toRight[NonEmpty[String]](s"$other VMEM execution is not implemented").flatMap {
            case VectorMemoryAccess.Load(bytes, signed, placement) =>
              Right(Instruction.Load(
                destination,
                address,
                bytes,
                signed,
                placement,
                bank,
                ordered && !signed && placement == LoadPlacement.Full
              ))
            case VectorMemoryAccess.Store(bytes, high) =>
              Right(Instruction.Store(dataRegister, address, bytes, high, bank))
            case VectorMemoryAccess.Atomic(operation, bytes) =>
              Right(Instruction.Atomic(operation, destination, address, dataRegister, bytes, returnValue, bank))
            // the comparand follows the data
            case VectorMemoryAccess.CompareSwap(bytes) =>
              (dataRegister + bytes / 4).refine[VectorRegisterIndex]
                .leftMap[NonEmpty[String]](_ => s"$other comparand exceeds the vector register bank")
                .map(Instruction.CompareSwap(destination, address, dataRegister, _, bytes, returnValue, bank))
            case _ if bank == RegisterBank.Accumulator =>
              Left[
                NonEmpty[String],
                Instruction
              ]("accumulator register bank is not implemented for this VMEM operation")

            // a compare-swap's comparand is operand A; a lone operand is both
            case VectorMemoryAccess.Extended(operation, bytes) =>
              val paired = operation == ExtendedAtomicOperation.FloatCompareSwap
              (dataRegister + (if (paired) bytes / 4 else 0)).refine[VectorRegisterIndex]
                .leftMap[NonEmpty[String]](_ => s"$other comparand exceeds the vector register bank")
                .map(comparand =>
                  Instruction.AtomicExtended(
                    operation,
                    Instruction.CompareSwap(destination, address, dataRegister, comparand, bytes, returnValue)
                  )
                )
          }
      }
    } yield operation
  }

  private def decodeBuffer(machine: InstructionDecoder.Decoded, target: Target): Either[NonEmpty[String], Instruction] =
    machine.definition match {
      case BufferInv | BufferInvl2 | BufferWbinvl1 | BufferWbinvl1Vol | BufferWbinvl1Sc | BufferGl0Inv | BufferGl1Inv =>
        Right(Instruction.CacheInvalidate)
      // Wait already flushes staged host writes before execution continues.
      case BufferWbl2 => Right(Instruction.Wait)
      case _          => VMResourceDecoder.buffer(machine, target)
    }

  /** F16 operands read an inline float constant as its binary16 value in the low half with zero above. The mixed
    * precision V_FMA_MIX and V_MAD_MIX sources keep the single-precision pattern.
    */
  private def operandSource(
      machine: InstructionDecoder.Decoded,
      encoding: Int,
      literal: Option[Long],
      target: Target
  ): Either[NonEmpty[String], InstructionSource] = {
    val name = machine.definition.mnemonic.raw
    vectorSource(encoding, literal, target).map {
      case InstructionSource.Floating(value) if name.endsWith("_f16") && !name.contains("_mix") =>
        InstructionSource.Constant(Bits.low16(IEEE754.floatToBinary16(value.toFloat).toLong).toLong)
      case other => other
    }
  }

  /** A binary16 SALU operand reads an inline float constant as its binary16 value. */
  private def half(opcode: Opcode, source: InstructionSource): InstructionSource = source match {
    case InstructionSource.Floating(value) if opcode.mnemonic.raw.endsWith("_f16") =>
      InstructionSource.Constant(Bits.low16(IEEE754.floatToBinary16(value.toFloat).toLong).toLong)
    case other => other
  }

  private def scalarSource(
      encoding: Int,
      literal: Option[Long],
      target: Target
  ): Either[NonEmpty[String], InstructionSource] =
    vectorSource(encoding, literal, target).filterOrElse(
      {
        case InstructionSource.VectorRegister(_) => false
        case _                                   => true
      },
      unsupportedSource(encoding)
    )

  private def unsupportedSource(encoding: Int): NonEmpty[String] =
    s"source encoding $encoding is not implemented or its literal is missing"

  private def vectorSource(
      encoding: Int,
      literal: Option[Long],
      target: Target
  ): Either[NonEmpty[String], InstructionSource] =
    SourceOperand.decode(encoding, target.isa).flatMap {
      case SourceOperand.Scalar(index) if nullRegister(target.isa).contains(index) =>
        Some(InstructionSource.Constant(0L))
      case SourceOperand.Scalar(index)                  => Some(InstructionSource.Scalar(index))
      case SourceOperand.VectorRegister(index)          => Some(InstructionSource.VectorRegister(index))
      case SourceOperand.Integer(value)                 => Some(InstructionSource.Constant(value))
      case SourceOperand.Floating(value)                => Some(InstructionSource.Floating(value))
      case SourceOperand.Special(SpecialSource.src_scc) =>
        Some(InstructionSource.Condition(ScalarBranchCondition.SccNonZero))
      case SourceOperand.Special(SpecialSource.src_vccz) =>
        Some(InstructionSource.Condition(ScalarBranchCondition.VccZero))
      case SourceOperand.Special(SpecialSource.src_execz) =>
        Some(InstructionSource.Condition(ScalarBranchCondition.ExecZero))
      case SourceOperand.Special(SpecialSource.src_shared_base) =>
        Some(InstructionSource.Constant(VMAddressSpace.LocalBase))
      case SourceOperand.Special(SpecialSource.src_shared_limit) =>
        Some(InstructionSource.Constant(VMAddressSpace.LocalLimit))
      case SourceOperand.Special(SpecialSource.src_private_base) =>
        Some(InstructionSource.Constant(VMAddressSpace.PrivateBase))
      case SourceOperand.Special(SpecialSource.src_private_limit) =>
        Some(InstructionSource.Constant(VMAddressSpace.PrivateLimit))
      case SourceOperand.Special(SpecialSource.src_pops_exiting_wave_id) =>
        Some(InstructionSource.Constant(0L))
      case SourceOperand.Literal(bytes) if bytes == 4 => literal.map(InstructionSource.Literal(_))
      case _                                          => None
    }.toRight(unsupportedSource(encoding))

  private def subDwordSelection(encoding: Int): Either[NonEmpty[String], SubDwordSelection] =
    SubDwordSelection.fromEncoding(encoding).toRight[NonEmpty[String]](s"unknown SDWA selection $encoding")

  private def subDwordDestinationUnused(encoding: Int): Either[NonEmpty[String], SubDwordDestinationUnused] =
    SubDwordDestinationUnused.fromEncoding(
      encoding
    ).toRight[NonEmpty[String]](s"unknown SDWA destination-unused mode $encoding")
}

/** One compiler per loaded code object; kernels with device calls share decoded linked text. */
private class VMCompiler {
  import VMCompiler.*

  private val linked = java.util.concurrent.ConcurrentHashMap[(Long, Int), Either[NonEmpty[String], DecodedProgram]]()

  def compile(kernel: CodeObjectKernel): Either[Fault, Kernel] = {
    val entryRange = linkedTextRanges(kernel.textAddress, kernel.text, Map(kernel.entry -> kernel.entrySize))
    decodeDeferringExternalBranches(kernel.text, kernel.target, kernel.waveSize, entryRange)
      .flatMap { program =>
        // Decode unrelated functions only when the entry can transfer outside its own range.
        if (selfContained(program)) Right(program)
        else linked.computeIfAbsent(
          (kernel.textAddress, kernel.waveSize.raw),
          _ =>
            decodeProgram(
              kernel.text,
              kernel.target,
              kernel.waveSize,
              linkedTextRanges(kernel.textAddress, kernel.text, kernel.functionSizes)
            )
        )
      }
      .leftMap(detail => Fault.InvalidProgram(detail))
      .flatMap { program =>
        val entryOffset = kernel.entry - kernel.textAddress
        Option.when(entryOffset >= 0L && entryOffset <= Int.MaxValue.toLong)(entryOffset.toInt)
          .flatMap(program.instructionIndices.get)
          .toRight(Fault.InvalidProgram(
            f"AMDGPU kernel ${kernel.name} entry 0x${kernel.entry}%X is not an instruction in .text"
          ))
          .map(entry =>
            Kernel(
              kernel,
              program.operations,
              program.instructionIndices,
              program.instructionOffsets,
              entry,
              skippable(program.operations)
            )
          )
      }
  }
}
