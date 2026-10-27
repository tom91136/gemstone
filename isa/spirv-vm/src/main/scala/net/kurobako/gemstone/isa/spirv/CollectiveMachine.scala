package net.kurobako.gemstone.isa.spirv

import Op.*
import VMCollectives.Kind as CollectiveKind
import VMCollectives.Payload as CollectivePayload
import VMCollectives.Request as CollectiveRequest
import VMInvocation.*
import net.kurobako.gemstone.binary.Bits.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.vm.*

/** The collectives an invocation joins: each checks its operands, then suspends the invocation with the request
  * [[CollectiveOps.resolve]] combines with those of the invocations it meets, or for a ballot query reads the
  * invocation's own ballot.
  */
private object CollectiveMachine {
  private val NonUniformFault: NonEmpty[String]         = "nonuniform operations require Subgroup scope"
  private val NonUniformFloatingFault: NonEmpty[String] = "nonuniform floating operations require Subgroup scope"

  def run(execution: InvocationState, collective: Collective): Flow[Unit] = collective match {
    case c: Collective.Elect          => elect(execution, c)
    case c: Collective.Vote           => vote(execution, c)
    case c: Collective.Reduction      => reduction(execution, c)
    case c: Collective.Value          => subgroupValue(execution, c)
    case c: Collective.Shuffle        => shuffle(execution, c)
    case c: Collective.Broadcast      => broadcast(execution, c)
    case c: Collective.Ballot         => ballot(execution, c)
    case c: Collective.Partition      => partition(execution, c)
    case c: Collective.BallotQuery    => ballotQuery(execution, c)
    case c: Collective.IntelShuffle   => intelShuffle(execution, c)
    case c: Collective.IntelShift     => intelShift(execution, c)
    case c: Collective.MatrixMultiply => matrixMultiply(execution, c)
    case c: Collective.MatrixRows     => matrixRows(execution, c)
  }

  private def matrixMultiply(execution: InvocationState, c: Collective.MatrixMultiply): Flow[Unit] =
    Traversal.traverse(c.operands)(bits(execution.values, _)).flatMap { parts =>
      suspend(
        execution,
        CollectiveRequest(
          CollectiveKind.MatrixMultiply,
          _,
          _,
          Scope.Subgroup,
          CollectivePayload.Value(c.ids.resultType, c.ids.dest, VMValue.Bits(c.ids.resultType, Array.concat(parts*))),
          selector = c.selector ++ parts.map(_.length.toLong)
        )
      )
    }

  private def matrixRows(execution: InvocationState, c: Collective.MatrixRows): Flow[Unit] =
    bits(execution.values, c.source).flatMap(lanes =>
      suspend(
        execution,
        CollectiveRequest(
          CollectiveKind.MatrixRows,
          _,
          _,
          Scope.Subgroup,
          CollectivePayload.Value(c.ids.resultType, c.ids.dest, VMValue.Bits(c.ids.resultType, lanes)),
          selector = c.selector
        )
      )
    )

  private def scopeOf(values: InvocationValues, scope: Int): Either[Fault, Long] =
    if (scope == Collective.ImpliedSubgroup) Right(Scope.Subgroup.toLong) else scalar(values, scope)

  private def subgroupOnly(execution: InvocationState, outcome: Flow[Unit], message: NonEmpty[String]): Flow[Unit] =
    outcome.flatMap { _ =>
      execution.suspended match {
        case Some(request) if request.scope != Scope.Subgroup =>
          execution.abandon()
          Left(Fault.InvalidRequest(message))
        case Some(request) =>
          execution.remark(request.copy(nonUniform = true))
          Done
        case None => Done
      }
    }

  /** The cluster size of a clustered reduction: a power of two. */
  private def clusterSize(values: InvocationValues, cluster: Option[Int]): Either[Fault, Vec[Long]] =
    cluster.fold[Either[Fault, Vec[Long]]](Right(Vec.empty))(operand =>
      scalar(values, operand).flatMap(size =>
        Either.cond(
          size > 0L && (size & (size - 1L)) == 0L,
          Vec(size),
          Fault.InvalidRequest(s"cluster size $size is not a power of two")
        )
      )
    )

