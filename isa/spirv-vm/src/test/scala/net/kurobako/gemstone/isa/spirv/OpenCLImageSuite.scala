package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.bits.*
import net.kurobako.gemstone.vm.*

class OpenCLImageSuite extends munit.FunSuite {
  private val header =
    s"""OpCapability ImageBasic
       |OpCapability ImageReadWrite
       |OpCapability LiteralSampler
       |OpCapability SampledBuffer
       |$openCL""".stripMargin

  private case class Image(
      record: AllocationId,
      texels: AllocationId,
      view: ImageView,
      relocation: KernelTask.PointerRelocation
  ) {
    def argument: KernelTask.Argument = buffer(record, range = Some(view.size.toLong.unsafe[Long > 0]))
  }

  private def image(
      memory: RegionMemory,
      format: Int,
      texelBytes: Int,
      width: Int,
      height: Int = 1,
      slices: Int = 1,
      volume: Boolean = false,
      contents: Vec[Byte] = Vec.empty
  ): Image = {
    val bytes  = width * height * slices * texelBytes
    val texels = memory.allocate(bytes.toLong.unsafe[Long > 0])
    contents.zipWithIndex.foreach((value, index) => memory.array(texels)(index) = value)
    val view   = ImageView(format, Vec(ImageLevel(width, height)), bytes.toLong, slices, volume)
    val record = memory.allocate(view.size.toLong.unsafe[Long > 0])
    Image(record, texels, view, writeImage(memory, record, 0, view, texels))
  }

  private def bytes(values: Int*): Vec[Byte] = Vec.from(values.map(_.toByte))

  private def float(value: Float): Long = single(value)

  private def unorm8(value: Int): Long = single(value / 255f)

  private case class Format(
      name: String,
      code: Int,
      texelBytes: Int,
      component: String,
      texel: Vec[Byte],
      read: Vec[Long]
  )

  private val Formats = Seq(
    Format(
      "RGBA UNORM_INT8",
      ImageFormat.Rgba8,
      4,
      "%float",
      bytes(0, 51, 255, 128),
      Vec(0L, unorm8(51), unorm8(255), unorm8(128))
    ),
    Format(
      "BGRA UNORM_INT8",
      ImageDescriptor.Format.Bgra8Unorm,
      4,
      "%float",
      bytes(0, 51, 255, 128),
      Vec(unorm8(255), unorm8(51), 0L, unorm8(128))
    ),
    Format(
      "R FLOAT",
      ImageFormat.R32f,
      4,
      "%float",
      littleEndian(Seq(float(1.5f)), 32),
      Vec(float(1.5f), 0L, 0L, float(1f))
    ),
    Format("RG UNORM_INT8", ImageFormat.Rg8, 2, "%float", bytes(255, 51), Vec(unorm8(255), unorm8(51), 0L, float(1f))),
    Format("R HALF_FLOAT", ImageFormat.R16f, 2, "%float", bytes(0x00, 0x3e), Vec(float(1.5f), 0L, 0L, float(1f))),
    Format(
      "RGBA SIGNED_INT8",
      ImageFormat.Rgba8i,
      4,
      "%uint",
      bytes(0xff, 0x7f, 0x80, 0x01),
      Vec(0xffffffffL, 127L, 0xffffff80L, 1L)
    ),
    Format(
      "RGBA UNSIGNED_INT32",
      ImageFormat.Rgba32ui,
      16,
      "%uint",
      littleEndian(Seq(7L, 42L, 0xffffffffL, 65536L), 32),
      Vec(7L, 42L, 0xffffffffL, 65536L)
    ),
    Format("R UNSIGNED_INT16", ImageFormat.R16ui, 2, "%uint", bytes(0x34, 0x12), Vec(0x1234L, 0L, 0L, 1L))
  )

