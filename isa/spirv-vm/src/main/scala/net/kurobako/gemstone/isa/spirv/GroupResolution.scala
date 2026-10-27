package net.kurobako.gemstone.isa.spirv

import VMCollectives.Kind as CollectiveKind
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*

/** What each invocation receives once the invocations of a scope have reached a group operation, as one body over a
  * [[GroupUnit]]. The resolver runs it on the invocations that arrived and the explorer renders it, so a result is
  * defined once. A body computes one component of the result, and an operand a view of the values of that component.
  */
private object GroupResolution {

  /** How a collective of `kind` combines the values it covers; a vote combines its predicates. */
  def combining(kind: CollectiveKind): Combine = kind match {
    case CollectiveKind.Any              => Combine.LogicalOr
    case CollectiveKind.All              => Combine.LogicalAnd
    case CollectiveKind.IntegerAdd       => Combine.IntegerAdd
    case CollectiveKind.IntegerMultiply  => Combine.IntegerMultiply
    case CollectiveKind.SignedMin        => Combine.SignedMin
    case CollectiveKind.SignedMax        => Combine.SignedMax
    case CollectiveKind.UnsignedMin      => Combine.UnsignedMin
    case CollectiveKind.UnsignedMax      => Combine.UnsignedMax
    case CollectiveKind.FloatingAdd      => Combine.FloatingAdd
    case CollectiveKind.FloatingMultiply => Combine.FloatingMultiply
    case CollectiveKind.FloatingMin      => Combine.FloatingMin
    case CollectiveKind.FloatingMax      => Combine.FloatingMax
    case CollectiveKind.BitwiseAnd       => Combine.BitwiseAnd
    case CollectiveKind.BitwiseOr        => Combine.BitwiseOr
    case CollectiveKind.BitwiseXor       => Combine.BitwiseXor
    case CollectiveKind.LogicalAnd       => Combine.LogicalAnd
    case CollectiveKind.LogicalOr        => Combine.LogicalOr
    case CollectiveKind.LogicalXor       => Combine.LogicalXor
    case other                           => throw IllegalArgumentException(s"$other combines no values")
  }

  /** The elected invocation is the first one that took part. */
  inline def elect[V, F[_]](inline algebra: Algebra[V], inline unit: GroupUnit[V, F])(target: Int): F[Unit] =
    unit.writeEach(Vec(target))((lane, _) => algebra.equal(unit.laneIndex(lane), unit.leader))(unit.done)

  /** Every invocation receives `combine` over the predicates of all the invocations that took part. */
  inline def vote[V, F[_]](inline algebra: Algebra[V], inline unit: GroupUnit[V, F])(
      combine: Combine,
      predicate: Int,
      target: Int
  ): F[Unit] =
    unit.gather(UnitAlgebra.Slot.First, predicate) { view =>
      unit.writeEach(Vec(target))((_, _) =>
        unit.fold(view, algebra.literal(0L), unit.unitSize, combine, target, bare = false)
      )(unit.done)
    }

  /** Every invocation receives 1 where the values of the invocations that took part agree, which for integers is where
    * the bitwise or and and of them are equal, and for floating-point values where IEEE equality holds between them
    * all.
    */
  inline def allEqual[V, F[_]](inline algebra: Algebra[V], inline unit: GroupUnit[V, F])(
      floating: Option[Int in (16, 32, 64)],
      source: Int,
      target: Int
  ): F[Unit] =
    unit.gather(UnitAlgebra.Slot.First, source) { view =>
      unit.writeEach(Vec(target)) { (_, _) =>
        floating match {
          case Some(width) =>
            val equal = unit.fold(view, algebra.literal(0L), unit.unitSize, Combine.FloatingEqual, source, bare = true)
            algebra.or(
              algebra.equal(unit.count, algebra.literal(1L)),
              algebra.equal(algebra.isNaN(equal, width), algebra.literal(0L))
            )
          case None =>
            algebra.equal(
              unit.fold(view, algebra.literal(0L), unit.unitSize, Combine.BitwiseOr, source, bare = false),
              unit.fold(view, algebra.literal(0L), unit.unitSize, Combine.BitwiseAnd, source, bare = false)
            )
        }
      }(unit.done)
    }

