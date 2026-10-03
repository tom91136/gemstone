package net.kurobako.gemstone.isa.spirv.v1_6

import cats.data.Chain
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import scala.annotation.tailrec

object Binary extends Decoder[Vec[Byte], RawModule, Binary.BinaryError]
    with Encoder[RawModule, Vec[Byte], Binary.BinaryError] {
  enum BinaryError {
    case MisalignedByteLength(length: Int)
    case TruncatedHeader(wordCount: Int)
    case InvalidMagic(found: Long)
    case InvalidVersion(found: Long)
    case VersionTooLarge(version: Version)
    case ZeroInstructionWordCount(wordOffset: Int)
    case TruncatedInstruction(wordOffset: Int, declaredWords: Int, remainingWords: Int)
    case InstructionTooLong(opcodeNumber: SPIRVOpcodeNumber, wordCount: Int > 0)

    def message: NonEmpty[String] = this match {
      case MisalignedByteLength(length)     => s"SPIR-V binary length $length is not a multiple of four"
      case TruncatedHeader(wordCount)       => s"SPIR-V header needs five words, found $wordCount"
      case InvalidMagic(found)              => f"invalid SPIR-V magic 0x$found%08x"
      case InvalidVersion(found)            => f"invalid SPIR-V version word 0x$found%08x"
      case VersionTooLarge(version)         => s"SPIR-V version $version does not fit the version word"
      case ZeroInstructionWordCount(offset) => s"instruction at word $offset has zero word count"
      case TruncatedInstruction(offset, declared, remaining) =>
        s"instruction at word $offset declares $declared words with only $remaining remaining"
      case InstructionTooLong(opcode, count) => s"opcode $opcode has unencodable word count $count"
    }
  }

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
        if (rawMagic == SPIRV.Magic) Right(Endianness.Little)
        else if (java.lang.Integer.reverseBytes(rawMagic) == SPIRV.Magic) Right(Endianness.Big)
        else Left(BinaryError.InvalidMagic(Bits.unsigned32(rawMagic).toLong))
      words = Vec.tabulate(wordCount)(index => Word(readInt(bytes, index * 4, endianness)))
      version <- decodeVersion(words(1)).toRight(BinaryError.InvalidVersion(words(1).toUnsigned.toLong))
      header = Header(version, words(2), words(3), words(4))
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
            RawInstruction(opcodeValue.unsafe[SPIRVOpcodeNumber], words.slice(offset + 1, offset + declared))
          )
        )
    }

  def encode(module: RawModule): Either[BinaryError, Vec[Byte]] = for {
    version <- encodeVersion(module.header.version).toRight(BinaryError.VersionTooLarge(module.header.version))
    bytes   <- module.instructions.find(_.wordCount.raw > 0xffff) match {
      case Some(instruction) =>
        Left(BinaryError.InstructionTooLong(instruction.opcodeNumber, instruction.wordCount))
      case None =>
        val header = Vec(
          Word(SPIRV.Magic),
          version,
          module.header.generator,
          module.header.bound,
          module.header.schema
        )
        val body = module.instructions.flatMap { instruction =>
          Vec(Word((instruction.wordCount.raw << 16) | instruction.opcodeNumber.toInt)) ++
            instruction.operandWords
        }
        Right((header ++ body).flatMap(word => writeInt(word.raw, module.endianness)))
    }
  } yield bytes

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
  private def decodeVersion(word: Word): Option[Version] = {
    val raw = word.toUnsigned.toLong
    // Physical layout: zero byte, major byte, minor byte, zero byte.
    if ((raw & 0xff0000ffL) != 0L) None
    else for {
      major <- Bits.unsignedIntField(raw, 16, 8).raw.refine[Int > 0].toOption
      minor = Bits.unsignedIntField(raw, 8, 8)
    } yield Version(major, minor)
  }

  private def encodeVersion(version: Version): Option[Word] =
    Option.when(version.major.raw <= 0xff && version.minor.raw <= 0xff)(
      Word((version.major.raw << 16) | (version.minor.raw << 8))
    )
}
