package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.memory.CheckedMemory
import net.kurobako.gemstone.memory.GuestAddress
import net.kurobako.gemstone.scheduler.DeterministicScheduler
import net.kurobako.gemstone.scheduler.LaunchShape
import net.kurobako.gemstone.trace.Trace
import net.kurobako.gemstone.trace.TraceEvent

sealed trait PtxInstruction extends Product with Serializable {
  def opcode: String
}

object PtxInstruction {
  final case class LoadParameter64(destination: String, parameter: String) extends PtxInstruction {
    override val opcode: String = "ld.param.u64"
  }
  final case class MoveThreadId(destination: String) extends PtxInstruction {
    override val opcode: String = "mov.u32"
  }
  final case class MultiplyWide32(destination: String, left: String, right: Long) extends PtxInstruction {
    override val opcode: String = "mul.wide.u32"
  }
  final case class Add64(destination: String, left: String, right: String) extends PtxInstruction {
    override val opcode: String = "add.s64"
  }
  final case class LoadGlobal32(destination: String, address: String) extends PtxInstruction {
    override val opcode: String = "ld.global.u32"
  }
  final case class Add32(destination: String, left: String, right: String) extends PtxInstruction {
    override val opcode: String = "add.u32"
  }
  final case class StoreGlobal32(address: String, source: String) extends PtxInstruction {
    override val opcode: String = "st.global.u32"
  }
  case object Return extends PtxInstruction {
    override val opcode: String = "ret"
  }
}

final case class PtxKernel(name: String, parameters: Vector[String], instructions: Vector[PtxInstruction])

final case class PtxParseError(line: Int, message: String)

object PtxParser {
  def parse(source: String): Either[PtxParseError, PtxKernel] =
    Parser.parse(source).left.map { error =>
      PtxParseError(error.line, error.toString)
    }.flatMap { program =>
      program.statements.collectFirst { case entry: Statement.Entry => entry } match {
        case None                                             => Left(PtxParseError(1, "missing .entry declaration"))
        case Some(Statement.Entry(_, name, parameters, body)) =>
          val lowered = body.collect { case Statement.Instruction(instruction) => lower(instruction) }
          lowered.collectFirst { case Left(error) => error } match {
            case Some(error) => Left(error)
            case None        =>
              Right(PtxKernel(name, parameters.map(_.name), lowered.collect { case Right(instruction) => instruction }))
          }
      }
    }

  private def lower(statement: Instruction): Either[PtxParseError, PtxInstruction] = {
    val operands = statement.operands
    val result   = (statement.mnemonic, operands) match {
      case ("ld.param.u64", Vector(destination, parameter)) =>
        for {
          destinationName <- registerName(destination)
          parameterName   <- addressSymbol(parameter)
        } yield PtxInstruction.LoadParameter64(destinationName, parameterName)
      case ("mov.u32", Vector(destination, Operand.Register("%tid.x", None))) =>
        registerName(destination).map(PtxInstruction.MoveThreadId.apply)
      case ("mul.wide.u32", Vector(destination, left, Operand.Immediate(right))) =>
        for {
          destinationName <- registerName(destination)
          leftName        <- registerName(left)
          immediate       <- right.toLongOption.toRight(s"invalid immediate: $right")
        } yield PtxInstruction.MultiplyWide32(destinationName, leftName, immediate)
      case ("add.s64", Vector(destination, left, right)) =>
        for {
          destinationName <- registerName(destination)
          leftName        <- registerName(left)
          rightName       <- registerName(right)
        } yield PtxInstruction.Add64(destinationName, leftName, rightName)
      case ("ld.global.u32", Vector(destination, address)) =>
        for {
          destinationName <- registerName(destination)
          addressName     <- addressRegister(address)
        } yield PtxInstruction.LoadGlobal32(destinationName, addressName)
      case ("add.u32", Vector(destination, left, right)) =>
        for {
          destinationName <- registerName(destination)
          leftName        <- registerName(left)
          rightName       <- registerName(right)
        } yield PtxInstruction.Add32(destinationName, leftName, rightName)
      case ("st.global.u32", Vector(address, source)) =>
        for {
          addressName <- addressRegister(address)
          sourceName  <- registerName(source)
        } yield PtxInstruction.StoreGlobal32(addressName, sourceName)
      case ("ret", Vector()) => Right(PtxInstruction.Return)
      case _                 => Left(s"unsupported instruction: ${Printer.render(statement)}")
    }
    result.left.map(message => PtxParseError(1, message))
  }

