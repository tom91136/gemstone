package net.kurobako.gemstone.isa.amdgpu.common

import scala.collection.mutable.ArrayBuffer

object Assembly {
  def parse(source: String): Either[ParseError, Program] = {
    val statements = Vector.newBuilder[Statement]
    val lines      = source.linesIterator.toVector
    var index      = 0
    while (index < lines.size) {
      parseLine(lines(index), index + 1) match {
        case Left(error)            => return Left(error)
        case Right(Some(statement)) => statements += statement
        case Right(None)            => ()
      }
      index += 1
    }
    Right(Program(statements.result()))
  }

  def parseInstruction(source: String): Either[ParseError, Instruction] =
    parse(source).flatMap {
      case Program(Vector(Statement.Instruction(instruction))) => Right(instruction)
      case Program(Vector())                                   => Left(ParseError(1, 1, "expected an instruction"))
      case _ => Left(ParseError(1, 1, "expected exactly one instruction"))
    }

  def print(program: Program): String = program.statements.map(printStatement).mkString("\n")

  def print(instruction: Instruction): String = {
    val operands  = instruction.operands.map(printOperand)
    val modifiers = instruction.modifiers.map { modifier =>
      modifier.value.fold(modifier.name)(value => s"${modifier.name}:$value")
    }
    val tail = operands ++ modifiers
    if (tail.isEmpty) instruction.opcode.value
    else {
      val operandText  = operands.mkString(", ")
      val modifierText = modifiers.mkString(" ")
      val separator    = if (operands.nonEmpty && modifiers.nonEmpty) " " else ""
      s"${instruction.opcode.value} $operandText$separator$modifierText"
    }
  }

  private def parseLine(source: String, line: Int): Either[ParseError, Option[Statement]] = {
    val text = stripComment(source).trim
    if (text.isEmpty) Right(None)
    else if (text.endsWith(":")) {
      val name = text.dropRight(1).trim
      if (isIdentifier(name)) Right(Some(Statement.Label(name)))
      else Left(ParseError(line, 1, s"invalid label '$name'"))
    } else if (text.startsWith(".")) {
      val end  = text.indexWhere(_.isWhitespace)
      val name = if (end < 0) text else text.take(end)
      val args = if (end < 0) "" else text.drop(end).trim
      if (name.matches("\\.[A-Za-z_][A-Za-z0-9_.]*")) Right(Some(Statement.Directive(name, args)))
      else Left(ParseError(line, 1, s"invalid directive '$name'"))
    } else {
      val pairSeparator = text.indexOf(" :: ")
      if (pairSeparator < 0) parseInstructionLine(text, line).map(value => Some(Statement.Instruction(value)))
      else {
        val left  = text.take(pairSeparator)
        val right = text.drop(pairSeparator + 4)
        for {
          first  <- parseInstructionLine(left, line)
          second <- parseInstructionLine(right, line)
        } yield Some(Statement.InstructionPair(first, second))
      }
    }
  }

  private def parseInstructionLine(text: String, line: Int): Either[ParseError, Instruction] = {
    val opcodeEnd  = text.indexWhere(_.isWhitespace)
    val opcodeText = if (opcodeEnd < 0) text else text.take(opcodeEnd)
    Opcode.fromString(opcodeText).left.map(message => ParseError(line, 1, message)).flatMap { opcode =>
      val tail = if (opcodeEnd < 0) "" else text.drop(opcodeEnd).trim
      splitTopLevel(tail, ',', line).flatMap { commaParts =>
        val operands                   = Vector.newBuilder[Operand]
        val modifiers                  = Vector.newBuilder[Modifier]
        var failed: Option[ParseError] = None
        commaParts.foreach { part =>
          if (failed.isEmpty) {
            val words = splitWhitespace(part)
            words.zipWithIndex.foreach { case (word, wordIndex) =>
              if (failed.isEmpty) {
                parseModifier(word) match {
                  case Some(modifier)                              => modifiers += modifier
                  case None if wordIndex > 0 && isIdentifier(word) => modifiers += Modifier(word.toLowerCase)
                  case None if wordIndex > 0                       => modifiers += Modifier(word)
                  case None                                        =>
                    parseOperand(word, line, text.indexOf(word) + 1) match {
                      case Right(operand) => operands += operand
                      case Left(error)    => failed = Some(error)
                    }
                }
              }
            }
          }
        }
        failed.toLeft(Instruction(opcode, operands.result(), modifiers.result()))
      }
    }
  }

