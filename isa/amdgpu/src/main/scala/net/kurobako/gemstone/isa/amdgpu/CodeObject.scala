package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.Col.*
import net.kurobako.gemstone.binary.Bits
import net.kurobako.gemstone.binary.ByteRange
import net.kurobako.gemstone.binary.IEEE754
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.execution.*

import cats.syntax.either.*
import cats.syntax.traverse.*

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets
import scala.collection.mutable // scalafix:ok GemstoneRuntimeCollections.mutable-collection; decoder and wave state

enum CodeObjectError {
  case Invalid(detail: String)
  case Unsupported(detail: String)

  def message: String = this match {
    case Invalid(detail)     => detail
    case Unsupported(detail) => detail
  }
}

/** One loadable kernel extracted from an ELF64 AMDGPU code object. */
final case class CodeObjectKernel private[amdgpu] (
    name: String,
    target: Target,
    kernargSize: Int,
    groupSegmentSize: Int,
    privateSegmentSize: Int,
    computePgmRsrc2: Int,
    kernelCodeProperties: Int,
    code: Col[Byte]
)

/** Strict, dependency-free ELF boundary for AMDGPU code objects. */
@SuppressWarnings(
  Array(
    "scalafix:DisableSyntax.return",
    "scalafix:GemstoneRuntimeCollections.mutable-collection"
  )
)
object CodeObject {
  private final case class Section(
      nameOffset: Int,
      sectionType: Int,
      address: Long,
      offset: Int,
      size: Int,
      link: Int,
      entrySize: Int
  )
  private final case class Symbol(name: String, value: Long, size: Int, section: Int, kind: Int)

  private val TargetPrefix  = "amdgcn-amd-amdhsa--".getBytes(StandardCharsets.US_ASCII)
  private val RawMagic      = "GEMAMD1\u0000".getBytes(StandardCharsets.US_ASCII)
  private val RawHeaderSize = 40

  def decode(image: Col[Byte]): Either[CodeObjectError, Map[String, CodeObjectKernel]] = {
    val bytes = Col.toArray(image)
    for {
      buffer      <- elfBuffer(bytes)
      target      <- findTarget(bytes)
      sections    <- readSections(buffer, bytes.length)
      names       <- sectionNames(bytes, sections, buffer.getShort(62) & 0xffff)
      symbols     <- readSymbols(bytes, sections, names)
      descriptors <- descriptorIndex(symbols)
      kernels     <- symbols.iterator
        .filter(symbol => symbol.kind == 2 && symbol.size > 0 && symbol.section > 0)
        .map(symbol => kernel(bytes, sections, names, descriptors, target, symbol).map(symbol.name -> _))
        .toCol
        .sequence
        .map(_.toMap)
      _ <- Either.cond(kernels.nonEmpty, (), CodeObjectError.Invalid("AMDGPU code object has no kernel symbols"))
    } yield kernels
  }

  /** Decode one explicitly described ROCr-internal kernel. These assembly-only ELF objects intentionally have no public
    * AMDGPU metadata or kernel descriptor, so every missing descriptor field is carried in a strict envelope by the
    * pinned virtual ROCr build. The ELF entry symbol and executable bytes are still independently validated.
    */
  def decodeRaw(image: Col[Byte]): Either[CodeObjectError, CodeObjectKernel] = {
    val bytes = Col.toArray(image)
    for {
      _ <- Either.cond(
        bytes.length >= RawHeaderSize && java.util.Arrays.equals(java.util.Arrays.copyOf(bytes, 8), RawMagic),
        (),
        CodeObjectError.Invalid("invalid AMDGPU raw-kernel envelope magic")
      )
      targetSize  <- intValue(unsignedIntAt(bytes, 8), "raw target-name size")
      nameSize    <- intValue(unsignedIntAt(bytes, 12), "raw kernel-name size")
      kernargSize <- intValue(unsignedIntAt(bytes, 16), "raw kernarg size")
      groupSize   <- intValue(unsignedIntAt(bytes, 20), "raw group-segment size")
      privateSize <- intValue(unsignedIntAt(bytes, 24), "raw private-segment size")
      elfSize     <- intValue(unsignedIntAt(bytes, 36), "raw ELF size")
      payloadSize = targetSize.toLong + nameSize.toLong + elfSize.toLong
      _ <- Either.cond(
        targetSize > 0 && nameSize > 0 && elfSize > 0 &&
          ByteRange.contains(bytes.length, RawHeaderSize, payloadSize) &&
          RawHeaderSize.toLong + payloadSize == bytes.length.toLong,
        (),
        CodeObjectError.Invalid("invalid AMDGPU raw-kernel envelope lengths")
      )
      targetName <- ascii(bytes, RawHeaderSize, targetSize, "raw target name")
      name       <- ascii(bytes, RawHeaderSize + targetSize, nameSize, "raw kernel name")
      _          <- Either.cond(
        name.nonEmpty && !name.contains('\u0000'),
        (),
        CodeObjectError.Invalid("invalid AMDGPU raw kernel name")
      )
      target <- Target.fromString(targetName).leftMap(CodeObjectError.Unsupported.apply)
      elfStart = RawHeaderSize + targetSize + nameSize
      elf      = java.util.Arrays.copyOfRange(bytes, elfStart, elfStart + elfSize)
      buffer   <- elfBuffer(elf)
      sections <- readSections(buffer, elf.length)
      names    <- sectionNames(elf, sections, buffer.getShort(62) & 0xffff)
      symbols  <- readSymbols(elf, sections, names)
      symbol   <- symbols.find(candidate => candidate.name == name && candidate.section > 0)
        .toRight(CodeObjectError.Invalid(s"AMDGPU raw kernel has no entry symbol $name"))
      section     <- sections.lift(symbol.section).toRight(CodeObjectError.Invalid(s"invalid section for $name"))
      sectionName <- names.lift(symbol.section).toRight(CodeObjectError.Invalid(s"missing section name for $name"))
      _           <- Either.cond(
        sectionName == ".text" && symbol.value >= section.address,
        (),
        CodeObjectError.Invalid(s"raw kernel $name does not point into .text")
      )
      relative <- intValue(symbol.value - section.address, s"$name section offset")
      _        <- Either.cond(
        relative < section.size,
        (),
        CodeObjectError.Invalid(s"raw kernel $name points past .text")
      )
      start = section.offset + relative
      code  = Col.unsafeFromArray(java.util.Arrays.copyOfRange(elf, start, section.offset + section.size))
    } yield CodeObjectKernel(
      name,
      target,
      kernargSize,
      groupSize,
      privateSize,
      littleInt(bytes, 28),
      littleInt(bytes, 32),
      code
    )
  }

  private def elfBuffer(bytes: Array[Byte]): Either[CodeObjectError, ByteBuffer] =
    Either.cond(
      bytes.length >= 64 &&
        bytes(0) == 0x7f.toByte && bytes(1) == 'E' && bytes(2) == 'L' && bytes(3) == 'F' &&
        bytes(4) == 2 && bytes(5) == 1,
      ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN),
      CodeObjectError.Invalid("expected a little-endian ELF64 code object")
    ).flatMap { buffer =>
      Either.cond(
        (buffer.getShort(18) & 0xffff) == 224,
        buffer,
        CodeObjectError.Invalid("ELF machine is not AMDGPU")
      )
    }

  private def readSections(
      buffer: ByteBuffer,
      imageSize: Int
  ): Either[CodeObjectError, Col[Section]] = {
    val tableOffset = buffer.getLong(40)
    val entrySize   = buffer.getShort(58) & 0xffff
    val count       = buffer.getShort(60) & 0xffff
    val tableSize   = entrySize.toLong * count.toLong
    if (entrySize < 64 || count == 0 || !ByteRange.contains(imageSize, tableOffset, tableSize))
      Left(CodeObjectError.Invalid("invalid ELF section table"))
    else
      Col.tabulate(count) { index =>
        val start       = Math.toIntExact(tableOffset + index.toLong * entrySize)
        val sectionType = buffer.getInt(start + 4)
        val offset      = buffer.getLong(start + 24)
        val size        = buffer.getLong(start + 32)
        val link        = buffer.getInt(start + 40)
        val stride      = buffer.getLong(start + 56)
        for {
          checkedOffset <- intValue(offset, "section offset")
          checkedSize   <- intValue(size, "section size")
          checkedStride <- intValue(stride, "section entry size")
          _             <- Either.cond(
            sectionType == 8 || ByteRange.contains(imageSize, offset, size),
            (),
            CodeObjectError.Invalid(s"ELF section $index exceeds the image")
          )
        } yield Section(
          buffer.getInt(start),
          sectionType,
          buffer.getLong(start + 16),
          checkedOffset,
          checkedSize,
          link,
          checkedStride
        )
      }.sequence
  }

  private def sectionNames(
      bytes: Array[Byte],
      sections: Col[Section],
      index: Int
  ): Either[CodeObjectError, Col[String]] =
    sections.lift(index).toRight(CodeObjectError.Invalid("invalid ELF section-name table index")).flatMap { table =>
      sections.map(section => cString(bytes, table, section.nameOffset)).sequence
    }

  private def readSymbols(
      bytes: Array[Byte],
      sections: Col[Section],
      names: Col[String]
  ): Either[CodeObjectError, Col[Symbol]] =
    sections.zip(names).collectFirst { case (section, ".symtab") => section }
      .toRight(CodeObjectError.Invalid("AMDGPU code object has no symbol table"))
      .flatMap { table =>
        for {
          strings <- sections.lift(table.link).toRight(CodeObjectError.Invalid("invalid ELF symbol string table"))
          _       <- Either.cond(
            table.entrySize >= 24 && table.size % table.entrySize == 0,
            (),
            CodeObjectError.Invalid("invalid ELF symbol-table entry size")
          )
          symbols <- Col.tabulate(table.size / table.entrySize) { index =>
            val start = table.offset + index * table.entrySize
            for {
              name <- cString(bytes, strings, littleInt(bytes, start))
              size <- intValue(littleLong(bytes, start + 16), "symbol size")
            } yield Symbol(
              name,
              littleLong(bytes, start + 8),
              size,
              littleShort(bytes, start + 6),
              bytes(start + 4) & 0x0f
            )
          }.sequence
        } yield symbols
      }

  private def descriptorIndex(symbols: Col[Symbol]): Either[CodeObjectError, Map[String, Symbol]] = {
    val descriptors = mutable.HashMap.empty[String, Symbol]
    val iterator    = symbols.iterator.filter(symbol => symbol.kind == 1 && symbol.name.endsWith(".kd"))
    while (iterator.hasNext) {
      val symbol = iterator.next()
      if (descriptors.contains(symbol.name))
        return Left(CodeObjectError.Invalid(s"duplicate AMDGPU kernel descriptor symbol ${symbol.name}"))
      descriptors.update(symbol.name, symbol)
    }
    Right(descriptors.toMap)
  }

  private def kernel(
      bytes: Array[Byte],
      sections: Col[Section],
      names: Col[String],
      descriptors: Map[String, Symbol],
      target: Target,
      symbol: Symbol
  ): Either[CodeObjectError, CodeObjectKernel] =
    for {
      section <- sections.lift(symbol.section).toRight(CodeObjectError.Invalid(s"invalid section for ${symbol.name}"))
      sectionName <-
        names.lift(symbol.section).toRight(CodeObjectError.Invalid(s"missing section name for ${symbol.name}"))
      _ <- Either.cond(
        sectionName == ".text" && symbol.value >= section.address,
        (),
        CodeObjectError.Invalid(s"kernel ${symbol.name} does not point into .text")
      )
      relative <- intValue(symbol.value - section.address, s"${symbol.name} section offset")
      _        <- Either.cond(
        relative <= section.size && symbol.size <= section.size - relative,
        (),
        CodeObjectError.Invalid(s"kernel ${symbol.name} exceeds .text")
      )
      start = section.offset + relative
      code  = Col.unsafeFromArray(java.util.Arrays.copyOfRange(bytes, start, start + symbol.size))
      descriptorSymbol <- descriptors.get(s"${symbol.name}.kd")
        .toRight(CodeObjectError.Invalid(s"kernel ${symbol.name} has no descriptor symbol"))
      descriptor       <- symbolBytes(bytes, sections, descriptorSymbol, 64, s"kernel ${symbol.name} descriptor")
      kernargSize      <- intValue(Bits.unsigned32(littleInt(descriptor, 8)), s"${symbol.name} kernarg size")
      groupSegmentSize <- intValue(
        Bits.unsigned32(littleInt(descriptor, 0)),
        s"${symbol.name} group-segment size"
      )
      privateSegmentSize <- intValue(
        Bits.unsigned32(littleInt(descriptor, 4)),
        s"${symbol.name} private-segment size"
      )
      computePgmRsrc2      = littleInt(descriptor, 52)
      kernelCodeProperties = littleShort(descriptor, 56)
    } yield CodeObjectKernel(
      symbol.name,
      target,
      kernargSize,
      groupSegmentSize,
      privateSegmentSize,
      computePgmRsrc2,
      kernelCodeProperties,
      code
    )

  private def symbolBytes(
      bytes: Array[Byte],
      sections: Col[Section],
      symbol: Symbol,
      minimumSize: Int,
      label: String
  ): Either[CodeObjectError, Array[Byte]] =
    for {
      section <- sections.lift(symbol.section).toRight(CodeObjectError.Invalid(s"invalid section for $label"))
      _ <- Either.cond(symbol.value >= section.address, (), CodeObjectError.Invalid(s"$label precedes its section"))
      relative <- intValue(symbol.value - section.address, s"$label section offset")
      _        <- Either.cond(
        symbol.size >= minimumSize && relative <= section.size && symbol.size <= section.size - relative,
        (),
        CodeObjectError.Invalid(s"$label is truncated")
      )
      start = section.offset + relative
    } yield java.util.Arrays.copyOfRange(bytes, start, start + symbol.size)

  private def findTarget(bytes: Array[Byte]): Either[CodeObjectError, Target] = {
    val start = bytes.indices.find(index =>
      index <= bytes.length - TargetPrefix.length &&
        java.util.Arrays.equals(
          java.util.Arrays.copyOfRange(bytes, index, index + TargetPrefix.length),
          TargetPrefix
        )
    )
    start.toRight(CodeObjectError.Invalid("AMDGPU target metadata is absent")).flatMap { prefixStart =>
      val valueStart = prefixStart + TargetPrefix.length
      val valueEnd   = Iterator.from(valueStart).takeWhile(index =>
        index < bytes.length && {
          val value = bytes(index) & 0xff
          (value >= 'a' && value <= 'z') || (value >= 'A' && value <= 'Z') ||
          (value >= '0' && value <= '9') || value == '_'
        }
      ).toCol.lastOption.map(_ + 1).getOrElse(valueStart)
      val name = String(bytes, valueStart, valueEnd - valueStart, StandardCharsets.US_ASCII)
      Target.fromString(name).leftMap(CodeObjectError.Unsupported.apply)
    }
  }

  private def cString(bytes: Array[Byte], table: Section, offset: Int): Either[CodeObjectError, String] =
    if (offset < 0 || offset >= table.size) Left(CodeObjectError.Invalid("ELF string offset is outside its table"))
    else {
      val start = table.offset + offset
      val end   = Iterator.from(start).takeWhile(index => index < table.offset + table.size && bytes(index) != 0)
        .toCol
        .lastOption
        .map(_ + 1)
        .getOrElse(start)
      Either.cond(
        end < table.offset + table.size || bytes.lift(end).contains(0),
        String(bytes, start, end - start, StandardCharsets.UTF_8),
        CodeObjectError.Invalid("unterminated ELF string")
      )
    }

  private def ascii(bytes: Array[Byte], offset: Int, size: Int, label: String): Either[CodeObjectError, String] = {
    val valid = (offset until offset + size).forall { index =>
      val value = bytes(index) & 0xff
      value >= 0x21 && value <= 0x7e
    }
    Either.cond(
      valid,
      String(bytes, offset, size, StandardCharsets.US_ASCII),
      CodeObjectError.Invalid(s"$label is not printable ASCII")
    )
  }

  private def unsignedIntAt(bytes: Array[Byte], offset: Int): Long = Bits.unsigned32(littleInt(bytes, offset))

  private def intValue(value: Long, label: String): Either[CodeObjectError, Int] =
    Either.cond(
      value >= 0L && value <= Int.MaxValue.toLong,
      value.toInt,
      CodeObjectError.Unsupported(s"$label is too large")
    )

  private def littleShort(bytes: Array[Byte], offset: Int): Int =
    LittleEndian.decode(bytes, offset, 2).toInt

  private def littleInt(bytes: Array[Byte], offset: Int): Int =
    LittleEndian.decode(bytes, offset, 4).toInt

  private def littleLong(bytes: Array[Byte], offset: Int): Long =
    LittleEndian.decode(bytes, offset, 8)
}

