package net.kurobako.gemstone.isa.amdgpu.asm

import cats.syntax.all.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.amdgpu.*
import net.kurobako.gemstone.isa.amdgpu.asm.Binary.*
import net.kurobako.gemstone.isa.amdgpu.asm.Program.*

object Assembler {
  private enum Part {
    case Bytes(value: Vec[Byte])
    case Branch(offset: Int, instruction: Instruction, expression: String)
    case Data(offset: Int, width: Int, expressions: Vec[String])
  }
  private case class State(offset: Int, labels: Map[String, BigInt], parts: Vec[Part])
  private val DataWidths =
    Map(".byte" -> 1, ".short" -> 2, ".hword" -> 2, ".long" -> 4, ".int" -> 4, ".quad" -> 8)
  private val Term = "([+-]?)(0[xX][0-9a-fA-F]+|[0-9]+|[A-Za-z_.$][A-Za-z0-9_.$]*)".r

  private def evaluate(expression: String, symbols: Map[String, BigInt], offset: Int): Either[CodecError, BigInt] = {
    val text  = expression.filterNot(_.isWhitespace)
    val terms = Term.findAllMatchIn(text).toVector
    if (terms.isEmpty || terms.map(_.matched).mkString != text || terms.drop(1).exists(_.group(1).isEmpty))
      Left(CodecError.InvalidOperand(s"invalid assembly expression '$expression'"))
    else terms.traverse { term =>
      val name  = term.group(2)
      val value = if (name == ".") Some(BigInt(offset))
      else if (name.startsWith("0x") || name.startsWith("0X")) Some(BigInt(name.drop(2), 16))
      else if (name.head.isDigit) Some(BigInt(name))
      else symbols.get(name)
      value.toRight(CodecError.InvalidOperand(s"undefined symbol '$name'")).map(value =>
        if (term.group(1) == "-") -value else value
      )
    }.map(_.sum)
  }

  private def branch(instruction: Instruction, target: Target): Option[String] =
    Option.when(Metadata.encodingsFor(instruction.definition, target.isa).exists(value =>
      value.format == EncodingFormat.SOPP && value.operands.exists(_.kind == OperandKind.LABEL)
    ))(instruction.operands)
      .flatMap {
        case Vec(Operand.Identifier(name)) => Some(name)
        case Vec(Operand.Expression(text)) => Some(text)
        case _                             => None
      }

  private def withOffset(instruction: Instruction, value: BigInt): Either[CodecError, Instruction] =
    Instruction.decode(instruction.definition, Vec(Operand.IntegerLiteral(value, 10)), instruction.mods) match {
      case Vec(result) => Right(result)
      case _           => Left(CodecError.InvalidOperand(s"invalid branch operands for ${instruction.opcode}"))
    }

