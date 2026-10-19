package net.kurobako.gemstone.isa.spirv

import Op.*
import VMInvocation.*
import VMMemory.*
import VMProgram.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*

/** SPV_NV_cooperative_vector. A cooperative vector is an ordinary vector of its invocation, and matrices and biases
  * stay in memory. Products accumulate in binary64 and round once to the result's type; integer products wrap at the
  * result's width. The optimal layouts, which only a driver's matrix conversion writes, are tightly packed row-major,
  * so a transposed M by K matrix is stored as K rows of M elements.
  */
private object CooperativeVectorOps {
  import Operand.*

  private val MatrixBSigned = 0x2L

  private enum Interpretation(
      val bits: Int in (8, 16, 32, 64),
      val floating: Boolean,
      val signed: Boolean,
      val packed: Boolean
  ) derives CanEqual {
    case Float16     extends Interpretation(bits = 16, floating = true, signed = true, packed = false)
    case Float32     extends Interpretation(bits = 32, floating = true, signed = true, packed = false)
    case Float64     extends Interpretation(bits = 64, floating = true, signed = true, packed = false)
    case E4M3        extends Interpretation(bits = 8, floating = true, signed = true, packed = false)
    case E5M2        extends Interpretation(bits = 8, floating = true, signed = true, packed = false)
    case Int8        extends Interpretation(bits = 8, floating = false, signed = true, packed = false)
    case Int16       extends Interpretation(bits = 16, floating = false, signed = true, packed = false)
    case Int32       extends Interpretation(bits = 32, floating = false, signed = true, packed = false)
    case Int64       extends Interpretation(bits = 64, floating = false, signed = true, packed = false)
    case UInt8       extends Interpretation(bits = 8, floating = false, signed = false, packed = false)
    case UInt16      extends Interpretation(bits = 16, floating = false, signed = false, packed = false)
    case UInt32      extends Interpretation(bits = 32, floating = false, signed = false, packed = false)
    case UInt64      extends Interpretation(bits = 64, floating = false, signed = false, packed = false)
    case Int8Packed  extends Interpretation(bits = 8, floating = false, signed = true, packed = true)
    case UInt8Packed extends Interpretation(bits = 8, floating = false, signed = false, packed = true)

    def bytes: Int in 1 ~ 8  = (bits / 8).unsafe[Int in 1 ~ 8]
    def width: Int in 1 ~ 64 = bits.widen[Int in 1 ~ 64]
  }
  import Interpretation.*

  private def interpretation(value: Long): Either[Fault, Interpretation] = value match {
    case 0L          => Right(Float16)
    case 1L          => Right(Float32)
    case 2L          => Right(Float64)
    case 3L          => Right(Int8)
    case 4L          => Right(Int16)
    case 5L          => Right(Int32)
    case 6L          => Right(Int64)
    case 7L          => Right(UInt8)
    case 8L          => Right(UInt16)
    case 9L          => Right(UInt32)
    case 10L         => Right(UInt64)
    case 1000491000L => Right(Int8Packed)
    case 1000491001L => Right(UInt8Packed)
    case 1000491002L => Right(E4M3)
    case 1000491003L => Right(E5M2)
    case _           => Left(Fault.InvalidRequest(s"cooperative vector component type $value is not defined"))
  }

