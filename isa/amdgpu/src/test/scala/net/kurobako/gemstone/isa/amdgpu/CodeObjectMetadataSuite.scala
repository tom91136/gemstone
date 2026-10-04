package net.kurobako.gemstone.isa.amdgpu

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*

class CodeObjectMetadataSuite extends munit.FunSuite {
  test("decode the server-owned AMDGPU launch manifest") {
    val result = CodeObjectMetadata.decode(Vec.unsafeWrapArray(codeObject(hidden = false))).toOption.get

    assertEquals(result.size, 1)
    val kernel = result.head
    assertEquals(kernel.name, "add")
    assertEquals(kernel.symbol, "add.kd")
    assertEquals(kernel.metadata.argumentBlockSize.raw, 20)
    assertEquals(kernel.metadata.explicitParameterCount.raw, 3)
    assertEquals(kernel.hiddenArguments, Vec.empty[CodeObjectMetadata.HiddenArgument])
    assertEquals(
      kernel.metadata.parameters.map(parameter =>
        (parameter.offset.raw, parameter.width.raw, parameter.kind, parameter.containsPointers)
      ),
      Vec(
        (0, 8, KernelMetadata.ParameterKind.Allocation, false),
        (8, 8, KernelMetadata.ParameterKind.Allocation, false),
        (16, 4, KernelMetadata.ParameterKind.Scalar, true)
      )
    )
  }

  test("exclude hidden AMDGPU fields from the explicit parameter count") {
    val manifest = CodeObjectMetadata.decode(Vec.unsafeWrapArray(codeObject(hidden = true))).toOption.get.head
    val metadata = manifest.metadata

    assertEquals(metadata.argumentBlockSize.raw, 24)
    assertEquals(metadata.parameters.size, 4)
    assertEquals(metadata.explicitParameterCount.raw, 3)
    assertEquals(
      manifest.hiddenArguments,
      Vec(CodeObjectMetadata.HiddenArgument("hidden_block_count_x", 20, 4))
    )
  }

  test("reject uint64 metadata sizes outside the supported signed range") {
    val oversized =
      Array(0xcf.toByte, 0x80.toByte, 0.toByte, 0.toByte, 0.toByte, 0.toByte, 0.toByte, 0.toByte, 0.toByte)
    val result = CodeObjectMetadata.decode(Vec.from(codeObject(hidden = false, blockSize = Some(oversized))))
    assert(result.isLeft)
    assert(result.swap.toOption.get.message.contains("exceeds supported range"))
  }

  test("kernels without optional argument descriptions retain their raw argument block") {
    val manifest =
      CodeObjectMetadata.decode(Vec.from(codeObject(hidden = false, omitArguments = true))).toOption.get.head
    assertEquals(manifest.metadata.argumentBlockSize.raw, 20)
    assertEquals(manifest.metadata.parameters, Vec.empty[KernelMetadata.Parameter])
    assertEquals(manifest.metadata.explicitParameterCount.raw, 0)
    assertEquals(manifest.hiddenArguments, Vec.empty[CodeObjectMetadata.HiddenArgument])
  }

  test("large ELF note sections preserve metadata and reject a truncated trailing note") {
    val image = Vec.from(codeObject(hidden = false))
    val notes = ELF.notes(image).toOption.get
    // Twelve zero bytes encode an empty note; vendor sections can contain thousands of these.
    val padding = Vec.fill[Byte](120000)(0)
    val padded  = padding ++ notes.head ++ padding
    assertEquals(CodeObjectMetadata.decodeNotes(Vec(padded)), CodeObjectMetadata.decodeOptional(image))
    val truncatedHeader = Vec.from(Array[Byte](4, 0, 0, 0, 8, 0, 0, 0, 32, 0, 0, 0))
    val result          = CodeObjectMetadata.decodeNotes(Vec(padded ++ truncatedHeader))
    assert(result.isLeft)
    assertEquals(result.swap.toOption.get.message.raw, "truncated ELF note")
  }

  private def codeObject(
      hidden: Boolean,
      blockSize: Option[Array[Byte]] = None,
      omitArguments: Boolean = false
  ): Array[Byte] = {
    val arguments = Seq(
      argument(0, 8, "global_buffer"),
      argument(8, 8, "global_buffer"),
      argument(16, 4, "by_value")
    ) ++ Option.when(hidden)(argument(20, 4, "hidden_block_count_x"))
    val metadata = concat(
      Array(0x81.toByte),
      string("amdhsa.kernels"),
      Array(0x91.toByte, (if (omitArguments) 0x83 else 0x84).toByte),
      if (omitArguments) Array.emptyByteArray
      else concat(string(".args"), Array((0x90 | arguments.size).toByte), concat(arguments*)),
      string(".kernarg_segment_size"),
      blockSize.getOrElse(unsigned(if (hidden) 24 else 20)),
      string(".name"),
      string("add"),
      string(".symbol"),
      string("add.kd")
    )
    val noteOffset        = 64
    val paddedDescription = (metadata.length + 3) & ~3
    val noteSize          = 12 + 8 + paddedDescription
    val sectionOffset     = noteOffset + noteSize
    val image             = Array.ofDim[Byte](sectionOffset + 2 * 64)
    val buffer            = ByteBuffer.wrap(image).order(ByteOrder.LITTLE_ENDIAN)

    buffer.put(0, 0x7f.toByte)
    buffer.put(1, 'E'.toByte)
    buffer.put(2, 'L'.toByte)
    buffer.put(3, 'F'.toByte)
    buffer.put(4, 2.toByte)
    buffer.put(5, 1.toByte)
    buffer.putShort(16, 3.toShort)
    buffer.putShort(18, 224.toShort)
    buffer.putInt(20, 1)
    buffer.putLong(40, sectionOffset.toLong)
    buffer.putShort(52, 64.toShort)
    buffer.putShort(58, 64.toShort)
    buffer.putShort(60, 2.toShort)
    buffer.putInt(noteOffset, 7)
    buffer.putInt(noteOffset + 4, metadata.length)
    buffer.putInt(noteOffset + 8, 32)
    Array.copy("AMDGPU\u0000".getBytes(StandardCharsets.US_ASCII), 0, image, noteOffset + 12, 7)
    Array.copy(metadata, 0, image, noteOffset + 20, metadata.length)
    val noteSection = sectionOffset + 64
    buffer.putInt(noteSection + 4, 7)
    buffer.putLong(noteSection + 24, noteOffset.toLong)
    buffer.putLong(noteSection + 32, noteSize.toLong)
    image
  }

  private def argument(offset: Int, size: Int, kind: String): Array[Byte] = concat(
    Array(0x83.toByte),
    string(".offset"),
    unsigned(offset),
    string(".size"),
    unsigned(size),
    string(".value_kind"),
    string(kind)
  )

  private def string(value: String): Array[Byte] = {
    val bytes = value.getBytes(StandardCharsets.UTF_8)
    assert(bytes.length < 32)
    Array((0xa0 | bytes.length).toByte) ++ bytes
  }

  private def unsigned(value: Int): Array[Byte] =
    if (value < 128) Array(value.toByte)
    else Array(0xce.toByte, (value >>> 24).toByte, (value >>> 16).toByte, (value >>> 8).toByte, value.toByte)

  private def concat(parts: Array[Byte]*): Array[Byte] = parts.view.flatten.toArray
}
