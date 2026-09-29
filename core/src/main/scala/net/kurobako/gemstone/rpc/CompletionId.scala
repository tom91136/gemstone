package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.constraint.*

type CompletionId = CompletionId.Type

/** A strictly positive completion identifier. */
object CompletionId extends Newtype[Long > 0] {
  extension (id: CompletionId) {
    def toLong: Long       = id.raw
    def next: CompletionId = apply(id.unwrap + 1L)
  }
}
