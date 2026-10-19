package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.isa.sass.Instruction.*
import net.kurobako.gemstone.vm.*

/** Interpret modifier tokens only after the instruction family has validated its ordered form. */
private object ModifierLowering {
  private def unsupported(detail: String): Nothing = throw IllegalArgumentException(detail)

  /** The ordered and unordered relations a floating comparison selects with one modifier. */
  val comparisons: Set[Modifier] = Set(
    Modifier.EQ,
    Modifier.NE,
    Modifier.LT,
    Modifier.LE,
    Modifier.GT,
    Modifier.GE,
    Modifier.EQU,
    Modifier.NEU,
    Modifier.LTU,
    Modifier.LEU,
    Modifier.GTU,
    Modifier.GEU
  )

  extension (raw: Modifier) {
    def predicateOperation: PredicateOperation = raw match {
      case Modifier.`AND` => PredicateOperation.And
      case Modifier.`OR`  => PredicateOperation.Or
      case Modifier.`XOR` => PredicateOperation.Xor
      case _              => unsupported(s"unsupported SASS predicate operation $raw")
    }
    def integerOperation(signed: Boolean = false): IntegerOperation = raw match {
      case Modifier.`ADD` | Modifier.`SUM` | Modifier.`ACC` => IntegerOperation.Add
      case Modifier.`AND`                                   => IntegerOperation.BitwiseAnd
      case Modifier.`OR`                                    => IntegerOperation.BitwiseOr
      case Modifier.`XOR`                                   => IntegerOperation.BitwiseXor
      case Modifier.`MIN`                                   => IntegerOperation.Minimum(signed)
      case Modifier.`MAX`                                   => IntegerOperation.Maximum(signed)
      case _                                                => unsupported(s"unsupported SASS integer operation $raw")
    }
    def atomicOperation(signed: Boolean): AtomicOperation = raw match {
      case Modifier.`ADD`  => AtomicOperation.Add
      case Modifier.`EXCH` => AtomicOperation.Exchange
      case Modifier.`AND`  => AtomicOperation.And
      case Modifier.`OR`   => AtomicOperation.Or
      case Modifier.`XOR`  => AtomicOperation.Xor
      case Modifier.`MIN`  => if (signed) AtomicOperation.MinimumSigned else AtomicOperation.MinimumUnsigned
      case Modifier.`MAX`  => if (signed) AtomicOperation.MaximumSigned else AtomicOperation.MaximumUnsigned
      case Modifier.`INC`  => AtomicOperation.IncrementWrap
      case Modifier.`DEC`  => AtomicOperation.DecrementWrap
      case _               => unsupported(s"unsupported SASS atomic operation $raw")
    }

    def comparisonCode: Comparison = raw match {
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
    def integerConversion: NumericFormat = raw match {
      case Modifier.`S8`  => NumericFormat.Signed(8)
      case Modifier.`U8`  => NumericFormat.Unsigned(8)
      case Modifier.`S16` => NumericFormat.Signed(16)
      case Modifier.`U16` => NumericFormat.Unsigned(16)
      case Modifier.`S32` => NumericFormat.Signed(32)
      case Modifier.`U32` => NumericFormat.Unsigned(32)
      case Modifier.`S64` => NumericFormat.Signed(64)
      case Modifier.`U64` => NumericFormat.Unsigned(64)
      case _              => unsupported(s"unsupported SASS integer conversion $raw")
    }
    def conversionRounding: ConversionRounding = raw match {
      case Modifier.`RN` => ConversionRounding.RN
      case Modifier.`RZ` => ConversionRounding.RZ
      case Modifier.`RM` => ConversionRounding.RM
      case Modifier.`RP` => ConversionRounding.RP
      case _             => unsupported(s"unsupported SASS rounding mode $raw")
    }
    def floatUnaryOperation: FloatUnaryOperation = raw match {
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
}
