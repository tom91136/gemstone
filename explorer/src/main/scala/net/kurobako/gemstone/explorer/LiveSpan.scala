package net.kurobako.gemstone.explorer

/** A stretch of issues over which a register is live, and how many units kept it live there. The span covers the issues
  * before which the register is live, from `start` up to, but not including, `end`.
  */
case class LiveSpan(start: Long, end: Long, units: Int)
