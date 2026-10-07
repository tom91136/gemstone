package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*

class InputAttachmentSuite extends munit.FunSuite {
  private case class Format(name: String, value: Int, components: Int, texelBytes: Int, integer: Boolean = false)

  private val formats = Seq(
    Format("Rgba32f", ImageFormat.Rgba32f, 4, 16),
    Format("R32f", ImageFormat.R32f, 1, 4),
    Format("Rgba8", ImageFormat.Rgba8, 4, 4),
    Format("Rg32f", ImageFormat.Rg32f, 2, 8),
    Format("Rgba32i", ImageFormat.Rgba32i, 4, 16, integer = true),
    Format("Rgba8i", ImageFormat.Rgba8i, 4, 4, integer = true),
    Format("Rgba32ui", ImageFormat.Rgba32ui, 4, 16, integer = true),
    Format("Rgba8ui", ImageFormat.Rgba8ui, 4, 4, integer = true),
    Format("R32ui", ImageFormat.R32ui, 1, 4, integer = true),
    Format("Rg32ui", ImageFormat.Rg32ui, 2, 8, integer = true),
    Format("D16Unorm", ImageDescriptor.Format.D16Unorm, 1, 2),
    Format("Bgra8Unorm", ImageDescriptor.Format.Bgra8Unorm, 4, 4),
    Format("D32SfloatS8Depth", ImageDescriptor.Format.D32SfloatS8Depth, 1, 8),
    Format("S8Uint", ImageDescriptor.Format.S8Uint, 1, 1, integer = true),
    Format("D32SfloatS8Stencil", ImageDescriptor.Format.D32SfloatS8Stencil, 1, 8, integer = true)
  )

  private val signed = Set(ImageFormat.Rgba32i, ImageFormat.Rgba8i)

  private val variants = formats.flatMap { format =>
    if (!format.integer) Seq(format -> None)
    else Seq(format                 -> None, format -> Some(if (signed(format.value)) "SignExtend" else "ZeroExtend"))
  }

  private case class Read(
      variant: (Format, Option[String]),
      sampled: Boolean,
      samples: Int,
      sample: Int,
      pixel: (Int, Int),
      layer: Option[Int]
  )

  private val sampleIndices = Seq((1, 0), (4, 0), (4, 3), (4, -1), (4, 4))
  private val pixels        = Seq((0, 0), (2, 1), (-1, 0), (3, 0), (0, 2))
  private val layers        = Seq(None, Some(0), Some(1), Some(-1), Some(2))

  // every format at an in-range address, then the bounds of each address dimension with formats in rotation
  private val reads = variants.zipWithIndex.flatMap { (variant, index) =>
    Seq(
      Read(variant, sampled = false, if (index % 2 == 0) 1 else 4, if (index % 2 == 0) 0 else 3, (2, 1), None),
      Read(variant, sampled = true, 4, 3, (2, 1), Option.when(index % 2 == 1)(1))
    )
  } ++ pairwise(sampleIndices.size, pixels.size).zipWithIndex.map { (row, index) =>
    val (samples, sample) = sampleIndices(row(0))
    Read(variants(index % variants.size), sampled = false, samples, sample, pixels(row(1)), None)
  } ++ pairwise(sampleIndices.size - 1, layers.size, pixels.size).zipWithIndex.map { (row, index) =>
    val (samples, sample) = sampleIndices(row(0) + 1)
    Read(variants((index + 7) % variants.size), sampled = true, samples, sample, pixels(row(2)), layers(row(1)))
  } ++ Seq(
    "Rgba32i"            -> Some("SignExtend"),
    "Rgba8i"             -> Some("SignExtend"),
    "Rgba32ui"           -> Some("ZeroExtend"),
    "Rgba8ui"            -> Some("ZeroExtend"),
    "Bgra8Unorm"         -> None,
    "S8Uint"             -> None,
    "D32SfloatS8Stencil" -> None
  ).map((name, extension) => Read(formats.find(_.name == name).get -> extension, sampled = false, 1, 0, (2, 1), None))

  for (read <- reads.distinct) {
    val (format, extension) = read.variant
    val image               =
      if (read.sampled) s"2D multisampled${read.layer.fold("")(layer => s" array layer $layer")}" else "SubpassData"
    val name = s"$image ${format.name}${extension.fold("")(" " + _)}"
    test(s"$name with ${read.samples} samples reads sample ${read.sample} at ${read.pixel}")(check(read))
  }

