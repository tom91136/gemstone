package net.kurobako.gemstone.isa.amdgpu

import ELF.*
import net.kurobako.gemstone.benchmarks.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*

/** Linked OpenMP images have hundreds of aliases; most accesses target the external heap or stack. */
object AMDGPUDispatchMemoryBenchmark {
  private object Memory extends BenchmarkAddressSpace {
    protected val regionSizes: Iterable[(AllocationId, Long)]                                   = Seq.empty
    def read(region: AllocationId, offset: Long >= 0, size: Int >= 0): Either[Fault, Vec[Byte]] =
      Left(Fault.Memory("routing benchmark does not read storage"))
    def write(region: AllocationId, offset: Long >= 0, bytes: Vec[Byte]): Either[Fault, Unit] =
      Left(Fault.Memory("routing benchmark does not write storage"))
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def run(arguments: Array[String]): BenchmarkSample = {
    val iterations = BenchmarkArguments.iterations(BenchmarkName("amdgpu-dispatch-memory"), arguments.toSeq, 5000000)
    val sections   = Array.tabulate(16)(index => (0x1000L + index * 0x10000L) -> Array.ofDim[Byte](8192))
    val kernel     = CodeObjectKernel(
      name = "routing",
      target = Target("gfx1036"),
      kernargSize = 0,
      groupSegmentSize = 0,
      privateSegmentSize = 0,
      computePgmRsrc2 = 0,
      kernelCodeProperties = 0,
      code = Vec.empty,
      globalMemory = sections
    )
    val aliases = Array.tabulate(512)(index =>
      VMDispatchMemory.GlobalBinding(
        address = 0x1000L + index * 16L,
        size = 16,
        allocation = AllocationId(1),
        offset = (index * 16L).unsafe[Long >= 0],
        allocationSize = 8192L
      )
    )
    val memory    = VMDispatchMemory(kernel, Array.emptyByteArray, Array.emptyByteArray, aliases, Memory)
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
    val measurement = Measurement(measure(iterations.raw))
    val expected    = iterations.raw / 8L * 6L + math.max(0, iterations.raw % 8 - 2)
    require(measurement.value == expected, s"routing checksum ${measurement.value} != $expected")
    measurement.sample(iterations.toLong)
  }
}
