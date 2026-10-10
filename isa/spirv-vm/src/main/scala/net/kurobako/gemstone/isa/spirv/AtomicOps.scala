package net.kurobako.gemstone.isa.spirv

import Op.*
import VMInvocation.*
import VMLowering.*
import VMMemory.*
import VMProgram.*
import net.kurobako.gemstone.binary.Bits.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.vm.*

/** Atomic memory operations and memory barriers. */
private object AtomicOps {
  import Operand.*

  def lower(site: Site): PartialFunction[Instruction, Op] = {
    case Instruction.AtomicStore(pointer, scope, semantics, value) =>
      store(site, id(pointer), id(scope), id(semantics), id(value))
    case Instruction.AtomicLoad(t, d, pointer, scope, semantics) =>
      load(site, resultType = id(t), dest = id(d), pointer = id(pointer), scope = id(scope), semantics = id(semantics))
    case Instruction.AtomicExchange(t, d, pointer, scope, semantics, value) =>
      modify(
        site,
        "OpAtomicExchange",
        AtomicOperation.Exchange,
        id(t),
        id(d),
        id(pointer),
        id(scope),
        id(semantics),
        Some(id(value))
      )
    case Instruction.AtomicCompareExchange(resultType, dest, pointer, scope, equal, unequal, value, comparator) =>
      compareExchange(site, resultType, dest, pointer, scope, equal, unequal, value, comparator)
    case Instruction.AtomicCompareExchangeWeak(resultType, dest, pointer, scope, equal, unequal, value, comparator) =>
      compareExchange(site, resultType, dest, pointer, scope, equal, unequal, value, comparator)
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
    case Instruction.AtomicIIncrement(t, d, pointer, scope, semantics) =>
      integer(site, "OpAtomicIIncrement", AtomicOperation.Increment, t, d, pointer, scope, semantics, None)
    case Instruction.AtomicIDecrement(t, d, pointer, scope, semantics) =>
      integer(site, "OpAtomicIDecrement", AtomicOperation.Decrement, t, d, pointer, scope, semantics, None)
    case Instruction.AtomicIAdd(t, d, pointer, scope, semantics, value) =>
      integer(site, "OpAtomicIAdd", AtomicOperation.Add, t, d, pointer, scope, semantics, Some(value))
    case Instruction.AtomicISub(t, d, pointer, scope, semantics, value) =>
      integer(site, "OpAtomicISub", AtomicOperation.Subtract, t, d, pointer, scope, semantics, Some(value))
    case Instruction.AtomicSMin(t, d, pointer, scope, semantics, value) =>
      integer(site, "OpAtomicSMin", AtomicOperation.MinimumSigned, t, d, pointer, scope, semantics, Some(value))
    case Instruction.AtomicUMin(t, d, pointer, scope, semantics, value) =>
      integer(site, "OpAtomicUMin", AtomicOperation.MinimumUnsigned, t, d, pointer, scope, semantics, Some(value))
    case Instruction.AtomicSMax(t, d, pointer, scope, semantics, value) =>
      integer(site, "OpAtomicSMax", AtomicOperation.MaximumSigned, t, d, pointer, scope, semantics, Some(value))
    case Instruction.AtomicUMax(t, d, pointer, scope, semantics, value) =>
      integer(site, "OpAtomicUMax", AtomicOperation.MaximumUnsigned, t, d, pointer, scope, semantics, Some(value))
    case Instruction.AtomicAnd(t, d, pointer, scope, semantics, value) =>
      integer(site, "OpAtomicAnd", AtomicOperation.And, t, d, pointer, scope, semantics, Some(value))
    case Instruction.AtomicOr(t, d, pointer, scope, semantics, value) =>
      integer(site, "OpAtomicOr", AtomicOperation.Or, t, d, pointer, scope, semantics, Some(value))
    case Instruction.AtomicXor(t, d, pointer, scope, semantics, value) =>
      integer(site, "OpAtomicXor", AtomicOperation.Xor, t, d, pointer, scope, semantics, Some(value))
    case Instruction.AtomicFAddEXT(t, d, pointer, scope, semantics, value) =>
      floating(site, "OpAtomicFAddEXT", AtomicOperation.FloatAdd, t, d, pointer, scope, semantics, value)
    case Instruction.AtomicFMinEXT(t, d, pointer, scope, semantics, value) =>
      floating(site, "OpAtomicFMinEXT", AtomicOperation.FloatMinimum, t, d, pointer, scope, semantics, value)
    case Instruction.AtomicFMaxEXT(t, d, pointer, scope, semantics, value) =>
      floating(site, "OpAtomicFMaxEXT", AtomicOperation.FloatMaximum, t, d, pointer, scope, semantics, value)
    case Instruction.MemoryBarrier(scope, semantics) =>
      step((execution, memory) =>
        memoryControls(execution.values, id(scope), id(semantics), "OpMemoryBarrier").flatMap(_ => memory.fence())
      )
  }

