package net.kurobako.gemstone.isa.ptx.v9_3

import Program.*

import VMIR.*
import VMExecution.*

import net.kurobako.gemstone.Vec

import net.kurobako.gemstone.binary.Binary16
import net.kurobako.gemstone.binary.Binary32
import net.kurobako.gemstone.binary.BitWidth as BinaryBitWidth
import net.kurobako.gemstone.binary.Bits
import net.kurobako.gemstone.binary.Bits.low32
import net.kurobako.gemstone.binary.FloatingPointWidth
import net.kurobako.gemstone.binary.IEEE754
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.binary.ScalarByteWidth
import net.kurobako.gemstone.isa.KernelName
import net.kurobako.gemstone.vm.LaunchGeometry
import net.kurobako.gemstone.vm.LaunchGeometry.Axis
import net.kurobako.gemstone.vm.LaunchGeometry.Dimensions
import net.kurobako.gemstone.memory.DeviceAllocationByteSize
import net.kurobako.gemstone.memory.GuestAddress
import net.kurobako.gemstone.memory.DeviceAllocationMemory
import net.kurobako.gemstone.memory.MemoryValueByteWidth

import cats.syntax.all.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import scala.collection.mutable

private object VMSemantics {

  private trait SemanticWidth[A] {
    def bitWidth(value: A): BinaryBitWidth
  }

  private given SemanticWidth[BitWidth] with {
    override def bitWidth(value: BitWidth): BinaryBitWidth = value.asBitWidth
  }

  private given SemanticWidth[PartWidth] with {
    override def bitWidth(value: PartWidth): BinaryBitWidth = value.asBitWidth
  }

  private inline def truncate[A](value: Long, width: A)(using semantic: SemanticWidth[A]): Long =
    Bits.truncate(value, semantic.bitWidth(width))

  private inline def signExtend[A](value: Long, width: A)(using semantic: SemanticWidth[A]): Long =
    Bits.signExtend(value, semantic.bitWidth(width))

  private inline def bitCount[A](value: Long, width: A)(using
      semantic: SemanticWidth[A]
  ): Int =
    Bits.bitCount(value, semantic.bitWidth(width))

  private inline def reverseBits[A](value: Long, width: A)(using semantic: SemanticWidth[A]): Long =
    Bits.reverseBits(value, semantic.bitWidth(width))

  private inline def byteCount(width: BitWidth) = Bits.byteCount(width.asByteAlignedBitWidth)

  @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
  private inline def memoryByteCount(width: BitWidth): MemoryValueByteWidth =
    MemoryValueByteWidth.assume(byteCount(width).value)

  private def stageResult(
      effects: VMStateEffects,
      result: Either[ExecutionFault, VMStateWrite]
  ): Either[ExecutionFault, Unit] =
    result match {
      case Left(fault)  => Left(fault)
      case Right(write) =>
        effects.stage(write)
        Right(())
    }

  private def stageRegisterResult(
      effects: VMStateEffects,
      lane: Int,
      destination: RegisterName,
      result: Either[ExecutionFault, Long]
  ): Either[ExecutionFault, Unit] =
    result match {
      case Left(fault)  => Left(fault)
      case Right(value) =>
        effects.stageRegister(lane, destination, value)
        Right(())
    }

  private def stageResults(
      effects: VMStateEffects,
      result: Either[ExecutionFault, Vec[VMStateWrite]]
  ): Either[ExecutionFault, Unit] =
    result match {
      case Left(fault)   => Left(fault)
      case Right(writes) =>
        writes.foreach(write => effects.stage(write))
        Right(())
    }

