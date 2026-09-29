package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

private object dimensions {
  def apply(x: Int, y: Int = 1, z: Int = 1): Dimensions =
    Dimensions(
      x.unsafe[Int > 0],
      y.unsafe[Int > 0],
      z.unsafe[Int > 0]
    )
}
