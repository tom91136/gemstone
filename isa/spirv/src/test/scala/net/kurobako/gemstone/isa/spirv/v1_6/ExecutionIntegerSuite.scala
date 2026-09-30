package net.kurobako.gemstone.isa.spirv.v1_6

import execution.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class ExecutionIntegerSuite extends munit.FunSuite {
  for {
    width  <- Seq(8, 16, 32, 64)
    lanes  <- Seq(1, 2, 3, 4, 8, 16)
    signed <- Seq(false, true)
  } test(s"OpenCL integer abs preserves the unsigned magnitude at width $width, lanes $lanes, signed $signed") {
    val modulus  = BigInt(1) << width
    val boundary = modulus >> 1
    val samples  = Seq(BigInt(0), BigInt(1), boundary - 1, boundary, boundary + 1, modulus - 1)
    for (start <- samples.indices) {
      val input    = Seq.tabulate(lanes)(index => samples((start + index) % samples.size))
      val expected = input.map(value => if (signed && value >= boundary) modulus - value else value)
      val opcode   = if (signed) ExtendedInstruction.OpenClStd.SAbs else ExtendedInstruction.OpenClStd.UAbs
      checkInteger(opcode, width, Seq(input.map(_.toLong)), expected.map(_.toLong))
    }
  }

  for {
    width  <- Seq(8, 16, 32, 64)
    signed <- Seq(false, true)
  } test(s"OpenCL difference, saturation and clamp handle boundary values at width $width, signed $signed") {
    val modulus = BigInt(1) << width
    val minimum = if (signed) -(modulus >> 1) else BigInt(0)
    val maximum = if (signed) (modulus >> 1) - 1 else modulus - 1
    val samples = Seq(minimum, minimum + 1, BigInt(0), BigInt(1), maximum - 1, maximum)
    for {
      left  <- samples
      right <- samples
    } {
      val inputs = Seq(Seq(left.toLong), Seq(right.toLong))
      checkInteger(
        if (signed) ExtendedInstruction.OpenClStd.SAbsDiff else ExtendedInstruction.OpenClStd.UAbsDiff,
        width,
        inputs,
        Seq((left - right).abs.toLong)
      )
      checkInteger(
        if (signed) ExtendedInstruction.OpenClStd.SAddSat else ExtendedInstruction.OpenClStd.UAddSat,
        width,
        inputs,
        Seq((left + right).max(minimum).min(maximum).toLong)
      )
    }
    for (lanes <- Seq(1, 2, 3, 4, 8, 16)) {
      val input = Seq.tabulate(lanes)(index => samples(index % samples.size))
      val lower = minimum + 1
      val upper = maximum - 1
      checkInteger(
        if (signed) ExtendedInstruction.OpenClStd.SClamp else ExtendedInstruction.OpenClStd.UClamp,
        width,
        Seq(input.map(_.toLong), Seq.fill(lanes)(lower.toLong), Seq.fill(lanes)(upper.toLong)),
        input.map(_.max(lower).min(upper).toLong)
      )
    }
  }

  private val integerOperations = {
    import ExtendedInstruction.OpenClStd.*
    Seq(
      ("hadd", SHadd, UHadd),
      ("rhadd", SRhadd, URhadd),
      ("sub_sat", SSubSat, USubSat),
      ("mul_hi", SMulHi, UMulHi),
      ("mad_hi", SMadHi, UMadHi),
      ("mad_sat", SMadSat, UMadSat),
      ("mul24", SMul24, UMul24),
      ("mad24", SMad24, UMad24),
      ("upsample", SUpsample, UUpsample)
    )
  }
  for {
    (name, signedOpcode, unsignedOpcode) <- integerOperations
    signed                               <- Seq(false, true)
    width <- if (name.endsWith("24")) Seq(32) else if (name == "upsample") Seq(8, 16, 32) else Seq(8, 16, 32, 64)
  } test(s"OpenCL $name handles overflow boundaries at width $width, signed $signed") {
    val modulus      = BigInt(1) << width
    val minimum      = if (signed) -(modulus >> 1) else BigInt(0)
    val maximum      = if (signed) (modulus >> 1) - 1 else modulus - 1
    val inputModulus = if (name.endsWith("24")) BigInt(1) << 24 else modulus
    val inputMinimum = if (signed) -(inputModulus >> 1) else BigInt(0)
    val inputMaximum = if (signed) (inputModulus >> 1) - 1 else inputModulus - 1
    val samples      = Seq(inputMinimum, inputMinimum + 1, BigInt(0), BigInt(1), inputMaximum - 1, inputMaximum)
    for {
      lanes <- Seq(1, 3, 4)
      start <- samples.indices
    } {
      val left     = Seq.tabulate(lanes)(index => samples((start + index) % samples.size))
      val right    = Seq.tabulate(lanes)(index => samples((start * 3 + index + 1) % samples.size))
      val third    = Seq.tabulate(lanes)(index => Seq(minimum, BigInt(0), BigInt(1), maximum)((start + index) % 4))
      val expected = left.indices.map { index =>
        val x = left(index); val y = right(index); val z = third(index)
        (name match {
          case "hadd"     => (x + y) >> 1
          case "rhadd"    => (x + y + 1) >> 1
          case "sub_sat"  => (x - y).max(minimum).min(maximum)
          case "mul_hi"   => (x * y) >> width
          case "mad_hi"   => ((x * y) >> width) + z
          case "mad_sat"  => (x * y + z).max(minimum).min(maximum)
          case "mul24"    => x * y
          case "mad24"    => x * y + z
          case "upsample" => (x << width) | (y & (modulus - 1))
        }).toLong
      }
      val inputs = Seq(left.map(_.toLong), right.map(_.toLong)) ++
        (if (name.startsWith("mad")) Seq(third.map(_.toLong)) else Seq.empty)
      checkInteger(
        if (signed) signedOpcode else unsignedOpcode,
        width,
        inputs,
        expected,
        resultWidth = Some(if (name == "upsample") width * 2 else width)
      )
    }
  }

  for (width <- Seq(8, 16, 32, 64)) test(s"OpenCL rotate masks the shift count at width $width") {
    val bits = (BigInt(1) << (width - 1)) | 3
    for (shift <- Seq(0, 1, width - 1, width, width + 1, -1)) {
      val count    = shift & (width - 1)
      val expected = ((bits << count) | (bits >> (width - count))).toLong
      checkInteger(
        ExtendedInstruction.OpenClStd.Rotate,
        width,
        Seq(Seq.fill(4)(bits.toLong), Seq.fill(4)(shift.toLong)),
        Seq.fill(4)(expected)
      )
    }
  }

  private type BinaryInstruction = (Operand, Operand, Operand, Operand) => Instruction
  private val divisions: Seq[(String, Boolean, Boolean, BinaryInstruction)] = Seq(
    ("SDiv", true, false, Instruction.SDiv.apply),
    ("SRem", true, true, Instruction.SRem.apply),
    ("UDiv", false, false, Instruction.UDiv.apply),
    ("UMod", false, true, Instruction.UMod.apply)
  )
  for {
    (name, signed, remainder, instruction) <- divisions
    width                                  <- Seq(8, 16, 32, 64)
    lanes                                  <- Seq(1, 2, 3, 4, 8, 16)
  } test(s"$name continues past exceptional integer lanes at width $width, lanes $lanes") {
    val modulus = BigInt(1) << width
    val samples = Seq(
      (BigInt(7), BigInt(0)),
      (BigInt(9), BigInt(2)),
      (BigInt(-9), BigInt(2)),
      (-(modulus >> 1), BigInt(-1))
    )
    def value(raw: BigInt): BigInt = if (signed) raw else raw & (modulus - 1)
    for (start <- samples.indices) {
      val input    = Seq.tabulate(lanes)(index => samples((start + index) % samples.size))
      val expected = input.map { (left, right) =>
        if (right == 0) 0L
        else if (remainder) (value(left) % value(right)).toLong
        else (value(left) / value(right)).toLong
      }
      checkInteger(0, width, Seq(input.map(_._1.toLong), input.map(_._2.toLong)), expected, Some(instruction))
    }
  }

  private def checkInteger(
      opcode: Int,
      width: Int,
      inputs: Seq[Seq[Long]],
      expected: Seq[Long],
      core: Option[BinaryInstruction] = None,
      resultWidth: Option[Int] = None
  ): Unit = {
    val outputWidth  = resultWidth.getOrElse(width)
    val lanes        = expected.size
    val scalar       = operand("IdResult", 2)
    val valueType    = if (lanes == 1) scalar else operand("IdResult", 3)
    val resultScalar = if (outputWidth == width) scalar else operand("IdResult", 16)
    val resultType = if (outputWidth == width) valueType else if (lanes == 1) resultScalar else operand("IdResult", 17)
    val pointer    = operand("IdResult", 4)
    val functionType = operand("IdResult", 5)
    val opencl       = operand("IdResult", 6)
    val function     = operand("IdResult", 7)
    val output       = operand("IdResult", 8)
    val arguments    = Vec.from(inputs.indices.map(index => operand("IdResult", 9 + index)))
    val declarations = Vec[Instruction](
      Instruction.ExtInstImport(opencl, stringOperand("OpenCL.std")),
      Instruction.TypeVoid(operand("IdResult", 1)),
      Instruction.TypeInt(scalar, operand("LiteralInteger", width), operand("LiteralInteger", 0))
    ) ++
      (if (lanes == 1) Vec.empty[Instruction]
       else Vec[Instruction](Instruction.TypeVector(valueType, scalar, operand("LiteralInteger", lanes))))
    val resultDeclarations = if (outputWidth == width) Vec.empty[Instruction]
    else
      Vec[Instruction](Instruction.TypeInt(
        resultScalar,
        operand("LiteralInteger", outputWidth),
        operand("LiteralInteger", 0)
      )) ++
        (if (lanes == 1) Vec.empty[Instruction]
         else Vec[Instruction](Instruction.TypeVector(resultType, resultScalar, operand("LiteralInteger", lanes))))
    val instructions = declarations ++ resultDeclarations ++ Vec(
      Instruction.TypePointer(pointer, operand("StorageClass", 5), resultType),
      Instruction.TypeFunction(functionType, operand("IdRef", 1), Vec(pointer) ++ Vec.fill(inputs.size)(valueType)),
      Instruction.EntryPoint(operand("ExecutionModel", 6), function, stringOperand("integer"), Vec.empty),
      Instruction.Function(operand("IdRef", 1), function, operand("FunctionControl", 0), functionType),
      Instruction.FunctionParameter(pointer, output)
    ) ++ arguments.map(argument => Instruction.FunctionParameter(valueType, argument)) ++ Vec(
      Instruction.Label(operand("IdResult", 20)),
      core.fold[Instruction](Instruction.ExtInst(
        resultType,
        operand("IdResult", 21),
        opencl,
        operand("LiteralExtInstInteger", opcode),
        arguments
      ))(instruction => instruction(resultType, operand("IdResult", 21), arguments(0), arguments(1))),
      Instruction.Store(output, operand("IdRef", 21), None),
      Instruction.Return(),
      Instruction.FunctionEnd()
    )
    val module     = Module(Header(ISA.version, Word(0), Word(22), Word(0)), instructions, Endianness.Little)
    val allocation = AllocationId(1L)
    val memory     = ArrayMemory(allocation, (if (lanes == 3) 4 else lanes) * outputWidth / 8)
    val byteWidth  = (width / 8).refine[Int where Between[0, 8]].toOption.get
    def bytes(values: Seq[Long]): Vec[Byte] = Vec.from(values.flatMap(value => LittleEndian.toVec(value, byteWidth)))
    val task                                = KernelTask.validated(
      "integer",
      Dimensions.One,
      Dimensions.One,
      Vec[KernelTask.Argument](KernelTask.Argument.Memory(allocation, 0L)) ++
        Vec.from(inputs.map(values => KernelTask.Argument.Scalar(bytes(values))))
    ).toOption.get
    assertEquals(executeUnchecked(module, task, memory), Right(()))
    assertEquals(
      memory.bytes.take(lanes * outputWidth / 8),
      Vec.from(expected.flatMap(value =>
        LittleEndian.toVec(value, (outputWidth / 8).refine[Int where Between[0, 8]].toOption.get)
      ))
    )
  }
}
