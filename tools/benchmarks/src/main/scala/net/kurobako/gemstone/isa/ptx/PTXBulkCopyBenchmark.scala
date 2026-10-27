package net.kurobako.gemstone.isa.ptx

import com.monovore.decline.Opts
import net.kurobako.gemstone.benchmarks.*
import net.kurobako.gemstone.benchmarks.BenchmarkArguments.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

object PTXBulkCopyBenchmark {
  val benchmark: Case = Opts(() => run())

  private val Lanes: Int > 0 = 32
  private val Bytes          = 1024
  private val Rounds         = 1000
  private val source         = s""".version 8.0
    .target sm_90
    .address_size 64
    .visible .entry bulk_copy(.param .u64 input, .param .u64 output) {
      .shared .align 128 .b8 buffer[$Bytes];
      .shared .align 8 .b64 bar;
      .reg .b64 %src, %dst, %token;
      .reg .b32 %tid, %round, %parity, %value;
      .reg .pred %leader, %done, %more;
      ld.param.u64 %src, [input];
      ld.param.u64 %dst, [output];
      mov.u32 %tid, %tid.x;
      setp.eq.u32 %leader, %tid, 0;
      @%leader mbarrier.init.shared::cta.b64 [bar], 1;
      fence.mbarrier_init.release.cluster;
      bar.sync 0;
      @!%leader bra DONE;
      mov.u32 %round, 0;
    LOOP:
      mbarrier.arrive.expect_tx.release.cta.shared::cta.b64 %token, [bar], $Bytes;
      cp.async.bulk.shared::cta.global.mbarrier::complete_tx::bytes [buffer], [%src], $Bytes, [bar];
      and.b32 %parity, %round, 1;
    WAIT:
      mbarrier.try_wait.parity.shared::cta.b64 %done, [bar], %parity;
      @!%done bra WAIT;
      add.u32 %round, %round, 1;
      setp.lt.u32 %more, %round, $Rounds;
      @%more bra LOOP;
      ld.shared.u32 %value, [buffer+${Bytes - 4}];
      st.global.u32 [%dst], %value;
    DONE:
      ret;
    }"""

  private def run(): BenchmarkSample = {
    val kernel   = VMCompiler.compile(source).fold(error => throw IllegalArgumentException(error.message), identity)
    val geometry = LaunchGeometry.forLaunch(Dimensions(Lanes, 1, 1), Dimensions(1, 1, 1))
      .fold(error => throw IllegalArgumentException(error), identity)
    val memory = BenchmarkMemory(Bytes, 4)
    val input  = memory.ids(0)
    val output = memory.ids(1)
    (0 until Bytes / 4).foreach(index => memory.putInt(input, index * 4, index * 97 - 11))
    val parameters = Map(
      ("input": NonEmpty[String])  -> Argument.fromLong(memory.base(input).raw),
      ("output": NonEmpty[String]) -> Argument.fromLong(memory.base(output).raw)
    )
    val sample   = Measurement.repeatSucceeding(1)(VMInterpreter.execute(kernel, geometry, parameters, memory))
    val expected = memory.getInt(input, Bytes - 4)
    if (memory.getInt(output, 0) != expected)
      throw IllegalStateException(s"bulk copy delivered ${memory.getInt(output, 0)}, expected $expected")
    sample
  }
}
