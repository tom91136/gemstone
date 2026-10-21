package net.kurobako.gemstone.isa.nvidia

import VMExecution.*
import VMIR.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*

/** Lane loops that feed each active lane's operands to a staged kernel and store its result; lanes that read each
  * other's values stage their writes in the context's `effects`. Each expansion is a method of its own, which keeps the
  * dispatch that selects it below the JIT's size limits. A value class, so running an instruction allocates no sinks.
  */
// lane kernels take their operands unpacked so a loop allocates no operand holders
@SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.null", "baumkuchen:data-clump"))
private class LaneLoop(val context: VMContext) extends AnyVal with NvidiaSinks[Long, Either[Fault, Unit]] {
  inline def effects: VMWriteBuffer = context.effects

  import LaneFaults.*

  inline def unary(destination: Register, source: Value)(inline kernel: Long => Long): Either[Fault, Unit] = {
    def lanes(context: VMContext, destination: Register, source: Value): Unit = context.active.foreachLane(lane =>
      context.lane(lane).setRegister(destination, kernel(context.read(lane, source)))
    )
    lanes(context, destination, source)
    Completed
  }

  inline def binary(destination: Register, left: Value, right: Value)(inline kernel: (Long, Long) => Long)
      : Either[Fault, Unit] = {
    def lanes(context: VMContext, destination: Register, left: Value, right: Value): Unit =
      context.active.foreachLane(lane =>
        context.lane(lane).setRegister(destination, kernel(context.read(lane, left), context.read(lane, right)))
      )
    lanes(context, destination, left, right)
    Completed
  }

  inline def ternary(destination: Register, first: Value, second: Value, third: Value)(
      inline kernel: (Long, Long, Long) => Long
  ): Either[Fault, Unit] = {
    def lanes(context: VMContext, destination: Register, first: Value, second: Value, third: Value): Unit =
      context.active.foreachLane(lane =>
        context.lane(lane).setRegister(
          destination,
          kernel(context.read(lane, first), context.read(lane, second), context.read(lane, third))
        )
      )
    lanes(context, destination, first, second, third)
    Completed
  }

  inline def quaternary(destination: Register, first: Value, second: Value, third: Value, fourth: Value)(
      inline kernel: (Long, Long, Long, Long) => Long
  ): Either[Fault, Unit] = {
    def lanes(
        context: VMContext,
        destination: Register,
        first: Value,
        second: Value,
        third: Value,
        fourth: Value
    ): Unit = context.active.foreachLane(lane =>
      context.lane(lane).setRegister(
        destination,
        kernel(
          context.read(lane, first),
          context.read(lane, second),
          context.read(lane, third),
          context.read(lane, fourth)
        )
      )
    )
    lanes(context, destination, first, second, third, fourth)
    Completed
  }

  inline def carryIn(destination: Register, left: Value, right: Value)(inline result: (Long, Long, Long) => Long)
      : Either[Fault, Unit] = {
    def lanes(context: VMContext, destination: Register, left: Value, right: Value): Unit =
      context.active.foreachLane { lane =>
        val storage = context.lane(lane)
        storage.setRegister(
          destination,
          result(context.read(lane, left), context.read(lane, right), if (storage.carry) 1L else 0L)
        )
      }
    lanes(context, destination, left, right)
    Completed
  }

  inline def carryIn(destination: Register, first: Value, second: Value, third: Value)(
      inline result: (Long, Long, Long, Long) => Long
  ): Either[Fault, Unit] = {
    def lanes(context: VMContext, destination: Register, first: Value, second: Value, third: Value): Unit =
      context.active.foreachLane { lane =>
        val storage = context.lane(lane)
        storage.setRegister(
          destination,
          result(
            context.read(lane, first),
            context.read(lane, second),
            context.read(lane, third),
            if (storage.carry) 1L else 0L
          )
        )
      }
    lanes(context, destination, first, second, third)
    Completed
  }

