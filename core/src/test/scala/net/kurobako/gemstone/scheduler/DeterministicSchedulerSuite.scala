package net.kurobako.gemstone.scheduler

class DeterministicSchedulerSuite extends munit.FunSuite {
  test("lanes have a stable block-major order") {
    val shape = LaunchShape.create(2, 3).toOption.get
    assertEquals(
      DeterministicScheduler.schedule(shape),
      Vector(Lane(0, 0), Lane(0, 1), Lane(0, 2), Lane(1, 0), Lane(1, 1), Lane(1, 2))
    )
  }
}
