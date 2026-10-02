package net.kurobako.gemstone.codestyle

import scala.collection.mutable

private object DependencyGraph {
  // Iterative Kosaraju traversal avoids overflowing on long dependency chains.
  def components(graph: Map[String, Set[String]]): Vector[Vector[String]] = {
    val visited  = mutable.Set.empty[String]
    val finished = mutable.ArrayBuffer.empty[String]
    for (start <- graph.keys.toVector.sorted if !visited(start)) {
      val stack = mutable.Stack((start, false))
      while (stack.nonEmpty) {
        val (node, returning) = stack.pop()
        if (returning) finished += node
        else if (visited.add(node)) {
          stack.push((node, true))
          graph(node).toVector.sorted.reverse.foreach(child => stack.push((child, false)))
        }
      }
    }
    val reverse = mutable.Map.from(graph.keys.map(_ -> mutable.Set.empty[String]))
    for {
      (from, targets) <- graph
      to              <- targets
    } reverse(to) += from
    visited.clear()
    val result = Vector.newBuilder[Vector[String]]
    for (start <- finished.reverse if !visited(start)) {
      val members = Vector.newBuilder[String]
      val stack   = mutable.Stack(start)
      while (stack.nonEmpty) {
        val node = stack.pop()
        if (visited.add(node)) {
          members += node
          reverse(node).foreach(stack.push)
        }
      }
      result += members.result().sorted
    }
    result.result().sortBy(_.head)
  }

  def reachability(graph: Map[String, Set[String]]): Map[String, Int] = {
    val groups     = components(graph)
    val owner      = groups.zipWithIndex.flatMap((members, index) => members.map(_ -> index)).toMap
    val successors = groups.indices.map { index =>
      groups(index).flatMap(graph).map(owner).filter(_ != index).toSet
    }.toVector
    val remaining    = successors.map(_.size).toArray
    val predecessors = Array.fill(groups.size)(mutable.ArrayBuffer.empty[Int])
    for {
      from <- successors.indices
      to   <- successors(from)
    } predecessors(to) += from
    val reachable = Array.fill(groups.size)(mutable.BitSet.empty)
    val ready     = mutable.Queue.from(groups.indices.filter(remaining(_) == 0))
    while (ready.nonEmpty) {
      val index = ready.dequeue()
      for (parent <- predecessors(index)) {
        reachable(parent) ++= reachable(index)
        reachable(parent) += index
        remaining(parent) -= 1
        if (remaining(parent) == 0) ready.enqueue(parent)
      }
    }
    groups.indices.flatMap { index =>
      val count = groups(index).size - 1 + reachable(index).iterator.map(groups(_).size).sum
      groups(index).map(_ -> count)
    }.toMap
  }
}