  inline def carryInOut(destination: Register, left: Value, right: Value)(
      inline result: (Long, Long, Long) => Long,
      inline carry: (Long, Long, Long, Long) => Long
  ): Either[Fault, Unit] = {
    def lanes(context: VMContext, destination: Register, left: Value, right: Value): Unit =
      context.active.foreachLane { lane =>
        val storage  = context.lane(lane)
        val a        = context.read(lane, left)
        val b        = context.read(lane, right)
        val incoming = if (storage.carry) 1L else 0L
        val value    = result(a, b, incoming)
        storage.setRegister(destination, value)
        storage.carry = carry(a, b, incoming, value) != 0L
      }
    lanes(context, destination, left, right)
    Completed
  }

  inline def carryInOut(destination: Register, first: Value, second: Value, third: Value)(
      inline result: (Long, Long, Long, Long) => Long,
      inline carry: (Long, Long, Long, Long, Long) => Long
  ): Either[Fault, Unit] = {
    def lanes(context: VMContext, destination: Register, first: Value, second: Value, third: Value): Unit =
      context.active.foreachLane { lane =>
        val storage  = context.lane(lane)
        val a        = context.read(lane, first)
        val b        = context.read(lane, second)
        val c        = context.read(lane, third)
        val incoming = if (storage.carry) 1L else 0L
        val value    = result(a, b, c, incoming)
        storage.setRegister(destination, value)
        storage.carry = carry(a, b, c, incoming, value) != 0L
      }
    lanes(context, destination, first, second, third)
    Completed
  }

  inline def produceCarries(destination: Register, first: Value, second: Value, third: Value)(
      inline result: (Long, Long, Long) => Long,
      inline carries: (Long, Long, Long) => Long
  ): Either[Fault, Unit] = {
    def lanes(context: VMContext, destination: Register, first: Value, second: Value, third: Value): Unit =
      context.active.foreachLane { lane =>
        val storage = context.lane(lane)
        val a       = context.read(lane, first)
        val b       = context.read(lane, second)
        val c       = context.read(lane, third)
        storage.setRegister(destination, result(a, b, c))
        storage.conditionCodes.setThreeInputCarry(carries(a, b, c).toInt)
      }
    lanes(context, destination, first, second, third)
    Completed
  }

  inline def consumeCarries(destination: Register, first: Value, second: Value, third: Value)(
      inline result: (Long, Long, Long, Long) => Long
  ): Either[Fault, Unit] = {
    def lanes(context: VMContext, destination: Register, first: Value, second: Value, third: Value)
        : Either[Fault, Unit] = everyLane(context) { lane =>
      val storage = context.lane(lane)
      val codes   = storage.conditionCodes
      val carry   = codes.threeInputCarryCount
      if (carry < 0) Left(undefinedCarry(context, lane))
      else {
        storage.setRegister(
          destination,
          result(context.read(lane, first), context.read(lane, second), context.read(lane, third), carry.toLong)
        )
        codes.invalidateThreeInputCarry()
        Completed
      }
    }
    lanes(context, destination, first, second, third)
  }

  inline def test(destination: Predicate, source: Value)(inline kernel: Long => Long): Either[Fault, Unit] = {
    def lanes(context: VMContext, destination: Predicate, source: Value): Unit = context.active.foreachLane(lane =>
      context.lane(lane).setPredicate(destination, kernel(context.read(lane, source)) != 0L)
    )
    lanes(context, destination, source)
    Completed
  }

  inline def compare(destination: Predicate, left: Value, right: Value)(inline kernel: (Long, Long) => Long)
      : Either[Fault, Unit] = {
    def lanes(context: VMContext, destination: Predicate, left: Value, right: Value): Unit =
      context.active.foreachLane(lane =>
        context.lane(lane).setPredicate(destination, kernel(context.read(lane, left), context.read(lane, right)) != 0L)
      )
    lanes(context, destination, left, right)
    Completed
  }

  inline def compare(destination: Predicate, left: Value, right: Value, other: Predicate)(
      inline kernel: (Long, Long, Long) => Long
  ): Either[Fault, Unit] = {
    def lanes(context: VMContext, destination: Predicate, left: Value, right: Value, other: Predicate): Unit =
      context.active.foreachLane { lane =>
        val storage = context.lane(lane)
        storage.setPredicate(
          destination,
          kernel(context.read(lane, left), context.read(lane, right), storage.predicateBit(other)) != 0L
        )
      }
    lanes(context, destination, left, right, other)
    Completed
  }

