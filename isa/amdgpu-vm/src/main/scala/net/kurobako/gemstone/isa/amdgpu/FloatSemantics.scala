package net.kurobako.gemstone.isa.amdgpu

import FloatPrimitives.*
import IntegerSemantics.*
import VMIR.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*

/** AMDGPU floating-point semantics on register bits. IEEE arithmetic, rounding and conversions are named primitives;
  * saturation, sign manipulation, classification and exponent extraction decompose into the algebra.
  */
// enum-dispatch arms must stay lexical to inline into staged kernels
@SuppressWarnings(Array("baumkuchen:near-duplicate-code"))
private object FloatSemantics {

  /** Floating-point VOP2 operations on modified SRC0 and SRC1 and the destination's previous dword, which the
    * accumulating forms read; `clamp` saturates results to [0, 1].
    */
  inline def vectorFloat[V, R](inline algebra: Algebra[V], operation: VectorBinaryOperation, clamp: Boolean)(
      inline sinks: AMDGPUSinks[V, R],
      dest: VectorRegisterIndex,
      srcA: InstructionSource,
      srcB: InstructionSource,
      modifiers: SourceModifiers
  )(otherwise: => R): R = operation match {
    case VectorBinaryOperation.AddF32 => sinks.vectorFloat(dest, srcA, srcB, modifiers, floatWidth(operation))(
        (a, b, _) => algebra.flushedSingle(sinks.modeRegister, clamp, a, b)(Floats.add)
      )
    case VectorBinaryOperation.SubtractF32 => sinks.vectorFloat(dest, srcA, srcB, modifiers, floatWidth(operation))(
        (a, b, _) => algebra.flushedSingle(sinks.modeRegister, clamp, a, b)(Floats.sub)
      )
    case VectorBinaryOperation.SubtractReverseF32 =>
      sinks.vectorFloat(dest, srcA, srcB, modifiers, floatWidth(operation))((a, b, _) =>
        algebra.flushedSingle(sinks.modeRegister, clamp, b, a)(Floats.sub)
      )
    case VectorBinaryOperation.MultiplyF32 => sinks.vectorFloat(dest, srcA, srcB, modifiers, floatWidth(operation))(
        (a, b, _) => algebra.flushedSingle(sinks.modeRegister, clamp, a, b)(Floats.mul)
      )
    case VectorBinaryOperation.MultiplyLegacyF32 =>
      sinks.vectorFloat(dest, srcA, srcB, modifiers, floatWidth(operation))((a, b, _) =>
        algebra.flushedSingle(sinks.modeRegister, clamp, a, b)(Floats.mulLegacyF32)
      )
    case VectorBinaryOperation.MinimumF32 =>
      sinks.vectorFloat(dest, srcA, srcB, modifiers, floatWidth(operation))((a, b, _) =>
        algebra.saturated(clamp, algebra.minimumNumber(a, b, 32), 32)
      )
    case VectorBinaryOperation.MinimumLegacyF32 =>
      sinks.vectorFloat(dest, srcA, srcB, modifiers, floatWidth(operation))((a, b, _) =>
        algebra.singleOperation(clamp, a, b)(Floats.minLegacyF32)
      )
    case VectorBinaryOperation.MaximumLegacyF32 =>
      sinks.vectorFloat(dest, srcA, srcB, modifiers, floatWidth(operation))((a, b, _) =>
        algebra.singleOperation(clamp, a, b)(Floats.maxLegacyF32)
      )
    case VectorBinaryOperation.MaximumF32 =>
      sinks.vectorFloat(dest, srcA, srcB, modifiers, floatWidth(operation))((a, b, _) =>
        algebra.saturated(clamp, algebra.maximumNumber(a, b, 32), 32)
      )
    case VectorBinaryOperation.MultiplyAccumulateF32 =>
      sinks.vectorFloat(dest, srcA, srcB, modifiers, floatWidth(operation))((a, b, d) =>
        algebra.flushedTernary(sinks.modeRegister, 32, a, b, d)((x, y, z) =>
          algebra.addSingle(algebra.multiplySingle(x, y), z)
        )
      )
    case VectorBinaryOperation.LoadExponentF32 =>
      sinks.vectorFloat(dest, srcA, srcB, modifiers, floatWidth(operation))((a, b, _) =>
        algebra.saturated(
          clamp,
          algebra.flushedUnary(sinks.modeRegister, 32, a)(x => algebra.loadExponent(x, b, 32)),
          32
        )
      )
    case VectorBinaryOperation.MinimumF16 =>
      sinks.vectorFloat(dest, srcA, srcB, modifiers, floatWidth(operation))((a, b, _) =>
        algebra.saturated(clamp, algebra.minimumNumber(a, b, 16), 16)
      )
    case VectorBinaryOperation.MaximumF16 =>
      sinks.vectorFloat(dest, srcA, srcB, modifiers, floatWidth(operation))((a, b, _) =>
        algebra.saturated(clamp, algebra.maximumNumber(a, b, 16), 16)
      )
    case VectorBinaryOperation.MinimumPropagateF32 =>
      sinks.vectorFloat(dest, srcA, srcB, modifiers, floatWidth(operation))((a, b, _) =>
        algebra.saturated(clamp, algebra.minimumPropagate(a, b, 32), 32)
      )
    case VectorBinaryOperation.MaximumPropagateF32 =>
      sinks.vectorFloat(dest, srcA, srcB, modifiers, floatWidth(operation))((a, b, _) =>
        algebra.saturated(clamp, algebra.maximumPropagate(a, b, 32), 32)
      )
    case VectorBinaryOperation.MinimumPropagateF16 =>
      sinks.vectorFloat(dest, srcA, srcB, modifiers, floatWidth(operation))((a, b, _) =>
        algebra.saturated(clamp, algebra.minimumPropagate(a, b, 16), 16)
      )
    case VectorBinaryOperation.MaximumPropagateF16 =>
      sinks.vectorFloat(dest, srcA, srcB, modifiers, floatWidth(operation))((a, b, _) =>
        algebra.saturated(clamp, algebra.maximumPropagate(a, b, 16), 16)
      )
    case VectorBinaryOperation.AddF16 => sinks.vectorFloat(dest, srcA, srcB, modifiers, floatWidth(operation))(
        (a, b, _) => algebra.flushedHalf(sinks.modeRegister, clamp, a, b)(Floats.add)
      )
    case VectorBinaryOperation.MultiplyF16 => sinks.vectorFloat(dest, srcA, srcB, modifiers, floatWidth(operation))(
        (a, b, _) => algebra.flushedHalf(sinks.modeRegister, clamp, a, b)(Floats.mul)
      )
    case VectorBinaryOperation.FusedMultiplyAccumulateF16 =>
      sinks.vectorFloat(dest, srcA, srcB, modifiers, floatWidth(operation))((a, b, d) =>
        algebra.saturated(
          clamp,
          algebra.flushedTernary(sinks.modeRegister, 16, a, b, d)(algebra.fusedMultiplyAdd(_, _, _, 16)),
          16
        )
      )
    case VectorBinaryOperation.SubtractF16 => sinks.vectorFloat(dest, srcA, srcB, modifiers, floatWidth(operation))(
        (a, b, _) => algebra.flushedHalf(sinks.modeRegister, clamp, a, b)(Floats.sub)
      )
    case VectorBinaryOperation.SubtractReverseF16 =>
      sinks.vectorFloat(dest, srcA, srcB, modifiers, floatWidth(operation))((a, b, _) =>
        algebra.flushedHalf(sinks.modeRegister, clamp, b, a)(Floats.sub)
      )
    case VectorBinaryOperation.PackRoundTowardZeroF16 =>
      sinks.vectorFloat(dest, srcA, srcB, modifiers, floatWidth(operation))((a, b, _) =>
        algebra.primitive(a, b)(Floats.cvtPkrtzF16F32)
      )
    // the exponent is a signed 16-bit integer
    case VectorBinaryOperation.LoadExponentF16 =>
      sinks.vectorFloat(dest, srcA, srcB, modifiers, floatWidth(operation))((a, b, _) =>
        algebra.saturated(
          clamp,
          algebra.flushedUnary(sinks.modeRegister, 16, a)(x =>
            algebra.primitive(x, b)(Floats.ldexp(_: Float16, _: SInt[16]))
          ),
          16
        )
      )
    case _ => otherwise
  }

