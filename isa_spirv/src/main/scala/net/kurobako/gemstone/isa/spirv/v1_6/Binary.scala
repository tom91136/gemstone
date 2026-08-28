package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.Col

enum BinaryError {
  case MisalignedByteLength(length: Int)
  case TruncatedHeader(wordCount: Int)
  case InvalidMagic(found: Long)
  case ZeroInstructionWordCount(wordOffset: Int)
  case TruncatedInstruction(wordOffset: Int, declaredWords: Int, remainingWords: Int)
  case InstructionTooLong(opcodeNumber: Int, wordCount: Int)

  def message: String = this match {
    case MisalignedByteLength(length)                      => s"SPIR-V binary length $length is not a multiple of four"
    case TruncatedHeader(wordCount)                        => s"SPIR-V header needs five words, found $wordCount"
    case InvalidMagic(found)                               => f"invalid SPIR-V magic 0x$found%08x"
    case ZeroInstructionWordCount(offset)                  => s"instruction at word $offset has zero word count"
    case TruncatedInstruction(offset, declared, remaining) =>
      s"instruction at word $offset declares $declared words with only $remaining remaining"
    case InstructionTooLong(opcode, count) => s"opcode $opcode has unencodable word count $count"
  }
}

object Binary {
  val Magic: Int = 0x07230203

  def parse(bytes: Col[Byte]): Either[BinaryError, RawModule] = {
    if (bytes.length % 4 != 0) return Left(BinaryError.MisalignedByteLength(bytes.length))
    val wordCount = bytes.length / 4
    if (wordCount < 5) return Left(BinaryError.TruncatedHeader(wordCount))
    val rawMagic   = readInt(bytes, 0, Endianness.Little)
    val endianness =
      if (rawMagic == Magic) Endianness.Little
      else if (java.lang.Integer.reverseBytes(rawMagic) == Magic) Endianness.Big
      else return Left(BinaryError.InvalidMagic(rawMagic.toLong & 0xffffffffL))
    val words        = Col.tabulate(wordCount)(index => Word(readInt(bytes, index * 4, endianness)))
    val header       = Header(SPIRVVersion.fromWord(words(1)), words(2), words(3), words(4))
    val instructions = Col.newBuilder[RawInstruction]
    var offset       = 5
    while (offset < words.size) {
      val first       = words(offset).bits
      val declared    = first >>> 16
      val opcodeValue = first & 0xffff
      if (declared == 0) return Left(BinaryError.ZeroInstructionWordCount(offset))
      val remaining = words.size - offset
      if (declared > remaining)
        return Left(BinaryError.TruncatedInstruction(offset, declared, remaining))
      instructions += RawInstruction(opcodeValue, words.slice(offset + 1, offset + declared))
      offset += declared
    }
    Right(RawModule(header, instructions.result(), endianness))
  }

  def serialize(module: RawModule): Either[BinaryError, Col[Byte]] = {
    module.instructions.find(_.wordCount > 0xffff) match {
      case Some(instruction) =>
        return Left(BinaryError.InstructionTooLong(instruction.opcodeNumber, instruction.wordCount))
      case None => ()
    }
    val words = Col.newBuilder[Word]
    words += Word(Magic)
    words += module.header.version.word
    words += module.header.generator
    words += module.header.bound
    words += module.header.schema
    module.instructions.foreach { instruction =>
      val count = instruction.wordCount
      words += Word((count << 16) | instruction.opcodeNumber)
      words ++= instruction.operandWords
    }
    val encoded = words.result()
    val bytes   = new Array[Byte](encoded.size * 4)
    encoded.zipWithIndex.foreach { case (word, index) => writeInt(bytes, index * 4, word.bits, module.endianness) }
    Right(Col.unsafeFromArray(bytes))
  }

  private def readInt(bytes: Col[Byte], offset: Int, endianness: Endianness): Int = {
    val a = bytes(offset).toInt & 0xff
    val b = bytes(offset + 1).toInt & 0xff
    val c = bytes(offset + 2).toInt & 0xff
    val d = bytes(offset + 3).toInt & 0xff
    endianness match {
      case Endianness.Little => a | (b << 8) | (c << 16) | (d << 24)
      case Endianness.Big    => (a << 24) | (b << 16) | (c << 8) | d
    }
  }

  private def writeInt(bytes: Array[Byte], offset: Int, value: Int, endianness: Endianness): Unit = {
    val shifts = endianness match {
      case Endianness.Little => Col(0, 8, 16, 24)
      case Endianness.Big    => Col(24, 16, 8, 0)
    }
    var index = 0
    while (index < 4) {
      bytes(offset + index) = ((value >>> shifts(index)) & 0xff).toByte
      index += 1
    }
  }
}
