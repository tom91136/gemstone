package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*

class LinkageSuite extends Suite {
  private val caller = spv"""
    $openCL
    OpCapability Linkage
    OpEntryPoint Kernel %main "call"
    OpDecorate %add LinkageAttributes "add" Import
    OpDecorate %counter LinkageAttributes "counter" Import
    %void = OpTypeVoid
    %uint = OpTypeInt 32 0
    %out_ptr = OpTypePointer CrossWorkgroup %uint
    %entry_fn = OpTypeFunction %void %out_ptr %uint %uint
    %add_fn = OpTypeFunction %uint %uint %uint
    %counter = OpVariable %out_ptr CrossWorkgroup
    %add = OpFunction %uint None %add_fn
    %add_a = OpFunctionParameter %uint
    %add_b = OpFunctionParameter %uint
    OpFunctionEnd
    %main = OpFunction %void None %entry_fn
    %out = OpFunctionParameter %out_ptr
    %x = OpFunctionParameter %uint
    %y = OpFunctionParameter %uint
    %entry = OpLabel
    %sum = OpFunctionCall %uint %add %x %y
    %base = OpLoad %uint %counter
    %total = OpIAdd %uint %sum %base
    OpStore %out %total
    OpReturn
    OpFunctionEnd
  """

  private val provider = spv"""
    $openCL
    OpCapability Linkage
    OpDecorate %add LinkageAttributes "add" Export
    OpDecorate %counter LinkageAttributes "counter" Export
    %uint = OpTypeInt 32 0
    %ptr = OpTypePointer CrossWorkgroup %uint
    %add_fn = OpTypeFunction %uint %uint %uint
    %forty_two = OpConstant %uint 42
    %counter = OpVariable %ptr CrossWorkgroup %forty_two
    %add = OpFunction %uint None %add_fn
    %a = OpFunctionParameter %uint
    %b = OpFunctionParameter %uint
    %add_entry = OpLabel
    %result = OpIAdd %uint %a %b
    OpReturnValue %result
    OpFunctionEnd
  """

  private def link(modules: Module*): Either[Fault, Module] =
    Linkage.link(Vector.from(modules), OpenCLBuiltins.supplies).map(_._1)

  private def symbols(module: Module, linkage: Int): Set[String] = module.instructions.collect {
    case Instruction.Decorate(_, decoration)
        if Operand.unsigned(decoration) == Decoration.LinkageAttributes && decoration.words.last.raw == linkage =>
      LiteralString.decode(decoration.words.slice(1, decoration.words.length - 1))
  }.toSet

  test("imports resolve against the exports of the other module") {
    val linked = link(caller, provider).value
    assertEquals(symbols(linked, LinkageType.Import), Set.empty[String])
    assertEquals(symbols(linked, LinkageType.Export), Set("add", "counter"))
    assertEquals(linked.instructions.count(_.opcode == "OpFunction"), 2)
    assertEquals(linked.instructions.count(_.opcode == "OpVariable"), 1)
    assertEquals(linked.instructions.count(_.opcode == "OpTypeInt"), 1)
    assertEquals(linked.instructions.count(_.opcode == "OpTypeFunction"), 2)
  }

  test("a kernel runs through the linked function and global") {
    val linked = link(caller, provider).value
    val memory = RegionMemory()
    val out    = memory.allocate(4L)
    assertEquals(run(linked, "call", memory, Vec(buffer(out), scalar(3L, 4), scalar(4L, 4))), Right(()))
    assertEquals(word(memory, out, 0), 49L)
  }

  test("module order does not change the linked result") {
    val linked = link(provider, caller).value
    val memory = RegionMemory()
    val out    = memory.allocate(4L)
    assertEquals(run(linked, "call", memory, Vec(buffer(out), scalar(10L, 4), scalar(20L, 4))), Right(()))
    assertEquals(word(memory, out, 0), 72L)
  }

  test("images link through their binary encoding") {
    val image =
      Linkage.link(Vec(ISA.encode(caller).value, ISA.encode(provider).value), OpenCLBuiltins.supplies).value.image
    assertEquals(ISA.decode(image).value, link(caller, provider).value)
  }

