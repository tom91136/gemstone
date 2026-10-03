package net.kurobako.gemstone.server

import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.atomic.AtomicInteger
import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.server.backend.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.AddressSpace.*
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
    private class ContendedMemory(delegate: Memory, addresses: AddressSpace, launch: Int, counter: Long)
        extends AddressSpace {
      override def hasNativePointers(region: AllocationId): Boolean = delegate.hasNativePointers(region)
      override def fenceHostMemory(): Either[Fault, Unit]           = delegate.fenceHostMemory()
      override def flushHostWrites(): Either[Fault, Unit]           = delegate.flushHostWrites()
      override def invalidateHostReads(): Unit                      = delegate.invalidateHostReads()
      override def defersHostReads                                  = delegate.defersHostReads
      override def flushHostReads(): Either[Fault, Unit]            = delegate.flushHostReads()
      override def prepareReads(ranges: Vec[(GuestAddress, Int >= 0)]): Either[Fault, Unit] =
        addresses.prepareReads(ranges)
      override def prepareSystemReads(ranges: Vec[(GuestAddress, Int >= 0)]): Either[Fault, Unit] =
        addresses.prepareSystemReads(ranges)
      override def address(region: AllocationId, offset: Long >= 0): Either[Fault, GuestAddress] =
        addresses.address(region, offset)
      override def locate(address: GuestAddress, size: Int >= 0): Either[Fault, Location] =
        addresses.locate(address, size)
      override def read(region: AllocationId, offset: Long >= 0, size: Int >= 0): Either[Fault, Vec[Byte]] =
        delegate.read(region, offset, size)
      override def write(region: AllocationId, offset: Long >= 0, bytes: Vec[Byte]): Either[Fault, Unit] =
        delegate.write(region, offset, bytes)
      override def readValue(
          region: AllocationId,
          offset: Long >= 0,
          size: Int in 1 ~ 8
      ): Either[Fault, Long] =
        delegate.readValue(region, offset, size)
      override def writeValue(
          region: AllocationId,
          offset: Long >= 0,
          value: Long,
          size: Int in 1 ~ 8
      ): Either[Fault, Unit] =
        delegate.writeValue(region, offset, value, size)
      override def read(address: GuestAddress, size: Int >= 0): Either[Fault, Vec[Byte]] =
        addresses.read(address, size)
      override def write(address: GuestAddress, bytes: Vec[Byte]): Either[Fault, Unit] =
        addresses.write(address, bytes)
      override def readValue(address: GuestAddress, size: Int in 1 ~ 8): Either[Fault, Long] =
        addresses.readValue(address, size)
      override def readSystemValue(address: GuestAddress, size: Int in 1 ~ 8): Either[Fault, Long] =
        addresses.readSystemValue(address, size)
      override def writeValue(address: GuestAddress, value: Long, size: Int in 1 ~ 8): Either[Fault, Unit] =
        addresses.writeValue(address, value, size)
      override def writeSystemValue(
          address: GuestAddress,
          value: Long,
          size: Int in 1 ~ 8
      ): Either[Fault, Unit] = addresses.writeSystemValue(address, value, size)
      override def writeSystemBatch(
          writes: Vec[(GuestAddress, Long, Int in 1 ~ 8)]
      ): Either[Fault, Unit] = addresses.writeSystemBatch(writes)
      override def atomicModifyValue(
          region: AllocationId,
          offset: Long >= 0,
          size: Int in 1 ~ 8
      )(update: Long => Long): Either[Fault, Long] =
        addresses.address(region, offset).flatMap(address =>
          contend(address, update)(next => delegate.atomicModifyValue(region, offset, size)(next))
        )
      override def atomicModifyValue(
          address: GuestAddress,
          size: Int in 1 ~ 8
      )(update: Long => Long): Either[Fault, Long] =
        contend(address, update)(next => addresses.atomicModifyValue(address, size)(next))

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
            // Assign sequence numbers inside the service's serialized CAS callback;
            // workgroups may enter atomicModifyValue in a different order.
            if (attempt == 0) operation.set(contendedOperations.getAndIncrement())
            val _ = contendedAttempts.incrementAndGet()
            if (attempt < 2) checkpoint(launch, s"counter-${operation.get()}-$attempt")
            desired
          }.flatMap { observed =>
            if (attempts.get() == 3) Right(observed)
            else Left(Fault.InvalidTask(s"expected two failed native CAS attempts, saw ${attempts.get()} attempts"))
          }
        }
    }

    private class Program(program: RuntimeBackend.Program) extends RuntimeBackend.Program {
      override def bindFunctionPointerNamespace(id: Long): Either[ExecutionFailure, Unit] =
        program.bindFunctionPointerNamespace(id)
      override def symbols                                                                  = program.symbols
      override def kernel(name: NonEmpty[String]): Either[ExecutionFailure, KernelMetadata] = program.kernel(name)
      override def global(name: NonEmpty[String]): Either[ExecutionFailure, GlobalImage]    = program.global(name)
      override def bindGlobal(
          name: NonEmpty[String],
          region: AllocationId,
          offset: Long >= 0,
          regionSize: Long > 0,
          memory: Memory
      ): Either[ExecutionFailure, Unit] = program.bindGlobal(name, region, offset, regionSize, memory)
      override def writeGlobal(
          name: NonEmpty[String],
          offset: Long >= 0,
          bytes: Vec[Byte]
      ): Either[ExecutionFailure, Unit] =
        program.writeGlobal(name, offset, bytes)
      override def close(): Unit = program.close()
      override def execute(
          task: KernelTask,
          memory: Memory,
          control: TaskControl,
          handler: Event => Either[Fault, Unit]
      ): Either[ExecutionFailure, Unit] = {
        val launch = launches.getAndIncrement()
        checkpoint(launch, "before")
        val checked = if (forceContention) memory match {
          case addresses: AddressSpace =>
            ContendedMemory(
              memory,
              addresses,
              launch,
              Files.readString(directory.resolve(s"counter-$launch")).trim.toLong
            )
          case _ => throw IllegalStateException("contention probe needs an address space")
        }
        else memory
        val result = program.execute(task, checked, control, handler).flatMap { result =>
          if (forceContention && contendedOperations.get() != expectedOperations)
            Left(ExecutionFailure.InvalidRequest(
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

    private class Backend(original: RuntimeBackend) extends RuntimeBackend {
      override def encoding: String                                                         = original.encoding
      override def load(image: Vec[Byte]): Either[ExecutionFailure, RuntimeBackend.Program] =
        original.load(image).map(Program(_))
    }

    def backends(workgroups: Workgroups): List[RuntimeBackend] =
      Backends.default(workgroups).programs.map(Backend(_))

    override def close(): Unit = temporary.close()
  }

}
