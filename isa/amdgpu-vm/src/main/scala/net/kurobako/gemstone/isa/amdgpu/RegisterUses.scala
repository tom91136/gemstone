package net.kurobako.gemstone.isa.amdgpu

import EncodingConstants.*
import IntegerSemantics.*
import VMIR.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.RegisterUse
import scala.annotation.tailrec

/** The registers a lowered instruction reads and writes, spelt as the ISA spells them, such as `v1`, `s[2:3]` or
  * `a[0:3]`, each with the dwords it spans. The instruction's own data gives the widths and the operands it leaves
  * unread, so a new instruction states its use or the match stops compiling. Implicit state, the null register and
  * every SGPR code from VCC on are left out, as are constants. A register an instruction preserves part of is also
  * read.
  */
private object RegisterUses {
  private case class Span(name: String, units: Int)

  private case class Use(reads: Vec[Span], writes: Vec[Span])

  private val Unused: Vec[Span] = Vec()

  private val NoUse = Use(Unused, Unused)

  private def spans(parts: Vec[Span]*): Vec[Span] = Vec.from(parts.flatten)

  private def name(prefix: String, first: Int, words: Int): String =
    if (words == 1) s"$prefix$first" else s"$prefix[$first:${first + words - 1}]"

  private def words(bytes: Int): Int = (bytes + 3) / 4

  def of(instruction: Instruction, waveSize: Int in (32, 64)): RegisterUse = {
    val found = Derivation(waveSize / 32).of(instruction)
    RegisterUse(
      found.reads.map(_.name).distinct,
      found.writes.map(_.name).distinct,
      (found.reads ++ found.writes).collect { case Span(name, units) if units > 1 => name -> units }.toMap
    )
  }

  private class Derivation(maskWords: Int) {
    private def scalar(first: ScalarRegisterIndex, count: Int = 1): Vec[Span] =
      if (first.raw >= Fixed.VccLo) Unused else Vec(Span(name("s", first.raw, count), count))

    private def scalars(first: Option[ScalarRegisterIndex], count: Int = 1): Vec[Span] =
      first.fold(Unused)(scalar(_, count))

    private def vector(first: VectorRegisterIndex, count: Int = 1, bank: RegisterBank = RegisterBank.Vector)
        : Vec[Span] =
      Vec(Span(name(if (bank == RegisterBank.Vector) "v" else "a", first.raw, count), count))

    private def vectors(first: Option[VectorRegisterIndex], count: Int = 1): Vec[Span] =
      first.fold(Unused)(vector(_, count))

    @tailrec
    private def source(value: InstructionSource, count: Int = 1): Vec[Span] = value match {
      case InstructionSource.Scalar(index)         => scalar(index, count)
      case InstructionSource.VectorRegister(index) => vector(index, count)
      case InstructionSource.HighHalf(half)        => source(half, 1)
      case InstructionSource.Selected(part, _, _)  => source(part, 1)
      case InstructionSource.Constant(_) | InstructionSource.Floating(_) | InstructionSource.Condition(_) |
          InstructionSource.Literal(_) | InstructionSource.DPP => Unused
    }

    private def sources(values: InstructionSource*): Vec[Span] = Vec.from(values.flatMap(source(_)))

    private def mask(value: InstructionSource): Vec[Span] = source(value, maskWords)

    private def maskDestination(first: ScalarRegisterIndex): Vec[Span] = scalar(first, maskWords)

    private def preserved(first: VectorRegisterIndex, components: Int, packing: Packing, bank: RegisterBank)
        : Vec[Span] = packing match {
      case Packing.High => vector(first, 1, bank)
      case Packing.Packed if components % 2 == 1 => vector(first.offsetBy((components / 2).unsafe[Int >= 0]), 1, bank)
      case Packing.Packed | Packing.Dword | Packing.Unpacked => Unused
    }

    private def scalarAddress(address: ScalarAddress): Vec[Span] =
      spans(scalar(address.base, if (address.buffer) 4 else 2), scalars(address.scalarOffset))

    private def imageAddress(address: ImageAddress): Vec[Span] =
      spans(Vec.from(address.words.flatMap(vector(_))), scalar(address.resource, 8))

