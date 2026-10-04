package net.kurobako.gemstone.server.backend

import java.util.concurrent.ExecutionException
import java.util.concurrent.FutureTask
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantLock
import java.util.zip.CRC32
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.concurrent.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.ptx.v9_3.{ISA as PTXISA, Program as PTXProgram, *}
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*
import scala.collection.immutable.ArraySeq
import scala.collection.mutable

object PTX {
  val encoding: String = "ptx-text"
}

class PTX(workgroups: Workgroups = Workgroups.Sequential) extends RuntimeBackend {
  override val encoding: String = PTX.encoding

  protected def decode(image: Vec[Byte]): Either[PTXISA.DecodeError, PTXProgram] = PTXISA.decode(image)

  private case class Image(bytes: Vec[Byte]) {
    // Hash outside the cache lock and retain it for release; modules can be many megabytes.
    private val hash = {
      val checksum = CRC32()
      bytes match {
        case image: ArraySeq.ofByte => checksum.update(image.unsafeArray)
        case _                      => checksum.update(Vec.toArray(bytes))
      }
      checksum.getValue.toInt
    }
    override def hashCode(): Int = hash
  }

  private class Cached(val image: Image) {
    val program: FutureTask[Either[PTXISA.DecodeError, PTXProgram]] = FutureTask(() => decode(image.bytes))
    // Includes callers still waiting for the parse, so a completed load cannot evict their entry.
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    var references: Int >= 0 = 0
  }

  // Share immutable parsed syntax only while loaded programs reference it; runtime state remains per load.
  private val cacheLock = ReentrantLock()
  private val decoded   = mutable.HashMap.empty[Image, Cached]

  private def release(cached: Cached): Unit = cacheLock.withLock {
    cached.references -= 1
    if (cached.references.raw == 0) {
      val _ = decoded.remove(cached.image)
    }
  }

  override def load(
      image: Vec[Byte]
  ): Either[ExecutionFailure, RuntimeBackend.Program] = {
    val key    = Image(image)
    val cached = cacheLock.withLock {
      val entry = decoded.getOrElseUpdate(key, Cached(key))
      entry.references += 1
      entry
    }

    val loaded = try {
      // FutureTask runs at most once. Different images parse concurrently; identical images wait here,
      // outside the cache lock. Each load still receives its own mutable runtime state.
      cached.program.run()
      val parsed = try cached.program.get()
      catch { case error: ExecutionException => throw error.getCause }
      parsed.left.map(error => ExecutionFailure.InvalidRequest(error.message))
        .flatMap(parsed =>
          LoadedProgram.load(parsed)
            .left.map(error => ExecutionFailure.InvalidRequest(error.message))
            .map(loaded => Program(loaded, cached))
        )
    } catch {
      case error: Throwable =>
        release(cached)
        throw error
    }
    loaded.left.map { error =>
      release(cached)
      error
    }
  }

  private class Program(loaded: LoadedProgram, cached: Cached)
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
      release(cached)
    }
  }
}
