package net.kurobako.gemstone.isa.spirv

import Op.*
import OpBuilder.*
import VMInvocation.*
import VMMemory.*
import VMProgram.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*

/** SPV_KHR_cooperative_matrix and SPV_NV_cooperative_matrix. Every invocation of a matrix's scope holds the whole
  * matrix, so loads, stores and products need no rendezvous: invocations that load, combine and store the same matrices
  * write the same values, and a matrix's length is its component count. Strides count components in kernels, as Intel's
  * compilers read them through byte pointers, and elements of the pointee in shaders.
  */
private object CooperativeMatrixOps {
  import Operand.*

  def lower(site: Site): PartialFunction[Instruction, Op] = {
    case Instruction.CooperativeMatrixLengthKHR(t, d, typeOrValue) => length(site, id(t), id(d), id(typeOrValue))
    case Instruction.CooperativeMatrixLengthNV(t, d, typeOrValue)  => length(site, id(t), id(d), id(typeOrValue))
    case Instruction.CooperativeMatrixLoadKHR(t, d, pointer, layout, stride, _) =>
      matrix(site, id(t), "OpCooperativeMatrixLoadKHR")(shape =>
        load(site, id(t), id(d), shape, id(pointer), Left(id(layout)), stride.map(id(_)))
      )
    case Instruction.CooperativeMatrixLoadNV(t, d, pointer, stride, columnMajor, _) =>
      matrix(site, id(t), "OpCooperativeMatrixLoadNV")(shape =>
        load(site, id(t), id(d), shape, id(pointer), Right(id(columnMajor)), Some(id(stride)))
      )
    case Instruction.CooperativeMatrixStoreKHR(pointer, source, layout, stride, _) =>
      matrix(site, site.program.valueType(id(source)), "OpCooperativeMatrixStoreKHR")(shape =>
        store(site, shape, id(pointer), id(source), Left(id(layout)), stride.map(id(_)))
      )
    case Instruction.CooperativeMatrixStoreNV(pointer, source, stride, columnMajor, _) =>
      matrix(site, site.program.valueType(id(source)), "OpCooperativeMatrixStoreNV")(shape =>
        store(site, shape, id(pointer), id(source), Right(id(columnMajor)), Some(id(stride)))
      )
    // SPV_NV_cooperative_matrix2's use conversion keeps every component in place; its transposition swaps rows and
    // columns
    case Instruction.CooperativeMatrixConvertNV(t, d, source) =>
      matrix(site, id(t), "OpCooperativeMatrixConvertNV")(_ =>
        step((execution, _) => transfer(execution.values, id(source), id(d), id(t)))
      )
    case Instruction.CooperativeMatrixTransposeNV(t, d, source) =>
      matrix(site, site.program.valueType(id(source)), "OpCooperativeMatrixTransposeNV")(shape =>
        step((execution, _) =>
          bits(execution.values, id(source)).map(lanes =>
            execution.values.update(
              id(d),
              VMValue.Bits(
                id(t),
                Array.tabulate(lanes.length)(lane => lanes((lane % shape.rows) * shape.columns + lane / shape.rows))
              )
            )
          )
        )
      )
    case Instruction.BitCastArrayQCOM(t, d, source) =>
      val program = site.program
      step((execution, _) =>
        execution.values.get(id(source)).toRight(undefined(id(source))).flatMap(program.encode(_))
          .flatMap(program.decode(id(t), _)).map(execution.values.update(id(d), _))
      )
    case Instruction.ExtractSubArrayQCOM(t, d, source, index) =>
      lowered(site.program.scalarLeafCount(id(t)).map { count =>
        step { (execution, _) =>
          val values = execution.values
          for {
            lanes <- bits(values, id(source))
            start <- scalar(values, id(index)).map(_.toInt)
            _     <- Either.cond(
              start >= 0 && start + count <= lanes.length,
              (),
              Fault.InvalidRequest(s"OpExtractSubArrayQCOM range $start + $count exceeds ${lanes.length} elements")
            )
          } yield values.update(id(d), VMValue.Bits(id(t), lanes.slice(start, start + count)))
        }
      })
    case Instruction.CompositeExtractCoopMatQCOM(t, d, source) =>
      matrix(site, site.program.valueType(id(source)), "OpCompositeExtractCoopMatQCOM")(shape =>
        lowered(lines(site, shape, id(t)).map { (count, componentBits, elementBits) =>
          step { (execution, _) =>
            bits(execution.values, id(source)).map { lanes =>
              val lane = execution.invocation.subgroupLocal
              val line = Array.tabulate(lineLength(shape))(at =>
                if (lane >= lineCount(shape)) 0L
                else lanes(if (shape.use == 1L) at * shape.columns + lane else lane * shape.columns + at)
              )
              execution.values.update(id(d), VMValue.Bits(id(t), packLine(line, count, componentBits, elementBits)))
            }
          }
        })
      )
    case Instruction.CompositeConstructCoopMatQCOM(t, d, source) =>
      matrix(site, id(t), "OpCompositeConstructCoopMatQCOM")(shape =>
        lowered(lines(site, shape, site.program.valueType(id(source))).map { (_, componentBits, elementBits) =>
          Dynamic { (execution, _) =>
            bits(execution.values, id(source)).flatMap(lanes =>
              suspend(
                execution,
                VMCollectives.Request(
                  VMCollectives.Kind.MatrixRows,
                  _,
                  _,
                  Scope.Subgroup,
                  VMCollectives.Payload.Value(id(t), id(d), VMValue.Bits(id(t), lanes)),
                  selector =
                    Vec(shape.rows.toLong, shape.columns.toLong, shape.use, componentBits.toLong, elementBits.toLong)
                )
              )
            )
          }
        })
      )
    case instruction @ Instruction.CooperativeMatrixMulAddKHR(t, _, a, b, c, _) => mulAdd(site, instruction, t, a, b, c)
    case instruction @ Instruction.CooperativeMatrixMulAddNV(t, _, a, b, c)     => mulAdd(site, instruction, t, a, b, c)
  }

