package net.kurobako.gemstone.isa.spirv

import Op.*
import VMInvocation.*
import VMProgram.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*

/** SPV_NV_cooperative_vector. A cooperative vector is an ordinary vector of its invocation, and matrices and biases
  * stay in memory. The optimal layouts, which only a driver's matrix conversion writes, are tightly packed row-major,
  * so a transposed M by K matrix is stored as K rows of M elements. Products are in [[FragmentSemantics]].
  */
private object CooperativeVectorOps {
  import Operand.*
  import VectorKinds.*
  import VectorKinds.Interpretation.*

  private def advancing(execution: InvocationState)(outcome: => Flow[Unit]): Flow[Unit] =
    outcome.map(_ => execution.pc += 1)

  /** Runs the cooperative vector operation `instruction` names. */
  def execute(execution: InvocationState, instruction: Instruction): Flow[Unit] = {
    val program = execution.values.program
    instruction match {
      case Instruction.CooperativeVectorLoadNV(t, d, pointer, offset, _) =>
        vector(program, id(t), "OpCooperativeVectorLoadNV").flatMap { (_, count, width) =>
          advancing(execution) {
            val memory = execution.memory
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
        vector(program, program.valueType(id(source)), "OpCooperativeVectorStoreNV").flatMap { (_, count, width) =>
          advancing(execution) {
            val memory = execution.memory
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
      case Instruction.CooperativeVectorOuterProductAccumulateNV(pointer, offset, a, b, layout, matrixKind, stride) =>
        outerProduct(
          execution,
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
        vector(program, program.valueType(id(source)), "OpCooperativeVectorReduceSumAccumulateNV").flatMap {
          (component, count, width) =>
            val add: (Long, Long) => Long = program.runtimeType(component) match {
              case Some(RuntimeType.Floating(bits)) => IEEE754.add(_, _, bits, IEEE754.NaNOperand.Left)
              case _                                => _ + _
            }
            advancing(execution) {
              val memory = execution.memory
              val values = execution.values
              for {
                base  <- address(values, id(pointer))
                start <- scalar(values, id(offset))
                lanes <- bits(values, id(source))
                _     <- (0 until count).foldLeft[Either[Fault, Unit]](Right(()))((result, lane) =>
                  result.flatMap(_ =>
                    memory.atomicModifyScalar(base.copy(address = base.address + start + lane * width), width)(
                      previous =>
                        add(previous, lanes(lane))
                    ).map(_ => ())
                  )
                )
              } yield ()
            }
        }
      case _ => Left(Fault.Unsupported(s"${instruction.opcode} is not a cooperative vector operation"))
    }
  }

  def vector(program: VMProgram, typeId: Int, opcode: String): Either[Fault, (Int, Int > 0, Int in 1 ~ 8)] =
    program.runtimeType(typeId) match {
      case Some(RuntimeType.VectorType(component, count)) =>
        program.scalarMemoryWidth(component).map(width => (component, count, width))
      case _ => Left(Fault.InvalidRequest(s"$opcode type %$typeId is not a cooperative vector"))
    }

  def products(program: VMProgram, resultType: Int, input: Int)
      : Either[Fault, (RuntimeType, Int > 0, RuntimeType, Int > 0)] =
    (program.runtimeType(resultType), program.runtimeType(program.valueType(input))) match {
      case (
            Some(RuntimeType.VectorType(resultComponent, resultCount)),
            Some(RuntimeType.VectorType(inputComponent, inputCount))
          ) =>
        (program.runtimeType(resultComponent), program.runtimeType(inputComponent)) match {
          case (Some(result @ (RuntimeType.Floating(_) | RuntimeType.Integer(_, _))), Some(source)) =>
            Right((result, resultCount, source, inputCount))
          case _ => Left(Fault.InvalidRequest("cooperative vector products need numeric input and result components"))
        }
      case _ => Left(Fault.InvalidRequest("cooperative vector products need cooperative vector input and result types"))
    }

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

  private case class Accumulation(
      pointer: Int,
      offset: Int,
      a: Int,
      b: Int,
      layout: Int,
      matrixKind: Int,
      stride: Option[Int]
  )

  def outers(program: VMProgram, a: Int, b: Int): Either[Fault, (Int > 0, Int > 0, Int in (16, 32, 64))] =
    (program.runtimeType(program.valueType(a)), program.runtimeType(program.valueType(b))) match {
      case (Some(RuntimeType.VectorType(left, rows)), Some(RuntimeType.VectorType(right, columns))) =>
        (program.runtimeType(left), program.runtimeType(right)) match {
          case (Some(RuntimeType.Floating(width)), Some(RuntimeType.Floating(other))) if width == other =>
            Right((rows, columns, width))
          case _ =>
            Left(Fault.Unsupported("cooperative vector outer products need floating vectors of one component type"))
        }
      case _ => Left(Fault.InvalidRequest("OpCooperativeVectorOuterProductAccumulateNV needs cooperative vectors"))
    }

  private def outerProduct(execution: InvocationState, operands: Accumulation): Flow[Unit] = {
    import operands.*
    outers(execution.values.program, a, b).flatMap { (rows, columns, width) =>
      advancing(execution) {
        val memory = execution.memory
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
              memory.atomicModifyScalar(base.copy(address = base.address + start + offsets(at)), kind.bytes)(previous =>
                round(real(previous, kind) + product, kind)
              ).map(_ => ())
            }
          )
        } yield ()
      }
    }
  }
}
