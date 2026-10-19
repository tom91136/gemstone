package net.kurobako.gemstone.isa.spirv.graphics

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.graphics.GraphicsProtocol.*
import net.kurobako.gemstone.graphics.ShaderMetadata.*
import net.kurobako.gemstone.isa.spirv.*
import net.kurobako.gemstone.isa.spirv.ImageFormats.*
import net.kurobako.gemstone.vm.*
import scala.collection.mutable

private object DrawExecution {
  def blend(
      sourceValue: Array[Float],
      destinationValue: Array[Float],
      constantsValue: GraphicsColour,
      state: GraphicsBlend,
      format: GraphicsFormat
  ): Array[Float] = {
    val normalised                 = !ImageTexelCodec.floating(format.shaderFormat.code)
    val minimum                    = if (ImageTexelCodec.signed(format.shaderFormat.code)) -1f else 0f
    def clamp(value: Float): Float = if (normalised) math.max(minimum, math.min(1f, value)) else value
    val source                     = sourceValue.map(clamp(_)); val destination = destinationValue.map(clamp(_))
    val constants = Array(constantsValue.r, constantsValue.g, constantsValue.b, constantsValue.a).map(clamp(_))
    def factor(mode: GraphicsBlendFactor, lane: Int): Float = clamp(mode match {
      case GraphicsBlendFactor.Zero                  => 0f
      case GraphicsBlendFactor.One                   => 1f
      case GraphicsBlendFactor.SrcColor              => source(lane)
      case GraphicsBlendFactor.OneMinusSrcColor      => 1f - source(lane)
      case GraphicsBlendFactor.DstColor              => destination(lane)
      case GraphicsBlendFactor.OneMinusDstColor      => 1f - destination(lane)
      case GraphicsBlendFactor.SrcAlpha              => source(3)
      case GraphicsBlendFactor.OneMinusSrcAlpha      => 1f - source(3)
      case GraphicsBlendFactor.DstAlpha              => destination(3)
      case GraphicsBlendFactor.OneMinusDstAlpha      => 1f - destination(3)
      case GraphicsBlendFactor.ConstantColor         => constants(lane)
      case GraphicsBlendFactor.OneMinusConstantColor => 1f - constants(lane)
      case GraphicsBlendFactor.ConstantAlpha         => constants(3)
      case GraphicsBlendFactor.OneMinusConstantAlpha => 1f - constants(3)
      case GraphicsBlendFactor.SrcAlphaSaturate      => if (lane == 3) 1f else math.min(source(3), 1f - destination(3))
    })
    Array.tabulate(4) { lane =>
      val src = source(lane) * factor(if (lane == 3) state.sourceAlpha else state.sourceColour, lane)
      val dst = destination(lane) * factor(if (lane == 3) state.destinationAlpha else state.destinationColour, lane)
      val operation: GraphicsBlendOp = if (lane == 3) state.alphaOp else state.colourOp
      operation match {
        case GraphicsBlendOp.Add             => src + dst
        case GraphicsBlendOp.Subtract        => src - dst
        case GraphicsBlendOp.ReverseSubtract => dst - src
        case GraphicsBlendOp.Min             => math.min(source(lane), destination(lane))
        case GraphicsBlendOp.Max             => math.max(source(lane), destination(lane))
      }
    }
  }
}

