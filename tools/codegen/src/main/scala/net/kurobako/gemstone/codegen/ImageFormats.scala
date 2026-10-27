package net.kurobako.gemstone.codegen

import com.monovore.decline.*
import java.nio.file.Path
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.string.*

/** Shared texel layout facts. Algorithms remain in the VM; frozen test references stay independent. */
object ImageFormats {
  private enum Encoding derives CanEqual { case Unorm, Half, Ufloat, SharedExponent }
  private enum Layout derives CanEqual   {
    case Floating
    case Integer(componentBytes: Int in (1, 2, 4), signed: Boolean, packed: Boolean)
    case Normalised(bits: Int in (8, 16), signed: Boolean, srgb: Boolean, bgra: Boolean)
    case Narrow(
        bytes: Int in (2, 4, 8),
        encoding: Encoding,
        widths: Vector[Int in 1 ~ 32],
        shifts: Vector[Int in 0 ~ 31]
    )
  }
  private case class Row(vulkan: Vector[String], shader: String, components: Int in 1 ~ 4, layout: Layout)
  import Layout.*

  private val LayoutFamilies = Vector("Integer", "Normalised", "Narrow")

  private val rows = Vector(
    Row(Vector("R32_SFLOAT"), "ImageFormat.R32f", 1, Floating),
    Row(Vector("R32G32_SFLOAT"), "ImageFormat.Rg32f", 2, Floating),
    Row(Vector("R32G32B32A32_SFLOAT"), "ImageFormat.Rgba32f", 4, Floating),
    Row(Vector.empty, "ImageDescriptor.Format.DecodedRgb32f", 3, Floating),
    Row(
      Vector("A2B10G10R10_UINT_PACK32"),
      "ImageFormat.Rgb10a2ui",
      4,
      Integer(componentBytes = 4, signed = false, packed = true)
    ),
    Row(
      Vector("R8_UINT"),
      "ImageFormat.R8ui",
      1,
      Integer(componentBytes = 1, signed = false, packed = false)
    ),
    Row(
      Vector("R8_SINT"),
      "ImageFormat.R8i",
      1,
      Integer(componentBytes = 1, signed = true, packed = false)
    ),
    Row(
      Vector("R8G8_UINT"),
      "ImageFormat.Rg8ui",
      2,
      Integer(componentBytes = 1, signed = false, packed = false)
    ),
    Row(
      Vector("R8G8_SINT"),
      "ImageFormat.Rg8i",
      2,
      Integer(componentBytes = 1, signed = true, packed = false)
    ),
    Row(
      Vector("A8B8G8R8_UINT_PACK32", "R8G8B8A8_UINT"),
      "ImageFormat.Rgba8ui",
      4,
      Integer(componentBytes = 1, signed = false, packed = false)
    ),
    Row(
      Vector("A8B8G8R8_SINT_PACK32", "R8G8B8A8_SINT"),
      "ImageFormat.Rgba8i",
      4,
      Integer(componentBytes = 1, signed = true, packed = false)
    ),
    Row(
      Vector("R16_UINT"),
      "ImageFormat.R16ui",
      1,
      Integer(componentBytes = 2, signed = false, packed = false)
    ),
    Row(
      Vector("R16_SINT"),
      "ImageFormat.R16i",
      1,
      Integer(componentBytes = 2, signed = true, packed = false)
    ),
    Row(
      Vector("R16G16_UINT"),
      "ImageFormat.Rg16ui",
      2,
      Integer(componentBytes = 2, signed = false, packed = false)
    ),
    Row(
      Vector("R16G16_SINT"),
      "ImageFormat.Rg16i",
      2,
      Integer(componentBytes = 2, signed = true, packed = false)
    ),
    Row(
      Vector("R16G16B16A16_UINT"),
      "ImageFormat.Rgba16ui",
      4,
      Integer(componentBytes = 2, signed = false, packed = false)
    ),
    Row(
      Vector("R16G16B16A16_SINT"),
      "ImageFormat.Rgba16i",
      4,
      Integer(componentBytes = 2, signed = true, packed = false)
    ),
    Row(
      Vector("R32_UINT"),
      "ImageFormat.R32ui",
      1,
      Integer(componentBytes = 4, signed = false, packed = false)
    ),
    Row(
      Vector("R32_SINT"),
      "ImageFormat.R32i",
      1,
      Integer(componentBytes = 4, signed = true, packed = false)
    ),
    Row(
      Vector("R32G32_UINT"),
      "ImageFormat.Rg32ui",
      2,
      Integer(componentBytes = 4, signed = false, packed = false)
    ),
    Row(
      Vector("R32G32_SINT"),
      "ImageFormat.Rg32i",
      2,
      Integer(componentBytes = 4, signed = true, packed = false)
    ),
    Row(
      Vector("R32G32B32A32_UINT"),
      "ImageFormat.Rgba32ui",
      4,
      Integer(componentBytes = 4, signed = false, packed = false)
    ),
    Row(
      Vector("R32G32B32A32_SINT"),
      "ImageFormat.Rgba32i",
      4,
      Integer(componentBytes = 4, signed = true, packed = false)
    ),
    Row(
      Vector("R8_UNORM"),
      "ImageFormat.R8",
      1,
      Normalised(bits = 8, signed = false, srgb = false, bgra = false)
    ),
    Row(
      Vector("R8_SNORM"),
      "ImageFormat.R8Snorm",
      1,
      Normalised(bits = 8, signed = true, srgb = false, bgra = false)
    ),
    Row(
      Vector("R8G8_UNORM"),
      "ImageFormat.Rg8",
      2,
      Normalised(bits = 8, signed = false, srgb = false, bgra = false)
    ),
    Row(
      Vector("R8G8_SNORM"),
      "ImageFormat.Rg8Snorm",
      2,
      Normalised(bits = 8, signed = true, srgb = false, bgra = false)
    ),
    Row(
      Vector("R8G8B8A8_UNORM", "A8B8G8R8_UNORM_PACK32"),
      "ImageFormat.Rgba8",
      4,
      Normalised(bits = 8, signed = false, srgb = false, bgra = false)
    ),
    Row(
      Vector("R8G8B8A8_SNORM", "A8B8G8R8_SNORM_PACK32"),
      "ImageFormat.Rgba8Snorm",
      4,
      Normalised(bits = 8, signed = true, srgb = false, bgra = false)
    ),
    Row(
      Vector("R8G8B8A8_SRGB", "A8B8G8R8_SRGB_PACK32"),
      "ImageDescriptor.Format.Rgba8Srgb",
      4,
      Normalised(bits = 8, signed = false, srgb = true, bgra = false)
    ),
    Row(
      Vector("B8G8R8A8_UNORM"),
      "ImageDescriptor.Format.Bgra8Unorm",
      4,
      Normalised(bits = 8, signed = false, srgb = false, bgra = true)
    ),
    Row(
      Vector("B8G8R8A8_SRGB"),
      "ImageDescriptor.Format.Bgra8Srgb",
      4,
      Normalised(bits = 8, signed = false, srgb = true, bgra = true)
    ),
    Row(
      Vector("R16_UNORM"),
      "ImageFormat.R16",
      1,
      Normalised(bits = 16, signed = false, srgb = false, bgra = false)
    ),
    Row(
      Vector("R16_SNORM"),
      "ImageFormat.R16Snorm",
      1,
      Normalised(bits = 16, signed = true, srgb = false, bgra = false)
    ),
    Row(
      Vector("R16G16_UNORM"),
      "ImageFormat.Rg16",
      2,
      Normalised(bits = 16, signed = false, srgb = false, bgra = false)
    ),
    Row(
      Vector("R16G16_SNORM"),
      "ImageFormat.Rg16Snorm",
      2,
      Normalised(bits = 16, signed = true, srgb = false, bgra = false)
    ),
    Row(
      Vector("R16G16B16A16_UNORM"),
      "ImageFormat.Rgba16",
      4,
      Normalised(bits = 16, signed = false, srgb = false, bgra = false)
    ),
    Row(
      Vector("R16G16B16A16_SNORM"),
      "ImageFormat.Rgba16Snorm",
      4,
      Normalised(bits = 16, signed = true, srgb = false, bgra = false)
    ),
    Row(
      Vector("B4G4R4A4_UNORM_PACK16"),
      "ImageDescriptor.Format.B4G4R4A4Unorm",
      4,
      Narrow(
        bytes = 2,
        encoding = Encoding.Unorm,
        widths = Vector(4, 4, 4, 4),
        shifts = Vector(4, 8, 12, 0)
      )
    ),
    Row(
      Vector("R5G6B5_UNORM_PACK16"),
      "ImageDescriptor.Format.R5G6B5Unorm",
      3,
      Narrow(bytes = 2, encoding = Encoding.Unorm, widths = Vector(5, 6, 5), shifts = Vector(11, 5, 0))
    ),
    Row(
      Vector("A1R5G5B5_UNORM_PACK16"),
      "ImageDescriptor.Format.A1R5G5B5Unorm",
      4,
      Narrow(
        bytes = 2,
        encoding = Encoding.Unorm,
        widths = Vector(5, 5, 5, 1),
        shifts = Vector(10, 5, 0, 15)
      )
    ),
    Row(
      Vector("A2B10G10R10_UNORM_PACK32"),
      "ImageFormat.Rgb10A2",
      4,
      Narrow(
        bytes = 4,
        encoding = Encoding.Unorm,
        widths = Vector(10, 10, 10, 2),
        shifts = Vector(0, 10, 20, 30)
      )
    ),
    Row(
      Vector("R16_SFLOAT"),
      "ImageFormat.R16f",
      1,
      Narrow(bytes = 2, encoding = Encoding.Half, widths = Vector.empty, shifts = Vector.empty)
    ),
    Row(
      Vector("R16G16_SFLOAT"),
      "ImageFormat.Rg16f",
      2,
      Narrow(bytes = 4, encoding = Encoding.Half, widths = Vector.empty, shifts = Vector.empty)
    ),
    Row(
      Vector("R16G16B16A16_SFLOAT"),
      "ImageFormat.Rgba16f",
      4,
      Narrow(bytes = 8, encoding = Encoding.Half, widths = Vector.empty, shifts = Vector.empty)
    ),
    Row(
      Vector("B10G11R11_UFLOAT_PACK32"),
      "ImageFormat.R11fG11fB10f",
      3,
      Narrow(
        bytes = 4,
        encoding = Encoding.Ufloat,
        widths = Vector(11, 11, 10),
        shifts = Vector(0, 11, 22)
      )
    ),
    Row(
      Vector("E5B9G9R9_UFLOAT_PACK32"),
      "ImageDescriptor.Format.E5B9G9R9Ufloat",
      3,
      Narrow(
        bytes = 4,
        encoding = Encoding.SharedExponent,
        widths = Vector.empty,
        shifts = Vector.empty
      )
    )
  )

