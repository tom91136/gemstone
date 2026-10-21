package net.kurobako.gemstone.server

import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.atomic.AtomicInteger
import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.server.backend.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*
import scala.concurrent.duration.*

/** Only tests wrap execution. Native clients rendezvous before execution and after execution, before
  * completion/writeback.
  */
private object checkpoints {
  class Checkpoint(forceContention: Boolean = false, expectedOperations: Int = 0)
      extends AutoCloseable {
    private val temporary                  = files.Directory("gemstone-memory-checkpoint-")
    val directory: Path                    = temporary.path
    private val launches                   = AtomicInteger()
    def count: Int                         = launches.get()
    val contendedOperations: AtomicInteger = AtomicInteger()
    val contendedAttempts: AtomicInteger   = AtomicInteger()

    private def checkpoint(launch: Int, phase: String): Unit = {
      val suffix   = s"$launch-$phase"
      val _        = Files.createFile(directory.resolve(s"device-$suffix"))
      val deadline = 30.seconds.fromNow
      while (!Files.exists(directory.resolve(s"host-$suffix"))) {
        if (deadline.isOverdue()) throw IllegalStateException(s"host checkpoint timed out: $suffix")
        Thread.sleep(1)
      }
    }

    /** Intercept only the pure update callback, after computing the desired value and before the native CAS. The CPU
      * changes the same word at the first two attempts. All address resolution and memory operations still use the
      * production service.
      */
    private class ContendedMemory(delegate: AddressSpace, launch: Int, counter: Long)
        extends ForwardingMemory(delegate) {
      override def readValue(region: AllocationId, offset: Long >= 0, size: Int in 1 ~ 8): Either[Fault, Long] =
        delegate.readValue(region, offset, size)
      override def writeValue(
          region: AllocationId,
          offset: Long >= 0,
          value: Long,
          size: Int in 1 ~ 8
      ): Either[Fault, Unit] =
        delegate.writeValue(region, offset, value, size)
      override def read(address: GuestAddress, size: Int >= 0): Either[Fault, Vec[Byte]] =
        delegate.read(address, size)
      override def write(address: GuestAddress, bytes: Vec[Byte]): Either[Fault, Unit] =
        delegate.write(address, bytes)
      override def readValue(address: GuestAddress, size: Int in 1 ~ 8): Either[Fault, Long] =
        delegate.readValue(address, size)
      override def readSystemValue(address: GuestAddress, size: Int in 1 ~ 8): Either[Fault, Long] =
        delegate.readSystemValue(address, size)
      override def writeValue(address: GuestAddress, value: Long, size: Int in 1 ~ 8): Either[Fault, Unit] =
        delegate.writeValue(address, value, size)
      override def writeSystemValue(
          address: GuestAddress,
          value: Long,
          size: Int in 1 ~ 8
      ): Either[Fault, Unit] = delegate.writeSystemValue(address, value, size)
      override def writeSystemBatch(
          writes: Vec[(GuestAddress, Long, Int in 1 ~ 8)]
      ): Either[Fault, Unit] = delegate.writeSystemBatch(writes)
      override def atomicModifyValue(
          region: AllocationId,
          offset: Long >= 0,
          size: Int in 1 ~ 8
      )(update: Long => Long): Either[Fault, Long] =
        delegate.address(region, offset).flatMap(address =>
          contend(address, update)(next => delegate.atomicModifyValue(region, offset, size)(next))
        )
      override def atomicModifyValue(
          address: GuestAddress,
          size: Int in 1 ~ 8
      )(update: Long => Long): Either[Fault, Long] =
        contend(address, update)(next => delegate.atomicModifyValue(address, size)(next))

      private def contend(
          address: GuestAddress,
          update: Long => Long
      )(run: (Long => Long) => Either[Fault, Long]): Either[Fault, Long] =
        if (address.raw != counter) run(update)
        else {
          val operation = AtomicInteger(-1)
          val attempts  = AtomicInteger()
          run { expected =>
            val desired = update(expected)
            val attempt = attempts.getAndIncrement()
            // Assign sequence numbers inside the service's serialised CAS callback;
            // workgroups may enter atomicModifyValue in a different order.
            if (attempt == 0) operation.set(contendedOperations.getAndIncrement())
            val _ = contendedAttempts.incrementAndGet()
            if (attempt < 2) checkpoint(launch, s"counter-${operation.get()}-$attempt")
            desired
          }.flatMap { observed =>
            if (attempts.get() == 3) Right(observed)
            else Left(Fault.InvalidRequest(s"expected two failed native CAS attempts, saw ${attempts.get()} attempts"))
          }
        }
    }

    private class Program(program: Backend.Program) extends Backend.Program {
      override def bindFunctionPointerNamespace(id: ProgramId): Either[Fault, Unit] =
        program.bindFunctionPointerNamespace(id)
      override def symbols                                                       = program.symbols
      override def kernel(name: NonEmpty[String]): Either[Fault, KernelMetadata] = program.kernel(name)
      override def global(name: NonEmpty[String]): Either[Fault, ProgramGlobal]  = program.global(name)
      override def bindGlobal(
          name: NonEmpty[String],
          binding: GlobalBinding,
          memory: AddressSpace
      ): Either[Fault, Unit] =
        program.bindGlobal(name, binding, memory)
      override def writeGlobal(name: NonEmpty[String], offset: Long >= 0, bytes: Vec[Byte]): Either[Fault, Unit] =
        program.writeGlobal(name, offset, bytes)
      override def close(): Unit                                                        = program.close()
      override def execute(task: KernelTask, context: TaskContext): Either[Fault, Unit] = {
        val launch = launches.getAndIncrement()
        checkpoint(launch, "before")
        val checked =
          if (forceContention)
            ContendedMemory(context.memory, launch, Files.readString(directory.resolve(s"counter-$launch")).trim.toLong)
          else context.memory
        val result = program.execute(task, context.withMemory(checked)).flatMap { result =>
          if (forceContention && contendedOperations.get() != expectedOperations)
            Left(Fault.InvalidRequest(
              s"expected $expectedOperations atomic operations, saw ${contendedOperations.get()}"
            ))
          else Right(result)
        }
        result match {
          case Left(error) => val _ = Files.writeString(directory.resolve(s"failure-$launch"), error.toString)
          case Right(_)    => checkpoint(launch, "after")
        }
        result
      }
    }

    private class CheckpointBackend(original: Backend) extends Backend {
      override def format: ProgramFormat                                  = original.format
      override def load(image: Vec[Byte]): Either[Fault, Backend.Program] = original.load(image).map(Program(_))
    }

    def backends(workgroups: Workgroups): Backends = {
      val defaults = Backends.default(workgroups)
      defaults.copy(programs = defaults.programs.map(CheckpointBackend(_)))
    }

    override def close(): Unit = temporary.close()
  }

}
