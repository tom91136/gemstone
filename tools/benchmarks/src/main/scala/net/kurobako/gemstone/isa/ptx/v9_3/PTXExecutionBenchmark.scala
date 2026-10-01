package net.kurobako.gemstone.isa.ptx.v9_3

import VMIR.*
import cats.syntax.all.*
import com.monovore.decline.Opts
import java.nio.file.Path
import net.kurobako.gemstone.*
import net.kurobako.gemstone.benchmarks.*
import net.kurobako.gemstone.benchmarks.BenchmarkArguments.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.string.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import scala.util.Using

/** Standalone warmed benchmark. Timings are observations, never test assertions. */
object PTXExecutionBenchmark {
  private enum GeometryMode {
    case Vector, Tiny
  }

  private case class Options(
      mode: GeometryMode,
      measured: Int > 0,
      warmup: Int >= 0,
      rounds: Int > 0,
      recording: Option[Path]
  )

  private val options = (
    Opts.option[String]("mode", help = "Geometry mode: vector or tiny").withDefault("vector")
      .validate("mode must be vector or tiny")(value => value == "vector" || value == "tiny")
      .map(value => if (value == "tiny") GeometryMode.Tiny else GeometryMode.Vector),
    Opts.option[Int]("measured", help = "Launches per measured round").withDefault(100)
      .refined[Int > 0]("measured launches must be positive"),
    Opts.option[Int]("warmup", help = "Warmup launches").withDefault(1000)
      .refined[Int >= 0]("warmup launches must be non-negative"),
    Opts.option[Int]("rounds", help = "Measured rounds").withDefault(5)
      .refined[Int > 0]("measured rounds must be positive"),
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
    val parsed          = BenchmarkArguments.parse(BenchmarkName("ptx-execution"), args.toSeq, options)
    val mode            = parsed.mode
    val measured        = parsed.measured
    val warmup          = parsed.warmup
    val rounds          = parsed.rounds
    val lanes: Int > 0  = if (mode == GeometryMode.Tiny) 1 else 256
    val groups: Int > 0 = if (mode == GeometryMode.Tiny) 1 else 16
    val size: Int > 0   = lanes * groups
    val kernel   = VMCompiler.compile(source).fold(e => throw IllegalArgumentException(e.message), value => value)
    val geometry = LaunchGeometry.forLaunch(
      dimensions(lanes),
      dimensions(groups)
    ).fold(error => throw IllegalArgumentException(error), value => value)
    val memory = ArrayCheckedMemory.allocate(size * 12)
    val base   = memory.allocation.start
    var i      = 0
    while (i < size.raw) {
      memory.writeInt32(base.offset(i.toLong * 4), i * 97 - 11)
        .fold(error => throw IllegalStateException(error.toString), value => value)
      memory.writeInt32(base.offset(size.raw.toLong * 4 + i.toLong * 4), i * 3 + 7)
        .fold(error => throw IllegalStateException(error.toString), value => value)
      i += 1
    }
    val parameters = Map(
      ("input_a": NonEmpty[String]) -> Argument.fromLong(base.raw),
      ("input_b": NonEmpty[String]) -> Argument.fromLong(base.raw + size.raw.toLong * 4),
      ("output": NonEmpty[String])  -> Argument.fromLong(base.raw + size.raw.toLong * 8)
    )
    def execute(): Unit =
      VMInterpreter.execute(kernel, geometry, parameters, memory)
        .fold(e => throw IllegalStateException(e.message), _ => ())
    def verify(): Unit = {
      var n = 0
      while (n < size.raw) {
        val observed = memory.readInt32(base.offset(size.raw.toLong * 8 + n.toLong * 4))
          .fold(error => throw IllegalStateException(error.toString), value => value)
        if (observed != n * 100 - 4)
          throw IllegalStateException(s"unexpected result at index $n")
        n += 1
      }
    }
    i = 0
    while (i < warmup.raw) { execute(); i += 1 }
    if (warmup.raw > 0) verify()
    Using.resource(PlatformMetrics.startRecording(parsed.recording)) { _ =>
      var round = 0
      while (round < rounds.raw) {
        val before = PlatformMetrics.allocatedBytes
        val start  = platform.monotonicNanos
        i = 0
        while (i < measured.raw) { execute(); i += 1 }
        val elapsed   = platform.monotonicNanos - start
        val allocated = for {
          start <- before
          end   <- PlatformMetrics.allocatedBytes
          if end >= start
        } yield end - start
        verify()
        Console.out.println(
          s"mode=${mode.toString.toLowerCaseRoot} round=$round launches=$measured nsPerLaunch=${elapsed.toDouble /
              measured.raw} bytesPerLaunch=${allocated.fold(Double.NaN)(_.raw.toDouble / measured.raw)} verified=$size"
        )
        round += 1
      }
    }
  }

  private def dimensions(x: Int > 0): Dimensions = Dimensions(x, 1, 1)
}
