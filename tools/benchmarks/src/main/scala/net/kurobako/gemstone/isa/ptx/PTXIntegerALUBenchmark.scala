package net.kurobako.gemstone.isa.ptx

import net.kurobako.gemstone.benchmarks.*
import net.kurobako.gemstone.benchmarks.BenchmarkArguments.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

/** A per-lane loop of integer ALU work over 16 groups of 256 lanes, dominated by the integer instruction paths. */
object PTXIntegerALUBenchmark {
  val benchmark: Case = iterations(20).map(count => () => run(count))

  private val Lanes: Int > 0  = 256
  private val Groups: Int > 0 = 16
  private val Rounds          = 64
  private val source          = s""".version 8.0
    .target sm_80
    .address_size 64
    .visible .entry alu(.param .u64 output) {
      .reg .pred %p;
      .reg .b32 %r<10>;
      .reg .b64 %rd<2>;
      ld.param.u64 %rd1, [output];
      mov.u32 %r0, %tid.x;
      mov.u32 %r1, %ctaid.x;
      mov.u32 %r2, %ntid.x;
      mad.lo.u32 %r0, %r1, %r2, %r0;
      mov.u32 %r3, %r0;
      mov.u32 %r4, 0;
    loop:
      add.u32 %r3, %r3, 0x9e3779b9;
      xor.b32 %r5, %r3, %r4;
      shl.b32 %r6, %r5, 3;
      shr.u32 %r7, %r5, 5;
      xor.b32 %r3, %r6, %r7;
      mul.lo.u32 %r3, %r3, 0x45d9f3b;
      min.u32 %r8, %r3, %r5;
      max.s32 %r9, %r3, %r8;
      sub.u32 %r3, %r9, %r8;
      or.b32 %r3, %r3, %r4;
      and.b32 %r3, %r3, 0x7fffffff;
      add.u32 %r4, %r4, 1;
      setp.lt.u32 %p, %r4, $Rounds;
      @%p bra loop;
      mul.wide.u32 %rd0, %r0, 4;
      add.s64 %rd1, %rd1, %rd0;
      st.global.u32 [%rd1], %r3;
      ret;
    }"""

  private def expected(index: Int): Int = (0 until Rounds).foldLeft(index) { (x, round) =>
    val added   = x + 0x9e3779b9
    val mixed   = added ^ round
    val shifted = (mixed << 3) ^ (mixed >>> 5)
    val product = shifted * 0x45d9f3b
    val low     = Integer.compareUnsigned(product, mixed) match {
      case order if order <= 0 => product
      case _                   => mixed
    }
    val high = math.max(product, low)
    ((high - low) | round) & 0x7fffffff
  }

  private def run(iterations: Int > 0): BenchmarkSample = {
    val size     = Lanes.raw * Groups.raw
    val kernel   = VMCompiler.compile(source).fold(error => throw IllegalArgumentException(error.message), identity)
    val geometry = LaunchGeometry.forLaunch(Dimensions(Lanes, 1, 1), Dimensions(Groups, 1, 1))
      .fold(error => throw IllegalArgumentException(error), identity)
    val memory     = BenchmarkMemory(size * 4)
    val output     = memory.ids(0)
    val parameters = Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.base(output).raw))
    val sample = Measurement.repeatSucceeding(iterations)(VMInterpreter.execute(kernel, geometry, parameters, memory))
    (0 until size).foreach { index =>
      if (memory.getInt(output, index * 4) != expected(index))
        throw IllegalStateException(s"unexpected result at index $index")
    }
    sample
  }
}
