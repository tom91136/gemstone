package net.kurobako.gemstone.isa.ptx

import Program.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import scala.collection.mutable

/** Where a routine's divergent branches reconverge, as hardware does with the barriers `ptxas` places. A branch that
  * splits a warp gets a barrier set up before it and synchronised at its immediate post-dominator, with the setup
  * hoisted out of every loop that contains the branch but not the join. A loop that waits on memory yields at its top
  * so the lanes that wait stop being waited for.
  */
private object Reconvergence {
  type Label = NonEmpty[String]

  /** What to emit ahead of a statement, in order. Barriers that are never live together share a token slot, numbered
    * per routine.
    */
  enum Action derives CanEqual {
    case Label(name: NonEmpty[String])
    case Setup(slot: Int)
    case Synchronise(slot: Int)
    case Yield
    case Jump(target: NonEmpty[String])
  }

  object Schedule {
    val empty: Schedule = Schedule(Map.empty, Map.empty)
  }

  /** Actions ahead of the statement at an index, and the branch targets to replace in the statement at an index. */
  case class Schedule(before: Map[Int, Vec[Action]], retargets: Map[Int, Map[Label, Label]])

  private val Strong: Set[Modifier]  = Set(Modifier.Volatile, Modifier.Relaxed, Modifier.Acquire, Modifier.Sc)
  private val Polling: Set[Modifier] = Set(Modifier.TryWait, Modifier.TestWait)

  /** Whether an instruction waits on memory another lane writes, so a loop around it needs lanes to take turns. */
  private def spins(instruction: Instruction): Boolean = {
    val signature = instruction.signature
    signature.opcode match {
      case Opcode.Atom | Opcode.Red => true
      case Opcode.Ld                => signature.modifiers.exists(Strong)
      case Opcode.Mbarrier          => signature.modifiers.exists(Polling)
      case _                        => false
    }
  }

  private def indexed(instruction: Instruction): Boolean = instruction.form match {
    case Form.BrxIdx(_, _) => true
    case _                 => false
  }

  private def uniform(instruction: Instruction): Boolean = instruction.signature.modifiers.contains(Modifier.Uni)

  private def leaves(instruction: Instruction): Boolean = instruction.pred.isEmpty &&
    (instruction.form match {
      case Form.Ret() | Form.Exit() | Form.Trap() => true
      case _                                      => false
    })

  private def branches(instruction: Instruction): Boolean = instruction.form match {
    case Form.Bra(_) | Form.BrxIdx(_, _) => true
    case _                               => false
  }

  private def isBranch(statement: Statement): Boolean = statement match {
    case Statement.Instruction(instruction) => branches(instruction)
    case _                                  => false
  }

  /** The labels a branch names, directly or through its `.branchtargets` table; none for other instructions. */
  private def branchLabels(form: Form, tables: Map[Label, Vec[Label]]): Option[Vec[Label]] = form match {
    case Form.Bra(Operand.Symbol(label))       => Some(Vec(label))
    case Form.BrxIdx(_, Operand.Symbol(table)) => Some(tables.getOrElse(table, Vec.empty[Label]))
    case _                                     => None
  }

  private def instructionAt(statements: Vec[Statement], index: Int): Option[Instruction] = statements(index) match {
    case Statement.Instruction(instruction) => Some(instruction)
    case _                                  => None
  }

