package net.kurobako.gemstone.isa.spirv

import LaneOps.*
import Op.*
import OpBuilder.*
import VMProgram.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.vm.*

/** Integer and floating-point arithmetic, bitwise operations, shifts, bit fields and the linear algebra opcodes. Each
  * family pairs a shape rule, checked once at load, with a lane kernel.
  */
// result type, site and operand ids are the raw operands of each opcode; a holder would allocate per instruction
@SuppressWarnings(Array("baumkuchen:data-clump"))
private object ArithmeticOps {
  import Operand.*

  def lower(site: Site): PartialFunction[Instruction, Op] = {
    case instruction @ Instruction.IAdd(_, _, a, b) =>
      wrapping(site, a, b)(staged(site, instruction, CoreFamily.Integer))
    case instruction @ Instruction.ISub(_, _, a, b) =>
      wrapping(site, a, b)(staged(site, instruction, CoreFamily.Integer))
    case instruction @ Instruction.IMul(_, _, a, b) =>
      wrapping(site, a, b)(staged(site, instruction, CoreFamily.Integer))
    case instruction @ Instruction.UDiv(t, _, a, b) =>
      division(site, "OpUDiv", t, a, b)(staged(site, instruction, CoreFamily.Integer))
    case instruction @ Instruction.UMod(t, _, a, b) =>
      division(site, "OpUMod", t, a, b)(staged(site, instruction, CoreFamily.Integer))
    case instruction @ Instruction.SDiv(t, _, a, b) =>
      division(site, "OpSDiv", t, a, b)(staged(site, instruction, CoreFamily.Integer))
    case instruction @ Instruction.SRem(t, _, a, b) =>
      division(site, "OpSRem", t, a, b)(staged(site, instruction, CoreFamily.Integer))
    case instruction @ Instruction.SMod(t, _, a, b) =>
      division(site, "OpSMod", t, a, b)(staged(site, instruction, CoreFamily.Integer))
    case instruction @ Instruction.SNegate(t, _, a) => negate(site, t, a)(staged(site, instruction, CoreFamily.Integer))
    case instruction @ Instruction.ShiftLeftLogical(t, _, a, b) =>
      shift(site, "OpShiftLeftLogical", t, a, b)(staged(site, instruction, CoreFamily.Integer))
    case instruction @ Instruction.ShiftRightLogical(t, _, a, b) =>
      shift(site, "OpShiftRightLogical", t, a, b)(staged(site, instruction, CoreFamily.Integer))
    case instruction @ Instruction.ShiftRightArithmetic(t, _, a, b) =>
      shift(site, "OpShiftRightArithmetic", t, a, b)(staged(site, instruction, CoreFamily.Integer))
    case instruction @ Instruction.BitwiseOr(t, _, a, b) =>
      bitwiseOperands(site, "OpBitwiseOr", t, a, b)(_ => staged(site, instruction, CoreFamily.Integer))
    case instruction @ Instruction.BitwiseXor(t, _, a, b) =>
      bitwiseOperands(site, "OpBitwiseXor", t, a, b)(_ => staged(site, instruction, CoreFamily.Integer))
    case instruction @ Instruction.BitwiseAnd(t, _, a, b) =>
      bitwiseOperands(site, "OpBitwiseAnd", t, a, b)(_ => staged(site, instruction, CoreFamily.Integer))
    case instruction @ Instruction.Not(t, _, a)      => not(site, t, a)(staged(site, instruction, CoreFamily.Integer))
    case instruction @ Instruction.BitCount(t, _, a) =>
      bitCount(site, t, a)(staged(site, instruction, CoreFamily.Integer))
    case instruction @ Instruction.BitReverse(t, _, a) =>
      bitField(site, t, a, None)(staged(site, instruction, CoreFamily.Integer))
    case instruction @ Instruction.BitFieldUExtract(resultType = t, base = base) =>
      bitField(site, t, base, None)(staged(site, instruction, CoreFamily.BitField))
    case instruction @ Instruction.BitFieldSExtract(resultType = t, base = base) =>
      bitField(site, t, base, None)(staged(site, instruction, CoreFamily.BitField))
    case instruction @ Instruction.BitFieldInsert(resultType = t, base = base, insert = insert) =>
      bitField(site, t, base, Some(insert))(staged(site, instruction, CoreFamily.BitField))
    case instruction @ Instruction.IAddCarry(t, _, a, b)    => extended(site, instruction, t, a, b)
    case instruction @ Instruction.ISubBorrow(t, _, a, b)   => extended(site, instruction, t, a, b)
    case instruction @ Instruction.UMulExtended(t, _, a, b) => extended(site, instruction, t, a, b)
    case instruction @ Instruction.SMulExtended(t, _, a, b) => extended(site, instruction, t, a, b)
    // binary16 operands are exact in binary32, and binary32 holds 2p + 2 bits of a binary16 result, so the basic
    // operations round once into binary16; remainders are exact.
    case instruction @ Instruction.FAdd(t, _, a, b) =>
      floating(site, "OpFAdd", t, a, b)(staged(site, instruction, CoreFamily.Floating))
    case instruction @ Instruction.FSub(t, _, a, b) =>
      floating(site, "OpFSub", t, a, b)(staged(site, instruction, CoreFamily.Floating))
    case instruction @ Instruction.FMul(t, _, a, b) =>
      floating(site, "OpFMul", t, a, b)(staged(site, instruction, CoreFamily.Floating))
    case instruction @ Instruction.FDiv(t, _, a, b) =>
      floating(site, "OpFDiv", t, a, b)(staged(site, instruction, CoreFamily.Floating))
    case instruction @ Instruction.FRem(t, _, a, b) =>
      floating(site, "OpFRem", t, a, b)(staged(site, instruction, CoreFamily.Floating))
    case instruction @ Instruction.FMod(t, _, a, b) =>
      floating(site, "OpFMod", t, a, b)(staged(site, instruction, CoreFamily.Floating))
    case instruction @ Instruction.FNegate(t, _, a) =>
      floatingNegate(site, t, a)(staged(site, instruction, CoreFamily.Floating))
    case instruction @ Instruction.FmaKHR(t, _, a, b, c) =>
      def fused = staged(site, instruction, CoreFamily.FusedMultiplyAdd)
      floating(site, "OpFmaKHR", t, a, b)(floating(site, "OpFmaKHR", t, b, c)(fused))
    case instruction @ Instruction.VectorTimesScalar(t, _, a, b) =>
      vectorTimesScalar(site, t, a, b)(staged(site, instruction, CoreFamily.Floating))
    case instruction @ Instruction.Dot(t, _, a, b) => dot(site, t, a, b)(staged(site, instruction, CoreFamily.Floating))
    case instruction @ Instruction.Transpose(t, d, a) =>
      matrix(site, t, d, MatrixOperation.Transpose, Vec(a))(staged(site, instruction, CoreFamily.Floating))
    case instruction @ Instruction.MatrixTimesScalar(t, _, a, b) if site.program.cooperativeMatrices.contains(id(t)) =>
      vectorTimesScalar(site, t, a, b)(staged(site, instruction, CoreFamily.Floating))
    case instruction @ Instruction.MatrixTimesScalar(t, d, a, b) =>
      matrix(site, t, d, MatrixOperation.Scale, Vec(a, b))(staged(site, instruction, CoreFamily.Floating))
    case instruction @ Instruction.VectorTimesMatrix(t, d, a, b) =>
      matrix(site, t, d, MatrixOperation.VectorMatrix, Vec(a, b))(staged(site, instruction, CoreFamily.Floating))
    case instruction @ Instruction.MatrixTimesVector(t, d, a, b) =>
      matrix(site, t, d, MatrixOperation.MatrixVector, Vec(a, b))(staged(site, instruction, CoreFamily.Floating))
    case instruction @ Instruction.MatrixTimesMatrix(t, d, a, b) =>
      matrix(site, t, d, MatrixOperation.Multiply, Vec(a, b))(staged(site, instruction, CoreFamily.Floating))
    case instruction @ Instruction.OuterProduct(t, d, a, b) =>
      matrix(site, t, d, MatrixOperation.OuterProduct, Vec(a, b))(staged(site, instruction, CoreFamily.Floating))
  }

