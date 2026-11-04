package net.kurobako.gemstone.isa.amdgpu

import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*
import scala.annotation.tailrec
import scala.collection.mutable

/** The host side of a device `printf` message, read as ROCclr's `devhcprintf.cpp` reads it. */
private object HostcallPrintf {
  private val Conversions    = "diouxXfFeEgGaAcspn"
  private val Limit: Int > 0 = 1024 * 1024

  def format(words: Array[Long]): (String, Int) = {
    val bytes = Array.ofDim[Byte]((words.length - 1) * 8)
    (1 until words.length).foreach(index => LittleEndian.write(words(index), bytes, (index - 1) * 8, 8))
    def terminator(from: Int): Int = bytes.indexOf(0.toByte, from) match { case -1 => bytes.length; case at => at }
    val formatEnd                  = terminator(0)
    val text                       = String(bytes, 0, formatEnd, StandardCharsets.ISO_8859_1)
    val end                        = words.length - 1
    val output                     = mutable.StringBuilder()

    def argument(index: Int): Long = words(1 + index)

    def cstring(index: Int): String = {
      val from = index * 8
      String(bytes, from, terminator(from) - from, StandardCharsets.ISO_8859_1)
    }

    def consume(specification: String, index: Int): Int = {
      val conversion = specification.last
      conversion match {
        case 's' =>
          val rendered = render(specification, Event.PrintArgument.Text(cstring(index)))
          output.append(rendered)
          index + paddedWords(rendered.length + 1)
        case 'p' =>
          val pointer = argument(index)
          output.append(render(pointerSpecification(specification), Event.PrintArgument.Text(pointerText(pointer))))
          index + 1
        case 'n'                                           => index + 1
        case 'f' | 'F' | 'e' | 'E' | 'g' | 'G' | 'a' | 'A' =>
          output.append(render(specification, Event.PrintArgument.Floating(Vec(argument(index)), 64)))
          index + 1
        case _ =>
          val (stripped, width) = integerSpecification(specification)
          output.append(render(stripped, Event.PrintArgument.Integer(Vec(argument(index)), width)))
          index + 1
      }
    }

    def convert(specification: String, index: Int): Int = {
      val stars = specification.count(_ == '*')
      stars match {
        case 0     => consume(specification, index)
        case 1 | 2 =>
          if (end - index < stars + 1) end
          else consume(
            substitute(specification, (0 until stars).map(offset => argument(index + offset).toInt)),
            index + stars
          )
        case _ => end
      }
    }

    @tailrec
    def loop(point: Int, index: Int): Unit = {
      val percent = text.indexOf('%', point)
      if (percent < 0) { output.append(text.substring(point)); () }
      else {
        output.append(text.substring(point, percent))
        val next = percent + 1
        if (next < text.length && text.charAt(next) == '%') { output.append('%'); loop(next + 1, index) }
        else if (index >= end) ()
        else {
          val conversion = text.indexWhere(Conversions.indexOf(_) >= 0, next)
          if (conversion < 0) ()
          else {
            val following = convert(text.substring(percent, conversion + 1), index)
            loop(conversion + 1, following)
          }
        }
      }
    }

    loop(0, paddedWords(formatEnd + 1))
    val latin = output.toString
    (String(latin.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8), latin.length)
  }

  private def paddedWords(bytes: Int): Int = (bytes + 7) / 8

  private def render(specification: String, argument: Event.PrintArgument): String =
    PrintRenderer.render(Event.Print(specification, Vec(argument)), Limit).getOrElse("")

  private def pointerText(pointer: Long): String =
    if (pointer == 0L) "(nil)" else "0x" + java.lang.Long.toHexString(pointer)

  private def pointerSpecification(specification: String): String = {
    val flags = specification.drop(1).takeWhile("-+ #0".indexOf(_) >= 0).filter(_ == '-')
    val width = specification.drop(1).dropWhile("-+ #0".indexOf(_) >= 0).takeWhile(_.isDigit)
    "%" + flags + width + "s"
  }

  private def integerSpecification(specification: String): (String, Int in 1 ~ 64) = {
    val modifiers            = specification.init.reverse.takeWhile("hlLjzt".indexOf(_) >= 0).reverse
    val width: Int in 1 ~ 64 = modifiers match {
      case ""   => 32
      case "hh" => 8
      case "h"  => 16
      case _    => 64
    }
    (specification.dropRight(modifiers.length + 1) + specification.last, width)
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def substitute(specification: String, values: Seq[Int]): String = {
    val builder = mutable.StringBuilder()
    var next    = 0
    var index   = 0
    while (index < specification.length) {
      val character = specification.charAt(index)
      if (character != '*') { builder.append(character); () }
      else {
        val value = values(next)
        next += 1
        val precision = index > 0 && specification.charAt(index - 1) == '.'
        if (value >= 0) builder.append(value)
        else if (precision) builder.setLength(builder.length - 1)
        else { builder.insert(1, '-'); builder.append(-value.toLong) }
        ()
      }
      index += 1
    }
    builder.toString
  }
}
