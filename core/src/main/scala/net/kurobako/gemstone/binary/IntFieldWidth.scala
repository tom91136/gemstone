package net.kurobako.gemstone.binary

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

private[gemstone] type IntFieldWidth = IntFieldWidth.T
private[gemstone] object IntFieldWidth extends RefinedType[Int, Interval.Closed[1, 31]]