  /** SALU floating-point operations on SRC0 and SRC1 dwords; binary16 operands are their low halves. */
  inline def scalarFloat[V, R](inline algebra: Algebra[V], operation: ScalarFloatOperation)(
      inline sinks: AMDGPUSinks[V, R],
      dest: ScalarRegisterIndex,
      srcA: InstructionSource,
      srcB: InstructionSource
  ): R = operation match {
    case ScalarFloatOperation.AddF16 =>
      sinks.scalarDword(dest, srcA, srcB)((a, b, _) =>
        algebra.flushedHalf(sinks.modeRegister, clamp = false, a, b)(Floats.add)
      )
    case ScalarFloatOperation.SubtractF16 =>
      sinks.scalarDword(dest, srcA, srcB)((a, b, _) =>
        algebra.flushedHalf(sinks.modeRegister, clamp = false, a, b)(Floats.sub)
      )
    case ScalarFloatOperation.MultiplyF16 =>
      sinks.scalarDword(dest, srcA, srcB)((a, b, _) =>
        algebra.flushedHalf(sinks.modeRegister, clamp = false, a, b)(Floats.mul)
      )
    case ScalarFloatOperation.MinimumF16 =>
      sinks.scalarDword(dest, srcA, srcB)((a, b, _) => algebra.minimumNumber(a, b, 16))
    case ScalarFloatOperation.MaximumF16 =>
      sinks.scalarDword(dest, srcA, srcB)((a, b, _) => algebra.maximumNumber(a, b, 16))
    case ScalarFloatOperation.MinimumF32 =>
      sinks.scalarDword(dest, srcA, srcB)((a, b, _) => algebra.minimumNumber(a, b, 32))
    case ScalarFloatOperation.MaximumF32 =>
      sinks.scalarDword(dest, srcA, srcB)((a, b, _) => algebra.maximumNumber(a, b, 32))
    case ScalarFloatOperation.MinimumPropagateF16 =>
      sinks.scalarDword(dest, srcA, srcB)((a, b, _) => algebra.minimumPropagate(a, b, 16))
    case ScalarFloatOperation.MaximumPropagateF16 =>
      sinks.scalarDword(dest, srcA, srcB)((a, b, _) => algebra.maximumPropagate(a, b, 16))
    case ScalarFloatOperation.MinimumPropagateF32 =>
      sinks.scalarDword(dest, srcA, srcB)((a, b, _) => algebra.minimumPropagate(a, b, 32))
    case ScalarFloatOperation.MaximumPropagateF32 =>
      sinks.scalarDword(dest, srcA, srcB)((a, b, _) => algebra.maximumPropagate(a, b, 32))
    case ScalarFloatOperation.PackRoundTowardZeroF16 =>
      sinks.scalarDword(dest, srcA, srcB)((a, b, _) => algebra.primitive(a, b)(Floats.cvtPkrtzF16F32))
  }

  /** SALU conversions and roundings of the SRC0 dword into a dword. */
  inline def scalarFloatUnary[V, R](inline algebra: Algebra[V], operation: ScalarFloatUnaryOperation)(
      inline sinks: AMDGPUSinks[V, R],
      dest: ScalarRegisterIndex,
      src: InstructionSource
  ): R = operation match {
    case ScalarFloatUnaryOperation.F32FromI32 =>
      sinks.scalarUnary(dest, src, readsPair = false, writesPair = false)((a, _) => algebra.signedToSingle(a))
    case ScalarFloatUnaryOperation.I32FromF32 =>
      sinks.scalarUnary(dest, src, readsPair = false, writesPair = false)((a, _) =>
        algebra.singleToInteger(a, signed = true)
      )
    case ScalarFloatUnaryOperation.F16FromF32 =>
      sinks.scalarUnary(dest, src, readsPair = false, writesPair = false)((a, _) => algebra.singleToHalf(a))
    case ScalarFloatUnaryOperation.F32FromF16 =>
      sinks.scalarUnary(dest, src, readsPair = false, writesPair = false)((a, _) => algebra.halfToSingle(a))
    case ScalarFloatUnaryOperation.F32FromHighF16 =>
      sinks.scalarUnary(dest, src, readsPair = false, writesPair = false)((a, _) =>
        algebra.halfToSingle(algebra.high16(a))
      )
    case ScalarFloatUnaryOperation.CeilF32 =>
      sinks.scalarUnary(dest, src, readsPair = false, writesPair = false)((a, _) =>
        algebra.primitive(a)(Floats.ceil(_: Float32))
      )
    case ScalarFloatUnaryOperation.FloorF32 =>
      sinks.scalarUnary(dest, src, readsPair = false, writesPair = false)((a, _) =>
        algebra.primitive(a)(Floats.floor(_: Float32))
      )
    case ScalarFloatUnaryOperation.RoundNearestEvenF32 =>
      sinks.scalarUnary(dest, src, readsPair = false, writesPair = false)((a, _) =>
        algebra.primitive(a)(Floats.rndne(_: Float32))
      )
    case ScalarFloatUnaryOperation.CeilF16 =>
      sinks.scalarUnary(dest, src, readsPair = false, writesPair = false)((a, _) =>
        algebra.primitive(a)(Floats.ceil(_: Float16))
      )
    case ScalarFloatUnaryOperation.FloorF16 =>
      sinks.scalarUnary(dest, src, readsPair = false, writesPair = false)((a, _) =>
        algebra.primitive(a)(Floats.floor(_: Float16))
      )
    case ScalarFloatUnaryOperation.TruncateF16 =>
      sinks.scalarUnary(dest, src, readsPair = false, writesPair = false)((a, _) =>
        algebra.primitive(a)(Floats.trunc(_: Float16))
      )
    case ScalarFloatUnaryOperation.RoundNearestEvenF16 =>
      sinks.scalarUnary(dest, src, readsPair = false, writesPair = false)((a, _) =>
        algebra.primitive(a)(Floats.rndne(_: Float16))
      )
    case ScalarFloatUnaryOperation.ExponentBase2F32 =>
      sinks.scalarUnary(dest, src, readsPair = false, writesPair = false)((a, _) =>
        algebra.primitive(a)(Floats.exp2(_: Float32))
      )
    case ScalarFloatUnaryOperation.LogarithmBase2F32 =>
      sinks.scalarUnary(dest, src, readsPair = false, writesPair = false)((a, _) =>
        algebra.primitive(a)(Floats.log2(_: Float32))
      )
    case ScalarFloatUnaryOperation.ReciprocalSquareRootF32 =>
      sinks.scalarUnary(dest, src, readsPair = false, writesPair = false)((a, _) =>
        algebra.primitive(a)(Floats.rsq(_: Float32))
      )
    case ScalarFloatUnaryOperation.SquareRootF32 =>
      sinks.scalarUnary(dest, src, readsPair = false, writesPair = false)((a, _) =>
        algebra.primitive(a)(Floats.sqrt(_: Float32))
      )
    case ScalarFloatUnaryOperation.ExponentBase2F16 =>
      sinks.scalarUnary(dest, src, readsPair = false, writesPair = false)((a, _) =>
        algebra.primitive(a)(Floats.exp2(_: Float16))
      )
    case ScalarFloatUnaryOperation.LogarithmBase2F16 =>
      sinks.scalarUnary(dest, src, readsPair = false, writesPair = false)((a, _) =>
        algebra.primitive(a)(Floats.log2(_: Float16))
      )
    case ScalarFloatUnaryOperation.ReciprocalF16 =>
      sinks.scalarUnary(dest, src, readsPair = false, writesPair = false)((a, _) =>
        algebra.primitive(a)(Floats.rcp(_: Float16))
      )
    case ScalarFloatUnaryOperation.ReciprocalSquareRootF16 =>
      sinks.scalarUnary(dest, src, readsPair = false, writesPair = false)((a, _) =>
        algebra.primitive(a)(Floats.rsq(_: Float16))
      )
    case ScalarFloatUnaryOperation.SquareRootF16 =>
      sinks.scalarUnary(dest, src, readsPair = false, writesPair = false)((a, _) =>
        algebra.primitive(a)(Floats.sqrt(_: Float16))
      )
  }

  /** The width whose sign bit the source modifiers of a floating-point VOP2 operation apply to. */
  def floatWidth(operation: VectorBinaryOperation): Int in (16, 32, 64) = operation match {
    case VectorBinaryOperation.MinimumF16 | VectorBinaryOperation.MaximumF16 | VectorBinaryOperation.AddF16 |
        VectorBinaryOperation.MultiplyF16 | VectorBinaryOperation.FusedMultiplyAccumulateF16 |
        VectorBinaryOperation.SubtractF16 | VectorBinaryOperation.SubtractReverseF16 |
        VectorBinaryOperation.LoadExponentF16 | VectorBinaryOperation.MinimumPropagateF16 |
        VectorBinaryOperation.MaximumPropagateF16 => 16
    case _ => 32
  }