@SuppressWarnings(Array("scalafix:DisableSyntax.var"))
object DispatchInterpreter {
  private val WaveSize        = 64
  private val KernargAddress  = 0x100000000000L
  private val DispatchAddress = 0x200000000000L
  private val AllocationBase  = 0x400000000000L
  private val AllocationSpan  = 0x000100000000L

  private[amdgpu] def singleDSOffset(word: Long): Int                       = Bits.low16(word).toInt
  private[amdgpu] def unsignedMultiplyHigh32(left: Long, right: Long): Long =
    Bits.high32(Bits.low32(left) * Bits.low32(right))
  private[amdgpu] def signedMultiplyImmediate(register: Long, immediate: Int): (Long, Boolean) = {
    val product  = register.toInt.toLong * immediate.toLong
    val overflow = product < Int.MinValue.toLong || product > Int.MaxValue.toLong
    (Bits.low32(product), overflow)
  }
  private[amdgpu] def unsignedSubtract32(left: Long, right: Long): (Long, Boolean) = {
    val unsignedLeft  = Bits.low32(left)
    val unsignedRight = Bits.low32(right)
    (Bits.low32(unsignedLeft - unsignedRight), unsignedLeft >= unsignedRight)
  }
  private[amdgpu] def unsignedSubtractWithBorrow32(left: Long, right: Long, noBorrowIn: Boolean): (Long, Boolean) = {
    val unsignedLeft  = Bits.low32(left)
    val unsignedRight = Bits.low32(right) + (if (noBorrowIn) 0L else 1L)
    (Bits.low32(unsignedLeft - unsignedRight), unsignedLeft >= unsignedRight)
  }
  private[amdgpu] def unsignedMultiply24(left: Long, right: Long): Long =
    Bits.low32((left & 0xffffffL) * (right & 0xffffffL))
  private[amdgpu] def unsignedMultiplyAdd24(left: Long, right: Long, addend: Long): Long =
    Bits.low32(unsignedMultiply24(left, right) + Bits.low32(addend))
  private[amdgpu] def unsignedSubtractReverse32(left: Long, right: Long): Long =
    Bits.low32(Bits.low32(right) - Bits.low32(left))
  private[amdgpu] def unsignedMin32(left: Long, right: Long): Long =
    math.min(Bits.low32(left), Bits.low32(right))
  private[amdgpu] def signedMultiply24(left: Long, right: Long): Long = {
    def signed(value: Long): Long = {
      val bits = value & 0xffffffL
      if ((bits & 0x800000L) == 0L) bits else bits | ~0xffffffL
    }
    Bits.low32(signed(left) * signed(right))
  }
  private[amdgpu] def bitReverse32(value: Long): Long =
    Bits.unsigned32(java.lang.Integer.reverse(value.toInt))
  private[amdgpu] def bpermuteSourceLane(address: Long): Int =
    ((address >>> 2) & 63L).toInt
  private[amdgpu] def dppQuadSourceLane(lane: Int, control: Int): Int =
    (lane & ~3) | ((control >>> ((lane & 3) * 2)) & 3)
  private[amdgpu] def dppSourceLane(lane: Int, control: Int): Option[Int] =
    if (control <= 0xff) Some(dppQuadSourceLane(lane, control))
    else if (control >= 0x121 && control <= 0x12f) {
      val rowBase = lane & ~15
      Some(rowBase | (((lane & 15) - (control & 15)) & 15))
    } else control match {
      case 0x142 => Option.when(((lane / 16) & 1) == 1)((lane / 16 - 1) * 16 + 15)
      case 0x143 => Option.when(lane / 16 >= 2)(31)
      case _     => None
    }
  private[amdgpu] def dppLaneEnabled(lane: Int, rowMask: Int, bankMask: Int): Boolean =
    (rowMask & (1 << (lane / 16))) != 0 && (bankMask & (1 << (lane & 3))) != 0
  private[amdgpu] def mbCount(mask: Long, executionMask: Long, lane: Int, high: Boolean, base: Long): Long = {
    val laneInHalf = lane & 31
    val belowLane  = if (laneInHalf == 0) 0L else (1L << laneInHalf) - 1L
    val relevant   =
      if (high) {
        if (lane < 32) 0L else Bits.high32(executionMask) & belowLane
      } else {
        val belowLow = if (lane < 32) belowLane else Bits.Mask32
        executionMask & belowLow
      }
    Bits.low32(base + java.lang.Integer.bitCount((mask & relevant).toInt).toLong)
  }

  final class PreparedKernel private[DispatchInterpreter] (
      val kernel: CodeObjectKernel,
      private[DispatchInterpreter] val operations: Col[ExecutableInstruction]
  )

  private final case class Decoded(operation: ExecutableInstruction, size: Int)
  private final case class AllocationRegion(base: Long, id: AllocationId, offset: Long)
  private final class VectorRegisters(lanes: Int) {
    private val registers = Array.ofDim[Array[Long] | Null](256)

    def apply(index: Int): Array[Long] = registers(index) match {
      case null =>
        val allocated = Array.ofDim[Long](lanes)
        registers(index) = allocated
        allocated
      case value => value
    }
  }
  private final case class WaveState(
      scalar: Array[Long],
      vector: VectorRegisters,
      carry: Array[Boolean],
      active: Array[Boolean],
      scalarCarry: Boolean,
      nextInstruction: Int
  )

  private enum WaveBoundary {
    case Barrier(state: WaveState)
    case End
  }

  def validate(code: Col[Byte], target: Target): Either[String, Unit] =
    decodeProgram(code, target).map(_ => ())

  def prepare(kernel: CodeObjectKernel): Either[ExecutionFailure, PreparedKernel] =
    decodeProgram(kernel.code, kernel.target)
      .leftMap(ExecutionFailure.Unsupported.apply)
      .map(PreparedKernel(kernel, _))

  def execute(
      prepared: PreparedKernel,
      dispatch: KernelDispatch,
      memory: ExecutionMemory,
      control: ExecutionControl
  ): Either[ExecutionFailure, Unit] =
    for {
      packed <- packArguments(dispatch.arguments, prepared.kernel.kernargSize)
        .leftMap(ExecutionFailure.InvalidRequest.apply)
      (kernarg, allocations) = packed
      _ <- runDispatch(prepared.kernel, prepared.operations, kernarg, allocations, dispatch, memory, control)
    } yield ()

  def execute(
      kernel: CodeObjectKernel,
      dispatch: KernelDispatch,
      memory: ExecutionMemory,
      control: ExecutionControl = ExecutionControl.default
  ): Either[ExecutionFailure, Unit] = prepare(kernel).flatMap(execute(_, dispatch, memory, control))

  private def decodeProgram(code: Col[Byte], target: Target): Either[String, Col[ExecutableInstruction]] = {
    val bytes   = Col.toArray(code)
    val result  = Col.newBuilder[(Int, ExecutableInstruction)]
    var offset  = 0
    var failure = Option.empty[String]
    while (offset < bytes.length && failure.isEmpty)
      if (offset > bytes.length - 4) failure = Some(s"truncated instruction at byte $offset")
      else
        decode(bytes, offset, target) match {
          case Left(error)    => failure = Some(s"byte $offset: $error")
          case Right(decoded) =>
            result += offset -> decoded.operation
            offset += decoded.size
        }
    failure.toLeft(Col.from(result.result())).flatMap { decoded =>
      val indices = decoded.iterator.zipWithIndex.map { case ((offset, _), index) => offset -> index }.toMap
      decoded.map { case (offset, operation) =>
        operation match {
          case ExecutableInstruction.BranchRelative(condition, displacement) =>
            val targetOffset = offset.toLong + 4L + displacement.toLong * 4L
            Option.when(targetOffset >= 0L && targetOffset <= Int.MaxValue.toLong)(targetOffset.toInt)
              .flatMap(indices.get)
              .map(index => ExecutableInstruction.Branch(condition, index))
              .toRight(s"branch at byte $offset targets non-instruction byte $targetOffset")
          case other => Right(other)
        }
      }.sequence
    }
  }

  private def decode(bytes: Array[Byte], offset: Int, target: Target): Either[String, Decoded] = {
    val word = unsignedInt(bytes, offset)
    if ((word >>> 26) == 0x30L) decodeSMEM(bytes, offset, word, target)
    else if ((word >>> 23) == 0x17dL) decodeSOP1(bytes, offset, word, target)
    else if ((word >>> 23) == 0x17eL) decodeSOPC(bytes, offset, word, target)
    else if ((word >>> 23) == 0x17fL) decodeSOPP(word, target)
    else if ((word >>> 28) == 0xbL) decodeSOPK(word, target)
    else if ((word >>> 30) == 2L) decodeSOP2(bytes, offset, word, target)
    else if ((word >>> 26) == 0x34L) decodeVOP3(bytes, offset, word, target)
    else if ((word >>> 26) == 0x36L) decodeDS(bytes, offset, word, target)
    else if ((word >>> 26) == 0x37L) decodeGlobalOrFlat(bytes, offset, word, target)
    else if ((word >>> 25) == 0x3fL) decodeVOP1(bytes, offset, word, target)
    else if ((word >>> 25) == 0x3eL) decodeVOPC(bytes, offset, word, target)
    else decodeVOP2(bytes, offset, word, target)
  }