  test("a linked module links again with a third") {
    val third = spv"""
      $openCL
      OpCapability Linkage
      OpDecorate %twice LinkageAttributes "twice" Export
      OpDecorate %add LinkageAttributes "add" Import
      %uint = OpTypeInt 32 0
      %add_fn = OpTypeFunction %uint %uint %uint
      %twice_fn = OpTypeFunction %uint %uint
      %add = OpFunction %uint None %add_fn
      %add_a = OpFunctionParameter %uint
      %add_b = OpFunctionParameter %uint
      OpFunctionEnd
      %twice = OpFunction %uint None %twice_fn
      %v = OpFunctionParameter %uint
      %twice_entry = OpLabel
      %doubled = OpFunctionCall %uint %add %v %v
      OpReturnValue %doubled
      OpFunctionEnd
    """
    val pair   = link(caller, provider).value
    val linked = link(third, pair).value
    assertEquals(symbols(linked, LinkageType.Import), Set.empty[String])
    assertEquals(symbols(linked, LinkageType.Export), Set("twice", "add", "counter"))
  }

  test("an import with no export and no builtin is rejected by name") {
    assert(link(caller, caller).left.exists(_.message.contains("unresolved symbol 'add'")))
  }

  test("imports naming a loader builtin stay imports") {
    val user = spv"""
      $openCL
      OpCapability Linkage
      OpDecorate %local LinkageAttributes "_Z12get_local_idj" Import
      %uint = OpTypeInt 32 0
      %local_fn = OpTypeFunction %uint %uint
      %local = OpFunction %uint None %local_fn
      %dim = OpFunctionParameter %uint
      OpFunctionEnd
    """
    assertEquals(symbols(link(user, provider).value, LinkageType.Import), Set("_Z12get_local_idj"))
  }

  test("imports of loader builtin variables resolve without an export") {
    val user = spv"""
      $openCL
      OpCapability Linkage
      OpEntryPoint Kernel %main "call"
      OpDecorate %ids LinkageAttributes "__spirv_BuiltInGlobalInvocationId" Import
      OpDecorate %ids BuiltIn GlobalInvocationId
      %void = OpTypeVoid
      %ulong = OpTypeInt 64 0
      %ids_vector = OpTypeVector %ulong 3
      %ids_ptr = OpTypePointer Input %ids_vector
      %ids = OpVariable %ids_ptr Input
      %main_fn = OpTypeFunction %void
      %main = OpFunction %void None %main_fn
      %entry = OpLabel
      OpReturn
      OpFunctionEnd
    """
    assertEquals(symbols(link(user, provider).value, LinkageType.Import), Set("__spirv_BuiltInGlobalInvocationId"))
  }

  test("a linked kernel reads a loader builtin variable per work-item") {
    val user = spv"""
      $openCL
      OpCapability Linkage
      OpEntryPoint Kernel %main "ids"
      OpDecorate %ids LinkageAttributes "__spirv_BuiltInGlobalInvocationId" Import
      OpDecorate %ids BuiltIn GlobalInvocationId
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %ulong = OpTypeInt 64 0
      %ids_vector = OpTypeVector %ulong 3
      %ids_ptr = OpTypePointer Input %ids_vector
      %out_ptr = OpTypePointer CrossWorkgroup %uint
      %main_fn = OpTypeFunction %void %out_ptr
      %ids = OpVariable %ids_ptr Input
      %main = OpFunction %void None %main_fn
      %out = OpFunctionParameter %out_ptr
      %entry = OpLabel
      %vector = OpLoad %ids_vector %ids
      %x = OpCompositeExtract %ulong %vector 0
      %slot = OpPtrAccessChain %out_ptr %out %x
      %narrow = OpUConvert %uint %x
      OpStore %slot %narrow
      OpReturn
      OpFunctionEnd
    """
    val linked = link(user, provider).value
    val memory = RegionMemory()
    val out    = memory.allocate(8L)
    assertEquals(run(linked, "ids", memory, Vec(buffer(out)), groups = testDimensions(2)), Right(()))
    assertEquals(words(memory, out, 0, 2), Vec(0L, 1L))
  }

