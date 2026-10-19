package net.kurobako.gemstone.vm

import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.string.*
import net.kurobako.gemstone.vm.Event.*
import scala.collection.mutable

/** Renders a structured device-`printf` event into text. Mirrors the C `printf` subset the interpreter emits:
  * integer/pointer/text/floating conversions, optional flags, width, precision, and `%vN` lane expansion.
  */
object PrintRenderer {

  def render(event: Print, maxOutputChars: Int > 0): Either[String, String] = {
    val output = mutable.StringBuilder()
    val format = event.format

    def literalEnd(from: Int): Int = {
      val percent = format.indexOf('%', from)
      if (percent < 0) format.length else percent
    }

    @scala.annotation.tailrec
    def loop(index: Int, argumentIndex: Int): Either[String, Int] = {
      val literal = literalEnd(index)
      if (literal > index) output.append(format.substring(index, literal))
      if (output.length > maxOutputChars) Left("device printf output exceeds the limit")
      else if (literal >= format.length) Right(argumentIndex)
      else if (literal + 1 < format.length && format.charAt(literal + 1) == '%') {
        output.append('%')
        loop(literal + 2, argumentIndex)
      } else
        parseConversion(literal) match {
          case Left(message)                                                         => Left(message)
          case Right((flagsAndSize, vectorWidth, lengthSpecifier, conversion, next)) =>
            if (argumentIndex >= event.arguments.size) Left("device printf arguments are exhausted")
            else {
              val value = event.arguments(argumentIndex)
              if (argumentLanes(value) != vectorWidth) Left("device printf vector width does not match")
              else
                (0 until vectorWidth).foldLeft[Either[String, Unit]](Right(())) { (accumulated, lane) =>
                  accumulated.flatMap { _ =>
                    renderArgument(value, flagsAndSize, lengthSpecifier, conversion, lane).flatMap { rendered =>
                      val separator = if (lane == 0) "" else ","
                      if (rendered.length.toLong + output.length + separator.length > maxOutputChars)
                        Left("device printf output exceeds the limit")
                      else { val _ = output.append(separator).append(rendered); Right(()) }
                    }
                  }
                } match {
                  case Left(message) => Left(message)
                  case Right(_)      => loop(next, argumentIndex + 1)
                }
            }
        }
    }

    def parseConversion(start: Int): Either[String, (String, Int, String, Char, Int)] = {
      val flagsEnd = scanWhile(format, start + 1, character => "-+ #0".indexOf(character) >= 0)
      if (flagsEnd < format.length && format.charAt(flagsEnd) == '*') Left("device printf width is not supported")
      else {
        val widthEnd                       = scanWhile(format, flagsEnd, _.isDigit)
        val (precisionEnd, precisionError) =
          if (widthEnd < format.length && format.charAt(widthEnd) == '.') {
            val digitsStart = widthEnd + 1
            if (digitsStart < format.length && format.charAt(digitsStart) == '*') (widthEnd, true)
            else (scanWhile(format, digitsStart, _.isDigit), false)
          } else (widthEnd, false)
        val width     = format.substring(flagsEnd, widthEnd)
        val precision = if (precisionEnd > widthEnd) format.substring(widthEnd + 1, precisionEnd) else ""
        def exceedsLimit(digits: String): Boolean =
          digits.nonEmpty && digits.toIntOption.forall(_ > maxOutputChars)
        if (exceedsLimit(width) || exceedsLimit(precision)) Left("device printf output exceeds the limit")
        else if (precisionError) Left("device printf precision is not supported")
        else {
          val (vectorEnd, vectorWidth, vectorError) =
            if (precisionEnd < format.length && format.charAt(precisionEnd) == 'v') {
              val digitsStart = precisionEnd + 1
              val digitsEnd   = scanWhile(format, digitsStart, _.isDigit)
              if (digitsStart == digitsEnd) (precisionEnd, 1, true)
              else (digitsEnd, format.substring(digitsStart, digitsEnd).toIntOption.getOrElse(0), false)
            } else (precisionEnd, 1, false)
          if (vectorError || vectorWidth <= 0) Left("device printf vector width is invalid")
          else {
            val lengthStart = vectorEnd
            val lengthEnd   = scanWhile(format, lengthStart, character => character == 'h' || character == 'l')
            if (lengthEnd >= format.length) Left("device printf conversion is missing")
            else
              Right(
                (
                  format.substring(start + 1, precisionEnd),
                  vectorWidth,
                  format.substring(lengthStart, lengthEnd),
                  format.charAt(lengthEnd),
                  lengthEnd + 1
                )
              )
          }
        }
      }
    }

    loop(index = 0, argumentIndex = 0) match {
      case Left(message)        => Left(message)
      case Right(argumentIndex) =>
        if (argumentIndex != event.arguments.size) Left("device printf has unused arguments")
        else if (output.length > maxOutputChars) Left("device printf output exceeds the limit")
        else Right(output.result())
    }
  }

