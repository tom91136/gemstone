package net.kurobako.gemstone.rpc

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

type CompletionId = CompletionId.T
object CompletionId extends RefinedType[Long, Positive] {
  extension (id: CompletionId) def next: CompletionId = CompletionId.applyUnsafe(Math.addExact(id.value, 1L))
}
