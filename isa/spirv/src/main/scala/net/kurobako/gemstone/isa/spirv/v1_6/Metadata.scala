package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.NoNul
import net.kurobako.gemstone.Vec
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

final case class Metadata(
    instructionClass: String :| (MinLength[1] & NoNul),
    since: Option[String :| (MinLength[1] & NoNul)],
    capabilities: Vec[String :| (MinLength[1] & NoNul)],
    operands: Vec[OperandShape]
)
