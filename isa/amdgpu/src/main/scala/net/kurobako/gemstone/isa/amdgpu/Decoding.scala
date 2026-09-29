package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.collection.*

/** Fixed encoding values shared by the AMDGPU decoders and interpreter. */
private object Decoding {
  // SGPR indices the ISA aliases to architectural state.
  final val VccLo    = 106
  final val VccHi    = 107
  final val M0       = 124
  final val NullSgpr = 125
  final val ExecLo   = 126
  final val ExecHi   = 127

  // Sentinel VOP source encodings.
  final val LiteralSource = 255
  final val DppSource     = 250
  final val SdwaSource    = 249

  // VOP inline float constants selected by source encodings 240..248.
  final val InlineFloatingFirst                = 240
  final val InlineFloatingConstants: Vec[Long] =
    Vec(0x3f000000L, 0xbf000000L, 0x3f800000L, 0xbf800000L, 0x40000000L, 0xc0000000L, 0x40800000L, 0xc0800000L,
      0x3e22f983L)
  final val InlineFloatingValues: Vec[Double] = Vec(0.5, -0.5, 1.0, -1.0, 2.0, -2.0, 4.0, -4.0, 0.15915494)

  // Format prefixes carried in the first dword.
  final val SmemGfx6Prefix    = 0x30L
  final val SmemGfx10Prefix   = 0x3dL
  final val Sop1Prefix        = 0x17dL
  final val SopCPrefix        = 0x17eL
  final val SopPPrefix        = 0x17fL
  final val SopKPrefix        = 0xbL
  final val Vop3PPrefix       = 0x33L
  final val Vop3Prefix        = 0x34L
  final val Vop3LiteralPrefix = 0x35L
  final val DsPrefix          = 0x36L
  final val GlobalFlatPrefix  = 0x37L
  final val MubufPrefix       = 0x38L
  final val Vop1Prefix        = 0x3fL
  final val VopCPrefix        = 0x3eL
  final val Sop2Prefix        = 2L
  final val NullOffset        = 0x7dL

  /** Flat/global scalar-address encodings that mean "no SGPR pair". */
  def isAbsentScalarOffset(value: Long): Boolean = value == 0x7dL || value == 0x7eL || value == 0x7fL
}