  /** Data operands, or the fault the first absent or opaque one raises. */
  private def data(site: Site, operands: Vec[Operand], opaque: Typed.Opaque => Fault): Either[Fault, Vec[Typed.Data]] =
    Traversal.traverse(operands)(operand =>
      typed(site, operand) match {
        case value: Typed.Data      => Right(value)
        case value: Typed.Opaque    => Left(opaque(value))
        case value: Typed.Undefined => Left(undefined(value.id))
      }
    )

  /** The integer lane kernel of `opcode` at `width` for `OpSpecConstantOp` folding, from the same semantics the ops
    * run, or None outside them. Unary kernels ignore their second operand.
    */
  def integerKernel(opcode: Int, width: Int in 1 ~ 64): Option[Kernel2] = ConstantFolder.matched(
    IntegerSemantics.binary(Execute, ConstantFolder, opcode, width, -1L)(OperandIds.none)(
      IntegerSemantics.unary(Execute, ConstantFolder, opcode, width)(OperandIds.none)(ConstantFolder.unmatched)
    )
  )

  // Wrapping addition, subtraction and multiplication take their width from the result and broadcast operands.
  private def wrapping(site: Site, left: Operand, right: Operand)(staged: => Op): Op =
    lowered(
      data(site, Vec(left, right), value => Fault.InvalidRequest(s"SPIR-V value %${value.id} is not numeric")).map(_ =>
        staged
      )
    )

