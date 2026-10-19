package net.kurobako.gemstone.isa.ptx

import java.util.concurrent.ExecutionException
import java.util.concurrent.locks.ReentrantLock
import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.concurrent.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.vm.*
import scala.collection.immutable.ArraySeq
import scala.collection.mutable

object PTXBackend {
  val format: ProgramFormat = ProgramFormat.PTXText
}

class PTXBackend(
    workgroups: Workgroups = Workgroups.Sequential,
    decode: Vec[Byte] => Either[ISA.DecodeError, Program] = ISA.decode(_)
) extends Backend {
  override val format: ProgramFormat = PTXBackend.format

  private def admit(program: Program): Either[Fault, Program] = {
    def reject(issues: NonEmpty[Vec[Validation.Issue]]) =
      Left(Fault.InvalidRequest(issues.map(_.message.raw).distinct.mkString("; ").unsafe))
    // ptxas accepts forms the specification omits, so only availability is enforced at load
    Validation.validate(program, forms = false) match {
      case ValidationOutcome.Accepted           => Right(program)
      case ValidationOutcome.Rejected(issues)   => reject(issues)
      case ValidationOutcome.Incomplete(issues) => reject(issues)
    }
  }

  private case class Image(bytes: Vec[Byte]) {
    // Hash outside the cache lock and retain it for release; modules can be many megabytes.
    private val hash = bytes match {
      case image: ArraySeq.ofByte => PlatformChecksum.crc32(image.unsafeArray)
      case _                      => PlatformChecksum.crc32(Vec.toArray(bytes))
    }
    override def hashCode(): Int = hash
  }

  private class Cached(val image: Image) {
    val program: PlatformConcurrency.Once[Either[Fault, Program]] = PlatformConcurrency.once(() =>
      decode(image.bytes).left.map(error => Fault.InvalidRequest(error.message)).flatMap(admit(_))
    )
    // Includes callers still waiting for the parse, so a completed load cannot evict their entry.
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    var references: Int >= 0 = 0
  }

  // Share immutable parsed syntax only while loaded programs reference it; runtime state remains per load.
  private val cacheLock = ReentrantLock()
  private val decoded   = mutable.HashMap.empty[Image, Cached]

  private def release(cached: Cached): Unit = cacheLock.withLock {
    cached.references -= 1
    if (cached.references == 0) {
      val _ = decoded.remove(cached.image)
    }
  }

  override def load(image: Vec[Byte]): Either[Fault, Backend.Program] = {
    val key    = Image(image)
    val cached = cacheLock.withLock {
      val entry = decoded.getOrElseUpdate(key, Cached(key))
      entry.references += 1
      entry
    }

    val loaded = try {
      // The parse runs at most once. Different images parse concurrently; identical images wait here,
      // outside the cache lock. Each load still receives its own mutable runtime state.
      cached.program.run()
      val parsed = try cached.program.get()
      catch { case error: ExecutionException => throw error.getCause }
      parsed.flatMap(LoadedProgram.load(_, workgroups, () => release(cached)))
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
}
