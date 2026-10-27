package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.vm.*

private object AmdGpuMachine {
  type Flow[A] = Either[Fault, A]
}

/** The machine operations AMDGPU semantics request: those of [[MachineAlgebra]] over the scalar state of a wave. An
  * operand is an instruction source, a write names a scalar register, a program location is an instruction index to
  * execute and a displacement in dwords from the next instruction to render, and the step an instruction yields is the
  * one the wave interpreter takes. The wave takes a branch as a whole, so a request for control names a wave-uniform
  * condition.
  */
private trait AmdGpuMachine[V, L, D, F[_]]
    extends MachineAlgebra[V, InstructionSource, ScalarRegisterIndex, L, Int, D, Int, F] {

  /** 1 where SCC, VCC or EXEC satisfies `condition`, otherwise 0. */
  inline def condition(condition: ScalarBranchCondition): V

  /** The 64 bits `operand` names: the register pair it starts, or the dword or constant it holds. */
  inline def uniformPair(operand: InstructionSource): V

  /** `value` written to the register pair that starts at `target`, then `next`. */
  inline def writePair(target: ScalarRegisterIndex, value: V)(inline next: => F[Int]): F[Int]

  /** The wave continues at the function `target` names with `returnAddress` in the register pair that starts at `link`.
    * The instance may run a function the code object calls but does not contain in its place and continue after the
    * call; it faults if `target` names neither.
    */
  inline def callTo(link: ScalarRegisterIndex, returnAddress: V, target: V): F[Int]

  /** The lanes of EXEC that `mask` has set continue at `taken` and the others fall through. A side that is not EXEC
    * runs after the other and is deferred on the conditional branch stack.
    */
  inline def fork(mask: V, taken: Int): F[Int]

  /** [[fork]] to the location the address `taken` names; the instance faults if it names no instruction. */
  inline def forkTo(mask: V, taken: V): F[Int]

  /** While the conditional branch stack is above the depth `saved` holds, the wave pops the side a fork deferred and
    * continues as it.
    */
  inline def join(saved: V): F[Int]

  /** The wave traps with the `code` it was given. */
  inline def trap(code: Int): F[Int]

  /** The wave halts and nothing resumes it; the instance faults, naming `reason`. */
  inline def halt(reason: String): F[Int]

  /** The writes the kernel makes to host-mapped memory become visible to the host. */
  inline def fenceHost: F[Int]

  /** The wave arrives at its workgroup's barrier and waits for the other waves of the workgroup. */
  inline def barrier: F[Int]

  /** The named barrier `id` expects `count` arrivals. */
  inline def initBarrier(id: V, count: V): F[Int]

  /** The wave joins the named barrier `id`, which its waits then await. */
  inline def joinBarrier(id: V): F[Int]

  /** The wave drops its joined named barrier; `next` takes whether the barrier now expects no wave. */
  inline def leaveBarrier(inline next: V => F[Int]): F[Int]

  /** The wave arrives at the named barrier `id`, which expects `count` arrivals where that is not zero; `next` takes
    * whether the wave arrived first.
    */
  inline def signalBarrier(id: V, count: V)(inline next: V => F[Int]): F[Int]

  /** The wave waits until the named barrier `id` completes a phase it has not seen. */
  inline def awaitBarrier(id: V): F[Int]

  /** `value` written to SCC, then `next`. */
  inline def writeCarry(value: V)(inline next: => F[Int]): F[Int]

  /** The MODE register, which the float modes read and `s_getreg_b32` reads a field of. */
  inline def modeRegister: V

  /** `value` written to MODE, then `next`. */
  inline def writeMode(value: V)(inline next: => F[Int]): F[Int]

  /** `value` written to the scalar register `target`, then `next`. */
  inline def writeWord(target: ScalarRegisterIndex, value: V)(inline next: => F[Int]): F[Int]

  /** `value` stored in the scalar slot `target` without reaching the lane masks, then `next`. */
  inline def writeSlot(target: ScalarRegisterIndex, value: V)(inline next: => F[Int]): F[Int]

  /** The low dword of M0, which relative moves offset their SGPRs by. */
  inline def relativeOffset: V

  /** The dword the SGPR `index` names holds, as an instruction reads it. */
  inline def scalarAt(index: V): V

  /** `value` written to the SGPR `index` names, as an instruction writes a dword, then `next`. */
  inline def writeScalarAt(index: V, value: V)(inline next: => F[Int]): F[Int]

  /** `next` where a move of `words` SGPRs from `origin` to `destination` stays in the SGPR file; otherwise the instance
    * faults.
    */
  inline def requireScalars(destination: V, origin: V, words: Int)(inline next: => F[Int]): F[Int]

  /** `next` over `value` as it is now, so writes that follow do not change what it reads. */
  inline def bind(value: V)(inline next: V => F[Int]): F[Int]

  /** The value the host clock `source` reads. */
  inline def clock(source: Clock): V

  /** The wave's index among the waves of its workgroup, in the order they were created. */
  inline def waveId: V

  /** The wave drops the host-mapped reads it has cached, so later loads read memory again. */
  inline def invalidateHostReads: F[Int]

  /** The writes the kernel makes to host-mapped memory become visible to the host, and the host-mapped reads the wave
    * has cached are dropped.
    */
  inline def writebackInvalidateHost: F[Int]

  /** The wave makes a GWS operation on the resource M0[21:16] plus `offset` names, with `data` from the first active
    * lane. `name` is the instruction, which the fault for a launch without GWS resources names.
    */
  inline def globalWaveSync(
      name: String,
      operation: GlobalWaveSyncOperation,
      offset: Int,
      data: VectorRegisterIndex,
      bank: RegisterBank
  ): F[Int]

  inline def localCounter(offset: Int, decrement: Boolean, dest: VectorRegisterIndex, bank: RegisterBank): F[Int]
}
