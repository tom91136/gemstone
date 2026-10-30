package net.kurobako.gemstone

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.testing.Suite
import scala.collection.immutable.ArraySeq
import scala.compiletime.testing.typeCheckErrors

class VecRangeSuite extends Suite {
  test("range produces half-open elements with refined lower and upper bounds") {
    val values: Vec[Int where (AtLeast[-2] & LessThan[3])] = Vec.range[-2, 3]
    val atMostTwo: Int <= 2                                = values.last
    assertEquals(values.map(_.raw), Vec(-2, -1, 0, 1, 2))
    assertEquals(atMostTwo.raw, 2)
  }

  test("range handles empty, negative and endpoint-adjacent ranges") {
    assertEquals(Vec.range[3, 3], Vec.empty[Int where (AtLeast[3] & LessThan[3])])
    assertEquals(Vec.range[-3, 0].map(_.raw), Vec(-3, -2, -1))
    assertEquals(Vec.range[-2147483648, -2147483647].map(_.raw), Vec(Int.MinValue))
    assertEquals(Vec.range[2147483646, 2147483647].map(_.raw), Vec(Int.MaxValue - 1))
    assertEquals(Vec.range[2147483647, 2147483647], Vec.empty[Int where (AtLeast[2147483647] & LessThan[2147483647])])
  }

  test("range rejects reversed bounds and sizes that cannot fit in Vec") {
    val _ = intercept[IllegalArgumentException](Vec.range[4, 3])
    val _ = intercept[IllegalArgumentException](Vec.range[-2147483648, 1])
  }

  test("indices produces nonnegative runtime indexes and an empty vector for zero") {
    def acceptIndex(index: Int >= 0): Int = index
    val indexes                           = Vec.indices(4)
    assertEquals(indexes.map(acceptIndex(_)), Vec(0, 1, 2, 3))
    assertEquals(Vec.indices(0), Vec.empty[Int >= 0])
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.collection.*
      def indices(count: Int) = Vec.indices(count)
    """).nonEmpty)
  }

  test("range refinements allow proven widening and reject an invalid narrowing") {
    assert(typeCheckErrors("""
      import net.kurobako.gemstone.collection.*
      import net.kurobako.gemstone.constraint.*
      import net.kurobako.gemstone.constraint.auto.*
      val lane = Vec.range[0, 32].head
      val nonnegative: Int >= 0 = lane
      val tooNarrow: Int <= 30 = lane
    """).nonEmpty)
    val lane: Int >= 0 = Vec.range[0, 32].head
    assertEquals(lane.raw, 0)
  }
  test("nonempty tabulation and mapping retain size evidence and primitive storage") {
    val bytes: NonEmpty[Vec[Byte]]    = Vec.tabulateNonEmpty(3)(index => (index.raw + 1).toByte)
    val labels: NonEmpty[Vec[String]] = bytes.mapNonEmpty(_.toString)
    val count: Int > 0                = labels.positiveSize
    assertEquals(bytes, Vec[Byte](1, 2, 3))
    assertEquals(labels, Vec("1", "2", "3"))
    assertEquals(count.raw, 3)
    val pairs: NonEmpty[Vec[String]]    = labels.flatMapNonEmpty(label => Vec.nonEmpty(label, label))
    val extended: NonEmpty[Vec[String]] = pairs.concatNonEmpty(Vec.empty)
    assertEquals(extended, Vec("1", "1", "2", "2", "3", "3"))
    assertEquals(extended.positiveSize.raw, 6)
    bytes match {
      case _: ArraySeq.ofByte => ()
      case _                  => fail("byte tabulation must retain primitive storage")
    }
  }

}
