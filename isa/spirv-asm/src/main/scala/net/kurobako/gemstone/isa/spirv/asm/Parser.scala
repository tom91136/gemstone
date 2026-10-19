package net.kurobako.gemstone.isa.spirv.asm

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.spirv.*
import net.kurobako.gemstone.string.*
import net.kurobako.gemstone.text.*
import scala.collection.mutable
import scala.util.control.NoStackTrace

object Parser {
  private val Digits = "[0-9]+".r

  private val Identifier = "%[0-9]+".r

  def parse(source: String): Either[SourceError, Module] =
    try {
      val (tokens, comments) = lex(source)
      Right(Reader(tokens, comments).read())
    } catch { case error: Invalid => Left(error.error) }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private class Reader(tokens: Vector[Token], comments: Vector[Token]) {
    private var position      = 0
    private var injected      = false
    private var nextId        = 1L
    private val usedIds       = mutable.Set.empty[Int]
    private val ids           = mutable.Map.empty[String, Word]
    private val defined       = mutable.Set.empty[Word]
    private val types         = mutable.Map.empty[Word, Instruction.TypeInt | Instruction.TypeFloat]
    private val imports       = mutable.Map.empty[Word, String]
    private val resultTypes   = mutable.Map.empty[Word, Word]
    private val fallback      = tokens.lastOption.getOrElse(Token("", quoted = false, SourcePosition.Start))
    private def take(): Token = {
      if (position == tokens.size) fail(fallback, "unexpected end of assembly")
      val token = tokens(position)
      position += 1
      token
    }
    private def id(token: Token): Word = {
      if (token.quoted || !token.text.matches("%[A-Za-z0-9_]+")) fail(token, "expected an ID")
      ids.getOrElseUpdate(
        token.text, {
          while (usedIds.contains(nextId.toInt)) nextId += 1
          if (nextId >= 0xffffffffL) fail(token, "ID space exhausted")
          val value = Word(nextId.toInt)
          usedIds += value.raw
          nextId += 1
          value
        }
      )
    }
    private def boundary: Boolean = position == tokens.size ||
      (!tokens(position).quoted &&
        (tokens(position).text.matches("Op[A-Z].*") ||
          (position + 1 < tokens.size && tokens(position + 1).text == "=")))
    private def raw(): Vec[Word] = {
      val token = take()
      if (token.quoted) LiteralString.encode(token.text)
      else if (token.text.startsWith("%")) Vec(id(token))
      else Vec(word(token))
    }
    private def number(token: Token, dataType: Word): Vec[Word] = {
      val (width, bits) = types.getOrElse(dataType, fail(token, "constant type must precede its value")) match {
        case Instruction.TypeFloat(_, width, encoding) =>
          if (encoding.nonEmpty) fail(token, "explicit floating encodings require raw !words")
          width.words.head.raw -> floatingBits(token, width.words.head.raw)
        case Instruction.TypeInt(_, width, signedness) =>
          val size = width.words.head.raw
          size -> integerBits(token, size, signedness.words.head.raw != 0)
      }
      Vec.tabulate((width + 31) / 32)(i => Word((bits >> (32 * i)).toInt))
    }
    private def integerBits(token: Token, width: Int, signed: Boolean): BigInt = {
      val value   = integer(token)
      val hex     = token.text.stripPrefix("-").stripPrefix("+").matches("0[xX].*")
      val minimum = if (signed) -(BigInt(1) << (width - 1)) else BigInt(0)
      val maximum = (BigInt(1) << (if (signed && !hex) width - 1 else width)) - 1
      if (value < minimum || value > maximum) fail(token, s"integer does not fit its $width-bit type")
      val narrowed = value & ((BigInt(1) << width) - 1)
      if (signed && narrowed.testBit(width - 1)) narrowed |
        (((BigInt(1) << (((width + 31) / 32) * 32)) - 1) ^ ((BigInt(1) << width) - 1))
      else narrowed
    }
    private def operand(kind: OperandKind, prefix: Vector[Word], opcode: Instruction.Definition): Vec[Word] = {
      if (boundary) fail(tokens.lift(position).getOrElse(fallback), s"missing $kind operand for $opcode")
      val token = take()
      if (injected || (token.text.startsWith("!") && !token.quoted)) {
        injected = true
        position -= 1
        raw()
      } else if (opcode == Instruction.Definition.SpecConstantOp && prefix.size >= 3 && token.text.startsWith("%"))
        Vec(id(token))
      else if (kind.category == OperandKind.Category.Id) {
        // specialization opcodes and extended instructions take their literal operands as bare numbers
        val literal = !token.text.startsWith("%") &&
          ((opcode == Instruction.Definition.SpecConstantOp && prefix.size >= 3) ||
            (opcode == Instruction.Definition.ExtInst && prefix.size >= 4))
        if (literal) Vec(word(token)) else Vec(id(token))
      } else if (kind == OperandKind.LiteralString) {
        if (!token.quoted) fail(token, "expected a quoted string")
        LiteralString.encode(token.text)
      } else if (token.quoted) fail(token, s"unexpected string for $kind")
      else if (kind.category == OperandKind.Category.Composite) {
        position -= 1
        Vec.from(kind.components).flatMap { component =>
          if (component == OperandKind.LiteralInteger && opcode == Instruction.Definition.Switch) {
            val dataType =
              prefix.headOption.flatMap(resultTypes.get).getOrElse(fail(token, "switch selector type is unknown"))
            number(take(), dataType)
          } else operand(component, prefix, opcode)
        }
      } else Instruction.operandKinds.get(kind) match {
        case Some(info) =>
          val selected = token.text.split("\\|", -1).toVector.map { name =>
            ExtensionEnumerants.of(info).find(value => value.name == name || value.aliases.contains(name))
              .getOrElse(fail(token, s"unknown $kind enumerant '$name'"))
          }
          if (info.kind.category == OperandKind.Category.ValueEnum && selected.size != 1)
            fail(token, s"$kind is not a mask")
          val value      = selected.foldLeft(0L)(_ | _.value)
          val parameters = selected.distinct.sortBy(_.value).flatMap(_.parameterKinds)
          Vec(Word(value.toInt)) ++ Vec.from(parameters).flatMap(parameter => operand(parameter, prefix, opcode))
        case None if kind == OperandKind.LiteralContextDependentNumber =>
          number(token, prefix.headOption.getOrElse(fail(token, "missing constant type")))
        case None if kind == OperandKind.LiteralSpecConstantOpInteger =>
          val name = ("Op" + token.text).unsafe[NonEmpty[String]]
          Instruction.definitionsFor(name).headOption.map(value => Vec(Word(value.opcodeNumber.toInt)))
            .getOrElse(fail(token, s"unknown specialization opcode '${token.text}'"))
        case None if kind == OperandKind.LiteralExtInstInteger && !token.text.headOption.exists(_.isDigit) =>
          val set   = prefix.lift(2).flatMap(imports.get).getOrElse(fail(token, "extended instruction set is unknown"))
          val value = ExtendedInstruction.names.get(set).flatMap(_.get(token.text))
            .getOrElse(fail(token, s"unknown extended instruction '${token.text}' in $set"))
          Vec(Word(value))
        case None => Vec(word(token))
      }
    }
    def read(): Module = {
      tokens.filter(token => !token.quoted && Identifier.matches(token.text)).foreach { token =>
        val numeric = token.copy(text = token.text.drop(1))
        val value   = integer(numeric)
        if (value <= 0 || value >= 0xffffffffL) fail(token, "ID must be between 1 and 4294967294")
        ids(token.text) = Word(value.toInt)
        usedIds += value.toInt
      }
      val instructions = mutable.ArrayBuffer.empty[Instruction]
      val starts       = mutable.ArrayBuffer.empty[Token]
      while (position < tokens.size) {
        starts += tokens(position)
        injected = false
        val first  = take()
        val result = if (!first.quoted && first.text.startsWith("%")) {
          val value = id(first)
          if (!defined.add(value)) fail(first, "duplicate result ID")
          if (take().text != "=") fail(first, "expected '=' after result ID")
          Some(value)
        } else None
        val opcode = if (result.nonEmpty) take() else first
        if (opcode.quoted) fail(opcode, "opcode cannot be quoted")
        val name       = opcode.text.refine[NonEmpty[String]].fold(_ => fail(opcode, "missing opcode"), identity)
        val definition = Instruction.definitionsFor(name).headOption
          .getOrElse(fail(opcode, s"unknown opcode '${opcode.text}'"))
        if (definition.shapes.exists(_.kind == OperandKind.IdResult) != result.nonEmpty)
          fail(opcode, "result assignment does not match instruction")
        val words = mutable.ArrayBuffer.empty[Word]
        definition.shapes.foreach { shape =>
          if (shape.kind == OperandKind.IdResult) words += result.get
          else shape.quantifier match {
            case OperandQuantifier.One      => words ++= operand(shape.kind, words.toVector, definition)
            case OperandQuantifier.Optional => if (!boundary) words ++= operand(shape.kind, words.toVector, definition)
            case OperandQuantifier.Repeated =>
              while (!boundary) words ++= operand(shape.kind, words.toVector, definition)
          }
        }
        if (injected) while (!boundary) words ++= raw()
        if (!boundary) fail(tokens(position), "unexpected trailing operand")
        val instruction = definition.decoder(RawInstruction(definition.opcodeNumber, Vec.from(words)))
          .fold(error => fail(opcode, error.message), identity)
        instructions += instruction
        if (definition.shapes.headOption.exists(_.kind == OperandKind.IdResultType))
          result.foreach(resultTypes(_) = words.head)
        instruction match {
          case numeric: (Instruction.TypeInt | Instruction.TypeFloat) =>
            val width = words(1).raw
            if (width <= 0 || width > 65536) fail(opcode, "unsupported numeric width")
            types(words.head) = numeric
          case Instruction.ExtInstImport(dest, setName) =>
            imports(dest.words.head) = LiteralString.decode(setName.words)
          case _ => ()
        }
      }
      val minimumBound     = ids.values.view.map(_.toUnsigned.toLong).maxOption.getOrElse(0L) + 1
      var header           = Header(ISA.version, Word(0), Word(minimumBound.toInt), Word(0))
      var order            = Endianness.Little
      val retained         = Vec.newBuilder[Module.Comment]
      var before: Int >= 0 = 0
      comments.foreach { comment =>
        while (
          before < starts.size &&
          (starts(before).position.line < comment.position.line ||
            (starts(before).position.line == comment.position.line &&
              starts(before).position.column <= comment.position.column))
        ) before += 1
        val fields   = comment.text.split(":", 2)
        val metadata = before == 0 && fields.length == 2 &&
          (PartialFunction.cond(fields(0)) { case "Version" | "Bound" | "Schema" | "Endianness" => true } ||
            (fields(0) == "Generator" && Digits.matches(fields(1).trim)))
        if (!metadata && !(before == 0 && comment.text == "SPIR-V"))
          retained += Module.Comment(before, comment.text)
        if (metadata) {
          val value = comment.copy(text = fields(1).trim)
          fields(0) match {
            case "Version" =>
              val parts = value.text.split("\\.", -1)
              if (parts.length != 2) fail(value, "expected major.minor version")
              val major = integer(value.copy(text = parts(0)))
              val minor = integer(value.copy(text = parts(1)))
              if (major <= 0 || major > 255 || minor < 0 || minor > 255)
                fail(value, "version components are outside 1..255/0..255")
              header = header.copy(version = Version(major.toInt.unsafe, minor.toInt.unsafe))
            case "Generator" if Digits.matches(value.text) => header = header.copy(generator = word(value))
            case "Bound"                                   => header = header.copy(bound = word(value))
            case "Schema"                                  => header = header.copy(schema = word(value))
            case "Endianness"                              => order = value.text match {
                case "Little" => Endianness.Little
                case "Big"    => Endianness.Big
                case _        => fail(value, "expected Little or Big endianness")
              }
            case _ => ()
          }
        }
      }
      if (header.bound.toUnsigned.toLong < minimumBound) fail(fallback, "header bound does not cover all IDs")
      Module(header, Vec.from(instructions), order, retained.result())
    }
  }

