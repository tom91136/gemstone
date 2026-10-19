package net.kurobako.gemstone.isa.ptx

import net.kurobako.gemstone.benchmarks.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.memory.*

object PTXParameters {

  /** The `output` base address and a counter argument, as a kernel that writes its results to `output` takes them. */
  def output(
      memory: BenchmarkMemory,
      output: AllocationId,
      counter: NonEmpty[String],
      value: Long
  ): Map[NonEmpty[String], Argument] =
    Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.base(output).raw), counter -> Argument.fromLong(value))
}
