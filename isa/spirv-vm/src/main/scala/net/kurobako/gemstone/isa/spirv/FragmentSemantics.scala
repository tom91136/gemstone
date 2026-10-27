package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*
import net.kurobako.gemstone.vm.*

/** SPIR-V cooperative matrix accesses and cooperative vector products, as requests to a [[SPIRVMachine]]. A matrix is
  * its components in row order, each placed in memory by its layout and stride; a product accumulates in partial
  * results, one for each row of its matrix, which follow the converted elements of its input.
  */
private object FragmentSemantics {

  /** The scalar type of the components of a vector. */
  enum Component derives CanEqual {
    case Floating(width: Int in (16, 32, 64))
    case Integer(width: Int in 1 ~ 64)
    case Other
  }

  /** The operands of a product; `bias` names the bias pointer, its offset and its kind. */
  case class Product(
      input: Int,
      inputKind: Int,
      matrix: Int,
      matrixOffset: Int,
      matrixKind: Int,
      bias: Option[(Int, Int, Int)],
      m: Int,
      k: Int,
      layout: Int,
      transpose: Int,
      stride: Option[Int],
      signedInput: Boolean
  )

  /** The `rows` lanes of the result and the `inputCount` of the input, with their components. */
  case class Shape(rows: Int, inputCount: Int, result: Component, source: Component)

  private inline def offsetOf[V](inline algebra: Algebra[V])(lines: Int, line: V, bytes: Long): V =
    if (lines == 0) algebra.literal(bytes)
    else {
      val scaled = if (lines == 1) line else algebra.multiply(algebra.literal(lines.toLong), line)
      if (bytes == 0L) scaled else algebra.add(scaled, algebra.literal(bytes))
    }

  private inline def place[V](inline algebra: Algebra[V])(
      columnMajor: V,
      line: V,
      lane: Int,
      columns: Int,
      width: Int
  ): V = {
    val (row, column) = (lane / columns, lane % columns)
    if (row == column) offsetOf(algebra)(row, line, row.toLong * width)
    else
      algebra.select(
        columnMajor,
        offsetOf(algebra)(column, line, row.toLong * width),
        offsetOf(algebra)(row, line, column.toLong * width)
      )
  }

  private inline def access[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      pointer: Int,
      layout: Either[Int, Int],
      stride: Option[Int],
      rows: Int,
      columns: Int,
      width: Int in 1 ~ 8
  )(inline next: (V, V) => F[Unit]): F[Unit] = {
    val khr = layout.isLeft
    machine.scalar(layout.merge)(selected =>
      machine.scalar(stride.getOrElse(layout.merge)) { supplied =>
        val columnMajor =
          if (khr) algebra.equal(selected, algebra.literal(1L)) else algebra.notEqual(selected, algebra.literal(0L))
        val valid =
          if (khr) algebra.lessThan(selected, algebra.literal(2L), signed = false) else algebra.literal(1L)
        val spacing =
          if (stride.isDefined) supplied
          else algebra.select(columnMajor, algebra.literal(rows.toLong), algebra.literal(columns.toLong))
        machine.require(valid, "cooperative matrix layout")(
          Fault.Unsupported(s"cooperative matrix layout $selected is not RowMajorKHR or ColumnMajorKHR")
        )(next(columnMajor, algebra.multiply(spacing, machine.strideBytes(pointer, width))))
      }
    )
  }

  /** The components of the `rows` by `columns` matrix `target`, typed `typeId`, are the `width` bytes at the offsets
    * the layout and stride place them at, past the address of `pointer`.
    */
  inline def matrixLoad[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      pointer: Int,
      layout: Either[Int, Int],
      stride: Option[Int],
      rows: Int,
      columns: Int,
      width: Int in 1 ~ 8,
      target: Int,
      typeId: Int
  ): F[Unit] = access(algebra, machine)(pointer, layout, stride, rows, columns, width)((columnMajor, line) =>
    machine.loadLeaves(pointer, rows * columns, width, target, typeId)(lane =>
      place(algebra)(columnMajor, line, lane, columns, width.raw)
    )(machine.advance)
  )

  /** The components of the matrix `source` holds are stored at the offsets the layout and stride place them at, past
    * the address of `pointer`.
    */
  inline def matrixStore[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      pointer: Int,
      source: Int,
      layout: Either[Int, Int],
      stride: Option[Int],
      rows: Int,
      columns: Int,
      width: Int in 1 ~ 8
  ): F[Unit] = access(algebra, machine)(pointer, layout, stride, rows, columns, width)((columnMajor, line) =>
    machine.storeLeaves(pointer, source, rows * columns, width)(lane =>
      place(algebra)(columnMajor, line, lane, columns, width.raw)
    )(machine.advance)
  )

