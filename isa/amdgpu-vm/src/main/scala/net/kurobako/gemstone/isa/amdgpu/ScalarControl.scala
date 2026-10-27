package net.kurobako.gemstone.isa.amdgpu

import EncodingConstants.Fixed.*
import VMIR.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.render.*
import net.kurobako.gemstone.vm.*

/** AMDGPU scalar control flow semantics, as requests to an [[AmdGpuMachine]]. */
private object ScalarControl {
  inline val VectorSkipShift = 28

  /** The address after the instruction, which a call saves. */
  private inline def returnAddress[V, L, D, F[_]](
      inline algebra: Algebra[V],
      inline machine: AmdGpuMachine[V, L, D, F]
  ): V =
    algebra.add(machine.pc, algebra.literal(4L))

  inline def branch[V, L, D, F[_]](inline machine: AmdGpuMachine[V, L, D, F])(
      condition: ScalarBranchCondition,
      target: Int
  ): F[Int] =
    if (condition == ScalarBranchCondition.Always) machine.jump(target)
    else machine.branch(machine.condition(condition), target)

  inline def call[V, L, D, F[_]](inline algebra: Algebra[V], inline machine: AmdGpuMachine[V, L, D, F])(
      link: ScalarRegisterIndex,
      target: Int
  ): F[Int] = machine.writePair(link, returnAddress(algebra, machine))(machine.jump(target))

  inline def getPc[V, L, D, F[_]](inline algebra: Algebra[V], inline machine: AmdGpuMachine[V, L, D, F])(
      link: ScalarRegisterIndex
  ): F[Int] = machine.writePair(link, returnAddress(algebra, machine))(machine.advance)

  inline def setPc[V, L, D, F[_]](inline machine: AmdGpuMachine[V, L, D, F])(target: InstructionSource): F[Int] =
    machine.jumpTo(machine.uniformPair(target))

  inline def swapPc[V, L, D, F[_]](inline algebra: Algebra[V], inline machine: AmdGpuMachine[V, L, D, F])(
      link: ScalarRegisterIndex,
      target: InstructionSource
  ): F[Int] = machine.callTo(link, returnAddress(algebra, machine), machine.uniformPair(target))

  inline def fork[V, L, D, F[_]](inline machine: AmdGpuMachine[V, L, D, F])(
      mask: InstructionSource,
      target: Int
  ): F[Int] =
    machine.fork(machine.uniformPair(mask), target)

  inline def forkIndirect[V, L, D, F[_]](inline machine: AmdGpuMachine[V, L, D, F])(
      mask: InstructionSource,
      target: InstructionSource
  ): F[Int] = machine.forkTo(machine.uniformPair(mask), machine.uniformPair(target))

  inline def join[V, L, D, F[_]](inline machine: AmdGpuMachine[V, L, D, F])(saved: InstructionSource): F[Int] =
    machine.join(machine.uniform(saved))

  inline def end[V, L, D, F[_]](inline machine: AmdGpuMachine[V, L, D, F]): F[Int] = machine.exit

  inline def trap[V, L, D, F[_]](inline machine: AmdGpuMachine[V, L, D, F])(code: Int): F[Int] = machine.trap(code)

  inline def halt[V, L, D, F[_]](inline machine: AmdGpuMachine[V, L, D, F])(reason: String): F[Int] =
    machine.halt(reason)

  inline def fence[V, L, D, F[_]](inline machine: AmdGpuMachine[V, L, D, F]): F[Int] = machine.fenceHost

  inline def invalidate[V, L, D, F[_]](inline machine: AmdGpuMachine[V, L, D, F]): F[Int] = machine.invalidateHostReads

  inline def writebackInvalidate[V, L, D, F[_]](inline machine: AmdGpuMachine[V, L, D, F]): F[Int] =
    machine.writebackInvalidateHost

