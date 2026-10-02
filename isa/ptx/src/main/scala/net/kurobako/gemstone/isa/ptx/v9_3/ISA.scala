package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.string.*

object ISA {
  enum DecodeError {
    case InvalidUTF8(offset: Int >= 0)
    case Syntax(error: Program.ParseError)

    def message: NonEmpty[String] = this match {
      case InvalidUTF8(offset) => s"invalid UTF-8 at byte offset $offset"
      case Syntax(error)       => error.message
    }
  }

  val version: Version                          = Instruction.version
  val instructions: Vec[Instruction.Definition] = Instruction.definitions

  def validate(program: Program, target: Target): ValidationOutcome[Validation.Issue] =
    Validation.validate(program, version, target)

  def decode(source: String): Either[Program.ParseError, Program] = Parser.decode(source)

  def decode(bytes: Vec[Byte]): Either[DecodeError, Program] =
    UTF8.decode(bytes)
      .left.map(error => DecodeError.InvalidUTF8(error.offset))
      .flatMap(source => Parser.decode(source).left.map(DecodeError.Syntax(_)))

}
