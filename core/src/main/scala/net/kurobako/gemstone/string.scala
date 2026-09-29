package net.kurobako.gemstone

import java.nio.ByteBuffer
import java.nio.charset.CharacterCodingException
import java.nio.charset.CodingErrorAction
import java.nio.charset.StandardCharsets

/** Locale-independent text operations for identifiers and other machine-readable text. */
object string {

  def formatRoot(pattern: String, arguments: AnyRef*): String = PlatformString.format(pattern, arguments*)

  /** Decode an encoded name without silently replacing malformed bytes. */
  def decodeUtf8(bytes: Array[Byte], offset: Int, length: Int): Either[CharacterCodingException, String] =
    try Right(StandardCharsets.UTF_8.newDecoder()
        .onMalformedInput(CodingErrorAction.REPORT)
        .onUnmappableCharacter(CodingErrorAction.REPORT)
        .decode(ByteBuffer.wrap(bytes, offset, length)).toString)
    catch { case error: CharacterCodingException => Left(error) }

  extension (value: String) {
    def toUpperCaseRoot: String = PlatformString.upper(value)

    def toLowerCaseRoot: String = PlatformString.lower(value)
  }
}
