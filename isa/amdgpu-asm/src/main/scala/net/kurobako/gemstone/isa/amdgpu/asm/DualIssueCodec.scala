package net.kurobako.gemstone.isa.amdgpu.asm

import Program.*
import cats.syntax.all.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.*
import net.kurobako.gemstone.string.*

private object DualIssueCodec {
  private enum Side(
      val destination: EncodingField,
      val source: EncodingField,
      val opcode: EncodingField,
      val sources: Vec[EncodingField],
      val negate: EncodingField
  ) {
    case X
        extends Side(
          EncodingField.VDSTX,
          EncodingField.SRCX0,
          EncodingField.OPX,
          Vec(EncodingField.SRCX0, EncodingField.VSRCX1, EncodingField.VSRCX2),
          EncodingField.NEGX
        )
    case Y
        extends Side(
          EncodingField.VDSTY,
          EncodingField.SRCY0,
          EncodingField.OPY,
          Vec(EncodingField.SRCY0, EncodingField.VSRCY1, EncodingField.VSRCY2),
          EncodingField.NEGY
        )
  }

  // LLVM negates the sources of VOPD3's float operations and selects, and takes bitop2's truth table as bitop3
  private def negatable(instruction: Opcode): Boolean =
    instruction.mnemonic.raw.endsWith("_f32") || instruction.mnemonic.raw.endsWith("_f64") ||
      instruction.mnemonic.raw == "v_dual_cndmask_b32"

  private val TruthTable: NonEmpty[String] = "bitop3"

  private def plain(operand: Operand): Operand = operand match {
    case Operand.Negated(value) => value
    case other                  => other
  }

  private def entries(
      instruction: Opcode,
      target: Target,
      side: Side,
      format: EncodingFormat
  ): Vec[Metadata.Encoding] =
    instruction.encodings(target).filter(value =>
      (if (format == EncodingFormat.VOPD3XY) value.format == format else value.format.base == EncodingFormat.VOPDXY) &&
        value.operands.exists(_.field.contains(side.destination))
    ).map(operands(_, side)).distinct

  private def operands(value: Metadata.Encoding, side: Side): Metadata.Encoding =
    value.copy(operands = value.operands.filterNot(info => info.field.isEmpty && info.kind == OperandKind.VCC).map {
      case info if info.field.contains(EncodingField.LITERAL) && info.kind == OperandKind.SRC =>
        info.copy(field = Some(side.source))
      case info => info
    })

