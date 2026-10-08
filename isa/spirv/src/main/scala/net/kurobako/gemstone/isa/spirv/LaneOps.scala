package net.kurobako.gemstone.isa.spirv

import Op.*
import VMInvocation.*
import VMLowering.*
import VMMemory.*
import net.kurobako.gemstone.vm.*

/** Lane kernels and the generic ops that apply them to scalar registers or vector lanes. A lowering chooses the scalar
  * form when every operand and the result live in the scalar bank.
  */
private object LaneOps {
  import Operand.*

  trait Kernel1 { def apply(a: Long): Long                   }
  trait Kernel2 { def apply(a: Long, b: Long): Long          }
  trait Kernel3 { def apply(a: Long, b: Long, c: Long): Long }

  class Scalar1(dest: Int, a: Int, kernel: Kernel1) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = {
      val registers = execution.values.scalars
      registers(dest) = kernel(registers(a))
      execution.pc += 1
      Done
    }
  }

  class Scalar2(dest: Int, a: Int, b: Int, kernel: Kernel2) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = {
      val registers = execution.values.scalars
      registers(dest) = kernel(registers(a), registers(b))
      execution.pc += 1
      Done
    }
  }

  class Scalar3(dest: Int, a: Int, b: Int, c: Int, kernel: Kernel3) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = {
      val registers = execution.values.scalars
      registers(dest) = kernel(registers(a), registers(b), registers(c))
      execution.pc += 1
      Done
    }
  }

  // Operand lane counts may differ only where the instruction broadcasts; the shorter operand repeats.
  class Lanes1(dest: Int, resultType: Int, a: Int, kernel: Kernel1) extends Op {
    @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = {
      val values = execution.values
      val left   = values.lanes(a)
      if (left == null) Left(undefined(a))
      else {
        val result = Array.ofDim[Long](left.length)
        var lane   = 0
        while (lane < result.length) {
          result(lane) = kernel(left(lane))
          lane += 1
        }
        values.write(dest, resultType, result)
        execution.pc += 1
        Done
      }
    }
  }

  class Lanes2(dest: Int, resultType: Int, a: Int, b: Int, kernel: Kernel2) extends Op {
    @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = {
      val values = execution.values
      val left   = values.lanes(a)
      val right  = values.lanes(b)
      if (left == null) Left(undefined(a))
      else if (right == null) Left(undefined(b))
      else {
        val result = Array.ofDim[Long](math.max(left.length, right.length))
        var lane   = 0
        // Equal lengths skip the broadcast division in every lane.
        if (left.length == right.length)
          while (lane < result.length) {
            result(lane) = kernel(left(lane), right(lane))
            lane += 1
          }
        else
          while (lane < result.length) {
            result(lane) = kernel(left(lane % left.length), right(lane % right.length))
            lane += 1
          }
        values.write(dest, resultType, result)
        execution.pc += 1
        Done
      }
    }
  }

  class Lanes3(dest: Int, resultType: Int, a: Int, b: Int, c: Int, kernel: Kernel3) extends Op {
    @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = {
      val values = execution.values
      val first  = values.lanes(a)
      val second = values.lanes(b)
      val third  = values.lanes(c)
      if (first == null) Left(undefined(a))
      else if (second == null) Left(undefined(b))
      else if (third == null) Left(undefined(c))
      else {
        val result = Array.ofDim[Long](math.max(first.length, math.max(second.length, third.length)))
        var lane   = 0
        if (first.length == result.length && second.length == result.length && third.length == result.length)
          while (lane < result.length) {
            result(lane) = kernel(first(lane), second(lane), third(lane))
            lane += 1
          }
        else
          while (lane < result.length) {
            result(lane) = kernel(first(lane % first.length), second(lane % second.length), third(lane % third.length))
            lane += 1
          }
        values.write(dest, resultType, result)
        execution.pc += 1
        Done
      }
    }
  }

  def unary(site: Site, resultType: Int, dest: Int, a: Int)(kernel: Kernel1): Op =
    if (site.program.scalarSlot(dest) && site.program.scalarSlot(a)) Scalar1(dest, a, kernel)
    else Lanes1(dest, resultType, a, kernel)

  def binary(site: Site, resultType: Int, dest: Int, a: Int, b: Int)(kernel: Kernel2): Op =
    if (site.program.scalarSlot(dest) && site.program.scalarSlot(a) && site.program.scalarSlot(b))
      Scalar2(dest, a, b, kernel)
    else Lanes2(dest, resultType, a, b, kernel)

  def ternary(site: Site, resultType: Int, dest: Int, a: Int, b: Int, c: Int)(kernel: Kernel3): Op =
    if (
      site.program.scalarSlot(dest) && site.program.scalarSlot(a) && site.program.scalarSlot(b) &&
      site.program.scalarSlot(c)
    )
      Scalar3(dest, a, b, c, kernel)
    else Lanes3(dest, resultType, a, b, c, kernel)

  /** The static kind of an operand value. */
  enum Typed {
    case Undefined(id: Int)
    case Opaque(id: Int, typeId: Int)
    case Data(id: Int, typeId: Int)

    def id: Int
  }

  def typed(site: Site, value: Operand): Typed = typed(site, id(value))

  def typed(site: Site, key: Int): Typed = {
    val typeId = site.program.valueType(key)
    if (typeId < 0) Typed.Undefined(key)
    else site.program.runtimeType(typeId) match {
      case Some(VMProgram.RuntimeType.Bool | _: VMProgram.RuntimeType.Integer | _: VMProgram.RuntimeType.Floating |
          _: VMProgram.RuntimeType.VectorType | _: VMProgram.RuntimeType.MatrixType | _: VMProgram.RuntimeType.Array |
          _: VMProgram.RuntimeType.Structure | VMProgram.RuntimeType.Event) => Typed.Data(key, typeId)
      case _ => Typed.Opaque(key, typeId)
    }
  }
}
