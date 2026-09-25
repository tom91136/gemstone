package net.kurobako.gemstone

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

/** A work dimension count, from one to three. */
type WorkDimension = Int :| Interval.Closed[1, 3]
