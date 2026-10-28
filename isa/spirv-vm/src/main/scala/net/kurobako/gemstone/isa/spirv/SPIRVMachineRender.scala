package net.kurobako.gemstone.isa.spirv

import IdRenderer.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.render.*
import net.kurobako.gemstone.semantics.render.RenderMachine.*

private object SPIRVMachineRender {
  val Control: (contextType: String, context: String)  = ("net.kurobako.gemstone.isa.spirv.Control", "control")
  val Atomic: (contextType: String, context: String)   = ("net.kurobako.gemstone.isa.spirv.Atomics", "atomics")
  val Copy: (contextType: String, context: String)     = ("net.kurobako.gemstone.isa.spirv.Copies", "copies")
  val Pointers: (contextType: String, context: String) = ("net.kurobako.gemstone.isa.spirv.Pointers", "pointers")
  val Barrier: (contextType: String, context: String)  = ("net.kurobako.gemstone.isa.spirv.Barriers", "barriers")
  val Group: (contextType: String, context: String)    = ("net.kurobako.gemstone.isa.spirv.Groups", "groups")
  val Clock: (contextType: String, context: String)    = ("net.kurobako.gemstone.isa.spirv.Clocks", "clocks")
  val Variable: (contextType: String, context: String) = ("net.kurobako.gemstone.isa.spirv.Variables", "variables")
  val Printing: (contextType: String, context: String) = ("net.kurobako.gemstone.isa.spirv.Printing", "printing")
  val Vectors: (contextType: String, context: String)  = ("net.kurobako.gemstone.isa.spirv.Vectors", "vectors")
  val Images: (contextType: String, context: String)   = ("net.kurobako.gemstone.isa.spirv.Images", "images")
  val Fragment: (contextType: String, context: String) = ("net.kurobako.gemstone.isa.spirv.Fragments", "fragments")
  val Pipe: (contextType: String, context: String)     = ("net.kurobako.gemstone.isa.spirv.Pipes", "pipes")
  val Enqueue: (contextType: String, context: String) = ("net.kurobako.gemstone.isa.spirv.DeviceQueues", "deviceQueues")
  val Tensors: (contextType: String, context: String) = ("net.kurobako.gemstone.isa.spirv.TensorAddressing", "tensors")
}

/** Renders SPIR-V machine operations as statements for a [[Definition]] over the ids `scope` names. A block reads as
  * its label.
  */
