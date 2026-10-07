package net.kurobako.gemstone.isa.spirv

import ImageDescriptor.*
import fixtures.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*

class CubeMapSuite extends munit.FunSuite {
  private val faces = Vector("+X", "-X", "+Y", "-Y", "+Z", "-Z")
  private val axes  =
    Vector((1.0, 0.0, 0.0), (-1.0, 0.0, 0.0), (0.0, 1.0, 0.0), (0.0, -1.0, 0.0), (0.0, 0.0, 1.0), (0.0, 0.0, -1.0))

  test("each axis projects to its face centre with zero radial derivative") {
    for (((x, y, z), face) <- axes.zipWithIndex) {
      assertEquals(CubeMap.project(x, y, z), CubeMap.Projection(face, .5, .5), faces(face))
      assertEquals(CubeMap.project(x * 7, y * 7, z * 7), CubeMap.Projection(face, .5, .5), faces(face))
      assertEquals(CubeMap.gradient(face, Array(x, y, z), Array(x, y, z)), (0.0, 0.0), faces(face))
    }
  }

  test("cube ties are deterministic and zero direction is finite") {
    assertEquals(CubeMap.project(1, 1, 1).face, 4)
    assertEquals(CubeMap.project(1, 1, -1).face, 5)
    assertEquals(CubeMap.project(1, -1, 0).face, 3)
    val p = CubeMap.project(0, 0, 0)
    assert(p.s.isFinite && p.t.isFinite)
  }

  for (size <- Seq(1L, 3L, 4L, 17L)) test(s"cube edge rotations at size $size") {
    val n = size - 1
    for (i <- 0L until size) {
      val expected = Vector(
        Vector((4, n, i), (5, 0L, i), (2, n, n - i), (3, n, i)),
        Vector((5, n, i), (4, 0L, i), (2, 0L, i), (3, 0L, n - i)),
        Vector((1, i, 0L), (0, n - i, 0L), (5, n - i, 0L), (4, i, 0L)),
        Vector((1, n - i, n), (0, i, n), (4, i, n), (5, n - i, n)),
        Vector((1, n, i), (0, 0L, i), (2, i, n), (3, i, 0L)),
        Vector((0, n, i), (1, 0L, i), (2, n - i, 0L), (3, n - i, n))
      )
      for {
        face       <- 0 until 6
        (xy, side) <- Vector((-1L, i), (size, i), (i, -1L), (i, size)).zipWithIndex
      } {
        val (target, x, y) = expected(face)(side)
        val clue           = s"${faces(face)} at $xy"
        assertEquals(CubeMap.taps(face, xy._1, xy._2, size, true), Vector(CubeMap.Texel(target, x, y)), clue)
        assertEquals(
          CubeMap.taps(face, xy._1, xy._2, size, false),
          Vector(CubeMap.Texel(face, Math.max(0L, Math.min(n, xy._1)), Math.max(0L, Math.min(n, xy._2)))),
          clue
        )
      }
    }
  }

  test("cube corner averages three incident faces") {
    for {
      face <- 0 until 6
      x    <- Seq(-1L, 4L)
      y    <- Seq(-1L, 4L)
    } {
      val taps = CubeMap.taps(face, x, y, 4, true)
      val clue = s"${faces(face)} at ($x, $y)"
      assertEquals(taps.size, 3, clue)
      assertEquals(taps.map(_.face).distinct.size, 3, clue)
      assert(taps.forall(t => (t.x == 0 || t.x == 3) && (t.y == 0 || t.y == 3)), clue)
    }
  }

  test("cube projected gradient matches the finite difference") {
    for ((axis, face) <- axes.zipWithIndex) {
      val c = Array(axis._1 * 4 + .2, axis._2 * 4 + .3, axis._3 * 4 + .4)
      val d = Array(.7, -.2, .3)
      val g = CubeMap.gradient(face, c, d)
      val e = 1e-5
      val a = CubeMap.project(c(0) - e * d(0), c(1) - e * d(1), c(2) - e * d(2))
      val b = CubeMap.project(c(0) + e * d(0), c(1) + e * d(1), c(2) + e * d(2))
      assertEqualsDouble(g._1, (b.s - a.s) / (2 * e), 1e-8, faces(face))
      assertEqualsDouble(g._2, (b.t - a.t) / (2 * e), 1e-8, faces(face))
    }
  }