  inline def predicate(destination: Predicate, source: Predicate)(inline kernel: Long => Long): Either[Fault, Unit] = {
    def lanes(context: VMContext, destination: Predicate, source: Predicate): Unit =
      context.active.foreachLane { lane =>
        val storage = context.lane(lane)
        storage.setPredicate(destination, kernel(storage.predicateBit(source)) != 0L)
      }
    lanes(context, destination, source)
    Completed
  }

  inline def predicate(destination: Predicate, left: Predicate, right: Predicate)(
      inline kernel: (Long, Long) => Long
  ): Either[Fault, Unit] = {
    def lanes(context: VMContext, destination: Predicate, left: Predicate, right: Predicate): Unit =
      context.active.foreachLane { lane =>
        val storage = context.lane(lane)
        storage.setPredicate(destination, kernel(storage.predicateBit(left), storage.predicateBit(right)) != 0L)
      }
    lanes(context, destination, left, right)
    Completed
  }

  inline def select(destination: Register, guard: Predicate, whenTrue: Value, whenFalse: Value)(
      inline kernel: (Long, Long, Long) => Long
  ): Either[Fault, Unit] = {
    def lanes(context: VMContext, destination: Register, guard: Predicate, whenTrue: Value, whenFalse: Value): Unit =
      context.active.foreachLane { lane =>
        val storage = context.lane(lane)
        storage.setRegister(
          destination,
          kernel(storage.predicateBit(guard), context.read(lane, whenTrue), context.read(lane, whenFalse))
        )
      }
    lanes(context, destination, guard, whenTrue, whenFalse)
    Completed
  }

  inline def elements(destinations: Vec[Register], sources: Vec[Value])(inline kernel: Long => Long)
      : Either[Fault, Unit] = {
    def lanes(context: VMContext, destinations: Vec[Register], sources: Vec[Value]): Unit = {
      val values = context.values(sources.size)
      context.active.foreachLane { lane =>
        var index = 0
        while (index < sources.size) {
          values(index) = context.read(lane, sources(index))
          index += 1
        }
        val storage = context.lane(lane)
        index = 0
        while (index < destinations.size) {
          storage.setRegister(destinations(index), kernel(values(index)))
          index += 1
        }
      }
    }
    lanes(context, destinations, sources)
    Completed
  }

  inline def split(low: Option[Register], high: Option[Register], source: Value)(
      inline lowHalf: Long => Long,
      inline highHalf: Long => Long
  ): Either[Fault, Unit] = {
    def both(context: VMContext, low: Register, high: Register, source: Value): Unit =
      context.active.foreachLane { lane =>
        val value   = context.read(lane, source)
        val storage = context.lane(lane)
        storage.setRegister(low, lowHalf(value))
        storage.setRegister(high, highHalf(value))
      }
    low match {
      case Some(lowDestination) =>
        high match {
          case Some(highDestination) =>
            both(context, lowDestination, highDestination, source)
            Completed
          case None => unary(lowDestination, source)(lowHalf)
        }
      case None =>
        high match {
          case Some(highDestination) => unary(highDestination, source)(highHalf)
          case None                  => Completed
        }
    }
  }

  inline def broadcast(destination: Register, source: Value)(inline kernel: Long => Long): Either[Fault, Unit] = {
    // only each warp's lowest active lane reads, before any lane of its warp is written
    def lanes(context: VMContext, destination: Register, source: Value): Unit =
      context.active.foreachLane { lane =>
        if ((lane & 31) == java.lang.Long.numberOfTrailingZeros(context.active.warpBits(lane.raw))) {
          val value  = kernel(context.read(lane, source))
          val live   = context.frame.live
          var target = lane & ~31
          val end    = math.min(target + 32, context.workgroup.laneCount)
          while (target < end) {
            if (live.contains(target)) context.lane(WorkgroupLane.fromIndex(target)).setRegister(destination, value)
            target += 1
          }
        }
      }
    lanes(context, destination, source)
    Completed
  }