  /** VOP1 conversions and unary operations, by operand form: `fromDword` reads a dword with binary32 modifiers,
    * `fromHalf` a dword with binary16 modifiers, `widening` a dword into a register pair, `narrowing` a pair into a
    * dword and `fromPair` a pair into a pair. `clamp` saturates floating-point results to [0, 1].
    */
  inline def convert[V, R](inline algebra: Algebra[V], operation: VectorConvertOperation, clamp: Boolean)(
      inline sinks: AMDGPUSinks[V, R],
      dest: VectorRegisterIndex,
      src: InstructionSource,
      modifiers: SourceModifiers
  ): R = operation match {
    case VectorConvertOperation.FrexpExponentF64 =>
      sinks.narrowingUnary(dest, src, modifiers)(a =>
        algebra.frexpExponent(algebra.denormalInput(sinks.modeRegister, a, 64), 64)
      )
    case VectorConvertOperation.FrexpExponentF32 =>
      sinks.modifiedUnary(dest, src, modifiers, 32)(a =>
        algebra.frexpExponent(algebra.denormalInput(sinks.modeRegister, a, 32), 32)
      )
    case VectorConvertOperation.FrexpMantissaF32 =>
      single(algebra, clamp)(sinks, dest, src, modifiers)(Floats.frexpMant(_: Float32))
    case VectorConvertOperation.FrexpMantissaF64 =>
      double(algebra, clamp)(sinks, dest, src, modifiers)(Floats.frexpMant(_: Float64))
    case VectorConvertOperation.ReciprocalF32 =>
      sinks.modifiedUnary(dest, src, modifiers, 32)(a => algebra.reciprocal(clamp, a))
    case VectorConvertOperation.ReciprocalF64 =>
      double(algebra, clamp)(sinks, dest, src, modifiers)(Floats.rcp(_: Float64))
    case VectorConvertOperation.TruncateF32 =>
      single(algebra, clamp)(sinks, dest, src, modifiers)(Floats.trunc(_: Float32))
    case VectorConvertOperation.FractionF32 =>
      single(algebra, clamp)(sinks, dest, src, modifiers)(Floats.fract(_: Float32))
    case VectorConvertOperation.FloorF32 =>
      single(algebra, clamp)(sinks, dest, src, modifiers)(Floats.floor(_: Float32))
    case VectorConvertOperation.RoundNearestEvenF32 =>
      single(algebra, clamp)(sinks, dest, src, modifiers)(Floats.rndne(_: Float32))
    case VectorConvertOperation.TruncateF64 =>
      double(algebra, clamp)(sinks, dest, src, modifiers)(Floats.trunc(_: Float64))
    case VectorConvertOperation.FloorF64 =>
      double(algebra, clamp)(sinks, dest, src, modifiers)(Floats.floor(_: Float64))
    case VectorConvertOperation.FractionF64 =>
      double(algebra, clamp)(sinks, dest, src, modifiers)(Floats.fract(_: Float64))
    case VectorConvertOperation.RoundNearestEvenF64 =>
      double(algebra, clamp)(sinks, dest, src, modifiers)(Floats.rndne(_: Float64))
    case VectorConvertOperation.ReciprocalSquareRootF32 =>
      sinks.modifiedUnary(dest, src, modifiers, 32)(a =>
        algebra.saturated(
          clamp,
          algebra.primitive(a)(Floats.rsq(_: Float32)),
          32
        )
      )
    case VectorConvertOperation.ReciprocalSquareRootF64 =>
      double(algebra, clamp)(sinks, dest, src, modifiers)(Floats.rsq(_: Float64))
    case VectorConvertOperation.SquareRootF32 =>
      sinks.modifiedUnary(dest, src, modifiers, 32)(a =>
        algebra.saturated(clamp, algebra.primitive(a)(Floats.sqrt(_: Float32)), 32)
      )
    case VectorConvertOperation.ExponentBase2F32 =>
      sinks.modifiedUnary(dest, src, modifiers, 32)(a =>
        algebra.saturated(clamp, algebra.primitive(a)(Floats.exp2(_: Float32)), 32)
      )
    case _ =>
      sinks.section(operation match {
        case VectorConvertOperation.SineF32 =>
          single(algebra, clamp)(sinks, dest, src, modifiers)(Floats.sin(_: Float32))
        case VectorConvertOperation.CosineF32 =>
          single(algebra, clamp)(sinks, dest, src, modifiers)(Floats.cos(_: Float32))
        case VectorConvertOperation.SineBoundedF32 =>
          single(algebra, clamp)(sinks, dest, src, modifiers)(Floats.sinBounded(_: Float32))
        case VectorConvertOperation.CosineBoundedF32 =>
          single(algebra, clamp)(sinks, dest, src, modifiers)(Floats.cosBounded(_: Float32))
        case VectorConvertOperation.ReciprocalClampF32 =>
          sinks.modifiedUnary(dest, src, modifiers, 32)(a =>
            algebra.saturated(clamp, algebra.primitive(a)(Floats.rcpClamp(_: Float32)), 32)
          )
        case VectorConvertOperation.ReciprocalClampF64 =>
          double(algebra, clamp)(sinks, dest, src, modifiers)(Floats.rcpClamp(_: Float64))
        case VectorConvertOperation.ReciprocalSquareRootClampF32 =>
          sinks.modifiedUnary(dest, src, modifiers, 32)(a =>
            algebra.saturated(clamp, algebra.primitive(a)(Floats.rsqClamp(_: Float32)), 32)
          )
        case VectorConvertOperation.ReciprocalSquareRootClampF64 =>
          double(algebra, clamp)(sinks, dest, src, modifiers)(Floats.rsqClamp(_: Float64))
        case VectorConvertOperation.LogarithmClampF32 =>
          sinks.modifiedUnary(dest, src, modifiers, 32)(a =>
            algebra.saturated(clamp, algebra.primitive(a)(Floats.logClampF32), 32)
          )
        case VectorConvertOperation.ReciprocalLegacyF32 =>
          sinks.modifiedUnary(dest, src, modifiers, 32)(a =>
            algebra.saturated(clamp, algebra.primitive(a)(Floats.rcpLegacyF32), 32)
          )
        case VectorConvertOperation.ReciprocalSquareRootLegacyF32 =>
          sinks.modifiedUnary(dest, src, modifiers, 32)(a =>
            algebra.saturated(clamp, algebra.primitive(a)(Floats.rsqLegacyF32), 32)
          )
        case VectorConvertOperation.LogarithmBase2F32 =>
          sinks.modifiedUnary(dest, src, modifiers, 32)(a =>
            algebra.saturated(
              clamp,
              algebra.primitive(a)(Floats.log2(_: Float32)),
              32
            )
          )
        case VectorConvertOperation.I32FromF32 =>
          sinks.modifiedUnary(dest, src, modifiers, 32)(a => algebra.singleToInteger(a, signed = true))
        case VectorConvertOperation.U32FromF32 =>
          sinks.modifiedUnary(dest, src, modifiers, 32)(a => algebra.singleToInteger(a, signed = false))
        case VectorConvertOperation.I32FromF64 =>
          sinks.narrowingUnary(dest, src, modifiers)(a =>
            algebra.primitive(a)(Floats.cvtI32F64)
          )
        case VectorConvertOperation.U32FromF64 =>
          sinks.narrowingUnary(dest, src, modifiers)(a =>
            algebra.primitive(a)(Floats.cvtU32F64)
          )
        case VectorConvertOperation.F32FromUByte0 =>
          sinks.modifiedUnary(dest, src, modifiers, 32)(a =>
            algebra.saturated(clamp, algebra.unsignedToSingle(algebra.byte(a, 0)), 32)
          )
        case VectorConvertOperation.F32FromUByte1 =>
          sinks.modifiedUnary(dest, src, modifiers, 32)(a =>
            algebra.saturated(clamp, algebra.unsignedToSingle(algebra.byte(a, 1)), 32)
          )
        case VectorConvertOperation.F32FromUByte2 =>
          sinks.modifiedUnary(dest, src, modifiers, 32)(a =>
            algebra.saturated(clamp, algebra.unsignedToSingle(algebra.byte(a, 2)), 32)
          )
        case VectorConvertOperation.F32FromUByte3 =>
          sinks.modifiedUnary(dest, src, modifiers, 32)(a =>
            algebra.saturated(clamp, algebra.unsignedToSingle(algebra.byte(a, 3)), 32)
          )
        case VectorConvertOperation.F32FromF64 =>
          sinks.narrowingUnary(dest, src, modifiers)(a =>
            algebra.saturated(clamp, algebra.converted(sinks.modeRegister, 64, 32, a)(Floats.cvtF32F64), 32)
          )
        case VectorConvertOperation.F32FromF16 =>
          sinks.modifiedUnary(dest, src, modifiers, 16)(a =>
            algebra.saturated(clamp, algebra.converted(sinks.modeRegister, 16, 32, a)(Floats.cvtF32F16), 32)
          )
        case _ =>
          sinks.section(operation match {
            case VectorConvertOperation.F16FromF32 =>
              sinks.modifiedUnary(dest, src, modifiers, 32)(a =>
                algebra.saturated(clamp, algebra.converted(sinks.modeRegister, 32, 16, a)(Floats.cvtF16F32), 16)
              )
            case VectorConvertOperation.I16FromF16 =>
              sinks.modifiedUnary(dest, src, modifiers, 16)(a =>
                algebra.primitive(a)(Floats.cvtI16F16)
              )
            case VectorConvertOperation.F64FromI32 =>
              sinks.wideningUnary(dest, src, modifiers)(a =>
                algebra.saturated(clamp, algebra.primitive(a)(Floats.cvtF64I32), 64)
              )
            case VectorConvertOperation.F64FromU32 =>
              sinks.wideningUnary(dest, src, modifiers)(a =>
                algebra.saturated(clamp, algebra.primitive(a)(Floats.cvtF64U32), 64)
              )
            case VectorConvertOperation.F64FromF32 =>
              sinks.wideningUnary(dest, src, modifiers)(a =>
                algebra.saturated(clamp, algebra.converted(sinks.modeRegister, 32, 64, a)(Floats.cvtF64F32), 64)
              )
            case VectorConvertOperation.F32FromI32 =>
              sinks.modifiedUnary(dest, src, modifiers, 32)(a =>
                algebra.saturated(clamp, algebra.signedToSingle(a), 32)
              )
            case VectorConvertOperation.F32FromU32 =>
              sinks.modifiedUnary(dest, src, modifiers, 32)(a =>
                algebra.saturated(clamp, algebra.unsignedToSingle(a), 32)
              )
            case VectorConvertOperation.CeilF32 =>
              single(algebra, clamp)(sinks, dest, src, modifiers)(Floats.ceil(_: Float32))
            case VectorConvertOperation.CeilF64 =>
              double(algebra, clamp)(sinks, dest, src, modifiers)(Floats.ceil(_: Float64))
            case VectorConvertOperation.SquareRootF64 =>
              double(algebra, clamp)(sinks, dest, src, modifiers)(Floats.sqrt(_: Float64))
            case VectorConvertOperation.FloorI32FromF32 =>
              sinks.modifiedUnary(dest, src, modifiers, 32)(a =>
                algebra.primitive(algebra.denormalInput(sinks.modeRegister, a, 32))(Floats.cvtFloorI32F32)
              )
            case VectorConvertOperation.NearestI32FromF32 =>
              sinks.modifiedUnary(dest, src, modifiers, 32)(a => algebra.primitive(a)(Floats.cvtNearestI32F32))
            case VectorConvertOperation.CeilF16 =>
              half(algebra, clamp)(sinks, dest, src, modifiers)(Floats.ceil(_: Float16))
            case VectorConvertOperation.FloorF16 =>
              half(algebra, clamp)(sinks, dest, src, modifiers)(Floats.floor(_: Float16))
            case VectorConvertOperation.TruncateF16 =>
              half(algebra, clamp)(sinks, dest, src, modifiers)(Floats.trunc(_: Float16))
            case VectorConvertOperation.RoundNearestEvenF16 =>
              half(algebra, clamp)(sinks, dest, src, modifiers)(Floats.rndne(_: Float16))
            case _ =>
              sinks.section(operation match {
                case VectorConvertOperation.FractionF16 =>
                  half(algebra, clamp)(sinks, dest, src, modifiers)(Floats.fract(_: Float16))
                case VectorConvertOperation.SquareRootF16 =>
                  half(algebra, clamp)(sinks, dest, src, modifiers)(Floats.sqrt(_: Float16))
                case VectorConvertOperation.ReciprocalF16 =>
                  half(algebra, clamp)(sinks, dest, src, modifiers)(Floats.rcp(_: Float16))
                case VectorConvertOperation.ReciprocalSquareRootF16 =>
                  half(algebra, clamp)(sinks, dest, src, modifiers)(Floats.rsq(_: Float16))
                case VectorConvertOperation.ExponentBase2F16 =>
                  half(algebra, clamp)(sinks, dest, src, modifiers)(Floats.exp2(_: Float16))
                case VectorConvertOperation.LogarithmBase2F16 =>
                  half(algebra, clamp)(sinks, dest, src, modifiers)(Floats.log2(_: Float16))
                case VectorConvertOperation.SineF16 =>
                  half(algebra, clamp)(sinks, dest, src, modifiers)(Floats.sin(_: Float16))
                case VectorConvertOperation.CosineF16 =>
                  half(algebra, clamp)(sinks, dest, src, modifiers)(Floats.cos(_: Float16))
                case VectorConvertOperation.SineBoundedF16 =>
                  half(algebra, clamp)(sinks, dest, src, modifiers)(Floats.sinBounded(_: Float16))
                case VectorConvertOperation.CosineBoundedF16 =>
                  half(algebra, clamp)(sinks, dest, src, modifiers)(Floats.cosBounded(_: Float16))
                case VectorConvertOperation.FrexpMantissaF16 =>
                  half(algebra, clamp)(sinks, dest, src, modifiers)(Floats.frexpMant(_: Float16))
                case VectorConvertOperation.FrexpExponentF16 =>
                  sinks.modifiedUnary(dest, src, modifiers, 16)(a =>
                    algebra.primitive(algebra.denormalInput(sinks.modeRegister, a, 16))(Floats.frexpExpF16)
                  )
                case VectorConvertOperation.I32FromI16 =>
                  sinks.modifiedUnary(dest, src, modifiers, 16)(a => algebra.truncate(algebra.signExtend(a, 16), 32))
                case VectorConvertOperation.U32FromU16 =>
                  sinks.modifiedUnary(dest, src, modifiers, 16)(a => algebra.truncate(a, 16))
                case VectorConvertOperation.F32FromBF16 =>
                  sinks.modifiedUnary(dest, src, modifiers, 16)(a =>
                    algebra.saturated(clamp, algebra.shiftLeft(algebra.truncate(a, 16), algebra.literal(16L)), 32)
                  )
                case VectorConvertOperation.OffsetF32FromI4 =>
                  sinks.modifiedUnary(dest, src, modifiers, 32)(a =>
                    algebra.saturated(clamp, algebra.primitive(a)(Media.offsetF32I4), 32)
                  )
                case VectorConvertOperation.SignedNormFromF16 =>
                  sinks.modifiedUnary(dest, src, modifiers, 16)(a => algebra.primitive(a)(Media.signedNormF16))
                case VectorConvertOperation.UnsignedNormFromF16 =>
                  sinks.modifiedUnary(dest, src, modifiers, 16)(a => algebra.primitive(a)(Media.unsignedNormF16))
                case VectorConvertOperation.PseudoRandom =>
                  sinks.modifiedUnary(dest, src, modifiers, 32)(a => algebra.primitive(a)(Minifloats.prng))
                case VectorConvertOperation.SaturatePackU8FromI16 =>
                  sinks.modifiedUnary(dest, src, modifiers, 32)(a =>
                    algebra.or(
                      algebra.saturatedByte(a),
                      algebra.shiftLeft(algebra.saturatedByte(algebra.high16(a)), algebra.literal(8L))
                    )
                  )
                case VectorConvertOperation.F16FromI16 =>
                  sinks.modifiedUnary(dest, src, modifiers, 16)(a =>
                    algebra.saturated(clamp, algebra.primitive(a)(Floats.cvtF16I16), 16)
                  )
                case VectorConvertOperation.F16FromU16 =>
                  sinks.modifiedUnary(dest, src, modifiers, 16)(a =>
                    algebra.saturated(clamp, algebra.primitive(a)(Floats.cvtF16U16), 16)
                  )
                case VectorConvertOperation.U16FromF16 =>
                  sinks.modifiedUnary(dest, src, modifiers, 16)(a => algebra.primitive(a)(Floats.cvtU16F16))
                case VectorConvertOperation.F32FromE4M3 =>
                  sinks.modifiedUnary(dest, src, modifiers, 32)(a => algebra.primitive(a)(Minifloats.fromE4M3))
                case VectorConvertOperation.F32FromE5M2 =>
                  sinks.modifiedUnary(dest, src, modifiers, 32)(a => algebra.primitive(a)(Minifloats.fromE5M2))
                case VectorConvertOperation.F32FromE4M3Fnuz =>
                  sinks.modifiedUnary(dest, src, modifiers, 32)(a => algebra.primitive(a)(Minifloats.fromE4M3Fnuz))
                case VectorConvertOperation.F32FromE5M2Fnuz =>
                  sinks.modifiedUnary(dest, src, modifiers, 32)(a => algebra.primitive(a)(Minifloats.fromE5M2Fnuz))
                case VectorConvertOperation.PairF32FromE4M3 =>
                  sinks.wideningUnary(dest, src, modifiers)(a => algebra.primitive(a)(Minifloats.pairFromE4M3))
                case VectorConvertOperation.PairF32FromE5M2 =>
                  sinks.wideningUnary(dest, src, modifiers)(a => algebra.primitive(a)(Minifloats.pairFromE5M2))
                case VectorConvertOperation.PairF32FromE4M3Fnuz =>
                  sinks.wideningUnary(dest, src, modifiers)(a => algebra.primitive(a)(Minifloats.pairFromE4M3Fnuz))
                case VectorConvertOperation.PairF32FromE5M2Fnuz =>
                  sinks.wideningUnary(dest, src, modifiers)(a => algebra.primitive(a)(Minifloats.pairFromE5M2Fnuz))
                case other => throw IllegalStateException(s"$other has no staged conversion")
              })
          })
      })
  }