  def lower(site: Site): PartialFunction[Instruction, Op] = {
    case Instruction.CooperativeVectorLoadNV(t, d, pointer, offset, _) =>
      vector(site, id(t), "OpCooperativeVectorLoadNV") { (_, count, width) =>
        step { (execution, memory) =>
          val values = execution.values
          for {
            base  <- address(values, id(pointer))
            start <- scalar(values, id(offset))
            lanes <- (0 until count).foldLeft[AddressSpace.ReadResult[Array[Long]]](Right(Array.ofDim[Long](count)))(
              (result, lane) =>
                result.flatMap(read =>
                  memory.readScalar(base.copy(address = base.address + start + lane * width), width).map {
                    value =>
                      read(lane) = value
                      read
                  }
                )
            )
          } yield values.update(id(d), VMValue.Bits(id(t), lanes))
        }
      }
    case Instruction.CooperativeVectorStoreNV(pointer, offset, source, _) =>
      vector(site, site.program.valueType(id(source)), "OpCooperativeVectorStoreNV") { (_, count, width) =>
        step { (execution, memory) =>
          val values = execution.values
          for {
            base  <- address(values, id(pointer))
            start <- scalar(values, id(offset))
            lanes <- bits(values, id(source))
            _     <- (0 until count).foldLeft[Either[Fault, Unit]](Right(()))((result, lane) =>
              result.flatMap(_ =>
                memory.writeScalar(base.copy(address = base.address + start + lane * width), lanes(lane), width)
              )
            )
          } yield ()
        }
      }
    case Instruction.CooperativeVectorMatrixMulNV(
          t,
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
          operands
        ) =>
      product(
        site,
        id(t),
        id(d),
        Product(
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
          operands = operands.fold(0L)(id(_).toLong)
        )
      )
    case Instruction.CooperativeVectorMatrixMulAddNV(
          t,
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
          operands
        ) =>
      product(
        site,
        id(t),
        id(d),
        Product(
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
          operands = operands.fold(0L)(id(_).toLong)
        )
      )
    case Instruction.CooperativeVectorOuterProductAccumulateNV(pointer, offset, a, b, layout, matrixKind, stride) =>
      outerProduct(
        site,
        Accumulation(
          pointer = id(pointer),
          offset = id(offset),
          a = id(a),
          b = id(b),
          layout = id(layout),
          matrixKind = id(matrixKind),
          stride = stride.map(id(_))
        )
      )
    case Instruction.CooperativeVectorReduceSumAccumulateNV(pointer, offset, source) =>
      val program = site.program
      vector(site, program.valueType(id(source)), "OpCooperativeVectorReduceSumAccumulateNV") {
        (component, count, width) =>
          val add: (Long, Long) => Long = program.runtimeType(component) match {
            case Some(RuntimeType.Floating(bits)) => IEEE754.add(_, _, bits, IEEE754.NaNOperand.Left)
            case _                                => _ + _
          }
          step { (execution, memory) =>
            val values = execution.values
            for {
              base  <- address(values, id(pointer))
              start <- scalar(values, id(offset))
              lanes <- bits(values, id(source))
              _     <- (0 until count).foldLeft[Either[Fault, Unit]](Right(()))((result, lane) =>
                result.flatMap(_ =>
                  memory.atomicModifyScalar(base.copy(address = base.address + start + lane * width), width)(previous =>
                    add(previous, lanes(lane))
                  ).map(_ => ())
                )
              )
            } yield ()
          }
      }
  }

  private inline def vector(site: Site, typeId: Int, opcode: String)(inline op: (Int, Int, Int in 1 ~ 8) => Op): Op =
    site.program.runtimeType(typeId) match {
      case Some(RuntimeType.VectorType(component, count)) =>
        lowered(site.program.scalarMemoryWidth(component).map(op(component, count, _)))
      case _ => Fail(Fault.InvalidRequest(s"$opcode type %$typeId is not a cooperative vector"))
    }

  private case class Product(
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
      operands: Long
  )

  private def product(site: Site, resultType: Int, dest: Int, operands: Product): Op = {
    val program = site.program
    (program.runtimeType(resultType), program.runtimeType(program.valueType(operands.input))) match {
      case (
            Some(RuntimeType.VectorType(resultComponent, resultCount)),
            Some(RuntimeType.VectorType(inputComponent, inputCount))
          ) =>
        (program.runtimeType(resultComponent), program.runtimeType(inputComponent)) match {
          case (Some(result @ (RuntimeType.Floating(_) | RuntimeType.Integer(_, _))), Some(source)) =>
            step { (execution, memory) =>
              val values = execution.values
              for {
                rows    <- scalar(values, operands.m).map(_.toInt)
                columns <- scalar(values, operands.k).map(_.toInt)
                _       <- Either.cond(
                  rows == resultCount.raw && columns > 0,
                  (),
                  Fault.InvalidRequest(s"cooperative vector product of M $rows and K $columns into $resultCount lanes")
                )
                inputKind   <- scalar(values, operands.inputKind).flatMap(interpretation(_))
                matrixKind  <- scalar(values, operands.matrixKind).flatMap(interpretation(_))
                lanes       <- bits(values, operands.input)
                elements    <- convertInput(lanes, inputCount, source, operands.operands, inputKind, columns)
                placements  <- placement(values, operands, rows, columns, matrixKind)
                base        <- address(values, operands.matrix)
                matrixStart <- scalar(values, operands.matrixOffset)
                weights     <- read(memory, base.address + matrixStart, base, placements, matrixKind)
                biases      <- operands.bias.fold[AddressSpace.ReadResult[Option[(Array[Long], Interpretation)]]](
                  Right(None)
                ) { (pointer, offset, kind) =>
                  for {
                    biasKind  <- scalar(values, kind).flatMap(interpretation(_))
                    biasBase  <- address(values, pointer)
                    biasStart <- scalar(values, offset)
                    raw       <- read(
                      memory,
                      biasBase.address + biasStart,
                      biasBase,
                      Array.tabulate(rows)(_ * biasKind.bytes.toLong),
                      biasKind
                    )
                  } yield Some((raw, biasKind))
                }
                sums <- accumulate(result, rows, columns, elements, inputKind, weights, matrixKind, biases)
              } yield values.update(dest, VMValue.Bits(resultType, sums))
            }
          case _ => Fail(Fault.InvalidRequest("cooperative vector products need numeric input and result components"))
        }
      case _ => Fail(Fault.InvalidRequest("cooperative vector products need cooperative vector input and result types"))
    }
  }

