package net.kurobako.gemstone.texture

import java.nio.ByteBuffer
import java.nio.ByteOrder
import net.kurobako.gemstone.collection.*

object TextureDescriptor {

  /** The descriptor in an entry, or none when the entry is free or holds an unknown code or an unreadable format. */
  def decode(entry: Array[Byte]): Option[TextureDescriptor] = {
    import TextureLayout.*
    if (entry.length < Table.EntryBytes) None
    else {
      val buffer                = ByteBuffer.wrap(entry).order(ByteOrder.LITTLE_ENDIAN)
      def byte(field: Int): Int = buffer.get(field) & 0xff
      val flags                 = byte(Field.Flags)
      val bits  = Vector(Field.BitsX, Field.BitsY, Field.BitsZ, Field.BitsW).map(byte(_)).takeWhile(_ != 0)
      val modes = Vector(Field.AddressX, Field.AddressY, Field.AddressZ).map(field => AddressMode.fromCode(byte(field)))
      for {
        kind     <- ObjectKind.fromCode(byte(Field.Object))
        channel  <- ChannelKind.fromCode(byte(Field.ChannelKind))
        resource <- ResourceKind.fromCode((flags >> Flag.ResourceShift) & Flag.ResourceMask)
        filter   <- FilterMode.fromCode(byte(Field.Filter))
        mip      <- decodeChain(buffer, resource)
        format = TexelFormat(channel, bits)
        if format.isValid && modes.forall(_.isDefined)
        dimensions = byte(Field.Dimensions)
        if dimensions >= 1 && dimensions <= 3
      } yield TextureDescriptor(
        kind = kind,
        resource = resource,
        dimensions = dimensions,
        base = buffer.getLong(Field.Base),
        pitch = buffer.getLong(Field.Pitch),
        slicePitch = buffer.getLong(Field.SlicePitch),
        width = buffer.getInt(Field.Width),
        height = buffer.getInt(Field.Height),
        depth = buffer.getInt(Field.Depth),
        format = format,
        address = modes.flatten,
        filter = filter,
        readMode = if ((flags & Flag.NormalisedRead) != 0) ReadMode.NormalisedFloat else ReadMode.ElementType,
        srgb = (flags & Flag.Srgb) != 0,
        normalisedCoordinates = (flags & Flag.NormalisedCoordinates) != 0,
        border = Vector(Field.BorderX, Field.BorderY, Field.BorderZ, Field.BorderW).map(buffer.getFloat(_)),
        mip = mip
      )
    }
  }

  private def decodeChain(buffer: ByteBuffer, resource: ResourceKind): Option[Option[MipChain]] = {
    import TextureLayout.*
    if (resource != ResourceKind.MipmappedArray) Some(None)
    else {
      val levels = buffer.getInt(Field.Levels)
      FilterMode.fromCode(buffer.get(Field.MipFilter) & 0xff).filter(_ => levels >= 1).map { filter =>
        Some(MipChain(
          table = buffer.getLong(Field.LevelTable),
          levels = levels,
          filter = filter,
          minLod = buffer.getFloat(Field.MinLod),
          maxLod = buffer.getFloat(Field.MaxLod),
          bias = buffer.getFloat(Field.LodBias)
        ))
      }
    }
  }
}

/** One entry of the descriptor table. `width`, `height`, and `depth` count texels, `pitch` and `slicePitch` bytes. Rows
  * of a 1D object are one texel high and slices of a 2D object are one deep. `border` is the colour read outside under
  * [[AddressMode.Border]]. The geometry is that of level zero, and `mip` addresses the rest of a mipmapped chain.
  */
case class TextureDescriptor(
    kind: ObjectKind,
    resource: ResourceKind,
    dimensions: Int,
    base: Long,
    pitch: Long,
    slicePitch: Long,
    width: Int,
    height: Int,
    depth: Int,
    format: TexelFormat,
    address: Vector[AddressMode],
    filter: FilterMode,
    readMode: ReadMode,
    srgb: Boolean,
    normalisedCoordinates: Boolean,
    border: Vector[Float],
    mip: Option[MipChain] = None
) derives CanEqual {
  def extent(axis: Int): Int = axis match {
    case 0 => width
    case 1 => height
    case _ => depth
  }

  def widthBytes: Long = width.toLong * format.texelBytes

  /** This descriptor re-addressed to the mip level `index`, which is clamped to the chain. */
  def atLevel(chain: MipChain, index: Int, memory: TexelMemory): Either[TexelFault, TextureDescriptor] = {
    import TextureLayout.*
    val address = chain.table + Math.max(0, Math.min(chain.levels - 1, index)).toLong * Level.Bytes
    memory.read(address, Level.Bytes).toRight(TexelFault.Unmapped(address)).map { record =>
      val buffer = ByteBuffer.wrap(record).order(ByteOrder.LITTLE_ENDIAN)
      copy(
        base = buffer.getLong(Level.Base),
        pitch = buffer.getLong(Level.Pitch),
        slicePitch = buffer.getLong(Level.SlicePitch),
        width = buffer.getInt(Level.Width),
        height = buffer.getInt(Level.Height),
        depth = buffer.getInt(Level.Depth),
        mip = None
      )
    }
  }

  @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
  def texelAddress(x: Long, y: Long, z: Long): Long = base + z * slicePitch + y * pitch + x * format.texelBytes

  def encode: Vec[Byte] = {
    import TextureLayout.*
    val buffer = ByteBuffer.allocate(Table.EntryBytes.toInt).order(ByteOrder.LITTLE_ENDIAN)
    buffer.putLong(Field.Base, base)
    buffer.putLong(Field.Pitch, pitch)
    buffer.putLong(Field.SlicePitch, slicePitch)
    buffer.putInt(Field.Width, width)
    buffer.putInt(Field.Height, height)
    buffer.putInt(Field.Depth, depth)
    buffer.put(Field.Object, kind.code.toByte)
    buffer.put(Field.Dimensions, dimensions.toByte)
    buffer.put(Field.ChannelKind, format.kind.code.toByte)
    val flags = (if (normalisedCoordinates) Flag.NormalisedCoordinates else 0) | (if (srgb) Flag.Srgb else 0) |
      (if (readMode == ReadMode.NormalisedFloat) Flag.NormalisedRead else 0) |
      ((resource.code & Flag.ResourceMask) << Flag.ResourceShift)
    buffer.put(Field.Flags, flags.toByte)
    val bits = Vector(Field.BitsX, Field.BitsY, Field.BitsZ, Field.BitsW)
    bits.zipWithIndex.foreach((field, i) => buffer.put(field, format.bits.lift(i).getOrElse(0).toByte))
    val modes = Vector(Field.AddressX, Field.AddressY, Field.AddressZ)
    modes.zipWithIndex.foreach((field, i) => buffer.put(field, address(i).code.toByte))
    buffer.put(Field.Filter, filter.code.toByte)
    val colours = Vector(Field.BorderX, Field.BorderY, Field.BorderZ, Field.BorderW)
    colours.zipWithIndex.foreach((field, i) => buffer.putFloat(field, border(i)))
    mip.foreach { chain =>
      buffer.putLong(Field.LevelTable, chain.table)
      buffer.putInt(Field.Levels, chain.levels)
      buffer.put(Field.MipFilter, chain.filter.code.toByte)
      buffer.putFloat(Field.MinLod, chain.minLod)
      buffer.putFloat(Field.MaxLod, chain.maxLod)
      buffer.putFloat(Field.LodBias, chain.bias)
    }
    Vec.unsafeWrapArray(buffer.array())
  }
}
