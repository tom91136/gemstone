package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.Col

import cats.data.Chain

private[v1_6] final case class DecodedOperands(fields: Col[Col[Operand]]) {
  def required(index: Int): Operand         = fields(index).head
  def optional(index: Int): Option[Operand] = fields(index).headOption
  def repeated(index: Int): Col[Operand]    = fields(index)
}

private[v1_6] object OperandDecoder {
  def decode(
      opcode: String,
      words: Col[Word],
      shapes: Col[OperandShape],
      kinds: Map[String, OperandKindInfo]
  ): Either[InstructionDecodeError, DecodedOperands] =
    decodeShapes(opcode, words, shapes, kinds, 0, 0, Chain.empty).map(fields =>
      DecodedOperands(Col.from(fields.iterator))
    )

  private def decodeShapes(
      opcode: String,
      words: Col[Word],
      shapes: Col[OperandShape],
      kinds: Map[String, OperandKindInfo],
      index: Int,
      offset: Int,
      fields: Chain[Col[Operand]]
  ): Either[InstructionDecodeError, Chain[Col[Operand]]] =
    if (index >= shapes.size) {
      if (offset == words.size) Right(fields)
      else Left(InstructionDecodeError.InvalidOperandCount(opcode, s"${words.size - offset} trailing words"))
    } else {
      val shape = shapes(index)
      shape.quantifier match {
        case OperandQuantifier.One =>
          decodeOne(opcode, shape.kind, words, offset, kinds).flatMap { case (operand, next) =>
            decodeShapes(opcode, words, shapes, kinds, index + 1, next, fields.append(Col(operand)))
          }
        case OperandQuantifier.Optional =>
          if (offset == words.size)
            decodeShapes(opcode, words, shapes, kinds, index + 1, offset, fields.append(Col.empty))
          else
            decodeOne(opcode, shape.kind, words, offset, kinds).flatMap { case (operand, next) =>
              decodeShapes(opcode, words, shapes, kinds, index + 1, next, fields.append(Col(operand)))
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
              fields.append(Col.from(operands.iterator))
            )
          }
      }
    }

  private def decodeRepeated(
      opcode: String,
      kind: String,
      words: Col[Word],
      offset: Int,
      kinds: Map[String, OperandKindInfo],
      operands: Chain[Operand]
  ): Either[InstructionDecodeError, (Chain[Operand], Int)] =
    if (offset >= words.size) Right(operands -> offset)
    else
      decodeOne(opcode, kind, words, offset, kinds).flatMap { case (operand, next) =>
        decodeRepeated(opcode, kind, words, next, kinds, operands.append(operand))
      }

  def minimumWords(shapes: Col[OperandShape], kinds: Map[String, OperandKindInfo]): Col[Word] =
    shapes.flatMap { shape =>
      shape.quantifier match {
        case OperandQuantifier.One      => minimumOperand(shape.kind, kinds)
        case OperandQuantifier.Optional => Col.empty[Word]
        case OperandQuantifier.Repeated => Col.empty[Word]
      }
    }

  private def minimumOperand(kind: String, kinds: Map[String, OperandKindInfo]): Col[Word] =
    if (kind == "LiteralString") Col(Word(0))
    else if (kind.startsWith("Pair")) Col(Word(0), Word(0))
    else kinds.get(kind) match {
      case Some(info) if info.enumerants.nonEmpty =>
        val selected = info.enumerants.minBy(enumerant => (enumerant.parameterKinds.size, enumerant.value))
        Col(Word(selected.value.toInt)) ++ selected.parameterKinds.flatMap(parameter =>
          minimumOperand(parameter, kinds)
        )
      case _ => Col(Word(0))
    }

  private def decodeOne(
      opcode: String,
      kind: String,
      words: Col[Word],
      offset: Int,
      kinds: Map[String, OperandKindInfo]
  ): Either[InstructionDecodeError, (Operand, Int)] =
    if (offset >= words.size)
      Left(InstructionDecodeError.InvalidOperandCount(opcode, s"missing $kind at word $offset"))
    else if (kind == "LiteralString") decodeString(opcode, kind, words, offset)
    else if (kind == "LiteralContextDependentNumber") Right(Operand(kind, words.drop(offset)) -> words.size)
    else if (kind.startsWith("Pair")) {
      if (offset + 2 > words.size)
        Left(InstructionDecodeError.InvalidOperandCount(opcode, s"short $kind at word $offset"))
      else Right(Operand(kind, words.slice(offset, offset + 2)) -> (offset + 2))
    } else kinds.get(kind) match {
      case Some(info) if info.category == "ValueEnum" => decodeValueEnum(opcode, info, words, offset, kinds)
      case Some(info) if info.category == "BitEnum"   => decodeBitEnum(opcode, info, words, offset, kinds)
      case _                                          => Right(Operand(kind, Col(words(offset))) -> (offset + 1))
    }

  private def decodeString(
      opcode: String,
      kind: String,
      words: Col[Word],
      offset: Int
  ): Either[InstructionDecodeError, (Operand, Int)] =
    words.indexWhere(
      word => (0 until 4).exists(byte => ((word.bits >>> (byte * 8)) & 0xff) == 0),
      offset
    ) match {
      case -1  => Left(InstructionDecodeError.UnterminatedString(opcode))
      case end => Right(Operand(kind, words.slice(offset, end + 1)) -> (end + 1))
    }

  private def decodeValueEnum(
      opcode: String,
      info: OperandKindInfo,
      words: Col[Word],
      offset: Int,
      kinds: Map[String, OperandKindInfo]
  ): Either[InstructionDecodeError, (Operand, Int)] = {
    val value = words(offset).toUnsigned
    info.enumerants.find(_.value == value) match {
      case None            => Left(InstructionDecodeError.InvalidEnumerant(info.kind, value))
      case Some(enumerant) => decodeParameters(opcode, info.kind, words, offset, enumerant.parameterKinds, kinds)
    }
  }

  private def decodeBitEnum(
      opcode: String,
      info: OperandKindInfo,
      words: Col[Word],
      offset: Int,
      kinds: Map[String, OperandKindInfo]
  ): Either[InstructionDecodeError, (Operand, Int)] = {
    val value       = words(offset).toUnsigned
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
      opcode: String,
      outerKind: String,
      words: Col[Word],
      offset: Int,
      parameterKinds: Col[String],
      kinds: Map[String, OperandKindInfo]
  ): Either[InstructionDecodeError, (Operand, Int)] =
    parameterKinds
      .foldLeft[Either[InstructionDecodeError, Int]](Right(offset + 1)) { (next, parameterKind) =>
        next.flatMap(current => decodeOne(opcode, parameterKind, words, current, kinds).map(_._2))
      }
      .map(next => Operand(outerKind, words.slice(offset, next)) -> next)
}
