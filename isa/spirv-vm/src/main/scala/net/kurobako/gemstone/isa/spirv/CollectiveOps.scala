package net.kurobako.gemstone.isa.spirv

import Op.*
import VMCollectives.Kind as CollectiveKind
import VMCollectives.Payload as CollectivePayload
import VMCollectives.Request as CollectiveRequest
import VMInvocation.*
import VMMemory.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.vm.*
import scala.collection.mutable

/** Workgroup and subgroup collectives, barriers and fragment derivatives: lowering builds the rendezvous request each
  * lane suspends with, and resolution combines the requests of the lanes that meet.
  */
private object CollectiveOps {
  import Operand.*

  def lower(site: Site): PartialFunction[Instruction, Op] =
    collectives(site).orElse(workgroup).orElse(extensions(site)).orElse(derivatives(site))

  private def collectives(site: Site): PartialFunction[Instruction, Op] =
    Function.unlift(instruction =>
      GroupSemantics.describe(instruction, Some(site.program)).map(_.fold[Op](Fail(_), CollectiveOp(_)))
    )

  // split and named barriers and matrix products
  private def extensions(site: Site): PartialFunction[Instruction, Op] = {
    // every invocation arrives before it waits, so the wait's rendezvous orders whatever the arrival releases
    case Instruction.ControlBarrierArriveEXT(executionScope, memory, semantics) =>
      ArriveBarrierOp(id(executionScope), id(memory), id(semantics))
    case Instruction.ControlBarrierWaitEXT(execution, memory, semantics) =>
      ControlBarrierOp(id(execution), id(memory), id(semantics))
    case Instruction.SubgroupMatrixMultiplyAccumulateINTEL(t, d, inner, a, b, c, operands) =>
      matrixMultiply(site, ResultIds(t, d), id(inner), Vec(id(a), id(b), id(c)), operands.fold(0L)(unsigned))
    case Instruction.NamedBarrierInitialize(t, d, count)            => InitializeBarrierOp(id(d), id(t), id(count))
    case Instruction.MemoryNamedBarrier(barrier, memory, semantics) =>
      NamedBarrierOp(id(barrier), id(memory), id(semantics))
  }

  private def workgroup: PartialFunction[Instruction, Op] = {
    case Instruction.ControlBarrier(execution, memory, semantics) =>
      ControlBarrierOp(id(execution), id(memory), id(semantics))
  }

  private def derivatives(site: Site): PartialFunction[Instruction, Op] = {
    case Instruction.DPdx(t, d, v)         => derivative(site, ResultIds(t, d), id(v), Derivative.Dx)
    case Instruction.DPdy(t, d, v)         => derivative(site, ResultIds(t, d), id(v), Derivative.Dy)
    case Instruction.Fwidth(t, d, v)       => derivative(site, ResultIds(t, d), id(v), Derivative.Width)
    case Instruction.DPdxFine(t, d, v)     => derivative(site, ResultIds(t, d), id(v), Derivative.Dx)
    case Instruction.DPdyFine(t, d, v)     => derivative(site, ResultIds(t, d), id(v), Derivative.Dy)
    case Instruction.FwidthFine(t, d, v)   => derivative(site, ResultIds(t, d), id(v), Derivative.Width)
    case Instruction.DPdxCoarse(t, d, v)   => derivative(site, ResultIds(t, d), id(v), Derivative.CoarseDx)
    case Instruction.DPdyCoarse(t, d, v)   => derivative(site, ResultIds(t, d), id(v), Derivative.CoarseDy)
    case Instruction.FwidthCoarse(t, d, v) => derivative(site, ResultIds(t, d), id(v), Derivative.CoarseWidth)
  }

  /** Intel shifts read `delta` lanes up from the previous or down into the next subgroup-sized window. */
  def intelShift(
      ids: ResultIds,
      first: Int,
      second: Int,
      delta: Int,
      direction: Collective.ShuffleDirection,
      opcode: String
  ): Op = CollectiveOp(Collective.IntelShift(opcode, ids, first, second, delta, direction))

