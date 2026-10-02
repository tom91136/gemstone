package net.kurobako.gemstone.isa.nvidia

import VMExecution.*
import VMIR.*
import cats.syntax.all.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Bits.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.string.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import scala.collection.mutable

object VMSemantics {

  private def stageResult(
      effects: VMBlockState.WriteBuffer,
      result: Either[VMFault, VMWrite]
  ): Either[VMFault, Unit] =
    result match {
      case Left(fault)  => Left(fault)
      case Right(write) =>
        effects.stage(write)
        Right(())
    }

  private def stageRegisterResult(
      effects: VMBlockState.WriteBuffer,
      lane: Int,
      destination: NonEmpty[String],
      result: Either[VMFault, Long]
  ): Either[VMFault, Unit] =
    result match {
      case Left(fault)  => Left(fault)
      case Right(value) =>
        effects.stageRegister(lane, destination, value)
        Right(())
    }

  private def stageResults(
      effects: VMBlockState.WriteBuffer,
      result: Either[VMFault, Vec[VMWrite]]
  ): Either[VMFault, Unit] =
    result match {
      case Left(fault)   => Left(fault)
      case Right(writes) =>
        writes.foreach(write => effects.stage(write))
        Right(())
    }

  extension (instruction: VMIR.Instruction) {
    // An instruction updates per-lane machine state; staged mutable maps avoid allocating immutable copies.
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    def execute(context: VMInstructionContext, effects: VMBlockState.WriteBuffer): Either[VMFault, Step] =
      if (executeFast(instruction, context)) Right(Step.Advance)
      else instruction match {
        case VMIR.Instruction.WriteConditionFlag(flag, source) =>
          forEveryLane(context) { lane =>
            context.read(lane, source).map(value =>
              effects.stage(VMWrite.ConditionFlag(lane, flag, Some(value != 0L)))
            )
          }.map(_ => Step.Advance)
        case VMIR.Instruction.ReadConditionFlag(destination, flag, predicate) =>
          forEveryLane(context) { lane =>
            context.state.conditionCodes(lane).read(flag).toRight(
              VMFault.InvalidState(s"condition flag $flag is undefined at pc ${context.pc}, lane $lane")
            ).map { value =>
              if (predicate) effects.stage(VMWrite.Predicate(lane, destination, value))
              else effects.stageRegister(lane, destination, if (value) 1L else 0L)
            }
          }.map(_ => Step.Advance)
        case VMIR.Instruction.InvalidateConditionFlag(flag) =>
          context.active.foreachLane(lane => effects.stage(VMWrite.ConditionFlag(lane, flag, None)))
          Right(Step.Advance)
        case VMIR.Instruction.InvalidateAddThreeCarry =>
          context.active.foreachLane(lane => effects.stage(VMWrite.AddThreeCarry(lane, None)))
          Right(Step.Advance)
        case VMIR.Instruction.IntegerAddThree(
              destination,
              first,
              second,
              third,
              width,
              carryMode,
              negateFirst,
              negateSecond,
              negateThird
            ) =>
          forEveryLane(context) { lane =>
            val carry = carryMode match {
              case AddThreeCarryMode.Consume =>
                context.state.conditionCodes(lane).readThreeInputCarry.toRight(
                  VMFault.InvalidState(s"IADD3 carry state is undefined at pc ${context.pc}, lane $lane")
                )
              case _ => Right(0)
            }
            for {
              firstValue  <- context.read(lane, first)
              secondValue <- context.read(lane, second)
              thirdValue  <- context.read(lane, third)
              carryValue  <- carry
            } yield {
              val mask     = (BigInt(1) << width.raw) - 1
              val operands = Vec(firstValue, secondValue, thirdValue).zip(
                Vec(negateFirst, negateSecond, negateThird)
              ).map { case (value, negate) =>
                val bits = unsignedInteger(value, width)
                if (negate) mask - bits + (if (carryMode == AddThreeCarryMode.Produce) 1 else 0) else bits
              }
              val sum = operands.sum + carryValue
              effects.stageRegister(lane, destination, (sum & mask).longValue)
              if (carryMode == AddThreeCarryMode.Produce)
                effects.stage(VMWrite.AddThreeCarry(lane, Some((sum >> width.raw).toInt)))
              else if (carryMode == AddThreeCarryMode.Consume)
                effects.stage(VMWrite.AddThreeCarry(lane, None))
            }
          }.map(_ => Step.Advance)
        case VMIR.Instruction.Tensor(operation, _)             => VMTensor.execute(operation, context, effects)
        case VMIR.Instruction.ConvertTF32(destination, source) =>
          forEveryLane(context) { lane =>
            stageRegisterResult(
              effects,
              lane,
              destination,
              context.read(lane, source).map { bits =>
                val magnitude = bits & 0x7fffffffL
                if (magnitude > 0x7f800000L) (bits & 0xffffe000L) | 0x00400000L
                else if (magnitude == 0x7f800000L) bits & 0xffffffffL
                else (bits + 0x1000L) & 0xffffe000L
              }
            )
          }.map(_ => Step.Advance)
        case VMIR.Instruction.LoadParameter(destinations, parameter, offset, elementWidth, signed) =>
          val elementBytes   = elementWidth.byteCount
          val argumentValues = context.arguments.get(parameter).map { argument =>
            if (offset.raw > argument.bytes.length - elementBytes.raw * destinations.size)
              Left(VMFault.InvalidState(
                s"parameter $parameter load at $offset exceeds ${argument.bytes.length} bytes"
              ))
            else
              Right(Array.tabulate(destinations.size) { index =>
                val byteOffset = offset.raw + index * elementBytes.raw
                loadBits(
                  LittleEndian.decode(argument.bytes.toArray, byteOffset, elementBytes),
                  elementWidth,
                  signed
                )
              })
          }
          var failure: Option[VMFault] = None
          context.active.foreachLane { lane =>
            if (failure.isEmpty)
              context.state.parameters(lane).get(parameter) match {
                case Some(value) if offset.raw == 0 && destinations.size == 1 =>
                  effects.stageRegister(lane, destinations.head, loadBits(value, elementWidth, signed))
                case Some(_) =>
                  failure = Some(VMFault.InvalidState(
                    s"unsupported local parameter load: $parameter+$offset"
                  ))
                case None =>
                  argumentValues match {
                    case None =>
                      failure = Some(VMFault.InvalidState(
                        s"missing kernel argument $parameter"
                      ))
                    case Some(Left(error))   => failure = Some(error)
                    case Some(Right(values)) =>
                      var index = 0
                      while (index < destinations.size) {
                        effects.stageRegister(lane, destinations(index), values(index))
                        index += 1
                      }
                  }
              }
          }
          failure match {
            case Some(fault) => Left(fault)
            case None        => Right(Step.Advance)
          }
        case VMIR.Instruction.LoadParameterIndirect(destination, address, width, signed) =>
          forEveryLane(context) { lane =>
            val loaded = context.read(lane, address).flatMap { rawAddress =>
              context.state.readParameterBuffer(rawAddress, width.byteCount) match {
                case Some(value)                         => Right(loadBits(value, width, signed))
                case None if isSharedAddress(rawAddress) =>
                  Right(loadBits(context.state.readShared(rawAddress, width), width, signed))
                case None if isLocalAddress(rawAddress) =>
                  Right(loadBits(context.state.readLocal(lane, rawAddress, width), width, signed))
                case None =>
                  context.memory
                    .readLittleEndian(GuestAddress(rawAddress), width.byteCount)
                    .leftMap(error =>
                      VMFault.fromMemory(error, s"pc ${context.pc}, lane $lane: ")
                    )
                    .map(value => loadBits(value, width, signed))
              }
            }
            stageRegisterResult(effects, lane, destination, loaded)
          }.map(_ => Step.Advance)
        case VMIR.Instruction.LoadParameterIndirectVector(destinations, address, width, signed) =>
          forEveryLane(context) { lane =>
            val writes = context.read(lane, address).flatMap { rawAddress =>
              destinations.zipWithIndex.traverse { case (destination, index) =>
                val elementAddress = rawAddress + index.toLong * width.byteCount.raw.toLong
                context.state.readParameterBuffer(elementAddress, width.byteCount) match {
                  case Some(loaded) =>
                    Right(VMWrite.Register(lane, destination, loadBits(loaded, width, signed)))
                  case None if isSharedAddress(elementAddress) =>
                    Right(VMWrite.Register(
                      lane,
                      destination,
                      loadBits(context.state.readShared(elementAddress, width), width, signed)
                    ))
                  case None if isLocalAddress(elementAddress) =>
                    Right(VMWrite.Register(
                      lane,
                      destination,
                      loadBits(context.state.readLocal(lane, elementAddress, width), width, signed)
                    ))
                  case None =>
                    context.memory
                      .readLittleEndian(GuestAddress(elementAddress), width.byteCount)
                      .leftMap(error =>
                        VMFault.fromMemory(error, s"pc ${context.pc}, lane $lane: ")
                      )
                      .map(loaded =>
                        VMWrite.Register(
                          lane,
                          destination,
                          loadBits(loaded, width, signed)
                        )
                      )
                }
              }
            }
            stageResults(effects, writes)
          }.map(_ => Step.Advance)
        case VMIR.Instruction.StoreParameter(parameter, offset, source, elementWidth) =>
          forEveryLane(context) { lane =>
            context.read(lane, source) match {
              case Left(fault)  => Left(fault)
              case Right(value) =>
                val stored = truncate(value, elementWidth)
                effects.stage(VMWrite.ParameterBytes(lane, parameter, offset.raw, elementWidth, stored))
                if (offset.raw == 0)
                  effects.stage(VMWrite.Parameter(lane, parameter, stored))
                source match {
                  case Value.Register(name) if context.state.functionRegisters(lane).contains(name) =>
                    effects.stage(VMWrite.FunctionParameter(
                      lane,
                      parameter,
                      context.state.functionRegisters(lane)(name)
                    ))
                  case Value.RegisterSlot(index) =>
                    val name = VMRegisterFile.name(index)
                    context.state.functionRegisters(lane).get(name).foreach(function =>
                      effects.stage(VMWrite.FunctionParameter(lane, parameter, function))
                    )
                  case _ => ()
                }
                Right(())
            }
          }.map(_ => Step.Advance)
        case VMIR.Instruction.ShuffleDown(destination, validity, source, delta, clamp, _, width) =>
          forEveryLane(context) { lane =>
            val writes = for {
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
            } yield validity.fold(
              Vec(VMWrite.Register(lane, destination, truncate(value, width)))
            )(name =>
              Vec(
                VMWrite.Register(lane, destination, truncate(value, width)),
                VMWrite.Predicate(lane, name, valid)
              )
            )
            stageResults(effects, writes)
          }.map(_ => Step.Advance)
        case VMIR.Instruction.ShuffleUp(destination, validity, source, delta, clamp, _, width) =>
          forEveryLane(context) { lane =>
            val writes = for {
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
            } yield validity.fold(
              Vec(VMWrite.Register(lane, destination, truncate(value, width)))
            )(name =>
              Vec(
                VMWrite.Register(lane, destination, truncate(value, width)),
                VMWrite.Predicate(lane, name, valid)
              )
            )
            stageResults(effects, writes)
          }.map(_ => Step.Advance)
        case VMIR.Instruction.ShuffleIndex(destination, validity, source, index, clamp, _, width) =>
          forEveryLane(context) { lane =>
            val writes = for {
              indexValue <- context.read(lane, index)
              clampValue <- context.read(lane, clamp)
              laneInWarp   = lane & 31
              requested    = (indexValue & 31L).toInt
              segmentMask  = ((clampValue >>> 8) & 31L).toInt
              segmentBase  = laneInWarp & segmentMask
              targetInWarp = segmentBase | (requested & ~segmentMask)
              maxLane      = segmentBase | ((clampValue & 31L).toInt & ~segmentMask)
              targetLane   = (lane - laneInWarp) + targetInWarp
              valid        = targetInWarp <= maxLane && targetLane < context.state.laneCount
              value <- context.read(if (valid) targetLane else lane, source)
            } yield validity.fold(
              Vec(VMWrite.Register(lane, destination, truncate(value, width)))
            )(name =>
              Vec(
                VMWrite.Register(lane, destination, truncate(value, width)),
                VMWrite.Predicate(lane, name, valid)
              )
            )
            stageResults(effects, writes)
          }.map(_ => Step.Advance)
        case VMIR.Instruction.ShuffleBfly(destination, validity, source, index, clamp, _, width) =>
          forEveryLane(context) { lane =>
            val writes = for {
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
            } yield validity.fold(
              Vec(VMWrite.Register(lane, destination, truncate(value, width)))
            )(name =>
              Vec(
                VMWrite.Register(lane, destination, truncate(value, width)),
                VMWrite.Predicate(lane, name, valid)
              )
            )
            stageResults(effects, writes)
          }.map(_ => Step.Advance)
        case VMIR.Instruction.VoteBallot(destination, predicate, memberMask) =>
          forEveryLane(context) { lane =>
            val write = context.read(lane, memberMask).flatMap { maskValue =>
              val warpBase = lane & ~31
              (0 until 32).foldLeft[Either[VMFault, Long]](Right(0L)) {
                case (result, laneInWarp) =>
                  val candidate = warpBase + laneInWarp
                  if (
                    (maskValue & (1L << laneInWarp)) == 0L || !context.active.contains(candidate) ||
                    candidate >= context.state.laneCount
                  ) result
                  else
                    result.flatMap { ballot =>
                      context.state.predicates(candidate).get(predicate).toRight(
                        VMFault.UndefinedRegister(predicate, context.pc, candidate)
                      ).map { selected =>
                        if (selected) ballot | (1L << laneInWarp) else ballot
                      }
                    }
              }.map(ballot =>
                VMWrite.Register(
                  lane,
                  destination,
                  truncate(ballot, 32)
                )
              )
            }
            stageResult(effects, write)
          }.map(_ => Step.Advance)
        case VMIR.Instruction.VotePredicate(destination, predicate, memberMask, operation) =>
          forEveryLane(context) { lane =>
            val write = context.read(lane, memberMask).flatMap { maskValue =>
              val warpBase = lane & ~31
              (0 until 32).foldLeft[Either[VMFault, Vec[Boolean]]](Right(Vec.empty)) {
                case (result, laneInWarp) =>
                  val candidate = warpBase + laneInWarp
                  if (
                    (maskValue & (1L << laneInWarp)) == 0L || !context.active.contains(candidate) ||
                    candidate >= context.state.laneCount
                  ) result
                  else
                    result.flatMap { values =>
                      context.state.predicates(candidate).get(predicate).toRight(
                        VMFault.UndefinedRegister(predicate, context.pc, candidate)
                      ).map(values :+ _)
                    }
              }.map { values =>
                val selected = operation match {
                  case VoteOperation.All     => values.nonEmpty && values.forall(value => value)
                  case VoteOperation.Any     => values.exists(value => value)
                  case VoteOperation.Uniform => values.distinct.size <= 1
                }
                VMWrite.Predicate(lane, destination, selected)
              }
            }
            stageResult(effects, write)
          }.map(_ => Step.Advance)
        case VMIR.Instruction.BitFieldExtract(destination, source, start, widthValue, width, signed) =>
          forEveryLane(context) { lane =>
            val write = for {
              sourceValue <- context.read(lane, source)
              startValue  <- context.read(lane, start)
              countValue  <- context.read(lane, widthValue)
            } yield {
              val result = extractBits(sourceValue, startValue, countValue, width, signed)
              VMWrite.Register(lane, destination, result)
            }
            stageResult(effects, write)
          }.map(_ => Step.Advance)
        case other => executeMemoryAndControl(other, context, effects)
      }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def executeScalarLoad(
      destination: NonEmpty[String],
      address: Value,
      width: Int where (Between[8, 64] & MultipleOf[8]),
      signed: Boolean,
      access: MemoryAccess,
      context: VMInstructionContext,
      effects: VMBlockState.WriteBuffer
  ): Either[VMFault, Step] = {
    val generic = access.generic
    val system  = access.systemScoped
    access match {
      case MemoryAccess.Shared | MemoryAccess.Local =>
        forEveryLane(context) { lane =>
          stageRegisterResult(
            effects,
            lane,
            destination,
            context.read(lane, address).map { rawAddress =>
              val bits = access match {
                case MemoryAccess.Shared => context.state.readShared(rawAddress, width)
                case MemoryAccess.Local  => context.state.readLocal(lane, rawAddress, width)
                case _                   => 0L
              }
              loadBits(bits, width, signed)
            }
          )
        }.map(_ => Step.Advance)
      case _ =>
        prepareGlobalReads(context, address, width.byteCount.raw, generic, system = system).flatMap { _ =>
          var failure: Option[VMFault] = None
          context.active.foreachLane { lane =>
            if (failure.isEmpty) {
              val rawAddress = readFast(context, lane, address)
              val loaded     = context.state.readParameterBuffer(rawAddress, width.byteCount) match {
                case Some(value)                                    => Right(value)
                case None if generic && isSharedAddress(rawAddress) =>
                  Right(context.state.readShared(rawAddress, width))
                case None if generic && isLocalAddress(rawAddress) =>
                  Right(context.state.readLocal(lane, rawAddress, width))
                case None if system =>
                  context.memory.readSystemLittleEndian(GuestAddress(rawAddress), width.byteCount)
                case None =>
                  context.memory.readLittleEndian(GuestAddress(rawAddress), width.byteCount)
              }
              loaded match {
                case Left(error)  => failure = Some(VMFault.fromMemory(error, s"pc ${context.pc}, lane $lane: "))
                case Right(value) => effects.stageRegister(lane, destination, loadBits(value, width, signed))
              }
            }
          }
          failure match {
            case Some(fault) => Left(fault)
            case None        => Right(Step.Advance)
          }
        }
    }
  }

