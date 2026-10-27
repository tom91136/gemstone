package net.kurobako.gemstone.isa.spirv

import Op.*
import OpBuilder.*
import VMInvocation.*
import VMMemory.*
import VMProgram.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.vm.*

/** Atomic memory operations and memory barriers. */
private object AtomicOps {
  import Operand.*

  def lower(using site: Site): PartialFunction[Instruction, Op] = {
    case Instruction.AtomicStore(pointer, scope, semantics, value) =>
      store(id(pointer), id(scope), id(semantics), id(value))
    case Instruction.AtomicLoad(t, d, pointer, scope, semantics) =>
      load(ResultIds(t, d), pointer = id(pointer), scope = id(scope), semantics = id(semantics))
    case instruction @ Instruction.AtomicExchange(resultType = t, value = value) =>
      exchange(instruction, t, value)
    case instruction @ Instruction.AtomicCompareExchange(
          resultType = resultType,
          value = value,
          comparator = comparator
        ) =>
      compareExchange(instruction, resultType, value, comparator)
    case instruction @ Instruction.AtomicCompareExchangeWeak(
          resultType = resultType,
          value = value,
          comparator = comparator
        ) =>
      compareExchange(instruction, resultType, value, comparator)
    case Instruction.AtomicFlagTestAndSet(t, d, pointer, scope, semantics) =>
      flagTestAndSet(ResultIds(t, d), pointer = id(pointer), scope = id(scope), semantics = id(semantics))
    case Instruction.AtomicFlagClear(pointer, scope, semantics) =>
      flagClear(id(pointer), id(scope), id(semantics))
    case instruction @ Instruction.AtomicIIncrement(resultType = t) =>
      integer(instruction, t, None)
    case instruction @ Instruction.AtomicIDecrement(resultType = t) =>
      integer(instruction, t, None)
    case instruction @ Instruction.AtomicIAdd(resultType = t, value = value) =>
      integer(instruction, t, Some(value))
    case instruction @ Instruction.AtomicISub(resultType = t, value = value) =>
      integer(instruction, t, Some(value))
    case instruction @ Instruction.AtomicSMin(resultType = t, value = value) =>
      integer(instruction, t, Some(value))
    case instruction @ Instruction.AtomicUMin(resultType = t, value = value) =>
      integer(instruction, t, Some(value))
    case instruction @ Instruction.AtomicSMax(resultType = t, value = value) =>
      integer(instruction, t, Some(value))
    case instruction @ Instruction.AtomicUMax(resultType = t, value = value) =>
      integer(instruction, t, Some(value))
    case instruction @ Instruction.AtomicAnd(resultType = t, value = value) =>
      integer(instruction, t, Some(value))
    case instruction @ Instruction.AtomicOr(resultType = t, value = value) =>
      integer(instruction, t, Some(value))
    case instruction @ Instruction.AtomicXor(resultType = t, value = value) =>
      integer(instruction, t, Some(value))
    case instruction @ Instruction.AtomicFAddEXT(resultType = t, value = value) =>
      floating(instruction, t, value)
    case instruction @ Instruction.AtomicFMinEXT(resultType = t, value = value) =>
      floating(instruction, t, value)
    case instruction @ Instruction.AtomicFMaxEXT(resultType = t, value = value) =>
      floating(instruction, t, value)
    case Instruction.MemoryBarrier(scope, semantics) => FenceOp(id(scope), id(semantics))
  }

  /** The 32- or 64-bit scalar integer or floating-point width of an atomic value type. */
  private def width(program: VMProgram, typeId: Int, opcode: String): Either[Fault, Int in (32, 64)] =
    program.numericShape(typeId) match {
      case Some((width, 1)) if width == 32 || width == 64 => Right(width.unsafe[Int in (32, 64)])
      case _ => Left(Fault.InvalidRequest(s"$opcode requires a 32- or 64-bit scalar type"))
    }

  private def store(pointer: Int, scope: Int, semantics: Int, value: Int)(using site: Site): Op = {
    val valueType = if (site.program.scalarSlot(value)) site.program.valueType(value) else -1
    width(site.program, valueType, "OpAtomicStore") match {
      case Left(error)  => Fail(error)
      case Right(width) => AtomicStoreOp(pointer, value, width.toMemoryValueByteWidth, scope, semantics)
    }
  }

