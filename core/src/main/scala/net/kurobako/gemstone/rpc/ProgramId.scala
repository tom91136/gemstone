package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.Constraint.{LongBetween, Newtype}

/** A strictly positive program identifier. */
object ProgramId extends Newtype[LongBetween[1L, Long.MaxValue.type]]("program id") {
  extension (id: ProgramId) def next: ProgramId = unsafe(Math.addExact(id.raw, 1L))
}

type ProgramId = ProgramId.Type