  for (format <- Formats)
    test(s"read_image and write_image round-trip ${format.name} texels through integer coordinates") {
      val module = spv"""
        $header
        OpEntryPoint Kernel %main "copy"
        %void = OpTypeVoid
        %uint = OpTypeInt 32 0
        %float = OpTypeFloat 32
        %v2uint = OpTypeVector %uint 2
        %v4 = OpTypeVector ${format.component} 4
        %ro = OpTypeImage %void 2D 0 0 0 0 Unknown ReadOnly
        %wo = OpTypeImage %void 2D 0 0 0 0 Unknown WriteOnly
        %v4_ptr = OpTypePointer CrossWorkgroup %v4
        %fn = OpTypeFunction %void %ro %wo %v4_ptr
        %uint_0 = OpConstant %uint 0
        %uint_1 = OpConstant %uint 1
        %where = OpConstantComposite %v2uint %uint_1 %uint_0
        %main = OpFunction %void None %fn
        %src = OpFunctionParameter %ro
        %dst = OpFunctionParameter %wo
        %out = OpFunctionParameter %v4_ptr
        %entry = OpLabel
        %texel = OpImageRead %v4 %src %where
        OpStore %out %texel
        OpImageWrite %dst %where %texel
        OpReturn
        OpFunctionEnd
      """
      val memory      = RegionMemory()
      val padding     = Vec.fill(format.texelBytes)(0x5a.toByte)
      val source      = image(memory, format.code, format.texelBytes, 2, contents = padding ++ format.texel)
      val destination = image(memory, format.code, format.texelBytes, 2)
      val out         = memory.allocate(16L)
      assertEquals(
        run(
          module,
          "copy",
          memory,
          Vec(source.argument, destination.argument, buffer(out)),
          relocations = Vec(source.relocation, destination.relocation)
        ),
        Right(())
      )
      assertEquals(words(memory, out, offset = 0, count = 4), format.read)
      assertEquals(memory.bytes(destination.texels).drop(format.texelBytes), format.texel)
    }

  private case class Query(name: String, code: Int, order: Long, dataType: Long)

  // SPIR-V ImageChannelOrder and ImageChannelDataType values; OpenCL C adds the CLK_ offsets.
  private val Queries = Seq(
    Query("RGBA UNORM_INT8", ImageFormat.Rgba8, order = 5L, dataType = 2L),
    Query("BGRA UNORM_INT8", ImageDescriptor.Format.Bgra8Unorm, order = 6L, dataType = 2L),
    Query("sRGBA UNORM_INT8", ImageDescriptor.Format.Rgba8Srgb, order = 17L, dataType = 2L),
    Query("R FLOAT", ImageFormat.R32f, order = 0L, dataType = 14L),
    Query("R HALF_FLOAT", ImageFormat.R16f, order = 0L, dataType = 13L),
    Query("RG UNSIGNED_INT16", ImageFormat.Rg16ui, order = 2L, dataType = 11L),
    Query("RGBA SNORM_INT8", ImageFormat.Rgba8Snorm, order = 5L, dataType = 0L),
    Query("RGBA SIGNED_INT16", ImageFormat.Rgba16i, order = 5L, dataType = 8L),
    Query("RGBA UNORM_INT16", ImageFormat.Rgba16, order = 5L, dataType = 3L)
  )

  for (query <- Queries) test(s"image queries report the size, channel order and data type of ${query.name} images") {
    val module = spv"""
      $header
      OpEntryPoint Kernel %main "query"
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %v2uint = OpTypeVector %uint 2
      %ro = OpTypeImage %void 2D 0 0 0 0 Unknown ReadOnly
      %uint_ptr = OpTypePointer CrossWorkgroup %uint
      %fn = OpTypeFunction %void %ro %uint_ptr
      %uint_0 = OpConstant %uint 0
      %ulong = OpTypeInt 64 0
      %ulong_2 = OpConstant %ulong 2
      %ulong_3 = OpConstant %ulong 3
      %v2uint_ptr = OpTypePointer CrossWorkgroup %v2uint
      %main = OpFunction %void None %fn
      %src = OpFunctionParameter %ro
      %out = OpFunctionParameter %uint_ptr
      %entry = OpLabel
      %size = OpImageQuerySizeLod %v2uint %src %uint_0
      %size_out = OpBitcast %v2uint_ptr %out
      OpStore %size_out %size
      %order = OpImageQueryOrder %uint %src
      %out2 = OpPtrAccessChain %uint_ptr %out %ulong_2
      OpStore %out2 %order
      %format = OpImageQueryFormat %uint %src
      %out3 = OpPtrAccessChain %uint_ptr %out %ulong_3
      OpStore %out3 %format
      OpReturn
      OpFunctionEnd
    """
    val memory = RegionMemory()
    val source = image(memory, query.code, texelBytes = 16, width = 3, height = 2)
    val out    = memory.allocate(16L)
    assertEquals(
      run(module, "query", memory, Vec(source.argument, buffer(out)), relocations = Vec(source.relocation)),
      Right(())
    )
    assertEquals(words(memory, out, offset = 0, count = 4), Vec(3L, 2L, query.order, query.dataType))
  }

