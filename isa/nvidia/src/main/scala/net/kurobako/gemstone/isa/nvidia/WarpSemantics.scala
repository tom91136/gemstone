package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*

/** NVIDIA warp-level semantics over the values lanes exchange: the sinks gather the participating lanes and move values
  * between them.
  */
private[isa] object WarpSemantics {

  /** `shfl.sync`: the lane each lane reads in its warp, itself when the exchange is invalid. The bounds operand holds
    * the clamp in its low five bits and, for the index and butterfly modes, the segment mask in bits 8 to 12.
    */
  inline def shuffle[V, R](inline algebra: Algebra[V], mode: ShuffleMode, width: Int in 1 ~ 64)(
      inline sinks: NvidiaSinks[V, R],
      destination: Register,
      validity: Option[Predicate],
      operand: Value,
      selector: Value,
      bounds: Value
  ): R = mode match {
    case ShuffleMode.Down => sinks.shuffle(destination, validity, operand, selector, bounds)(
        (lane, select, bound, size) =>
          algebra.let("t", algebra.add(lane, algebra.distance(select)))(target =>
            algebra.select(algebra.downValid(target, bound, size), target, lane)
          ),
        (lane, select, bound, size) =>
          algebra.let("t", algebra.add(lane, algebra.distance(select)))(algebra.downValid(_, bound, size)),
        value => algebra.truncate(value, width)
      )
    case ShuffleMode.Up => sinks.shuffle(destination, validity, operand, selector, bounds)(
        (lane, select, bound, _) =>
          algebra.let("offset", algebra.distance(select))(offset =>
            algebra.let("t", algebra.subtract(lane, offset))(target =>
              algebra.select(algebra.upValid(lane, offset, target, bound), target, lane)
            )
          ),
        (lane, select, bound, _) =>
          algebra.let("offset", algebra.distance(select))(offset =>
            algebra.let("t", algebra.subtract(lane, offset))(algebra.upValid(lane, offset, _, bound))
          ),
        value => algebra.truncate(value, width)
      )
    case ShuffleMode.Index => sinks.shuffle(destination, validity, operand, selector, bounds)(
        (lane, select, bound, size) =>
          algebra.segmented(lane, bound)((base, other) =>
            algebra.let("t", algebra.or(base, algebra.and(algebra.distance(select), other)))(target =>
              algebra.select(algebra.indexValid(base, other, target, bound, size), target, lane)
            )
          ),
        (lane, select, bound, size) =>
          algebra.segmented(lane, bound)((base, other) =>
            algebra.let("t", algebra.or(base, algebra.and(algebra.distance(select), other)))(
              algebra.indexValid(base, other, _, bound, size)
            )
          ),
        value => algebra.truncate(value, width)
      )
    case ShuffleMode.Butterfly => sinks.shuffle(destination, validity, operand, selector, bounds)(
        (lane, select, bound, size) =>
          algebra.segmented(lane, bound)((base, other) =>
            algebra.let("t", algebra.xor(lane, algebra.distance(select)))(target =>
              algebra.select(
                algebra.and(
                  algebra.lessOrEqual(target, algebra.segmentMaximum(base, other, bound), signed = true),
                  algebra.lessThan(target, size, signed = true)
                ),
                target,
                lane
              )
            )
          ),
        (lane, select, bound, _) =>
          algebra.segmented(lane, bound)((base, other) =>
            algebra.lessOrEqual(
              algebra.xor(lane, algebra.distance(select)),
              algebra.segmentMaximum(base, other, bound),
              signed = true
            )
          ),
        value => algebra.truncate(value, width)
      )
  }

  extension [V](inline algebra: Algebra[V]) {
    inline def distance(selector: V): V = algebra.and(selector, algebra.literal(31L))

    /** The clamp of an up or down shuffle: the bounds operand's low word, at most 31. */
    inline def clamp(bounds: V): V =
      algebra.minimum(algebra.and(bounds, algebra.literal(0xffffffffL)), algebra.literal(31L), signed = true)

    inline def downValid(target: V, bounds: V, size: V): V =
      algebra.and(
        algebra.lessOrEqual(target, algebra.clamp(bounds), signed = true),
        algebra.lessThan(target, size, signed = true)
      )

    inline def upValid(lane: V, offset: V, target: V, bounds: V): V =
      algebra.and(
        algebra.lessOrEqual(offset, lane, signed = true),
        algebra.lessOrEqual(algebra.clamp(bounds), target, signed = true)
      )

    /** The lane's segment base and the mask of the bits a lane may choose within its segment. */
    inline def segmented(lane: V, bounds: V)(inline body: (V, V) => V): V =
      algebra.let("m", algebra.and(algebra.shiftRightLogical(bounds, algebra.literal(8L)), algebra.literal(31L)))(
        mask =>
          algebra.let("base", algebra.and(lane, mask))(base => algebra.let("free", algebra.not(mask))(body(base, _)))
      )

    inline def segmentMaximum(base: V, free: V, bounds: V): V =
      algebra.or(base, algebra.and(algebra.distance(bounds), free))

    inline def indexValid(base: V, free: V, target: V, bounds: V, size: V): V =
      algebra.and(
        algebra.lessOrEqual(target, algebra.segmentMaximum(base, free, bounds), signed = true),
        algebra.lessThan(target, size, signed = true)
      )
  }

  /** `vote.sync` over the participating lanes' predicates as bits of `votes`, and their mask `members`; a negated vote
    * counts the complements.
    */
  inline def vote[V, R](inline algebra: Algebra[V], operation: VoteOperation, negated: Boolean)(
      inline sinks: NvidiaSinks[V, R],
      destination: Predicate,
      voter: Predicate,
      memberMask: Value
  ): R =
    if (negated) voteOn(algebra, operation, negated = true)(sinks, destination, voter, memberMask)
    else voteOn(algebra, operation, negated = false)(sinks, destination, voter, memberMask)

  inline def voteOn[V, R](inline algebra: Algebra[V], operation: VoteOperation, inline negated: Boolean)(
      inline sinks: NvidiaSinks[V, R],
      destination: Predicate,
      voter: Predicate,
      memberMask: Value
  ): R = operation match {
    case VoteOperation.All => sinks.vote(destination, voter, memberMask)((votes, members) =>
        algebra.let("v", algebra.cast(votes, members, negated))(voted =>
          algebra.and(algebra.equal(voted, members), algebra.notEqual(members, algebra.literal(0L)))
        )
      )
    case VoteOperation.Any =>
      sinks.vote(destination, voter, memberMask)((votes, members) =>
        algebra.notEqual(algebra.cast(votes, members, negated), algebra.literal(0L))
      )
    case VoteOperation.Uniform => sinks.vote(destination, voter, memberMask)((votes, members) =>
        algebra.let("v", algebra.cast(votes, members, negated))(voted =>
          algebra.or(algebra.equal(voted, algebra.literal(0L)), algebra.equal(voted, members))
        )
      )
  }

  /** `vote.sync.ballot`: the mask of the participating lanes whose vote is set. */
  inline def ballot[V, R](
      inline algebra: Algebra[V],
      negated: Boolean
  )(inline sinks: NvidiaSinks[V, R], destination: Register, voter: Predicate, memberMask: Value): R =
    if (negated)
      sinks.ballot(destination, voter, memberMask)((votes, members) => algebra.cast(votes, members, negated = true))
    else
      sinks.ballot(destination, voter, memberMask)((votes, members) => algebra.cast(votes, members, negated = false))

  extension [V](inline algebra: Algebra[V]) {
    inline def cast(votes: V, members: V, inline negated: Boolean): V =
      if (negated) algebra.xor(votes, members) else votes
  }

  /** `elect.sync`: the lowest participating lane. */
  inline def elect[V, R](inline algebra: Algebra[V])(
      inline sinks: NvidiaSinks[V, R],
      elected: Option[Register],
      leader: Predicate,
      memberMask: Value
  ): R =
    sinks.elect(elected, leader, memberMask)(
      members => algebra.trailingZeros(members, 32),
      (lane, chosen) => algebra.equal(lane, chosen)
    )

  /** `redux.sync` of binary32 operands: their magnitudes where `absolute`, folded by `min` or `max`, which propagate
    * NaN only where `propagateNaN`.
    */
  @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
  inline def reduceFloat[V, R](inline algebra: Algebra[V], maximum: Boolean, absolute: Boolean, propagateNaN: Boolean)(
      inline sinks: NvidiaSinks[V, R],
      destination: Register,
      source: Value,
      memberMask: Value
  ): R =
    if (absolute) reduceFloatOf(algebra, maximum, propagateNaN, absolute = true)(sinks, destination, source, memberMask)
    else reduceFloatOf(algebra, maximum, propagateNaN, absolute = false)(sinks, destination, source, memberMask)

  @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
  inline def reduceFloatOf[V, R](
      inline algebra: Algebra[V],
      maximum: Boolean,
      propagateNaN: Boolean,
      inline absolute: Boolean
  )(inline sinks: NvidiaSinks[V, R], destination: Register, source: Value, memberMask: Value): R =
    if (maximum)
      if (propagateNaN) sinks.reduceFloat(destination, source, memberMask, maximum, propagateNaN)(
        value => algebra.magnitude(value, absolute),
        (l, r) =>
          algebra.primitive(l, r)((a: Float32, b: Float32) =>
            FloatArithmetic.maximum(a, b, absolute = false, xorSign = false)
          )
      )
      else sinks.reduceFloat(destination, source, memberMask, maximum, propagateNaN)(
        value => algebra.magnitude(value, absolute),
        (l, r) =>
          algebra.primitive(l, r)((a: Float32, b: Float32) =>
            FloatArithmetic.maximumNumber(a, b, absolute = false, xorSign = false)
          )
      )
    else if (propagateNaN) sinks.reduceFloat(destination, source, memberMask, maximum, propagateNaN)(
      value => algebra.magnitude(value, absolute),
      (l, r) =>
        algebra.primitive(l, r)((a: Float32, b: Float32) =>
          FloatArithmetic.minimum(a, b, absolute = false, xorSign = false)
        )
    )
    else sinks.reduceFloat(destination, source, memberMask, maximum, propagateNaN)(
      value => algebra.magnitude(value, absolute),
      (l, r) =>
        algebra.primitive(l, r)((a: Float32, b: Float32) =>
          FloatArithmetic.minimumNumber(a, b, absolute = false, xorSign = false)
        )
    )

  extension [V](inline algebra: Algebra[V]) {
    inline def magnitude(value: V, inline absolute: Boolean): V =
      if (absolute) algebra.and(value, algebra.literal(0x7fffffffL)) else value
  }

  /** `match.sync`; the predicate of `.all` holds where the lanes matched. */
  inline def matching[V, R](inline algebra: Algebra[V], width: Int in 1 ~ 64, all: Boolean)(
      inline sinks: NvidiaSinks[V, R],
      found: Option[Register],
      predicate: Option[Predicate],
      source: Value,
      memberMask: Value
  ): R = sinks.matching(found, predicate, source, memberMask, width, all)(matched =>
    algebra.notEqual(matched, algebra.literal(0L))
  )

  /** `redux.sync` of the participating lanes' operands at `width` bits; `otherwise` for an operation it does not
    * support.
    */
  inline def reduce[V, R](inline algebra: Algebra[V], operation: IntegerOperation, width: Int in 1 ~ 64)(
      inline sinks: NvidiaSinks[V, R],
      destination: Register,
      source: Value,
      memberMask: Value
  )(otherwise: => R): R = operation match {
    case IntegerOperation.Add =>
      reduced(algebra, operation, width)(sinks, destination, source, memberMask)((l, r) =>
        algebra.truncate(algebra.add(l, r), width)
      )
    case IntegerOperation.BitwiseAnd =>
      reduced(algebra, operation, width)(sinks, destination, source, memberMask)((l, r) => algebra.and(l, r))
    case IntegerOperation.BitwiseOr =>
      reduced(algebra, operation, width)(sinks, destination, source, memberMask)((l, r) => algebra.or(l, r))
    case IntegerOperation.BitwiseXor =>
      reduced(algebra, operation, width)(sinks, destination, source, memberMask)((l, r) => algebra.xor(l, r))
    case IntegerOperation.Minimum(signed) =>
      extremumReduced(algebra, operation, width)(sinks, destination, source, memberMask, signed, maximum = false)
    case IntegerOperation.Maximum(signed) =>
      extremumReduced(algebra, operation, width)(sinks, destination, source, memberMask, signed, maximum = true)
    case _ => otherwise
  }

  private inline def reduced[V, R](inline algebra: Algebra[V], operation: IntegerOperation, width: Int in 1 ~ 64)(
      inline sinks: NvidiaSinks[V, R],
      destination: Register,
      source: Value,
      memberMask: Value
  )(inline combine: (V, V) => V): R =
    sinks.reduce(destination, source, memberMask, operation, width)(value => algebra.truncate(value, width), combine)

  private inline def extremumReduced[V, R](
      inline algebra: Algebra[V],
      operation: IntegerOperation,
      width: Int in 1 ~ 64
  )(
      inline sinks: NvidiaSinks[V, R],
      destination: Register,
      source: Value,
      memberMask: Value,
      signed: Boolean,
      inline maximum: Boolean
  ): R =
    if (signed) extremumAt(algebra, operation, width)(sinks, destination, source, memberMask, signed = true, maximum)
    else extremumAt(algebra, operation, width)(sinks, destination, source, memberMask, signed = false, maximum)

  private inline def extremumAt[V, R](
      inline algebra: Algebra[V],
      operation: IntegerOperation,
      width: Int in 1 ~ 64
  )(
      inline sinks: NvidiaSinks[V, R],
      destination: Register,
      source: Value,
      memberMask: Value,
      inline signed: Boolean,
      inline maximum: Boolean
  ): R = reduced(algebra, operation, width)(sinks, destination, source, memberMask)((l, r) =>
    algebra.extremum(l, r, width, signed, maximum)
  )
}
