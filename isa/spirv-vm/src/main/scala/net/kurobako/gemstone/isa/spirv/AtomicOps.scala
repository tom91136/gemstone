package net.kurobako.gemstone.isa.spirv

import Op.*
import OpBuilder.*
import VMInvocation.*
import VMMemory.*
import VMProgram.*
import net.kurobako.gemstone.binary.Bits.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.vm.*

/** Atomic memory operations and memory barriers. */
// result type, site and operand ids are the raw operands of each opcode; a holder would allocate per instruction
@SuppressWarnings(Array("baumkuchen:data-clump"))
private object AtomicOps {
  import Operand.*

  def lower(site: Site): PartialFunction[Instruction, Op] = {
    case Instruction.AtomicStore(pointer, scope, semantics, value) =>
      store(site, id(pointer), id(scope), id(semantics), id(value))
    case Instruction.AtomicLoad(t, d, pointer, scope, semantics) =>
      load(site, resultType = id(t), dest = id(d), pointer = id(pointer), scope = id(scope), semantics = id(semantics))
    case instruction @ Instruction.AtomicExchange(resultType = t, value = value) =>
      exchange(site, instruction, t, value)
    case instruction @ Instruction.AtomicCompareExchange(
          resultType = resultType,
          value = value,
          comparator = comparator
        ) =>
      compareExchange(site, instruction, resultType, value, comparator)
    case instruction @ Instruction.AtomicCompareExchangeWeak(
          resultType = resultType,
          value = value,
          comparator = comparator
        ) =>
      compareExchange(site, instruction, resultType, value, comparator)
    case Instruction.AtomicFlagTestAndSet(t, d, pointer, scope, semantics) =>
      flagTestAndSet(
        site,
        resultType = id(t),
        dest = id(d),
        pointer = id(pointer),
        scope = id(scope),
        semantics = id(semantics)
      )
    case Instruction.AtomicFlagClear(pointer, scope, semantics) =>
      flagClear(site, id(pointer), id(scope), id(semantics))
    case instruction @ Instruction.AtomicIIncrement(resultType = t) =>
      integer(site, instruction, t, None)
    case instruction @ Instruction.AtomicIDecrement(resultType = t) =>
      integer(site, instruction, t, None)
    case instruction @ Instruction.AtomicIAdd(resultType = t, value = value) =>
      integer(site, instruction, t, Some(value))
    case instruction @ Instruction.AtomicISub(resultType = t, value = value) =>
      integer(site, instruction, t, Some(value))
    case instruction @ Instruction.AtomicSMin(resultType = t, value = value) =>
      integer(site, instruction, t, Some(value))
    case instruction @ Instruction.AtomicUMin(resultType = t, value = value) =>
      integer(site, instruction, t, Some(value))
    case instruction @ Instruction.AtomicSMax(resultType = t, value = value) =>
      integer(site, instruction, t, Some(value))
    case instruction @ Instruction.AtomicUMax(resultType = t, value = value) =>
      integer(site, instruction, t, Some(value))
    case instruction @ Instruction.AtomicAnd(resultType = t, value = value) =>
      integer(site, instruction, t, Some(value))
    case instruction @ Instruction.AtomicOr(resultType = t, value = value) =>
      integer(site, instruction, t, Some(value))
    case instruction @ Instruction.AtomicXor(resultType = t, value = value) =>
      integer(site, instruction, t, Some(value))
    case instruction @ Instruction.AtomicFAddEXT(resultType = t, value = value) =>
      floating(site, instruction, t, value)
    case instruction @ Instruction.AtomicFMinEXT(resultType = t, value = value) =>
      floating(site, instruction, t, value)
    case instruction @ Instruction.AtomicFMaxEXT(resultType = t, value = value) =>
      floating(site, instruction, t, value)
    case Instruction.MemoryBarrier(scope, semantics) =>
      step((execution, memory) =>
        memoryControls(execution.values, id(scope), id(semantics), "OpMemoryBarrier").flatMap(_ => memory.fence())
      )
  }

  /** The 32- or 64-bit scalar integer or floating-point width of an atomic value type. */
  private def width(program: VMProgram, typeId: Int, opcode: String): Either[Fault, Int in (32, 64)] =
    program.numericShape(typeId) match {
      case Some((width, 1)) if width == 32 || width == 64 => Right(width.unsafe[Int in (32, 64)])
      case _ => Left(Fault.InvalidRequest(s"$opcode requires a 32- or 64-bit scalar type"))
    }

  private def store(site: Site, pointer: Int, scope: Int, semantics: Int, value: Int): Op = {
    val opcode    = "OpAtomicStore"
    val valueType = if (site.program.scalarSlot(value)) site.program.valueType(value) else -1
    width(site.program, valueType, opcode) match {
      case Left(error)  => Fail(error)
      case Right(width) =>
        step { (execution, memory) =>
          val values = execution.values
          for {
            location <- StagedOps.target(values, pointer, valueType, opcode)
            _        <- memoryControls(values, scope, semantics, opcode)
            _ <- memory.atomicWriteScalar(location, values.scalars(value) & mask(width), width.toMemoryValueByteWidth)
          } yield ()
        }
    }
  }

