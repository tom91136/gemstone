package net.kurobako.gemstone.isa.spirv

import Operand.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.render.*

/** SPV_ARM_tensors element access and size queries, as requests to a [[SPIRVMachine]]. A tensor's descriptor is a
  * little-endian block of its base address, then its shapes, then its strides, one 8-byte word each. An element outside
  * the shape reads as the out-of-bounds value, or zero, and an element outside it is not written.
  */
private object TensorArmSemantics {

  private val OutOfBoundsValue = 0x2
  private val Unsupported      = 0x4 | 0x8

  def handles(instruction: Instruction): Boolean = instruction match {
    case Instruction.TensorQuerySizeARM(_, _, _, _) | Instruction.TensorReadARM(_, _, _, _, _) |
        Instruction.TensorWriteARM(_, _, _, _) => true
    case _ => false
  }

  /** The mask word of the tensor operands, then the words that follow it. */
  def operandWords(operands: Option[Operand]): Vec[Int] =
    operands.fold(Vec.empty[Int])(operand => Vec.from(operand.words.map(_.raw)))

  /** Whether the tensor operands avoid the memory-scope availability and visibility the VM does not model. */
  def supported(words: Vec[Int]): Boolean = words.headOption.forall(mask => (mask & Unsupported) == 0)

  /** The id of the out-of-bounds value a read returns, which follows the mask word when the mask names it. */
  def outOfBounds(words: Vec[Int]): Option[Int] =
    if (words.headOption.exists(mask => (mask & OutOfBoundsValue) != 0)) words.lift(1) else None

  /** Partial results 0 to `2 * rank` hold the descriptor's base address, its `rank` shapes, then its `rank` strides. */
  inline def descriptor[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      tensor: Int,
      rank: Int
  )(inline next: => F[Unit]): F[Unit] =
    machine.repeat(algebra.literal(2L * rank + 1))(word =>
      machine.loadElement(tensor, algebra.multiply(word, algebra.literal(8L)), algebra.literal(8L))(value =>
        machine.setPartial(word, value)(machine.done)
      )
    )(next)

  /** Coordinate `at` of the `lane`th element of a read or write, which runs along the innermost dimension. */
  inline def coordinate[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      coordinates: Int,
      rank: Int,
      lane: V,
      at: Int
  ): V =
    if (at == rank - 1) algebra.add(machine.leaf(coordinates, at), lane) else machine.leaf(coordinates, at)

  /** Whether the `lane`th element lies within the shape; a negative coordinate compares as a large unsigned one. */
  inline def inside[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      coordinates: Int,
      rank: Int,
      lane: V
  ): V = (0 until rank).foldLeft(algebra.literal(1L)) { (found, at) =>
    algebra.and(
      found,
      algebra.lessThan(
        coordinate(algebra, machine)(coordinates, rank, lane, at),
        machine.partial(algebra.literal(1L + at)),
        signed = false
      )
    )
  }

  /** The byte offset of the `lane`th element from the base address, by the strides of the descriptor. */
  inline def offset[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      coordinates: Int,
      rank: Int,
      lane: V
  ): V = (0 until rank).foldLeft(algebra.literal(0L)) { (sum, at) =>
    algebra.add(
      sum,
      algebra.multiply(
        coordinate(algebra, machine)(coordinates, rank, lane, at),
        machine.partial(algebra.literal(1L + rank + at))
      )
    )
  }

