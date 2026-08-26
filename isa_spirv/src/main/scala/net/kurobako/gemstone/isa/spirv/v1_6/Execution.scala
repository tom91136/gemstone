package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.execution.CancellationToken
import net.kurobako.gemstone.execution.Effects
import net.kurobako.gemstone.execution.ExecutableProgram
import net.kurobako.gemstone.execution.ExecutionBudget
import net.kurobako.gemstone.execution.ExecutionFault
import net.kurobako.gemstone.execution.ExecutionLimits
import net.kurobako.gemstone.execution.LaneMask
import net.kurobako.gemstone.execution.Pc
import net.kurobako.gemstone.execution.Step
import net.kurobako.gemstone.execution.Write

import scala.collection.mutable

enum ExecutableInstruction {
  case Ignore(name: String)
  case Constant(resultId: Int, value: Long)
  case Variable(resultId: Int, initializerId: Option[Int])
  case Load(resultId: Int, pointerId: Int)
  case Store(pointerId: Int, valueId: Int)
  case Copy(resultId: Int, sourceId: Int)
  case Binary(name: String, resultId: Int, leftId: Int, rightId: Int, operation: BinaryOperation)
  case Select(resultId: Int, conditionId: Int, trueId: Int, falseId: Int)
  case Jump(target: Pc)
  case JumpIf(conditionId: Int, whenTrue: Pc, whenFalse: Pc)
  case Return(valueId: Option[Int])
  case Unsupported(name: String, detail: String)
}

enum BinaryOperation {
  case Add
  case Subtract
  case Multiply
  case BitAnd
  case BitOr
  case BitXor
  case ShiftLeft
  case LogicalShiftRight
  case Equal
  case NotEqual
  case UnsignedLessThan
  case UnsignedLessThanEqual
  case UnsignedGreaterThan
  case UnsignedGreaterThanEqual
}

enum SpirvWrite extends Write {
  case Value(id: Int, value: Long)
  case Memory(address: Long, value: Long)
}

final class ExecutionContext private[v1_6] (initialValues: Map[Int, Long], initialMemory: Map[Long, Long]) {
  private val values  = mutable.HashMap.from(initialValues)
  private val memory  = mutable.HashMap.from(initialMemory)
  private val effects = new Effects[SpirvWrite]

  def value(id: Int, pc: Pc): Either[ExecutionFault, Long] =
    values.get(id).toRight(ExecutionFault.UndefinedRegister(s"%$id", pc, 0))

  def snapshot: Map[Int, Long]        = values.toMap
  def memorySnapshot: Map[Long, Long] = memory.toMap

  private[v1_6] def write(id: Int, value: Long): Either[ExecutionFault, Unit] = {
    effects.stage(SpirvWrite.Value(id, value & 0xffffffffL))
    commitEffects()
  }

  private[v1_6] def writeMemory(address: Long, value: Long): Either[ExecutionFault, Unit] = {
    effects.stage(SpirvWrite.Memory(address, value & 0xffffffffL))
    commitEffects()
  }

  private[v1_6] def readMemory(address: Long): Either[ExecutionFault, Long] =
    memory.get(address).toRight(ExecutionFault.Memory(s"undefined SPIR-V memory address 0x${address.toHexString}"))

  private def commitEffects(): Either[ExecutionFault, Unit] =
    effects.commit(
      _ => Right(()),
      {
        case SpirvWrite.Value(resultId, result) => values.update(resultId, result)
        case SpirvWrite.Memory(address, result) => memory.update(address, result)
      }
    )
}

final case class SpirvExecutable(program: ExecutableProgram[ExecutableInstruction])

enum LoweringError {
  case Malformed(instruction: String, detail: String)

  def message: String = this match {
    case Malformed(instruction, detail) => s"$instruction: $detail"
  }
}

object Lowering {
  def lower(module: Module): Either[LoweringError, SpirvExecutable] = {
    val labels = module.instructions.zipWithIndex.collect {
      case (instruction, index) if instruction.name == "OpLabel" && instruction.operands.nonEmpty =>
        instruction.operands.head.bits -> Pc.unsafe(index)
    }.toMap
    module.instructions.foldLeft[Either[LoweringError, Vector[ExecutableInstruction]]](Right(Vector.empty)) {
      case (result, instruction) =>
        for {
          values <- result
          value  <- lowerInstruction(instruction, labels)
        } yield values :+ value
    }.map(values => SpirvExecutable(ExecutableProgram(values)))
  }

  private def lowerInstruction(
      instruction: Instruction,
      labels: Map[Int, Pc]
  ): Either[LoweringError, ExecutableInstruction] =
    KnownInstruction.fromSyntax(instruction) match {
      case Left(_)      => Right(ExecutableInstruction.Unsupported(instruction.name, "unknown SPIR-V opcode"))
      case Right(known) => lowerKnownInstruction(known, labels)
    }