  private def executeVectorLoad(
      destinations: Vec[NonEmpty[String]],
      address: Value,
      width: Int where (Between[8, 64] & MultipleOf[8]),
      signed: Boolean,
      access: MemoryAccess,
      context: VMInstructionContext,
      effects: VMBlockState.WriteBuffer
  ): Either[VMFault, Step] = {
    val generic                          = access.generic
    val system                           = access.systemScoped
    val coherentVectorCandidate          = access.coherentVectorCandidate
    def execute(): Either[VMFault, Step] =
      forEveryLane(context) { lane =>
        val writes = context.read(lane, address).flatMap { rawAddress =>
          if (
            coherentVectorCandidate &&
            coherentGlobalVector(context, rawAddress, destinations.size, width)
          )
            loadCoherentVector(context, lane, rawAddress, destinations, width, signed)
          else destinations.zipWithIndex.traverse { case (destination, index) =>
            val elementAddress = rawAddress + index.toLong * width.byteCount.raw.toLong
            val loaded         = access match {
              case MemoryAccess.Shared     => Right(context.state.readShared(elementAddress, width))
              case MemoryAccess.Local      => Right(context.state.readLocal(lane, elementAddress, width))
              case MemoryAccess.Generic(_) => context.state.readParameterBuffer(elementAddress, width.byteCount) match {
                  case Some(value)                             => Right(value)
                  case None if isSharedAddress(elementAddress) => Right(context.state.readShared(elementAddress, width))
                  case None if isLocalAddress(elementAddress)  =>
                    Right(context.state.readLocal(lane, elementAddress, width))
                  case None if system =>
                    context.memory.readSystemLittleEndian(GuestAddress(elementAddress), width.byteCount)
                  case None => context.memory.readLittleEndian(GuestAddress(elementAddress), width.byteCount)
                }
              case MemoryAccess.Global(true) =>
                context.memory.readSystemLittleEndian(GuestAddress(elementAddress), width.byteCount)
              case MemoryAccess.Global(false) =>
                context.memory.readLittleEndian(GuestAddress(elementAddress), width.byteCount)
            }
            loaded.leftMap(error => VMFault.fromMemory(error, s"pc ${context.pc}, lane $lane: "))
              .map(value => VMWrite.Register(lane, destination, loadBits(value, width, signed)))
          }
        }
        stageResults(effects, writes)
      }.map(_ => Step.Advance)
    access match {
      case MemoryAccess.Shared | MemoryAccess.Local => execute()
      case _                                        =>
        val totalBytes = destinations.size * width.byteCount.raw
        prepareGlobalReads(context, address, totalBytes, generic, system = system).flatMap(_ => execute())
    }
  }

  private def executeScalarStore(
      address: Value,
      source: Value,
      width: Int where (Between[8, 64] & MultipleOf[8]),
      access: MemoryAccess,
      context: VMInstructionContext,
      effects: VMBlockState.WriteBuffer
  ): Either[VMFault, Step] = {
    val system = access match {
      case MemoryAccess.Global(true) | MemoryAccess.Generic(true) => true
      case _                                                      => false
    }
    if (access == MemoryAccess.Shared || access == MemoryAccess.Local)
      forEveryLane(context) { lane =>
        val write = for {
          rawAddress <- context.read(lane, address)
          value      <- context.read(lane, source)
        } yield access match {
          case MemoryAccess.Shared => VMWrite.Shared(lane, rawAddress, width, value)
          case MemoryAccess.Local  => VMWrite.Local(lane, rawAddress, width, value)
          case _                   => VMWrite.Memory(lane, GuestAddress(rawAddress), width, value)
        }
        stageResult(effects, write)
      }.map(_ => Step.Advance)
    else {
      context.active.foreachLane { lane =>
        val rawAddress = readFast(context, lane, address)
        val value      = readFast(context, lane, source)
        if (context.state.parameterBufferRange(rawAddress, width.byteCount.raw).nonEmpty)
          effects.stage(VMWrite.ParameterBuffer(rawAddress, width, value))
        else access match {
          case MemoryAccess.Generic(_) if isSharedAddress(rawAddress) =>
            effects.stage(VMWrite.Shared(lane, rawAddress, width, value))
          case MemoryAccess.Generic(_) if isLocalAddress(rawAddress) =>
            effects.stage(VMWrite.Local(lane, rawAddress, width, value))
          case _ if system => effects.stage(VMWrite.SystemMemory(lane, GuestAddress(rawAddress), width, value))
          case _           => effects.stageMemory(lane, GuestAddress(rawAddress), width, value)
        }
      }
      Right(Step.Advance)
    }
  }