  inline def getMode[V, L, D, F[_]](inline algebra: Algebra[V], inline machine: AmdGpuMachine[V, L, D, F])(
      dest: ScalarRegisterIndex,
      offset: Int in 0 ~ 31,
      size: Int in 1 ~ 32
  ): F[Int] = machine.writeSlot(
    dest,
    algebra.and(
      algebra.shiftRightLogical(machine.modeRegister, algebra.literal(offset.toLong)),
      algebra.literal(Bits.mask(size))
    )
  )(machine.advance)

  inline def setMode[V, L, D, F[_]](inline algebra: Algebra[V], inline machine: AmdGpuMachine[V, L, D, F])(
      offset: Int in 0 ~ 31,
      size: Int in 1 ~ 32,
      source: InstructionSource,
      writable: Int
  ): F[Int] = {
    val field = algebra.literal(((Bits.mask(size) << offset).toInt & writable).toLong & 0xffffffffL)
    machine.writeMode(
      algebra.or(
        algebra.and(machine.modeRegister, algebra.not(field)),
        algebra.and(algebra.shiftLeft(machine.uniform(source), algebra.literal(offset.toLong)), field)
      )
    )(machine.advance)
  }

  inline def setVectorSkip[V, L, D, F[_]](inline algebra: Algebra[V], inline machine: AmdGpuMachine[V, L, D, F])(
      source: InstructionSource,
      bit: InstructionSource
  ): F[Int] = machine.writeMode(
    algebra.or(
      algebra.and(machine.modeRegister, algebra.not(algebra.literal(1L << VectorSkipShift))),
      algebra.shiftLeft(
        algebra.and(
          algebra.shiftRightLogical(machine.uniform(source), algebra.and(machine.uniform(bit), algebra.literal(31L))),
          algebra.literal(1L)
        ),
        algebra.literal(VectorSkipShift.toLong)
      )
    )
  )(machine.advance)

  inline def readClock[V, L, D, F[_]](inline algebra: Algebra[V], inline machine: AmdGpuMachine[V, L, D, F])(
      dest: ScalarRegisterIndex,
      clock: Clock
  ): F[Int] = clock match {
    case Clock.ShaderCycles =>
      machine.writeSlot(dest, algebra.and(machine.clock(clock), algebra.literal(0xfffffL)))(machine.advance)
    case _ => machine.writePair(dest, machine.clock(clock))(machine.advance)
  }

  inline def readWaveId[V, L, D, F[_]](inline machine: AmdGpuMachine[V, L, D, F])(dest: ScalarRegisterIndex): F[Int] =
    machine.writeWord(dest, machine.waveId)(machine.advance)

  /** The named barrier an operand names: the low six bits where `fromM0`, sign-extended, or else the low dword. */
  private inline def barrierId[V](inline algebra: Algebra[V], inline value: V, fromM0: Boolean): V =
    if (fromM0) algebra.signExtend(value, 6) else algebra.signExtend(value, 32)

  inline def barrier[V, L, D, F[_]](inline machine: AmdGpuMachine[V, L, D, F]): F[Int] = machine.barrier

  inline def initBarrier[V, L, D, F[_]](inline algebra: Algebra[V], inline machine: AmdGpuMachine[V, L, D, F])(
      id: InstructionSource,
      fromM0: Boolean,
      count: InstructionSource
  ): F[Int] = machine.initBarrier(
    barrierId(algebra, machine.uniform(id), fromM0),
    algebra.and(algebra.shiftRightLogical(machine.uniform(count), algebra.literal(16L)), algebra.literal(0xffffL))
  )

  inline def joinBarrier[V, L, D, F[_]](inline algebra: Algebra[V], inline machine: AmdGpuMachine[V, L, D, F])(
      id: InstructionSource,
      fromM0: Boolean
  ): F[Int] = machine.joinBarrier(barrierId(algebra, machine.uniform(id), fromM0))

  inline def leaveBarrier[V, L, D, F[_]](inline machine: AmdGpuMachine[V, L, D, F]): F[Int] =
    machine.leaveBarrier(left => machine.writeCarry(left)(machine.advance))

