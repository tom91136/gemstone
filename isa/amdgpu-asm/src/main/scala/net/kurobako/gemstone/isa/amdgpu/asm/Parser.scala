package net.kurobako.gemstone.isa.amdgpu.asm

import cats.data.Chain
import cats.syntax.all.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.*
import net.kurobako.gemstone.isa.amdgpu.asm.Program.*
import net.kurobako.gemstone.string.*
import net.kurobako.gemstone.text.*
import scala.annotation.tailrec
import scala.util.Try

object Parser {
  def parse(source: String): Either[SourceError, Program] =
    source.linesIterator.zipWithIndex.toVec
      .traverse { case (line, index) =>
        val end     = commentOffset(line)
        val comment = line.drop(end).refine[NonEmpty[String]].toOption.map(Statement.Comment(_))
        parseLine(line.take(end), (index + 1).unsafe[Int > 0]).map(statement =>
          Vec.from(statement.toSeq ++ comment.toSeq)
        )
      }
      .map(statements => Program(statements.flatten))

  private def error(line: Int > 0, offset: Int, message: NonEmpty[String]): SourceError =
    SourceError(SourcePosition(line, (offset + 1).unsafe[Int > 0]), message)

  private def parseLine(source: String, line: Int > 0): Either[SourceError, Option[Statement]] = {
    val start = source.indexWhere(_ > ' ')
    val text  = source.trim
    if (text.isEmpty) Right(None)
    else if (text.endsWith(":")) {
      val name = text.dropRight(1).trim
      if (isIdentifier(name)) Right(Some(Statement.Label(name)))
      else Left(error(line, start, s"invalid label '$name'"))
    } else if (text.startsWith(".")) {
      val end  = text.indexWhere(_.isWhitespace)
      val name = if (end < 0) text else text.take(end)
      val args = if (end < 0) "" else text.drop(end).trim
      if (isDirective(name)) Right(Some(Statement.Directive(name, args)))
      else Left(error(line, start, s"invalid directive '$name'"))
    } else {
      text match {
        case s"$left :: $right" =>
          for {
            first  <- parseInstructionLine(left, start, line)
            second <- parseInstructionLine(right, start + left.length + 4, line)
          } yield Some(Statement.InstructionPair(first, second))
        case _ => parseInstructionLine(text, start, line).map(value => Some(Statement.Instruction(value)))
      }
    }
  }