  private def elect(execution: InvocationState, c: Collective.Elect): Flow[Unit] =
    scalar(execution.values, c.scope).flatMap { value =>
      if (value != Scope.Subgroup) Left(Fault.InvalidRequest(Collective.ElectFault))
      else
        suspend(
          execution,
          CollectiveRequest(
            CollectiveKind.Elect,
            _,
            _,
            Scope.Subgroup,
            CollectivePayload.Value(c.ids.resultType, c.ids.dest, VMValue.Bits(c.ids.resultType, Array(1L))),
            nonUniform = true
          )
        )
    }

  private def vote(execution: InvocationState, c: Collective.Vote): Flow[Unit] = {
    val outcome = (scopeOf(execution.values, c.scope), execution.values.get(c.predicate)) match {
      case (Right(value), Some(predicateValue @ VMValue.Bits(_, Array(_))))
          if isWorkgroupOrSubgroup(value) && c.valid =>
        suspend(
          execution,
          CollectiveRequest(
            c.kind,
            _,
            _,
            value.toInt,
            CollectivePayload.Value(c.ids.resultType, c.ids.dest, predicateValue)
          )
        )
      case (Right(value), _) if !isWorkgroupOrSubgroup(value) => Left(scopeFault(c.opcode, value))
      case (Right(_), Some(_))                                =>
        Left(Fault.InvalidRequest(s"${c.opcode} requires scalar boolean predicate and result types"))
      case (Left(error), _) => Left(error)
      case (_, None)        => Left(undefined(c.predicate))
    }
    if (c.subgroup) subgroupOnly(execution, outcome, NonUniformFault) else outcome
  }

  private def reduction(execution: InvocationState, c: Collective.Reduction): Flow[Unit] = {
    val values  = execution.values
    val outcome = (scalar(values, c.scope), values.get(c.source)) match {
      case (Right(value), Some(sourceValue: VMValue.Bits))
          if isWorkgroupOrSubgroup(value) && (c.operation <= 2L || c.cluster.nonEmpty) && c.matching =>
        clusterSize(values, c.cluster).flatMap(selector =>
          suspend(
            execution,
            CollectiveRequest(
              c.kind,
              _,
              _,
              value.toInt,
              CollectivePayload.Value(c.ids.resultType, c.ids.dest, sourceValue),
              c.operation.toInt,
              selector
            )
          )
        )
      case (Right(_), _) if c.operation > 2L && c.cluster.isEmpty =>
        Left(
          Fault.Unsupported(
            s"${c.opcode} group operation ${c.operation} is not Reduce, InclusiveScan, or ExclusiveScan"
          )
        )
      case (Right(value), _) if !isWorkgroupOrSubgroup(value) => Left(scopeFault(c.opcode, value))
      case (Right(_), Some(_))                                =>
        Left(Fault.InvalidRequest(s"${c.opcode} requires matching ${c.shape.described} scalar or vector types"))
      case (Left(error), _) => Left(error)
      case (_, None)        => Left(undefined(c.source))
    }
    if (c.subgroup) subgroupOnly(execution, outcome, NonUniformFloatingFault) else outcome
  }

  private def subgroupValue(execution: InvocationState, c: Collective.Value): Flow[Unit] =
    subgroupValue(execution, c.kind, c.ids, c.scope, c.source, c.operation, c.cluster, c.compatible)

  private def subgroupValue(
      execution: InvocationState,
      kind: CollectiveKind,
      ids: ResultIds,
      scope: Int,
      source: Int,
      operation: Int,
      cluster: Option[Int],
      compatible: Boolean
  ): Flow[Unit] =
    (scopeOf(execution.values, scope), execution.values.get(source)) match {
      case (Right(Scope.Subgroup), Some(value @ VMValue.Bits(sourceType, _)))
          if compatible || sourceType == ids.resultType =>
        clusterSize(execution.values, cluster).flatMap(selector =>
          suspend(
            execution,
            CollectiveRequest(
              kind,
              _,
              _,
              Scope.Subgroup,
              CollectivePayload.Value(ids.resultType, ids.dest, value),
              operation,
              selector,
              nonUniform = true
            )
          )
        )
      case (Left(error), _) => Left(error)
      case (_, None)        => Left(undefined(source))
      case _                =>
        Left(Fault.InvalidRequest("subgroup operation requires Subgroup scope and compatible value/result types"))
    }

