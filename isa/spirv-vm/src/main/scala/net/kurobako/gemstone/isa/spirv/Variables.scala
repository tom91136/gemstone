package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.semantics.prelude.*

/** The private storage rendered definitions request from the machine. */
private[isa] trait Variables {
  def variable(): UInt[64]
  def initialisedVariable(initialiser: Any): UInt[64]
  def variableLengthArray(count: Any): UInt[64]
  def saveMemory(): UInt[64]
  def restoreMemory(pointer: Any): Unit
  def memorySemantics(source: Any): UInt[64]
}
