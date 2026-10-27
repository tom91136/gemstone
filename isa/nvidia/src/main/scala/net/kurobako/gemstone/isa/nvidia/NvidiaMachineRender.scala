package net.kurobako.gemstone.isa.nvidia

import TensorMapping.*
import VMExecution.*
import VMIR.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.{Machine => NvidiaState}
import net.kurobako.gemstone.semantics.render.*
import net.kurobako.gemstone.semantics.render.RenderMachine.*
import net.kurobako.gemstone.vm.*
import scala.collection.mutable

private[isa] object NvidiaMachineRender {
  val Cluster: (contextType: String, context: String)  = ("net.kurobako.gemstone.isa.nvidia.Cluster", "cluster")
  val Barriers: (contextType: String, context: String) = ("net.kurobako.gemstone.isa.nvidia.Barriers", "barriers")
  val Frames: (contextType: String, context: String)   = ("net.kurobako.gemstone.isa.nvidia.Frames", "frames")
  val Units: (contextType: String, context: String)    = ("net.kurobako.gemstone.isa.nvidia.Units", "units")
  val Tensors: (contextType: String, context: String)  = ("net.kurobako.gemstone.isa.nvidia.Tensors", "tensors")
  val Memory: (contextType: String, context: String)   = NvidiaState.memoryContext

  private def cluster(method: String, result: Type, arguments: Vec[Term]): Term =
    Term.context(Cluster, MachineCall(method, Vec.empty, arguments.map((_, Some(Word)))), Some(result))

  private def slots(parameters: Vec[Parameter]): Term =
    Term.Constant(parameters.map(_.raw).mkString("Seq(", ", ", ")"))

  private def locations(targets: Vec[Pc]): Term =
    Term.Constant(targets.map(_.toInt).mkString("Seq(", ", ", ")"))

  private def operationTerm(operation: AtomicOperation): Term =
    Term.Constant(s"net.kurobako.gemstone.vm.AtomicOperation.$operation")

  private def reductionName(operator: TensorMap.Reduction): String =
    s"net.kurobako.gemstone.isa.nvidia.TensorMap.Reduction.$operator"

  private def reductionTerm(operator: TensorMap.Reduction): Term = Term.Constant(reductionName(operator))

  private def elementTerm(element: TensorMap.Element): Term =
    Term.Constant(s"net.kurobako.gemstone.isa.nvidia.TensorMap.Element.$element")

  private def layoutTerm(name: String): Term =
    Term.Constant(s"net.kurobako.gemstone.isa.nvidia.TensorLayout.$name")

  private def tensorOperationTerm(operation: TensorMap.Operation): Term = operation match {
    case TensorMap.Operation.Reduce(operator) =>
      Term.Constant(s"net.kurobako.gemstone.isa.nvidia.TensorMap.Operation.Reduce(${reductionName(operator)})")
    case other => Term.Constant(s"net.kurobako.gemstone.isa.nvidia.TensorMap.Operation.$other")
  }

  private def sharedCheck(address: Term, bytes: Term, what: String): Term.Steps =
    statement(
      Machine,
      MachineCall(
        "require",
        Vec.empty,
        Vec(
          (cluster("shared", Type.Bool, Vec(address, bytes)), Some(Type.Bool)),
          (Term.Constant(s"\"$what\""), None)
        )
      )
    )

  private def barrierCheck(barrier: Term): Term.Steps = sharedCheck(barrier, Term.Literal(8L), "mbarrier")

  private val IR = "net.kurobako.gemstone.isa.nvidia.VMIR"

  /** A tcgen05.mma product as a term, with the term of the operand enabling D to accumulate, and the product its
    * instruction descriptor decodes to where that is an immediate.
    */
  case class RenderedProduct(
      term: Term,
      enable: Term,
      descriptor: Term,
      operation: Tcgen05.Multiply,
      shape: Option[TensorMemory.Product]
  )

  private def shape(operation: Tcgen05.Multiply): Option[TensorMemory.Product] = operation.descriptor match {
    case Value.Immediate(bits) => TensorMemory.product(operation, bits).toOption
    case _                     => None
  }

  private def reductionText(reduction: BarrierReduction): String = {
    val result = reduction.result match {
      case BarrierResult.Combined(destination, operation) =>
        s"$IR.BarrierResult.Combined($IR.Predicate(${destination.raw}), $IR.PredicateOperation.$operation)"
      case BarrierResult.Count(destination) => s"$IR.BarrierResult.Count($IR.Register(${destination.raw}))"
    }
    s"(result = $result, predicate = $IR.Predicate(${reduction.predicate.raw}), negated = ${reduction.negated})"
  }
}

