package net.kurobako.gemstone.isa.ptx.v9_3

import Program.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.nvidia.*
import scala.collection.mutable
import scala.util.control.NoStackTrace

/** PTX parser used by program loading, with private tokens and a mutable cursor. */
object Parser extends Decoder[String, Program, ParseError] {

  private case class Token(text: String, offset: Int >= 0, line: Int > 0, column: Int > 0, quoted: Boolean = false)

  private case class Failure(token: Token, expectation: String) extends RuntimeException with NoStackTrace

  private def name(value: String): NonEmpty[String] = value.unsafe[NonEmpty[String]]

  def parse(source: String): Either[ParseError, Program] =
    try {
      val (tokens, comments, end) = tokenize(source)
      Right(Cursor(tokens, comments, end).program())
    } catch {
      case Failure(token, expectation) =>
        Left(ParseError(
          token.offset,
          token.line,
          token.column,
          Vec(expectation)
        ))
    }

  override def decode(source: String): Either[ParseError, Program] = parse(source)

  /** Extract one semantic statement; parse retains surrounding comments in the program. */
  def parseStatement(source: String): Either[ParseError, Statement] =
    parse(source).map(program =>
      Program(program.statements.filter {
        case Statement.Comment(_) => false
        case _                    => true
      })
    ).flatMap {
      case Program(Vec(value)) => Right(value)
      case Program(values)     => Left(ParseError(
          0,
          1,
          1,
          Vec(s"one statement, found ${values.size}")
        ))
    }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def tokenize(source: String): (Array[Token], Array[Token], Token) = {
    val tokens       = mutable.ArrayBuffer.empty[Token]
    val comments     = mutable.ArrayBuffer.empty[Token]
    val sourceCursor = SourceCursor(source)

    def token(text: String, start: SourceCursor.Position, quoted: Boolean = false): Token =
      Token(text, start.offset, start.line, start.column, quoted)

    def advance(): Unit = {
      if (sourceCursor.at('\u0000'))
        throw Failure(token("", sourceCursor.position), "PTX source without NUL characters")
      sourceCursor.advance()
    }

    def wordCharacter: Boolean = !sourceCursor.atEnd && !sourceCursor.char().isWhitespace &&
      !sourceCursor.startsWith("//") && !sourceCursor.startsWith("/*") &&
      !".,;:{}()[]+-|@!=\"".contains(sourceCursor.char())

    while (!sourceCursor.atEnd) {
      val character = sourceCursor.char()
      if (character.isWhitespace) advance()
      else if (sourceCursor.startsWith("//")) {
        val start = sourceCursor.position
        while (!sourceCursor.atEnd && !sourceCursor.at('\n')) advance()
        comments += token(sourceCursor.slice(start.offset), start)
      } else if (sourceCursor.startsWith("/*")) {
        val start = sourceCursor.position
        advance()
        advance()
        while (sourceCursor.has(1) && !sourceCursor.startsWith("*/")) advance()
        if (!sourceCursor.has(1)) throw Failure(token("", start), "closing */")
        advance()
        advance()
        comments += token(sourceCursor.slice(start.offset), start)
      } else {
        val startOffset                                                = sourceCursor.offset
        val startLine                                                  = sourceCursor.line
        val startColumn                                                = sourceCursor.column
        def currentToken(text: String, quoted: Boolean = false): Token =
          Token(text, startOffset, startLine, startColumn, quoted)
        character match {
          case '"' =>
            advance()
            val value  = StringBuilder()
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
                ) =>
            val previous = tokens.remove(tokens.size - 1)
            advance()
            while (wordCharacter) advance()
            tokens += previous.copy(text = sourceCursor.slice(previous.offset))
          case value @ ('.' | ',' | ';' | ':' | '{' | '}' | '(' | ')' | '[' | ']' | '+' | '-' | '|' | '@' | '!' | '=') =>
            advance(); tokens += currentToken(value.toString)
          case _ =>
            while (wordCharacter) advance()
            if (sourceCursor.offset == startOffset) throw Failure(currentToken(character.toString), "PTX token")
            tokens += currentToken(sourceCursor.slice(startOffset))
        }
      }
    }
    (tokens.toArray, comments.toArray, token("", sourceCursor.position))
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.return"))
  private class Cursor(tokens: Array[Token], comments: Array[Token], end: Token) {
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
      while (commentIndex < comments.length && comments(commentIndex).offset.raw < current.offset.raw) {
        statements += Statement.Comment(name(comments(commentIndex).text))
        commentIndex += 1
      }

    private def appendStatement(statements: mutable.ArrayBuffer[Statement]): Unit = {
      appendComments(statements)
      statements += statement()
      appendComments(statements)
    }

    private def statement(): Statement =
      if (at("{")) block()
      else if (at(".")) dottedStatement()
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

