package net.kurobako.gemstone.isa.ptx

import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.string.*
import net.kurobako.gemstone.text.*

object ISA {
  enum DecodeError {
    case InvalidUTF8(offset: Int >= 0)
    case Syntax(error: SourceError)

    def message: NonEmpty[String] = this match {
      case InvalidUTF8(offset) => s"invalid UTF-8 at byte offset $offset"
      case Syntax(error)       => error.diagnostic
    }
  }

  /** Checks each module against the PTX ISA version and target it declares. */
  def validate(program: Program): ValidationOutcome[Validation.Issue] = Validation.validate(program, forms = true)

  def decode(bytes: Vec[Byte]): Either[DecodeError, Program] =
    UTF8.decode(bytes)
      .left.map(error => DecodeError.InvalidUTF8(error.offset))
      .flatMap(source => Parser.parse(source).left.map(DecodeError.Syntax(_)))

}
