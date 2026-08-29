package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.Col.*

import cats.parse.Parser as P
import cats.parse.Parser0 as P0
import cats.syntax.all.*

import scala.reflect.ClassTag

/** Parser for PTX modules, declarations, functions, entries, instructions, and structured operands. */
object Parser {
  private val whitespaceCharacter: P[Unit] =
    P.charWhere(character => character == ' ' || character == '\t' || character == '\r' || character == '\n').void

  private val lineComment: P[Unit] =
    P.string("//") *> P.charsWhile0(character => character != '\n' && character != '\r').void

  private val blockComment: P[Unit] =
    P.string("/*") *> P.until(P.string("*/")).void <* P.string("*/")

  private val skipped: P0[Unit] = (whitespaceCharacter | lineComment | blockComment).rep0.void

  private def lexeme[A](parser: P[A]): P[A] = (skipped.with1 *> parser) <* skipped

  private val identifierCharacter: P[Char] =
    P.charWhere(character => character.isLetterOrDigit || character == '_' || character == '$')

  private val identifierStart: P[Char] =
    P.charWhere(character => character.isLetter || character == '_' || character == '$')

  private val identifier: P[String]          = lexeme((identifierStart *> identifierCharacter.rep0).void.string)
  private val segmentPart: P[String]         = lexeme(identifierCharacter.rep.void.string)
  private val adjacentSegmentPart: P[String] = identifierCharacter.rep.void.string

  private val segment: P[String] = (segmentPart ~ (lexeme(P.string("::")) *> segmentPart).rep0).map {
    case (head, tail) =>
      head + tail.map("::" + _).mkString
  }

  private val adjacentSegment: P[String] = (adjacentSegmentPart ~ (P.string("::") *> adjacentSegmentPart).rep0).map {
    case (head, tail) =>
      head + tail.map("::" + _).mkString
  }

  private def looksLikeRegisterPart(value: String): Boolean =
    value.nonEmpty && "bhsfup".contains(value.head.toLower) &&
      value.tail.forall(character => character.isLetterOrDigit || character == '_') && value.length <= 5

  private val registerToken: P[(String, Option[String])] = lexeme {
    ((P.char('%') *> (segmentPart ~ (P.string(".") *> adjacentSegmentPart).rep0)) ~
      (P.char('<') *> P.charsWhile(_.isDigit) <* P.char('>')).string.backtrack.?).map {
      case ((head, tail), range) =>
        val segments          = (head +: tail).toCol
        val registerPartIndex = segments.indexWhere(looksLikeRegisterPart)
        val (nameParts, part) =
          if (registerPartIndex > 0 && registerPartIndex == segments.size - 1)
            (segments.take(registerPartIndex), Some(segments.last))
          else (segments, None)
        ("%" + nameParts.mkString(".") + range.getOrElse(""), part)
    }
  }

  private val register: P[Operand.Register] =
    registerToken.map { case (name, part) => Operand.Register(name, part) }

  private val hexadecimalDigit: Char => Boolean = character => character.isDigit || "abcdefABCDEF".contains(character)
  private val decimalDigit: P[Char]             = P.charWhere(_.isDigit)
  private val exponent: P[Unit]                 = (P.charIn("eE") *> P.charIn("+-").? *> decimalDigit.rep).void

  private val number: P[String] = lexeme {
    val prefixed = (P.char('0') *> P.charIn("xXbBfFdDeE") *> P.charsWhile(hexadecimalDigit)).void
    val decimal  = (decimalDigit.rep ~ (P.char('.') *> decimalDigit.rep).? ~ exponent.?).void
    val leadDot  = (P.char('.') *> decimalDigit.rep ~ exponent.?).void
    (P.charIn("+-").?.with1 ~ (prefixed.backtrack | decimal.backtrack | leadDot.backtrack)).void.string
  }

  private val stringLiteral: P[Operand.StringLiteral] = lexeme {
    val escaped = (P.char('\\') ~ P.anyChar).string
    val plain   =
      P.charWhere(character => character != '"' && character != '\\' && character != '\n' && character != '\r')
        .map(_.toString)
    P.char('"') *> (escaped.backtrack | plain).rep0.map(_.mkString) <* P.char('"')
  }.map(Operand.StringLiteral.apply)

  private val comma: P[Unit]     = lexeme(P.char(','))
  private val semicolon: P[Unit] = lexeme(P.char(';'))

  private def separated0[A: ClassTag](parser: P0[A], separator: P[Unit]): P0[Col[A]] =
    (parser ~ (separator *> parser).rep0).map { case (head, tail) => (head +: tail).toCol }.backtrack.?
      .map(_.getOrElse(Col.empty[A]))

  private def enclosed(open: Char, close: Char, item: P[Operand]): P[Col[Operand]] =
    (P.char(open) *> separated0(item.backtrack.?, comma) <* lexeme(P.char(close))).map(_.flatten)

