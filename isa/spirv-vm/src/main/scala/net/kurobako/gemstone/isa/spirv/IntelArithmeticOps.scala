package net.kurobako.gemstone.isa.spirv

import LaneOps.*
import Op.*
import OpBuilder.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*

/** Vendor arithmetic staged from [[IntelSemantics]] and [[MixedDotSemantics]]: SPV_INTEL_shader_integer_functions2,
  * SPV_INTEL_ternary_bitwise_function, the bfloat16 and TF32 conversions, and SPV_VALVE_mixed_float_dot_product.
  */
private object IntelArithmeticOps {
  import Operand.*

  private enum Format derives CanEqual {
    case Integer, Floating
  }

  def lower(site: Site): PartialFunction[Instruction, Op] = {
    case instruction @ Instruction.UCountLeadingZerosINTEL(t, _, a)  => integer(site, instruction, t, Vec(a))
    case instruction @ Instruction.UCountTrailingZerosINTEL(t, _, a) => integer(site, instruction, t, Vec(a))
    case instruction @ Instruction.AbsISubINTEL(t, _, a, b)          => integer(site, instruction, t, Vec(a, b))
    case instruction @ Instruction.AbsUSubINTEL(t, _, a, b)          => integer(site, instruction, t, Vec(a, b))
    case instruction @ Instruction.IAddSatINTEL(t, _, a, b)          => integer(site, instruction, t, Vec(a, b))
    case instruction @ Instruction.UAddSatINTEL(t, _, a, b)          => integer(site, instruction, t, Vec(a, b))
    case instruction @ Instruction.ISubSatINTEL(t, _, a, b)          => integer(site, instruction, t, Vec(a, b))
    case instruction @ Instruction.USubSatINTEL(t, _, a, b)          => integer(site, instruction, t, Vec(a, b))
    case instruction @ Instruction.IAverageINTEL(t, _, a, b)         => integer(site, instruction, t, Vec(a, b))
    case instruction @ Instruction.UAverageINTEL(t, _, a, b)         => integer(site, instruction, t, Vec(a, b))
    case instruction @ Instruction.IAverageRoundedINTEL(t, _, a, b)  => integer(site, instruction, t, Vec(a, b))
    case instruction @ Instruction.UAverageRoundedINTEL(t, _, a, b)  => integer(site, instruction, t, Vec(a, b))
    case instruction @ Instruction.IMul32x16INTEL(t, _, a, b)        => integer(site, instruction, t, Vec(a, b))
    case instruction @ Instruction.UMul32x16INTEL(t, _, a, b)        => integer(site, instruction, t, Vec(a, b))
    case instruction @ Instruction.BitwiseFunctionINTEL(t, _, a, b, c, table) =>
      lowered(
        shapes(site, instruction.opcode.raw, t, Vec(a, b, c))(site.program.integerShape).flatMap(_ =>
          Either.cond(
            site.program.integerShape(site.program.valueType(id(table))).exists(_._2 == 1),
            staged(site, instruction, CoreFamily.IntelInteger),
            Fault.InvalidRequest(s"${instruction.opcode} LUTIndex %${id(table)} is not an integer scalar")
          )
        )
      )
    case instruction @ Instruction.ConvertFToBF16INTEL(t, _, a) =>
      conversion(site, instruction, a, source => site.program.floatingShape(source).map(_._2), 16, Format.Integer, t)
    case instruction @ Instruction.ConvertBF16ToFINTEL(t, _, a) => conversion(
        site,
        instruction,
        a,
        source => site.program.integerShape(source).collect { case (width, lanes) if width == 16 => lanes },
        32,
        Format.Floating,
        t
      )
    case instruction @ Instruction.RoundFToTF32INTEL(t, _, a) => conversion(
        site,
        instruction,
        a,
        source => site.program.floatingShape(source).collect { case (width, lanes) if width == 32 => lanes },
        32,
        Format.Floating,
        t
      )
    case instruction @ Instruction.FDot2MixAcc32VALVE(t, _, a, b, accumulator) =>
      mixed(site, instruction, t, a, b, accumulator, 32)
    case instruction @ Instruction.FDot2MixAcc16VALVE(t, _, a, b, accumulator) =>
      mixed(site, instruction, t, a, b, accumulator, 16)
    case instruction @ Instruction.FDot4MixAcc32VALVE(t, _, a, b, accumulator) =>
      val program = site.program
      val vectors = Vec(a, b).forall(operand =>
        program.float8Shape(program.valueType(id(operand))).exists(_._2 == 4)
      )
      val scalar = program.floatingShape(id(t)).exists(shape => shape._1 == 32 && shape._2 == 1)
      if (vectors && scalar && program.valueType(id(accumulator)) == id(t))
        staged(site, instruction, CoreFamily.MixedDot)
      else
        Fail(Fault.InvalidRequest(
          s"${instruction.opcode} takes two four-lane 8-bit float vectors and a binary32 accumulator of its result type"
        ))
  }

