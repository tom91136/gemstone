package net.kurobako.gemstone.isa.ptx

import com.monovore.decline.Opts
import net.kurobako.gemstone.benchmarks.*
import net.kurobako.gemstone.benchmarks.BenchmarkArguments.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

/** Generic accesses into the shared window, with and without a registered allocation there. */
object PTXGenericSharedBenchmark {
  private enum Kernel derives CanEqual { case Stream, Stride }

  private val selected: Kernel = Option(System.getenv("T251_KERNEL")).getOrElse("stream") match {
    case "stride" => Kernel.Stride
    case _        => Kernel.Stream
  }
  private val rounds: Int > 0 = Option(System.getenv("T251_ROUNDS")).fold(2000)(_.toInt).unsafe[Int > 0]

  val benchmark: Case = Opts(launch => run(selected, rounds, launch))

  private val Lanes: Int > 0  = 128
  private val Groups: Int > 0 = 4

  private def source(kernel: Kernel): String = kernel match {
    case Kernel.Stream => """.version 8.0
      .target sm_89
      .address_size 64
      .visible .entry stream(.param .u64 output, .param .u32 rounds) {
        .shared .align 4 .b8 buffer[4096];
        .reg .u32 %i, %n, %tid, %idx, %sum;
        .reg .u64 %base, %addr, %out;
        .reg .pred %p;
        ld.param.u64 %out, [output];
        ld.param.u32 %n, [rounds];
        mov.u32 %tid, %tid.x;
        mov.u64 %base, buffer;
        mov.u32 %sum, %tid;
        mov.u32 %i, 0;
      LOOP:
        mad.lo.u32 %idx, %i, 7, %tid;
        and.b32 %idx, %idx, 1023;
        mul.lo.u32 %idx, %idx, 4;
        add.u64 %addr, %base, %idx;
        ld.u32 %sum, [%addr];
        add.u32 %sum, %sum, 1;
        st.u32 [%addr], %sum;
        add.u32 %i, %i, 1;
        setp.lt.u32 %p, %i, %n;
        @%p bra LOOP;
        mul.wide.u32 %off, %tid, 4;
        add.u64 %out, %out, %off;
        st.global.u32 [%out], %sum;
        ret;
      }"""
    case Kernel.Stride => """.version 8.0
      .target sm_89
      .address_size 64
      .visible .entry stride(.param .u64 output, .param .u32 rounds) {
        .shared .align 8 .b8 buffer[2048];
        .reg .u32 %i, %n, %tid, %idx, %word;
        .reg .u64 %base, %addr, %out;
        .reg .b64 %v;
        .reg .pred %p;
        ld.param.u64 %out, [output];
        ld.param.u32 %n, [rounds];
        mov.u32 %tid, %tid.x;
        mov.u64 %base, buffer;
        mov.u32 %i, 0;
      LOOP:
        mad.lo.u32 %idx, %i, 37, %tid;
        and.b32 %idx, %idx, 255;
        mul.lo.u32 %idx, %idx, 8;
        add.u64 %addr, %base, %idx;
        ld.u64 %v, [%addr];
        cvt.u32.u64 %word, %v;
        add.u32 %word, %word, 1;
        cvt.u64.u32 %v, %word;
        st.u64 [%addr], %v;
        add.u32 %i, %i, 1;
        setp.lt.u32 %p, %i, %n;
        @%p bra LOOP;
        mul.wide.u32 %off, %tid, 8;
        add.u64 %out, %out, %off;
        st.global.u64 [%out], %v;
        ret;
      }"""
  }

  private lazy val compiledStream = VMCompiler.compile(source(Kernel.Stream))
    .fold(error => throw IllegalArgumentException(error.message), identity)
  private lazy val compiledStride = VMCompiler.compile(source(Kernel.Stride))
    .fold(error => throw IllegalArgumentException(error.message), identity)

  private def run(kernel: Kernel, rounds: Int > 0, launch: LaunchControl): BenchmarkSample = {
    val compiled = kernel match {
      case Kernel.Stream => compiledStream
      case Kernel.Stride => compiledStride
    }
    val geometry = LaunchGeometry.forLaunch(Dimensions(Lanes, 1, 1), Dimensions(Groups, 1, 1))
      .fold(error => throw IllegalArgumentException(error), identity)
    val buffer      = BenchmarkMemory(Lanes.raw * Groups.raw * 8)
    val destination = buffer.ids(0)
    val arguments   = Map[NonEmpty[String], Argument](
      ("output": NonEmpty[String]) -> Argument.fromLong(buffer.base(destination).raw)
    ) + (("rounds": NonEmpty[String]) -> Argument.fromLong(rounds.toLong))
    val sample = Measurement.repeatSucceeding(1)(
      VMInterpreter.execute(compiled, geometry, arguments, buffer, launch())
    )
    val checksum = (0 until Lanes.raw * Groups.raw).map { index =>
      kernel match {
        case Kernel.Stream => buffer.getInt(destination, index * 8).toLong
        case Kernel.Stride => buffer.getLong(destination, index * 8)
      }
    }.sum
    println(s"T259_CHECKSUM=$checksum")
    sample
  }
}