  /** Every invocation receives the value of the first one that took part. */
  inline def broadcastFirst[V, F[_]](inline unit: GroupUnit[V, F])(source: Int, target: Int): F[Unit] =
    unit.gather(UnitAlgebra.Slot.First, source) { view =>
      unit.writeEach(Vec(target))((lane, _) => unit.peer(lane, view, unit.leader))(unit.done)
    }

  /** Every invocation receives the value of the invocation that `scope` and the local id `keys` name. */
  inline def broadcast[V, F[_]](inline unit: GroupUnit[V, F])(
      scope: V,
      keys: Vec[V],
      source: Int,
      target: Int
  ): F[Unit] =
    unit.gather(UnitAlgebra.Slot.First, source) { view =>
      val index = unit.lane(scope, keys)
      unit.writeEach(Vec(target))((lane, _) => unit.peer(lane, view, index))(unit.done)
    }

  /** A reduction gives every invocation `combine` over the values of the invocations that took part; an inclusive scan
    * gives it over those up to and including the invocation, and an exclusive scan over those before it. Minimum and
    * maximum reductions of floating-point values start at the first value, which a scan starts before.
    */
  inline def reduction[V, F[_]](inline algebra: Algebra[V], inline unit: GroupUnit[V, F])(
      combine: Combine,
      operation: Long,
      source: Int,
      target: Int
  ): F[Unit] = {
    val bare = operation == 0L && (combine == Combine.FloatingMin || combine == Combine.FloatingMax)
    unit.gather(UnitAlgebra.Slot.First, source) { view =>
      unit.writeEach(Vec(target)) { (lane, _) =>
        val own = unit.laneIndex(lane)
        operation match {
          case 1L => unit.fold(view, algebra.literal(0L), algebra.add(own, algebra.literal(1L)), combine, target, bare)
          case 2L => unit.fold(view, algebra.literal(0L), own, combine, target, bare)
          case _  => unit.fold(view, algebra.literal(0L), unit.unitSize, combine, target, bare)
        }
      }(unit.done)
    }
  }

  /** A clustered reduction gives every invocation `combine` over the values of the aligned cluster of `size` it belongs
    * to.
    */
  inline def clustered[V, F[_]](inline algebra: Algebra[V], inline unit: GroupUnit[V, F])(
      combine: Combine,
      source: Int,
      size: Int,
      target: Int
  ): F[Unit] = {
    val bare = combine == Combine.FloatingMin || combine == Combine.FloatingMax
    unit.gather(UnitAlgebra.Slot.First, source) { view =>
      unit.gather(UnitAlgebra.Slot.Second, size) { sizes =>
        unit.writeEach(Vec(target)) { (lane, _) =>
          val own   = unit.laneIndex(lane)
          val width = unit.peer(lane, sizes, own)
          val first = algebra.subtract(own, algebra.remainder(own, width, signed = false))
          unit.fold(view, first, algebra.add(first, width), combine, target, bare)
        }(unit.done)
      }
    }
  }

  /** Every invocation receives the value of the invocation `selection` picks from the index it offered, or its own
    * where that invocation did not take part.
    */
  inline def shuffle[V, F[_]](inline algebra: Algebra[V], inline unit: GroupUnit[V, F])(
      selection: Collective.Selection,
      source: Int,
      lane: Int,
      target: Int
  ): F[Unit] =
    unit.gather(UnitAlgebra.Slot.First, source) { data =>
      unit.gather(UnitAlgebra.Slot.Second, lane) { lanes =>
        unit.writeEach(Vec(target)) { (own, _) =>
          val position = unit.laneIndex(own)
          val picked   = selection.source(algebra)(position, unit.peer(own, lanes, position), unit.unitSize)
          algebra.select(unit.active(own, picked), unit.peer(own, data, picked), unit.peer(own, data, position))
        }(unit.done)
      }
    }

