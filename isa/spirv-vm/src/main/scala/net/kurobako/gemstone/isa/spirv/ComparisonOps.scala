package net.kurobako.gemstone.isa.spirv

import LaneOps.*
import Op.*
import VMLowering.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Bits.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*

/** Integer, Boolean and floating-point comparisons, classification and selection. */
private object ComparisonOps {
  import Operand.*

  private inline def flag(value: Boolean): Long = if (value) 1L else 0L

  /** Integer and Boolean comparison kernels by opcode and operand width, shared with `OpSpecConstantOp` folding. Unary
    * kernels ignore their second operand.
    */
  val integerComparisons: Map[Int, (Int in 1 ~ 64) => Kernel2] = {
    def plain(predicate: (Long, Long) => Boolean): (Int in 1 ~ 64) => Kernel2 = _ => (a, b) => flag(predicate(a, b))
    def logical(predicate: (Boolean, Boolean) => Boolean) = plain((a, b) => predicate(a != 0L, b != 0L))
    def unsigned(predicate: Int => Boolean) = plain((a, b) => predicate(java.lang.Long.compareUnsigned(a, b)))
    def signed(predicate: (Long, Long) => Boolean)(width: Int in 1 ~ 64): Kernel2 =
      (a, b) => flag(predicate(signExtend(a, width), signExtend(b, width)))
    Map(
      SPIRV.Opcode.LogicalEqual      -> logical(_ == _),
      SPIRV.Opcode.LogicalNotEqual   -> logical(_ != _),
      SPIRV.Opcode.LogicalOr         -> logical(_ || _),
      SPIRV.Opcode.LogicalAnd        -> logical(_ && _),
      SPIRV.Opcode.LogicalNot        -> logical((a, _) => !a),
      SPIRV.Opcode.IEqual            -> plain(_ == _),
      SPIRV.Opcode.INotEqual         -> plain(_ != _),
      SPIRV.Opcode.ULessThan         -> unsigned(_ < 0),
      SPIRV.Opcode.UGreaterThan      -> unsigned(_ > 0),
      SPIRV.Opcode.ULessThanEqual    -> unsigned(_ <= 0),
      SPIRV.Opcode.UGreaterThanEqual -> unsigned(_ >= 0),
      SPIRV.Opcode.SGreaterThan      -> signed(_ > _),
      SPIRV.Opcode.SGreaterThanEqual -> signed(_ >= _),
      SPIRV.Opcode.SLessThan         -> signed(_ < _),
      SPIRV.Opcode.SLessThanEqual    -> signed(_ <= _)
    )
  }