  extension (instruction: VMIR.Instruction) {
    // A PTX instruction updates per-lane machine state; staged mutable maps avoid allocating immutable copies.
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    def execute(context: VMExecutionContext, effects: VMStateEffects): Either[ExecutionFault, Step] =
      if (executeFast(instruction, context)) Right(Step.Advance)
      else instruction match {
        case VMIR.Instruction.LoadParameter(destinations, parameter, offset, elementWidth, signed) =>
          val elementBytes   = byteCount(elementWidth)
          val argumentValues = context.arguments.get(parameter).map { argument =>
            if (offset < 0 || offset > argument.bytes.length - elementBytes.value * destinations.size)
              Left(ExecutionFault.InvalidState(
                s"parameter $parameter load at $offset exceeds ${argument.bytes.length} bytes"
              ))
            else
              Right(Array.tabulate(destinations.size) { index =>
                val byteOffset = offset + index * elementBytes.value
                loadBits(LittleEndian.decode(argument.bytes, byteOffset, elementBytes), elementWidth, signed)
              })
          }
          var failure: Option[ExecutionFault] = None
          context.active.foreachLane { lane =>
            if (failure.isEmpty)
              context.state.parameters(lane).get(parameter) match {
                case Some(value) if offset == 0 && destinations.size == 1 =>
                  effects.stageRegister(lane, destinations.head, loadBits(value, elementWidth, signed))
                case Some(_) =>
                  failure = Some(ExecutionFault.InvalidState(
                    s"unsupported local parameter load: $parameter+$offset"
                  ))
                case None =>
                  argumentValues match {
                    case None =>
                      failure = Some(ExecutionFault.InvalidState(
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
              context.state.readParameterBuffer(rawAddress, byteCount(width)) match {
                case Some(value)                         => Right(loadBits(value, width, signed))
                case None if isSharedAddress(rawAddress) =>
                  Right(loadBits(context.state.readShared(rawAddress, width), width, signed))
                case None if isLocalAddress(rawAddress) =>
                  Right(loadBits(context.state.readLocal(lane, rawAddress, width), width, signed))
                case None =>
                  context.memory
                    .readLittleEndian(GuestAddress(rawAddress), memoryByteCount(width))
                    .leftMap(error =>
                      ExecutionFault.Memory(
                        s"pc ${context.pc.value}, lane $lane: ${error.message}"
                      )
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
                val elementAddress = rawAddress + index.toLong * byteCount(width).value.toLong
                context.state.readParameterBuffer(elementAddress, byteCount(width)) match {
                  case Some(loaded) =>
                    Right(VMStateWrite.Register(lane, destination, loadBits(loaded, width, signed)))
                  case None if isSharedAddress(elementAddress) =>
                    Right(VMStateWrite.Register(
                      lane,
                      destination,
                      loadBits(context.state.readShared(elementAddress, width), width, signed)
                    ))
                  case None if isLocalAddress(elementAddress) =>
                    Right(VMStateWrite.Register(
                      lane,
                      destination,
                      loadBits(context.state.readLocal(lane, elementAddress, width), width, signed)
                    ))
                  case None =>
                    context.memory
                      .readLittleEndian(GuestAddress(elementAddress), memoryByteCount(width))
                      .leftMap(error =>
                        ExecutionFault.Memory(
                          s"pc ${context.pc.value}, lane $lane: ${error.message}"
                        )
                      )
                      .map(loaded =>
                        VMStateWrite.Register(
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
                effects.stage(VMStateWrite.ParameterBytes(lane, parameter, offset, elementWidth, stored))
                if (offset == 0)
                  effects.stage(VMStateWrite.Parameter(lane, parameter, stored))
                source match {
                  case Value.Register(name) if context.state.functionRegisters(lane).contains(name) =>
                    effects.stage(VMStateWrite.FunctionParameter(
                      lane,
                      parameter,
                      context.state.functionRegisters(lane)(name)
                    ))
                  case Value.RegisterSlot(index) =>
                    val name = VMRegisterFile.name(index)
                    context.state.functionRegisters(lane).get(name).foreach(function =>
                      effects.stage(VMStateWrite.FunctionParameter(lane, parameter, function))
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
              Vec(VMStateWrite.Register(lane, destination, truncate(value, width)))
            )(name =>
              Vec(
                VMStateWrite.Register(lane, destination, truncate(value, width)),
                VMStateWrite.Predicate(lane, name, valid)
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
              Vec(VMStateWrite.Register(lane, destination, truncate(value, width)))
            )(name =>
              Vec(
                VMStateWrite.Register(lane, destination, truncate(value, width)),
                VMStateWrite.Predicate(lane, name, valid)
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
              Vec(VMStateWrite.Register(lane, destination, truncate(value, width)))
            )(name =>
              Vec(
                VMStateWrite.Register(lane, destination, truncate(value, width)),
                VMStateWrite.Predicate(lane, name, valid)
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
              Vec(VMStateWrite.Register(lane, destination, truncate(value, width)))
            )(name =>
              Vec(
                VMStateWrite.Register(lane, destination, truncate(value, width)),
                VMStateWrite.Predicate(lane, name, valid)
              )
            )
            stageResults(effects, writes)
          }.map(_ => Step.Advance)
        case VMIR.Instruction.VoteBallot(destination, predicate, memberMask) =>
          forEveryLane(context) { lane =>
            val write = context.read(lane, memberMask).flatMap { maskValue =>
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
              }.map(ballot => VMStateWrite.Register(lane, destination, truncate(ballot, BitWidth(32))))
            }
            stageResult(effects, write)
          }.map(_ => Step.Advance)
        case VMIR.Instruction.VotePredicate(destination, predicate, memberMask, operation) =>
          forEveryLane(context) { lane =>
            val write = context.read(lane, memberMask).flatMap { maskValue =>
              val warpBase = lane & ~31
              (0 until 32).foldLeft[Either[ExecutionFault, Vec[Boolean]]](Right(Vec.empty)) {
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
                  case VoteOperation.All     => values.nonEmpty && values.forall(value => value)
                  case VoteOperation.Any     => values.exists(value => value)
                  case VoteOperation.Uniform => values.distinct.size <= 1
                }
                VMStateWrite.Predicate(lane, destination, selected)
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
              VMStateWrite.Register(lane, destination, result)
            }
            stageResult(effects, write)
          }.map(_ => Step.Advance)
        case other => executeMemoryAndControl(other, context, effects)
      }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:GemstoneNoAssume"))
  private def executeMemoryAndControl(
      instruction: VMIR.Instruction,
      context: VMExecutionContext,
      effects: VMStateEffects
  ): Either[ExecutionFault, Step] = instruction match {
    case VMIR.Instruction.LoadGlobal(destination, address, width, signed) =>
      prepareGlobalReads(context, address, byteCount(width).value, false).flatMap { _ =>
        var failure: Option[ExecutionFault] = None
        context.active.foreachLane { lane =>
          if (failure.isEmpty) {
            val rawAddress = readFast(context, lane, address)
            context.state.readParameterBuffer(rawAddress, byteCount(width)) match {
              case Some(value) =>
                effects.stageRegister(lane, destination, loadBits(value, width, signed))
              case None =>
                context.memory.readLittleEndian(GuestAddress(rawAddress), memoryByteCount(width)) match {
                  case Left(error) =>
                    failure = Some(ExecutionFault.Memory(
                      s"pc ${context.pc.value}, lane $lane: ${error.message}"
                    ))
                  case Right(value) =>
                    effects.stageRegister(lane, destination, loadBits(value, width, signed))
                }
            }
          }
        }
        failure match {
          case Some(fault) => Left(fault)
          case None        => Right(Step.Advance)
        }
      }
    case VMIR.Instruction.LoadGeneric(destination, address, width, signed) =>
      prepareGlobalReads(context, address, byteCount(width).value, true).flatMap { _ =>
        var failure: Option[ExecutionFault] = None
        context.active.foreachLane { lane =>
          if (failure.isEmpty) {
            val rawAddress = readFast(context, lane, address)
            context.state.readParameterBuffer(rawAddress, byteCount(width)) match {
              case Some(value) =>
                effects.stageRegister(lane, destination, loadBits(value, width, signed))
              case None if isSharedAddress(rawAddress) =>
                effects.stageRegister(
                  lane,
                  destination,
                  loadBits(context.state.readShared(rawAddress, width), width, signed)
                )
              case None if isLocalAddress(rawAddress) =>
                effects.stageRegister(
                  lane,
                  destination,
                  loadBits(context.state.readLocal(lane, rawAddress, width), width, signed)
                )
              case None =>
                context.memory.readLittleEndian(GuestAddress(rawAddress), memoryByteCount(width)) match {
                  case Left(error) =>
                    failure = Some(ExecutionFault.Memory(
                      s"pc ${context.pc.value}, lane $lane: ${error.message}"
                    ))
                  case Right(value) =>
                    effects.stageRegister(lane, destination, loadBits(value, width, signed))
                }
            }
          }
        }
        failure match {
          case Some(fault) => Left(fault)
          case None        => Right(Step.Advance)
        }
      }
    case VMIR.Instruction.LoadGlobalVector(destinations, address, width, signed) =>
      prepareGlobalReads(context, address, destinations.size * byteCount(width).value, false).flatMap { _ =>
        forEveryLane(context) { lane =>
          val writes = context.read(lane, address).flatMap { rawAddress =>
            if (coherentGlobalVector(context, rawAddress, destinations.size, width))
              loadCoherentVector(context, lane, rawAddress, destinations, width, signed)
            else destinations.zipWithIndex.traverse { case (destination, index) =>
              context.memory
                .readLittleEndian(
                  GuestAddress(rawAddress + index.toLong * byteCount(width).value.toLong),
                  memoryByteCount(width)
                )
                .leftMap(error =>
                  ExecutionFault.Memory(
                    s"pc ${context.pc.value}, lane $lane: ${error.message}"
                  )
                )
                .map(value =>
                  VMStateWrite.Register(
                    lane,
                    destination,
                    loadBits(value, width, signed)
                  )
                )
            }
          }
          stageResults(effects, writes)
        }.map(_ => Step.Advance)
      }
    case VMIR.Instruction.LoadGenericVector(destinations, address, width, signed) =>
      prepareGlobalReads(context, address, destinations.size * byteCount(width).value, true).flatMap { _ =>
        forEveryLane(context) { lane =>
          val writes = context.read(lane, address).flatMap { rawAddress =>
            if (coherentGlobalVector(context, rawAddress, destinations.size, width))
              loadCoherentVector(context, lane, rawAddress, destinations, width, signed)
            else destinations.zipWithIndex.traverse { case (destination, index) =>
              val elementAddress = rawAddress + index.toLong * byteCount(width).value.toLong
              context.state.readParameterBuffer(elementAddress, byteCount(width)) match {
                case Some(value) => Right(VMStateWrite.Register(
                    lane,
                    destination,
                    loadBits(value, width, signed)
                  ))
                case None if isSharedAddress(elementAddress) =>
                  Right(VMStateWrite.Register(
                    lane,
                    destination,
                    loadBits(context.state.readShared(elementAddress, width), width, signed)
                  ))
                case None if isLocalAddress(elementAddress) =>
                  Right(VMStateWrite.Register(
                    lane,
                    destination,
                    loadBits(context.state.readLocal(lane, elementAddress, width), width, signed)
                  ))
                case None =>
                  context.memory
                    .readLittleEndian(GuestAddress(elementAddress), memoryByteCount(width))
                    .leftMap(error =>
                      ExecutionFault.Memory(
                        s"pc ${context.pc.value}, lane $lane: ${error.message}"
                      )
                    )
                    .map(value =>
                      VMStateWrite.Register(
                        lane,
                        destination,
                        loadBits(value, width, signed)
                      )
                    )
              }
            }
          }
          stageResults(effects, writes)
        }.map(_ => Step.Advance)
      }
    case VMIR.Instruction.StoreGlobal(address, source, width) =>
      context.active.foreachLane { lane =>
        val rawAddress = readFast(context, lane, address)
        val value      = readFast(context, lane, source)
        if (context.state.parameterBufferRange(rawAddress, byteCount(width).value).nonEmpty)
          effects.stage(VMStateWrite.ParameterBuffer(rawAddress, width, value))
        else effects.stageMemory(lane, GuestAddress(rawAddress), width, value)
      }
      Right(Step.Advance)
    case VMIR.Instruction.StoreGeneric(address, source, width) =>
      context.active.foreachLane { lane =>
        val rawAddress = readFast(context, lane, address)
        val value      = readFast(context, lane, source)
        if (context.state.parameterBufferRange(rawAddress, byteCount(width).value).nonEmpty)
          effects.stage(VMStateWrite.ParameterBuffer(rawAddress, width, value))
        else if (isSharedAddress(rawAddress)) effects.stage(VMStateWrite.Shared(lane, rawAddress, width, value))
        else if (isLocalAddress(rawAddress)) effects.stage(VMStateWrite.Local(lane, rawAddress, width, value))
        else effects.stageMemory(lane, GuestAddress(rawAddress), width, value)
      }
      Right(Step.Advance)
    case VMIR.Instruction.StoreGenericVector(address, sources, width) =>
      forEveryLane(context) { lane =>
        val writes = context.read(lane, address).flatMap { rawAddress =>
          if (coherentGlobalVector(context, rawAddress, sources.size, width))
            storeCoherentVector(context, lane, rawAddress, sources, width)
          else sources.zipWithIndex.traverse { case (source, index) =>
            context.read(lane, source).map { value =>
              val elementAddress = rawAddress + index.toLong * byteCount(width).value.toLong
              if (isSharedAddress(elementAddress))
                VMStateWrite.Shared(lane, elementAddress, width, value)
              else if (isLocalAddress(elementAddress))
                VMStateWrite.Local(lane, elementAddress, width, value)
              else
                VMStateWrite.Memory(lane, GuestAddress(elementAddress), width, value)
            }
          }
        }
        stageResults(effects, writes)
      }.map(_ => Step.Advance)
    case VMIR.Instruction.LoadShared(destination, address, width, signed) =>
      forEveryLane(context) { lane =>
        stageRegisterResult(
          effects,
          lane,
          destination,
          context.read(lane, address).map(rawAddress =>
            loadBits(context.state.readShared(rawAddress, width), width, signed)
          )
        )
      }.map(_ => Step.Advance)
    case VMIR.Instruction.LoadSharedVector(destinations, address, width, signed) =>
      forEveryLane(context) { lane =>
        val writes = context.read(lane, address).map { rawAddress =>
          destinations.zipWithIndex.map { case (destination, index) =>
            VMStateWrite.Register(
              lane,
              destination,
              loadBits(
                context.state.readShared(rawAddress + index.toLong * byteCount(width).value.toLong, width),
                width,
                signed
              )
            )
          }
        }
        stageResults(effects, writes)
      }.map(_ => Step.Advance)
    case VMIR.Instruction.LoadLocal(destination, address, width, signed) =>
      forEveryLane(context) { lane =>
        stageRegisterResult(
          effects,
          lane,
          destination,
          context.read(lane, address).map(rawAddress =>
            loadBits(context.state.readLocal(lane, rawAddress, width), width, signed)
          )
        )
      }.map(_ => Step.Advance)
    case VMIR.Instruction.LoadLocalVector(destinations, address, width, signed) =>
      forEveryLane(context) { lane =>
        val writes = context.read(lane, address).map { rawAddress =>
          destinations.zipWithIndex.map { case (destination, index) =>
            VMStateWrite.Register(
              lane,
              destination,
              loadBits(
                context.state.readLocal(lane, rawAddress + index.toLong * byteCount(width).value.toLong, width),
                width,
                signed
              )
            )
          }
        }
        stageResults(effects, writes)
      }.map(_ => Step.Advance)
    case VMIR.Instruction.StoreShared(address, source, width) =>
      forEveryLane(context) { lane =>
        val write = for {
          rawAddress <- context.read(lane, address)
          value      <- context.read(lane, source)
        } yield VMStateWrite.Shared(lane, rawAddress, width, value)
        stageResult(effects, write)
      }.map(_ => Step.Advance)
    case VMIR.Instruction.StoreSharedVector(address, sources, width) =>
      forEveryLane(context) { lane =>
        val writes = context.read(lane, address).flatMap { rawAddress =>
          sources.zipWithIndex.traverse { case (source, index) =>
            context.read(lane, source).map(value =>
              VMStateWrite.Shared(
                lane,
                rawAddress + index.toLong * byteCount(width).value.toLong,
                width,
                value
              )
            )
          }
        }
        stageResults(effects, writes)
      }.map(_ => Step.Advance)
    case VMIR.Instruction.StoreLocal(address, source, width) =>
      forEveryLane(context) { lane =>
        val write = for {
          rawAddress <- context.read(lane, address)
          value      <- context.read(lane, source)
        } yield VMStateWrite.Local(lane, rawAddress, width, value)
        stageResult(effects, write)
      }.map(_ => Step.Advance)
    case VMIR.Instruction.StoreLocalVector(address, sources, width) =>
      forEveryLane(context) { lane =>
        val writes = context.read(lane, address).flatMap { rawAddress =>
          sources.zipWithIndex.traverse { case (source, index) =>
            context.read(lane, source).map(value =>
              VMStateWrite.Local(
                lane,
                rawAddress + index.toLong * byteCount(width).value.toLong,
                width,
                value
              )
            )
          }
        }
        stageResults(effects, writes)
      }.map(_ => Step.Advance)
    case VMIR.Instruction.AsyncCopy(destination, source, size) =>
      forEveryLane(context) { lane =>
        val writes = for {
          destinationAddress <- context.read(lane, destination)
          sourceAddress      <- context.read(lane, source)
          pending            <- Vec.from(0 until size by 8).traverse { offset =>
            val width = BitWidth.assume(Math.min(8, size - offset) * 8)
            context.memory
              .readLittleEndian(GuestAddress(sourceAddress + offset.toLong), memoryByteCount(width))
              .leftMap(error =>
                ExecutionFault.Memory(
                  s"pc ${context.pc.value}, lane $lane: ${error.message}"
                )
              )
              .map(value =>
                VMStateWrite.Shared(
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
      val values                          = mutable.Map.empty[Int, Long]
      val masks                           = mutable.Map.empty[Int, Long]
      var failure: Option[ExecutionFault] = None
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
              failure = Some(ExecutionFault.InvalidState(
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
            VMStateWrite.AtomicSharedAdd(lane, destination, rawAddress, width, sourceValue)
          else
            VMStateWrite.AtomicMemoryAdd(
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
            VMStateWrite.AtomicSharedCompareExchange(lane, destination, rawAddress, width, comparatorValue, sourceValue)
          else
            VMStateWrite.AtomicMemoryCompareExchange(
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
            VMStateWrite.AtomicSharedExchange(lane, destination, rawAddress, width, sourceValue)
          else
            VMStateWrite.AtomicMemoryExchange(lane, destination, GuestAddress(rawAddress), width, sourceValue)
        stageResult(effects, write)
      }.map(_ => Step.Advance)
    case VMIR.Instruction.AtomicBinary(destination, address, source, width, space, operation) =>
      forEveryLane(context) { lane =>
        val write = for {
          rawAddress  <- context.read(lane, address)
          sourceValue <- context.read(lane, source)
        } yield
          if (atomicTargetsShared(space, rawAddress))
            VMStateWrite.AtomicSharedBinary(lane, destination, rawAddress, width, sourceValue, operation)
          else
            VMStateWrite.AtomicMemoryBinary(
              lane,
              destination,
              GuestAddress(rawAddress),
              width,
              sourceValue,
              operation
            )
        stageResult(effects, write)
      }.map(_ => Step.Advance)
    case VMIR.Instruction.AtomicMin(destination, address, source, width, space) =>
      forEveryLane(context) { lane =>
        val write = for {
          rawAddress  <- context.read(lane, address)
          sourceValue <- context.read(lane, source)
        } yield
          if (atomicTargetsShared(space, rawAddress))
            VMStateWrite.AtomicSharedMin(lane, destination, rawAddress, width, sourceValue)
          else
            VMStateWrite.AtomicMemoryMin(
              lane,
              destination,
              GuestAddress(rawAddress),
              width,
              sourceValue
            )
        stageResult(effects, write)
      }.map(_ => Step.Advance)
    case VMIR.Instruction.AtomicAddFloat(destination, address, source, width) =>
      forEveryLane(context) { lane =>
        val write = for {
          rawAddress  <- context.read(lane, address)
          sourceValue <- context.read(lane, source)
        } yield VMStateWrite.AtomicMemoryAddFloat(
          lane,
          destination,
          GuestAddress(rawAddress),
          width,
          sourceValue
        )
        stageResult(effects, write)
      }.map(_ => Step.Advance)
    case VMIR.Instruction.SetPredicate(destination, comparison, kind, left, right, width) =>
      forEveryLane(context) { lane =>
        val write = for {
          leftValue  <- context.read(lane, left)
          rightValue <- context.read(lane, right)
        } yield VMStateWrite.Predicate(lane, destination, compare(leftValue, rightValue, comparison, kind, width))
        stageResult(effects, write)
      }.map(_ => Step.Advance)
    case VMIR.Instruction.PredicateCompare(destination, comparison, left, right) =>
      forEveryLane(context) { lane =>
        val write = for {
          leftValue <- context.state.predicates(lane).get(left).toRight(
            ExecutionFault.UndefinedRegister(left, context.pc, lane)
          )
          rightValue <- context.state.predicates(lane).get(right).toRight(
            ExecutionFault.UndefinedRegister(right, context.pc, lane)
          )
        } yield VMStateWrite.Predicate(
          lane,
          destination,
          comparePredicates(leftValue, rightValue, comparison)
        )
        stageResult(effects, write)
      }.map(_ => Step.Advance)
    case VMIR.Instruction.PredicateUnary(destination, source) =>
      forEveryLane(context) { lane =>
        stageResult(
          effects,
          context.state.predicates(lane).get(source).toRight(
            ExecutionFault.UndefinedRegister(source, context.pc, lane)
          ).map(value => VMStateWrite.Predicate(lane, destination, !value))
        )
      }.map(_ => Step.Advance)
    case VMIR.Instruction.PredicateBinary(operation, destination, left, right) =>
      forEveryLane(context) { lane =>
        val write = for {
          leftValue <- context.state.predicates(lane).get(left).toRight(
            ExecutionFault.UndefinedRegister(left, context.pc, lane)
          )
          rightValue <- context.state.predicates(lane).get(right).toRight(
            ExecutionFault.UndefinedRegister(right, context.pc, lane)
          )
        } yield VMStateWrite.Predicate(
          lane,
          destination,
          operation match {
            case PredicateOperation.And => leftValue && rightValue
            case PredicateOperation.Or  => leftValue || rightValue
            case PredicateOperation.Xor => leftValue ^ rightValue
          }
        )
        stageResult(effects, write)
      }.map(_ => Step.Advance)
    case VMIR.Instruction.Branch(target)  => Right(Step.Jump(target))
    case VMIR.Instruction.BarrierSync(id) =>
      // A workgroup barrier is identified by its hardware barrier slot, not
      // by the static PC at which a divergent cohort happens to arrive.
      Right(Step.Suspend(WaitKey(WaitScope.Workgroup, id.toLong, 0L)))
    case VMIR.Instruction.BarrierReduce(_, id, _, _) =>
      Right(Step.Suspend(WaitKey(WaitScope.Workgroup, id.toLong, 0L)))
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
            Left(ExecutionFault.InvalidState("bar.warp.sync member mask differs across active lanes"))
          else
            Right(Step.Suspend(WaitKey(WaitScope.Warp, memberMaskValue, context.pc.value.toLong)))
      }
    case VMIR.Instruction.MemoryBarrier =>
      context.memory.fence().leftMap(error => ExecutionFault.Memory(error.message)).map(_ => Step.Advance)
    case VMIR.Instruction.Prefetch                                   => Right(Step.Advance)
    case VMIR.Instruction.Call(function, returnParameter, parameter) =>
      val groups = mutable.LinkedHashMap.empty[FunctionName, VMLaneMask]
      forEveryLane(context) { lane =>
        context.functionName(lane, function).flatMap { name =>
          VMLaneMask.one(lane).map { member =>
            groups(name) = groups.getOrElse(name, VMLaneMask.Empty).union(member)
          }
        }
      }.flatMap { _ =>
        groups.foldLeft[Either[ExecutionFault, Unit]](Right(())) { case (current, (name, lanes)) =>
          current.flatMap { _ =>
            val groupContext = VMExecutionContext(
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
              context.scheduler
            )
            context.kernel.functions.get(name) match {
              case Some(deviceFunction) =>
                VMInterpreter.executeDeviceFunction(groupContext, deviceFunction, parameter, returnParameter)
              case None =>
                forEveryLane(groupContext) { lane =>
                  executeBuiltin(groupContext, lane, name, returnParameter, parameter).getOrElse(
                    Left(ExecutionFault.InvalidState(s"missing device function $name"))
                  )
                }
            }
          }
        }
      }.map(_ => Step.Advance)
    case VMIR.Instruction.Trap =>
      Left(ExecutionFault.ArchitecturalTrap("ptx", 0L, context.pc))
    case VMIR.Instruction.Return => Right(Step.Exit(context.active))
    case other                   => Left(ExecutionFault.InvalidState(s"unhandled compiled PTX instruction $other"))
  }

  /** CUB's scan descriptors rely on coherent aligned status/value transfers, including 16-byte double descriptors. This
    * target behavior is stronger than PTX's minimum vector ordering; it is not a general 128-bit atomic guarantee. Keep
    * shared/local/parameter routing separate and retain the existing word path through eight bytes.
    */
  private def coherentGlobalVector(
      context: VMExecutionContext,
      address: Long,
      count: Int,
      width: BitWidth
  ): Boolean = {
    val size = count * byteCount(width).value
    size > 0 && size <= 16 && (size & (size - 1)) == 0 && (address & (size - 1).toLong) == 0L &&
    !isSharedAddress(address) && !isLocalAddress(address) &&
    context.state.parameterBufferRange(address, size).isEmpty
  }

  private def prepareGlobalReads(
      context: VMExecutionContext,
      address: Value,
      size: Int,
      generic: Boolean
  ): Either[ExecutionFault, Unit] = {
    val ranges = Vec.newBuilder[(GuestAddress, Int :| Positive0)]
    context.active.foreachLane { lane =>
      val raw = readFast(context, lane, address)
      if (
        context.state.parameterBufferRange(
          raw,
          size
        ).isEmpty && (!generic || (!isSharedAddress(raw) && !isLocalAddress(raw)))
      ) {
        val _ = ranges += ((GuestAddress(raw), size.refineUnsafe[Positive0]))
      }
    }
    context.memory.prepareReads(ranges.result()).leftMap(error => ExecutionFault.Memory(error.message))
  }

  private def loadCoherentVector(
      context: VMExecutionContext,
      lane: Int,
      address: Long,
      destinations: Vec[RegisterName],
      width: BitWidth,
      signed: Boolean
  ): Either[ExecutionFault, Vec[VMStateWrite]] =
    if (destinations.nonEmpty && destinations.size * byteCount(width).value <= 8) {
      val packedBytes = MemoryValueByteWidth.applyUnsafe(destinations.size * byteCount(width).value)
      context.memory.readLittleEndian(GuestAddress(address), packedBytes)
        .leftMap(error =>
          ExecutionFault.Memory(s"pc ${context.pc.value}, lane $lane: ${error.message}")
        )
        .map { packed =>
          destinations.zipWithIndex.map { case (destination, index) =>
            VMStateWrite.Register(
              lane,
              destination,
              loadBits(packed >>> (index * width.value), width, signed)
            )
          }
        }

    } else {
      val totalBytes = Math.multiplyExact(destinations.size, byteCount(width).value).refineUnsafe[Positive0]
      context.memory.readBytes(GuestAddress(address), totalBytes)
        .leftMap(error =>
          ExecutionFault.Memory(s"pc ${context.pc.value}, lane $lane: ${error.message}")
        )
        .map { bytes =>
          destinations.zipWithIndex.map { case (destination, index) =>
            val value =
              net.kurobako.gemstone.binary.LittleEndian.decode(
                bytes,
                (index * byteCount(width).value).refineUnsafe[Positive0],
                byteCount(width)
              )
            VMStateWrite.Register(lane, destination, loadBits(value, width, signed))
          }
        }
    }

  private def storeCoherentVector(
      context: VMExecutionContext,
      lane: Int,
      address: Long,
      sources: Vec[Value],
      width: BitWidth
  ): Either[ExecutionFault, Vec[VMStateWrite]] =
    if (sources.nonEmpty && sources.size * byteCount(width).value <= 8) {
      sources.zipWithIndex.foldLeft[Either[ExecutionFault, Long]](Right(0L)) { case (result, (source, index)) =>
        for {
          packed <- result
          value  <- context.read(lane, source)
        } yield packed | (truncate(value, width) << (index * width.value))
      }.map { packed =>
        val packedWidth = BitWidth.applyUnsafe(sources.size * width.value)
        Vec(VMStateWrite.Memory(lane, GuestAddress(address), packedWidth, packed))
      }

    } else {
      sources.traverse(source => context.read(lane, source))
        .map(values => Vec(VMStateWrite.MemoryVector(lane, GuestAddress(address), width, values)))
    }

  /** The common nvcc path is a dense register-only instruction stream. Keep the checked, staged path below for memory
    * and rendezvous operations, but avoid allocating an Either and a VMStateWrite for every lane of an arithmetic
    * instruction. Reads still happen before the destination is changed, matching the staged path's aliasing rule.
    */
  private def executeFast(
      instruction: VMIR.Instruction,
      context: VMExecutionContext
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
      val lowIndex  = if (destinations(0).value == "_") -1 else VMRegisterFile.index(destinations(0))
      val highIndex = if (destinations(1).value == "_") -1 else VMRegisterFile.index(destinations(1))
      context.active.foreachLane { lane =>
        val value = readFast(context, lane, source)
        if (destinations(0).value != "_")
          writeRegister(context.state.registers(lane), destinations(0), lowIndex, truncate(value, partWidth))
        if (destinations(1).value != "_")
          writeRegister(
            context.state.registers(lane),
            destinations(1),
            highIndex,
            truncate(value >>> partWidth.value, partWidth)
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
          truncate((low & truncate(-1L, partWidth)) | (truncate(high, partWidth) << partWidth.value), combinedWidth)
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
      val destinationIndex = VMRegisterFile.index(destination)
      context.active.foreachLane(lane =>
        writeRegister(
          context.state.registers(lane),
          destination,
          destinationIndex,
          convert(readFast(context, lane, source), sourceType, destinationType, rounding)
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
    case VMIR.Instruction.FloatBinary(operation, destination, left, right, width) =>
      val destinationIndex = VMRegisterFile.index(destination)
      context.active.foreachLane { lane =>
        val leftBits  = readFast(context, lane, left)
        val rightBits = readFast(context, lane, right)
        val result    = floatBinaryBits(operation, leftBits, rightBits, width)
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
    case VMIR.Instruction.FloatUnary(operation, destination, source, width) =>
      val destinationIndex = VMRegisterFile.index(destination)
      context.active.foreachLane { lane =>
        val sourceValue = readFast(context, lane, source)
        val result      = floatUnaryBits(operation, sourceValue, width)
        writeRegister(context.state.registers(lane), destination, destinationIndex, result)
      }
      true
    case VMIR.Instruction.FusedMultiplyAdd(destination, left, right, addend, width) =>
      val destinationIndex = VMRegisterFile.index(destination)
      context.active.foreachLane { lane =>
        val leftBits   = readFast(context, lane, left)
        val rightBits  = readFast(context, lane, right)
        val addendBits = readFast(context, lane, addend)
        val result     = fusedMultiplyAddBits(leftBits, rightBits, addendBits, width)
        writeRegister(context.state.registers(lane), destination, destinationIndex, result)
      }
      true
    case VMIR.Instruction.FusedMultiplyAddPacked(destination, left, right, addend, bf16) =>
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
            bf16
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
    case VMIR.Instruction.SetPredicate(destination, comparison, kind, left, right, width) =>
      val destinationIndex = VMPredicateFile.index(destination)
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

  private def writeRegister(registers: VMRegisterFile, name: RegisterName, index: Int, value: Long): Unit =
    if (index >= 0) registers.updateSlot(index, value) else registers.update(name, value)

  private def writePredicate(predicates: VMPredicateFile, name: RegisterName, index: Int, value: Boolean): Unit =
    if (index >= 0) predicates.updateSlot(index, value) else predicates.update(name, value)

  private def readPredicateFast(predicates: VMPredicateFile, name: RegisterName, index: Int): Boolean =
    if (index >= 0) predicates.getSlot(index).getOrElse(false) else predicates.get(name).getOrElse(false)

  private def executeBuiltin(
      context: VMExecutionContext,
      lane: Int,
      name: FunctionName,
      returnParameters: Vec[ParameterName],
      parameters: Vec[ParameterName]
  ): Option[Either[ExecutionFault, Unit]] = {
    def parameter(index: Int, width: ScalarByteWidth = ScalarByteWidth.Qword): Long =
      if (index < parameters.size)
        context.state.parameters(lane).getOrElse(
          parameters(index),
          context.state.readParameterBytes(lane, parameters(index), 0, width)
        )
      else 0L

    def returnValue(value: Long): Unit =
      returnParameters.headOption.foreach(name => context.state.parameters(lane)(name) = value)

    def writeLocalPointer(index: Int, value: Long): Unit =
      if (index < parameters.size) context.state.writeLocal(lane, parameter(index), BitWidth(32), value)

    name.value match {
      case "cudaGetDevice" =>
        writeLocalPointer(0, 0L)
        returnValue(0L)
        Some(Right(()))
      case "cudaDeviceGetAttribute" =>
        val attribute = parameter(1, ScalarByteWidth.Dword).toInt
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
            val allocation = DeviceAllocationByteSize.fromLong(parameter(0)) match {
              case Left(message) =>
                Left(ExecutionFault.InvalidState(s"invalid malloc size: $message"))
              case Right(size) =>
                memory.allocate(size).leftMap(error => ExecutionFault.Memory(error.message))
            }
            allocation match {
              case Left(fault)    => Some(Left(fault))
              case Right(address) =>
                returnValue(address.value)
                Some(Right(()))
            }
          case _ => Some(Left(ExecutionFault.InvalidState("device malloc requires dynamic address memory")))
        }
      case "free" =>
        context.memory match {
          case memory: DeviceAllocationMemory =>
            val _ = memory.free(GuestAddress(parameter(0)))
            Some(Right(()))
          case _ => Some(Left(ExecutionFault.InvalidState("device free requires dynamic address memory")))
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
      context: VMExecutionContext,
      lane: Int,
      returnParameters: Vec[ParameterName],
      parameters: Vec[ParameterName],
      parameter: (Int, ScalarByteWidth) => Long
  ): Either[ExecutionFault, Unit] =
    if (parameters.size < 6)
      Left(ExecutionFault.InvalidState(
        s"cudaLaunchDevice expects six parameters, found ${parameters.size}"
      ))
    else {
      val target = context.state.functionParameters(lane).get(parameters(0))
      val child  = target.flatMap(context.kernel.functions.get).toRight(
        ExecutionFault.InvalidState(
          s"cudaLaunchDevice target is not a lowered entry: ${target.getOrElse("<unknown>")}"
        )
      )
      child match {
        case Left(fault)     => Left(fault)
        case Right(function) =>
          val buffer = parameter(1, ScalarByteWidth.Qword)
          val grid   =
            (0 until 3).map(index =>
              context.state.readParameterBytes(lane, parameters(2), index * 4, ScalarByteWidth.Dword) &
                0xffffffffL
            )
          val block =
            (0 until 3).map(index =>
              context.state.readParameterBytes(lane, parameters(3), index * 4, ScalarByteWidth.Dword) &
                0xffffffffL
            )
          val childArguments                          = mutable.Map.empty[ParameterName, Argument]
          var argumentFailure: Option[ExecutionFault] = None
          var index                                   = 0
          while (index < function.parameters.size && argumentFailure.isEmpty) {
            val width  = function.parameterWidths(index)
            val offset = function.parameterOffsets(index)
            context.state.readParameterBufferBytes(buffer + offset.toLong, width) match {
              case Some(bytes) =>
                childArguments(function.parameters(index)) =
                  Argument(Vec.unsafeFromArray(bytes).refineUnsafe[MinLength[1]])
              case None => argumentFailure = Some(ExecutionFault.InvalidState(
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
              geometry.leftMap(error => ExecutionFault.InvalidState(error)) match {
                case Left(fault)  => Left(fault)
                case Right(value) =>
                  VMInterpreter.execute(
                    Kernel(
                      net.kurobako.gemstone.isa.KernelName(function.name.value),
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
                    context.scheduler,
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

  private def readFast(context: VMExecutionContext, lane: Int, value: Value): Long = value match {
    case Value.Register(name)       => context.state.registers(lane)(name)
    case Value.RegisterSlot(index)  => context.state.registers(lane).getSlot(index)
    case Value.Predicate(name)      => if (context.state.predicates(lane).get(name).getOrElse(false)) 1L else 0L
    case Value.PredicateSlot(index) => if (context.state.predicates(lane).getSlot(index).getOrElse(false)) 1L else 0L
    case Value.Symbol(name)         =>
      context.state.parameterAddresses.get(name)
        .orElse(context.kernel.sharedSymbols.get(name))
        .orElse(context.kernel.globalSymbols.get(name).map(_.value))
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
      val sizeX = context.geometry.groupSize.x
      val index = axis match {
        case Axis.X => lane           % sizeX
        case Axis.Y => (lane / sizeX) % context.geometry.groupSize.y
        case Axis.Z => lane / sizeX / context.geometry.groupSize.y
      }
      index.toLong
    case Value.BlockId(axis)  => axis.select(context.group).toLong
    case Value.BlockDim(axis) => axis.select(context.geometry.groupSize).toLong
    case Value.GridDim(axis)  => axis.select(context.geometry.groupCount).toLong
    case Value.Function(name) => throw IllegalStateException(s"function symbol $name is not numeric")
  }

  private def dimensions(x: Int, y: Int, z: Int): Either[String, Dimensions] =
    (
      x.refineEither[Positive],
      y.refineEither[Positive],
      z.refineEither[Positive]
    ).mapN((x, y, z) => Dimensions(x, y, z))

  // Preserve the first lane fault without evaluating later lanes after failure.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def forEveryLane(context: VMExecutionContext)(
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
    java.lang.Long.compareUnsigned(address, PTXAddressSpace.LocalLimit) < 0 && !isSharedAddress(address)

  private def isSharedAddress(address: Long): Boolean = PTXAddressSpace.isShared(address)

  // An atomic without an explicit state space uses generic addressing, so its
  // target follows the computed address. An explicit .shared or .global wins.
  private def atomicTargetsShared(space: VMIR.AtomicSpace, address: Long): Boolean =
    space == VMIR.AtomicSpace.Shared || (space == VMIR.AtomicSpace.Generic && isSharedAddress(address))

  private def integerUnaryResult(operation: IntegerUnaryOperation, value: Long, width: BitWidth): Long = {
    val result = operation match {
      case IntegerUnaryOperation.Negate           => -value
      case IntegerUnaryOperation.Absolute         => Math.abs(signExtend(truncate(value, width), width))
      case IntegerUnaryOperation.BitwiseNot       => ~value
      case IntegerUnaryOperation.PopulationCount  => bitCount(value, width).toLong
      case IntegerUnaryOperation.LeadingZeroCount =>
        if (width.value == 32) Integer.numberOfLeadingZeros(Bits.signed32(value)).toLong
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
    if (left) low32((pair << shift) >>> 32)
    else low32(pair >>> shift)
  }

  private def integerBinaryResult(
      operation: IntegerOperation,
      left: Long,
      right: Long,
      width: BitWidth
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
      case IntegerOperation.MultiplyWideUnsigned16 => Bits.low16(left) * Bits.low16(right)
      case IntegerOperation.MultiplyWideSigned16   =>
        signExtend(left, BitWidth(16)) * signExtend(right, BitWidth(16))
      case IntegerOperation.MultiplyWideUnsigned32 => low32(left) * low32(right)
      case IntegerOperation.MultiplyWideSigned32   => left.toInt.toLong * right.toInt.toLong
      case IntegerOperation.Minimum(signed)        =>
        if (signed) Math.min(signExtend(left, width), signExtend(right, width))
        else if (width.value == 64) {
          if (java.lang.Long.compareUnsigned(left, right) <= 0) left else right
        } else Math.min(truncate(left, width), truncate(right, width))
      case IntegerOperation.Maximum(signed) =>
        if (signed) Math.max(signExtend(left, width), signExtend(right, width))
        else if (width.value == 64) {
          if (java.lang.Long.compareUnsigned(left, right) >= 0) left else right
        } else Math.max(truncate(left, width), truncate(right, width))
      case IntegerOperation.ShiftLeft         => left << (right & (width.value - 1).toLong).toInt
      case IntegerOperation.ShiftRightLogical =>
        if (width.value == 32) Bits.low32(left) >>> (right & 31L).toInt
        else left >>> (right & 63L).toInt
      case IntegerOperation.ShiftRightArithmetic =>
        if (width.value == 32) (left.toInt >> (right & 31L).toInt).toLong
        else left >> (right & 63L).toInt
    }
    truncate(result, width)
  }

  private def floatBinaryBits(
      operation: FloatOperation,
      leftBits: Long,
      rightBits: Long,
      width: FloatWidth
  ): Long =
    if (width.value == 16) {
      val left   = IEEE754.binary16ToFloat(Binary16.low(leftBits))
      val right  = IEEE754.binary16ToFloat(Binary16.low(rightBits))
      val result = operation match {
        case FloatOperation.Add      => left + right
        case FloatOperation.Subtract => left - right
        case FloatOperation.Multiply => left * right
        case FloatOperation.Divide   => left / right
        case FloatOperation.Minimum  => Math.min(left, right)
        case FloatOperation.Maximum  => Math.max(left, right)
        case FloatOperation.CopySign => Math.copySign(right, left)
      }
      IEEE754.floatToBinary16(result).value
    } else if (width.value == 32) {
      val left   = IEEE754.binary32ToFloat(Binary32.low(leftBits))
      val right  = IEEE754.binary32ToFloat(Binary32.low(rightBits))
      val result = operation match {
        case FloatOperation.Add      => left + right
        case FloatOperation.Subtract => left - right
        case FloatOperation.Multiply => left * right
        case FloatOperation.Divide   => left / right
        case FloatOperation.Minimum  => Math.min(left, right)
        case FloatOperation.Maximum  => Math.max(left, right)
        case FloatOperation.CopySign => Math.copySign(right, left)
      }
      IEEE754.floatToBinary32(result).value
    } else {
      val left   = IEEE754.binary64ToDouble(leftBits)
      val right  = IEEE754.binary64ToDouble(rightBits)
      val result = operation match {
        case FloatOperation.Add      => left + right
        case FloatOperation.Subtract => left - right
        case FloatOperation.Multiply => left * right
        case FloatOperation.Divide   => left / right
        case FloatOperation.Minimum  => Math.min(left, right)
        case FloatOperation.Maximum  => Math.max(left, right)
        case FloatOperation.CopySign => Math.copySign(right, left)
      }
      IEEE754.doubleToBinary64(result)
    }

  private def fusedMultiplyAddBits(leftBits: Long, rightBits: Long, addendBits: Long, width: FloatWidth): Long =
    if (width.value == 16)
      IEEE754.floatToBinary16(
        Math.fma(
          IEEE754.binary16ToFloat(Binary16.low(leftBits)),
          IEEE754.binary16ToFloat(Binary16.low(rightBits)),
          IEEE754.binary16ToFloat(Binary16.low(addendBits))
        )
      ).value
    else if (width.value == 32)
      IEEE754.floatToBinary32(
        Math.fma(
          IEEE754.binary32ToFloat(Binary32.low(leftBits)),
          IEEE754.binary32ToFloat(Binary32.low(rightBits)),
          IEEE754.binary32ToFloat(Binary32.low(addendBits))
        )
      ).value
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
  private def floatUnaryBits(operation: FloatUnaryOperation, bits: Long, width: FloatWidth): Long =
    if (width.value == 16) {
      val halfBits = truncate(bits, BitWidth(16))
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
          IEEE754.floatToBinary16(result).value
      }
    } else if (width.value == 32) {
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
      IEEE754.floatToBinary32(result).value
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
      IEEE754.binary32ToFloat(Binary32.low(truncate(bits, BitWidth(16)) << 16))
    else IEEE754.binary16ToFloat(Binary16.low(bits))

  private def packedFloatBits(value: Float, bf16: Boolean): Long =
    if (bf16) bfloat16FromFloat(value, Some(ConversionRounding.RN)) else IEEE754.floatToBinary16(value).value

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
        IEEE754.binary32ToFloat(Binary32.low(truncate(left, BitWidth(16)) << 16)),
        IEEE754.binary32ToFloat(Binary32.low(truncate(right, BitWidth(16)) << 16)),
        IEEE754.binary32ToFloat(Binary32.low(truncate(addend, BitWidth(16)) << 16))
      ),
      Some(ConversionRounding.RN)
    )

  private def loadBits(value: Long, width: BitWidth, signed: Boolean): Long = {
    val bits = truncate(value, width)
    if (signed) signExtend(bits, width) else bits
  }

  private def bitFind(value: Long, width: BitWidth, signed: Boolean, shiftAmount: Boolean): Long = {
    val bits   = truncate(value, width)
    val search =
      if (signed && ((bits >>> (width.value - 1)) & 1L) != 0L) truncate(~bits, width)
      else bits
    val position =
      if (search == 0L) -1
      else if (width.value == 32) 31 - Integer.numberOfLeadingZeros(search.toInt)
      else 63 - java.lang.Long.numberOfLeadingZeros(search)
    if (position < 0) 0xffffffffL
    else if (shiftAmount) (width.value - 1 - position).toLong
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
    var result = signExtend(truncate(addend, BitWidth(32)), BitWidth(32))
    var index  = 0
    while (index < 4) {
      val leftByte   = (left >>> (index * 8)) & 0xffL
      val rightByte  = (right >>> (index * 8)) & 0xffL
      val leftValue  = if (leftSigned && leftByte >= 0x80L) leftByte - 0x100L else leftByte
      val rightValue = if (rightSigned && rightByte >= 0x80L) rightByte - 0x100L else rightByte
      result += leftValue * rightValue
      index += 1
    }
    truncate(result, BitWidth(32))
  }

  private def shuffleClamp(value: Long): Int =
    Math.min(value & 0xffffffffL, 31L).toInt

  @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
  private def extractBits(source: Long, start: Long, count: Long, width: BitWidth, signed: Boolean): Long = {
    val sourceBits = truncate(source, width)
    val bitStart   = (start & 0xffL).toInt
    val bitCount   = (count & 0xffL).toInt
    if (bitStart >= width.value || bitCount == 0) 0L
    else {
      val extractedWidth    = Math.min(bitCount, width.value - bitStart)
      val mask              = if (extractedWidth == 64) -1L else (1L << extractedWidth) - 1L
      val extracted         = (sourceBits >>> bitStart) & mask
      val extractedBitWidth = BinaryBitWidth.assume(extractedWidth)
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
    val mask0            = if (positionOverflow) 0L else truncate(-1L << start1.toInt, BitWidth(32))
    val mask1            =
      if (sum >= 32L || positionOverflow || widthOverflow) 0L
      else if (count1 == 0L) 0xffffffffL
      else truncate(-1L << sum.toInt, BitWidth(32))
    truncate(mask0 & ~mask1, BitWidth(32))
  }

  private def multiplyHigh(left: Long, right: Long, width: BitWidth, signed: Boolean): Long = {
    val leftValue =
      if (signed) BigInt(signExtend(left, width)) else BigInt(java.lang.Long.toUnsignedString(truncate(left, width)))
    val rightValue =
      if (signed) BigInt(signExtend(right, width)) else BigInt(java.lang.Long.toUnsignedString(truncate(right, width)))
    ((leftValue * rightValue) >> width.value).longValue
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
      width: BitWidth
  ): (Long, Boolean) = {
    val first         = left + right
    val firstOverflow = java.lang.Long.compareUnsigned(first, left) < 0
    val result        = if (carryIn) first + 1L else first
    val nextOverflow  = carryIn && java.lang.Long.compareUnsigned(result, first) < 0
    if (width.value == 64) result -> (firstOverflow || nextOverflow)
    else truncate(result, width)  -> ((result >>> width.value) != 0L)
  }

  private[v9_3] def integerMultiplyAdd(
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
        if (resultWidth.value == 64) overflowHigh != 0L
        else overflowHigh != 0L || (result >>> resultWidth.value) != 0L
      truncate(result, resultWidth) -> overflow
    case IntegerMultiplyAddOperation.High(operandWidth, resultWidth, usesCarry, _) =>
      val leftValue   = truncate(left, operandWidth)
      val rightValue  = truncate(right, operandWidth)
      val productLow  = leftValue * rightValue
      val productHigh = unsignedMultiplyHigh(leftValue, rightValue)
      val high        =
        if (operandWidth.value == 64) productHigh
        else truncate(
          (productLow >>> operandWidth.value) | (productHigh << (64 - operandWidth.value)),
          operandWidth
        )
      val addendValue = truncate(addend, resultWidth)
      addUnsigned(high, addendValue, usesCarry && carryIn, resultWidth)
  }

  private def integerAddWithCarry(
      left: Long,
      right: Long,
      width: BitWidth,
      usesCarry: Boolean,
      carryIn: Boolean
  ): (Long, Boolean) = {
    val sum =
      BigInt(java.lang.Long.toUnsignedString(truncate(left, width))) +
        BigInt(java.lang.Long.toUnsignedString(truncate(right, width))) +
        (if (usesCarry && carryIn) 1 else 0)
    (truncate(sum.longValue, width), (sum >> width.value) != 0)
  }

  private def integerSubWithBorrow(
      left: Long,
      right: Long,
      width: BitWidth,
      usesBorrow: Boolean,
      borrowIn: Boolean
  ): (Long, Boolean) = {
    val difference =
      BigInt(java.lang.Long.toUnsignedString(truncate(left, width))) -
        BigInt(java.lang.Long.toUnsignedString(truncate(right, width))) -
        (if (usesBorrow && borrowIn) 1 else 0)
    (truncate(difference.longValue, width), difference < 0)
  }

  @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
  private def insertBits(insert: Long, base: Long, start: Long, count: Long, width: BitWidth): Long = {
    val bitStart = (start & 0xffL).toInt
    val bitCount = (count & 0xffL).toInt
    if (bitCount == 0 || bitStart >= width.value) truncate(base, width)
    else {
      val insertedWidth    = Math.min(bitCount, width.value - bitStart)
      val fieldMask        = if (insertedWidth == 64) -1L else ((1L << insertedWidth) - 1L) << bitStart
      val insertedBitWidth = BinaryBitWidth.assume(insertedWidth)
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
      val byte       = if (signExtend && (selected & 0x80) != 0) 0xff else selected
      bits | (byte.toLong << (8 * index))
    }
    truncate(result, BitWidth(32))
  }

  private def divideInteger(left: Long, right: Long, width: BitWidth, signed: Boolean): Long = {
    val leftBits  = truncate(left, width)
    val rightBits = truncate(right, width)
    if (rightBits == 0L) 0L
    else if (signed) {
      val dividend = signExtend(leftBits, width)
      val divisor  = signExtend(rightBits, width)
      if (dividend == Long.MinValue && divisor == -1L) Long.MinValue
      else dividend / divisor
    } else if (width.value == 64) java.lang.Long.divideUnsigned(leftBits, rightBits)
    else leftBits / rightBits
  }

  private def remainderInteger(left: Long, right: Long, width: BitWidth, signed: Boolean): Long = {
    val leftBits  = truncate(left, width)
    val rightBits = truncate(right, width)
    if (rightBits == 0L) 0L
    else if (signed) {
      val dividend = signExtend(leftBits, width)
      val divisor  = signExtend(rightBits, width)
      if (dividend == Long.MinValue && divisor == -1L) 0L
      else dividend % divisor
    } else if (width.value == 64) java.lang.Long.remainderUnsigned(leftBits, rightBits)
    else leftBits % rightBits
  }

  private def compare(
      left: Long,
      right: Long,
      comparison: Comparison,
      kind: Char,
      width: BitWidth
  ): Boolean = {
    val leftValue          = truncate(left, width)
    val rightValue         = truncate(right, width)
    val (order, unordered) =
      if (kind == 'f' || kind == 'b') {
        val leftFloat =
          if (kind == 'b') IEEE754.binary32ToFloat(Binary32.low(leftValue << 16)).toDouble
          else IEEE754.toDouble(leftValue, FloatingPointWidth.applyUnsafe(width.value))
        val rightFloat =
          if (kind == 'b') IEEE754.binary32ToFloat(Binary32.low(rightValue << 16)).toDouble
          else IEEE754.toDouble(rightValue, FloatingPointWidth.applyUnsafe(width.value))
        val order = if (leftFloat < rightFloat) -1 else if (leftFloat > rightFloat) 1 else 0
        order -> (leftFloat.isNaN || rightFloat.isNaN)
      } else {
        val integerOrder =
          if (kind == 's') java.lang.Long.compare(signExtend(leftValue, width), signExtend(rightValue, width))
          else if (width.value == 64) java.lang.Long.compareUnsigned(leftValue, rightValue)
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

  // The bf16 branch terminates before the shared conversion matrix.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return"))
  private def convert(
      value: Long,
      sourceType: ConversionType,
      destinationType: ConversionType,
      rounding: Option[ConversionRounding]
  ): Long = {
    val sourceWidth         = sourceType.width
    val destinationWidth    = destinationType.width
    val sourceIsBf16        = sourceType == ConversionType.BF16
    val destinationIsBf16   = destinationType == ConversionType.BF16
    val sourceKind          = sourceType.kind
    val destinationKind     = destinationType.kind
    lazy val floatingSource =
      if (sourceIsBf16)
        IEEE754.binary32ToFloat(Binary32.low(truncate(value, BitWidth(16)) << 16)).toDouble
      else IEEE754.toDouble(value, FloatingPointWidth.applyUnsafe(sourceWidth.value))

    if (destinationIsBf16) {
      val floating =
        if (sourceKind == 'f') floatingSource
        else {
          val sourceBits = truncate(value, sourceWidth)
          val integer    = if (sourceKind == 's') signExtend(sourceBits, sourceWidth) else sourceBits
          if (sourceKind == 'u' && sourceWidth.value == 64 && integer < 0)
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
          case Some(ConversionRounding.RZI) => Some(if (floating < 0.0) Math.ceil(floating) else Math.floor(floating))
          case Some(ConversionRounding.RMI) => Some(Math.floor(floating))
          case Some(ConversionRounding.RPI) => Some(Math.ceil(floating))
          case _                            => None
        }
        IEEE754.fromDouble(integral.getOrElse(floating), FloatingPointWidth.applyUnsafe(destinationWidth.value))
      } else {
        val rounded = rounding match {
          case Some(ConversionRounding.RN) | Some(ConversionRounding.RNI) => Math.rint(floating)
          case Some(ConversionRounding.RM) | Some(ConversionRounding.RMI) => Math.floor(floating)
          case Some(ConversionRounding.RP) | Some(ConversionRounding.RPI) => Math.ceil(floating)
          case _ => if (floating < 0) Math.ceil(floating) else Math.floor(floating)
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
          if (sourceKind == 'u' && sourceWidth.value == 64 && integer < 0)
            BigInt(java.lang.Long.toUnsignedString(integer)).toDouble
          else integer.toDouble
        IEEE754.fromDouble(floating, FloatingPointWidth.applyUnsafe(destinationWidth.value))
      } else truncate(integer, destinationWidth)
    }
  }

  private def bfloat16FromFloat(value: Float, rounding: Option[ConversionRounding]): Long = {
    val bits    = IEEE754.floatToBinary32(value)
    val upper   = (bits.value >>> 16) & 0xffffL
    val lower   = bits.value & 0xffffL
    val rounded = rounding match {
      case Some(ConversionRounding.RZ) | Some(ConversionRounding.RZI)                                => upper
      case Some(ConversionRounding.RM) | Some(ConversionRounding.RMI) if value < 0.0f && lower != 0L => upper + 1L
      case Some(ConversionRounding.RP) | Some(ConversionRounding.RPI) if value > 0.0f && lower != 0L => upper + 1L
      case _ if lower > 0x8000L || (lower == 0x8000L && (upper & 1L) != 0L)                          => upper + 1L
      case _                                                                                         => upper
    }
    truncate(rounded, BitWidth(16))
  }

}
