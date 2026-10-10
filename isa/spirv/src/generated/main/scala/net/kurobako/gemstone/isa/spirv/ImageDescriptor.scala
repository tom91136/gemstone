// Generated from Gemstone ImageDescriptorABI. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.spirv

@javax.annotation.processing.Generated(Array("sbt codegen"))
object ImageDescriptor {
  object Format {
    inline val D16Unorm = 1000
    inline val Bgra8Unorm = 1001
    inline val D32SfloatS8Depth = 1002
    inline val S8Uint = 1003
    inline val D32SfloatS8Stencil = 1004
    inline val Rgba8Srgb = 1005
    inline val Bgra8Srgb = 1006
    inline val B4G4R4A4Unorm = 1007
    inline val R5G6B5Unorm = 1008
    inline val A1R5G5B5Unorm = 1009
    inline val E5B9G9R9Ufloat = 1010
    inline val DecodedRgb32f = 1011
    inline val D32Sfloat = 1012
  }

  object Flags {
    inline val LevelTable = 2147483648L
    inline val Volume = 1073741824L
    inline val Swizzle = 536870912L
    inline val Pitched = 268435456L
    inline val FormatMask = 268435455L
  }

  object SamplerWord {
    inline val MinFilter = 0
    inline val MagFilter = 1
    inline val AddressU = 2
    inline val AddressV = 3
    inline val Unnormalized = 4
    inline val CompareEnable = 5
    inline val CompareOp = 6
    inline val BorderColour = 7
    inline val MipLodBias = 8
    inline val MinLod = 9
    inline val MaxLod = 10
    inline val MipmapMode = 11
    inline val AddressW = 12
    inline val Count = 13
  }

  object Layout {
    inline val Width = 0
    inline val Height = 4
    inline val Slices = 8
    inline val Format = 12
    inline val Texels = 16
    inline val TexelBytes = 24
    inline val Levels = 32
    inline val Samples = 36
    inline val LevelTable = 40
    inline val LevelBytes = 16
    inline val LevelWidth = 0
    inline val LevelHeight = 4
    inline val LevelOffset = 8
    inline val SwizzleBytes = 16
    inline val PitchBytes = 8
    inline val InlineHeaderBytes = 16
  }

  object CompareOp {
    inline val Never = 0
    inline val Less = 1
    inline val Equal = 2
    inline val LessOrEqual = 3
    inline val Greater = 4
    inline val NotEqual = 5
    inline val GreaterOrEqual = 6
    inline val Always = 7
  }

  object Filter {
    inline val Nearest = 0
    inline val Linear = 1
  }

  object AddressMode {
    inline val Repeat = 0
    inline val MirroredRepeat = 1
    inline val ClampToEdge = 2
    inline val ClampToBorder = 3
    inline val MirrorClampToEdge = 4
  }

  object MipmapMode {
    inline val Nearest = 0
    inline val Linear = 1
  }

  object BorderColour {
    inline val FloatTransparentBlack = 0
    inline val IntTransparentBlack = 1
    inline val FloatOpaqueBlack = 2
    inline val IntOpaqueBlack = 3
    inline val FloatOpaqueWhite = 4
    inline val IntOpaqueWhite = 5
  }

  object Swizzle {
    inline val Identity = 0
    inline val Zero = 1
    inline val One = 2
    inline val R = 3
    inline val G = 4
    inline val B = 5
    inline val A = 6
  }
}
