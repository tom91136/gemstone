package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.benchmarks.*
import net.kurobako.gemstone.benchmarks.BenchmarkArguments.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*

/** Linked OpenMP images have hundreds of aliases; most accesses target the external heap or stack. */
object AMDGPUDispatchMemoryBenchmark {
  val benchmark: Case = iterations(5000000).map(count => () => run(count))

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def run(iterations: Int > 0): BenchmarkSample = {
    val sections = Array.tabulate(16)(index => (0x1000L + index * 0x10000L) -> Array.ofDim[Byte](8192))
    val kernel   = CodeObjectKernel(
      name = "routing",
      target = Target("gfx1036"),
      kernargSize = 0,
      groupSegmentSize = 0,
      privateSegmentSize = 0,
      computePgmRsrc2 = 0,
      kernelCodeProperties = 0,
      text = Vec.empty,
      entrySize = 0,
      globalMemory = sections
    )
    val aliases = Array.tabulate(512)(index =>
      VMDispatchMemory.GlobalMapping(
        GuestAddress(0x1000L + index * 16L),
        16,
        GlobalBinding.validated("alias", AllocationId(1), (index * 16L).unsafe[Long >= 0], 16L, 8192L)
          .fold(error => throw IllegalStateException(error.message), identity)
      )
    )
    val memory = VMDispatchMemory(
      kernel,
      Array.emptyByteArray,
      Array.emptyByteArray,
      aliases,
      BenchmarkMemory(),
      VMDispatchMemory.KernargAddress
    )
    val addresses = Array.tabulate(4096) { index =>
      index % 8 match {
        case 0 => 0x1000L + (index % aliases.length) * 16L
        case 1 => sections(1 + index % (sections.length - 1))._1 + index
        case _ => 0x400000000000L + index * 8L
      }
    }
    def measure(count: Int): Long = {
      var index    = 0
      var external = 0L
      while (index < count) {
        if (memory.isExternal(addresses(index % addresses.length), 4)) external += 1L
        index += 1
      }
      external
    }
    val measurement = Measurement(measure(iterations))
    val expected    = iterations / 8L * 6L + math.max(0, iterations % 8 - 2)
    require(measurement.value == expected, s"routing checksum ${measurement.value} != $expected")
    measurement.sample(iterations.widen[Long > 0])
  }
}
