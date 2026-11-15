package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import scala.collection.mutable
import scala.util.Using

class ConcurrencyReplaySuite extends Suite {
  import ConcurrencyScheduleKernels.*

  test("ReplayDevice reproduces a seeded launch") {
    val image = ISA.encode(spinModule()).value
    Using.resource(WorkerPool(1, minGroups = 1L, minWorkItems = 1L)) { pool =>
      val memory = ArrayMemory()
      val region = memory.allocate(8L)
      val replay = ReplayDevice(
        BackendRegistry(Vec(SPIRVBackend(Validation.Environment.OpenCLCompute, workgroups = pool))),
        memory,
        TaskControl.Limits(2000000L),
        ConcurrencySchedule.Random(7L)
      )
      val task = KernelTask.validated(
        "spin",
        testDimensions(1),
        testDimensions(2),
        Vec(buffer(region, 0L, Some(8L)))
      ).value
      val checkpoints = mutable.ArrayBuffer.empty[ReplayDevice.Checkpoint]
      val whole       = replay.launch(
        ProgramFormat.SPIRVOpenCLCompute,
        image,
        task,
        (checkpoint, _, _) => Right(checkpoints += checkpoint).map(_ => ())
      )
      assert(whole.isRight, whole)
      val finished = LittleEndian.decodeInt32(memory.bytes(region), 4)
      assertEquals(finished, 1)

      val resumed = replay.resume(checkpoints(10), (_, _, _) => Right(()))
      assert(resumed.isRight, resumed)
      assertEquals(LittleEndian.decodeInt32(memory.bytes(region), 4), finished)
    }
  }

}
