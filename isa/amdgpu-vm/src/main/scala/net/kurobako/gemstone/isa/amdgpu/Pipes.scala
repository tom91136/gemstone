package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import net.kurobako.gemstone.profile.*
import scala.annotation.tailrec

private object Pipes {

  def of(instruction: Instruction, trans: Boolean): Option[Pipe] = {
    val pipe = arithmetic(instruction)
    if (trans && pipe.exists(_ != Pipe.Fp64)) Some(Pipe.Mufu) else pipe
  }

  @tailrec
  private def arithmetic(instruction: Instruction): Option[Pipe] = instruction match {
    case instruction: Instruction.VDual   => dual(instruction.left.operation).orElse(dual(instruction.right.operation))
    case instruction: Instruction.VBinary => Some(binary(instruction.operation))
    case instruction: Instruction.VBinarySubDword => Some(subDword(instruction.operation))
    case instruction: Instruction.VBinary64       => Some(binary64(instruction.operation))
    case instruction: Instruction.VPackedBinary   => Some(packedBinary(instruction.operation))
    case instruction: Instruction.VPackedTernary  => Some(packedTernary(instruction.operation))
    case instruction: Instruction.VTernary        => Some(ternary(instruction.operation))
    case instruction: Instruction.VMedia          => Some(media(instruction.operation))
    case instruction: Instruction.VConvert        => Some(convert(instruction.operation))
    case instruction: Instruction.VDot            => Some(dot(instruction.operation))
    case instruction: Instruction.VCompare        => Some(compare(instruction.operation))
    case instruction: Instruction.VOutputModified => arithmetic(instruction.applied)
    case instruction: Instruction.VHalfResult     => arithmetic(instruction.operation)
    case instruction: Instruction.VClampResult    => arithmetic(instruction.operation)
    case instruction: Instruction.VSubDwordResult => arithmetic(instruction.operation)
    case instruction: Instruction.VDPP            => arithmetic(instruction.operation)
    case instruction: Instruction.WithStatus      => arithmetic(instruction.access)
    case Instruction.VMatrix(_, _, _, _, _, _)    => Some(Pipe.Tensor)
    case _: (Instruction.VPackedF32 | Instruction.VMadF32 | Instruction.VDot2F32F16 | Instruction.VPackB32F16 |
          Instruction.VFmaMixF16 | Instruction.VFmaMixF32 | Instruction.VMadU64U32 | Instruction.VMadI64I32 |
          Instruction.VDivScaleF32 | Instruction.VDivFixupF32 | Instruction.VConvertNarrow |
          Instruction.VConvertPackedHalf | Instruction.VScaledConvert | Instruction.VConvertStochasticHalf) =>
      Some(Pipe.Fma)
    case _: (Instruction.VTrigPreopF64 | Instruction.VMadF64 | Instruction.VDivScaleF64 | Instruction.VDivFmasF64 |
          Instruction.VDivFixupF64) => Some(Pipe.Fp64)
    case _: (Instruction.VBitOp3 | Instruction.VNot | Instruction.VBitReverse | Instruction.VBitFind |
          Instruction.VCndMask | Instruction.VMaskBitCount | Instruction.VBitCount | Instruction.VDot4U32U8 |
          Instruction.VQuadSad |
          Instruction.VAddCarry | Instruction.VSubCarry | Instruction.VSubReverseCarry | Instruction.VShiftLeft64 |
          Instruction.VShiftLeftAdd64 | Instruction.VShiftRight64 | Instruction.VShiftRightArithmetic64) =>
      Some(Pipe.Alu)
    case Instruction.SSetReg | Instruction.SGprIndexOff | Instruction.CacheInvalidate |
        Instruction.CacheWritebackInvalidate | Instruction.Hostcall | Instruction.Barrier |
        Instruction.BarrierLeave | Instruction.Wait | Instruction.Sleep | Instruction.End |
        _: (Instruction.SLoad | Instruction.SMove | Instruction.SWriteExec | Instruction.SMoveRelative |
          Instruction.SMultiplyImmediate | Instruction.SMove64 | Instruction.SSetMode | Instruction.SGetMode |
          Instruction.SGetPc | Instruction.SReadClock | Instruction.SReadWaveId | Instruction.SSetPc |
          Instruction.SSwapPc | Instruction.SSaveExec | Instruction.SUnary | Instruction.SReciprocalF32 |
          Instruction.SFmaF32 | Instruction.SFmaF16 | Instruction.SFloat | Instruction.SFloatUnary |
          Instruction.SBinary | Instruction.SMinMax | Instruction.SBinary64 | Instruction.SGprIndex |
          Instruction.SCSelect | Instruction.SAddImmediate | Instruction.SFloatCompare | Instruction.SCmp |
          Instruction.SStore | Instruction.SAtomic | Instruction.SCompareSwap | Instruction.SBufferLoad |
          Instruction.VMove | Instruction.VMove64 | Instruction.VPermuteLanes |
          Instruction.VPermlaneSwap | Instruction.VAccumulatorRead | Instruction.VAccumulatorWrite |
          Instruction.VAccumulatorMove | Instruction.VPackedMove32 | Instruction.VMoveRelative |
          Instruction.VMoveRelativeDestination | Instruction.VSwapRelative | Instruction.VSwap |
          Instruction.VSwapHalves | Instruction.VReadFirstLane | Instruction.VReadLane | Instruction.VWriteLane |
          Instruction.Load | Instruction.Store | Instruction.Atomic | Instruction.LocalSource2 |
          Instruction.CompareSwap | Instruction.AtomicExtended | Instruction.LocalCounter |
          Instruction.ExchangePair | Instruction.GlobalWaveSync | Instruction.LoadTranspose | Instruction.LoadPair |
          Instruction.StorePair | Instruction.FormatLoad | Instruction.FormatStore | Instruction.LoadLocal |
          Instruction.StoreLocal | Instruction.Image | Instruction.DSPermute | Instruction.DSBPermute |
          Instruction.DSSwizzle | Instruction.BranchRelative | Instruction.SubvectorLoopRelative |
          Instruction.SubvectorLoop | Instruction.CallRelative | Instruction.Call | Instruction.Halt |
          Instruction.Branch | Instruction.BarrierSignal | Instruction.BarrierWait | Instruction.BarrierInit |
          Instruction.BarrierJoin | Instruction.WaitCounts | Instruction.Trap | Instruction.ForkIndirect |
          Instruction.ForkRelative | Instruction.Fork | Instruction.Join | Instruction.SetVectorSkip) => None
  }

