package net.kurobako.gemstone.isa.spirv

import LaneOps.*
import Op.*
import OpBuilder.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.vm.*

/** Integer, Boolean and floating-point comparisons, classification and selection. */
private object ComparisonOps {
  import Operand.*

  /** The integer or Boolean comparison kernel of `opcode` at operand width `width` for `OpSpecConstantOp` folding, from
    * the same semantics the ops run, or None outside them. Unary kernels ignore their second operand.
    */
  def comparisonKernel(opcode: Int, width: Int in 1 ~ 64): Option[Kernel2] = ConstantFolder.matched(
    ComparisonSemantics.binary(Execute, ConstantFolder, opcode, width)(OperandIds.none)(
      ComparisonSemantics.unary(Execute, ConstantFolder, opcode)(OperandIds.none)(ConstantFolder.unmatched)
    )
  )

  private val floatingFamilies =
    Seq(CoreFamily.OrderedComparison, CoreFamily.UnorderedComparison, CoreFamily.Classification)

  def lower(using Site): PartialFunction[Instruction, Op] = {
    case instruction @ Instruction.IEqual(t, d, a, b) =>
      integerEquality("OpIEqual", BinaryOperands(t, d, a, b), instruction)
    case instruction @ Instruction.INotEqual(t, d, a, b) =>
      integerEquality("OpINotEqual", BinaryOperands(t, d, a, b), instruction)
    case instruction @ Instruction.LogicalEqual(t, d, a, b) =>
      logical("OpLogicalEqual", BinaryOperands(t, d, a, b), instruction)
    case instruction @ Instruction.LogicalNotEqual(t, d, a, b) =>
      logical("OpLogicalNotEqual", BinaryOperands(t, d, a, b), instruction)
    case instruction @ Instruction.LogicalOr(t, d, a, b) =>
      logical("OpLogicalOr", BinaryOperands(t, d, a, b), instruction)
    case instruction @ Instruction.LogicalAnd(t, d, a, b) =>
      logical("OpLogicalAnd", BinaryOperands(t, d, a, b), instruction)
    case instruction @ Instruction.LogicalNot(t, _, a)           => logicalNot(t, a, instruction)
    case instruction @ Instruction.Any(t, _, a)                  => reduction(t, a, instruction)
    case instruction @ Instruction.All(t, _, a)                  => reduction(t, a, instruction)
    case instruction @ Instruction.ULessThan(t, d, a, b)         => unsigned(BinaryOperands(t, d, a, b), instruction)
    case instruction @ Instruction.UGreaterThan(t, d, a, b)      => unsigned(BinaryOperands(t, d, a, b), instruction)
    case instruction @ Instruction.ULessThanEqual(t, d, a, b)    => unsigned(BinaryOperands(t, d, a, b), instruction)
    case instruction @ Instruction.UGreaterThanEqual(t, d, a, b) => unsigned(BinaryOperands(t, d, a, b), instruction)
    case instruction @ Instruction.SGreaterThan(t, d, a, b)      =>
      signed("OpSGreaterThan", BinaryOperands(t, d, a, b), instruction)
    case instruction @ Instruction.SGreaterThanEqual(t, d, a, b) =>
      signed("OpSGreaterThanEqual", BinaryOperands(t, d, a, b), instruction)
    case instruction @ Instruction.SLessThan(t, d, a, b) =>
      signed("OpSLessThan", BinaryOperands(t, d, a, b), instruction)
    case instruction @ Instruction.SLessThanEqual(t, d, a, b) =>
      signed("OpSLessThanEqual", BinaryOperands(t, d, a, b), instruction)
    case instruction @ Instruction.FOrdEqual(t, d, a, b) =>
      floating("OpFOrdEqual", instruction, BinaryOperands(t, d, a, b))
    case instruction @ Instruction.FOrdNotEqual(t, d, a, b) =>
      floating("OpFOrdNotEqual", instruction, BinaryOperands(t, d, a, b))
    case instruction @ Instruction.LessOrGreater(t, d, a, b) =>
      floating("OpLessOrGreater", instruction, BinaryOperands(t, d, a, b))
    case instruction @ Instruction.Ordered(t, d, a, b) => floating("OpOrdered", instruction, BinaryOperands(t, d, a, b))
    case instruction @ Instruction.FUnordEqual(t, d, a, b) =>
      floating("OpFUnordEqual", instruction, BinaryOperands(t, d, a, b))
    case instruction @ Instruction.FUnordNotEqual(t, d, a, b) =>
      floating("OpFUnordNotEqual", instruction, BinaryOperands(t, d, a, b))
    case instruction @ Instruction.FUnordLessThan(t, d, a, b) =>
      floating("OpFUnordLessThan", instruction, BinaryOperands(t, d, a, b))
    case instruction @ Instruction.FUnordGreaterThan(t, d, a, b) =>
      floating("OpFUnordGreaterThan", instruction, BinaryOperands(t, d, a, b))
    case instruction @ Instruction.FUnordLessThanEqual(t, d, a, b) =>
      floating("OpFUnordLessThanEqual", instruction, BinaryOperands(t, d, a, b))
    case instruction @ Instruction.FUnordGreaterThanEqual(t, d, a, b) =>
      floating("OpFUnordGreaterThanEqual", instruction, BinaryOperands(t, d, a, b))
    case instruction @ Instruction.Unordered(t, d, a, b) =>
      floating("OpUnordered", instruction, BinaryOperands(t, d, a, b))
    case instruction @ Instruction.FOrdLessThan(t, d, a, b) =>
      floating("OpFOrdLessThan", instruction, BinaryOperands(t, d, a, b))
    case instruction @ Instruction.FOrdLessThanEqual(t, d, a, b) =>
      floating("OpFOrdLessThanEqual", instruction, BinaryOperands(t, d, a, b))
    case instruction @ Instruction.FOrdGreaterThan(t, d, a, b) =>
      floating("OpFOrdGreaterThan", instruction, BinaryOperands(t, d, a, b))
    case instruction @ Instruction.FOrdGreaterThanEqual(t, d, a, b) =>
      floating("OpFOrdGreaterThanEqual", instruction, BinaryOperands(t, d, a, b))
    case instruction @ Instruction.IsInf(t, _, a)        => classify("OpIsInf", instruction, t, a)
    case instruction @ Instruction.IsNan(t, _, a)        => classify("OpIsNan", instruction, t, a)
    case instruction @ Instruction.IsFinite(t, _, a)     => classify("OpIsFinite", instruction, t, a)
    case instruction @ Instruction.IsNormal(t, _, a)     => classify("OpIsNormal", instruction, t, a)
    case instruction @ Instruction.SignBitSet(t, _, a)   => classify("OpSignBitSet", instruction, t, a)
    case instruction @ Instruction.Select(t, d, c, a, b) =>
      select(instruction, ResultIds(t, d), condition = c, whenTrue = a, whenFalse = b)
  }

