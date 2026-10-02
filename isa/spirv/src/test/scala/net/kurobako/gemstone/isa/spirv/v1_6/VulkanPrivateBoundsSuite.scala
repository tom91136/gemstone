package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.spirv.v1_6.execution.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class VulkanPrivateBoundsSuite extends munit.FunSuite {
  for {
    storage <- Seq(6, 7)
    index   <- Seq(-1, 4)
    shader  <- Seq(false, true)
  } test(s"private bounds storage=$storage index=$index shader=$shader") {
    def ref(n: Int): Operand                                   = operand(OperandKind.IdRef, n)
    def literal(n: Int): Operand                               = operand(OperandKind.LiteralInteger, n)
    def constant(id: Int, value: Int, t: Int = 2): Instruction =
      Instruction.Constant(ref(t), ref(id), operand(OperandKind.LiteralContextDependentNumber, value))
    val local         = Instruction.Variable(ref(8), ref(31), operand(OperandKind.StorageClass, storage), None)
    val outputStorage = if (shader) 2 else 5
    val module        = Module(
      Header(ISA.version, Word(0), Word(48), Word(0)),
      Vec(
        Instruction.TypeVoid(ref(1)),
        Instruction.TypeInt(ref(2), literal(32), literal(0)),
        Instruction.TypeInt(ref(3), literal(32), literal(1)),
        Instruction.TypeVector(ref(4), ref(2), literal(4)),
        Instruction.TypeStruct(ref(5), Vec(ref(2), ref(2))),
        Instruction.TypePointer(ref(6), operand(OperandKind.StorageClass, outputStorage), ref(5)),
        Instruction.TypePointer(ref(7), operand(OperandKind.StorageClass, outputStorage), ref(2)),
        Instruction.TypePointer(ref(8), operand(OperandKind.StorageClass, storage), ref(4)),
        Instruction.TypePointer(ref(9), operand(OperandKind.StorageClass, storage), ref(2)),
        Instruction.TypeFunction(ref(10), ref(1), if (shader) Vec.empty else Vec(ref(6))),
        constant(11, 0, 3),
        constant(12, 1, 3),
        constant(14, index, 3),
        constant(15, 99),
        constant(16, 11),
        constant(17, 22),
        constant(18, 33),
        constant(19, 44),
        Instruction.ConstantComposite(ref(4), ref(20), Vec(ref(16), ref(17), ref(18), ref(19))),
        Instruction.Decorate(ref(5), operand(OperandKind.Decoration, Decoration.BufferBlock)),
        Instruction.MemberDecorate(ref(5), literal(0), operand(OperandKind.Decoration, Decoration.Offset, 0)),
        Instruction.MemberDecorate(ref(5), literal(1), operand(OperandKind.Decoration, Decoration.Offset, 4))
      ) ++
        (if (shader) Vec(
           Instruction.Decorate(ref(30), operand(OperandKind.Decoration, Decoration.Binding, 0)),
           Instruction.Decorate(ref(30), operand(OperandKind.Decoration, Decoration.DescriptorSet, 0)),
           Instruction.Variable(ref(6), ref(30), operand(OperandKind.StorageClass, StorageClass.Uniform), None)
         )
         else Vec.empty[Instruction]) ++ (if (storage == 6) Vec(local) else Vec.empty[Instruction]) ++ Vec(
          Instruction.EntryPoint(
            operand(OperandKind.ExecutionModel, if (shader) 5 else 6),
            ref(40),
            stringOperand("bounds"),
            if (shader) if (storage == 6) Vec(ref(30), ref(31)) else Vec(ref(30)) else Vec.empty
          ),
          Instruction.ExecutionMode(ref(40), operand(OperandKind.ExecutionMode, ExecutionMode.LocalSize, 1, 1, 1)),
          Instruction.Function(ref(1), ref(40), operand(OperandKind.FunctionControl, FunctionControl.None), ref(10))
        ) ++ (if (shader) Vec.empty[Instruction] else Vec(Instruction.FunctionParameter(ref(6), ref(30)))) ++ Vec(
          Instruction.Label(ref(41))
        ) ++ (if (storage == 7) Vec(local) else Vec.empty[Instruction]) ++ Vec(
          Instruction.Store(ref(31), ref(20), None),
          Instruction.AccessChain(ref(9), ref(32), ref(31), Vec(ref(14))),
          Instruction.Store(ref(32), ref(15), None),
          Instruction.Load(ref(2), ref(33), ref(32), None),
          Instruction.AccessChain(ref(9), ref(34), ref(31), Vec(ref(12))),
          Instruction.Load(ref(2), ref(35), ref(34), None),
          Instruction.AccessChain(ref(7), ref(36), ref(30), Vec(ref(11))),
          Instruction.Store(ref(36), ref(33), None),
          Instruction.AccessChain(ref(7), ref(37), ref(30), Vec(ref(12))),
          Instruction.Store(ref(37), ref(35), None),
          Instruction.Return(),
          Instruction.FunctionEnd()
        ),
      Endianness.Little
    )
    val region = AllocationId(1L)
    val memory = ArrayMemory(region, 8)
    val task   = KernelTask.validated(
      "bounds",
      Dimensions.One,
      Dimensions.One,
      Vec(KernelTask.Argument.Memory(region, 0L))
    ).toOption.get
    if (shader) {
      assertEquals(executeUnchecked(module, task, memory), Right(()))
      assertEquals(
        memory.bytes,
        LittleEndian.toVec(0L, 4) ++ LittleEndian.toVec(22L, 4)
      )
    } else {
      assert(executeUnchecked(module, task, memory).isLeft)
      assertEquals(memory.bytes, Vec.fill(8)(0.toByte))
    }
  }
}
