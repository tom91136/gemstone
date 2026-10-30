package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*

/** The consumers of AMDGPU semantics, one method per operand shape: execution runs the shape's lane loop or scalar
  * step, description names its operands. A shape takes the IR operands it reads and writes, with the implicit state it
  * touches (SCC, VCC, EXEC, M0, the lane index) fixed by the shape, and the kernels the semantics define over the
  * values it reads. Dword operands arrive as their low 32 bits unless a shape says otherwise; register pairs as 64
  * bits; source modifiers apply before the kernel. Flag kernels see the result last.
  */
private trait AMDGPUSinks[V, R] {

  /** `body` as a unit of its own, which execution compiles to a method of its own. */
  inline def section(inline body: R): R

  /** The MODE register's dword. */
  inline def modeRegister: V

  /** `before`, then `after`. */
  inline def sequence(inline before: R, inline after: R): R

  /** SOP2 on SRC0 and SRC1 dwords and SCC into a dword. */
  inline def scalarDword(dest: ScalarRegisterIndex, a: InstructionSource, b: InstructionSource)(
      inline result: (V, V, V) => V
  ): R

  /** [[scalarDword]] that also writes SCC. */
  inline def scalarDwordFlagged(dest: ScalarRegisterIndex, a: InstructionSource, b: InstructionSource)(
      inline result: (V, V, V) => V,
      inline flag: (V, V, V, V) => V
  ): R

  /** SOP2 on SRC0 and SRC1 register pairs and SCC into a register pair. */
  inline def scalarPair(dest: ScalarRegisterIndex, a: InstructionSource, b: InstructionSource)(
      inline result: (V, V, V) => V
  ): R

  inline def scalarPairFlagged(dest: ScalarRegisterIndex, a: InstructionSource, b: InstructionSource)(
      inline result: (V, V, V) => V,
      inline flag: (V, V, V, V) => V
  ): R

  /** [[scalarPairFlagged]] on a SRC0 register pair and a SRC1 dword. */
  inline def scalarPairDwordFlagged(dest: ScalarRegisterIndex, a: InstructionSource, b: InstructionSource)(
      inline result: (V, V, V) => V,
      inline flag: (V, V, V, V) => V
  ): R

  /** SOP1 on SRC0, a dword or a pair, and the destination's previous dword, into a dword or a pair. */
  inline def scalarUnary(dest: ScalarRegisterIndex, a: InstructionSource, readsPair: Boolean, writesPair: Boolean)(
      inline result: (V, V) => V
  ): R

  inline def scalarUnaryFlagged(
      dest: ScalarRegisterIndex,
      a: InstructionSource,
      readsPair: Boolean,
      writesPair: Boolean
  )(inline result: (V, V) => V, inline flag: (V, V, V) => V): R

  /** SOPK on the destination's previous dword and a sign-extended immediate, writing the dword and SCC. */
  inline def scalarImmediate(dest: ScalarRegisterIndex, immediate: Long)(
      inline result: (V, V) => V,
      inline flag: (V, V, V) => V
  ): R

  /** S_*_SAVEEXEC on the source and EXEC, read at `width` bits: the previous EXEC to `dest`, the result to EXEC and the
    * flag to SCC.
    */
  inline def saveExec(dest: ScalarRegisterIndex, a: InstructionSource, width: Int in (32, 64))(
      inline result: (V, V) => V,
      inline flag: (V, V, V) => V
  ): R

  /** S_*_WREXEC on the source and EXEC, read at `width` bits: the result to EXEC and `dest` and the flag to SCC. */
  inline def writeExec(dest: ScalarRegisterIndex, a: InstructionSource, width: Int in (32, 64))(
      inline result: (V, V) => V,
      inline flag: (V, V, V) => V
  ): R

  /** S_CMP on dwords or register pairs into SCC. */
  inline def scalarCompare(a: InstructionSource, b: InstructionSource, pair: Boolean)(inline kernel: (V, V) => V): R

  /** A scalar move of a dword or a register pair. */
  inline def scalarMove(dest: ScalarRegisterIndex, a: InstructionSource, pair: Boolean)(inline kernel: V => V): R

  /** A scalar binary32 operation on a modified dword. */
  inline def scalarModified(dest: ScalarRegisterIndex, a: InstructionSource, modifiers: SourceModifiers)(
      inline kernel: V => V
  ): R

  inline def scalarTernary(
      dest: ScalarRegisterIndex,
      a: InstructionSource,
      b: InstructionSource,
      c: InstructionSource
  )(inline kernel: (V, V, V) => V): R