  private inline def scanWhile(format: String, from: Int, inline predicate: Char => Boolean): Int = {
    val found = format.indexWhere(character => !predicate(character), from)
    if (found < 0) format.length else found
  }

  private def argumentLanes(argument: PrintArgument): Int = argument match {
    case PrintArgument.Integer(bits, _)  => bits.size
    case PrintArgument.Floating(bits, _) => bits.size
    case _                               => 1
  }

  private def renderArgument(
      argument: PrintArgument,
      flagsAndSize: String,
      lengthSpecifier: String,
      conversion: Char,
      lane: Int
  ): Either[String, String] = argument match {
    case PrintArgument.Integer(bits, width) =>
      if (lane >= bits.size) Left("device printf integer argument is invalid")
      else {
        val effectiveWidth: Int in 1 ~ 64 =
          if (lengthSpecifier == "hh") 8
          else if (lengthSpecifier == "h") 16 else if (lengthSpecifier == "l") 64 else width
        if (conversion == 'c') formatted(flagsAndSize, 'c', (bits(lane) & 0xff).toInt)
        else if ("diuoxX".indexOf(conversion) >= 0)
          integer(flagsAndSize, conversion, bits(lane), effectiveWidth)
        else Left(s"device printf conversion %$conversion is unsupported")
      }
    case PrintArgument.Floating(bits, width) =>
      if (lane >= bits.size)
        Left("device printf floating argument is invalid")
      else if ("aAeEfFgG".indexOf(conversion) < 0) Left(s"device printf conversion %$conversion is unsupported")
      else floating(flagsAndSize, conversion, IEEE754.toDouble(bits(lane), width))
    case PrintArgument.Pointer(address) =>
      if (lane != 0 || conversion != 'p') Left(s"device printf conversion %$conversion is unsupported")
      else Right(s"0x${java.lang.Long.toHexString(address)}")
    case PrintArgument.Text(value) =>
      if (lane != 0 || conversion != 's') Left(s"device printf conversion %$conversion is unsupported")
      else formatted(flagsAndSize, 's', value)
  }

  private case class Format(flags: String, width: Int, precision: Option[Int])

  private def formatOptions(specifier: String): Either[String, Format] = {
    val flagsEnd       = scanWhile(specifier, 0, character => "-+ #0".indexOf(character) >= 0)
    val precisionStart = specifier.indexOf('.', flagsEnd)
    val widthText      = specifier.substring(flagsEnd, if (precisionStart < 0) specifier.length else precisionStart)
    val precisionText  = if (precisionStart < 0) "" else specifier.substring(precisionStart + 1)
    def size(text: String): Option[Int] =
      if (text.isEmpty) Some(0) else text.toIntOption.filter(value => value >= 0)
    for {
      width     <- size(widthText).toRight("device printf width exceeds the limit")
      precision <- size(precisionText).toRight("device printf precision exceeds the limit")
    } yield Format(specifier.take(flagsEnd), width, Option.when(precisionStart >= 0)(precision))
  }

  private def padNumber(format: Format, sign: String, prefix: String, digits: String, zeroes: Boolean): String = {
    val padding = math.max(0, format.width - sign.length - prefix.length - digits.length)
    if (format.flags.contains('-')) sign + prefix + digits + " " * padding
    else if (zeroes && format.flags.contains('0')) sign + prefix + "0" * padding + digits
    else " " * padding + sign + prefix + digits
  }

  private def sign(negative: Boolean, flags: String): String =
    if (negative) "-" else if (flags.contains('+')) "+" else if (flags.contains(' ')) " " else ""

  // Java Formatter rejects integer precision and treats unsigned decimal as signed.
  private def integer(
      specifier: String,
      conversion: Char,
      bits: Long,
      width: Int in 1 ~ 64
  ): Either[String, String] =
    formatOptions(specifier).map { format =>
      val signed        = conversion == 'd' || conversion == 'i'
      val value         = if (signed) Bits.signExtend(bits, width) else Bits.truncate(bits, width)
      val base          = if (conversion == 'o') 8 else if (conversion == 'x' || conversion == 'X') 16 else 10
      val magnitudeBits = if (signed && value < 0L) -value else value
      val digits        = if (format.precision.contains(0) && value == 0L) ""
      else java.lang.Long.toUnsignedString(magnitudeBits, base)
      val padded    = "0" * math.max(0, format.precision.getOrElse(0) - digits.length) + digits
      val magnitude =
        if (format.flags.contains('#') && conversion == 'o' && !padded.startsWith("0")) "0" + padded else padded
      val prefix   = if (format.flags.contains('#') && base == 16 && value != 0) "0x" else ""
      val rendered = padNumber(
        format,
        if (signed) sign(value < 0, format.flags) else "",
        prefix,
        magnitude,
        format.precision.isEmpty
      )
      if (conversion == 'X') rendered.toUpperCaseRoot else rendered
    }