  /** Leaf `i` of the `count` of `target`, typed `typeId`, is the `bytes`-byte element at the coordinates of the `i`th
    * lane, or `fill` where the element lies outside the tensor, then `next`.
    */
  inline def read[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      tensor: Int,
      coordinates: Int,
      rank: Int,
      count: Int,
      element: Int,
      bytes: Int,
      fill: Option[Int],
      target: Int,
      typeId: Int
  )(inline next: => F[Unit]): F[Unit] = {
    val first = 2 * rank + 1
    val zero  = fill.fold(algebra.literal(0L))(machine.leaf(_, 0))
    machine.partials(first + count)(
      descriptor(algebra, machine)(tensor, rank)(
        machine.repeat(algebra.literal(count.toLong))(lane =>
          machine.setPartial(algebra.add(lane, algebra.literal(first.toLong)), zero)(
            machine.when(inside(algebra, machine)(coordinates, rank, lane))(
              machine.loadAbsolute(
                machine.partial(algebra.literal(0L)),
                offset(algebra, machine)(coordinates, rank, lane),
                algebra.literal(bytes.toLong),
                element
              )(loaded =>
                machine.setPartial(algebra.add(lane, algebra.literal(first.toLong)), loaded)(machine.done)
              )
            )(machine.done)
          )
        )(
          machine.eachLeaf(Array.empty[Int], count, target, typeId)(index =>
            machine.partial(algebra.literal((first + index).toLong))
          )(next)
        )
      )
    )
  }

  /** Each leaf of `source` is stored as the `bytes`-byte element at the coordinates of its lane, where that element
    * lies within the tensor, then `next`.
    */
  inline def write[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      tensor: Int,
      coordinates: Int,
      source: Int,
      rank: Int,
      count: Int,
      element: Int,
      bytes: Int
  )(inline next: => F[Unit]): F[Unit] =
    machine.partials(2 * rank + 1)(
      descriptor(algebra, machine)(tensor, rank)(
        machine.repeat(algebra.literal(count.toLong))(lane =>
          machine.when(inside(algebra, machine)(coordinates, rank, lane))(
            machine.storeAbsolute(
              machine.partial(algebra.literal(0L)),
              offset(algebra, machine)(coordinates, rank, lane),
              algebra.literal(bytes.toLong),
              machine.leafAt(Array(source), 1, lane),
              element
            )(machine.done)
          )(machine.done)
        )(next)
      )
    )

  /** `target`, typed `typeId`, is the extent of the tensor along `dimension`, then `next`. */
  inline def size[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      tensor: Int,
      dimension: V,
      target: Int,
      typeId: Int
  )(inline next: => F[Unit]): F[Unit] =
    machine.loadElement(
      tensor,
      algebra.add(algebra.literal(8L), algebra.multiply(dimension, algebra.literal(8L))),
      algebra.literal(8L)
    )(extent => machine.eachLeaf(Array.empty[Int], 1, target, typeId)(_ => extent)(next))

  /** The forms the explorer shows: the element reads and writes of a tensor of `rank` dimensions, of `bytes`-byte
    * elements.
    */
  def render(instruction: Instruction, machine: SPIRVMachineRender, rank: Int, bytes: Int): Option[Term.Steps] =
    instruction match {
      case Instruction.TensorQuerySizeARM(resultType, dest, tensor, dimension) =>
        Some(size(
          Render,
          machine
        )(id(tensor), machine.leaf(id(dimension), 0), id(dest), id(resultType))(machine.advance))
      case Instruction.TensorReadARM(resultType, dest, tensor, coordinates, operands)
          if supported(operandWords(operands)) =>
        Some(read(Render, machine)(
          tensor = id(tensor),
          coordinates = id(coordinates),
          rank = rank,
          count = machine.leafCount(id(dest)),
          element = id(resultType),
          bytes = bytes,
          fill = outOfBounds(operandWords(operands)),
          target = id(dest),
          typeId = id(resultType)
        )(machine.advance))
      case Instruction.TensorWriteARM(tensor, coordinates, source, operands) if supported(operandWords(operands)) =>
        Some(write(Render, machine)(
          tensor = id(tensor),
          coordinates = id(coordinates),
          source = id(source),
          rank = rank,
          count = machine.leafCount(id(source)),
          element = id(source),
          bytes = bytes
        )(machine.advance))
      case _ => None
    }
}
