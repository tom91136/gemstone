package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.Constraint.Positive

import net.kurobako.gemstone.Constraint.NonEmptyString
import net.kurobako.gemstone.NonEmptyVec

import net.kurobako.gemstone.vm.LaunchGeometry.Dimensions

private[v9_3] object TestDimensions {
  def apply(x: Int, y: Int = 1, z: Int = 1): Dimensions =
    Dimensions(
      Positive.either(x).fold(message => throw IllegalArgumentException(message), value => value),
      Positive.either(y).fold(message => throw IllegalArgumentException(message), value => value),
      Positive.either(z).fold(message => throw IllegalArgumentException(message), value => value)
    )
}
