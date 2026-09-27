package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.Constraint.{LongBetween, Newtype}

/** A strictly positive completion identifier. */
object CompletionId extends Newtype[LongBetween[1L, Long.MaxValue.type]]("completion id") {
  extension (id: CompletionId) def next: CompletionId = unsafe(Math.addExact(id.raw, 1L))
}

type CompletionId = CompletionId.Type
