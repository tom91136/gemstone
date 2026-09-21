package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.vm.LaunchGeometry.Dimensions

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

private[v9_3] object TestDimensions {
  def apply(x: Int, y: Int = 1, z: Int = 1): Dimensions =
    Dimensions(
      x.refineEither[Positive].fold(message => throw IllegalArgumentException(message), value => value),
      y.refineEither[Positive].fold(message => throw IllegalArgumentException(message), value => value),
      z.refineEither[Positive].fold(message => throw IllegalArgumentException(message), value => value)
    )
}
