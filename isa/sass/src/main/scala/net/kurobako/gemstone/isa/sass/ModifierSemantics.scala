package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.isa.sass.Instruction.*

/** Interpret modifier tokens only after the instruction family has validated its ordered form. */
private object ModifierSemantics {
  private def unsupported(detail: String): Nothing          = throw IllegalArgumentException(detail)
  def predicateOperation(raw: Modifier): PredicateOperation = raw match {
    case Modifier.`AND` => PredicateOperation.And
    case Modifier.`OR`  => PredicateOperation.Or
    case Modifier.`XOR` => PredicateOperation.Xor
    case _              => unsupported(s"unsupported SASS predicate operation $raw")
  }
  def integerOperation(raw: Modifier, signed: Boolean = false): IntegerOperation = raw match {
    case Modifier.`ADD` | Modifier.`SUM` | Modifier.`ACC` => IntegerOperation.Add
    case Modifier.`AND`                                   => IntegerOperation.BitwiseAnd
    case Modifier.`OR`                                    => IntegerOperation.BitwiseOr
    case Modifier.`XOR`                                   => IntegerOperation.BitwiseXor
    case Modifier.`MIN`                                   => IntegerOperation.Minimum(signed)
    case Modifier.`MAX`                                   => IntegerOperation.Maximum(signed)
    case _                                                => unsupported(s"unsupported SASS integer operation $raw")
  }
  def comparisonCode(raw: Modifier): Comparison = raw match {
    case Modifier.`EQ`  => Comparison.Equal
    case Modifier.`NE`  => Comparison.NotEqual
    case Modifier.`LT`  => Comparison.LessThan
    case Modifier.`LE`  => Comparison.LessThanOrEqual
    case Modifier.`GT`  => Comparison.GreaterThan
    case Modifier.`GE`  => Comparison.GreaterThanOrEqual
    case Modifier.`EQU` => Comparison.EqualUnordered
    case Modifier.`NEU` => Comparison.NotEqualUnordered
    case Modifier.`LTU` => Comparison.LessThanUnordered
    case Modifier.`LEU` => Comparison.LessThanOrEqualUnordered
    case Modifier.`GTU` => Comparison.GreaterThanUnordered
    case Modifier.`GEU` => Comparison.GreaterThanOrEqualUnordered
    case Modifier.`NUM` => Comparison.Ordered
    case Modifier.`NAN` => Comparison.Unordered
    case _              => unsupported(s"unsupported SASS comparison $raw")
  }
  def integerConversion(raw: Modifier): ConversionType = raw match {
    case Modifier.`S32` => ConversionType.S32
    case Modifier.`U32` => ConversionType.U32
    case Modifier.`S64` => ConversionType.S64
    case Modifier.`U64` => ConversionType.U64
    case _              => unsupported(s"unsupported SASS integer conversion $raw")
  }
  def conversionRounding(raw: Modifier): ConversionRounding = raw match {
    case Modifier.`RN` => ConversionRounding.RN
    case Modifier.`RZ` => ConversionRounding.RZ
    case Modifier.`RM` => ConversionRounding.RM
    case Modifier.`RP` => ConversionRounding.RP
    case _             => unsupported(s"unsupported SASS rounding mode $raw")
  }
  def floatUnaryOperation(raw: Modifier): FloatUnaryOperation = raw match {
    case Modifier.`RCP`  => FloatUnaryOperation.Reciprocal
    case Modifier.`RSQ`  => FloatUnaryOperation.ReciprocalSquareRoot
    case Modifier.`SQRT` => FloatUnaryOperation.SquareRoot
    case Modifier.`EX2`  => FloatUnaryOperation.Exponent2
    case Modifier.`LG2`  => FloatUnaryOperation.Log2
    case Modifier.`SIN`  => FloatUnaryOperation.Sine
    case Modifier.`COS`  => FloatUnaryOperation.Cosine
    case Modifier.`TANH` => FloatUnaryOperation.Tanh
    case _               => unsupported(s"unsupported SASS floating operation $raw")
  }
}
