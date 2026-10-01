package net.kurobako.gemstone.isa.spirv.v1_6.graphics

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.graphics.GraphicsProtocol.*
import net.kurobako.gemstone.graphics.ShaderMetadata.*
import net.kurobako.gemstone.isa.spirv.v1_6.*
import net.kurobako.gemstone.isa.spirv.v1_6.ImageFormats.*
import net.kurobako.gemstone.vm.*
import scala.collection.mutable

private class DrawExecution(memory: GraphicsAccess, workgroups: Workgroups, handler: Event => Either[Fault, Unit]) {
  import ShaderRecords.*
  // Owned by one submission; no shader bytes or compiled state survive its completion.
  private val programs                              = mutable.Map.empty[Vec[Byte], () => LoadedProgram]
  private def load(image: Vec[Byte]): LoadedProgram = {
    val factory = programs.getOrElseUpdate(
      image, {
        val compiled = GraphicsAccess.get(ISA.decode(image).left.map(error => Fault.InvalidTask(error.message))
          .flatMap(VMCompiler.compile(_, Validation.Environment.VulkanGraphics)))
        () => VMProgramRuntime(compiled)
      }
    )
    factory()
  }
  def execute(draw: GraphicsDraw): Long = draw.indirect match {
    case None           => direct(draw)
    case Some(indirect) =>
      (0 until indirect.count.raw).foldLeft(0L) { (samples, index) =>
        val offset               = index.toLong * indirect.stride.raw
        def read(word: Int): Int = memory.word(indirect.memory, offset + word * 4, 4).toInt
        val call                 = GraphicsDrawCall(
          read(0).unsafe[Int >= 0],
          read(1).unsafe[Int >= 0],
          read(2),
          read(if (draw.index.nonEmpty) 4 else 3),
          if (draw.index.nonEmpty) read(3) else 0
        )
        samples + direct(draw.copy(call = call, indirect = None))
      }
  }
  private def direct(draw: GraphicsDraw): Long =
    if (draw.call.vertices.raw == 0 || draw.call.instances.raw == 0) 0L
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
      val output   = VertexStage.execute(prepared, memory, workgroups, handler, load(prepared.vertex.image))
      if (prepared.raster.discard || !output.hasPosition) 0L else fragments(prepared, output)
    } finally memory.releaseScratch()

  private def fragments(draw: GraphicsDraw, output: VertexStage.Output): Long = {
    val rasterizer = Rasterizer(draw, memory)
    val fragments  = rasterizer.execute(output)
    val depth      = draw.depth.map(ImageStorage(_, memory))
    val early      = draw.fragment.exists(_.earlyFragmentTests)
    def test(fragment: Fragment, coverage: Int, overrideDepth: Option[Float]): Int = depth.fold(coverage) { target =>
      (0 until draw.raster.samples.raw).foldLeft(coverage) { (mask, sample) =>
        if (
          (mask & (1 << sample)) != 0 && !FragmentOperations.test(
            target,
            target.offset(0, 0, fragment.x, fragment.y, 0, sample),
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
    val records      = shader.fold(Array.empty[Array[Byte]])(allocate(_, fragments.size))
    shader.foreach { stage =>
      fields(stage, ShaderDirection.Input).foreach { (variable, field) =>
        fragments.zipWithIndex.foreach { (fragment, invocation) =>
          val offset   = invocation * stage.interfaces(variable).size.raw + field.offset.raw
          val data     = records(variable)
          val position =
            if (rasterizer.perSample) Rasterizer.samplePosition(draw.raster.samples.raw, fragment.sample) else (.5, .5)
          val values = field.location match {
            case Some(location) => fragment.varying.getOrElse(
                location.raw,
                throw IllegalArgumentException("missing fragment varying")
              ).drop(field.qualifiers.component.raw).map(bits)
            case None => field.builtin.map(_.raw) match {
                case Some(BuiltIn.FragCoord) => Array(
                    bits((fragment.x + position._1).toFloat),
                    bits((fragment.y + position._2).toFloat),
                    bits(fragment.z),
                    bits(fragment.reciprocalW)
                  )
                case Some(BuiltIn.PointCoord)       => fragment.pointCoord.map(bits)
                case Some(BuiltIn.FrontFacing)      => Array(if (fragment.front) 1 else 0)
                case Some(BuiltIn.HelperInvocation) => Array(if (coverage(invocation) == 0) 1 else 0)
                case Some(BuiltIn.SampleId)         => Array(fragment.sample)
                case Some(BuiltIn.SamplePosition)   =>
                  val p = Rasterizer.samplePosition(draw.raster.samples.raw, fragment.sample);
                  Array(bits(p._1.toFloat), bits(p._2.toFloat))
                case Some(BuiltIn.SampleMask)  => Array(coverage(invocation))
                case Some(BuiltIn.DeviceIndex) => Array(0)
                case Some(BuiltIn.ViewIndex)   => Array(draw.viewIndex.raw)
                case _                         => throw IllegalArgumentException("unsupported fragment builtin")
              }
          }
          if (field.size.raw == 1) data(offset) = values(0).toByte
          else {
            require(field.size.raw % 4 == 0 && field.size.raw / 4 <= values.length, "invalid fragment field width");
            for (lane <- 0 until field.size.raw / 4) put(data, offset + lane * 4, values(lane))
          }
        }
      }
      val executor = ShaderExecutor(stage, memory, workgroups, handler, load(stage.image))
      executor.execute(fragments.size, records)
    }
    val outputs = shader.toVector.flatMap(stage => fields(stage, ShaderDirection.Output))
    def read(variable: Int, fieldOffset: Int, invocation: Int): Int =
      word(records(variable), invocation * shader.get.interfaces(variable).size.raw + fieldOffset)
    outputs.foreach { (variable, field) =>
      if (field.builtin.exists(_.raw == BuiltIn.SampleMask))
        fragments.indices.foreach(i => coverage(i) &= read(variable, field.offset.raw, i))
      if (draw.raster.alphaToCoverage && field.location.exists(_.raw == 0)) fragments.indices.foreach { i =>
        val alpha = if (field.size.raw >= 16) float(read(variable, field.offset.raw + 12, i)) else 1f
        val count = if (alpha.isNaN) 0 else math.round(math.max(0f, math.min(1f, alpha)) * draw.raster.samples.raw)
        coverage(i) &= (1 << count) - 1
      }
    }
    if (!early) fragments.indices.foreach { i =>
      val overrideDepth = outputs.collectFirst {
        case (variable, field) if field.builtin.exists(_.raw == BuiltIn.FragDepth) =>
          float(read(variable, field.offset.raw, i))
      }
      coverage(i) = test(fragments(i), coverage(i), overrideDepth)
    }
    val colors = outputs.filter(_._2.location.nonEmpty).groupBy(_._2.location.get.raw)
    colors.foreach { (location, fields) =>
      draw.colors.lift(location).flatten.foreach { image =>
        val target = ImageStorage(image, memory)
        val blend  = draw.blends(location)
        fragments.indices.foreach { i =>
          if (coverage(i) != 0) {
            val color = Array(0, 0, 0, if (ImageTexelCodec.integer(image.format.shaderFormat.code)) 1 else bits(1f))
            fields.foreach { (variable, field) =>
              for (lane <- 0 until field.size.raw / 4)
                color(field.qualifiers.component.raw + lane) = read(variable, field.offset.raw + lane * 4, i)
            }
            for (sample <- 0 until draw.raster.samples.raw if (coverage(i) & (1 << sample)) != 0) {
              val offset = target.offset(0, 0, fragments(i).x, fragments(i).y, 0, sample)
              val value  = if (blend.enabled) FragmentOperations.blend(
                color.map(float),
                target.color(offset),
                draw.blendConstants,
                blend,
                image.format
              ).map(bits)
              else color
              target.writeColor(offset, value, blend.writeMask)
            }
          }
        }
      }
    }
    if (early) earlySamples else coverage.foldLeft(0L)((sum, mask) => sum + Integer.bitCount(mask))
  }
}
