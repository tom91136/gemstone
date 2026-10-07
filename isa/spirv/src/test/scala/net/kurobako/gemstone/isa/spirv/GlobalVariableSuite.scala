package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*

class GlobalVariableSuite extends munit.FunSuite {
  test("composite-initialised CrossWorkgroup globals materialise their initialiser") {
    val module = spv"""
      $openCL
      OpEntryPoint Kernel %main "global" %table
      OpDecorate %table Constant
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %two = OpConstant %uint 2
      %array = OpTypeArray %uint %two
      %array_ptr = OpTypePointer CrossWorkgroup %array
      %uint_ptr = OpTypePointer CrossWorkgroup %uint
      %fn = OpTypeFunction %void %uint_ptr
      %first = OpConstant %uint 17
      %second = OpConstant %uint 29
      %one = OpConstant %uint 1
      %initial = OpConstantComposite %array %first %second
      %table = OpVariable %array_ptr CrossWorkgroup %initial
      %main = OpFunction %void None %fn
      %out = OpFunctionParameter %uint_ptr
      %entry = OpLabel
      %element = OpAccessChain %uint_ptr %table %one
      %loaded = OpLoad %uint %element
      OpStore %out %loaded
      OpReturn
      OpFunctionEnd
    """
    val memory = RegionMemory()
    val out    = memory.allocate(4L)
    assertEquals(run(module, "global", memory, Vec(buffer(out))), Right(()))
    assertEquals(word(memory, out, 0), 29L)
  }

  test("Workgroup and Private variables are rematerialised at their SPIR-V lifetime boundaries") {
    for (
      (storage, local, groups) <- Seq(
        ("Workgroup", testDimensions(1), testDimensions(2)),
        ("Private", testDimensions(2), testDimensions(1))
      )
    ) {
      val module = spv"""
        $openCL
        OpEntryPoint Kernel %main "lifetime" %counter
        %void = OpTypeVoid
        %uint = OpTypeInt 32 0
        %counter_ptr = OpTypePointer $storage %uint
        %uint_ptr = OpTypePointer CrossWorkgroup %uint
        %fn = OpTypeFunction %void %uint_ptr
        %seven = OpConstant %uint 7
        %one = OpConstant %uint 1
        %counter = OpVariable %counter_ptr $storage %seven
        %main = OpFunction %void None %fn
        %out = OpFunctionParameter %uint_ptr
        %entry = OpLabel
        %loaded = OpLoad %uint %counter
        OpStore %out %loaded
        %next = OpIAdd %uint %loaded %one
        OpStore %counter %next
        OpReturn
        OpFunctionEnd
      """
      val memory = RegionMemory()
      val out    = memory.allocate(4L)
      assertEquals(
        run(module, "lifetime", memory, Vec(buffer(out)), local, groups),
        Right(()),
        storage
      )
      assertEquals(word(memory, out, 0), 7L, storage)
    }
  }

  test("rebinding a global is idempotent for the same storage and rejected for different storage") {
    val module = spv"""
      $openCL
      OpEntryPoint Kernel %main "main"
      OpName %value "value"
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %fn = OpTypeFunction %void
      %uint_ptr = OpTypePointer CrossWorkgroup %uint
      %seven = OpConstant %uint 7
      %value = OpVariable %uint_ptr CrossWorkgroup %seven
      %main = OpFunction %void None %fn
      %entry = OpLabel
      OpReturn
      OpFunctionEnd
    """
    val program                                      = load(module).value
    val memory                                       = RegionMemory()
    val first                                        = memory.allocate(4L)
    val second                                       = memory.allocate(4L)
    def binding(region: AllocationId): GlobalBinding = GlobalBinding.validated("value", region, 0L, 4L, 4L).value
    assertEquals(program.bindGlobal("value", binding(first), memory), Right(()))
    assertEquals(word(memory, first, 0), 7L)
    assertEquals(program.writeGlobal("value", 0L, Vec[Byte](9, 0, 0, 0)), Right(()))
    assertEquals(program.bindGlobal("value", binding(first), memory), Right(()))
    assertEquals(word(memory, first, 0), 9L)
    assert(program.bindGlobal("value", binding(second), memory).isLeft)
  }
}
