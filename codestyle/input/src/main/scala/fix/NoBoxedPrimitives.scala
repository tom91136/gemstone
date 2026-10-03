/*
rule = GemstoneNoBoxedPrimitives
 */
package fix

import java.lang.{Integer => JInt}

object NoBoxedPrimitives {
  val boolean: java.lang.Boolean = ??? // assert: GemstoneNoBoxedPrimitives.boxed-type
  val byte: java.lang.Byte = ??? // assert: GemstoneNoBoxedPrimitives.boxed-type
  val short: java.lang.Short = ??? // assert: GemstoneNoBoxedPrimitives.boxed-type
  val integer: Integer = ??? // assert: GemstoneNoBoxedPrimitives.boxed-type
  val long: java.lang.Long = ??? // assert: GemstoneNoBoxedPrimitives.boxed-type
  val float: java.lang.Float = ??? // assert: GemstoneNoBoxedPrimitives.boxed-type
  val double: java.lang.Double = ??? // assert: GemstoneNoBoxedPrimitives.boxed-type
  val character: Character = ??? // assert: GemstoneNoBoxedPrimitives.boxed-type
  val void: java.lang.Void = ??? // assert: GemstoneNoBoxedPrimitives.boxed-type
  type Alias = JInt // assert: GemstoneNoBoxedPrimitives.boxed-type
  val nested: List[JInt] = Nil // assert: GemstoneNoBoxedPrimitives.boxed-type
  val constructed = new JInt(1) // assert: GemstoneNoBoxedPrimitives.boxed-type
  val constructedWithoutNew = JInt(1) // assert: GemstoneNoBoxedPrimitives.boxing
  val boxed = JInt.valueOf(1) // assert: GemstoneNoBoxedPrimitives.boxing
  val qualifiedBox = java.lang.Integer.valueOf(2) // assert: GemstoneNoBoxedPrimitives.boxing
  val decoded = JInt.decode("0x10") // assert: GemstoneNoBoxedPrimitives.boxing
  val boxedTrue = java.lang.Boolean.TRUE // assert: GemstoneNoBoxedPrimitives.boxing
  val boxedFalse = java.lang.Boolean.FALSE // assert: GemstoneNoBoxedPrimitives.boxing
  val classToken = classOf[JInt] // assert: GemstoneNoBoxedPrimitives.boxed-type

  val a = boolean.booleanValue() // assert: GemstoneNoBoxedPrimitives.unboxing
  val b = byte.byteValue() // assert: GemstoneNoBoxedPrimitives.unboxing
  val c = short.shortValue() // assert: GemstoneNoBoxedPrimitives.unboxing
  val d = integer.intValue() // assert: GemstoneNoBoxedPrimitives.unboxing
  val e = long.longValue() // assert: GemstoneNoBoxedPrimitives.unboxing
  val f = float.floatValue() // assert: GemstoneNoBoxedPrimitives.unboxing
  val g = double.doubleValue() // assert: GemstoneNoBoxedPrimitives.unboxing
  val h = character.charValue() // assert: GemstoneNoBoxedPrimitives.unboxing
  def number(value: Number): Long = value.longValue() // assert: GemstoneNoBoxedPrimitives.unboxing
  val scalaBigInt = BigInt(1).longValue // assert: GemstoneNoBoxedPrimitives.unboxing
  val scalaBigDecimal = BigDecimal(1).intValue // assert: GemstoneNoBoxedPrimitives.unboxing
  def javaBigInt(value: java.math.BigInteger): Double = value.doubleValue() // assert: GemstoneNoBoxedPrimitives.unboxing
  def javaBigDecimal(value: java.math.BigDecimal): Int = value.intValue() // assert: GemstoneNoBoxedPrimitives.unboxing
  val eta: () => Int = integer.intValue // assert: GemstoneNoBoxedPrimitives.unboxing

  val primitive: Int = 1
  val parsed = JInt.parseInt("12")
  val rotated = JInt.rotateLeft(primitive, 3)
  val bits = java.lang.Double.doubleToRawLongBits(1.0)
  val limit = JInt.MAX_VALUE
  val primitiveClass = JInt.TYPE
  val converted = BigInt(1).toLong
  val atomic = java.util.concurrent.atomic.AtomicInteger(0)

  object Shadowed {
    class Integer {
      def intValue(): Int = 1
    }
    object Integer {
      def valueOf(value: Int): Int = value
    }
    val local: Integer = new Integer
    val value = local.intValue()
    val factory = Integer.valueOf(1)
  }

  @SuppressWarnings(Array("scalafix:GemstoneNoBoxedPrimitives"))
  def javaBoundary(value: JInt): Int = value.intValue()
}
