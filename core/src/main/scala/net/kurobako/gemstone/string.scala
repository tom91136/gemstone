package net.kurobako.gemstone

import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.CharacterCodingException
import java.nio.charset.CodingErrorAction
import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import scala.annotation.targetName

/** Locale-independent text operations for identifiers and other machine-readable text. */
object string {

  /** Strict UTF-8 conversion for text carried in byte-oriented formats. Offsets are byte offsets. */
  object UTF8 {
    case class Error(
        offset: Int >= 0,
        message: NonEmpty[String],
        private[string] cause: CharacterCodingException
    )

    def encode(value: String): Either[Error, Vec[Byte]] = {
      val input = CharBuffer.wrap(value)
      try {
        val encoded = StandardCharsets.UTF_8.newEncoder()
          .onMalformedInput(CodingErrorAction.REPORT)
          .onUnmappableCharacter(CodingErrorAction.REPORT)
          .encode(input)
        val bytes = Array.ofDim[Byte](encoded.remaining())
        encoded.get(bytes)
        Right(Vec.from(bytes))
      } catch {
        case error: CharacterCodingException =>
          val byteOffset = value.substring(0, input.position()).getBytes(StandardCharsets.UTF_8).length
          Left(Error(byteOffset.unsafe[Int >= 0], s"invalid UTF-16 input at UTF-8 byte offset $byteOffset", error))
      }
    }

    def decode(bytes: Vec[Byte]): Either[Error, String] = decode(Vec.toArray(bytes), 0, bytes.size)

    def decode(bytes: Array[Byte], offset: Int, length: Int): Either[Error, String] = {
      val input = ByteBuffer.wrap(bytes, offset, length)
      try
        Right(StandardCharsets.UTF_8.newDecoder()
          .onMalformedInput(CodingErrorAction.REPORT)
          .onUnmappableCharacter(CodingErrorAction.REPORT)
          .decode(input)
          .toString)
      catch {
        case error: CharacterCodingException =>
          val byteOffset = input.position()
          Left(Error(byteOffset.unsafe[Int >= 0], s"invalid UTF-8 at byte offset $byteOffset", error))
      }
    }
  }

  /** Java-style signed integer literal: decimal, leading-zero octal, or 0x/0X/# hexadecimal. */
  def parseIntLiteral(text: String): Option[Int] = {
    val negative        = text.startsWith("-")
    val unsigned        = if (negative || text.startsWith("+")) text.drop(1) else text
    val (digits, radix) = unsigned match {
      case s"0x$value"                  => (value, 16)
      case s"0X$value"                  => (value, 16)
      case s"#$value"                   => (value, 16)
      case s"0$value" if value.nonEmpty => (value, 8)
      case _                            => (unsigned, 10)
    }
    if (digits.isEmpty || digits.startsWith("-") || digits.startsWith("+")) None
    else scala.util.Try(Integer.parseInt(if (negative) s"-$digits" else digits, radix)).toOption
  }

  /** ASCII digit of `radix`, independent of Unicode digit categories. */
  def isDigit(character: Char, radix: Int in 2 ~ 36): Boolean = {
    val value =
      if (character >= '0' && character <= '9') character - '0'
      else if (character >= 'a' && character <= 'z') character - 'a' + 10
      else if (character >= 'A' && character <= 'Z') character - 'A' + 10
      else radix.raw
    value < radix
  }

  /** Signed decimal or 0x hexadecimal text, with 0b binary, leading-zero octal and `_` digit separators when the
    * grammar allows them. Whitespace and suffixes belong to the caller's grammar.
    */
  def parseBigIntLiteral(
      text: String,
      binary: Boolean = false,
      separators: Boolean = false,
      octal: Boolean = false
  ): Option[BigInt] = {
    val negative                         = text.startsWith("-")
    val unsigned                         = if (negative || text.startsWith("+")) text.drop(1) else text
    val literal: (String, Int in 2 ~ 36) = unsigned match {
      case s"0x$value"                           => (value, 16)
      case s"0X$value"                           => (value, 16)
      case s"0b$value" if binary                 => (value, 2)
      case s"0B$value" if binary                 => (value, 2)
      case s"0$value" if octal && value.nonEmpty => (value, 8)
      case _                                     => (unsigned, 10)
    }
    val (body, radix) = literal
    val digits        = if (separators) body.filterNot(_ == '_') else body
    Option.when(digits.nonEmpty && digits.forall(isDigit(_, radix))) {
      val value = BigInt(digits, radix)
      if (negative) -value else value
    }
  }

  // Java Formatter's Object varargs require boxed arguments at this boundary.
  @SuppressWarnings(Array("scalafix:NoBoxedPrimitives"))
  def formatRoot(pattern: String, arguments: (java.lang.Integer | java.lang.Double | String)*): String =
    PlatformString.format(pattern, arguments*)

  /** Decode an encoded name without silently replacing malformed bytes. */
  def decodeUtf8(bytes: Array[Byte], offset: Int, length: Int): Either[CharacterCodingException, String] =
    UTF8.decode(bytes, offset, length).left.map(_.cause)

  extension (value: NonEmpty[String]) {
    @targetName("nonEmptyUpperCaseRoot")
    def toUpperCaseRoot: NonEmpty[String] = PlatformString.upper(value).unsafe[NonEmpty[String]]

    @targetName("nonEmptyLowerCaseRoot")
    def toLowerCaseRoot: NonEmpty[String] = PlatformString.lower(value).unsafe[NonEmpty[String]]
  }

  extension (value: String) {
    def toUpperCaseRoot: String = PlatformString.upper(value)

    def toLowerCaseRoot: String = PlatformString.lower(value)
  }
}
