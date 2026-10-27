package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.vm.*

/** One unit's life over the issue stream, as the states it went through. */
case class Band(unit: MachineUnit, segments: Vec[Segment])