  /** The input's K elements in the input interpretation: packed interpretations reinterpret the bytes of 32-bit lanes,
    * lowest first, and the others convert each lane.
    */
  private def convertInput(
      lanes: Array[Long],
      count: Int,
      source: RuntimeType,
      operands: Long,
      kind: Interpretation,
      columns: Int
  ): Either[Fault, Array[Long]] =
    if (kind.packed) source match {
      case RuntimeType.Integer(width, _) if width == 32 && columns <= count * 4 =>
        Right(Array.tabulate(columns)(at => (lanes(at / 4) >>> ((at % 4) * 8)) & 0xffL))
      case _ => Left(Fault.InvalidRequest(s"packed cooperative vector input of $count lanes holds no $columns bytes"))
    }
    else if (count != columns) Left(Fault.InvalidRequest(s"cooperative vector input of $count lanes is not K $columns"))
    else source match {
      case RuntimeType.Floating(width)   => Right(lanes.map(lane => round(IEEE754.toDouble(lane, width), kind)))
      case RuntimeType.Integer(width, _) =>
        val bits   = width.widen[Int in 1 ~ 64]
        val signed = (operands & MatrixBSigned) != 0L
        Right(lanes.map { lane =>
          val value = if (signed) Bits.signExtend(lane, bits) else Bits.truncate(lane, bits)
          if (kind.floating) round(if (signed) value.toDouble else unsigned(value), kind)
          else IntegerArithmetic.clampToWidth(value, signed, kind.width, kind.signed)
        })
      case _ => Left(Fault.InvalidRequest("cooperative vector input components are not numeric"))
    }

  /** The byte offset of each matrix element, row-major over M rows of K. */
  private def placement(
      values: InvocationValues,
      operands: Product,
      rows: Int,
      columns: Int,
      kind: Interpretation
  ): Either[Fault, Array[Long]] = for {
    layout     <- scalar(values, operands.layout)
    transposed <- scalar(values, operands.transpose).map(_ != 0L)
    stride     <- operands.stride.fold[Either[Fault, Option[Long]]](Right(None))(scalar(values, _).map(Some(_)))
    offsets    <- matrixOffsets(layout, transposed, stride, rows, columns, kind.bytes.toLong)
  } yield offsets

  private def matrixOffsets(
      layout: Long,
      transposed: Boolean,
      stride: Option[Long],
      rows: Int,
      columns: Int,
      size: Long
  ): Either[Fault, Array[Long]] = {
    def offsets(at: (Int, Int) => Long) = Array.tabulate(rows * columns)(lane => at(lane / columns, lane % columns))
    (layout, stride) match {
      case (0L | 1L, _) if transposed =>
        Left(Fault.InvalidRequest("row- and column-major cooperative vector matrices cannot be transposed"))
      case (0L, Some(spacing))        => Right(offsets((row, column) => row * spacing + column * size))
      case (1L, Some(spacing))        => Right(offsets((row, column) => column * spacing + row * size))
      case (0L | 1L, None)            => Left(Fault.InvalidRequest("a row- or column-major matrix needs a stride"))
      case (2L | 3L, _) if transposed => Right(offsets((row, column) => (column.toLong * rows + row) * size))
      case (2L | 3L, _)               => Right(offsets((row, column) => (row.toLong * columns + column) * size))
      case _ => Left(Fault.InvalidRequest(s"cooperative vector matrix layout $layout is not defined"))
    }
  }

  private def read(
      memory: MemoryState,
      start: Long,
      base: Pointer,
      offsets: Array[Long],
      kind: Interpretation
  ): AddressSpace.ReadResult[Array[Long]] =
    offsets.indices.foldLeft[AddressSpace.ReadResult[Array[Long]]](Right(Array.ofDim[Long](offsets.length)))(
      (result, at) =>
        result.flatMap(read =>
          memory.readScalar(base.copy(address = start + offsets(at)), kind.bytes).map { value =>
            read(at) = value
            read
          }
        )
    )

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def accumulate(
      result: RuntimeType,
      rows: Int,
      columns: Int,
      input: Array[Long],
      inputKind: Interpretation,
      weights: Array[Long],
      matrixKind: Interpretation,
      biases: Option[(Array[Long], Interpretation)]
  ): Either[Fault, Array[Long]] = result match {
    case RuntimeType.Floating(width) =>
      val inputs = Array.tabulate(columns)(column => real(input(column), inputKind))
      Right(Array.tabulate(rows) { row =>
        var sum    = biases.fold(0.0)((raw, kind) => real(raw(row), kind))
        var column = 0
        while (column < columns) {
          sum += real(weights(row * columns + column), matrixKind) * inputs(column)
          column += 1
        }
        IEEE754.fromDouble(sum, width)
      })
    case RuntimeType.Integer(width, _)
        if !inputKind.floating && !matrixKind.floating && biases.forall(!_._2.floating) =>
      val bits   = width.widen[Int in 1 ~ 64]
      val inputs = Array.tabulate(columns)(column => integer(input(column), inputKind))
      Right(Array.tabulate(rows) { row =>
        var sum    = biases.fold(0L)((raw, kind) => integer(raw(row), kind))
        var column = 0
        while (column < columns) {
          sum += integer(weights(row * columns + column), matrixKind) * inputs(column)
          column += 1
        }
        Bits.truncate(sum, bits)
      })
    case _ => Left(Fault.InvalidRequest("an integer cooperative vector product needs integer interpretations"))
  }

