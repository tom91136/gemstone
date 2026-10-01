package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

private object dimensions {
  def apply(x: Int > 0, y: Int > 0 = 1, z: Int > 0 = 1): Dimensions =
    Dimensions(
      x,
      y,
      z
    )
}
