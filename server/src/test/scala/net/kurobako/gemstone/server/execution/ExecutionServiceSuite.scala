package net.kurobako.gemstone.server.execution

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.isa.{KernelMetadata as ISAKernelMetadata, KernelName as ISAKernelName, ProgramSymbols}
import net.kurobako.gemstone.memory.AllocationKind
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import java.util.concurrent.atomic.AtomicBoolean
import scala.util.Using

final class ExecutionServiceSuite extends munit.FunSuite {
  private val encoding = ProgramEncoding.from("test-image").toOption.get

  private final class Backend(failLoad: Boolean = false) extends RuntimeBackend {
    val sessionClosed                      = AtomicBoolean()
    override val encoding: ProgramEncoding = ExecutionServiceSuite.this.encoding

    override def open(services: MachineServices): BackendSession = Session(services)

    private final class Session(services: MachineServices) extends BackendSession {
      override def load(image: Vec[Byte]): Either[RuntimeError, RuntimeProgram] =
        if (failLoad) Left(RuntimeError.Decode("bad image")) else Right(Program(services))
      override def close(): Unit = sessionClosed.set(true)
    }
  }

  private final class Program(services: MachineServices) extends RuntimeProgram {
    private val mutableClosed = AtomicBoolean()
    def closed: Boolean       = mutableClosed.get()

    override def symbols: Either[RuntimeError, ProgramSymbols] = Right(ProgramSymbols(Vec.empty, Vec.empty))
    override def kernel(name: ISAKernelName): Either[RuntimeError, ISAKernelMetadata] =
      ISAKernelMetadata.validated(
        8,
        1,
        Vec(
          ISAKernelMetadata.Parameter(8, 0, ISAKernelMetadata.ParameterKind.Memory, false)
        )
      ).left.map(RuntimeError.Load(_))
    override val globals: Option[RuntimeProgram.Globals] = None

    override def execute(
        task: KernelTask,
        memory: Memory,
        control: TaskControl
    ): Either[RuntimeError, Result] =
      task.arguments.headOption match {
        case Some(KernelTask.Argument.Memory(region, offset)) =>
          for {
            _ <- memory.write(region, offset, Vec[Byte](42)).left.map(RuntimeError.Execution(_))
            _ <- services.observer.observe(
              control.observation,
              Event.Print("done", Vec(Event.PrintArgument.Integer(Vec(42L), 32, true)))
            ).left.map(error => RuntimeError.Execution(Fault.Observer(error.message)))
          } yield Result(control.usedInstructions)
        case _ => Left(RuntimeError.Execution(Fault.InvalidTask("memory argument is required")))
      }

    override def close(): Unit = mutableClosed.set(true)
  }

  test("service translates API ownership into a VM task and records VM events") {
    val backend = Backend()
    Using.resource(ExecutionService(BackendCatalog.from(backend), _.run())) { service =>
      val allocation = service.registerAllocation(8, AllocationKind.Shared).toOption.get
      val program    = service.load(encoding, Vec[Byte](1)).toOption.get
      val task       = KernelTask.validated(
        ISAKernelName("write"),
        LaunchGeometry.Dimensions.One,
        LaunchGeometry.Dimensions.One,
        Vec(KernelTask.Argument.Memory(MemoryRegion(allocation.value), 2))
      ).toOption.get
      val completion = service.launch(program, task).toOption.get

      assertEquals(service.completion(completion), Right(CompletionStatus.Succeeded))
      assertEquals(service.read(allocation, 2, 1), Right(Vec[Byte](42)))
      assertEquals(service.completionPrints(completion).map(_.map(_.format)), Right(Vec("done")))
      assert(service.completionDirtyRanges(completion).toOption.get.exists(_.allocation == allocation))
    }
    assert(backend.sessionClosed.get())
  }

  test("failed loads still leave their backend session under service ownership") {
    val backend = Backend(failLoad = true)
    val service = ExecutionService(BackendCatalog.from(backend), _.run())
    assert(service.load(encoding, Vec.empty).isLeft)
    service.close()
    assert(backend.sessionClosed.get())
  }

  test("unknown encodings fail without opening a backend session") {
    val backend = Backend()
    Using.resource(ExecutionService(BackendCatalog.from(backend), _.run())) { service =>
      val unknown = ProgramEncoding.from("unknown-image").toOption.get
      assert(service.load(unknown, Vec.empty).left.exists {
        case ExecutionFailure.Unsupported(_) => true
        case _                               => false
      })
    }
    assert(!backend.sessionClosed.get())
  }
}
