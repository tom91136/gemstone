package net.kurobako.gemstone

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*

class ElfSuite extends munit.FunSuite {
  private val (symbolNames, symbolTable) = elfImage.symbols(
    Seq(elfImage.Symbol("kernel", 2, 3, 0x1010L, 4L), elfImage.Symbol("global", 1, 4, 8L, 8L))
  )
  private def image(imageType: Int = 3): Array[Byte] = elfImage.write(
    190,
    Seq(
      elfImage.Section(".strtab", 3, symbolNames),
      elfImage.Section(".symtab", 2, symbolTable, link = 1, entrySize = 24),
      elfImage.Section(
        ".text",
        1,
        bytes.words(0x01234567, 0x7f, 0x11, 0x22, 0x33, 0x44),
        address = 0x1000L,
        flags = 6L
      ),
      elfImage.Section(".bss", 8, address = 0x2000L, noBitsSize = 4096)
    ),
    imageType = imageType,
    abiVersion = 8,
    flags = 89 << 8
  )

  private def parse(bytes: Array[Byte]) = elf.Image.parse(Vec.unsafeWrapArray(bytes))

  test("ELF64 images expose named sections, symbols, and section contents") {
    val parsed = parse(image()).value
    assertEquals(parsed.header.machine.raw, 190)
    assertEquals(parsed.header.abiVersion.raw, 8)
    assertEquals(parsed.header.flags, 89 << 8)
    assertEquals(parsed.sections.map(_.name), Vec("", ".strtab", ".symtab", ".text", ".bss", ".shstrtab"))
    val text = parsed.section(".text").get
    assertEquals(text.address, 0x1000L)
    assertEquals(parsed.contents(text).take(4), Vec[Byte](0x67, 0x45, 0x23, 0x01))
    assertEquals(parsed.contents(parsed.section(".bss").get), Vec.empty[Byte])
    val symbols = parsed.symbols.get
    assertEquals(symbols.head.name, None)
    assertEquals(symbols.flatMap(_.name), Vec[String]("kernel", "global"))
    assertEquals(parsed.symbolBytes(symbols(1)).value, Vec[Byte](0x33, 0, 0, 0))
  }

  test("symbol offsets are addresses in executables and section offsets in relocatable images") {
    val executable = parse(image()).value
    assertEquals(executable.symbolOffset(executable.symbols.get(1)).value._2.raw, 0x10)
    val relocatable = parse(image(imageType = 1)).value
    assert(relocatable.symbolOffset(relocatable.symbols.get(1)).isLeft)
    assertEquals(relocatable.symbolOffset(relocatable.symbols.get(2)).value._2.raw, 8)
  }

  test("an ELF image may have no section table") {
    val header = image().take(64)
    val _      = bytes.longs(0L).copyToArray(header, 40)
    val _      = bytes.shorts(0, 0).copyToArray(header, 58)
    assertEquals(parse(header).map(_.sections.size), Right(0))
  }

  test("ELF64 rejects big-endian, truncated tables, and out-of-image sections while allowing NOBITS") {
    val valid = image()
    assert(parse(valid.take(63)).isLeft)
    assert(parse(valid.dropRight(1)).isLeft)
    val bigEndian = valid.clone()
    bigEndian(5) = 2
    assert(parse(bigEndian).isLeft)
    val sections  = LittleEndian.decodeInt64(valid, 40).toInt
    val oversized = valid.clone()
    val _         = bytes.longs(1L << 20).copyToArray(oversized, sections + 3 * 64 + 32)
    assert(parse(oversized).isLeft)
    val noBits = oversized.clone()
    val _      = bytes.words(8).copyToArray(noBits, sections + 3 * 64 + 4)
    assertEquals(parse(noBits).map(_.sections(3).size.raw), Right(1 << 20))
    val unnamed = valid.clone()
    val _       = bytes.words(8).copyToArray(unnamed, sections + 5 * 64 + 4)
    assert(parse(unnamed).isLeft, "the section-name table must be a string table")
  }

  test("ELF64 symbol tables accept extended strides and reject partial entries") {
    val names  = Vec[Byte](0, 'k', 0)
    val padded = Vec.unsafeWrapArray(bytes.words(1, 0x00020011, 42, 0, 8, 0, 0, 0))
    assertEquals(
      elf.symbols(padded, names, 32),
      Right(Vec(elf.Symbol(Some("k"), 1, 2, 42L, 8)))
    )
    assert(elf.symbols(padded, names, 0).isLeft)
    assert(elf.symbols(padded.take(24), names, 16).isLeft)
  }

  test("ELF64 REL and RELA entries preserve unsigned symbol IDs and signed explicit addends") {
    for (explicit <- Vec(false, true)) {
      val entry      = bytes.longs((Seq(0x100000008L, 0xfedcba98abcdef01L) ++ Option.when(explicit)(-17L))*)
      val relocation = elf.relocations(Vec.unsafeWrapArray(entry), explicit, 0).value.head
      assertEquals(relocation.offset, 0x100000008L)
      assertEquals(relocation.symbol.raw, 0xfedcba98L)
      assertEquals(relocation.kind.raw, 0xabcdef01L)
      assertEquals(relocation.addend, Option.when(explicit)(-17L))
      assert(elf.relocations(Vec.unsafeWrapArray(entry.dropRight(1)), explicit, 0).isLeft)
      assertEquals(elf.relocations(Vec.empty, explicit, 0), Right(Vec.empty[elf.Relocation]))
    }
  }

  test("ELF string tables validate offsets, terminators and UTF-8") {
    val names = Vec[Byte](0, 'k'.toByte, 0)
    assertEquals(elf.stringAt(names, 1), Right("k"))
    assert(elf.stringAt(names, -1).isLeft)
    assert(elf.stringAt(names, 3).isLeft)
    assert(elf.stringAt(Vec[Byte]('k'.toByte), 0).isLeft)
    assert(elf.stringAt(Vec[Byte](0xc3.toByte, 0x28.toByte, 0), 0).isLeft)
  }
}
