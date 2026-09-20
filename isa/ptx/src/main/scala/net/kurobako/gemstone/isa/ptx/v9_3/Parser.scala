package net.kurobako.gemstone.isa.ptx.v9_3

import Program.*

import net.kurobako.gemstone.NoNul
import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.execution.KernelName

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import scala.collection.mutable
import scala.util.control.NoStackTrace

/** Allocation-conscious PTX parser used by program loading. The combinator parser remains the readable grammar and
  * reference implementation; this cursor parser deliberately keeps its tokens and mutable cursor private.
  */
object Parser {
  private type Name = String :| (MinLength[1] & NoNul)

  private final case class Token(text: String, offset: Int, line: Int, column: Int, quoted: Boolean = false)

  private final case class Failure(token: Token, expectation: String) extends RuntimeException with NoStackTrace

  @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
  private def name(value: String): Name = value.assume[MinLength[1] & NoNul]

  def parse(source: String): Either[ParseError, Program] = {
    val trimmed = source.trim
    if (trimmed.isEmpty) Right(Program(Vec.empty))
    else if (trimmed.indexOf('\u0000') >= 0) Left(ParseError(0, 1, 1, Vec("PTX source without NUL characters")))
    else
      try Right(Cursor(tokenize(trimmed), trimmed.length).program())
      catch {
        case Failure(token, expectation) =>
          Left(ParseError(
            token.offset.refineUnsafe[Positive0],
            token.line.refineUnsafe[Positive],
            token.column.refineUnsafe[Positive],
            Vec(expectation)
          ))
      }
  }

  def parseStatement(source: String): Either[ParseError, Statement] =
    parse(source).flatMap {
      case Program(Vec(value)) => Right(value)
      case Program(values)     => Left(ParseError(0, 1, 1, Vec(s"one statement, found ${values.size}")))
    }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def tokenize(source: String): Array[Token] = {
    val tokens = mutable.ArrayBuffer.empty[Token]
    var offset = 0
    var line   = 1
    var column = 1

    def advance(): Unit = {
      val character = source.charAt(offset)
      offset += 1
      if (character == '\n') { line += 1; column = 1 }
      else column += 1
    }

    def add(text: String, start: Int, startLine: Int, startColumn: Int, quoted: Boolean = false): Unit =
      tokens += Token(text, start, startLine, startColumn, quoted)

    while (offset < source.length) {
      val character = source.charAt(offset)
      if (character.isWhitespace) advance()
      else if (character == '/' && offset + 1 < source.length && source.charAt(offset + 1) == '/') {
        while (offset < source.length && source.charAt(offset) != '\n') advance()
      } else if (character == '/' && offset + 1 < source.length && source.charAt(offset + 1) == '*') {
        val start       = offset
        val startLine   = line
        val startColumn = column
        advance()
        advance()
        while (offset + 1 < source.length && !(source.charAt(offset) == '*' && source.charAt(offset + 1) == '/'))
          advance()
        if (offset + 1 >= source.length) throw Failure(Token("", start, startLine, startColumn), "closing */")
        advance()
        advance()
      } else {
        val start       = offset
        val startLine   = line
        val startColumn = column
        character match {
          case '"' =>
            advance()
            val value  = StringBuilder()
            var closed = false
            while (offset < source.length && !closed)
              source.charAt(offset) match {
                case '"'         => advance(); closed = true
                case '\n' | '\r' => throw Failure(Token("", start, startLine, startColumn), "closing quote")
                case '\\' if offset + 1 < source.length =>
                  value.append('\\')
                  advance()
                  value.append(source.charAt(offset))
                  advance()
                case _ =>
                  value.append(source.charAt(offset))
                  advance()
              }
            if (!closed) throw Failure(Token("", start, startLine, startColumn), "closing quote")
            add(value.result(), start, startLine, startColumn, quoted = true)
          case ':' if offset + 1 < source.length && source.charAt(offset + 1) == ':' =>
            advance(); advance(); add("::", start, startLine, startColumn)
          case '.'
              if offset + 1 < source.length && source.charAt(offset + 1).isDigit &&
                tokens.lastOption.exists(token =>
                  token.offset + token.text.length == offset && token.text.forall(_.isDigit)
                ) =>
            val previous = tokens.remove(tokens.size - 1)
            advance()
            while (
              offset < source.length &&
              !source.charAt(offset).isWhitespace &&
              !".,;:{}()[]+-|@!=\"".contains(source.charAt(offset))
            ) advance()
            add(source.substring(previous.offset, offset), previous.offset, previous.line, previous.column)
          case value @ ('.' | ',' | ';' | ':' | '{' | '}' | '(' | ')' | '[' | ']' | '+' | '-' | '|' | '@' | '!' |
              '=') =>
            advance(); add(value.toString, start, startLine, startColumn)
          case _ =>
            while (
              offset < source.length && !source.charAt(offset).isWhitespace &&
              !".,;:{}()[]+-|@!=\"".contains(source.charAt(offset))
            ) advance()
            if (offset == start) throw Failure(Token(character.toString, start, startLine, startColumn), "PTX token")
            add(source.substring(start, offset), start, startLine, startColumn)
        }
      }
    }
    tokens.toArray
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.return"))
  private final class Cursor(tokens: Array[Token], sourceLength: Int) {
    private var index = 0

    private def eof: Boolean = index >= tokens.length

    private def current: Token =
      if (eof) {
        val last = tokens.last
        Token("", sourceLength, last.line, last.column + math.max(1, last.text.length))
      } else tokens(index)

    private def at(text: String): Boolean = !eof && current.text == text && !current.quoted

    private def adjacent: Boolean =
      index > 0 && current.offset == tokens(index - 1).offset + tokens(index - 1).text.length

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
      while (!eof) statements += statement()
      Program(Vec.from(statements))
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
        body += statement()
      }
      expect("}")
      Statement.Block(Vec.from(body))
    }

