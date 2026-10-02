package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.spirv.v1_6.execution.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class VulkanUndefinedIntegerSuite extends munit.FunSuite {
  for {
    width     <- Seq(32, 64)
    count     <- Seq(1, 4)
    operation <- Seq("sdiv", "srem", "smod", "udiv", "umod")
  }
    test(s"Vulkan $operation $width-bit $count-lane undefined results do not abort valid lanes") {
      def ref(n: Int): Operand = operand(OperandKind.IdRef, n)
      val valueType            = ref(if (count == 1) 2 else 3)
      val left                 = ref(if (count == 1) 20 else 12); val right = ref(if (count == 1) 8 else 13)
      val minimum              = 1L << (width - 1)
      def constant(id: Int, value: Long): Instruction = Instruction.Constant(
        ref(2),
        ref(id),
        if (width == 32) operand(OperandKind.LiteralContextDependentNumber, value.toInt)
        else operand(OperandKind.LiteralContextDependentNumber, value.toInt, (value >>> 32).toInt)
      )
      val calculation = operation match {
        case "sdiv" => Instruction.SDiv(valueType, ref(17), left, right)
        case "srem" => Instruction.SRem(valueType, ref(17), left, right)
        case "smod" => Instruction.SMod(valueType, ref(17), left, right)
        case "udiv" => Instruction.UDiv(valueType, ref(17), left, right)
        case _      => Instruction.UMod(valueType, ref(17), left, right)
      }
      val module = Module(
        Header(ISA.version, Word(0), Word(32), Word(0)),
        Vec(
          Instruction.TypeVoid(ref(1)),
          Instruction.TypeInt(
            ref(2),
            operand(OperandKind.LiteralInteger, width),
            operand(OperandKind.LiteralInteger, if (operation.startsWith("s")) 1 else 0)
          ),
          Instruction.TypeVector(ref(3), ref(2), operand(OperandKind.LiteralInteger, 4)),
          Instruction.TypeStruct(ref(4), Vec(valueType)),
          Instruction.TypePointer(ref(5), operand(OperandKind.StorageClass, StorageClass.Uniform), ref(4)),
          Instruction.TypePointer(ref(6), operand(OperandKind.StorageClass, StorageClass.Uniform), valueType),
          Instruction.TypeFunction(ref(7), ref(1), Vec.empty),
          constant(8, 0),
          constant(9, 2),
          constant(10, minimum),
          constant(11, -1L),
          constant(20, 8),
          constant(21, 3),
          constant(22, 10),
          Instruction.ConstantComposite(ref(3), ref(12), Vec(ref(20), ref(10), ref(21), ref(22))),
          Instruction.ConstantComposite(ref(3), ref(13), Vec(ref(8), ref(11), ref(9), ref(21))),
          Instruction.Decorate(ref(4), operand(OperandKind.Decoration, Decoration.BufferBlock)),
          Instruction.MemberDecorate(
            ref(4),
            operand(OperandKind.LiteralInteger, 0),
            operand(OperandKind.Decoration, Decoration.Offset, 0)
          ),
          Instruction.Decorate(ref(14), operand(OperandKind.Decoration, Decoration.Binding, 0)),
          Instruction.Decorate(ref(14), operand(OperandKind.Decoration, Decoration.DescriptorSet, 0)),
          Instruction.Variable(ref(5), ref(14), operand(OperandKind.StorageClass, StorageClass.Uniform), None),
          Instruction.EntryPoint(
            operand(OperandKind.ExecutionModel, ExecutionModel.GLCompute),
            ref(15),
            stringOperand("undefined_integer"),
            Vec(ref(14))
          ),
          Instruction.ExecutionMode(ref(15), operand(OperandKind.ExecutionMode, ExecutionMode.LocalSize, 1, 1, 1)),
          Instruction.Function(ref(1), ref(15), operand(OperandKind.FunctionControl, FunctionControl.None), ref(7)),
          Instruction.Label(ref(19)),
          Instruction.AccessChain(ref(6), ref(16), ref(14), Vec(ref(8))),
          calculation,
          Instruction.Store(ref(16), ref(17), None),
          Instruction.Return(),
          Instruction.FunctionEnd()
        ),
        Endianness.Little
      )
      val region = AllocationId(1L); val memory = ArrayMemory(region, width / 8 * count)
      val task   = KernelTask.validated(
        "undefined_integer",
        Dimensions.One,
        Dimensions.One,
        Vec(KernelTask.Argument.Memory(region, 0L))
      ).toOption.get
      assertEquals(executeUnchecked(module, task, memory), Right(()))
      val expected =
        // Division overflow uses the same wrapped result as kernel execution;
        // zero divisors retain zero while the remaining lanes still execute.
        Vec(
          0L,
          if (operation == "umod" || operation == "sdiv") minimum else 0L,
          1L,
          if (operation.endsWith("div")) 3L else 1L
        ).take(count)
      assertEquals(memory.bytes, expected.flatMap(LittleEndian.toVec(_, (width / 8).unsafe[Int where Between[0, 8]])))
    }

  for {
    width            <- Seq(32, 64)
    count            <- Seq(1, 4)
    operation        <- Seq("uextract", "sextract", "insert")
    (offset, length) <- Seq((-1, 1), (0, -1), (width, 1), (width - 1, 2))
  }
    test(s"Vulkan $operation $width-bit $count-lane undefined range $offset/$length does not abort") {
      def ref(n: Int): Operand = operand(OperandKind.IdRef, n)
      val valueType            = ref(if (count == 1) 2 else 3)
      val left                 = ref(if (count == 1) 20 else 12); val right = ref(if (count == 1) 8 else 13)
      val minimum              = 1L << (width - 1)
      def constant(id: Int, value: Long): Instruction = Instruction.Constant(
        ref(2),
        ref(id),
        if (width == 32) operand(OperandKind.LiteralContextDependentNumber, value.toInt)
        else operand(OperandKind.LiteralContextDependentNumber, value.toInt, (value >>> 32).toInt)
      )
      val calculation = operation match {
        case "uextract" => Instruction.BitFieldUExtract(valueType, ref(17), left, ref(23), ref(24))
        case "sextract" => Instruction.BitFieldSExtract(valueType, ref(17), left, ref(23), ref(24))
        case _          => Instruction.BitFieldInsert(valueType, ref(17), left, right, ref(23), ref(24))
      }
      val module = Module(
        Header(ISA.version, Word(0), Word(32), Word(0)),
        Vec(
          Instruction.TypeVoid(ref(1)),
          Instruction.TypeInt(
            ref(2),
            operand(OperandKind.LiteralInteger, width),
            operand(OperandKind.LiteralInteger, if (operation.startsWith("s")) 1 else 0)
          ),
          Instruction.TypeVector(ref(3), ref(2), operand(OperandKind.LiteralInteger, 4)),
          Instruction.TypeStruct(ref(4), Vec(valueType)),
          Instruction.TypePointer(ref(5), operand(OperandKind.StorageClass, StorageClass.Uniform), ref(4)),
          Instruction.TypePointer(ref(6), operand(OperandKind.StorageClass, StorageClass.Uniform), valueType),
          Instruction.TypeFunction(ref(7), ref(1), Vec.empty),
          constant(8, 0),
          constant(9, 2),
          constant(10, minimum),
          constant(11, -1L),
          constant(20, 8),
          constant(21, 3),
          constant(22, 10),
          constant(23, offset.toLong),
          constant(24, length.toLong),
          Instruction.ConstantComposite(ref(3), ref(12), Vec(ref(20), ref(10), ref(21), ref(22))),
          Instruction.ConstantComposite(ref(3), ref(13), Vec(ref(8), ref(11), ref(9), ref(21))),
          Instruction.Decorate(ref(4), operand(OperandKind.Decoration, Decoration.BufferBlock)),
          Instruction.MemberDecorate(
            ref(4),
            operand(OperandKind.LiteralInteger, 0),
            operand(OperandKind.Decoration, Decoration.Offset, 0)
          ),
          Instruction.Decorate(ref(14), operand(OperandKind.Decoration, Decoration.Binding, 0)),
          Instruction.Decorate(ref(14), operand(OperandKind.Decoration, Decoration.DescriptorSet, 0)),
          Instruction.Variable(ref(5), ref(14), operand(OperandKind.StorageClass, StorageClass.Uniform), None),
          Instruction.EntryPoint(
            operand(OperandKind.ExecutionModel, ExecutionModel.GLCompute),
            ref(15),
            stringOperand("undefined_integer"),
            Vec(ref(14))
          ),
          Instruction.ExecutionMode(ref(15), operand(OperandKind.ExecutionMode, ExecutionMode.LocalSize, 1, 1, 1)),
          Instruction.Function(ref(1), ref(15), operand(OperandKind.FunctionControl, FunctionControl.None), ref(7)),
          Instruction.Label(ref(19)),
          Instruction.AccessChain(ref(6), ref(16), ref(14), Vec(ref(8))),
          calculation,
          Instruction.Store(ref(16), ref(17), None),
          Instruction.Return(),
          Instruction.FunctionEnd()
        ),
        Endianness.Little
      )
      val region = AllocationId(1L); val memory = ArrayMemory(region, width / 8 * count)
      val task   = KernelTask.validated(
        "undefined_integer",
        Dimensions.One,
        Dimensions.One,
        Vec(KernelTask.Argument.Memory(region, 0L))
      ).toOption.get
      assertEquals(executeUnchecked(module, task, memory), Right(()))
      val expected =
        (if (operation == "insert") Vec(8L, minimum, 3L, 10L) else Vec.fill(4)(0L)).take(count)
      assertEquals(memory.bytes, expected.flatMap(LittleEndian.toVec(_, (width / 8).unsafe[Int where Between[0, 8]])))
    }
}
