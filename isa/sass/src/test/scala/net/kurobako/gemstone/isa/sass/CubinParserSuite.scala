package net.kurobako.gemstone.isa.sass

import java.nio.ByteBuffer
import java.nio.ByteOrder
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*

class CubinParserSuite extends munit.FunSuite {
  private def metadataElf(abi: Int, sm: Int): Array[Byte] = {
    val bytes = Array.ofDim[Byte](193)
    bytes(0) = 0x7f.toByte
    bytes(1) = 'E'.toByte
    bytes(2) = 'L'.toByte
    bytes(3) = 'F'.toByte
    bytes(4) = 2
    bytes(5) = 1
    bytes(8) = abi.toByte
    val header = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
    header.putShort(16, 1)
    header.putShort(18, 190)
    header.putLong(40, 64L)
    header.putInt(48, if (abi >= 8) sm << 8 else sm)
    header.putShort(58, 64.toShort)
    header.putShort(60, 2.toShort)
    header.putShort(62, 1.toShort)
    header.putInt(64 + 64 + 4, 3)
    header.putLong(64 + 64 + 24, 192L)
    header.putLong(64 + 64 + 32, 1L)
    bytes
  }

  private def clusterElf(x: Int, explicit: Boolean): Array[Byte] = {
    val names   = "\u0000.text.probe\u0000.nv.info.probe\u0000".getBytes(java.nio.charset.StandardCharsets.US_ASCII)
    val payload = 320 + names.length
    val bytes   = java.util.Arrays.copyOf(metadataElf(8, 90), payload + 20)
    val buffer  = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
    buffer.putShort(60, 4.toShort)
    buffer.putLong(152, 320L)
    buffer.putLong(160, names.length.toLong)
    Array.copy(names, 0, bytes, 320, names.length)
    buffer.putInt(192, 1)
    buffer.putInt(196, 1)
    buffer.putInt(256, 13)
    buffer.putInt(260, 1)
    buffer.putLong(280, payload.toLong)
    buffer.putLong(288, if (explicit) 20L else 16L)
    // ptxas EIATTR_CTA_PER_CLUSTER: format 4, attribute 0x3d, three u32 dimensions.
    buffer.putInt(payload, 0x000c3d04)
    buffer.putInt(payload + 4, x)
    buffer.putInt(payload + 8, 1)
    buffer.putInt(payload + 12, 1)
    // EIATTR_EXPLICIT_CLUSTER has no payload.
    buffer.putInt(payload + 16, 0x00003e01)
    bytes
  }

  test("cubin preserves per-kernel cluster requirements and rejects malformed dimensions") {
    val clustered = Cubin.decode(clusterElf(2, explicit = true)).fold(fail(_), identity).kernels.head
    assertEquals(clustered.requiredCluster, LaunchGeometry.Dimensions(2, 1, 1))
    assert(clustered.explicitCluster)
    val ordinary = Cubin.decode(clusterElf(1, explicit = false)).fold(fail(_), identity).kernels.head
    assertEquals(ordinary.requiredCluster, LaunchGeometry.Dimensions.One)
    assert(!ordinary.explicitCluster)
    assert(Cubin.decode(clusterElf(0, explicit = false)).isLeft)
  }

  test("CUDA ELF ABI 7 reads the architecture from the low flags byte") {
    assertEquals(Cubin.decode(metadataElf(7, 35)).map(_.sm.raw), Right(35))
  }

  test("truncated and corrupt ELF metadata fails without throwing") {
    val image = metadataElf(8, 89)
    for (size <- Vec.nonEmpty(0, 6, 63, 100, 192)) assert(Cubin.decode(image.take(size)).isLeft)
    val invalid = image.clone()
    ByteBuffer.wrap(invalid).order(ByteOrder.LITTLE_ENDIAN).putLong(64 + 64 + 24, Long.MaxValue)
    assert(Cubin.decode(invalid).isLeft)
    val invalidAbi = image.clone()
    invalidAbi(8) = 6
    assert(Cubin.decode(invalidAbi).isLeft)
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

}
