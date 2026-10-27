package net.kurobako.gemstone.isa.spirv

import Op.*
import VMInvocation.*
import VMMemory.*
import VMProgram.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*

/** SPV_NV_tensor_addressing layouts and views, and SPV_NV_cooperative_matrix2's tensor loads and stores, which address
  * each matrix element as that extension's matrixCoordToTensorElement and matrixCoordToTensorElementWithView define.
  * State is 32-bit, so addressing wraps as the extension's uint32_t arithmetic does.
  */
private object TensorAddressingOps {
  import Operand.*

  private val Word = 0xffffffffL

  private val TensorViewOperand = 0x1
  private val DecodeFunc        = 0x2

  def lower: PartialFunction[Instruction, Op] = {
    case instruction if TensorAddressingSemantics.transfers(instruction) => TensorAddressingOp(instruction)
  }

  private class TensorAddressingOp(instruction: Instruction) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      TensorAddressingSemantics.tensorAddressing(InvocationMachine(execution))(instruction)
  }

  /** Runs the tensor layout, view or tensor transfer `instruction` names, then continues after it. */
  def execute(execution: InvocationState, instruction: Instruction): Flow[Unit] = {
    val values = execution.values
    instruction match {
      case Instruction.CreateTensorLayoutNV(t, d) =>
        advancing(execution)(layoutType(values, id(t)).map(_ => values.update(id(d), createdLayout(id(t)))))
      case Instruction.TensorLayoutSetDimensionNV(t, d, source, dimensions) =>
        advancing(execution)(layoutType(values, id(t)).flatMap(shape =>
          update(values, t, d, source, dimensions, shape.dimensions) { (lanes, inputs) =>
            inputs.indices.foreach { at =>
              lanes(TensorLayout.Dimension + at) = inputs(at)
              lanes(TensorLayout.Span + at) = inputs(at)
              lanes(TensorLayout.Offset + at) = 0L
            }
            lanes(TensorLayout.Stride + inputs.length - 1) = 1L
            (inputs.length - 2 to 0 by -1).foreach { at =>
              val block = math.max(lanes(TensorLayout.BlockSize + at + 1), 1L)
              lanes(TensorLayout.Stride + at) =
                (lanes(TensorLayout.Stride + at + 1) * ((inputs(at + 1) + block - 1) / block)) & Word
            }
          }
        ))
      case Instruction.TensorLayoutSetStrideNV(t, d, source, strides) =>
        advancing(execution)(layoutType(values, id(t)).flatMap(shape =>
          replace(values, t, d, source, strides, shape.dimensions, TensorLayout.Stride)
        ))
      case Instruction.TensorLayoutSetBlockSizeNV(t, d, source, sizes) =>
        advancing(execution)(layoutType(values, id(t)).flatMap(shape =>
          replace(values, t, d, source, sizes, shape.dimensions, TensorLayout.BlockSize)
        ))
      case Instruction.TensorLayoutSliceNV(t, d, source, slices) =>
        advancing(execution)(layoutType(values, id(t)).flatMap(shape =>
          update(values, t, d, source, slices, shape.dimensions * 2) { (lanes, inputs) =>
            (0 until shape.dimensions).foreach { at =>
              lanes(TensorLayout.Offset + at) = (lanes(TensorLayout.Offset + at) + inputs(at * 2)) & Word
              lanes(TensorLayout.Span + at) = inputs(at * 2 + 1)
            }
          }
        ))
      case Instruction.TensorLayoutSetClampValueNV(t, d, source, value) =>
        advancing(execution)(layoutType(values, id(t)).flatMap(_ =>
          update(values, t, d, source, Vec(value), 1)((lanes, inputs) => lanes(TensorLayout.ClampValue) = inputs(0))
        ))
      case Instruction.CreateTensorViewNV(t, d) =>
        advancing(execution)(viewType(values, id(t)).map(_ => values.update(id(d), createdView(id(t)))))
      case Instruction.TensorViewSetDimensionNV(t, d, source, dimensions) =>
        advancing(execution)(viewType(values, id(t)).flatMap(shape =>
          update(values, t, d, source, dimensions, shape.dimensions) { (lanes, inputs) =>
            inputs.indices.foreach(at => lanes(TensorView.Dimension + at) = inputs(at))
            lanes(TensorView.Stride + inputs.length - 1) = 1L
            (inputs.length - 2 to 0 by -1).foreach(at =>
              lanes(TensorView.Stride + at) = (lanes(TensorView.Stride + at + 1) * inputs(at + 1)) & Word
            )
          }
        ))
      case Instruction.TensorViewSetStrideNV(t, d, source, strides) =>
        advancing(execution)(viewType(values, id(t)).flatMap(shape =>
          replace(values, t, d, source, strides, shape.dimensions, TensorView.Stride)
        ))
      case Instruction.TensorViewSetClipNV(t, d, source, rowOffset, rowSpan, columnOffset, columnSpan) =>
        advancing(execution)(viewType(values, id(t)).flatMap(_ =>
          replace(values, t, d, source, Vec(rowOffset, rowSpan, columnOffset, columnSpan), 4, TensorView.Clip)
        ))
      case Instruction.CooperativeMatrixLoadTensorNV(t, d, pointer, source, tensorLayout, _, operands) =>
        advancing(execution)(transfer(
          execution,
          id(t),
          id(pointer),
          id(source),
          id(tensorLayout),
          operands,
          Some(id(d))
        ))
      case Instruction.CooperativeMatrixStoreTensorNV(pointer, source, tensorLayout, _, operands) =>
        advancing(execution)(
          transfer(
            execution,
            values.program.valueType(id(source)),
            id(pointer),
            id(source),
            id(tensorLayout),
            operands,
            None
          )
        )
      case _ => Left(Fault.Unsupported(s"${instruction.opcode} is not a tensor addressing operation"))
    }
  }

  private def advancing(execution: InvocationState)(outcome: => Flow[Unit]): Flow[Unit] =
    outcome.map(_ => execution.pc += 1)

  private def layoutType(values: InvocationValues, typeId: Int): Either[Fault, TensorLayout] =
    values.program.tensorLayouts.get(typeId).toRight(Fault.InvalidRequest(s"%$typeId is not a tensor layout type"))

  private def viewType(values: InvocationValues, typeId: Int): Either[Fault, TensorView] =
    values.program.tensorViews.get(typeId).toRight(Fault.InvalidRequest(s"%$typeId is not a tensor view type"))

  private def createdLayout(typeId: Int): VMValue = {
    val lanes = Array.ofDim[Long](TensorLayout.Lanes)
    java.util.Arrays.fill(lanes, TensorLayout.BlockSize, TensorLayout.BlockSize + 5, 1L)
    VMValue.Bits(typeId, lanes)
  }

  private def createdView(typeId: Int): VMValue = {
    val lanes = Array.ofDim[Long](TensorView.Lanes)
    lanes(TensorView.Clip + 1) = Word
    lanes(TensorView.Clip + 3) = Word
    VMValue.Bits(typeId, lanes)
  }

  /** A copy of `source` that `change` updates with the 32-bit values of `inputs`, of which there must be `count`. */
  private def update(
      values: InvocationValues,
      resultType: Operand,
      dest: Operand,
      source: Operand,
      inputs: Vec[Operand],
      count: Int
  )(change: (Array[Long], Array[Long]) => Unit): Flow[Unit] =
    if (inputs.length != count)
      Left(Fault.InvalidRequest(s"tensor state update takes $count operands, not ${inputs.length}"))
    else {
      val (typeId, target, state, ids) = (id(resultType), id(dest), id(source), inputs.map(id(_)))
      val read                         = Array.ofDim[Long](count)
      for {
        lanes <- bits(values, state)
        _     <- words(values, ids, read)
      } yield {
        val copy = lanes.clone()
        change(copy, read)
        values.update(target, VMValue.Bits(typeId, copy))
      }
    }

  /** The 32-bit values of the scalars `ids` into `read`. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
  private def words(values: InvocationValues, ids: Vec[Int], read: Array[Long]): Either[Fault, Unit] = {
    var failure: Fault | Null = null
    var at                    = 0
    while (at < read.length && failure == null) {
      scalar(values, ids(at)) match {
        case Left(error)  => failure = error
        case Right(value) => read(at) = value & Word
      }
      at += 1
    }
    if (failure != null) Left(failure) else Right(())
  }

  private def replace(
      values: InvocationValues,
      resultType: Operand,
      dest: Operand,
      source: Operand,
      inputs: Vec[Operand],
      count: Int,
      at: Int
  ): Flow[Unit] =
    update(values, resultType, dest, source, inputs, count)((lanes, inputs) =>
      inputs.indices.foreach(index => lanes(at + index) = inputs(index))
    )

  private def transfer(
      execution: InvocationState,
      matrixType: Int,
      pointer: Int,
      source: Int,
      tensorLayout: Int,
      operands: Operand,
      dest: Option[Int]
  ): Flow[Unit] = {
    val values  = execution.values
    val program = values.program
    val memory  = execution.memory
    val words   = Vec.from(operands.words.map(_.raw))
    val mask    = words.head
    (
      program.cooperativeMatrices.get(matrixType),
      program.tensorLayouts.get(program.valueType(tensorLayout)),
      Option.when((mask & TensorViewOperand) != 0)(words.lift(1)).map(_.flatMap(view =>
        program.tensorViews.get(program.valueType(view)).map(view -> _)
      ))
    ) match {
      case _ if (mask & DecodeFunc) != 0 =>
        Left(
          Fault.Unsupported("a tensor load's DecodeFunc calls a SPIR-V function for each element, which no VM op makes")
        )
      case (_, _, Some(None)) =>
        Left(Fault.InvalidRequest("a tensor load or store's TensorView is not a tensor view"))
      case (Some(shape), Some(layoutShape), viewOperand) =>
        program.scalarMemoryWidth(shape.component).flatMap { width =>
          val viewing = viewOperand.flatten
          for {
            base   <- address(values, pointer)
            lanes  <- bits(values, source)
            state  <- bits(values, tensorLayout)
            viewed <- viewing.fold[Either[Fault, Option[(Array[Long], TensorView)]]](Right(None))((at, shape) =>
              bits(values, at).map(view => Some((view, shape)))
            )
            places <- Either.cond(
              (0 until layoutShape.dimensions).forall(at => state(TensorLayout.Span + at) != 0L) &&
                viewed.forall((view, shape) =>
                  !shape.hasDimensions || (0 until shape.dimensions).forall(at =>
                    view(TensorView.Dimension + at) != 0L
                  )
                ),
              Addressing(state, layoutShape, viewed, shape.columns, dest.isEmpty).indices(shape.rows * shape.columns),
              Fault.InvalidRequest("a tensor layout or view addresses through a zero span or dimension")
            )
            access = MatrixAccess(base, places, width, sparse = true)
            result <- dest.fold[AddressSpace.ReadResult[Unit]](access.write(memory, lanes)) { target =>
              val clamp = state(TensorLayout.ClampValue) &
                (if (width >= 4) Word else (1L << (width * 8)) - 1L)
              val loaded = lanes.clone()
              places.indices.foreach(at => if (places(at) == Clamped) loaded(at) = clamp)
              access.read(memory, loaded).map(read => values.update(target, VMValue.Bits(matrixType, read)))
            }
          } yield result
        }
      case _ => Left(Fault.InvalidRequest("a tensor load or store needs a cooperative matrix and a tensor layout"))
    }
  }

  /** A matrix element outside a store's tensor or a view's clip, which the transfer leaves alone. */
  private val Skipped = -1L

  /** A matrix element outside a load's tensor under constant clamping, which reads the clamp value. */
  private val Clamped = -2L

  private object Addressing {
    private object Clamp {
      val Undefined: Long   = 0L
      val Constant: Long    = 1L
      val ClampToEdge: Long = 2L
      val Repeat: Long      = 3L
    }
  }

  /** How one execution's matrix of `columns` columns addresses the tensor: each element's tensor element, which stores
    * outside the tensor discard unless clamping is undefined, where the VM addresses the unclamped coordinate.
    */
  private class Addressing(
      state: Array[Long],
      shape: TensorLayout,
      viewed: Option[(Array[Long], TensorView)],
      columns: Int,
      store: Boolean
  ) {
    import Addressing.*

    private val span = Array.ofDim[Long](shape.dimensions)

    /** The view's dimensions and strides, each held in the axis order the layout reads. */
    private val (dimensions, strides) = viewed.fold((Array.emptyLongArray, Array.emptyLongArray)) { (view, viewShape) =>
      val dimensions = Array.tabulate(viewShape.dimensions)(at =>
        if (viewShape.hasDimensions) view(TensorView.Dimension + at) else state(TensorLayout.Span + at)
      )
      val strides = Array.ofDim[Long](viewShape.dimensions)
      if (viewShape.hasDimensions)
        (0 until viewShape.dimensions).foreach(at => strides(at) = view(TensorView.Stride + at))
      else {
        strides(viewShape.dimensions - 1) = 1L
        (viewShape.dimensions - 2 to 0 by -1).foreach(at =>
          strides(at) = (strides(at + 1) * state(TensorLayout.Span + at + 1)) & Word
        )
      }
      (dimensions, strides)
    }
    private val coordinate = Array.ofDim[Long](dimensions.length)

    def indices(count: Int): Array[Long] = Array.tabulate(count)(at => element(linear(at / columns, at % columns)))

    // the element's index in the view's clip, or Skipped outside it
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private def linear(row: Int, column: Int): Long = viewed match {
      case None                    => (row.toLong * columns + column) & Word
      case Some((view, viewShape)) =>
        val (rowOffset, rowSpan)       = (view(TensorView.Clip), view(TensorView.Clip + 1))
        val (columnOffset, columnSpan) = (view(TensorView.Clip + 2), view(TensorView.Clip + 3))
        if (
          row < rowOffset || row >= rowOffset + rowSpan || column < columnOffset || column >= columnOffset + columnSpan
        ) Skipped
        else {
          val width     = math.min(columns.toLong, columnSpan)
          var remaining = ((row - rowOffset) * width + (column - columnOffset)) & Word
          var at        = viewShape.dimensions - 1
          while (at >= 0) {
            val axis = viewShape.permutation(at)
            coordinate(axis) = remaining % dimensions(axis)
            remaining /= dimensions(axis)
            at -= 1
          }
          var sum = 0L
          at = 0
          while (at < viewShape.dimensions) {
            sum = (sum + coordinate(at) * strides(at)) & Word
            at += 1
          }
          sum
        }
    }

    // the tensor element at `index` of the layout's span, or Skipped or Clamped outside the tensor
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private def element(index: Long): Long =
      if (index == Skipped) Skipped
      else {
        var remaining = index
        var at        = shape.dimensions - 1
        while (at >= 0) {
          span(at) = remaining % state(TensorLayout.Span + at)
          remaining /= state(TensorLayout.Span + at)
          at -= 1
        }
        var sum = 0L
        at = 0
        while (at < shape.dimensions && sum >= 0L) {
          val extent     = state(TensorLayout.Dimension + at)
          val coordinate = span(at) + signed(state(TensorLayout.Offset + at))
          val outside    = (coordinate < 0L || coordinate >= extent) && shape.clampMode != Clamp.Undefined
          if (outside && (store || shape.clampMode == Clamp.Constant)) sum = if (store) Skipped else Clamped
          else {
            val clamped =
              if (!outside) coordinate
              else if (shape.clampMode == Clamp.ClampToEdge) math.min(math.max(coordinate, 0L), extent - 1L)
              else if (shape.clampMode == Clamp.Repeat) Math.floorMod(coordinate, math.max(extent, 1L))
              else if (extent <= 1L) 0L
              else {
                val period = Math.floorMod(coordinate, 2L * extent - 2L)
                if (period >= extent) 2L * extent - 2L - period else period
              }
            val block = math.max(state(TensorLayout.BlockSize + at), 1L)
            sum = (sum + (clamped / block) * state(TensorLayout.Stride + at)) & Word
          }
          at += 1
        }
        sum
      }
  }

  private def signed(word: Long): Long = word.toInt.toLong
}
