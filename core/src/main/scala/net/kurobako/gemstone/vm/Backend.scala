package net.kurobako.gemstone.vm

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*

object Backend {

  /** A loaded program. Its owner closes it once, after the last call into it has returned. */
  trait Program extends AutoCloseable {

    /** Programs that expose program-scoped function pointers bind their identity before any launch observes them. */
    def bindFunctionPointerNamespace(@scala.annotation.unused id: ProgramId): Either[Fault, Unit] = Right(())
    def symbols: Either[Fault, ProgramSymbols]
    def kernel(name: NonEmpty[String]): Either[Fault, KernelMetadata]
    def global(@scala.annotation.unused name: NonEmpty[String]): Either[Fault, ProgramGlobal] =
      Left(Fault.Unsupported("program global metadata is unavailable"))
    def bindGlobal(
        @scala.annotation.unused name: NonEmpty[String],
        @scala.annotation.unused binding: GlobalBinding,
        @scala.annotation.unused memory: AddressSpace
    ): Either[Fault, Unit] = Left(Fault.Unsupported("program globals cannot be bound"))
    def writeGlobal(
        @scala.annotation.unused name: NonEmpty[String],
        @scala.annotation.unused offset: Long >= 0,
        @scala.annotation.unused bytes: Vec[Byte]
    ): Either[Fault, Unit] = Left(Fault.Unsupported("program globals are not writable"))
    def execute(
        task: KernelTask,
        memory: AddressSpace,
        control: TaskControl,
        handler: Event => Either[Fault, Unit]
    ): Either[Fault, Unit]
    override def close(): Unit = ()
  }

  /** Optional image transformation configured by the application, independent of program execution. */
  trait Specialization {
    def describe(image: Vec[Byte]): Either[Fault, Vec[SpecializationInfo]]
    def specialize(image: Vec[Byte], constants: Vec[SpecializationConstant]): Either[Fault, Vec[Byte]]
  }
}

trait Backend {
  def encoding: ProgramEncoding
  def load(image: Vec[Byte]): Either[Fault, Backend.Program]
}
