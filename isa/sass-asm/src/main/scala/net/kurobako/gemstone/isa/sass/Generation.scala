package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.constraint.*

object Generation {
  def of(sm: Int >= 35): Generation = if (sm < 50) Kepler else if (sm < 70) Maxwell else Modern
}

/** Instruction word and bundle layout shared by an architecture family. */
enum Generation(val instructionBytes: Int in (8, 16), val bundleBytes: Int in (16, 32, 64)) derives CanEqual {
  case Kepler  extends Generation(8, 64)
  case Maxwell extends Generation(8, 32)
  case Modern  extends Generation(16, 16)

  /** Instruction slots per bundle; legacy bundles start with one control word. */
  def slots: Int = if (this == Modern) 1 else bundleBytes / 8 - 1

  /** Byte offset from a bundle start to its first instruction. */
  def controlBytes: Int = if (this == Modern) 0 else 8
}
