package net.kurobako.gemstone.isa.spirv

/** The blocks an OpSwitch selects between: the literals, compared under `mask` to the selector, and the block of each,
  * with the block for any other selector.
  */
private class Cases(val mask: Long, val literals: Array[Long], val blocks: Array[Block], val default: Block)
