package net.kurobako.gemstone.isa.spirv.v1_6

import VMInvocation.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.*

private object VMCollectives {
  def openCLBroadcastScope(name: String): Option[Int] =
    if (name.startsWith("_Z19sub_group_broadcast")) Some(3)
    else if (name.startsWith("_Z20work_group_broadcast")) Some(2)
    else None

  enum Kind {
    case Any
    case All
    case UnsignedMin
    case UnsignedMax
    case SignedMin
    case SignedMax
    case IntegerAdd
    case IntegerMultiply
    case BitwiseAnd
    case BitwiseOr
    case BitwiseXor
    case LogicalAnd
    case LogicalOr
    case LogicalXor
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
    case Value(resultType: Int, destination: Int, value: RuntimeValue)
    case Shuffle(resultType: Int, destination: Int, data: RuntimeValue, invocationId: RuntimeValue)
    case ShuffleUp(
        resultType: Int,
        destination: Int,
        previous: RuntimeValue,
        current: RuntimeValue,
        delta: Long
    )
    case ShuffleDown(
        resultType: Int,
        destination: Int,
        current: RuntimeValue,
        next: RuntimeValue,
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
      selector: Vec[Long] = Vec.empty
  )
}
