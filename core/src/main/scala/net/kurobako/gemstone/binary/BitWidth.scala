package net.kurobako.gemstone.binary

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

private[gemstone] type BitWidth = BitWidth.T
private[gemstone] object BitWidth extends RefinedType[Int, Interval.Closed[1, 64]]
