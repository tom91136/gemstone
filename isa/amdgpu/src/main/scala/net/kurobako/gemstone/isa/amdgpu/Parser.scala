package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.Vec.*
import Program.*

import cats.data.Chain
import cats.parse.Parser as P
import cats.syntax.all.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import scala.annotation.tailrec
import scala.util.Try

object Parser extends net.kurobako.gemstone.isa.Decoder[String, Program] {
  override type Error = ParseError

  private def parseError(line: Int, column: Int, message: String): ParseError =
    ParseError(
      line.refineUnsafe[Positive],
      column.refineUnsafe[Positive],
      message
    )

  override def decode(source: String): Either[ParseError, Program] = parse(source)

  def parse(source: String): Either[ParseError, Program] =
    source.linesIterator.zipWithIndex.toVec
      .traverse { case (line, index) => parseLine(line, index + 1) }
      .map(statements => Program(statements.flatten))

  def parseInstruction(source: String): Either[ParseError, Instruction] =
    parse(source).flatMap {
      case Program(Vec(Statement.Instruction(instruction))) => Right(instruction)
      case Program(Vec())                                   => Left(parseError(1, 1, "expected an instruction"))
      case _ => Left(parseError(1, 1, "expected exactly one instruction"))
    }

  private def parseLine(source: String, line: Int): Either[ParseError, Option[Statement]] = {
    val text = stripComment(source).trim
    if (text.isEmpty) Right(None)
    else if (text.endsWith(":")) {
      val name = text.dropRight(1).trim
      if (isIdentifier(name)) Right(Some(Statement.Label(name)))
      else Left(parseError(line, 1, s"invalid label '$name'"))
    } else if (text.startsWith(".")) {
      val end  = text.indexWhere(_.isWhitespace)
      val name = if (end < 0) text else text.take(end)
      val args = if (end < 0) "" else text.drop(end).trim
      if (directiveName.parseAll(name).isRight) Right(Some(Statement.Directive(name, args)))
      else Left(parseError(line, 1, s"invalid directive '$name'"))
    } else {
      text match {
        case s"$left :: $right" =>
          for {
            first  <- parseInstructionLine(left, line)
            second <- parseInstructionLine(right, line)
          } yield Some(Statement.InstructionPair(first, second))
        case _ => parseInstructionLine(text, line).map(value => Some(Statement.Instruction(value)))
      }
    }
  }

  private def parseInstructionLine(text: String, line: Int): Either[ParseError, Instruction] = {
    val opcodeEnd  = text.indexWhere(_.isWhitespace)
    val opcodeText = if (opcodeEnd < 0) text else text.take(opcodeEnd)
    if (opcodeName.parseAll(opcodeText).isLeft) Left(parseError(line, 1, s"invalid AMDGPU opcode '$opcodeText'"))
    else {
      val opcode = Opcode.normalize(opcodeText)
      val tail   = if (opcodeEnd < 0) "" else text.drop(opcodeEnd).trim
      splitTopLevel(tail, ',', line).flatMap { commaParts =>
        commaParts
          .traverse(part => parseWords(splitWhitespace(part), line, text))
          .map(_.flatten.foldLeft((Chain.empty[Operand], Chain.empty[Modifier])) {
            case ((operands, modifiers), parsed) =>
              parsed.fold(
                operand => operands.append(operand) -> modifiers,
                modifier => operands                -> modifiers.append(modifier)
              )
          })
          .flatMap { case (operands, modifiers) =>
            Instruction.decode(opcode, Vec.from(operands.toVector.view), Vec.from(modifiers.toVector.view)).leftMap {
              case Instruction.DecodeError.UnknownOpcode(value) =>
                parseError(line, 1, s"unknown AMDGPU opcode '$value'")
              case Instruction.DecodeError.InvalidOperandCount(value, expected, actual) =>
                parseError(line, 1, s"$value expects ${expected.mkString(" or ")} operands, found $actual")
              case Instruction.DecodeError.AmbiguousForms(value, definitions) =>
                parseError(line, 1, s"$value ambiguously matched ${definitions.mkString(", ")}")
            }
          }
      }
    }
  }

  private def parseWords(
      words: Vec[String],
      line: Int,
      instruction: String
  ): Either[ParseError, Vec[Either[Operand, Modifier]]] =
    words.zipWithIndex.traverse { case (word, wordIndex) =>
      parseModifier(word) match {
        case Some(modifier)        => Right(Right(modifier))
        case None if wordIndex > 0 =>
          val name = if (isIdentifier(word)) word.toLowerCase else word
          ModifierName.either(name)
            .leftMap(_ => parseError(line, instruction.indexOf(word) + 1, s"invalid modifier '$word'"))
            .map(value => Right(Modifier(value)))
        case None => parseOperand(word, line, instruction.indexOf(word) + 1).map(Left(_))
      }
    }

