package net.kurobako.gemstone.isa.spirv.v1_6

final case class Header(version: SPIRVVersion, generator: Word, bound: Word, schema: Word)
