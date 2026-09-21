package net.kurobako.gemstone.isa

import net.kurobako.gemstone.NoNul

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

type KernelName = KernelName.T
object KernelName extends RefinedType[String, MinLength[1] & NoNul]
