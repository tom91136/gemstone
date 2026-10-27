package net.kurobako.gemstone.isa.spirv

import FragmentSemantics.*
import Op.*
import Operand.*
import VMInvocation.*
import VMMemory.*
import VMProgram.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.vm.*

/** The lowering of cooperative vector operations to ops that request them of the machine. */
private object CooperativeVectorLowering {

  def lower(site: Site): PartialFunction[Instruction, Op] = {
    case instruction if CooperativeSemantics.productOf(instruction).isDefined =>
      CooperativeSemantics.productOf(instruction).fold(Fail(Fault.Unsupported("not a product")))(
        (operands, target, typeId) => product(site.program, operands, target, typeId)
      )
    case instruction if CooperativeSemantics.operatesOnVector(instruction) =>
      lowered(operandTypes(site.program, instruction).map(_ => VectorOp(instruction)))
  }

  private def component(runtime: RuntimeType): Component = runtime match {
    case RuntimeType.Floating(width)   => Component.Floating(width)
    case RuntimeType.Integer(width, _) => Component.Integer(width.widen[Int in 1 ~ 64])
    case _                             => Component.Other
  }

  private def product(program: VMProgram, operands: Product, target: Int, typeId: Int): Op =
    lowered(CooperativeVectorOps.products(program, typeId, operands.input).map { (result, rows, source, inputCount) =>
      ProductOp(operands, Shape(rows.raw, inputCount.raw, component(result), component(source)), target, typeId)
    })

  private def operandTypes(program: VMProgram, instruction: Instruction): Either[Fault, Unit] = instruction match {
    case Instruction.CooperativeVectorLoadNV(resultType = t) =>
      CooperativeVectorOps.vector(program, id(t), "OpCooperativeVectorLoadNV").map(_ => ())
    case Instruction.CooperativeVectorStoreNV(src = source) =>
      CooperativeVectorOps.vector(program, program.valueType(id(source)), "OpCooperativeVectorStoreNV").map(_ => ())
    case Instruction.CooperativeVectorOuterProductAccumulateNV(a = a, b = b) =>
      CooperativeVectorOps.outers(program, id(a), id(b)).map(_ => ())
    case Instruction.CooperativeVectorReduceSumAccumulateNV(_, _, source) =>
      CooperativeVectorOps.vector(program, program.valueType(id(source)), "OpCooperativeVectorReduceSumAccumulateNV")
        .map(_ => ())
    case _ => Right(())
  }

  private class VectorOp(instruction: Instruction) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      CooperativeSemantics.vector(InvocationMachine(execution))(instruction)
  }

  private class ProductOp(operands: Product, shape: Shape, target: Int, typeId: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      FragmentSemantics.product(Execute, InvocationMachine(execution))(operands, shape, target, typeId)
  }
}
