package net.kurobako.gemstone.codegen

import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.collection.*

class SASSCaptureSuite extends munit.FunSuite {
  private def ascii(value: String): Array[Byte]                             = value.getBytes(StandardCharsets.US_ASCII)
  private def text(value: Array[Byte]): String                              = String(value, StandardCharsets.US_ASCII)
  private def assertBytes(actual: Array[Byte], expected: Array[Byte]): Unit =
    assertEquals(Vec.from(actual), Vec.from(expected))

  private def frame(blocks: (Int, Array[Byte])*): Array[Byte] = {
    val size   = 8 + blocks.map(_._2.length + 8).sum
    val buffer = ByteBuffer.allocate(size).order(ByteOrder.LITTLE_ENDIAN)
    buffer.putLong(size.toLong)
    blocks.foreach { (rawLength, compressed) =>
      buffer.putInt(rawLength)
      buffer.putInt(compressed.length)
      buffer.put(compressed)
    }
    buffer.array()
  }

  private def literals(bytes: Array[Byte]): Array[Byte] = {
    val output = ByteArrayOutputStream()
    output.write(math.min(15, bytes.length) << 4)
    if (bytes.length >= 15) {
      val remaining = bytes.length - 15
      (0 until remaining / 255).foreach(_ => output.write(255))
      output.write(remaining % 255)
    }
    output.write(bytes)
    output.toByteArray
  }

  private def record(pc: Int, destination: Int, source: Int, payload: Array[Byte]): Array[Byte] =
    ascii(s"\n<P 0x${pc.toHexString} 0x${destination.toHexString} 0x${source.toHexString} ${payload.length}>\n") ++
      payload

  test("legacy capture keeps conditional records from distinct source pointers") {
    val first     = ascii("ARCHITECTURE \"Kepler\"\nCLASS \"F2I\"\n##IF\n\u0000")
    val branch    = ascii(" DEFINED(ARCH_SEMANTICS)\n FORMAT Register:Rd;\n##ENDIF\nEND;\n\u0000")
    val unrelated = ascii("not part of the description\u0000")
    val capture   = SASSCapture.legacy(
      record(0x455203, 0x1000, 0x2000, first) ++
        record(0x455c7c, 0x3000, 0x1000, unrelated) ++
        record(0x455203, 0x4000, 0x5000, branch)
    )
    assertBytes(capture.bytes, first.dropRight(1) ++ branch.dropRight(1))
    assertEquals(capture.detail, ujson.Obj("records" -> 2, "source_pointers" -> 2, "callsite" -> "0x455203"))
  }

  test("legacy capture drops exactly the caller's final byte and permits trailing disassembly") {
    val capture = SASSCapture.legacy(record(0x455203, 1, 2, ascii("ARCHITECTURE Test\nEND;\nX")) ++ ascii("listing"))
    assertEquals(text(capture.bytes), "ARCHITECTURE Test\nEND;\n")
  }

  test("legacy capture rejects missing records, truncation, unexpected gaps and incomplete descriptions") {
    val good = record(0x455203, 1, 2, ascii("ARCHITECTURE Test\nEND;\u0000"))
    Vec(
      Array.emptyByteArray,
      good.dropRight(1),
      good ++ ascii("unexpected") ++ good,
      record(0x455203, 1, 2, ascii("ARCHITECTURE Test\nEND\u0000")),
      good ++ good,
      ascii("\n<P 0x455203 0x1 0x2 999999999999999>\n"),
      ascii("\n<P 0x455203 0x1 0x2 1>")
    ).foreach(bytes => intercept[IllegalArgumentException](SASSCapture.legacy(bytes)))
  }

  test("modern stream decodes literal blocks and ignores captured allocator padding") {
    val bytes   = ascii("abc")
    val capture = SASSCapture.modernStream(frame(bytes.length -> literals(bytes)) ++ Array.fill[Byte](7)(-1))
    assertEquals(text(capture.bytes), "abc")
    assertEquals(capture.detail, ujson.Obj("lz4_blocks" -> 1))
    val long = Array.tabulate[Byte](600)(index => (index % 127).toByte)
    assertBytes(SASSCapture.modernStream(frame(long.length -> literals(long))).bytes, long)
  }