  private def registerName(operand: Operand): Either[String, String] = operand match {
    case Operand.Register(name, None) => Right(name)
    case other                        => Left(s"expected register, found ${Printer.render(other)}")
  }

  private def addressRegister(operand: Operand): Either[String, String] = operand match {
    case Operand.Address(Vector(register)) => registerName(register)
    case other                             => Left(s"expected register address, found ${Printer.render(other)}")
  }

  private def addressSymbol(operand: Operand): Either[String, String] = operand match {
    case Operand.Address(Vector(Operand.Symbol(name))) => Right(name)
    case other => Left(s"expected parameter address, found ${Printer.render(other)}")
  }
}

final case class PtxExecutionError(message: String)
final case class PtxExecutionResult(trace: Trace)

object PtxInterpreter {
  def execute(
      kernel: PtxKernel,
      shape: LaunchShape,
      arguments: Map[String, GuestAddress],
      memory: CheckedMemory
  ): Either[PtxExecutionError, PtxExecutionResult] = {
    var trace: Trace                       = Trace.empty
    var step: Long                         = 0L
    var failure: Option[PtxExecutionError] = None

    val lanes     = DeterministicScheduler.schedule(shape)
    var laneIndex = 0
    while (laneIndex < lanes.size && failure.isEmpty) {
      val lane      = lanes(laneIndex)
      val registers = scala.collection.mutable.Map.empty[String, Long]
      trace = trace.append(TraceEvent.SchedulerDecision(step, lane.block, lane.index))
      step += 1L

      var pc = 0
      while (pc < kernel.instructions.size && failure.isEmpty) {
        val instruction = kernel.instructions(pc)
        trace = trace.append(TraceEvent.Instruction(step, kernel.name, lane.block, lane.index, pc, instruction.opcode))
        step += 1L

        def register(name: String): Either[PtxExecutionError, Long] =
          registers.get(name).toRight(PtxExecutionError(s"lane ${lane.index}: undefined register $name at pc $pc"))

        val outcome: Either[PtxExecutionError, Unit] = instruction match {
          case PtxInstruction.LoadParameter64(destination, parameter) =>
            arguments
              .get(parameter)
              .toRight(PtxExecutionError(s"missing kernel argument $parameter"))
              .map(address => registers(destination) = address.value)
          case PtxInstruction.MoveThreadId(destination) =>
            registers(destination) = lane.index.toLong
            Right(())
          case PtxInstruction.MultiplyWide32(destination, left, right) =>
            register(left).map(value => registers(destination) = value * right)
          case PtxInstruction.Add64(destination, left, right) =>
            for {
              leftValue  <- register(left)
              rightValue <- register(right)
            } yield registers(destination) = leftValue + rightValue
          case PtxInstruction.LoadGlobal32(destination, address) =>
            register(address).flatMap { value =>
              memory
                .readInt32(GuestAddress(value))
                .left
                .map(error => PtxExecutionError(error.message))
                .map(loaded => registers(destination) = loaded.toLong & 0xffffffffL)
            }
          case PtxInstruction.Add32(destination, left, right) =>
            for {
              leftValue  <- register(left)
              rightValue <- register(right)
            } yield registers(destination) = (leftValue + rightValue) & 0xffffffffL
          case PtxInstruction.StoreGlobal32(address, source) =>
            for {
              addressValue <- register(address)
              sourceValue  <- register(source)
              _            <- memory
                .writeInt32(GuestAddress(addressValue), sourceValue.toInt)
                .left
                .map(error => PtxExecutionError(error.message))
            } yield {
              trace = trace.append(
                TraceEvent.OutputWrite(step, lane.block, lane.index, addressValue, sourceValue.toInt)
              )
              step += 1L
            }
          case PtxInstruction.Return => Right(())
        }
        outcome.left.foreach(error => failure = Some(error))
        pc += 1
      }
      laneIndex += 1
    }

    failure.toLeft(PtxExecutionResult(trace))
  }
}
