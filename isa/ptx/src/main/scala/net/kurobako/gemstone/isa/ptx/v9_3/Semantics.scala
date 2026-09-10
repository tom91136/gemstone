package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.Col

import net.kurobako.gemstone.binary.Bits.bitCount
import net.kurobako.gemstone.binary.Bits.reverseBits
import net.kurobako.gemstone.binary.Bits.byteCount
import net.kurobako.gemstone.binary.Bits.low32
import net.kurobako.gemstone.binary.Bits.signExtend
import net.kurobako.gemstone.binary.Bits.truncate
import net.kurobako.gemstone.binary.IEEE754
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.execution.*
import net.kurobako.gemstone.memory.GuestAddress
import net.kurobako.gemstone.memory.DeviceAllocationMemory
import net.kurobako.gemstone.trace.Trace

import cats.syntax.all.*

import scala.collection.mutable // scalafix:ok GemstoneRuntimeCollections.mutable-collection; architectural state

@SuppressWarnings(
  Array(
    "scalafix:DisableSyntax.var",
    "scalafix:DisableSyntax.return",
    "scalafix:GemstoneRuntimeCollections.mutable-collection",
    "scalafix:GemstoneNoExplicitNew.explicit-new"
  )
)
private object Semantics {
  extension (instruction: ExecutableInstruction) {
    def execute(context: ExecutionContext, effects: Effects[StateWrite]): Either[ExecutionFault, Step] =
      if (executeFast(instruction, context)) Right(Step.Advance)
      else instruction match {
        case ExecutableInstruction.LoadParameter(destinations, parameter, offset, elementWidth, signed) =>
          forEveryLane(context) { lane =>
            val elementBytes = elementWidth / 8
            context.state.parameters(lane).get(parameter) match {
              case Some(value) if offset == 0 && destinations.size == 1 =>
                effects.stage(StateWrite.Register(
                  lane,
                  destinations.head,
                  loadBits(value, elementWidth, signed)
                ))
                Right(())
              case Some(_) =>
                Left(ExecutionFault.InvalidState(s"unsupported local parameter load: $parameter+$offset"))
              case None =>
                context.arguments.get(parameter).toRight(
                  ExecutionFault.InvalidState(s"missing kernel argument $parameter")
                ).flatMap { argument =>
                  if (offset < 0 || offset > argument.bytes.length - elementBytes * destinations.size)
                    Left(ExecutionFault.InvalidState(
                      s"parameter $parameter load at $offset exceeds ${argument.bytes.length} bytes"
                    ))
                  else {
                    destinations.zipWithIndex.foreach { case (destination, index) =>
                      val value = LittleEndian.decode(argument.bytes, offset + index * elementBytes, elementBytes)
                      effects.stage(StateWrite.Register(lane, destination, loadBits(value, elementWidth, signed)))
                    }
                    Right(())
                  }
                }
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.LoadParameterIndirect(destination, address, width, signed) =>
          forEveryLane(context) { lane =>
            context.read(lane, address).flatMap { rawAddress =>
              context.state.readParameterBuffer(rawAddress, byteCount(width)) match {
                case Some(loaded) =>
                  Right(effects.stage(StateWrite.Register(lane, destination, loadBits(loaded, width, signed))))
                case None if isSharedAddress(rawAddress) =>
                  Right(effects.stage(StateWrite.Register(
                    lane,
                    destination,
                    loadBits(context.state.readShared(rawAddress, width), width, signed)
                  )))
                case None if isLocalAddress(rawAddress) =>
                  Right(effects.stage(StateWrite.Register(
                    lane,
                    destination,
                    loadBits(context.state.readLocal(lane, rawAddress, width), width, signed)
                  )))
                case None =>
                  context.memory
                    .readLittleEndian(GuestAddress(rawAddress), byteCount(width))
                    .leftMap(error => ExecutionFault.Memory(s"pc ${context.pc.value}, lane $lane: ${error.message}"))
                    .map(loaded =>
                      effects.stage(StateWrite.Register(
                        lane,
                        destination,
                        loadBits(loaded, width, signed)
                      ))
                    )
              }
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.LoadParameterIndirectVector(destinations, address, width, signed) =>
          forEveryLane(context) { lane =>
            context.read(lane, address).flatMap { rawAddress =>
              destinations.zipWithIndex.foldLeft[Either[ExecutionFault, Unit]](Right(())) {
                case (result, (destination, index)) =>
                  result.flatMap { _ =>
                    val elementAddress = rawAddress + index.toLong * byteCount(width).toLong
                    context.state.readParameterBuffer(elementAddress, byteCount(width)) match {
                      case Some(loaded) =>
                        Right(effects.stage(StateWrite.Register(lane, destination, loadBits(loaded, width, signed))))
                      case None if isSharedAddress(elementAddress) =>
                        Right(effects.stage(StateWrite.Register(
                          lane,
                          destination,
                          loadBits(context.state.readShared(elementAddress, width), width, signed)
                        )))
                      case None if isLocalAddress(elementAddress) =>
                        Right(effects.stage(StateWrite.Register(
                          lane,
                          destination,
                          loadBits(context.state.readLocal(lane, elementAddress, width), width, signed)
                        )))
                      case None =>
                        context.memory
                          .readLittleEndian(GuestAddress(elementAddress), byteCount(width))
                          .leftMap(error =>
                            ExecutionFault.Memory(s"pc ${context.pc.value}, lane $lane: ${error.message}")
                          )
                          .map(loaded =>
                            effects.stage(StateWrite.Register(
                              lane,
                              destination,
                              loadBits(loaded, width, signed)
                            ))
                          )
                    }
                  }
              }
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.StoreParameter(parameter, offset, source, elementWidth) =>
          forEveryLane(context) { lane =>
            context.read(lane, source).map { value =>
              val stored = truncate(value, elementWidth)
              effects.stage(StateWrite.ParameterBytes(lane, parameter, offset, elementWidth, stored))
              if (offset == 0)
                effects.stage(StateWrite.Parameter(lane, parameter, stored))
              source match {
                case Value.Register(name) if context.state.functionRegisters(lane).contains(name) =>
                  effects.stage(StateWrite.FunctionParameter(
                    lane,
                    parameter,
                    context.state.functionRegisters(lane)(name)
                  ))
                case Value.RegisterSlot(index) =>
                  val name = RegisterFile.name(index)
                  context.state.functionRegisters(lane).get(name).foreach(function =>
                    effects.stage(StateWrite.FunctionParameter(lane, parameter, function))
                  )
                case _ => ()
              }
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.Move(destination, source, width) =>
          source match {
            case Value.Function(name) =>
              forEveryLane(context) { lane =>
                effects.stage(StateWrite.FunctionRegister(lane, destination, name))
                Right(())
              }.map(_ => Step.Advance)
            case _ =>
              forEveryLane(context) { lane =>
                context.read(lane, source).map(value =>
                  effects.stage(StateWrite.Register(lane, destination, truncate(value, width)))
                )
              }.map(_ => Step.Advance)
          }
        case ExecutableInstruction.MovePredicate(destination, source) =>
          forEveryLane(context) { lane =>
            context.read(lane, source).map(value =>
              effects.stage(StateWrite.Predicate(lane, destination, value != 0L))
            )
          }.map(_ => Step.Advance)
        case ExecutableInstruction.MoveSplit(destinations, source, partWidth) =>
          forEveryLane(context) { lane =>
            context.read(lane, source).map { value =>
              if (destinations(0) != "_")
                effects.stage(StateWrite.Register(lane, destinations(0), truncate(value, partWidth)))
              if (destinations(1) != "_")
                effects.stage(StateWrite.Register(lane, destinations(1), truncate(value >>> partWidth, partWidth)))
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.MoveCombine(destination, sources, partWidth) =>
          forEveryLane(context) { lane =>
            for {
              low  <- context.read(lane, sources(0))
              high <- context.read(lane, sources(1))
            } yield effects.stage(StateWrite.Register(
              lane,
              destination,
              truncate((low & truncate(-1L, partWidth)) | (truncate(high, partWidth) << partWidth), partWidth * 2)
            ))
          }.map(_ => Step.Advance)
        case ExecutableInstruction.IntegerUnary(operation, destination, source, width) =>
          forEveryLane(context) { lane =>
            context.read(lane, source).map { value =>
              val result = operation match {
                case IntegerUnaryOperation.Negate          => -value
                case IntegerUnaryOperation.Absolute        => Math.abs(signExtend(truncate(value, width), width))
                case IntegerUnaryOperation.BitwiseNot      => ~value
                case IntegerUnaryOperation.PopulationCount =>
                  bitCount(value, width).toLong
                case IntegerUnaryOperation.LeadingZeroCount =>
                  if (width == 32) Integer.numberOfLeadingZeros((value & 0xffffffffL).toInt).toLong
                  else java.lang.Long.numberOfLeadingZeros(value).toLong
                case IntegerUnaryOperation.BitReverse =>
                  reverseBits(value, width)
              }
              effects.stage(StateWrite.Register(lane, destination, truncate(result, width)))
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.BitFind(destination, source, width, signed, shiftAmount) =>
          forEveryLane(context) { lane =>
            context.read(lane, source).map { value =>
              effects.stage(StateWrite.Register(
                lane,
                destination,
                bitFind(value, width, signed, shiftAmount)
              ))
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.DotProduct4(destination, left, right, addend, leftSigned, rightSigned) =>
          forEveryLane(context) { lane =>
            for {
              leftValue   <- context.read(lane, left)
              rightValue  <- context.read(lane, right)
              addendValue <- context.read(lane, addend)
            } yield effects.stage(StateWrite.Register(
              lane,
              destination,
              dotProduct4(leftValue, rightValue, addendValue, leftSigned, rightSigned)
            ))
          }.map(_ => Step.Advance)
        case ExecutableInstruction.IntegerAddWithCarry(destination, left, right, width, carryIn, carryOut) =>
          forEveryLane(context) { lane =>
            for {
              leftValue  <- context.read(lane, left)
              rightValue <- context.read(lane, right)
            } yield {
              val (result, nextCarry) =
                integerAddWithCarry(leftValue, rightValue, width, carryIn, context.state.carry(lane))
              effects.stage(StateWrite.Register(lane, destination, result))
              if (carryOut) effects.stage(StateWrite.Carry(lane, nextCarry))
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.IntegerSubWithBorrow(destination, left, right, width, borrowIn, borrowOut) =>
          forEveryLane(context) { lane =>
            for {
              leftValue  <- context.read(lane, left)
              rightValue <- context.read(lane, right)
            } yield {
              val (result, nextBorrow) =
                integerSubWithBorrow(leftValue, rightValue, width, borrowIn, context.state.carry(lane))
              effects.stage(StateWrite.Register(lane, destination, result))
              if (borrowOut) effects.stage(StateWrite.Carry(lane, nextBorrow))
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.IntegerBinary(operation, destination, left, right, width) =>
          forEveryLane(context) { lane =>
            for {
              leftValue  <- context.read(lane, left)
              rightValue <- context.read(lane, right)
            } yield {
              val result = operation match {
                case IntegerOperation.Add                    => leftValue + rightValue
                case IntegerOperation.Subtract               => leftValue - rightValue
                case IntegerOperation.BitwiseOr              => leftValue | rightValue
                case IntegerOperation.BitwiseAnd             => leftValue & rightValue
                case IntegerOperation.BitwiseXor             => leftValue ^ rightValue
                case IntegerOperation.MultiplyLow            => leftValue * rightValue
                case IntegerOperation.MultiplyHigh(signed)   => multiplyHigh(leftValue, rightValue, width, signed)
                case IntegerOperation.Divide(signed)         => divideInteger(leftValue, rightValue, width, signed)
                case IntegerOperation.Remainder(signed)      => remainderInteger(leftValue, rightValue, width, signed)
                case IntegerOperation.MultiplyWideUnsigned16 =>
                  (leftValue & 0xffffL) * (rightValue & 0xffffL)
                case IntegerOperation.MultiplyWideUnsigned32 =>
                  low32(leftValue) * low32(rightValue)
                case IntegerOperation.MultiplyWideSigned32 => leftValue.toInt.toLong * rightValue.toInt.toLong
                case IntegerOperation.Minimum(signed)      =>
                  if (signed) Math.min(signExtend(leftValue, width), signExtend(rightValue, width))
                  else if (width == 64) {
                    if (java.lang.Long.compareUnsigned(leftValue, rightValue) <= 0) leftValue else rightValue
                  } else Math.min(truncate(leftValue, width), truncate(rightValue, width))
                case IntegerOperation.Maximum(signed) =>
                  if (signed) Math.max(signExtend(leftValue, width), signExtend(rightValue, width))
                  else if (width == 64) {
                    if (java.lang.Long.compareUnsigned(leftValue, rightValue) >= 0) leftValue else rightValue
                  } else Math.max(truncate(leftValue, width), truncate(rightValue, width))
                case IntegerOperation.ShiftLeft         => leftValue << (rightValue & (width - 1).toLong).toInt
                case IntegerOperation.ShiftRightLogical =>
                  if (width == 32) ((leftValue & 0xffffffffL) >>> (rightValue & 31L).toInt).toLong
                  else (leftValue >>> (rightValue & 63L).toInt).toLong
                case IntegerOperation.ShiftRightArithmetic =>
                  if (width == 32) (leftValue.toInt >> (rightValue & 31L).toInt).toLong
                  else (leftValue >> (rightValue & 63L).toInt).toLong
              }
              effects.stage(StateWrite.Register(lane, destination, truncate(result, width)))
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.Convert(destination, source, sourceType, destinationType, rounding) =>
          forEveryLane(context) { lane =>
            context.read(lane, source).map { value =>
              effects.stage(StateWrite.Register(
                lane,
                destination,
                convert(value, sourceType, destinationType, rounding)
              ))
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.MultiplyAdd(destination, left, right, addend, operation) =>
          forEveryLane(context) { lane =>
            for {
              leftValue   <- context.read(lane, left)
              rightValue  <- context.read(lane, right)
              addendValue <- context.read(lane, addend)
            } yield {
              val (result, carry) =
                integerMultiplyAdd(leftValue, rightValue, addendValue, operation, context.state.carry(lane))
              effects.stage(StateWrite.Register(lane, destination, result))
              operation match {
                case IntegerMultiplyAddOperation.Low(_, _, _, true) | IntegerMultiplyAddOperation.High(_, _, _, true) =>
                  effects.stage(StateWrite.Carry(lane, carry))
                case _ => ()
              }
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.FloatBinary(operation, destination, left, right, width) =>
          forEveryLane(context) { lane =>
            for {
              leftBits  <- context.read(lane, left)
              rightBits <- context.read(lane, right)
            } yield {
              val result =
                if (width == 16) {
                  val leftValue  = IEEE754.binary16ToFloat(leftBits)
                  val rightValue = IEEE754.binary16ToFloat(rightBits)
                  val value      = operation match {
                    case FloatOperation.Add      => leftValue + rightValue
                    case FloatOperation.Subtract => leftValue - rightValue
                    case FloatOperation.Multiply => leftValue * rightValue
                    case FloatOperation.Divide   => leftValue / rightValue
                    case FloatOperation.Minimum  => Math.min(leftValue, rightValue)
                    case FloatOperation.Maximum  => Math.max(leftValue, rightValue)
                    case FloatOperation.CopySign => Math.copySign(rightValue, leftValue)
                  }
                  IEEE754.floatToBinary16(value)
                } else if (width == 32) {
                  val leftValue  = IEEE754.binary32ToFloat(leftBits)
                  val rightValue = IEEE754.binary32ToFloat(rightBits)
                  val value      = operation match {
                    case FloatOperation.Add      => leftValue + rightValue
                    case FloatOperation.Subtract => leftValue - rightValue
                    case FloatOperation.Multiply => leftValue * rightValue
                    case FloatOperation.Divide   => leftValue / rightValue
                    case FloatOperation.Minimum  => Math.min(leftValue, rightValue)
                    case FloatOperation.Maximum  => Math.max(leftValue, rightValue)
                    // PTX's operand order is copysign(a, b) = sign(a) * abs(b).
                    // This is the reverse of java.lang.Math.copySign's argument order.
                    case FloatOperation.CopySign => Math.copySign(rightValue, leftValue)
                  }
                  IEEE754.floatToBinary32(value)
                } else {
                  val leftValue  = IEEE754.binary64ToDouble(leftBits)
                  val rightValue = IEEE754.binary64ToDouble(rightBits)
                  val value      = operation match {
                    case FloatOperation.Add      => leftValue + rightValue
                    case FloatOperation.Subtract => leftValue - rightValue
                    case FloatOperation.Multiply => leftValue * rightValue
                    case FloatOperation.Divide   => leftValue / rightValue
                    case FloatOperation.Minimum  => Math.min(leftValue, rightValue)
                    case FloatOperation.Maximum  => Math.max(leftValue, rightValue)
                    case FloatOperation.CopySign => Math.copySign(rightValue, leftValue)
                  }
                  IEEE754.doubleToBinary64(value)
                }
              effects.stage(StateWrite.Register(lane, destination, result))
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.FloatBinaryPacked(operation, destination, left, right, bf16) =>
          forEveryLane(context) { lane =>
            for {
              leftBits  <- context.read(lane, left)
              rightBits <- context.read(lane, right)
            } yield effects.stage(
              StateWrite.Register(lane, destination, packedBinaryBits(operation, leftBits, rightBits, bf16))
            )
          }.map(_ => Step.Advance)
        case ExecutableInstruction.FloatUnary(operation, destination, source, width) =>
          forEveryLane(context) { lane =>
            context.read(lane, source).map { value =>
              effects.stage(StateWrite.Register(lane, destination, floatUnaryBits(operation, value, width)))
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.FusedMultiplyAdd(destination, left, right, addend, width) =>
          forEveryLane(context) { lane =>
            for {
              leftBits   <- context.read(lane, left)
              rightBits  <- context.read(lane, right)
              addendBits <- context.read(lane, addend)
            } yield {
              val result = if (width == 16) {
                val value = Math.fma(
                  IEEE754.binary16ToFloat(leftBits),
                  IEEE754.binary16ToFloat(rightBits),
                  IEEE754.binary16ToFloat(addendBits)
                )
                IEEE754.floatToBinary16(value)
              } else if (width == 32) {
                val value = Math.fma(
                  IEEE754.binary32ToFloat(leftBits),
                  IEEE754.binary32ToFloat(rightBits),
                  IEEE754.binary32ToFloat(addendBits)
                )
                IEEE754.floatToBinary32(value)
              } else {
                val value = Math.fma(
                  IEEE754.binary64ToDouble(leftBits),
                  IEEE754.binary64ToDouble(rightBits),
                  IEEE754.binary64ToDouble(addendBits)
                )
                IEEE754.doubleToBinary64(value)
              }
              effects.stage(StateWrite.Register(lane, destination, result))
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.FusedMultiplyAddPacked(destination, left, right, addend, bf16) =>
          forEveryLane(context) { lane =>
            for {
              leftBits   <- context.read(lane, left)
              rightBits  <- context.read(lane, right)
              addendBits <- context.read(lane, addend)
            } yield effects.stage(
              StateWrite.Register(lane, destination, packedFmaBits(leftBits, rightBits, addendBits, bf16))
            )
          }.map(_ => Step.Advance)
        case ExecutableInstruction.FusedMultiplyAddBf16(destination, left, right, addend) =>
          forEveryLane(context) { lane =>
            for {
              leftBits   <- context.read(lane, left)
              rightBits  <- context.read(lane, right)
              addendBits <- context.read(lane, addend)
            } yield effects.stage(
              StateWrite.Register(lane, destination, bf16FmaBits(leftBits, rightBits, addendBits))
            )
          }.map(_ => Step.Advance)
        case ExecutableInstruction.ShuffleDown(destination, validity, source, delta, clamp, _, width) =>
          forEveryLane(context) { lane =>
            for {
              deltaValue <- context.read(lane, delta)
              clampValue <- context.read(lane, clamp)
              distance   = (deltaValue & 31L).toInt
              clampLane  = shuffleClamp(clampValue)
              sourceLane = lane + distance
              laneInWarp = lane & 31
              valid      = laneInWarp + distance <= clampLane && sourceLane < context.state.laneCount
              targetLane = if (valid) sourceLane else lane
              value <- if (targetLane < context.state.laneCount)
                context.read(targetLane, source)
              else Right(0L)
            } yield {
              effects.stage(StateWrite.Register(lane, destination, truncate(value, width)))
              validity.foreach(name => effects.stage(StateWrite.Predicate(lane, name, valid)))
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.ShuffleUp(destination, validity, source, delta, clamp, _, width) =>
          forEveryLane(context) { lane =>
            for {
              deltaValue <- context.read(lane, delta)
              clampValue <- context.read(lane, clamp)
              distance   = (deltaValue & 31L).toInt
              clampLane  = shuffleClamp(clampValue)
              laneInWarp = lane & 31
              valid      = laneInWarp >= distance && laneInWarp - distance >= clampLane
              targetLane = if (valid) lane - distance else lane
              value <- if (targetLane < context.state.laneCount)
                context.read(targetLane, source)
              else Right(0L)
            } yield {
              effects.stage(StateWrite.Register(lane, destination, truncate(value, width)))
              validity.foreach(name => effects.stage(StateWrite.Predicate(lane, name, valid)))
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.ShuffleIndex(destination, validity, source, index, clamp, _, width) =>
          forEveryLane(context) { lane =>
            for {
              indexValue <- context.read(lane, index)
              clampValue <- context.read(lane, clamp)
              laneInWarp   = lane & 31
              requested    = (indexValue & 31L).toInt
              targetInWarp = Math.min(requested, shuffleClamp(clampValue))
              targetLane   = (lane - laneInWarp) + targetInWarp
              valid        = requested <= shuffleClamp(clampValue) && targetLane < context.state.laneCount
              value <- context.read(if (valid) targetLane else lane, source)
            } yield {
              effects.stage(StateWrite.Register(lane, destination, truncate(value, width)))
              validity.foreach(name => effects.stage(StateWrite.Predicate(lane, name, valid)))
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.ShuffleBfly(destination, validity, source, index, clamp, _, width) =>
          forEveryLane(context) { lane =>
            for {
              indexValue <- context.read(lane, index)
              clampValue <- context.read(lane, clamp)
              laneInWarp = lane & 31
              bflyLane   = laneInWarp ^ (indexValue & 31L).toInt
              segmask    = ((clampValue >>> 8) & 31L).toInt
              clampLane  = (clampValue & 31L).toInt
              maxLane    = (laneInWarp & segmask) | (clampLane & ~segmask)
              valid      = bflyLane <= maxLane
              targetLane = (lane - laneInWarp) + bflyLane
              value <- context.read(if (valid && targetLane < context.state.laneCount) targetLane else lane, source)
            } yield {
              effects.stage(StateWrite.Register(lane, destination, truncate(value, width)))
              validity.foreach(name => effects.stage(StateWrite.Predicate(lane, name, valid)))
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.VoteBallot(destination, predicate, memberMask) =>
          forEveryLane(context) { lane =>
            context.read(lane, memberMask).flatMap { maskValue =>
              val warpBase = lane & ~31
              (0 until 32).foldLeft[Either[ExecutionFault, Long]](Right(0L)) {
                case (result, laneInWarp) =>
                  val candidate = warpBase + laneInWarp
                  if (
                    (maskValue & (1L << laneInWarp)) == 0L || !context.active.contains(candidate) ||
                    candidate >= context.state.laneCount
                  ) result
                  else
                    result.flatMap { ballot =>
                      context.state.predicates(candidate).get(predicate).toRight(
                        ExecutionFault.UndefinedRegister(predicate, context.pc, candidate)
                      ).map { selected =>
                        if (selected) ballot | (1L << laneInWarp) else ballot
                      }
                    }
              }.map(ballot => effects.stage(StateWrite.Register(lane, destination, truncate(ballot, 32))))
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.VotePredicate(destination, predicate, memberMask, operation) =>
          forEveryLane(context) { lane =>
            context.read(lane, memberMask).flatMap { maskValue =>
              val warpBase = lane & ~31
              (0 until 32).foldLeft[Either[ExecutionFault, Col[Boolean]]](Right(Col.empty)) {
                case (result, laneInWarp) =>
                  val candidate = warpBase + laneInWarp
                  if (
                    (maskValue & (1L << laneInWarp)) == 0L || !context.active.contains(candidate) ||
                    candidate >= context.state.laneCount
                  ) result
                  else
                    result.flatMap { values =>
                      context.state.predicates(candidate).get(predicate).toRight(
                        ExecutionFault.UndefinedRegister(predicate, context.pc, candidate)
                      ).map(values :+ _)
                    }
              }.map { values =>
                val selected = operation match {
                  case VoteOperation.All     => values.nonEmpty && values.forall(identity)
                  case VoteOperation.Any     => values.exists(identity)
                  case VoteOperation.Uniform => values.distinct.size <= 1
                }
                effects.stage(StateWrite.Predicate(lane, destination, selected))
              }
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.BitFieldExtract(destination, source, start, widthValue, width, signed) =>
          forEveryLane(context) { lane =>
            for {
              sourceValue <- context.read(lane, source)
              startValue  <- context.read(lane, start)
              countValue  <- context.read(lane, widthValue)
            } yield {
              val result = extractBits(sourceValue, startValue, countValue, width, signed)
              effects.stage(StateWrite.Register(lane, destination, result))
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.BitFieldInsert(destination, insertValue, baseValue, bitPosition, bitLength, width) =>
          forEveryLane(context) { lane =>
            for {
              insert <- context.read(lane, insertValue)
              base   <- context.read(lane, baseValue)
              start  <- context.read(lane, bitPosition)
              count  <- context.read(lane, bitLength)
            } yield effects.stage(StateWrite.Register(lane, destination, insertBits(insert, base, start, count, width)))
          }.map(_ => Step.Advance)
        case ExecutableInstruction.BitMask(destination, start, widthValue, mode) =>
          forEveryLane(context) { lane =>
            for {
              startValue <- context.read(lane, start)
              countValue <- context.read(lane, widthValue)
            } yield effects.stage(StateWrite.Register(
              lane,
              destination,
              bitMask(startValue, countValue, mode)
            ))
          }.map(_ => Step.Advance)
        case ExecutableInstruction.BytePermute(destination, left, right, control, mode) =>
          forEveryLane(context) { lane =>
            for {
              leftValue    <- context.read(lane, left)
              rightValue   <- context.read(lane, right)
              controlValue <- context.read(lane, control)
            } yield effects.stage(StateWrite.Register(
              lane,
              destination,
              permuteBytes(leftValue, rightValue, controlValue, mode)
            ))
          }.map(_ => Step.Advance)
        case ExecutableInstruction.Select(destination, whenTrue, whenFalse, predicate, width) =>
          forEveryLane(context) { lane =>
            for {
              trueValue  <- context.read(lane, whenTrue)
              falseValue <- context.read(lane, whenFalse)
              condition  <- context.state.predicates(lane).get(predicate).toRight(
                ExecutionFault.UndefinedRegister(predicate, context.pc, lane)
              )
            } yield effects.stage(StateWrite.Register(
              lane,
              destination,
              truncate(if (condition) trueValue else falseValue, width)
            ))
          }.map(_ => Step.Advance)
        case ExecutableInstruction.LoadGlobal(destination, address, width, signed) =>
          forEveryLane(context) { lane =>
            context.read(lane, address).flatMap { rawAddress =>
              context.state.readParameterBuffer(rawAddress, byteCount(width)) match {
                case Some(loaded) =>
                  Right(effects.stage(StateWrite.Register(lane, destination, loadBits(loaded, width, signed))))
                case None =>
                  context.memory
                    .readLittleEndian(GuestAddress(rawAddress), byteCount(width))
                    .leftMap(error => ExecutionFault.Memory(s"pc ${context.pc.value}, lane $lane: ${error.message}"))
                    .map { loaded =>
                      effects.stage(StateWrite.Register(
                        lane,
                        destination,
                        loadBits(loaded, width, signed)
                      ))
                    }
              }
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.LoadGeneric(destination, address, width, signed) =>
          forEveryLane(context) { lane =>
            context.read(lane, address).flatMap { rawAddress =>
              context.state.readParameterBuffer(rawAddress, byteCount(width)) match {
                case Some(value) => Right(effects.stage(StateWrite.Register(
                    lane,
                    destination,
                    loadBits(value, width, signed)
                  )))
                case None if isSharedAddress(rawAddress) =>
                  Right(effects.stage(StateWrite.Register(
                    lane,
                    destination,
                    loadBits(context.state.readShared(rawAddress, width), width, signed)
                  )))
                case None if isLocalAddress(rawAddress) =>
                  Right(effects.stage(StateWrite.Register(
                    lane,
                    destination,
                    loadBits(context.state.readLocal(lane, rawAddress, width), width, signed)
                  )))
                case None =>
                  context.memory
                    .readLittleEndian(GuestAddress(rawAddress), byteCount(width))
                    .leftMap(error => ExecutionFault.Memory(s"pc ${context.pc.value}, lane $lane: ${error.message}"))
                    .map(value =>
                      effects.stage(StateWrite.Register(
                        lane,
                        destination,
                        loadBits(value, width, signed)
                      ))
                    )
              }
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.DiscardGlobalLoad(address, width) =>
          forEveryLane(context) { lane =>
            context.read(lane, address).flatMap { rawAddress =>
              if (context.state.readParameterBuffer(rawAddress, byteCount(width)).nonEmpty) Right(())
              else
                context.memory
                  .readLittleEndian(GuestAddress(rawAddress), byteCount(width))
                  .leftMap(error => ExecutionFault.Memory(s"pc ${context.pc.value}, lane $lane: ${error.message}"))
                  .map(_ => ())
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.LoadGlobalVector(destinations, address, width, signed) =>
          forEveryLane(context) { lane =>
            context.read(lane, address).flatMap { rawAddress =>
              if (coherentGlobalVector(context, rawAddress, destinations.size, width))
                loadCoherentVector(context, effects, lane, rawAddress, destinations, width, signed)
              else destinations.zipWithIndex.foldLeft[Either[ExecutionFault, Unit]](Right(())) {
                case (result, (destination, index)) =>
                  result.flatMap { _ =>
                    context.memory
                      .readLittleEndian(
                        GuestAddress(rawAddress + index.toLong * byteCount(width).toLong),
                        byteCount(width)
                      )
                      .leftMap(error => ExecutionFault.Memory(s"pc ${context.pc.value}, lane $lane: ${error.message}"))
                      .map(value =>
                        effects.stage(StateWrite.Register(
                          lane,
                          destination,
                          loadBits(value, width, signed)
                        ))
                      )
                  }
              }
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.LoadGenericVector(destinations, address, width, signed) =>
          forEveryLane(context) { lane =>
            context.read(lane, address).flatMap { rawAddress =>
              if (coherentGlobalVector(context, rawAddress, destinations.size, width))
                loadCoherentVector(context, effects, lane, rawAddress, destinations, width, signed)
              else destinations.zipWithIndex.foldLeft[Either[ExecutionFault, Unit]](Right(())) {
                case (result, (destination, index)) =>
                  result.flatMap { _ =>
                    val elementAddress = rawAddress + index.toLong * byteCount(width).toLong
                    context.state.readParameterBuffer(elementAddress, byteCount(width)) match {
                      case Some(value) => Right(effects.stage(StateWrite.Register(
                          lane,
                          destination,
                          loadBits(value, width, signed)
                        )))
                      case None if isSharedAddress(elementAddress) =>
                        Right(effects.stage(StateWrite.Register(
                          lane,
                          destination,
                          loadBits(context.state.readShared(elementAddress, width), width, signed)
                        )))
                      case None if isLocalAddress(elementAddress) =>
                        Right(effects.stage(StateWrite.Register(
                          lane,
                          destination,
                          loadBits(context.state.readLocal(lane, elementAddress, width), width, signed)
                        )))
                      case None =>
                        context.memory
                          .readLittleEndian(GuestAddress(elementAddress), byteCount(width))
                          .leftMap(error =>
                            ExecutionFault.Memory(s"pc ${context.pc.value}, lane $lane: ${error.message}")
                          )
                          .map(value =>
                            effects.stage(StateWrite.Register(
                              lane,
                              destination,
                              loadBits(value, width, signed)
                            ))
                          )
                    }
                  }
              }
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.StoreGlobal(address, source, width) =>
          forEveryLane(context) { lane =>
            for {
              rawAddress <- context.read(lane, address)
              value      <- context.read(lane, source)
            } yield
              if (context.state.parameterBufferRange(rawAddress, byteCount(width)).nonEmpty)
                effects.stage(StateWrite.ParameterBuffer(rawAddress, width, value))
              else effects.stage(StateWrite.Memory(lane, GuestAddress(rawAddress), width, value))
          }.map(_ => Step.Advance)
        case ExecutableInstruction.StoreGeneric(address, source, width) =>
          forEveryLane(context) { lane =>
            for {
              rawAddress <- context.read(lane, address)
              value      <- context.read(lane, source)
            } yield
              if (context.state.parameterBufferRange(rawAddress, byteCount(width)).nonEmpty)
                effects.stage(StateWrite.ParameterBuffer(rawAddress, width, value))
              else if (isSharedAddress(rawAddress)) effects.stage(StateWrite.Shared(lane, rawAddress, width, value))
              else if (isLocalAddress(rawAddress)) effects.stage(StateWrite.Local(lane, rawAddress, width, value))
              else effects.stage(StateWrite.Memory(lane, GuestAddress(rawAddress), width, value))
          }.map(_ => Step.Advance)
        case ExecutableInstruction.StoreGlobalVector(address, sources, width) =>
          forEveryLane(context) { lane =>
            context.read(lane, address).flatMap { rawAddress =>
              if (coherentGlobalVector(context, rawAddress, sources.size, width))
                storeCoherentVector(context, effects, lane, rawAddress, sources, width)
              else sources.zipWithIndex.foldLeft[Either[ExecutionFault, Unit]](Right(())) {
                case (result, (source, index)) =>
                  result.flatMap { _ =>
                    context.read(lane, Value.Register(source)).map(value =>
                      effects.stage(StateWrite.Memory(
                        lane,
                        GuestAddress(rawAddress + index.toLong * byteCount(width).toLong),
                        width,
                        value
                      ))
                    )
                  }
              }
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.StoreGenericVector(address, sources, width) =>
          forEveryLane(context) { lane =>
            context.read(lane, address).flatMap { rawAddress =>
              if (coherentGlobalVector(context, rawAddress, sources.size, width))
                storeCoherentVector(context, effects, lane, rawAddress, sources, width)
              else sources.zipWithIndex.foldLeft[Either[ExecutionFault, Unit]](Right(())) {
                case (result, (source, index)) =>
                  result.flatMap { _ =>
                    context.read(lane, Value.Register(source)).map { value =>
                      val elementAddress = rawAddress + index.toLong * byteCount(width).toLong
                      if (isSharedAddress(elementAddress))
                        effects.stage(StateWrite.Shared(lane, elementAddress, width, value))
                      else if (isLocalAddress(elementAddress))
                        effects.stage(StateWrite.Local(lane, elementAddress, width, value))
                      else
                        effects.stage(StateWrite.Memory(lane, GuestAddress(elementAddress), width, value))
                    }
                  }
              }
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.LoadShared(destination, address, width, signed) =>
          forEveryLane(context) { lane =>
            context.read(lane, address).map(rawAddress =>
              effects.stage(StateWrite.Register(
                lane,
                destination,
                loadBits(context.state.readShared(rawAddress, width), width, signed)
              ))
            )
          }.map(_ => Step.Advance)
        case ExecutableInstruction.LoadSharedVector(destinations, address, width, signed) =>
          forEveryLane(context) { lane =>
            context.read(lane, address).map { rawAddress =>
              destinations.zipWithIndex.foreach { case (destination, index) =>
                effects.stage(StateWrite.Register(
                  lane,
                  destination,
                  loadBits(
                    context.state.readShared(rawAddress + index.toLong * byteCount(width), width),
                    width,
                    signed
                  )
                ))
              }
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.LoadLocal(destination, address, width, signed) =>
          forEveryLane(context) { lane =>
            context.read(lane, address).map(rawAddress =>
              effects.stage(StateWrite.Register(
                lane,
                destination,
                loadBits(context.state.readLocal(lane, rawAddress, width), width, signed)
              ))
            )
          }.map(_ => Step.Advance)
        case ExecutableInstruction.LoadLocalVector(destinations, address, width, signed) =>
          forEveryLane(context) { lane =>
            context.read(lane, address).map { rawAddress =>
              destinations.zipWithIndex.foreach { case (destination, index) =>
                effects.stage(StateWrite.Register(
                  lane,
                  destination,
                  loadBits(
                    context.state.readLocal(lane, rawAddress + index.toLong * byteCount(width), width),
                    width,
                    signed
                  )
                ))
              }
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.StoreShared(address, source, width) =>
          forEveryLane(context) { lane =>
            for {
              rawAddress <- context.read(lane, address)
              value      <- context.read(lane, source)
            } yield effects.stage(StateWrite.Shared(lane, rawAddress, width, value))
          }.map(_ => Step.Advance)
        case ExecutableInstruction.StoreSharedVector(address, sources, width) =>
          forEveryLane(context) { lane =>
            context.read(lane, address).flatMap { rawAddress =>
              sources.zipWithIndex.foldLeft[Either[ExecutionFault, Unit]](Right(())) {
                case (result, (source, index)) =>
                  result.flatMap { _ =>
                    context.read(lane, Value.Register(source)).map(value =>
                      effects.stage(StateWrite.Shared(
                        lane,
                        rawAddress + index.toLong * byteCount(width),
                        width,
                        value
                      ))
                    )
                  }
              }
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.StoreLocal(address, source, width) =>
          forEveryLane(context) { lane =>
            for {
              rawAddress <- context.read(lane, address)
              value      <- context.read(lane, source)
            } yield effects.stage(StateWrite.Local(lane, rawAddress, width, value))
          }.map(_ => Step.Advance)
        case ExecutableInstruction.StoreLocalVector(address, sources, width) =>
          forEveryLane(context) { lane =>
            context.read(lane, address).flatMap { rawAddress =>
              sources.zipWithIndex.foldLeft[Either[ExecutionFault, Unit]](Right(())) {
                case (result, (source, index)) =>
                  result.flatMap { _ =>
                    context.read(lane, Value.Register(source)).map(value =>
                      effects.stage(StateWrite.Local(
                        lane,
                        rawAddress + index.toLong * byteCount(width),
                        width,
                        value
                      ))
                    )
                  }
              }
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.AsyncCopy(destination, source, size) =>
          forEveryLane(context) { lane =>
            for {
              destinationAddress <- context.read(lane, destination)
              sourceAddress      <- context.read(lane, source)
              _                  <- (0 until size by 8).foldLeft[Either[ExecutionFault, Unit]](Right(())) {
                case (result, offset) =>
                  result.flatMap { _ =>
                    val width = Math.min(8, size - offset) * 8
                    context.memory
                      .readLittleEndian(GuestAddress(sourceAddress + offset.toLong), byteCount(width))
                      .leftMap(error => ExecutionFault.Memory(s"pc ${context.pc.value}, lane $lane: ${error.message}"))
                      .map(value =>
                        effects.stage(StateWrite.Shared(
                          lane,
                          destinationAddress + offset.toLong,
                          width,
                          value
                        ))
                      )
                  }
              }
            } yield ()
          }.map(_ => Step.Advance)
        case ExecutableInstruction.AsyncCommit | ExecutableInstruction.AsyncWait                 => Right(Step.Advance)
        case ExecutableInstruction.WarpReduce(destination, source, memberMask, operation, width) =>
          val values                          = mutable.Map.empty[Int, Long]
          val masks                           = mutable.Map.empty[Int, Long]
          var failure: Option[ExecutionFault] = None
          context.active.foreachLane { lane =>
            if (failure.isEmpty)
              context.read(lane, source).flatMap(value =>
                context.read(lane, memberMask).map { mask =>
                  values(lane) = value
                  masks(lane) = mask
                }
              ).left.foreach(error => failure = Some(error))
          }
          failure match {
            case Some(error) => Left(error)
            case None        =>
              context.active.foreachLane { lane =>
                val warpBase = lane & ~31
                val mask     = masks.getOrElse(lane, 0L)
                val members  = Col.toCol(values.iterator.collect {
                  case (candidate, value)
                      if (candidate & ~31) == warpBase && (mask & (1L << (candidate & 31))) != 0L => value
                })
                if ((mask & (1L << (lane & 31))) == 0L || members.isEmpty)
                  failure = Some(ExecutionFault.InvalidState(s"redux.sync lane $lane is absent from its member mask"))
                else {
                  val reduced = members.tail.foldLeft(truncate(members.head, width)) { (accumulator, rawValue) =>
                    val value = truncate(rawValue, width)
                    operation match {
                      case IntegerOperation.Add                       => truncate(accumulator + value, width)
                      case IntegerOperation.Minimum(signed) if signed =>
                        Math.min(signExtend(accumulator, width), signExtend(value, width))
                      case IntegerOperation.Minimum(_) =>
                        if (java.lang.Long.compareUnsigned(accumulator, value) < 0) accumulator else value
                      case IntegerOperation.Maximum(signed) if signed =>
                        Math.max(signExtend(accumulator, width), signExtend(value, width))
                      case IntegerOperation.Maximum(_) =>
                        if (java.lang.Long.compareUnsigned(accumulator, value) > 0) accumulator else value
                      case _ => accumulator
                    }
                  }
                  effects.stage(StateWrite.Register(lane, destination, truncate(reduced, width)))
                }
              }
              failure.toLeft(Step.Advance)
          }
        case ExecutableInstruction.AtomicAdd(destination, address, source, width, global) =>
          forEveryLane(context) { lane =>
            for {
              rawAddress  <- context.read(lane, address)
              sourceValue <- context.read(lane, source)
            } yield
              if (global)
                effects.stage(StateWrite.AtomicMemoryAdd(
                  lane,
                  destination,
                  GuestAddress(rawAddress),
                  width,
                  sourceValue
                ))
              else effects.stage(StateWrite.AtomicSharedAdd(lane, destination, rawAddress, width, sourceValue))
          }.map(_ => Step.Advance)
        case ExecutableInstruction.AtomicMin(destination, address, source, width, global) =>
          forEveryLane(context) { lane =>
            for {
              rawAddress  <- context.read(lane, address)
              sourceValue <- context.read(lane, source)
            } yield
              if (global)
                effects.stage(StateWrite.AtomicMemoryMin(
                  lane,
                  destination,
                  GuestAddress(rawAddress),
                  width,
                  sourceValue
                ))
              else effects.stage(StateWrite.AtomicSharedMin(lane, destination, rawAddress, width, sourceValue))
          }.map(_ => Step.Advance)
        case ExecutableInstruction.AtomicAddFloat(destination, address, source, width) =>
          forEveryLane(context) { lane =>
            for {
              rawAddress  <- context.read(lane, address)
              sourceValue <- context.read(lane, source)
            } yield effects.stage(StateWrite.AtomicMemoryAddFloat(
              lane,
              destination,
              GuestAddress(rawAddress),
              width,
              sourceValue
            ))
          }.map(_ => Step.Advance)
        case ExecutableInstruction.SetPredicate(destination, comparison, kind, left, right, width) =>
          forEveryLane(context) { lane =>
            for {
              leftValue  <- context.read(lane, left)
              rightValue <- context.read(lane, right)
            } yield effects.stage(
              StateWrite.Predicate(lane, destination, compare(leftValue, rightValue, comparison, kind, width))
            )
          }.map(_ => Step.Advance)
        case ExecutableInstruction.PredicateCompare(destination, comparison, left, right) =>
          forEveryLane(context) { lane =>
            for {
              leftValue <- context.state.predicates(lane).get(left).toRight(
                ExecutionFault.UndefinedRegister(left, context.pc, lane)
              )
              rightValue <- context.state.predicates(lane).get(right).toRight(
                ExecutionFault.UndefinedRegister(right, context.pc, lane)
              )
            } yield effects.stage(StateWrite.Predicate(
              lane,
              destination,
              comparePredicates(leftValue, rightValue, comparison)
            ))
          }.map(_ => Step.Advance)
        case ExecutableInstruction.PredicateUnary(destination, source) =>
          forEveryLane(context) { lane =>
            context.state.predicates(lane).get(source).toRight(
              ExecutionFault.UndefinedRegister(source, context.pc, lane)
            ).map(value => effects.stage(StateWrite.Predicate(lane, destination, !value)))
          }.map(_ => Step.Advance)
        case ExecutableInstruction.PredicateBinary(operation, destination, left, right) =>
          forEveryLane(context) { lane =>
            for {
              leftValue <- context.state.predicates(lane).get(left).toRight(
                ExecutionFault.UndefinedRegister(left, context.pc, lane)
              )
              rightValue <- context.state.predicates(lane).get(right).toRight(
                ExecutionFault.UndefinedRegister(right, context.pc, lane)
              )
            } yield effects.stage(StateWrite.Predicate(
              lane,
              destination,
              operation match {
                case PredicateOperation.And => leftValue && rightValue
                case PredicateOperation.Or  => leftValue || rightValue
                case PredicateOperation.Xor => leftValue ^ rightValue
              }
            ))
          }.map(_ => Step.Advance)
        case ExecutableInstruction.Branch(target)  => Right(Step.Jump(target))
        case ExecutableInstruction.BarrierSync(id) =>
          // A workgroup barrier is identified by its hardware barrier slot, not
          // by the static PC at which a divergent cohort happens to arrive.
          Right(Step.Suspend(WaitKey(WaitScope.Workgroup, id.toLong, 0L)))
        case ExecutableInstruction.BarrierReduce(_, id, _, _) =>
          Right(Step.Suspend(WaitKey(WaitScope.Workgroup, id.toLong, 0L)))
        case ExecutableInstruction.WarpBarrier(memberMask) =>
          var firstLane = -1
          context.active.foreachLane { lane => if (firstLane < 0) firstLane = lane }
          context.read(firstLane, memberMask).flatMap { memberMaskValue =>
            var mismatch = false
            context.active.foreachLane { lane =>
              if (!mismatch) {
                context.read(lane, memberMask) match {
                  case Right(value) if value == memberMaskValue => ()
                  case _                                        => mismatch = true
                }
              }
            }
            if (mismatch)
              Left(ExecutionFault.InvalidState("bar.warp.sync member mask differs across active lanes"))
            else
              Right(Step.Suspend(WaitKey(WaitScope.Warp, memberMaskValue, context.pc.value.toLong)))
          }
        case ExecutableInstruction.MemoryBarrier                              => Right(Step.Advance)
        case ExecutableInstruction.Prefetch                                   => Right(Step.Advance)
        case ExecutableInstruction.Call(function, returnParameter, parameter) =>
          forEveryLane(context) { lane =>
            context.functionName(lane, function).flatMap { name =>
              executeBuiltin(context, lane, name, returnParameter, parameter).getOrElse {
                context.kernel.functions.get(name).toRight(
                  ExecutionFault.InvalidState(s"missing device function $name")
                ).flatMap(deviceFunction =>
                  executeFunction(context, lane, deviceFunction, parameter, returnParameter)
                )
              }
            }
          }.map(_ => Step.Advance)
        case ExecutableInstruction.Trap =>
          Left(ExecutionFault.ArchitecturalTrap("ptx", 0L, context.pc))
        case ExecutableInstruction.Return => Right(Step.Exit(context.active))
      }
  }

  /** CUB's scan descriptors rely on coherent aligned status/value transfers, including 16-byte double descriptors. This
    * target behavior is stronger than PTX's minimum vector ordering; it is not a general 128-bit atomic guarantee. Keep
    * shared/local/parameter routing separate and retain the existing word path through eight bytes.
    */
  private def coherentGlobalVector(context: ExecutionContext, address: Long, count: Int, width: Int): Boolean = {
    val size = count * byteCount(width)
    size > 0 && size <= 16 && (size & (size - 1)) == 0 && (address & (size - 1).toLong) == 0L &&
    !isSharedAddress(address) && !isLocalAddress(address) &&
    context.state.parameterBufferRange(address, size).isEmpty
  }

  private def loadCoherentVector(
      context: ExecutionContext,
      effects: Effects[StateWrite],
      lane: Int,
      address: Long,
      destinations: Col[String],
      width: Int,
      signed: Boolean
  ): Either[ExecutionFault, Unit] =
    if (destinations.size * byteCount(width) <= 8) {
      context.memory.readLittleEndian(GuestAddress(address), destinations.size * byteCount(width))
        .leftMap(error => ExecutionFault.Memory(s"pc ${context.pc.value}, lane $lane: ${error.message}"))
        .map { packed =>
          destinations.zipWithIndex.foreach { case (destination, index) =>
            effects.stage(StateWrite.Register(lane, destination, loadBits(packed >>> (index * width), width, signed)))
          }
        }

    } else {
      context.memory.readBytes(GuestAddress(address), destinations.size * byteCount(width))
        .leftMap(error => ExecutionFault.Memory(s"pc ${context.pc.value}, lane $lane: ${error.message}"))
        .map { bytes =>
          destinations.zipWithIndex.foreach { case (destination, index) =>
            val value =
              net.kurobako.gemstone.binary.LittleEndian.decode(bytes, index * byteCount(width), byteCount(width))
            effects.stage(StateWrite.Register(lane, destination, loadBits(value, width, signed)))
          }
        }
    }

  private def storeCoherentVector(
      context: ExecutionContext,
      effects: Effects[StateWrite],
      lane: Int,
      address: Long,
      sources: Col[String],
      width: Int
  ): Either[ExecutionFault, Unit] =
    if (sources.size * byteCount(width) <= 8) {
      sources.zipWithIndex.foldLeft[Either[ExecutionFault, Long]](Right(0L)) { case (result, (source, index)) =>
        for {
          packed <- result
          value  <- context.read(lane, Value.Register(source))
        } yield packed | (truncate(value, width) << (index * width))
      }.map(packed => effects.stage(StateWrite.Memory(lane, GuestAddress(address), sources.size * width, packed)))

    } else {
      sources.traverse(source => context.read(lane, Value.Register(source)))
        .map(values => effects.stage(StateWrite.MemoryVector(lane, GuestAddress(address), width, values)))
    }

  /** The common nvcc path is a dense register-only instruction stream. Keep the checked, staged path below for memory
    * and rendezvous operations, but avoid allocating an Either and a StateWrite for every lane of an arithmetic
    * instruction. Reads still happen before the destination is changed, matching the staged path's aliasing rule.
    */
  private def executeFast(
      instruction: ExecutableInstruction,
      context: ExecutionContext
  ): Boolean = instruction match {
    case ExecutableInstruction.Move(destination, Value.Function(function), _) =>
      context.active.foreachLane(lane => context.state.functionRegisters(lane)(destination) = function)
      true
    case ExecutableInstruction.Move(destination, source, width) =>
      val destinationIndex = RegisterFile.index(destination)
      context.active.foreachLane(lane =>
        writeRegister(
          context.state.registers(lane),
          destination,
          destinationIndex,
          truncate(readFast(context, lane, source), width)
        )
      )
      true
    case ExecutableInstruction.MovePredicate(destination, source) =>
      val destinationIndex = PredicateFile.index(destination)
      context.active.foreachLane(lane =>
        writePredicate(
          context.state.predicates(lane),
          destination,
          destinationIndex,
          readFast(context, lane, source) != 0L
        )
      )
      true
    case ExecutableInstruction.MoveSplit(destinations, source, partWidth) =>
      val lowIndex  = if (destinations(0) == "_") -1 else RegisterFile.index(destinations(0))
      val highIndex = if (destinations(1) == "_") -1 else RegisterFile.index(destinations(1))
      context.active.foreachLane { lane =>
        val value = readFast(context, lane, source)
        if (destinations(0) != "_")
          writeRegister(context.state.registers(lane), destinations(0), lowIndex, truncate(value, partWidth))
        if (destinations(1) != "_")
          writeRegister(
            context.state.registers(lane),
            destinations(1),
            highIndex,
            truncate(value >>> partWidth, partWidth)
          )
      }
      true
    case ExecutableInstruction.MoveCombine(destination, sources, partWidth) =>
      val destinationIndex = RegisterFile.index(destination)
      context.active.foreachLane { lane =>
        val low  = readFast(context, lane, sources(0))
        val high = readFast(context, lane, sources(1))
        writeRegister(
          context.state.registers(lane),
          destination,
          destinationIndex,
          truncate((low & truncate(-1L, partWidth)) | (truncate(high, partWidth) << partWidth), partWidth * 2)
        )
      }
      true
    case ExecutableInstruction.IntegerUnary(operation, destination, source, width) =>
      val destinationIndex = RegisterFile.index(destination)
      context.active.foreachLane { lane =>
        val value  = readFast(context, lane, source)
        val result = operation match {
          case IntegerUnaryOperation.Negate          => -value
          case IntegerUnaryOperation.Absolute        => Math.abs(signExtend(truncate(value, width), width))
          case IntegerUnaryOperation.BitwiseNot      => ~value
          case IntegerUnaryOperation.PopulationCount =>
            bitCount(value, width).toLong
          case IntegerUnaryOperation.LeadingZeroCount =>
            if (width == 32) Integer.numberOfLeadingZeros((value & 0xffffffffL).toInt).toLong
            else java.lang.Long.numberOfLeadingZeros(value).toLong
          case IntegerUnaryOperation.BitReverse => reverseBits(value, width)
        }
        writeRegister(context.state.registers(lane), destination, destinationIndex, truncate(result, width))
      }
      true
    case ExecutableInstruction.BitFind(destination, source, width, signed, shiftAmount) =>
      val destinationIndex = RegisterFile.index(destination)
      context.active.foreachLane { lane =>
        writeRegister(
          context.state.registers(lane),
          destination,
          destinationIndex,
          bitFind(readFast(context, lane, source), width, signed, shiftAmount)
        )
      }
      true
    case ExecutableInstruction.DotProduct4(destination, left, right, addend, leftSigned, rightSigned) =>
      val destinationIndex = RegisterFile.index(destination)
      context.active.foreachLane { lane =>
        writeRegister(
          context.state.registers(lane),
          destination,
          destinationIndex,
          dotProduct4(
            readFast(context, lane, left),
            readFast(context, lane, right),
            readFast(context, lane, addend),
            leftSigned,
            rightSigned
          )
        )
      }
      true
    case ExecutableInstruction.IntegerAddWithCarry(destination, left, right, width, carryIn, carryOut) =>
      val destinationIndex = RegisterFile.index(destination)
      context.active.foreachLane { lane =>
        val (result, nextCarry) = integerAddWithCarry(
          readFast(context, lane, left),
          readFast(context, lane, right),
          width,
          carryIn,
          context.state.carry(lane)
        )
        writeRegister(context.state.registers(lane), destination, destinationIndex, result)
        if (carryOut) context.state.carry(lane) = nextCarry
      }
      true
    case ExecutableInstruction.IntegerSubWithBorrow(destination, left, right, width, borrowIn, borrowOut) =>
      val destinationIndex = RegisterFile.index(destination)
      context.active.foreachLane { lane =>
        val (result, nextBorrow) = integerSubWithBorrow(
          readFast(context, lane, left),
          readFast(context, lane, right),
          width,
          borrowIn,
          context.state.carry(lane)
        )
        writeRegister(context.state.registers(lane), destination, destinationIndex, result)
        if (borrowOut) context.state.carry(lane) = nextBorrow
      }
      true
    case ExecutableInstruction.IntegerBinary(operation, destination, left, right, width) =>
      val destinationIndex = RegisterFile.index(destination)
      context.active.foreachLane { lane =>
        val leftValue  = readFast(context, lane, left)
        val rightValue = readFast(context, lane, right)
        val result     = operation match {
          case IntegerOperation.Add                    => leftValue + rightValue
          case IntegerOperation.Subtract               => leftValue - rightValue
          case IntegerOperation.BitwiseOr              => leftValue | rightValue
          case IntegerOperation.BitwiseAnd             => leftValue & rightValue
          case IntegerOperation.BitwiseXor             => leftValue ^ rightValue
          case IntegerOperation.MultiplyLow            => leftValue * rightValue
          case IntegerOperation.MultiplyHigh(signed)   => multiplyHigh(leftValue, rightValue, width, signed)
          case IntegerOperation.Divide(signed)         => divideInteger(leftValue, rightValue, width, signed)
          case IntegerOperation.Remainder(signed)      => remainderInteger(leftValue, rightValue, width, signed)
          case IntegerOperation.MultiplyWideUnsigned16 =>
            (leftValue & 0xffffL) * (rightValue & 0xffffL)
          case IntegerOperation.MultiplyWideUnsigned32 => low32(leftValue) * low32(rightValue)
          case IntegerOperation.MultiplyWideSigned32   => leftValue.toInt.toLong * rightValue.toInt.toLong
          case IntegerOperation.Minimum(signed)        =>
            if (signed) Math.min(signExtend(leftValue, width), signExtend(rightValue, width))
            else if (width == 64) {
              if (java.lang.Long.compareUnsigned(leftValue, rightValue) <= 0) leftValue else rightValue
            } else Math.min(truncate(leftValue, width), truncate(rightValue, width))
          case IntegerOperation.Maximum(signed) =>
            if (signed) Math.max(signExtend(leftValue, width), signExtend(rightValue, width))
            else if (width == 64) {
              if (java.lang.Long.compareUnsigned(leftValue, rightValue) >= 0) leftValue else rightValue
            } else Math.max(truncate(leftValue, width), truncate(rightValue, width))
          case IntegerOperation.ShiftLeft         => leftValue << (rightValue & (width - 1).toLong).toInt
          case IntegerOperation.ShiftRightLogical =>
            if (width == 32) (leftValue & 0xffffffffL) >>> (rightValue & 31L).toInt
            else leftValue >>> (rightValue & 63L).toInt
          case IntegerOperation.ShiftRightArithmetic =>
            if (width == 32) (leftValue.toInt >> (rightValue & 31L).toInt).toLong
            else leftValue >> (rightValue & 63L).toInt
        }
        writeRegister(context.state.registers(lane), destination, destinationIndex, truncate(result, width))
      }
      true
    case ExecutableInstruction.Convert(destination, source, sourceType, destinationType, rounding) =>
      val destinationIndex = RegisterFile.index(destination)
      context.active.foreachLane(lane =>
        writeRegister(
          context.state.registers(lane),
          destination,
          destinationIndex,
          convert(readFast(context, lane, source), sourceType, destinationType, rounding)
        )
      )
      true
    case ExecutableInstruction.MultiplyAdd(destination, left, right, addend, operation) =>
      val destinationIndex = RegisterFile.index(destination)
      context.active.foreachLane { lane =>
        val (result, carry) = integerMultiplyAdd(
          readFast(context, lane, left),
          readFast(context, lane, right),
          readFast(context, lane, addend),
          operation,
          context.state.carry(lane)
        )
        writeRegister(context.state.registers(lane), destination, destinationIndex, result)
        operation match {
          case IntegerMultiplyAddOperation.Low(_, _, _, true) | IntegerMultiplyAddOperation.High(_, _, _, true) =>
            context.state.carry(lane) = carry
          case _ => ()
        }
      }
      true
    case ExecutableInstruction.BitFieldInsert(destination, insertValue, baseValue, bitPosition, bitLength, width) =>
      val destinationIndex = RegisterFile.index(destination)
      context.active.foreachLane { lane =>
        writeRegister(
          context.state.registers(lane),
          destination,
          destinationIndex,
          insertBits(
            readFast(context, lane, insertValue),
            readFast(context, lane, baseValue),
            readFast(context, lane, bitPosition),
            readFast(context, lane, bitLength),
            width
          )
        )
      }
      true
    case ExecutableInstruction.BitMask(destination, start, widthValue, mode) =>
      val destinationIndex = RegisterFile.index(destination)
      context.active.foreachLane { lane =>
        writeRegister(
          context.state.registers(lane),
          destination,
          destinationIndex,
          bitMask(readFast(context, lane, start), readFast(context, lane, widthValue), mode)
        )
      }
      true
    case ExecutableInstruction.BytePermute(destination, left, right, control, mode) =>
      val destinationIndex = RegisterFile.index(destination)
      context.active.foreachLane { lane =>
        writeRegister(
          context.state.registers(lane),
          destination,
          destinationIndex,
          permuteBytes(
            readFast(context, lane, left),
            readFast(context, lane, right),
            readFast(context, lane, control),
            mode
          )
        )
      }
      true
    case ExecutableInstruction.FloatBinary(operation, destination, left, right, width) =>
      val destinationIndex = RegisterFile.index(destination)
      context.active.foreachLane { lane =>
        val leftBits  = readFast(context, lane, left)
        val rightBits = readFast(context, lane, right)
        val result    = if (width == 16) {
          val value = operation match {
            case FloatOperation.Add      => IEEE754.binary16ToFloat(leftBits) + IEEE754.binary16ToFloat(rightBits)
            case FloatOperation.Subtract => IEEE754.binary16ToFloat(leftBits) - IEEE754.binary16ToFloat(rightBits)
            case FloatOperation.Multiply => IEEE754.binary16ToFloat(leftBits) * IEEE754.binary16ToFloat(rightBits)
            case FloatOperation.Divide   => IEEE754.binary16ToFloat(leftBits) / IEEE754.binary16ToFloat(rightBits)
            case FloatOperation.Minimum  =>
              Math.min(IEEE754.binary16ToFloat(leftBits), IEEE754.binary16ToFloat(rightBits))
            case FloatOperation.Maximum =>
              Math.max(IEEE754.binary16ToFloat(leftBits), IEEE754.binary16ToFloat(rightBits))
            case FloatOperation.CopySign =>
              Math.copySign(IEEE754.binary16ToFloat(rightBits), IEEE754.binary16ToFloat(leftBits))
          }
          IEEE754.floatToBinary16(value)
        } else if (width == 32) {
          val value = operation match {
            case FloatOperation.Add      => IEEE754.binary32ToFloat(leftBits) + IEEE754.binary32ToFloat(rightBits)
            case FloatOperation.Subtract => IEEE754.binary32ToFloat(leftBits) - IEEE754.binary32ToFloat(rightBits)
            case FloatOperation.Multiply => IEEE754.binary32ToFloat(leftBits) * IEEE754.binary32ToFloat(rightBits)
            case FloatOperation.Divide   => IEEE754.binary32ToFloat(leftBits) / IEEE754.binary32ToFloat(rightBits)
            case FloatOperation.Minimum  =>
              Math.min(IEEE754.binary32ToFloat(leftBits), IEEE754.binary32ToFloat(rightBits))
            case FloatOperation.Maximum =>
              Math.max(IEEE754.binary32ToFloat(leftBits), IEEE754.binary32ToFloat(rightBits))
            case FloatOperation.CopySign =>
              Math.copySign(IEEE754.binary32ToFloat(rightBits), IEEE754.binary32ToFloat(leftBits))
          }
          IEEE754.floatToBinary32(value)
        } else {
          val value = operation match {
            case FloatOperation.Add      => IEEE754.binary64ToDouble(leftBits) + IEEE754.binary64ToDouble(rightBits)
            case FloatOperation.Subtract => IEEE754.binary64ToDouble(leftBits) - IEEE754.binary64ToDouble(rightBits)
            case FloatOperation.Multiply => IEEE754.binary64ToDouble(leftBits) * IEEE754.binary64ToDouble(rightBits)
            case FloatOperation.Divide   => IEEE754.binary64ToDouble(leftBits) / IEEE754.binary64ToDouble(rightBits)
            case FloatOperation.Minimum  =>
              Math.min(IEEE754.binary64ToDouble(leftBits), IEEE754.binary64ToDouble(rightBits))
            case FloatOperation.Maximum =>
              Math.max(IEEE754.binary64ToDouble(leftBits), IEEE754.binary64ToDouble(rightBits))
            case FloatOperation.CopySign =>
              Math.copySign(IEEE754.binary64ToDouble(rightBits), IEEE754.binary64ToDouble(leftBits))
          }
          IEEE754.doubleToBinary64(value)
        }
        writeRegister(context.state.registers(lane), destination, destinationIndex, result)
      }
      true
    case ExecutableInstruction.FloatBinaryPacked(operation, destination, left, right, bf16) =>
      val destinationIndex = RegisterFile.index(destination)
      context.active.foreachLane { lane =>
        writeRegister(
          context.state.registers(lane),
          destination,
          destinationIndex,
          packedBinaryBits(operation, readFast(context, lane, left), readFast(context, lane, right), bf16)
        )
      }
      true
    case ExecutableInstruction.FloatUnary(operation, destination, source, width) =>
      val destinationIndex = RegisterFile.index(destination)
      context.active.foreachLane { lane =>
        val sourceValue = readFast(context, lane, source)
        val result      = floatUnaryBits(operation, sourceValue, width)
        writeRegister(context.state.registers(lane), destination, destinationIndex, result)
      }
      true
    case ExecutableInstruction.FusedMultiplyAdd(destination, left, right, addend, width) =>
      val destinationIndex = RegisterFile.index(destination)
      context.active.foreachLane { lane =>
        val leftBits   = readFast(context, lane, left)
        val rightBits  = readFast(context, lane, right)
        val addendBits = readFast(context, lane, addend)
        val result     = if (width == 16)
          IEEE754.floatToBinary16(
            Math.fma(
              IEEE754.binary16ToFloat(leftBits),
              IEEE754.binary16ToFloat(rightBits),
              IEEE754.binary16ToFloat(addendBits)
            )
          )
        else if (width == 32)
          IEEE754.floatToBinary32(
            Math.fma(
              IEEE754.binary32ToFloat(leftBits),
              IEEE754.binary32ToFloat(rightBits),
              IEEE754.binary32ToFloat(addendBits)
            )
          )
        else
          IEEE754.doubleToBinary64(
            Math.fma(
              IEEE754.binary64ToDouble(leftBits),
              IEEE754.binary64ToDouble(rightBits),
              IEEE754.binary64ToDouble(addendBits)
            )
          )
        writeRegister(context.state.registers(lane), destination, destinationIndex, result)
      }
      true
    case ExecutableInstruction.FusedMultiplyAddPacked(destination, left, right, addend, bf16) =>
      val destinationIndex = RegisterFile.index(destination)
      context.active.foreachLane { lane =>
        writeRegister(
          context.state.registers(lane),
          destination,
          destinationIndex,
          packedFmaBits(
            readFast(context, lane, left),
            readFast(context, lane, right),
            readFast(context, lane, addend),
            bf16
          )
        )
      }
      true
    case ExecutableInstruction.FusedMultiplyAddBf16(destination, left, right, addend) =>
      val destinationIndex = RegisterFile.index(destination)
      context.active.foreachLane { lane =>
        writeRegister(
          context.state.registers(lane),
          destination,
          destinationIndex,
          bf16FmaBits(
            readFast(context, lane, left),
            readFast(context, lane, right),
            readFast(context, lane, addend)
          )
        )
      }
      true
    case ExecutableInstruction.SetPredicate(destination, comparison, kind, left, right, width) =>
      val destinationIndex = PredicateFile.index(destination)
      context.active.foreachLane { lane =>
        val value = compare(
          readFast(context, lane, left),
          readFast(context, lane, right),
          comparison,
          kind,
          width
        )
        writePredicate(context.state.predicates(lane), destination, destinationIndex, value)
      }
      true
    case ExecutableInstruction.PredicateCompare(destination, comparison, left, right) =>
      val destinationIndex = PredicateFile.index(destination)
      val leftIndex        = PredicateFile.index(left)
      val rightIndex       = PredicateFile.index(right)
      context.active.foreachLane { lane =>
        val leftValue  = readPredicateFast(context.state.predicates(lane), left, leftIndex)
        val rightValue = readPredicateFast(context.state.predicates(lane), right, rightIndex)
        writePredicate(
          context.state.predicates(lane),
          destination,
          destinationIndex,
          comparePredicates(leftValue, rightValue, comparison)
        )
      }
      true
    case ExecutableInstruction.PredicateUnary(destination, source) =>
      val destinationIndex = PredicateFile.index(destination)
      val sourceIndex      = PredicateFile.index(source)
      context.active.foreachLane { lane =>
        writePredicate(
          context.state.predicates(lane),
          destination,
          destinationIndex,
          !readPredicateFast(context.state.predicates(lane), source, sourceIndex)
        )
      }
      true
    case ExecutableInstruction.PredicateBinary(operation, destination, left, right) =>
      val destinationIndex = PredicateFile.index(destination)
      val leftIndex        = PredicateFile.index(left)
      val rightIndex       = PredicateFile.index(right)
      context.active.foreachLane { lane =>
        val leftValue  = readPredicateFast(context.state.predicates(lane), left, leftIndex)
        val rightValue = readPredicateFast(context.state.predicates(lane), right, rightIndex)
        writePredicate(
          context.state.predicates(lane),
          destination,
          destinationIndex,
          operation match {
            case PredicateOperation.And => leftValue && rightValue
            case PredicateOperation.Or  => leftValue || rightValue
            case PredicateOperation.Xor => leftValue ^ rightValue
          }
        )
      }
      true
    case ExecutableInstruction.Select(destination, whenTrue, whenFalse, predicate, width) =>
      val destinationIndex = RegisterFile.index(destination)
      val predicateIndex   = PredicateFile.index(predicate)
      context.active.foreachLane { lane =>
        val selected = if (readPredicateFast(context.state.predicates(lane), predicate, predicateIndex))
          readFast(context, lane, whenTrue)
        else readFast(context, lane, whenFalse)
        writeRegister(context.state.registers(lane), destination, destinationIndex, truncate(selected, width))
      }
      true
    case _ => false
  }

  private def writeRegister(registers: RegisterFile, name: String, index: Int, value: Long): Unit =
    if (index >= 0) registers.updateSlot(index, value) else registers.update(name, value)

  private def writePredicate(predicates: PredicateFile, name: String, index: Int, value: Boolean): Unit =
    if (index >= 0) predicates.updateSlot(index, value) else predicates.update(name, value)

  private def readPredicateFast(predicates: PredicateFile, name: String, index: Int): Boolean =
    if (index >= 0) predicates.getSlot(index).getOrElse(false) else predicates.get(name).getOrElse(false)

  private def executeBuiltin(
      context: ExecutionContext,
      lane: Int,
      name: String,
      returnParameters: Col[String],
      parameters: Col[String]
  ): Option[Either[ExecutionFault, Unit]] = {
    def parameter(index: Int, width: Int = 8): Long =
      if (index < parameters.size)
        context.state.parameters(lane).getOrElse(
          parameters(index),
          context.state.readParameterBytes(lane, parameters(index), 0, width)
        )
      else 0L

    def returnValue(value: Long): Unit =
      returnParameters.headOption.foreach(name => context.state.parameters(lane)(name) = value)

    def writeLocalPointer(index: Int, value: Long): Unit =
      if (index < parameters.size) context.state.writeLocal(lane, parameter(index), 32, value)

    name match {
      case "cudaGetDevice" =>
        writeLocalPointer(0, 0L)
        returnValue(0L)
        Some(Right(()))
      case "cudaDeviceGetAttribute" =>
        val attribute = parameter(1, 4).toInt
        writeLocalPointer(0, deviceAttribute(attribute).toLong)
        returnValue(0L)
        Some(Right(()))
      case "cudaOccupancyMaxActiveBlocksPerMultiprocessor" |
          "cudaOccupancyMaxActiveBlocksPerMultiprocessorWithFlags" |
          "__cuda_syscall_cnpMaxActiveBlocksPerMultiprocessorWithFlags" =>
        writeLocalPointer(0, 1L)
        returnValue(0L)
        Some(Right(()))
      case "cudaGetLastError" | "cudaPeekAtLastError" | "__cudaDeviceSynchronizeDeprecationAvoidance" =>
        returnValue(0L)
        Some(Right(()))
      case "cudaGetErrorName" | "cudaGetErrorString" | "vprintf" =>
        returnValue(0L)
        Some(Right(()))
      case "cudaGetParameterBuffer" =>
        // The first argument is the requested alignment; the second is the
        // number of bytes needed for the child kernel's argument packet.
        val size   = Math.max(1L, parameter(1)).min(Int.MaxValue.toLong).toInt
        val buffer = context.state.allocateParameterBuffer(size)
        returnValue(buffer)
        Some(Right(()))
      case "malloc" =>
        context.memory match {
          case memory: DeviceAllocationMemory =>
            returnValue(memory.allocate(parameter(0)))
            Some(Right(()))
          case _ => Some(Left(ExecutionFault.InvalidState("device malloc requires dynamic address memory")))
        }
      case "free" =>
        context.memory match {
          case memory: DeviceAllocationMemory =>
            memory.free(parameter(0))
            Some(Right(()))
          case _ => Some(Left(ExecutionFault.InvalidState("device free requires dynamic address memory")))
        }
      case "cudaLaunchDevice" =>
        Some(executeDeviceLaunch(context, lane, returnParameters, parameters, parameter))
      case _ => None
    }
  }

  private def deviceAttribute(attribute: Int): Int = attribute match {
    case 1 | 2 | 3 | 39 => 1024
    case 4              => 64
    case 5              => Int.MaxValue
    case 6 | 7          => 65535
    case 8              => 49152
    case 10             => 32
    case 16             => 1
    case 41             => 1
    case 75             => 7
    case _              => 0
  }

  private def executeDeviceLaunch(
      context: ExecutionContext,
      lane: Int,
      returnParameters: Col[String],
      parameters: Col[String],
      parameter: (Int, Int) => Long
  ): Either[ExecutionFault, Unit] =
    if (parameters.size < 6)
      Left(ExecutionFault.InvalidState(s"cudaLaunchDevice expects six parameters, found ${parameters.size}"))
    else {
      val target = context.state.functionParameters(lane).get(parameters(0))
      val child  = target.flatMap(context.kernel.functions.get).toRight(
        ExecutionFault.InvalidState(s"cudaLaunchDevice target is not a lowered entry: ${target.getOrElse("<unknown>")}")
      )
      child.flatMap { function =>
        val buffer = parameter(1, 8)
        val grid   =
          (0 until 3).map(index => context.state.readParameterBytes(lane, parameters(2), index * 4, 4) & 0xffffffffL)
        val block =
          (0 until 3).map(index => context.state.readParameterBytes(lane, parameters(3), index * 4, 4) & 0xffffffffL)
        val childArguments                          = mutable.Map.empty[String, Argument]
        var argumentFailure: Option[ExecutionFault] = None
        var index                                   = 0
        while (index < function.parameters.size && argumentFailure.isEmpty) {
          val width  = function.parameterWidths(index)
          val offset = function.parameterOffsets(index)
          context.state.readParameterBufferBytes(buffer + offset.toLong, width) match {
            case Some(bytes) => childArguments(function.parameters(index)) = Argument(Col.unsafeFromArray(bytes))
            case None        => argumentFailure = Some(ExecutionFault.InvalidState(
                s"cudaLaunchDevice parameter buffer 0x${java.lang.Long.toUnsignedString(buffer, 16)} is out of range"
              ))
          }
          index += 1
        }
        argumentFailure.toLeft {
          val geometry = for {
            groupCount <- Dimensions.create(grid(0).toInt, grid(1).toInt, grid(2).toInt)
            groupSize  <- Dimensions.create(block(0).toInt, block(1).toInt, block(2).toInt)
            value      <- LaunchGeometry.create(groupSize, groupCount)
          } yield value
          geometry.leftMap(error => ExecutionFault.InvalidState(error)).flatMap { value =>
            val result = Interpreter.execute(
              Kernel(
                function.name,
                function.parameters.zip(function.parameterWidths).map { case (name, width) =>
                  KernelParameter(name, width)
                },
                function.program,
                context.kernel.functions,
                context.kernel.sharedSymbols ++ function.sharedSymbols,
                context.kernel.globalSymbols
              ),
              value,
              childArguments.toMap,
              context.memory,
              context.control,
              captureTrace = false,
              initialParameterBuffers = context.state.parameterBuffers.toMap
            )
            result.leftMap { error =>
              ExecutionFault.InvalidState(error.message)
            }
          }
        }
      }.map(_ => returnParameters.headOption.foreach(name => context.state.parameters(lane)(name) = 0L))
    }

  private def readFast(context: ExecutionContext, lane: Int, value: Value): Long = value match {
    case Value.Register(name)       => context.state.registers(lane)(name)
    case Value.RegisterSlot(index)  => context.state.registers(lane).getSlot(index)
    case Value.Predicate(name)      => if (context.state.predicates(lane).get(name).getOrElse(false)) 1L else 0L
    case Value.PredicateSlot(index) => if (context.state.predicates(lane).getSlot(index).getOrElse(false)) 1L else 0L
    case Value.Symbol(name)         =>
      context.state.parameterAddresses.get(name)
        .orElse(context.kernel.sharedSymbols.get(name))
        .orElse(context.kernel.globalSymbols.get(name))
        .getOrElse(0L)
    case Value.Immediate(immediate)  => immediate
    case Value.Address(base, offset) => readFast(context, lane, base) + offset
    case Value.LaneId                => (lane & 31).toLong
    case Value.ActiveMask            => context.active.warpBits(lane)
    case Value.LaneMaskGreaterEqual  => (0xffffffffL << (lane & 31)) & 0xffffffffL
    case Value.LaneMaskGreater       => (0xffffffffL << ((lane & 31) + 1)) & 0xffffffffL
    case Value.LaneMaskLessEqual     => (1L << ((lane & 31) + 1)) - 1L
    case Value.LaneMaskLess          => (1L << (lane & 31)) - 1L
    case Value.LaneMaskEqual         => 1L << (lane & 31)
    case Value.ThreadId(axis)        => axis.select(context.geometry.localCoordinates(lane)).toLong
    case Value.BlockId(axis)         => axis.select(context.group).toLong
    case Value.BlockDim(axis)        => axis.select(context.geometry.groupSize).toLong
    case Value.GridDim(axis)         => axis.select(context.geometry.groupCount).toLong
    case Value.Function(name)        => throw IllegalStateException(s"function symbol $name is not numeric")
  }

  private def executeFunction(
      caller: ExecutionContext,
      callerLane: Int,
      function: DeviceFunction,
      parameters: Col[String],
      returnParameters: Col[String]
  ): Either[ExecutionFault, Unit] = {
    val oneLane = LaneMask.all(1).toOption.getOrElse(LaneMask.Empty)
    val state   = BlockState(1, oneLane)
    val input   = if (parameters.size != function.parameters.size) {
      Left(ExecutionFault.InvalidState(
        s"device function ${function.name} expects ${function.parameters.size} parameters, found ${parameters.size}"
      ))
    } else {
      parameters.zip(function.parameters).foldLeft[Either[ExecutionFault, Unit]](Right(())) {
        case (current, (callerName, functionName)) =>
          current.flatMap { _ =>
            caller.state.parameters(callerLane).get(callerName).toRight(
              ExecutionFault.InvalidState(s"missing call parameter $callerName")
            ).map(value => state.parameters(0)(functionName) = value)
          }
      }
    }
    input match {
      case Left(fault) => Left(fault)
      case Right(())   =>
        var pc      = Pc.Entry
        var running = true
        val effects = Effects[StateWrite]
        while (running && pc.value < function.program.instructions.length) {
          caller.control.checkpoint() match {
            case Left(fault) => return Left(fault)
            case Right(())   => ()
          }
          val predicated = function.program.instructions(pc.value)
          val active     = predicated.predication match {
            case None            => oneLane
            case Some(predicate) =>
              state.predicates(0).get(predicate.name) match {
                case Some(value) if value != predicate.negated => oneLane
                case Some(_)                                   => LaneMask.Empty
                case None => return Left(ExecutionFault.UndefinedRegister(predicate.name, pc, 0))
              }
          }
          val functionContext = ExecutionContext(
            caller.kernel.copy(sharedSymbols = caller.kernel.sharedSymbols ++ function.sharedSymbols),
            caller.block,
            caller.group,
            caller.geometry,
            state,
            pc,
            active,
            Map.empty,
            caller.memory,
            caller.control
          )
          effects.clear()
          val outcome =
            if (active.isEmpty) Right(Step.Advance)
            else predicated.instruction.execute(functionContext, effects)
          outcome match {
            case Left(fault) => return Left(fault)
            case Right(step) =>
              effects.commit(
                StateEffects.validateWrite(caller.memory, pc),
                StateEffects.applyWrite(state, caller.memory)
              ) match {
                case Left(fault) => return Left(fault)
                case Right(())   => ()
              }
              step match {
                case Step.Advance =>
                  pc.next match {
                    case Right(next) => pc = next
                    case Left(_)     => return Left(ExecutionFault.InvalidProgram("function pc overflow"))
                  }
                case Step.Exit(_)        => running = false
                case Step.Jump(target)   => pc = target
                case Step.Split(_, _, _) =>
                  return Left(
                    ExecutionFault.InvalidState(s"unsupported control split in device function ${function.name}")
                  )
                case Step.Suspend(_) =>
                  return Left(ExecutionFault.InvalidState(s"unsupported barrier in device function ${function.name}"))
                case Step.Trap(fault) => return Left(fault)
              }
          }
        }
        if (returnParameters.size != function.returnParameters.size) {
          return Left(ExecutionFault.InvalidState(
            s"device function ${function.name} returns ${function.returnParameters.size} parameters, found ${returnParameters.size}"
          ))
        }
        returnParameters.zip(function.returnParameters).foreach { case (callerName, functionName) =>
          state.parameters(0).get(functionName).foreach { value =>
            caller.state.parameters(callerLane)(callerName) = value
          }
        }
        Right(())
    }
  }

  private def forEveryLane(context: ExecutionContext)(
      function: Int => Either[ExecutionFault, Unit]
  ): Either[ExecutionFault, Unit] = {
    var failure: Option[ExecutionFault] = None
    context.active.foreachLane { lane =>
      if (failure.isEmpty) function(lane).left.foreach(fault => failure = Some(fault))
    }
    failure.toLeft(())
  }

  // The emulator keeps per-lane local storage in the low virtual-address
  // range; host-visible CUDA allocations are projected above 4 GiB.
  private def isLocalAddress(address: Long): Boolean =
    java.lang.Long.compareUnsigned(address, 0x100000000L) < 0 && !isSharedAddress(address)

  private def isSharedAddress(address: Long): Boolean =
    java.lang.Long.compareUnsigned(address, 0x10000000L) >= 0 &&
      java.lang.Long.compareUnsigned(address, 0x40000000L) < 0

  private def floatUnaryBits(operation: FloatUnaryOperation, bits: Long, width: Int): Long =
    if (width == 16) {
      val halfBits = truncate(bits, 16)
      operation match {
        case FloatUnaryOperation.Negate   => halfBits ^ 0x8000L
        case FloatUnaryOperation.Absolute => halfBits & 0x7fffL
        case _                            =>
          val value  = IEEE754.binary16ToFloat(halfBits)
          val result = operation match {
            case FloatUnaryOperation.Negate               => -value
            case FloatUnaryOperation.Absolute             => Math.abs(value)
            case FloatUnaryOperation.Reciprocal           => 1.0f / value
            case FloatUnaryOperation.SquareRoot           => Math.sqrt(value.toDouble).toFloat
            case FloatUnaryOperation.ReciprocalSquareRoot => (1.0 / Math.sqrt(value.toDouble)).toFloat
            case FloatUnaryOperation.Exponent2            => Math.pow(2.0, value.toDouble).toFloat
            case FloatUnaryOperation.Log2                 => (Math.log(value.toDouble) / Math.log(2.0)).toFloat
            case FloatUnaryOperation.Sine                 => Math.sin(value.toDouble).toFloat
            case FloatUnaryOperation.Cosine               => Math.cos(value.toDouble).toFloat
            case FloatUnaryOperation.Tanh                 => Math.tanh(value.toDouble).toFloat
          }
          IEEE754.floatToBinary16(result)
      }
    } else if (width == 32) {
      val value  = IEEE754.binary32ToFloat(bits)
      val result = operation match {
        case FloatUnaryOperation.Negate               => return bits ^ (1L << 31)
        case FloatUnaryOperation.Absolute             => Math.abs(value)
        case FloatUnaryOperation.Reciprocal           => 1.0f / value
        case FloatUnaryOperation.SquareRoot           => Math.sqrt(value.toDouble).toFloat
        case FloatUnaryOperation.ReciprocalSquareRoot => (1.0 / Math.sqrt(value.toDouble)).toFloat
        case FloatUnaryOperation.Exponent2            => Math.pow(2.0, value.toDouble).toFloat
        case FloatUnaryOperation.Log2                 => (Math.log(value.toDouble) / Math.log(2.0)).toFloat
        case FloatUnaryOperation.Sine                 => Math.sin(value.toDouble).toFloat
        case FloatUnaryOperation.Cosine               => Math.cos(value.toDouble).toFloat
        case FloatUnaryOperation.Tanh                 => Math.tanh(value.toDouble).toFloat
      }
      IEEE754.floatToBinary32(result)
    } else {
      val value  = IEEE754.binary64ToDouble(bits)
      val result = operation match {
        case FloatUnaryOperation.Negate               => return bits ^ (1L << 63)
        case FloatUnaryOperation.Absolute             => Math.abs(value)
        case FloatUnaryOperation.Reciprocal           => 1.0 / value
        case FloatUnaryOperation.SquareRoot           => Math.sqrt(value)
        case FloatUnaryOperation.ReciprocalSquareRoot => 1.0 / Math.sqrt(value)
        case FloatUnaryOperation.Exponent2            => Math.pow(2.0, value)
        case FloatUnaryOperation.Log2                 => Math.log(value) / Math.log(2.0)
        case FloatUnaryOperation.Sine                 => Math.sin(value)
        case FloatUnaryOperation.Cosine               => Math.cos(value)
        case FloatUnaryOperation.Tanh                 => Math.tanh(value)
      }
      IEEE754.doubleToBinary64(result)
    }

  private def packedFloatValue(bits: Long, bf16: Boolean): Float =
    if (bf16) IEEE754.binary32ToFloat((truncate(bits, 16) << 16) & 0xffffffffL)
    else IEEE754.binary16ToFloat(bits)

  private def packedFloatBits(value: Float, bf16: Boolean): Long =
    if (bf16) bfloat16FromFloat(value, Some("rn")) else IEEE754.floatToBinary16(value)

  private def packedFmaBits(left: Long, right: Long, addend: Long, bf16: Boolean): Long = {
    val low = packedFloatBits(
      Math.fma(
        packedFloatValue(left, bf16),
        packedFloatValue(right, bf16),
        packedFloatValue(addend, bf16)
      ),
      bf16
    )
    val high = packedFloatBits(
      Math.fma(
        packedFloatValue(left >>> 16, bf16),
        packedFloatValue(right >>> 16, bf16),
        packedFloatValue(addend >>> 16, bf16)
      ),
      bf16
    )
    low | (high << 16)
  }

  private def packedBinaryBits(operation: FloatOperation, left: Long, right: Long, bf16: Boolean): Long = {
    def applyOperation(leftValue: Float, rightValue: Float): Float = operation match {
      case FloatOperation.Add      => leftValue + rightValue
      case FloatOperation.Subtract => leftValue - rightValue
      case FloatOperation.Multiply => leftValue * rightValue
      case FloatOperation.Divide   => leftValue / rightValue
      case FloatOperation.Minimum  => Math.min(leftValue, rightValue)
      case FloatOperation.Maximum  => Math.max(leftValue, rightValue)
      case FloatOperation.CopySign => Math.copySign(rightValue, leftValue)
    }
    val low  = packedFloatBits(applyOperation(packedFloatValue(left, bf16), packedFloatValue(right, bf16)), bf16)
    val high = packedFloatBits(
      applyOperation(packedFloatValue(left >>> 16, bf16), packedFloatValue(right >>> 16, bf16)),
      bf16
    )
    low | (high << 16)
  }

  private def bf16FmaBits(left: Long, right: Long, addend: Long): Long =
    bfloat16FromFloat(
      Math.fma(
        IEEE754.binary32ToFloat((truncate(left, 16) << 16) & 0xffffffffL),
        IEEE754.binary32ToFloat((truncate(right, 16) << 16) & 0xffffffffL),
        IEEE754.binary32ToFloat((truncate(addend, 16) << 16) & 0xffffffffL)
      ),
      Some("rn")
    )

  private def loadBits(value: Long, width: Int, signed: Boolean): Long = {
    val bits = truncate(value, width)
    if (signed) signExtend(bits, width) else bits
  }

  private def bitFind(value: Long, width: Int, signed: Boolean, shiftAmount: Boolean): Long = {
    val bits   = truncate(value, width)
    val search =
      if (signed && ((bits >>> (width - 1)) & 1L) != 0L) truncate(~bits, width)
      else bits
    val position =
      if (search == 0L) -1
      else if (width == 32) 31 - Integer.numberOfLeadingZeros(search.toInt)
      else 63 - java.lang.Long.numberOfLeadingZeros(search)
    if (position < 0) 0xffffffffL
    else if (shiftAmount) (width - 1 - position).toLong
    else position.toLong
  }

  private def dotProduct4(
      left: Long,
      right: Long,
      addend: Long,
      leftSigned: Boolean,
      rightSigned: Boolean
  ): Long = {
    var result = signExtend(truncate(addend, 32), 32)
    var index  = 0
    while (index < 4) {
      val leftByte   = (left >>> (index * 8)) & 0xffL
      val rightByte  = (right >>> (index * 8)) & 0xffL
      val leftValue  = if (leftSigned && leftByte >= 0x80L) leftByte - 0x100L else leftByte
      val rightValue = if (rightSigned && rightByte >= 0x80L) rightByte - 0x100L else rightByte
      result += leftValue * rightValue
      index += 1
    }
    truncate(result, 32)
  }

  private def shuffleClamp(value: Long): Int =
    Math.min(value & 0xffffffffL, 31L).toInt

  private def extractBits(source: Long, start: Long, count: Long, width: Int, signed: Boolean): Long = {
    val sourceBits = truncate(source, width)
    val bitStart   = (start & 0xffL).toInt
    val bitCount   = (count & 0xffL).toInt
    if (bitStart >= width || bitCount == 0) 0L
    else {
      val extractedWidth = Math.min(bitCount, width - bitStart)
      val mask           = if (extractedWidth == 64) -1L else (1L << extractedWidth) - 1L
      val extracted      = (sourceBits >>> bitStart) & mask
      truncate(if (signed) signExtend(extracted, extractedWidth) else extracted, width)
    }
  }

  private def bitMask(start: Long, count: Long, mode: String): Long = {
    val rawStart         = start & 0xffffffffL
    val rawCount         = count & 0xffffffffL
    val start1           = rawStart & 0x1fL
    val count1           = rawCount & 0x1fL
    val positionOverflow = mode == "clamp" && rawStart >= 32L
    val widthOverflow    = mode == "clamp" && rawCount >= 32L
    val sum              = start1 + count1
    val mask0            = if (positionOverflow) 0L else truncate(-1L << start1.toInt, 32)
    val mask1            =
      if (sum >= 32L || positionOverflow || widthOverflow) 0L
      else if (count1 == 0L) 0xffffffffL
      else truncate(-1L << sum.toInt, 32)
    truncate(mask0 & ~mask1, 32)
  }

  private def multiplyHigh(left: Long, right: Long, width: Int, signed: Boolean): Long = {
    val leftValue =
      if (signed) BigInt(signExtend(left, width)) else BigInt(java.lang.Long.toUnsignedString(truncate(left, width)))
    val rightValue =
      if (signed) BigInt(signExtend(right, width)) else BigInt(java.lang.Long.toUnsignedString(truncate(right, width)))
    ((leftValue * rightValue) >> width).longValue
  }

  private def integerMultiplyAdd(
      left: Long,
      right: Long,
      addend: Long,
      operation: IntegerMultiplyAddOperation,
      carryIn: Boolean
  ): (Long, Boolean) = operation match {
    case IntegerMultiplyAddOperation.Low(operandWidth, resultWidth, usesCarry, _) =>
      val leftValue   = BigInt(java.lang.Long.toUnsignedString(truncate(left, operandWidth)))
      val rightValue  = BigInt(java.lang.Long.toUnsignedString(truncate(right, operandWidth)))
      val addendValue = BigInt(java.lang.Long.toUnsignedString(truncate(addend, resultWidth)))
      val sum         = leftValue * rightValue + addendValue + (if (usesCarry && carryIn) 1 else 0)
      (truncate(sum.longValue, resultWidth), (sum >> resultWidth) != 0)
    case IntegerMultiplyAddOperation.High(operandWidth, resultWidth, usesCarry, _) =>
      val leftValue   = BigInt(java.lang.Long.toUnsignedString(truncate(left, operandWidth)))
      val rightValue  = BigInt(java.lang.Long.toUnsignedString(truncate(right, operandWidth)))
      val addendValue = BigInt(java.lang.Long.toUnsignedString(truncate(addend, resultWidth)))
      val sum         = (leftValue * rightValue >> operandWidth) + addendValue + (if (usesCarry && carryIn) 1 else 0)
      (truncate(sum.longValue, resultWidth), (sum >> resultWidth) != 0)
  }

  private def integerAddWithCarry(
      left: Long,
      right: Long,
      width: Int,
      usesCarry: Boolean,
      carryIn: Boolean
  ): (Long, Boolean) = {
    val sum =
      BigInt(java.lang.Long.toUnsignedString(truncate(left, width))) +
        BigInt(java.lang.Long.toUnsignedString(truncate(right, width))) +
        (if (usesCarry && carryIn) 1 else 0)
    (truncate(sum.longValue, width), (sum >> width) != 0)
  }

  private def integerSubWithBorrow(
      left: Long,
      right: Long,
      width: Int,
      usesBorrow: Boolean,
      borrowIn: Boolean
  ): (Long, Boolean) = {
    val difference =
      BigInt(java.lang.Long.toUnsignedString(truncate(left, width))) -
        BigInt(java.lang.Long.toUnsignedString(truncate(right, width))) -
        (if (usesBorrow && borrowIn) 1 else 0)
    (truncate(difference.longValue, width), difference < 0)
  }

  private def insertBits(insert: Long, base: Long, start: Long, count: Long, width: Int): Long = {
    val bitStart = (start & 0xffL).toInt
    val bitCount = (count & 0xffL).toInt
    if (bitCount == 0 || bitStart >= width) truncate(base, width)
    else {
      val insertedWidth = Math.min(bitCount, width - bitStart)
      val fieldMask     = if (insertedWidth == 64) -1L else ((1L << insertedWidth) - 1L) << bitStart
      truncate((base & ~fieldMask) | ((insert & truncate(-1L, insertedWidth)) << bitStart), width)
    }
  }

  private def permuteBytes(left: Long, right: Long, control: Long, mode: String): Long = {
    val source = Array.tabulate(8) { index =>
      val value = if (index < 4) left else right
      ((value >>> (8 * (index & 3))) & 0xffL).toInt
    }
    val selector      = (control & 3L).toInt
    val selectedBytes = mode match {
      case ""     => Array.tabulate(4)(index => ((control >>> (4 * index)) & 7L).toInt)
      case "f4e"  => Array.tabulate(4)(index => (selector + index) & 7)
      case "b4e"  => Array.tabulate(4)(index => (selector - index) & 7)
      case "rc8"  => Array.fill(4)(selector)
      case "ecl"  => Array.tabulate(4)(index => Math.max(index, selector))
      case "ecr"  => Array.tabulate(4)(index => Math.min(index, selector))
      case "rc16" => Array.tabulate(4)(index => ((selector & 1) * 2) + (index & 1))
      case other  => throw new IllegalArgumentException(s"unsupported prmt mode: $other")
    }
    val result = (0 until 4).foldLeft(0L) { (bits, index) =>
      val selection  = selectedBytes(index)
      val selected   = source(selection)
      val signExtend = mode.isEmpty && ((control >>> (4 * index)) & 8L) != 0L
      val byte       = if (signExtend && (selected & 0x80) != 0) 0xff else selected
      bits | (byte.toLong << (8 * index))
    }
    truncate(result, 32)
  }

  private def divideInteger(left: Long, right: Long, width: Int, signed: Boolean): Long = {
    val leftBits  = truncate(left, width)
    val rightBits = truncate(right, width)
    if (rightBits == 0L) 0L
    else if (signed) {
      val dividend = signExtend(leftBits, width)
      val divisor  = signExtend(rightBits, width)
      if (dividend == Long.MinValue && divisor == -1L) Long.MinValue
      else dividend / divisor
    } else if (width == 64) java.lang.Long.divideUnsigned(leftBits, rightBits)
    else leftBits / rightBits
  }

  private def remainderInteger(left: Long, right: Long, width: Int, signed: Boolean): Long = {
    val leftBits  = truncate(left, width)
    val rightBits = truncate(right, width)
    if (rightBits == 0L) 0L
    else if (signed) {
      val dividend = signExtend(leftBits, width)
      val divisor  = signExtend(rightBits, width)
      if (dividend == Long.MinValue && divisor == -1L) 0L
      else dividend % divisor
    } else if (width == 64) java.lang.Long.remainderUnsigned(leftBits, rightBits)
    else leftBits % rightBits
  }

  private def compare(
      left: Long,
      right: Long,
      comparison: Comparison,
      kind: Char,
      width: Int
  ): Boolean = {
    val leftValue          = truncate(left, width)
    val rightValue         = truncate(right, width)
    val (order, unordered) =
      if (kind == 'f' || kind == 'b') {
        val leftFloat =
          if (kind == 'b') IEEE754.binary32ToFloat((leftValue << 16) & 0xffffffffL).toDouble
          else IEEE754.toDouble(leftValue, width)
        val rightFloat =
          if (kind == 'b') IEEE754.binary32ToFloat((rightValue << 16) & 0xffffffffL).toDouble
          else IEEE754.toDouble(rightValue, width)
        val order = if (leftFloat < rightFloat) -1 else if (leftFloat > rightFloat) 1 else 0
        order -> (leftFloat.isNaN || rightFloat.isNaN)
      } else {
        val integerOrder =
          if (kind == 's') java.lang.Long.compare(signExtend(leftValue, width), signExtend(rightValue, width))
          else if (width == 64) java.lang.Long.compareUnsigned(leftValue, rightValue)
          else java.lang.Long.compare(leftValue, rightValue)
        integerOrder -> false
      }
    comparison match {
      case Comparison.Ordered                     => !unordered
      case Comparison.Unordered                   => unordered
      case Comparison.Equal                       => !unordered && order == 0
      case Comparison.EqualUnordered              => unordered || order == 0
      case Comparison.NotEqual                    => !unordered && order != 0
      case Comparison.NotEqualUnordered           => unordered || order != 0
      case Comparison.LessThan                    => !unordered && order < 0
      case Comparison.LessThanUnordered           => unordered || order < 0
      case Comparison.LessThanOrEqual             => !unordered && order <= 0
      case Comparison.LessThanOrEqualUnordered    => unordered || order <= 0
      case Comparison.GreaterThan                 => !unordered && order > 0
      case Comparison.GreaterThanUnordered        => unordered || order > 0
      case Comparison.GreaterThanOrEqual          => !unordered && order >= 0
      case Comparison.GreaterThanOrEqualUnordered => unordered || order >= 0
    }
  }

  private def comparePredicates(left: Boolean, right: Boolean, comparison: Comparison): Boolean = comparison match {
    case Comparison.Ordered | Comparison.Unordered                              => false
    case Comparison.Equal | Comparison.EqualUnordered                           => left == right
    case Comparison.NotEqual | Comparison.NotEqualUnordered                     => left != right
    case Comparison.LessThan | Comparison.LessThanUnordered                     => !left && right
    case Comparison.LessThanOrEqual | Comparison.LessThanOrEqualUnordered       => !left || right
    case Comparison.GreaterThan | Comparison.GreaterThanUnordered               => left && !right
    case Comparison.GreaterThanOrEqual | Comparison.GreaterThanOrEqualUnordered => left || !right
  }

  private def convert(value: Long, sourceType: String, destinationType: String, rounding: Option[String]): Long = {
    val sourceWidth         = conversionTypeWidth(sourceType)
    val destinationWidth    = conversionTypeWidth(destinationType)
    val sourceIsBf16        = sourceType == "bf16"
    val destinationIsBf16   = destinationType == "bf16"
    val sourceKind          = if (sourceIsBf16) 'f' else sourceType.head
    val destinationKind     = if (destinationIsBf16) 'f' else destinationType.head
    lazy val floatingSource =
      if (sourceIsBf16) IEEE754.binary32ToFloat(truncate(value, 16) << 16).toDouble
      else IEEE754.toDouble(value, sourceWidth)

    if (destinationIsBf16) {
      val floating =
        if (sourceKind == 'f') floatingSource
        else {
          val sourceBits = truncate(value, sourceWidth)
          val integer    = if (sourceKind == 's') signExtend(sourceBits, sourceWidth) else sourceBits
          if (sourceKind == 'u' && sourceWidth == 64 && integer < 0)
            BigInt(java.lang.Long.toUnsignedString(integer)).toDouble
          else integer.toDouble
        }
      return bfloat16FromFloat(floating.toFloat, rounding)
    }
    if (sourceKind == 'f') {
      val floating = floatingSource
      if (destinationKind == 'f') {
        // The `i` variants round the floating-point value to an integral
        // value before converting it.  CUDA's device math uses
        // `cvt.rzi.f32.f32` for range reduction; treating it as an ordinary
        // same-width conversion leaves the fractional part in place and
        // changes the scale used by sinhf/coshf.
        val integral = rounding match {
          case Some("rzi") => Some(if (floating < 0.0) Math.ceil(floating) else Math.floor(floating))
          case Some("rmi") => Some(Math.floor(floating))
          case Some("rpi") => Some(Math.ceil(floating))
          case _           => None
        }
        IEEE754.fromDouble(integral.getOrElse(floating), destinationWidth)
      } else {
        val rounded = rounding match {
          case Some("rn") | Some("rni") => Math.rint(floating)
          case Some("rm") | Some("rmi") => Math.floor(floating)
          case Some("rp") | Some("rpi") => Math.ceil(floating)
          case _                        => if (floating < 0) Math.ceil(floating) else Math.floor(floating)
        }
        truncate(rounded.toLong, destinationWidth)
      }
    } else {
      val sourceBits = truncate(value, sourceWidth)
      val integer    =
        if (sourceKind == 's') signExtend(sourceBits, sourceWidth)
        else sourceBits
      if (destinationKind == 'f') {
        val floating =
          if (sourceKind == 'u' && sourceWidth == 64 && integer < 0)
            BigInt(java.lang.Long.toUnsignedString(integer)).toDouble
          else integer.toDouble
        IEEE754.fromDouble(floating, destinationWidth)
      } else truncate(integer, destinationWidth)
    }
  }

  private def bfloat16FromFloat(value: Float, rounding: Option[String]): Long = {
    val bits    = IEEE754.floatToBinary32(value)
    val upper   = (bits >>> 16) & 0xffffL
    val lower   = bits & 0xffffL
    val rounded = rounding match {
      case Some("rz") | Some("rzi")                                         => upper
      case Some("rm") | Some("rmi") if value < 0.0f && lower != 0L          => upper + 1L
      case Some("rp") | Some("rpi") if value > 0.0f && lower != 0L          => upper + 1L
      case _ if lower > 0x8000L || (lower == 0x8000L && (upper & 1L) != 0L) => upper + 1L
      case _                                                                => upper
    }
    truncate(rounded, 16)
  }

  private def conversionTypeWidth(name: String): Int = if (name == "bf16") 16 else name.drop(1).toInt
}

final case class ExecutionError(message: String)
final case class ExecutionResult(trace: Trace)
