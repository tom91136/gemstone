package net.kurobako.gemstone.isa.spirv.v1_6.graphics

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.graphics.GraphicsProtocol.*
import net.kurobako.gemstone.isa.spirv.v1_6.*
import net.kurobako.gemstone.isa.spirv.v1_6.ImageFormats.*
import net.kurobako.gemstone.vm.KernelTask.*
import scala.collection.mutable

private object ShaderDescriptors {
  def validate(binding: DescriptorBinding): Unit = binding.descriptor match {
    case GraphicsDescriptor.Image(image, _)             => validateImage(image)
    case GraphicsDescriptor.Sampler(sampler)            => validateSampler(sampler)
    case GraphicsDescriptor.Combined(image, _, sampler) => validateImage(image); validateSampler(sampler)
  }
  private def validateSampler(sampler: GraphicsSampler): Unit = {
    require(
      Vector(sampler.mipLodBias, sampler.minLod, sampler.maxLod).forall(java.lang.Float.isFinite(_)),
      "non-finite sampler LOD"
    )
    require(sampler.minLod <= sampler.maxLod, "sampler LOD range is reversed")
  }
  private def validateImage(image: GraphicsImage): Unit = {
    if (image.format.compression != GraphicsCompression.None) {
      require(image.samples.raw == 1, "compressed image cannot be multisampled")
      val _ = decodedLayout(image)
    } else {
      // The shader descriptor ABI has implicit tight pitches. Padded transfer images must not use it.
      image.levels.foreach { level =>
        val row = Math.multiplyExact(
          Math.multiplyExact(level.extent.width.raw.toLong, image.format.texelBytes.raw.toLong),
          image.samples.raw.toLong
        )
        val slice = Math.multiplyExact(row, level.extent.height.raw.toLong)
        val layer = Math.multiplyExact(slice, level.extent.depth.raw.toLong)
        require(
          level.rowPitch.raw == row && level.slicePitch.raw == slice && level.layerPitch.raw == layer,
          "shader image storage must have tight pitches"
        )
      }
    }
    require(image.volume || image.levels.forall(_.extent.depth.raw == 1), "array image has volume depth")
    image.levels.zipWithIndex.foreach { (level, index) =>
      require(
        index < 32 && level.extent.depth.raw == math.max(1, image.extent.depth.raw >>> index),
        "shader image mip depth is inconsistent"
      )
    }
  }
  // Compute and validate the entire decoded allocation before allocating any pixels.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def decodedLayout(image: GraphicsImage): (NonEmpty[Vec[GraphicsImageLevel]], Int > 0) = {
    val unit              = image.format.decoded.texelBytes.toLong
    var offset: Long >= 0 = 0L
    val levels            = image.levels.mapNonEmpty { level =>
      val row   = level.extent.width.toLong.widen[Long > 0] * unit
      val slice = row * level.extent.height.toLong
      val layer = slice * level.extent.depth.toLong
      val end   = offset + layer * image.layers.toLong
      require(end.raw <= Int.MaxValue, "decoded image exceeds allocation limit")
      val result = GraphicsImageLevel(level.extent, offset, row, slice, layer)
      offset = end
      result
    }
    val last = levels.last
    val size = last.layerPitch * image.layers.toLong + last.offset
    (levels, size.toIntExact)
  }
}

/** Descriptors and any decoded pixels belong to one draw/dispatch. Sharing ends before another command can change the
  * source.
  */
private class ShaderDescriptors(memory: GraphicsAccess) {
  import ImageDescriptor.*
  private val decoded                     = mutable.Map.empty[GraphicsImage, GraphicsImage]
  private val descriptors                 = mutable.Map.empty[(GraphicsImage, GraphicsComponents), GraphicsRange]
  private val pointers                    = mutable.ArrayBuffer.empty[PointerRelocation]
  def relocations: Vec[PointerRelocation] = Vec.from(pointers)

  private def allocate(bytes: Array[Byte]): GraphicsRange = {
    val (region, size) = memory.allocate(bytes)
    GraphicsRange(region, 0L, size)
  }
  private def point(source: GraphicsRange, offset: Int >= 0, target: GraphicsRange): Unit =
    pointers += PointerRelocation(
      RelocationSource.Memory(source.allocation),
      offset.toLong,
      target.allocation,
      target.offset
    )

