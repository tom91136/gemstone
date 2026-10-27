package net.kurobako.gemstone.isa.spirv

import Operand.*
import VMInvocation.*
import VMMemory.*
import VMProgram.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.vm.*

/** SPV_ARM_tensors element access and size queries over the tensor descriptors the bindings hold. */
private object TensorArmOps {

  def lower: PartialFunction[Instruction, Op] = {
    case instruction if TensorArmSemantics.handles(instruction) => TensorArmOp(instruction)
  }

  private class TensorArmOp(instruction: Instruction) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = {
      val program = execution.values.program
      val machine = InvocationMachine(execution)
      instruction match {
        case Instruction.TensorQuerySizeARM(resultType, dest, tensor, dimension) =>
          tensorOf(program, id(tensor)).flatMap(_ =>
            TensorArmSemantics.size(Execute, machine)(
              id(tensor),
              machine.leaf(id(dimension), 0),
              id(dest),
              id(resultType)
            )(machine.advance)
          )
        case Instruction.TensorReadARM(resultType, dest, tensor, coordinates, operands) =>
          val words = TensorArmSemantics.operandWords(operands)
          if (!TensorArmSemantics.supported(words)) Left(unsupported)
          else tensorOf(program, id(tensor)).flatMap((rank, element) =>
            program.scalarMemoryWidth(element).flatMap(bytes =>
              TensorArmSemantics.read(Execute, machine)(
                id(tensor),
                id(coordinates),
                rank,
                count(program, id(resultType)),
                element,
                bytes,
                TensorArmSemantics.outOfBounds(words),
                id(dest),
                id(resultType)
              )(machine.advance)
            )
          )
        case Instruction.TensorWriteARM(tensor, coordinates, source, operands) =>
          val words = TensorArmSemantics.operandWords(operands)
          if (!TensorArmSemantics.supported(words)) Left(unsupported)
          else tensorOf(program, id(tensor)).flatMap((rank, element) =>
            program.scalarMemoryWidth(element).flatMap(bytes =>
              TensorArmSemantics.write(Execute, machine)(
                id(tensor),
                id(coordinates),
                id(source),
                rank,
                count(program, program.valueType(id(source))),
                element,
                bytes
              )(machine.advance)
            )
          )
        case _ => Left(Fault.InvalidRequest(s"${instruction.opcode} is not a tensor access"))
      }
    }
  }

  private val unsupported = Fault.Unsupported(
    "a tensor access's MakeElementAvailable and MakeElementVisible operands need memory scopes, which no VM op makes"
  )

  private def tensorOf(program: VMProgram, tensor: Int): Either[Fault, (Int, Int)] =
    program.runtimeType(program.valueType(tensor)) match {
      case Some(RuntimeType.Tensor(rank, element)) => Right((rank, element))
      case _ => Left(Fault.InvalidRequest(s"SPIR-V tensor %$tensor is not ranked"))
    }

  private def count(program: VMProgram, typeId: Int): Int = program.runtimeType(typeId) match {
    case Some(RuntimeType.Array(_, length)) => length.toInt
    case _                                  => 1
  }
}