  private case class Token(text: String, quoted: Boolean, position: SourcePosition)
  private class Invalid(val error: SourceError) extends RuntimeException(error.message) with NoStackTrace
  private def fail(token: Token, message: NonEmpty[String]): Nothing =
    throw Invalid(SourceError(token.position, message))

  private def integer(token: Token): BigInt =
    parseBigIntLiteral(token.text.stripPrefix("!")).getOrElse(fail(token, s"invalid integer '${token.text}'"))
  private def word(token: Token): Word = {
    val value = integer(token)
    if (value < Int.MinValue || value > 0xffffffffL) fail(token, "integer does not fit a SPIR-V word")
    Word(value.toInt)
  }
  private val hex = "([+-]?)0[xX]1(?:\\.([0-9a-fA-F]*))?[pP]([+-]?[0-9]+)".r

  private def floatingBits(token: Token, width: Int): BigInt = {
    val (mantissa, maximumExponent) = width match {
      case 16 => (10, 16)
      case 32 => (23, 128)
      case 64 => (52, 1024)
      case _  => fail(token, s"unsupported floating width $width; use raw !words")
    }
    token.text match {
      // SPIR-V spells infinities and NaN payloads using the otherwise overflowing exponent.
      case hex(sign, fraction, exponent) if BigInt(exponent) == maximumExponent =>
        val digits       = Option(fraction).getOrElse("")
        val payload      = if (digits.isEmpty) BigInt(0) else BigInt(digits, 16)
        val shift        = mantissa - digits.length * 4
        val fractionBits = if (shift >= 0) payload << shift else payload >> -shift
        val signBit      = if (sign == "-") BigInt(1) << (width - 1) else BigInt(0)
        signBit | (BigInt(maximumExponent * 2 - 1) << mantissa) | fractionBits
      case _ =>
        val value = try java.lang.Double.parseDouble(token.text)
        catch { case _: NumberFormatException => fail(token, s"invalid floating literal '${token.text}'") }
        width match {
          case 16 => BigInt(IEEE754.doubleToBinary16(value).toLong)
          case 32 => BigInt(java.lang.Float.floatToRawIntBits(value.toFloat).toLong & 0xffffffffL)
          case _  => BigInt(java.lang.Double.doubleToRawLongBits(value)) & ((BigInt(1) << 64) - 1)
        }
    }
  }
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def lex(source: String): (Vector[Token], Vector[Token]) = {
    val tokens          = mutable.ArrayBuffer.empty[Token]
    val comments        = mutable.ArrayBuffer.empty[Token]
    val cursor          = SourceCursor(source)
    def advance(): Char = {
      val char = cursor.char()
      if (char == 0) fail(Token("", quoted = false, cursor.position), "NUL in assembly source")
      cursor.advance()
      char
    }
    while (!cursor.atEnd)
      if (cursor.char().isWhitespace) { val _ = advance() }
      else {
        val start = Token("", cursor.at('"'), cursor.position)
        val value = mutable.StringBuilder()
        if (cursor.at(';')) {
          val _ = advance()
          while (!cursor.atEnd && !cursor.at('\n')) value.append(advance())
          comments += start.copy(text = value.toString.trim)
        } else if (cursor.at('"')) {
          val _      = advance()
          var closed = false
          while (!cursor.atEnd && !closed)
            advance() match {
              case '"'  => closed = true
              case '\\' =>
                if (cursor.atEnd) fail(start, "unterminated string escape")
                value.append(advance())
              case char => value.append(char)
            }
          if (!closed) fail(start, "unterminated string")
          tokens += start.copy(text = value.toString)
        } else if (cursor.at('=')) {
          tokens += start.copy(text = advance().toString)
        } else {
          while (!cursor.atEnd && !cursor.char().isWhitespace && !cursor.at(';') && !cursor.at('='))
            value.append(advance())
          tokens += start.copy(text = value.toString)
        }
      }
    (tokens.toVector, comments.toVector)
  }
}
