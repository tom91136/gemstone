package net.kurobako.gemstone.texture

import java.nio.ByteBuffer
import java.nio.ByteOrder

object TextureFixtures {
  val Base: Long = 0x400000000000L

  def descriptor(
      format: TexelFormat,
      width: Int,
      height: Int = 1,
      depth: Int = 1,
      dimensions: Int = 1,
      pitch: Long = 0,
      slicePitch: Long = 0,
      address: AddressMode = AddressMode.Clamp,
      filter: FilterMode = FilterMode.Point,
      readMode: ReadMode = ReadMode.ElementType,
      normalised: Boolean = false,
      srgb: Boolean = false,
      kind: ObjectKind = ObjectKind.Texture,
      border: Vector[Float] = Vector.fill(4)(0f)
  ): TextureDescriptor = {
    val rowBytes = if (pitch != 0) pitch else width.toLong * format.texelBytes
    TextureDescriptor(
      kind = kind,
      resource = ResourceKind.Array,
      dimensions = dimensions,
      base = Base,
      pitch = rowBytes,
      slicePitch = if (slicePitch != 0) slicePitch else rowBytes * height,
      width = width,
      height = height,
      depth = depth,
      format = format,
      address = Vector.fill(3)(address),
      filter = filter,
      readMode = readMode,
      srgb = srgb,
      normalisedCoordinates = normalised,
      border = border
    )
  }

  val F32x1: TexelFormat = TexelFormat(ChannelKind.Float, width = 32, channels = 1)
  val F32x4: TexelFormat = TexelFormat(ChannelKind.Float, width = 32, channels = 4)
  val U8x1: TexelFormat  = TexelFormat(ChannelKind.Unsigned, width = 8, channels = 1)
  val U8x4: TexelFormat  = TexelFormat(ChannelKind.Unsigned, width = 8, channels = 4)
  val S8x1: TexelFormat  = TexelFormat(ChannelKind.Signed, width = 8, channels = 1)
  val U32x1: TexelFormat = TexelFormat(ChannelKind.Unsigned, width = 32, channels = 1)
  val S16x2: TexelFormat = TexelFormat(ChannelKind.Signed, width = 16, channels = 2)
  val F16x2: TexelFormat = TexelFormat(ChannelKind.Float, width = 16, channels = 2)

  def floats(values: Float*): ArrayMemory = {
    val buffer = ByteBuffer.allocate(values.length * 4).order(ByteOrder.LITTLE_ENDIAN)
    values.foreach(buffer.putFloat(_))
    ArrayMemory(Base, buffer.array())
  }

  def memoryOf(values: Int*): ArrayMemory = ArrayMemory(Base, values.map(_.toByte).toArray)
}
