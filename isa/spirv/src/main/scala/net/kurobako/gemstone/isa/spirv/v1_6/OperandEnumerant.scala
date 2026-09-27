package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.Constraint.NonEmptyString

import net.kurobako.gemstone.Vec

case class OperandEnumerant(value: Long, parameterKinds: Vec[NonEmptyString])