  /** V_CVT_PK_*8_F32 and V_CVT_SR_*8_F32 on modified binary32 sources and the destination's previous dword. */
  inline def narrow[V, R](inline algebra: Algebra[V], instruction: Instruction.VConvertNarrow)(
      inline sinks: AMDGPUSinks[V, R]
  ): R = {
    val format   = instruction.format
    val position = instruction.position
    if (instruction.stochastic)
      sinks.vectorFloat(instruction.dest, instruction.srcA, instruction.srcB, instruction.modifiers, 32)((a, b, old) =>
        algebra.primitive(a, b, old, sinks.modeRegister)(
          (value: Float32, seed: UInt[32], kept: UInt[32], mode: UInt[32]) =>
            Minifloats.stochastic(value, seed, kept, mode, format, position)
        )
      )
    else
      sinks.vectorFloat(instruction.dest, instruction.srcA, instruction.srcB, instruction.modifiers, 32)((a, b, old) =>
        algebra.primitive(a, b, old, sinks.modeRegister)(
          (first: Float32, second: Float32, kept: UInt[32], mode: UInt[32]) =>
            Minifloats.pack(first, second, kept, mode, format, position == 1)
        )
      )
  }

  /** A binary16 unary operation on a modified binary16 source, its denormals as MODE has them, saturated when `clamp`.
    */
  inline def half[V, R](inline algebra: Algebra[V], clamp: Boolean)(
      inline sinks: AMDGPUSinks[V, R],
      dest: VectorRegisterIndex,
      src: InstructionSource,
      modifiers: SourceModifiers
  )(inline operation: Float16 => Float16): R =
    sinks.modifiedUnary(dest, src, modifiers, 16)(a =>
      algebra.saturated(clamp, algebra.flushedUnary(sinks.modeRegister, 16, a)(algebra.primitive(_)(operation)), 16)
    )

  /** [[half]] on binary32. */
  inline def single[V, R](inline algebra: Algebra[V], clamp: Boolean)(
      inline sinks: AMDGPUSinks[V, R],
      dest: VectorRegisterIndex,
      src: InstructionSource,
      modifiers: SourceModifiers
  )(inline operation: Float32 => Float32): R =
    sinks.modifiedUnary(dest, src, modifiers, 32)(a =>
      algebra.saturated(clamp, algebra.flushedUnary(sinks.modeRegister, 32, a)(algebra.primitive(_)(operation)), 32)
    )

