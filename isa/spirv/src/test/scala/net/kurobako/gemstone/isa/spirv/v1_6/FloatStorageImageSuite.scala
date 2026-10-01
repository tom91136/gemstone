package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.spirv.v1_6.execution.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*

class FloatStorageImageSuite extends munit.FunSuite {
  for {
    model                <- List(0, 4, 5)
    (format, components) <- List(3 -> 1, 6 -> 2, 1 -> 4)
    outside              <- List(false, true)
  } test(s"stage $model float image format $format preserves values and bounds (outside=$outside)") {
    def ref(n: Int)                         = operand("IdRef", n)
    def dest(n: Int)                        = operand("IdResult", n)
    def lit(n: Int)                         = operand("LiteralInteger", n)
    def floatConstant(n: Int, value: Float) = Instruction.Constant(
      ref(3),
      dest(n),
      operand("LiteralContextDependentNumber", java.lang.Float.floatToRawIntBits(value))
    )
    val module = Module(
      Header(ISA.version, Word(0), Word(28), Word(0)),
      Vec(
        Instruction.Capability(operand("Capability", Capability.Shader)),
        Instruction.MemoryModel(
          operand("AddressingModel", AddressingModel.Logical),
          operand("MemoryModel", MemoryModel.GLSL450)
        ),
        Instruction.EntryPoint(operand("ExecutionModel", model), ref(21), stringOperand("main"), Vec(ref(12), ref(13)))
      ) ++
        (model match {
          case 5 => Vec(Instruction.ExecutionMode(ref(21), operand("ExecutionMode", ExecutionMode.LocalSize, 1, 1, 1)))
          case 4 => Vec(Instruction.ExecutionMode(ref(21), operand("ExecutionMode", ExecutionMode.OriginUpperLeft)))
          case _ => Vec.empty[Instruction]
        }) ++ Vec(
          Instruction.Decorate(ref(12), operand("Decoration", Decoration.Binding, 0)),
          Instruction.Decorate(ref(12), operand("Decoration", Decoration.DescriptorSet, 0)),
          Instruction.Decorate(ref(13), operand("Decoration", Decoration.Binding, 1)),
          Instruction.Decorate(ref(13), operand("Decoration", Decoration.DescriptorSet, 0)),
          Instruction.Decorate(ref(8), operand("Decoration", Decoration.Block)),
          Instruction.MemberDecorate(ref(8), lit(0), operand("Decoration", Decoration.Offset, 0)),
          Instruction.TypeVoid(dest(1)),
          Instruction.TypeInt(dest(2), lit(32), lit(0)),
          Instruction.TypeFloat(dest(3), lit(32), None),
          Instruction.TypeVector(dest(4), ref(2), lit(2)),
          Instruction.TypeVector(dest(5), ref(3), lit(4)),
          Instruction.TypeImage(
            dest(6),
            ref(3),
            operand("Dim", Dim.Value2D),
            lit(0),
            lit(0),
            lit(0),
            lit(2),
            operand("ImageFormat", format),
            None
          ),
          Instruction.TypePointer(dest(7), operand("StorageClass", StorageClass.UniformConstant), ref(6)),
          Instruction.TypeStruct(dest(8), Vec(ref(5))),
          Instruction.TypePointer(dest(9), operand("StorageClass", StorageClass.StorageBuffer), ref(8)),
          Instruction.TypePointer(dest(10), operand("StorageClass", StorageClass.StorageBuffer), ref(5)),
          Instruction.TypeFunction(dest(11), ref(1), Vec.empty),
          Instruction.Variable(ref(7), dest(12), operand("StorageClass", StorageClass.UniformConstant), None),
          Instruction.Variable(ref(9), dest(13), operand("StorageClass", StorageClass.StorageBuffer), None),
          Instruction.Constant(ref(2), dest(14), operand("LiteralContextDependentNumber", 0)),
          Instruction.Constant(ref(2), dest(26), operand("LiteralContextDependentNumber", if (outside) 1 else 0)),
          Instruction.ConstantComposite(ref(4), dest(15), Vec(ref(26), ref(14))),
          floatConstant(16, -1.0f),
          floatConstant(17, 0.25f),
          floatConstant(18, 1.0f),
          floatConstant(19, 2.0f),
          Instruction.ConstantComposite(ref(5), dest(20), Vec(ref(16), ref(17), ref(18), ref(19))),
          Instruction.Function(ref(1), dest(21), operand("FunctionControl", FunctionControl.None), ref(11)),
          Instruction.Label(dest(22)),
          Instruction.Load(ref(6), dest(23), ref(12), None),
          Instruction.ImageWrite(ref(23), ref(15), ref(20), None),
          Instruction.ImageRead(ref(5), dest(24), ref(23), ref(15), None),
          Instruction.AccessChain(ref(10), dest(25), ref(13), Vec(ref(14))),
          Instruction.Store(ref(25), ref(24), None),
          Instruction.Return(),
          Instruction.FunctionEnd()
        ),
      Endianness.Little
    )

    val program = LoadedProgram.load(
      module,
      if (model == 5) Validation.Environment.VulkanCompute else Validation.Environment.VulkanGraphics
    ).fold(error => fail(error.message), identity)
    val region     = AllocationId(507L)
    val memory     = ArrayMemory(region, 64)
    val imageBytes = 16 + components * 4
    for (
      (
        offset,
        word
      ) <- Seq(0L -> 0xfeedL, 8L -> 1L, 12L -> 1L, 16L -> 1L, 20L -> format.toLong, (8L + imageBytes) -> 0xbeefL)
    )
      assertEquals(
        memory.write(region, offset.unsafe[Long >= 0], LittleEndian.toVec(word, 4)),
        Right(())
      )
    val task = KernelTask.validated(
      "main",
      LaunchGeometry.Dimensions.One,
      LaunchGeometry.Dimensions.One,
      Vec(
        KernelTask.Argument.Memory(
          region,
          8L,
          Some(imageBytes.toLong.unsafe[Long > 0])
        ),
        KernelTask.Argument.Memory(
          region,
          48L,
          Some(16L)
        )
      )
    ).fold(fail(_), identity)
    val _       = program.execute(task, memory, TaskControl()).fold(error => fail(error.message), identity)
    val written = Seq(-1.0f, 0.25f, 1.0f, 2.0f)
    for (lane <- 0 until components)
      assertEquals(
        LittleEndian.decode(memory.bytes, 24 + lane * 4, 4),
        if (outside) 0L else java.lang.Float.floatToRawIntBits(written(lane)).toLong & 0xffffffffL
      )
    assertEquals(LittleEndian.decode(memory.bytes, 0, 4), 0xfeedL)
    assertEquals(LittleEndian.decode(memory.bytes, 8 + imageBytes, 4), 0xbeefL)
    for (lane <- 0 until 4) {
      val expected = if (outside) 0.0f else if (lane < components) written(lane) else if (lane == 3) 1.0f else 0.0f
      assertEquals(
        LittleEndian.decode(memory.bytes, 48 + lane * 4, 4),
        java.lang.Float.floatToRawIntBits(expected).toLong & 0xffffffffL
      )
    }
  }
}