  private val qualifier: P[Operand.Qualifier] = (lexeme(P.char('.')) *> segment).map(value =>
    Operand.Qualifier(Col(value))
  )

  val operand: P[Operand] = P.recursive { recursive =>
    lazy val atom: P[Operand] =
      stringLiteral.backtrack
        .orElse(enclosed('{', '}', recursive).map(Operand.VectorLiteral.apply).backtrack)
        .orElse(enclosed('(', ')', recursive).map(Operand.Parenthesized.apply).backtrack)
        .orElse(enclosed('[', ']', recursive).map(Operand.Address.apply).backtrack)
        .orElse(register.backtrack)
        .orElse(number.map(Operand.Immediate.apply).backtrack)
        .orElse(qualifier.backtrack)
        .orElse(identifier.map(Operand.Symbol.apply).backtrack)

    lazy val partSuffix: P[Operand => Operand] = (lexeme(P.char('.')) *> segmentPart)
      .filter(looksLikeRegisterPart)
      .map { part => (value: Operand) =>
        value match {
          case Operand.Register(name, None) => Operand.Register(name, Some(part))
          case Operand.Symbol(name)         => Operand.Register(name, Some(part))
          case other                        => other
        }
      }
      .backtrack

    lazy val indexed: P[Operand] = atom.flatMap { base =>
      val canTakePart = base match {
        case Operand.Register(_, None) => true
        case Operand.Symbol(_)         => true
        case _                         => false
      }
      val suffixes = if (canTakePart) partSuffix.rep0.map(_.toCol) else P.pure(Col.empty[Operand => Operand])
      suffixes.flatMap { functions =>
        val suffixed = functions.foldLeft(base)((value, function) => function(value))
        enclosed('[', ']', recursive).backtrack.?.map {
          case Some(Col(single)) => Operand.Index(suffixed, single)
          case _                 => suffixed
        }
      }
    }

    lazy val negated: P[Operand] = (lexeme(P.char('-')) *> indexed).map(Operand.Negate.apply).backtrack

    lazy val additive: P[Operand] =
      (negated.orElse(indexed) ~ (lexeme(P.charIn("+-")) ~ negated.orElse(indexed)).rep0).map {
        case (head, tail) =>
          tail.foldLeft(head) { case (left, (operator, right)) =>
            if (operator == '+') Operand.Add(left, right) else Operand.Subtract(left, right)
          }
      }

    (additive ~ (lexeme(P.char('|')) *> additive).backtrack.?).map {
      case (base, Some(predicate)) => Operand.WithPredicate(base, predicate)
      case (base, None)            => base
    }
  }

  private val opcode: P[(String, Col[Modifier])] = ((segment ~ (P.char('.') *> adjacentSegment).rep0) <* skipped).map {
    case (mnemonic, modifiers) =>
      mnemonic -> modifiers.iterator.map(Modifier.apply).toCol
  }

  private val strictOperands: P0[Col[Operand]] = separated0(operand.backtrack, comma)

  private val dottedSegment: P[String] =
    (identifierCharacter.rep ~ (P.string("::") *> identifierCharacter.rep).rep0).string.backtrack

  private val dottedPath: P[Col[String]] =
    (lexeme(P.char('.')) *> (dottedSegment ~ (P.char('.') *> dottedSegment).rep0)).map { case (head, tail) =>
      (head +: tail).toCol
    }

  private val parameterDeclaration: P[Parameter] =
    ((dottedPath.backtrack | number.backtrack.map(Col(_))).rep ~ (registerToken.map(_._1).backtrack | identifier))
      .flatMap { case (qualifiers, name) =>
        enclosed('[', ']', operand).backtrack.rep0.map { dimensions =>
          Parameter(qualifiers.iterator.flatten.toCol, name, dimensions.iterator.flatten.toCol)
        }
      }

  private val parameterList: P[Col[Parameter]] =
    P.char('(') *> separated0(parameterDeclaration.backtrack, comma) <* lexeme(P.char(')'))

  private val predication: P0[Predication] =
    (lexeme(P.char('@')) *> lexeme(P.char('!')).as(true).backtrack.? ~ operand.backtrack).map {
      case (negated, value) => Predication(negated.getOrElse(false), value)
    }

  private val directiveArguments: P0[Col[DirectiveArgument]] = {
    val first         = operand.backtrack.map(value => DirectiveArgument(DirectiveSeparator.Space, value))
    val commaArgument = (comma *> operand.backtrack)
      .map(value => DirectiveArgument(DirectiveSeparator.Comma, value))
    val spaceArgument = operand.backtrack
      .map(value => DirectiveArgument(DirectiveSeparator.Space, value))
    val next: P[DirectiveArgument] = commaArgument.backtrack.orElse(spaceArgument)
    (first ~ next.rep0).map { case (head, tail) => (head +: tail).toCol }.backtrack.?
      .map(_.getOrElse(Col.empty[DirectiveArgument]))
  }