  private class CollectiveOp(collective: Collective) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      GroupSemantics.join(InvocationMachine(execution))(collective)
  }

  private class ControlBarrierOp(executionScope: Int, memoryScope: Int, semantics: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      BarrierSemantics.controlBarrier(InvocationMachine(execution))(executionScope, memoryScope, semantics)
  }

  private class ArriveBarrierOp(executionScope: Int, memoryScope: Int, semantics: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      BarrierSemantics.arriveBarrier(InvocationMachine(execution))(executionScope, memoryScope, semantics)
  }

  /** A named barrier spanning every subgroup of the workgroup waits as a workgroup barrier does. */
  private class NamedBarrierOp(barrier: Int, memoryScope: Int, semantics: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      BarrierSemantics.namedBarrier(InvocationMachine(execution))(barrier, memoryScope, semantics)
  }

  private class InitializeBarrierOp(target: Int, typeId: Int, count: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      BarrierSemantics.initializeBarrier(InvocationMachine(execution))(target, typeId, count)
  }

  /** The element `typeId`'s components read as under SPV_INTEL_subgroup_matrix_multiply_accumulate's operand `flags`:
    * packed narrow integers or floats, TF32 or bfloat16 views, or the component type itself.
    */
  private def multiplyElement(
      program: VMProgram,
      typeId: Int,
      flags: Long,
      signed: Long,
      int8: Long,
      int4: Long,
      half: Long,
      bfloat16: Long,
      tf32: Long
  ): Option[(SubgroupMatrixMultiply.Element, Int)] = {
    import SubgroupMatrixMultiply.*
    def has(flag: Long) = (flags & flag) != 0L
    program.floatingShape(typeId).map { (width, _) =>
      val kind =
        if (width == 16) Kind.Half
        else if (width == 32) { if (has(tf32)) Kind.TensorFloat32 else Kind.Single }
        else Kind.Double
      (Element(kind, width.raw), width.raw)
    }.orElse(program.integerShape(typeId).map { (width, _) =>
      val element =
        if (has(int8)) Element(if (has(signed)) Kind.Signed else Kind.Unsigned, 8)
        else if (has(int4)) Element(if (has(signed)) Kind.Signed else Kind.Unsigned, 4)
        else if (has(half)) Element(Kind.Half, 16)
        else if (has(bfloat16)) Element(Kind.BFloat16, 16)
        else Element(if (has(signed)) Kind.Signed else Kind.Unsigned, width)
      (element, width)
    })
  }

  private def matrixMultiply(
      site: Site,
      ids: ResultIds,
      inner: Int,
      operands: Vec[Int],
      flags: Long
  ): Op = {
    import SubgroupMatrixMultiply.*
    val program                              = site.program
    val types                                = operands.map(program.valueType(_))
    def element(typeId: Int, bfloat16: Long) = program.floatingShape(typeId).map(shape =>
      Element(if (shape._1 == 16) Kind.Half else Kind.Single, shape._1)
    )
      .orElse(program.integerShape(typeId).map(shape =>
        if ((flags & bfloat16) != 0L) Element(Kind.BFloat16, 16) else Element(Kind.Signed, shape._1)
      ))
    val result      = element(ids.resultType, 0x8L)
    val accumulator = element(types(2), 0x4L)
    val shape       = for {
      k <- program.constants.get(inner).collect { case VMValue.Bits(_, Array(value)) => value }
      a <- multiplyElement(
        program,
        types(0),
        flags,
        signed = 0x1L,
        int8 = 0x10L,
        int4 = 0x40L,
        half = 0x400L,
        bfloat16 = 0x1000L,
        tf32 = 0x100L
      )
      b <- multiplyElement(
        program,
        types(1),
        flags,
        signed = 0x2L,
        int8 = 0x20L,
        int4 = 0x80L,
        half = 0x800L,
        bfloat16 = 0x2000L,
        tf32 = 0x200L
      )
      c      <- accumulator
      output <- result
      if k > 0L && a._2 % a._1.bits == 0 && b._2 % b._1.bits == 0
    } yield Dimensions(
      inner = k.toInt,
      a = a._1,
      b = b._1,
      c = c,
      result = output,
      aComponent = a._2,
      bComponent = b._2
    )
    shape.fold(
      Fail(Fault.InvalidRequest("OpSubgroupMatrixMultiplyAccumulateINTEL operands do not form a matrix product"))
    )(found => MatrixMultiplyOp(ids, operands, found.selector))
  }

  private class MatrixMultiplyOp(ids: ResultIds, operands: Vec[Int], selector: Vec[Long]) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      InvocationMachine(execution).collective(Collective.MatrixMultiply(ids, operands, selector))
  }

  /** Derivatives pause the lane until its quad's neighbours reach the same site. */
  private def derivative(site: Site, ids: ResultIds, operand: Int, derivative: Derivative): Op =
    site.program.floatingShape(ids.resultType) match {
      case None                            => Fail(Fault.InvalidRequest("derivative result is not floating point"))
      case Some((width, _)) if width != 32 =>
        Fail(Fault.Unsupported("derivatives currently require 32-bit components"))
      case Some((_, count)) =>
        Dynamic { (execution, _) =>
          if (!execution.fragmentQuad)
            Left(Fault.InvalidRequest("fragment derivatives require an aligned four-lane quad"))
          else
            bits(execution.values, operand).flatMap { data =>
              Either.cond(
                data.length == count,
                execution.quadRequest =
                  Some(QuadRequest(
                    execution.currentFunction.id,
                    execution.pc,
                    ids.resultType,
                    ids.dest,
                    IArray.unsafeFromArray(data),
                    derivative
                  )),
                Fault.InvalidRequest("derivative operand shape mismatch")
              )
            }
        }
    }

  /** Resolves every rendezvous the suspended lanes of a workgroup have reached; returns how many it resolved. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.return"))
  def resolve(
      program: VMProgram,
      executions: mutable.ArrayBuffer[InvocationState],
      memory: MemoryState
  ): Either[Fault, Int] = {
    var resolved = resolveQuad(executions) match {
      case Left(error)  => return Left(error)
      case Right(count) => count
    }
    val subgroups =
      if (
        executions.exists(execution =>
          execution.reconvergenceJoin.nonEmpty || execution.suspended.exists(_.scope == Scope.Subgroup)
        )
      )
        executions.view.map(_.invocation.subgroupIndex).toSet.toArray.sorted
      else Array.emptyIntArray
    var index = 0
    while (index < subgroups.length) {
      val participants = Vec.from(executions.view.filter(_.invocation.subgroupIndex == subgroups(index)))
      // A preempted lane has not reached its next convergence point, so the active set is not yet known.
      val settled = !participants.exists(_.preempted)
      if (settled) resolved += resolveReconvergence(mutable.ArrayBuffer.from(participants))
      val requests   = participants.flatMap(_.suspended)
      val nonUniform =
        if (settled) requests.filter(request => request.scope == Scope.Subgroup && request.nonUniform)
        else Vec.empty[CollectiveRequest]
      val sites = nonUniform.foldLeft(Vector.empty[CollectiveRequest]) { (sites, request) =>
        if (sites.exists(same(_, request))) sites else sites :+ request
      }
      var siteIndex = 0
      while (siteIndex < sites.size) {
        val site = sites(siteIndex)
        combine(program, participants.filter(_.suspended.exists(same(_, site))), site, memory) match {
          case Left(error) => return Left(error)
          case Right(())   => resolved += 1
        }
        siteIndex += 1
      }
      if (
        nonUniform.isEmpty && requests.nonEmpty && requests.head.scope == Scope.Subgroup &&
        participants.forall(execution => !execution.completed && execution.suspended.exists(same(_, requests.head)))
      )
        combine(program, participants, requests.head, memory) match {
          case Left(error) => return Left(error)
          case Right(())   => resolved += 1
        }
      index += 1
    }
    executions.flatMap(_.suspended).find(_.scope == Scope.Workgroup) match {
      case Some(request)
          if executions.forall(execution => !execution.completed && execution.suspended.exists(same(_, request))) =>
        combine(program, Vec.from(executions), request, memory) match {
          case Left(error) => return Left(error)
          case Right(())   => resolved += 1
        }
      case _ => ()
    }
    Right(resolved)
  }

  // Lanes pause at structured merges and function returns so a lane cannot evaluate a later, uniform derivative
  // while its neighbours are still in divergent code.
  private def resolveReconvergence(executions: mutable.ArrayBuffer[InvocationState]): Int = {
    val ready = executions.flatMap(_.reconvergenceJoin).toSet.filter { join =>
      executions.forall { execution =>
        val participates = join match {
          case ReconvergenceJoin.Merge(label)             => execution.reconvergenceRegions.exists(_.merge == label)
          case ReconvergenceJoin.FunctionReturn(function) =>
            execution.currentFunction.id == function || execution.calls.exists(_.function.id == function)
        }
        !participates || execution.completed || execution.fragmentKilled || execution.reconvergenceJoin.contains(join)
      }
    }
    executions.foreach { execution =>
      execution.reconvergenceJoin.filter(ready.contains).foreach { join =>
        join match {
          case ReconvergenceJoin.Merge(label) =>
            val index = execution.reconvergenceRegions.indexWhere(_.merge == label)
            execution.reconvergenceRegions.remove(index, execution.reconvergenceRegions.size - index)
          case ReconvergenceJoin.FunctionReturn(_) => execution.reconvergedReturn = true
        }
        execution.reconvergenceJoin = None
      }
    }
    ready.size
  }

  private def resolveQuad(executions: mutable.ArrayBuffer[InvocationState]): Either[Fault, Int] = {
    val requests = executions.flatMap(_.quadRequest)
    if (requests.isEmpty) Right(0)
    else if (executions.size != 4) Left(Fault.InvalidRequest("fragment derivatives require four lanes"))
    else {
      val laneRequests = executions.map(_.quadRequest).toVector
      // Non-participating neighbours have undefined operands at this dynamic site. Choose zero without executing their
      // paths or reviving killed lanes.
      def value(lane: Int, component: Int, site: QuadRequest): Double =
        laneRequests(lane).filter(request =>
          request.function == site.function && request.pc == site.pc && request.derivative == site.derivative &&
            request.operand.length == site.operand.length
        ).fold(0.0)(request => IEEE754.binary32ToFloat(Binary32.low(request.operand(component))).toDouble)
      executions.zipWithIndex.foreach { (execution, lane) =>
        execution.quadRequest.foreach { request =>
          val left = if (request.derivative.coarse) 0 else lane & 2
          val top  = if (request.derivative.coarse) 0 else lane & 1
          val dx   = Array.tabulate(request.operand.length)(i => value(left + 1, i, request) - value(left, i, request))
          val dy   = Array.tabulate(request.operand.length)(i => value(top + 2, i, request) - value(top, i, request))
          if (request.derivative == Derivative.Gradients) execution.gradients = Some((dx, dy))
          else {
            val data = Array.tabulate(dx.length) { i =>
              val result = request.derivative match {
                case Derivative.Dx | Derivative.CoarseDx => dx(i)
                case Derivative.Dy | Derivative.CoarseDy => dy(i)
                case _                                   => Math.abs(dx(i)) + Math.abs(dy(i))
              }
              IEEE754.floatToBinary32(result.toFloat).toLong
            }
            execution.values.update(request.destination, VMValue.Bits(request.resultType, data))
            execution.pc += 1
          }
          execution.quadRequest = None
        }
      }
      Right(1)
    }
  }

  private def same(left: CollectiveRequest, right: CollectiveRequest): Boolean =
    left.kind == right.kind && left.function == right.function && left.pc == right.pc && left.scope == right.scope &&
      left.operation == right.operation && left.selector == right.selector && left.nonUniform == right.nonUniform &&
      ((left.payload, right.payload) match {
        case (CollectivePayload.Value(leftType, _, _), CollectivePayload.Value(rightType, _, _)) => leftType ==
            rightType
        case (CollectivePayload.Shuffle(resultType = leftType), CollectivePayload.Shuffle(resultType = rightType)) =>
          leftType == rightType
        case (CollectivePayload.Barrier(leftScope, leftFlags), CollectivePayload.Barrier(rightScope, rightFlags)) =>
          leftScope == rightScope && leftFlags == rightFlags
        case (
              CollectivePayload.PipeReservation(leftType, _, leftPipe, leftCount, leftWriting),
              CollectivePayload.PipeReservation(rightType, _, rightPipe, rightCount, rightWriting)
            ) =>
          leftType == rightType && leftPipe.address == rightPipe.address && leftCount == rightCount &&
          leftWriting == rightWriting
        case (
              CollectivePayload.ShuffleUp(resultType = leftType),
              CollectivePayload.ShuffleUp(resultType = rightType)
            ) =>
          leftType == rightType
        case (
              CollectivePayload.ShuffleDown(resultType = leftType),
              CollectivePayload.ShuffleDown(resultType = rightType)
            ) =>
          leftType == rightType
        case _ => false
      })

  /** Writes each participant's result and moves it past its rendezvous. */
  private def complete(participants: Vec[InvocationState], results: Vec[Option[VMValue]]): Unit =
    participants.zip(results).foreach { (execution, result) =>
      val request =
        execution.suspended.getOrElse(throw IllegalStateException("collective participant is not suspended"))
      result.foreach { value =>
        def place(resultType: Int, destination: Int): Unit =
          if (destination >= 0) execution.values.update(destination, retag(value, resultType))
        request.payload match {
          case CollectivePayload.Value(resultType, destination, _)      => place(resultType, destination)
          case CollectivePayload.Shuffle(resultType, destination, _, _) => place(resultType, destination)
          case CollectivePayload.ShuffleUp(resultType = resultType, destination = destination) =>
            place(resultType, destination)
          case CollectivePayload.ShuffleDown(resultType = resultType, destination = destination) =>
            place(resultType, destination)
          case CollectivePayload.Barrier(_, _)                                                       => ()
          case CollectivePayload.PipeReservation(resultType = resultType, destination = destination) =>
            place(resultType, destination)
        }
      }
      // The invocation resumes at the rendezvous it waited at, which the timeline records before it moves past it.
      execution.resume()
      execution.pc = request.pc + 1
    }

  /** Completes a rendezvous. */
  private def combine(
      program: VMProgram,
      participants: Vec[InvocationState],
      expected: CollectiveRequest,
      memory: MemoryState
  ): Either[Fault, Unit] =
    outcomes(program, participants, expected, memory).map(complete(participants, _))

  private val ArithmeticFaults: Map[CollectiveKind, String] = Map(
    CollectiveKind.IntegerAdd      -> "OpGroupIAdd",
    CollectiveKind.IntegerMultiply -> "OpGroupIMulKHR"
  )

  private def subgroupUnit(
      participants: Vec[InvocationState],
      positions: Array[Int],
      size: Int,
      columns: Array[Array[Long]],
      targets: Int
  ): GroupResolver =
    GroupResolver(positions, size, participants.head.invocation.subgroupSize, columns, targets, Vec.empty, Array.empty)

  /** The partition of the invocations that took part by the values they hold, one word of four bits per 32 invocations.
    * Every component of a vector value must match.
    */
  private def resolvePartition(
      program: VMProgram,
      participants: Vec[InvocationState],
      values: Vec[VMValue],
      resultType: Int
  ): Either[Fault, Vec[Option[VMValue]]] = {
    val inputs = values.collect { case VMValue.Bits(_, lanes) => lanes }
    if (inputs.size != values.size || inputs.isEmpty)
      Left(Fault.InvalidRequest("OpGroupNonUniformPartitionEXT participants do not all provide matching values"))
    else {
      val typeId     = values.head.typeId
      val floating   = program.floatingShape(typeId).map(_._1)
      val components = inputs.head.length
      val positions  = GroupResolver.indices(participants, subgroup = true)
      val size       = GroupResolver.extent(positions, participants.head.invocation.maxSubgroupSize)
      val columns    = Array.tabulate(components)(component =>
        GroupResolver.column(positions.length)(at => inputs(at)(component))
      )
      val unit = subgroupUnit(participants, positions, size, columns, 4 * components)
      (0 until components).foreach(component =>
        GroupResolution.partition(Execute, unit)(floating, component, Vec.from((0 until 4).map(4 * component + _)))
      )
      val words = unit.results.map(lanes =>
        Array.tabulate(4)(word =>
          (0 until components).foldLeft(-1L)((all, component) => all & lanes(4 * component + word))
        )
      )
      Right(Vec.from(words.map(lanes => Some(VMValue.Bits(resultType, lanes)))))
    }
  }

  /** The results of a broadcast from the invocation that the local id of the request names, which [[GroupResolution]]
    * gives the invocations that took part.
    */
  private def resolveBroadcast(
      participants: Vec[InvocationState],
      expected: CollectiveRequest,
      values: Vec[VMValue],
      resultType: Int
  ): Either[Fault, Vec[Option[VMValue]]] = {
    val inputs = values.collect { case VMValue.Bits(_, lanes) => lanes }
    if (inputs.size != values.size || inputs.isEmpty)
      Left(Fault.InvalidRequest("OpGroupBroadcast participants do not all provide matching values"))
    else {
      val subgroup  = expected.scope == Scope.Subgroup
      val positions = GroupResolver.indices(participants, subgroup)
      val size      = if (subgroup) GroupResolver.extent(positions, participants.head.invocation.maxSubgroupSize)
      else positions.length
      val components = inputs.head.length
      val columns    =
        Array.tabulate(components)(column => GroupResolver.column(positions.length)(at => inputs(at)(column)))
      val locals = Array.tabulate(3)(axis =>
        GroupResolver.column(positions.length)(at => participants(at).invocation.local(axis))
      )
      val keys  = expected.selector
      val scope = expected.scope.toLong
      val unit  =
        GroupResolver(
          positions,
          size,
          participants.head.invocation.subgroupSize,
          columns,
          components,
          Vec.empty,
          locals
        )
      if (unit.lane(scope, keys) >= size)
        Left(
          Fault.InvalidRequest(
            s"OpGroupBroadcast local id ${keys.mkString("(", ",", ")")} is outside the workgroup"
          )
        )
      else {
        (0 until components).foreach(component => GroupResolution.broadcast(unit)(scope, keys, component, component))
        Right(Vec.from(unit.results.map(lanes => Some(VMValue.Bits(resultType, lanes)))))
      }
    }
  }

  /** The results of an election, vote, broadcast of the first value, reduction or scan, which the invocations that took
    * part are given by [[GroupResolution]].
    */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def resolveUnit(
      program: VMProgram,
      participants: Vec[InvocationState],
      expected: CollectiveRequest,
      resultType: Int
  ): Either[Fault, Vec[Option[VMValue]]] = {
    val kind     = expected.kind
    val requests = participants.flatMap(_.suspended)
    val offered  = requests.map(_.payload).collect { case CollectivePayload.Value(_, _, value) => value }
    val inputs   = offered.collect { case VMValue.Bits(_, lanes) => lanes }
    val vote     = kind == CollectiveKind.Any || kind == CollectiveKind.All
    if (kind != CollectiveKind.Elect && !vote && (inputs.size != requests.size || inputs.isEmpty))
      Left(
        Fault.InvalidRequest(
          s"${ArithmeticFaults.getOrElse(kind, kind.toString)} participants do not all provide matching values"
        )
      )
    else {
      val subgroup  = expected.scope == Scope.Subgroup
      val positions = GroupResolver.indices(participants, subgroup)
      val size      = if (subgroup) GroupResolver.extent(positions, participants.head.invocation.maxSubgroupSize)
      else positions.length
      val clustered  = expected.operation == Collective.ClusteredReduce
      val components = if (kind == CollectiveKind.Elect || vote) 1 else inputs.head.length
      val columns    = Array.ofDim[Array[Long]](components + (if (clustered) 1 else 0))
      var column     = 0
      while (column < columns.length) {
        columns(column) =
          if (column == components)
            GroupResolver.column(positions.length)(_ => expected.selector.headOption.getOrElse(1L))
          else if (vote) GroupResolver.column(positions.length)(at => if (VMValue.isTrue(offered(at))) 1L else 0L)
          else GroupResolver.column(positions.length)(at => inputs(at)(column))
        column += 1
      }
      val combine = if (kind == CollectiveKind.Elect || kind == CollectiveKind.BroadcastFirst) Combine.IntegerAdd
      else GroupResolution.combining(kind)
      val unit = GroupResolver(
        positions,
        size,
        participants.head.invocation.subgroupSize,
        columns,
        components,
        Vec(Combine.Operator(program, resultType, combine)),
        Array.empty
      )
      var component = 0
      while (component < components) {
        kind match {
          case CollectiveKind.Elect                    => GroupResolution.elect(Execute, unit)(component)
          case CollectiveKind.BroadcastFirst           => GroupResolution.broadcastFirst(unit)(component, component)
          case CollectiveKind.Any | CollectiveKind.All =>
            GroupResolution.vote(Execute, unit)(combine, component, component)
          case _ if clustered => GroupResolution.clustered(Execute, unit)(combine, component, components, component)
          case _ => GroupResolution.reduction(Execute, unit)(combine, expected.operation.toLong, component, component)
        }
        component += 1
      }
      unit.shareEqualResults()
      Right(Vec.from(unit.results.map(lanes => Some(VMValue.Bits(resultType, lanes)))))
    }
  }

  /** The results of a shuffle, which [[GroupResolution]] picks from the invocations that took part. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def resolveShuffle(
      participants: Vec[InvocationState],
      expected: CollectiveRequest,
      resultType: Int
  ): Either[Fault, Vec[Option[VMValue]]] = {
    val payloads = participants.flatMap(_.suspended.map(_.payload))
    val data     = payloads.collect { case CollectivePayload.Shuffle(data = VMValue.Bits(_, lanes)) => lanes }
    val indices  = payloads.collect { case CollectivePayload.Shuffle(invocationId = VMValue.Bits(_, Array(index))) =>
      index
    }
    if (indices.size != participants.size || data.size != participants.size)
      Left(Fault.InvalidRequest("subgroup shuffle invocation id must be scalar"))
    else {
      val positions  = GroupResolver.indices(participants, subgroup = true)
      val size       = GroupResolver.extent(positions, participants.head.invocation.maxSubgroupSize)
      val components = data.head.length
      val columns    = Array.ofDim[Array[Long]](components + 1)
      var column     = 0
      while (column < columns.length) {
        columns(column) =
          if (column == components) GroupResolver.column(positions.length)(indices(_))
          else GroupResolver.column(positions.length)(data(_)(column))
        column += 1
      }
      val unit      = subgroupUnit(participants, positions, size, columns, components)
      val selection = Collective.Selection.of(expected.operation, expected.selector)
      var component = 0
      while (component < components) {
        GroupResolution.shuffle(Execute, unit)(selection, component, components, component)
        component += 1
      }
      unit.shareEqualResults()
      Right(Vec.from(unit.results.map(lanes => Some(VMValue.Bits(resultType, lanes)))))
    }
  }

  /** The result of an all-equal, which every invocation receives: each component must agree across the invocations, and
    * values of different types or lengths never do.
    */
  private def allEqual(
      program: VMProgram,
      participants: Vec[InvocationState],
      values: Vec[VMValue],
      resultType: Int
  ): Either[Fault, Vec[Option[VMValue]]] = {
    val inputs = values.collect { case VMValue.Bits(typeId, lanes) => (typeId, lanes) }
    if (inputs.size != values.size || inputs.map(_._2.length).distinct.size != 1)
      Right(Vec.fill(participants.size)(Some(VMValue.Bits(resultType, Array(0L)))))
    else {
      val (typeId, first) = inputs.head
      val floating        = program.floatingShape(typeId).map(_._1)
      val operators       = floating match {
        case Some(_) => Vec(Combine.Operator(program, typeId, Combine.FloatingEqual))
        case None    => Vec(
            Combine.Operator(program, typeId, Combine.BitwiseOr),
            Combine.Operator(program, typeId, Combine.BitwiseAnd)
          )
      }
      val positions = GroupResolver.indices(participants, subgroup = true)
      val size      = GroupResolver.extent(positions, participants.head.invocation.maxSubgroupSize)
      val agreed    = Array.tabulate(first.length) { component =>
        val unit = GroupResolver(
          positions,
          size,
          participants.head.invocation.subgroupSize,
          Array(GroupResolver.column(positions.length)(at => inputs(at)._2(component))),
          1,
          operators,
          Array.empty
        )
        GroupResolution.allEqual(Execute, unit)(floating, 0, 0)
        unit.results
      }
      Right(Vec.from(Array.tabulate[Option[VMValue]](participants.size) { at =>
        Some(VMValue.Bits(resultType, Array(if (agreed.forall(_(at)(0) != 0L)) 1L else 0L)))
      }))
    }
  }

  /** The results of a shift, which [[GroupResolution]] reads from the operands of the invocations that took part. A
    * source lane outside the subgroup or one that did not take part faults.
    */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def resolveShift(
      participants: Vec[InvocationState],
      direction: Collective.ShuffleDirection,
      resultType: Int
  ): Either[Fault, Vec[Option[VMValue]]] = {
    def sourced(execution: InvocationState, request: CollectiveRequest): Either[Fault, Unit] = {
      val size     = execution.invocation.subgroupSize.toLong
      val distance = request.payload match {
        case CollectivePayload.ShuffleUp(delta = delta)   => -delta
        case CollectivePayload.ShuffleDown(delta = delta) => delta
        case _                                            => 0L
      }
      val unwrapped = execution.invocation.subgroupLocal.toLong + distance
      val source    = if (unwrapped < 0L) unwrapped + size else if (unwrapped >= size) unwrapped - size else unwrapped
      participants.find(_.invocation.subgroupLocal.toLong == source) match {
        case Some(sourceExecution) => sourceExecution.suspended.map(_.payload) match {
            case Some(CollectivePayload.ShuffleUp(_, _, _, _, _) | CollectivePayload.ShuffleDown(_, _, _, _, _)) =>
              Right(())
            case _ => Left(Fault.InvalidRequest(s"subgroup shuffle-${direction.label} source is not suspended"))
          }
        case None => Left(Fault.InvalidRequest(s"subgroup shuffle-${direction.label} source lane $source is absent"))
      }
    }
    Traversal.traverse(participants)(execution =>
      execution.suspended.toRight(Fault.InvalidRequest("collective participant is not suspended"))
        .flatMap(sourced(execution, _))
    ).map { _ =>
      val operands = participants.flatMap(_.suspended).map(_.payload).map {
        case CollectivePayload.ShuffleUp(
              previous = VMValue.Bits(_, previous),
              current = VMValue.Bits(_, current),
              delta = delta
            ) =>
          (previous, current, delta)
        case CollectivePayload.ShuffleDown(
              current = VMValue.Bits(_, current),
              next = VMValue.Bits(_, next),
              delta = delta
            ) =>
          (current, next, delta)
        case _ => throw IllegalStateException("subgroup shifts carry shift payloads")
      }
      val positions  = GroupResolver.indices(participants, subgroup = true)
      val size       = GroupResolver.extent(positions, participants.head.invocation.maxSubgroupSize)
      val components = operands.head._1.length
      val columns    = Array.ofDim[Array[Long]](2 * components + 1)
      var column     = 0
      while (column < components) {
        columns(column) = GroupResolver.column(positions.length)(at => operands(at)._1(column))
        columns(components + column) = GroupResolver.column(positions.length)(at => operands(at)._2(column))
        column += 1
      }
      columns(2 * components) = GroupResolver.column(positions.length)(at => operands(at)._3)
      val unit      = subgroupUnit(participants, positions, size, columns, components)
      var component = 0
      while (component < components) {
        GroupResolution.shift(Execute, unit)(direction, component, components + component, 2 * components, component)
        component += 1
      }
      Vec.from(unit.results.map(lanes => Option(VMValue.Bits(resultType, lanes))))
    }
  }

  private def outcomes(
      program: VMProgram,
      participants: Vec[InvocationState],
      expected: CollectiveRequest,
      memory: MemoryState
  ): Either[Fault, Vec[Option[VMValue]]] = {
    val requests                                          = participants.flatMap(_.suspended)
    def payloadValue(request: CollectiveRequest): VMValue = request.payload match {
      case CollectivePayload.Value(_, _, value) => value
      case _ => throw IllegalStateException("collective participants have incompatible payloads")
    }
    def lanesOf(kind: String): Either[Fault, Vec[Array[Long]]] = {
      val inputs = requests.map(payloadValue(_)).collect { case VMValue.Bits(_, lanes) => lanes }
      Either.cond(
        inputs.size == requests.size && inputs.nonEmpty,
        inputs,
        Fault.InvalidRequest(s"$kind participants do not all provide matching values")
      )
    }
    val results: Either[Fault, Vec[Option[VMValue]]] = expected.payload match {
      case CollectivePayload.PipeReservation(resultType, _, pipe, packets, end) =>
        PipeOps.reserveOnce(memory, pipe, packets, end).map(reserved =>
          Vec.fill(participants.size)(Some(VMValue.Bits(resultType, Array(reserved))))
        )
      case CollectivePayload.Barrier(memoryScope, semantics) =>
        // Broader scopes publish to and refresh host memory. Fence once at the rendezvous, before lanes resume.
        val fence =
          if (semantics == 0L) Right(())
          else if (memoryScope == Scope.Workgroup || memoryScope == Scope.Subgroup || memoryScope == 4) Right(())
          else memory.fence()
        fence.map(_ => Vec.fill(participants.size)(None))
      case CollectivePayload.Shuffle(resultType = resultType)   => resolveShuffle(participants, expected, resultType)
      case CollectivePayload.ShuffleUp(resultType = resultType) =>
        resolveShift(participants, Collective.ShuffleDirection.Up, resultType)
      case CollectivePayload.ShuffleDown(resultType = resultType) =>
        resolveShift(participants, Collective.ShuffleDirection.Down, resultType)
      case CollectivePayload.Value(resultType, _, _) =>
        val values = requests.map(payloadValue(_))
        expected.kind match {
          case CollectiveKind.Elect | CollectiveKind.BroadcastFirst | CollectiveKind.Any | CollectiveKind.All |
              CollectiveKind.UnsignedMin | CollectiveKind.UnsignedMax | CollectiveKind.SignedMin |
              CollectiveKind.SignedMax | CollectiveKind.IntegerAdd | CollectiveKind.IntegerMultiply |
              CollectiveKind.BitwiseAnd | CollectiveKind.BitwiseOr | CollectiveKind.BitwiseXor |
              CollectiveKind.LogicalAnd | CollectiveKind.LogicalOr | CollectiveKind.LogicalXor |
              CollectiveKind.FloatingAdd | CollectiveKind.FloatingMultiply | CollectiveKind.FloatingMin |
              CollectiveKind.FloatingMax =>
            resolveUnit(program, participants, expected, resultType)
          case CollectiveKind.AllEqual  => allEqual(program, participants, values, resultType)
          case CollectiveKind.Broadcast => resolveBroadcast(participants, expected, values, resultType)
          case CollectiveKind.Ballot    =>
            if (!values.forall { case VMValue.Bits(_, Array(_)) => true; case _ => false })
              Left(
                Fault.InvalidRequest(
                  "OpGroupNonUniformBallot participants do not all provide scalar boolean predicates"
                )
              )
            else {
              val positions = GroupResolver.indices(participants, subgroup = true)
              val columns   = Array(GroupResolver.column(positions.length)(at =>
                values(at) match {
                  case VMValue.Bits(_, Array(predicate)) => predicate
                  case _                                 => 0L
                }
              ))
              val words = 4
              val unit  =
                subgroupUnit(participants, positions, GroupResolver.extent(positions, words * 32), columns, words)
              GroupResolution.ballot(Execute, unit)(0, Vec(0, 1, 2, 3))
              unit.shareEqualResults()
              Right(Vec.from(unit.results.map(lanes => Some(VMValue.Bits(resultType, lanes)))))
            }
          case CollectiveKind.MatrixMultiply =>
            val shape                  = SubgroupMatrixMultiply.Dimensions.of(expected.selector.take(7))
            val Vec(aLanes, bLanes, _) = expected.selector.drop(7).map(_.toInt): @unchecked
            lanesOf("OpSubgroupMatrixMultiplyAccumulateINTEL").map { inputs =>
              val contributions = participants.zip(inputs).map((execution, lanes) =>
                (
                  execution.invocation.subgroupLocal,
                  lanes.slice(0, aLanes),
                  lanes.slice(aLanes, aLanes + bLanes),
                  lanes.drop(aLanes + bLanes)
                )
              )
              val columns =
                SubgroupMatrixMultiply(
                  shape,
                  contributions,
                  participants.headOption.fold(1)(_.invocation.maxSubgroupSize)
                )
              participants.map(execution =>
                Some(VMValue.Bits(resultType, columns(execution.invocation.subgroupLocal)))
              )
            }
          case CollectiveKind.MatrixRows =>
            val Vec(rows, columns, use, componentBits, elementBits) = expected.selector.map(_.toInt): @unchecked
            val shape = VMProgram.CooperativeMatrix(0, rows, columns, use.toLong, nv = false)
            lanesOf("OpCompositeConstructCoopMatQCOM").map { inputs =>
              val matrix = Array.ofDim[Long](rows * columns)
              participants.zip(inputs).foreach { (execution, elements) =>
                val lane = execution.invocation.subgroupLocal
                if (lane < CooperativeMatrixOps.lineCount(shape)) {
                  val line = CooperativeMatrixOps.unpackLine(
                    elements,
                    CooperativeMatrixOps.lineLength(shape),
                    componentBits,
                    elementBits
                  )
                  line.indices.foreach(at =>
                    matrix(if (use == 1) at * columns + lane else lane * columns + at) = line(at)
                  )
                }
              }
              val result = Some(VMValue.Bits(resultType, matrix))
              Vec.fill(participants.size)(result)
            }
          case CollectiveKind.Partition => resolvePartition(program, participants, values, resultType)
          case kind                     => Left(Fault.InvalidRequest(s"$kind rendezvous unexpectedly carried a value"))
        }
    }
    results
  }
}
