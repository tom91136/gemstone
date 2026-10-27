package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import net.kurobako.gemstone.semantics.prelude.*

/** The conditional branch stack and traps of a wave, which rendered AMDGPU definitions request control from. */
private[isa] trait Control {

  /** The lanes of EXEC that `mask` has set continue at `taken` and the others fall through; a side that is not EXEC
    * runs after the other.
    */
  def fork(mask: UInt[64], taken: UInt[64]): Unit

  /** While the stack is above the depth `saved` holds, the wave pops the side a fork deferred and continues as it. */
  def join(saved: UInt[32]): Unit

  /** The wave traps with `code`. */
  def trap(code: UInt[16]): Unit

  /** The wave halts and nothing resumes it. */
  def halt(reason: String): Unit

  /** The writes the kernel makes to host-mapped memory become visible to the host. */
  def fenceHost(): Unit

  /** The wave arrives at its workgroup's barrier and waits for the other waves of the workgroup. */
  def barrier(): Unit

  /** The named barrier `id` expects `count` arrivals. */
  def initBarrier(id: UInt[64], count: UInt[64]): Unit

  /** The wave joins the named barrier `id`, which its waits then await. */
  def joinBarrier(id: UInt[64]): Unit

  /** The wave drops its joined named barrier; whether the barrier now expects no wave. */
  def leaveBarrier(): Bool

  /** The wave arrives at the named barrier `id`, which expects `count` arrivals where that is not zero; whether it
    * arrived first.
    */
  def signalBarrier(id: UInt[64], count: UInt[64]): Bool

  /** The wave waits until the named barrier `id` completes a phase it has not seen. */
  def awaitBarrier(id: UInt[64]): Unit

  /** The wave makes a GWS operation on the resource M0[21:16] plus `offset`, with `data` from the first active lane. */
  def globalWaveSync(name: String, operation: GlobalWaveSyncOperation, offset: UInt[32], data: UInt[32]): Unit

  def localCounter(decrement: Boolean, offset: UInt[32]): UInt[32]

  /** The host's monotonic clock: nanoseconds, or a tenth of them for the real-time clock. */
  def clock(source: Clock): UInt[64]

  /** The wave's index among the waves of its workgroup. */
  def waveId(): UInt[32]

  /** The wave drops the host-mapped reads it has cached, so later loads read memory again. */
  def invalidateHostReads(): Unit

  /** The dword of SGPR `index`, which a relative move reads. */
  def scalarAt(index: UInt[64]): UInt[32]

  /** Writes `value` to SGPR `index`, which a relative move writes. */
  def writeScalarAt(index: UInt[64], value: UInt[32]): Unit

  /** The wave faults unless a move of `words` SGPRs from `origin` to `destination` stays in the SGPR file. */
  def requireScalars(destination: UInt[64], origin: UInt[64], words: UInt[32]): Unit
}
