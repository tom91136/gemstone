package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.isa.spirv.v1_6.execution.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class GLSLIntegerMinMaxSuite extends munit.FunSuite {
  for {
    width  <- Seq[Int in (8, 16, 32, 64)](8, 16, 32, 64)
    rawWidth = width.raw
    byteWidth = width.widen[Int in (8, 16, 32, 64)].toMemoryValueByteWidth
    opcode <- Seq(5, 7, 38, 39, 41, 42, 44, 45, 73, 74, 75)
    if opcode < 73 || rawWidth == 32
    count          <- Seq(1, 4)
    declaredSigned <- Seq(0, 1)
  }
    test(s"GLSL integer opcode $opcode: $rawWidth bits, $count lanes, declared signedness $declaredSigned") {
      val signed  = Set(5, 7, 39, 42, 45).contains(opcode)
      val unary   = opcode == 5 || opcode == 7 || opcode >= 73
      val high    = BigInt(1) << (rawWidth - 1)
      val maximum = if (signed) high - 1 else (high << 1) - 1
      val inputs  = if (opcode >= 73) Seq(BigInt(0), BigInt(-1), -high, high - 1)
      else if (unary) Seq(BigInt(0), BigInt(-5), -high, maximum)
      else if (signed) Seq(-high, BigInt(-5), BigInt(3), maximum)
      else Seq(BigInt(0), maximum, high, BigInt(3))
      val lower = if (signed) Seq(-7, -2, 0, 5).map(BigInt(_))
      else Seq(BigInt(1), high, high - 1, BigInt(3))
      val upper = if (signed) Seq(BigInt(7), BigInt(2), BigInt(2), maximum - 1)
      else Seq(BigInt(2), maximum - 1, maximum, BigInt(5))
      val expected = opcode match {
        case 5  => Seq(BigInt(0), BigInt(5), high, maximum)
        case 7  => Seq(0, -1, -1, 1).map(BigInt(_))
        case 73 => Seq(-1, 0, 31, 0).map(BigInt(_))
        case 74 => Seq(-1, -1, 30, 30).map(BigInt(_))
        case 75 => Seq(-1, 31, 31, 30).map(BigInt(_))
        case 39 => Seq(-high, BigInt(-5), BigInt(0), BigInt(5))
        case 42 => Seq(BigInt(-7), BigInt(-2), BigInt(3), maximum)
        case 45 => Seq(BigInt(-7), BigInt(-2), BigInt(2), maximum - 1)
        case 38 => Seq(BigInt(0), high, high - 1, BigInt(3))
        case 41 => Seq(BigInt(1), maximum, high, BigInt(3))
        case _  => Seq(BigInt(1), maximum - 1, high, BigInt(3))
      }
      val voidType     = operand(OperandKind.IdResult, 1)
      val scalar       = operand(OperandKind.IdResult, 2)
      val vector       = operand(OperandKind.IdResult, 3)
      val pointer      = operand(OperandKind.IdResult, 4)
      val functionType = operand(OperandKind.IdResult, 5)
      val function     = operand(OperandKind.IdResult, 6)
      val output       = operand(OperandKind.IdResult, 7)
      val left         = operand(OperandKind.IdResult, 8)
      val right        = operand(OperandKind.IdResult, 9)
      val bound        = operand(OperandKind.IdResult, 10)
      val result       = operand(OperandKind.IdResult, 11)
      val importId     = operand(OperandKind.IdResult, 12)
      val valueType    = if (count == 1) scalar else vector
      val module       = Module(
        Header(ISA.version, Word(0), Word(14), Word(0)),
        Vec(
          Instruction.TypeVoid(voidType),
          Instruction.ExtInstImport(importId, stringOperand("GLSL.std.450")),
          Instruction.TypeInt(
            scalar,
            operand(OperandKind.LiteralInteger, rawWidth),
            operand(OperandKind.LiteralInteger, declaredSigned)
          ),
          Instruction.TypeVector(vector, scalar, operand(OperandKind.LiteralInteger, 4)),
          Instruction.TypePointer(pointer, operand(OperandKind.StorageClass, StorageClass.CrossWorkgroup), valueType),
          Instruction.TypeFunction(functionType, voidType, Vec(pointer, valueType, valueType, valueType)),
          Instruction.EntryPoint(
            operand(OperandKind.ExecutionModel, ExecutionModel.Kernel),
            function,
            stringOperand("minmax"),
            Vec.empty
          ),
          Instruction.Function(
            voidType,
            function,
            operand(OperandKind.FunctionControl, FunctionControl.None),
            functionType
          ),
          Instruction.FunctionParameter(pointer, output),
          Instruction.FunctionParameter(valueType, left),
          Instruction.FunctionParameter(valueType, right),
          Instruction.FunctionParameter(valueType, bound),
          Instruction.Label(operand(OperandKind.IdResult, 13)),
          Instruction.ExtInst(
            valueType,
            result,
            importId,
            operand(OperandKind.LiteralExtInstInteger, opcode),
            if (unary) Vec(left) else if (opcode >= 44) Vec(left, right, bound) else Vec(left, right)
          ),
          Instruction.Store(output, result, None),
          Instruction.Return(),
          Instruction.FunctionEnd()
        ),
        Endianness.Little
      )
      def encoded(values: Seq[BigInt]): Vec[Byte] = Vec.from(values.take(count).flatMap { value =>
        LittleEndian.toVec(value.toLong, byteWidth.widen[Int in 0 ~ 8])
      })
      val allocation = AllocationId(1L)
      val memory     = ArrayMemory(allocation, byteWidth.raw * count)
      val dispatch   = KernelTask.validated(
        "minmax",
        Dimensions.One,
        Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(allocation, 0L),
          KernelTask.Argument.Scalar(encoded(inputs)),
          KernelTask.Argument.Scalar(encoded(lower)),
          KernelTask.Argument.Scalar(encoded(upper))
        )
      ).toOption.get
      assertEquals(executeUnchecked(module, dispatch, memory), Right(()))
      assertEquals(memory.bytes, encoded(expected))
    }
}