    private def address(value: MemoryAddress): Vec[Span] = value match {
      case MemoryAddress.Global(vec, base, _)  => base.fold(vector(vec, 2))(scalar(_, 2) ++ vector(vec))
      case MemoryAddress.Flat(vec, _, base)    => base.fold(vector(vec, 2))(scalar(_, 2) ++ vector(vec))
      case MemoryAddress.Scratch(vec, base, _) => spans(vectors(vec), scalars(base))
      case buffer: MemoryAddress.Buffer        =>
        spans(
          scalar(buffer.resource, 4),
          scalars(buffer.scalar),
          vectors(buffer.vector, if (buffer.wide) 2 else 1),
          vectors(buffer.index)
        )
      case MemoryAddress.Local(vec, _)       => vector(vec)
      case MemoryAddress.Texel(image)        => imageAddress(image)
      case MemoryAddress.LaneLocal(_)        => Unused
      case MemoryAddress.LaneGlobal(base, _) => scalars(base, 2)
    }

    private def use(reads: Vec[Span], writes: Vec[Span]): Use = Use(reads, writes)

    private def merged(parts: Use*): Use = Use(spans(parts.map(_.reads)*), spans(parts.map(_.writes)*))

    private def accumulates(operation: VectorBinaryOperation): Boolean = operation match {
      case VectorBinaryOperation.MultiplyAccumulateF32 | VectorBinaryOperation.FusedMultiplyAccumulateF16 => true
      case _                                                                                              => false
    }

    private def comparison(operation: VectorComparison, a: InstructionSource, b: InstructionSource): Vec[Span] =
      operation.kind match {
        case VectorComparisonKind.Signed64 | VectorComparisonKind.Unsigned64 | VectorComparisonKind.Float64 =>
          source(a, 2) ++ source(b, 2)
        case VectorComparisonKind.ClassFloat64 => source(a, 2) ++ source(b)
        case _                                 => sources(a, b)
      }

    private def dual(half: DualVector): Use = {
      val count    = if (half.operation.wide) 2 else 1
      val dest     = vector(half.dest, count)
      val operands = half.operation match {
        case DualVectorOperation.Move => source(half.left, count)
        case DualVectorOperation.FmaF32 | DualVectorOperation.FmaF64 | DualVectorOperation.DotHalvesF32 |
            DualVectorOperation.DotAlignedHalvesF32 | DualVectorOperation.DotBFloat16F32 |
            DualVectorOperation.DotAlignedBFloat16F32 =>
          source(half.left, count) ++ source(half.right, count) ++ source(half.addend, count)
        case DualVectorOperation.ConditionalMask =>
          source(half.left) ++ source(half.right) ++ half.condition.fold(Unused)(mask(_))
        case DualVectorOperation.AddF32 | DualVectorOperation.SubtractF32 | DualVectorOperation.ReverseSubtractF32 |
            DualVectorOperation.MultiplyF32 | DualVectorOperation.MinimumF32 | DualVectorOperation.MaximumF32 |
            DualVectorOperation.AddU32 | DualVectorOperation.ShiftLeft32 | DualVectorOperation.And32 |
            DualVectorOperation.MultiplyLegacyF32 | DualVectorOperation.SubtractU32 |
            DualVectorOperation.MinimumI32 | DualVectorOperation.MaximumI32 | DualVectorOperation.ShiftRight32 |
            DualVectorOperation.ShiftRightArithmetic32 | DualVectorOperation.AddF64 |
            DualVectorOperation.MultiplyF64 | DualVectorOperation.MinimumF64 | DualVectorOperation.MaximumF64 |
            DualVectorOperation.BitOperation2 =>
          source(half.left, count) ++ source(half.right, count)
      }
      Use(operands, dest)
    }

    private type Scalar =
      Instruction.SLoad | Instruction.SMove | Instruction.SWriteExec | Instruction.SMoveRelative |
        Instruction.SMultiplyImmediate | Instruction.SMove64 | Instruction.SSetReg.type | Instruction.SSetMode |
        Instruction.SGetMode | Instruction.SGetPc | Instruction.SReadClock | Instruction.SReadWaveId |
        Instruction.SSetPc | Instruction.SSwapPc | Instruction.SSaveExec | Instruction.SUnary |
        Instruction.SReciprocalF32 | Instruction.SFmaF32 | Instruction.SFmaF16 | Instruction.SFloat |
        Instruction.SFloatUnary | Instruction.SBinary | Instruction.SMinMax | Instruction.SBinary64 |
        Instruction.SGprIndex | Instruction.SGprIndexOff.type | Instruction.SCSelect | Instruction.SAddImmediate |
        Instruction.SFloatCompare | Instruction.SCmp