  private def lowerKnownInstruction(
      instruction: KnownInstruction,
      labels: Map[Int, Pc]
  ): Either[LoweringError, ExecutableInstruction] =
    instruction match {
      case KnownInstruction.OpConstant(arguments) =>
        val operands = operandBits(arguments)
        requireOperands(instruction.name, operands, 3)
          .map(_ => ExecutableInstruction.Constant(operands(1), operands(2).toLong & 0xffffffffL))
      case KnownInstruction.OpSpecConstant(arguments) =>
        val operands = operandBits(arguments)
        requireOperands(instruction.name, operands, 3)
          .map(_ => ExecutableInstruction.Constant(operands(1), operands(2).toLong & 0xffffffffL))
      case KnownInstruction.OpConstantTrue(arguments) =>
        val operands = operandBits(arguments)
        requireOperands(instruction.name, operands, 2).map(_ => ExecutableInstruction.Constant(operands(1), 1L))
      case KnownInstruction.OpSpecConstantTrue(arguments) =>
        val operands = operandBits(arguments)
        requireOperands(instruction.name, operands, 2).map(_ => ExecutableInstruction.Constant(operands(1), 1L))
      case KnownInstruction.OpConstantFalse(arguments) =>
        val operands = operandBits(arguments)
        requireOperands(instruction.name, operands, 2).map(_ => ExecutableInstruction.Constant(operands(1), 0L))
      case KnownInstruction.OpSpecConstantFalse(arguments) =>
        val operands = operandBits(arguments)
        requireOperands(instruction.name, operands, 2).map(_ => ExecutableInstruction.Constant(operands(1), 0L))
      case KnownInstruction.OpVariable(arguments) =>
        val operands = operandBits(arguments)
        requireOperands(instruction.name, operands, 3)
          .map(_ => ExecutableInstruction.Variable(operands(1), operands.lift(3)))
      case KnownInstruction.OpLoad(arguments) =>
        val operands = operandBits(arguments)
        requireOperands(instruction.name, operands, 3)
          .map(_ => ExecutableInstruction.Load(operands(1), operands(2)))
      case KnownInstruction.OpStore(arguments) =>
        val operands = operandBits(arguments)
        requireOperands(instruction.name, operands, 2)
          .map(_ => ExecutableInstruction.Store(operands(0), operands(1)))
      case KnownInstruction.OpCopyObject(arguments) =>
        val operands = operandBits(arguments)
        requireOperands(instruction.name, operands, 3)
          .map(_ => ExecutableInstruction.Copy(operands(1), operands(2)))
      case KnownInstruction.OpIAdd(arguments) =>
        binary(instruction.name, arguments, BinaryOperation.Add)
      case KnownInstruction.OpISub(arguments) =>
        binary(instruction.name, arguments, BinaryOperation.Subtract)
      case KnownInstruction.OpIMul(arguments) =>
        binary(instruction.name, arguments, BinaryOperation.Multiply)
      case KnownInstruction.OpBitwiseAnd(arguments) =>
        binary(instruction.name, arguments, BinaryOperation.BitAnd)
      case KnownInstruction.OpBitwiseOr(arguments) =>
        binary(instruction.name, arguments, BinaryOperation.BitOr)
      case KnownInstruction.OpBitwiseXor(arguments) =>
        binary(instruction.name, arguments, BinaryOperation.BitXor)
      case KnownInstruction.OpShiftLeftLogical(arguments) =>
        binary(instruction.name, arguments, BinaryOperation.ShiftLeft)
      case KnownInstruction.OpShiftRightLogical(arguments) =>
        binary(instruction.name, arguments, BinaryOperation.LogicalShiftRight)
      case KnownInstruction.OpIEqual(arguments) =>
        binary(instruction.name, arguments, BinaryOperation.Equal)
      case KnownInstruction.OpINotEqual(arguments) =>
        binary(instruction.name, arguments, BinaryOperation.NotEqual)
      case KnownInstruction.OpULessThan(arguments) =>
        binary(instruction.name, arguments, BinaryOperation.UnsignedLessThan)
      case KnownInstruction.OpULessThanEqual(arguments) =>
        binary(instruction.name, arguments, BinaryOperation.UnsignedLessThanEqual)
      case KnownInstruction.OpUGreaterThan(arguments) =>
        binary(instruction.name, arguments, BinaryOperation.UnsignedGreaterThan)
      case KnownInstruction.OpUGreaterThanEqual(arguments) =>
        binary(instruction.name, arguments, BinaryOperation.UnsignedGreaterThanEqual)
      case KnownInstruction.OpSelect(arguments) =>
        val operands = operandBits(arguments)
        requireOperands(instruction.name, operands, 5)
          .map(_ => ExecutableInstruction.Select(operands(1), operands(2), operands(3), operands(4)))
      case KnownInstruction.OpBranch(arguments) =>
        val operands = operandBits(arguments)
        requireOperands(instruction.name, operands, 1).flatMap(_ =>
          labels.get(operands(0)).toRight(LoweringError.Malformed(instruction.name, s"unknown label %${operands(0)}"))
        ).map(ExecutableInstruction.Jump.apply)
      case KnownInstruction.OpBranchConditional(arguments) =>
        val operands = operandBits(arguments)
        requireOperands(instruction.name, operands, 3).flatMap { _ =>
          for {
            whenTrue <- labels.get(operands(1)).toRight(
              LoweringError.Malformed(instruction.name, s"unknown label %${operands(1)}")
            )
            whenFalse <- labels.get(operands(2)).toRight(
              LoweringError.Malformed(instruction.name, s"unknown label %${operands(2)}")
            )
          } yield ExecutableInstruction.JumpIf(operands(0), whenTrue, whenFalse)
        }
      case KnownInstruction.OpReturn(_)              => Right(ExecutableInstruction.Return(None))
      case KnownInstruction.OpReturnValue(arguments) =>
        val operands = operandBits(arguments)
        requireOperands(instruction.name, operands, 1)
          .map(_ => ExecutableInstruction.Return(Some(operands(0))))
      case value if isIgnored(value.name) => Right(ExecutableInstruction.Ignore(value.name))
      case value => Right(ExecutableInstruction.Unsupported(value.name, "semantic lowering is not implemented"))
    }

