package net.kurobako.gemstone

import cats.syntax.all.*
import scala.reflect.ClassTag

final class VecSuite extends munit.FunSuite {
  test("constructors own immutable indexed storage") {
    val mutableSource = scala.collection.mutable.ArrayBuffer(1, 2, 3)
    val values        = Vec.from(mutableSource)
    mutableSource(1) = 9

    assert(Vec.sameElements(values, Vec(1, 2, 3)))
    assertEquals(values(1), 2)
  }

  test("empty collections retain the element runtime type") {
    def genericEmpty[A: ClassTag]: Vec[A] = Vec.empty
    assertEquals(genericEmpty[String].length, 0)
  }

  test("sequence extraction is representation-neutral") {
    Vec("left", "right") match {
      case Vec(left, right) => assertEquals(left -> right, "left" -> "right")
      case _                => fail("collection extractor did not preserve values")
    }
  }

  test("Cats traverses Vec without a representation-specific instance") {
    val parsed = Vec("1", "2", "3").traverse(_.toIntOption.toRight("not an integer"))
    assertEquals(parsed, Right(Vec(1, 2, 3)))
  }

  test("byte keys retain content equality while caching their hash") {
    val left  = ByteKey(Vec[Byte](1, 2, 3))
    val right = ByteKey(Vec[Byte](1, 2, 3))
    assertEquals(left, right)
    assertEquals(left.hashCode, right.hashCode)
  }
}
