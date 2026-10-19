package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.render.*
import net.kurobako.gemstone.testing.*

private object Values {
  val Unused: InstructionSource = InstructionSource.Constant(0L)

  /** A result and whether its flag is set. */
  def flagged(out: (Long, Long)): (Long, Boolean) = (out._1, out._2 != 0L)

  def subDword(operation: VectorSubDwordOperation): Instruction.VBinarySubDword = Instruction.VBinarySubDword(
    operation,
    0,
    Unused,
    Unused,
    destSelection = SubDwordSelection.DWord,
    destUnused = SubDwordDestinationUnused.Preserve,
    srcASelection = SubDwordSelection.DWord,
    srcBSelection = SubDwordSelection.DWord,
    srcASignExtend = false,
    srcBSignExtend = false
  )

  def operand(index: ScalarRegisterIndex): InstructionSource = InstructionSource.Scalar(index)
  def vector(index: VectorRegisterIndex): InstructionSource  = InstructionSource.VectorRegister(index)

  /** Renders a wave64 lane over registers and register pairs from 0 to 3 named `d`, `a`, `b` and `c`. */
  def render: LaneRenderer = LaneRenderer(RenderContext.Names(named, 64), None)

  private val Names = Vector("d", "a", "b", "c")

  private val named: String => Option[String] = {
    val Register = raw"[sv](\d+)".r
    val Pair     = raw"[sv]\[(\d+):\d+\]".r
    {
      case Register(index) if index.toInt < 4 => Some(Names(index.toInt))
      case Pair(index) if index.toInt < 4     => Some(Names(index.toInt))
      case _                                  => None
    }
  }

  /** A rendering's single value, or its assignments. */
  def shown(steps: Term.Steps): String = steps.all match {
    case Vec(Term.Step(_, value)) => value.show
    case _                        => steps.show
  }

  def packed(operation: VectorPackedBinaryOperation, clamp: Boolean): Instruction.VPackedBinary =
    Instruction.VPackedBinary(
      operation,
      0,
      Unused,
      Unused,
      srcAHighForHigh = false,
      srcBHighForHigh = false,
      clamp = clamp
    )
}

/** Applies a shape's kernels to `a`, `b`, `c` and `d`, in the order the shape reads its operands and implicit state,
  * for the result and the flag. Unary conversions report the operand form they read as the flag: 32 or 16 for a
  * modified dword, 2 widening, 3 narrowing and 4 for a register pair. Routing shapes report their lane as the flag.
  */
private class Values(a: Long, b: Long = 0L, c: Long = 0L, d: Long = 0L) extends AMDGPUSinks[Long, (Long, Long)] {
  private type Out = (Long, Long)

  private def applied: Applied[Out] = Applied(a, b)((_, 0L))

  inline def section(inline body: Out): Out = body

  inline def modeRegister: Long = 0L

  inline def sequence(inline before: Out, inline after: Out): Out = {
    val _ = before
    after
  }

  inline def scalarDword(dest: ScalarRegisterIndex, x: InstructionSource, y: InstructionSource)(
      inline result: (Long, Long, Long) => Long
  ): Out = (result(a, b, c), c)

  inline def scalarDwordFlagged(dest: ScalarRegisterIndex, x: InstructionSource, y: InstructionSource)(
      inline result: (Long, Long, Long) => Long,
      inline flag: (Long, Long, Long, Long) => Long
  ): Out = {
    val value = result(a, b, c)
    (value, flag(a, b, c, value))
  }

  inline def scalarPair(dest: ScalarRegisterIndex, x: InstructionSource, y: InstructionSource)(
      inline result: (Long, Long, Long) => Long
  ): Out = (result(a, b, c), c)

  inline def scalarPairFlagged(dest: ScalarRegisterIndex, x: InstructionSource, y: InstructionSource)(
      inline result: (Long, Long, Long) => Long,
      inline flag: (Long, Long, Long, Long) => Long
  ): Out = {
    val value = result(a, b, c)
    (value, flag(a, b, c, value))
  }

  inline def scalarPairDwordFlagged(dest: ScalarRegisterIndex, x: InstructionSource, y: InstructionSource)(
      inline result: (Long, Long, Long) => Long,
      inline flag: (Long, Long, Long, Long) => Long
  ): Out = {
    val value = result(a, b, c)
    (value, flag(a, b, c, value))
  }

