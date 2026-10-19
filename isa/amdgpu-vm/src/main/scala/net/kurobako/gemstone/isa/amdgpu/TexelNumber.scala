package net.kurobako.gemstone.isa.amdgpu

/** How a channel's bits read as a number. */
private[isa] enum TexelNumber derives CanEqual {
  case Unorm, Snorm, Uscaled, Sscaled, Uint, Sint, Float, Srgb
}
