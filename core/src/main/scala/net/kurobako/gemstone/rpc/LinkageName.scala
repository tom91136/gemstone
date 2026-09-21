package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.NoNul

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

type LinkageName = LinkageName.T
object LinkageName extends RefinedType[String, MinLength[1] & NoNul]