  /** Every invocation receives the `first` or `second` operand of the invocation `delta` lanes up or down from it
    * within the launched subgroup. Where that invocation lies outside the subgroup, an upward shift reads its `first`
    * operand and a downward shift its `second`, so the operands are the previous and current values or the current and
    * next ones.
    */
  inline def shift[V, F[_]](inline algebra: Algebra[V], inline unit: GroupUnit[V, F])(
      direction: Collective.ShuffleDirection,
      first: Int,
      second: Int,
      delta: Int,
      target: Int
  ): F[Unit] =
    unit.gather(UnitAlgebra.Slot.First, first) { before =>
      unit.gather(UnitAlgebra.Slot.Second, second) { after =>
        unit.gather(UnitAlgebra.Slot.Third, delta) { distances =>
          unit.writeEach(Vec(target)) { (lane, _) =>
            val own       = unit.laneIndex(lane)
            val distance  = unit.peer(lane, distances, own)
            val width     = unit.launched
            val unwrapped = direction match {
              case Collective.ShuffleDirection.Up   => algebra.subtract(own, distance)
              case Collective.ShuffleDirection.Down => algebra.add(own, distance)
            }
            val below  = algebra.lessThan(unwrapped, algebra.literal(0L), signed = true)
            val above  = algebra.lessOrEqual(width, unwrapped, signed = true)
            val source = algebra.select(
              below,
              algebra.add(unwrapped, width),
              algebra.select(above, algebra.subtract(unwrapped, width), unwrapped)
            )
            val outside = algebra.or(below, above)
            direction match {
              case Collective.ShuffleDirection.Up =>
                algebra.select(outside, unit.peer(lane, before, source), unit.peer(lane, after, source))
              case Collective.ShuffleDirection.Down =>
                algebra.select(outside, unit.peer(lane, after, source), unit.peer(lane, before, source))
            }
          }(unit.done)
        }
      }
    }

  /** Word `word` of the ballot of the invocations that took part and offered a non-zero value in `view`: bit `i` is set
    * where the invocation at index `32 * word + i` did.
    */
  inline def ballotWord[V, F[_]](inline algebra: Algebra[V], inline unit: GroupUnit[V, F])(view: V, word: Int): V =
    unit.bits(view, algebra.literal(32L * word))

  /** Each of the `targets` receives its word of the ballot of the predicates offered. */
  inline def ballot[V, F[_]](inline algebra: Algebra[V], inline unit: GroupUnit[V, F])(
      predicate: Int,
      targets: Vec[Int]
  ): F[Unit] =
    unit.gather(UnitAlgebra.Slot.First, predicate) { view =>
      unit.writeEach(targets)((_, word) => ballotWord(algebra, unit)(view, word))(unit.done)
    }

  /** Word `word` of the partition the calling invocation `lane` is in: the invocations that took part and hold the
    * value `view` holds for it.
    */
  inline def partitionWord[V, F[_]](inline algebra: Algebra[V], inline unit: GroupUnit[V, F])(
      lane: Unit,
      view: V,
      word: Int,
      floating: Option[Int in (16, 32, 64)]
  ): V = unit.matching(view, unit.peer(lane, view, unit.laneIndex(lane)), algebra.literal(32L * word), floating)

  /** Each of the `targets` receives its word of the partition of the invocations that took part by `source`. */
  inline def partition[V, F[_]](inline algebra: Algebra[V], inline unit: GroupUnit[V, F])(
      floating: Option[Int in (16, 32, 64)],
      source: Int,
      targets: Vec[Int]
  ): F[Unit] =
    unit.gather(UnitAlgebra.Slot.First, source) { view =>
      unit.writeEach(targets)((lane, word) => partitionWord(algebra, unit)(lane, view, word, floating))(unit.done)
    }

  /** The bits of `value`, the word `word` of a ballot, of the invocations before the index `until`. */
  private inline def below[V](inline algebra: Algebra[V])(until: V, word: Int, value: V): V =
    algebra.and(
      value,
      algebra.subtract(
        algebra.shiftLeft(
          algebra.literal(1L),
          algebra.minimum(
            algebra.maximum(algebra.subtract(until, algebra.literal(32L * word)), algebra.literal(0L), signed = true),
            algebra.literal(32L),
            signed = true
          )
        ),
        algebra.literal(1L)
      )
    )

  private inline def testBit[V](inline algebra: Algebra[V])(value: V, bit: V, word: Int): V = {
    val within = algebra.subtract(bit, algebra.literal(32L * word))
    algebra.select(
      algebra.lessThan(within, algebra.literal(32L), signed = false),
      algebra.and(algebra.shiftRightLogical(value, within), algebra.literal(1L)),
      algebra.literal(0L)
    )
  }

