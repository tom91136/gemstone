package net.kurobako.gemstone.isa.amdgpu.asm

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.isa.amdgpu.*
import net.kurobako.gemstone.isa.amdgpu.EncodingConstants.*
import net.kurobako.gemstone.isa.amdgpu.domain.*
import net.kurobako.gemstone.string.*

private object OperandCodec {
  case class Literal(value: Long, size: Int in (4, 8) = 4) {
    def bytes: Vec[Byte] = LittleEndian.toVec(value, size)
  }
  private val ForcedWideLiteral = "lit64\\((0[xX][0-9a-fA-F]+|[+-]?[0-9]+)\\)".r
  private val ForcedLiteral     = "lit\\((0[xX][0-9a-fA-F]+|[+-]?[0-9]+)\\)".r

  private def source(target: Target): DomainTable =
    DomainCatalogue.table(target.isa, "src")
      .getOrElse(throw IllegalStateException(s"missing generated source domain for ${target.isa}"))

  /** Whether `bits` wide registers may start at `first` in `file`. */
  def aligned(target: Target, file: RegisterFile, first: Int, bits: Int): Boolean =
    first % source(target).alignment(file, bits) == 0

  /** The code the source domain gives the null register, where the family has one. */
  def undefined(field: EncodingField, code: Long, target: Target): Option[NonEmpty[String]] =
    source(target).undefined(target.isa, field, code).map(_.unsafe[NonEmpty[String]])

  private def scalar(file: RegisterFile): Boolean = file == RegisterFile.Scalar || file == RegisterFile.TrapTemporary

  private def word(value: BigInt): Long = value.toLong & 0xffffffffL

  private def constant(table: DomainTable, value: BigInt, bits: Int): Option[Int] = {
    val modulus  = BigInt(1) << bits
    val signed   = if (value >= modulus / 2 && value < modulus) value - modulus else value
    val integer  = Option.when(signed.isValidInt)(signed.toInt).flatMap(number => table.encode(Slot.Integer(number)))
    val floating = Option.when(bits == 32)(table.runs.collectFirst {
      case Run.Floats(number, code)
          if (java.lang.Float.floatToRawIntBits(number.toFloat).toLong & 0xffffffffL) == value =>
        BigInt(code)
    }).flatten
    integer.orElse(floating).map(_.toInt)
  }

  def encode(operand: Operand, info: OperandInfo, target: Target): Option[(Int, Option[Literal])] =
    if (info.kind.isMessage || info.kind == OperandKind.SIMM4)
      SymbolicOperandEncoder.encode(operand, info.kind, target).filter(value =>
        value >= 0 && value < (BigInt(1) << (if (info.kind == OperandKind.SIMM4) 4 else 8))
      ).map(value => (value.toInt, None))
    else encode(source(target), operand, info.bits.raw, !info.field.contains(EncodingField.SDST))

  private def encode(table: DomainTable, operand: Operand, bits: Int, isSource: Boolean)
      : Option[(Int, Option[Literal])] = {
    val width = math.max(bits, 32)
    operand match {
      case Operand.Register(file, first, last) if scalar(file) =>
        table.registers(file, first, bits).filter(_ => last - first + 1 == width / 32)
          .map((_, None))
      case Operand.Identifier(name)                     => table.code(name, bits).map((_, None))
      case Operand.SpecialRegister(name)                => table.code(name, bits).map((_, None))
      case Operand.IntegerLiteral(value, _) if isSource =>
        constant(table, value, bits).map((_, None)).orElse {
          if (table.hasLiteral(4) && value >= Int.MinValue && value <= BigInt("ffffffff", 16))
            table.encode(Slot.Literal(4)).map(code => (code.toInt, Some(Literal(word(value)))))
          else if (table.hasLiteral(8) && bits >= 64 && value >= Long.MinValue && value < (BigInt(1) << 64))
            table.encode(Slot.Literal(8)).map(code => (code.toInt, Some(Literal(value.toLong, 8))))
          else None
        }
      case Operand.DecimalLiteral(value) if isSource =>
        value.toDoubleOption.flatMap { number =>
          table.encode(Slot.Floating(number)).map(code => (code.toInt, None)).orElse {
            val index = Fixed.InlineFloatingValues.indexOf(number)
            Option.when(index >= 0 && table.hasLiteral(4))(
              table.encode(Slot.Literal(4)).map(code =>
                (code.toInt, Some(Literal(Fixed.InlineFloatingConstants(index))))
              )
            ).flatten
          }
        }
      case Operand.Expression(ForcedWideLiteral(value)) if isSource && table.hasLiteral(8) && bits >= 64 =>
        parseBigIntLiteral(value).filter(number => number >= Long.MinValue && number < (BigInt(1) << 64))
          .flatMap(number => table.encode(Slot.Literal(8)).map(code => (code.toInt, Some(Literal(number.toLong, 8)))))
      case Operand.Expression(ForcedLiteral(value)) if isSource && table.hasLiteral(4) =>
        parseBigIntLiteral(value).filter(number => number >= Int.MinValue && number <= BigInt("ffffffff", 16))
          .flatMap(number => table.encode(Slot.Literal(4)).map(code => (code.toInt, Some(Literal(word(number))))))
      case _ => None
    }
  }

  def decode(value: Int, info: OperandInfo, literal: Option[Literal], target: Target): Option[Operand] =
    if (info.kind.isMessage || info.kind == OperandKind.SIMM4) Some(Operand.IntegerLiteral(BigInt(value), 10))
    else decode(source(target), value, info.bits.raw, !info.field.contains(EncodingField.SDST), literal)

  private def decode(table: DomainTable, code: Int, bits: Int, isSource: Boolean, literal: Option[Literal])
      : Option[Operand] = {
    val count = math.max(bits, 32) / 32
    if (code < 128)
      table.name(code, bits).map(Operand.SpecialRegister(_)).orElse(
        table.registersAt(code, bits).filter((file, _) => scalar(file))
          .map((file, first) => Operand.Register(file, first, first + count - 1))
      )
    else if (!isSource) None
    else table.decode(code).flatMap {
      case Slot.Integer(number)  => Some(Operand.IntegerLiteral(BigInt(number), 10))
      case Slot.Floating(number) => Some(Operand.DecimalLiteral(
          (if (code == Source.INLINE_FLOATING_C_MAX) SourceOperand.InverseTwoPi else number).toString
        ))
      case Slot.Named(name)   => Option.when(name.startsWith("src_"))(Operand.SpecialRegister(name))
      case Slot.Literal(size) => literal.filter(_.size.raw == size).map { bits =>
          Operand.Expression(s"${if (size == 8) "lit64" else "lit"}(0x${bits.value.toHexString})")
        }
      case _ => None
    }
  }
}
