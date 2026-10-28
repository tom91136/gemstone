package net.kurobako.gemstone.vm

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import scala.annotation.unused

object Backend {

  /** A loaded program. Its owner closes it once, after the last call into it has returned. */
  trait Program extends AutoCloseable {

    /** Programs that expose program-scoped function pointers bind their identity before any launch observes them. */
    def bindFunctionPointerNamespace(@unused id: ProgramId): Either[Fault, Unit] = Right(())
    def symbols: Either[Fault, ProgramSymbols]
    def kernel(name: NonEmpty[String]): Either[Fault, KernelMetadata]
    def global(@unused name: NonEmpty[String]): Either[Fault, ProgramGlobal] =
      Left(Fault.Unsupported("program global metadata is unavailable"))
    def bindGlobal(
        @unused name: NonEmpty[String],
        @unused binding: GlobalBinding,
        @unused memory: AddressSpace
    ): Either[Fault, Unit] = Left(Fault.Unsupported("program globals cannot be bound"))
    def writeGlobal(
        @unused name: NonEmpty[String],
        @unused offset: Long >= 0,
        @unused bytes: Vec[Byte]
    ): Either[Fault, Unit] = Left(Fault.Unsupported("program globals are not writable"))

    /** Compiles a kernel ahead of its launches, so that an invalid program is reported here and not by `execute`. */
    def build(@unused name: NonEmpty[String]): Either[Fault, Unit] = Right(())
    def execute(task: KernelTask, context: TaskContext): Either[Fault, Unit]
    override def close(): Unit = ()
  }

  /** Optional image transformation configured by the application, independent of program execution. */
  trait Specialization {
    def describe(image: Vec[Byte]): Either[Fault, Vec[SpecializationInfo]]
    def specialize(image: Vec[Byte], constants: Vec[SpecializationConstant]): Either[Fault, Vec[Byte]]
  }
}

trait Backend {
  def format: ProgramFormat
  def load(image: Vec[Byte]): Either[Fault, Backend.Program]
}
