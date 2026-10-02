package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.spirv.v1_6.execution.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*

class FragmentDiscardSuite extends munit.FunSuite {
  private def ref(n: Int)  = operand(OperandKind.IdRef, n)
  private def dest(n: Int) = operand(OperandKind.IdResult, n)
  private def lit(n: Int)  = operand(OperandKind.LiteralInteger, n)
  private def discardBlock(load: Int, condition: Int, merge: Int, killed: Int): Vec[Instruction] = Vec(
    Instruction.Load(ref(2), dest(load), ref(6), None),
    Instruction.FOrdLessThan(ref(12), dest(condition), ref(load), ref(35)),
    Instruction.SelectionMerge(ref(merge), operand(OperandKind.SelectionControl, SelectionControl.None)),
    Instruction.BranchConditional(ref(condition), ref(killed), ref(merge), Vec.empty),
    Instruction.Label(dest(killed)),
    Instruction.Kill(),
    Instruction.Label(dest(merge))
  )
  private def module(nested: Boolean, derivative: Boolean): Module = Module(
    Header(ISA.version, Word(0), Word(70), Word(0)),
    Vec(
      Instruction.Capability(operand(OperandKind.Capability, Capability.Shader)),
      Instruction.MemoryModel(
        operand(OperandKind.AddressingModel, AddressingModel.Logical),
        operand(OperandKind.MemoryModel, MemoryModel.GLSL450)
      ),
      Instruction.EntryPoint(
        operand(OperandKind.ExecutionModel, ExecutionModel.Fragment),
        ref(20),
        stringOperand("main"),
        Vec(ref(6), ref(7), ref(13), ref(16), ref(44))
      ),
      Instruction.ExecutionMode(ref(20), operand(OperandKind.ExecutionMode, ExecutionMode.OriginUpperLeft)),
      Instruction.Decorate(ref(6), operand(OperandKind.Decoration, Decoration.Location, 0)),
      Instruction.Decorate(ref(7), operand(OperandKind.Decoration, Decoration.Location, 0)),
      Instruction.Decorate(ref(16), operand(OperandKind.Decoration, Decoration.BuiltIn, 23)),
      Instruction.Decorate(ref(44), operand(OperandKind.Decoration, Decoration.BuiltIn, 20)),
      Instruction.Decorate(ref(13), operand(OperandKind.Decoration, Decoration.Binding, 0)),
      Instruction.Decorate(ref(13), operand(OperandKind.Decoration, Decoration.DescriptorSet, 0)),
      Instruction.Decorate(ref(9), operand(OperandKind.Decoration, Decoration.Block)),
      Instruction.MemberDecorate(ref(9), lit(0), operand(OperandKind.Decoration, Decoration.Offset, 0)),
      Instruction.MemberDecorate(ref(9), lit(1), operand(OperandKind.Decoration, Decoration.Offset, 4)),
      Instruction.TypeVoid(dest(1)),
      Instruction.TypeFloat(dest(2), lit(32), None),
      Instruction.TypePointer(dest(3), operand(OperandKind.StorageClass, StorageClass.Input), ref(2)),
      Instruction.TypePointer(dest(4), operand(OperandKind.StorageClass, StorageClass.Output), ref(2)),
      Instruction.TypeFunction(dest(5), ref(1), Vec.empty),
      Instruction.TypeInt(dest(8), lit(32), lit(0)),
      Instruction.TypeStruct(dest(9), Vec(ref(8), ref(8))),
      Instruction.TypePointer(dest(10), operand(OperandKind.StorageClass, StorageClass.StorageBuffer), ref(9)),
      Instruction.TypePointer(dest(11), operand(OperandKind.StorageClass, StorageClass.StorageBuffer), ref(8)),
      Instruction.TypeBool(dest(12)),
      Instruction.TypePointer(dest(14), operand(OperandKind.StorageClass, StorageClass.Input), ref(12)),
      Instruction.Constant(ref(8), dest(15), operand(OperandKind.LiteralContextDependentNumber, 0)),
      Instruction.Constant(ref(8), dest(17), operand(OperandKind.LiteralContextDependentNumber, 1)),
      Instruction.Constant(ref(2), dest(35), operand(OperandKind.LiteralContextDependentNumber, 0x40400000)),
      Instruction.Constant(ref(8), dest(43), operand(OperandKind.LiteralContextDependentNumber, -1)),
      Instruction.TypeArray(dest(40), ref(8), ref(17)),
      Instruction.TypePointer(dest(41), operand(OperandKind.StorageClass, StorageClass.Output), ref(40)),
      Instruction.ConstantComposite(ref(40), dest(42), Vec(ref(43))),
      Instruction.Variable(ref(3), dest(6), operand(OperandKind.StorageClass, StorageClass.Input), None),
      Instruction.Variable(ref(4), dest(7), operand(OperandKind.StorageClass, StorageClass.Output), None),
      Instruction.Variable(ref(10), dest(13), operand(OperandKind.StorageClass, StorageClass.StorageBuffer), None),
      Instruction.Variable(ref(14), dest(16), operand(OperandKind.StorageClass, StorageClass.Input), None),
      Instruction.Variable(ref(41), dest(44), operand(OperandKind.StorageClass, StorageClass.Output), Some(ref(42))),
      Instruction.Function(ref(1), dest(20), operand(OperandKind.FunctionControl, FunctionControl.None), ref(5)),
      Instruction.Label(dest(21)),
      Instruction.AccessChain(ref(11), dest(26), ref(13), Vec(ref(15))),
      Instruction.AtomicIAdd(ref(8), dest(27), ref(26), ref(17), ref(15), ref(17))
    ) ++
      (if (nested) Vec(Instruction.FunctionCall(ref(1), dest(30), ref(50), Vec.empty))
       else discardBlock(22, 23, 24, 25)) ++ Vec(
        Instruction.Load(ref(2), dest(31), ref(6), None)
      ) ++ (if (derivative) Vec(Instruction.DPdx(ref(2), dest(32), ref(31))) else Vec.empty[Instruction]) ++ Vec(
        Instruction.Store(ref(7), ref(if (derivative) 32 else 31), None),
        Instruction.AccessChain(ref(11), dest(28), ref(13), Vec(ref(17))),
        Instruction.AtomicIAdd(ref(8), dest(29), ref(28), ref(17), ref(15), ref(17)),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ) ++
      (if (nested) Vec(
         Instruction.Function(ref(1), dest(50), operand(OperandKind.FunctionControl, FunctionControl.None), ref(5)),
         Instruction.Label(dest(51))
       ) ++ discardBlock(55, 56, 57, 58) ++ Vec(Instruction.Return(), Instruction.FunctionEnd())
       else Vec.empty[Instruction]),
    Endianness.Little
  )

