package net.kurobako.gemstone.isa.spirv

import Operand.*
import VMProgram.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.render.*

/** SPIR-V cooperative matrix and vector semantics, as requests to a [[SPIRVMachine]]. */
private object CooperativeSemantics {

  private val matrixAccesses = Set(
    "OpCooperativeMatrixLoadKHR",
    "OpCooperativeMatrixLoadNV",
    "OpCooperativeMatrixStoreKHR",
    "OpCooperativeMatrixStoreNV"
  )

  private val vectorOperations = Set(
    "OpCooperativeVectorLoadNV",
    "OpCooperativeVectorStoreNV",
    "OpCooperativeVectorMatrixMulNV",
    "OpCooperativeVectorMatrixMulAddNV",
    "OpCooperativeVectorOuterProductAccumulateNV",
    "OpCooperativeVectorReduceSumAccumulateNV"
  )

  def accessesMatrix(instruction: Instruction): Boolean = matrixAccesses(instruction.opcode.raw)

  def operatesOnVector(instruction: Instruction): Boolean = vectorOperations(instruction.opcode.raw)

  inline def length[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      count: Long,
      target: Int,
      typeId: Int
  ): F[Unit] = machine.scalarValue(target, typeId, count)(machine.advance)

  inline def matrix[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      instruction: Instruction,
      rows: Int,
      columns: Int,
      width: Int in 1 ~ 8
  ): F[Unit] = instruction match {
    case Instruction.CooperativeMatrixLoadKHR(resultType, d, pointer, layout, stride, _) =>
      FragmentSemantics.matrixLoad(algebra, machine)(
        id(pointer),
        Left(id(layout)),
        stride.map(id(_)),
        rows,
        columns,
        width,
        id(d),
        id(resultType)
      )
    case Instruction.CooperativeMatrixLoadNV(resultType, d, pointer, stride, columnMajor, _) =>
      FragmentSemantics.matrixLoad(algebra, machine)(
        id(pointer),
        Right(id(columnMajor)),
        Some(id(stride)),
        rows,
        columns,
        width,
        id(d),
        id(resultType)
      )
    case Instruction.CooperativeMatrixStoreKHR(pointer, source, layout, stride, _) =>
      FragmentSemantics.matrixStore(algebra, machine)(
        id(pointer),
        id(source),
        Left(id(layout)),
        stride.map(id(_)),
        rows,
        columns,
        width
      )
    case Instruction.CooperativeMatrixStoreNV(pointer, source, stride, columnMajor, _) =>
      FragmentSemantics.matrixStore(algebra, machine)(
        id(pointer),
        id(source),
        Right(id(columnMajor)),
        Some(id(stride)),
        rows,
        columns,
        width
      )
    case _ => machine.unreachable
  }

