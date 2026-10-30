package net.kurobako.gemstone.isa.spirv.graphics

import net.kurobako.gemstone.graphics.GraphicsProtocol.*
import net.kurobako.gemstone.isa.spirv.*
import net.kurobako.gemstone.isa.spirv.ImageFormats.*
import net.kurobako.gemstone.testing.Suite

class GraphicsCodecSuite extends Suite {
  test("generated format layouts agree with the texel codecs including decoded and aspect views") {
    GraphicsFormat.values.foreach { format =>
      val decoded = format.decoded
      assertEquals(decoded.compression, GraphicsCompression.None)
      assertEquals(ImageTexelCodec.texelBytes(decoded.shaderFormat.code), decoded.texelBytes.raw, format.toString)
      if (format.compression == GraphicsCompression.None) assertEquals(decoded, format)
      else {
        val pixels = CompressedTexels.decode(format, Array.fill[Byte](format.texelBytes)(0))
        // ETC/EAC blocks cover 4 x 4 texels; the decoder returns RGBA for every texel.
        assertEquals(pixels.length, 4 * 4 * 4)
        assert(ImageTexelCodec.floating(decoded.shaderFormat.code))
      }
    }
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