    private type Moves =
      Instruction.VMove | Instruction.VDual | Instruction.VMove64 | Instruction.VPackedF32 | Instruction.VMedia |
        Instruction.VQuadSad | Instruction.VPermuteLanes | Instruction.VPermlaneSwap | Instruction.VBitOp3 |
        Instruction.VAccumulatorRead | Instruction.VAccumulatorWrite | Instruction.VAccumulatorMove |
        Instruction.VMatrix | Instruction.VPackedMove32 | Instruction.VMoveRelative |
        Instruction.VMoveRelativeDestination | Instruction.VSwapRelative | Instruction.VNot | Instruction.VSwap |
        Instruction.VSwapHalves | Instruction.VBitReverse | Instruction.VBitFind | Instruction.VConvert |
        Instruction.VConvertNarrow | Instruction.VConvertPackedHalf | Instruction.VScaledConvert |
        Instruction.VConvertStochasticHalf | Instruction.VOutputModified | Instruction.VHalfResult |
        Instruction.VClampResult | Instruction.VSubDwordResult | Instruction.VDPP

    private type Arithmetic =
      Instruction.VCndMask | Instruction.VReadFirstLane | Instruction.VReadLane | Instruction.VWriteLane |
        Instruction.VMaskBitCount | Instruction.VBitCount | Instruction.VBinary | Instruction.VDot2F32F16 |
        Instruction.VDot | Instruction.VDot4U32U8 | Instruction.VTrigPreopF64 | Instruction.VBinary64 |
        Instruction.VBinarySubDword | Instruction.VPackedBinary | Instruction.VPackB32F16 |
        Instruction.VPackedTernary | Instruction.VFmaMixF16 | Instruction.VFmaMixF32 | Instruction.VAddCarry |
        Instruction.VSubCarry | Instruction.VSubReverseCarry | Instruction.VMadF32 | Instruction.VMadF64 |
        Instruction.VTernary | Instruction.VMadU64U32 | Instruction.VMadI64I32 | Instruction.VCompare |
        Instruction.VDivScaleF32 | Instruction.VDivScaleF64 | Instruction.VDivFmasF64 | Instruction.VDivFixupF32 |
        Instruction.VDivFixupF64 | Instruction.VShiftLeft64 | Instruction.VShiftLeftAdd64 | Instruction.VShiftRight64 |
        Instruction.VShiftRightArithmetic64

    private type Memory =
      Instruction.CacheInvalidate.type | Instruction.CacheWritebackInvalidate.type | Instruction.Load |
        Instruction.Store | Instruction.Atomic | Instruction.LocalSource2 | Instruction.CompareSwap |
        Instruction.AtomicExtended | Instruction.LocalCounter | Instruction.ExchangePair | Instruction.GlobalWaveSync |
        Instruction.LoadTranspose | Instruction.LoadPair | Instruction.StorePair | Instruction.FormatLoad |
        Instruction.FormatStore | Instruction.LoadLocal | Instruction.StoreLocal | Instruction.SBufferLoad |
        Instruction.Image | Instruction.WithStatus | Instruction.DSPermute | Instruction.DSBPermute |
        Instruction.DSSwizzle | Instruction.SStore | Instruction.SAtomic | Instruction.SCompareSwap

    private type Control =
      Instruction.BranchRelative | Instruction.Branch | Instruction.SubvectorLoopRelative |
        Instruction.SubvectorLoop | Instruction.CallRelative | Instruction.Call | Instruction.Halt |
        Instruction.Hostcall.type | Instruction.Barrier.type | Instruction.BarrierWait |
        Instruction.BarrierLeave.type | Instruction.Wait.type | Instruction.Sleep.type | Instruction.End.type |
        Instruction.Trap | Instruction.BarrierSignal | Instruction.BarrierInit | Instruction.BarrierJoin |
        Instruction.WaitCounts | Instruction.ForkIndirect | Instruction.ForkRelative | Instruction.Fork |
        Instruction.Join | Instruction.SetVectorSkip

    def of(instruction: Instruction): Use = instruction match {
      case i: Scalar     => scalar(i)
      case i: Moves      => moves(i)
      case i: Arithmetic => vector(i)
      case i: Memory     => memory(i)
      case i: Control    => control(i)
    }

