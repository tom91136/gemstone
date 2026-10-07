package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import scala.collection.mutable

object VMLane {
  private val NoValues     = Array.emptyLongArray
  private val DefinedTrue  = Some(true)
  private val DefinedFalse = Some(false)
}

/** Registers, predicates and `.param` storage of one thread within one frame. Storage grows on first write; unwritten
  * registers read as zero and unwritten predicates as undefined.
  */
@SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
class VMLane {
  private var registers: Array[Long] = VMLane.NoValues
  // Two words per 64 predicates: the defined bits, then their values.
  private var predicates: Array[Long]                   = VMLane.NoValues
  private var parameters: Array[Array[Byte] | Null]     = Array.empty
  private var codes: VMConditionCodes | Null            = null
  private var returns: mutable.ArrayBuffer[Long] | Null = null
  var carry: Boolean                                    = false

  def register(register: Register): Long = {
    val index = register.raw
    if (index < registers.length) registers(index) else 0L
  }

  def setRegister(register: Register, value: Long): Unit = {
    val index = register.raw
    if (index >= registers.length)
      registers = java.util.Arrays.copyOf(registers, math.max(index + 1, registers.length * 2))
    registers(index) = value
  }

  def predicate(predicate: Predicate): Option[Boolean] = {
    val word = (predicate.raw >>> 6) * 2
    val bit  = 1L << (predicate.raw & 63)
    if (word >= predicates.length || (predicates(word) & bit) == 0L) None
    else if ((predicates(word + 1) & bit) != 0L) VMLane.DefinedTrue
    else VMLane.DefinedFalse
  }

  def setPredicate(predicate: Predicate, value: Boolean): Unit = {
    val word = (predicate.raw >>> 6) * 2
    val bit  = 1L << (predicate.raw & 63)
    if (word >= predicates.length)
      predicates = java.util.Arrays.copyOf(predicates, math.max(word + 2, predicates.length * 2))
    predicates(word) |= bit
    if (value) predicates(word + 1) |= bit else predicates(word + 1) &= ~bit
  }

  /** Unwritten parameter bytes have unspecified contents, like local memory; they read as zero. */
  def readParameter(parameter: Parameter, offset: Int >= 0, size: Int in 0 ~ 8): Long =
    parameterBytes(parameter) match {
      case None        => 0L
      case Some(bytes) => LittleEndian.decode(size)(index =>
          if (offset.raw + index < bytes.length) bytes(offset.raw + index) else 0.toByte
        )
    }

  def writeParameter(parameter: Parameter, offset: Int >= 0, width: ScalarBitWidth, value: Long): Unit = {
    val end   = Math.addExact(offset.raw, width.toScalarByteWidth.raw)
    val bytes = parameterBytes(parameter) match {
      case Some(existing) if existing.length >= end => existing
      case existing                                 =>
        val grown = existing.fold(Array.ofDim[Byte](end))(java.util.Arrays.copyOf(_, end))
        setParameterBytes(parameter, grown)
        grown
    }
    LittleEndian.write(value, bytes, offset.raw, width.toScalarByteWidth)
  }

  def parameterBytes(parameter: Parameter): Option[Array[Byte]] =
    if (parameter.raw < parameters.length) Option(parameters(parameter.raw)) else None

  def setParameterBytes(parameter: Parameter, bytes: Array[Byte]): Unit = {
    val index = parameter.raw
    if (index >= parameters.length)
      parameters = java.util.Arrays.copyOf(parameters, math.max(index + 1, parameters.length * 2))
    parameters(index) = bytes
  }

  def conditionCodes: VMConditionCodes = codes match {
    case null =>
      val allocated = VMConditionCodes()
      codes = allocated
      allocated
    case existing => existing
  }

  def returnStack: mutable.ArrayBuffer[Long] = returns match {
    case null =>
      val allocated = mutable.ArrayBuffer.empty[Long]
      returns = allocated
      allocated
    case existing => existing
  }

  def clear(): Unit = {
    java.util.Arrays.fill(registers, 0L)
    java.util.Arrays.fill(predicates, 0L)
    parameters.indices.foreach(index => parameters(index) = null)
    codes match {
      case null     => ()
      case existing => existing.clear()
    }
    returns match {
      case null     => ()
      case existing => existing.clear()
    }
    carry = false
  }
}
