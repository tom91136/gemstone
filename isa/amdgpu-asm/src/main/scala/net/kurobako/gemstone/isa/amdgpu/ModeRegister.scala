package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.constraint.*

/** MODE register fields the VM reads. */
private object ModeRegister {
  inline val DenormalShift       = 4
  val Denormal32Input: Int       = 1 << 4
  val Denormal32Output: Int      = 1 << 5
  val Denormal16And64Input: Int  = 1 << 6
  val Denormal16And64Output: Int = 1 << 7
  val DX10Clamp: Int             = 1 << 8
  val IEEE: Int                  = 1 << 9

  /** GFX12 retired IEEE mode and DX10 clamping, so its MODE bits 8 and 9 always read as clear. */
  def ieeeSupported(isa: ISA): Boolean = isa != ISA.RDNA4 && isa != ISA.CDNA5

  /** Hardware ignores OMOD under IEEE mode or when the result width keeps output denormals. */
  def outputModifierApplies(mode: Int, width: Int in (16, 32, 64)): Boolean =
    (mode & (IEEE | (if (width == 32) Denormal32Output else Denormal16And64Output))) == 0
}
