/*
rule = AsmBoundary
 */
package net.kurobako.gemstone.isa.ptx

import net.kurobako.gemstone.isa.ptx.asm.Printer // assert: AsmBoundary.asm-reference

object AsmBoundaryRuntime {
  val imported  = Printer.rendered                              // assert: AsmBoundary.asm-reference
  val relative  = asm.Printer.rendered                          // assert: AsmBoundary.asm-reference
  val allowed   = AsmBoundaryUnrelated.value
}

object AsmBoundaryUnrelated {
  val value = 1
}
