package net.kurobako.gemstone.memory

import net.kurobako.gemstone.Constraint.NonNegative
import net.kurobako.gemstone.Constraint.NonEmptyString

case class Allocation(
    start: GuestAddress,
    size: NonNegative[Int],
    provenance: NonEmptyString
)