  private def source(operand: Operand, target: Target, bits: Int): Option[(Int, Option[OperandCodec.Literal])] =
    operand match {
      case Operand.Register(RegisterFile.VectorRegister, first, last)
          if last - first + 1 == (bits + 31) / 32 && first >= 0 && last < 256 =>
        Some((first + 256, None))
      case _ => OperandCodec.encode(
          operand,
          OperandInfo(Some(EncodingField.SSRC0), OperandKind.SRC, implicitOperand = false, bits.unsafe[Int >= 0]),
          target
        )
    }

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
        word <- layout.assemble((x._1 ++ y._1).updated(
          EncodingField.VDSTY,
          if (format == EncodingFormat.VOPD3XY) yd else yd & ~BigInt(1)
        ))
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
      instruction.mods.exists(mod =>
        (mod.name != "e96" || mod.value.nonEmpty) && (mod.name != TruthTable || mod.value.isEmpty)
      ) || (instruction.mods.nonEmpty && format != EncodingFormat.VOPD3XY)
    ) None
    else entries(instruction.definition, target, side, format).view.flatMap { entry =>
      val schema  = entry.operands.filterNot(_.implicitOperand)
      val negated = schema.zip(instruction.operands).collect { case (info, Operand.Negated(_)) =>
        info.field.map(side.sources.indexOf(_)).filter(_ >= 0)
      }
      val table = instruction.mods.find(_.name == TruthTable).map(_.value.flatMap(value =>
        parseBigIntLiteral(value).filter(bits => bits >= 0 && bits < 256)
      ))
      val signs = Option.when(
        negated.forall(_.nonEmpty) &&
          (negated.isEmpty ||
            (format == EncodingFormat.VOPD3XY &&
              negatable(instruction.definition))) && table.forall(_.nonEmpty) &&
          (table.isEmpty || instruction.opcode.raw == "v_dual_bitop2_b32")
      )(
        Map(side.negate -> negated.flatten.foldLeft(BigInt(0))((bits, index) => bits.setBit(index))).filter(_ =>
          format == EncodingFormat.VOPD3XY
        ) ++ table.flatten.map(side.sources(2) -> _)
      )
      Option.when(schema.size == instruction.operands.size && signs.nonEmpty)(
        schema.zip(instruction.operands.map(plain(_)))
      ).flatMap(_.traverse {
        case (info, operand) =>
          info.field.flatMap { field =>
            if (field == EncodingField.LITERAL) operand match {
              case Operand.IntegerLiteral(value, _) if value >= Int.MinValue && value <= BigInt("ffffffff", 16) =>
                Some((Map.empty[EncodingField, BigInt], Some(OperandCodec.Literal(value.toLong & 0xffffffffL))))
              case _ => None
            }
            // a VOPD3 select's lane mask is one SGPR, as dual issue runs in wave32
            else if (info.kind.isScalarRegister)
              source(operand, target, 32).collect {
                case (value, None) if value < 256 => (Map(field -> BigInt(value)), None)
              }
            // LLVM's VOPD3 source classes allow inline constants, but no appended literal.
            else source(operand, target, info.bits).filter { case (_, literal) =>
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
          values.foldLeft(Map(side.opcode -> BigInt(entry.opcode)) ++ signs.getOrElse(Map.empty))((all, item) =>
            all ++ item._1
          ),
          literals.headOption
        ))
      }
    }.headOption

  def decode(bytes: Array[Byte], offset: Int, target: Target): Option[Either[CodecError, DecodedStatement]] =
    InstructionDecoder.dual(bytes, offset, target).map(_.left.map(CodecError.InvalidEncoding(_)).flatMap { dual =>
      val format  = dual.layout.format
      val literal = dual.literal.map(OperandCodec.Literal(_))
      def half(side: Side, candidates: Vec[(Opcode, Metadata.Encoding)]): Option[Instruction] =
        candidates.headOption.flatMap { case (definition, encoding) =>
          operands(encoding, side).operands.filterNot(_.implicitOperand).traverse { info =>
            info.field.flatMap { field =>
              val raw = dual(field).toInt
              if (field == EncodingField.LITERAL) literal.map(value => Operand.IntegerLiteral(BigInt(value.value), 16))
              else if (info.kind.isScalarRegister)
                OperandCodec.decode(
                  raw,
                  info.copy(field = Some(EncodingField.SSRC0), bits = 32.unsafe[Int >= 0]),
                  None,
                  target
                )
              else if (field.isVectorDestination || field.isVectorSource) {
                val value = if (field == EncodingField.VDSTY) dual.destinationY.toInt else raw
                Some(Operand.Register(RegisterFile.VectorRegister, value, value + (info.bits.raw + 31) / 32 - 1))
              } else if (raw >= 256)
                Some(Operand.Register(
                  RegisterFile.VectorRegister,
                  raw - 256,
                  raw - 256 + (info.bits.raw + 31) / 32 - 1
                ))
              else OperandCodec.decode(raw, info.copy(field = Some(EncodingField.SSRC0)), literal, target)
            }
          }.flatMap { args =>
            val schema = operands(encoding, side).operands.filterNot(_.implicitOperand)
            val signs  = if (format == EncodingFormat.VOPD3XY) dual(side.negate) else 0L
            val signed = schema.zip(args).map { (info, arg) =>
              val index = info.field.fold(-1)(side.sources.indexOf(_))
              if (index >= 0 && ((signs >>> index) & 1L) != 0L) Operand.Negated(arg) else arg
            }
            val table = dual(side.sources(2))
            val mods  =
              if (format != EncodingFormat.VOPD3XY) Vec.empty[Modifier]
              else if (definition.mnemonic.raw == "v_dual_bitop2_b32" && table != 0L)
                Vec(Modifier("e96"), Modifier(TruthTable, Some(s"0x${table.toHexString}")))
              else Vec(Modifier("e96"))
            Instruction.decode(definition, signed, mods).toOption
          }
        }
      (for {
        x       <- half(Side.X, dual.x)
        y       <- half(Side.Y, dual.y)
        encoded <- encode(x, y, target).toOption
        if encoded == Vec.unsafeWrapArray(bytes.slice(offset, offset + dual.size))
      } yield DecodedStatement(Statement.InstructionPair(x, y), dual.size))
        .toRight(CodecError.InvalidEncoding(s"invalid dual-issue instruction at byte $offset"))
    })
}
