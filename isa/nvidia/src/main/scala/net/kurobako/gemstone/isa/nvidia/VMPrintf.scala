package net.kurobako.gemstone.isa.nvidia

import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*
import scala.annotation.tailrec
import scala.collection.mutable

/** Decodes a device `vprintf` call: a format string and a buffer that packs each argument at its natural alignment
  * after the default promotions, so integers narrower than `long` take four bytes and floating-point values eight.
  */
private object VMPrintf {

  /** The unsigned value of `size` bytes at a byte offset of the argument buffer. */
  trait ReadArgument {
    def apply(offset: Long, size: Int): Either[Fault, Long]
  }

  /** The byte at a device address. */
  trait ReadByte {
    def apply(address: Long): Either[Fault, Int]
  }

  trait ReadString {
    def apply(address: Long): Either[Fault, String]
  }

  /** A printed event and the number of arguments it consumed. */
  case class Decoded(event: Event.Print, arguments: Int)

  private val MaxFormatBytes = 1024
  val MaxStringBytes: Int    = 1 << 20
  private val NullText       = "(null)"

  private case class Conversion(character: Char, size: Int, index: Int)

  /** A NUL-terminated string at `address`, read a byte at a time. */
  def string(read: ReadByte, address: Long, limit: Int): Either[Fault, String] = {
    @tailrec
    def loop(index: Int, bytes: mutable.ArrayBuffer[Byte]): Either[Fault, String] =
      if (index >= limit) Left(Fault.InvalidRequest(s"device printf string exceeds $limit bytes"))
      else read(address + index) match {
        case Left(fault)  => Left(fault)
        case Right(value) =>
          if (value == 0) Right(String(bytes.toArray, StandardCharsets.UTF_8))
          else loop(index + 1, bytes += value.toByte)
      }
    loop(0, mutable.ArrayBuffer.empty)
  }

  /** The event `format` and its packed `arguments` print, or `None` when the format uses a conversion the device
    * runtime rejects, which makes `vprintf` return -1.
    */
  def decode(
      readFormat: ReadByte,
      format: Long,
      arguments: ReadArgument,
      readString: ReadString
  ): Either[Fault, Option[Decoded]] =
    string(readFormat, format, MaxFormatBytes).flatMap { template =>
      conversions(template) match {
        case None             => Right(None)
        case Some(specifiers) =>
          val start = (0L, Vec.empty[Event.PrintArgument], template)
          specifiers.foldLeft[Either[Fault, (Long, Vec[Event.PrintArgument], String)]](Right(start)) {
            (state, specifier) =>
              state.flatMap { (position, decoded, format) =>
                val offset = (position + specifier.size - 1) / specifier.size * specifier.size
                arguments(offset, specifier.size).flatMap { value =>
                  // pointers print as text, which gives %p the width and justification of %s
                  if (specifier.character == 'p')
                    Right((
                      offset + specifier.size,
                      decoded :+ Event.PrintArgument.Text(if (value == 0L) "(nil)" else s"0x${value.toHexString}"),
                      format.patch(specifier.index, "s", 1)
                    ))
                  else argument(specifier, value, readString).map(found =>
                    (offset + specifier.size, decoded :+ found, format)
                  )
                }
              }
          }.map((_, decoded, format) => Some(Decoded(Event.Print(format, decoded), specifiers.size)))
      }
    }

  private def argument(specifier: Conversion, value: Long, readString: ReadString)
      : Either[Fault, Event.PrintArgument] = specifier.character match {
    case 's' => (if (value == 0L) Right(NullText) else readString(value)).map(Event.PrintArgument.Text(_))
    case floating if "aAeEfFgG".contains(floating) => Right(Event.PrintArgument.Floating(Vec(value), 64))
    case _ => Right(Event.PrintArgument.Integer(Vec(value), (specifier.size * 8).unsafe[Int in 1 ~ 64]))
  }

  private def conversions(format: String): Option[Vec[Conversion]] = {
    inline def skip(from: Int, inline accepted: Char => Boolean): Int = {
      val stop = format.indexWhere(!accepted(_), from)
      if (stop < 0) format.length else stop
    }
    @tailrec
    def loop(index: Int, found: Vec[Conversion]): Option[Vec[Conversion]] =
      if (index >= format.length) Some(found)
      else if (format(index) != '%') loop(index + 1, found)
      else if (index + 1 < format.length && format(index + 1) == '%') loop(index + 2, found)
      else {
        val flags     = skip(index + 1, "-+ #0".contains(_))
        val width     = skip(flags, _.isDigit)
        val precision = if (width < format.length && format(width) == '.') skip(width + 1, _.isDigit) else width
        val length    = skip(precision, "hlzjt".contains(_))
        if (length >= format.length) None
        else {
          val wide =
            format.substring(precision, length).exists(character => character == 'l' || "zjt".contains(character))
          val character = format(length)
          if ("diuoxX".contains(character)) loop(length + 1, found :+ Conversion(character, if (wide) 8 else 4, length))
          else if (character == 'c') loop(length + 1, found :+ Conversion(character, 4, length))
          else if ("aAeEfFgGps".contains(character)) loop(length + 1, found :+ Conversion(character, 8, length))
          else None
        }
      }
    loop(0, Vec.empty)
  }
}
