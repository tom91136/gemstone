package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.render.*

/** GLSL.std.450 and OpenCL.std functions over matrices, vectors and the printf buffer, as requests to a
  * [[SPIRVMachine]].
  */
private object ExtendedSemantics {

  inline def printf[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      format: Int,
      arguments: Array[Int],
      target: Int,
      typeId: Int
  ): F[Unit] = machine.printf(format, arguments, target, typeId)(machine.advance)

  /** Lane `i` of the result, of `count`, is the lane that the unsigned remainder of mask lane `i` by `total` picks of
    * the lanes of the first `operands.length - 1` operands, which hold `total` between them; the last holds the masks.
    */
  inline def shuffle[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      operands: Array[Int],
      total: Int,
      count: Int,
      target: Int,
      typeId: Int
  ): F[Unit] = machine.eachLeaf(operands, count, target, typeId) { lane =>
    algebra.let(
      "selected",
      algebra.remainder(
        machine.leaf(operands(operands.length - 1), lane),
        algebra.literal(total.toLong),
        signed = false
      )
    )(selected => machine.leafAt(operands, operands.length - 1, selected))
  }(machine.advance)

  inline def vectorLoad[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      offset: Int,
      pointer: Int,
      count: Int,
      size: Int in 1 ~ 8,
      target: Int,
      typeId: Int
  ): F[Unit] = machine.loadVector(offset, pointer, count, size, target, typeId)(machine.advance)

  inline def vectorStore[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      data: Int,
      offset: Int,
      pointer: Int,
      count: Int,
      size: Int in 1 ~ 8
  ): F[Unit] = machine.storeVector(data, offset, pointer, count, size)(machine.advance)

  /** The extended instruction `mnemonic` over the ids `ids`, whose elements are `bytes` bytes wide and whose vectors or
    * square matrices have `rows` rows, or none for an instruction they do not cover. A store has no result, so its ids
    * are its operands.
    */
  def render(
      mnemonic: String,
      ids: Vec[Int],
      bytes: Int,
      rows: Int,
      machine: SPIRVMachineRender
  ): Option[Term.Steps] = mnemonic match {
    case "OpenCL.std vstoren" =>
      Some(vectorStore(machine)(ids(0), ids(1), ids(2), rows, bytes.unsafe[Int in 1 ~ 8]))
    case _ =>
      val (resultType, dest, sources) = (ids(0), ids(1), ids.drop(2).toArray)
      val width                       = (bytes * 8).unsafe[Int in (16, 32, 64)]
      mnemonic match {
        case "GLSL.std.450 Determinant" =>
          Some(MatrixSemantics.renderDeterminant(machine)(sources(0), rows, width, dest, resultType))
        case "GLSL.std.450 MatrixInverse" =>
          Some(MatrixSemantics.renderInverse(machine)(sources(0), rows, width, dest, resultType))
        case "OpenCL.std printf" => Some(printf(machine)(sources(0), sources.tail, dest, resultType))
        case "OpenCL.std shuffle" | "OpenCL.std shuffle2" =>
          Some(shuffle(Render, machine)(
            sources,
            total = sources.init.map(machine.leafCount).sum,
            count = machine.leafCount(dest),
            target = dest,
            typeId = resultType
          ))
        case "OpenCL.std vloadn" =>
          Some(vectorLoad(machine)(sources(0), sources(1), rows, bytes.unsafe[Int in 1 ~ 8], dest, resultType))
        case _ => None
      }
  }
}