  inline def writeFlag(flag: VMConditionCodes.Flag, source: Value)(inline kernel: Long => Long): Either[Fault, Unit] = {
    def lanes(context: VMContext, flag: VMConditionCodes.Flag, source: Value): Unit =
      context.active.foreachLane(lane =>
        context.lane(lane).conditionCodes.set(flag, kernel(context.read(lane, source)) != 0L)
      )
    lanes(context, flag, source)
    Completed
  }

  inline def readFlag(destination: Register, flag: VMConditionCodes.Flag)(inline kernel: Long => Long)
      : Either[Fault, Unit] = {
    def lanes(context: VMContext, destination: Register, flag: VMConditionCodes.Flag): Either[Fault, Unit] =
      everyLane(context) { lane =>
        val storage = context.lane(lane)
        val state   = storage.conditionCodes.state(flag)
        if (state < 0) Left(undefinedFlag(context, lane, flag))
        else {
          storage.setRegister(destination, kernel(state.toLong))
          Completed
        }
      }
    lanes(context, destination, flag)
  }

  inline def testFlag(destination: Predicate, flag: VMConditionCodes.Flag)(inline kernel: Long => Long)
      : Either[Fault, Unit] = {
    def lanes(context: VMContext, destination: Predicate, flag: VMConditionCodes.Flag): Either[Fault, Unit] =
      everyLane(context) { lane =>
        val storage = context.lane(lane)
        val state   = storage.conditionCodes.state(flag)
        if (state < 0) Left(undefinedFlag(context, lane, flag))
        else {
          storage.setPredicate(destination, kernel(state.toLong) != 0L)
          Completed
        }
      }
    lanes(context, destination, flag)
  }

  inline def shuffle(
      destination: Register,
      validity: Option[Predicate],
      operand: Value,
      selector: Value,
      bounds: Value
  )(
      inline source: (Long, Long, Long, Long) => Long,
      inline valid: (Long, Long, Long, Long) => Long,
      inline value: Long => Long
  ): Either[Fault, Unit] = {
    def lanes(
        context: VMContext,
        effects: VMWriteBuffer,
        destination: Register,
        validity: Option[Predicate],
        operand: Value,
        selector: Value,
        bounds: Value
    ): Unit = {
      val laneCount = context.workgroup.laneCount.raw
      context.active.foreachLane { lane =>
        val index    = (lane & 31).toLong
        val warpBase = lane & ~31
        val select   = context.read(lane, selector)
        val bound    = context.read(lane, bounds)
        val size     = (laneCount - warpBase).toLong
        val from     = WorkgroupLane.fromIndex(warpBase + source(index, select, bound, size).toInt)
        effects.stageRegister(lane, destination, value(context.read(from, operand)))
      }
      validity.foreach { predicate =>
        context.active.foreachLane { lane =>
          val index  = (lane & 31).toLong
          val select = context.read(lane, selector)
          val bound  = context.read(lane, bounds)
          val size   = (laneCount - (lane & ~31)).toLong
          effects.stage(VMWrite.Predicate(lane, predicate, valid(index, select, bound, size) != 0L))
        }
      }
    }
    lanes(context, effects, destination, validity, operand, selector, bounds)
    Completed
  }

  inline def ballot(destination: Register, voter: Predicate, memberMask: Value)(inline kernel: (Long, Long) => Long)
      : Either[Fault, Unit] = {
    def lanes(
        context: VMContext,
        effects: VMWriteBuffer,
        destination: Register,
        voter: Predicate,
        memberMask: Value
    ): Either[Fault, Unit] = everyLane(context) { lane =>
      val members = context.read(lane, memberMask) & context.active.warpBits(lane.raw)
      val votes   = gather(context, lane, members, voter)
      if (votes < 0L) Left(undefinedVote(context, lane, members, voter))
      else {
        effects.stageRegister(lane, destination, kernel(votes, members))
        Completed
      }
    }
    lanes(context, effects, destination, voter, memberMask)
  }

