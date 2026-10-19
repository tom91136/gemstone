package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

// a newtype needs a ClassTag and .raw on the hot path
@SuppressWarnings(Array("baumkuchen:alias-with-operations"))
private object WorkgroupLane {
  def fromIndex(index: Int): WorkgroupLane =
    index.unsafe[Int in 0 ~ 1023].toShortExact

  type WarpBase = Int where (Between[0, 992] & MultipleOf[32])

  val warpBases: Vec[WarpBase] = Vec.range[0, 32].map(index => (index * 32).unsafe[WarpBase])

  def inWarp(base: WarpBase, lane: WarpLane): WorkgroupLane =
    fromIndex(base + lane)
}

/** A physical thread index within a NVIDIA workgroup, rather than within a warp. */
private type WorkgroupLane = Short in 0 ~ 1023
