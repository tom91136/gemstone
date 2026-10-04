package net.kurobako.gemstone.isa.amdgpu

import BufferInstructionDefs.*
import DataShareInstructionDefs.*
import DualVectorInstructionDefs.*
import ELF.*
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
import VectorCompareInstructionDefs.*
import cats.syntax.either.*
import cats.syntax.traverse.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*
import scala.collection.immutable.IntMap

private object VMCompiler {
  extension (layout: InstructionLayout) {
    private def scalarRegister(word: Long, field: EncodingField): ScalarRegisterIndex =
      layout.get(word, field).toInt.unsafe[ScalarRegisterIndex]

    private def register(word: Long, field: EncodingField): VectorRegisterIndex =
      layout.get(word, field).toInt.unsafe[VectorRegisterIndex]
  }

  private type VendorInstruction = Opcode

  private def isAbsentScalarOffset(value: Long): Boolean =
    value == Fixed.NullSgpr || value == Fixed.ExecLo || value == Fixed.ExecHi

  private def scalarUnaryOperation(opcode: VendorInstruction): Option[ScalarUnaryOperation] = opcode match {
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

  private def scalarBinaryOperation(opcode: VendorInstruction): Option[ScalarBinaryOperation] = opcode match {
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

  private def scalarMinMaxOperation(opcode: VendorInstruction): Option[ScalarMinMaxOperation] = opcode match {
    case SMinU32 => Some(ScalarMinMaxOperation.MinimumU32)
    case SMaxU32 => Some(ScalarMinMaxOperation.MaximumU32)
    case SMinI32 => Some(ScalarMinMaxOperation.MinimumI32)
    case SMaxI32 => Some(ScalarMinMaxOperation.MaximumI32)
    case _       => None
  }

  private def scalarBinary64Operation(opcode: VendorInstruction): Option[ScalarBinary64Operation] = opcode match {
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

  private def scalarCompareOperation(opcode: VendorInstruction): Option[ScalarCompareOperation] = opcode match {
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

  private def scalarImmediateCompareOperation(opcode: VendorInstruction): Option[(ScalarCompareOperation, Boolean)] =
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

  private def vectorBitFindOperation(opcode: VendorInstruction): Option[VectorBitFindOperation] = opcode match {
    case VFfblB32 | VCtzI32B32 => Some(VectorBitFindOperation.FirstBitLow32)
    case VFfbhI32 | VClsI32    => Some(VectorBitFindOperation.FirstBitHighI32)
    case VFfbhU32 | VClzI32U32 => Some(VectorBitFindOperation.FirstBitHighU32)
    case _                     => None
  }

  private def vectorConvertOperation(opcode: VendorInstruction): Option[VectorConvertOperation] = opcode match {
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

  private def vectorBinaryOperation(opcode: VendorInstruction): Option[VectorBinaryOperation] = opcode match {
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

  private def vectorF32BinaryOperation(opcode: VendorInstruction): Option[VectorF32BinaryOperation] = opcode match {
    case VAddF32                 => Some(VectorF32BinaryOperation.Add)
    case VSubF32                 => Some(VectorF32BinaryOperation.Subtract)
    case VSubrevF32              => Some(VectorF32BinaryOperation.ReverseSubtract)
    case VMulF32 | VMulLegacyF32 => Some(VectorF32BinaryOperation.Multiply)
    case VLdexpF32               => Some(VectorF32BinaryOperation.LoadExponent)
    case VMaxF32 | VMaxNumF32    => Some(VectorF32BinaryOperation.Maximum)
    case VMinF32 | VMinNumF32    => Some(VectorF32BinaryOperation.Minimum)
    case _                       => None
  }

  private def vectorSubDwordOperation(opcode: VendorInstruction): Option[VectorSubDwordOperation] = opcode match {
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

  private def vectorBinary64Operation(opcode: VendorInstruction): Option[VectorBinary64Operation] = opcode match {
    case VLdexpF64            => Some(VectorBinary64Operation.LoadExponent)
    case VAddF64              => Some(VectorBinary64Operation.Add)
    case VMulF64              => Some(VectorBinary64Operation.Multiply)
    case VMaxF64 | VMaxNumF64 => Some(VectorBinary64Operation.Maximum)
    case VMinF64 | VMinNumF64 => Some(VectorBinary64Operation.Minimum)
    case _                    => None
  }

  private def vectorPackedBinaryOperation(opcode: VendorInstruction): Option[VectorPackedBinaryOperation] =
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

  private def vectorTernaryOperation(opcode: VendorInstruction): Option[VectorTernaryOperation] = opcode match {
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
    case VFmaF32       => Some(VectorTernaryOperation.FusedMultiplyAddF32)
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

  private def dsAtomicOperation(opcode: VendorInstruction): Option[DSAtomicOperation] = opcode match {
    case DsAddU32 | DsAddRtnU32 => Some(DSAtomicOperation.AddU32)
    case DsSubU32 | DsSubRtnU32 => Some(DSAtomicOperation.SubtractU32)
    case DsIncU32 | DsIncRtnU32 => Some(DSAtomicOperation.IncrementU32)
    case DsDecU32 | DsDecRtnU32 => Some(DSAtomicOperation.DecrementU32)
    case DsMinI32 | DsMinRtnI32 => Some(DSAtomicOperation.MinimumI32)
    case DsMaxI32 | DsMaxRtnI32 => Some(DSAtomicOperation.MaximumI32)
    case DsMinU32 | DsMinRtnU32 => Some(DSAtomicOperation.MinimumU32)
    case DsMaxU32 | DsMaxRtnU32 => Some(DSAtomicOperation.MaximumU32)
    case DsAddRtnU64            => Some(DSAtomicOperation.AddU64)
    case DsSubRtnU64            => Some(DSAtomicOperation.SubtractU64)
    case DsIncRtnU64            => Some(DSAtomicOperation.IncrementU64)
    case DsDecRtnU64            => Some(DSAtomicOperation.DecrementU64)
    case DsAndRtnB32            => Some(DSAtomicOperation.And32)
    case DsOrRtnB32             => Some(DSAtomicOperation.Or32)
    case DsXorRtnB32            => Some(DSAtomicOperation.Xor32)
    case DsAndRtnB64            => Some(DSAtomicOperation.And64)
    case DsOrRtnB64             => Some(DSAtomicOperation.Or64)
    case DsXorRtnB64            => Some(DSAtomicOperation.Xor64)
    case DsMinRtnI64            => Some(DSAtomicOperation.MinimumI64)
    case DsMaxRtnI64            => Some(DSAtomicOperation.MaximumI64)
    case DsMinRtnU64            => Some(DSAtomicOperation.MinimumU64)
    case DsMaxRtnU64            => Some(DSAtomicOperation.MaximumU64)
    case DsAddRtnF32            => Some(DSAtomicOperation.AddF32)
    case DsMinRtnF32            => Some(DSAtomicOperation.MinimumF32)
    case DsMaxRtnF32            => Some(DSAtomicOperation.MaximumF32)
    case DsAddRtnF64            => Some(DSAtomicOperation.AddF64)
    case DsMinRtnF64            => Some(DSAtomicOperation.MinimumF64)
    case DsMaxRtnF64            => Some(DSAtomicOperation.MaximumF64)
    case _                      => None
  }

  private def dsAtomicByteWidth(operation: DSAtomicOperation): Int in (4, 8) = operation match {
    case DSAtomicOperation.AddU64 | DSAtomicOperation.SubtractU64 | DSAtomicOperation.IncrementU64 |
        DSAtomicOperation.DecrementU64 | DSAtomicOperation.And64 | DSAtomicOperation.Or64 | DSAtomicOperation.Xor64 |
        DSAtomicOperation.MinimumI64 | DSAtomicOperation.MaximumI64 | DSAtomicOperation.MinimumU64 |
        DSAtomicOperation.MaximumU64 | DSAtomicOperation.AddF64 | DSAtomicOperation.MinimumF64 |
        DSAtomicOperation.MaximumF64 => 8
    case _ => 4
  }

  private def dsAtomicReturnsValue(opcode: VendorInstruction): Boolean = opcode match {
    case DsAddU32 | DsSubU32 | DsIncU32 | DsDecU32 | DsMinI32 | DsMaxI32 | DsMinU32 | DsMaxU32 => false
    case _                                                                                     => true
  }

  private def vectorComparison(opcode: VendorInstruction): Option[VectorComparison] =
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

  // Decoder membership tables are immutable and shared. Keeping them out of
  // instruction-level methods avoids rebuilding a HashSet for every decoded
  // instruction in a code object.
  private lazy val SupportedVCompareSDWAOpcodes: Set[VendorInstruction] = Set(
    VCmpEqU32,
    VCmpEqF32,
    VCmpEqF16,
    VCmpNeqF16,
    VCmpLgF16,
    VCmpLtF16,
    VCmpLeF16,
    VCmpGtF16,
    VCmpGeF16,
    VCmpNltF16,
    VCmpNleF16,
    VCmpNgtF16,
    VCmpNgeF16,
    VCmpOF16,
    VCmpUF16,
    VCmpNeU32,
    VCmpGtF32,
    VCmpGeF32,
    VCmpNgeF32,
    VCmpLeF32,
    VCmpNgtF32,
    VCmpNltF32,
    VCmpGtI32,
    VCmpGtU32,
    VCmpGeI32,
    VCmpGeI16,
    VCmpGeU32,
    VCmpLtU16,
    VCmpLeI32,
    VCmpLtI32,
    VCmpLtU32,
    VCmpLeU32,
    VCmpEqI16,
    VCmpEqU16,
    VCmpNeI16,
    VCmpNeU16,
    VCmpGtI16,
    VCmpGtU16,
    VCmpGeU16,
    VCmpLtI16,
    VCmpLeI16,
    VCmpLeU16
  )
  private lazy val SupportedVOP3SourceModifierOpcodes: Set[VendorInstruction] = Set(
    VCndmaskB32,
    VLdexpF32,
    VLdexpF64,
    VRcpF32,
    VSRcpF32,
    VFrexpMantF32,
    VFrexpMantF64,
    VCvtF64F32,
    VMaxF64,
    VMaxNumF64,
    VMinF64,
    VMinNumF64,
    VPkAddU16,
    VPkMulLoU16,
    VPkSubI16,
    VPkLshrrevB16,
    VAddF32,
    VSubF32,
    VSubrevF32,
    VMulF32,
    VMulLegacyF32,
    VAddF64,
    VMulF64,
    VMadF32,
    VFmaF32,
    VFmacF32,
    VMacF32,
    VFmaF16,
    VPkFmaF16,
    VFmaMixF32,
    VFmaMixloF16,
    VFmaMixhiF16,
    VPkAddF16,
    VPkMulF16,
    VDivScaleF32,
    VDivFixupF32,
    VPkMaxF16,
    VPkMinF16,
    VPackB32F16,
    VFmaF64,
    VFmacF64,
    VDivScaleF64,
    VDivFmasF64,
    VDivFixupF64,
    VMaxF32,
    VMinF32,
    VMaxNumF32,
    VMinNumF32,
    VTrigPreopF64
  )
  private lazy val SDWAVOP2Opcodes: Set[VendorInstruction] = Set(
    VCndmaskB32,
    VAddCoCiU32,
    VSubCoCiU32,
    VSubrevCoCiU32,
    VAddU32,
    VAddNcU32,
    VAddCoU32,
    VAddcCoU32,
    VSubNcU32,
    VSubU32,
    VAddU16,
    VAndB32,
    VLshlrevB32,
    VLshlrevB16,
    VLshrrevB32,
    VLshrrevB16,
    VMaxI16,
    VMaxF16,
    VMinI16,
    VMinF16,
    VMinU16,
    VMulLoU16,
    VAddF16,
    VMulF16,
    VMulU32U24,
    VOrB32,
    VXorB32,
    VSubU16,
    VSubCoU32,
    VSubbCoU32,
    VSubrevCoU32,
    VSubbrevCoU32,
    VMulI32I24
  )

  private case class Decoded(operation: Instruction, size: Int)
  private case class DecodedProgram(
      operations: Vec[Instruction],
      instructionIndices: Map[Int, Int],
      instructionOffsets: Vec[Int]
  )
  def validate(code: Vec[Byte], target: Target): Either[NonEmpty[String], Unit] =
    decodeProgram(code, target).map(_ => ())

  /** Decode one instruction for decoder-focused tests without exposing the executable model publicly. */
  def decodeInstruction(
      code: Vec[Byte],
      target: Target
  ): Either[NonEmpty[String], Instruction] =
    Either.cond[NonEmpty[String], Unit](code.size >= 4, (), "truncated instruction at byte 0")
      .flatMap(_ => decode(Vec.toArray(code), 0, target).map(_.operation))

  private def selfContained(program: DecodedProgram): Boolean = {
    def constant(source: InstructionSource): Option[Long] = source match {
      case InstructionSource.Constant(value) => Some(value)
      case InstructionSource.Literal(value)  => Some(value)
      case _                                 => None
    }
    def relativeJump(index: Int, source: InstructionSource): Option[Int] =
      if (index < 4) None
      else (program.operations.slice(index - 4, index), source) match {
        // A common long-branch sequence computes PC + a constant without changing its address space.
        // Prove its destination; do not rewrite the instructions or assume unknown indirect targets.
        // Restrict the proof to ordinary SGPRs, excluding the VCC/EXEC register aliases.
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
      val normalized = Vec.newBuilder[(Int, Int)]
      var index      = 0
      while (index < ranges.size) {
        val (start, size) = ranges(index)
        val nextStart     = if (index + 1 < ranges.size) ranges(index + 1)._1 else length
        var end           = math.min(start + size, nextStart)
        // Inferred ELF function sizes commonly include the alignment gap up
        // to the next symbol. Drop complete all-zero words from the tail so
        // a gap cannot be mistaken for an instruction.
        while (
          end - start >= 4 && bytes(end - 4) == 0 && bytes(end - 3) == 0 && bytes(end - 2) == 0 && bytes(end - 1) == 0
        )
          end -= 4
        if (end > start) normalized += start -> (end - start)
        index += 1
      }
      normalized.result()
    }
  }

  // Decode variable-width instructions while retaining the first malformed range.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def decodeProgram(
      code: Vec[Byte],
      target: Target,
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
          // A function's inferred size can include a short alignment tail.
          // Accept it only when every remaining byte is zero; arbitrary
          // truncated/non-instruction bytes must still fail validation.
          val padding = (offset until rangeEnd).forall(bytes(_) == 0)
          if (padding) offset = rangeEnd
          else failure = Some(s"truncated instruction at byte $offset")
        } else
          decode(bytes, offset, target) match {
            case Right(decoded) if offset + decoded.size <= rangeEnd =>
              result += offset -> decoded.operation
              offset += decoded.size
            case Right(_) =>
              failure = Some(s"instruction at byte $offset exceeds function range")
            case Left(_) if (offset until rangeEnd).forall(bytes(_) == 0) =>
              // Linkers commonly place an all-zero alignment gap after a
              // symbol whose size was inferred from the next symbol. Do not
              // interpret it as code, but never skip non-zero bytes.
              offset = rangeEnd
            case Left(error) => failure = Some(s"byte $offset: $error")
          }
      rangeIndex += 1
    }
    failure.toLeft(Vec.from(result.result())).flatMap { decoded =>
      // Byte offsets are dense integer keys; IntMap avoids the boxed tuple and
      // generic hash-map builder previously allocated for every decoded code
      // object while retaining the same immutable lookup behaviour.
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

  private lazy val layouts = InstructionLayout.Catalogue.byISA.view.mapValues(values =>
    values.filter(layout =>
      Set(
        EncodingFormat.SMEM,
        EncodingFormat.SOP1,
        EncodingFormat.SOPC,
        EncodingFormat.SOPP,
        EncodingFormat.SOPK,
        EncodingFormat.SOP2,
        EncodingFormat.VOP3P,
        EncodingFormat.VOPDXY,
        EncodingFormat.VOP3,
        EncodingFormat.DS,
        EncodingFormat.VDS,
        EncodingFormat.FLAT,
        EncodingFormat.FLAT_GLBL,
        EncodingFormat.VGLOBAL,
        EncodingFormat.VFLAT,
        EncodingFormat.FLAT_SCRATCH,
        EncodingFormat.VSCRATCH,
        EncodingFormat.MUBUF,
        EncodingFormat.VOP1,
        EncodingFormat.VOPC,
        EncodingFormat.VOP2
      )(layout.format) || (layout.format == EncodingFormat.FLAT_GLOBAL && layout.bits == 64)
    )
      .sortBy(layout => -layout.fixedMask.bitCount)
  ).toMap

  private def decode(bytes: Array[Byte], offset: Int, target: Target): Either[NonEmpty[String], Decoded] =
    try decodeInstructionBytes(bytes, offset, target).flatMap(decoded =>
        VMRegisterBounds.validate(decoded.operation, VMIR.waveSize(target)).map(_ => decoded)
      )
    catch { case error: IllegalArgumentException => Left(s"invalid register encoding: ${error.getMessage}") }

  private def decodeInstructionBytes(
      bytes: Array[Byte],
      offset: Int,
      target: Target
  ): Either[NonEmpty[String], Decoded] = {
    val word = unsignedWord(bytes, offset)
    layouts.getOrElse(target.isa, Vec.empty[InstructionLayout]).find(_.matches(word))
      .toRight[NonEmpty[String]](s"unknown instruction encoding for ${target.name} at byte $offset")
      .flatMap { layout =>
        layout.format match {
          case EncodingFormat.SMEM  => decodeSMEM(bytes, offset, word, target, layout)
          case EncodingFormat.SOP1  => decodeSOP1(bytes, offset, word, target, layout)
          case EncodingFormat.SOPC  => decodeSOPC(bytes, offset, word, target, layout)
          case EncodingFormat.SOPP  => decodeSOPP(word, target, layout)
          case EncodingFormat.SOPK  => decodeSOPK(bytes, offset, word, target, layout)
          case EncodingFormat.SOP2  => decodeSOP2(bytes, offset, word, target, layout)
          case EncodingFormat.VOP3P =>
            InstructionLayout.Catalogue.forFormat(target, EncodingFormat.VOP3P_MFMA)
              .filter(candidate =>
                OpcodeIndex.definitions(candidate.format, candidate.get(word, EncodingField.OP).toInt, target).nonEmpty
              )
              .fold(decodeVOP3P(bytes, offset, word, target, layout))(candidate =>
                decodeMFMA(bytes, offset, word, target, candidate)
              )
          case EncodingFormat.VOPDXY                  => decodeVOPD(bytes, offset, target, layout)
          case EncodingFormat.VOP3                    => decodeVOP3(bytes, offset, word, target, layout)
          case EncodingFormat.DS | EncodingFormat.VDS => decodeDS(bytes, offset, word, target, layout)
          case EncodingFormat.MUBUF                   => decodeMUBUF(bytes, offset, word, target, layout)
          case EncodingFormat.VOP1                    => decodeVOP1(bytes, offset, word, target, layout)
          case EncodingFormat.VOPC                    => decodeVOPC(bytes, offset, word, target, layout)
          case EncodingFormat.VOP2                    => decodeVOP2(bytes, offset, word, target, layout)
          case EncodingFormat.FLAT | EncodingFormat.FLAT_GLBL | EncodingFormat.FLAT_GLOBAL | EncodingFormat.VGLOBAL | EncodingFormat.VFLAT | EncodingFormat.FLAT_SCRATCH | EncodingFormat.VSCRATCH =>
            decodeGlobalOrFlat(bytes, offset, word, target, layout)
          case format => Left(s"$format execution is not implemented")
        }
      }
  }

  private def decodeVOPD(
      bytes: Array[Byte],
      offset: Int,
      target: Target,
      layout: InstructionLayout
  ): Either[NonEmpty[String], Decoded] = {
    def opcode(word: Long, field: EncodingField, destination: EncodingField): Either[NonEmpty[String], Opcode] = {
      val encoded = layout.get(word, field).toInt
      val choices = Vec(EncodingFormat.VOPDXY, EncodingFormat.VOPDXY_INST_LITERAL).flatMap(format =>
        OpcodeIndex.encodings(format, encoded, target)
      ).filter(_._2.operands.exists(_.field.contains(destination))).map(_._1).distinct
      choices match {
        case Vec(value) => Right(value)
        case _          => Left(s"unknown or ambiguous dual-issue $field opcode $encoded for ${target.name}")
      }
    }
    def half(
        opcode: Opcode,
        destination: VectorRegisterIndex,
        source0: Int,
        source1: VectorRegisterIndex,
        literal: Option[Long]
    ): Either[NonEmpty[String], DualVector] =
      for {
        left      <- vectorSource(source0, literal, target)
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
        embedded = InstructionSource.Literal(literal.getOrElse(0L))
        right    = if (opcode == VDualFmamkF32) embedded else InstructionSource.VectorRegister(source1)
        addend   = if (opcode == VDualFmaakF32) embedded
        else InstructionSource.VectorRegister(if (opcode == VDualFmamkF32) source1 else destination)
      } yield DualVector(operation, destination, left, right, addend)
    for {
      _ <- requireBytes(bytes, offset, 8)
      word = LittleEndian.decode(bytes, offset, 8)
      x <- opcode(word, EncodingField.OPX, EncodingField.VDSTX)
      y <- opcode(word, EncodingField.OPY, EncodingField.VDSTY)
      srcX       = layout.get(word, EncodingField.SRCX0).toInt
      srcY       = layout.get(word, EncodingField.SRCY0).toInt
      hasLiteral = srcX == Source.LITERAL_CONST || srcY == Source.LITERAL_CONST ||
        Vec(x, y).exists(op => op == VDualFmaakF32 || op == VDualFmamkF32)
      size = if (hasLiteral) 12 else 8
      _ <- requireBytes(bytes, offset, size)
      literal = Option.when(hasLiteral)(unsignedWord(bytes, offset + 8))
      destX   = layout.register(word, EncodingField.VDSTX)
      destY   = (layout.get(word, EncodingField.VDSTY).toInt | ((destX.raw & 1) ^ 1)).unsafe[VectorRegisterIndex]
      left  <- half(x, destX, srcX, layout.register(word, EncodingField.VSRCX1), literal)
      right <- half(y, destY, srcY, layout.register(word, EncodingField.VSRCY1), literal)
    } yield Decoded(Instruction.VDual(left, right), size)
  }

  private def decodeSMEM(
      bytes: Array[Byte],
      offset: Int,
      word: Long,
      target: Target,
      layout: InstructionLayout
  ): Either[NonEmpty[String], Decoded] =
    for {
      _      <- requireBytes(bytes, offset, 8)
      opcode <- OpcodeIndex.resolve(EncodingFormat.SMEM, layout.get(word, EncodingField.OP).toInt, target)
      count  <- opcode match {
        case SLoadU8 | SLoadU16 | SLoadDword | SLoadB32 => Right(1: Int in (1, 2, 3, 4, 8, 16))
        case SLoadDwordx2 | SLoadB64                    => Right(2: Int in (1, 2, 3, 4, 8, 16))
        case SLoadB96                                   => Right(3: Int in (1, 2, 3, 4, 8, 16))
        case SLoadDwordx4 | SLoadB128                   => Right(4: Int in (1, 2, 3, 4, 8, 16))
        case SLoadDwordx8 | SLoadB256                   => Right(8: Int in (1, 2, 3, 4, 8, 16))
        case SLoadDwordx16 | SLoadB512                  => Right(16: Int in (1, 2, 3, 4, 8, 16))
        case other => Left[NonEmpty[String], Nothing](s"$other SMEM execution is not implemented")
      }
      second  = unsignedWord(bytes, offset + 4)
      encoded = word | (second << 32)
      // RDNA3+ uses s124 as the null offset register; older RDNA uses s125.
      nullOffset = if (Set(ISA.RDNA3, ISA.RDNA3_5, ISA.RDNA4, ISA.CDNA5, ISA.GFX11_7)(target.isa))
        Fixed.M0
      else Fixed.NullSgpr
      immediateField = layout.byName.get(EncodingField.IMM)
      immediate      = immediateField.forall(_.get(encoded) != 0L)
      scalarOffset   = if (immediateField.isEmpty)
        Option.when(layout.get(encoded, EncodingField.SOFFSET) != nullOffset.toLong)(
          layout.scalarRegister(encoded, EncodingField.SOFFSET)
        )
      else if (layout.byName.get(EncodingField.SOFFSET_EN).exists(_.get(encoded) != 0L))
        Some(layout.scalarRegister(encoded, EncodingField.SOFFSET))
      else Option.when(!immediate)(layout.scalarRegister(encoded, EncodingField.OFFSET))
    } yield Decoded(
      Instruction.SLoad(
        layout.scalarRegister(word, EncodingField.SDATA),
        count,
        layout.scalarRegister(word, EncodingField.SBASE),
        if (immediate) layout.get(encoded, EncodingField.OFFSET).toInt
        else 0,
        scalarOffset,
        opcode match {
          case SLoadU8  => 1
          case SLoadU16 => 2
          case _        => 4
        }
      ),
      8
    )

  private def decodeSOP1(
      bytes: Array[Byte],
      offset: Int,
      word: Long,
      target: Target,
      layout: InstructionLayout
  ): Either[NonEmpty[String], Decoded] =
    for {
      opcode <- OpcodeIndex.resolve(EncodingFormat.SOP1, layout.get(word, EncodingField.OP).toInt, target)
      literal =
        Option.when(layout.get(word, EncodingField.SSRC0) == Source.LITERAL_CONST.toLong)(())
      _   <- requireBytes(bytes, offset, if (literal.nonEmpty) 8 else 4)
      src <- scalarSource(
        layout.get(word, EncodingField.SSRC0).toInt,
        literal.map(_ => unsignedWord(bytes, offset + 4)),
        target
      )
      destination = layout.scalarRegister(word, EncodingField.SDST)
      operation <- opcode match {
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
          Right(Instruction.SAndSaveExec(destination, src, 32))
        case SAndSaveexecB64 =>
          Right(Instruction.SAndSaveExec(destination, src, 64))
        case SAndn2SaveexecB32 | SAndNot1SaveexecB32 =>
          Right(Instruction.SAndn2SaveExec(destination, src, 32))
        case SAndn2SaveexecB64 | SAndNot1SaveexecB64 =>
          Right(Instruction.SAndn2SaveExec(destination, src, 64))
        case SOrSaveexecB32 =>
          Right(Instruction.SOrSaveExec(destination, src, 32))
        case SOrSaveexecB64 =>
          Right(Instruction.SOrSaveExec(destination, src, 64))
        case SXorSaveexecB32 =>
          Right(Instruction.SXorSaveExec(destination, src, 32))
        case SXorSaveexecB64 =>
          Right(Instruction.SXorSaveExec(destination, src, 64))
        case other =>
          scalarUnaryOperation(other)
            .map(operation => Instruction.SUnary(operation, destination, src))
            .toRight[NonEmpty[String]](s"$other SOP1 execution is not implemented")
      }
    } yield Decoded(operation, if (literal.nonEmpty) 8 else 4)

  private def decodeSOP2(
      bytes: Array[Byte],
      offset: Int,
      word: Long,
      target: Target,
      layout: InstructionLayout
  ): Either[NonEmpty[String], Decoded] = {
    val srcAEncoding = layout.get(word, EncodingField.SSRC0).toInt
    val srcBEncoding = layout.get(word, EncodingField.SSRC1).toInt
    val hasLiteral   = srcAEncoding == Source.LITERAL_CONST || srcBEncoding == Source.LITERAL_CONST
    for {
      opcode <- OpcodeIndex.resolve(EncodingFormat.SOP2, layout.get(word, EncodingField.OP).toInt, target)
      instructionLiteral = opcode == SFmaakF32 || opcode == SFmamkF32
      size               = if (hasLiteral || instructionLiteral) 8 else 4
      _ <- requireBytes(bytes, offset, size)
      literal = Option.when(hasLiteral || instructionLiteral)(unsignedWord(bytes, offset + 4))
      srcA <- scalarSource(srcAEncoding, literal, target)
      srcB <- scalarSource(srcBEncoding, literal, target)
      destination = layout.scalarRegister(word, EncodingField.SDST)
      operation <- opcode match {
        case SFmacF32    => Right(Instruction.SFmaF32(destination, srcA, srcB, InstructionSource.Scalar(destination)))
        case SFmaakF32   => Right(Instruction.SFmaF32(destination, srcA, srcB, InstructionSource.Constant(literal.get)))
        case SFmamkF32   => Right(Instruction.SFmaF32(destination, srcA, InstructionSource.Constant(literal.get), srcB))
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
    } yield Decoded(operation, size)
  }

  private def decodeSOPK(
      bytes: Array[Byte],
      offset: Int,
      word: Long,
      target: Target,
      layout: InstructionLayout
  ): Either[NonEmpty[String], Decoded] =
    OpcodeIndex.resolve(EncodingFormat.SOPK, layout.get(word, EncodingField.OP).toInt, target).flatMap {
      // Memory and LDS operations finish synchronously before the next instruction.
      case SWaitcntVscnt | SWaitcntLgkmcnt => Right(Decoded(Instruction.Wait, 4))
      case SMovkI32                        =>
        val immediate = layout.signed(word, EncodingField.SIMM16)
        Right(Decoded(
          Instruction.SMoveImmediate(layout.scalarRegister(word, EncodingField.SDST), immediate),
          4
        ))
      case SMulkI32 =>
        val immediate = layout.signed(word, EncodingField.SIMM16)
        Right(Decoded(
          Instruction.SMultiplyImmediate(layout.scalarRegister(word, EncodingField.SDST), immediate),
          4
        ))
      case SAddkI32 | SAddkCoI32 =>
        val immediate = layout.signed(word, EncodingField.SIMM16)
        Right(Decoded(
          Instruction.SAddImmediate(layout.scalarRegister(word, EncodingField.SDST), immediate),
          4
        ))
      case SGetregB32 =>
        val encoded = layout.get(word, EncodingField.SIMM16)
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
        value.map(value =>
          Decoded(
            Instruction.SMoveImmediate(layout.scalarRegister(word, EncodingField.SDST), value.toInt),
            4
          )
        ).toRight(s"unsupported hardware register selector $encoded")
      case opcode @ (SSetregB32 | SSetregImm32B32) =>
        val size = if (opcode == SSetregImm32B32) 8 else 4
        requireBytes(bytes, offset, size).map { _ =>
          Decoded(
            Instruction.SSetReg(
              layout.get(word, EncodingField.SIMM16).toInt,
              layout.scalarRegister(word, EncodingField.SDST)
            ),
            size
          )
        }
      case compare @ (SCmpkEqI32 | SCmpkLgI32 | SCmpkGtI32 | SCmpkGeI32 | SCmpkLtI32 | SCmpkLeI32 | SCmpkEqU32 | SCmpkLgU32 | SCmpkGtU32 | SCmpkGeU32 | SCmpkLtU32 | SCmpkLeU32) =>
        scalarImmediateCompareOperation(compare)
          .map { case (operation, signed) =>
            val immediate =
              if (signed) layout.signed(word, EncodingField.SIMM16).toLong else layout.get(word, EncodingField.SIMM16)
            Decoded(
              Instruction.SCmp(
                operation,
                InstructionSource.Scalar(layout.scalarRegister(word, EncodingField.SDST)),
                InstructionSource.Constant(immediate)
              ),
              4
            )
          }
          .toRight[NonEmpty[String]](s"$compare SOPK execution is not implemented")
      case other => Left[NonEmpty[String], Nothing](s"$other SOPK execution is not implemented")
    }

  private def decodeSOPC(
      bytes: Array[Byte],
      offset: Int,
      word: Long,
      target: Target,
      layout: InstructionLayout
  ): Either[NonEmpty[String], Decoded] = {
    val srcAEncoding = layout.get(word, EncodingField.SSRC0).toInt
    val srcBEncoding = layout.get(word, EncodingField.SSRC1).toInt
    val hasLiteral   = srcAEncoding == Source.LITERAL_CONST || srcBEncoding == Source.LITERAL_CONST
    for {
      _      <- requireBytes(bytes, offset, if (hasLiteral) 8 else 4)
      opcode <- OpcodeIndex.resolve(EncodingFormat.SOPC, layout.get(word, EncodingField.OP).toInt, target)
      literal = Option.when(hasLiteral)(unsignedWord(bytes, offset + 4))
      srcA      <- scalarSource(srcAEncoding, literal, target)
      srcB      <- scalarSource(srcBEncoding, literal, target)
      operation <- opcode match {
        case SSetGprIdxOn =>
          // The interpreter has no hardware GPR-index mode. Keep this
          // compiler-generated mode transition explicit, while preserving
          // ordinary register addressing for the virtual execution model.
          Right(Instruction.SSetGprIdxOn)
        case other =>
          scalarCompareOperation(other)
            .map(operation => Instruction.SCmp(operation, srcA, srcB))
            .orElse(vectorComparison(other).filter(operation =>
              operation.kind == VectorComparisonKind.Float16 || operation.kind == VectorComparisonKind.Float32
            ).map(operation => Instruction.SFloatCompare(operation, srcA, srcB)))
            .toRight[NonEmpty[String]](s"$other SOPC execution is not implemented")
      }
    } yield Decoded(operation, if (hasLiteral) 8 else 4)
  }

  private def decodeSOPP(word: Long, target: Target, layout: InstructionLayout): Either[NonEmpty[String], Decoded] =
    OpcodeIndex.resolve(EncodingFormat.SOPP, layout.get(word, EncodingField.OP).toInt, target).flatMap { opcode =>
      val immediate = layout.get(word, EncodingField.SIMM16).toInt
      opcode match {
        // Register operations and scalar loads complete synchronously in the VM.
        // Reuse Wait to preserve the existing staged-write flush at dependency waits.
        case SWaitcnt | SWaitcntVscnt | SWaitcntDepctr | SDelayAlu | SWaitKmcnt | SWaitAlu |
            SWaitLoadcnt | SWaitStorecnt | SWaitDscnt | SWaitLoadcntDscnt | SWaitStorecntDscnt |
            SWaitSamplecnt | SWaitBvhcnt | SWaitExpcnt =>
          Right(Decoded(Instruction.Wait, 4))
        case SSendmsg if immediate == 3 && Set(ISA.RDNA3, ISA.RDNA3_5, ISA.GFX11_7, ISA.RDNA4)(target.isa) =>
          // MSG_DEALLOC_VGPRS releases hardware registers after their last use.
          Right(Decoded(Instruction.Wait, 4))
        case SNop | SInstPrefetch | SSetInstPrefetchDistance => Right(Decoded(Instruction.Wait, 4))
        // `s_clause` only constrains the hardware workgroups's clause grouping;
        // it has no observable effect in the deterministic virtual machine.
        case SClause => Right(Decoded(Instruction.Wait, 4))
        // Wave priority only affects physical scheduling; virtual execution is
        // deterministic and therefore treats the hint as a no-op.
        case SSetprio     => Right(Decoded(Instruction.Wait, 4))
        case SBarrierWait => Right(Decoded(Instruction.BarrierWait(immediate.toShort.toInt), 4))
        case SBarrier     => Right(Decoded(Instruction.Barrier, 4))
        case SEndpgm      => Right(Decoded(Instruction.End, 4))
        // Linked runtime failure handlers may be decoded without being called.
        // Preserve the trap as an executable operation rather than rejecting the module.
        case STrap => Right(Decoded(Instruction.Trap(immediate), 4))
        // LLVM emits s_code_end as instruction-cache padding at the end of a
        // linked GFX10+ .text section.  It is not a program terminator (the
        // kernel's s_endpgm performs that job); treating the padding marker as
        // a no-op lets us index the whole linked text without executing it.
        case SCodeEnd       => Right(Decoded(Instruction.Wait, 4))
        case SBranch        => branch(immediate, ScalarBranchCondition.Always)
        case SCbranchScc0   => branch(immediate, ScalarBranchCondition.SccZero)
        case SCbranchScc1   => branch(immediate, ScalarBranchCondition.SccNonZero)
        case SCbranchVccz   => branch(immediate, ScalarBranchCondition.VccZero)
        case SCbranchVccnz  => branch(immediate, ScalarBranchCondition.VccNonZero)
        case SCbranchExecz  => branch(immediate, ScalarBranchCondition.ExecZero)
        case SCbranchExecnz => branch(immediate, ScalarBranchCondition.ExecNonZero)
        case SSleep         => Right(Decoded(Instruction.Wait, 4))
        case other          => Left[NonEmpty[String], Nothing](s"$other SOPP execution is not implemented")
      }
    }

  private def branch(immediate: Int, condition: ScalarBranchCondition): Either[NonEmpty[String], Decoded] = {
    val displacement = Bits.signedIntField(immediate.toLong, 0, 16)
    Right(Decoded(Instruction.BranchRelative(condition, displacement), 4))
  }

  private def decodeVOP2(
      bytes: Array[Byte],
      offset: Int,
      word: Long,
      target: Target,
      layout: InstructionLayout
  ): Either[NonEmpty[String], Decoded] = {
    val srcAEncoding = layout.get(word, EncodingField.SRC0).toInt
    val opcodeResult = OpcodeIndex.resolve(EncodingFormat.VOP2, layout.get(word, EncodingField.OP).toInt, target)
    val hasLiteral   = srcAEncoding == Source.LITERAL_CONST || opcodeResult.exists(opcode =>
      opcode == VMadmkF32 || opcode == VMadakF32 || opcode == VFmamkF32 || opcode == VFmaakF32
    )
    val hasSDWA = srcAEncoding == Fixed.SdwaSource
    for {
      _      <- requireBytes(bytes, offset, if (hasLiteral || hasSDWA) 8 else 4)
      opcode <- opcodeResult.leftMap[NonEmpty[String]](error => s"$error (word=0x${word.toHexString})")
      extension = Option.when(hasSDWA)(unsignedWord(bytes, offset + 4))
      srcA <- extension match {
        case Some(sdwa) =>
          val encoding = Bits.low8Int(sdwa)
          Right(
            if (Bits.isSet(sdwa, 23)) InstructionSource.Scalar(encoding.raw.unsafe[ScalarRegisterIndex])
            else InstructionSource.VectorRegister(encoding)
          )
        case None => vectorSource(
            srcAEncoding,
            Option.when(hasLiteral)(unsignedWord(bytes, offset + 4)),
            target
          )
      }
      // VOP2's SRC1 field is a VGPR index in the ordinary encoding.  It is
      // only allowed to name an SGPR when the SDWA extension explicitly
      // selects scalar source mode.
      srcB = extension match {
        case Some(sdwa) if Bits.isSet(sdwa, 31) =>
          InstructionSource.Scalar(layout.scalarRegister(word, EncodingField.VSRC1))
        case _ => InstructionSource.VectorRegister(layout.register(word, EncodingField.VSRC1))
      }
      literal = Option.when(hasLiteral)(unsignedWord(bytes, offset + 4))
      dest    = layout.register(word, EncodingField.VDST)
      operation <- opcode match {
        case supported if hasSDWA && SDWAVOP2Opcodes.contains(supported) =>
          for {
            sdwa <- extension.toRight[NonEmpty[String]]("missing SDWA extension word")
            _    <- Either.cond[NonEmpty[String], Unit](
              Bits.unsignedField(sdwa, 20, 2) == 0L &&
                Bits.unsignedField(sdwa, 28, 2) == 0L,
              (),
              s"$supported SDWA source modifiers are not implemented"
            )
            _ <- Either.cond[NonEmpty[String], Unit](
              Bits.unsignedField(sdwa, 13, 3) == 0L,
              (),
              s"$supported SDWA destination clamp modifiers are not implemented"
            )
            destSelection   <- subDwordSelection(Bits.unsignedIntField(sdwa, 8, 3).raw)
            destUnused      <- subDwordDestinationUnused(Bits.unsignedIntField(sdwa, 11, 2).raw)
            srcASelection   <- subDwordSelection(Bits.unsignedIntField(sdwa, 16, 3).raw)
            srcBSelection   <- subDwordSelection(Bits.unsignedIntField(sdwa, 24, 3).raw)
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
            srcASignExtend = Bits.isSet(sdwa, 19),
            srcBSignExtend = Bits.isSet(sdwa, 27)
          )
        case unsupported if hasSDWA =>
          Left[NonEmpty[String], Nothing](s"$unsupported SDWA execution is not implemented")
        case VFmacF32 =>
          Right(Instruction.VMadF32(
            operation = VectorMadF32Operation.FusedMultiplyAdd,
            dest = dest,
            srcA = srcA,
            srcB = srcB,
            srcC = InstructionSource.VectorRegister(dest),
            absA = false,
            absB = false,
            absC = false,
            negateA = false,
            negateB = false,
            negateC = false,
            clamp = false
          ))
        case VLshlrevB64 =>
          Right(Instruction.VShiftLeft64(dest, srcA, srcB))
        case VFmacF64 =>
          Right(Instruction.VMadF64(
            dest = dest,
            srcA = srcA,
            srcB = srcB,
            srcC = InstructionSource.VectorRegister(dest),
            absA = false,
            absB = false,
            absC = false,
            negateA = false,
            negateB = false,
            negateC = false
          ))
        case VAddF64 | VMulF64 | VMaxF64 | VMaxNumF64 | VMinF64 | VMinNumF64 =>
          vectorBinary64Operation(opcode).map(operation =>
            Instruction.VBinary64Modified(
              operation,
              dest,
              srcA,
              srcB,
              false,
              false,
              false,
              false
            )
          ).toRight[NonEmpty[String]](s"$opcode VOP2 execution is not implemented")
        case VMadmkF32 | VFmamkF32 =>
          literal.map(value => Instruction.VMadMkF32(dest = dest, srcA = srcA, multiplier = value, addend = srcB))
            .toRight[NonEmpty[String]](s"$opcode is missing its literal operand")
        case VMadakF32 | VFmaakF32 =>
          literal.map(value => Instruction.VMadAkF32(dest = dest, srcA = srcA, srcB = srcB, addend = value))
            .toRight[NonEmpty[String]](s"$opcode is missing its literal operand")
        case VDot2F32F16 =>
          Right(Instruction.VDot2F32F16(dest = dest, srcA = srcA, srcB = srcB, accumulate = false))
        case VDot2cF32F16 =>
          Right(Instruction.VDot2F32F16(dest = dest, srcA = srcA, srcB = srcB, accumulate = true))
        case VMovB32     => Right(Instruction.VMove(dest, srcA))
        case VNotB32     => Right(Instruction.VNot(dest, srcA))
        case VCndmaskB32 =>
          Right(Instruction.VCndMask(
            dest = dest,
            srcA = srcA,
            srcB = srcB,
            condition = InstructionSource.Scalar(Fixed.VccLo),
            absA = false,
            absB = false,
            negateA = false,
            negateB = false
          ))
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
    } yield Decoded(operation, if (hasLiteral || hasSDWA) 8 else 4)
  }

  private def decodeVOP1(
      bytes: Array[Byte],
      offset: Int,
      word: Long,
      target: Target,
      layout: InstructionLayout
  ): Either[NonEmpty[String], Decoded] = {
    val sourceEncoding = layout.get(word, EncodingField.SRC0).toInt
    val hasLiteral     = sourceEncoding == Source.LITERAL_CONST
    val hasDPP         = sourceEncoding == Fixed.DppSource
    val hasSDWA        = sourceEncoding == Fixed.SdwaSource
    for {
      _      <- requireBytes(bytes, offset, if (hasLiteral || hasDPP || hasSDWA) 8 else 4)
      opcode <- OpcodeIndex.resolve(EncodingFormat.VOP1, layout.get(word, EncodingField.OP).toInt, target)
      extension = Option.when(hasDPP || hasSDWA)(unsignedWord(bytes, offset + 4))
      src <- extension match {
        case Some(sdwa) if hasSDWA =>
          val encoding = Bits.low8Int(sdwa)
          Right(if (Bits.isSet(sdwa, 23)) InstructionSource.Scalar(encoding.raw.unsafe[ScalarRegisterIndex])
          else InstructionSource.VectorRegister(encoding))
        case Some(dpp) => Right(InstructionSource.VectorRegister(Bits.low8Int(dpp)))
        case None      => vectorSource(
            sourceEncoding,
            Option.when(hasLiteral)(unsignedWord(bytes, offset + 4)),
            target
          )
      }
      destination = layout.register(word, EncodingField.VDST)
      operation <- opcode match {
        case VMovB32 if hasDPP =>
          val dpp     = extension.get
          val control = Bits.unsignedIntField(dpp, 8, 9).raw
          for {
            _ <- Either.cond[NonEmpty[String], Unit](
              control <= 0xff || (control >= 0x100 && control <= 0x13f) || control == 0x142 || control == 0x143,
              (),
              s"$VMovB32 DPP control $control is not implemented"
            )
            _ <- Either.cond[NonEmpty[String], Unit](
              Bits.unsignedField(dpp, 20, 4) == 0L,
              (),
              s"$VMovB32 DPP source modifiers are not implemented"
            )
          } yield Instruction.VMoveDPP(
            dest = destination,
            src = Bits.low8Int(dpp),
            control = control,
            rowMask = Bits.unsignedIntField(dpp, 28, 4).raw,
            bankMask = Bits.unsignedIntField(dpp, 24, 4).raw,
            boundControl = Bits.isSet(dpp, 19),
            fetchInactive = Bits.isSet(dpp, 18)
          )
        case unsupported if hasDPP =>
          Left[NonEmpty[String], Nothing](s"$unsupported DPP execution is not implemented")
        case VMovB32             => Right(Instruction.VMove(destination, src))
        case VMovB64 if !hasSDWA => Right(Instruction.VMove64(destination, src))
        case VMovrelsB32         => Right(Instruction.VMoveRelative(destination, src))
        case VMovreldB32         => Right(Instruction.VMoveRelativeDestination(destination, src))
        case VNotB32             => Right(Instruction.VNot(destination, src))
        case VSwapB32            => Right(Instruction.VSwap(destination, src))
        case VBfrevB32           =>
          Right(Instruction.VBitReverse(destination, src))
        case VReadfirstlaneB32 =>
          Right(Instruction.VReadFirstLane(Bits.unsignedIntField(word, 17, 7).raw.unsafe[ScalarRegisterIndex], src))
        case other =>
          vectorBitFindOperation(other)
            .map(operation => Instruction.VBitFind(operation, destination, src))
            .orElse(
              vectorConvertOperation(other)
                .map(operation => Instruction.VConvert(operation, destination, src))
            )
            .toRight[NonEmpty[String]](s"$other VOP1 execution is not implemented")
      }
    } yield Decoded(operation, if (hasLiteral || hasDPP || hasSDWA) 8 else 4)
  }

  private def decodeVOPC(
      bytes: Array[Byte],
      offset: Int,
      word: Long,
      target: Target,
      layout: InstructionLayout
  ): Either[NonEmpty[String], Decoded] = {
    val srcAEncoding = layout.get(word, EncodingField.SRC0).toInt
    val hasLiteral   = srcAEncoding == Source.LITERAL_CONST
    val hasSDWA      = srcAEncoding == Fixed.SdwaSource
    for {
      _      <- requireBytes(bytes, offset, if (hasLiteral || hasSDWA) 8 else 4)
      opcode <- OpcodeIndex.resolve(EncodingFormat.VOPC, layout.get(word, EncodingField.OP).toInt, target)
      extension = Option.when(hasSDWA)(unsignedWord(bytes, offset + 4))
      srcA <- extension match {
        case Some(sdwa) if Bits.isSet(sdwa, 23) =>
          Right(InstructionSource.Scalar(Bits.low8Int(sdwa).raw.unsafe[ScalarRegisterIndex]))
        case Some(sdwa) => Right(InstructionSource.VectorRegister(Bits.low8Int(sdwa)))
        case None       => vectorSource(
            srcAEncoding,
            Option.when(hasLiteral)(unsignedWord(bytes, offset + 4)),
            target
          )
      }
      // In VOPC, SRC1 is always a VGPR in the normal encoding.  Unlike
      // SRC0, its 8-bit field does not use the scalar/constant source table.
      srcB = extension match {
        case Some(sdwa) if Bits.isSet(sdwa, 31) =>
          InstructionSource.Scalar(layout.scalarRegister(word, EncodingField.VSRC1))
        case _ => InstructionSource.VectorRegister(layout.register(word, EncodingField.VSRC1))
      }
      destination = extension.flatMap(sdwa =>
        Option.when(Bits.isSet(sdwa, 15))(Bits.unsignedIntField(sdwa, 8, 7).raw.unsafe[ScalarRegisterIndex])
      )
      comparison <- vectorComparison(opcode).toRight[NonEmpty[String]](s"$opcode VOPC execution is not implemented")
      operation  <- if (hasSDWA) {
        for {
          sdwa          <- extension.toRight[NonEmpty[String]]("missing SDWA extension word")
          srcASelection <- subDwordSelection(Bits.unsignedIntField(sdwa, 16, 3).raw)
          srcBSelection <- subDwordSelection(Bits.unsignedIntField(sdwa, 24, 3).raw)
          _             <- Either.cond[NonEmpty[String], Unit](
            Bits.unsignedField(sdwa, 20, 2) == 0L,
            (),
            s"$opcode SDWA source modifiers are not implemented"
          )
          _ <- Either.cond[NonEmpty[String], Unit](
            SupportedVCompareSDWAOpcodes.contains(opcode),
            (),
            s"$opcode SDWA execution is not implemented"
          )
        } yield Instruction.VCompareSubDword(
          operation = comparison,
          srcA = srcA,
          srcB = srcB,
          srcASelection = srcASelection,
          srcBSelection = srcBSelection,
          srcASignExtend = Bits.isSet(sdwa, 19),
          srcBSignExtend = Bits.isSet(sdwa, 27),
          destination = destination
        )
      } else Right(Instruction.VCompare(comparison, srcA, srcB))
    } yield Decoded(operation, if (hasLiteral || hasSDWA) 8 else 4)
  }

  private def decodeVOP3(
      bytes: Array[Byte],
      offset: Int,
      word: Long,
      target: Target,
      layout: InstructionLayout
  ): Either[NonEmpty[String], Decoded] =
    for {
      _ <- requireBytes(bytes, offset, 8)
      opcodeId = layout.get(word, EncodingField.OP).toInt
      // GFX9 VOP3-encoded compares reuse the VOPC opcode space, but the
      // generated metadata only lists their compact VOPC form.
      opcode <- if (OpcodeIndex.definitions(EncodingFormat.VOP3, opcodeId, target).isEmpty)
        OpcodeIndex.resolve(EncodingFormat.VOPC, opcodeId, target)
      else OpcodeIndex.resolve(EncodingFormat.VOP3, opcodeId, target)
      second       = unsignedWord(bytes, offset + 4)
      encoded      = word | (second << 32)
      srcAEncoding = layout.get(encoded, EncodingField.SRC0).toInt
      srcBEncoding = layout.get(encoded, EncodingField.SRC1).toInt
      srcCEncoding = layout.get(encoded, EncodingField.SRC2).toInt
      hasLiteral   = srcAEncoding == Source.LITERAL_CONST || srcBEncoding == Source.LITERAL_CONST ||
        srcCEncoding == Source.LITERAL_CONST
      _ <- requireBytes(bytes, offset, if (hasLiteral) 12 else 8)
      literal = Option.when(hasLiteral)(unsignedWord(bytes, offset + 8))
      srcA <- vectorSource(srcAEncoding, literal, target).leftMap[NonEmpty[String]](error =>
        s"$error in VOP3 word 0x${word.toHexString}/0x${second.toHexString}"
      )
      srcB <- vectorSource(srcBEncoding, literal, target).leftMap[NonEmpty[String]](error =>
        s"$error in VOP3 word 0x${word.toHexString}/0x${second.toHexString}"
      )
      srcC <- vectorSource(srcCEncoding, literal, target).leftMap[NonEmpty[String]](error =>
        s"$error in VOP3 word 0x${word.toHexString}/0x${second.toHexString}"
      )
      _ <- Either.cond[NonEmpty[String], Unit](
        (second >>> 27) == 0L || vectorComparison(opcode).nonEmpty ||
          SupportedVOP3SourceModifierOpcodes.contains(opcode) || opcode == VBitop3B16 || opcode == VBitop3B32,
        (),
        s"$opcode source modifiers are not implemented"
      )
      destination      = layout.register(word, EncodingField.VDST)
      carryDestination = Bits.unsignedIntField(word, 8, 7).raw.unsafe[ScalarRegisterIndex]
      absoluteA        = Bits.isSet(word, 8)
      absoluteB        = Bits.isSet(word, 9)
      absoluteC        = Bits.isSet(word, 10)
      negateA          = Bits.isSet(second, 29)
      negateB          = Bits.isSet(second, 30)
      negateC          = Bits.isSet(second, 31)
      clamp            = Bits.isSet(word, 15)
      operation <- opcode match {
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
                .map(Instruction.SReciprocalF32(_, srcA, absoluteA, negateA, clamp))
          }
        case VRcpF32 =>
          Right(Instruction.VUnaryF32Modified(
            dest = destination,
            src = srcA,
            absolute = absoluteA,
            negate = negateA,
            clamp = clamp
          ))
        case VFrexpMantF32 =>
          Right(Instruction.VFrexpMantF32Modified(
            dest = destination,
            src = srcA,
            absolute = absoluteA,
            negate = negateA,
            clamp = clamp
          ))
        case VCvtF64F32 =>
          Right(Instruction.VCvtF64F32Modified(
            dest = destination,
            src = srcA,
            absolute = absoluteA,
            negate = negateA,
            clamp = clamp
          ))
        case VFrexpMantF64 =>
          Right(Instruction.VUnaryF64Modified(
            dest = destination,
            src = srcA,
            absolute = absoluteA,
            negate = negateA
          ))
        case VTrigPreopF64 =>
          Right(Instruction.VTrigPreopF64(
            dest = destination,
            src = srcA,
            segment = srcB,
            absolute = absoluteA,
            negate = negateA
          ))
        case VBitop3B16 | VBitop3B32 =>
          // CDNA4 repurposes the VOP3 modifier fields for the eight-bit truth table.
          val table =
            (layout.get(encoded, EncodingField.NEG) |
              (layout.get(encoded, EncodingField.ABS) << 3) |
              (layout.get(encoded, EncodingField.OMOD) << 6)).toInt
          Right(Instruction.VBitOp3(destination, srcA, srcB, srcC, table, opcode == VBitop3B16))
        case VFmaF64 | VFmacF64 =>
          Right(Instruction.VMadF64(
            dest = destination,
            srcA = srcA,
            srcB = srcB,
            srcC = if (opcode == VFmacF64) InstructionSource.VectorRegister(destination) else srcC,
            absA = absoluteA,
            absB = absoluteB,
            absC = absoluteC,
            negateA = negateA,
            negateB = negateB,
            negateC = negateC
          ))
        case VDivFmasF64 =>
          Right(Instruction.VDivFmasF64(
            dest = Bits.low8Int(word),
            srcA = srcA,
            srcB = srcB,
            srcC = srcC
          ))
        case VDivFixupF64 =>
          Right(Instruction.VDivFixupF64(
            dest = Bits.low8Int(word),
            quotient = srcA,
            denominator = srcB,
            numerator = srcC,
            absolute = layout.get(word, EncodingField.ABS).toInt,
            negate = layout.get(encoded, EncodingField.NEG).toInt
          ))
        case VDivScaleF64 =>
          Right(Instruction.VDivScaleF64(
            dest = destination,
            conditionDest = carryDestination,
            srcA = srcA,
            srcB = srcB,
            srcC = srcC
          ))
        case VLdexpF64 | VMulF64 | VMaxF64 | VMaxNumF64 | VMinF64 | VMinNumF64 =>
          vectorBinary64Operation(opcode)
            .map(operation =>
              Instruction.VBinary64Modified(
                operation = operation,
                dest = destination,
                srcA = srcA,
                srcB = srcB,
                absA = absoluteA,
                absB = absoluteB,
                negateA = negateA,
                negateB = negateB
              )
            )
            .toRight[NonEmpty[String]](s"$opcode VOP3 execution is not implemented")
        case VMbcntLoU32B32 =>
          Right(Instruction.VMaskBitCount(destination, srcA, srcB, high = false))
        case VMbcntHiU32B32 =>
          Right(Instruction.VMaskBitCount(destination, srcA, srcB, high = true))
        case VReadlaneB32 =>
          Right(Instruction.VReadLane(layout.scalarRegister(word, EncodingField.VDST), srcA, srcB))
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
                // VOP3's clamp modifier is encoded in DWORD0[15], not in the
                // source/modifier DWORD1.
                clamp = clamp
              )
            )
            .toRight[NonEmpty[String]](s"$opcode VOP3 execution is not implemented")
        case VCndmaskB32 => srcC match {
            case InstructionSource.Scalar(_) =>
              Right(Instruction.VCndMask(
                dest = destination,
                srcA = srcA,
                srcB = srcB,
                condition = srcC,
                absA = absoluteA,
                absB = absoluteB,
                negateA = negateA,
                negateB = negateB
              ))
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
            operation = if (opcode == VFmaF32) VectorMadF32Operation.FusedMultiplyAdd
            else VectorMadF32Operation.MultiplyAdd,
            dest = destination,
            srcA = srcA,
            srcB = srcB,
            srcC = srcC,
            absA = absoluteA,
            absB = absoluteB,
            absC = absoluteC,
            negateA = negateA,
            negateB = negateB,
            negateC = negateC,
            clamp = clamp
          ))
        case VMacF32 | VFmacF32 =>
          Right(Instruction.VMadF32(
            operation = if (opcode == VFmacF32) VectorMadF32Operation.FusedMultiplyAdd
            else VectorMadF32Operation.MultiplyAdd,
            dest = destination,
            srcA = srcA,
            srcB = srcB,
            srcC = InstructionSource.VectorRegister(destination),
            absA = absoluteA,
            absB = absoluteB,
            absC = false,
            negateA = negateA,
            negateB = negateB,
            negateC = false,
            clamp = clamp
          ))
        case VDivFixupF32 =>
          Right(Instruction.VDivFixupF32(
            dest = Bits.low8Int(word),
            quotient = srcA,
            denominator = srcB,
            numerator = srcC,
            absolute = layout.get(word, EncodingField.ABS).toInt,
            negate = layout.get(encoded, EncodingField.NEG).toInt
          ))
        case VBcntU32B32 =>
          Right(Instruction.VBitCount(dest = Bits.low8Int(word), src = srcA, base = srcB))
        case VPkFmaF16 =>
          Right(Instruction.VPackedFmaF16(
            dest = destination,
            srcA = srcA,
            srcB = srcB,
            srcC = srcC,
            lowSelection = Bits.unsignedIntField(word, 11, 3).raw,
            highSelection = layout.get(encoded, EncodingField.OMOD).toInt |
              (Bits.unsignedIntField(word, 14, 1).raw << 2),
            negateLow = layout.get(encoded, EncodingField.NEG).toInt,
            negateHigh = layout.get(word, EncodingField.ABS).toInt,
            clamp = clamp
          ))
        case VAdd3U32 | VOr3B32 | VXor3B32 | VFmaF16 | VDivFmasF32 | VMadLegacyU16 | VMadU16 | VMadU32U24 | VAlignbitB32 | VBfiB32 |
            VMed3I16 | VMed3I32 | VMed3U16 | VMed3U32 | VMin3F32 |
            VMax3I16 | VMax3U16 | VMin3I16 | VMin3U16 |
            VMax3I32 | VMax3U32 | VMin3I32 | VMin3U32 |
            VAndOrB32 | VBfeI32 | VBfeU32 | VXadU32 | VMadI32I24 | VAddLshlU32 | VLshlAddU32 | VLshlOrB32 | VPermB32 =>
          vectorTernaryOperation(opcode)
            .map(operation => Instruction.VTernary(operation, destination, srcA, srcB, srcC))
            .toRight[NonEmpty[String]](s"$opcode VOP3 execution is not implemented")
        case VPackB32F16 =>
          Right(Instruction.VPackB32F16(destination, srcA, srcB))
        case packed @ (VPkAddU16 | VPkAddF16 | VPkMulLoU16 | VPkMulF16 | VPkMaxF16 | VPkMinF16 | VPkSubI16 | VPkLshrrevB16) =>
          for {
            _ <- Either.cond[NonEmpty[String], Unit](
              (second >>> 29) == 0L,
              (),
              s"$packed packed modifiers are not implemented"
            )
            packedOperation <- vectorPackedBinaryOperation(packed)
              .toRight[NonEmpty[String]](s"$packed VOP3 execution is not implemented")
          } yield Instruction.VPackedBinary(
            operation = packedOperation,
            dest = destination,
            srcA = srcA,
            srcB = srcB,
            srcAHighForHigh = Bits.isSet(second, 27),
            srcBHighForHigh = Bits.isSet(second, 28)
          )
        case VAddF64 =>
          vectorBinary64Operation(opcode)
            .map(operation =>
              Instruction.VBinary64Modified(
                operation = operation,
                dest = destination,
                srcA = srcA,
                srcB = srcB,
                absA = absoluteA,
                absB = absoluteB,
                negateA = negateA,
                negateB = negateB
              )
            )
            .toRight[NonEmpty[String]](s"$opcode VOP3 execution is not implemented")
        case VMadU64U32 | VMadCoU64U32 =>
          Right(Instruction.VMadU64U32(destination, carryDestination, srcA, srcB, srcC))
        case VMadI64I32 | VMadCoI64I32 =>
          Right(Instruction.VMadI64I32(destination, carryDestination, srcA, srcB, srcC))
        case VDivScaleF32 =>
          Right(Instruction.VDivScaleF32(
            dest = destination,
            conditionDest = carryDestination,
            srcA = srcA,
            srcB = srcB,
            srcC = srcC
          ))
        case VAddF32 | VSubF32 | VSubrevF32 | VMulF32 | VMulLegacyF32 | VLdexpF32 | VMaxF32 | VMinF32 | VMinNumF32 | VMaxNumF32 =>
          vectorF32BinaryOperation(opcode)
            .map(operation =>
              Instruction.VBinaryF32Modified(
                operation = operation,
                dest = destination,
                srcA = srcA,
                srcB = srcB,
                absA = absoluteA,
                absB = absoluteB,
                negateA = negateA,
                negateB = negateB,
                clamp = clamp
              )
            )
            .toRight[NonEmpty[String]](s"$opcode VOP3 execution is not implemented")
        case other =>
          vectorComparison(other)
            .map(comparison =>
              Instruction.VCompareTo(
                dest = Bits.unsignedIntField(word, 0, 7).raw.unsafe[ScalarRegisterIndex],
                operation = comparison,
                srcA = srcA,
                srcB = srcB,
                absA = absoluteA,
                absB = absoluteB,
                negateA = negateA,
                negateB = negateB
              )
            )
            .toRight[NonEmpty[String]](s"$other VOP3 execution is not implemented")
      }
    } yield Decoded(operation, if (hasLiteral) 12 else 8)

  private def decodeMFMA(
      bytes: Array[Byte],
      offset: Int,
      word: Long,
      target: Target,
      layout: InstructionLayout
  ): Either[NonEmpty[String], Decoded] = for {
    _ <- requireBytes(bytes, offset, 8)
    encoded = word | (unsignedWord(bytes, offset + 4) << 32)
    opcode <- OpcodeIndex.resolve(layout.format, layout.get(encoded, EncodingField.OP).toInt, target)
    size   <- opcode match {
      case VMfmaF324x4x116bF32 | VMfmaF324x4x1f32      => Right[NonEmpty[String], Int in (4, 16, 32)](4)
      case VMfmaF3216x16x4F32_1 | VMfmaF3216x16x4f32_2 => Right[NonEmpty[String], Int in (4, 16, 32)](16)
      case VMfmaF3232x32x2F32_1 | VMfmaF3232x32x2f32_2 => Right[NonEmpty[String], Int in (4, 16, 32)](32)
      case _ => Left[NonEmpty[String], Int in (4, 16, 32)](s"$opcode MFMA execution is not implemented")
    }
    blockBroadcast <- layout.get(encoded, EncodingField.CBSZ).toInt.refine[Int in 0 ~ 4]
    blockId        <- layout.get(encoded, EncodingField.ABID).toInt.refine[Int in 0 ~ 15]
    _              <- Either.cond[NonEmpty[String], Unit](
      blockBroadcast.raw <= (if (size.raw == 4) 4 else 0) && blockId.raw < (1 << blockBroadcast.raw),
      (),
      s"$opcode has invalid block broadcast CBSZ=$blockBroadcast ABID=$blockId"
    )
    a <- vectorSource(layout.get(encoded, EncodingField.SRC0).toInt, None, target)
    b <- vectorSource(layout.get(encoded, EncodingField.SRC1).toInt, None, target)
    c <- vectorSource(layout.get(encoded, EncodingField.SRC2).toInt, None, target)
    accumulator = layout.get(encoded, EncodingField.ACC).toInt |
      (if (!layout.byName.contains(EncodingField.ACC_CD) || layout.get(encoded, EncodingField.ACC_CD) != 0) 4 else 0)
  } yield Decoded(
    Instruction.VMatrixF32(
      layout.register(encoded, EncodingField.VDST),
      a,
      b,
      c,
      size,
      blockBroadcast,
      blockId,
      accumulator.unsafe[Int where Between[0, 7]],
      layout.get(encoded, EncodingField.BLGP).toInt.unsafe[Int where Between[0, 7]]
    ),
    8
  )

  /** Decode the packed 16-bit VOP3P encoding used by RDNA instructions such as `v_pk_lshrrev_b16`. VOP3P has the same
    * two-dword source layout as VOP3, but its opcode is selected from the packed-vector opcode space and the packed
    * lane selectors live in DWORD1 bits 27--28.
    */
  private def decodeVOP3P(
      bytes: Array[Byte],
      offset: Int,
      word: Long,
      target: Target,
      layout: InstructionLayout
  ): Either[NonEmpty[String], Decoded] = {
    val opcodeId = layout.get(word, EncodingField.OP).toInt
    for {
      _ <- requireBytes(bytes, offset, 8)
      second       = unsignedWord(bytes, offset + 4)
      encoded      = word | (second << 32)
      srcAEncoding = layout.get(encoded, EncodingField.SRC0).toInt
      srcBEncoding = layout.get(encoded, EncodingField.SRC1).toInt
      srcCEncoding = layout.get(encoded, EncodingField.SRC2).toInt
      hasLiteral   = srcAEncoding == Source.LITERAL_CONST || srcBEncoding == Source.LITERAL_CONST ||
        srcCEncoding == Source.LITERAL_CONST
      _      <- requireBytes(bytes, offset, if (hasLiteral) 12 else 8)
      opcode <- OpcodeIndex.resolve(EncodingFormat.VOP3P, opcodeId, target)
        .orElse(OpcodeIndex.resolve(EncodingFormat.VOP3, opcodeId, target))
      literal = Option.when(hasLiteral)(unsignedWord(bytes, offset + 8))
      srcA <- vectorSource(srcAEncoding, literal, target).leftMap[NonEmpty[String]](error =>
        s"$error in VOP3P word 0x${word.toHexString}/0x${second.toHexString}"
      )
      srcB <- vectorSource(srcBEncoding, literal, target).leftMap[NonEmpty[String]](error =>
        s"$error in VOP3P word 0x${word.toHexString}/0x${second.toHexString}"
      )
      srcC <- vectorSource(srcCEncoding, literal, target).leftMap[NonEmpty[String]](error =>
        s"$error in VOP3P word 0x${word.toHexString}/0x${second.toHexString}"
      )
      destination     = layout.register(word, EncodingField.VDST)
      opSelection     = layout.get(word, EncodingField.OP_SEL).toInt
      opSelectionHigh =
        (layout.get(encoded, EncodingField.OP_SEL_HI) |
          (layout.byName.get(EncodingField.OP_SEL_HI_2).fold(0L)(_.get(encoded)) << 2)).toInt
      negateLow  = layout.get(encoded, EncodingField.NEG).toInt
      negateHigh = layout.get(word, EncodingField.NEG_HI).toInt
      clamp      = Bits.isSet(word, 15)
      operation <- opcode match {
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
        case VFmaMixloF16 | VFmaMixhiF16 =>
          Right(Instruction.VFmaMixF16(
            dest = destination,
            srcA = srcA,
            srcB = srcB,
            srcC = srcC,
            opSelection = opSelection,
            opSelectionHi = opSelectionHigh,
            negate = negateLow,
            absolute = negateHigh,
            clamp = clamp,
            high = opcode == VFmaMixhiF16
          ))
        case VFmaMixF32 =>
          Right(Instruction.VFmaMixF32(
            dest = destination,
            srcA = srcA,
            srcB = srcB,
            srcC = srcC,
            opSelection = opSelection,
            opSelectionHi = opSelectionHigh,
            negate = negateLow,
            absolute = negateHigh,
            clamp = clamp
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
            _ <- Either.cond[NonEmpty[String], Unit](
              (second >>> 29) == 0L,
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
            srcAHighForHigh = Bits.isSet(second, 27),
            srcBHighForHigh = Bits.isSet(second, 28)
          )
        case other => Left[NonEmpty[String], Nothing](s"$other VOP3P execution is not implemented")
      }
    } yield Decoded(operation, if (hasLiteral) 12 else 8)
  }

  private def decodeDS(
      bytes: Array[Byte],
      offset: Int,
      word: Long,
      target: Target,
      layout: InstructionLayout
  ): Either[NonEmpty[String], Decoded] =
    for {
      _ <- requireBytes(bytes, offset, 8)
      rawOpcode = layout.get(word, EncodingField.OP).toInt
      opcode <- OpcodeIndex.resolve(layout.format, rawOpcode, target).leftMap[NonEmpty[String]](_ =>
        s"unknown DS opcode $rawOpcode for ${target.name} word=0x${word.toHexString}"
      )
      _ <- Either.cond[NonEmpty[String], Unit](
        layout.byName.get(EncodingField.GDS).forall(_.get(word) == 0),
        (),
        s"$opcode GDS execution is not implemented"
      )
      second   = unsignedWord(bytes, offset + 4)
      encoded  = word | (second << 32)
      address  = layout.register(encoded, EncodingField.ADDR)
      dataA    = layout.register(encoded, EncodingField.DATA0)
      dataB    = layout.register(encoded, EncodingField.DATA1)
      dest     = layout.register(encoded, EncodingField.VDST)
      offsetA  = layout.get(word, EncodingField.OFFSET0).toInt
      offsetB  = layout.get(word, EncodingField.OFFSET1).toInt
      dsOffset = Bits.low16Int(word).raw
      operation <- opcode match {
        case DsPermuteB32  => Right(Instruction.DSPermute(dest, address, dataA, dsOffset))
        case DsBpermuteB32 => Right(Instruction.DSBPermute(dest, address, dataA, dsOffset))
        // DS_SWIZZLE uses the address field as its source VGPR.  Unlike the
        // ordinary DS read/write forms, the data field is reserved and is
        // encoded as zero by clang/LLVM.
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
            .map(operation =>
              Instruction.DSAtomic(
                operation = operation,
                dest = dest,
                address = address,
                src = dataA,
                bytes = dsAtomicByteWidth(operation),
                offset = dsOffset,
                returnValue = dsAtomicReturnsValue(atomic)
              )
            )
            .toRight[NonEmpty[String]](s"$atomic DS execution is not implemented")
        case DsWriteB8 | DsStoreB8   => Right(Instruction.DSWrite(address, dataA, None, 1, dsOffset, None))
        case DsWriteB16 | DsStoreB16 => Right(Instruction.DSWrite(address, dataA, None, 2, dsOffset, None))
        case DsWriteB32 | DsStoreB32 => Right(Instruction.DSWrite(address, dataA, None, 4, dsOffset, None))
        case DsWriteB64 | DsStoreB64 => Right(Instruction.DSWrite(address, dataA, None, 8, dsOffset, None))
        case DsWriteB96 | DsStoreB96 =>
          Right(Instruction.DSWrite(address, dataA, None, 12, dsOffset, None))
        case DsWriteB128 | DsStoreB128 =>
          Right(Instruction.DSWrite(address, dataA, None, 16, dsOffset, None))
        case DsWriteB8D16Hi | DsStoreB8D16Hi   => Right(Instruction.DSWriteD16HighByte(address, dataA, dsOffset))
        case DsWriteB16D16Hi | DsStoreB16D16Hi => Right(Instruction.DSWriteD16High(address, dataA, dsOffset))
        case DsWrite2B32 | DsStore2addrB32     =>
          Right(Instruction.DSWrite(
            address,
            dataA,
            Some(dataB),
            4,
            offsetA * 4,
            Some(offsetB * 4)
          ))
        case DsWrite2B64 | DsStore2addrB64 =>
          Right(Instruction.DSWrite(
            address,
            dataA,
            Some(dataB),
            8,
            offsetA * 8,
            Some(offsetB * 8)
          ))
        case DsWrite2st64B32 | DsStore2addrStride64B32 =>
          Right(Instruction.DSWrite(
            address,
            dataA,
            Some(dataB),
            4,
            offsetA * 64 * 4,
            Some(offsetB * 64 * 4)
          ))
        case DsWrite2st64B64 | DsStore2addrStride64B64 =>
          Right(Instruction.DSWrite(
            address,
            dataA,
            Some(dataB),
            8,
            offsetA * 64 * 8,
            Some(offsetB * 64 * 8)
          ))
        case DsReadU8 | DsLoadU8   => Right(Instruction.DSRead(dest, address, 1, dsOffset, None))
        case DsReadU16 | DsLoadU16 => Right(Instruction.DSRead(dest, address, 2, dsOffset, None))
        case DsReadI8 | DsLoadI8   =>
          Right(Instruction.DSSignedRead(dest, address, 1, dsOffset, None))
        case DsReadI16 | DsLoadI16 =>
          Right(Instruction.DSSignedRead(dest, address, 2, dsOffset, None))
        case DsReadU8D16 | DsLoadU8D16 =>
          Right(Instruction.DSReadD16(dest, address, 1, dsOffset, false))
        case DsReadU8D16Hi | DsLoadU8D16Hi =>
          Right(Instruction.DSReadD16(dest, address, 1, dsOffset, true))
        case DsReadU16D16 | DsLoadU16D16 =>
          Right(Instruction.DSReadD16(dest, address, 2, dsOffset, false))
        case DsReadU16D16Hi | DsLoadU16D16Hi =>
          Right(Instruction.DSReadD16(dest, address, 2, dsOffset, true))
        case DsReadI8D16 | DsLoadI8D16 =>
          Right(Instruction.DSSignedReadD16(dest, address, 1, dsOffset, false))
        case DsReadI8D16Hi | DsLoadI8D16Hi =>
          Right(Instruction.DSSignedReadD16(dest, address, 1, dsOffset, true))
        case DsReadB32 | DsLoadB32       => Right(Instruction.DSRead(dest, address, 4, dsOffset, None))
        case DsReadB96 | DsLoadB96       => Right(Instruction.DSRead(dest, address, 12, dsOffset, None))
        case DsReadB128 | DsLoadB128     => Right(Instruction.DSRead(dest, address, 16, dsOffset, None))
        case DsReadB64 | DsLoadB64       => Right(Instruction.DSRead(dest, address, 8, dsOffset, None))
        case DsRead2B32 | DsLoad2addrB32 =>
          Right(Instruction.DSRead(dest, address, 4, offsetA * 4, Some(offsetB * 4)))
        case DsRead2B64 | DsLoad2addrB64 =>
          Right(Instruction.DSRead(dest, address, 8, offsetA * 8, Some(offsetB * 8)))
        case DsRead2st64B32 | DsLoad2addrStride64B32 =>
          Right(Instruction.DSRead(
            dest,
            address,
            4,
            offsetA * 64 * 4,
            Some(offsetB * 64 * 4)
          ))
        case DsRead2st64B64 | DsLoad2addrStride64B64 =>
          Right(Instruction.DSRead(
            dest,
            address,
            8,
            offsetA * 64 * 8,
            Some(offsetB * 64 * 8)
          ))
        case other => Left[NonEmpty[String], Nothing](s"$other DS execution is not implemented")
      }
    } yield Decoded(operation, 8)

  private def vmemLoad(
      global: Boolean,
      dest: VectorRegisterIndex,
      address: VectorRegisterIndex,
      scalar: Option[ScalarRegisterIndex],
      bytes: Int in 1 ~ 16,
      offset: Int,
      coherent: Boolean
  ): Instruction =
    if (global)
      Instruction.GlobalLoad(
        dest = dest,
        address = address,
        scalarAddress = scalar,
        bytes = bytes,
        offset = offset,
        coherent = coherent
      )
    else Instruction.FlatLoad(dest = dest, address = address, bytes = bytes, offset = offset, coherent = coherent)

  private def vmemLoadSigned(
      global: Boolean,
      dest: VectorRegisterIndex,
      address: VectorRegisterIndex,
      scalar: Option[ScalarRegisterIndex],
      bytes: Int in 1 ~ 2,
      offset: Int
  ): Instruction =
    if (global)
      Instruction.GlobalLoadSigned(
        dest = dest,
        address = address,
        scalarAddress = scalar,
        bytes = bytes,
        offset = offset
      )
    else Instruction.FlatLoadSigned(dest = dest, address = address, bytes = bytes, offset = offset)

  private def vmemLoadD16(
      global: Boolean,
      dest: VectorRegisterIndex,
      address: VectorRegisterIndex,
      scalar: Option[ScalarRegisterIndex],
      offset: Int,
      high: Boolean
  ): Instruction =
    if (global)
      Instruction.GlobalLoadD16(dest = dest, address = address, scalarAddress = scalar, offset = offset, high = high)
    else Instruction.VFlatLoadD16(dest = dest, address = address, offset = offset, high = high)

  private def vmemStore(
      global: Boolean,
      address: VectorRegisterIndex,
      scalar: Option[ScalarRegisterIndex],
      src: VectorRegisterIndex,
      bytes: Int in 1 ~ 16,
      offset: Int
  ): Instruction =
    if (global)
      Instruction.GlobalStore(address = address, scalarAddress = scalar, src = src, bytes = bytes, offset = offset)
    else Instruction.FlatStore(address = address, src = src, bytes = bytes, offset = offset)

  private def vmemStoreD16(
      global: Boolean,
      address: VectorRegisterIndex,
      scalar: Option[ScalarRegisterIndex],
      src: VectorRegisterIndex,
      bytes: Int in 1 ~ 2,
      offset: Int
  ): Instruction =
    if (global)
      Instruction.GlobalStoreD16High(
        address = address,
        scalarAddress = scalar,
        src = src,
        bytes = bytes,
        offset = offset
      )
    else Instruction.FlatStoreD16High(address = address, src = src, bytes = bytes, offset = offset)

  private def vmemCompareSwap(
      global: Boolean,
      dest: VectorRegisterIndex,
      address: VectorRegisterIndex,
      scalar: Option[ScalarRegisterIndex],
      src: VectorRegisterIndex,
      bytes: Int in (4, 8),
      compareOffset: Int,
      offset: Int,
      returnValue: Boolean
  ): Instruction =
    Instruction.GlobalAtomicCompareSwap(
      dest = dest,
      address = address,
      scalarAddress = if (global) scalar else None,
      compareSrc = (src.raw + compareOffset).unsafe[VectorRegisterIndex],
      swapSrc = src,
      bytes = bytes,
      offset = offset,
      returnValue = returnValue
    )

  private def decodeGlobalOrFlat(
      bytes: Array[Byte],
      offset: Int,
      rawWord: Long,
      target: Target,
      layout: InstructionLayout
  ): Either[NonEmpty[String], Decoded] =
    for {
      _ <- requireBytes(bytes, offset, layout.bits / 8)
      word    = rawWord
      second  = unsignedWord(bytes, offset + 4)
      encoded = word | (second << 32)
      wide    = Option.when(layout.bits > 64)(BigInt(word) | (BigInt(second) << 32) |
        (BigInt(unsignedWord(bytes, offset + 8)) << 64))
      field = (name: EncodingField) => wide.fold(layout.get(encoded, name))(value => layout.get(value, name).toLong)
      vectorAddress =
        field(if (wide.nonEmpty) EncodingField.VADDR else EncodingField.ADDR).toInt.unsafe[VectorRegisterIndex]
      dataRegister =
        field(if (wide.nonEmpty) EncodingField.VSRC else EncodingField.DATA).toInt.unsafe[VectorRegisterIndex]
      scalarAddress = field(EncodingField.SADDR).toInt.unsafe[ScalarRegisterIndex]
      destination   = field(EncodingField.VDST).toInt.unsafe[VectorRegisterIndex]
      returnValue   = if (wide.nonEmpty) (field(EncodingField.TH) & 1L) != 0
      else layout.byName.get(EncodingField.GLC).fold(Bits.isSet(word, 16))(_.get(word) != 0)
      coherent = if (wide.nonEmpty) field(EncodingField.SCOPE) >= 2 else returnValue
      segment  = if (layout.format == EncodingFormat.VSCRATCH || layout.format == EncodingFormat.FLAT_SCRATCH) 1L
      else if (layout.format == EncodingFormat.VGLOBAL) 2L
      else if (layout.format == EncodingFormat.VFLAT) 0L else field(EncodingField.SEG)
      // Legacy FLAT offsets are unsigned; GLOBAL/SCRATCH and the wider RDNA4 encodings are signed.
      memoryOffset = if (segment == 0 && wide.isEmpty) field(EncodingField.OFFSET).toInt
      else wide.fold(layout.signed(encoded, EncodingField.OFFSET))(value => layout.signed(value, EncodingField.OFFSET))
      // RDNA3 and later encode the null scalar address as 124.
      absentScalar = isAbsentScalarOffset(scalarAddress.raw.toLong) ||
        (Set(ISA.RDNA3, ISA.RDNA3_5, ISA.GFX11_7, ISA.RDNA4)(target.isa) && scalarAddress.raw == 124)
      operation <- if (segment == 1) {
        val scalar = Option.when(!absentScalar)(scalarAddress)
        val vector = Option.when(layout.byName.get(EncodingField.SVE).fold(scalarAddress.raw != 127)(_ =>
          field(EncodingField.SVE) != 0
        ))(vectorAddress)
        OpcodeIndex.resolve(
          if (wide.nonEmpty) EncodingFormat.VSCRATCH else EncodingFormat.FLAT_SCRATCH,
          field(EncodingField.OP).toInt,
          target
        ).flatMap { opcode =>
          def load(width: Int where Between[1, 16], signed: Boolean = false): Either[NonEmpty[String], Instruction] =
            Right(Instruction.ScratchLoad(destination, vector, scalar, width, memoryOffset, signed))
          def store(width: Int where Between[1, 16]): Either[NonEmpty[String], Instruction] =
            Right(Instruction.ScratchStore(vector, scalar, dataRegister, width, memoryOffset))
          opcode match {
            case ScratchLoadB32 | ScratchLoadDword      => load(4)
            case ScratchLoadB64 | ScratchLoadDwordx2    => load(8)
            case ScratchLoadB96 | ScratchLoadDwordx3    => load(12)
            case ScratchLoadB128 | ScratchLoadDwordx4   => load(16)
            case ScratchLoadU8 | ScratchLoadUbyte       => load(1)
            case ScratchLoadU16 | ScratchLoadUshort     => load(2)
            case ScratchLoadI8 | ScratchLoadSbyte       => load(1, true)
            case ScratchLoadI16 | ScratchLoadSshort     => load(2, true)
            case ScratchStoreB32 | ScratchStoreDword    => store(4)
            case ScratchStoreB64 | ScratchStoreDwordx2  => store(8)
            case ScratchStoreB96 | ScratchStoreDwordx3  => store(12)
            case ScratchStoreB128 | ScratchStoreDwordx4 => store(16)
            case ScratchStoreB8 | ScratchStoreByte      => store(1)
            case ScratchStoreB16 | ScratchStoreShort    => store(2)
            case other => Left[NonEmpty[String], Instruction](s"$other SCRATCH execution is not implemented")
          }
        }
      } else if (segment == 2) {
        val scalar = Option.when(!absentScalar)(scalarAddress)
        val format = if (layout.format == EncodingFormat.FLAT_GLOBAL || layout.format == EncodingFormat.VGLOBAL)
          layout.format
        else EncodingFormat.GLOBAL
        OpcodeIndex.resolve(format, field(EncodingField.OP).toInt, target).flatMap {
          case GlobalInv                       => Right(Instruction.CacheInvalidate)
          case GlobalWb                        => Right(Instruction.Wait)
          case GlobalWbinv                     => Right(Instruction.CacheWritebackInvalidate)
          case GlobalLoadDword | GlobalLoadB32 =>
            Right(vmemLoad(
              true,
              destination,
              vectorAddress,
              scalar,
              4,
              memoryOffset,
              coherent
            ))
          case GlobalLoadDwordx2 | GlobalLoadB64 =>
            Right(vmemLoad(
              true,
              destination,
              vectorAddress,
              scalar,
              8,
              memoryOffset,
              coherent
            ))
          case GlobalLoadDwordx3 | GlobalLoadB96 =>
            Right(vmemLoad(
              true,
              destination,
              vectorAddress,
              scalar,
              12,
              memoryOffset,
              coherent
            ))
          case GlobalLoadDwordx4 | GlobalLoadB128 =>
            Right(vmemLoad(
              true,
              destination,
              vectorAddress,
              scalar,
              16,
              memoryOffset,
              coherent
            ))
          case GlobalLoadUbyte | GlobalLoadU8 =>
            Right(vmemLoad(
              true,
              destination,
              vectorAddress,
              scalar,
              1,
              memoryOffset,
              coherent
            ))
          case GlobalLoadSbyte | GlobalLoadI8 =>
            Right(vmemLoadSigned(true, destination, vectorAddress, scalar, 1, memoryOffset))
          case GlobalLoadSshort | GlobalLoadI16 =>
            Right(vmemLoadSigned(true, destination, vectorAddress, scalar, 2, memoryOffset))
          case GlobalLoadShortD16 =>
            Right(vmemLoadD16(true, destination, vectorAddress, scalar, memoryOffset, false))
          case GlobalLoadShortD16Hi =>
            Right(vmemLoadD16(true, destination, vectorAddress, scalar, memoryOffset, true))
          case GlobalLoadUshort | GlobalLoadU16 =>
            Right(vmemLoad(
              true,
              destination,
              vectorAddress,
              scalar,
              2,
              memoryOffset,
              coherent
            ))
          case GlobalStoreDword | GlobalStoreB32 =>
            Right(vmemStore(true, vectorAddress, scalar, dataRegister, 4, memoryOffset))
          case GlobalStoreDwordx2 | GlobalStoreB64 =>
            Right(vmemStore(true, vectorAddress, scalar, dataRegister, 8, memoryOffset))
          case GlobalStoreDwordx3 | GlobalStoreB96 =>
            Right(vmemStore(true, vectorAddress, scalar, dataRegister, 12, memoryOffset))
          case GlobalStoreDwordx4 | GlobalStoreB128 =>
            Right(vmemStore(true, vectorAddress, scalar, dataRegister, 16, memoryOffset))
          case GlobalStoreByte | GlobalStoreB8 =>
            Right(vmemStore(true, vectorAddress, scalar, dataRegister, 1, memoryOffset))
          case GlobalStoreShort | GlobalStoreB16 =>
            Right(vmemStore(true, vectorAddress, scalar, dataRegister, 2, memoryOffset))
          case GlobalAtomicAdd | GlobalAtomicAddU32 =>
            Right(Instruction.GlobalAtomicAdd(
              dest = destination,
              address = vectorAddress,
              scalarAddress = scalar,
              src = dataRegister,
              bytes = 4,
              offset = memoryOffset,
              returnValue = returnValue
            ))
          case GlobalAtomicAddX2 | GlobalAtomicAddU64 =>
            Right(Instruction.GlobalAtomicAdd(
              dest = destination,
              address = vectorAddress,
              scalarAddress = scalar,
              src = dataRegister,
              bytes = 8,
              offset = memoryOffset,
              returnValue = returnValue
            ))
          case GlobalAtomicInc | GlobalAtomicIncU32 =>
            Right(Instruction.GlobalAtomicInc(
              dest = destination,
              address = vectorAddress,
              scalarAddress = scalar,
              src = dataRegister,
              bytes = 4,
              offset = memoryOffset,
              returnValue = returnValue
            ))
          case GlobalAtomicIncX2 | GlobalAtomicIncU64 =>
            Right(Instruction.GlobalAtomicInc(
              dest = destination,
              address = vectorAddress,
              scalarAddress = scalar,
              src = dataRegister,
              bytes = 8,
              offset = memoryOffset,
              returnValue = returnValue
            ))
          case GlobalAtomicCmpswap | GlobalAtomicCmpswapB32 =>
            Right(vmemCompareSwap(
              true,
              destination,
              vectorAddress,
              scalar,
              dataRegister,
              4,
              1,
              memoryOffset,
              returnValue
            ))
          case GlobalAtomicCmpswapX2 | GlobalAtomicCmpswapB64 =>
            Right(vmemCompareSwap(
              true,
              destination,
              vectorAddress,
              scalar,
              dataRegister,
              8,
              2,
              memoryOffset,
              returnValue
            ))
          case GlobalStoreByteD16Hi | GlobalStoreD16HiB8 =>
            Right(vmemStoreD16(true, vectorAddress, scalar, dataRegister, 1, memoryOffset))
          case GlobalStoreShortD16Hi | GlobalStoreD16HiB16 =>
            Right(vmemStoreD16(true, vectorAddress, scalar, dataRegister, 2, memoryOffset))
          case other => Left[NonEmpty[String], Nothing](s"$other GLOBAL execution is not implemented")
        }
      } else if (segment == 0 && (scalarAddress.raw == 0 || absentScalar)) {
        OpcodeIndex.resolve(
          if (wide.nonEmpty) EncodingFormat.VFLAT else EncodingFormat.FLAT,
          field(EncodingField.OP).toInt,
          target
        ).flatMap {
          case FlatLoadUbyte | FlatLoadU8 =>
            Right(vmemLoad(
              false,
              destination,
              vectorAddress,
              None,
              1,
              memoryOffset,
              coherent
            ))
          case FlatLoadSbyte | FlatLoadI8 =>
            Right(vmemLoadSigned(false, destination, vectorAddress, None, 1, memoryOffset))
          case FlatLoadShortD16 =>
            Right(vmemLoadD16(false, destination, vectorAddress, None, memoryOffset, false))
          case FlatLoadShortD16Hi =>
            Right(vmemLoadD16(false, destination, vectorAddress, None, memoryOffset, true))
          case FlatLoadSshort | FlatLoadI16 =>
            Right(vmemLoadSigned(false, destination, vectorAddress, None, 2, memoryOffset))
          case FlatLoadUshort | FlatLoadU16 =>
            Right(vmemLoad(
              false,
              destination,
              vectorAddress,
              None,
              2,
              memoryOffset,
              coherent
            ))
          case FlatLoadDword | FlatLoadB32 =>
            Right(vmemLoad(
              false,
              destination,
              vectorAddress,
              None,
              4,
              memoryOffset,
              coherent
            ))
          case FlatLoadDwordx2 | FlatLoadB64 =>
            Right(vmemLoad(
              false,
              destination,
              vectorAddress,
              None,
              8,
              memoryOffset,
              coherent
            ))
          case FlatLoadDwordx3 | FlatLoadB96 =>
            Right(vmemLoad(
              false,
              destination,
              vectorAddress,
              None,
              12,
              memoryOffset,
              coherent
            ))
          case FlatLoadDwordx4 | FlatLoadB128 =>
            Right(vmemLoad(
              false,
              destination,
              vectorAddress,
              None,
              16,
              memoryOffset,
              coherent
            ))
          case FlatStoreShort | FlatStoreB16 =>
            Right(vmemStore(false, vectorAddress, None, dataRegister, 2, memoryOffset))
          case FlatStoreByte | FlatStoreB8 =>
            Right(vmemStore(false, vectorAddress, None, dataRegister, 1, memoryOffset))
          case FlatStoreByteD16Hi | FlatStoreD16HiB8 =>
            Right(vmemStoreD16(false, vectorAddress, None, dataRegister, 1, memoryOffset))
          case FlatStoreShortD16Hi | FlatStoreD16HiB16 =>
            Right(vmemStoreD16(false, vectorAddress, None, dataRegister, 2, memoryOffset))
          case FlatStoreDword | FlatStoreB32 =>
            Right(vmemStore(false, vectorAddress, None, dataRegister, 4, memoryOffset))
          case FlatStoreDwordx2 | FlatStoreB64 =>
            Right(vmemStore(false, vectorAddress, None, dataRegister, 8, memoryOffset))
          case FlatStoreDwordx3 | FlatStoreB96 =>
            Right(vmemStore(false, vectorAddress, None, dataRegister, 12, memoryOffset))
          case FlatStoreDwordx4 | FlatStoreB128 =>
            Right(vmemStore(false, vectorAddress, None, dataRegister, 16, memoryOffset))
          case opcode @ (FlatAtomicInc | FlatAtomicIncU32 | FlatAtomicIncX2 | FlatAtomicIncU64) =>
            Right(Instruction.GlobalAtomicInc(
              dest = destination,
              address = vectorAddress,
              scalarAddress = None,
              src = dataRegister,
              bytes = if (opcode == FlatAtomicIncX2 || opcode == FlatAtomicIncU64) 8 else 4,
              offset = memoryOffset,
              returnValue = returnValue
            ))
          case FlatAtomicAddX2 | FlatAtomicAddU64 =>
            Right(Instruction.GlobalAtomicAdd(
              destination,
              vectorAddress,
              None,
              dataRegister,
              8,
              memoryOffset,
              returnValue
            ))
          case FlatAtomicCmpswap | FlatAtomicCmpswapB32 =>
            Right(vmemCompareSwap(
              false,
              destination,
              vectorAddress,
              None,
              dataRegister,
              4,
              1,
              memoryOffset,
              returnValue
            ))
          case FlatAtomicSwap | FlatAtomicSwapB32 =>
            Right(Instruction.GlobalAtomicSwap(
              dest = destination,
              address = vectorAddress,
              scalarAddress = None,
              src = dataRegister,
              bytes = 4,
              offset = memoryOffset,
              returnValue = returnValue
            ))
          case FlatAtomicSwapX2 | FlatAtomicSwapB64 =>
            Right(Instruction.GlobalAtomicSwap(
              dest = destination,
              address = vectorAddress,
              scalarAddress = None,
              src = dataRegister,
              bytes = 8,
              offset = memoryOffset,
              returnValue = returnValue
            ))
          case atomicOpcode @ (FlatAtomicAdd | FlatAtomicAddU32 | FlatAtomicSub | FlatAtomicSubU32 |
              FlatAtomicMinU32 | FlatAtomicMaxU32 | FlatAtomicUmin | FlatAtomicUmax |
              FlatAtomicSmin | FlatAtomicSmax | FlatAtomicAnd | FlatAtomicAndB32 |
              FlatAtomicOr | FlatAtomicOrB32 | FlatAtomicXor | FlatAtomicXorB32) =>
            val atomicOperation = atomicOpcode match {
              case FlatAtomicAdd | FlatAtomicAddU32  => AtomicBinaryOperation.Add
              case FlatAtomicSub | FlatAtomicSubU32  => AtomicBinaryOperation.Subtract
              case FlatAtomicMinU32 | FlatAtomicUmin => AtomicBinaryOperation.MinimumUnsigned
              case FlatAtomicMaxU32 | FlatAtomicUmax => AtomicBinaryOperation.MaximumUnsigned
              case FlatAtomicSmin                    => AtomicBinaryOperation.MinimumSigned
              case FlatAtomicSmax                    => AtomicBinaryOperation.MaximumSigned
              case FlatAtomicAnd | FlatAtomicAndB32  => AtomicBinaryOperation.And
              case FlatAtomicOr | FlatAtomicOrB32    => AtomicBinaryOperation.Or
              case _                                 => AtomicBinaryOperation.Xor
            }
            Right(Instruction.GlobalAtomicBinary(
              operation = atomicOperation,
              dest = destination,
              address = vectorAddress,
              scalarAddress = None,
              src = dataRegister,
              bytes = 4,
              offset = memoryOffset,
              returnValue = returnValue
            ))
          case FlatAtomicCmpswapX2 | FlatAtomicCmpswapB64 =>
            Right(vmemCompareSwap(
              false,
              destination,
              vectorAddress,
              None,
              dataRegister,
              8,
              2,
              memoryOffset,
              returnValue
            ))
          case other => Left[NonEmpty[String], Nothing](s"$other FLAT execution is not implemented")
        }
      } else Left[
        NonEmpty[String],
        Nothing
      ](s"VMEM scalar address s[$scalarAddress:${scalarAddress.raw + 1}] is not implemented")
      banked <- if (!layout.byName.get(EncodingField.ACC).exists(_.get(encoded) != 0)) Right(operation)
      else operation match {
        case op: Instruction.GlobalLoad  => Right(op.copy(bank = RegisterBank.Accumulator))
        case op: Instruction.FlatLoad    => Right(op.copy(bank = RegisterBank.Accumulator))
        case op: Instruction.GlobalStore => Right(op.copy(bank = RegisterBank.Accumulator))
        case op: Instruction.FlatStore   => Right(op.copy(bank = RegisterBank.Accumulator))
        case _                           =>
          Left[NonEmpty[String], Instruction]("accumulator register bank is not implemented for this VMEM operation")
      }
    } yield Decoded(banked, layout.bits / 8)

  private def decodeMUBUF(
      bytes: Array[Byte],
      offset: Int,
      word: Long,
      target: Target,
      layout: InstructionLayout
  ): Either[NonEmpty[String], Decoded] =
    for {
      _ <- requireBytes(bytes, offset, 8)
      second  = unsignedWord(bytes, offset + 4)
      encoded = word | (second << 32)
      opcode <- OpcodeIndex.resolve(EncodingFormat.MUBUF, layout.get(word, EncodingField.OP).toInt, target)
      cacheOperation = opcode match {
        case BufferInv | BufferInvl2 | BufferWbinvl1 | BufferWbinvl1Vol |
            BufferGl0Inv | BufferGl1Inv => Some(Instruction.CacheInvalidate)
        // Wait already flushes staged host writes before execution continues.
        case BufferWbl2 => Some(Instruction.Wait)
        case _          => None
      }
      unsupportedAddressing =
        Vec(EncodingField.IDXEN, EncodingField.ADDR64, EncodingField.LDS, EncodingField.TFE, EncodingField.ACC)
          .exists(field => layout.byName.get(field).exists(_.get(encoded) != 0L))
      _ <- Either.cond[NonEmpty[String], Unit](
        cacheOperation.nonEmpty || !unsupportedAddressing,
        (),
        s"$opcode buffer addressing/modifiers are not implemented"
      )
      address =
        Option.when(layout.get(encoded, EncodingField.OFFEN) != 0L)(layout.register(encoded, EncodingField.VADDR))
      dataRegister = layout.register(encoded, EncodingField.VDATA)
      resource     = layout.scalarRegister(encoded, EncodingField.SRSRC)
      soffset      = layout.register(encoded, EncodingField.SOFFSET)
      scalarOffset = Option.when(soffset.raw != 0x80)(soffset.raw.unsafe[ScalarRegisterIndex])
      immediate    = layout.get(word, EncodingField.OFFSET).toInt
      operation <- cacheOperation.fold[Either[NonEmpty[String], Instruction]](opcode match {
        case BufferLoadDword | BufferLoadB32 =>
          Right(Instruction.BufferLoad(
            dest = dataRegister,
            address = address,
            resource = resource,
            scalarOffset = scalarOffset,
            bytes = 4,
            offset = immediate
          ))
        case BufferLoadUbyte | BufferLoadU8 =>
          Right(Instruction.BufferLoad(
            dest = dataRegister,
            address = address,
            resource = resource,
            scalarOffset = scalarOffset,
            bytes = 1,
            offset = immediate
          ))
        case BufferLoadDwordx2 | BufferLoadB64 =>
          Right(Instruction.BufferLoad(
            dest = dataRegister,
            address = address,
            resource = resource,
            scalarOffset = scalarOffset,
            bytes = 8,
            offset = immediate
          ))
        case BufferLoadDwordx4 | BufferLoadB128 =>
          Right(Instruction.BufferLoad(
            dest = dataRegister,
            address = address,
            resource = resource,
            scalarOffset = scalarOffset,
            bytes = 16,
            offset = immediate
          ))
        case BufferLoadShortD16 | BufferLoadD16B16 =>
          Right(Instruction.BufferLoadD16(
            dest = dataRegister,
            address = address,
            resource = resource,
            scalarOffset = scalarOffset,
            bytes = 2,
            offset = immediate,
            high = false
          ))
        case BufferLoadShortD16Hi | BufferLoadD16HiB16 =>
          Right(Instruction.BufferLoadD16(
            dest = dataRegister,
            address = address,
            resource = resource,
            scalarOffset = scalarOffset,
            bytes = 2,
            offset = immediate,
            high = true
          ))
        case BufferStoreShort | BufferStoreB16 =>
          Right(Instruction.BufferStore(
            address = address,
            resource = resource,
            scalarOffset = scalarOffset,
            src = dataRegister,
            bytes = 2,
            offset = immediate
          ))
        case BufferStoreByte | BufferStoreB8 =>
          Right(Instruction.BufferStore(
            address = address,
            resource = resource,
            scalarOffset = scalarOffset,
            src = dataRegister,
            bytes = 1,
            offset = immediate
          ))
        case BufferStoreShortD16Hi | BufferStoreD16HiB16 =>
          Right(Instruction.BufferStoreD16High(
            address = address,
            resource = resource,
            scalarOffset = scalarOffset,
            src = dataRegister,
            bytes = 2,
            offset = immediate
          ))
        case BufferStoreDword | BufferStoreB32 =>
          Right(Instruction.BufferStore(
            address = address,
            resource = resource,
            scalarOffset = scalarOffset,
            src = dataRegister,
            bytes = 4,
            offset = immediate
          ))
        case BufferStoreDwordx2 | BufferStoreB64 =>
          Right(Instruction.BufferStore(
            address = address,
            resource = resource,
            scalarOffset = scalarOffset,
            src = dataRegister,
            bytes = 8,
            offset = immediate
          ))
        case BufferStoreDwordx4 | BufferStoreB128 =>
          Right(Instruction.BufferStore(
            address = address,
            resource = resource,
            scalarOffset = scalarOffset,
            src = dataRegister,
            bytes = 16,
            offset = immediate
          ))
        case other => Left[NonEmpty[String], Nothing](s"$other MUBUF execution is not implemented")
      })(Right(_))
    } yield Decoded(operation, 8)

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
  private def requireBytes(bytes: Array[Byte], offset: Int, size: Int): Either[NonEmpty[String], Unit] =
    Either.cond[NonEmpty[String], Unit](
      ByteRange.contains(bytes.length, offset.toLong, size.toLong),
      (),
      s"truncated $size-byte instruction"
    )

  private def unsignedWord(bytes: Array[Byte], offset: Int): Long =
    LittleEndian.decode(bytes, offset, 4)
}

/** One compiler per loaded code object; kernels with device calls share decoded linked text. */
private class VMCompiler {
  import VMCompiler.*

  private val linked = java.util.concurrent.ConcurrentHashMap[Long, Either[NonEmpty[String], DecodedProgram]]()

  def compile(kernel: CodeObjectKernel): Either[Fault, Kernel] = {
    val linkedText  = kernel.textAddress != 0L && kernel.text.nonEmpty
    val programCode = if (linkedText) kernel.text else kernel.code
    val decoded     = if (linkedText) {
      val entrySize  = math.min(kernel.code.size, kernel.functionSizes.getOrElse(kernel.codeAddress, kernel.code.size))
      val entryRange = linkedTextRanges(kernel.textAddress, kernel.text, Map(kernel.codeAddress -> entrySize))
      decodeProgram(programCode, kernel.target, entryRange, deferExternalBranches = true).flatMap { program =>
        // Decode unrelated functions only when the entry can transfer outside its own range.
        if (selfContained(program)) Right(program)
        else linked.computeIfAbsent(
          kernel.textAddress,
          _ =>
            decodeProgram(
              programCode,
              kernel.target,
              linkedTextRanges(kernel.textAddress, kernel.text, kernel.functionSizes)
            )
        )
      }
    } else decodeProgram(programCode, kernel.target)
    decoded
      .leftMap(detail => Fault.Unsupported(detail))
      .flatMap { program =>
        val entryOffset = if (linkedText) kernel.codeAddress - kernel.textAddress else 0L
        val entryIndex  =
          if (entryOffset == 0L) Some(0)
          else if (entryOffset < 0L || entryOffset > Int.MaxValue.toLong) None
          else program.instructionIndices.get(entryOffset.toInt)
        entryIndex.toRight(
          Fault.InvalidTask(
            f"AMDGPU kernel ${kernel.name} entry 0x${kernel.codeAddress}%X is not an instruction in .text"
          )
        ).map { entry =>
          Kernel(
            kernel,
            program.operations,
            program.instructionIndices,
            program.instructionOffsets,
            entry
          )
        }
      }
  }
}
