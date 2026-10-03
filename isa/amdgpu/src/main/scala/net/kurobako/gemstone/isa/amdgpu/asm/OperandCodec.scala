package net.kurobako.gemstone.isa.amdgpu.asm

import Program.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.isa.amdgpu.*
import net.kurobako.gemstone.isa.amdgpu.EncodingConstants.*

private object OperandCodec {
  case class Literal(value: Long, size: Int in (4, 8) = 4) {
    def bytes: Vec[Byte] = LittleEndian.toVec(value, size)
  }
  private val ForcedWideLiteral = "lit64\\((0[xX][0-9a-fA-F]+|[+-]?[0-9]+)\\)".r
  private val ForcedLiteral     = "lit\\((0[xX][0-9a-fA-F]+|[+-]?[0-9]+)\\)".r

  private def source(target: Target): OperandEncoding =
    OperandEncoding.Catalogue.forKind(target.isa, OperandKind.SRC)
      .getOrElse(throw IllegalStateException(s"missing generated source selectors for ${target.isa}"))

  def literalSize(code: Int, target: Target): Option[Int in (4, 8)] = SourceOperand.literalSize(code, target.isa)

  private def special(name: String, target: Target): Option[(Int, Int)] = {
    val selectors = source(target).selectors
    selectors.get(name).filter(_ => !name.startsWith("src_literal")).map { code =>
      val width = if (name == "null" || (name.startsWith("src_") && name != "src_lds_direct")) 0
      else if (selectors.contains(s"${name}_lo")) 64 else 32
      (code, width)
    }
  }

  def encode(operand: Operand, info: OperandInfo, target: Target): Option[(Int, Option[Literal])] = {
    val isSource = !info.field.contains(EncodingField.SDST)
    val encoding = source(target)
    if (info.kind.isMessage || info.kind == OperandKind.SIMM4)
      SymbolicOperandEncoder.encode(operand, info.kind, target).filter(value =>
        value >= 0 && value < (BigInt(1) << (if (info.kind == OperandKind.SIMM4) 4 else 8))
      ).map(value => (value.toInt, None))
    else operand match {
      case Operand.Register(RegisterFile.Scalar, first, last)
          if first >= 0 && last < 128 && last - first + 1 == math.max(info.bits.raw, 32) / 32 &&
            (info.bits.raw <= 32 || first % 2 == 0) =>
        Some((first, None))
      case Operand.Register(RegisterFile.TrapTemporary, first, last)
          if first >= 0 && last - first + 1 == math.max(info.bits.raw, 32) / 32 =>
        for {
          start <- encoding.selectors.get(s"ttmp$first")
          end   <- encoding.selectors.get(s"ttmp$last")
          if end - start == last - first && (info.bits.raw <= 32 || first % 2 == 0)
        } yield (start, None)
      case Operand.Identifier(name) => special(name, target).filter(value =>
          value._2 == 0 || value._2 == math.max(info.bits.raw, 32)
        ).map { case (index, _) => (index, None) }
      case Operand.SpecialRegister(name) => special(name, target).filter { case (_, bits) =>
          bits == 0 || bits == math.max(info.bits.raw, 32)
        }
          .map { case (index, _) => (index, None) }
      case Operand.IntegerLiteral(value, _) if isSource =>
        val modulus  = BigInt(1) << info.bits.raw
        val signed   = if (value >= modulus / 2 && value < modulus) value - modulus else value
        val floating = if (info.bits.raw == 32) encoding.floating.toVector.find { case (_, number) =>
          BigInt(java.lang.Float.floatToRawIntBits(number.toFloat).toLong & 0xffffffffL) == value
        }.map(_._1)
        else None
        encoding.selectors.get(signed.toString).orElse(floating).map(code => (code, None)).orElse {
          if (value >= Int.MinValue && value <= BigInt("ffffffff", 16))
            Some((Source.LITERAL_CONST, Some(Literal(value.toLong & 0xffffffffL))))
          else if (
            encoding.selectors.contains("src_literal64") && info.bits.raw >= 64 &&
            value >= Long.MinValue && value < (BigInt(1) << 64)
          )
            Some((Source.LITERAL64_CONST, Some(Literal(value.toLong, 8))))
          else None
        }
      case Operand.DecimalLiteral(value) if isSource =>
        value.toDoubleOption.flatMap { number =>
          encoding.floating.toVector.find(_._2 == number).map(value => (value._1, None)).orElse {
            val index = Fixed.InlineFloatingValues.indexOf(number)
            Option.when(index >= 0)((Source.LITERAL_CONST, Some(Literal(Fixed.InlineFloatingConstants(index)))))
          }
        }
      case Operand.Expression(ForcedWideLiteral(value))
          if isSource && encoding.selectors.contains("src_literal64") && info.bits.raw >= 64 =>
        val number = if (value.startsWith("0x") || value.startsWith("0X")) BigInt(value.drop(2), 16) else BigInt(value)
        Option.when(number >= Long.MinValue && number < (BigInt(1) << 64))((
          Source.LITERAL64_CONST,
          Some(Literal(number.toLong, 8))
        ))
      case Operand.Expression(ForcedLiteral(value)) if isSource =>
        val number = if (value.startsWith("0x") || value.startsWith("0X")) BigInt(value.drop(2), 16) else BigInt(value)
        Option.when(number >= Int.MinValue && number <= BigInt("ffffffff", 16))((
          Source.LITERAL_CONST,
          Some(Literal(number.toLong & 0xffffffffL))
        ))
      case _ => None
    }
  }

  def decode(value: Int, info: OperandInfo, literal: Option[Literal], target: Target): Option[Operand] = {
    val isSource = !info.field.contains(EncodingField.SDST)
    if (info.kind.isMessage || info.kind == OperandKind.SIMM4) Some(Operand.IntegerLiteral(BigInt(value), 10))
    else if (value < 128) {
      val aliases = if (math.max(info.bits.raw, 32) == 64) Vec("vcc", "exec", "null")
      else Vec("vcc_lo", "vcc_hi", "exec_lo", "exec_hi", "m0", "null")
      aliases.find(name => special(name, target).exists(_._1 == value)).map(Operand.SpecialRegister(_))
        .orElse(Option.when(math.max(info.bits.raw, 32) == 32 || (value % 2 == 0 && value < 127))(
          Operand.Register(RegisterFile.Scalar, value, value + math.max(info.bits.raw, 32) / 32 - 1)
        ))
    } else if (!isSource) None
    else SourceOperand.decode(value, target.isa).flatMap {
      case SourceOperand.Integer(number)   => Some(Operand.IntegerLiteral(BigInt(number), 10))
      case SourceOperand.Floating(number)  => Some(Operand.DecimalLiteral(number.toString))
      case SourceOperand.Special(register) => Some(Operand.SpecialRegister(register.toString))
      case SourceOperand.Literal(size)     => literal.filter(_.size == size).map(bits =>
          Operand.Expression(s"${if (size.raw == 8) "lit64" else "lit"}(0x${bits.value.toHexString})")
        )
      case _ => None
    }
  }
}