  private def check(read: Read): Unit = {
    val Read((format, extension), sampled, samples, sample, (x, y), layer) = read
    val arrayed                                                            = layer.isDefined
    val slices                                                             = if (arrayed) 2 else 1
    val component                                                          =
      if (!format.integer) "%float" else if (signed(format.value) && extension.isEmpty) "%int" else "%uint"
    val masks    = Option.when(samples > 1)("Sample").toSeq ++ extension
    val operands = (masks.mkString("|") +: Option.when(samples > 1)("%sample").toSeq).filter(_.nonEmpty).mkString(" ")
    val module   = spv"""
      $vulkan
      ${if (sampled) "" else "OpCapability InputAttachment"}
      ${if (sampled && arrayed) "OpCapability ImageMSArray" else ""}
      ${if (sampled) "OpCapability ImageQuery" else ""}
      OpEntryPoint Fragment %main "main" %image %out %frag_coord
      OpExecutionMode %main OriginUpperLeft
      OpDecorate %image DescriptorSet 0
      OpDecorate %image Binding 0
      ${if (sampled) "" else "OpDecorate %image InputAttachmentIndex 0"}
      OpDecorate %out DescriptorSet 0
      OpDecorate %out Binding 1
      OpDecorate %frag_coord BuiltIn FragCoord
      OpDecorate %out_block Block
      OpMemberDecorate %out_block 0 Offset 0
      OpMemberDecorate %out_block 1 Offset 16
      OpMemberDecorate %out_block 2 Offset 24
      %void = OpTypeVoid
      %int = OpTypeInt 32 1
      %uint = OpTypeInt 32 0
      %float = OpTypeFloat 32
      %v4float = OpTypeVector %float 4
      %texel_type = OpTypeVector $component 4
      %image_type = OpTypeImage $component ${if (sampled) "2D" else "SubpassData"} 0 ${if (arrayed) 1 else 0} ${
        if (samples == 1) 0 else 1
      } ${if (sampled) 1 else 2} Unknown
      %coord_type = OpTypeVector %int ${if (arrayed) 3 else 2}
      %image_ptr = OpTypePointer UniformConstant %image_type
      %out_block = OpTypeStruct %texel_type %int %coord_type
      %out_ptr = OpTypePointer StorageBuffer %out_block
      %texel_ptr = OpTypePointer StorageBuffer %texel_type
      %int_ptr = OpTypePointer StorageBuffer %int
      %coord_ptr = OpTypePointer StorageBuffer %coord_type
      %input_ptr = OpTypePointer Input %v4float
      %fn = OpTypeFunction %void
      %image = OpVariable %image_ptr UniformConstant
      %out = OpVariable %out_ptr StorageBuffer
      %frag_coord = OpVariable %input_ptr Input
      %int_0 = OpConstant %int 0
      %int_1 = OpConstant %int 1
      %int_2 = OpConstant %int 2
      %unqueried_size = OpConstantComposite %coord_type %int_1 %int_2 ${if (arrayed) "%int_2" else ""}
      %x = OpConstant %int ${if (sampled) x else 0}
      %y = OpConstant %int ${if (sampled) y else 0}
      %layer = OpConstant %int ${layer.getOrElse(0)}
      %coord = OpConstantComposite %coord_type %x %y ${if (arrayed) "%layer" else ""}
      %sample = OpConstant %int $sample
      %main = OpFunction %void None %fn
      %entry = OpLabel
      %image_value = OpLoad %image_type %image
      %texel = ${if (sampled) "OpImageFetch" else "OpImageRead"} %texel_type %image_value %coord $operands
      %texel_element = OpAccessChain %texel_ptr %out %int_0
      OpStore %texel_element %texel
      %samples = ${if (sampled) "OpImageQuerySamples %int %image_value" else "OpCopyObject %int %int_1"}
      %size = ${
        if (sampled) "OpImageQuerySize %coord_type %image_value" else "OpCopyObject %coord_type %unqueried_size"
      }
      %samples_element = OpAccessChain %int_ptr %out %int_1
      %size_element = OpAccessChain %coord_ptr %out %int_2
      OpStore %samples_element %samples
      OpStore %size_element %size
      OpReturn
      OpFunctionEnd
    """
    val memory = RegionMemory()
    val texels = memory.allocate((slices * 6 * samples * format.texelBytes).toLong.refine[Long > 0].value)
    val bytes  = memory.array(texels)
    def put(at: Int, value: Long, width: Int in 0 ~ 8 = 4): Unit = LittleEndian.write(value, bytes, at, width)
    for {
      imageLayer <- 0 until slices
      pixel      <- 0 until 6
      slot       <- 0 until samples
    } {
      val at   = ((imageLayer * 6 + pixel) * samples + slot) * format.texelBytes
      val base = imageLayer * 96 + pixel * 16 + slot * 4 + 1
      format.value match {
        case ImageFormat.Rgba32f | ImageFormat.R32f | ImageFormat.Rg32f | ImageDescriptor.Format.D32SfloatS8Depth =>
          for (lane <- 0 until format.components) put(at + lane * 4, floatBits((base + lane).toFloat / 8))
        case ImageFormat.Rgba32i | ImageFormat.Rgba32ui | ImageFormat.R32ui | ImageFormat.Rg32ui =>
          for (lane <- 0 until format.components) put(at + lane * 4, 0x80000000L + base + lane)
        case ImageDescriptor.Format.D16Unorm           => put(at, base * 257L, 2)
        case ImageDescriptor.Format.S8Uint             => put(at, base.toLong, 1)
        case ImageDescriptor.Format.D32SfloatS8Stencil =>
          put(at, floatBits(.75f)); put(at + 4, base.toLong, 1); put(at + 5, 0xa5L, 1)
        case _ => put(at, (0 until 4).foldLeft(0L)((word, lane) => word | ((base + lane).toLong << (lane * 8))))
      }
    }
    val view = ImageView(
      format.value,
      Vec(ImageLevel(3, 2)),
      (slices * 6 * samples * format.texelBytes).toLong,
      slices = slices,
      samples = samples
    )
    val size       = view.size.toLong.refine[Long > 0].value
    val descriptor = memory.allocate(size)
    val relocation = writeImage(memory, descriptor, 0, view, texels)
    val out        = memory.allocate(40L)
    val fragCoord  = memory.allocate(Vec.from(
      Seq(x + .5f, y + .5f, .5f, 1f).flatMap(value => LittleEndian.toVec(floatBits(value), 4))
    ))
    val arguments = Vec(buffer(descriptor, range = Some(size)), buffer(out), buffer(fragCoord))
    assertEquals(run(module, "main", memory, arguments, relocations = Vec(relocation)), Right(()))
    val valid = x >= 0 && x < 3 && y >= 0 && y < 2 && sample >= 0 && sample < samples &&
      layer.forall(value => value >= 0 && value < slices)
    val base     = layer.getOrElse(0) * 96 + (y * 3 + x) * 16 + sample * 4 + 1
    val expected = Vec.tabulate(4) { lane =>
      if (!valid) 0L
      else if (format.integer) format.value match {
        case ImageDescriptor.Format.S8Uint | ImageDescriptor.Format.D32SfloatS8Stencil =>
          if (lane == 0) base.toLong else if (lane == 3) 1L else 0L
        case ImageFormat.Rgba8i  => (base + lane).toByte.toLong & 0xffffffffL
        case ImageFormat.Rgba8ui => (base + lane).toLong
        case _ => if (lane < format.components) 0x80000000L + base + lane else if (lane == 3) 1L else 0L
      }
      else floatBits(
        if (lane >= format.components) { if (lane == 3) 1f else 0f }
        else format.value match {
          case ImageDescriptor.Format.D16Unorm   => base * 257f / 65535f
          case ImageFormat.Rgba8                 => (base + lane).toFloat / 255f
          case ImageDescriptor.Format.Bgra8Unorm => (base + (if (lane < 3) 2 - lane else lane)).toFloat / 255f
          case _                                 => (base + lane).toFloat / 8f
        }
      )
    }
    assertEquals(words(memory, out, 0, 4), expected)
    assertEquals(word(memory, out, 16), if (sampled) 4L else 1L)
    assertEquals(words(memory, out, 24, 2), Vec(if (sampled) 3L else 1L, 2L))
    if (arrayed) assertEquals(word(memory, out, 32), 2L)
  }
}