  private def executeVectorStore(
      address: Value,
      sources: Vec[Value],
      width: Int where (Between[8, 64] & MultipleOf[8]),
      access: MemoryAccess,
      context: VMInstructionContext,
      effects: VMBlockState.WriteBuffer
  ): Either[VMFault, Step] = {
    val system                  = access.systemScoped
    val ordinaryGlobalOrGeneric = access.coherentVectorCandidate
    val sharedOrLocal           = access.sharedOrLocal
    if (ordinaryGlobalOrGeneric)
      forEveryLane(context) { lane =>
        val writes = context.read(lane, address).flatMap { rawAddress =>
          if (coherentGlobalVector(context, rawAddress, sources.size, width))
            storeCoherentVector(context, lane, rawAddress, sources, width)
          else sources.zipWithIndex.traverse { case (source, index) =>
            context.read(lane, source).map { value =>
              val elementAddress = rawAddress + index.toLong * width.byteCount.raw.toLong
              if (isSharedAddress(elementAddress)) VMWrite.Shared(lane, elementAddress, width, value)
              else if (isLocalAddress(elementAddress)) VMWrite.Local(lane, elementAddress, width, value)
              else VMWrite.Memory(lane, GuestAddress(elementAddress), width, value)
            }
          }
        }
        stageResults(effects, writes)
      }.map(_ => Step.Advance)
    else if (sharedOrLocal)
      forEveryLane(context) { lane =>
        val writes = context.read(lane, address).flatMap { rawAddress =>
          sources.zipWithIndex.traverse { case (source, index) =>
            context.read(lane, source).map { value =>
              val elementAddress = rawAddress + index.toLong * width.byteCount.raw.toLong
              access match {
                case MemoryAccess.Shared => VMWrite.Shared(lane, elementAddress, width, value)
                case MemoryAccess.Local  => VMWrite.Local(lane, elementAddress, width, value)
                case _                   => VMWrite.Memory(lane, GuestAddress(elementAddress), width, value)
              }
            }
          }
        }
        stageResults(effects, writes)
      }.map(_ => Step.Advance)
    else {
      context.active.foreachLane { lane =>
        val rawAddress = readFast(context, lane, address)
        sources.zipWithIndex.foreach { (source, index) =>
          val elementAddress = rawAddress + index.toLong * width.byteCount.raw.toLong
          val value          = readFast(context, lane, source)
          if (
            access != MemoryAccess.Shared && access != MemoryAccess.Local &&
            context.state.parameterBufferRange(elementAddress, width.byteCount.raw).nonEmpty
          )
            effects.stage(VMWrite.ParameterBuffer(elementAddress, width, value))
          else access match {
            case MemoryAccess.Shared => effects.stage(VMWrite.Shared(lane, elementAddress, width, value))
            case MemoryAccess.Local  => effects.stage(VMWrite.Local(lane, elementAddress, width, value))
            case MemoryAccess.Generic(_) if isSharedAddress(elementAddress) =>
              effects.stage(VMWrite.Shared(lane, elementAddress, width, value))
            case MemoryAccess.Generic(_) if isLocalAddress(elementAddress) =>
              effects.stage(VMWrite.Local(lane, elementAddress, width, value))
            case _ if system => effects.stage(VMWrite.SystemMemory(lane, GuestAddress(elementAddress), width, value))
            case _           => effects.stageMemory(lane, GuestAddress(elementAddress), width, value)
          }
        }
      }
      Right(Step.Advance)
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def executeMemoryAndControl(
      instruction: VMIR.Instruction,
      context: VMInstructionContext,
      effects: VMBlockState.WriteBuffer
  ): Either[VMFault, Step] = instruction match {
    case VMIR.Instruction.Load(destination, address, width, signed, access) =>
      executeScalarLoad(destination, address, width, signed, access, context, effects)
    case VMIR.Instruction.LoadVector(destinations, address, width, signed, access) =>
      executeVectorLoad(destinations, address, width, signed, access, context, effects)
    case VMIR.Instruction.Store(address, source, width, access) =>
      executeScalarStore(address, source, width, access, context, effects)
    case VMIR.Instruction.StoreVector(address, sources, width, access) =>
      executeVectorStore(address, sources, width, access, context, effects)
    case VMIR.Instruction.AsyncCopy(destination, source, size) =>
      forEveryLane(context) { lane =>
        val writes = for {
          destinationAddress <- context.read(lane, destination)
          sourceAddress      <- context.read(lane, source)
          pending            <- Vec.from(0 until size.raw by 8).traverse { offset =>
            val width = (Math.min(8, size.raw - offset) * 8).unsafe[Int where (Between[8, 64] & MultipleOf[8])]
            context.memory
              .readLittleEndian(GuestAddress(sourceAddress + offset.toLong), width.byteCount)
              .leftMap(error =>
                VMFault.fromMemory(error, s"pc ${context.pc}, lane $lane: ")
              )
              .map(value =>
                VMWrite.Shared(
                  lane,
                  destinationAddress + offset.toLong,
                  width,
                  value
                )
              )
          }
        } yield pending
        stageResults(effects, writes)
      }.map(_ => Step.Advance)
    case VMIR.Instruction.AsyncCommit | VMIR.Instruction.AsyncWait                      => Right(Step.Advance)
    case VMIR.Instruction.WarpReduce(destination, source, memberMask, operation, width) =>
      val values                   = mutable.Map.empty[Int, Long]
      val masks                    = mutable.Map.empty[Int, Long]
      var failure: Option[VMFault] = None
      context.active.foreachLane { lane =>
        if (failure.isEmpty)
          context.read(lane, source) match {
            case Left(error)  => failure = Some(error)
            case Right(value) => context.read(lane, memberMask) match {
                case Left(error) => failure = Some(error)
                case Right(mask) =>
                  values(lane) = value
                  masks(lane) = mask
              }
          }
      }
      failure match {
        case Some(error) => Left(error)
        case None        =>
          context.active.foreachLane { lane =>
            val warpBase = lane & ~31
            val mask     = masks.getOrElse(lane, 0L)
            val members  = Vec.toVec(values.view.collect {
              case (candidate, value)
                  if (candidate & ~31) == warpBase && (mask & (1L << (candidate & 31))) != 0L => value
            })
            if ((mask & (1L << (lane & 31))) == 0L || members.isEmpty)
              failure = Some(VMFault.InvalidState(
                s"redux.sync lane $lane is absent from its member mask"
              ))
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
              effects.stageRegister(lane, destination, truncate(reduced, width))
            }
          }
          failure.toLeft(Step.Advance)
      }
    case VMIR.Instruction.AtomicAdd(destination, address, source, width, space) =>
      forEveryLane(context) { lane =>
        val write = for {
          rawAddress  <- context.read(lane, address)
          sourceValue <- context.read(lane, source)
        } yield
          if (atomicTargetsShared(space, rawAddress))
            VMWrite.AtomicSharedAdd(lane, destination, rawAddress, width, sourceValue)
          else
            VMWrite.AtomicMemoryAdd(
              lane,
              destination,
              GuestAddress(rawAddress),
              width,
              sourceValue
            )
        stageResult(effects, write)
      }.map(_ => Step.Advance)
    case VMIR.Instruction.AtomicCompareExchange(destination, address, comparator, source, width, space) =>
      forEveryLane(context) { lane =>
        val write = for {
          rawAddress      <- context.read(lane, address)
          comparatorValue <- context.read(lane, comparator)
          sourceValue     <- context.read(lane, source)
        } yield
          if (atomicTargetsShared(space, rawAddress))
            VMWrite.AtomicSharedCompareExchange(lane, destination, rawAddress, width, comparatorValue, sourceValue)
          else
            VMWrite.AtomicMemoryCompareExchange(
              lane,
              destination,
              GuestAddress(rawAddress),
              width,
              comparatorValue,
              sourceValue
            )
        stageResult(effects, write)
      }.map(_ => Step.Advance)
    case VMIR.Instruction.AtomicExchange(destination, address, source, width, space) =>
      forEveryLane(context) { lane =>
        val write = for {
          rawAddress  <- context.read(lane, address)
          sourceValue <- context.read(lane, source)
        } yield
          if (atomicTargetsShared(space, rawAddress))
            VMWrite.AtomicSharedExchange(lane, destination, rawAddress, width, sourceValue)
          else
            VMWrite.AtomicMemoryExchange(lane, destination, GuestAddress(rawAddress), width, sourceValue)
        stageResult(effects, write)
      }.map(_ => Step.Advance)
    case VMIR.Instruction.AtomicBinary(destination, address, source, width, space, operation) =>
      forEveryLane(context) { lane =>
        val write = for {
          rawAddress  <- context.read(lane, address)
          sourceValue <- context.read(lane, source)
        } yield
          if (atomicTargetsShared(space, rawAddress))
            VMWrite.AtomicSharedBinary(lane, destination, rawAddress, width, sourceValue, operation)
          else
            VMWrite.AtomicMemoryBinary(
              lane,
              destination,
              GuestAddress(rawAddress),
              width,
              sourceValue,
              operation
            )
        stageResult(effects, write)
      }.map(_ => Step.Advance)
    case VMIR.Instruction.AtomicAddFloat(destination, address, source, width) =>
      forEveryLane(context) { lane =>
        val write = for {
          rawAddress  <- context.read(lane, address)
          sourceValue <- context.read(lane, source)
        } yield VMWrite.AtomicMemoryAddFloat(
          lane,
          destination,
          GuestAddress(rawAddress),
          width,
          sourceValue
        )
        stageResult(effects, write)
      }.map(_ => Step.Advance)
    case VMIR.Instruction.Branch(target)  => Right(Step.Jump(target))
    case VMIR.Instruction.BarrierSync(id) =>
      // A workgroup barrier is identified by its hardware barrier slot, not
      // by the static PC at which a divergent cohort happens to arrive.
      Right(Step.Suspend(WaitKey(WaitScope.Workgroup, id.raw.toLong, 0L)))
    case VMIR.Instruction.BarrierReduce(_, id, _, _) =>
      Right(Step.Suspend(WaitKey(WaitScope.Workgroup, id.raw.toLong, 0L)))
    case VMIR.Instruction.SASSReturnPush(address) =>
      context.active.foreachLane(lane => context.state.sassReturns(lane) += address)
      Right(Step.Advance)
    case VMIR.Instruction.SASSReturnPop(destination) =>
      forEveryLane(context) { lane =>
        val stack = context.state.sassReturns(lane)
        if (stack.isEmpty) Left(VMFault.InvalidState("SASS return stack is empty"))
        else {
          effects.stageRegister(lane, destination, stack.remove(stack.size - 1).toLong)
          Right(())
        }
      }.map(_ => Step.Advance)
    case VMIR.Instruction.Convergence(slot, action) =>
      val masks = mutable.Map.empty[Int, Long]
      context.active.foreachLane { lane =>
        val warp = lane & ~31
        masks(warp) = masks.getOrElse(warp, 0L) | (1L << (lane & 31))
      }
      action match {
        case VMIR.ConvergenceAction.Setup =>
          masks.foreach { (warp, mask) => context.state.convergence((warp, slot)) = mask }
          Right(Step.Advance)
        case VMIR.ConvergenceAction.Break =>
          masks.foreach { (warp, mask) =>
            val key = (warp, slot)
            context.state.convergence(key) = context.state.convergence.getOrElse(key, 0L) & ~mask
          }
          Right(Step.Advance)
        case VMIR.ConvergenceAction.Synchronize => Right(Step.Suspend(WaitKey(WaitScope.Warp, 0L, -slot.toLong - 1L)))
      }
    case VMIR.Instruction.WarpBarrier(memberMask) =>
      var firstLane = -1
      context.active.foreachLane { lane => if (firstLane < 0) firstLane = lane }
      context.read(firstLane, memberMask) match {
        case Left(fault)            => Left(fault)
        case Right(memberMaskValue) =>
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
            Left(VMFault.InvalidState("bar.warp.sync member mask differs across active lanes"))
          else
            Right(Step.Suspend(WaitKey(WaitScope.Warp, memberMaskValue, context.pc.toInt.toLong)))
      }
    case VMIR.Instruction.MemoryBarrier =>
      context.memory.fence().leftMap(error => VMFault.fromMemory(error)).map(_ => Step.Advance)
    case VMIR.Instruction.Prefetch                                   => Right(Step.Advance)
    case VMIR.Instruction.Call(function, returnParameter, parameter) =>
      val groups = mutable.LinkedHashMap.empty[NonEmpty[String], VMLaneMask]
      forEveryLane(context) { lane =>
        context.functionName(lane, function).flatMap { name =>
          VMLaneMask.one(lane).map { member =>
            groups(name) = groups.getOrElse(name, VMLaneMask.Empty).union(member)
          }
        }
      }.flatMap { _ =>
        groups.foldLeft[Either[VMFault, Unit]](Right(())) { case (current, (name, lanes)) =>
          current.flatMap { _ =>
            val groupContext = VMInstructionContext(
              context.kernel,
              context.block,
              context.group,
              context.geometry,
              context.state,
              context.pc,
              lanes,
              context.arguments,
              context.memory,
              context.control,
              context.workgroups
            )
            context.kernel.functions.get(name) match {
              case Some(deviceFunction) =>
                VMInterpreter.executeDeviceFunction(groupContext, deviceFunction, parameter, returnParameter)
              case None =>
                forEveryLane(groupContext) { lane =>
                  executeBuiltin(groupContext, lane, name, returnParameter, parameter).getOrElse(
                    Left(VMFault.InvalidState(s"missing device function $name"))
                  )
                }
            }
          }
        }
      }.map(_ => Step.Advance)
    case VMIR.Instruction.Trap =>
      Left(VMFault.ArchitecturalTrap(
        context.kernel.isaName.toLowerCaseRoot.unsafe[NonEmpty[String]],
        0L,
        context.pc
      ))
    case VMIR.Instruction.Return => Right(Step.Exit(context.active))
    case other => Left(VMFault.InvalidState(s"unhandled compiled ${context.kernel.isaName} instruction $other"))
  }

  /** CUB's scan descriptors rely on coherent aligned status/value transfers, including 16-byte double descriptors. This
    * target behavior is stronger than PTX's minimum vector ordering; it is not a general 128-bit atomic guarantee. Keep
    * shared/local/parameter routing separate and retain the existing word path through eight bytes.
    */
  private def coherentGlobalVector(
      context: VMInstructionContext,
      address: Long,
      count: Int,
      width: Int where (Between[8, 64] & MultipleOf[8])
  ): Boolean = {
    val size = count * width.byteCount.raw
    size > 0 && size <= 16 && (size & (size - 1)) == 0 && (address & (size - 1).toLong) == 0L &&
    !isSharedAddress(address) && !isLocalAddress(address) &&
    context.state.parameterBufferRange(address, size).isEmpty
  }

  private def prepareGlobalReads(
      context: VMInstructionContext,
      address: Value,
      size: Int,
      generic: Boolean,
      system: Boolean
  ): Either[VMFault, Unit] = {
    val ranges = Vec.newBuilder[(GuestAddress, Int >= 0)]
    context.active.foreachLane { lane =>
      val raw = readFast(context, lane, address)
      if (
        context.state.parameterBufferRange(
          raw,
          size
        ).isEmpty && (!generic || (!isSharedAddress(raw) && !isLocalAddress(raw)))
      ) {
        val _ = ranges += ((GuestAddress(raw), size.unsafe[Int >= 0]))
      }
    }
    (if (system) context.memory.prepareSystemReads(ranges.result())
     else context.memory.prepareReads(ranges.result())).leftMap(error =>
      VMFault.fromMemory(error, s"${context.kernel.name}, pc ${context.pc.raw}: ")
    )
  }

  private def loadCoherentVector(
      context: VMInstructionContext,
      lane: Int,
      address: Long,
      destinations: Vec[NonEmpty[String]],
      width: Int where (Between[8, 64] & MultipleOf[8]),
      signed: Boolean
  ): Either[VMFault, Vec[VMWrite]] =
    if (destinations.nonEmpty && destinations.size * width.byteCount.raw <= 8) {
      val packedBytes = (destinations.size * width.byteCount.raw).unsafe[Int where Between[1, 8]]
      context.memory.readLittleEndian(GuestAddress(address), packedBytes)
        .leftMap(error =>
          VMFault.fromMemory(error, s"pc ${context.pc}, lane $lane: ")
        )
        .map { packed =>
          destinations.zipWithIndex.map { case (destination, index) =>
            VMWrite.Register(
              lane,
              destination,
              loadBits(packed >>> (index * width.raw), width, signed)
            )
          }
        }

    } else {
      val totalBytes = destinations.nonNegativeSize * width.byteCount
      context.memory.readBytes(GuestAddress(address), totalBytes)
        .leftMap(error =>
          VMFault.fromMemory(error, s"pc ${context.pc}, lane $lane: ")
        )
        .map { bytes =>
          destinations.zipWithIndex.map { case (destination, index) =>
            val value =
              LittleEndian.decode(
                bytes,
                index * width.byteCount.raw,
                width.byteCount
              )
            VMWrite.Register(lane, destination, loadBits(value, width, signed))
          }
        }
    }

  private def storeCoherentVector(
      context: VMInstructionContext,
      lane: Int,
      address: Long,
      sources: Vec[Value],
      width: Int where (Between[8, 64] & MultipleOf[8])
  ): Either[VMFault, Vec[VMWrite]] =
    if (sources.nonEmpty && sources.size * width.byteCount.raw <= 8) {
      sources.zipWithIndex.foldLeft[Either[VMFault, Long]](Right(0L)) { case (result, (source, index)) =>
        for {
          packed <- result
          value  <- context.read(lane, source)
        } yield packed | (truncate(value, width) << (index * width.raw))
      }.map { packed =>
        val packedWidth = (sources.size * width.raw).unsafe[Int where (Between[8, 64] & MultipleOf[8])]
        Vec(VMWrite.Memory(lane, GuestAddress(address), packedWidth, packed))
      }

    } else {
      sources.traverse(source => context.read(lane, source))
        .map(values => Vec(VMWrite.MemoryVector(lane, GuestAddress(address), width, values)))
    }

  /** The common nvcc path is a dense register-only instruction stream. Keep the checked, staged path below for memory
    * and rendezvous operations, but avoid allocating an Either and a VMWrite for every lane of an arithmetic
    * instruction. Reads still happen before the destination is changed, matching the staged path's aliasing rule.
    */
  private def executeFast(
      instruction: VMIR.Instruction,
      context: VMInstructionContext
  ): Boolean = instruction match {
    case VMIR.Instruction.Move(destination, Value.Function(function), _) =>
      context.active.foreachLane(lane => context.state.functionRegisters(lane)(destination) = function)
      true
    case VMIR.Instruction.Move(destination, source, width) =>
      val destinationIndex = VMRegisterFile.index(destination)
      context.active.foreachLane(lane =>
        writeRegister(
          context.state.registers(lane),
          destination,
          destinationIndex,
          truncate(readFast(context, lane, source), width)
        )
      )
      true
    case VMIR.Instruction.MovePredicate(destination, source) =>
      val destinationIndex = VMPredicateFile.index(destination)
      context.active.foreachLane(lane =>
        writePredicate(
          context.state.predicates(lane),
          destination,
          destinationIndex,
          readFast(context, lane, source) != 0L
        )
      )
      true
    case VMIR.Instruction.MoveSplit(destinations, source, partWidth) =>
      val lowIndex  = if (destinations(0) == "_") -1 else VMRegisterFile.index(destinations(0))
      val highIndex = if (destinations(1) == "_") -1 else VMRegisterFile.index(destinations(1))
      context.active.foreachLane { lane =>
        val value = readFast(context, lane, source)
        if (destinations(0) != "_")
          writeRegister(context.state.registers(lane), destinations(0), lowIndex, truncate(value, partWidth))
        if (destinations(1) != "_")
          writeRegister(
            context.state.registers(lane),
            destinations(1),
            highIndex,
            truncate(value >>> partWidth.raw, partWidth)
          )
      }
      true
    case VMIR.Instruction.MoveCombine(destination, sources, partWidth) =>
      val destinationIndex = VMRegisterFile.index(destination)
      val combinedWidth    = partWidth.combined
      context.active.foreachLane { lane =>
        val low  = readFast(context, lane, sources(0))
        val high = readFast(context, lane, sources(1))
        writeRegister(
          context.state.registers(lane),
          destination,
          destinationIndex,
          truncate((low & truncate(-1L, partWidth)) | (truncate(high, partWidth) << partWidth.raw), combinedWidth)
        )
      }
      true
    case VMIR.Instruction.IntegerUnary(operation, destination, source, width) =>
      val destinationIndex = VMRegisterFile.index(destination)
      context.active.foreachLane { lane =>
        writeRegister(
          context.state.registers(lane),
          destination,
          destinationIndex,
          integerUnaryResult(operation, readFast(context, lane, source), width)
        )
      }
      true
    case VMIR.Instruction.BitFind(destination, source, width, signed, shiftAmount) =>
      val destinationIndex = VMRegisterFile.index(destination)
      context.active.foreachLane { lane =>
        writeRegister(
          context.state.registers(lane),
          destination,
          destinationIndex,
          bitFind(readFast(context, lane, source), width, signed, shiftAmount)
        )
      }
      true
    case VMIR.Instruction.DotProduct4(destination, left, right, addend, leftSigned, rightSigned) =>
      val destinationIndex = VMRegisterFile.index(destination)
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
    case VMIR.Instruction.IntegerAddWithCarry(destination, left, right, width, carryIn, carryOut) =>
      val destinationIndex = VMRegisterFile.index(destination)
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
    case VMIR.Instruction.IntegerSubWithBorrow(destination, left, right, width, borrowIn, borrowOut) =>
      val destinationIndex = VMRegisterFile.index(destination)
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
    case VMIR.Instruction.IntegerBinary(operation, destination, left, right, width) =>
      val destinationIndex = VMRegisterFile.index(destination)
      context.active.foreachLane { lane =>
        val leftValue  = readFast(context, lane, left)
        val rightValue = readFast(context, lane, right)
        writeRegister(
          context.state.registers(lane),
          destination,
          destinationIndex,
          integerBinaryResult(operation, leftValue, rightValue, width)
        )
      }
      true
    case VMIR.Instruction.FunnelShift(destination, low, high, amount, left, clamp) =>
      val destinationIndex = VMRegisterFile.index(destination)
      context.active.foreachLane { lane =>
        writeRegister(
          context.state.registers(lane),
          destination,
          destinationIndex,
          funnelShiftResult(
            readFast(context, lane, low),
            readFast(context, lane, high),
            readFast(context, lane, amount),
            left,
            clamp
          )
        )
      }
      true
    case VMIR.Instruction.Convert(destination, source, sourceType, destinationType, rounding) =>
      val destinationIndex  = VMRegisterFile.index(destination)
      val sourceFormat      = sourceType.format
      val destinationFormat = destinationType.format
      context.active.foreachLane(lane =>
        writeRegister(
          context.state.registers(lane),
          destination,
          destinationIndex,
          convert(readFast(context, lane, source), sourceFormat, destinationFormat, rounding)
        )
      )
      true
    case VMIR.Instruction.MultiplyAdd(destination, left, right, addend, operation) =>
      val destinationIndex = VMRegisterFile.index(destination)
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
    case VMIR.Instruction.BitFieldInsert(destination, insertValue, baseValue, bitPosition, bitLength, width) =>
      val destinationIndex = VMRegisterFile.index(destination)
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
    case VMIR.Instruction.BitMask(destination, start, widthValue, mode) =>
      val destinationIndex = VMRegisterFile.index(destination)
      context.active.foreachLane { lane =>
        writeRegister(
          context.state.registers(lane),
          destination,
          destinationIndex,
          bitMask(readFast(context, lane, start), readFast(context, lane, widthValue), mode)
        )
      }
      true
    case VMIR.Instruction.BytePermute(destination, left, right, control, mode) =>
      val destinationIndex = VMRegisterFile.index(destination)
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
    case VMIR.Instruction.FloatBinary(operation, destination, left, right, width, control) =>
      val destinationIndex = VMRegisterFile.index(destination)
      context.active.foreachLane { lane =>
        val leftBits  = floatInput(readFast(context, lane, left), width, control)
        val rightBits = floatInput(readFast(context, lane, right), width, control)
        val result    = floatResult(floatBinaryBits(operation, leftBits, rightBits, width, control), width, control)
        writeRegister(context.state.registers(lane), destination, destinationIndex, result)
      }
      true
    case VMIR.Instruction.FloatBinaryPacked(operation, destination, left, right, bf16) =>
      val destinationIndex = VMRegisterFile.index(destination)
      context.active.foreachLane { lane =>
        writeRegister(
          context.state.registers(lane),
          destination,
          destinationIndex,
          packedBinaryBits(operation, readFast(context, lane, left), readFast(context, lane, right), bf16)
        )
      }
      true
    case VMIR.Instruction.FloatUnary(operation, destination, source, width, control) =>
      val destinationIndex = VMRegisterFile.index(destination)
      context.active.foreachLane { lane =>
        val sourceValue = floatInput(readFast(context, lane, source), width, control)
        val result      = floatResult(floatUnaryBits(operation, sourceValue, width), width, control)
        writeRegister(context.state.registers(lane), destination, destinationIndex, result)
      }
      true
    case VMIR.Instruction.FusedMultiplyAdd(destination, left, right, addend, width, control, rounding) =>
      val destinationIndex = VMRegisterFile.index(destination)
      context.active.foreachLane { lane =>
        val leftBits   = floatInput(readFast(context, lane, left), width, control)
        val rightBits  = floatInput(readFast(context, lane, right), width, control)
        val addendBits = floatInput(readFast(context, lane, addend), width, control)
        val result     = floatResult(
          if (rounding == ConversionRounding.RN || width.raw == 16)
            fusedMultiplyAddBits(leftBits, rightBits, addendBits, width)
          else directedFusedMultiplyAddBits(leftBits, rightBits, addendBits, width, rounding),
          width,
          control
        )
        writeRegister(context.state.registers(lane), destination, destinationIndex, result)
      }
      true
    case VMIR.Instruction.FusedMultiplyAddPacked(destination, left, right, addend, bf16, saturate) =>
      val destinationIndex = VMRegisterFile.index(destination)
      context.active.foreachLane { lane =>
        writeRegister(
          context.state.registers(lane),
          destination,
          destinationIndex,
          packedFmaBits(
            readFast(context, lane, left),
            readFast(context, lane, right),
            readFast(context, lane, addend),
            bf16,
            saturate
          )
        )
      }
      true
    case VMIR.Instruction.FusedMultiplyAddBf16(destination, left, right, addend) =>
      val destinationIndex = VMRegisterFile.index(destination)
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
    case VMIR.Instruction.SetPredicate(destination, comparison, format, left, right, flushSubnormal) =>
      val destinationIndex = VMPredicateFile.index(destination)
      context.active.foreachLane { lane =>
        val value = compare(
          flushFloat32(readFast(context, lane, left), flushSubnormal),
          flushFloat32(readFast(context, lane, right), flushSubnormal),
          comparison,
          format
        )
        writePredicate(context.state.predicates(lane), destination, destinationIndex, value)
      }
      true
    case VMIR.Instruction.PredicateCompare(destination, comparison, left, right) =>
      val destinationIndex = VMPredicateFile.index(destination)
      val leftIndex        = VMPredicateFile.index(left)
      val rightIndex       = VMPredicateFile.index(right)
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
    case VMIR.Instruction.PredicateUnary(destination, source) =>
      val destinationIndex = VMPredicateFile.index(destination)
      val sourceIndex      = VMPredicateFile.index(source)
      context.active.foreachLane { lane =>
        writePredicate(
          context.state.predicates(lane),
          destination,
          destinationIndex,
          !readPredicateFast(context.state.predicates(lane), source, sourceIndex)
        )
      }
      true
    case VMIR.Instruction.PredicateBinary(operation, destination, left, right) =>
      val destinationIndex = VMPredicateFile.index(destination)
      val leftIndex        = VMPredicateFile.index(left)
      val rightIndex       = VMPredicateFile.index(right)
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
    case VMIR.Instruction.Select(destination, whenTrue, whenFalse, predicate, width) =>
      val destinationIndex = VMRegisterFile.index(destination)
      val predicateIndex   = VMPredicateFile.index(predicate)
      context.active.foreachLane { lane =>
        val selected = if (readPredicateFast(context.state.predicates(lane), predicate, predicateIndex))
          readFast(context, lane, whenTrue)
        else readFast(context, lane, whenFalse)
        writeRegister(context.state.registers(lane), destination, destinationIndex, truncate(selected, width))
      }
      true
    case _ => false
  }

  private def writeRegister(registers: VMRegisterFile, name: NonEmpty[String], index: Int, value: Long): Unit =
    if (index >= 0) registers.updateSlot(index, value) else registers.update(name, value)

  private def writePredicate(predicates: VMPredicateFile, name: NonEmpty[String], index: Int, value: Boolean): Unit =
    if (index >= 0) predicates.updateSlot(index, value) else predicates.update(name, value)

  private def readPredicateFast(predicates: VMPredicateFile, name: NonEmpty[String], index: Int): Boolean =
    if (index >= 0) predicates.getSlot(index).getOrElse(false) else predicates.get(name).getOrElse(false)

  private def executeBuiltin(
      context: VMInstructionContext,
      lane: Int,
      name: NonEmpty[String],
      returnParameters: Vec[NonEmpty[String]],
      parameters: Vec[NonEmpty[String]]
  ): Option[Either[VMFault, Unit]] = {
    def parameter(index: Int, width: Int where Between[0, 8] = 8): Long =
      if (index < parameters.size)
        context.state.parameters(lane).getOrElse(
          parameters(index),
          context.state.readParameterBytes(lane, parameters(index), 0, width)
        )
      else 0L

    def returnValue(value: Long): Unit =
      returnParameters.headOption.foreach(name => context.state.parameters(lane)(name) = value)

    def writeLocalPointer(index: Int, value: Long): Unit =
      if (index < parameters.size)
        context.state.writeLocal(lane, parameter(index), 32, value)

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
            val allocation = parameter(0) match {
              case size if size < 1L || size > Int.MaxValue.toLong =>
                Left(VMFault.InvalidState(s"invalid malloc size: $size"))
              case size =>
                memory.allocate(size.toInt.unsafe[Int > 0]).leftMap(error =>
                  VMFault.fromMemory(error)
                )
            }
            allocation match {
              case Left(fault)    => Some(Left(fault))
              case Right(address) =>
                returnValue(address.raw)
                Some(Right(()))
            }
          case _ => Some(Left(VMFault.InvalidState("device malloc requires dynamic address memory")))
        }
      case "free" =>
        context.memory match {
          case memory: DeviceAllocationMemory =>
            val _ = memory.free(GuestAddress(parameter(0)))
            Some(Right(()))
          case _ => Some(Left(VMFault.InvalidState("device free requires dynamic address memory")))
        }
      case "cudaLaunchDevice" =>
        Some(executeDeviceLaunch(context, lane, returnParameters, parameters, parameter(_, _)))
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

  // Dynamic-parallelism arguments are decoded into a mutable launch frame before the child dispatch.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def executeDeviceLaunch(
      context: VMInstructionContext,
      lane: Int,
      returnParameters: Vec[NonEmpty[String]],
      parameters: Vec[NonEmpty[String]],
      parameter: (Int, Int where Between[0, 8]) => Long
  ): Either[VMFault, Unit] =
    if (parameters.size < 6)
      Left(VMFault.InvalidState(
        s"cudaLaunchDevice expects six parameters, found ${parameters.size}"
      ))
    else {
      val target = context.state.functionParameters(lane).get(parameters(0))
      val child  = target.flatMap(context.kernel.functions.get).toRight(
        VMFault.InvalidState(
          s"cudaLaunchDevice target is not a lowered entry: ${target.getOrElse("<unknown>")}"
        )
      )
      child match {
        case Left(fault)     => Left(fault)
        case Right(function) =>
          val buffer = parameter(1, 8)
          val grid   =
            (0 until 3).map(index =>
              context.state.readParameterBytes(lane, parameters(2), index * 4, 4) &
                0xffffffffL
            )
          val block =
            (0 until 3).map(index =>
              context.state.readParameterBytes(lane, parameters(3), index * 4, 4) &
                0xffffffffL
            )
          val childArguments                   = mutable.Map.empty[NonEmpty[String], Argument]
          var argumentFailure: Option[VMFault] = None
          var index                            = 0
          while (index < function.parameters.size && argumentFailure.isEmpty) {
            val width  = function.parameterWidths(index)
            val offset = function.parameterOffsets(index)
            context.state.readParameterBufferBytes(buffer + offset.raw.toLong, width.raw) match {
              case Some(bytes) =>
                childArguments(function.parameters(index)) =
                  Argument(Vec.unsafeWrapArray(bytes).unsafe)
              case None => argumentFailure = Some(VMFault.InvalidState(
                  s"cudaLaunchDevice parameter buffer 0x${java.lang.Long.toUnsignedString(buffer, 16)} is out of range"
                ))
            }
            index += 1
          }
          argumentFailure match {
            case Some(fault) => Left(fault)
            case None        =>
              val geometry = for {
                groupCount <- dimensions(grid(0).toInt, grid(1).toInt, grid(2).toInt)
                groupSize  <- dimensions(block(0).toInt, block(1).toInt, block(2).toInt)
                value      <- LaunchGeometry.forLaunch(groupSize, groupCount)
              } yield value
              geometry.leftMap(error => VMFault.InvalidState(error)) match {
                case Left(fault)  => Left(fault)
                case Right(value) =>
                  VMInterpreter.execute(
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
                    context.workgroups,
                    context.state.parameterBuffers.toMap,
                    None
                  )
              }
          }
      } match {
        case Left(fault) => Left(fault)
        case Right(_)    =>
          returnParameters.headOption.foreach(name => context.state.parameters(lane)(name) = 0L)
          Right(())
      }
    }

