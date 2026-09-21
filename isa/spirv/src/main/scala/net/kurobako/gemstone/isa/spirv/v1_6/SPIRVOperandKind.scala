package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.NoNul
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

type SPIRVOperandKind = SPIRVOperandKind.T
object SPIRVOperandKind extends RefinedType[String, MinLength[1] & NoNul]
