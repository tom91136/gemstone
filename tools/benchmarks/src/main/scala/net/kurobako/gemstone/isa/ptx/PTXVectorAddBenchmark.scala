package net.kurobako.gemstone.isa.ptx

import net.kurobako.gemstone.benchmarks.*
import net.kurobako.gemstone.benchmarks.BenchmarkArguments.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

/** Integer vector addition over 16 groups of 256 lanes. */
object PTXVectorAddBenchmark {
  val benchmark: Case = iterations(100).map(count => () => run(count))

  private val Lanes: Int > 0  = 256
  private val Groups: Int > 0 = 16
  private val source          = """.version 8.0
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

  private def run(iterations: Int > 0): BenchmarkSample = {
    val size     = Lanes.raw * Groups.raw
    val kernel   = VMCompiler.compile(source).fold(error => throw IllegalArgumentException(error.message), identity)
    val geometry = LaunchGeometry.forLaunch(Dimensions(Lanes, 1, 1), Dimensions(Groups, 1, 1))
      .fold(error => throw IllegalArgumentException(error), identity)
    val memory = BenchmarkMemory(size * 4, size * 4, size * 4)
    val a      = memory.ids(0)
    val b      = memory.ids(1)
    val output = memory.ids(2)
    (0 until size).foreach { index =>
      memory.putInt(a, index * 4, index * 97 - 11)
      memory.putInt(b, index * 4, index * 3 + 7)
    }
    val parameters = Map(
      ("input_a": NonEmpty[String]) -> Argument.fromLong(memory.base(a).raw),
      ("input_b": NonEmpty[String]) -> Argument.fromLong(memory.base(b).raw),
      ("output": NonEmpty[String])  -> Argument.fromLong(memory.base(output).raw)
    )
    val sample = Measurement.repeatSucceeding(iterations)(VMInterpreter.execute(kernel, geometry, parameters, memory))
    (0 until size).foreach { index =>
      if (memory.getInt(output, index * 4) != index * 100 - 4)
        throw IllegalStateException(s"unexpected result at index $index")
    }
    sample
  }
}
