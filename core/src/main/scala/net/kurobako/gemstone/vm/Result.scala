package net.kurobako.gemstone.vm

import net.kurobako.gemstone.Constraint.NonNegative

case class Result(instructions: NonNegative[Long])