  private def readFast(context: VMInstructionContext, lane: Int, value: Value): Long = value match {
    case Value.Register(name)       => context.state.registers(lane)(name)
    case Value.RegisterSlot(index)  => context.state.registers(lane).getSlot(index)
    case Value.Predicate(name)      => if (context.state.predicates(lane).get(name).getOrElse(false)) 1L else 0L
    case Value.PredicateSlot(index) => if (context.state.predicates(lane).getSlot(index).getOrElse(false)) 1L else 0L
    case Value.Symbol(name)         =>
      context.state.parameterAddresses.get(name)
        .orElse(context.kernel.sharedSymbols.get(name))
        .orElse(context.kernel.globalSymbols.get(name).map(_.raw))
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
    case Value.ThreadId(axis)        =>
      val sizeX = context.geometry.groupSize.x.raw
      val index = axis match {
        case Axis.X => lane           % sizeX
        case Axis.Y => (lane / sizeX) % context.geometry.groupSize.y.raw
        case Axis.Z => lane / sizeX / context.geometry.groupSize.y.raw
      }
      index.toLong
    case Value.BlockId(axis)  => axis.select(context.group).raw.toLong
    case Value.BlockDim(axis) => axis.select(context.geometry.groupSize).raw.toLong
    case Value.GridDim(axis)  => axis.select(context.geometry.groupCount).raw.toLong
    case Value.Function(name) => throw IllegalStateException(s"function symbol $name is not numeric")
  }