  private def dual(operation: DualVectorOperation): Option[Pipe] = {
    import DualVectorOperation.*
    operation match {
      case Move => None
      case AddF32 | SubtractF32 | ReverseSubtractF32 | MultiplyF32 | FmaF32 | MinimumF32 | MaximumF32 |
          MultiplyLegacyF32 | DotHalvesF32 | DotAlignedHalvesF32 | DotBFloat16F32 | DotAlignedBFloat16F32 =>
        Some(Pipe.Fma)
      case ConditionalMask | AddU32 | ShiftLeft32 | And32 | SubtractU32 | MinimumI32 | MaximumI32 | ShiftRight32 |
          ShiftRightArithmetic32 | BitOperation2 =>
        Some(Pipe.Alu)
      case AddF64 | MultiplyF64 | FmaF64 | MinimumF64 | MaximumF64 => Some(Pipe.Fp64)
    }
  }

  private def binary(operation: VectorBinaryOperation): Pipe = {
    import VectorBinaryOperation.*
    operation match {
      case AddU32 | ShiftRightArithmeticI32 | ShiftRightArithmeticI16 | ShiftLeft32 | ShiftRight32 | ShiftLeft16 |
          ShiftRight16 | AddU16 | And32 | Or32 | Xor32 | SubtractU16 | SubtractReverseU16 | SubtractU32 |
          SubtractReverseU32 | MinimumU32 | MaximumU32 | MinimumI32 | MinimumI16 | MinimumU16 | MaximumI32 |
          MaximumI16 | MaximumU16 | Xnor32 | AddCarryU32 | AddWithCarryU32 | SubtractCarryU32 |
          SubtractWithBorrowU32 | AddI32 | SubtractI32 | AddI16 | SubtractI16 =>
        Pipe.Alu
      case MultiplyLowU32 | MultiplyHighU32 | MultiplyHighI32 | SubtractReverseF32 | MultiplyU24 | MultiplyHighU24 |
          MultiplyI24 | MultiplyHighI24 | MultiplyLowU16 | MinimumF32 | MinimumF16 | MaximumF32 | MaximumF16 |
          LoadExponentF32 | AddF32 | MultiplyF32 | MultiplyAccumulateF32 | AddF16 | MultiplyF16 |
          FusedMultiplyAccumulateF16 | SubtractF32 | SubtractF16 | SubtractReverseF16 | LoadExponentF16 |
          PackRoundTowardZeroF16 | MultiplyLegacyF32 | MinimumPropagateF32 | MaximumPropagateF32 |
          MinimumPropagateF16 | MaximumPropagateF16 | MinimumLegacyF32 | MaximumLegacyF32 =>
        Pipe.Fma
    }
  }