  inline def vote(destination: Predicate, voter: Predicate, memberMask: Value)(inline kernel: (Long, Long) => Long)
      : Either[Fault, Unit] = {
    def lanes(
        context: VMContext,
        effects: VMWriteBuffer,
        destination: Predicate,
        voter: Predicate,
        memberMask: Value
    ): Either[Fault, Unit] = everyLane(context) { lane =>
      val members = context.read(lane, memberMask) & context.active.warpBits(lane.raw)
      val votes   = gather(context, lane, members, voter)
      if (votes < 0L) Left(undefinedVote(context, lane, members, voter))
      else {
        effects.stage(VMWrite.Predicate(lane, destination, kernel(votes, members) != 0L))
        Completed
      }
    }
    lanes(context, effects, destination, voter, memberMask)
  }

  inline def elect(elected: Option[Register], leader: Predicate, memberMask: Value)(
      inline choose: Long => Long,
      inline isLeader: (Long, Long) => Long
  ): Either[Fault, Unit] = {
    def lanes(
        context: VMContext,
        effects: VMWriteBuffer,
        elected: Option[Register],
        leader: Predicate,
        memberMask: Value
    ): Either[Fault, Unit] = {
      val leaders = everyLane(context) { lane =>
        val mask    = context.read(lane, memberMask)
        val index   = (lane & 31).toLong
        val members = mask & context.active.warpBits(lane.raw)
        if ((mask & (1L << index)) == 0L) Left(absent("elect.sync", lane))
        else if (members == 0L) Left(Fault.Backend("elect.sync has no participating lane"))
        else {
          effects.stage(VMWrite.Predicate(lane, leader, isLeader(index, choose(members)) != 0L))
          Completed
        }
      }
      if (leaders.isRight) elected.foreach { register =>
        context.active.foreachLane { lane =>
          val members = context.read(lane, memberMask) & context.active.warpBits(lane.raw)
          effects.stageRegister(lane, register, choose(members))
        }
      }
      leaders
    }
    lanes(context, effects, elected, leader, memberMask)
  }

  inline def reduceFloat(
      destination: Register,
      source: Value,
      memberMask: Value,
      maximum: Boolean,
      propagateNaN: Boolean
  )(
      inline element: Long => Long,
      inline combine: (Long, Long) => Long
  ): Either[Fault, Unit] = reduce(destination, source, memberMask, IntegerOperation.BitwiseOr, 32)(element, combine)

  inline def matching(
      found: Option[Register],
      predicate: Option[Predicate],
      source: Value,
      memberMask: Value,
      width: Int in 1 ~ 64,
      all: Boolean
  )(inline holds: Long => Long): Either[Fault, Unit] = {
    def lanes(
        context: VMContext,
        effects: VMWriteBuffer,
        found: Option[Register],
        predicate: Option[Predicate],
        source: Value,
        memberMask: Value
    ): Either[Fault, Unit] = everyLane(context) { lane =>
      val mask    = context.read(lane, memberMask)
      val members = mask & context.active.warpBits(lane.raw)
      if ((mask & (1L << (lane & 31))) == 0L) Left(absent("match.sync", lane))
      else {
        val bits      = if (width == 64) -1L else (1L << width) - 1L
        val own       = context.read(lane, source) & bits
        val warpBase  = lane & ~31
        var remaining = members
        var equal     = 0L
        while (remaining != 0L) {
          val index = java.lang.Long.numberOfTrailingZeros(remaining)
          if ((context.read(WorkgroupLane.fromIndex(warpBase + index), source) & bits) == own) equal |= 1L << index
          remaining &= remaining - 1L
        }
        val matched = if (!all) equal else if (equal == members) members else 0L
        found.foreach(effects.stageRegister(lane, _, matched))
        predicate.foreach(slot => effects.stage(VMWrite.Predicate(lane, slot, holds(matched) != 0L)))
        Completed
      }
    }
    lanes(context, effects, found, predicate, source, memberMask)
  }

