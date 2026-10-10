package external

import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.ptx.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import scala.collection.mutable

class PTXReplaySuite extends munit.FunSuite {
  private val image = Vec.from(
    """.version 8.0
      |.target sm_80
      |.address_size 64
      |.visible .entry squares(.param .u64 output) {
      |  .reg .b32 %r<5>;
      |  .reg .b64 %rd<3>;
      |  ld.param.u64 %rd0, [output];
      |  mov.u32 %r2, %ctaid.x;
      |  mov.u32 %r3, %ntid.x;
      |  mov.u32 %r4, %tid.x;
      |  mad.lo.u32 %r0, %r2, %r3, %r4;
      |  mul.lo.u32 %r1, %r0, %r0;
      |  mul.wide.u32 %rd1, %r0, 4;
      |  add.u64 %rd2, %rd0, %rd1;
      |  st.global.u32 [%rd2], %r1;
      |  ret;
      |}
      |""".stripMargin.getBytes(StandardCharsets.UTF_8)
  )

  private def device(memory: ArrayMemory) = ReplayDevice(BackendRegistry(Vec(PTXBackend())), memory)

  private def task(output: AllocationId): KernelTask =
    KernelTask.validated(
      "squares",
      Dimensions(4, 1, 1),
      Dimensions(2, 1, 1),
      Vec(KernelTask.Argument.Memory(output, 0L))
    )
      .value

  test("a launch stopped at a checkpoint resumes there and finishes as an uninterrupted launch would") {
    val whole       = ArrayMemory()
    val wholeOutput = whole.allocate(32L)
    assert(device(whole).launch(ProgramFormat.PTXText, image, task(wholeOutput), (_, _, _) => Right(())).isRight)

    val memory  = ArrayMemory()
    val output  = memory.allocate(32L)
    val replay  = device(memory)
    val points  = mutable.ArrayBuffer.empty[(ReplayDevice.Checkpoint, Fault.Location)]
    val stopped = replay.launch(
      ProgramFormat.PTXText,
      image,
      task(output),
      (checkpoint, at, _) =>
        if (checkpoint.step == 9L) Left(Fault.Cancelled)
        else Right(points += checkpoint -> at).map(_ => ())
    )
    assertEquals(stopped, Left(Fault.Cancelled))
    assertNotEquals(memory.bytes(output), whole.bytes(wholeOutput))

    val (checkpoint, location) = points(5)
    val resumed                = mutable.ArrayBuffer.empty[(Long, Fault.Location)]
    assert(replay.resume(checkpoint, (point, at, _) => Right(resumed += point.step -> at).map(_ => ())).isRight)
    assertEquals(resumed.head, 5L -> location)
    assertEquals(memory.bytes(output), whole.bytes(wholeOutput))
  }

  test("resuming restores device memory to the launch's starting contents first") {
    val memory = ArrayMemory()
    val output = memory.allocate(32L)
    val replay = device(memory)
    val first  = mutable.ArrayBuffer.empty[ReplayDevice.Checkpoint]
    assert(replay.launch(
      ProgramFormat.PTXText,
      image,
      task(output),
      (point, _, _) => Right(first += point).map(_ => ())
    )
      .isRight)
    val finished = memory.bytes(output)
    memory.array(output)(0) = 99
    val observed = java.util.concurrent.atomic.AtomicInteger()
    assert(replay.resume(first.head, (_, _, _) => Right(observed.incrementAndGet()).map(_ => ())).isRight)
    assertEquals(memory.bytes(output), finished)
    assertEquals(observed.get(), first.size)
  }
}
