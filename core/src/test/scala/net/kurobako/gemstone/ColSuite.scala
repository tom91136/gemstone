package net.kurobako.gemstone

import net.kurobako.gemstone.Col
import scala.reflect.ClassTag

class ColSuite extends munit.FunSuite {
  test("constructors own immutable indexed storage") {
    val source = scala.collection.mutable.ArrayBuffer(1, 2, 3)
    val values = Col.from(source)
    source(1) = 9

    assert(Col.sameElements(values, Col(1, 2, 3)))
    assertEquals(values(1), 2)
  }

  test("empty collections retain the element runtime type") {
    def genericEmpty[A: ClassTag]: Col[A] = Col.empty
    assertEquals(genericEmpty[String].length, 0)
  }

  test("sequence extraction is representation-neutral") {
    Col("left", "right") match {
      case Col(left, right) => assertEquals(left -> right, "left" -> "right")
      case _                => fail("collection extractor did not preserve values")
    }
  }
}
