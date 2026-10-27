package net.kurobako.gemstone.isa.spirv

import Op.*
import OpBuilder.*
import VMInvocation.*
import VMMemory.*
import VMProgram.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.vm.*

/** SPV_KHR_cooperative_matrix and SPV_NV_cooperative_matrix. Every invocation of a matrix's scope holds the whole
  * matrix, so loads, stores and products need no rendezvous: invocations that load, combine and store the same matrices
  * write the same values, and a matrix's length is its component count. Strides count components in kernels, as Intel's
  * compilers read them through byte pointers, and elements of the pointee in shaders.
  */
private object CooperativeMatrixOps {
  import Operand.*

  def lower(using site: Site): PartialFunction[Instruction, Op] = {
    case Instruction.CooperativeMatrixLengthKHR(t, d, typeOrValue)          => length(ResultIds(t, d), id(typeOrValue))
    case Instruction.CooperativeMatrixLengthNV(t, d, typeOrValue)           => length(ResultIds(t, d), id(typeOrValue))
    case instruction @ Instruction.CooperativeMatrixLoadKHR(resultType = t) =>
      access(instruction, id(t))
    case instruction @ Instruction.CooperativeMatrixLoadNV(resultType = t) =>
      access(instruction, id(t))
    case instruction @ Instruction.CooperativeMatrixStoreKHR(src = source) =>
      access(instruction, site.program.valueType(id(source)))
    case instruction @ Instruction.CooperativeMatrixStoreNV(src = source) =>
      access(instruction, site.program.valueType(id(source)))
    case Instruction.CooperativeMatrixConvertNV(t, d, source) =>
      matrix(id(t), "OpCooperativeMatrixConvertNV")(_ => CopyOp(id(source), id(d), id(t)))
    case Instruction.CooperativeMatrixTransposeNV(t, d, source) =>
      matrix(site.program.valueType(id(source)), "OpCooperativeMatrixTransposeNV")(shape =>
        TransposeOp(id(source), rows = shape.columns, columns = shape.rows, id(d), id(t))
      )
    case Instruction.BitCastArrayQCOM(t, d, source) =>
      lowered(for {
        origin <- scalarArray(site.program, site.program.valueType(id(source)))
        result <- scalarArray(site.program, id(t))
      } yield BitCastArray(id(source), origin._1, origin._2, id(d), result._1, result._2, id(t)))
    case Instruction.ExtractSubArrayQCOM(t, d, source, index) =>
      lowered(site.program.scalarLeafCount(id(t)).map(_ => SubArrayExtract(id(source), id(index), id(d), id(t))))
    case Instruction.CompositeExtractCoopMatQCOM(t, d, source) =>
      matrix(site.program.valueType(id(source)), "OpCompositeExtractCoopMatQCOM")(shape =>
        lowered(lines(shape, id(t)).map { (count, componentBits, elementBits) =>
          LineExtract(id(source), shape, count, componentBits, elementBits, id(d), id(t))
        })
      )
    case Instruction.CompositeConstructCoopMatQCOM(t, d, source) =>
      matrix(id(t), "OpCompositeConstructCoopMatQCOM")(shape =>
        lowered(lines(shape, site.program.valueType(id(source))).map { (_, componentBits, elementBits) =>
          LineConstruct(
            id(source),
            id(d),
            id(t),
            Vec(shape.rows.toLong, shape.columns.toLong, shape.use, componentBits.toLong, elementBits.toLong)
          )
        })
      )
    case Instruction.CooperativeMatrixPerElementOpNV(t, d, source, function, operands) =>
      perElement(ResultIds(t, d), id(source), id(function), operands.map(id(_)))
    case Instruction.CooperativeMatrixReduceNV(t, d, source, reduce, combine) =>
      reduction(ResultIds(t, d), id(source), unsigned(reduce), id(combine))
    case instruction @ Instruction.CooperativeMatrixMulAddKHR(t, _, a, b, c, _) => mulAdd(instruction, t, a, b, c)
    case instruction @ Instruction.CooperativeMatrixMulAddNV(t, _, a, b, c)     => mulAdd(instruction, t, a, b, c)
  }