  private inline def sameShapes[S](
      site: Site,
      opcode: String,
      resultType: Operand,
      left: Operand,
      right: Operand,
      inline shape: Int => Option[S],
      resultKind: String,
      operandKind: String
  )(using CanEqual[S, S]): Either[Fault, S] =
    data(
      site,
      Vec(left, right),
      value => Fault.InvalidRequest(s"$opcode ${side(value.id, left)} operand %${value.id} is a pointer")
    )
      .flatMap { operands =>
        (shape(id(resultType)), shape(operands(0).typeId), shape(operands(1).typeId)) match {
          case (Some(result), Some(leftShape), Some(rightShape)) if result == leftShape && result == rightShape =>
            Right(result)
          case (None, _, _) =>
            Left(Fault.InvalidRequest(s"$opcode result type %${id(resultType)} is not $resultKind"))
          case (_, None, _) =>
            Left(Fault.InvalidRequest(s"$opcode left operand %${id(left)} is not $operandKind"))
          case (_, _, None) =>
            Left(Fault.InvalidRequest(s"$opcode right operand %${id(right)} is not $operandKind"))
          case (Some(result), Some(leftShape), Some(rightShape)) =>
            Left(
              Fault.InvalidRequest(
                s"$opcode type shapes do not match: result $result, left $leftShape, right $rightShape"
              )
            )
        }
      }

  private def sameIntegerShapes(site: Site, opcode: String, resultType: Operand, left: Operand, right: Operand)
      : Either[Fault, (Int in 1 ~ 64, Int)] = sameShapes(
    site,
    opcode,
    resultType,
    left,
    right,
    site.program.integerShape,
    resultKind = "an integer scalar or vector",
    operandKind = "an integer scalar or vector"
  )

  private def side(value: Int, left: Operand): String = if (value == id(left)) "left" else "right"

  private def division(site: Site, name: String, resultType: Operand, left: Operand, right: Operand)(staged: => Op)
      : Op = lowered(sameIntegerShapes(site, name, resultType, left, right).map(_ => staged))

  private def negate(site: Site, resultType: Operand, source: Operand)(staged: => Op): Op = lowered(
    data(site, Vec(source), _ => Fault.InvalidRequest("OpSNegate source is a pointer")).flatMap { operands =>
      (site.program.integerShape(operands(0).typeId), site.program.integerShape(id(resultType))) match {
        case (Some(shape), Some(result)) if shape == result => Right(staged)
        case _                                              =>
          Left(Fault.InvalidRequest("OpSNegate requires matching integer scalar/vector operand and result types"))
      }
    }
  )

