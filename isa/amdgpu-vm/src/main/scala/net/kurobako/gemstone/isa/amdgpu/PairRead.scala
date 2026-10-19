package net.kurobako.gemstone.isa.amdgpu

/** How a source other than a register pair reads 64 bits: as an integer, as a float whose literal is the high dword, or
  * as packed binary32 whose inline constant is one binary32.
  */
private enum PairRead derives CanEqual {
  case Integer, Floating, Packed
}