  require(rows.flatMap(_.vulkan).distinct.size == rows.flatMap(_.vulkan).size, "duplicate Vulkan format")
  require(rows.map(_.shader).distinct.size == rows.size, "duplicate shader format")
  rows.foreach { row =>
    row.layout match {
      case Narrow(bytes, Encoding.Unorm | Encoding.Ufloat, widths, shifts) =>
        require(
          widths.size == row.components.raw && shifts.size == row.components.raw,
          "packed format needs a width and shift per component"
        )
        require(
          widths.zip(shifts).forall((width, shift) => width + shift <= bytes * 8),
          "packed component exceeds its texel"
        )
      case Normalised(bits, _, srgb, bgra) =>
        require(bits == 8 || (!srgb && !bgra), "normalised channels are 8-bit, or 16-bit linear RGBA")
      case _ => ()
    }
  }

  def narrowEncodingSource: String =
    GeneratedHeader.scala("from ImageFormats") +
      s"package net.kurobako.gemstone.isa.spirv\n\n${GeneratedHeader.annotation()}private enum NarrowEncoding derives CanEqual { case ${Encoding.values.mkString(", ")} }\n"

  def scalaSource: String = {
    def layout(row: Row): (String, String) = row.layout match {
      case Floating                             => ("Floating", row.components.toString)
      case Integer(bytes, signed, packed)       => ("Integer", s"${row.components}, $bytes, $signed, $packed")
      case Normalised(bits, signed, srgb, bgra) =>
        ("Normalised", s"${row.components}, $bits, $signed, $srgb, $bgra")
      case Narrow(bytes, encoding, widths, shifts) =>
        (
          "Narrow",
          s"${row.components}, $bytes, NarrowEncoding.$encoding, ${Emission.collection("Vector", widths.map(_.toString))}, " +
            Emission.collection("Vector", shifts.map(_.toString))
        )
    }
    val groups = LayoutFamilies.map { family =>
      val selected = rows.filter(row => layout(row)._1 == family)
      val values   = selected.zipWithIndex.map { (row, index) =>
        s"  private val ${family.toLowerCaseRoot}$index = Some(${family}ImageFormat.Layout(${layout(row)._2}))"
      }.mkString("\n")
      val cases = selected.zipWithIndex.map { (row, index) =>
        s"    case ${row.shader} => ${family.toLowerCaseRoot}$index"
      }.mkString("\n")
      s"$values\n  def ${family.toLowerCaseRoot}(format: Int): Option[${family}ImageFormat.Layout] = format match {\n$cases\n    case _ => None\n  }"
    }.mkString("\n\n")
    val floatCases = rows.collect { case Row(_, shader, components, Floating) =>
      s"    case $shader => $components"
    }.mkString("\n")
    val floats  = s"  def floatComponents(format: Int): Int = format match {\n$floatCases\n    case _ => 0\n  }"
    val storage = rows.collect {
      case Row(_, shader, _, Floating | Normalised(_, _, _, _) | Narrow(_, _, _, _))
          if shader.startsWith("ImageFormat.") =>
        shader
    }
    val floatingStorage =
      s"  def floatingStorage(format: Int): Boolean = format match {\n    case ${storage.mkString(" | ")} => true\n    case _ => false\n  }"
    val shaderCases =
      shaders.map(shader => s"    case GraphicsShaderFormat.${shaderName(shader)} => $shader").mkString("\n")
    val shaderCodes =
      s"  extension (format: GraphicsShaderFormat) { def code: Int = format match {\n$shaderCases\n  } }"
    GeneratedHeader.scala("from ImageFormats") +
      s"package net.kurobako.gemstone.isa.spirv\n\nimport net.kurobako.gemstone.graphics.GraphicsFormats.*\n\n${GeneratedHeader.annotation()}private object ImageFormats {\n$groups\n$floats\n$floatingStorage\n$shaderCodes\n}\n"
  }

