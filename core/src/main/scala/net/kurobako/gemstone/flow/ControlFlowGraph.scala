package net.kurobako.gemstone.flow

import net.kurobako.gemstone.collection.*
import scala.annotation.tailrec

object ControlFlowGraph {

  /** `None` successors leave the block open. Indices at or beyond the last instruction leave the graph. */
  def apply(successors: Vec[Option[Vec[Int]]], entry: Int): ControlFlowGraph = {
    val count            = successors.size
    val firsts: Vec[Int] =
      (Vec(0, entry) ++ (0 until count).flatMap { index =>
        val targets = successors(index)
        if (targets.contains(Vec(index + 1))) Vec.empty[Int]
        else Vec(index + 1) ++ targets.getOrElse(Vec.empty[Int])
      }).filter(_ < count).distinct.sorted
    val blocks: Vec[Block] = firsts.zipWithIndex.map { case (first, k) =>
      val last = (if (k + 1 < firsts.size) firsts(k + 1) else count) - 1
      successors(last) match {
        case None          => Block(first, last, Vec.empty[Int], open = true)
        case Some(targets) =>
          Block(first, last, targets.filter(_ < count).map(t => firsts.lastIndexWhere(_ <= t)).distinct, open = false)
      }
    }
    new ControlFlowGraph(blocks, firsts.lastIndexWhere(_ <= entry))
  }
}

class ControlFlowGraph private (val blocks: Vec[Block], entry: Int) {
  private val predecessors: Map[Int, Seq[Int]] =
    blocks.indices.flatMap(u => blocks(u).successors.map(v => (v, u))).groupMap(_._1)(_._2)

  private val rpo: List[Int] = {
    @tailrec
    def depthFirst(stack: List[(Int, List[Int])], visited: Set[Int], finished: List[Int]): List[Int] = stack match {
      case Nil                                                       => finished
      case (block, Nil) :: rest                                      => depthFirst(rest, visited, block :: finished)
      case (block, next :: others) :: rest if visited.contains(next) =>
        depthFirst((block, others) :: rest, visited, finished)
      case (block, next :: others) :: rest =>
        depthFirst((next, blocks(next).successors.toList) :: (block, others) :: rest, visited + next, finished)
    }
    if (blocks.isEmpty) Nil else depthFirst(List((entry, blocks(entry).successors.toList)), Set(entry), Nil)
  }

  private val number: Map[Int, Int] = rpo.zipWithIndex.toMap

  private val idoms: Map[Int, Int] = {
    @tailrec
    def intersect(state: Map[Int, Int], a: Int, b: Int): Int =
      if (a == b) a
      else if (number(a) > number(b)) intersect(state, state(a), b)
      else intersect(state, a, state(b))
    @tailrec
    def solve(current: Map[Int, Int]): Map[Int, Int] = {
      val next = rpo.drop(1).foldLeft(current) { (state, block) =>
        val candidate = predecessors.getOrElse(block, Nil).filter(state.contains).reduceLeft(intersect(state, _, _))
        if (state.get(block).contains(candidate)) state else state.updated(block, candidate)
      }
      if (next == current) current else solve(next)
    }
    solve(Map(entry -> entry)).removed(entry)
  }

  private def body(header: Int, latches: Vec[Int]): Vec[Int] = {
    @tailrec
    def walk(work: List[Int], seen: Set[Int]): Set[Int] = work match {
      case Nil           => seen
      case block :: rest =>
        val fresh = predecessors.getOrElse(block, Nil).filter(p => reachable(p) && !seen.contains(p)).toList
        walk(fresh ++ rest, seen ++ fresh)
    }
    Vec(walk(latches.filter(_ != header).toList, latches.toSet + header).toSeq.sorted*)
  }

  val loops: Vec[Loop] = {
    val latches = rpo.flatMap(u => blocks(u).successors.filter(dominates(_, u)).map(v => (v, u))).groupMap(_._1)(_._2)
    Vec(latches.toList.sortBy(_._1).map { case (header, sources) =>
      val ordered = Vec(sources.distinct.sorted*)
      Loop(header, body(header, ordered), ordered)
    }*)
  }

  val reducible: Boolean =
    rpo.forall(u => blocks(u).successors.forall(v => number(v) > number(u) || dominates(v, u)))

  def blockOf(instruction: Int): Int = blocks.lastIndexWhere(_.first <= instruction)

  def reachable(block: Int): Boolean = number.contains(block)

  def immediateDominator(block: Int): Option[Int] = idoms.get(block)

  def dominates(dominator: Int, block: Int): Boolean = {
    @tailrec
    def climb(current: Int): Boolean =
      if (current == dominator) true
      else idoms.get(current) match {
        case Some(parent) => climb(parent)
        case None         => false
      }
    reachable(block) && climb(block)
  }
}
