package net.kurobako.gemstone.isa.spirv

import ImageDescriptor.*
import fixtures.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*

class SampledImageSuite extends munit.FunSuite {
  private case class Sample(
      name: String,
      x: Float,
      y: Float,
      filter: Int = Filter.Nearest,
      mode: Int = AddressMode.ClampToEdge,
      border: Int = BorderColour.FloatTransparentBlack,
      unnormalized: Boolean = false,
      compare: Boolean = false,
      rgb: Boolean = false,
      projectiveQ: Option[Float] = None,
      gatherComponent: Int = 0,
      mapping: Option[Seq[Int]] = None,
      alias: Boolean = false,
      mip: Boolean = false,
      lod: Float = 0,
      bias: Float = 0,
      minLod: Float = 0,
      maxLod: Float = 0,
      mipmap: Int = MipmapMode.Nearest,
      baseLevel: Int = 0,
      operation: String = "sample",
      gradientX: Float = 0,
      gradientY: Float = 0,
      arrayLayer: Option[Float] = None,
      dimensions: Int = 2,
      z: Float = 0,
      gradientZ: Float = 0,
      modeW: Int = AddressMode.ClampToEdge,
      offset: Option[(Int, Int)] = None,
      expected: Seq[Float]
  )

  private val samples = List(
    Sample("projective 1D", 1.5f, 0f, dimensions = 1, projectiveQ = Some(2f), expected = Seq(.25f, 0f, 0f, 1f)),
    Sample("projective 2D", 1.5f, .5f, projectiveQ = Some(2f), expected = Seq(.25f, 0f, 0f, 1f)),
    Sample(
      "projective 3D",
      1.5f,
      .5f,
      dimensions = 3,
      z = 1.5f,
      mip = true,
      projectiveQ = Some(2f),
      expected = Seq(4.25f, 0f, 0f, 1f)
    ),
    Sample(
      "projective depth reference",
      3f,
      1f,
      projectiveQ = Some(4f),
      compare = true,
      expected = Seq(1f, 0f, 0f, 1f)
    ),
    Sample(
      "projective explicit gradients unchanged",
      .5f,
      .5f,
      projectiveQ = Some(2f),
      mip = true,
      maxLod = 1,
      operation = "grad",
      gradientX = 1,
      expected = Seq(2f, 0f, 0f, 1f)
    ),
    Sample("gather order", .5f, .5f, operation = "gather", expected = Seq(.75f, 1f, .25f, 0f)),
    Sample(
      "gather ignores filters and LOD clamps",
      .5f,
      .5f,
      operation = "gather",
      mip = true,
      filter = Filter.Linear,
      minLod = 1,
      maxLod = 1,
      expected = Seq(.75f, 1f, .25f, 0f)
    ),
    Sample(
      "gather view base level",
      .5f,
      .5f,
      operation = "gather",
      mip = true,
      baseLevel = 1,
      expected = Seq.fill(4)(2f)
    ),
    Sample("gather missing alpha", .5f, .5f, operation = "gather", gatherComponent = 3, expected = Seq.fill(4)(1f)),
    Sample(
      "gather swizzle before component selection",
      .5f,
      .5f,
      operation = "gather",
      mip = true,
      mapping = Some(Seq(Swizzle.A, Swizzle.Zero, Swizzle.One, Swizzle.R)),
      gatherComponent = 3,
      expected = Seq(.75f, 1f, .25f, 0f)
    ),
    Sample(
      "gather clamp border",
      0f,
      0f,
      operation = "gather",
      mode = AddressMode.ClampToBorder,
      border = BorderColour.FloatOpaqueWhite,
      expected = Seq(1f, 0f, 1f, 1f)
    ),
    Sample("gather compare each texel", .5f, .5f, operation = "gather", compare = true, expected = Seq(1f, 1f, 0f, 0f)),
    Sample("gather offset", .5f, .5f, operation = "gather", offset = Some((-1, 0)), expected = Seq(.75f, .75f, 0f, 0f)),
    Sample(
      "gather rounded array layer",
      .5f,
      .5f,
      operation = "gather",
      mip = true,
      arrayLayer = Some(1f),
      expected = Seq(4.75f, 5f, 4.25f, 4f)
    ),
    Sample(
      "decoded RGB fetch swizzle",
      1f,
      0f,
      mip = true,
      rgb = true,
      mapping = Some(Seq(Swizzle.R, Swizzle.A, Swizzle.Zero, Swizzle.One)),
      operation = "fetch",
      expected = Seq(.25f, 1f, 0f, 1f)
    ),
    Sample(
      "RGB view channel permutation",
      .75f,
      .25f,
      mip = true,
      rgb = true,
      mapping = Some(Seq(Swizzle.G, Swizzle.B, Swizzle.A, Swizzle.R)),
      expected = Seq(.5f, 1f, 1f, .25f)
    ),
    Sample(
      "RGB view swizzled border constants",
      -1f,
      -1f,
      mode = AddressMode.ClampToBorder,
      mip = true,
      rgb = true,
      mapping = Some(Seq(Swizzle.Zero, Swizzle.One, Swizzle.R, Swizzle.A)),
      expected = Seq(0f, 1f, 0f, 1f)
    ),
    Sample(
      "R32 view missing channels and constants",
      .75f,
      .25f,
      mip = true,
      mapping = Some(Seq(Swizzle.A, Swizzle.Zero, Swizzle.One, Swizzle.R)),
      expected = Seq(1f, 0f, 1f, .25f)
    ),
    Sample("decoded RGB view stride", .75f, .25f, mip = true, rgb = true, expected = Seq(.25f, .5f, 1f, 1f)),
    Sample(
      "decoded RGB view filtering",
      .5f,
      .5f,
      filter = Filter.Linear,
      mip = true,
      rgb = true,
      expected = Seq(.5f, .5f, 1f, 1f)
    ),
    Sample(
      "decoded RGB transparent border retains absent alpha",
      -1f,
      -1f,
      mode = AddressMode.ClampToBorder,
      mip = true,
      rgb = true,
      expected = Seq(0f, 0f, 0f, 1f)
    ),
    Sample(
      "volume mip dimensions",
      0,
      0,
      dimensions = 3,
      mip = true,
      lod = 1,
      operation = "query",
      expected = Seq(1, 1, 1, 2)
    ),
    Sample("volume base dimensions", 0, 0, dimensions = 3, mip = true, operation = "query", expected = Seq(2, 2, 2, 2)),
    Sample(
      "volume view dimensions",
      0,
      0,
      dimensions = 3,
      mip = true,
      baseLevel = 1,
      operation = "query",
      expected = Seq(1, 1, 1, 1)
    ),
    Sample("volume first slice", .75f, .25f, dimensions = 3, z = .25f, mip = true, expected = Seq(.25f, 0, 0, 1)),
    Sample("volume last slice", .75f, .25f, dimensions = 3, z = .75f, mip = true, expected = Seq(4.25f, 0, 0, 1)),
    Sample(
      "volume eight-tap filtering",
      .5f,
      .5f,
      dimensions = 3,
      z = .5f,
      mip = true,
      filter = Filter.Linear,
      expected = Seq(2.5f, 0, 0, 1)
    ),
    Sample(
      "volume W repeat",
      .75f,
      .25f,
      dimensions = 3,
      z = 1.75f,
      mip = true,
      modeW = AddressMode.Repeat,
      expected = Seq(4.25f, 0, 0, 1)
    ),
    Sample(
      "volume W mirror",
      .75f,
      .25f,
      dimensions = 3,
      z = 1.75f,
      mip = true,
      modeW = AddressMode.MirroredRepeat,
      expected = Seq(.25f, 0, 0, 1)
    ),
    Sample(
      "volume W border",
      .75f,
      .25f,
      dimensions = 3,
      z = 1.75f,
      mip = true,
      modeW = AddressMode.ClampToBorder,
      expected = Seq(0, 0, 0, 1)
    ),
    Sample(
      "volume W border filtering",
      .5f,
      .5f,
      dimensions = 3,
      z = 1f,
      mip = true,
      modeW = AddressMode.ClampToBorder,
      filter = Filter.Linear,
      expected = Seq(2.25f, 0, 0, 1)
    ),
    Sample(
      "volume Z gradient selects mip",
      .75f,
      .25f,
      dimensions = 3,
      z = .75f,
      mip = true,
      maxLod = 1,
      operation = "grad",
      gradientZ = 1,
      expected = Seq(8, 0, 0, 1)
    ),
    Sample(
      "volume mip fetch",
      0,
      0,
      dimensions = 3,
      mip = true,
      lod = 1,
      operation = "fetch",
      expected = Seq(8, 0, 0, 1)
    ),
    Sample(
      "volume fetch invalid Z",
      0,
      0,
      dimensions = 3,
      z = 2,
      mip = true,
      operation = "fetch",
      expected = Seq(0, 0, 0, 0)
    ),
    Sample(
      "volume mip interpolation",
      .75f,
      .25f,
      dimensions = 3,
      z = .75f,
      mip = true,
      lod = .5f,
      maxLod = 1,
      mipmap = MipmapMode.Linear,
      expected = Seq(6.125f, 0, 0, 1)
    ),
    Sample(
      "volume mip view",
      .75f,
      .25f,
      dimensions = 3,
      z = .75f,
      mip = true,
      baseLevel = 1,
      expected = Seq(8, 0, 0, 1)
    ),
    Sample(
      "explicit x gradient",
      .75f,
      .25f,
      mip = true,
      maxLod = 1,
      operation = "grad",
      gradientX = 1,
      expected = Seq(2, 0, 0, 1)
    ),
    Sample(
      "explicit y gradient",
      .75f,
      .25f,
      mip = true,
      maxLod = 1,
      operation = "grad",
      gradientY = 1,
      expected = Seq(2, 0, 0, 1)
    ),
    Sample(
      "zero gradient magnifies",
      .75f,
      .25f,
      mip = true,
      maxLod = 1,
      operation = "grad",
      expected = Seq(.25f, 0, 0, 1)
    ),
    Sample("explicit LOD sampler bias", .75f, .25f, mip = true, bias = 1, maxLod = 1, expected = Seq(2, 0, 0, 1)),
    Sample(
      "explicit LOD negative sampler bias",
      .75f,
      .25f,
      mip = true,
      lod = 1,
      bias = -1,
      maxLod = 1,
      expected = Seq(.25f, 0, 0, 1)
    ),
    Sample(
      "out-of-range size query stays within view",
      0,
      0,
      mip = true,
      lod = 100,
      operation = "query",
      expected = Seq(1, 1, 2, 1)
    ),
    Sample(
      "negative size query stays within view",
      0,
      0,
      mip = true,
      lod = -1,
      operation = "query",
      expected = Seq(2, 2, 2, 1)
    ),
    Sample(
      "constant coordinate LOD query",
      0,
      0,
      mip = true,
      maxLod = 1,
      operation = "querylod",
      expected = Seq(0, Float.NegativeInfinity, 0, 1)
    ),
    Sample(
      "LOD query clamps level but preserves unclamped LOD",
      0,
      0,
      mip = true,
      minLod = .75f,
      maxLod = 1,
      mipmap = MipmapMode.Linear,
      operation = "querylod",
      expected = Seq(.75f, Float.NegativeInfinity, 0, 1)
    ),
    Sample("mip size and count", 0, 0, mip = true, lod = 1, operation = "query", expected = Seq(1, 1, 2, 1)),
    Sample(
      "view-relative size and count",
      0,
      0,
      mip = true,
      baseLevel = 1,
      operation = "query",
      expected = Seq(1, 1, 1, 1)
    ),
    Sample("mip texel fetch", 0, 0, mip = true, lod = 1, operation = "fetch", expected = Seq(2, 0, 0, 1)),
    Sample("mip texel fetch outside level", 1, 0, mip = true, lod = 1, operation = "fetch", expected = Seq(0, 0, 0, 0)),
    Sample("mip nearest", .75f, .25f, mip = true, lod = 1, maxLod = 1, expected = Seq(2, 0, 0, 1)),
    Sample(
      "mip linear",
      .75f,
      .25f,
      mip = true,
      lod = .5f,
      maxLod = 1,
      mipmap = MipmapMode.Linear,
      expected = Seq(1.125f, 0, 0, 1)
    ),
    Sample("view-relative mip", .75f, .25f, mip = true, baseLevel = 1, expected = Seq(2, 0, 0, 1)),
    Sample("minimum LOD clamp", .75f, .25f, mip = true, minLod = 1, maxLod = 1, expected = Seq(2, 0, 0, 1)),
    Sample("maximum LOD clamp", .75f, .25f, mip = true, lod = 1, expected = Seq(.25f, 0, 0, 1)),
    Sample("mip view aliases storage", .25f, .25f, mip = true, alias = true, expected = Seq(.625f, 0, 0, 1)),
    Sample("storage write visible through sampled alias", .25f, .25f, alias = true, expected = Seq(.625f, 0, 0, 1)),
    Sample("nearest", .75f, .25f, expected = Seq(.25f, 0, 0, 1)),
    Sample("linear", .5f, .5f, filter = Filter.Linear, expected = Seq(.5f, 0, 0, 1)),
    Sample("repeat", 1.75f, .75f, mode = AddressMode.Repeat, expected = Seq(1, 0, 0, 1)),
    Sample("mirrored repeat", 1.75f, .75f, mode = AddressMode.MirroredRepeat, expected = Seq(.75f, 0, 0, 1)),
    Sample("edge", -.25f, .75f, expected = Seq(.75f, 0, 0, 1)),
    Sample("transparent border", -.25f, .75f, mode = AddressMode.ClampToBorder, expected = Seq(0, 0, 0, 1)),
    Sample(
      "white border",
      -.25f,
      .75f,
      mode = AddressMode.ClampToBorder,
      border = BorderColour.FloatOpaqueWhite,
      expected = Seq(1, 0, 0, 1)
    ),
    Sample(
      "linear border",
      0,
      .25f,
      filter = Filter.Linear,
      mode = AddressMode.ClampToBorder,
      border = BorderColour.FloatOpaqueWhite,
      expected = Seq(.5f, 0, 0, 1)
    ),
    Sample("unnormalized", 1.5f, .5f, unnormalized = true, expected = Seq(.25f, 0, 0, 1)),
    Sample(
      "comparison before filtering",
      .5f,
      .5f,
      filter = Filter.Linear,
      compare = true,
      expected = Seq(.5f, 0, 0, 1)
    ),
    Sample(
      "offset before repeat",
      .25f,
      .25f,
      mode = AddressMode.Repeat,
      offset = Some((-1, -1)),
      expected = Seq(1, 0, 0, 1)
    ),
    Sample(
      "offset before linear filtering",
      0f,
      0f,
      filter = Filter.Linear,
      offset = Some((1, 1)),
      expected = Seq(.5f, 0, 0, 1)
    ),
    Sample(
      "offset before border",
      .25f,
      .25f,
      mode = AddressMode.ClampToBorder,
      border = BorderColour.FloatOpaqueWhite,
      offset = Some((-1, 0)),
      expected = Seq(1, 0, 0, 1)
    ),
    Sample(
      "offset at mip level",
      -6.5f,
      .5f,
      mip = true,
      lod = 1,
      maxLod = 1,
      offset = Some((7, 0)),
      expected = Seq(2, 0, 0, 1)
    ),
    Sample(
      "offset with gradient",
      -6.5f,
      .5f,
      mip = true,
      operation = "grad",
      gradientX = 1,
      maxLod = 1,
      offset = Some((7, 0)),
      expected = Seq(2, 0, 0, 1)
    ),
    Sample("offset shadow comparison", .25f, .25f, compare = true, offset = Some((0, 1)), expected = Seq(1, 0, 0, 1)),
    Sample(
      "offset preserves array layer",
      .25f,
      .25f,
      mip = true,
      arrayLayer = Some(1),
      offset = Some((1, 0)),
      expected = Seq(4.25f, 0, 0, 1)
    ),
    Sample("offset fetch outside", 0, 0, operation = "fetch", offset = Some((-1, 0)), expected = Seq(0, 0, 0, 0))
  ) ++
    (for {
      amount     <- List(-8, -1, 7)
      dimensions <- List(1, 2)
      operation  <- List("sample", "fetch")
    } yield Sample(
      s"${dimensions}D $operation offset $amount",
      if (operation == "fetch") 1f - amount.toFloat else .75f - amount.toFloat / 2f,
      .25f,
      dimensions = dimensions,
      operation = operation,
      offset = Some((amount, 0)),
      expected = Seq(.25f, 0, 0, 1)
    )) ++ List(-2f, .49f, .5f, .51f, 1.49f, 1.5f, 1.51f, 5f).zipWithIndex.map { (layer, index) =>
      val mode     = Seq("nearest", "linear", "mip", "grad")(index % 4)
      val selected = if (layer == .5f) 0 else Math.max(0, Math.min(2, Math.floor(layer + .5).toInt))
      val expected = mode match { case "linear" => .5f; case "mip" => 1.125f; case "grad" => 2f; case _ => .25f }
      Sample(
        s"array layer $layer $mode",
        if (mode == "linear") .5f else .75f,
        if (mode == "linear") .5f else .25f,
        filter = if (mode == "linear") Filter.Linear else Filter.Nearest,
        mip = true,
        arrayLayer = Some(layer),
        lod = if (mode == "mip") .5f else 0f,
        maxLod = if (mode == "mip" || mode == "grad") 1 else 0,
        mipmap = if (mode == "mip") MipmapMode.Linear else MipmapMode.Nearest,
        operation = if (mode == "grad") "grad" else "sample",
        gradientX = if (mode == "grad") 1 else 0,
        expected = Seq(expected + selected * 4f, 0f, 0f, 1f)
      )
    }

