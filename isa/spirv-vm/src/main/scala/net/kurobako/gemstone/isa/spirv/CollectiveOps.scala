package net.kurobako.gemstone.isa.spirv

import Op.*
import VMCollectives.Kind as CollectiveKind
import VMCollectives.Payload as CollectivePayload
import VMCollectives.Request as CollectiveRequest
import VMInvocation.*
import VMMemory.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Bits.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.vm.*
import scala.collection.mutable

/** Workgroup and subgroup collectives, barriers and fragment derivatives: lowering builds the rendezvous request each
  * lane suspends with, and resolution combines the requests of the lanes that meet.
  */
// result type, site and operand ids are the raw operands of each opcode; a holder would allocate per instruction
@SuppressWarnings(Array("baumkuchen:data-clump"))
private object CollectiveOps {
  import Operand.*

  /** Which way a subgroup shift reads from. */
  enum ShuffleDirection derives CanEqual {
    case Up, Down

    def label: String = this match {
      case Up   => "up"
      case Down => "down"
    }
  }

  def lower(site: Site): PartialFunction[Instruction, Op] =
    workgroup(site).orElse(nonUniform(site)).orElse(extensions(site)).orElse(derivatives(site))

  // split and named barriers, vendor subgroup reductions, partitions and matrix products
  private def extensions(site: Site): PartialFunction[Instruction, Op] = {
    // every invocation arrives before it waits, so the wait's rendezvous orders whatever the arrival releases
    case Instruction.ControlBarrierArriveEXT(executionScope, memory, semantics) =>
      step { (execution, _) =>
        val values = execution.values
        scalar(values, id(executionScope)).flatMap(scope =>
          Either.cond(isWorkgroupOrSubgroup(scope), (), scopeFault("OpControlBarrierArriveEXT", scope)).flatMap(_ =>
            memoryControls(values, id(memory), id(semantics), "OpControlBarrierArriveEXT")
          )
        )
      }
    case Instruction.ControlBarrierWaitEXT(execution, memory, semantics) =>
      barrier(id(execution), id(memory), id(semantics))
    case Instruction.SubgroupMatrixMultiplyAccumulateINTEL(t, d, inner, a, b, c, operands) =>
      matrixMultiply(site, id(t), id(d), id(inner), Vec(id(a), id(b), id(c)), operands.fold(0L)(unsigned))
    case Instruction.NamedBarrierInitialize(t, d, count) =>
      step((execution, _) => scalar(execution.values, id(count)).map(execution.values.updateScalar(id(d), id(t), _)))
    case Instruction.MemoryNamedBarrier(barrier, memory, semantics) =>
      namedBarrier(id(barrier), id(memory), id(semantics))
    // SPV_AMD_shader_ballot's reductions and scans over the active lanes of a subgroup
    case Instruction.GroupIAddNonUniformAMD(t, d, scope, operation, value) =>
      subgroupArithmetic(site, CollectiveKind.IntegerAdd, t, d, scope, operation, value, None)
    case Instruction.GroupUMinNonUniformAMD(t, d, scope, operation, value) =>
      subgroupArithmetic(site, CollectiveKind.UnsignedMin, t, d, scope, operation, value, None)
    case Instruction.GroupSMinNonUniformAMD(t, d, scope, operation, value) =>
      subgroupArithmetic(site, CollectiveKind.SignedMin, t, d, scope, operation, value, None)
    case Instruction.GroupUMaxNonUniformAMD(t, d, scope, operation, value) =>
      subgroupArithmetic(site, CollectiveKind.UnsignedMax, t, d, scope, operation, value, None)
    case Instruction.GroupSMaxNonUniformAMD(t, d, scope, operation, value) =>
      subgroupArithmetic(site, CollectiveKind.SignedMax, t, d, scope, operation, value, None)
    case instruction @ Instruction.GroupFAddNonUniformAMD(t, d, scope, operation, value) =>
      subgroupFloating(site, instruction, CollectiveKind.FloatingAdd, t, d, scope, operation, value, None)
    case instruction @ Instruction.GroupFMinNonUniformAMD(t, d, scope, operation, value) =>
      subgroupFloating(site, instruction, CollectiveKind.FloatingMin, t, d, scope, operation, value, None)
    case instruction @ Instruction.GroupFMaxNonUniformAMD(t, d, scope, operation, value) =>
      subgroupFloating(site, instruction, CollectiveKind.FloatingMax, t, d, scope, operation, value, None)
    case Instruction.GroupNonUniformPartitionEXT(t, d, value) => partition(site, id(t), id(d), id(value))
  }

  private def workgroup(site: Site): PartialFunction[Instruction, Op] = {
    case Instruction.ControlBarrier(execution, memory, semantics) => barrier(id(execution), id(memory), id(semantics))
    case Instruction.GroupReserveReadPipePackets(t, d, scope, pipe, count, _, _) =>
      pipeReservation(id(t), id(d), id(scope), id(pipe), id(count), end = PipeEnd.Read)
    case Instruction.GroupReserveWritePipePackets(t, d, scope, pipe, count, _, _) =>
      pipeReservation(id(t), id(d), id(scope), id(pipe), id(count), end = PipeEnd.Write)
    case Instruction.GroupAny(t, d, scope, predicate) =>
      boolean(site, "OpGroupAny", CollectiveKind.Any, t, d, id(scope), predicate)
    case Instruction.GroupAll(t, d, scope, predicate) =>
      boolean(site, "OpGroupAll", CollectiveKind.All, t, d, id(scope), predicate)
    case Instruction.GroupUMin(t, d, scope, operation, value) =>
      group(site, "OpGroupUMin", CollectiveKind.UnsignedMin, Shape.Integer, t, d, scope, operation, value)
    case Instruction.GroupUMax(t, d, scope, operation, value) =>
      group(site, "OpGroupUMax", CollectiveKind.UnsignedMax, Shape.Integer, t, d, scope, operation, value)
    case Instruction.GroupSMin(t, d, scope, operation, value) =>
      group(site, "OpGroupSMin", CollectiveKind.SignedMin, Shape.Integer, t, d, scope, operation, value)
    case Instruction.GroupSMax(t, d, scope, operation, value) =>
      group(site, "OpGroupSMax", CollectiveKind.SignedMax, Shape.Integer, t, d, scope, operation, value)
    case Instruction.GroupIAdd(t, d, scope, operation, value) =>
      group(site, "OpGroupIAdd", CollectiveKind.IntegerAdd, Shape.Integer, t, d, scope, operation, value)
    case Instruction.GroupIMulKHR(t, d, scope, operation, value) =>
      group(site, "OpGroupIMulKHR", CollectiveKind.IntegerMultiply, Shape.Integer, t, d, scope, operation, value)
    case Instruction.GroupBitwiseAndKHR(t, d, scope, operation, value) =>
      group(site, "OpGroupBitwiseAndKHR", CollectiveKind.BitwiseAnd, Shape.Integer, t, d, scope, operation, value)
    case Instruction.GroupBitwiseOrKHR(t, d, scope, operation, value) =>
      group(site, "OpGroupBitwiseOrKHR", CollectiveKind.BitwiseOr, Shape.Integer, t, d, scope, operation, value)
    case Instruction.GroupBitwiseXorKHR(t, d, scope, operation, value) =>
      group(site, "OpGroupBitwiseXorKHR", CollectiveKind.BitwiseXor, Shape.Integer, t, d, scope, operation, value)
    case Instruction.GroupLogicalAndKHR(t, d, scope, operation, value) =>
      group(site, "OpGroupLogicalAndKHR", CollectiveKind.LogicalAnd, Shape.Boolean, t, d, scope, operation, value)
    case Instruction.GroupLogicalOrKHR(t, d, scope, operation, value) =>
      group(site, "OpGroupLogicalOrKHR", CollectiveKind.LogicalOr, Shape.Boolean, t, d, scope, operation, value)
    case Instruction.GroupLogicalXorKHR(t, d, scope, operation, value) =>
      group(site, "OpGroupLogicalXorKHR", CollectiveKind.LogicalXor, Shape.Boolean, t, d, scope, operation, value)
    case Instruction.GroupFAdd(t, d, scope, operation, value) =>
      group(site, "OpGroupFAdd", CollectiveKind.FloatingAdd, Shape.Floating, t, d, scope, operation, value)
    case Instruction.GroupFMin(t, d, scope, operation, value) =>
      group(site, "OpGroupFMin", CollectiveKind.FloatingMin, Shape.Floating, t, d, scope, operation, value)
    case Instruction.GroupFMax(t, d, scope, operation, value) =>
      group(site, "OpGroupFMax", CollectiveKind.FloatingMax, Shape.Floating, t, d, scope, operation, value)
    case Instruction.GroupFMulKHR(t, d, scope, operation, value) =>
      group(site, "OpGroupFMulKHR", CollectiveKind.FloatingMultiply, Shape.Floating, t, d, scope, operation, value)
    case Instruction.SubgroupShuffleINTEL(t, d, data, invocation) =>
      intelShuffle(id(t), id(d), id(data), id(invocation), ShuffleIndex.Absolute)
    case Instruction.SubgroupShuffleXorINTEL(t, d, data, invocation) =>
      intelShuffle(id(t), id(d), id(data), id(invocation), ShuffleIndex.Xor)
    case Instruction.SubgroupShuffleUpINTEL(t, d, previous, current, delta) =>
      intelShift(id(t), id(d), id(previous), id(current), id(delta), ShuffleDirection.Up, "OpSubgroupShuffleUpINTEL")
    case Instruction.SubgroupShuffleDownINTEL(t, d, current, next, delta) =>
      intelShift(id(t), id(d), id(current), id(next), id(delta), ShuffleDirection.Down, "OpSubgroupShuffleDownINTEL")
    case Instruction.GroupBroadcast(t, d, scope, value, local) =>
      broadcast(site, resultType = id(t), dest = id(d), scope = id(scope), source = id(value), local = id(local))
  }

