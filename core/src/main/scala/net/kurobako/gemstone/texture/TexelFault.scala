package net.kurobako.gemstone.texture

/** A fetch the device cannot complete. */
enum TexelFault derives CanEqual {
  case Unmapped(address: Long)
  case OutOfBounds(x: Long, y: Long, z: Long)
}
