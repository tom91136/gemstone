package net.kurobako.gemstone.vm

import net.kurobako.gemstone.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class ConcurrencyScheduleSuite extends Suite {

  private def seed(schedule: ConcurrencySchedule): Option[Long] = schedule match {
    case random: ConcurrencySchedule.Random => Some(random.seed)
    case _                                  => None
  }

  test("parses fixed schedules") {
    assertEquals(ConcurrencySchedule.parse(""), Right(ConcurrencySchedule.Fixed))
    assertEquals(ConcurrencySchedule.parse("fixed"), Right(ConcurrencySchedule.Fixed))
    assertEquals(ConcurrencySchedule.parse("  fixed  "), Right(ConcurrencySchedule.Fixed))
  }

  test("parses random schedules with 64-bit integer seeds") {
    assertEquals(ConcurrencySchedule.parse("random:0").toOption.flatMap(seed(_)), Some(0L))
    assertEquals(ConcurrencySchedule.parse("random:1234567890123").toOption.flatMap(seed(_)), Some(1234567890123L))
    assertEquals(ConcurrencySchedule.parse("random:-42").toOption.flatMap(seed(_)), Some(-42L))
    assert(ConcurrencySchedule.parse("random:not-a-number").isLeft)
    assert(ConcurrencySchedule.parse("random:").isLeft)
  }

  test("rejects PCT schedule as unimplemented") {
    assert(ConcurrencySchedule.parse("pct:123:5").isLeft)
  }

  test("rejects unknown schedules") {
    assert(ConcurrencySchedule.parse("round-robin").isLeft)
  }

  test("reads schedule from environment map") {
    assertEquals(ConcurrencySchedule.fromEnvironment(Map.empty), Right(ConcurrencySchedule.Fixed))
    assertEquals(
      ConcurrencySchedule.fromEnvironment(Map(environment.Schedule -> "random:777")).toOption.flatMap(seed(_)),
      Some(777L)
    )
    assert(ConcurrencySchedule.fromEnvironment(Map(environment.Schedule -> "garbage")).isLeft)
  }

  test("fixed schedule always chooses candidate zero") {
    for (candidates <- 1 to 10)
      assertEquals(ConcurrencySchedule.Fixed.choose(candidates), 0)
  }

  test("random schedule chooses within bounds and reproduces sequence for same seed") {
    val seed     = 42L
    val run1     = ConcurrencySchedule.Random(seed)
    val run2     = ConcurrencySchedule.Random(seed)
    val choices1 = (1 to 20).map(candidates => run1.choose(candidates))
    val choices2 = (1 to 20).map(candidates => run2.choose(candidates))
    assertEquals(choices1, choices2)
    choices1.zipWithIndex.foreach { case (choice, idx) =>
      val candidates = idx + 1
      assert(choice >= 0 && choice < candidates)
    }
  }

  test("workgroup schedules derive deterministic child seeds") {
    val base   = ConcurrencySchedule.Random(100L)
    val origin = base.forWorkgroup(Coordinates.origin)
    val groupA = base.forWorkgroup(Coordinates(1, 0, 0))
    val groupB = base.forWorkgroup(Coordinates(1, 0, 0))
    val groupC = base.forWorkgroup(Coordinates(0, 1, 0))
    assertEquals(seed(origin), seed(base.forWorkgroup(Coordinates.origin)))
    assertEquals(seed(groupA), seed(groupB))
    assertNotEquals(seed(groupA), seed(groupC))
  }

  test("workgroup schedules do not share generator state between launches") {
    val schedule            = ConcurrencySchedule.Random(100L)
    def choices(): Seq[Int] = {
      val origin = schedule.forWorkgroup(Coordinates.origin)
      (1 to 20).map(candidates => origin.choose(candidates))
    }
    assertEquals(choices(), choices())
  }
}
