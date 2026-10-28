package net.kurobako.gemstone.isa.spirv

import LaneOps.*
import Op.*
import OpBuilder.*
import VMInvocation.*
import VMMemory.*
import VMProgram.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.vm.*

/** Integer and floating-point arithmetic, bitwise operations, shifts, bit fields and the linear algebra opcodes. Each
  * family pairs a shape rule, checked once at load, with a lane kernel.
  */
private object ArithmeticOps {
  import Operand.*

  def lower(using site: Site): PartialFunction[Instruction, Op] = {
    case instruction @ Instruction.IAdd(_, _, a, b) =>
      wrapping(a, b)(staged(site, instruction, CoreFamily.Integer))
    case instruction @ Instruction.ISub(_, _, a, b) =>
      wrapping(a, b)(staged(site, instruction, CoreFamily.Integer))
    case instruction @ Instruction.IMul(_, _, a, b) =>
      wrapping(a, b)(staged(site, instruction, CoreFamily.Integer))
    case instruction @ Instruction.UDiv(t, d, a, b) =>
      division("OpUDiv", BinaryOperands(t, d, a, b))(staged(site, instruction, CoreFamily.Integer))
    case instruction @ Instruction.UMod(t, d, a, b) =>
      division("OpUMod", BinaryOperands(t, d, a, b))(staged(site, instruction, CoreFamily.Integer))
    case instruction @ Instruction.SDiv(t, d, a, b) =>
      division("OpSDiv", BinaryOperands(t, d, a, b))(staged(site, instruction, CoreFamily.Integer))
    case instruction @ Instruction.SRem(t, d, a, b) =>
      division("OpSRem", BinaryOperands(t, d, a, b))(staged(site, instruction, CoreFamily.Integer))
    case instruction @ Instruction.SMod(t, d, a, b) =>
      division("OpSMod", BinaryOperands(t, d, a, b))(staged(site, instruction, CoreFamily.Integer))
    case instruction @ Instruction.SNegate(t, _, a) => negate(t, a)(staged(site, instruction, CoreFamily.Integer))
    case instruction @ Instruction.ShiftLeftLogical(t, _, a, b) =>
      shift("OpShiftLeftLogical", t, a, b)(staged(site, instruction, CoreFamily.Integer))
    case instruction @ Instruction.ShiftRightLogical(t, _, a, b) =>
      shift("OpShiftRightLogical", t, a, b)(staged(site, instruction, CoreFamily.Integer))
    case instruction @ Instruction.ShiftRightArithmetic(t, _, a, b) =>
      shift("OpShiftRightArithmetic", t, a, b)(staged(site, instruction, CoreFamily.Integer))
    case instruction @ Instruction.BitwiseOr(t, d, a, b) =>
      bitwiseOperands("OpBitwiseOr", BinaryOperands(t, d, a, b))(_ => staged(site, instruction, CoreFamily.Integer))
    case instruction @ Instruction.BitwiseXor(t, d, a, b) =>
      bitwiseOperands("OpBitwiseXor", BinaryOperands(t, d, a, b))(_ => staged(site, instruction, CoreFamily.Integer))
    case instruction @ Instruction.BitwiseAnd(t, d, a, b) =>
      bitwiseOperands("OpBitwiseAnd", BinaryOperands(t, d, a, b))(_ => staged(site, instruction, CoreFamily.Integer))
    case instruction @ Instruction.Not(t, _, a)      => not(t, a)(staged(site, instruction, CoreFamily.Integer))
    case instruction @ Instruction.BitCount(t, _, a) =>
      bitCount(t, a)(staged(site, instruction, CoreFamily.Integer))
    case instruction @ Instruction.BitReverse(t, _, a) =>
      bitField(t, a, None)(staged(site, instruction, CoreFamily.Integer))
    case instruction @ Instruction.BitFieldUExtract(resultType = t, base = base) =>
      bitField(t, base, None)(staged(site, instruction, CoreFamily.BitField))
    case instruction @ Instruction.BitFieldSExtract(resultType = t, base = base) =>
      bitField(t, base, None)(staged(site, instruction, CoreFamily.BitField))
    case instruction @ Instruction.BitFieldInsert(resultType = t, base = base, insert = insert) =>
      bitField(t, base, Some(insert))(staged(site, instruction, CoreFamily.BitField))
    case instruction @ Instruction.IAddCarry(t, d, a, b)    => extended(instruction, BinaryOperands(t, d, a, b))
    case instruction @ Instruction.ISubBorrow(t, d, a, b)   => extended(instruction, BinaryOperands(t, d, a, b))
    case instruction @ Instruction.UMulExtended(t, d, a, b) => extended(instruction, BinaryOperands(t, d, a, b))
    case instruction @ Instruction.SMulExtended(t, d, a, b) => extended(instruction, BinaryOperands(t, d, a, b))
    // binary16 operands are exact in binary32, and binary32 holds 2p + 2 bits of a binary16 result, so the basic
    // operations round once into binary16; remainders are exact.
    case instruction @ Instruction.FAdd(t, d, a, b) =>
      floating("OpFAdd", BinaryOperands(t, d, a, b))(staged(site, instruction, CoreFamily.Floating))
    case instruction @ Instruction.FSub(t, d, a, b) =>
      floating("OpFSub", BinaryOperands(t, d, a, b))(staged(site, instruction, CoreFamily.Floating))
    case instruction @ Instruction.FMul(t, d, a, b) =>
      floating("OpFMul", BinaryOperands(t, d, a, b))(staged(site, instruction, CoreFamily.Floating))
    case instruction @ Instruction.FDiv(t, d, a, b) =>
      floating("OpFDiv", BinaryOperands(t, d, a, b))(staged(site, instruction, CoreFamily.Floating))
    case instruction @ Instruction.FRem(t, d, a, b) =>
      floating("OpFRem", BinaryOperands(t, d, a, b))(staged(site, instruction, CoreFamily.Floating))
    case instruction @ Instruction.FMod(t, d, a, b) =>
      floating("OpFMod", BinaryOperands(t, d, a, b))(staged(site, instruction, CoreFamily.Floating))
    case instruction @ Instruction.FNegate(t, _, a) =>
      floatingNegate(t, a)(staged(site, instruction, CoreFamily.Floating))
    case instruction @ Instruction.FmaKHR(t, d, a, b, c) =>
      def fused = staged(site, instruction, CoreFamily.FusedMultiplyAdd)
      floating("OpFmaKHR", BinaryOperands(t, d, a, b))(floating("OpFmaKHR", BinaryOperands(t, d, b, c))(fused))
    case instruction @ Instruction.VectorTimesScalar(t, _, a, b) =>
      vectorTimesScalar(t, a, b)(staged(site, instruction, CoreFamily.Floating))
    case instruction @ Instruction.Dot(t, d, a, b) =>
      dot(BinaryOperands(t, d, a, b))(staged(site, instruction, CoreFamily.Floating))
    case instruction @ Instruction.Transpose(t, d, a) =>
      matrix(ResultIds(t, d), MatrixOperation.Transpose, Vec(a))(staged(site, instruction, CoreFamily.Floating))
    case instruction @ Instruction.MatrixTimesScalar(t, _, a, b) if site.program.cooperativeMatrices.contains(id(t)) =>
      vectorTimesScalar(t, a, b)(staged(site, instruction, CoreFamily.Floating))
    case instruction @ Instruction.MatrixTimesScalar(t, d, a, b) =>
      matrix(ResultIds(t, d), MatrixOperation.Scale, Vec(a, b))(staged(site, instruction, CoreFamily.Floating))
    case instruction @ Instruction.VectorTimesMatrix(t, d, a, b) =>
      matrix(ResultIds(t, d), MatrixOperation.VectorMatrix, Vec(a, b))(staged(site, instruction, CoreFamily.Floating))
    case instruction @ Instruction.MatrixTimesVector(t, d, a, b) =>
      matrix(ResultIds(t, d), MatrixOperation.MatrixVector, Vec(a, b))(staged(site, instruction, CoreFamily.Floating))
    case instruction @ Instruction.MatrixTimesMatrix(t, d, a, b) =>
      matrix(ResultIds(t, d), MatrixOperation.Multiply, Vec(a, b))(staged(site, instruction, CoreFamily.Floating))
    case instruction @ Instruction.OuterProduct(t, d, a, b) =>
      matrix(ResultIds(t, d), MatrixOperation.OuterProduct, Vec(a, b))(staged(site, instruction, CoreFamily.Floating))
  }