  /** [[half]] on a binary64 register pair. */
  inline def double[V, R](inline algebra: Algebra[V], clamp: Boolean)(
      inline sinks: AMDGPUSinks[V, R],
      dest: VectorRegisterIndex,
      src: InstructionSource,
      modifiers: SourceModifiers
  )(inline operation: Float64 => Float64): R =
    sinks.pairUnary(dest, src, PairRead.Floating, modifiers)(a =>
      algebra.saturated(clamp, algebra.flushedUnary(sinks.modeRegister, 64, a)(algebra.primitive(_)(operation)), 64)
    )

  /** V_RCP_F32 and S_RCP_F32. */
  extension [V](inline algebra: Algebra[V]) {
    inline def reciprocal(clamp: Boolean, value: V): V =
      algebra.saturated(clamp, algebra.reciprocalSingle(value), 32)
  }

  /** V_CMP and S_CMP on floating-point kinds, as 1 or 0; integer kinds take `otherwise`. The scalar form compares SRC0
    * and SRC1 into SCC, the vector forms read binary16 and binary32 dwords, binary64 pairs and class masks.
    */
  inline def compare[V, R](inline algebra: Algebra[V], kind: VectorComparisonKind, test: FloatTest, swap: Boolean)(
      inline sinks: AMDGPUSinks[V, R],
      inline scalar: Boolean,
      dest: ScalarRegisterIndex,
      srcA: InstructionSource,
      srcB: InstructionSource,
      modifiers: SourceModifiers,
      selection: Option[SubDwordSources],
      updateExec: Boolean
  )(otherwise: => R): R = kind match {
    case VectorComparisonKind.Float16 =>
      sinks.section(
        ordered(
          algebra,
          test,
          swap,
          16
        )(sinks, scalar, pair = false, dest, srcA, srcB, modifiers, selection, updateExec)
      )
    case VectorComparisonKind.Float32 =>
      sinks.section(
        ordered(
          algebra,
          test,
          swap,
          32
        )(sinks, scalar, pair = false, dest, srcA, srcB, modifiers, selection, updateExec)
      )
    case VectorComparisonKind.Float64 =>
      sinks.section(
        ordered(algebra, test, swap, 64)(sinks, scalar, pair = true, dest, srcA, srcB, modifiers, selection, updateExec)
      )
    case VectorComparisonKind.ClassFloat16 =>
      inline if (scalar) sinks.scalarCompare(srcA, srcB, pair = false)((a, b) => algebra.classTest(a, b, 16))
      else
        sinks.compareModified(dest, srcA, srcB, modifiers, selection, updateExec, 16)((a, b) =>
          algebra.classTest(a, b, 16)
        )
    case VectorComparisonKind.ClassFloat32 =>
      inline if (scalar) sinks.scalarCompare(srcA, srcB, pair = false)((a, b) => algebra.classTest(a, b, 32))
      else
        sinks.compareClass(dest, srcA, srcB, modifiers, selection, updateExec)((a, b) => algebra.classTest(a, b, 32))
    case VectorComparisonKind.ClassFloat64 =>
      inline if (scalar) sinks.scalarCompare(srcA, srcB, pair = false)((a, b) => algebra.classTest(a, b, 64))
      else sinks.comparePairClass(dest, srcA, srcB, modifiers, updateExec)((a, b) => algebra.classTest(a, b, 64))
    case _ => otherwise
  }

  inline def ordered[V, R](inline algebra: Algebra[V], test: FloatTest, swap: Boolean, width: Int in (16, 32, 64))(
      inline sinks: AMDGPUSinks[V, R],
      inline scalar: Boolean,
      inline pair: Boolean,
      dest: ScalarRegisterIndex,
      srcA: InstructionSource,
      srcB: InstructionSource,
      modifiers: SourceModifiers,
      selection: Option[SubDwordSources],
      updateExec: Boolean
  ): R = test match {
    case FloatTest.Less =>
      floatShape(algebra, sinks, scalar, pair, dest, srcA, srcB, modifiers, selection, updateExec, width)((a, b) =>
        if (swap) algebra.less(b, a, width) else algebra.less(a, b, width)
      )
    case FloatTest.LessOrEqual =>
      floatShape(algebra, sinks, scalar, pair, dest, srcA, srcB, modifiers, selection, updateExec, width)((a, b) =>
        if (swap) algebra.lessOrEqual(b, a, width) else algebra.lessOrEqual(a, b, width)
      )
    case FloatTest.Equal =>
      floatShape(algebra, sinks, scalar, pair, dest, srcA, srcB, modifiers, selection, updateExec, width)((a, b) =>
        algebra.equal(a, b, width)
      )
    case FloatTest.NotEqual =>
      floatShape(algebra, sinks, scalar, pair, dest, srcA, srcB, modifiers, selection, updateExec, width)((a, b) =>
        algebra.negation(algebra.equal(a, b, width))
      )
    case FloatTest.OrderedNotEqual =>
      floatShape(algebra, sinks, scalar, pair, dest, srcA, srcB, modifiers, selection, updateExec, width)((a, b) =>
        algebra.or(algebra.less(a, b, width), algebra.less(b, a, width))
      )
    case FloatTest.NotLess =>
      floatShape(algebra, sinks, scalar, pair, dest, srcA, srcB, modifiers, selection, updateExec, width)((a, b) =>
        algebra.negation(if (swap) algebra.less(b, a, width) else algebra.less(a, b, width))
      )
    case FloatTest.NotLessOrEqual =>
      floatShape(algebra, sinks, scalar, pair, dest, srcA, srcB, modifiers, selection, updateExec, width)((a, b) =>
        algebra.negation(if (swap) algebra.lessOrEqual(b, a, width) else algebra.lessOrEqual(a, b, width))
      )
    case FloatTest.NotLessOrGreater =>
      floatShape(algebra, sinks, scalar, pair, dest, srcA, srcB, modifiers, selection, updateExec, width)((a, b) =>
        algebra.negation(algebra.or(algebra.less(a, b, width), algebra.less(b, a, width)))
      )
    case FloatTest.Ordered =>
      floatShape(algebra, sinks, scalar, pair, dest, srcA, srcB, modifiers, selection, updateExec, width)((a, b) =>
        algebra.negation(algebra.unordered(a, b, width))
      )
    case FloatTest.Unordered =>
      floatShape(algebra, sinks, scalar, pair, dest, srcA, srcB, modifiers, selection, updateExec, width)((a, b) =>
        algebra.unordered(a, b, width)
      )
    case FloatTest.Never =>
      floatShape(algebra, sinks, scalar, pair, dest, srcA, srcB, modifiers, selection, updateExec, width)((_, _) =>
        algebra.literal(0L)
      )
    case FloatTest.Always =>
      floatShape(algebra, sinks, scalar, pair, dest, srcA, srcB, modifiers, selection, updateExec, width)((_, _) =>
        algebra.literal(1L)
      )
  }

  inline def floatShape[V, R](
      inline algebra: Algebra[V],
      inline sinks: AMDGPUSinks[V, R],
      inline scalar: Boolean,
      inline pair: Boolean,
      dest: ScalarRegisterIndex,
      srcA: InstructionSource,
      srcB: InstructionSource,
      modifiers: SourceModifiers,
      selection: Option[SubDwordSources],
      updateExec: Boolean,
      width: Int in (16, 32, 64)
  )(inline kernel: (V, V) => V): R =
    inline if (scalar)
      sinks.scalarCompare(srcA, srcB, pair = false)((a, b) =>
        algebra.compared(sinks.modeRegister, width, a, b)(kernel)
      )
    else inline if (pair)
      sinks.comparePair(dest, srcA, srcB, PairRead.Floating, modifiers, updateExec)((a, b) =>
        algebra.compared(sinks.modeRegister, width, a, b)(kernel)
      )
    else
      sinks.compareModified(dest, srcA, srcB, modifiers, selection, updateExec, width)((a, b) =>
        algebra.compared(sinks.modeRegister, width, a, b)(kernel)
      )

  extension [V](inline algebra: Algebra[V]) {

    /** `test` on binary32 operands as MODE.FP_DENORM has them read; other widths compare as they are. */
    inline def compared(mode: V, width: Int in (16, 32, 64), a: V, b: V)(inline test: (V, V) => V): V =
      if (width == 32) test(algebra.denormalInput(mode, a, 32), algebra.denormalInput(mode, b, 32)) else test(a, b)

    inline def negation(condition: V): V = algebra.xor(condition, algebra.literal(1L))

    inline def less(a: V, b: V, width: Int in (16, 32, 64)): V = width.raw match {
      case 16 => algebra.primitive(a, b)(Floats.lt(_: Float16, _: Float16))
      case 32 => algebra.primitive(a, b)(Floats.lt(_: Float32, _: Float32))
      case _  => algebra.primitive(a, b)(Floats.lt(_: Float64, _: Float64))
    }

    inline def lessOrEqual(a: V, b: V, width: Int in (16, 32, 64)): V = width.raw match {
      case 16 => algebra.primitive(a, b)(Floats.le(_: Float16, _: Float16))
      case 32 => algebra.primitive(a, b)(Floats.le(_: Float32, _: Float32))
      case _  => algebra.primitive(a, b)(Floats.le(_: Float64, _: Float64))
    }

    inline def equal(a: V, b: V, width: Int in (16, 32, 64)): V = width.raw match {
      case 16 => algebra.primitive(a, b)(Floats.eq(_: Float16, _: Float16))
      case 32 => algebra.primitive(a, b)(Floats.eq(_: Float32, _: Float32))
      case _  => algebra.primitive(a, b)(Floats.eq(_: Float64, _: Float64))
    }

