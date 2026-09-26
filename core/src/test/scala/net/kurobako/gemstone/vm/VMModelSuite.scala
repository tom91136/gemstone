package net.kurobako.gemstone.vm

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.isa.KernelName

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import java.util.concurrent.atomic.AtomicInteger

final class VMModelSuite extends munit.FunSuite {
  test("kernel task excludes runtime identity and validates relocation sources") {
    val arguments = Vec[KernelTask.Argument](KernelTask.Argument.Memory(MemoryRegion(7L), 0L))
    val valid     = KernelTask.validated(
      KernelName("fill"),
      LaunchGeometry.Dimensions.One,
      LaunchGeometry.Dimensions.One,
      arguments,
      pointerRelocations = Vec(
        KernelTask.PointerRelocation(
          KernelTask.RelocationSource.Argument(0),
          0L,
          MemoryRegion(8L),
          0L
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
          KernelTask.RelocationSource.Argument(1),
          0L,
          MemoryRegion(8L),
          0L
        )
      )
    )
    assert(invalid.isLeft)
  }

  test("launch geometry maps partial and virtual work-group extents") {
    val geometry = LaunchGeometry
      .forLaunch(
        groupSize = LaunchGeometry.Dimensions(4, 3, 2),
        groupCount = LaunchGeometry.Dimensions(3, 2, 2),
        globalSize = Some(LaunchGeometry.GlobalDimensions(10, 5, 3)),
        workDim = 3,
        globalOffset = LaunchGeometry.Coordinates(11, 0, 0)
      )
      .fold(message => fail(message), identity)

    assertEquals(
      geometry.activeGroupSize(LaunchGeometry.Coordinates(2, 1, 1)),
      Right(LaunchGeometry.Dimensions(2, 2, 1))
    )
    assertEquals(
      geometry.globalCoordinatesLong(LaunchGeometry.Coordinates(1, 1, 0), LaunchGeometry.Coordinates(1, 2, 1)),
      Right(LaunchGeometry.LongCoordinates(16, 5, 1))
    )
    assert(geometry.containsGlobal(LaunchGeometry.LongCoordinates(11, 0, 0)))
    assert(!geometry.containsGlobal(LaunchGeometry.LongCoordinates(10, 0, 0)))

    val virtual = LaunchGeometry
      .virtual(LaunchGeometry.Dimensions(256, 1, 1), LaunchGeometry.Dimensions(16777216, 1, 1))
      .fold(message => fail(message), identity)
    assertEquals(virtual.globalExtent, LaunchGeometry.GlobalDimensions(4294967296L, 1L, 1L))
    assertEquals(
      virtual.globalCoordinatesLong(
        LaunchGeometry.Coordinates(16777215, 0, 0),
        LaunchGeometry.Coordinates(255, 0, 0)
      ),
      Right(LaunchGeometry.LongCoordinates(4294967295L, 0L, 0L))
    )
  }

  test("task control accounts for instructions and observes cancellation") {
    val limited = TaskControl.forScheduler(TaskLimits(maxInstructions = 2L, quantum = 1))
    assert(limited.checkpoint().isRight)
    assert(limited.checkpoint().isRight)
    assertEquals(limited.checkpoint(), Left(Fault.BudgetExceeded(2L)))
    assert(limited.usedInstructions == 2L)

    val cancellation = TaskControl.Cancellation.Source()
    val cancelled    = TaskControl.forScheduler(cancellation = cancellation)
    cancellation.cancel()
    assertEquals(cancelled.checkpoint(), Left(Fault.Cancelled))
  }

  test("sequential scheduling stops at the first fault") {
    val visited = AtomicInteger()
    val result  = Scheduler.Sequential.forEach(5L, parallel = false) { index =>
      val _ = visited.incrementAndGet()
      if (index == 2L) Left(Fault.Backend("stop")) else Right(())
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