  private inline def real[V](inline algebra: Algebra[V])(raw: V, kind: V): V =
    algebra.primitive(raw, kind)(VectorKinds.realOf(_: UInt[64], _: UInt[64]))

  private inline def integral[V](inline algebra: Algebra[V])(raw: V, kind: V): V =
    algebra.primitive(raw, kind)(VectorKinds.integerOf(_: UInt[64], _: UInt[64]))

  private inline def defined[V](inline algebra: Algebra[V])(kind: V): V =
    algebra.primitive(kind)(VectorKinds.defined(_: UInt[64]))

  private inline def packed[V](inline algebra: Algebra[V])(kind: V): V =
    algebra.primitive(kind)(VectorKinds.packed(_: UInt[64]))

  private inline def floating[V](inline algebra: Algebra[V])(kind: V): V =
    algebra.primitive(kind)(VectorKinds.floating(_: UInt[64]))

  private inline def bytes[V](inline algebra: Algebra[V])(kind: V): V =
    algebra.primitive(kind)(VectorKinds.bytes(_: UInt[64]))

  private inline def flag[V](inline algebra: Algebra[V])(value: Boolean): V = algebra.literal(if (value) 1L else 0L)

  /** The input element `at`, in the kind `inputKind` names: the bytes of 32-bit lanes if the kind is packed, otherwise
    * each lane converted.
    */
  private inline def element[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      operands: Product,
      shape: Shape,
      inputKind: V,
      at: V
  ): V = algebra.select(
    packed(algebra)(inputKind),
    algebra.and(
      algebra.shiftRightLogical(
        machine.leafAt(Array(operands.input), 1, algebra.shiftRightLogical(at, algebra.literal(2L))),
        algebra.multiply(algebra.and(at, algebra.literal(3L)), algebra.literal(8L))
      ),
      algebra.literal(0xffL)
    ),
    shape.source match {
      case Component.Floating(width) =>
        algebra.primitive(machine.leafAt(Array(operands.input), 1, at), algebra.literal(width.toLong), inputKind)(
          VectorKinds.fromFloat(_: UInt[64], _: UInt[64], _: UInt[64])
        )
      case Component.Integer(width) =>
        val lane     = machine.leafAt(Array(operands.input), 1, at)
        val extended = if (operands.signedInput) algebra.signExtend(lane, width) else algebra.truncate(lane, width)
        algebra.primitive(extended, flag(algebra)(operands.signedInput), inputKind)(
          VectorKinds.fromInteger(_: UInt[64], _: UInt[64], _: UInt[64])
        )
      case Component.Other => algebra.literal(0L)
    }
  )

  /** The byte offset of the matrix element at row `r` and column `c`, whose elements are `size` bytes. */
  private inline def placed[V](inline algebra: Algebra[V])(
      layout: V,
      transposed: V,
      spacing: V,
      m: V,
      k: V,
      size: V,
      r: V,
      c: V
  ): V = algebra.select(
    algebra.lessThan(layout, algebra.literal(2L), signed = false),
    algebra.select(
      algebra.equal(layout, algebra.literal(0L)),
      algebra.add(algebra.multiply(r, spacing), algebra.multiply(c, size)),
      algebra.add(algebra.multiply(c, spacing), algebra.multiply(r, size))
    ),
    algebra.select(
      transposed,
      algebra.multiply(algebra.add(algebra.multiply(c, m), r), size),
      algebra.multiply(algebra.add(algebra.multiply(r, k), c), size)
    )
  )

  private inline def accumulated[V](inline algebra: Algebra[V])(
      floatingResult: Boolean,
      sum: V,
      weight: V,
      matrixKind: V,
      input: V
  ): V =
    if (floatingResult)
      algebra.primitive(
        sum,
        algebra.primitive(real(algebra)(weight, matrixKind), input)(Matrices.multiply(_: UInt[64], _: UInt[64]))
      )(Matrices.add(_: UInt[64], _: UInt[64]))
    else algebra.add(sum, algebra.multiply(integral(algebra)(weight, matrixKind), input))

  private inline def finished[V](inline algebra: Algebra[V])(shape: Shape, sum: V): V = shape.result match {
    case Component.Floating(width) =>
      algebra.primitive(sum, algebra.literal(width.toLong))(Matrices.narrow(_: UInt[64], _: UInt[64]))
    case Component.Integer(width) => algebra.truncate(sum, width)
    case Component.Other          => sum
  }

