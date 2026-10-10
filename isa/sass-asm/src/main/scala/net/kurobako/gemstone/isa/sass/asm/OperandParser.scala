package net.kurobako.gemstone.isa.sass.asm

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.*
import net.kurobako.gemstone.string.*
import net.kurobako.gemstone.text.*
import scala.util.control.NonFatal

/** Recursive-descent parser for the structured operand subset shared by text and generated-binary frontends. */
object OperandParser {
  def identifierStart(character: Char): Boolean = character.isLetter || character == '_'

  def identifierPart(character: Char): Boolean = character.isLetterOrDigit || character == '_' || character == '$'

  def parse(source: String): Either[String, Operand] = {
    val reader = Reader(source)
    try {
      val value = reader.operand()
      if (!reader.cursor.atEnd) reader.fail("end of operand")
      Right(value)
    } catch {
      case failure: SourceCursor.Failure =>
        Left(s"invalid SASS operand '$source' at ${reader.failedAt(failure).raw}")
      case NonFatal(error) => Left(s"invalid SASS operand '$source': ${error.getMessage}")
    }
  }

  def parseMany(source: String): Either[String, Vec[Operand]] =
    if (source.trim.isEmpty) Right(Vec.empty)
    else {
      val reader = Reader(source)
      try {
        val values = reader.operandList()
        reader.cursor.skipWhitespace()
        if (!reader.cursor.atEnd) reader.fail("end of operand list")
        Right(values)
      } catch {
        case failure: SourceCursor.Failure =>
          Left(s"invalid SASS operand list at ${reader.failedAt(failure).raw}")
        case NonFatal(error) => Left(s"invalid SASS operand list: ${error.getMessage}")
      }
    }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private class Reader(source: String) {
    val cursor: SourceCursor       = SourceCursor(source)
    private var furthest: Int >= 0 = cursor.offset

    def failedAt(failure: SourceCursor.Failure): Int >= 0 =
      if (furthest.raw > failure.mark.offset.raw) furthest else failure.mark.offset

    private def record(offset: Int >= 0): Unit =
      if (offset.raw >= furthest.raw) furthest = offset

    def fail(expectation: NonEmpty[String]): Nothing = {
      record(cursor.offset)
      cursor.fail(expectation)
    }

    private def expect(value: Char): Unit =
      if (!cursor.consume(value)) fail(s"'$value'")

    private def expect(value: String): Unit =
      if (!cursor.consume(value)) fail(s"'$value'")

    private def attempt[A](parse: => A): Option[A] = {
      val start = cursor.mark
      try Some(parse)
      catch {
        case failure: SourceCursor.Failure =>
          record(failure.mark.offset)
          cursor.restore(start)
          None
      }
    }

    private def readWhile(predicate: Char => Boolean): String = {
      val start = cursor.offset
      cursor.skipWhile(predicate)
      cursor.slice(start)
    }

    private def digits(): String = {
      val value = readWhile(isDigit(_, 10))
      if (value.isEmpty) fail("decimal digit")
      value
    }

    private def suffix(): String = {
      val value = readWhile(identifierPart(_))
      if (value.isEmpty) fail("operand selector")
      value
    }

    private def identifier(): String = {
      if (cursor.atEnd || !identifierStart(cursor.char())) fail("identifier")
      val start = cursor.offset
      cursor.advance()
      cursor.skipWhile(identifierPart(_))
      cursor.slice(start)
    }

    private def dottedSuffixes(): Vec[String] = {
      val result = Vec.newBuilder[String]
      while (cursor.consume('.')) result += suffix()
      result.result()
    }

    private def register(): (Operand.Register, Vec[String]) = {
      val bank =
        if (cursor.consume("UR")) "UR"
        else if (cursor.consume("SB")) "SB"
        else if (cursor.consume('R')) "R"
        else if (cursor.consume('B')) "B"
        else {
          val value = readWhile(c => c.isLetter || c == '_')
          if (value.isEmpty || value == "P" || value == "UP" || value.startsWith("SR")) fail("register bank")
          value
        }
      // An out-of-range index is an error, not an identifier to backtrack into.
      val index: Int in 0 ~ 255 =
        if (cursor.consume('Z')) 255
        else {
          val text = digits()
          text.toIntOption.flatMap(_.refine[Int in 0 ~ 255].toOption)
            .getOrElse(throw IllegalArgumentException(s"register index $text exceeds 255"))
        }
      Operand.Register(RegisterBank.fromSpelling(bank), index) -> dottedSuffixes()
    }

    private def tryRegister(): Option[(Operand.Register, Vec[String])] = attempt(register())

    def operandList(): Vec[Operand] = {
      val values = Vec.newBuilder[Operand]
      values += operand()
      var more = true
      while (more) {
        val separatorStart = cursor.offset
        cursor.skipWhitespace()
        if (cursor.consume(',')) {
          cursor.skipWhitespace()
          values += operand()
        } else {
          if (cursor.offset != separatorStart) fail("comma after whitespace")
          more = false
        }
      }
      values.result()
    }

    def operand(): Operand = {
      val decorations = readWhile(c => "!~-+".contains(c))
      val value       = atom()
      val inverted    = decorations.contains('!') || decorations.contains('~')
      val negative    = decorations.contains('-')
      value match {
        case Operand.Predicate(index, uniform, negated) =>
          Operand.Predicate(index, uniform, negated ^ inverted ^ negative)
        case Operand.Unary(inner, priorNegative, priorNot, priorAbsolute) if negative || inverted =>
          Operand.Unary(inner, negative ^ priorNegative, inverted ^ priorNot, priorAbsolute)
        case _ if negative || inverted => Operand.Unary(value, negative, inverted, absolute = false)
        case _                         => value
      }
    }

    private def atom(): Operand =
      if (cursor.startsWith("bits<")) rawBits()
      else if (cursor.startsWith("desc[")) descriptorAddress()
      else if (cursor.startsWith("c[")) constantBank()
      else if (cursor.consume('[')) address()
      else if (cursor.consume('{')) group()
      else if (cursor.consume('|')) absolute()
      else if (!cursor.atEnd && cursor.char().isDigit) magnitude()
      else if (!cursor.atEnd && identifierStart(cursor.char())) wordOperand()
      else fail("operand")

    private def rawBits(): Operand = {
      expect("bits<")
      if (!cursor.atEnd && cursor.char().isDigit) rawWidthBits()
      else rawTypedBits()
    }

    private def rawTypedBits(): Operand = {
      val format = identifier()
      expect('>')
      expect(':')
      val width = digits().toInt
      expect('(')
      val bits = rawHexBits()
      expect(')')
      val encoding = FloatEncoding.fromSpelling(format)
      val checked  = width.refine[Int in (16, 32, 64)].toOption.filter(value => encoding.width.forall(_ == value))
        .getOrElse(fail(s"floating width matching $format"))
      if (bits.bitLength > width) fail(s"floating payload within $width bits")
      Operand.FloatingBits(encoding, checked, bits)
    }

    private def rawWidthBits(): Operand = {
      val width = digits().toInt
      expect(">(")
      val bits = rawHexBits()
      expect(')')
      if (width < 0) throw IllegalArgumentException(s"negative bit width $width")
      if (width > 0 && bits.bitLength > width) throw IllegalArgumentException(s"bit payload exceeds $width bits")
      Operand.Bits(bits, width.unsafe[Int >= 0])
    }

    private def rawHexBits(): BigInt = {
      if (!cursor.startsWith("0x") && !cursor.startsWith("0X")) fail("hexadecimal bit payload")
      integer(16)
    }

    private def integer(radix: Int in 2 ~ 36): BigInt = {
      val start = cursor.offset
      cursor.advance()
      cursor.advance()
      cursor.skipWhile(character => isDigit(character, radix) || character == '_')
      parseBigIntLiteral(cursor.slice(start), binary = true, separators = true)
        .getOrElse(fail(if (radix.raw == 16) "hexadecimal digit" else "binary digit"))
    }

    private def exponent(): Unit = {
      if (!cursor.consume('e') && !cursor.consume('E')) fail("floating exponent")
      if (!cursor.consume('+')) { val _ = cursor.consume('-') }
      val _ = digits()
    }

    private def magnitude(): Operand =
      if (cursor.startsWith("0x") || cursor.startsWith("0X")) Operand.Bits(integer(16), 0)
      else if (cursor.startsWith("0b") || cursor.startsWith("0B")) Operand.Bits(integer(2), 0)
      else {
        val whole = digits()
        if (cursor.consume('.')) {
          val fraction      = readWhile(_.isDigit)
          val exponentStart = cursor.offset
          if (cursor.at('e') || cursor.at('E')) exponent()
          val raw    = whole + "." + fraction + cursor.slice(exponentStart)
          val number = BigDecimal(raw)
          Operand.Floating(number, raw, number == 0 && raw.startsWith("-"))
        } else if (cursor.at('e') || cursor.at('E')) {
          val exponentStart = cursor.offset
          exponent()
          val raw    = whole + cursor.slice(exponentStart)
          val number = BigDecimal(raw)
          Operand.Floating(number, raw, number == 0 && raw.startsWith("-"))
        } else Operand.Integer(BigInt(whole))
      }

    private def wordOperand(): Operand = {
      val registerValue = tryRegister()
      registerValue match {
        case Some((reg, selectors)) =>
          if (selectors.isEmpty) reg else Operand.Qualified(reg, OperandSelector.fromSpellings(selectors))
        case None =>
          val name          = identifier()
          val selectors     = dottedSuffixes()
          val base: Operand = Operand.predicate(name).getOrElse(
            if (name.startsWith("SR") || name.startsWith("UR_TID") || name.startsWith("UR_CTAID"))
              Operand.SpecialRegister(SpecialRegister.fromSpelling(name))
            else if (Set("INF", "QNAN", "SNAN", "NAN")(name))
              Operand.FloatingSymbol(FloatingConstant.fromSpelling(name))
            else Operand.Symbol(name)
          )
          if (selectors.isEmpty) base
          else if (name.startsWith("SR_") || name.startsWith("UR_TID") || name.startsWith("UR_CTAID"))
            Operand.SpecialRegister(SpecialRegister.fromSpelling((name +: selectors).mkString(".")))
          else Operand.Qualified(base, OperandSelector.fromSpellings(selectors))
      }
    }

    private def constantBank(): Operand = {
      expect('c')
      expect('[')
      val bank = integerValue(operand())
      expect(']')
      expect('[')
      val offset = attempt(addressExpression()).getOrElse(operand())
      expect(']')
      val value     = Operand.ConstantBank(bank, offset)
      val selectors = dottedSuffixes()
      if (selectors.isEmpty) value else Operand.Qualified(value, OperandSelector.fromSpellings(selectors))
    }

    private def descriptorAddress(): Operand = {
      expect("desc[")
      val descriptor = operand()
      expect(']')
      expect('[')
      val terms = addressTerms()
      expect(']')
      buildAddress(terms, Some(descriptor))
    }

    private def group(): Operand = {
      val values = operandList()
      expect('}')
      Operand.Group(values)
    }

    private def absolute(): Operand = {
      val value = operand()
      expect('|')
      Operand.Unary(value, negative = false, not = false, absolute = true)
    }

    private def address(): Operand = {
      val terms = addressTerms()
      expect(']')
      buildAddress(terms)
    }

    private def addressExpression(): Operand = {
      val (base, selectors) = register()
      val terms             = AddressTerm.Register(base, selectors) +: signedAddressRest()
      buildAddress(terms)
    }

    private enum AddressTerm {
      case Register(value: Operand.Register, suffixes: Vec[String])
      case Integer(value: BigInt)
    }

    private def addressTerms(): Vec[AddressTerm] = {
      val first = addressTerm()
      first +: signedAddressRest()
    }

    private def signedAddressRest(): Vec[AddressTerm] = {
      val terms = Vec.newBuilder[AddressTerm]
      var more  = true
      while (more) {
        val separatorStart = cursor.mark
        cursor.skipWhitespace()
        if (cursor.at('+') || cursor.at('-')) {
          val factor = if (cursor.consume('+')) 1 else { expect('-'); -1 }
          cursor.skipWhitespace()
          val next = addressTerm()
          terms +=
            (next match {
              case register: AddressTerm.Register =>
                if (factor < 0) throw IllegalArgumentException("negative register term in address")
                register
              case AddressTerm.Integer(value) => AddressTerm.Integer(factor * value)
            })
        } else {
          cursor.restore(separatorStart)
          more = false
        }
      }
      terms.result()
    }

    private def addressTerm(): AddressTerm =
      attempt {
        val (value, suffixes) = register()
        AddressTerm.Register(value, suffixes)
      }.getOrElse {
        val sign = if (cursor.consume('+')) 1 else if (cursor.consume('-')) -1 else 1
        AddressTerm.Integer(sign * integerValue(magnitude()))
      }

    private def integerValue(value: Operand): BigInt = value match {
      case Operand.Integer(number) => number
      case Operand.Bits(number, _) => number
      case _                       => fail("integer operand")
    }

    private def buildAddress(terms: Vec[AddressTerm], descriptor: Option[Operand] = None): Operand = {
      val registers = terms.collect { case register: AddressTerm.Register => register }
      val base      = registers.headOption.map(_.value)
      val index     = registers.drop(1).headOption.map(_.value)
      if (registers.size > 2) throw IllegalArgumentException("address has more than two registers")
      if (registers.drop(1).exists(_.suffixes.nonEmpty))
        throw IllegalArgumentException("selectors on an address index register are unsupported")
      val (qualifiers, addressModes) = registers.headOption.toVec.flatMap(_.suffixes).partitionMap { spelling =>
        AddressMode.fromSpelling(spelling) match {
          case AddressMode.Unknown(value) => Left(OperandSelector.fromSpelling(value.raw))
          case mode                       => Right(mode)
        }
      }
      if (addressModes.distinct.size > 1) throw IllegalArgumentException("conflicting address modes")
      val displacement = terms.collect {
        case AddressTerm.Integer(value) => value
      }.sum
      val mode                           = addressModes.headOption
      val scale: Int in (1, 2, 4, 8, 16) = mode.flatMap(AddressMode.scale).getOrElse(1)
      val result                         =
        Operand.Address(base, index, displacement, scale, mode, descriptor)
      if (qualifiers.isEmpty) result else Operand.Qualified(result, qualifiers)
    }
  }
}
