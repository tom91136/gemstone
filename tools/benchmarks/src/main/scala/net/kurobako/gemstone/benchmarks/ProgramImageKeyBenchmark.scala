package net.kurobako.gemstone.benchmarks

import net.kurobako.gemstone.{ByteKey, PlatformTime, Vec}

import cats.syntax.all.*
import com.monovore.decline.Opts
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import scala.collection.mutable

/** Measures repeated cache lookups for immutable program images. */
object ProgramImageKeyBenchmark {
  private enum KeyMode {
    case Raw, Cached
  }

  private final case class Options(bytes: Int :| Positive, lookups: Int :| Positive, key: KeyMode)

  private val name    = BenchmarkName("program-image-key")
  private val options = (
    Opts.option[Int]("bytes", help = "Program image size").withDefault(4 * 1024 * 1024)
      .validate("image size must be positive")(_ > 0).map(_.refineUnsafe[Positive]),
    Opts.option[Int]("lookups", help = "Measured cache lookups").withDefault(100000)
      .validate("lookup count must be positive")(_ > 0).map(_.refineUnsafe[Positive]),
    Opts.option[String]("key", help = "Cache key implementation: cached or raw").withDefault("cached")
      .validate("key must be cached or raw")(value => value == "cached" || value == "raw")
      .map(value => if (value == "raw") KeyMode.Raw else KeyMode.Cached)
  ).mapN(Options(_, _, _))

  def run(arguments: Array[String]): Unit = {
    val parsed = BenchmarkArguments.parse(name, arguments.toSeq, options)
    val bytes  = Vec.tabulate(parsed.bytes)(index => index.toByte)
    parsed.key match {
      case KeyMode.Raw    => measure(bytes, parsed.lookups, parsed.bytes, parsed.key)
      case KeyMode.Cached => measure(ByteKey(bytes), parsed.lookups, parsed.bytes, parsed.key)
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def measure[K](key: K, lookups: Int :| Positive, bytes: Int :| Positive, mode: KeyMode): Unit = {
    val cache    = mutable.HashMap(key -> 1)
    val started  = PlatformTime.monotonicNanos
    var index    = 0
    var checksum = 0L
    while (index < lookups) {
      checksum += cache(key)
      index += 1
    }
    val elapsed = math.max(0L, PlatformTime.monotonicNanos - started)
    Console.out.println(
      s"key=${mode.toString.toLowerCase} bytes=$bytes lookups=$lookups " +
        s"nsPerLookup=${elapsed.toDouble / lookups} checksum=$checksum"
    )
  }
}