  private def load(ids: ResultIds, pointer: Int, scope: Int, semantics: Int)(using site: Site): Op =
    width(site.program, ids.resultType, "OpAtomicLoad") match {
      case Left(error)  => Fail(error)
      case Right(width) =>
        AtomicLoadOp(pointer, width.toMemoryValueByteWidth, ids.dest, ids.resultType, scope, semantics)
    }

  private def mismatched(value: Int, resultType: Int)(using site: Site): Boolean =
    !site.program.scalarSlot(value) || site.program.valueType(value) != resultType

  private def exchange(instruction: Instruction, resultType: Operand, value: Operand)(using site: Site): Op = {
    val opcode = instruction.opcode.raw
    width(site.program, id(resultType), opcode) match {
      case Left(error)                                       => Fail(error)
      case Right(_) if mismatched(id(value), id(resultType)) =>
        Fail(Fault.InvalidRequest(s"$opcode requires matching result and value types"))
      case Right(_) => staged(site, instruction, CoreFamily.Atomic)
    }
  }

  private def integer(instruction: Instruction, resultType: Operand, value: Option[Operand])(using site: Site): Op = {
    val opcode = instruction.opcode.raw
    site.program.integerShape(id(resultType)) match {
      case Some((width, 1)) if width == 32 || width == 64 =>
        if (value.exists(operand => mismatched(id(operand), id(resultType))))
          Fail(Fault.InvalidRequest(s"$opcode requires matching result and value types"))
        else staged(site, instruction, CoreFamily.Atomic)
      case _ => Fail(Fault.InvalidRequest(s"$opcode requires a 32- or 64-bit scalar integer result"))
    }
  }

  private def floating(instruction: Instruction, resultType: Operand, value: Operand)(using site: Site): Op = {
    val opcode = instruction.opcode.raw
    site.program.floatingShape(id(resultType)) match {
      case Some((_, 1)) =>
        if (mismatched(id(value), id(resultType)))
          Fail(Fault.InvalidRequest(s"$opcode requires matching result and value types"))
        else staged(site, instruction, CoreFamily.Atomic)
      case _ => Fail(Fault.InvalidRequest(s"$opcode requires a 16-, 32-, or 64-bit scalar floating-point result"))
    }
  }

  private def compareExchange(instruction: Instruction, resultType: Operand, value: Operand, comparator: Operand)(using
      site: Site
  ): Op = {
    val opcode       = "OpAtomicCompareExchange"
    val resultTypeId = id(resultType)
    width(site.program, resultTypeId, opcode) match {
      case Left(error)                                                                                 => Fail(error)
      case Right(_) if mismatched(id(value), resultTypeId) || mismatched(id(comparator), resultTypeId) =>
        Fail(Fault.InvalidRequest(s"$opcode requires matching result, value, and comparator types"))
      case Right(_) => staged(site, instruction, CoreFamily.Atomic)
    }
  }

  private def flagTestAndSet(ids: ResultIds, pointer: Int, scope: Int, semantics: Int)(using site: Site): Op =
    if (!site.program.runtimeType(ids.resultType).contains(RuntimeType.Bool))
      Fail(Fault.InvalidRequest("OpAtomicFlagTestAndSet requires a Boolean result"))
    else FlagTestAndSetOp(pointer, ids.dest, ids.resultType, scope, semantics)

  private def flagClear(pointer: Int, scope: Int, semantics: Int): Op = FlagClearOp(pointer, scope, semantics)

  private class AtomicLoadOp(pointer: Int, size: Int in 1 ~ 8, target: Int, typeId: Int, scope: Int, semantics: Int)
      extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      MemoryAccessSemantics.atomicLoad(InvocationMachine(execution))(pointer, size, target, typeId, scope, semantics)
  }

  private class AtomicStoreOp(pointer: Int, source: Int, size: Int in 1 ~ 8, scope: Int, semantics: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      MemoryAccessSemantics.atomicStore(InvocationMachine(execution))(pointer, source, size, scope, semantics)
  }

  private class FlagTestAndSetOp(pointer: Int, target: Int, typeId: Int, scope: Int, semantics: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      MemoryAccessSemantics.flagTestAndSet(InvocationMachine(execution))(pointer, target, typeId, scope, semantics)
  }

  private class FlagClearOp(pointer: Int, scope: Int, semantics: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      MemoryAccessSemantics.flagClear(InvocationMachine(execution))(pointer, scope, semantics)
  }

  private class FenceOp(scope: Int, semantics: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      BarrierSemantics.fence(InvocationMachine(execution))(scope, semantics)
  }
}