  /** Basic blocks in layout order, then a virtual exit numbered `count`. A block starts at its first label, or at its
    * first instruction when unlabelled, and ends at a branch or an unconditional `ret`, `exit` or `trap`. Labels past
    * the last instruction name the exit.
    */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private class Graph(statements: Vec[Statement], tables: Map[Label, Vec[Label]]) {
    private val starts = mutable.ArrayBuffer.empty[Int]
    private val firsts = mutable.ArrayBuffer.empty[Int]
    private val lasts  = mutable.ArrayBuffer.empty[Int]
    private val owners = mutable.HashMap.empty[Label, Int]

    locally {
      var open                    = false
      def begin(index: Int): Unit = {
        starts += index
        firsts += -1
        lasts += -1
        open = true
      }
      statements.indices.foreach { index =>
        statements(index) match {
          case Statement.Label(name) =>
            if (!open || firsts.last >= 0) begin(index)
            owners(name) = starts.size - 1
          case Statement.Instruction(instruction) =>
            if (!open) begin(index)
            if (firsts.last < 0) firsts(firsts.size - 1) = index
            lasts(lasts.size - 1) = index
            if (branches(instruction) || leaves(instruction)) open = false
          case _ => ()
        }
      }
    }

    val count: Int = if (firsts.nonEmpty && firsts.last < 0) starts.size - 1 else starts.size
    val exit: Int  = count

    def start(block: Int): Int = starts(block)
    def first(block: Int): Int = firsts(block)
    def last(block: Int): Int  = lasts(block)

    def terminator(block: Int): Instruction = instructionAt(statements, lasts(block)).get

    private def target(label: Label): Int = owners.get(label).filter(_ < count).getOrElse(exit)

    /** The blocks a block's last instruction can reach, in the order its branch names them, without repeats. */
    private def reach(block: Int): Array[Int] = {
      val next            = if (block + 1 < count) block + 1 else exit
      val last            = terminator(block)
      val guarded         = last.pred.nonEmpty
      val named: Seq[Int] =
        branchLabels(last.form, tables).fold(if (leaves(last)) Seq(exit) else Seq(next))(_.map(target(_)))
      val fallsThrough = guarded && branches(last)
      (if (fallsThrough) named :+ next else named).distinct.toArray
    }

    def delivers(block: Int): Boolean = {
      val last = terminator(block)
      !leaves(last) && (!branches(last) || last.pred.nonEmpty)
    }

    def labels(block: Int): Set[Label] = owners.collect { case (name, owner) if owner == block => name }.toSet

    val successors: Array[Array[Int]] =
      Array.tabulate(count + 1)(block => if (block == count) Array.empty[Int] else reach(block))
    val predecessors: Array[Array[Int]] = {
      val found = Array.fill(count + 1)(mutable.ArrayBuffer.empty[Int])
      successors.indices.foreach(block => successors(block).foreach(found(_) += block))
      found.map(_.toArray)
    }

    def splits(block: Int): Boolean = {
      val last = terminator(block)
      branches(last) && !uniform(last) && (last.pred.nonEmpty || indexed(last)) &&
      successors(block).length > 1
    }

    def waits(block: Int): Boolean = (first(block) to last(block)).exists(index =>
      instructionAt(statements, index).exists(spins(_))
    )
  }

  /** Immediate dominators of every node that `root` reaches over `edges`, with the root its own and -1 for the rest, by
    * iterating Cooper, Harvey and Kennedy's intersection over reverse postorder.
    */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def dominators(root: Int, edges: Array[Array[Int]], reverseEdges: Array[Array[Int]]): Array[Int] = {
    val size   = edges.length
    val number = Array.fill(size)(-1)
    val order  = Array.ofDim[Int](size)
    val cursor = Array.ofDim[Int](size)
    val stack  = Array.ofDim[Int](size)
    val seen   = Array.ofDim[Boolean](size)
    var depth  = 1
    var done   = 0
    stack(0) = root
    seen(root) = true
    while (depth > 0) {
      val node = stack(depth - 1)
      if (cursor(node) < edges(node).length) {
        val next = edges(node)(cursor(node))
        cursor(node) += 1
        if (!seen(next)) {
          seen(next) = true
          stack(depth) = next
          depth += 1
        }
      } else {
        number(node) = done
        order(done) = node
        done += 1
        depth -= 1
      }
    }
    val idom = Array.fill(size)(-1)
    idom(root) = root
    def meet(a: Int, b: Int): Int = {
      var left  = a
      var right = b
      while (left != right) {
        while (number(left) < number(right)) left = idom(left)
        while (number(right) < number(left)) right = idom(right)
      }
      left
    }
    var changed = true
    while (changed) {
      changed = false
      var at = done - 2
      while (at >= 0) {
        val node      = order(at)
        var candidate = -1
        var from      = 0
        while (from < reverseEdges(node).length) {
          val other = reverseEdges(node)(from)
          if (idom(other) >= 0) candidate = if (candidate < 0) other else meet(other, candidate)
          from += 1
        }
        if (candidate != idom(node)) { idom(node) = candidate; changed = true }
        at -= 1
      }
    }
    idom
  }

  /** A dominator tree numbered so that ancestry is two comparisons. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private class Tree(root: Int, val idom: Array[Int]) {
    private val size      = idom.length
    private val enter     = Array.fill(size)(-1)
    private val leave     = Array.fill(size)(-1)
    val depth: Array[Int] = Array.ofDim[Int](size)

    locally {
      val head = Array.fill(size)(-1)
      val next = Array.fill(size)(-1)
      var node = 0
      while (node < size) {
        if (node != root && idom(node) >= 0) { next(node) = head(idom(node)); head(idom(node)) = node }
        node += 1
      }
      val stack = Array.ofDim[Int](size)
      var top   = 1
      var clock = 1
      stack(0) = root
      enter(root) = 0
      while (top > 0) {
        val at    = stack(top - 1)
        val child = head(at)
        if (child < 0) {
          leave(at) = clock
          clock += 1
          top -= 1
        } else {
          head(at) = next(child)
          depth(child) = depth(at) + 1
          enter(child) = clock
          clock += 1
          stack(top) = child
          top += 1
        }
      }
    }

    def reaches(node: Int): Boolean = enter(node) >= 0

    def dominates(above: Int, below: Int): Boolean =
      enter(above) >= 0 && enter(below) >= 0 && enter(above) <= enter(below) && leave(below) <= leave(above)
  }

  /** A natural loop: every block that reaches a back edge to `header` without passing it. */
  private class Loop(val header: Int, val body: Array[Int])

