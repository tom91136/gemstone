package net.kurobako.gemstone.isa.nvidia

import MemorySemantics.*
import VMIR.*
import net.kurobako.gemstone.semantics.*

/** The staged semantics of byte permutes, video, packed, dot-product and conversion instructions. */
private[isa] object NvidiaLaneStaged {

  inline def permute[V, R](inline algebra: Algebra[V], instruction: Instruction)(inline sinks: NvidiaSinks[V, R])(
      inline next: Instruction => R
  ): R = instruction match {
    case Instruction.BytePermute(destination, left, right, control, mode) =>
      IntegerSemantics.bytePermute(algebra, mode)(sinks, destination, left, right, control)
    case _ => next(instruction)
  }

  inline def video[V, R](inline algebra: Algebra[V], instruction: Instruction)(inline sinks: NvidiaSinks[V, R])(
      inline next: Instruction => R
  ): R = instruction match {
    case Instruction.VideoScalar(
          destination,
          left,
          right,
          addend,
          operation,
          leftSelect,
          rightSelect,
          leftSigned,
          rightSigned,
          resultSigned,
          saturate,
          secondary,
          merge,
          wrap
        ) =>
      IntegerSemantics.videoScalar(
        algebra,
        operation,
        leftSelect,
        rightSelect,
        leftSigned,
        rightSigned,
        resultSigned,
        saturate,
        secondary,
        merge,
        wrap
      )(sinks, destination, left, right, addend)
    case Instruction.VideoMultiplyAdd(
          destination,
          left,
          right,
          addend,
          leftSelect,
          rightSelect,
          leftSigned,
          rightSigned,
          negateProduct,
          negateAddend,
          plusOne,
          shift,
          saturate
        ) =>
      IntegerSemantics.videoMultiplyAdd(
        algebra,
        leftSelect,
        rightSelect,
        leftSigned,
        rightSigned,
        negateProduct,
        negateAddend,
        plusOne,
        shift,
        saturate
      )(sinks, destination, left, right, addend)
    case Instruction.ConvertStochastic(destination, high, low, bits, rest, format, upper, relu, saturate) =>
      IntegerSemantics.convertStochastic(algebra, format, upper, relu, saturate)(
        sinks,
        destination,
        high,
        low,
        bits,
        rest
      )
    case Instruction.SelectSign(destination, whenNonNegative, whenNegative, condition, width, floating, flush) =>
      IntegerSemantics.selectSign(algebra, bits(width), floating, flush)(
        sinks,
        destination,
        whenNonNegative,
        whenNegative,
        condition
      )
    case Instruction.TestProperty(destination, source, property, width) =>
      IntegerSemantics.testProperty(algebra, property, width)(sinks, destination, source)
    case Instruction.CompareSetCombined(
          destination,
          comparison,
          format,
          left,
          right,
          truth,
          flush,
          operation,
          predicate,
          negated
        ) =>
      IntegerSemantics.compareSetCombined(algebra, comparison, format, truth, flush, operation, negated)(
        sinks,
        destination,
        left,
        right,
        predicate
      )
    case Instruction.SetPredicateCombined(
          destination,
          comparison,
          format,
          left,
          right,
          flush,
          operation,
          predicate,
          negated
        ) =>
      IntegerSemantics.setPredicateCombined(algebra, comparison, format, flush, operation, negated)(
        sinks,
        destination,
        left,
        right,
        predicate
      )
    case _ => next(instruction)
  }

  inline def lanes[V, R](inline algebra: Algebra[V], instruction: Instruction)(inline sinks: NvidiaSinks[V, R])(
      inline next: Instruction => R
  ): R = instruction match {
    case Instruction.DotProduct2(destination, left, right, addend, high, leftSigned, rightSigned) =>
      IntegerSemantics.dotProduct2(algebra, high, leftSigned, rightSigned)(sinks, destination, left, right, addend)
    case Instruction.SumAbsoluteDifference(destination, left, right, addend, width, signed) =>
      IntegerSemantics.sumAbsoluteDifference(algebra, bits(width), signed)(sinks, destination, left, right, addend)
    case Instruction.PackSaturated(destination, high, low, rest, bits, signed) =>
      IntegerSemantics.packSaturated(algebra, bits, signed)(sinks, destination, high, low, rest)
    case Instruction.ConvertToFloat8(destination, high, low, rest, format, halves, relu) =>
      IntegerSemantics.convertToFloat8(algebra, format, halves, relu)(sinks, destination, high, low, rest)
    case Instruction.ConvertNarrow(destination, high, low, scale, format, halves, bf16, rounding, relu, saturate) =>
      IntegerSemantics.convertNarrow(algebra, format, halves, bf16, rounding, relu, saturate)(
        sinks,
        destination,
        high,
        low,
        scale
      )
    case Instruction.ConvertWiden(destination, source, scale, format, bf16, relu, saturate) =>
      IntegerSemantics.convertWiden(algebra, format, bf16, relu, saturate)(sinks, destination, source, scale)
    case Instruction.ConvertFromFloat8(destination, source, format) =>
      IntegerSemantics.convertFromFloat8(algebra, format)(sinks, destination, source)
    case Instruction.SpaceTest(destination, address, space) =>
      IntegerSemantics.spaceTest(algebra, space)(sinks, destination, address)
    case Instruction.FindNthSet(destination, mask, base, offset) =>
      IntegerSemantics.findNthSet(algebra)(sinks, destination, mask, base, offset)
    case Instruction.VideoSimd(
          destination,
          left,
          right,
          addend,
          operation,
          lanes,
          leftSigned,
          rightSigned,
          resultSigned,
          saturate,
          accumulate
        ) =>
      IntegerSemantics.simd(
        algebra,
        operation,
        lanes,
        leftSigned,
        rightSigned,
        resultSigned,
        saturate,
        accumulate
      )(
        sinks,
        destination,
        left,
        right,
        addend
      )
    case Instruction.ConvertPacked(destination, high, low, _, destinationFormat, rounding, clamp) =>
      IntegerSemantics.convertPacked(
        algebra,
        destinationFormat == NumericFormat.BF16,
        rounding,
        clamp.contains(ConversionClamp.Relu)
      )(sinks, destination, high, low)
    case Instruction.CompareSet(destination, comparison, format, left, right, truth, packed, flushSubnormal) =>
      IntegerSemantics.compareSet(algebra, comparison, format, truth, packed, flushSubnormal)(
        sinks,
        destination,
        left,
        right
      )
    case _ => next(instruction)
  }
}