  test("3D, array and buffer image queries report every extent") {
    val module = spv"""
      $header
      OpEntryPoint Kernel %main "extents"
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %v3uint = OpTypeVector %uint 3
      %volume = OpTypeImage %void 3D 0 0 0 0 Unknown ReadOnly
      %layers = OpTypeImage %void 2D 0 1 0 0 Unknown ReadOnly
      %texels = OpTypeImage %void Buffer 0 0 0 0 Unknown ReadOnly
      %v3uint_ptr = OpTypePointer CrossWorkgroup %v3uint
      %uint_ptr = OpTypePointer CrossWorkgroup %uint
      %fn = OpTypeFunction %void %volume %layers %texels %v3uint_ptr %uint_ptr
      %uint_0 = OpConstant %uint 0
      %ulong = OpTypeInt 64 0
      %ulong_1 = OpConstant %ulong 1
      %main = OpFunction %void None %fn
      %a = OpFunctionParameter %volume
      %b = OpFunctionParameter %layers
      %c = OpFunctionParameter %texels
      %out = OpFunctionParameter %v3uint_ptr
      %width = OpFunctionParameter %uint_ptr
      %entry = OpLabel
      %a_size = OpImageQuerySizeLod %v3uint %a %uint_0
      OpStore %out %a_size
      %b_size = OpImageQuerySizeLod %v3uint %b %uint_0
      %out1 = OpPtrAccessChain %v3uint_ptr %out %ulong_1
      OpStore %out1 %b_size
      %c_size = OpImageQuerySize %uint %c
      OpStore %width %c_size
      OpReturn
      OpFunctionEnd
    """
    val memory = RegionMemory()
    val volume = image(memory, ImageFormat.Rgba8, texelBytes = 4, width = 3, height = 2, slices = 4, volume = true)
    val layers = image(memory, ImageFormat.Rgba8, texelBytes = 4, width = 5, height = 6, slices = 7)
    val texels = image(memory, ImageFormat.R32f, texelBytes = 4, width = 9)
    val out    = memory.allocate(32L)
    val width  = memory.allocate(4L)
    assertEquals(
      run(
        module,
        "extents",
        memory,
        Vec(volume.argument, layers.argument, texels.argument, buffer(out), buffer(width)),
        relocations = Vec(volume.relocation, layers.relocation, texels.relocation)
      ),
      Right(())
    )
    assertEquals(words(memory, out, offset = 0, count = 3), Vec(3L, 2L, 4L))
    assertEquals(words(memory, out, offset = 16, count = 3), Vec(5L, 6L, 7L))
    assertEquals(word(memory, width, 0), 9L)
  }

  private def samplingModule(sampler: String, coordinate: String): Module = spv"""
    $header
    OpEntryPoint Kernel %main "sample"
    %void = OpTypeVoid
    %uint = OpTypeInt 32 0
    %float = OpTypeFloat 32
    %v2uint = OpTypeVector %uint 2
    %v2float = OpTypeVector %float 2
    %v4float = OpTypeVector %float 4
    %ro = OpTypeImage %void 2D 0 0 0 0 Unknown ReadOnly
    %sampler_t = OpTypeSampler
    %sampled = OpTypeSampledImage %ro
    %v4float_ptr = OpTypePointer CrossWorkgroup %v4float
    %fn = OpTypeFunction %void %ro %sampler_t %v4float_ptr
    %uint_0 = OpConstant %uint 0
    %uint_1 = OpConstant %uint 1
    %minus_1 = OpConstant %uint 0xffffffff
    %float_0 = OpConstant %float 0
    %float_0_5 = OpConstant %float 0.5
    %float_1_5 = OpConstant %float 1.5
    %float_1 = OpConstant %float 1
    %float_texel_2 = OpConstant %float 0.833333313465118408203125
    %texel_1 = OpConstantComposite %v2uint %uint_1 %uint_0
    %outside = OpConstantComposite %v2uint %minus_1 %uint_0
    %centre_1 = OpConstantComposite %v2float %float_1_5 %float_0_5
    %halfway = OpConstantComposite %v2float %float_0_5 %float_0_5
    %wrapped = OpConstantComposite %v2float %float_1 %float_0_5
    %centre_2 = OpConstantComposite %v2float %float_texel_2 %float_0_5
    %nearest = OpConstantSampler %sampler_t ClampToEdge 0 Nearest
    %main = OpFunction %void None %fn
    %src = OpFunctionParameter %ro
    %argument = OpFunctionParameter %sampler_t
    %out = OpFunctionParameter %v4float_ptr
    %entry = OpLabel
    %combined = OpSampledImage %sampled %src $sampler
    %texel = OpImageSampleExplicitLod %v4float %combined $coordinate Lod %float_0
    OpStore %out %texel
    OpReturn
    OpFunctionEnd
  """