  /** The first active lane's SRC0 into a scalar; an empty EXEC faults. */
  inline def readFirstLane(dest: ScalarRegisterIndex, a: InstructionSource)(inline kernel: V => V): R

  /** The SRC0 of the lane `index` selects from the lane operand and the wave size into a scalar. */
  inline def readLane(dest: ScalarRegisterIndex, a: InstructionSource, lane: InstructionSource)(
      inline index: (V, V) => V
  )(inline kernel: V => V): R

  /** SRC0 into the destination of the lane `index` selects. */
  inline def writeLane(dest: VectorRegisterIndex, a: InstructionSource, lane: InstructionSource)(
      inline index: (V, V) => V
  )(inline kernel: V => V): R

  /** SRC0 of each active lane, as it reads without truncation, into `dest` in `bank`. */
  inline def vectorUnary(bank: RegisterBank, dest: VectorRegisterIndex, a: InstructionSource)(inline kernel: V => V): R

  /** The accumulator register `src` into `dest` in `destinationBank`. */
  inline def accumulatorMove(destinationBank: RegisterBank, dest: VectorRegisterIndex, src: VectorRegisterIndex)(
      inline kernel: V => V
  ): R

  /** V_MOVRELS: the vector register SRC0 names, offset by `field` of M0, selected as SDWA says; an offset beyond the
    * last register reads register 0, and any other source faults.
    */
  inline def moveRelative(dest: VectorRegisterIndex, a: InstructionSource, field: RelativeField)(
      inline kernel: V => V
  ): R

  /** V_MOVRELD: SRC0 into the destination `field` of M0 offsets, which a write beyond the last register leaves out.
    * SRC0 is the vector register `sourceField` of M0 offsets when it is set, as V_MOVRELSD does.
    */
  inline def moveRelativeDestination(
      dest: VectorRegisterIndex,
      a: InstructionSource,
      sourceField: Option[RelativeField],
      field: RelativeField
  )(
      inline kernel: V => V
  ): R

  /** V_SWAPREL: each of the registers M0[9:0] and M0[25:16] offset takes the other's dword through the kernel, and
    * neither does where either is beyond the last.
    */
  inline def swapRelative(dest: VectorRegisterIndex, src: VectorRegisterIndex)(inline kernel: V => V): R

  /** V_SWAP: each register takes the other's dword through the kernel; other sources fault. */
  inline def swap(dest: VectorRegisterIndex, a: InstructionSource)(inline kernel: V => V): R

  /** V_SWAP_B16: the destination takes `dest` of its and the source's previous dwords; the source then takes `src` of
    * its dword, the destination's when they are one register, and the destination's previous dword.
    */
  inline def swapHalves(instruction: Instruction.VSwapHalves)(inline dest: (V, V) => V, inline src: (V, V) => V): R

  /** SRC0's dword modified at `width` bits into a dword. */
  inline def modifiedUnary(
      dest: VectorRegisterIndex,
      a: InstructionSource,
      modifiers: SourceModifiers,
      width: Int in (16, 32, 64)
  )(inline kernel: V => V): R

  inline def wideningUnary(dest: VectorRegisterIndex, a: InstructionSource, modifiers: SourceModifiers)(
      inline kernel: V => V
  ): R

  /** A modified floating-point register pair into a dword. */
  inline def narrowingUnary(dest: VectorRegisterIndex, a: InstructionSource, modifiers: SourceModifiers)(
      inline kernel: V => V
  ): R

  inline def pairUnary(dest: VectorRegisterIndex, a: InstructionSource, read: PairRead, modifiers: SourceModifiers)(
      inline kernel: V => V
  ): R

  inline def vectorBinary(dest: VectorRegisterIndex, a: InstructionSource, b: InstructionSource)(
      inline kernel: (V, V) => V
  ): R

  /** Modified SRC0 and SRC1 and the destination's previous dword. */
  inline def vectorFloat(
      dest: VectorRegisterIndex,
      a: InstructionSource,
      b: InstructionSource,
      modifiers: SourceModifiers,
      width: Int in (16, 32, 64)
  )(inline kernel: (V, V, V) => V): R

  /** Three modified sources; the destination takes the result's low dword. */
  inline def vectorTernary(
      dest: VectorRegisterIndex,
      a: InstructionSource,
      b: InstructionSource,
      c: InstructionSource,
      modifiers: SourceModifiers,
      width: Int in (16, 32, 64)
  )(inline kernel: (V, V, V) => V): R

