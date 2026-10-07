package net.kurobako.gemstone.isa.amdgpu

import EncodingConstants.*
import VMIR.*
import net.kurobako.gemstone.constraint.*

/** Validate complete statically addressed register spans before instructions reach execution. */
private object VMRegisterBounds {
  def validate(instruction: Instruction, waveSize: Int in (32, 64)): Either[NonEmpty[String], Unit] = {
    def span(first: VectorRegisterIndex, words: Int): Boolean       = words > 0 && words <= 256 - first.raw
    def scalarSpan(first: ScalarRegisterIndex, words: Int): Boolean = words > 0 && words <= 128 - first.raw
    def maskDestination(first: ScalarRegisterIndex): Boolean        = scalarSpan(first, waveSize.raw / 32)
    @annotation.tailrec
    def source(value: InstructionSource, words: Int = 2): Boolean = value match {
      case InstructionSource.Scalar(first) if first.raw == Fixed.ExecHi || first.raw == Fixed.VccHi => true
      case InstructionSource.Scalar(first)         => scalarSpan(first, words)
      case InstructionSource.VectorRegister(first) => span(first, words)
      case InstructionSource.HighHalf(half)        => source(half, 1)
      case _                                       => true
    }
    def words(bytes: Int): Int                 = (bytes + 3) / 4
    def address(value: MemoryAddress): Boolean = value match {
      case MemoryAddress.Global(vector, scalar, _) => scalar.fold(span(vector, 2))(scalarSpan(_, 2))
      case MemoryAddress.Flat(vector, _)           => span(vector, 2)
      case MemoryAddress.Buffer(_, resource, _, _) => scalarSpan(resource, 4)
      case _                                       => true
    }
    def comparison(op: VectorComparison, a: InstructionSource, b: InstructionSource): Boolean = op.kind match {
      case VectorComparisonKind.Signed64 | VectorComparisonKind.Unsigned64 | VectorComparisonKind.Float64 =>
        source(a) && source(b)
      case VectorComparisonKind.ClassFloat64 => source(a)
      case _                                 => true
    }
    val valid = instruction match {
      case i: Instruction.SLoad     => scalarSpan(i.dest, i.count.raw) && scalarSpan(i.base, 2)
      case i: Instruction.SMove64   => scalarSpan(i.dest, 2) && source(i.src)
      case i: Instruction.SGetPc    => scalarSpan(i.dest, 2)
      case i: Instruction.SSetPc    => source(i.src)
      case i: Instruction.SSwapPc   => scalarSpan(i.dest, 2) && source(i.src)
      case i: Instruction.SSaveExec => scalarSpan(i.dest, i.width.raw / 32) && source(i.src)
      case i: Instruction.SUnary    => i.operation match {
          case ScalarUnaryOperation.Not64 => scalarSpan(i.dest, 2) && source(i.src)
          case ScalarUnaryOperation.CountBits64 | ScalarUnaryOperation.FirstBitHighI64 | ScalarUnaryOperation.FirstBitHighU64 =>
            source(i.src)
          case _ => true
        }
      case i: Instruction.SBinary64 => scalarSpan(i.dest, 2) && source(i.srcA) && source(i.srcB)
      case i: Instruction.SCSelect  => scalarSpan(i.dest, i.width.raw / 32) && source(i.srcA, i.width.raw / 32) &&
        source(i.srcB, i.width.raw / 32)
      case i: Instruction.SCmp => i.operation match {
          case ScalarCompareOperation.EqualU64 | ScalarCompareOperation.NotEqualU64 => source(i.srcA) && source(i.srcB)
          case _                                                                    => true
        }
      case i: Instruction.VMove64         => span(i.dest, 2) && source(i.src)
      case i: Instruction.VPackedF32      => span(i.dest, 2) && source(i.srcA) && source(i.srcB) && source(i.srcC)
      case i: Instruction.VPackedMove32   => span(i.dest, 2) && source(i.srcA) && source(i.srcB)
      case i: Instruction.VShiftLeftAdd64 => span(i.dest, 2) && source(i.src) && source(i.addend)
      case i: Instruction.VMatrixF32      =>
        val count = if (i.size.raw == 4) 4 else i.size.raw * i.size.raw / 64
        span(i.dest, count) && source(i.srcA, 1) && source(i.srcB, 1) && source(i.srcC, count)
      case i: Instruction.VWaveMatrixF16 =>
        val count = 256 / waveSize.raw
        span(i.dest, count) && span(i.srcA, 8) && span(i.srcB, 8) && source(i.srcC, count)
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
      case i: Instruction.VTrigPreopF64 => span(i.dest, 2) && source(i.src)
      case i: Instruction.VBinary64     =>
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
      case i: Instruction.VCompare                => maskDestination(i.dest) && comparison(i.operation, i.srcA, i.srcB)
      case i: Instruction.VCndMask                => source(i.condition)
      case i: Instruction.VAddCarry               => maskDestination(i.carryDest) && i.carryIn.forall(source(_))
      case i: Instruction.VSubCarry               => maskDestination(i.carryDest) && i.carryIn.forall(source(_))
      case i: Instruction.VSubReverseCarry        => maskDestination(i.carryDest) && i.carryIn.forall(source(_))
      case i: Instruction.VDivScaleF32            => maskDestination(i.conditionDest)
      case i: Instruction.Load                    => address(i.address) && span(i.dest, words(i.bytes.raw))
      case i: Instruction.Store                   => address(i.address) && span(i.src, words(i.bytes.raw))
      case i: Instruction.Atomic                  =>
        address(i.address) && span(i.src, words(i.bytes.raw)) && (!i.returnValue || span(i.dest, words(i.bytes.raw)))
      case i: Instruction.CompareSwap =>
        address(i.address) && span(i.data, 2 * words(i.bytes.raw)) &&
        (!i.returnValue || span(i.dest, words(i.bytes.raw)))
      case i: Instruction.LoadPair        => span(i.dest, 2 * words(i.bytes.raw))
      case i: Instruction.StorePair       => span(i.srcA, words(i.bytes.raw)) && span(i.srcB, words(i.bytes.raw))
      case i: Instruction.VDPP            => validate(i.operation, waveSize).isRight
      case i: Instruction.VClampResult    => validate(i.operation, waveSize).isRight
      case i: Instruction.VHalfResult     => validate(i.operation, waveSize).isRight
      case i: Instruction.VOutputModified =>
        validate(i.ignored, waveSize).isRight && validate(i.applied, waveSize).isRight
      case _ => true
    }
    Either.cond(valid, (), "instruction register span exceeds the register bank")
  }
}