  private case class Vertex(
      vulkan: String,
      encoding: String,
      widths: Vector[Int],
      components: Int,
      bytes: Int,
      bgra: Boolean = false
  )
  private val vertices = {
    def uniform(prefix: String, bits: Int, components: Int, bgra: Boolean, encodings: Vector[(String, String)]) =
      encodings.map { (suffix, encoding) =>
        Vertex(s"${prefix}_$suffix", encoding, Vector.fill(4)(bits), components, bits / 8 * components, bgra)
      }
    val normalised = Vector(
      "UNORM"   -> "Unorm",
      "SNORM"   -> "Snorm",
      "UINT"    -> "Uint",
      "SINT"    -> "Sint",
      "USCALED" -> "Uscaled",
      "SSCALED" -> "Sscaled"
    )
    val words    = Vector("SFLOAT" -> "Float", "UINT" -> "Uint", "SINT" -> "Sint")
    val channels = Vector("R", "G", "B", "A")
    val plain    = for {
      bits       <- Vector(8, 16, 32)
      components <- 1 to 4
      prefix = channels.take(components).map(_ + bits).mkString
      row <- uniform(
        prefix,
        bits,
        components,
        bgra = false,
        if (bits == 32) words else normalised ++ (if (bits == 16) Vector("SFLOAT" -> "Float") else Vector.empty)
      )
    } yield row
    val bgra = Vector("B8G8R8" -> 3, "B8G8R8A8" -> 4).flatMap { (prefix, components) =>
      uniform(prefix, 8, components, bgra = true, normalised)
    }
    val packed = for {
      (prefix, bgra)     <- Vector("A2B10G10R10" -> false, "A2R10G10B10" -> true)
      (suffix, encoding) <- normalised
    } yield Vertex(s"${prefix}_${suffix}_PACK32", encoding, Vector(10, 10, 10, 2), components = 4, bytes = 4, bgra)
    plain ++ bgra ++ packed ++
      uniform(
        "A8B8G8R8",
        bits = 8,
        components = 4,
        bgra = false,
        normalised.map((suffix, encoding) => s"${suffix}_PACK32" -> encoding)
      ) :+
      Vertex("B10G11R11_UFLOAT_PACK32", "Ufloat", Vector(11, 11, 10, 0), components = 3, bytes = 4)
  }
  private case class Compressed(
      vulkan: String,
      bytes: Int,
      components: Int,
      compatibility: Int,
      encoding: String,
      srgb: Boolean = false
  )
  private val compressed = Vector(
    Compressed("ETC2_R8G8B8_UNORM_BLOCK", bytes = 8, components = 3, compatibility = 1, "EtcRgb"),
    Compressed(
      vulkan = "ETC2_R8G8B8_SRGB_BLOCK",
      bytes = 8,
      components = 3,
      compatibility = 1,
      encoding = "EtcRgb",
      true
    ),
    Compressed("ETC2_R8G8B8A1_UNORM_BLOCK", bytes = 8, components = 4, compatibility = 2, "EtcPunchthrough"),
    Compressed(
      vulkan = "ETC2_R8G8B8A1_SRGB_BLOCK",
      bytes = 8,
      components = 4,
      compatibility = 2,
      encoding = "EtcPunchthrough",
      true
    ),
    Compressed("ETC2_R8G8B8A8_UNORM_BLOCK", bytes = 16, components = 4, compatibility = 3, "EtcRgba"),
    Compressed(
      vulkan = "ETC2_R8G8B8A8_SRGB_BLOCK",
      bytes = 16,
      components = 4,
      compatibility = 3,
      encoding = "EtcRgba",
      true
    ),
    Compressed("EAC_R11_UNORM_BLOCK", bytes = 8, components = 1, compatibility = 4, "EacR"),
    Compressed("EAC_R11_SNORM_BLOCK", bytes = 8, components = 1, compatibility = 4, "EacSignedR"),
    Compressed("EAC_R11G11_UNORM_BLOCK", bytes = 16, components = 2, compatibility = 5, "EacRg"),
    Compressed("EAC_R11G11_SNORM_BLOCK", bytes = 16, components = 2, compatibility = 5, "EacSignedRg")
  )
  require(vertices.map(_.vulkan).distinct.size == vertices.size, "duplicate vertex format")
  require(compressed.map(_.vulkan).distinct.size == compressed.size, "duplicate compressed format")

