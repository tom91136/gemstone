package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.bits.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*

class VulkanDescriptorSuite extends Suite {
  for (
    (name, operation, expected, version) <- Seq(
      ("OpFMul", "OpFMul %float %a %b", 10.0, "1.3"),
      ("GLSL.std.450 Sin", "OpExtInst %float %glsl Sin %a", 4.0 + math.sin(2.0), "1.3"),
      ("GLSL.std.450 Pow", "OpExtInst %float %glsl Pow %a %b", 12.0, "1.6")
    )
  )
    test(s"$name reads a uniform buffer and writes a storage buffer in SPIR-V $version") {
      // before SPIR-V 1.4 the entry point interface omits descriptor variables
      val module = spv"""
        ; Version: $version
        $vulkan
        OpEntryPoint GLCompute %main "main" ${if (version == "1.6") "%out %args" else ""}
        OpExecutionMode %main LocalSize 1 1 1
        OpDecorate %floats ArrayStride 4
        OpDecorate %result Block
        OpMemberDecorate %result 0 Offset 0
        OpDecorate %out DescriptorSet 0
        OpDecorate %out Binding 0
        OpDecorate %inputs Block
        OpMemberDecorate %inputs 0 Offset 0
        OpMemberDecorate %inputs 1 Offset 4
        OpMemberDecorate %inputs 2 Offset 8
        OpDecorate %args DescriptorSet 0
        OpDecorate %args Binding 1
        %void = OpTypeVoid
        %fn = OpTypeFunction %void
        %float = OpTypeFloat 32
        %int = OpTypeInt 32 1
        %floats = OpTypeRuntimeArray %float
        %result = OpTypeStruct %floats
        %result_ptr = OpTypePointer StorageBuffer %result
        %inputs = OpTypeStruct %float %float %float
        %inputs_ptr = OpTypePointer Uniform %inputs
        %uniform_float_ptr = OpTypePointer Uniform %float
        %storage_float_ptr = OpTypePointer StorageBuffer %float
        %out = OpVariable %result_ptr StorageBuffer
        %args = OpVariable %inputs_ptr Uniform
        %zero = OpConstant %int 0
        %one = OpConstant %int 1
        %two = OpConstant %int 2
        %main = OpFunction %void None %fn
        %entry = OpLabel
        %a_ptr = OpAccessChain %uniform_float_ptr %args %zero
        %a = OpLoad %float %a_ptr
        %b_ptr = OpAccessChain %uniform_float_ptr %args %one
        %b = OpLoad %float %b_ptr
        %c_ptr = OpAccessChain %uniform_float_ptr %args %two
        %c = OpLoad %float %c_ptr
        %value = $operation
        %sum = OpFAdd %float %value %c
        %out_ptr = OpAccessChain %storage_float_ptr %out %zero %zero
        OpStore %out_ptr %sum
        OpReturn
        OpFunctionEnd
      """
      assertEquals(
        load(module).value.kernel("main").value.parameters.map(_.kind),
        Vec(KernelMetadata.ParameterKind.Allocation, KernelMetadata.ParameterKind.Allocation)
      )
      val memory = RegionMemory()
      val region = memory.allocate(16L)
      writeWords(memory, region, 4, single(2f), single(3f), single(4f))
      assertEquals(run(module, "main", memory, Vec(buffer(region), buffer(region, 4L))), Right(()))
      assertEqualsDouble(java.lang.Float.intBitsToFloat(word(memory, region, 0).toInt).toDouble, expected, 0.000001)
    }

