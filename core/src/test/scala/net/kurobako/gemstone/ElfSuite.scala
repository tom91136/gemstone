package net.kurobako.gemstone

import java.nio.ByteBuffer
import java.nio.ByteOrder
import net.kurobako.gemstone.collection.*

class ElfSuite extends munit.FunSuite {
  // ELF64 header/section offsets are fixtures from the ELF file format, independent of the reader.
  private def image(order: ByteOrder): Array[Byte] = {
    val buffer = ByteBuffer.allocate(128).order(order)
    buffer.put(Array[Byte](0x7f, 'E', 'L', 'F', 2, if (order == ByteOrder.LITTLE_ENDIAN) 1 else 2))
    buffer.putLong(40, 64)
    buffer.putShort(58, 64.toShort)
    buffer.putShort(60, 1.toShort)
    buffer.putInt(64, 7)
    buffer.putInt(68, 1)
    buffer.putLong(72, 6)
    buffer.putLong(80, 0x12345678L)
    buffer.putLong(88, 128)
    buffer.array()
  }

  test("ELF64 section headers respect both byte orders") {
    for (order <- Seq(ByteOrder.LITTLE_ENDIAN, ByteOrder.BIG_ENDIAN)) {
      val sections = elf.sections64(image(order)).fold(message => fail(message), identity)
      assertEquals(sections.size, 1)
      val section = sections.head
      assertEquals(section.nameOffset, 7)
      assertEquals(section.sectionType, 1)
      assertEquals(section.flags, 6L)
      assertEquals(section.address, 0x12345678L)
      assertEquals(section.offset.raw, 128)
      assertEquals(section.size.raw, 0)
    }
  }

  test("an ELF image may have no section table") {
    val bytes  = image(ByteOrder.LITTLE_ENDIAN)
    val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
    buffer.putLong(40, 0)
    buffer.putShort(58, 0.toShort)
    buffer.putShort(60, 0.toShort)
    assertEquals(elf.sections64(bytes).map(_.size), Right(0))
  }

  test("ELF64 rejects truncated tables and out-of-image sections while allowing NOBITS") {
    val bytes = image(ByteOrder.LITTLE_ENDIAN)
    assert(elf.sections64(bytes.take(63)).isLeft)
    assert(elf.sections64(bytes.take(127)).isLeft)
    val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
    buffer.putLong(96, 4096)
    assert(elf.sections64(bytes).isLeft)
    buffer.putInt(68, 8) // SHT_NOBITS occupies memory without file contents.
    assertEquals(elf.sections64(bytes).map(_.head.size.raw), Right(4096))
    buffer.putLong(40, Long.MaxValue)
    assert(elf.sections64(bytes).isLeft)
  }
  test("ELF headers and symbol entries preserve endianness and extended strides") {
    Vec(ByteOrder.LITTLE_ENDIAN, ByteOrder.BIG_ENDIAN).foreach { order =>
      val bytes  = image(order)
      val header = ByteBuffer.wrap(bytes).order(order)
      header.putShort(16, 2.toShort).putShort(18, 190.toShort).put(8, 8.toByte).putInt(48, 89 << 8)
      val decoded = elf.header64(bytes).toOption.get
      assertEquals(decoded.machine, 190)
      assertEquals(decoded.abiVersion, 8)
      assertEquals(decoded.flags, 89 << 8)
      val symbols = ByteBuffer.allocate(32).order(order)
      symbols.putInt(0, 3).put(4, 0x11.toByte).putShort(6, 2.toShort).putLong(8, 42).putLong(16, 8)
      assertEquals(elf.symbols64(Vec.from(symbols.array()), order, 32), Right(Vec(elf.Symbol64(3, 1, 2, 42, 8))))
      assert(elf.symbols64(Vec.from(symbols.array()), order).isLeft)
      assert(elf.symbols64(Vec.empty, order, 0).isLeft)
    }
  }

  test("ELF64 REL and RELA entries preserve unsigned symbol IDs and signed explicit addends") {
    for {
      order    <- Vec(ByteOrder.LITTLE_ENDIAN, ByteOrder.BIG_ENDIAN)
      explicit <- Vec(false, true)
    } {
      val buffer = ByteBuffer.allocate(if (explicit) 24 else 16).order(order)
      buffer.putLong(0, 0x100000008L).putLong(8, 0xfedcba98abcdef01L)
      if (explicit) buffer.putLong(16, -17L)
      val bytes      = Vec.from(buffer.array())
      val relocation = elf.relocations64(bytes, order, explicit).toOption.get.head
      assertEquals(relocation.offset, 0x100000008L)
      assertEquals(relocation.symbol.raw, 0xfedcba98L)
      assertEquals(relocation.kind.raw, 0xabcdef01L)
      assertEquals(relocation.addend, Option.when(explicit)(-17L))
      assert(elf.relocations64(bytes.dropRight(1), order, explicit).isLeft)
      assertEquals(elf.relocations64(Vec.empty, order, explicit), Right(Vec.empty[elf.Relocation64]))
    }
  }

  test("ELF string tables validate offsets, terminators and UTF-8") {
    val names = Vec[Byte](0, 'k'.toByte, 0)
    assertEquals(elf.stringAt(names, 1), Right("k"))
    assert(elf.stringAt(names, -1).isLeft)
    assert(elf.stringAt(names, 3).isLeft)
    assert(elf.stringAt(Vec[Byte]('k'.toByte), 0).isLeft)
    assert(elf.stringAt(Vec[Byte](0xc3.toByte, 0x28.toByte, 0), 0).isLeft)
    val section = elf.sections64(image(ByteOrder.LITTLE_ENDIAN)).toOption.get.head
    assertEquals(
      elf.sectionBytes(image(ByteOrder.LITTLE_ENDIAN), section.copy(sectionType = 8, size = 4096)),
      Right(Vec.empty[Byte])
    )
    assert(elf.sectionBytes(image(ByteOrder.LITTLE_ENDIAN), section.copy(size = 1)).isLeft)
  }

}
