package net.kurobako.gemstone.vm

import net.kurobako.gemstone.{NonNegativeInt, NonNegativeLong, PositiveInt, PositiveLong, Vec, WorkDimension}
import net.kurobako.gemstone.isa.KernelName

import java.util.concurrent.atomic.AtomicInteger

final class VMModelSuite extends munit.FunSuite {
  test("kernel task excludes runtime identity and validates relocation sources") {
    val arguments = Vec[KernelTask.Argument](KernelTask.Argument.Memory(MemoryRegion(7L), NonNegativeLong.Zero))
    val valid     = KernelTask.validated(
      KernelName("fill"),
      LaunchGeometry.Dimensions.One,
      LaunchGeometry.Dimensions.One,
      arguments,
      pointerRelocations = Vec(
        KernelTask.PointerRelocation(
          KernelTask.RelocationSource.Argument(NonNegativeInt.Zero),
          NonNegativeLong.Zero,
          MemoryRegion(8L),
          NonNegativeLong.Zero
        )
      )
    )
    assert(valid.isRight)

    val invalid = KernelTask.validated(
      KernelName("fill"),
      LaunchGeometry.Dimensions.One,
      LaunchGeometry.Dimensions.One,
      arguments,
      pointerRelocations = Vec(
        KernelTask.PointerRelocation(
          KernelTask.RelocationSource.Argument(NonNegativeInt(1)),
          NonNegativeLong.Zero,
          MemoryRegion(8L),
          NonNegativeLong.Zero
        )
      )
    )
    assert(invalid.isLeft)
  }

  test("launch geometry maps partial and virtual work-group extents") {
    val geometry = LaunchGeometry
      .forLaunch(
        groupSize = LaunchGeometry.Dimensions(PositiveInt(4), PositiveInt(3), PositiveInt(2)),
        groupCount = LaunchGeometry.Dimensions(PositiveInt(3), PositiveInt(2), PositiveInt(2)),
        globalSize = Some(LaunchGeometry.GlobalDimensions(
          PositiveLong(10L),
          PositiveLong(5L),
          PositiveLong(3L)
        )),
        workDim = WorkDimension.Three,
        globalOffset =
          LaunchGeometry.Coordinates(NonNegativeInt(11), NonNegativeInt(0), NonNegativeInt(0))
      )
      .fold(message => fail(message), identity)

    assertEquals(
      geometry.activeGroupSize(LaunchGeometry.Coordinates(
        NonNegativeInt(2),
        NonNegativeInt(1),
        NonNegativeInt(1)
      )),
      Right(LaunchGeometry.Dimensions(PositiveInt(2), PositiveInt(2), PositiveInt(1)))
    )
    assertEquals(
      geometry.globalCoordinatesLong(
        LaunchGeometry.Coordinates(NonNegativeInt(1), NonNegativeInt(1), NonNegativeInt(0)),
        LaunchGeometry.Coordinates(NonNegativeInt(1), NonNegativeInt(2), NonNegativeInt(1))
      ),
      Right(LaunchGeometry.LongCoordinates(
        NonNegativeLong(16L),
        NonNegativeLong(5L),
        NonNegativeLong(1L)
      ))
    )
    assert(geometry.containsGlobal(LaunchGeometry.LongCoordinates(
      NonNegativeLong(11L),
      NonNegativeLong(0L),
      NonNegativeLong(0L)
    )))
    assert(!geometry.containsGlobal(LaunchGeometry.LongCoordinates(
      NonNegativeLong(10L),
      NonNegativeLong(0L),
      NonNegativeLong(0L)
    )))

    val virtual = LaunchGeometry
      .virtual(
        LaunchGeometry.Dimensions(PositiveInt(256), PositiveInt(1), PositiveInt(1)),
        LaunchGeometry.Dimensions(PositiveInt(16777216), PositiveInt(1), PositiveInt(1))
      )
      .fold(message => fail(message), identity)
    assertEquals(
      virtual.globalExtent,
      LaunchGeometry.GlobalDimensions(
        PositiveLong(4294967296L),
        PositiveLong(1L),
        PositiveLong(1L)
      )
    )
    assertEquals(
      virtual.globalCoordinatesLong(
        LaunchGeometry.Coordinates(NonNegativeInt(16777215), NonNegativeInt(0), NonNegativeInt(0)),
        LaunchGeometry.Coordinates(NonNegativeInt(255), NonNegativeInt(0), NonNegativeInt(0))
      ),
      Right(LaunchGeometry.LongCoordinates(
        NonNegativeLong(4294967295L),
        NonNegativeLong(0L),
        NonNegativeLong(0L)
      ))
    )
  }

  test("task control accounts for instructions and observes cancellation") {
    val limited =
      TaskControl.forScheduler(TaskLimits(maxInstructions = PositiveLong(2L), quantum = PositiveInt(1)))
    assert(limited.checkpoint().isRight)
    assert(limited.checkpoint().isRight)
    assertEquals(limited.checkpoint(), Left(Fault.BudgetExceeded(PositiveLong(2L))))
    assert(limited.usedInstructions.raw == 2L)

    val cancellation = TaskControl.Cancellation.Source()
    val cancelled    = TaskControl.forScheduler(cancellation = cancellation)
    cancellation.cancel()
    assertEquals(cancelled.checkpoint(), Left(Fault.Cancelled))
  }

  test("sequential scheduling stops at the first fault") {
    val visited = AtomicInteger()
    val result  = Scheduler.Sequential.forEach(NonNegativeLong(5L), parallel = false) { index =>
      val _ = visited.incrementAndGet()
      if (index.raw == 2L) Left(Fault.Backend("stop")) else Right(())
    }
    assertEquals(result, Left(Fault.Backend("stop")))
    assertEquals(visited.get(), 3)
  }

  test("observer failure is explicit") {
    val observer: Observer[Event] = (context, _) => Left(Observer.Error(s"closed:${context.value}"))
    assertEquals(
      observer.observe(
        ObservationContext(7L),
        Event.Print("value=%d", Vec(Event.PrintArgument.Integer(Vec(1L), 32)))
      ),
      Left(Observer.Error("closed:7"))
    )
  }
}
