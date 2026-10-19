package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*

/** NVIDIA predicate semantics over predicates read as 1 or 0. */
private[isa] object PredicateSemantics {

  inline def test[V, R](inline algebra: Algebra[V])(
      inline sinks: NvidiaSinks[V, R],
      destination: Predicate,
      source: Value
  ): R =
    sinks.test(destination, source)(value => algebra.notEqual(value, algebra.literal(0L)))

  inline def writeFlag[V, R](inline algebra: Algebra[V])(
      inline sinks: NvidiaSinks[V, R],
      flag: VMConditionCodes.Flag,
      source: Value
  ): R = sinks.writeFlag(flag, source)(value => algebra.notEqual(value, algebra.literal(0L)))

  /** A condition flag read as 1 or 0, unchanged. */
  inline def readFlag[V, R](inline sinks: NvidiaSinks[V, R], destination: Register, flag: VMConditionCodes.Flag): R =
    sinks.readFlag(destination, flag)(value => value)

  inline def testFlag[V, R](inline sinks: NvidiaSinks[V, R], destination: Predicate, flag: VMConditionCodes.Flag): R =
    sinks.testFlag(destination, flag)(value => value)

  inline def not[V, R](inline algebra: Algebra[V])(
      inline sinks: NvidiaSinks[V, R],
      destination: Predicate,
      source: Predicate
  ): R =
    sinks.predicate(destination, source)(predicate => algebra.equal(predicate, algebra.literal(0L)))

  inline def binary[V, R](inline algebra: Algebra[V], operation: PredicateOperation)(
      inline sinks: NvidiaSinks[V, R],
      destination: Predicate,
      left: Predicate,
      right: Predicate
  ): R = operation match {
    case PredicateOperation.And => sinks.predicate(destination, left, right)((l, r) => algebra.and(l, r))
    case PredicateOperation.Or  => sinks.predicate(destination, left, right)((l, r) => algebra.or(l, r))
    case PredicateOperation.Xor => sinks.predicate(destination, left, right)((l, r) => algebra.xor(l, r))
  }

  /** Predicates order false before true and are never unordered. */
  inline def comparison[V, R](inline algebra: Algebra[V], comparison: Comparison)(
      inline sinks: NvidiaSinks[V, R],
      destination: Predicate,
      left: Predicate,
      right: Predicate
  ): R = comparison match {
    case Comparison.Ordered | Comparison.Unordered =>
      sinks.predicate(destination, left, right)((_, _) => algebra.literal(0L))
    case Comparison.Equal | Comparison.EqualUnordered =>
      sinks.predicate(destination, left, right)((l, r) => algebra.equal(l, r))
    case Comparison.NotEqual | Comparison.NotEqualUnordered =>
      sinks.predicate(destination, left, right)((l, r) => algebra.notEqual(l, r))
    case Comparison.LessThan | Comparison.LessThanUnordered =>
      sinks.predicate(destination, left, right)((l, r) => algebra.lessThan(l, r, signed = true))
    case Comparison.LessThanOrEqual | Comparison.LessThanOrEqualUnordered =>
      sinks.predicate(destination, left, right)((l, r) => algebra.lessOrEqual(l, r, signed = true))
    case Comparison.GreaterThan | Comparison.GreaterThanUnordered =>
      sinks.predicate(destination, left, right)((l, r) => algebra.lessThan(r, l, signed = true))
    case Comparison.GreaterThanOrEqual | Comparison.GreaterThanOrEqualUnordered =>
      sinks.predicate(destination, left, right)((l, r) => algebra.lessOrEqual(r, l, signed = true))
  }

  inline def select[V, R](
      inline algebra: Algebra[V],
      width: Int in 1 ~ 64
  )(inline sinks: NvidiaSinks[V, R], destination: Register, guard: Predicate, whenTrue: Value, whenFalse: Value): R =
    sinks.select(destination, guard, whenTrue, whenFalse)((p, t, f) => algebra.truncate(algebra.select(p, t, f), width))
}
