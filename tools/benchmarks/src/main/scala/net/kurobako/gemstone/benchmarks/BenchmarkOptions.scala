package net.kurobako.gemstone.benchmarks

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

final case class BenchmarkOptions(
    warmups: Int :| Positive0,
    samples: Int :| Positive,
    invocation: BenchmarkInvocation
)
