package net.kurobako.gemstone.isa.spirv.v1_6

import execution.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.spirv.v1_6 as spv
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*

class VulkanComputeSuite extends munit.FunSuite {
  private def bytes(module: Module): Vec[Byte] =
    ISA.encode(module).fold(error => fail(error.message), identity)

  test("GLCompute descriptor bindings are scoped to each entry point interface") {
    val first  = operand(OperandKind.IdResult, 10)
    val second = operand(OperandKind.IdResult, 20)
    val left   = operand(OperandKind.IdResult, 30)
    val right  = operand(OperandKind.IdResult, 31)
    val module = Module(
      Header(ISA.version, Word(0), Word(64), Word(0)),
      Vec(
        Instruction.EntryPoint(
          operand(OperandKind.ExecutionModel, spv.ExecutionModel.GLCompute),
          first,
          stringOperand("first"),
          Vec(left)
        ),
        Instruction.EntryPoint(
          operand(OperandKind.ExecutionModel, spv.ExecutionModel.GLCompute),
          second,
          stringOperand("second"),
          Vec(right)
        ),
        Instruction.Decorate(left, operand(OperandKind.Decoration, spv.Decoration.Binding, 0)),
        Instruction.Decorate(left, operand(OperandKind.Decoration, spv.Decoration.DescriptorSet, 0)),
        Instruction.Decorate(right, operand(OperandKind.Decoration, spv.Decoration.Binding, 1)),
        Instruction.Decorate(right, operand(OperandKind.Decoration, spv.Decoration.DescriptorSet, 0)),
        Instruction.Function(
          operand(OperandKind.IdResultType, 1),
          first,
          operand(OperandKind.FunctionControl, spv.FunctionControl.None),
          operand(OperandKind.IdRef, 2)
        ),
        Instruction.FunctionEnd(),
        Instruction.Function(
          operand(OperandKind.IdResultType, 1),
          second,
          operand(OperandKind.FunctionControl, spv.FunctionControl.None),
          operand(OperandKind.IdRef, 2)
        ),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val program = VMCompiler.compileUnchecked(module).fold(error => fail(error.message), identity)
    val loaded  = VMProgramRuntime(program)
    assertEquals(VMShaderInterface.argumentVariables(loaded, "first"), Vec(30))
    assertEquals(VMShaderInterface.argumentVariables(loaded, "second"), Vec(31))
  }

  test("GLCompute specialization constant operations decode and load") {
    val module = ISA.decode(bytes(vulkan.specialization)).fold(error => fail(error.message), identity)
    assert(module.instructions.exists {
      case Instruction.SpecConstantOp(_, _, _, _) => true
      case _                                      => false
    })

    val loaded =
      LoadedProgram.load(module, Validation.Environment.VulkanCompute).fold(error => fail(error.message), identity)
    assert(loaded.kernelNames.contains("main"))
  }

  test("GLCompute entry point exposes storage and uniform descriptor bindings") {
    val image  = bytes(vulkan.fma)
    val module = ISA.decode(image).fold(error => fail(error.message), identity)
    val loaded =
      LoadedProgram.load(module, Validation.Environment.VulkanCompute).fold(error => fail(error.message), identity)
    val metadata = loaded.kernel("main").fold(error => fail(error.message), identity)
    assertEquals(metadata.explicitParameterCount.raw, 2)
    assertEquals(
      metadata.parameters.map(_.kind),
      Vec(KernelMetadata.ParameterKind.Allocation, KernelMetadata.ParameterKind.Allocation)
    )
  }

  test("GLCompute FMA reads uniform data and writes a storage buffer") {
    val image  = bytes(vulkan.fma)
    val module = ISA.decode(image).fold(error => fail(error.message), identity)
    val loaded =
      LoadedProgram.load(module, Validation.Environment.VulkanCompute).fold(error => fail(error.message), identity)
    val allocation = AllocationId(501L)
    val memory     = ArrayMemory(allocation, 16)
    for ((offset, bits) <- Vec(4L -> 0x40000000L, 8L -> 0x40400000L, 12L -> 0x40800000L))
      assertEquals(
        memory.write(allocation, offset.unsafe[Long >= 0], LittleEndian.toVec(bits, 4)),
        Right(())
      )
    val task = KernelTask.validated(
      "main",
      LaunchGeometry.Dimensions.One,
      LaunchGeometry.Dimensions.One,
      Vec(
        KernelTask.Argument.Memory(allocation, 0L),
        KernelTask.Argument.Memory(allocation, 4L)
      )
    ).fold(message => fail(message), identity)
    val _ = loaded.execute(task, memory, TaskControl()).fold(error => fail(error.message), identity)
    assertEquals(memory.bytes.take(4), LittleEndian.toVec(0x41200000L, 4))
  }

  test("GLSL.std.450 Sin executes in a GLCompute shader") {
    val image  = bytes(vulkan.sin)
    val module = ISA.decode(image).fold(error => fail(error.message), identity)
    val loaded =
      LoadedProgram.load(module, Validation.Environment.VulkanCompute).fold(error => fail(error.message), identity)
    val allocation = AllocationId(502L)
    val memory     = ArrayMemory(allocation, 16)
    for ((offset, bits) <- Vec(4L -> 0x40000000L, 8L -> 0x40400000L, 12L -> 0x40800000L))
      assertEquals(
        memory.write(allocation, offset.unsafe[Long >= 0], LittleEndian.toVec(bits, 4)),
        Right(())
      )
    val task = KernelTask.validated(
      "main",
      LaunchGeometry.Dimensions.One,
      LaunchGeometry.Dimensions.One,
      Vec(
        KernelTask.Argument.Memory(allocation, 0L),
        KernelTask.Argument.Memory(allocation, 4L)
      )
    ).fold(message => fail(message), identity)
    val _      = loaded.execute(task, memory, TaskControl()).fold(error => fail(error.message), identity)
    val result =
      java.lang.Float.intBitsToFloat(LittleEndian.decode(memory.bytes, 0, 4).toInt)
    assertEqualsDouble(result.toDouble, 4.0 + math.sin(2.0), 0.000001)
  }

  test("GLSL.std.450 Pow executes in a GLCompute shader") {
    val image  = bytes(vulkan.pow)
    val module = ISA.decode(image).fold(error => fail(error.message), identity)
    val loaded =
      LoadedProgram.load(module, Validation.Environment.VulkanCompute).fold(error => fail(error.message), identity)
    val allocation = AllocationId(503L)
    val memory     = ArrayMemory(allocation, 16)
    for ((offset, bits) <- Vec(4L -> 0x40000000L, 8L -> 0x40400000L, 12L -> 0x40800000L))
      assertEquals(
        memory.write(allocation, offset.unsafe[Long >= 0], LittleEndian.toVec(bits, 4)),
        Right(())
      )
    val task = KernelTask.validated(
      "main",
      LaunchGeometry.Dimensions.One,
      LaunchGeometry.Dimensions.One,
      Vec(
        KernelTask.Argument.Memory(allocation, 0L),
        KernelTask.Argument.Memory(allocation, 4L)
      )
    ).fold(message => fail(message), identity)
    val _      = loaded.execute(task, memory, TaskControl()).fold(error => fail(error.message), identity)
    val result =
      java.lang.Float.intBitsToFloat(LittleEndian.decode(memory.bytes, 0, 4).toInt)
    assertEqualsDouble(result.toDouble, 12.0, 0.000001)
  }

  test("runtime array length and robust accesses use descriptor bounds within a shared allocation") {
    def ref(value: Int): Operand     = operand(OperandKind.IdRef, value)
    def dest(value: Int): Operand    = operand(OperandKind.IdResult, value)
    def typ(value: Int): Operand     = operand(OperandKind.IdResultType, value)
    def literal(value: Int): Operand = operand(OperandKind.LiteralInteger, value)
    val module                       = Module(
      Header(ISA.version, Word(0), Word(64), Word(0)),
      Vec(
        Instruction.Capability(operand(OperandKind.Capability, 1)),
        Instruction.MemoryModel(operand(OperandKind.AddressingModel, 0), operand(OperandKind.MemoryModel, 1)),
        Instruction.EntryPoint(
          operand(OperandKind.ExecutionModel, 5),
          ref(20),
          stringOperand("main"),
          Vec(ref(10), ref(11))
        ),
        Instruction.ExecutionMode(ref(20), operand(OperandKind.ExecutionMode, 17, 1, 1, 1)),
        Instruction.Decorate(ref(3), operand(OperandKind.Decoration, 6, 4)),
        Instruction.Decorate(ref(4), operand(OperandKind.Decoration, 2)),
        Instruction.MemberDecorate(ref(4), literal(0), operand(OperandKind.Decoration, 35, 0)),
        Instruction.MemberDecorate(ref(4), literal(1), operand(OperandKind.Decoration, 35, 4)),
        Instruction.Decorate(ref(10), operand(OperandKind.Decoration, 33, 0)),
        Instruction.Decorate(ref(10), operand(OperandKind.Decoration, 34, 0)),
        Instruction.Decorate(ref(11), operand(OperandKind.Decoration, 33, 1)),
        Instruction.Decorate(ref(11), operand(OperandKind.Decoration, 34, 0)),
        Instruction.TypeVoid(dest(1)),
        Instruction.TypeInt(dest(2), literal(32), literal(0)),
        Instruction.TypeRuntimeArray(dest(3), ref(2)),
        Instruction.TypeStruct(dest(4), Vec(ref(2), ref(3))),
        Instruction.TypePointer(dest(5), operand(OperandKind.StorageClass, 12), ref(4)),
        Instruction.TypePointer(dest(6), operand(OperandKind.StorageClass, 12), ref(2)),
        Instruction.TypeFunction(dest(7), ref(1), Vec.empty),
        Instruction.Constant(typ(2), dest(12), operand(OperandKind.LiteralContextDependentNumber, 0)),
        Instruction.Constant(typ(2), dest(13), operand(OperandKind.LiteralContextDependentNumber, 1)),
        Instruction.Constant(typ(2), dest(14), operand(OperandKind.LiteralContextDependentNumber, 2)),
        Instruction.Variable(typ(5), dest(10), operand(OperandKind.StorageClass, 12), None),
        Instruction.Variable(typ(5), dest(11), operand(OperandKind.StorageClass, 12), None),
        Instruction.Function(typ(1), dest(20), operand(OperandKind.FunctionControl, 0), ref(7)),
        Instruction.Label(dest(21)),
        Instruction.ArrayLength(typ(2), dest(22), ref(10), literal(1)),
        Instruction.AccessChain(typ(6), dest(23), ref(11), Vec(ref(12))),
        Instruction.Store(ref(23), ref(22), None),
        Instruction.AccessChain(typ(6), dest(24), ref(10), Vec(ref(13), ref(12))),
        Instruction.Load(typ(2), dest(25), ref(24), None),
        Instruction.AccessChain(typ(6), dest(26), ref(11), Vec(ref(13), ref(12))),
        Instruction.Store(ref(26), ref(25), None),
        Instruction.AccessChain(typ(6), dest(27), ref(10), Vec(ref(13), ref(13))),
        Instruction.Load(typ(2), dest(28), ref(27), None),
        Instruction.Store(ref(27), ref(12), None),
        Instruction.AccessChain(typ(6), dest(29), ref(11), Vec(ref(13), ref(13))),
        Instruction.Store(ref(29), ref(28), None),
        Instruction.AtomicIAdd(typ(2), dest(30), ref(27), ref(13), ref(12), ref(13)),
        Instruction.AccessChain(typ(6), dest(31), ref(11), Vec(ref(13), ref(14))),
        Instruction.Store(ref(31), ref(30), None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )

    val program =
      LoadedProgram.load(module, Validation.Environment.VulkanCompute).fold(error => fail(error.message), identity)
    val region = AllocationId(504L)
    val memory = ArrayMemory(region, 32)
    for ((offset, value) <- Vec(8L -> 99L, 12L -> 77L))
      assertEquals(
        memory.write(region, offset.unsafe[Long >= 0], LittleEndian.toVec(value, 4)),
        Right(())
      )
    val task = KernelTask.validated(
      "main",
      LaunchGeometry.Dimensions.One,
      LaunchGeometry.Dimensions.One,
      Vec(
        KernelTask.Argument.Memory(
          region,
          4L,
          Some(8L)
        ),
        KernelTask.Argument.Memory(
          region,
          16L,
          Some(16L)
        )
      )
    )
      .fold(fail(_), identity)
    val _ = program.execute(task, memory, TaskControl()).fold(error => fail(error.message), identity)
    assertEquals(LittleEndian.decode(memory.bytes, 12, 4), 77L)
    assertEquals(
      Vec(16, 20, 24, 28).map(offset => LittleEndian.decode(memory.bytes, offset, 4)),
      Vec(1L, 99L, 0L, 0L)
    )
  }

  test("OpCopyLogical copies values between distinct decorated structure types") {
    def ref(value: Int): Operand     = operand(OperandKind.IdRef, value)
    def dest(value: Int): Operand    = operand(OperandKind.IdResult, value)
    def typ(value: Int): Operand     = operand(OperandKind.IdResultType, value)
    def literal(value: Int): Operand = operand(OperandKind.LiteralInteger, value)
    val module                       = Module(
      Header(ISA.version, Word(0), Word(32), Word(0)),
      Vec(
        Instruction.Capability(operand(OperandKind.Capability, 1)),
        Instruction.MemoryModel(operand(OperandKind.AddressingModel, 0), operand(OperandKind.MemoryModel, 1)),
        Instruction.EntryPoint(operand(OperandKind.ExecutionModel, 5), ref(20), stringOperand("main"), Vec(ref(10))),
        Instruction.ExecutionMode(ref(20), operand(OperandKind.ExecutionMode, 17, 1, 1, 1)),
        Instruction.Decorate(ref(4), operand(OperandKind.Decoration, 2)),
        Instruction.MemberDecorate(ref(4), literal(0), operand(OperandKind.Decoration, 35, 0)),
        Instruction.MemberDecorate(ref(4), literal(1), operand(OperandKind.Decoration, 35, 16)),
        Instruction.Decorate(ref(10), operand(OperandKind.Decoration, 33, 0)),
        Instruction.Decorate(ref(10), operand(OperandKind.Decoration, 34, 0)),
        Instruction.TypeVoid(dest(1)),
        Instruction.TypeInt(dest(2), literal(32), literal(0)),
        Instruction.TypeStruct(dest(3), Vec(ref(2), ref(2))),
        Instruction.TypeStruct(dest(4), Vec(ref(2), ref(2))),
        Instruction.TypePointer(dest(5), operand(OperandKind.StorageClass, 12), ref(4)),
        Instruction.TypeFunction(dest(7), ref(1), Vec.empty),
        Instruction.Constant(typ(2), dest(12), operand(OperandKind.LiteralContextDependentNumber, 19)),
        Instruction.Constant(typ(2), dest(13), operand(OperandKind.LiteralContextDependentNumber, 23)),
        Instruction.ConstantComposite(typ(3), dest(14), Vec(ref(12), ref(13))),
        Instruction.Variable(typ(5), dest(10), operand(OperandKind.StorageClass, 12), None),
        Instruction.Function(typ(1), dest(20), operand(OperandKind.FunctionControl, 0), ref(7)),
        Instruction.Label(dest(21)),
        Instruction.CopyLogical(typ(4), dest(22), ref(14)),
        Instruction.Store(ref(10), ref(22), None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )

    val program =
      LoadedProgram.load(module, Validation.Environment.VulkanCompute).fold(error => fail(error.message), identity)
    val region = AllocationId(505L)
    val memory = ArrayMemory(region, 20)
    val task   = KernelTask.validated(
      "main",
      LaunchGeometry.Dimensions.One,
      LaunchGeometry.Dimensions.One,
      Vec(KernelTask.Argument.Memory(
        region,
        0L,
        Some(20L)
      ))
    ).fold(fail(_), identity)
    val _ = program.execute(task, memory, TaskControl()).fold(error => fail(error.message), identity)
    assertEquals(LittleEndian.decode(memory.bytes, 0, 4), 19L)
    assertEquals(LittleEndian.decode(memory.bytes, 16, 4), 23L)
    assertEquals(memory.bytes.slice(4, 16), Vec.fill(12)(0.toByte))
  }

  for (format <- Seq(33, 32))
    test(s"storage image format $format preserves descriptor aliases, query size, channels and coordinate bounds") {
      def ref(value: Int): Operand     = operand(OperandKind.IdRef, value)
      def dest(value: Int): Operand    = operand(OperandKind.IdResult, value)
      def typ(value: Int): Operand     = operand(OperandKind.IdResultType, value)
      def literal(value: Int): Operand = operand(OperandKind.LiteralInteger, value)
      val module                       = Module(
        Header(ISA.version, Word(0), Word(64), Word(0)),
        Vec(
          Instruction.Capability(operand(OperandKind.Capability, 1)),
          Instruction.MemoryModel(operand(OperandKind.AddressingModel, 0), operand(OperandKind.MemoryModel, 1)),
          Instruction.EntryPoint(
            operand(OperandKind.ExecutionModel, 5),
            ref(30),
            stringOperand("main"),
            Vec(ref(10), ref(11), ref(12))
          ),
          Instruction.ExecutionMode(ref(30), operand(OperandKind.ExecutionMode, 17, 1, 1, 1)),
          Instruction.Decorate(ref(10), operand(OperandKind.Decoration, 33, 0)),
          Instruction.Decorate(ref(10), operand(OperandKind.Decoration, 34, 0)),
          Instruction.Decorate(ref(11), operand(OperandKind.Decoration, 33, 1)),
          Instruction.Decorate(ref(11), operand(OperandKind.Decoration, 34, 0)),
          Instruction.Decorate(ref(12), operand(OperandKind.Decoration, 33, 2)),
          Instruction.Decorate(ref(12), operand(OperandKind.Decoration, 34, 0)),
          Instruction.Decorate(ref(7), operand(OperandKind.Decoration, 2)),
          Instruction.MemberDecorate(ref(7), literal(0), operand(OperandKind.Decoration, 35, 0)),
          Instruction.MemberDecorate(ref(7), literal(1), operand(OperandKind.Decoration, 35, 16)),
          Instruction.MemberDecorate(ref(7), literal(2), operand(OperandKind.Decoration, 35, 24)),
          Instruction.MemberDecorate(ref(7), literal(3), operand(OperandKind.Decoration, 35, 32)),
          Instruction.TypeVoid(dest(1)),
          Instruction.TypeInt(dest(2), literal(32), literal(0)),
          Instruction.TypeVector(dest(3), ref(2), literal(2)),
          Instruction.TypeVector(dest(4), ref(2), literal(4)),
          Instruction.TypeImage(
            dest(5),
            ref(2),
            operand(OperandKind.Dim, 1),
            literal(0),
            literal(0),
            literal(0),
            literal(2),
            operand(OperandKind.ImageFormat, format),
            None
          ),
          Instruction.TypePointer(dest(6), operand(OperandKind.StorageClass, 0), ref(5)),
          Instruction.TypeStruct(dest(7), Vec(ref(4), ref(3), ref(2), ref(4))),
          Instruction.TypePointer(dest(8), operand(OperandKind.StorageClass, 12), ref(7)),
          Instruction.TypeFunction(dest(9), ref(1), Vec.empty),
          Instruction.TypePointer(dest(13), operand(OperandKind.StorageClass, 11), ref(2)),
          Instruction.Constant(typ(2), dest(20), operand(OperandKind.LiteralContextDependentNumber, 0)),
          Instruction.Constant(typ(2), dest(21), operand(OperandKind.LiteralContextDependentNumber, 1)),
          Instruction.Constant(typ(2), dest(22), operand(OperandKind.LiteralContextDependentNumber, -1)),
          Instruction.Constant(typ(2), dest(23), operand(OperandKind.LiteralContextDependentNumber, 77)),
          Instruction.ConstantComposite(typ(3), dest(24), Vec(ref(21), ref(20))),
          Instruction.ConstantComposite(typ(3), dest(25), Vec(ref(22), ref(20))),
          Instruction.ConstantComposite(
            typ(4),
            dest(26),
            if (format == 33) Vec(ref(23), ref(23), ref(23), ref(23)) else Vec(ref(23), ref(21), ref(22), ref(20))
          ),
          Instruction.Variable(typ(6), dest(10), operand(OperandKind.StorageClass, 0), None),
          Instruction.Variable(typ(6), dest(11), operand(OperandKind.StorageClass, 0), None),
          Instruction.Variable(typ(8), dest(12), operand(OperandKind.StorageClass, 12), None),
          Instruction.Function(typ(1), dest(30), operand(OperandKind.FunctionControl, 0), ref(9)),
          Instruction.Label(dest(31)),
          Instruction.Load(typ(5), dest(41), ref(10), None),
          Instruction.ImageWrite(ref(41), ref(24), ref(26), None),
          Instruction.Load(typ(5), dest(42), ref(11), None),
          Instruction.ImageRead(typ(4), dest(43), ref(42), ref(24), None),
          Instruction.ImageQuerySize(typ(3), dest(44), ref(42)),
          if (format == 33) Instruction.ImageTexelPointer(typ(13), dest(45), ref(11), ref(24), ref(20))
          else Instruction.Nop(),
          if (format == 33) Instruction.AtomicIAdd(typ(2), dest(46), ref(45), ref(21), ref(20), ref(21))
          else Instruction.CopyObject(typ(2), dest(46), ref(23)),
          Instruction.ImageWrite(ref(41), ref(25), ref(26), None),
          Instruction.ImageRead(typ(4), dest(47), ref(42), ref(25), None),
          Instruction.CompositeConstruct(typ(7), dest(48), Vec(ref(43), ref(44), ref(46), ref(47))),
          Instruction.Store(ref(12), ref(48), None),
          Instruction.Return(),
          Instruction.FunctionEnd()
        ),
        Endianness.Little
      )

      val program =
        LoadedProgram.load(module, Validation.Environment.VulkanCompute).fold(error => fail(error.message), identity)
      val region = AllocationId(506L)
      val task   = KernelTask.validated(
        "main",
        LaunchGeometry.Dimensions.One,
        LaunchGeometry.Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(
            region,
            4L,
            Some(32L)
          ),
          KernelTask.Argument.Memory(
            region,
            4L,
            Some(32L)
          ),
          KernelTask.Argument.Memory(
            region,
            48L,
            Some(48L)
          )
        )
      ).fold(fail(_), identity)
      for (width <- Vec(2L, 0L, 16385L)) {
        val memory = ArrayMemory(region, 96)
        for ((offset, value) <- Vec(0L -> 99L, 4L -> width, 8L -> 2L, 12L -> 1L, 16L -> format.toLong, 36L -> 99L))
          assertEquals(
            memory.write(region, offset.unsafe[Long >= 0], LittleEndian.toVec(value, 4)),
            Right(())
          )
        val result = program.execute(task, memory, TaskControl())
        if (width == 2) {
          val _                       = result.fold(error => fail(error.message), identity)
          def word(offset: Int): Long = LittleEndian.decode(memory.bytes, offset, 4)
          assertEquals(
            Vec(48, 52, 56, 60, 64, 68, 72, 80, 84, 88, 92).map(word(_)),
            if (format == 33) Vec(77L, 0L, 0L, 1L, 2L, 2L, 77L, 0L, 0L, 0L, 0L)
            else Vec(77L, 1L, 255L, 0L, 2L, 2L, 77L, 0L, 0L, 0L, 0L)
          )
          assertEquals(word(24), if (format == 33) 78L else 0x00ff014dL)
          assertEquals(word(0), 99L)
          assertEquals(word(36), 99L)
        } else assert(result.isLeft)
      }
    }

}
