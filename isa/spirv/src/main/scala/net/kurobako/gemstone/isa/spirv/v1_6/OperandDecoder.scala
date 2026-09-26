package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.{NonEmptyVec, Vec}
import net.kurobako.gemstone.binary.{BitWidth, Bits}

import cats.data.Chain

private[v1_6] object OperandDecoder {
  final case class DecodedOperands(fields: Vec[Vec[Operand]]) {
    def required(index: Int): Operand         = fields(index).head
    def optional(index: Int): Option[Operand] = fields(index).headOption
    def repeated(index: Int): Vec[Operand]    = fields(index)
  }

  def decode(
      definition: Instruction.Definition,
      words: Vec[Word],
      shapes: Vec[OperandShape],
      kinds: Map[SPIRVOperandKind, OperandKindInfo]
  ): Either[InstructionDecodeError, DecodedOperands] =
    decodeShapes(definition.opcode, words, shapes, kinds, 0, 0, Chain.empty)
      .map(fields => DecodedOperands(Vec.from(fields.toVector.view))) match {
      case right @ Right(_)                                           => right
      case Left(error) if definition == Instruction.Definition.Switch =>
        // OpSwitch literals have the width of their selector.  The core
        // grammar represents the common 32-bit form as a pair, but a 64-bit
        // selector uses two literal words followed by the target label.  The
        // selector type is resolved after binary decoding, so accept the
        // lossless three-word form here when the fixed-width parse rejects it.
        decodeSwitch64(words) match {
          case right @ Right(_) => right
          case Left(_)          => Left(error)
        }
      case Left(error) => Left(error)
    }

  private def decodeSwitch64(
      words: Vec[Word]
  ): Either[InstructionDecodeError, DecodedOperands] =
    if (words.size < 2 || (words.size - 2) % 3 != 0)
      Left(InstructionDecodeError.InvalidOperandCount(
        Instruction.Definition.Switch.opcode,
        "literal/label pairs are not 64-bit aligned"
      ))
    else {
      val selector = Operand(SPIRVOperandKind("IdRef"), Vec.nonEmpty(words(0)))
      val default  = Operand(SPIRVOperandKind("IdRef"), Vec.nonEmpty(words(1)))
      val targets  = (2 until words.size by 3).map(index =>
        Operand(
          SPIRVOperandKind("PairLiteralIntegerIdRef"),
          NonEmptyVec.unsafeFromVec(words.slice(index, index + 3))
        )
      )
      Right(DecodedOperands(Vec(Vec(selector), Vec(default), Vec.from(targets))))
    }

  private def decodeShapes(
      opcode: SPIRVOpcode,
      words: Vec[Word],
      shapes: Vec[OperandShape],
      kinds: Map[SPIRVOperandKind, OperandKindInfo],
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
      opcode: SPIRVOpcode,
      kind: SPIRVOperandKind,
      words: Vec[Word],
      offset: Int,
      kinds: Map[SPIRVOperandKind, OperandKindInfo],
      operands: Chain[Operand]
  ): Either[InstructionDecodeError, (Chain[Operand], Int)] =
    if (offset >= words.size) Right(operands -> offset)
    else
      decodeOne(opcode, kind, words, offset, kinds).flatMap { case (operand, next) =>
        decodeRepeated(opcode, kind, words, next, kinds, operands.append(operand))
      }

  def minimumWords(shapes: Vec[OperandShape], kinds: Map[SPIRVOperandKind, OperandKindInfo]): Vec[Word] =
    shapes.flatMap { shape =>
      shape.quantifier match {
        case OperandQuantifier.One      => minimumOperand(shape.kind, kinds)
        case OperandQuantifier.Optional => Vec.empty[Word]
        case OperandQuantifier.Repeated => Vec.empty[Word]
      }
    }

  private def minimumOperand(kind: SPIRVOperandKind, kinds: Map[SPIRVOperandKind, OperandKindInfo]): Vec[Word] =
    if (kind.raw == "LiteralString") Vec(Word(0))
    else if (kind.raw.startsWith("Pair")) Vec(Word(0), Word(0))
    else kinds.get(kind) match {
      case Some(info) if info.enumerants.nonEmpty =>
        val selected = info.enumerants.minBy(enumerant => (enumerant.parameterKinds.size, enumerant.value))
        Vec(Word(selected.value.toInt)) ++ selected.parameterKinds.flatMap(parameter =>
          minimumOperand(parameter, kinds)
        )
      case _ => Vec(Word(0))
    }

  private def decodeOne(
      opcode: SPIRVOpcode,
      kind: SPIRVOperandKind,
      words: Vec[Word],
      offset: Int,
      kinds: Map[SPIRVOperandKind, OperandKindInfo]
  ): Either[InstructionDecodeError, (Operand, Int)] =
    if (offset >= words.size)
      Left(InstructionDecodeError.InvalidOperandCount(opcode, s"missing $kind at word $offset"))
    else if (kind.raw == "LiteralString") decodeString(opcode, kind, words, offset)
    else if (kind.raw == "LiteralContextDependentNumber")
      Right(Operand(kind, NonEmptyVec.unsafeFromVec(words.drop(offset))) -> words.size)
    else if (kind.raw.startsWith("Pair")) {
      if (offset + 2 > words.size)
        Left(InstructionDecodeError.InvalidOperandCount(opcode, s"short $kind at word $offset"))
      else Right(Operand(kind, NonEmptyVec.unsafeFromVec(words.slice(offset, offset + 2))) -> (offset + 2))
    } else kinds.get(kind) match {
      case Some(info) if info.category.raw == "ValueEnum" => decodeValueEnum(opcode, info, words, offset, kinds)
      case Some(info) if info.category.raw == "BitEnum"   => decodeBitEnum(opcode, info, words, offset, kinds)
      case _ => Right(Operand(kind, Vec.nonEmpty(words(offset))) -> (offset + 1))
    }

  private def decodeString(
      opcode: SPIRVOpcode,
      kind: SPIRVOperandKind,
      words: Vec[Word],
      offset: Int
  ): Either[InstructionDecodeError, (Operand, Int)] =
    words.indexWhere(
      word => (0 until 4).exists(byte => Bits.unsignedField(word.toUnsigned.raw, byte * 8, BitWidth(8)) == 0L),
      offset
    ) match {
      case -1  => Left(InstructionDecodeError.UnterminatedString(opcode))
      case end => Right(Operand(kind, NonEmptyVec.unsafeFromVec(words.slice(offset, end + 1))) -> (end + 1))
    }

  private def decodeValueEnum(
      opcode: SPIRVOpcode,
      info: OperandKindInfo,
      words: Vec[Word],
      offset: Int,
      kinds: Map[SPIRVOperandKind, OperandKindInfo]
  ): Either[InstructionDecodeError, (Operand, Int)] = {
    val value = words(offset).toUnsigned.raw
    info.enumerants.find(_.value == value) match {
      case None            => Left(InstructionDecodeError.InvalidEnumerant(info.kind, value))
      case Some(enumerant) => decodeParameters(opcode, info.kind, words, offset, enumerant.parameterKinds, kinds)
    }
  }

  private def decodeBitEnum(
      opcode: SPIRVOpcode,
      info: OperandKindInfo,
      words: Vec[Word],
      offset: Int,
      kinds: Map[SPIRVOperandKind, OperandKindInfo]
  ): Either[InstructionDecodeError, (Operand, Int)] = {
    val value       = words(offset).toUnsigned.raw
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
      opcode: SPIRVOpcode,
      outerKind: SPIRVOperandKind,
      words: Vec[Word],
      offset: Int,
      parameterKinds: Vec[SPIRVOperandKind],
      kinds: Map[SPIRVOperandKind, OperandKindInfo]
  ): Either[InstructionDecodeError, (Operand, Int)] =
    parameterKinds
      .foldLeft[Either[InstructionDecodeError, Int]](Right(offset + 1)) { (next, parameterKind) =>
        next.flatMap(current => decodeOne(opcode, parameterKind, words, current, kinds).map(_._2))
      }
      .map(next => Operand(outerKind, NonEmptyVec.unsafeFromVec(words.slice(offset, next))) -> next)
}