  private def sampleOnce(
      module: Module,
      format: Int,
      texelBytes: Int,
      contents: Vec[Byte],
      sampler: Sampler = Sampler(),
      width: Int = 2
  ): Vec[Long] = {
    val memory = RegionMemory()
    val source = image(memory, format, texelBytes, width, contents = contents)
    val state  = memory.allocate(SamplerBytes.toLong.unsafe[Long > 0])
    val out    = memory.allocate(16L)
    writeSampler(memory, state, 0, sampler)
    assertEquals(
      run(
        module,
        "sample",
        memory,
        Vec(source.argument, buffer(state, range = Some(SamplerBytes.toLong.unsafe[Long > 0])), buffer(out)),
        relocations = Vec(source.relocation)
      ),
      Right(())
    )
    words(memory, out, offset = 0, count = 4)
  }

  private val rgba = bytes(0, 0, 0, 0, 255, 51, 0, 255)

  test("literal samplers read texels at unnormalised integer and floating-point coordinates") {
    val expected = Vec(unorm8(255), unorm8(51), 0L, unorm8(255))
    assertEquals(sampleOnce(samplingModule("%nearest", "%texel_1"), ImageFormat.Rgba8, 4, rgba), expected)
    assertEquals(sampleOnce(samplingModule("%nearest", "%centre_1"), ImageFormat.Rgba8, 4, rgba), expected)
  }

  test("clamp-to-border samplers return transparent black for alpha formats and opaque black otherwise") {
    val clamp = Sampler(
      addressU = ImageDescriptor.AddressMode.ClampToBorder,
      addressV = ImageDescriptor.AddressMode.ClampToBorder,
      unnormalized = true
    )
    val module = samplingModule("%argument", "%outside")
    assertEquals(sampleOnce(module, ImageFormat.Rgba8, 4, rgba, clamp), Vec(0L, 0L, 0L, 0L))
    assertEquals(
      sampleOnce(module, ImageFormat.R32f, 4, littleEndian(Seq(float(2f), float(3f)), 32), clamp),
      Vec(0L, 0L, 0L, float(1f))
    )
  }

  test("normalised linear samplers interpolate and repeat") {
    val linear = Sampler(
      minFilter = ImageDescriptor.Filter.Linear,
      magFilter = ImageDescriptor.Filter.Linear,
      addressU = ImageDescriptor.AddressMode.Repeat,
      addressV = ImageDescriptor.AddressMode.Repeat
    )
    val ramp = littleEndian(Seq(float(0f), float(1f)), 32)
    assertEquals(
      sampleOnce(samplingModule("%argument", "%halfway"), ImageFormat.R32f, 4, ramp, linear),
      Vec(float(0.5f), 0L, 0L, float(1f))
    )
    assertEquals(
      sampleOnce(samplingModule("%argument", "%wrapped"), ImageFormat.R32f, 4, ramp, linear),
      Vec(float(0.5f), 0L, 0L, float(1f))
    )
  }

  test("linear samplers denormalise coordinates in single precision") {
    val linear = Sampler(minFilter = ImageDescriptor.Filter.Linear, magFilter = ImageDescriptor.Filter.Linear)
    val texels = littleEndian(Seq(float(0f), float(1e6f), float(7f)), 32)
    assertEquals(
      sampleOnce(samplingModule("%argument", "%centre_2"), ImageFormat.R32f, 4, texels, linear, width = 3),
      Vec(float(7f), 0L, 0L, float(1f))
    )
  }

