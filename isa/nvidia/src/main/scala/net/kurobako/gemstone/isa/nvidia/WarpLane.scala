package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

private[isa] object WarpLane {

  /** All physical lane indices, carrying their compile-time range proof. */
  val values: Vec[WarpLane] = Vec.range[0, 32]
}

/** A lane index within one 32-thread NVIDIA warp. */
private[isa] type WarpLane = Int where (AtLeast[0] & LessThan[32])
