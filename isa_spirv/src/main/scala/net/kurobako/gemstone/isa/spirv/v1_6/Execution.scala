package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.Col

import net.kurobako.gemstone.execution.*
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

enum SPIRVWrite extends Write {
  case Value(id: Int, value: Long)
  case Memory(address: Long, value: Long)
}

final class ExecutionContext private[v1_6] (initialValues: Map[Int, Long], initialMemory: Map[Long, Long]) {
  private val values  = mutable.HashMap.from(initialValues)
  private val memory  = mutable.HashMap.from(initialMemory)
  private val effects = new Effects[SPIRVWrite]

  def value(id: Int, pc: Pc): Either[ExecutionFault, Long] =
    values.get(id).toRight(ExecutionFault.UndefinedRegister(s"%$id", pc, 0))

  def snapshot: Map[Int, Long]        = values.toMap
  def memorySnapshot: Map[Long, Long] = memory.toMap

  private[v1_6] def write(id: Int, value: Long): Either[ExecutionFault, Unit] = {
    effects.stage(SPIRVWrite.Value(id, value & 0xffffffffL))
    commitEffects()
  }

  private[v1_6] def writeMemory(address: Long, value: Long): Either[ExecutionFault, Unit] = {
    effects.stage(SPIRVWrite.Memory(address, value & 0xffffffffL))
    commitEffects()
  }

  private[v1_6] def readMemory(address: Long): Either[ExecutionFault, Long] =
    memory.get(address).toRight(ExecutionFault.Memory(s"undefined SPIR-V memory address 0x${address.toHexString}"))

  private def commitEffects(): Either[ExecutionFault, Unit] =
    effects.commit(
      _ => Right(()),
      {
        case SPIRVWrite.Value(resultId, result) => values.update(resultId, result)
        case SPIRVWrite.Memory(address, result) => memory.update(address, result)
      }
    )
}

final case class SPIRVExecutable(program: ExecutableProgram[ExecutableInstruction])

enum LoweringError {
  case Malformed(instruction: String, detail: String)

  def message: String = this match {
    case Malformed(instruction, detail) => s"$instruction: $detail"
  }
}

object Lowering {
  def lower(module: Module): Either[LoweringError, SPIRVExecutable] = {
    val labels = module.instructions.zipWithIndex.collect {
      case (Instruction.Label(dest), index) => dest.bits -> Pc.unsafe(index)
    }.toMap
    val lowered = Col.newBuilder[ExecutableInstruction]
    var index   = 0
    while (index < module.instructions.size) {
      val instruction = module.instructions(index)
      lowerInstruction(instruction, labels) match {
        case Left(error)  => return Left(error)
        case Right(value) =>
          lowered += value
          index += 1
      }
    }
    Right(SPIRVExecutable(ExecutableProgram(lowered.result())))
  }