  private inline def floatingResult(shape: Shape): Boolean = shape.result match {
    case Component.Floating(_) => true
    case _                     => false
  }

  private inline def packedSource(shape: Shape): Boolean = shape.source match {
    case Component.Integer(32) => true
    case _                     => false
  }

  /** The operands of the product are valid: the kinds are defined, the layout is one with the stride it needs, the
    * input holds the elements the kind packs or the columns, and an integer product has integer kinds.
    */
  inline def checks[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      operands: Product,
      shape: Shape,
      m: V,
      k: V,
      inputKind: V,
      matrixKind: V,
      biasKind: V,
      layout: V,
      transposed: V
  )(inline next: => F[Unit]): F[Unit] = {
    val rowwise     = algebra.lessThan(algebra.literal(1L), layout, signed = false)
    val packedInput = packed(algebra)(inputKind)
    val unpacked    = algebra.equal(packedInput, algebra.literal(0L))
    val strided     = if (operands.stride.isDefined) algebra.literal(1L) else rowwise
    val packedFits  =
      if (packedSource(shape))
        algebra.or(unpacked, algebra.lessOrEqual(k, algebra.literal(shape.inputCount * 4L), signed = true))
      else unpacked
    val numeric      = if (shape.source != Component.Other) algebra.literal(1L) else packedInput
    val biasDefined  = if (operands.bias.isEmpty) algebra.literal(1L) else defined(algebra)(biasKind)
    val integerKinds =
      if (floatingResult(shape)) algebra.literal(1L)
      else
        algebra.equal(
          algebra.or(
            algebra.or(floating(algebra)(inputKind), floating(algebra)(matrixKind)),
            floating(algebra)(biasKind)
          ),
          algebra.literal(0L)
        )
    machine.require(
      algebra.and(
        algebra.equal(m, algebra.literal(shape.rows.toLong)),
        algebra.lessThan(algebra.literal(0L), k, signed = true)
      ),
      "cooperative vector product shape"
    )(Fault.InvalidRequest(s"cooperative vector product of M $m and K $k into ${shape.rows} lanes"))(
      machine.require(defined(algebra)(inputKind), "cooperative vector input kind")(
        Fault.InvalidRequest(s"cooperative vector component type $inputKind is not defined")
      )(
        machine.require(defined(algebra)(matrixKind), "cooperative vector matrix kind")(
          Fault.InvalidRequest(s"cooperative vector component type $matrixKind is not defined")
        )(
          machine.require(algebra.lessThan(layout, algebra.literal(4L), signed = false), "cooperative vector layout")(
            Fault.InvalidRequest(s"cooperative vector matrix layout $layout is not defined")
          )(
            machine.require(
              algebra.or(rowwise, algebra.equal(transposed, algebra.literal(0L))),
              "cooperative vector transpose"
            )(Fault.InvalidRequest("row- and column-major cooperative vector matrices cannot be transposed"))(
              machine.require(strided, "cooperative vector stride")(
                Fault.InvalidRequest("a row- or column-major matrix needs a stride")
              )(
                machine.require(packedFits, "cooperative vector packed input")(
                  Fault.InvalidRequest(
                    s"packed cooperative vector input of ${shape.inputCount} lanes holds no $k bytes"
                  )
                )(
                  machine.require(
                    algebra.or(packedInput, algebra.equal(k, algebra.literal(shape.inputCount.toLong))),
                    "cooperative vector input length"
                  )(Fault.InvalidRequest(s"cooperative vector input of ${shape.inputCount} lanes is not K $k"))(
                    machine.require(numeric, "cooperative vector input component")(
                      Fault.InvalidRequest("cooperative vector input components are not numeric")
                    )(
                      machine.require(biasDefined, "cooperative vector bias kind")(
                        Fault.InvalidRequest(s"cooperative vector component type $biasKind is not defined")
                      )(
                        machine.require(integerKinds, "cooperative vector integer product")(
                          Fault.InvalidRequest("an integer cooperative vector product needs integer interpretations")
                        )(next)
                      )
                    )
                  )
                )
              )
            )
          )
        )
      )
    )
  }

  /** The partial results are zero, after the `k` converted input elements. */
  inline def inputs[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      operands: Product,
      shape: Shape,
      k: V,
      inputKind: V
  )(inline next: => F[Unit]): F[Unit] =
    machine.partials(shape.rows + (if (packedSource(shape)) shape.inputCount * 4 else shape.inputCount))(
      machine.repeat(k)(c =>
        machine.setPartial(
          algebra.add(algebra.literal(shape.rows.toLong), c),
          if (floatingResult(shape))
            real(algebra)(element(algebra, machine)(operands, shape, inputKind, c), inputKind)
          else integral(algebra)(element(algebra, machine)(operands, shape, inputKind, c), inputKind)
        )(machine.done)
      )(next)
    )

