package net.kurobako.gemstone.isa.amdgpu

/** Which lanes and register bits hold a matrix instruction's elements: the CDNA matrix core, whose wave64 layouts the
  * MFMA chapter of the CDNA ISA defines, or the gfx11 and gfx12 WMMA layouts.
  */
private[isa] enum MatrixLayout derives CanEqual {
  case Core, Wave11, Wave12
}