  /** The operands' data types, which must all have the result type's shape. */
  private inline def shapes(site: Site, opcode: String, resultType: Operand, operands: Vec[Operand])(
      inline shape: Int => Option[(Int in 1 ~ 64, Int)]
  ): Either[Fault, (Int in 1 ~ 64, Int)] = {
    val expected = shape(id(resultType))
    operands.foldLeft(expected.toRight(Fault.InvalidRequest(s"$opcode result type %${id(resultType)} has no lanes"))) {
      (result, operand) =>
        result.flatMap(found =>
          typed(site, operand) match {
            case Typed.Undefined(value)                                 => Left(undefined(value))
            case Typed.Data(_, typeId) if shape(typeId).contains(found) => Right(found)
            case _ => Left(Fault.InvalidRequest(s"$opcode operand %${id(operand)} does not match its result type"))
          }
        )
    }
  }

  private def integer(site: Site, instruction: Instruction, resultType: Operand, operands: Vec[Operand]): Op =
    lowered(
      shapes(site, instruction.opcode.raw, resultType, operands)(site.program.integerShape).map(_ =>
        staged(site, instruction, CoreFamily.IntelInteger)
      )
    )

  /** A lane-wise conversion from a source of `lanes` lanes into `width`-bit integer or floating-point lanes. */
  private inline def conversion(
      site: Site,
      instruction: Instruction,
      operand: Operand,
      inline lanes: Int => Option[Int],
      width: Int,
      format: Format,
      resultType: Operand
  ): Op = {
    val result = format match {
      case Format.Integer =>
        site.program.integerShape(id(resultType)).collect { case (bits, count) if bits.raw == width => count }
      case Format.Floating =>
        site.program.floatingShape(id(resultType)).collect { case (bits, count) if bits.raw == width => count }
    }
    typed(site, operand) match {
      case Typed.Undefined(value)                                              => Fail(undefined(value))
      case Typed.Data(_, typeId) if result.nonEmpty && lanes(typeId) == result =>
        staged(site, instruction, CoreFamily.IntelConversion)
      case _ =>
        Fail(Fault.InvalidRequest(s"${instruction.opcode} operand %${id(operand)} does not convert to its result"))
    }
  }

  /** A dot product of two-lane binary16 vectors accumulated at the result's `width`. */
  private def mixed(
      site: Site,
      instruction: Instruction,
      resultType: Operand,
      a: Operand,
      b: Operand,
      accumulator: Operand,
      width: Int
  ): Op = {
    val program = site.program
    val vectors = Vec(a, b).forall(operand =>
      program.floatingShape(program.valueType(id(operand))).exists(shape =>
        shape._1 == 16 && shape._2 == 2
      )
    )
    val scalar = program.floatingShape(id(resultType)).exists(shape => shape._1.raw == width && shape._2 == 1)
    if (vectors && scalar && program.valueType(id(accumulator)) == id(resultType))
      staged(site, instruction, CoreFamily.MixedDot)
    else
      Fail(Fault.InvalidRequest(
        s"${instruction.opcode} takes two binary16 two-lane vectors and a $width-bit accumulator of its result type"
      ))
  }
}
