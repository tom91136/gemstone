package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.{Instruction as VMInstruction, *}

/** Instruction-local compilation facilities. Architectural state is represented explicitly in VMIR. */
private trait VMCompilationContext {
  def sm: Int
  def instruction: DecodedInstruction
  def physicalRegister(operand: Operand): Value.Register
  def source(operand: Operand, bits: Int where (Between[8, 64] & MultipleOf[8]) = 32): Value
  def floatSource(operand: Operand): Value
  def doubleSource(operand: Operand): Value
  def predicateSource(operand: Operand): Value.Register
  def temp(prefix: String = "t"): Value.Register
  def emit(instruction: VMInstruction): Unit
  def store(destination: Operand, value: Value, bits: Int where (Between[8, 64] & MultipleOf[8]) = 32): Unit
  def readConditionFlag(flag: VMConditionCodes.Flag): Value.Register
  def updateIntegerCC(lhs: Value, rhs: Value, result: Value, isSub: Boolean, carry: Option[Value]): Unit
  def setZeroFlag(value: Value): Unit
}
