package net.kurobako.gemstone.isa.amdgpu

import IntegerSemantics.*
import VMIR.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*

/** The staged semantics of the MFMA, SMFMAC, WMMA and SWMMAC matrix instructions over their element formats. */
private object MatrixSemantics {

  /** An element of a floating-point `format` as binary32, which holds it exactly; XF32 first rounds to 10 bits. */
  inline def widen[V](inline algebra: Algebra[V], format: MatrixFormat, value: V): V = format match {
    case MatrixFormat.F16  => algebra.primitive(value)(Floats.cvtF32F16)
    case MatrixFormat.BF16 => algebra.primitive(value)(MatrixArithmetic.widen(_: BFloat16))
    case MatrixFormat.XF32 =>
      algebra.primitive(algebra.primitive(value)(MatrixArithmetic.roundTf32))(MatrixArithmetic.widen(_: TensorFloat32))
    case MatrixFormat.FP8     => algebra.primitive(value)(MatrixArithmetic.widen(_: Float8E4M3))
    case MatrixFormat.BF8     => algebra.primitive(value)(MatrixArithmetic.widen(_: Float8E5M2))
    case MatrixFormat.FP8FNUZ => algebra.primitive(value)(MatrixArithmetic.widen(_: Float8E4M3FNUZ))
    case MatrixFormat.BF8FNUZ => algebra.primitive(value)(MatrixArithmetic.widen(_: Float8E5M2FNUZ))
    case MatrixFormat.FP6     => algebra.primitive(value)(MatrixArithmetic.widen(_: Float6E2M3))
    case MatrixFormat.BF6     => algebra.primitive(value)(MatrixArithmetic.widen(_: Float6E3M2))
    case MatrixFormat.FP4     => algebra.primitive(value)(MatrixArithmetic.widen(_: Float4E2M1))
    case _                    => value
  }

  /** An element of an integer `format` as its exact value. */
  inline def integer[V](inline algebra: Algebra[V], format: MatrixFormat, value: V): V = format match {
    case MatrixFormat.U8 => algebra.primitive(value)(MatrixArithmetic.unsigned8)
    case MatrixFormat.I4 => algebra.primitive(value)(MatrixArithmetic.signed4)
    case MatrixFormat.U4 => algebra.primitive(value)(MatrixArithmetic.unsigned4)
    case _               => algebra.primitive(value)(MatrixArithmetic.signed8)
  }

  /** A binary32 product step on elements [[widen]] has converted. */
  inline def product[V](inline algebra: Algebra[V], sum: V, a: V, b: V): V =
    algebra.primitive(a, b, sum)(Floats.fma(_: Float32, _: Float32, _: Float32))

  /** An element of A or B as the matrix core reads it: binary32 elements as MODE flushes their denormals. */
  inline def core[V](inline algebra: Algebra[V], format: MatrixFormat, value: V, mode: V): V = format match {
    case MatrixFormat.F32 => algebra.primitive(value, mode)(MatrixArithmetic.input)
    case _                => widen(algebra, format, value)
  }

  /** [[product]] on the matrix core, whose binary32 A and B follow MODE's denormal flushing and which saturates
    * overflows under MODE.FP16_OVFL.
    */
  inline def coreProduct[V](inline algebra: Algebra[V], sum: V, a: V, b: V, mode: V): V =
    algebra.primitive(a, b, sum, mode)(MatrixArithmetic.fma)

  /** Two 16-bit elements as the halves of one dword, `low` first. */
  inline def pair[V](inline algebra: Algebra[V], low: V, high: V): V =
    algebra.pack(algebra.truncate(low, 16), algebra.truncate(high, 16))

  /** A chained shape's products as RDNA3 and RDNA3.5 sum them: each step is a V_DOT2 on two consecutive elements of A
    * and of B, accumulating in C's format.
    */
  inline def chained[V, R](inline algebra: Algebra[V], instruction: Instruction.VMatrix)(
      inline sinks: AMDGPUSinks[V, R]
  ): R = {
    val shape = instruction.shape
    shape.c match {
      case MatrixFormat.F16 =>
        sinks.multiplyMatrices(instruction)(
          c => c,
          a => a,
          b => b,
          (sum, a0, b0, a1, b1) =>
            algebra.primitive(pair(algebra, a0, a1), pair(algebra, b0, b1), sum)(
              (x: UInt[32], y: UInt[32], z: Float16) =>
                Dots.halvesAligned(x, y, z)
            ),
          sum => sum
        )
      case MatrixFormat.BF16 =>
        sinks.multiplyMatrices(instruction)(
          c => c,
          a => a,
          b => b,
          (sum, a0, b0, a1, b1) =>
            algebra.primitive(pair(algebra, a0, a1), pair(algebra, b0, b1), sum)(
              (x: UInt[32], y: UInt[32], z: BFloat16) =>
                Dots.bfloat16Aligned(x, y, z)
            ),
          sum => sum
        )
      case _ =>
        sinks.multiplyMatrices(instruction)(
          c => c,
          a => a,
          b => b,
          (sum, a0, b0, a1, b1) =>
            if (shape.a == MatrixFormat.F16)
              algebra.primitive(pair(algebra, a0, a1), pair(algebra, b0, b1), sum)(
                (x: UInt[32], y: UInt[32], z: Float32) =>
                  Dots.halvesToFloatAligned(x, y, z)
              )
            else
              algebra.primitive(pair(algebra, a0, a1), pair(algebra, b0, b1), sum)(
                (x: UInt[32], y: UInt[32], z: Float32) =>
                  Dots.bfloat16ToFloatAligned(x, y, z)
              ),
          sum => sum
        )
    }
  }

