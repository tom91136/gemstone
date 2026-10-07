package net.kurobako.gemstone.codegen

import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets
import scala.collection.mutable

/** Readers for the pinned nvdisasm capture hooks and their private compressed stream framing. */
object SASSCapture {
  case class Capture(bytes: Array[Byte], detail: ujson.Obj)

  private val startMarker        = "\nSTART_MACHINE_DESCRIPTION\n"
  private val endMarker          = "\nEND_MACHINE_DESCRIPTION\n"
  private val maximumDescription = 64 * 1024 * 1024

  private def check(condition: Boolean, message: String): Unit =
    if (!condition) throw IllegalArgumentException(message)

  // Latin-1 preserves byte offsets while finding the ASCII capture delimiters.
  private def byteText(bytes: Array[Byte]): String = String(bytes, StandardCharsets.ISO_8859_1)

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def legacy(stdout: Array[Byte]): Capture = {
    val text     = byteText(stdout)
    val output   = ByteArrayOutputStream()
    val sources  = mutable.HashSet.empty[BigInt]
    var position = 0
    var records  = 0
    var next     = text.indexOf("\n<P ", position)
    while (next >= 0) {
      val between = text.substring(position, next)
      check(between.isEmpty || between == "\n", "unexpected bytes between caller records")
      val headerEnd = text.indexOf('\n', next + 1)
      check(headerEnd >= 0, "truncated memcpy record header")
      val header                             = text.substring(next + 1, headerEnd)
      def hexadecimal(value: String): BigInt = {
        check(value.nonEmpty && value.forall(c => "0123456789abcdef".contains(c)), "invalid memcpy record pointer")
        BigInt(value, 16)
      }
      val (caller, source, length) = header match {
        case s"<P 0x$pc 0x$destination 0x$src $size>" =>
          val caller = hexadecimal(pc)
          val _      = hexadecimal(destination)
          val source = hexadecimal(src)
          check(size.nonEmpty && size.forall(c => c >= '0' && c <= '9'), "invalid memcpy record size")
          val length = size.toIntOption.getOrElse(throw IllegalArgumentException("invalid memcpy record size"))
          (caller, source, length)
        case _ => throw IllegalArgumentException("invalid memcpy record header")
      }
      val payload = headerEnd + 1
      check(length <= stdout.length - payload, "truncated memcpy record")
      if (caller == BigInt(0x455203)) {
        check(length > 0, "empty memcpy caller record")
        check(length - 1 <= maximumDescription - output.size(), "description exceeds 64 MiB")
        // The caller copies len+1 bytes and overwrites the final byte at its destination.
        output.write(stdout, payload, length - 1)
        val _ = sources.add(source)
        records += 1
      }
      position = payload + length
      next = text.indexOf("\n<P ", position)
    }
    check(records > 0, "caller capture is empty")
    val bytes   = output.toByteArray
    val logical = byteText(bytes)
    check(
      logical.startsWith("ARCHITECTURE ") && logical.indexOf("ARCHITECTURE ", 1) < 0,
      "caller records do not form one table stream"
    )
    check(logical.trim.endsWith(";"), "caller stream does not end at a complete table footer")
    Capture(bytes, ujson.Obj("records" -> records, "source_pointers" -> sources.size, "callsite" -> "0x455203"))
  }

  def modern(stderr: Array[Byte]): Capture = {
    val text  = byteText(stderr)
    val start = text.indexOf(startMarker)
    val end   = text.indexOf(endMarker)
    check(
      start >= 0 && end >= start + startMarker.length &&
        text.indexOf(startMarker, start + 1) < 0 && text.indexOf(endMarker, end + 1) < 0,
      "expected one complete machine description capture"
    )
    modernStream(stderr.slice(start + startMarker.length, end))
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def modernStream(stream: Array[Byte]): Capture = {
    check(stream.length >= 8, "truncated stream header")
    val buffer   = ByteBuffer.wrap(stream).order(ByteOrder.LITTLE_ENDIAN)
    val expected = buffer.getLong(0)
    check(expected >= 8 && expected <= stream.length, "declared stream exceeds captured bytes")
    val output     = ByteArrayOutputStream()
    var dictionary = Array.emptyByteArray
    var position   = 8
    var blocks     = 0
    while (position < expected) {
      check(expected - position >= 8, "incomplete LZ4 block header")
      val rawLength        = buffer.getInt(position)
      val compressedLength = buffer.getInt(position + 4)
      check(
        rawLength > 0 &&
          rawLength <=
          (1 << 20) && compressedLength > 0 &&
          compressedLength <= rawLength + rawLength / 255 + 16 && compressedLength <= expected - position - 8,
        "invalid LZ4 block sizes"
      )
      check(rawLength <= maximumDescription - output.size(), "description exceeds 64 MiB")
      val decoded = decompress(stream, position + 8, compressedLength, rawLength, dictionary)
      output.write(decoded)
      val retained = math.min(65536, dictionary.length + decoded.length)
      val previous = math.max(0, retained - decoded.length)
      val next     = Array.ofDim[Byte](retained)
      Array.copy(dictionary, dictionary.length - previous, next, 0, previous)
      Array.copy(decoded, decoded.length - (retained - previous), next, previous, retained - previous)
      dictionary = next
      position += 8 + compressedLength
      blocks += 1
    }
    Capture(output.toByteArray, ujson.Obj("lz4_blocks" -> blocks))
  }

  // https://github.com/lz4/lz4/blob/dev/doc/lz4_Block_format.md
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def decompress(
      source: Array[Byte],
      start: Int,
      length: Int,
      rawLength: Int,
      dictionary: Array[Byte]
  ): Array[Byte] = {
    val output      = Array.ofDim[Byte](rawLength)
    val end         = start + length
    var input       = start
    var written     = 0
    var lastMatch   = -1
    var finished    = false
    def byte(): Int = {
      check(input < end, "truncated LZ4 sequence")
      val result = source(input) & 255
      input += 1
      result
    }
    def extended(base: Int): Int = {
      var result = base
      if (base == 15) {
        var extra = 255
        while (extra == 255) {
          extra = byte()
          check(extra <= rawLength - result, "LZ4 sequence exceeds declared output size")
          result += extra
        }
      }
      result
    }
    while (input < end && !finished) {
      val token    = byte()
      val literals = extended(token >>> 4)
      check(literals <= end - input && literals <= rawLength - written, "invalid LZ4 literal size")
      Array.copy(source, input, output, written, literals)
      input += literals
      written += literals
      if (input == end) {
        check(lastMatch < 0 || (literals >= 5 && rawLength - lastMatch >= 12), "invalid LZ4 final sequence")
        finished = true
      } else {
        val offset = byte() | (byte() << 8)
        check(offset > 0 && offset <= dictionary.length + written, "invalid LZ4 match offset")
        val matched = extended(token & 15) + 4
        check(matched <= rawLength - written, "LZ4 match exceeds declared output size")
        lastMatch = written
        var index = 0
        while (index < matched) {
          val from = written - offset
          output(written) = if (from < 0) dictionary(dictionary.length + from) else output(from)
          written += 1
          index += 1
        }
      }
    }
    check(finished && written == rawLength, "LZ4 decompression size mismatch")
    output
  }
}
