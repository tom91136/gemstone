package net.kurobako.gemstone.texture

import java.nio.ByteBuffer
import java.nio.ByteOrder
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.texture.TextureFixtures.*

class TextureDescriptorSuite extends Suite {
  private val full = descriptor(
    TexelFormat(ChannelKind.Unsigned, 8, 4),
    width = 64,
    height = 32,
    depth = 8,
    dimensions = 3,
    pitch = 128,
    slicePitch = 4096,
    address = AddressMode.Mirror,
    filter = FilterMode.Linear,
    readMode = ReadMode.NormalisedFloat,
    normalised = true,
    srgb = true,
    border = Vector(0.25f, 0.5f, 0.75f, 1f)
  ).copy(resource = ResourceKind.Pitch2D)

  test("the table layout is fixed") {
    assertEquals(TextureLayout.Table.BaseAddress, 0x3f0000000000L)
    assertEquals(TextureLayout.Table.EntryBytes, 128L)
    assertEquals(TextureLayout.Table.Entries, 65536L)
    assert(TextureLayout.Table.BaseAddress + TextureLayout.Table.Entries * TextureLayout.Table.EntryBytes <
      0x400000000000L)
  }

  test("an entry has the declared size and field offsets") {
    val bytes  = full.encode
    val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
    assertEquals(bytes.length, 128)
    assertEquals(buffer.getLong(TextureLayout.Field.Base), Base)
    assertEquals(buffer.getLong(TextureLayout.Field.Pitch), 128L)
    assertEquals(buffer.getLong(TextureLayout.Field.SlicePitch), 4096L)
    assertEquals(buffer.getInt(TextureLayout.Field.Width), 64)
    assertEquals(buffer.getInt(TextureLayout.Field.Height), 32)
    assertEquals(buffer.getInt(TextureLayout.Field.Depth), 8)
    assertEquals(bytes(TextureLayout.Field.Object).toInt, TextureLayout.Object.Texture)
    assertEquals(bytes(TextureLayout.Field.Dimensions).toInt, 3)
    assertEquals(bytes(TextureLayout.Field.ChannelKind).toInt, TextureLayout.Channel.Unsigned)
    assertEquals(bytes(TextureLayout.Field.BitsW).toInt, 8)
    assertEquals(bytes(TextureLayout.Field.AddressZ).toInt, TextureLayout.Address.Mirror)
    assertEquals(bytes(TextureLayout.Field.Filter).toInt, TextureLayout.Filter.Linear)
    assertEquals(buffer.getFloat(TextureLayout.Field.BorderZ), 0.75f)
    val flags = TextureLayout.Flag.NormalisedCoordinates | TextureLayout.Flag.Srgb | TextureLayout.Flag.NormalisedRead |
      (TextureLayout.Resource.Pitch2D << TextureLayout.Flag.ResourceShift)
    assertEquals(bytes(TextureLayout.Field.Flags).toInt, flags)
  }

  test("fields do not overlap and fit the entry") {
    import TextureLayout.Field.*
    val spans = Vector(
      Base        -> 8,
      Pitch       -> 8,
      SlicePitch  -> 8,
      Width       -> 4,
      Height      -> 4,
      Depth       -> 4,
      Object      -> 1,
      Dimensions  -> 1,
      ChannelKind -> 1,
      Flags       -> 1,
      BitsX       -> 1,
      BitsY       -> 1,
      BitsZ       -> 1,
      BitsW       -> 1,
      AddressX    -> 1,
      AddressY    -> 1,
      AddressZ    -> 1,
      Filter      -> 1,
      BorderX     -> 4,
      BorderY     -> 4,
      BorderZ     -> 4,
      BorderW     -> 4,
      LevelTable  -> 8,
      Levels      -> 4,
      MipFilter   -> 1,
      MinLod      -> 4,
      MaxLod      -> 4,
      LodBias     -> 4
    )
    val covered = spans.flatMap((offset, size) => offset until offset + size)
    assertEquals(covered.distinct.size, covered.size)
    assertEquals(covered.max, 91)
    assertEquals(covered.take(64), (0 until 64).toVector)
  }

  test("an entry decodes to the descriptor that wrote it") {
    assertEquals(TextureDescriptor.decode(full.encode), Some(full))
    val surface =
      descriptor(F32x4, 16, 16, dimensions = 2, kind = ObjectKind.Surface).copy(resource = ResourceKind.Array)
    assertEquals(TextureDescriptor.decode(surface.encode), Some(surface))
    val linear = descriptor(U8x1, 4096, address = AddressMode.Border).copy(resource = ResourceKind.Linear)
    assertEquals(TextureDescriptor.decode(linear.encode), Some(linear))
  }

