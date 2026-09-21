package net.kurobako.gemstone.isa

import net.kurobako.gemstone.Vec

/** Immutable exported symbols used by native facades to construct process-local handles. */
final case class ProgramSymbols(kernels: Vec[KernelSymbol], globals: Vec[GlobalSymbol])