  // Storage/view formats and shader formats have different identities. In particular,
  // compressed blocks name their decoded format and combined depth/stencil views retain an eight-byte stride.
  private case class Storage(
      name: String,
      vulkan: Vector[String],
      shader: String,
      bytes: Int,
      depth: String = "None",
      stencil: Int = -1,
      compression: String = "None",
      srgb: Boolean = false,
      decoded: Option[String] = None
  )
  private def shaderName(symbol: String): String = symbol.split('.').last
  private def formatName(symbol: String): String = symbol.split('_').map { part =>
    part.head.toString + part.drop(1).toLowerCaseRoot
  }.mkString
  private val storage = rows.map { row =>
    val bytes = row.layout match {
      case Floating                  => row.components * 4
      case Integer(bytes, _, packed) => if (packed) 4 else row.components * bytes
      case Normalised(bits = bits)   => row.components * bits / 8
      case Narrow(bytes = bytes)     => bytes.raw
    }
    val srgb = row.layout match {
      case Normalised(srgb = srgb) => srgb
      case _                       => false
    }
    Storage(name = shaderName(row.shader), vulkan = row.vulkan, shader = row.shader, bytes = bytes, srgb = srgb)
  } ++ Vector(
    Storage(
      name = "D16Unorm",
      vulkan = Vector("D16_UNORM"),
      shader = "ImageDescriptor.Format.D16Unorm",
      bytes = 2,
      depth = "Unorm16"
    ),
    Storage(
      name = "D32Sfloat",
      vulkan = Vector("D32_SFLOAT"),
      shader = "ImageFormat.R32f",
      bytes = 4,
      depth = "Float32"
    ),
    Storage(
      name = "S8Uint",
      vulkan = Vector("S8_UINT"),
      shader = "ImageDescriptor.Format.S8Uint",
      bytes = 1,
      stencil = 0
    ),
    Storage(
      name = "D32SfloatS8Depth",
      vulkan = Vector("D32_SFLOAT_S8_UINT"),
      shader = "ImageDescriptor.Format.D32SfloatS8Depth",
      bytes = 8,
      depth = "Float32",
      stencil = 4
    ),
    Storage(
      name = "D32SfloatS8Stencil",
      vulkan = Vector.empty,
      shader = "ImageDescriptor.Format.D32SfloatS8Stencil",
      bytes = 8,
      depth = "Float32",
      stencil = 4
    )
  ) ++ compressed.map { row =>
    val shader = row.components match {
      case 1 => "ImageFormat.R32f"
      case 2 => "ImageFormat.Rg32f"
      case 3 => "ImageDescriptor.Format.DecodedRgb32f"
      case 4 => "ImageFormat.Rgba32f"
    }
    Storage(
      name = formatName(row.vulkan),
      vulkan = Vector(row.vulkan),
      shader = shader,
      bytes = row.bytes,
      compression = row.encoding,
      srgb = row.srgb,
      decoded = Some(shaderName(shader))
    )
  }
  require(storage.map(_.name).distinct.size == storage.size, "duplicate storage format")
  private val shaders = storage.map(_.shader).distinct

