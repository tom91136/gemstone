package net.kurobako.gemstone.scheduler

class LaunchShapeSuite extends munit.FunSuite {
  test("launch dimensions must be positive") {
    assertEquals(LaunchShape.create(2, 3).map(shape => shape.blocks -> shape.threadsPerBlock), Right(2 -> 3))
    assert(LaunchShape.create(0, 3).isLeft)
    assert(LaunchShape.create(2, 0).isLeft)
  }
}