  private case class Accumulation(
      pointer: Int,
      offset: Int,
      a: Int,
      b: Int,
      layout: Int,
      matrixKind: Int,
      stride: Option[Int]
  )

  private def outerProduct(site: Site, operands: Accumulation): Op = {
    import operands.*
    val program = site.program
    (program.runtimeType(program.valueType(a)), program.runtimeType(program.valueType(b))) match {
      case (Some(RuntimeType.VectorType(left, rows)), Some(RuntimeType.VectorType(right, columns))) =>
        (program.runtimeType(left), program.runtimeType(right)) match {
          case (Some(RuntimeType.Floating(width)), Some(RuntimeType.Floating(other))) if width == other =>
            step { (execution, memory) =>
              val values = execution.values
              for {
                kind <- scalar(values, matrixKind).flatMap(interpretation(_))
                _    <- Either.cond(
                  kind == Float16 || kind == Float32,
                  (),
                  Fault.Unsupported(s"cooperative vector outer products accumulate binary16 or binary32, not $kind")
                )
                arrangement <- scalar(values, layout)
                spacing     <- stride.fold[Either[Fault, Option[Long]]](Right(None))(scalar(values, _).map(Some(_)))
                offsets     <-
                  matrixOffsets(arrangement, transposed = false, spacing, rows, columns, kind.bytes.toLong)
                base  <- address(values, pointer)
                start <- scalar(values, offset)
                left  <- bits(values, a)
                right <- bits(values, b)
                _     <- offsets.indices.foldLeft[Either[Fault, Unit]](Right(()))((result, at) =>
                  result.flatMap { _ =>
                    val product = IEEE754.toDouble(left(at / columns), width) *
                      IEEE754.toDouble(right(at % columns), width)
                    memory.atomicModifyScalar(base.copy(address = base.address + start + offsets(at)), kind.bytes)(
                      previous => round(real(previous, kind) + product, kind)
                    ).map(_ => ())
                  }
                )
              } yield ()
            }
          case _ =>
            Fail(Fault.Unsupported("cooperative vector outer products need floating vectors of one component type"))
        }
      case _ => Fail(Fault.InvalidRequest("OpCooperativeVectorOuterProductAccumulateNV needs cooperative vectors"))
    }
  }

  private def unsigned(value: Long): Double =
    if (value >= 0L) value.toDouble else ((value >>> 1) | (value & 1L)).toDouble * 2.0

  private def real(raw: Long, kind: Interpretation): Double = kind match {
    case Float16          => IEEE754.toDouble(raw & 0xffffL, 16)
    case Float32          => IEEE754.toDouble(raw & 0xffffffffL, 32)
    case Float64          => IEEE754.toDouble(raw, 64)
    case E4M3             => Minifloat.E4M3.toDouble(raw)
    case E5M2             => Minifloat.E5M2.toDouble(raw)
    case _ if kind.signed => Bits.signExtend(raw, kind.width).toDouble
    case _                => unsigned(Bits.truncate(raw, kind.width))
  }

  private def integer(raw: Long, kind: Interpretation): Long =
    if (kind.signed) Bits.signExtend(raw, kind.width) else Bits.truncate(raw, kind.width)

  /** Rounds to nearest, ties to even, and saturates conversions to integers. */
  private def round(value: Double, kind: Interpretation): Long = kind match {
    case Float16 => IEEE754.fromDouble(value, 16)
    case Float32 => IEEE754.fromDouble(value, 32)
    case Float64 => IEEE754.fromDouble(value, 64)
    case E4M3    => Minifloat.E4M3.fromDouble(value, saturate = true)
    case E5M2    => Minifloat.E5M2.fromDouble(value)
    case _       => IEEE754.toIntegerSaturated(value, kind.width, kind.signed, IEEE754.Rounding.NearestEven)
  }
}
