package net.kurobako.gemstone.isa.ptx

import cats.syntax.all.*
import net.kurobako.gemstone.benchmarks.*
import net.kurobako.gemstone.benchmarks.BenchmarkArguments.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import scala.util.Using

/** Divergent nested device calls exchange per-lane results through shared memory and CTA barriers. */
object PTXCallBenchmark {
  val benchmark: Case = (
    iterations(5),
    positive("calls", "Nested calls per lane", 8),
    positive("lanes", "Even lane count per group, at most 1024", 128)
      .validate("lanes must be even and at most 1024")(value => value.raw <= 1024 && value.raw % 2 == 0),
    positive("groups", "Workgroups per dispatch", 16),
    workers(1)
  ).mapN((iterations, calls, lanes, groups, workers) => () => run(iterations, calls, lanes, groups, workers))

  private def source(lanes: Int > 0): String = s""".version 8.0
    .target sm_80
    .address_size 64
    .func (.param .u64 result) increment(.param .u64 value) {
      .reg .u64 %rd0;
      ld.param.u64 %rd0, [value];
      add.u64 %rd0, %rd0, 1;
      st.param.u64 [result], %rd0;
      ret;
    }
    .func (.param .u64 result) increment_twice(.param .u64 value) {
      .reg .u64 %rd0;
      ld.param.u64 %rd0, [value];
      add.u64 %rd0, %rd0, 2;
      st.param.u64 [result], %rd0;
      ret;
    }
    .func (.param .u64 result) worker(.param .u64 value) {
      .shared .align 8 .b8 exchange[${lanes.raw * 8}];
      .reg .u64 %rd<3>;
      .reg .u32 %r<2>;
      .reg .pred %p0;
      .param .u64 argument;
      .param .u64 returned;
      ld.param.u64 %rd0, [value];
      st.param.u64 [argument], %rd0;
      mov.u32 %r0, %tid.x;
      and.b32 %r1, %r0, 1;
      setp.ne.u32 %p0, %r1, 0;
      @%p0 bra odd;
      call (returned), increment, (argument);
      bra exchange_result;
    odd:
      call (returned), increment_twice, (argument);
    exchange_result:
      ld.param.u64 %rd0, [returned];
      mov.u64 %rd1, exchange;
      mul.wide.u32 %rd2, %r0, 8;
      add.u64 %rd2, %rd1, %rd2;
      st.shared.u64 [%rd2], %rd0;
      bar.sync 0;
      xor.b32 %r0, %r0, 1;
      mul.wide.u32 %rd2, %r0, 8;
      add.u64 %rd2, %rd1, %rd2;
      ld.shared.u64 %rd0, [%rd2];
      bar.sync 0;
      st.param.u64 [result], %rd0;
      ret;
    }
    .visible .entry calls(.param .u64 output, .param .u32 count) {
      .reg .u32 %r<5>;
      .reg .u64 %rd<3>;
      .reg .pred %p0;
      .param .u64 argument;
      .param .u64 returned;
      ld.param.u64 %rd0, [output];
      ld.param.u32 %r3, [count];
      mov.u32 %r0, %tid.x;
      mov.u32 %r1, %ctaid.x;
      mov.u32 %r4, %ntid.x;
      mad.lo.u32 %r0, %r1, %r4, %r0;
      cvt.u64.u32 %rd1, %r0;
      mov.u32 %r2, 0;
    loop:
      st.param.u64 [argument], %rd1;
      call (returned), worker, (argument);
      ld.param.u64 %rd1, [returned];
      add.u32 %r2, %r2, 1;
      setp.lt.u32 %p0, %r2, %r3;
      @%p0 bra loop;
      mul.wide.u32 %rd2, %r0, 8;
      add.u64 %rd0, %rd0, %rd2;
      st.global.u64 [%rd0], %rd1;
      ret;
    }"""

  private def run(iterations: Int > 0, calls: Int > 0, lanes: Int > 0, groups: Int > 0, workers: Int > 0)
      : BenchmarkSample = {
    val kernel =
      VMCompiler.compile(source(lanes)).fold(error => throw IllegalArgumentException(error.message), identity)
    val geometry = LaunchGeometry.forLaunch(Dimensions(lanes, 1, 1), Dimensions(groups, 1, 1))
      .fold(error => throw IllegalArgumentException(error), identity)
    val count      = Math.multiplyExact(lanes.raw, groups.raw)
    val memory     = BenchmarkMemory(Math.multiplyExact(count, 8))
    val output     = memory.ids(0)
    val parameters = Map(
      ("output": NonEmpty[String]) -> Argument.fromLong(memory.base(output).raw),
      ("count": NonEmpty[String])  -> Argument.fromLong(calls.raw.toLong)
    )
    val sample = Using.resource(WorkerPool(parallelism = workers, minGroups = 1, minWorkItems = 1)) { pool =>
      Measurement.repeat(iterations) {
        VMInterpreter.execute(
          kernel = kernel,
          geometry = geometry,
          arguments = parameters,
          memory = memory,
          workgroups = pool
        )
          .fold(error => throw IllegalStateException(error.message), _ => ())
      }
    }
    val oddCalls = calls.raw & 1
    (0 until count).foreach { lane =>
      val actual   = memory.getLong(output, lane * 8)
      val expected = (lane ^ oddCalls).toLong + calls.raw.toLong / 2 * 3 +
        (if (oddCalls == 0) 0 else if ((lane & 1) == 0) 2 else 1)
      require(actual == expected, s"lane $lane: expected $expected, got $actual")
    }
    sample
  }
}