  /** The natural loops of the reachable blocks, merged by header and ordered outermost first, with the innermost loop
    * of each block and the loop enclosing each loop; -1 for none.
    */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private class Loops(graph: Graph, forward: Tree) {
    val all: Array[Loop] = {
      val sources = mutable.LinkedHashMap.empty[Int, mutable.ArrayBuffer[Int]]
      (0 until graph.count).filter(forward.reaches(_)).foreach { block =>
        graph.successors(block).foreach { target =>
          if (target < graph.count && forward.dominates(target, block))
            sources.getOrElseUpdate(target, mutable.ArrayBuffer.empty) += block
        }
      }
      sources.toArray.map { (header, entries) =>
        val body    = mutable.ArrayBuffer(header)
        val seen    = mutable.BitSet(header)
        val pending = mutable.Stack.from(entries)
        while (pending.nonEmpty) {
          val block = pending.pop()
          if (seen.add(block)) {
            body += block
            graph.predecessors(block).foreach(from => if (forward.reaches(from) && !seen(from)) pending.push(from))
          }
        }
        Loop(header, body.toArray)
      }.sortBy(-_.body.length)
    }

    val innermost: Array[Int] = Array.fill(graph.count)(-1)
    val parent: Array[Int]    = Array.fill(all.length)(-1)

    locally {
      all.indices.foreach { id =>
        parent(id) = innermost(all(id).header)
        all(id).body.foreach(block => innermost(block) = id)
      }
    }

    def contains(loop: Int, block: Int): Boolean = {
      var at = innermost(block)
      while (at >= 0 && at != loop) at = parent(at)
      at == loop
    }
  }

  private enum Where derives CanEqual {
    case Branch(block: Int)
    case Preheader(loop: Int)
  }

  private case class Region(where: Where, join: Int)

  private case class Preheader(label: Option[Label], retargets: Map[Int, Map[Label, Label]], stub: Boolean)

  /** A token slot for every region, in region order, so that two regions share a slot only when a lane can never hold
    * both tokens. A token is held from its setup until its join, so regions conflict when one is set up where the other
    * is held, when one is held where the other joins (a lane arriving there without a setup of its own would present
    * the wrong token), or when they join together.
    */
  private def assign(regions: Array[Region], graph: Graph, loops: Loops): Array[Int] = {
    val held = regions.map { region =>
      val reached                 = java.util.BitSet(graph.count)
      val pending                 = mutable.Stack.empty[Int]
      def enter(block: Int): Unit =
        if (block < graph.count && block != region.join && !reached.get(block)) {
          reached.set(block); pending.push(block)
        }
      region.where match {
        case Where.Branch(block)   => graph.successors(block).foreach(enter(_))
        case Where.Preheader(loop) => enter(loops.all(loop).header)
      }
      while (pending.nonEmpty) graph.successors(pending.pop()).foreach(enter(_))
      reached
    }
    val setups = regions.map(_.where match {
      case Where.Branch(block) => block
      case _                   => -1
    })
    def conflicts(a: Int, b: Int): Boolean =
      held(a).intersects(held(b)) || regions(a).join == regions(b).join || held(a).get(regions(b).join) ||
        held(b).get(regions(a).join) || (setups(a) >= 0 && held(b).get(setups(a))) ||
        (setups(b) >= 0 && held(a).get(setups(b)))
    val slots = Array.fill(regions.length)(-1)
    regions.indices.foreach { id =>
      val taken = (0 until id).collect { case other if conflicts(other, id) => slots(other) }.toSet
      slots(id) = Iterator.from(0).find(!taken(_)).get
    }
    slots
  }