  inline def multiply[V, R](inline algebra: Algebra[V], instruction: Instruction.VMatrix)(
      inline sinks: AMDGPUSinks[V, R]
  ): R = {
    val shape = instruction.shape
    val clamp = instruction.controls.clamp
    if (shape.chained) chained(algebra, instruction)(sinks)
    else shape.c match {
      case MatrixFormat.F64 =>
        sinks.multiplyMatrices(instruction)(
          c => c,
          a => a,
          b => b,
          (sum, a, b, _, _) => algebra.primitive(a, b, sum)(Floats.fma(_: Float64, _: Float64, _: Float64)),
          sum => sum
        )
      case MatrixFormat.I32 if shape.layout == MatrixLayout.Core =>
        sinks.multiplyMatrices(instruction)(
          c => algebra.primitive(c)(MatrixArithmetic.accumulator),
          a => integer(algebra, shape.a, a),
          b => integer(algebra, shape.b, b),
          (sum, a, b, _, _) => algebra.primitive(a, b, sum)(MatrixArithmetic.multiplyAdd),
          sum => algebra.primitive(sum, sinks.modeRegister)(MatrixArithmetic.coreResult)
        )
      case MatrixFormat.I32 if shape.grouped > 0 =>
        sinks.multiplyMatrices(instruction)(
          c => algebra.primitive(c)(MatrixArithmetic.accumulator),
          a => integer(algebra, shape.a, a),
          b => integer(algebra, shape.b, b),
          (sum, a, b, index, _) =>
            algebra.primitive(a, b, sum, index)((x: SInt[64], y: SInt[64], z: SInt[64], at: SInt[64]) =>
              MatrixArithmetic.groupedMultiplyAdd(x, y, z, at, shape.grouped, clamp)
            ),
          sum => algebra.primitive(sum)((value: SInt[64]) => MatrixArithmetic.result(value, clamp))
        )
      case MatrixFormat.I32 =>
        sinks.multiplyMatrices(instruction)(
          c => algebra.primitive(c)(MatrixArithmetic.accumulator),
          a => integer(algebra, shape.a, a),
          b => integer(algebra, shape.b, b),
          (sum, a, b, _, _) => algebra.primitive(a, b, sum)(MatrixArithmetic.multiplyAdd),
          sum => algebra.primitive(sum)((value: SInt[64]) => MatrixArithmetic.result(value, clamp))
        )
      case MatrixFormat.F16 =>
        sinks.multiplyMatrices(instruction)(
          c => algebra.primitive(c)(Floats.cvtF32F16),
          a => widen(algebra, shape.a, a),
          b => widen(algebra, shape.b, b),
          (sum, a, b, _, _) => product(algebra, sum, a, b),
          sum => algebra.primitive(sum)(Floats.cvtF16F32)
        )
      case MatrixFormat.BF16 =>
        sinks.multiplyMatrices(instruction)(
          c => algebra.primitive(c)(MatrixArithmetic.widen(_: BFloat16)),
          a => widen(algebra, shape.a, a),
          b => widen(algebra, shape.b, b),
          (sum, a, b, _, _) => product(algebra, sum, a, b),
          sum => algebra.primitive(sum)(MatrixArithmetic.roundBf16)
        )
      case _ if instruction.controls.scales.nonEmpty =>
        sinks.multiplyMatrices(instruction)(
          c => c,
          a => widen(algebra, shape.a, a),
          b => widen(algebra, shape.b, b),
          (sum, a, b, scaleA, scaleB) =>
            algebra.primitive(
              algebra.primitive(a, scaleA)(MatrixArithmetic.scaled),
              algebra.primitive(b, scaleB)(MatrixArithmetic.scaled),
              sum,
              sinks.modeRegister
            )(MatrixArithmetic.scaledFma),
          sum => sum
        )
      case _ if shape.layout == MatrixLayout.Core =>
        sinks.multiplyMatrices(instruction)(
          c => c,
          a => core(algebra, shape.a, a, sinks.modeRegister),
          b => core(algebra, shape.b, b, sinks.modeRegister),
          (sum, a, b, _, _) => coreProduct(algebra, sum, a, b, sinks.modeRegister),
          sum => sum
        )
      case _ =>
        sinks.multiplyMatrices(instruction)(
          c => c,
          a => widen(algebra, shape.a, a),
          b => widen(algebra, shape.b, b),
          (sum, a, b, _, _) => product(algebra, sum, a, b),
          sum => sum
        )
    }
  }
}
