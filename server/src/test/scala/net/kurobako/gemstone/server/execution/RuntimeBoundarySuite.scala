package net.kurobako.gemstone.server.execution

import net.kurobako.gemstone.Constraint.NonNegative

import net.kurobako.gemstone.rpc.ExecutionProtocol.ExecutionFailure
import net.kurobako.gemstone.*

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.isa.KernelMetadata
import net.kurobako.gemstone.Constraint.NonEmptyString
import net.kurobako.gemstone.Constraint.NonEmptyString
import net.kurobako.gemstone.isa.KernelSymbol
import net.kurobako.gemstone.isa.ProgramSymbols
import net.kurobako.gemstone.vm.*

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

final class RuntimeBoundarySuite extends munit.FunSuite {
  private val Encoding = "test-binary"
  private val Name     = NonEmptyString("main")

  private object EmptyMemory extends Memory {
    override def read(
        region: MemoryRegion,
        offset: NonNegative[Long],
        size: NonNegative[Int]
    ): Either[Fault, Vec[Byte]] = Left(Fault.Memory("unexpected read"))

    override def write(
        region: MemoryRegion,
        offset: NonNegative[Long],
        bytes: Vec[Byte]
    ): Either[Fault, Unit] = Left(Fault.Memory("unexpected write"))
  }

  private final class FakeBackend extends RuntimeBackend {
    override val encoding: String = Encoding

    override def load(
        services: RuntimeBackend.Services,
        image: Vec[Byte]
    ): Either[ExecutionFailure, RuntimeBackend.Program] =
      if (image.isEmpty) Left(ExecutionFailure.InvalidRequest("empty test image")) else Right(FakeProgram(services))
  }

  private final class FakeProgram(services: RuntimeBackend.Services) extends RuntimeBackend.Program {
    private val closed = AtomicBoolean()

    override val symbols: Either[ExecutionFailure, ProgramSymbols] =
      Right(ProgramSymbols(Vec(KernelSymbol(Name, NonEmptyString("main"))), Vec.empty))

    override def kernel(name: NonEmptyString): Either[ExecutionFailure, KernelMetadata] =
      if (name == Name) KernelMetadata.validated(
        0,
        0,
        Vec.empty
      ).left.map(ExecutionFailure.InvalidRequest(_))
      else Left(ExecutionFailure.NotFound(s"unknown kernel: ${name.raw}"))

    override val globals: Option[RuntimeBackend.Program.Globals] = None

    override def execute(
        task: KernelTask,
        memory: Memory,
        control: TaskControl
    ): Either[ExecutionFailure, Result] =
      if (closed.get()) Left(ExecutionFailure.Failed("program is closed"))
      else
        for {
          _ <- control.checkpoint().left.map(ExecutionFailure.fromFault(_))
          _ <- services.observer
            .observe(control.observation, Event.Print("test", Vec.empty))
            .left.map(error => ExecutionFailure.fromFault(Fault.Observer(error.message)))
        } yield Result(control.usedInstructions)

    override def close(): Unit = closed.set(true)
  }

  test("backend load routes to an isolated runtime program and correlated observer") {
    val observer = CompletionObserver()
    val token    = ObservationContext(1L)
    val observed = AtomicReference(Vec.empty[Event])
    assertEquals(observer.register(token) { event => observed.updateAndGet(_ :+ event); Right(()) }, Right(()))

    val program = FakeBackend()
      .load(RuntimeBackend.Services(observer, Scheduler.Sequential), Vec(1.toByte)).toOption.get
    val task = KernelTask.validated(
      Name,
      LaunchGeometry.Dimensions.One,
      LaunchGeometry.Dimensions.One,
      Vec.empty
    ).toOption.get
    val control = TaskControl.forScheduler(observation = token)

    assertEquals(program.execute(task, EmptyMemory, control), Right(Result(1L)))
    assertEquals(observed.get(), Vec(Event.Print("test", Vec.empty)))
    program.close()
    assert(program.execute(task, EmptyMemory, control).isLeft)
  }

  test("runtime boundary preserves decode, cancellation, and observer failures") {
    val observer = CompletionObserver()
    val backend  = FakeBackend()
    val services = RuntimeBackend.Services(observer, Scheduler.Sequential)
    assertEquals(backend.load(services, Vec.empty), Left(ExecutionFailure.InvalidRequest("empty test image")))
    val program = backend.load(services, Vec(1.toByte)).toOption.get
    val task    = KernelTask.validated(
      Name,
      LaunchGeometry.Dimensions.One,
      LaunchGeometry.Dimensions.One,
      Vec.empty
    ).toOption.get
    val cancellation = TaskControl.Cancellation.Source()
    cancellation.cancel()
    val cancelled = TaskControl.forScheduler(cancellation = cancellation, observation = ObservationContext(2L))
    assertEquals(program.execute(task, EmptyMemory, cancelled), Left(ExecutionFailure.fromFault(Fault.Cancelled)))

    val unobserved = TaskControl.forScheduler(observation = ObservationContext(3L))
    assert(program.execute(task, EmptyMemory, unobserved).left.exists(_.message.contains("no observer registered")))
  }
}