    private def dottedStatement(): Statement = {
      val mark = index
      expect(".")
      val first = word()
      index = mark
      if (looksLikeFunctionDeclaration()) function()
      else if (first == "version" || first == "target" || first == "address_size") lineDirective()
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
      while (!eof && !at(";") && !at("}") && !(current.line > startLine && at(".")))
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
      if (accept("=")) while (!eof && !at(";")) index += 1
      expect(";")
      Statement.Directive(path, Vec.from(arguments), DirectiveTerminator.Semicolon)
    }

    private def dottedPath(): Vec[DirectiveName] :| MinLength[1] = {
      val parts = mutable.ArrayBuffer.empty[DirectiveName]
      expect(".")
      parts += DirectiveName(name(segment(allowSingleColon = false)))
      while (at(".") && adjacent) {
        expect(".")
        parts += DirectiveName(name(segment(allowSingleColon = false)))
      }
      Vec.from(parts).refineUnsafe[MinLength[1]]
    }

    private def dataDeclaration(): Statement = {
      val parts = mutable.ArrayBuffer.empty[String]
      while (at(".")) {
        expect(".")
        parts += segment(allowSingleColon = false)
        if (parts.last == "align" && !eof && current.text.toIntOption.nonEmpty) parts += word()
      }
      val address =
        DataAddressSpace.values.find(value => parts.contains(value.mnemonic)).getOrElse(fail("data address space"))
      val kind   = DataType.values.find(value => parts.contains(value.mnemonic)).getOrElse(fail("data type"))
      val symbol = SymbolName(name(word()))
      val count  =
        if (accept("[")) {
          val value = if (accept("]")) None
          else {
            val parsed = word().toIntOption.flatMap(_.refineOption[Positive]).getOrElse(fail("positive element count"))
            expect("]")
            Some(parsed)
          }
          value
        } else None
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
        declarationQualifiers(Vec.from(parts), Set(address.mnemonic, kind.mnemonic)),
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
          val alignment = parts(position + 1).toIntOption.flatMap(_.refineOption[Positive])
            .getOrElse(fail("positive alignment"))
          qualifiers += DeclarationQualifier.Alignment(alignment)
          position += 2
        } else if (value.headOption.exists(character => character.isDigit || character == '+' || character == '-')) {
          qualifiers += DeclarationQualifier.Value(ImmediateText(name(value)))
          position += 1
        } else {
          qualifiers += DeclarationQualifier.Attribute(QualifierName(name(value)))
          position += 1
        }
      }
      Vec.from(qualifiers)
    }

    private def function(): Statement = {
      var visible  = false
      var scanning = true
      while (scanning) {
        expect(".")
        word() match {
          case "visible"                 => visible = true
          case "weak"                    => ()
          case "extern"                  => ()
          case kind @ ("entry" | "func") =>
            scanning = false
            val isEntry            = kind == "entry"
            val returns            = if (!isEntry && at("(")) this.parameters() else Vec.empty[Parameter]
            val functionName       = word()
            val functionParameters = if (at("(")) this.parameters() else Vec.empty[Parameter]
            while (!eof && !at("{") && !at(";")) index += 1
            val body =
              if (accept("{")) {
                val statements = mutable.ArrayBuffer.empty[Statement]
                while (!at("}")) {
                  if (eof) fail("closing }")
                  statements += statement()
                }
                expect("}")
                Some(Vec.from(statements))
              } else { val _ = accept(";"); None }
            return if (isEntry)
              Statement.Entry(
                visible,
                KernelName(name(functionName)),
                functionParameters,
                body.getOrElse(Vec.empty[Statement])
              )
            else
              Statement.Function(visible, FunctionName(name(functionName)), returns, functionParameters, body)
          case _ => fail(".entry or .func")
        }
      }
      fail("function")
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
      val parts = mutable.ArrayBuffer.empty[String]
      while (at(".")) {
        expect(".")
        parts += segment(allowSingleColon = false)
        if (parts.last == "align" && !eof && current.text.toIntOption.nonEmpty) parts += word()
      }
      val kind = DataType.values.find(value => parts.contains(value.mnemonic)).getOrElse(fail("parameter data type"))
      val parameterName = ParameterName(name(word()))
      val dimensions    = mutable.ArrayBuffer.empty[Operand]
      while (accept("[")) {
        if (!at("]")) dimensions ++= operandsUntil("]")
        expect("]")
      }
      Parameter(declarationQualifiers(Vec.from(parts), Set(kind.mnemonic)), kind, parameterName, Vec.from(dimensions))
    }

    private def labelOrPrototype(): Statement = {
      val label = word()
      expect(":")
      if (at(".") && index + 1 < tokens.length && tokens(index + 1).text == "callprototype") {
        while (!eof && !accept(";")) index += 1
        Statement.Directive(
          Vec(DirectiveName(name("callprototype"))).refineUnsafe[MinLength[1]],
          Vec.empty,
          DirectiveTerminator.Semicolon
        )
      } else Statement.Label(LabelName(name(label)))
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
        modifiers += Modifier(ModifierName(name(segment(allowSingleColon = true))))
      }
      val operands   = operandsUntil(";")
      val _          = accept(";")
      val normalized = {
        val values = Vec.from(modifiers)
        if (operation == "vote" && values.exists(_.name.value == "sync") && values.exists(_.name.value == "ballot"))
          Vec(Modifier(ModifierName(name("sync")))) ++ values.filterNot(_.name.value == "sync")
        else values
      }
      Instruction.decodeParsed(name(operation), operands, normalized, predicate) match {
        case Right(value)                                   => Statement.Instruction(value)
        case Left(Instruction.DecodeError.UnknownOpcode(_)) =>
          Statement.UnsupportedInstruction(Opcode(name(operation)), operands, normalized, predicate)
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
              Operand.Register(RegisterName(name(symbol.value)), Some(RegisterPart(name(part))))
            case _ => index = mark; suffix = false; value
          }
        else
          value match {
            case Operand.Address(_) | Operand.Qualified(_, _) =>
              value = Operand.Qualified(value, QualifierName(name(part)))
            case _ => index = mark; suffix = false
          }
      }
      if (accept("[")) {
        val values = operandsUntil("]")
        expect("]")
        values match {
          case Vec(single) => Operand.Index(value, single)
          case _           => value
        }
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
        val values = mutable.ArrayBuffer(QualifierName(name(segment(allowSingleColon = false))))
        while (at(".") && adjacent) {
          expect(".")
          values += QualifierName(name(segment(allowSingleColon = false)))
        }
        Operand.Qualifier(Vec.from(values).refineUnsafe[MinLength[1]])
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
              RegisterName(name(segments.take(part).mkString("."))),
              Some(RegisterPart(name(segments.last)))
            )
          else Operand.Register(RegisterName(name(segments.mkString("."))), None)
        } else if (number(value)) Operand.Immediate(ImmediateText(name(value)))
        else Operand.Symbol(SymbolName(name(value)))
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
