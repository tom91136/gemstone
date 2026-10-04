package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.spirv.v1_6.execution.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class VulkanConversionSuite extends munit.FunSuite {
  for {
    width <- Seq[Int in (32, 64)](32, 64)
    rawWidth  = width.raw
    byteWidth = width.widen[Int in (8, 16, 32, 64)].toMemoryValueByteWidth
    signed <- Seq(0, 1)
  }
    test(s"Vulkan $rawWidth-bit floating conversion chooses saturated $signed integer endpoints") {
      def ref(n: Int): Operand = operand(OperandKind.IdRef, n)
      val module               = Module(
        Header(ISA.version, Word(0), Word(32), Word(0)),
        Vec(
          Instruction.TypeVoid(ref(1)),
          Instruction.TypeFloat(ref(2), operand(OperandKind.LiteralInteger, rawWidth), None),
          Instruction.TypeInt(
            ref(3),
            operand(OperandKind.LiteralInteger, 32),
            operand(OperandKind.LiteralInteger, signed)
          ),
          Instruction.TypeVector(ref(4), ref(2), operand(OperandKind.LiteralInteger, 4)),
          Instruction.TypeVector(ref(5), ref(3), operand(OperandKind.LiteralInteger, 4)),
          Instruction.TypeStruct(ref(6), Vec(ref(4))),
          Instruction.TypeStruct(ref(7), Vec(ref(5))),
          Instruction.TypePointer(ref(8), operand(OperandKind.StorageClass, StorageClass.Uniform), ref(6)),
          Instruction.TypePointer(ref(9), operand(OperandKind.StorageClass, StorageClass.Uniform), ref(7)),
          Instruction.TypePointer(ref(10), operand(OperandKind.StorageClass, StorageClass.Uniform), ref(4)),
          Instruction.TypePointer(ref(11), operand(OperandKind.StorageClass, StorageClass.Uniform), ref(5)),
          Instruction.TypeFunction(ref(12), ref(1), Vec.empty),
          Instruction.Constant(ref(3), ref(13), operand(OperandKind.LiteralContextDependentNumber, 0)),
          Instruction.Decorate(ref(6), operand(OperandKind.Decoration, Decoration.BufferBlock)),
          Instruction.Decorate(ref(7), operand(OperandKind.Decoration, Decoration.BufferBlock)),
          Instruction.MemberDecorate(
            ref(6),
            operand(OperandKind.LiteralInteger, 0),
            operand(OperandKind.Decoration, Decoration.Offset, 0)
          ),
          Instruction.MemberDecorate(
            ref(7),
            operand(OperandKind.LiteralInteger, 0),
            operand(OperandKind.Decoration, Decoration.Offset, 0)
          ),
          Instruction.Decorate(ref(14), operand(OperandKind.Decoration, Decoration.Binding, 0)),
          Instruction.Decorate(ref(14), operand(OperandKind.Decoration, Decoration.DescriptorSet, 0)),
          Instruction.Decorate(ref(15), operand(OperandKind.Decoration, Decoration.Binding, 1)),
          Instruction.Decorate(ref(15), operand(OperandKind.Decoration, Decoration.DescriptorSet, 0)),
          Instruction.Variable(ref(8), ref(14), operand(OperandKind.StorageClass, StorageClass.Uniform), None),
          Instruction.Variable(ref(9), ref(15), operand(OperandKind.StorageClass, StorageClass.Uniform), None),
          Instruction.EntryPoint(
            operand(OperandKind.ExecutionModel, ExecutionModel.GLCompute),
            ref(16),
            stringOperand("convert"),
            Vec(ref(14), ref(15))
          ),
          Instruction.ExecutionMode(ref(16), operand(OperandKind.ExecutionMode, ExecutionMode.LocalSize, 1, 1, 1)),
          Instruction.Function(ref(1), ref(16), operand(OperandKind.FunctionControl, FunctionControl.None), ref(12)),
          Instruction.Label(ref(17)),
          Instruction.AccessChain(ref(10), ref(18), ref(14), Vec(ref(13))),
          Instruction.AccessChain(ref(11), ref(19), ref(15), Vec(ref(13))),
          Instruction.Load(ref(4), ref(20), ref(18), None),
          if (signed == 1) Instruction.ConvertFToS(ref(5), ref(21), ref(20))
          else Instruction.ConvertFToU(ref(5), ref(21), ref(20)),
          Instruction.Store(ref(19), ref(21), None),
          Instruction.Return(),
          Instruction.FunctionEnd()
        ),
        Endianness.Little
      )
      val region  = AllocationId(1L); val inputSize = byteWidth.widen[Int > 0] * (4: Int > 0)
      val memory  = ArrayMemory(region, (inputSize + 16).raw)
      val inputs  = Vec(Double.NegativeInfinity, -0.75, Double.PositiveInfinity, Double.NaN)
      val encoded = inputs.flatMap { value =>
        val bits = if (rawWidth == 32) java.lang.Float.floatToRawIntBits(value.toFloat).toLong
        else java.lang.Double.doubleToRawLongBits(value)
        LittleEndian.toVec(bits, byteWidth.widen[Int in 0 ~ 8])
      }
      assertEquals(memory.write(region, 0L, encoded), Right(()))
      val task = KernelTask.validated(
        "convert",
        Dimensions.One,
        Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(region, 0L),
          KernelTask.Argument.Memory(
            region,
            inputSize.toLong.widen[Long >= 0]
          )
        )
      ).toOption.get
      assertEquals(executeUnchecked(module, task, memory), Right(()))
      val expected = Vec(if (signed == 1) 0x80000000L else 0L, 0L, if (signed == 1) 0x7fffffffL else 0xffffffffL, 0L)
      assertEquals(memory.bytes.drop(inputSize.raw), expected.flatMap(LittleEndian.toVec(_, 4)))
    }
}