  inline def reduce(
      destination: Register,
      source: Value,
      memberMask: Value,
      operation: IntegerOperation,
      width: Int in 1 ~ 64
  )(
      inline element: Long => Long,
      inline combine: (Long, Long) => Long
  ): Either[Fault, Unit] = {
    def lanes(
        context: VMContext,
        effects: VMWriteBuffer,
        destination: Register,
        source: Value,
        memberMask: Value
    ): Either[Fault, Unit] = everyLane(context) { lane =>
      val mask    = context.read(lane, memberMask)
      val members = mask & context.active.warpBits(lane.raw)
      if ((mask & (1L << (lane & 31))) == 0L || members == 0L) Left(absent("redux.sync", lane))
      else {
        val warpBase    = lane & ~31
        var remaining   = members & (members - 1L)
        var accumulated = element(context.read(
          WorkgroupLane.fromIndex(warpBase + java.lang.Long.numberOfTrailingZeros(members)),
          source
        ))
        while (remaining != 0L) {
          val other = WorkgroupLane.fromIndex(warpBase + java.lang.Long.numberOfTrailingZeros(remaining))
          accumulated = combine(accumulated, element(context.read(other, source)))
          remaining &= remaining - 1L
        }
        effects.stageRegister(lane, destination, accumulated)
        Completed
      }
    }
    lanes(context, effects, destination, source, memberMask)
  }

  inline def cancelledCta(destination: Register, low: Value, high: Value)(inline kernel: (Long, Long) => Long)
      : Either[Fault, Unit] = {
    def lanes(context: VMContext, effects: VMWriteBuffer, destination: Register, low: Value, high: Value)
        : Either[Fault, Unit] = everyLane(context) { lane =>
      val highBits = context.read(lane, high)
      if ((highBits & 1L) == 0L) Left(Fault.Backend("cannot query CTA coordinates from a failed cancellation"))
      else {
        effects.stageRegister(lane, destination, kernel(context.read(lane, low), highBits))
        Completed
      }
    }
    lanes(context, effects, destination, low, high)
  }

  inline def load(destination: Register, address: Value, width: ScalarBitWidth, access: MemoryAccess)(
      inline element: (Long, Long) => Long,
      inline value: Long => Long
  ): Either[Fault, Unit] = {
    def lanes(
        context: VMContext,
        effects: VMWriteBuffer,
        destination: Register,
        address: Value,
        width: ScalarBitWidth,
        access: MemoryAccess
    ): Either[Fault, Unit] = {
      val prepared =
        VMMemoryAccess.prepareReads(context, access, width.toScalarByteWidth.widen[Int >= 0])(lane =>
          element(context.read(lane, address), 0L)
        )
      if (prepared.isLeft) prepared
      else everyLane(context) { lane =>
        val loaded = VMMemoryAccess.loadValue(context, lane, access, element(context.read(lane, address), 0L), width)
        if (loaded.isRight) effects.stageRegister(lane, destination, value(context.scalar.elem))
        loaded
      }
    }
    lanes(context, effects, destination, address, width, access)
  }

  /** A load of consecutive elements, coherent as one access where [[VMMemoryAccess.coherentVector]] allows. */
  inline def loadVector(destinations: Vec[Register], address: Value, width: ScalarBitWidth, access: MemoryAccess)(
      inline element: (Long, Long) => Long,
      inline value: Long => Long
  ): Either[Fault, Unit] = {
    def lanes(
        context: VMContext,
        effects: VMWriteBuffer,
        destinations: Vec[Register],
        address: Value,
        width: ScalarBitWidth,
        access: MemoryAccess
    ): Either[Fault, Unit] = {
      val elementBytes = width.toScalarByteWidth
      val totalBytes   = destinations.nonNegativeSize * elementBytes
      val prepared     =
        VMMemoryAccess.prepareReads(context, access, totalBytes)(lane => element(context.read(lane, address), 0L))
      if (prepared.isLeft) prepared
      else everyLane(context) { lane =>
        val base  = context.read(lane, address)
        val start = element(base, 0L)
        if (!VMMemoryAccess.coherentVector(context, access, start, totalBytes)) {
          var result = Completed
          var index  = 0
          while (index < destinations.size && result.isRight) {
            result = VMMemoryAccess.loadValue(context, lane, access, element(base, index.toLong), width)
            if (result.isRight) effects.stageRegister(lane, destinations(index), value(context.scalar.elem))
            index += 1
          }
          result
        } else if (totalBytes <= 8)
          context.device.memory.readValueInto(
            GuestAddress(start),
            totalBytes.unsafe[Int in 1 ~ 8],
            context.scalar
          ) match {
            case null =>
              var index = 0
              while (index < destinations.size) {
                effects.stageRegister(lane, destinations(index), value(context.scalar.elem >>> (index * width)))
                index += 1
              }
              Completed
            case fault: Fault => Left(VMWriteBuffer.located(fault, context.pc, lane))
          }
        else
          context.device.memory.read(GuestAddress(start), totalBytes) match {
            case Left(fault)  => Left(VMWriteBuffer.located(fault, context.pc, lane))
            case Right(bytes) =>
              var index = 0
              while (index < destinations.size) {
                effects.stageRegister(
                  lane,
                  destinations(index),
                  value(LittleEndian.decode(bytes, index * elementBytes, elementBytes))
                )
                index += 1
              }
              Completed
          }
      }
    }
    lanes(context, effects, destinations, address, width, access)
  }

