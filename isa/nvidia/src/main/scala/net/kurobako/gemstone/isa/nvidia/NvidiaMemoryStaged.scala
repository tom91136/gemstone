package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import VMIR.Matrix.*
import net.kurobako.gemstone.semantics.*

/** The staged semantics of loads, stores, atomics and tensor instructions. */
private[isa] object NvidiaMemoryStaged {

  inline def tensor[V, R](inline algebra: Algebra[V], instruction: Instruction)(inline sinks: NvidiaSinks[V, R])(
      otherwise: => R
  ): R = {
    def warpgroup(operation: Matrix.Operation.WarpgroupMultiply): R =
      WarpgroupSemantics.multiply(algebra, operation)(sinks)(otherwise)

    instruction match {
      case Instruction.Tensor(operation: Matrix.Operation.Multiply) => multiply(algebra, operation)(sinks)(otherwise)
      case Instruction.Tensor(operation: Matrix.Operation.WarpgroupMultiply) => warpgroup(operation)
      case Instruction.TensorMemoryMultiply(multiply)                        =>
        sinks.multiplyTensorMemory(multiply)(TensorMemorySemantics.element(algebra, _, _))
      case _ => otherwise
    }
  }

  /** An `mma` product by how it accumulates, one element of D at a time; other accumulations take `otherwise`. */
  inline def multiply[V, R](inline algebra: Algebra[V], operation: Operation.Multiply)(
      inline sinks: NvidiaSinks[V, R]
  )(otherwise: => R): R = operation.accumulation match {
    case Accumulation.Exact(chunk) if operation.d.element == Element.S32 =>
      sinks.multiplyMatrices(operation)(operands =>
        TensorSemantics.integer(algebra, operation, chunk, operands, operands.c, negateA = false, negateB = false)
      )
    case Accumulation.Fused(rounding) if operation.d.element == Element.F64 =>
      sinks.multiplyMatrices(operation)(TensorSemantics.fused(algebra, operation, rounding, _))
    case blocked: Accumulation.Blocked if operation.d.element == Element.F32 || operation.d.element == Element.F16 =>
      val blocks = TensorSemantics.BlockedProduct(operation, blocked)
      sinks.multiplyMatrices(operation)(operands =>
        TensorSemantics.blocked(algebra, blocks, operands, operands.c, negateA = false, negateB = false)
      )
    case Accumulation.Emulated => sinks.multiplyMatrices(operation)(TensorSemantics.emulated(algebra, operation, _))
    case _                     => otherwise
  }

  inline def memory[V, R](inline algebra: Algebra[V], instruction: Instruction)(inline sinks: NvidiaSinks[V, R])(
      inline next: Instruction => R
  ): R = instruction match {
    case Instruction.Load(destination, address, width, signed, access) =>
      MemorySemantics.load(algebra, width, signed)(sinks, destination, address, access)
    case Instruction.LoadVector(destinations, address, width, signed, access) =>
      MemorySemantics.loadVector(algebra, width, signed)(sinks, destinations, address, access)
    case Instruction.LoadParameter(destinations, parameter, offset, width, signed) =>
      MemorySemantics.loadParameter(algebra, width, signed)(sinks, destinations, parameter, offset)
    case Instruction.Store(address, source, width, access) =>
      MemorySemantics.store(algebra, width)(sinks, address, source, access)
    case Instruction.StoreVector(address, sources, width, access) =>
      MemorySemantics.storeVector(algebra, width)(sinks, address, sources, access)
    case Instruction.StoreParameter(parameter, offset, source, width) =>
      MemorySemantics.storeParameter(algebra, width)(sinks, parameter, offset, source)
    case Instruction.Atomic(destination, address, source, width, space, operation) =>
      MemorySemantics.atomic(algebra, operation, width)(sinks, destination, address, source, space)
    case Instruction.AtomicCompareExchange(destination, address, comparator, source, width, space) =>
      MemorySemantics.compareExchange(algebra, width)(
        sinks = sinks,
        destination = destination,
        address = address,
        comparator = comparator,
        source = source,
        space = space
      )
    case _ => next(instruction)
  }
}