  test("runtime array length and robust accesses use descriptor bounds within a shared allocation") {
    val module = spv"""
      $vulkan
      OpEntryPoint GLCompute %main "main" %in %out
      OpExecutionMode %main LocalSize 1 1 1
      OpDecorate %words ArrayStride 4
      OpDecorate %block Block
      OpMemberDecorate %block 0 Offset 0
      OpMemberDecorate %block 1 Offset 4
      OpDecorate %in DescriptorSet 0
      OpDecorate %in Binding 0
      OpDecorate %out DescriptorSet 0
      OpDecorate %out Binding 1
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %words = OpTypeRuntimeArray %uint
      %block = OpTypeStruct %uint %words
      %block_ptr = OpTypePointer StorageBuffer %block
      %uint_ptr = OpTypePointer StorageBuffer %uint
      %fn = OpTypeFunction %void
      %zero = OpConstant %uint 0
      %one = OpConstant %uint 1
      %two = OpConstant %uint 2
      %in = OpVariable %block_ptr StorageBuffer
      %out = OpVariable %block_ptr StorageBuffer
      %main = OpFunction %void None %fn
      %entry = OpLabel
      %length = OpArrayLength %uint %in 1
      %out_length = OpAccessChain %uint_ptr %out %zero
      OpStore %out_length %length
      %in_first = OpAccessChain %uint_ptr %in %one %zero
      %first = OpLoad %uint %in_first
      %out_first = OpAccessChain %uint_ptr %out %one %zero
      OpStore %out_first %first
      %in_beyond = OpAccessChain %uint_ptr %in %one %one
      %beyond = OpLoad %uint %in_beyond
      OpStore %in_beyond %zero
      %out_second = OpAccessChain %uint_ptr %out %one %one
      OpStore %out_second %beyond
      %previous = OpAtomicIAdd %uint %in_beyond %one %zero %one
      %out_third = OpAccessChain %uint_ptr %out %one %two
      OpStore %out_third %previous
      OpReturn
      OpFunctionEnd
    """
    val memory = RegionMemory()
    val region = memory.allocate(32L)
    writeWords(memory, region, 8, 99L, 77L)
    val arguments = Vec(buffer(region, 4L, Some(8L)), buffer(region, 16L, Some(16L)))
    assertEquals(run(module, "main", memory, arguments), Right(()))
    assertEquals(word(memory, region, 12), 77L)
    assertEquals(words(memory, region, offset = 16, count = 4), Vec(1L, 99L, 0L, 0L))
  }

  test("a storage buffer pointer reloaded from a function variable or its copy keeps its bounds and matrix layout") {
    val module = spv"""
      OpCapability VariablePointers
      $vulkan
      OpEntryPoint GLCompute %main "main" %in %out
      OpExecutionMode %main LocalSize 1 1 1
      OpDecorate %words ArrayStride 4
      OpDecorate %block Block
      OpMemberDecorate %block 0 Offset 0
      OpMemberDecorate %block 0 RowMajor
      OpMemberDecorate %block 0 MatrixStride 16
      OpMemberDecorate %block 1 Offset 32
      OpDecorate %in DescriptorSet 0
      OpDecorate %in Binding 0
      OpDecorate %out DescriptorSet 0
      OpDecorate %out Binding 1
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %float = OpTypeFloat 32
      %column = OpTypeVector %float 2
      %matrix = OpTypeMatrix %column 2
      %words = OpTypeRuntimeArray %uint
      %block = OpTypeStruct %matrix %words
      %block_ptr = OpTypePointer StorageBuffer %block
      %matrix_ptr = OpTypePointer StorageBuffer %matrix
      %uint_ptr = OpTypePointer StorageBuffer %uint
      %block_holder_ptr = OpTypePointer Function %block_ptr
      %matrix_holder_ptr = OpTypePointer Function %matrix_ptr
      %fn = OpTypeFunction %void
      %zero = OpConstant %uint 0
      %one = OpConstant %uint 1
      %two = OpConstant %uint 2
      %in = OpVariable %block_ptr StorageBuffer
      %out = OpVariable %block_ptr StorageBuffer
      %main = OpFunction %void None %fn
      %entry = OpLabel
      %block_holder = OpVariable %block_holder_ptr Function
      %matrix_holder = OpVariable %matrix_holder_ptr Function
      %copied_holder = OpVariable %block_holder_ptr Function
      OpStore %block_holder %in
      %reloaded = OpLoad %block_ptr %block_holder
      %length = OpArrayLength %uint %reloaded 1
      OpCopyMemory %copied_holder %block_holder
      %copied = OpLoad %block_ptr %copied_holder
      %beyond_ptr = OpAccessChain %uint_ptr %copied %one %two
      %beyond = OpLoad %uint %beyond_ptr
      %member = OpAccessChain %matrix_ptr %reloaded %zero
      OpStore %matrix_holder %member
      %reloaded_member = OpLoad %matrix_ptr %matrix_holder
      %matrix_value = OpLoad %matrix %reloaded_member
      %element = OpCompositeExtract %float %matrix_value 0 1
      %element_bits = OpBitcast %uint %element
      %out0 = OpAccessChain %uint_ptr %out %one %zero
      OpStore %out0 %length
      %out1 = OpAccessChain %uint_ptr %out %one %one
      OpStore %out1 %beyond
      %out2 = OpAccessChain %uint_ptr %out %one %two
      OpStore %out2 %element_bits
      OpReturn
      OpFunctionEnd
    """
    val memory = RegionMemory()
    val in     = memory.allocate(48L)
    val out    = memory.allocate(48L)
    writeWords(memory, in, 4, single(2f))
    writeWords(memory, in, 16, single(3f))
    writeWords(memory, in, 32, 11L, 13L, 99L)
    assertEquals(run(module, "main", memory, Vec(buffer(in, range = Some(40L)), buffer(out))), Right(()))
    assertEquals(words(memory, out, offset = 32, count = 3), Vec(2L, 0L, single(3f)))
  }

