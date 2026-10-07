package net.kurobako.gemstone.isa.amdgpu

import fixtures.*
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import scala.util.Using

class CodeObjectIndexSuite extends munit.FunSuite {
  // Machine bytes are fixed ISA fixtures: deriving them from our encoder would hide decoder errors.

  test("executable sections can be extracted without kernel descriptors or target metadata") {
    val image = minimalCodeObject()
    val table = LittleEndian.decodeInt64(image, 40).toInt
    // ELF64 section-header fields; the fixture's .text is section 4.
    LittleEndian.write(6L, image, table + 4 * 64 + 8, 8) // SHF_ALLOC | SHF_EXECINSTR
    val descriptors     = LittleEndian.decodeInt64(image, table + 5 * 64 + 24).toInt
    val descriptorBytes = LittleEndian.decodeInt64(image, table + 5 * 64 + 32).toInt
    java.util.Arrays.fill(image, descriptors, descriptors + descriptorBytes, 0.toByte)
    val code = CodeObject.decode(Vec.from(image)).flatMap(_.executableSections).value
    assertEquals(code.map(_.name), Vec(".text"))
    assertEquals(code.head.address, 0x1000L)
    assertEquals(code.head.bytes, Vec(0x00, 0x00, 0x81, 0xbf).map(_.toByte))
    assertEquals(code.head.symbols, Vec(ExecutableSection.Symbol("kernel", 0, 4)))
    // Remove the symbol table: inspection of executable bytes must still work.
    LittleEndian.write(0L, image, table + 3 * 64 + 4, 4)
    val stripped = CodeObject.decode(Vec.from(image)).flatMap(_.executableSections).value
    assertEquals(stripped.head.bytes, code.head.bytes)
    assertEquals(stripped.head.symbols, Vec.empty[ExecutableSection.Symbol])
  }

  test("executable extraction rejects out-of-section symbols, unresolved relocations, and invalid string tables") {
    val image = minimalCodeObject(globalPointer = true)
    val table = LittleEndian.decodeInt64(image, 40).toInt
    LittleEndian.write(6L, image, table + 4 * 64 + 8, 8)
    def extract(bytes: Array[Byte]) = CodeObject.decode(Vec.from(bytes)).flatMap(_.executableSections)
    val oversized                   = image.clone()
    val symbols                     = LittleEndian.decodeInt64(image, table + 3 * 64 + 24).toInt
    LittleEndian.write(8L, oversized, symbols + 24 + 16, 8)
    assert(extract(oversized).isLeft)
    val relocated = image.clone()
    LittleEndian.write(4L, relocated, table + 8 * 64 + 44, 4)
    assert(extract(relocated).left.exists {
      case CodeObjectError.Invalid(_) => true
      case _                          => false
    })
    val dynamic     = image.clone()
    val relocations = LittleEndian.decodeInt64(image, table + 8 * 64 + 24).toInt
    LittleEndian.write(0L, dynamic, table + 8 * 64 + 44, 4)
    LittleEndian.write(0x1000L, dynamic, relocations, 8)
    assert(extract(dynamic).left.exists {
      case CodeObjectError.Invalid(_) => true
      case _                          => false
    })
    val invalidStrings = image.clone()
    LittleEndian.write(8L, invalidStrings, table + 1 * 64 + 4, 4) // SHT_NOBITS is not a string table.
    assert(extract(invalidStrings).isLeft)
  }

  test("text relocations resolve local symbols, reject undefined ones, and leave the ELF unchanged") {
    val image   = minimalCodeObject(globalPointer = true)
    val table   = LittleEndian.decodeInt64(image, 40).toInt
    val records = LittleEndian.decodeInt64(image, table + 8 * 64 + 24).toInt
    val symbols = LittleEndian.decodeInt64(image, table + 3 * 64 + 24).toInt
    // ELF64 .text is section 4, .rela is section 8, and symbol 1 is kernel at 0x1000.
    LittleEndian.write(6L, image, table + 4 * 64 + 8, 8)
    LittleEndian.write(4L, image, table + 8 * 64 + 44, 4)
    LittleEndian.write(0x1000L, image, records, 8)
    LittleEndian.write((1L << 32) | EncodingConstants.R_AMDGPU_REL32, image, records + 8, 8)
    LittleEndian.write(12L, image, records + 16, 8)
    val original   = Vec.from(image)
    val objectFile = CodeObject.decode(original).value
    assertEquals(objectFile.executableSections.map(_.head.bytes), Right(Vec[Byte](12, 0, 0, 0)))
    assertEquals(Vec.from(image), original)
    LittleEndian.write(0L, image, symbols + 24 + 6, 2) // SHN_UNDEF
    LittleEndian.write((1L << 32) | EncodingConstants.R_AMDGPU_ABS32, image, records + 8, 8)
    LittleEndian.write(0L, image, records + 16, 8)
    val external = CodeObject.decode(Vec.from(image)).value
    assert(external.executableSections.left.exists {
      case CodeObjectError.Unsupported(message) => message.contains("kernel")
      case _                                    => false
    })
    // ELF symbol zero supplies S=0, so this ABS32 relocation is just its addend.
    LittleEndian.write(EncodingConstants.R_AMDGPU_ABS32.toLong, image, records + 8, 8)
    LittleEndian.write(12L, image, records + 16, 8)
    assertEquals(
      CodeObject.decode(Vec.from(image)).flatMap(_.executableSections).map(_.head.bytes),
      Right(Vec[Byte](12, 0, 0, 0))
    )
  }

