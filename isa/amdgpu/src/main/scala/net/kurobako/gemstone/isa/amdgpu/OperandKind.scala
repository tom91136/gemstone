package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.NoNul

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

type OperandKind = OperandKind.T
object OperandKind extends RefinedType[String, MinLength[1] & NoNul]