  private def floating(specifier: String, conversion: Char, value: Double): Either[String, String] =
    formatOptions(specifier).map { format =>
      val negative    = java.lang.Double.doubleToRawLongBits(value) < 0
      val lower       = conversion.toLower
      val hexadecimal = lower == 'a' && value.isFinite
      val digits      = if (value.isNaN) "nan"
      else if (value.isInfinite) "inf"
      else if (hexadecimal) hexFloat(math.abs(value), format.precision, format.flags.contains('#'))
      else decimalFloat(math.abs(value), lower, format.precision.getOrElse(6), format.flags.contains('#'))
      val rendered = padNumber(
        format,
        sign(negative && !value.isNaN, format.flags),
        if (hexadecimal) "0x" else "",
        digits,
        value.isFinite
      )
      if (conversion.isUpper) rendered.toUpperCaseRoot else rendered
    }

  // Start with the exact binary value, then round once using the C default
  // ties-to-even rule. Decimal string conversion first would change ties.
  private def decimalFloat(value: Double, conversion: Char, precision: Int, alternate: Boolean): String = {
    val exact                                       = BigDecimal.exact(value).bigDecimal
    val rounding                                    = java.math.RoundingMode.HALF_EVEN
    def exponent(number: java.math.BigDecimal): Int =
      if (number.signum() == 0) 0 else number.precision() - number.scale() - 1
    def point(text: String): String = if (alternate && !text.contains('.')) text + "." else text
    def trim(text: String): String  =
      if (alternate || !text.contains('.')) text else text.reverse.dropWhile(_ == '0').stripPrefix(".").reverse
    def scientific(number: java.math.BigDecimal, digits: Int, strip: Boolean): String = {
      val power    = exponent(number)
      val mantissa = point(number.movePointLeft(power).setScale(digits, rounding).toPlainString)
      (if (strip) trim(mantissa) else mantissa) + "e" + (if (power < 0) "-" else "+") +
        (if (math.abs(power) < 10) "0" else "") + math.abs(power).toString
    }
    if (conversion == 'f') point(exact.setScale(precision, rounding).toPlainString)
    else if (conversion == 'e')
      scientific(exact.round(java.math.MathContext(precision + 1, rounding)), precision, strip = false)
    else {
      val significant = math.max(1, precision)
      val rounded     = exact.round(java.math.MathContext(significant, rounding))
      val power       = exponent(rounded)
      if (power < -4 || power >= significant) scientific(rounded, significant - 1, strip = true)
      else trim(point(rounded.setScale(significant - power - 1, rounding).toPlainString))
    }
  }

  private def hexFloat(value: Double, precision: Option[Int], alternate: Boolean): String = {
    val bits            = java.lang.Double.doubleToRawLongBits(value)
    val encodedExponent = ((bits >>> 52) & 0x7ffL).toInt
    val exponent        = if (value == 0.0) 0 else if (encodedExponent == 0) -1022 else encodedExponent - 1023
    val significand     = (bits & 0xfffffffffffffL) | (if (encodedExponent == 0) 0L else 1L << 52)
    val digits          = precision.getOrElse(13)
    val shift           = 52 - math.min(13, digits) * 4
    val quotient        = significand >>> shift
    val remainder       = significand & ((1L << shift) - 1)
    val half            = if (shift == 0) 0L else 1L << (shift - 1)
    val rounded         = quotient +
      (if (shift != 0 && (remainder > half || (remainder == half && (quotient & 1L) != 0))) 1L else 0L)
    val text     = java.lang.Long.toHexString(rounded)
    val padded   = "0" * math.max(0, math.min(13, digits) + 1 - text.length) + text
    val fraction = padded.drop(1) + "0" * math.max(0, digits - 13)
    val retained = if (precision.isEmpty) fraction.reverse.dropWhile(_ == '0').reverse else fraction
    padded.take(1) + (if (retained.nonEmpty || alternate) "." + retained else "") + "p" +
      (if (exponent < 0) "" else "+") + exponent.toString
  }

  // Java Formatter's Object varargs require a boxed argument at this boundary.
  @SuppressWarnings(Array("scalafix:NoBoxedPrimitives"))
  private def formatted(specifier: String, conversion: Char, value: Int): Either[String, String] =
    formatValue(specifier, conversion, java.lang.Integer.valueOf(value))

  private def formatted(specifier: String, conversion: Char, value: String): Either[String, String] =
    formatValue(specifier, conversion, value)

  // the boxed Integer passes straight through to the Formatter
  @SuppressWarnings(Array("scalafix:NoBoxedPrimitives"))
  private def formatValue(
      specifier: String,
      conversion: Char,
      value: java.lang.Integer | String
  ): Either[String, String] =
    try Right(string.formatRoot(s"%$specifier$conversion", value))
    catch { case _: java.util.IllegalFormatException => Left(s"device printf cannot format %$conversion") }

}