    private def scalar(instruction: Scalar): Use = instruction match {
      case i: Instruction.SLoad =>
        use(spans(scalar(i.base, 2), scalars(i.scalarOffset)), scalar(i.dest, i.count))
      case i: Instruction.SMove      => use(source(i.src), scalar(i.dest))
      case i: Instruction.SWriteExec =>
        use(source(i.src, i.width / 32), scalar(i.dest, i.width / 32))
      case i: Instruction.SMoveRelative =>
        use(source(i.src, i.words), scalar(i.dest, i.words))
      case i: Instruction.SMultiplyImmediate => use(scalar(i.dest), scalar(i.dest))
      case i: Instruction.SMove64            => use(source(i.src, 2), scalar(i.dest, 2))
      case Instruction.SSetReg               => NoUse
      case i: Instruction.SSetMode           => use(source(i.src), Unused)
      case i: Instruction.SGetMode           => use(Unused, scalar(i.dest))
      case i: Instruction.SGetPc             => use(Unused, scalar(i.dest, 2))
      case i: Instruction.SReadClock         => use(Unused, scalar(i.dest, if (i.clock == Clock.ShaderCycles) 1 else 2))
      case i: Instruction.SReadWaveId        => use(Unused, scalar(i.dest))
      case i: Instruction.SSetPc             => use(source(i.src, 2), Unused)
      case i: Instruction.SSwapPc            => use(source(i.src, 2), scalar(i.dest, 2))
      case i: Instruction.SSaveExec          =>
        use(source(i.src, i.width / 32), scalar(i.dest, i.width / 32))
      case i: Instruction.SUnary =>
        val result = scalar(i.dest, if (scalarUnaryWritesPair(i.operation)) 2 else 1)
        val reads  = i.operation match {
          case ScalarUnaryOperation.ClearBit32 | ScalarUnaryOperation.SetBit32 | ScalarUnaryOperation.ClearBit64 |
              ScalarUnaryOperation.SetBit64 => result
          case _ => Unused
        }
        use(source(i.src, if (scalarUnaryReadsPair(i.operation)) 2 else 1) ++ reads, result)
      case i: Instruction.SReciprocalF32 => use(source(i.src), scalar(i.dest))
      case i: Instruction.SFmaF32        => use(sources(i.srcA, i.srcB, i.srcC), scalar(i.dest))
      case i: Instruction.SFmaF16        => use(sources(i.srcA, i.srcB, i.srcC), scalar(i.dest))
      case i: Instruction.SFloat         => use(sources(i.srcA, i.srcB), scalar(i.dest))
      case i: Instruction.SFloatUnary    => use(source(i.src), scalar(i.dest))
      case i: Instruction.SBinary        => use(sources(i.srcA, i.srcB), scalar(i.dest))
      case i: Instruction.SMinMax        => use(sources(i.srcA, i.srcB), scalar(i.dest))
      case i: Instruction.SBinary64      =>
        val narrowSecond = i.operation match {
          case ScalarBinary64Operation.ShiftLeft | ScalarBinary64Operation.ShiftRight |
              ScalarBinary64Operation.ShiftRightArithmetic | ScalarBinary64Operation.BitFieldExtractI64 |
              ScalarBinary64Operation.BitFieldExtractU64 | ScalarBinary64Operation.BitFieldMask => true
          case ScalarBinary64Operation.Multiply | ScalarBinary64Operation.AddNoCarry |
              ScalarBinary64Operation.SubtractNoCarry | ScalarBinary64Operation.And | ScalarBinary64Operation.AndNot |
              ScalarBinary64Operation.Or | ScalarBinary64Operation.OrNot | ScalarBinary64Operation.Nor |
              ScalarBinary64Operation.Xor | ScalarBinary64Operation.Nand | ScalarBinary64Operation.Xnor => false
        }
        val narrowFirst = i.operation == ScalarBinary64Operation.BitFieldMask
        use(
          source(i.srcA, if (narrowFirst) 1 else 2) ++ source(i.srcB, if (narrowSecond) 1 else 2),
          scalar(i.dest, 2)
        )
      case i: Instruction.SGprIndex => use(i.index.fold(Unused)(source(_)), Unused)
      case Instruction.SGprIndexOff => NoUse
      case i: Instruction.SCSelect  =>
        use(source(i.srcA, i.width / 32) ++ source(i.srcB, i.width / 32), scalar(i.dest, i.width / 32))
      case i: Instruction.SAddImmediate => use(scalar(i.dest), scalar(i.dest))
      case i: Instruction.SFloatCompare => use(sources(i.srcA, i.srcB), Unused)
      case i: Instruction.SCmp          =>
        val count = if (scalarCompareWide(i.operation)) 2 else 1
        i.operation match {
          case ScalarCompareOperation.BitSet64 | ScalarCompareOperation.BitClear64 =>
            use(source(i.srcA, 2) ++ source(i.srcB), Unused)
          case _ => use(source(i.srcA, count) ++ source(i.srcB, count), Unused)
        }
    }