  private def parseInstructionLine(text: String, offset: Int, line: Int > 0): Either[SourceError, Instruction] = {
    val opcodeEnd  = text.indexWhere(_.isWhitespace)
    val opcodeText = if (opcodeEnd < 0) text else text.take(opcodeEnd)
    if (!isOpcode(opcodeText)) Left(error(line, offset, s"invalid AMDGPU opcode '$opcodeText'"))
    else {
      val opcode    = opcodeText.toLowerCaseRoot
      val tailStart = if (opcodeEnd < 0) text.length else opcodeEnd
      operandWords(text.drop(tailStart), offset + tailStart, line).flatMap { parts =>
        parts
          .traverse(parseWords(_, line))
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
                error(line, offset, s"unknown AMDGPU opcode '$value'")
              case Instruction.DecodeError.InvalidOperandCount(value, expected, actual) =>
                error(line, offset, s"$value expects ${expected.mkString(" or ")} operands, found $actual")
              case Instruction.DecodeError.AmbiguousForms(value, definitions) =>
                error(line, offset, s"$value ambiguously matched ${definitions.mkString(", ")}")
            }
          }
      }
    }
  }

  /** Comma-separated operand groups, each split into whitespace-separated words with their line offsets. */
  private def operandWords(text: String, offset: Int, line: Int > 0): Either[SourceError, Vec[Vec[(Int, String)]]] =
    if (text.trim.isEmpty) Right(Vec.empty)
    else split(text, offset, line, _ == ',').flatMap(_.traverse { (start, part) =>
      Delimited.splitTopLevel(part, _.isWhitespace)
        .leftMap(unbalanced => error(line, start + unbalanced.offset.raw, unbalanced.message))
        .map(_.filter(_.text.nonEmpty).map(word => (start + word.offset.raw, word.text)))
    })

  /** Top-level parts with their line offsets after leading whitespace; empty parts are rejected. */
  private def split(
      text: String,
      offset: Int,
      line: Int > 0,
      separator: Char => Boolean
  ): Either[SourceError, Vec[(Int, String)]] =
    Delimited.splitTopLevel(text, separator)
      .leftMap(unbalanced => error(line, offset + unbalanced.offset.raw, unbalanced.message))
      .flatMap(_.traverse { segment =>
        val start = segment.text.indexWhere(_ > ' ')
        val at    = offset + segment.offset.raw
        if (start < 0) Left(error(line, at, "empty operand")) else Right((at + start, segment.text.trim))
      })

  private def parseWords(
      words: Vec[(Int, String)],
      line: Int > 0
  ): Either[SourceError, Vec[Either[Operand, Modifier]]] =
    words.zipWithIndex.traverse { case ((offset, word), wordIndex) =>
      parseModifier(word) match {
        case Some(modifier)        => Right(Right(modifier))
        case None if wordIndex > 0 =>
          val name = if (isIdentifier(word)) word.toLowerCaseRoot else word
          name.refine[NonEmpty[String]]
            .leftMap(_ => error(line, offset, s"invalid modifier '$word'"))
            .map(value => Right(Modifier(value)))
        case None => parseOperand(word, offset, line).map(Left(_))
      }
    }

  private def parseOperand(text: String, offset: Int, line: Int > 0): Either[SourceError, Operand] =
    if (text.startsWith("-") && text.length > 1 && !looksNumeric(text))
      parseOperand(text.drop(1), offset + 1, line).map(Operand.Negated(_))
    else if (text.startsWith("|") && text.endsWith("|") && text.length > 2)
      parseOperand(text.slice(1, text.length - 1), offset + 1, line).map(Operand.Absolute(_))
    else if (text.startsWith("neg(") && text.endsWith(")"))
      parseOperand(text.slice(4, text.length - 1), offset + 4, line).map(Operand.Negated(_))
    else if (text.startsWith("sext(") && text.endsWith(")"))
      parseOperand(text.slice(5, text.length - 1), offset + 5, line).map(Operand.SignExtended(_))
    else if (text.startsWith("[") && text.endsWith("]")) {
      val inner = text.slice(1, text.length - 1)
      (if (inner.trim.isEmpty) Right(Vec.empty[(Int, String)]) else split(inner, offset + 1, line, _ == ','))
        .flatMap(_.traverse((start, part) => parseOperand(part, start, line))).map { values =>
          if (
            values.nonEmpty && values.forall {
              case Operand.Register(_, _, _) => true
              case _                         => false
            }
          ) Operand.RegisterList(values)
          else Operand.Expression(text)
        }
    } else parseRegister(text.dropRight(2)).filter(_ => text.endsWith(".l") || text.endsWith(".h"))
      .map(Operand.Half(
        _,
        text.endsWith(".h")
      )).orElse(parseRegister(text)).orElse(parseNumber(text)).orElse(parseAtom(text)) match {
      case Some(value) => Right(value)
      case None        => Left(error(line, offset, s"invalid operand '$text'"))
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
    else if (text.nonEmpty && Delimited.splitTopLevel(text, _ => false).isRight) Some(Operand.Expression(text))
    else None

  private def parseModifier(text: String): Option[Modifier] =
    Delimited.splitTopLevel(text, _ == ':').toOption.collect {
      case Vec(name, value, rest*) if isIdentifier(name.text) && (value.text.nonEmpty || rest.nonEmpty) =>
        Modifier(
          name.text.toLowerCaseRoot.unsafe[NonEmpty[String]],
          Some(text.drop(name.text.length + 1).unsafe[NonEmpty[String]])
        )
    }.orElse(Option.when(KnownFlagModifiers.contains(text.toLowerCaseRoot))(
      Modifier(text.toLowerCaseRoot.unsafe[NonEmpty[String]])
    ))

  private def commentOffset(source: String): Int = {
    @tailrec
    def loop(index: Int, quoted: Boolean, escaped: Boolean): Int =
      if (index >= source.length) source.length
      else if (escaped) loop(index + 1, quoted, false)
      else if (quoted && source(index) == '\\') loop(index + 1, quoted, true)
      else if (source(index) == '"') loop(index + 1, !quoted, false)
      else if (!quoted && (source(index) == '#' || source.startsWith("//", index))) index
      else loop(index + 1, quoted, false)
    loop(0, quoted = false, escaped = false)
  }

  private def looksNumeric(text: String): Boolean =
    text.dropWhile(char => char == '+' || char == '-').headOption.exists(_.isDigit)
  private def isIdentifier(text: String): Boolean =
    text.headOption.exists(character => character.isLetter || character == '_' || character == '$') &&
      text.forall(character => character.isLetterOrDigit || "_.$".contains(character))

  private def isOpcode(text: String): Boolean =
    text.headOption.exists(_.isLetter) &&
      text.forall(character => character.isLetterOrDigit || "_.".contains(character))

  private def isDirective(text: String): Boolean =
    text.lift(1).exists(character => character.isLetter || character == '_') && text.startsWith(".") &&
      text.drop(1).forall(character => character.isLetterOrDigit || "_.".contains(character))

  private def digits(value: String): Boolean = value.nonEmpty && value.forall(_.isDigit)

  private def registerFile(prefix: String): Option[RegisterFile] = prefix match {
    case "s"    => Some(RegisterFile.Scalar)
    case "v"    => Some(RegisterFile.VectorRegister)
    case "a"    => Some(RegisterFile.Accumulator)
    case "ttmp" => Some(RegisterFile.TrapTemporary)
    case _      => None
  }

  private val RegisterPrefixes = Vec("ttmp", "s", "v", "a")

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