  private def pointerFault(opcode: String, left: Operand)(value: Typed): Fault =
    Fault.InvalidRequest(s"$opcode ${if (value.id == id(left)) "left" else "right"} operand %${value.id} is a pointer")

  /** Both operands as data, or the fault for the first absent or opaque one. */
  private def pair(operands: BinaryOperands, opaque: Typed => Fault)(using site: Site): Either[Fault, (Int, Int)] =
    (typed(site, operands.left), typed(site, operands.right)) match {
      case (Typed.Data(_, leftType), Typed.Data(_, rightType)) => Right((leftType, rightType))
      case (Typed.Undefined(value), _)                         => Left(undefined(value))
      case (value: Typed.Opaque, _)                            => Left(opaque(value))
      case (_, Typed.Undefined(value))                         => Left(undefined(value))
      case (_, value)                                          => Left(opaque(value))
    }

  private def integerEquality(opcode: String, operands: BinaryOperands, instruction: Instruction)(using
      site: Site
  )
      : Op = lowered(pair(operands, pointerFault(opcode, operands.left)).flatMap { case (leftType, rightType) =>
    (
      site.program.integerShape(leftType),
      site.program.integerShape(rightType),
      site.program.booleanLanes(operands.ids.resultType)
    ) match {
      case (Some(leftShape), Some(rightShape), Some(resultLanes))
          if leftShape == rightShape && leftShape._2 == resultLanes =>
        Right(staged(site, instruction, CoreFamily.Comparison))
      case (None, _, _) =>
        Left(Fault.InvalidRequest(s"$opcode left operand %${id(operands.left)} is not an integer scalar or vector"))
      case (_, None, _) =>
        Left(Fault.InvalidRequest(s"$opcode right operand %${id(operands.right)} is not an integer scalar or vector"))
      case (_, _, None) =>
        Left(Fault.InvalidRequest(s"$opcode result type %${operands.ids.resultType} is not a boolean scalar or vector"))
      case (Some(leftShape), Some(rightShape), Some(resultLanes)) =>
        Left(Fault.InvalidRequest(
          s"$opcode type shapes do not match: left $leftShape, right $rightShape, result lanes $resultLanes"
        ))
    }
  })

