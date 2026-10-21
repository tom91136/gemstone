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
    Reader(opcode, words, kinds).decodeShapes(shapes, index = 0, offset = 0, Chain.empty)
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

  /** Decodes the operands of one instruction from its `words`. */
  private class Reader(opcode: NonEmpty[String], words: Vec[Word], kinds: Map[OperandKind, OperandKindInfo]) {
    def decodeShapes(
        shapes: Vec[OperandShape],
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
        val shape  = shapes(index)
        def single = decodeOne(shape.kind, offset).flatMap { case (operand, next) =>
          decodeShapes(shapes, index + 1, next, fields.append(Vec(operand)))
        }
        shape.quantifier match {
          case OperandQuantifier.One      => single
          case OperandQuantifier.Optional =>
            if (offset == words.size) decodeShapes(shapes, index + 1, offset, fields.append(Vec.empty))
            else single
          case OperandQuantifier.Repeated =>
            decodeRepeated(shape.kind, offset, Chain.empty).flatMap { case (operands, next) =>
              decodeShapes(shapes, index + 1, next, fields.append(Vec.from(operands.toVector.view)))
            }
        }
      }

    private def decodeRepeated(
        kind: OperandKind,
        offset: Int,
        operands: Chain[Operand]
    ): Either[InstructionDecodeError, (Chain[Operand], Int)] =
      if (offset >= words.size) Right(operands -> offset)
      else
        decodeOne(kind, offset).flatMap { case (operand, next) =>
          decodeRepeated(kind, next, operands.append(operand))
        }

    private def decodeOne(kind: OperandKind, offset: Int): Either[InstructionDecodeError, (Operand, Int)] =
      if (offset >= words.size)
        Left(InstructionDecodeError.InvalidOperandCount(opcode, s"missing $kind at word $offset"))
      else if (kind == OperandKind.LiteralString) decodeString(kind, offset)
      else if (kind == OperandKind.LiteralContextDependentNumber)
        Right(Operand(kind, words.drop(offset).unsafe) -> words.size)
      else if (kind.category == OperandKind.Category.Composite)
        decodeSpan(kind, offset, offset, kind.components)
      else kinds.get(kind) match {
        case Some(info) if info.kind.category == OperandKind.Category.ValueEnum => decodeValueEnum(info, offset)
        case Some(info) if info.kind.category == OperandKind.Category.BitEnum   => decodeBitEnum(info, offset)
        case _ => Right(Operand(kind, Vec.nonEmpty(words(offset))) -> (offset + 1))
      }

    private def decodeString(kind: OperandKind, offset: Int): Either[InstructionDecodeError, (Operand, Int)] =
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

    private def decodeValueEnum(info: OperandKindInfo, offset: Int): Either[InstructionDecodeError, (Operand, Int)] = {
      val value = words(offset).toUnsigned.toLong
      info.enumerants.find(_.value == value) match {
        case None            => Left(InstructionDecodeError.InvalidEnumerant(info.kind, value))
        case Some(enumerant) => decodeSpan(info.kind, offset, offset + 1, enumerant.parameterKinds)
      }
    }

    private def decodeBitEnum(info: OperandKindInfo, offset: Int): Either[InstructionDecodeError, (Operand, Int)] = {
      val value       = words(offset).toUnsigned.toLong
      val enumerants  = ExtensionEnumerants.of(info)
      val allowedMask = enumerants.foldLeft(0L)(_ | _.value)
      if ((value & ~allowedMask) != 0L) Left(InstructionDecodeError.InvalidEnumerant(info.kind, value))
      else {
        val parameterKinds = enumerants.filter { enumerant =>
          if (enumerant.value == 0L) value == 0L else (value & enumerant.value) == enumerant.value
        }.flatMap(_.parameterKinds)
        decodeSpan(info.kind, offset, offset + 1, parameterKinds)
      }
    }

    /** One operand of `outerKind` from `offset`, whose nested operands of `parts` start at `first`. */
    private def decodeSpan(
        outerKind: OperandKind,
        offset: Int,
        first: Int,
        parts: Seq[OperandKind]
    ): Either[InstructionDecodeError, (Operand, Int)] =
      parts
        .foldLeft[Either[InstructionDecodeError, Int]](Right(first)) { (next, part) =>
          next.flatMap(current => decodeOne(part, current).map(_._2))
        }
        .map(next => Operand(outerKind, words.slice(offset, next).unsafe) -> next)
  }
}
