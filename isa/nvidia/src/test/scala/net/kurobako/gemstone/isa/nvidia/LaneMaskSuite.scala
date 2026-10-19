package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.constraint.*

class LaneMaskSuite extends munit.FunSuite {
  private def size(mask: VMLaneMask): Int = mask.positiveSize.fold(0)(_.raw)

  test("mask operations preserve their inputs across warp and word boundaries") {
    val lanes = Set(0, 31, 32, 63, 64, 127, 511, 512, VMLaneMask.MaximumLanes - 1)
    val sets  = Vector(Set.empty[Int], lanes, lanes.filter(_ % 2 == 0), lanes.filter(_ >= 64)) ++
      lanes.toVector.sorted.map(Set(_))
    val full                                 = VMLaneMask.all(1024)
    def contents(mask: VMLaneMask): Set[Int] = {
      val result = Set.newBuilder[Int]
      mask.foreachLane { lane =>
        val _ = result += lane.toInt
      }
      result.result()
    }
    for {
      left  <- sets
      right <- sets
    } {
      val a       = full.filterLanes(lane => left.contains(lane.toInt))
      val b       = full.filterLanes(lane => right.contains(lane.toInt))
      val results = Vector(
        a.intersect(b)                                    -> (left.intersect(right)),
        a.union(b)                                        -> (left.union(right)),
        a.subtract(b)                                     -> (left.diff(right)),
        a.filterLanes(lane => right.contains(lane.toInt)) -> (left.intersect(right))
      )
      results.foreach { case (mask, expected) =>
        assertEquals(contents(mask), expected)
        assertEquals(size(mask), expected.size)
        assertEquals(mask.isEmpty, expected.isEmpty)
      }
      assertEquals(contents(a), left)
      assertEquals(contents(b), right)
      assertEquals(contents(a.intersect(a)), left)
      assertEquals(contents(a.union(a)), left)
      assertEquals(contents(a.subtract(a)), Set.empty[Int])
    }
    assertEquals(size(full), VMLaneMask.MaximumLanes)
  }

  test("grouped lane arrivals deduplicate and retain independent snapshots") {
    val builder    = VMLaneMask.Builder()
    val empty      = builder.result()
    val firstLanes = Set(0, 31, 32, 63, 64, 1023)
    firstLanes.foreach { lane =>
      builder.add(WorkgroupLane.fromIndex(lane))
      builder.add(WorkgroupLane.fromIndex(lane))
    }
    val first       = builder.result()
    val secondLanes = Set(1, 65, 127, 512)
    secondLanes.foreach(lane => builder.add(WorkgroupLane.fromIndex(lane)))
    val second = builder.result()
    assertEquals(size(empty), 0)
    assertEquals(size(first), firstLanes.size)
    val allLanes = firstLanes ++ secondLanes
    assertEquals(size(second), allLanes.size)
    (0 until VMLaneMask.MaximumLanes).foreach { lane =>
      assertEquals(first.contains(lane), firstLanes(lane))
      assertEquals(second.contains(lane), allLanes(lane))
    }
    assert(scala.compiletime.testing.typeCheckErrors("VMLaneMask.Builder().add(-1)").nonEmpty)
    assert(scala.compiletime.testing.typeCheckErrors("VMLaneMask.Builder().add(1024)").nonEmpty)
  }

}
