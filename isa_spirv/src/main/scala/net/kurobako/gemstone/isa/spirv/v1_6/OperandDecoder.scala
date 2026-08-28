package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.Col

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
  ): Either[InstructionDecodeError, DecodedOperands] = {
    val fields = Col.newBuilder[Col[Operand]]
    var offset = 0
    var index  = 0
    while (index < shapes.size) {
      val shape = shapes(index)
      shape.quantifier match {
        case OperandQuantifier.One =>
          decodeOne(opcode, shape.kind, words, offset, kinds) match {
            case Left(error)            => return Left(error)
            case Right((operand, next)) =>
              fields += Col(operand)
              offset = next
          }
        case OperandQuantifier.Optional =>
          if (offset == words.size) fields += Col.empty
          else decodeOne(opcode, shape.kind, words, offset, kinds) match {
            case Left(error)            => return Left(error)
            case Right((operand, next)) =>
              fields += Col(operand)
              offset = next
          }
        case OperandQuantifier.Repeated =>
          val operands = Col.newBuilder[Operand]
          while (offset < words.size)
            decodeOne(opcode, shape.kind, words, offset, kinds) match {
              case Left(error)            => return Left(error)
              case Right((operand, next)) =>
                operands += operand
                offset = next
            }
          fields += operands.result()
      }
      index += 1
    }
    if (offset != words.size)
      Left(InstructionDecodeError.InvalidOperandCount(opcode, s"${words.size - offset} trailing words"))
    else Right(DecodedOperands(fields.result()))
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
  ): Either[InstructionDecodeError, (Operand, Int)] = {
    if (offset >= words.size)
      return Left(InstructionDecodeError.InvalidOperandCount(opcode, s"missing $kind at word $offset"))
    if (kind == "LiteralString") decodeString(opcode, kind, words, offset)
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
  }

  private def decodeString(
      opcode: String,
      kind: String,
      words: Col[Word],
      offset: Int
  ): Either[InstructionDecodeError, (Operand, Int)] = {
    var end   = offset
    var found = false
    while (end < words.size && !found) {
      val bits = words(end).bits
      found = (0 until 4).exists(byte => ((bits >>> (byte * 8)) & 0xff) == 0)
      end += 1
    }
    if (!found) Left(InstructionDecodeError.UnterminatedString(opcode))
    else Right(Operand(kind, words.slice(offset, end)) -> end)
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
  ): Either[InstructionDecodeError, (Operand, Int)] = {
    var next  = offset + 1
    var index = 0
    while (index < parameterKinds.size) {
      decodeOne(opcode, parameterKinds(index), words, next, kinds) match {
        case Left(error)            => return Left(error)
        case Right((_, nextOffset)) => next = nextOffset
      }
      index += 1
    }
    Right(Operand(outerKind, words.slice(offset, next)) -> next)
  }
}
