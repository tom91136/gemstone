package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.flow.Liveness
import net.kurobako.gemstone.flow.Window
import net.kurobako.gemstone.vm.RegisterUse
import scala.reflect.ClassTag
import scala.util.Random

// move suites inspect the candidates this object builds
@SuppressWarnings(Array("bonsai:test-only-member"))
private[check] object Moves {
  enum Kind derives CanEqual {
    case Opcode, Swap, Delete, Insert, Immediate
  }

  case class Candidate[I](kind: Kind, instructions: Vec[I], uses: Vec[RegisterUse])

  trait Proposer[I] {
    def candidates(kind: Kind, random: Random, limit: Int): Vec[Candidate[I]]
  }

  def propose[I](seed: Long, limit: Int)(proposer: Proposer[I]): Vec[Candidate[I]] = {
    val random = Random(seed)
    Kind.values.toVec.flatMap(proposer.candidates(_, random, limit))
  }

  def each[A, B](values: Vec[A])(f: A => Option[B]): Option[Vec[B]] = {
    val mapped = values.iterator.map(f).takeWhile(_.isDefined).toVec
    Option.when(mapped.size == values.size)(mapped.flatten)
  }

  def unitsOf(use: RegisterUse, names: Vec[String]): Set[String] =
    names.flatMap(name => Liveness.units(name, use.unitsOf(name))).toSet

  def defined(window: Window, uses: Vec[RegisterUse]): Vec[Set[String]] =
    uses.scanLeft(window.liveIn)((live, use) => live ++ unitsOf(use, use.writes))

  /** How an instruction reaches memory: what tells it from every other instruction, and whether it loads. */
  case class Access[K](identity: K, loads: Boolean)

  /** The instructions of `sequence` that load or store memory, each as what identifies it. */
  def accesses[I, K: ClassTag](sequence: Vec[I])(access: I => Option[Access[K]]): Vec[K] =
    sequence.flatMap(instruction => access(instruction).map(_.identity).toVec)

  /** Whether `sequence` keeps the window's loads and stores, `pinned`, as they are and in their order, so that no move
    * changes, deletes, reorders or creates one and the other moves work around them. Whether two of them may change
    * places is for a proof, which tells apart addresses that may alias. Where a load's result `waits` on an instruction
    * that no window holds, `sequence` also reads no register a load before it wrote.
    */
  def ordered[I, K: ClassTag](pinned: Vec[K], sequence: Vec[I], used: Vec[RegisterUse], waits: Boolean)(
      access: I => Option[Access[K]]
  )(using CanEqual[K, K]): Boolean = {
    val kept = accesses(sequence)(access)
    kept.size == pinned.size && kept.zip(pinned).forall(_ == _) &&
    (!waits || sequence.zip(used).foldLeft(Option(Set.empty[String])) { case (pending, (instruction, use)) =>
      val writes = unitsOf(use, use.writes)
      pending.filter(_.intersect(unitsOf(use, use.reads)).isEmpty).map(waiting =>
        if (access(instruction).exists(_.loads)) waiting ++ writes else waiting -- writes
      )
    }.nonEmpty)
  }

  def sound(window: Window, exec: Set[String], written: Set[String], used: Vec[RegisterUse]): Boolean =
    used.foldLeft(Option(window.liveIn)) { (live, use) =>
      val writes = unitsOf(use, use.writes)
      live.filter(live =>
        unitsOf(use, use.reads).subsetOf(live) && writes.intersect(exec).isEmpty &&
          writes.intersect(window.liveOut).subsetOf(written)
      ).map(_ ++ writes)
    }.nonEmpty
}
