package net.kurobako.gemstone.isa.amdgpu

import VMDispatchMemory.*
import net.kurobako.gemstone.vm.*

private object PeerCode {

  /** Another program's code, decoded for the calling wave, with that program's global bindings. */
  class Program(val kernel: VMIR.Kernel, val mappings: Array[GlobalMapping])

  val Alone: PeerCode = (_, _) => Right(Option.empty[Program])
}

/** The other programs of a session, whose code a wave may call into. */
private trait PeerCode {

  /** Program `id` as a wave of `caller` runs it, or nothing when the session has no such program. */
  def resolve(id: ProgramId, caller: CodeObjectKernel): Either[Fault, Option[PeerCode.Program]]
}