  private def decodeSMEM(
      bytes: Array[Byte],
      offset: Int,
      word: Long,
      target: Target
  ): Either[String, Decoded] =
    for {
      _      <- requireBytes(bytes, offset, 8)
      opcode <- opcodeName("SMEM", ((word >>> 18) & 0xffL).toInt, target)
      count  <- opcode match {
        case "s_load_dword"   => Right(1)
        case "s_load_dwordx2" => Right(2)
        case "s_load_dwordx4" => Right(4)
        case "s_load_dwordx8" => Right(8)
        case other            => Left(s"$other SMEM execution is not implemented")
      }
      _ <- Either.cond((word & (1L << 17)) != 0L, (), s"$opcode requires an immediate offset")
      second = unsignedInt(bytes, offset + 4)
      _ <- Either.cond((second >>> 25) == 0L, (), s"$opcode scalar offset is not implemented")
    } yield Decoded(
      ExecutableInstruction.SLoad(
        ((word >>> 6) & 0x7fL).toInt,
        count,
        ((word & 0x3fL) * 2L).toInt,
        (second & 0x1ffffffL).toInt
      ),
      8
    )

  private def decodeSOP1(
      bytes: Array[Byte],
      offset: Int,
      word: Long,
      target: Target
  ): Either[String, Decoded] =
    for {
      opcode <- opcodeName("SOP1", ((word >>> 8) & 0xffL).toInt, target)
      literal = Option.when((word & 0xffL) == 255L)(())
      _         <- requireBytes(bytes, offset, if (literal.nonEmpty) 8 else 4)
      src       <- scalarSource((word & 0xffL).toInt, literal.map(_ => unsignedInt(bytes, offset + 4)))
      operation <- opcode match {
        case "s_mov_b32"          => Right(ExecutableInstruction.SMove(((word >>> 16) & 0x7fL).toInt, src))
        case "s_mov_b64"          => Right(ExecutableInstruction.SMove64(((word >>> 16) & 0x7fL).toInt, src))
        case "s_and_saveexec_b64" => Right(ExecutableInstruction.SAndSaveExec(((word >>> 16) & 0x7fL).toInt, src))
        case other                => Left(s"$other SOP1 execution is not implemented")
      }
    } yield Decoded(operation, if (literal.nonEmpty) 8 else 4)

  private def decodeSOP2(
      bytes: Array[Byte],
      offset: Int,
      word: Long,
      target: Target
  ): Either[String, Decoded] = {
    val srcAEncoding = (word & 0xffL).toInt
    val srcBEncoding = ((word >>> 8) & 0xffL).toInt
    val hasLiteral   = srcAEncoding == 255 || srcBEncoding == 255
    for {
      _      <- requireBytes(bytes, offset, if (hasLiteral) 8 else 4)
      opcode <- opcodeName("SOP2", ((word >>> 23) & 0x7fL).toInt, target)
      literal = Option.when(hasLiteral)(unsignedInt(bytes, offset + 4))
      srcA      <- scalarSource(srcAEncoding, literal)
      srcB      <- scalarSource(srcBEncoding, literal)
      operation <- Either.cond(
        Set(
          "s_and_b32",
          "s_mul_i32",
          "s_mul_hi_u32",
          "s_add_i32",
          "s_sub_i32",
          "s_add_u32",
          "s_addc_u32",
          "s_sub_u32",
          "s_subb_u32",
          "s_lshl_b32",
          "s_lshr_b32",
          "s_min_u32",
          "s_and_b64",
          "s_andn2_b64",
          "s_or_b64",
          "s_lshl_b64",
          "s_lshr_b64",
          "s_cselect_b32",
          "s_cselect_b64"
        ).contains(opcode),
        if (opcode == "s_cselect_b32")
          ExecutableInstruction.SCSelect(((word >>> 16) & 0x7fL).toInt, srcA, srcB, 32)
        else if (opcode == "s_cselect_b64")
          ExecutableInstruction.SCSelect(((word >>> 16) & 0x7fL).toInt, srcA, srcB, 64)
        else if (opcode.endsWith("_b64"))
          ExecutableInstruction.SBinary64(opcode, ((word >>> 16) & 0x7fL).toInt, srcA, srcB)
        else ExecutableInstruction.SBinary(opcode, ((word >>> 16) & 0x7fL).toInt, srcA, srcB),
        s"$opcode SOP2 execution is not implemented"
      )
    } yield Decoded(operation, if (hasLiteral) 8 else 4)
  }

  private def decodeSOPK(word: Long, target: Target): Either[String, Decoded] =
    opcodeName("SOPK", ((word >>> 23) & 0x1fL).toInt, target).flatMap {
      case "s_movk_i32" =>
        val bits      = Bits.low16(word).toInt
        val immediate = if ((bits & 0x8000) == 0) bits else bits - 0x10000
        Right(Decoded(ExecutableInstruction.SMoveImmediate(((word >>> 16) & 0x7fL).toInt, immediate), 4))
      case "s_mulk_i32" =>
        val bits      = Bits.low16(word).toInt
        val immediate = if ((bits & 0x8000) == 0) bits else bits - 0x10000
        Right(Decoded(ExecutableInstruction.SMultiplyImmediate(((word >>> 16) & 0x7fL).toInt, immediate), 4))
      case "s_cmpk_eq_i32" =>
        val bits      = Bits.low16(word).toInt
        val immediate = if ((bits & 0x8000) == 0) bits else bits - 0x10000
        Right(Decoded(ExecutableInstruction.SCmpEqI32(((word >>> 16) & 0x7fL).toInt, immediate), 4))
      case other => Left(s"$other SOPK execution is not implemented")
    }

  private def decodeSOPC(
      bytes: Array[Byte],
      offset: Int,
      word: Long,
      target: Target
  ): Either[String, Decoded] = {
    val srcAEncoding = (word & 0xffL).toInt
    val srcBEncoding = ((word >>> 8) & 0xffL).toInt
    val hasLiteral   = srcAEncoding == 255 || srcBEncoding == 255
    for {
      _      <- requireBytes(bytes, offset, if (hasLiteral) 8 else 4)
      opcode <- opcodeName("SOPC", ((word >>> 16) & 0x7fL).toInt, target)
      literal = Option.when(hasLiteral)(unsignedInt(bytes, offset + 4))
      srcA      <- scalarSource(srcAEncoding, literal)
      srcB      <- scalarSource(srcBEncoding, literal)
      operation <- opcode match {
        case supported
            if Set(
              "s_cmp_eq_u32",
              "s_cmp_eq_u64",
              "s_cmp_gt_i32",
              "s_cmp_gt_u32",
              "s_cmp_lg_u32",
              "s_cmp_lg_u64",
              "s_cmp_lt_i32"
            ).contains(supported) =>
          Right(ExecutableInstruction.SCmp(supported, srcA, srcB))
        case other => Left(s"$other SOPC execution is not implemented")
      }
    } yield Decoded(operation, if (hasLiteral) 8 else 4)
  }

  private def decodeSOPP(word: Long, target: Target): Either[String, Decoded] =
    opcodeName("SOPP", ((word >>> 16) & 0x7fL).toInt, target).flatMap {
      case "s_waitcnt"        => Right(Decoded(ExecutableInstruction.Wait, 4))
      case "s_nop"            => Right(Decoded(ExecutableInstruction.Wait, 4))
      case "s_barrier"        => Right(Decoded(ExecutableInstruction.Barrier, 4))
      case "s_endpgm"         => Right(Decoded(ExecutableInstruction.End, 4))
      case "s_branch"         => branch(word, ScalarBranchCondition.Always)
      case "s_cbranch_scc0"   => branch(word, ScalarBranchCondition.SccZero)
      case "s_cbranch_scc1"   => branch(word, ScalarBranchCondition.SccNonZero)
      case "s_cbranch_vccz"   => branch(word, ScalarBranchCondition.VccZero)
      case "s_cbranch_vccnz"  => branch(word, ScalarBranchCondition.VccNonZero)
      case "s_cbranch_execz"  => branch(word, ScalarBranchCondition.ExecZero)
      case "s_cbranch_execnz" => branch(word, ScalarBranchCondition.ExecNonZero)
      case other              => Left(s"$other SOPP execution is not implemented")
    }

  private def branch(word: Long, condition: ScalarBranchCondition): Either[String, Decoded] = {
    val immediate    = Bits.low16(word).toInt
    val displacement = if ((immediate & 0x8000) == 0) immediate else immediate - 0x10000
    Right(Decoded(ExecutableInstruction.BranchRelative(condition, displacement), 4))
  }

  private def decodeVOP2(
      bytes: Array[Byte],
      offset: Int,
      word: Long,
      target: Target
  ): Either[String, Decoded] = {
    val srcAEncoding = (word & 0x1ffL).toInt
    val hasLiteral   = srcAEncoding == 255
    val hasSDWA      = srcAEncoding == 249
    for {
      _      <- requireBytes(bytes, offset, if (hasLiteral || hasSDWA) 8 else 4)
      opcode <- opcodeName("VOP2", ((word >>> 25) & 0x7fL).toInt, target)
      extension = Option.when(hasSDWA)(unsignedInt(bytes, offset + 4))
      srcA <- extension match {
        case Some(sdwa) => Right(InstructionSource.VectorRegister((sdwa & 0xffL).toInt))
        case None       => vectorSource(srcAEncoding, Option.when(hasLiteral)(unsignedInt(bytes, offset + 4)))
      }
      srcB = extension match {
        case Some(sdwa) if (sdwa & 0x80000000L) != 0L => InstructionSource.Scalar(((word >>> 9) & 0xffL).toInt)
        case _                                        => InstructionSource.VectorRegister(((word >>> 9) & 0xffL).toInt)
      }
      dest = ((word >>> 17) & 0xffL).toInt
      operation <- opcode match {
        case supported
            if hasSDWA && Set(
              "v_add_u16",
              "v_and_b32",
              "v_lshlrev_b32",
              "v_lshlrev_b16",
              "v_lshrrev_b32",
              "v_or_b32",
              "v_sub_u16"
            ).contains(supported) =>
          for {
            sdwa <- extension.toRight("missing SDWA extension word")
            _    <- Either.cond(
              (sdwa & 0x38380000L) == 0L,
              (),
              s"$supported SDWA source modifiers are not implemented"
            )
            _ <- Either.cond(
              ((sdwa >>> 11) & 0x3L) == 0L && ((sdwa >>> 13) & 0x7L) == 0L,
              (),
              s"$supported SDWA destination preserve/clamp modifiers are not implemented"
            )
            destSelection <- subDwordSelection(((sdwa >>> 8) & 0x7L).toInt)
            srcASelection <- subDwordSelection(((sdwa >>> 16) & 0x7L).toInt)
            srcBSelection <- subDwordSelection(((sdwa >>> 24) & 0x7L).toInt)
          } yield ExecutableInstruction.VBinarySubDword(
            supported,
            dest,
            srcA,
            srcB,
            destSelection,
            srcASelection,
            srcBSelection
          )
        case "v_mov_b32"     => Right(ExecutableInstruction.VMove(dest, srcA))
        case "v_cndmask_b32" => Right(ExecutableInstruction.VCndMask(dest, srcA, srcB, InstructionSource.Scalar(106)))
        case "v_add_u32" | "v_add_co_u32" | "v_addc_co_u32" | "v_sub_co_u32" | "v_subb_co_u32" |
            "v_ashrrev_i32" | "v_lshlrev_b32" | "v_lshrrev_b32" | "v_add_u16" | "v_and_b32" | "v_or_b32" |
            "v_lshlrev_b16" | "v_sub_u16" | "v_sub_u32" | "v_subrev_u32" | "v_mul_u32_u24" |
            "v_mul_i32_i24" | "v_add_f32" | "v_sub_f32" =>
          Right(ExecutableInstruction.VBinary(opcode, dest, srcA, srcB))
        case other => Left(s"$other VOP2 execution is not implemented")
      }
    } yield Decoded(operation, if (hasLiteral || hasSDWA) 8 else 4)
  }