  test("pitched images address rows by their row pitch for reads, writes, sampling and queries") {
    val module = spv"""
      $header
      OpEntryPoint Kernel %main "pitched"
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %float = OpTypeFloat 32
      %v2uint = OpTypeVector %uint 2
      %v2float = OpTypeVector %float 2
      %v4float = OpTypeVector %float 4
      %rw = OpTypeImage %void 2D 0 0 0 0 Unknown ReadWrite
      %ro = OpTypeImage %void 2D 0 0 0 0 Unknown ReadOnly
      %sampler_t = OpTypeSampler
      %sampled = OpTypeSampledImage %ro
      %v4float_ptr = OpTypePointer CrossWorkgroup %v4float
      %v2uint_ptr = OpTypePointer CrossWorkgroup %v2uint
      %fn = OpTypeFunction %void %rw %ro %v4float_ptr %v4float_ptr %v2uint_ptr
      %uint_0 = OpConstant %uint 0
      %uint_1 = OpConstant %uint 1
      %float_0 = OpConstant %float 0
      %float_1_5 = OpConstant %float 1.5
      %last = OpConstantComposite %v2uint %uint_1 %uint_1
      %below = OpConstantComposite %v2uint %uint_0 %uint_1
      %centre = OpConstantComposite %v2float %float_1_5 %float_1_5
      %nearest = OpConstantSampler %sampler_t ClampToEdge 0 Nearest
      %main = OpFunction %void None %fn
      %image = OpFunctionParameter %rw
      %view = OpFunctionParameter %ro
      %read = OpFunctionParameter %v4float_ptr
      %sample = OpFunctionParameter %v4float_ptr
      %size = OpFunctionParameter %v2uint_ptr
      %entry = OpLabel
      %texel = OpImageRead %v4float %image %last
      OpStore %read %texel
      OpImageWrite %image %below %texel
      %combined = OpSampledImage %sampled %view %nearest
      %sampled_texel = OpImageSampleExplicitLod %v4float %combined %centre Lod %float_0
      OpStore %sample %sampled_texel
      %extent = OpImageQuerySize %v2uint %image
      OpStore %size %extent
      OpReturn
      OpFunctionEnd
    """
    val memory = RegionMemory()
    val texels = memory.allocate(32L)
    writeWords(memory, texels, 0, float(1f), float(2f), 0x5aL, 0x5aL, float(3f), float(4f), 0x5aL, 0x5aL)
    val view = ImageView(ImageFormat.R32f, Vec(ImageLevel(2, 2)), 32L, rowPitch = Some(16L))
    def record(): (KernelTask.Argument, KernelTask.PointerRelocation) = {
      val region = memory.allocate(view.size.toLong.unsafe[Long > 0])
      buffer(region, range = Some(view.size.toLong.unsafe[Long > 0])) -> writeImage(memory, region, 0, view, texels)
    }
    val (image, imageRelocation)     = record()
    val (sampled, sampledRelocation) = record()
    val read                         = memory.allocate(16L)
    val sample                       = memory.allocate(16L)
    val size                         = memory.allocate(8L)
    assertEquals(
      run(
        module,
        "pitched",
        memory,
        Vec(image, sampled, buffer(read), buffer(sample), buffer(size)),
        relocations = Vec(imageRelocation, sampledRelocation)
      ),
      Right(())
    )
    assertEquals(word(memory, read, 0), float(4f))
    assertEquals(word(memory, sample, 0), float(4f))
    assertEquals(words(memory, size, offset = 0, count = 2), Vec(2L, 2L))
    assertEquals(
      words(memory, texels, offset = 0, count = 8),
      Vec(float(1f), float(2f), 0x5aL, 0x5aL, float(4f), float(4f), 0x5aL, 0x5aL)
    )
  }

  for (
    (name, format, texelBytes, stored, doubled) <- Seq(
      ("UNORM_INT16", ImageDescriptor.Format.D16Unorm, 2, 0x8000L, 0xffffL),
      ("FLOAT", ImageDescriptor.Format.D32Sfloat, 4, float(0.25f), float(0.5f))
    )
  )
    test(s"depth images read and write scalar $name depths and report the depth channel order") {
      val module = spv"""
        $header
        OpEntryPoint Kernel %main "depth"
        %void = OpTypeVoid
        %uint = OpTypeInt 32 0
        %float = OpTypeFloat 32
        %v2uint = OpTypeVector %uint 2
        %ro = OpTypeImage %void 2D 1 0 0 0 Unknown ReadOnly
        %wo = OpTypeImage %void 2D 1 0 0 0 Unknown WriteOnly
        %float_ptr = OpTypePointer CrossWorkgroup %float
        %uint_ptr = OpTypePointer CrossWorkgroup %uint
        %fn = OpTypeFunction %void %ro %wo %float_ptr %uint_ptr
        %uint_0 = OpConstant %uint 0
        %uint_1 = OpConstant %uint 1
        %float_2 = OpConstant %float 2
        %second = OpConstantComposite %v2uint %uint_1 %uint_0
        %main = OpFunction %void None %fn
        %src = OpFunctionParameter %ro
        %dst = OpFunctionParameter %wo
        %out = OpFunctionParameter %float_ptr
        %channels = OpFunctionParameter %uint_ptr
        %entry = OpLabel
        %depth = OpImageRead %float %src %second
        OpStore %out %depth
        %doubled = OpFMul %float %depth %float_2
        OpImageWrite %dst %second %doubled
        %order = OpImageQueryOrder %uint %src
        OpStore %channels %order
        OpReturn
        OpFunctionEnd
      """
      val memory      = RegionMemory()
      val source      = image(memory, format, texelBytes, 2, contents = littleEndian(Seq(0L, stored), texelBytes * 8))
      val destination = image(memory, format, texelBytes, 2)
      val out         = memory.allocate(4L)
      val channels    = memory.allocate(4L)
      assertEquals(
        run(
          module,
          "depth",
          memory,
          Vec(source.argument, destination.argument, buffer(out), buffer(channels)),
          relocations = Vec(source.relocation, destination.relocation)
        ),
        Right(())
      )
      val expected = if (format == ImageDescriptor.Format.D16Unorm) float(0x8000 / 65535f) else stored
      assertEquals(word(memory, out, 0), expected)
      assertEquals(
        LittleEndian.decode(memory.bytes(destination.texels), texelBytes, texelBytes.unsafe[Int in 0 ~ 8]),
        doubled
      )
      assertEquals(word(memory, channels, 0), 13L)
    }

