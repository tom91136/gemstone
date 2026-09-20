package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.binary.{Bits, ByteRange, ScalarByteWidth}
import net.kurobako.gemstone.binary.IEEE754
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.execution.*

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executors
import scala.collection.mutable

private trait CodeObjectTestSupport { self: munit.FunSuite =>
  protected final def testDimensions(x: Int, y: Int = 1, z: Int = 1): Dimensions =
    Dimensions(
      x.refineEither[Positive].fold(message => throw IllegalArgumentException(message), value => value),
      y.refineEither[Positive].fold(message => throw IllegalArgumentException(message), value => value),
      z.refineEither[Positive].fold(message => throw IllegalArgumentException(message), value => value)
    )

  // Mutable allocations are confined to this final in-memory execution fixture.
  protected final class TestMemory extends ExecutionMemory {
    private val allocations = mutable.Map.empty[AllocationId, Array[Byte]]

    def allocate(id: AllocationId, size: Int): Unit = allocations(id) = Array.ofDim[Byte](size)

    def putInt(id: AllocationId, offset: Int :| Positive0, value: Int): Unit =
      LittleEndian.write(value.toLong, allocations(id), offset, ScalarByteWidth(4))

    def getInt(id: AllocationId, offset: Int :| Positive0): Int =
      LittleEndian.decode(allocations(id), offset, ScalarByteWidth(4)).toInt

    override def read(
        id: AllocationId,
        offset: Long :| Positive0,
        size: Int :| Positive0
    ): Either[ExecutionFailure, Vec[Byte]] =
      allocations.get(id).toRight(ExecutionFailure.NotFound("test allocation")).flatMap { bytes =>
        Either.cond(
          ByteRange.contains(bytes.length, offset, size.toLong),
          Vec.unsafeFromArray(java.util.Arrays.copyOfRange(bytes, offset.toInt, offset.toInt + size)),
          ExecutionFailure.InvalidRequest("test read range")
        )
      }

    override def write(
        id: AllocationId,
        offset: Long :| Positive0,
        bytes: Vec[Byte]
    ): Either[ExecutionFailure, Unit] =
      allocations.get(id).toRight(ExecutionFailure.NotFound("test allocation")).flatMap { destination =>
        Either.cond(
          ByteRange.contains(destination.length, offset, bytes.length.toLong), {
            val copied = bytes.copyToArray(destination, offset.toInt, bytes.length)
            assertEquals(copied, bytes.length)
            ()
          },
          ExecutionFailure.InvalidRequest("test write range")
        )
      }

    override def recordPointerRelocation(relocation: PointerRelocation): Unit = ()
  }

  protected def minimalCodeObject(globalPointer: Boolean = false): Array[Byte] = {
    def aligned(value: Int): Int = (value + 7) & ~7
    val sectionNames             =
      (if (globalPointer)
         "\u0000.shstrtab\u0000.strtab\u0000.symtab\u0000.text\u0000.rodata\u0000.bss\u0000.got\u0000.rela.dyn\u0000"
       else "\u0000.shstrtab\u0000.strtab\u0000.symtab\u0000.text\u0000.rodata\u0000.bss\u0000")
        .getBytes(StandardCharsets.US_ASCII)
    val symbolNames       = "\u0000kernel\u0000kernel.kd\u0000device_global\u0000".getBytes(StandardCharsets.US_ASCII)
    val target            = "amdgcn-amd-amdhsa--gfx900\u0000".getBytes(StandardCharsets.US_ASCII)
    val sectionNameOffset = 64
    val symbolNameOffset  = aligned(sectionNameOffset + sectionNames.length)
    val symbolTableOffset = aligned(symbolNameOffset + symbolNames.length)
    val textOffset        = aligned(symbolTableOffset + 4 * 24)
    val readOnlyOffset    = aligned(textOffset + 4)
    val readOnlySize      = 64 + target.length
    val sectionTable      = aligned(readOnlyOffset + readOnlySize)
    val sectionCount      = if (globalPointer) 9 else 7
    val gotOffset         = aligned(sectionTable + sectionCount * 64)
    val relocationOffset  = aligned(gotOffset + 8)
    val bytes             = Array.ofDim[Byte](if (globalPointer) relocationOffset + 24 else sectionTable + 7 * 64)
    val buffer            = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)

