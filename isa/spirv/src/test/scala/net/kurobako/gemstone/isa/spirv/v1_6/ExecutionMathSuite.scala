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
    val voidType     = operand("IdResult", 1)
    val f64          = operand("IdResult", 2)
    val pointerType  = operand("IdResult", 3)
    val functionType = operand("IdResult", 4)
    val opencl       = operand("IdResult", 5)
    val function     = operand("IdResult", 6)
    val output       = operand("IdResult", 7)
    val left         = operand("IdResult", 8)
    val right        = operand("IdResult", 9)
    val result       = operand("IdResult", 11)
    val module       = Module(
      Header(ISA.version, Word(0), Word(20), Word(0)),
      Vec(
        Instruction.ExtInstImport(opencl, stringOperand("OpenCL.std")),
        Instruction.TypeVoid(voidType),
        Instruction.TypeFloat(f64, operand("LiteralInteger", 64), None),
        Instruction.TypePointer(pointerType, operand("StorageClass", spv.StorageClass.CrossWorkgroup), f64),
        Instruction.TypeFunction(functionType, voidType, Vec(pointerType, f64, f64)),
        Instruction.EntryPoint(
          operand("ExecutionModel", spv.ExecutionModel.Kernel),
          function,
          stringOperand("hypot"),
          Vec.empty
        ),
        Instruction.Function(voidType, function, operand("FunctionControl", spv.FunctionControl.None), functionType),
        Instruction.FunctionParameter(pointerType, output),
        Instruction.FunctionParameter(f64, left),
        Instruction.FunctionParameter(f64, right),
        Instruction.Label(operand("IdResult", 10)),
        Instruction.ExtInst(f64, result, opencl, operand("LiteralExtInstInteger", 32), Vec(left, right)),
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
    val voidType     = operand("IdResult", 1)
    val f32          = operand("IdResult", 2)
    val pointerType  = operand("IdResult", 3)
    val functionType = operand("IdResult", 4)
    val opencl       = operand("IdResult", 5)
    val function     = operand("IdResult", 6)
    val output       = operand("IdResult", 7)
    val left         = operand("IdResult", 8)
    val right        = operand("IdResult", 9)
    val addend       = operand("IdResult", 10)
    val result       = operand("IdResult", 12)
    val module       = Module(
      Header(ISA.version, Word(0), Word(16), Word(0)),
      Vec(
        Instruction.ExtInstImport(opencl, stringOperand("OpenCL.std")),
        Instruction.TypeVoid(voidType),
        Instruction.TypeFloat(f32, operand("LiteralInteger", 32), None),
        Instruction.TypePointer(pointerType, operand("StorageClass", spv.StorageClass.CrossWorkgroup), f32),
        Instruction.TypeFunction(functionType, voidType, Vec(pointerType, f32, f32, f32)),
        Instruction.EntryPoint(
          operand("ExecutionModel", spv.ExecutionModel.Kernel),
          function,
          stringOperand("fma"),
          Vec.empty
        ),
        Instruction.Function(voidType, function, operand("FunctionControl", spv.FunctionControl.None), functionType),
        Instruction.FunctionParameter(pointerType, output),
        Instruction.FunctionParameter(f32, left),
        Instruction.FunctionParameter(f32, right),
        Instruction.FunctionParameter(f32, addend),
        Instruction.Label(operand("IdResult", 11)),
        Instruction.ExtInst(f32, result, opencl, operand("LiteralExtInstInteger", 26), Vec(left, right, addend)),
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
    val voidType     = operand("IdResult", 1)
    val f32          = operand("IdResult", 2)
    val pointerType  = operand("IdResult", 3)
    val functionType = operand("IdResult", 4)
    val opencl       = operand("IdResult", 5)
    val function     = operand("IdResult", 6)
    val output       = operand("IdResult", 7)
    val input        = operand("IdResult", 8)
    val result       = operand("IdResult", 10)
    val module       = Module(
      Header(ISA.version, Word(0), Word(11), Word(0)),
      Vec(
        Instruction.ExtInstImport(opencl, stringOperand("OpenCL.std")),
        Instruction.TypeVoid(voidType),
        Instruction.TypeFloat(f32, operand("LiteralInteger", 32), None),
        Instruction.TypePointer(pointerType, operand("StorageClass", spv.StorageClass.CrossWorkgroup), f32),
        Instruction.TypeFunction(functionType, voidType, Vec(pointerType, f32)),
        Instruction.EntryPoint(
          operand("ExecutionModel", spv.ExecutionModel.Kernel),
          function,
          stringOperand("native_recip"),
          Vec.empty
        ),
        Instruction.Function(voidType, function, operand("FunctionControl", spv.FunctionControl.None), functionType),
        Instruction.FunctionParameter(pointerType, output),
        Instruction.FunctionParameter(f32, input),
        Instruction.Label(operand("IdResult", 9)),
        Instruction.ExtInst(f32, result, opencl, operand("LiteralExtInstInteger", 90), Vec(input)),
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
    val voidType     = operand("IdResult", 1)
    val f32          = operand("IdResult", 2)
    val pointerType  = operand("IdResult", 3)
    val functionType = operand("IdResult", 4)
    val opencl       = operand("IdResult", 5)
    val function     = operand("IdResult", 6)
    val output       = operand("IdResult", 7)
    val input        = operand("IdResult", 8)
    val result       = operand("IdResult", 10)
    val module       = Module(
      Header(ISA.version, Word(0), Word(11), Word(0)),
      Vec(
        Instruction.ExtInstImport(opencl, stringOperand("OpenCL.std")),
        Instruction.TypeVoid(voidType),
        Instruction.TypeFloat(f32, operand("LiteralInteger", 32), None),
        Instruction.TypePointer(pointerType, operand("StorageClass", spv.StorageClass.CrossWorkgroup), f32),
        Instruction.TypeFunction(functionType, voidType, Vec(pointerType, f32)),
        Instruction.EntryPoint(
          operand("ExecutionModel", spv.ExecutionModel.Kernel),
          function,
          stringOperand("native_sin"),
          Vec.empty
        ),
        Instruction.Function(voidType, function, operand("FunctionControl", spv.FunctionControl.None), functionType),
        Instruction.FunctionParameter(pointerType, output),
        Instruction.FunctionParameter(f32, input),
        Instruction.Label(operand("IdResult", 9)),
        Instruction.ExtInst(f32, result, opencl, operand("LiteralExtInstInteger", 92), Vec(input)),
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
    val voidType     = operand("IdResult", 1)
    val f32          = operand("IdResult", 2)
    val pointerType  = operand("IdResult", 3)
    val functionType = operand("IdResult", 4)
    val opencl       = operand("IdResult", 5)
    val function     = operand("IdResult", 6)
    val output       = operand("IdResult", 7)
    val input        = operand("IdResult", 8)
    val result       = operand("IdResult", 10)
    val module       = Module(
      Header(ISA.version, Word(0), Word(11), Word(0)),
      Vec(
        Instruction.ExtInstImport(opencl, stringOperand("OpenCL.std")),
        Instruction.TypeVoid(voidType),
        Instruction.TypeFloat(f32, operand("LiteralInteger", 32), None),
        Instruction.TypePointer(pointerType, operand("StorageClass", spv.StorageClass.CrossWorkgroup), f32),
        Instruction.TypeFunction(functionType, voidType, Vec(pointerType, f32)),
        Instruction.EntryPoint(
          operand("ExecutionModel", spv.ExecutionModel.Kernel),
          function,
          stringOperand("native_cos"),
          Vec.empty
        ),
        Instruction.Function(voidType, function, operand("FunctionControl", spv.FunctionControl.None), functionType),
        Instruction.FunctionParameter(pointerType, output),
        Instruction.FunctionParameter(f32, input),
        Instruction.Label(operand("IdResult", 9)),
        Instruction.ExtInst(f32, result, opencl, operand("LiteralExtInstInteger", 81), Vec(input)),
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
    val voidType   = operand("IdResult", 1)
    val floatType  = operand("IdResult", 2)
    val vectorType = operand("IdResult", 3)
    val pointer    = operand("IdResult", 4)
    val signature  = operand("IdResult", 5)
    val function   = operand("IdResult", 6)
    val output     = operand("IdResult", 7)
    val vector     = operand("IdResult", 8)
    val scalar     = operand("IdResult", 9)
    val product    = operand("IdResult", 11)
    val module     = Module(
      Header(ISA.version, Word(0), Word(12), Word(0)),
      Vec(
        Instruction.TypeVoid(voidType),
        Instruction.TypeFloat(floatType, operand("LiteralInteger", 32), None),
        Instruction.TypeVector(vectorType, floatType, operand("LiteralInteger", 2)),
        Instruction.TypePointer(pointer, operand("StorageClass", spv.StorageClass.CrossWorkgroup), vectorType),
        Instruction.TypeFunction(signature, voidType, Vec(pointer, vectorType, floatType)),
        Instruction.EntryPoint(
          operand("ExecutionModel", spv.ExecutionModel.Kernel),
          function,
          stringOperand("vector_scale"),
          Vec.empty
        ),
        Instruction.Function(voidType, function, operand("FunctionControl", spv.FunctionControl.None), signature),
        Instruction.FunctionParameter(pointer, output),
        Instruction.FunctionParameter(vectorType, vector),
        Instruction.FunctionParameter(floatType, scalar),
        Instruction.Label(operand("IdResult", 10)),
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
}