  // single-level views sample with implicit LOD in fragments, so they also run in a stage without derivatives
  for {
    (sample, index) <- samples.zipWithIndex
    stage           <-
      if (sample.operation == "sample" && !sample.mip) Seq("Fragment", Seq("Vertex", "GLCompute")(index % 2))
      else Seq(Seq("Fragment", "Vertex", "GLCompute")(index % 3))
  } {
    val combined = index / 3 % 2 == 1
    test(s"${sample.name} in $stage with ${if (combined) "combined" else "separate"} sampler") {
      check(sample, stage, combined)
    }
  }

  // depth compare, unnormalized coordinates and aliases with the other sampler kind; LOD queries in fragments
  for {
    (sample, index) <- samples.zipWithIndex
    if sample.compare || sample.unnormalized || sample.alias || sample.operation == "querylod"
  } {
    val querylod = sample.operation == "querylod"
    val stage    =
      if (querylod) "Fragment"
      else if (sample.operation == "sample" && !sample.mip) "Fragment"
      else Seq("Fragment", "Vertex", "GLCompute")(index % 3)
    val usual    = index / 3 % 2 == 1
    val combined = if (querylod) usual else !usual
    test(s"${sample.name} in $stage with ${if (combined) "combined" else "separate"} sampler") {
      check(sample, stage, combined)
    }
  }