  def lower(site: Site): PartialFunction[Instruction, Op] = {
    case Instruction.IEqual(t, d, a, b)    => integerEquality(site, "OpIEqual", t, d, a, b, SPIRV.Opcode.IEqual)
    case Instruction.INotEqual(t, d, a, b) => integerEquality(site, "OpINotEqual", t, d, a, b, SPIRV.Opcode.INotEqual)
    case Instruction.LogicalEqual(t, d, a, b) => logical(site, "OpLogicalEqual", t, d, a, b, SPIRV.Opcode.LogicalEqual)
    case Instruction.LogicalNotEqual(t, d, a, b) =>
      logical(site, "OpLogicalNotEqual", t, d, a, b, SPIRV.Opcode.LogicalNotEqual)
    case Instruction.LogicalOr(t, d, a, b)         => logical(site, "OpLogicalOr", t, d, a, b, SPIRV.Opcode.LogicalOr)
    case Instruction.LogicalAnd(t, d, a, b)        => logical(site, "OpLogicalAnd", t, d, a, b, SPIRV.Opcode.LogicalAnd)
    case Instruction.LogicalNot(t, d, a)           => logicalNot(site, t, d, a)
    case Instruction.Any(t, d, a)                  => reduction(site, t, d, a, all = false)
    case Instruction.All(t, d, a)                  => reduction(site, t, d, a, all = true)
    case Instruction.ULessThan(t, d, a, b)         => unsigned(site, t, d, a, b, SPIRV.Opcode.ULessThan)
    case Instruction.UGreaterThan(t, d, a, b)      => unsigned(site, t, d, a, b, SPIRV.Opcode.UGreaterThan)
    case Instruction.ULessThanEqual(t, d, a, b)    => unsigned(site, t, d, a, b, SPIRV.Opcode.ULessThanEqual)
    case Instruction.UGreaterThanEqual(t, d, a, b) => unsigned(site, t, d, a, b, SPIRV.Opcode.UGreaterThanEqual)
    case Instruction.SGreaterThan(t, d, a, b) => signed(site, "OpSGreaterThan", t, d, a, b, SPIRV.Opcode.SGreaterThan)
    case Instruction.SGreaterThanEqual(t, d, a, b) =>
      signed(site, "OpSGreaterThanEqual", t, d, a, b, SPIRV.Opcode.SGreaterThanEqual)
    case Instruction.SLessThan(t, d, a, b)      => signed(site, "OpSLessThan", t, d, a, b, SPIRV.Opcode.SLessThan)
    case Instruction.SLessThanEqual(t, d, a, b) =>
      signed(site, "OpSLessThanEqual", t, d, a, b, SPIRV.Opcode.SLessThanEqual)
    case Instruction.FOrdEqual(t, d, a, b)    => floating(site, "OpFOrdEqual", t, d, a, b)((x, y) => x == y)
    case Instruction.FOrdNotEqual(t, d, a, b) =>
      floating(site, "OpFOrdNotEqual", t, d, a, b)((x, y) => !x.isNaN && !y.isNaN && x != y)
    case Instruction.Ordered(t, d, a, b)     => floating(site, "OpOrdered", t, d, a, b)((x, y) => !x.isNaN && !y.isNaN)
    case Instruction.FUnordEqual(t, d, a, b) =>
      floating(site, "OpFUnordEqual", t, d, a, b)((x, y) => x == y || x.isNaN || y.isNaN)
    case Instruction.FUnordNotEqual(t, d, a, b) => floating(site, "OpFUnordNotEqual", t, d, a, b)((x, y) => x != y)
    case Instruction.FUnordLessThan(t, d, a, b) =>
      floating(site, "OpFUnordLessThan", t, d, a, b)((x, y) => x < y || x.isNaN || y.isNaN)
    case Instruction.FUnordGreaterThan(t, d, a, b) =>
      floating(site, "OpFUnordGreaterThan", t, d, a, b)((x, y) => x > y || x.isNaN || y.isNaN)
    case Instruction.FUnordLessThanEqual(t, d, a, b) =>
      floating(site, "OpFUnordLessThanEqual", t, d, a, b)((x, y) => x <= y || x.isNaN || y.isNaN)
    case Instruction.FUnordGreaterThanEqual(t, d, a, b) =>
      floating(site, "OpFUnordGreaterThanEqual", t, d, a, b)((x, y) => x >= y || x.isNaN || y.isNaN)
    case Instruction.Unordered(t, d, a, b)    => floating(site, "OpUnordered", t, d, a, b)((x, y) => x.isNaN || y.isNaN)
    case Instruction.FOrdLessThan(t, d, a, b) => floating(site, "OpFOrdLessThan", t, d, a, b)(_ < _)
    case Instruction.FOrdLessThanEqual(t, d, a, b)    => floating(site, "OpFOrdLessThanEqual", t, d, a, b)(_ <= _)
    case Instruction.FOrdGreaterThan(t, d, a, b)      => floating(site, "OpFOrdGreaterThan", t, d, a, b)(_ > _)
    case Instruction.FOrdGreaterThanEqual(t, d, a, b) =>
      floating(site, "OpFOrdGreaterThanEqual", t, d, a, b)(_ >= _)
    case Instruction.IsInf(t, d, a) =>
      classify(site, "OpIsInf", t, d, a)((raw, _, exponent, fraction) =>
        (raw & exponent) == exponent && (raw & fraction) == 0L
      )
    case Instruction.IsNan(t, d, a) =>
      classify(site, "OpIsNan", t, d, a)((raw, _, exponent, fraction) =>
        (raw & exponent) == exponent && (raw & fraction) != 0L
      )
    case Instruction.IsFinite(t, d, a) =>
      classify(site, "OpIsFinite", t, d, a)((raw, _, exponent, _) => (raw & exponent) != exponent)
    case Instruction.IsNormal(t, d, a) =>
      classify(site, "OpIsNormal", t, d, a)((raw, _, exponent, _) =>
        (raw & exponent) != exponent && (raw & exponent) != 0L
      )
    case Instruction.SignBitSet(t, d, a) =>
      classify(site, "OpSignBitSet", t, d, a)((raw, sign, _, _) => (raw & sign) != 0L)
    case Instruction.Select(t, d, c, a, b) =>
      select(site, resultType = t, dest = d, condition = c, whenTrue = a, whenFalse = b)
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
      dest: Operand,
      left: Operand,
      right: Operand,
      code: Int
  )
      : Op = lowered(pair(site, left, right, pointerFault(opcode, left)).flatMap { case (leftType, rightType) =>
    (
      site.program.integerShape(leftType),
      site.program.integerShape(rightType),
      site.program.booleanLanes(id(resultType))
    ) match {
      case (Some(leftShape), Some(rightShape), Some(resultLanes))
          if leftShape == rightShape && leftShape._2 == resultLanes =>
        Right(binary(site, id(resultType), id(dest), id(left), id(right))(integerComparisons(code)(leftShape._1)))
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
      dest: Operand,
      left: Operand,
      right: Operand,
      code: Int
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
          Right(binary(site, id(resultType), id(dest), id(left), id(right))(integerComparisons(code)(1)))
        case _ => Left(mismatch)
      }
    })
  }

  private def logicalNot(site: Site, resultType: Operand, dest: Operand, source: Operand): Op =
    typed(site, source) match {
      case Typed.Undefined(value) => Fail(undefined(value))
      case Typed.Data(_, sourceType)
          if site.program.booleanLanes(sourceType).isDefined &&
            site.program.booleanLanes(sourceType) == site.program.booleanLanes(id(resultType)) =>
        val kernel = integerComparisons(SPIRV.Opcode.LogicalNot)(1)
        unary(site, id(resultType), id(dest), id(source))(kernel(_, 0L))
      case _ => Fail(Fault.InvalidRequest("OpLogicalNot requires matching boolean operand and result types"))
    }

  private def reduction(site: Site, resultType: Operand, dest: Operand, source: Operand, all: Boolean): Op = {
    val name = if (all) "All" else "Any"
    typed(site, source) match {
      case Typed.Undefined(value)    => Fail(undefined(value))
      case Typed.Opaque(_, _)        => Fail(Fault.InvalidRequest(s"Op$name source is a pointer"))
      case Typed.Data(_, sourceType) =>
        (site.program.booleanLanes(sourceType), site.program.booleanLanes(id(resultType))) match {
          case (Some(_), Some(1)) =>
            step { (execution, _) =>
              Option(execution.values.lanes(id(source))).toRight(undefined(id(source))).map { lanes =>
                execution.values.updateScalar(
                  id(dest),
                  id(resultType),
                  flag(if (all) !lanes.contains(0L) else lanes.exists(_ != 0L))
                )
              }
            }
          case _ => Fail(Fault.InvalidRequest(s"Op$name requires a boolean vector"))
        }
    }
  }

  // Unsigned comparisons broadcast operands without checking their shapes.
  private def unsigned(site: Site, resultType: Operand, dest: Operand, left: Operand, right: Operand, code: Int): Op =
    lowered(pair(site, left, right, value => Fault.InvalidRequest(s"SPIR-V value %${value.id} is not numeric")).map { _ =>
      binary(site, id(resultType), id(dest), id(left), id(right))(integerComparisons(code)(64))
    })

  private def signed(
      site: Site,
      opcode: String,
      resultType: Operand,
      dest: Operand,
      left: Operand,
      right: Operand,
      code: Int
  )
      : Op = lowered(pair(site, left, right, pointerFault(opcode, left)).flatMap { case (leftType, rightType) =>
    (
      site.program.integerShape(leftType),
      site.program.integerShape(rightType),
      site.program.booleanLanes(id(resultType))
    ) match {
      case (Some((width, count)), Some((rightWidth, rightCount)), Some(resultCount))
          if width == rightWidth && count == rightCount && count == resultCount =>
        Right(binary(site, id(resultType), id(dest), id(left), id(right))(integerComparisons(code)(width)))
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

  private def floating(site: Site, opcode: String, resultType: Operand, dest: Operand, left: Operand, right: Operand)(
      predicate: (Double, Double) => Boolean
  ): Op = lowered(pair(site, left, right, pointerFault(opcode, left)).flatMap { case (leftType, rightType) =>
    (
      site.program.floatingShape(leftType),
      site.program.floatingShape(rightType),
      site.program.booleanLanes(id(resultType))
    ) match {
      case (Some(leftShape @ (width, count)), Some(rightShape), Some(resultLanes))
          if leftShape == rightShape && count == resultLanes =>
        Right(binary(site, id(resultType), id(dest), id(left), id(right))((a, b) =>
          flag(predicate(IEEE754.toDouble(a, width), IEEE754.toDouble(b, width)))
        ))
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

  private def classify(site: Site, opcode: String, resultType: Operand, dest: Operand, source: Operand)(
      predicate: (Long, Long, Long, Long) => Boolean
  ): Op = typed(site, source) match {
    case Typed.Undefined(value)    => Fail(undefined(value))
    case Typed.Opaque(_, _)        => Fail(Fault.InvalidRequest(s"$opcode source is a pointer"))
    case Typed.Data(_, sourceType) =>
      (site.program.floatingShape(sourceType), site.program.booleanLanes(id(resultType))) match {
        case (Some((width, sourceLanes)), Some(resultLanes)) if sourceLanes == resultLanes =>
          val sign     = 1L << (width.raw - 1)
          val fraction =
            (1L <<
              (width.raw match {
                case 16 => 10
                case 32 => 23
                case _  => 52
              })) - 1L
          val exponent = (sign - 1L) & ~fraction
          unary(site, id(resultType), id(dest), id(source))(raw => flag(predicate(raw, sign, exponent, fraction)))
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
              ternary(
                site,
                resultType = id(resultType),
                dest = id(dest),
                a = id(condition),
                b = id(whenTrue),
                c = id(whenFalse)
              )((c, a, b) =>
                if (c != 0L) a else b
              )
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