  private def shift(site: Site, name: String, resultType: Operand, base: Operand, shift: Operand)(staged: => Op)
      : Op = lowered(
    data(
      site,
      Vec(base, shift),
      _ => Fault.InvalidRequest(s"$name requires integer scalar or vector operands")
    ).flatMap {
      operands =>
        (
          site.program.integerShape(id(resultType)),
          site.program.integerShape(operands(0).typeId),
          site.program.integerShape(operands(1).typeId)
        ) match {
          case (Some(result @ (_, count)), Some(baseShape), Some((_, shiftCount)))
              if baseShape == result && shiftCount == count =>
            Right(staged)
          case _ =>
            Left(Fault.InvalidRequest(
              s"$name requires an integer scalar/vector result and base of the same type, " +
                "plus an integer shift operand with the same lane count"
            ))
        }
    }
  )

  /** Integer operands whose type is the result type or has its integer shape. */
  def bitwiseOperands(site: Site, opcode: String, resultType: Operand, left: Operand, right: Operand)(
      op: (Int in 1 ~ 64) => Op
  ): Op = site.program.integerShape(id(resultType)) match {
    case None =>
      Fail(Fault.InvalidRequest(s"$opcode result type %${id(resultType)} is not an integer scalar or vector"))
    case Some(shape @ (width, _)) =>
      def matches(typeId: Int): Boolean = typeId == id(resultType) || site.program.integerShape(typeId).contains(shape)
      (typed(site, left), typed(site, right)) match {
        case (Typed.Undefined(value), _) => Fail(undefined(value))
        case (_, Typed.Undefined(value)) => Fail(undefined(value))
        case (Typed.Data(_, leftType), Typed.Data(_, rightType)) if matches(leftType) && matches(rightType) => op(width)
        case _                                                                                              =>
          Fail(Fault.InvalidRequest(s"$opcode requires matching integer scalar or vector operand and result types"))
      }
  }

  private def not(site: Site, resultType: Operand, source: Operand)(staged: => Op): Op =
    site.program.integerShape(id(resultType)) match {
      case None =>
        Fail(Fault.InvalidRequest(s"OpNot result type %${id(resultType)} is not an integer scalar or vector"))
      case Some(shape) =>
        typed(site, source) match {
          case Typed.Undefined(value) => Fail(undefined(value))
          case Typed.Data(_, sourceType)
              if sourceType == id(resultType) || site.program.integerShape(sourceType).contains(shape) =>
            staged
          case _ =>
            Fail(Fault.InvalidRequest("OpNot requires matching integer scalar or vector operand and result types"))
        }
    }

  private def bitCount(site: Site, resultType: Operand, base: Operand)(staged: => Op): Op = lowered(
    data(site, Vec(base), _ => Fault.InvalidRequest("OpBitCount base is not an integer value")).flatMap { operands =>
      (site.program.integerShape(id(resultType)), site.program.integerShape(operands(0).typeId)) match {
        case (Some((_, count)), Some((_, baseCount))) if count == baseCount => Right(staged)
        case _                                                              =>
          Left(Fault.InvalidRequest("OpBitCount requires integer operands and results with equal component counts"))
      }
    }
  )

  /** Bit reversal and bit-field instructions, whose base and inserted value have the result's integer shape. */
  private def bitField(site: Site, resultType: Operand, base: Operand, insert: Option[Operand])(staged: => Op): Op =
    site.program.integerShape(id(resultType)) match {
      case None        => Fail(Fault.InvalidRequest("bit-field result requires integer components"))
      case Some(shape) =>
        def matching(operand: Operand, message: NonEmpty[String]): Either[Fault, Unit] = typed(site, operand) match {
          case Typed.Data(_, typeId) if site.program.integerShape(typeId).contains(shape) => Right(())
          case _ => Left(Fault.InvalidRequest(message))
        }
        lowered(for {
          _ <- matching(base, "bit-field base shape does not match result")
          _ <- insert.fold[Either[Fault, Unit]](Right(()))(matching(
            _,
            "bit-field inserted value shape does not match result"
          ))
        } yield staged)
    }

