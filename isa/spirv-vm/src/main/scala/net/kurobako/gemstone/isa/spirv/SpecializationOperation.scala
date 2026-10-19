package net.kurobako.gemstone.isa.spirv

import VMProgram.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Bits.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.semantics.*
import scala.collection.mutable

/** Evaluates `OpSpecConstantOp` over constant operands at load time. */
private object SpecializationOperation {
  def evaluate(
      types: mutable.Map[Int, RuntimeType],
      constants: mutable.Map[Int, Constant],
      resultType: Int,
      opcode: Long,
      operands: Vec[Long]
  ): Option[Constant] = {
    def source(index: Int): Option[(Int, Vec[Long])] = operands.lift(index).flatMap { value =>
      constants.get(Math.toIntExact(value)).collect { case Constant.Bits(t, lanes) => (t, lanes) }
    }
    def shape(t: Int): Option[(RuntimeType, Int)] = types.get(t).flatMap {
      case RuntimeType.VectorType(component, count) => types.get(component).map((_, count))
      case scalar @ (RuntimeType.Bool | RuntimeType.Integer(_, _) | RuntimeType.Floating(_) | RuntimeType.Float8(_)) =>
        Some((scalar, 1))
      case _ => None
    }
    def integer(t: Int): Option[(Int in 1 ~ 64, Int)] = shape(t).collect { case (RuntimeType.Integer(width, _), n) =>
      (width, n)
    }
    def floating(t: Int): Option[(Int in (16, 32, 64), Int)] =
      shape(t).collect { case (RuntimeType.Floating(width), n) => (width, n) }
    def boolean(t: Int): Option[Int] = shape(t).collect { case (RuntimeType.Bool, n) => n }
    def lanes(a: Vec[Long], b: Vec[Long], kernel: LaneOps.Kernel2): Vec[Long] =
      Vec.tabulate(a.size)(i => kernel(a(i), b(i)))
    def result(lanes: Vec[Long]): Option[Constant] =
      scalarLeafCount(types, resultType).toOption.filter(_.raw == lanes.size).map(_ => Constant.Bits(resultType, lanes))
    def component(t: Int, indices: Vec[Long]): Option[(Int, Int)] =
      if (indices.isEmpty) Some((t, 0))
      else {
        val index = indices.head

        def homogeneous(element: Int, count: Long) =
          if (index >= 0 && index < count)
            scalarLeafCount(types, element).toOption.map(n => (element, Math.toIntExact(index * n)))
          else None
        val child = types.get(t) match {
          case Some(RuntimeType.Structure(members)) if index >= 0 && index < members.size =>
            val preceding = members.take(index.toInt).map(m => scalarLeafCount(types, m).toOption)
            if (preceding.forall(_.isDefined))
              Some((members(index.toInt), preceding.flatten.foldLeft(0: Int >= 0)(_ + _).raw))
            else None
          case Some(RuntimeType.VectorType(element, count)) => homogeneous(element, count.raw)
          case Some(RuntimeType.MatrixType(element, count)) => homogeneous(element, count.raw)
          case Some(RuntimeType.Array(element, count))      => homogeneous(element, count)
          case _                                            => None
        }
        child.flatMap { case (element, offset) =>
          component(element, indices.drop(1)).map { case (leaf, inner) => (leaf, offset + inner) }
        }
      }
    opcode match {
      case SPIRV.Opcode.CompositeExtract if operands.size >= 2 =>
        for {
          composite <- source(0)
          selected  <- component(composite._1, operands.drop(1))
          if selected._1 == resultType
          count <- scalarLeafCount(types, resultType).toOption
          value <- result(composite._2.slice(selected._2, selected._2 + count))
        } yield value
      case SPIRV.Opcode.CompositeInsert if operands.size >= 3 =>
        for {
          inserted  <- source(0)
          composite <- source(1)
          if composite._1 == resultType
          selected <- component(resultType, operands.drop(2))
          if selected._1 == inserted._1
          value <-
            result(composite._2.take(selected._2) ++ inserted._2 ++ composite._2.drop(selected._2 + inserted._2.size))
        } yield value
      case SPIRV.Opcode.VectorShuffle if operands.size >= 3 =>
        for {
          a        <- source(0)
          b        <- source(1)
          aShape   <- shape(a._1)
          bShape   <- shape(b._1)
          outShape <- shape(resultType)
          if aShape._1 == bShape._1 && aShape._1 == outShape._1
          joined  = a._2 ++ b._2
          indices = operands.drop(2)
          if indices.forall(i => i == 0xffffffffL || (i >= 0 && i < joined.size))
          value <- result(indices.map(i => if (i == 0xffffffffL) 0L else joined(i.toInt)))
        } yield value
      case SPIRV.Opcode.Select if operands.size == 3 =>
        for {
          condition <- source(0)
          if shape(condition._1).exists(_._1 == RuntimeType.Bool)
          a <- source(1)
          b <- source(2)
          if a._1 == resultType && b._1 == resultType && a._2.size == b._2.size
          if condition._2.size == 1 || condition._2.size == a._2.size
          value <- result(Vec.tabulate(a._2.size)(i =>
            if (condition._2(if (condition._2.size == 1) 0 else i) != 0) a._2(i) else b._2(i)
          ))
        } yield value
      case SPIRV.Opcode.LogicalNot | SPIRV.Opcode.SNegate | SPIRV.Opcode.Not if operands.size == 1 =>
        for {
          a     <- source(0)
          value <-
            if (opcode == SPIRV.Opcode.LogicalNot)
              boolean(a._1).filter(boolean(resultType).contains).flatMap(_ =>
                ComparisonOps.comparisonKernel(SPIRV.Opcode.LogicalNot, 1).flatMap(kernel =>
                  result(lanes(a._2, a._2, kernel))
                )
              )
            else integer(a._1).filter(integer(resultType).contains).flatMap { case (width, _) =>
              ArithmeticOps.integerKernel(opcode.toInt, width).flatMap(kernel => result(lanes(a._2, a._2, kernel)))
            }
        } yield value
      case op if operands.size == 2 && ComparisonOps.comparisonKernel(op.toInt, 1).nonEmpty =>
        for {
          a           <- source(0)
          b           <- source(1)
          resultLanes <- boolean(resultType)
          width       <- (integer(a._1), integer(b._1)) match {
            case (Some(left @ (width, count)), Some(right)) if left == right && count == resultLanes => Some(width)
            case (None, None) if boolean(a._1).contains(resultLanes) && boolean(b._1).contains(resultLanes) =>
              Some(1: Int in 1 ~ 64)
            case _ => None
          }
          if (op <= SPIRV.Opcode.LogicalNot) == (width == 1 && integer(a._1).isEmpty)
          kernel <- ComparisonOps.comparisonKernel(op.toInt, width)
          value  <- result(lanes(a._2, b._2, kernel))
        } yield value
      case op if operands.size == 2 && ArithmeticOps.integerKernel(op.toInt, 64).nonEmpty =>
        val shifts = op >= SPIRV.Opcode.ShiftRightLogical && op <= SPIRV.Opcode.ShiftLeftLogical
        for {
          a              <- source(0)
          b              <- source(1)
          (width, count) <- integer(resultType)
          if integer(a._1).contains((width, count))
          amount <- integer(b._1).filter((bWidth, bCount) => bCount == count && (shifts || bWidth == width))
          kernel <- ArithmeticOps.integerKernel(op.toInt, width)
          value  <- result(lanes(a._2, b._2.map(_ & mask(amount._1)), kernel))
        } yield value
      case SPIRV.Opcode.UConvert | SPIRV.Opcode.SConvert if operands.size == 1 =>
        for {
          a         <- source(0)
          (from, _) <- integer(a._1)
          (to, _)   <- integer(resultType)
          value     <- result(lanes(
            a._2,
            a._2,
            ConversionSemantics.integer(Execute, ConstantFolder, opcode.toInt, from, to)(OperandIds.none)(
              throw IllegalArgumentException(s"opcode $opcode has no conversion semantics")
            )
          ))
        } yield value
      case SPIRV.Opcode.FConvert if operands.size == 1 =>
        for {
          a         <- source(0)
          (from, _) <- floating(a._1)
          (to, _)   <- floating(resultType)
          value     <- result(lanes(
            a._2,
            a._2,
            ConversionSemantics.toFloatingWidth(Execute, ConstantFolder, from, to, IEEE754.Rounding.NearestEven)(
              OperandIds.none
            )
          ))
        } yield value
      case SPIRV.Opcode.QuantizeToF16 if operands.size == 1 =>
        for {
          a <- source(0)
          if a._1 == resultType && floating(resultType).exists(_._1 == 32)
          value <- result(lanes(a._2, a._2, ConversionSemantics.quantize(Execute, ConstantFolder)(OperandIds.none)))
        } yield value
      case _ => None
    }
  }
}
