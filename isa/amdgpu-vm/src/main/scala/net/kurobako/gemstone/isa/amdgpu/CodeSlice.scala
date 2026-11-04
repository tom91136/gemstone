package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.vm.*

/** The code a wave runs: one program's decoded text, its jump targets, and the memory that program's code sees. */
private class CodeSlice(
    val program: VMIR.Kernel,
    val jumps: Array[Either[Fault, Int]],
    val memory: VMDispatchMemory
) {
  val base: Long           = program.source.textAddress
  private val length: Long = program.source.text.size.toLong

  def contains(address: Long): Boolean = address - base >= 0L && address - base < length

  def indexOf(address: Long): Option[Int] =
    if (contains(address)) program.instructionIndices.get((address - base).toInt) else None
}