    private def moves(instruction: Moves): Use = instruction match {
      case i: Instruction.VMove           => use(source(i.src), vector(i.dest))
      case Instruction.VDual(left, right) => merged(dual(left), dual(right))
      case i: Instruction.VMove64         => use(source(i.src, 2), vector(i.dest, 2))
      case i: Instruction.VPackedF32      =>
        val third = if (i.operation == PackedF32Operation.Fma) source(i.srcC, 2) else Unused
        use(source(i.srcA, 2) ++ source(i.srcB, 2) ++ third, vector(i.dest, 2))
      case i: Instruction.VMedia   => use(sources(i.srcA, i.srcB, i.srcC), vector(i.dest))
      case i: Instruction.VQuadSad =>
        val count = if (i.wide) 4 else 2
        use(source(i.srcA, 2) ++ source(i.srcB) ++ source(i.srcC, count), vector(i.dest, count))
      case i: Instruction.VPermuteLanes =>
        use(sources(i.src, i.low, i.high) ++ (if (i.boundControl) Unused else vector(i.dest)), vector(i.dest))
      case i: Instruction.VPermlaneSwap =>
        use(vector(i.dest) ++ vector(i.src), vector(i.dest) ++ vector(i.src))
      case i: Instruction.VBitOp3           => use(sources(i.srcA, i.srcB, i.srcC), vector(i.dest))
      case i: Instruction.VAccumulatorRead  => use(vector(i.src, 1, RegisterBank.Accumulator), vector(i.dest))
      case i: Instruction.VAccumulatorWrite =>
        use(source(i.src), vector(i.dest, 1, RegisterBank.Accumulator))
      case i: Instruction.VAccumulatorMove =>
        use(vector(i.src, 1, RegisterBank.Accumulator), vector(i.dest, 1, RegisterBank.Accumulator))
      case i: Instruction.VMatrix =>
        val shape                          = i.shape
        def banked(bit: Int): RegisterBank =
          if ((i.controls.banks & (1 << bit)) != 0) RegisterBank.Accumulator else RegisterBank.Vector
        def operand(value: InstructionSource, count: Int, bit: Int): Vec[Span] = value match {
          case InstructionSource.VectorRegister(index) => vector(index, count, banked(bit))
          case other                                   => source(other, count)
        }
        val dest      = vector(i.dest, shape.outputRegisters, banked(2))
        val preserved = if (shape.sparse || shape.halfRegisters) dest else Unused
        val reads     = operand(i.srcA, shape.aRegisters, 0) ++ operand(i.srcB, shape.bRegisters, 1) ++ preserved ++
          (if (shape.sparse) source(i.srcC) else operand(i.srcC, shape.outputRegisters, 2)) ++
          i.controls.scales.fold(Unused)(scales => sources(scales.a, scales.b))
        use(reads, dest)
      case i: Instruction.VPackedMove32 =>
        use(source(i.srcA, 2) ++ source(i.srcB, 2), vector(i.dest, 2))
      case i: Instruction.VMoveRelative            => use(source(i.src), vector(i.dest))
      case i: Instruction.VMoveRelativeDestination =>
        use(source(i.src), vector(i.dest))
      case i: Instruction.VSwapRelative => use(vector(i.dest) ++ vector(i.src), vector(i.dest) ++ vector(i.src))
      case i: Instruction.VNot          => use(source(i.src), vector(i.dest))
      case i: Instruction.VSwap         => use(vector(i.dest) ++ source(i.src), vector(i.dest) ++ source(i.src))
      case i: Instruction.VSwapHalves   => use(vector(i.dest) ++ vector(i.src), vector(i.dest) ++ vector(i.src))
      case i: Instruction.VBitReverse   => use(source(i.src), vector(i.dest))
      case i: Instruction.VBitFind      => use(source(i.src), vector(i.dest))
      case i: Instruction.VConvert      =>
        use(source(i.src, i.operation.sourceWords), vector(i.dest, i.operation.destWords))
      case i: Instruction.VConvertNarrow =>
        use(sources(i.srcA, i.srcB) ++ vector(i.dest), vector(i.dest))
      case i: Instruction.VConvertPackedHalf => use(source(i.src), vector(i.dest))
      case i: Instruction.VScaledConvert     =>
        val dwords   = i.conversion.dwords
        val existing = i.conversion match {
          case ScaledConversion(Minifloats.Wide.Single, ScaledConversion.Layout.Widen(_)) => Unused
          case ScaledConversion(
                _,
                ScaledConversion.Layout.Widen(_) | ScaledConversion.Layout.NarrowPair(_) |
                ScaledConversion.Layout.NarrowStochastic(_) | ScaledConversion.Layout.NarrowStochasticPair
              ) =>
            vector(i.dest, dwords.dest)
          case ScaledConversion(
                _,
                ScaledConversion.Layout.WidenPair(_) | ScaledConversion.Layout.WidenBlock(_) |
                ScaledConversion.Layout.NarrowBlock(_, _, _)
              ) => Unused
        }
        use(
          source(i.srcA, dwords.a) ++ source(i.srcB, dwords.b) ++ source(i.srcC, dwords.c) ++ existing,
          vector(i.dest, dwords.dest)
        )
      case i: Instruction.VConvertStochasticHalf => use(sources(i.srcA, i.srcB) ++ vector(i.dest), vector(i.dest))
      case i: Instruction.VOutputModified        => merged(of(i.ignored), use(vector(i.dest), vector(i.dest)))
      case i: Instruction.VHalfResult            => merged(of(i.operation), use(vector(i.dest), vector(i.dest)))
      case i: Instruction.VClampResult           => merged(of(i.operation), use(Unused, vector(i.dest)))
      case i: Instruction.VSubDwordResult        =>
        val existing = if (i.unused.readsPrevious(i.selection)) vector(i.dest) else Unused
        merged(of(i.operation), use(existing, vector(i.dest)))
      case i: Instruction.VDPP =>
        val kept = i.control match {
          case DPPControl.DPP16(_, rowMask, bankMask, boundControl)
              if !boundControl || rowMask != 0xf || bankMask != 0xf =>
            of(i.operation).writes.filter(!_.name.startsWith("s"))
          case DPPControl.DPP16(_, _, _, _) | DPPControl.DPP8(_) => Unused
        }
        merged(of(i.operation), use(vector(i.src, if (i.wide) 2 else 1, i.bank) ++ kept, Unused))
    }

