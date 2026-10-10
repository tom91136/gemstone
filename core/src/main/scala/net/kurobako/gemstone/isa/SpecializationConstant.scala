package net.kurobako.gemstone.isa

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.rpc.Wire.*

case class SpecializationConstant(
    specId: Binary32,
    value: Vec[Byte]
) derives Codec
