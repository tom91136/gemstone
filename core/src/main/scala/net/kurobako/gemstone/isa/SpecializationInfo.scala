package net.kurobako.gemstone.isa

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*

case class SpecializationInfo(
    specId: Binary32,
    byteSize: Int > 0
)
