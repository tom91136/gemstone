package net.kurobako.gemstone.isa.ptx

import net.kurobako.gemstone.*

/** The register class of a special register: a scalar of `tpe`, or a vector of four for `vector`. */
case class SpecialRegister(name: String, tpe: PtxType, vector: Boolean)
