// Generated from ImageFormats. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.spirv

import javax.annotation.processing.Generated

import net.kurobako.gemstone.graphics.GraphicsFormats.*

@Generated(Array("sbt codegen"))
private object ImageFormats {
  private val integer0 = Some(IntegerImageFormat.Layout(4, 4, false, true))
  private val integer1 = Some(IntegerImageFormat.Layout(1, 1, false, false))
  private val integer2 = Some(IntegerImageFormat.Layout(1, 1, true, false))
  private val integer3 = Some(IntegerImageFormat.Layout(2, 1, false, false))
  private val integer4 = Some(IntegerImageFormat.Layout(2, 1, true, false))
  private val integer5 = Some(IntegerImageFormat.Layout(4, 1, false, false))
  private val integer6 = Some(IntegerImageFormat.Layout(4, 1, true, false))
  private val integer7 = Some(IntegerImageFormat.Layout(1, 2, false, false))
  private val integer8 = Some(IntegerImageFormat.Layout(1, 2, true, false))
  private val integer9 = Some(IntegerImageFormat.Layout(2, 2, false, false))
  private val integer10 = Some(IntegerImageFormat.Layout(2, 2, true, false))
  private val integer11 = Some(IntegerImageFormat.Layout(4, 2, false, false))
  private val integer12 = Some(IntegerImageFormat.Layout(4, 2, true, false))
  private val integer13 = Some(IntegerImageFormat.Layout(1, 4, false, false))
  private val integer14 = Some(IntegerImageFormat.Layout(1, 4, true, false))
  private val integer15 = Some(IntegerImageFormat.Layout(2, 4, false, false))
  private val integer16 = Some(IntegerImageFormat.Layout(2, 4, true, false))
  private val integer17 = Some(IntegerImageFormat.Layout(4, 4, false, false))
  private val integer18 = Some(IntegerImageFormat.Layout(4, 4, true, false))
  def integer(format: Int): Option[IntegerImageFormat.Layout] = format match {
    case ImageFormat.Rgb10a2ui => integer0
    case ImageFormat.R8ui => integer1
    case ImageFormat.R8i => integer2
    case ImageFormat.Rg8ui => integer3
    case ImageFormat.Rg8i => integer4
    case ImageFormat.Rgba8ui => integer5
    case ImageFormat.Rgba8i => integer6
    case ImageFormat.R16ui => integer7
    case ImageFormat.R16i => integer8
    case ImageFormat.Rg16ui => integer9
    case ImageFormat.Rg16i => integer10
    case ImageFormat.Rgba16ui => integer11
    case ImageFormat.Rgba16i => integer12
    case ImageFormat.R32ui => integer13
    case ImageFormat.R32i => integer14
    case ImageFormat.Rg32ui => integer15
    case ImageFormat.Rg32i => integer16
    case ImageFormat.Rgba32ui => integer17
    case ImageFormat.Rgba32i => integer18
    case _ => None
  }

  private val normalised0 = Some(NormalisedImageFormat.Layout(1, 8, false, false, false))
  private val normalised1 = Some(NormalisedImageFormat.Layout(1, 8, true, false, false))
  private val normalised2 = Some(NormalisedImageFormat.Layout(2, 8, false, false, false))
  private val normalised3 = Some(NormalisedImageFormat.Layout(2, 8, true, false, false))
  private val normalised4 = Some(NormalisedImageFormat.Layout(4, 8, false, false, false))
  private val normalised5 = Some(NormalisedImageFormat.Layout(4, 8, true, false, false))
  private val normalised6 = Some(NormalisedImageFormat.Layout(4, 8, false, true, false))
  private val normalised7 = Some(NormalisedImageFormat.Layout(4, 8, false, false, true))
  private val normalised8 = Some(NormalisedImageFormat.Layout(4, 8, false, true, true))
  private val normalised9 = Some(NormalisedImageFormat.Layout(1, 16, false, false, false))
  private val normalised10 = Some(NormalisedImageFormat.Layout(1, 16, true, false, false))
  private val normalised11 = Some(NormalisedImageFormat.Layout(2, 16, false, false, false))
  private val normalised12 = Some(NormalisedImageFormat.Layout(2, 16, true, false, false))
  private val normalised13 = Some(NormalisedImageFormat.Layout(4, 16, false, false, false))
  private val normalised14 = Some(NormalisedImageFormat.Layout(4, 16, true, false, false))
  def normalised(format: Int): Option[NormalisedImageFormat.Layout] = format match {
    case ImageFormat.R8 => normalised0
    case ImageFormat.R8Snorm => normalised1
    case ImageFormat.Rg8 => normalised2
    case ImageFormat.Rg8Snorm => normalised3
    case ImageFormat.Rgba8 => normalised4
    case ImageFormat.Rgba8Snorm => normalised5
    case ImageDescriptor.Format.Rgba8Srgb => normalised6
    case ImageDescriptor.Format.Bgra8Unorm => normalised7
    case ImageDescriptor.Format.Bgra8Srgb => normalised8
    case ImageFormat.R16 => normalised9
    case ImageFormat.R16Snorm => normalised10
    case ImageFormat.Rg16 => normalised11
    case ImageFormat.Rg16Snorm => normalised12
    case ImageFormat.Rgba16 => normalised13
    case ImageFormat.Rgba16Snorm => normalised14
    case _ => None
  }