  inline def signal[V, L, D, F[_]](inline algebra: Algebra[V], inline machine: AmdGpuMachine[V, L, D, F])(
      id: InstructionSource,
      isFirst: Boolean,
      fromM0: Boolean
  ): F[Int] = {
    val value = machine.uniform(id)
    machine.signalBarrier(
      barrierId(algebra, value, fromM0),
      if (fromM0) algebra.and(algebra.shiftRightLogical(value, algebra.literal(16L)), algebra.literal(0x7fL))
      else algebra.literal(0L)
    )(first => if (isFirst) machine.writeCarry(first)(machine.advance) else machine.advance)
  }

  inline def awaitBarrier[V, L, D, F[_]](inline algebra: Algebra[V], inline machine: AmdGpuMachine[V, L, D, F])(
      id: Int
  ): F[Int] = machine.awaitBarrier(algebra.literal(id.toLong))

  inline def globalWaveSync[V, L, D, F[_]](inline machine: AmdGpuMachine[V, L, D, F])(
      sync: Instruction.GlobalWaveSync
  ): F[Int] = machine.globalWaveSync(sync.name, sync.operation, sync.offset, sync.data, sync.bank)

  inline def localCounter[V, L, D, F[_]](inline machine: AmdGpuMachine[V, L, D, F])(
      counter: Instruction.LocalCounter
  ): F[Int] = machine.localCounter(counter.offset, counter.decrement, counter.dest, counter.bank)

  /** The register `index` names, offset by `offset` and wrapped to 32 bits as a signed dword offset is. */
  private inline def relativeRegister[V](inline algebra: Algebra[V], index: Int, offset: V): V =
    algebra.signExtend(algebra.add(algebra.literal(index.toLong), offset), 32)

  /** The register `word` dwords past `index`, which a word of a move addresses. */
  private inline def wordAt[V](inline algebra: Algebra[V], index: V, word: Int): V =
    if (word == 0) index else algebra.add(index, algebra.literal(word.toLong))

  /** The dword `word` of the 64 bits `operand` names. */
  private inline def operandWord[V, L, D, F[_]](inline algebra: Algebra[V], inline machine: AmdGpuMachine[V, L, D, F])(
      operand: InstructionSource,
      word: Int
  ): V = algebra.truncate(algebra.shiftRightLogical(machine.uniformPair(operand), algebra.literal(32L * word)), 32)

  /** The `words` dwords of a move, each written as `value` gives it once the word before it is written. */
  private inline def moveWords[V, L, D, F[_]](inline algebra: Algebra[V], inline machine: AmdGpuMachine[V, L, D, F])(
      destination: V,
      origin: V,
      words: Int in (1, 2)
  )(inline value: Int => V): F[Int] = machine.requireScalars(destination, origin, words)(
    machine.writeScalarAt(destination, value(0))(
      if (words == 1) machine.advance
      else machine.writeScalarAt(algebra.add(destination, algebra.literal(1L)), value(1))(machine.advance)
    )
  )

  /** The register a move writes first, which M0 offsets where `relativeDest`. With both fields relative, M0[25:16]
    * offsets it and M0[9:0] offsets the source.
    */
  private inline def moveDestination[V, L, D, F[_]](
      inline algebra: Algebra[V],
      inline machine: AmdGpuMachine[V, L, D, F]
  )(dest: ScalarRegisterIndex, relativeSource: Boolean, relativeDest: Boolean)(inline next: V => F[Int]): F[Int] = {
    val offsets = machine.relativeOffset
    val field   =
      if (relativeSource) algebra.and(algebra.shiftRightLogical(offsets, algebra.literal(16L)), algebra.literal(0x3ffL))
      else algebra.signExtend(offsets, 32)
    machine.bind(
      if (relativeDest) relativeRegister(algebra, dest.raw, field) else algebra.literal(dest.raw.toLong)
    )(next)
  }

