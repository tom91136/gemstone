package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.isa.spirv.v1_6.execution.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class ExecutionRemainderSuite extends munit.FunSuite {
  for {
    width     <- Seq[Int in (16, 32, 64)](16, 32, 64)
    rawWidth = width.raw
    byteWidth = width.widen[Int in (8, 16, 32, 64)].toMemoryValueByteWidth
    operation <- Seq("FRem", "FMod", "SMod")
  }
    test(s"$operation preserves operand signs in $rawWidth-bit vector lanes") {
      val voidType     = operand(OperandKind.IdResult, 1)
      val scalar       = operand(OperandKind.IdResult, 2)
      val vector       = operand(OperandKind.IdResult, 3)
      val pointer      = operand(OperandKind.IdResult, 4)
      val functionType = operand(OperandKind.IdResult, 5)
      val function     = operand(OperandKind.IdResult, 6)
      val output       = operand(OperandKind.IdResult, 7)
      val left         = operand(OperandKind.IdResult, 8)
      val right        = operand(OperandKind.IdResult, 9)
      val result       = operand(OperandKind.IdResult, 11)
      val module       = Module(
        Header(ISA.version, Word(0), Word(12), Word(0)),
        Vec(
          Instruction.TypeVoid(voidType),
          if (operation == "SMod")
            Instruction.TypeInt(
              scalar,
              operand(OperandKind.LiteralInteger, rawWidth),
              operand(OperandKind.LiteralInteger, 1)
            )
          else Instruction.TypeFloat(scalar, operand(OperandKind.LiteralInteger, rawWidth), None),
          Instruction.TypeVector(vector, scalar, operand(OperandKind.LiteralInteger, 4)),
          Instruction.TypePointer(pointer, operand(OperandKind.StorageClass, StorageClass.CrossWorkgroup), vector),
          Instruction.TypeFunction(functionType, voidType, Vec(pointer, vector, vector)),
          Instruction.EntryPoint(
            operand(OperandKind.ExecutionModel, ExecutionModel.Kernel),
            function,
            stringOperand("remainder"),
            Vec.empty
          ),
          Instruction.Function(
            voidType,
            function,
            operand(OperandKind.FunctionControl, FunctionControl.None),
            functionType
          ),
          Instruction.FunctionParameter(pointer, output),
          Instruction.FunctionParameter(vector, left),
          Instruction.FunctionParameter(vector, right),
          Instruction.Label(operand(OperandKind.IdResult, 10)),
          operation match {
            case "SMod" => Instruction.SMod(vector, result, left, right)
            case "FMod" => Instruction.FMod(vector, result, left, right)
            case _      => Instruction.FRem(vector, result, left, right)
          },
          Instruction.Store(output, result, None),
          Instruction.Return(),
          Instruction.FunctionEnd()
        ),
        Endianness.Little
      )
      def encoded(values: Seq[Double]): Vec[Byte] = Vec.from(values.flatMap { value =>
        val bits = if (operation == "SMod") value.toLong
        else rawWidth match {
          case 16 => IEEE754.floatToBinary16(value.toFloat).raw.toLong
          case 32 => java.lang.Float.floatToRawIntBits(value.toFloat).toLong
          case 64 => java.lang.Double.doubleToRawLongBits(value)
        }
        LittleEndian.toVec(bits, byteWidth.widen[Int in 0 ~ 8])
      })
      val allocation = AllocationId(1L)
      val memory     = ArrayMemory(allocation, byteWidth.raw * 4)
      val dispatch   = KernelTask.validated(
        "remainder",
        Dimensions.One,
        Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(allocation, 0L),
          KernelTask.Argument.Scalar(encoded(Seq(5.5, -5.5, 5.5, -5.5))),
          KernelTask.Argument.Scalar(encoded(Seq(2, 2, -2, -2)))
        )
      ).toOption.get
      assertEquals(executeUnchecked(module, dispatch, memory), Right(()))
      assertEquals(
        memory.bytes,
        encoded(if (operation == "SMod") Seq(1, 1, -1, -1)
        else if (operation == "FMod") Seq(1.5, 0.5, -0.5, -1.5) else Seq(1.5, -1.5, 1.5, -1.5))
      )
    }
}
