package net.kurobako.gemstone.isa.sass.asm

import cats.syntax.all.*
import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.*
import net.kurobako.gemstone.isa.sass.SASSCodeWords.BundleControl as Control
import net.kurobako.gemstone.isa.sass.asm.Program.Statement.*
import scala.annotation.tailrec

/** Parses labeled programs and instructions into the generated instruction cases produced by binary decoding. */
object Parser extends isa.Decoder[String, Program, Error] {
  def parseProgram(source: String): Either[Error, Program] = decode(source)

  def parse(source: String): Either[String, Instruction] = InstructionSyntax.parse(source)

  private def syntax(line: Int, value: String): Error = Error.Syntax(line.unsafe[Int > 0], Error.detail(value))

  override def decode(source: String): Either[Error, Program] = {
    val initial = Program(None, Vec.empty)
    source.linesIterator.zipWithIndex.foldLeft[Either[Error, Program]](Right(initial)) {
      case (result, (line, index)) => result.flatMap { program =>
          val comment    = line.indexOf("//")
          val body       = (if (comment < 0) line else line.take(comment)).trim
          val annotation = if (comment < 0) None
          else {
            val text = line.drop(comment + 2).trim
            Option.when(text.startsWith("encoding:"))(text.drop("encoding:".length).trim)
          }
          val parts = body.split(';').toVec.map(_.trim).filter(_.nonEmpty)
          if (parts.isEmpty && annotation.nonEmpty) Left(syntax(index + 1, "encoding annotation has no instruction"))
          else parts.zipWithIndex.foldLeft[Either[Error, Program]](Right(program)) {
            case (state, (part, partIndex)) => state.flatMap(value =>
                parseStatement(value, part, if (partIndex == parts.size - 1) annotation else None, index + 1)
              )
          }
        }
    }.flatMap { program =>
      program.statements.foldLeft[Either[Error, Set[String]]](Right(Set.empty)) {
        case (state, Label(name)) => state.flatMap { names =>
            if (names(name.raw)) Left(Error.DuplicateLabel(name)) else Right(names + name.raw)
          }
        case (state, _) => state
      }.map(_ => program)
    }
  }

  @tailrec
  private def parseStatement(
      program: Program,
      source: String,
      annotation: Option[String],
      line: Int
  ): Either[Error, Program] = {
    val (prefix, rest) =
      source.span(character => character.isLetterOrDigit || character == '_' || character == '.' || character == '$')
    if (rest.startsWith(":")) {
      if (!validName(prefix) || annotation.nonEmpty && rest.drop(1).trim.isEmpty)
        Left(syntax(line, "invalid label declaration"))
      else {
        val labeled   = program.copy(statements = program.statements :+ Label(prefix.unsafe[NonEmpty[String]]))
        val remaining = rest.drop(1).trim
        if (remaining.isEmpty) Right(labeled) else parseStatement(labeled, remaining, annotation, line)
      }
    } else if (source.startsWith(".")) {
      if (annotation.nonEmpty) Left(syntax(line, "directives cannot carry instruction annotations"))
      else tokens(source) match {
        case Vec(".target", target) =>
          if (program.target.nonEmpty) Left(syntax(line, "duplicate .target directive"))
          else Target.parse(target).left.map(syntax(line, _)).map(value => program.copy(target = Some(value)))
        case values if values.headOption.contains(".bundle") =>
          parseControl(values.drop(1)).left.map(syntax(line, _))
            .map(value => program.copy(statements = program.statements :+ Bundle(value)))
        case _ => Left(syntax(line, s"unsupported directive $source"))
      }
    } else {
      val operation: Either[String, Operation] = annotation match {
        case Some(text) => for {
            annotation <- MachineSyntax.parse(text)
            result     <- MachineSyntax.decode(source, annotation)
          } yield Operation(result.instruction.operation, Some(result.instruction))
        case None => parseOperation(source)
      }
      operation.left.map(syntax(line, _)).map(value => program.copy(statements = program.statements :+ value))
    }
  }

  private def parseOperation(source: String): Either[String, Operation] = Parser.parse(source) match {
    case Right(value)   => Right(Operation(value))
    case Left(original) =>
      val end = source.reverse.takeWhile(character =>
        character.isLetterOrDigit || character == '_' || character == '.' || character == '$'
      ).reverse
      if (!validName(end)) Left(original)
      else Parser.parse(source.dropRight(end.length) + "0x0").flatMap { instruction =>
        if (!directTarget(instruction)) Left(original)
        else Right(Operation(
          instruction,
          references = Vec(
            Program.LabelReference((instruction.operands.size - 1).unsafe[Int >= 0], end.unsafe[NonEmpty[String]])
          )
        ))
      }
  }

  private def directTarget(instruction: Instruction): Boolean = instruction match {
    case _: Instruction.Bra | _: Instruction.Cal | _: Instruction.Call | _: Instruction.Jcal |
        _: Instruction.Jmp | _: Instruction.Bssy | _: Instruction.Ssy | _: Instruction.Pbk |
        _: Instruction.Pcnt | _: Instruction.Pret => instruction.operands.lastOption.exists {
        case Operand.Integer(_) | Operand.Bits(_, _) => true
        case _                                       => false
      }
    case _ => false
  }

  def validName(value: String): Boolean = value.headOption.exists(character =>
    character.isLetter || character == '_' || character == '.' || character == '$'
  ) && value.forall(character => character.isLetterOrDigit || character == '_' || character == '.' || character == '$')

  private def tokens(source: String): Vec[String] = {
    @tailrec
    def loop(rest: String, result: Vec[String]): Vec[String] =
      if (rest.isEmpty) result
      else {
        val (token, remaining) = rest.span(!_.isWhitespace)
        loop(remaining.trim, result :+ token)
      }
    loop(source.trim, Vec.empty)
  }

  private def hex(source: String): Either[String, BigInt] = {
    val digits = source.stripPrefix("0x")
    if (digits.isEmpty || !digits.forall(character => Character.digit(character, 16) >= 0))
      Left(s"invalid hexadecimal value $source")
    else Right(digits.foldLeft(BigInt(0))((value, character) => value * 16 + Character.digit(character, 16)))
  }

  private def parseControl(values: Vec[String]): Either[String, Control] = values match {
    case Vec("kepler", bytes) if bytes.length == 16 =>
      bytes.grouped(2).toVec.traverse(hex(_).map(_.toByte)).map(Control.Kepler(_))
    case Vec("maxwell", reserved, first, second, third) => for {
        bit <-
          reserved.toIntOption.toRight("invalid Maxwell reserved bit").flatMap(_.refine[Int in (0, 1)].left.map(_.raw))
        slots <- Vec(first, second, third).traverse { source =>
          hex(source).flatMap(value =>
            if (!value.isValidInt) Left("Maxwell control exceeds 21 bits")
            else value.toInt.refine[SASSCodeWords.ControlBits].left.map(_.raw).map(SASSCodeWords.ControlWord(_))
          )
        }
      } yield Control.Maxwell(slots, bit)
    case _ => Left("expected .bundle kepler <16 hex digits> or .bundle maxwell <0|1> <three hex controls>")
  }

  def symbolicReference(operation: Operation): Either[String, Program.LabelReference] =
    operation.references match {
      case Vec(reference)
          if directTarget(operation.instruction) &&
            reference.operand.raw == operation.instruction.operands.size - 1 && validName(reference.name.raw) =>
        Right(reference)
      case _ => Left("label reference must name exactly one direct control-flow destination")
    }
}
