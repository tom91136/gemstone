package net.kurobako.gemstone.isa.sass.asm

import cats.syntax.all.*
import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.*
import net.kurobako.gemstone.isa.sass.CodeWords.BundleControl as Control
import net.kurobako.gemstone.isa.sass.asm.Program.Statement.*
import net.kurobako.gemstone.string.*
import net.kurobako.gemstone.text.*
import scala.annotation.tailrec

/** Parses labelled programs and instructions into the generated instruction cases produced by binary decoding. */
object Parser {
  def parse(source: String): Either[String, Instruction] = InstructionSyntax.parse(source)

  private def syntax(position: SourcePosition, value: String): Error =
    Error.Syntax(SourceError(position, Error.detail(value)))

  def parseProgram(source: String): Either[Error, Program] = {
    val initial = Program(None, Vec.empty)
    source.linesIterator.zipWithIndex.foldLeft[Either[Error, Program]](Right(initial)) {
      case (result, (line, index)) => result.flatMap { program =>
          def at(offset: Int): SourcePosition =
            SourcePosition((index + 1).unsafe[Int > 0], (offset + 1).unsafe[Int > 0])
          val comment    = line.indexOf("//")
          val body       = if (comment < 0) line else line.take(comment)
          val annotation = if (comment < 0) None
          else {
            val text = line.drop(comment + 2).trim
            Option.when(text.startsWith("encoding:"))(text.drop("encoding:".length).trim)
          }
          val separators = body.indices.filter(body(_) == ';').toVec
          val parts      = (-1 +: separators).zip(separators :+ body.length).toVec.flatMap { (separator, end) =>
            val start = body.indexWhere(_ > ' ', separator + 1)
            Option.when(start >= 0 && start < end)(start -> body.substring(start, end).trim)
          }
          if (parts.isEmpty && annotation.nonEmpty) Left(syntax(at(comment), "encoding annotation has no instruction"))
          else parts.zipWithIndex.foldLeft[Either[Error, Program]](Right(program)) {
            case (state, ((start, part), partIndex)) => state.flatMap(value =>
                parseStatement(value, part, if (partIndex == parts.size - 1) annotation else None, at(start))
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
      position: SourcePosition
  ): Either[Error, Program] = {
    val (prefix, rest) =
      source.span(character => character.isLetterOrDigit || character == '_' || character == '.' || character == '$')
    if (rest.startsWith(":")) {
      if (!validName(prefix) || annotation.nonEmpty && rest.drop(1).trim.isEmpty)
        Left(syntax(position, "invalid label declaration"))
      else {
        val labelled  = program.copy(statements = program.statements :+ Label(prefix.unsafe[NonEmpty[String]]))
        val remaining = rest.drop(1).trim
        val column    = (position.column.raw + source.length - remaining.length).unsafe[Int > 0]
        if (remaining.isEmpty) Right(labelled)
        else parseStatement(labelled, remaining, annotation, position.copy(column = column))
      }
    } else if (source.startsWith(".")) {
      if (annotation.nonEmpty) Left(syntax(position, "directives cannot carry instruction annotations"))
      else tokens(source) match {
        case Vec(".target", target) =>
          if (program.target.nonEmpty) Left(syntax(position, "duplicate .target directive"))
          else Target.parse(target).left.map(syntax(position, _)).map(value => program.copy(target = Some(value)))
        case values if values.headOption.contains(".bundle") =>
          parseControl(values.drop(1)).left.map(syntax(position, _))
            .map(value => program.copy(statements = program.statements :+ Bundle(value)))
        case _ => Left(syntax(position, s"unsupported directive $source"))
      }
    } else {
      val operation: Either[String, Operation] = annotation match {
        case Some(text) => for {
            annotation <- MachineSyntax.parse(text)
            result     <- MachineSyntax.decode(source, annotation)
          } yield Operation(result.instruction.operation, Some(result.instruction))
        case None => parseOperation(source)
      }
      operation.left.map(syntax(position, _)).map(value => program.copy(statements = program.statements :+ value))
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
          reference = Some(
            Program.LabelReference((instruction.operands.size - 1).unsafe[Int >= 0], end.unsafe[NonEmpty[String]])
          )
        ))
      }
  }

  private def directTarget(instruction: Instruction): Boolean =
    ControlFlow.directTarget(instruction) && instruction.operands.lastOption.exists {
      case Operand.Integer(_) | Operand.Bits(_, _) => true
      case _                                       => false
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

  private def hex(source: String): Either[String, BigInt] =
    parseBigIntLiteral(s"0x${source.stripPrefix("0x")}").toRight(s"invalid hexadecimal value $source")

  private def parseControl(values: Vec[String]): Either[String, Control] = values match {
    case Vec("kepler", bytes) if bytes.length == 16 =>
      bytes.grouped(2).toVec.traverse(hex(_).map(_.toByte)).map(Control.Kepler(_))
    case Vec("maxwell", reserved, first, second, third) => for {
        bit <-
          reserved.toIntOption.toRight("invalid Maxwell reserved bit").flatMap(_.refine[Int in (0, 1)].left.map(_.raw))
        slots <- Vec(first, second, third).traverse { source =>
          hex(source).flatMap(value =>
            if (!value.isValidInt) Left("Maxwell control exceeds 21 bits")
            else value.toInt.refine[CodeWords.ControlBits].left.map(_.raw)
          )
        }
      } yield Control.Maxwell(slots, bit)
    case _ => Left("expected .bundle kepler <16 hex digits> or .bundle maxwell <0|1> <three hex controls>")
  }

  def symbolicReference(operation: Operation): Either[String, Program.LabelReference] =
    operation.reference.filter(reference =>
      directTarget(operation.instruction) &&
        reference.operand.raw == operation.instruction.operands.size - 1 && validName(reference.name.raw)
    ).toRight("label reference must name exactly one direct control-flow destination")
}
