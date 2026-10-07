package net.kurobako.gemstone.isa.spirv.graphics

import java.nio.file.Path
import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.environment as env
import net.kurobako.gemstone.graphics.GraphicsProtocol.*
import net.kurobako.gemstone.isa.spirv.*
import net.kurobako.gemstone.isa.spirv.ImageFormats.*
import net.kurobako.gemstone.testing.*
import scala.concurrent.duration.*

class GraphicsCodecSuite extends munit.FunSuite {
  test("generated format layouts agree with the texel codecs including decoded and aspect views") {
    GraphicsFormat.values.foreach { format =>
      val decoded = format.decoded
      assertEquals(decoded.compression, GraphicsCompression.None)
      assertEquals(ImageTexelCodec.texelBytes(decoded.shaderFormat.code), decoded.texelBytes.raw, format.toString)
      if (format.compression == GraphicsCompression.None) assertEquals(decoded, format)
      else {
        val pixels = CompressedTexels.decode(format, Array.fill[Byte](format.texelBytes.raw)(0))
        // ETC/EAC blocks cover 4 x 4 texels; the decoder returns RGBA for every texel.
        assertEquals(pixels.length, 4 * 4 * 4)
        assert(ImageTexelCodec.floating(decoded.shaderFormat.code))
      }
    }
  }

  test("Scala compressed and vertex codecs match the frozen native oracle across random bit patterns") {
    val directory = Path.of(sys.env(env.NativeDirectory))
    val result    = process.run(
      command = Seq(directory.resolve("gemstone_graphics_reference").toString),
      directory = directory,
      set = Map.empty,
      unset = Set.empty,
      timeout = 30.seconds
    )
    assertEquals(result.exit, 0, result.stderr)
    def bytes(value: String): Array[Byte] = value.grouped(2).map(Integer.parseInt(_, 16).toByte).toArray
    def word(value: String): Int          = java.lang.Long.parseLong(value, 16).toInt
    val lines                             = result.stdout.linesIterator.toVector
    lines.foreach { line =>
      val fields = line.split(' ')
      if (fields(0) == "C") {
        val data   = bytes(fields(3))
        val format = GraphicsFormat.values.find(format =>
          format.compression == GraphicsCompression.fromOrdinal(fields(1).toInt) && format.srgb == (fields(2) == "1")
        ).getOrElse(fail(s"unknown compressed format: $line"))
        assertEquals(format.texelBytes.raw, data.length)
        val decoded  = CompressedTexels.decode(format, data).map(java.lang.Float.floatToRawIntBits(_))
        val expected = fields.drop(4).map(word(_))
        assertEquals(decoded.toVector, expected.toVector, line.take(60))
      } else {
        val components = fields(4).toInt
        val format     = GraphicsVertexFormat(
          encoding = GraphicsVertexEncoding.fromOrdinal(fields(1).toInt),
          widths = Vec.from(fields.slice(5, 5 + components).map(v => v.toInt.unsafe[Int > 0])),
          bytes = fields(2).toInt.unsafe[Int > 0],
          bgra = fields(3) == "1"
        )
        val actual   = VertexStage.decode(format, bytes(fields(5 + components)))
        val expected = fields.drop(6 + components).map(word(_))
        assertEquals(actual.toVector, expected.toVector, line)
      }
    }
    assertEquals(lines.count(_.startsWith("C ")), 5120)
    assert(lines.count(_.startsWith("V ")) > 10000)
  }

  test("stencil operations preserve masked bits at both arithmetic boundaries") {
    val operations = GraphicsStencilOp.values
    for {
      stored <- Vector(0, 1, 127, 254, 255)
      mask   <- Vector(0, 15, 240, 255)
    } {
      val expected =
        Vector(stored, 0, 0xab, math.min(255, stored + 1), math.max(0, stored - 1), ~stored, stored + 1, stored - 1)
      val state = GraphicsStencil(
        fail = GraphicsStencilOp.Keep,
        pass = GraphicsStencilOp.Keep,
        depthFail = GraphicsStencilOp.Keep,
        compare = GraphicsCompare.Always,
        compareMask = 255,
        writeMask = mask,
        reference = 0xab
      )
      operations.zipWithIndex.foreach { (operation, index) =>
        assertEquals(
          FragmentOperations.stencil(stored, state, operation),
          ((stored & ~mask) | (expected(index) & mask)) & 255
        )
      }
    }
  }
}
