package net.kurobako.gemstone.isa.ptx.v9_3

import Program.*
import VMIR.*

import net.kurobako.gemstone.PlatformTime
import net.kurobako.gemstone.benchmarks.BenchmarkArguments
import net.kurobako.gemstone.benchmarks.BenchmarkName
import net.kurobako.gemstone.benchmarks.PlatformMetrics
import net.kurobako.gemstone.memory.ArrayCheckedMemory
import net.kurobako.gemstone.vm.LaunchGeometry
import net.kurobako.gemstone.vm.LaunchGeometry.Dimensions
import cats.syntax.all.*
import com.monovore.decline.Opts
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import java.nio.file.Path
import scala.util.Using

/** Standalone warmed benchmark. Timings are observations, never test assertions. */
object PTXExecutionBenchmark {
  private enum GeometryMode {
    case Vector, Tiny
  }

  private final case class Options(
      mode: GeometryMode,
      measured: Int :| Positive,
      warmup: Int :| Positive0,
      rounds: Int :| Positive,
      recording: Option[Path]
  )

  private val options = (
    Opts.option[String]("mode", help = "Geometry mode: vector or tiny").withDefault("vector")
      .validate("mode must be vector or tiny")(value => value == "vector" || value == "tiny")
      .map(value => if (value == "tiny") GeometryMode.Tiny else GeometryMode.Vector),
    Opts.option[Int]("measured", help = "Launches per measured round").withDefault(100)
      .validate("measured launches must be positive")(_ > 0).map(_.refineUnsafe[Positive]),
    Opts.option[Int]("warmup", help = "Warmup launches").withDefault(1000)
      .validate("warmup launches must be non-negative")(_ >= 0).map(_.refineUnsafe[Positive0]),
    Opts.option[Int]("rounds", help = "Measured rounds").withDefault(5)
      .validate("measured rounds must be positive")(_ > 0).map(_.refineUnsafe[Positive]),
    Opts.option[String]("recording", help = "JFR output path").map(Path.of(_)).orNone
  ).mapN(Options(_, _, _, _, _))
  private val source = """.version 8.0
    .target sm_80
    .address_size 64
    .visible .entry vector_add(.param .u64 input_a, .param .u64 input_b, .param .u64 output) {
      .reg .b32 %r<6>;
      .reg .b64 %rd<4>;
      ld.param.u64 %rd1, [input_a];
      ld.param.u64 %rd2, [input_b];
      ld.param.u64 %rd3, [output];
      mov.u32 %r0, %tid.x;
      mov.u32 %r4, %ctaid.x;
      mov.u32 %r5, %ntid.x;
      mad.lo.u32 %r0, %r4, %r5, %r0;
      mul.wide.u32 %rd0, %r0, 4;
      add.s64 %rd1, %rd1, %rd0;
      add.s64 %rd2, %rd2, %rd0;
      add.s64 %rd3, %rd3, %rd0;
      ld.global.u32 %r1, [%rd1];
      ld.global.u32 %r2, [%rd2];
      add.u32 %r3, %r1, %r2;
      st.global.u32 [%rd3], %r3;
      ret;
    }"""

  // Setup, warmup and sample loops use counters so collection allocation is not part of the measurement.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def run(args: Array[String]): Unit = {
    val parsed                  = BenchmarkArguments.parse(BenchmarkName("ptx-execution"), args.toSeq, options)
    val mode                    = parsed.mode
    val measured                = parsed.measured
    val warmup                  = parsed.warmup
    val rounds                  = parsed.rounds
    val lanes: Int :| Positive  = if (mode == GeometryMode.Tiny) 1 else 256
    val groups: Int :| Positive = if (mode == GeometryMode.Tiny) 1 else 16
    val size: Int :| Positive   = (lanes * groups).refineUnsafe[Positive]
    val kernel   = VMCompiler.compile(source).fold(e => throw IllegalArgumentException(e.message), value => value)
    val geometry = LaunchGeometry.forLaunch(
      dimensions(lanes),
      dimensions(groups)
    ).fold(error => throw IllegalArgumentException(error), value => value)
    val memory = ArrayCheckedMemory.allocate((size * 12).refineUnsafe[Positive0])
    val base   = memory.allocation.start
    var i      = 0
    while (i < size) {
      memory.writeInt32(base.offset(i.toLong * 4), i * 97 - 11)
        .fold(error => throw IllegalStateException(error.toString), value => value)
      memory.writeInt32(base.offset(size.toLong * 4 + i.toLong * 4), i * 3 + 7)
        .fold(error => throw IllegalStateException(error.toString), value => value)
      i += 1
    }
    val parameters = Map(
      ParameterName("input_a") -> Argument(base.value),
      ParameterName("input_b") -> Argument(base.value + size.toLong * 4),
      ParameterName("output")  -> Argument(base.value + size.toLong * 8)
    )
    def execute(): Unit =
      VMInterpreter.execute(kernel, geometry, parameters, memory)
        .fold(e => throw IllegalStateException(e.message), _ => ())
    def verify(): Unit = {
      var n = 0
      while (n < size) {
        val observed = memory.readInt32(base.offset(size.toLong * 8 + n.toLong * 4))
          .fold(error => throw IllegalStateException(error.toString), value => value)
        if (observed != n * 100 - 4)
          throw IllegalStateException(s"unexpected result at index $n")
        n += 1
      }
    }
    i = 0
    while (i < warmup) { execute(); i += 1 }
    if (warmup > 0) verify()
    Using.resource(PlatformMetrics.startRecording(parsed.recording)) { _ =>
      var round = 0
      while (round < rounds) {
        val before = PlatformMetrics.allocatedBytes
        val start  = PlatformTime.monotonicNanos
        i = 0
        while (i < measured) { execute(); i += 1 }
        val elapsed   = PlatformTime.monotonicNanos - start
        val allocated = for {
          start <- before
          end   <- PlatformMetrics.allocatedBytes
          if end >= start
        } yield (end - start).refineUnsafe[Positive0]
        verify()
        Console.out.println(
          s"mode=${mode.toString.toLowerCase} round=$round launches=$measured nsPerLaunch=${elapsed.toDouble /
              measured} bytesPerLaunch=${allocated.fold(Double.NaN)(_.toDouble / measured)} verified=$size"
        )
        round += 1
      }
    }
  }

  private def dimensions(x: Int :| Positive): Dimensions = Dimensions(x, 1, 1)
}
