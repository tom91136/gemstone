package net.kurobako.gemstone.isa.spirv.v1_6

import execution.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class ExecutionHalfMemorySuite extends munit.FunSuite {
  private val voidType      = operand(OperandKind.IdResult, 1)
  private val sizeType      = operand(OperandKind.IdResult, 2)
  private val halfType      = operand(OperandKind.IdResult, 3)
  private val floatType     = operand(OperandKind.IdResult, 4)
  private val vectorType    = operand(OperandKind.IdResult, 5)
  private val halfPointer   = operand(OperandKind.IdResult, 6)
  private val vectorPointer = operand(OperandKind.IdResult, 7)
  private val signature     = operand(OperandKind.IdResult, 8)
  private val opencl        = operand(OperandKind.IdResult, 9)
  private val offset        = operand(OperandKind.IdResult, 10)
  private val function      = operand(OperandKind.IdResult, 11)
  private val pointer       = operand(OperandKind.IdResult, 12)
  private val data          = operand(OperandKind.IdResult, 13)
  private val result        = operand(OperandKind.IdResult, 15)

  private def module(load: Boolean, opcode: Int, mode: Option[Int]): Module = {
    val secondType = if (load) vectorPointer else vectorType
    val sources    = if (load) Vec(offset, pointer, operand(OperandKind.LiteralInteger, 3))
    else Vec(data, offset, pointer) ++ Vec.from(mode.map(value => operand(OperandKind.FPRoundingMode, value)))
    Module(
      Header(ISA.version, Word(0), Word(16), Word(0)),
      Vec[Instruction](
        Instruction.TypeVoid(voidType),
        Instruction.TypeInt(sizeType, operand(OperandKind.LiteralInteger, 64), operand(OperandKind.LiteralInteger, 0)),
        Instruction.TypeFloat(halfType, operand(OperandKind.LiteralInteger, 16), None),
        Instruction.TypeFloat(floatType, operand(OperandKind.LiteralInteger, 32), None),
        Instruction.TypeVector(vectorType, floatType, operand(OperandKind.LiteralInteger, 3)),
        Instruction.TypePointer(halfPointer, operand(OperandKind.StorageClass, StorageClass.CrossWorkgroup), halfType),
        Instruction.TypePointer(
          vectorPointer,
          operand(OperandKind.StorageClass, StorageClass.CrossWorkgroup),
          vectorType
        ),
        Instruction.TypeFunction(signature, voidType, Vec(halfPointer, secondType)),
        Instruction.ExtInstImport(opencl, stringOperand("OpenCL.std")),
        Instruction.Constant(sizeType, offset, operand(OperandKind.LiteralContextDependentNumber, 1, 0)),
        Instruction.EntryPoint(
          operand(OperandKind.ExecutionModel, ExecutionModel.Kernel),
          function,
          stringOperand("half_memory"),
          Vec.empty
        ),
        Instruction.Function(voidType, function, operand(OperandKind.FunctionControl, FunctionControl.None), signature),
        Instruction.FunctionParameter(halfPointer, pointer),
        Instruction.FunctionParameter(secondType, data),
        Instruction.Label(operand(OperandKind.IdResult, 14)),
        Instruction.ExtInst(
          if (load) vectorType else voidType,
          result,
          opencl,
          operand(OperandKind.LiteralExtInstInteger, opcode),
          sources
        )
      ) ++ (if (load) Vec[Instruction](Instruction.Store(data, result, None)) else Vec.empty[Instruction]) ++
        Vec(Instruction.Return(), Instruction.FunctionEnd()),
      Endianness.Little
    )
  }

  private def run(program: Module, initial: Vec[Byte], argument: KernelTask.Argument): Vec[Byte] = {
    val allocation = AllocationId(1L)
    val memory     = ArrayMemory(allocation, initial.size)
    assertEquals(memory.write(allocation, 0L, initial), Right(()))
    val task = KernelTask.validated(
      "half_memory",
      Dimensions.One,
      Dimensions.One,
      Vec(KernelTask.Argument.Memory(allocation, 0L), argument)
    ).toOption.get
    assertEquals(executeUnchecked(program, task, memory), Right(()))
    memory.bytes
  }

  // OpenCL.std: aligned half3 uses four-half strides; the padding element is not accessed.
  for (
    (label, opcode, start) <- Seq(
      ("packed", ExtendedInstruction.OpenClStd.VloadHalfn, 6),
      ("aligned", ExtendedInstruction.OpenClStd.VloadaHalfn, 8)
    )
  ) test(s"$label half3 load uses the correct stride and preserves signed zero and subnormals") {
    val sentinel = Vec.fill(48)(0x55.toByte)
    val halves   = Vec(0x8000L, 1L, 0xc100L).flatMap(value => LittleEndian.toVec(value, 2))
    val initial  = sentinel.take(start) ++ halves ++ sentinel.drop(start + halves.size)
    val expected = Vec(0x80000000L, 0x33800000L, 0xc0200000L).flatMap(value => LittleEndian.toVec(value, 4))
    val actual   = run(module(true, opcode, None), initial, KernelTask.Argument.Memory(AllocationId(1L), 32L))
    // Ordinary float3 storage may overwrite its fourth, padding component.
    assertEquals(actual.take(44), initial.take(32) ++ expected)
  }

  private val roundingCases = Seq(
    ("nearest even", FPRoundingMode.RTE, Vec(0x3c00L, 0xbc00L, 0x3c02L), Vec(0x7c00L, 0xfc00L, 0x0000L)),
    ("toward zero", FPRoundingMode.RTZ, Vec(0x3c00L, 0xbc00L, 0x3c01L), Vec(0x7bffL, 0xfbffL, 0x0000L)),
    ("upward", FPRoundingMode.RTP, Vec(0x3c01L, 0xbc00L, 0x3c02L), Vec(0x7c00L, 0xfbffL, 0x0001L)),
    ("downward", FPRoundingMode.RTN, Vec(0x3c00L, 0xbc01L, 0x3c01L), Vec(0x7bffL, 0xfc00L, 0x0000L))
  )
  for {
    (label, opcode, start) <- Seq(
      ("packed", ExtendedInstruction.OpenClStd.VstoreHalfnR, 6),
      ("aligned", ExtendedInstruction.OpenClStd.VstoreaHalfnR, 8)
    )
    (rounding, mode, ties, limits) <- roundingCases
    (values, expected, boundary)   <- Seq(
      (Vec(0x3f801000L, 0xbf801000L, 0x3f803000L), ties, "ties"),
      (Vec(0x477ff000L, 0xc77ff000L, 0x33000000L), limits, "overflow and underflow")
    )
  } test(s"$label half3 store rounds $boundary $rounding and leaves padding intact") {
    val initial  = Vec.fill(24)(0x55.toByte)
    val argument = KernelTask.Argument.Scalar(values.flatMap(value => LittleEndian.toVec(value, 4)))
    val bytes    = expected.flatMap(value => LittleEndian.toVec(value, 2))
    assertEquals(
      run(module(false, opcode, Some(mode)), initial, argument),
      initial.take(start) ++ bytes ++ initial.drop(start + bytes.size)
    )
  }
  for (
    (label, mode, tiny) <- Seq(
      ("nearest even", FPRoundingMode.RTE, 0x8000L),
      ("toward zero", FPRoundingMode.RTZ, 0x8000L),
      ("upward", FPRoundingMode.RTP, 0x8000L),
      ("downward", FPRoundingMode.RTN, 0x8001L)
    )
  ) test(s"half store handles negative underflow and infinities $label") {
    val initial  = Vec.fill(24)(0x55.toByte)
    val input    = Vec(0xb3000000L, 0x7f800000L, 0xff800000L).flatMap(value => LittleEndian.toVec(value, 4))
    val expected = Vec(tiny, 0x7c00L, 0xfc00L).flatMap(value => LittleEndian.toVec(value, 2))
    assertEquals(
      run(
        module(false, ExtendedInstruction.OpenClStd.VstoreHalfnR, Some(mode)),
        initial,
        KernelTask.Argument.Scalar(input)
      ),
      initial.take(6) ++ expected ++ initial.drop(12)
    )
  }

  test("half store without a rounding operand defaults to nearest even at overflow") {
    val initial  = Vec.fill(24)(0x55.toByte)
    val input    = Vec(0x477ff000L, 0xc77ff000L, 0x80000000L).flatMap(value => LittleEndian.toVec(value, 4))
    val expected = Vec(0x7c00L, 0xfc00L, 0x8000L).flatMap(value => LittleEndian.toVec(value, 2))
    assertEquals(
      run(
        module(false, ExtendedInstruction.OpenClStd.VstoreHalfn, None),
        initial,
        KernelTask.Argument.Scalar(input)
      ),
      initial.take(6) ++ expected ++ initial.drop(12)
    )
  }

}