  private def scalarArray(program: VMProgram, array: Int): Either[Fault, (Int, Int in 1 ~ 8)] =
    program.runtimeType(array) match {
      case Some(RuntimeType.Array(element, count)) => program.scalarMemoryWidth(element).map((count.toInt, _))
      case _                                       => Left(Fault.InvalidRequest(s"%$array is not an array of scalars"))
    }

  private class BitCastArray(
      source: Int,
      have: Int,
      from: Int in 1 ~ 8,
      target: Int,
      count: Int,
      to: Int in 1 ~ 8,
      typeId: Int
  ) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      CompositeSemantics.bitCast(Execute, InvocationMachine(execution))(source, have, from, target, count, to, typeId)
  }

  private class SubArrayExtract(source: Int, index: Int, target: Int, typeId: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      CompositeSemantics.subArray(InvocationMachine(execution))(source, index, target, typeId)
  }

  private class LineExtract(
      source: Int,
      shape: CooperativeMatrix,
      count: Int,
      componentBits: Int,
      elementBits: Int,
      target: Int,
      typeId: Int
  ) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      CooperativeSemantics.ownLine(Execute, InvocationMachine(execution))(
        source,
        shape.rows,
        shape.columns,
        shape.use.toInt,
        componentBits,
        elementBits,
        count,
        target,
        typeId
      )
  }

  private class LineConstruct(source: Int, dest: Int, resultType: Int, selector: Vec[Long]) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      CooperativeSemantics.construct(InvocationMachine(execution))(source, dest, resultType, selector)
  }

  /** The rows of an A or accumulator matrix, or columns of a B matrix, that SPV_QCOM_cooperative_matrix_conversion
    * gives one lane each.
    */
  def lineCount(shape: CooperativeMatrix): Int = if (shape.use == 1L) shape.columns else shape.rows

  def lineLength(shape: CooperativeMatrix): Int = if (shape.use == 1L) shape.rows else shape.columns

  /** The array's length and the bits of a matrix component and of an array element, which packs whole components. */
  private def lines(shape: CooperativeMatrix, array: Int)(using site: Site): Either[Fault, (Int, Int, Int)] = {
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

  def unpackLine(elements: Array[Long], length: Int, componentBits: Int, elementBits: Int): Array[Long] = {
    val each = elementBits / componentBits
    val mask = if (componentBits == 64) -1L else (1L << componentBits) - 1L
    Array.tabulate(length)(at => (elements(at / each) >>> ((at % each) * componentBits)) & mask)
  }

  private def matrix(typeId: Int, opcode: String)(using site: Site)(op: CooperativeMatrix => Op): Op =
    site.program.cooperativeMatrices.get(typeId)
      .fold(Fail(Fault.InvalidRequest(s"$opcode type %$typeId is not a cooperative matrix")))(op)

  private def access(instruction: Instruction, typeId: Int)(using site: Site): Op =
    matrix(typeId, instruction.opcode.raw)(shape =>
      lowered(site.program.scalarMemoryWidth(shape.component).map(MatrixOp(instruction, shape.rows, shape.columns, _)))
    )

  // OpCooperativeMatrixLengthKHR names a type; Intel's compilers also pass a matrix value
  private def length(ids: ResultIds, operand: Int)(using site: Site): Op = {
    val program = site.program
    program.cooperativeMatrices.get(operand).orElse(program.cooperativeMatrices.get(program.valueType(operand))).fold(
      Fail(Fault.InvalidRequest(s"cooperative matrix length operand %$operand is not a cooperative matrix"))
    )(shape => LengthOp((shape.rows * shape.columns).toLong, ids.dest, ids.resultType))
  }

  private class LengthOp(count: Long, dest: Int, resultType: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      CooperativeSemantics.length(InvocationMachine(execution))(count, dest, resultType)
  }

  private class CopyOp(source: Int, dest: Int, resultType: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      CompositeSemantics.copy(InvocationMachine(execution))(source, dest, resultType)
  }

  private class TransposeOp(source: Int, rows: Int, columns: Int, dest: Int, resultType: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      MatrixSemantics.transpose(InvocationMachine(execution))(source, rows, columns, dest, resultType)
  }

  private class MatrixOp(instruction: Instruction, rows: Int, columns: Int, width: Int in 1 ~ 8) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      CooperativeSemantics.matrix(Execute, InvocationMachine(execution))(instruction, rows, columns, width)
  }

  private def returns(function: Function, component: Int)(using site: Site): Boolean =
    site.program.module.instructions(function.start) match {
      case Instruction.Function(resultType = resultType) => id(resultType) == component
      case _                                             => false
    }

  private def perElement(
      ids: ResultIds,
      source: Int,
      function: Int,
      operands: Vec[Int]
  )(using site: Site): Op = {
    val program  = site.program
    val matrices = program.cooperativeMatrices
    (matrices.get(program.valueType(source)), matrices.get(ids.resultType), program.functions.get(function)) match {
      case (Some(shape), Some(result), Some(callee))
          if shape == result && callee.parameters.size == 3 + operands.size && returns(callee, shape.component) =>
        val same    = Vec.tabulate(operands.size)(at => matrices.get(program.valueType(operands(at))).contains(shape))
        val fixed   = Array.tabulate(3 + operands.size)(at => if (at < 3 || same(at - 3)) -1 else operands(at - 3))
        val sources = source +: operands.indices.filter(same(_)).map(operands(_))
        PerElementOp(
          Sweep(callee, shape.component, fixed, shape.rows * shape.columns),
          shape,
          source,
          operands.toArray,
          sources.toArray,
          ids.dest,
          ids.resultType
        )
      case _ => Fail(Fault.InvalidRequest(
          s"OpCooperativeMatrixPerElementOpNV needs a matrix of its result type and a function of %$function " +
            "returning its component that takes its row, column and component, then the operands"
        ))
    }
  }

  private class PerElementOp(
      sweep: Sweep,
      shape: CooperativeMatrix,
      source: Int,
      operands: Array[Int],
      sources: Array[Int],
      dest: Int,
      resultType: Int
  ) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      CooperativeSemantics.perElement(Execute, InvocationMachine(execution))(
        sweep,
        shape.rows,
        shape.columns,
        source,
        operands,
        sources,
        dest,
        resultType
      )
  }

  private def reduction(ids: ResultIds, source: Int, mask: Long, combine: Int)(using site: Site): Op = {
    val program  = site.program
    val matrices = program.cooperativeMatrices
    (matrices.get(program.valueType(source)), matrices.get(ids.resultType), program.functions.get(combine)) match {
      case (Some(shape), Some(result), Some(callee))
          if result.component == shape.component && result.use == shape.use && callee.parameters.size == 2 &&
            returns(callee, shape.component) && aligned(shape, result, mask) =>
        Reduction.of(shape.rows, shape.columns, mask, result.rows * result.columns) match {
          case Left(message)    => Fail(Fault.InvalidRequest(s"OpCooperativeMatrixReduceNV $message"))
          case Right(reduction) =>
            ReduceOp(
              Sweep(callee, shape.component, Array(-1, -1), reduction.calls),
              reduction,
              source,
              ids.dest,
              ids.resultType
            )
        }
      case _ => Fail(Fault.InvalidRequest(
          s"OpCooperativeMatrixReduceNV needs matrices of one component and a function of %$combine " +
            "combining two of them"
        ))
    }
  }

  private def aligned(shape: CooperativeMatrix, result: CooperativeMatrix, mask: Long): Boolean = mask match {
    case Reduction.Row    => result.rows == shape.rows
    case Reduction.Column => result.columns == shape.columns
    case _                => true
  }

  private class ReduceOp(sweep: Sweep, reduction: Reduction, source: Int, dest: Int, resultType: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      CooperativeSemantics.reduce(InvocationMachine(execution))(sweep, reduction, source, dest, resultType)
  }

  private def mulAdd(instruction: Instruction, resultType: Operand, a: Operand, b: Operand, c: Operand)(using
      site: Site
  )
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