  /** Each partial result starts at its bias, or at zero. */
  inline def biases[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      operands: Product,
      shape: Shape,
      m: V,
      biasKind: V,
      biasOffset: V
  )(inline next: => F[Unit]): F[Unit] = {
    val size = bytes(algebra)(biasKind)
    machine.repeat(m)(r =>
      if (operands.bias.isDefined)
        machine.loadElement(
          operands.bias.fold(operands.matrix)(_._1),
          algebra.add(biasOffset, algebra.multiply(r, size)),
          size
        )(raw =>
          machine.setPartial(
            r,
            if (floatingResult(shape)) real(algebra)(raw, biasKind) else integral(algebra)(raw, biasKind)
          )(machine.done)
        )
      else machine.setPartial(r, algebra.literal(0L))(machine.done)
    )(next)
  }

  /** Each partial result adds the products of the elements of its row with the input. */
  inline def weights[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      operands: Product,
      shape: Shape,
      m: V,
      k: V,
      matrixKind: V,
      matrixOffset: V,
      layout: V,
      transposed: V,
      spacing: V
  )(inline next: => F[Unit]): F[Unit] = {
    val size = bytes(algebra)(matrixKind)
    machine.repeat(m)(r =>
      machine.repeat(k)(c =>
        machine.loadElement(
          operands.matrix,
          algebra.add(matrixOffset, placed(algebra)(layout, transposed, spacing, m, k, size, r, c)),
          size
        )(weight =>
          machine.setPartial(
            r,
            accumulated(algebra)(
              floatingResult(shape),
              machine.partial(r),
              weight,
              matrixKind,
              machine.partial(algebra.add(algebra.literal(shape.rows.toLong), c))
            )
          )(machine.done)
        )
      )(machine.done)
    )(next)
  }

  /** The lanes of `target`, typed `typeId`, are the partial results rounded to the result type. */
  inline def results[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      operands: Product,
      shape: Shape,
      target: Int,
      typeId: Int
  ): F[Unit] =
    machine.eachLeaf(Array(operands.input), shape.rows, target, typeId)(row =>
      finished(algebra)(shape, machine.partial(algebra.literal(row.toLong)))
    )(machine.advance)

  /** The operands of the product, which every stage reads. */
  inline def scalars[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(operands: Product)(
      inline next: (V, V, V, V, V, V, V, V, V, V) => F[Unit]
  ): F[Unit] = machine.scalar(operands.m)(m =>
    machine.scalar(operands.k)(k =>
      machine.scalar(operands.inputKind)(inputKind =>
        machine.scalar(operands.matrixKind)(matrixKind =>
          machine.scalar(operands.bias.fold(operands.matrixKind)(_._3))(biasKind =>
            machine.scalar(operands.bias.fold(operands.matrixOffset)(_._2))(biasOffset =>
              machine.scalar(operands.matrixOffset)(matrixOffset =>
                machine.scalar(operands.layout)(layout =>
                  machine.scalar(operands.transpose)(transposed =>
                    machine.scalar(operands.stride.getOrElse(operands.layout))(supplied =>
                      next(
                        m,
                        k,
                        inputKind,
                        matrixKind,
                        biasKind,
                        biasOffset,
                        matrixOffset,
                        layout,
                        transposed,
                        if (operands.stride.isDefined) supplied else algebra.literal(0L)
                      )
                    )
                  )
                )
              )
            )
          )
        )
      )
    )
  )

  /** The `rows` lanes of `target`, typed `typeId`, are the matrix in memory multiplied by the input, plus the bias if
    * there is one.
    */
  inline def product[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      operands: Product,
      shape: Shape,
      target: Int,
      typeId: Int
  ): F[Unit] = scalars(algebra, machine)(operands)(
    (m, k, inputKind, matrixKind, biasKind, biasOffset, matrixOffset, layout, transposed, spacing) =>
      checks(algebra, machine)(operands, shape, m, k, inputKind, matrixKind, biasKind, layout, transposed)(
        inputs(algebra, machine)(operands, shape, k, inputKind)(
          biases(algebra, machine)(operands, shape, m, biasKind, biasOffset)(
            weights(algebra, machine)(
              operands,
              shape,
              m,
              k,
              matrixKind,
              matrixOffset,
              layout,
              transposed,
              spacing
            )(results(algebra, machine)(operands, shape, target, typeId))
          )
        )
      )
  )
}