  private def decodeVOP1(
      bytes: Array[Byte],
      offset: Int,
      word: Long,
      target: Target
  ): Either[String, Decoded] = {
    val sourceEncoding = (word & 0x1ffL).toInt
    val hasLiteral     = sourceEncoding == 255
    val hasDPP         = sourceEncoding == 250
    for {
      _      <- requireBytes(bytes, offset, if (hasLiteral || hasDPP) 8 else 4)
      opcode <- opcodeName("VOP1", ((word >>> 9) & 0xffL).toInt, target)
      extension = Option.when(hasDPP)(unsignedInt(bytes, offset + 4))
      src <- extension match {
        case Some(dpp) => Right(InstructionSource.VectorRegister((dpp & 0xffL).toInt))
        case None      => vectorSource(sourceEncoding, Option.when(hasLiteral)(unsignedInt(bytes, offset + 4)))
      }
      operation <- opcode match {
        case "v_mov_b32" if extension.nonEmpty =>
          val dpp     = extension.get
          val control = ((dpp >>> 8) & 0x1ffL).toInt
          for {
            _ <- Either.cond(
              control <= 0xff || (control >= 0x121 && control <= 0x12f) || control == 0x142 || control == 0x143,
              (),
              s"v_mov_b32 DPP control $control is not implemented"
            )
            _ <- Either.cond(
              (dpp & 0x00f00000L) == 0L,
              (),
              "v_mov_b32 DPP source modifiers are not implemented"
            )
          } yield ExecutableInstruction.VMoveDPP(
            ((word >>> 17) & 0xffL).toInt,
            (dpp & 0xffL).toInt,
            control,
            ((dpp >>> 28) & 0xfL).toInt,
            ((dpp >>> 24) & 0xfL).toInt,
            (dpp & (1L << 19)) != 0L,
            (dpp & (1L << 18)) != 0L
          )
        case "v_mov_b32"                       => Right(ExecutableInstruction.VMove(((word >>> 17) & 0xffL).toInt, src))
        case "v_bfrev_b32" | "v_bfrev_b32_e32" =>
          Right(ExecutableInstruction.VBitReverse(((word >>> 17) & 0xffL).toInt, src))
        case "v_readfirstlane_b32" =>
          Right(ExecutableInstruction.VReadFirstLane(((word >>> 17) & 0x7fL).toInt, src))
        case other => Left(s"$other VOP1 execution is not implemented")
      }
    } yield Decoded(operation, if (hasLiteral || hasDPP) 8 else 4)
  }

  private def decodeVOPC(
      bytes: Array[Byte],
      offset: Int,
      word: Long,
      target: Target
  ): Either[String, Decoded] = {
    val srcAEncoding = (word & 0x1ffL).toInt
    val hasLiteral   = srcAEncoding == 255
    for {
      _      <- requireBytes(bytes, offset, if (hasLiteral) 8 else 4)
      opcode <- opcodeName("VOPC", ((word >>> 17) & 0xffL).toInt, target)
      srcA   <- vectorSource(srcAEncoding, Option.when(hasLiteral)(unsignedInt(bytes, offset + 4)))
      srcB = InstructionSource.VectorRegister(((word >>> 9) & 0xffL).toInt)
      _ <- Either.cond(
        Set(
          "v_cmp_eq_u32",
          "v_cmp_eq_u64",
          "v_cmp_lg_u16",
          "v_cmp_lg_u32",
          "v_cmp_lg_u64",
          "v_cmp_ne_u32",
          "v_cmp_ne_u64",
          "v_cmp_ge_u32",
          "v_cmp_ge_u64",
          "v_cmp_gt_i32",
          "v_cmp_gt_i64",
          "v_cmp_gt_u32",
          "v_cmp_gt_u64",
          "v_cmp_le_i32",
          "v_cmp_le_u32",
          "v_cmp_le_u64",
          "v_cmp_lt_i32",
          "v_cmp_lt_i64",
          "v_cmp_lt_u32",
          "v_cmp_lt_u64"
        ).contains(opcode),
        (),
        s"$opcode VOPC execution is not implemented"
      )
    } yield Decoded(ExecutableInstruction.VCompare(opcode, srcA, srcB), if (hasLiteral) 8 else 4)
  }

  private def decodeVOP3(
      bytes: Array[Byte],
      offset: Int,
      word: Long,
      target: Target
  ): Either[String, Decoded] =
    for {
      _      <- requireBytes(bytes, offset, 8)
      opcode <- opcodeName("VOP3", ((word >>> 16) & 0x3ffL).toInt, target)
      second = unsignedInt(bytes, offset + 4)
      srcA <- vectorSource((second & 0x1ffL).toInt, None)
      srcB <- vectorSource(((second >>> 9) & 0x1ffL).toInt, None)
      srcC <- vectorSource(((second >>> 18) & 0x1ffL).toInt, None)
      _    <- Either.cond(
        (second >>> 27) == 0L || Set("v_pk_add_u16", "v_pk_sub_i16", "v_add_f64", "v_add_f64_e64").contains(opcode),
        (),
        s"$opcode source modifiers are not implemented"
      )
      operation <- opcode match {
        case "v_lshlrev_b64" | "v_lshlrev_b64_e64" =>
          Right(ExecutableInstruction.VShiftLeft64((word & 0xffL).toInt, srcA, srcB))
        case "v_mbcnt_lo_u32_b32" | "v_mbcnt_lo_u32_b32_e64" =>
          Right(ExecutableInstruction.VMaskBitCount((word & 0xffL).toInt, srcA, srcB, false))
        case "v_mbcnt_hi_u32_b32" | "v_mbcnt_hi_u32_b32_e64" =>
          Right(ExecutableInstruction.VMaskBitCount((word & 0xffL).toInt, srcA, srcB, true))
        case "v_mul_lo_u32" | "v_mul_hi_u32" =>
          Right(ExecutableInstruction.VBinary(opcode, (word & 0xffL).toInt, srcA, srcB))
        case "v_cndmask_b32" | "v_cndmask_b32_e64" => srcC match {
            case InstructionSource.Scalar(_) =>
              Right(ExecutableInstruction.VCndMask((word & 0xffL).toInt, srcA, srcB, srcC))
            case _ => Left(s"$opcode condition is not a scalar mask")
          }
        case "v_add_co_u32" =>
          Right(ExecutableInstruction.VAddCarry((word & 0xffL).toInt, ((word >>> 8) & 0x7fL).toInt, srcA, srcB, None))
        case "v_addc_co_u32" =>
          Right(ExecutableInstruction.VAddCarry(
            (word & 0xffL).toInt,
            ((word >>> 8) & 0x7fL).toInt,
            srcA,
            srcB,
            Some(srcC)
          ))
        case "v_add3_u32" | "v_mad_legacy_u16" | "v_mad_u32_u24" | "v_alignbit_b32" | "v_bfi_b32" |
            "v_add_lshl_u32" | "v_lshl_add_u32" | "v_lshl_or_b32" | "v_perm_b32" =>
          Right(ExecutableInstruction.VTernary(opcode, (word & 0xffL).toInt, srcA, srcB, srcC))
        case packed @ ("v_pk_add_u16" | "v_pk_sub_i16") =>
          Either.cond(
            (second >>> 29) == 0L,
            ExecutableInstruction.VPackedBinary(
              packed,
              (word & 0xffL).toInt,
              srcA,
              srcB,
              (second & (1L << 27)) != 0L,
              (second & (1L << 28)) != 0L
            ),
            s"$packed packed modifiers are not implemented"
          )
        case "v_add_f64" | "v_add_f64_e64" =>
          Either.cond(
            (second & 0xb8000000L) == 0L,
            ExecutableInstruction.VFloat64Add((word & 0xffL).toInt, srcA, srcB, (second & 0x40000000L) != 0L),
            "v_add_f64 source modifiers other than src1 negation are not implemented"
          )
        case "v_mad_u64_u32" =>
          Right(ExecutableInstruction.VMadU64U32((word & 0xffL).toInt, ((word >>> 8) & 0x7fL).toInt, srcA, srcB, srcC))
        case compare
            if Set(
              "v_cmp_eq_u32",
              "v_cmp_ne_u32",
              "v_cmp_gt_u32",
              "v_cmp_ge_u64",
              "v_cmp_le_u64",
              "v_cmp_lt_i64",
              "v_cmp_lt_u64"
            ).contains(compare) =>
          Right(ExecutableInstruction.VCompareTo((word & 0x7fL).toInt, compare, srcA, srcB))
        case other => Left(s"$other VOP3 execution is not implemented")
      }
    } yield Decoded(operation, 8)

  private def decodeDS(
      bytes: Array[Byte],
      offset: Int,
      word: Long,
      target: Target
  ): Either[String, Decoded] =
    for {
      _      <- requireBytes(bytes, offset, 8)
      opcode <- opcodeName("DS", ((word >>> 17) & 0xffL).toInt, target)
      _      <- Either.cond((word & 0x10000L) == 0L, (), s"$opcode GDS execution is not implemented")
      second   = unsignedInt(bytes, offset + 4)
      address  = (second & 0xffL).toInt
      dataA    = ((second >>> 8) & 0xffL).toInt
      dataB    = ((second >>> 16) & 0xffL).toInt
      dest     = ((second >>> 24) & 0xffL).toInt
      offsetA  = (word & 0xffL).toInt
      offsetB  = ((word >>> 8) & 0xffL).toInt
      dsOffset = singleDSOffset(word)
      operation <- opcode match {
        case "ds_bpermute_b32"     => Right(ExecutableInstruction.DSBPermute(dest, address, dataA))
        case "ds_write_b8"         => Right(ExecutableInstruction.DSWrite(address, dataA, None, 1, dsOffset, None))
        case "ds_write_b16"        => Right(ExecutableInstruction.DSWrite(address, dataA, None, 2, dsOffset, None))
        case "ds_write_b32"        => Right(ExecutableInstruction.DSWrite(address, dataA, None, 4, dsOffset, None))
        case "ds_write_b64"        => Right(ExecutableInstruction.DSWrite(address, dataA, None, 8, dsOffset, None))
        case "ds_write_b16_d16_hi" => Right(ExecutableInstruction.DSWriteD16High(address, dataA, dsOffset))
        case "ds_write2_b32"       =>
          Right(ExecutableInstruction.DSWrite(address, dataA, Some(dataB), 4, offsetA * 4, Some(offsetB * 4)))
        case "ds_write2_b64" =>
          Right(ExecutableInstruction.DSWrite(address, dataA, Some(dataB), 8, offsetA * 8, Some(offsetB * 8)))
        case "ds_write2st64_b32" =>
          Right(ExecutableInstruction.DSWrite(address, dataA, Some(dataB), 4, offsetA * 64 * 4, Some(offsetB * 64 * 4)))
        case "ds_write2st64_b64" =>
          Right(ExecutableInstruction.DSWrite(address, dataA, Some(dataB), 8, offsetA * 64 * 8, Some(offsetB * 64 * 8)))
        case "ds_read_u8"       => Right(ExecutableInstruction.DSRead(dest, address, 1, dsOffset, None))
        case "ds_read_u16"      => Right(ExecutableInstruction.DSRead(dest, address, 2, dsOffset, None))
        case "ds_read_b32"      => Right(ExecutableInstruction.DSRead(dest, address, 4, dsOffset, None))
        case "ds_read_b64"      => Right(ExecutableInstruction.DSRead(dest, address, 8, dsOffset, None))
        case "ds_read2_b32"     => Right(ExecutableInstruction.DSRead(dest, address, 4, offsetA * 4, Some(offsetB * 4)))
        case "ds_read2_b64"     => Right(ExecutableInstruction.DSRead(dest, address, 8, offsetA * 8, Some(offsetB * 8)))
        case "ds_read2st64_b32" =>
          Right(ExecutableInstruction.DSRead(dest, address, 4, offsetA * 64 * 4, Some(offsetB * 64 * 4)))
        case "ds_read2st64_b64" =>
          Right(ExecutableInstruction.DSRead(dest, address, 8, offsetA * 64 * 8, Some(offsetB * 64 * 8)))
        case other => Left(s"$other DS execution is not implemented")
      }
    } yield Decoded(operation, 8)

