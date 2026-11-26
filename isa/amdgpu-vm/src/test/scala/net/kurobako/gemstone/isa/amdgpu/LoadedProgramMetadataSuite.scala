package net.kurobako.gemstone.isa.amdgpu

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*

class LoadedProgramMetadataSuite extends Suite {
  private val generic = "wr_target_archE4294967295E".refine[NonEmpty[String]].value
  private val variant = "wr_target_archE1030E".refine[NonEmpty[String]].value

  private def string(value: String): Array[Byte] =
    Array((0xa0 | value.length).toByte) ++ value.getBytes(StandardCharsets.UTF_8)

  private def unsigned(value: Int): Array[Byte] =
    if (value < 128) Array(value.toByte)
    else Array(0xce.toByte, (value >>> 24).toByte, (value >>> 16).toByte, (value >>> 8).toByte, value.toByte)

  private def concat(parts: Array[Byte]*): Array[Byte] = parts.view.flatten.toArray

  private def note(kernels: Seq[(String, Int)]): Array[Byte] = {
    val entries = kernels.map { case (name, limit) =>
      concat(
        Array(0x84.toByte),
        string(".name"),
        string(name),
        string(".symbol"),
        string(name + ".kd"),
        string(".kernarg_segment_size"),
        unsigned(20),
        string(".max_flat_workgroup_size"),
        unsigned(limit)
      )
    }
    val metadata =
      concat(Array(0x81.toByte), string("amdhsa.kernels"), Array((0x90 | entries.size).toByte), concat(entries*))
    val description = metadata ++ Array.ofDim[Byte](((metadata.length + 3) & ~3) - metadata.length)
    bytes.words(7, metadata.length, 32) ++ "AMDGPU\u0000\u0000".getBytes(StandardCharsets.US_ASCII) ++ description
  }

  private def image(): Array[Byte] = {
    val target     = "amdgcn-amd-amdhsa--gfx1036\u0000".getBytes(StandardCharsets.US_ASCII)
    val descriptor = ByteBuffer.allocate(64).order(ByteOrder.LITTLE_ENDIAN)
      .putInt(0, 16).putInt(4, 8).putInt(8, 20).putInt(52, 0x80).array()
    val (names, table) = elfImage.symbols(Seq(
      elfImage.Symbol(generic, 2, 4, 0x1000L, 8L),
      elfImage.Symbol(generic + ".kd", 1, 5, 0x2000L, 64L),
      elfImage.Symbol(variant, 2, 4, 0x1008L, 8L),
      elfImage.Symbol(variant + ".kd", 1, 5, 0x2040L, 64L)
    ))
    elfImage.write(
      224,
      Seq(
        elfImage.Section(".shstrtab", 3),
        elfImage.Section(".strtab", 3, names),
        elfImage.Section(".symtab", 2, table, link = 2, entrySize = 24),
        elfImage.Section(".text", 1, Vec.fill(16)(0.toByte), address = 0x1000L),
        elfImage.Section(".rodata", 1, Vec.unsafeWrapArray(descriptor ++ descriptor ++ target), address = 0x2000L),
        elfImage.Section(".note", 7, Vec.unsafeWrapArray(note(Seq(generic -> 256, variant -> 192))))
      )
    )
  }

  test("an exact kernel name keeps its own launch bound ahead of the selected variant alias") {
    val aliases = KernelVariants.select(Set(generic, variant), "gfx1036", _ => true).value
    assertEquals(aliases(generic), variant, "the generic name aliases the 192-bound gfx1030 body")

    val decoded = CodeObject.decode(Vec.unsafeWrapArray(image())).value
    val loaded  = LoadedProgram.load(decoded)
    val result  = loaded.kernel(generic)

    assertEquals(
      result.toOption.flatMap(_.maxThreadsPerWorkGroup.map(_.raw)),
      Some(256),
      "the launch bound must come from the exact kernel, not its variant alias"
    )
  }
}
