package net.kurobako.gemstone.memory

import net.kurobako.gemstone.collection.*

class WrittenRangesSuite extends munit.FunSuite {
  test("the transfer size is chosen by the caller without changing recorded intervals") {
    val ranges = WrittenRanges()
    ranges.add(5, 7)
    assertEquals(ranges.chunks(3), Vec((5, 3), (8, 3), (11, 1)))
    assertEquals(ranges.chunks(10), Vec((5, 7)))
    val _ = intercept[IllegalArgumentException](ranges.add(-1, 1))
    val _ = intercept[IllegalArgumentException](ranges.add(1, -1))
    val _ = intercept[ArithmeticException](ranges.add(Int.MaxValue, 1))
    assertEquals(ranges.chunks(10), Vec((5, 7)))
  }

  test("writeback preserves unwritten bytes within a word and across a transfer boundary") {
    val ranges = WrittenRanges()
    List(65, 67, 65535, 65537).foreach(offset => ranges.add(offset, 1))
    assertEquals(ranges.chunks(65536), Vec((65, 1), (67, 1), (65535, 1), (65537, 1)))
  }

  test("overlapping and adjacent writes merge without duplicating bytes") {
    val ranges = WrittenRanges()
    ranges.add(20, 4)
    ranges.add(10, 4)
    ranges.add(13, 8)
    ranges.add(11, 1)
    ranges.add(24, 1)
    ranges.add(0, 0)
    assertEquals(ranges.chunks(65536), Vec((10, 15)))
    assertEquals(ranges.chunks(65536), Vec((10, 15))) // Queries must not consume completion metadata.
  }

  test("long written intervals stay bounded without rounding into neighbouring memory") {
    val ranges = WrittenRanges()
    ranges.add(7, 131073)
    assertEquals(ranges.chunks(65536), Vec((7, 65536), (65543, 65536), (131079, 1)))
  }

  test("a write bridges adjacent intervals regardless of insertion order") {
    val writes = List((10, 4), (14, 6), (20, 4))
    writes.permutations.foreach { ordered =>
      val ranges = WrittenRanges()
      ordered.foreach((offset, size) => ranges.add(offset, size))
      assertEquals(ranges.chunks(65536), Vec((10, 14)), clues(ordered))
      assertEquals(ranges.chunks(5), Vec((10, 5), (15, 5), (20, 4)), clues(ordered))
    }
  }

  test("reordered sparse writes preserve exactly the written set") {
    val ranges = WrittenRanges()
    val writes = (0 until 300).map(index => ((index * 127) % 1024, 1 + index % 9))
    writes.reverse.foreach((offset, size) => ranges.add(offset, size))
    val expected = writes.flatMap((offset, size) => offset until offset + size).toSet
    val actual   = ranges.chunks(65536).flatMap((offset, size) => Vec.from(offset until offset + size))
    assertEquals(actual.toSet, expected)
    assertEquals(actual.size, expected.size)
  }
}
