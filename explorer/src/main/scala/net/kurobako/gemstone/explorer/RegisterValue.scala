package net.kurobako.gemstone.explorer

/** A register's value at one issue: which lane of the unit holds it, how the ISA names it, and its bits. A lane of `-1`
  * names a scalar the whole wave shares.
  */
case class RegisterValue(lane: Int, name: String, bits: Long)
