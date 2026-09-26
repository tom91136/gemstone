package net.kurobako.gemstone.server.execution

import net.kurobako.gemstone.binary.{BitWidth, Bits, FloatingPointWidth, IEEE754}
import net.kurobako.gemstone.vm.Event.{Print, PrintArgument}

import java.util.Locale

/** Renders a structured device-`printf` event into text. Mirrors the C `printf` subset the interpreter emits:
  * integer/pointer/text/floating conversions, optional flags, width, precision, and `%vN` lane expansion.
  */
private[execution] object PrintRenderer {
  private final val MaximumOutputBytes = 1024 * 1024

  def render(event: Print): Either[String, String] = {
    val output = StringBuilder()
    val format = event.format

    def literalEnd(from: Int): Int = {
      val percent = format.indexOf('%', from)
      if (percent < 0) format.length else percent
    }

    def loop(index: Int, argumentIndex: Int): Either[String, Int] = {
      val literal = literalEnd(index)
      if (literal > index) output.append(format.substring(index, literal))
      if (literal >= format.length) Right(argumentIndex)
      else if (literal + 1 < format.length && format.charAt(literal + 1) == '%') {
        output.append('%')
        loop(literal + 2, argumentIndex)
      } else
        parseConversion(literal).flatMap { case (flagsAndSize, vectorWidth, lengthSpecifier, conversion, next) =>
          if (argumentIndex >= event.arguments.size) Left("device printf arguments are exhausted")
          else {
            val value = event.arguments(argumentIndex)
            if (argumentLanes(value) != vectorWidth) Left("device printf vector width does not match")
            else
              (0 until vectorWidth).foldLeft[Either[String, Unit]](Right(())) { (accumulated, lane) =>
                accumulated.flatMap { _ =>
                  if (lane != 0) output.append(',')
                  appendArgument(output, value, flagsAndSize, lengthSpecifier, conversion, lane)
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
        if (precisionError) Left("device printf precision is not supported")
        else {
          val (vectorEnd, vectorWidth, vectorError) =
            if (precisionEnd < format.length && format.charAt(precisionEnd) == 'v') {
              val digitsStart = precisionEnd + 1
              val digitsEnd   = scanWhile(format, digitsStart, _.isDigit)
              if (digitsStart == digitsEnd) (precisionEnd, 1, true)
              else (digitsEnd, format.substring(digitsStart, digitsEnd).toInt, false)
            } else (precisionEnd, 1, false)
          if (vectorError) Left("device printf vector width is missing")
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

    loop(0, 0) match {
      case Left(message)        => Left(message)
      case Right(argumentIndex) =>
        if (argumentIndex != event.arguments.size) Left("device printf has unused arguments")
        else if (output.length > MaximumOutputBytes) Left("device printf output exceeds the limit")
        else Right(output.result())
    }
  }

  private def scanWhile(format: String, from: Int, predicate: Char => Boolean): Int = {
    val found = format.indexWhere(character => !predicate(character), from)
    if (found < 0) format.length else found
  }

  private def argumentLanes(argument: PrintArgument): Int = argument match {
    case PrintArgument.Integer(bits, _)  => bits.size
    case PrintArgument.Floating(bits, _) => bits.size
    case _                               => 1
  }

  @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
  private def widthMask(width: Int): Long = Bits.mask(BitWidth.unsafe(width))

  @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
  private def signedValue(bits: Long, width: Int): Long = Bits.signExtend(bits, BitWidth.unsafe(width))

  @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
  private def floatingValue(bits: Long, width: Int): Double = IEEE754.toDouble(bits, FloatingPointWidth.unsafe(width))

  private def append(output: StringBuilder, result: Either[String, String]): Either[String, Unit] =
    result.map { rendered => output.append(rendered); () }

  private def appendArgument(
      output: StringBuilder,
      argument: PrintArgument,
      flagsAndSize: String,
      lengthSpecifier: String,
      conversion: Char,
      lane: Int
  ): Either[String, Unit] = argument match {
    case PrintArgument.Integer(bits, width) =>
      if (lane >= bits.size || width <= 0 || width > 64) Left("device printf integer argument is invalid")
      else {
        val effectiveWidth =
          if (lengthSpecifier == "hh") 8
          else if (lengthSpecifier == "h") 16 else if (lengthSpecifier == "l") 64 else width
        if (conversion == 'c') append(output, formatted(flagsAndSize, 'c', (bits(lane) & 0xff).toInt))
        else if (conversion == 'd' || conversion == 'i')
          append(output, formatted(flagsAndSize, 'd', signedValue(bits(lane), effectiveWidth)))
        else if (conversion == 'u') append(output, formatted(flagsAndSize, 'd', bits(lane) & widthMask(effectiveWidth)))
        else if (conversion == 'o' || conversion == 'x' || conversion == 'X')
          append(output, formatted(flagsAndSize, conversion, bits(lane) & widthMask(effectiveWidth)))
        else Left(s"device printf conversion %$conversion is unsupported")
      }
    case PrintArgument.Floating(bits, width) =>
      if (lane >= bits.size || (width != 16 && width != 32 && width != 64))
        Left("device printf floating argument is invalid")
      else if ("aAeEfFgG".indexOf(conversion) < 0) Left(s"device printf conversion %$conversion is unsupported")
      else if (conversion == 'a' || conversion == 'A') {
        val rendered = hexFloat(floatingValue(bits(lane), width))
        output.append(if (conversion == 'A') rendered.toUpperCase(Locale.ROOT) else rendered)
        Right(())
      } else append(output, formatted(flagsAndSize, conversion, floatingValue(bits(lane), width)))
    case PrintArgument.Pointer(address) =>
      if (lane != 0 || conversion != 'p') Left(s"device printf conversion %$conversion is unsupported")
      else {
        output.append("0x")
        output.append(java.lang.Long.toHexString(address))
        Right(())
      }
    case PrintArgument.Text(value) =>
      if (lane != 0 || conversion != 's') Left(s"device printf conversion %$conversion is unsupported")
      else append(output, formatted(flagsAndSize, 's', value))
  }

  private def formatted(specifier: String, conversion: Char, value: Long): Either[String, String] =
    formatValue(specifier, conversion, java.lang.Long.valueOf(value))

  private def formatted(specifier: String, conversion: Char, value: Int): Either[String, String] =
    formatValue(specifier, conversion, java.lang.Integer.valueOf(value))

  private def formatted(specifier: String, conversion: Char, value: Double): Either[String, String] =
    formatValue(specifier, conversion, java.lang.Double.valueOf(value))

  private def formatted(specifier: String, conversion: Char, value: String): Either[String, String] =
    formatValue(specifier, conversion, value)

  private def formatValue(specifier: String, conversion: Char, value: AnyRef): Either[String, String] =
    try Right(String.format(Locale.ROOT, s"%$specifier$conversion", value))
    catch { case _: java.util.IllegalFormatException => Left(s"device printf cannot format %$conversion") }

  private def hexFloat(value: Double): String = {
    val bits     = java.lang.Double.doubleToRawLongBits(value)
    val negative = bits < 0
    val exponent = ((bits >>> 52) & 0x7ffL).toInt - 1023
    val mantissa = bits & 0xfffffffffffffL
    val sign     = if (negative) "-" else ""
    if (value.isNaN) "nan"
    else if (value.isInfinite) s"${sign}inf"
    else if ((bits & 0x7fffffffffffffffL) == 0L) s"${sign}0x0p+0"
    else if (exponent == -1023) s"${sign}0x0.${java.lang.Long.toHexString(mantissa)}p-1022"
    else s"${sign}0x1.${java.lang.Long.toHexString(mantissa)}p${if (exponent >= 0) "+" else ""}$exponent"
  }
}