  test("image block reads take a byte column and row per lane, and vector components from successive rows") {
    val module = spv"""
      OpCapability SubgroupImageBlockIOINTEL
      OpExtension "SPV_INTEL_subgroups"
      $header
      OpEntryPoint Kernel %main "block" %lid
      OpDecorate %lid BuiltIn LocalInvocationIndex
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %v2uint = OpTypeVector %uint 2
      %ro = OpTypeImage %void 2D 0 0 0 0 Unknown ReadOnly
      %wo = OpTypeImage %void 2D 0 0 0 0 Unknown WriteOnly
      %v2uint_ptr = OpTypePointer CrossWorkgroup %v2uint
      %lid_ptr = OpTypePointer Input %uint
      %lid = OpVariable %lid_ptr Input
      %fn = OpTypeFunction %void %ro %wo %v2uint_ptr
      %uint_0 = OpConstant %uint 0
      %uint_1 = OpConstant %uint 1
      %uint_1000 = OpConstant %uint 1000
      %row_1 = OpConstantComposite %v2uint %uint_0 %uint_1
      %row_0 = OpConstantComposite %v2uint %uint_0 %uint_0
      %main = OpFunction %void None %fn
      %src = OpFunctionParameter %ro
      %dst = OpFunctionParameter %wo
      %out = OpFunctionParameter %v2uint_ptr
      %entry = OpLabel
      %lane = OpLoad %uint %lid
      %pair = OpSubgroupImageBlockReadINTEL %v2uint %src %row_1
      %slot = OpPtrAccessChain %v2uint_ptr %out %lane
      OpStore %slot %pair
      %scaled = OpIMul %uint %lane %uint_1000
      OpSubgroupImageBlockWriteINTEL %dst %row_0 %scaled
      OpReturn
      OpFunctionEnd
    """
    val memory      = RegionMemory()
    val texels      = (0 until 3).flatMap(row => (0 until 8).map(column => row * 100L + column))
    val source      = image(memory, ImageFormat.R32ui, 4, 8, height = 3, contents = littleEndian(texels, 32))
    val destination = image(memory, ImageFormat.R32ui, texelBytes = 4, width = 8, height = 3)
    val out         = memory.allocate(64L)
    assertEquals(
      run(
        module,
        "block",
        memory,
        Vec(source.argument, destination.argument, buffer(out)),
        local = testDimensions(8),
        relocations = Vec(source.relocation, destination.relocation)
      ),
      Right(())
    )
    assertEquals(
      words(memory, out, offset = 0, count = 16),
      Vec.from((0 until 8).flatMap(lane => Seq(100L + lane, 200L + lane)))
    )
    assertEquals(words(memory, destination.texels, offset = 0, count = 8), Vec.from((0 until 8).map(_ * 1000L)))
  }