  inline def scalarUnary(dest: ScalarRegisterIndex, x: InstructionSource, readsPair: Boolean, writesPair: Boolean)(
      inline result: (Long, Long) => Long
  ): Out = (result(a, b), 0L)

  inline def scalarUnaryFlagged(
      dest: ScalarRegisterIndex,
      x: InstructionSource,
      readsPair: Boolean,
      writesPair: Boolean
  )(inline result: (Long, Long) => Long, inline flag: (Long, Long, Long) => Long): Out = {
    val value = result(a, b)
    (value, flag(a, b, value))
  }

  inline def scalarImmediate(dest: ScalarRegisterIndex, immediate: Long)(
      inline result: (Long, Long) => Long,
      inline flag: (Long, Long, Long) => Long
  ): Out = {
    val value = result(a, immediate)
    (value, flag(a, immediate, value))
  }

  inline def saveExec(dest: ScalarRegisterIndex, x: InstructionSource, width: Int in (32, 64))(
      inline result: (Long, Long) => Long,
      inline flag: (Long, Long, Long) => Long
  ): Out = {
    val value = result(a, b)
    (value, flag(a, b, value))
  }

  inline def writeExec(dest: ScalarRegisterIndex, x: InstructionSource, width: Int in (32, 64))(
      inline result: (Long, Long) => Long,
      inline flag: (Long, Long, Long) => Long
  ): Out = (result(a, b), flag(a, b, c))

  inline def scalarCompare(x: InstructionSource, y: InstructionSource, pair: Boolean)(
      inline kernel: (Long, Long) => Long
  ): Out = (kernel(a, b), 0L)

  inline def scalarMove(dest: ScalarRegisterIndex, x: InstructionSource, pair: Boolean)(inline kernel: Long => Long)
      : Out = (kernel(a), 0L)

  inline def scalarModified(dest: ScalarRegisterIndex, x: InstructionSource, modifiers: SourceModifiers)(
      inline kernel: Long => Long
  ): Out = (kernel(a), 0L)

  inline def scalarTernary(
      dest: ScalarRegisterIndex,
      x: InstructionSource,
      y: InstructionSource,
      z: InstructionSource
  )(inline kernel: (Long, Long, Long) => Long): Out = (kernel(a, b, c), 0L)

  inline def readFirstLane(dest: ScalarRegisterIndex, x: InstructionSource)(inline kernel: Long => Long): Out =
    (kernel(a), 0L)

  inline def readLane(dest: ScalarRegisterIndex, x: InstructionSource, lane: InstructionSource)(
      inline index: (Long, Long) => Long
  )(inline kernel: Long => Long): Out = (kernel(a), index(b, c))

  inline def writeLane(dest: VectorRegisterIndex, x: InstructionSource, lane: InstructionSource)(
      inline index: (Long, Long) => Long
  )(inline kernel: Long => Long): Out = (kernel(a), index(b, c))

  inline def vectorUnary(bank: RegisterBank, dest: VectorRegisterIndex, x: InstructionSource)(
      inline kernel: Long => Long
  ): Out = (kernel(a), 0L)

  inline def accumulatorMove(destinationBank: RegisterBank, dest: VectorRegisterIndex, src: VectorRegisterIndex)(
      inline kernel: Long => Long
  ): Out = (kernel(a), 0L)

  inline def moveRelative(dest: VectorRegisterIndex, x: InstructionSource, field: RelativeField)(
      inline kernel: Long => Long
  ): Out = (kernel(a), 0L)

  inline def moveRelativeDestination(
      dest: VectorRegisterIndex,
      x: InstructionSource,
      sourceField: Option[RelativeField],
      field: RelativeField
  )(inline kernel: Long => Long): Out = (kernel(a), 0L)

  inline def swapRelative(dest: VectorRegisterIndex, src: VectorRegisterIndex)(inline kernel: Long => Long): Out =
    (kernel(a), 0L)

  inline def swap(dest: VectorRegisterIndex, x: InstructionSource)(inline kernel: Long => Long): Out = (kernel(a), 0L)

  inline def modifiedUnary(
      dest: VectorRegisterIndex,
      x: InstructionSource,
      modifiers: SourceModifiers,
      width: Int in (16, 32, 64)
  )(inline kernel: Long => Long): Out = (kernel(a), width.toLong)

  inline def wideningUnary(dest: VectorRegisterIndex, x: InstructionSource, modifiers: SourceModifiers)(
      inline kernel: Long => Long
  ): Out = (kernel(a), 2L)

