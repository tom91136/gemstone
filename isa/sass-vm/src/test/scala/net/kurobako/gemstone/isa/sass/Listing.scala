package net.kurobako.gemstone.isa.sass

import cats.syntax.all.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.string.*
import net.kurobako.gemstone.text.*
import scala.annotation.tailrec
import scala.util.control.NonFatal

/** Parses `nvdisasm -c` listings and address-free assembly blocks into decoded instructions. Listings printed with
  * `-hex` also yield their machine words: 64 bits per instruction before sm_70, 128 bits from sm_70.
  */
object Listing {
  private case class Record(
      address: Int,
      opcode: String,
      operands: String,
      predicate: Option[String],
      word: Option[BigInt]
  )

  def parse(text: String, kernel: String, sm: Int): Either[String, Vec[DecodedInstruction]] =
    records(text, kernel, sm).flatMap(_.traverse(decode(_, kernel, sm)))

  def function(text: String, kernel: String, sm: Int): Either[String, DecodedFunction] =
    parse(text, kernel, sm).map(DecodedFunction(kernel, _))

  def words(text: String, kernel: String, sm: Int): Either[String, Vec[(Int >= 0, Array[Byte])]] =
    records(text, kernel, sm).flatMap(_.traverse { record =>
      val width = if (sm < 70) 8 else 16
      for {
        address <- record.address.refine[Int >= 0].left.map(_.raw)
        word    <- record.word.toRight(s"$kernel+0x${record.address.toHexString}: listing has no machine word")
      } yield address -> Array.tabulate[Byte](width)(index => (word >> (index * 8)).toByte)
    })

  /** Instructions separated by newlines or `;` at 16-byte steps; a `name:` prefix labels an instruction for
    * `` `(name) ``.
    */
  def program(text: String, kernel: String = "kernel"): Either[String, DecodedFunction] = {
    val lines                = text.linesIterator.flatMap(_.split(';')).map(_.trim).filter(_.nonEmpty).toVec
    val (statements, labels) = lines.foldLeft((Vec.empty[String], Map.empty[String, Int])) {
      case ((statements, labels), line) =>
        line match {
          case s"$name:$rest" if Parser.validName(name) =>
            (if (rest.isBlank) statements else statements :+ rest.trim, labels + (name -> statements.size * 16))
          case _ => (statements :+ line, labels)
        }
    }
    statements.zipWithIndex.traverse { (line, index) =>
      catching(parseRecord(s"/*${"%04x".format(index * 16)}*/ $line;"))
        .flatMap(_.toRight(s"invalid SASS statement: $line"))
        .flatMap(record => resolve(record, labels))
        .flatMap(decode(_, kernel, 0))
    }.map(DecodedFunction(kernel, _))
  }

  private def catching[A](body: => A): Either[String, A] =
    try Right(body)
    catch {
      case SourceCursor.Failure(mark, expectation) =>
        Left(s"invalid nvdisasm text: ${expectation.raw} at ${mark.offset.raw}")
      case NonFatal(error) => Left(s"invalid nvdisasm text: ${error.getMessage}")
    }

  private def records(text: String, kernel: String, sm: Int): Either[String, Vec[Record]] = catching {
    val width                   = if (sm < 70) 8 else 16
    val bundle                  = if (sm < 50) 64 else if (sm < 70) 32 else 0
    def next(address: Int): Int = {
      val following = address + width
      if (bundle != 0 && following % bundle == 0) following + width else following
    }
    val lines               = text.linesIterator.toVec
    val (parsed, positions) = lines.indices.foldLeft((Vec.empty[Record], Map.empty[String, Int])) {
      case ((records, labels), index) =>
        parseRecord(lines(index)) match {
          case Some(record) =>
            val word =
              if (sm < 70) record.word
              else (record.word, lines.lift(index + 1).flatMap(hexWord(_))).mapN((low, high) => low | (high << 64))
            (records :+ record.copy(word = word), labels)
          case None =>
            lines(index).trim match {
              case s"$name:" if Parser.validName(name) => (records, labels + (name -> records.size))
              case _                                   => (records, labels)
            }
        }
    }
    val addresses = parsed.map(_.address)
    if (
      addresses.headOption.exists(_ != (if (bundle == 0) 0 else width)) ||
      addresses.sliding(2).exists(pair => pair.size == 2 && pair(1) != pair(0) + width && pair(1) != next(pair(0)))
    ) throw IllegalArgumentException(s"noncontiguous SASS instruction addresses in $kernel (sm_$sm)")
    val end    = parsed.lastOption.fold(0)(record => next(record.address))
    val labels = positions.map((name, index) => name -> parsed.lift(index).fold(end)(_.address))
    (parsed, labels)
  }.flatMap { (parsed, labels) =>
    if (parsed.isEmpty || !labels.contains(kernel)) Left(s"nvdisasm did not return kernel $kernel")
    else parsed.traverse(resolve(_, labels))
  }

  private def whitespace(c: Char): Boolean =
    c == ' ' || c == '\t' || c == '\r' || c == '\n' || c == '\f' || c == '\u000b'

  private def hexWord(line: String): Option[BigInt] = line.trim match {
    case s"/*$contents*/" =>
      contents.trim match {
        case value @ s"0x$digits" if digits.length == 16 => parseBigIntLiteral(value)
        case _                                           => None
      }
    case _ => None
  }