    buffer.put(0, 0x7f.toByte)
    buffer.put(1, 'E'.toByte)
    buffer.put(2, 'L'.toByte)
    buffer.put(3, 'F'.toByte)
    buffer.put(4, 2.toByte)
    buffer.put(5, 1.toByte)
    buffer.putShort(16, 3.toShort)
    buffer.putShort(18, 224.toShort)
    buffer.putInt(20, 1)
    buffer.putLong(40, sectionTable.toLong)
    buffer.putShort(52, 64.toShort)
    buffer.putShort(58, 64.toShort)
    buffer.putShort(60, sectionCount.toShort)
    buffer.putShort(62, 1.toShort)
    Array.copy(sectionNames, 0, bytes, sectionNameOffset, sectionNames.length)
    Array.copy(symbolNames, 0, bytes, symbolNameOffset, symbolNames.length)
    val textByteOffset     = textOffset.refineEither[Positive0].fold(fail(_), value => value)
    val readOnlyByteOffset = readOnlyOffset.refineEither[Positive0].fold(fail(_), value => value)
    LittleEndian.write(0xbf810000L, bytes, textByteOffset, ScalarByteWidth(4))
    LittleEndian.write(16L, bytes, readOnlyByteOffset, ScalarByteWidth(4))
    LittleEndian.write(8L, bytes, readOnlyByteOffset + 4, ScalarByteWidth(4))
    LittleEndian.write(24L, bytes, readOnlyByteOffset + 8, ScalarByteWidth(4))
    LittleEndian.write(0x80L, bytes, readOnlyByteOffset + 52, ScalarByteWidth(4))
    LittleEndian.write(0x0aL, bytes, readOnlyByteOffset + 56, ScalarByteWidth(2))
    Array.copy(target, 0, bytes, readOnlyOffset + 64, target.length)

    def section(
        index: Int,
        name: Int,
        kind: Int,
        address: Long,
        offset: Int,
        size: Int,
        link: Int = 0,
        alignment: Int = 1,
        entrySize: Int = 0
    ): Unit = {
      val start = sectionTable + index * 64
      buffer.putInt(start, name)
      buffer.putInt(start + 4, kind)
      buffer.putLong(start + 16, address)
      buffer.putLong(start + 24, offset.toLong)
      buffer.putLong(start + 32, size.toLong)
      buffer.putInt(start + 40, link)
      buffer.putLong(start + 48, alignment.toLong)
      val _ = buffer.putLong(start + 56, entrySize.toLong)
    }
    section(1, 1, 3, 0L, sectionNameOffset, sectionNames.length)
    section(2, 11, 3, 0L, symbolNameOffset, symbolNames.length)
    section(3, 19, 2, 0L, symbolTableOffset, 4 * 24, link = 2, alignment = 8, entrySize = 24)
    section(4, 27, 1, 0x1000L, textOffset, 4, alignment = 4)
    section(5, 33, 1, 0x2000L, readOnlyOffset, readOnlySize, alignment = 8)
    section(6, 41, 8, 0x3000L, 0, 16, alignment = 8)

    def symbol(index: Int, name: Int, kind: Int, section: Int, value: Long, size: Long): Unit = {
      val start = symbolTableOffset + index * 24
      buffer.putInt(start, name)
      buffer.put(start + 4, kind.toByte)
      buffer.putShort(start + 6, section.toShort)
      buffer.putLong(start + 8, value)
      val _ = buffer.putLong(start + 16, size)
    }
    symbol(1, 1, 2, 4, 0x1000L, 4L)
    symbol(2, 8, 1, 5, 0x2000L, 64L)
    symbol(3, 18, 1, 6, 0x3000L, 16L)
    if (globalPointer) {
      section(7, 46, 1, 0x4000L, gotOffset, 8, alignment = 8)
      section(8, 51, 4, 0x5000L, relocationOffset, 24, link = 3, alignment = 8, entrySize = 24)
      // R_AMDGPU_ABS64 for device_global (symbol 3) stored into the .got slot.
      val relocationByteOffset = relocationOffset
      LittleEndian.write(0x4000L, bytes, relocationByteOffset, ScalarByteWidth(8))
      LittleEndian.write((3L << 32) | 3L, bytes, relocationByteOffset + 8, ScalarByteWidth(8))
      LittleEndian.write(0L, bytes, relocationByteOffset + 16, ScalarByteWidth(8))
    }
    bytes
  }
}
