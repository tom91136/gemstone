package net.kurobako.gemstone.texture

enum ReadMode derives CanEqual {

  /** Integer channels read as integers, float channels as floats. */
  case ElementType

  /** Integer channels read as floats in [0, 1] or [-1, 1]. */
  case NormalisedFloat
}