  private def subDword(operation: VectorSubDwordOperation): Pipe = {
    import VectorSubDwordOperation.*
    operation match {
      case ConditionalMask32 | AddU32 | AddU16 | AddCarryU32 | AddWithCarryU32 | And32 | Or32 | Xor32 |
          ShiftLeft16 | ShiftLeft32 | ShiftRight16 | ShiftRight32 | MaximumI16 | MinimumI16 | MinimumU16 |
          SubtractU16 | SubtractReverseU16 | SubtractU32 | SubtractCarryU32 | SubtractWithBorrowU32 |
          SubtractReverseU32 | SubtractReverseCarryU32 | SubtractReverseWithBorrowU32 =>
        Pipe.Alu
      case AddF16 | MaximumF16 | MinimumF16 | MultiplyLowU16 | MultiplyF16 | MultiplyU24 | MultiplyI24 => Pipe.Fma
    }
  }

  private def binary64(operation: VectorBinary64Operation): Pipe = {
    import VectorBinary64Operation.*
    operation match {
      case LoadExponent | Add | Multiply | Maximum | Minimum | MaximumPropagate | MinimumPropagate => Pipe.Fp64
      case AddU64 | SubtractU64 | MaximumI64 | MaximumU64 | MinimumI64 | MinimumU64                => Pipe.Alu
      case MultiplyLowU64                                                                          => Pipe.Fma
    }
  }

  private def packedBinary(operation: VectorPackedBinaryOperation): Pipe = {
    import VectorPackedBinaryOperation.*
    operation match {
      case AddU16 | AddI16 | SubtractI16 | SubtractU16 | MaximumI16 | MaximumU16 | MinimumI16 | MinimumU16 |
          ShiftLeft16 | ShiftRight16 | ShiftRightArithmetic16 =>
        Pipe.Alu
      case AddF16 | MultiplyLowU16 | MultiplyF16 | MaximumF16 | MinimumF16 | MaximumPropagateF16 |
          MinimumPropagateF16 =>
        Pipe.Fma
    }
  }

  private def packedTernary(operation: PackedTernaryOperation): Pipe = {
    import PackedTernaryOperation.*
    operation match {
      case Maximum3I16 | Maximum3U16 | Minimum3I16 | Minimum3U16 | AddMaximumI16 | AddMaximumU16 | AddMinimumI16 |
          AddMinimumU16 =>
        Pipe.Alu
      case FusedMultiplyAddF16 | MultiplyAddU16 | MultiplyAddI16 | Maximum3PropagateF16 | Minimum3PropagateF16 |
          Maximum3F16 | Minimum3F16 =>
        Pipe.Fma
    }
  }

  private def ternary(operation: VectorTernaryOperation): Pipe = {
    import VectorTernaryOperation.*
    operation match {
      case Add3U32 | Or3B32 | Xor3B32 | Maximum3I32 | Maximum3U32 | Minimum3I32 | Minimum3U32 | Median3I16 |
          Median3I32 | Median3U16 | Median3U32 | Maximum3I16 | Maximum3U16 | Minimum3I16 | Minimum3U16 | AlignBit32 |
          AlignByte32 | BitFieldInsert32 | AndOr32 | BitFieldExtractI32 | BitFieldExtractU32 | XorAddU32 |
          AddShiftLeftU32 | ShiftLeftAddU32 | ShiftLeftOr32 | PermuteBytes32 | SumAbsoluteDifferenceU8 |
          SumAbsoluteDifferenceHighU8 | SumAbsoluteDifferenceU16 | SumAbsoluteDifferenceU32 |
          MaskedSumAbsoluteDifferenceU8 | MaximumMinimumI32 | MinimumMaximumI32 | MaximumMinimumU32 |
          MinimumMaximumU32 | AddMaximumI32 | AddMaximumU32 | AddMinimumI32 | AddMinimumU32 | ShiftRightPackI8 |
          ShiftRightPackU8 =>
        Pipe.Alu
      case Minimum3F32 | Maximum3F32 | Median3F32 | Maximum3F16 | Minimum3F16 | Median3F16 | MaximumMinimumF32 |
          MinimumMaximumF32 | MaximumMinimumF16 | MinimumMaximumF16 | MultiplyAddU16 | FusedMultiplyAddF16 |
          DivisionFusedMultiplyAddF32 | MultiplyAddLegacyU16 | MultiplyAddU24 | MultiplyAddI24 | MultiplyAddU32U16 |
          MultiplyAddI32I16 | MultiplyAddI16 | MultiplyAddF16 | DivisionFixupF16 | Minimum3PropagateF32 |
          Maximum3PropagateF32 | Minimum3PropagateF16 | Maximum3PropagateF16 | MaximumMinimumPropagateF32 |
          MinimumMaximumPropagateF32 | MaximumMinimumPropagateF16 | MinimumMaximumPropagateF16 | MultiplyAddU32 =>
        Pipe.Fma
    }
  }