  private def dimensions(x: Int, y: Int, z: Int): Either[NonEmpty[String], Dimensions] =
    (
      x.refine[Int > 0].left.map[NonEmpty[String]](_ => s"invalid x dimension: $x"),
      y.refine[Int > 0].left.map[NonEmpty[String]](_ => s"invalid y dimension: $y"),
      z.refine[Int > 0].left.map[NonEmpty[String]](_ => s"invalid z dimension: $z")
    ).mapN((x, y, z) => Dimensions(x, y, z))

  // Preserve the first lane fault without evaluating later lanes after failure.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def forEveryLane(context: VMInstructionContext)(
      function: Int => Either[VMFault, Unit]
  ): Either[VMFault, Unit] = {
    var failure: Option[VMFault] = None
    context.active.foreachLane { lane =>
      if (failure.isEmpty) function(lane).left.foreach(fault => failure = Some(fault))
    }
    failure.toLeft(())
  }

  // The emulator keeps per-lane local storage in the low virtual-address
  // range; host-visible CUDA allocations are projected above 4 GiB.
  private def isLocalAddress(address: Long): Boolean =
    java.lang.Long.compareUnsigned(address, VMAddressSpace.LocalLimit) < 0 && !isSharedAddress(address)

  private def isSharedAddress(address: Long): Boolean = VMAddressSpace.isShared(address)

  // An atomic without an explicit state space uses generic addressing, so its
  // target follows the computed address. An explicit .shared or .global wins.
  private def atomicTargetsShared(space: VMIR.AtomicSpace, address: Long): Boolean =
    space == VMIR.AtomicSpace.Shared || (space == VMIR.AtomicSpace.Generic && isSharedAddress(address))

  private def integerUnaryResult(
      operation: IntegerUnaryOperation,
      value: Long,
      width: Int where (Between[8, 64] & MultipleOf[8])
  ): Long = {
    val result = operation match {
      case IntegerUnaryOperation.Negate           => -value
      case IntegerUnaryOperation.Absolute         => Math.abs(signExtend(truncate(value, width), width))
      case IntegerUnaryOperation.BitwiseNot       => ~value
      case IntegerUnaryOperation.PopulationCount  => bitCount(value, width).toLong
      case IntegerUnaryOperation.LeadingZeroCount =>
        if (width.raw == 32) Integer.numberOfLeadingZeros(Bits.signed32(value)).toLong
        else java.lang.Long.numberOfLeadingZeros(value).toLong
      case IntegerUnaryOperation.BitReverse => reverseBits(value, width)
    }
    truncate(result, width)
  }

