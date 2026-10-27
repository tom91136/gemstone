package net.kurobako.gemstone.benchmarks

import cats.syntax.all.*
import net.kurobako.gemstone.benchmarks.BenchmarkArguments.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.spirv.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*

/** Sampling rounds over a 2x2 colour and depth image: implicit and explicit LOD, depth-compare and gather lookups. */
object SPIRVImageSamplingBenchmark {
  private val Extent          = 2
  private val Texels          = Seq(0f, .25f, .75f, 1f)
  private val TexelBytes      = Texels.size * 4
  private val ViewBytes       = ImageDescriptor.Layout.LevelTable + ImageDescriptor.Layout.LevelBytes
  private val SamplerBytes    = ImageDescriptor.SamplerWord.Count * 4
  private val ColourView      = 0
  private val ColourSampler   = 64
  private val DepthView       = 128
  private val DepthSampler    = 192
  private val DescriptorBytes = 256
  private val OutputBytes     = 16

  private case class Options(iterations: Int > 0, rounds: Int > 0)

  val benchmark: Case = (
    iterations(2),
    positive("rounds", "Image sampling rounds per invocation", 16)
  ).mapN(Options(_, _)).map(options => prepare(options))

  // a fresh program's first execution is several times slower, so load once
  private def prepare(options: Options): () => BenchmarkSample = {
    val memory      = BenchmarkMemory(TexelBytes, TexelBytes, DescriptorBytes, OutputBytes)
    val colour      = memory.ids(0)
    val depth       = memory.ids(1)
    val descriptors = memory.ids(2)
    val output      = memory.ids(3)
    Texels.zipWithIndex.foreach { (value, index) =>
      memory.putInt(colour, index * 4, java.lang.Float.floatToRawIntBits(value))
      memory.putInt(depth, index * 4, java.lang.Float.floatToRawIntBits(value))
    }
    val colourView = writeView(memory, descriptors, ColourView, colour)
    val depthView  = writeView(memory, descriptors, DepthView, depth)
    writeSampler(memory, descriptors, ColourSampler, ImageDescriptor.Filter.Linear, None)
    writeSampler(
      memory,
      descriptors,
      DepthSampler,
      ImageDescriptor.Filter.Nearest,
      Some(ImageDescriptor.CompareOp.Less)
    )
    def binding(offset: Int, size: Int): KernelTask.Argument =
      KernelTask.Argument.Memory(descriptors, offset.toLong.unsafe[Long >= 0], Some(size.toLong.unsafe[Long > 0]))
    val task = KernelTask.validated(
      kernelName = "main",
      groupSize = LaunchGeometry.Dimensions.One,
      groupCount = LaunchGeometry.Dimensions.One,
      arguments = Vec(
        binding(ColourView, ViewBytes),
        binding(ColourSampler, SamplerBytes),
        binding(DepthView, ViewBytes),
        binding(DepthSampler, SamplerBytes),
        KernelTask.Argument.Memory(output, 0L)
      ),
      pointerRelocations = Vec(colourView, depthView)
    ).fold(error => throw IllegalArgumentException(error), identity)
    val program =
      ISA.encode(module(options.rounds)).left.map(error => Fault.InvalidRequest(error.message))
        .flatMap(SPIRVBackend(Validation.Environment.VulkanGraphics).load(_))
        .fold(error => throw IllegalArgumentException(error.message), identity)
    val expected = Seq(2.75f, 1f, .25f, 3f).map(_ * options.rounds.raw)
    () => {
      val sample = Measurement.repeatSucceeding(options.iterations)(
        program.execute(task, TaskContext(memory, TaskControl(), _ => Right(())))
      )
      verify(memory, output, expected)
      sample
    }
  }

  private def verify(memory: BenchmarkMemory, output: AllocationId, expected: Seq[Float]): Unit =
    expected.zipWithIndex.foreach { (value, lane) =>
      val actual = memory.getInt(output, lane * 4)
      val wanted = java.lang.Float.floatToRawIntBits(value)
      if (actual != wanted)
        throw IllegalStateException(s"image sampling mismatch in lane $lane: $actual != $wanted")
    }

  private def writeView(
      memory: BenchmarkMemory,
      region: AllocationId,
      offset: Int,
      texels: AllocationId
  ): KernelTask.PointerRelocation = {
    import ImageDescriptor.*
    def put(at: Int, value: Int): Unit = memory.putInt(region, offset + at, value)
    put(Layout.Width, Extent)
    put(Layout.Height, Extent)
    put(Layout.Slices, 1)
    put(Layout.Format, (ImageFormat.R32f | Flags.LevelTable).toInt)
    memory.putLong(region, offset + Layout.TexelBytes, TexelBytes.toLong)
    put(Layout.Levels, 1)
    put(Layout.LevelTable + Layout.LevelWidth, Extent)
    put(Layout.LevelTable + Layout.LevelHeight, Extent)
    KernelTask.PointerRelocation(
      KernelTask.RelocationSource.Memory(region),
      (offset + Layout.Texels).toLong.unsafe[Long >= 0],
      texels,
      0L
    )
  }

