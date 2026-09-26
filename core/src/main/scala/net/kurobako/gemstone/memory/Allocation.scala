package net.kurobako.gemstone.memory

import net.kurobako.gemstone.{NonEmptyString, NonNegativeInt}

final case class Allocation(
    start: GuestAddress,
    size: NonNegativeInt,
    provenance: NonEmptyString
)
