package net.kurobako.gemstone.texture

/** How a mipmapped lookup picks its level: the level itself, or the coordinate derivatives along screen x and y. */
enum LevelOfDetail derives CanEqual {
  case Explicit(level: Double)
  case Gradient(dx: IndexedSeq[Double], dy: IndexedSeq[Double])
}
