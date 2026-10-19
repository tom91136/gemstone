package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

/** How a texel's channels are packed, as the hardware names them: the channel widths from the most significant, so the
  * last is x. `widths` lists them from x.
  */
private[isa] enum TexelLayout(val widths: Vec[Int in 1 ~ 32]) derives CanEqual {
  case Invalid        extends TexelLayout(Vec.empty)
  case X8             extends TexelLayout(Vec(8))
  case X16            extends TexelLayout(Vec(16))
  case X8Y8           extends TexelLayout(Vec(8, 8))
  case X32            extends TexelLayout(Vec(32))
  case X16Y16         extends TexelLayout(Vec(16, 16))
  case Z10Y11X11      extends TexelLayout(Vec(11, 11, 10))
  case Z11Y11X10      extends TexelLayout(Vec(10, 11, 11))
  case W10Z10Y10X2    extends TexelLayout(Vec(2, 10, 10, 10))
  case W2Z10Y10X10    extends TexelLayout(Vec(10, 10, 10, 2))
  case X8Y8Z8W8       extends TexelLayout(Vec(8, 8, 8, 8))
  case X32Y32         extends TexelLayout(Vec(32, 32))
  case X16Y16Z16W16   extends TexelLayout(Vec(16, 16, 16, 16))
  case X32Y32Z32      extends TexelLayout(Vec(32, 32, 32))
  case X32Y32Z32W32   extends TexelLayout(Vec(32, 32, 32, 32))
  case Z5Y6X5         extends TexelLayout(Vec(5, 6, 5))
  case W1Z5Y5X5       extends TexelLayout(Vec(5, 5, 5, 1))
  case W5Z5Y5X1       extends TexelLayout(Vec(1, 5, 5, 5))
  case W4Z4Y4X4       extends TexelLayout(Vec(4, 4, 4, 4))
  case Y4X4           extends TexelLayout(Vec(4, 4))
  case SharedExponent extends TexelLayout(Vec(9, 9, 9, 5))

  val channels: Int = widths.size
  val bits: Int     = widths.rawElements.sum
  val bytes: Int    = bits / 8

  /** Each channel's lowest bit. */
  val offsets: Vec[Int] = widths.scanLeft(0)(_ + _).take(widths.size)
}