  private def parseOperand(text: String, line: Int, column: Int): Either[ParseError, Operand] =
    if (text.startsWith("-") && text.length > 1 && !looksNumeric(text))
      parseOperand(text.drop(1), line, column + 1).map(Operand.Negated.apply)
    else if (text.startsWith("|") && text.endsWith("|") && text.length > 2)
      parseOperand(text.slice(1, text.length - 1), line, column + 1).map(Operand.Absolute.apply)
    else parseRegister(text).orElse(parseNumber(text)).orElse(parseAtom(text)) match {
      case Some(value) => Right(value)
      case None        => Left(ParseError(line, column, s"invalid operand '$text'"))
    }

  private def parseRegister(text: String): Option[Operand] = {
    val Single                             = "(?i)(s|v|a|ttmp)([0-9]+)".r
    val Range                              = "(?i)(s|v|a|ttmp)\\[([0-9]+):([0-9]+)\\]".r
    def file(prefix: String): RegisterFile = prefix.toLowerCase match {
      case "s"    => RegisterFile.Scalar
      case "v"    => RegisterFile.Vector
      case "a"    => RegisterFile.Accumulator
      case "ttmp" => RegisterFile.TrapTemporary
    }
    text match {
      case Single(prefix, number) => Some(Operand.Register(file(prefix), number.toInt, number.toInt))
      case Range(prefix, first, last) if first.toInt <= last.toInt =>
        Some(Operand.Register(file(prefix), first.toInt, last.toInt))
      case _ if SpecialRegisters.contains(text.toLowerCase) => Some(Operand.SpecialRegister(text.toLowerCase))
      case _                                                => None
    }
  }

  private def parseNumber(text: String): Option[Operand] = {
    val signed = if (text.startsWith("-") || text.startsWith("+")) text.drop(1) else text
    val sign   = if (text.startsWith("-")) -1 else 1
    if (signed.matches("0[xX][0-9a-fA-F]+"))
      Some(Operand.IntegerLiteral(BigInt(signed.drop(2), 16) * sign, 16))
    else if (signed.matches("[0-9]+")) Some(Operand.IntegerLiteral(BigInt(signed) * sign, 10))
    else if (text.matches("[+-]?([0-9]+\\.[0-9]*|[0-9]*\\.[0-9]+)([eE][+-]?[0-9]+)?"))
      Some(Operand.DecimalLiteral(text.toLowerCase))
    else None
  }

  private def parseAtom(text: String): Option[Operand] =
    if (isIdentifier(text)) Some(Operand.Identifier(text))
    else if (balanced(text) && text.nonEmpty) Some(Operand.Expression(text))
    else None

  private def parseModifier(text: String): Option[Modifier] = {
    val colon = topLevelColon(text)
    if (colon > 0 && isIdentifier(text.take(colon)))
      Some(Modifier(text.take(colon).toLowerCase, Some(text.drop(colon + 1))))
    else if (KnownFlagModifiers.contains(text.toLowerCase)) Some(Modifier(text.toLowerCase))
    else None
  }

