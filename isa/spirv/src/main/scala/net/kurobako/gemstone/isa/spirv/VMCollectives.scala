package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.*

private object VMCollectives {
  def isNonUniform(instruction: Instruction): Boolean = instruction match {
    case _: Instruction.GroupNonUniformElect |
        _: Instruction.GroupNonUniformAll |
        _: Instruction.GroupNonUniformAny |
        _: Instruction.GroupNonUniformAllEqual |
        _: Instruction.GroupNonUniformBroadcast |
        _: Instruction.GroupNonUniformBroadcastFirst |
        _: Instruction.GroupNonUniformBallot |
        _: Instruction.GroupNonUniformInverseBallot |
        _: Instruction.GroupNonUniformBallotBitExtract |
        _: Instruction.GroupNonUniformBallotBitCount |
        _: Instruction.GroupNonUniformBallotFindLSB |
        _: Instruction.GroupNonUniformBallotFindMSB |
        _: Instruction.GroupNonUniformShuffle |
        _: Instruction.GroupNonUniformShuffleXor |
        _: Instruction.GroupNonUniformShuffleUp |
        _: Instruction.GroupNonUniformShuffleDown |
        _: Instruction.GroupNonUniformIAdd |
        _: Instruction.GroupNonUniformIMul |
        _: Instruction.GroupNonUniformSMin |
        _: Instruction.GroupNonUniformUMin |
        _: Instruction.GroupNonUniformSMax |
        _: Instruction.GroupNonUniformUMax |
        _: Instruction.GroupNonUniformFAdd |
        _: Instruction.GroupNonUniformFMul |
        _: Instruction.GroupNonUniformFMin |
        _: Instruction.GroupNonUniformFMax |
        _: Instruction.GroupNonUniformBitwiseAnd |
        _: Instruction.GroupNonUniformBitwiseOr |
        _: Instruction.GroupNonUniformBitwiseXor |
        _: Instruction.GroupNonUniformLogicalAnd |
        _: Instruction.GroupNonUniformLogicalOr |
        _: Instruction.GroupNonUniformLogicalXor => true
    case _ => false
  }

  enum Kind {
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
    case Barrier
  }

  enum Payload {
    case Value(resultType: Int, destination: Int, value: VMValue)
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