  test("a descriptor array pointer reloaded from a function variable indexes its elements") {
    val module = spv"""
      OpCapability VariablePointers
      $vulkan
      OpEntryPoint GLCompute %main "main" %buffers %out
      OpExecutionMode %main LocalSize 1 1 1
      OpDecorate %block Block
      OpMemberDecorate %block 0 Offset 0
      OpDecorate %buffers DescriptorSet 0
      OpDecorate %buffers Binding 0
      OpDecorate %out DescriptorSet 0
      OpDecorate %out Binding 1
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %two = OpConstant %uint 2
      %block = OpTypeStruct %uint
      %blocks = OpTypeArray %block %two
      %block_ptr = OpTypePointer StorageBuffer %block
      %blocks_ptr = OpTypePointer StorageBuffer %blocks
      %uint_ptr = OpTypePointer StorageBuffer %uint
      %holder_ptr = OpTypePointer Function %blocks_ptr
      %fn = OpTypeFunction %void
      %zero = OpConstant %uint 0
      %one = OpConstant %uint 1
      %buffers = OpVariable %blocks_ptr StorageBuffer
      %out = OpVariable %block_ptr StorageBuffer
      %main = OpFunction %void None %fn
      %entry = OpLabel
      %holder = OpVariable %holder_ptr Function
      OpStore %holder %buffers
      %reloaded = OpLoad %blocks_ptr %holder
      %second = OpAccessChain %uint_ptr %reloaded %one %zero
      %value = OpLoad %uint %second
      %out_value = OpAccessChain %uint_ptr %out %zero
      OpStore %out_value %value
      OpReturn
      OpFunctionEnd
    """
    val memory = RegionMemory()
    val first  = memory.allocate(16L)
    val second = memory.allocate(16L)
    val out    = memory.allocate(4L)
    writeWords(memory, first, 0, 5L, 6L, 7L, 8L)
    writeWords(memory, second, 0, 42L)
    assertEquals(run(module, "main", memory, Vec(buffer(first), buffer(second), buffer(out))), Right(()))
    assertEquals(word(memory, out, 0), 42L)
  }

  test("OpPtrAccessChain and OpPtrDiff step by the ArrayStride of the base pointer type") {
    val module = spv"""
      OpCapability VariablePointers
      $vulkan
      OpEntryPoint GLCompute %main "main" %in %out
      OpExecutionMode %main LocalSize 1 1 1
      OpDecorate %elements ArrayStride 12
      OpDecorate %element_ptr ArrayStride 12
      OpMemberDecorate %element 0 Offset 0
      OpDecorate %block Block
      OpMemberDecorate %block 0 Offset 0
      OpDecorate %result Block
      OpMemberDecorate %result 0 Offset 0
      OpMemberDecorate %result 1 Offset 4
      OpDecorate %in DescriptorSet 0
      OpDecorate %in Binding 0
      OpDecorate %out DescriptorSet 0
      OpDecorate %out Binding 1
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %int = OpTypeInt 32 1
      %uint3 = OpTypeVector %uint 3
      %element = OpTypeStruct %uint3
      %elements = OpTypeRuntimeArray %element
      %block = OpTypeStruct %elements
      %result = OpTypeStruct %uint %int
      %block_ptr = OpTypePointer StorageBuffer %block
      %result_ptr = OpTypePointer StorageBuffer %result
      %element_ptr = OpTypePointer StorageBuffer %element
      %uint_ptr = OpTypePointer StorageBuffer %uint
      %int_ptr = OpTypePointer StorageBuffer %int
      %fn = OpTypeFunction %void
      %zero = OpConstant %uint 0
      %one = OpConstant %uint 1
      %two = OpConstant %uint 2
      %in = OpVariable %block_ptr StorageBuffer
      %out = OpVariable %result_ptr StorageBuffer
      %main = OpFunction %void None %fn
      %entry = OpLabel
      %first = OpAccessChain %element_ptr %in %zero %zero
      %next = OpPtrAccessChain %element_ptr %first %one
      %component = OpAccessChain %uint_ptr %next %zero %zero
      %value = OpLoad %uint %component
      %third = OpAccessChain %element_ptr %in %zero %two
      %distance = OpPtrDiff %int %third %first
      %out_value = OpAccessChain %uint_ptr %out %zero
      OpStore %out_value %value
      %out_distance = OpAccessChain %int_ptr %out %one
      OpStore %out_distance %distance
      OpReturn
      OpFunctionEnd
    """
    val memory = RegionMemory()
    val in     = memory.allocate(48L)
    val out    = memory.allocate(8L)
    writeWords(memory, in, 0, Seq.range(10L, 22L)*)
    assertEquals(run(module, "main", memory, Vec(buffer(in), buffer(out))), Right(()))
    assertEquals(words(memory, out, offset = 0, count = 2), Vec(13L, 2L))
  }