  private def decode(image: GraphicsImage): GraphicsImage = decoded.getOrElseUpdate(
    image, {
      val (levels, size) = ShaderDescriptors.decodedLayout(image)
      val result         = image.copy(
        memory = allocate(Array.ofDim[Byte](size.raw)),
        levels = levels,
        format = image.format.decoded
      )
      ImageOperations(memory).decode(image, result)
      result
    }
  )
  private def imageDescriptor(source: GraphicsImage, components: GraphicsComponents): GraphicsRange =
    descriptors.getOrElseUpdate(
      (source, components), {
        val image    = if (source.format.compression == GraphicsCompression.None) source else decode(source)
        val swizzled = components.values.zipWithIndex.exists((value, lane) =>
          value != GraphicsSwizzle.Identity && value.ordinal != GraphicsSwizzle.R.ordinal + lane
        )
        val tableEnd = Layout.LevelTable + image.levels.size * Layout.LevelBytes
        val bytes    = Array.ofDim[Byte](tableEnd + (if (swizzled) Layout.SwizzleBytes else 0))
        def word(offset: Int, value: Long): Unit = LittleEndian.write(value, bytes, offset, 4)
        word(Layout.Width, image.extent.width.raw)
        word(Layout.Height, image.extent.height.raw)
        word(Layout.Slices, if (image.volume) image.extent.depth.raw else image.layers.raw)
        word(
          Layout.Format,
          image.format.shaderFormat.code.toLong | Flags.LevelTable |
            (if (image.volume) Flags.Volume else 0L) | (if (swizzled) Flags.Swizzle else 0L)
        )
        LittleEndian.write(image.memory.size.raw, bytes, Layout.TexelBytes, 8)
        word(Layout.Levels, image.levels.size)
        word(Layout.Samples, if (image.samples.raw == 1) 0 else image.samples.raw)
        image.levels.zipWithIndex.foreach { (level, index) =>
          val base = Layout.LevelTable + index * Layout.LevelBytes
          word(base + Layout.LevelWidth, level.extent.width.raw)
          word(base + Layout.LevelHeight, level.extent.height.raw)
          LittleEndian.write(level.offset.raw, bytes, base + Layout.LevelOffset, 8)
        }
        if (swizzled) components.values.zipWithIndex.foreach((value, lane) => word(tableEnd + lane * 4, value.ordinal))
        val result = allocate(bytes)
        point(result, Layout.Texels, image.memory)
        result
      }
    )
  private def samplerBytes(sampler: GraphicsSampler, prefix: Int): Array[Byte] = {
    val bytes                              = Array.ofDim[Byte](prefix + SamplerWord.Count * 4)
    def word(index: Int, value: Int): Unit = LittleEndian.write(value.toLong, bytes, prefix + index * 4, 4)
    word(SamplerWord.MinFilter, sampler.minFilter.ordinal)
    word(SamplerWord.MagFilter, sampler.magFilter.ordinal)
    word(SamplerWord.AddressU, sampler.addressU.ordinal)
    word(SamplerWord.AddressV, sampler.addressV.ordinal)
    word(SamplerWord.AddressW, sampler.addressW.ordinal)
    word(SamplerWord.Unnormalized, if (sampler.unnormalized) 1 else 0)
    word(SamplerWord.CompareEnable, if (sampler.compareEnable) 1 else 0)
    word(SamplerWord.CompareOp, sampler.compareOp.ordinal)
    word(SamplerWord.BorderColor, sampler.borderColor.ordinal)
    word(SamplerWord.MipLodBias, java.lang.Float.floatToRawIntBits(sampler.mipLodBias))
    word(SamplerWord.MinLod, java.lang.Float.floatToRawIntBits(sampler.minLod))
    word(SamplerWord.MaxLod, java.lang.Float.floatToRawIntBits(sampler.maxLod))
    word(SamplerWord.MipmapMode, sampler.mipmapMode.ordinal)
    bytes
  }
  private def samplerDescriptor(sampler: GraphicsSampler, image: Option[GraphicsRange]): GraphicsRange = {
    val bytes = samplerBytes(sampler, if (image.nonEmpty) Layout.InlineHeaderBytes else 0)
    image.foreach(range => LittleEndian.write(range.size.raw, bytes, 8, 8))
    val result = allocate(bytes)
    image.foreach(point(result, 0, _))
    result
  }
  def prepare(binding: DescriptorBinding): GraphicsRange = {
    memory.checkpoint()
    binding.descriptor match {
      case GraphicsDescriptor.Image(image, components)             => imageDescriptor(image, components)
      case GraphicsDescriptor.Sampler(sampler)                     => samplerDescriptor(sampler, None)
      case GraphicsDescriptor.Combined(image, components, sampler) =>
        samplerDescriptor(sampler, Some(imageDescriptor(image, components)))
    }
  }
}