  private def splitTopLevel(text: String, separator: Char, line: Int): Either[ParseError, Vector[String]] = {
    if (text.isEmpty) return Right(Vector.empty)
    val result = ArrayBuffer.empty[String]
    var start  = 0
    var round  = 0
    var square = 0
    var index  = 0
    while (index < text.length) {
      text(index) match {
        case '('                                                    => round += 1
        case ')'                                                    => round -= 1
        case '['                                                    => square += 1
        case ']'                                                    => square -= 1
        case char if char == separator && round == 0 && square == 0 =>
          val part = text.slice(start, index).trim
          if (part.isEmpty) return Left(ParseError(line, index + 1, "empty operand"))
          result += part
          start = index + 1
        case _ => ()
      }
      if (round < 0 || square < 0) return Left(ParseError(line, index + 1, "unmatched closing delimiter"))
      index += 1
    }
    if (round != 0 || square != 0) Left(ParseError(line, text.length, "unclosed delimiter"))
    else {
      val last = text.drop(start).trim
      if (last.isEmpty) Left(ParseError(line, text.length, "empty operand"))
      else Right((result += last).toVector)
    }
  }

  private def splitWhitespace(text: String): Vector[String] = {
    val result = ArrayBuffer.empty[String]
    var start  = -1
    var round  = 0
    var square = 0
    var index  = 0
    while (index <= text.length) {
      val char = if (index == text.length) ' ' else text(index)
      if (start < 0 && !char.isWhitespace) start = index
      if (start >= 0) {
        char match {
          case '(' => round += 1
          case ')' => round -= 1
          case '[' => square += 1
          case ']' => square -= 1
          case _   => ()
        }
        if (char.isWhitespace && round == 0 && square == 0) {
          result += text.slice(start, index)
          start = -1
        }
      }
      index += 1
    }
    result.toVector
  }

  private def printStatement(statement: Statement): String = statement match {
    case Statement.Instruction(value)                              => print(value)
    case Statement.InstructionPair(left, right)                    => s"${print(left)} :: ${print(right)}"
    case Statement.Label(name)                                     => s"$name:"
    case Statement.Directive(name, arguments) if arguments.isEmpty => name
    case Statement.Directive(name, arguments)                      => s"$name $arguments"
  }

  private def printOperand(operand: Operand): String = operand match {
    case Operand.Register(file, first, last) =>
      val prefix = file match {
        case RegisterFile.Scalar        => "s"
        case RegisterFile.Vector        => "v"
        case RegisterFile.Accumulator   => "a"
        case RegisterFile.TrapTemporary => "ttmp"
      }
      if (first == last) s"$prefix$first" else s"$prefix[$first:$last]"
    case Operand.SpecialRegister(name)     => name
    case Operand.IntegerLiteral(value, 16) =>
      if (value.signum < 0) s"-0x${(-value).toString(16)}" else s"0x${value.toString(16)}"
    case Operand.IntegerLiteral(value, _) => value.toString
    case Operand.DecimalLiteral(value)    => value
    case Operand.Identifier(name)         => name
    case Operand.Expression(text)         => text
    case Operand.Negated(value)           => s"-${printOperand(value)}"
    case Operand.Absolute(value)          => s"|${printOperand(value)}|"
  }

  private def stripComment(source: String): String = {
    val slash = source.indexOf("//")
    val hash  = source.indexOf('#')
    val end   = Vector(slash, hash).filter(_ >= 0).minOption.getOrElse(source.length)
    source.take(end)
  }

  private def topLevelColon(text: String): Int = {
    var square = 0
    var round  = 0
    var index  = 0
    while (index < text.length) {
      text(index) match {
        case '['                              => square += 1
        case ']'                              => square -= 1
        case '('                              => round += 1
        case ')'                              => round -= 1
        case ':' if square == 0 && round == 0 => return index
        case _                                => ()
      }
      index += 1
    }
    -1
  }

  private def balanced(text: String): Boolean = {
    var round  = 0
    var square = 0
    text.foreach {
      case '(' => round += 1
      case ')' => round -= 1
      case '[' => square += 1
      case ']' => square -= 1
      case _   => ()
    }
    round == 0 && square == 0
  }

  private def looksNumeric(text: String): Boolean =
    text.dropWhile(char => char == '+' || char == '-').headOption.exists(_.isDigit)
  private def isIdentifier(text: String): Boolean = text.matches("[A-Za-z_$][A-Za-z0-9_.$]*")

  private val KnownFlagModifiers = Set(
    "a16",
    "addr64",
    "clamp",
    "d16",
    "da",
    "dlc",
    "done",
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
