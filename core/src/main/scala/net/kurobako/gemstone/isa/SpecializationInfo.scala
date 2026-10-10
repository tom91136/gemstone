package net.kurobako.gemstone.isa

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.rpc.Wire.*

case class SpecializationInfo(
    specId: Binary32,
    byteSize: Int > 0
) derives Codec
