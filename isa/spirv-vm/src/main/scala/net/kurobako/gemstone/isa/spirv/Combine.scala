package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Bits.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*

private[isa] object Combine {

  /** `kind` over values of the type `resultType` names, with the value that leaves another unchanged. */
  class Operator(program: VMProgram, resultType: Int, val kind: Combine) {
    private val integerWidth: Int in 1 ~ 64        = program.integerShape(resultType).map(_._1).getOrElse(64)
    private val floatingWidth: Int in (16, 32, 64) = program.floatingShape(resultType).map(_._1).getOrElse(64)
    private val logical                            = program.booleanLanes(resultType).nonEmpty
    private val resultMask                         =
      if (logical) 1L else program.integerShape(resultType).fold(-1L)(shape => mask(shape._1))

    val identity: Long = kind match {
      case IntegerAdd | FloatingAdd | BitwiseOr | BitwiseXor | LogicalOr | LogicalXor => 0L
      case IntegerMultiply                                                            => 1L
      case SignedMin               => ((1L << (integerWidth - 1)) - 1L) & mask(integerWidth)
      case SignedMax               => (1L << (integerWidth - 1)) & mask(integerWidth)
      case UnsignedMin             => mask(integerWidth)
      case UnsignedMax             => 0L
      case BitwiseAnd | LogicalAnd => resultMask
      case FloatingMultiply        => IEEE754.fromDouble(1.0, floatingWidth)
      case FloatingMin             => IEEE754.fromDouble(Double.PositiveInfinity, floatingWidth)
      case FloatingMax             => IEEE754.fromDouble(Double.NegativeInfinity, floatingWidth)
      case FloatingEqual           => IEEE754.fromDouble(Double.NaN, floatingWidth)
    }

    def apply(left: Long, right: Long): Long = kind match {
      case IntegerAdd      => (left + right) & mask(integerWidth)
      case IntegerMultiply => (left * right) & mask(integerWidth)
      case SignedMin       =>
        Execute.truncate(
          IntegerSemantics.minimum(Execute, left, right & mask(integerWidth), integerWidth, signed = true),
          integerWidth
        )
      case SignedMax =>
        Execute.truncate(
          IntegerSemantics.maximum(Execute, left, right & mask(integerWidth), integerWidth, signed = true),
          integerWidth
        )
      case UnsignedMin =>
        Execute.truncate(
          IntegerSemantics.minimum(Execute, left, right & mask(integerWidth), integerWidth, signed = false),
          integerWidth
        )
      case UnsignedMax =>
        Execute.truncate(
          IntegerSemantics.maximum(Execute, left, right & mask(integerWidth), integerWidth, signed = false),
          integerWidth
        )
      case FloatingAdd      => IEEE754.add(left, right, floatingWidth, IEEE754.NaNOperand.Left)
      case FloatingMultiply => IEEE754.multiply(left, right, floatingWidth, IEEE754.NaNOperand.Left)
      case FloatingMin      => IEEE754.minNum(left, right, floatingWidth)
      case FloatingMax      => IEEE754.maxNum(left, right, floatingWidth)
      case FloatingEqual    =>
        if (IEEE754.toDouble(left, floatingWidth) == IEEE754.toDouble(right, floatingWidth)) left
        else IEEE754.fromDouble(Double.NaN, floatingWidth)
      case BitwiseAnd | LogicalAnd => left & normalised(right) & resultMask
      case BitwiseOr | LogicalOr   => (left | normalised(right)) & resultMask
      case BitwiseXor | LogicalXor => (left ^ normalised(right)) & resultMask
    }

    private def normalised(value: Long): Long = if (logical) { if (value != 0L) 1L else 0L }
    else value
  }
}

/** How a reduction or scan combines the values of the invocations it covers. */
private[isa] enum Combine derives CanEqual {
  case IntegerAdd, IntegerMultiply, SignedMin, SignedMax, UnsignedMin, UnsignedMax
  case FloatingAdd, FloatingMultiply, FloatingMin, FloatingMax, FloatingEqual
  case BitwiseAnd, BitwiseOr, BitwiseXor, LogicalAnd, LogicalOr, LogicalXor
}