  test("ELF symbol names preserve UTF-8 while target IDs remain ASCII") {
    val image       = minimalCodeObject()
    val original    = "kernel".getBytes(StandardCharsets.US_ASCII)
    val replacement = "kérnl".getBytes(StandardCharsets.UTF_8)
    assertEquals(replacement.length, original.length)
    image.indices.filter(offset => image.slice(offset, offset + original.length).sameElements(original)).foreach {
      offset =>
        Array.copy(replacement, 0, image, offset, replacement.length)
    }
    val indexed = ELF.index(Vec.from(image)).value
    assertEquals(indexed.kernelNames.toSet[String], Set("kérnl"))
    assertEquals[String, String](indexed.kernel("kérnl").value.name, "kérnl")
    assertEquals(indexed.target, Target("gfx900"))
  }

  test("wave32-capable processors select wave width per kernel descriptor") {
    val image        = minimalCodeObject()
    val target       = "amdgcn-amd-amdhsa--gfx900".getBytes(StandardCharsets.US_ASCII)
    val targetOffset = image.indices.find(index => image.slice(index, index + target.length).sameElements(target)).get
    java.util.Arrays.fill(image, targetOffset, targetOffset + target.length, 0.toByte)
    // Independent LLVM machine codes and kernel descriptor property bit 10.
    val sections   = LittleEndian.decodeInt64(image, 40).toInt
    val descriptor = LittleEndian.decodeInt64(image, sections + 5 * 64 + 24).toInt
    for {
      machine                <- Vec(0x45L, 0x48L, 0x49L, 0x5aL)
      (properties, waveSize) <- Vec(0xe -> 64, 0x40e -> 32)
    } {
      LittleEndian.write(machine, image, 48, 4)
      LittleEndian.write(properties.toLong, image, descriptor + 56, 2)
      assertEquals(ELF.index(Vec.from(image)).flatMap(_.kernel("kernel")).map(_.waveSize.raw), Right(waveSize))
    }
  }

  test("ELF machine flags identify code objects without optional target strings") {
    val image  = minimalCodeObject()
    val target = "amdgcn-amd-amdhsa--gfx900".getBytes(StandardCharsets.US_ASCII)
    val offset = image.indices.find(index => image.slice(index, index + target.length).sameElements(target)).get
    java.util.Arrays.fill(image, offset, offset + target.length, 0.toByte)
    // Fixed LLVM ELF machine codes and feature bits exercise the generated lookup independently.
    for ((flags, name) <- Vec(0x45 -> "gfx1036", 0x32f -> "gfx906", 0x344 -> "gfx1103")) {
      LittleEndian.write(flags.toLong, image, 48, 4)
      assertEquals(ELF.index(Vec.from(image)).map(_.target.name.raw), Right(name))
    }
    LittleEndian.write(0xffL, image, 48, 4)
    assert(ELF.index(Vec.from(image)).left.exists {
      case CodeObjectError.Unsupported(message) => message.contains("machine 0xff")
      case _                                    => false
    })
  }

  test("ordinary function aliases retain both symbols and share their executable address") {
    val image   = minimalCodeObject()
    val table   = LittleEndian.decodeInt64(image, 40).toInt
    val symbols = LittleEndian.decodeInt64(image, table + 3 * 64 + 24).toInt
    // Reuse symbol 3 as another STT_FUNC at kernel's address, retaining its distinct name.
    image(symbols + 3 * 24 + 4) = 2
    LittleEndian.write(4L, image, symbols + 3 * 24 + 6, 2)
    LittleEndian.write(0x1000L, image, symbols + 3 * 24 + 8, 8)
    LittleEndian.write(4L, image, symbols + 3 * 24 + 16, 8)
    LittleEndian.write(6L, image, table + 4 * 64 + 8, 8) // SHF_ALLOC | SHF_EXECINSTR
    val bytes   = Vec.from(image)
    val section = CodeObject.decode(bytes).flatMap(_.executableSections).value.head
    assertEquals(
      section.symbols.toSet,
      Set(ExecutableSection.Symbol("kernel", 0, 4), ExecutableSection.Symbol("device_global", 0, 4))
    )
    val indexed = ELF.index(bytes).value
    assertEquals(indexed.kernelNames.toSet[String], Set("kernel"))
    assertEquals(indexed.kernel("kernel").value.functionTargets.get(0x1000L), Some("device_global"))
  }