  test("a linked kernel classifies floats through the llvm.is.fpclass import") {
    val user = spv"""
      $openCL
      OpCapability Linkage
      OpEntryPoint Kernel %main "classify"
      OpDecorate %is_fpclass LinkageAttributes "llvm.is.fpclass.f32" Import
      OpDecorate %ids LinkageAttributes "__spirv_BuiltInGlobalInvocationId" Import
      OpDecorate %ids BuiltIn GlobalInvocationId
      %void = OpTypeVoid
      %bool = OpTypeBool
      %uint = OpTypeInt 32 0
      %ulong = OpTypeInt 64 0
      %float = OpTypeFloat 32
      %ids_vector = OpTypeVector %ulong 3
      %ids_ptr = OpTypePointer Input %ids_vector
      %ptr = OpTypePointer CrossWorkgroup %uint
      %main_fn = OpTypeFunction %void %ptr %ptr
      %class_fn = OpTypeFunction %bool %float %uint
      %infinities = OpConstant %uint 516
      %one = OpConstant %uint 1
      %zero = OpConstant %uint 0
      %ids = OpVariable %ids_ptr Input
      %is_fpclass = OpFunction %bool None %class_fn
      %value_in = OpFunctionParameter %float
      %mask_in = OpFunctionParameter %uint
      OpFunctionEnd
      %main = OpFunction %void None %main_fn
      %in = OpFunctionParameter %ptr
      %out = OpFunctionParameter %ptr
      %entry = OpLabel
      %vector = OpLoad %ids_vector %ids
      %x = OpCompositeExtract %ulong %vector 0
      %from = OpPtrAccessChain %ptr %in %x
      %to = OpPtrAccessChain %ptr %out %x
      %raw = OpLoad %uint %from
      %value = OpBitcast %float %raw
      %hit = OpFunctionCall %bool %is_fpclass %value %infinities
      %flag = OpSelect %uint %hit %one %zero
      OpStore %to %flag
      OpReturn
      OpFunctionEnd
    """
    val linked = link(user, provider).value
    val memory = RegionMemory()
    val in     = memory.allocate(16L)
    val out    = memory.allocate(16L)
    writeWords(memory, in, 0, 0x7f800000L, 0xff800000L, 0x7fc00000L, 0x3f800000L)
    assertEquals(run(linked, "classify", memory, Vec(buffer(in), buffer(out)), groups = testDimensions(4)), Right(()))
    assertEquals(words(memory, out, 0, 4), Vec(1L, 1L, 0L, 0L))
  }

  test("a linked kernel runs past the sub_group_barrier import") {
    val user = spv"""
      $openCL
      OpCapability Linkage
      OpEntryPoint Kernel %main "sync"
      OpDecorate %barrier LinkageAttributes "_Z17sub_group_barrierj" Import
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %ptr = OpTypePointer CrossWorkgroup %uint
      %main_fn = OpTypeFunction %void %ptr
      %barrier_fn = OpTypeFunction %void %uint
      %fence = OpConstant %uint 1
      %done = OpConstant %uint 7
      %barrier = OpFunction %void None %barrier_fn
      %flags = OpFunctionParameter %uint
      OpFunctionEnd
      %main = OpFunction %void None %main_fn
      %out = OpFunctionParameter %ptr
      %entry = OpLabel
      %call = OpFunctionCall %void %barrier %fence
      OpStore %out %done
      OpReturn
      OpFunctionEnd
    """
    val linked = link(user, provider).value
    val memory = RegionMemory()
    val out    = memory.allocate(4L)
    assertEquals(run(linked, "sync", memory, Vec(buffer(out)), groups = testDimensions(4)), Right(()))
    assertEquals(words(memory, out, 0, 1), Vec(7L))
  }

  test("a symbol exported twice is rejected") {
    assert(link(provider, provider).left.exists(_.message.contains("exported more than once")))
  }

  test("an import must match the type of its export") {
    val mismatched = spv"""
      $openCL
      OpCapability Linkage
      OpDecorate %add LinkageAttributes "add" Import
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %add_fn = OpTypeFunction %void %uint
      %add = OpFunction %void None %add_fn
      %a = OpFunctionParameter %uint
      OpFunctionEnd
    """
    assert(link(mismatched, provider).left.exists(_.message.contains("does not match")))
  }

  test("pointer parameters match across pointee types of the same storage class") {
    val importer = spv"""
      $openCL
      OpCapability Linkage
      OpDecorate %fill LinkageAttributes "fill" Import
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %uptr = OpTypePointer Generic %uint
      %fill_fn = OpTypeFunction %void %uptr
      %fill = OpFunction %void None %fill_fn
      %p = OpFunctionParameter %uptr
      OpFunctionEnd
    """
    val exporter = spv"""
      $openCL
      OpCapability Linkage
      OpDecorate %fill LinkageAttributes "fill" Export
      %void = OpTypeVoid
      %float = OpTypeFloat 32
      %fptr = OpTypePointer Generic %float
      %fill_fn = OpTypeFunction %void %fptr
      %fill = OpFunction %void None %fill_fn
      %p = OpFunctionParameter %fptr
      %entry = OpLabel
      OpReturn
      OpFunctionEnd
    """
    assertEquals(symbols(link(importer, exporter).value, LinkageType.Import), Set.empty[String])
  }

