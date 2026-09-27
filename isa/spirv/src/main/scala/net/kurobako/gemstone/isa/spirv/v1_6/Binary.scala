package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.Constraint.IntBetween

import net.kurobako.gemstone.Constraint.Positive
import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.binary.Bits
import net.kurobako.gemstone.binary.LittleEndian
import cats.data.Chain

import scala.annotation.tailrec

object Binary {
  enum BinaryError {
    case MisalignedByteLength(length: Int)
    case TruncatedHeader(wordCount: Int)
    case InvalidMagic(found: Long)
    case ZeroInstructionWordCount(wordOffset: Int)
    case TruncatedInstruction(wordOffset: Int, declaredWords: Int, remainingWords: Int)
    case InstructionTooLong(opcodeNumber: SPIRVOpcodeNumber, wordCount: Positive[Int])

    def message: String = this match {
      case MisalignedByteLength(length)     => s"SPIR-V binary length $length is not a multiple of four"
      case TruncatedHeader(wordCount)       => s"SPIR-V header needs five words, found $wordCount"
      case InvalidMagic(found)              => f"invalid SPIR-V magic 0x$found%08x"
      case ZeroInstructionWordCount(offset) => s"instruction at word $offset has zero word count"
      case TruncatedInstruction(offset, declared, remaining) =>
        s"instruction at word $offset declares $declared words with only $remaining remaining"
      case InstructionTooLong(opcode, count) => s"opcode $opcode has unencodable word count $count"
    }
  }

  val Magic: Int = 0x07230203

  def decode(bytes: Vec[Byte]): Either[BinaryError, RawModule] =
    for {
      _ <- Either.cond(
        bytes.length % 4 == 0,
        (),
        BinaryError.MisalignedByteLength(bytes.length)
      )
      wordCount = bytes.length / 4
      _ <- Either.cond(wordCount >= 5, (), BinaryError.TruncatedHeader(wordCount))
      rawMagic = readInt(bytes, 0, Endianness.Little)
      endianness <-
        if (rawMagic == Magic) Right(Endianness.Little)
        else if (java.lang.Integer.reverseBytes(rawMagic) == Magic) Right(Endianness.Big)
        else Left(BinaryError.InvalidMagic(Bits.unsigned32(rawMagic).raw))
      words  = Vec.tabulate(wordCount)(index => Word(readInt(bytes, index * 4, endianness)))
      header = Header(SPIRVVersion.fromWord(words(1)), words(2), words(3), words(4))
      instructions <- parseInstructions(words, 5, Chain.empty)
    } yield RawModule(header, instructions, endianness)

  @tailrec
  private def parseInstructions(
      words: Vec[Word],
      offset: Int,
      instructions: Chain[RawInstruction]
  ): Either[BinaryError, Vec[RawInstruction]] =
    if (offset >= words.size) Right(Vec.from(instructions.toVector.view))
    else {
      val first       = words(offset).raw
      val declared    = first >>> 16
      val opcodeValue = first & 0xffff
      val remaining   = words.size - offset
      if (declared == 0) Left(BinaryError.ZeroInstructionWordCount(offset))
      else if (declared > remaining)
        Left(BinaryError.TruncatedInstruction(offset, declared, remaining))
      else
        parseInstructions(
          words,
          offset + declared,
          instructions.append(
            RawInstruction(SPIRVOpcodeNumber.unsafe(opcodeValue), words.slice(offset + 1, offset + declared))
          )
        )
    }

  def encode(module: RawModule): Either[BinaryError, Vec[Byte]] =
    module.instructions.find(_.wordCount.raw > 0xffff) match {
      case Some(instruction) =>
        Left(BinaryError.InstructionTooLong(instruction.opcodeNumber, instruction.wordCount))
      case None =>
        val header = Vec(
          Word(Magic),
          module.header.version.word,
          module.header.generator,
          module.header.bound,
          module.header.schema
        )
        val body = module.instructions.flatMap { instruction =>
          Vec(Word((instruction.wordCount.raw << 16) | instruction.opcodeNumber.raw)) ++
            instruction.operandWords
        }
        Right((header ++ body).flatMap(word => writeInt(word.raw, module.endianness)))
    }

  private def readInt(bytes: Vec[Byte], offset: Int, endianness: Endianness): Int =
    endianness match {
      case Endianness.Little => LittleEndian.decodeInt32(bytes, offset)
      case Endianness.Big    =>
        java.lang.Integer.reverseBytes(LittleEndian.decodeInt32(bytes, offset))
    }

  private def writeInt(value: Int, endianness: Endianness): Vec[Byte] =
    LittleEndian.toVec(
      endianness match {
        case Endianness.Little => value.toLong
        case Endianness.Big    => java.lang.Integer.reverseBytes(value).toLong
      },
      4
    )
}