  test("absolute and relative dynamic relocations initialise global pointers") {
    val image = minimalCodeObject(globalPointer = true)
    // HIP reaches a device global through a .got slot filled by an absolute 64-bit relocation.
    assertEquals(
      ELF.index(
        Vec.from(image)
      ).flatMap(_.kernel("kernel")).value.globalMemory.find(_._1 == 0x4000L).map(_._2.take(8).toSeq),
      Some(LittleEndian.toVec(0x3000L, 8).toSeq)
    )
    val table      = LittleEndian.decodeInt64(image, 40).toInt
    val relocation = LittleEndian.decodeInt64(image, table + 8 * 64 + 24).toInt
    LittleEndian.write(EncodingConstants.R_AMDGPU_RELATIVE64.toLong, image, relocation + 8, 8)
    LittleEndian.write(0x3008L, image, relocation + 16, 8)
    val kernel = ELF.index(Vec.from(image)).flatMap(_.kernel("kernel")).value
    assertEquals(
      kernel.globalMemory.find(_._1 == 0x4000L).map(_._2.take(8).toSeq),
      Some(LittleEndian.toVec(0x3008L, 8).toSeq)
    )
  }

  test("kernel descriptors supply COMPUTE_PGM_RSRC1") {
    val image      = minimalCodeObject()
    val sizes      = LittleEndian.toVec(16L, 4) ++ LittleEndian.toVec(8L, 4) ++ LittleEndian.toVec(24L, 4)
    val descriptor = image.indices.find(offset => image.slice(offset, offset + 12).sameElements(sizes)).get
    LittleEndian.write(0x00af0000L, image, descriptor + 48, 4)
    assertEquals(ELF.index(Vec.from(image)).flatMap(_.kernel("kernel")).value.computePgmRsrc1, 0x00af0000)
  }

  test("ELF symbol names reject malformed UTF-8 instead of replacing bytes") {
    val image    = minimalCodeObject()
    val original = "kernel".getBytes(StandardCharsets.US_ASCII)
    val offset = image.indices.find(offset => image.slice(offset, offset + original.length).sameElements(original)).get
    image(offset) = 0xff.toByte
    assertEquals(
      ELF.index(Vec.from(image)).left.map(_.message),
      Left[NonEmpty[String], ELF.Index]("invalid UTF-8 at byte offset 0")
    )
  }

  test("a real ELF index validates descriptors and materialises one shared kernel lazily") {
    val image   = minimalCodeObject()
    val indexed = ELF.index(Vec.from(image)).value
    assertEquals(indexed.target, Target("gfx900"))
    assertEquals(indexed.kernelNames.toSet[String], Set("kernel"))
    assertEquals(
      indexed.global("device_global").map { (address, size) =>
        val rawAddress: Long = address.raw
        val rawSize: Int     = size.raw
        rawAddress -> rawSize
      },
      Some(0x3000L -> 16)
    )
    assertEquals(indexed.writeGlobal("device_global", 4L, Vec[Byte](9, 8, 7, 6)), Right(()))

    // The immutable input owns a copy of the mutable source. Changing that source
    // must not affect later lazy kernel materialisation.
    java.util.Arrays.fill(image, 0.toByte)
    Using.resource(threads.Pool(4)) { pool =>
      val pending = Vec.fill(16)(pool.executor.submit(() => indexed.kernel("kernel").value))
      val kernels = pending.map(_.get(5, TimeUnit.SECONDS))
      val kernel  = kernels.head
      assert(kernels.forall(_ eq kernel), "concurrent materialisation returned distinct kernels")
      assertEquals[String, String](kernel.name, "kernel")
      assertEquals(kernel.target, Target("gfx900"))
      assertEquals(kernel.kernargSize.raw, 24)
      assertEquals(kernel.groupSegmentSize.raw, 16)
      assertEquals(kernel.privateSegmentSize.raw, 8)
      assertEquals(kernel.computePgmRsrc2, 0x80)
      assertEquals(kernel.kernelCodeProperties, 0x0a)
      assertEquals(kernel.entrySize.raw, 4)
      assertEquals(
        kernel.text.drop((kernel.entry - kernel.textAddress).toInt).take(4),
        LittleEndian.toVec(0xbf810000L, 4)
      )
      assertEquals(kernel.readOnlyAddress, 0x2000L)
      assert(kernel.readOnly.length > 64)
      assertEquals(
        kernel.globalMemory.find(_._1 == 0x3000L).map(_._2.slice(4, 8).toSeq),
        Some(Seq[Byte](9, 8, 7, 6))
      )
    }
  }

