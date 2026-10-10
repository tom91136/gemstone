package net.kurobako.gemstone.isa.spirv

/** SPIR-V `ImageChannelOrder` and `ImageChannelDataType` of the view formats OpenCL images use. */
private object ImageChannels {
  private val R    = 0
  private val RG   = 2
  private val RGBA = 5
  private val BGRA = 6

  private val Depth = 13

  private val SRGBA = 17
  private val SBGRA = 18

  private val SnormInt8     = 0
  private val SnormInt16    = 1
  private val UnormInt8     = 2
  private val UnormInt16    = 3
  private val SignedInt8    = 7
  private val SignedInt16   = 8
  private val SignedInt32   = 9
  private val UnsignedInt8  = 10
  private val UnsignedInt16 = 11
  private val UnsignedInt32 = 12
  private val HalfFloat     = 13
  private val Float         = 14

  private def orders(order: Int, formats: (Int, Int)*): Seq[(Int, (Int, Int))] =
    formats.map((format, dataType) => format -> (order, dataType))

  private val table: Map[Int, (Int, Int)] = (
    orders(
      R,
      ImageFormat.R32f     -> Float,
      ImageFormat.R16f     -> HalfFloat,
      ImageFormat.R16      -> UnormInt16,
      ImageFormat.R8       -> UnormInt8,
      ImageFormat.R16Snorm -> SnormInt16,
      ImageFormat.R8Snorm  -> SnormInt8,
      ImageFormat.R32i     -> SignedInt32,
      ImageFormat.R16i     -> SignedInt16,
      ImageFormat.R8i      -> SignedInt8,
      ImageFormat.R32ui    -> UnsignedInt32,
      ImageFormat.R16ui    -> UnsignedInt16,
      ImageFormat.R8ui     -> UnsignedInt8
    ) ++ orders(
      RG,
      ImageFormat.Rg32f     -> Float,
      ImageFormat.Rg16f     -> HalfFloat,
      ImageFormat.Rg16      -> UnormInt16,
      ImageFormat.Rg8       -> UnormInt8,
      ImageFormat.Rg16Snorm -> SnormInt16,
      ImageFormat.Rg8Snorm  -> SnormInt8,
      ImageFormat.Rg32i     -> SignedInt32,
      ImageFormat.Rg16i     -> SignedInt16,
      ImageFormat.Rg8i      -> SignedInt8,
      ImageFormat.Rg32ui    -> UnsignedInt32,
      ImageFormat.Rg16ui    -> UnsignedInt16,
      ImageFormat.Rg8ui     -> UnsignedInt8
    ) ++ orders(
      RGBA,
      ImageFormat.Rgba32f     -> Float,
      ImageFormat.Rgba16f     -> HalfFloat,
      ImageFormat.Rgba16      -> UnormInt16,
      ImageFormat.Rgba8       -> UnormInt8,
      ImageFormat.Rgba16Snorm -> SnormInt16,
      ImageFormat.Rgba8Snorm  -> SnormInt8,
      ImageFormat.Rgba32i     -> SignedInt32,
      ImageFormat.Rgba16i     -> SignedInt16,
      ImageFormat.Rgba8i      -> SignedInt8,
      ImageFormat.Rgba32ui    -> UnsignedInt32,
      ImageFormat.Rgba16ui    -> UnsignedInt16,
      ImageFormat.Rgba8ui     -> UnsignedInt8
    ) ++ Seq(
      ImageDescriptor.Format.Bgra8Unorm -> (BGRA, UnormInt8),
      ImageDescriptor.Format.Rgba8Srgb  -> (SRGBA, UnormInt8),
      ImageDescriptor.Format.Bgra8Srgb  -> (SBGRA, UnormInt8),
      ImageDescriptor.Format.D16Unorm   -> (Depth, UnormInt16),
      ImageDescriptor.Format.D32Sfloat  -> (Depth, Float)
    )
  ).toMap

  def apply(format: Int): Option[(Int, Int)] = table.get(format)

  /** Whether reads of the format substitute one for a missing alpha channel. */
  def opaque(format: Int): Boolean = table.get(format).exists((order, _) => order == R || order == RG)
}