  private def operandBits(arguments: InstructionArguments): Vector[Int] = arguments.operands.map(_.bits)

  private def requireOperands(
      name: String,
      operands: Vector[Int],
      count: Int
  ): Either[LoweringError, Unit] =
    if (operands.size >= count) Right(())
    else Left(LoweringError.Malformed(name, s"expected $count operands, found ${operands.size}"))

  private def binary(
      name: String,
      arguments: InstructionArguments,
      operation: BinaryOperation
  ): Either[LoweringError, ExecutableInstruction] = {
    val operands = operandBits(arguments)
    requireOperands(name, operands, 4)
      .map(_ => ExecutableInstruction.Binary(name, operands(1), operands(2), operands(3), operation))
  }

  private def isIgnored(name: String): Boolean =
    name == "OpNop" || name == "OpCapability" || name == "OpExtension" || name == "OpExtInstImport" ||
      name == "OpMemoryModel" || name == "OpEntryPoint" || name == "OpExecutionMode" || name == "OpSource" ||
      name == "OpSourceExtension" || name == "OpString" || name == "OpName" || name == "OpMemberName" ||
      name.startsWith("OpDecorate") || name.startsWith("OpMemberDecorate") || name.startsWith("OpType") ||
      name == "OpFunction" || name == "OpFunctionParameter" || name == "OpFunctionEnd" || name == "OpLabel"
}

extension (instruction: ExecutableInstruction) {
  def execute(context: ExecutionContext, pc: Pc): Step = instruction match {
    case ExecutableInstruction.Ignore(_)                         => Step.Advance
    case ExecutableInstruction.Constant(resultId, value)         => writeStep(context, resultId, value)
    case ExecutableInstruction.Variable(resultId, initializerId) =>
      val address = resultId.toLong & 0xffffffffL
      initializerId match {
        case None     => writeStep(context, resultId, address)
        case Some(id) => context.value(id, pc) match {
            case Left(fault)    => Step.Trap(fault)
            case Right(initial) => context.writeMemory(address, initial) match {
                case Left(fault) => Step.Trap(fault)
                case Right(())   => writeStep(context, resultId, address)
              }
          }
      }
    case ExecutableInstruction.Load(resultId, pointerId) =>
      context.value(pointerId, pc) match {
        case Left(fault)    => Step.Trap(fault)
        case Right(address) =>
          context.readMemory(address).fold(Step.Trap.apply, value => writeStep(context, resultId, value))
      }
    case ExecutableInstruction.Store(pointerId, valueId) =>
      (context.value(pointerId, pc), context.value(valueId, pc)) match {
        case (Right(address), Right(value)) =>
          context.writeMemory(address, value).fold(Step.Trap.apply, _ => Step.Advance)
        case (Left(fault), _) => Step.Trap(fault)
        case (_, Left(fault)) => Step.Trap(fault)
      }
    case ExecutableInstruction.Copy(resultId, sourceId) =>
      context.value(sourceId, pc).fold(Step.Trap.apply, value => writeStep(context, resultId, value))
    case ExecutableInstruction.Binary(_, resultId, leftId, rightId, operation) =>
      (context.value(leftId, pc), context.value(rightId, pc)) match {
        case (Right(left), Right(right)) => writeStep(context, resultId, evaluate(operation, left, right))
        case (Left(fault), _)            => Step.Trap(fault)
        case (_, Left(fault))            => Step.Trap(fault)
      }
    case ExecutableInstruction.Select(resultId, conditionId, trueId, falseId) =>
      context.value(conditionId, pc) match {
        case Left(fault)      => Step.Trap(fault)
        case Right(condition) =>
          val selected = if (condition != 0L) trueId else falseId
          context.value(selected, pc).fold(Step.Trap.apply, value => writeStep(context, resultId, value))
      }
    case ExecutableInstruction.Jump(target)                             => Step.Jump(target)
    case ExecutableInstruction.JumpIf(conditionId, whenTrue, whenFalse) =>
      context.value(conditionId, pc).fold(Step.Trap.apply, value => Step.Jump(if (value != 0L) whenTrue else whenFalse))
    case ExecutableInstruction.Return(_)                 => Step.Exit(LaneMask.unsafe(1L))
    case ExecutableInstruction.Unsupported(name, detail) => Step.Trap(ExecutionFault.Unsupported(name, detail))
  }

