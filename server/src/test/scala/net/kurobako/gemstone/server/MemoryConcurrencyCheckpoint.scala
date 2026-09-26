package net.kurobako.gemstone.server

import net.kurobako.gemstone.*

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.isa.KernelName
import net.kurobako.gemstone.memory.{GuestAddress, MemoryValueByteWidth}
import net.kurobako.gemstone.server.execution.RuntimeBackend
import net.kurobako.gemstone.vm.{AddressSpace, Fault, KernelTask, Memory, MemoryRegion, TaskControl}

import java.nio.file.{Files, Path}
import java.util.concurrent.atomic.AtomicInteger
import scala.concurrent.duration.*
import scala.util.Using

/** Only tests wrap execution. Native clients rendezvous before execution and after execution, before
  * completion/writeback.
  */
private final class MemoryConcurrencyCheckpoint(forceContention: Boolean = false, expectedOperations: Int = 0)
    extends AutoCloseable {
  val directory: Path     = Files.createTempDirectory("gemstone-memory-checkpoint-")
  private val launches    = AtomicInteger()
  def count: Int          = launches.get()
  val contendedOperations = AtomicInteger()
  val contendedAttempts   = AtomicInteger()

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
  private final class ContendedMemory(delegate: Memory, addresses: AddressSpace, launch: Int, counter: Long)
      extends Memory with AddressSpace {
    override def hasNativePointers(region: MemoryRegion): Boolean          = delegate.hasNativePointers(region)
    override def fenceHostMemory()                                         = delegate.fenceHostMemory()
    override def flushHostWrites()                                         = delegate.flushHostWrites()
    override def invalidateHostReads(): Unit                               = delegate.invalidateHostReads()
    override def defersHostReads                                           = delegate.defersHostReads
    override def flushHostReads()                                          = delegate.flushHostReads()
    override def prepareReads(ranges: Vec[(GuestAddress, NonNegativeInt)]) = addresses.prepareReads(ranges)
    override def address(region: MemoryRegion, offset: NonNegativeLong)    = addresses.address(region, offset)
    override def locate(address: GuestAddress, size: NonNegativeInt)       = addresses.locate(address, size)
    override def read(region: MemoryRegion, offset: NonNegativeLong, size: NonNegativeInt) =
      delegate.read(region, offset, size)
    override def write(region: MemoryRegion, offset: NonNegativeLong, bytes: Vec[Byte]) =
      delegate.write(region, offset, bytes)
    override def readValue(region: MemoryRegion, offset: NonNegativeLong, size: MemoryValueByteWidth) =
      delegate.readValue(region, offset, size)
    override def writeValue(region: MemoryRegion, offset: NonNegativeLong, value: Long, size: MemoryValueByteWidth) =
      delegate.writeValue(region, offset, value, size)
    override def read(memory: Memory, address: GuestAddress, size: NonNegativeInt) =
      addresses.read(delegate, address, size)
    override def write(memory: Memory, address: GuestAddress, bytes: Vec[Byte]) =
      addresses.write(delegate, address, bytes)
    override def readValue(memory: Memory, address: GuestAddress, size: MemoryValueByteWidth) =
      addresses.readValue(delegate, address, size)
    override def writeValue(memory: Memory, address: GuestAddress, value: Long, size: MemoryValueByteWidth) =
      addresses.writeValue(delegate, address, value, size)
    override def atomicModifyValue(
        region: MemoryRegion,
        offset: NonNegativeLong,
        size: MemoryValueByteWidth
    )(update: Long => Long) =
      addresses.address(region, offset).flatMap(address =>
        contend(address, update)(next => delegate.atomicModifyValue(region, offset, size)(next))
      )
    override def atomicModifyValue(
        memory: Memory,
        address: GuestAddress,
        size: MemoryValueByteWidth
    )(update: Long => Long) =
      contend(address, update)(next => addresses.atomicModifyValue(delegate, address, size)(next))

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

  private final class Program(program: RuntimeBackend.Program) extends RuntimeBackend.Program {
    override def bindProgramId(id: Long): Unit                                   = program.bindProgramId(id)
    override def symbols                                                         = program.symbols
    override def kernel(name: KernelName)                                        = program.kernel(name)
    override def globals                                                         = program.globals
    override def close(): Unit                                                   = program.close()
    override def execute(task: KernelTask, memory: Memory, control: TaskControl) = {
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
      val result = program.execute(task, checked, control).flatMap { result =>
        if (forceContention && contendedOperations.get() != expectedOperations)
          Left(RuntimeBackend.Error.Invalid(
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

  private final class Backend(original: RuntimeBackend) extends RuntimeBackend {
    override def encoding: String                                          = original.encoding
    override def load(services: RuntimeBackend.Services, image: Vec[Byte]) =
      original.load(services, image).map(Program(_))
  }

  val backends: List[RuntimeBackend] = RuntimeBackend.defaults.map(Backend(_))

  override def close(): Unit = {
    Using.resource(Files.list(directory))(_.forEach { path =>
      val _ = Files.deleteIfExists(path)
    })
    val _ = Files.deleteIfExists(directory)
  }
}