  // The wrap form takes the shift modulo 32; the clamp form saturates it at 32,
  // so a clamp of 32 selects the opposite half of the 64-bit pair rather than
  // leaving the input unchanged.
  private def funnelShiftResult(low: Long, high: Long, amount: Long, left: Boolean, clamp: Boolean): Long = {
    val shift = if (clamp) Math.min(amount & 0xffffffffL, 32L).toInt else (amount & 31L).toInt
    val pair  = (low & 0xffffffffL) | ((high & 0xffffffffL) << 32)
    if (left) low32((pair << shift) >>> 32).toLong
    else low32(pair >>> shift).toLong
  }

  private def integerBinaryResult(
      operation: IntegerOperation,
      left: Long,
      right: Long,
      width: Int where (Between[8, 64] & MultipleOf[8])
  ): Long = {
    val result = operation match {
      case IntegerOperation.Add                    => left + right
      case IntegerOperation.Subtract               => left - right
      case IntegerOperation.BitwiseOr              => left | right
      case IntegerOperation.BitwiseAnd             => left & right
      case IntegerOperation.BitwiseXor             => left ^ right
      case IntegerOperation.MultiplyLow            => left * right
      case IntegerOperation.MultiplyHigh(signed)   => multiplyHigh(left, right, width, signed)
      case IntegerOperation.Divide(signed)         => divideInteger(left, right, width, signed)
      case IntegerOperation.Remainder(signed)      => remainderInteger(left, right, width, signed)
      case IntegerOperation.MultiplyWideUnsigned16 => Bits.low16(left).toLong * Bits.low16(right).toLong
      case IntegerOperation.MultiplyWideSigned16   =>
        signExtend(
          left,
          16
        ) * signExtend(right, 16)
      case IntegerOperation.MultiplyWideUnsigned32 => low32(left).toLong * low32(right).toLong
      case IntegerOperation.MultiplyWideSigned32   => left.toInt.toLong * right.toInt.toLong
      case IntegerOperation.Minimum(signed)        =>
        if (signed) Math.min(signExtend(left, width), signExtend(right, width))
        else if (width.raw == 64) {
          if (java.lang.Long.compareUnsigned(left, right) <= 0) left else right
        } else Math.min(truncate(left, width), truncate(right, width))
      case IntegerOperation.Maximum(signed) =>
        if (signed) Math.max(signExtend(left, width), signExtend(right, width))
        else if (width.raw == 64) {
          if (java.lang.Long.compareUnsigned(left, right) >= 0) left else right
        } else Math.max(truncate(left, width), truncate(right, width))
      case IntegerOperation.ShiftLeft =>
        if ((right & 0xffffffffL) >= width.raw) 0L else left << right.toInt
      case IntegerOperation.ShiftRightLogical =>
        if ((right & 0xffffffffL) >= width.raw) 0L else truncate(left, width) >>> right.toInt
      case IntegerOperation.ShiftRightArithmetic =>
        signExtend(left, width) >> Math.min(right & 0xffffffffL, width.raw.toLong - 1L).toInt
    }
    truncate(result, width)
  }

  private def flushFloat32(bits: Long, enabled: Boolean): Long =
    if (enabled && (bits & 0x7f800000L) == 0) bits & 0x80000000L else bits

  private def floatInput(bits: Long, width: Int in (16, 32, 64), control: FloatControl): Long = {
    val exponent = width.raw match {
      case 16 => 0x7c00L
      case 32 => 0x7f800000L
      case _  => 0x7ff0000000000000L
    }
    if (control.flushSubnormal && (bits & exponent) == 0) bits & (1L << (width.raw - 1)) else bits
  }

  private def floatResult(bits: Long, width: Int in (16, 32, 64), control: FloatControl): Long = {
    val bounded = if (!control.saturate) bits
    else {
      val value = IEEE754.toDouble(bits, width)
      if (value.isNaN || value <= 0.0) 0L
      else if (value >= 1.0) IEEE754.fromDouble(1.0, width)
      else bits
    }
    floatInput(bounded, width, control)
  }

  private def floatExtremum(
      operation: FloatOperation,
      leftBits: Long,
      rightBits: Long,
      width: Int in (16, 32, 64),
      control: FloatControl
  ): Long = {
    val sign            = 1L << (width.raw - 1)
    val a               = if (control.absolute) leftBits & ~sign else leftBits
    val b               = if (control.absolute) rightBits & ~sign else rightBits
    val left            = IEEE754.toDouble(a, width)
    val right           = IEEE754.toDouble(b, width)
    val numberOperation = operation == FloatOperation.MinimumNumber || operation == FloatOperation.MaximumNumber
    val nan             = (left.isNaN && right.isNaN) ||
      ((!numberOperation || control.propagateNaN) && (left.isNaN || right.isNaN))
    val result = if (nan) IEEE754.fromDouble(Double.NaN, width)
    else if (left.isNaN) b
    else if (right.isNaN) a
    else if (left == 0.0 && right == 0.0)
      if (operation == FloatOperation.Minimum || operation == FloatOperation.MinimumNumber) (a | b) & sign
      else (a & b) & sign
    else if ((left < right) == (operation == FloatOperation.Minimum || operation == FloatOperation.MinimumNumber)) a
    else b
    if (control.xorSign && !nan) (result & ~sign) | ((leftBits ^ rightBits) & sign) else result
  }

  private def floatBinaryBits(
      operation: FloatOperation,
      leftBits: Long,
      rightBits: Long,
      width: Int in (16, 32, 64),
      control: FloatControl
  ): Long = {
    val extremum = operation == FloatOperation.Minimum || operation == FloatOperation.Maximum ||
      operation == FloatOperation.MinimumNumber || operation == FloatOperation.MaximumNumber
    if (
      extremum && (control.absolute || control.xorSign || control.propagateNaN ||
        operation == FloatOperation.MinimumNumber || operation == FloatOperation.MaximumNumber)
    )
      floatExtremum(operation, leftBits, rightBits, width, control)
    else if (width.raw == 16) {
      val left   = IEEE754.binary16ToFloat(Binary16.low(leftBits))
      val right  = IEEE754.binary16ToFloat(Binary16.low(rightBits))
      val result = floatOperation(operation, left, right)
      IEEE754.floatToBinary16(result).toLong
    } else if (width.raw == 32) {
      val left   = IEEE754.binary32ToFloat(Binary32.low(leftBits))
      val right  = IEEE754.binary32ToFloat(Binary32.low(rightBits))
      val result = floatOperation(operation, left, right)
      IEEE754.floatToBinary32(result).toLong
    } else {
      val left   = IEEE754.binary64ToDouble(leftBits)
      val right  = IEEE754.binary64ToDouble(rightBits)
      val result = doubleOperation(operation, left, right)
      IEEE754.doubleToBinary64(result)
    }

  }

  private def floatOperation(operation: FloatOperation, left: Float, right: Float): Float = operation match {
    case FloatOperation.Add           => left + right
    case FloatOperation.Subtract      => left - right
    case FloatOperation.Multiply      => left * right
    case FloatOperation.Divide        => left / right
    case FloatOperation.Minimum       => Math.min(left, right)
    case FloatOperation.MinimumNumber => if (left.isNaN) right else if (right.isNaN) left else Math.min(left, right)
    case FloatOperation.Maximum       => Math.max(left, right)
    case FloatOperation.MaximumNumber => if (left.isNaN) right else if (right.isNaN) left else Math.max(left, right)
    case FloatOperation.CopySign      => Math.copySign(right, left)
  }

  private def doubleOperation(operation: FloatOperation, left: Double, right: Double): Double = operation match {
    case FloatOperation.Add           => left + right
    case FloatOperation.Subtract      => left - right
    case FloatOperation.Multiply      => left * right
    case FloatOperation.Divide        => left / right
    case FloatOperation.Minimum       => Math.min(left, right)
    case FloatOperation.MinimumNumber => if (left.isNaN) right else if (right.isNaN) left else Math.min(left, right)
    case FloatOperation.Maximum       => Math.max(left, right)
    case FloatOperation.MaximumNumber => if (left.isNaN) right else if (right.isNaN) left else Math.max(left, right)
    case FloatOperation.CopySign      => Math.copySign(right, left)
  }

  private def directedFusedMultiplyAddBits(
      leftBits: Long,
      rightBits: Long,
      addendBits: Long,
      width: Int in (16, 32, 64),
      rounding: ConversionRounding
  ): Long = {
    val nearestBits                = fusedMultiplyAddBits(leftBits, rightBits, addendBits, width)
    def decode(bits: Long): Double =
      if (width.raw == 32) IEEE754.binary32ToFloat(Binary32.low(bits)).toDouble
      else IEEE754.binary64ToDouble(bits)
    val a = decode(leftBits); val b = decode(rightBits); val c = decode(addendBits)
    if (!a.isFinite || !b.isFinite || !c.isFinite) nearestBits
    else {
      // Exact comparison is necessary: a binary64 intermediate can lose a tiny
      // addend which still changes a directed binary32 rounding decision.
      val exact      = java.math.BigDecimal(a).multiply(java.math.BigDecimal(b)).add(java.math.BigDecimal(c))
      val nearest    = decode(nearestBits)
      val comparison = if (nearest.isPosInfinity) 1
      else if (nearest.isNegInfinity) -1
      else java.math.BigDecimal(nearest).compareTo(exact)
      val direction = rounding match {
        case ConversionRounding.RP if comparison < 0                       => 1
        case ConversionRounding.RM if comparison > 0                       => -1
        case ConversionRounding.RZ if exact.signum() > 0 && comparison > 0 => -1
        case ConversionRounding.RZ if exact.signum() < 0 && comparison < 0 => 1
        case _                                                             => 0
      }
      val positiveZeroSum = (a == 0.0 || b == 0.0) && c == 0.0 &&
        (java.lang.Double.doubleToRawLongBits(a) ^ java.lang.Double.doubleToRawLongBits(b)) >= 0 &&
        java.lang.Double.doubleToRawLongBits(c) >= 0
      if (exact.signum() == 0 && rounding == ConversionRounding.RM && !positiveZeroSum)
        if (width.raw == 32) 0x80000000L else Long.MinValue
      else if (direction == 0) nearestBits
      else if (width.raw == 32)
        IEEE754.floatToBinary32(if (direction > 0) Math.nextUp(nearest.toFloat) else Math.nextDown(nearest.toFloat)).raw
      else IEEE754.doubleToBinary64(if (direction > 0) Math.nextUp(nearest) else Math.nextDown(nearest))
    }
  }

  private def fusedMultiplyAddBits(
      leftBits: Long,
      rightBits: Long,
      addendBits: Long,
      width: Int in (16, 32, 64)
  ): Long =
    if (width.raw == 16)
      // Keep the binary16 product/addend boundary through the final half
      // rounding: a binary32 intermediate can round onto a half-ULP tie.
      IEEE754.doubleToBinary16(
        Math.fma(
          IEEE754.binary16ToFloat(Binary16.low(leftBits)).toDouble,
          IEEE754.binary16ToFloat(Binary16.low(rightBits)).toDouble,
          IEEE754.binary16ToFloat(Binary16.low(addendBits)).toDouble
        )
      ).toLong
    else if (width.raw == 32)
      IEEE754.floatToBinary32(
        Math.fma(
          IEEE754.binary32ToFloat(Binary32.low(leftBits)),
          IEEE754.binary32ToFloat(Binary32.low(rightBits)),
          IEEE754.binary32ToFloat(Binary32.low(addendBits))
        )
      ).toLong
    else
      IEEE754.doubleToBinary64(
        Math.fma(
          IEEE754.binary64ToDouble(leftBits),
          IEEE754.binary64ToDouble(rightBits),
          IEEE754.binary64ToDouble(addendBits)
        )
      )

