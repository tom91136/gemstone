package net.kurobako.gemstone.isa.spirv.v1_6

import execution.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.spirv.v1_6 as spv
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
class ExecutionConversionSuite extends munit.FunSuite {
  test("dispatch converts signed integer bit patterns to floating-point vectors") {
    val voidType     = operand(OperandKind.IdResult, 1)
    val u32          = operand(OperandKind.IdResult, 2)
    val inputType    = operand(OperandKind.IdResult, 3)
    val f32          = operand(OperandKind.IdResult, 4)
    val resultType   = operand(OperandKind.IdResult, 5)
    val outputType   = operand(OperandKind.IdResult, 6)
    val function     = operand(OperandKind.IdResult, 8)
    val output       = operand(OperandKind.IdResult, 9)
    val input        = operand(OperandKind.IdResult, 10)
    val rhs          = operand(OperandKind.IdResult, 11)
    val converted    = operand(OperandKind.IdResult, 13)
    val added        = operand(OperandKind.IdResult, 14)
    val subtracted   = operand(OperandKind.IdResult, 15)
    val multiplied   = operand(OperandKind.IdResult, 16)
    val integerValue = operand(OperandKind.IdResult, 17)
    val result       = operand(OperandKind.IdResult, 18)
    val divided      = operand(OperandKind.IdResult, 19)
    val instructions = Vec(
      Instruction.TypeVoid(voidType),
      Instruction.TypeInt(u32, operand(OperandKind.LiteralInteger, 32), operand(OperandKind.LiteralInteger, 0)),
      Instruction.TypeVector(inputType, u32, operand(OperandKind.LiteralInteger, 2)),
      Instruction.TypeFloat(f32, operand(OperandKind.LiteralInteger, 32), None),
      Instruction.TypeVector(resultType, f32, operand(OperandKind.LiteralInteger, 2)),
      Instruction.TypePointer(
        outputType,
        operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup),
        resultType
      ),
      Instruction.TypeFunction(operand(OperandKind.IdResult, 7), voidType, Vec(outputType, inputType, resultType)),
      Instruction.EntryPoint(
        operand(OperandKind.ExecutionModel, spv.ExecutionModel.Kernel),
        function,
        stringOperand("signed_to_float"),
        Vec.empty
      ),
      Instruction.Function(
        voidType,
        function,
        operand(OperandKind.FunctionControl, spv.FunctionControl.None),
        operand(OperandKind.IdRef, 7)
      ),
      Instruction.FunctionParameter(outputType, output),
      Instruction.FunctionParameter(inputType, input),
      Instruction.FunctionParameter(resultType, rhs),
      Instruction.Label(operand(OperandKind.IdResult, 12)),
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
    val module =
      Module(Header(ISA.version, Word(0), Word(20), Word(0)), instructions, Endianness.Little)
    val allocation = AllocationId(4L)
    val memory     = ArrayMemory(allocation, 8)
    val dispatch   = KernelTask
      .validated(
        "signed_to_float",
        Dimensions.One,
        Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(allocation, 0L),
          KernelTask.Argument.Scalar(LittleEndian.toVec(0xfffffffeL, 4) ++
            LittleEndian.toVec(3L, 4)),
          KernelTask.Argument.Scalar(
            LittleEndian.toVec(java.lang.Float.floatToRawIntBits(0.5f).toLong, 4) ++
              LittleEndian.toVec(java.lang.Float.floatToRawIntBits(1.5f).toLong, 4)
          )
        )
      )
      .toOption
      .get