  /** The rows of an A or accumulator matrix, or columns of a B matrix, that SPV_QCOM_cooperative_matrix_conversion
    * gives one lane each.
    */
  def lineCount(shape: CooperativeMatrix): Int = if (shape.use == 1L) shape.columns else shape.rows

  def lineLength(shape: CooperativeMatrix): Int = if (shape.use == 1L) shape.rows else shape.columns

  /** The array's length and the bits of a matrix component and of an array element, which packs whole components. */
  private def lines(site: Site, shape: CooperativeMatrix, array: Int): Either[Fault, (Int, Int, Int)] = {
    val program = site.program
    (program.runtimeType(array), program.scalarMemoryWidth(shape.component)) match {
      case (Some(RuntimeType.Array(element, count)), Right(component)) =>
        program.scalarMemoryWidth(element).flatMap { bytes =>
          val (componentBits, elementBits) = (component * 8, bytes * 8)
          Either.cond(
            elementBits % componentBits == 0 && count.raw * (elementBits / componentBits) == lineLength(shape),
            (count.toInt, componentBits, elementBits),
            Fault.InvalidRequest(s"array %$array does not hold one line of cooperative matrix %${shape.component}")
          )
        }
      case _ => Left(Fault.InvalidRequest(s"%$array is not an array of a cooperative matrix's line"))
    }
  }

  /** A line's components packed, lowest first, into `count` array elements. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def packLine(line: Array[Long], count: Int, componentBits: Int, elementBits: Int): Array[Long] = {
    val each = elementBits / componentBits
    val mask = if (componentBits == 64) -1L else (1L << componentBits) - 1L
    Array.tabulate(count) { element =>
      var word = 0L
      var part = 0
      while (part < each) {
        word |= (line(element * each + part) & mask) << (part * componentBits)
        part += 1
      }
      word
    }
  }

  def unpackLine(elements: Array[Long], length: Int, componentBits: Int, elementBits: Int): Array[Long] = {
    val each = elementBits / componentBits
    val mask = if (componentBits == 64) -1L else (1L << componentBits) - 1L
    Array.tabulate(length)(at => (elements(at / each) >>> ((at % each) * componentBits)) & mask)
  }

  private def matrix(site: Site, typeId: Int, opcode: String)(op: CooperativeMatrix => Op): Op =
    site.program.cooperativeMatrices.get(typeId)
      .fold(Fail(Fault.InvalidRequest(s"$opcode type %$typeId is not a cooperative matrix")))(op)

  // OpCooperativeMatrixLengthKHR names a type; Intel's compilers also pass a matrix value
  private def length(site: Site, resultType: Int, dest: Int, operand: Int): Op = {
    val program = site.program
    program.cooperativeMatrices.get(operand).orElse(program.cooperativeMatrices.get(program.valueType(operand))).fold(
      Fail(Fault.InvalidRequest(s"cooperative matrix length operand %$operand is not a cooperative matrix"))
    ) { shape =>
      val count = (shape.rows * shape.columns).toLong
      step((execution, _) => Right(execution.values.updateScalar(dest, resultType, count)))
    }
  }

  /** The component width in bytes, and the bytes a stride counts. */
  private def units(site: Site, shape: CooperativeMatrix, pointer: Int): Either[Fault, (Int in 1 ~ 8, Long)] = {
    val program = site.program
    program.scalarMemoryWidth(shape.component).map { width =>
      val pointee = program.runtimeType(program.valueType(pointer)) match {
        case Some(RuntimeType.Pointer(_, element)) if program.vulkan => program.typeSize(element).toLong
        case _                                                       => width.toLong
      }
      (width, if (pointee > 0L) pointee else width.toLong)
    }
  }