    /** V_CMP_CLASS: bit `i` of the mask's dword tests class `i`: signalling and quiet NaN, negative infinity, normal,
      * denormal and zero, then positive zero, denormal, normal and infinity.
      */
    inline def classTest(value: V, mask: V, width: Int in (16, 32, 64)): V =
      algebra.and(
        algebra.shiftRightLogical(algebra.truncate(mask, 32), algebra.floatClass(value, width)),
        algebra.literal(1L)
      )

    inline def floatClass(value: V, width: Int in (16, 32, 64)): V = {
      val (fraction, exponent) = width.raw match {
        case 16 => (10L, 0x1fL)
        case 32 => (23L, 0xffL)
        case _  => (52L, 0x7ffL)
      }
      algebra.let(
        "e",
        algebra.and(algebra.shiftRightLogical(value, algebra.literal(fraction)), algebra.literal(exponent))
      )(e =>
        algebra.let("f", algebra.and(value, algebra.literal((1L << fraction) - 1L)))(f =>
          algebra.let(
            "n",
            algebra.notEqual(algebra.and(value, algebra.literal(1L << (width - 1))), algebra.literal(0L))
          )(negative =>
            algebra.select(
              algebra.equal(e, algebra.literal(exponent)),
              algebra.select(
                algebra.equal(f, algebra.literal(0L)),
                algebra.select(negative, algebra.literal(2L), algebra.literal(9L)),
                algebra.select(
                  algebra.equal(algebra.and(f, algebra.literal(1L << (fraction - 1))), algebra.literal(0L)),
                  algebra.literal(0L),
                  algebra.literal(1L)
                )
              ),
              algebra.select(
                algebra.equal(e, algebra.literal(0L)),
                algebra.select(
                  algebra.equal(f, algebra.literal(0L)),
                  algebra.select(negative, algebra.literal(5L), algebra.literal(6L)),
                  algebra.select(negative, algebra.literal(4L), algebra.literal(7L))
                ),
                algebra.select(negative, algebra.literal(3L), algebra.literal(8L))
              )
            )
          )
        )
      )
    }

    /** V_FREXP_EXP: the exponent of a finite non-zero value as a fraction in [0.5, 1), otherwise zero. A denormal's
      * exponent counts its fraction's leading zeros.
      */
    inline def frexpExponent(value: V, width: Int in (32, 64)): V = {
      val fraction = if (width == 64) 52L else 23L
      val exponent = if (width == 64) 0x7ffL else 0xffL
      algebra.let("m", algebra.and(value, algebra.literal((1L << (width - 1)) - 1L)))(magnitude =>
        algebra.let("e", algebra.shiftRightLogical(magnitude, algebra.literal(fraction)))(e =>
          algebra.select(
            algebra.or(algebra.equal(magnitude, algebra.literal(0L)), algebra.equal(e, algebra.literal(exponent))),
            algebra.literal(0L),
            algebra.truncate(
              algebra.select(
                algebra.equal(e, algebra.literal(0L)),
                algebra.subtract(
                  algebra.literal(width - (exponent / 2 - 1) - fraction),
                  algebra.leadingZeros(magnitude, width.widen[Int in 1 ~ 64])
                ),
                algebra.subtract(e, algebra.literal(exponent / 2 - 1))
              ),
              32
            )
          )
        )
      )
    }
  }

  /** 64-bit VOP2 and VOP3 operations on modified register pairs; `scaled` takes LDEXP's dword exponent. */
  inline def binary64[V, R](inline algebra: Algebra[V], operation: VectorBinary64Operation)(
      inline sinks: AMDGPUSinks[V, R],
      dest: VectorRegisterIndex,
      srcA: InstructionSource,
      srcB: InstructionSource,
      modifiers: SourceModifiers
  ): R = operation match {
    case VectorBinary64Operation.LoadExponent =>
      sinks.pairDword(dest, srcA, srcB, PairRead.Floating, modifiers)((a, b) => algebra.loadExponent(a, b, 64))
    case VectorBinary64Operation.Add =>
      sinks.pairBinary(dest, srcA, srcB, PairRead.Floating, modifiers)((a, b) =>
        algebra.primitive(a, b)(Floats.add(_: Float64, _: Float64))
      )
    case VectorBinary64Operation.Multiply =>
      sinks.pairBinary(dest, srcA, srcB, PairRead.Floating, modifiers)((a, b) =>
        algebra.primitive(a, b)(Floats.mul(_: Float64, _: Float64))
      )
    case VectorBinary64Operation.Maximum =>
      sinks.pairBinary(dest, srcA, srcB, PairRead.Floating, modifiers)((a, b) => algebra.maximumNumber(a, b, 64))
    case VectorBinary64Operation.Minimum =>
      sinks.pairBinary(dest, srcA, srcB, PairRead.Floating, modifiers)((a, b) => algebra.minimumNumber(a, b, 64))
    case VectorBinary64Operation.MaximumPropagate =>
      sinks.pairBinary(dest, srcA, srcB, PairRead.Floating, modifiers)((a, b) => algebra.maximumPropagate(a, b, 64))
    case VectorBinary64Operation.MinimumPropagate =>
      sinks.pairBinary(dest, srcA, srcB, PairRead.Floating, modifiers)((a, b) => algebra.minimumPropagate(a, b, 64))
  }

  /** V_MAD_F32 rounds the product before the sum; V_FMA_F32 rounds once. */
  inline def multiplyAdd[V, R](inline algebra: Algebra[V], operation: VectorMadF32Operation, clamp: Boolean)(
      inline sinks: AMDGPUSinks[V, R],
      dest: VectorRegisterIndex,
      srcA: InstructionSource,
      srcB: InstructionSource,
      srcC: InstructionSource,
      modifiers: SourceModifiers
  ): R = operation match {
    case VectorMadF32Operation.FusedMultiplyAdd =>
      single(algebra, clamp)(sinks, dest, srcA, srcB, srcC, modifiers)(algebra.fusedMultiplyAdd(_, _, _, 32))
    case VectorMadF32Operation.MultiplyAdd =>
      single(algebra, clamp)(sinks, dest, srcA, srcB, srcC, modifiers)((a, b, c) =>
        algebra.addSingle(algebra.multiplySingle(a, b), c)
      )
    case VectorMadF32Operation.MultiplyAddLegacy =>
      single(algebra, clamp)(sinks, dest, srcA, srcB, srcC, modifiers)((a, b, c) =>
        algebra.addSingle(algebra.primitive(a, b)(Floats.mulLegacyF32), c)
      )
    case VectorMadF32Operation.FusedMultiplyAddLegacy =>
      single(algebra, clamp)(sinks, dest, srcA, srcB, srcC, modifiers)(algebra.primitive(_, _, _)(Floats.fmaLegacyF32))
    case VectorMadF32Operation.MultiplyLighting =>
      single(algebra, clamp)(sinks, dest, srcA, srcB, srcC, modifiers)(algebra.primitive(_, _, _)(Floats.mullitF32))
  }