  test("every channel kind, address mode, and filter round trips") {
    ChannelKind.values.foreach { kind =>
      val format = TexelFormat(kind, width = 16, channels = 2)
      val d      = descriptor(format, 8)
      assertEquals(TextureDescriptor.decode(d.encode).map(_.format), Some(format))
    }
    AddressMode.values.foreach { mode =>
      assertEquals(
        TextureDescriptor.decode(descriptor(F32x1, 4, address = mode).encode).map(_.address),
        Some(Vector.fill(3)(mode))
      )
    }
    FilterMode.values.foreach { mode =>
      assertEquals(TextureDescriptor.decode(descriptor(F32x1, 4, filter = mode).encode).map(_.filter), Some(mode))
    }
  }

  test("a mipmapped entry decodes with its level chain") {
    val chain  = MipChain(Base + 0x1000, 5, FilterMode.Linear, 0.5f, 3.25f, -1.5f)
    val mapped = full.copy(resource = ResourceKind.MipmappedArray, mip = Some(chain))
    assertEquals(TextureDescriptor.decode(mapped.encode), Some(mapped))
    val empty = mapped.encode.clone()
    ByteBuffer.wrap(empty).order(ByteOrder.LITTLE_ENDIAN).putInt(TextureLayout.Field.Levels, 0)
    assertEquals(TextureDescriptor.decode(empty), None)
  }

  test("free, short, and corrupt entries do not decode") {
    assertEquals(TextureDescriptor.decode(Array.ofDim[Byte](128)), None)
    assertEquals(TextureDescriptor.decode(full.encode.take(127)), None)
    def corrupt(field: Int, value: Int): Option[TextureDescriptor] = {
      val bytes = full.encode
      bytes(field) = value.toByte
      TextureDescriptor.decode(bytes)
    }
    assertEquals(corrupt(TextureLayout.Field.Object, 9), None)
    assertEquals(corrupt(TextureLayout.Field.ChannelKind, 9), None)
    assertEquals(corrupt(TextureLayout.Field.AddressY, 9), None)
    assertEquals(corrupt(TextureLayout.Field.Filter, 9), None)
    assertEquals(corrupt(TextureLayout.Field.Dimensions, 0), None)
    assertEquals(corrupt(TextureLayout.Field.Dimensions, 4), None)
    assertEquals(corrupt(TextureLayout.Field.BitsX, 12), None)
  }

  test("texel addresses combine slice, row, and column strides") {
    assertEquals(full.texelAddress(3, 2, 1), Base + 4096 + 2 * 128 + 3 * 4)
    assertEquals(full.widthBytes, 256L)
  }

  test("handles index the table by their low 32 bits") {
    import TextureLayout.Table.*
    assertEquals(TextureTable.entryAddress(0L), None)
    assertEquals(TextureTable.entryAddress(1L), Some(BaseAddress + EntryBytes))
    assertEquals(TextureTable.entryAddress(0xdeadbeef00000001L), Some(BaseAddress + EntryBytes))
    assertEquals(TextureTable.entryAddress(65535L), Some(BaseAddress + 65535 * EntryBytes))
    assertEquals(TextureTable.entryAddress(65536L), None)
    assertEquals(TextureTable.entryAddress(0xffffffffL), None)
    assertEquals(TextureTable.entryAddress(0x100000000L), None)
  }

  private def table(entries: (Int, TextureDescriptor)*): ArrayMemory = {
    val memory =
      ArrayMemory(TextureLayout.Table.BaseAddress, Array.ofDim[Byte](4 * TextureLayout.Table.EntryBytes.toInt))
    entries.foreach((index, d) =>
      memory.write(TextureLayout.Table.BaseAddress + index * TextureLayout.Table.EntryBytes, d.encode)
    )
    memory
  }

  test("handles resolve to live entries of the expected kind") {
    val surface = descriptor(F32x1, 4, kind = ObjectKind.Surface)
    val memory  = table(2 -> full, 3 -> surface)
    assertEquals(TextureTable.resolve(2L, ObjectKind.Texture, memory), Right(full))
    assertEquals(TextureTable.resolve(0x500000003L, ObjectKind.Surface, memory), Right(surface))
    assertEquals(TextureTable.resolve(3L, ObjectKind.Texture, memory), Left(TextureTable.Failure.NotLive(3L)))
    assertEquals(TextureTable.resolve(1L, ObjectKind.Texture, memory), Left(TextureTable.Failure.NotLive(1L)))
  }

  test("invalid and unreadable handles fail distinctly") {
    val memory = table(2 -> full)
    assertEquals(TextureTable.resolve(0L, ObjectKind.Texture, memory), Left(TextureTable.Failure.InvalidHandle(0L)))
    assertEquals(
      TextureTable.resolve(70000L, ObjectKind.Texture, memory),
      Left(TextureTable.Failure.InvalidHandle(70000L))
    )
    assertEquals(
      TextureTable.resolve(9L, ObjectKind.Texture, memory),
      Left(TextureTable.Failure.Unreadable(TextureLayout.Table.BaseAddress + 9 * TextureLayout.Table.EntryBytes))
    )
  }
}
