package net.kurobako.gemstone.isa.nvidia

import MemorySemantics.*
import VMIR.*
import net.kurobako.gemstone.semantics.*

/** The staged semantics of warp-wide data exchange and voting. */
private[isa] object NvidiaWarpStaged {

  inline def warp[V, R](inline algebra: Algebra[V], instruction: Instruction)(inline sinks: NvidiaSinks[V, R])(
      otherwise: => R,
      inline next: Instruction => R
  ): R = instruction match {
    case Instruction.Shuffle(mode, destination, validity, source, lane, clamp, _, width) =>
      WarpSemantics.shuffle(algebra, mode, bits(width))(
        sinks = sinks,
        destination = destination,
        validity = validity,
        operand = source,
        selector = lane,
        bounds = clamp
      )
    case Instruction.VoteBallot(destination, voter, memberMask, negated) =>
      WarpSemantics.ballot(algebra, negated)(sinks, destination, voter, memberMask)
    case Instruction.VotePredicate(destination, voter, memberMask, operation, negated) =>
      WarpSemantics.vote(algebra, operation, negated)(sinks, destination, voter, memberMask)
    case Instruction.Elect(destination, leader, memberMask) =>
      WarpSemantics.elect(algebra)(sinks, destination, leader, memberMask)
    case Instruction.WarpReduceFloat(destination, source, memberMask, maximum, absolute, propagateNaN) =>
      WarpSemantics.reduceFloat(algebra, maximum, absolute, propagateNaN)(sinks, destination, source, memberMask)
    case Instruction.WarpMatch(destination, predicate, source, memberMask, width, all) =>
      WarpSemantics.matching(algebra, bits(width), all)(sinks, destination, predicate, source, memberMask)
    case Instruction.WarpReduce(destination, source, memberMask, operation, width) =>
      WarpSemantics.reduce(algebra, operation, bits(width))(sinks, destination, source, memberMask)(otherwise)
    case _ => next(instruction)
  }
}
