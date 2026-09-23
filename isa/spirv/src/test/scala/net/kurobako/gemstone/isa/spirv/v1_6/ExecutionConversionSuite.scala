package net.kurobako.gemstone.isa.spirv.v1_6
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.{Coordinates, Dimensions, GlobalDimensions}

import net.kurobako.gemstone.Vec

import net.kurobako.gemstone.binary.ByteRange
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.binary.ScalarByteWidth
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import java.util.concurrent.atomic.AtomicReference
final class ExecutionConversionSuite extends munit.FunSuite with ExecutionTestSupport {
  test("dispatch converts signed integer bit patterns to floating-point vectors") {
    val voidType     = operand("IdResult", 1)
    val u32          = operand("IdResult", 2)
    val inputType    = operand("IdResult", 3)
    val f32          = operand("IdResult", 4)
    val resultType   = operand("IdResult", 5)
    val outputType   = operand("IdResult", 6)
    val function     = operand("IdResult", 8)
    val output       = operand("IdResult", 9)
    val input        = operand("IdResult", 10)
    val rhs          = operand("IdResult", 11)
    val converted    = operand("IdResult", 13)
    val added        = operand("IdResult", 14)
    val subtracted   = operand("IdResult", 15)
    val multiplied   = operand("IdResult", 16)
    val integerValue = operand("IdResult", 17)
    val result       = operand("IdResult", 18)
    val divided      = operand("IdResult", 19)
    val instructions = Vec(
      Instruction.TypeVoid(voidType),
      Instruction.TypeInt(u32, operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
      Instruction.TypeVector(inputType, u32, operand("LiteralInteger", 2)),
      Instruction.TypeFloat(f32, operand("LiteralInteger", 32), None),
      Instruction.TypeVector(resultType, f32, operand("LiteralInteger", 2)),
      Instruction.TypePointer(outputType, operand("StorageClass", 5), resultType),
      Instruction.TypeFunction(operand("IdResult", 7), voidType, Vec(outputType, inputType, resultType)),
      Instruction.EntryPoint(operand("ExecutionModel", 6), function, stringOperand("signed_to_float"), Vec.empty),
      Instruction.Function(voidType, function, operand("FunctionControl", 0), operand("IdRef", 7)),
      Instruction.FunctionParameter(outputType, output),
      Instruction.FunctionParameter(inputType, input),
      Instruction.FunctionParameter(resultType, rhs),
      Instruction.Label(operand("IdResult", 12)),
      Instruction.ConvertSToF(resultType, converted, input),
      Instruction.FAdd(resultType, added, converted, rhs),
      Instruction.FSub(resultType, subtracted, added, rhs),
      Instruction.FMul(resultType, multiplied, subtracted, rhs),
      Instruction.FDiv(resultType, divided, multiplied, rhs),
      Instruction.ConvertFToS(inputType, integerValue, divided),
      Instruction.ConvertSToF(resultType, result, integerValue),
      Instruction.Store(output, result, None),
      Instruction.Return(),
      Instruction.FunctionEnd()
    )
    val module     = Module(Header(ISA.version, Word(0), Word(20), Word(0)), instructions, Endianness.Little)
    val allocation = MemoryRegion(4L)
    val memory     = ArrayMemory(allocation, 8)
    val dispatch   = KernelTask
      .validated(
        KernelName("signed_to_float"),
        Dimensions.One,
        Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(allocation, 0L),
          KernelTask.Argument.Scalar(LittleEndian.toVec(0xfffffffeL, ScalarByteWidth(4)) ++
            LittleEndian.toVec(3L, ScalarByteWidth(4))),
          KernelTask.Argument.Scalar(
            LittleEndian.toVec(java.lang.Float.floatToRawIntBits(0.5f).toLong, ScalarByteWidth(4)) ++
              LittleEndian.toVec(java.lang.Float.floatToRawIntBits(1.5f).toLong, ScalarByteWidth(4))
          )
        )
      )
      .toOption
      .get

    assertEquals(executeUnchecked(module, dispatch, memory), Right(()))
    assertEquals(
      memory.bytes,
      LittleEndian.toVec(java.lang.Float.floatToRawIntBits(-2.0f).toLong, ScalarByteWidth(4)) ++
        LittleEndian.toVec(java.lang.Float.floatToRawIntBits(3.0f).toLong, ScalarByteWidth(4))
    )

    val malformed = module.copy(instructions =
      instructions.updated(
        13,
        Instruction.ConvertSToF(u32, converted, input)
      )
    )
    executeUnchecked(malformed, dispatch, memory) match {
      case Left(Fault.InvalidTask(message)) =>
        assertEquals(message, "OpConvertSToF result type %2 is not a floating-point scalar or vector")
      case result => fail(s"expected typed conversion result failure, found $result")
    }

    val malformedSubtract = module.copy(instructions =
      instructions.updated(
        15,
        Instruction.FSub(u32, subtracted, added, rhs)
      )
    )
    executeUnchecked(malformedSubtract, dispatch, memory) match {
      case Left(Fault.InvalidTask(message)) =>
        assertEquals(message, "OpFSub result type %2 is not a floating-point scalar or vector")
      case value => fail(s"expected typed floating subtraction failure, found $value")
    }

    val malformedAdd = module.copy(instructions =
      instructions.updated(
        14,
        Instruction.FAdd(u32, added, converted, rhs)
      )
    )
    executeUnchecked(malformedAdd, dispatch, memory) match {
      case Left(Fault.InvalidTask(message)) =>
        assertEquals(message, "OpFAdd result type %2 is not a floating-point scalar or vector")
      case value => fail(s"expected typed floating addition failure, found $value")
    }

    val malformedMultiply = module.copy(instructions =
      instructions.updated(
        16,
        Instruction.FMul(u32, multiplied, subtracted, rhs)
      )
    )
    executeUnchecked(malformedMultiply, dispatch, memory) match {
      case Left(Fault.InvalidTask(message)) =>
        assertEquals(message, "OpFMul result type %2 is not a floating-point scalar or vector")
      case value => fail(s"expected typed floating multiplication failure, found $value")
    }

    val malformedFloatToSigned = module.copy(instructions =
      instructions.updated(
        18,
        Instruction.ConvertFToS(resultType, integerValue, divided)
      )
    )
    executeUnchecked(malformedFloatToSigned, dispatch, memory) match {
      case Left(Fault.InvalidTask(message)) =>
        assertEquals(message, "OpConvertFToS result type %5 is not an integer scalar or vector")
      case value => fail(s"expected typed floating-to-signed conversion failure, found $value")
    }

    val malformedDivide = module.copy(instructions =
      instructions.updated(
        17,
        Instruction.FDiv(u32, divided, multiplied, rhs)
      )
    )
    executeUnchecked(malformedDivide, dispatch, memory) match {
      case Left(Fault.InvalidTask(message)) =>
        assertEquals(message, "OpFDiv result type %2 is not a floating-point scalar or vector")
      case value => fail(s"expected typed floating division failure, found $value")
    }

    val halfModule = module.copy(instructions =
      instructions
        .updated(1, Instruction.TypeInt(u32, operand("LiteralInteger", 16), operand("LiteralInteger", 0)))
        .updated(3, Instruction.TypeFloat(f32, operand("LiteralInteger", 16), None))
    )
    val halfAllocation = MemoryRegion(5L)
    val halfMemory     = ArrayMemory(halfAllocation, 4)
    val halfDispatch   = KernelTask
      .validated(
        KernelName("signed_to_float"),
        Dimensions.One,
        Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(halfAllocation, 0L),
          KernelTask.Argument.Scalar(LittleEndian.toVec(0L, ScalarByteWidth(2)) ++
            LittleEndian.toVec(3L, ScalarByteWidth(2))),
          KernelTask.Argument.Scalar(LittleEndian.toVec(0x0001L, ScalarByteWidth(2)) ++
            LittleEndian.toVec(0x3e00L, ScalarByteWidth(2)))
        )
      )
      .toOption
      .get
    assertEquals(executeUnchecked(halfModule, halfDispatch, halfMemory), Right(()))
    assertEquals(
      halfMemory.bytes,
      LittleEndian.toVec(0L, ScalarByteWidth(2)) ++ LittleEndian.toVec(0x4200L, ScalarByteWidth(2))
    )
  }

  test("dispatch compares integer values with a typed boolean result") {
    val voidType     = operand("IdResult", 1)
    val u32          = operand("IdResult", 2)
    val bool         = operand("IdResult", 3)
    val pointerType  = operand("IdResult", 4)
    val function     = operand("IdResult", 6)
    val output       = operand("IdResult", 7)
    val left         = operand("IdResult", 8)
    val right        = operand("IdResult", 9)
    val remainder    = operand("IdResult", 11)
    val equal        = operand("IdResult", 12)
    val selected     = operand("IdResult", 13)
    val instructions = Vec(
      Instruction.TypeVoid(voidType),
      Instruction.TypeInt(u32, operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
      Instruction.TypeBool(bool),
      Instruction.TypePointer(pointerType, operand("StorageClass", 5), u32),
      Instruction.TypeFunction(operand("IdResult", 5), voidType, Vec(pointerType, u32, u32)),
      Instruction.EntryPoint(operand("ExecutionModel", 6), function, stringOperand("integer_equal"), Vec.empty),
      Instruction.Function(voidType, function, operand("FunctionControl", 0), operand("IdRef", 5)),
      Instruction.FunctionParameter(pointerType, output),
      Instruction.FunctionParameter(u32, left),
      Instruction.FunctionParameter(u32, right),
      Instruction.Label(operand("IdResult", 10)),
      Instruction.SRem(u32, remainder, left, right),
      Instruction.IEqual(bool, equal, remainder, right),
      Instruction.Select(u32, selected, equal, left, remainder),
      Instruction.Store(output, selected, None),
      Instruction.Return(),
      Instruction.FunctionEnd()
    )
    val module     = Module(Header(ISA.version, Word(0), Word(16), Word(0)), instructions, Endianness.Little)
    val allocation = MemoryRegion(6L)
    val memory     = ArrayMemory(allocation, 4)
    val dispatch   = KernelTask
      .validated(
        KernelName("integer_equal"),
        Dimensions.One,
        Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(allocation, 0L),
          KernelTask.Argument.Scalar(LittleEndian.toVec(0xfffffff9L, ScalarByteWidth(4))),
          KernelTask.Argument.Scalar(LittleEndian.toVec(3L, ScalarByteWidth(4)))
        )
      )
      .toOption
      .get

    assertEquals(executeUnchecked(module, dispatch, memory), Right(()))
    assertEquals(memory.bytes, LittleEndian.toVec(0xffffffffL, ScalarByteWidth(4)))

    val divided = module.copy(instructions = instructions.updated(11, Instruction.SDiv(u32, remainder, left, right)))
    assertEquals(executeUnchecked(divided, dispatch, memory), Right(()))
    assertEquals(memory.bytes, LittleEndian.toVec(0xfffffffeL, ScalarByteWidth(4)))

    val xor = module.copy(instructions = instructions.updated(11, Instruction.BitwiseXor(u32, remainder, left, right)))
    assertEquals(executeUnchecked(xor, dispatch, memory), Right(()))
    assertEquals(memory.bytes, LittleEndian.toVec(0xfffffffaL, ScalarByteWidth(4)))

    val shiftedRight = module.copy(instructions =
      instructions.updated(11, Instruction.ShiftRightLogical(u32, remainder, left, right))
    )
    assertEquals(executeUnchecked(shiftedRight, dispatch, memory), Right(()))
    assertEquals(memory.bytes, LittleEndian.toVec(0x1fffffffL, ScalarByteWidth(4)))

    val shiftedLeft = module.copy(instructions =
      instructions.updated(11, Instruction.ShiftLeftLogical(u32, remainder, left, right))
    )
    assertEquals(executeUnchecked(shiftedLeft, dispatch, memory), Right(()))
    assertEquals(memory.bytes, LittleEndian.toVec(0xffffffc8L, ScalarByteWidth(4)))

    val not = module.copy(instructions = instructions.updated(11, Instruction.Not(u32, remainder, left)))
    assertEquals(executeUnchecked(not, dispatch, memory), Right(()))
    assertEquals(memory.bytes, LittleEndian.toVec(6L, ScalarByteWidth(4)))

    val negated = module.copy(instructions = instructions.updated(11, Instruction.SNegate(u32, remainder, left)))
    assertEquals(executeUnchecked(negated, dispatch, memory), Right(()))
    assertEquals(memory.bytes, LittleEndian.toVec(7L, ScalarByteWidth(4)))

    val signedLess =
      module.copy(instructions = instructions.updated(12, Instruction.SLessThan(bool, equal, left, right)))
    assertEquals(executeUnchecked(signedLess, dispatch, memory), Right(()))
    assertEquals(memory.bytes, LittleEndian.toVec(0xfffffff9L, ScalarByteWidth(4)))

    val signedGreater =
      module.copy(instructions = instructions.updated(12, Instruction.SGreaterThan(bool, equal, left, right)))
    assertEquals(executeUnchecked(signedGreater, dispatch, memory), Right(()))
    assertEquals(memory.bytes, LittleEndian.toVec(0xffffffffL, ScalarByteWidth(4)))

    val malformed = module.copy(instructions =
      instructions.updated(
        12,
        Instruction.IEqual(u32, equal, remainder, right)
      )
    )
    executeUnchecked(malformed, dispatch, memory) match {
      case Left(Fault.InvalidTask(message)) =>
        assertEquals(message, "OpIEqual result type %2 is not a boolean scalar or vector")
      case value => fail(s"expected typed integer equality failure, found $value")
    }

    val malformedRemainder = module.copy(instructions =
      instructions.updated(
        11,
        Instruction.SRem(bool, remainder, left, right)
      )
    )
    executeUnchecked(malformedRemainder, dispatch, memory) match {
      case Left(Fault.InvalidTask(message)) =>
        assertEquals(message, "OpSRem result type %3 is not an integer scalar or vector")
      case value => fail(s"expected typed signed remainder failure, found $value")
    }

    val malformedDivision = module.copy(instructions =
      instructions.updated(
        11,
        Instruction.SDiv(bool, remainder, left, right)
      )
    )
    executeUnchecked(malformedDivision, dispatch, memory) match {
      case Left(Fault.InvalidTask(message)) =>
        assertEquals(message, "OpSDiv result type %3 is not an integer scalar or vector")
      case value => fail(s"expected typed signed division failure, found $value")
    }
  }

  test("dispatch performs ordered floating equality") {
    val voidType     = operand("IdResult", 1)
    val f32          = operand("IdResult", 2)
    val bool         = operand("IdResult", 3)
    val pointerType  = operand("IdResult", 4)
    val function     = operand("IdResult", 6)
    val output       = operand("IdResult", 7)
    val left         = operand("IdResult", 8)
    val right        = operand("IdResult", 9)
    val equal        = operand("IdResult", 11)
    val lessEqual    = operand("IdResult", 12)
    val selected     = operand("IdResult", 13)
    val result       = operand("IdResult", 14)
    val instructions = Vec(
      Instruction.TypeVoid(voidType),
      Instruction.TypeFloat(f32, operand("LiteralInteger", 32), None),
      Instruction.TypeBool(bool),
      Instruction.TypePointer(pointerType, operand("StorageClass", 5), f32),
      Instruction.TypeFunction(operand("IdResult", 5), voidType, Vec(pointerType, f32, f32)),
      Instruction.EntryPoint(operand("ExecutionModel", 6), function, stringOperand("floating_equal"), Vec.empty),
      Instruction.Function(voidType, function, operand("FunctionControl", 0), operand("IdRef", 5)),
      Instruction.FunctionParameter(pointerType, output),
      Instruction.FunctionParameter(f32, left),
      Instruction.FunctionParameter(f32, right),
      Instruction.Label(operand("IdResult", 10)),
      Instruction.FOrdEqual(bool, equal, left, right),
      Instruction.FOrdLessThanEqual(bool, lessEqual, left, right),
      Instruction.Select(f32, selected, equal, left, right),
      Instruction.Select(f32, result, lessEqual, left, selected),
      Instruction.Store(output, result, None),
      Instruction.Return(),
      Instruction.FunctionEnd()
    )
    val module = Module(Header(ISA.version, Word(0), Word(16), Word(0)), instructions, Endianness.Little)

    def run(id: Long :| Positive, leftBits: Long, rightBits: Long): Vec[Byte] = {
      val allocation = MemoryRegion(id)
      val memory     = ArrayMemory(allocation, 4)
      val dispatch   = KernelTask
        .validated(
          KernelName("floating_equal"),
          Dimensions.One,
          Dimensions.One,
          Vec(
            KernelTask.Argument.Memory(allocation, 0L),
            KernelTask.Argument.Scalar(LittleEndian.toVec(leftBits, ScalarByteWidth(4))),
            KernelTask.Argument.Scalar(LittleEndian.toVec(rightBits, ScalarByteWidth(4)))
          )
        )
        .toOption
        .get
      assertEquals(executeUnchecked(module, dispatch, memory), Right(()))
      memory.bytes
    }

    assertEquals(run(7L, 0x80000000L, 0L), LittleEndian.toVec(0x80000000L, ScalarByteWidth(4)))
    assertEquals(run(8L, 0x7fc00000L, 0x40400000L), LittleEndian.toVec(0x40400000L, ScalarByteWidth(4)))
    assertEquals(run(9L, 0x40000000L, 0x40400000L), LittleEndian.toVec(0x40000000L, ScalarByteWidth(4)))

    val malformed = module.copy(instructions =
      instructions.updated(
        11,
        Instruction.FOrdEqual(f32, equal, left, right)
      )
    )
    val allocation = MemoryRegion(10L)
    val dispatch   = KernelTask
      .validated(
        KernelName("floating_equal"),
        Dimensions.One,
        Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(allocation, 0L),
          KernelTask.Argument.Scalar(LittleEndian.toVec(0L, ScalarByteWidth(4))),
          KernelTask.Argument.Scalar(LittleEndian.toVec(0L, ScalarByteWidth(4)))
        )
      )
      .toOption
      .get
    executeUnchecked(malformed, dispatch, ArrayMemory(allocation, 4)) match {
      case Left(Fault.InvalidTask(message)) =>
        assertEquals(message, "OpFOrdEqual result type %2 is not a boolean scalar or vector")
      case value => fail(s"expected typed ordered equality failure, found $value")
    }

    val malformedLessEqual = module.copy(instructions =
      instructions.updated(
        12,
        Instruction.FOrdLessThanEqual(f32, lessEqual, left, right)
      )
    )
    executeUnchecked(malformedLessEqual, dispatch, ArrayMemory(allocation, 4)) match {
      case Left(Fault.InvalidTask(message)) =>
        assertEquals(message, "OpFOrdLessThanEqual result type %2 is not a boolean scalar or vector")
      case value => fail(s"expected typed ordered less-than-or-equal failure, found $value")
    }
  }

  test("dispatch converts floating-point values to unsigned integers without signed narrowing") {
    val voidType     = operand("IdResult", 1)
    val u64          = operand("IdResult", 2)
    val f64          = operand("IdResult", 3)
    val pointerType  = operand("IdResult", 4)
    val functionType = operand("IdResult", 5)
    val function     = operand("IdResult", 6)
    val output       = operand("IdResult", 7)
    val input        = operand("IdResult", 8)
    val result       = operand("IdResult", 10)
    val instructions = Vec(
      Instruction.TypeVoid(voidType),
      Instruction.TypeInt(u64, operand("LiteralInteger", 64), operand("LiteralInteger", 0)),
      Instruction.TypeFloat(f64, operand("LiteralInteger", 64), None),
      Instruction.TypePointer(pointerType, operand("StorageClass", 5), u64),
      Instruction.TypeFunction(functionType, voidType, Vec(pointerType, f64)),
      Instruction.EntryPoint(operand("ExecutionModel", 6), function, stringOperand("float_to_unsigned"), Vec.empty),
      Instruction.Function(voidType, function, operand("FunctionControl", 0), functionType),
      Instruction.FunctionParameter(pointerType, output),
      Instruction.FunctionParameter(f64, input),
      Instruction.Label(operand("IdResult", 9)),
      Instruction.ConvertFToU(u64, result, input),
      Instruction.Store(output, result, None),
      Instruction.Return(),
      Instruction.FunctionEnd()
    )
    val module = Module(Header(ISA.version, Word(0), Word(11), Word(0)), instructions, Endianness.Little)

    def run(id: Long :| Positive, value: Double): Either[Fault, Vec[Byte]] = {
      val allocation = MemoryRegion(id)
      val memory     = ArrayMemory(allocation, 8)
      val dispatch   = KernelTask
        .validated(
          KernelName("float_to_unsigned"),
          Dimensions.One,
          Dimensions.One,
          Vec(
            KernelTask.Argument.Memory(allocation, 0L),
            KernelTask.Argument.Scalar(LittleEndian.toVec(
              java.lang.Double.doubleToRawLongBits(value),
              ScalarByteWidth(8)
            ))
          )
        )
        .toOption
        .get
      executeUnchecked(module, dispatch, memory).map(_ => memory.bytes)
    }

    assertEquals(run(13L, 255.75), Right(LittleEndian.toVec(255L, ScalarByteWidth(8))))
    assertEquals(run(14L, java.lang.Math.scalb(1.0, 63)), Right(LittleEndian.toVec(Long.MinValue, ScalarByteWidth(8))))
    run(15L, -1.0) match {
      case Left(Fault.InvalidTask(message)) =>
        assert(message.contains("negative values are outside the unsigned range"))
      case value => fail(s"expected unsigned conversion range failure, found $value")
    }

    val malformed  = module.copy(instructions = instructions.updated(10, Instruction.ConvertFToU(f64, result, input)))
    val allocation = MemoryRegion(16L)
    val dispatch   = KernelTask
      .validated(
        KernelName("float_to_unsigned"),
        Dimensions.One,
        Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(allocation, 0L),
          KernelTask.Argument.Scalar(LittleEndian.toVec(java.lang.Double.doubleToRawLongBits(1.0), ScalarByteWidth(8)))
        )
      )
      .toOption
      .get
    executeUnchecked(malformed, dispatch, ArrayMemory(allocation, 8)) match {
      case Left(Fault.InvalidTask(message)) =>
        assertEquals(message, "OpConvertFToU result type %3 is not an integer scalar or vector")
      case value => fail(s"expected typed unsigned conversion failure, found $value")
    }
  }

  test("saturated float-to-signed conversion maps NaN to zero") {
    val voidType   = operand("IdResult", 1)
    val floatType  = operand("IdResult", 2)
    val intType    = operand("IdResult", 3)
    val pointer    = operand("IdResult", 4)
    val signature  = operand("IdResult", 5)
    val function   = operand("IdResult", 6)
    val output     = operand("IdResult", 7)
    val input      = operand("IdResult", 8)
    val converted  = operand("IdResult", 10)
    val module = Module(
      Header(ISA.version, Word(0), Word(11), Word(0)),
      Vec(
        Instruction.TypeVoid(voidType),
        Instruction.TypeFloat(floatType, operand("LiteralInteger", 32), None),
        Instruction.TypeInt(intType, operand("LiteralInteger", 32), operand("LiteralInteger", 1)),
        Instruction.TypePointer(pointer, operand("StorageClass", 5), intType),
        Instruction.TypeFunction(signature, voidType, Vec(pointer, floatType)),
        Instruction.Decorate(converted, operand("Decoration", 28)),
        Instruction.EntryPoint(operand("ExecutionModel", 6), function, stringOperand("saturated_nan"), Vec.empty),
        Instruction.Function(voidType, function, operand("FunctionControl", 0), signature),
        Instruction.FunctionParameter(pointer, output),
        Instruction.FunctionParameter(floatType, input),
        Instruction.Label(operand("IdResult", 9)),
        Instruction.ConvertFToS(intType, converted, input),
        Instruction.Store(output, converted, None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val allocation = MemoryRegion(17L)
    val memory     = ArrayMemory(allocation, 4)
    val dispatch = KernelTask
      .validated(
        KernelName("saturated_nan"),
        Dimensions.One,
        Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(allocation, 0L),
          KernelTask.Argument.Scalar(
            LittleEndian.toVec(java.lang.Float.floatToRawIntBits(Float.NaN).toLong, ScalarByteWidth(4))
          )
        )
      )
      .toOption
      .get

    assertEquals(executeUnchecked(module, dispatch, memory), Right(()))
    assertEquals(memory.bytes, LittleEndian.toVec(0L, ScalarByteWidth(4)))
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
    val module = Module(
      Header(ISA.version, Word(0), Word(12), Word(0)),
      Vec(
        Instruction.TypeVoid(voidType),
        Instruction.TypeFloat(floatType, operand("LiteralInteger", 32), None),
        Instruction.TypeVector(vectorType, floatType, operand("LiteralInteger", 2)),
        Instruction.TypePointer(pointer, operand("StorageClass", 5), vectorType),
        Instruction.TypeFunction(signature, voidType, Vec(pointer, vectorType, floatType)),
        Instruction.EntryPoint(operand("ExecutionModel", 6), function, stringOperand("vector_scale"), Vec.empty),
        Instruction.Function(voidType, function, operand("FunctionControl", 0), signature),
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
    val allocation = MemoryRegion(18L)
    val memory     = ArrayMemory(allocation, 8)
    def bits(value: Float): Vec[Byte] =
      LittleEndian.toVec(java.lang.Float.floatToRawIntBits(value).toLong, ScalarByteWidth(4))
    val dispatch = KernelTask
      .validated(
        KernelName("vector_scale"),
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
