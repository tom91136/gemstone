package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
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

  private val programGlobals = spv"""
    $openCL
    OpEntryPoint Kernel %step "step"
    OpName %counter "counter"
    OpName %table "table"
    OpName %pointer "pointer"
    OpName %table_pointer "table_pointer"
    OpName %calls "step.calls"
    %void = OpTypeVoid
    %uint = OpTypeInt 32 0
    %ulong = OpTypeInt 64 0
    %ulong_0 = OpConstant %ulong 0
    %ulong_1 = OpConstant %ulong 1
    %ulong_2 = OpConstant %ulong 2
    %ulong_4 = OpConstant %ulong 4
    %array = OpTypeArray %uint %ulong_4
    %uint_ptr = OpTypePointer CrossWorkgroup %uint
    %array_ptr = OpTypePointer CrossWorkgroup %array
    %uint_ptr_ptr = OpTypePointer CrossWorkgroup %uint_ptr
    %fn = OpTypeFunction %void %uint_ptr
    %uint_0 = OpConstant %uint 0
    %uint_1 = OpConstant %uint 1
    %uint_2 = OpConstant %uint 2
    %uint_3 = OpConstant %uint 3
    %uint_4 = OpConstant %uint 4
    %uint_5 = OpConstant %uint 5
    %uint_10 = OpConstant %uint 10
    %initial = OpConstantComposite %array %uint_1 %uint_2 %uint_3 %uint_4
    %counter = OpVariable %uint_ptr CrossWorkgroup %uint_5
    %table = OpVariable %array_ptr CrossWorkgroup %initial
    %pointer = OpVariable %uint_ptr_ptr CrossWorkgroup %counter
    %third = OpSpecConstantOp %uint_ptr PtrAccessChain %table %ulong_0 %ulong_2
    %table_pointer = OpVariable %uint_ptr_ptr CrossWorkgroup %third
    %calls = OpVariable %uint_ptr CrossWorkgroup %uint_0
    %step = OpFunction %void None %fn
    %out = OpFunctionParameter %uint_ptr
    %entry = OpLabel
    %target = OpLoad %uint_ptr %pointer
    %value = OpLoad %uint %target
    OpStore %out %value
    %element = OpLoad %uint_ptr %table_pointer
    %third_value = OpLoad %uint %element
    %out1 = OpPtrAccessChain %uint_ptr %out %ulong_1
    OpStore %out1 %third_value
    %called = OpLoad %uint %calls
    %out2 = OpPtrAccessChain %uint_ptr %out %ulong_2
    OpStore %out2 %called
    %next = OpIAdd %uint %value %uint_10
    OpStore %target %next
    %bumped = OpIAdd %uint %third_value %uint_1
    OpStore %element %bumped
    %more = OpIAdd %uint %called %uint_1
    OpStore %calls %more
    OpReturn
    OpFunctionEnd
  """

  test("pointer-initialised CrossWorkgroup globals address their targets") {
    val memory = RegionMemory()
    val out    = memory.allocate(12L)
    assertEquals(run(programGlobals, "step", memory, Vec(buffer(out))), Right(()))
    assertEquals(words(memory, out, offset = 0, count = 3), Vec(5L, 3L, 0L))
  }

  test("program symbols list every CrossWorkgroup global with its size") {
    val globals = load(programGlobals).value.symbols.value.globals
    assertEquals(
      globals.map(global => global.name.raw -> global.size.raw).toSet,
      Set("counter" -> 4L, "table" -> 16L, "pointer" -> 8L, "table_pointer" -> 8L, "step.calls" -> 4L)
    )
  }

  test("bound program globals persist across dispatches and pointer initialisers address their bound storage") {
    val program = load(programGlobals).value
    val memory  = RegionMemory()
    val out     = memory.allocate(12L)
    val regions = program.symbols.value.globals.map { global =>
      val size    = global.size.unsafe[Long > 0]
      val region  = memory.allocate(size)
      val binding = GlobalBinding.validated(global.name, region, 0L, global.size, size).value
      assertEquals(program.bindGlobal(global.name, binding, memory), Right(()))
      global.name.raw -> region
    }.toMap
    for (launch <- 0 until 3) {
      assertEquals(execute(program, task("step", Vec(buffer(out))), memory), Right(()))
      assertEquals(
        words(memory, out, offset = 0, count = 3),
        Vec(5L + 10L * launch, 3L + launch, launch.toLong),
        s"launch $launch"
      )
    }
    assertEquals(word(memory, regions("counter"), 0), 35L)
    assertEquals(words(memory, regions("table"), offset = 0, count = 4), Vec(1L, 2L, 6L, 4L))
    assertEquals(word(memory, regions("step.calls"), 0), 3L)
    val counter = memory.address(regions("counter"), 0L).value.raw
    val table   = memory.address(regions("table"), 0L).value.raw
    assertEquals(LittleEndian.decode(memory.bytes(regions("pointer")), 0, 8), counter)
    assertEquals(LittleEndian.decode(memory.bytes(regions("table_pointer")), 0, 8), table + 8L)
  }

  test("specialisation constant pointer casts of null stay null") {
    val module = spv"""
      OpCapability GenericPointer
      $openCL
      OpEntryPoint Kernel %main "null_casts"
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %ulong = OpTypeInt 64 0
      %bool = OpTypeBool
      %uint_ptr = OpTypePointer CrossWorkgroup %uint
      %generic_ptr = OpTypePointer Generic %uint
      %slot_ptr = OpTypePointer CrossWorkgroup %uint_ptr
      %fn = OpTypeFunction %void %uint_ptr
      %uint_0 = OpConstant %uint 0
      %uint_1 = OpConstant %uint 1
      %ulong_0 = OpConstant %ulong 0
      %null_generic = OpConstantNull %generic_ptr
      %null_global = OpSpecConstantOp %uint_ptr GenericCastToPtr %null_generic
      %back = OpSpecConstantOp %generic_ptr PtrCastToGeneric %null_global
      %slot = OpVariable %slot_ptr CrossWorkgroup %null_global
      %main = OpFunction %void None %fn
      %out = OpFunctionParameter %uint_ptr
      %entry = OpLabel
      %stored = OpLoad %uint_ptr %slot
      %stored_address = OpConvertPtrToU %ulong %stored
      %cast_address = OpConvertPtrToU %ulong %back
      %both = OpIAdd %ulong %stored_address %cast_address
      %zero = OpIEqual %bool %both %ulong_0
      %flag = OpSelect %uint %zero %uint_1 %uint_0
      OpStore %out %flag
      OpReturn
      OpFunctionEnd
    """
    val memory = RegionMemory()
    val out    = memory.allocate(4L)
    assertEquals(run(module, "null_casts", memory, Vec(buffer(out))), Right(()))
    assertEquals(word(memory, out, 0), 1L)
  }
}
