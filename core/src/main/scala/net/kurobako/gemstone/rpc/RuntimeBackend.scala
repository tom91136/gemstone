package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

object RuntimeBackend {

  abstract class Program extends AutoCloseable {
    private val closed = java.util.concurrent.atomic.AtomicBoolean()

    protected def open[A](operation: => Either[ExecutionFailure, A]): Either[ExecutionFailure, A] =
      if (closed.get()) Left(ExecutionFailure.Failed("program is closed")) else operation

    override def close(): Unit = closed.set(true)

    // Backends that expose program-scoped function pointers can bind their
    // service identity before the program becomes visible to launches.
    def bindFunctionPointerNamespace(@scala.annotation.unused id: Long): Either[ExecutionFailure, Unit] = Right(())
    def symbols: Either[ExecutionFailure, ProgramSymbols]
    def kernel(name: NonEmpty[String]): Either[ExecutionFailure, KernelMetadata]
    def global(@scala.annotation.unused name: NonEmpty[String]): Either[ExecutionFailure, GlobalImage] =
      open(Left(ExecutionFailure.Unsupported("program global metadata is unavailable")))
    def bindGlobal(
        @scala.annotation.unused name: NonEmpty[String],
        @scala.annotation.unused region: AllocationId,
        @scala.annotation.unused offset: Long >= 0,
        @scala.annotation.unused regionSize: Long > 0,
        @scala.annotation.unused memory: Memory
    ): Either[ExecutionFailure, Unit] = open(Left(ExecutionFailure.Unsupported("program globals cannot be bound")))
    def writeGlobal(
        @scala.annotation.unused name: NonEmpty[String],
        @scala.annotation.unused offset: Long >= 0,
        @scala.annotation.unused bytes: Vec[Byte]
    ): Either[ExecutionFailure, Unit] = open(Left(ExecutionFailure.Unsupported("program globals are not writable")))
    def execute(
        task: KernelTask,
        memory: Memory,
        control: TaskControl,
        handler: Event => Either[Fault, Unit] = _ => Right(())
    ): Either[ExecutionFailure, Unit]
  }

  /** Optional image transformation configured by the application, independent of program execution. */
  trait Specialization {
    def describe(image: Vec[Byte]): Either[ExecutionFailure, Vec[ExecutionProtocol.SpecializationInfo]]
    def specialize(
        image: Vec[Byte],
        constants: Vec[ExecutionProtocol.SpecializationConstant]
    ): Either[ExecutionFailure, Vec[Byte]]
  }

}

trait RuntimeBackend {
  def encoding: String
  def load(image: Vec[Byte]): Either[ExecutionFailure, RuntimeBackend.Program]
}