  private def decodeGlobalOrFlat(
      bytes: Array[Byte],
      offset: Int,
      word: Long,
      target: Target
  ): Either[String, Decoded] =
    for {
      _ <- requireBytes(bytes, offset, 8)
      second        = unsignedInt(bytes, offset + 4)
      scalarAddress = ((second >>> 16) & 0x7fL).toInt
      operation <- if ((word & 0x8000L) != 0L) {
        val scalar = Option.when(scalarAddress != 0x7f)(scalarAddress)
        opcodeName("GLOBAL", ((word >>> 18) & 0x7fL).toInt, target).flatMap {
          case "global_load_dword" =>
            Right(ExecutableInstruction.GlobalLoad(
              ((second >>> 24) & 0xffL).toInt,
              (second & 0xffL).toInt,
              scalar,
              4,
              (word & 0x1fffL).toInt
            ))
          case "global_load_dwordx2" =>
            Right(ExecutableInstruction.GlobalLoad(
              ((second >>> 24) & 0xffL).toInt,
              (second & 0xffL).toInt,
              scalar,
              8,
              (word & 0x1fffL).toInt
            ))
          case "global_load_dwordx4" =>
            Right(ExecutableInstruction.GlobalLoad(
              ((second >>> 24) & 0xffL).toInt,
              (second & 0xffL).toInt,
              scalar,
              16,
              (word & 0x1fffL).toInt
            ))
          case "global_load_ubyte" =>
            Right(ExecutableInstruction.GlobalLoad(
              ((second >>> 24) & 0xffL).toInt,
              (second & 0xffL).toInt,
              scalar,
              1,
              (word & 0x1fffL).toInt
            ))
          case d16 @ ("global_load_short_d16" | "global_load_short_d16_hi") =>
            Right(ExecutableInstruction.GlobalLoadD16(
              ((second >>> 24) & 0xffL).toInt,
              (second & 0xffL).toInt,
              scalar,
              (word & 0x1fffL).toInt,
              d16.endsWith("_hi")
            ))
          case "global_load_ushort" =>
            Right(ExecutableInstruction.GlobalLoad(
              ((second >>> 24) & 0xffL).toInt,
              (second & 0xffL).toInt,
              scalar,
              2,
              (word & 0x1fffL).toInt
            ))
          case "global_store_dword" =>
            Right(ExecutableInstruction.GlobalStore(
              (second & 0xffL).toInt,
              scalar,
              ((second >>> 8) & 0xffL).toInt,
              4,
              (word & 0x1fffL).toInt
            ))
          case "global_store_dwordx2" =>
            Right(ExecutableInstruction.GlobalStore(
              (second & 0xffL).toInt,
              scalar,
              ((second >>> 8) & 0xffL).toInt,
              8,
              (word & 0x1fffL).toInt
            ))
          case "global_store_dwordx4" =>
            Right(ExecutableInstruction.GlobalStore(
              (second & 0xffL).toInt,
              scalar,
              ((second >>> 8) & 0xffL).toInt,
              16,
              (word & 0x1fffL).toInt
            ))
          case "global_store_byte" =>
            Right(ExecutableInstruction.GlobalStore(
              (second & 0xffL).toInt,
              scalar,
              ((second >>> 8) & 0xffL).toInt,
              1,
              (word & 0x1fffL).toInt
            ))
          case "global_store_short" =>
            Right(ExecutableInstruction.GlobalStore(
              (second & 0xffL).toInt,
              scalar,
              ((second >>> 8) & 0xffL).toInt,
              2,
              (word & 0x1fffL).toInt
            ))
          case d16 @ ("global_store_byte_d16_hi" | "global_store_short_d16_hi") =>
            Right(ExecutableInstruction.GlobalStoreD16High(
              (second & 0xffL).toInt,
              scalar,
              ((second >>> 8) & 0xffL).toInt,
              if (d16.contains("byte")) 1 else 2,
              (word & 0x1fffL).toInt
            ))
          case other => Left(s"$other GLOBAL execution is not implemented")
        }
      } else if (scalarAddress == 0) {
        opcodeName("FLAT", ((word >>> 18) & 0x7fL).toInt, target).flatMap {
          case "flat_load_ubyte" =>
            Right(ExecutableInstruction.FlatLoad(
              ((second >>> 24) & 0xffL).toInt,
              (second & 0xffL).toInt,
              1,
              (word & 0x1fffL).toInt
            ))
          case "flat_load_dword" =>
            Right(ExecutableInstruction.FlatLoad(
              ((second >>> 24) & 0xffL).toInt,
              (second & 0xffL).toInt,
              4,
              (word & 0x1fffL).toInt
            ))
          case "flat_load_dwordx4" =>
            Right(ExecutableInstruction.FlatLoad(
              ((second >>> 24) & 0xffL).toInt,
              (second & 0xffL).toInt,
              16,
              (word & 0x1fffL).toInt
            ))
          case "flat_store_short" =>
            Right(ExecutableInstruction.FlatStore(
              (second & 0xffL).toInt,
              ((second >>> 8) & 0xffL).toInt,
              2,
              (word & 0x1fffL).toInt
            ))
          case "flat_store_byte" =>
            Right(ExecutableInstruction.FlatStore(
              (second & 0xffL).toInt,
              ((second >>> 8) & 0xffL).toInt,
              1,
              (word & 0x1fffL).toInt
            ))
          case "flat_store_dword" =>
            Right(ExecutableInstruction.FlatStore(
              (second & 0xffL).toInt,
              ((second >>> 8) & 0xffL).toInt,
              4,
              (word & 0x1fffL).toInt
            ))
          case "flat_store_dwordx2" =>
            Right(ExecutableInstruction.FlatStore(
              (second & 0xffL).toInt,
              ((second >>> 8) & 0xffL).toInt,
              8,
              (word & 0x1fffL).toInt
            ))
          case "flat_store_dwordx4" =>
            Right(ExecutableInstruction.FlatStore(
              (second & 0xffL).toInt,
              ((second >>> 8) & 0xffL).toInt,
              16,
              (word & 0x1fffL).toInt
            ))
          case other => Left(s"$other FLAT execution is not implemented")
        }
      } else Left(s"VMEM scalar address s[$scalarAddress:${scalarAddress + 1}] is not implemented")
    } yield Decoded(operation, 8)

  private def opcodeName(format: String, opcode: Int, target: Target): Either[String, String] = {
    val names = Instruction.definitions.iterator.filter(definition =>
      encodingsFor(definition, target).exists(encoding =>
        (encoding.format == format || (format == "VOP3" && encoding.format.startsWith("VOP3_"))) &&
          encoding.opcode == opcode
      )
    ).map(_.opcode).toCol.distinct
    names match {
      case Col(name)                                                 => Right(name)
      case Col() if format == "VOP3" && target.family == Family.GFX9 =>
        Map(
          202 -> "v_cmp_eq_u32",
          204 -> "v_cmp_gt_u32",
          205 -> "v_cmp_ne_u32",
          225 -> "v_cmp_lt_i64",
          233 -> "v_cmp_lt_u64",
          235 -> "v_cmp_le_u64",
          238 -> "v_cmp_ge_u64",
          256 -> "v_cndmask_b32_e64",
          281 -> "v_add_co_u32",
          284 -> "v_addc_co_u32",
          493 -> "v_perm_b32",
          509 -> "v_lshl_add_u32",
          510 -> "v_add_lshl_u32",
          512 -> "v_lshl_or_b32",
          458 -> "v_bfi_b32",
          899 -> "v_pk_sub_i16",
          906 -> "v_pk_add_u16",
          640 -> "v_add_f64"
        ).get(opcode).toRight(s"unknown $format opcode $opcode for ${target.name}")
      case Col() => Left(s"unknown $format opcode $opcode for ${target.name}")
      case aliases if aliases.toSet == Set("v_mul_lo_i32", "v_mul_lo_u32") =>
        Right("v_mul_lo_u32")
      case _ => Left(s"ambiguous $format opcode $opcode for ${target.name}: ${names.mkString(", ")}")
    }
  }

  private def scalarSource(encoding: Int, literal: Option[Long]): Either[String, InstructionSource] =
    if (encoding <= 127) Right(InstructionSource.Scalar(encoding))
    else inlineConstant(encoding, literal)

  private def vectorSource(encoding: Int, literal: Option[Long]): Either[String, InstructionSource] =
    if (encoding >= 256) Right(InstructionSource.VectorRegister(encoding - 256))
    else scalarSource(encoding, literal)

  private def inlineConstant(encoding: Int, literal: Option[Long]): Either[String, InstructionSource] =
    if (encoding >= 128 && encoding <= 192) Right(InstructionSource.Constant(encoding - 128L))
    else if (encoding >= 193 && encoding <= 208) Right(InstructionSource.Constant(192L - encoding))
    else if (encoding >= 240 && encoding <= 248)
      Right(
        InstructionSource.Constant(
          Col(
            0x3f000000L,
            0xbf000000L,
            0x3f800000L,
            0xbf800000L,
            0x40000000L,
            0xc0000000L,
            0x40800000L,
            0xc0800000L,
            0x3e22f983L
          )(encoding - 240)
        )
      )
    else if (encoding == 255) literal.map(InstructionSource.Constant.apply).toRight("missing literal dword")
    else Left(s"source encoding $encoding is not implemented")

  private def subDwordSelection(encoding: Int): Either[String, SubDwordSelection] =
    SubDwordSelection.values.lift(encoding).toRight(s"unknown SDWA selection $encoding")

  private def packArguments(
      arguments: Col[KernelArgument],
      kernargSize: Int
  ): Either[String, (Array[Byte], Col[AllocationRegion])] = {
    val allocationIds   = arguments.collect { case KernelArgument.Allocation(id, _) => id }.distinct
    val allocationBases = allocationIds.zipWithIndex.map { case (id, index) =>
      id -> (AllocationBase + index.toLong * AllocationSpan)
    }.toMap
    val allocations = allocationIds.map(id => AllocationRegion(allocationBases(id), id, 0L))
    val parts       = arguments.map {
      case KernelArgument.Scalar(bytes)          => Right(bytes)
      case KernelArgument.Allocation(id, offset) =>
        Either.cond(
          offset >= 0L && offset < AllocationSpan, {
            val address = allocationBases(id) + offset
            LittleEndian.toCol(address, 8)
          },
          s"allocation offset $offset is outside the interpreter address span"
        )
      case KernelArgument.Local(_) => Left("gfx900 local kernargs are not implemented")
    }
    parts.sequence.flatMap { validParts =>
      val packed = Col.toArray(validParts.flatten)
      Either.cond(
        packed.length == kernargSize,
        packed -> allocations,
        s"kernel descriptor requires $kernargSize kernarg bytes, received ${packed.length}"
      )
    }
  }

  private def runDispatch(
      kernel: CodeObjectKernel,
      operations: Col[ExecutableInstruction],
      kernarg: Array[Byte],
      allocations: Col[AllocationRegion],
      dispatch: KernelDispatch,
      memory: ExecutionMemory,
      control: ExecutionControl
  ): Either[ExecutionFailure, Unit] =
    for {
      localCount <- dispatch.geometry.groupSize.volumeInt.leftMap(ExecutionFailure.InvalidRequest.apply)
      _          <- {
        val dispatchBytes = Array.ofDim[Byte](64)
        LittleEndian.write(dispatch.geometry.groupSize.x.toLong, dispatchBytes, 4, 2)
        LittleEndian.write(dispatch.geometry.groupSize.y.toLong, dispatchBytes, 6, 2)
        LittleEndian.write(dispatch.geometry.groupSize.z.toLong, dispatchBytes, 8, 2)
        LittleEndian.write(dispatch.geometry.globalSize.x.toLong, dispatchBytes, 12, 4)
        LittleEndian.write(dispatch.geometry.globalSize.y.toLong, dispatchBytes, 16, 4)
        LittleEndian.write(dispatch.geometry.globalSize.z.toLong, dispatchBytes, 20, 4)
        LittleEndian.write(dispatch.privateSegmentBytes.toLong, dispatchBytes, 24, 4)
        LittleEndian.write(dispatch.groupSegmentBytes.toLong, dispatchBytes, 28, 4)
        var groupZ  = 0
        var failure = Option.empty[ExecutionFailure]
        while (groupZ < dispatch.geometry.groupCount.z && failure.isEmpty) {
          var groupY = 0
          while (groupY < dispatch.geometry.groupCount.y && failure.isEmpty) {
            var groupX = 0
            while (groupX < dispatch.geometry.groupCount.x && failure.isEmpty) {
              failure = runGroup(
                kernel,
                operations,
                kernarg,
                dispatchBytes,
                allocations,
                dispatch,
                localCount,
                groupX,
                groupY,
                groupZ,
                memory,
                control
              ).left.toOption
              groupX += 1
            }
            groupY += 1
          }
          groupZ += 1
        }
        failure.toLeft(())
      }
    } yield ()

  private def runGroup(
      kernel: CodeObjectKernel,
      operations: Col[ExecutableInstruction],
      kernarg: Array[Byte],
      dispatchBytes: Array[Byte],
      allocations: Col[AllocationRegion],
      dispatch: KernelDispatch,
      localCount: Int,
      groupX: Int,
      groupY: Int,
      groupZ: Int,
      memory: ExecutionMemory,
      control: ExecutionControl
  ): Either[ExecutionFailure, Unit] = {
    val lds     = Array.ofDim[Byte](math.max(kernel.groupSegmentSize, dispatch.groupSegmentBytes))
    val initial = Col.tabulate((localCount + WaveSize - 1) / WaveSize)(_ * WaveSize).map(start =>
      (start, math.min(WaveSize, localCount - start), Option.empty[WaveState])
    )

    def phase(waves: Col[(Int, Int, Option[WaveState])]): Either[ExecutionFailure, Unit] =
      waves.map { case (start, lanes, state) =>
        runWave(
          kernel,
          operations,
          kernarg,
          dispatchBytes,
          allocations,
          groupX,
          groupY,
          groupZ,
          start,
          lanes,
          lds,
          Array.tabulate(lanes) { lane =>
            val linear = start + lane
            val localX = linear                                   % dispatch.geometry.groupSize.x
            val localY = (linear / dispatch.geometry.groupSize.x) % dispatch.geometry.groupSize.y
            val localZ =
              (linear.toLong / (dispatch.geometry.groupSize.x.toLong * dispatch.geometry.groupSize.y.toLong)).toInt
            groupX * dispatch.geometry.groupSize.x + localX < dispatch.geometry.globalSize.x &&
            groupY * dispatch.geometry.groupSize.y + localY < dispatch.geometry.globalSize.y &&
            groupZ * dispatch.geometry.groupSize.z + localZ < dispatch.geometry.globalSize.z
          },
          state,
          memory,
          control
        ).map(start -> lanes -> _)
      }.sequence.flatMap { boundaries =>
        val waiting = boundaries.collect { case ((start, lanes), WaveBoundary.Barrier(state)) =>
          (start, lanes, Some(state))
        }
        if (waiting.isEmpty) Right(())
        else if (waiting.length == boundaries.length) phase(waiting)
        else Left(ExecutionFailure.Failed("AMDGPU workgroup diverged around s_barrier"))
      }

    phase(initial)
  }