  private def writeStep(context: ExecutionContext, id: Int, value: Long): Step =
    context.write(id, value).fold(Step.Trap.apply, _ => Step.Advance)

  private def evaluate(operation: BinaryOperation, left: Long, right: Long): Long = operation match {
    case BinaryOperation.Add                      => left + right
    case BinaryOperation.Subtract                 => left - right
    case BinaryOperation.Multiply                 => left * right
    case BinaryOperation.BitAnd                   => left & right
    case BinaryOperation.BitOr                    => left | right
    case BinaryOperation.BitXor                   => left ^ right
    case BinaryOperation.ShiftLeft                => left << (right.toInt & 31)
    case BinaryOperation.LogicalShiftRight        => left >>> (right.toInt & 31)
    case BinaryOperation.Equal                    => if (left == right) 1L else 0L
    case BinaryOperation.NotEqual                 => if (left != right) 1L else 0L
    case BinaryOperation.UnsignedLessThan         => if (java.lang.Long.compareUnsigned(left, right) < 0) 1L else 0L
    case BinaryOperation.UnsignedLessThanEqual    => if (java.lang.Long.compareUnsigned(left, right) <= 0) 1L else 0L
    case BinaryOperation.UnsignedGreaterThan      => if (java.lang.Long.compareUnsigned(left, right) > 0) 1L else 0L
    case BinaryOperation.UnsignedGreaterThanEqual => if (java.lang.Long.compareUnsigned(left, right) >= 0) 1L else 0L
  }
}

enum ExecutionResult {
  case Completed(values: Map[Int, Long], memory: Map[Long, Long], instructions: Long)
  case Fault(fault: ExecutionFault, values: Map[Int, Long], memory: Map[Long, Long], instructions: Long)
}

object Interpreter {
  def run(
      executable: SpirvExecutable,
      initialValues: Map[Int, Long] = Map.empty,
      initialMemory: Map[Long, Long] = Map.empty,
      limits: ExecutionLimits = ExecutionLimits.Default,
      cancellation: CancellationToken = CancellationToken.Never
  ): ExecutionResult = {
    val budget = ExecutionBudget(limits.maxInstructions) match {
      case Left(message) =>
        return ExecutionResult.Fault(ExecutionFault.InvalidState(message), initialValues, initialMemory, 0L)
      case Right(value) => value
    }
    val context                       = new ExecutionContext(initialValues, initialMemory)
    var pc                            = Pc.Entry
    var running                       = true
    var fault: Option[ExecutionFault] = None
    while (running && fault.isEmpty)
      if (cancellation.isCancelled) fault = Some(ExecutionFault.Cancelled)
      else budget.consume() match {
        case Left(value) => fault = Some(value)
        case Right(())   =>
          executable.program.at(pc) match {
            case Left(value)        => fault = Some(value)
            case Right(instruction) => instruction.execute(context, pc) match {
                case Step.Advance => pc.next match {
                    case Left(value) => fault = Some(value)
                    case Right(next) => pc = next
                  }
                case Step.Jump(target)   => pc = target
                case Step.Exit(_)        => running = false
                case Step.Trap(value)    => fault = Some(value)
                case Step.Split(_, _, _) =>
                  fault = Some(ExecutionFault.InvalidState("single-invocation SPIR-V executor cannot split"))
                case Step.Suspend(_) =>
                  fault = Some(ExecutionFault.Unsupported("suspend", "scheduler waits are not implemented"))
              }
          }
      }
    fault match {
      case Some(value) => ExecutionResult.Fault(value, context.snapshot, context.memorySnapshot, budget.used)
      case None        => ExecutionResult.Completed(context.snapshot, context.memorySnapshot, budget.used)
    }
  }
}