  private def check(sample: Sample, stage: String, combined: Boolean): Unit = {
    val dimensions                 = sample.dimensions
    val arrayed                    = sample.arrayLayer.isDefined
    val implicitLod                = stage == "Fragment" && !sample.mip
    val (texelValues, secondLevel) =
      if (arrayed) (
        (0 until 3).flatMap(layer => Seq(0f, .25f, .75f, 1f).map(_ + layer * 4f)) ++
          (0 until 3).map(2f + _ * 4f),
        48L
      )
      else if (dimensions == 3) (Seq(0f, .25f, .75f, 1f, 4f, 4.25f, 4.75f, 5f, 8f), 32L)
      else if (sample.rgb) (Seq(0f, .25f, .75f, 1f).flatMap(red => Seq(red, .5f, 1f)), 0L)
      else (Seq(0f, .25f, .75f, 1f, 2f), 16L)
    val texelBytes = Vec.from(texelValues.flatMap(value => LittleEndian.toVec(floatBits(value), 4)))
    val memory     = RegionMemory()
    val texels     = memory.allocate(texelBytes)
    val levels     =
      if (!sample.mip) Vec(ImageLevel(2, if (dimensions == 1) 1 else 2))
      else if (sample.rgb) Vec(ImageLevel(width = 2, height = 2))
      else Vec(ImageLevel(width = 2, height = 2), ImageLevel(width = 1, height = 1, secondLevel)).drop(sample.baseLevel)
    val view = ImageView(
      if (sample.rgb) ImageDescriptor.Format.DecodedRgb32f else ImageFormat.R32f,
      levels,
      texelBytes.size.toLong,
      slices = if (arrayed) 3 else if (dimensions == 3) 2 >> sample.baseLevel else 1,
      volume = dimensions == 3,
      swizzle = sample.mapping.map(Vec.from(_))
    )
    val storage = ImageView(ImageFormat.R32f, Vec(ImageLevel(width = 2, height = 2)), 16L)
    val sampler = Sampler(
      minFilter = sample.filter,
      magFilter = sample.filter,
      addressU = sample.mode,
      addressV = sample.mode,
      addressW = sample.modeW,
      unnormalized = sample.unnormalized,
      compareOp = Option.when(sample.compare)(CompareOp.Less),
      borderColour = sample.border,
      mipLodBias = sample.bias,
      minLod = sample.minLod,
      maxLod = sample.maxLod,
      mipmapMode = sample.mipmap
    )
    val (viewAt, storageAt, samplerAt, combinedAt) = (0, 96, 160, 224)
    val descriptors                     = memory.allocate((combinedAt + CombinedBytes).toLong.refine[Long > 0].value)
    def binding(offset: Int, size: Int) =
      buffer(descriptors, offset.toLong.refine[Long >= 0].value, Some(size.toLong.refine[Long > 0].value))
    val out         = memory.allocate(16L)
    val relocations = Vec.from(
      Seq(writeImage(memory, descriptors, viewAt, view, texels)) ++
        Option.when(sample.alias)(writeImage(memory, descriptors, storageAt, storage, texels)) ++
        Option.when(combined)(writeCombined(memory, descriptors, combinedAt, viewAt -> view, sampler))
    )
    if (!combined) writeSampler(memory, descriptors, samplerAt, sampler)
    val arguments = Vec.from(
      (if (combined) Seq(binding(combinedAt, CombinedBytes))
       else Seq(binding(viewAt, view.size), binding(samplerAt, SamplerBytes))) ++ Seq(buffer(out)) ++
        Option.when(sample.alias)(binding(storageAt, storage.size))
    )
    val before      = memory.bytes(descriptors)
    val coordinates = Seq("%x") ++ (if (dimensions >= 2) Seq("%y") else Seq.empty) ++
      (if (arrayed || dimensions == 3) Seq("%layer") else Seq.empty) ++ sample.projectiveQ.map(_ => "%q")
    def vector(lanes: Int, scalar: String): String      = if (lanes == 1) s"%$scalar" else s"%v$lanes$scalar"
    def gradient(scalar: Float, lanes: String*): String =
      if (dimensions == 1) s"OpConstant %float $scalar"
      else s"OpConstantComposite ${vector(dimensions, "float")} ${lanes.take(dimensions).mkString(" ")}"
    def operands(mask: Option[String], ids: String*): String = {
      val masks = mask.toSeq ++ sample.offset.map(_ => "ConstOffset")
      if (masks.isEmpty) "" else (masks.mkString("|") +: (ids ++ sample.offset.map(_ => "%offset"))).mkString(" ")
    }
    val explicitOperands =
      if (sample.operation == "grad") operands(Some("Grad"), "%dx", "%dy") else operands(Some("Lod"), "%lod")
    val projective     = if (sample.projectiveQ.isDefined) "Proj" else ""
    val lodKind        = if (implicitLod) "Implicit" else "Explicit"
    val sampleOperands = if (implicitLod) operands(None) else explicitOperands
    val sizeLanes      = if (dimensions == 3) 3 else 2
    val operation      = sample.operation match {
      case "gather" if sample.compare =>
        s"%result = OpImageDrefGather %v4float %sampled %coord %reference ${operands(None)}"
      case "gather" => s"%result = OpImageGather %v4float %sampled %coord %component ${operands(None)}"
      case "sample" | "grad" if sample.compare =>
        s"""%depth = OpImageSample${projective}Dref${lodKind}Lod %float %sampled %coord %reference $sampleOperands
           |%result = OpCompositeConstruct %v4float %depth %zero %zero %one""".stripMargin
      case "sample" | "grad" =>
        s"%result = OpImageSample$projective${lodKind}Lod %v4float %sampled %coord $sampleOperands"
      case "fetch" =>
        s"""%fetched = OpImage %image_type %sampled
           |%texel = OpConvertFToS ${vector(coordinates.size, "int")} %coord
           |%result = OpImageFetch %v4float %fetched %texel ${operands(Some("Lod"), "%level")}""".stripMargin
      case "query" =>
        s"""%queried = OpImage %image_type %sampled
           |%levels = OpImageQueryLevels %uint %queried
           |%size = OpImageQuerySizeLod ${vector(sizeLanes, "uint")} %queried %level
           |%size_float = OpConvertUToF ${vector(sizeLanes, "float")} %size
           |%width = OpCompositeExtract %float %size_float 0
           |%height = OpCompositeExtract %float %size_float 1
           |${if (dimensions == 3) "%depth = OpCompositeExtract %float %size_float 2" else ""}
           |%level_count = OpConvertUToF %float %levels
           |%result = OpCompositeConstruct %v4float %width %height ${
            if (dimensions == 3) "%depth %level_count" else "%level_count %one"
          }""".stripMargin
      case "querylod" =>
        """%lods = OpImageQueryLod %v2float %sampled %coord
           |%lod_level = OpCompositeExtract %float %lods 0
           |%lod_unclamped = OpCompositeExtract %float %lods 1
           |%result = OpCompositeConstruct %v4float %lod_level %lod_unclamped %zero %one""".stripMargin
    }
    val (offsetX, offsetY) = sample.offset.getOrElse((0, 0))
    val depth              = if (sample.compare) 1 else 0
    val module             = spv"""
      $vulkan
      OpCapability ImageQuery
      OpCapability ImageGatherExtended
      ${if (dimensions == 1) "OpCapability Sampled1D" else ""}
      OpEntryPoint $stage %main "main" %image ${if (combined) "" else "%sampler"} %out ${
        if (sample.alias) "%storage" else ""
      }
      ${stage match {
        case "Fragment"  => "OpExecutionMode %main OriginUpperLeft"
        case "GLCompute" => "OpExecutionMode %main LocalSize 1 1 1"
        case _           => ""
      }}
      OpDecorate %image DescriptorSet 0
      OpDecorate %image Binding 0
      ${if (combined) "" else "OpDecorate %sampler DescriptorSet 0\nOpDecorate %sampler Binding 1"}
      OpDecorate %out DescriptorSet 0
      OpDecorate %out Binding 2
      ${if (sample.alias) "OpDecorate %storage DescriptorSet 0\nOpDecorate %storage Binding 3" else ""}
      OpDecorate %out_block Block
      OpMemberDecorate %out_block 0 Offset 0
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %int = OpTypeInt 32 1
      %float = OpTypeFloat 32
      %v2float = OpTypeVector %float 2
      %v3float = OpTypeVector %float 3
      %v4float = OpTypeVector %float 4
      %v2uint = OpTypeVector %uint 2
      %v3uint = OpTypeVector %uint 3
      %v2int = OpTypeVector %int 2
      %v3int = OpTypeVector %int 3
      %image_type = OpTypeImage %float ${dimensions}D $depth ${if (arrayed) 1 else 0} 0 1 Unknown
      %image_ptr = OpTypePointer UniformConstant %image_type
      %sampler_type = OpTypeSampler
      %sampler_ptr = OpTypePointer UniformConstant %sampler_type
      %sampled_type = OpTypeSampledImage %image_type
      %sampled_ptr = OpTypePointer UniformConstant %sampled_type
      %storage_type = OpTypeImage %float 2D 0 0 0 2 R32f
      %storage_ptr = OpTypePointer UniformConstant %storage_type
      %out_block = OpTypeStruct %v4float
      %out_ptr = OpTypePointer StorageBuffer %out_block
      %out_element_ptr = OpTypePointer StorageBuffer %v4float
      %fn = OpTypeFunction %void
      %image = OpVariable ${if (combined) "%sampled_ptr" else "%image_ptr"} UniformConstant
      ${if (combined) "" else "%sampler = OpVariable %sampler_ptr UniformConstant"}
      ${if (sample.alias) "%storage = OpVariable %storage_ptr UniformConstant" else ""}
      %out = OpVariable %out_ptr StorageBuffer
      %uint_0 = OpConstant %uint 0
      %component = OpConstant %uint ${sample.gatherComponent}
      %level = OpConstant %int ${sample.lod.toInt}
      %offset_x = OpConstant %int $offsetX
      %offset_y = OpConstant %int $offsetY
      %offset = ${
        if (dimensions == 1) s"OpConstant %int $offsetX" else "OpConstantComposite %v2int %offset_x %offset_y"
      }
      %zero = OpConstant %float 0
      %one = OpConstant %float 1
      %reference = OpConstant %float 0.5
      %x = OpConstant %float ${sample.x}
      %y = OpConstant %float ${sample.y}
      %layer = OpConstant %float ${sample.arrayLayer.getOrElse(sample.z)}
      %q = OpConstant %float ${sample.projectiveQ.getOrElse(1f)}
      %coord = ${
        if (coordinates.size == 1) s"OpConstant %float ${sample.x}"
        else s"OpConstantComposite ${vector(coordinates.size, "float")} ${coordinates.mkString(" ")}"
      }
      %gradient_x = OpConstant %float ${sample.gradientX}
      %gradient_y = OpConstant %float ${sample.gradientY}
      %gradient_z = OpConstant %float ${sample.gradientZ}
      %dx = ${gradient(sample.gradientX, "%gradient_x", "%zero", "%gradient_z")}
      %dy = ${gradient(sample.gradientY, "%zero", "%gradient_y", "%gradient_z")}
      %lod = OpConstant %float ${sample.lod}
      %int_0 = OpConstant %int 0
      %origin = OpConstantComposite %v2int %int_0 %int_0
      %written_value = OpConstant %float 0.625
      %written = OpConstantComposite %v4float %written_value %zero %zero %one
      %main = OpFunction %void None %fn
      %entry = OpLabel
      ${
        if (sample.alias) """%storage_image = OpLoad %storage_type %storage
                            |OpImageWrite %storage_image %origin %written""".stripMargin
        else ""
      }
      ${
        if (combined) "%sampled = OpLoad %sampled_type %image"
        else """%image_value = OpLoad %image_type %image
               |%sampler_value = OpLoad %sampler_type %sampler
               |%sampled = OpSampledImage %sampled_type %image_value %sampler_value""".stripMargin
      }
      $operation
      %out_element = OpAccessChain %out_element_ptr %out %uint_0
      OpStore %out_element %result
      OpReturn
      OpFunctionEnd
    """
    assertEquals(run(module, "main", memory, arguments, relocations = relocations), Right(()))
    assertEquals(words(memory, out, offset = 0, count = 4), Vec.from(sample.expected.map(floatBits)))
    assertEquals(memory.bytes(descriptors), before)
    val written =
      if (sample.alias) Vec.from(LittleEndian.toVec(floatBits(.625f), 4)) ++ texelBytes.drop(4) else texelBytes
    assertEquals(memory.bytes(texels), written)
  }
}
