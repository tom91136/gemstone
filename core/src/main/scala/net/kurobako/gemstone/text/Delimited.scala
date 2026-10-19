package net.kurobako.gemstone.text

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import scala.annotation.tailrec

object Delimited {
  case class Segment(offset: Int >= 0, text: String)

  case class Unbalanced(offset: Int >= 0, message: NonEmpty[String])

  /** Splits at separators outside `()`, `[]`, and `{}` nesting. Segments keep their input offset and surrounding
    * whitespace; an input without separators is one segment. An unclosed delimiter is reported at its opening.
    */
  inline def splitTopLevel(text: String, inline separator: Char => Boolean): Either[Unbalanced, Vec[Segment]] = {
    def segment(start: Int, end: Int): Segment = Segment(start.unsafe[Int >= 0], text.substring(start, end))
    def closing(open: Int): Char               = text.charAt(open) match {
      case '(' => ')'
      case '[' => ']'
      case _   => '}'
    }
    @tailrec
    def loop(index: Int, start: Int, open: List[Int], segments: List[Segment]): Either[Unbalanced, Vec[Segment]] =
      if (index == text.length) open match {
        case Nil        => Right(Vec.from((segment(start, index) :: segments).reverse))
        case inner :: _ => Left(Unbalanced(inner.unsafe[Int >= 0], "unclosed delimiter"))
      }
      else text.charAt(index) match {
        case '(' | '[' | '{'           => loop(index + 1, start, index :: open, segments)
        case close @ (')' | ']' | '}') => open match {
            case inner :: rest if closing(inner) == close => loop(index + 1, start, rest, segments)
            case _ => Left(Unbalanced(index.unsafe[Int >= 0], "unmatched closing delimiter"))
          }
        case character if open.isEmpty && separator(character) =>
          loop(index + 1, index + 1, open, segment(start, index) :: segments)
        case _ => loop(index + 1, start, open, segments)
      }
    loop(index = 0, start = 0, Nil, Nil)
  }
}
