package net.kurobako.gemstone.isa.sass

import java.nio.ByteBuffer
import java.nio.ByteOrder
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

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

  test("CUDA ELF ABI 7 reads the architecture from the low flags byte") {
    assertEquals(Cubin.decode(metadataElf(7, 35)).map(_.sm.raw), Right(35))
  }

  test("truncated and corrupt ELF metadata fails without throwing") {
    val image = metadataElf(8, 89)
    for (size <- Vec(0, 6, 63, 100, 192)) assert(Cubin.decode(image.take(size)).isLeft)
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