  def encode(program: Program, target: Target, waveSize: Int in (32, 64) = 32): Either[CodecError, Vec[Byte]] = {
    val planned = program.statements.foldLeft[Either[CodecError, State]](Right(State(0, Map.empty, Vec.empty))) {
      (state, statement) =>
        state.flatMap { state =>
          def append(part: Part, count: Int): Either[CodecError, State] =
            Either.cond(
              count >= 0 && state.offset <= Int.MaxValue - count,
              state.copy(offset = state.offset + count, parts = state.parts :+ part),
              CodecError.InvalidOperand("assembly exceeds maximum byte size")
            )
          def bytes(value: Vec[Byte]): Either[CodecError, State] = append(Part.Bytes(value), value.size)
          statement match {
            case Statement.Comment(_)  => Right(state)
            case Statement.Label(name) =>
              Either.cond(
                !state.labels.contains(name),
                state.copy(labels = state.labels.updated(name, BigInt(state.offset))),
                CodecError.InvalidOperand(s"duplicate label '$name'")
              )
            case Statement.Instruction(instruction) => branch(instruction, target) match {
                case Some(expression) => append(Part.Branch(state.offset, instruction, expression), 4)
                case None             => Binary.encode(instruction, target, waveSize).flatMap(bytes(_))
              }
            case pair @ Statement.InstructionPair(_, _) =>
              Binary.encodeStatement(pair, target, waveSize).flatMap(bytes(_))
            case Statement.Directive(".text", "") => Right(state)
            case Statement.Directive(".section", args) if args.split(',').headOption.exists(_.trim == ".text") =>
              Right(state)
            case Statement.Directive(name, _)
                if Set(".globl", ".global", ".local", ".hidden", ".type", ".size")(name) => Right(state)
            case Statement.Directive(name, args) if DataWidths.contains(name) =>
              val expressions = args.split(",", -1).toVec.map(_.trim)
              val width       = DataWidths(name)
              if (expressions.exists(_.isEmpty)) Left(CodecError.InvalidOperand(s"$name requires data expressions"))
              else append(Part.Data(state.offset, width, expressions), Math.multiplyExact(width, expressions.size))
            case Statement.Directive(name, args) if Set(".set", ".equ")(name) =>
              args.split(",", -1).toVector.map(_.trim) match {
                case Vector(symbol, expression) if symbol.matches("[A-Za-z_.$][A-Za-z0-9_.$]*") =>
                  if (state.labels.contains(symbol)) Left(CodecError.InvalidOperand(s"duplicate symbol '$symbol'"))
                  else evaluate(expression, state.labels, state.offset).map(value =>
                    state.copy(labels = state.labels.updated(symbol, value))
                  )
                case _ => Left(CodecError.InvalidOperand(s"invalid $name directive"))
              }
            case Statement.Directive(name, args) if Set(".p2align", ".align", ".balign", ".zero", ".space")(name) =>
              val values = args.split(",", -1).toVec.map(_.trim)
              values.traverse(evaluate(_, state.labels, state.offset)).flatMap {
                case Vec(amount)       => padding(state.offset, name, amount, None, target).flatMap(bytes(_))
                case Vec(amount, fill) => padding(state.offset, name, amount, Some(fill), target).flatMap(bytes(_))
                case _                 => Left(CodecError.InvalidOperand(s"invalid $name directive"))
              }
            case Statement.Directive(name, _) =>
              Left(CodecError.InvalidOperand(s"unsupported directive '$name' in a raw instruction stream"))
          }
        }
    }
    planned.flatMap { state =>
      state.parts.traverse {
        case Part.Bytes(value)                            => Right(value)
        case Part.Branch(offset, instruction, expression) =>
          evaluate(expression, state.labels, offset).flatMap { address =>
            val delta = address - offset - 4
            if (delta % 4 != 0 || delta / 4 < Short.MinValue || delta / 4 > Short.MaxValue)
              Left(CodecError.InvalidOperand(s"branch target '$expression' is unaligned or out of range"))
            else withOffset(instruction, delta / 4).flatMap(Binary.encode(_, target, waveSize))
          }
        case Part.Data(offset, width, expressions) => expressions.zipWithIndex.traverse { case (expression, index) =>
            evaluate(expression, state.labels, offset + index * width).flatMap { value =>
              if (value < -(BigInt(1) << (width * 8 - 1)) || value >= (BigInt(1) << (width * 8)))
                Left(CodecError.InvalidOperand(s"$width-byte data value is out of range: $expression"))
              else Right(Vec.from((0 until width).map(index => ((value >> (index * 8)) & 255).toByte)))
            }
          }.map(_.flatten)
      }.map(_.flatten)
    }
  }

  private def padding(
      offset: Int,
      name: String,
      amount: BigInt,
      fill: Option[BigInt],
      target: Target
  ): Either[CodecError, Vec[Byte]] = {
    val power = name == ".p2align"
    if (amount < 0 || !amount.isValidInt || (power && amount > 30) || fill.exists(value => value < 0 || value > 255))
      Left(CodecError.InvalidOperand(s"invalid $name amount or fill"))
    else {
      val boundary  = if (power) 1 << amount.toInt else amount.toInt
      val alignment = name == ".align" || name == ".balign"
      val count     = if (name == ".zero" || name == ".space") boundary
      else if (boundary == 0) 0 else (boundary - offset % boundary) % boundary
      if (alignment && boundary != 0 && (boundary & (boundary - 1)) != 0)
        Left(CodecError.InvalidOperand(s"$name requires a power-of-two alignment"))
      else if (count > Int.MaxValue - offset) Left(CodecError.InvalidOperand("padding exceeds maximum byte size"))
      else fill match {
        case Some(value)                                 => Right(Vec.fill(count)(value.toByte))
        case None if name == ".zero" || name == ".space" => Right(Vec.fill(count)(0.toByte))
        case None => Binary.encode(Instruction.SNop(Operand.IntegerLiteral(BigInt(0), 10), Vec.empty), target)
            .map(nop => Vec.fill(count / 4)(nop).flatten ++ Vec.fill(count % 4)(0.toByte))
      }
    }
  }
}

class Assembler(target: Target, waveSize: Int in (32, 64) = 32) extends Encoder[Program, Vec[Byte], CodecError] {
  override def encode(program: Program): Either[CodecError, Vec[Byte]] = Assembler.encode(program, target, waveSize)
}