  /** Data operands, or the fault the first absent or opaque one raises. */
  private def data(operands: Vec[Operand], opaque: Typed.Opaque => Fault)(using
      site: Site
  ): Either[Fault, Vec[Typed.Data]] =
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
  private def wrapping(left: Operand, right: Operand)(using Site)(staged: => Op): Op =
    lowered(
      data(Vec(left, right), value => Fault.InvalidRequest(s"SPIR-V value %${value.id} is not numeric")).map(_ =>
        staged
      )
    )

  private inline def sameShapes[S](
      opcode: String,
      operands: BinaryOperands,
      inline shape: Int => Option[S],
      resultKind: String,
      operandKind: String
  )(using Site, CanEqual[S, S]): Either[Fault, S] =
    data(
      Vec(operands.left, operands.right),
      value => Fault.InvalidRequest(s"$opcode ${side(value.id, operands.left)} operand %${value.id} is a pointer")
    )
      .flatMap { found =>
        (shape(operands.ids.resultType), shape(found(0).typeId), shape(found(1).typeId)) match {
          case (Some(result), Some(leftShape), Some(rightShape)) if result == leftShape && result == rightShape =>
            Right(result)
          case (None, _, _) =>
            Left(Fault.InvalidRequest(s"$opcode result type %${operands.ids.resultType} is not $resultKind"))
          case (_, None, _) =>
            Left(Fault.InvalidRequest(s"$opcode left operand %${id(operands.left)} is not $operandKind"))
          case (_, _, None) =>
            Left(Fault.InvalidRequest(s"$opcode right operand %${id(operands.right)} is not $operandKind"))
          case (Some(result), Some(leftShape), Some(rightShape)) =>
            Left(
              Fault.InvalidRequest(
                s"$opcode type shapes do not match: result $result, left $leftShape, right $rightShape"
              )
            )
        }
      }

