package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.constraint.*

/** Fields of the MODE register. */
private[isa] object ModeRegister {
  inline val DenormalShift       = 4
  val Denormal32Input: Int       = 1 << 4
  val Denormal32Output: Int      = 1 << 5
  val Denormal16And64Input: Int  = 1 << 6
  val Denormal16And64Output: Int = 1 << 7
  val DX10Clamp: Int             = 1 << 8
  val IEEE: Int                  = 1 << 9

  /** Every field that flushes denormals, as MODE.DENORM holds them. */
  val DenormalMask: Int = Denormal32Input | Denormal32Output | Denormal16And64Input | Denormal16And64Output

  /** MODE.FP_ROUND, the field `s_round_mode` writes. */
  inline val RoundShift = 0
  inline val RoundWidth = 4
  val RoundMask: Int    = (1 << RoundWidth) - 1

  /** FP_ROUND holds binary32's rounding in its low two bits, and binary16's and binary64's in the next two. */
  val Round32Mask: Int      = 0x3
  val Round16And64Mask: Int = 0xc

  /** GFX12 retired IEEE mode and DX10 clamping, so its MODE bits 8 and 9 always read as clear. */
  def ieeeSupported(isa: ISA): Boolean = isa != ISA.RDNA4 && isa != ISA.CDNA5

  /** The MODE bits a write changes; the others keep their value. */
  def writable(isa: ISA): Int = if (ieeeSupported(isa)) -1 else ~(DX10Clamp | IEEE)

  /** Hardware ignores OMOD under IEEE mode or when the result width keeps output denormals. */
  def outputModifierDisabled(mode: Int, width: Int in (16, 32, 64)): Boolean =
    (mode & (IEEE | (if (width == 32) Denormal32Output else Denormal16And64Output))) != 0
}