  test("modern LZ4 match copies overlap within the current block") {
    // One literal 'a', a 20-byte match at distance one, then five terminal literals.
    val compressed = Array[Byte](0x1f, 'a'.toByte, 1, 0, 1, 0x50) ++ ascii("bcdef")
    assertEquals(text(SASSCapture.modernStream(frame(26 -> compressed)).bytes), "a" * 21 + "bcdef")
  }

  test("modern LZ4 dictionary matches can continue into bytes produced by the current block") {
    val first   = ascii("abcdefghijklmnop")
    val second  = Array[Byte](0x0f, 4, 0, 1, 0x50) ++ ascii("12345")
    val capture = SASSCapture.modernStream(frame(first.length -> literals(first), 25 -> second))
    assertEquals(text(capture.bytes), "abcdefghijklmnop" + "mnop" * 5 + "12345")
    assertEquals(capture.detail("lz4_blocks").num, 2.0)
  }

  test("modern LZ4 retains the last 64 KiB across multiple blocks") {
    val first    = Array.tabulate[Byte](70000)(index => (index % 251).toByte)
    val middle   = ascii("middle")
    val second   = Array[Byte](0x0f, -1, -1, 1, 0x50) ++ ascii("12345")
    val expected = first ++ middle ++ (first ++ middle).takeRight(65535).take(20) ++ ascii("12345")
    val capture  = SASSCapture.modernStream(frame(
      first.length  -> literals(first),
      middle.length -> literals(middle),
      25            -> second
    ))
    assertBytes(capture.bytes, expected)
  }

  test("modern markers allow surrounding diagnostics but must be unique and correctly ordered") {
    val start  = ascii("\nSTART_MACHINE_DESCRIPTION\n")
    val end    = ascii("\nEND_MACHINE_DESCRIPTION\n")
    val stream = frame(3 -> literals(ascii("abc")))
    assertEquals(text(SASSCapture.modern(ascii("before") ++ start ++ stream ++ end ++ ascii("after")).bytes), "abc")
    Vec(
      start ++ stream,
      stream ++ end,
      end ++ start ++ stream,
      start ++ stream ++ end ++ end,
      start ++ start ++ stream ++ end
    ).foreach(bytes => intercept[IllegalArgumentException](SASSCapture.modern(bytes)))
  }

  test("modern stream rejects incomplete headers and invalid declared sizes") {
    def header(size: Long): Array[Byte] = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(size).array()
    val missingBlock                    = header(16) ++ Array.fill[Byte](8)(0)
    val excessive                       = missingBlock.clone()
    ByteBuffer.wrap(excessive).order(ByteOrder.LITTLE_ENDIAN).putInt(8, (1 << 20) + 1)
    Vec(
      ascii("short"),
      header(99),
      header(7),
      header(-1),
      header(15) ++ Array.fill[Byte](7)(0),
      missingBlock,
      excessive,
      frame(3 -> literals(ascii("abc"))).dropRight(1),
      frame(1 -> Array.fill[Byte](18)(0))
    ).foreach(bytes => intercept[IllegalArgumentException](SASSCapture.modernStream(bytes)))
  }

  test("modern LZ4 rejects bad offsets, truncated sequences and output overruns") {
    Vec(
      20 -> Array[Byte](0x00, 0, 0),                                          // zero offset
      20 -> Array[Byte](0x00, 1, 0),                                          // no dictionary or output yet
      20 -> Array[Byte](0x00, 1),                                             // truncated offset
      20 -> Array[Byte](0xf0.toByte),                                         // truncated literal length
      20 -> Array[Byte](0xf0.toByte, -1),                                     // overflowing literal length
      20 -> Array[Byte](0x50, 1, 2),                                          // truncated literal payload
      5  -> literals(ascii("abcdef")),                                        // literal output overrun
      10 -> (Array[Byte](0x1f, 'a'.toByte, 1, 0, 1, 0x50) ++ ascii("bcdef")), // match output overrun
      8  -> (Array[Byte](0x13, 'a'.toByte, 1, 0)),                            // missing terminal literal sequence
      4  -> literals(ascii("abc"))                                            // short decompressed output
    ).foreach { (size, compressed) =>
      intercept[IllegalArgumentException](SASSCapture.modernStream(frame(size -> compressed)))
    }
  }
}
