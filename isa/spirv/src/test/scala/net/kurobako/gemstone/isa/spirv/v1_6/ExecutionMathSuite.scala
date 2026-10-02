package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.spirv.v1_6 as spv
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class ExecutionMathSuite extends munit.FunSuite {
  import execution.*

  test("dispatch executes typed OpenCL.std scalar f64 hypot") {
    val voidType     = operand(OperandKind.IdResult, 1)
    val f64          = operand(OperandKind.IdResult, 2)
    val pointerType  = operand(OperandKind.IdResult, 3)
    val functionType = operand(OperandKind.IdResult, 4)
    val opencl       = operand(OperandKind.IdResult, 5)
    val function     = operand(OperandKind.IdResult, 6)
    val output       = operand(OperandKind.IdResult, 7)
    val left         = operand(OperandKind.IdResult, 8)
    val right        = operand(OperandKind.IdResult, 9)
    val result       = operand(OperandKind.IdResult, 11)
    val module       = Module(
      Header(ISA.version, Word(0), Word(20), Word(0)),
      Vec(
        Instruction.ExtInstImport(opencl, stringOperand("OpenCL.std")),
        Instruction.TypeVoid(voidType),
        Instruction.TypeFloat(f64, operand(OperandKind.LiteralInteger, 64), None),
        Instruction.TypePointer(pointerType, operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup), f64),
        Instruction.TypeFunction(functionType, voidType, Vec(pointerType, f64, f64)),
        Instruction.EntryPoint(
          operand(OperandKind.ExecutionModel, spv.ExecutionModel.Kernel),
          function,
          stringOperand("hypot"),
          Vec.empty
        ),
        Instruction.Function(
          voidType,
          function,
          operand(OperandKind.FunctionControl, spv.FunctionControl.None),
          functionType
        ),
        Instruction.FunctionParameter(pointerType, output),
        Instruction.FunctionParameter(f64, left),
        Instruction.FunctionParameter(f64, right),
        Instruction.Label(operand(OperandKind.IdResult, 10)),
        Instruction.ExtInst(f64, result, opencl, operand(OperandKind.LiteralExtInstInteger, 32), Vec(left, right)),
        Instruction.Store(output, result, None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val allocation = AllocationId(22L)
    val memory     = ArrayMemory(allocation, 8)
    val dispatch   = KernelTask
      .validated(
        "hypot",
        Dimensions.One,
        Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(allocation, 0L),
          KernelTask.Argument.Scalar(LittleEndian.toVec(
            java.lang.Double.doubleToRawLongBits(3.0),
            8
          )),
          KernelTask.Argument.Scalar(LittleEndian.toVec(
            java.lang.Double.doubleToRawLongBits(4.0),
            8
          ))
        )
      )
      .toOption
      .get

    assertEquals(executeUnchecked(module, dispatch, memory), Right(()))
    assertEquals(
      memory.bytes,
      LittleEndian.toVec(java.lang.Double.doubleToRawLongBits(5.0), 8)
    )
  }

  test("dispatch executes OpenCL.std fma with one rounding") {
    val voidType     = operand(OperandKind.IdResult, 1)
    val f32          = operand(OperandKind.IdResult, 2)
    val pointerType  = operand(OperandKind.IdResult, 3)
    val functionType = operand(OperandKind.IdResult, 4)
    val opencl       = operand(OperandKind.IdResult, 5)
    val function     = operand(OperandKind.IdResult, 6)
    val output       = operand(OperandKind.IdResult, 7)
    val left         = operand(OperandKind.IdResult, 8)
    val right        = operand(OperandKind.IdResult, 9)
    val addend       = operand(OperandKind.IdResult, 10)
    val result       = operand(OperandKind.IdResult, 12)
    val module       = Module(
      Header(ISA.version, Word(0), Word(16), Word(0)),
      Vec(
        Instruction.ExtInstImport(opencl, stringOperand("OpenCL.std")),
        Instruction.TypeVoid(voidType),
        Instruction.TypeFloat(f32, operand(OperandKind.LiteralInteger, 32), None),
        Instruction.TypePointer(pointerType, operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup), f32),
        Instruction.TypeFunction(functionType, voidType, Vec(pointerType, f32, f32, f32)),
        Instruction.EntryPoint(
          operand(OperandKind.ExecutionModel, spv.ExecutionModel.Kernel),
          function,
          stringOperand("fma"),
          Vec.empty
        ),
        Instruction.Function(
          voidType,
          function,
          operand(OperandKind.FunctionControl, spv.FunctionControl.None),
          functionType
        ),
        Instruction.FunctionParameter(pointerType, output),
        Instruction.FunctionParameter(f32, left),
        Instruction.FunctionParameter(f32, right),
        Instruction.FunctionParameter(f32, addend),
        Instruction.Label(operand(OperandKind.IdResult, 11)),
        Instruction.ExtInst(
          f32,
          result,
          opencl,
          operand(OperandKind.LiteralExtInstInteger, 26),
          Vec(left, right, addend)
        ),
        Instruction.Store(output, result, None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val allocation = AllocationId(25L)
    val memory     = ArrayMemory(allocation, 4)
    val a          = 1.0f + math.pow(2, -23).toFloat
    val b          = 1.0f - math.pow(2, -23).toFloat
    val c          = -1.0f
    val dispatch   = KernelTask
      .validated(
        "fma",
        Dimensions.One,
        Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(allocation, 0L),
          KernelTask.Argument.Scalar(LittleEndian.toVec(
            java.lang.Float.floatToRawIntBits(a).toLong,
            4
          )),
          KernelTask.Argument.Scalar(LittleEndian.toVec(
            java.lang.Float.floatToRawIntBits(b).toLong,
            4
          )),
          KernelTask.Argument.Scalar(LittleEndian.toVec(
            java.lang.Float.floatToRawIntBits(c).toLong,
            4
          ))
        )
      )
      .toOption
      .get

    assertEquals(executeUnchecked(module, dispatch, memory), Right(()))
    assertEquals(
      memory.bytes,
      LittleEndian.toVec(java.lang.Float.floatToRawIntBits(Math.fma(a, b, c)).toLong, 4)
    )
  }

  test("dispatch executes OpenCL.std native reciprocal") {
    val voidType     = operand(OperandKind.IdResult, 1)
    val f32          = operand(OperandKind.IdResult, 2)
    val pointerType  = operand(OperandKind.IdResult, 3)
    val functionType = operand(OperandKind.IdResult, 4)
    val opencl       = operand(OperandKind.IdResult, 5)
    val function     = operand(OperandKind.IdResult, 6)
    val output       = operand(OperandKind.IdResult, 7)
    val input        = operand(OperandKind.IdResult, 8)
    val result       = operand(OperandKind.IdResult, 10)
    val module       = Module(
      Header(ISA.version, Word(0), Word(11), Word(0)),
      Vec(
        Instruction.ExtInstImport(opencl, stringOperand("OpenCL.std")),
        Instruction.TypeVoid(voidType),
        Instruction.TypeFloat(f32, operand(OperandKind.LiteralInteger, 32), None),
        Instruction.TypePointer(pointerType, operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup), f32),
        Instruction.TypeFunction(functionType, voidType, Vec(pointerType, f32)),
        Instruction.EntryPoint(
          operand(OperandKind.ExecutionModel, spv.ExecutionModel.Kernel),
          function,
          stringOperand("native_recip"),
          Vec.empty
        ),
        Instruction.Function(
          voidType,
          function,
          operand(OperandKind.FunctionControl, spv.FunctionControl.None),
          functionType
        ),
        Instruction.FunctionParameter(pointerType, output),
        Instruction.FunctionParameter(f32, input),
        Instruction.Label(operand(OperandKind.IdResult, 9)),
        Instruction.ExtInst(f32, result, opencl, operand(OperandKind.LiteralExtInstInteger, 90), Vec(input)),
        Instruction.Store(output, result, None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val allocation = AllocationId(23L)
    val memory     = ArrayMemory(allocation, 4)
    val dispatch   = KernelTask
      .validated(
        "native_recip",
        Dimensions.One,
        Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(allocation, 0L),
          KernelTask.Argument.Scalar(LittleEndian.toVec(
            java.lang.Float.floatToRawIntBits(4.0f).toLong,
            4
          ))
        )
      )
      .toOption
      .get

    assertEquals(executeUnchecked(module, dispatch, memory), Right(()))
    assertEquals(
      memory.bytes,
      LittleEndian.toVec(java.lang.Float.floatToRawIntBits(0.25f).toLong, 4)
    )
  }

  test("dispatch executes OpenCL.std native sine") {
    val voidType     = operand(OperandKind.IdResult, 1)
    val f32          = operand(OperandKind.IdResult, 2)
    val pointerType  = operand(OperandKind.IdResult, 3)
    val functionType = operand(OperandKind.IdResult, 4)
    val opencl       = operand(OperandKind.IdResult, 5)
    val function     = operand(OperandKind.IdResult, 6)
    val output       = operand(OperandKind.IdResult, 7)
    val input        = operand(OperandKind.IdResult, 8)
    val result       = operand(OperandKind.IdResult, 10)
    val module       = Module(
      Header(ISA.version, Word(0), Word(11), Word(0)),
      Vec(
        Instruction.ExtInstImport(opencl, stringOperand("OpenCL.std")),
        Instruction.TypeVoid(voidType),
        Instruction.TypeFloat(f32, operand(OperandKind.LiteralInteger, 32), None),
        Instruction.TypePointer(pointerType, operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup), f32),
        Instruction.TypeFunction(functionType, voidType, Vec(pointerType, f32)),
        Instruction.EntryPoint(
          operand(OperandKind.ExecutionModel, spv.ExecutionModel.Kernel),
          function,
          stringOperand("native_sin"),
          Vec.empty
        ),
        Instruction.Function(
          voidType,
          function,
          operand(OperandKind.FunctionControl, spv.FunctionControl.None),
          functionType
        ),
        Instruction.FunctionParameter(pointerType, output),
        Instruction.FunctionParameter(f32, input),
        Instruction.Label(operand(OperandKind.IdResult, 9)),
        Instruction.ExtInst(f32, result, opencl, operand(OperandKind.LiteralExtInstInteger, 92), Vec(input)),
        Instruction.Store(output, result, None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val allocation = AllocationId(24L)
    val memory     = ArrayMemory(allocation, 4)
    val dispatch   = KernelTask
      .validated(
        "native_sin",
        Dimensions.One,
        Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(allocation, 0L),
          KernelTask.Argument.Scalar(LittleEndian.toVec(
            java.lang.Float.floatToRawIntBits(0.5f).toLong,
            4
          ))
        )
      )
      .toOption
      .get

    assertEquals(executeUnchecked(module, dispatch, memory), Right(()))
    val actual = java.lang.Float.intBitsToFloat(LittleEndian.decodeInt32(memory.bytes, 0))
    assert(math.abs(actual - 0.47942555f) < 1.0e-6f)
  }

  test("dispatch executes OpenCL.std native cosine") {
    val voidType     = operand(OperandKind.IdResult, 1)
    val f32          = operand(OperandKind.IdResult, 2)
    val pointerType  = operand(OperandKind.IdResult, 3)
    val functionType = operand(OperandKind.IdResult, 4)
    val opencl       = operand(OperandKind.IdResult, 5)
    val function     = operand(OperandKind.IdResult, 6)
    val output       = operand(OperandKind.IdResult, 7)
    val input        = operand(OperandKind.IdResult, 8)
    val result       = operand(OperandKind.IdResult, 10)
    val module       = Module(
      Header(ISA.version, Word(0), Word(11), Word(0)),
      Vec(
        Instruction.ExtInstImport(opencl, stringOperand("OpenCL.std")),
        Instruction.TypeVoid(voidType),
        Instruction.TypeFloat(f32, operand(OperandKind.LiteralInteger, 32), None),
        Instruction.TypePointer(pointerType, operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup), f32),
        Instruction.TypeFunction(functionType, voidType, Vec(pointerType, f32)),
        Instruction.EntryPoint(
          operand(OperandKind.ExecutionModel, spv.ExecutionModel.Kernel),
          function,
          stringOperand("native_cos"),
          Vec.empty
        ),
        Instruction.Function(
          voidType,
          function,
          operand(OperandKind.FunctionControl, spv.FunctionControl.None),
          functionType
        ),
        Instruction.FunctionParameter(pointerType, output),
        Instruction.FunctionParameter(f32, input),
        Instruction.Label(operand(OperandKind.IdResult, 9)),
        Instruction.ExtInst(f32, result, opencl, operand(OperandKind.LiteralExtInstInteger, 81), Vec(input)),
        Instruction.Store(output, result, None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val allocation = AllocationId(25L)
    val memory     = ArrayMemory(allocation, 4)
    val dispatch   = KernelTask
      .validated(
        "native_cos",
        Dimensions.One,
        Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(allocation, 0L),
          KernelTask.Argument.Scalar(LittleEndian.toVec(
            java.lang.Float.floatToRawIntBits(0.5f).toLong,
            4
          ))
        )
      )
      .toOption
      .get

    assertEquals(executeUnchecked(module, dispatch, memory), Right(()))
    val actual = java.lang.Float.intBitsToFloat(LittleEndian.decodeInt32(memory.bytes, 0))
    assert(math.abs(actual - 0.87758255f) < 1.0e-6f)
  }

  test("floating minimum and maximum preserve IEEE signed zero") {
    assertEquals(VMInterpreter.floatingMinimumBits(16, 0x0000L, 0x8000L), 0x8000L)
    assertEquals(VMInterpreter.floatingMaximumBits(16, 0x0000L, 0x8000L), 0x0000L)
    assertEquals(VMInterpreter.floatingMinimumBits(32, 0L, 0x80000000L), 0x80000000L)
    assertEquals(VMInterpreter.floatingMaximumBits(32, 0L, 0x80000000L), 0L)
    assertEquals(
      VMInterpreter.floatingMinimumBits(64, 0L, 0x8000000000000000L),
      0x8000000000000000L
    )
    assertEquals(VMInterpreter.floatingMaximumBits(64, 0L, 0x8000000000000000L), 0L)
  }

  test("vector-times-scalar multiplies each floating lane") {
    val voidType   = operand(OperandKind.IdResult, 1)
    val floatType  = operand(OperandKind.IdResult, 2)
    val vectorType = operand(OperandKind.IdResult, 3)
    val pointer    = operand(OperandKind.IdResult, 4)
    val signature  = operand(OperandKind.IdResult, 5)
    val function   = operand(OperandKind.IdResult, 6)
    val output     = operand(OperandKind.IdResult, 7)
    val vector     = operand(OperandKind.IdResult, 8)
    val scalar     = operand(OperandKind.IdResult, 9)
    val product    = operand(OperandKind.IdResult, 11)
    val module     = Module(
      Header(ISA.version, Word(0), Word(12), Word(0)),
      Vec(
        Instruction.TypeVoid(voidType),
        Instruction.TypeFloat(floatType, operand(OperandKind.LiteralInteger, 32), None),
        Instruction.TypeVector(vectorType, floatType, operand(OperandKind.LiteralInteger, 2)),
        Instruction.TypePointer(
          pointer,
          operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup),
          vectorType
        ),
        Instruction.TypeFunction(signature, voidType, Vec(pointer, vectorType, floatType)),
        Instruction.EntryPoint(
          operand(OperandKind.ExecutionModel, spv.ExecutionModel.Kernel),
          function,
          stringOperand("vector_scale"),
          Vec.empty
        ),
        Instruction.Function(
          voidType,
          function,
          operand(OperandKind.FunctionControl, spv.FunctionControl.None),
          signature
        ),
        Instruction.FunctionParameter(pointer, output),
        Instruction.FunctionParameter(vectorType, vector),
        Instruction.FunctionParameter(floatType, scalar),
        Instruction.Label(operand(OperandKind.IdResult, 10)),
        Instruction.VectorTimesScalar(vectorType, product, vector, scalar),
        Instruction.Store(output, product, None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val allocation                    = AllocationId(18L)
    val memory                        = ArrayMemory(allocation, 8)
    def bits(value: Float): Vec[Byte] =
      LittleEndian.toVec(java.lang.Float.floatToRawIntBits(value).toLong, 4)
    val dispatch = KernelTask
      .validated(
        "vector_scale",
        Dimensions.One,
        Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(allocation, 0L),
          KernelTask.Argument.Scalar(bits(2.0f) ++ bits(-3.0f)),
          KernelTask.Argument.Scalar(bits(-2.0f))
        )
      )
      .toOption
      .get

    assertEquals(executeUnchecked(module, dispatch, memory), Right(()))
    assertEquals(memory.bytes, bits(-4.0f) ++ bits(6.0f))
  }

  import ExtendedInstruction.OpenClStd.*

  for {
    width <- Seq(16, 32, 64)
    lanes <- Seq(1, 3, 4, 16)
  } {
    test(s"OpenCL fract and modf return and store rounded components at width $width, lanes $lanes") {
      for (
        (x, fraction, integral) <- Seq(
          (0.5, 0.5, 0.0),
          (-1.25, 0.75, -2.0),
          (4.0, 0.0, 4.0),
          (-0.0, -0.0, -0.0),
          (Double.NegativeInfinity, -0.0, Double.NegativeInfinity)
        )
      )
        check(Fract, width, Seq(Seq.fill(lanes)(x)), Seq.fill(lanes)(fraction), Some(Seq.fill(lanes)(integral)))
      for (
        (x, fraction, integral) <- Seq(
          (0.5, 0.5, 0.0),
          (-1.25, -0.25, -1.0),
          (-0.0, -0.0, -0.0),
          (Double.NegativeInfinity, -0.0, Double.NegativeInfinity)
        )
      )
        check(Modf, width, Seq(Seq.fill(lanes)(x)), Seq.fill(lanes)(fraction), Some(Seq.fill(lanes)(integral)))
    }
    test(s"OpenCL frexp normalizes finite, zero and subnormal values at width $width, lanes $lanes") {
      val smallest = width match {
        case 16 => Math.scalb(1.0, -24); case 32 => Math.scalb(1.0, -149); case _ => Double.MinPositiveValue
      }
      val exponent = width match { case 16 => -23; case 32 => -148; case _ => -1073 }
      for ((x, fraction, power) <- Seq((12.0, 0.75, 4), (-0.5, -0.5, 0), (-0.0, -0.0, 0), (smallest, 0.5, exponent)))
        check(
          Frexp,
          width,
          Seq(Seq.fill(lanes)(x)),
          Seq.fill(lanes)(fraction),
          Some(Seq.fill(lanes)(power.toDouble)),
          integerPointer = true
        )
    }
    test(s"OpenCL magnitude selection handles signs, ties and NaNs at width $width, lanes $lanes") {
      for (
        (x, y, minimum, maximum) <- Seq(
          (2.0, -3.0, 2.0, -3.0),
          (-2.0, 2.0, -2.0, 2.0),
          (0.0, -0.0, -0.0, 0.0),
          (Double.NaN, 2.0, 2.0, 2.0),
          (2.0, Double.NaN, 2.0, 2.0)
        )
      ) {
        check(Minmag, width, Seq(Seq.fill(lanes)(x), Seq.fill(lanes)(y)), Seq.fill(lanes)(minimum))
        check(Maxmag, width, Seq(Seq.fill(lanes)(x), Seq.fill(lanes)(y)), Seq.fill(lanes)(maximum))
      }
    }
  }

  for {
    width <- Seq(16, 32, 64)
    lanes <- Seq(1, 3, 4, 16)
  } {
    test(s"OpenCL ilogb returns exact signed exponents at width $width, lanes $lanes") {
      val smallest = width match {
        case 16 => Math.scalb(1.0, -24); case 32 => Math.scalb(1.0, -149); case _ => Double.MinPositiveValue
      }
      val exponent = width match { case 16 => -24; case 32 => -149; case _ => -1074 }
      for (
        (x, power) <- Seq(
          (0.5, -1),
          (-12.0, 3),
          (smallest, exponent),
          (Double.PositiveInfinity, Int.MaxValue),
          (0.0, Int.MinValue),
          (Double.NaN, Int.MaxValue)
        )
      )
        check(Ilogb, width, Seq(Seq.fill(lanes)(x)), Seq.fill(lanes)(power.toDouble), integerResult = true)
    }
    test(s"OpenCL remainder and remquo use nearest even quotient at width $width, lanes $lanes") {
      for (
        (x, y, remain, quotient) <- Seq(
          (7.0, 2.0, -1.0, 4),
          (5.0, 2.0, 1.0, 2),
          (-7.0, 2.0, 1.0, -4),
          (7.0, -2.0, -1.0, -4),
          (-0.0, 2.0, -0.0, 0),
          (517.0, 2.0, 1.0, 2)
        )
      ) {
        check(Remainder, width, Seq(Seq.fill(lanes)(x), Seq.fill(lanes)(y)), Seq.fill(lanes)(remain))
        check(
          Remquo,
          width,
          Seq(Seq.fill(lanes)(x), Seq.fill(lanes)(y)),
          Seq.fill(lanes)(remain),
          Some(Seq.fill(lanes)(quotient.toDouble)),
          integerPointer = true
        )
      }
    }
  }

  for {
    width <- Seq(16, 32, 64)
    lanes <- Seq(1, 3, 4, 16)
  }
    test(s"OpenCL special math dispatch handles scalar and vector lanes at width $width, lanes $lanes") {
      for (
        (opcode, x, expected, ulps) <- Seq(
          (Erf, 1.0, 0.8427007929497149, 16),
          (Erfc, 1.0, 0.15729920705028513, 16),
          (Tgamma, 0.5, 1.772453850905516, 16),
          (Lgamma, 0.5, 0.5723649429247001, 16)
        )
      )
        check(opcode, width, Seq(Seq.fill(lanes)(x)), Seq.fill(lanes)(expected), toleranceUlps = ulps)
      check(
        LgammaR,
        width,
        Seq(Seq.fill(lanes)(-0.5)),
        Seq.fill(lanes)(1.2655121234846454),
        Some(Seq.fill(lanes)(-1.0)),
        integerPointer = true,
        toleranceUlps = 16
      )
    }

  for (approximateHalf <- Seq(false, true)) {
    test(s"OpenCL approximate math families execute every unary operation, half $approximateHalf") {
      val x         = Seq(0.5, 1.0, 2.0, 4.0)
      val functions = Seq[(Int, Int, Double => Double)](
        (HalfCos, NativeCos, Math.cos),
        (HalfExp, NativeExp, Math.exp),
        (HalfExp2, NativeExp2, value => Math.pow(2.0, value)),
        (HalfExp10, NativeExp10, value => Math.pow(10.0, value)),
        (HalfLog, NativeLog, Math.log),
        (HalfLog2, NativeLog2, value => Math.log(value) / Math.log(2.0)),
        (HalfLog10, NativeLog10, Math.log10),
        (HalfRecip, NativeRecip, value => 1.0 / value),
        (HalfRsqrt, NativeRsqrt, value => 1.0 / Math.sqrt(value)),
        (HalfSin, NativeSin, Math.sin),
        (HalfSqrt, NativeSqrt, Math.sqrt),
        (HalfTan, NativeTan, Math.tan)
      )
      functions.foreach { case (halfOpcode, nativeOpcode, reference) =>
        check(if (approximateHalf) halfOpcode else nativeOpcode, 32, Seq(x), x.map(reference), toleranceUlps = 1)
      }
    }
    test(s"OpenCL approximate divide and powr return computed values, half $approximateHalf") {
      check(
        if (approximateHalf) HalfDivide else NativeDivide,
        32,
        Seq(Seq(7.0, -7.0, 0.0, -0.0), Seq.fill(4)(2.0)),
        Seq(3.5, -3.5, 0.0, -0.0)
      )
      check(
        if (approximateHalf) HalfPowr else NativePowr,
        32,
        Seq(Seq(4.0, 2.0, 0.5, 0.25), Seq(0.5, -1.0, 2.0, 0.5)),
        Seq(2.0, 0.5, 0.25, 0.5)
      )
    }
  }

  test("OpenCL powr preserves NaNs and rejects undefined zero and infinity combinations") {
    for {
      opcode <- Seq(Powr, NativePowr, HalfPowr)
      width  <- if (opcode == Powr) Seq(16, 32, 64) else Seq(32)
    } {
      check(
        opcode,
        width,
        Seq(
          Seq(Double.NaN, 0.0, Double.PositiveInfinity, 1.0),
          Seq(0.0, Double.NaN, 0.0, Double.PositiveInfinity)
        ),
        Seq.fill(4)(Double.NaN)
      )
      check(
        opcode,
        width,
        Seq(Seq(1.0, -0.0, Double.PositiveInfinity, -2.0), Seq(Double.NegativeInfinity, -0.0, -0.0, 2.0)),
        Seq.fill(4)(Double.NaN)
      )
      check(
        opcode,
        width,
        Seq(Seq(1.0, 1.0, 0.5, 2.0), Seq(-2.0, 2.0, Double.PositiveInfinity, Double.PositiveInfinity)),
        Seq(1.0, 1.0, 0.0, Double.PositiveInfinity)
      )
    }
  }

  test("OpenCL pointer math handles mixed finite and exceptional lanes") {
    for (width <- Seq(16, 32, 64)) {
      check(
        Frexp,
        width,
        Seq(Seq(Double.PositiveInfinity, Double.NaN, -12.0, -0.0)),
        Seq(Double.PositiveInfinity, Double.NaN, -0.75, -0.0),
        Some(Seq(0.0, 0.0, 4.0, 0.0)),
        integerPointer = true
      )
      check(
        Fract,
        width,
        Seq(Seq(-1.25, 2.75, -0.0, Double.PositiveInfinity)),
        Seq(0.75, 0.75, -0.0, 0.0),
        Some(Seq(-2.0, 2.0, -0.0, Double.PositiveInfinity))
      )
      check(
        Remquo,
        width,
        Seq(Seq(Double.PositiveInfinity, 1.0, Double.NaN, -7.0), Seq(1.0, 0.0, 2.0, 2.0)),
        Seq(Double.NaN, Double.NaN, Double.NaN, 1.0),
        Some(Seq(0.0, 0.0, 0.0, -4.0)),
        integerPointer = true
      )
    }
    check(
      Remquo,
      64,
      Seq(Seq(Double.MaxValue), Seq(Double.MinPositiveValue)),
      Seq(0.0),
      Some(Seq(0.0)),
      integerPointer = true
    )
  }

  test("OpenCL half math rounds the double result directly without a float intermediate") {
    check(Log, 16, Seq(Seq(0.005340576171875)), Seq(-5.23046875))
    check(Exp, 16, Seq(Seq(0.007297515869140625)), Seq(1.0068359375))
    check(Sin, 16, Seq(Seq(0.2093505859375)), Seq(0.2078857421875))
  }

  test("OpenCL half nextafter advances one representable half, including zeros and infinities") {
    for (
      (x, y, expected) <- Seq(
        (0.75244140625, 0.79638671875, 0.7529296875),
        (-0.75244140625, -0.79638671875, -0.7529296875),
        (0.0, -1.0, -Math.scalb(1.0, -24)),
        (-0.0, 1.0, Math.scalb(1.0, -24)),
        (0.0, -0.0, -0.0),
        (65504.0, Double.PositiveInfinity, Double.PositiveInfinity),
        (Double.PositiveInfinity, 0.0, 65504.0),
        (Double.NegativeInfinity, 0.0, -65504.0)
      )
    )
      check(Nextafter, 16, Seq(Seq(x), Seq(y)), Seq(expected))
  }

  test("OpenCL pi-scaled trigonometry reduces arguments before multiplication") {
    check(Sinpi, 64, Seq(Seq(0.8462670594)), Seq(0.4644082125981883), toleranceUlps = 4)
    check(Sinpi, 64, Seq(Seq(0.8740322002)), Seq(0.3854906528116995), toleranceUlps = 4)
    check(Sinpi, 64, Seq(Seq(0.9999999999999999)), Seq(3.487868498008632e-16), toleranceUlps = 4)
    check(Sinpi, 64, Seq(Seq(-0.9999999999999999)), Seq(-3.487868498008632e-16), toleranceUlps = 4)
    check(Sinpi, 64, Seq(Seq(123456789.125)), Seq(-0.3826834323650898), toleranceUlps = 4)
    check(Sinpi, 64, Seq(Seq(-123456789.125)), Seq(0.3826834323650898), toleranceUlps = 4)
    check(Cospi, 64, Seq(Seq(0.8462670594)), Seq(-0.8856212576894007), toleranceUlps = 4)
    check(Cospi, 64, Seq(Seq(0.8740322002)), Seq(-0.9227117407916785), toleranceUlps = 4)
    check(Cospi, 64, Seq(Seq(0.9999999999999999)), Seq(-1.0), toleranceUlps = 4)
    check(Cospi, 64, Seq(Seq(-0.9999999999999999)), Seq(-1.0), toleranceUlps = 4)
    check(Cospi, 64, Seq(Seq(123456789.125)), Seq(-0.9238795325112867), toleranceUlps = 4)
    check(Cospi, 64, Seq(Seq(-123456789.125)), Seq(-0.9238795325112867), toleranceUlps = 4)
    check(Tanpi, 64, Seq(Seq(0.8462670594)), Seq(-0.5243869301532309), toleranceUlps = 4)
    check(Tanpi, 64, Seq(Seq(0.8740322002)), Seq(-0.4177801536165042), toleranceUlps = 4)
    check(Tanpi, 64, Seq(Seq(0.9999999999999999)), Seq(-3.487868498008632e-16), toleranceUlps = 4)
    check(Tanpi, 64, Seq(Seq(-0.9999999999999999)), Seq(3.487868498008632e-16), toleranceUlps = 4)
    check(Tanpi, 64, Seq(Seq(123456789.125)), Seq(0.41421356237309503), toleranceUlps = 4)
    check(Tanpi, 64, Seq(Seq(-123456789.125)), Seq(-0.41421356237309503), toleranceUlps = 4)
    check(Cospi, 64, Seq(Seq(Math.scalb(1.0, 64))), Seq(1.0))
    check(Sinpi, 64, Seq(Seq(-0.0)), Seq(-0.0))
    check(Tanpi, 64, Seq(Seq(1.0, 2.0, -1.0, -2.0)), Seq(-0.0, 0.0, 0.0, -0.0))
    check(
      Tanpi,
      64,
      Seq(Seq(0.5, 1.5, -0.5, -1.5)),
      Seq(Double.PositiveInfinity, Double.NegativeInfinity, Double.NegativeInfinity, Double.PositiveInfinity)
    )
  }

  test("OpenCL half smoothstep rounds arithmetic to the operand precision") {
    check(Smoothstep, 16, Seq(Seq(0.3991507449), Seq(0.7130365772), Seq(0.4567896435)), Seq(0.0888671875))
  }

  private def raw(width: Int, value: Double): Long = width match {
    case 16 => IEEE754.floatToBinary16(value.toFloat).raw.toLong
    case 32 => java.lang.Float.floatToRawIntBits(value.toFloat).toLong & 0xffffffffL
    case _  => java.lang.Double.doubleToRawLongBits(value)
  }

  private def check(
      opcode: Int,
      width: Int,
      inputs: Seq[Seq[Double]],
      expected: Seq[Double],
      stored: Option[Seq[Double]] = None,
      integerPointer: Boolean = false,
      integerResult: Boolean = false,
      toleranceUlps: Int = 0
  ): Unit = {
    val lanes        = expected.size
    val scalar       = operand(OperandKind.IdResult, 2)
    val valueType    = if (lanes == 1) scalar else operand(OperandKind.IdResult, 3)
    val storedScalar = if (integerPointer) operand(OperandKind.IdResult, 16) else scalar
    val storedType   =
      if (integerPointer && lanes != 1) operand(OperandKind.IdResult, 17)
      else if (integerPointer) storedScalar else valueType
    val resultType = if (integerResult) {
      if (lanes == 1) operand(OperandKind.IdResult, 16) else operand(OperandKind.IdResult, 17)
    } else valueType
    val resultWidth   = if (integerResult) 32 else width
    val pointer       = operand(OperandKind.IdResult, 4)
    val storedPointer = operand(OperandKind.IdResult, 18)
    val functionType  = operand(OperandKind.IdResult, 5)
    val opencl        = operand(OperandKind.IdResult, 6)
    val function      = operand(OperandKind.IdResult, 7)
    val output        = operand(OperandKind.IdResult, 8)
    val auxiliary     = operand(OperandKind.IdResult, 19)
    val arguments     = Vec.from(inputs.indices.map(index => operand(OperandKind.IdResult, 9 + index)))
    val declarations  = Vec[Instruction](
      Instruction.ExtInstImport(opencl, stringOperand("OpenCL.std")),
      Instruction.TypeVoid(operand(OperandKind.IdResult, 1)),
      Instruction.TypeFloat(scalar, operand(OperandKind.LiteralInteger, width), None),
      Instruction.TypeInt(
        operand(OperandKind.IdResult, 16),
        operand(OperandKind.LiteralInteger, 32),
        operand(OperandKind.LiteralInteger, 1)
      )
    ) ++
      (if (lanes == 1) Vec.empty[Instruction]
       else Vec[Instruction](
         Instruction.TypeVector(valueType, scalar, operand(OperandKind.LiteralInteger, lanes)),
         Instruction.TypeVector(
           operand(OperandKind.IdResult, 17),
           operand(OperandKind.IdRef, 16),
           operand(OperandKind.LiteralInteger, lanes)
         )
       ))
    val instructions = declarations ++ Vec(
      Instruction.TypePointer(pointer, operand(OperandKind.StorageClass, 5), resultType),
      Instruction.TypePointer(storedPointer, operand(OperandKind.StorageClass, 5), storedType),
      Instruction.TypeFunction(
        functionType,
        operand(OperandKind.IdRef, 1),
        Vec(pointer, storedPointer) ++ Vec.fill(inputs.size)(valueType)
      ),
      Instruction.EntryPoint(operand(OperandKind.ExecutionModel, 6), function, stringOperand("math"), Vec.empty),
      Instruction.Function(
        operand(OperandKind.IdRef, 1),
        function,
        operand(OperandKind.FunctionControl, 0),
        functionType
      ),
      Instruction.FunctionParameter(pointer, output),
      Instruction.FunctionParameter(storedPointer, auxiliary)
    ) ++ arguments.map(argument => Instruction.FunctionParameter(valueType, argument)) ++ Vec(
      Instruction.Label(operand(OperandKind.IdResult, 20)),
      Instruction.ExtInst(
        resultType,
        operand(OperandKind.IdResult, 21),
        opencl,
        operand(OperandKind.LiteralExtInstInteger, opcode),
        arguments ++ (if (stored.isDefined) Vec(auxiliary) else Vec.empty)
      ),
      Instruction.Store(output, operand(OperandKind.IdRef, 21), None),
      Instruction.Return(),
      Instruction.FunctionEnd()
    )
    val module      = Module(Header(ISA.version, Word(0), Word(22), Word(0)), instructions, Endianness.Little)
    val allocation  = AllocationId(1L)
    val stride      = (if (lanes == 3) 4 else lanes) * resultWidth / 8
    val storedWidth = if (integerPointer) 32 else width
    val memory      = ArrayMemory(allocation, stride + (if (lanes == 3) 4 else lanes) * storedWidth / 8)
    def bytes(bits: Seq[Long], bitsWidth: Int): Vec[Byte] =
      Vec.from(bits.flatMap(value =>
        LittleEndian.toVec(value, (bitsWidth / 8).refine[Int where Between[0, 8]].toOption.get)
      ))
    val task = KernelTask.validated(
      "math",
      Dimensions.One,
      Dimensions.One,
      Vec[KernelTask.Argument](
        KernelTask.Argument.Memory(allocation, 0L),
        KernelTask.Argument.Memory(allocation, stride.toLong.unsafe[Long >= 0])
      ) ++
        Vec.from(inputs.map(values => KernelTask.Argument.Scalar(bytes(values.map(raw(width, _)), width))))
    ).toOption.get
    assertEquals(executeUnchecked(module, task, memory), Right(()))
    expected.zipWithIndex.foreach { case (value, lane) =>
      val actual =
        LittleEndian.decode(
          memory.bytes,
          lane * resultWidth / 8,
          (resultWidth / 8).refine[Int where Between[0, 8]].toOption.get
        )
      val wanted = if (integerResult) value.toLong & 0xffffffffL else raw(width, value)
      if (value.isNaN && !integerResult) {
        val exponentMask = width match { case 16 => 0x7c00L; case 32 => 0x7f800000L; case _ => 0x7ff0000000000000L }
        val fractionMask = width match { case 16 => 0x3ffL; case 32 => 0x7fffffL; case _ => 0xfffffffffffffL }
        assert(
          (actual & exponentMask) == exponentMask && (actual & fractionMask) != 0L,
          s"opcode $opcode, width $width, lane $lane, inputs ${inputs.map(_(lane))}: expected NaN, got bits $actual"
        )
      } else assert((BigInt(actual) - BigInt(wanted)).abs <= toleranceUlps, s"lane $lane: bits $actual != $wanted")
    }
    stored.foreach(values =>
      assertEquals(
        memory.bytes.slice(stride, stride + lanes * storedWidth / 8),
        bytes(values.map(value => if (integerPointer) value.toLong else raw(width, value)), storedWidth)
      )
    )
  }

}
