package net.kurobako.gemstone.isa.amdgpu.asm

import Program.*
import cats.syntax.all.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.*
import net.kurobako.gemstone.isa.amdgpu.EncodingConstants.*

private object DualIssueCodec {
  private enum Side(val destination: EncodingField, val source: EncodingField, val opcode: EncodingField) {
    case X extends Side(EncodingField.VDSTX, EncodingField.SRCX0, EncodingField.OPX)
    case Y extends Side(EncodingField.VDSTY, EncodingField.SRCY0, EncodingField.OPY)
  }

  private def entries(
      instruction: Opcode,
      target: Target,
      side: Side,
      format: EncodingFormat
  ): Vec[Metadata.Encoding] =
    Metadata.encodingsFor(instruction, target).filter(value =>
      (if (format == EncodingFormat.VOPD3XY) value.format == format else value.format.base == EncodingFormat.VOPDXY) &&
        value.operands.exists(_.field.contains(side.destination))
    )
      .map(value =>
        value.copy(operands = value.operands.filterNot(info => info.field.isEmpty && info.kind == OperandKind.VCC).map {
          case info if info.field.contains(EncodingField.LITERAL) && info.kind == OperandKind.SRC =>
            info.copy(field = Some(side.source))
          case info => info
        })
      ).distinct

  private def source(operand: Operand, target: Target, bits: Int): Option[(Int, Option[OperandCodec.Literal])] =
    operand match {
      case Operand.Register(RegisterFile.VectorRegister, first, last)
          if last - first + 1 == (bits + 31) / 32 && first >= 0 && last < 256 =>
        Some((first + 256, None))
      case _ => OperandCodec.encode(
          operand,
          OperandInfo(Some(EncodingField.SSRC0), OperandKind.SRC, false, bits.unsafe[Int >= 0]),
          target
        )
    }

  def matches(bytes: Vec[Byte], offset: Int, target: Target): Boolean =
    Vec(EncodingFormat.VOPDXY, EncodingFormat.VOPD3XY).flatMap(InstructionLayout.Catalogue.forFormat(target, _)).exists(
      layout =>
        bytes.size - offset >= 4 &&
          (BigInt(LittleEndian.decode(bytes, offset, 4)) & layout.fixedMask) == layout.prefixes.head
    )

  def encode(left: Instruction, right: Instruction, target: Target): Either[CodecError, Vec[Byte]] = {
    val result = Vec(EncodingFormat.VOPDXY, EncodingFormat.VOPD3XY).view.flatMap { format =>
      for {
        layout <- InstructionLayout.Catalogue.forFormat(target, format)
        x      <- half(left, target, Side.X, format)
        y      <- half(right, target, Side.Y, format)
        xd     <- x._1.get(EncodingField.VDSTX)
        yd     <- y._1.get(EncodingField.VDSTY)
        if format == EncodingFormat.VOPD3XY || (xd.toInt & 1) != (yd.toInt & 1)
        literals = Vec(x._2, y._2).flatten.distinct
        if literals.size <= 1
        parts <- (x._1 ++ y._1).updated(
          EncodingField.VDSTY,
          if (format == EncodingFormat.VOPD3XY) yd else yd & ~BigInt(1)
        ).toVector.traverse {
          case (name, number) =>
            layout.byName.get(name).flatMap(_.put(number))
        }
        word = parts.foldLeft(layout.prefixes.head)(_ | _)
      } yield Vec.from((0 until layout.bits / 8).map(index => ((word >> (index * 8)) & 255).toByte)) ++
        literals.flatMap(_.bytes)
    }.headOption
    result.toRight(CodecError.InvalidOperand("invalid dual-issue operands, destination parity, or shared literal"))
  }

