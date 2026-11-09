package net.kurobako.gemstone.isa.spirv

import ExtendedInstruction.GlslStd450 as Glsl
import ExtendedInstruction.OpenClStd as OpenCL
import Instruction.*
import net.kurobako.gemstone.profile.*

private[isa] object InstructionPipes {
  private val glslTranscendental: Set[Int] = Set(
    Glsl.Sin,
    Glsl.Cos,
    Glsl.Tan,
    Glsl.Pow,
    Glsl.Exp,
    Glsl.Log,
    Glsl.Exp2,
    Glsl.Log2,
    Glsl.Sqrt,
    Glsl.InverseSqrt,
    Glsl.Asin,
    Glsl.Acos,
    Glsl.Atan,
    Glsl.Atan2,
    Glsl.Sinh,
    Glsl.Cosh,
    Glsl.Tanh,
    Glsl.Asinh,
    Glsl.Acosh,
    Glsl.Atanh
  )

  private val openClTranscendental: Set[Int] = Set(
    OpenCL.Cos,
    OpenCL.Exp,
    OpenCL.Exp2,
    OpenCL.Exp10,
    OpenCL.Log,
    OpenCL.Log2,
    OpenCL.Log10,
    OpenCL.Pow,
    OpenCL.Pown,
    OpenCL.Powr,
    OpenCL.Rootn,
    OpenCL.Rsqrt,
    OpenCL.Sin,
    OpenCL.Sqrt,
    OpenCL.Tan,
    OpenCL.Acos,
    OpenCL.Acosh,
    OpenCL.Acospi,
    OpenCL.Asin,
    OpenCL.Asinh,
    OpenCL.Asinpi,
    OpenCL.Atan,
    OpenCL.Atan2,
    OpenCL.Atan2pi,
    OpenCL.Atanh,
    OpenCL.Atanpi,
    OpenCL.Cosh,
    OpenCL.Sinh,
    OpenCL.Tanh,
    OpenCL.Sinpi,
    OpenCL.Cospi,
    OpenCL.Tanpi,
    OpenCL.Cbrt,
    OpenCL.Hypot,
    OpenCL.NativeCos,
    OpenCL.NativeDivide,
    OpenCL.NativeExp,
    OpenCL.NativeExp2,
    OpenCL.NativeExp10,
    OpenCL.NativeLog,
    OpenCL.NativeLog2,
    OpenCL.NativeLog10,
    OpenCL.NativePowr,
    OpenCL.NativeRecip,
    OpenCL.NativeRsqrt,
    OpenCL.NativeSin,
    OpenCL.NativeSqrt,
    OpenCL.NativeTan
  )

  private inline def floating(typeId: Int, inline width: Int => Option[Int], narrow: Pipe): Option[Pipe] =
    width(typeId).map(bits => if (bits == 64) Pipe.Fp64 else narrow)

  private inline def extendedPipe(
      instruction: ExtInst,
      inline width: Int => Option[Int],
      inline integer: Int => Boolean,
      inline sets: Int => Option[ExtendedSet]
  ): Option[Pipe] =
    sets(Operand.id(instruction.set)).flatMap { set =>
      val opcode         = SPIRVStaged.extendedOpcode(instruction, openCL = set == ExtendedSet.OpenCL)
      val transcendental = if (set == ExtendedSet.OpenCL) openClTranscendental else glslTranscendental
      val resultType     = Operand.id(instruction.resultType)
      if (transcendental.contains(opcode)) floating(resultType, width, Pipe.Mufu)
      else if (integer(resultType)) Some(Pipe.Alu)
      else floating(resultType, width, Pipe.Fma)
    }

  inline def of(
      instruction: Instruction,
      inline valueType: Int => Int,
      inline width: Int => Option[Int],
      inline integer: Int => Boolean,
      inline sets: Int => Option[ExtendedSet]
  ): Option[Pipe] =
    instruction match {
      case _: (FNegate | FAdd | FSub | FMul | FmaKHR | VectorTimesScalar | MatrixTimesScalar | VectorTimesMatrix |
            MatrixTimesVector | MatrixTimesMatrix | OuterProduct | Dot | FConvert | ConvertSToF | ConvertUToF) =>
        floating(Operand.id(instruction.operands(0)), width, Pipe.Fma)
      case _: (FDiv | FRem | FMod)        => floating(Operand.id(instruction.operands(0)), width, Pipe.Mufu)
      case _: (ConvertFToS | ConvertFToU) => floating(valueType(Operand.id(instruction.operands(2))), width, Pipe.Fma)
      case _: (IMul | UMulExtended | SMulExtended | SDot | UDot | SUDot | SDotAccSat | UDotAccSat | SUDotAccSat) =>
        Some(Pipe.Fma)
      case _: (SNegate | IAdd | ISub | UDiv | SDiv | UMod | SRem | SMod | IAddCarry | ISubBorrow | ShiftRightLogical |
            ShiftRightArithmetic | ShiftLeftLogical | BitwiseOr | BitwiseXor | BitwiseAnd | Not | BitFieldInsert |
            BitFieldSExtract | BitFieldUExtract | BitReverse | BitCount | BitwiseFunctionINTEL |
            CooperativeMatrixReduceNV) =>
        Some(Pipe.Alu)
      case _: (CooperativeMatrixMulAddKHR | CooperativeMatrixMulAddNV) => Some(Pipe.Tensor)
      case extInst: ExtInst                                            => extendedPipe(extInst, width, integer, sets)
      case _                                                           => None
    }
}
