package net.kurobako.gemstone.vm

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.either.*
import scala.util.Using

class DeviceSuite extends munit.FunSuite {
  private val format = ProgramFormat("test")

  /** Writes the image's first byte to the first argument's allocation, printing it and charging one instruction per
    * byte of image.
    */
  private open class EchoProgram(image: Vec[Byte]) extends Backend.Program {
    override def symbols: Either[Fault, ProgramSymbols]                        = Left(Fault.Unsupported("no symbols"))
    override def kernel(name: NonEmpty[String]): Either[Fault, KernelMetadata] = Left(Fault.Unsupported("no kernels"))
    override def execute(
        task: KernelTask,
        memory: AddressSpace,
        control: TaskControl,
        handler: Event => Either[Fault, Unit]
    ): Either[Fault, Unit] = task.arguments.head match {
      case KernelTask.Argument.Memory(region, offset, _) =>
        for {
          _ <- control.checkpoint(image.size.toLong.refine[Long > 0].value)
          _ <- memory.write(region, offset, image.take(1))
          _ <- handler(Event.Print("%d", Vec(Event.PrintArgument.Integer(Vec(image.head.toLong), 8))))
        } yield ()
      case other => Left(Fault.InvalidRequest(s"unexpected argument $other"))
    }
  }

  private class EchoBackend(override val format: ProgramFormat) extends Backend {
    override def load(image: Vec[Byte]): Either[Fault, Backend.Program] =
      if (image.isEmpty) Left(Fault.InvalidRequest("empty image")) else Right(EchoProgram(image))
  }

  private def task(region: AllocationId): KernelTask = KernelTask.validated(
    "echo",
    LaunchGeometry.Dimensions.One,
    LaunchGeometry.Dimensions.One,
    Vec(KernelTask.Argument.Memory(region, 0L))
  ).value

  test("a device loads through the backend for the format and launches synchronously against its memory") {
    val memory = ArrayMemory()
    val output = memory.allocate(4L)
    val device = Device(BackendRegistry(Vec(EchoBackend(format))), memory)
    Using.resource(device.load(format, Vec[Byte](42)).value) { program =>
      assertEquals(device.launch(program, task(output)).map(_.size), Right(1))
      assertEquals(memory.bytes(output), Vec[Byte](42, 0, 0, 0))
    }
  }

  test("launches report the events the program raised") {
    val memory  = ArrayMemory()
    val device  = Device(BackendRegistry(Vec(EchoBackend(format))), memory)
    val program = device.load(format, Vec[Byte](7)).value
    device.launch(program, task(memory.allocate(1L))).value match {
      case Vec(print: Event.Print) => assertEquals(PrintRenderer.render(print, 64), Right("7"))
      case other                   => fail(s"unexpected events $other")
    }
  }

  test("formats without a backend and rejected images fail the load") {
    val device = Device(BackendRegistry(Vec(EchoBackend(format))), ArrayMemory())
    assert(device.load(ProgramFormat("other"), Vec[Byte](1)).left.exists {
      case Fault.Unsupported(_) => true; case _ => false
    })
    assert(device.load(format, Vec.empty).left.exists { case Fault.InvalidRequest(_) => true; case _ => false })
  }

  test("every launch gets a fresh allowance from the device limits") {
    val memory = ArrayMemory()
    val output = memory.allocate(1L)
    val device = Device(BackendRegistry(Vec(EchoBackend(format))), memory, TaskControl.Limits(3L))
    val small  = device.load(format, Vec[Byte](1, 2, 3)).value
    val large  = device.load(format, Vec[Byte](1, 2, 3, 4)).value
    assert(device.launch(small, task(output)).isRight)
    assert(device.launch(small, task(output)).isRight)
    assertEquals(device.launch(large, task(output)), Left(Fault.BudgetExceeded(3L)))
  }

  private class Recording extends LaunchObserver {
    val instructions: java.util.concurrent.ConcurrentLinkedQueue[Fault.Location] =
      java.util.concurrent.ConcurrentLinkedQueue[Fault.Location]()
    val accesses: java.util.concurrent.ConcurrentLinkedQueue[LaunchObserver.Access] =
      java.util.concurrent.ConcurrentLinkedQueue[LaunchObserver.Access]()
    override def instruction(at: Fault.Location, state: ArchitecturalState): Either[Fault, Unit] =
      Right(instructions.add(at)).map(_ => ())
    override def memory(access: LaunchObserver.Access): Unit = { val _ = accesses.add(access) }
  }

  private object NoState extends ArchitecturalState

  /** Reports one instruction per image byte to an attached observer before echoing. */
  private class ObservedEcho(image: Vec[Byte]) extends EchoProgram(image) {
    override def execute(
        task: KernelTask,
        memory: AddressSpace,
        control: TaskControl,
        handler: Event => Either[Fault, Unit]
    ): Either[Fault, Unit] = control.observer.fold[Either[Fault, Unit]](Right(()))(observer =>
      image.indices.foldLeft[Either[Fault, Unit]](Right(()))((result, index) =>
        result.flatMap(_ =>
          observer.instruction(
            Fault.Location("echo", None, Some(index.toLong.unsafe), None, None),
            NoState
          )
        )
      )
    ).flatMap(_ => super.execute(task, memory, control, handler))
  }

  private class ObservedBackend extends Backend {
    override val format: ProgramFormat                                  = DeviceSuite.this.format
    override def load(image: Vec[Byte]): Either[Fault, Backend.Program] = Right(ObservedEcho(image))
  }

  test("an observer attached to a launch sees its instructions and device memory accesses") {
    val memory   = ArrayMemory()
    val output   = memory.allocate(1L)
    val device   = Device(BackendRegistry(Vec(ObservedBackend())), memory)
    val program  = device.load(format, Vec[Byte](5, 6)).value
    val observer = Recording()
    assert(device.launch(program, task(output), Some(observer)).isRight)
    assertEquals(
      Vec.from(observer.instructions.toArray(Array.empty[Fault.Location])).map(_.instruction),
      Vec(Some(0L), Some(1L)).map(_.map(_.unsafe[Long >= 0]))
    )
    assertEquals(
      Vec.from(observer.accesses.toArray(Array.empty[LaunchObserver.Access])),
      Vec(LaunchObserver.Access(LaunchObserver.AccessKind.Write, AddressSpace.Location.Within(output, 0L), 1))
    )
    assertEquals(memory.bytes(output), Vec[Byte](5))
  }

  test("an observer stops a launch by returning a fault") {
    val memory               = ArrayMemory()
    val output               = memory.allocate(1L)
    val device               = Device(BackendRegistry(Vec(ObservedBackend())), memory)
    val program              = device.load(format, Vec[Byte](5)).value
    val stop: LaunchObserver = (_, _) => Left(Fault.Cancelled)
    assertEquals(device.launch(program, task(output), Some(stop)), Left(Fault.Cancelled))
    assertEquals(memory.bytes(output), Vec[Byte](0))
  }

  test("a registry holds one backend per format") {
    val registry = BackendRegistry(Vec(EchoBackend(format), EchoBackend(ProgramFormat("other"))))
    assertEquals(registry.formats, Set(format, ProgramFormat("other")))
    intercept[IllegalArgumentException](BackendRegistry(Vec(EchoBackend(format), EchoBackend(format))))
  }
}