  for (access <- Seq("OpImageWrite %view %where %texel", "%read = OpImageRead %float4 %view %where"))
    test(
      s"${access.split(' ').find(_.startsWith("OpImage")).get} of a storage image view without texel encoding faults"
    ) {
      val module = spv"""
        $vulkan
        OpEntryPoint GLCompute %main "main" %image
        OpExecutionMode %main LocalSize 1 1 1
        OpDecorate %image DescriptorSet 0
        OpDecorate %image Binding 0
        %void = OpTypeVoid
        %int = OpTypeInt 32 1
        %float = OpTypeFloat 32
        %float4 = OpTypeVector %float 4
        %int2 = OpTypeVector %int 2
        %storage = OpTypeImage %float 2D 0 0 0 2 R32f
        %storage_ptr = OpTypePointer UniformConstant %storage
        %fn = OpTypeFunction %void
        %image = OpVariable %storage_ptr UniformConstant
        %zero = OpConstant %int 0
        %one = OpConstant %float 1
        %where = OpConstantComposite %int2 %zero %zero
        %texel = OpConstantComposite %float4 %one %one %one %one
        %main = OpFunction %void None %fn
        %entry = OpLabel
        %view = OpLoad %storage %image
        $access
        OpReturn
        OpFunctionEnd
      """
      val view       = ImageView(ImageDescriptor.Format.D16Unorm, Vec(ImageLevel(1)), 8L)
      val memory     = RegionMemory()
      val size       = view.size.toLong.refine[Long > 0].value
      val descriptor = memory.allocate(size)
      val texels     = memory.allocate(8L)
      val relocation = writeImage(memory, descriptor, 0, view, texels)
      val result     =
        run(module, "main", memory, Vec(buffer(descriptor, range = Some(size))), relocations = Vec(relocation))
      assert(result.leftValue.message.contains("has no texel"), result)
      assert(memory.bytes(texels).forall(_ == 0))
    }

  test("OpCopyLogical copies values between distinct decorated structure types") {
    val module = spv"""
      $vulkan
      OpEntryPoint GLCompute %main "main" %out
      OpExecutionMode %main LocalSize 1 1 1
      OpDecorate %block Block
      OpMemberDecorate %block 0 Offset 0
      OpMemberDecorate %block 1 Offset 16
      OpDecorate %out DescriptorSet 0
      OpDecorate %out Binding 0
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %plain = OpTypeStruct %uint %uint
      %block = OpTypeStruct %uint %uint
      %block_ptr = OpTypePointer StorageBuffer %block
      %fn = OpTypeFunction %void
      %first = OpConstant %uint 19
      %second = OpConstant %uint 23
      %value = OpConstantComposite %plain %first %second
      %out = OpVariable %block_ptr StorageBuffer
      %main = OpFunction %void None %fn
      %entry = OpLabel
      %copy = OpCopyLogical %block %value
      OpStore %out %copy
      OpReturn
      OpFunctionEnd
    """
    val memory = RegionMemory()
    val region = memory.allocate(20L)
    assertEquals(run(module, "main", memory, Vec(buffer(region, 0L, Some(20L)))), Right(()))
    assertEquals(words(memory, region, offset = 0, count = 5), Vec(19L, 0L, 0L, 0L, 23L))
  }

