package net.kurobako.gemstone.isa.spirv

import VMCollectives.Kind as CollectiveKind
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*

private object Collective {
  val ClusteredReduce = 3

  /** The scope of the KHR ballot and vote instructions, which act on the subgroup without naming it. */
  val ImpliedSubgroup = -1

  val ElectFault: NonEmpty[String] = "subgroup election requires Subgroup scope and boolean result"

  /** Which way a subgroup shift reads from. */
  enum ShuffleDirection derives CanEqual {
    case Up, Down

    def label: String = this match {
      case Up   => "up"
      case Down => "down"
    }
  }

  /** The bits of ballot word `word`, whose low 32 bits hold lanes `32 * word` up, for lanes `from` until `until`. */
  def ballotLanes(from: Int, until: Int, word: Int): Long = {
    def below(lanes: Int): Long = (1L << math.min(math.max(lanes - word * 32, 0), 32)) - 1L
    below(until) & ~below(from)
  }

  /** The operand types a reduction accepts. */
  enum Shape derives CanEqual {
    case Integer, Boolean, Floating

    def described: String = this match {
      case Integer  => "integer"
      case Boolean  => "boolean"
      case Floating => "floating"
    }
  }

  object Selection {

    /** The selection whose `ordinal` and `size` a request carries. */
    def of(ordinal: Int, size: Vec[Long]): Selection = ordinal match {
      case 0 => Absolute
      case 1 => Fixed
      case 2 => Xor
      case 3 => Up
      case 4 => Down
      case 5 => Rotate(size.headOption)
      case 6 => QuadBroadcast
      case _ => QuadSwap
    }
  }

  /** The invocation of a subgroup a shuffle reads, from the own index, the operand and the subgroup size. */
  enum Selection derives CanEqual {
    case Absolute
    case Fixed
    case Xor
    case Up
    case Down

    /** Rotation within aligned clusters of `size`, or within the whole subgroup. */
    case Rotate(size: Option[Long])
    case QuadBroadcast

    /** Directions 0, 1 and 2 swap horizontally, vertically and diagonally within the quad. */
    case QuadSwap

    /** The size of the clusters of a rotation, which the request carries beside the case. */
    def clusterSizes: Vec[Long] = this match {
      case Rotate(Some(n)) => Vec(n)
      case _               => Vec.empty
    }

    /** The index of the invocation this selection picks for the invocation at `local`, from the `index` operand it
      * offered and the `subgroup` size.
      */
    inline def source[V](inline algebra: Algebra[V])(local: V, index: V, subgroup: V): V = this match {
      case Absolute | Fixed => index
      case Xor              => algebra.xor(local, index)
      case Up               => algebra.subtract(local, index)
      case Down             => algebra.add(local, index)
      case Rotate(Some(n))  =>
        val width  = algebra.literal(n)
        val within = algebra.remainder(local, width, signed = false)
        algebra.add(
          algebra.subtract(local, within),
          algebra.remainder(algebra.add(within, index), width, signed = false)
        )
      case Rotate(None)  => algebra.remainder(algebra.add(local, index), subgroup, signed = false)
      case QuadBroadcast => algebra.or(algebra.and(local, algebra.literal(~3L)), index)
      case QuadSwap      => algebra.xor(local, algebra.add(index, algebra.literal(1L)))
    }
  }

}

/** A collective instruction at its operands: what its invocation offers to the invocations of the scope that reach the
  * same instruction, and the checks that precede the offer. The flags are what the loader decides from the types of the
  * operands. A scope of -1 is the subgroup the instruction implies.
  */
private enum Collective derives CanEqual {

  def ids: ResultIds

  /** The first active invocation of the subgroup receives true and the others false. */
  case Elect(ids: ResultIds, scope: Int)

  /** Whether any or all of the active invocations of the scope hold `predicate`; `subgroup` requires Subgroup scope. */
  case Vote(
      opcode: String,
      kind: CollectiveKind,
      ids: ResultIds,
      scope: Int,
      predicate: Int,
      subgroup: Boolean,
      valid: Boolean
  )

  /** A reduction or scan of `source` over the active invocations of the scope, `cluster` apart for a clustered one. */
  case Reduction(
      opcode: String,
      kind: CollectiveKind,
      shape: Collective.Shape,
      ids: ResultIds,
      scope: Int,
      operation: Long,
      source: Int,
      cluster: Option[Int],
      subgroup: Boolean,
      matching: Boolean
  )

  /** A value of the active invocations of the subgroup: whether they agree, the first one, or a reduction. */
  case Value(
      kind: CollectiveKind,
      ids: ResultIds,
      scope: Int,
      source: Int,
      operation: Int,
      cluster: Option[Int],
      compatible: Boolean
  )

  /** The `source` of the invocation `selection` picks from the `lane` operand. */
  case Shuffle(
      ids: ResultIds,
      scope: Int,
      source: Int,
      lane: Int,
      selection: Collective.Selection,
      validLane: Boolean
  )

  /** The `source` of the invocation of the workgroup or subgroup whose local id `local` holds. */
  case Broadcast(ids: ResultIds, scope: Int, source: Int, local: Int)

  /** The ballot of the active invocations of the subgroup whose `predicate` holds. */
  case Ballot(ids: ResultIds, scope: Int, predicate: Int, valid: Boolean)

  /** The ballot of the active invocations of the subgroup that hold the same `source`. */
  case Partition(ids: ResultIds, source: Int)

  /** The product of the subgroup's matrices, from the lanes of each invocation's `operands` (A, B and C), whose shape
    * `selector` names.
    */
  case MatrixMultiply(ids: ResultIds, operands: Vec[Int], selector: Vec[Long])

  /** The matrix whose lines the subgroup's invocations offer as the packed `source`, whose shape `selector` names: the
    * rows, columns and use, then the bits of a component and of an element.
    */
  case MatrixRows(ids: ResultIds, source: Int, selector: Vec[Long])

  /** A bit, count or position read from the ballot in `source`: `mode` -1 tests the `bit`, 0 to 2 count and 3 and 4
    * search.
    */
  case BallotQuery(
      ids: ResultIds,
      scope: Int,
      source: Int,
      mode: Int,
      bit: Option[Int],
      valid: Boolean
  )

  /** The `data` of the invocation `invocation` names, or the one at the exclusive or of it and the own index. */
  case IntelShuffle(ids: ResultIds, data: Int, invocation: Int, xor: Boolean)

  /** `delta` invocations up or down a window of `first` followed by `second`. */
  case IntelShift(
      opcode: String,
      ids: ResultIds,
      first: Int,
      second: Int,
      delta: Int,
      direction: Collective.ShuffleDirection
  )
}
