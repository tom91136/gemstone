package net.kurobako.gemstone.isa.amdgpu

import fixtures.*
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.testing.*
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
    val code = CodeObject.decode(Vec.from(image)).flatMap(_.executableSections).toOption.get
    assertEquals(code.map(_.name), Vec(".text"))
    assertEquals(code.head.address, 0x1000L)
    assertEquals(code.head.bytes, Vec(0x00, 0x00, 0x81, 0xbf).map(_.toByte))
    assertEquals(code.head.symbols, Vec(ExecutableSection.Symbol("kernel", 0, 4)))
    // Remove the symbol table: inspection of executable bytes must still work.
    LittleEndian.write(0L, image, table + 3 * 64 + 4, 4)
    val stripped = CodeObject.decode(Vec.from(image)).flatMap(_.executableSections).toOption.get
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

  test("text relocations resolve local and supplied external symbols without mutating the ELF") {
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
    val objectFile = CodeObject.decode(original).toOption.get
    assertEquals(objectFile.executableSections.map(_.head.bytes), Right(Vec[Byte](12, 0, 0, 0)))
    assertEquals(Vec.from(image), original)
    LittleEndian.write(0L, image, symbols + 24 + 6, 2) // SHN_UNDEF
    LittleEndian.write((1L << 32) | EncodingConstants.R_AMDGPU_ABS32, image, records + 8, 8)
    LittleEndian.write(0L, image, records + 16, 8)
    val external = CodeObject.decode(Vec.from(image)).toOption.get
    assert(external.executableSections.left.exists {
      case CodeObjectError.Unsupported(message) => message.contains("kernel")
      case _                                    => false
    })
    assertEquals(
      external.relocatedSections(Map("kernel" -> 0xbf810000L), Map.empty).map(_.head.bytes),
      Right(Vec(0x00, 0x00, 0x81, 0xbf).map(_.toByte))
    )
    assert(external.relocatedSections(Map("kernel" -> 0x100000000L), Map.empty).isLeft)
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
    val indexed = ELF.index(Vec.from(image)).toOption.get
    assertEquals(indexed.kernelNames, Set("kérnl"))
    assertEquals(indexed.kernel("kérnl").toOption.get.name, "kérnl")
    assertEquals(indexed.target, Target("gfx900"))
  }

  test("ELF symbol names reject malformed UTF-8 instead of replacing bytes") {
    val image    = minimalCodeObject()
    val original = "kernel".getBytes(StandardCharsets.US_ASCII)
    val offset = image.indices.find(offset => image.slice(offset, offset + original.length).sameElements(original)).get
    image(offset) = 0xff.toByte
    assertEquals(
      ELF.index(Vec.from(image)).left.map(_.message),
      Left[NonEmpty[String], ELF.Index]("ELF string is not valid UTF-8")
    )
  }

  test("a real ELF index validates descriptors and materializes one shared kernel lazily") {
    val image   = minimalCodeObject()
    val indexed = ELF.index(Vec.from(image)).toOption.get
    assertEquals(indexed.target, Target("gfx900"))
    assertEquals(indexed.kernelNames, Set("kernel"))
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
    // must not affect later lazy kernel materialization.
    java.util.Arrays.fill(image, 0.toByte)
    Using.resource(threads.Pool(4)) { pool =>
      val pending = Vec.fill(16)(pool.executor.submit(() => indexed.kernel("kernel").toOption.get))
      val kernels = pending.map(_.get(5, TimeUnit.SECONDS))
      val kernel  = kernels.head
      assert(kernels.forall(_ eq kernel), "concurrent materialization returned distinct kernels")
      assertEquals(kernel.name, "kernel")
      assertEquals(kernel.target, Target("gfx900"))
      assertEquals(kernel.kernargSize, 24)
      assertEquals(kernel.groupSegmentSize, 16)
      assertEquals(kernel.privateSegmentSize, 8)
      assertEquals(kernel.computePgmRsrc2, 0x80)
      assertEquals(kernel.kernelCodeProperties, 0x0a)
      assertEquals(kernel.code, LittleEndian.toVec(0xbf810000L, 4))
      assertEquals(kernel.readOnlyAddress, 0x2000L)
      assert(kernel.readOnly.length > 64)
      assertEquals(
        kernel.globalMemory.find(_._1 == 0x3000L).map(_._2.slice(4, 8).toSeq),
        Some(Seq[Byte](9, 8, 7, 6))
      )
    }
  }

  test("identical ELF indexes isolate mutable globals across cached views") {
    val first  = ELF.index(Vec.unsafeWrapArray(minimalCodeObject())).toOption.get
    val second = ELF.index(Vec.unsafeWrapArray(minimalCodeObject())).toOption.get
    assertEquals(first.writeGlobal("device_global", 4L, Vec[Byte](9, 8, 7, 6)), Right(()))
    assertEquals(
      second.kernel("kernel").toOption.get.globalMemory.find(_._1 == 0x3000L).map { case (_, bytes) =>
        bytes.slice(4, 8).toSeq
      },
      Some(Seq[Byte](0, 0, 0, 0))
    )
  }

  test("linked code objects infer zero-sized kernel symbols from the next function") {
    assertEquals(ELF.inferredFunctionSize(0x1000L, 0x1400L, Seq(0x1000L, 0x1180L, 0x1300L)), Some(0x180))
    assertEquals(ELF.inferredFunctionSize(0x1300L, 0x1400L, Seq(0x1000L, 0x1180L, 0x1300L)), Some(0x100))
    assertEquals(ELF.inferredFunctionSize(0x1400L, 0x1400L, Seq.empty), None)
  }

  test("inferred linked functions discard alignment padding after their final end-program") {
    val code = Array[Byte](
      0x00,
      0x00,
      0x81.toByte,
      0xbf.toByte,
      0x00,
      0x00,
      0x00,
      0x00
    )
    assertEquals(
      ELF.trimTrailingKernelPadding(code).toSeq,
      code.take(4).toSeq
    )
  }

}