  private case class Sample(
      name: String,
      direction: Seq[Float],
      expected: Float,
      linear: Boolean = false,
      compare: Boolean = false,
      lod: Int = 0,
      gradient: Option[String] = None,
      gather: Option[Seq[Float]] = None
  )

  private val samples = Seq(
    Sample("gather seam", Seq(1, 0, 1), .625f, gather = Some(Seq(.625f, .125f, .125f, .625f))),
    Sample("gather corner", Seq(1, 1, 1), .625f, gather = Some(Seq(.625f, .125f, .625f, .375f))),
    Sample("gather comparison corner", Seq(1, 1, 1), 1f, compare = true, gather = Some(Seq(1f, 0f, 1f, 0f))),
    Sample("positive X", Seq(1, 0, 0), .125f),
    Sample("negative X", Seq(-1, 0, 0), .25f),
    Sample("positive Y", Seq(0, 1, 0), .375f),
    Sample("negative Y", Seq(0, -1, 0), .5f),
    Sample("positive Z", Seq(0, 0, 1), .625f),
    Sample("negative Z", Seq(0, 0, -1), .75f),
    Sample("linear X Z seam", Seq(1, 0, 1), .375f, linear = true),
    Sample("linear X negative Y seam", Seq(1, -1, 0), .3125f, linear = true),
    Sample("linear corner", Seq(1, 1, 1), .375f, linear = true),
    Sample("comparison seam", Seq(1, 0, 1), .5f, linear = true, compare = true),
    Sample("explicit mip", Seq(1, 0, 0), .875f, lod = 1),
    Sample("projected gradient mip", Seq(1, 0, 0), .875f, gradient = Some("%zero %zero %one")),
    Sample("radial gradient does not change mip", Seq(1, 0, 0), .125f, gradient = Some("%one %zero %zero"))
  )

