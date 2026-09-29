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
    val first  = operand("IdResult", 10)
    val second = operand("IdResult", 20)
    val left   = operand("IdResult", 30)
    val right  = operand("IdResult", 31)
    val module = Module(
      Header(ISA.version, Word(0), Word(64), Word(0)),
      Vec(
        Instruction.EntryPoint(
          operand("ExecutionModel", spv.ExecutionModel.GLCompute),
          first,
          stringOperand("first"),
          Vec(left)
        ),
        Instruction.EntryPoint(
          operand("ExecutionModel", spv.ExecutionModel.GLCompute),
          second,
          stringOperand("second"),
          Vec(right)
        ),
        Instruction.Decorate(left, operand("Decoration", spv.Decoration.Binding, 0)),
        Instruction.Decorate(left, operand("Decoration", spv.Decoration.DescriptorSet, 0)),
        Instruction.Decorate(right, operand("Decoration", spv.Decoration.Binding, 1)),
        Instruction.Decorate(right, operand("Decoration", spv.Decoration.DescriptorSet, 0)),
        Instruction.Function(
          operand("IdResultType", 1),
          first,
          operand("FunctionControl", spv.FunctionControl.None),
          operand("IdRef", 2)
        ),
        Instruction.FunctionEnd(),
        Instruction.Function(
          operand("IdResultType", 1),
          second,
          operand("FunctionControl", spv.FunctionControl.None),
          operand("IdRef", 2)
        ),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val program = VMCompiler.compileUnchecked(module).fold(error => fail(error.message), identity)
    val loaded  = VMProgramRuntime(program)
    assertEquals(loaded.descriptorVariables("first"), Vec(30))
    assertEquals(loaded.descriptorVariables("second"), Vec(31))
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
}
