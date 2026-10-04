package net.kurobako.gemstone.isa.spirv.v1_6

import execution.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*

class TypeLayoutSuite extends munit.FunSuite {
  for (base <- Seq(0, 1000001))
    test(s"scalar leaves preserve nested aggregates, empty structures and errors with type base $base") {
      def ref(offset: Int): Operand = operand(OperandKind.IdRef, base + offset)
      val module                    = Module(
        Header(ISA.version, Word(0), Word(base + 10), Word(0)),
        Vec(
          Instruction.TypeInt(ref(1), operand(OperandKind.LiteralInteger, 32), operand(OperandKind.LiteralInteger, 0)),
          Instruction.TypeVector(ref(2), ref(1), operand(OperandKind.LiteralInteger, 3)),
          Instruction.Constant(ref(1), ref(3), operand(OperandKind.LiteralContextDependentNumber, 2)),
          Instruction.TypeArray(ref(4), ref(2), ref(3)),
          Instruction.TypePointer(ref(5), operand(OperandKind.StorageClass, StorageClass.CrossWorkgroup), ref(4)),
          Instruction.TypeStruct(ref(6), Vec(ref(2), ref(4), ref(5))),
          Instruction.TypeStruct(ref(7), Vec.empty),
          Instruction.TypeVoid(ref(8))
        ),
        Endianness.Little
      )
      val layout = loadUnchecked(module).fold(error => fail(error.message), identity).layout
      for (_ <- 0 until 3) {
        assertEquals(layout.scalarLeafCount(base + 6), Right[Fault, Int >= 0](10))
        assertEquals(layout.scalarLeafCount(base + 7), Right[Fault, Int >= 0](0))
        assert(layout.scalarLeafCount(base + 8).isLeft)
        assert(layout.scalarLeafCount(base + 9).isLeft)
        assert(layout.scalarLeafCount(-1).isLeft)
      }
    }
  for (index <- Seq(1, 2, -1))
    test(s"nested composite extraction preserves scalar and vector values and rejects invalid index $index") {
      def ref(id: Int): Operand = operand(OperandKind.IdRef, id)
      val module                = Module(
        Header(ISA.version, Word(0), Word(18), Word(0)),
        Vec(
          Instruction.TypeInt(ref(1), operand(OperandKind.LiteralInteger, 32), operand(OperandKind.LiteralInteger, 0)),
          Instruction.TypeVector(ref(2), ref(1), operand(OperandKind.LiteralInteger, 2)),
          Instruction.TypeStruct(ref(3), Vec(ref(1), ref(2))),
          Instruction.TypePointer(ref(4), operand(OperandKind.StorageClass, StorageClass.CrossWorkgroup), ref(1)),
          Instruction.TypeVoid(ref(6)),
          Instruction.TypeFunction(ref(5), ref(6), Vec(ref(4))),
          Instruction.Constant(ref(1), ref(7), operand(OperandKind.LiteralContextDependentNumber, 10)),
          Instruction.Constant(ref(1), ref(8), operand(OperandKind.LiteralContextDependentNumber, 20)),
          Instruction.Constant(ref(1), ref(9), operand(OperandKind.LiteralContextDependentNumber, 30)),
          Instruction.ConstantComposite(ref(2), ref(10), Vec(ref(8), ref(9))),
          Instruction.ConstantComposite(ref(3), ref(11), Vec(ref(7), ref(10))),
          Instruction.EntryPoint(
            operand(OperandKind.ExecutionModel, ExecutionModel.Kernel),
            ref(12),
            stringOperand("extract"),
            Vec.empty
          ),
          Instruction.Function(ref(6), ref(12), operand(OperandKind.FunctionControl, FunctionControl.None), ref(5)),
          Instruction.FunctionParameter(ref(4), ref(13)),
          Instruction.Label(ref(14)),
          Instruction.CompositeExtract(ref(2), ref(15), ref(11), Vec(operand(OperandKind.LiteralInteger, 1))),
          Instruction.CompositeExtract(ref(1), ref(16), ref(15), Vec(operand(OperandKind.LiteralInteger, index))),
          Instruction.IAdd(ref(1), ref(17), ref(16), ref(7)),
          Instruction.Store(ref(13), ref(17), None),
          Instruction.Return(),
          Instruction.FunctionEnd()
        ),
        Endianness.Little
      )
      val allocation = AllocationId(1)
      val memory     = ArrayMemory(allocation, 4)
      val task       = KernelTask.validated(
        "extract",
        LaunchGeometry.Dimensions.One,
        LaunchGeometry.Dimensions.One,
        Vec(KernelTask.Argument.Memory(allocation, 0L))
      ).fold(fail(_), identity)
      val result = executeUnchecked(module, task, memory)
      if (index == 1) {
        assertEquals(result, Right(()))
        assertEquals(memory.bytes, LittleEndian.toVec(40L, 4))
      } else {
        assert(result.left.toOption.exists(_.message.contains("OpComposite index")))
        assertEquals(memory.bytes, LittleEndian.toVec(0L, 4))
      }
    }
}
