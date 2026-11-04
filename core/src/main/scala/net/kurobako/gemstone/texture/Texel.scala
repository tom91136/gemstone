package net.kurobako.gemstone.texture

/** Four components. Integral results hold exact integers in the doubles, floats are rounded to binary32. */
case class Texel(values: Vector[Double], integral: Boolean) derives CanEqual {
  def apply(channel: Int): Double = values(channel)
}