  private inline def counted[V](inline algebra: Algebra[V])(until: V, first: V, second: V, third: V, fourth: V): V =
    algebra.add(
      algebra.add(
        algebra.populationCount(below(algebra)(until, 0, first)),
        algebra.populationCount(below(algebra)(until, 1, second))
      ),
      algebra.add(
        algebra.populationCount(below(algebra)(until, 2, third)),
        algebra.populationCount(below(algebra)(until, 3, fourth))
      )
    )

  private inline def lowest[V](inline algebra: Algebra[V])(word: V, offset: Long, otherwise: V): V =
    algebra.select(
      algebra.notEqual(word, algebra.literal(0L)),
      algebra.add(algebra.trailingZeros(word, 64), algebra.literal(offset)),
      otherwise
    )

  private inline def highest[V](inline algebra: Algebra[V])(word: V, offset: Long, otherwise: V): V =
    algebra.select(
      algebra.notEqual(word, algebra.literal(0L)),
      algebra.add(algebra.subtract(algebra.literal(63L), algebra.leadingZeros(word, 64)), algebra.literal(offset)),
      otherwise
    )

  /** Whether the bit `bit` of the four words of a ballot is set. */
  inline def ballotBit[V](inline algebra: Algebra[V])(first: V, second: V, third: V, fourth: V, bit: V): V =
    algebra.or(
      algebra.or(testBit(algebra)(first, bit, 0), testBit(algebra)(second, bit, 1)),
      algebra.or(testBit(algebra)(third, bit, 2), testBit(algebra)(fourth, bit, 3))
    )

  /** The number of bits set in the four words of a ballot, for the invocations before the index `until`. */
  inline def ballotCount[V](inline algebra: Algebra[V])(first: V, second: V, third: V, fourth: V, until: V): V =
    counted(algebra)(until, first, second, third, fourth)

  /** The index of the lowest bit set in the four words of a ballot among the `size` invocations, or all ones. */
  inline def ballotLowest[V](inline algebra: Algebra[V])(first: V, second: V, third: V, fourth: V, size: V): V =
    lowest(algebra)(
      below(algebra)(size, 0, first),
      0L,
      lowest(algebra)(
        below(algebra)(size, 1, second),
        32L,
        lowest(algebra)(
          below(algebra)(size, 2, third),
          64L,
          lowest(algebra)(below(algebra)(size, 3, fourth), 96L, algebra.literal(0xffffffffL))
        )
      )
    )

  /** The index of the highest bit set in the four words of a ballot among the `size` invocations, or all ones. */
  inline def ballotHighest[V](inline algebra: Algebra[V])(first: V, second: V, third: V, fourth: V, size: V): V =
    highest(algebra)(
      below(algebra)(size, 3, fourth),
      96L,
      highest(algebra)(
        below(algebra)(size, 2, third),
        64L,
        highest(algebra)(
          below(algebra)(size, 1, second),
          32L,
          highest(algebra)(below(algebra)(size, 0, first), 0L, algebra.literal(0xffffffffL))
        )
      )
    )

  /** The answer of a ballot query, from the four words of the ballot, the `bit` it tests, the index `own` of the
    * invocation and the number `size` of invocations in the subgroup: mode -1 tests the bit, 0 to 2 count the bits of
    * all the invocations, of those up to and including the invocation and of those before it, and 3 and 4 find the
    * lowest and highest bit of the subgroup.
    */
  inline def ballotQuery[V](inline algebra: Algebra[V])(
      mode: Int,
      first: V,
      second: V,
      third: V,
      fourth: V,
      bit: V,
      own: V,
      size: V
  ): V = mode match {
    case -1 => ballotBit(algebra)(first, second, third, fourth, bit)
    case 0  => ballotCount(algebra)(first, second, third, fourth, size)
    case 1  => ballotCount(algebra)(first, second, third, fourth, algebra.add(own, algebra.literal(1L)))
    case 2  => ballotCount(algebra)(first, second, third, fourth, own)
    case 3  => ballotLowest(algebra)(first, second, third, fourth, size)
    case _  => ballotHighest(algebra)(first, second, third, fourth, size)
  }
}
