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
    decode: Vec[Byte] => Either[ISA.DecodeError, Program] = ISA.decode(_),
    retainReleased: Boolean = true
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

  // Share immutable parsed syntax between loads; runtime state remains per load. Each case of a conformance run loads
  // its module afresh, and parsing dominates the load, so released syntax stays retained while memory allows.
  private val cacheLock = ReentrantLock()
  private val decoded   = mutable.HashMap.empty[Image, Cached]
  private val retained  = Retained[Cached]()

  // Only a program that loaded is retained, so a failed decode is retried.
  private def release(cached: Cached, loaded: Boolean): Unit = cacheLock.withLock {
    cached.references -= 1
    if (cached.references == 0) {
      val _ = decoded.remove(cached.image)
      if (loaded && retainReleased) retained.put(cached.image.hashCode, cached)
    }
  }

  override def load(image: Vec[Byte]): Either[Fault, Backend.Program] = {
    val key    = Image(image)
    val cached = cacheLock.withLock {
      val entry =
        decoded.getOrElseUpdate(key, retained.take(key.hashCode)(_.image.bytes == image).getOrElse(Cached(key)))
      entry.references += 1
      entry
    }

    val loaded = try {
      // The parse runs at most once. Different images parse concurrently; identical images wait here,
      // outside the cache lock. Each load still receives its own mutable runtime state.
      cached.program.run()
      val parsed = try cached.program.get()
      catch { case error: ExecutionException => throw error.getCause }
      parsed.flatMap(LoadedProgram.load(_, workgroups, () => release(cached, loaded = true)))
    } catch {
      case error: Throwable =>
        release(cached, loaded = false)
        throw error
    }
    loaded.left.map { error =>
      release(cached, loaded = false)
      error
    }
  }
}