  private def half(
      instruction: Instruction,
      target: Target,
      side: Side,
      format: EncodingFormat
  ): Option[(Map[EncodingField, BigInt], Option[OperandCodec.Literal])] =
    if (
      instruction.mods.exists(mod => mod.name != "e96" || mod.value.nonEmpty) ||
      (instruction.mods.nonEmpty && format != EncodingFormat.VOPD3XY)
    ) None
    else entries(instruction.definition, target, side, format).view.flatMap { entry =>
      val schema = entry.operands.filterNot(_.implicitOperand)
      Option.when(schema.size == instruction.operands.size)(schema.zip(instruction.operands)).flatMap(_.traverse {
        case (info, operand) =>
          info.field.flatMap { field =>
            if (field == EncodingField.LITERAL) operand match {
              case Operand.IntegerLiteral(value, _) if value >= Int.MinValue && value <= BigInt("ffffffff", 16) =>
                Some((Map.empty[EncodingField, BigInt], Some(OperandCodec.Literal(value.toLong & 0xffffffffL))))
              case _ => None
            }
            // LLVM's VOPD3 source classes allow inline constants, but no appended literal.
            else source(operand, target, info.bits.raw).filter { case (_, literal) =>
              format != EncodingFormat.VOPD3XY || literal.isEmpty
            }.flatMap { case (value, literal) =>
              if (field.isVectorDestination || field.isVectorSource)
                Option.when(value >= 256)((Map(field -> BigInt(value - 256)), literal))
              else Some((Map(field -> BigInt(value)), literal))
            }
          }
      }).flatMap { values =>
        val literals = values.flatMap(_._2).distinct
        Option.when(literals.size <= 1)((
          values.foldLeft(Map(side.opcode -> BigInt(entry.opcode)))((all, item) => all ++ item._1),
          literals.headOption
        ))
      }
    }.headOption

  def decode(bytes: Vec[Byte], offset: Int, target: Target): Either[CodecError, DecodedStatement] = {
    val result = for {
      layout <- Vec(
        EncodingFormat.VOPDXY,
        EncodingFormat.VOPD3XY
      ).flatMap(InstructionLayout.Catalogue.forFormat(target, _)).find(layout =>
        bytes.size - offset >= 4 && (BigInt(LittleEndian.decode(bytes, offset, 4)) & layout.fixedMask) ==
          layout.prefixes.head
      )
      format = layout.format
      if bytes.size - offset >= layout.bits / 8
      word = (0 until layout.bits / 8).foldLeft(BigInt(0))((word, index) =>
        word | (BigInt(bytes(offset + index) & 255) << (index * 8))
      )
      if (word & layout.fixedMask) == layout.prefixes.head
      pairs = Vec(Side.X, Side.Y).map { side =>
        Opcode.definitions.flatMap(definition =>
          entries(
            definition,
            target,
            side,
            format
          ).filter(_.opcode == layout.get(word, side.opcode).toInt).map(definition -> _)
        )
      }
      x <- pairs(0).headOption
      y <- pairs(1).headOption
      hasLiteral = Vec(x, y).exists(_._2.operands.exists(_.field.contains(EncodingField.LITERAL))) ||
        Vec(EncodingField.SRCX0, EncodingField.SRCY0).exists(layout.get(word, _) == Source.LITERAL_CONST)
      size = layout.bits / 8 + (if (hasLiteral) 4 else 0)
      if bytes.size - offset >= size
      literal = Option.when(hasLiteral)(OperandCodec.Literal(LittleEndian.decode(bytes, offset + layout.bits / 8, 4)))
      halves <- Vec(x, y).traverse { case (definition, entry) =>
        entry.operands.filterNot(_.implicitOperand).traverse { info =>
          info.field.flatMap { field =>
            val raw = layout.get(word, field).toInt
            if (field == EncodingField.LITERAL) literal.map(value => Operand.IntegerLiteral(BigInt(value.value), 16))
            else if (field.isVectorDestination || field.isVectorSource) {
              val value =
                if (field == EncodingField.VDSTY && format != EncodingFormat.VOPD3XY) raw |
                  ((layout.get(word, EncodingField.VDSTX).toInt & 1) ^ 1)
                else raw
              Some(Operand.Register(RegisterFile.VectorRegister, value, value + (info.bits.raw + 31) / 32 - 1))
            } else if (raw >= 256)
              Some(Operand.Register(RegisterFile.VectorRegister, raw - 256, raw - 256 + (info.bits.raw + 31) / 32 - 1))
            else OperandCodec.decode(raw, info.copy(field = Some(EncodingField.SSRC0)), literal, target)
          }
        }.flatMap(args =>
          Instruction.decode(
            definition,
            args,
            if (format == EncodingFormat.VOPD3XY) Vec(Modifier("e96")) else Vec.empty[Modifier]
          ).headOption
        )
      }
      encoded <- encode(halves(0), halves(1), target).toOption
      if encoded == bytes.slice(offset, offset + size)
    } yield DecodedStatement(Statement.InstructionPair(halves(0), halves(1)), size.unsafe[Int > 0])
    result.toRight(CodecError.InvalidEncoding(s"invalid or truncated dual-issue instruction at byte $offset"))
  }
}
