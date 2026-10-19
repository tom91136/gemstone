package net.kurobako.gemstone.isa.amdgpu

import EncodingConstants.*
import VMIR.*
import net.kurobako.gemstone.constraint.*

/** Validate complete statically addressed register spans before instructions reach execution. */
private object VMRegisterBounds {
  def validate(instruction: Instruction, waveSize: Int in (32, 64)): Either[NonEmpty[String], Unit] = {
    def span(first: VectorRegisterIndex, words: Int): Boolean       = words > 0 && words <= 256 - first
    def scalarSpan(first: ScalarRegisterIndex, words: Int): Boolean = words > 0 && words <= 128 - first
    def maskDestination(first: ScalarRegisterIndex): Boolean        = scalarSpan(first, waveSize / 32)
    @annotation.tailrec
    def source(value: InstructionSource, words: Int = 2): Boolean = value match {
      case InstructionSource.Scalar(first) if first.raw == Fixed.ExecHi || first.raw == Fixed.VccHi => true
      case InstructionSource.Scalar(first)         => scalarSpan(first, words)
      case InstructionSource.VectorRegister(first) => span(first, words)
      case InstructionSource.HighHalf(half)        => source(half, 1)
      case InstructionSource.Selected(part, _, _)  => source(part, 1)
      case _                                       => true
    }
    def pairs(half: DualVector): Boolean =
      !half.operation.wide || span(half.dest, 2) && source(half.left) && source(half.right) && source(half.addend)
    def words(bytes: Int): Int                 = (bytes + 3) / 4
    def address(value: MemoryAddress): Boolean = value match {
      case MemoryAddress.Global(vector, scalar, _)   => scalar.fold(span(vector, 2))(scalarSpan(_, 2))
      case MemoryAddress.Flat(vector, _, scalar)     => scalar.fold(span(vector, 2))(scalarSpan(_, 2))
      case MemoryAddress.Buffer(resource = resource) => scalarSpan(resource, 4)
      case MemoryAddress.Texel(image)                => scalarSpan(image.resource, 8)
      case _                                         => true
    }
    def comparison(op: VectorComparison, a: InstructionSource, b: InstructionSource): Boolean = op.kind match {
      case VectorComparisonKind.Signed64 | VectorComparisonKind.Unsigned64 | VectorComparisonKind.Float64 =>
        source(a) && source(b)
      case VectorComparisonKind.ClassFloat64 => source(a)
      case _                                 => true
    }
    val valid = instruction match {
      case i: Instruction.SLoad      => scalarSpan(i.dest, i.count) && scalarSpan(i.base, 2)
      case i: Instruction.SMove64    => scalarSpan(i.dest, 2) && source(i.src)
      case i: Instruction.SGetPc     => scalarSpan(i.dest, 2)
      case i: Instruction.SReadClock =>
        scalarSpan(i.dest, if (i.clock == Clock.ShaderCycles) 1 else 2)
      case i: Instruction.SSetPc     => source(i.src)
      case i: Instruction.SSwapPc    => scalarSpan(i.dest, 2) && source(i.src)
      case i: Instruction.SSaveExec  => scalarSpan(i.dest, i.width / 32) && source(i.src)
      case i: Instruction.SWriteExec => scalarSpan(i.dest, i.width / 32) && source(i.src)
      case i: Instruction.SUnary     => i.operation match {
          case ScalarUnaryOperation.Not64 => scalarSpan(i.dest, 2) && source(i.src)
          case ScalarUnaryOperation.CountBits64 | ScalarUnaryOperation.FirstBitHighI64 | ScalarUnaryOperation.FirstBitHighU64 =>
            source(i.src)
          case _ => true
        }
      case i: Instruction.SBinary64 => scalarSpan(i.dest, 2) && source(i.srcA) && source(i.srcB)
      case i: Instruction.SCSelect  => scalarSpan(i.dest, i.width / 32) && source(i.srcA, i.width / 32) &&
        source(i.srcB, i.width / 32)
      case i: Instruction.SCmp => i.operation match {
          case ScalarCompareOperation.EqualU64 | ScalarCompareOperation.NotEqualU64 => source(i.srcA) && source(i.srcB)
          case _                                                                    => true
        }
      case i: Instruction.VMove64       => span(i.dest, 2) && source(i.src)
      case i: Instruction.LoadTranspose =>
        span(i.dest, (i.layout.bits * i.layout.elements + 31) / 32) && address(i.address)
      case i: Instruction.VScaledConvert =>
        val dwords = i.conversion.dwords
        span(i.dest, dwords.dest) && source(i.srcA, math.max(dwords.a, 1)) && source(i.srcB, math.max(dwords.b, 1)) &&
        source(i.srcC, math.max(dwords.c, 1))
      case i: Instruction.VPackedF32      => span(i.dest, 2) && source(i.srcA) && source(i.srcB) && source(i.srcC)
      case i: Instruction.VPackedMove32   => span(i.dest, 2) && source(i.srcA) && source(i.srcB)
      case i: Instruction.VShiftLeftAdd64 => span(i.dest, 2) && source(i.src) && source(i.addend)
      case i: Instruction.VMatrix         =>
        val shape = i.shape
        span(i.dest, shape.outputRegisters) && source(i.srcA, shape.aRegisters) &&
        source(i.srcB, shape.bRegisters) && source(i.srcC, if (shape.sparse) 1 else shape.outputRegisters) &&
        i.controls.scales.forall(scales => source(scales.a, 1) && source(scales.b, 1))
      case i: Instruction.VConvert => i.operation match {
          case VectorConvertOperation.F64FromI32 | VectorConvertOperation.F64FromU32 |
              VectorConvertOperation.F64FromF32 => span(i.dest, 2)
          case VectorConvertOperation.PairF32FromE4M3 | VectorConvertOperation.PairF32FromE5M2 |
              VectorConvertOperation.PairF32FromE4M3Fnuz | VectorConvertOperation.PairF32FromE5M2Fnuz =>
            span(i.dest, 2)
          case VectorConvertOperation.FrexpExponentF64 | VectorConvertOperation.I32FromF64 |
              VectorConvertOperation.U32FromF64 | VectorConvertOperation.F32FromF64 => source(i.src)
          case VectorConvertOperation.FrexpMantissaF64 | VectorConvertOperation.ReciprocalF64 |
              VectorConvertOperation.TruncateF64 | VectorConvertOperation.FloorF64 |
              VectorConvertOperation.FractionF64 | VectorConvertOperation.RoundNearestEvenF64 |
              VectorConvertOperation.ReciprocalSquareRootF64 | VectorConvertOperation.CeilF64 |
              VectorConvertOperation.SquareRootF64 | VectorConvertOperation.ReciprocalClampF64 |
              VectorConvertOperation.ReciprocalSquareRootClampF64 => span(i.dest, 2) && source(i.src)
          case _ => true
        }
      case i: Instruction.VTrigPreopF64 => span(i.dest, 2) && source(i.src)
      case i: Instruction.Call          => scalarSpan(i.dest, 2)
      case i: Instruction.CallRelative  => scalarSpan(i.dest, 2)
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
      case i: Instruction.Load                    => address(i.address) && span(i.dest, words(i.bytes))
      case i: Instruction.Store                   => address(i.address) && span(i.src, words(i.bytes))
      case i: Instruction.Atomic                  =>
        address(i.address) && span(i.src, words(i.bytes)) && (!i.returnValue || span(i.dest, words(i.bytes)))
      case i: Instruction.CompareSwap =>
        address(i.address) && span(i.data, words(i.bytes)) && span(i.comparand, words(i.bytes)) &&
        (!i.returnValue || span(i.dest, words(i.bytes)))
      case i: Instruction.FormatLoad =>
        address(i.address) && span(i.dest, VMResourceDecoder.registers(i.components, i.packing))
      case i: Instruction.FormatStore =>
        address(i.address) && span(i.src, VMResourceDecoder.registers(i.components, i.packing))
      case i: Instruction.LoadLocal   => address(i.address)
      case i: Instruction.StoreLocal  => address(i.address)
      case i: Instruction.SBufferLoad => scalarSpan(i.dest, i.count) && scalarSpan(i.resource, 4)
      case i: Instruction.Image       =>
        scalarSpan(i.address.resource, 8) &&
        span(i.data, VMResourceDecoder.registers(Integer.bitCount(i.mask).max(1), i.packing))
      case i: Instruction.WithStatus      => span(i.status, 1) && validate(i.access, waveSize).isRight
      case i: Instruction.LoadPair        => span(i.dest, 2 * words(i.bytes))
      case i: Instruction.StorePair       => span(i.srcA, words(i.bytes)) && span(i.srcB, words(i.bytes))
      case i: Instruction.VDPP            => validate(i.operation, waveSize).isRight
      case i: Instruction.VClampResult    => validate(i.operation, waveSize).isRight
      case i: Instruction.VHalfResult     => validate(i.operation, waveSize).isRight
      case i: Instruction.VSubDwordResult => validate(i.operation, waveSize).isRight
      case i: Instruction.AtomicExtended  => validate(i.access, waveSize).isRight
      case i: Instruction.ExchangePair    =>
        validate(i.first, waveSize).isRight && validate(i.second, waveSize).isRight
      case Instruction.VDual(left, right) => pairs(left) && pairs(right)
      case i: Instruction.VQuadSad        =>
        span(i.dest, if (i.wide) 4 else 2) && source(i.srcA) && source(i.srcC, if (i.wide) 4 else 2)
      case i: Instruction.VOutputModified =>
        validate(i.ignored, waveSize).isRight && validate(i.applied, waveSize).isRight
      case i: Instruction.SStore =>
        scalarSpan(i.src, i.count) && scalarSpan(i.address.base, if (i.address.buffer) 4 else 2)
      case i: Instruction.SAtomic =>
        scalarSpan(i.data, words(i.bytes)) && scalarSpan(i.address.base, if (i.address.buffer) 4 else 2)
      case i: Instruction.SCompareSwap =>
        scalarSpan(i.data, 2 * words(i.bytes)) && scalarSpan(i.address.base, if (i.address.buffer) 4 else 2)
      case i: Instruction.ForkIndirect => source(i.mask) && source(i.target)
      case i: Instruction.ForkRelative => source(i.mask)
      case i: Instruction.Fork         => source(i.mask)
      case _                           => true
    }
    Either.cond(valid, (), "instruction register span exceeds the register bank")
  }
}