  /** The byte offset of each component, row-major, from the layout and stride ids, each read by `read`. */
  private inline def offsets(
      shape: CooperativeMatrix,
      layout: Either[Int, Int],
      stride: Option[Int],
      width: Int,
      unit: Long
  )(inline read: Int => Either[Fault, Long]): Either[Fault, Array[Long]] = for {
    columnMajor <- layout.fold(
      id =>
        read(id).flatMap(value =>
          Either.cond(
            value == 0L || value == 1L,
            value == 1L,
            Fault.Unsupported(s"cooperative matrix layout $value is not RowMajorKHR or ColumnMajorKHR")
          )
        ),
      id => read(id).map(_ != 0L)
    )
    spacing <- stride.fold[Either[Fault, Long]](Right((if (columnMajor) shape.rows else shape.columns).toLong))(read)
  } yield Array.tabulate(shape.rows * shape.columns) { lane =>
    val (row, column)  = (lane / shape.columns, lane % shape.columns)
    val (major, minor) = if (columnMajor) (column, row) else (row, column)
    major * spacing * unit + minor.toLong * width
  }

  /** The component offsets, fixed as the site lowers where its layout and stride are constants, else read as the
    * instruction runs.
    */
  private def placement(
      site: Site,
      shape: CooperativeMatrix,
      layout: Either[Int, Int],
      stride: Option[Int],
      width: Int,
      unit: Long
  ): InvocationValues => Either[Fault, Array[Long]] = {
    val constants = site.program.constants
    offsets(shape, layout, stride, width, unit)(id =>
      constants.get(id).collect { case VMValue.Bits(_, Array(value)) => value }.toRight(undefined(id))
    ).fold(
      _ => values => offsets(shape, layout, stride, width, unit)(scalar(values, _)),
      places => _ => Right(places)
    )
  }

  private def load(
      site: Site,
      resultType: Int,
      dest: Int,
      shape: CooperativeMatrix,
      pointer: Int,
      layout: Either[Int, Int],
      stride: Option[Int]
  ): Op = lowered(units(site, shape, pointer).map { (width, unit) =>
    val place = placement(site, shape, layout, stride, width, unit)
    step { (execution, memory) =>
      val values = execution.values
      for {
        base   <- address(values, pointer)
        places <- place(values)
        lanes  <- read(memory, base, places, width)
      } yield values.update(dest, VMValue.Bits(resultType, lanes))
    }
  })

  @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
  private def read(memory: MemoryState, base: Pointer, places: Array[Long], width: Int in 1 ~ 8)
      : AddressSpace.ReadResult[Array[Long]] = {
    val lanes                                             = Array.ofDim[Long](places.length)
    var failure: Fault | AddressSpace.Pending.type | Null = null
    var lane                                              = 0
    while (lane < lanes.length && (failure eq null)) {
      memory.readScalar(base.copy(address = base.address + places(lane)), width) match {
        case Left(error)  => failure = error
        case Right(value) => lanes(lane) = value
      }
      lane += 1
    }
    if (failure ne null) Left(failure) else Right(lanes)
  }

  private def store(
      site: Site,
      shape: CooperativeMatrix,
      pointer: Int,
      source: Int,
      layout: Either[Int, Int],
      stride: Option[Int]
  ): Op = lowered(units(site, shape, pointer).map { (width, unit) =>
    val place = placement(site, shape, layout, stride, width, unit)
    step { (execution, memory) =>
      val values = execution.values
      for {
        base   <- address(values, pointer)
        lanes  <- bits(values, source)
        places <- place(values)
        _      <- write(memory, base, places, lanes, width)
      } yield ()
    }
  })

  @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
  private def write(memory: MemoryState, base: Pointer, places: Array[Long], lanes: Array[Long], width: Int in 1 ~ 8)
      : Either[Fault, Unit] = {
    var failure: Fault | Null = null
    var lane                  = 0
    while (lane < places.length && failure == null) {
      memory.writeScalar(base.copy(address = base.address + places(lane)), lanes(lane), width) match {
        case Left(error) => failure = error
        case Right(())   => ()
      }
      lane += 1
    }
    if (failure != null) Left(failure) else Right(())
  }

  private def mulAdd(site: Site, instruction: Instruction, resultType: Operand, a: Operand, b: Operand, c: Operand)
      : Op = {
    val matrices = site.program.cooperativeMatrices
    val shapes   = Vec(a, b, c).map(operand => matrices.get(site.program.valueType(id(operand))))
    (shapes, matrices.get(id(resultType))) match {
      case (Vec(Some(left), Some(right), Some(addend)), Some(result))
          if left.columns == right.rows && left.rows == result.rows && right.columns == result.columns &&
            addend.rows == result.rows && addend.columns == result.columns &&
            (result.nv || (left.use == 0L && right.use == 1L && addend.use == 2L && result.use == 2L)) =>
        staged(site, instruction, CoreFamily.CooperativeMatrix)
      case _ => Fail(Fault.InvalidRequest(
          s"${instruction.opcode} needs M by K, K by N and M by N matrices of matching uses and an M by N result"
        ))
    }
  }
}