private class SPIRVMachineRender(scope: Scope[Int])
    extends RenderMachine[Int, Int, Block, Unit](
      id => Term.Variable(scope.operand(id).fold(s"%$id")(_.name)),
      id => scope.operand(id).fold(s"%$id")(_.name),
      block => Term.Literal(block.label.toLong),
      () => scope.temporary()
    ) with SPIRVMachine[Term, Term, RenderMachine.Rendered]
    with GroupUnit[Term, RenderMachine.Rendered] {
  import SPIRVMachineRender.*

  private def request(method: String, arguments: Vec[(Term, Option[Type])]): Term.Steps =
    guarded(None, Term.context(Control, MachineCall(method, Vec.empty, arguments), None, effect = true))

  private def values(ids: Array[Int]): Vec[(Term, Option[Type])] =
    Vec.from(ids).flatMap(id => scope.operands(id).map(found => (Term.Variable(found.name), None)))

  private def block(id: Int): (Term, Option[Type]) = (Term.Literal(id.toLong), None)

  private def valued(
      owner: (contextType: String, context: String),
      method: String,
      dest: Operand,
      arguments: Vec[Term]
  ): Term.Steps = {
    val target = Operand.id(dest)
    binding(name(target), effectCall(owner, ContextCall(method, arguments, Some(typeOf(target)))))
  }

  inline def scalar(operand: Int)(inline next: Term => Term.Steps): Term.Steps =
    next(Term.Variable(scope.operand(operand).fold(s"%$operand")(_.name)))

  inline def predecessor: Term = Term.context(Control, MachineCall("predecessor", Vec.empty, Vec.empty), Some(Word))

  inline def enter(label: Int): Term.Steps = request("enter", Vec(block(label)))

  inline def phi(group: Phis): Term.Steps = Term.Steps.concat(Vec.from(group.destinations.indices).map { index =>
    val parents = Vec.from(group.parents(index))
    val sources = Vec.from(group.sources(index))
    val matches = parents.map(parent => Term.Binary("==", predecessor, Term.Literal(parent.toLong)))
    val taken   = sources.map(source => Term.Variable(scope.operand(source).fold(s"%$source")(_.name)))
    val chosen  = matches.zip(taken).init.foldRight(taken.last) { case ((condition, source), otherwise) =>
      Term.Select(condition, source, otherwise)
    }
    val reached = matches.reduceLeft[Term](Term.Binary("|", _, _))
    statement(
      Machine,
      MachineCall(
        "require",
        Vec.empty,
        Vec((reached, Some(Type.Bool)), (Term.Constant(s"\"OpPhi %${group.destinations(index)} incoming edge\""), None))
      )
    ) ++ binding(scope.operand(group.destinations(index)).fold(s"%${group.destinations(index)}")(_.name), chosen)
  })

  inline def switch(selector: Term, cases: Cases): Term.Steps = {
    val table = cases.literals.zip(cases.blocks.map(_.label)).map((literal, block) => s"${literal}L -> $block")
    request(
      "switch",
      Vec((selector, Some(Word)), (Term.Constant(table.mkString("Seq(", ", ", ")")), None), block(cases.default.label))
    )
  }

  inline def call(callee: Callee): Term.Steps =
    request("call", block(callee.function.id) +: values(callee.arguments))

  inline def callIndirect(call: Indirect): Term.Steps =
    request("callIndirect", values(Array(call.pointer)) ++ values(call.arguments))

  inline def sweep(sweep: Sweep)(inline argument: (Int, Term, Int) => Term)(
      inline next: (Int => Term) => Term.Steps
  ): Term.Steps = {
    val returned = Vec.tabulate(sweep.calls)(_ => scope.temporary())
    Term.Steps.concat(Vec.tabulate(sweep.calls) { call =>
      val previous  = if (call == 0) Term.Literal(0L) else Term.Variable(returned(call - 1))
      val arguments = Vec.tabulate(sweep.fixed.length)(parameter =>
        if (sweep.fixed(parameter) >= 0) values(Array(sweep.fixed(parameter)))
        else Vec((argument(call, previous, parameter), Option.empty[Type]))
      ).flatten
      binding(
        returned(call),
        Term.context(
          Control,
          MachineCall("called", Vec.empty, block(sweep.function.id) +: arguments),
          Some(Word),
          effect = true
        )
      )
    }) ++ next(call => Term.Variable(returned(call)))
  }

  inline def returnControl: Term.Steps = request("returnControl", Vec.empty)

  inline def returnValue(value: Int): Term.Steps = request("returnValue", values(Array(value)))

  inline def kill: Term.Steps = request("kill", Vec.empty)

  inline def terminate: Term.Steps = request("terminate", Vec.empty)

  inline def unreachable: Term.Steps =
    guarded(
      None,
      Term.context(
        Machine,
        MachineCall(
          "require",
          Vec.empty,
          Vec((Term.Constant("false"), None), (Term.Constant("\"OpUnreachable\""), None))
        ),
        None,
        effect = true
      )
    )

  inline def abort(message: Int): Term.Steps = request("abort", values(Array(message)))

  private def name(id: Int): String = scope.operand(id).fold(s"%$id")(_.name)

  private def pointed(pointer: Int): (Term, Option[Type]) = (Term.Variable(name(pointer)), Some(Word))

  private def loadOf(pointer: Int, tpe: Type): Term = Term.context(
    Machine,
    MachineCall("load", Vec(tpe), Vec((RenderMachine.space(Space.Generic), None), pointed(pointer))),
    Some(tpe)
  )

  private def plain(id: Int): (Term, Option[Type]) = (Term.Variable(name(id)), None)

  inline def loadScalar(pointer: Int, size: Int in 1 ~ 8, target: Int, typeId: Int)(
      inline next: => Term.Steps
  ): Term.Steps = {
    val tpe = Type.unsigned(Type.width(size.raw * 8))
    binding(
      name(target),
      loadOf(pointer, tpe)
    ) ++ next
  }

  inline def loadAddress(pointer: Int, target: Int, typeId: Int, instruction: Instruction)(
      inline next: => Term.Steps
  ): Term.Steps = binding(
    name(target),
    loadOf(pointer, Word)
  ) ++ next

  inline def loadValue(pointer: Int, target: Int, typeId: Int, size: Int >= 0)(
      inline next: => Term.Steps
  ): Term.Steps =
    binding(
      name(target),
      Term.context(
        Machine,
        MachineCall(
          "loadBytes",
          Vec.empty,
          Vec((RenderMachine.space(Space.Generic), None), pointed(pointer), (Term.Constant(size.toString), Some(Word)))
        ),
        None
      )
    ) ++ next

  inline def loadHandle(pointer: Int, target: Int, typeId: Int, pointee: Int)(
      inline next: => Term.Steps
  ): Term.Steps = write((), target, read((), pointer))(next)

  inline def loadSampled(pointer: Int, target: Int, typeId: Int, imageType: Int)(
      inline next: => Term.Steps
  ): Term.Steps = binding(
    name(target),
    loadOf(pointer, Word)
  ) ++ next

  inline def storeScalar(pointer: Int, source: Int, size: Int in 1 ~ 8, instruction: Instruction)(
      inline next: => Term.Steps
  ): Term.Steps = {
    val tpe = Type.unsigned(Type.width(size.raw * 8))
    statement(
      Machine,
      MachineCall(
        "store",
        Vec(tpe),
        Vec((RenderMachine.space(Space.Generic), None), pointed(pointer), (Term.Variable(name(source)), Some(tpe)))
      )
    ) ++ next
  }

  inline def storeValue(
      pointer: Int,
      source: Int,
      instruction: Instruction
  )(inline next: => Term.Steps): Term.Steps =
    statement(
      Machine,
      MachineCall(
        "store",
        Vec(Word),
        Vec((RenderMachine.space(Space.Generic), None), pointed(pointer), (Term.Variable(name(source)), Some(Word)))
      )
    ) ++ next

  inline def copyMemory(target: Int, origin: Int)(inline next: => Term.Steps): Term.Steps =
    statement(Copy, MachineCall("copy", Vec.empty, Vec(pointed(target), pointed(origin)))) ++ next

  inline def copyMemorySized(target: Int, origin: Int, size: Int)(inline next: => Term.Steps): Term.Steps =
    loadBytes((), Space.Generic, Term.Variable(name(origin)), Term.Variable(name(size)), "OpCopyMemorySized")(bytes =>
      storeBytes((), Space.Generic, Term.Variable(name(target)), bytes, "OpCopyMemorySized")(next)
    )

  inline def variable(target: Int, typeId: Int, storageClass: Int, initialiser: Option[Int])(
      inline next: => Term.Steps
  ): Term.Steps = binding(
    name(target),
    initialiser.fold(Term.context(Variable, MachineCall("variable", Vec.empty, Vec.empty), Some(Word)))(value =>
      Term.context(Variable, MachineCall("initialisedVariable", Vec.empty, Vec(plain(value))), Some(Word))
    )
  ) ++ next

  inline def variableLengthArray(target: Int, typeId: Int, element: Int, length: Term)(
      inline next: => Term.Steps
  ): Term.Steps = binding(
    name(target),
    Term.context(
      Variable,
      MachineCall("variableLengthArray", Vec.empty, Vec[(Term, Option[Type])]((length, None))),
      Some(Word)
    )
  ) ++ next

  inline def groupAsyncCopy(
      target: Int,
      typeId: Int,
      scope: Int,
      destination: Int,
      source: Int,
      count: Int,
      stride: Int,
      element: Int
  )(inline next: => Term.Steps): Term.Steps = {
    val operands = Vec(scope, destination, source, count, stride) ++ (if (element < 0) Vec.empty[Int] else Vec(element))
    binding(
      name(target),
      effectCall(
        Copy,
        ContextCall("groupAsyncCopy", operands.map[Term](id => Term.Variable(name(id))), Some(typeOf(target)))
      )
    ) ++ next
  }

  inline def waitEvents(scope: Int, count: Int, events: Int)(inline next: => Term.Steps): Term.Steps =
    effect(Copy, "waitEvents", Vec(scope, count, events).map[Term](id => Term.Variable(name(id)))) ++ next

  inline def saveMemory(target: Int, typeId: Int)(inline next: => Term.Steps): Term.Steps =
    binding(name(target), Term.context(Variable, MachineCall("saveMemory", Vec.empty, Vec.empty), Some(Word))) ++ next

  inline def restoreMemory(pointer: Int)(inline next: => Term.Steps): Term.Steps =
    statement(Variable, MachineCall("restoreMemory", Vec.empty, Vec(pointed(pointer)))) ++ next

  inline def memorySemantics(source: Int, target: Int, typeId: Int)(inline next: => Term.Steps): Term.Steps =
    binding(
      name(target),
      Term.context(Variable, MachineCall("memorySemantics", Vec.empty, Vec(pointed(source))), Some(Word))
    ) ++ next

  inline def atomicLoad(pointer: Int, size: Int in 1 ~ 8, target: Int, typeId: Int, scope: Int, semantics: Int)(
      inline next: => Term.Steps
  ): Term.Steps = {
    val tpe = Type.unsigned(Type.width(size.raw * 8))
    binding(
      name(target),
      Term.context(
        Atomic,
        MachineCall("load", Vec(tpe), Vec(pointed(pointer), plain(scope), plain(semantics))),
        Some(tpe)
      )
    ) ++ next
  }

  inline def atomicStore(pointer: Int, source: Int, size: Int in 1 ~ 8, scope: Int, semantics: Int)(
      inline next: => Term.Steps
  ): Term.Steps = {
    val tpe = Type.unsigned(Type.width(size.raw * 8))
    statement(
      Atomic,
      MachineCall(
        "store",
        Vec.empty,
        Vec(pointed(pointer), (Term.Variable(name(source)), Some(tpe)), plain(scope), plain(semantics))
      )
    ) ++ next
  }

  inline def flagTestAndSet(pointer: Int, target: Int, typeId: Int, scope: Int, semantics: Int)(
      inline next: => Term.Steps
  ): Term.Steps =
    binding(
      name(target),
      Term.context(
        Atomic,
        MachineCall("flagTestAndSet", Vec.empty, Vec(pointed(pointer), plain(scope), plain(semantics))),
        Some(Type.Bool)
      )
    ) ++ next

  inline def flagClear(pointer: Int, scope: Int, semantics: Int)(inline next: => Term.Steps): Term.Steps =
    statement(Atomic, MachineCall("flagClear", Vec.empty, Vec(pointed(pointer), plain(scope), plain(semantics)))) ++
      next

  inline def controlBarrier(scope: Int, memory: Int, semantics: Int): Term.Steps =
    statement(Barrier, MachineCall("controlBarrier", Vec.empty, Vec(plain(scope), plain(memory), plain(semantics))))

  inline def arriveBarrier(scope: Int, memory: Int, semantics: Int)(inline next: => Term.Steps): Term.Steps =
    statement(Barrier, MachineCall("arriveBarrier", Vec.empty, Vec(plain(scope), plain(memory), plain(semantics)))) ++
      next

  inline def fence(scope: Int, semantics: Int)(inline next: => Term.Steps): Term.Steps =
    statement(Barrier, MachineCall("fence", Vec.empty, Vec(plain(scope), plain(semantics)))) ++ next

  inline def namedBarrier(barrier: Int, memory: Int, semantics: Int): Term.Steps =
    statement(Barrier, MachineCall("namedBarrier", Vec.empty, Vec(plain(barrier), plain(memory), plain(semantics))))

  inline def initializeBarrier(target: Int, typeId: Int, count: Int)(inline next: => Term.Steps): Term.Steps =
    binding(
      name(target),
      Term.context(Barrier, MachineCall("initializeBarrier", Vec.empty, Vec(plain(count))), Some(Word))
    ) ++ next

  inline def collective(collective: Collective): Term.Steps = renderCollective(collective)

  private val subgroupScope: Term = Term.Constant("net.kurobako.gemstone.isa.spirv.Scope.Subgroup")

  private def grouped(call: MachineCall, result: Type): Term = Term.context(Group, call, Some(result))

  private def grouping(id: Int): (Term, Option[Type]) =
    if (id == Collective.ImpliedSubgroup) (subgroupScope, None) else plain(id)

  private def typeOf(id: Int): Type = scope.operand(id).fold(Word)(_.tpe)

  private def reduced(
      kind: VMCollectives.Kind,
      operation: Long,
      target: Int,
      source: Int,
      cluster: Option[Int]
  ): Term.Steps = {
    val combine = GroupResolution.combining(kind)
    cluster match {
      case Some(size) => GroupResolution.clustered(Render, this)(combine, source, size, target)
      case None       => GroupResolution.reduction(Render, this)(combine, operation, source, target)
    }
  }

  inline def bits(view: Term, first: Term): Term =
    grouped(MachineCall("bits", Vec.empty, Vec((view, None), (first, Some(Word)))), Type.unsigned(32))

  inline def matching(view: Term, own: Term, first: Term, floating: Option[Int in (16, 32, 64)]): Term = {
    val width = Term.Constant(floating.fold("None")(bits => s"Some($bits)"))
    grouped(
      MachineCall("matching", Vec.empty, Vec((view, None), (own, None), (first, Some(Word)), (width, None))),
      Type.unsigned(32)
    )
  }

  inline def leader: Term = grouped(MachineCall("leader", Vec.empty, Vec.empty), Word)

  inline def subgroupLocal: Term = grouped(MachineCall("subgroupLocal", Vec.empty, Vec.empty), Word)

  inline def subgroupSize: Term = grouped(MachineCall("subgroupSize", Vec.empty, Vec.empty), Word)

  inline def count: Term = grouped(MachineCall("count", Vec.empty, Vec.empty), Word)

  inline def launched: Term = grouped(MachineCall("launched", Vec.empty, Vec.empty), Word)

  inline def lane(scope: Term, keys: Vec[Term]): Term =
    grouped(MachineCall("lane", Vec.empty, (scope, None) +: keys.map(key => (key, None))), Word)

  inline def fold(view: Term, from: Term, until: Term, combine: Combine, typed: Int, bare: Boolean): Term = {
    val tpe = typeOf(typed)
    grouped(
      MachineCall(
        if (bare) "foldFirst" else "fold",
        Vec(tpe),
        Vec(
          (Term.Constant(s"net.kurobako.gemstone.isa.spirv.Combine.$combine"), None),
          (view, None),
          (from, Some(Word)),
          (until, Some(Word))
        )
      ),
      tpe
    )
  }

  private def scalar(id: Int): Int = {
    if (scope.operands(id).size != 1) throw IllegalStateException(s"%$id is not a scalar")
    id
  }

  private def elected(target: Int): Term.Steps = GroupResolution.elect(Render, this)(target)

  private def voted(kind: VMCollectives.Kind, predicate: Int, target: Int): Term.Steps =
    GroupResolution.vote(Render, this)(GroupResolution.combining(kind), predicate, target)

  private def floatingOf(id: Int): Option[Int in (16, 32, 64)] = typeOf(id) match {
    case Type.Float(width) => Some(width.unsafe[Int in (16, 32, 64)])
    case _                 => None
  }

  private def allEqualed(source: Int, target: Int): Term.Steps =
    GroupResolution.allEqual(Render, this)(floatingOf(source), source, target)

  private def firstBroadcast(source: Int, target: Int): Term.Steps =
    GroupResolution.broadcastFirst(this)(source, target)

  private def shuffled(selection: Collective.Selection, source: Int, lane: Int, target: Int): Term.Steps =
    GroupResolution.shuffle(Render, this)(selection, source, lane, target)

  private def ballotted(predicate: Int, dest: Int): Term.Steps =
    gather(UnitAlgebra.Slot.First, predicate) { view =>
      Term.Steps.concat(scope.operands(dest).zipWithIndex.map((found, word) =>
        binding(found.name, GroupResolution.ballotWord(Render, this)(view, word))
      ))
    }

  private def queried(dest: Int, source: Int, mode: Int, bit: Option[Int]): Term.Steps = {
    val Vec(first, second, third, fourth) = leaves(source): @unchecked
    val own                               = laneIndex(())
    val tested                            =
      bit.fold(own)(operand => Term.Variable(scope.operand(operand).fold(s"%$operand")(_.name)))
    val words = Vec(first, second, third, fourth)
    binding(
      name(dest),
      mode match {
        case -1 => ballotTest(words, tested)
        case 0  => ballotTotal(words, unitSize)
        case 1  => ballotTotal(words, Render.add(own, Render.literal(1L)))
        case 2  => ballotTotal(words, own)
        case 3  => ballotFirst(words)
        case _  => ballotLast(words)
      }
    )
  }

  private def ballotTest(words: Vec[Term], bit: Term): Term =
    GroupResolution.ballotBit(Render)(words(0), words(1), words(2), words(3), bit)

  private def ballotTotal(words: Vec[Term], until: Term): Term =
    GroupResolution.ballotCount(Render)(words(0), words(1), words(2), words(3), until)

  private def ballotFirst(words: Vec[Term]): Term =
    GroupResolution.ballotLowest(Render)(words(0), words(1), words(2), words(3), unitSize)

  private def ballotLast(words: Vec[Term]): Term =
    GroupResolution.ballotHighest(Render)(words(0), words(1), words(2), words(3), unitSize)

  private def renderCollective(collective: Collective): Term.Steps = {
    val dest = collective.ids.dest
    collective match {
      case Collective.Elect(_, _)                              => elected(scalar(dest))
      case Collective.Vote(kind = kind, predicate = predicate) => voted(kind, predicate, scalar(dest))
      case Collective.Reduction(
            kind = kind,
            operation = operation,
            source = source,
            cluster = cluster
          ) =>
        reduced(kind, operation, scalar(dest), scalar(source), cluster)
      case Collective.Value(kind = kind, source = source, operation = operation, cluster = cluster) =>
        kind match {
          case VMCollectives.Kind.AllEqual       => allEqualed(scalar(source), scalar(dest))
          case VMCollectives.Kind.BroadcastFirst => firstBroadcast(scalar(source), scalar(dest))
          case _ => reduced(kind, operation.toLong, scalar(dest), scalar(source), cluster)
        }
      case Collective.Shuffle(source = source, lane = lane, selection = selection) =>
        shuffled(selection, scalar(source), scalar(lane), scalar(dest))
      case Collective.Broadcast(_, scopeId, source, local) =>
        GroupResolution.broadcast(this)(
          grouping(scopeId)._1,
          Vec(Term.Variable(name(scalar(local)))),
          scalar(source),
          scalar(dest)
        )
      case Collective.Ballot(predicate = predicate) => ballotted(scalar(predicate), dest)
      case Collective.Partition(_, source)          =>
        gather(UnitAlgebra.Slot.First, scalar(source)) { view =>
          Term.Steps.concat(scope.operands(dest).zipWithIndex.map((found, word) =>
            binding(found.name, GroupResolution.partitionWord(Render, this)((), view, word, floatingOf(source)))
          ))
        }
      case Collective.BallotQuery(source = source, mode = mode, bit = bit) => queried(dest, source, mode, bit)
      case Collective.MatrixMultiply(_, operands, _)                       =>
        val lanes: Vec[(Term, Option[Type])] = operands.flatMap(operand => leaves(operand).map(lane => (lane, None)))
        Term.Steps.concat(scope.operands(dest).zipWithIndex.map((found, word) =>
          binding(
            found.name,
            grouped(
              MachineCall(
                "matrixMultiply",
                Vec.empty,
                Vec[(Term, Option[Type])]((Term.Literal(word.toLong), None)) ++ lanes
              ),
              Word
            )
          )
        ))
      case Collective.MatrixRows(_, source, selector) =>
        val shape = selector.map(value => (Term.Literal(value), None))
        val lines = leaves(source).map(lane => (lane, None))
        Term.Steps.concat(scope.operands(dest).zipWithIndex.map((found, word) =>
          binding(
            found.name,
            grouped(
              MachineCall(
                "matrixRows",
                Vec.empty,
                Vec[(Term, Option[Type])]((Term.Literal(word.toLong), None)) ++ shape ++ lines
              ),
              Word
            )
          )
        ))
      case Collective.IntelShuffle(_, data, invocation, xor) =>
        val selection = if (xor) Collective.Selection.Xor else Collective.Selection.Absolute
        shuffled(selection, scalar(data), scalar(invocation), scalar(dest))
      case Collective.IntelShift(_, _, first, second, delta, direction) =>
        GroupResolution.shift(Render, this)(direction, scalar(first), scalar(second), scalar(delta), scalar(dest))
    }
  }

  private def leaves(id: Int): Vec[Term] = scope.operands(id).map(found => Term.Variable(found.name))

  private def assigned(target: Int, values: Vec[Term]): Term.Steps =
    Term.Steps.concat(scope.operands(target).zip(values).map((found, value) => binding(found.name, value)))

  private def inRange(index: Term, size: Int, label: String): Term.Steps = statement(
    Machine,
    MachineCall(
      "require",
      Vec.empty,
      Vec((Term.Binary("<u", index, Term.Literal(size.toLong)), Some(Type.Bool)), (Term.Constant(s"\"$label\""), None))
    )
  )

  private def indexed(index: Term, values: Vec[Term]): Term = values.zipWithIndex.init.foldRight(values.last) {
    case ((value, position), otherwise) =>
      Term.Select(Term.Binary("==", index, Term.Literal(position.toLong)), value, otherwise)
  }

  inline def extract(source: Int, offset: Int, count: Int, target: Int, typeId: Int)(
      inline next: => Term.Steps
  ): Term.Steps = assigned(target, leaves(source).slice(offset, offset + count)) ++ next

  inline def construct(parts: Array[Int], leaves: Int, target: Int, typeId: Int)(
      inline next: => Term.Steps
  ): Term.Steps = assigned(target, Vec.from(parts).flatMap(this.leaves(_))) ++ next

  inline def insert(source: Int, composite: Int, offset: Int, count: Int, target: Int, typeId: Int)(
      inline next: => Term.Steps
  ): Term.Steps = {
    val kept = leaves(composite)
    assigned(target, kept.patch(offset, leaves(source), count)) ++ next
  }

  inline def shuffle(left: Int, right: Int, selection: Array[Long], target: Int, typeId: Int)(
      inline next: => Term.Steps
  ): Term.Steps = {
    val combined = leaves(left) ++ leaves(right)
    assigned(
      target,
      Vec.from(selection).map(selected => if (selected == 0xffffffffL) Term.Literal(0L) else combined(selected.toInt))
    ) ++ next
  }

  inline def transpose(source: Int, rows: Int, columns: Int, target: Int, typeId: Int)(
      inline next: => Term.Steps
  ): Term.Steps = {
    val values = leaves(source)
    assigned(target, Vec.from(0 until rows * columns).map(lane => values(Matrices.transposed(lane, rows, columns)))) ++
      next
  }

  private def call(owner: (contextType: String, context: String), invocation: ContextCall): Term = {
    val ContextCall(method, arguments, result) = invocation
    Term.context(
      owner,
      MachineCall(method, Vec.empty, arguments.map(argument => (argument, Option.empty[Type]))),
      result
    )
  }

  private def effectCall(owner: (contextType: String, context: String), invocation: ContextCall): Term = {
    val ContextCall(method, arguments, result) = invocation
    Term.context(
      owner,
      MachineCall(method, Vec.empty, arguments.map(argument => (argument, Option.empty[Type]))),
      result,
      effect = true
    )
  }

  private def effect(owner: (contextType: String, context: String), method: String, arguments: Vec[Term]): Term.Steps =
    guarded(None, effectCall(owner, ContextCall(method, arguments, None)))

  def leafCount(id: Int): Int = scope.operands(id).size

  def component(id: Int): FragmentSemantics.Component = typeOf(id).unranged match {
    case Type.Float(width)      => FragmentSemantics.Component.Floating(width.raw.unsafe[Int in (16, 32, 64)])
    case Type.Integer(width, _) => FragmentSemantics.Component.Integer(width)
    case _                      => FragmentSemantics.Component.Other
  }

  def leafBytes(id: Int): Int in 1 ~ 8 = ((typeOf(id).bits.raw + 7) / 8).unsafe[Int in 1 ~ 8]

  inline def eachLeaf(sources: Array[Int], count: Int, target: Int, typeId: Int)(inline body: Int => Term)(
      inline next: => Term.Steps
  ): Term.Steps = assigned(target, Vec.from(0 until count).map(body(_))) ++ next

  inline def leaf(source: Int, index: Int): Term = leaves(source)(index)

  inline def leafAt(sources: Array[Int], values: Int, index: Term): Term =
    indexed(index, Vec.from(sources.take(values)).flatMap(leaves(_)))

  inline def ownLeaf(source: Int, index: Int, rows: Int, columns: Int, use: Int): Term =
    grouped(
      MachineCall(
        "ownLeaf",
        Vec.empty,
        Vec[(Term, Option[Type])](
          (Term.Literal(index.toLong), None),
          (Term.Literal(rows.toLong), None),
          (Term.Literal(columns.toLong), None),
          (Term.Literal(use.toLong), None)
        ) ++ leaves(source).map(lane => (lane, None))
      ),
      Word
    )

  private def at(pointer: Int, offset: Term): (Term, Option[Type]) = (
    offset match {
      case Term.Literal(0L) => Term.Variable(name(pointer))
      case _                => Term.Binary("+", Term.Variable(name(pointer)), offset)
    },
    Some(Word)
  )

  inline def strideBytes(pointer: Int, width: Int in 1 ~ 8): Term =
    Term.context(
      Fragment,
      MachineCall("strideBytes", Vec.empty, Vec(pointed(pointer), (Term.Literal(width.toLong), None))),
      Some(Word)
    )

  inline def loadLeaves(pointer: Int, count: Int, width: Int in 1 ~ 8, target: Int, typeId: Int)(
      inline offset: Int => Term
  )(inline next: => Term.Steps): Term.Steps = {
    val tpe = Type.unsigned(Type.width(width.raw * 8))
    assigned(
      target,
      Vec.from(0 until count).map(lane =>
        Term.context(
          Machine,
          MachineCall("load", Vec(tpe), Vec((RenderMachine.space(Space.Generic), None), at(pointer, offset(lane)))),
          Some(tpe)
        )
      )
    ) ++ next
  }

  inline def storeLeaves(pointer: Int, source: Int, count: Int, width: Int in 1 ~ 8)(inline offset: Int => Term)(
      inline next: => Term.Steps
  ): Term.Steps = {
    val tpe   = Type.unsigned(Type.width(width.raw * 8))
    val lanes = leaves(source)
    Term.Steps.concat(Vec.from(0 until count).map(lane =>
      statement(
        Machine,
        MachineCall(
          "store",
          Vec(tpe),
          Vec((RenderMachine.space(Space.Generic), None), at(pointer, offset(lane)), (lanes(lane), Some(tpe)))
        )
      )
    )) ++ next
  }

  inline def loadElement(pointer: Int, offset: Term, bytes: Term)(inline next: Term => Term.Steps): Term.Steps = {
    val loaded = scope.temporary()
    binding(
      loaded,
      Term.context(
        Fragment,
        MachineCall("loadElement", Vec.empty, Vec(pointed(pointer), (offset, Some(Word)), (bytes, Some(Word)))),
        Some(Word)
      )
    ) ++ next(Term.Variable(loaded))
  }

  inline def storeElement(
      pointer: Int,
      offset: Term,
      bytes: Term,
      value: Term
  )(inline next: => Term.Steps): Term.Steps =
    statement(
      Fragment,
      MachineCall(
        "storeElement",
        Vec.empty,
        Vec(pointed(pointer), (offset, Some(Word)), (bytes, Some(Word)), (value, Some(Word)))
      )
    ) ++ next

  inline def loadAbsolute(address: Term, offset: Term, bytes: Term, pointee: Int)(
      inline next: Term => Term.Steps
  ): Term.Steps = {
    val loaded = scope.temporary()
    binding(
      loaded,
      Term.context(
        Tensors,
        MachineCall(
          "loadAbsolute",
          Vec.empty,
          Vec((address, Some(Word)), (offset, Some(Word)), (bytes, Some(Word)), literal(pointee))
        ),
        Some(Word)
      )
    ) ++ next(Term.Variable(loaded))
  }

  inline def storeAbsolute(address: Term, offset: Term, bytes: Term, value: Term, pointee: Int)(
      inline next: => Term.Steps
  ): Term.Steps =
    statement(
      Tensors,
      MachineCall(
        "storeAbsolute",
        Vec.empty,
        Vec((address, Some(Word)), (offset, Some(Word)), (bytes, Some(Word)), (value, Some(Word)), literal(pointee))
      )
    ) ++ next

  inline def loadLevel(offset: Term, bytes: Term)(inline next: Term => Term.Steps): Term.Steps = {
    val loaded = scope.temporary()
    binding(
      loaded,
      Term.context(
        Images,
        MachineCall("loadLevel", Vec.empty, Vec((offset, Some(Word)), (bytes, Some(Word)))),
        Some(Word)
      )
    ) ++ next(Term.Variable(loaded))
  }

  inline def storeLevel(offset: Term, bytes: Term, value: Term)(inline next: => Term.Steps): Term.Steps =
    statement(
      Images,
      MachineCall("storeLevel", Vec.empty, Vec((offset, Some(Word)), (bytes, Some(Word)), (value, Some(Word))))
    ) ++ next

  inline def repeat(count: Term)(inline body: Term => Term.Steps)(inline next: => Term.Steps): Term.Steps = {
    val index = scope.temporary()
    RenderMachine.each(
      index,
      Term.context(Fragment, MachineCall("indices", Vec.empty, Vec((count, Some(Word)))), None),
      body(Term.Variable(index))
    ) ++ next
  }

  inline def partials(count: Int)(inline next: => Term.Steps): Term.Steps =
    statement(Fragment, MachineCall("partials", Vec.empty, Vec((Term.Literal(count.toLong), None)))) ++ next

  inline def setPartial(lane: Term, value: Term)(inline next: => Term.Steps): Term.Steps =
    statement(Fragment, MachineCall("setPartial", Vec.empty, Vec((lane, Some(Word)), (value, Some(Word))))) ++ next

  inline def partial(lane: Term): Term =
    Term.context(Fragment, MachineCall("partial", Vec.empty, Vec((lane, Some(Word)))), Some(Word))

  private def imaged(method: String, arguments: Vec[(Term, Option[Type])]): Term =
    Term.context(Images, MachineCall(method, Vec.empty, arguments), Some(Word))

  private def literal(value: Int): (Term, Option[Type]) = (Term.Literal(value.toLong), None)

  private def word(term: Term): (Term, Option[Type]) = (term, Some(Word))

  private def read[A](method: String, arguments: Vec[(Term, Option[Type])])(next: Term => Term.Steps): Term.Steps = {
    val found = scope.temporary()
    binding(found, imaged(method, arguments)) ++ next(Term.Variable(found))
  }

  inline def vulkan: Term = imaged("vulkan", Vec.empty)

  inline def integral(operand: Int): Term = typeOf(operand) match {
    case Type.Float(_) => Term.Literal(0L)
    case _             => Term.Literal(1L)
  }

  inline def count(operand: Int): Term = Term.Literal(scope.operands(operand).size.toLong)

  inline def numeric(operand: Int)(inline next: => Term.Steps): Term.Steps =
    statement(Images, MachineCall("numeric", Vec.empty, leaves(operand).map(word(_)))) ++ next

  inline def view(image: Int, query: Boolean)(inline next: (Term, Term, Term) => Term.Steps): Term.Steps =
    read("dimensions", Vec(pointed(image)))(dimensions =>
      read("arrayed", Vec(pointed(image)))(arrayed =>
        read("cube", Vec(pointed(image)))(cube => next(dimensions, arrayed, cube))
      )
    )

  inline def sampler(image: Int, missing: NonEmpty[String])(inline next: => Term.Steps): Term.Steps =
    statement(Images, MachineCall("sampler", Vec.empty, Vec(pointed(image)))) ++ next

  inline def samplerWords(image: Int, into: Int)(inline next: => Term.Steps): Term.Steps =
    statement(Images, MachineCall("samplerWords", Vec.empty, Vec(pointed(image), literal(into)))) ++ next

  inline def samplerWord(image: Int, word: Int)(inline next: Term => Term.Steps): Term.Steps =
    read("samplerWord", Vec(pointed(image), literal(word)))(next)

  inline def formatWord(image: Int)(inline next: Term => Term.Steps): Term.Steps =
    read("formatWord", Vec(pointed(image)))(next)

  inline def levelCount(image: Int)(inline next: Term => Term.Steps): Term.Steps =
    read("levelCount", Vec(pointed(image)))(next)

  inline def level(image: Int, level: Term, into: Int)(inline next: => Term.Steps): Term.Steps =
    statement(Images, MachineCall("level", Vec.empty, Vec(pointed(image), word(level), literal(into)))) ++ next

  inline def texel(layer: Term, x: Term, row: Term, into: Int)(inline next: => Term.Steps): Term.Steps =
    statement(Images, MachineCall("texel", Vec.empty, Vec(word(layer), word(x), word(row), literal(into)))) ++ next

  inline def swizzle(image: Int, from: Term)(inline next: => Term.Steps): Term.Steps =
    statement(Images, MachineCall("swizzle", Vec.empty, Vec(pointed(image), word(from)))) ++ next

  inline def gradients(from: Int, count: Term, resultType: Int, dest: Int, into: Int)(
      inline next: Term => Term.Steps
  ): Term.Steps = read("gradients", Vec(literal(from), word(count), literal(into)))(next)

  inline def lodResult(typeId: Int)(inline next: => Term.Steps): Term.Steps = next

  inline def when(condition: Term)(inline body: => Term.Steps)(inline next: => Term.Steps): Term.Steps = {
    val inner = body
    (if (inner.all.isEmpty) Term.Steps.empty else Term.Steps(Term.Step("machine", Term.When(condition, inner.all)))) ++
      next
  }

  inline def printf(format: Int, arguments: Array[Int], target: Int, typeId: Int)(
      inline next: => Term.Steps
  ): Term.Steps = effect(Printing, "printf", leaves(format) ++ Vec.from(arguments).flatMap(leaves(_))) ++
    assigned(target, Vec(Term.Literal(0L))) ++ next

  inline def loadVector(offset: Int, pointer: Int, count: Int, size: Int in 1 ~ 8, target: Int, typeId: Int)(
      inline next: => Term.Steps
  ): Term.Steps = assigned(
    target,
    Vec.from(0 until count).map(lane =>
      call(
        Vectors,
        ContextCall(
          "loaded",
          Vec(
            Term.Literal(lane.toLong),
            Term.Variable(name(offset)),
            Term.Variable(name(pointer)),
            Term.Literal(count.toLong),
            Term.Literal(size.toLong)
          ),
          None
        )
      )
    )
  ) ++ next

  inline def storeVector(data: Int, offset: Int, pointer: Int, count: Int, size: Int in 1 ~ 8)(
      inline next: => Term.Steps
  ): Term.Steps = effect(
    Vectors,
    "stored",
    Vec(
      Term.Variable(name(offset)),
      Term.Variable(name(pointer)),
      Term.Literal(count.toLong),
      Term.Literal(size.toLong)
    ) ++ leaves(data)
  ) ++ next

  inline def convertHandle(source: Int, target: Int, typeId: Int)(inline next: => Term.Steps): Term.Steps =
    assigned(target, leaves(source)) ++ next

  inline def rawAccessChain(
      base: Int,
      stride: Int,
      element: Int,
      offset: Int,
      pointeeType: Int,
      target: Int,
      typeId: Int
  )(inline next: => Term.Steps): Term.Steps = binding(
    name(target),
    Term.Binary(
      "+",
      Term.Variable(name(base)),
      Term.Binary(
        "+",
        Term.Binary("*", Term.Variable(name(stride)), Term.Variable(name(element))),
        Term.Variable(name(offset))
      )
    )
  ) ++ next

  inline def predicatedLoad(predicate: Int, pointer: Int, default: Int, target: Int, typeId: Int, size: Int >= 0)(
      inline next: => Term.Steps
  ): Term.Steps = {
    val tpe = Type.unsigned(Type.width(size * 8))
    binding(
      name(target),
      Term.Select(
        Term.Variable(name(predicate)),
        loadOf(pointer, tpe),
        Term.Variable(name(default))
      )
    ) ++ next
  }

  inline def predicatedStore(predicate: Int, pointer: Int, source: Int)(inline next: => Term.Steps): Term.Steps =
    guarded(
      Some(Term.Variable(name(predicate))),
      Term.context(
        Machine,
        MachineCall(
          "store",
          Vec(typeOf(source)),
          Vec(
            (RenderMachine.space(Space.Generic), None),
            pointed(pointer),
            (Term.Variable(name(source)), Some(typeOf(source)))
          )
        ),
        None,
        effect = true
      )
    ) ++ next

  inline def maskedGather(pointers: Int, mask: Int, fill: Int, target: Int, typeId: Int, width: Int in 1 ~ 8)(
      inline next: => Term.Steps
  ): Term.Steps = {
    val tpe       = Type.unsigned(Type.width(width.raw * 8))
    val addresses = leaves(pointers)
    val selected  = leaves(mask)
    val fills     = leaves(fill)
    Term.Steps.concat(scope.operands(target).zipWithIndex.map((found, lane) =>
      binding(
        found.name,
        Term.Select(
          Term.Binary("!=", selected(lane), Term.Literal(0L)),
          Term.context(
            Machine,
            MachineCall(
              "load",
              Vec(tpe),
              Vec((RenderMachine.space(Space.Generic), None), (addresses(lane), Some(Word)))
            ),
            Some(tpe)
          ),
          fills(lane)
        )
      )
    )) ++ next
  }

  inline def maskedScatter(data: Int, pointers: Int, mask: Int, width: Int in 1 ~ 8)(
      inline next: => Term.Steps
  ): Term.Steps = {
    val tpe       = Type.unsigned(Type.width(width.raw * 8))
    val addresses = leaves(pointers)
    val selected  = leaves(mask)
    val lanes     = leaves(data)
    Term.Steps.concat(Vec.from(lanes.indices).map(lane =>
      guarded(
        Some(Term.Binary("!=", selected(lane), Term.Literal(0L))),
        Term.context(
          Machine,
          MachineCall(
            "store",
            Vec(tpe),
            Vec((RenderMachine.space(Space.Generic), None), (addresses(lane), Some(Word)), (lanes(lane), Some(tpe)))
          ),
          None,
          effect = true
        )
      )
    )) ++ next
  }

  inline def convertSampledHandle(source: Int, target: Int, typeId: Int)(inline next: => Term.Steps): Term.Steps =
    lanewise(Images, "handledSampledImage", target, Vec(Term.Variable(name(source)))) ++ next

  inline def subArray(source: Int, index: Int, target: Int, typeId: Int)(inline next: => Term.Steps): Term.Steps = {
    val lanes = leaves(source)
    val count = scope.operands(target).size
    val at    = Term.Variable(name(index))
    inRange(at, lanes.size - count + 1, "OpExtractSubArrayQCOM index") ++ assigned(
      target,
      Vec.from(0 until count).map(lane =>
        indexed(if (lane == 0) at else Term.Binary("+", at, Term.Literal(lane.toLong)), lanes)
      )
    ) ++ next
  }

  inline def extractDynamic(vector: Int, index: Int, target: Int, typeId: Int)(
      inline next: => Term.Steps
  ): Term.Steps = {
    val lanes = leaves(vector)
    val at    = Term.Variable(name(index))
    inRange(at, lanes.size, "OpVectorExtractDynamic index") ++ assigned(target, Vec(indexed(at, lanes))) ++ next
  }

  inline def insertDynamic(vector: Int, component: Int, index: Int, target: Int, typeId: Int)(
      inline next: => Term.Steps
  ): Term.Steps = {
    val lanes  = leaves(vector)
    val at     = Term.Variable(name(index))
    val chosen = Term.Variable(name(component))
    inRange(at, lanes.size, "OpVectorInsertDynamic index") ++
      assigned(
        target,
        lanes.zipWithIndex.map((lane, position) =>
          Term.Select(Term.Binary("==", at, Term.Literal(position.toLong)), chosen, lane)
        )
      ) ++ next
  }

  inline def copyValue(source: Int, target: Int, typeId: Int)(inline next: => Term.Steps): Term.Steps =
    assigned(target, leaves(source)) ++ next

  inline def conditionalCopy(pairs: Array[Int], target: Int, typeId: Int)(inline next: => Term.Steps): Term.Steps = {
    val conditions = Vec.from(0 until pairs.length by 2).map(index => leaves(pairs(index)).head)
    statement(
      Machine,
      MachineCall(
        "require",
        Vec.empty,
        Vec(
          (conditions.reduceLeft[Term](Term.Binary("|", _, _)), Some(Type.Bool)),
          (Term.Constant("\"OpConditionalCopyObjectINTEL has no operand whose condition holds\""), None)
        )
      )
    ) ++ assigned(
      target,
      Vec.from(0 until leafCount(target)).map(lane =>
        (0 until pairs.length - 2 by 2).foldRight(leaves(pairs.last)(lane)) { (index, otherwise) =>
          Term.Select(leaves(pairs(index)).head, leaves(pairs(index + 1))(lane), otherwise)
        }
      )
    ) ++ next
  }

  inline def zero(target: Int, typeId: Int, opcode: String)(inline next: => Term.Steps): Term.Steps =
    assigned(target, Vec.fill[Term](scope.operands(target).size)(Term.Literal(0L))) ++ next

  inline def prefetch(pointer: Int)(inline next: => Term.Steps): Term.Steps = statement(
    Machine,
    MachineCall(
      "require",
      Vec.empty,
      Vec(
        (Term.context(Pointers, MachineCall("isAddress", Vec.empty, Vec(pointed(pointer))), Some(Type.Bool)), None),
        (Term.Constant(s"\"${name(pointer)} is an address\""), None)
      )
    )
  ) ++ next

  inline def readClock(scope: Int, target: Int, typeId: Int, halves: Boolean)(
      inline next: => Term.Steps
  ): Term.Steps = {
    val time = Term.context(Clock, MachineCall("readClock", Vec.empty, Vec(plain(scope))), Some(Word))
    if (halves) {
      val (bindings, count) = Term.Steps.hoisted(this.scope, time)
      bindings ++ assigned(target, Vec(Term.truncate(count, 32), Term.Binary(">>", count, Term.Literal(32L)))) ++ next
    } else binding(name(target), time) ++ next
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  inline def accessChain(base: Int, element: Int, indexes: Array[Int], chain: AccessChain, target: Int, typeId: Int)(
      inline next: => Term.Steps
  ): Term.Steps = AccessLayout.walk[Term, Term.Steps](
    chain = chain,
    start = chain.sample,
    element = element >= 0,
    count = indexes.length,
    origin = Term.Variable(name(base)),
    address = location => Term.Literal(location),
    number = position => chain.literals.lift(position).getOrElse(0L),
    index = position => Term.Variable(name(if (position < 0) element else indexes(position))),
    pending = () => null,
    plus = (offset, delta) => if (delta == 0L) offset else Term.Binary("+", offset, Term.Literal(delta)),
    scaled = (offset, selected, stride) => Term.Binary("+", offset, Term.Binary("*", selected, Term.Literal(stride))),
    failed = error =>
      statement(
        Machine,
        MachineCall(
          "require",
          Vec.empty,
          Vec((Term.Constant("false"), Some(Type.Bool)), (Term.Constant(s"\"${error.message}\""), None))
        )
      ),
    reached = (offset, _, _, _) => binding(name(target), offset) ++ next
  )

  inline def pointerToInteger(pointer: Int, mask: Long, target: Int, typeId: Int)(
      inline next: => Term.Steps
  ): Term.Steps =
    binding(
      name(target),
      if (mask == -1L) Term.Variable(name(pointer))
      else Term.Binary("&", Term.Variable(name(pointer)), Term.Literal(mask))
    ) ++ next

  inline def pointersToIntegers(source: Int, mask: Long, target: Int, typeId: Int)(
      inline next: => Term.Steps
  ): Term.Steps = assigned(target, leaves(source).map(Term.Binary("&", _, Term.Literal(mask)))) ++ next

  inline def integerToPointer(
      source: Int,
      pointee: Int,
      storageClass: Int,
      function: Boolean,
      target: Int,
      typeId: Int
  )(inline next: => Term.Steps): Term.Steps = binding(name(target), Term.Variable(name(source))) ++ next

  inline def integersToPointers(source: Int, lanes: Int, target: Int, typeId: Int)(
      inline next: => Term.Steps
  ): Term.Steps = assigned(target, leaves(source)) ++ next

  inline def castToGeneric(source: Int, cast: PointerCast, target: Int, typeId: Int)(
      inline next: => Term.Steps
  ): Term.Steps = binding(name(target), Term.Variable(name(source))) ++ next

  inline def castFromGeneric(source: Int, cast: PointerCast, target: Int, typeId: Int)(
      inline next: => Term.Steps
  ): Term.Steps = binding(
    name(target),
    compute(
      Addresses.Retained,
      Term.Variable(name(source)),
      Term.Literal(cast.storageClass.toLong),
      Term.Literal(if (cast.explicit) 1L else 0L)
    )
  ) ++ next

  private def lanewise(
      owner: (contextType: String, context: String),
      method: String,
      dest: Int,
      arguments: Vec[Term]
  ): Term.Steps = Term.Steps.concat(scope.operands(dest).zipWithIndex.map((found, lane) =>
    binding(found.name, call(owner, ContextCall(method, Term.Literal(lane.toLong) +: arguments, Some(found.tpe))))
  ))

  inline def image(instruction: Instruction): Term.Steps = renderImage(instruction)

  inline def scalarValue(target: Int, typeId: Int, value: Long)(inline next: => Term.Steps): Term.Steps =
    binding(name(target), Term.Literal(value)) ++ next

  inline def pipe(instruction: Instruction): Term.Steps =
    instruction match {
      case Instruction.WritePipe(dest = d, pipe = pipe, pointer = packet) =>
        valued(Pipe, "write", d, operands(pipe, packet))
      case Instruction.ReadPipe(dest = d, pipe = pipe, pointer = packet) =>
        valued(Pipe, "read", d, operands(pipe, packet))
      case Instruction.ReservedWritePipe(
            dest = d,
            pipe = pipe,
            reserveId = reserved,
            index = index,
            pointer = packet
          ) =>
        valued(Pipe, "reservedWrite", d, operands(pipe, reserved, index, packet))
      case Instruction.ReservedReadPipe(dest = d, pipe = pipe, reserveId = reserved, index = index, pointer = packet) =>
        valued(Pipe, "reservedRead", d, operands(pipe, reserved, index, packet))
      case Instruction.ReserveWritePipePackets(dest = d, pipe = pipe, numPackets = count) =>
        valued(Pipe, "reserveWrite", d, operands(pipe, count))
      case Instruction.ReserveReadPipePackets(dest = d, pipe = pipe, numPackets = count) =>
        valued(Pipe, "reserveRead", d, operands(pipe, count))
      case Instruction.GroupReserveWritePipePackets(dest = d, execution = group, pipe = pipe, numPackets = count) =>
        valued(Pipe, "groupReserveWrite", d, operands(group, pipe, count))
      case Instruction.GroupReserveReadPipePackets(dest = d, execution = group, pipe = pipe, numPackets = count) =>
        valued(Pipe, "groupReserveRead", d, operands(group, pipe, count))
      case Instruction.IsValidReserveId(_, d, reserved) => valued(Pipe, "isValidReserveId", d, operands(reserved))
      case Instruction.GetNumPipePackets(dest = d, pipe = pipe) => valued(Pipe, "numPackets", d, operands(pipe))
      case Instruction.GetMaxPipePackets(dest = d, pipe = pipe) => valued(Pipe, "maxPackets", d, operands(pipe))
      case Instruction.CreatePipeFromPipeStorage(_, d, storage) => valued(Pipe, "fromStorage", d, operands(storage))
      case _                                                    => Term.Steps.empty
    }

  inline def deviceEnqueue(instruction: Instruction): Term.Steps =
    instruction match {
      case Instruction.GetDefaultQueue(_, d)                     => valued(Enqueue, "defaultQueue", d, Vec.empty)
      case Instruction.CreateUserEvent(_, d)                     => valued(Enqueue, "createUserEvent", d, Vec.empty)
      case Instruction.BuildNDRange(_, d, global, local, offset) =>
        valued(Enqueue, "buildNDRange", d, operands(global, local, offset))
      case Instruction.EnqueueKernel(
            _,
            d,
            queue,
            flags,
            range,
            count,
            waits,
            returned,
            invoke,
            param,
            size,
            alignment,
            locals
          ) =>
        valued(
          Enqueue,
          "enqueueKernel",
          d,
          operands(queue, flags, range, count, waits, returned, invoke, param, size, alignment) ++
            locals.flatMap(operands(_))
        )
      case Instruction.EnqueueMarker(_, d, queue, count, waits, returned) =>
        valued(Enqueue, "enqueueMarker", d, operands(queue, count, waits, returned))
      case Instruction.RetainEvent(event)                => effect(Enqueue, "retainEvent", operands(event))
      case Instruction.ReleaseEvent(event)               => effect(Enqueue, "releaseEvent", operands(event))
      case Instruction.IsValidEvent(_, d, event)         => valued(Enqueue, "isValidEvent", d, operands(event))
      case Instruction.SetUserEventStatus(event, status) =>
        effect(Enqueue, "setUserEventStatus", operands(event, status))
      case Instruction.CaptureEventProfilingInfo(event, info, value) =>
        effect(Enqueue, "captureEventProfilingInfo", operands(event, info, value))
      case Instruction.GetKernelWorkGroupSize(_, d, invoke, param, size, alignment) =>
        valued(Enqueue, "kernelWorkGroupSize", d, operands(invoke, param, size, alignment))
      case Instruction.GetKernelPreferredWorkGroupSizeMultiple(_, d, invoke, param, size, alignment) =>
        valued(Enqueue, "kernelPreferredWorkGroupSizeMultiple", d, operands(invoke, param, size, alignment))
      case Instruction.GetKernelMaxNumSubgroups(_, d, invoke, param, size, alignment) =>
        valued(Enqueue, "kernelMaxNumSubgroups", d, operands(invoke, param, size, alignment))
      case Instruction.GetKernelLocalSizeForSubgroupCount(_, d, subgroups, invoke, param, size, alignment) =>
        valued(Enqueue, "kernelLocalSizeForSubgroupCount", d, operands(subgroups, invoke, param, size, alignment))
      case Instruction.GetKernelNDrangeSubGroupCount(_, d, range, invoke, param, size, alignment) =>
        valued(Enqueue, "kernelNDrangeSubGroupCount", d, operands(range, invoke, param, size, alignment))
      case Instruction.GetKernelNDrangeMaxSubGroupSize(_, d, range, invoke, param, size, alignment) =>
        valued(Enqueue, "kernelNDrangeMaxSubGroupSize", d, operands(range, invoke, param, size, alignment))
      case _ => Term.Steps.empty
    }

  inline def tensorAddressing(instruction: Instruction): Term.Steps = {
    import Operand.*
    instruction match {
      case Instruction.CreateTensorLayoutNV(_, d) => valued(Tensors, "createLayout", d, Vec.empty)
      case Instruction.TensorLayoutSetDimensionNV(_, d, layout, dimensions) =>
        valued(Tensors, "setLayoutDimension", d, operands(layout) ++ dimensions.flatMap(operands(_)))
      case Instruction.TensorLayoutSetStrideNV(_, d, layout, strides) =>
        valued(Tensors, "setLayoutStride", d, operands(layout) ++ strides.flatMap(operands(_)))
      case Instruction.TensorLayoutSetBlockSizeNV(_, d, layout, sizes) =>
        valued(Tensors, "setLayoutBlockSize", d, operands(layout) ++ sizes.flatMap(operands(_)))
      case Instruction.TensorLayoutSliceNV(_, d, layout, slices) =>
        valued(Tensors, "sliceLayout", d, operands(layout) ++ slices.flatMap(operands(_)))
      case Instruction.TensorLayoutSetClampValueNV(_, d, layout, value) =>
        valued(Tensors, "setLayoutClampValue", d, operands(layout, value))
      case Instruction.CreateTensorViewNV(_, d)                         => valued(Tensors, "createView", d, Vec.empty)
      case Instruction.TensorViewSetDimensionNV(_, d, view, dimensions) =>
        valued(Tensors, "setViewDimension", d, operands(view) ++ dimensions.flatMap(operands(_)))
      case Instruction.TensorViewSetStrideNV(_, d, view, strides) =>
        valued(Tensors, "setViewStride", d, operands(view) ++ strides.flatMap(operands(_)))
      case Instruction.TensorViewSetClipNV(_, d, view, rowOffset, rowSpan, columnOffset, columnSpan) =>
        valued(Tensors, "setViewClip", d, operands(view, rowOffset, rowSpan, columnOffset, columnSpan))
      case Instruction.CooperativeMatrixLoadTensorNV(_, d, pointer, source, tensorLayout, _, addressing) =>
        lanewise(
          Tensors,
          "tensorLoad",
          id(d),
          operands(pointer, source, tensorLayout) ++ modifiers(Some(addressing))
        )
      case Instruction.CooperativeMatrixStoreTensorNV(pointer, source, tensorLayout, _, addressing) =>
        effect(Tensors, "tensorStore", operands(pointer, source, tensorLayout) ++ modifiers(Some(addressing)))
      case _ => Term.Steps.empty
    }
  }

  inline def cooperativeVector(instruction: Instruction): Term.Steps = {
    import Operand.*
    instruction match {
      case Instruction.CooperativeVectorLoadNV(_, d, pointer, offset, _) =>
        lanewise(Fragment, "vectorLoad", id(d), operands(pointer, offset))
      case Instruction.CooperativeVectorStoreNV(pointer, offset, source, _) =>
        effect(Fragment, "vectorStore", operands(pointer, offset, source))
      case Instruction.CooperativeVectorOuterProductAccumulateNV(pointer, offset, a, b, layout, matrixKind, stride) =>
        effect(
          Fragment,
          "outerProductAccumulate",
          operands(pointer, offset, a, b, layout, matrixKind) ++ stride.fold(Vec.empty[Term])(operands(_))
        )
      case Instruction.CooperativeVectorReduceSumAccumulateNV(pointer, offset, source) =>
        effect(Fragment, "reduceSumAccumulate", operands(pointer, offset, source))
      case _ => Term.Steps.empty
    }
  }

  private def operands(ids: Operand*): Vec[Term] = Vec.from(ids).flatMap(operand => leaves(Operand.id(operand)))

  private def modifiers(mods: Option[Operand]): Vec[Term] = mods.fold(Vec.empty[Term])(operand =>
    Vec.from(operand.words.toVector).zipWithIndex.flatMap((word, position) =>
      if (position == 0) Vec[Term](Term.Literal(word.toUnsigned.toLong)) else leaves(word.raw)
    )
  )

  private def renderImage(instruction: Instruction): Term.Steps = {
    import Operand.*
    def read(method: String, dest: Operand, arguments: Vec[Term]) = lanewise(Images, method, id(dest), arguments)
    instruction match {
      case Instruction.SampledImage(_, d, i, s)              => read("sampledImage", d, operands(i, s))
      case Instruction.Image(_, d, s)                        => read("image", d, operands(s))
      case Instruction.ImageSampleImplicitLod(_, d, i, c, m) =>
        read("sampleImplicit", d, operands(i, c) ++ modifiers(m))
      case Instruction.ImageSampleExplicitLod(_, d, i, c, m) =>
        read("sampleExplicit", d, operands(i, c) ++ modifiers(Some(m)))
      case Instruction.ImageSampleDrefImplicitLod(_, d, i, c, r, m) =>
        read("sampleDrefImplicit", d, operands(i, c, r) ++ modifiers(m))
      case Instruction.ImageSampleDrefExplicitLod(_, d, i, c, r, m) =>
        read("sampleDrefExplicit", d, operands(i, c, r) ++ modifiers(Some(m)))
      case Instruction.ImageSampleProjImplicitLod(_, d, i, c, m) =>
        read("sampleProjImplicit", d, operands(i, c) ++ modifiers(m))
      case Instruction.ImageSampleProjExplicitLod(_, d, i, c, m) =>
        read("sampleProjExplicit", d, operands(i, c) ++ modifiers(Some(m)))
      case Instruction.ImageSampleProjDrefImplicitLod(_, d, i, c, r, m) =>
        read("sampleProjDrefImplicit", d, operands(i, c, r) ++ modifiers(m))
      case Instruction.ImageSampleProjDrefExplicitLod(_, d, i, c, r, m) =>
        read("sampleProjDrefExplicit", d, operands(i, c, r) ++ modifiers(Some(m)))
      case Instruction.ImageGather(_, d, i, c, component, m) =>
        read("gather", d, operands(i, c, component) ++ modifiers(m))
      case Instruction.ImageDrefGather(_, d, i, c, r, m)     => read("drefGather", d, operands(i, c, r) ++ modifiers(m))
      case Instruction.ImageQueryLod(_, d, i, c)             => read("queryLod", d, operands(i, c))
      case Instruction.ImageQueryLevels(_, d, i)             => read("queryLevels", d, operands(i))
      case Instruction.ImageQuerySizeLod(_, d, i, lod)       => read("querySizeLod", d, operands(i, lod))
      case Instruction.ImageQuerySize(_, d, i)               => read("querySize", d, operands(i))
      case Instruction.ImageQuerySamples(_, d, i)            => read("querySamples", d, operands(i))
      case Instruction.ImageQueryOrder(_, d, i)              => read("queryOrder", d, operands(i))
      case Instruction.ImageQueryFormat(_, d, i)             => read("queryFormat", d, operands(i))
      case Instruction.ImageFetch(_, d, i, c, m)             => read("fetch", d, operands(i, c) ++ modifiers(m))
      case Instruction.ImageRead(_, d, i, c, m)              => read("read", d, operands(i, c) ++ modifiers(m))
      case Instruction.ImageTexelPointer(_, d, i, c, sample) => read("texelPointer", d, operands(i, c, sample))
      case Instruction.ImageWrite(i, c, texel, m)            =>
        effect(Images, "write", operands(i, c, texel) ++ modifiers(m))
      case _ => Term.Steps.empty
    }
  }

  inline def arrayLength(structure: Int, member: Int, declared: Option[Int], target: Int, typeId: Int)(
      inline next: => Term.Steps
  ): Term.Steps = binding(
    name(target),
    Term.context(
      Pointers,
      MachineCall("arrayLength", Vec.empty, Vec(pointed(structure), (Term.Literal(member.toLong), None))),
      Some(Word)
    )
  ) ++ next
}
