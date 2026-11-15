package net.kurobako.gemstone.isa.ptx

import execution.*
import java.nio.charset.StandardCharsets
import java.util.concurrent.ConcurrentHashMap
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.isa.ptx.asm.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import scala.collection.mutable
import scala.jdk.CollectionConverters.*
import scala.util.Using

class ConcurrencyReplaySuite extends Suite with ConcurrencyScheduleKernels {

  test("ReplayDevice reproduces a seeded launch") {
    Using.resource(WorkerPool(1, minGroups = 1L, minWorkItems = 1L)) { pool =>
      val memory  = DeviceMemory(4096)
      val backend = PTXBackend(
        workgroups = pool,
        text = instruction => Some(Printer.render(instruction))
      )
      val task = KernelTask.validated(
        "wg_wait",
        Dimensions(1, 1, 1),
        Dimensions(2, 1, 1),
        Vec(KernelTask.Argument.Memory(AllocationId(1L), 0L))
      ).value
      val replay = ReplayDevice(
        BackendRegistry(Vec(backend)),
        memory,
        TaskControl.Limits(2000000L),
        ConcurrencySchedule.Random(7L)
      )
      val checkpoints = mutable.ArrayBuffer.empty[ReplayDevice.Checkpoint]
      val whole       = replay.launch(
        ProgramFormat.PTXText,
        Vec.from(spinSource.getBytes(StandardCharsets.UTF_8)),
        task,
        (checkpoint, _, _) => Right(checkpoints += checkpoint).map(_ => ())
      )
      assertEquals(whole.map(_.size), Right(0))
      val finished = memory.ints(4L, 1)
      assertEquals(finished, Vec(1))

      val resumed = replay.resume(checkpoints(10), (_, _, _) => Right(()))
      assertEquals(resumed.map(_.size), Right(0))
      assertEquals(memory.ints(4L, 1), finished)
    }
  }

  test("a seeded cluster launch replays exactly") {
    Using.resource(WorkerPool(2, minGroups = 1L, minWorkItems = 1L)) { pool =>
      val memory = DeviceMemory(4096)
      assertEquals(memory.writeInt32(memory.start, 1), Right(()))
      val backend = PTXBackend(
        workgroups = pool,
        text = instruction => Some(Printer.render(instruction))
      )
      val task = KernelTask.validated(
        "wg_order",
        Dimensions(1, 1, 1),
        Dimensions(4, 1, 1),
        Vec(KernelTask.Argument.Memory(AllocationId(1L), 0L)),
        cluster = Some(Dimensions(2, 1, 1))
      ).value
      val replay = ReplayDevice(
        BackendRegistry(Vec(backend)),
        memory,
        TaskControl.Limits(2000000L),
        ConcurrencySchedule.Random(7L)
      )
      val checkpoints = mutable.ArrayBuffer.empty[ReplayDevice.Checkpoint]
      val whole       = replay.launch(
        ProgramFormat.PTXText,
        Vec.from(workgroupOrderSource.getBytes(StandardCharsets.UTF_8)),
        task,
        (checkpoint, _, _) => Right(checkpoints += checkpoint).map(_ => ())
      )
      assertEquals(whole.map(_.size), Right(0))
      val finished = memory.ints(0L, 1).head

      val resumed = replay.resume(checkpoints(checkpoints.size / 2), (_, _, _) => Right(()))
      assertEquals(resumed.map(_.size), Right(0))
      assertEquals(memory.ints(0L, 1).head, finished)
    }
  }

  test("a seeded cluster launch runs on the calling thread") {
    val kernel = compileKernel(""".version 8.0
      |.target sm_100
      |.address_size 64
      |.visible .entry noop() {
      |  ret;
      |}""".stripMargin)
    val memory   = DeviceMemory(64)
    val threads  = ConcurrentHashMap.newKeySet[String]()
    val observer = new LaunchObserver {
      def instruction(at: Fault.Location, state: ArchitecturalState): Either[Fault, Unit] = {
        threads.add(Thread.currentThread().getName)
        Right(())
      }
    }
    Using.resource(WorkerPool(2, minGroups = 1L, minWorkItems = 1L)) { pool =>
      val caller = Thread.currentThread().getName
      val result = execute(
        kernel,
        4,
        32,
        Map.empty[NonEmpty[String], Argument],
        memory,
        TaskControl(observer = Some(observer)),
        workgroups = pool,
        cluster = Some(Dimensions(2, 1, 1)),
        schedule = ConcurrencySchedule.Random(7L)
      )
      assertEquals(result, Right(()))
      assertEquals(threads.asScala.toSet, Set(caller))
    }
  }

  test("a checkpoint replays under the schedule that made it") {
    Using.resource(WorkerPool(1, minGroups = 1L, minWorkItems = 1L)) { pool =>
      val fixed  = workgroupOrder(ConcurrencySchedule.Fixed)
      val seed   = Seq(0L, 1L, 2L).find(candidate => workgroupOrder(ConcurrencySchedule.Random(candidate)) != fixed).get
      val memory = DeviceMemory(4096)
      assertEquals(memory.writeInt32(memory.start, 1), Right(()))
      val backend = PTXBackend(
        workgroups = pool,
        text = instruction => Some(Printer.render(instruction))
      )
      val task = KernelTask.validated(
        "wg_order",
        Dimensions(1, 1, 1),
        Dimensions(2, 1, 1),
        Vec(KernelTask.Argument.Memory(AllocationId(1L), 0L))
      ).value
      val seeded = ReplayDevice(
        BackendRegistry(Vec(backend)),
        memory,
        TaskControl.Limits(2000000L),
        ConcurrencySchedule.Random(seed)
      )
      val checkpoints = mutable.ArrayBuffer.empty[ReplayDevice.Checkpoint]
      val whole       = seeded.launch(
        ProgramFormat.PTXText,
        Vec.from(workgroupOrderSource.getBytes(StandardCharsets.UTF_8)),
        task,
        (checkpoint, _, _) => Right(checkpoints += checkpoint).map(_ => ())
      )
      assertEquals(whole.map(_.size), Right(0))
      val expected = memory.ints(0L, 1).head
      assertNotEquals(expected, fixed)

      val defaultDevice = ReplayDevice(BackendRegistry(Vec(backend)), memory)
      val resumed       = defaultDevice.resume(checkpoints(5), (_, _, _) => Right(()))
      assertEquals(resumed.map(_.size), Right(0))
      assertEquals(memory.ints(0L, 1).head, expected)
    }
  }
}