  // Uniform includes legacy BufferBlock storage buffers, alongside StorageBuffer.
  private val atomicStorage = Set(
    StorageClass.Uniform,
    StorageClass.Workgroup,
    StorageClass.CrossWorkgroup,
    StorageClass.Generic,
    StorageClass.AtomicCounter,
    StorageClass.Image,
    StorageClass.StorageBuffer,
    StorageClass.PhysicalStorageBuffer
  )

  /** The 32- or 64-bit scalar integer or floating-point width of an atomic value type. */
  private def width(program: VMProgram, typeId: Int, opcode: String): Either[Fault, Int in (32, 64)] =
    program.numericShape(typeId) match {
      case Some((width, 1)) if width.raw == 32 || width.raw == 64 => Right(width.raw.unsafe[Int in (32, 64)])
      case _ => Left(Fault.InvalidRequest(s"$opcode requires a 32- or 64-bit scalar type"))
    }

  private def target(values: InvocationValues, pointer: Int, typeId: Int, opcode: String): Either[Fault, Pointer] =
    for {
      location <- address(values, pointer)
      _        <- Either.cond(
        location.pointee == typeId,
        (),
        Fault.InvalidRequest(s"$opcode pointer pointee %${location.pointee} does not match type %$typeId")
      )
      _ <- Either.cond(
        atomicStorage.contains(location.storageClass),
        (),
        Fault.InvalidRequest(s"$opcode pointer storage class ${location.storageClass} does not support atomic access")
      )
    } yield location

  private def store(site: Site, pointer: Int, scope: Int, semantics: Int, value: Int): Op = {
    val opcode    = "OpAtomicStore"
    val valueType = if (site.program.scalarSlot(value)) site.program.valueType(value) else -1
    width(site.program, valueType, opcode) match {
      case Left(error)  => Fail(error)
      case Right(width) =>
        step { (execution, memory) =>
          val values = execution.values
          for {
            location <- target(values, pointer, valueType, opcode)
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
            location <- target(values, pointer, resultType, opcode)
            _        <- memoryControls(values, scope, semantics, opcode)
            result   <- memory.atomicReadScalar(location, width.toMemoryValueByteWidth)
          } yield values.updateScalar(dest, resultType, result & mask(width))
        }
    }
  }

  private def matching(site: Site, value: Int, resultType: Int): Boolean =
    site.program.scalarSlot(value) && site.program.valueType(value) == resultType

  /** A read-modify-write whose operand, when present, must have the result type. Integer read-modify-writes accept any
    * storage class whose pointee matches.
    */
  private def modify(
      site: Site,
      opcode: String,
      operation: AtomicOperation,
      resultType: Int,
      dest: Int,
      pointer: Int,
      scope: Int,
      semantics: Int,
      value: Option[Int],
      anyStorage: Boolean = false
  ): Op = width(site.program, resultType, opcode) match {
    case Left(error)                                              => Fail(error)
    case Right(_) if value.exists(!matching(site, _, resultType)) =>
      Fail(Fault.InvalidRequest(s"$opcode requires matching result and value types"))
    case Right(width) =>
      val bits: ScalarBitWidth = width.widen
      step { (execution, memory) =>
        val values  = execution.values
        val operand = value.fold(1L)(values.scalars(_))
        val located =
          if (!anyStorage) target(values, pointer, resultType, opcode)
          else address(values, pointer).flatMap(location =>
            Either.cond(
              location.pointee == resultType,
              location,
              Fault.InvalidRequest(
                s"$opcode pointer pointee %${location.pointee} does not match result type %$resultType"
              )
            )
          )
        for {
          location <- located
          _        <- memoryControls(values, scope, semantics, opcode)
          previous <- memory.atomicModifyScalar(location, width.toMemoryValueByteWidth)(operation(_, operand, bits))
        } yield values.updateScalar(dest, resultType, previous & mask(width))
      }
  }

