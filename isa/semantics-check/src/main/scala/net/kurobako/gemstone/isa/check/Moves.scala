package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.flow.Liveness
import net.kurobako.gemstone.flow.Window
import net.kurobako.gemstone.vm.RegisterUse
import scala.util.Random

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
    val mapped = values.map(f)
    Option.when(mapped.forall(_.isDefined))(mapped.flatten)
  }

  def unitsOf(use: RegisterUse, names: Vec[String]): Set[String] =
    names.flatMap(name => Liveness.units(name, use.unitsOf(name))).toSet

  def defined(window: Window, uses: Vec[RegisterUse]): Vec[Set[String]] =
    uses.scanLeft(window.liveIn)((live, use) => live ++ unitsOf(use, use.writes))

  def sound(window: Window, exec: Set[String], written: Set[String], used: Vec[RegisterUse]): Boolean =
    used.foldLeft(Option(window.liveIn)) { (live, use) =>
      val writes = unitsOf(use, use.writes)
      live.filter(live =>
        unitsOf(use, use.reads).subsetOf(live) && writes.intersect(exec).isEmpty &&
          writes.intersect(window.liveOut).subsetOf(written)
      ).map(_ ++ writes)
    }.nonEmpty
}
