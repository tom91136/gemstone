package net.kurobako.gemstone.benchmarks

import net.kurobako.gemstone.NonNegativeInt
import net.kurobako.gemstone.PositiveInt

final case class BenchmarkOptions(
    warmups: NonNegativeInt,
    samples: PositiveInt,
    invocation: BenchmarkInvocation
)
