package net.kurobako.gemstone.server.backend

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantLock
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.concurrent.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.ptx.v9_3.{ISA as PTXISA, Program as PTXProgram, *}
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

object PTX {
  val encoding: String = "ptx-text"
}

class PTX(workgroups: Workgroups = Workgroups.Sequential) extends RuntimeBackend {
  override val encoding: String = PTX.encoding

  private case class Cached(program: PTXProgram, references: Int > 0)

  // Share immutable parsed syntax only while loaded programs reference it; runtime state remains per load.
  private val cacheLock = ReentrantLock()
  private val decoded   = AtomicReference(Map.empty[Vec[Byte], Cached])

  private def release(image: Vec[Byte]): Unit = cacheLock.withLock {
    val cache = decoded.get()
    cache.get(image).foreach { cached =>
      if (cached.references.raw == 1) decoded.set(cache.removed(image))
      else decoded.set(cache.updated(
        image,
        cached.copy(references = (cached.references.raw - 1).unsafe[Int > 0])
      ))
    }
  }

  override def load(
      image: Vec[Byte]
  ): Either[ExecutionFailure, RuntimeBackend.Program] = {
    val program = cacheLock.withLock {
      val cache = decoded.get()
      cache.get(image) match {
        case Some(cached) =>
          decoded.set(cache.updated(
            image,
            cached.copy(references = (cached.references.raw + 1).unsafe[Int > 0])
          ))
          Right(cached.program)
        case None =>
          PTXISA.decode(image)
            .left.map(error => ExecutionFailure.InvalidRequest(error.message))
            .map { parsed =>
              decoded.set(cache.updated(image, Cached(parsed, 1)))
              parsed
            }
      }
    }
    program.flatMap { parsed =>
      LoadedProgram.load(parsed)
        .left.map(error => ExecutionFailure.InvalidRequest(error.message))
        .map(loaded => Program(loaded, image))
        .left.map { error =>
          release(image)
          error
        }
    }
  }

  private class Program(loaded: LoadedProgram, image: Vec[Byte])
      extends RuntimeBackend.Program {
    private val closed = AtomicBoolean(false)
    override def execute(
        task: KernelTask,
        memory: Memory,
        control: TaskControl,
        handler: Event => Either[Fault, Unit]
    ): Either[ExecutionFailure, Unit] = open {
      memory match {
        case addresses: AddressSpace =>
          loaded.execute(task, addresses, control, workgroups).left.map(ExecutionFailure.fromFault(_))
        case _ => Left(ExecutionFailure.Unsupported("PTX execution requires address-space capable memory"))
      }
    }

    override def symbols: Either[ExecutionFailure, ProgramSymbols] =
      open(loaded.symbols.left.map(error => ExecutionFailure.InvalidRequest(error.message)))

    override def kernel(name: NonEmpty[String]): Either[ExecutionFailure, KernelMetadata] =
      open(loaded.kernel(name).left.map(error => ExecutionFailure.InvalidRequest(error.message)))

    override def global(name: NonEmpty[String]): Either[ExecutionFailure, GlobalImage] = open {
      loaded.global(name).left.map(error => ExecutionFailure.InvalidRequest(error.message))
    }

    override def bindGlobal(
        name: NonEmpty[String],
        region: AllocationId,
        offset: Long >= 0,
        regionSize: Long > 0,
        memory: Memory
    ): Either[ExecutionFailure, Unit] = open {
      memory match {
        case addresses: AddressSpace =>
          loaded.global(name).left.map(_ =>
            ExecutionFailure.NotFound(s"PTX global '$name' is not present")
          )
            .flatMap(_ =>
              loaded.bindGlobal(name, region, offset, regionSize, addresses)
                .left.map(ExecutionFailure.fromFault(_))
            )
        case _ => Left(ExecutionFailure.Unsupported("PTX global binding requires address-space capable memory"))
      }
    }

    override def writeGlobal(
        name: NonEmpty[String],
        offset: Long >= 0,
        bytes: Vec[Byte]
    ): Either[ExecutionFailure, Unit] = open {
      Left(ExecutionFailure.Unsupported("PTX globals are not writable"))
    }

    override def close(): Unit = if (closed.compareAndSet(false, true)) {
      super.close()
      release(image)
    }
  }
}
