package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*

/** Intel's arithmetic extensions by opcode: SPV_INTEL_shader_integer_functions2, whose saturating, averaging and
  * absolute-difference forms match OpenCL's at the result width, SPV_INTEL_ternary_bitwise_function, and the bfloat16
  * and TF32 conversions, which round to nearest even.
  */
object IntelSemantics {

  inline def integerUnary[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      opcode: Int,
      width: Int in 1 ~ 64
  )(
      ids: OperandIds
  )(otherwise: => R): R = opcode match {
    case SPIRV.Opcode.UCountLeadingZerosINTEL  => sinks.unary(ids)(a => algebra.leadingZeros(a, width))
    case SPIRV.Opcode.UCountTrailingZerosINTEL => sinks.unary(ids)(a => algebra.trailingZeros(a, width))
    case _                                     => otherwise
  }

  inline def integerBinary[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      opcode: Int,
      width: Int in 1 ~ 64
  )(ids: OperandIds)(otherwise: => R): R = opcode match {
    case SPIRV.Opcode.AbsISubINTEL =>
      sinks.binary(ids)(algebra.absoluteDifference(_, _, width, signed = true))
    case SPIRV.Opcode.AbsUSubINTEL =>
      sinks.binary(ids)(algebra.absoluteDifference(_, _, width, signed = false))
    case SPIRV.Opcode.IAddSatINTEL => sinks.binary(ids)((a, b) =>
        algebra.truncate(algebra.addSaturated(a, b, width, signed = true), width)
      )
    case SPIRV.Opcode.UAddSatINTEL =>
      sinks.binary(ids)((a, b) => algebra.addSaturated(a, b, width, signed = false))
    case SPIRV.Opcode.ISubSatINTEL => sinks.binary(ids)((a, b) =>
        algebra.truncate(algebra.subtractSaturated(a, b, width, signed = true), width)
      )
    case SPIRV.Opcode.USubSatINTEL =>
      sinks.binary(ids)((a, b) => algebra.subtractSaturated(a, b, width, signed = false))
    case SPIRV.Opcode.IAverageINTEL => OpenCLSemantics.halving(algebra, sinks, width, signed = true, round = false)(ids)
    case SPIRV.Opcode.UAverageINTEL =>
      OpenCLSemantics.halving(algebra, sinks, width, signed = false, round = false)(ids)
    case SPIRV.Opcode.IAverageRoundedINTEL =>
      OpenCLSemantics.halving(algebra, sinks, width, signed = true, round = true)(ids)
    case SPIRV.Opcode.UAverageRoundedINTEL =>
      OpenCLSemantics.halving(algebra, sinks, width, signed = false, round = true)(ids)
    case SPIRV.Opcode.IMul32x16INTEL =>
      sinks.binary(ids)((a, b) => algebra.truncate(algebra.multiply(a, algebra.signExtend(b, 16)), width))
    case SPIRV.Opcode.UMul32x16INTEL =>
      sinks.binary(ids)((a, b) => algebra.truncate(algebra.multiply(a, algebra.truncate(b, 16)), width))
    case _ => otherwise
  }

  /** OpBitwiseFunctionINTEL: each result bit is the bit of the lookup table the bits of A, B and C index, A lowest. */
  inline def bitwiseFunction[V, R](inline algebra: Algebra[V], inline sinks: SPIRVSinks[V, R], width: Int in 1 ~ 64)(
      ids: OperandIds
  ): R = sinks.quaternary(ids)((a, b, c, table) =>
    algebra.let("na", algebra.not(a))(na =>
      algebra.let("nb", algebra.not(b))(nb =>
        algebra.let("nc", algebra.not(c))(nc =>
          algebra.truncate(
            algebra.or(
              algebra.or(
                algebra.or(minterm(algebra, table, 0, na, nb, nc), minterm(algebra, table, 1, a, nb, nc)),
                algebra.or(minterm(algebra, table, 2, na, b, nc), minterm(algebra, table, 3, a, b, nc))
              ),
              algebra.or(
                algebra.or(minterm(algebra, table, 4, na, nb, c), minterm(algebra, table, 5, a, nb, c)),
                algebra.or(minterm(algebra, table, 6, na, b, c), minterm(algebra, table, 7, a, b, c))
              )
            ),
            width
          )
        )
      )
    )
  )

  /** The bits where `a`, `b` and `c` are all set, kept where bit `index` of the table is set. */
  inline def minterm[V](inline algebra: Algebra[V], table: V, inline index: Int, a: V, b: V, c: V): V = algebra.and(
    algebra.subtract(
      algebra.literal(0L),
      algebra.and(algebra.shiftRightLogical(table, algebra.literal(index.toLong)), algebra.literal(1L))
    ),
    algebra.and(algebra.and(a, b), c)
  )

  inline def conversion[V, R](inline algebra: Algebra[V], inline sinks: SPIRVSinks[V, R], opcode: Int)(ids: OperandIds)(
      otherwise: => R
  ): R = opcode match {
    case SPIRV.Opcode.ConvertFToBF16INTEL => sinks.unary(ids)(a => algebra.primitive(a)(toBFloat16(_)))
    case SPIRV.Opcode.ConvertBF16ToFINTEL => sinks.unary(ids)(a => algebra.primitive(a)(fromBFloat16(_)))
    case SPIRV.Opcode.RoundFToTF32INTEL   => sinks.unary(ids)(a => algebra.primitive(a)(roundToTensorFloat32(_)))
    case _                                => otherwise
  }

  def toBFloat16(x: Float32): UInt[16] = UInt[16](IEEE754.binary32ToBfloat16(Binary32.low(x.bits.raw)).toLong)

  def fromBFloat16(x: UInt[16]): Float32 = Float32(Minifloat.BFloat16.toDouble(x.raw))

  /** A binary32 value rounded to TF32's 10 fraction bits, the low 13 bits of its encoding clear. */
  def roundToTensorFloat32(x: Float32): Float32 =
    FloatingPoint[32](Minifloat.TensorFloat32.fromDouble(x.toDouble) << 13)
}