  /** Schedules barriers and yields for the flattened statements of one routine; `tables` holds each `.branchtargets`
    * list.
    */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def schedule(statements: Vec[Statement], tables: Map[Label, Vec[Label]]): Schedule =
    if (!statements.exists(isBranch(_))) Schedule.empty
    else {
      val graph   = Graph(statements, tables)
      val forward = Tree(0, dominators(0, graph.successors, graph.predecessors))
      val reverse = Tree(graph.exit, dominators(graph.exit, graph.predecessors, graph.successors))
      val loops   = Loops(graph, forward)

      val regions    = mutable.LinkedHashMap.empty[Region, Int]
      val preheaders = mutable.HashMap.empty[Int, Option[Preheader]]

      def preheader(loop: Int): Option[Preheader] = preheaders.getOrElseUpdate(
        loop, {
          val header  = loops.all(loop).header
          val named   = graph.labels(header)
          val entered = header > 0 && graph.delivers(header - 1) && loops.contains(loop, header - 1)
          val label   = s"convergence preheader $header".unsafe[NonEmpty[String]]
          val edges   = graph.predecessors(header).filter(from => forward.reaches(from) && !loops.contains(loop, from))
            .flatMap { from =>
              val replaced = branchLabels(graph.terminator(from).form, tables).getOrElse(Vec.empty[Label]).collect {
                case target if named(target) => target -> label
              }.toMap
              Option.when(replaced.nonEmpty)(graph.last(from) -> replaced)
            }.toMap
          Option.when(!entered || edges.nonEmpty)(Preheader(Option.when(edges.nonEmpty)(label), edges, entered))
        }
      )

      (0 until graph.count).filter(block => forward.reaches(block) && graph.splits(block)).foreach { block =>
        val join = reverse.idom(block)
        if (join >= 0 && join < graph.count && graph.successors(block).forall(reverse.reaches(_))) {
          var outer = -1
          var at    = loops.innermost(block)
          while (at >= 0 && !loops.contains(at, join)) {
            outer = at
            at = loops.parent(at)
          }
          val placed =
            if (outer < 0) Some(Where.Branch(block))
            else if (reverse.dominates(join, loops.all(outer).header) && preheader(outer).nonEmpty)
              Some(Where.Preheader(outer))
            else None
          placed.foreach(where => regions.getOrElseUpdate(Region(where, join), regions.size))
        }
      }

      val ordered = regions.toArray.sortBy(_._2).map(_._1)
      val slot    = assign(ordered, graph, loops)

      val before                                  = mutable.HashMap.empty[Int, mutable.ArrayBuffer[Action]]
      def add(index: Int, actions: Action*): Unit = before.getOrElseUpdate(index, mutable.ArrayBuffer.empty) ++= actions

      val retargets = mutable.HashMap.empty[Int, Map[Label, Label]]
      val stubs     = mutable.ArrayBuffer.empty[Action]
      regions.toSeq.collect { case (Region(Where.Preheader(loop), _), id) => loop -> id }.groupMap(_._1)(_._2).toSeq
        .sortBy(_._1).foreach { (loop, barriers) =>
          val found = preheader(loop)
          val label = found.flatMap(_.label)
          found.foreach(_.retargets.foreach { (index, replaced) =>
            retargets(index) = retargets.getOrElse(index, Map.empty) ++ replaced
          })
          val setups = label.map(Action.Label(_)).toSeq ++ barriers.map(id => Action.Setup(slot(id)))
          if (found.exists(_.stub)) {
            val header = found.flatMap(_.retargets.values.flatMap(_.keys).headOption)
            stubs ++= setups ++ header.map(Action.Jump(_))
          } else add(graph.start(loops.all(loop).header), setups*)
        }
      if (stubs.nonEmpty) {
        val end   = statements.indices.findLast(instructionAt(statements, _).nonEmpty).fold(0)(_ + 1)
        val close = "convergence end".unsafe[NonEmpty[String]]
        if (graph.delivers(graph.count - 1)) add(end, (Action.Jump(close) +: stubs.toSeq :+ Action.Label(close))*)
        else add(end, stubs.toSeq*)
      }

      def nesting(where: Where): Int = where match {
        case Where.Branch(block)   => 2 * forward.depth(block) + 1
        case Where.Preheader(loop) => 2 * forward.depth(loops.all(loop).header)
      }
      regions.toSeq.groupBy(_._1.join).toSeq.sortBy(_._1).foreach { (join, entered) =>
        val inner = entered.sortBy((region, id) => (-nesting(region.where), id))
        add(graph.first(join), inner.map((_, id) => Action.Synchronise(slot(id)))*)
      }

      loops.all.foreach { loop =>
        if (loop.body.exists(graph.waits(_))) add(graph.first(loop.header), Action.Yield)
      }

      regions.foreach {
        case (Region(Where.Branch(block), _), id) => add(graph.last(block), Action.Setup(slot(id)))
        case _                                    => ()
      }

      Schedule(before.view.mapValues(Vec.from(_)).toMap, retargets.toMap)
    }
}
