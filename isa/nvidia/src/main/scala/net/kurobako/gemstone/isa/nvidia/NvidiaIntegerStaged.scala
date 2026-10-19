package net.kurobako.gemstone.isa.nvidia

import IntegerSemantics.*
import MemorySemantics.*
import VMIR.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*

/** The staged semantics of integer arithmetic, carries and bit manipulation. */
private[isa] object NvidiaIntegerStaged {

  inline def bitwise[V, R](inline algebra: Algebra[V], instruction: Instruction)(inline sinks: NvidiaSinks[V, R])(
      inline next: Instruction => R
  ): R = instruction match {
    case Instruction.BitFind(destination, source, width, signed, shiftAmount) =>
      IntegerSemantics.bitFind(algebra, bits(width), signed, shiftAmount)(sinks, destination, source)
    case Instruction.DotProduct4(destination, left, right, addend, leftSigned, rightSigned) =>
      IntegerSemantics.dotProduct4(algebra, leftSigned, rightSigned)(sinks, destination, left, right, addend)
    case Instruction.BitFieldExtract(destination, source, start, count, width, signed) =>
      IntegerSemantics.bitFieldExtract(algebra, bits(width), signed)(
        sinks,
        destination,
        source,
        start,
        count
      )
    case Instruction.BitFieldInsert(destination, insert, base, start, count, width) =>
      IntegerSemantics.bitFieldInsert(algebra, bits(width))(
        sinks = sinks,
        destination = destination,
        first = insert,
        second = base,
        third = start,
        fourth = count
      )
    case Instruction.BitMask(destination, start, count, mode) =>
      algebra.bitMask(mode)(sinks, destination, start, count)
    case Instruction.Convert(destination, source, sourceFormat, destinationFormat, rounding, clamp) =>
      IntegerSemantics.convert(algebra, sourceFormat, destinationFormat, rounding, clamp)(sinks, destination, source)
    case Instruction.ConvertTF32(destination, source, away, towardZero, relu, saturate) =>
      IntegerSemantics.convertTF32(algebra, away, towardZero, relu, saturate)(sinks, destination, source)
    case _ => next(instruction)
  }

  inline def carries[V, R](inline algebra: Algebra[V], instruction: Instruction)(inline sinks: NvidiaSinks[V, R])(
      inline next: Instruction => R
  ): R = instruction match {
    case Instruction.MultiplyAdd(
          destination,
          left,
          right,
          addend,
          IntegerMultiplyAddOperation.Low(operand, result, carryIn, carryOut)
        ) =>
      IntegerSemantics.multiplyAddLowCarry(algebra, bits(operand), bits(result), carryIn, carryOut)(
        sinks,
        destination,
        left,
        right,
        addend
      )
    case Instruction.MultiplyAdd(
          destination,
          left,
          right,
          addend,
          IntegerMultiplyAddOperation.High(operand, result, carryOut)
        ) =>
      IntegerSemantics.multiplyAddHigh(algebra, bits(operand), bits(result), carryOut)(
        sinks,
        destination,
        left,
        right,
        addend
      )
    case Instruction.MultiplyAdd(
          destination,
          left,
          right,
          addend,
          IntegerMultiplyAddOperation.HighSum(width, signed, saturate)
        ) =>
      IntegerSemantics.multiplyAddHighSum(
        algebra,
        bits(width),
        signed,
        saturate
      )(sinks, destination, left, right, addend)
    case Instruction.MultiplyAdd(
          destination,
          left,
          right,
          addend,
          IntegerMultiplyAddOperation.Wide(operand, signed)
        ) =>
      IntegerSemantics.multiplyAddWide(algebra, operand.widen[Int in 1 ~ 64], signed)(
        sinks,
        destination,
        left,
        right,
        addend
      )
    case Instruction.MultiplyAdd(
          destination,
          left,
          right,
          addend,
          IntegerMultiplyAddOperation.Narrow(high, signed, saturate)
        ) =>
      IntegerSemantics.multiplyAddNarrow(algebra, high, signed, saturate)(sinks, destination, left, right, addend)
    case Instruction.IntegerAddWithCarry(destination, left, right, width, carryIn, carryOut) =>
      IntegerSemantics.addWithCarry(algebra, bits(width), carryIn, carryOut)(sinks, destination, left, right)
    case Instruction.IntegerSubtractWithBorrow(destination, left, right, width, borrowIn, borrowOut) =>
      IntegerSemantics.subtractWithBorrow(algebra, bits(width), borrowIn, borrowOut)(sinks, destination, left, right)
    case Instruction.IntegerAddThree(
          destination,
          first,
          second,
          third,
          width,
          mode,
          negateFirst,
          negateSecond,
          negateThird
        ) =>
      IntegerSemantics.addThreeCarry(
        algebra,
        bits(width),
        produce = mode == AddThreeCarryMode.Produce,
        negateFirst = negateFirst,
        negateSecond = negateSecond,
        negateThird = negateThird
      )(sinks, destination, first, second, third)
    case _ => next(instruction)
  }

  inline def integer[V, R](inline algebra: Algebra[V], instruction: Instruction)(inline sinks: NvidiaSinks[V, R])(
      inline next: Instruction => R
  ): R = instruction match {
    case Instruction.Move(destination, source, width) =>
      IntegerSemantics.move(algebra, bits(width))(sinks, destination, source)
    case Instruction.IntegerUnary(operation, destination, source, width) =>
      IntegerSemantics.unary(algebra, operation, bits(width))(sinks, destination, source)
    case Instruction.IntegerBinary(operation, destination, left, right, width) =>
      IntegerSemantics.binary(algebra, operation, bits(width))(sinks, destination, left, right)
    case Instruction.MultiplyAdd(
          destination,
          left,
          right,
          addend,
          IntegerMultiplyAddOperation.Low(operand, result, false, false)
        ) =>
      IntegerSemantics.multiplyAddLow(algebra, bits(operand), bits(result))(sinks, destination, left, right, addend)
    case Instruction.IntegerAddThree(
          destination,
          first,
          second,
          third,
          width,
          AddThreeCarryMode.None,
          negateFirst,
          negateSecond,
          negateThird
        ) =>
      IntegerSemantics.addThree(
        algebra,
        bits(width),
        negateFirst = negateFirst,
        negateSecond = negateSecond,
        negateThird = negateThird
      )(sinks, destination, first, second, third)
    case Instruction.IntegerSubtractAdd(destination, left, right, addend, width) =>
      IntegerSemantics.subtractAdd(algebra, bits(width))(sinks, destination, left, right, addend)
    case Instruction.Logic3(destination, first, second, third, table, width) =>
      IntegerSemantics.logic3(algebra, table, bits(width))(sinks, destination, first, second, third)
    case Instruction.FunnelShift(destination, low, high, amount, left, clamp) =>
      IntegerSemantics.funnel(algebra, left, clamp)(sinks, destination, low, high, amount)
    case _ => next(instruction)
  }
}