  test("media block reads and writes run a region's rows, padded to powers of two, through the subgroup's values") {
    val module = spv"""
      OpCapability SubgroupImageMediaBlockIOINTEL
      OpExtension "SPV_INTEL_media_block_io"
      $header
      OpEntryPoint Kernel %main "media" %lid
      OpDecorate %lid BuiltIn LocalInvocationIndex
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %v2uint = OpTypeVector %uint 2
      %ro = OpTypeImage %void 2D 0 0 0 0 Unknown ReadOnly
      %wo = OpTypeImage %void 2D 0 0 0 0 Unknown WriteOnly
      %v2uint_ptr = OpTypePointer CrossWorkgroup %v2uint
      %lid_ptr = OpTypePointer Input %uint
      %lid = OpVariable %lid_ptr Input
      %fn = OpTypeFunction %void %ro %wo %v2uint_ptr
      %uint_0 = OpConstant %uint 0
      %uint_1 = OpConstant %uint 1
      %uint_2 = OpConstant %uint 2
      %uint_3 = OpConstant %uint 3
      %uint_4 = OpConstant %uint 4
      %uint_1000 = OpConstant %uint 1000
      %second_texel = OpConstantComposite %v2uint %uint_4 %uint_0
      %second_row = OpConstantComposite %v2uint %uint_0 %uint_1
      %main = OpFunction %void None %fn
      %src = OpFunctionParameter %ro
      %dst = OpFunctionParameter %wo
      %out = OpFunctionParameter %v2uint_ptr
      %entry = OpLabel
      %lane = OpLoad %uint %lid
      %pair = OpSubgroupImageMediaBlockReadINTEL %v2uint %src %second_texel %uint_4 %uint_3
      %slot = OpPtrAccessChain %v2uint_ptr %out %lane
      OpStore %slot %pair
      %scaled = OpIMul %uint %lane %uint_1000
      OpSubgroupImageMediaBlockWriteINTEL %dst %second_row %uint_2 %uint_2 %scaled
      OpReturn
      OpFunctionEnd
    """
    val memory      = RegionMemory()
    val texels      = (0 until 3).flatMap(row => (0 until 8).map(column => row * 100L + column))
    val source      = image(memory, ImageFormat.R32ui, 4, 8, height = 3, contents = littleEndian(texels, 32))
    val destination = image(memory, ImageFormat.R32ui, texelBytes = 4, width = 8, height = 3)
    val out         = memory.allocate(64L)
    assertEquals(
      run(
        module,
        "media",
        memory,
        Vec(source.argument, destination.argument, buffer(out)),
        local = testDimensions(8),
        relocations = Vec(source.relocation, destination.relocation)
      ),
      Right(())
    )
    // four texels a row from the second: lanes 0 to 3 take row 0 then row 2, lanes 4 to 7 row 1 then a fourth row
    // past the region
    assertEquals(
      words(memory, out, offset = 0, count = 16),
      Vec.from((0 until 8).flatMap(lane =>
        Seq(100L * (lane / 4) + 1 + lane % 4, if (lane < 4) 201L + lane else 0L)
      ))
    )
    assertEquals(words(memory, destination.texels, offset = 32, count = 8), Vec(0L, 1000L, 0L, 0L, 0L, 0L, 0L, 0L))
    assertEquals(words(memory, destination.texels, offset = 64, count = 2), Vec(2000L, 3000L))
  }

  test("bindless image handles address the descriptors their arguments bind") {
    val module = spv"""
      OpCapability BindlessImagesINTEL
      OpExtension "SPV_INTEL_bindless_images"
      $header
      OpEntryPoint Kernel %main "bindless"
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %ulong = OpTypeInt 64 0
      %v2uint = OpTypeVector %uint 2
      %v4uint = OpTypeVector %uint 4
      %ro = OpTypeImage %void 2D 0 0 0 0 Unknown ReadOnly
      %descriptor_ptr = OpTypePointer CrossWorkgroup %uint
      %v4uint_ptr = OpTypePointer CrossWorkgroup %v4uint
      %fn = OpTypeFunction %void %descriptor_ptr %v4uint_ptr
      %uint_1 = OpConstant %uint 1
      %uint_2 = OpConstant %uint 2
      %where = OpConstantComposite %v2uint %uint_1 %uint_2
      %main = OpFunction %void None %fn
      %descriptor = OpFunctionParameter %descriptor_ptr
      %out = OpFunctionParameter %v4uint_ptr
      %entry = OpLabel
      %handle = OpConvertPtrToU %ulong %descriptor
      %image = OpConvertHandleToImageINTEL %ro %handle
      %texel = OpImageRead %v4uint %image %where
      OpStore %out %texel
      OpReturn
      OpFunctionEnd
    """
    val memory = RegionMemory()
    val texels = (0 until 3).flatMap(row => (0 until 4).map(column => row * 100L + column))
    val source = image(memory, ImageFormat.R32ui, 4, 4, height = 3, contents = littleEndian(texels, 32))
    val out    = memory.allocate(16L)
    assertEquals(
      run(
        module,
        "bindless",
        memory,
        Vec(source.argument, buffer(out)),
        relocations = Vec(source.relocation)
      ),
      Right(())
    )
    assertEquals(words(memory, out, offset = 0, count = 1), Vec(201L))
  }