  test("pointer parameters in different storage classes do not match") {
    val importer = spv"""
      $openCL
      OpCapability Linkage
      OpDecorate %fill LinkageAttributes "fill" Import
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %uptr = OpTypePointer Generic %uint
      %fill_fn = OpTypeFunction %void %uptr
      %fill = OpFunction %void None %fill_fn
      %p = OpFunctionParameter %uptr
      OpFunctionEnd
    """
    val exporter = spv"""
      $openCL
      OpCapability Linkage
      OpDecorate %fill LinkageAttributes "fill" Export
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %uptr = OpTypePointer CrossWorkgroup %uint
      %fill_fn = OpTypeFunction %void %uptr
      %fill = OpFunction %void None %fill_fn
      %p = OpFunctionParameter %uptr
      %entry = OpLabel
      OpReturn
      OpFunctionEnd
    """
    assert(link(importer, exporter).left.exists(_.message.contains("does not match")))
  }

  test("modules with different memory models are rejected") {
    val logical = spv"""
      OpCapability Shader
      OpMemoryModel Logical GLSL450
    """
    assert(link(provider, logical).isLeft)
  }

  test("duplicate LinkOnceODR definitions collapse into the first") {
    val shared = spv"""
      $openCL
      OpCapability Linkage
      OpDecorate %add LinkageAttributes "add" LinkOnceODR
      %uint = OpTypeInt 32 0
      %add_fn = OpTypeFunction %uint %uint %uint
      %add = OpFunction %uint None %add_fn
      %a = OpFunctionParameter %uint
      %b = OpFunctionParameter %uint
      %add_entry = OpLabel
      %result = OpIAdd %uint %a %b
      OpReturnValue %result
      OpFunctionEnd
    """
    val counters = spv"""
      $openCL
      OpCapability Linkage
      OpDecorate %counter LinkageAttributes "counter" Export
      %uint = OpTypeInt 32 0
      %ptr = OpTypePointer CrossWorkgroup %uint
      %forty_two = OpConstant %uint 42
      %counter = OpVariable %ptr CrossWorkgroup %forty_two
    """
    val linked = link(shared, shared, caller, counters).value
    assertEquals(linked.instructions.count(_.opcode == "OpFunction"), 2)
    assertEquals(symbols(linked, LinkageType.Import), Set.empty[String])
  }

  test("collision-free links keep every entry point name") {
    assertEquals(
      Linkage.link(Vector(caller, provider), OpenCLBuiltins.supplies).value._2,
      Vector(Backend.LinkedKernel(0, "call", "call"))
    )
  }

  test("entry points sharing a name are renamed by module order") {
    val doubler = spv"""
      $openCL
      OpCapability Linkage
      OpEntryPoint Kernel %main "k"
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %out_ptr = OpTypePointer CrossWorkgroup %uint
      %entry_fn = OpTypeFunction %void %out_ptr %uint
      %main = OpFunction %void None %entry_fn
      %out = OpFunctionParameter %out_ptr
      %x = OpFunctionParameter %uint
      %entry = OpLabel
      %doubled = OpIAdd %uint %x %x
      OpStore %out %doubled
      OpReturn
      OpFunctionEnd
    """
    val shifter = spv"""
      $openCL
      OpCapability Linkage
      OpEntryPoint Kernel %main "k"
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %out_ptr = OpTypePointer CrossWorkgroup %uint
      %entry_fn = OpTypeFunction %void %out_ptr %uint
      %hundred = OpConstant %uint 100
      %main = OpFunction %void None %entry_fn
      %out = OpFunctionParameter %out_ptr
      %x = OpFunctionParameter %uint
      %entry = OpLabel
      %shifted = OpIAdd %uint %x %hundred
      OpStore %out %shifted
      OpReturn
      OpFunctionEnd
    """
    val (linked, kernels) = Linkage.link(Vector(doubler, shifter), OpenCLBuiltins.supplies).value
    assertEquals(kernels, Vector(Backend.LinkedKernel(0, "k", "k"), Backend.LinkedKernel(1, "k", "k.1")))
    val memory = RegionMemory()
    val first  = memory.allocate(4L)
    val second = memory.allocate(4L)
    assertEquals(run(linked, "k", memory, Vec(buffer(first), scalar(3L, 4))), Right(()))
    assertEquals(run(linked, "k.1", memory, Vec(buffer(second), scalar(3L, 4))), Right(()))
    assertEquals(word(memory, first, 0), 6L)
    assertEquals(word(memory, second, 0), 103L)
  }
}
