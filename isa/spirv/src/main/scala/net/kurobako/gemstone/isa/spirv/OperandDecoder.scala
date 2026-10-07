package net.kurobako.gemstone.isa.spirv

import cats.data.Chain
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

private object OperandDecoder {
  case class DecodedOperands(fields: Vec[Vec[Operand]]) {
    def required(index: Int): Operand         = fields(index).head
    def optional(index: Int): Option[Operand] = fields(index).headOption
    def repeated(index: Int): Vec[Operand]    = fields(index)
  }

  def decode(
      opcode: NonEmpty[String],
      words: Vec[Word],
      shapes: Vec[OperandShape],
      kinds: Map[OperandKind, OperandKindInfo]
  ): Either[InstructionDecodeError, DecodedOperands] =
    decodeShapes(opcode, words, shapes, kinds, 0, 0, Chain.empty)
      .map(fields => DecodedOperands(Vec.from(fields.toVector.view)))
  def decodeSwitch64(
      opcode: NonEmpty[String],
      words: Vec[Word]
  ): Either[InstructionDecodeError, DecodedOperands] =
    if (words.size < 2 || (words.size - 2) % 3 != 0)
      Left(InstructionDecodeError.InvalidOperandCount(
        opcode,
        "literal/label pairs are not 64-bit aligned"
      ))
    else {
      val selector = Operand(OperandKind.IdRef, Vec.nonEmpty(words(0)))
      val default  = Operand(OperandKind.IdRef, Vec.nonEmpty(words(1)))
      val targets  = (2 until words.size by 3).map(index =>
        Operand(
          OperandKind.PairLiteralIntegerIdRef,
          words.slice(index, index + 3).unsafe
        )
      )
      Right(DecodedOperands(Vec(Vec(selector), Vec(default), Vec.from(targets))))
    }

  private def decodeShapes(
      opcode: NonEmpty[String],
      words: Vec[Word],
      shapes: Vec[OperandShape],
      kinds: Map[OperandKind, OperandKindInfo],
      index: Int,
      offset: Int,
      fields: Chain[Vec[Operand]]
  ): Either[InstructionDecodeError, Chain[Vec[Operand]]] =
    if (index >= shapes.size) {
      if (offset == words.size) Right(fields)
      else Left(InstructionDecodeError.InvalidOperandCount(
        opcode,
        s"${words.size - offset} trailing words"
      ))
    } else {
      val shape = shapes(index)
      shape.quantifier match {
        case OperandQuantifier.One =>
          decodeOne(opcode, shape.kind, words, offset, kinds).flatMap { case (operand, next) =>
            decodeShapes(opcode, words, shapes, kinds, index + 1, next, fields.append(Vec(operand)))
          }
        case OperandQuantifier.Optional =>
          if (offset == words.size)
            decodeShapes(opcode, words, shapes, kinds, index + 1, offset, fields.append(Vec.empty))
          else
            decodeOne(opcode, shape.kind, words, offset, kinds).flatMap { case (operand, next) =>
              decodeShapes(opcode, words, shapes, kinds, index + 1, next, fields.append(Vec(operand)))
            }
        case OperandQuantifier.Repeated =>
          decodeRepeated(opcode, shape.kind, words, offset, kinds, Chain.empty).flatMap { case (operands, next) =>
            decodeShapes(
              opcode,
              words,
              shapes,
              kinds,
              index + 1,
              next,
              fields.append(Vec.from(operands.toVector.view))
            )
          }
      }
    }

  private def decodeRepeated(
      opcode: NonEmpty[String],
      kind: OperandKind,
      words: Vec[Word],
      offset: Int,
      kinds: Map[OperandKind, OperandKindInfo],
      operands: Chain[Operand]
  ): Either[InstructionDecodeError, (Chain[Operand], Int)] =
    if (offset >= words.size) Right(operands -> offset)
    else
      decodeOne(opcode, kind, words, offset, kinds).flatMap { case (operand, next) =>
        decodeRepeated(opcode, kind, words, next, kinds, operands.append(operand))
      }

