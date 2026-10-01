package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.spirv.v1_6.execution.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class GLSLDistanceSuite extends munit.FunSuite {
  for {
    width     <- Seq(16, 32, 64)
    operation <- Seq("Length", "Distance")
    count     <- Seq(1, 4)
  }
    test(s"$operation computes $width-bit results from $count components") {
      val voidType     = operand("IdResult", 1)
      val scalar       = operand("IdResult", 2)
      val vector       = operand("IdResult", 3)
      val pointer      = operand("IdResult", 4)
      val functionType = operand("IdResult", 5)
      val function     = operand("IdResult", 6)
      val output       = operand("IdResult", 7)
      val left         = operand("IdResult", 8)
      val right        = operand("IdResult", 9)
      val result       = operand("IdResult", 11)
      val module       = Module(
        Header(ISA.version, Word(0), Word(14), Word(0)),
        Vec(
          Instruction.TypeVoid(voidType),
          Instruction.ExtInstImport(operand("IdResult", 12), stringOperand("GLSL.std.450")),
          Instruction.TypeFloat(scalar, operand("LiteralInteger", width), None),
          Instruction.TypeVector(vector, scalar, operand("LiteralInteger", 4)),
          Instruction.TypePointer(pointer, operand("StorageClass", StorageClass.CrossWorkgroup), scalar),
          Instruction.TypeFunction(
            functionType,
            voidType,
            Vec(pointer, if (count == 1) scalar else vector, if (count == 1) scalar else vector)
          ),
          Instruction.EntryPoint(
            operand("ExecutionModel", ExecutionModel.Kernel),
            function,
            stringOperand("remainder"),
            Vec.empty
          ),
          Instruction.Function(voidType, function, operand("FunctionControl", FunctionControl.None), functionType),
          Instruction.FunctionParameter(pointer, output),
          Instruction.FunctionParameter(if (count == 1) scalar else vector, left),
          Instruction.FunctionParameter(if (count == 1) scalar else vector, right),
          Instruction.Label(operand("IdResult", 10)),
          Instruction.ExtInst(
            scalar,
            result,
            operand("IdRef", 12),
            operand("LiteralExtInstInteger", if (operation == "Length") 66 else 67),
            if (operation == "Length") Vec(left) else Vec(left, right)
          ),
          Instruction.Store(output, result, None),
          Instruction.Return(),
          Instruction.FunctionEnd()
        ),
        Endianness.Little
      )
      def encoded(values: Seq[Double]): Vec[Byte] = Vec.from(values.flatMap { value =>
        val bits = width match {
          case 16 => IEEE754.floatToBinary16(value.toFloat).raw.toLong
          case 32 => java.lang.Float.floatToRawIntBits(value.toFloat).toLong
          case 64 => java.lang.Double.doubleToRawLongBits(value)
        }
        LittleEndian.toVec(bits, (width / 8).unsafe[Int where Between[0, 8]])
      })
      val allocation = AllocationId(1L)
      val memory     = ArrayMemory(allocation, width / 8)
      val dispatch   = KernelTask.validated(
        "remainder",
        Dimensions.One,
        Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(allocation, 0L),
          KernelTask.Argument.Scalar(encoded(if (count == 1) Seq(-3) else Seq(3, 4, 0, 0))),
          KernelTask.Argument.Scalar(encoded(if (count == 1) Seq(1) else Seq(0, 0, 0, 0)))
        )
      ).toOption.get
      assertEquals(executeUnchecked(module, dispatch, memory), Right(()))
      assertEquals(
        memory.bytes,
        encoded(Seq(if (count == 4) 5 else if (operation == "Length") 3 else 4))
      )
    }
}