private class DrawExecution(memory: GraphicsAccess, workgroups: Workgroups, handler: Event => Either[Fault, Unit]) {
  import ShaderRecords.*
  // Owned by one submission; no shader bytes or compiled state survive its completion.
  private val programs                          = mutable.Map.empty[Vec[Byte], VMProgram]
  private def load(image: Vec[Byte]): VMProgram =
    programs.getOrElseUpdate(
      image,
      GraphicsAccess.get(ISA.decode(image).left.map(error => Fault.InvalidRequest(error.message))
        .flatMap(LoadedProgram.load(_, Validation.Environment.VulkanGraphics, workgroups)))
    )
  def execute(draw: GraphicsDraw): Long = draw.indirect match {
    case None           => direct(draw)
    case Some(indirect) =>
      (0 until indirect.count).foldLeft(0L) { (samples, index) =>
        val offset               = index.toLong * indirect.stride
        def read(word: Int): Int = memory.word(indirect.memory, offset + word * 4, 4).toInt
        val call                 = GraphicsDrawCall(
          vertices = read(0).unsafe[Int >= 0],
          instances = read(1).unsafe[Int >= 0],
          firstVertex = read(2),
          firstInstance = read(if (draw.index.nonEmpty) 4 else 3),
          vertexOffset = if (draw.index.nonEmpty) read(3) else 0
        )
        samples + direct(draw.copy(call = call, indirect = None))
      }
  }
  private def direct(draw: GraphicsDraw): Long =
    if (draw.call.vertices == 0 || draw.call.instances == 0) 0L
    else try {
      val images                                          = ShaderDescriptors(memory)
      def prepare(shader: GraphicsShader): GraphicsShader = {
        val replacements =
          shader.descriptorBindings.map(binding => binding.argument.raw -> images.prepare(binding)).toMap
        shader.copy(
          arguments = Vec.from(shader.arguments.zipWithIndex.map { (argument, index) =>
            replacements.get(index).orElse(argument)
          }),
          relocations = shader.relocations ++ images.relocations,
          descriptorBindings = Vec.empty
        )
      }
      val prepared = draw.copy(vertex = prepare(draw.vertex), fragment = draw.fragment.map(prepare(_)))
      val output   = VertexStage.execute(prepared, memory, handler, load(prepared.vertex.image))
      if (prepared.raster.discard || !output.hasPosition) 0L else fragments(prepared, output)
    } finally memory.releaseScratch()

