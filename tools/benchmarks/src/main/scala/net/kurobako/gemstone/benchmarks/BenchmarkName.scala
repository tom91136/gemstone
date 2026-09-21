package net.kurobako.gemstone.benchmarks

import net.kurobako.gemstone.NoNul

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

type BenchmarkName = BenchmarkName.T
object BenchmarkName extends RefinedType[String, MinLength[1] & NoNul]
