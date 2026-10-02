package net.kurobako.gemstone

import java.nio.ByteBuffer
import java.nio.ByteOrder

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
      assertEquals(section.offset, 128)
      assertEquals(section.size, 0)
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
    assertEquals(elf.sections64(bytes).map(_.head.size), Right(4096))
    buffer.putLong(40, Long.MaxValue)
    assert(elf.sections64(bytes).isLeft)
  }
}