  private def nonUniform(site: Site): PartialFunction[Instruction, Op] = {
    case Instruction.GroupNonUniformElect(t, d, scope) =>
      if (!site.program.booleanLanes(id(t)).contains(1))
        Fail(Fault.InvalidRequest("subgroup election requires Subgroup scope and boolean result"))
      else
        Dynamic { (execution, _) =>
          scalar(execution.values, id(scope)).flatMap { value =>
            if (value != Scope.Subgroup)
              Left(Fault.InvalidRequest("subgroup election requires Subgroup scope and boolean result"))
            else
              suspend(
                execution,
                CollectiveRequest(
                  CollectiveKind.Elect,
                  _,
                  _,
                  Scope.Subgroup,
                  CollectivePayload.Value(id(t), id(d), VMValue.Bits(id(t), Array(1L))),
                  nonUniform = true
                )
              )
          }
        }
    case Instruction.GroupNonUniformAll(t, d, scope, value) =>
      nonUniform(boolean(site, "OpGroupNonUniformAll", CollectiveKind.All, t, d, id(scope), value))
    case Instruction.GroupNonUniformAny(t, d, scope, value) =>
      nonUniform(boolean(site, "OpGroupNonUniformAny", CollectiveKind.Any, t, d, id(scope), value))
    case Instruction.GroupNonUniformAllEqual(t, d, scope, value) =>
      subgroupValue(site, CollectiveKind.AllEqual, id(t), id(d), id(scope), id(value))
    case Instruction.GroupNonUniformBroadcastFirst(t, d, scope, value) =>
      subgroupValue(site, CollectiveKind.BroadcastFirst, id(t), id(d), id(scope), id(value))
    case Instruction.GroupNonUniformBroadcast(t, d, scope, value, lane) =>
      subgroupShuffle(site, t, d, id(scope), value, lane)((_, index, _) => index)
    case Instruction.GroupNonUniformShuffle(t, d, scope, value, lane) =>
      subgroupShuffle(site, t, d, id(scope), value, lane)((_, index, _) => index)
    case Instruction.GroupNonUniformShuffleXor(t, d, scope, value, lane) =>
      subgroupShuffle(site, t, d, id(scope), value, lane)((local, index, _) => local ^ index)
    case Instruction.GroupNonUniformRotateKHR(t, d, scope, value, delta, cluster) =>
      // Lanes rotate within aligned clusters, or within the whole subgroup.
      val size = cluster.map(operand => site.program.constants.get(id(operand)))
      size match {
        case Some(Some(VMValue.Bits(_, Array(lanes)))) if lanes <= 0L || (lanes & (lanes - 1L)) != 0L =>
          Fail(Fault.InvalidRequest(s"OpGroupNonUniformRotateKHR cluster size $lanes is not a power of two"))
        case Some(Some(VMValue.Bits(_, Array(lanes)))) =>
          subgroupShuffle(site, t, d, id(scope), value, delta)((local, index, _) =>
            local - local % lanes + (local % lanes + index) % lanes
          )
        case Some(_) => Fail(Fault.InvalidRequest("OpGroupNonUniformRotateKHR cluster size must be a scalar constant"))
        case None    =>
          subgroupShuffle(site, t, d, id(scope), value, delta)((local, index, subgroup) => (local + index) % subgroup)
      }
    case Instruction.GroupNonUniformShuffleUp(t, d, scope, value, lane) =>
      subgroupShuffle(site, t, d, id(scope), value, lane)((local, index, _) => local - index)
    case Instruction.GroupNonUniformShuffleDown(t, d, scope, value, lane) =>
      subgroupShuffle(site, t, d, id(scope), value, lane)((local, index, _) => local + index)
    case Instruction.GroupNonUniformBallot(t, d, scope, value) => ballot(site, id(t), id(d), id(scope), id(value))
    case Instruction.GroupNonUniformQuadBroadcast(t, d, scope, value, index) =>
      subgroupShuffle(site, t, d, id(scope), value, index)((local, lane, _) => (local & ~3L) | lane)
    // directions 0, 1 and 2 swap horizontally, vertically and diagonally within the quad
    case Instruction.GroupNonUniformQuadSwap(t, d, scope, value, direction) =>
      subgroupShuffle(site, t, d, id(scope), value, direction)((local, swap, _) => local ^ (swap + 1L))
    case Instruction.SubgroupBallotKHR(t, d, predicate) => ballot(site, id(t), id(d), ImpliedSubgroup, id(predicate))
    case Instruction.SubgroupFirstInvocationKHR(t, d, value) =>
      subgroupValue(site, CollectiveKind.BroadcastFirst, id(t), id(d), ImpliedSubgroup, id(value))
    case Instruction.SubgroupAllKHR(t, d, predicate) =>
      nonUniform(boolean(site, "OpSubgroupAllKHR", CollectiveKind.All, t, d, ImpliedSubgroup, predicate))
    case Instruction.SubgroupAnyKHR(t, d, predicate) =>
      nonUniform(boolean(site, "OpSubgroupAnyKHR", CollectiveKind.Any, t, d, ImpliedSubgroup, predicate))
    case Instruction.SubgroupAllEqualKHR(t, d, predicate) =>
      subgroupValue(site, CollectiveKind.AllEqual, id(t), id(d), ImpliedSubgroup, id(predicate))
    case Instruction.SubgroupReadInvocationKHR(t, d, value, index) =>
      subgroupShuffle(site, t, d, ImpliedSubgroup, value, index)((_, lane, _) => lane)
    case Instruction.GroupNonUniformIAdd(t, d, scope, operation, value, cluster) =>
      subgroupArithmetic(site, CollectiveKind.IntegerAdd, t, d, scope, operation, value, cluster)
    case Instruction.GroupNonUniformIMul(t, d, scope, operation, value, cluster) =>
      subgroupArithmetic(site, CollectiveKind.IntegerMultiply, t, d, scope, operation, value, cluster)
    case Instruction.GroupNonUniformSMin(t, d, scope, operation, value, cluster) =>
      subgroupArithmetic(site, CollectiveKind.SignedMin, t, d, scope, operation, value, cluster)
    case Instruction.GroupNonUniformUMin(t, d, scope, operation, value, cluster) =>
      subgroupArithmetic(site, CollectiveKind.UnsignedMin, t, d, scope, operation, value, cluster)
    case Instruction.GroupNonUniformSMax(t, d, scope, operation, value, cluster) =>
      subgroupArithmetic(site, CollectiveKind.SignedMax, t, d, scope, operation, value, cluster)
    case Instruction.GroupNonUniformUMax(t, d, scope, operation, value, cluster) =>
      subgroupArithmetic(site, CollectiveKind.UnsignedMax, t, d, scope, operation, value, cluster)
    case Instruction.GroupNonUniformBitwiseAnd(t, d, scope, operation, value, cluster) =>
      subgroupArithmetic(site, CollectiveKind.BitwiseAnd, t, d, scope, operation, value, cluster)
    case Instruction.GroupNonUniformBitwiseOr(t, d, scope, operation, value, cluster) =>
      subgroupArithmetic(site, CollectiveKind.BitwiseOr, t, d, scope, operation, value, cluster)
    case Instruction.GroupNonUniformBitwiseXor(t, d, scope, operation, value, cluster) =>
      subgroupArithmetic(site, CollectiveKind.BitwiseXor, t, d, scope, operation, value, cluster)
    case Instruction.GroupNonUniformLogicalAnd(t, d, scope, operation, value, cluster) =>
      subgroupArithmetic(site, CollectiveKind.LogicalAnd, t, d, scope, operation, value, cluster)
    case Instruction.GroupNonUniformLogicalOr(t, d, scope, operation, value, cluster) =>
      subgroupArithmetic(site, CollectiveKind.LogicalOr, t, d, scope, operation, value, cluster)
    case Instruction.GroupNonUniformLogicalXor(t, d, scope, operation, value, cluster) =>
      subgroupArithmetic(site, CollectiveKind.LogicalXor, t, d, scope, operation, value, cluster)
    case instruction @ Instruction.GroupNonUniformFAdd(t, d, scope, operation, value, cluster) =>
      subgroupFloating(site, instruction, CollectiveKind.FloatingAdd, t, d, scope, operation, value, cluster)
    case instruction @ Instruction.GroupNonUniformFMul(t, d, scope, operation, value, cluster) =>
      subgroupFloating(site, instruction, CollectiveKind.FloatingMultiply, t, d, scope, operation, value, cluster)
    case instruction @ Instruction.GroupNonUniformFMin(t, d, scope, operation, value, cluster) =>
      subgroupFloating(site, instruction, CollectiveKind.FloatingMin, t, d, scope, operation, value, cluster)
    case instruction @ Instruction.GroupNonUniformFMax(t, d, scope, operation, value, cluster) =>
      subgroupFloating(site, instruction, CollectiveKind.FloatingMax, t, d, scope, operation, value, cluster)
    case Instruction.GroupNonUniformInverseBallot(t, d, scope, source) =>
      ballotQuery(site, resultType = id(t), dest = id(d), scope = id(scope), source = id(source), mode = -1, None)
    case Instruction.GroupNonUniformBallotBitExtract(t, d, scope, source, bit) =>
      ballotQuery(
        site,
        resultType = id(t),
        dest = id(d),
        scope = id(scope),
        source = id(source),
        mode = -1,
        Some(id(bit))
      )
    case Instruction.GroupNonUniformBallotBitCount(t, d, scope, operation, source) =>
      ballotQuery(
        site,
        resultType = id(t),
        dest = id(d),
        scope = id(scope),
        source = id(source),
        mode = id(operation),
        None
      )
    case Instruction.GroupNonUniformBallotFindLSB(t, d, scope, source) =>
      ballotQuery(site, resultType = id(t), dest = id(d), scope = id(scope), source = id(source), mode = 3, None)
    case Instruction.GroupNonUniformBallotFindMSB(t, d, scope, source) =>
      ballotQuery(site, resultType = id(t), dest = id(d), scope = id(scope), source = id(source), mode = 4, None)
  }