  private def runWave(
      kernel: CodeObjectKernel,
      operations: Col[ExecutableInstruction],
      kernarg: Array[Byte],
      dispatchBytes: Array[Byte],
      allocations: Col[AllocationRegion],
      groupX: Int,
      groupY: Int,
      groupZ: Int,
      localStart: Int,
      lanes: Int,
      lds: Array[Byte],
      initialActive: Array[Boolean],
      initial: Option[WaveState],
      memory: ExecutionMemory,
      control: ExecutionControl
  ): Either[ExecutionFailure, WaveBoundary] = {
    val scalar                                 = initial.fold(Array.fill[Long](128)(0L))(_.scalar)
    val vector                                 = initial.fold(VectorRegisters(lanes))(_.vector)
    val carry                                  = initial.fold(Array.fill(lanes)(false))(_.carry)
    val active                                 = initial.fold(initialActive)(_.active)
    var scalarCarry                            = initial.fold(false)(_.scalarCarry)
    var nextUserSgpr                           = 0
    def enabled(value: Int, bit: Int): Boolean = (value & (1 << bit)) != 0
    def reserve(count: Int): Int               = {
      val start = nextUserSgpr
      nextUserSgpr += count
      start
    }
    if (initial.isEmpty) {
      if (enabled(kernel.kernelCodeProperties, 0)) nextUserSgpr += 4
      if (enabled(kernel.kernelCodeProperties, 1)) setPair(scalar, reserve(2), DispatchAddress)
      if (enabled(kernel.kernelCodeProperties, 2)) setPair(scalar, reserve(2), 0L)
      if (enabled(kernel.kernelCodeProperties, 3)) setPair(scalar, reserve(2), KernargAddress)
      if (enabled(kernel.kernelCodeProperties, 4)) setPair(scalar, reserve(2), 0L)
      if (enabled(kernel.kernelCodeProperties, 5)) setPair(scalar, reserve(2), 0L)
      if (enabled(kernel.kernelCodeProperties, 6)) scalar(reserve(1)) = kernel.privateSegmentSize.toLong
      if (enabled(kernel.computePgmRsrc2, 7)) scalar(reserve(1)) = Bits.unsigned32(groupX)
      if (enabled(kernel.computePgmRsrc2, 8)) scalar(reserve(1)) = Bits.unsigned32(groupY)
      if (enabled(kernel.computePgmRsrc2, 9)) scalar(reserve(1)) = Bits.unsigned32(groupZ)
      if (enabled(kernel.computePgmRsrc2, 10)) scalar(reserve(1)) = 0L
      (0 until lanes).foreach(lane => vector(0)(lane) = localStart.toLong + lane.toLong)
    }

    def laneMask(flags: Array[Boolean]): Long = {
      var mask = 0L
      var lane = 0
      while (lane < flags.length) {
        if (flags(lane)) mask |= 1L << lane
        lane += 1
      }
      mask
    }

    def source(value: InstructionSource, lane: Int): Long = value match {
      case InstructionSource.Scalar(106)           => Bits.low32(laneMask(carry))
      case InstructionSource.Scalar(107)           => Bits.high32(laneMask(carry))
      case InstructionSource.Scalar(126)           => Bits.low32(laneMask(active))
      case InstructionSource.Scalar(127)           => Bits.high32(laneMask(active))
      case InstructionSource.Scalar(index)         => scalar(index)
      case InstructionSource.VectorRegister(index) => vector(index)(lane)
      case InstructionSource.Constant(raw)         => raw
    }

    def sourceScalar64(value: InstructionSource): Long = value match {
      case InstructionSource.Scalar(106)       => laneMask(carry)
      case InstructionSource.Scalar(126)       => laneMask(active)
      case InstructionSource.Scalar(index)     => pair(scalar, index)
      case InstructionSource.Constant(raw)     => raw
      case InstructionSource.VectorRegister(_) => 0L
    }

    def writeScalar64(dest: Int, value: Long): Unit = dest match {
      case 106 =>
        var lane = 0
        while (lane < carry.length) {
          carry(lane) = (value & (1L << lane)) != 0L
          lane += 1
        }
      case 126 =>
        var lane = 0
        while (lane < active.length) {
          active(lane) = (value & (1L << lane)) != 0L
          lane += 1
        }
      case _ => setPair(scalar, dest, value)
    }

    def foreachLane(operation: Int => Unit): Unit = {
      var lane = 0
      while (lane < lanes) {
        operation(lane)
        lane += 1
      }
    }

    def foreachActive(operation: Int => Unit): Unit = {
      var lane = 0
      while (lane < lanes) {
        if (active(lane)) operation(lane)
        lane += 1
      }
    }

    def step(index: Int): Either[ExecutionFailure, Option[Int]] =
      operations.lift(index) match {
        case None => Left(ExecutionFailure.Failed("AMDGPU kernel reached the end without s_endpgm"))
        case Some(ExecutableInstruction.End)                       => Right(None)
        case Some(ExecutableInstruction.Wait)                      => Right(Some(index + 1))
        case Some(ExecutableInstruction.Barrier)                   => Right(Some(-1))
        case Some(ExecutableInstruction.Branch(condition, target)) =>
          val taken = condition match {
            case ScalarBranchCondition.Always      => true
            case ScalarBranchCondition.SccZero     => !scalarCarry
            case ScalarBranchCondition.SccNonZero  => scalarCarry
            case ScalarBranchCondition.VccZero     => !carry.contains(true)
            case ScalarBranchCondition.VccNonZero  => carry.contains(true)
            case ScalarBranchCondition.ExecZero    => !active.contains(true)
            case ScalarBranchCondition.ExecNonZero => active.contains(true)
          }
          Right(Some(if (taken) target else index + 1))
        case Some(ExecutableInstruction.BranchRelative(_, _)) =>
          Left(ExecutionFailure.Failed("unresolved AMDGPU branch reached execution"))
        case Some(ExecutableInstruction.SLoad(dest, count, base, offset)) =>
          val address = pair(scalar, base) + offset.toLong
          readBytes(address, count * 4, kernarg, dispatchBytes, allocations, memory).map { bytes =>
            (0 until count).foreach(register => scalar(dest + register) = unsignedInt(bytes, register * 4))
            Some(index + 1)
          }
        case Some(ExecutableInstruction.SMove(dest, src)) =>
          scalar(dest) = Bits.low32(source(src, 0))
          Right(Some(index + 1))
        case Some(ExecutableInstruction.SMoveImmediate(dest, value)) =>
          scalar(dest) = Bits.unsigned32(value)
          Right(Some(index + 1))
        case Some(ExecutableInstruction.SMultiplyImmediate(dest, value)) =>
          val (result, overflow) = signedMultiplyImmediate(scalar(dest), value)
          scalar(dest) = result
          scalarCarry = overflow
          Right(Some(index + 1))
        case Some(ExecutableInstruction.SMove64(dest, src)) =>
          writeScalar64(dest, sourceScalar64(src))
          Right(Some(index + 1))
        case Some(ExecutableInstruction.SAndSaveExec(dest, src)) =>
          val previous = laneMask(active)
          val result   = previous & sourceScalar64(src)
          writeScalar64(dest, previous)
          writeScalar64(126, result)
          scalarCarry = result != 0L
          Right(Some(index + 1))
        case Some(ExecutableInstruction.SBinary(opcode, dest, srcA, srcB)) =>
          val left   = Bits.low32(source(srcA, 0))
          val right  = Bits.low32(source(srcB, 0))
          val result = opcode match {
            case "s_and_b32"    => left & right
            case "s_mul_i32"    => left * right
            case "s_mul_hi_u32" => unsignedMultiplyHigh32(left, right)
            case "s_add_i32"    =>
              val sum = Bits.low32(left + right)
              scalarCarry = ((~(left ^ right) & (left ^ sum)) & 0x80000000L) != 0L
              sum
            case "s_sub_i32" =>
              val difference = Bits.low32(left - right)
              scalarCarry = (((left ^ right) & (left ^ difference)) & 0x80000000L) != 0L
              difference
            case "s_lshl_b32" => left << (right.toInt & 31)
            case "s_lshr_b32" => left >>> (right.toInt & 31)
            case "s_min_u32"  =>
              val minimum = unsignedMin32(left, right)
              scalarCarry = minimum != 0L
              minimum
            case "s_add_u32" =>
              val sum = left + right
              scalarCarry = sum > Bits.Mask32
              sum
            case "s_addc_u32" =>
              val sum = left + right + (if (scalarCarry) 1L else 0L)
              scalarCarry = sum > Bits.Mask32
              sum
            case "s_sub_u32" =>
              scalarCarry = left >= right
              left - right
            case "s_subb_u32" =>
              val borrow = if (scalarCarry) 0L else 1L
              scalarCarry = left >= right + borrow
              left - right - borrow
          }
          scalar(dest) = Bits.low32(result)
          Right(Some(index + 1))
        case Some(ExecutableInstruction.SBinary64(opcode, dest, srcA, srcB)) =>
          val left   = sourceScalar64(srcA)
          val right  = sourceScalar64(srcB)
          val result = opcode match {
            case "s_and_b64"   => left & right
            case "s_andn2_b64" => left & ~right
            case "s_or_b64"    => left | right
            case "s_lshl_b64"  => left << (right.toInt & 63)
            case "s_lshr_b64"  => left >>> (right.toInt & 63)
          }
          writeScalar64(dest, result)
          scalarCarry = result != 0L
          Right(Some(index + 1))
        case Some(ExecutableInstruction.SCSelect(dest, srcA, srcB, width)) =>
          val selected = if (scalarCarry) srcA else srcB
          if (width == 64) writeScalar64(dest, sourceScalar64(selected))
          else scalar(dest) = Bits.low32(source(selected, 0))
          Right(Some(index + 1))
        case Some(ExecutableInstruction.SCmpEqI32(src, immediate)) =>
          scalarCarry = scalar(src).toInt == immediate
          Right(Some(index + 1))
        case Some(ExecutableInstruction.SCmp(opcode, srcA, srcB)) =>
          val left32  = Bits.low32(source(srcA, 0))
          val right32 = Bits.low32(source(srcB, 0))
          scalarCarry = opcode match {
            case "s_cmp_eq_u32" => left32 == right32
            case "s_cmp_gt_i32" => left32.toInt > right32.toInt
            case "s_cmp_gt_u32" => left32 > right32
            case "s_cmp_lg_u32" => left32 != right32
            case "s_cmp_lt_i32" => left32.toInt < right32.toInt
            case "s_cmp_eq_u64" => sourceScalar64(srcA) == sourceScalar64(srcB)
            case "s_cmp_lg_u64" => sourceScalar64(srcA) != sourceScalar64(srcB)
          }
          Right(Some(index + 1))
        case Some(ExecutableInstruction.VMove(dest, src)) =>
          foreachActive(lane => vector(dest)(lane) = Bits.low32(source(src, lane)))
          Right(Some(index + 1))
        case Some(ExecutableInstruction.VBitReverse(dest, src)) =>
          foreachActive(lane => vector(dest)(lane) = bitReverse32(source(src, lane)))
          Right(Some(index + 1))
        case Some(ExecutableInstruction.VMoveDPP(dest, src, control, rowMask, bankMask, boundControl, fetchInactive)) =>
          val sourceValues = vector(src).clone()
          foreachActive { lane =>
            if (dppLaneEnabled(lane, rowMask, bankMask)) {
              dppSourceLane(lane, control) match {
                case Some(sourceLane) if sourceLane < lanes =>
                  vector(dest)(lane) =
                    if (fetchInactive || active(sourceLane)) Bits.low32(sourceValues(sourceLane)) else 0L
                case _ if boundControl => vector(dest)(lane) = 0L
                case _                 => ()
              }
            }
          }
          Right(Some(index + 1))
        case Some(ExecutableInstruction.VCndMask(dest, srcA, srcB, condition)) =>
          val conditionMask = sourceScalar64(condition)
          foreachActive { lane =>
            val selected = if ((conditionMask & (1L << lane)) != 0L) srcB else srcA
            vector(dest)(lane) = Bits.low32(source(selected, lane))
          }
          Right(Some(index + 1))
        case Some(ExecutableInstruction.VReadFirstLane(dest, src)) =>
          active.indices.find(active).toRight(ExecutionFailure.Failed("v_readfirstlane_b32 has an empty EXEC mask"))
            .map { lane =>
              scalar(dest) = Bits.low32(source(src, lane))
              Some(index + 1)
            }
        case Some(ExecutableInstruction.VMaskBitCount(dest, mask, base, high)) =>
          val executionMask = laneMask(active)
          foreachActive { lane =>
            vector(dest)(lane) = mbCount(source(mask, lane), executionMask, lane, high, source(base, lane))
          }
          Right(Some(index + 1))
        case Some(ExecutableInstruction.VBinary(opcode, dest, srcA, srcB)) =>
          foreachActive { lane =>
            val left  = Bits.low32(source(srcA, lane))
            val right = Bits.low32(source(srcB, lane))
            opcode match {
              case "v_add_u32"     => vector(dest)(lane) = Bits.low32(left + right)
              case "v_mul_lo_u32"  => vector(dest)(lane) = Bits.low32(left * right)
              case "v_mul_hi_u32"  => vector(dest)(lane) = unsignedMultiplyHigh32(left, right)
              case "v_ashrrev_i32" =>
                vector(dest)(lane) = Bits.unsigned32(right.toInt >> (left.toInt & 31))
              case "v_lshlrev_b32" => vector(dest)(lane) = Bits.low32(right << (left.toInt & 31))
              case "v_lshrrev_b32" => vector(dest)(lane) = right >>> (left.toInt & 31)
              case "v_add_u16"     => vector(dest)(lane) = Bits.low16(left + right)
              case "v_and_b32"     => vector(dest)(lane) = left & right
              case "v_or_b32"      => vector(dest)(lane) = left | right
              case "v_lshlrev_b16" => vector(dest)(lane) = Bits.low16(Bits.low16(right) << (left.toInt & 15))
              case "v_sub_u16"     => vector(dest)(lane) = Bits.low16(left - right)
              case "v_sub_u32"     => vector(dest)(lane) = Bits.low32(left - right)
              case "v_subrev_u32"  => vector(dest)(lane) = unsignedSubtractReverse32(left, right)
              case "v_mul_u32_u24" => vector(dest)(lane) = unsignedMultiply24(left, right)
              case "v_mul_i32_i24" => vector(dest)(lane) = signedMultiply24(left, right)
              case "v_add_f32"     =>
                val result = IEEE754.binary32ToFloat(left) + IEEE754.binary32ToFloat(right)
                vector(dest)(lane) = IEEE754.floatToBinary32(result)
              case "v_sub_f32" =>
                val result = IEEE754.binary32ToFloat(left) - IEEE754.binary32ToFloat(right)
                vector(dest)(lane) = IEEE754.floatToBinary32(result)
              case "v_add_co_u32" =>
                val sum = left + right
                vector(dest)(lane) = Bits.low32(sum)
                carry(lane) = sum > Bits.Mask32
              case "v_addc_co_u32" =>
                val sum = left + right + (if (carry(lane)) 1L else 0L)
                vector(dest)(lane) = Bits.low32(sum)
                carry(lane) = sum > Bits.Mask32
              case "v_sub_co_u32" =>
                val (difference, noBorrow) = unsignedSubtract32(left, right)
                vector(dest)(lane) = difference
                carry(lane) = noBorrow
              case "v_subb_co_u32" =>
                val (difference, noBorrow) = unsignedSubtractWithBorrow32(left, right, carry(lane))
                vector(dest)(lane) = difference
                carry(lane) = noBorrow
            }
          }
          Right(Some(index + 1))
        case Some(ExecutableInstruction.VBinarySubDword(
              opcode,
              dest,
              srcA,
              srcB,
              destSelection,
              srcASelection,
              srcBSelection
            )) =>
          foreachActive { lane =>
            val left   = selectSubDword(source(srcA, lane), srcASelection)
            val right  = selectSubDword(source(srcB, lane), srcBSelection)
            val result = opcode match {
              case "v_add_u16"     => Bits.low16(left + right)
              case "v_and_b32"     => left & right
              case "v_lshlrev_b16" => Bits.low16(Bits.low16(right) << (left.toInt & 15))
              case "v_lshlrev_b32" => Bits.low32(right << (left.toInt & 31))
              case "v_lshrrev_b32" => right >>> (left.toInt & 31)
              case "v_or_b32"      => left | right
              case "v_sub_u16"     => Bits.low16(left - right)
            }
            vector(dest)(lane) = placeSubDword(result, destSelection)
          }
          Right(Some(index + 1))
        case Some(ExecutableInstruction.VPackedBinary(opcode, dest, srcA, srcB, srcAHighForHigh, srcBHighForHigh)) =>
          foreachActive { lane =>
            val left                              = Bits.low32(source(srcA, lane))
            val right                             = Bits.low32(source(srcB, lane))
            def calculate(a: Long, b: Long): Long = opcode match {
              case "v_pk_add_u16" => Bits.low16(a + b)
              case "v_pk_sub_i16" => Bits.low16(a - b)
            }
            val low  = calculate(Bits.low16(left), Bits.low16(right))
            val high = calculate(
              if (srcAHighForHigh) left >>> 16 else Bits.low16(left),
              if (srcBHighForHigh) right >>> 16 else Bits.low16(right)
            )
            vector(dest)(lane) = low | (high << 16)
          }
          Right(Some(index + 1))
        case Some(ExecutableInstruction.VAddCarry(dest, carryDest, srcA, srcB, carryIn)) =>
          val outputCarry = Array.fill(lanes)(false)
          foreachActive { lane =>
            val left     = Bits.low32(source(srcA, lane))
            val right    = Bits.low32(source(srcB, lane))
            val carryBit = carryIn.exists(mask => (sourceScalar64(mask) & (1L << lane)) != 0L)
            val sum      = left + right + (if (carryBit) 1L else 0L)
            vector(dest)(lane) = Bits.low32(sum)
            outputCarry(lane) = sum > Bits.Mask32
          }
          writeScalar64(carryDest, laneMask(outputCarry))
          Right(Some(index + 1))
        case Some(ExecutableInstruction.VTernary(opcode, dest, srcA, srcB, srcC)) =>
          foreachActive { lane =>
            val left   = Bits.low32(source(srcA, lane))
            val right  = Bits.low32(source(srcB, lane))
            val addend = Bits.low32(source(srcC, lane))
            vector(dest)(lane) = Bits.low32(
              opcode match {
                case "v_add3_u32"       => left + right + addend
                case "v_mad_legacy_u16" => Bits.low16(left) * Bits.low16(right) + Bits.low16(addend)
                case "v_mad_u32_u24"    => unsignedMultiplyAdd24(left, right, addend)
                case "v_alignbit_b32"   => Bits.join32(right, left) >>> (addend.toInt & 31)
                case "v_bfi_b32"        => (left & right) | (~left & addend)
                case "v_add_lshl_u32"   => (left + right) << (addend.toInt & 31)
                case "v_lshl_add_u32"   => (left << (right.toInt & 31)) + addend
                case "v_lshl_or_b32"    => (left << (right.toInt & 31)) | addend
                case "v_perm_b32"       => permuteBytes(left, right, addend)
              }
            )
          }
          Right(Some(index + 1))
        case Some(ExecutableInstruction.VMadU64U32(dest, carryDest, srcA, srcB, srcC)) =>
          val madCarry = Array.fill(lanes)(false)
          foreachActive { lane =>
            val product = Bits.low32(source(srcA, lane)) * Bits.low32(source(srcB, lane))
            val addend  = source64(srcC, lane, scalar, vector)
            val sum     = product + addend
            vector(dest)(lane) = Bits.low32(sum)
            vector(dest + 1)(lane) = Bits.high32(sum)
            madCarry(lane) = java.lang.Long.compareUnsigned(sum, product) < 0
          }
          writeScalar64(carryDest, laneMask(madCarry))
          Right(Some(index + 1))
        case Some(ExecutableInstruction.VCompare(opcode, srcA, srcB)) =>
          foreachLane { lane =>
            if (!active(lane)) carry(lane) = false
            else {
              val leftLow  = Bits.low32(source(srcA, lane))
              val rightLow = Bits.low32(source(srcB, lane))
              def left64   = source64(srcA, lane, scalar, vector)
              def right64  = source64(srcB, lane, scalar, vector)
              carry(lane) = opcode match {
                case "v_cmp_eq_u32" => leftLow == rightLow
                case "v_cmp_eq_u64" => left64 == right64
                case "v_cmp_lg_u16" => Bits.low16(leftLow) != Bits.low16(rightLow)
                case "v_cmp_lg_u32" => leftLow != rightLow
                case "v_cmp_lg_u64" => left64 != right64
                case "v_cmp_ne_u32" => leftLow != rightLow
                case "v_cmp_ne_u64" => left64 != right64
                case "v_cmp_ge_u32" => leftLow >= rightLow
                case "v_cmp_ge_u64" => java.lang.Long.compareUnsigned(left64, right64) >= 0
                case "v_cmp_gt_i32" => leftLow.toInt > rightLow.toInt
                case "v_cmp_gt_i64" => left64 > right64
                case "v_cmp_gt_u32" => leftLow > rightLow
                case "v_cmp_gt_u64" => java.lang.Long.compareUnsigned(left64, right64) > 0
                case "v_cmp_le_i32" => leftLow.toInt <= rightLow.toInt
                case "v_cmp_le_u32" => leftLow <= rightLow
                case "v_cmp_le_u64" => java.lang.Long.compareUnsigned(left64, right64) <= 0
                case "v_cmp_lt_i32" => leftLow.toInt < rightLow.toInt
                case "v_cmp_lt_i64" => left64 < right64
                case "v_cmp_lt_u32" => leftLow < rightLow
                case "v_cmp_lt_u64" => java.lang.Long.compareUnsigned(left64, right64) < 0
              }
            }
          }
          Right(Some(index + 1))
        case Some(ExecutableInstruction.VCompareTo(dest, opcode, srcA, srcB)) =>
          val result = Array.fill(lanes)(false)
          foreachActive { lane =>
            result(lane) = opcode match {
              case "v_cmp_eq_u32" =>
                Bits.low32(source(srcA, lane)) == Bits.low32(source(srcB, lane))
              case "v_cmp_gt_u32" =>
                Bits.low32(source(srcA, lane)) > Bits.low32(source(srcB, lane))
              case "v_cmp_ne_u32" =>
                Bits.low32(source(srcA, lane)) != Bits.low32(source(srcB, lane))
              case "v_cmp_lt_i64" =>
                source64(srcA, lane, scalar, vector) < source64(srcB, lane, scalar, vector)
              case "v_cmp_le_u64" =>
                java.lang.Long.compareUnsigned(
                  source64(srcA, lane, scalar, vector),
                  source64(srcB, lane, scalar, vector)
                ) <= 0
              case "v_cmp_ge_u64" =>
                java.lang.Long.compareUnsigned(
                  source64(srcA, lane, scalar, vector),
                  source64(srcB, lane, scalar, vector)
                ) >= 0
              case "v_cmp_lt_u64" =>
                java.lang.Long.compareUnsigned(
                  source64(srcA, lane, scalar, vector),
                  source64(srcB, lane, scalar, vector)
                ) < 0
            }
          }
          writeScalar64(dest, laneMask(result))
          Right(Some(index + 1))
        case Some(ExecutableInstruction.VShiftLeft64(dest, amount, src)) =>
          foreachActive { lane =>
            val sourceLow  = Bits.low32(source(src, lane))
            val sourceHigh = src match {
              case InstructionSource.VectorRegister(register) => Bits.low32(vector(register + 1)(lane))
              case InstructionSource.Scalar(register)         => Bits.low32(scalar(register + 1))
              case InstructionSource.Constant(_)              => 0L
            }
            val shifted = Bits.join32(sourceLow, sourceHigh) << (source(amount, lane).toInt & 63)
            vector(dest)(lane) = Bits.low32(shifted)
            vector(dest + 1)(lane) = Bits.high32(shifted)
          }
          Right(Some(index + 1))
        case Some(ExecutableInstruction.VFloat64Add(dest, srcA, srcB, negateB)) =>
          foreachActive { lane =>
            val left   = IEEE754.binary64ToDouble(source64(srcA, lane, scalar, vector))
            val rawB   = IEEE754.binary64ToDouble(source64(srcB, lane, scalar, vector))
            val right  = if (negateB) -rawB else rawB
            val result = IEEE754.doubleToBinary64(left + right)
            vector(dest)(lane) = Bits.low32(result)
            vector(dest + 1)(lane) = Bits.high32(result)
          }
          Right(Some(index + 1))
        case Some(ExecutableInstruction.DSRead(dest, addressRegister, byteCount, offsetA, offsetB)) =>
          def loadLane(lane: Int): Either[ExecutionFailure, Unit] =
            if (lane == lanes) Right(())
            else if (!active(lane)) loadLane(lane + 1)
            else {
              val base = Bits.low32(vector(addressRegister)(lane))
              for {
                first  <- readLDS(lds, base + offsetA.toLong, byteCount)
                second <- offsetB.traverse(offset => readLDS(lds, base + offset.toLong, byteCount))
                _ = writeVectorBytes(vector, dest, lane, first)
                _ = second.foreach(bytes => writeVectorBytes(vector, dest + (byteCount + 3) / 4, lane, bytes))
                _ <- loadLane(lane + 1)
              } yield ()
            }
          loadLane(0).map(_ => Some(index + 1))
        case Some(ExecutableInstruction.DSBPermute(dest, addressRegister, src)) =>
          val addresses = vector(addressRegister).clone()
          val sources   = vector(src).clone()
          foreachActive { lane =>
            val sourceLane = bpermuteSourceLane(addresses(lane))
            vector(dest)(lane) = if (sourceLane < lanes) Bits.low32(sources(sourceLane)) else 0L
          }
          Right(Some(index + 1))
        case Some(ExecutableInstruction.DSWrite(addressRegister, srcA, srcB, byteCount, offsetA, offsetB)) =>
          def storeLane(lane: Int): Either[ExecutionFailure, Unit] =
            if (lane == lanes) Right(())
            else if (!active(lane)) storeLane(lane + 1)
            else {
              val base = Bits.low32(vector(addressRegister)(lane))
              for {
                _ <- writeLDS(lds, base + offsetA.toLong, vectorBytes(vector, srcA, lane, byteCount))
                _ <- (srcB, offsetB) match {
                  case (Some(register), Some(offset)) =>
                    writeLDS(lds, base + offset.toLong, vectorBytes(vector, register, lane, byteCount))
                  case (None, None) => Right(())
                  case _            => Left(ExecutionFailure.Failed("malformed paired DS write"))
                }
                _ <- storeLane(lane + 1)
              } yield ()
            }
          storeLane(0).map(_ => Some(index + 1))
        case Some(ExecutableInstruction.DSWriteD16High(addressRegister, src, offset)) =>
          def storeLane(lane: Int): Either[ExecutionFailure, Unit] =
            if (lane == lanes) Right(())
            else if (!active(lane)) storeLane(lane + 1)
            else {
              val base  = Bits.low32(vector(addressRegister)(lane))
              val value = vector(src)(lane) >>> 16
              writeLDS(lds, base + offset.toLong, LittleEndian.toArray(value, 2)).flatMap(_ => storeLane(lane + 1))
            }
          storeLane(0).map(_ => Some(index + 1))
        case Some(ExecutableInstruction.GlobalLoad(dest, addressRegister, scalarAddress, byteCount, offset)) =>
          def loadLane(lane: Int): Either[ExecutionFailure, Unit] =
            if (lane == lanes) Right(())
            else if (!active(lane)) loadLane(lane + 1)
            else {
              val address = scalarAddress
                .map(register => pair(scalar, register) + Bits.low32(vector(addressRegister)(lane)))
                .getOrElse(pair(vector, addressRegister, lane)) + signed13(offset)
              readBytes(address, byteCount, kernarg, dispatchBytes, allocations, memory).flatMap { bytes =>
                writeVectorBytes(vector, dest, lane, bytes)
                loadLane(lane + 1)
              }
            }
          loadLane(0).map(_ => Some(index + 1))
        case Some(ExecutableInstruction.GlobalLoadD16(dest, addressRegister, scalarAddress, offset, high)) =>
          def loadLane(lane: Int): Either[ExecutionFailure, Unit] =
            if (lane == lanes) Right(())
            else if (!active(lane)) loadLane(lane + 1)
            else {
              val address = scalarAddress
                .map(register => pair(scalar, register) + Bits.low32(vector(addressRegister)(lane)))
                .getOrElse(pair(vector, addressRegister, lane)) + signed13(offset)
              readBytes(address, 2, kernarg, dispatchBytes, allocations, memory).flatMap { bytes =>
                val loaded = LittleEndian.decode(bytes, 0, 2)
                vector(dest)(lane) =
                  if (high) (vector(dest)(lane) & 0x0000ffffL) | (loaded << 16)
                  else (vector(dest)(lane) & 0xffff0000L) | loaded
                loadLane(lane + 1)
              }
            }
          loadLane(0).map(_ => Some(index + 1))
        case Some(ExecutableInstruction.GlobalStore(addressRegister, scalarAddress, src, byteCount, offset)) =>
          def storeLane(lane: Int): Either[ExecutionFailure, Unit] =
            if (lane == lanes) Right(())
            else if (!active(lane)) storeLane(lane + 1)
            else {
              val address = scalarAddress
                .map(register => pair(scalar, register) + Bits.low32(vector(addressRegister)(lane)))
                .getOrElse(pair(vector, addressRegister, lane)) + signed13(offset)
              val bytes = vectorBytes(vector, src, lane, byteCount)
              writeBytes(address, bytes, allocations, memory).flatMap(_ => storeLane(lane + 1))
            }
          storeLane(0).map(_ => Some(index + 1))
        case Some(ExecutableInstruction.GlobalStoreD16High(addressRegister, scalarAddress, src, byteCount, offset)) =>
          def storeLane(lane: Int): Either[ExecutionFailure, Unit] =
            if (lane == lanes) Right(())
            else if (!active(lane)) storeLane(lane + 1)
            else {
              val address = scalarAddress
                .map(register => pair(scalar, register) + Bits.low32(vector(addressRegister)(lane)))
                .getOrElse(pair(vector, addressRegister, lane)) + signed13(offset)
              val bytes = LittleEndian.toArray(vector(src)(lane) >>> 16, byteCount)
              writeBytes(address, bytes, allocations, memory).flatMap(_ => storeLane(lane + 1))
            }
          storeLane(0).map(_ => Some(index + 1))
        case Some(ExecutableInstruction.FlatLoad(dest, addressRegister, byteCount, offset)) =>
          def loadLane(lane: Int): Either[ExecutionFailure, Unit] =
            if (lane == lanes) Right(())
            else if (!active(lane)) loadLane(lane + 1)
            else {
              val address = pair(vector, addressRegister, lane) + signed13(offset)
              readBytes(address, byteCount, kernarg, dispatchBytes, allocations, memory).flatMap { bytes =>
                writeVectorBytes(vector, dest, lane, bytes)
                loadLane(lane + 1)
              }
            }
          loadLane(0).map(_ => Some(index + 1))
        case Some(ExecutableInstruction.FlatStore(addressRegister, src, byteCount, offset)) =>
          def storeLane(lane: Int): Either[ExecutionFailure, Unit] =
            if (lane == lanes) Right(())
            else if (!active(lane)) storeLane(lane + 1)
            else {
              val address = pair(vector, addressRegister, lane) + signed13(offset)
              val bytes   = vectorBytes(vector, src, lane, byteCount)
              writeBytes(address, bytes, allocations, memory).flatMap(_ => storeLane(lane + 1))
            }
          storeLane(0).map(_ => Some(index + 1))
      }

    var instructionIndex                  = initial.fold(0)(_.nextInstruction)
    var running                           = true
    var reachedBarrier                    = false
    var failure: Option[ExecutionFailure] = None
    while (running && failure.isEmpty)
      control.checkpoint(math.max(1, active.count(identity))) match {
        case Left(fault) => failure = Some(ExecutionFailure.Failed(fault.message))
        case Right(())   => step(instructionIndex) match {
            case Left(error)     => failure = Some(error)
            case Right(None)     => running = false
            case Right(Some(-1)) =>
              reachedBarrier = true
              running = false
              instructionIndex += 1
            case Right(Some(index)) => instructionIndex = index
          }
      }
    failure match {
      case Some(error)            => Left(error)
      case None if reachedBarrier =>
        Right(WaveBoundary.Barrier(
          WaveState(scalar, vector, carry, active, scalarCarry, instructionIndex)
        ))
      case None => Right(WaveBoundary.End)
    }
  }

