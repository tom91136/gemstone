package net.kurobako.gemstone.isa.spirv.v1_6

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

type SPIRVOpcodeNumber = SPIRVOpcodeNumber.T
object SPIRVOpcodeNumber extends RefinedType[Int, Interval.Closed[0, 65535]]
