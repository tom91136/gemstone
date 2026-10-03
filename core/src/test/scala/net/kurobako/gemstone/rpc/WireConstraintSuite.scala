package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.rpc.Wire.*
import scala.compiletime.testing.typeCheckErrors

class WireConstraintSuite extends munit.FunSuite {
  private object Count extends Newtype[Int >= 7]
  private object Label extends Newtype[NonEmpty[String]] {
    override protected def validate(value: NonEmpty[String]): Unit =
      require(value.startsWith("k"), "label must start with k")
  }
  private object Nested extends Newtype[Count.Type]

  private def decode[A: Codec, B: Codec](value: B): Either[String, A] =
    Codec[B].encode(value, ExecutionRPC.Limits).flatMap(Codec[A].decode(_, ExecutionRPC.Limits))

  test("numeric codecs derive arbitrary bounds and intersections") {
    assertEquals(decode[Int >= 7, Int](7).map(_.raw), Right(7))
    assert(decode[Int >= 7, Int](6).isLeft)
    assertEquals(decode[Long < -3L, Long](-4L).map(_.raw), Right(-4L))
    assert(decode[Long < -3L, Long](-3L).isLeft)
    assertEquals(decode[Int in -2 ~ 5, Int](5).map(_.raw), Right(5))
    assert(decode[Int in -2 ~ 5, Int](6).isLeft)
    assertEquals(
      Codec[Int in -2 ~ 5].shape,
      Shape.Alias("Int", Shape.Int32, Vector(Constraint.IntInterval(-2, 5)))
    )
  }

  test("finite choices and multiples validate and describe their domains") {
    assertEquals(decode[Int in (2, 5, 9), Int](5).map(_.raw), Right(5))
    assert(decode[Int in (2, 5, 9), Int](4).isLeft)
    assertEquals(
      Codec[Int in (2, 5, 9)].shape,
      Shape.Alias("Int", Shape.Int32, Vector(Constraint.OneOf(Vector(2L, 5L, 9L))))
    )
    type Aligned = Long where (Between[0L, 32L] & MultipleOf[8L])
    assertEquals(decode[Aligned, Long](24L).map(_.raw), Right(24L))
    assert(decode[Aligned, Long](25L).isLeft)
    assert(decode[Aligned, Long](40L).isLeft)
  }

  test("range shorthand derives the same codec and rejects empty schemas") {
    assertEquals(Codec[Int in -2 ~ 5].shape, Codec[Int where Between[-2, 5]].shape)
    assertEquals(decode[Int in -2 ~ 5, Int](5).map(_.raw), Right(5))
    assert(decode[Int in -2 ~ 5, Int](6).isLeft)
    val errors = typeCheckErrors("""
      import net.kurobako.gemstone.constraint.*
      import net.kurobako.gemstone.rpc.Wire.*
      Codec[Int in 3 ~ 0]
    """)
    assert(errors.exists(_.message.contains("Unsatisfiable constraint")))
  }

  test("nonempty collections compose with their element codecs") {
    assert(decode[NonEmpty[Vec[Int]], Vec[Int]](Vec.empty).isLeft)
    assertEquals(decode[NonEmpty[Vec[Int]], Vec[Int]](Vec(1, 2)).map(_.raw), Right(Vec(1, 2)))
    assert(decode[NonEmpty[String], String]("").isLeft)
    assertEquals(decode[NonEmpty[String], String]("\u0000").map(_.raw), Right("\u0000"))
  }

  test("newtypes derive from refined representations and preserve custom validation") {
    assertEquals(decode[Count.Type, Int](7).map(_.raw), Right(7))
    assert(decode[Count.Type, Int](6).isLeft)
    assertEquals(
      Codec[Count.Type].shape,
      Shape.Alias("Count", Shape.Int32, Vector(Constraint.IntInterval(7, Int.MaxValue)))
    )
    assertEquals(decode[Label.Type, String]("kernel").map(_.raw), Right("kernel"))
    assert(decode[Label.Type, String]("value").isLeft)
    assert(decode[Label.Type, String]("").isLeft)
    assertEquals(decode[Nested.Type, Int](9).map(_.raw), Right(9))
    assert(decode[Nested.Type, Int](6).isLeft)
  }
  test("protocol identities distinguish numeric bounds, finite choices and alignment") {
    def schema[A: Codec]: ProtocolShape = ProtocolShape(
      "fixture",
      Vector(OperationShape(Alternative("request", Codec[A].shape), Shape.UnitValue)),
      Shape.Text
    )
    val interval = schema[Long in -3L ~ 17L]
    assertEquals(
      interval.canonical,
      "protocol(fixture){request:alias(Long,i64)[long-interval[-3,17]]=>unit};failure=text"
    )
    assertNotEquals(interval.canonical, schema[Long in -2L ~ 17L].canonical)
    assertNotEquals(interval.canonical, schema[Long in -3L ~ 18L].canonical)
    val choices = schema[Int in (2, 5, 9)]
    assertEquals(
      choices.canonical,
      "protocol(fixture){request:alias(Int,i32)[one-of[2,5,9]]=>unit};failure=text"
    )
    assertNotEquals(choices.canonical, schema[Int in (2, 5, 10)].canonical)
    val aligned = schema[Long where (Between[0L, 32L] & MultipleOf[8L])]
    assertEquals(
      aligned.canonical,
      "protocol(fixture){request:alias(Long,i64)[long-interval[0,32],multiple-of[8]]=>unit};failure=text"
    )
    assertNotEquals(aligned.canonical, schema[Long where (Between[0L, 32L] & MultipleOf[16L])].canonical)
  }

  test("narrow numeric codecs retain their carrier bounds and schema") {
    assertEquals(decode[Byte in 0 ~ 31, Int](31).map(_.raw), Right(31.toByte))
    assert(decode[Byte in 0 ~ 31, Int](32).isLeft)
    assert(decode[Byte in 0 ~ 255, Int](128).isLeft)
    assertEquals(decode[Short in 0 ~ 1023, Int](1023).map(_.raw), Right(1023.toShort))
    assert(decode[Short in 0 ~ 1023, Int](1024).isLeft)
    assert(decode[Short, Int](32768).isLeft)
    assert(decode[Short, Int](-32769).isLeft)
    assertEquals(decode[Short, Int](-32768), Right((-32768).toShort))
    assertEquals(
      Codec[Byte in 0 ~ 31].shape,
      Shape.Alias("Byte", Shape.Int8, Vector(Constraint.IntInterval(0, 31)))
    )
    assertEquals(
      Codec[Short > 0].shape,
      Shape.Alias("Short", Shape.Int16, Vector(Constraint.Positive))
    )
    assertEquals(decode[Vec[Short], Vec[Short]](Vec(1.toShort, (-2).toShort)), Right(Vec(1.toShort, (-2).toShort)))
    val schema = ProtocolShape(
      "narrow",
      Vector(OperationShape(Alternative("request", Codec[Short].shape), Shape.UnitValue)),
      Shape.Text
    )
    assertEquals(schema.canonical, "protocol(narrow){request:i16=>unit};failure=text")
  }

}