  private def readBytes(
      address: Long,
      size: Int,
      kernarg: Array[Byte],
      dispatch: Array[Byte],
      allocations: Col[AllocationRegion],
      memory: ExecutionMemory
  ): Either[ExecutionFailure, Array[Byte]] =
    localRead(address, size, KernargAddress, kernarg)
      .orElse(localRead(address, size, DispatchAddress, dispatch))
      .map(Right.apply)
      .getOrElse(resolve(address, allocations).flatMap { case (region, relative) =>
        memory.read(region.id, region.offset + relative, size).map(Col.toArray)
      })

  private def writeBytes(
      address: Long,
      bytes: Array[Byte],
      allocations: Col[AllocationRegion],
      memory: ExecutionMemory
  ): Either[ExecutionFailure, Unit] =
    resolve(address, allocations).flatMap { case (region, relative) =>
      memory.write(region.id, region.offset + relative, Col.unsafeFromArray(bytes))
    }

  private def resolve(
      address: Long,
      allocations: Col[AllocationRegion]
  ): Either[ExecutionFailure, (AllocationRegion, Long)] =
    allocations.find(region => address >= region.base && address - region.base < AllocationSpan)
      .map(region => region -> (address - region.base))
      .toRight(ExecutionFailure.Failed(f"unmapped AMDGPU address 0x$address%016x"))