  /** SRC0, SRC1 and the lane's VCC bit, which the flag replaces. */
  inline def vectorCarry(dest: VectorRegisterIndex, a: InstructionSource, b: InstructionSource)(
      inline result: (V, V, V) => V,
      inline flag: (V, V, V, V) => V
  ): R

  /** SRC0, SRC1 and the lane's bit of the carry-in mask, zero without one; the flags form the mask `carryDest`
    * receives.
    */
  inline def maskCarry(
      dest: VectorRegisterIndex,
      carryDest: ScalarRegisterIndex,
      a: InstructionSource,
      b: InstructionSource,
      carryIn: Option[InstructionSource]
  )(inline result: (V, V, V) => V, inline flag: (V, V, V, V) => V): R

  /** SDWA on the selected source parts; the result lands in the destination selection. */
  inline def subDword(instruction: Instruction.VBinarySubDword)(inline kernel: (V, V) => V): R

  /** SDWA on the selected source parts and the lane's VCC bit, which the flag replaces. */
  inline def subDwordFlagged(instruction: Instruction.VBinarySubDword)(
      inline result: (V, V, V) => V,
      inline flag: (V, V, V, V) => V
  ): R

  /** SDWA on the selected source parts and the lane's VCC bit. */
  inline def subDwordConditional(instruction: Instruction.VBinarySubDword)(inline result: (V, V, V) => V): R

  /** Packed 16-bit operations on the low halves, then on the halves the high selection picks. */
  inline def packedBinary(instruction: Instruction.VPackedBinary)(inline kernel: (V, V) => V): R

  /** Modified SRC0 and SRC1 and the lane's bit of the condition mask. */
  inline def select(
      dest: VectorRegisterIndex,
      a: InstructionSource,
      b: InstructionSource,
      condition: InstructionSource,
      modifiers: SourceModifiers
  )(inline kernel: (V, V, V) => V): R

  /** The mask, the lane index and the base. */
  inline def maskCount(dest: VectorRegisterIndex, mask: InstructionSource, base: InstructionSource)(
      inline kernel: (V, V, V) => V
  ): R

  /** A VOPD half's sources, FMA addend and VCC bit; the first half's results wait for the second half to read. */
  inline def dualFirst(half: DualVector)(inline kernel: (V, V, V, V) => V): R

  inline def dualSecond(half: DualVector, other: DualVector)(inline kernel: (V, V, V, V) => V): R

  /** Whole register blocks: `count` dwords of each source from the vector register it names, the lone dword of any
    * other source, and the `width` dwords from `dest` before the write, into `width` dwords from `dest`.
    */
  inline def block(dest: VectorRegisterIndex, width: Int, sources: Vec[(InstructionSource, Int)])(
      inline kernel: (IndexedSeq[V], IndexedSeq[V]) => IndexedSeq[V]
  ): R

  /** Three source dwords and the destination's previous dword. */
  inline def accumulating(
      dest: VectorRegisterIndex,
      a: InstructionSource,
      b: InstructionSource,
      c: InstructionSource
  )(inline kernel: (V, V, V, V) => V): R

  /** Packed binary32 on the dwords each half's selection picks, negated as its negate bits say. */
  inline def packedSingle(instruction: Instruction.VPackedF32)(inline kernel: (V, V, V) => V): R

  /** Packed binary16 on the halves each half's selection picks, negated as its negate bits say. */
  inline def packedHalf(instruction: Instruction.VPackedTernary)(inline kernel: (V, V, V) => V): R

  /** The binary16 source halves OP_SEL picks, modified at 16 bits. */
  inline def halves(instruction: Instruction.VPackB32F16)(inline kernel: (V, V) => V): R

  inline def pairBinary(
      dest: VectorRegisterIndex,
      a: InstructionSource,
      b: InstructionSource,
      read: PairRead,
      modifiers: SourceModifiers
  )(inline kernel: (V, V) => V): R

  /** A modified register pair and a raw dword into a register pair. */
  inline def pairDword(
      dest: VectorRegisterIndex,
      a: InstructionSource,
      b: InstructionSource,
      read: PairRead,
      modifiers: SourceModifiers
  )(inline kernel: (V, V) => V): R

  /** Three modified floating-point register pairs into a register pair. */
  inline def pairTernary(
      dest: VectorRegisterIndex,
      a: InstructionSource,
      b: InstructionSource,
      c: InstructionSource,
      modifiers: SourceModifiers
  )(inline kernel: (V, V, V) => V): R

