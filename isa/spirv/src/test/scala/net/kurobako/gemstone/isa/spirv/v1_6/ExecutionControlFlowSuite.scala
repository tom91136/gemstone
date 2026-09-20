package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.Vec

import net.kurobako.gemstone.binary.ByteRange
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.binary.ScalarByteWidth
import net.kurobako.gemstone.execution.ExecutionFailure
import net.kurobako.gemstone.execution.ExecutionControl
import net.kurobako.gemstone.execution.ExecutionLimits
import net.kurobako.gemstone.execution.ExecutionMemory
import net.kurobako.gemstone.execution.AllocationId
import net.kurobako.gemstone.execution.Dimensions
import net.kurobako.gemstone.execution.KernelArgument
import net.kurobako.gemstone.execution.KernelDispatch
import net.kurobako.gemstone.execution.KernelName
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import net.kurobako.gemstone.execution.PointerRelocation
import net.kurobako.gemstone.execution.ProgramId

import java.util.concurrent.atomic.AtomicReference
final class ExecutionControlFlowSuite extends munit.FunSuite with ExecutionTestSupport {
  test("dispatch executes typed function calls and return values") {
    val voidType        = operand("IdResult", 1)
    val u32             = operand("IdResult", 2)
    val pointerType     = operand("IdResult", 3)
    val entryType       = operand("IdResult", 4)
    val helperType      = operand("IdResult", 5)
    val one             = operand("IdResult", 6)
    val entryFunction   = operand("IdResult", 7)
    val helperFunction  = operand("IdResult", 8)
    val output          = operand("IdResult", 9)
    val input           = operand("IdResult", 10)
    val callResult      = operand("IdResult", 12)
    val helperParameter = operand("IdResult", 14)
    val helperResult    = operand("IdResult", 16)
    val module          = Module(
      Header(ISA.version, Word(0), Word(24), Word(0)),
      Vec(
        Instruction.TypeVoid(voidType),
        Instruction.TypeInt(u32, operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
        Instruction.TypePointer(pointerType, operand("StorageClass", 5), u32),
        Instruction.TypeFunction(entryType, voidType, Vec(pointerType, u32)),
        Instruction.TypeFunction(helperType, u32, Vec(u32)),
        Instruction.Constant(u32, one, operand("LiteralContextDependentNumber", 1)),
        Instruction.EntryPoint(operand("ExecutionModel", 6), entryFunction, stringOperand("call"), Vec.empty),
        Instruction.Function(voidType, entryFunction, operand("FunctionControl", 0), entryType),
        Instruction.FunctionParameter(pointerType, output),
        Instruction.FunctionParameter(u32, input),
        Instruction.Label(operand("IdResult", 11)),
        Instruction.FunctionCall(u32, callResult, helperFunction, Vec(input)),
        Instruction.Store(output, callResult, None),
        Instruction.Return(),
        Instruction.FunctionEnd(),
        Instruction.Function(u32, helperFunction, operand("FunctionControl", 0), helperType),
        Instruction.FunctionParameter(u32, helperParameter),
        Instruction.Label(operand("IdResult", 15)),
        Instruction.IAdd(u32, helperResult, helperParameter, one),
        Instruction.ReturnValue(helperResult),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val allocation = AllocationId(21L)
    val memory     = ArrayMemory(allocation, 4)
    val dispatch   = KernelDispatch
      .validated(
        ProgramId(21L),
        KernelName("call"),
        Dimensions.One,
        Dimensions.One,
        Vec(
          KernelArgument.Allocation(allocation, 0L),
          KernelArgument.Scalar(LittleEndian.toVec(41L, ScalarByteWidth(4)))
        )
      )
      .toOption
      .get

    assertEquals(Interpreter.runUnchecked(module, dispatch, memory), Right(()))
    assertEquals(memory.bytes, LittleEndian.toVec(42L, ScalarByteWidth(4)))
  }

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
        Instruction.TypePointer(pointerType, operand("StorageClass", 5), f64),
        Instruction.TypeFunction(functionType, voidType, Vec(pointerType, f64, f64)),
        Instruction.EntryPoint(operand("ExecutionModel", 6), function, stringOperand("hypot"), Vec.empty),
        Instruction.Function(voidType, function, operand("FunctionControl", 0), functionType),
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
    val dispatch   = KernelDispatch
      .validated(
        ProgramId(22L),
        KernelName("hypot"),
        Dimensions.One,
        Dimensions.One,
        Vec(
          KernelArgument.Allocation(allocation, 0L),
          KernelArgument.Scalar(LittleEndian.toVec(java.lang.Double.doubleToRawLongBits(3.0), ScalarByteWidth(8))),
          KernelArgument.Scalar(LittleEndian.toVec(java.lang.Double.doubleToRawLongBits(4.0), ScalarByteWidth(8)))
        )
      )
      .toOption
      .get

    assertEquals(Interpreter.runUnchecked(module, dispatch, memory), Right(()))
    assertEquals(memory.bytes, LittleEndian.toVec(java.lang.Double.doubleToRawLongBits(5.0), ScalarByteWidth(8)))
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
        Instruction.TypePointer(pointerType, operand("StorageClass", 5), f32),
        Instruction.TypeFunction(functionType, voidType, Vec(pointerType, f32)),
        Instruction.EntryPoint(operand("ExecutionModel", 6), function, stringOperand("native_recip"), Vec.empty),
        Instruction.Function(voidType, function, operand("FunctionControl", 0), functionType),
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
    val dispatch   = KernelDispatch
      .validated(
        ProgramId(23L),
        KernelName("native_recip"),
        Dimensions.One,
        Dimensions.One,
        Vec(
          KernelArgument.Allocation(allocation, 0L),
          KernelArgument.Scalar(LittleEndian.toVec(java.lang.Float.floatToRawIntBits(4.0f).toLong, ScalarByteWidth(4)))
        )
      )
      .toOption
      .get

    assertEquals(Interpreter.runUnchecked(module, dispatch, memory), Right(()))
    assertEquals(memory.bytes, LittleEndian.toVec(java.lang.Float.floatToRawIntBits(0.25f).toLong, ScalarByteWidth(4)))
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
        Instruction.TypePointer(pointerType, operand("StorageClass", 5), f32),
        Instruction.TypeFunction(functionType, voidType, Vec(pointerType, f32)),
        Instruction.EntryPoint(operand("ExecutionModel", 6), function, stringOperand("native_sin"), Vec.empty),
        Instruction.Function(voidType, function, operand("FunctionControl", 0), functionType),
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
    val dispatch   = KernelDispatch
      .validated(
        ProgramId(24L),
        KernelName("native_sin"),
        Dimensions.One,
        Dimensions.One,
        Vec(
          KernelArgument.Allocation(allocation, 0L),
          KernelArgument.Scalar(LittleEndian.toVec(java.lang.Float.floatToRawIntBits(0.5f).toLong, ScalarByteWidth(4)))
        )
      )
      .toOption
      .get

    assertEquals(Interpreter.runUnchecked(module, dispatch, memory), Right(()))
    val actual = java.lang.Float.intBitsToFloat(LittleEndian.decode(memory.bytes, 0, ScalarByteWidth(4)).toInt)
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
        Instruction.TypePointer(pointerType, operand("StorageClass", 5), f32),
        Instruction.TypeFunction(functionType, voidType, Vec(pointerType, f32)),
        Instruction.EntryPoint(operand("ExecutionModel", 6), function, stringOperand("native_cos"), Vec.empty),
        Instruction.Function(voidType, function, operand("FunctionControl", 0), functionType),
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
    val dispatch   = KernelDispatch
      .validated(
        ProgramId(25L),
        KernelName("native_cos"),
        Dimensions.One,
        Dimensions.One,
        Vec(
          KernelArgument.Allocation(allocation, 0L),
          KernelArgument.Scalar(LittleEndian.toVec(java.lang.Float.floatToRawIntBits(0.5f).toLong, ScalarByteWidth(4)))
        )
      )
      .toOption
      .get

    assertEquals(Interpreter.runUnchecked(module, dispatch, memory), Right(()))
    val actual = java.lang.Float.intBitsToFloat(LittleEndian.decode(memory.bytes, 0, ScalarByteWidth(4)).toInt)
    assert(math.abs(actual - 0.87758255f) < 1.0e-6f)
  }

  test("dispatch executes structured loops and Phi values") {
    val voidType    = operand("IdResult", 1)
    val u32         = operand("IdResult", 2)
    val pointerType = operand("IdResult", 3)
    val function    = operand("IdResult", 5)
    val output      = operand("IdResult", 6)
    val bound       = operand("IdResult", 7)
    val entry       = operand("IdResult", 8)
    val header      = operand("IdResult", 9)
    val body        = operand("IdResult", 10)
    val merge       = operand("IdResult", 11)
    val index       = operand("IdResult", 12)
    val condition   = operand("IdResult", 13)
    val destination = operand("IdResult", 14)
    val next        = operand("IdResult", 15)
    val zero        = operand("IdResult", 16)
    val one         = operand("IdResult", 17)
    val module      = Module(
      Header(ISA.version, Word(0), Word(32), Word(0)),
      Vec(
        Instruction.TypeVoid(voidType),
        Instruction.TypeInt(u32, operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
        Instruction.TypeBool(operand("IdResult", 18)),
        Instruction.TypePointer(pointerType, operand("StorageClass", 5), u32),
        Instruction.TypeFunction(operand("IdResult", 4), voidType, Vec(pointerType, u32)),
        Instruction.Constant(u32, zero, operand("LiteralContextDependentNumber", 0)),
        Instruction.Constant(u32, one, operand("LiteralContextDependentNumber", 1)),
        Instruction.EntryPoint(operand("ExecutionModel", 6), function, stringOperand("sequence"), Vec.empty),
        Instruction.Function(voidType, function, operand("FunctionControl", 0), operand("IdRef", 4)),
        Instruction.FunctionParameter(pointerType, output),
        Instruction.FunctionParameter(u32, bound),
        Instruction.Label(entry),
        Instruction.Branch(header),
        Instruction.Label(header),
        Instruction.Phi(u32, index, Vec(phiPair(zero, entry), phiPair(next, body))),
        Instruction.LoopMerge(merge, body, operand("LoopControl", 0)),
        Instruction.ULessThan(operand("IdResultType", 18), condition, index, bound),
        Instruction.BranchConditional(condition, body, merge, Vec.empty),
        Instruction.Label(body),
        Instruction.PtrAccessChain(pointerType, destination, output, index, Vec.empty),
        Instruction.Store(destination, index, None),
        Instruction.IAdd(u32, next, index, one),
        Instruction.Branch(header),
        Instruction.Label(merge),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val allocation = AllocationId(2L)
    val memory     = ArrayMemory(allocation, 36)
    val dispatch   = KernelDispatch
      .validated(
        ProgramId(2L),
        KernelName("sequence"),
        Dimensions.One,
        Dimensions.One,
        Vec(
          KernelArgument.Allocation(allocation, 4L),
          KernelArgument.Scalar(LittleEndian.toVec(8L, ScalarByteWidth(4)))
        )
      )
      .toOption
      .get

    val decoded = ISA.decode(ISA.encode(module).toOption.get).toOption.get
    assertEquals(Interpreter.runUnchecked(decoded, dispatch, memory), Right(()))
    assertEquals(memory.bytes.take(4), Vec.fill(4)(0.toByte))
    assertEquals(memory.bytes.drop(4), Vec.from((0L until 8L).flatMap(LittleEndian.toVec(_, ScalarByteWidth(4)))))
  }

  test("dispatch validates lifetime markers around function objects") {
    val voidType     = operand("IdResult", 1)
    val u8           = operand("IdResult", 2)
    val u32          = operand("IdResult", 3)
    val outputType   = operand("IdResult", 4)
    val localType    = operand("IdResult", 5)
    val localBytePtr = operand("IdResult", 6)
    val function     = operand("IdResult", 8)
    val output       = operand("IdResult", 9)
    val local        = operand("IdResult", 11)
    val bytePointer  = operand("IdResult", 12)
    val loaded       = operand("IdResult", 13)
    val value        = operand("IdResult", 14)
    val instructions = Vec(
      Instruction.TypeVoid(voidType),
      Instruction.TypeInt(u8, operand("LiteralInteger", 8), operand("LiteralInteger", 0)),
      Instruction.TypeInt(u32, operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
      Instruction.TypePointer(outputType, operand("StorageClass", 5), u32),
      Instruction.TypePointer(localType, operand("StorageClass", 7), u32),
      Instruction.TypePointer(localBytePtr, operand("StorageClass", 7), u8),
      Instruction.TypeFunction(operand("IdResult", 7), voidType, Vec(outputType)),
      Instruction.Constant(u32, value, operand("LiteralContextDependentNumber", 0x12345678)),
      Instruction.EntryPoint(operand("ExecutionModel", 6), function, stringOperand("lifetime"), Vec.empty),
      Instruction.Function(voidType, function, operand("FunctionControl", 0), operand("IdRef", 7)),
      Instruction.FunctionParameter(outputType, output),
      Instruction.Label(operand("IdResult", 10)),
      Instruction.Variable(localType, local, operand("StorageClass", 7), None),
      Instruction.Bitcast(localBytePtr, bytePointer, local),
      Instruction.LifetimeStart(bytePointer, operand("LiteralInteger", 4)),
      Instruction.Store(local, value, None),
      Instruction.Load(u32, loaded, local, None),
      Instruction.Store(output, loaded, None),
      Instruction.LifetimeStop(bytePointer, operand("LiteralInteger", 4)),
      Instruction.Return(),
      Instruction.FunctionEnd()
    )
    val module     = Module(Header(ISA.version, Word(0), Word(20), Word(0)), instructions, Endianness.Little)
    val allocation = AllocationId(3L)
    val memory     = ArrayMemory(allocation, 4)
    val dispatch   = KernelDispatch
      .validated(
        ProgramId(3L),
        KernelName("lifetime"),
        Dimensions.One,
        Dimensions.One,
        Vec(KernelArgument.Allocation(allocation, 0L))
      )
      .toOption
      .get

    assertEquals(Interpreter.runUnchecked(module, dispatch, memory), Right(()))
    assertEquals(memory.bytes, LittleEndian.toVec(0x12345678L, ScalarByteWidth(4)))

    val malformed = module.copy(instructions =
      instructions.updated(
        14,
        Instruction.LifetimeStart(bytePointer, operand("LiteralInteger", 4, 0))
      )
    )
    Interpreter.runUnchecked(malformed, dispatch, memory) match {
      case Left(ExecutionFailure.InvalidRequest(message)) =>
        assertEquals(message, "OpLifetimeStart size must contain one word, found 2")
      case result => fail(s"expected typed malformed lifetime marker failure, found $result")
    }
  }

}