/** Renders NVIDIA machine operations as statements for a [[Definition]] over the operands `names` gives, under `guard`,
  * if the instruction has one.
  */
private[isa] class NvidiaMachineRender(names: Names, guard: Option[Term])
    extends RenderMachine[Value, Register, Pc, VMExecution.Step](
      names.value,
      names.register,
      pc => Term.Literal(pc.toInt.toLong),
      () => names.temporary(),
      guard
    ) with NvidiaMachine[Term, Unit, Term, Term, RenderMachine.Rendered] {
  import NvidiaMachineRender.*

  inline def explicitCluster: Term = names.value(Value.ExplicitCluster)

  inline def clusterSize: Term                               = cluster("clusterSize", Word, Vec.empty)
  inline def ctaRank: Term                                   = cluster("ctaRank", Word, Vec.empty)
  inline def rankOf(address: Term): Term                     = cluster("rankOf", Word, Vec(address))
  inline def clusterAddress(address: Term, rank: Term): Term = cluster("clusterAddress", Word, Vec(address, rank))

  inline def inCluster(address: Term, bytes: Term): Term =
    Term.context(
      Cluster,
      MachineCall("inCluster", Vec.empty, Vec((address, Some(Word)), (bytes, Some(Word)))),
      Some(Type.Bool)
    )

  inline def sharedWindow(lane: Unit, address: Term, bytes: Term, what: String)(
      inline next: (Term, Term) => Term.Steps
  ): Term.Steps =
    sharedCheck(address, bytes, what) ++
      next(cluster("owner", Word, Vec(address)), cluster("offset", Word, Vec(address)))

  inline def requireShared(address: Term, what: String)(inline next: => Term.Steps): Term.Steps =
    sharedCheck(address, Term.Literal(1L), what) ++ next

  inline def eachDestination(lane: Unit, cta: Term, multicast: Option[Value])(
      inline each: Term => Term.Steps
  )(inline next: => Term.Steps): Term.Steps = multicast match {
    case None       => each(cta) ++ next
    case Some(mask) =>
      RenderMachine.each(
        "cta",
        Term.context(Cluster, MachineCall("destinations", Vec.empty, Vec((names.value(mask), Some(Word)))), None),
        each(Term.Variable("cta"))
      ) ++ next
  }

  inline def eachRank(inline each: Term => Term.Steps): Term.Steps =
    RenderMachine.each(
      "rank",
      Term.context(Cluster, MachineCall("ranks", Vec.empty, Vec.empty), None),
      each(Term.Variable("rank"))
    )

  inline def storeShared(address: Term, value: Term, width: ScalarBitWidth)(inline next: => Term.Steps): Term.Steps = {
    val tpe = Type.unsigned(Type.width(width.raw))
    statement(Cluster, MachineCall("storeShared", Vec(tpe), Vec((address, Some(Word)), (value, Some(tpe))))) ++ next
  }

  inline def storeSharedBytes(address: Term, data: Term, size: Term)(inline next: => Term.Steps): Term.Steps =
    statement(
      Cluster,
      MachineCall("storeSharedBytes", Vec.empty, Vec((address, Some(Word)), (data, None), (size, Some(Word))))
    ) ++
      next

  inline def storeCluster(cta: Term, offset: Term, data: Term)(inline next: => Term.Steps): Term.Steps =
    statement(Cluster, MachineCall("store", Vec.empty, Vec((cta, None), (offset, Some(Word)), (data, None)))) ++ next

  inline def completeTx(cta: Term, barrier: Term, bytes: Term)(inline next: => Term.Steps): Term.Steps =
    statement(
      Cluster,
      MachineCall("completeTx", Vec.empty, Vec((cta, None), (barrier, Some(Word)), (bytes, Some(Word))))
    ) ++ next

  inline def clusterBarrier(arrive: Boolean): Term.Steps =
    guarded(
      guard,
      Term.context(
        Cluster,
        MachineCall("barrier", Vec.empty, Vec((Term.Constant(arrive.toString), None))),
        None,
        effect = true
      )
    )

  inline def agreed(lane: Unit, view: Term): Term =
    Term.context(Units, MachineCall("agreed", Vec.empty, Vec((view, Some(Word)))), Some(Type.Bool))

  inline def sameCta(first: Term, second: Term): Term =
    Term.context(Cluster, MachineCall("same", Vec.empty, Vec((first, None), (second, None))), Some(Type.Bool))

  inline def clearCluster(cta: Term, offset: Term, bytes: Term)(inline next: => Term.Steps): Term.Steps =
    statement(Cluster, MachineCall("clear", Vec.empty, Vec((cta, None), (offset, Some(Word)), (bytes, Some(Word))))) ++
      next

  inline def storeWord(cta: Term, offset: Term, value: Term, width: ScalarBitWidth)(
      inline next: => Term.Steps
  ): Term.Steps = {
    val tpe = Type.unsigned(Type.width(width.raw))
    statement(
      Cluster,
      MachineCall("storeWord", Vec(tpe), Vec((cta, None), (offset, Some(Word)), (value, Some(tpe))))
    ) ++ next
  }

  inline def reduceWord(cta: Term, offset: Term, operand: Term, width: ScalarBitWidth, operation: AtomicOperation)(
      inline next: => Term.Steps
  ): Term.Steps = {
    val tpe = Type.unsigned(Type.width(width.raw))
    statement(
      Cluster,
      MachineCall(
        "reduceWord",
        Vec(tpe),
        Vec((cta, None), (offset, Some(Word)), (operand, Some(tpe)), (operationTerm(operation), None))
      )
    ) ++ next
  }

  inline def reduceBytes(cta: Term, offset: Term, data: Term, copy: Instruction.AsyncBulkReduce)(
      inline next: => Term.Steps
  ): Term.Steps = statement(
    Cluster,
    MachineCall(
      "reduceBytes",
      Vec.empty,
      Vec(
        (cta, None),
        (offset, Some(Word)),
        (data, None),
        (reductionTerm(copy.operator), None),
        (elementTerm(copy.element), None)
      )
    )
  ) ++ next

  inline def reduceGlobal(lane: Unit, address: Term, data: Term, copy: Instruction.AsyncBulkReduce)(
      inline next: => Term.Steps
  ): Term.Steps = statement(
    Memory,
    MachineCall(
      "reduceGlobal",
      Vec.empty,
      Vec((address, Some(Word)), (data, None), (reductionTerm(copy.operator), None), (elementTerm(copy.element), None))
    )
  ) ++ next

  inline def storeGlobalMasked(lane: Unit, destination: Term, source: Term, bytes: Term, mask: Term)(
      inline next: => Term.Steps
  ): Term.Steps = statement(
    Memory,
    MachineCall(
      "storeGlobalMasked",
      Vec.empty,
      Vec((destination, Some(Word)), (source, Some(Word)), (bytes, Some(Word)), (mask, Some(Word)))
    )
  ) ++ next

  private val transfers = mutable.HashMap.empty[String, TensorMappingRender.Transfer]

  inline def tensorTransfer(map: Term, coordinates: Vec[Term], offsets: Vec[Term], copy: Instruction.AsyncTensor)(
      inline next: Term => Term.Steps
  ): Term.Steps = {
    val transfer = names.temporary()
    transfers(transfer) = TensorMappingRender.Transfer(map, coordinates, offsets, copy)
    binding(
      transfer,
      Term.context(
        Tensors,
        MachineCall(
          "transfer",
          Vec.empty,
          Vec(
            (map, None),
            (Term.Constant(copy.im2col.toString), None),
            (Term.Constant(copy.gather.toString), None),
            (tensorOperationTerm(copy.operation), None),
            (Term.Constant(coordinates.size.toString), None)
          ) ++ (coordinates ++ offsets).map((_, Some(Word)))
        ),
        None
      )
    ) ++ next(Term.Variable(transfer))
  }

  inline def tensorFact(transfer: Term, fact: TensorLayout.Fact): Term =
    Term.context(
      Tensors,
      MachineCall("fact", Vec.empty, Vec((transfer, None), (layoutTerm(s"Fact.$fact"), None))),
      Some(Word)
    )

  def tensorField(map: Term, kind: TensorMapping.Field, axis: Int): Term =
    TensorMappingRender.field(Tensors, map, kind, axis)

  inline def tensorText(transfer: Term, text: TensorLayout.Text): String = ""

  inline def eachIndex(count: Term)(inline each: Term => Term.Steps)(inline next: => Term.Steps): Term.Steps =
    RenderMachine.each(
      "run",
      Term.context(Tensors, MachineCall("indices", Vec.empty, Vec((count, Some(Word)))), None),
      each(Term.Variable("run"))
    ) ++ next

  inline def eachRun(transfer: Term)(inline each: Term => Term.Steps)(inline next: => Term.Steps): Term.Steps =
    (transfer match {
      case Term.Variable(name) =>
        transfers.get(name).fold(Term.Steps.empty)(TensorMappingRender.runs(names, Tensors, transfer, _)(each))
      case _ => Term.Steps.empty
    }) ++ next

  private def runArguments(transfer: Term, run: Term): Vec[(Term, Option[Type])] = run match {
    case Term.Call(_, Vec(offset, address, count, inside)) =>
      Vec((transfer, None), (offset, Some(Word)), (address, Some(Word)), (count, Some(Word)), (inside, Some(Type.Bool)))
    case other => Vec((transfer, None), (other, Some(Word)))
  }

  inline def loadRun(lane: Unit, transfer: Term, run: Term)(inline next: => Term.Steps): Term.Steps =
    statement(
      Tensors,
      MachineCall(
        "loadRun",
        Vec.empty,
        runArguments(transfer, run) :+ ((tensorFact(transfer, TensorLayout.Fact.Fill), Some(Word)))
      )
    ) ++ next

  inline def storeRun(lane: Unit, transfer: Term, run: Term)(inline next: => Term.Steps): Term.Steps =
    statement(Tensors, MachineCall("storeRun", Vec.empty, runArguments(transfer, run))) ++ next

  inline def reduceRun(lane: Unit, transfer: Term, run: Term, operator: TensorMap.Reduction)(
      inline next: => Term.Steps
  ): Term.Steps =
    statement(
      Tensors,
      MachineCall("reduceRun", Vec.empty, runArguments(transfer, run) :+ ((reductionTerm(operator), None)))
    ) ++ next

  private def moveChunks(method: String, cta: Term, start: Term, transfer: Term): Term.Steps = {
    val prelude                  = Vec.newBuilder[Term.Step]
    def bound(value: Term): Term = {
      val name = names.temporary()
      names.keep(name)
      prelude += Term.Step(name, value)
      Term.Variable(name)
    }
    def fact(which: TensorLayout.Fact): Term = tensorFact(transfer, which)
    val swizzle                              = bound(fact(TensorLayout.Fact.Swizzle))
    val atomicity                            = bound(fact(TensorLayout.Fact.Atomicity))
    val bytes                                = bound(fact(TensorLayout.Fact.Bytes))
    val size                                 = bound(Render.chunk(swizzle, atomicity))
    val loop                                 = names.temporary()
    val index                                = Term.Variable(loop)
    val at                                   = names.temporary()
    names.keep(at)
    val body = Term.Steps(Term.Step(at, Render.multiply(index, size))) ++ statement(
      Cluster,
      MachineCall(
        method,
        Vec.empty,
        Vec(
          (cta, None),
          (TensorMapping.placed(Render, swizzle, atomicity, start, Term.Variable(at)), Some(Word)),
          (transfer, None),
          (Term.Variable(at), Some(Word)),
          (Render.length(bytes, Term.Variable(at), size), Some(Word))
        )
      )
    )
    Term.Steps(prelude.result()) ++ RenderMachine.each(
      loop,
      Term.context(Tensors, MachineCall("indices", Vec.empty, Vec((Render.chunks(bytes, size), Some(Word)))), None),
      body
    )
  }

  inline def placeBox(cta: Term, start: Term, transfer: Term)(inline next: => Term.Steps): Term.Steps =
    moveChunks("placeChunk", cta, start, transfer) ++ next

  inline def fetchBox(cta: Term, start: Term, transfer: Term)(inline next: => Term.Steps): Term.Steps =
    moveChunks("fetchChunk", cta, start, transfer) ++ next

  inline def peerRank(cta: Term, signalled: Term): Term =
    Term.context(Cluster, MachineCall("peerRank", Vec.empty, Vec((cta, None), (signalled, None))), Some(Word))

  inline def hasCta(rank: Term): Term =
    Term.context(Cluster, MachineCall("present", Vec.empty, Vec((rank, Some(Word)))), Some(Type.Bool))

  inline def ctaAt(rank: Term): Term =
    Term.context(Cluster, MachineCall("at", Vec.empty, Vec((rank, Some(Word)))), None)

  inline def replaceTensorMap(map: Term, field: TensorMap.Field, index: Int, value: Term)(
      inline next: Term => Term.Steps
  ): Term.Steps = {
    val updated = names.temporary()
    binding(
      updated,
      Term.context(
        Memory,
        MachineCall(
          "replaceTensorMap",
          Vec.empty,
          Vec(
            (map, None),
            (Term.Constant(s"net.kurobako.gemstone.isa.nvidia.TensorMap.Field.$field"), None),
            (Term.Constant(index.toString), None),
            (value, Some(Word))
          )
        ),
        None
      )
    ) ++ next(Term.Variable(updated))
  }

  inline def mbarrierInit(lane: Unit, barrier: Term, count: Term)(inline next: => Term.Steps): Term.Steps =
    barrierCheck(barrier) ++
      statement(Barriers, MachineCall("init", Vec.empty, Vec((barrier, Some(Word)), (count, Some(Word))))) ++
      next

  inline def mbarrierInvalidate(lane: Unit, barrier: Term)(inline next: => Term.Steps): Term.Steps =
    barrierCheck(barrier) ++ statement(Barriers, MachineCall("invalidate", Vec.empty, Vec((barrier, Some(Word))))) ++
      next

  inline def mbarrierLoad(lane: Unit, barrier: Term)(inline next: => Term.Steps): Term.Steps =
    barrierCheck(barrier) ++ statement(Barriers, MachineCall("load", Vec.empty, Vec((barrier, Some(Word))))) ++ next

  inline def mbarrierArrive(lane: Unit, barrier: Term, count: Term, bytes: Term, drop: Boolean, complete: Boolean)(
      inline next: Term => Term.Steps
  ): Term.Steps = {
    val token = names.temporary()
    barrierCheck(barrier) ++ binding(
      token,
      Term.context(
        Barriers,
        MachineCall(
          "arrive",
          Vec.empty,
          Vec(
            (barrier, Some(Word)),
            (count, Some(Word)),
            (bytes, Some(Word)),
            (Term.Constant(drop.toString), None),
            (Term.Constant(complete.toString), None)
          )
        ),
        Some(Word),
        effect = true
      )
    ) ++ next(Term.Variable(token))
  }

  inline def mbarrierTransaction(lane: Unit, barrier: Term, bytes: Term, complete: Boolean)(
      inline next: => Term.Steps
  ): Term.Steps =
    barrierCheck(barrier) ++ statement(
      Barriers,
      MachineCall(
        "transaction",
        Vec.empty,
        Vec((barrier, Some(Word)), (bytes, Some(Word)), (Term.Constant(complete.toString), None))
      )
    ) ++ next

  inline def mbarrierTest(lane: Unit, barrier: Term, phase: Term, parity: Boolean)(
      inline next: Term => Term.Steps
  ): Term.Steps = {
    val completed = names.temporary()
    barrierCheck(barrier) ++ binding(
      completed,
      Term.context(
        Barriers,
        MachineCall(
          "test",
          Vec.empty,
          Vec((barrier, Some(Word)), (phase, Some(Word)), (Term.Constant(parity.toString), None))
        ),
        Some(Type.Bool)
      )
    ) ++ next(Term.Variable(completed))
  }

  inline def writePredicate(lane: Unit, target: Predicate, value: Term)(inline next: => Term.Steps): Term.Steps =
    binding(names.predicate(target), value) ++ next

  inline def call(function: Value, returns: Vec[Parameter], parameters: Vec[Parameter]): Term.Steps =
    guarded(
      guard,
      Term.context(
        Frames,
        MachineCall(
          "call",
          Vec.empty,
          Vec((names.value(function), Some(Word)), (slots(returns), None), (slots(parameters), None))
        ),
        None,
        effect = true
      )
    )

  inline def returnControl: Term.Steps =
    guarded(guard, Term.context(Frames, MachineCall("returnControl", Vec.empty, Vec.empty), None, effect = true))

  inline def fenceHost: Term.Steps =
    guarded(guard, Term.context(Memory, MachineCall("fence", Vec.empty, Vec.empty), None, effect = true))

  inline def branchTable(index: Value, targets: Vec[Pc]): Term.Steps =
    guarded(
      guard,
      Term.context(
        Frames,
        MachineCall("branchTable", Vec.empty, Vec((names.value(index), Some(Word)), (locations(targets), None))),
        None,
        effect = true
      )
    )

  inline def trap: Term.Steps =
    guarded(
      guard,
      Term.context(
        Machine,
        MachineCall("require", Vec.empty, Vec((Term.Constant("false"), None), (Term.Constant("\"trap\""), None))),
        None,
        effect = true
      )
    )

  inline def callBuiltin(builtin: Builtin, returns: Vec[Parameter], parameters: Vec[Parameter])(
      inline next: => Term.Steps
  ): Term.Steps =
    guarded(
      guard,
      Term.context(
        Frames,
        MachineCall(
          "builtin",
          Vec.empty,
          Vec(
            (Term.Constant(s"net.kurobako.gemstone.isa.nvidia.VMIR.Builtin.$builtin"), None),
            (slots(returns), None),
            (slots(parameters), None)
          )
        ),
        None,
        effect = true
      )
    ) ++ next

  inline def setupConvergence(token: Register)(inline next: => Term.Steps): Term.Steps =
    everyLane(lane =>
      write(lane, token, Term.context(Barriers, MachineCall("setup", Vec.empty, Vec.empty), Some(Word)))(done)
    )(next)

  inline def synchroniseConvergence(token: Register): Term.Steps =
    guarded(
      guard,
      Term.context(
        Barriers,
        MachineCall("synchronise", Vec.empty, Vec((read((), Value.Register(token)), Some(Word)))),
        None,
        effect = true
      )
    )

  inline def leaveConvergence(token: Register)(inline next: => Term.Steps): Term.Steps =
    everyLane(lane =>
      statement(Barriers, MachineCall("leave", Vec.empty, Vec((read(lane, Value.Register(token)), Some(Word)))))
    )(next)

  inline def parkReduce(barrier: Term, count: Term, reduction: BarrierReduction): Term.Steps =
    guarded(
      guard,
      Term.context(
        Barriers,
        MachineCall(
          "parkReduce",
          Vec.empty,
          Vec((barrier, Some(Word)), (count, Some(Word)), (Term.Constant(reductionText(reduction)), None))
        ),
        None,
        effect = true
      )
    )

  inline def parkEach(
      inline barrier: Unit => Term,
      inline count: Unit => Term,
      inline blocking: Boolean,
      reduction: Option[BarrierReduction]
  ): Term.Steps = guarded(
    guard,
    Term.context(
      Barriers,
      MachineCall(
        "parkEach",
        Vec.empty,
        Vec(
          (barrier(()), Some(Word)),
          (count(()), Some(Word)),
          (Term.Constant(blocking.toString), None),
          (Term.Constant(reduction.fold("None")(reduced => s"Some(${reductionText(reduced)})")), None)
        )
      ),
      None,
      effect = true
    )
  )

  inline def warpSync(memberMask: Value): Term.Steps =
    guarded(
      guard,
      Term.context(
        Barriers,
        MachineCall("warpSync", Vec.empty, Vec((names.value(memberMask), Some(Word)))),
        None,
        effect = true
      )
    )

  inline def stackPointer(lane: Unit): Term =
    Term.context(Frames, MachineCall("stackPointer", Vec.empty, Vec.empty), Some(Word))

  inline def allocate(lane: Unit, size: Term, alignment: Term)(inline next: Term => Term.Steps): Term.Steps = {
    val pointer = names.temporary()
    binding(
      pointer,
      Term.context(
        Frames,
        MachineCall("allocate", Vec.empty, Vec((size, Some(Word)), (alignment, Some(Word)))),
        Some(Word),
        effect = true
      )
    ) ++ next(Term.Variable(pointer))
  }

  inline def restoreStack(lane: Unit, pointer: Term)(inline next: => Term.Steps): Term.Steps =
    statement(Frames, MachineCall("restoreStack", Vec.empty, Vec((pointer, Some(Word))))) ++ next

  inline def pushReturn(lane: Unit, address: Term)(inline next: => Term.Steps): Term.Steps =
    statement(Frames, MachineCall("pushReturn", Vec.empty, Vec((address, Some(Word))))) ++ next

  inline def unitIndex(lane: Unit): Term =
    Term.context(Units, MachineCall("unitIndex", Vec.empty, Vec.empty), Some(Word))

  inline def when(condition: Term)(inline body: => Term.Steps)(inline next: => Term.Steps): Term.Steps = {
    val inner = body
    (condition match {
      case _ if inner.all.isEmpty => Term.Steps.empty
      case Term.Literal(1L)       => inner
      case _                      => Term.Steps(Term.Step("machine", Term.When(condition, inner.all)))
    }) ++ next
  }

  inline def withCta[A](rank: Term)(inline next: Term => Term.Steps): Term.Steps = {
    val cta = names.temporary()
    binding(cta, Term.context(Cluster, MachineCall("at", Vec.empty, Vec((rank, Some(Word)))), None)) ++
      next(Term.Variable(cta))
  }

  inline def valueText(text: TensorMemoryLayout.Text, value: Term): String = ""

  inline def tensorMemoryPermitted(cta: Term): Term =
    Term.context(Cluster, MachineCall("tensorMemoryPermitted", Vec.empty, Vec((cta, None))), Some(Type.Bool))

  inline def tensorMemoryTaken(cta: Term): Term =
    Term.context(Cluster, MachineCall("tensorMemoryTaken", Vec.empty, Vec((cta, None))), Some(Word))

  inline def relinquishTensorMemory(cta: Term)(inline next: => Term.Steps): Term.Steps =
    guarded(
      guard,
      Term.context(Cluster, MachineCall("relinquishTensorMemory", Vec.empty, Vec((cta, None))), None, effect = true)
    ) ++
      next

  inline def allocateTensorMemory(cta: Term, granules: Term)(inline next: Term => Term.Steps): Term.Steps = {
    val start = names.temporary()
    binding(
      start,
      Term.context(
        Cluster,
        MachineCall("allocateTensorMemory", Vec.empty, Vec((cta, None), (granules, Some(Word)))),
        Some(Word),
        effect = true
      )
    ) ++ next(Term.Variable(start))
  }

  inline def freeTensorMemory(cta: Term, mask: Term)(inline next: Term => Term.Steps): Term.Steps = {
    val freed = names.temporary()
    binding(
      freed,
      Term.context(
        Cluster,
        MachineCall("freeTensorMemory", Vec.empty, Vec((cta, None), (mask, Some(Word)))),
        Some(Type.Bool),
        effect = true
      )
    ) ++ next(Term.Variable(freed))
  }

  inline def tensorMemoryCell(cta: Term, lane: Term, column: Term): Term =
    Term.context(
      Cluster,
      MachineCall("tensorMemoryCell", Vec.empty, Vec((cta, None), (lane, Some(Word)), (column, Some(Word)))),
      Some(Word)
    )

  inline def storeTensorMemory(cta: Term, lane: Term, column: Term, value: Term)(
      inline next: => Term.Steps
  ): Term.Steps =
    statement(
      Cluster,
      MachineCall(
        "storeTensorMemory",
        Vec.empty,
        Vec((cta, None), (lane, Some(Word)), (column, Some(Word)), (value, Some(Word)))
      )
    ) ++ next

  inline def storeTensorWords(cta: Term, lane: Term, column: Term, data: Term)(
      inline next: => Term.Steps
  ): Term.Steps =
    statement(
      Cluster,
      MachineCall(
        "storeTensorWords",
        Vec.empty,
        Vec((cta, None), (lane, Some(Word)), (column, Some(Word)), (data, None))
      )
    ) ++ next

  inline def shiftTensorMemory(cta: Term, lane: Term, column: Term)(inline next: => Term.Steps): Term.Steps =
    statement(
      Cluster,
      MachineCall(
        "shiftTensorMemory",
        Vec.empty,
        Vec(
          (cta, None),
          (lane, Some(Word)),
          (column, Some(Word)),
          (Term.Constant("32"), None),
          (Term.Constant("8"), None)
        )
      )
    ) ++ next

  type Gathered = RenderedProduct

  inline def gatherProduct(lane: Unit, operation: Tcgen05.Multiply)(inline next: Gathered => Term.Steps)
      : Term.Steps = {
    val product  = names.temporary()
    val optional = Vec.from(operation.metadata) ++
      Vec.from(operation.scales).flatMap(scales => Vec(scales.a, scales.b)) ++
      Vec.from(operation.zeroColumns) ++ operation.disabled
    binding(
      product,
      Term.context(
        Tensors,
        MachineCall(
          "product",
          Vec.empty,
          Vec(
            (Term.Constant(s"$IR.Tcgen05.Kind.${operation.kind}"), None),
            (Term.Constant(operation.pair.toString), None),
            (Term.Constant(operation.ws.toString), None),
            (Term.Constant(operation.shiftA.toString), None),
            (Term.Constant(operation.shift.toString), None),
            (names.value(operation.descriptor), Some(Word)),
            (names.value(operation.d), Some(Word)),
            (names.value(operation.a), Some(Word)),
            (names.value(operation.b), Some(Word)),
            (names.value(operation.enable), Some(Word))
          ) ++ optional.map(value => (names.value(value), Some(Word)))
        ),
        None
      )
    ) ++ next(
      RenderedProduct(
        Term.Variable(product),
        names.value(operation.enable),
        names.value(operation.descriptor),
        operation,
        shape(operation)
      )
    )
  }

  inline def elementCount(product: Gathered): Term =
    Term.context(Tensors, MachineCall("size", Vec.empty, Vec((product.term, None))), Some(Word))

  inline def computeElement(product: Gathered, index: Term)(inline next: Term => Term.Steps): Term.Steps =
    product.shape match {
      case Some(shape) =>
        val (steps, value) = TensorMemoryRender.element(names, Tensors, product.term, product.enable, shape, index)
        steps ++ next(value)
      case None =>
        val (prelude, classes) =
          TensorMemoryRender.dispatch(
            names,
            Tensors,
            product.term,
            product.enable,
            product.descriptor,
            product.operation,
            index
          )
        prelude ++ Term.Steps.concat(classes.map { dispatched =>
          val body = dispatched.steps ++ next(dispatched.value)
          dispatched.condition.fold(body)(condition => Term.Steps(Term.Step("", Term.When(condition, body.all))))
        })
    }

  inline def storeElement(lane: Unit, product: Gathered, index: Term, value: Term)(inline next: => Term.Steps)
      : Term.Steps =
    statement(
      Tensors,
      MachineCall("store", Vec.empty, Vec((product.term, None), (index, Some(Word)), (value, Some(Word))))
    ) ++ next

  inline def eachUnit(inline body: Unit => Term.Steps)(inline next: => Term.Steps): Term.Steps =
    everyLane(body)(next)

  inline def writeUnit(lane: Unit, target: Register, value: Term)(inline next: => Term.Steps): Term.Steps =
    write(lane, target, value)(next)

  inline def writeUnitPredicate(lane: Unit, target: Predicate, value: Term)(inline next: => Term.Steps): Term.Steps =
    writePredicate(lane, target, value)(next)

  inline def retainTensorMemory(cta: Term, mask: Term)(inline next: => Term.Steps): Term.Steps =
    guarded(
      guard,
      Term.context(
        Cluster,
        MachineCall("retainTensorMemory", Vec.empty, Vec((cta, None), (mask, Some(Word)))),
        None,
        effect = true
      )
    ) ++
      next

  inline def acquireLock(cta: Term, offset: Term, holder: Term)(inline next: Term => Term.Steps): Term.Steps =
    lock("acquireLock", cta, offset, holder)(next)

  inline def releaseLock(cta: Term, offset: Term, holder: Term)(inline next: Term => Term.Steps): Term.Steps =
    lock("releaseLock", cta, offset, holder)(next)

  private inline def lock(method: String, cta: Term, offset: Term, holder: Term)(
      inline next: Term => Term.Steps
  ): Term.Steps = {
    val held = names.temporary()
    binding(
      held,
      Term.context(
        Cluster,
        MachineCall(method, Vec.empty, Vec((cta, None), (offset, Some(Word)), (holder, Some(Word)))),
        Some(Type.Bool),
        effect = true
      )
    ) ++ next(Term.Variable(held))
  }

  inline def loadChunk(cta: Term, offset: Term, packing: Matrix.Packing)(
      inline next: Term => Term.Steps
  ): Term.Steps = {
    val chunk = names.temporary()
    binding(
      chunk,
      Term.context(
        Cluster,
        MachineCall(
          "loadChunk",
          Vec.empty,
          Vec((cta, None), (offset, Some(Word)), (Term.Constant(s"$IR.Matrix.Packing.$packing"), None))
        ),
        None
      )
    ) ++ next(Term.Variable(chunk))
  }
}