  private def load(site: Site, resultType: Int, dest: Int, pointer: Int, scope: Int, semantics: Int): Op = {
    val opcode = "OpAtomicLoad"
    width(site.program, resultType, opcode) match {
      case Left(error)  => Fail(error)
      case Right(width) =>
        step { (execution, memory) =>
          val values = execution.values
          for {
            location <- StagedOps.target(values, pointer, resultType, opcode)
            _        <- memoryControls(values, scope, semantics, opcode)
            result   <- memory.atomicReadScalar(location, width.toMemoryValueByteWidth)
          } yield values.updateScalar(dest, resultType, result & mask(width))
        }
    }
  }

  private def matching(site: Site, value: Int, resultType: Int): Boolean =
    site.program.scalarSlot(value) && site.program.valueType(value) == resultType

  private def exchange(site: Site, instruction: Instruction, resultType: Operand, value: Operand): Op = {
    val opcode = instruction.opcode.raw
    width(site.program, id(resultType), opcode) match {
      case Left(error)                                            => Fail(error)
      case Right(_) if !matching(site, id(value), id(resultType)) =>
        Fail(Fault.InvalidRequest(s"$opcode requires matching result and value types"))
      case Right(_) => staged(site, instruction, CoreFamily.Atomic)
    }
  }

  private def integer(site: Site, instruction: Instruction, resultType: Operand, value: Option[Operand]): Op = {
    val opcode = instruction.opcode.raw
    site.program.integerShape(id(resultType)) match {
      case Some((width, 1)) if width == 32 || width == 64 =>
        if (value.exists(operand => !matching(site, id(operand), id(resultType))))
          Fail(Fault.InvalidRequest(s"$opcode requires matching result and value types"))
        else staged(site, instruction, CoreFamily.Atomic)
      case _ => Fail(Fault.InvalidRequest(s"$opcode requires a 32- or 64-bit scalar integer result"))
    }
  }

  private def floating(site: Site, instruction: Instruction, resultType: Operand, value: Operand): Op = {
    val opcode = instruction.opcode.raw
    site.program.floatingShape(id(resultType)) match {
      case Some((_, 1)) =>
        if (!matching(site, id(value), id(resultType)))
          Fail(Fault.InvalidRequest(s"$opcode requires matching result and value types"))
        else staged(site, instruction, CoreFamily.Atomic)
      case _ => Fail(Fault.InvalidRequest(s"$opcode requires a 16-, 32-, or 64-bit scalar floating-point result"))
    }
  }

  private def compareExchange(
      site: Site,
      instruction: Instruction,
      resultType: Operand,
      value: Operand,
      comparator: Operand
  ): Op = {
    val opcode       = "OpAtomicCompareExchange"
    val resultTypeId = id(resultType)
    width(site.program, resultTypeId, opcode) match {
      case Left(error) => Fail(error)
      case Right(_) if !matching(site, id(value), resultTypeId) || !matching(site, id(comparator), resultTypeId) =>
        Fail(Fault.InvalidRequest(s"$opcode requires matching result, value, and comparator types"))
      case Right(_) => staged(site, instruction, CoreFamily.Atomic)
    }
  }

  private def flagTarget(
      program: VMProgram,
      values: InvocationValues,
      pointer: Int,
      scope: Int,
      semantics: Int,
      opcode: String
  )
      : Either[Fault, Pointer] =
    for {
      location <- address(values, pointer)
      _        <- Either.cond(
        program.integerShape(location.pointee).exists { case (width, count) => width == 32 && count == 1 } ||
          StagedOps.untypedPointer(values.program, pointer),
        (),
        Fault.InvalidRequest(s"$opcode requires a pointer to a 32-bit integer flag")
      )
      _ <- Either.cond(
        StagedOps.atomicStorage.contains(location.storageClass),
        (),
        Fault.InvalidRequest(s"$opcode pointer storage class does not support atomic access")
      )
      _ <- memoryControls(values, scope, semantics, opcode)
    } yield location

  private def flagTestAndSet(site: Site, resultType: Int, dest: Int, pointer: Int, scope: Int, semantics: Int): Op = {
    val opcode = "OpAtomicFlagTestAndSet"
    if (!site.program.runtimeType(resultType).contains(RuntimeType.Bool))
      Fail(Fault.InvalidRequest(s"$opcode requires a Boolean result"))
    else
      step { (execution, memory) =>
        flagTarget(site.program, execution.values, pointer, scope, semantics, opcode)
          .flatMap(memory.atomicModifyScalar(_, 4)(_ => 1L))
          .map(previous => execution.values.updateScalar(dest, resultType, if (previous == 0L) 0L else 1L))
      }
  }

  private def flagClear(site: Site, pointer: Int, scope: Int, semantics: Int): Op = {
    val opcode = "OpAtomicFlagClear"
    step { (execution, memory) =>
      val values = execution.values
      for {
        location <- flagTarget(site.program, values, pointer, scope, semantics, opcode)
        order    <- scalar(values, semantics)
        _        <- Either.cond(
          (order & 0xaL) == 0L,
          (),
          Fault.InvalidRequest(s"$opcode cannot use Acquire or AcquireRelease memory semantics")
        )
        _ <- memory.atomicWriteScalar(location, 0L, 4)
      } yield ()
    }
  }
}