  private def extended(site: Site, instruction: Instruction, resultType: Operand, left: Operand, right: Operand)
      : Op = {
    val opcode = instruction.opcodeNumber.toInt
    val member = site.program.runtimeType(id(resultType)) match {
      case Some(RuntimeType.Structure(members)) if members.size == 2 && members(0) == members(1) => Some(members(0))
      case _                                                                                     => None
    }
    val shape = member.flatMap(typeId =>
      site.program.runtimeType(typeId) match {
        case Some(RuntimeType.Integer(width, signed))       => Some((width.widen[Int in 1 ~ 64], 1, signed))
        case Some(RuntimeType.VectorType(component, count)) => site.program.runtimeType(component).collect {
            case RuntimeType.Integer(width, signed) => (width.widen[Int in 1 ~ 64], count, signed)
          }
        case _ => None
      }
    )
    val mismatch =
      Fault.InvalidRequest("extended integer arithmetic requires a two-member integer structure and matching operands")
    (typed(site, left), typed(site, right), shape) match {
      case (Typed.Undefined(value), _, _) => Fail(undefined(value))
      case (_, Typed.Undefined(value), _) => Fail(undefined(value))
      case (Typed.Data(_, leftType), Typed.Data(_, rightType), Some((_, _, signed)))
          if member.contains(leftType) && leftType == rightType && (opcode == SPIRV.Opcode.SMulExtended || !signed) =>
        staged(site, instruction, CoreFamily.Extended)
      case _ => Fail(mismatch)
    }
  }

  /** Floating-point operands and result of one shape, named `opcode` in faults. */
  def floating(site: Site, opcode: String, resultType: Operand, left: Operand, right: Operand)(staged: => Op): Op =
    lowered(
      sameShapes(
        site,
        opcode,
        resultType,
        left,
        right,
        site.program.floatingShape,
        resultKind = "a floating-point scalar or vector",
        operandKind = "floating-point"
      ).map(_ => staged)
    )

  private def floatingNegate(site: Site, resultType: Operand, source: Operand)(staged: => Op): Op = lowered(
    data(site, Vec(source), _ => Fault.InvalidRequest("OpFNegate source is a pointer")).flatMap { operands =>
      (site.program.floatingShape(operands(0).typeId), site.program.floatingShape(id(resultType))) match {
        case (Some(shape), Some(result)) if shape == result => Right(staged)
        case _                                              =>
          Left(
            Fault.InvalidRequest("OpFNegate requires matching floating-point scalar/vector operand and result types")
          )
      }
    }
  )

  private def vectorTimesScalar(site: Site, resultType: Operand, vector: Operand, scalar: Operand)(staged: => Op): Op =
    (typed(site, vector), typed(site, scalar)) match {
      case (Typed.Undefined(value), _)                            => Fail(undefined(value))
      case (_, Typed.Undefined(value))                            => Fail(undefined(value))
      case (Typed.Data(_, vectorType), Typed.Data(_, scalarType)) =>
        (
          site.program.floatingShape(vectorType),
          site.program.floatingShape(scalarType),
          site.program.floatingShape(id(resultType))
        ) match {
          case (Some((width, count)), Some((scalarWidth, 1)), Some((resultWidth, resultCount)))
              if width == scalarWidth && width == resultWidth && count == resultCount && count > 1 =>
            staged
          case _ => Fail(Fault.InvalidRequest("OpVectorTimesScalar requires a floating vector and matching scalar"))
        }
      case _ => Fail(Fault.InvalidRequest("OpVectorTimesScalar requires a floating vector and scalar"))
    }

  private def dot(site: Site, resultType: Operand, left: Operand, right: Operand)(staged: => Op): Op =
    (typed(site, left), typed(site, right)) match {
      case (Typed.Undefined(value), _)                         => Fail(undefined(value))
      case (_, Typed.Undefined(value))                         => Fail(undefined(value))
      case (Typed.Data(_, leftType), Typed.Data(_, rightType)) =>
        (
          site.program.floatingShape(leftType),
          site.program.floatingShape(rightType),
          site.program.floatingShape(id(resultType))
        ) match {
          case (Some((width, count)), Some((rightWidth, rightCount)), Some((resultWidth, 1)))
              if width == rightWidth && width == resultWidth && count == rightCount =>
            staged
          case _ => Fail(Fault.InvalidRequest("OpDot requires matching floating vectors and a scalar result"))
        }
      case _ => Fail(Fault.InvalidRequest("OpDot operands are not floating-point vectors"))
    }

