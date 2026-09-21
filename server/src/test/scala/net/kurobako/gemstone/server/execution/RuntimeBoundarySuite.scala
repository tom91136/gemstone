package net.kurobako.gemstone.server.execution

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.isa.KernelMetadata
import net.kurobako.gemstone.isa.KernelName
import net.kurobako.gemstone.isa.LinkageName
import net.kurobako.gemstone.isa.KernelSymbol
import net.kurobako.gemstone.isa.ProgramSymbols
import net.kurobako.gemstone.vm.*

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

final class RuntimeBoundarySuite extends munit.FunSuite {
  private val Encoding = "test-binary"
  private val Name     = KernelName("main")

  private object EmptyMemory extends Memory {
    override def read(
        region: MemoryRegion,
        offset: Long :| Positive0,
        size: Int :| Positive0
    ): Either[Fault, Vec[Byte]] = Left(Fault.Memory("unexpected read"))

    override def write(
        region: MemoryRegion,
        offset: Long :| Positive0,
        bytes: Vec[Byte]
    ): Either[Fault, Unit] = Left(Fault.Memory("unexpected write"))
  }

  private final class FakeBackend extends RuntimeBackend {
    override val encoding: String = Encoding

    override def load(
        services: RuntimeBackend.Services,
        image: Vec[Byte]
    ): Either[RuntimeBackend.Error, RuntimeBackend.Program] =
      if (image.isEmpty) Left(RuntimeBackend.Error.Invalid("empty test image")) else Right(FakeProgram(services))
  }

  private final class FakeProgram(services: RuntimeBackend.Services) extends RuntimeBackend.Program {
    private val closed = AtomicBoolean()

    override val symbols: Either[RuntimeBackend.Error, ProgramSymbols] =
      Right(ProgramSymbols(Vec(KernelSymbol(Name, LinkageName("main"))), Vec.empty))

    override def kernel(name: KernelName): Either[RuntimeBackend.Error, KernelMetadata] =
      if (name == Name) KernelMetadata.validated(0, 0, Vec.empty).left.map(RuntimeBackend.Error.Invalid(_))
      else Left(RuntimeBackend.Error.NotFound(s"unknown kernel: ${name.value}"))

    override val globals: Option[RuntimeBackend.Program.Globals] = None

    override def execute(
        task: KernelTask,
        memory: Memory,
        control: TaskControl
    ): Either[RuntimeBackend.Error, Result] =
      if (closed.get()) Left(RuntimeBackend.Error.Failed("program is closed"))
      else
        for {
          _ <- control.checkpoint().left.map(RuntimeBackend.Error.Execution(_))
          _ <- services.observer
            .observe(control.observation, Event.Print("test", Vec.empty))
            .left.map(error => RuntimeBackend.Error.Execution(Fault.Observer(error.message)))
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
    assertEquals(backend.load(services, Vec.empty), Left(RuntimeBackend.Error.Invalid("empty test image")))
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
    assertEquals(program.execute(task, EmptyMemory, cancelled), Left(RuntimeBackend.Error.Execution(Fault.Cancelled)))

    val unobserved = TaskControl.forScheduler(observation = ObservationContext(3L))
    assert(program.execute(task, EmptyMemory, unobserved).left.exists(_.message.contains("no observer registered")))
  }
}
