package net.kurobako.gemstone.isa.sass.asm

import cats.syntax.all.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.sass.*
import net.kurobako.gemstone.isa.sass.Instruction as SASSInstruction
import scala.util.control.NonFatal

/** Decodes NVIDIA instruction listings into the typed runtime model. */
object ListingDecoder {
  private case class RawInstruction(
      address: Int,
      opcode: String,
      operands: String,
      predicate: Option[String],
      encodingWord: Option[Long]
  )

  /** Keep text and binary decoding on the same operand convention used by lowering. */
  private def normalizeOperands(opcode: String, operands: Vec[String]): Vec[String] =
    opcode.takeWhile(_ != '.') match {
      case "P2R" if operands.size == 4 && operands(1) == "PR" => operands.patch(1, Vec.empty, 1)
      case "RET" | "CALL" | "BRX" if operands.size == 1       =>
        operands.head.split(Array(' ', '\t', '\r', '\n', '\f', '\u000b')).filter(_.nonEmpty).toVec
      case _ => operands
    }

  /** Parse NVIDIA text directly into typed opcodes, guards, and operands for execution. */
  def parse(text: String, kernel: String, architecture: Int = 0): Either[String, Vec[DecodedFunction]] =
    parseListing(text, kernel, architecture).flatMap { instructions =>
      instructions.traverse(decodeRawInstruction(_, kernel, architecture)).map { instructions =>
        Vec(DecodedFunction(kernel, instructions))
      }
    }

  private def decodeGuard(raw: Option[String]): Either[String, Option[SASSInstruction.Guard]] =
    raw.fold[Either[String, Option[SASSInstruction.Guard]]](Right(None)) { value =>
      val predicate = value.stripPrefix("@")
      val negated   = predicate.startsWith("!")
      SASSOperandParser.parse(predicate.stripPrefix("!")).flatMap {
        case parsed: Operand.Predicate =>
          Right(Some(SASSInstruction.Guard(parsed.copy(negated = parsed.negated ^ negated))))
        case other => Left(s"invalid SASS predicate $value: $other")
      }
    }

  private def decodeParts(
      address: Int >= 0,
      opcode: String,
      operands: Vec[Operand],
      predicate: Option[String],
      encodingWord: Option[Long]
  ): Either[String, DecodedInstruction] = {
    val opcodeParts = opcode.split('.').toVec
    val base        = opcodeParts.headOption.getOrElse(opcode)
    for {
      guard     <- decodeGuard(predicate)
      operation <- SASSInstruction.decode(
        base,
        opcodeParts.drop(1).map(SASSInstruction.Modifier(_)),
        operands,
        guard
      )
    } yield DecodedInstruction(
      address,
      operation,
      encodingWord.map(value => BigInt(java.lang.Long.toUnsignedString(value))),
      None
    )
  }

  private def decodeRawInstruction(
      instruction: RawInstruction,
      kernel: String,
      architecture: Int
  ): Either[String, DecodedInstruction] = {
    val source            = typedOperandSource(instruction.opcode, instruction.operands)
    lazy val textOperands = if (instruction.operands.isEmpty) Vec.empty[String]
    else normalizeOperands(instruction.opcode, instruction.operands.split(',').toVec.map(_.trim))
    // Preserve the public text API's trailing-comma handling and error offsets without
    // splitting and joining operand text on the ordinary successful path.
    val operands = SASSOperandParser.parseMany(source).left.flatMap { error =>
      val normalized = textOperands.mkString(", ")
      if (normalized == source) Left(error) else SASSOperandParser.parseMany(normalized)
    }
    def diagnosticPrefix: String =
      s"$kernel+0x${instruction.address.toHexString} (sm_$architecture): ${instruction.opcode} ${textOperands.mkString(", ")}: "

    for {
      address <- instruction.address.refine[Int >= 0].left.map(_.raw)
      args    <- operands.left.map(error => diagnosticPrefix + error)
      normalized = normalizeTypedOperands(instruction.opcode, instruction.operands, args)
      decoded <- decodeParts(address, instruction.opcode, normalized, instruction.predicate, instruction.encodingWord)
        .left.map(error => diagnosticPrefix + error)
    } yield decoded
  }

