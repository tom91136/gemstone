package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

/** One workgroup's units over the issue stream, stacked by the lanes they carry. */
case class Row(workgroup: Coordinates, bands: Vec[Band], lanes: Int)