    private def dottedStatement(): Statement = {
      val mark = index
      expect(".")
      val first = word()
      index = mark
      if (looksLikeFunctionDeclaration()) function()
      else if (Set("version", "target", "address_size", "file", "loc").contains(first)) lineDirective()
      else if (looksLikeDataDeclaration()) {
        val dataMark = index
        try dataDeclaration()
        catch {
          case _: Failure =>
            index = dataMark
            directive()
        }
      } else directive()
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
      val startLine = current.line
      val path      = dottedPath()
      val arguments = mutable.ArrayBuffer.empty[DirectiveArgument]
      var separator = DirectiveSeparator.Space
      while (!eof && !at(";") && !at("}") && current.line <= startLine)
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
      val initializer = if (accept("=")) {
        val values = operandsUntil(";")
        if (values.isEmpty) fail("initializer")
        values
      } else Vec.empty[Operand]
      expect(";")
      Statement.Directive(path, Vec.from(arguments), DirectiveTerminator.Semicolon, initializer)
    }

    private def dottedPath(): NonEmpty[Vec[NonEmpty[String]]] = {
      val parts = mutable.ArrayBuffer.empty[NonEmpty[String]]
      expect(".")
      parts += name(segment(allowSingleColon = false))
      while (at(".") && adjacent) {
        expect(".")
        parts += name(segment(allowSingleColon = false))
      }
      Vec.from(parts).unsafe
    }

    private def dataDeclaration(): Statement = {
      val parts   = declarationParts()
      val address =
        DataAddressSpace.values.find(value => parts.contains(value.mnemonic)).getOrElse(fail("data address space"))
      val kind   = DataType.values.find(value => parts.contains(value.mnemonic)).getOrElse(fail("data type"))
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
      val initializer =
        if (accept("=")) {
          if (accept("{")) {
            val values = operandsUntil("}")
            expect("}")
            values
          } else operandsUntil(";")
        } else Vec.empty[Operand]
      expect(";")
      Statement.Data(
        address,
        declarationQualifiers(parts, Set(address.mnemonic, kind.mnemonic)),
        kind,
        symbol,
        count,
        initializer
      )
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
        parts += segment(allowSingleColon = false)
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
      val parts = declarationParts()
      val kind  = DataType.values.find(value => parts.contains(value.mnemonic)).getOrElse(fail("parameter data type"))
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
      val operation = segment(allowSingleColon = false)
      val modifiers = mutable.ArrayBuffer.empty[Modifier]
      while (at(".") && adjacent) {
        expect(".")
        modifiers += Modifier(name(segment(allowSingleColon = true)))
      }
      val operands   = operandsUntil(";")
      val _          = accept(";")
      val normalized = {
        val values = Vec.from(modifiers)
        if (operation == "vote" && values.contains(Modifier.Sync) && values.contains(Modifier.Ballot))
          Vec(Modifier.Sync) ++ values.filterNot(_ == Modifier.Sync)
        else values
      }
      Instruction.decodeParsed(name(operation), operands, normalized, predicate) match {
        case Right(value)                                   => Statement.Instruction(value)
        case Left(Instruction.DecodeError.UnknownOpcode(_)) =>
          Statement.UnsupportedInstruction(name(operation), operands, normalized, predicate)
        case Left(error) => fail(error.toString)
      }
    }

    private def operandsUntil(closing: String): Vec[Operand] = {
      val values = mutable.ArrayBuffer.empty[Operand]
      while (!eof && !at(closing) && !isLabel) {
        values += operand()
        if (!accept(",") && !at(closing) && !(closing == ";" && eof) && !isLabel)
          fail(s"comma or $closing")
      }
      Vec.from(values)
    }

    private def operand(): Operand = {
      val base = additive()
      if (accept("|")) Operand.WithPredicate(base, additive()) else base
    }

    private def additive(): Operand = {
      var value = if (accept("-")) Operand.Negate(indexed()) else indexed()
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
        val part = segment(allowSingleColon = false)
        if (looksLikeRegisterPart(part))
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
        val values = mutable.ArrayBuffer(name(segment(allowSingleColon = false)))
        while (at(".") && adjacent) {
          expect(".")
          values += name(segment(allowSingleColon = false))
        }
        Operand.Qualifier(Vec.from(values).unsafe)
      } else {
        val value = word()
        if (value.startsWith("%")) {
          val segments = mutable.ArrayBuffer(value)
          while (at(".") && adjacent) {
            expect(".")
            segments += segment(allowSingleColon = false)
          }
          val part = segments.indexWhere(looksLikeRegisterPart(_), 1)
          if (part > 0 && part == segments.size - 1)
            Operand.Register(
              name(segments.take(part).mkString(".")),
              Some(OperandDomains.RegisterPart.fromSpelling(name(segments.last)))
            )
          else Operand.Register(name(segments.mkString(".")), None)
        } else if (number(value)) Operand.Immediate(name(value))
        else Operand.Symbol(name(value))
      }

    private def segment(allowSingleColon: Boolean): String = {
      val value = StringBuilder(word())
      while (adjacent && (at("::") || (allowSingleColon && at(":")))) {
        index += 1
        val _ = value.append("::").append(word())
      }
      value.result()
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
