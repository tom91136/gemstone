package net.kurobako.gemstone.isa.spirv

import Collective.*
import Operand.*
import VMCollectives.Kind as CollectiveKind
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.render.*
import net.kurobako.gemstone.vm.*

/** SPIR-V group and subgroup operations, as requests to a [[SPIRVMachine]]. The machine resolves them once the
  * invocations that take part have arrived, so one definition describes both the request an invocation makes and the
  * result it receives.
  */
private object GroupSemantics {

  private val ClusterFault: NonEmpty[String] = "a cluster size accompanies exactly the ClusteredReduce group operation"

  inline def join[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(collective: Collective): F[Unit] =
    machine.collective(collective)

  /** The semantics of `instruction` rendered over `machine`, or none for an instruction they do not cover. */
  def render(instruction: Instruction, machine: SPIRVMachineRender): Option[Term.Steps] = instruction match {
    case Instruction.SubgroupMatrixMultiplyAccumulateINTEL(t, d, _, a, b, c, _) =>
      Some(join(machine)(Collective.MatrixMultiply(ResultIds(t, d), Vec(id(a), id(b), id(c)), Vec.empty)))
    case _ => describe(instruction, None).flatMap(_.toOption).map(join(machine)(_))
  }

  private def clusterMatches(operation: Operand, cluster: Option[Operand]): Boolean =
    (unsigned(operation) == ClusteredReduce) == cluster.nonEmpty &&
      unsigned(operation) <= ClusteredReduce

  /** The collective `instruction` requests, or the fault the loader finds in it, or none if it is no collective. The
    * program decides the flags that depend on operand types; none holds them all.
    */
  def describe(instruction: Instruction, program: Option[VMProgram]): Option[Either[Fault, Collective]] =
    Descriptions(program).all.lift(instruction).flatten

  private type Description = PartialFunction[Instruction, Some[Either[Fault, Collective]]]

  private class Descriptions(program: Option[VMProgram]) {
    def holds(check: VMProgram => Boolean): Boolean = program.forall(check)
    def scalarBoolean(typeId: Int): Boolean         = holds(_.booleanLanes(typeId).contains(1))
    def scalarBooleanValue(value: Int): Boolean = holds(found => found.booleanLanes(found.valueType(value)).contains(1))
    def uint4(typeId: Int): Boolean             = holds(_.integerShape(typeId).contains(32 -> 4))

    def vote(
        opcode: String,
        kind: CollectiveKind,
        t: Operand,
        d: Operand,
        scope: Int,
        predicate: Operand,
        sub: Boolean
    ): Right[Nothing, Collective] =
      Right(
        Collective.Vote(
          opcode,
          kind,
          ResultIds(t, d),
          scope,
          id(predicate),
          sub,
          scalarBoolean(id(t)) && scalarBooleanValue(id(predicate))
        )
      )

    def reduction(
        opcode: String,
        kind: CollectiveKind,
        shape: Shape,
        t: Operand,
        d: Operand,
        scope: Operand,
        operation: Operand,
        source: Operand,
        cluster: Option[Operand] = None,
        sub: Boolean = false
    ): Collective = {
      val matching = holds { found =>
        val sourceType = found.valueType(id(source))
        shape match {
          case Shape.Boolean => found.booleanLanes(id(t)).isDefined &&
            found.booleanLanes(id(t)) == found.booleanLanes(sourceType)
          case Shape.Integer => found.integerShape(id(t)).isDefined &&
            found.integerShape(id(t)) == found.integerShape(sourceType)
          case Shape.Floating => found.floatingShape(id(t)).isDefined &&
            found.floatingShape(id(t)) == found.floatingShape(sourceType)
        }
      }
      Collective.Reduction(
        opcode = opcode,
        kind = kind,
        shape = shape,
        ids = ResultIds(t, d),
        scope = id(scope),
        operation = unsigned(operation),
        source = id(source),
        cluster = cluster.map(id),
        subgroup = sub,
        matching = matching
      )
    }

    def floating(
        opcode: String,
        kind: CollectiveKind,
        t: Operand,
        d: Operand,
        scope: Operand,
        operation: Operand,
        source: Operand,
        cluster: Option[Operand]
    ): Either[Fault, Collective] =
      Either.cond(
        clusterMatches(operation, cluster),
        reduction(opcode, kind, Shape.Floating, t, d, scope, operation, source, cluster, sub = true),
        Fault.InvalidRequest(ClusterFault)
      )

    def arithmetic(
        kind: CollectiveKind,
        t: Operand,
        d: Operand,
        scope: Operand,
        operation: Operand,
        source: Operand,
        cluster: Option[Operand]
    ): Either[Fault, Collective] = {
      val logical = kind == CollectiveKind.LogicalAnd || kind == CollectiveKind.LogicalOr ||
        kind == CollectiveKind.LogicalXor
      if (!clusterMatches(operation, cluster)) Left(Fault.InvalidRequest(ClusterFault))
      else if (!holds(found => if (logical) found.booleanLanes(id(t)).nonEmpty else found.integerShape(id(t)).nonEmpty))
        Left(Fault.InvalidRequest("subgroup arithmetic operand type is incompatible with its operation"))
      else
        Right(
          Collective.Value(
            kind = kind,
            ids = ResultIds(t, d),
            scope = id(scope),
            source = id(source),
            operation = unsigned(operation).toInt,
            cluster = cluster.map(id),
            compatible = false
          )
        )
    }

    def value(kind: CollectiveKind, t: Operand, d: Operand, scope: Int, source: Operand): Right[Nothing, Collective] =
      Right(
        Collective.Value(
          kind,
          ResultIds(t, d),
          scope,
          id(source),
          0,
          None,
          kind == CollectiveKind.AllEqual && scalarBoolean(id(t))
        )
      )

    def shuffle(
        t: Operand,
        d: Operand,
        scope: Int,
        source: Operand,
        lane: Operand,
        selection: Selection
    ): Right[Nothing, Collective] =
      Right(
        Collective.Shuffle(
          ResultIds(t, d),
          scope,
          id(source),
          id(lane),
          selection,
          holds(found => found.integerShape(found.valueType(id(lane))).contains(32 -> 1))
        )
      )

    def ballot(t: Operand, d: Operand, scope: Int, predicate: Operand): Right[Nothing, Collective] =
      Right(
        Collective.Ballot(ResultIds(t, d), scope, id(predicate), uint4(id(t)) && scalarBooleanValue(id(predicate)))
      )

    def query(
        t: Operand,
        d: Operand,
        scope: Operand,
        source: Operand,
        mode: Int,
        bit: Option[Operand]
    ): Right[Nothing, Collective] =
      Right(
        Collective.BallotQuery(
          ResultIds(t, d),
          id(scope),
          id(source),
          mode,
          bit.map(id),
          holds { found =>
            found.integerShape(found.valueType(id(source))).contains(32 -> 4) &&
            (if (mode == -1) found.booleanLanes(id(t)).contains(1) else found.integerShape(id(t)).contains(32 -> 1))
          }
        )
      )

    def rotate(
        t: Operand,
        d: Operand,
        scope: Operand,
        source: Operand,
        delta: Operand,
        cluster: Option[Operand]
    ): Either[Fault, Collective] =
      cluster.map(operand => program.map(_.constants.get(id(operand)))) match {
        case Some(Some(Some(VMValue.Bits(_, Array(lanes))))) if lanes <= 0L || (lanes & (lanes - 1L)) != 0L =>
          Left(Fault.InvalidRequest(s"OpGroupNonUniformRotateKHR cluster size $lanes is not a power of two"))
        case Some(Some(Some(VMValue.Bits(_, Array(lanes))))) =>
          shuffle(t, d, id(scope), source, delta, Selection.Rotate(Some(lanes)))
        case Some(Some(_)) =>
          Left(Fault.InvalidRequest("OpGroupNonUniformRotateKHR cluster size must be a scalar constant"))
        case _ => shuffle(t, d, id(scope), source, delta, Selection.Rotate(None))
      }

    val implied = ImpliedSubgroup

    def all: Description = votes.orElse(scans).orElse(shuffles).orElse(ballots)

    private def votes: Description = {
      case Instruction.GroupNonUniformElect(t, d, scope) =>
        Some(Either.cond(
          scalarBoolean(id(t)),
          Collective.Elect(ResultIds(t, d), id(scope)),
          Fault.InvalidRequest(ElectFault)
        ))
      case Instruction.GroupAny(t, d, scope, predicate) =>
        Some(vote("OpGroupAny", CollectiveKind.Any, t, d, id(scope), predicate, sub = false))
      case Instruction.GroupAll(t, d, scope, predicate) =>
        Some(vote("OpGroupAll", CollectiveKind.All, t, d, id(scope), predicate, sub = false))
      case Instruction.GroupNonUniformAll(t, d, scope, predicate) =>
        Some(vote("OpGroupNonUniformAll", CollectiveKind.All, t, d, id(scope), predicate, sub = true))
      case Instruction.GroupNonUniformAny(t, d, scope, predicate) =>
        Some(vote("OpGroupNonUniformAny", CollectiveKind.Any, t, d, id(scope), predicate, sub = true))
      case Instruction.SubgroupAllKHR(t, d, predicate) =>
        Some(vote("OpSubgroupAllKHR", CollectiveKind.All, t, d, implied, predicate, sub = true))
      case Instruction.SubgroupAnyKHR(t, d, predicate) =>
        Some(vote("OpSubgroupAnyKHR", CollectiveKind.Any, t, d, implied, predicate, sub = true))
      case Instruction.GroupUMin(t, d, scope, operation, source) =>
        Some(Right(reduction("OpGroupUMin", CollectiveKind.UnsignedMin, Shape.Integer, t, d, scope, operation, source)))
      case Instruction.GroupUMax(t, d, scope, operation, source) =>
        Some(Right(reduction("OpGroupUMax", CollectiveKind.UnsignedMax, Shape.Integer, t, d, scope, operation, source)))
      case Instruction.GroupSMin(t, d, scope, operation, source) =>
        Some(Right(reduction("OpGroupSMin", CollectiveKind.SignedMin, Shape.Integer, t, d, scope, operation, source)))
      case Instruction.GroupSMax(t, d, scope, operation, source) =>
        Some(Right(reduction("OpGroupSMax", CollectiveKind.SignedMax, Shape.Integer, t, d, scope, operation, source)))
      case Instruction.GroupIAdd(t, d, scope, operation, source) =>
        Some(Right(reduction("OpGroupIAdd", CollectiveKind.IntegerAdd, Shape.Integer, t, d, scope, operation, source)))
      case Instruction.GroupIMulKHR(t, d, scope, operation, source) =>
        Some(Right(reduction(
          "OpGroupIMulKHR",
          CollectiveKind.IntegerMultiply,
          Shape.Integer,
          t,
          d,
          scope,
          operation,
          source
        )))
      case Instruction.GroupBitwiseAndKHR(t, d, scope, operation, source) =>
        Some(Right(reduction(
          "OpGroupBitwiseAndKHR",
          CollectiveKind.BitwiseAnd,
          Shape.Integer,
          t,
          d,
          scope,
          operation,
          source
        )))
      case Instruction.GroupBitwiseOrKHR(t, d, scope, operation, source) =>
        Some(Right(reduction(
          "OpGroupBitwiseOrKHR",
          CollectiveKind.BitwiseOr,
          Shape.Integer,
          t,
          d,
          scope,
          operation,
          source
        )))
      case Instruction.GroupBitwiseXorKHR(t, d, scope, operation, source) =>
        Some(Right(reduction(
          "OpGroupBitwiseXorKHR",
          CollectiveKind.BitwiseXor,
          Shape.Integer,
          t,
          d,
          scope,
          operation,
          source
        )))
      case Instruction.GroupLogicalAndKHR(t, d, scope, operation, source) =>
        Some(Right(reduction(
          "OpGroupLogicalAndKHR",
          CollectiveKind.LogicalAnd,
          Shape.Boolean,
          t,
          d,
          scope,
          operation,
          source
        )))
      case Instruction.GroupLogicalOrKHR(t, d, scope, operation, source) =>
        Some(Right(reduction(
          "OpGroupLogicalOrKHR",
          CollectiveKind.LogicalOr,
          Shape.Boolean,
          t,
          d,
          scope,
          operation,
          source
        )))
    }

    private def scans: Description = {
      case Instruction.GroupLogicalXorKHR(t, d, scope, operation, source) =>
        Some(Right(reduction(
          "OpGroupLogicalXorKHR",
          CollectiveKind.LogicalXor,
          Shape.Boolean,
          t,
          d,
          scope,
          operation,
          source
        )))
      case Instruction.GroupFAdd(t, d, scope, operation, source) =>
        Some(Right(reduction(
          "OpGroupFAdd",
          CollectiveKind.FloatingAdd,
          Shape.Floating,
          t,
          d,
          scope,
          operation,
          source
        )))
      case Instruction.GroupFMin(t, d, scope, operation, source) =>
        Some(Right(reduction(
          "OpGroupFMin",
          CollectiveKind.FloatingMin,
          Shape.Floating,
          t,
          d,
          scope,
          operation,
          source
        )))
      case Instruction.GroupFMax(t, d, scope, operation, source) =>
        Some(Right(reduction(
          "OpGroupFMax",
          CollectiveKind.FloatingMax,
          Shape.Floating,
          t,
          d,
          scope,
          operation,
          source
        )))
      case Instruction.GroupFMulKHR(t, d, scope, operation, source) =>
        Some(Right(reduction(
          "OpGroupFMulKHR",
          CollectiveKind.FloatingMultiply,
          Shape.Floating,
          t,
          d,
          scope,
          operation,
          source
        )))
      case Instruction.GroupBroadcast(t, d, scope, source, local) =>
        Some(Right(Collective.Broadcast(
          ids = ResultIds(t, d),
          scope = id(scope),
          source = id(source),
          local = id(local)
        )))
      case Instruction.SubgroupShuffleINTEL(t, d, data, invocation) =>
        Some(Right(Collective.IntelShuffle(ResultIds(t, d), id(data), id(invocation), xor = false)))
      case Instruction.SubgroupShuffleXorINTEL(t, d, data, invocation) =>
        Some(Right(Collective.IntelShuffle(ResultIds(t, d), id(data), id(invocation), xor = true)))
      case Instruction.SubgroupShuffleUpINTEL(t, d, previous, current, delta) =>
        Some(
          Right(
            Collective.IntelShift(
              "OpSubgroupShuffleUpINTEL",
              ResultIds(t, d),
              id(previous),
              id(current),
              id(delta),
              ShuffleDirection.Up
            )
          )
        )
      case Instruction.SubgroupShuffleDownINTEL(t, d, current, next, delta) =>
        Some(
          Right(
            Collective.IntelShift(
              "OpSubgroupShuffleDownINTEL",
              ResultIds(t, d),
              id(current),
              id(next),
              id(delta),
              ShuffleDirection.Down
            )
          )
        )
      case Instruction.GroupNonUniformAllEqual(t, d, scope, source) =>
        Some(value(CollectiveKind.AllEqual, t, d, id(scope), source))
      case Instruction.GroupNonUniformBroadcastFirst(t, d, scope, source) =>
        Some(value(CollectiveKind.BroadcastFirst, t, d, id(scope), source))
      case Instruction.SubgroupFirstInvocationKHR(t, d, source) =>
        Some(value(CollectiveKind.BroadcastFirst, t, d, implied, source))
      case Instruction.SubgroupAllEqualKHR(t, d, source) =>
        Some(value(CollectiveKind.AllEqual, t, d, implied, source))
      case Instruction.GroupNonUniformBroadcast(t, d, scope, source, lane) =>
        Some(shuffle(t, d, id(scope), source, lane, Selection.Fixed))
      case Instruction.GroupNonUniformShuffle(t, d, scope, source, lane) =>
        Some(shuffle(t, d, id(scope), source, lane, Selection.Absolute))
      case Instruction.GroupNonUniformShuffleXor(t, d, scope, source, lane) =>
        Some(shuffle(t, d, id(scope), source, lane, Selection.Xor))
      case Instruction.GroupNonUniformRotateKHR(t, d, scope, source, delta, cluster) =>
        Some(rotate(t, d, scope, source, delta, cluster))
    }

    private def shuffles: Description = {
      case Instruction.GroupNonUniformShuffleUp(t, d, scope, source, lane) =>
        Some(shuffle(t, d, id(scope), source, lane, Selection.Up))
      case Instruction.GroupNonUniformShuffleDown(t, d, scope, source, lane) =>
        Some(shuffle(t, d, id(scope), source, lane, Selection.Down))
      case Instruction.GroupNonUniformQuadBroadcast(t, d, scope, source, index) =>
        Some(shuffle(t, d, id(scope), source, index, Selection.QuadBroadcast))
      case Instruction.GroupNonUniformQuadSwap(t, d, scope, source, direction) =>
        Some(shuffle(t, d, id(scope), source, direction, Selection.QuadSwap))
      case Instruction.SubgroupReadInvocationKHR(t, d, source, index) =>
        Some(shuffle(t, d, implied, source, index, Selection.Fixed))
      case Instruction.GroupNonUniformBallot(t, d, scope, predicate) => Some(ballot(t, d, id(scope), predicate))
      case Instruction.SubgroupBallotKHR(t, d, predicate)            => Some(ballot(t, d, implied, predicate))
      case Instruction.GroupNonUniformPartitionEXT(t, d, source)     =>
        Some(
          Either.cond(
            uint4(id(t)),
            Collective.Partition(ResultIds(t, d), id(source)),
            Fault.InvalidRequest("OpGroupNonUniformPartitionEXT requires a four-lane 32-bit integer result")
          )
        )
      case Instruction.GroupNonUniformInverseBallot(t, d, scope, source) =>
        Some(query(t, d, scope, source, -1, None))
      case Instruction.GroupNonUniformBallotBitExtract(t, d, scope, source, bit) =>
        Some(query(t, d, scope, source, -1, Some(bit)))
      case Instruction.GroupNonUniformBallotBitCount(t, d, scope, operation, source) =>
        Some(query(t, d, scope, source, id(operation), None))
      case Instruction.GroupNonUniformBallotFindLSB(t, d, scope, source) => Some(query(t, d, scope, source, 3, None))
      case Instruction.GroupNonUniformBallotFindMSB(t, d, scope, source) => Some(query(t, d, scope, source, 4, None))
      case Instruction.GroupNonUniformIAdd(t, d, scope, operation, source, cluster) =>
        Some(arithmetic(CollectiveKind.IntegerAdd, t, d, scope, operation, source, cluster))
      case Instruction.GroupNonUniformIMul(t, d, scope, operation, source, cluster) =>
        Some(arithmetic(CollectiveKind.IntegerMultiply, t, d, scope, operation, source, cluster))
      case Instruction.GroupNonUniformSMin(t, d, scope, operation, source, cluster) =>
        Some(arithmetic(CollectiveKind.SignedMin, t, d, scope, operation, source, cluster))
      case Instruction.GroupNonUniformUMin(t, d, scope, operation, source, cluster) =>
        Some(arithmetic(CollectiveKind.UnsignedMin, t, d, scope, operation, source, cluster))
      case Instruction.GroupNonUniformSMax(t, d, scope, operation, source, cluster) =>
        Some(arithmetic(CollectiveKind.SignedMax, t, d, scope, operation, source, cluster))
    }

    private def ballots: Description = {
      case Instruction.GroupNonUniformUMax(t, d, scope, operation, source, cluster) =>
        Some(arithmetic(CollectiveKind.UnsignedMax, t, d, scope, operation, source, cluster))
      case Instruction.GroupNonUniformBitwiseAnd(t, d, scope, operation, source, cluster) =>
        Some(arithmetic(CollectiveKind.BitwiseAnd, t, d, scope, operation, source, cluster))
      case Instruction.GroupNonUniformBitwiseOr(t, d, scope, operation, source, cluster) =>
        Some(arithmetic(CollectiveKind.BitwiseOr, t, d, scope, operation, source, cluster))
      case Instruction.GroupNonUniformBitwiseXor(t, d, scope, operation, source, cluster) =>
        Some(arithmetic(CollectiveKind.BitwiseXor, t, d, scope, operation, source, cluster))
      case Instruction.GroupNonUniformLogicalAnd(t, d, scope, operation, source, cluster) =>
        Some(arithmetic(CollectiveKind.LogicalAnd, t, d, scope, operation, source, cluster))
      case Instruction.GroupNonUniformLogicalOr(t, d, scope, operation, source, cluster) =>
        Some(arithmetic(CollectiveKind.LogicalOr, t, d, scope, operation, source, cluster))
      case Instruction.GroupNonUniformLogicalXor(t, d, scope, operation, source, cluster) =>
        Some(arithmetic(CollectiveKind.LogicalXor, t, d, scope, operation, source, cluster))
      case Instruction.GroupNonUniformFAdd(t, d, scope, operation, source, cluster) =>
        Some(floating("OpGroupNonUniformFAdd", CollectiveKind.FloatingAdd, t, d, scope, operation, source, cluster))
      case Instruction.GroupNonUniformFMul(t, d, scope, operation, source, cluster) =>
        Some(floating(
          "OpGroupNonUniformFMul",
          CollectiveKind.FloatingMultiply,
          t,
          d,
          scope,
          operation,
          source,
          cluster
        ))
      case Instruction.GroupNonUniformFMin(t, d, scope, operation, source, cluster) =>
        Some(floating("OpGroupNonUniformFMin", CollectiveKind.FloatingMin, t, d, scope, operation, source, cluster))
      case Instruction.GroupNonUniformFMax(t, d, scope, operation, source, cluster) =>
        Some(floating("OpGroupNonUniformFMax", CollectiveKind.FloatingMax, t, d, scope, operation, source, cluster))
      case Instruction.GroupIAddNonUniformAMD(t, d, scope, operation, source) =>
        Some(arithmetic(CollectiveKind.IntegerAdd, t, d, scope, operation, source, None))
      case Instruction.GroupUMinNonUniformAMD(t, d, scope, operation, source) =>
        Some(arithmetic(CollectiveKind.UnsignedMin, t, d, scope, operation, source, None))
      case Instruction.GroupSMinNonUniformAMD(t, d, scope, operation, source) =>
        Some(arithmetic(CollectiveKind.SignedMin, t, d, scope, operation, source, None))
      case Instruction.GroupUMaxNonUniformAMD(t, d, scope, operation, source) =>
        Some(arithmetic(CollectiveKind.UnsignedMax, t, d, scope, operation, source, None))
      case Instruction.GroupSMaxNonUniformAMD(t, d, scope, operation, source) =>
        Some(arithmetic(CollectiveKind.SignedMax, t, d, scope, operation, source, None))
      case Instruction.GroupFAddNonUniformAMD(t, d, scope, operation, source) =>
        Some(floating("OpGroupFAddNonUniformAMD", CollectiveKind.FloatingAdd, t, d, scope, operation, source, None))
      case Instruction.GroupFMinNonUniformAMD(t, d, scope, operation, source) =>
        Some(floating("OpGroupFMinNonUniformAMD", CollectiveKind.FloatingMin, t, d, scope, operation, source, None))
      case Instruction.GroupFMaxNonUniformAMD(t, d, scope, operation, source) =>
        Some(floating("OpGroupFMaxNonUniformAMD", CollectiveKind.FloatingMax, t, d, scope, operation, source, None))
    }
  }
}
