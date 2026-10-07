package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.isa.*

case class Header(version: Version, generator: Word, bound: Word, schema: Word)