  private val aliasedStorageImages = spv"""
      $vulkan
      OpEntryPoint GLCompute %main "main" %writer %reader %out
      OpExecutionMode %main LocalSize 1 1 1
      OpDecorate %writer DescriptorSet 0
      OpDecorate %writer Binding 0
      OpDecorate %reader DescriptorSet 0
      OpDecorate %reader Binding 1
      OpDecorate %out DescriptorSet 0
      OpDecorate %out Binding 2
      OpDecorate %result Block
      OpMemberDecorate %result 0 Offset 0
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %int = OpTypeInt 32 1
      %uint4 = OpTypeVector %uint 4
      %int2 = OpTypeVector %int 2
      %image = OpTypeImage %uint 2D 0 0 0 2 R32ui
      %image_ptr = OpTypePointer UniformConstant %image
      %texel_ptr = OpTypePointer Image %uint
      %result = OpTypeStruct %uint4
      %result_ptr = OpTypePointer StorageBuffer %result
      %fn = OpTypeFunction %void
      %writer = OpVariable %image_ptr UniformConstant
      %reader = OpVariable %image_ptr UniformConstant
      %out = OpVariable %result_ptr StorageBuffer
      %int_0 = OpConstant %int 0
      %int_1 = OpConstant %int 1
      %uint_0 = OpConstant %uint 0
      %device = OpConstant %uint 1
      %uint_1 = OpConstant %uint 1
      %uint_77 = OpConstant %uint 77
      %where = OpConstantComposite %int2 %int_1 %int_0
      %texel = OpConstantComposite %uint4 %uint_77 %uint_0 %uint_0 %uint_1
      %main = OpFunction %void None %fn
      %entry = OpLabel
      %written = OpLoad %image %writer
      OpImageWrite %written %where %texel
      %pointer = OpImageTexelPointer %texel_ptr %reader %where %uint_0
      %previous = OpAtomicIAdd %uint %pointer %device %uint_0 %uint_1
      %read = OpLoad %image %reader
      %value = OpImageRead %uint4 %read %where
      %result_value = OpCompositeInsert %uint4 %previous %value 3
      OpStore %out %result_value
      OpReturn
      OpFunctionEnd
    """

  /** Runs [[aliasedStorageImages]] with both bindings over one descriptor for `view`. */
  private def runAliased(view: ImageView): (Either[Fault, Unit], RegionMemory, AllocationId, AllocationId) = {
    val memory     = RegionMemory()
    val size       = view.size.toLong.refine[Long > 0].value
    val descriptor = memory.allocate(size)
    val texels     = memory.allocate(view.texelBytes.max(8L).refine[Long > 0].value)
    val out        = memory.allocate(16L)
    val relocation = writeImage(memory, descriptor, 0, view, texels)
    val arguments  = Vec(buffer(descriptor, range = Some(size)), buffer(descriptor, range = Some(size)), buffer(out))
    (run(aliasedStorageImages, "main", memory, arguments, relocations = Vec(relocation)), memory, texels, out)
  }

  test("storage image descriptors aliasing one image observe each other's writes and atomics") {
    val (result, memory, texels, out) = runAliased(ImageView(ImageFormat.R32ui, Vec(ImageLevel(2)), 8L))
    assertEquals(result, Right(()))
    assertEquals(
      words(memory, out, offset = 0, count = 4),
      Vec(78L, 0L, 0L, 77L),
      "read texel with the atomic's prior value"
    )
    assertEquals(words(memory, texels, offset = 0, count = 2), Vec(0L, 78L))
  }

  for (
    (name, view) <- Seq(
      "width 0"                    -> ImageView(ImageFormat.R32ui, Vec(ImageLevel(0)), 8L),
      "2D width 16385"             -> ImageView(ImageFormat.R32ui, Vec(ImageLevel(16385)), 16385L * 4),
      "3D width 2049"              -> ImageView(ImageFormat.R32ui, Vec(ImageLevel(2049)), 2049L * 4, volume = true),
      "2049 layers"                -> ImageView(ImageFormat.R32ui, Vec(ImageLevel(2)), 2049L * 8, slices = 2049),
      "2 samples"                  -> ImageView(ImageFormat.R32ui, Vec(ImageLevel(2)), 16L, samples = 2),
      "texels past the allocation" -> ImageView(ImageFormat.R32ui, Vec(ImageLevel(2)), 4L)
    )
  )
    test(s"a storage image descriptor with $name is rejected") {
      val (result, memory, texels, _) = runAliased(view)
      assert(result.leftValue.message.contains("image level exceeds texel allocation"), result)
      assert(memory.bytes(texels).forall(_ == 0))
    }
}