  private def parseOperand(text: String, line: Int, column: Int): Either[ParseError, Operand] =
    if (text.startsWith("-") && text.length > 1 && !looksNumeric(text))
      parseOperand(text.drop(1), line, column + 1).map(Operand.Negated(_))
    else if (text.startsWith("|") && text.endsWith("|") && text.length > 2)
      parseOperand(text.slice(1, text.length - 1), line, column + 1).map(Operand.Absolute(_))
    else parseRegister(text).orElse(parseNumber(text)).orElse(parseAtom(text)) match {
      case Some(value) => Right(value)
      case None        => Left(parseError(line, column, s"invalid operand '$text'"))
    }

  private def parseRegister(text: String): Option[Operand] =
    text.toLowerCase match {
      case s"$prefix[$first:$last]" if digits(first) && digits(last) =>
        for {
          firstValue <- first.toIntOption
          lastValue  <- last.toIntOption
          if firstValue <= lastValue
          file <- registerFile(prefix)
        } yield Operand.Register(file, firstValue, lastValue)
      case value if SpecialRegisters.contains(value) => Some(Operand.SpecialRegister(value))
      case value                                     =>
        RegisterPrefixes.find(value.startsWith(_)).flatMap { prefix =>
          val number = value.drop(prefix.length)
          for {
            file     <- registerFile(prefix)
            register <- number.toIntOption
            if digits(number)
          } yield Operand.Register(file, register, register)
        }
    }

  private def parseNumber(text: String): Option[Operand] = {
    val signed = if (text.startsWith("-") || text.startsWith("+")) text.drop(1) else text
    val sign   = if (text.startsWith("-")) -1 else 1
    if (
      signed.length > 2 && signed.take(2).equalsIgnoreCase("0x") &&
      signed.drop(2).forall(character => Character.digit(character, 16) >= 0)
    )
      Some(Operand.IntegerLiteral(BigInt(signed.drop(2), 16) * sign, 16))
    else if (digits(signed)) Some(Operand.IntegerLiteral(BigInt(signed) * sign, 10))
    else if (text.contains('.') && Try(BigDecimal(text)).isSuccess)
      Some(Operand.DecimalLiteral(text.toLowerCase))
    else None
  }

  private def parseAtom(text: String): Option[Operand] =
    if (isIdentifier(text)) Some(Operand.Identifier(text))
    else if (balanced(text) && text.nonEmpty) Some(Operand.Expression(text))
    else None

  private def parseModifier(text: String): Option[Modifier] = {
    val colon = topLevelColon(text)
    if (colon > 0 && colon < text.length - 1 && isIdentifier(text.take(colon)))
      ModifierValue.either(text.drop(colon + 1)).toOption.map(value =>
        Modifier(ModifierName.applyUnsafe(text.take(colon).toLowerCase), Some(value))
      )
    else if (KnownFlagModifiers.contains(text.toLowerCase))
      Some(Modifier(ModifierName.applyUnsafe(text.toLowerCase)))
    else None
  }

  private def splitTopLevel(text: String, separator: Char, line: Int): Either[ParseError, Vec[String]] = {
    @tailrec
    def loop(index: Int, start: Int, round: Int, square: Int, result: Chain[String]): Either[ParseError, Vec[String]] =
      if (index == text.length) {
        val last = text.drop(start).trim
        if (round != 0 || square != 0) Left(parseError(line, text.length, "unclosed delimiter"))
        else if (last.isEmpty) Left(parseError(line, text.length, "empty operand"))
        else Right(Vec.from(result.append(last).toVector.view))
      } else {
        val (nextRound, nextSquare) = text(index) match {
          case '(' => round + 1 -> square
          case ')' => round - 1 -> square
          case '[' => round     -> (square + 1)
          case ']' => round     -> (square - 1)
          case _   => round     -> square
        }
        if (nextRound < 0 || nextSquare < 0)
          Left(parseError(line, index + 1, "unmatched closing delimiter"))
        else if (text(index) == separator && nextRound == 0 && nextSquare == 0) {
          val part = text.slice(start, index).trim
          if (part.isEmpty) Left(parseError(line, index + 1, "empty operand"))
          else loop(index + 1, index + 1, nextRound, nextSquare, result.append(part))
        } else loop(index + 1, start, nextRound, nextSquare, result)
      }

    if (text.isEmpty) Right(Vec.empty) else loop(0, 0, 0, 0, Chain.empty)
  }

