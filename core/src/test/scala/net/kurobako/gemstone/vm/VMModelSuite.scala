package net.kurobako.gemstone.vm

import java.util.concurrent.atomic.AtomicInteger
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*

class VMModelSuite extends munit.FunSuite {
  private val booleans = Vec(false, true)

  test("inactive groups remain inactive across the Int narrowing boundary") {
    val geometry = LaunchGeometry.forLaunch(
      LaunchGeometry.Dimensions(1024, 1, 1),
      LaunchGeometry.Dimensions(4194306, 1, 1),
      Some(LaunchGeometry.GlobalDimensions(1L, 1L, 1L))
    ).toOption.get
    for (group <- Seq[Int >= 0](1, 2097152, 4194304, 4194305))
      assert(geometry.activeGroupSize(LaunchGeometry.Coordinates(group, 0, 0)).isLeft)
    assertEquals(
      geometry.activeGroupSize(LaunchGeometry.Coordinates.origin),
      Right(LaunchGeometry.Dimensions.One)
    )
  }

  test("kernel task excludes runtime identity and validates relocation sources") {
    val arguments = Vec[KernelTask.Argument](KernelTask.Argument.Memory(AllocationId(7L), 0L))
    val valid     = KernelTask.validated(
      "fill",
      LaunchGeometry.Dimensions.One,
      LaunchGeometry.Dimensions.One,
      arguments,
      pointerRelocations = Vec(
        KernelTask.PointerRelocation(
          KernelTask.RelocationSource.Argument(0),
          0L,
          AllocationId(8L),
          0L
        )
      )
    )
    assert(valid.isRight)

    val invalid = KernelTask.validated(
      "fill",
      LaunchGeometry.Dimensions.One,
      LaunchGeometry.Dimensions.One,
      arguments,
      pointerRelocations = Vec(
        KernelTask.PointerRelocation(
          KernelTask.RelocationSource.Argument(1),
          0L,
          AllocationId(8L),
          0L
        )
      )
    )
    assert(invalid.isLeft)
    val invalidUpdate = valid.toOption.get.withPointerRelocations(Vec(
      KernelTask.PointerRelocation(KernelTask.RelocationSource.Argument(1), 0L, AllocationId(8L), 0L)
    ))
    assert(invalidUpdate.isLeft)
    assertEquals(valid.toOption.get.withPointerRelocations(Vec.empty), valid)
  }

  test("argument buffers exclude structured arguments and inferred argument relocations") {
    val buffer              = KernelTask.ArgumentBuffer(AllocationId(7L), 0L)
    val structuredArguments = Vec(KernelTask.Argument.Memory(buffer.region, 0L))
    for {
      structured <- booleans
      discover   <- booleans
    } {
      val task = KernelTask.validated(
        kernelName = "fill",
        groupSize = LaunchGeometry.Dimensions.One,
        groupCount = LaunchGeometry.Dimensions.One,
        arguments = if (structured) structuredArguments else Vec.empty,
        discoverArgumentRelocations = discover,
        argumentBuffer = Some(buffer)
      )
      assertEquals(task.isRight, !structured && !discover)
    }
  }

  test("launch geometry maps partial work-group extents") {
    val geometry = LaunchGeometry
      .forLaunch(
        groupSize = LaunchGeometry.Dimensions(4, 3, 2),
        groupCount = LaunchGeometry.Dimensions(3, 2, 2),
        globalSize = Some(LaunchGeometry.GlobalDimensions(
          10L,
          5L,
          3L
        )),
        workDim = 3,
        globalOffset =
          LaunchGeometry.Coordinates(11, 0, 0)
      )
      .fold(message => fail(message), identity)

    assertEquals(
      geometry.activeGroupSize(LaunchGeometry.Coordinates(
        2,
        1,
        1
      )),
      Right(LaunchGeometry.Dimensions(2, 2, 1))
    )
    assertEquals(
      geometry.globalCoordinates(LaunchGeometry.Coordinates(1, 1, 0), LaunchGeometry.Coordinates(1, 2, 1)),
      LaunchGeometry.LongCoordinates(16L, 5L, 1L)
    )
    assert(geometry.containsGlobal(LaunchGeometry.LongCoordinates(
      11L,
      0L,
      0L
    )))
    assert(!geometry.containsGlobal(LaunchGeometry.LongCoordinates(
      10L,
      0L,
      0L
    )))
  }

  test("task control accounts for instructions and observes cancellation") {
    val limited =
      TaskControl(TaskControl.Limits(maxInstructions = 2L))
    assert(limited.checkpoint().isRight)
    assert(limited.checkpoint().isRight)
    assertEquals(limited.checkpoint(), Left(Fault.BudgetExceeded(2L)))
    assert(limited.usedInstructions == 2L)

    val cancelled = TaskControl()
    cancelled.cancel()
    cancelled.cancel()
    assert(cancelled.isCancelled)
    assertEquals(cancelled.checkpoint(), Left(Fault.Cancelled))
  }

  test("default controls have independent cancellation flags") {
    val first  = TaskControl()
    val second = TaskControl()
    first.cancel()
    assertEquals(first.checkpoint(), Left(Fault.Cancelled))
    assertEquals(first.usedInstructions.raw, 0L)
    assertEquals(second.checkpoint(), Right(()))
  }

  test("sequential scheduling stops at the first fault") {
    val visited = AtomicInteger()
    val result  = Workgroups.Sequential.forEach(5L, parallel = false) { index =>
      val _ = visited.incrementAndGet()
      if (index == 2L) Left(Fault.Backend("stop")) else Right(())
    }
    assertEquals(result, Left(Fault.Backend("stop")))
    assertEquals(visited.get(), 3)
  }
}
