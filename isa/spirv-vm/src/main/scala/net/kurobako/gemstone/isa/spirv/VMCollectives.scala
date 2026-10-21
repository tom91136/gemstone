package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.*

private object VMCollectives {
  def isNonUniform(instruction: Instruction): Boolean = instruction match {
    case Instruction.GroupNonUniformElect(_, _, _) |
        Instruction.GroupNonUniformAll(_, _, _, _) |
        Instruction.GroupNonUniformAny(_, _, _, _) |
        Instruction.GroupNonUniformAllEqual(_, _, _, _) |
        Instruction.GroupNonUniformBroadcast(_, _, _, _, _) |
        Instruction.GroupNonUniformBroadcastFirst(_, _, _, _) |
        Instruction.GroupNonUniformBallot(_, _, _, _) |
        Instruction.GroupNonUniformInverseBallot(_, _, _, _) |
        Instruction.GroupNonUniformBallotBitExtract(_, _, _, _, _) |
        Instruction.GroupNonUniformBallotBitCount(_, _, _, _, _) |
        Instruction.GroupNonUniformBallotFindLSB(_, _, _, _) |
        Instruction.GroupNonUniformBallotFindMSB(_, _, _, _) |
        Instruction.GroupNonUniformShuffle(_, _, _, _, _) |
        Instruction.GroupNonUniformShuffleXor(_, _, _, _, _) |
        Instruction.GroupNonUniformShuffleUp(_, _, _, _, _) |
        Instruction.GroupNonUniformShuffleDown(_, _, _, _, _) |
        Instruction.GroupNonUniformRotateKHR(_, _, _, _, _, _) |
        Instruction.GroupNonUniformIAdd(_, _, _, _, _, _) |
        Instruction.GroupNonUniformIMul(_, _, _, _, _, _) |
        Instruction.GroupNonUniformSMin(_, _, _, _, _, _) |
        Instruction.GroupNonUniformUMin(_, _, _, _, _, _) |
        Instruction.GroupNonUniformSMax(_, _, _, _, _, _) |
        Instruction.GroupNonUniformUMax(_, _, _, _, _, _) |
        Instruction.GroupNonUniformFAdd(_, _, _, _, _, _) |
        Instruction.GroupNonUniformFMul(_, _, _, _, _, _) |
        Instruction.GroupNonUniformFMin(_, _, _, _, _, _) |
        Instruction.GroupNonUniformFMax(_, _, _, _, _, _) |
        Instruction.GroupNonUniformBitwiseAnd(_, _, _, _, _, _) |
        Instruction.GroupNonUniformBitwiseOr(_, _, _, _, _, _) |
        Instruction.GroupNonUniformBitwiseXor(_, _, _, _, _, _) |
        Instruction.GroupNonUniformLogicalAnd(_, _, _, _, _, _) |
        Instruction.GroupNonUniformLogicalOr(_, _, _, _, _, _) |
        Instruction.GroupNonUniformLogicalXor(_, _, _, _, _, _) |
        Instruction.GroupNonUniformQuadBroadcast(_, _, _, _, _) |
        Instruction.GroupNonUniformQuadSwap(_, _, _, _, _) |
        Instruction.SubgroupBallotKHR(_, _, _) |
        Instruction.SubgroupFirstInvocationKHR(_, _, _) |
        Instruction.SubgroupAllKHR(_, _, _) |
        Instruction.SubgroupAnyKHR(_, _, _) |
        Instruction.SubgroupAllEqualKHR(_, _, _) |
        Instruction.SubgroupReadInvocationKHR(_, _, _, _) |
        Instruction.GroupNonUniformPartitionEXT(_, _, _) |
        Instruction.GroupIAddNonUniformAMD(_, _, _, _, _) |
        Instruction.GroupFAddNonUniformAMD(_, _, _, _, _) |
        Instruction.GroupFMinNonUniformAMD(_, _, _, _, _) |
        Instruction.GroupUMinNonUniformAMD(_, _, _, _, _) |
        Instruction.GroupSMinNonUniformAMD(_, _, _, _, _) |
        Instruction.GroupFMaxNonUniformAMD(_, _, _, _, _) |
        Instruction.GroupUMaxNonUniformAMD(_, _, _, _, _) |
        Instruction.GroupSMaxNonUniformAMD(_, _, _, _, _) |
        Instruction.CompositeConstructCoopMatQCOM(_, _, _) => true
    case _ => false
  }

  enum Kind derives CanEqual {
    case Elect
    case AllEqual
    case BroadcastFirst
    case BitwiseAnd
    case BitwiseOr
    case BitwiseXor
    case LogicalAnd
    case LogicalOr
    case LogicalXor
    case Any
    case All
    case UnsignedMin
    case UnsignedMax
    case SignedMin
    case SignedMax
    case IntegerAdd
    case IntegerMultiply
    case FloatingAdd
    case FloatingMin
    case FloatingMax
    case FloatingMultiply
    case Shuffle
    case ShuffleUp
    case ShuffleDown
    case Broadcast
    case Ballot
    case Partition
    case MatrixMultiply
    case MatrixRows
    case Barrier
    case PipeReservation
  }

  enum Payload {
    case Value(resultType: Int, destination: Int, operand: VMValue)
    case Shuffle(resultType: Int, destination: Int, data: VMValue, invocationId: VMValue)
    case ShuffleUp(
        resultType: Int,
        destination: Int,
        previous: VMValue,
        current: VMValue,
        delta: Long
    )
    case ShuffleDown(
        resultType: Int,
        destination: Int,
        current: VMValue,
        next: VMValue,
        delta: Long
    )
    case Barrier(memoryScope: Int, semantics: Long)
    case PipeReservation(resultType: Int, destination: Int, pipe: VMMemory.Pointer, packets: Long, end: PipeEnd)
  }

  case class Request(
      kind: Kind,
      function: Int,
      pc: Int,
      scope: Int,
      payload: Payload,
      operation: Int = 0,
      selector: Vec[Long] = Vec.empty,
      nonUniform: Boolean = false
  )
}