    assertEquals(executeUnchecked(module, dispatch, memory), Right(()))
    assertEquals(
      memory.bytes,
      LittleEndian.toVec(java.lang.Float.floatToRawIntBits(-2.0f).toLong, 4) ++
        LittleEndian.toVec(java.lang.Float.floatToRawIntBits(3.0f).toLong, 4)
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
        .updated(
          1,
          Instruction.TypeInt(u32, operand(OperandKind.LiteralInteger, 16), operand(OperandKind.LiteralInteger, 0))
        )
        .updated(3, Instruction.TypeFloat(f32, operand(OperandKind.LiteralInteger, 16), None))
    )
    val halfAllocation = AllocationId(5L)
    val halfMemory     = ArrayMemory(halfAllocation, 4)
    val halfDispatch   = KernelTask
      .validated(
        "signed_to_float",
        Dimensions.One,
        Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(halfAllocation, 0L),
          KernelTask.Argument.Scalar(LittleEndian.toVec(0L, 2) ++
            LittleEndian.toVec(3L, 2)),
          KernelTask.Argument.Scalar(LittleEndian.toVec(0x0001L, 2) ++
            LittleEndian.toVec(0x3e00L, 2))
        )
      )
      .toOption
      .get
    assertEquals(executeUnchecked(halfModule, halfDispatch, halfMemory), Right(()))
    assertEquals(
      halfMemory.bytes,
      LittleEndian.toVec(0L, 2) ++ LittleEndian.toVec(0x4200L, 2)
    )
  }

  test("dispatch converts floating-point values to unsigned integers without signed narrowing") {
    val voidType     = operand(OperandKind.IdResult, 1)
    val u64          = operand(OperandKind.IdResult, 2)
    val f64          = operand(OperandKind.IdResult, 3)
    val pointerType  = operand(OperandKind.IdResult, 4)
    val functionType = operand(OperandKind.IdResult, 5)
    val function     = operand(OperandKind.IdResult, 6)
    val output       = operand(OperandKind.IdResult, 7)
    val input        = operand(OperandKind.IdResult, 8)
    val result       = operand(OperandKind.IdResult, 10)
    val instructions = Vec(
      Instruction.TypeVoid(voidType),
      Instruction.TypeInt(u64, operand(OperandKind.LiteralInteger, 64), operand(OperandKind.LiteralInteger, 0)),
      Instruction.TypeFloat(f64, operand(OperandKind.LiteralInteger, 64), None),
      Instruction.TypePointer(pointerType, operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup), u64),
      Instruction.TypeFunction(functionType, voidType, Vec(pointerType, f64)),
      Instruction.EntryPoint(
        operand(OperandKind.ExecutionModel, spv.ExecutionModel.Kernel),
        function,
        stringOperand("float_to_unsigned"),
        Vec.empty
      ),
      Instruction.Function(
        voidType,
        function,
        operand(OperandKind.FunctionControl, spv.FunctionControl.None),
        functionType
      ),
      Instruction.FunctionParameter(pointerType, output),
      Instruction.FunctionParameter(f64, input),
      Instruction.Label(operand(OperandKind.IdResult, 9)),
      Instruction.ConvertFToU(u64, result, input),
      Instruction.Store(output, result, None),
      Instruction.Return(),
      Instruction.FunctionEnd()
    )
    val module =
      Module(Header(ISA.version, Word(0), Word(11), Word(0)), instructions, Endianness.Little)

    def run(id: Long > 0, value: Double): Either[Fault, Vec[Byte]] = {
      val allocation = AllocationId(id)
      val memory     = ArrayMemory(allocation, 8)
      val dispatch   = KernelTask
        .validated(
          "float_to_unsigned",
          Dimensions.One,
          Dimensions.One,
          Vec(
            KernelTask.Argument.Memory(allocation, 0L),
            KernelTask.Argument.Scalar(LittleEndian.toVec(
              java.lang.Double.doubleToRawLongBits(value),
              8
            ))
          )
        )
        .toOption
        .get
      executeUnchecked(module, dispatch, memory).map(_ => memory.bytes)
    }

    assertEquals(run(13L, 255.75), Right(LittleEndian.toVec(255L, 8)))
    assertEquals(
      run(14L, java.lang.Math.scalb(1.0, 63)),
      Right(LittleEndian.toVec(Long.MinValue, 8))
    )
    assertEquals(run(15L, -0.75), Right(LittleEndian.toVec(0L, 8)))
    run(15L, -1.0) match {
      case Left(Fault.InvalidTask(message)) =>
        assert(message.contains("negative values are outside the unsigned range"))
      case value => fail(s"expected unsigned conversion range failure, found $value")
    }

    val malformed  = module.copy(instructions = instructions.updated(10, Instruction.ConvertFToU(f64, result, input)))
    val allocation = AllocationId(16L)
    val dispatch   = KernelTask
      .validated(
        "float_to_unsigned",
        Dimensions.One,
        Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(allocation, 0L),
          KernelTask.Argument.Scalar(LittleEndian.toVec(
            java.lang.Double.doubleToRawLongBits(1.0),
            8
          ))
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

  for {
    sourceWidth <- Seq(16, 32, 64)
    resultWidth <- Seq(8, 16, 32, 64)
    lanes       <- Seq(1, 4)
  }
    test(s"unsigned conversion honors rounding and saturation at $sourceWidth to $resultWidth bits, $lanes lanes") {
      val samples  = Seq(2.3, 3.8, 2.5, 3.5)
      val expected = Seq(Seq(2L, 4L, 2L, 4L), Seq(2L, 3L, 2L, 3L), Seq(3L, 4L, 3L, 4L), Seq(2L, 3L, 2L, 3L))
      for {
        mode     <- 0 to 3
        start    <- samples.indices
        saturate <- Seq(false, true)
      }
        checkUnsignedConversion(
          sourceWidth,
          resultWidth,
          mode,
          saturate,
          Seq.tabulate(lanes)(lane => samples((start + lane) % samples.size)),
          Seq.tabulate(lanes)(lane => expected(mode)((start + lane) % samples.size))
        )
      for (mode <- Seq(0, 1, 2))
        checkUnsignedConversion(sourceWidth, resultWidth, mode, false, Seq.fill(lanes)(-0.5), Seq.fill(lanes)(0L))
      val maximum = ((BigInt(1) << resultWidth) - 1).toLong
      for (
        (value, expected) <- Seq((Double.NaN, 0L), (Double.NegativeInfinity, 0L), (Double.PositiveInfinity, maximum))
      )
        checkUnsignedConversion(sourceWidth, resultWidth, 0, true, Seq.fill(lanes)(value), Seq.fill(lanes)(expected))
      if (resultWidth == 64 && sourceWidth != 16)
        checkUnsignedConversion(
          sourceWidth,
          resultWidth,
          0,
          true,
          Seq.fill(lanes)(Math.scalb(1.0, 63)),
          Seq.fill(lanes)(Long.MinValue)
        )
    }

  private def checkUnsignedConversion(
      sourceWidth: Int,
      resultWidth: Int,
      mode: Int,
      saturate: Boolean,
      inputValues: Seq[Double],
      expected: Seq[Long]
  ): Unit = {
    val lanes       = inputValues.size
    val voidType    = operand(OperandKind.IdResult, 1)
    val floatScalar = operand(OperandKind.IdResult, 2)
    val intScalar   = operand(OperandKind.IdResult, 3)
    val floatType   = if (lanes == 1) floatScalar else operand(OperandKind.IdResult, 11)
    val intType     = if (lanes == 1) intScalar else operand(OperandKind.IdResult, 12)
    val pointer     = operand(OperandKind.IdResult, 4)
    val signature   = operand(OperandKind.IdResult, 5)
    val function    = operand(OperandKind.IdResult, 6)
    val output      = operand(OperandKind.IdResult, 7)
    val input       = operand(OperandKind.IdResult, 8)
    val converted   = operand(OperandKind.IdResult, 10)
    val types       = Vec[Instruction](
      Instruction.TypeVoid(voidType),
      Instruction.TypeFloat(floatScalar, operand(OperandKind.LiteralInteger, sourceWidth), None),
      Instruction.TypeInt(
        intScalar,
        operand(OperandKind.LiteralInteger, resultWidth),
        operand(OperandKind.LiteralInteger, 0)
      )
    ) ++
      (if (lanes == 1) Vec.empty[Instruction]
       else Vec[Instruction](
         Instruction.TypeVector(floatType, floatScalar, operand(OperandKind.LiteralInteger, lanes)),
         Instruction.TypeVector(intType, intScalar, operand(OperandKind.LiteralInteger, lanes))
       ))
    val decorations = Vec[Instruction](Instruction.Decorate(converted, operand(OperandKind.Decoration, 39, mode))) ++
      (if (saturate) Vec[Instruction](Instruction.Decorate(converted, operand(OperandKind.Decoration, 28)))
       else Vec.empty[Instruction])
    val instructions = types ++ decorations ++ Vec[Instruction](
      Instruction.TypePointer(pointer, operand(OperandKind.StorageClass, 5), intType),
      Instruction.TypeFunction(signature, voidType, Vec(pointer, floatType)),
      Instruction.EntryPoint(operand(OperandKind.ExecutionModel, 6), function, stringOperand("convert"), Vec.empty),
      Instruction.Function(voidType, function, operand(OperandKind.FunctionControl, 0), signature),
      Instruction.FunctionParameter(pointer, output),
      Instruction.FunctionParameter(floatType, input),
      Instruction.Label(operand(OperandKind.IdResult, 9)),
      Instruction.ConvertFToU(intType, converted, input),
      Instruction.Store(output, converted, None),
      Instruction.Return(),
      Instruction.FunctionEnd()
    )
    val module      = Module(Header(ISA.version, Word(0), Word(13), Word(0)), instructions, Endianness.Little)
    val allocation  = AllocationId(27L)
    val memory      = ArrayMemory(allocation, lanes * resultWidth / 8)
    val sourceBytes = Vec.from(inputValues.flatMap { value =>
      val bits = sourceWidth match {
        case 16 => IEEE754.doubleToBinary16(value).raw.toLong
        case 32 => java.lang.Float.floatToRawIntBits(value.toFloat).toLong
        case _  => java.lang.Double.doubleToRawLongBits(value)
      }
      LittleEndian.toVec(bits, (sourceWidth / 8).refine[Int in 0 ~ 8].toOption.get)
    })
    val task = KernelTask.validated(
      "convert",
      Dimensions.One,
      Dimensions.One,
      Vec(KernelTask.Argument.Memory(allocation, 0L), KernelTask.Argument.Scalar(sourceBytes))
    ).toOption.get
    assertEquals(executeUnchecked(module, task, memory), Right(()))
    assertEquals(
      memory.bytes,
      Vec.from(expected.flatMap(value =>
        LittleEndian.toVec(value, (resultWidth / 8).refine[Int in 0 ~ 8].toOption.get)
      ))
    )
  }

  test("saturated float-to-signed conversion maps NaN to zero") {
    val voidType  = operand(OperandKind.IdResult, 1)
    val floatType = operand(OperandKind.IdResult, 2)
    val intType   = operand(OperandKind.IdResult, 3)
    val pointer   = operand(OperandKind.IdResult, 4)
    val signature = operand(OperandKind.IdResult, 5)
    val function  = operand(OperandKind.IdResult, 6)
    val output    = operand(OperandKind.IdResult, 7)
    val input     = operand(OperandKind.IdResult, 8)
    val converted = operand(OperandKind.IdResult, 10)
    val module    = Module(
      Header(ISA.version, Word(0), Word(11), Word(0)),
      Vec(
        Instruction.TypeVoid(voidType),
        Instruction.TypeFloat(floatType, operand(OperandKind.LiteralInteger, 32), None),
        Instruction.TypeInt(intType, operand(OperandKind.LiteralInteger, 32), operand(OperandKind.LiteralInteger, 1)),
        Instruction.TypePointer(pointer, operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup), intType),
        Instruction.TypeFunction(signature, voidType, Vec(pointer, floatType)),
        Instruction.Decorate(converted, operand(OperandKind.Decoration, spv.Decoration.SaturatedConversion)),
        Instruction.EntryPoint(
          operand(OperandKind.ExecutionModel, spv.ExecutionModel.Kernel),
          function,
          stringOperand("saturated_nan"),
          Vec.empty
        ),
        Instruction.Function(
          voidType,
          function,
          operand(OperandKind.FunctionControl, spv.FunctionControl.None),
          signature
        ),
        Instruction.FunctionParameter(pointer, output),
        Instruction.FunctionParameter(floatType, input),
        Instruction.Label(operand(OperandKind.IdResult, 9)),
        Instruction.ConvertFToS(intType, converted, input),
        Instruction.Store(output, converted, None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val allocation = AllocationId(17L)
    val memory     = ArrayMemory(allocation, 4)
    val dispatch   = KernelTask
      .validated(
        "saturated_nan",
        Dimensions.One,
        Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(allocation, 0L),
          KernelTask.Argument.Scalar(
            LittleEndian.toVec(java.lang.Float.floatToRawIntBits(Float.NaN).toLong, 4)
          )
        )
      )
      .toOption
      .get

    assertEquals(executeUnchecked(module, dispatch, memory), Right(()))
    assertEquals(memory.bytes, LittleEndian.toVec(0L, 4))
  }

}