    private def vector(instruction: Arithmetic): Use = instruction match {
      case i: Instruction.VCndMask =>
        use(sources(i.srcA, i.srcB) ++ mask(i.condition), vector(i.dest))
      case i: Instruction.VReadFirstLane => use(source(i.src), scalar(i.dest))
      case i: Instruction.VReadLane      => use(sources(i.src, i.lane), scalar(i.dest))
      case i: Instruction.VWriteLane     => use(sources(i.src, i.lane) ++ vector(i.dest), vector(i.dest))
      case i: Instruction.VMaskBitCount  => use(sources(i.mask, i.base), vector(i.dest))
      case i: Instruction.VBitCount      => use(sources(i.src, i.base), vector(i.dest))
      case i: Instruction.VBinary        =>
        use(sources(i.srcA, i.srcB) ++ (if (accumulates(i.operation)) vector(i.dest) else Unused), vector(i.dest))
      case i: Instruction.VDot2F32F16 =>
        use(sources(i.srcA, i.srcB) ++ (if (i.accumulate) vector(i.dest) else Unused), vector(i.dest))
      case i: Instruction.VDot          => use(sources(i.srcA, i.srcB, i.srcC), vector(i.dest))
      case i: Instruction.VDot4U32U8    => use(sources(i.srcA, i.srcB, i.srcC), vector(i.dest))
      case i: Instruction.VTrigPreopF64 => use(source(i.src, 2) ++ source(i.segment), vector(i.dest, 2))
      case i: Instruction.VBinary64     =>
        val second = if (i.operation == VectorBinary64Operation.LoadExponent) 1 else 2
        use(source(i.srcA, 2) ++ source(i.srcB, second), vector(i.dest, 2))
      case i: Instruction.VBinarySubDword =>
        val existing = if (i.destUnused.readsPrevious(i.destSelection)) vector(i.dest) else Unused
        use(sources(i.srcA, i.srcB) ++ existing, vector(i.dest))
      case i: Instruction.VPackedBinary  => use(sources(i.srcA, i.srcB), vector(i.dest))
      case i: Instruction.VPackB32F16    => use(sources(i.srcA, i.srcB), vector(i.dest))
      case i: Instruction.VPackedTernary => use(sources(i.srcA, i.srcB, i.srcC), vector(i.dest))
      case i: Instruction.VFmaMixF16     => use(sources(i.srcA, i.srcB, i.srcC) ++ vector(i.dest), vector(i.dest))
      case i: Instruction.VFmaMixF32     => use(sources(i.srcA, i.srcB, i.srcC), vector(i.dest))
      case i: Instruction.VAddCarry      =>
        use(sources(i.srcA, i.srcB) ++ i.carryIn.fold(Unused)(mask(_)), vector(i.dest) ++ maskDestination(i.carryDest))
      case i: Instruction.VSubCarry =>
        use(sources(i.srcA, i.srcB) ++ i.carryIn.fold(Unused)(mask(_)), vector(i.dest) ++ maskDestination(i.carryDest))
      case i: Instruction.VSubReverseCarry =>
        use(sources(i.srcA, i.srcB) ++ i.carryIn.fold(Unused)(mask(_)), vector(i.dest) ++ maskDestination(i.carryDest))
      case i: Instruction.VMadF32 => use(sources(i.srcA, i.srcB, i.srcC), vector(i.dest))
      case i: Instruction.VMadF64 =>
        use(source(i.srcA, 2) ++ source(i.srcB, 2) ++ source(i.srcC, 2), vector(i.dest, 2))
      case i: Instruction.VTernary   => use(sources(i.srcA, i.srcB, i.srcC), vector(i.dest))
      case i: Instruction.VMadU64U32 =>
        use(sources(i.srcA, i.srcB) ++ source(i.srcC, 2), vector(i.dest, 2) ++ maskDestination(i.carryDest))
      case i: Instruction.VMadI64I32 =>
        use(sources(i.srcA, i.srcB) ++ source(i.srcC, 2), vector(i.dest, 2) ++ maskDestination(i.carryDest))
      case i: Instruction.VCompare     => use(comparison(i.operation, i.srcA, i.srcB), maskDestination(i.dest))
      case i: Instruction.VDivScaleF32 =>
        use(sources(i.srcA, i.srcB, i.srcC), vector(i.dest) ++ maskDestination(i.conditionDest))
      case i: Instruction.VDivScaleF64 =>
        use(
          source(i.srcA, 2) ++ source(i.srcB, 2) ++ source(i.srcC, 2),
          vector(i.dest, 2) ++ maskDestination(i.conditionDest)
        )
      case i: Instruction.VDivFmasF64 =>
        use(source(i.srcA, 2) ++ source(i.srcB, 2) ++ source(i.srcC, 2), vector(i.dest, 2))
      case i: Instruction.VDivFixupF32 => use(sources(i.quotient, i.denominator, i.numerator), vector(i.dest))
      case i: Instruction.VDivFixupF64 =>
        use(source(i.quotient, 2) ++ source(i.denominator, 2) ++ source(i.numerator, 2), vector(i.dest, 2))
      case i: Instruction.VShiftLeft64    => use(source(i.amount) ++ source(i.src, 2), vector(i.dest, 2))
      case i: Instruction.VShiftLeftAdd64 =>
        use(source(i.src, 2) ++ source(i.amount) ++ source(i.addend, 2), vector(i.dest, 2))
      case i: Instruction.VShiftRight64           => use(source(i.amount) ++ source(i.src, 2), vector(i.dest, 2))
      case i: Instruction.VShiftRightArithmetic64 => use(source(i.amount) ++ source(i.src, 2), vector(i.dest, 2))
    }