  private enum MatrixOperation derives CanEqual {
    case Transpose, Scale, VectorMatrix, MatrixVector, Multiply, OuterProduct
  }

  // Every matrix is a sequence of columns; storage padding is not part of a value's lanes.
  private def matrix(
      site: Site,
      resultType: Operand,
      dest: Operand,
      operation: MatrixOperation,
      sources: Vec[Operand]
  )(staged: => Op): Op =
    lowered(data(site, sources, _ => Fault.InvalidRequest(s"matrix $operation requires data operands")).flatMap {
      operands =>
        val program      = site.program
        val resultId     = id(resultType)
        val leftType     = operands(0).typeId
        val rightType    = if (operands.size == 2) operands(1).typeId else leftType
        val resultMatrix = program.matrixShape(resultId)
        val leftMatrix   = program.matrixShape(leftType)
        val rightMatrix  = program.matrixShape(rightType)
        val resultVector = program.floatingShape(resultId)
        val leftVector   = program.floatingShape(leftType)
        val rightVector  = program.floatingShape(rightType)
        val mismatch     = Fault.InvalidRequest(s"matrix $operation operand/result shapes do not match")
        val valid        = operation match {
          case MatrixOperation.Transpose => (leftMatrix, resultMatrix) match {
              case (Some((width, rows, columns)), Some((outWidth, outRows, outColumns))) =>
                width == outWidth && rows == outColumns && columns == outRows
              case _ => false
            }
          case MatrixOperation.Scale => (leftMatrix, rightVector) match {
              case (Some(shape @ (width, _, _)), Some((scalarWidth, 1))) =>
                resultMatrix.contains(shape) && width == scalarWidth
              case _ => false
            }
          case MatrixOperation.Multiply => (leftMatrix, rightMatrix, resultMatrix) match {
              case (
                    Some((width, rows, inner)),
                    Some((rightWidth, rightRows, columns)),
                    Some((outWidth, outRows, outColumns))
                  ) =>
                width == rightWidth && width == outWidth && inner == rightRows && outRows == rows &&
                outColumns == columns
              case _ => false
            }
          case MatrixOperation.MatrixVector => (leftMatrix, rightVector, resultVector) match {
              case (Some((width, rows, columns)), Some((rightWidth, count)), Some((outWidth, outCount))) =>
                width == rightWidth && width == outWidth && columns == count && rows == outCount
              case _ => false
            }
          case MatrixOperation.VectorMatrix => (leftVector, rightMatrix, resultVector) match {
              case (Some((width, count)), Some((rightWidth, rows, columns)), Some((outWidth, outCount))) =>
                width == rightWidth && width == outWidth && rows == count && columns == outCount
              case _ => false
            }
          case MatrixOperation.OuterProduct => (leftVector, rightVector, resultMatrix) match {
              case (Some((width, rows)), Some((rightWidth, columns)), Some((outWidth, outRows, outColumns))) =>
                width == rightWidth && width == outWidth && rows == outRows && columns == outColumns
              case _ => false
            }
        }
        (operation, leftMatrix) match {
          case (_, _) if !valid                                      => Left(mismatch)
          case (MatrixOperation.Transpose, Some((_, rows, columns))) =>
            Right(transpose(resultId, id(dest), operands(0).id, rows, columns, mismatch))
          case _ => Right(staged)
        }
    })

  /** Transposition only moves lanes: result lane `row + columns * column` is source lane `column + rows * row`. */
  private def transpose(resultType: Int, dest: Int, source: Int, rows: Int, columns: Int, mismatch: Fault): Op =
    step { (execution, _) =>
      val values = execution.values
      Option(values.lanes(source)) match {
        case Some(lanes) if lanes.length == rows * columns =>
          Right(values.update(
            dest,
            VMValue.Bits(
              resultType,
              Array.tabulate(lanes.length)(lane => lanes((lane % columns) * rows + lane / columns))
            )
          ))
        case None => Left(undefined(source))
        case _    => Left(mismatch)
      }
    }
}