  private def lowerInstruction(
      instruction: Instruction,
      labels: Map[Int, Pc]
  ): Either[LoweringError, ExecutableInstruction] = instruction match {
    case Instruction.Constant(_, dest, value)     => constant(instruction.opcode, dest, value)
    case Instruction.SpecConstant(_, dest, value) => constant(instruction.opcode, dest, value)
    case Instruction.ConstantTrue(_, dest) => id(instruction.opcode, dest).map(ExecutableInstruction.Constant(_, 1L))
    case Instruction.SpecConstantTrue(_, dest) =>
      id(instruction.opcode, dest).map(ExecutableInstruction.Constant(_, 1L))
    case Instruction.ConstantFalse(_, dest) => id(instruction.opcode, dest).map(ExecutableInstruction.Constant(_, 0L))
    case Instruction.SpecConstantFalse(_, dest) =>
      id(instruction.opcode, dest).map(ExecutableInstruction.Constant(_, 0L))
    case Instruction.Variable(_, dest, _, initializer) =>
      for {
        result  <- id(instruction.opcode, dest)
        initial <- optionalId(instruction.opcode, initializer)
      } yield ExecutableInstruction.Variable(result, initial)
    case Instruction.Load(_, dest, pointer, _) =>
      for {
        result    <- id(instruction.opcode, dest)
        pointerId <- id(instruction.opcode, pointer)
      } yield ExecutableInstruction.Load(result, pointerId)
    case Instruction.Store(pointer, src, _) =>
      for {
        pointerId <- id(instruction.opcode, pointer)
        sourceId  <- id(instruction.opcode, src)
      } yield ExecutableInstruction.Store(pointerId, sourceId)
    case Instruction.CopyObject(_, dest, src) =>
      for {
        result   <- id(instruction.opcode, dest)
        sourceId <- id(instruction.opcode, src)
      } yield ExecutableInstruction.Copy(result, sourceId)
    case Instruction.IAdd(_, dest, srcA, srcB) => binary(instruction.opcode, dest, srcA, srcB, BinaryOperation.Add)
    case Instruction.ISub(_, dest, srcA, srcB) => binary(instruction.opcode, dest, srcA, srcB, BinaryOperation.Subtract)
    case Instruction.IMul(_, dest, srcA, srcB) => binary(instruction.opcode, dest, srcA, srcB, BinaryOperation.Multiply)
    case Instruction.BitwiseAnd(_, dest, srcA, srcB) =>
      binary(instruction.opcode, dest, srcA, srcB, BinaryOperation.BitAnd)
    case Instruction.BitwiseOr(_, dest, srcA, srcB) =>
      binary(instruction.opcode, dest, srcA, srcB, BinaryOperation.BitOr)
    case Instruction.BitwiseXor(_, dest, srcA, srcB) =>
      binary(instruction.opcode, dest, srcA, srcB, BinaryOperation.BitXor)
    case Instruction.ShiftLeftLogical(_, dest, srcA, srcB) =>
      binary(instruction.opcode, dest, srcA, srcB, BinaryOperation.ShiftLeft)
    case Instruction.ShiftRightLogical(_, dest, srcA, srcB) =>
      binary(instruction.opcode, dest, srcA, srcB, BinaryOperation.LogicalShiftRight)
    case Instruction.IEqual(_, dest, srcA, srcB) => binary(instruction.opcode, dest, srcA, srcB, BinaryOperation.Equal)
    case Instruction.INotEqual(_, dest, srcA, srcB) =>
      binary(instruction.opcode, dest, srcA, srcB, BinaryOperation.NotEqual)
    case Instruction.ULessThan(_, dest, srcA, srcB) =>
      binary(instruction.opcode, dest, srcA, srcB, BinaryOperation.UnsignedLessThan)
    case Instruction.ULessThanEqual(_, dest, srcA, srcB) =>
      binary(instruction.opcode, dest, srcA, srcB, BinaryOperation.UnsignedLessThanEqual)
    case Instruction.UGreaterThan(_, dest, srcA, srcB) =>
      binary(instruction.opcode, dest, srcA, srcB, BinaryOperation.UnsignedGreaterThan)
    case Instruction.UGreaterThanEqual(_, dest, srcA, srcB) =>
      binary(instruction.opcode, dest, srcA, srcB, BinaryOperation.UnsignedGreaterThanEqual)
    case Instruction.Select(_, dest, pred, trueValue, falseValue) =>
      for {
        result    <- id(instruction.opcode, dest)
        condition <- id(instruction.opcode, pred)
        whenTrue  <- id(instruction.opcode, trueValue)
        whenFalse <- id(instruction.opcode, falseValue)
      } yield ExecutableInstruction.Select(result, condition, whenTrue, whenFalse)
    case Instruction.Branch(targetLabel) =>
      id(instruction.opcode, targetLabel).flatMap(target =>
        labels.get(target).toRight(LoweringError.Malformed(instruction.opcode, s"unknown label %$target"))
      ).map(ExecutableInstruction.Jump.apply)
    case Instruction.BranchConditional(pred, trueLabel, falseLabel, _) =>
      for {
        condition <- id(instruction.opcode, pred)
        trueId    <- id(instruction.opcode, trueLabel)
        falseId   <- id(instruction.opcode, falseLabel)
        whenTrue  <- labels.get(trueId).toRight(LoweringError.Malformed(instruction.opcode, s"unknown label %$trueId"))
        whenFalse <-
          labels.get(falseId).toRight(LoweringError.Malformed(instruction.opcode, s"unknown label %$falseId"))
      } yield ExecutableInstruction.JumpIf(condition, whenTrue, whenFalse)
    case Instruction.Return             => Right(ExecutableInstruction.Return(None))
    case Instruction.ReturnValue(value) =>
      id(instruction.opcode, value).map(id => ExecutableInstruction.Return(Some(id)))
    case Instruction.Nop | Instruction.FunctionEnd => Right(ExecutableInstruction.Ignore(instruction.opcode))
    case _: Instruction.Capability | _: Instruction.Extension | _: Instruction.ExtInstImport |
        _: Instruction.MemoryModel | _: Instruction.EntryPoint | _: Instruction.ExecutionMode |
        _: Instruction.Source | _: Instruction.SourceExtension | _: Instruction.String | _: Instruction.Name |
        _: Instruction.MemberName | _: Instruction.Decorate | _: Instruction.MemberDecorate |
        _: Instruction.DecorateId | _: Instruction.MemberDecorateIdEXT | _: Instruction.DecorateString |
        _: Instruction.MemberDecorateString | _: Instruction.TypeVoid | _: Instruction.TypeBool |
        _: Instruction.TypeInt | _: Instruction.TypeFloat | _: Instruction.TypeVector | _: Instruction.TypeMatrix |
        _: Instruction.TypeImage | _: Instruction.TypeSampler | _: Instruction.TypeSampledImage |
        _: Instruction.TypeArray | _: Instruction.TypeRuntimeArray | _: Instruction.TypeStruct |
        _: Instruction.TypeOpaque | _: Instruction.TypePointer | _: Instruction.TypeFunction |
        _: Instruction.TypeEvent | _: Instruction.TypeDeviceEvent | _: Instruction.TypeReserveId |
        _: Instruction.TypeQueue | _: Instruction.TypePipe | _: Instruction.TypeForwardPointer |
        _: Instruction.TypePipeStorage | _: Instruction.TypeNamedBarrier | _: Instruction.TypeTensorARM |
        _: Instruction.TypeGraphARM | _: Instruction.TypeUntypedPointerKHR | _: Instruction.TypeCooperativeMatrixKHR |
        _: Instruction.TypeRayQueryKHR | _: Instruction.TypeNodePayloadArrayAMDX | _: Instruction.TypeBufferEXT |
        _: Instruction.TypeHitObjectNV | _: Instruction.TypeVectorIdEXT | _: Instruction.TypeHitObjectEXT |
        _: Instruction.TypeAccelerationStructureKHR | _: Instruction.TypeCooperativeMatrixNV |
        _: Instruction.TypeTensorLayoutNV | _: Instruction.TypeTensorViewNV | _: Instruction.TypeVmeImageINTEL |
        _: Instruction.TypeAvcImePayloadINTEL | _: Instruction.TypeAvcRefPayloadINTEL |
        _: Instruction.TypeAvcSicPayloadINTEL | _: Instruction.TypeAvcMcePayloadINTEL |
        _: Instruction.TypeAvcMceResultINTEL | _: Instruction.TypeAvcImeResultINTEL |
        _: Instruction.TypeAvcImeResultSingleReferenceStreamoutINTEL |
        _: Instruction.TypeAvcImeResultDualReferenceStreamoutINTEL |
        _: Instruction.TypeAvcImeSingleReferenceStreaminINTEL |
        _: Instruction.TypeAvcImeDualReferenceStreaminINTEL | _: Instruction.TypeAvcRefResultINTEL |
        _: Instruction.TypeAvcSicResultINTEL | _: Instruction.TypeBufferSurfaceINTEL |
        _: Instruction.TypeStructContinuedINTEL | _: Instruction.TypeTaskSequenceALTERA |
        _: Instruction.Function | _: Instruction.FunctionParameter | _: Instruction.Label =>
      Right(ExecutableInstruction.Ignore(instruction.opcode))
    case value => Right(ExecutableInstruction.Unsupported(value.opcode, "semantic lowering is not implemented"))
  }

