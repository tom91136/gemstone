package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.constraint.*

type ProgramId = ProgramId.Type

/** A strictly positive program identifier. */
object ProgramId extends Newtype[Long > 0] {
  extension (id: ProgramId) {
    def toLong: Long    = id.raw
    def next: ProgramId = apply(id.unwrap + 1L)
  }
}
