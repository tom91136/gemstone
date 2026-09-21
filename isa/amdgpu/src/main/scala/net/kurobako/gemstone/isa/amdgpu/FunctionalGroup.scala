package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.NoNul

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

type FunctionalGroup = FunctionalGroup.T
object FunctionalGroup extends RefinedType[String, MinLength[1] & NoNul]