  private inline def moveFromScalar[V, L, D, F[_]](
      inline algebra: Algebra[V],
      inline machine: AmdGpuMachine[V, L, D, F]
  )(
      destination: V,
      index: Int,
      words: Int in (1, 2)
  ): F[Int] = moveWords(algebra, machine)(destination, algebra.literal(index.toLong), words)(word =>
    machine.scalarAt(algebra.literal((index + word).toLong))
  )

  private inline def moveFromRelativeScalar[V, L, D, F[_]](
      inline algebra: Algebra[V],
      inline machine: AmdGpuMachine[V, L, D, F]
  )(
      destination: V,
      index: Int,
      src: InstructionSource,
      words: Int in (1, 2),
      relativeDest: Boolean
  ): F[Int] = {
    val offsets      = machine.relativeOffset
    val sourceOffset =
      if (relativeDest) algebra.and(offsets, algebra.literal(0x3ffL)) else algebra.signExtend(offsets, 32)
    machine.bind(relativeRegister(algebra, index, sourceOffset)) { origin =>
      moveWords(algebra, machine)(destination, origin, words)(word =>
        algebra.select(
          algebra.lessThan(origin, algebra.literal(0L), signed = true),
          operandWord(algebra, machine)(src, word),
          machine.scalarAt(wordAt(algebra, origin, word))
        )
      )
    }
  }

  private inline def moveFromOperand[V, L, D, F[_]](
      inline algebra: Algebra[V],
      inline machine: AmdGpuMachine[V, L, D, F]
  )(
      destination: V,
      src: InstructionSource,
      words: Int in (1, 2)
  ): F[Int] = moveWords(algebra, machine)(destination, algebra.literal(-1L), words)(word =>
    operandWord(algebra, machine)(src, word)
  )

  private inline def moveFault[V, L, D, F[_]](inline algebra: Algebra[V], inline machine: AmdGpuMachine[V, L, D, F])(
      src: InstructionSource
  ): F[Int] = machine.require(algebra.literal(0L), "s_movrels requires a scalar register source")(
    Fault.Backend(s"s_movrels requires a scalar register source, found $src")
  )(machine.advance)

  /** The move of `words` SGPRs from `src` to `dest`, which M0 offsets the source of where `relativeSource` and the
    * destination of where `relativeDest`. With both, M0[9:0] offsets the source and M0[25:16] the destination.
    */
  inline def moveRelative[V, L, D, F[_]](inline algebra: Algebra[V], inline machine: AmdGpuMachine[V, L, D, F])(
      dest: ScalarRegisterIndex,
      src: InstructionSource,
      words: Int in (1, 2),
      relativeSource: Boolean,
      relativeDest: Boolean
  ): F[Int] = moveDestination(algebra, machine)(dest, relativeSource, relativeDest) { destination =>
    src match {
      case InstructionSource.Scalar(index) if relativeSource =>
        moveFromRelativeScalar(algebra, machine)(destination, index.raw, src, words, relativeDest)
      case InstructionSource.Scalar(index) => moveFromScalar(algebra, machine)(destination, index.raw, words)
      case _ if relativeSource             => moveFault(algebra, machine)(src)
      case _                               => moveFromOperand(algebra, machine)(destination, src, words)
    }
  }

  /** The first pass of a subvector loop, which skips to `target` where EXEC is empty. Otherwise it runs EXEC's low
    * half, if that has lanes, and saves the high half in `state`.
    */
  inline def subvectorLoopBegin[V, L, D, F[_]](inline algebra: Algebra[V], inline machine: AmdGpuMachine[V, L, D, F])(
      state: ScalarRegisterIndex,
      target: Int
  ): F[Int] = machine.bind(machine.uniformPair(InstructionSource.Scalar(ExecLo))) { exec =>
    val zero   = algebra.literal(0L)
    val empty  = algebra.equal(exec, zero)
    val low    = algebra.truncate(exec, 32)
    val high   = algebra.shiftRightLogical(exec, algebra.literal(32L))
    val lowSet = algebra.notEqual(low, zero)
    val held   = machine.uniform(InstructionSource.Scalar(state))
    machine.writeWord(state, algebra.select(empty, held, algebra.select(lowSet, high, zero)))(
      machine.writePair(ExecLo, algebra.select(lowSet, low, exec))(machine.branch(empty, target))
    )
  }