  test("read-write 3D images observe their own writes") {
    val module = spv"""
      $header
      OpEntryPoint Kernel %main "update"
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %v4uint = OpTypeVector %uint 4
      %rw = OpTypeImage %void 3D 0 0 0 0 Unknown ReadWrite
      %v4uint_ptr = OpTypePointer CrossWorkgroup %v4uint
      %fn = OpTypeFunction %void %rw %v4uint_ptr
      %uint_0 = OpConstant %uint 0
      %uint_1 = OpConstant %uint 1
      %where = OpConstantComposite %v4uint %uint_1 %uint_0 %uint_1 %uint_0
      %ones = OpConstantComposite %v4uint %uint_1 %uint_1 %uint_1 %uint_1
      %main = OpFunction %void None %fn
      %volume = OpFunctionParameter %rw
      %out = OpFunctionParameter %v4uint_ptr
      %entry = OpLabel
      %before = OpImageRead %v4uint %volume %where
      %after = OpIAdd %v4uint %before %ones
      OpImageWrite %volume %where %after
      %observed = OpImageRead %v4uint %volume %where
      OpStore %out %observed
      OpReturn
      OpFunctionEnd
    """
    val memory = RegionMemory()
    val volume = image(memory, ImageFormat.Rgba8ui, texelBytes = 4, width = 2, slices = 2, volume = true)
    memory.array(volume.texels).indices.foreach(index => memory.array(volume.texels)(index) = index.toByte)
    val out = memory.allocate(16L)
    assertEquals(
      run(module, "update", memory, Vec(volume.argument, buffer(out)), relocations = Vec(volume.relocation)),
      Right(())
    )
    assertEquals(words(memory, out, offset = 0, count = 4), Vec(13L, 14L, 15L, 16L))
    assertEquals(memory.bytes(volume.texels).slice(12, 16), bytes(13, 14, 15, 16))
  }

  test("read_imageh returns half-precision lanes") {
    val module = spv"""
      $header
      OpEntryPoint Kernel %main "half"
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %half = OpTypeFloat 16
      %v2uint = OpTypeVector %uint 2
      %v4half = OpTypeVector %half 4
      %ro = OpTypeImage %void 2D 0 0 0 0 Unknown ReadOnly
      %v4half_ptr = OpTypePointer CrossWorkgroup %v4half
      %fn = OpTypeFunction %void %ro %v4half_ptr
      %uint_0 = OpConstant %uint 0
      %where = OpConstantComposite %v2uint %uint_0 %uint_0
      %main = OpFunction %void None %fn
      %src = OpFunctionParameter %ro
      %out = OpFunctionParameter %v4half_ptr
      %entry = OpLabel
      %texel = OpImageRead %v4half %src %where
      OpStore %out %texel
      OpReturn
      OpFunctionEnd
    """
    val memory = RegionMemory()
    val source = image(memory, ImageFormat.R32f, 4, 1, contents = littleEndian(Seq(float(1.5f)), 32))
    val out    = memory.allocate(8L)
    assertEquals(
      run(module, "half", memory, Vec(source.argument, buffer(out)), relocations = Vec(source.relocation)),
      Right(())
    )
    assertEquals(memory.bytes(out), littleEndian(Seq(0x3e00L, 0L, 0L, 0x3c00L), 16))
  }

  for (extend <- Seq("ZeroExtend", "SignExtend", "Nontemporal"))
    test(s"integer images sample with Lod|$extend operands and write with $extend") {
      val module = spv"""
        $header
        OpEntryPoint Kernel %main "sample"
        %void = OpTypeVoid
        %uint = OpTypeInt 32 0
        %float = OpTypeFloat 32
        %v2uint = OpTypeVector %uint 2
        %v4uint = OpTypeVector %uint 4
        %ro = OpTypeImage %void 2D 0 0 0 0 Unknown ReadWrite
        %sampler_t = OpTypeSampler
        %sampled = OpTypeSampledImage %ro
        %v4uint_ptr = OpTypePointer CrossWorkgroup %v4uint
        %fn = OpTypeFunction %void %ro %v4uint_ptr
        %uint_0 = OpConstant %uint 0
        %uint_1 = OpConstant %uint 1
        %float_0 = OpConstant %float 0
        %where = OpConstantComposite %v2uint %uint_1 %uint_0
        %nearest = OpConstantSampler %sampler_t ClampToEdge 0 Nearest
        %main = OpFunction %void None %fn
        %src = OpFunctionParameter %ro
        %out = OpFunctionParameter %v4uint_ptr
        %entry = OpLabel
        %combined = OpSampledImage %sampled %src %nearest
        %texel = OpImageSampleExplicitLod %v4uint %combined %where Lod|$extend %float_0
        OpStore %out %texel
        OpImageWrite %src %where %texel $extend
        OpReturn
        OpFunctionEnd
      """
      val memory = RegionMemory()
      val source = image(memory, ImageFormat.Rgba8ui, 4, 2, contents = bytes(0, 0, 0, 0, 7, 8, 250, 255))
      val out    = memory.allocate(16L)
      assertEquals(
        run(module, "sample", memory, Vec(source.argument, buffer(out)), relocations = Vec(source.relocation)),
        Right(())
      )
      assertEquals(words(memory, out, offset = 0, count = 4), Vec(7L, 8L, 250L, 255L))
    }
}