  private def integer(
      site: Site,
      opcode: String,
      operation: AtomicOperation,
      resultType: Operand,
      dest: Operand,
      pointer: Operand,
      scope: Operand,
      semantics: Operand,
      value: Option[Operand]
  ): Op = site.program.integerShape(id(resultType)) match {
    case Some((width, 1)) if width.raw == 32 || width.raw == 64 =>
      modify(
        site,
        opcode,
        operation,
        id(resultType),
        id(dest),
        id(pointer),
        id(scope),
        id(semantics),
        value.map(id(_)),
        anyStorage = true
      )
    case _ => Fail(Fault.InvalidRequest(s"$opcode requires a 32- or 64-bit scalar integer result"))
  }

  private def floating(
      site: Site,
      opcode: String,
      operation: AtomicOperation,
      resultType: Operand,
      dest: Operand,
      pointer: Operand,
      scope: Operand,
      semantics: Operand,
      value: Operand
  ): Op = site.program.floatingShape(id(resultType)) match {
    case Some((width, 1)) =>
      if (!matching(site, id(value), id(resultType)))
        Fail(Fault.InvalidRequest(s"$opcode requires matching result and value types"))
      else {
        val bits: ScalarBitWidth = width.widen
        step { (execution, memory) =>
          val values  = execution.values
          val operand = values.scalars(id(value))
          for {
            location <- target(values, id(pointer), id(resultType), opcode)
            _        <- memoryControls(values, id(scope), id(semantics), opcode)
            previous <- memory.atomicModifyScalar(location, width.toFloatByteWidth)(operation(_, operand, bits))
          } yield values.updateScalar(id(dest), id(resultType), previous & mask(width))
        }
      }
    case _ => Fail(Fault.InvalidRequest(s"$opcode requires a 16-, 32-, or 64-bit scalar floating-point result"))
  }

  private def compareExchange(
      site: Site,
      resultType: Operand,
      dest: Operand,
      pointer: Operand,
      scope: Operand,
      equal: Operand,
      unequal: Operand,
      value: Operand,
      comparator: Operand
  ): Op = {
    val opcode       = "OpAtomicCompareExchange"
    val resultTypeId = id(resultType)
    val destId       = id(dest)
    val pointerId    = id(pointer)
    val scopeId      = id(scope)
    val equalId      = id(equal)
    val unequalId    = id(unequal)
    val valueId      = id(value)
    val comparatorId = id(comparator)
    width(site.program, resultTypeId, opcode) match {
      case Left(error) => Fail(error)
      case Right(_) if !matching(site, valueId, resultTypeId) || !matching(site, comparatorId, resultTypeId) =>
        Fail(Fault.InvalidRequest(s"$opcode requires matching result, value, and comparator types"))
      case Right(width) =>
        val bits = mask(width)
        step { (execution, memory) =>
          val values   = execution.values
          val expected = values.scalars(comparatorId) & bits
          val desired  = values.scalars(valueId) & bits
          for {
            location <- target(values, pointerId, resultTypeId, opcode)
            _        <- memoryControls(values, scopeId, equalId, opcode)
            _        <- memoryControls(values, scopeId, unequalId, opcode)
            previous <- memory.atomicModifyScalar(location, width.toMemoryValueByteWidth)(previous =>
              if ((previous & bits) == expected) desired else previous
            )
          } yield values.updateScalar(destId, resultTypeId, previous & bits)
        }
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
        program.integerShape(location.pointee).exists { case (width, count) => width.raw == 32 && count == 1 },
        (),
        Fault.InvalidRequest(s"$opcode requires a pointer to a 32-bit integer flag")
      )
      _ <- Either.cond(
        atomicStorage.contains(location.storageClass),
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
