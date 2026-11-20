package net.kurobako.gemstone.isa.spirv

import cats.syntax.all.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.spirv.Binary as SPIRVBinary
import scala.annotation.tailrec

object ISA {
  enum DecodeError {
    case Binary(error: SPIRVBinary.BinaryError)
    case Instruction(error: ModuleDecodeError)

    def message: NonEmpty[String] = this match {
      case Binary(error)      => error.message
      case Instruction(error) => error.message
    }
  }

  val version: Version = Instruction.version

  def decode(module: RawModule): Either[ModuleDecodeError, Module] =
    decodeAll(module.instructions).map(Module(module.header, _, module.endianness))

  private[spirv] def decodeAll(raws: Vec[RawInstruction]): Either[ModuleDecodeError, Vec[Instruction]] = {
    @tailrec
    def loop(
        index: Int,
        widths: Map[Word, Int],
        decoded: Vector[Instruction]
    ): Either[ModuleDecodeError, Vector[Instruction]] =
      if (index == raws.size) Right(decoded)
      else
        Instruction.decode(raws(index), widths) match {
          case Left(error)        => Left(ModuleDecodeError.Instruction(index.unsafe[Int >= 0], error))
          case Right(instruction) => loop(index + 1, recordWidths(widths, instruction), decoded :+ instruction)
        }
    loop(0, Map.empty, Vector.empty).map(Vec.from(_))
  }

  private[spirv] def recordWidths(widths: Map[Word, Int], instruction: Instruction): Map[Word, Int] =
    instruction match {
      case Instruction.TypeInt(dest, width, _) => widths.updated(dest.words.head, width.words.head.raw)
      case _                                   =>
        val operands = instruction.operands
        val result   = operands.find(_.kind == OperandKind.IdResult)
        val tpe      =
          operands.find(_.kind == OperandKind.IdResultType).flatMap(operand => widths.get(operand.words.head))
        (result, tpe) match {
          case (Some(id), Some(width)) => widths.updated(id.words.head, width)
          case _                       => widths
        }
    }

  def decode(bytes: Vec[Byte]): Either[DecodeError, Module] = for {
    raw    <- Binary.decode(bytes).leftMap(DecodeError.Binary(_))
    module <- decode(raw).leftMap(DecodeError.Instruction(_))
  } yield module

  def encode(module: Module): Either[Binary.BinaryError, Vec[Byte]] =
    Binary.encode(RawModule(module.header, module.instructions.map(Instruction.encode), module.endianness))
}