  private def logical(opcode: String, operands: BinaryOperands, instruction: Instruction)(using
      site: Site
  )
      : Op = {
    val mismatch = Fault.InvalidRequest(s"$opcode requires matching boolean scalar or vector operand and result types")
    lowered(pair(operands, _ => mismatch).flatMap { case (leftType, rightType) =>
      (
        site.program.booleanLanes(leftType),
        site.program.booleanLanes(rightType),
        site.program.booleanLanes(operands.ids.resultType)
      ) match {
        case (Some(leftCount), Some(rightCount), Some(resultCount))
            if leftCount == rightCount && leftCount == resultCount =>
          Right(staged(site, instruction, CoreFamily.Comparison))
        case _ => Left(mismatch)
      }
    })
  }

  private def logicalNot(resultType: Operand, source: Operand, instruction: Instruction)(using site: Site): Op =
    typed(site, source) match {
      case Typed.Undefined(value) => Fail(undefined(value))
      case Typed.Data(_, sourceType)
          if site.program.booleanLanes(sourceType).isDefined &&
            site.program.booleanLanes(sourceType) == site.program.booleanLanes(id(resultType)) =>
        staged(site, instruction, CoreFamily.Comparison)
      case _ => Fail(Fault.InvalidRequest("OpLogicalNot requires matching boolean operand and result types"))
    }

  private def reduction(resultType: Operand, source: Operand, instruction: Instruction)(using
      site: Site
  )
      : Op = {
    val name = if (instruction.opcodeNumber.toInt == SPIRV.Opcode.All) "All" else "Any"
    typed(site, source) match {
      case Typed.Undefined(value)    => Fail(undefined(value))
      case Typed.Opaque(_, _)        => Fail(Fault.InvalidRequest(s"Op$name source is a pointer"))
      case Typed.Data(_, sourceType) =>
        (site.program.booleanLanes(sourceType), site.program.booleanLanes(id(resultType))) match {
          case (Some(_), Some(1)) =>
            staged(site, instruction, CoreFamily.Comparison)
          case _ => Fail(Fault.InvalidRequest(s"Op$name requires a boolean vector"))
        }
    }
  }

  // Unsigned comparisons broadcast operands without checking their shapes.
  private def unsigned(operands: BinaryOperands, instruction: Instruction)(using site: Site): Op =
    lowered(pair(operands, value => Fault.InvalidRequest(s"SPIR-V value %${value.id} is not numeric")).map { _ =>
      staged(site, instruction, CoreFamily.Comparison)
    })

  private def signed(opcode: String, operands: BinaryOperands, instruction: Instruction)(using
      site: Site
  )
      : Op = lowered(pair(operands, pointerFault(opcode, operands.left)).flatMap { case (leftType, rightType) =>
    (
      site.program.integerShape(leftType),
      site.program.integerShape(rightType),
      site.program.booleanLanes(operands.ids.resultType)
    ) match {
      case (Some((width, count)), Some((rightWidth, rightCount)), Some(resultCount))
          if width == rightWidth && count == rightCount && count == resultCount =>
        Right(staged(site, instruction, CoreFamily.Comparison))
      case (None, _, _) =>
        Left(Fault.InvalidRequest(s"$opcode left operand %${id(operands.left)} is not an integer scalar or vector"))
      case (_, None, _) =>
        Left(Fault.InvalidRequest(s"$opcode right operand %${id(operands.right)} is not an integer scalar or vector"))
      case (_, _, None) =>
        Left(Fault.InvalidRequest(s"$opcode result type %${operands.ids.resultType} is not a boolean scalar or vector"))
      case (Some(leftShape), Some(rightShape), Some(resultLanes)) =>
        Left(Fault.InvalidRequest(
          s"$opcode type shapes do not match: left $leftShape, right $rightShape, result lanes $resultLanes"
        ))
    }
  })