  /** A load of consecutive elements of a lane's `.param` storage, from `offset`. */
  inline def loadParameter(destinations: Vec[Register], parameter: Parameter, offset: Int >= 0, width: ScalarBitWidth)(
      inline element: (Long, Long) => Long,
      inline value: Long => Long
  ): Either[Fault, Unit] = {
    def lanes(
        context: VMContext,
        effects: VMWriteBuffer,
        destinations: Vec[Register],
        parameter: Parameter,
        offset: Int >= 0,
        width: ScalarBitWidth
    ): Unit = {
      val elementBytes = width.toScalarByteWidth.widen[Int in 0 ~ 8]
      context.active.foreachLane { lane =>
        val storage = context.lane(lane)
        var index   = 0
        while (index < destinations.size) {
          val at = element(offset.toLong, index.toLong).toInt.unsafe[Int >= 0]
          effects.stageRegister(lane, destinations(index), value(storage.readParameter(parameter, at, elementBytes)))
          index += 1
        }
      }
    }
    lanes(context, effects, destinations, parameter, offset, width)
    Completed
  }

  inline def store(address: Value, source: Value, width: ScalarBitWidth, access: MemoryAccess)(
      inline element: (Long, Long) => Long,
      inline value: Long => Long
  ): Either[Fault, Unit] = {
    def lanes(
        context: VMContext,
        effects: VMWriteBuffer,
        address: Value,
        source: Value,
        width: ScalarBitWidth,
        access: MemoryAccess
    ): Either[Fault, Unit] = everyLane(context) { lane =>
      val at = element(context.read(lane, address), 0L)
      VMMemoryAccess.misaligned(context, access, lane, at, width.toScalarByteWidth) match {
        case null =>
          VMMemoryAccess.stageStore(context, effects, lane, access, at, width, value(context.read(lane, source)))
          Completed
        case fault: Fault => Left(fault)
      }
    }
    lanes(context, effects, address, source, width, access)
  }

  inline def storeVector(address: Value, sources: Vec[Value], width: ScalarBitWidth, access: MemoryAccess)(
      inline element: (Long, Long) => Long,
      inline value: Long => Long
  ): Either[Fault, Unit] = {
    def lanes(
        context: VMContext,
        effects: VMWriteBuffer,
        address: Value,
        sources: Vec[Value],
        width: ScalarBitWidth,
        access: MemoryAccess
    ): Either[Fault, Unit] = {
      val elementBytes = width.toScalarByteWidth
      val totalBytes   = sources.nonNegativeSize * elementBytes
      everyLane(context) { lane =>
        val base  = context.read(lane, address)
        val start = element(base, 0L)
        VMMemoryAccess.misaligned(context, access, lane, start, totalBytes) match {
          case fault: Fault => Left(fault)
          case null         =>
            if (!VMMemoryAccess.coherentVector(context, access, start, totalBytes)) {
              var index = 0
              while (index < sources.size) {
                VMMemoryAccess.stageStore(
                  context,
                  effects,
                  lane,
                  access,
                  element(base, index.toLong),
                  width,
                  value(context.read(lane, sources(index)))
                )
                index += 1
              }
            } else if (totalBytes <= 8) {
              var packed = 0L
              var index  = 0
              while (index < sources.size) {
                packed |= value(context.read(lane, sources(index))) << (index * width)
                index += 1
              }
              effects.stage(VMWrite.Memory(
                lane,
                GuestAddress(start),
                (totalBytes.raw * 8).unsafe[ScalarBitWidth],
                packed,
                system = false
              ))
            } else {
              val bytes = Vec.newBuilder[Byte]
              var index = 0
              while (index < sources.size) {
                bytes ++= LittleEndian.toVec(value(context.read(lane, sources(index))), elementBytes)
                index += 1
              }
              effects.stage(VMWrite.MemoryBytes(lane, GuestAddress(start), bytes.result()))
            }
            Completed
        }
      }
    }
    lanes(context, effects, address, sources, width, access)
  }

