package net.kurobako.gemstone.isa.spirv

import Operand.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.render.*

/** SPIR-V matrix semantics, as requests to a [[SPIRVMachine]]. A matrix is its lanes in column order. */
private object MatrixSemantics {

  inline def transpose[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      source: Int,
      rows: Int,
      columns: Int,
      target: Int,
      typeId: Int
  ): F[Unit] = machine.transpose(source, rows, columns, target, typeId)(machine.advance)

  private inline def add[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(left: V, right: V): V =
    machine.compute(Matrices.Add, left, right)

  private inline def subtract[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(left: V, right: V): V =
    machine.compute(Matrices.Subtract, left, right)

  private inline def multiply[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(left: V, right: V): V =
    machine.compute(Matrices.Multiply, left, right)

  private inline def determinant2[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(a: V, b: V, c: V, d: V): V =
    subtract(machine)(multiply(machine)(a, d), multiply(machine)(b, c))

  private inline def determinant3[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      a: V,
      b: V,
      c: V,
      d: V,
      e: V,
      f: V,
      g: V,
      h: V,
      i: V
  ): V = {
    val first  = multiply(machine)(a, determinant2(machine)(e, f, h, i))
    val second = subtract(machine)(first, multiply(machine)(b, determinant2(machine)(d, f, g, i)))
    add(machine)(second, multiply(machine)(c, determinant2(machine)(d, e, g, h)))
  }

  private inline def determinant4[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      a: V,
      b: V,
      c: V,
      d: V,
      e: V,
      f: V,
      g: V,
      h: V,
      i: V,
      j: V,
      k: V,
      l: V,
      m: V,
      n: V,
      o: V,
      p: V
  ): V = {
    val first  = multiply(machine)(a, determinant3(machine)(f, g, h, j, k, l, n, o, p))
    val second = subtract(machine)(first, multiply(machine)(b, determinant3(machine)(e, g, h, i, k, l, m, o, p)))
    val third  = add(machine)(second, multiply(machine)(c, determinant3(machine)(e, f, h, i, j, l, m, n, p)))
    subtract(machine)(third, multiply(machine)(d, determinant3(machine)(e, f, g, i, j, k, m, n, o)))
  }

  private inline def element[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      source: Int,
      rows: Int,
      width: Int in (16, 32, 64),
      r: Int,
      c: Int
  ): V = machine.compute(Matrices.Widen, machine.leaf(source, c * rows + r), algebra.literal(width.toLong))

  private inline def matrix2[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(inline m: (Int, Int) => V): V = {
    val (a, b, c, d) = (m(0, 0), m(0, 1), m(1, 0), m(1, 1))
    determinant2(machine)(a, b, c, d)
  }

  private inline def matrix3[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(inline m: (Int, Int) => V): V = {
    val (a, b, c) = (m(0, 0), m(0, 1), m(0, 2))
    val (d, e, f) = (m(1, 0), m(1, 1), m(1, 2))
    val (g, h, i) = (m(2, 0), m(2, 1), m(2, 2))
    determinant3(machine)(a, b, c, d, e, f, g, h, i)
  }

  private inline def matrix4[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(inline m: (Int, Int) => V): V = {
    val (a, b, c, d) = (m(0, 0), m(0, 1), m(0, 2), m(0, 3))
    val (e, f, g, h) = (m(1, 0), m(1, 1), m(1, 2), m(1, 3))
    val (i, j, k, l) = (m(2, 0), m(2, 1), m(2, 2), m(2, 3))
    val (n, o, p, q) = (m(3, 0), m(3, 1), m(3, 2), m(3, 3))
    determinant4(machine)(a, b, c, d, e, f, g, h, i, j, k, l, n, o, p, q)
  }

  private inline def determinantOf[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      source: Int,
      inline rows: Int,
      width: Int in (16, 32, 64)
  ): V = inline rows match {
    case 2 => matrix2(machine)((r, c) => element(algebra, machine)(source, rows, width, r, c))
    case 3 => matrix3(machine)((r, c) => element(algebra, machine)(source, rows, width, r, c))
    case 4 => matrix4(machine)((r, c) => element(algebra, machine)(source, rows, width, r, c))
  }

  /** The determinant of the square matrix `source` holds, of the literal `rows`, computed in double precision by
    * expansion along the first row, then narrowed to `width` bits. Each size is a body of its own, so that none grows
    * too large to compile.
    */
  inline def determinant[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      source: Int,
      inline rows: Int,
      width: Int in (16, 32, 64),
      target: Int,
      typeId: Int
  ): F[Unit] = machine.eachLeaf(Array(source), 1, target, typeId)(_ =>
    machine.compute(
      Matrices.Narrow,
      determinantOf(algebra, machine)(source, rows, width),
      algebra.literal(width.toLong)
    )
  )(machine.advance)

  private inline def minor[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      source: Int,
      rows: Int,
      width: Int in (16, 32, 64),
      r: Int,
      c: Int,
      i: Int,
      j: Int
  ): V = element(algebra, machine)(source, rows, width, if (i < c) i else i + 1, if (j < r) j else j + 1)

  private inline def cofactor[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      source: Int,
      inline rows: Int,
      width: Int in (16, 32, 64),
      r: Int,
      c: Int
  ): V = inline rows match {
    case 2 => minor(algebra, machine)(source, rows, width, r, c, 0, 0)
    case 3 => matrix2(machine)((i, j) => minor(algebra, machine)(source, rows, width, r, c, i, j))
    case 4 => matrix3(machine)((i, j) => minor(algebra, machine)(source, rows, width, r, c, i, j))
  }

  /** Lane `r + c * rows` of the inverse, of the literal `rows`, is the cofactor of element `(c, r)` over the
    * determinant, which expansion along the first row gives.
    */
  inline def inverse[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      source: Int,
      inline rows: Int,
      width: Int in (16, 32, 64),
      target: Int,
      typeId: Int
  ): F[Unit] = {
    val denominator = determinantOf(algebra, machine)(source, rows, width)
    machine.eachLeaf(Array(source), rows * rows, target, typeId) { lane =>
      val r      = lane % rows
      val c      = lane / rows
      val found  = cofactor(algebra, machine)(source, rows, width, r, c)
      val signed = if ((r + c) % 2 == 0) found else machine.compute(Matrices.Negate, found)
      machine.compute(
        Matrices.Narrow,
        machine.compute(Matrices.Divide, signed, denominator),
        algebra.literal(width.toLong)
      )
    }(machine.advance)
  }

  private def renderDeterminantTwo(machine: SPIRVMachineRender)(
      source: Int,
      width: Int in (16, 32, 64),
      target: Int,
      typeId: Int
  ): Term.Steps = determinant(Render, machine)(source, 2, width, target, typeId)

  private def renderInverseTwo(machine: SPIRVMachineRender)(
      source: Int,
      width: Int in (16, 32, 64),
      target: Int,
      typeId: Int
  ): Term.Steps = inverse(Render, machine)(source, 2, width, target, typeId)

  private def renderDeterminantThree(machine: SPIRVMachineRender)(
      source: Int,
      width: Int in (16, 32, 64),
      target: Int,
      typeId: Int
  ): Term.Steps = determinant(Render, machine)(source, 3, width, target, typeId)

  private def renderInverseThree(machine: SPIRVMachineRender)(
      source: Int,
      width: Int in (16, 32, 64),
      target: Int,
      typeId: Int
  ): Term.Steps = inverse(Render, machine)(source, 3, width, target, typeId)

  private def renderDeterminantFour(machine: SPIRVMachineRender)(
      source: Int,
      width: Int in (16, 32, 64),
      target: Int,
      typeId: Int
  ): Term.Steps = determinant(Render, machine)(source, 4, width, target, typeId)

  private def renderInverseFour(machine: SPIRVMachineRender)(
      source: Int,
      width: Int in (16, 32, 64),
      target: Int,
      typeId: Int
  ): Term.Steps = inverse(Render, machine)(source, 4, width, target, typeId)

  /** The determinant of the `rows` by `rows` matrix `source` holds, for the explorer. */
  def renderDeterminant(machine: SPIRVMachineRender)(
      source: Int,
      rows: Int,
      width: Int in (16, 32, 64),
      target: Int,
      typeId: Int
  ): Term.Steps = rows match {
    case 2 => renderDeterminantTwo(machine)(source, width, target, typeId)
    case 3 => renderDeterminantThree(machine)(source, width, target, typeId)
    case _ => renderDeterminantFour(machine)(source, width, target, typeId)
  }

  /** The inverse of the `rows` by `rows` matrix `source` holds, for the explorer. */
  def renderInverse(machine: SPIRVMachineRender)(
      source: Int,
      rows: Int,
      width: Int in (16, 32, 64),
      target: Int,
      typeId: Int
  ): Term.Steps = rows match {
    case 2 => renderInverseTwo(machine)(source, width, target, typeId)
    case 3 => renderInverseThree(machine)(source, width, target, typeId)
    case _ => renderInverseFour(machine)(source, width, target, typeId)
  }

  /** The matrix semantics of `instruction` rendered over `machine`, for a matrix of `rows` by `columns`, or none for an
    * instruction they do not cover.
    */
  def render(instruction: Instruction, machine: SPIRVMachineRender, rows: Int, columns: Int): Option[Term.Steps] =
    instruction match {
      case Instruction.Transpose(resultType, dest, matrix) =>
        Some(transpose(machine)(id(matrix), rows, columns, id(dest), id(resultType)))
      case _ => None
    }
}
