package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.isa.{KernelMetadata, KernelName}
import net.kurobako.gemstone.binary.{LittleEndian, ScalarByteWidth}
import net.kurobako.gemstone.vm.{Event, KernelTask, LaunchGeometry, MemoryRegion, Observer, Scheduler, TaskControl}
import net.kurobako.gemstone.{NonEmptyVec, NonNegativeInt, NonNegativeLong, PositiveInt, PositiveLong}

final class VulkanComputeSuite extends munit.FunSuite with ExecutionTestSupport {
  private def resource(name: String): Vec[Byte] =
    Option(getClass.getResourceAsStream(name)).fold(fail(s"missing resource $name")) { stream =>
      try Vec.from(stream.readAllBytes().toIndexedSeq)
      finally stream.close()
    }

  test("GLCompute descriptor bindings are scoped to each entry point interface") {
    val first  = operand("IdResult", 10)
    val second = operand("IdResult", 20)
    val left   = operand("IdResult", 30)
    val right  = operand("IdResult", 31)
    val module = Module(
      Header(ISA.version, Word(0), Word(64), Word(0)),
      Vec(
        Instruction.EntryPoint(operand("ExecutionModel", 5), first, stringOperand("first"), Vec(left)),
        Instruction.EntryPoint(operand("ExecutionModel", 5), second, stringOperand("second"), Vec(right)),
        Instruction.Decorate(left, operand("Decoration", 33, 0)),
        Instruction.Decorate(left, operand("Decoration", 34, 0)),
        Instruction.Decorate(right, operand("Decoration", 33, 1)),
        Instruction.Decorate(right, operand("Decoration", 34, 0)),
        Instruction.Function(operand("IdResultType", 1), first, operand("FunctionControl", 0), operand("IdRef", 2)),
        Instruction.FunctionEnd(),
        Instruction.Function(operand("IdResultType", 1), second, operand("FunctionControl", 0), operand("IdRef", 2)),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val program = VMCompiler.compileUnchecked(module).fold(error => fail(error.message), identity)
    val loaded  = VMLoadedProgram(program)
    assertEquals(VMInterpreter.descriptorVariables(loaded, KernelName("first")), Vec(30))
    assertEquals(VMInterpreter.descriptorVariables(loaded, KernelName("second")), Vec(31))
  }

  test("GLCompute dot kernel decodes specialization constant operations") {
    val bytes  = resource("/glcompute-dot-double.spv")
    val module = ISA.decode(bytes).fold(error => fail(error.message), identity)
    assert(module.instructions.exists {
      case Instruction.SpecConstantOp(_, _, _, _) => true
      case _                                      => false
    })
    val machine = VirtualMachine(Validation.Environment.VulkanCompute, Observer.discard[Event], Scheduler.Sequential)
    val loaded  = machine.load(module).fold(error => fail(error.message), identity)
    assert(machine.kernelNames(loaded).contains(KernelName("main")))
  }

  test("GLCompute entry point exposes storage and uniform descriptor bindings") {
    val bytes    = resource("/glcompute-fma.spv")
    val module   = ISA.decode(bytes).fold(error => fail(error.message), identity)
    val machine  = VirtualMachine(Validation.Environment.VulkanCompute, Observer.discard[Event], Scheduler.Sequential)
    val loaded   = machine.load(module).fold(error => fail(error.message), identity)
    val metadata = machine.kernel(loaded, KernelName("main")).fold(error => fail(error.message), identity)
    assertEquals(metadata.explicitParameterCount.raw, 2)
    assertEquals(
      metadata.parameters.map(_.kind),
      Vec(KernelMetadata.ParameterKind.Memory, KernelMetadata.ParameterKind.Memory)
    )
  }

  test("GLCompute FMA reads uniform data and writes a storage buffer") {
    val bytes      = resource("/glcompute-fma.spv")
    val module     = ISA.decode(bytes).fold(error => fail(error.message), identity)
    val machine    = VirtualMachine(Validation.Environment.VulkanCompute, Observer.discard[Event], Scheduler.Sequential)
    val loaded     = machine.load(module).fold(error => fail(error.message), identity)
    val allocation = MemoryRegion(501L)
    val memory     = ArrayMemory(allocation, 16)
    for ((offset, bits) <- Vec(4L -> 0x40000000L, 8L -> 0x40400000L, 12L -> 0x40800000L))
      assertEquals(
        memory.write(allocation, NonNegativeLong.unsafe(offset), LittleEndian.toVec(bits, ScalarByteWidth(4))),
        Right(())
      )
    val task = KernelTask.validated(
      KernelName("main"),
      LaunchGeometry.Dimensions.One,
      LaunchGeometry.Dimensions.One,
      Vec(
        KernelTask.Argument.Memory(allocation, NonNegativeLong(0L)),
        KernelTask.Argument.Memory(allocation, NonNegativeLong(4L))
      )
    ).fold(message => fail(message), identity)
    val _ = machine.execute(loaded, task, memory, TaskControl.default).fold(error => fail(error.message), identity)
    assertEquals(memory.bytes.take(4), LittleEndian.toVec(0x41200000L, ScalarByteWidth.Dword))
  }

  test("GLSL.std.450 Sin executes in a GLCompute shader") {
    val bytes      = resource("/glcompute-sin.spv")
    val module     = ISA.decode(bytes).fold(error => fail(error.message), identity)
    val machine    = VirtualMachine(Validation.Environment.VulkanCompute, Observer.discard[Event], Scheduler.Sequential)
    val loaded     = machine.load(module).fold(error => fail(error.message), identity)
    val allocation = MemoryRegion(502L)
    val memory     = ArrayMemory(allocation, 16)
    for ((offset, bits) <- Vec(4L -> 0x40000000L, 8L -> 0x40400000L, 12L -> 0x40800000L))
      assertEquals(
        memory.write(allocation, NonNegativeLong.unsafe(offset), LittleEndian.toVec(bits, ScalarByteWidth(4))),
        Right(())
      )
    val task = KernelTask.validated(
      KernelName("main"),
      LaunchGeometry.Dimensions.One,
      LaunchGeometry.Dimensions.One,
      Vec(
        KernelTask.Argument.Memory(allocation, NonNegativeLong(0L)),
        KernelTask.Argument.Memory(allocation, NonNegativeLong(4L))
      )
    ).fold(message => fail(message), identity)
    val _      = machine.execute(loaded, task, memory, TaskControl.default).fold(error => fail(error.message), identity)
    val result = java.lang.Float.intBitsToFloat(LittleEndian.decode(memory.bytes, 0, ScalarByteWidth.Dword).toInt)
    assertEqualsDouble(result.toDouble, 4.0 + math.sin(2.0), 0.000001)
  }

  test("GLSL.std.450 Pow executes in a GLCompute shader") {
    val bytes      = resource("/glcompute-pow.spv")
    val module     = ISA.decode(bytes).fold(error => fail(error.message), identity)
    val machine    = VirtualMachine(Validation.Environment.VulkanCompute, Observer.discard[Event], Scheduler.Sequential)
    val loaded     = machine.load(module).fold(error => fail(error.message), identity)
    val allocation = MemoryRegion(503L)
    val memory     = ArrayMemory(allocation, 16)
    for ((offset, bits) <- Vec(4L -> 0x40000000L, 8L -> 0x40400000L, 12L -> 0x40800000L))
      assertEquals(
        memory.write(allocation, NonNegativeLong.unsafe(offset), LittleEndian.toVec(bits, ScalarByteWidth(4))),
        Right(())
      )
    val task = KernelTask.validated(
      KernelName("main"),
      LaunchGeometry.Dimensions.One,
      LaunchGeometry.Dimensions.One,
      Vec(
        KernelTask.Argument.Memory(allocation, NonNegativeLong(0L)),
        KernelTask.Argument.Memory(allocation, NonNegativeLong(4L))
      )
    ).fold(message => fail(message), identity)
    val _      = machine.execute(loaded, task, memory, TaskControl.default).fold(error => fail(error.message), identity)
    val result = java.lang.Float.intBitsToFloat(LittleEndian.decode(memory.bytes, 0, ScalarByteWidth.Dword).toInt)
    assertEqualsDouble(result.toDouble, 12.0, 0.000001)
  }
}
