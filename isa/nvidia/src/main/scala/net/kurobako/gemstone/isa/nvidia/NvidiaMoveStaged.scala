package net.kurobako.gemstone.isa.nvidia

import MemorySemantics.*
import VMIR.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.vm.*

/** The staged semantics of register moves, condition flags and predicates. */
private[isa] object NvidiaMoveStaged {

  inline def moves[V, R](inline algebra: Algebra[V], instruction: Instruction)(inline sinks: NvidiaSinks[V, R])(
      inline next: Instruction => R
  ): R = instruction match {
    case Instruction.MoveVector(destinations, sources, width) =>
      IntegerSemantics.moveVector(algebra, bits(width))(sinks, destinations, sources)
    case Instruction.MoveCombine(destination, sources, partWidth) =>
      IntegerSemantics.combine(algebra, partWidth.widen[Int in 1 ~ 64])(sinks, destination, sources(0), sources(1))
    case Instruction.MoveSplit(destinations, source, partWidth) =>
      IntegerSemantics.split(algebra, partWidth.widen[Int in 1 ~ 64])(sinks, destinations(0), destinations(1), source)
    case Instruction.UniformMove(destination, source) =>
      IntegerSemantics.uniformMove(algebra)(sinks, destination, source)
    case Instruction.WriteConditionFlag(flag, source)     => PredicateSemantics.writeFlag(algebra)(sinks, flag, source)
    case Instruction.ReadConditionFlag(destination, flag) => PredicateSemantics.readFlag(sinks, destination, flag)
    case Instruction.ReadConditionPredicate(destination, flag) =>
      PredicateSemantics.testFlag(sinks, destination, flag)
    case Instruction.MBarrierPending(destination, token) =>
      IntegerSemantics.pendingArrivals(algebra)(sinks, destination, token)
    case Instruction.ClusterCancelStatus(destination, response) =>
      IntegerSemantics.cancelled(algebra)(sinks, destination, response)
    case Instruction.ClusterCancelCta(destination, low, high, axis) =>
      IntegerSemantics.cancelledCoordinate(algebra, axis)(sinks, destination, low, high)
    case _ => next(instruction)
  }

  inline def predicates[V, R](inline algebra: Algebra[V], instruction: Instruction)(inline sinks: NvidiaSinks[V, R])(
      inline next: Instruction => R
  ): R = instruction match {
    case Instruction.SetPredicate(destination, comparison, format, left, right, flushSubnormal) =>
      setPredicate(algebra, comparison, format, flushSubnormal)(sinks, destination, left, right)
    case Instruction.MovePredicate(destination, source) =>
      PredicateSemantics.test(algebra)(sinks, destination, source)
    case Instruction.PredicateUnary(destination, source) =>
      PredicateSemantics.not(algebra)(sinks, destination, source)
    case Instruction.PredicateBinary(operation, destination, left, right) =>
      PredicateSemantics.binary(algebra, operation)(sinks, destination, left, right)
    case Instruction.PredicateCompare(destination, comparison, left, right) =>
      PredicateSemantics.comparison(algebra, comparison)(sinks, destination, left, right)
    case Instruction.Select(destination, whenTrue, whenFalse, guard, width) =>
      PredicateSemantics.select(algebra, bits(width))(
        sinks = sinks,
        destination = destination,
        guard = guard,
        whenTrue = whenTrue,
        whenFalse = whenFalse
      )
    case _ => next(instruction)
  }

  inline def setPredicate[V, R](
      inline algebra: Algebra[V],
      comparison: Comparison,
      format: NumericFormat,
      flushSubnormal: Boolean
  )(inline sinks: NvidiaSinks[V, R], destination: Predicate, left: Value, right: Value): R = format match {
    case NumericFormat.Signed(_) =>
      IntegerSemantics.integerComparison(algebra, comparison, signed = true, bits(format.width), flushSubnormal)(
        sinks,
        destination,
        left,
        right
      )
    case NumericFormat.Unsigned(_) | NumericFormat.Bits(_) =>
      IntegerSemantics.integerComparison(algebra, comparison, signed = false, bits(format.width), flushSubnormal)(
        sinks,
        destination,
        left,
        right
      )
    case NumericFormat.IEEE(width) =>
      FloatSemantics.comparison(algebra, comparison, width, bfloat16 = false, flushSubnormal)(
        sinks,
        destination,
        left,
        right
      )
    case NumericFormat.BF16 =>
      FloatSemantics.comparison(algebra, comparison, 16, bfloat16 = true, flushSubnormal)(
        sinks,
        destination,
        left,
        right
      )
  }
}
