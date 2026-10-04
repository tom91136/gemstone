package net.kurobako.gemstone

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import scala.compiletime.testing.typeCheckErrors

class NarrowConstraintSuite extends munit.FunSuite {
  private object Lane       extends Newtype[Byte in 0 ~ 31]
  private object NestedLane extends Newtype[Lane.Type]

  test("Byte and Short literals retain their carriers and predicates") {
    val lane: Byte in 0 ~ 31                                     = 31
    val index: Short in 0 ~ 1023                                 = 1023
    val negative: Byte in -128 ~ -1                              = -128
    val aligned: Short where (Between[0, 1024] & MultipleOf[16]) = 512
    assertEquals(lane.raw, 31.toByte)
    assertEquals(index.raw, 1023.toShort)
    assertEquals(negative.raw, (-128).toByte)
    assertEquals(aligned.raw, 512.toShort)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val lane: Byte in 0 ~ 31 = 32
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val value: Byte >= 0 = 128
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val value: Short >= 0 = 32768
    """).nonEmpty)
  }

  test("runtime validation requires the exact carrier and validates before naming") {
    val byte: Byte   = 17
    val short: Short = 1024
    assertEquals(byte.refine[Byte in 0 ~ 31].map(_.raw), Right(byte))
    assert(short.refine[Short in 0 ~ 1023].isLeft)
    assert((-1).toByte.refine[Byte >= 0].isLeft)
    assertEquals(Lane.either(byte).toOption.get.unwrap.raw, byte)
    val lane   = Lane(17)
    val nested = NestedLane(lane)
    assertEquals(nested.raw: Byte, byte)
    assertEquals(nested.unwrap, lane)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val value: Int = 17
      value.refine[Byte in 0 ~ 31]
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      val value: Byte = 17
      value.refine[Short > 0]
    """).nonEmpty)
  }

  test("proven widening preserves ranges across every narrow carrier") {
    import constraint.auto.*
    val byte: Byte in 1 ~ 31                = 17
    val short: Short > 0                    = byte
    val int: Int > 0                        = byte
    val long: Long > 0                      = byte
    val shortInt: Int > 0                   = short
    val shortLong: Long > 0                 = short
    val byteCarrierBound: Int in 1 ~ 127    = 17: Byte > 0
    val shortCarrierBound: Int in 1 ~ 32767 = short
    assertEquals(short.raw, 17.toShort)
    assertEquals(int.raw, 17)
    assertEquals(long.raw, 17L)
    assertEquals(shortInt.raw, 17)
    assertEquals(shortLong.raw, 17L)
    assertEquals(byteCarrierBound.raw, 17)
    assertEquals(shortCarrierBound.raw, 17)
    assertEquals(byte.toShort.raw, 17.toShort)
    assertEquals(byte.toInt.raw, 17)
    assertEquals(byte.toLong.raw, 17L)
    assertEquals(short.toInt.raw, 17)
    assertEquals(short.toLong.raw, 17L)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      import net.kurobako.gemstone.constraint.auto.*
      val positive: Byte > 0 = 17
      val small: Int in 1 ~ 16 = positive
    """).nonEmpty)
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      import net.kurobako.gemstone.constraint.auto.*
      val int: Int in 1 ~ 31 = 17
      val byte: Byte in 1 ~ 31 = int
    """).nonEmpty)
  }

  test("exact narrowing preserves predicates and rejects carrier overflow") {
    val upper: Int >= 0              = 127
    val lower: Int in -32768 ~ 32767 = -32768
    assertEquals(upper.toByteExact.raw, 127.toByte)
    assertEquals(lower.toShortExact.raw, (-32768).toShort)
    val lanes = Vec.range[0, 32].map(_.toByteExact)
    assertEquals(lanes.map(_.raw), Vec.from(0 until 32).map(_.toByte))
    assert(Vec.toArray(lanes).getClass == classOf[Array[Byte]])
    val _ = intercept[ArithmeticException]((128: Int >= 0).toByteExact)
    val _ = intercept[ArithmeticException]((-129: Int < 0).toByteExact)
    val _ = intercept[ArithmeticException]((32768: Int >= 0).toShortExact)
    val _ = intercept[ArithmeticException]((-32769: Int < 0).toShortExact)
  }
}
