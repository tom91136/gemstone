package net.kurobako.gemstone.isa.ptx

import Program.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.text.*
import scala.collection.mutable
import scala.util.control.NoStackTrace

/** PTX parser used by program loading, with private tokens and a mutable cursor. */
object Parser {

  // Punctuation spelling is immutable and shared; token positions remain private to each parse.
  private val asciiText = Array.tabulate(128)(_.toChar.toString)

  private case class Token(text: String, offset: Int >= 0, quoted: Boolean = false)

  private case class Failure(token: Token, expectation: String)         extends RuntimeException with NoStackTrace
  private case class Rejection(token: Token, message: NonEmpty[String]) extends RuntimeException with NoStackTrace

  private def name(value: String): NonEmpty[String] = value.unsafe[NonEmpty[String]]

  def parse(source: String): Either[SourceError, Program] =
    try {
      val (tokens, comments, end) = tokenise(source)
      Right(Cursor(source, tokens, comments, end).program())
    } catch {
      // Successful parses need token offsets only. Recover diagnostic coordinates on the error path.
      case Failure(token, expectation) =>
        Left(SourceError(SourcePosition.at(source, token.offset), s"expected $expectation"))
      case Rejection(token, message) => Left(SourceError(SourcePosition.at(source, token.offset), message))
    }

  /** Extract one semantic statement; parse retains surrounding comments in the program. */
  def parseStatement(source: String): Either[SourceError, Statement] =
    parse(source).map(program =>
      Program(program.statements.filter {
        case Statement.Comment(_) => false
        case _                    => true
      })
    ).flatMap {
      case Program(Vec(value)) => Right(value)
      case Program(values) => Left(SourceError(SourcePosition.Start, s"expected one statement, found ${values.size}"))
    }