  inline def narrowingUnary(dest: VectorRegisterIndex, x: InstructionSource, modifiers: SourceModifiers)(
      inline kernel: Long => Long
  ): Out = (kernel(a), 3L)

  inline def pairUnary(dest: VectorRegisterIndex, x: InstructionSource, read: PairRead, modifiers: SourceModifiers)(
      inline kernel: Long => Long
  ): Out = (kernel(a), 4L)

  inline def vectorBinary(dest: VectorRegisterIndex, x: InstructionSource, y: InstructionSource)(
      inline kernel: (Long, Long) => Long
  ): Out = (kernel(a, b), 0L)

  inline def vectorFloat(
      dest: VectorRegisterIndex,
      x: InstructionSource,
      y: InstructionSource,
      modifiers: SourceModifiers,
      width: Int in (16, 32, 64)
  )(inline kernel: (Long, Long, Long) => Long): Out = (kernel(a, b, c), 0L)

  inline def vectorTernary(
      dest: VectorRegisterIndex,
      x: InstructionSource,
      y: InstructionSource,
      z: InstructionSource,
      modifiers: SourceModifiers,
      width: Int in (16, 32, 64)
  )(inline kernel: (Long, Long, Long) => Long): Out = (kernel(a, b, c), 0L)

  inline def vectorCarry(dest: VectorRegisterIndex, x: InstructionSource, y: InstructionSource)(
      inline result: (Long, Long, Long) => Long,
      inline flag: (Long, Long, Long, Long) => Long
  ): Out = {
    val value = result(a, b, c)
    (value, flag(a, b, c, value))
  }

  inline def maskCarry(
      dest: VectorRegisterIndex,
      carryDest: ScalarRegisterIndex,
      x: InstructionSource,
      y: InstructionSource,
      carryIn: Option[InstructionSource]
  )(inline result: (Long, Long, Long) => Long, inline flag: (Long, Long, Long, Long) => Long): Out = {
    val value = result(a, b, c)
    (value, flag(a, b, c, value))
  }

  inline def subDword(instruction: Instruction.VBinarySubDword)(inline kernel: (Long, Long) => Long): Out =
    (kernel(a, b), 0L)

  inline def subDwordFlagged(instruction: Instruction.VBinarySubDword)(
      inline result: (Long, Long, Long) => Long,
      inline flag: (Long, Long, Long, Long) => Long
  ): Out = {
    val value = result(a, b, c)
    (value, flag(a, b, c, value))
  }

  inline def subDwordConditional(instruction: Instruction.VBinarySubDword)(inline result: (Long, Long, Long) => Long)
      : Out = (result(a, b, c), c)

  inline def packedBinary(instruction: Instruction.VPackedBinary)(inline kernel: (Long, Long) => Long): Out =
    (kernel(a, b), 0L)

  inline def select(
      dest: VectorRegisterIndex,
      x: InstructionSource,
      y: InstructionSource,
      condition: InstructionSource,
      modifiers: SourceModifiers
  )(inline kernel: (Long, Long, Long) => Long): Out = (kernel(a, b, c), 0L)

  inline def maskCount(dest: VectorRegisterIndex, mask: InstructionSource, base: InstructionSource)(
      inline kernel: (Long, Long, Long) => Long
  ): Out = (kernel(a, b, c), 0L)

  inline def dualFirst(half: DualVector)(inline kernel: (Long, Long, Long, Long) => Long): Out =
    (kernel(a, b, c, d), 0L)

  inline def dualSecond(half: DualVector, other: DualVector)(inline kernel: (Long, Long, Long, Long) => Long): Out =
    (kernel(a, b, c, d), 0L)

  inline def swapHalves(instruction: Instruction.VSwapHalves)(
      inline dest: (Long, Long) => Long,
      inline src: (Long, Long) => Long
  ): Out = (dest(a, b), src(b, a))

  inline def swapLanes(instruction: Instruction.VPermlaneSwap)(inline kernel: Long => Long): Out = (kernel(a), 0L)

  inline def block(dest: VectorRegisterIndex, width: Int, sources: Vec[(InstructionSource, Int)])(
      inline kernel: (IndexedSeq[Long], IndexedSeq[Long]) => IndexedSeq[Long]
  ): Out = (kernel(Vec(a, b, c), Vec.fill(width)(d)).head, 0L)

