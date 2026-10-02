package net.kurobako.gemstone.isa.amdgpu.asm

import cats.data.Chain
import cats.parse.Parser as P
import cats.syntax.all.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.amdgpu.*
import net.kurobako.gemstone.isa.amdgpu.asm.Program.*
import net.kurobako.gemstone.string.*
import scala.annotation.tailrec
import scala.util.Try

object Parser extends Decoder[String, Program, Parser.Error] {
  case class Error(
      line: Int > 0,
      column: Int > 0,
      message: NonEmpty[String]
  ) {
    override def toString: String = s"$line:$column: $message"
  }

  override def decode(source: String): Either[Error, Program] = parse(source)

  def parse(source: String): Either[Error, Program] =
    source.linesIterator.zipWithIndex.toVec
      .traverse { case (line, index) =>
        val end     = commentOffset(line)
        val comment = line.drop(end).refine[NonEmpty[String]].toOption.map(Statement.Comment(_))
        parseLine(line.take(end), (index + 1).unsafe[Int > 0]).map(statement =>
          Vec.from(statement.toSeq ++ comment.toSeq)
        )
      }
      .map(statements => Program(statements.flatten))

  def parseInstruction(source: String): Either[Error, Instruction] =
    parse(source).map(program =>
      Program(program.statements.filter {
        case Statement.Comment(_) => false
        case _                    => true
      })
    ).flatMap {
      case Program(Vec(Statement.Instruction(instruction))) => Right(instruction)
      case Program(Vec())                                   => Left(Error(1, 1, "expected an instruction"))
      case _                                                => Left(Error(1, 1, "expected exactly one instruction"))
    }

