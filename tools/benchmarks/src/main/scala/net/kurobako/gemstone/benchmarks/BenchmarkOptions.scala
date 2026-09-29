package net.kurobako.gemstone.benchmarks

import net.kurobako.gemstone.constraint.*

case class BenchmarkOptions(
    warmups: Int >= 0,
    samples: Int > 0,
    invocation: BenchmarkInvocation
)
