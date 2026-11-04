package net.kurobako.gemstone.texture

/** Where a raw surface access starts: byte offset `x` along row `y` of slice `z` of `surface`. */
case class SurfacePosition(surface: TextureDescriptor, x: Long, y: Long, z: Long)
