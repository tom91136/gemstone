package net.kurobako.gemstone.isa.spirv.graphics

import java.nio.file.Path
import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.environment as env
import net.kurobako.gemstone.graphics.GraphicsProtocol.*
import net.kurobako.gemstone.testing.*
import scala.concurrent.duration.*

class GraphicsCodecOracleSuite extends munit.FunSuite {
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
}