    private def memory(instruction: Memory): Use = instruction match {
      case Instruction.CacheInvalidate | Instruction.CacheWritebackInvalidate => NoUse
      case i: Instruction.Load                                                =>
        val existing = i.placement match {
          case LoadPlacement.Low | LoadPlacement.High => vector(i.dest, 1, i.bank)
          case LoadPlacement.Full | LoadPlacement.LowZeroingHigh | LoadPlacement.HighZeroingLow => Unused
        }
        use(address(i.address) ++ existing, vector(i.dest, words(i.bytes), i.bank))
      case i: Instruction.Store  => use(address(i.address) ++ vector(i.src, words(i.bytes), i.bank), Unused)
      case i: Instruction.Atomic =>
        val returned = if (i.returnValue) vector(i.dest, words(i.bytes), i.bank) else Unused
        use(address(i.address) ++ vector(i.src, words(i.bytes), i.bank), returned)
      case i: Instruction.LocalSource2 => use(vector(i.address), Unused)
      case i: Instruction.CompareSwap  =>
        val returned = if (i.returnValue) vector(i.dest, words(i.bytes), i.bank) else Unused
        use(
          address(i.address) ++ vector(i.data, words(i.bytes), i.bank) ++ vector(i.comparand, words(i.bytes), i.bank),
          returned
        )
      case i: Instruction.AtomicExtended => of(i.access)
      case i: Instruction.LocalCounter   => use(Unused, vector(i.dest, 1, i.bank))
      case i: Instruction.ExchangePair   => merged(of(i.first), of(i.second))
      case i: Instruction.GlobalWaveSync => use(vector(i.data, 1, i.bank), Unused)
      case i: Instruction.LoadTranspose  =>
        use(address(i.address), vector(i.dest, (i.layout.bits * i.layout.elements + 31) / 32))
      case i: Instruction.LoadPair =>
        use(vector(i.address), vector(i.dest, 2 * words(i.bytes), i.bank))
      case i: Instruction.StorePair =>
        use(
          vector(i.address) ++ vector(i.srcA, words(i.bytes), i.bank) ++ vector(i.srcB, words(i.bytes), i.bank),
          Unused
        )
      case i: Instruction.FormatLoad =>
        use(
          address(i.address) ++ preserved(i.dest, i.components, i.packing, i.bank),
          vector(i.dest, VMResourceDecoder.registers(i.components, i.packing), i.bank)
        )
      case i: Instruction.FormatStore =>
        use(address(i.address) ++ vector(i.src, VMResourceDecoder.registers(i.components, i.packing), i.bank), Unused)
      case i: Instruction.LoadLocal   => use(address(i.address), Unused)
      case i: Instruction.StoreLocal  => use(address(i.address), Unused)
      case i: Instruction.SBufferLoad =>
        use(spans(scalar(i.resource, 4), scalars(i.scalarOffset)), scalar(i.dest, i.count))
      case i: Instruction.Image =>
        val components = Integer.bitCount(i.mask).max(1)
        val data       = vector(i.data, VMResourceDecoder.registers(components, i.packing), i.bank)
        val existing   = preserved(i.data, components, i.packing, i.bank)
        i.operation match {
          case ImageOperation.Load(_, _) => use(imageAddress(i.address) ++ existing, data)
          case ImageOperation.Dimensions =>
            use(i.address.words.headOption.fold(Unused)(vector(_)) ++ scalar(i.address.resource, 8) ++ existing, data)
          case ImageOperation.Store(_)           => use(imageAddress(i.address) ++ data, Unused)
          case ImageOperation.Sample(sampler, _) =>
            use(imageAddress(i.address) ++ scalar(sampler, 4) ++ existing, data)
        }
      case i: Instruction.WithStatus => merged(of(i.access), use(Unused, vector(i.status)))
      case i: Instruction.DSPermute  =>
        use(vector(i.address) ++ vector(i.src, 1, i.bank), vector(i.dest, 1, i.bank))
      case i: Instruction.DSBPermute =>
        use(vector(i.address) ++ vector(i.src, 1, i.bank), vector(i.dest, 1, i.bank))
      case i: Instruction.DSSwizzle => use(vector(i.src), vector(i.dest, 1, i.bank))
      case i: Instruction.SStore    =>
        use(scalar(i.src, i.count) ++ scalarAddress(i.address), Unused)
      case i: Instruction.SAtomic =>
        val data = scalar(i.data, words(i.bytes))
        use(data ++ scalarAddress(i.address), if (i.returnValue) data else Unused)
      case i: Instruction.SCompareSwap =>
        val data = scalar(i.data, 2 * words(i.bytes))
        use(data ++ scalarAddress(i.address), if (i.returnValue) scalar(i.data, words(i.bytes)) else Unused)
    }

