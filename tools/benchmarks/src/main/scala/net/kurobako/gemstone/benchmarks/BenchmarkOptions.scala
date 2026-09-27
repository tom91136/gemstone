package net.kurobako.gemstone.benchmarks

import net.kurobako.gemstone.Constraint.{NonNegative, Positive}

case class BenchmarkOptions(
    warmups: NonNegative[Int],
    samples: Positive[Int],
    invocation: BenchmarkInvocation
)
