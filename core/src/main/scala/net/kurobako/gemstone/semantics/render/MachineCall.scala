package net.kurobako.gemstone.semantics.render

import net.kurobako.gemstone.collection.*

case class MachineCall(method: String, typeArguments: Vec[Type], arguments: Vec[(Term, Option[Type])])
