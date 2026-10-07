package net.kurobako.gemstone.isa.amdgpu

import BufferInstructionDefs.*
import DataShareInstructionDefs.*
import DualVectorInstructionDefs.*
import EncodingConstants.*
import FlatInstructionDefs.*
import MatrixInstructionDefs.*
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

private object VMCompiler {
  private def isAbsentScalarOffset(value: Long): Boolean =
    value == Fixed.NullSgpr || value == Fixed.ExecLo || value == Fixed.ExecHi

  /** GCN V_CMPX writes its mask to both EXEC and the VOPC destination; GFX10 onwards writes only EXEC. */
  private def comparisonDestination(
      comparison: VectorComparison,
      destination: ScalarRegisterIndex,
      target: Target
  ): ScalarRegisterIndex = target.isa match {
    case ISA.GFX6 | ISA.GFX7 | ISA.GFX8 | ISA.GFX9 | ISA.CDNA1 | ISA.CDNA2 | ISA.CDNA3 | ISA.CDNA4 => destination
    case _ => if (comparison.updateExec) Fixed.NullSgpr else destination
  }

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
    case _                         => None
  }

  private def scalarBinaryOperation(opcode: Opcode): Option[ScalarBinaryOperation] = opcode match {
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

  private def scalarMinMaxOperation(opcode: Opcode): Option[ScalarMinMaxOperation] = opcode match {
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
    case _                       => None
  }

  private def scalarCompareOperation(opcode: Opcode): Option[ScalarCompareOperation] = opcode match {
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
    case VFrexpExpI32F64                        => Some(VectorConvertOperation.FrexpExponentF64)
    case VFrexpExpI32F32                        => Some(VectorConvertOperation.FrexpExponentF32)
    case VFrexpMantF32                          => Some(VectorConvertOperation.FrexpMantissaF32)
    case VFrexpMantF64                          => Some(VectorConvertOperation.FrexpMantissaF64)
    case VRcpIflagF32 | VRcpF32 | VRcpLegacyF32 => Some(VectorConvertOperation.ReciprocalF32)
    case VRcpF64                                => Some(VectorConvertOperation.ReciprocalF64)
    case VTruncF32                              => Some(VectorConvertOperation.TruncateF32)
    case VFractF32                              => Some(VectorConvertOperation.FractionF32)
    case VFloorF32                              => Some(VectorConvertOperation.FloorF32)
    case VRndneF32                              => Some(VectorConvertOperation.RoundNearestEvenF32)
    case VTruncF64                              => Some(VectorConvertOperation.TruncateF64)
    case VFloorF64                              => Some(VectorConvertOperation.FloorF64)
    case VFractF64                              => Some(VectorConvertOperation.FractionF64)
    case VRndneF64                              => Some(VectorConvertOperation.RoundNearestEvenF64)
    case VRsqF32                                => Some(VectorConvertOperation.ReciprocalSquareRootF32)
    case VRsqF64                                => Some(VectorConvertOperation.ReciprocalSquareRootF64)
    case VSqrtF32                               => Some(VectorConvertOperation.SquareRootF32)
    case VExpF32                                => Some(VectorConvertOperation.ExponentBase2F32)
    case VSinF32                                => Some(VectorConvertOperation.SineF32)
    case VCosF32                                => Some(VectorConvertOperation.CosineF32)
    case VLogF32 | VLogLegacyF32                => Some(VectorConvertOperation.LogarithmBase2F32)
    case VCvtI32F32                             => Some(VectorConvertOperation.I32FromF32)
    case VCvtU32F32                             => Some(VectorConvertOperation.U32FromF32)
    case VCvtI32F64                             => Some(VectorConvertOperation.I32FromF64)
    case VCvtU32F64                             => Some(VectorConvertOperation.U32FromF64)
    case VCvtF32Ubyte0                          => Some(VectorConvertOperation.F32FromUByte0)
    case VCvtF32Ubyte1                          => Some(VectorConvertOperation.F32FromUByte1)
    case VCvtF32Ubyte2                          => Some(VectorConvertOperation.F32FromUByte2)
    case VCvtF32Ubyte3                          => Some(VectorConvertOperation.F32FromUByte3)
    case VCvtF32F64                             => Some(VectorConvertOperation.F32FromF64)
    case VCvtF32F16                             => Some(VectorConvertOperation.F32FromF16)
    case VCvtF16F32                             => Some(VectorConvertOperation.F16FromF32)
    case VCvtI16F16                             => Some(VectorConvertOperation.I16FromF16)
    case VCvtF64I32                             => Some(VectorConvertOperation.F64FromI32)
    case VCvtF64U32                             => Some(VectorConvertOperation.F64FromU32)
    case VCvtF64F32                             => Some(VectorConvertOperation.F64FromF32)
    case VCvtF32I32                             => Some(VectorConvertOperation.F32FromI32)
    case VCvtF32U32                             => Some(VectorConvertOperation.F32FromU32)
    case _                                      => None
  }

  private def vectorBinaryOperation(opcode: Opcode): Option[VectorBinaryOperation] = opcode match {
    case VAddU32 | VAddNcU32       => Some(VectorBinaryOperation.AddU32)
    case VMulLoU32                 => Some(VectorBinaryOperation.MultiplyLowU32)
    case VMulHiU32                 => Some(VectorBinaryOperation.MultiplyHighU32)
    case VMulHiI32                 => Some(VectorBinaryOperation.MultiplyHighI32)
    case VAshrrevI32               => Some(VectorBinaryOperation.ShiftRightArithmeticI32)
    case VAshrrevI16               => Some(VectorBinaryOperation.ShiftRightArithmeticI16)
    case VLshlrevB32               => Some(VectorBinaryOperation.ShiftLeft32)
    case VLshrrevB32               => Some(VectorBinaryOperation.ShiftRight32)
    case VLshlrevB16               => Some(VectorBinaryOperation.ShiftLeft16)
    case VLshrrevB16               => Some(VectorBinaryOperation.ShiftRight16)
    case VAddU16 | VAddNcU16       => Some(VectorBinaryOperation.AddU16)
    case VAndB32                   => Some(VectorBinaryOperation.And32)
    case VOrB32                    => Some(VectorBinaryOperation.Or32)
    case VXorB32                   => Some(VectorBinaryOperation.Xor32)
    case VSubU16 | VSubNcU16       => Some(VectorBinaryOperation.SubtractU16)
    case VSubU32 | VSubNcU32       => Some(VectorBinaryOperation.SubtractU32)
    case VSubrevU32 | VSubrevNcU32 => Some(VectorBinaryOperation.SubtractReverseU32)
    case VSubrevF32                => Some(VectorBinaryOperation.SubtractReverseF32)
    case VMulU32U24                => Some(VectorBinaryOperation.MultiplyU24)
    case VMulHiU32U24              => Some(VectorBinaryOperation.MultiplyHighU24)
    case VMulI32I24                => Some(VectorBinaryOperation.MultiplyI24)
    case VMulHiI32I24              => Some(VectorBinaryOperation.MultiplyHighI24)
    case VMulLoU16                 => Some(VectorBinaryOperation.MultiplyLowU16)
    case VMinU32                   => Some(VectorBinaryOperation.MinimumU32)
    case VMaxU32                   => Some(VectorBinaryOperation.MaximumU32)
    case VMinI32                   => Some(VectorBinaryOperation.MinimumI32)
    case VMinI16                   => Some(VectorBinaryOperation.MinimumI16)
    case VMinU16                   => Some(VectorBinaryOperation.MinimumU16)
    case VMinF32 | VMinNumF32      => Some(VectorBinaryOperation.MinimumF32)
    case VMinF16                   => Some(VectorBinaryOperation.MinimumF16)
    case VMaxI32                   => Some(VectorBinaryOperation.MaximumI32)
    case VMaxI16                   => Some(VectorBinaryOperation.MaximumI16)
    case VMaxU16                   => Some(VectorBinaryOperation.MaximumU16)
    case VMaxF32 | VMaxNumF32      => Some(VectorBinaryOperation.MaximumF32)
    case VMaxF16                   => Some(VectorBinaryOperation.MaximumF16)
    case VLdexpF32                 => Some(VectorBinaryOperation.LoadExponentF32)
    case VAddF32                   => Some(VectorBinaryOperation.AddF32)
    case VMulF32 | VMulLegacyF32   => Some(VectorBinaryOperation.MultiplyF32)
    case VMacF32                   => Some(VectorBinaryOperation.MultiplyAccumulateF32)
    case VAddF16                   => Some(VectorBinaryOperation.AddF16)
    case VMulF16                   => Some(VectorBinaryOperation.MultiplyF16)
    case VFmacF16                  => Some(VectorBinaryOperation.FusedMultiplyAccumulateF16)
    case VSubF32                   => Some(VectorBinaryOperation.SubtractF32)
    case VAddCoU32                 => Some(VectorBinaryOperation.AddCarryU32)
    case VAddCoCiU32 | VAddcCoU32  => Some(VectorBinaryOperation.AddWithCarryU32)
    case VSubCoU32                 => Some(VectorBinaryOperation.SubtractCarryU32)
    case VSubCoCiU32 | VSubbCoU32  => Some(VectorBinaryOperation.SubtractWithBorrowU32)
    case _                         => None
  }

  private def vectorSubDwordOperation(opcode: Opcode): Option[VectorSubDwordOperation] = opcode match {
    case VCndmaskB32                              => Some(VectorSubDwordOperation.ConditionalMask32)
    case VAddU32 | VAddNcU32                      => Some(VectorSubDwordOperation.AddU32)
    case VAddU16 | VAddNcU16                      => Some(VectorSubDwordOperation.AddU16)
    case VAddF16                                  => Some(VectorSubDwordOperation.AddF16)
    case VAddCoU32                                => Some(VectorSubDwordOperation.AddCarryU32)
    case VAddCoCiU32 | VAddcCoU32                 => Some(VectorSubDwordOperation.AddWithCarryU32)
    case VAndB32                                  => Some(VectorSubDwordOperation.And32)
    case VOrB32                                   => Some(VectorSubDwordOperation.Or32)
    case VXorB32                                  => Some(VectorSubDwordOperation.Xor32)
    case VLshlrevB16                              => Some(VectorSubDwordOperation.ShiftLeft16)
    case VLshlrevB32                              => Some(VectorSubDwordOperation.ShiftLeft32)
    case VLshrrevB16                              => Some(VectorSubDwordOperation.ShiftRight16)
    case VLshrrevB32                              => Some(VectorSubDwordOperation.ShiftRight32)
    case VMaxI16                                  => Some(VectorSubDwordOperation.MaximumI16)
    case VMaxF16                                  => Some(VectorSubDwordOperation.MaximumF16)
    case VMinI16                                  => Some(VectorSubDwordOperation.MinimumI16)
    case VMinU16                                  => Some(VectorSubDwordOperation.MinimumU16)
    case VMinF16                                  => Some(VectorSubDwordOperation.MinimumF16)
    case VMulLoU16                                => Some(VectorSubDwordOperation.MultiplyLowU16)
    case VMulF16                                  => Some(VectorSubDwordOperation.MultiplyF16)
    case VMulU32U24                               => Some(VectorSubDwordOperation.MultiplyU24)
    case VMulI32I24                               => Some(VectorSubDwordOperation.MultiplyI24)
    case VSubU16 | VSubNcU16                      => Some(VectorSubDwordOperation.SubtractU16)
    case VSubU32 | VSubNcU32                      => Some(VectorSubDwordOperation.SubtractU32)
    case VSubCoU32                                => Some(VectorSubDwordOperation.SubtractCarryU32)
    case VSubCoCiU32 | VSubbCoU32                 => Some(VectorSubDwordOperation.SubtractWithBorrowU32)
    case VSubrevU32 | VSubrevNcU32 | VSubrevCoU32 =>
      Some(VectorSubDwordOperation.SubtractReverseU32)
    case VSubrevCoCiU32 | VSubbrevCoU32 => Some(VectorSubDwordOperation.SubtractReverseWithBorrowU32)
    case _                              => None
  }

  private def vectorBinary64Operation(opcode: Opcode): Option[VectorBinary64Operation] = opcode match {
    case VLdexpF64            => Some(VectorBinary64Operation.LoadExponent)
    case VAddF64              => Some(VectorBinary64Operation.Add)
    case VMulF64              => Some(VectorBinary64Operation.Multiply)
    case VMaxF64 | VMaxNumF64 => Some(VectorBinary64Operation.Maximum)
    case VMinF64 | VMinNumF64 => Some(VectorBinary64Operation.Minimum)
    case _                    => None
  }

  private def vectorPackedBinaryOperation(opcode: Opcode): Option[VectorPackedBinaryOperation] =
    opcode match {
      case VPkAddU16     => Some(VectorPackedBinaryOperation.AddU16)
      case VPkAddF16     => Some(VectorPackedBinaryOperation.AddF16)
      case VPkMulLoU16   => Some(VectorPackedBinaryOperation.MultiplyLowU16)
      case VPkMulF16     => Some(VectorPackedBinaryOperation.MultiplyF16)
      case VPkMaxF16     => Some(VectorPackedBinaryOperation.MaximumF16)
      case VPkMinF16     => Some(VectorPackedBinaryOperation.MinimumF16)
      case VPkMaxI16     => Some(VectorPackedBinaryOperation.MaximumI16)
      case VPkMaxU16     => Some(VectorPackedBinaryOperation.MaximumU16)
      case VPkSubI16     => Some(VectorPackedBinaryOperation.SubtractI16)
      case VPkLshrrevB16 => Some(VectorPackedBinaryOperation.ShiftRight16)
      case _             => None
    }

  private def vectorTernaryOperation(opcode: Opcode): Option[VectorTernaryOperation] = opcode match {
    case VAdd3U32      => Some(VectorTernaryOperation.Add3U32)
    case VOr3B32       => Some(VectorTernaryOperation.Or3B32)
    case VXor3B32      => Some(VectorTernaryOperation.Xor3B32)
    case VMax3I32      => Some(VectorTernaryOperation.Maximum3I32)
    case VMax3U32      => Some(VectorTernaryOperation.Maximum3U32)
    case VMin3I32      => Some(VectorTernaryOperation.Minimum3I32)
    case VMin3F32      => Some(VectorTernaryOperation.Minimum3F32)
    case VMin3U32      => Some(VectorTernaryOperation.Minimum3U32)
    case VMed3I16      => Some(VectorTernaryOperation.Median3I16)
    case VMed3I32      => Some(VectorTernaryOperation.Median3I32)
    case VMed3U16      => Some(VectorTernaryOperation.Median3U16)
    case VMed3U32      => Some(VectorTernaryOperation.Median3U32)
    case VMax3I16      => Some(VectorTernaryOperation.Maximum3I16)
    case VMax3U16      => Some(VectorTernaryOperation.Maximum3U16)
    case VMin3I16      => Some(VectorTernaryOperation.Minimum3I16)
    case VMin3U16      => Some(VectorTernaryOperation.Minimum3U16)
    case VMadU16       => Some(VectorTernaryOperation.MultiplyAddU16)
    case VFmaF16       => Some(VectorTernaryOperation.FusedMultiplyAddF16)
    case VDivFmasF32   => Some(VectorTernaryOperation.DivisionFusedMultiplyAddF32)
    case VMadLegacyU16 => Some(VectorTernaryOperation.MultiplyAddLegacyU16)
    case VMadU32U24    => Some(VectorTernaryOperation.MultiplyAddU24)
    case VMadI32I24    => Some(VectorTernaryOperation.MultiplyAddI24)
    case VAlignbitB32  => Some(VectorTernaryOperation.AlignBit32)
    case VBfiB32       => Some(VectorTernaryOperation.BitFieldInsert32)
    case VAndOrB32     => Some(VectorTernaryOperation.AndOr32)
    case VBfeI32       => Some(VectorTernaryOperation.BitFieldExtractI32)
    case VBfeU32       => Some(VectorTernaryOperation.BitFieldExtractU32)
    case VXadU32       => Some(VectorTernaryOperation.XorAddU32)
    case VAddLshlU32   => Some(VectorTernaryOperation.AddShiftLeftU32)
    case VLshlAddU32   => Some(VectorTernaryOperation.ShiftLeftAddU32)
    case VLshlOrB32    => Some(VectorTernaryOperation.ShiftLeftOr32)
    case VPermB32      => Some(VectorTernaryOperation.PermuteBytes32)
    case _             => None
  }

  private def dsAtomicOperation(opcode: Opcode): Option[(AtomicOperation, Int in (4, 8))] = opcode match {
    case DsAddU32 | DsAddRtnU32 => Some((AtomicOperation.Add, 4))
    case DsSubU32 | DsSubRtnU32 => Some((AtomicOperation.Subtract, 4))
    case DsIncU32 | DsIncRtnU32 => Some((AtomicOperation.IncrementWrap, 4))
    case DsDecU32 | DsDecRtnU32 => Some((AtomicOperation.DecrementWrap, 4))
    case DsMinI32 | DsMinRtnI32 => Some((AtomicOperation.MinimumSigned, 4))
    case DsMaxI32 | DsMaxRtnI32 => Some((AtomicOperation.MaximumSigned, 4))
    case DsMinU32 | DsMinRtnU32 => Some((AtomicOperation.MinimumUnsigned, 4))
    case DsMaxU32 | DsMaxRtnU32 => Some((AtomicOperation.MaximumUnsigned, 4))
    case DsAndRtnB32            => Some((AtomicOperation.And, 4))
    case DsOrRtnB32             => Some((AtomicOperation.Or, 4))
    case DsXorRtnB32            => Some((AtomicOperation.Xor, 4))
    case DsAddRtnF32            => Some((AtomicOperation.FloatAdd, 4))
    case DsMinRtnF32            => Some((AtomicOperation.FloatMinimum, 4))
    case DsMaxRtnF32            => Some((AtomicOperation.FloatMaximum, 4))
    case DsAddRtnU64            => Some((AtomicOperation.Add, 8))
    case DsSubRtnU64            => Some((AtomicOperation.Subtract, 8))
    case DsIncRtnU64            => Some((AtomicOperation.IncrementWrap, 8))
    case DsDecRtnU64            => Some((AtomicOperation.DecrementWrap, 8))
    case DsMinRtnI64            => Some((AtomicOperation.MinimumSigned, 8))
    case DsMaxRtnI64            => Some((AtomicOperation.MaximumSigned, 8))
    case DsMinRtnU64            => Some((AtomicOperation.MinimumUnsigned, 8))
    case DsMaxRtnU64            => Some((AtomicOperation.MaximumUnsigned, 8))
    case DsAndRtnB64            => Some((AtomicOperation.And, 8))
    case DsOrRtnB64             => Some((AtomicOperation.Or, 8))
    case DsXorRtnB64            => Some((AtomicOperation.Xor, 8))
    case DsAddRtnF64            => Some((AtomicOperation.FloatAdd, 8))
    case DsMinRtnF64            => Some((AtomicOperation.FloatMinimum, 8))
    case DsMaxRtnF64            => Some((AtomicOperation.FloatMaximum, 8))
    case _                      => None
  }

  private enum VectorMemoryAccess {
    case Load(bytes: Int in 1 ~ 16, signed: Boolean, placement: LoadPlacement)
    case Store(bytes: Int in 1 ~ 16, high: Boolean)
    case Atomic(operation: AtomicOperation, bytes: Int in (4, 8))
    case CompareSwap(bytes: Int in (4, 8))
  }

  private def vectorMemoryAccess(opcode: Opcode): Option[VectorMemoryAccess] = {
    import VectorMemoryAccess.*
    opcode match {
      case GlobalLoadUbyte | GlobalLoadU8 | FlatLoadUbyte | FlatLoadU8 | ScratchLoadUbyte | ScratchLoadU8 | BufferLoadUbyte |
          BufferLoadU8 =>
        Some(Load(1, false, LoadPlacement.Full))
      case GlobalLoadSbyte | GlobalLoadI8 | FlatLoadSbyte | FlatLoadI8 | ScratchLoadSbyte | ScratchLoadI8 | BufferLoadSbyte |
          BufferLoadI8 =>
        Some(Load(1, true, LoadPlacement.Full))
      case GlobalLoadUshort | GlobalLoadU16 | FlatLoadUshort | FlatLoadU16 | ScratchLoadUshort | ScratchLoadU16 |
          BufferLoadUshort | BufferLoadU16 =>
        Some(Load(2, false, LoadPlacement.Full))
      case GlobalLoadSshort | GlobalLoadI16 | FlatLoadSshort | FlatLoadI16 | ScratchLoadSshort | ScratchLoadI16 |
          BufferLoadSshort | BufferLoadI16 =>
        Some(Load(2, true, LoadPlacement.Full))
      case GlobalLoadShortD16 | FlatLoadShortD16 | ScratchLoadShortD16 | BufferLoadShortD16 | BufferLoadD16B16 =>
        Some(Load(2, false, LoadPlacement.Low))
      case GlobalLoadShortD16Hi | FlatLoadShortD16Hi | ScratchLoadShortD16Hi | BufferLoadShortD16Hi | BufferLoadD16HiB16 =>
        Some(Load(2, false, LoadPlacement.High))
      case GlobalLoadDword | GlobalLoadB32 | FlatLoadDword | FlatLoadB32 | ScratchLoadDword | ScratchLoadB32 | BufferLoadDword |
          BufferLoadB32 =>
        Some(Load(4, false, LoadPlacement.Full))
      case GlobalLoadDwordx2 | GlobalLoadB64 | FlatLoadDwordx2 | FlatLoadB64 | ScratchLoadDwordx2 | ScratchLoadB64 |
          BufferLoadDwordx2 | BufferLoadB64 =>
        Some(Load(8, false, LoadPlacement.Full))
      case GlobalLoadDwordx3 | GlobalLoadB96 | FlatLoadDwordx3 | FlatLoadB96 | ScratchLoadDwordx3 | ScratchLoadB96 |
          BufferLoadDwordx3 | BufferLoadB96 =>
        Some(Load(12, false, LoadPlacement.Full))
      case GlobalLoadDwordx4 | GlobalLoadB128 | FlatLoadDwordx4 | FlatLoadB128 | ScratchLoadDwordx4 | ScratchLoadB128 |
          BufferLoadDwordx4 | BufferLoadB128 =>
        Some(Load(16, false, LoadPlacement.Full))
      case GlobalStoreByte | GlobalStoreB8 | FlatStoreByte | FlatStoreB8 | ScratchStoreByte | ScratchStoreB8 | BufferStoreByte |
          BufferStoreB8 =>
        Some(Store(1, false))
      case GlobalStoreShort | GlobalStoreB16 | FlatStoreShort | FlatStoreB16 | ScratchStoreShort | ScratchStoreB16 |
          BufferStoreShort | BufferStoreB16 =>
        Some(Store(2, false))
      case GlobalStoreDword | GlobalStoreB32 | FlatStoreDword | FlatStoreB32 | ScratchStoreDword | ScratchStoreB32 |
          BufferStoreDword | BufferStoreB32 =>
        Some(Store(4, false))
      case GlobalStoreDwordx2 | GlobalStoreB64 | FlatStoreDwordx2 | FlatStoreB64 | ScratchStoreDwordx2 |
          ScratchStoreB64 | BufferStoreDwordx2 | BufferStoreB64 => Some(Store(8, false))
      case GlobalStoreDwordx3 | GlobalStoreB96 | FlatStoreDwordx3 | FlatStoreB96 | ScratchStoreDwordx3 |
          ScratchStoreB96 | BufferStoreDwordx3 | BufferStoreB96 => Some(Store(12, false))
      case GlobalStoreDwordx4 | GlobalStoreB128 | FlatStoreDwordx4 | FlatStoreB128 | ScratchStoreDwordx4 |
          ScratchStoreB128 | BufferStoreDwordx4 | BufferStoreB128 => Some(Store(16, false))
      case GlobalStoreByteD16Hi | GlobalStoreD16HiB8 | FlatStoreByteD16Hi | FlatStoreD16HiB8 | ScratchStoreByteD16Hi |
          ScratchStoreD16HiB8 | BufferStoreByteD16Hi | BufferStoreD16HiB8 => Some(Store(1, true))
      case GlobalStoreShortD16Hi | GlobalStoreD16HiB16 | FlatStoreShortD16Hi | FlatStoreD16HiB16 |
          ScratchStoreShortD16Hi | ScratchStoreD16HiB16 | BufferStoreShortD16Hi | BufferStoreD16HiB16 =>
        Some(Store(2, true))
      case GlobalAtomicAdd | GlobalAtomicAddU32 | FlatAtomicAdd | FlatAtomicAddU32 =>
        Some(Atomic(AtomicOperation.Add, 4))
      case GlobalAtomicAddX2 | GlobalAtomicAddU64 | FlatAtomicAddX2 | FlatAtomicAddU64 =>
        Some(Atomic(AtomicOperation.Add, 8))
      case GlobalAtomicSub | GlobalAtomicSubU32 | FlatAtomicSub | FlatAtomicSubU32 =>
        Some(Atomic(AtomicOperation.Subtract, 4))
      case GlobalAtomicSwap | GlobalAtomicSwapB32 | FlatAtomicSwap | FlatAtomicSwapB32 =>
        Some(Atomic(AtomicOperation.Exchange, 4))
      case GlobalAtomicSwapX2 | GlobalAtomicSwapB64 | FlatAtomicSwapX2 | FlatAtomicSwapB64 =>
        Some(Atomic(AtomicOperation.Exchange, 8))
      case GlobalAtomicInc | GlobalAtomicIncU32 | FlatAtomicInc | FlatAtomicIncU32 =>
        Some(Atomic(AtomicOperation.IncrementWrap, 4))
      case GlobalAtomicIncX2 | GlobalAtomicIncU64 | FlatAtomicIncX2 | FlatAtomicIncU64 =>
        Some(Atomic(AtomicOperation.IncrementWrap, 8))
      case GlobalAtomicUmin | GlobalAtomicMinU32 | FlatAtomicUmin | FlatAtomicMinU32 =>
        Some(Atomic(AtomicOperation.MinimumUnsigned, 4))
      case GlobalAtomicUmax | GlobalAtomicMaxU32 | FlatAtomicUmax | FlatAtomicMaxU32 =>
        Some(Atomic(AtomicOperation.MaximumUnsigned, 4))
      case GlobalAtomicSmin | GlobalAtomicMinI32 | FlatAtomicSmin | FlatAtomicMinI32 =>
        Some(Atomic(AtomicOperation.MinimumSigned, 4))
      case GlobalAtomicSmax | GlobalAtomicMaxI32 | FlatAtomicSmax | FlatAtomicMaxI32 =>
        Some(Atomic(AtomicOperation.MaximumSigned, 4))
      case GlobalAtomicAnd | GlobalAtomicAndB32 | FlatAtomicAnd | FlatAtomicAndB32 =>
        Some(Atomic(AtomicOperation.And, 4))
      case GlobalAtomicOr | GlobalAtomicOrB32 | FlatAtomicOr | FlatAtomicOrB32 => Some(Atomic(AtomicOperation.Or, 4))
      case GlobalAtomicXor | GlobalAtomicXorB32 | FlatAtomicXor | FlatAtomicXorB32 =>
        Some(Atomic(AtomicOperation.Xor, 4))
      case GlobalAtomicCmpswap | GlobalAtomicCmpswapB32 | FlatAtomicCmpswap | FlatAtomicCmpswapB32 =>
        Some(CompareSwap(4))
      case GlobalAtomicCmpswapX2 | GlobalAtomicCmpswapB64 | FlatAtomicCmpswapX2 | FlatAtomicCmpswapB64 =>
        Some(CompareSwap(8))
      case _ => None
    }
  }

  private def dsAtomicReturnsValue(opcode: Opcode): Boolean = opcode match {
    case DsAddU32 | DsSubU32 | DsIncU32 | DsDecU32 | DsMinI32 | DsMaxI32 | DsMinU32 | DsMaxU32 => false
    case _                                                                                     => true
  }

  private def vectorComparison(opcode: Opcode): Option[VectorComparison] =
    Opcode.ComparisonOpcodes.get(opcode).map { comparison =>
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
    }

  /** The sources, bit `i` for source `i`, whose ABS and NEG bits the VOP3 IR applies; any other set bit is rejected. */
  private def vop3SourceModifierMask(opcode: Opcode): Int = opcode match {
    case VRcpF32 | VSRcpF32 | VFrexpMantF32 | VFrexpMantF64 | VCvtF64F32 | VLdexpF32 | VLdexpF64 | VTrigPreopF64 |
        VCvtF32F16 | VCvtF16F32 | VCvtI16F16 => 1
    case VCndmaskB32 | VAddF32 | VSubF32 | VSubrevF32 | VMulF32 | VMulLegacyF32 | VAddF64 | VMulF64 | VMaxF32 |
        VMinF32 | VMaxNumF32 | VMinNumF32 | VMaxF64 | VMinF64 | VMaxNumF64 | VMinNumF64 | VMacF32 | VFmacF32 |
        VFmacF64 | VPackB32F16 | VAddF16 | VMulF16 | VMinF16 | VMaxF16 | VFmacF16 => 3
    // VOP3-encoded V_PK_FMA_F16 carries NEG_HI in the ABS bits.
    case VMadF32 | VFmaF32 | VFmaF64 | VFmaF16 | VMin3F32 | VDivFixupF32 | VDivFixupF64 | VDivFmasF32 |
        VDivFmasF64 | VDivScaleF32 | VDivScaleF64 | VPkFmaF16 => 7
    case other => vectorComparison(other).fold(0)(comparisonModifierMask(_))
  }

  /** Integer comparisons take no source modifiers and the class mask is an integer. */
  private def comparisonModifierMask(comparison: VectorComparison): Int = comparison.kind match {
    case VectorComparisonKind.Float16 | VectorComparisonKind.Float32 | VectorComparisonKind.Float64 => 3
    case VectorComparisonKind.ClassFloat32 | VectorComparisonKind.ClassFloat64                      => 1
    case _                                                                                          => 0
  }

  /** The result width of the VOP3 opcodes whose OMOD the VM implements. */
  private def vop3OutputWidth(opcode: Opcode): Option[Int in (16, 32, 64)] = opcode match {
    case VRcpF32 | VFrexpMantF32 | VLdexpF32 | VAddF32 | VSubF32 | VSubrevF32 | VMulF32 | VMulLegacyF32 | VMaxF32 |
        VMinF32 | VMaxNumF32 | VMinNumF32 | VMin3F32 | VMadF32 | VFmaF32 | VMacF32 | VFmacF32 | VDivFixupF32 |
        VDivFmasF32 => Some(32)
    case VFrexpMantF64 | VCvtF64F32 | VLdexpF64 | VAddF64 | VMulF64 | VMaxF64 | VMinF64 | VMaxNumF64 | VMinNumF64 |
        VFmaF64 | VFmacF64 | VDivFixupF64 | VDivFmasF64 | VTrigPreopF64 => Some(64)
    case VFmaF16 | VAddF16 | VMulF16 | VMinF16 | VMaxF16 | VFmacF16 | VCvtF16F32 => Some(16)
    case VCvtF32F16                                                              => Some(32)
    case _                                                                       => None
  }

  /** The GFX11 OP_SEL bits each VOP3-encoded f16 VOP1/VOP2 opcode defines; bit 3 selects the destination half. */
  private def halfOpSelection(opcode: Opcode): Option[Int] = opcode match {
    case VAddF16 | VMulF16 | VMinF16 | VMaxF16 | VFmacF16 => Some(0xb)
    case VCvtF32F16                                       => Some(0x1)
    case VCvtF16F32                                       => Some(0x8)
    case VCvtI16F16                                       => Some(0x9)
    case _                                                => None
  }

  // f16 VOP1/VOP2 opcodes whose result fills only the low half of the destination.
  private lazy val HalfResultOpcodes: Set[Opcode] =
    Set(VAddF16, VMulF16, VMinF16, VMaxF16, VFmacF16, VCvtF16F32, VCvtI16F16)

  /** GFX10 onwards keeps the high half of a 16-bit result's destination; earlier generations zero it. */
  private def preservesHighHalf(target: Target): Boolean =
    target.isa.isRDNA || target.isa == ISA.GFX11_7 || target.isa == ISA.CDNA5

  /** Only GFX11 onwards defines OP_SEL for the VOP3 forms of 16-bit VOP1/VOP2 opcodes. */
  private def halfOpSelectionDefined(target: Target): Boolean = target.isa match {
    case ISA.RDNA3 | ISA.RDNA3_5 | ISA.GFX11_7 | ISA.RDNA4 | ISA.CDNA5 => true
    case _                                                             => false
  }

  private def halfResult(
      opcode: Opcode,
      operation: Instruction,
      destination: VectorRegisterIndex,
      high: Boolean,
      target: Target
  ): Instruction =
    if (HalfResultOpcodes.contains(opcode) && (high || preservesHighHalf(target)))
      Instruction.VHalfResult(operation, destination, high)
    else operation

  // Opcodes whose IR applies the VOP3 clamp bit itself.
  private lazy val ClampingVOP3Opcodes: Set[Opcode] = Set(
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
    VLdexpF32,
    VMaxF32,
    VMinF32,
    VMaxNumF32,
    VMinNumF32,
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
    VAddNcU16,
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
  private lazy val UnmodifiedVOP3POpcodes: Set[Opcode] =
    Set(VAccvgprReadB32, VAccvgprRead, VAccvgprWriteB32, VAccvgprWrite, VDot4U32U8)
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
  private case class DecodedProgram(
      operations: Vec[Instruction],
      instructionIndices: Map[Int, Int],
      instructionOffsets: Vec[Int]
  )
  def validate(code: Vec[Byte], target: Target): Either[NonEmpty[String], Unit] =
    decodeProgram(code, target, TargetDefaults.waveSize(target)).map(_ => ())

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
              base == jump && low == base && lowSource == base && high.raw == base.raw + 1 && highSource == high &&
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
      case _: Instruction.SSwapPc | _: Instruction.BranchRelative => true
      case _                                                      => false
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
  private def decodeProgram(
      code: Vec[Byte],
      target: Target,
      waveSize: Int in (32, 64),
      functionRanges: Vec[(Int, Int)] = Vec.empty,
      deferExternalBranches: Boolean = false
  ): Either[NonEmpty[String], DecodedProgram] = {
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
    failure.toLeft(Vec.from(result.result())).flatMap { decoded =>
      val indices = IntMap.from(decoded.view.zipWithIndex.map { case ((offset, _), index) => offset -> index })
      decoded.map { case (offset, operation) =>
        operation match {
          case Instruction.BranchRelative(condition, displacement) =>
            val targetOffset = offset.toLong + 4L + displacement.toLong * 4L
            val target = Option.when(targetOffset >= 0L && targetOffset <= Int.MaxValue.toLong)(targetOffset.toInt)
              .flatMap(indices.get)
            target match {
              case Some(index)                   => Right(Instruction.Branch(condition, index))
              case None if deferExternalBranches => Right(operation)
              case None                          =>
                Left[NonEmpty[String], Nothing](s"branch at byte $offset targets non-instruction byte $targetOffset")
            }
          case other => Right(other)
        }
      }.sequence.map(operations => DecodedProgram(operations, indices, decoded.map(_._1)))
    }
  }

  extension (machine: InstructionDecoder.Decoded) {
    private def vector(field: EncodingField): Either[NonEmpty[String], VectorRegisterIndex] =
      machine(field).toInt.refine[VectorRegisterIndex]
        .leftMap(_ => s"${machine.definition} $field ${machine(field)} exceeds the vector register bank")

    private def scalar(field: EncodingField): Either[NonEmpty[String], ScalarRegisterIndex] =
      machine(field).toInt.refine[ScalarRegisterIndex]
        .leftMap(_ => s"${machine.definition} $field ${machine(field)} exceeds the scalar register bank")

    private def isSet(field: EncodingField): Boolean = machine(field) != 0
  }

  private def decode(
      bytes: Array[Byte],
      offset: Int,
      target: Target,
      waveSize: Int in (32, 64)
  ): Either[NonEmpty[String], Decoded] =
    InstructionDecoder.dual(bytes, offset, target)
      .map(_.flatMap(dual => decodeVOPD(dual, target).map(Decoded(_, dual.size.raw))))
      .getOrElse(InstructionDecoder.decode(bytes, offset, target).flatMap(machine =>
        decodeMachine(machine, offset, target).map(Decoded(_, machine.size.raw))
      ))
      .flatMap(decoded => VMRegisterBounds.validate(decoded.operation, waveSize).map(_ => decoded))

  private def decodeMachine(
      machine: InstructionDecoder.Decoded,
      offset: Int,
      target: Target
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
    case EncodingFormat.VOP3P_MFMA                     => decodeMFMA(machine, target)
    case format if format.base == EncodingFormat.VOP3P => decodeVOP3P(machine, target)
    case format if format.base == EncodingFormat.VOP3  => decodeVOP3(machine, target)
    case EncodingFormat.DS | EncodingFormat.VDS        => decodeDS(machine)
    case EncodingFormat.MUBUF                          => decodeMUBUF(machine)
    case format if format.base == EncodingFormat.VOP1  => decodeVOP1(machine, target)
    case format if format.base == EncodingFormat.VOPC  => decodeVOPC(machine, target)
    case format if format.base == EncodingFormat.VOP2  => decodeVOP2(machine, target)
    case EncodingFormat.FLAT | EncodingFormat.FLAT_GLBL | EncodingFormat.FLAT_GLOBAL | EncodingFormat.VGLOBAL |
        EncodingFormat.VFLAT | EncodingFormat.FLAT_SCRATCH | EncodingFormat.VSCRATCH =>
      decodeGlobalOrFlat(machine, target)
    case format => Left(s"$format execution is not implemented")
  }

  private def decodeVOPD(dual: InstructionDecoder.Dual, target: Target): Either[NonEmpty[String], Instruction] = {
    def half(
        candidates: Vec[(Opcode, Metadata.Encoding)],
        destination: Long,
        source0: EncodingField,
        source1: EncodingField
    ): Either[NonEmpty[String], DualVector] =
      for {
        opcode <- candidates.headOption.map(_._1).toRight[NonEmpty[String]]("missing dual-issue opcode")
        dest   <- destination.toInt.refine[VectorRegisterIndex]
          .leftMap[NonEmpty[String]](_ => s"dual-issue destination $destination exceeds the vector register bank")
        second <- dual(source1).toInt.refine[VectorRegisterIndex]
          .leftMap[NonEmpty[String]](_ => s"dual-issue $source1 exceeds the vector register bank")
        left      <- vectorSource(dual(source0).toInt, dual.literal, target)
        operation <- opcode match {
          case VDualMovB32                                  => Right(DualVectorOperation.Move)
          case VDualAddF32                                  => Right(DualVectorOperation.AddF32)
          case VDualSubF32                                  => Right(DualVectorOperation.SubtractF32)
          case VDualSubrevF32                               => Right(DualVectorOperation.ReverseSubtractF32)
          case VDualMulF32                                  => Right(DualVectorOperation.MultiplyF32)
          case VDualFmacF32 | VDualFmaakF32 | VDualFmamkF32 => Right(DualVectorOperation.FmaF32)
          case VDualMinF32 | VDualMinNumF32                 => Right(DualVectorOperation.MinimumF32)
          case VDualMaxF32 | VDualMaxNumF32                 => Right(DualVectorOperation.MaximumF32)
          case VDualCndmaskB32                              => Right(DualVectorOperation.ConditionalMask)
          case VDualAddNcU32                                => Right(DualVectorOperation.AddU32)
          case VDualLshlrevB32                              => Right(DualVectorOperation.ShiftLeft32)
          case VDualAndB32                                  => Right(DualVectorOperation.And32)
          case other => Left[NonEmpty[String], DualVectorOperation](s"dual-issue ${other.opcode} is not implemented")
        }
        embedded = InstructionSource.Literal(dual.literal.getOrElse(0L))
        right    = if (opcode == VDualFmamkF32) embedded else InstructionSource.VectorRegister(second)
        addend   = if (opcode == VDualFmaakF32) embedded
        else InstructionSource.VectorRegister(if (opcode == VDualFmamkF32) second else dest)
      } yield DualVector(operation, dest, left, right, addend)
    if (dual.layout.format != EncodingFormat.VOPDXY) Left(s"${dual.layout.format} execution is not implemented")
    else for {
      left  <- half(dual.x, dual(EncodingField.VDSTX), EncodingField.SRCX0, EncodingField.VSRCX1)
      right <- half(dual.y, dual.destinationY, EncodingField.SRCY0, EncodingField.VSRCY1)
    } yield Instruction.VDual(left, right)
  }

  private def decodeSMEM(machine: InstructionDecoder.Decoded, target: Target): Either[NonEmpty[String], Instruction] = {
    val opcode = machine.definition
    // RDNA3+ uses s124 as the null offset register; older RDNA uses s125.
    val nullOffset = if (Set(ISA.RDNA3, ISA.RDNA3_5, ISA.RDNA4, ISA.CDNA5, ISA.GFX11_7)(target.isa)) Fixed.M0
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
    for {
      count <- opcode match {
        case SLoadU8 | SLoadU16 | SLoadDword | SLoadB32 => Right(1: Int in (1, 2, 3, 4, 8, 16))
        case SLoadDwordx2 | SLoadB64                    => Right(2: Int in (1, 2, 3, 4, 8, 16))
        case SLoadB96                                   => Right(3: Int in (1, 2, 3, 4, 8, 16))
        case SLoadDwordx4 | SLoadB128                   => Right(4: Int in (1, 2, 3, 4, 8, 16))
        case SLoadDwordx8 | SLoadB256                   => Right(8: Int in (1, 2, 3, 4, 8, 16))
        case SLoadDwordx16 | SLoadB512                  => Right(16: Int in (1, 2, 3, 4, 8, 16))
        case other => Left[NonEmpty[String], Nothing](s"$other SMEM execution is not implemented")
      }
      scalarOffset <-
        if (!machine.contains(EncodingField.IMM))
          if (machine(EncodingField.SOFFSET) != nullOffset.toLong) machine.scalar(EncodingField.SOFFSET).map(Some(_))
          else Right(None)
        else if (machine.isSet(EncodingField.SOFFSET_EN)) machine.scalar(EncodingField.SOFFSET).map(Some(_))
        else if (!immediate && machine.literal.isEmpty) machine.scalar(EncodingField.OFFSET).map(Some(_))
        else Right(None)
      data <- machine.scalar(EncodingField.SDATA)
      base <- machine.scalar(EncodingField.SBASE)
    } yield Instruction.SLoad(
      data,
      count,
      base,
      offset,
      scalarOffset,
      opcode match {
        case SLoadU8  => 1
        case SLoadU16 => 2
        case _        => 4
      }
    )
  }

  private def decodeSOP1(
      machine: InstructionDecoder.Decoded,
      offset: Int,
      target: Target
  ): Either[NonEmpty[String], Instruction] =
    for {
      src         <- scalarSource(machine(EncodingField.SSRC0).toInt, machine.literal, target)
      destination <- machine.scalar(EncodingField.SDST)
      operation   <- machine.definition match {
        case SBarrierSignal        => Right(Instruction.BarrierSignal(src, false))
        case SBarrierSignalIsfirst => Right(Instruction.BarrierSignal(src, true))
        case SMovB32               => Right(Instruction.SMove(destination, src))
        case SMovB64               => Right(Instruction.SMove64(destination, src))
        case SCmovB32              =>
          Right(Instruction.SCSelect(destination, src, InstructionSource.Scalar(destination), 32))
        case SCmovB64 =>
          Right(Instruction.SCSelect(destination, src, InstructionSource.Scalar(destination), 64))
        case SGetpcB64       => Right(Instruction.SGetPc(destination, offset))
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
        case other =>
          scalarUnaryOperation(other)
            .map(operation => Instruction.SUnary(operation, destination, src))
            .toRight[NonEmpty[String]](s"$other SOP1 execution is not implemented")
      }
    } yield operation

  private def decodeSOP2(machine: InstructionDecoder.Decoded, target: Target): Either[NonEmpty[String], Instruction] = {
    val opcode   = machine.definition
    def constant = machine.literal.map(InstructionSource.Constant(_))
      .toRight[NonEmpty[String]](s"$opcode is missing its literal operand")
    for {
      srcA        <- scalarSource(machine(EncodingField.SSRC0).toInt, machine.literal, target)
      srcB        <- scalarSource(machine(EncodingField.SSRC1).toInt, machine.literal, target)
      destination <- machine.scalar(EncodingField.SDST)
      operation   <- opcode match {
        case SFmacF32    => Right(Instruction.SFmaF32(destination, srcA, srcB, InstructionSource.Scalar(destination)))
        case SFmaakF32   => constant.map(Instruction.SFmaF32(destination, srcA, srcB, _))
        case SFmamkF32   => constant.map(Instruction.SFmaF32(destination, srcA, _, srcB))
        case SCselectB32 => Right(Instruction.SCSelect(destination, srcA, srcB, 32))
        case SCselectB64 => Right(Instruction.SCSelect(destination, srcA, srcB, 64))
        case other       =>
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
            .toRight[NonEmpty[String]](s"$other SOP2 execution is not implemented")
      }
    } yield operation
  }

  /** The MODE bits a HWREG selector names, clipped to MODE's 32 bits, or None when it names another register. */
  private def modeField(encoded: Long, target: Target): Option[(Int in 0 ~ 31, Int in 1 ~ 32)] = for {
    fields <- OperandEncoding.Catalogue.forKind(target.isa, OperandKind.HWREG)
      .map(_.fields.map(field => field.field.name -> field).toMap)
    id <- fields.get(EncodingField.ID)
    // GFX12 renames the register HW_REG_WAVE_MODE.
    name <- id.names.get("HW_REG_MODE").orElse(id.names.get("HW_REG_WAVE_MODE"))
    if id.field.get(encoded) == name.toLong
    offset <- fields.get(EncodingField.OFFSET)
    size   <- fields.get(EncodingField.SIZE)
    first  <- offset.field.get(encoded).toInt.refine[Int in 0 ~ 31].toOption
    count  <- math.min(size.field.get(encoded).toInt + 1, 32 - first.raw).refine[Int in 1 ~ 32].toOption
  } yield (first, count)

  private def decodeSOPK(machine: InstructionDecoder.Decoded, target: Target): Either[NonEmpty[String], Instruction] = {
    val immediate = machine.signed(EncodingField.SIMM16)
    machine.definition match {
      // Memory and LDS operations finish synchronously before the next instruction.
      case SWaitcntVscnt | SWaitcntLgkmcnt => Right(Instruction.Wait)
      case SMovkI32                        =>
        machine.scalar(EncodingField.SDST).map(Instruction.SMove(_, InstructionSource.Constant(immediate)))
      case SMulkI32 =>
        machine.scalar(EncodingField.SDST).map(Instruction.SMultiplyImmediate(_, immediate.toInt))
      case SAddkI32 | SAddkCoI32 =>
        machine.scalar(EncodingField.SDST).map(Instruction.SAddImmediate(_, immediate.toInt))
      case SGetregB32 =>
        val encoded = machine(EncodingField.SIMM16)
        val fields  = OperandEncoding.Catalogue.forKind(target.isa, OperandKind.HWREG)
          .map(_.fields.map(field => field.field.name -> field).toMap)
        val value = for {
          fields      <- fields
          id          <- fields.get(EncodingField.ID)
          sharedBases <- id.names.get("HW_REG_SH_MEM_BASES")
          if id.field.get(encoded) == sharedBases.toLong
          offset <- fields.get(EncodingField.OFFSET)
          size   <- fields.get(EncodingField.SIZE)
        } yield (VMAddressSpace.HardwareBases >>> offset.field.get(encoded).toInt) &
          (-1L >>> (64 - size.field.get(encoded).toInt - 1))
        modeField(encoded, target) match {
          case Some((offset, size)) => machine.scalar(EncodingField.SDST).map(Instruction.SGetMode(_, offset, size))
          case None                 =>
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
      case other => Left[NonEmpty[String], Nothing](s"$other SOPK execution is not implemented")
    }
  }

  private def decodeSOPC(machine: InstructionDecoder.Decoded, target: Target): Either[NonEmpty[String], Instruction] =
    for {
      srcA      <- scalarSource(machine(EncodingField.SSRC0).toInt, machine.literal, target)
      srcB      <- scalarSource(machine(EncodingField.SSRC1).toInt, machine.literal, target)
      operation <- machine.definition match {
        case SSetGprIdxOn =>
          // GPR-index mode is not modelled; registers keep ordinary addressing.
          Right(Instruction.SSetGprIdxOn)
        case other =>
          scalarCompareOperation(other)
            .map(operation => Instruction.SCmp(operation, srcA, srcB))
            .orElse(vectorComparison(other).filter(operation =>
              operation.kind == VectorComparisonKind.Float16 || operation.kind == VectorComparisonKind.Float32
            ).map(operation => Instruction.SFloatCompare(operation, srcA, srcB)))
            .toRight[NonEmpty[String]](s"$other SOPC execution is not implemented")
      }
    } yield operation

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
        Right(Instruction.SSetMode(VMState.Mode.DenormalShift, 4, InstructionSource.Constant(immediate & 0xf)))
      case SSendmsg if immediate == 3 && Set(ISA.RDNA3, ISA.RDNA3_5, ISA.GFX11_7, ISA.RDNA4)(target.isa) =>
        // MSG_DEALLOC_VGPRS releases hardware registers after their last use.
        Right(Instruction.Wait)
      case SNop | SInstPrefetch | SSetInstPrefetchDistance => Right(Instruction.Wait)
      // Clause grouping has no observable effect.
      case SClause => Right(Instruction.Wait)
      // Wave priority only affects physical scheduling.
      case SSetprio     => Right(Instruction.Wait)
      case SBarrierWait => Right(Instruction.BarrierWait(machine.signed(EncodingField.SIMM16).toInt))
      case SBarrier     => Right(Instruction.Barrier)
      case SEndpgm      => Right(Instruction.End)
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
      case SSleep         => Right(Instruction.Wait)
      case other          => Left[NonEmpty[String], Nothing](s"$other SOPP execution is not implemented")
    }
  }

  /** SDWA executes only opcodes the catalogue lists with an SDWA encoding. */
  private def sdwaListed(machine: InstructionDecoder.Decoded): Either[NonEmpty[String], Unit] =
    Either.cond(machine.encoding.format.isSdwa, (), s"${machine.definition} SDWA execution is not implemented")

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
        operation match {
          case _: (Instruction.VMove | Instruction.VNot | Instruction.VBitReverse | Instruction.VBitFind |
                Instruction.VBinary | Instruction.VAddCarry | Instruction.VSubCarry | Instruction.VSubReverseCarry |
                Instruction.VMadF32 | Instruction.VCndMask) => true
          case _ => false
        },
        (),
        s"$opcode DPP execution is not implemented"
      )
      _ <- Either.cond[NonEmpty[String], Unit](
        Vec(EncodingField.SRC0_NEG, EncodingField.SRC0_ABS, EncodingField.SRC1_NEG, EncodingField.SRC1_ABS)
          .forall(!machine.isSet(_)),
        (),
        s"$opcode DPP source modifiers are not implemented"
      )
      control <-
        if (octets) Right(DPPControl.DPP8(Vec(
          EncodingField.LANE_SEL_0,
          EncodingField.LANE_SEL_1,
          EncodingField.LANE_SEL_2,
          EncodingField.LANE_SEL_3,
          EncodingField.LANE_SEL_4,
          EncodingField.LANE_SEL_5,
          EncodingField.LANE_SEL_6,
          EncodingField.LANE_SEL_7
        ).zipWithIndex.foldLeft(0) { case (selectors, (field, lane)) =>
          selectors | (machine(field).toInt << (lane * 3))
        }))
        else {
          val control = machine(EncodingField.DPP_CTRL).toInt
          Either.cond[NonEmpty[String], DPPControl](
            VMArithmetic.dppControlValid(control, target.isa),
            DPPControl.DPP16(
              control,
              machine(EncodingField.ROW_MASK).toInt,
              machine(EncodingField.BANK_MASK).toInt,
              machine.isSet(EncodingField.BOUND_CTRL)
            ),
            f"$opcode DPP control 0x$control%x is not valid for ${target.name}"
          )
        }
      source <- machine.vector(EncodingField.VSRC0)
    } yield Instruction.VDPP(
      operation,
      source,
      control,
      if (octets) machine(EncodingField.SRC0) == DPP8.DPP8_FI_1 else machine.isSet(EncodingField.FI)
    )
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

  private def decodeVOP2(machine: InstructionDecoder.Decoded, target: Target): Either[NonEmpty[String], Instruction] = {
    val opcode  = machine.definition
    val hasDPP  = machine.format.isDpp
    val hasSDWA = machine.format.isSdwa
    val literal = machine.literal
    (for {
      srcA <-
        if (hasSDWA) subDwordSource(machine, EncodingField.VSRC0, EncodingField.S0, target)
        else if (hasDPP) Right(InstructionSource.DPP)
        else operandSource(machine, machine(EncodingField.SRC0).toInt, literal, target)
      // VSRC1 names an SGPR only when SDWA selects scalar source mode.
      srcB <-
        if (hasSDWA) subDwordSource(machine, EncodingField.VSRC1, EncodingField.S1, target)
        else machine.vector(EncodingField.VSRC1).map(InstructionSource.VectorRegister(_))
      dest      <- machine.vector(EncodingField.VDST)
      operation <- opcode match {
        case supported if hasSDWA =>
          for {
            _ <- sdwaListed(machine)
            _ <- Either.cond[NonEmpty[String], Unit](
              Vec(EncodingField.SRC0_NEG, EncodingField.SRC0_ABS, EncodingField.SRC1_NEG, EncodingField.SRC1_ABS)
                .forall(!machine.isSet(_)),
              (),
              s"$supported SDWA source modifiers are not implemented"
            )
            _ <- Either.cond[NonEmpty[String], Unit](
              !machine.isSet(EncodingField.CLAMP) && !machine.isSet(EncodingField.OMOD),
              (),
              s"$supported SDWA destination clamp modifiers are not implemented"
            )
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
    } yield operation).flatMap(operation => if (hasDPP) dataParallel(machine, operation, target) else Right(operation))
      .flatMap(operation =>
        if (hasSDWA) Right(operation)
        else machine.vector(EncodingField.VDST).map(halfResult(opcode, operation, _, false, target))
      )
  }

  private def decodeVOP1(machine: InstructionDecoder.Decoded, target: Target): Either[NonEmpty[String], Instruction] = {
    val opcode  = machine.definition
    val hasDPP  = machine.format.isDpp
    val hasSDWA = machine.format.isSdwa
    (for {
      src <-
        if (hasSDWA) subDwordSource(machine, EncodingField.VSRC0, EncodingField.S0, target)
        else if (hasDPP) Right(InstructionSource.DPP)
        else operandSource(machine, machine(EncodingField.SRC0).toInt, machine.literal, target)
      destination <- machine.vector(EncodingField.VDST)
      operation   <- opcode match {
        case VAccvgprMovB32 => src match {
            case InstructionSource.VectorRegister(index) => Right(Instruction.VAccumulatorMove(destination, index))
            case _ => Left[NonEmpty[String], Instruction](s"$opcode requires an accumulator register")
          }
        case VMovB32             => Right(Instruction.VMove(destination, src))
        case VMovB64 if !hasSDWA => Right(Instruction.VMove64(destination, src))
        case VMovrelsB32         => Right(Instruction.VMoveRelative(destination, src))
        case VMovreldB32         => Right(Instruction.VMoveRelativeDestination(destination, src))
        case VNotB32             => Right(Instruction.VNot(destination, src))
        case VSwapB32            => Right(Instruction.VSwap(destination, src))
        case VBfrevB32           =>
          Right(Instruction.VBitReverse(destination, src))
        case VReadfirstlaneB32 => machine.scalar(EncodingField.VDST).map(Instruction.VReadFirstLane(_, src))
        case other             =>
          vectorBitFindOperation(other)
            .map(operation => Instruction.VBitFind(operation, destination, src))
            .orElse(
              vectorConvertOperation(other)
                .map(operation => Instruction.VConvert(operation, destination, src))
            )
            .toRight[NonEmpty[String]](s"$other VOP1 execution is not implemented")
      }
    } yield operation).flatMap(operation => if (hasDPP) dataParallel(machine, operation, target) else Right(operation))
      .flatMap(operation =>
        if (hasSDWA) Right(operation)
        else machine.vector(EncodingField.VDST).map(halfResult(opcode, operation, _, false, target))
      )
  }

  private def decodeVOPC(machine: InstructionDecoder.Decoded, target: Target): Either[NonEmpty[String], Instruction] = {
    val opcode  = machine.definition
    val hasSDWA = machine.format.isSdwa
    for {
      _ <- Either.cond[NonEmpty[String], Unit](
        !machine.format.isDpp,
        (),
        s"$opcode DPP execution is not implemented"
      )
      srcA <-
        if (hasSDWA) subDwordSource(machine, EncodingField.VSRC0, EncodingField.S0, target)
        else operandSource(machine, machine(EncodingField.SRC0).toInt, machine.literal, target)
      // VSRC1 names an SGPR only when SDWA selects scalar source mode.
      srcB <-
        if (hasSDWA) subDwordSource(machine, EncodingField.VSRC1, EncodingField.S1, target)
        else machine.vector(EncodingField.VSRC1).map(InstructionSource.VectorRegister(_))
      comparison <- vectorComparison(opcode).toRight[NonEmpty[String]](s"$opcode VOPC execution is not implemented")
      operation  <-
        if (hasSDWA) for {
          _           <- sdwaListed(machine)
          destination <-
            if (machine.isSet(EncodingField.SD)) machine.scalar(EncodingField.SDST)
            else Right(Fixed.VccLo: ScalarRegisterIndex)
          srcASelection <- subDwordSelection(machine(EncodingField.SRC0_SEL).toInt)
          srcBSelection <- subDwordSelection(machine(EncodingField.SRC1_SEL).toInt)
          modifiers = SourceModifiers(
            machine(EncodingField.SRC0_ABS).toInt | (machine(EncodingField.SRC1_ABS).toInt << 1),
            machine(EncodingField.SRC0_NEG).toInt | (machine(EncodingField.SRC1_NEG).toInt << 1)
          )
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
    } yield operation
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
    for {
      _ <- Either.cond[NonEmpty[String], Unit](
        !machine.format.isDpp,
        (),
        s"$opcode DPP execution is not implemented"
      )
      srcA <- operand(EncodingField.SRC0)
      srcB <- operand(EncodingField.SRC1)
      srcC <- operand(EncodingField.SRC2)
      _    <- Either.cond[NonEmpty[String], Unit](
        ((modifiers.absolute | modifiers.negate) & ~vop3SourceModifierMask(opcode)) == 0,
        (),
        s"$opcode source modifiers are not implemented"
      )
      opSelection = machine(EncodingField.OP_SEL).toInt
      _ <- Either.cond[NonEmpty[String], Unit](
        halfOpSelection(opcode).forall(defined =>
          opSelection == 0 || (halfOpSelectionDefined(target) && (opSelection & ~defined) == 0)
        ),
        (),
        s"$opcode op_sel is not implemented"
      )
      destination      <- machine.vector(EncodingField.VDST)
      carryDestination <- machine.scalar(EncodingField.SDST)
      half = (source: InstructionSource, bit: Int) =>
        if ((opSelection & (1 << bit)) != 0 && halfOpSelection(opcode).nonEmpty) InstructionSource.HighHalf(source)
        else source
      lower = (clamp: Boolean) =>
        opcode match {
          case VAddF16 | VMulF16 | VMinF16 | VMaxF16 =>
            vectorBinaryOperation(opcode).map(operation =>
              Instruction.VBinary(operation, destination, half(srcA, 0), half(srcB, 1), clamp, modifiers)
            ).toRight[NonEmpty[String]](s"$opcode VOP3 execution is not implemented")
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
          case VCvtF32F16 | VCvtF16F32 | VCvtI16F16 =>
            vectorConvertOperation(opcode).map(operation =>
              Instruction.VConvert(operation, destination, half(srcA, 0), modifiers, clamp)
            ).toRight[NonEmpty[String]](s"$opcode VOP3 execution is not implemented")
          case VLshlAddU64 =>
            Right(Instruction.VShiftLeftAdd64(destination, srcA, srcB, srcC))
          case VLshlrevB64 =>
            Right(Instruction.VShiftLeft64(destination, srcA, srcB))
          case VLshrrevB64 =>
            Right(Instruction.VShiftRight64(destination, srcA, srcB))
          case VAshrrevI64 =>
            Right(Instruction.VShiftRightArithmetic64(destination, srcA, srcB))
          case VSRcpF32 =>
            srcA match {
              case _: InstructionSource.VectorRegister =>
                Left[NonEmpty[String], Instruction](s"$opcode requires a scalar source")
              case _ => destination.raw.refine[ScalarRegisterIndex]
                  .left.map[NonEmpty[String]](_ => s"$opcode scalar destination $destination exceeds the scalar bank")
                  .map(Instruction.SReciprocalF32(_, srcA, modifiers, clamp))
            }
          case VRcpF32 =>
            Right(Instruction.VConvert(VectorConvertOperation.ReciprocalF32, destination, srcA, modifiers, clamp))
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
          case VLdexpF64 | VMulF64 | VMaxF64 | VMaxNumF64 | VMinF64 | VMinNumF64 =>
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
          case VAddU32 | VAddNcU32 | VAddNcU16 | VSubNcU16 | VMulLoU16 | VAshrrevI16 | VAshrrevI32 |
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
            Right(Instruction.VPackedFmaF16(
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
              VAndOrB32 | VBfeI32 | VBfeU32 | VXadU32 | VMadI32I24 | VAddLshlU32 | VLshlAddU32 | VLshlOrB32 | VPermB32 =>
            vectorTernaryOperation(opcode)
              .map(operation => Instruction.VTernary(operation, destination, srcA, srcB, srcC, modifiers, clamp))
              .toRight[NonEmpty[String]](s"$opcode VOP3 execution is not implemented")
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
                  srcBHighForHigh = (machine(EncodingField.OMOD) & 2) != 0
                )
              )
          case VAddF64 =>
            vectorBinary64Operation(opcode)
              .map(operation =>
                Instruction.VBinary64(operation, destination, srcA, srcB, modifiers)
              )
              .toRight[NonEmpty[String]](s"$opcode VOP3 execution is not implemented")
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
          case VAddF32 | VSubF32 | VSubrevF32 | VMulF32 | VMulLegacyF32 | VLdexpF32 | VMaxF32 | VMinF32 | VMinNumF32 | VMaxNumF32 =>
            vectorBinaryOperation(opcode)
              .map(operation =>
                Instruction.VBinary(operation, destination, srcA, srcB, clamp, modifiers)
              )
              .toRight[NonEmpty[String]](s"$opcode VOP3 execution is not implemented")
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
      clamp   = machine.isSet(EncodingField.CLAMP)
      clamped = (clamp: Boolean) =>
        if (!clamp || ClampingVOP3Opcodes.contains(opcode) || vectorComparison(opcode).nonEmpty) lower(clamp)
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
    } yield halfResult(opcode, operation, destination, (opSelection & 8) != 0, target)
  }

  private def decodeMFMA(machine: InstructionDecoder.Decoded, target: Target): Either[NonEmpty[String], Instruction] = {
    val opcode = machine.definition
    for {
      size <- opcode match {
        case VMfmaF324x4x116bF32 | VMfmaF324x4x1f32      => Right[NonEmpty[String], Int in (4, 16, 32)](4)
        case VMfmaF3216x16x4F32_1 | VMfmaF3216x16x4f32_2 => Right[NonEmpty[String], Int in (4, 16, 32)](16)
        case VMfmaF3232x32x2F32_1 | VMfmaF3232x32x2f32_2 => Right[NonEmpty[String], Int in (4, 16, 32)](32)
        case _ => Left[NonEmpty[String], Int in (4, 16, 32)](s"$opcode MFMA execution is not implemented")
      }
      blockBroadcast <- machine(EncodingField.CBSZ).toInt.refine[Int in 0 ~ 4]
      blockId        <- machine(EncodingField.ABID).toInt.refine[Int in 0 ~ 15]
      _              <- Either.cond[NonEmpty[String], Unit](
        blockBroadcast.raw <= (if (size.raw == 4) 4 else 0) && blockId.raw < (1 << blockBroadcast.raw),
        (),
        s"$opcode has invalid block broadcast CBSZ=$blockBroadcast ABID=$blockId"
      )
      a           <- vectorSource(machine(EncodingField.SRC0).toInt, None, target)
      b           <- vectorSource(machine(EncodingField.SRC1).toInt, None, target)
      c           <- vectorSource(machine(EncodingField.SRC2).toInt, None, target)
      accumulator <-
      (machine(EncodingField.ACC).toInt |
        (if (!machine.contains(EncodingField.ACC_CD) || machine.isSet(EncodingField.ACC_CD)) 4 else 0))
        .refine[Int where Between[0, 7]]
      lanes       <- machine(EncodingField.BLGP).toInt.refine[Int where Between[0, 7]]
      destination <- machine.vector(EncodingField.VDST)
    } yield Instruction.VMatrixF32(destination, a, b, c, size, blockBroadcast, blockId, accumulator, lanes)
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
    for {
      _ <- Either.cond[NonEmpty[String], Unit](
        !machine.format.isDpp,
        (),
        s"$opcode DPP execution is not implemented"
      )
      srcA <- operand(EncodingField.SRC0)
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
        case VAccvgprWriteB32 | VAccvgprWrite => Right(Instruction.VAccumulatorWrite(destination, srcA))
        case VWmmaF1616x16x16F16 | VWmmaF3216x16x16F16
            if Set(ISA.RDNA3, ISA.RDNA3_5, ISA.GFX11_7)(target.isa) =>
          (srcA, srcB) match {
            case (InstructionSource.VectorRegister(a), InstructionSource.VectorRegister(b)) if !clamp =>
              Right(Instruction.VWaveMatrixF16(
                destination,
                a,
                b,
                srcC,
                opcode == VWmmaF1616x16x16F16,
                (opSelection & 4) != 0,
                negateLow,
                negateHigh
              ))
            case _ => Left[NonEmpty[String], Instruction](s"$opcode requires VGPR multiplicands without clamp")
          }
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
        case VPkFmaF16 =>
          Right(Instruction.VPackedFmaF16(
            dest = destination,
            srcA = srcA,
            srcB = srcB,
            srcC = srcC,
            lowSelection = opSelection,
            highSelection = opSelectionHigh,
            negateLow = negateLow,
            negateHigh = negateHigh,
            clamp = clamp
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
        case VDot4U32U8 =>
          Right(Instruction.VDot4U32U8(
            dest = destination,
            srcA = srcA,
            srcB = srcB,
            srcC = srcC
          ))
        case packed @ (
              VPkAddU16 | VPkAddF16 | VPkMulLoU16 | VPkMulF16 |
              VPkMaxF16 | VPkMinF16 | VPkMaxI16 | VPkMaxU16 | VPkSubI16 |
              VPkLshrrevB16
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
            srcBHighForHigh = (opSelectionHigh & 2) != 0
          )
        case other => Left[NonEmpty[String], Nothing](s"$other VOP3P execution is not implemented")
      }
    } yield operation
  }

  private def decodeDS(machine: InstructionDecoder.Decoded): Either[NonEmpty[String], Instruction] = {
    val opcode   = machine.definition
    val offsetA  = machine(EncodingField.OFFSET0).toInt
    val offsetB  = machine(EncodingField.OFFSET1).toInt
    val dsOffset = offsetA | (offsetB << 8)
    for {
      _ <- Either.cond[NonEmpty[String], Unit](
        !machine.isSet(EncodingField.GDS),
        (),
        s"$opcode GDS execution is not implemented"
      )
      address <- machine.vector(EncodingField.ADDR)
      dataA   <- machine.vector(EncodingField.DATA0)
      dataB   <- machine.vector(EncodingField.DATA1)
      dest    <- machine.vector(EncodingField.VDST)
      local = MemoryAddress.Local(address, dsOffset)
      operation <- opcode match {
        case DsPermuteB32  => Right(Instruction.DSPermute(dest, address, dataA, dsOffset))
        case DsBpermuteB32 => Right(Instruction.DSBPermute(dest, address, dataA, dsOffset))
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
              DsMaxRtnF32 | DsMaxRtnF64
            ) =>
          dsAtomicOperation(atomic)
            .map((operation, bytes) =>
              Instruction.Atomic(operation, dest, local, dataA, bytes, dsAtomicReturnsValue(atomic))
            )
            .toRight[NonEmpty[String]](s"$atomic DS execution is not implemented")
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
        case other => Left[NonEmpty[String], Nothing](s"$other DS execution is not implemented")
      }
    } yield operation
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
      absentScalar = isAbsentScalarOffset(scalarAddress.raw.toLong) ||
        (Set(ISA.RDNA3, ISA.RDNA3_5, ISA.GFX11_7, ISA.RDNA4)(target.isa) && scalarAddress.raw == 124)
      scalar = Option.when(!absentScalar)(scalarAddress)
      address <-
        if (segment == 1) {
          val vector = Option.when(
            if (machine.contains(EncodingField.SVE)) machine.isSet(EncodingField.SVE) else scalarAddress.raw != 127
          )(vectorAddress)
          Right(MemoryAddress.Scratch(vector, scalar, memoryOffset))
        } else if (segment == 2) Right(MemoryAddress.Global(vectorAddress, scalar, memoryOffset))
        else if (segment == 0 && (scalarAddress.raw == 0 || absentScalar))
          Right(MemoryAddress.Flat(vectorAddress, memoryOffset))
        else
          Left[
            NonEmpty[String],
            Nothing
          ](s"VMEM scalar address s[$scalarAddress:${scalarAddress.raw + 1}] is not implemented")
      // Only global and flat loads honour the coherence of their cache policy.
      ordered = coherent && segment != 1
      bank    = if (machine.isSet(EncodingField.ACC)) RegisterBank.Accumulator else RegisterBank.Vector
      operation <- machine.definition match {
        case GlobalInv   => Right(Instruction.CacheInvalidate)
        case GlobalWb    => Right(Instruction.Wait)
        case GlobalWbinv => Right(Instruction.CacheWritebackInvalidate)
        case other       =>
          vectorMemoryAccess(other).toRight[NonEmpty[String]](s"$other VMEM execution is not implemented").flatMap {
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
            case _ if bank == RegisterBank.Accumulator =>
              Left[
                NonEmpty[String],
                Instruction
              ]("accumulator register bank is not implemented for this VMEM operation")
            case VectorMemoryAccess.Atomic(operation, bytes) =>
              Right(Instruction.Atomic(operation, destination, address, dataRegister, bytes, returnValue))
            case VectorMemoryAccess.CompareSwap(bytes) =>
              Right(Instruction.CompareSwap(destination, address, dataRegister, bytes, returnValue))
          }
      }
    } yield operation
  }

  private def decodeMUBUF(machine: InstructionDecoder.Decoded): Either[NonEmpty[String], Instruction] = {
    val opcode         = machine.definition
    val cacheOperation = opcode match {
      case BufferInv | BufferInvl2 | BufferWbinvl1 | BufferWbinvl1Vol |
          BufferGl0Inv | BufferGl1Inv => Some(Instruction.CacheInvalidate)
      // Wait already flushes staged host writes before execution continues.
      case BufferWbl2 => Some(Instruction.Wait)
      case _          => None
    }
    val unsupportedAddressing =
      Vec(EncodingField.IDXEN, EncodingField.ADDR64, EncodingField.LDS, EncodingField.TFE, EncodingField.ACC)
        .exists(machine.isSet(_))
    cacheOperation.fold(
      for {
        _ <- Either.cond[NonEmpty[String], Unit](
          !unsupportedAddressing,
          (),
          s"$opcode buffer addressing/modifiers are not implemented"
        )
        address <-
          if (machine.isSet(EncodingField.OFFEN)) machine.vector(EncodingField.VADDR).map(Some(_)) else Right(None)
        dataRegister <- machine.vector(EncodingField.VDATA)
        resource     <- machine.scalar(EncodingField.SRSRC)
        // SOFFSET 0x80 is the inline constant zero.
        scalarOffset <-
          if (machine(EncodingField.SOFFSET) == 0x80) Right(None)
          else machine.scalar(EncodingField.SOFFSET).map(Some(_))
        buffer = MemoryAddress.Buffer(address, resource, scalarOffset, machine(EncodingField.OFFSET).toInt)
        operation <- vectorMemoryAccess(opcode) match {
          case Some(VectorMemoryAccess.Load(bytes, signed, placement)) =>
            Right(Instruction.Load(dataRegister, buffer, bytes, signed, placement))
          case Some(VectorMemoryAccess.Store(bytes, high)) =>
            Right(Instruction.Store(dataRegister, buffer, bytes, high))
          case _ => Left[NonEmpty[String], Nothing](s"$opcode MUBUF execution is not implemented")
        }
      } yield operation
    )(Right(_))
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
    val name = machine.definition.opcode.raw
    vectorSource(encoding, literal, target).map {
      case InstructionSource.Floating(value) if name.endsWith("_f16") && !name.contains("_mix") =>
        InstructionSource.Constant(Bits.low16(IEEE754.floatToBinary16(value.toFloat).toLong).toLong)
      case other => other
    }
  }

  private def scalarSource(
      encoding: Int,
      literal: Option[Long],
      target: Target
  ): Either[NonEmpty[String], InstructionSource] =
    source(encoding, literal, target, vector = false)

  private def vectorSource(
      encoding: Int,
      literal: Option[Long],
      target: Target
  ): Either[NonEmpty[String], InstructionSource] =
    source(encoding, literal, target, vector = true)

  private def source(
      encoding: Int,
      literal: Option[Long],
      target: Target,
      vector: Boolean
  ): Either[NonEmpty[String], InstructionSource] =
    SourceOperand.decode(encoding, target.isa).flatMap {
      case SourceOperand.Scalar(index)                   => Some(InstructionSource.Scalar(index))
      case SourceOperand.VectorRegister(index) if vector => Some(InstructionSource.VectorRegister(index))
      case SourceOperand.Integer(value)                  => Some(InstructionSource.Constant(value))
      case SourceOperand.Floating(value)                 => Some(InstructionSource.Floating(value))
      case SourceOperand.Special(SpecialSource.src_scc)  =>
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
      case SourceOperand.Literal(bytes) if bytes.raw == 4 => literal.map(InstructionSource.Literal(_))
      case _                                              => None
    }.toRight[NonEmpty[String]](s"source encoding $encoding is not implemented or its literal is missing")

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
    val entryRange = linkedTextRanges(kernel.textAddress, kernel.text, Map(kernel.entry -> kernel.entrySize.raw))
    decodeProgram(kernel.text, kernel.target, kernel.waveSize, entryRange, deferExternalBranches = true)
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
      .leftMap(detail => Fault.Unsupported(detail))
      .flatMap { program =>
        val entryOffset = kernel.entry - kernel.textAddress
        Option.when(entryOffset >= 0L && entryOffset <= Int.MaxValue.toLong)(entryOffset.toInt)
          .flatMap(program.instructionIndices.get)
          .toRight(Fault.InvalidRequest(
            f"AMDGPU kernel ${kernel.name} entry 0x${kernel.entry}%X is not an instruction in .text"
          ))
          .map(Kernel(kernel, program.operations, program.instructionIndices, program.instructionOffsets, _))
      }
  }
}