  private def media(operation: VectorMediaOperation): Pipe = {
    import VectorMediaOperation.*
    operation match {
      case LerpU8 | PackI16I32 | PackU16U32 | PackI16F32 | PackU16F32 | BitFieldMask32 => Pipe.Alu
      case CubeIdF32 | CubeScF32 | CubeTcF32 | CubeMaF32 | PackByteF32 | PackNormI16F32 | PackNormU16F32 |
          PackNormI16F16 | PackNormU16F16 | PackF16F32 | PackBF16F32 =>
        Pipe.Fma
    }
  }

  private def dot(operation: DotOperation): Pipe = {
    import DotOperation.*
    operation match {
      case SignedBytes | UnsignedBytes | SignedShorts | UnsignedShorts | SignedNibbles | UnsignedNibbles |
          MixedBytes(_, _) | MixedNibbles(_, _) =>
        Pipe.Alu
      case Halves | AlignedHalves | AlignedBFloat16Halves | AlignedHalvesToHalf | AlignedBFloat16HalvesToBFloat16 |
          BFloat16Halves | HalvesToHalf | BFloat16HalvesToBFloat16 | EightBitFloats(_, _) =>
        Pipe.Fma
    }
  }

  private def convert(operation: VectorConvertOperation): Pipe = {
    import VectorConvertOperation.*
    operation match {
      case FrexpExponentF64 | FrexpMantissaF64 | ReciprocalF64 | TruncateF64 | FloorF64 | FractionF64 |
          RoundNearestEvenF64 | ReciprocalSquareRootF64 | I32FromF64 | U32FromF64 | F32FromF64 | F64FromI32 |
          F64FromU32 | F64FromF32 | CeilF64 | SquareRootF64 | ReciprocalClampF64 | ReciprocalSquareRootClampF64 =>
        Pipe.Fp64
      case FrexpExponentF32 | FrexpExponentF16 | I32FromF32 | U32FromF32 | F32FromUByte0 | F32FromUByte1 |
          F32FromUByte2 | F32FromUByte3 | F16FromI16 | F16FromU16 | U16FromF16 | I16FromF16 | F32FromI32 |
          F32FromU32 | FloorI32FromF32 | NearestI32FromF32 | OffsetF32FromI4 | SignedNormFromF16 |
          UnsignedNormFromF16 | SaturatePackU8FromI16 | SaturatePackI4FromI8 | SaturatePackU4FromU8 |
          I32FromI16 | U32FromU16 | PseudoRandom =>
        Pipe.Alu
      case ReciprocalF32 | TruncateF32 | FloorF32 | FractionF32 | RoundNearestEvenF32 | ReciprocalSquareRootF32 |
          SquareRootF32 | ExponentBase2F32 | SineF32 | CosineF32 | LogarithmBase2F32 | CeilF32 | CeilF16 | FloorF16 |
          TruncateF16 | RoundNearestEvenF16 | FractionF16 | SquareRootF16 | ReciprocalF16 | ReciprocalSquareRootF16 |
          ExponentBase2F16 | LogarithmBase2F16 | SineF16 | CosineF16 | FrexpMantissaF16 | FrexpMantissaF32 |
          F32FromF16 | F16FromF32 | F32FromBF16 | ReciprocalClampF32 | ReciprocalSquareRootClampF32 |
          LogarithmClampF32 | ReciprocalLegacyF32 | ReciprocalSquareRootLegacyF32 | SineBoundedF32 |
          CosineBoundedF32 | SineBoundedF16 | CosineBoundedF16 | F32FromE4M3 | F32FromE5M2 | F32FromE4M3Fnuz |
          F32FromE5M2Fnuz | PairF32FromE4M3 | PairF32FromE5M2 | PairF32FromE4M3Fnuz | PairF32FromE5M2Fnuz |
          F16FromE4M3 | F16FromE5M2 | PackedF16FromE4M3 | PackedF16FromE5M2 =>
        Pipe.Fma
    }
  }

  private def compare(comparison: VectorComparison): Pipe = {
    import VectorComparisonKind.*
    comparison.kind match {
      case Signed16 | Signed32 | Signed64 | Unsigned16 | Unsigned32 | Unsigned64 => Pipe.Alu
      case Float16 | Float32 | ClassFloat16 | ClassFloat32                       => Pipe.Fma
      case Float64 | ClassFloat64                                                => Pipe.Fp64
    }
  }
}