  private def parseLine(source: String, line: Int > 0): Either[Error, Option[Statement]] = {
    val text = source.trim
    if (text.isEmpty) Right(None)
    else if (text.endsWith(":")) {
      val name = text.dropRight(1).trim
      if (isIdentifier(name)) Right(Some(Statement.Label(name)))
      else Left(Error(line, 1, s"invalid label '$name'"))
    } else if (text.startsWith(".")) {
      val end  = text.indexWhere(_.isWhitespace)
      val name = if (end < 0) text else text.take(end)
      val args = if (end < 0) "" else text.drop(end).trim
      if (directiveName.parseAll(name).isRight) Right(Some(Statement.Directive(name, args)))
      else Left(Error(line, 1, s"invalid directive '$name'"))
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

  private def parseInstructionLine(text: String, line: Int > 0): Either[Error, Instruction] = {
    val opcodeEnd  = text.indexWhere(_.isWhitespace)
    val opcodeText = if (opcodeEnd < 0) text else text.take(opcodeEnd)
    if (opcodeName.parseAll(opcodeText).isLeft) Left(Error(line, 1, s"invalid AMDGPU opcode '$opcodeText'"))
    else {
      val opcode = opcodeText.toLowerCaseRoot
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
                Error(line, 1, s"unknown AMDGPU opcode '$value'")
              case Instruction.DecodeError.InvalidOperandCount(value, expected, actual) =>
                Error(line, 1, s"$value expects ${expected.mkString(" or ")} operands, found $actual")
              case Instruction.DecodeError.AmbiguousForms(value, definitions) =>
                Error(line, 1, s"$value ambiguously matched ${definitions.mkString(", ")}")
            }
          }
      }
    }
  }

  private def parseWords(
      words: Vec[String],
      line: Int > 0,
      instruction: String
  ): Either[Error, Vec[Either[Operand, Modifier]]] =
    words.zipWithIndex.traverse { case (word, wordIndex) =>
      val column = (instruction.indexOf(word) + 1).unsafe[Int > 0]
      parseModifier(word) match {
        case Some(modifier)        => Right(Right(modifier))
        case None if wordIndex > 0 =>
          val name = if (isIdentifier(word)) word.toLowerCaseRoot else word
          name.refine[NonEmpty[String]]
            .leftMap(_ => Error(line, column, s"invalid modifier '$word'"))
            .map(value => Right(Modifier(value)))
        case None => parseOperand(word, line, column).map(Left(_))
      }
    }

  private def parseOperand(text: String, line: Int > 0, column: Int > 0): Either[Error, Operand] =
    if (text.startsWith("-") && text.length > 1 && !looksNumeric(text))
      parseOperand(text.drop(1), line, column + 1).map(Operand.Negated(_))
    else if (text.startsWith("|") && text.endsWith("|") && text.length > 2)
      parseOperand(text.slice(1, text.length - 1), line, column + 1).map(Operand.Absolute(_))
    else if (text.startsWith("neg(") && text.endsWith(")"))
      parseOperand(text.slice(4, text.length - 1), line, column + 4).map(Operand.Negated(_))
    else if (text.startsWith("sext(") && text.endsWith(")"))
      parseOperand(text.slice(5, text.length - 1), line, column + 5).map(Operand.SignExtended(_))
    else if (text.startsWith("[") && text.endsWith("]"))
      splitTopLevel(text.slice(1, text.length - 1), ',', line)
        .flatMap(_.traverse(parseOperand(_, line, column + 1))).map { values =>
          if (
            values.nonEmpty && values.forall {
              case Operand.Register(_, _, _) => true
              case _                         => false
            }
          ) Operand.RegisterList(values)
          else Operand.Expression(text)
        }
    else parseRegister(text.dropRight(2)).filter(_ => text.endsWith(".l") || text.endsWith(".h"))
      .map(Operand.Half(
        _,
        text.endsWith(".h")
      )).orElse(parseRegister(text)).orElse(parseNumber(text)).orElse(parseAtom(text)) match {
      case Some(value) => Right(value)
      case None        => Left(Error(line, column, s"invalid operand '$text'"))
    }

  private def parseRegister(text: String): Option[Operand] =
    text.toLowerCaseRoot match {
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

  private def parseNumber(text: String): Option[Operand] =
    parseBigIntLiteral(text).map(value =>
      Operand.IntegerLiteral(value, if (text.toLowerCaseRoot.contains("0x")) 16 else 10)
    )
      .orElse(Option.when(
        text.contains('.') && Try(BigDecimal(text)).isSuccess
      )(Operand.DecimalLiteral(text.toLowerCaseRoot)))

  private def parseAtom(text: String): Option[Operand] =
    if (isIdentifier(text)) Some(Operand.Identifier(text))
    else if (balanced(text) && text.nonEmpty) Some(Operand.Expression(text))
    else None

  private def parseModifier(text: String): Option[Modifier] = {
    val colon = topLevelColon(text)
    if (colon > 0 && colon < text.length - 1 && isIdentifier(text.take(colon)))
      text.drop(colon + 1).refine[NonEmpty[String]].toOption.map(value =>
        Modifier(text.take(colon).toLowerCaseRoot.unsafe[NonEmpty[String]], Some(value))
      )
    else if (KnownFlagModifiers.contains(text.toLowerCaseRoot))
      Some(Modifier(text.toLowerCaseRoot.unsafe[NonEmpty[String]]))
    else None
  }

  private def splitTopLevel(text: String, separator: Char, line: Int > 0): Either[Error, Vec[String]] =
    if (text.isEmpty) Right(Vec.empty)
    else {
      val lastColumn = text.length.unsafe[Int > 0]
      @tailrec
      def loop(
          column: Int > 0,
          start: Int,
          closing: List[Char],
          result: Chain[String]
      ): Either[Error, Vec[String]] = {
        val index = column.raw - 1
        if (index == text.length) {
          val last = text.drop(start).trim
          if (closing.nonEmpty) Left(Error(line, lastColumn, "unclosed delimiter"))
          else if (last.isEmpty) Left(Error(line, lastColumn, "empty operand"))
          else Right(Vec.from(result.append(last).toVector.view))
        } else text(index) match {
          case '('             => loop(column + 1, start, ')' :: closing, result)
          case '{'             => loop(column + 1, start, '}' :: closing, result)
          case '['             => loop(column + 1, start, ']' :: closing, result)
          case ')' | ']' | '}' => closing match {
              case expected :: rest if expected == text(index) => loop(column + 1, start, rest, result)
              case _ => Left(Error(line, column, "unmatched closing delimiter"))
            }
          case character if character == separator && closing.isEmpty =>
            val part = text.slice(start, index).trim
            if (part.isEmpty) Left(Error(line, column, "empty operand"))
            else loop(column + 1, index + 1, closing, result.append(part))
          case _ => loop(column + 1, start, closing, result)
        }
      }
      loop(1, 0, Nil, Chain.empty)
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
        case '(' | '{' => round + 1 -> square
        case ')' | '}' => round - 1 -> square
        case '['       => round     -> (square + 1)
        case ']'       => round     -> (square - 1)
        case _         => round     -> square
      }
      val completedAt = nextStart.filter(_ => character.isWhitespace && nextRound == 0 && nextSquare == 0)
      val nextResult  = completedAt.fold(result)(start => result.append(text.slice(start, index)))
      if (index == text.length) nextResult
      else loop(index + 1, if (completedAt.nonEmpty) None else nextStart, nextRound, nextSquare, nextResult)
    }

    Vec.from(loop(0, None, 0, 0, Chain.empty).toVector.view)
  }

  private def commentOffset(source: String): Int = {
    @tailrec
    def loop(index: Int, quoted: Boolean, escaped: Boolean): Int =
      if (index >= source.length) source.length
      else if (escaped) loop(index + 1, quoted, false)
      else if (quoted && source(index) == '\\') loop(index + 1, quoted, true)
      else if (source(index) == '"') loop(index + 1, !quoted, false)
      else if (!quoted && (source(index) == '#' || source.startsWith("//", index))) index
      else loop(index + 1, quoted, false)
    loop(0, false, false)
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
    "compr",
    "row_en",
    "nv",
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
    "src_lds_direct",
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
