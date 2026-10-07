package net.kurobako.gemstone.testing

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets

/** Little-endian ELF64 images assembled from the file-format layout, independent of the reader under test. */
object elfImage {
  case class Section(
      name: String,
      sectionType: Int,
      contents: Array[Byte] = Array.emptyByteArray,
      address: Long = 0L,
      flags: Long = 0L,
      link: Int = 0,
      info: Int = 0,
      entrySize: Int = 0,
      noBitsSize: Int = 0
  )

  case class Symbol(name: String, kind: Int, section: Int, value: Long, size: Long)

  case class Relocation(offset: Long, symbol: Long, kind: Long, addend: Option[Long] = None)

  private val SectionNames = ".shstrtab"

  /** A string table and the offset of each name in it. */
  def strings(names: Seq[String]): (Array[Byte], Map[String, Int]) = {
    val (bytes, offsets) = names.distinct.foldLeft((Vector[Byte](0), Map.empty[String, Int])) {
      case ((table, offsets), name) =>
        (table ++ name.getBytes(StandardCharsets.UTF_8) :+ 0.toByte) -> offsets.updated(name, table.size)
    }
    bytes.toArray -> offsets.updated("", 0)
  }

  /** `.strtab` and `.symtab` contents; the null symbol is prepended. */
  def symbols(entries: Seq[Symbol]): (Array[Byte], Array[Byte]) = {
    val (table, offsets) = strings(entries.map(_.name))
    val buffer           = ByteBuffer.allocate((entries.size + 1) * 24).order(ByteOrder.LITTLE_ENDIAN)
    entries.zipWithIndex.foreach { case (symbol, index) =>
      val start = (index + 1) * 24
      buffer.putInt(start, offsets(symbol.name)).put(start + 4, symbol.kind.toByte)
        .putShort(start + 6, symbol.section.toShort).putLong(start + 8, symbol.value)
      val _ = buffer.putLong(start + 16, symbol.size)
    }
    table -> buffer.array()
  }

  def relocations(entries: Seq[Relocation]): Array[Byte] = {
    val width  = if (entries.exists(_.addend.nonEmpty)) 24 else 16
    val buffer = ByteBuffer.allocate(entries.size * width).order(ByteOrder.LITTLE_ENDIAN)
    entries.zipWithIndex.foreach { case (entry, index) =>
      val start = index * width
      buffer.putLong(start, entry.offset).putLong(start + 8, (entry.symbol << 32) | (entry.kind & 0xffffffffL))
      entry.addend.foreach(addend => buffer.putLong(start + 16, addend))
    }
    buffer.array()
  }

  /** Sections are numbered from 1 in order; a section named `.shstrtab` receives the generated section names, and one
    * is appended when absent.
    */
  def write(
      machine: Int,
      sections: Seq[Section],
      imageType: Int = 3,
      abiVersion: Int = 0,
      flags: Int = 0
  ): Array[Byte] = {
    val withNames = if (sections.exists(_.name == SectionNames)) sections else sections :+ Section(SectionNames, 3)
    val (names, nameAt) = strings(withNames.map(_.name))
    val resolved        =
      withNames.map(section => if (section.name == SectionNames) section.copy(contents = names) else section)
    def aligned(value: Int): Int = (value + 7) & ~7
    val offsets                  = resolved.scanLeft(64)((offset, section) => aligned(offset + section.contents.length))
    val table                    = offsets.last
    val buffer                   = ByteBuffer.allocate(table + (resolved.size + 1) * 64).order(ByteOrder.LITTLE_ENDIAN)
    buffer.put(Array[Byte](0x7f, 'E', 'L', 'F', 2, 1, 1)).put(8, abiVersion.toByte)
    buffer.putShort(16, imageType.toShort).putShort(18, machine.toShort).putInt(20, 1).putLong(40, table.toLong)
      .putInt(48, flags).putShort(52, 64.toShort).putShort(58, 64.toShort).putShort(60, (resolved.size + 1).toShort)
      .putShort(62, (resolved.indexWhere(_.name == SectionNames) + 1).toShort)
    resolved.zip(offsets).zipWithIndex.foreach { case ((section, offset), index) =>
      Array.copy(section.contents, 0, buffer.array(), offset, section.contents.length)
      val start = table + (index + 1) * 64
      buffer.putInt(
        start,
        nameAt(section.name)
      ).putInt(start + 4, section.sectionType).putLong(start + 8, section.flags)
        .putLong(start + 16, section.address).putLong(start + 24, offset.toLong)
        .putLong(start + 32, (if (section.sectionType == 8) section.noBitsSize else section.contents.length).toLong)
        .putInt(start + 40, section.link).putInt(start + 44, section.info).putLong(start + 48, 8L)
      val _ = buffer.putLong(start + 56, section.entrySize.toLong)
    }
    buffer.array()
  }

  /** A gfx900 HSA code object: `.text` (section 4) holds `kernel`, `.rodata` (5) its descriptor, `.bss` (6) the 16-byte
    * `device_global` (symbol 3). With `globalPointer`, `.got` (7) at 0x4000 receives `device_global`'s address through
    * an `R_AMDGPU_ABS64` in `.rela.dyn` (8).
    */
  def amdgpu(
      code: Array[Byte],
      kernargSize: Int,
      kernelCodeProperties: Int,
      globalPointer: Boolean = false,
      extraSymbols: Seq[Symbol] = Seq.empty
  ): Array[Byte] = {
    val target     = "amdgcn-amd-amdhsa--gfx900\u0000".getBytes(StandardCharsets.US_ASCII)
    val descriptor = ByteBuffer.allocate(64 + target.length).order(ByteOrder.LITTLE_ENDIAN)
      .putInt(0, 16).putInt(4, 8).putInt(8, kernargSize).putInt(52, 0x80).putShort(56, kernelCodeProperties.toShort)
      .array()
    Array.copy(target, 0, descriptor, 64, target.length)
    val (symbolNames, symbolTable) = symbols(
      Seq(
        Symbol("kernel", 2, 4, 0x1000L, code.length.toLong),
        Symbol("kernel.kd", 1, 5, 0x2000L, 64L),
        Symbol("device_global", 1, 6, 0x3000L, 16L)
      ) ++ extraSymbols
    )
    write(
      224,
      Seq(
        Section(SectionNames, 3),
        Section(".strtab", 3, symbolNames),
        Section(".symtab", 2, symbolTable, link = 2, entrySize = 24),
        Section(".text", 1, code, address = 0x1000L),
        Section(".rodata", 1, descriptor, address = 0x2000L),
        Section(".bss", 8, address = 0x3000L, noBitsSize = 16)
      ) ++
        (if (globalPointer)
           Seq(
             Section(".got", 1, Array.ofDim[Byte](8), address = 0x4000L),
             Section(
               ".rela.dyn",
               4,
               relocations(Seq(Relocation(0x4000L, 3L, 3L, Some(0L)))),
               address = 0x5000L,
               link = 3,
               entrySize = 24
             )
           )
         else Seq.empty)
    )
  }
}