  private def sameIntegerShapes(opcode: String, operands: BinaryOperands)(using
      site: Site
  )
      : Either[Fault, (Int in 1 ~ 64, Int)] = sameShapes(
    opcode,
    operands,
    site.program.integerShape,
    resultKind = "an integer scalar or vector",
    operandKind = "an integer scalar or vector"
  )

  private def side(value: Int, left: Operand): String = if (value == id(left)) "left" else "right"

  private def division(name: String, operands: BinaryOperands)(using Site)(staged: => Op): Op =
    lowered(sameIntegerShapes(name, operands).map(_ => staged))

  private def negate(resultType: Operand, source: Operand)(using site: Site)(staged: => Op): Op = lowered(
    data(Vec(source), _ => Fault.InvalidRequest("OpSNegate source is a pointer")).flatMap { operands =>
      (site.program.integerShape(operands(0).typeId), site.program.integerShape(id(resultType))) match {
        case (Some(shape), Some(result)) if shape == result => Right(staged)
        case _                                              =>
          Left(Fault.InvalidRequest("OpSNegate requires matching integer scalar/vector operand and result types"))
      }
    }
  )

  private def shift(name: String, resultType: Operand, base: Operand, shift: Operand)(using
      site: Site
  )(staged: => Op)
      : Op = lowered(
    data(
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
  def bitwiseOperands(opcode: String, operands: BinaryOperands)(using
      site: Site
  )(
      op: (Int in 1 ~ 64) => Op
  ): Op = site.program.integerShape(operands.ids.resultType) match {
    case None =>
      Fail(Fault.InvalidRequest(s"$opcode result type %${operands.ids.resultType} is not an integer scalar or vector"))
    case Some(shape @ (width, _)) =>
      def matches(typeId: Int): Boolean =
        typeId == operands.ids.resultType || site.program.integerShape(typeId).contains(shape)
      (typed(site, operands.left), typed(site, operands.right)) match {
        case (Typed.Undefined(value), _) => Fail(undefined(value))
        case (_, Typed.Undefined(value)) => Fail(undefined(value))
        case (Typed.Data(_, leftType), Typed.Data(_, rightType)) if matches(leftType) && matches(rightType) => op(width)
        case _                                                                                              =>
          Fail(Fault.InvalidRequest(s"$opcode requires matching integer scalar or vector operand and result types"))
      }
  }

  private def not(resultType: Operand, source: Operand)(using site: Site)(staged: => Op): Op =
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

  private def bitCount(resultType: Operand, base: Operand)(using site: Site)(staged: => Op): Op = lowered(
    data(Vec(base), _ => Fault.InvalidRequest("OpBitCount base is not an integer value")).flatMap { operands =>
      (site.program.integerShape(id(resultType)), site.program.integerShape(operands(0).typeId)) match {
        case (Some((_, count)), Some((_, baseCount))) if count == baseCount => Right(staged)
        case _                                                              =>
          Left(Fault.InvalidRequest("OpBitCount requires integer operands and results with equal component counts"))
      }
    }
  )

  /** Bit reversal and bit-field instructions, whose base and inserted value have the result's integer shape. */
  private def bitField(resultType: Operand, base: Operand, insert: Option[Operand])(using
      site: Site
  )(staged: => Op): Op =
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

  private def extended(instruction: Instruction, operands: BinaryOperands)(using site: Site): Op = {
    val opcode = instruction.opcodeNumber.toInt
    val member = site.program.runtimeType(operands.ids.resultType) match {
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
    (typed(site, operands.left), typed(site, operands.right), shape) match {
      case (Typed.Undefined(value), _, _) => Fail(undefined(value))
      case (_, Typed.Undefined(value), _) => Fail(undefined(value))
      case (Typed.Data(_, leftType), Typed.Data(_, rightType), Some((_, _, signed)))
          if member.contains(leftType) && leftType == rightType && (opcode == SPIRV.Opcode.SMulExtended || !signed) =>
        staged(site, instruction, CoreFamily.Extended)
      case _ => Fail(mismatch)
    }
  }

  /** Floating-point operands and result of one shape, named `opcode` in faults. */
  def floating(opcode: String, operands: BinaryOperands)(using site: Site)(staged: => Op): Op =
    lowered(
      sameShapes(
        opcode,
        operands,
        site.program.floatingShape,
        resultKind = "a floating-point scalar or vector",
        operandKind = "floating-point"
      ).map(_ => staged)
    )

  private def floatingNegate(resultType: Operand, source: Operand)(using site: Site)(staged: => Op): Op = lowered(
    data(Vec(source), _ => Fault.InvalidRequest("OpFNegate source is a pointer")).flatMap { operands =>
      (site.program.floatingShape(operands(0).typeId), site.program.floatingShape(id(resultType))) match {
        case (Some(shape), Some(result)) if shape == result => Right(staged)
        case _                                              =>
          Left(
            Fault.InvalidRequest("OpFNegate requires matching floating-point scalar/vector operand and result types")
          )
      }
    }
  )

  private def vectorTimesScalar(resultType: Operand, vector: Operand, scalar: Operand)(using
      site: Site
  )(staged: => Op): Op =
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

  private def dot(operands: BinaryOperands)(using site: Site)(staged: => Op): Op =
    (typed(site, operands.left), typed(site, operands.right)) match {
      case (Typed.Undefined(value), _)                         => Fail(undefined(value))
      case (_, Typed.Undefined(value))                         => Fail(undefined(value))
      case (Typed.Data(_, leftType), Typed.Data(_, rightType)) =>
        (
          site.program.floatingShape(leftType),
          site.program.floatingShape(rightType),
          site.program.floatingShape(operands.ids.resultType)
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
  private def matrix(ids: ResultIds, operation: MatrixOperation, sources: Vec[Operand])(using
      site: Site
  )(staged: => Op): Op =
    lowered(data(sources, _ => Fault.InvalidRequest(s"matrix $operation requires data operands")).flatMap {
      operands =>
        val program      = site.program
        val resultId     = ids.resultType
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
            Right(Transposed(operands(0).id, rows, columns, ids.dest, resultId))
          case _ => Right(staged)
        }
    })

  private class Transposed(source: Int, rows: Int, columns: Int, dest: Int, resultType: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      MatrixSemantics.transpose(InvocationMachine(execution))(source, rows, columns, dest, resultType)
  }
}
