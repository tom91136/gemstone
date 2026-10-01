// Generated from ImageFormats. Regenerate with: sbt codegen
package net.kurobako.gemstone.graphics

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.rpc.Wire.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
object GraphicsFormats {
  enum GraphicsDepthFormat { case None, Unorm16, Float32 }
  enum GraphicsCompression {
    case None, EtcRgb, EtcPunchthrough, EtcRgba, EacR, EacRg, EacSignedR, EacSignedRg
  }

  /** Shader texel interpretations. Numeric descriptor codes are generated in the SPIR-V module. */
  enum GraphicsShaderFormat {
    case R32f, Rg32f, Rgba32f, DecodedRgb32f, Rgb10a2ui, R8ui, R8i, Rg8ui, Rg8i, Rgba8ui, Rgba8i, R16ui, R16i, Rg16ui,
      Rg16i, Rgba16ui, Rgba16i, R32ui, R32i, Rg32ui, Rg32i, Rgba32ui, Rgba32i, R8, R8Snorm, Rg8, Rg8Snorm, Rgba8,
      Rgba8Snorm, Rgba8Srgb, Bgra8Unorm, Bgra8Srgb, B4G4R4A4Unorm, R5G6B5Unorm, A1R5G5B5Unorm, Rgb10A2, R16f, Rg16f,
      Rgba16f, R11fG11fB10f, E5B9G9R9Ufloat, D16Unorm, S8Uint, D32SfloatS8Depth, D32SfloatS8Stencil
  }