  private def fixedLineDirective(name: String): P[Statement] =
    ((dottedPath.filter(path => path.size == 1 && path.head == name) ~ operand) <* semicolon.backtrack.?).map {
      case (path, value) =>
        Statement.Directive(
          path,
          Col(DirectiveArgument(DirectiveSeparator.Space, value)),
          DirectiveTerminator.Line
        )
    }

  private val targetDirective: P[Statement] =
    ((dottedPath.filter(path => path.size == 1 && path.head == "target") ~
      (identifier.map(Operand.Symbol.apply) ~
        (comma *> identifier.map(Operand.Symbol.apply)).rep0).map { case (head, tail) =>
        (head +: tail).toCol
      }) <*
      semicolon.backtrack.?).map {
      case (path, values) =>
        val arguments = values.zipWithIndex.map { case (value, index) =>
          val separator = if (index == 0) DirectiveSeparator.Space else DirectiveSeparator.Comma
          DirectiveArgument(separator, value)
        }
        Statement.Directive(path, arguments, DirectiveTerminator.Line)
    }

  private val lineDirective: P[Statement] =
    fixedLineDirective("version").backtrack
      .orElse(targetDirective.backtrack)
      .orElse(fixedLineDirective("address_size"))

  private val directive: P[Statement] = (dottedPath ~ directiveArguments).flatMap { case (path, arguments) =>
    semicolon.map(_ => Statement.Directive(path, arguments, DirectiveTerminator.Semicolon))
  }

  private val label: P[Statement] = (identifier <* lexeme(P.char(':'))).map(Statement.Label.apply)

  private val instruction: P[Statement] = (predication.backtrack.?.with1 ~ opcode).flatMap {
    case (predicate, (operation, modifiers)) =>
      (strictOperands ~ semicolon.backtrack.?).mapFilter { case (operands, _) =>
        Instruction.decode(operation, operands, modifiers, predicate).toOption.map(Statement.Instruction.apply)
      }
  }

  private val functionDirective: P[Unit] = {
    val names = Set("maxntid", "reqntid", "minnctapersm", "maxnreg", "maxclusterrank", "explicitcluster")
    (dottedPath.filter(path => path.size == 1 && names.contains(path.head)) ~ directiveArguments).void
  }

  private def entryOrFunction(self: P[Statement]): P[Statement] =
    for {
      _       <- lexeme(P.char('.'))
      visible <- (lexeme(P.string("visible")) *> lexeme(P.char('.'))).as(true).backtrack.?
      isEntry <- lexeme(P.string("entry")).as(true).backtrack | lexeme(P.string("func")).as(false)
      returns <-
        if (isEntry) P.pure(Col.empty[Parameter])
        else parameterList.backtrack.?.map(_.getOrElse(Col.empty[Parameter]))
      name       <- identifier
      parameters <- parameterList.backtrack.?.map(_.getOrElse(Col.empty[Parameter]))
      _          <- functionDirective.backtrack.rep0
      body       <- (lexeme(P.char('{')) *> (self.rep0 <* lexeme(P.char('}')))).map(_.toCol).backtrack.?
      _          <- if (!isEntry && body.isEmpty) semicolon.backtrack.? else P.pure(Option.empty[Unit])
    } yield
      if (isEntry) Statement.Entry(visible.isDefined, name, parameters, body.getOrElse(Col.empty[Statement]))
      else Statement.Function(visible.isDefined, name, returns, parameters, body)

  val statement: P[Statement] = P.recursive { self =>
    val block = (lexeme(P.char('{')) *> (self.rep0 <* lexeme(P.char('}')))).map(values => Statement.Block(values.toCol))
    entryOrFunction(self).backtrack
      .orElse(block.backtrack)
      .orElse(label.backtrack)
      .orElse(lineDirective.backtrack)
      .orElse(directive.backtrack)
      .orElse(instruction)
  }

  private val module: P[Col[Statement]] = (statement.rep.map(_.iterator.toCol) <* skipped) <* P.end

  def parse(source: String): Either[ParseError, Program] = {
    val trimmed = source.trim
    if (trimmed.isEmpty) Right(Program(Col.empty))
    else
      module.parseAll(trimmed).map(Program.apply).leftMap { error =>
        val offset = error.failedAtOffset
        val prefix = trimmed.take(offset)
        val line   = prefix.count(_ == '\n') + 1
        val column = offset - prefix.lastIndexOf('\n')
        ParseError(offset, line, column, error.expected.iterator.take(12).map(_.toString).toCol)
      }
  }

  def parseStatement(source: String): Either[ParseError, Statement] =
    parse(source).flatMap {
      case Program(Col(value)) => Right(value)
      case Program(values)     => Left(ParseError(0, 1, 1, Col(s"one statement, found ${values.size}")))
    }
}