  private def floating(opcode: String, instruction: Instruction, operands: BinaryOperands)(using
      site: Site
  ): Op = lowered(pair(operands, pointerFault(opcode, operands.left)).flatMap {
    case (leftType, rightType) =>
      (
        site.program.floatingShape(leftType),
        site.program.floatingShape(rightType),
        site.program.booleanLanes(operands.ids.resultType)
      ) match {
        case (Some(leftShape @ (_, count)), Some(rightShape), Some(resultLanes))
            if leftShape == rightShape && count == resultLanes =>
          Right(staged(site, instruction, floatingFamilies*))
        case (None, _, _) =>
          Left(Fault.InvalidRequest(s"$opcode left operand %${id(operands.left)} is not floating-point"))
        case (_, None, _) =>
          Left(Fault.InvalidRequest(s"$opcode right operand %${id(operands.right)} is not floating-point"))
        case (_, _, None) =>
          Left(
            Fault.InvalidRequest(s"$opcode result type %${operands.ids.resultType} is not a boolean scalar or vector")
          )
        case (Some(leftShape), Some(rightShape), Some(resultLanes)) =>
          Left(Fault.InvalidRequest(
            s"$opcode %${operands.ids.dest} operands %${id(operands.left)}/%${id(operands.right)} type shapes do not match: " +
              s"left $leftShape, right $rightShape, result lanes $resultLanes"
          ))
      }
  })

  private def classify(opcode: String, instruction: Instruction, resultType: Operand, source: Operand)(using
      site: Site
  )
      : Op = typed(site, source) match {
    case Typed.Undefined(value)    => Fail(undefined(value))
    case Typed.Opaque(_, _)        => Fail(Fault.InvalidRequest(s"$opcode source is a pointer"))
    case Typed.Data(_, sourceType) =>
      (site.program.floatingShape(sourceType), site.program.booleanLanes(id(resultType))) match {
        case (Some((_, sourceLanes)), Some(resultLanes)) if sourceLanes == resultLanes =>
          staged(site, instruction, floatingFamilies*)
        case (None, _) =>
          Fail(Fault.InvalidRequest(s"$opcode source %${id(source)} is not a floating-point scalar or vector"))
        case (_, None) =>
          Fail(Fault.InvalidRequest(s"$opcode result type %${id(resultType)} is not a boolean scalar or vector"))
        case (Some((_, sourceLanes)), Some(resultLanes)) =>
          Fail(Fault.InvalidRequest(s"$opcode cannot classify $sourceLanes lanes into $resultLanes lanes"))
      }
  }

  private def select(
      instruction: Instruction,
      ids: ResultIds,
      condition: Operand,
      whenTrue: Operand,
      whenFalse: Operand
  )(using
      site: Site
  )
      : Op = {
    val program = site.program
    (typed(site, condition), typed(site, whenTrue), typed(site, whenFalse)) match {
      case (Typed.Undefined(_), _, _) | (Typed.Opaque(_, _), _, _) =>
        Fail(Fault.InvalidRequest(s"OpSelect condition %${id(condition)} is not boolean"))
      case (_, Typed.Undefined(value), _)                                                    => Fail(undefined(value))
      case (_, _, Typed.Undefined(value))                                                    => Fail(undefined(value))
      case (Typed.Data(_, conditionType), Typed.Data(_, trueType), Typed.Data(_, falseType)) =>
        (program.booleanLanes(conditionType), program.scalarLeafCount(ids.resultType)) match {
          case (None, _)        => Fail(Fault.InvalidRequest(s"OpSelect condition %${id(condition)} is not boolean"))
          case (_, Left(error)) => Fail(error)
          case (Some(conditionCount), Right(resultCount)) =>
            val trueCount  = program.scalarLeafCount(trueType).fold(_ => -1, _.raw)
            val falseCount = program.scalarLeafCount(falseType).fold(_ => -1, _.raw)
            if (
              trueCount == resultCount.raw && falseCount == resultCount.raw &&
              (conditionCount == 1 || conditionCount == resultCount.raw)
            )
              staged(site, instruction, CoreFamily.Comparison)
            else
              Fail(Fault.InvalidRequest(
                s"OpSelect condition/value shapes are incompatible: condition $conditionCount, true $trueCount, false $falseCount"
              ))
        }
      case (Typed.Data(_, conditionType), _, _) if program.booleanLanes(conditionType).contains(1) =>
        step { (execution, _) =>
          scalar(execution.values, id(condition)).flatMap(test =>
            transfer(execution.values, if (test != 0L) id(whenTrue) else id(whenFalse), ids.dest, ids.resultType)
          )
        }
      case _ => Fail(Fault.InvalidRequest(s"OpSelect condition %${id(condition)} is invalid"))
    }
  }
}