  private def constant(name: String, dest: Operand, value: Operand): Either[LoweringError, ExecutableInstruction] =
    for {
      result <- id(name, dest)
      bits   <- word(name, value)
    } yield ExecutableInstruction.Constant(result, bits.toLong & 0xffffffffL)

  private def binary(
      name: String,
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      operation: BinaryOperation
  ): Either[LoweringError, ExecutableInstruction] =
    for {
      result <- id(name, dest)
      left   <- id(name, srcA)
      right  <- id(name, srcB)
    } yield ExecutableInstruction.Binary(name, result, left, right, operation)

  private def id(name: String, operand: Operand): Either[LoweringError, Int] = word(name, operand)
  private def optionalId(name: String, operand: Option[Operand]): Either[LoweringError, Option[Int]] = operand match {
    case None        => Right(None)
    case Some(value) => id(name, value).map(Some.apply)
  }
  private def word(name: String, operand: Operand): Either[LoweringError, Int] =
    operand.words match {
      case Col(value) => Right(value.bits)
      case values     => Left(LoweringError.Malformed(name, s"${operand.kind} has ${values.size} words"))
    }

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
      executable: SPIRVExecutable,
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
        case Right(())   => executable.program.at(pc) match {
            case Left(value)  => fault = Some(value)
            case Right(value) => value.execute(context, pc) match {
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
