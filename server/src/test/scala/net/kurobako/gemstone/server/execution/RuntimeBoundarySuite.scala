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
  private val Encoding = ProgramEncoding.from("test-binary").toOption.get
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
    override val encoding: ProgramEncoding                       = Encoding
    override def open(services: MachineServices): BackendSession = FakeSession(services)
  }

  private final class FakeSession(services: MachineServices) extends BackendSession {
    private val closed = AtomicBoolean()

    override def load(image: Vec[Byte]): Either[RuntimeError, RuntimeProgram] =
      if (closed.get()) Left(RuntimeError.Lifecycle("backend session is closed"))
      else if (image.isEmpty) Left(RuntimeError.Decode("empty test image"))
      else Right(FakeProgram(services))

    override def close(): Unit = closed.set(true)
  }

  private final class FakeProgram(services: MachineServices) extends RuntimeProgram {
    private val closed = AtomicBoolean()

    override val symbols: Either[RuntimeError, ProgramSymbols] =
      Right(ProgramSymbols(Vec(KernelSymbol(Name, LinkageName("main"))), Vec.empty))

    override def kernel(name: KernelName): Either[RuntimeError, KernelMetadata] =
      if (name == Name) KernelMetadata.validated(0, 0, Vec.empty).left.map(RuntimeError.Load(_))
      else Left(RuntimeError.NotFound(s"unknown kernel: ${name.value}"))

    override val globals: Option[RuntimeProgram.Globals] = None

    override def execute(
        task: KernelTask,
        memory: Memory,
        control: TaskControl
    ): Either[RuntimeError, Result] =
      if (closed.get()) Left(RuntimeError.Lifecycle("program is closed"))
      else
        for {
          _ <- control.checkpoint().left.map(RuntimeError.Execution(_))
          _ <- services.observer
            .observe(control.observation, Event.Print("test", Vec.empty))
            .left.map(error => RuntimeError.Execution(Fault.Observer(error.message)))
        } yield Result(control.usedInstructions)

    override def close(): Unit = closed.set(true)
  }

  test("open encoding routes to an isolated runtime program and correlated observer") {
    val observer = CompletionObserver()
    val token    = TaskToken(1L)
    val observed = AtomicReference(Vec.empty[Event])
    assertEquals(observer.register(token) { event => observed.updateAndGet(_ :+ event); Right(()) }, Right(()))

    val catalog = BackendCatalog.from(FakeBackend())
    val session = catalog.backend(Encoding).map(_.open(MachineServices(observer, Scheduler.Sequential))).toOption.get
    val program = session.load(Vec(1.toByte)).toOption.get
    val task    = KernelTask.validated(
      Name,
      LaunchGeometry.Dimensions.One,
      LaunchGeometry.Dimensions.One,
      Vec.empty
    ).toOption.get
    val control = TaskControl.forScheduler(observation = token.observation)

    assertEquals(program.execute(task, EmptyMemory, control), Right(Result(1L)))
    assertEquals(observed.get(), Vec(Event.Print("test", Vec.empty)))
    program.close()
    assert(program.execute(task, EmptyMemory, control).isLeft)
    session.close()
    assert(session.load(Vec(1.toByte)).isLeft)
  }

  test("runtime boundary preserves unknown encoding, decode, cancellation, and observer failures") {
    val observer = CompletionObserver()
    val catalog  = BackendCatalog.from(FakeBackend())
    val missing  = ProgramEncoding.from("missing").toOption.get
    assertEquals(catalog.backend(missing), Left(RuntimeError.UnknownEncoding(missing)))

    val session = catalog.backend(Encoding).map(_.open(MachineServices(observer, Scheduler.Sequential))).toOption.get
    assertEquals(session.load(Vec.empty), Left(RuntimeError.Decode("empty test image")))
    val program = session.load(Vec(1.toByte)).toOption.get
    val task    = KernelTask.validated(
      Name,
      LaunchGeometry.Dimensions.One,
      LaunchGeometry.Dimensions.One,
      Vec.empty
    ).toOption.get
    val cancellation = TaskControl.Cancellation.Source()
    cancellation.cancel()
    val cancelled = TaskControl.forScheduler(cancellation = cancellation, observation = TaskToken(2L).observation)
    assertEquals(program.execute(task, EmptyMemory, cancelled), Left(RuntimeError.Execution(Fault.Cancelled)))

    val unobserved = TaskControl.forScheduler(observation = TaskToken(3L).observation)
    assert(program.execute(task, EmptyMemory, unobserved).left.exists(_.message.contains("no observer registered")))
  }
}
