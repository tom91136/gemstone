package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*

class CubinParserSuite extends munit.FunSuite {
  private def metadataElf(abi: Int, sm: Int, sections: Seq[elfImage.Section] = Seq.empty): Array[Byte] =
    elfImage.write(190, sections, imageType = 1, abiVersion = abi, flags = if (abi >= 8) sm << 8 else sm)

  // ptxas EIATTR_CTA_PER_CLUSTER (format 4, attribute 0x3d, three u32 dimensions) and the payload-free
  // EIATTR_EXPLICIT_CLUSTER.
  private def clusterElf(x: Int, explicit: Boolean): Array[Byte] = metadataElf(
    8,
    90,
    Seq(
      elfImage.Section(".text.probe", 1),
      elfImage.Section(
        ".nv.info.probe",
        1,
        Vec.unsafeWrapArray(
          bytes.words(0x000c3d04, x, 1, 1) ++ (if (explicit) bytes.words(0x00003e01) else Array.emptyByteArray)
        )
      )
    )
  )

  test("cubin preserves per-kernel cluster requirements and rejects malformed dimensions") {
    val clustered = Cubin.decode(Vec.unsafeWrapArray(clusterElf(2, explicit = true))).value.kernels.head
    assertEquals(clustered.requiredCluster, LaunchGeometry.Dimensions(2, 1, 1))
    assert(clustered.explicitCluster)
    val ordinary = Cubin.decode(Vec.unsafeWrapArray(clusterElf(1, explicit = false))).value.kernels.head
    assertEquals(ordinary.requiredCluster, LaunchGeometry.Dimensions.One)
    assert(!ordinary.explicitCluster)
    assert(Cubin.decode(Vec.unsafeWrapArray(clusterElf(0, explicit = false))).isLeft)
  }

  test("CUDA ELF ABI 7 reads the architecture from the low flags byte") {
    assertEquals(Cubin.decode(Vec.unsafeWrapArray(metadataElf(7, 35))).map(_.sm.raw), Right(35))
  }

  test("truncated and corrupt ELF metadata fails without throwing") {
    val image = metadataElf(8, 89)
    for (size <- Vec.nonEmpty(0, 6, 63, 100, 192)) assert(Cubin.decode(Vec.unsafeWrapArray(image.take(size))).isLeft)
    val invalid = image.clone()
    LittleEndian.write(Long.MaxValue, invalid, LittleEndian.decodeInt64(invalid, 40).toInt + 64 + 24, 8)
    assert(Cubin.decode(Vec.unsafeWrapArray(invalid)).isLeft)
    val invalidAbi = image.clone()
    invalidAbi(8) = 6
    assert(Cubin.decode(Vec.unsafeWrapArray(invalidAbi)).isLeft)
  }
  test("cubin metadata rejects empty kernel names and invalid parameter sizes at construction") {
    assert(scala.compiletime.testing.typeCheckErrors("""
      import net.kurobako.gemstone.isa.sass.Cubin
      Cubin.Parameter(0, 0, 0)
    """).nonEmpty)
    assert(scala.compiletime.testing.typeCheckErrors("""
      import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
      import net.kurobako.gemstone.isa.sass.Cubin
      Cubin.Kernel("", 0, 0, 0, Vec.empty)
    """).nonEmpty)
    assert(scala.compiletime.testing.typeCheckErrors("""
      import net.kurobako.gemstone.isa.sass.Cubin
      Cubin.Parameter(0, -1, 8)
    """).nonEmpty)
  }

  private def copyAdd: Array[Byte] = Fixture.copyAddCubin.toArray

  test("embedded CUDA ELF ABI 8 exposes the kernel parameter layout") {
    val image  = copyAdd
    val module = Cubin.decode(Vec.unsafeWrapArray(image)).value
    assertEquals(module.sm, 89: Int >= 35)
    assertEquals(module.kernels.map(_.name), Vec("copy_add"))
    assertEquals(module.kernels.head.parameters.map(p => p.offset -> p.size), Vec[(Int >= 0, Int > 0)]((0, 8), (8, 8)))
    val program = CubinBackend().load(Vec.from(image)).value
    try {
      val metadata = program.kernel("copy_add").value
      assertEquals(metadata.argumentBlockSize.raw, 16)
      assert(metadata.registerCount.exists(_.raw > 0))
    } finally program.close()
  }

