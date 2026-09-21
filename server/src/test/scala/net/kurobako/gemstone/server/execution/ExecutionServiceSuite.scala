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
  private val encoding = "test-image"

  private final class Backend extends RuntimeBackend {
    val programClosed             = AtomicBoolean()
    override val encoding: String = ExecutionServiceSuite.this.encoding

    override def load(
        services: RuntimeBackend.Services,
        image: Vec[Byte]
    ): Either[RuntimeBackend.Error, RuntimeBackend.Program] = Right(Program(services, programClosed))
  }

  private final class Program(services: RuntimeBackend.Services, closed: AtomicBoolean) extends RuntimeBackend.Program {
    override def symbols: Either[RuntimeBackend.Error, ProgramSymbols] = Right(ProgramSymbols(Vec.empty, Vec.empty))
    override def kernel(name: ISAKernelName): Either[RuntimeBackend.Error, ISAKernelMetadata] =
      ISAKernelMetadata.validated(
        8,
        1,
        Vec(
          ISAKernelMetadata.Parameter(8, 0, ISAKernelMetadata.ParameterKind.Memory, false)
        )
      ).left.map(RuntimeBackend.Error.Invalid(_))
    override val globals: Option[RuntimeBackend.Program.Globals] = None

    override def execute(
        task: KernelTask,
        memory: Memory,
        control: TaskControl
    ): Either[RuntimeBackend.Error, Result] =
      task.arguments.headOption match {
        case Some(KernelTask.Argument.Memory(region, offset)) =>
          for {
            _ <- memory.write(region, offset, Vec[Byte](42)).left.map(RuntimeBackend.Error.Execution(_))
            _ <- services.observer.observe(
              control.observation,
              Event.Print("done", Vec(Event.PrintArgument.Integer(Vec(42L), 32, true)))
            ).left.map(error => RuntimeBackend.Error.Execution(Fault.Observer(error.message)))
          } yield Result(control.usedInstructions)
        case _ => Left(RuntimeBackend.Error.Execution(Fault.InvalidTask("memory argument is required")))
      }

    override def close(): Unit = closed.set(true)
  }

  test("service translates API ownership into a VM task and records VM events") {
    val backend = Backend()
    Using.resource(ExecutionService(List(backend))) { service =>
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
    assert(backend.programClosed.get())
  }

  test("unknown encodings fail without invoking a backend") {
    val backend = Backend()
    Using.resource(ExecutionService(List(backend))) { service =>
      val unknown = "unknown-image"
      assert(service.load(unknown, Vec.empty).left.exists {
        case ExecutionFailure.Unsupported(_) => true
        case _                               => false
      })
    }
    assert(!backend.programClosed.get())
  }
}