  // shares the Failure channel Cursor uses, so parse has one handler for both stages
  @SuppressWarnings(Array("baumkuchen:throwing-helper", "scalafix:DisableSyntax.var"))
  private def tokenise(source: String): (Array[Token], Array[Token], Token) = {
    val tokens       = mutable.ArrayBuffer.empty[Token]
    val comments     = mutable.ArrayBuffer.empty[Token]
    val sourceCursor = SourceCursor(source)

    def token(text: String, start: SourceCursor.Mark, quoted: Boolean = false): Token =
      Token(text, start.offset, quoted)

    def advance(): Unit = {
      if (sourceCursor.at('\u0000'))
        throw Failure(token("", sourceCursor.mark), "PTX source without NUL characters")
      sourceCursor.advance()
    }

    def wordCharacter: Boolean =
      if (sourceCursor.atEnd) false
      else sourceCursor.char() match {
        case '.' | ',' | ';' | ':' | '{' | '}' | '(' | ')' | '[' | ']' | '+' | '-' | '|' | '@' | '!' | '=' | '"' =>
          false
        case '/'       => !sourceCursor.startsWith("//") && !sourceCursor.startsWith("/*")
        case character => !character.isWhitespace
      }

    while (!sourceCursor.atEnd) {
      val character = sourceCursor.char()
      if (character.isWhitespace) advance()
      else if (character == '/' && sourceCursor.startsWith("//")) {
        val start = sourceCursor.mark
        while (!sourceCursor.atEnd && !sourceCursor.at('\n')) advance()
        comments += token(sourceCursor.slice(start.offset), start)
      } else if (character == '/' && sourceCursor.startsWith("/*")) {
        val start = sourceCursor.mark
        advance()
        advance()
        while (sourceCursor.has(1) && !sourceCursor.startsWith("*/")) advance()
        if (!sourceCursor.has(1)) throw Failure(token("", start), "closing */")
        advance()
        advance()
        comments += token(sourceCursor.slice(start.offset), start)
      } else {
        val startOffset                                                = sourceCursor.offset
        def currentToken(text: String, quoted: Boolean = false): Token =
          Token(text, startOffset, quoted)
        character match {
          case '"' =>
            advance()
            val value  = mutable.StringBuilder()
            var closed = false
            while (!sourceCursor.atEnd && !closed)
              sourceCursor.char() match {
                case '"'                         => advance(); closed = true
                case '\n' | '\r'                 => throw Failure(currentToken(""), "closing quote")
                case '\\' if sourceCursor.has(1) =>
                  value.append('\\')
                  advance()
                  value.append(sourceCursor.char())
                  advance()
                case _ =>
                  value.append(sourceCursor.char())
                  advance()
              }
            if (!closed) throw Failure(currentToken(""), "closing quote")
            tokens += currentToken(value.result(), quoted = true)
          case ':' if sourceCursor.startsWith("::") =>
            advance(); advance(); tokens += currentToken("::")
          case '.'
              if sourceCursor.has(1) && sourceCursor.char(1).isDigit &&
                tokens.lastOption.exists(previous =>
                  previous.offset.raw + previous.text.length == sourceCursor.offset.raw &&
                    previous.text.forall(_.isDigit)
                ) &&
                // a qualifier value such as `cta_group::1` ends before the next qualifier, as in `.128x256b`
                !(tokens.length >= 2 && tokens(tokens.length - 2).text == "::") =>
            val previous = tokens.remove(tokens.size - 1)
            advance()
            while (wordCharacter) advance()
            tokens += previous.copy(text = sourceCursor.slice(previous.offset))
          case value @ ('.' | ',' | ';' | ':' | '{' | '}' | '(' | ')' | '[' | ']' | '+' | '-' | '|' | '@' | '!' | '=') =>
            advance(); tokens += currentToken(asciiText(value.toInt))
          case _ =>
            while (wordCharacter) advance()
            if (sourceCursor.offset == startOffset) throw Failure(currentToken(character.toString), "PTX token")
            tokens += currentToken(sourceCursor.slice(startOffset))
        }
      }
    }
    (tokens.toArray, comments.toArray, token("", sourceCursor.mark))
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.return"))
  private class Cursor(source: String, tokens: Array[Token], comments: Array[Token], end: Token) {
    private var index        = 0
    private var commentIndex = 0

    private def eof: Boolean = index >= tokens.length

    private def current: Token = if (eof) end else tokens(index)

    private def at(text: String): Boolean = !eof && current.text == text && !current.quoted

    private def adjacent: Boolean =
      index > 0 && current.offset.raw == tokens(index - 1).offset.raw + tokens(index - 1).text.length

    private def accept(text: String): Boolean =
      if (at(text)) { index += 1; true }
      else false

    private def expect(text: String): Unit =
      if (!accept(text)) fail(text)

    private def fail(expectation: String): Nothing = throw Failure(current, expectation)

    private def reject(message: NonEmpty[String]): Nothing = throw Rejection(current, message)

    private def word(): String = {
      if (eof || current.quoted || punctuation(current.text)) fail("identifier")
      val value = current.text
      index += 1
      value
    }

    private def punctuation(value: String): Boolean =
      value.length == 1 && ".,;:{}()[]+-|@!=".contains(value)

    def program(): Program = {
      val statements = mutable.ArrayBuffer.empty[Statement]
      while (!eof) appendStatement(statements)
      appendComments(statements)
      Program(Vec.from(statements))
    }

    private def appendComments(statements: mutable.ArrayBuffer[Statement]): Unit =
      while (commentIndex < comments.length && comments(commentIndex).offset < current.offset) {
        statements += Statement.Comment(name(comments(commentIndex).text))
        commentIndex += 1
      }

    private def appendStatement(statements: mutable.ArrayBuffer[Statement]): Unit = {
      appendComments(statements)
      if (at(".")) statements ++= dottedStatements()
      else statements += statement()
      appendComments(statements)
    }

    private def statement(): Statement =
      if (at("{")) block()
      else if (isLabel) labelOrPrototype()
      else instruction()

    private def isLabel: Boolean = index + 1 < tokens.length && !tokens(index).quoted && tokens(index + 1).text == ":"

    private def block(): Statement = {
      expect("{")
      val body = mutable.ArrayBuffer.empty[Statement]
      while (!at("}")) {
        if (eof) fail("closing }")
        appendStatement(body)
      }
      appendComments(body)
      expect("}")
      Statement.Block(Vec.from(body))
    }

    private def dottedStatements(): Vec[Statement] = {
      val mark = index
      expect(".")
      val first = word()
      index = mark
      if (looksLikeFunctionDeclaration()) Vec(function())
      else if (Set("version", "target", "address_size", "file", "loc").contains(first)) Vec(lineDirective())
      else if (looksLikeDataDeclaration()) {
        val dataMark = index
        try dataDeclarations()
        catch {
          case Failure(_, _) =>
            index = dataMark
            Vec(directive())
        }
      } else Vec(directive())
    }

    private def looksLikeFunctionDeclaration(): Boolean = {
      var cursor = index
      while (cursor + 1 < tokens.length && tokens(cursor).text == ".")
        tokens(cursor + 1).text match {
          case "entry" | "func"              => return true
          case "visible" | "weak" | "extern" => cursor += 2
          case _                             => return false
        }
      false
    }

    private def looksLikeDataDeclaration(): Boolean = {
      var cursor   = index
      var address  = false
      var dataType = false
      while (
        cursor < tokens.length && tokens(cursor).text != ";" && tokens(cursor).text != "{" &&
        tokens(cursor).text != "}"
      ) {
        if (tokens(cursor).text == "." && cursor + 1 < tokens.length) {
          val value = tokens(cursor + 1).text
          address ||= DataAddressSpace.values.exists(_.mnemonic == value)
          dataType ||= DataType.values.exists(_.mnemonic == value)
        }
        cursor += 1
      }
      address && dataType
    }

    private def lineDirective(): Statement = {
      val newline   = source.indexOf('\n', current.offset)
      val lineEnd   = if (newline < 0) source.length else newline
      val path      = dottedPath()
      val arguments = mutable.ArrayBuffer.empty[DirectiveArgument]
      var separator = DirectiveSeparator.Space
      while (!eof && !at(";") && !at("}") && current.offset < lineEnd)
        if (accept(",")) separator = DirectiveSeparator.Comma
        else {
          arguments += DirectiveArgument(separator, operand())
          separator = DirectiveSeparator.Space
        }
      val _ = accept(";")
      Statement.Directive(path, Vec.from(arguments), DirectiveTerminator.Line)
    }

    private def directive(): Statement = {
      val path      = dottedPath()
      val arguments = mutable.ArrayBuffer.empty[DirectiveArgument]
      var separator = DirectiveSeparator.Space
      while (!eof && !at(";") && !at("="))
        if (accept(",")) separator = DirectiveSeparator.Comma
        else {
          arguments += DirectiveArgument(separator, operand())
          separator = DirectiveSeparator.Space
        }
      val initialiser = if (accept("=")) {
        val values = operandsUntil(";")
        if (values.isEmpty) fail("initialiser")
        values
      } else Vec.empty[Operand]
      expect(";")
      Statement.Directive(path, Vec.from(arguments), DirectiveTerminator.Semicolon, initialiser)
    }

    private def dottedPath(): NonEmpty[Vec[NonEmpty[String]]] = {
      val parts = mutable.ArrayBuffer.empty[NonEmpty[String]]
      expect(".")
      parts += name(segment())
      while (at(".") && adjacent) {
        expect(".")
        parts += name(segment())
      }
      Vec.from(parts).unsafe
    }

    /** A declaration of one or more comma-separated variables sharing the leading state space, type and qualifiers. */
    private def dataDeclarations(): Vec[Statement] = {
      val parts   = declarationParts()
      val present = parts.toSet
      val address =
        DataAddressSpace.values.find(value => present(value.mnemonic)).getOrElse(fail("data address space"))
      val kind                  = DataType.values.find(value => present(value.mnemonic)).getOrElse(fail("data type"))
      val qualifiers            = declarationQualifiers(parts, Set(address.mnemonic, kind.mnemonic))
      def variable(): Statement = {
        val symbol = name(word())
        val count  =
          if (accept("[")) {
            val value = if (accept("]")) Extent.Unsized
            else {
              val parsed =
                word().toIntOption.flatMap(refinementValue => refinementValue.refine[Int > 0].toOption).getOrElse(
                  fail("positive element count")
                )
              expect("]")
              Extent.Sized(parsed)
            }
            value
          } else Extent.Scalar
        val initialiser =
          if (accept("=")) {
            if (accept("{")) {
              val values = operandsUntil("}")
              expect("}")
              values
            } else Vec(operand())
          } else Vec.empty[Operand]
        Statement.Data(address, qualifiers, kind, symbol, count, initialiser)
      }
      val variables = mutable.ArrayBuffer(variable())
      while (accept(",")) variables += variable()
      expect(";")
      Vec.from(variables)
    }

    private def declarationQualifiers(parts: Vec[String], excluded: Set[String]): Vec[DeclarationQualifier] = {
      val qualifiers = mutable.ArrayBuffer.empty[DeclarationQualifier]
      var position   = 0
      while (position < parts.size) {
        val value = parts(position)
        if (excluded(value)) position += 1
        else if (value == "align" && position + 1 < parts.size) {
          val alignment =
            parts(position + 1).toIntOption.flatMap(refinementValue => refinementValue.refine[Int > 0].toOption)
              .getOrElse(fail("positive alignment"))
          qualifiers += DeclarationQualifier.Alignment(alignment)
          position += 2
        } else if (value.headOption.exists(character => character.isDigit || character == '+' || character == '-')) {
          qualifiers += DeclarationQualifier.Value(name(value))
          position += 1
        } else {
          qualifiers += DeclarationQualifier.Attribute(name(value))
          position += 1
        }
      }
      Vec.from(qualifiers)
    }

    private def declarationParts(): Vec[String] = {
      val parts = mutable.ArrayBuffer.empty[String]
      while (at(".")) {
        expect(".")
        parts += segment()
        if (parts.last == "align" && !eof && current.text.toIntOption.nonEmpty) parts += word()
      }
      Vec.from(parts)
    }

    private def function(): Statement = {
      var linkage  = Linkage.Default
      var scanning = true
      while (scanning) {
        expect(".")
        word() match {
          case value @ ("visible" | "weak" | "extern") =>
            if (linkage != Linkage.Default) fail("one linkage qualifier")
            linkage = Linkage.values.find(_.mnemonic == value).getOrElse(fail("linkage qualifier"))
          case kind @ ("entry" | "func") =>
            scanning = false
            val isEntry            = kind == "entry"
            val returns            = if (!isEntry && at("(")) this.parameters() else Vec.empty[Parameter]
            val functionName       = word()
            val functionParameters = if (at("(")) this.parameters() else Vec.empty[Parameter]
            val directives         = functionDirectives()
            val body               =
              if (accept("{")) {
                val statements = mutable.ArrayBuffer.empty[Statement]
                while (!at("}")) {
                  if (eof) fail("closing }")
                  appendStatement(statements)
                }
                appendComments(statements)
                expect("}")
                Some(Vec.from(statements))
              } else { expect(";"); None }
            return if (isEntry)
              Statement.Entry(
                linkage,
                name(functionName),
                functionParameters,
                body,
                directives
              )
            else
              Statement.Function(linkage, name(functionName), returns, functionParameters, body, directives)
          case _ => fail(".entry or .func")
        }
      }
      fail("function")
    }

    private def functionDirectives(): Vec[Statement.Directive] = {
      val directives = mutable.ArrayBuffer.empty[Statement.Directive]
      while (!eof && !at("{") && !at(";")) {
        val path      = dottedPath()
        val arguments = mutable.ArrayBuffer.empty[DirectiveArgument]
        var separator = DirectiveSeparator.Space
        while (!eof && !at(".") && !at("{") && !at(";"))
          if (accept(",")) separator = DirectiveSeparator.Comma
          else {
            arguments += DirectiveArgument(separator, operand())
            separator = DirectiveSeparator.Space
          }
        directives += Statement.Directive(path, Vec.from(arguments), DirectiveTerminator.Line)
      }
      Vec.from(directives)
    }

    private def parameters(): Vec[Parameter] = {
      expect("(")
      val values = mutable.ArrayBuffer.empty[Parameter]
      while (!at(")")) {
        values += parameter()
        if (!accept(",") && !at(")")) fail("comma or closing )")
      }
      expect(")")
      Vec.from(values)
    }

    private def parameter(): Parameter = {
      val parts         = declarationParts()
      val present       = parts.toSet
      val kind          = DataType.values.find(value => present(value.mnemonic)).getOrElse(fail("parameter data type"))
      val parameterName = name(word())
      val dimensions    = mutable.ArrayBuffer.empty[Option[Operand]]
      while (accept("[")) {
        dimensions += (if (at("]")) None else Some(operand()))
        expect("]")
      }
      Parameter(declarationQualifiers(parts, Set(kind.mnemonic)), kind, parameterName, Vec.from(dimensions))
    }

    private def labelOrPrototype(): Statement = {
      val label = word()
      expect(":")
      if (at(".") && index + 1 < tokens.length && tokens(index + 1).text == "callprototype") {
        expect(".")
        expect("callprototype")
        val returns = if (at("(")) parameters() else Vec.empty[Parameter]
        expect("_")
        val inputs     = if (at("(")) parameters() else Vec.empty[Parameter]
        val directives = functionDirectives()
        expect(";")
        Statement.CallPrototype(name(label), returns, inputs, directives)
      } else Statement.Label(name(label))
    }

    private def instruction(): Statement = {
      val predicate =
        if (accept("@")) {
          val negated = accept("!")
          Some(Predication(negated, operandAtom()))
        } else None
      val operation = segment()
      val modifiers = mutable.ArrayBuilder.make[Modifier]
      modifiers.sizeHint(2)
      while (at(".") && adjacent) {
        expect(".")
        modifiers += Modifier(name(modifierSegment()))
      }
      val operands   = operandsUntil(";")
      val _          = accept(";")
      val normalised = {
        val values = Vec.unsafeWrapArray(modifiers.result())
        if (operation == "vote" && values.contains(Modifier.Sync) && values.contains(Modifier.Ballot))
          Vec(Modifier.Sync) ++ values.filterNot(_ == Modifier.Sync)
        else values
      }
      Instruction.decodeParsed(name(operation), operands, normalised, predicate) match {
        case Right(value)                                      => Statement.Instruction(value)
        case Left(DecodeError(_, DecodeProblem.UnknownOpcode)) =>
          Statement.UnsupportedInstruction(name(operation), operands, normalised, predicate)
        case Left(error) => reject(error.message("PTX"))
      }
    }

    private def operandsUntil(closing: String): Vec[Operand] = {
      val values = mutable.ArrayBuilder.make[Operand]
      values.sizeHint(4)
      while (!eof && !at(closing) && !isLabel) {
        values += operand()
        if (!accept(",") && !at(closing) && !(closing == ";" && eof) && !isLabel)
          fail(s"comma or $closing")
      }
      Vec.unsafeWrapArray(values.result())
    }

    private def operand(): Operand = {
      val base = additive()
      if (accept("|")) Operand.WithPredicate(base, additive()) else base
    }

    private def additive(): Operand = {
      var value =
        if (accept("-")) Operand.Negate(indexed()) else if (accept("!")) Operand.Not(indexed()) else indexed()
      while (at("+") || at("-")) {
        val add = accept("+")
        if (!add) expect("-")
        val right = if (accept("-")) Operand.Negate(indexed()) else indexed()
        value = if (add) Operand.Add(value, right) else Operand.Subtract(value, right)
      }
      value
    }

    private def indexed(): Operand = {
      var value  = operandAtom()
      var suffix = true
      while (suffix && at(".") && adjacent) {
        val mark = index
        expect(".")
        val part = segment()
        if (
          looksLikeRegisterPart(part) ||
          PartialFunction.cond(part) { case "x" | "y" | "z" | "w" | "r" | "g" | "b" | "a" => true }
        )
          value = value match {
            case Operand.Symbol(symbol) =>
              Operand.Register(symbol, Some(OperandDomains.RegisterPart.fromSpelling(name(part))))
            case _ => index = mark; suffix = false; value
          }
        else
          value match {
            case Operand.Address(_) | Operand.Qualified(_, _) =>
              value = Operand.Qualified(value, OperandDomains.QualifiedOperand.fromSpelling(name(part)))
            case _ => index = mark; suffix = false
          }
      }
      if (accept("[")) {
        val key = operand()
        expect("]")
        Operand.Index(value, key)
      } else value
    }

    private def operandAtom(): Operand =
      if (eof) fail("operand")
      else if (current.quoted) {
        val value = current.text
        index += 1
        Operand.StringLiteral(value)
      } else if (accept("{")) {
        val values = operandsUntil("}")
        expect("}")
        Operand.VectorLiteral(values)
      } else if (accept("(")) {
        val values = operandsUntil(")")
        expect(")")
        Operand.Parenthesized(values)
      } else if (accept("[")) {
        val values = operandsUntil("]")
        expect("]")
        Operand.Address(values)
      } else if (accept(".")) {
        val values = mutable.ArrayBuffer(name(segment()))
        while (at(".") && adjacent) {
          expect(".")
          values += name(segment())
        }
        Operand.Qualifier(Vec.from(values).unsafe)
      } else {
        val value = word()
        if (value.startsWith("%")) {
          if (!at(".") || !adjacent) Operand.Register(name(value), None)
          else {
            val segments = mutable.ArrayBuffer(value)
            while (at(".") && adjacent) {
              expect(".")
              segments += segment()
            }
            val part = segments.indexWhere(looksLikeRegisterPart(_), 1)
            if (part > 0 && part == segments.size - 1)
              Operand.Register(
                name(segments.take(part).mkString(".")),
                Some(OperandDomains.RegisterPart.fromSpelling(name(segments.last)))
              )
            else Operand.Register(name(segments.mkString(".")), None)
          }
        } else if (value == "generic" && accept("(")) {
          val symbol = operand()
          expect(")")
          Operand.GenericAddress(symbol)
        } else if (number(value)) Operand.Immediate(name(value))
        else Operand.Symbol(name(value))
      }

    private def segment(): String = joined(at("::"))

    private def modifierSegment(): String = joined(at("::") || at(":"))

    private inline def joined(separator: => Boolean): String = {
      val first = word()
      if (!adjacent || !separator) first
      else {
        val value = mutable.StringBuilder(first)
        while (adjacent && separator) {
          index += 1
          val _ = value.append("::").append(word())
        }
        value.result()
      }
    }

    private def number(value: String): Boolean = {
      val unsigned = if (value.startsWith("+") || value.startsWith("-")) value.drop(1) else value
      unsigned.nonEmpty &&
      (unsigned.head.isDigit || (unsigned.head == '.' && unsigned.drop(1).headOption.exists(_.isDigit)))
    }

    private def looksLikeRegisterPart(value: String): Boolean =
      value.nonEmpty && "bhsfup".contains(value.head.toLower) &&
        value.tail.forall(character => character.isLetterOrDigit || character == '_') && value.length <= 5
  }
}
