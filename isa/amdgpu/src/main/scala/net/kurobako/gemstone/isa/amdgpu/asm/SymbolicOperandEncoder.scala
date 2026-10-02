package net.kurobako.gemstone.isa.amdgpu.asm

import Program.*
import cats.syntax.all.*
import net.kurobako.gemstone.isa.amdgpu.*
import net.kurobako.gemstone.isa.amdgpu.EncodingConstants.*
import net.kurobako.gemstone.isa.amdgpu.OperandEncoding.*
import net.kurobako.gemstone.string.*

private object SymbolicOperandEncoder {
  private def fields(kind: OperandKind, target: Target): Vector[NamedField] =
    OperandEncoding.Catalogue.forKind(target.isa, kind).toVector.flatMap(_.fields)

  private def arguments(text: String, name: String): Option[Vector[String]] =
    Option.when(text.startsWith(s"$name(") && text.endsWith(")"))(
      text.drop(name.length + 1).dropRight(1).split(',').toVector.map(_.trim).filter(_.nonEmpty)
    )

  def encode(operand: Operand, kind: OperandKind, target: Target): Option[BigInt] = operand match {
    case Operand.IntegerLiteral(value, _) => Some(value)
    case Operand.Expression(text)         =>
      val schema = fields(if (kind == OperandKind.SENDMSG_RTN) OperandKind.SENDMSG else kind, target)
      def pack(values: Vector[(EncodingField, String)]): Option[BigInt] = values.traverse { case (name, value) =>
        schema.find(_.field.name == name).flatMap { info =>
          parseBigIntLiteral(
            value
          ).orElse(info.names.get(value).flatMap(parseBigIntLiteral(_))).flatMap(info.field.put(_))
        }.orElse(Option.when(value == "0" && (name == EncodingField.GSOP || name == EncodingField.STREAMID))(BigInt(0)))
      }.map(_.foldLeft(BigInt(0))(_ | _))
      if (kind == OperandKind.WAIT_EVENT && text.startsWith("{") && text.endsWith("}")) {
        text.drop(1).dropRight(1).split(',').toVector.map(_.trim).traverse { value =>
          value.split(':').map(_.trim).toVector match {
            case Vector(name, bit) => EncodingField.fromName(name.toUpperCaseRoot).map(_ -> bit)
            case _                 => None
          }
        }.filter(values => values.map(_._1).distinct.size == values.size).flatMap(pack(_))
      } else if (kind == OperandKind.HWREG) arguments(text, "hwreg").flatMap {
        case Vector(id) => pack(Vector(EncodingField.ID -> id, EncodingField.OFFSET -> "0", EncodingField.SIZE -> "31"))
        case Vector(id, offset, size) => for {
            start <- parseBigIntLiteral(offset)
            width <- parseBigIntLiteral(size)
            if start >= 0 && width > 0 && start + width <= 32
            bits <- pack(Vector(
              EncodingField.ID     -> id,
              EncodingField.OFFSET -> offset,
              EncodingField.SIZE   -> (width - 1).toString
            ))
          } yield bits
        case _ => None
      }
      else if (kind.isMessage) arguments(text, "sendmsg").flatMap { args =>
        Option.when(args.nonEmpty && args.size <= 3)(Vector(
          EncodingField.MSG,
          EncodingField.GSOP,
          EncodingField.STREAMID
        ).zip(args))
          .flatMap(pack(_))
      }
      else arguments(text, "gpr_idx").flatMap { args =>
        val names = Vector("SRC0", "SRC1", "SRC2", "DST")
        Option.when(args.distinct.size == args.size && args.forall(names.contains(_)))(
          args.foldLeft(BigInt(0))((bits, name) => bits.setBit(names.indexOf(name)))
        )
      }
    case _ => None
  }

  def counters(instruction: Instruction, kind: OperandKind, target: Target): Option[BigInt] = {
    val schema = fields(kind, target)
    val parts  = instruction.operands.toVector.collect { case Operand.Expression(text) => text } ++
      instruction.mods.toVector.filter(_.value.isEmpty).map(_.name.raw)
    val Counter = "([a-z_]+)\\(([0-9]+)\\)".r
    val values  = parts.traverse {
      case Counter(name, value) =>
        val field =
          (if (kind == OperandKind.WAITCNT) name.stripSuffix("cnt") else name.stripPrefix("depctr_")).toUpperCaseRoot
        schema.find(info => EncodingField.fromName(field).contains(info.field.name)).flatMap(info =>
          parseBigIntLiteral(value).flatMap(info.field.put(_)).map(info.field -> _)
        )
      case _ => None
    }
    values.filter(_.map(_._1.name).distinct.size == parts.size).filter(_.nonEmpty).map { values =>
      // Unspecified counters disable their waits.
      val initial =
        schema.foldLeft(if (kind == OperandKind.WAITCNT && Set(ISA.GFX6, ISA.GFX7, ISA.GFX8)(target.isa)) BigInt(0x3000)
        else BigInt(0))((bits, info) => bits | info.field.mask)
      values.foldLeft(initial) { case (bits, (field, value)) => (bits & ~field.mask) | value }
    }
  }