  private def derivatives(site: Site): PartialFunction[Instruction, Op] = {
    case Instruction.DPdx(t, d, v)         => derivative(site, id(t), id(d), id(v), Derivative.Dx)
    case Instruction.DPdy(t, d, v)         => derivative(site, id(t), id(d), id(v), Derivative.Dy)
    case Instruction.Fwidth(t, d, v)       => derivative(site, id(t), id(d), id(v), Derivative.Width)
    case Instruction.DPdxFine(t, d, v)     => derivative(site, id(t), id(d), id(v), Derivative.Dx)
    case Instruction.DPdyFine(t, d, v)     => derivative(site, id(t), id(d), id(v), Derivative.Dy)
    case Instruction.FwidthFine(t, d, v)   => derivative(site, id(t), id(d), id(v), Derivative.Width)
    case Instruction.DPdxCoarse(t, d, v)   => derivative(site, id(t), id(d), id(v), Derivative.CoarseDx)
    case Instruction.DPdyCoarse(t, d, v)   => derivative(site, id(t), id(d), id(v), Derivative.CoarseDy)
    case Instruction.FwidthCoarse(t, d, v) => derivative(site, id(t), id(d), id(v), Derivative.CoarseWidth)
  }

  private def isWorkgroupOrSubgroup(scope: Long): Boolean = scope == Scope.Workgroup || scope == Scope.Subgroup

  private def scopeFault(opcode: String, scope: Long): Fault =
    Fault.Unsupported(s"$opcode execution scope $scope is not Subgroup or Workgroup")

  private enum ShuffleIndex derives CanEqual {
    case Absolute, Xor
  }

  private trait ShuffleSource { def apply(local: Long, index: Long, subgroup: Long): Long }

  private val SingleMemoryOrders = Vec(0L, 2L, 4L, 8L, 16L)