  inline def vector[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(instruction: Instruction): F[Unit] =
    machine.cooperativeVector(instruction)

  inline def construct[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      source: Int,
      target: Int,
      typeId: Int,
      selector: Vec[Long]
  ): F[Unit] = machine.collective(Collective.MatrixRows(ResultIds(typeId, target), source, selector))

  /** Element `(row, column)` of the `rows` by `columns` matrix `matrix` holds, whose lanes are in row order, becomes
    * what the function of `sweep` returns for its row, column and value, and the same lane of each matrix of `operands`
    * or the value of any other; the function is called in column order. `sources` are the matrices read.
    */
  inline def perElement[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      sweep: Sweep,
      rows: Int,
      columns: Int,
      matrix: Int,
      operands: Array[Int],
      sources: Array[Int],
      target: Int,
      typeId: Int
  ): F[Unit] = machine.sweep(sweep) { (call, _, parameter) =>
    val (row, column) = (call % rows, call / rows)
    parameter match {
      case 0     => algebra.literal(row.toLong)
      case 1     => algebra.literal(column.toLong)
      case 2     => machine.leaf(matrix, row * columns + column)
      case other => machine.leaf(operands(other - 3), row * columns + column)
    }
  }(returned =>
    machine.eachLeaf(sources, rows * columns, target, typeId)(lane =>
      returned(lane % columns * rows + lane / columns)
    )(machine.advance)
  )

  /** The lanes of `target` are what the function of `sweep` returns for the lanes of `matrix` that `reduction` folds.
    */
  inline def reduce[V, D, F[_]](inline machine: SPIRVMachine[V, D, F])(
      sweep: Sweep,
      reduction: Reduction,
      matrix: Int,
      target: Int,
      typeId: Int
  ): F[Unit] = machine.sweep(sweep) { (call, previous, parameter) =>
    if (parameter == 1) machine.leaf(matrix, reduction.second(call))
    else if (reduction.first(call) < 0) previous
    else machine.leaf(matrix, reduction.first(call))
  }(returned =>
    machine.eachLeaf(Array(matrix), reduction.results.length, target, typeId)(lane =>
      if (reduction.results(lane) < 0) machine.leaf(matrix, -reduction.results(lane) - 1)
      else returned(reduction.results(lane))
    )(machine.advance)
  )

  /** The components of the invocation's own line of the `rows` by `columns` matrix `source`, packed into the `count`
    * elements of `target`, typed `typeId`, then `next`.
    */
  inline def ownLine[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      source: Int,
      rows: Int,
      columns: Int,
      use: Int,
      componentBits: Int,
      elementBits: Int,
      count: Int,
      target: Int,
      typeId: Int
  ): F[Unit] = machine.eachLeaf(Array(source), count, target, typeId)(element =>
    packed(algebra, machine)(source, rows, columns, use, componentBits, elementBits, element)
  )(machine.advance)

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private inline def packed[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      source: Int,
      rows: Int,
      columns: Int,
      use: Int,
      componentBits: Int,
      elementBits: Int,
      element: Int
  ): V = {
    val each = elementBits / componentBits
    val mask = if (componentBits == 64) -1L else (1L << componentBits) - 1L
    var word = algebra.and(machine.ownLeaf(source, element * each, rows, columns, use), algebra.literal(mask))
    var part = 1
    while (part < each) {
      val component =
        algebra.and(machine.ownLeaf(source, element * each + part, rows, columns, use), algebra.literal(mask))
      word = algebra.or(word, algebra.shiftLeft(component, algebra.literal((part * componentBits).toLong)))
      part += 1
    }
    word
  }

  /** The operands, destination and result type of the cooperative vector product `instruction` names. */
  def productOf(instruction: Instruction): Option[(FragmentSemantics.Product, Int, Int)] = instruction match {
    case Instruction.CooperativeVectorMatrixMulNV(
          resultType,
          d,
          input,
          inputKind,
          matrix,
          matrixOffset,
          matrixKind,
          m,
          k,
          layout,
          transpose,
          stride,
          flags
        ) =>
      Some((
        FragmentSemantics.Product(
          input = id(input),
          inputKind = id(inputKind),
          matrix = id(matrix),
          matrixOffset = id(matrixOffset),
          matrixKind = id(matrixKind),
          bias = None,
          m = id(m),
          k = id(k),
          layout = id(layout),
          transpose = id(transpose),
          stride = stride.map(id(_)),
          signedInput = signedInput(flags)
        ),
        id(d),
        id(resultType)
      ))
    case Instruction.CooperativeVectorMatrixMulAddNV(
          resultType,
          d,
          input,
          inputKind,
          matrix,
          matrixOffset,
          matrixKind,
          bias,
          biasOffset,
          biasKind,
          m,
          k,
          layout,
          transpose,
          stride,
          flags
        ) =>
      Some((
        FragmentSemantics.Product(
          input = id(input),
          inputKind = id(inputKind),
          matrix = id(matrix),
          matrixOffset = id(matrixOffset),
          matrixKind = id(matrixKind),
          bias = Some((id(bias), id(biasOffset), id(biasKind))),
          m = id(m),
          k = id(k),
          layout = id(layout),
          transpose = id(transpose),
          stride = stride.map(id(_)),
          signedInput = signedInput(flags)
        ),
        id(d),
        id(resultType)
      ))
    case _ => None
  }

  private def checked(machine: SPIRVMachineRender, operands: FragmentSemantics.Product, shape: FragmentSemantics.Shape)
      : Term.Steps = FragmentSemantics.scalars(Render, machine)(operands)(
    (m, k, inputKind, matrixKind, biasKind, _, _, layout, transposed, _) =>
      FragmentSemantics.checks(Render, machine)(
        operands,
        shape,
        m,
        k,
        inputKind,
        matrixKind,
        biasKind,
        layout,
        transposed
      )(Term.Steps.empty)
  )

  private def converted(
      machine: SPIRVMachineRender,
      operands: FragmentSemantics.Product,
      shape: FragmentSemantics.Shape
  ): Term.Steps = FragmentSemantics.scalars(Render, machine)(operands)((_, k, inputKind, _, _, _, _, _, _, _) =>
    FragmentSemantics.inputs(Render, machine)(operands, shape, k, inputKind)(Term.Steps.empty)
  )

  private def started(
      machine: SPIRVMachineRender,
      operands: FragmentSemantics.Product,
      shape: FragmentSemantics.Shape
  ): Term.Steps = FragmentSemantics.scalars(Render, machine)(operands)((m, _, _, _, biasKind, biasOffset, _, _, _, _) =>
    FragmentSemantics.biases(Render, machine)(operands, shape, m, biasKind, biasOffset)(Term.Steps.empty)
  )

  private def summed(
      machine: SPIRVMachineRender,
      operands: FragmentSemantics.Product,
      shape: FragmentSemantics.Shape
  ): Term.Steps = FragmentSemantics.scalars(Render, machine)(operands)(
    (m, k, _, matrixKind, _, _, matrixOffset, layout, transposed, spacing) =>
      FragmentSemantics.weights(Render, machine)(
        operands,
        shape,
        m,
        k,
        matrixKind,
        matrixOffset,
        layout,
        transposed,
        spacing
      )(Term.Steps.empty)
  )

  private def renderProduct(
      machine: SPIRVMachineRender,
      operands: FragmentSemantics.Product,
      shape: FragmentSemantics.Shape,
      target: Int,
      typeId: Int
  ): Term.Steps = checked(machine, operands, shape) ++ converted(machine, operands, shape) ++
    started(machine, operands, shape) ++ summed(machine, operands, shape) ++
    FragmentSemantics.results(Render, machine)(operands, shape, target, typeId)

  private def shapeOf(machine: SPIRVMachineRender, result: Int, input: Int): FragmentSemantics.Shape =
    FragmentSemantics.Shape(
      machine.leafCount(result),
      machine.leafCount(input),
      machine.component(result),
      machine.component(input)
    )

  private def named(function: Int): Function = Function(function, Vec.empty, 0, 0, Map.empty)

  private def renderPerElement(machine: SPIRVMachineRender, rows: Int, columns: Int)(
      resultType: Int,
      target: Int,
      source: Int,
      function: Int,
      operands: Vec[Int]
  ): Term.Steps = {
    val same    = Vec.tabulate(operands.size)(at => machine.leafCount(operands(at)) == rows * columns)
    val fixed   = Array.tabulate(3 + operands.size)(at => if (at < 3 || same(at - 3)) -1 else operands(at - 3))
    val sources = source +: operands.indices.filter(same(_)).map(operands(_))
    perElement(Render, machine)(
      Sweep(named(function), resultType, fixed, rows * columns),
      rows,
      columns,
      source,
      operands.toArray,
      sources.toArray,
      target,
      resultType
    )
  }

  private def renderReduce(machine: SPIRVMachineRender, reduction: Reduction)(
      resultType: Int,
      target: Int,
      source: Int,
      combine: Int
  ): Term.Steps =
    reduce(machine)(
      Sweep(named(combine), resultType, Array(-1, -1), reduction.calls),
      reduction,
      source,
      target,
      resultType
    )

  private def signedInput(flags: Option[Operand]): Boolean =
    (flags.fold(0L)(id(_).toLong) & VectorKinds.MatrixBSigned) != 0L

  private def matrixValue(instruction: Instruction): Int = instruction match {
    case Instruction.CooperativeMatrixLoadKHR(dest = d)      => id(d)
    case Instruction.CooperativeMatrixLoadNV(dest = d)       => id(d)
    case Instruction.CooperativeMatrixStoreKHR(src = source) => id(source)
    case Instruction.CooperativeMatrixStoreNV(src = source)  => id(source)
    case _                                                   => 0
  }

  /** The forms the explorer shows, for matrices of `rows` by `columns` of `use`: lengths, loads, stores, conversions
    * between uses, transposition, the lines composites convert to and from, and cooperative vector operations.
    */
  def render(
      instruction: Instruction,
      machine: SPIRVMachineRender,
      rows: Int,
      columns: Int,
      use: Int
  ): Option[Term.Steps] = instruction match {
    case Instruction.CompositeConstructCoopMatQCOM(resultType, dest, source) =>
      Some(
        construct(machine)(
          id(source),
          id(dest),
          id(resultType),
          Vec(
            rows.toLong,
            columns.toLong,
            use.toLong,
            machine.leafBytes(id(dest)) * 8L,
            machine.leafBytes(id(source)) * 8L
          )
        )
      )
    case Instruction.CompositeExtractCoopMatQCOM(resultType, dest, source) =>
      Some(
        ownLine(Render, machine)(
          id(source),
          rows,
          columns,
          use,
          machine.leafBytes(id(source)) * 8,
          machine.leafBytes(id(dest)) * 8,
          machine.leafCount(id(dest)),
          id(dest),
          id(resultType)
        )
      )
    case Instruction.CooperativeMatrixLengthKHR(resultType, dest, _) =>
      Some(length(machine)((rows * columns).toLong, id(dest), id(resultType)))
    case Instruction.CooperativeMatrixLengthNV(resultType, dest, _) =>
      Some(length(machine)((rows * columns).toLong, id(dest), id(resultType)))
    case Instruction.CooperativeMatrixConvertNV(resultType, dest, source) =>
      Some(CompositeSemantics.copy(machine)(id(source), id(dest), id(resultType)))
    case Instruction.CooperativeMatrixTransposeNV(resultType, dest, source) =>
      Some(MatrixSemantics.transpose(machine)(id(source), rows = columns, columns = rows, id(dest), id(resultType)))
    case Instruction.CooperativeMatrixPerElementOpNV(resultType, dest, source, function, operands) =>
      Some(renderPerElement(
        machine,
        rows,
        columns
      )(id(resultType), id(dest), id(source), id(function), operands.map(id(_))))
    case Instruction.CooperativeMatrixReduceNV(resultType, dest, source, reduce, combine) =>
      Reduction.of(rows, columns, unsigned(reduce), machine.leafCount(id(dest))).toOption.map(reduction =>
        renderReduce(machine, reduction)(id(resultType), id(dest), id(source), id(combine))
      )
    case _ if accessesMatrix(instruction) =>
      Some(matrix(Render, machine)(instruction, rows, columns, machine.leafBytes(matrixValue(instruction))))
    case _ if productOf(instruction).isDefined =>
      productOf(instruction).map((operands, target, typeId) =>
        renderProduct(machine, operands, shapeOf(machine, target, operands.input), target, typeId)
      )
    case _ if operatesOnVector(instruction) => Some(vector(machine)(instruction))
    case _                                  => None
  }
}