  // Sign-bit operations return before the floating conversion path.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return"))
  private def floatUnaryBits(operation: FloatUnaryOperation, bits: Long, width: Int in (16, 32, 64)): Long =
    if (width.raw == 16) {
      val halfBits = truncate(bits, 16)
      operation match {
        case FloatUnaryOperation.Negate   => halfBits ^ 0x8000L
        case FloatUnaryOperation.Absolute => halfBits & 0x7fffL
        case _                            =>
          val value  = IEEE754.binary16ToFloat(Binary16.low(halfBits))
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
          IEEE754.floatToBinary16(result).toLong
      }
    } else if (width.raw == 32) {
      val value  = IEEE754.binary32ToFloat(Binary32.low(bits))
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
      IEEE754.floatToBinary32(result).toLong
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
    if (bf16)
      IEEE754.binary32ToFloat(Binary32.low(truncate(bits, 16) << 16))
    else IEEE754.binary16ToFloat(Binary16.low(bits))

  private def packedFloatBits(value: Float, bf16: Boolean): Long =
    if (bf16) bfloat16FromFloat(value, Some(ConversionRounding.RN)) else IEEE754.floatToBinary16(value).toLong

  private def packedFmaBits(left: Long, right: Long, addend: Long, bf16: Boolean, saturate: Boolean): Long = {
    def lane(a: Long, b: Long, c: Long): Long = {
      val result = if (bf16)
        Math.fma(packedFloatValue(a, bf16), packedFloatValue(b, bf16), packedFloatValue(c, bf16))
      else IEEE754.binary16ToFloat(Binary16.low(fusedMultiplyAddBits(a, b, c, 16: Int in (16, 32, 64)))).toFloat
      val bounded = if (!saturate) result else if (result.isNaN || result <= 0.0f) 0.0f else math.min(result, 1.0f)
      packedFloatBits(bounded, bf16)
    }
    lane(left, right, addend) | (lane(left >>> 16, right >>> 16, addend >>> 16) << 16)
  }

  private def packedBinaryBits(operation: FloatOperation, left: Long, right: Long, bf16: Boolean): Long = {
    val low =
      packedFloatBits(floatOperation(operation, packedFloatValue(left, bf16), packedFloatValue(right, bf16)), bf16)
    val high = packedFloatBits(
      floatOperation(operation, packedFloatValue(left >>> 16, bf16), packedFloatValue(right >>> 16, bf16)),
      bf16
    )
    low | (high << 16)
  }

  private def bf16FmaBits(left: Long, right: Long, addend: Long): Long =
    bfloat16FromFloat(
      Math.fma(
        IEEE754.binary32ToFloat(Binary32.low(truncate(left, 16) << 16)),
        IEEE754.binary32ToFloat(Binary32.low(truncate(right, 16) << 16)),
        IEEE754.binary32ToFloat(Binary32.low(truncate(addend, 16) << 16))
      ),
      Some(ConversionRounding.RN)
    )

  private def loadBits(value: Long, width: Int where (Between[8, 64] & MultipleOf[8]), signed: Boolean): Long = {
    val bits = truncate(value, width)
    if (signed) signExtend(bits, width) else bits
  }

  private def bitFind(
      value: Long,
      width: Int where (Between[8, 64] & MultipleOf[8]),
      signed: Boolean,
      shiftAmount: Boolean
  ): Long = {
    val bits   = truncate(value, width)
    val search =
      if (signed && ((bits >>> (width.raw - 1)) & 1L) != 0L) truncate(~bits, width)
      else bits
    val position =
      if (search == 0L) -1
      else if (width.raw == 32) 31 - Integer.numberOfLeadingZeros(search.toInt)
      else 63 - java.lang.Long.numberOfLeadingZeros(search)
    if (position < 0) 0xffffffffL
    else if (shiftAmount) (width.raw - 1 - position).toLong
    else position.toLong
  }

  // Fixed-width packed dot product is a measured arithmetic hot loop.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def dotProduct4(
      left: Long,
      right: Long,
      addend: Long,
      leftSigned: Boolean,
      rightSigned: Boolean
  ): Long = {
    var result = signExtend(
      truncate(addend, 32),
      32
    )
    var index = 0
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

  private def extractBits(
      source: Long,
      start: Long,
      count: Long,
      width: Int where (Between[8, 64] & MultipleOf[8]),
      signed: Boolean
  ): Long = {
    val sourceBits = truncate(source, width)
    val bitStart   = (start & 0xffL).toInt
    val bitCount   = (count & 0xffL).toInt
    if (bitCount == 0) 0L
    else if (bitStart >= width.raw)
      if (signed && (sourceBits & (1L << (width.raw - 1))) != 0L) truncate(-1L, width) else 0L
    else {
      val extractedWidth    = Math.min(bitCount, width.raw - bitStart)
      val mask              = if (extractedWidth == 64) -1L else (1L << extractedWidth) - 1L
      val extracted         = (sourceBits >>> bitStart) & mask
      val extractedBitWidth = extractedWidth.unsafe[Int where Between[1, 64]]
      truncate(if (signed) Bits.signExtend(extracted, extractedBitWidth) else extracted, width)
    }
  }

  private def bitMask(start: Long, count: Long, mode: BitMaskMode): Long = {
    val rawStart         = start & 0xffffffffL
    val rawCount         = count & 0xffffffffL
    val start1           = rawStart & 0x1fL
    val count1           = rawCount & 0x1fL
    val positionOverflow = mode == BitMaskMode.Clamp && rawStart >= 32L
    val widthOverflow    = mode == BitMaskMode.Clamp && rawCount >= 32L
    val sum              = start1 + count1
    val mask0            =
      if (positionOverflow) 0L else truncate(-1L << start1.toInt, 32)
    val mask1 =
      if (sum >= 32L || positionOverflow || widthOverflow) 0L
      else if (count1 == 0L) 0xffffffffL
      else truncate(-1L << sum.toInt, 32)
    truncate(mask0 & ~mask1, 32)
  }

  private def multiplyHigh(
      left: Long,
      right: Long,
      width: Int where (Between[8, 64] & MultipleOf[8]),
      signed: Boolean
  ): Long = {
    val leftValue =
      if (signed) BigInt(signExtend(left, width)) else BigInt(java.lang.Long.toUnsignedString(truncate(left, width)))
    val rightValue =
      if (signed) BigInt(signExtend(right, width)) else BigInt(java.lang.Long.toUnsignedString(truncate(right, width)))
    ((leftValue * rightValue) >> width.raw).longValue
  }

  private def unsignedMultiplyHigh(left: Long, right: Long): Long = {
    val mask      = 0xffffffffL
    val leftLow   = left & mask
    val leftHigh  = left >>> 32
    val rightLow  = right & mask
    val rightHigh = right >>> 32
    val low       = leftLow * rightLow
    val middle1   = leftHigh * rightLow + (low >>> 32)
    val middle2   = leftLow * rightHigh + (middle1 & mask)
    leftHigh * rightHigh + (middle1 >>> 32) + (middle2 >>> 32)
  }

  private def addUnsigned(
      left: Long,
      right: Long,
      carryIn: Boolean,
      width: Int where (Between[8, 64] & MultipleOf[8])
  ): (Long, Boolean) = {
    val first         = left + right
    val firstOverflow = java.lang.Long.compareUnsigned(first, left) < 0
    val result        = if (carryIn) first + 1L else first
    val nextOverflow  = carryIn && java.lang.Long.compareUnsigned(result, first) < 0
    if (width.raw == 64) result  -> (firstOverflow || nextOverflow)
    else truncate(result, width) -> ((result >>> width.raw) != 0L)
  }

  def integerMultiplyAdd(
      left: Long,
      right: Long,
      addend: Long,
      operation: IntegerMultiplyAddOperation,
      carryIn: Boolean
  ): (Long, Boolean) = operation match {
    case IntegerMultiplyAddOperation.Low(operandWidth, resultWidth, usesCarry, _) =>
      val leftValue    = truncate(left, operandWidth)
      val rightValue   = truncate(right, operandWidth)
      val productLow   = leftValue * rightValue
      val productHigh  = unsignedMultiplyHigh(leftValue, rightValue)
      val addendValue  = truncate(addend, resultWidth)
      val first        = productLow + addendValue
      val firstCarry   = java.lang.Long.compareUnsigned(first, productLow) < 0
      val useCarry     = usesCarry && carryIn
      val result       = if (useCarry) first + 1L else first
      val secondCarry  = useCarry && java.lang.Long.compareUnsigned(result, first) < 0
      val overflowHigh = productHigh + (if (firstCarry || secondCarry) 1L else 0L)
      val overflow     =
        if (resultWidth.raw == 64) overflowHigh != 0L
        else overflowHigh != 0L || (result >>> resultWidth.raw) != 0L
      truncate(result, resultWidth) -> overflow
    case IntegerMultiplyAddOperation.High(operandWidth, resultWidth, usesCarry, _) =>
      val leftValue   = truncate(left, operandWidth)
      val rightValue  = truncate(right, operandWidth)
      val productLow  = leftValue * rightValue
      val productHigh = unsignedMultiplyHigh(leftValue, rightValue)
      val high        =
        if (operandWidth.raw == 64) productHigh
        else truncate(
          (productLow >>> operandWidth.raw) | (productHigh << (64 - operandWidth.raw)),
          operandWidth
        )
      val addendValue = truncate(addend, resultWidth)
      addUnsigned(high, addendValue, usesCarry && carryIn, resultWidth)
  }

  private def integerAddWithCarry(
      left: Long,
      right: Long,
      width: Int where (Between[8, 64] & MultipleOf[8]),
      usesCarry: Boolean,
      carryIn: Boolean
  ): (Long, Boolean) = {
    val sum =
      BigInt(java.lang.Long.toUnsignedString(truncate(left, width))) +
        BigInt(java.lang.Long.toUnsignedString(truncate(right, width))) +
        (if (usesCarry && carryIn) 1 else 0)
    (truncate(sum.longValue, width), (sum >> width.raw) != 0)
  }

  private def unsignedInteger(
      value: Long,
      width: Int where (Between[8, 64] & MultipleOf[8])
  ): BigInt = BigInt(java.lang.Long.toUnsignedString(truncate(value, width)))

  private def integerSubWithBorrow(
      left: Long,
      right: Long,
      width: Int where (Between[8, 64] & MultipleOf[8]),
      usesBorrow: Boolean,
      borrowIn: Boolean
  ): (Long, Boolean) = {
    val difference =
      BigInt(java.lang.Long.toUnsignedString(truncate(left, width))) -
        BigInt(java.lang.Long.toUnsignedString(truncate(right, width))) -
        (if (usesBorrow && borrowIn) 1 else 0)
    (truncate(difference.longValue, width), difference < 0)
  }

  private def insertBits(
      insert: Long,
      base: Long,
      start: Long,
      count: Long,
      width: Int where (Between[8, 64] & MultipleOf[8])
  ): Long = {
    val bitStart = (start & 0xffL).toInt
    val bitCount = (count & 0xffL).toInt
    if (bitCount == 0 || bitStart >= width.raw) truncate(base, width)
    else {
      val insertedWidth    = Math.min(bitCount, width.raw - bitStart)
      val fieldMask        = if (insertedWidth == 64) -1L else ((1L << insertedWidth) - 1L) << bitStart
      val insertedBitWidth = insertedWidth.unsafe[Int where Between[1, 64]]
      truncate((base & ~fieldMask) | ((insert & Bits.truncate(-1L, insertedBitWidth)) << bitStart), width)
    }
  }

  private def permuteBytes(left: Long, right: Long, control: Long, mode: BytePermuteMode): Long = {
    val source = Array.tabulate(8) { index =>
      val value = if (index < 4) left else right
      ((value >>> (8 * (index & 3))) & 0xffL).toInt
    }
    val selector      = (control & 3L).toInt
    val selectedBytes = mode match {
      case BytePermuteMode.Default => Array.tabulate(4)(index => ((control >>> (4 * index)) & 7L).toInt)
      case BytePermuteMode.F4E     => Array.tabulate(4)(index => (selector + index) & 7)
      case BytePermuteMode.B4E     => Array.tabulate(4)(index => (selector - index) & 7)
      case BytePermuteMode.RC8     => Array.fill(4)(selector)
      case BytePermuteMode.ECL     => Array.tabulate(4)(index => Math.max(index, selector))
      case BytePermuteMode.ECR     => Array.tabulate(4)(index => Math.min(index, selector))
      case BytePermuteMode.RC16    => Array.tabulate(4)(index => ((selector & 1) * 2) + (index & 1))
    }
    val result = (0 until 4).foldLeft(0L) { (bits, index) =>
      val selection  = selectedBytes(index)
      val selected   = source(selection)
      val signExtend = mode == BytePermuteMode.Default && ((control >>> (4 * index)) & 8L) != 0L
      val byte       = if (signExtend) { if ((selected & 0x80) != 0) 0xff else 0 }
      else selected
      bits | (byte.toLong << (8 * index))
    }
    truncate(result, 32)
  }

  private def divideInteger(
      left: Long,
      right: Long,
      width: Int where (Between[8, 64] & MultipleOf[8]),
      signed: Boolean
  ): Long = {
    val leftBits  = truncate(left, width)
    val rightBits = truncate(right, width)
    if (rightBits == 0L) 0L
    else if (signed) {
      val dividend = signExtend(leftBits, width)
      val divisor  = signExtend(rightBits, width)
      if (dividend == Long.MinValue && divisor == -1L) Long.MinValue
      else dividend / divisor
    } else if (width.raw == 64) java.lang.Long.divideUnsigned(leftBits, rightBits)
    else leftBits / rightBits
  }

  private def remainderInteger(
      left: Long,
      right: Long,
      width: Int where (Between[8, 64] & MultipleOf[8]),
      signed: Boolean
  ): Long = {
    val leftBits  = truncate(left, width)
    val rightBits = truncate(right, width)
    if (rightBits == 0L) 0L
    else if (signed) {
      val dividend = signExtend(leftBits, width)
      val divisor  = signExtend(rightBits, width)
      if (dividend == Long.MinValue && divisor == -1L) 0L
      else dividend % divisor
    } else if (width.raw == 64) java.lang.Long.remainderUnsigned(leftBits, rightBits)
    else leftBits % rightBits
  }

  private def compare(
      left: Long,
      right: Long,
      comparison: Comparison,
      format: NumericFormat
  ): Boolean = {
    val width      = format.width
    val leftValue  = truncate(left, width)
    val rightValue = truncate(right, width)
    format match {
      case NumericFormat.IEEE(bits) => compareFloating(
          IEEE754.toDouble(leftValue, bits),
          IEEE754.toDouble(rightValue, bits),
          comparison
        )
      case NumericFormat.BF16 => compareFloating(
          IEEE754.binary32ToFloat(Binary32.low(leftValue << 16)).toDouble,
          IEEE754.binary32ToFloat(Binary32.low(rightValue << 16)).toDouble,
          comparison
        )
      case NumericFormat.Signed(_) => compareOrder(
          java.lang.Long.compare(signExtend(leftValue, width), signExtend(rightValue, width)),
          false,
          comparison
        )
      case NumericFormat.Bits(_) | NumericFormat.Unsigned(_) => compareOrder(
          if (width.raw == 64) java.lang.Long.compareUnsigned(leftValue, rightValue)
          else java.lang.Long.compare(leftValue, rightValue),
          false,
          comparison
        )
    }
  }

  private def compareFloating(left: Double, right: Double, comparison: Comparison): Boolean =
    compareOrder(if (left < right) -1 else if (left > right) 1 else 0, left.isNaN || right.isNaN, comparison)

  private def compareOrder(order: Int, unordered: Boolean, comparison: Comparison): Boolean =
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

  private def comparePredicates(left: Boolean, right: Boolean, comparison: Comparison): Boolean = comparison match {
    case Comparison.Ordered | Comparison.Unordered                              => false
    case Comparison.Equal | Comparison.EqualUnordered                           => left == right
    case Comparison.NotEqual | Comparison.NotEqualUnordered                     => left != right
    case Comparison.LessThan | Comparison.LessThanUnordered                     => !left && right
    case Comparison.LessThanOrEqual | Comparison.LessThanOrEqualUnordered       => !left || right
    case Comparison.GreaterThan | Comparison.GreaterThanUnordered               => left && !right
    case Comparison.GreaterThanOrEqual | Comparison.GreaterThanOrEqualUnordered => left || !right
  }

  // The bf16 branch terminates before the shared conversion matrix.

  private def exactIntegerOfBinary(bits: Long, width: Int in (32, 64)): BigInt = {
    val (exponentWidth, fractionWidth, bias) =
      if (width.raw == 32) (8, 23, 127) else (11, 52, 1023)
    val signMask     = 1L << (width.raw - 1)
    val fractionMask = (1L << fractionWidth) - 1L
    val exponentMask = (1L << exponentWidth) - 1L
    val negative     = (bits & signMask) != 0L
    val exponent     = ((bits >>> fractionWidth) & exponentMask).toInt
    val fraction     = bits & fractionMask
    val significand  = if (exponent == 0) BigInt(fraction) else BigInt(fraction | (1L << fractionWidth))
    val shift        = (if (exponent == 0) 1 - bias else exponent - bias) - fractionWidth
    val magnitude    = if (shift >= 0) significand << shift else significand >> -shift
    if (negative) -magnitude else magnitude
  }

  private def integerToFloat(value: BigInt, width: Int in (32, 64), rounding: Option[ConversionRounding]): Long = {
    val nearestBits = if (width.raw == 32) java.lang.Float.floatToRawIntBits(value.toFloat).toLong & 0xffffffffL
    else java.lang.Double.doubleToRawLongBits(value.toDouble)
    val nearestExact = exactIntegerOfBinary(nearestBits, width)
    val direction    = rounding match {
      case Some(ConversionRounding.RP | ConversionRounding.RPI)                     => 1
      case Some(ConversionRounding.RM | ConversionRounding.RMI)                     => -1
      case Some(ConversionRounding.RZ | ConversionRounding.RZI) if value.signum > 0 => -1
      case Some(ConversionRounding.RZ | ConversionRounding.RZI) if value.signum < 0 => 1
      case _                                                                        => 0
    }
    val adjustUp   = direction > 0 && nearestExact < value
    val adjustDown = direction < 0 && nearestExact > value
    if (!adjustUp && !adjustDown) nearestBits
    else if (width.raw == 32) {
      val nearest = java.lang.Float.intBitsToFloat(nearestBits.toInt)
      java.lang.Float.floatToRawIntBits(if (adjustUp) Math.nextUp(nearest)
      else Math.nextDown(nearest)).toLong & 0xffffffffL
    } else {
      val nearest = java.lang.Double.longBitsToDouble(nearestBits)
      java.lang.Double.doubleToRawLongBits(if (adjustUp) Math.nextUp(nearest) else Math.nextDown(nearest))
    }
  }

  private def convert(
      value: Long,
      sourceType: NumericFormat,
      destinationType: NumericFormat,
      rounding: Option[ConversionRounding]
  ): Long = sourceType match {
    case NumericFormat.IEEE(bits) =>
      convertFloating(IEEE754.toDouble(value, bits), destinationType, rounding)
    case NumericFormat.BF16 =>
      convertFloating(
        IEEE754.binary32ToFloat(Binary32.low(truncate(value, sourceType.width) << 16)).toDouble,
        destinationType,
        rounding
      )
    case source: (NumericFormat.Bits | NumericFormat.Signed | NumericFormat.Unsigned) =>
      convertInteger(value, source, destinationType, rounding)
  }

  private def convertFloating(
      floating: Double,
      destination: NumericFormat,
      rounding: Option[ConversionRounding]
  ): Long = destination match {
    case NumericFormat.BF16       => bfloat16FromFloat(floating.toFloat, rounding)
    case NumericFormat.IEEE(bits) =>
      // Integral rounding precedes format conversion for the `i` variants.
      val integral = rounding match {
        case Some(ConversionRounding.RZI) => true
        case Some(ConversionRounding.RMI) => true
        case Some(ConversionRounding.RPI) => true
        case Some(ConversionRounding.RNI) => true
        case _                            => false
      }
      val input = rounding match {
        case Some(ConversionRounding.RZI) => if (floating < 0.0) Math.ceil(floating) else Math.floor(floating)
        case Some(ConversionRounding.RMI) => Math.floor(floating)
        case Some(ConversionRounding.RPI) => Math.ceil(floating)
        case Some(ConversionRounding.RNI) => Math.rint(floating)
        case _                            => floating
      }
      if (bits.raw == 32 && !integral && !input.isNaN) {
        val nearest = input.toFloat
        val rounded = rounding match {
          case Some(ConversionRounding.RP) if nearest.toDouble < input              => Math.nextUp(nearest)
          case Some(ConversionRounding.RM) if nearest.toDouble > input              => Math.nextDown(nearest)
          case Some(ConversionRounding.RZ) if input > 0 && nearest.toDouble > input => Math.nextDown(nearest)
          case Some(ConversionRounding.RZ) if input < 0 && nearest.toDouble < input => Math.nextUp(nearest)
          case _                                                                    => nearest
        }
        IEEE754.floatToBinary32(rounded).raw
      } else IEEE754.fromDouble(input, bits)
    case _ =>
      val rounded = rounding match {
        case Some(ConversionRounding.RN | ConversionRounding.RNI) => Math.rint(floating)
        case Some(ConversionRounding.RM | ConversionRounding.RMI) => Math.floor(floating)
        case Some(ConversionRounding.RP | ConversionRounding.RPI) => Math.ceil(floating)
        case _ => if (floating < 0) Math.ceil(floating) else Math.floor(floating)
      }
      truncate(rounded.toLong, destination.width)
  }

  private def convertInteger(
      value: Long,
      source: NumericFormat.Bits | NumericFormat.Signed | NumericFormat.Unsigned,
      destination: NumericFormat,
      rounding: Option[ConversionRounding]
  ): Long = {
    val sourceBits = truncate(value, source.width)
    val integer    = source match {
      case NumericFormat.Signed(_) => signExtend(sourceBits, source.width)
      case _                       => sourceBits
    }
    val unsigned64 = source match {
      case NumericFormat.Unsigned(bits) => bits.raw == 64 && sourceBits < 0L
      case _                            => false
    }
    destination match {
      case NumericFormat.IEEE(bits) => bits.raw match {
          case 32 => integerToFloat(integerValue(integer, unsigned64), 32, rounding)
          case 64 => integerToFloat(integerValue(integer, unsigned64), 64, rounding)
          case _  => IEEE754.fromDouble(integerAsDouble(integer, unsigned64), bits)
        }
      case NumericFormat.BF16 => bfloat16FromFloat(integerAsDouble(integer, unsigned64).toFloat, rounding)
      case _                  => truncate(integer, destination.width)
    }
  }

  private def integerValue(value: Long, unsigned64: Boolean): BigInt =
    if (unsigned64) BigInt(java.lang.Long.toUnsignedString(value)) else BigInt(value)

  private def integerAsDouble(value: Long, unsigned64: Boolean): Double =
    if (unsigned64) integerValue(value, true).toDouble else value.toDouble

  private def bfloat16FromFloat(value: Float, rounding: Option[ConversionRounding]): Long = {
    val bits    = IEEE754.floatToBinary32(value)
    val upper   = (bits.toLong >>> 16) & 0xffffL
    val lower   = bits.toLong & 0xffffL
    val rounded = rounding match {
      case Some(ConversionRounding.RZ) | Some(ConversionRounding.RZI)                                => upper
      case Some(ConversionRounding.RM) | Some(ConversionRounding.RMI) if value < 0.0f && lower != 0L => upper + 1L
      case Some(ConversionRounding.RP) | Some(ConversionRounding.RPI) if value > 0.0f && lower != 0L => upper + 1L
      case _ if lower > 0x8000L || (lower == 0x8000L && (upper & 1L) != 0L)                          => upper + 1L
      case _                                                                                         => upper
    }
    truncate(rounded, 16)
  }

}
