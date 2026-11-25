package net.kurobako.gemstone.isa.sass

import java.nio.ByteBuffer
import java.nio.ByteOrder
import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*

class CubinParserSuite extends Suite {
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

  // ptxas EIATTR_MAX_THREADS (format 4, attribute 0x05, three u32 dimensions).
  private def maxThreadsElf(x: Int, y: Int, z: Int): Array[Byte] = metadataElf(
    8,
    89,
    Seq(
      elfImage.Section(".text.probe", 1),
      elfImage.Section(".nv.info.probe", 1, Vec.unsafeWrapArray(bytes.words(0x000c0504, x, y, z)))
    )
  )

  test("cubin reads the per-kernel maximum thread count and rejects empty dimensions") {
    val bounded = Cubin.decode(Vec.unsafeWrapArray(maxThreadsElf(32, 4, 2))).value.kernels.head
    assertEquals(bounded.maxThreadsPerBlock.map(_.raw), Some(256))
    val open = Cubin.decode(Vec.unsafeWrapArray(clusterElf(1, explicit = false))).value.kernels.head
    assertEquals(open.maxThreadsPerBlock, None)
    assert(Cubin.decode(Vec.unsafeWrapArray(maxThreadsElf(0, 1, 1))).isLeft)
  }

  test("a ptxas cubin states __launch_bounds__ as the thread bound") {
    val module = Cubin.decode(Fixture.launchBoundCubin).value
    assertEquals(module.kernels.find(_.name == "bounded").get.maxThreadsPerBlock.map(_.raw), Some(256))
    assertEquals(module.kernels.find(_.name == "open").get.maxThreadsPerBlock, None)
    val program = CubinBackend(text = Harness.text).load(Fixture.launchBoundCubin).value
    try {
      assertEquals(program.kernel("bounded").value.maxThreadsPerWorkGroup.map(_.raw), Some(256))
      assertEquals(program.kernel("open").value.maxThreadsPerWorkGroup, None)
    } finally program.close()
  }

  test("a reserved-smem cubin reports the kernel's static size without the per-block reserve") {
    val reserved = Cubin.decode(Vec.unsafeWrapArray(bytes.hex(ReservedSharedFixtures.sm90WithReserve))).value
    assertEquals(reserved.sm.raw, 90)
    assertEquals(reserved.kernels.head.sharedSizeBytes.raw, 400)
    val plain = Cubin.decode(Vec.unsafeWrapArray(bytes.hex(ReservedSharedFixtures.sm89WithoutReserve))).value
    assertEquals(plain.sm.raw, 89)
    assertEquals(plain.kernels.head.sharedSizeBytes.raw, 400)
    val capped = Cubin.decode(Vec.unsafeWrapArray(bytes.hex(ReservedSharedFixtures.sm100WithCap))).value
    assertEquals(capped.sm.raw, 100)
    assertEquals(capped.kernels.head.sharedSizeBytes.raw, 400)
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
    val program = CubinBackend(text = Harness.text).load(Vec.from(image)).value
    try {
      val metadata = program.kernel("copy_add").value
      assertEquals(metadata.argumentBlockSize.raw, 16)
      assert(metadata.registerCount.exists(_.raw > 0))
    } finally program.close()
  }

  test("cubin states per-kernel shared and local sizes and a module-wide constant size") {
    val image  = Fixture.functionAttributesCubin.toArray
    val module = Cubin.decode(Vec.unsafeWrapArray(image)).value
    val all    = module.kernels.find(_.name == "k_all").get
    val plain  = module.kernels.find(_.name == "k_plain").get
    assertEquals((all.sharedSizeBytes.raw, all.localSizeBytes.raw, all.registerCount.raw), (400, 1024, 12))
    assertEquals((plain.sharedSizeBytes.raw, plain.localSizeBytes.raw, plain.registerCount.raw), (0, 0, 8))
    assertEquals(Cubin.constSizeBytes(module).raw, 80)
    val program = CubinBackend(text = Harness.text).load(Vec.from(image)).value
    try {
      val metadata = program.kernel("k_all").value
      assertEquals(metadata.sharedSizeBytes.map(_.raw), Some(400))
      assertEquals(metadata.localSizeBytes.map(_.raw), Some(1024))
      assertEquals(metadata.constSizeBytes.map(_.raw), Some(80))
      assertEquals(program.kernel("k_plain").value.constSizeBytes.map(_.raw), Some(80))
    } finally program.close()
  }

  test("kernel register counts use NVIDIA metadata when section-header counts are absent") {
    val image    = copyAdd
    val sections = elf.Image.parse(Vec.unsafeWrapArray(image)).value.sections
    val module   = Cubin.decode(Vec.unsafeWrapArray(image)).value
    val kernel   = module.kernels.head
    val buffer   = ByteBuffer.wrap(image).order(ByteOrder.LITTLE_ENDIAN)
    // CUDA libraries can leave SHI_REGISTERS unset, retaining EIATTR_REGCOUNT in .nv.info.
    val sectionTable = buffer.getLong(40).toInt
    buffer.putInt(sectionTable + kernel.section.raw * 64 + 44, module.sections(kernel.section).info & 0x00ffffff)
    val info   = module.sections.find(_.name == ".nv.info").get
    val record = sections(info.index.raw).offset.raw
    assertEquals(buffer.getShort(record) & 0xffff, 0x2f04)
    buffer.putInt(record + 8, 14)
    val program = CubinBackend(text = Harness.text).load(Vec.from(image)).value
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
    val buffer       = ByteBuffer.wrap(image).order(ByteOrder.LITTLE_ENDIAN)
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