  test("kernel register counts use NVIDIA metadata when section-header counts are absent") {
    val image    = copyAdd
    val sections = elf.Image.parse(Vec.unsafeWrapArray(image)).value.sections
    val module   = Cubin.decode(Vec.unsafeWrapArray(image)).value
    val kernel   = module.kernels.head
    val buffer   = java.nio.ByteBuffer.wrap(image).order(java.nio.ByteOrder.LITTLE_ENDIAN)
    // CUDA libraries can leave SHI_REGISTERS unset, retaining EIATTR_REGCOUNT in .nv.info.
    val sectionTable = buffer.getLong(40).toInt
    buffer.putInt(sectionTable + kernel.section.raw * 64 + 44, module.sections(kernel.section).info & 0x00ffffff)
    val info   = module.sections.find(_.name == ".nv.info").get
    val record = sections(info.index.raw).offset.raw
    assertEquals(buffer.getShort(record) & 0xffff, 0x2f04)
    buffer.putInt(record + 8, 14)
    val program = CubinBackend().load(Vec.from(image)).value
    try assertEquals(program.kernel("copy_add").value.registerCount.map(_.raw), Some(14))
    finally program.close()
    buffer.putInt(record + 8, -1)
    assert(Cubin.decode(Vec.unsafeWrapArray(image)).isLeft)
  }

  test("stale CUDA metadata links resolve only uniquely matching kernel symbols") {
    val image        = copyAdd
    val module       = Cubin.decode(Vec.unsafeWrapArray(image)).value
    val headers      = elf.Image.parse(Vec.unsafeWrapArray(image)).value.sections
    val kernel       = module.kernels.head
    val info         = module.sections.find(_.name == ".nv.info").get
    val table        = module.sections.find(_.kind == 2).get
    val buffer       = java.nio.ByteBuffer.wrap(image).order(java.nio.ByteOrder.LITTLE_ENDIAN)
    val sectionTable = buffer.getLong(40).toInt
    // cuBLAS CUTLASS images retain matching old links in .nv.info and .text after stripping sections.
    Vec(info.index, kernel.section).foreach(index =>
      buffer.putInt(sectionTable + index.raw * 64 + 40, info.index)
    )
    val symbol = module.sections(kernel.section).info & 0x00ffffff
    buffer.putInt(sectionTable + kernel.section.raw * 64 + 44, symbol)
    val record = headers(info.index.raw).offset.raw
    buffer.putInt(record + 8, 14)
    assertEquals(Cubin.decode(Vec.unsafeWrapArray(image)).value.kernels.head.registerCount.raw, 14)

    val symbolOffset = headers(table.index).offset.raw + symbol * 24
    val originalName = buffer.getInt(symbolOffset)
    buffer.putInt(symbolOffset, originalName + 1)
    assert(Cubin.decode(Vec.unsafeWrapArray(image)).isLeft, "symbol name must match its text section")
    buffer.putInt(symbolOffset, originalName)

    buffer.putInt(sectionTable + kernel.section.raw * 64 + 44, symbol - 1)
    assert(Cubin.decode(Vec.unsafeWrapArray(image)).isLeft, "text section must refer to the same symbol index")
    buffer.putInt(sectionTable + kernel.section.raw * 64 + 44, symbol)

    val other = module.sections.find(_.name == ".debug_frame").get
    Array.copy(image, sectionTable + table.index.raw * 64, image, sectionTable + other.index.raw * 64, 64)
    assert(
      Cubin.decode(Vec.unsafeWrapArray(image)).isLeft,
      "a stale link must not choose between multiple symbol tables"
    )
  }
}