  def matrix(name: String, text: String): Option[(EncodingField, BigInt)] = {
    val kind = if (name.endsWith("_SCALE_FMT")) EncodingField.SCALE_FMT
    else if (name.endsWith("_SCALE")) EncodingField.SCALE else EncodingField.FMT
    val schema = OperandEncoding.Catalogue.forKind(ISA.CDNA5, OperandKind.MATRIX).toVector.flatMap(_.fields)
    val a      = name.startsWith("MATRIX_A_")
    val field  = kind match {
      case EncodingField.SCALE_FMT => if (a) EncodingField.SCALE_NEG else EncodingField.SCALE_NEG_HI
      case EncodingField.SCALE     => if (a) EncodingField.SCALE_OP_SEL else EncodingField.SCALE_OP_SEL_HI
      case _                       => if (a) EncodingField.OP_SEL else EncodingField.OP_SEL_HI
    }
    schema.find(_.field.name == kind).flatMap(info =>
      parseBigIntLiteral(
        text
      ).orElse(info.names.get(text.stripPrefix(s"MATRIX_${kind}_")).flatMap(parseBigIntLiteral(_)))
    )
      .map(field -> _)
  }

  def bufferFormat(text: String, isa: ISA): Option[Map[EncodingField, BigInt]] = {
    val schema = OperandEncoding.Catalogue.forKind(isa, OperandKind.BUFFER_FORMAT).toVector.flatMap(_.fields)
    Option.when(text.startsWith("[") && text.endsWith("]"))(text.drop(1).dropRight(1).split(',').toVector.map(_.trim))
      .flatMap(_.traverse { name =>
        val (field, key) = if (name.startsWith("BUF_DATA_FORMAT_"))
          (EncodingField.DFMT, name.stripPrefix("BUF_DATA_FORMAT_"))
        else if (name.startsWith("BUF_NUM_FORMAT_")) (EncodingField.NFMT, name.stripPrefix("BUF_NUM_FORMAT_"))
        else (EncodingField.FORMAT, name.stripPrefix("BUF_FMT_"))
        schema.find(_.field.name == field).flatMap(_.names.get(key)).flatMap(parseBigIntLiteral(_)).map(field -> _)
      }).filter(values => values.nonEmpty && values.map(_._1).distinct.size == values.size).map(_.toMap)
  }

  def swizzle(text: String, isa: ISA): Option[BigInt] = arguments(text, "swizzle").flatMap {
    case Vector("QUAD_PERM", a, b, c, d) =>
      Vector(a, b, c, d).traverse(parseBigIntLiteral(_)).filter(_.forall(n => n >= 0 && n < 4))
        .map(_.zipWithIndex.foldLeft(BigInt(Swizzle.QUAD_PERM_ENC)) { case (bits, (value, index)) =>
          bits | (value << (index * 2))
        })
    case Vector("BITMASK_PERM", quoted) =>
      val mask = quoted.stripPrefix("\"").stripSuffix("\"")
      Option.when(mask.length == Swizzle.BITMASK_WIDTH && mask.forall("01pi".contains(_))) {
        mask.reverse.zipWithIndex.foldLeft(BigInt(0)) { case (bits, (value, index)) =>
          bits | (if (value == 'p' || value == 'i') BigInt(1) << index else BigInt(0)) |
            (if (value == '1') BigInt(1) << (index + Swizzle.BITMASK_OR_SHIFT) else BigInt(0)) |
            (if (value == 'i') BigInt(1) << (index + Swizzle.BITMASK_XOR_SHIFT) else BigInt(0))
        }
      }
    case Vector("BROADCAST", group, lane) => for {
        size  <- parseBigIntLiteral(group).filter(n => n >= 2 && n <= 32 && (n & (n - 1)) == 0)
        index <- parseBigIntLiteral(lane).filter(n => n >= 0 && n < size)
      } yield (32 - size) | (index << 5)
    case Vector(mode @ ("SWAP" | "REVERSE"), group) =>
      parseBigIntLiteral(group).filter(n =>
        (n & (n - 1)) == 0 && (if (mode == "SWAP") n >= 1 && n <= 16 else n >= 2 && n <= 32)
      )
        .map(n => BigInt(31) | ((if (mode == "SWAP") n else n - 1) << 10))
    case Vector("FFT", value) if !Set(ISA.GFX6, ISA.GFX7, ISA.GFX8)(isa) =>
      parseBigIntLiteral(value).filter(n => n >= 0 && n <= Swizzle.FFT_SWIZZLE_MAX).map(_ | Swizzle.FFT_MODE_ENC)
    case Vector("ROTATE", direction, size) if !Set(ISA.GFX6, ISA.GFX7, ISA.GFX8)(isa) =>
      for {
        dir   <- parseBigIntLiteral(direction).filter(n => n >= 0 && n <= 1)
        count <- parseBigIntLiteral(size).filter(n => n >= 0 && n <= Swizzle.ROTATE_MAX_SIZE)
      } yield BigInt(Swizzle.ROTATE_MODE_ENC) | (dir << Swizzle.ROTATE_DIR_SHIFT) | (count << Swizzle.ROTATE_SIZE_SHIFT)
    case _ => None
  }
}
