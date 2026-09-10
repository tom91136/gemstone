package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.execution.*
import net.kurobako.gemstone.memory.ArrayCheckedMemory
import java.lang.management.ManagementFactory
import java.nio.file.Paths
import jdk.jfr.{Configuration, Recording}

/** Standalone warmed benchmark. Timings are observations, never test assertions. */
object PTXExecutionBenchmark {
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

  def main(args: Array[String]): Unit = {
    val mode     = args.headOption.getOrElse("vector")
    val measured = args.lift(1).fold(100)(_.toInt)
    val warmup   = args.lift(2).fold(1000)(_.toInt)
    val rounds   = args.lift(3).fold(5)(_.toInt)
    val lanes    = if (mode == "tiny") 1 else 256
    val groups   = if (mode == "tiny") 1 else 16
    val size     = lanes * groups
    require(measured > 0 && warmup >= 0 && rounds > 0)
    val kernel   = ISA.compile(source).fold(e => throw IllegalArgumentException(e.message), identity)
    val geometry = LaunchGeometry.create(
      Dimensions.create(lanes, 1, 1).toOption.get,
      Dimensions.create(groups, 1, 1).toOption.get
    ).toOption.get
    val memory = ArrayCheckedMemory.allocate(size * 12)
    val base   = memory.allocation.start
    var i      = 0
    while (i < size) {
      memory.writeInt32(base.offset(i.toLong * 4), i * 97 - 11).toOption.get
      memory.writeInt32(base.offset(size.toLong * 4 + i.toLong * 4), i * 3 + 7).toOption.get
      i += 1
    }
    val parameters = Map(
      "input_a" -> Argument(base.value),
      "input_b" -> Argument(base.value + size.toLong * 4),
      "output"  -> Argument(base.value + size.toLong * 8)
    )
    def execute(): Unit =
      Interpreter.execute(kernel, geometry, parameters, memory, ExecutionControl.default, captureTrace = false)
        .fold(e => throw IllegalStateException(e.message), _ => ())
    def verify(): Unit = {
      var n = 0
      while (n < size) {
        require(memory.readInt32(base.offset(size.toLong * 8 + n.toLong * 4)).toOption.get == n * 100 - 4)
        n += 1
      }
    }
    i = 0
    while (i < warmup) { execute(); i += 1 }
    verify()
    val bean = ManagementFactory.getThreadMXBean.asInstanceOf[com.sun.management.ThreadMXBean]
    bean.setThreadAllocatedMemoryEnabled(true)
    val recording = args.lift(4).map { path =>
      val value = Recording(Configuration.getConfiguration("profile"))
      value.setDestination(Paths.get(path))
      value.start()
      value
    }
    var round = 0
    while (round < rounds) {
      val before = bean.getThreadAllocatedBytes(Thread.currentThread().threadId())
      val start  = System.nanoTime()
      i = 0
      while (i < measured) { execute(); i += 1 }
      val elapsed   = System.nanoTime() - start
      val allocated = bean.getThreadAllocatedBytes(Thread.currentThread().threadId()) - before
      verify()
      println(s"mode=$mode round=$round launches=$measured nsPerLaunch=${elapsed.toDouble /
          measured} bytesPerLaunch=${allocated.toDouble / measured} verified=$size")
      round += 1
    }
    recording.foreach { value => value.stop(); value.close() }
  }
}