  /** A register pair, a dword and a register pair, as integers, into a register pair. */
  inline def pairShiftAdd(
      dest: VectorRegisterIndex,
      a: InstructionSource,
      b: InstructionSource,
      c: InstructionSource
  )(inline kernel: (V, V, V) => V): R

  /** An integer register pair and two raw dwords into a dword. */
  inline def windowed(dest: VectorRegisterIndex, a: InstructionSource, b: InstructionSource, c: InstructionSource)(
      inline kernel: (V, V, V) => V
  ): R

  /** V_PERMLANE: each active lane reads the source of the lane `origin` finds from its index and the two selector
    * dwords; a lane it cannot read gives zero under bound control and leaves the destination otherwise.
    */
  inline def permuteLanes(instruction: Instruction.VPermuteLanes)(inline origin: (V, V, V) => V)(
      inline kernel: V => V
  ): R

  /** V_PERMLANE*_SWAP: each active lane reads the other register of the lane across its `rows` boundary. */
  inline def swapLanes(instruction: Instruction.VPermlaneSwap)(inline kernel: V => V): R

  /** Two raw dwords and an integer register pair into a register pair; the flags form `carryDest`'s mask. */
  inline def multiplyAdd64(
      dest: VectorRegisterIndex,
      carryDest: ScalarRegisterIndex,
      a: InstructionSource,
      b: InstructionSource,
      c: InstructionSource
  )(inline result: (V, V, V, V) => V, inline flag: (V, V, V, V, V) => V): R

  /** Modified source dwords and the lane's VCC bit. */
  inline def scaled(
      dest: VectorRegisterIndex,
      a: InstructionSource,
      b: InstructionSource,
      c: InstructionSource,
      modifiers: SourceModifiers
  )(inline result: (V, V, V, V) => V): R

  /** [[scaled]] whose flags form the mask `conditionDest` receives. */
  inline def scaledFlagged(
      dest: VectorRegisterIndex,
      conditionDest: ScalarRegisterIndex,
      a: InstructionSource,
      b: InstructionSource,
      c: InstructionSource,
      modifiers: SourceModifiers
  )(inline result: (V, V, V, V) => V, inline flag: (V, V, V, V, V) => V): R

  inline def pairScaled(
      dest: VectorRegisterIndex,
      a: InstructionSource,
      b: InstructionSource,
      c: InstructionSource,
      modifiers: SourceModifiers
  )(inline result: (V, V, V, V) => V): R

  inline def pairScaledFlagged(
      dest: VectorRegisterIndex,
      conditionDest: ScalarRegisterIndex,
      a: InstructionSource,
      b: InstructionSource,
      c: InstructionSource,
      modifiers: SourceModifiers
  )(inline result: (V, V, V, V) => V, inline flag: (V, V, V, V, V) => V): R

  /** V_CMP on the sources as they read, or their SDWA selections; the lanes' results form the mask `dest` receives and,
    * when `updateExec`, EXEC.
    */
  inline def compare(
      dest: ScalarRegisterIndex,
      a: InstructionSource,
      b: InstructionSource,
      selection: Option[SubDwordSources],
      updateExec: Boolean
  )(inline kernel: (V, V) => V): R

  /** [[compare]] on sources modified at `width` bits. */
  inline def compareModified(
      dest: ScalarRegisterIndex,
      a: InstructionSource,
      b: InstructionSource,
      modifiers: SourceModifiers,
      selection: Option[SubDwordSources],
      updateExec: Boolean,
      width: Int in (16, 32, 64)
  )(inline kernel: (V, V) => V): R

  /** A modified binary32 SRC0, or its SDWA selection, against SRC1's dword as a class mask. */
  inline def compareClass(
      dest: ScalarRegisterIndex,
      a: InstructionSource,
      b: InstructionSource,
      modifiers: SourceModifiers,
      selection: Option[SubDwordSources],
      updateExec: Boolean
  )(inline kernel: (V, V) => V): R

  inline def comparePair(
      dest: ScalarRegisterIndex,
      a: InstructionSource,
      b: InstructionSource,
      read: PairRead,
      modifiers: SourceModifiers,
      updateExec: Boolean
  )(inline kernel: (V, V) => V): R

  /** A modified binary64 SRC0 against SRC1's dword as a class mask. */
  inline def comparePairClass(
      dest: ScalarRegisterIndex,
      a: InstructionSource,
      b: InstructionSource,
      modifiers: SourceModifiers,
      updateExec: Boolean
  )(inline kernel: (V, V) => V): R

