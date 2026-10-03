package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import net.kurobako.gemstone.constraint.*
import EncodingConstants.Fixed

/** Validate complete statically addressed register spans before instructions reach execution. */
private object VMRegisterBounds {
  def validate(instruction: Instruction, waveSize: Int in (32, 64) = 64): Either[NonEmpty[String], Unit] = {
    def span(first: VectorRegisterIndex, words: Int): Boolean           = words > 0 && words <= 256 - first.raw
    def scalarSpan(first: ScalarRegisterIndex, words: Int): Boolean = words > 0 && words <= 128 - first.raw
    def maskDestination(first: ScalarRegisterIndex): Boolean = scalarSpan(first, waveSize.raw / 32)
    def source(value: InstructionSource, words: Int = 2): Boolean = value match {
      case InstructionSource.Scalar(first) if first.raw == Fixed.ExecHi || first.raw == Fixed.VccHi => true
      case InstructionSource.Scalar(first)         => scalarSpan(first, words)
      case InstructionSource.VectorRegister(first) => span(first, words)
      case _                                       => true
    }
    def words(bytes: Int): Int                                                 = (bytes + 3) / 4
    def address(vector: VectorRegisterIndex, scalar: Option[ScalarRegisterIndex]): Boolean =
      scalar.fold(span(vector, 2))(scalarSpan(_, 2))
    def comparison(op: VectorComparison, a: InstructionSource, b: InstructionSource): Boolean = op.kind match {
      case VectorComparisonKind.Signed64 | VectorComparisonKind.Unsigned64 | VectorComparisonKind.Float64 =>
        source(a) && source(b)
      case VectorComparisonKind.ClassFloat64 => source(a)
      case _                                 => true
    }
    val valid = instruction match {
      case i: Instruction.SLoad          => scalarSpan(i.dest, i.count.raw) && scalarSpan(i.base, 2)
      case i: Instruction.SMove64        => scalarSpan(i.dest, 2) && source(i.src)
      case i: Instruction.SGetPc         => scalarSpan(i.dest, 2)
      case i: Instruction.SSetPc         => source(i.src)
      case i: Instruction.SSwapPc        => scalarSpan(i.dest, 2) && source(i.src)
      case i: Instruction.SAndSaveExec   => scalarSpan(i.dest, i.width.raw / 32) && source(i.src)
      case i: Instruction.SAndn2SaveExec => scalarSpan(i.dest, i.width.raw / 32) && source(i.src)
      case i: Instruction.SOrSaveExec    => scalarSpan(i.dest, i.width.raw / 32) && source(i.src)
      case i: Instruction.SXorSaveExec   => scalarSpan(i.dest, i.width.raw / 32) && source(i.src)
      case i: Instruction.SUnary         => i.operation match {
          case ScalarUnaryOperation.Not64       => scalarSpan(i.dest, 2) && source(i.src)
          case ScalarUnaryOperation.CountBits64 => source(i.src)
          case _                                => true
        }
      case i: Instruction.SBinary64 => scalarSpan(i.dest, 2) && source(i.srcA) && source(i.srcB)
      case i: Instruction.SCSelect  => scalarSpan(i.dest, i.width.raw / 32) && source(i.srcA, i.width.raw / 32) &&
        source(i.srcB, i.width.raw / 32)
      case i: Instruction.SCmp => i.operation match {
          case ScalarCompareOperation.EqualU64 | ScalarCompareOperation.NotEqualU64 => source(i.srcA) && source(i.srcB)
          case _                                                                    => true
        }
      case i: Instruction.VConvert => i.operation match {
          case VectorConvertOperation.F64FromI32 | VectorConvertOperation.F64FromU32 |
              VectorConvertOperation.F64FromF32 => span(i.dest, 2)
          case VectorConvertOperation.FrexpExponentF64 | VectorConvertOperation.I32FromF64 |
              VectorConvertOperation.U32FromF64 | VectorConvertOperation.F32FromF64 => source(i.src)
          case VectorConvertOperation.FrexpMantissaF64 | VectorConvertOperation.ReciprocalF64 |
              VectorConvertOperation.TruncateF64 | VectorConvertOperation.FloorF64 |
              VectorConvertOperation.FractionF64 | VectorConvertOperation.RoundNearestEvenF64 |
              VectorConvertOperation.ReciprocalSquareRootF64 => span(i.dest, 2) && source(i.src)
          case _ => true
        }
      case i: Instruction.VCvtF64F32Modified => span(i.dest, 2)
      case i: Instruction.VUnaryF64Modified  => span(i.dest, 2) && source(i.src)
      case i: Instruction.VTrigPreopF64      => span(i.dest, 2) && source(i.src)
      case i: Instruction.VBinary64Modified  =>
        span(i.dest, 2) && source(i.srcA) &&
          source(i.srcB, if (i.operation == VectorBinary64Operation.LoadExponent) 1 else 2)
      case i: Instruction.VMadF64      => span(i.dest, 2) && source(i.srcA) && source(i.srcB) && source(i.srcC)
      case i: Instruction.VDivScaleF64 =>
        span(i.dest, 2) && maskDestination(i.conditionDest) && source(i.srcA) && source(i.srcB) && source(i.srcC)
      case i: Instruction.VDivFmasF64  => span(i.dest, 2) && source(i.srcA) && source(i.srcB) && source(i.srcC)
      case i: Instruction.VDivFixupF64 => span(i.dest, 2) && source(i.quotient) && source(i.denominator) &&
        source(i.numerator)
      case i: Instruction.VMadU64U32              => span(i.dest, 2) && maskDestination(i.carryDest) && source(i.srcC)
      case i: Instruction.VMadI64I32              => span(i.dest, 2) && maskDestination(i.carryDest) && source(i.srcC)
      case i: Instruction.VShiftLeft64            => span(i.dest, 2) && source(i.src)
      case i: Instruction.VShiftRight64           => span(i.dest, 2) && source(i.src)
      case i: Instruction.VShiftRightArithmetic64 => span(i.dest, 2) && source(i.src)
      case i: Instruction.VCompare                => comparison(i.operation, i.srcA, i.srcB)
      case i: Instruction.VCompareTo              => (i.operation.updateExec || maskDestination(i.dest)) && comparison(i.operation, i.srcA, i.srcB)
      case i: Instruction.VCompareSubDword        => i.destination.forall(maskDestination)
      case i: Instruction.VCndMask                => source(i.condition)
      case i: Instruction.VAddCarry               => maskDestination(i.carryDest) && i.carryIn.forall(source(_))
      case i: Instruction.VSubCarry               => maskDestination(i.carryDest) && i.carryIn.forall(source(_))
      case i: Instruction.VSubReverseCarry        => maskDestination(i.carryDest) && i.carryIn.forall(source(_))
      case i: Instruction.VDivScaleF32            => maskDestination(i.conditionDest)
      case i: Instruction.GlobalLoad         => address(i.address, i.scalarAddress) && span(i.dest, words(i.bytes.raw))
      case i: Instruction.GlobalLoadSigned   => address(i.address, i.scalarAddress)
      case i: Instruction.GlobalLoadD16      => address(i.address, i.scalarAddress)
      case i: Instruction.GlobalStore        => address(i.address, i.scalarAddress) && span(i.src, words(i.bytes.raw))
      case i: Instruction.GlobalStoreD16High => address(i.address, i.scalarAddress)
      case i: Instruction.GlobalAtomicAdd    =>
        address(i.address, i.scalarAddress) && span(i.src, words(i.bytes.raw)) &&
        (!i.returnValue || span(i.dest, words(i.bytes.raw)))
      case i: Instruction.GlobalAtomicSwap =>
        address(i.address, i.scalarAddress) && span(i.src, words(i.bytes.raw)) &&
        (!i.returnValue || span(i.dest, words(i.bytes.raw)))
      case i: Instruction.GlobalAtomicBinary =>
        address(i.address, i.scalarAddress) && span(i.src, words(i.bytes.raw)) &&
        (!i.returnValue || span(i.dest, words(i.bytes.raw)))
      case i: Instruction.GlobalAtomicInc =>
        address(i.address, i.scalarAddress) && span(i.src, words(i.bytes.raw)) &&
        (!i.returnValue || span(i.dest, words(i.bytes.raw)))
      case i: Instruction.GlobalAtomicCompareSwap =>
        address(i.address, i.scalarAddress) && span(i.compareSrc, words(i.bytes.raw)) &&
        span(i.swapSrc, words(i.bytes.raw)) &&
        (!i.returnValue || span(i.dest, words(i.bytes.raw)))
      case i: Instruction.FlatLoad           => span(i.address, 2) && span(i.dest, words(i.bytes.raw))
      case i: Instruction.FlatLoadSigned     => span(i.address, 2)
      case i: Instruction.VFlatLoadD16       => span(i.address, 2)
      case i: Instruction.FlatStore          => span(i.address, 2) && span(i.src, words(i.bytes.raw))
      case i: Instruction.FlatStoreD16High   => span(i.address, 2)
      case i: Instruction.BufferLoad         => scalarSpan(i.resource, 4) && span(i.dest, words(i.bytes.raw))
      case i: Instruction.BufferLoadD16      => scalarSpan(i.resource, 4)
      case i: Instruction.BufferStore        => scalarSpan(i.resource, 4) && span(i.src, words(i.bytes.raw))
      case i: Instruction.BufferStoreD16High => scalarSpan(i.resource, 4)
      case i: Instruction.DSRead             => span(i.dest, words(i.bytes.raw) * (if (i.offsetB.isDefined) 2 else 1))
      case i: Instruction.DSSignedRead       => span(i.dest, if (i.offsetB.isDefined) 2 else 1)
      case i: Instruction.DSAtomic           => span(i.src, words(i.bytes.raw)) &&
        (!i.returnValue || span(i.dest, words(i.bytes.raw)))
      case i: Instruction.DSWrite => span(i.srcA, words(i.bytes.raw)) && i.srcB.forall(span(_, words(i.bytes.raw)))
      case _                      => true
    }
    Either.cond(valid, (), "instruction register span exceeds the register bank")
  }
}