  test("code inspection preserves explicit sizes and trims inferred padding like materialisation") {
    Seq(0L, 4L, 8L).foreach { symbolSize =>
      val image   = minimalCodeObject()
      val table   = LittleEndian.decodeInt64(image, 40).toInt
      val symbols = LittleEndian.decodeInt64(image, table + 3 * 64 + 24).toInt
      LittleEndian.write(8L, image, table + 4 * 64 + 32, 8)
      LittleEndian.write(symbolSize, image, symbols + 24 + 16, 8)
      val indexed = ELF.index(Vec.unsafeWrapArray(image)).value
      val code    = indexed.kernelCode("kernel").value
      assertEquals(code.size, if (symbolSize == 8L) 8 else 4)
      assertEquals(indexed.kernelCodeSize("kernel"), Some(code.size))
      assertEquals(indexed.kernelCodeSize("missing"), None)
      val kernel = indexed.kernel("kernel").value
      val entry  = (kernel.entry - kernel.textAddress).toInt
      assertEquals(code, kernel.text.slice(entry, entry + kernel.entrySize.raw))
      assertEquals(
        indexed.kernelCode("missing").swap.toOption.map(_.message),
        indexed.kernel("missing").swap.toOption.map(_.message)
      )
    }
  }

  test("zero-sized function symbols end at the next function and are dropped at the section end") {
    val image = elfImage.amdgpu(
      bytes.words(0xbf810000, 0xbf810000, 0xbf810000, 0xbf810000),
      kernargSize = 0,
      kernelCodeProperties = 0x0a,
      extraSymbols = Seq(elfImage.Symbol("helper", 2, 4, 0x1008L, 0L), elfImage.Symbol("tail", 2, 4, 0x1010L, 0L))
    )
    val table   = LittleEndian.decodeInt64(image, 40).toInt
    val symbols = LittleEndian.decodeInt64(image, table + 3 * 64 + 24).toInt
    LittleEndian.write(0L, image, symbols + 24 + 16, 8)
    val kernel = ELF.index(Vec.unsafeWrapArray(image)).value.kernel("kernel").value
    assertEquals(kernel.entrySize.raw, 8)
    assertEquals(kernel.functionSizes, Map(0x1000L -> 8, 0x1008L -> 8))
  }

  test("function aliases resolve deterministically and reject conflicting call semantics") {
    assertEquals(
      ELF.selectFunctionTarget(Vec("alias", "_Z5logIfv")),
      Right("_Z5logIfv")
    )
    assertEquals(
      ELF.selectFunctionTarget(Vec("_Z9aliasLogIfv", "_Z5logIfv")),
      Right("_Z5logIfv")
    )
    assertEquals(
      ELF.selectFunctionTarget(Vec("_Z5logIfv", "_Z5expIfv")),
      Left(CodeObjectError.Invalid("AMDGPU function aliases have conflicting call semantics: _Z5expIfv, _Z5logIfv"))
    )
    assertEquals(ELF.selectFunctionTarget(Vec("second", "first", "second")), Right("first"))
  }

  test("code-object keys compare content and keep CRC collisions apart in the cache") {
    val first = CodeObjectKey(Vec[Byte](1, 2, 3))
    assertEquals(first, CodeObjectKey(Vec[Byte](1, 2, 3)))
    assertEquals(first.hashCode(), CodeObjectKey(Vec[Byte](1, 2, 3)).hashCode())
    assertNotEquals(first, CodeObjectKey(Vec[Byte](1, 2, 4)))
    // Distinct eight-byte images with the same CRC32, generated independently of CodeObjectKey.
    val left  = CodeObjectKey(Vec.unsafeWrapArray(bytes.hex("58fbb7a9b2680392")))
    val right = CodeObjectKey(Vec.unsafeWrapArray(bytes.hex("69176c4aa471a321")))
    assertEquals(left.hashCode(), right.hashCode())
    assertNotEquals(left, right)
    val cache = LRUCache[CodeObjectKey, String](2)
    assertEquals(cache.get(left)("left"), "left")
    assertEquals(cache.get(right)("right"), "right")
    assertEquals(cache.get(left)(fail("collision displaced the first image")), "left")
  }
}
