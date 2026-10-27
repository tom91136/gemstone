package net.kurobako.gemstone.isa.spirv

import VMInvocation.*
import VMMemory.*
import net.kurobako.gemstone.vm.*

private object Op {
  type Flow[A] = AddressSpace.ReadResult[A]

  val Done: AddressSpace.ReadResult[Unit] = Right(())

  /** An instruction whose operands failed load-time checks faults only when it executes. */
  class Fail(fault: Fault) extends Op {
    private val result                                                                      = Left(fault)
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = result
    override def failure: Option[Fault]                                                     = Some(fault)
  }

  /** An op whose run is a closure over its lowered operands; used where a dedicated class adds nothing. */
  class Dynamic(body: (InvocationState, MemoryState) => AddressSpace.ReadResult[Unit]) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = body(execution, memory)
  }

  /** A step that writes its result and falls through to the next instruction. */
  def step(body: (InvocationState, MemoryState) => AddressSpace.ReadResult[Unit]): Op =
    Dynamic { (execution, memory) =>
      body(execution, memory) match {
        case Right(_) =>
          execution.pc += 1
          Done
        case failed => failed
      }
    }

  /** A hint that reads nothing: it faults only when its pointer operand is not an address. */
  def prefetch(pointer: Int): Op = Prefetch(pointer)

  private class Prefetch(pointer: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      MemoryAccessSemantics.prefetch(InvocationMachine(execution))(pointer)
  }

  /** Suspends the invocation at a collective rendezvous built from its current position. */
  inline def suspend(
      execution: InvocationState,
      inline request: (Int, Int) => VMCollectives.Request
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

  /** `count` consecutive instructions without an execution effect. */
  class Skip(count: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = {
      execution.pc += count
      Done
    }
  }

  def undefined(id: Int): Fault = Fault.InvalidRequest(s"SPIR-V value %$id is undefined")

  /** Whether `count` elements of `size` bytes past `base` leave the 64-bit address space; `count` is unsigned, as the
    * size_t offsets of OpenCL.std are.
    */
  def displacementOverflows(base: Long, count: Long, size: Long): Boolean =
    count < 0L || Math.multiplyHigh(count, size) != 0L || java.lang.Long.compareUnsigned(base + count * size, base) < 0

  def displacementFault(base: Long, count: Long, size: Long): Fault = Fault.Memory(
    s"SPIR-V address 0x${java.lang.Long.toHexString(base)} + ${java.lang.Long.toUnsignedString(count)} * $size bytes " +
      "leaves the address space"
  )

  /** The op a lowering produced, or one that faults with its load-time failure. */
  def lowered(op: Either[Fault, Op]): Op = op.fold(Fail(_), identity)

  private val Checked: Either[Fault, Unit] = Right(())

  /** Checks the memory scope and semantics operands of an atomic or barrier. */
  def memoryControls(values: InvocationValues, scope: Int, semantics: Int, opcode: String): Either[Fault, Unit] =
    scalar(values, scope) match {
      case Left(error)        => Left(error)
      case Right(memoryScope) =>
        if (memoryScope < 0L || memoryScope > 6L)
          Left(Fault.InvalidRequest(s"$opcode has invalid memory scope $memoryScope"))
        else scalar(values, semantics) match {
          case Left(error)            => Left(error)
          case Right(memorySemantics) =>
            if ((memorySemantics & ~0xffdeL) == 0L) Checked
            else
              Left(
                Fault.InvalidRequest(
                  s"$opcode has unknown memory-semantics bits 0x${java.lang.Long.toHexString(memorySemantics)}"
                )
              )
        }
    }

  def isWorkgroupOrSubgroup(scope: Long): Boolean = scope == Scope.Workgroup || scope == Scope.Subgroup

  def scopeFault(opcode: String, scope: Long): Fault =
    Fault.Unsupported(s"$opcode execution scope $scope is not Subgroup or Workgroup")

  def scalar(values: InvocationValues, id: Int): Either[Fault, Long] =
    if (values.program.scalarSlot(id)) Right(values.scalars(id))
    else values.get(id) match {
      case Some(VMValue.Bits(_, Array(value))) => Right(value)
      case Some(VMValue.Bits(_, lanes)) => Left(Fault.InvalidRequest(s"SPIR-V value %$id has ${lanes.length} lanes"))
      case Some(_)                      => Left(Fault.InvalidRequest(s"SPIR-V value %$id is not numeric"))
      case None                         => Left(undefined(id))
    }

  def conditionalOperand(values: InvocationValues, pairs: Array[Int]): Either[Fault, Int] =
    (0 until pairs.length by 2).view
      .map(index => scalar(values, pairs(index)).map(value => (value != 0L, pairs(index + 1))))
      .collectFirst[Either[Fault, Int]] {
        case Left(error)            => Left(error)
        case Right((true, operand)) => Right(operand)
      }
      .getOrElse(Left(Fault.InvalidRequest("OpConditionalCopyObjectINTEL has no operand whose condition holds")))

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

  /** The fault lowering found, where the op only raises it. */
  def failure: Option[Fault] = None
}