  /** The end of a subvector loop. A wave64 whose high half is still to run saves the low half in `state` and jumps back
    * to `target` with the high half as EXEC. Otherwise EXEC's halves are joined from `state` where the high half has
    * lanes.
    */
  inline def subvectorLoopEnd[V, L, D, F[_]](inline algebra: Algebra[V], inline machine: AmdGpuMachine[V, L, D, F])(
      state: ScalarRegisterIndex,
      target: Int,
      wave64: Boolean
  ): F[Int] = machine.bind(machine.uniformPair(InstructionSource.Scalar(ExecLo))) { exec =>
    machine.bind(machine.uniform(InstructionSource.Scalar(state))) { held =>
      val zero    = algebra.literal(0L)
      val low     = algebra.truncate(exec, 32)
      val high    = algebra.shiftRightLogical(exec, algebra.literal(32L))
      val hasHigh = algebra.notEqual(high, zero)
      val upper   = algebra.or(algebra.shiftLeft(high, algebra.literal(32L)), held)
      val joined  = algebra.select(hasHigh, upper, exec)
      if (wave64)
        machine.bind(algebra.and(algebra.equal(high, zero), algebra.notEqual(held, zero))) { second =>
          machine.writeWord(state, algebra.select(second, low, held))(
            machine.writePair(ExecLo, algebra.select(second, algebra.shiftLeft(held, algebra.literal(32L)), joined))(
              machine.branch(second, target)
            )
          )
        }
      else machine.writePair(ExecLo, joined)(machine.advance)
    }
  }

  inline def subvectorLoop[V, L, D, F[_]](inline algebra: Algebra[V], inline machine: AmdGpuMachine[V, L, D, F])(
      begin: Boolean,
      state: ScalarRegisterIndex,
      target: Int,
      wave64: Boolean
  ): F[Int] =
    if (begin) subvectorLoopBegin(algebra, machine)(state, target)
    else subvectorLoopEnd(algebra, machine)(state, target, wave64)

  /** The semantics of `instruction`, rendered over `machine`, or none for an instruction they do not cover. A
    * displacement is rendered as the operand of the instruction, so the locations the instruction names read as that
    * operand does. A wave64 subvector loop has its second pass where `wave64`.
    */
  def render(
      instruction: Instruction,
      machine: AmdGpuMachineRender,
      writable: Int,
      wave64: Boolean
  ): Option[Term.Steps] =
    instruction match {
      case Instruction.BranchRelative(condition, displacement) => Some(branch(machine)(condition, displacement))
      case Instruction.CallRelative(link, displacement)        => Some(call(Render, machine)(link, displacement))
      case Instruction.SGetPc(link, _)                         => Some(getPc(Render, machine)(link))
      case Instruction.SSetPc(target)                          => Some(setPc(machine)(target))
      case Instruction.SSwapPc(link, target)                   => Some(swapPc(Render, machine)(link, target))
      case Instruction.ForkRelative(mask, displacement)        => Some(fork(machine)(mask, displacement))
      case Instruction.ForkIndirect(mask, target)              => Some(forkIndirect(machine)(mask, target))
      case Instruction.Join(saved)                             => Some(join(machine)(saved))
      case Instruction.End                                     => Some(end(machine))
      case Instruction.Trap(code)                              => Some(trap(machine)(code))
      case Instruction.Barrier                                 => Some(barrier(machine))
      case Instruction.BarrierSignal(id, isFirst, fromM0)      => Some(signal(Render, machine)(id, isFirst, fromM0))
      case Instruction.BarrierInit(id, fromM0, count)          => Some(initBarrier(Render, machine)(id, fromM0, count))
      case Instruction.BarrierJoin(id, fromM0)                 => Some(joinBarrier(Render, machine)(id, fromM0))
      case Instruction.BarrierLeave                            => Some(leaveBarrier(machine))
      case Instruction.BarrierWait(id)                         => Some(awaitBarrier(Render, machine)(id))
      case sync: Instruction.GlobalWaveSync                    => Some(globalWaveSync(machine)(sync))
      case Instruction.Halt(reason)                            => Some(halt(machine)(reason))
      case Instruction.CacheInvalidate                         => Some(invalidate(machine))
      case Instruction.CacheWritebackInvalidate                => Some(writebackInvalidate(machine))
      case Instruction.SGetMode(dest, offset, size)            => Some(getMode(Render, machine)(dest, offset, size))
      case Instruction.SSetMode(offset, size, source)          =>
        Some(setMode(Render, machine)(offset, size, source, writable))
      case Instruction.SetVectorSkip(src, bit)  => Some(setVectorSkip(Render, machine)(src, bit))
      case Instruction.SReadClock(dest, clock)  => Some(readClock(Render, machine)(dest, clock))
      case Instruction.SReadWaveId(dest)        => Some(readWaveId(machine)(dest))
      case counter: Instruction.LocalCounter    => Some(localCounter(machine)(counter))
      case Instruction.Wait | Instruction.Sleep => Some(fence(machine))
      case _                                    => relativeRender(instruction, machine, wave64)
    }