  def coreSource: String = {
    val cases = storage.map { row =>
      s"  case ${row.name} extends GraphicsFormat(GraphicsShaderFormat.${shaderName(row.shader)}, ${row.bytes}, GraphicsDepthFormat.${row.depth}, ${row.stencil}, GraphicsCompression.${row.compression}, ${row.srgb})"
    }.mkString("\n")
    val decoded = storage.flatMap(row => row.decoded.map(name => s"    case ${row.name} => $name")).mkString("\n")
    GeneratedHeader.scala("from ImageFormats") + s"""package net.kurobako.gemstone.graphics

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.rpc.Wire.*

${GeneratedHeader.annotation()}object GraphicsFormats {
  enum GraphicsDepthFormat derives CanEqual { case None, Unorm16, Float32 }
  enum GraphicsCompression derives CanEqual {
    case None, EtcRgb, EtcPunchthrough, EtcRgba, EacR, EacRg, EacSignedR, EacSignedRg
  }
  /** Shader texel interpretations. Numeric descriptor codes are generated in the SPIR-V module. */
  enum GraphicsShaderFormat derives CanEqual { case ${shaders.map(shaderName(_)).mkString(", ")} }

  /** Supported storage/view layouts. Metadata cannot be supplied independently of the format. */
  enum GraphicsFormat(
      val shaderFormat: GraphicsShaderFormat,
      val texelBytes: Int > 0,
      val depth: GraphicsDepthFormat,
      val stencilOffset: Int,
      val compression: GraphicsCompression,
      val srgb: Boolean
  ) derives Codec, CanEqual {
$cases
    def decoded: GraphicsFormat = this match {
$decoded
      case _ => this
    }
  }
}
"""
  }
  def main(arguments: Array[String]): Unit = CodegenCommand.run(
    Command("ImageFormats", "Generate shared graphics format domains")(
      CodegenCommand.path("core-source-root")
    ),
    arguments
  ) { root =>
    val path = root.resolve("net/kurobako/gemstone/graphics/GraphicsFormats.scala")
    val _    = GeneratedFiles.write(path, GeneratedScalaFormatter(path, coreSource))
  }

