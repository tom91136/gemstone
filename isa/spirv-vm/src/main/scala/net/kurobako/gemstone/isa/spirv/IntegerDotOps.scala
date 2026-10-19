package net.kurobako.gemstone.isa.spirv

import Op.*
import OpBuilder.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*

/** Integer dot products of SPV_KHR_integer_dot_product, staged from [[IntegerDotSemantics]]. Accumulating vector
  * products whose partial sums may leave 64 bits sum exactly in arbitrary precision here.
  */
private object IntegerDotOps {
  import Operand.*

  def lower(site: Site): PartialFunction[Instruction, Op] = {
    case instruction @ Instruction.SDot(resultType = t, vector1 = a, packedVectorFormat = packed) =>
      dot(site, instruction, t, a, None, packed)
    case instruction @ Instruction.UDot(resultType = t, vector1 = a, packedVectorFormat = packed) =>
      dot(site, instruction, t, a, None, packed)
    case instruction @ Instruction.SUDot(resultType = t, vector1 = a, packedVectorFormat = packed) =>
      dot(site, instruction, t, a, None, packed)
    case instruction @ Instruction.SDotAccSat(
          resultType = t,
          vector1 = a,
          accumulator = acc,
          packedVectorFormat = packed
        ) =>
      dot(site, instruction, t, a, Some(acc), packed)
    case instruction @ Instruction.UDotAccSat(
          resultType = t,
          vector1 = a,
          accumulator = acc,
          packedVectorFormat = packed
        ) =>
      dot(site, instruction, t, a, Some(acc), packed)
    case instruction @ Instruction.SUDotAccSat(
          resultType = t,
          vector1 = a,
          accumulator = acc,
          packedVectorFormat = packed
        ) =>
      dot(site, instruction, t, a, Some(acc), packed)
  }

  private def dot(
      site: Site,
      instruction: Instruction,
      resultTypeOperand: Operand,
      firstOperand: Operand,
      accumulator: Option[Operand],
      packed: Option[Operand]
  ): Op = {
    val program    = site.program
    val opcode     = instruction.opcodeNumber.toInt
    val ids        = SPIRVStaged.operandIds(instruction)
    val resultType = id(resultTypeOperand)
    val first      = id(firstOperand)
    lowered(for {
      result <- program.integerShape(resultType).map(_._1)
        .toRight(Fault.InvalidRequest(s"integer dot product result %$resultType is not an integer"))
      component <-
        if (packed.isDefined) Right((8: Int in 1 ~ 64) -> 1)
        else program.integerShape(program.valueType(first))
          .toRight(Fault.InvalidRequest(s"integer dot product operand %$first is not an integer vector"))
    } yield (packed, accumulator.map(id(_))) match {
      case (None, Some(addend)) if !IntegerDotSemantics.exact(component._1, component._2) =>
        wide(
          resultType = resultType,
          dest = ids.result,
          first = first,
          second = ids(1),
          addend = addend,
          component = component._1,
          result = result,
          opcode = opcode
        )
      case _ =>
        staged(site, instruction, CoreFamily.IntegerDot)
    })
  }

  private val Unsigned64 = BigInt(1) << 64

  /** The low `width` bits of `value` as an exact integer. */
  private def exact(value: Long, width: Int in 1 ~ 64, signed: Boolean): BigInt =
    if (signed) BigInt(Bits.signExtend(value, width))
    else {
      val bits = Bits.truncate(value, width)
      if (bits < 0L) BigInt(bits) + Unsigned64 else BigInt(bits)
    }

  /** An accumulating vector product summed exactly, for components too wide for a 64-bit sum. */
  private def wide(
      resultType: Int,
      dest: Int,
      first: Int,
      second: Int,
      addend: Int,
      component: Int in 1 ~ 64,
      result: Int in 1 ~ 64,
      opcode: Int
  ): Op = {
    val signed     = opcode != SPIRV.Opcode.UDotAccSat
    val secondSign = opcode == SPIRV.Opcode.SDotAccSat
    step { (execution, _) =>
      val values = execution.values
      for {
        a     <- bits(values, first)
        b     <- bits(values, second)
        start <- scalar(values, addend)
      } yield {
        val sum = a.indices.foldLeft(exact(start, result, signed))((sum, lane) =>
          sum + exact(a(lane), component, signed) * exact(b(lane), component, secondSign)
        )
        values.updateScalar(dest, resultType, IntegerArithmetic.clampToWidth(sum, result, signed))
      }
    }
  }
}
