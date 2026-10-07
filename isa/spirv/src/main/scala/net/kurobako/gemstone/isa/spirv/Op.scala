package net.kurobako.gemstone.isa.spirv

import VMInvocation.*
import VMMemory.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.vm.*

private object Op {
  val Done: AddressSpace.ReadResult[Unit] = Right(())

  /** An instruction whose operands failed load-time checks faults only when it executes. */
  class Fail(fault: Fault) extends Op {
    private val result                                                                      = Left(fault)
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = result
  }

  /** An op whose run is a closure over its lowered operands; used where a dedicated class adds nothing. */
  class Dynamic(body: (InvocationState, MemoryState) => AddressSpace.ReadResult[Unit]) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = body(execution, memory)
  }

  /** A step that writes its result and falls through to the next instruction. */
  def step(body: (InvocationState, MemoryState) => AddressSpace.ReadResult[Unit]): Op =
    Dynamic((execution, memory) => body(execution, memory).map(_ => execution.pc += 1))

  /** Suspends the invocation at a collective rendezvous built from its current position. */
  def suspend(
      execution: InvocationState,
      request: (Int, Int) => VMCollectives.Request
  ): AddressSpace.ReadResult[Unit] = {
    execution.suspend(request(execution.currentFunction.id, execution.pc))
    Done
  }

  /** Instructions without an execution effect. */
  object Next extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = {
      execution.pc += 1
      Done
    }
  }

  def undefined(id: Int): Fault.InvalidRequest = Fault.InvalidRequest(s"SPIR-V value %$id is undefined")

  /** The op a lowering produced, or one that faults with its load-time failure. */
  def lowered(op: Either[Fault, Op]): Op = op.fold(Fail(_), identity)

  /** The rounding an `FPRoundingMode` value names, or `default` for any other value. */
  def rounding(mode: Int, default: IEEE754.Rounding): IEEE754.Rounding = mode match {
    case FPRoundingMode.RTE => IEEE754.Rounding.NearestEven
    case FPRoundingMode.RTZ => IEEE754.Rounding.TowardZero
    case FPRoundingMode.RTP => IEEE754.Rounding.Up
    case FPRoundingMode.RTN => IEEE754.Rounding.Down
    case _                  => default
  }

  /** Checks the memory scope and semantics operands of an atomic or barrier. */
  def memoryControls(values: InvocationValues, scope: Int, semantics: Int, opcode: String): Either[Fault, Unit] =
    for {
      memoryScope <- scalar(values, scope)
      _           <- Either.cond(
        memoryScope >= 0L && memoryScope <= 6L,
        (),
        Fault.InvalidRequest(s"$opcode has invalid memory scope $memoryScope")
      )
      memorySemantics <- scalar(values, semantics)
      _               <- Either.cond(
        (memorySemantics & ~0xffdeL) == 0L,
        (),
        Fault.InvalidRequest(
          s"$opcode has unknown memory-semantics bits 0x${java.lang.Long.toHexString(memorySemantics)}"
        )
      )
    } yield ()

  def scalar(values: InvocationValues, id: Int): Either[Fault, Long] =
    if (values.program.scalarSlot(id)) Right(values.scalars(id))
    else values.get(id) match {
      case Some(VMValue.Bits(_, Array(value))) => Right(value)
      case Some(VMValue.Bits(_, lanes)) => Left(Fault.InvalidRequest(s"SPIR-V value %$id has ${lanes.length} lanes"))
      case Some(_)                      => Left(Fault.InvalidRequest(s"SPIR-V value %$id is not numeric"))
      case None                         => Left(undefined(id))
    }

  def bits(values: InvocationValues, id: Int): Either[Fault, Array[Long]] = values.get(id) match {
    case Some(VMValue.Bits(_, lanes)) => Right(lanes)
    case Some(_)                      => Left(Fault.InvalidRequest(s"SPIR-V value %$id is not numeric"))
    case None                         => Left(undefined(id))
  }

  def address(values: InvocationValues, id: Int): Either[Fault, Pointer] = values.get(id) match {
    case Some(VMValue.Address(_, pointer)) => Right(pointer)
    case Some(_)                           => Left(Fault.InvalidRequest(s"SPIR-V value %$id is not a pointer"))
    case None                              => Left(undefined(id))
  }

  def retag(value: VMValue, typeId: Int): VMValue = value match {
    case VMValue.Bits(_, lanes)      => VMValue.Bits(typeId, lanes)
    case VMValue.Address(_, pointer) => VMValue.Address(typeId, pointer)
  }

  /** Copies value `from` into `to` under the static type of `to`. */
  def transfer(values: InvocationValues, from: Int, to: Int, typeId: Int): Either[Fault, Unit] =
    if (values.program.scalarSlot(from) && values.program.scalarSlot(to)) {
      values.scalars(to) = values.scalars(from)
      Right(())
    } else values.get(from) match {
      case Some(value) => Right(values.update(to, retag(value, typeId)))
      case None        => Left(undefined(from))
    }
}

/** One lowered instruction. Operand ids, shapes and widths are resolved when the program loads. */
private abstract class Op {
  def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit]
}
