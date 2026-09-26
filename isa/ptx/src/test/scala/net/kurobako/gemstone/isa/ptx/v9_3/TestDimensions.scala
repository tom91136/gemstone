package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.{NonEmptyString, NonEmptyVec, NonNegativeInt, NonNegativeLong, PositiveInt, PositiveLong}

import net.kurobako.gemstone.vm.LaunchGeometry.Dimensions

private[v9_3] object TestDimensions {
  def apply(x: Int, y: Int = 1, z: Int = 1): Dimensions =
    Dimensions(
      PositiveInt.either(x).fold(message => throw IllegalArgumentException(message), value => value),
      PositiveInt.either(y).fold(message => throw IllegalArgumentException(message), value => value),
      PositiveInt.either(z).fold(message => throw IllegalArgumentException(message), value => value)
    )
}