  // kept out of render, whose inlined expansions of these would push it past HotSpot's huge-method limit
  private def relativeRender(
      instruction: Instruction,
      machine: AmdGpuMachineRender,
      wave64: Boolean
  ): Option[Term.Steps] =
    instruction match {
      case move: Instruction.SMoveRelative                       => Some(moveRender(move, machine))
      case loop: Instruction.SubvectorLoopRelative if loop.begin => Some(beginRender(loop, machine))
      case loop: Instruction.SubvectorLoopRelative               => Some(endRender(loop, machine, wave64))
      case _                                                     => None
    }

  private def moveRender(move: Instruction.SMoveRelative, machine: AmdGpuMachineRender): Term.Steps = move.src match {
    case InstructionSource.Scalar(index) if move.relativeSource => relativeScalarRender(move, index.raw, machine)
    case InstructionSource.Scalar(index)                        => scalarRender(move, index.raw, machine)
    case _ if move.relativeSource                               => faultRender(move.src, machine)
    case _                                                      => operandRender(move, machine)
  }

  private def relativeScalarRender(
      move: Instruction.SMoveRelative,
      index: Int,
      machine: AmdGpuMachineRender
  ): Term.Steps =
    moveDestination(Render, machine)(move.dest, move.relativeSource, move.relativeDest)(destination =>
      moveFromRelativeScalar(Render, machine)(destination, index, move.src, move.words, move.relativeDest)
    )

  private def scalarRender(move: Instruction.SMoveRelative, index: Int, machine: AmdGpuMachineRender): Term.Steps =
    moveDestination(Render, machine)(move.dest, move.relativeSource, move.relativeDest)(destination =>
      moveFromScalar(Render, machine)(destination, index, move.words)
    )

  private def faultRender(src: InstructionSource, machine: AmdGpuMachineRender): Term.Steps =
    moveFault(Render, machine)(src)

  private def operandRender(move: Instruction.SMoveRelative, machine: AmdGpuMachineRender): Term.Steps =
    moveDestination(Render, machine)(move.dest, move.relativeSource, move.relativeDest)(destination =>
      moveFromOperand(Render, machine)(destination, move.src, move.words)
    )

  private def beginRender(loop: Instruction.SubvectorLoopRelative, machine: AmdGpuMachineRender): Term.Steps =
    subvectorLoopBegin(Render, machine)(loop.state, loop.displacement)

  private def endRender(
      loop: Instruction.SubvectorLoopRelative,
      machine: AmdGpuMachineRender,
      wave64: Boolean
  ): Term.Steps = subvectorLoopEnd(Render, machine)(loop.state, loop.displacement, wave64)
}