  private def writeSampler(
      memory: BenchmarkMemory,
      region: AllocationId,
      offset: Int,
      filter: Int,
      compare: Option[Int]
  ): Unit = {
    import ImageDescriptor.*
    def put(word: Int, value: Int): Unit = memory.putInt(region, offset + word * 4, value)
    put(SamplerWord.MinFilter, filter)
    put(SamplerWord.MagFilter, filter)
    put(SamplerWord.AddressU, AddressMode.ClampToEdge)
    put(SamplerWord.AddressV, AddressMode.ClampToEdge)
    put(SamplerWord.AddressW, AddressMode.ClampToEdge)
    put(SamplerWord.CompareEnable, if (compare.isDefined) 1 else 0)
    put(SamplerWord.CompareOp, compare.getOrElse(CompareOp.Never))
    put(SamplerWord.MaxLod, java.lang.Float.floatToRawIntBits(1000f))
  }

  // implicit lod needs the fragment stage's derivatives
  private def module(rounds: Int > 0): Module = {
    val chain = (1 to rounds.raw).flatMap { round =>
      Seq(
        s"%implicit$round = OpImageSampleImplicitLod %v4float %colour_sampled %coord",
        s"%explicit$round = OpImageSampleExplicitLod %v4float %colour_sampled %coord Lod %zero",
        s"%compared$round = OpImageSampleDrefExplicitLod %float %depth_sampled %depth_coord %half Lod %zero",
        s"%compared_vector$round = OpCompositeConstruct %v4float %compared$round %zero %zero %one",
        s"%gathered$round = OpImageGather %v4float %colour_sampled %coord %component",
        s"%sum${round}_a = OpFAdd %v4float %acc${round - 1} %implicit$round",
        s"%sum${round}_b = OpFAdd %v4float %sum${round}_a %explicit$round",
        s"%sum${round}_c = OpFAdd %v4float %sum${round}_b %compared_vector$round",
        s"%acc$round = OpFAdd %v4float %sum${round}_c %gathered$round"
      )
    }
    val source = s"""OpCapability Shader
                    |OpCapability ImageGatherExtended
                    |OpMemoryModel Logical GLSL450
                    |OpEntryPoint Fragment %main "main" %colour %colour_sampler %depth %depth_sampler %out
                    |OpExecutionMode %main OriginUpperLeft
                    |OpDecorate %colour DescriptorSet 0
                    |OpDecorate %colour Binding 0
                    |OpDecorate %colour_sampler DescriptorSet 0
                    |OpDecorate %colour_sampler Binding 1
                    |OpDecorate %depth DescriptorSet 0
                    |OpDecorate %depth Binding 2
                    |OpDecorate %depth_sampler DescriptorSet 0
                    |OpDecorate %depth_sampler Binding 3
                    |OpDecorate %out DescriptorSet 0
                    |OpDecorate %out Binding 4
                    |OpDecorate %out_block Block
                    |OpMemberDecorate %out_block 0 Offset 0
                    |%void = OpTypeVoid
                    |%uint = OpTypeInt 32 0
                    |%float = OpTypeFloat 32
                    |%v2float = OpTypeVector %float 2
                    |%v4float = OpTypeVector %float 4
                    |%colour_type = OpTypeImage %float 2D 0 0 0 1 Unknown
                    |%depth_type = OpTypeImage %float 2D 1 0 0 1 Unknown
                    |%sampler_type = OpTypeSampler
                    |%colour_sampled_type = OpTypeSampledImage %colour_type
                    |%depth_sampled_type = OpTypeSampledImage %depth_type
                    |%colour_ptr = OpTypePointer UniformConstant %colour_type
                    |%depth_ptr = OpTypePointer UniformConstant %depth_type
                    |%sampler_ptr = OpTypePointer UniformConstant %sampler_type
                    |%out_block = OpTypeStruct %v4float
                    |%out_ptr = OpTypePointer StorageBuffer %out_block
                    |%out_element_ptr = OpTypePointer StorageBuffer %v4float
                    |%fn = OpTypeFunction %void
                    |%colour = OpVariable %colour_ptr UniformConstant
                    |%colour_sampler = OpVariable %sampler_ptr UniformConstant
                    |%depth = OpVariable %depth_ptr UniformConstant
                    |%depth_sampler = OpVariable %sampler_ptr UniformConstant
                    |%out = OpVariable %out_ptr StorageBuffer
                    |%uint_0 = OpConstant %uint 0
                    |%component = OpConstant %uint 0
                    |%zero = OpConstant %float 0
                    |%one = OpConstant %float 1
                    |%half = OpConstant %float 0.5
                    |%three_quarters = OpConstant %float 0.75
                    |%coord = OpConstantComposite %v2float %half %half
                    |%depth_coord = OpConstantComposite %v2float %three_quarters %three_quarters
                    |%acc0 = OpConstantComposite %v4float %zero %zero %zero %zero
                    |%main = OpFunction %void None %fn
                    |%entry = OpLabel
                    |%colour_image = OpLoad %colour_type %colour
                    |%colour_sampler_value = OpLoad %sampler_type %colour_sampler
                    |%colour_sampled = OpSampledImage %colour_sampled_type %colour_image %colour_sampler_value
                    |%depth_image = OpLoad %depth_type %depth
                    |%depth_sampler_value = OpLoad %sampler_type %depth_sampler
                    |%depth_sampled = OpSampledImage %depth_sampled_type %depth_image %depth_sampler_value
                    |${chain.mkString("\n")}
                    |%out_element = OpAccessChain %out_element_ptr %out %uint_0
                    |OpStore %out_element %acc${rounds.raw}
                    |OpReturn
                    |OpFunctionEnd
                    |""".stripMargin
    asm.Parser.parse(source).fold(error => throw IllegalArgumentException(error.toString), identity)
  }
}
