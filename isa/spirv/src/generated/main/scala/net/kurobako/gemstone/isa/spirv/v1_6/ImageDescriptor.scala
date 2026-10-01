// Generated from Gemstone ImageDescriptorABI. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.spirv.v1_6

@javax.annotation.processing.Generated(Array("sbt codegen"))
object ImageDescriptor {
  object Format {
    final val D16Unorm: Int = 1000
    final val Bgra8Unorm: Int = 1001
    final val D32SfloatS8Depth: Int = 1002
    final val S8Uint: Int = 1003
    final val D32SfloatS8Stencil: Int = 1004
    final val Rgba8Srgb: Int = 1005
    final val Bgra8Srgb: Int = 1006
    final val B4G4R4A4Unorm: Int = 1007
    final val R5G6B5Unorm: Int = 1008
    final val A1R5G5B5Unorm: Int = 1009
    final val E5B9G9R9Ufloat: Int = 1010
    final val DecodedRgb32f: Int = 1011
  }

  object Flags {
    final val LevelTable: Long = 2147483648L
    final val Volume: Long = 1073741824L
    final val Swizzle: Long = 536870912L
    final val FormatMask: Long = 536870911L
  }

  object SamplerWord {
    final val MinFilter: Int = 0
    final val MagFilter: Int = 1
    final val AddressU: Int = 2
    final val AddressV: Int = 3
    final val Unnormalized: Int = 4
    final val CompareEnable: Int = 5
    final val CompareOp: Int = 6
    final val BorderColor: Int = 7
    final val MipLodBias: Int = 8
    final val MinLod: Int = 9
    final val MaxLod: Int = 10
    final val MipmapMode: Int = 11
    final val AddressW: Int = 12
    final val Count: Int = 13
  }

  object Layout {
    final val Width: Int = 0
    final val Height: Int = 4
    final val Slices: Int = 8
    final val Format: Int = 12
    final val Texels: Int = 16
    final val TexelBytes: Int = 24
    final val Levels: Int = 32
    final val Samples: Int = 36
    final val LevelTable: Int = 40
    final val LevelBytes: Int = 16
    final val LevelWidth: Int = 0
    final val LevelHeight: Int = 4
    final val LevelOffset: Int = 8
    final val SwizzleBytes: Int = 16
    final val InlineHeaderBytes: Int = 16
  }

  object CompareOp {
    final val Never: Int = 0
    final val Less: Int = 1
    final val Equal: Int = 2
    final val LessOrEqual: Int = 3
    final val Greater: Int = 4
    final val NotEqual: Int = 5
    final val GreaterOrEqual: Int = 6
    final val Always: Int = 7
  }

  object Filter {
    final val Nearest: Int = 0
    final val Linear: Int = 1
  }

  object AddressMode {
    final val Repeat: Int = 0
    final val MirroredRepeat: Int = 1
    final val ClampToEdge: Int = 2
    final val ClampToBorder: Int = 3
    final val MirrorClampToEdge: Int = 4
  }

  object MipmapMode {
    final val Nearest: Int = 0
    final val Linear: Int = 1
  }

  object BorderColor {
    final val FloatTransparentBlack: Int = 0
    final val IntTransparentBlack: Int = 1
    final val FloatOpaqueBlack: Int = 2
    final val IntOpaqueBlack: Int = 3
    final val FloatOpaqueWhite: Int = 4
    final val IntOpaqueWhite: Int = 5
  }

  object Swizzle {
    final val Identity: Int = 0
    final val Zero: Int = 1
    final val One: Int = 2
    final val R: Int = 3
    final val G: Int = 4
    final val B: Int = 5
    final val A: Int = 6
  }
}