  private def barrier(executionScope: Int, memoryScope: Int, semantics: Int): Op = Dynamic { (execution, _) =>
    val values = execution.values
    for {
      scope  <- scalar(values, executionScope)
      _      <- Either.cond(isWorkgroupOrSubgroup(scope), (), scopeFault("OpControlBarrier", scope))
      _      <- memoryControls(values, memoryScope, semantics, "OpControlBarrier")
      memory <- scalar(values, memoryScope)
      flags  <- scalar(values, semantics)
      _      <- Either.cond(
        SingleMemoryOrders.contains(flags & 0x1eL),
        (),
        Fault.InvalidRequest(f"OpControlBarrier memory semantics 0x$flags%x are invalid or unsupported")
      )
      _ <- suspend(
        execution,
        CollectiveRequest(CollectiveKind.Barrier, _, _, scope.toInt, CollectivePayload.Barrier(memory.toInt, flags))
      )
    } yield ()
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
      resultType: Int,
      dest: Int,
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
    val result      = element(resultType, 0x8L)
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
    ) { found =>
      val selector = found.selector
      Dynamic { (execution, _) =>
        val values = execution.values
        Traversal.traverse(operands)(bits(values, _)).flatMap { parts =>
          suspend(
            execution,
            CollectiveRequest(
              CollectiveKind.MatrixMultiply,
              _,
              _,
              Scope.Subgroup,
              CollectivePayload.Value(resultType, dest, VMValue.Bits(resultType, Array.concat(parts*))),
              selector = selector ++ parts.map(_.length.toLong)
            )
          )
        }
      }
    }
  }

  /** A named barrier spanning every subgroup of the workgroup waits as a workgroup barrier does. */
  private def namedBarrier(barrier: Int, memoryScope: Int, semantics: Int): Op = Dynamic { (execution, _) =>
    val values = execution.values
    for {
      count <- scalar(values, barrier)
      _     <- Either.cond(
        count >= execution.invocation.numSubgroups,
        (),
        Fault.Unsupported(
          s"OpMemoryNamedBarrier over $count of the workgroup's ${execution.invocation.numSubgroups} subgroups"
        )
      )
      _      <- memoryControls(values, memoryScope, semantics, "OpMemoryNamedBarrier")
      memory <- scalar(values, memoryScope)
      flags  <- scalar(values, semantics)
      _      <- suspend(
        execution,
        CollectiveRequest(CollectiveKind.Barrier, _, _, Scope.Workgroup, CollectivePayload.Barrier(memory.toInt, flags))
      )
    } yield ()
  }

  private def boolean(
      site: Site,
      opcode: String,
      kind: CollectiveKind,
      resultType: Operand,
      dest: Operand,
      scope: Int,
      predicate: Operand
  )
      : Op = {
    val program = site.program
    val valid   = program.booleanLanes(id(resultType)).contains(1) &&
      program.booleanLanes(program.valueType(id(predicate))).contains(1)
    Dynamic { (execution, _) =>
      (scopeOf(execution.values, scope), execution.values.get(id(predicate))) match {
        case (Right(value), Some(predicateValue @ VMValue.Bits(_, Array(_))))
            if isWorkgroupOrSubgroup(value) && valid =>
          suspend(
            execution,
            CollectiveRequest(
              kind,
              _,
              _,
              value.toInt,
              CollectivePayload.Value(id(resultType), id(dest), predicateValue)
            )
          )
        case (Right(value), _) if !isWorkgroupOrSubgroup(value) => Left(scopeFault(opcode, value))
        case (Right(_), Some(_))                                =>
          Left(Fault.InvalidRequest(s"$opcode requires scalar boolean predicate and result types"))
        case (Left(error), _) => Left(error)
        case (_, None)        => Left(undefined(id(predicate)))
      }
    }
  }

  private enum Shape derives CanEqual {
    case Integer, Boolean, Floating
  }

  /** A workgroup or subgroup reduction or scan whose operand has the result's shape. */
  private def group(
      site: Site,
      opcode: String,
      kind: CollectiveKind,
      shape: Shape,
      resultType: Operand,
      dest: Operand,
      scope: Operand,
      operation: Operand,
      source: Operand,
      cluster: Option[Operand] = None
  ): Op = {
    val program    = site.program
    val sourceType = program.valueType(id(source))
    val matching   = shape match {
      case Shape.Boolean => program.booleanLanes(id(resultType)).isDefined &&
        program.booleanLanes(id(resultType)) == program.booleanLanes(sourceType)
      case Shape.Integer => program.integerShape(id(resultType)).isDefined &&
        program.integerShape(id(resultType)) == program.integerShape(sourceType)
      case Shape.Floating => program.floatingShape(id(resultType)).isDefined &&
        program.floatingShape(id(resultType)) == program.floatingShape(sourceType)
    }
    val described = shape match {
      case Shape.Boolean  => "boolean"
      case Shape.Integer  => "integer"
      case Shape.Floating => "floating"
    }
    val groupOperation = unsigned(operation)
    Dynamic { (execution, _) =>
      (scalar(execution.values, id(scope)), execution.values.get(id(source))) match {
        case (Right(value), Some(sourceValue: VMValue.Bits))
            if isWorkgroupOrSubgroup(value) && (groupOperation <= 2L || cluster.nonEmpty) && matching =>
          clusterSize(execution.values, cluster).flatMap(selector =>
            suspend(
              execution,
              CollectiveRequest(
                kind,
                _,
                _,
                value.toInt,
                CollectivePayload.Value(id(resultType), id(dest), sourceValue),
                groupOperation.toInt,
                selector
              )
            )
          )
        case (Right(_), _) if groupOperation > 2L && cluster.isEmpty =>
          Left(
            Fault.Unsupported(s"$opcode group operation $groupOperation is not Reduce, InclusiveScan, or ExclusiveScan")
          )
        case (Right(value), _) if !isWorkgroupOrSubgroup(value) => Left(scopeFault(opcode, value))
        case (Right(_), Some(_))                                =>
          Left(Fault.InvalidRequest(s"$opcode requires matching $described scalar or vector types"))
        case (Left(error), _) => Left(error)
        case (_, None)        => Left(undefined(id(source)))
      }
    }
  }

  private def intelShuffle(resultType: Int, dest: Int, data: Int, invocation: Int, index: ShuffleIndex): Op =
    Dynamic { (execution, _) =>
      (execution.values.get(data), execution.values.get(invocation)) match {
        case (Some(value @ VMValue.Bits(dataType, lanes)), Some(VMValue.Bits(selectorType, Array(selector))))
            if dataType == resultType && lanes.nonEmpty =>
          val source = index match {
            case ShuffleIndex.Absolute => selector
            case ShuffleIndex.Xor      => selector ^ execution.invocation.subgroupLocal.toLong
          }
          suspend(
            execution,
            CollectiveRequest(
              CollectiveKind.Shuffle,
              _,
              _,
              Scope.Subgroup,
              CollectivePayload.Shuffle(resultType, dest, value, VMValue.Bits(selectorType, Array(source)))
            )
          )
        case (Some(_), Some(VMValue.Bits(_, lanes))) if lanes.length != 1 =>
          Left(Fault.InvalidRequest("OpSubgroupShuffleINTEL invocation id must be scalar"))
        case (Some(_), Some(_)) =>
          Left(Fault.InvalidRequest("OpSubgroupShuffleINTEL requires matching data and result types"))
        case (None, _) => Left(undefined(data))
        case (_, None) => Left(undefined(invocation))
      }
    }

  /** Intel shifts read `delta` lanes up from the previous or down into the next subgroup-sized window. */
  def intelShift(
      resultType: Int,
      dest: Int,
      first: Int,
      second: Int,
      delta: Int,
      direction: ShuffleDirection,
      opcode: String
  ): Op = Dynamic { (execution, _) =>
    val values = execution.values
    (values.get(first), values.get(second), scalar(values, delta)) match {
      case (
            Some(left @ VMValue.Bits(leftType, leftLanes)),
            Some(right @ VMValue.Bits(rightType, rightLanes)),
            Right(distance)
          )
          if leftType == resultType && rightType == resultType && leftLanes.length == rightLanes.length &&
            distance >= 0L =>
        val (kind, payload) = direction match {
          case ShuffleDirection.Up =>
            (CollectiveKind.ShuffleUp, CollectivePayload.ShuffleUp(resultType, dest, left, right, distance))
          case ShuffleDirection.Down =>
            (CollectiveKind.ShuffleDown, CollectivePayload.ShuffleDown(resultType, dest, left, right, distance))
        }
        suspend(execution, CollectiveRequest(kind, _, _, Scope.Subgroup, payload))
      case (Some(_), Some(_), Right(distance)) if distance < 0L =>
        Left(Fault.InvalidRequest(s"$opcode delta must be non-negative"))
      case (Some(_), Some(_), Right(_)) =>
        Left(Fault.InvalidRequest(s"$opcode requires matching result and operand types"))
      case (_, _, Left(error)) => Left(error)
      case (None, _, _)        => Left(undefined(first))
      case (_, None, _)        => Left(undefined(second))
    }
  }

  private def broadcast(site: Site, resultType: Int, dest: Int, scope: Int, source: Int, local: Int): Op = {
    val program = site.program
    Dynamic { (execution, _) =>
      val values = execution.values
      (scalar(values, scope), values.get(source), values.get(local)) match {
        case (Right(value), Some(sourceValue @ VMValue.Bits(sourceType, _)), Some(VMValue.Bits(localType, lanes)))
            if isWorkgroupOrSubgroup(value) && sourceType == resultType &&
              program.integerShape(localType).exists((_, count) => count == lanes.length && count >= 1 && count <= 3) =>
          val width = program.integerShape(localType).map(_._1).getOrElse(64: Int in 1 ~ 64)
          suspend(
            execution,
            CollectiveRequest(
              CollectiveKind.Broadcast,
              _,
              _,
              value.toInt,
              CollectivePayload.Value(resultType, dest, sourceValue),
              selector = Vec.from(lanes.map(_ & mask(width)))
            )
          )
        case (Right(value), _, _) if !isWorkgroupOrSubgroup(value) => Left(scopeFault("OpGroupBroadcast", value))
        case (Right(_), None, _)                                   => Left(undefined(source))
        case (Right(_), _, None)                                   => Left(undefined(local))
        case (Right(_), _, _)                                      =>
          Left(Fault.InvalidRequest(
            "OpGroupBroadcast requires matching result/source bit types and a scalar, two-lane or three-lane integer local id"
          ))
        case (Left(error), _, _) => Left(error)
      }
    }
  }

  private def pipeReservation(resultType: Int, dest: Int, scope: Int, pipe: Int, count: Int, end: PipeEnd): Op =
    Dynamic { (execution, _) =>
      val values = execution.values
      for {
        group   <- scalar(values, scope)
        _       <- Either.cond(isWorkgroupOrSubgroup(group), (), scopeFault("pipe reservation", group))
        handle  <- address(values, pipe)
        packets <- scalar(values, count)
        _       <- suspend(
          execution,
          CollectiveRequest(
            CollectiveKind.PipeReservation,
            _,
            _,
            group.toInt,
            CollectivePayload.PipeReservation(resultType, dest, handle, packets & 0xffffffffL, end)
          )
        )
      } yield ()
    }

  /** Non-uniform requests rendezvous among the active lanes of a subgroup. */
  /** The scope id of the KHR ballot and vote instructions, which act on the subgroup without one. */
  private val ImpliedSubgroup = -1

  private def scopeOf(values: InvocationValues, scope: Int): Either[Fault, Long] =
    if (scope == ImpliedSubgroup) Right(Scope.Subgroup.toLong) else scalar(values, scope)

  private def nonUniform(op: Op, message: NonEmpty[String] = "nonuniform operations require Subgroup scope"): Op =
    Dynamic {
      (execution, memory) =>
        op.run(execution, memory).flatMap { _ =>
          execution.suspended match {
            case Some(request) if request.scope != Scope.Subgroup =>
              execution.resume()
              Left(Fault.InvalidRequest(message))
            case Some(request) =>
              execution.suspend(request.copy(nonUniform = true))
              Done
            case None => Done
          }
        }
    }

  private def subgroupValue(
      site: Site,
      kind: CollectiveKind,
      resultType: Int,
      dest: Int,
      scope: Int,
      source: Int,
      operation: Int = 0,
      cluster: Option[Operand] = None
  ): Op = {
    val compatible = kind == CollectiveKind.AllEqual && site.program.booleanLanes(resultType).contains(1)
    Dynamic { (execution, _) =>
      (scopeOf(execution.values, scope), execution.values.get(source)) match {
        case (Right(Scope.Subgroup), Some(value @ VMValue.Bits(sourceType, _)))
            if compatible || sourceType == resultType =>
          clusterSize(execution.values, cluster).flatMap(selector =>
            suspend(
              execution,
              CollectiveRequest(
                kind,
                _,
                _,
                Scope.Subgroup,
                CollectivePayload.Value(resultType, dest, value),
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
    }
  }

  private def subgroupShuffle(
      site: Site,
      resultType: Operand,
      dest: Operand,
      scope: Int,
      source: Operand,
      lane: Operand
  )(
      select: ShuffleSource
  ): Op = {
    val request   = subgroupValue(site, CollectiveKind.Shuffle, id(resultType), id(dest), scope, id(source))
    val laneType  = site.program.valueType(id(lane))
    val validLane = site.program.integerShape(laneType).contains(32 -> 1)
    Dynamic { (execution, memory) =>
      request.run(execution, memory).flatMap { _ =>
        val outcome = for {
          index <- scalar(execution.values, id(lane))
          _ <- Either.cond(validLane, (), Fault.InvalidRequest("subgroup shuffle lane must be a 32-bit scalar integer"))
          data <- execution.values.get(id(source)).toRight(undefined(id(source)))
        } yield execution.suspended.foreach { pending =>
          val selected =
            select(execution.invocation.subgroupLocal.toLong, index, execution.invocation.maxSubgroupSize.toLong)
          execution.suspend(pending.copy(payload =
            CollectivePayload.Shuffle(id(resultType), id(dest), data, VMValue.Bits(laneType, Array(selected)))
          ))
        }
        outcome.left.map { error =>
          execution.resume()
          error
        }
      }
    }
  }

  private def ballot(site: Site, resultType: Int, dest: Int, scope: Int, predicate: Int): Op = {
    val program = site.program
    val valid   = program.integerShape(resultType).contains(32 -> 4) &&
      program.booleanLanes(program.valueType(predicate)).contains(1)
    Dynamic { (execution, _) =>
      (scopeOf(execution.values, scope), execution.values.get(predicate)) match {
        case (Right(Scope.Subgroup), Some(value @ VMValue.Bits(_, Array(_)))) if valid =>
          suspend(
            execution,
            CollectiveRequest(
              CollectiveKind.Ballot,
              _,
              _,
              Scope.Subgroup,
              CollectivePayload.Value(resultType, dest, value),
              nonUniform = true
            )
          )
        case (Right(value), _) if value != Scope.Subgroup =>
          Left(Fault.Unsupported(s"OpGroupNonUniformBallot execution scope $value is not Subgroup"))
        case (Right(_), Some(_)) =>
          Left(Fault.InvalidRequest(
            "OpGroupNonUniformBallot requires a four-lane 32-bit integer result and scalar boolean predicate"
          ))
        case (Left(error), _) => Left(error)
        case (_, None)        => Left(undefined(predicate))
      }
    }
  }

  /** A ballot of the active lanes of the subgroup whose value equals the lane's own. */
  private def partition(site: Site, resultType: Int, dest: Int, source: Int): Op =
    if (!site.program.integerShape(resultType).contains(32 -> 4))
      Fail(Fault.InvalidRequest("OpGroupNonUniformPartitionEXT requires a four-lane 32-bit integer result"))
    else
      Dynamic { (execution, _) =>
        execution.values.get(source) match {
          case Some(value @ VMValue.Bits(_, _)) =>
            suspend(
              execution,
              CollectiveRequest(
                CollectiveKind.Partition,
                _,
                _,
                Scope.Subgroup,
                CollectivePayload.Value(resultType, dest, value),
                nonUniform = true
              )
            )
          case Some(_) => Left(Fault.InvalidRequest("OpGroupNonUniformPartitionEXT partitions numeric values"))
          case None    => Left(undefined(source))
        }
      }

  private def subgroupArithmetic(
      site: Site,
      kind: CollectiveKind,
      resultType: Operand,
      dest: Operand,
      scope: Operand,
      operation: Operand,
      source: Operand,
      cluster: Option[Operand]
  ): Op = {
    val logical = kind == CollectiveKind.LogicalAnd || kind == CollectiveKind.LogicalOr ||
      kind == CollectiveKind.LogicalXor
    if (!clusterMatches(operation, cluster))
      Fail(Fault.InvalidRequest("a cluster size accompanies exactly the ClusteredReduce group operation"))
    else if (
      if (logical) site.program.booleanLanes(id(resultType)).isEmpty
      else site.program.integerShape(id(resultType)).isEmpty
    )
      Fail(Fault.InvalidRequest("subgroup arithmetic operand type is incompatible with its operation"))
    else subgroupValue(
      site,
      kind,
      id(resultType),
      id(dest),
      id(scope),
      id(source),
      unsigned(operation).toInt,
      cluster = cluster
    )
  }

  private def clusterMatches(operation: Operand, cluster: Option[Operand]): Boolean =
    (unsigned(operation) == ClusteredReduce) == cluster.nonEmpty && unsigned(operation) <= ClusteredReduce

  /** The cluster size of a clustered reduction: a power of two. */
  private def clusterSize(values: InvocationValues, cluster: Option[Operand]): Either[Fault, Vec[Long]] =
    cluster.fold[Either[Fault, Vec[Long]]](Right(Vec.empty))(operand =>
      scalar(values, id(operand)).flatMap(size =>
        Either.cond(
          size > 0L && (size & (size - 1L)) == 0L,
          Vec(size),
          Fault.InvalidRequest(s"cluster size $size is not a power of two")
        )
      )
    )

  private def subgroupFloating(
      site: Site,
      instruction: Instruction,
      kind: CollectiveKind,
      resultType: Operand,
      dest: Operand,
      scope: Operand,
      operation: Operand,
      source: Operand,
      cluster: Option[Operand]
  ): Op =
    if (!clusterMatches(operation, cluster))
      Fail(Fault.InvalidRequest("a cluster size accompanies exactly the ClusteredReduce group operation"))
    else
      nonUniform(
        group(site, instruction.opcode, kind, Shape.Floating, resultType, dest, scope, operation, source, cluster),
        "nonuniform floating operations require Subgroup scope"
      )

  /** Ballot queries read a uint4 ballot within the invocation; `mode` -1 tests one bit, 0 to 2 count, 3 and 4 search.
    */
  /** The bits of ballot word `word`, whose low 32 bits hold lanes `32 * word` up, for lanes `from` until `until`. */
  def ballotLanes(from: Int, until: Int, word: Int): Long = {
    def below(lanes: Int): Long = (1L << math.min(math.max(lanes - word * 32, 0), 32)) - 1L
    below(until) & ~below(from)
  }

  private def ballotQuery(
      site: Site,
      resultType: Int,
      dest: Int,
      scope: Int,
      source: Int,
      mode: Int,
      index: Option[Int]
  ): Op = {
    val program = site.program
    val valid   = program.integerShape(program.valueType(source)).contains(32 -> 4) &&
      (if (mode == -1) program.booleanLanes(resultType).contains(1)
       else program.integerShape(resultType).contains(32 -> 1))
    step { (execution, _) =>
      val values     = execution.values
      val invocation = execution.invocation
      val bit        = index.fold[Either[Fault, Long]](Right(invocation.subgroupLocal.toLong))(scalar(values, _))
      (scalar(values, scope), values.get(source), bit) match {
        case (Right(Scope.Subgroup), Some(VMValue.Bits(_, words)), Right(local)) if valid && words.length == 4 =>
          def count(until: Int): Long =
            words.indices.map(word => java.lang.Long.bitCount(words(word) & ballotLanes(0, until, word))).sum.toLong
          val active = words.indices.map(word => words(word) & ballotLanes(0, invocation.maxSubgroupSize, word))
          val result = mode match {
            case -1 =>
              if (local >= 0 && local < 128 && (words(local.toInt / 32) >>> (local % 32) & 1L) != 0L) 1L else 0L
            case 0 => count(invocation.maxSubgroupSize)
            case 1 => count(invocation.subgroupLocal + 1)
            case 2 => count(invocation.subgroupLocal)
            case 3 =>
              active.indices.find(active(_) != 0L)
                .fold(0xffffffffL)(word => word * 32L + java.lang.Long.numberOfTrailingZeros(active(word)))
            case 4 =>
              active.indices.findLast(active(_) != 0L)
                .fold(0xffffffffL)(word => word * 32L + 63 - java.lang.Long.numberOfLeadingZeros(active(word)))
            case _ => -1L
          }
          if (result < 0) Left(Fault.InvalidRequest("invalid subgroup ballot operation"))
          else Right(values.updateScalar(dest, resultType, result))
        case (Left(error), _, _) => Left(error)
        case (_, _, Left(error)) => Left(error)
        case (_, None, _)        => Left(undefined(source))
        case _                   =>
          Left(Fault.InvalidRequest("subgroup ballot requires Subgroup scope, uint4 value and compatible result"))
      }
    }
  }

  /** Derivatives pause the lane until its quad's neighbours reach the same site. */
  private def derivative(site: Site, resultType: Int, dest: Int, operand: Int, derivative: Derivative): Op =
    site.program.floatingShape(resultType) match {
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
                    resultType,
                    dest,
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
      execution.pc = request.pc + 1
      execution.resume()
    }

  private def floatingIdentity(width: Int in (16, 32, 64), kind: CollectiveKind): Long = kind match {
    case CollectiveKind.FloatingMultiply => IEEE754.fromDouble(1.0, width)
    case CollectiveKind.FloatingMin      => IEEE754.fromDouble(Double.PositiveInfinity, width)
    case CollectiveKind.FloatingMax      => IEEE754.fromDouble(Double.NegativeInfinity, width)
    case _                               => 0L
  }

  private val ClusteredReduce = 3

  /** Completes a rendezvous; a clustered reduction reduces each aligned cluster of subgroup lanes on its own. */
  private def combine(
      program: VMProgram,
      participants: Vec[InvocationState],
      expected: CollectiveRequest,
      memory: MemoryState
  ): Either[Fault, Unit] =
    (if (expected.operation != ClusteredReduce) outcomes(program, participants, expected, memory)
     else {
       val size     = expected.selector.headOption.getOrElse(1L)
       val clusters =
         participants.indices.groupBy(participants(_).invocation.subgroupLocal.toLong / size).values.toVector
       clusters.foldLeft[Either[Fault, Vector[(Int, Option[VMValue])]]](Right(Vector.empty)) { (result, members) =>
         for {
           merged  <- result
           reduced <- outcomes(program, Vec.from(members.map(participants(_))), expected.copy(operation = 0), memory)
         } yield merged ++ members.zip(reduced)
       }.map(pairs => Vec.from(pairs.sortBy(_._1).map(_._2)))
     }).map(complete(participants, _))

  // Per-lane reduction and scan loops write directly into the collective result buffers.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
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
    def scan(
        inputs: Vec[Array[Long]],
        initial: Long,
        resultType: Int
    )(op: (Long, Long) => Long): Vec[Option[VMValue]] = {
      val accumulated = Array.fill(inputs.head.length)(initial)
      val scanned     = Vec.newBuilder[Option[VMValue]]
      inputs.foreach { input =>
        val before = accumulated.clone()
        var lane   = 0
        while (lane < accumulated.length) {
          accumulated(lane) = op(accumulated(lane), input(lane))
          lane += 1
        }
        scanned += Some(VMValue.Bits(resultType, if (expected.operation == 1) accumulated.clone() else before))
      }
      scanned.result()
    }
    def reduceOrScan(
        inputs: Vec[Array[Long]],
        identity: Long,
        resultType: Int
    )(op: (Long, Long) => Long): Vec[Option[VMValue]] =
      if (expected.operation == 0) {
        val reduced =
          Array.tabulate(inputs.head.length)(lane => inputs.foldLeft(identity)((acc, input) => op(acc, input(lane))))
        Vec.fill(participants.size)(Some(VMValue.Bits(resultType, reduced)))
      } else scan(inputs, identity, resultType)(op)
    def lanesOf(kind: String): Either[Fault, Vec[Array[Long]]] = {
      val inputs = requests.map(payloadValue(_)).collect { case VMValue.Bits(_, lanes) => lanes }
      Either.cond(
        inputs.size == requests.size && inputs.nonEmpty,
        inputs,
        Fault.InvalidRequest(s"$kind participants do not all provide matching values")
      )
    }
    // A shift selects the source lane in a subgroup-sized window; an out-of-window source reads the neighbouring operand.
    def shift(resultType: Int, direction: ShuffleDirection): Either[Fault, Vec[Option[VMValue]]] = {
      def shifted(execution: InvocationState, request: CollectiveRequest): Either[Fault, Option[VMValue]] = {
        val size     = execution.invocation.subgroupSize.toLong
        val distance = request.payload match {
          case CollectivePayload.ShuffleUp(delta = delta)   => -delta
          case CollectivePayload.ShuffleDown(delta = delta) => delta
          case _                                            => 0L
        }
        val unwrapped = execution.invocation.subgroupLocal.toLong + distance
        val outside   = unwrapped < 0L || unwrapped >= size
        val source    = if (unwrapped < 0L) unwrapped + size else if (unwrapped >= size) unwrapped - size else unwrapped
        participants.find(_.invocation.subgroupLocal.toLong == source) match {
          case Some(sourceExecution) => sourceExecution.suspended.map(_.payload) match {
              case Some(CollectivePayload.ShuffleUp(previous = previous, current = current)) =>
                Right(Some(retag(if (outside) previous else current, resultType)))
              case Some(CollectivePayload.ShuffleDown(current = current, next = next)) =>
                Right(Some(retag(if (outside) next else current, resultType)))
              case _ =>
                Left(Fault.InvalidRequest(s"subgroup shuffle-${direction.label} source is not suspended"))
            }
          case None =>
            Left(Fault.InvalidRequest(s"subgroup shuffle-${direction.label} source lane $source is absent"))
        }
      }
      Traversal.traverse(participants)(execution =>
        execution.suspended.toRight(Fault.InvalidRequest("collective participant is not suspended"))
          .flatMap(shifted(execution, _))
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
      case CollectivePayload.Shuffle(resultType = resultType) =>
        Traversal.traverse(participants)(execution =>
          execution.suspended.map(_.payload) match {
            case Some(CollectivePayload.Shuffle(_, _, data, VMValue.Bits(_, Array(sourceLocal)))) =>
              participants.find(_.invocation.subgroupLocal.toLong == sourceLocal) match {
                case Some(sourceExecution) => sourceExecution.suspended.map(_.payload) match {
                    case Some(CollectivePayload.Shuffle(data = sourceData)) =>
                      Right(Some(retag(sourceData, resultType)))
                    case _ => Left(Fault.InvalidRequest("subgroup shuffle source is not suspended"))
                  }
                // SYCL selection and permutation permit an out-of-range source with an unspecified result; the lane
                // keeps its own input.
                case None => Right(Some(retag(data, resultType)))
              }
            case _ => Left(Fault.InvalidRequest("subgroup shuffle invocation id must be scalar"))
          }
        )
      case CollectivePayload.ShuffleUp(resultType = resultType)   => shift(resultType, ShuffleDirection.Up)
      case CollectivePayload.ShuffleDown(resultType = resultType) => shift(resultType, ShuffleDirection.Down)
      case CollectivePayload.Value(resultType, _, _)              =>
        val values = requests.map(payloadValue(_))
        expected.kind match {
          case CollectiveKind.Elect =>
            Right(Vec.from(participants.indices.map(index =>
              Some(VMValue.Bits(resultType, Array(if (index == 0) 1L else 0L)))
            )))
          case CollectiveKind.BroadcastFirst => Right(Vec.fill(participants.size)(Some(values.head)))
          case CollectiveKind.AllEqual       =>
            val inputs = values.collect { case VMValue.Bits(typeId, lanes) => (typeId, lanes) }
            val equal  = inputs.size == values.size && inputs.tail.forall { case (typeId, lanes) =>
              val first = inputs.head._2
              lanes.length == first.length && lanes.indices.forall { lane =>
                program.floatingShape(typeId) match {
                  case Some((width, _)) => IEEE754.toDouble(lanes(lane), width) == IEEE754.toDouble(first(lane), width)
                  case None             => lanes(lane) == first(lane)
                }
              }
            }
            Right(Vec.fill(participants.size)(Some(VMValue.Bits(resultType, Array(if (equal) 1L else 0L)))))
          case CollectiveKind.Any | CollectiveKind.All =>
            val flags  = values.map(VMValue.isTrue)
            val result = if (expected.kind == CollectiveKind.Any) flags.exists(identity) else flags.forall(identity)
            Right(Vec.fill(participants.size)(Some(VMValue.Bits(resultType, Array(if (result) 1L else 0L)))))
          case CollectiveKind.UnsignedMin | CollectiveKind.UnsignedMax | CollectiveKind.SignedMin | CollectiveKind.SignedMax =>
            lanesOf(expected.kind.toString).map { inputs =>
              val width: Int in 1 ~ 64 = program.integerShape(resultType).map(_._1).getOrElse(64)
              val signed  = expected.kind == CollectiveKind.SignedMin || expected.kind == CollectiveKind.SignedMax
              val maximum = expected.kind == CollectiveKind.UnsignedMax || expected.kind == CollectiveKind.SignedMax
              val extremum: (Long, Long) => Long = expected.kind match {
                case CollectiveKind.SignedMin =>
                  (l, r) => Execute.truncate(IntegerSemantics.minimum(Execute, l, r, width, signed = true), width)
                case CollectiveKind.SignedMax =>
                  (l, r) => Execute.truncate(IntegerSemantics.maximum(Execute, l, r, width, signed = true), width)
                case CollectiveKind.UnsignedMin =>
                  (l, r) => Execute.truncate(IntegerSemantics.minimum(Execute, l, r, width, signed = false), width)
                case _ =>
                  (l, r) => Execute.truncate(IntegerSemantics.maximum(Execute, l, r, width, signed = false), width)
              }
              if (expected.operation == 0) {
                val lanes =
                  Array.tabulate(inputs.head.length)(lane =>
                    inputs.view.map(_(lane) & mask(width)).reduce(extremum)
                  )
                Vec.fill(participants.size)(Some(VMValue.Bits(resultType, lanes)))
              } else {
                val identity =
                  if (signed) { if (maximum) 1L << (width - 1) else (1L << (width - 1)) - 1L }
                  else if (maximum) 0L
                  else mask(width)
                scan(inputs, identity & mask(width), resultType)(extremum)
              }
            }
          case CollectiveKind.IntegerAdd | CollectiveKind.IntegerMultiply =>
            lanesOf(if (expected.kind == CollectiveKind.IntegerAdd) "OpGroupIAdd" else "OpGroupIMulKHR").map { inputs =>
              val width: Int in 1 ~ 64 = program.integerShape(resultType).map(_._1).getOrElse(64)
              val add                  = expected.kind == CollectiveKind.IntegerAdd
              reduceOrScan(inputs, if (add) 0L else 1L, resultType)((left, right) =>
                (if (add) left + right else left * right) & mask(width)
              )
            }
          case CollectiveKind.BitwiseAnd | CollectiveKind.BitwiseOr | CollectiveKind.BitwiseXor | CollectiveKind.LogicalAnd |
              CollectiveKind.LogicalOr | CollectiveKind.LogicalXor =>
            lanesOf(expected.kind.toString).map { inputs =>
              val logical    = program.booleanLanes(resultType).nonEmpty
              val resultMask = if (logical) 1L else program.integerShape(resultType).fold(-1L)(shape => mask(shape._1))
              val identity   = expected.kind match {
                case CollectiveKind.BitwiseAnd | CollectiveKind.LogicalAnd => resultMask
                case _                                                     => 0L
              }
              reduceOrScan(inputs, identity, resultType) { (left, right) =>
                val normalised = if (logical) { if (right != 0L) 1L else 0L }
                else right
                (expected.kind match {
                  case CollectiveKind.BitwiseAnd | CollectiveKind.LogicalAnd => left & normalised
                  case CollectiveKind.BitwiseOr | CollectiveKind.LogicalOr   => left | normalised
                  case _                                                     => left ^ normalised
                }) & resultMask
              }
            }
          case CollectiveKind.FloatingAdd | CollectiveKind.FloatingMultiply | CollectiveKind.FloatingMin | CollectiveKind.FloatingMax =>
            lanesOf(expected.kind.toString).map { inputs =>
              val width: Int in (16, 32, 64) = program.floatingShape(resultType).map(_._1).getOrElse(64)
              val op: (Long, Long) => Long   = expected.kind match {
                case CollectiveKind.FloatingAdd      => IEEE754.add(_, _, width, IEEE754.NaNOperand.Left)
                case CollectiveKind.FloatingMultiply => IEEE754.multiply(_, _, width, IEEE754.NaNOperand.Left)
                case CollectiveKind.FloatingMin      => IEEE754.minNum(_, _, width)
                case _                               => IEEE754.maxNum(_, _, width)
              }
              val identity = floatingIdentity(width, expected.kind)
              if (
                expected.operation == 0 &&
                (expected.kind == CollectiveKind.FloatingMin || expected.kind == CollectiveKind.FloatingMax)
              ) {
                val reduced = Array.tabulate(inputs.head.length)(lane => inputs.view.map(_(lane)).reduce(op))
                Vec.fill(participants.size)(Some(VMValue.Bits(resultType, reduced)))
              } else reduceOrScan(inputs, identity, resultType)(op)
            }
          case CollectiveKind.Broadcast =>
            val selected = (expected.scope, expected.selector) match {
              case (Scope.Subgroup, Vec(x)) => participants.indexWhere(_.invocation.subgroupLocal.toLong == x)
              case (_, Vec(x))              => participants.indexWhere(_.invocation.local(0) == x)
              case (_, Vec(x, y))           =>
                participants.indexWhere(execution =>
                  execution.invocation.local(0) == x && execution.invocation.local(1) == y &&
                    execution.invocation.local(2) == 0
                )
              case (_, Vec(x, y, z)) =>
                participants.indexWhere(execution =>
                  execution.invocation.local(0) == x && execution.invocation.local(1) == y &&
                    execution.invocation.local(2) == z
                )
              case _ => -1
            }
            if (selected < 0)
              Left(Fault.InvalidRequest(
                s"OpGroupBroadcast local id ${expected.selector.mkString("(", ",", ")")} is outside the workgroup"
              ))
            else Right(Vec.fill(participants.size)(Some(values(selected))))
          case CollectiveKind.Ballot =>
            val ballot = Array.fill(4)(0L)
            val valid  = participants.zip(values).forall {
              case (_, VMValue.Bits(_, Array(_))) => true
              case _                              => false
            }
            if (valid) participants.zip(values).foreach {
              case (execution, VMValue.Bits(_, Array(predicate))) =>
                val local = execution.invocation.subgroupLocal
                if (predicate != 0L) ballot(local / 32) |= 1L << (local % 32)
              case _ => ()
            }
            if (!valid)
              Left(
                Fault.InvalidRequest(
                  "OpGroupNonUniformBallot participants do not all provide scalar boolean predicates"
                )
              )
            else Right(Vec.fill(participants.size)(Some(VMValue.Bits(resultType, ballot))))
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
          case CollectiveKind.Partition =>
            lanesOf("OpGroupNonUniformPartitionEXT").map { inputs =>
              val typeId = values.head match {
                case VMValue.Bits(typeId, _)    => typeId
                case VMValue.Address(typeId, _) => typeId
              }
              val width = program.floatingShape(typeId).map(_._1)
              // lanes partition by bits, except that floating-point zeros of either sign compare equal
              val keys = inputs.map(_.toSeq.map(bits =>
                if (width.exists(IEEE754.toDouble(bits, _) == 0.0)) 0L else bits
              ))
              val ballots = keys.indices.groupBy(keys(_)).view.mapValues { members =>
                val ballot = Array.fill(4)(0L)
                members.foreach { member =>
                  val local = participants(member).invocation.subgroupLocal
                  ballot(local / 32) |= 1L << (local % 32)
                }
                VMValue.Bits(resultType, ballot)
              }.toMap
              keys.map(key => Some(ballots(key)))
            }
          case kind => Left(Fault.InvalidRequest(s"$kind rendezvous unexpectedly carried a value"))
        }
    }
    results
  }
}
