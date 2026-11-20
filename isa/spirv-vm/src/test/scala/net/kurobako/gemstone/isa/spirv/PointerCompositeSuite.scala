package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.*

class PointerCompositeSuite extends Suite {

  private val types = """
    %void = OpTypeVoid
    %uint = OpTypeInt 32 0
    %bool = OpTypeBool
    %uint_0 = OpConstant %uint 0
    %uint_1 = OpConstant %uint 1
    %uint_2 = OpConstant %uint 2
    %uint_3 = OpConstant %uint 3
    %global_ptr = OpTypePointer CrossWorkgroup %uint
    %private_ptr = OpTypePointer Function %uint
    %wrapper = OpTypeStruct %global_ptr %uint
    %wrapper_ptr = OpTypePointer Function %wrapper
    %global_wrapper_ptr = OpTypePointer CrossWorkgroup %wrapper
    %pair = OpTypeArray %global_ptr %uint_2
    %pair_ptr = OpTypePointer Function %pair
    %nested = OpTypeStruct %uint %pair
    %fn = OpTypeFunction %void %global_ptr %global_ptr
    %take_fn = OpTypeFunction %uint %wrapper
    %make_fn = OpTypeFunction %wrapper %global_ptr %uint
    %private_wrapper = OpTypeStruct %private_ptr %uint
    %private_wrapper_ptr = OpTypePointer Function %private_wrapper
    %dims = OpTypeArray %uint %uint_2
    %julia = OpTypeStruct %global_ptr %dims
    %julia_ptr = OpTypePointer Function %julia
    %element_fn = OpTypeFunction %uint %julia_ptr %uint
    %wrapper_null = OpConstantNull %wrapper
    %pair_null = OpConstantNull %pair
  """

  private def kernel(body: String, extra: String = ""): Module = assemble(s"""
    $openCL
    OpEntryPoint Kernel %main "main"
    $types
    $extra
    %main = OpFunction %void None %fn
    %in = OpFunctionParameter %global_ptr
    %out = OpFunctionParameter %global_ptr
    %entry = OpLabel
    $body
    OpReturn
    OpFunctionEnd
  """)

  private def check(module: Module, expected: Seq[Long]): Unit = {
    val memory = RegionMemory()
    val in     = memory.allocate(32L)
    val out    = memory.allocate(32L)
    writeWords(memory, in, 0, 11L, 22L, 33L, 44L)
    assertEquals(run(module, "main", memory, Vec(buffer(in), buffer(out))), Right(()))
    assertEquals(words(memory, out, offset = 0, count = expected.size), Vec.from(expected))
  }

  test("a pointer inserted into a struct is extracted and used") {
    check(
      kernel("""
        %a = OpCompositeInsert %wrapper %in %wrapper_null 0
        %b = OpCompositeInsert %wrapper %uint_2 %a 1
        %p = OpCompositeExtract %global_ptr %b 0
        %n = OpCompositeExtract %uint %b 1
        %v = OpLoad %uint %p
        %sum = OpIAdd %uint %v %n
        OpStore %out %sum
      """),
      Seq(13L)
    )
  }

  test("a struct constructed from a pointer keeps it usable for stores") {
    check(
      kernel("""
        %w = OpCompositeConstruct %wrapper %out %uint_3
        %p = OpCompositeExtract %global_ptr %w 0
        %n = OpCompositeExtract %uint %w 1
        OpStore %p %n
      """),
      Seq(3L)
    )
  }

  test("an array of pointers is constructed, inserted into and extracted from") {
    check(
      kernel("""
        %a = OpCompositeConstruct %pair %in %in
        %b = OpCompositeInsert %pair %out %a 1
        %first = OpCompositeExtract %global_ptr %b 0
        %second = OpCompositeExtract %global_ptr %b 1
        %v = OpLoad %uint %first
        OpStore %second %v
      """),
      Seq(11L)
    )
  }

  test("a struct nesting an array of pointers extracts a sub-composite and a leaf") {
    check(
      kernel(
        """
        %a = OpCompositeConstruct %pair %out %in
        %w = OpCompositeConstruct %nested %uint_3 %a
        %inner = OpCompositeExtract %pair %w 1
        %p = OpCompositeExtract %global_ptr %inner 1
        %q = OpCompositeExtract %global_ptr %w 1 0
        %v = OpLoad %uint %p
        OpStore %q %v
      """
      ),
      Seq(11L)
    )
  }

  test("a struct of a pointer round trips through private memory") {
    check(
      kernel("""
        %slot = OpVariable %wrapper_ptr Function
        %w = OpCompositeConstruct %wrapper %in %uint_1
        OpStore %slot %w
        %loaded = OpLoad %wrapper %slot
        %p = OpCompositeExtract %global_ptr %loaded 0
        %n = OpCompositeExtract %uint %loaded 1
        %v = OpLoad %uint %p
        %sum = OpIAdd %uint %v %n
        OpStore %out %sum
      """),
      Seq(12L)
    )
  }

