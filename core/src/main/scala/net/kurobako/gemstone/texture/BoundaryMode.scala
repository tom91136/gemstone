package net.kurobako.gemstone.texture

/** How a surface access outside the surface behaves: fault, move to the nearest texel, or read zeros and drop writes.
  */
enum BoundaryMode derives CanEqual { case Trap, Clamp, Zero }
