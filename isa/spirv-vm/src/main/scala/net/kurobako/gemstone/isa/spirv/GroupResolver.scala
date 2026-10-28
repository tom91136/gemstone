package net.kurobako.gemstone.isa.spirv

import VMInvocation.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*

@SuppressWarnings(Array("scalafix:DisableSyntax.var"))
private object GroupResolver {

  /** Resolution computes every result in place. */
  type Resolved[A] = A

  /** The `count` values `value` gives. */
  inline def column(count: Int)(inline value: Int => Long): Array[Long] = {
    val found = Array.ofDim[Long](count)
    var at    = 0
    while (at < count) {
      found(at) = value(at)
      at += 1
    }
    found
  }

  /** The index in the scope of each of the invocations, which is its place among them unless they share a subgroup. */
  def indices(participants: Vec[InvocationState], subgroup: Boolean): Array[Int] = {
    val found = Array.ofDim[Int](participants.size)
    var at    = 0
    while (at < found.length) {
      found(at) = if (subgroup) participants(at).invocation.subgroupLocal else at
      at += 1
    }
    found
  }

  /** The largest of the indices, or `least` if that is larger. */
  def extent(indices: Array[Int], least: Int): Int = {
    var found = least
    var at    = 0
    while (at < indices.length) {
      found = math.max(found, indices(at) + 1)
      at += 1
    }
    found
  }
}

/** The execute instance of [[GroupUnit]]: the lanes are the `positions.length` invocations that took part, at the index
  * each holds in the scope within a unit of `size`. A view is the number of an operand, whose value for invocation `at`
  * is `columns(operand)(at)`, and `results(at)(target)` receives what the invocation is given.
  */
@SuppressWarnings(Array("scalafix:DisableSyntax.var"))
private class GroupResolver(
    positions: Array[Int],
    size: Int,
    launch: Int,
    columns: Array[Array[Long]],
    targets: Int,
    operators: Vec[Combine.Operator],
    locals: Array[Array[Long]]
) extends GroupUnit[Long, GroupResolver.Resolved] {
  val results: Array[Array[Long]] = {
    val rows = Array.ofDim[Array[Long]](positions.length)
    var at   = 0
    while (at < rows.length) {
      rows(at) = Array.ofDim[Long](targets)
      at += 1
    }
    rows
  }

  private val holder = Array.ofDim[Int](size)
  java.util.Arrays.fill(holder, -1)
  positions.indices.foreach(at => holder(positions(at)) = at)

  private var current     = 0
  private var foldColumn  = -1
  private var foldCombine = -1
  private var foldFrom    = 0L
  private var foldUntil   = 0L
  private var foldBare    = false
  private var foldStarted = false
  private var foldTotal   = 0L

  /** Gives each invocation the result of the one before it where they are equal, as a reduction gives all the same. */
  def shareEqualResults(): Unit = {
    var at = 1
    while (at < results.length) {
      if (java.util.Arrays.equals(results(at), results(at - 1))) results(at) = results(at - 1)
      at += 1
    }
  }

  inline def done: Unit = ()

  inline def unitSize: Long = size.toLong

  inline def leader: Long = positions(0).toLong

  inline def count: Long = positions.length.toLong

  inline def launched: Long = launch.toLong

  inline def lane(scope: Long, keys: Vec[Long]): Long =
    if (scope == Scope.Subgroup.toLong && keys.length == 1) {
      if (at(keys(0)) >= 0) keys(0) else size.toLong
    } else {
      val values = keys.toArray
      var index  = 0
      while (index < positions.length && !matches(index, values)) index += 1
      if (index < positions.length) positions(index).toLong else size.toLong
    }

  inline def laneIndex(lane: Unit): Long = positions(current).toLong

  inline def active(lane: Unit, index: Long): Long = if (at(index) >= 0) 1L else 0L

  inline def gather[A](slot: UnitAlgebra.Slot, operand: Int)(inline next: Long => A): A = next(operand.toLong)

  inline def peer(lane: Unit, view: Long, index: Long): Long = {
    val found = at(index)
    if (found >= 0) columns(view.toInt)(found) else 0L
  }

  inline def writeEach[A](targets: Vec[Int])(inline value: (Unit, Int) => Long)(inline next: => A): A = {
    current = 0
    while (current < positions.length) {
      var target = 0
      while (target < targets.length) {
        results(current)(targets(target)) = value((), target)
        target += 1
      }
      current += 1
    }
    next
  }

  inline def fold(view: Long, from: Long, until: Long, combine: Combine, typed: Int, bare: Boolean): Long =
    folding(view.toInt, from, until, combine, bare)

  inline def bits(view: Long, first: Long): Long = gathered(view.toInt, first)

  inline def matching(view: Long, own: Long, first: Long, floating: Option[Int in (16, 32, 64)]): Long =
    matched(view.toInt, own, first, floating)

  private def same(value: Long, own: Long, floating: Option[Int in (16, 32, 64)]): Boolean =
    value == own ||
      (floating match {
        case Some(width) => IEEE754.toDouble(value, width) == IEEE754.toDouble(own, width)
        case None        => false
      })

  private def matched(operand: Int, own: Long, first: Long, floating: Option[Int in (16, 32, 64)]): Long = {
    val values = columns(operand)
    var found  = 0L
    var lane   = 0
    while (lane < 32) {
      val at = this.at(first + lane)
      if (at >= 0 && same(values(at), own, floating)) found |= 1L << lane
      lane += 1
    }
    found
  }

  private def gathered(operand: Int, first: Long): Long = {
    val values = columns(operand)
    var found  = 0L
    var lane   = 0
    while (lane < 32) {
      val at = this.at(first + lane)
      if (at >= 0 && values(at) != 0L) found |= 1L << lane
      lane += 1
    }
    found
  }

  private def matches(position: Int, keys: Array[Long]): Boolean = keys.length match {
    case 1 => locals(0)(position) == keys(0)
    case 2 => locals(0)(position) == keys(0) && locals(1)(position) == keys(1) && locals(2)(position) == 0L
    case 3 => locals(0)(position) == keys(0) && locals(1)(position) == keys(1) && locals(2)(position) == keys(2)
    case _ => false
  }

  private def at(index: Long): Int =
    if (java.lang.Long.compareUnsigned(index, size.toLong) < 0) holder(index.toInt) else -1

  private def folding(operand: Int, first: Long, last: Long, kind: Combine, bare: Boolean): Long = {
    val limit  = if (java.lang.Long.compareUnsigned(last, size.toLong) < 0) last else size.toLong
    val values = columns(operand)
    var slot   = 0
    while (slot < operators.length && operators(slot).kind != kind) slot += 1
    if (slot == operators.length) throw IllegalStateException(s"no operation to fold $kind")
    val combine = operators(slot)
    val resume  = operand == foldColumn && kind.ordinal == foldCombine && first == foldFrom && bare == foldBare &&
      limit >= foldUntil
    var index = if (resume) foldUntil else first
    var total = if (resume) foldTotal else combine.identity
    var begun = if (resume) foldStarted else !bare
    while (index < limit) {
      val found = holder(index.toInt)
      if (found >= 0) { total = if (begun) combine(total, values(found)) else values(found); begun = true }
      index += 1L
    }
    foldColumn = operand
    foldCombine = kind.ordinal
    foldFrom = first
    foldUntil = math.max(limit, first)
    foldBare = bare
    foldStarted = begun
    foldTotal = total
    total
  }
}