  private def splitWhitespace(text: String): Vec[String] = {
    @tailrec
    def loop(
        index: Int,
        start: Option[Int],
        round: Int,
        square: Int,
        result: Chain[String]
    ): Chain[String] = {
      val character               = if (index == text.length) ' ' else text(index)
      val nextStart               = start.orElse(Option.when(!character.isWhitespace)(index))
      val (nextRound, nextSquare) = character match {
        case '(' => round + 1 -> square
        case ')' => round - 1 -> square
        case '[' => round     -> (square + 1)
        case ']' => round     -> (square - 1)
        case _   => round     -> square
      }
      val completedAt = nextStart.filter(_ => character.isWhitespace && nextRound == 0 && nextSquare == 0)
      val nextResult  = completedAt.fold(result)(start => result.append(text.slice(start, index)))
      if (index == text.length) nextResult
      else loop(index + 1, if (completedAt.nonEmpty) None else nextStart, nextRound, nextSquare, nextResult)
    }

    Vec.from(loop(0, None, 0, 0, Chain.empty).toVector.view)
  }

  private def stripComment(source: String): String = {
    val slash = source.indexOf("//")
    val hash  = source.indexOf('#')
    val end   = Vec(slash, hash).filter(_ >= 0).minOption.getOrElse(source.length)
    source.take(end)
  }

  private def topLevelColon(text: String): Int = {
    @tailrec
    def loop(index: Int, square: Int, round: Int): Int =
      if (index == text.length) -1
      else text(index) match {
        case '['                              => loop(index + 1, square + 1, round)
        case ']'                              => loop(index + 1, square - 1, round)
        case '('                              => loop(index + 1, square, round + 1)
        case ')'                              => loop(index + 1, square, round - 1)
        case ':' if square == 0 && round == 0 => index
        case _                                => loop(index + 1, square, round)
      }
    loop(0, 0, 0)
  }

  private def balanced(text: String): Boolean = {
    val (round, square) = text.foldLeft(0 -> 0) {
      case ((round, square), '(') => round + 1 -> square
      case ((round, square), ')') => round - 1 -> square
      case ((round, square), '[') => round     -> (square + 1)
      case ((round, square), ']') => round     -> (square - 1)
      case (state, _)             => state
    }
    round == 0 && square == 0
  }

  private def looksNumeric(text: String): Boolean =
    text.dropWhile(char => char == '+' || char == '-').headOption.exists(_.isDigit)
  private def isIdentifier(text: String): Boolean = identifier.parseAll(text).isRight

  private def digits(value: String): Boolean = value.nonEmpty && value.forall(_.isDigit)

  private def registerFile(prefix: String): Option[RegisterFile] = prefix match {
    case "s"    => Some(RegisterFile.Scalar)
    case "v"    => Some(RegisterFile.VectorRegister)
    case "a"    => Some(RegisterFile.Accumulator)
    case "ttmp" => Some(RegisterFile.TrapTemporary)
    case _      => None
  }

  private val RegisterPrefixes = Vec("ttmp", "s", "v", "a")

  private val identifierStart = P.charWhere(character => character.isLetter || character == '_' || character == '$')
  private val identifierPart  = P.charWhere(character => character.isLetterOrDigit || "_.$".contains(character))
  private val identifier      = (identifierStart ~ identifierPart.rep0).string
  private val opcodeName      =
    (P.charWhere(_.isLetter) ~ P.charWhere(character =>
      character.isLetterOrDigit || "_.".contains(character)
    ).rep0).string
  private val directiveName = P.char('.') *>
    (P.charWhere(character =>
      character.isLetter || character == '_'
    ) ~ P.charWhere(character => character.isLetterOrDigit || "_.".contains(character)).rep0).string

  private val KnownFlagModifiers = Set(
    "a16",
    "addr64",
    "clamp",
    "d16",
    "da",
    "dlc",
    "done",
    "dpp",
    "dpp8",
    "e32",
    "e64",
    "gds",
    "glc",
    "idxen",
    "lds",
    "lwe",
    "offen",
    "r128",
    "sc0",
    "sc1",
    "slc",
    "sdwa",
    "tfe",
    "unorm",
    "vm"
  )

  private val SpecialRegisters = Set(
    "exec",
    "exec_hi",
    "exec_lo",
    "flat_scratch",
    "flat_scratch_hi",
    "flat_scratch_lo",
    "m0",
    "null",
    "scc",
    "src_execz",
    "src_scc",
    "src_vccz",
    "vcc",
    "vcc_hi",
    "vcc_lo",
    "xnack_mask",
    "xnack_mask_hi",
    "xnack_mask_lo"
  )
}