  private def shuffle(execution: InvocationState, c: Collective.Shuffle): Flow[Unit] =
    subgroupValue(
      execution,
      CollectiveKind.Shuffle,
      c.ids,
      c.scope,
      c.source,
      0,
      None,
      compatible = false
    )
      .flatMap { _ =>
        val laneType = execution.values.program.valueType(c.lane)
        val outcome  = for {
          index <- scalar(execution.values, c.lane)
          _     <-
            Either.cond(c.validLane, (), Fault.InvalidRequest("subgroup shuffle lane must be a 32-bit scalar integer"))
          data <- execution.values.get(c.source).toRight(undefined(c.source))
        } yield execution.suspended.foreach { pending =>
          execution.suspend(pending.copy(
            payload =
              CollectivePayload.Shuffle(c.ids.resultType, c.ids.dest, data, VMValue.Bits(laneType, Array(index))),
            operation = c.selection.ordinal,
            selector = c.selection.clusterSizes
          ))
        }
        outcome.left.map { error =>
          execution.abandon()
          error
        }
      }

  private def broadcast(execution: InvocationState, c: Collective.Broadcast): Flow[Unit] = {
    val values  = execution.values
    val program = values.program
    (scalar(values, c.scope), values.get(c.source), values.get(c.local)) match {
      case (Right(value), Some(sourceValue @ VMValue.Bits(sourceType, _)), Some(VMValue.Bits(localType, lanes)))
          if isWorkgroupOrSubgroup(value) && sourceType == c.ids.resultType &&
            program.integerShape(localType).exists((_, count) => count == lanes.length && count >= 1 && count <= 3) =>
        val width = program.integerShape(localType).map(_._1).getOrElse(64: Int in 1 ~ 64)
        suspend(
          execution,
          CollectiveRequest(
            CollectiveKind.Broadcast,
            _,
            _,
            value.toInt,
            CollectivePayload.Value(c.ids.resultType, c.ids.dest, sourceValue),
            selector = Vec.from(lanes.map(_ & mask(width)))
          )
        )
      case (Right(value), _, _) if !isWorkgroupOrSubgroup(value) => Left(scopeFault("OpGroupBroadcast", value))
      case (Right(_), None, _)                                   => Left(undefined(c.source))
      case (Right(_), _, None)                                   => Left(undefined(c.local))
      case (Right(_), _, _)                                      =>
        Left(
          Fault.InvalidRequest(
            "OpGroupBroadcast requires matching result/source bit types and a scalar, two-lane or three-lane integer local id"
          )
        )
      case (Left(error), _, _) => Left(error)
    }
  }

  private def ballot(execution: InvocationState, c: Collective.Ballot): Flow[Unit] =
    (scopeOf(execution.values, c.scope), execution.values.get(c.predicate)) match {
      case (Right(Scope.Subgroup), Some(value @ VMValue.Bits(_, Array(_)))) if c.valid =>
        suspend(
          execution,
          CollectiveRequest(
            CollectiveKind.Ballot,
            _,
            _,
            Scope.Subgroup,
            CollectivePayload.Value(c.ids.resultType, c.ids.dest, value),
            nonUniform = true
          )
        )
      case (Right(value), _) if value != Scope.Subgroup =>
        Left(Fault.Unsupported(s"OpGroupNonUniformBallot execution scope $value is not Subgroup"))
      case (Right(_), Some(_)) =>
        Left(
          Fault.InvalidRequest(
            "OpGroupNonUniformBallot requires a four-lane 32-bit integer result and scalar boolean predicate"
          )
        )
      case (Left(error), _) => Left(error)
      case (_, None)        => Left(undefined(c.predicate))
    }

  private def partition(execution: InvocationState, c: Collective.Partition): Flow[Unit] =
    execution.values.get(c.source) match {
      case Some(value @ VMValue.Bits(_, _)) =>
        suspend(
          execution,
          CollectiveRequest(
            CollectiveKind.Partition,
            _,
            _,
            Scope.Subgroup,
            CollectivePayload.Value(c.ids.resultType, c.ids.dest, value),
            nonUniform = true
          )
        )
      case Some(_) => Left(Fault.InvalidRequest("OpGroupNonUniformPartitionEXT partitions numeric values"))
      case None    => Left(undefined(c.source))
    }