  /** A binary32 VOP3 operation on three modified sources, its denormals as MODE has them, saturated when `clamp`. */
  inline def single[V, R](inline algebra: Algebra[V], clamp: Boolean)(
      inline sinks: AMDGPUSinks[V, R],
      dest: VectorRegisterIndex,
      srcA: InstructionSource,
      srcB: InstructionSource,
      srcC: InstructionSource,
      modifiers: SourceModifiers
  )(inline operation: (V, V, V) => V): R =
    sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
      algebra.saturated(clamp, algebra.flushedTernary(sinks.modeRegister, 32, a, b, c)(operation), 32)
    )

  /** V_DIV_SCALE: the operand pre-scaled away from the denormal range. */
  extension [V](inline algebra: Algebra[V]) {
    inline def divisionScale(width: Int in (32, 64), a: V, b: V, c: V): V =
      if (width == 64) algebra.primitive(a, b, c)(Floats.divScale(_: Float64, _: Float64, _: Float64))
      else algebra.primitive(a, b, c)(Floats.divScale(_: Float32, _: Float32, _: Float32))

    /** [[divisionScale]] on operands as MODE has their denormals read. */
    inline def flushedScale(mode: V, width: Int in (32, 64), a: V, b: V, c: V): V = {
      val wide = width.widen[Int in (16, 32, 64)]
      algebra.divisionScale(
        width,
        algebra.denormalInput(mode, a, wide),
        algebra.denormalInput(mode, b, wide),
        algebra.denormalInput(mode, c, wide)
      )
    }

    /** The flag V_DIV_SCALE leaves for V_DIV_FMAS, from the denominator and numerator. */
    inline def divisionScaleFlag(width: Int in (32, 64), b: V, c: V): V =
      if (width == 64) algebra.primitive(b, c)(Floats.divScaleFlag(_: Float64, _: Float64))
      else algebra.primitive(b, c)(Floats.divScaleFlag(_: Float32, _: Float32))

    /** V_DIV_FMAS: a fused multiply-add, rescaled when the flag is set. */
    inline def divisionFusedMultiplyAdd(width: Int in (32, 64), a: V, b: V, c: V, scaled: V): V =
      if (width == 64)
        algebra.primitive(a, b, c, scaled)(Floats.divFmas(_: Float64, _: Float64, _: Float64, _: Bool))
      else algebra.primitive(a, b, c, scaled)(Floats.divFmas(_: Float32, _: Float32, _: Float32, _: Bool))

    /** V_TRIG_PREOP_F64: the segment of 2/pi the dword selects, scaled for the operand's exponent. */
    inline def trigPreop(value: V, segment: V): V =
      algebra.primitive(value, segment)(Floats.trigPreopF64)

    /** The bfloat16 halves of `a` and `b` widened exactly, the low product fused with the rounded high one, plus `d`.
      */
    inline def dotBFloat16(a: V, b: V, d: V): V =
      algebra.let(
        "p",
        algebra.fusedMultiplyAdd(
          algebra.bfloat16(a),
          algebra.bfloat16(b),
          algebra.multiplySingle(algebra.bfloat16(algebra.high16(a)), algebra.bfloat16(algebra.high16(b))),
          32
        )
      )(products => algebra.addSingle(products, d))

    /** The low half's bfloat16 as binary32, exactly. */
    inline def bfloat16(value: V): V =
      algebra.shiftLeft(algebra.truncate(value, 16), algebra.literal(16L))
  }

  /** V_PK_ADD_F32, V_PK_MUL_F32 and V_PK_FMA_F32 on one dword of each source. */
  inline def packedSingle[V, R](inline algebra: Algebra[V], instruction: Instruction.VPackedF32)(
      inline sinks: AMDGPUSinks[V, R]
  ): R = instruction.operation match {
    case PackedF32Operation.Add =>
      sinks.packedSingle(instruction)((a, b, _) =>
        algebra.flushedBinary(sinks.modeRegister, 32, a, b)(algebra.addSingle)
      )
    case PackedF32Operation.Multiply =>
      sinks.packedSingle(instruction)((a, b, _) =>
        algebra.flushedBinary(sinks.modeRegister, 32, a, b)(algebra.multiplySingle)
      )
    case PackedF32Operation.Fma =>
      sinks.packedSingle(instruction)((a, b, c) =>
        algebra.flushedTernary(sinks.modeRegister, 32, a, b, c)(algebra.fusedMultiplyAdd(_, _, _, 32))
      )
  }

  /** V_PACK_B32_F16: two binary16 values, saturated when `clamp`, as the low and high halves. */
  extension [V](inline algebra: Algebra[V]) {
    inline def packHalves(clamp: Boolean, a: V, b: V): V =
      algebra.pack(
        algebra.truncate(algebra.saturated(clamp, a, 16), 16),
        algebra.truncate(algebra.saturated(clamp, b, 16), 16)
      )
  }

  /** V_DOT on the packed elements of SRC0 and SRC1 and the SRC2 dword (CDNA3 ISA 12.10). */
  inline def dot[V, R](inline algebra: Algebra[V], operation: DotOperation, clamp: Boolean)(
      inline sinks: AMDGPUSinks[V, R],
      dest: VectorRegisterIndex,
      srcA: InstructionSource,
      srcB: InstructionSource,
      srcC: InstructionSource,
      modifiers: SourceModifiers
  ): R = operation match {
    case DotOperation.Halves =>
      // the hardware rounds the whole dot product once and does not clamp it
      sinks.vectorTernary(dest, srcA, srcB, srcC, SourceModifiers.Unmodified, 32)((a, b, c) =>
        algebra.primitive(a, b, c)((x: UInt[32], y: UInt[32], z: Float32) => Dots.halvesToFloat(x, y, z))
      )
    case DotOperation.AlignedHalves =>
      sinks.vectorTernary(dest, srcA, srcB, srcC, SourceModifiers.Unmodified, 32)((a, b, c) =>
        algebra.primitive(a, b, c)((x: UInt[32], y: UInt[32], z: Float32) => Dots.halvesToFloatAligned(x, y, z))
      )
    case DotOperation.BFloat16Halves =>
      sinks.vectorTernary(dest, srcA, srcB, srcC, SourceModifiers.Unmodified, 32)((a, b, c) =>
        algebra.saturated(clamp, algebra.dotBFloat16(a, b, c), 32)
      )
    case DotOperation.SignedBytes   => integerDot(algebra, 8, signed = true, clamp)(sinks, dest, srcA, srcB, srcC)
    case DotOperation.UnsignedBytes => integerDot(algebra, 8, signed = false, clamp)(sinks, dest, srcA, srcB, srcC)
    case _                          =>
      sinks.section(operation match {
        case DotOperation.SignedShorts   => integerDot(algebra, 16, signed = true, clamp)(sinks, dest, srcA, srcB, srcC)
        case DotOperation.UnsignedShorts =>
          integerDot(algebra, 16, signed = false, clamp)(sinks, dest, srcA, srcB, srcC)
        case _ =>
          sinks.section(operation match {
            case DotOperation.SignedNibbles =>
              integerDot(algebra, 4, signed = true, clamp)(sinks, dest, srcA, srcB, srcC)
            case DotOperation.UnsignedNibbles =>
              integerDot(algebra, 4, signed = false, clamp)(sinks, dest, srcA, srcB, srcC)
            case DotOperation.MixedBytes(signedA, signedB) =>
              mixedDot(algebra, 8, signedA, signedB, clamp)(sinks, dest, srcA, srcB, srcC)
            case DotOperation.MixedNibbles(signedA, signedB) =>
              mixedDot(algebra, 4, signedA, signedB, clamp)(sinks, dest, srcA, srcB, srcC)
            case _ =>
              sinks.section(operation match {
                case DotOperation.HalvesToHalf =>
                  sinks.vectorTernary(dest, srcA, srcB, srcC, SourceModifiers.Unmodified, 32)((a, b, c) =>
                    algebra.primitive(a, b, algebra.truncate(c, 16))(Dots.halves)
                  )
                case DotOperation.BFloat16HalvesToBFloat16 =>
                  sinks.vectorTernary(dest, srcA, srcB, srcC, SourceModifiers.Unmodified, 32)((a, b, c) =>
                    algebra.primitive(a, b, algebra.truncate(c, 16))(Dots.bfloat16)
                  )
                case DotOperation.EightBitFloats(formatA, formatB) =>
                  sinks.vectorTernary(dest, srcA, srcB, srcC, modifiers, 32)((a, b, c) =>
                    algebra.primitive(a, b, c)((x: UInt[32], y: UInt[32], z: Float32) =>
                      Dots.eightBit(x, y, z, formatA, formatB)
                    )
                  )
                case other => throw IllegalStateException(s"$other has no staged dot product")
              })
          })
      })
  }

  /** V_PK_FMA_F16, V_PK_MAD_U16/I16 and V_PK_MINIMUM3/MAXIMUM3_F16 on each triple of halves. */
  inline def packedTernary[V, R](inline algebra: Algebra[V], instruction: Instruction.VPackedTernary)(
      inline sinks: AMDGPUSinks[V, R]
  ): R = {
    val clamp = instruction.clamp
    instruction.operation match {
      case PackedTernaryOperation.FusedMultiplyAddF16 =>
        sinks.packedHalf(instruction)((a, b, c) =>
          algebra.packedHalfFusedMultiplyAdd(sinks.modeRegister, clamp, a, b, c)
        )
      case PackedTernaryOperation.MultiplyAddU16 =>
        sinks.packedHalf(instruction)((a, b, c) =>
          algebra.packedInteger(algebra.add(algebra.multiply(a, b), c), clamp, signed = false)
        )
      case PackedTernaryOperation.MultiplyAddI16 =>
        sinks.packedHalf(instruction)((a, b, c) =>
          algebra.packedInteger(algebra.multiplyAddSigned16(a, b, c), clamp, signed = true)
        )
      case PackedTernaryOperation.Minimum3PropagateF16 =>
        sinks.packedHalf(instruction)((a, b, c) =>
          algebra.saturated(clamp, algebra.minimumPropagate(algebra.minimumPropagate(a, b, 16), c, 16), 16)
        )
      case PackedTernaryOperation.Maximum3PropagateF16 =>
        sinks.packedHalf(instruction)((a, b, c) =>
          algebra.saturated(clamp, algebra.maximumPropagate(algebra.maximumPropagate(a, b, 16), c, 16), 16)
        )
    }
  }

  /** V_PK_FMA_F16 on one half of each source, its denormals as MODE has them. */
  extension [V](inline algebra: Algebra[V]) {
    inline def packedHalfFusedMultiplyAdd(mode: V, clamp: Boolean, a: V, b: V, c: V): V =
      algebra.saturated(clamp, algebra.flushedTernary(mode, 16, a, b, c)(algebra.fusedMultiplyAdd(_, _, _, 16)), 16)

    /** V_FMA_MIX and V_MAD_MIX into a binary16 half of the destination's previous dword. Bit `i` of `selectionHigh`
      * converts source `i` from the binary16 half bit `i` of `selection` names; `absolute` and `negate` then apply. The
      * unfused forms round the binary32 product first.
      */
    inline def mixedHalf(
        selection: Int,
        selectionHigh: Int,
        negate: Int,
        absolute: Int,
        clamp: Boolean,
        high: Boolean,
        fused: Boolean,
        a: V,
        b: V,
        c: V,
        d: V
    ): V =
      algebra.mergeHalf(
        d,
        algebra.saturated(
          clamp,
          if (fused)
            algebra.primitive(
              algebra.mixed(a, 0, selection, selectionHigh, negate, absolute),
              algebra.mixed(b, 1, selection, selectionHigh, negate, absolute),
              algebra.mixed(c, 2, selection, selectionHigh, negate, absolute)
            )(Floats.fmaMixF16)
          else
            algebra.primitive(
              algebra.multiplySingle(
                algebra.mixed(a, 0, selection, selectionHigh, negate, absolute),
                algebra.mixed(b, 1, selection, selectionHigh, negate, absolute)
              ),
              algebra.mixed(c, 2, selection, selectionHigh, negate, absolute)
            )(Floats.madMixF16),
          16
        ),
        high
      )

    /** V_FMA_MIX_F32 and V_MAD_MIX_F32, with operands as [[mixedHalf]] reads them. */
    inline def mixedSingle(
        selection: Int,
        selectionHigh: Int,
        negate: Int,
        absolute: Int,
        clamp: Boolean,
        fused: Boolean,
        a: V,
        b: V,
        c: V
    ): V =
      algebra.saturated(
        clamp,
        if (fused)
          algebra.fusedMultiplyAdd(
            algebra.mixed(a, 0, selection, selectionHigh, negate, absolute),
            algebra.mixed(b, 1, selection, selectionHigh, negate, absolute),
            algebra.mixed(c, 2, selection, selectionHigh, negate, absolute),
            32
          )
        else
          algebra.addSingle(
            algebra.multiplySingle(
              algebra.mixed(a, 0, selection, selectionHigh, negate, absolute),
              algebra.mixed(b, 1, selection, selectionHigh, negate, absolute)
            ),
            algebra.mixed(c, 2, selection, selectionHigh, negate, absolute)
          ),
        32
      )

    /** Mixed-precision source `index` as binary32 bits. */
    inline def mixed(
        value: V,
        inline index: Int,
        selection: Int,
        selectionHigh: Int,
        negate: Int,
        absolute: Int
    ): V = {
      val converted =
        if ((selectionHigh & (1 << index)) != 0)
          algebra.halfToSingle(if ((selection & (1 << index)) != 0) algebra.high16(value) else value)
        else algebra.truncate(value, 32)
      val magnitude =
        if ((absolute & (1 << index)) != 0) algebra.and(converted, algebra.literal(0x7fffffffL)) else converted
      if ((negate & (1 << index)) != 0) algebra.xor(magnitude, algebra.literal(0x80000000L)) else magnitude
    }
  }

  /** VOPD: one half of a dual-issued pair on its two sources, the FMA addend and the lane's VCC bit; the second half
    * also writes the first's results.
    */
  inline def dual[V, R](inline algebra: Algebra[V], half: DualVector, other: DualVector)(
      inline sinks: AMDGPUSinks[V, R],
      inline second: Boolean
  ): R = half.operation match {
    case DualVectorOperation.Move =>
      dualShape(algebra, sinks, second, half, other)((a, _, _, _) => algebra.truncate(a, 32))
    case DualVectorOperation.AddF32 =>
      dualShape(algebra, sinks, second, half, other)((a, b, _, _) => algebra.addSingle(a, b))
    case DualVectorOperation.SubtractF32 =>
      dualShape(algebra, sinks, second, half, other)((a, b, _, _) =>
        algebra.singleOperation(clamp = false, a, b)(Floats.sub)
      )
    case DualVectorOperation.ReverseSubtractF32 =>
      dualShape(algebra, sinks, second, half, other)((a, b, _, _) =>
        algebra.singleOperation(clamp = false, b, a)(Floats.sub)
      )
    case DualVectorOperation.MultiplyF32 =>
      dualShape(algebra, sinks, second, half, other)((a, b, _, _) => algebra.multiplySingle(a, b))
    case DualVectorOperation.FmaF32 =>
      dualShape(algebra, sinks, second, half, other)((a, b, c, _) => algebra.fusedMultiplyAdd(a, b, c, 32))
    case DualVectorOperation.MinimumF32 =>
      dualShape(algebra, sinks, second, half, other)((a, b, _, _) => algebra.minimumNumber(a, b, 32))
    case DualVectorOperation.MaximumF32 =>
      dualShape(algebra, sinks, second, half, other)((a, b, _, _) => algebra.maximumNumber(a, b, 32))
    case DualVectorOperation.ConditionalMask =>
      dualShape(algebra, sinks, second, half, other)((a, b, _, condition) => algebra.conditional(a, b, condition))
    case DualVectorOperation.AddU32 =>
      dualShape(algebra, sinks, second, half, other)((a, b, _, _) => algebra.truncate(algebra.add(a, b), 32))
    case DualVectorOperation.ShiftLeft32 =>
      dualShape(algebra, sinks, second, half, other)((a, b, _, _) =>
        algebra.truncate(algebra.shiftLeft(b, algebra.amount(a, 31L)), 32)
      )
    case DualVectorOperation.And32 =>
      dualShape(algebra, sinks, second, half, other)((a, b, _, _) => algebra.truncate(algebra.and(a, b), 32))
    case DualVectorOperation.MultiplyLegacyF32 =>
      dualShape(algebra, sinks, second, half, other)((a, b, _, _) => algebra.primitive(a, b)(Floats.mulLegacyF32))
    case DualVectorOperation.DotHalvesF32 =>
      dualShape(algebra, sinks, second, half, other)((a, b, c, _) =>
        algebra.primitive(a, b, c)((x: UInt[32], y: UInt[32], z: Float32) => Dots.halvesToFloat(x, y, z))
      )
    case DualVectorOperation.DotAlignedHalvesF32 =>
      dualShape(algebra, sinks, second, half, other)((a, b, c, _) =>
        algebra.primitive(a, b, c)((x: UInt[32], y: UInt[32], z: Float32) => Dots.halvesToFloatAligned(x, y, z))
      )
    case DualVectorOperation.DotBFloat16F32 =>
      dualShape(algebra, sinks, second, half, other)((a, b, c, _) => algebra.dotBFloat16(a, b, c))
    case DualVectorOperation.SubtractU32 =>
      dualShape(algebra, sinks, second, half, other)((a, b, _, _) => algebra.truncate(algebra.subtract(a, b), 32))
    case DualVectorOperation.MinimumI32 =>
      dualShape(algebra, sinks, second, half, other)((a, b, _, _) =>
        algebra.extremum(a, b, 32, signed = true, maximum = false)
      )
    case DualVectorOperation.MaximumI32 =>
      dualShape(algebra, sinks, second, half, other)((a, b, _, _) =>
        algebra.extremum(a, b, 32, signed = true, maximum = true)
      )
    case DualVectorOperation.ShiftRight32 =>
      dualShape(algebra, sinks, second, half, other)((a, b, _, _) =>
        algebra.shiftRightLogical(algebra.truncate(b, 32), algebra.amount(a, 31L))
      )
    case DualVectorOperation.AddF64 =>
      dualShape(algebra, sinks, second, half, other)((a, b, _, _) =>
        algebra.primitive(a, b)(Floats.add(_: Float64, _: Float64))
      )
    case DualVectorOperation.MultiplyF64 =>
      dualShape(algebra, sinks, second, half, other)((a, b, _, _) =>
        algebra.primitive(a, b)(Floats.mul(_: Float64, _: Float64))
      )
    case DualVectorOperation.FmaF64 =>
      dualShape(algebra, sinks, second, half, other)((a, b, c, _) => algebra.fusedMultiplyAdd(a, b, c, 64))
    case DualVectorOperation.MinimumF64 =>
      dualShape(algebra, sinks, second, half, other)((a, b, _, _) => algebra.minimumNumber(a, b, 64))
    case DualVectorOperation.MaximumF64 =>
      dualShape(algebra, sinks, second, half, other)((a, b, _, _) => algebra.maximumNumber(a, b, 64))
    // V_BITOP3 with a third input of zero: bit `(a << 2) | (b << 1)` of the truth table
    case DualVectorOperation.BitOperation2 =>
      dualShape(algebra, sinks, second, half, other)((a, b, _, _) =>
        algebra.truncate(
          algebra.blend(
            a,
            algebra.blend(b, algebra.entry(half.table, 6), algebra.entry(half.table, 4)),
            algebra.blend(b, algebra.entry(half.table, 2), algebra.entry(half.table, 0))
          ),
          32
        )
      )
    case DualVectorOperation.ShiftRightArithmetic32 =>
      dualShape(algebra, sinks, second, half, other)((a, b, _, _) =>
        algebra.truncate(algebra.shiftRightArithmetic(algebra.signExtend(b, 32), algebra.amount(a, 31L)), 32)
      )
  }

  inline def dualShape[V, R](
      inline algebra: Algebra[V],
      inline sinks: AMDGPUSinks[V, R],
      inline second: Boolean,
      half: DualVector,
      other: DualVector
  )(
      inline kernel: (V, V, V, V) => V
  ): R =
    inline if (second)
      sinks.dualSecond(half, other)((a, b, c, condition) => algebra.negatedKernel(half, a, b, c, condition)(kernel))
    else sinks.dualFirst(half)((a, b, c, condition) => algebra.negatedKernel(half, a, b, c, condition)(kernel))

  extension [V](inline algebra: Algebra[V]) {
    inline def negatedKernel(half: DualVector, a: V, b: V, c: V, condition: V)(
        inline kernel: (V, V, V, V) => V
    ): V = kernel(algebra.negated(half, a, 0), algebra.negated(half, b, 1), algebra.negated(half, c, 2), condition)

    /** Source `index` of a dual-issue half, its sign flipped where the VOPD3 NEG bits name it. */
    inline def negated(half: DualVector, value: V, inline index: Int): V =
      if ((half.negate & (1 << index)) == 0) value
      else algebra.xor(value, algebra.literal(if (half.operation.wide) Long.MinValue else 0x80000000L))

    /** VOP3 OMOD: the `width`-bit result scaled with denormals flushed, then saturated when `clamp`. */
    inline def outputModifier(
        scale: OutputScale,
        width: Int in (16, 32, 64),
        clamp: Boolean,
        value: V
    ): V =
      algebra.saturated(
        clamp,
        width.raw match {
          case 16 => algebra.primitive(value)((x: Float16) => Floats.omod(x, scale.factor))
          case 32 => algebra.primitive(value)((x: Float32) => Floats.omod(x, scale.factor))
          case _  => algebra.primitive(value)((x: Float64) => Floats.omod(x, scale.factor))
        },
        width
      )
  }

}