  private def typedOperandSource(opcode: String, source: String): String =
    opcode.takeWhile(_ != '.') match {
      case "RET" | "CALL" | "BRX" if !source.contains(',') =>
        source.split(Array(' ', '\t', '\r', '\n', '\f', '\u000b')).filter(_.nonEmpty).mkString(", ")
      case _ => source
    }

  private def normalizeTypedOperands(opcode: String, source: String, operands: Vec[Operand]): Vec[Operand] =
    opcode.takeWhile(_ != '.') match {
      case "P2R" =>
        val fields = source.split(',')
        if (operands.size == 4 && fields.length == 4 && fields(1).trim == "PR") operands.patch(1, Vec.empty, 1)
        else operands
      case _ => operands
    }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def parseListing(text: String, kernel: String, architecture: Int): Either[String, Vec[RawInstruction]] = try {
    // The JSON formatter in nvdisasm 13.2 loses PR and zero-fill load operands.
    // Text output preserves them, including guards distinct from load predicates.
    val architectureMarker = "EF_CUDA_SM"
    val architectureAt     = text.indexOf(architectureMarker)
    val sm                 = if (architecture != 0) architecture
    else if (architectureAt < 0) 86
    else text.drop(architectureAt + architectureMarker.length).takeWhile(_.isDigit).toIntOption.getOrElse(86)
    val width                          = if (sm < 70) 8 else 16
    val bundle                         = if (sm < 50) 64 else if (sm < 70) 32 else 0
    def nextAddress(address: Int): Int = {
      val next = address + width
      if (bundle != 0 && next % bundle == 0) next + width else next
    }
    def isHex(value: String): Boolean =
      value.nonEmpty && value.forall(c => (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F'))
    def letter(c: Char): Boolean   = (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')
    def hexDigit(c: Char): Boolean =
      (c >= '0' && c <= '9') ||
        (c >= 'a' && c <= 'f') ||
        (c >= 'A' && c <= 'F')
    def whitespace(c: Char): Boolean = c == ' ' || c == '\t' || c == '\r' || c == '\n' || c == '\f' || c == '\u000b'
    def opcodeCharacter(c: Char): Boolean =
      (c >= 'A' && c <= 'Z') ||
        (c >= '0' && c <= '9') || c == '_' || c == '.'
    def validLabel(value: String): Boolean =
      value.headOption.exists(c => letter(c) || "_.$".contains(c)) &&
        value.forall(c => letter(c) || (c >= '0' && c <= '9') || "_.$".contains(c))
    def normalizeAddressOperands(value: String): String = {
      val result = StringBuilder()
      val cursor = SourceCursor(value)
      while (!cursor.atEnd) {
        val character = cursor.char()
        result.append(character)
        cursor.advance()
        if (character == ']') {
          val whitespaceStart = cursor.offset
          cursor.skipWhitespace()
          if (!cursor.at('[')) {
            result.append(cursor.slice(whitespaceStart))
          }
        }
      }
      result.result()
    }
    def parseEncoding(line: String): Option[Long] = {
      val commentAt = line.lastIndexOf("/*")
      if (commentAt < 0) None
      else line.drop(commentAt).trim match {
        case s"/*$contents*/" => contents.trim match {
            case s"0x$digits" if digits.length == 16 && isHex(digits) =>
              Some(java.lang.Long.parseUnsignedLong(digits, 16))
            case _ => None
          }
        case _ => None
      }
    }
    def parseRecord(line: String): Option[RawInstruction] = {
      val cursor = SourceCursor(line.trim)
      cursor.skipWhile(whitespace(_))
      if (!cursor.startsWith("/*")) None
      else {
        val _            = cursor.consume("/*")
        val addressStart = cursor.offset
        cursor.skipWhile(hexDigit(_))
        val address = cursor.slice(addressStart)
        if (address.length < 4 || !cursor.consume("*/")) None
        else {
          def malformed(): Nothing = cursor.fail("unrecognized instruction record in nvdisasm output")
          cursor.skipWhile(whitespace(_))
          val _ = cursor.consume('{')
          cursor.skipWhile(whitespace(_))
          val predicate = if (cursor.at('@')) {
            val start = cursor.offset
            val _     = cursor.consume('@')
            val _     = cursor.consume('!')
            val _     = cursor.consume('U')
            if (!cursor.consume('P') || cursor.atEnd || !"0123456T".contains(cursor.char())) malformed()
            cursor.advance()
            val raw              = cursor.slice(start)
            val beforeWhitespace = cursor.offset
            cursor.skipWhile(whitespace(_))
            if (cursor.offset == beforeWhitespace) malformed()
            Some(raw)
          } else None
          val opcodeStart = cursor.offset
          cursor.skipWhile(opcodeCharacter(_))
          val operation = cursor.slice(opcodeStart)
          if (operation.isEmpty) malformed()
          val operandsStart = cursor.offset
          while (!cursor.atEnd && !cursor.at(';') && !cursor.at('}')) cursor.advance()
          val operandText = cursor.slice(operandsStart).trim
          if (!cursor.consume(';') && !cursor.consume('}')) malformed()
          cursor.skipWhile(whitespace(_))
          val _ = cursor.consume('}')
          cursor.skipWhile(whitespace(_))
          if (!cursor.atEnd) {
            if (!cursor.consume("/*")) malformed()
            while (!cursor.atEnd) cursor.advance()
          }
          Some(RawInstruction(
            Integer.parseInt(address, 16),
            operation,
            operandText,
            predicate,
            parseEncoding(line)
          ))
        }
      }
    }
    def parseLabel(line: String): Option[String] = line.trim match {
      case s"$name:" if validLabel(name) => Some(name)
      case _                             => None
    }
    def resolveLabels(operand: String, labels: Map[String, Int]): String = {
      val result = StringBuilder()
      val cursor = SourceCursor(operand)
      while (!cursor.atEnd)
        if (cursor.consume("`(")) {
          val start = cursor.offset
          cursor.skipWhile(_ != ')')
          val name = cursor.slice(start)
          if (!cursor.consume(')')) throw IllegalArgumentException("malformed SASS label reference")
          if (!validLabel(name)) throw IllegalArgumentException("malformed SASS label reference")
          val address = labels.getOrElse(name, throw IllegalArgumentException(s"unresolved SASS target $name"))
          val _       = result.append("0x").append(address.toHexString)
        } else {
          val _ = result.append(cursor.char())
          cursor.advance()
        }
      result.result()
    }

    val records          = Vec.newBuilder[RawInstruction]
    val labelPositions   = Map.newBuilder[String, Int]
    var instructionCount = 0
    text.linesIterator.foreach { line =>
      parseRecord(line) match {
        case Some(instruction) =>
          records += instruction
          instructionCount += 1
        case None => parseLabel(line).foreach(name => labelPositions += name -> instructionCount)
      }
    }
    val instructions = records.result()
    val addresses    = instructions.map(_.address)
    if (
      addresses.headOption.exists(_ != (if (bundle == 0) 0 else width)) ||
      addresses.sliding(2).exists(pair =>
        pair.size == 2 && pair(1) != pair(0) + width && pair(1) != nextAddress(pair(0))
      )
    )
      throw IllegalArgumentException(s"noncontiguous SASS instruction addresses in $kernel (sm_$sm)")
    val endAddress = instructions.lastOption.fold(0)(instruction => nextAddress(instruction.address))
    val labels     = labelPositions.result().map { (name, index) =>
      name -> instructions.lift(index).fold(endAddress)(_.address)
    }
    val resolved = instructions.map { instruction =>
      instruction.copy(operands = resolveLabels(normalizeAddressOperands(instruction.operands), labels))
    }
    if (resolved.isEmpty || !labels.contains(kernel)) Left(s"nvdisasm did not return kernel $kernel")
    else Right(resolved)
  } catch {
    case SourceCursor.Failure(position, expectation) =>
      Left(s"invalid nvdisasm text: ${expectation.raw} at ${position.offset.raw}")
    case NonFatal(error) => Left(s"invalid nvdisasm text: ${error.getMessage}")
  }

}
