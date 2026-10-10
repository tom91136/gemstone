package net.kurobako.gemstone.isa.spirv

import Op.*
import VMLowering.*
import net.kurobako.gemstone.vm.*

/** Integer dot products of SPV_KHR_integer_dot_product. Plain products wrap to the result width; accumulating ones add
  * the exact product to the accumulator and saturate.
  */
private object IntegerDotOps {
  import Operand.*

  private enum Signedness(val first: Boolean, val second: Boolean) {
    case Signed   extends Signedness(true, true)
    case Unsigned extends Signedness(false, false)
    case Mixed    extends Signedness(true, false)
  }

  def lower(site: Site): PartialFunction[Instruction, Op] = {
    case Instruction.SDot(t, d, a, b, packed) => dot(site, id(t), id(d), id(a), id(b), None, packed, Signedness.Signed)
    case Instruction.UDot(t, d, a, b, packed) =>
      dot(site, id(t), id(d), id(a), id(b), None, packed, Signedness.Unsigned)
    case Instruction.SUDot(t, d, a, b, packed) => dot(site, id(t), id(d), id(a), id(b), None, packed, Signedness.Mixed)
    case Instruction.SDotAccSat(t, d, a, b, acc, packed) =>
      dot(site, id(t), id(d), id(a), id(b), Some(id(acc)), packed, Signedness.Signed)
    case Instruction.UDotAccSat(t, d, a, b, acc, packed) =>
      dot(site, id(t), id(d), id(a), id(b), Some(id(acc)), packed, Signedness.Unsigned)
    case Instruction.SUDotAccSat(t, d, a, b, acc, packed) =>
      dot(site, id(t), id(d), id(a), id(b), Some(id(acc)), packed, Signedness.Mixed)
  }

  private def extend(value: Long, width: Int, signed: Boolean): Long =
    if (width == 64) value
    else if (signed) (value << (64 - width)) >> (64 - width)
    else value & ((1L << width) - 1L)

  private def dot(
      site: Site,
      resultType: Int,
      dest: Int,
      first: Int,
      second: Int,
      accumulator: Option[Int],
      packed: Option[Operand],
      signedness: Signedness
  ): Op = {
    val program = site.program
    lowered(for {
      result <- program.integerShape(resultType).map(_._1.raw)
        .toRight(Fault.InvalidRequest(s"integer dot product result %$resultType is not an integer"))
      component <-
        if (packed.isDefined) Right(8)
        else program.integerShape(program.valueType(first)).map(_._1.raw)
          .toRight(Fault.InvalidRequest(s"integer dot product operand %$first is not an integer vector"))
    } yield {
      def lanes(id: Int, signed: Boolean, values: VMInvocation.InvocationValues): Either[Fault, Array[Long]] =
        bits(values, id).map(words =>
          if (packed.isDefined) Array.tabulate(4)(lane => extend(words(0) >>> (lane * 8), 8, signed))
          else words.map(extend(_, component, signed))
        )
      step { (execution, _) =>
        val values = execution.values
        for {
          a     <- lanes(first, signedness.first, values)
          b     <- lanes(second, signedness.second, values)
          added <- accumulator.fold[Either[Fault, Option[Long]]](Right(None))(scalar(values, _).map(Some(_)))
        } yield {
          val value = added match {
            case None        => a.indices.foldLeft(0L)((sum, lane) => sum + a(lane) * b(lane))
            case Some(start) =>
              val signed = signedness != Signedness.Unsigned
              val exact  = a.indices.foldLeft(BigInt(extend(start, result, signed)))((sum, lane) =>
                sum + BigInt(a(lane)) * BigInt(b(lane))
              )
              val (low, high) =
                if (signed) (-(BigInt(1) << (result - 1)), (BigInt(1) << (result - 1)) - 1)
                else (BigInt(0), (BigInt(1) << result) - 1)
              exact.max(low).min(high).toLong
          }
          values.updateScalar(dest, resultType, extend(value, result, signed = false))
        }
      }
    })
  }
}
