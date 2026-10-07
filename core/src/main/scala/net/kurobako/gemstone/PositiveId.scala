package net.kurobako.gemstone

import net.kurobako.gemstone.constraint.*

/** A strictly positive identifier issued in strictly increasing order. */
trait PositiveId extends Newtype[Long > 0] {
  extension (id: Type) def next: Type = apply(id.unwrap + 1L)
}
