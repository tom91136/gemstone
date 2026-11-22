package net.kurobako.gemstone.memory

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.*

class WrittenGranulesSuite extends Suite {
  private type Chunk = (Long >= 0, Int > 0)

  test("a write marks every granule it touches and nothing else") {
    val granules = WrittenGranules(1000L, 16)
    granules.add(20, 1)
    granules.add(70, 1)
    assertEquals(granules.chunks(1000), Vec[Chunk]((16, 16), (64, 16)))
  }

  test("touching granules join into one run and chunk at the requested size") {
    val granules = WrittenGranules(4096L, 16)
    granules.add(0, 16)
    granules.add(16, 16)
    granules.add(100, 1)
    assertEquals(granules.chunks(24), Vec[Chunk]((0, 24), (24, 8), (96, 16)))
  }

  test("runs spanning words are found whole") {
    val granules = WrittenGranules(1L << 20, 16)
    granules.add(16 * 60, 16 * 200)
    granules.add(16 * 1000, 1)
    assertEquals(granules.chunks(1 << 20), Vec[Chunk]((16 * 60, 16 * 200), (16 * 1000, 16)))
  }

  test("the last granule is clipped to the extent") {
    val granules = WrittenGranules(70L, 16)
    granules.add(69, 1)
    granules.add(0, 1)
    assertEquals(granules.chunks(64), Vec[Chunk]((0, 16), (64, 6)))
  }

  test("an empty write records nothing and the size depends only on the extent") {
    val granules = WrittenGranules(1L << 30, 4096)
    val before   = granules.retainedBytes
    granules.add(5, 0)
    assertEquals(granules.chunks(64), Vec.empty[Chunk])
    (0L until (1L << 30) by 512L).foreach(offset => granules.add(offset.unsafe[Long >= 0], 32))
    assertEquals(granules.retainedBytes, before)
    assertEquals(before, (1L << 30) / 4096L / 8L)
  }
}
