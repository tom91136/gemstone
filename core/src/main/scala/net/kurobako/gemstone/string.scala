package net.kurobako.gemstone

import java.nio.ByteBuffer
import java.nio.charset.CharacterCodingException
import java.nio.charset.CodingErrorAction
import java.nio.charset.StandardCharsets

/** Locale-independent text operations for identifiers and other machine-readable text. */
object string {

  /** Signed decimal or hexadecimal text; whitespace and suffixes belong to the caller's grammar. */
  def parseBigIntLiteral(text: String): Option[BigInt] = {
    def unsigned(num: String): Option[BigInt] = {
      val (digits, radix) = num match {
        case s"0x$value" => (value, 16)
        case s"0X$value" => (value, 16)
        case _           => (num, 10)
      }
      Option.when(digits.nonEmpty && digits.forall(c =>
        (c >= '0' && c <= '9') ||
          (radix == 16 && ((c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F')))
      ))(BigInt(digits, radix))
    }
    text match {
      case s"+$num" => unsigned(num)
      case s"-$num" => unsigned(num).map(-_)
      case _        => unsigned(text)
    }
  }

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