  /** The operation MODE leaves unscaled, or the scaling one then the kernel on its `width`-bit result. */
  inline def outputModified(instruction: Instruction.VOutputModified)(inline kernel: V => V): R

  /** The operation, then the kernel on its `width`-bit result. */
  inline def clampResult(instruction: Instruction.VClampResult)(inline kernel: V => V): R

  /** The operation, then the kernel on the destination's previous dword and the operation's 16-bit result. */
  inline def halfResult(instruction: Instruction.VHalfResult)(inline kernel: (V, V) => V): R

  /** The operation, then the kernel on the destination's previous dword and the operation's result. */
  inline def placedResult(instruction: Instruction.VSubDwordResult)(inline kernel: (V, V) => V): R

  /** DPP16: the lanes the row and bank masks enable read the lane `origin` names, or none; the operation runs on them.
    */
  inline def dpp16(instruction: Instruction.VDPP, rowMask: Int, bankMask: Int, boundControl: Boolean)(
      inline origin: V => V
  )(inline kernel: V => V): R

  /** DPP8: each lane reads the lane `origin` names; the operation runs on them. */
  inline def dpp8(instruction: Instruction.VDPP)(inline origin: V => V)(inline kernel: V => V): R

  /** DS_PERMUTE: each active lane's source to the lane `destination` finds from its address, its index and the wave
    * size.
    */
  inline def permute(
      dest: VectorRegisterIndex,
      address: VectorRegisterIndex,
      src: VectorRegisterIndex,
      offset: Int,
      bank: RegisterBank
  )(
      inline destination: (V, V, V) => V
  )(inline kernel: V => V): R

  /** DS_BPERMUTE: each active lane reads the source of the lane `origin` finds from its address and index; an inactive
    * one reads as zero unless `fetchInactive`, as DS_BPERMUTE_FI_B32 has it.
    */
  inline def backwardPermute(
      dest: VectorRegisterIndex,
      address: VectorRegisterIndex,
      src: VectorRegisterIndex,
      offset: Int,
      bank: RegisterBank,
      fetchInactive: Boolean
  )(inline origin: (V, V) => V)(inline kernel: V => V): R

  /** DS_SWIZZLE: each active lane reads the source of the lane `origin` finds from its index. */
  inline def swizzle(dest: VectorRegisterIndex, src: VectorRegisterIndex, bank: RegisterBank)(
      inline origin: V => V
  )(
      inline kernel: V => V
  ): R

  /** An atomic read-modify-write on each active lane, from the old value and the source's dword or register pair to the
    * value stored; the old value returns to the destination when the instruction asks for it.
    */
  inline def atomic(instruction: Instruction.Atomic): Sink.Binary[V, R]

  /** A DS source-2 update on each active lane, from the old value and the other one [[IntegerSemantics.source2Address]]
    * finds, to the value stored.
    */
  inline def localSource2(instruction: Instruction.LocalSource2): Sink.Binary[V, R]

  /** A compare-and-swap on each active lane, from the old value, the comparand and the data to the value stored. */
  inline def compareSwap(instruction: Instruction.CompareSwap)(inline kernel: (V, V, V) => V): R

  /** A scalar read-modify-write, once for the wave, from the old value and the data SGPRs to the value stored; the old
    * value returns to the data SGPRs when the instruction asks for it.
    */
  inline def scalarAtomic(instruction: Instruction.SAtomic): Sink.Binary[V, R]

  /** A scalar compare-and-swap, from the old value, the comparand and the data to the value stored. */
  inline def scalarCompareSwap(instruction: Instruction.SCompareSwap)(inline kernel: (V, V, V) => V): R

  /** A matrix instruction on a complete wave: each element of D this lane holds folds `step` over K from `addend` of
    * its element of C, with the bits of the elements of A's row and B's column the shape and the broadcasts lay out,
    * after the instruction's negations and absolute value and `inputA` and `inputB`'s conversion, and the E8M0 scales
    * of A and B for block-scaled products; `result` makes the element of D from the sum. A chained shape's `step` takes
    * two consecutive elements of K, the second pair in place of the scales, and a grouped shape's the index of K in
    * place of A's scale.
    */
  inline def multiplyMatrices(instruction: Instruction.VMatrix)(
      inline addend: V => V,
      inline inputA: V => V,
      inline inputB: V => V,
      inline step: (V, V, V, V, V) => V,
      inline result: V => V
  ): R
}