  private def parseRecord(line: String): Option[Record] = {
    val cursor = SourceCursor(line.trim)
    if (!cursor.consume("/*")) None
    else {
      val addressStart = cursor.offset
      cursor.skipWhile(isDigit(_, 16))
      val address = cursor.slice(addressStart)
      if (address.length < 4 || !cursor.consume("*/")) None
      else {
        def malformed(): Nothing = cursor.fail("unrecognised instruction record in nvdisasm output")
        cursor.skipWhile(whitespace(_))
        val _ = cursor.consume('{')
        cursor.skipWhile(whitespace(_))
        val predicate = Option.when(cursor.at('@')) {
          val start = cursor.offset
          val _     = cursor.consume('@')
          val _     = cursor.consume('!')
          val _     = cursor.consume('U')
          if (!cursor.consume('P') || cursor.atEnd || !"0123456T".contains(cursor.char())) malformed()
          cursor.advance()
          val raw    = cursor.slice(start)
          val before = cursor.offset
          cursor.skipWhile(whitespace(_))
          if (cursor.offset == before) malformed()
          raw
        }
        val opcodeStart = cursor.offset
        cursor.skipWhile(c =>
          (c >= 'A' && c <= 'Z') ||
            (c >= '0' && c <= '9') || c == '_' || c == '.'
        )
        val opcode = cursor.slice(opcodeStart)
        if (opcode.isEmpty) malformed()
        val operandsStart = cursor.offset
        // a closing brace ends a dual-issue pair unless it closes an operand list such as `DEPBAR {3, 2}`
        @tailrec def operandsEnd(open: Int): Unit =
          if (!cursor.atEnd && !cursor.at(';') && !(open == 0 && cursor.at('}'))) {
            val c = cursor.char()
            cursor.advance()
            operandsEnd(if (c == '{') open + 1 else if (c == '}') open - 1 else open)
          }
        operandsEnd(0)
        val operands = cursor.slice(operandsStart).trim
        if (!cursor.consume(';') && !cursor.consume('}')) malformed()
        cursor.skipWhile(whitespace(_))
        val _ = cursor.consume('}')
        cursor.skipWhile(whitespace(_))
        if (!cursor.atEnd && !cursor.consume("/*")) malformed()
        Some(Record(
          Integer.parseInt(address, 16),
          opcode,
          operands,
          predicate,
          hexWord(line.drop(line.lastIndexOf("/*")))
        ))
      }
    }
  }

  /** Joins `c[0x0] [0x10]` operand pieces and replaces label references with their addresses. */
  private def resolve(record: Record, labels: Map[String, Int]): Either[String, Record] = catching {
    val result = StringBuilder()
    val cursor = SourceCursor(record.operands)
    while (!cursor.atEnd)
      if (cursor.consume("`(")) {
        val start = cursor.offset
        cursor.skipWhile(_ != ')')
        val name = cursor.slice(start)
        if (!cursor.consume(')') || !Parser.validName(name)) throw IllegalArgumentException("malformed SASS label")
        val address = labels.getOrElse(name, throw IllegalArgumentException(s"unresolved SASS target $name"))
        val _       = result.append("0x").append(address.toHexString)
      } else {
        val character = cursor.char()
        val _         = result.append(character)
        cursor.advance()
        if (character == ']') {
          val start = cursor.offset
          cursor.skipWhitespace()
          if (!cursor.at('[')) { val _ = result.append(cursor.slice(start)) }
        }
      }
    record.copy(operands = result.result())
  }

  /** RET/CALL/BRX may separate operands with whitespace and P2R may name PR explicitly; lowering expects neither. */
  private def decode(record: Record, kernel: String, sm: Int): Either[String, DecodedInstruction] = {
    val base   = record.opcode.takeWhile(_ != '.')
    val source = base match {
      case "RET" | "CALL" | "BRX" if !record.operands.contains(',') =>
        record.operands.split(Array(' ', '\t', '\r', '\n', '\f', '\u000b')).filter(_.nonEmpty).mkString(", ")
      case _ => record.operands
    }
    val fields = if (record.operands.isEmpty) Vec.empty[String] else record.operands.split(',').toVec.map(_.trim)
    def diagnostic: String =
      s"$kernel+0x${record.address.toHexString} (sm_$sm): ${record.opcode} ${fields.mkString(", ")}: "
    val parsed = OperandParser.parseMany(source).left.flatMap { error =>
      val joined = fields.mkString(", ")
      if (joined == source) Left(error) else OperandParser.parseMany(joined)
    }
    for {
      address  <- record.address.refine[Int >= 0].left.map(_.raw)
      operands <- parsed.left.map(diagnostic + _)
      normalised = if (base == "P2R" && operands.size == 4 && fields.lift(1).contains("PR"))
        operands.patch(1, Vec.empty[Operand], 1)
      else operands
      guard <- record.predicate.traverse { value =>
        val negated = value.stripPrefix("@").startsWith("!")
        OperandParser.parse(value.stripPrefix("@").stripPrefix("!")).flatMap {
          case predicate: Operand.Predicate =>
            Right(Instruction.Guard(predicate.copy(negated = predicate.negated ^ negated)))
          case other => Left(s"invalid SASS predicate $value: $other")
        }
      }.left.map(diagnostic + _)
      modifiers = record.opcode.split('.').toVec.drop(1).map(Instruction.Modifier(_))
      operation <- Instruction.decode(base, modifiers, normalised, guard).left.map(diagnostic + _)
    } yield DecodedInstruction(address, operation, record.word, None)
  }
}