  for {
    nested     <- List(false, true)
    derivative <- List(false, true)
    helpers    <- List(false, true)
    inputs     <- List(Seq(1f, 2f, 3f, 5f), Seq.fill(4)(1f), Seq.fill(4)(5f))
  } test(
    s"discard nested=$nested derivative=$derivative helpers=$helpers inputs=$inputs preserves earlier stores and stops later work"
  ) {

    val program = LoadedProgram.load(
      module(nested, derivative),
      Validation.Environment.VulkanGraphics
    ).fold(e => fail(e.message), identity)
    val region = AllocationId(708L)
    val memory = ArrayMemory(region, 128)
    val values = inputs ++ inputs.reverse
    for ((v, i) <- values.zipWithIndex)
      assertEquals(
        memory.write(
          region,
          (16L + i * 4).unsafe[Long >= 0],
          LittleEndian.toVec(java.lang.Float.floatToRawIntBits(v).toLong, 4)
        ),
        Right(())
      )
    val flags = Seq.tabulate(8)(i => if (helpers && i % 2 == 1) 1.toByte else 0.toByte)
    assertEquals(memory.write(region, 80L, Vec.from(flags)), Right(()))
    val task = KernelTask.validated(
      "main",
      testDimensions(if (derivative) 4 else 1),
      testDimensions(if (derivative) 2 else 8),
      Vec(
        KernelTask.Argument.Memory(
          region,
          0L,
          Some(8L)
        ),
        KernelTask.Argument.Memory(
          region,
          16L,
          Some(32L)
        ),
        KernelTask.Argument.Memory(
          region,
          48L,
          Some(32L)
        ),
        KernelTask.Argument.Memory(
          region,
          80L,
          Some(8L)
        ),
        KernelTask.Argument.Memory(
          region,
          88L,
          Some(32L)
        )
      )
    ).fold(fail(_), identity)
    val _ = program.execute(task, memory, TaskControl()).fold(e => fail(e.message), identity)
    assertEquals(LittleEndian.decode(memory.bytes, 0, 4), flags.count(_ == 0).toLong)
    assertEquals(
      LittleEndian.decode(memory.bytes, 4, 4),
      values.zip(flags).count((v, h) => v >= 3 && h == 0).toLong
    )
    for ((v, i) <- values.zipWithIndex) {
      assertEquals(LittleEndian.decode(memory.bytes, 88 + i * 4, 4), if (v < 3) 0L else 0xffffffffL)
      if (v < 3) assertEquals(LittleEndian.decode(memory.bytes, 48 + i * 4, 4), 0L)
    }
  }
}