  inline def storeParameter(parameter: Parameter, offset: Int >= 0, source: Value, width: ScalarBitWidth)(
      inline element: (Long, Long) => Long,
      inline value: Long => Long
  ): Either[Fault, Unit] = {
    def lanes(
        context: VMContext,
        effects: VMWriteBuffer,
        parameter: Parameter,
        offset: Int >= 0,
        source: Value,
        width: ScalarBitWidth
    ): Unit = context.active.foreachLane(lane =>
      effects.stageParameter(
        lane,
        parameter,
        element(offset.toLong, 0L).toInt.unsafe[Int >= 0],
        width,
        value(context.read(lane, source))
      )
    )
    lanes(context, effects, parameter, offset, source, width)
    Completed
  }

  inline def atomic(
      fetched: Option[Register],
      address: Value,
      source: Value,
      width: ScalarBitWidth,
      space: MemorySpace
  ): Atomic = Atomic(context, fetched, address, source, width, space)

  inline def multiplyMatrices(operation: Matrix.Operation.Multiply)(
      inline element: MatrixOperands[Long] => Long
  ): Either[Fault, Unit] = {
    def warps(context: VMContext, operation: Matrix.Operation.Multiply): Either[Fault, Unit] =
      VMTensor.participating(context).map(_.foreach(VMTensor.multiply(operation, _, context)(element)))
    warps(context, operation)
  }

  inline def multiplyWarpgroup(operation: Matrix.Operation.WarpgroupMultiply)(
      inline element: WarpgroupOperands[Long] => Long
  ): Either[Fault, Unit] = {
    def warpgroups(context: VMContext, operation: Matrix.Operation.WarpgroupMultiply): Either[Fault, Unit] =
      VMWarpgroup.execute(operation, context)(element)
    warpgroups(context, operation)
  }

  inline def multiplyTensorMemory(operation: Tcgen05.Multiply)(
      inline element: (TensorMemory.Product, TensorMemoryOperands[Long]) => Long
  ): Either[Fault, Unit] = {
    def products(context: VMContext, operation: Tcgen05.Multiply): Either[Fault, Unit] =
      VMTensorMemory.multiply(context, operation)(element)
    products(context, operation)
  }

  /** The exchange applies when the write buffer commits, in its atomic order. */
  inline def compareExchange(
      destination: Register,
      address: Value,
      comparator: Value,
      source: Value,
      width: ScalarBitWidth,
      space: MemorySpace
  )(inline kernel: (Long, Long, Long) => Long): Either[Fault, Unit] = {
    class Exchanged(comparator: Long, value: Long) extends AtomicUpdate {
      def apply(previous: Long): Long = kernel(previous, comparator, value)
    }
    def lanes(
        context: VMContext,
        effects: VMWriteBuffer,
        destination: Register,
        address: Value,
        comparator: Value,
        source: Value,
        width: ScalarBitWidth,
        space: MemorySpace
    ): Either[Fault, Unit] = everyLane(context) { lane =>
      val raw = context.read(lane, address)
      VMMemoryAccess.misalignedAtomic(context, lane, raw, width) match {
        case null =>
          effects.stage(VMWrite.Atomic(
            lane,
            Some(destination),
            raw,
            width,
            VMMemoryAccess.atomicTargetsShared(space, raw, width),
            Exchanged(context.read(lane, comparator), context.read(lane, source))
          ))
          Completed
        case fault: Fault => Left(fault)
      }
    }
    lanes(
      context = context,
      effects = effects,
      destination = destination,
      address = address,
      comparator = comparator,
      source = source,
      width = width,
      space = space
    )
  }
}
