package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Constraint.IntBetween

import CodeObject.*
import net.kurobako.gemstone.isa.amdgpu.Instruction as AMDGPUInstruction
import BufferInstructionDefs.*
import DataShareInstructionDefs.*
import FlatInstructionDefs.*
import PackedVectorInstructionDefs.*
import ScalarMemoryInstructionDefs.*
import SOP1InstructionDefs.*
import SOP2InstructionDefs.*
import SOPCInstructionDefs.*
import SOPKInstructionDefs.*
import SOPPInstructionDefs.*
import VectorCompareInstructionDefs.*
import VOP1InstructionDefs.*
import VOP2InstructionDefs.*
import VOP3InstructionDefs.*
import VMIR.*

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.Vec.*
import net.kurobako.gemstone.binary.{Bits, ByteRange, LittleEndian}
import net.kurobako.gemstone.vm.Fault

import cats.syntax.either.*
import cats.syntax.traverse.*

import scala.collection.immutable.IntMap

private[amdgpu] object VMCompiler {
  private type VendorInstruction = AMDGPUInstruction.Definition

  private def scalarUnaryOperation(opcode: VendorInstruction): Option[ScalarUnaryOperation] = opcode match {
    case SAbsI32      => Some(ScalarUnaryOperation.AbsoluteI32)
    case SSextI32I8   => Some(ScalarUnaryOperation.SignExtendI8)
    case SSextI32I16  => Some(ScalarUnaryOperation.SignExtendI16)
    case SBcnt1I32B32 => Some(ScalarUnaryOperation.CountBits32)
    case SBcnt1I32B64 => Some(ScalarUnaryOperation.CountBits64)
    case SBrevB32     => Some(ScalarUnaryOperation.ReverseBits32)
    case SNotB32      => Some(ScalarUnaryOperation.Not32)
    case SNotB64      => Some(ScalarUnaryOperation.Not64)
    case SFlbitI32    => Some(ScalarUnaryOperation.FirstBitHighI32)
    case SBitset0B32  => Some(ScalarUnaryOperation.ClearBit32)
    case SBitset1B32  => Some(ScalarUnaryOperation.SetBit32)
    case _            => None
  }

  private def scalarBinaryOperation(opcode: VendorInstruction): Option[ScalarBinaryOperation] = opcode match {
    case SAndB32       => Some(ScalarBinaryOperation.And32)
    case SOrB32        => Some(ScalarBinaryOperation.Or32)
    case SMulI32       => Some(ScalarBinaryOperation.MultiplyI32)
    case SMulHiU32     => Some(ScalarBinaryOperation.MultiplyHighU32)
    case SMulHiI32     => Some(ScalarBinaryOperation.MultiplyHighI32)
    case SAddI32       => Some(ScalarBinaryOperation.AddI32)
    case SSubI32       => Some(ScalarBinaryOperation.SubtractI32)
    case SLshlB32      => Some(ScalarBinaryOperation.ShiftLeft32)
    case SLshl2AddU32  => Some(ScalarBinaryOperation.ShiftLeftTwoAndAddU32)
    case SAshrI32      => Some(ScalarBinaryOperation.ShiftRightArithmetic32)
    case SBfeU32       => Some(ScalarBinaryOperation.BitFieldExtractU32)
    case SBfeI32       => Some(ScalarBinaryOperation.BitFieldExtractI32)
    case SLshrB32      => Some(ScalarBinaryOperation.ShiftRight32)
    case SAndn2B32     => Some(ScalarBinaryOperation.AndNot32)
    case SOrn2B32      => Some(ScalarBinaryOperation.OrNot32)
    case SNorB32       => Some(ScalarBinaryOperation.Nor32)
    case SPackLlB32B16 => Some(ScalarBinaryOperation.PackLowLow16)
    case SPackLhB32B16 => Some(ScalarBinaryOperation.PackLowHigh16)
    case SPackHhB32B16 => Some(ScalarBinaryOperation.PackHighHigh16)
    case SAddU32       => Some(ScalarBinaryOperation.AddU32)
    case SAddcU32      => Some(ScalarBinaryOperation.AddCarryU32)
    case SSubU32       => Some(ScalarBinaryOperation.SubtractU32)
    case SSubbU32      => Some(ScalarBinaryOperation.SubtractBorrowU32)
    case SXorB32       => Some(ScalarBinaryOperation.Xor32)
    case _             => None
  }

  private def scalarMinMaxOperation(opcode: VendorInstruction): Option[ScalarMinMaxOperation] = opcode match {
    case SMinU32 => Some(ScalarMinMaxOperation.MinimumU32)
    case SMaxU32 => Some(ScalarMinMaxOperation.MaximumU32)
    case SMinI32 => Some(ScalarMinMaxOperation.MinimumI32)
    case SMaxI32 => Some(ScalarMinMaxOperation.MaximumI32)
    case _       => None
  }

  private def scalarBinary64Operation(opcode: VendorInstruction): Option[ScalarBinary64Operation] = opcode match {
    case SAndB64   => Some(ScalarBinary64Operation.And)
    case SAndn2B64 => Some(ScalarBinary64Operation.AndNot)
    case SOrB64    => Some(ScalarBinary64Operation.Or)
    case SOrn2B64  => Some(ScalarBinary64Operation.OrNot)
    case SNorB64   => Some(ScalarBinary64Operation.Nor)
    case SXorB64   => Some(ScalarBinary64Operation.Xor)
    case SLshlB64  => Some(ScalarBinary64Operation.ShiftLeft)
    case SLshrB64  => Some(ScalarBinary64Operation.ShiftRight)
    case SAshrI64  => Some(ScalarBinary64Operation.ShiftRightArithmetic)
    case SBfeI64   => Some(ScalarBinary64Operation.BitFieldExtractI64)
    case _         => None
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

  private def scalarImmediateCompareOperation(opcode: VendorInstruction): Option[ScalarImmediateCompareOperation] =
    opcode match {
      case SCmpkEqI32 => Some(ScalarImmediateCompareOperation.EqualI32)
      case SCmpkLgI32 => Some(ScalarImmediateCompareOperation.NotEqualI32)
      case SCmpkGtI32 => Some(ScalarImmediateCompareOperation.GreaterI32)
      case SCmpkLtI32 => Some(ScalarImmediateCompareOperation.LessI32)
      case SCmpkEqU32 => Some(ScalarImmediateCompareOperation.EqualU32)
      case SCmpkLgU32 => Some(ScalarImmediateCompareOperation.NotEqualU32)
      case SCmpkGtU32 => Some(ScalarImmediateCompareOperation.GreaterU32)
      case SCmpkGeU32 => Some(ScalarImmediateCompareOperation.GreaterOrEqualU32)
      case SCmpkLtU32 => Some(ScalarImmediateCompareOperation.LessU32)
      case SCmpkLeU32 => Some(ScalarImmediateCompareOperation.LessOrEqualU32)
      case _          => None
    }

  private def vectorBitFindOperation(opcode: VendorInstruction): Option[VectorBitFindOperation] = opcode match {
    case VFfblB32 => Some(VectorBitFindOperation.FirstBitLow32)
    case VFfbhI32 => Some(VectorBitFindOperation.FirstBitHighI32)
    case VFfbhU32 => Some(VectorBitFindOperation.FirstBitHighU32)
    case _        => None
  }

  private def vectorConvertOperation(opcode: VendorInstruction): Option[VectorConvertOperation] = opcode match {
    case VFrexpExpI32F64                        => Some(VectorConvertOperation.FrexpExponentF64)
    case VFrexpExpI32F32                        => Some(VectorConvertOperation.FrexpExponentF32)
    case VFrexpMantF32                          => Some(VectorConvertOperation.FrexpMantissaF32)
    case VFrexpMantF64                          => Some(VectorConvertOperation.FrexpMantissaF64)
    case VRcpIflagF32 | VRcpF32 | VRcpLegacyF32 => Some(VectorConvertOperation.ReciprocalF32)
    case VRcpF64                                => Some(VectorConvertOperation.ReciprocalF64)
    case VTruncF32                              => Some(VectorConvertOperation.TruncateF32)
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
    case VMinF32                   => Some(VectorBinaryOperation.MinimumF32)
    case VMinF16                   => Some(VectorBinaryOperation.MinimumF16)
    case VMaxI32                   => Some(VectorBinaryOperation.MaximumI32)
    case VMaxI16                   => Some(VectorBinaryOperation.MaximumI16)
    case VMaxU16                   => Some(VectorBinaryOperation.MaximumU16)
    case VMaxF32                   => Some(VectorBinaryOperation.MaximumF32)
    case VMaxF16                   => Some(VectorBinaryOperation.MaximumF16)
    case VLdexpF32                 => Some(VectorBinaryOperation.LoadExponentF32)
    case VAddF32                   => Some(VectorBinaryOperation.AddF32)
    case VMulF32 | VMulLegacyF32   => Some(VectorBinaryOperation.MultiplyF32)
    case VMacF32 | VFmacF32        => Some(VectorBinaryOperation.MultiplyAccumulateF32)
    case VAddF16                   => Some(VectorBinaryOperation.AddF16)
    case VMulF16                   => Some(VectorBinaryOperation.MultiplyF16)
    case VFmacF16                  => Some(VectorBinaryOperation.FusedMultiplyAccumulateF16)
    case VSubF32                   => Some(VectorBinaryOperation.SubtractF32)
    case VAddCoU32                 => Some(VectorBinaryOperation.AddCarryU32)
    case VAddcCoU32                => Some(VectorBinaryOperation.AddWithCarryU32)
    case VSubCoU32                 => Some(VectorBinaryOperation.SubtractCarryU32)
    case VSubbCoU32                => Some(VectorBinaryOperation.SubtractWithBorrowU32)
    case _                         => None
  }

  private def vectorF32BinaryOperation(opcode: VendorInstruction): Option[VectorF32BinaryOperation] = opcode match {
    case VAddF32                 => Some(VectorF32BinaryOperation.Add)
    case VSubF32                 => Some(VectorF32BinaryOperation.Subtract)
    case VSubrevF32              => Some(VectorF32BinaryOperation.ReverseSubtract)
    case VMulF32 | VMulLegacyF32 => Some(VectorF32BinaryOperation.Multiply)
    case VLdexpF32               => Some(VectorF32BinaryOperation.LoadExponent)
    case VMaxF32                 => Some(VectorF32BinaryOperation.Maximum)
    case VMinF32                 => Some(VectorF32BinaryOperation.Minimum)
    case _                       => None
  }

  private def vectorSubDwordOperation(opcode: VendorInstruction): Option[VectorSubDwordOperation] = opcode match {
    case VCndmaskB32                              => Some(VectorSubDwordOperation.ConditionalMask32)
    case VAddU32 | VAddNcU32                      => Some(VectorSubDwordOperation.AddU32)
    case VAddU16 | VAddNcU16                      => Some(VectorSubDwordOperation.AddU16)
    case VAddF16                                  => Some(VectorSubDwordOperation.AddF16)
    case VAddCoU32                                => Some(VectorSubDwordOperation.AddCarryU32)
    case VAddcCoU32                               => Some(VectorSubDwordOperation.AddWithCarryU32)
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
    case VSubbCoU32                               => Some(VectorSubDwordOperation.SubtractWithBorrowU32)
    case VSubrevU32 | VSubrevNcU32 | VSubrevCoU32 =>
      Some(VectorSubDwordOperation.SubtractReverseU32)
    case VSubbrevCoU32 => Some(VectorSubDwordOperation.SubtractReverseWithBorrowU32)
    case _             => None
  }

  private def vectorBinary64Operation(opcode: VendorInstruction): Option[VectorBinary64Operation] = opcode match {
    case VLdexpF64            => Some(VectorBinary64Operation.LoadExponent)
    case VAddF64 | VAddF64E64 => Some(VectorBinary64Operation.Add)
    case VMulF64 | VMulF64E64 => Some(VectorBinary64Operation.Multiply)
    case VMaxF64 | VMaxF64E64 => Some(VectorBinary64Operation.Maximum)
    case VMinF64 | VMinF64E64 => Some(VectorBinary64Operation.Minimum)
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

  private def dsAtomicByteWidth(operation: DSAtomicOperation): AtomicByteCount = operation match {
    case DSAtomicOperation.AddU64 | DSAtomicOperation.SubtractU64 | DSAtomicOperation.IncrementU64 |
        DSAtomicOperation.DecrementU64 | DSAtomicOperation.And64 | DSAtomicOperation.Or64 | DSAtomicOperation.Xor64 |
        DSAtomicOperation.MinimumI64 | DSAtomicOperation.MaximumI64 | DSAtomicOperation.MinimumU64 |
        DSAtomicOperation.MaximumU64 | DSAtomicOperation.AddF64 | DSAtomicOperation.MinimumF64 |
        DSAtomicOperation.MaximumF64 => AtomicByteCount(8)
    case _ => AtomicByteCount(4)
  }

  private def dsAtomicReturnsValue(opcode: VendorInstruction): Boolean = opcode match {
    case DsAddU32 | DsSubU32 | DsIncU32 | DsDecU32 | DsMinI32 | DsMaxI32 | DsMinU32 | DsMaxU32 => false
    case _                                                                                     => true
  }

  private def vectorComparison(opcode: VendorInstruction): Option[VectorComparison] =
    AMDGPUInstruction.ComparisonOpcodes.get(opcode).map { comparison =>
      val kind = comparison.kind match {
        case AMDGPUInstruction.ComparisonKind.Signed16     => VectorComparisonKind.Signed16
        case AMDGPUInstruction.ComparisonKind.Signed32     => VectorComparisonKind.Signed32
        case AMDGPUInstruction.ComparisonKind.Signed64     => VectorComparisonKind.Signed64
        case AMDGPUInstruction.ComparisonKind.Unsigned16   => VectorComparisonKind.Unsigned16
        case AMDGPUInstruction.ComparisonKind.Unsigned32   => VectorComparisonKind.Unsigned32
        case AMDGPUInstruction.ComparisonKind.Unsigned64   => VectorComparisonKind.Unsigned64
        case AMDGPUInstruction.ComparisonKind.Float16      => VectorComparisonKind.Float16
        case AMDGPUInstruction.ComparisonKind.Float32      => VectorComparisonKind.Float32
        case AMDGPUInstruction.ComparisonKind.Float64      => VectorComparisonKind.Float64
        case AMDGPUInstruction.ComparisonKind.ClassFloat32 => VectorComparisonKind.ClassFloat32
        case AMDGPUInstruction.ComparisonKind.ClassFloat64 => VectorComparisonKind.ClassFloat64
      }
      val relation = comparison.relation match {
        case AMDGPUInstruction.ComparisonRelation.Equal             => VectorComparisonRelation.Equal
        case AMDGPUInstruction.ComparisonRelation.NotEqual          => VectorComparisonRelation.NotEqual
        case AMDGPUInstruction.ComparisonRelation.Less              => VectorComparisonRelation.Less
        case AMDGPUInstruction.ComparisonRelation.LessOrEqual       => VectorComparisonRelation.LessOrEqual
        case AMDGPUInstruction.ComparisonRelation.Greater           => VectorComparisonRelation.Greater
        case AMDGPUInstruction.ComparisonRelation.GreaterOrEqual    => VectorComparisonRelation.GreaterOrEqual
        case AMDGPUInstruction.ComparisonRelation.NotLess           => VectorComparisonRelation.NotLess
        case AMDGPUInstruction.ComparisonRelation.NotLessOrEqual    => VectorComparisonRelation.NotLessOrEqual
        case AMDGPUInstruction.ComparisonRelation.NotGreater        => VectorComparisonRelation.NotGreater
        case AMDGPUInstruction.ComparisonRelation.NotGreaterOrEqual => VectorComparisonRelation.NotGreaterOrEqual
        case AMDGPUInstruction.ComparisonRelation.Ordered           => VectorComparisonRelation.Ordered
        case AMDGPUInstruction.ComparisonRelation.Unordered         => VectorComparisonRelation.Unordered
        case AMDGPUInstruction.ComparisonRelation.NotLessOrGreater  => VectorComparisonRelation.NotLessOrGreater
        case AMDGPUInstruction.ComparisonRelation.Class             => VectorComparisonRelation.Class
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
    VCmpLgU32,
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
    VCmpLgI16,
    VCmpLgU16,
    VCmpNeI16,
    VCmpNeU16,
    VCmpGtI16,
    VCmpGtU16,
    VCmpGeU16,
    VCmpLtI16,
    VCmpLtU16,
    VCmpLeI16,
    VCmpLeU16
  )
  private lazy val SupportedVOP3SourceModifierOpcodes: Set[VendorInstruction] = Set(
    VCndmaskB32,
    VLdexpF32,
    VLdexpF64,
    VRcpF32,
    VFrexpMantF32,
    VFrexpMantF64,
    VCvtF64F32,
    VMaxF64,
    VMaxF64E64,
    VMinF64,
    VMinF64E64,
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
    VAddF64E64,
    VMulF64,
    VMulF64E64,
    VMadF32,
    VFmaF32,
    VFmacF32,
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
    VDivScaleF64,
    VDivFmasF64,
    VDivFixupF64,
    VMaxF32,
    VMinF32,
    VTrigPreopF64
  )
  private lazy val SDWAVOP2Opcodes: Set[VendorInstruction] = Set(
    VAddU32,
    VAddNcU32,
    VAddCoU32,
    VAddcCoU32,
    VSubNcU32,
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
    VCndmaskB32,
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
  def validate(code: Vec[Byte], target: Target): Either[String, Unit] =
    decodeProgram(code, target).map(_ => ())

  /** Decode one instruction for decoder-focused tests without exposing the executable model publicly. */
  private[amdgpu] def decodeInstruction(
      code: Vec[Byte],
      target: Target
  ): Either[String, Instruction] =
    Either.cond(code.size >= 4, (), "truncated instruction at byte 0")
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
              base == jump && low == base && lowSource == base && high == base + 1 && highSource == high &&
              addend == temporary && temporary != base && temporary != high && constant(zero).contains(0L) =>
          for {
            a <- constant(left)
            b <- constant(right)
            target = offset.toLong + 4L + Bits.low32(a + b).raw
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

  def compile(kernel: CodeObjectKernel): Either[Fault, Kernel] = {
    val linkedText     = kernel.textAddress != 0L && kernel.text.nonEmpty
    val programCode    = if (linkedText) kernel.text else kernel.code
    val functionRanges =
      if (linkedText) linkedTextRanges(kernel.textAddress, kernel.text, kernel.functionSizes)
      else Vec.empty[(Int, Int)]
    val decoded = if (linkedText) {
      val entrySize  = math.min(kernel.code.size, kernel.functionSizes.getOrElse(kernel.codeAddress, kernel.code.size))
      val entryRange = linkedTextRanges(kernel.textAddress, kernel.text, Map(kernel.codeAddress -> entrySize))
      decodeProgram(programCode, kernel.target, entryRange, deferExternalBranches = true).flatMap { program =>
        // Decode unrelated functions only when the entry can transfer outside its own range.
        if (selfContained(program)) Right(program)
        else decodeProgram(programCode, kernel.target, functionRanges)
      }
    } else decodeProgram(programCode, kernel.target, functionRanges)
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
  ): Either[String, DecodedProgram] = {
    val bytes      = Vec.toArray(code)
    val ranges     = if (functionRanges.nonEmpty) functionRanges else Vec(0 -> bytes.length)
    val result     = Vec.newBuilder[(Int, Instruction)]
    var rangeIndex = 0
    var failure    = Option.empty[String]
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
              case None => Left(s"branch at byte $offset targets non-instruction byte $targetOffset")
            }
          case other => Right(other)
        }
      }.sequence.map(operations => DecodedProgram(operations, indices, decoded.map(_._1)))
    }
  }

  private def decode(bytes: Array[Byte], offset: Int, target: Target): Either[String, Decoded] = {
    val word      = unsignedWord(bytes, offset)
    val high2Bits = Bits.unsignedField(word, 30, 2)
    val high4Bits = Bits.unsignedField(word, 28, 4)
    val high6Bits = Bits.unsignedField(word, 26, 6)
    val high7Bits = Bits.unsignedField(word, 25, 7)
    val high9Bits = Bits.unsignedField(word, 23, 9)
    // GFX6--9 use the 0x30 SMEM prefix; GFX10+ moved the same scalar-memory
    // encoding to 0x3d.  The operand fields and literal layout are otherwise
    // identical for the subset interpreted here.
    if (high6Bits == Decoding.SmemGfx6Prefix || high6Bits == Decoding.SmemGfx10Prefix)
      decodeSMEM(bytes, offset, word, target)
    else if (high9Bits == Decoding.Sop1Prefix) decodeSOP1(bytes, offset, word, target)
    else if (high9Bits == Decoding.SopCPrefix) decodeSOPC(bytes, offset, word, target)
    else if (high9Bits == Decoding.SopPPrefix) decodeSOPP(word, target)
    else if (high4Bits == Decoding.SopKPrefix) decodeSOPK(word, target)
    else if (high2Bits == Decoding.Sop2Prefix) decodeSOP2(bytes, offset, word, target)
    else if (high6Bits == Decoding.Vop3PPrefix) decodeVOP3P(bytes, offset, word, target)
    else if (high6Bits == Decoding.Vop3Prefix || high6Bits == Decoding.Vop3LiteralPrefix)
      decodeVOP3(bytes, offset, word, target)
    else if (high6Bits == Decoding.DsPrefix) decodeDS(bytes, offset, word, target)
    else if (high6Bits == Decoding.GlobalFlatPrefix) decodeGlobalOrFlat(bytes, offset, word, target)
    else if (high6Bits == Decoding.MubufPrefix) decodeMUBUF(bytes, offset, word, target)
    else if (high7Bits == Decoding.Vop1Prefix) decodeVOP1(bytes, offset, word, target)
    else if (high7Bits == Decoding.VopCPrefix) decodeVOPC(bytes, offset, word, target)
    else decodeVOP2(bytes, offset, word, target)
  }

  private def decodeSMEM(
      bytes: Array[Byte],
      offset: Int,
      word: Long,
      target: Target
  ): Either[String, Decoded] =
    for {
      _      <- requireBytes(bytes, offset, 8)
      opcode <- opcodeDefinition("SMEM", Bits.unsignedIntField(word, 18, 8), target)
      count  <- opcode match {
        case SLoadDword    => Right(1)
        case SLoadDwordx2  => Right(2)
        case SLoadDwordx4  => Right(4)
        case SLoadDwordx8  => Right(8)
        case SLoadDwordx16 => Right(16)
        case other         => Left(s"$other SMEM execution is not implemented")
      }
      // GFX10+ moved the immediate/SGPR offset selector out of the first
      // dword.  The high seven bits of the second dword carry SOFFSET; the
      // null-register encoding (0x7d on pre-GFX11 targets) denotes an
      // immediate offset.  GFX6--9 retain the explicit IMM bit in dword 0.
      second    = unsignedWord(bytes, offset + 4)
      immediate = target.family match {
        case Family.GFX10 | Family.GFX11 | Family.GFX12 =>
          Bits.unsignedField(second, 25, 7) == Decoding.NullOffset
        case _ => Bits.isSet(word, 17)
      }
    } yield Decoded(
      Instruction.SLoad(
        Bits.unsignedIntField(word, 6, 7),
        count,
        Bits.unsignedIntField(word, 0, 6) * 2,
        if (immediate) Bits.unsignedIntField(second, 0, 21) else 0,
        Option.when(!immediate)(Bits.unsignedIntField(second, 25, 7))
      ),
      8
    )

  private def decodeSOP1(
      bytes: Array[Byte],
      offset: Int,
      word: Long,
      target: Target
  ): Either[String, Decoded] =
    for {
      opcode <- opcodeDefinition("SOP1", Bits.unsignedIntField(word, 8, 8), target)
      literal =
        Option.when(Bits.unsignedField(word, 0, 8) == Decoding.LiteralSource.toLong)(())
      _   <- requireBytes(bytes, offset, if (literal.nonEmpty) 8 else 4)
      src <- scalarSource(
        Bits.unsignedIntField(word, 0, 8),
        literal.map(_ => unsignedWord(bytes, offset + 4))
      )
      destination = Bits.unsignedIntField(word, 16, 7)
      operation <- opcode match {
        case SMovB32  => Right(Instruction.SMove(destination, src))
        case SMovB64  => Right(Instruction.SMove64(destination, src))
        case SCmovB32 =>
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
        case SAndn2SaveexecB32 =>
          Right(Instruction.SAndn2SaveExec(destination, src, 32))
        case SAndn2SaveexecB64 =>
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
            .toRight(s"$other SOP1 execution is not implemented")
      }
    } yield Decoded(operation, if (literal.nonEmpty) 8 else 4)

  private def decodeSOP2(
      bytes: Array[Byte],
      offset: Int,
      word: Long,
      target: Target
  ): Either[String, Decoded] = {
    val srcAEncoding = Bits.unsignedIntField(word, 0, 8)
    val srcBEncoding = Bits.unsignedIntField(word, 8, 8)
    val hasLiteral   = srcAEncoding == Decoding.LiteralSource || srcBEncoding == Decoding.LiteralSource
    for {
      _      <- requireBytes(bytes, offset, if (hasLiteral) 8 else 4)
      opcode <- opcodeDefinition("SOP2", Bits.unsignedIntField(word, 23, 7), target)
      literal = Option.when(hasLiteral)(unsignedWord(bytes, offset + 4))
      srcA <- scalarSource(srcAEncoding, literal)
      srcB <- scalarSource(srcBEncoding, literal)
      destination = Bits.unsignedIntField(word, 16, 7)
      operation <- opcode match {
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
            .toRight(s"$other SOP2 execution is not implemented")
      }
    } yield Decoded(operation, if (hasLiteral) 8 else 4)
  }

  private def decodeSOPK(word: Long, target: Target): Either[String, Decoded] =
    opcodeDefinition("SOPK", Bits.unsignedIntField(word, 23, 5), target).flatMap {
      // Memory and LDS operations finish synchronously before the next instruction.
      case SWaitcntVscnt | SWaitcntLgkmcnt => Right(Decoded(Instruction.Wait, 4))
      case SMovkI32                        =>
        val immediate = Bits.signedIntField(word, 0, 16)
        Right(Decoded(
          Instruction.SMoveImmediate(Bits.unsignedIntField(word, 16, 7), immediate),
          4
        ))
      case SMulkI32 =>
        val immediate = Bits.signedIntField(word, 0, 16)
        Right(Decoded(
          Instruction.SMultiplyImmediate(Bits.unsignedIntField(word, 16, 7), immediate),
          4
        ))
      case SAddkI32 =>
        val immediate = Bits.signedIntField(word, 0, 16)
        Right(Decoded(
          Instruction.SAddImmediate(Bits.unsignedIntField(word, 16, 7), immediate),
          4
        ))
      case SSetregB32 | SSetregImm32B32 =>
        // Hardware register writes (notably FLAT_SCR_LO/HI) configure the
        // physical wave, while the virtual machine models scratch storage
        // directly per lane. Retain the decoded operation so compiler-
        // generated prologues execute deterministically as a no-op.
        Right(
          Decoded(
            Instruction.SSetReg(Bits.low16Int(word), Bits.unsignedIntField(word, 16, 7)),
            4
          )
        )
      case compare @ (SCmpkEqI32 | SCmpkLgI32 | SCmpkGtI32 | SCmpkLtI32 | SCmpkEqU32 | SCmpkLgU32 | SCmpkGtU32 | SCmpkGeU32 | SCmpkLtU32 | SCmpkLeU32) =>
        val immediate = Bits.signedIntField(word, 0, 16)
        scalarImmediateCompareOperation(compare)
          .map(operation =>
            Decoded(
              Instruction.SCmpImmediate(operation, Bits.unsignedIntField(word, 16, 7), immediate),
              4
            )
          )
          .toRight(s"$compare SOPK execution is not implemented")
      case other => Left(s"$other SOPK execution is not implemented")
    }

  private def decodeSOPC(
      bytes: Array[Byte],
      offset: Int,
      word: Long,
      target: Target
  ): Either[String, Decoded] = {
    val srcAEncoding = Bits.low8Int(word)
    val srcBEncoding = Bits.unsignedIntField(word, 8, 8)
    val hasLiteral   = srcAEncoding == Decoding.LiteralSource || srcBEncoding == Decoding.LiteralSource
    for {
      _      <- requireBytes(bytes, offset, if (hasLiteral) 8 else 4)
      opcode <- opcodeDefinition("SOPC", Bits.unsignedIntField(word, 16, 7), target)
      literal = Option.when(hasLiteral)(unsignedWord(bytes, offset + 4))
      srcA      <- scalarSource(srcAEncoding, literal)
      srcB      <- scalarSource(srcBEncoding, literal)
      operation <- opcode match {
        case SSetGprIdxOn =>
          // The interpreter has no hardware GPR-index mode. Keep this
          // compiler-generated mode transition explicit, while preserving
          // ordinary register addressing for the virtual execution model.
          Right(Instruction.SSetGprIdxOn)
        case other =>
          scalarCompareOperation(other)
            .map(operation => Instruction.SCmp(operation, srcA, srcB))
            .toRight(s"$other SOPC execution is not implemented")
      }
    } yield Decoded(operation, if (hasLiteral) 8 else 4)
  }

  private def decodeSOPP(word: Long, target: Target): Either[String, Decoded] =
    opcodeDefinition("SOPP", Bits.unsignedIntField(word, 16, 7), target).flatMap {
      case SWaitcnt | SWaitcntVscnt => Right(Decoded(Instruction.Wait, 4))
      case SNop | SInstPrefetch     => Right(Decoded(Instruction.Wait, 4))
      // `s_clause` only constrains the hardware scheduler's clause grouping;
      // it has no observable effect in the deterministic virtual machine.
      case SClause => Right(Decoded(Instruction.Wait, 4))
      // Wave priority only affects physical scheduling; virtual execution is
      // deterministic and therefore treats the hint as a no-op.
      case SSetprio => Right(Decoded(Instruction.Wait, 4))
      case SBarrier => Right(Decoded(Instruction.Barrier, 4))
      case SEndpgm  => Right(Decoded(Instruction.End, 4))
      // Linked runtime failure handlers may be decoded without being called.
      // Preserve the trap as an executable operation rather than rejecting the module.
      case STrap => Right(Decoded(Instruction.Trap(Bits.low16Int(word)), 4))
      // LLVM emits s_code_end as instruction-cache padding at the end of a
      // linked GFX10+ .text section.  It is not a program terminator (the
      // kernel's s_endpgm performs that job); treating the padding marker as
      // a no-op lets us index the whole linked text without executing it.
      case SCodeEnd       => Right(Decoded(Instruction.Wait, 4))
      case SBranch        => branch(word, ScalarBranchCondition.Always)
      case SCbranchScc0   => branch(word, ScalarBranchCondition.SccZero)
      case SCbranchScc1   => branch(word, ScalarBranchCondition.SccNonZero)
      case SCbranchVccz   => branch(word, ScalarBranchCondition.VccZero)
      case SCbranchVccnz  => branch(word, ScalarBranchCondition.VccNonZero)
      case SCbranchExecz  => branch(word, ScalarBranchCondition.ExecZero)
      case SCbranchExecnz => branch(word, ScalarBranchCondition.ExecNonZero)
      case SSleep         => Right(Decoded(Instruction.Wait, 4))
      case other          => Left(s"$other SOPP execution is not implemented")
    }

  private def branch(word: Long, condition: ScalarBranchCondition): Either[String, Decoded] = {
    val immediate    = Bits.low16Int(word)
    val displacement = if ((immediate & 0x8000) == 0) immediate else immediate - 0x10000
    Right(Decoded(Instruction.BranchRelative(condition, displacement), 4))
  }

  private def decodeVOP2(
      bytes: Array[Byte],
      offset: Int,
      word: Long,
      target: Target
  ): Either[String, Decoded] = {
    val srcAEncoding = Bits.unsignedIntField(word, 0, 9)
    val opcodeResult = opcodeDefinition("VOP2", Bits.unsignedIntField(word, 25, 7), target)
    val hasLiteral   = srcAEncoding == Decoding.LiteralSource || opcodeResult.exists(opcode =>
      opcode == VMadmkF32 || opcode == VFmamkF32 || opcode == VFmaakF32
    )
    val hasSDWA = srcAEncoding == Decoding.SdwaSource
    for {
      _      <- requireBytes(bytes, offset, if (hasLiteral || hasSDWA) 8 else 4)
      opcode <- opcodeResult.leftMap(error => s"$error (word=0x${word.toHexString})")
      extension = Option.when(hasSDWA)(unsignedWord(bytes, offset + 4))
      srcA <- extension match {
        case Some(sdwa) =>
          val encoding = Bits.low8Int(sdwa)
          Right(
            if (Bits.isSet(sdwa, 23)) InstructionSource.Scalar(encoding)
            else InstructionSource.VectorRegister(encoding)
          )
        case None => vectorSource(
            srcAEncoding,
            Option.when(hasLiteral)(unsignedWord(bytes, offset + 4))
          )
      }
      // VOP2's SRC1 field is a VGPR index in the ordinary encoding.  It is
      // only allowed to name an SGPR when the SDWA extension explicitly
      // selects scalar source mode.
      srcB = extension match {
        case Some(sdwa) if Bits.isSet(sdwa, 31) =>
          InstructionSource.Scalar(Bits.unsignedIntField(word, 9, 8))
        case _ => InstructionSource.VectorRegister(Bits.unsignedIntField(word, 9, 8))
      }
      literal = Option.when(hasLiteral)(unsignedWord(bytes, offset + 4))
      dest    = Bits.unsignedIntField(word, 17, 8)
      operation <- opcode match {
        case supported if hasSDWA && SDWAVOP2Opcodes.contains(supported) =>
          for {
            sdwa <- extension.toRight("missing SDWA extension word")
            _    <- Either.cond(
              Bits.unsignedField(sdwa, 20, 2) == 0L &&
                Bits.unsignedField(sdwa, 28, 2) == 0L,
              (),
              s"$supported SDWA source modifiers are not implemented"
            )
            _ <- Either.cond(
              Bits.unsignedField(sdwa, 13, 3) == 0L,
              (),
              s"$supported SDWA destination clamp modifiers are not implemented"
            )
            destSelection   <- subDwordSelection(Bits.unsignedIntField(sdwa, 8, 3))
            destUnused      <- subDwordDestinationUnused(Bits.unsignedIntField(sdwa, 11, 2))
            srcASelection   <- subDwordSelection(Bits.unsignedIntField(sdwa, 16, 3))
            srcBSelection   <- subDwordSelection(Bits.unsignedIntField(sdwa, 24, 3))
            binaryOperation <- vectorSubDwordOperation(supported)
              .toRight(s"$supported SDWA execution is not implemented")
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
          Left(s"$unsupported SDWA execution is not implemented")
        case VMadmkF32 | VFmamkF32 =>
          literal.map(value => Instruction.VMadMkF32(dest = dest, srcA = srcA, multiplier = value, addend = srcB))
            .toRight(s"$opcode is missing its literal operand")
        case VFmaakF32 =>
          literal.map(value => Instruction.VMadAkF32(dest = dest, srcA = srcA, srcB = srcB, addend = value))
            .toRight(s"$opcode is missing its literal operand")
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
            condition = InstructionSource.Scalar(Decoding.VccLo),
            absA = false,
            absB = false,
            negateA = false,
            negateB = false
          ))
        case VAddCoU32 =>
          Right(Instruction.VAddCarry(
            dest = dest,
            carryDest = Decoding.VccLo,
            srcA = srcA,
            srcB = srcB,
            carryIn = None
          ))
        case VAddCoCiU32 | VAddcCoU32 =>
          Right(Instruction.VAddCarry(
            dest = dest,
            carryDest = Decoding.VccLo,
            srcA = srcA,
            srcB = srcB,
            carryIn = Some(InstructionSource.Scalar(Decoding.VccLo))
          ))
        case VSubCoU32 =>
          Right(Instruction.VSubCarry(
            dest = dest,
            carryDest = Decoding.VccLo,
            srcA = srcA,
            srcB = srcB,
            carryIn = None
          ))
        case VSubCoCiU32 | VSubbCoU32 =>
          Right(Instruction.VSubCarry(
            dest = dest,
            carryDest = Decoding.VccLo,
            srcA = srcA,
            srcB = srcB,
            carryIn = Some(InstructionSource.Scalar(Decoding.VccLo))
          ))
        case VSubrevCoU32 =>
          Right(Instruction.VSubReverseCarry(
            dest = dest,
            carryDest = Decoding.VccLo,
            srcA = srcA,
            srcB = srcB,
            carryIn = None
          ))
        case VSubbrevCoU32 | VSubrevCoCiU32 =>
          Right(Instruction.VSubReverseCarry(
            dest = dest,
            carryDest = Decoding.VccLo,
            srcA = srcA,
            srcB = srcB,
            carryIn = Some(InstructionSource.Scalar(Decoding.VccLo))
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
            .toRight(s"$other VOP2 execution is not implemented")
      }
    } yield Decoded(operation, if (hasLiteral || hasSDWA) 8 else 4)
  }

  private def decodeVOP1(
      bytes: Array[Byte],
      offset: Int,
      word: Long,
      target: Target
  ): Either[String, Decoded] = {
    val sourceEncoding = Bits.unsignedIntField(word, 0, 9)
    val hasLiteral     = sourceEncoding == Decoding.LiteralSource
    val hasDPP         = sourceEncoding == Decoding.DppSource
    val hasSDWA        = sourceEncoding == Decoding.SdwaSource
    for {
      _      <- requireBytes(bytes, offset, if (hasLiteral || hasDPP || hasSDWA) 8 else 4)
      opcode <- opcodeDefinition("VOP1", Bits.unsignedIntField(word, 9, 8), target)
      extension = Option.when(hasDPP || hasSDWA)(unsignedWord(bytes, offset + 4))
      src <- extension match {
        case Some(sdwa) if hasSDWA =>
          val encoding = Bits.low8Int(sdwa)
          Right(if (Bits.isSet(sdwa, 23)) InstructionSource.Scalar(encoding)
          else InstructionSource.VectorRegister(encoding))
        case Some(dpp) => Right(InstructionSource.VectorRegister(Bits.low8Int(dpp)))
        case None      => vectorSource(
            sourceEncoding,
            Option.when(hasLiteral)(unsignedWord(bytes, offset + 4))
          )
      }
      destination = Bits.unsignedIntField(word, 17, 8)
      operation <- opcode match {
        case VMovB32 if hasDPP =>
          val dpp     = extension.get
          val control = Bits.unsignedIntField(dpp, 8, 9)
          for {
            _ <- Either.cond(
              control <= 0xff || (control >= 0x100 && control <= 0x13f) || control == 0x142 || control == 0x143,
              (),
              s"$VMovB32 DPP control $control is not implemented"
            )
            _ <- Either.cond(
              Bits.unsignedField(dpp, 20, 4) == 0L,
              (),
              s"$VMovB32 DPP source modifiers are not implemented"
            )
          } yield Instruction.VMoveDPP(
            dest = destination,
            src = Bits.low8Int(dpp),
            control = control,
            rowMask = Bits.unsignedIntField(dpp, 28, 4),
            bankMask = Bits.unsignedIntField(dpp, 24, 4),
            boundControl = Bits.isSet(dpp, 19),
            fetchInactive = Bits.isSet(dpp, 18)
          )
        case unsupported if hasDPP =>
          Left(s"$unsupported DPP execution is not implemented")
        case VMovB32     => Right(Instruction.VMove(destination, src))
        case VMovrelsB32 => Right(Instruction.VMoveRelative(destination, src))
        case VMovreldB32 => Right(Instruction.VMoveRelativeDestination(destination, src))
        case VNotB32     => Right(Instruction.VNot(destination, src))
        case VSwapB32    => Right(Instruction.VSwap(destination, src))
        case VBfrevB32   =>
          Right(Instruction.VBitReverse(destination, src))
        case VReadfirstlaneB32 =>
          Right(Instruction.VReadFirstLane(Bits.unsignedIntField(word, 17, 7), src))
        case other =>
          vectorBitFindOperation(other)
            .map(operation => Instruction.VBitFind(operation, destination, src))
            .orElse(
              vectorConvertOperation(other)
                .map(operation => Instruction.VConvert(operation, destination, src))
            )
            .toRight(s"$other VOP1 execution is not implemented")
      }
    } yield Decoded(operation, if (hasLiteral || hasDPP || hasSDWA) 8 else 4)
  }

  private def decodeVOPC(
      bytes: Array[Byte],
      offset: Int,
      word: Long,
      target: Target
  ): Either[String, Decoded] = {
    val srcAEncoding = Bits.unsignedIntField(word, 0, 9)
    val hasLiteral   = srcAEncoding == Decoding.LiteralSource
    val hasSDWA      = srcAEncoding == Decoding.SdwaSource
    for {
      _      <- requireBytes(bytes, offset, if (hasLiteral || hasSDWA) 8 else 4)
      opcode <- opcodeDefinition("VOPC", Bits.unsignedIntField(word, 17, 8), target)
      extension = Option.when(hasSDWA)(unsignedWord(bytes, offset + 4))
      srcA <- extension match {
        case Some(sdwa) if Bits.isSet(sdwa, 23) => Right(InstructionSource.Scalar(Bits.low8Int(sdwa)))
        case Some(sdwa)                         => Right(InstructionSource.VectorRegister(Bits.low8Int(sdwa)))
        case None                               => vectorSource(
            srcAEncoding,
            Option.when(hasLiteral)(unsignedWord(bytes, offset + 4))
          )
      }
      // In VOPC, SRC1 is always a VGPR in the normal encoding.  Unlike
      // SRC0, its 8-bit field does not use the scalar/constant source table.
      srcB = extension match {
        case Some(sdwa) if Bits.isSet(sdwa, 31) =>
          InstructionSource.Scalar(Bits.unsignedIntField(word, 9, 8))
        case _ => InstructionSource.VectorRegister(Bits.unsignedIntField(word, 9, 8))
      }
      destination = extension.flatMap(sdwa =>
        Option.when(Bits.isSet(sdwa, 15))(Bits.unsignedIntField(sdwa, 8, 7))
      )
      comparison <- vectorComparison(opcode).toRight(s"$opcode VOPC execution is not implemented")
      operation  <- if (hasSDWA) {
        for {
          sdwa          <- extension.toRight("missing SDWA extension word")
          srcASelection <- subDwordSelection(Bits.unsignedIntField(sdwa, 16, 3))
          srcBSelection <- subDwordSelection(Bits.unsignedIntField(sdwa, 24, 3))
          _             <- Either.cond(
            Bits.unsignedField(sdwa, 20, 2) == 0L,
            (),
            s"$opcode SDWA source modifiers are not implemented"
          )
          _ <- Either.cond(
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
      target: Target
  ): Either[String, Decoded] =
    for {
      _ <- requireBytes(bytes, offset, 8)
      opcodeId = Bits.unsignedIntField(word, 16, 10)
      // GFX9 VOP3-encoded compares reuse the VOPC opcode space, but the
      // generated metadata only lists their compact VOPC form.
      opcode <- opcodeDefinition("VOP3", opcodeId, target).orElse(opcodeDefinition("VOPC", opcodeId, target))
      second       = unsignedWord(bytes, offset + 4)
      srcAEncoding = Bits.unsignedIntField(second, 0, 9)
      srcBEncoding = Bits.unsignedIntField(second, 9, 9)
      srcCEncoding = Bits.unsignedIntField(second, 18, 9)
      hasLiteral   = srcAEncoding == Decoding.LiteralSource || srcBEncoding == Decoding.LiteralSource ||
        srcCEncoding == Decoding.LiteralSource
      _ <- requireBytes(bytes, offset, if (hasLiteral) 12 else 8)
      literal = Option.when(hasLiteral)(unsignedWord(bytes, offset + 8))
      srcA <- vectorSource(srcAEncoding, literal).leftMap(error =>
        s"$error in VOP3 word 0x${word.toHexString}/0x${second.toHexString}"
      )
      srcB <- vectorSource(srcBEncoding, literal).leftMap(error =>
        s"$error in VOP3 word 0x${word.toHexString}/0x${second.toHexString}"
      )
      srcC <- vectorSource(srcCEncoding, literal).leftMap(error =>
        s"$error in VOP3 word 0x${word.toHexString}/0x${second.toHexString}"
      )
      _ <- Either.cond(
        (second >>> 27) == 0L || vectorComparison(opcode).nonEmpty ||
          SupportedVOP3SourceModifierOpcodes.contains(opcode),
        (),
        s"$opcode source modifiers are not implemented"
      )
      destination      = Bits.unsignedIntField(word, 0, 8)
      carryDestination = Bits.unsignedIntField(word, 8, 7)
      absoluteA        = Bits.isSet(word, 8)
      absoluteB        = Bits.isSet(word, 9)
      absoluteC        = Bits.isSet(word, 10)
      negateA          = Bits.isSet(second, 29)
      negateB          = Bits.isSet(second, 30)
      negateC          = Bits.isSet(second, 31)
      clamp            = Bits.isSet(word, 15)
      operation <- opcode match {
        case VLshlrevB64 | VLshlrevB64E64 =>
          Right(Instruction.VShiftLeft64(destination, srcA, srcB))
        case VLshrrevB64 =>
          Right(Instruction.VShiftRight64(destination, srcA, srcB))
        case VAshrrevI64 =>
          Right(Instruction.VShiftRightArithmetic64(destination, srcA, srcB))
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
        case VFmaF64 =>
          Right(Instruction.VMadF64(
            dest = destination,
            srcA = srcA,
            srcB = srcB,
            srcC = srcC,
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
            absolute = Bits.unsignedIntField(word, 8, 3),
            negate = Bits.unsignedIntField(second, 29, 3)
          ))
        case VDivScaleF64 =>
          Right(Instruction.VDivScaleF64(
            dest = destination,
            conditionDest = carryDestination,
            srcA = srcA,
            srcB = srcB,
            srcC = srcC
          ))
        case VLdexpF64 | VMulF64 | VMulF64E64 | VMaxF64 | VMaxF64E64 | VMinF64 |
            VMinF64E64 =>
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
            .toRight(s"$opcode VOP3 execution is not implemented")
        case VMbcntLoU32B32 =>
          Right(Instruction.VMaskBitCount(destination, srcA, srcB, high = false))
        case VMbcntHiU32B32 | VMbcntHiU32B32E64 =>
          Right(Instruction.VMaskBitCount(destination, srcA, srcB, high = true))
        case VReadlaneB32 | VReadlaneB32E64 =>
          Right(Instruction.VReadLane(destination, srcA, srcB))
        case VWritelaneB32 | VWritelaneB32E64 =>
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
            .toRight(s"$opcode VOP3 execution is not implemented")
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
            case _ => Left(s"$opcode condition is not a scalar mask")
          }
        case VAddCoU32 =>
          Right(Instruction.VAddCarry(
            dest = destination,
            carryDest = carryDestination,
            srcA = srcA,
            srcB = srcB,
            carryIn = None
          ))
        case VAddCoCiU32 | VAddcCoU32 =>
          Right(Instruction.VAddCarry(
            dest = destination,
            carryDest = carryDestination,
            srcA = srcA,
            srcB = srcB,
            carryIn = Some(srcC)
          ))
        case VSubCoU32 =>
          Right(Instruction.VSubCarry(
            dest = destination,
            carryDest = carryDestination,
            srcA = srcA,
            srcB = srcB,
            carryIn = None
          ))
        case VSubbCoU32 | VSubCoCiU32 =>
          Right(Instruction.VSubCarry(
            dest = destination,
            carryDest = carryDestination,
            srcA = srcA,
            srcB = srcB,
            carryIn = Some(srcC)
          ))
        case VSubrevCoU32 =>
          Right(Instruction.VSubReverseCarry(
            dest = destination,
            carryDest = carryDestination,
            srcA = srcA,
            srcB = srcB,
            carryIn = None
          ))
        case VSubbrevCoU32 | VSubrevCoCiU32 =>
          Right(Instruction.VSubReverseCarry(
            dest = destination,
            carryDest = carryDestination,
            srcA = srcA,
            srcB = srcB,
            carryIn = Some(srcC)
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
        case VFmacF32 =>
          // FMAC is the accumulator form: the implicit addend is the
          // destination VGPR, which the VBinary execution path preserves.
          Right(Instruction.VBinary(
            VectorBinaryOperation.MultiplyAccumulateF32,
            destination,
            srcA,
            srcB,
            clamp = false
          ))
        case VDivFixupF32 =>
          Right(Instruction.VDivFixupF32(
            dest = Bits.low8Int(word),
            quotient = srcA,
            denominator = srcB,
            numerator = srcC,
            absolute = Bits.unsignedIntField(word, 8, 3),
            negate = Bits.unsignedIntField(second, 29, 3)
          ))
        case VBcntU32B32 =>
          Right(Instruction.VBitCount(dest = Bits.low8Int(word), src = srcA, base = srcB))
        case VPkFmaF16 =>
          Right(Instruction.VPackedFmaF16(
            dest = destination,
            srcA = srcA,
            srcB = srcB,
            srcC = srcC,
            lowSelection = Bits.unsignedIntField(word, 11, 3),
            highSelection = Bits.unsignedIntField(second, 27, 2) |
              (Bits.unsignedIntField(word, 14, 1) << 2),
            negateLow = Bits.unsignedIntField(second, 29, 3),
            negateHigh = Bits.unsignedIntField(word, 8, 3),
            clamp = clamp
          ))
        case VAdd3U32 | VOr3B32 | VXor3B32 | VFmaF16 | VDivFmasF32 | VMadLegacyU16 | VMadU16 | VMadU32U24 | VAlignbitB32 | VBfiB32 |
            VMed3I16 | VMed3I32 | VMed3U16 | VMed3U32 | VMin3F32 |
            VMax3I16 | VMax3U16 | VMin3I16 | VMin3U16 |
            VMax3I32 | VMax3U32 | VMin3I32 | VMin3U32 |
            VAndOrB32 | VBfeI32 | VBfeU32 | VXadU32 | VMadI32I24 | VAddLshlU32 | VLshlAddU32 | VLshlOrB32 | VPermB32 =>
          vectorTernaryOperation(opcode)
            .map(operation => Instruction.VTernary(operation, destination, srcA, srcB, srcC))
            .toRight(s"$opcode VOP3 execution is not implemented")
        case VPackB32F16 =>
          Right(Instruction.VPackB32F16(destination, srcA, srcB))
        case packed @ (VPkAddU16 | VPkAddF16 | VPkMulLoU16 | VPkMulF16 | VPkMaxF16 | VPkMinF16 | VPkSubI16 | VPkLshrrevB16) =>
          for {
            _               <- Either.cond((second >>> 29) == 0L, (), s"$packed packed modifiers are not implemented")
            packedOperation <- vectorPackedBinaryOperation(packed)
              .toRight(s"$packed VOP3 execution is not implemented")
          } yield Instruction.VPackedBinary(
            operation = packedOperation,
            dest = destination,
            srcA = srcA,
            srcB = srcB,
            srcAHighForHigh = Bits.isSet(second, 27),
            srcBHighForHigh = Bits.isSet(second, 28)
          )
        case VAddF64 | VAddF64E64 =>
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
            .toRight(s"$opcode VOP3 execution is not implemented")
        case VMadU64U32 =>
          Right(Instruction.VMadU64U32(destination, carryDestination, srcA, srcB, srcC))
        case VMadI64I32 =>
          Right(Instruction.VMadI64I32(destination, carryDestination, srcA, srcB, srcC))
        case VDivScaleF32 =>
          Right(Instruction.VDivScaleF32(
            dest = destination,
            conditionDest = carryDestination,
            srcA = srcA,
            srcB = srcB,
            srcC = srcC
          ))
        case VAddF32 | VSubF32 | VSubrevF32 | VMulF32 | VMulLegacyF32 | VLdexpF32 | VMaxF32 | VMinF32 =>
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
            .toRight(s"$opcode VOP3 execution is not implemented")
        case other =>
          vectorComparison(other)
            .map(comparison =>
              Instruction.VCompareTo(
                dest = Bits.unsignedIntField(word, 0, 7),
                operation = comparison,
                srcA = srcA,
                srcB = srcB,
                absA = absoluteA,
                absB = absoluteB,
                negateA = negateA,
                negateB = negateB
              )
            )
            .toRight(s"$other VOP3 execution is not implemented")
      }
    } yield Decoded(operation, if (hasLiteral) 12 else 8)

  /** Decode the packed 16-bit VOP3P encoding used by RDNA instructions such as `v_pk_lshrrev_b16`. VOP3P has the same
    * two-dword source layout as VOP3, but its opcode is selected from the packed-vector opcode space and the packed
    * lane selectors live in DWORD1 bits 27--28.
    */
  private def decodeVOP3P(
      bytes: Array[Byte],
      offset: Int,
      word: Long,
      target: Target
  ): Either[String, Decoded] = {
    val opcodeId = Bits.unsignedIntField(word, 16, 10)
    for {
      _ <- requireBytes(bytes, offset, 8)
      second       = unsignedWord(bytes, offset + 4)
      srcAEncoding = Bits.unsignedIntField(second, 0, 9)
      srcBEncoding = Bits.unsignedIntField(second, 9, 9)
      srcCEncoding = Bits.unsignedIntField(second, 18, 9)
      hasLiteral   = srcAEncoding == Decoding.LiteralSource || srcBEncoding == Decoding.LiteralSource ||
        srcCEncoding == Decoding.LiteralSource
      _      <- requireBytes(bytes, offset, if (hasLiteral) 12 else 8)
      opcode <- opcodeDefinition("VOP3P", opcodeId, target)
        .orElse(opcodeDefinition("VOP3", opcodeId, target))
      literal = Option.when(hasLiteral)(unsignedWord(bytes, offset + 8))
      srcA <- vectorSource(srcAEncoding, literal).leftMap(error =>
        s"$error in VOP3P word 0x${word.toHexString}/0x${second.toHexString}"
      )
      srcB <- vectorSource(srcBEncoding, literal).leftMap(error =>
        s"$error in VOP3P word 0x${word.toHexString}/0x${second.toHexString}"
      )
      srcC <- vectorSource(srcCEncoding, literal).leftMap(error =>
        s"$error in VOP3P word 0x${word.toHexString}/0x${second.toHexString}"
      )
      destination     = Bits.unsignedIntField(word, 0, 8)
      opSelection     = Bits.unsignedIntField(word, 11, 3)
      opSelectionHigh = Bits.unsignedIntField(second, 27, 2) |
        (Bits.unsignedIntField(word, 14, 1) << 2)
      negateLow  = Bits.unsignedIntField(second, 29, 3)
      negateHigh = Bits.unsignedIntField(word, 8, 3)
      clamp      = Bits.isSet(word, 15)
      operation <- opcode match {
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
            _               <- Either.cond((second >>> 29) == 0L, (), s"$packed packed modifiers are not implemented")
            packedOperation <- vectorPackedBinaryOperation(packed)
              .toRight(s"$packed VOP3P execution is not implemented")
          } yield Instruction.VPackedBinary(
            operation = packedOperation,
            dest = destination,
            srcA = srcA,
            srcB = srcB,
            srcAHighForHigh = Bits.isSet(second, 27),
            srcBHighForHigh = Bits.isSet(second, 28)
          )
        case other => Left(s"$other VOP3P execution is not implemented")
      }
    } yield Decoded(operation, if (hasLiteral) 12 else 8)
  }

  private def decodeDS(
      bytes: Array[Byte],
      offset: Int,
      word: Long,
      target: Target
  ): Either[String, Decoded] =
    for {
      _ <- requireBytes(bytes, offset, 8)
      // GCN/CDNA place GDS at bit 16 and OP at 17:24. RDNA moved both
      // fields one bit upward while retaining the two eight-bit offsets.
      opcodeShift = target.family match {
        case Family.GFX10 | Family.GFX11 | Family.GFX12 => 18
        case _                                          => 17
      }
      rawOpcode = Bits.unsignedIntField(word, opcodeShift, 8)
      opcode <- opcodeDefinition("DS", rawOpcode, target).leftMap(_ =>
        s"unknown DS opcode $rawOpcode for ${target.name} word=0x${word.toHexString}"
      )
      _ <- Either.cond(!Bits.isSet(word, opcodeShift - 1), (), s"$opcode GDS execution is not implemented")
      second   = unsignedWord(bytes, offset + 4)
      address  = Bits.unsignedIntField(second, 0, 8)
      dataA    = Bits.unsignedIntField(second, 8, 8)
      dataB    = Bits.unsignedIntField(second, 16, 8)
      dest     = Bits.unsignedIntField(second, 24, 8)
      offsetA  = Bits.unsignedIntField(word, 0, 8)
      offsetB  = Bits.unsignedIntField(word, 8, 8)
      dsOffset = Bits.low16Int(word)
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
            .toRight(s"$atomic DS execution is not implemented")
        case DsWriteB8  => Right(Instruction.DSWrite(address, dataA, None, TransferByteCount(1), dsOffset, None))
        case DsWriteB16 => Right(Instruction.DSWrite(address, dataA, None, TransferByteCount(2), dsOffset, None))
        case DsWriteB32 => Right(Instruction.DSWrite(address, dataA, None, TransferByteCount(4), dsOffset, None))
        case DsWriteB64 => Right(Instruction.DSWrite(address, dataA, None, TransferByteCount(8), dsOffset, None))
        case DsWriteB96 =>
          Right(Instruction.DSWrite(address, dataA, None, TransferByteCount(12), dsOffset, None))
        case DsWriteB128 =>
          Right(Instruction.DSWrite(address, dataA, None, TransferByteCount(16), dsOffset, None))
        case DsWriteB8D16Hi  => Right(Instruction.DSWriteD16HighByte(address, dataA, dsOffset))
        case DsWriteB16D16Hi => Right(Instruction.DSWriteD16High(address, dataA, dsOffset))
        case DsWrite2B32     =>
          Right(Instruction.DSWrite(
            address,
            dataA,
            Some(dataB),
            TransferByteCount(4),
            offsetA * 4,
            Some(offsetB * 4)
          ))
        case DsWrite2B64 =>
          Right(Instruction.DSWrite(
            address,
            dataA,
            Some(dataB),
            TransferByteCount(8),
            offsetA * 8,
            Some(offsetB * 8)
          ))
        case DsWrite2st64B32 =>
          Right(Instruction.DSWrite(
            address,
            dataA,
            Some(dataB),
            TransferByteCount(4),
            offsetA * 64 * 4,
            Some(offsetB * 64 * 4)
          ))
        case DsWrite2st64B64 =>
          Right(Instruction.DSWrite(
            address,
            dataA,
            Some(dataB),
            TransferByteCount(8),
            offsetA * 64 * 8,
            Some(offsetB * 64 * 8)
          ))
        case DsReadU8  => Right(Instruction.DSRead(dest, address, TransferByteCount(1), dsOffset, None))
        case DsReadU16 => Right(Instruction.DSRead(dest, address, TransferByteCount(2), dsOffset, None))
        case DsReadI8  =>
          Right(Instruction.DSSignedRead(dest, address, NarrowTransferByteCount(1), dsOffset, None))
        case DsReadI16 =>
          Right(Instruction.DSSignedRead(dest, address, NarrowTransferByteCount(2), dsOffset, None))
        case DsReadU8D16 =>
          Right(Instruction.DSReadD16(dest, address, NarrowTransferByteCount(1), dsOffset, false))
        case DsReadU8D16Hi =>
          Right(Instruction.DSReadD16(dest, address, NarrowTransferByteCount(1), dsOffset, true))
        case DsReadU16D16 =>
          Right(Instruction.DSReadD16(dest, address, NarrowTransferByteCount(2), dsOffset, false))
        case DsReadU16D16Hi =>
          Right(Instruction.DSReadD16(dest, address, NarrowTransferByteCount(2), dsOffset, true))
        case DsReadI8D16 =>
          Right(Instruction.DSSignedReadD16(dest, address, NarrowTransferByteCount(1), dsOffset, false))
        case DsReadI8D16Hi =>
          Right(Instruction.DSSignedReadD16(dest, address, NarrowTransferByteCount(1), dsOffset, true))
        case DsReadB32  => Right(Instruction.DSRead(dest, address, TransferByteCount(4), dsOffset, None))
        case DsReadB96  => Right(Instruction.DSRead(dest, address, TransferByteCount(12), dsOffset, None))
        case DsReadB128 => Right(Instruction.DSRead(dest, address, TransferByteCount(16), dsOffset, None))
        case DsReadB64  => Right(Instruction.DSRead(dest, address, TransferByteCount(8), dsOffset, None))
        case DsRead2B32 =>
          Right(Instruction.DSRead(dest, address, TransferByteCount(4), offsetA * 4, Some(offsetB * 4)))
        case DsRead2B64 =>
          Right(Instruction.DSRead(dest, address, TransferByteCount(8), offsetA * 8, Some(offsetB * 8)))
        case DsRead2st64B32 =>
          Right(Instruction.DSRead(
            dest,
            address,
            TransferByteCount(4),
            offsetA * 64 * 4,
            Some(offsetB * 64 * 4)
          ))
        case DsRead2st64B64 =>
          Right(Instruction.DSRead(
            dest,
            address,
            TransferByteCount(8),
            offsetA * 64 * 8,
            Some(offsetB * 64 * 8)
          ))
        case other => Left(s"$other DS execution is not implemented")
      }
    } yield Decoded(operation, 8)

  private def vmemLoad(
      global: Boolean,
      dest: Int,
      address: Int,
      scalar: Option[Int],
      bytes: TransferByteCount,
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
      dest: Int,
      address: Int,
      scalar: Option[Int],
      bytes: NarrowTransferByteCount,
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
      dest: Int,
      address: Int,
      scalar: Option[Int],
      offset: Int,
      high: Boolean
  ): Instruction =
    if (global)
      Instruction.GlobalLoadD16(dest = dest, address = address, scalarAddress = scalar, offset = offset, high = high)
    else Instruction.VFlatLoadD16(dest = dest, address = address, offset = offset, high = high)

  private def vmemStore(
      global: Boolean,
      address: Int,
      scalar: Option[Int],
      src: Int,
      bytes: TransferByteCount,
      offset: Int
  ): Instruction =
    if (global)
      Instruction.GlobalStore(address = address, scalarAddress = scalar, src = src, bytes = bytes, offset = offset)
    else Instruction.FlatStore(address = address, src = src, bytes = bytes, offset = offset)

  private def vmemStoreD16(
      global: Boolean,
      address: Int,
      scalar: Option[Int],
      src: Int,
      bytes: NarrowTransferByteCount,
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
      dest: Int,
      address: Int,
      scalar: Option[Int],
      src: Int,
      bytes: AtomicByteCount,
      compareOffset: Int,
      offset: Int,
      returnValue: Boolean
  ): Instruction =
    Instruction.GlobalAtomicCompareSwap(
      dest = dest,
      address = address,
      scalarAddress = if (global) scalar else None,
      compareSrc = src + compareOffset,
      swapSrc = src,
      bytes = bytes,
      offset = offset,
      returnValue = returnValue
    )

  private def decodeGlobalOrFlat(
      bytes: Array[Byte],
      offset: Int,
      rawWord: Long,
      target: Target
  ): Either[String, Decoded] =
    for {
      _ <- requireBytes(bytes, offset, 8)
      // RDNA encodes the VMEM coherence/address-mode controls in the bit that
      // was the high offset bit on GFX9.  Keep the raw word for mode
      // selection, but clear that control bit before extracting the offset.
      word = if (target.family == Family.GFX9) rawWord else rawWord & ~0x1000L
      // GFX10+ uses bit 12 for VMEM controls, leaving a signed 12-bit
      // displacement. Normalize negative values into the 13-bit form used
      // by the common execution path; GFX9 already carries signed 13 bits.
      memoryOffset = if (target.family == Family.GFX9) Bits.signedIntField(word, 0, 13)
      else Bits.signedIntField(word, 0, 12)
      second        = unsignedWord(bytes, offset + 4)
      vectorAddress = Bits.unsignedIntField(second, 0, 8)
      dataRegister  = Bits.unsignedIntField(second, 8, 8)
      scalarAddress = Bits.unsignedIntField(second, 16, 7)
      destination   = Bits.unsignedIntField(second, 24, 8)
      returnValue   = Bits.isSet(word, 16)
      operation <- if (Bits.isSet(word, 15)) {
        // GFX10 encodes the absent scalar address as 0x7d (the disassembler
        // prints it as `null`); older encodings use 0x7f.  Neither is an
        // actual SGPR pair for the flat/global operations we interpret.
        val scalar = Option.when(!Decoding.isAbsentScalarOffset(scalarAddress))(scalarAddress)
        opcodeDefinition("GLOBAL", Bits.unsignedIntField(word, 18, 7), target).flatMap {
          case GlobalLoadDword =>
            Right(vmemLoad(true, destination, vectorAddress, scalar, TransferByteCount(4), memoryOffset, returnValue))
          case GlobalLoadDwordx2 =>
            Right(vmemLoad(true, destination, vectorAddress, scalar, TransferByteCount(8), memoryOffset, returnValue))
          case GlobalLoadDwordx3 =>
            Right(vmemLoad(true, destination, vectorAddress, scalar, TransferByteCount(12), memoryOffset, returnValue))
          case GlobalLoadDwordx4 =>
            Right(vmemLoad(true, destination, vectorAddress, scalar, TransferByteCount(16), memoryOffset, returnValue))
          case GlobalLoadUbyte =>
            Right(vmemLoad(true, destination, vectorAddress, scalar, TransferByteCount(1), memoryOffset, returnValue))
          case GlobalLoadSbyte =>
            Right(vmemLoadSigned(true, destination, vectorAddress, scalar, NarrowTransferByteCount(1), memoryOffset))
          case GlobalLoadSshort =>
            Right(vmemLoadSigned(true, destination, vectorAddress, scalar, NarrowTransferByteCount(2), memoryOffset))
          case GlobalLoadShortD16 =>
            Right(vmemLoadD16(true, destination, vectorAddress, scalar, memoryOffset, false))
          case GlobalLoadShortD16Hi =>
            Right(vmemLoadD16(true, destination, vectorAddress, scalar, memoryOffset, true))
          case GlobalLoadUshort =>
            Right(vmemLoad(true, destination, vectorAddress, scalar, TransferByteCount(2), memoryOffset, returnValue))
          case GlobalStoreDword =>
            Right(vmemStore(true, vectorAddress, scalar, dataRegister, TransferByteCount(4), memoryOffset))
          case GlobalStoreDwordx2 =>
            Right(vmemStore(true, vectorAddress, scalar, dataRegister, TransferByteCount(8), memoryOffset))
          case GlobalStoreDwordx3 =>
            Right(vmemStore(true, vectorAddress, scalar, dataRegister, TransferByteCount(12), memoryOffset))
          case GlobalStoreDwordx4 =>
            Right(vmemStore(true, vectorAddress, scalar, dataRegister, TransferByteCount(16), memoryOffset))
          case GlobalStoreByte =>
            Right(vmemStore(true, vectorAddress, scalar, dataRegister, TransferByteCount(1), memoryOffset))
          case GlobalStoreShort =>
            Right(vmemStore(true, vectorAddress, scalar, dataRegister, TransferByteCount(2), memoryOffset))
          case GlobalAtomicAdd =>
            Right(Instruction.GlobalAtomicAdd(
              dest = destination,
              address = vectorAddress,
              scalarAddress = scalar,
              src = dataRegister,
              bytes = AtomicByteCount(4),
              offset = memoryOffset,
              returnValue = returnValue
            ))
          case GlobalAtomicAddX2 =>
            Right(Instruction.GlobalAtomicAdd(
              dest = destination,
              address = vectorAddress,
              scalarAddress = scalar,
              src = dataRegister,
              bytes = AtomicByteCount(8),
              offset = memoryOffset,
              returnValue = returnValue
            ))
          case GlobalAtomicInc =>
            Right(Instruction.GlobalAtomicInc(
              dest = destination,
              address = vectorAddress,
              scalarAddress = scalar,
              src = dataRegister,
              bytes = AtomicByteCount(4),
              offset = memoryOffset,
              returnValue = returnValue
            ))
          case GlobalAtomicIncX2 =>
            Right(Instruction.GlobalAtomicInc(
              dest = destination,
              address = vectorAddress,
              scalarAddress = scalar,
              src = dataRegister,
              bytes = AtomicByteCount(8),
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
              AtomicByteCount(4),
              1,
              memoryOffset,
              returnValue
            ))
          case GlobalAtomicCmpswapX2 =>
            Right(vmemCompareSwap(
              true,
              destination,
              vectorAddress,
              scalar,
              dataRegister,
              AtomicByteCount(8),
              2,
              memoryOffset,
              returnValue
            ))
          case GlobalStoreByteD16Hi =>
            Right(vmemStoreD16(true, vectorAddress, scalar, dataRegister, NarrowTransferByteCount(1), memoryOffset))
          case GlobalStoreShortD16Hi =>
            Right(vmemStoreD16(true, vectorAddress, scalar, dataRegister, NarrowTransferByteCount(2), memoryOffset))
          case other => Left(s"$other GLOBAL execution is not implemented")
        }
      } else if (scalarAddress == 0 || Decoding.isAbsentScalarOffset(scalarAddress)) {
        opcodeDefinition("FLAT", Bits.unsignedIntField(word, 18, 7), target).flatMap {
          case FlatLoadUbyte =>
            Right(vmemLoad(false, destination, vectorAddress, None, TransferByteCount(1), memoryOffset, returnValue))
          case FlatLoadSbyte =>
            Right(vmemLoadSigned(false, destination, vectorAddress, None, NarrowTransferByteCount(1), memoryOffset))
          case FlatLoadShortD16 =>
            Right(vmemLoadD16(false, destination, vectorAddress, None, memoryOffset, false))
          case FlatLoadShortD16Hi =>
            Right(vmemLoadD16(false, destination, vectorAddress, None, memoryOffset, true))
          case FlatLoadSshort =>
            Right(vmemLoadSigned(false, destination, vectorAddress, None, NarrowTransferByteCount(2), memoryOffset))
          case FlatLoadUshort =>
            Right(vmemLoad(false, destination, vectorAddress, None, TransferByteCount(2), memoryOffset, returnValue))
          case FlatLoadDword =>
            Right(vmemLoad(false, destination, vectorAddress, None, TransferByteCount(4), memoryOffset, returnValue))
          case FlatLoadDwordx2 =>
            Right(vmemLoad(false, destination, vectorAddress, None, TransferByteCount(8), memoryOffset, returnValue))
          case FlatLoadDwordx3 =>
            Right(vmemLoad(false, destination, vectorAddress, None, TransferByteCount(12), memoryOffset, returnValue))
          case FlatLoadDwordx4 =>
            Right(vmemLoad(false, destination, vectorAddress, None, TransferByteCount(16), memoryOffset, returnValue))
          case FlatStoreShort =>
            Right(vmemStore(false, vectorAddress, None, dataRegister, TransferByteCount(2), memoryOffset))
          case FlatStoreByte =>
            Right(vmemStore(false, vectorAddress, None, dataRegister, TransferByteCount(1), memoryOffset))
          case FlatStoreByteD16Hi =>
            Right(vmemStoreD16(false, vectorAddress, None, dataRegister, NarrowTransferByteCount(1), memoryOffset))
          case FlatStoreShortD16Hi =>
            Right(vmemStoreD16(false, vectorAddress, None, dataRegister, NarrowTransferByteCount(2), memoryOffset))
          case FlatStoreDword =>
            Right(vmemStore(false, vectorAddress, None, dataRegister, TransferByteCount(4), memoryOffset))
          case FlatStoreDwordx2 =>
            Right(vmemStore(false, vectorAddress, None, dataRegister, TransferByteCount(8), memoryOffset))
          case FlatStoreDwordx3 =>
            Right(vmemStore(false, vectorAddress, None, dataRegister, TransferByteCount(12), memoryOffset))
          case FlatStoreDwordx4 =>
            Right(vmemStore(false, vectorAddress, None, dataRegister, TransferByteCount(16), memoryOffset))
          case FlatAtomicCmpswap | FlatAtomicCmpswapB32 =>
            Right(vmemCompareSwap(
              false,
              destination,
              vectorAddress,
              None,
              dataRegister,
              AtomicByteCount(4),
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
              bytes = AtomicByteCount(4),
              offset = memoryOffset,
              returnValue = returnValue
            ))
          case FlatAtomicSwapX2 | FlatAtomicSwapB64 =>
            Right(Instruction.GlobalAtomicSwap(
              dest = destination,
              address = vectorAddress,
              scalarAddress = None,
              src = dataRegister,
              bytes = AtomicByteCount(8),
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
              bytes = AtomicByteCount(4),
              offset = memoryOffset,
              returnValue = returnValue
            ))
          case FlatAtomicCmpswapX2 =>
            Right(vmemCompareSwap(
              false,
              destination,
              vectorAddress,
              None,
              dataRegister,
              AtomicByteCount(8),
              2,
              memoryOffset,
              returnValue
            ))
          case other => Left(s"$other FLAT execution is not implemented")
        }
      } else Left(s"VMEM scalar address s[$scalarAddress:${scalarAddress + 1}] is not implemented")
    } yield Decoded(operation, 8)

  private def decodeMUBUF(
      bytes: Array[Byte],
      offset: Int,
      word: Long,
      target: Target
  ): Either[String, Decoded] =
    for {
      _ <- requireBytes(bytes, offset, 8)
      second = unsignedWord(bytes, offset + 4)
      opcode <- opcodeDefinition("MUBUF", Bits.unsignedIntField(word, 18, 7), target)
      addressingMode = Bits.unsignedField(word, 12, 6)
      _ <- Either.cond(
        addressingMode == 0L || addressingMode == 1L,
        (),
        s"$opcode buffer addressing/modifiers are not implemented"
      )
      address      = Option.when(Bits.unsignedIntField(word, 12, 2) != 0)(Bits.low8Int(second))
      dataRegister = Bits.unsignedIntField(second, 8, 8)
      resource     = Bits.unsignedIntField(second, 16, 5) * 4
      soffset      = Bits.unsignedIntField(second, 24, 8)
      scalarOffset = Option.when(soffset != 0x80)(soffset)
      immediate    = Bits.unsignedIntField(word, 0, 12)
      operation <- opcode match {
        case BufferWbinvl1 | BufferWbinvl1Vol |
            BufferGl0Inv | BufferGl1Inv =>
          Right(Instruction.BufferCacheInvalidate)
        case BufferLoadDword =>
          Right(Instruction.BufferLoad(
            dest = dataRegister,
            address = address,
            resource = resource,
            scalarOffset = scalarOffset,
            bytes = TransferByteCount(4),
            offset = immediate
          ))
        case BufferLoadUbyte =>
          Right(Instruction.BufferLoad(
            dest = dataRegister,
            address = address,
            resource = resource,
            scalarOffset = scalarOffset,
            bytes = TransferByteCount(1),
            offset = immediate
          ))
        case BufferLoadDwordx2 =>
          Right(Instruction.BufferLoad(
            dest = dataRegister,
            address = address,
            resource = resource,
            scalarOffset = scalarOffset,
            bytes = TransferByteCount(8),
            offset = immediate
          ))
        case BufferLoadDwordx4 =>
          Right(Instruction.BufferLoad(
            dest = dataRegister,
            address = address,
            resource = resource,
            scalarOffset = scalarOffset,
            bytes = TransferByteCount(16),
            offset = immediate
          ))
        case BufferLoadShortD16 =>
          Right(Instruction.BufferLoadD16(
            dest = dataRegister,
            address = address,
            resource = resource,
            scalarOffset = scalarOffset,
            bytes = NarrowTransferByteCount(2),
            offset = immediate,
            high = false
          ))
        case BufferLoadShortD16Hi =>
          Right(Instruction.BufferLoadD16(
            dest = dataRegister,
            address = address,
            resource = resource,
            scalarOffset = scalarOffset,
            bytes = NarrowTransferByteCount(2),
            offset = immediate,
            high = true
          ))
        case BufferStoreShort =>
          Right(Instruction.BufferStore(
            address = address,
            resource = resource,
            scalarOffset = scalarOffset,
            src = dataRegister,
            bytes = TransferByteCount(2),
            offset = immediate
          ))
        case BufferStoreByte =>
          Right(Instruction.BufferStore(
            address = address,
            resource = resource,
            scalarOffset = scalarOffset,
            src = dataRegister,
            bytes = TransferByteCount(1),
            offset = immediate
          ))
        case BufferStoreShortD16Hi =>
          Right(Instruction.BufferStoreD16High(
            address = address,
            resource = resource,
            scalarOffset = scalarOffset,
            src = dataRegister,
            bytes = NarrowTransferByteCount(2),
            offset = immediate
          ))
        case BufferStoreDword =>
          Right(Instruction.BufferStore(
            address = address,
            resource = resource,
            scalarOffset = scalarOffset,
            src = dataRegister,
            bytes = TransferByteCount(4),
            offset = immediate
          ))
        case BufferStoreDwordx2 =>
          Right(Instruction.BufferStore(
            address = address,
            resource = resource,
            scalarOffset = scalarOffset,
            src = dataRegister,
            bytes = TransferByteCount(8),
            offset = immediate
          ))
        case BufferStoreDwordx4 =>
          Right(Instruction.BufferStore(
            address = address,
            resource = resource,
            scalarOffset = scalarOffset,
            src = dataRegister,
            bytes = TransferByteCount(16),
            offset = immediate
          ))
        case other => Left(s"$other MUBUF execution is not implemented")
      }
    } yield Decoded(operation, 8)

  private def opcodeDefinition(format: String, opcode: Int, target: Target): Either[String, VendorInstruction] = {
    val definitions = OpcodeIndex.definitions(format, opcode, target)
    // RDNA's FLAT_GLBL encoding is shared by the flat and global mnemonics.
    // Once the decoder has selected the address mode, retain only the
    // corresponding mnemonic family; otherwise an otherwise valid opcode is
    // reported as ambiguous (for example flat_store_dword/global_store_dword).
    val modeDefinitions = format match {
      case "GLOBAL" => definitions.filter(AMDGPUInstruction.GlobalDefinitions.contains)
      case "FLAT"   => definitions.filter(AMDGPUInstruction.FlatDefinitions.contains)
      case _        => definitions
    }
    modeDefinitions match {
      case Vec(name)                                                 => Right(name)
      case Vec() if format == "VOP3" && target.family == Family.GFX9 =>
        Gfx9Vop3Aliases.byOpcode.get(opcode).toRight(s"unknown $format opcode $opcode for ${target.name}")
      case Vec() => Left(s"unknown $format opcode $opcode for ${target.name}")
      case aliases if aliases.toSet == Set(VMulLoI32, VMulLoU32) =>
        Right(VMulLoU32)
      case _ => Left(s"ambiguous $format opcode $opcode for ${target.name}: ${definitions.mkString(", ")}")
    }
  }

  private def scalarSource(encoding: Int, literal: Option[Long]): Either[String, InstructionSource] =
    if (encoding <= 127) Right(InstructionSource.Scalar(encoding))
    // The virtual execution model has no hardware private-segment base. The
    // compiler still materializes the aperture registers while setting up the
    // scratch descriptor; their values are intentionally zero in the
    // interpreter's flat virtual address space.
    else if (encoding >= 235 && encoding <= 239) Right(InstructionSource.Constant(0L))
    else inlineConstant(encoding, literal)

  private def vectorSource(encoding: Int, literal: Option[Long]): Either[String, InstructionSource] =
    if (encoding >= 256) Right(InstructionSource.VectorRegister(encoding - 256))
    else scalarSource(encoding, literal)

  private def inlineConstant(encoding: Int, literal: Option[Long]): Either[String, InstructionSource] =
    if (encoding >= 128 && encoding <= 192) Right(InstructionSource.Constant(encoding - 128L))
    else if (encoding >= 193 && encoding <= 208) Right(InstructionSource.Constant(192L - encoding))
    else if (
      encoding >= Decoding.InlineFloatingFirst
      && encoding < Decoding.InlineFloatingFirst + Decoding.InlineFloatingConstants.size
    )
      Right(
        InstructionSource.Constant(Decoding.InlineFloatingConstants(encoding - Decoding.InlineFloatingFirst))
      )
    else if (encoding == Decoding.LiteralSource)
      literal.map(raw => InstructionSource.Literal(raw)).toRight("missing literal dword")
    else Left(s"source encoding $encoding is not implemented")

  private def subDwordSelection(encoding: Int): Either[String, SubDwordSelection] =
    SubDwordSelection.values.lift(encoding).toRight(s"unknown SDWA selection $encoding")

  private def subDwordDestinationUnused(encoding: Int): Either[String, SubDwordDestinationUnused] =
    SubDwordDestinationUnused.values.lift(encoding).toRight(s"unknown SDWA destination-unused mode $encoding")
  private def requireBytes(bytes: Array[Byte], offset: Int, size: Int): Either[String, Unit] =
    Either.cond(
      ByteRange.contains(bytes.length, offset.toLong, size.toLong),
      (),
      s"truncated $size-byte instruction"
    )

  private def unsignedWord(bytes: Array[Byte], offset: Int): Long =
    LittleEndian.decode(bytes, offset, 4)
}