  private def decodeOne(
      opcode: NonEmpty[String],
      kind: OperandKind,
      words: Vec[Word],
      offset: Int,
      kinds: Map[OperandKind, OperandKindInfo]
  ): Either[InstructionDecodeError, (Operand, Int)] =
    if (offset >= words.size)
      Left(InstructionDecodeError.InvalidOperandCount(opcode, s"missing $kind at word $offset"))
    else if (kind == OperandKind.LiteralString) decodeString(opcode, kind, words, offset)
    else if (kind == OperandKind.LiteralContextDependentNumber)
      Right(Operand(kind, words.drop(offset).unsafe) -> words.size)
    else if (kind.category == OperandKind.Category.Composite) {
      kind.components.foldLeft[Either[InstructionDecodeError, Int]](Right(offset)) { (next, component) =>
        next.flatMap(current => decodeOne(opcode, component, words, current, kinds).map(_._2))
      }.map(next => Operand(kind, words.slice(offset, next).unsafe) -> next)
    } else kinds.get(kind) match {
      case Some(info) if info.kind.category == OperandKind.Category.ValueEnum =>
        decodeValueEnum(opcode, info, words, offset, kinds)
      case Some(info) if info.kind.category == OperandKind.Category.BitEnum =>
        decodeBitEnum(opcode, info, words, offset, kinds)
      case _ => Right(Operand(kind, Vec.nonEmpty(words(offset))) -> (offset + 1))
    }

  private def decodeString(
      opcode: NonEmpty[String],
      kind: OperandKind,
      words: Vec[Word],
      offset: Int
  ): Either[InstructionDecodeError, (Operand, Int)] =
    words.indexWhere(
      word =>
        (0 until 4).exists(byte =>
          Bits.unsignedField(word.toUnsigned.toLong, byte * 8, 8) == 0L
        ),
      offset
    ) match {
      case -1  => Left(InstructionDecodeError.UnterminatedString(opcode))
      case end => Right(Operand(kind, words.slice(offset, end + 1).unsafe) -> (end + 1))
    }

  private def decodeValueEnum(
      opcode: NonEmpty[String],
      info: OperandKindInfo,
      words: Vec[Word],
      offset: Int,
      kinds: Map[OperandKind, OperandKindInfo]
  ): Either[InstructionDecodeError, (Operand, Int)] = {
    val value = words(offset).toUnsigned.toLong
    info.enumerants.find(_.value == value) match {
      case None            => Left(InstructionDecodeError.InvalidEnumerant(info.kind, value))
      case Some(enumerant) => decodeParameters(opcode, info.kind, words, offset, enumerant.parameterKinds, kinds)
    }
  }

  private def decodeBitEnum(
      opcode: NonEmpty[String],
      info: OperandKindInfo,
      words: Vec[Word],
      offset: Int,
      kinds: Map[OperandKind, OperandKindInfo]
  ): Either[InstructionDecodeError, (Operand, Int)] = {
    val value       = words(offset).toUnsigned.toLong
    val allowedMask = info.enumerants.foldLeft(0L)(_ | _.value)
    if ((value & ~allowedMask) != 0L) Left(InstructionDecodeError.InvalidEnumerant(info.kind, value))
    else {
      val parameterKinds = info.enumerants.filter { enumerant =>
        if (enumerant.value == 0L) value == 0L else (value & enumerant.value) == enumerant.value
      }.flatMap(_.parameterKinds)
      decodeParameters(opcode, info.kind, words, offset, parameterKinds, kinds)
    }
  }

  private def decodeParameters(
      opcode: NonEmpty[String],
      outerKind: OperandKind,
      words: Vec[Word],
      offset: Int,
      parameterKinds: Vec[OperandKind],
      kinds: Map[OperandKind, OperandKindInfo]
  ): Either[InstructionDecodeError, (Operand, Int)] =
    parameterKinds
      .foldLeft[Either[InstructionDecodeError, Int]](Right(offset + 1)) { (next, parameterKind) =>
        next.flatMap(current => decodeOne(opcode, parameterKind, words, current, kinds).map(_._2))
      }
      .map(next => Operand(outerKind, words.slice(offset, next).unsafe) -> next)
}