  private def ballotQuery(execution: InvocationState, c: Collective.BallotQuery): Flow[Unit] = {
    val values     = execution.values
    val invocation = execution.invocation
    val bit        = c.bit.fold[Either[Fault, Long]](Right(invocation.subgroupLocal.toLong))(scalar(values, _))
    val outcome    = (scalar(values, c.scope), values.get(c.source), bit) match {
      case (Right(Scope.Subgroup), Some(VMValue.Bits(_, words)), Right(local)) if c.valid && words.length == 4 =>
        val result = GroupResolution.ballotQuery(Execute)(
          c.mode,
          words(0),
          words(1),
          words(2),
          words(3),
          local,
          invocation.subgroupLocal.toLong,
          invocation.maxSubgroupSize.toLong
        )
        if (c.mode < -1 || c.mode > 4) Left(Fault.InvalidRequest("invalid subgroup ballot operation"))
        else Right(values.updateScalar(c.ids.dest, c.ids.resultType, result))
      case (Left(error), _, _) => Left(error)
      case (_, _, Left(error)) => Left(error)
      case (_, None, _)        => Left(undefined(c.source))
      case _                   =>
        Left(Fault.InvalidRequest("subgroup ballot requires Subgroup scope, uint4 value and compatible result"))
    }
    outcome.map { _ =>
      execution.pc += 1
    }
  }

  private def intelShuffle(execution: InvocationState, c: Collective.IntelShuffle): Flow[Unit] =
    (execution.values.get(c.data), execution.values.get(c.invocation)) match {
      case (Some(value @ VMValue.Bits(dataType, lanes)), Some(VMValue.Bits(selectorType, Array(selector))))
          if dataType == c.ids.resultType && lanes.nonEmpty =>
        suspend(
          execution,
          CollectiveRequest(
            CollectiveKind.Shuffle,
            _,
            _,
            Scope.Subgroup,
            CollectivePayload.Shuffle(c.ids.resultType, c.ids.dest, value, VMValue.Bits(selectorType, Array(selector))),
            operation = (if (c.xor) Collective.Selection.Xor else Collective.Selection.Absolute).ordinal
          )
        )
      case (Some(_), Some(VMValue.Bits(_, lanes))) if lanes.length != 1 =>
        Left(Fault.InvalidRequest("OpSubgroupShuffleINTEL invocation id must be scalar"))
      case (Some(_), Some(_)) =>
        Left(Fault.InvalidRequest("OpSubgroupShuffleINTEL requires matching data and result types"))
      case (None, _) => Left(undefined(c.data))
      case (_, None) => Left(undefined(c.invocation))
    }

  private def intelShift(execution: InvocationState, c: Collective.IntelShift): Flow[Unit] = {
    val values = execution.values
    (values.get(c.first), values.get(c.second), scalar(values, c.delta)) match {
      case (
            Some(left @ VMValue.Bits(leftType, leftLanes)),
            Some(right @ VMValue.Bits(rightType, rightLanes)),
            Right(distance)
          )
          if leftType == c.ids.resultType && rightType == c.ids.resultType && leftLanes.length == rightLanes.length &&
            distance >= 0L =>
        val (kind, payload) = c.direction match {
          case Collective.ShuffleDirection.Up =>
            (CollectiveKind.ShuffleUp, CollectivePayload.ShuffleUp(c.ids.resultType, c.ids.dest, left, right, distance))
          case Collective.ShuffleDirection.Down =>
            (
              CollectiveKind.ShuffleDown,
              CollectivePayload.ShuffleDown(c.ids.resultType, c.ids.dest, left, right, distance)
            )
        }
        suspend(execution, CollectiveRequest(kind, _, _, Scope.Subgroup, payload))
      case (Some(_), Some(_), Right(distance)) if distance < 0L =>
        Left(Fault.InvalidRequest(s"${c.opcode} delta must be non-negative"))
      case (Some(_), Some(_), Right(_)) =>
        Left(Fault.InvalidRequest(s"${c.opcode} requires matching result and operand types"))
      case (_, _, Left(error)) => Left(error)
      case (None, _, _)        => Left(undefined(c.first))
      case (_, None, _)        => Left(undefined(c.second))
    }
  }
}