  def nativeSource: String = {
    def capability(name: String)(supported: Row => Boolean): String = {
      val labels = rows.filter(supported).flatMap(_.vulkan).map(name => s"    case VK_FORMAT_$name:").mkString("\n")
      s"inline bool $name(VkFormat format) {\n  switch (format) {\n$labels return true;\n    default: return false;\n  }\n}"
    }
    val capabilities = Vector(
      capability("colourFormat")(_ => true),
      capability("integerColourFormat")(_.layout match {
        case Integer(_, _, _) => true
        case _                => false
      }),
      capability("colourAttachmentFormat")(_.layout match {
        case Narrow(encoding = Encoding.SharedExponent) => false
        case _                                          => true
      }),
      capability("blendColourFormat")(_.layout match {
        case Floating => true
        // SNORM blending stays unadvertised; the CTS reference uses unclamped one-minus factors.
        case Normalised(signed = signed) => !signed
        case Narrow(encoding = encoding) => encoding != Encoding.SharedExponent
        case _                           => false
      }),
      capability("storageColourFormat")(row => row.shader.startsWith("ImageFormat."))
    ).mkString("\n")
    val vertexCases = vertices.map { row =>
      s"    case VK_FORMAT_${row.vulkan}: return VertexFormat{VertexEncoding::${row.encoding}, {${row.widths.mkString(", ")}}, ${row.components}, ${row.bytes}, ${row.bgra}};"
    }.mkString("\n")
    val compressedCases = compressed.map { row =>
      s"    case VK_FORMAT_${row.vulkan}: return CompressedImageFormat{${row.compatibility}};"
    }.mkString("\n")
    val storageCases = storage.filter(_.vulkan.nonEmpty).map { row =>
      val labels = row.vulkan.map(name => s"    case VK_FORMAT_$name:").mkString("\n")
      val value  = if (row.name == "D32SfloatS8Depth")
        "aspects == VK_IMAGE_ASPECT_STENCIL_BIT ? execution::GraphicsFormat::D32SfloatS8Stencil : execution::GraphicsFormat::D32SfloatS8Depth"
      else s"execution::GraphicsFormat::${row.name}"
      s"$labels return $value;"
    }.mkString("\n")
    val storageRows = storage.filter(_.vulkan.nonEmpty)
    def storageProperty(name: String, resultType: String, fallback: String)(value: Storage => String): String = {
      val cases =
        storageRows.filter(row => value(row) != fallback).groupBy(value).toVector.sortBy(_._1).map { (result, rows) =>
          val labels = rows.view.flatMap(_.vulkan).map(name => s"    case VK_FORMAT_$name:").mkString("\n")
          s"$labels return $result;"
        }.mkString("\n")
      s"inline $resultType $name(VkFormat format) {\n  switch (format) {\n$cases\n    default: return $fallback;\n  }\n}"
    }
    val properties = Vector(
      storageProperty("texelSize", "uint32_t", "0")(_.bytes.toString),
      storageProperty("depthFormat", "bool", "false")(row => (row.depth != "None").toString),
      storageProperty("stencilFormat", "bool", "false")(row => (row.stencil >= 0).toString)
    ).mkString("\n")
    GeneratedHeader.cpp("from ImageFormats") +
      s"""#pragma once
#include <array>
#include <cstdint>
#include <optional>
#include <vulkan/vulkan.h>
#include "gemstone_execution_rpc.hpp"
namespace gemstone::vulkan {
inline std::optional<execution::GraphicsFormat> graphicsFormat(VkFormat format, VkImageAspectFlags aspects = 0) {
  switch (format) {
$storageCases
    default: return std::nullopt;
  }
}
$properties
using VertexEncoding = execution::GraphicsVertexEncoding;
struct VertexFormat {
  VertexEncoding encoding;
  std::array<uint8_t, 4> widths;
  uint8_t components, bytes;
  bool bgra;
};
inline std::optional<VertexFormat> vertexFormat(VkFormat format) {
  switch (format) {
$vertexCases
    default: return std::nullopt;
  }
}
struct CompressedImageFormat {
  uint32_t compatibility;
  static uint32_t blocks(uint32_t size) { return size / 4 + (size % 4 != 0); }
};
inline std::optional<CompressedImageFormat> compressedImageFormat(VkFormat format) {
  switch (format) {
$compressedCases
    default: return std::nullopt;
  }
}
$capabilities
} // namespace gemstone::vulkan
"""
  }
}