  private def localRead(address: Long, size: Int, base: Long, bytes: Array[Byte]): Option[Array[Byte]] = {
    val offset = address - base
    Option.when(ByteRange.contains(bytes.length, offset, size.toLong))(
      java.util.Arrays.copyOfRange(bytes, offset.toInt, offset.toInt + size)
    )
  }

  private def requireBytes(bytes: Array[Byte], offset: Int, size: Int): Either[String, Unit] =
    Either.cond(
      ByteRange.contains(bytes.length, offset.toLong, size.toLong),
      (),
      s"truncated $size-byte instruction"
    )

  private def unsignedInt(bytes: Array[Byte], offset: Int): Long = LittleEndian.decode(bytes, offset, 4)

  private def pair(registers: Array[Long], first: Int): Long =
    Bits.join32(registers(first), registers(first + 1))

  private def pair(registers: VectorRegisters, first: Int, lane: Int): Long =
    Bits.join32(registers(first)(lane), registers(first + 1)(lane))

  private def setPair(registers: Array[Long], first: Int, value: Long): Unit = {
    registers(first) = Bits.low32(value)
    registers(first + 1) = Bits.high32(value)
  }

  private def selectSubDword(value: Long, selection: SubDwordSelection): Long = {
    val mask = Bits.mask(selection.width)
    (value >>> selection.shift) & mask
  }

  private def placeSubDword(value: Long, selection: SubDwordSelection): Long = {
    val mask = Bits.mask(selection.width)
    Bits.low32((value & mask) << selection.shift)
  }

  private[amdgpu] def permuteBytes(srcA: Long, srcB: Long, selectors: Long): Long = {
    val sources = Bits.join32(srcB, srcA)
    (0 until 4).foldLeft(0L) { (result, byte) =>
      val selector = ((selectors >>> (byte * 8)) & 0x7L).toInt
      result | (((sources >>> (selector * 8)) & 0xffL) << (byte * 8))
    }
  }

  private def readLDS(lds: Array[Byte], address: Long, size: Int): Either[ExecutionFailure, Array[Byte]] =
    Either.cond(
      ByteRange.contains(lds.length, address, size.toLong),
      java.util.Arrays.copyOfRange(lds, address.toInt, address.toInt + size),
      ExecutionFailure.Failed(f"out-of-bounds AMDGPU LDS read at 0x$address%x ($size bytes)")
    )

  private def writeLDS(lds: Array[Byte], address: Long, bytes: Array[Byte]): Either[ExecutionFailure, Unit] =
    Either.cond(
      ByteRange.contains(lds.length, address, bytes.length.toLong), {
        System.arraycopy(bytes, 0, lds, address.toInt, bytes.length)
        ()
      },
      ExecutionFailure.Failed(f"out-of-bounds AMDGPU LDS write at 0x$address%x (${bytes.length} bytes)")
    )

  private def vectorBytes(
      registers: VectorRegisters,
      first: Int,
      lane: Int,
      size: Int
  ): Array[Byte] =
    Array.tabulate(size) { byte =>
      val register = first + byte / 4
      ((registers(register)(lane) >>> ((byte % 4) * 8)) & 0xffL).toByte
    }

  private def writeVectorBytes(
      registers: VectorRegisters,
      first: Int,
      lane: Int,
      bytes: Array[Byte]
  ): Unit = {
    var byte = 0
    while (byte < bytes.length) {
      val register = first + byte / 4
      val shift    = (byte % 4) * 8
      if (shift == 0) registers(register)(lane) = 0L
      registers(register)(lane) |= (bytes(byte).toLong & 0xffL) << shift
      byte += 1
    }
  }

  private def source64(
      source: InstructionSource,
      lane: Int,
      scalar: Array[Long],
      vector: VectorRegisters
  ): Long = source match {
    case InstructionSource.Scalar(index)         => pair(scalar, index)
    case InstructionSource.VectorRegister(index) => pair(vector, index, lane)
    case InstructionSource.Constant(raw)         => raw
  }

  private def signed13(value: Int): Long = Bits.signExtend(value.toLong, 13)
}