  inline def accumulating(
      dest: VectorRegisterIndex,
      x: InstructionSource,
      y: InstructionSource,
      z: InstructionSource
  )(inline kernel: (Long, Long, Long, Long) => Long): Out = (kernel(a, b, c, d), 0L)

  inline def packedSingle(instruction: Instruction.VPackedF32)(inline kernel: (Long, Long, Long) => Long): Out =
    (kernel(a, b, c), 0L)

  inline def packedHalf(instruction: Instruction.VPackedTernary)(inline kernel: (Long, Long, Long) => Long): Out =
    (kernel(a, b, c), 0L)

  inline def halves(instruction: Instruction.VPackB32F16)(inline kernel: (Long, Long) => Long): Out =
    (kernel(a, b), 0L)

  inline def pairBinary(
      dest: VectorRegisterIndex,
      x: InstructionSource,
      y: InstructionSource,
      read: PairRead,
      modifiers: SourceModifiers
  )(inline kernel: (Long, Long) => Long): Out = (kernel(a, b), 0L)

  inline def pairDword(
      dest: VectorRegisterIndex,
      x: InstructionSource,
      y: InstructionSource,
      read: PairRead,
      modifiers: SourceModifiers
  )(inline kernel: (Long, Long) => Long): Out = (kernel(a, b), 1L)

  inline def pairTernary(
      dest: VectorRegisterIndex,
      x: InstructionSource,
      y: InstructionSource,
      z: InstructionSource,
      modifiers: SourceModifiers
  )(inline kernel: (Long, Long, Long) => Long): Out = (kernel(a, b, c), 0L)

  inline def pairShiftAdd(
      dest: VectorRegisterIndex,
      x: InstructionSource,
      y: InstructionSource,
      z: InstructionSource
  )(inline kernel: (Long, Long, Long) => Long): Out = (kernel(a, b, c), 0L)

  inline def multiplyAdd64(
      dest: VectorRegisterIndex,
      carryDest: ScalarRegisterIndex,
      x: InstructionSource,
      y: InstructionSource,
      z: InstructionSource
  )(inline result: (Long, Long, Long, Long) => Long, inline flag: (Long, Long, Long, Long, Long) => Long): Out = {
    val value = result(a, b, c, d)
    (value, flag(a, b, c, d, value))
  }

  inline def scaled(
      dest: VectorRegisterIndex,
      x: InstructionSource,
      y: InstructionSource,
      z: InstructionSource,
      modifiers: SourceModifiers
  )(inline result: (Long, Long, Long, Long) => Long): Out = (result(a, b, c, d), 0L)

  inline def scaledFlagged(
      dest: VectorRegisterIndex,
      conditionDest: ScalarRegisterIndex,
      x: InstructionSource,
      y: InstructionSource,
      z: InstructionSource,
      modifiers: SourceModifiers
  )(inline result: (Long, Long, Long, Long) => Long, inline flag: (Long, Long, Long, Long, Long) => Long): Out = {
    val value = result(a, b, c, d)
    (value, flag(a, b, c, d, value))
  }

  inline def pairScaled(
      dest: VectorRegisterIndex,
      x: InstructionSource,
      y: InstructionSource,
      z: InstructionSource,
      modifiers: SourceModifiers
  )(inline result: (Long, Long, Long, Long) => Long): Out = (result(a, b, c, d), 0L)

  inline def pairScaledFlagged(
      dest: VectorRegisterIndex,
      conditionDest: ScalarRegisterIndex,
      x: InstructionSource,
      y: InstructionSource,
      z: InstructionSource,
      modifiers: SourceModifiers
  )(inline result: (Long, Long, Long, Long) => Long, inline flag: (Long, Long, Long, Long, Long) => Long): Out = {
    val value = result(a, b, c, d)
    (value, flag(a, b, c, d, value))
  }

  inline def compare(
      dest: ScalarRegisterIndex,
      x: InstructionSource,
      y: InstructionSource,
      selection: Option[SubDwordSources],
      updateExec: Boolean
  )(inline kernel: (Long, Long) => Long): Out = (kernel(a, b), 0L)

  inline def compareModified(
      dest: ScalarRegisterIndex,
      x: InstructionSource,
      y: InstructionSource,
      modifiers: SourceModifiers,
      selection: Option[SubDwordSources],
      updateExec: Boolean,
      width: Int in (16, 32, 64)
  )(inline kernel: (Long, Long) => Long): Out = (kernel(a, b), width.toLong)