  private val narrow0 =
    Some(NarrowImageFormat.Layout(4, 2, NarrowEncoding.Unorm, Vector(4, 4, 4, 4), Vector(4, 8, 12, 0)))
  private val narrow1 = Some(NarrowImageFormat.Layout(3, 2, NarrowEncoding.Unorm, Vector(5, 6, 5), Vector(11, 5, 0)))
  private val narrow2 =
    Some(NarrowImageFormat.Layout(4, 2, NarrowEncoding.Unorm, Vector(5, 5, 5, 1), Vector(10, 5, 0, 15)))
  private val narrow3 =
    Some(NarrowImageFormat.Layout(4, 4, NarrowEncoding.Unorm, Vector(10, 10, 10, 2), Vector(0, 10, 20, 30)))
  private val narrow4 = Some(NarrowImageFormat.Layout(1, 2, NarrowEncoding.Half, Vector.empty, Vector.empty))
  private val narrow5 = Some(NarrowImageFormat.Layout(2, 4, NarrowEncoding.Half, Vector.empty, Vector.empty))
  private val narrow6 = Some(NarrowImageFormat.Layout(4, 8, NarrowEncoding.Half, Vector.empty, Vector.empty))
  private val narrow7 =
    Some(NarrowImageFormat.Layout(3, 4, NarrowEncoding.Ufloat, Vector(11, 11, 10), Vector(0, 11, 22)))
  private val narrow8 = Some(NarrowImageFormat.Layout(3, 4, NarrowEncoding.SharedExponent, Vector.empty, Vector.empty))
  def narrow(format: Int): Option[NarrowImageFormat.Layout] = format match {
    case ImageDescriptor.Format.B4G4R4A4Unorm => narrow0
    case ImageDescriptor.Format.R5G6B5Unorm => narrow1
    case ImageDescriptor.Format.A1R5G5B5Unorm => narrow2
    case ImageFormat.Rgb10A2 => narrow3
    case ImageFormat.R16f => narrow4
    case ImageFormat.Rg16f => narrow5
    case ImageFormat.Rgba16f => narrow6
    case ImageFormat.R11fG11fB10f => narrow7
    case ImageDescriptor.Format.E5B9G9R9Ufloat => narrow8
    case _ => None
  }
  def floatComponents(format: Int): Int = format match {
    case ImageFormat.R32f => 1
    case ImageFormat.Rg32f => 2
    case ImageFormat.Rgba32f => 4
    case ImageDescriptor.Format.DecodedRgb32f => 3
    case _ => 0
  }
  def floatingStorage(format: Int): Boolean = format match {
    case ImageFormat.R32f | ImageFormat.Rg32f | ImageFormat.Rgba32f | ImageFormat.R8 | ImageFormat.R8Snorm | ImageFormat.Rg8 | ImageFormat.Rg8Snorm | ImageFormat.Rgba8 | ImageFormat.Rgba8Snorm | ImageFormat.R16 | ImageFormat.R16Snorm | ImageFormat.Rg16 | ImageFormat.Rg16Snorm | ImageFormat.Rgba16 | ImageFormat.Rgba16Snorm | ImageFormat.Rgb10A2 | ImageFormat.R16f | ImageFormat.Rg16f | ImageFormat.Rgba16f | ImageFormat.R11fG11fB10f =>
      true
    case _ => false
  }
  extension (format: GraphicsShaderFormat) {
    def code: Int = format match {
      case GraphicsShaderFormat.R32f => ImageFormat.R32f
      case GraphicsShaderFormat.Rg32f => ImageFormat.Rg32f
      case GraphicsShaderFormat.Rgba32f => ImageFormat.Rgba32f
      case GraphicsShaderFormat.DecodedRgb32f => ImageDescriptor.Format.DecodedRgb32f
      case GraphicsShaderFormat.Rgb10a2ui => ImageFormat.Rgb10a2ui
      case GraphicsShaderFormat.R8ui => ImageFormat.R8ui
      case GraphicsShaderFormat.R8i => ImageFormat.R8i
      case GraphicsShaderFormat.Rg8ui => ImageFormat.Rg8ui
      case GraphicsShaderFormat.Rg8i => ImageFormat.Rg8i
      case GraphicsShaderFormat.Rgba8ui => ImageFormat.Rgba8ui
      case GraphicsShaderFormat.Rgba8i => ImageFormat.Rgba8i
      case GraphicsShaderFormat.R16ui => ImageFormat.R16ui
      case GraphicsShaderFormat.R16i => ImageFormat.R16i
      case GraphicsShaderFormat.Rg16ui => ImageFormat.Rg16ui
      case GraphicsShaderFormat.Rg16i => ImageFormat.Rg16i
      case GraphicsShaderFormat.Rgba16ui => ImageFormat.Rgba16ui
      case GraphicsShaderFormat.Rgba16i => ImageFormat.Rgba16i
      case GraphicsShaderFormat.R32ui => ImageFormat.R32ui
      case GraphicsShaderFormat.R32i => ImageFormat.R32i
      case GraphicsShaderFormat.Rg32ui => ImageFormat.Rg32ui
      case GraphicsShaderFormat.Rg32i => ImageFormat.Rg32i
      case GraphicsShaderFormat.Rgba32ui => ImageFormat.Rgba32ui
      case GraphicsShaderFormat.Rgba32i => ImageFormat.Rgba32i
      case GraphicsShaderFormat.R8 => ImageFormat.R8
      case GraphicsShaderFormat.R8Snorm => ImageFormat.R8Snorm
      case GraphicsShaderFormat.Rg8 => ImageFormat.Rg8
      case GraphicsShaderFormat.Rg8Snorm => ImageFormat.Rg8Snorm
      case GraphicsShaderFormat.Rgba8 => ImageFormat.Rgba8
      case GraphicsShaderFormat.Rgba8Snorm => ImageFormat.Rgba8Snorm
      case GraphicsShaderFormat.Rgba8Srgb => ImageDescriptor.Format.Rgba8Srgb
      case GraphicsShaderFormat.Bgra8Unorm => ImageDescriptor.Format.Bgra8Unorm
      case GraphicsShaderFormat.Bgra8Srgb => ImageDescriptor.Format.Bgra8Srgb
      case GraphicsShaderFormat.R16 => ImageFormat.R16
      case GraphicsShaderFormat.R16Snorm => ImageFormat.R16Snorm
      case GraphicsShaderFormat.Rg16 => ImageFormat.Rg16
      case GraphicsShaderFormat.Rg16Snorm => ImageFormat.Rg16Snorm
      case GraphicsShaderFormat.Rgba16 => ImageFormat.Rgba16
      case GraphicsShaderFormat.Rgba16Snorm => ImageFormat.Rgba16Snorm
      case GraphicsShaderFormat.B4G4R4A4Unorm => ImageDescriptor.Format.B4G4R4A4Unorm
      case GraphicsShaderFormat.R5G6B5Unorm => ImageDescriptor.Format.R5G6B5Unorm
      case GraphicsShaderFormat.A1R5G5B5Unorm => ImageDescriptor.Format.A1R5G5B5Unorm
      case GraphicsShaderFormat.Rgb10A2 => ImageFormat.Rgb10A2
      case GraphicsShaderFormat.R16f => ImageFormat.R16f
      case GraphicsShaderFormat.Rg16f => ImageFormat.Rg16f
      case GraphicsShaderFormat.Rgba16f => ImageFormat.Rgba16f
      case GraphicsShaderFormat.R11fG11fB10f => ImageFormat.R11fG11fB10f
      case GraphicsShaderFormat.E5B9G9R9Ufloat => ImageDescriptor.Format.E5B9G9R9Ufloat
      case GraphicsShaderFormat.D16Unorm => ImageDescriptor.Format.D16Unorm
      case GraphicsShaderFormat.S8Uint => ImageDescriptor.Format.S8Uint
      case GraphicsShaderFormat.D32SfloatS8Depth => ImageDescriptor.Format.D32SfloatS8Depth
      case GraphicsShaderFormat.D32SfloatS8Stencil => ImageDescriptor.Format.D32SfloatS8Stencil
    }
  }
}
