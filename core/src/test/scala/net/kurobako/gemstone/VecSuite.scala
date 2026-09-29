package net.kurobako.gemstone

import cats.syntax.all.*
import net.kurobako.gemstone.collection.*
import scala.reflect.ClassTag

class VecSuite extends munit.FunSuite {
  test("constructors own immutable indexed storage") {
    val mutableSource = scala.collection.mutable.ArrayBuffer(1, 2, 3)
    val values        = Vec.from(mutableSource)
    mutableSource(1) = 9

    assert(Vec.sameElements(values, Vec(1, 2, 3)))
    assertEquals(values(1), 2)
  }

  test("empty collections produce the platform array representation for their element type") {
    def genericEmpty[A: ClassTag]: Vec[A] = Vec.empty
    val references                        = Vec.toArray(genericEmpty[String])
    val primitives                        = Vec.toArray(genericEmpty[Int])
    assertEquals(references.length, 0)
    assertEquals(primitives.length, 0)
    assertEquals(references.getClass, Array.empty[String].getClass)
    assertEquals(primitives.getClass, Array.empty[Int].getClass)
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

}
