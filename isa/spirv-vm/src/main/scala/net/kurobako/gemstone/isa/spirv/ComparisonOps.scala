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

  def lower(site: Site): PartialFunction[Instruction, Op] = {
    case instruction @ Instruction.IEqual(t, _, a, b)    => integerEquality(site, "OpIEqual", t, a, b, instruction)
    case instruction @ Instruction.INotEqual(t, _, a, b) =>
      integerEquality(site, "OpINotEqual", t, a, b, instruction)
    case instruction @ Instruction.LogicalEqual(t, _, a, b)    => logical(site, "OpLogicalEqual", t, a, b, instruction)
    case instruction @ Instruction.LogicalNotEqual(t, _, a, b) =>
      logical(site, "OpLogicalNotEqual", t, a, b, instruction)
    case instruction @ Instruction.LogicalOr(t, _, a, b)         => logical(site, "OpLogicalOr", t, a, b, instruction)
    case instruction @ Instruction.LogicalAnd(t, _, a, b)        => logical(site, "OpLogicalAnd", t, a, b, instruction)
    case instruction @ Instruction.LogicalNot(t, _, a)           => logicalNot(site, t, a, instruction)
    case instruction @ Instruction.Any(t, _, a)                  => reduction(site, t, a, instruction)
    case instruction @ Instruction.All(t, _, a)                  => reduction(site, t, a, instruction)
    case instruction @ Instruction.ULessThan(_, _, a, b)         => unsigned(site, a, b, instruction)
    case instruction @ Instruction.UGreaterThan(_, _, a, b)      => unsigned(site, a, b, instruction)
    case instruction @ Instruction.ULessThanEqual(_, _, a, b)    => unsigned(site, a, b, instruction)
    case instruction @ Instruction.UGreaterThanEqual(_, _, a, b) => unsigned(site, a, b, instruction)
    case instruction @ Instruction.SGreaterThan(t, _, a, b)      => signed(site, "OpSGreaterThan", t, a, b, instruction)
    case instruction @ Instruction.SGreaterThanEqual(t, _, a, b) =>
      signed(site, "OpSGreaterThanEqual", t, a, b, instruction)
    case instruction @ Instruction.SLessThan(t, _, a, b)      => signed(site, "OpSLessThan", t, a, b, instruction)
    case instruction @ Instruction.SLessThanEqual(t, _, a, b) =>
      signed(site, "OpSLessThanEqual", t, a, b, instruction)
    case instruction @ Instruction.FOrdEqual(t, d, a, b)    => floating(site, "OpFOrdEqual", instruction, t, d, a, b)
    case instruction @ Instruction.FOrdNotEqual(t, d, a, b) => floating(site, "OpFOrdNotEqual", instruction, t, d, a, b)
    case instruction @ Instruction.LessOrGreater(t, d, a, b) =>
      floating(site, "OpLessOrGreater", instruction, t, d, a, b)
    case instruction @ Instruction.Ordered(t, d, a, b)     => floating(site, "OpOrdered", instruction, t, d, a, b)
    case instruction @ Instruction.FUnordEqual(t, d, a, b) => floating(site, "OpFUnordEqual", instruction, t, d, a, b)
    case instruction @ Instruction.FUnordNotEqual(t, d, a, b) =>
      floating(site, "OpFUnordNotEqual", instruction, t, d, a, b)
    case instruction @ Instruction.FUnordLessThan(t, d, a, b) =>
      floating(site, "OpFUnordLessThan", instruction, t, d, a, b)
    case instruction @ Instruction.FUnordGreaterThan(t, d, a, b) =>
      floating(site, "OpFUnordGreaterThan", instruction, t, d, a, b)
    case instruction @ Instruction.FUnordLessThanEqual(t, d, a, b) =>
      floating(site, "OpFUnordLessThanEqual", instruction, t, d, a, b)
    case instruction @ Instruction.FUnordGreaterThanEqual(t, d, a, b) =>
      floating(site, "OpFUnordGreaterThanEqual", instruction, t, d, a, b)
    case instruction @ Instruction.Unordered(t, d, a, b)    => floating(site, "OpUnordered", instruction, t, d, a, b)
    case instruction @ Instruction.FOrdLessThan(t, d, a, b) => floating(site, "OpFOrdLessThan", instruction, t, d, a, b)
    case instruction @ Instruction.FOrdLessThanEqual(t, d, a, b) =>
      floating(site, "OpFOrdLessThanEqual", instruction, t, d, a, b)
    case instruction @ Instruction.FOrdGreaterThan(t, d, a, b) =>
      floating(site, "OpFOrdGreaterThan", instruction, t, d, a, b)
    case instruction @ Instruction.FOrdGreaterThanEqual(t, d, a, b) =>
      floating(site, "OpFOrdGreaterThanEqual", instruction, t, d, a, b)
    case instruction @ Instruction.IsInf(t, _, a)        => classify(site, "OpIsInf", instruction, t, a)
    case instruction @ Instruction.IsNan(t, _, a)        => classify(site, "OpIsNan", instruction, t, a)
    case instruction @ Instruction.IsFinite(t, _, a)     => classify(site, "OpIsFinite", instruction, t, a)
    case instruction @ Instruction.IsNormal(t, _, a)     => classify(site, "OpIsNormal", instruction, t, a)
    case instruction @ Instruction.SignBitSet(t, _, a)   => classify(site, "OpSignBitSet", instruction, t, a)
    case instruction @ Instruction.Select(t, d, c, a, b) =>
      select(site, instruction, resultType = t, dest = d, condition = c, whenTrue = a, whenFalse = b)
  }

  private def pointerFault(opcode: String, left: Operand)(value: Typed): Fault =
    Fault.InvalidRequest(s"$opcode ${if (value.id == id(left)) "left" else "right"} operand %${value.id} is a pointer")

  /** Both operands as data, or the fault for the first absent or opaque one. */
  private def pair(site: Site, left: Operand, right: Operand, opaque: Typed => Fault): Either[Fault, (Int, Int)] =
    (typed(site, left), typed(site, right)) match {
      case (Typed.Data(_, leftType), Typed.Data(_, rightType)) => Right((leftType, rightType))
      case (Typed.Undefined(value), _)                         => Left(undefined(value))
      case (value: Typed.Opaque, _)                            => Left(opaque(value))
      case (_, Typed.Undefined(value))                         => Left(undefined(value))
      case (_, value)                                          => Left(opaque(value))
    }

  private def integerEquality(
      site: Site,
      opcode: String,
      resultType: Operand,
      left: Operand,
      right: Operand,
      instruction: Instruction
  )
      : Op = lowered(pair(site, left, right, pointerFault(opcode, left)).flatMap { case (leftType, rightType) =>
    (
      site.program.integerShape(leftType),
      site.program.integerShape(rightType),
      site.program.booleanLanes(id(resultType))
    ) match {
      case (Some(leftShape), Some(rightShape), Some(resultLanes))
          if leftShape == rightShape && leftShape._2 == resultLanes =>
        Right(staged(site, instruction, CoreFamily.Comparison))
      case (None, _, _) =>
        Left(Fault.InvalidRequest(s"$opcode left operand %${id(left)} is not an integer scalar or vector"))
      case (_, None, _) =>
        Left(Fault.InvalidRequest(s"$opcode right operand %${id(right)} is not an integer scalar or vector"))
      case (_, _, None) =>
        Left(Fault.InvalidRequest(s"$opcode result type %${id(resultType)} is not a boolean scalar or vector"))
      case (Some(leftShape), Some(rightShape), Some(resultLanes)) =>
        Left(Fault.InvalidRequest(
          s"$opcode type shapes do not match: left $leftShape, right $rightShape, result lanes $resultLanes"
        ))
    }
  })

  private def logical(
      site: Site,
      opcode: String,
      resultType: Operand,
      left: Operand,
      right: Operand,
      instruction: Instruction
  )
      : Op = {
    val mismatch = Fault.InvalidRequest(s"$opcode requires matching boolean scalar or vector operand and result types")
    lowered(pair(site, left, right, _ => mismatch).flatMap { case (leftType, rightType) =>
      (
        site.program.booleanLanes(leftType),
        site.program.booleanLanes(rightType),
        site.program.booleanLanes(id(resultType))
      ) match {
        case (Some(leftCount), Some(rightCount), Some(resultCount))
            if leftCount == rightCount && leftCount == resultCount =>
          Right(staged(site, instruction, CoreFamily.Comparison))
        case _ => Left(mismatch)
      }
    })
  }

  private def logicalNot(site: Site, resultType: Operand, source: Operand, instruction: Instruction): Op =
    typed(site, source) match {
      case Typed.Undefined(value) => Fail(undefined(value))
      case Typed.Data(_, sourceType)
          if site.program.booleanLanes(sourceType).isDefined &&
            site.program.booleanLanes(sourceType) == site.program.booleanLanes(id(resultType)) =>
        staged(site, instruction, CoreFamily.Comparison)
      case _ => Fail(Fault.InvalidRequest("OpLogicalNot requires matching boolean operand and result types"))
    }

  private def reduction(site: Site, resultType: Operand, source: Operand, instruction: Instruction)
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
  private def unsigned(
      site: Site,
      left: Operand,
      right: Operand,
      instruction: Instruction
  )
      : Op =
    lowered(pair(site, left, right, value => Fault.InvalidRequest(s"SPIR-V value %${value.id} is not numeric")).map { _ =>
      staged(site, instruction, CoreFamily.Comparison)
    })

  private def signed(
      site: Site,
      opcode: String,
      resultType: Operand,
      left: Operand,
      right: Operand,
      instruction: Instruction
  )
      : Op = lowered(pair(site, left, right, pointerFault(opcode, left)).flatMap { case (leftType, rightType) =>
    (
      site.program.integerShape(leftType),
      site.program.integerShape(rightType),
      site.program.booleanLanes(id(resultType))
    ) match {
      case (Some((width, count)), Some((rightWidth, rightCount)), Some(resultCount))
          if width == rightWidth && count == rightCount && count == resultCount =>
        Right(staged(site, instruction, CoreFamily.Comparison))
      case (None, _, _) =>
        Left(Fault.InvalidRequest(s"$opcode left operand %${id(left)} is not an integer scalar or vector"))
      case (_, None, _) =>
        Left(Fault.InvalidRequest(s"$opcode right operand %${id(right)} is not an integer scalar or vector"))
      case (_, _, None) =>
        Left(Fault.InvalidRequest(s"$opcode result type %${id(resultType)} is not a boolean scalar or vector"))
      case (Some(leftShape), Some(rightShape), Some(resultLanes)) =>
        Left(Fault.InvalidRequest(
          s"$opcode type shapes do not match: left $leftShape, right $rightShape, result lanes $resultLanes"
        ))
    }
  })

  private def floating(
      site: Site,
      opcode: String,
      instruction: Instruction,
      resultType: Operand,
      dest: Operand,
      left: Operand,
      right: Operand
  ): Op = lowered(pair(site, left, right, pointerFault(opcode, left)).flatMap { case (leftType, rightType) =>
    (
      site.program.floatingShape(leftType),
      site.program.floatingShape(rightType),
      site.program.booleanLanes(id(resultType))
    ) match {
      case (Some(leftShape @ (_, count)), Some(rightShape), Some(resultLanes))
          if leftShape == rightShape && count == resultLanes =>
        Right(staged(site, instruction, floatingFamilies*))
      case (None, _, _) => Left(Fault.InvalidRequest(s"$opcode left operand %${id(left)} is not floating-point"))
      case (_, None, _) => Left(Fault.InvalidRequest(s"$opcode right operand %${id(right)} is not floating-point"))
      case (_, _, None) =>
        Left(Fault.InvalidRequest(s"$opcode result type %${id(resultType)} is not a boolean scalar or vector"))
      case (Some(leftShape), Some(rightShape), Some(resultLanes)) =>
        Left(Fault.InvalidRequest(
          s"$opcode %${id(dest)} operands %${id(left)}/%${id(right)} type shapes do not match: " +
            s"left $leftShape, right $rightShape, result lanes $resultLanes"
        ))
    }
  })

  private def classify(
      site: Site,
      opcode: String,
      instruction: Instruction,
      resultType: Operand,
      source: Operand
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
      site: Site,
      instruction: Instruction,
      resultType: Operand,
      dest: Operand,
      condition: Operand,
      whenTrue: Operand,
      whenFalse: Operand
  )
      : Op = {
    val program = site.program
    (typed(site, condition), typed(site, whenTrue), typed(site, whenFalse)) match {
      case (Typed.Undefined(_), _, _) | (Typed.Opaque(_, _), _, _) =>
        Fail(Fault.InvalidRequest(s"OpSelect condition %${id(condition)} is not boolean"))
      case (_, Typed.Undefined(value), _)                                                    => Fail(undefined(value))
      case (_, _, Typed.Undefined(value))                                                    => Fail(undefined(value))
      case (Typed.Data(_, conditionType), Typed.Data(_, trueType), Typed.Data(_, falseType)) =>
        (program.booleanLanes(conditionType), program.scalarLeafCount(id(resultType))) match {
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
            transfer(execution.values, if (test != 0L) id(whenTrue) else id(whenFalse), id(dest), id(resultType))
          )
        }
      case _ => Fail(Fault.InvalidRequest(s"OpSelect condition %${id(condition)} is invalid"))
    }
  }
}