  private def fragments(draw: GraphicsDraw, output: VertexStage.Output): Long = {
    val rasteriser = Rasteriser(draw, memory)
    val fragments  = rasteriser.execute(output)
    val depth      = draw.depth.map(ImageStorage(_, memory))
    val early      = draw.fragment.exists(_.earlyFragmentTests)
    def test(fragment: Fragment, coverage: Int, overrideDepth: Option[Float]): Int = depth.fold(coverage) { target =>
      (0 until draw.raster.samples).foldLeft(coverage) { (mask, sample) =>
        if (
          (mask & (1 << sample)) != 0 && !FragmentOperations.test(
            target,
            target.offset(level = 0, layer = 0, fragment.x, fragment.y, 0, sample),
            overrideDepth.getOrElse(fragment.depth(sample)),
            fragment.front,
            draw.depthStencil
          )
        ) mask & ~(1 << sample)
        else mask
      }
    }
    val coverage     = fragments.map(f => if (early) test(f, f.coverage, None) else f.coverage).toArray
    val earlySamples = if (early) coverage.foldLeft(0L)((sum, mask) => sum + Integer.bitCount(mask)) else 0L
    val shader       = draw.fragment
    val count        = fragments.size.unsafe[Int >= 0]
    val records      = shader.fold(Array.empty[Array[Byte]])(allocate(_, count))
    shader.foreach { stage =>
      fields(stage, ShaderDirection.Input).foreach { (variable, field) =>
        fragments.zipWithIndex.foreach { (fragment, invocation) =>
          val offset   = invocation * stage.interfaces(variable).size + field.offset
          val data     = records(variable)
          val position =
            if (rasteriser.perSample) Rasteriser.samplePosition(draw.raster.samples, fragment.sample)
            else Rasteriser.PixelCentre
          val values = field.location match {
            case Some(location) => fragment.varying.getOrElse(
                location,
                throw IllegalArgumentException("missing fragment varying")
              ).drop(field.qualifiers.component).map(bits)
            case None => field.builtin.rawElements match {
                case Some(BuiltIn.FragCoord) => Array(
                    bits((fragment.x + position._1).toFloat),
                    bits((fragment.y + position._2).toFloat),
                    bits(fragment.z),
                    bits(fragment.reciprocalW)
                  )
                case Some(BuiltIn.PointCoord)       => Array(bits(fragment.pointCoord(0)), bits(fragment.pointCoord(1)))
                case Some(BuiltIn.FrontFacing)      => Array(if (fragment.front) 1 else 0)
                case Some(BuiltIn.HelperInvocation) => Array(if (coverage(invocation) == 0) 1 else 0)
                case Some(BuiltIn.SampleId)         => Array(fragment.sample)
                case Some(BuiltIn.SamplePosition)   =>
                  val p = Rasteriser.samplePosition(draw.raster.samples, fragment.sample);
                  Array(bits(p._1.toFloat), bits(p._2.toFloat))
                case Some(BuiltIn.SampleMask)  => Array(coverage(invocation))
                case Some(BuiltIn.DeviceIndex) => Array(0)
                case Some(BuiltIn.ViewIndex)   => Array(draw.viewIndex.raw)
                case _                         => throw IllegalArgumentException("unsupported fragment builtin")
              }
          }
          if (field.size == 1) data(offset) = values(0).toByte
          else {
            require(field.size % 4 == 0 && field.size / 4 <= values.length, "invalid fragment field width");
            for (lane <- 0 until field.size / 4) put(data, offset + lane * 4, values(lane))
          }
        }
      }
      val executor = ShaderExecutor(stage, memory, handler, load(stage.image))
      executor.execute(count, records)
    }
    val outputs = shader.toVector.flatMap(stage => fields(stage, ShaderDirection.Output))
    def read(variable: Int, fieldOffset: Int, invocation: Int): Int =
      word(records(variable), invocation * shader.get.interfaces(variable).size + fieldOffset)
    outputs.foreach { (variable, field) =>
      if (field.builtin.exists(_.raw == BuiltIn.SampleMask))
        fragments.indices.foreach(i => coverage(i) &= read(variable, field.offset, i))
      if (draw.raster.alphaToCoverage && field.location.exists(_ == 0)) fragments.indices.foreach { i =>
        val alpha = if (field.size >= 16) float(read(variable, field.offset.raw + 12, i)) else 1f
        val count = if (alpha.isNaN) 0 else math.round(math.max(0f, math.min(1f, alpha)) * draw.raster.samples)
        coverage(i) &= (1 << count) - 1
      }
    }
    if (!early) fragments.indices.foreach { i =>
      val overrideDepth = outputs.collectFirst {
        case (variable, field) if field.builtin.exists(_.raw == BuiltIn.FragDepth) =>
          float(read(variable, field.offset, i))
      }
      coverage(i) = test(fragments(i), coverage(i), overrideDepth)
    }
    val colours = outputs.filter(_._2.location.nonEmpty).groupBy(_._2.location.get.raw)
    colours.foreach { (location, fields) =>
      draw.colours.lift(location).flatten.foreach { image =>
        val target = ImageStorage(image, memory)
        val blend  = draw.blends(location)
        fragments.indices.foreach { i =>
          if (coverage(i) != 0) {
            val colour = Array(0, 0, 0, if (ImageTexelCodec.integer(image.format.shaderFormat.code)) 1 else bits(1f))
            fields.foreach { (variable, field) =>
              for (lane <- 0 until field.size / 4)
                colour(field.qualifiers.component + lane) = read(variable, field.offset.raw + lane * 4, i)
            }
            for (sample <- 0 until draw.raster.samples if (coverage(i) & (1 << sample)) != 0) {
              val offset = target.offset(level = 0, layer = 0, fragments(i).x, fragments(i).y, 0, sample)
              val value  = if (blend.enabled) DrawExecution.blend(
                colour.map(float),
                target.colour(offset),
                draw.blendConstants,
                blend,
                image.format
              ).map(bits)
              else colour
              target.writeColour(offset, value, blend.writeMask)
            }
          }
        }
      }
    }
    if (early) earlySamples else coverage.foldLeft(0L)((sum, mask) => sum + Integer.bitCount(mask))
  }
}