  test("a struct of a pointer round trips through global memory") {
    val module = kernel(
      """
        %w = OpCompositeConstruct %wrapper %in %uint_2
        %slot = OpBitcast %global_wrapper_ptr %out
        OpStore %slot %w
        %loaded = OpLoad %wrapper %slot
        %p = OpCompositeExtract %global_ptr %loaded 0
        %v = OpLoad %uint %p
        %again = OpPtrAccessChain %global_ptr %out %uint_3
        OpStore %again %v
      """
    )
    val memory = RegionMemory()
    val in     = memory.allocate(32L)
    val out    = memory.allocate(32L)
    writeWords(memory, in, 0, 11L, 22L, 33L, 44L)
    assertEquals(run(module, "main", memory, Vec(buffer(in), buffer(out))), Right(()))
    assertEquals(word(memory, out, 3 * 4), 11L)
    assertEquals(word(memory, out, 2 * 4), 2L)
  }

  test("a struct of a pointer selected by OpPhi keeps its pointer") {
    check(
      kernel(
        """
        %w0 = OpCompositeConstruct %wrapper %in %uint_1
        %w1 = OpCompositeConstruct %wrapper %in %uint_2
        %cond = OpIEqual %bool %uint_1 %uint_1
        OpSelectionMerge %join None
        OpBranchConditional %cond %left %right
        %left = OpLabel
        OpBranch %join
        %right = OpLabel
        OpBranch %join
        %join = OpLabel
        %w = OpPhi %wrapper %w0 %left %w1 %right
        %p = OpCompositeExtract %global_ptr %w 0
        %n = OpCompositeExtract %uint %w 1
        %v = OpLoad %uint %p
        %sum = OpIAdd %uint %v %n
        OpStore %out %sum
      """
      ),
      Seq(12L)
    )
  }

  test("a struct of a pointer passes by value through a call and returns") {
    check(
      kernel(
        """
        %w = OpFunctionCall %wrapper %make %in %uint_3
        %sum = OpFunctionCall %uint %take %w
        OpStore %out %sum
      """,
        extra = """
        %make = OpFunction %wrapper None %make_fn
        %make_p = OpFunctionParameter %global_ptr
        %make_n = OpFunctionParameter %uint
        %make_entry = OpLabel
        %made = OpCompositeConstruct %wrapper %make_p %make_n
        OpReturnValue %made
        OpFunctionEnd
        %take = OpFunction %uint None %take_fn
        %take_w = OpFunctionParameter %wrapper
        %take_entry = OpLabel
        %take_p = OpCompositeExtract %global_ptr %take_w 0
        %take_n = OpCompositeExtract %uint %take_w 1
        %take_v = OpLoad %uint %take_p
        %take_sum = OpIAdd %uint %take_v %take_n
        OpReturnValue %take_sum
        OpFunctionEnd
      """
      ),
      Seq(14L)
    )
  }

  test("a null pointer in a struct reads back as null") {
    check(
      kernel("""
        %p = OpCompositeExtract %global_ptr %wrapper_null 0
        %null = OpConvertPtrToU %uint %p
        OpStore %out %null
      """),
      Seq(0L)
    )
  }

  test("a Julia array wrapper of a pointer and dimensions is built, stored and indexed through memory") {
    check(
      kernel(
        """
        %dimensions = OpCompositeConstruct %dims %uint_2 %uint_3
        %array = OpCompositeConstruct %julia %in %dimensions
        %slot = OpVariable %julia_ptr Function
        OpStore %slot %array
        %element = OpFunctionCall %uint %index %slot %uint_1
        OpStore %out %element
      """,
        extra = """
        %index = OpFunction %uint None %element_fn
        %index_slot = OpFunctionParameter %julia_ptr
        %index_at = OpFunctionParameter %uint
        %index_entry = OpLabel
        %index_array = OpLoad %julia %index_slot
        %index_base = OpCompositeExtract %global_ptr %index_array 0
        %index_stride = OpCompositeExtract %uint %index_array 1 1
        %index_offset = OpIMul %uint %index_at %index_stride
        %index_at_ptr = OpPtrAccessChain %global_ptr %index_base %index_offset
        %index_value = OpLoad %uint %index_at_ptr
        OpReturnValue %index_value
        OpFunctionEnd
      """
      ),
      Seq(44L)
    )
  }

  test("a struct holding a pointer to private memory keeps it usable after a store and load") {
    check(
      kernel("""
        %target = OpVariable %private_ptr Function
        %slot = OpVariable %private_wrapper_ptr Function
        OpStore %target %uint_1
        %w = OpCompositeConstruct %private_wrapper %target %uint_3
        OpStore %slot %w
        %loaded = OpLoad %private_wrapper %slot
        %p = OpCompositeExtract %private_ptr %loaded 0
        %n = OpCompositeExtract %uint %loaded 1
        OpStore %p %n
        %result = OpLoad %uint %target
        OpStore %out %result
      """),
      Seq(3L)
    )
  }
}
