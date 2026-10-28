package net.kurobako.gemstone.semantics.render

import net.kurobako.gemstone.collection.*

// mutually recursive with Term: a call holds terms
@SuppressWarnings(Array("baumkuchen:acyclic"))
case class MachineCall(method: String, typeArguments: Vec[Type], arguments: Vec[(Term, Option[Type])])
