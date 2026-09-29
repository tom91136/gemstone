package net.kurobako.gemstone.benchmarks

import net.kurobako.gemstone.constraint.*

type BenchmarkName = BenchmarkName.Type
object BenchmarkName extends Newtype[NonEmpty[String]] {}
