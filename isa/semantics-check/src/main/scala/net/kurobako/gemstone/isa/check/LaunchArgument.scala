package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*

/** One value of a recorded launch: a scalar of a stated type, or a buffer of a stated size and contents. `i32:64` is a
  * four-byte integer, `f64:0.5` an eight-byte float, `buffer:4096` a zeroed buffer, `buffer:4096:fill=f32:1.0` a buffer
  * of that constant, and `buffer:4096:ramp=i32:0:1` a buffer of `start + index * step`, with the start and step
  * defaulting to 0 and 1.
  */
enum LaunchArgument derives CanEqual {
  case Scalar(element: LaunchArgument.Element, value: LaunchArgument.Number)
  case Buffer(size: Long > 0, fill: LaunchArgument.Fill)
}

object LaunchArgument {

  enum Element(val name: String, val width: Int, val floating: Boolean) derives CanEqual {
    case I8  extends Element("i8", 1, false)
    case I16 extends Element("i16", 2, false)
    case I32 extends Element("i32", 4, false)
    case I64 extends Element("i64", 8, false)
    case F32 extends Element("f32", 4, true)
    case F64 extends Element("f64", 8, true)

    def number(text: String): Either[String, Number] =
      if (floating) text.toDoubleOption.map(Number.Real(_)).toRight(s"'$text' is not a $name")
      else text.toLongOption.map(Number.Integer(_)).toRight(s"'$text' is not a $name")

    def bits(number: Number): Long = (this, number) match {
      case (F32, Number.Real(value))  => java.lang.Float.floatToRawIntBits(value.toFloat).toLong
      case (F64, Number.Real(value))  => java.lang.Double.doubleToRawLongBits(value)
      case (_, Number.Integer(value)) => value
      case (_, Number.Real(value))    => value.toLong
    }

    def bytes(number: Number): Vec[Byte] = {
      val value = bits(number)
      Vec.tabulate(width)(byte => (value >>> (8 * byte)).toByte)
    }
  }

  object Element {
    def parse(text: String): Either[String, Element] =
      Element.values.find(_.name == text).toRight(s"'$text' is not one of ${Element.values.map(_.name).mkString(", ")}")
  }

  enum Number derives CanEqual {
    case Integer(value: Long)
    case Real(value: Double)

    def plus(step: Number, times: Long): Number = (this, step) match {
      case (Integer(start), Integer(by)) => Integer(start + by * times)
      case (Real(start), Real(by))       => Real(start + by * times)
      case (Integer(start), Real(by))    => Real(start + by * times)
      case (Real(start), Integer(by))    => Real(start + by * times)
    }
  }

  enum Fill derives CanEqual {
    case Zero
    case Constant(element: Element, value: Number)
    case Ramp(element: Element, start: Number, step: Number)
  }

  def parse(text: String): Either[String, Vec[LaunchArgument]] =
    BlockRegion.each(text.split(',').toVec)(one)

  private def one(text: String): Either[String, LaunchArgument] = text.split(':').toList match {
    case "buffer" :: size :: rest =>
      for {
        bytes <- size.toLongOption.toRight(s"'$size' is not a buffer size").flatMap(
          _.refine[Long > 0].left.map(_ => s"a buffer of $size bytes is empty")
        )
        fill <- filled(rest)
      } yield Buffer(bytes, fill)
    case type_ :: value :: Nil =>
      for {
        element <- Element.parse(type_)
        number  <- element.number(value)
      } yield Scalar(element, number)
    case _ =>
      Left(s"'$text' is neither <type>:<value> nor buffer:<bytes>[:fill=<type>:<value> | :ramp=<type>[:start[:step]]]")
  }

  private def filled(parts: List[String]): Either[String, Fill] = parts match {
    case Nil                                              => Right(Fill.Zero)
    case head :: value :: Nil if head.startsWith("fill=") =>
      for {
        element <- Element.parse(head.stripPrefix("fill="))
        number  <- element.number(value)
      } yield Fill.Constant(element, number)
    case head :: rest if head.startsWith("ramp=") && rest.size <= 2 =>
      for {
        element <- Element.parse(head.stripPrefix("ramp="))
        start   <- rest.headOption.fold(element.number("0"))(element.number)
        step    <- rest.drop(1).headOption.fold(element.number("1"))(element.number)
      } yield Fill.Ramp(element, start, step)
    case _ => Left(s"'${parts.mkString(":")}' is neither fill=<type>:<value> nor ramp=<type>[:start[:step]]")
  }

  /** The bytes of a buffer of `size` bytes with `fill`; a tail shorter than an element stays zero. */
  def contents(size: Int, fill: Fill): Array[Byte] = {
    val out                                                 = new Array[Byte](size)
    def write(element: Element, value: Int => Number): Unit = (0 until size / element.width).foreach { index =>
      val bytes = element.bytes(value(index))
      bytes.indices.foreach(byte => out(index * element.width + byte) = bytes(byte))
    }
    fill match {
      case Fill.Zero                       => ()
      case Fill.Constant(element, value)   => write(element, _ => value)
      case Fill.Ramp(element, start, step) => write(element, index => start.plus(step, index.toLong))
    }
    out
  }
}