  /** Supported storage/view layouts. Metadata cannot be supplied independently of the format. */
  enum GraphicsFormat(
      val shaderFormat: GraphicsShaderFormat,
      val texelBytes: Int > 0,
      val depth: GraphicsDepthFormat,
      val stencilOffset: Int,
      val compression: GraphicsCompression,
      val srgb: Boolean
  ) derives Codec {
    case R32f extends GraphicsFormat(
          GraphicsShaderFormat.R32f,
          4,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.None,
          false
        )
    case Rg32f extends GraphicsFormat(
          GraphicsShaderFormat.Rg32f,
          8,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.None,
          false
        )
    case Rgba32f extends GraphicsFormat(
          GraphicsShaderFormat.Rgba32f,
          16,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.None,
          false
        )
    case DecodedRgb32f extends GraphicsFormat(
          GraphicsShaderFormat.DecodedRgb32f,
          12,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.None,
          false
        )
    case Rgb10a2ui extends GraphicsFormat(
          GraphicsShaderFormat.Rgb10a2ui,
          4,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.None,
          false
        )
    case R8ui extends GraphicsFormat(
          GraphicsShaderFormat.R8ui,
          1,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.None,
          false
        )
    case R8i extends GraphicsFormat(
          GraphicsShaderFormat.R8i,
          1,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.None,
          false
        )
    case Rg8ui extends GraphicsFormat(
          GraphicsShaderFormat.Rg8ui,
          2,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.None,
          false
        )
    case Rg8i extends GraphicsFormat(
          GraphicsShaderFormat.Rg8i,
          2,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.None,
          false
        )
    case Rgba8ui extends GraphicsFormat(
          GraphicsShaderFormat.Rgba8ui,
          4,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.None,
          false
        )
    case Rgba8i extends GraphicsFormat(
          GraphicsShaderFormat.Rgba8i,
          4,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.None,
          false
        )
    case R16ui extends GraphicsFormat(
          GraphicsShaderFormat.R16ui,
          2,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.None,
          false
        )
    case R16i extends GraphicsFormat(
          GraphicsShaderFormat.R16i,
          2,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.None,
          false
        )
    case Rg16ui extends GraphicsFormat(
          GraphicsShaderFormat.Rg16ui,
          4,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.None,
          false
        )
    case Rg16i extends GraphicsFormat(
          GraphicsShaderFormat.Rg16i,
          4,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.None,
          false
        )
    case Rgba16ui extends GraphicsFormat(
          GraphicsShaderFormat.Rgba16ui,
          8,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.None,
          false
        )
    case Rgba16i extends GraphicsFormat(
          GraphicsShaderFormat.Rgba16i,
          8,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.None,
          false
        )
    case R32ui extends GraphicsFormat(
          GraphicsShaderFormat.R32ui,
          4,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.None,
          false
        )
    case R32i extends GraphicsFormat(
          GraphicsShaderFormat.R32i,
          4,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.None,
          false
        )
    case Rg32ui extends GraphicsFormat(
          GraphicsShaderFormat.Rg32ui,
          8,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.None,
          false
        )
    case Rg32i extends GraphicsFormat(
          GraphicsShaderFormat.Rg32i,
          8,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.None,
          false
        )
    case Rgba32ui extends GraphicsFormat(
          GraphicsShaderFormat.Rgba32ui,
          16,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.None,
          false
        )
    case Rgba32i extends GraphicsFormat(
          GraphicsShaderFormat.Rgba32i,
          16,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.None,
          false
        )
    case R8 extends GraphicsFormat(
          GraphicsShaderFormat.R8,
          1,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.None,
          false
        )
    case R8Snorm extends GraphicsFormat(
          GraphicsShaderFormat.R8Snorm,
          1,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.None,
          false
        )
    case Rg8 extends GraphicsFormat(
          GraphicsShaderFormat.Rg8,
          2,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.None,
          false
        )
    case Rg8Snorm extends GraphicsFormat(
          GraphicsShaderFormat.Rg8Snorm,
          2,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.None,
          false
        )
    case Rgba8 extends GraphicsFormat(
          GraphicsShaderFormat.Rgba8,
          4,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.None,
          false
        )
    case Rgba8Snorm extends GraphicsFormat(
          GraphicsShaderFormat.Rgba8Snorm,
          4,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.None,
          false
        )
    case Rgba8Srgb extends GraphicsFormat(
          GraphicsShaderFormat.Rgba8Srgb,
          4,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.None,
          true
        )
    case Bgra8Unorm extends GraphicsFormat(
          GraphicsShaderFormat.Bgra8Unorm,
          4,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.None,
          false
        )
    case Bgra8Srgb extends GraphicsFormat(
          GraphicsShaderFormat.Bgra8Srgb,
          4,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.None,
          true
        )
    case B4G4R4A4Unorm extends GraphicsFormat(
          GraphicsShaderFormat.B4G4R4A4Unorm,
          2,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.None,
          false
        )
    case R5G6B5Unorm extends GraphicsFormat(
          GraphicsShaderFormat.R5G6B5Unorm,
          2,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.None,
          false
        )
    case A1R5G5B5Unorm extends GraphicsFormat(
          GraphicsShaderFormat.A1R5G5B5Unorm,
          2,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.None,
          false
        )
    case Rgb10A2 extends GraphicsFormat(
          GraphicsShaderFormat.Rgb10A2,
          4,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.None,
          false
        )
    case R16f extends GraphicsFormat(
          GraphicsShaderFormat.R16f,
          2,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.None,
          false
        )
    case Rg16f extends GraphicsFormat(
          GraphicsShaderFormat.Rg16f,
          4,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.None,
          false
        )
    case Rgba16f extends GraphicsFormat(
          GraphicsShaderFormat.Rgba16f,
          8,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.None,
          false
        )
    case R11fG11fB10f extends GraphicsFormat(
          GraphicsShaderFormat.R11fG11fB10f,
          4,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.None,
          false
        )
    case E5B9G9R9Ufloat extends GraphicsFormat(
          GraphicsShaderFormat.E5B9G9R9Ufloat,
          4,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.None,
          false
        )
    case D16Unorm extends GraphicsFormat(
          GraphicsShaderFormat.D16Unorm,
          2,
          GraphicsDepthFormat.Unorm16,
          -1,
          GraphicsCompression.None,
          false
        )
    case D32Sfloat extends GraphicsFormat(
          GraphicsShaderFormat.R32f,
          4,
          GraphicsDepthFormat.Float32,
          -1,
          GraphicsCompression.None,
          false
        )
    case S8Uint extends GraphicsFormat(
          GraphicsShaderFormat.S8Uint,
          1,
          GraphicsDepthFormat.None,
          0,
          GraphicsCompression.None,
          false
        )
    case D32SfloatS8Depth extends GraphicsFormat(
          GraphicsShaderFormat.D32SfloatS8Depth,
          8,
          GraphicsDepthFormat.Float32,
          4,
          GraphicsCompression.None,
          false
        )
    case D32SfloatS8Stencil extends GraphicsFormat(
          GraphicsShaderFormat.D32SfloatS8Stencil,
          8,
          GraphicsDepthFormat.Float32,
          4,
          GraphicsCompression.None,
          false
        )
    case Etc2R8g8b8UnormBlock extends GraphicsFormat(
          GraphicsShaderFormat.DecodedRgb32f,
          8,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.EtcRgb,
          false
        )
    case Etc2R8g8b8SrgbBlock extends GraphicsFormat(
          GraphicsShaderFormat.DecodedRgb32f,
          8,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.EtcRgb,
          true
        )
    case Etc2R8g8b8a1UnormBlock extends GraphicsFormat(
          GraphicsShaderFormat.Rgba32f,
          8,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.EtcPunchthrough,
          false
        )
    case Etc2R8g8b8a1SrgbBlock extends GraphicsFormat(
          GraphicsShaderFormat.Rgba32f,
          8,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.EtcPunchthrough,
          true
        )
    case Etc2R8g8b8a8UnormBlock extends GraphicsFormat(
          GraphicsShaderFormat.Rgba32f,
          16,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.EtcRgba,
          false
        )
    case Etc2R8g8b8a8SrgbBlock extends GraphicsFormat(
          GraphicsShaderFormat.Rgba32f,
          16,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.EtcRgba,
          true
        )
    case EacR11UnormBlock extends GraphicsFormat(
          GraphicsShaderFormat.R32f,
          8,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.EacR,
          false
        )
    case EacR11SnormBlock extends GraphicsFormat(
          GraphicsShaderFormat.R32f,
          8,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.EacSignedR,
          false
        )
    case EacR11g11UnormBlock extends GraphicsFormat(
          GraphicsShaderFormat.Rg32f,
          16,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.EacRg,
          false
        )
    case EacR11g11SnormBlock extends GraphicsFormat(
          GraphicsShaderFormat.Rg32f,
          16,
          GraphicsDepthFormat.None,
          -1,
          GraphicsCompression.EacSignedRg,
          false
        )
    def decoded: GraphicsFormat = this match {
      case Etc2R8g8b8UnormBlock => DecodedRgb32f
      case Etc2R8g8b8SrgbBlock => DecodedRgb32f
      case Etc2R8g8b8a1UnormBlock => Rgba32f
      case Etc2R8g8b8a1SrgbBlock => Rgba32f
      case Etc2R8g8b8a8UnormBlock => Rgba32f
      case Etc2R8g8b8a8SrgbBlock => Rgba32f
      case EacR11UnormBlock => R32f
      case EacR11SnormBlock => R32f
      case EacR11g11UnormBlock => Rg32f
      case EacR11g11SnormBlock => Rg32f
      case _ => this
    }
  }
}