  // layer values clamp below 0 and above the last layer; comparison results cannot tell layers apart; gathers also
  // read the second cube and linear seams a clamped layer
  for {
    (sample, index) <- samples.zipWithIndex
    layer           <- Seq(None, Some(if (sample.compare) 0f else Seq(-1f, 0f, 1f, 2f)(index % 4))) ++
      Option.when(!sample.compare && sample.gather.nonEmpty)(Some(index + 1f)) ++
      Option.when(!sample.compare && sample.linear)(Some(-1f))
  } test(s"Cube ${layer.fold("")(value => s"array layer $value ")}${sample.name}") {
    val arrayed = layer.isDefined
    val layers  = if (arrayed) 12 else 6
    val module  = spv"""
      $vulkan
      OpCapability ImageQuery
      ${if (arrayed) "OpCapability SampledCubeArray" else ""}
      OpEntryPoint GLCompute %main "main" %image %sampler %out
      OpExecutionMode %main LocalSize 1 1 1
      OpDecorate %image DescriptorSet 0
      OpDecorate %image Binding 0
      OpDecorate %sampler DescriptorSet 0
      OpDecorate %sampler Binding 1
      OpDecorate %out DescriptorSet 0
      OpDecorate %out Binding 2
      OpDecorate %out_block Block
      OpMemberDecorate %out_block 0 Offset 0
      OpMemberDecorate %out_block 1 Offset 16
      OpMemberDecorate %out_block 2 Offset 32
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %float = OpTypeFloat 32
      %v3float = OpTypeVector %float 3
      %v4float = OpTypeVector %float 4
      %size_type = OpTypeVector %uint ${if (arrayed) 3 else 2}
      %image_type = OpTypeImage %float Cube ${if (sample.compare) 1 else 0} ${if (arrayed) 1 else 0} 0 1 Unknown
      %image_ptr = OpTypePointer UniformConstant %image_type
      %sampler_type = OpTypeSampler
      %sampler_ptr = OpTypePointer UniformConstant %sampler_type
      %sampled_type = OpTypeSampledImage %image_type
      %out_block = OpTypeStruct %v4float %size_type %uint
      %out_ptr = OpTypePointer StorageBuffer %out_block
      %colour_ptr = OpTypePointer StorageBuffer %v4float
      %size_ptr = OpTypePointer StorageBuffer %size_type
      %levels_ptr = OpTypePointer StorageBuffer %uint
      %fn = OpTypeFunction %void
      %image = OpVariable %image_ptr UniformConstant
      %sampler = OpVariable %sampler_ptr UniformConstant
      %out = OpVariable %out_ptr StorageBuffer
      %uint_0 = OpConstant %uint 0
      %uint_1 = OpConstant %uint 1
      %uint_2 = OpConstant %uint 2
      %x = OpConstant %float ${sample.direction(0)}
      %y = OpConstant %float ${sample.direction(1)}
      %z = OpConstant %float ${sample.direction(2)}
      %lod = OpConstant %float ${sample.lod}
      %reference = OpConstant %float 0.4
      %layer = OpConstant %float ${layer.fold("0x1.8p+128")(_.toString)}
      %zero = OpConstant %float 0
      %one = OpConstant %float 1
      %coord = ${
        if (arrayed || sample.compare) "OpConstantComposite %v4float %x %y %z %layer"
        else "OpConstantComposite %v3float %x %y %z"
      }
      %dx = OpConstantComposite %v3float ${sample.gradient.getOrElse("%zero %zero %zero")}
      %dy = OpConstantComposite %v3float %zero %zero %zero
      %main = OpFunction %void None %fn
      %entry = OpLabel
      %image_value = OpLoad %image_type %image
      %sampler_value = OpLoad %sampler_type %sampler
      %sampled = OpSampledImage %sampled_type %image_value %sampler_value
      ${
        if (sample.gather.isDefined && sample.compare) "%result = OpImageDrefGather %v4float %sampled %coord %reference"
        else if (sample.gather.isDefined) "%result = OpImageGather %v4float %sampled %coord %uint_0"
        else if (sample.compare)
          """%depth = OpImageSampleDrefExplicitLod %float %sampled %coord %reference Lod %lod
            |%result = OpCompositeConstruct %v4float %depth %zero %zero %one""".stripMargin
        else if (sample.gradient.isDefined) "%result = OpImageSampleExplicitLod %v4float %sampled %coord Grad %dx %dy"
        else "%result = OpImageSampleExplicitLod %v4float %sampled %coord Lod %lod"
      }
      %colour_element = OpAccessChain %colour_ptr %out %uint_0
      OpStore %colour_element %result
      %size = OpImageQuerySizeLod %size_type %image_value %uint_0
      %size_element = OpAccessChain %size_ptr %out %uint_1
      OpStore %size_element %size
      %levels = OpImageQueryLevels %uint %image_value
      %levels_element = OpAccessChain %levels_ptr %out %uint_2
      OpStore %levels_element %levels
      OpReturn
      OpFunctionEnd
    """
    val memory = RegionMemory()
    val texels = memory.allocate(Vec.from(for {
      mip   <- 0 to 1
      face  <- 0 until layers
      _     <- 0 until (if (mip == 0) 16 else 4)
      value <- LittleEndian.toVec(floatBits((face % 6 + 1) / 8f + (face / 6) * 2f + mip * .75f), 4)
    } yield value))
    val view = ImageView(
      ImageFormat.R32f,
      Vec(ImageLevel(4, 4), ImageLevel(2, 2, layers * 64L)),
      layers * 80L,
      slices = layers
    )
    val sampler = Sampler(
      minFilter = if (sample.linear) Filter.Linear else Filter.Nearest,
      magFilter = if (sample.linear) Filter.Linear else Filter.Nearest,
      addressU = AddressMode.Repeat,
      addressV = AddressMode.Repeat,
      addressW = AddressMode.Repeat,
      compareOp = Option.when(sample.compare)(CompareOp.Less),
      maxLod = 1
    )
    val size        = view.size.toLong.refine[Long > 0].value
    val image       = memory.allocate(size)
    val relocation  = writeImage(memory, image, 0, view, texels)
    val samplerData = memory.allocate(SamplerBytes.toLong.refine[Long > 0].value)
    writeSampler(memory, samplerData, 0, sampler)
    val out       = memory.allocate(36L)
    val arguments = Vec(buffer(image, range = Some(size)), buffer(samplerData), buffer(out))
    assertEquals(run(module, "main", memory, arguments, relocations = Vec(relocation)), Right(()))
    val shift    = if (layer.exists(_ >= 1)) 2f else 0f
    val expected = sample.gather.fold(Seq(sample.expected + shift, 0f, 0f, 1f))(_.map(_ + shift))
    assertEquals(words(memory, out, 0, 4), Vec.from(expected.map(floatBits)))
    assertEquals(words(memory, out, 16, 2), Vec(4L, 4L))
    if (arrayed) assertEquals(word(memory, out, 24), 2L)
    assertEquals(word(memory, out, 32), 2L)
  }
}
