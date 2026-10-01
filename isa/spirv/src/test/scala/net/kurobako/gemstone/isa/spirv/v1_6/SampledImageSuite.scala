package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.spirv.v1_6.execution.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*

class SampledImageSuite extends munit.FunSuite {
  private case class Sample(
      name: String,
      x: Float,
      y: Float,
      filter: Int = 0,
      mode: Int = 2,
      border: Int = 0,
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
      mipmap: Int = 0,
      baseLevel: Int = 0,
      operation: String = "sample",
      gradientX: Float = 0,
      gradientY: Float = 0,
      arrayLayer: Option[Float] = None,
      dimensions: Int = 2,
      z: Float = 0,
      gradientZ: Float = 0,
      modeW: Int = 2,
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
      filter = 1,
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
      mapping = Some(Seq(6, 1, 2, 3)),
      gatherComponent = 3,
      expected = Seq(.75f, 1f, .25f, 0f)
    ),
    Sample("gather clamp border", 0f, 0f, operation = "gather", mode = 3, border = 4, expected = Seq(1f, 0f, 1f, 1f)),
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
      mapping = Some(Seq(3, 6, 1, 2)),
      operation = "fetch",
      expected = Seq(.25f, 1f, 0f, 1f)
    ),
    Sample(
      "RGB view channel permutation",
      .75f,
      .25f,
      mip = true,
      rgb = true,
      mapping = Some(Seq(4, 5, 6, 3)),
      expected = Seq(.5f, 1f, 1f, .25f)
    ),
    Sample(
      "RGB view swizzled border constants",
      -1f,
      -1f,
      mode = 3,
      mip = true,
      rgb = true,
      mapping = Some(Seq(1, 2, 3, 6)),
      expected = Seq(0f, 1f, 0f, 1f)
    ),
    Sample(
      "R32 view missing channels and constants",
      .75f,
      .25f,
      mip = true,
      mapping = Some(Seq(6, 1, 2, 3)),
      expected = Seq(1f, 0f, 1f, .25f)
    ),
    Sample("decoded RGB view stride", .75f, .25f, mip = true, rgb = true, expected = Seq(.25f, .5f, 1f, 1f)),
    Sample(
      "decoded RGB view filtering",
      .5f,
      .5f,
      filter = 1,
      mip = true,
      rgb = true,
      expected = Seq(.5f, .5f, 1f, 1f)
    ),
    Sample(
      "decoded RGB transparent border retains absent alpha",
      -1f,
      -1f,
      mode = 3,
      border = 0,
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
      filter = 1,
      expected = Seq(2.5f, 0, 0, 1)
    ),
    Sample(
      "volume W repeat",
      .75f,
      .25f,
      dimensions = 3,
      z = 1.75f,
      mip = true,
      modeW = 0,
      expected = Seq(4.25f, 0, 0, 1)
    ),
    Sample(
      "volume W mirror",
      .75f,
      .25f,
      dimensions = 3,
      z = 1.75f,
      mip = true,
      modeW = 1,
      expected = Seq(.25f, 0, 0, 1)
    ),
    Sample("volume W border", .75f, .25f, dimensions = 3, z = 1.75f, mip = true, modeW = 3, expected = Seq(0, 0, 0, 1)),
    Sample(
      "volume W border filtering",
      .5f,
      .5f,
      dimensions = 3,
      z = 1f,
      mip = true,
      modeW = 3,
      filter = 1,
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
      z = 0,
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
      mipmap = 1,
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
      mipmap = 1,
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
    Sample("mip linear", .75f, .25f, mip = true, lod = .5f, maxLod = 1, mipmap = 1, expected = Seq(1.125f, 0, 0, 1)),
    Sample("view-relative mip", .75f, .25f, mip = true, baseLevel = 1, expected = Seq(2, 0, 0, 1)),
    Sample("minimum LOD clamp", .75f, .25f, mip = true, minLod = 1, maxLod = 1, expected = Seq(2, 0, 0, 1)),
    Sample("maximum LOD clamp", .75f, .25f, mip = true, lod = 1, expected = Seq(.25f, 0, 0, 1)),
    Sample("mip view aliases storage", .25f, .25f, mip = true, alias = true, expected = Seq(.625f, 0, 0, 1)),
    Sample("storage write visible through sampled alias", .25f, .25f, alias = true, expected = Seq(.625f, 0, 0, 1)),
    Sample("nearest", .75f, .25f, expected = Seq(.25f, 0, 0, 1)),
    Sample("linear", .5f, .5f, filter = 1, expected = Seq(.5f, 0, 0, 1)),
    Sample("repeat", 1.75f, .75f, mode = 0, expected = Seq(1, 0, 0, 1)),
    Sample("mirrored repeat", 1.75f, .75f, mode = 1, expected = Seq(.75f, 0, 0, 1)),
    Sample("edge", -.25f, .75f, expected = Seq(.75f, 0, 0, 1)),
    Sample("transparent border", -.25f, .75f, mode = 3, expected = Seq(0, 0, 0, 1)),
    Sample("white border", -.25f, .75f, mode = 3, border = 4, expected = Seq(1, 0, 0, 1)),
    Sample("linear border", 0, .25f, filter = 1, mode = 3, border = 4, expected = Seq(.5f, 0, 0, 1)),
    Sample("unnormalized", 1.5f, .5f, unnormalized = true, expected = Seq(.25f, 0, 0, 1)),
    Sample("comparison before filtering", .5f, .5f, filter = 1, compare = true, expected = Seq(.5f, 0, 0, 1))
  )
  for {
    model    <- List(0, 4, 5)
    combined <- List(false, true)
    sample   <- samples ++
      (for {
        amount     <- -8 to 7
        dimensions <- List(1, 2)
        operation  <- List("sample", "fetch")
      } yield Sample(
        s"$dimensions-dimensional $operation offset $amount",
        if (operation == "fetch") 1f - amount.toFloat else .75f - amount.toFloat / 2f,
        .25f,
        dimensions = dimensions,
        operation = operation,
        offset = Some((amount, 0)),
        expected = Seq(.25f, 0, 0, 1)
      )) ++ List(
        Sample("offset before repeat", .25f, .25f, mode = 0, offset = Some((-1, -1)), expected = Seq(1, 0, 0, 1)),
        Sample(
          "offset before linear filtering",
          0f,
          0f,
          filter = 1,
          offset = Some((1, 1)),
          expected = Seq(.5f, 0, 0, 1)
        ),
        Sample(
          "offset before border",
          .25f,
          .25f,
          mode = 3,
          border = 4,
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
        Sample(
          "offset shadow comparison",
          .25f,
          .25f,
          compare = true,
          offset = Some((0, 1)),
          expected = Seq(1, 0, 0, 1)
        ),
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
        layer <- List(-2f, .49f, .5f, .51f, 1.49f, 1.5f, 1.51f, 5f)
        mode  <- List("nearest", "linear", "mip", "grad")
      } yield {
        val selected = if (layer == .5f) 0 else Math.max(0, Math.min(2, Math.floor(layer + .5).toInt))
        val expected = mode match { case "linear" => .5f; case "mip" => 1.125f; case "grad" => 2f; case _ => .25f }
        Sample(
          s"array layer $layer $mode",
          if (mode == "linear") .5f else .75f,
          if (mode == "linear") .5f else .25f,
          filter = if (mode == "linear") 1 else 0,
          mip = true,
          arrayLayer = Some(layer),
          lod = if (mode == "mip") .5f else 0f,
          maxLod = if (mode == "mip" || mode == "grad") 1 else 0,
          mipmap = if (mode == "mip") 1 else 0,
          operation = if (mode == "grad") "grad" else "sample",
          gradientX = if (mode == "grad") 1 else 0,
          expected = Seq(expected + selected * 4f, 0f, 0f, 1f)
        )
      })
  }
    test(s"stage $model combined=$combined ${sample.name}") {
      def ref(n: Int)              = operand("IdRef", n)
      def dest(n: Int)             = operand("IdResult", n)
      def lit(n: Int)              = operand("LiteralInteger", n)
      def fc(n: Int, value: Float) = Instruction.Constant(
        ref(3),
        dest(n),
        operand("LiteralContextDependentNumber", java.lang.Float.floatToRawIntBits(value))
      )
      def imageOperands(mask: Int, ids: Int*) = operand(
        "ImageOperands",
        (Seq(mask | (if (sample.offset.isDefined) 8 else 0)) ++ ids ++
          (if (sample.offset.isDefined) Seq(64) else Seq.empty))*
      )
      val implicitOperands = sample.offset.map(_ => imageOperands(0))
      val coordinateType   = sample.dimensions +
        (if (sample.arrayLayer.isDefined || sample.projectiveQ.isDefined) 1 else 0) match {
        case 1 => 3
        case 2 => 4
        case 3 => 60
        case _ => 5
      }
      val fetchCoordinateType = sample.dimensions + (if (sample.arrayLayer.isDefined) 1 else 0) match {
        case 1 => 2
        case 2 => 43
        case _ => 65
      }
      val ordinarySampling = if (model == 4 && !sample.mip) {
        if (sample.compare)
          Instruction.ImageSampleDrefImplicitLod(ref(3), dest(39), ref(32), ref(15), ref(18), implicitOperands)
        else Instruction.ImageSampleImplicitLod(ref(5), dest(24), ref(32), ref(15), implicitOperands)
      } else {
        if (sample.compare) Instruction.ImageSampleDrefExplicitLod(
          ref(3),
          dest(39),
          ref(32),
          ref(15),
          ref(18),
          if (sample.operation == "grad") imageOperands(4, 56, 57) else imageOperands(2, 38)
        )
        else Instruction.ImageSampleExplicitLod(
          ref(5),
          dest(24),
          ref(32),
          ref(15),
          if (sample.operation == "grad") imageOperands(4, 56, 57) else imageOperands(2, 38)
        )
      }
      val sampling = if (sample.operation == "gather") {
        if (sample.compare) Instruction.ImageDrefGather(ref(5), dest(24), ref(32), ref(15), ref(18), implicitOperands)
        else Instruction.ImageGather(ref(5), dest(24), ref(32), ref(15), ref(69), implicitOperands)
      } else if (sample.projectiveQ.isEmpty) ordinarySampling
      else ordinarySampling match {
        case Instruction.ImageSampleImplicitLod(t, d, i, c, m) => Instruction.ImageSampleProjImplicitLod(t, d, i, c, m)
        case Instruction.ImageSampleExplicitLod(t, d, i, c, m) => Instruction.ImageSampleProjExplicitLod(t, d, i, c, m)
        case Instruction.ImageSampleDrefImplicitLod(t, d, i, c, r, m) =>
          Instruction.ImageSampleProjDrefImplicitLod(t, d, i, c, r, m)
        case Instruction.ImageSampleDrefExplicitLod(t, d, i, c, r, m) =>
          Instruction.ImageSampleProjDrefExplicitLod(t, d, i, c, r, m)
        case _ => fail("unexpected sampling operation")
      }
      val module = Module(
        Header(ISA.version, Word(0), Word(70), Word(0)),
        Vec(
          Instruction.Capability(operand("Capability", Capability.Shader)),
          Instruction.MemoryModel(
            operand("AddressingModel", AddressingModel.Logical),
            operand("MemoryModel", MemoryModel.GLSL450)
          ),
          Instruction.EntryPoint(
            operand("ExecutionModel", model),
            ref(21),
            stringOperand("main"),
            (if (combined) Vec(ref(12), ref(13)) else Vec(ref(12), ref(30), ref(13))) ++
              (if (sample.alias) Vec(ref(42)) else Vec.empty[Operand])
          )
        ) ++
          (model match {
            case 5 =>
              Vec(Instruction.ExecutionMode(ref(21), operand("ExecutionMode", ExecutionMode.LocalSize, 1, 1, 1)))
            case 4 => Vec(Instruction.ExecutionMode(ref(21), operand("ExecutionMode", ExecutionMode.OriginUpperLeft)))
            case _ => Vec.empty[Instruction]
          }) ++
          Vec(
            Instruction.Decorate(ref(12), operand("Decoration", Decoration.Binding, 0)),
            Instruction.Decorate(ref(12), operand("Decoration", Decoration.DescriptorSet, 0)),
            Instruction.Decorate(ref(13), operand("Decoration", Decoration.Binding, 2)),
            Instruction.Decorate(ref(13), operand("Decoration", Decoration.DescriptorSet, 0)),
            Instruction.Decorate(ref(8), operand("Decoration", Decoration.Block)),
            Instruction.MemberDecorate(ref(8), lit(0), operand("Decoration", Decoration.Offset, 0)),
            Instruction.TypeVoid(dest(1)),
            Instruction.TypeInt(dest(2), lit(32), lit(0)),
            Instruction.TypeFloat(dest(3), lit(32), None),
            Instruction.TypeVector(dest(4), ref(3), lit(2)),
            Instruction.TypeVector(dest(60), ref(3), lit(3)),
            Instruction.TypeVector(dest(5), ref(3), lit(4)),
            Instruction.TypeVector(dest(43), ref(2), lit(2)),
            Instruction.TypeVector(dest(65), ref(2), lit(3)),
            Instruction.TypeImage(
              dest(6),
              ref(3),
              operand("Dim", sample.dimensions - 1),
              lit(if (sample.compare) 1 else 0),
              lit(if (sample.arrayLayer.isDefined) 1 else 0),
              lit(0),
              lit(1),
              operand("ImageFormat", ImageFormat.Unknown),
              None
            ),
            Instruction.TypePointer(dest(7), operand("StorageClass", StorageClass.UniformConstant), ref(6)),
            Instruction.TypeSampler(dest(26)),
            Instruction.TypePointer(dest(27), operand("StorageClass", StorageClass.UniformConstant), ref(26)),
            Instruction.TypeSampledImage(dest(28), ref(6)),
            Instruction.TypePointer(dest(29), operand("StorageClass", StorageClass.UniformConstant), ref(28)),
            Instruction.TypeStruct(dest(8), Vec(ref(5))),
            Instruction.TypePointer(dest(9), operand("StorageClass", StorageClass.StorageBuffer), ref(8)),
            Instruction.TypePointer(dest(10), operand("StorageClass", StorageClass.StorageBuffer), ref(5)),
            Instruction.TypeFunction(dest(11), ref(1), Vec.empty),
            Instruction.Variable(
              ref(if (combined) 29 else 7),
              dest(12),
              operand("StorageClass", StorageClass.UniformConstant),
              None
            ),
            Instruction.Variable(ref(9), dest(13), operand("StorageClass", StorageClass.StorageBuffer), None)
          ) ++
          (if (sample.alias) Vec(
             Instruction.TypeImage(
               dest(40),
               ref(3),
               operand("Dim", Dim.Value2D),
               lit(0),
               lit(0),
               lit(0),
               lit(2),
               operand("ImageFormat", ImageFormat.R32f),
               None
             ),
             Instruction.TypePointer(dest(41), operand("StorageClass", StorageClass.UniformConstant), ref(40)),
             Instruction.Decorate(ref(42), operand("Decoration", Decoration.Binding, 3)),
             Instruction.Decorate(ref(42), operand("Decoration", Decoration.DescriptorSet, 0)),
             Instruction.Variable(ref(41), dest(42), operand("StorageClass", StorageClass.UniformConstant), None)
           )
           else Vec.empty[Instruction]) ++
          (if (combined) Vec.empty[Instruction]
           else Vec(
             Instruction.Decorate(ref(30), operand("Decoration", Decoration.Binding, 1)),
             Instruction.Decorate(ref(30), operand("Decoration", Decoration.DescriptorSet, 0)),
             Instruction.Variable(ref(27), dest(30), operand("StorageClass", StorageClass.UniformConstant), None)
           )) ++
          Vec(
            Instruction.Constant(ref(2), dest(14), operand("LiteralContextDependentNumber", 0)),
            Instruction.Constant(ref(2), dest(51), operand("LiteralContextDependentNumber", sample.lod.toInt)),
            Instruction.Constant(
              ref(2),
              dest(62),
              operand("LiteralContextDependentNumber", sample.offset.fold(0)(_._1))
            ),
            Instruction.Constant(
              ref(2),
              dest(63),
              operand("LiteralContextDependentNumber", sample.offset.fold(0)(_._2))
            ),
            if (sample.dimensions == 1) Instruction.Constant(
              ref(2),
              dest(64),
              operand("LiteralContextDependentNumber", sample.offset.fold(0)(_._1))
            )
            else Instruction.ConstantComposite(ref(43), dest(64), Vec(ref(62), ref(63))),
            fc(68, sample.projectiveQ.getOrElse(1f)),
            Instruction.Constant(ref(2), dest(69), operand("LiteralContextDependentNumber", sample.gatherComponent)),
            fc(58, sample.gradientX),
            fc(59, sample.gradientY),
            fc(66, sample.gradientZ),
            fc(61, sample.arrayLayer.getOrElse(sample.z)),
            fc(16, sample.x),
            fc(17, sample.y),
            fc(18, .5f),
            fc(19, 0),
            if (sample.dimensions == 1) fc(56, sample.gradientX)
            else if (sample.dimensions == 3)
              Instruction.ConstantComposite(ref(60), dest(56), Vec(ref(58), ref(19), ref(66)))
            else Instruction.ConstantComposite(ref(4), dest(56), Vec(ref(58), ref(19))),
            if (sample.dimensions == 1) fc(57, sample.gradientY)
            else if (sample.dimensions == 3)
              Instruction.ConstantComposite(ref(60), dest(57), Vec(ref(19), ref(59), ref(66)))
            else Instruction.ConstantComposite(ref(4), dest(57), Vec(ref(19), ref(59))),
            fc(38, sample.lod),
            fc(20, 1),
            if (coordinateType == 3) fc(15, sample.x)
            else Instruction.ConstantComposite(
              ref(coordinateType),
              dest(15),
              (if (sample.dimensions == 1) Vec(ref(16)) else Vec(ref(16), ref(17))) ++
                (if (sample.arrayLayer.isDefined || sample.dimensions == 3) Vec(ref(61)) else Vec.empty[Operand]) ++
                (if (sample.projectiveQ.isDefined) Vec(ref(68)) else Vec.empty)
            ),
            fc(46, .625f),
            Instruction.ConstantComposite(ref(5), dest(47), Vec(ref(46), ref(19), ref(19), ref(20)))
          ) ++
          (if (sample.alias) Vec(Instruction.ConstantComposite(ref(43), dest(44), Vec(ref(14), ref(14))))
           else Vec.empty[Instruction]) ++
          Vec(
            Instruction.Function(ref(1), dest(21), operand("FunctionControl", FunctionControl.None), ref(11)),
            Instruction.Label(dest(22))
          ) ++
          (if (sample.alias) Vec(
             Instruction.Load(ref(40), dest(45), ref(42), None),
             Instruction.ImageWrite(ref(45), ref(44), ref(47), None)
           )
           else Vec.empty[Instruction]) ++
          (if (combined) Vec(Instruction.Load(ref(28), dest(32), ref(12), None))
           else Vec(
             Instruction.Load(ref(6), dest(23), ref(12), None),
             Instruction.Load(ref(26), dest(31), ref(30), None),
             Instruction.SampledImage(ref(28), dest(32), ref(23), ref(31))
           )) ++
          (sample.operation match {
            case "querylod" => Vec(
                Instruction.ImageQueryLod(ref(4), dest(50), ref(32), ref(15)),
                Instruction.CompositeExtract(ref(3), dest(53), ref(50), Vec(lit(0))),
                Instruction.CompositeExtract(ref(3), dest(54), ref(50), Vec(lit(1))),
                Instruction.CompositeConstruct(ref(5), dest(24), Vec(ref(53), ref(54), ref(19), ref(20)))
              )
            case "query" => Vec(
                Instruction.Image(ref(6), dest(48), ref(32)),
                Instruction.ImageQueryLevels(ref(2), dest(49), ref(48)),
                Instruction.ImageQuerySizeLod(ref(if (sample.dimensions == 3) 65 else 43), dest(50), ref(48), ref(51)),
                Instruction.ConvertUToF(ref(if (sample.dimensions == 3) 60 else 4), dest(52), ref(50)),
                Instruction.CompositeExtract(ref(3), dest(53), ref(52), Vec(lit(0))),
                Instruction.CompositeExtract(ref(3), dest(54), ref(52), Vec(lit(1))),
                Instruction.ConvertUToF(ref(3), dest(55), ref(49)),
                if (sample.dimensions == 3) Instruction.CompositeExtract(ref(3), dest(67), ref(52), Vec(lit(2)))
                else Instruction.Nop(),
                Instruction.CompositeConstruct(
                  ref(5),
                  dest(24),
                  if (sample.dimensions == 3) Vec(ref(53), ref(54), ref(67), ref(55))
                  else Vec(ref(53), ref(54), ref(55), ref(20))
                )
              )
            case "fetch" => Vec(
                Instruction.Image(ref(6), dest(48), ref(32)),
                Instruction.ConvertFToS(ref(fetchCoordinateType), dest(44), ref(15)),
                Instruction.ImageFetch(ref(5), dest(24), ref(48), ref(44), Some(imageOperands(2, 51)))
              )
            case _ => Vec(sampling)
          }) ++
          (if (sample.compare && sample.operation != "gather")
             Vec(Instruction.CompositeConstruct(ref(5), dest(24), Vec(ref(39), ref(19), ref(19), ref(20))))
           else Vec.empty[Instruction]) ++
          Vec(
            Instruction.AccessChain(ref(10), dest(25), ref(13), Vec(ref(14))),
            Instruction.Store(ref(25), ref(24), None),
            Instruction.Return(),
            Instruction.FunctionEnd()
          ),
        Endianness.Little
      )

      val program = LoadedProgram.load(
        module,
        if (model == 5) Validation.Environment.VulkanCompute else Validation.Environment.VulkanGraphics
      ).fold(error => fail(error.message), identity)
      val region                          = AllocationId(508L); val memory = ArrayMemory(region, 512)
      def write(offset: Long, word: Long) = assertEquals(
        memory.write(region, offset.unsafe[Long >= 0], LittleEndian.toVec(word, 4)),
        Right(())
      )
      for (
        (
          offset,
          word
        ) <- Seq(0L -> 0xfeedL, 8L -> 2L, 12L -> 2L, 16L -> 1L, 20L -> 3L, 40L -> 0xbeefL, 72L -> 32L, 160L -> 0xf00dL)
      ) write(offset, word)
      if (sample.dimensions == 1) write(12, 1)
      for ((v, i) <- Seq(0f, .25f, .75f, 1f).zipWithIndex)
        write(24L + i * 4, java.lang.Float.floatToRawIntBits(v).toLong)
      val samplerWords = Seq(
        sample.filter,
        sample.filter,
        sample.mode,
        sample.mode,
        if (sample.unnormalized) 1 else 0,
        if (sample.compare) 1 else 0,
        1,
        sample.border,
        java.lang.Float.floatToRawIntBits(sample.bias),
        java.lang.Float.floatToRawIntBits(sample.minLod),
        java.lang.Float.floatToRawIntBits(sample.maxLod),
        sample.mipmap
      )
      for ((v, i) <- samplerWords.zipWithIndex) write(80L + i * 4, v.toLong)
      val viewOffset = if (sample.mip) 176L else 8L
      val viewSize   =
        if (sample.mip) 40L + (2 - sample.baseLevel) * 16 + (if (sample.mapping.isDefined) 16 else 0) else 32L
      if (sample.mip) {
        write(48, java.lang.Float.floatToRawIntBits(2.0f).toLong)
        write(72, viewSize)
        write(176, if (sample.baseLevel == 0) 2 else 1); write(180, if (sample.baseLevel == 0) 2 else 1)
        write(184, 1); write(188, 0x80000003L); write(200, 28); write(208, 2 - sample.baseLevel)
        for (level <- sample.baseLevel until 2) {
          val at = 216L + (level - sample.baseLevel) * 16
          write(at, if (level == 0) 2 else 1); write(at + 4, if (level == 0) 2 else 1)
          write(at + 8, if (level == 0) 0 else 24)
        }
      }
      if (sample.arrayLayer.isDefined) {
        write(184, 3); write(200, 60); write(240, 48)
        for (layer <- 0 until 3) {
          for ((value, index) <- Seq(0f, .25f, .75f, 1f).zipWithIndex)
            write(300 + layer * 16 + index * 4, java.lang.Float.floatToRawIntBits(value + layer * 4f).toLong)
          write(348 + layer * 4, java.lang.Float.floatToRawIntBits(2f + layer * 4f).toLong)
        }
      }
      if (sample.dimensions == 3) {
        write(128, sample.modeW)
        write(184, if (sample.baseLevel == 0) 2 else 1); write(188, 0xc0000003L); write(200, 36)
        for (level <- sample.baseLevel until 2) write(224 + (level - sample.baseLevel) * 16, if (level == 0) 0 else 32)
        for ((value, index) <- Seq(0f, .25f, .75f, 1f, 4f, 4.25f, 4.75f, 5f, 8f).zipWithIndex)
          write(300 + index * 4, java.lang.Float.floatToRawIntBits(value).toLong)
      }
      if (sample.rgb) {
        write(188, 0x800003f3L); write(200, 48); write(208, 1)
        for {
          (red, index)  <- Seq(0f, .25f, .75f, 1f).zipWithIndex
          (value, lane) <- Seq(red, .5f, 1f).zipWithIndex
        }
          write(300 + index * 12 + lane * 4, java.lang.Float.floatToRawIntBits(value).toLong)
      }
      sample.mapping.foreach { mapping =>
        write(188, 0xa0000000L | (if (sample.rgb) 1011 else 3))
        val levels = if (sample.rgb) 1 else 2 - sample.baseLevel
        for ((component, lane) <- mapping.zipWithIndex) write(176 + 40 + levels * 16 + lane * 4, component.toLong)
      }
      val args =
        (if (combined) Vec(KernelTask.Argument.Memory(
           region,
           64L,
           Some(if (sample.dimensions == 3) 68L
           else 64L)
         ))
         else Vec(
           KernelTask.Argument.Memory(
             region,
             viewOffset.unsafe[Long >= 0],
             Some(viewSize.unsafe[Long > 0])
           ),
           KernelTask.Argument.Memory(
             region,
             80L,
             Some(if (sample.dimensions == 3) 52L
             else 48L)
           )
         )) ++ Vec(KernelTask.Argument.Memory(
          region,
          144L,
          Some(16L)
        )) ++
          (if (sample.alias) Vec(KernelTask.Argument.Memory(
             region,
             8L,
             Some(32L)
           ))
           else Vec.empty[KernelTask.Argument])
      val relocations =
        (if (combined)
           Vec(KernelTask.PointerRelocation(
             KernelTask.RelocationSource.Memory(region),
             64L,
             region,
             viewOffset.unsafe[Long >= 0]
           ))
         else Vec.empty[KernelTask.PointerRelocation]) ++
          (if (sample.mip)
             Vec(KernelTask.PointerRelocation(
               KernelTask.RelocationSource.Memory(region),
               192L,
               region,
               if (sample.arrayLayer.isDefined || sample.dimensions == 3 || sample.rgb)
                 300L
               else 24L
             ))
           else Vec.empty[KernelTask.PointerRelocation])
      val task = KernelTask.validated(
        "main",
        LaunchGeometry.Dimensions.One,
        LaunchGeometry.Dimensions.One,
        args,
        pointerRelocations = relocations
      ).fold(fail(_), identity)
      val _ = program.execute(task, memory, TaskControl()).fold(error => fail(error.message), identity)
      for ((expected, lane) <- sample.expected.zipWithIndex) assertEquals(
        LittleEndian.decode(memory.bytes, 144 + lane * 4, 4),
        java.lang.Float.floatToRawIntBits(expected).toLong & 0xffffffffL
      )
      for ((offset, expected) <- Seq(0 -> 0xfeedL, 40 -> 0xbeefL, 160 -> 0xf00dL))
        assertEquals(LittleEndian.decode(memory.bytes, offset, 4), expected)
    }
}
