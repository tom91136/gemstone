package net.kurobako.gemstone.binary

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

private[gemstone] type ByteAlignedBitWidth = ByteAlignedBitWidth.T
private[gemstone] object ByteAlignedBitWidth extends RefinedType[Int, Interval.Closed[0, 64] & Multiple[8]]
