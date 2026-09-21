package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.NoNul

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

type GlobalName = GlobalName.T
object GlobalName extends RefinedType[String, MinLength[1] & NoNul]