    private def control(instruction: Control): Use = instruction match {
      case Instruction.BranchRelative(_, _) | Instruction.Branch(_, _) => NoUse
      case i: Instruction.SubvectorLoopRelative                        => use(scalar(i.state), scalar(i.state))
      case i: Instruction.SubvectorLoop                                => use(scalar(i.state), scalar(i.state))
      case i: Instruction.CallRelative                                 => use(Unused, scalar(i.dest, 2))
      case i: Instruction.Call                                         => use(Unused, scalar(i.dest, 2))
      case Instruction.Halt(_) | Instruction.Hostcall | Instruction.Barrier | Instruction.BarrierWait(_) |
          Instruction.BarrierLeave | Instruction.Wait | Instruction.Sleep | Instruction.End |
          Instruction.Trap(_) => NoUse
      case i: Instruction.BarrierSignal => use(source(i.id), Unused)
      case i: Instruction.BarrierInit   => use(sources(i.id, i.m0), Unused)
      case i: Instruction.BarrierJoin   => use(source(i.id), Unused)
      case i: Instruction.WaitCounts    =>
        use(Vec.from(i.counts.flatMap(count => count.base.fold(Unused)(source(_)))), Unused)
      case i: Instruction.ForkIndirect  => use(source(i.mask, 2) ++ source(i.target, 2), Unused)
      case i: Instruction.ForkRelative  => use(source(i.mask, 2), Unused)
      case i: Instruction.Fork          => use(source(i.mask, 2), Unused)
      case i: Instruction.Join          => use(source(i.saved), Unused)
      case i: Instruction.SetVectorSkip => use(sources(i.src, i.bit), Unused)
    }
  }
}