  inline def compareClass(
      dest: ScalarRegisterIndex,
      x: InstructionSource,
      y: InstructionSource,
      modifiers: SourceModifiers,
      selection: Option[SubDwordSources],
      updateExec: Boolean
  )(inline kernel: (Long, Long) => Long): Out = (kernel(a, b), 32L)

  inline def comparePair(
      dest: ScalarRegisterIndex,
      x: InstructionSource,
      y: InstructionSource,
      read: PairRead,
      modifiers: SourceModifiers,
      updateExec: Boolean
  )(inline kernel: (Long, Long) => Long): Out = (kernel(a, b), 64L)

  inline def comparePairClass(
      dest: ScalarRegisterIndex,
      x: InstructionSource,
      y: InstructionSource,
      modifiers: SourceModifiers,
      updateExec: Boolean
  )(inline kernel: (Long, Long) => Long): Out = (kernel(a, b), 64L)

  inline def outputModified(instruction: Instruction.VOutputModified)(inline kernel: Long => Long): Out =
    (kernel(a), 0L)

  inline def clampResult(instruction: Instruction.VClampResult)(inline kernel: Long => Long): Out = (kernel(a), 0L)

  inline def halfResult(instruction: Instruction.VHalfResult)(inline kernel: (Long, Long) => Long): Out =
    (kernel(a, b), 0L)

  inline def placedResult(instruction: Instruction.VSubDwordResult)(inline kernel: (Long, Long) => Long): Out =
    (kernel(a, b), 0L)

  inline def windowed(dest: VectorRegisterIndex, x: InstructionSource, y: InstructionSource, z: InstructionSource)(
      inline kernel: (Long, Long, Long) => Long
  ): Out = (kernel(a, b, c), 0L)

  inline def permuteLanes(instruction: Instruction.VPermuteLanes)(inline origin: (Long, Long, Long) => Long)(
      inline kernel: Long => Long
  ): Out = (kernel(a), origin(b, c, d))

  inline def dpp16(instruction: Instruction.VDPP, rowMask: Int, bankMask: Int, boundControl: Boolean)(
      inline origin: Long => Long
  )(inline kernel: Long => Long): Out = (kernel(a), origin(b))

  inline def dpp8(instruction: Instruction.VDPP)(inline origin: Long => Long)(inline kernel: Long => Long): Out =
    (kernel(a), origin(b))

  inline def permute(
      dest: VectorRegisterIndex,
      address: VectorRegisterIndex,
      src: VectorRegisterIndex,
      offset: Int,
      bank: RegisterBank
  )(
      inline destination: (Long, Long) => Long
  )(inline kernel: Long => Long): Out = (kernel(a), destination(b, c))

  inline def backwardPermute(
      dest: VectorRegisterIndex,
      address: VectorRegisterIndex,
      src: VectorRegisterIndex,
      offset: Int,
      bank: RegisterBank,
      fetchInactive: Boolean
  )(inline origin: Long => Long)(inline kernel: Long => Long): Out = (kernel(a), origin(b))

  inline def swizzle(dest: VectorRegisterIndex, src: VectorRegisterIndex, bank: RegisterBank)(
      inline origin: Long => Long
  )(
      inline kernel: Long => Long
  ): Out = (kernel(a), origin(b))

  inline def atomic(instruction: Instruction.Atomic): Applied[Out] = applied

  inline def localSource2(instruction: Instruction.LocalSource2): Applied[Out] = applied

  inline def compareSwap(instruction: Instruction.CompareSwap)(inline kernel: (Long, Long, Long) => Long): Out =
    (kernel(a, b, c), 0L)

  inline def scalarAtomic(instruction: Instruction.SAtomic): Applied[Out] = applied

  inline def scalarCompareSwap(instruction: Instruction.SCompareSwap)(inline kernel: (Long, Long, Long) => Long)
      : Out = (kernel(a, b, c), 0L)

  inline def multiplyMatrices(instruction: Instruction.VMatrix)(
      inline addend: Long => Long,
      inline inputA: Long => Long,
      inline inputB: Long => Long,
      inline step: (Long, Long, Long, Long, Long) => Long,
      inline result: Long => Long
  ): Out = (result(step(addend(a), inputA(b), inputB(c), d, 0L)), 0L)
}
