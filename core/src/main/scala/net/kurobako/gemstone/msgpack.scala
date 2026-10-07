package net.kurobako.gemstone

import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.string.*

object msgpack {
  private val InitialCapacity = 256

  object Limits {
    val Unbounded: Limits = Limits(Int.MaxValue, Int.MaxValue, Int.MaxValue)
  }

  case class Limits(maximumElements: Int > 0, maximumBytes: Int > 0, maximumTextBytes: Int > 0)

  /** Accumulates the first failure; later writes are ignored. */
  class Writer(val limits: Limits) {
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var mutableBytes = Array.ofDim[Byte](math.min(InitialCapacity, limits.maximumBytes.raw))
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var length = 0
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var failure = Option.empty[String]

    private def emit(value: Int): Unit = {
      mutableBytes(length) = value.toByte
      length += 1
    }

    private def reserve(additional: Int): Boolean =
      if (failure.nonEmpty) false
      else if (additional < 0 || additional > limits.maximumBytes.raw - length) {
        reject(s"wire value exceeds the ${limits.maximumBytes}-byte limit")
        false
      } else {
        val required = length + additional
        if (required > mutableBytes.length) {
          val doubled = math.min(limits.maximumBytes.raw.toLong, math.max(1L, mutableBytes.length.toLong * 2L)).toInt
          mutableBytes = java.util.Arrays.copyOf(mutableBytes, math.max(required, doubled))
        }
        true
      }

    private def writeRawByte(value: Int): Unit =
      if (reserve(1)) emit(value)

    private def writeRawShort(value: Int): Unit =
      if (reserve(2)) {
        emit(value >>> 8)
        emit(value)
      }

    private def writeRawInt(value: Int): Unit =
      if (reserve(4)) {
        emit(value >>> 24)
        emit(value >>> 16)
        emit(value >>> 8)
        emit(value)
      }

    private def writeRawLong(value: Long): Unit = {
      writeRawInt((value >>> 32).toInt)
      writeRawInt(value.toInt)
    }

    private def writeRawBytes(value: Array[Byte]): Unit =
      if (reserve(value.length)) {
        Array.copy(value, 0, mutableBytes, length, value.length)
        length += value.length
      }

    def writeNil(): Unit = writeRawByte(0xc0)

    def writeBoolean(value: Boolean): Unit = writeRawByte(if (value) 0xc3 else 0xc2)

    def writeInt(value: Int): Unit =
      if (value >= 0 && value <= 0x7f) writeRawByte(value)
      else if (value < 0 && value >= -32) writeRawByte(value & 0xff)
      else if (value >= Byte.MinValue && value <= Byte.MaxValue) {
        writeRawByte(0xd0)
        writeRawByte(value)
      } else if (value >= Short.MinValue && value <= Short.MaxValue) {
        writeRawByte(0xd1)
        writeRawShort(value)
      } else {
        writeRawByte(0xd2)
        writeRawInt(value)
      }

    def writeLong(value: Long): Unit =
      if (value >= Int.MinValue && value <= Int.MaxValue) writeInt(value.toInt)
      else {
        writeRawByte(0xd3)
        writeRawLong(value)
      }

    def writeFloat(value: Float): Unit = {
      writeRawByte(0xca)
      writeRawInt(java.lang.Float.floatToRawIntBits(value))
    }

    def writeDouble(value: Double): Unit = {
      writeRawByte(0xcb)
      writeRawLong(java.lang.Double.doubleToRawLongBits(value))
    }

    private def writeSized(size: Int, marker16: Int, marker32: Int): Unit =
      if (size <= 0xffff) {
        writeRawByte(marker16)
        writeRawShort(size)
      } else {
        writeRawByte(marker32)
        writeRawInt(size)
      }

    private def writeSized8(size: Int, marker8: Int, marker16: Int, marker32: Int): Unit =
      if (size <= 0xff) {
        writeRawByte(marker8)
        writeRawByte(size)
      } else writeSized(size, marker16, marker32)

    def writeArrayHeader(size: Int >= 0): Unit =
      if (size.raw > limits.maximumElements.raw)
        reject(s"collection exceeds the ${limits.maximumElements}-element limit")
      else if (size.raw <= 15) writeRawByte(0x90 | size.raw)
      else writeSized(size.raw, 0xdc, 0xdd)

    def writeString(value: String): Unit = {
      val bytes = value.getBytes(StandardCharsets.UTF_8)
      if (bytes.length > limits.maximumTextBytes.raw)
        reject(s"UTF-8 value exceeds the ${limits.maximumTextBytes}-byte text limit")
      else {
        if (bytes.length <= 31) writeRawByte(0xa0 | bytes.length) else writeSized8(bytes.length, 0xd9, 0xda, 0xdb)
        writeRawBytes(bytes)
      }
    }

    def writeBytes(value: Array[Byte]): Unit =
      if (value.length > limits.maximumBytes.raw)
        reject(s"byte collection exceeds the ${limits.maximumBytes}-byte limit")
      else {
        writeSized8(value.length, 0xc4, 0xc5, 0xc6)
        writeRawBytes(value)
      }

    def reject(message: String): Unit = if (failure.isEmpty) failure = Some(message)

    def result(): Either[String, Array[Byte]] =
      failure.toLeft(java.util.Arrays.copyOf(mutableBytes, length))
  }

  class Reader(bytes: Array[Byte], start: Int, size: Int, limits: Limits = Limits.Unbounded) {
    require(ByteRange.contains(bytes.length, start.toLong, size.toLong), "invalid MessagePack input range")
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var mutableCursor = start

    def remaining: Int = start + size - mutableCursor

    private def take(size: Int): Either[String, Int] =
      if (size < 0 || size > remaining)
        Left(s"MessagePack value is truncated: required $size bytes, found $remaining")
      else {
        val offset = mutableCursor
        mutableCursor += size
        Right(offset)
      }

    private def peek(): Int =
      if (remaining > 0) bytes(mutableCursor) & 0xff else -1

    private def readRawByte(): Either[String, Int] =
      take(1).map(offset => bytes(offset) & 0xff)

    private def readRawInt(): Either[String, Int] =
      take(4).map { offset =>
        (bytes(offset) & 0xff) << 24 |
          (bytes(offset + 1) & 0xff) << 16 |
          (bytes(offset + 2) & 0xff) << 8 |
          (bytes(offset + 3) & 0xff)
      }

    private def readRawLong(): Either[String, Long] = for {
      high <- readRawInt()
      low  <- readRawInt()
    } yield (high.toLong << 32) | Integer.toUnsignedLong(low)

    private def readRawShort(): Either[String, Int] =
      take(2).map(offset => ((bytes(offset) & 0xff) << 8) | (bytes(offset + 1) & 0xff))

    def tryReadNil(): Either[String, Boolean] =
      if (peek() == 0xc0) {
        mutableCursor += 1
        Right(true)
      } else Right(false)

    def readBoolean(): Either[String, Boolean] = readRawByte().flatMap {
      case 0xc2  => Right(false)
      case 0xc3  => Right(true)
      case other => Left(f"expected boolean, found 0x$other%02x")
    }

    def readFloat(): Either[String, Float] = readRawByte().flatMap {
      case 0xca  => readRawInt().map(java.lang.Float.intBitsToFloat(_))
      case other => Left(f"expected float32, found 0x$other%02x")
    }
    def readDouble(): Either[String, Double] = readRawByte().flatMap {
      case 0xcb  => readRawLong().map(java.lang.Double.longBitsToDouble(_))
      case 0xca  => readRawInt().map(value => java.lang.Float.intBitsToFloat(value).toDouble)
      case other => Left(f"expected floating value, found 0x$other%02x")
    }

    def readLong(): Either[String, Long] = readRawByte().flatMap {
      case marker if marker <= 0x7f => Right(marker.toLong)
      case marker if marker >= 0xe0 => Right(marker.toByte.toLong)
      case 0xcc                     => readRawByte().map(_.toLong)
      case 0xcd                     => readRawShort().map(_.toLong)
      case 0xce                     => readRawInt().map(_.toLong & 0xffffffffL)
      case 0xcf                     => readRawLong().flatMap(value =>
          if (value < 0L) Left("uint64 value exceeds the signed range") else Right(value)
        )
      case 0xd0  => readRawByte().map(_.toByte.toLong)
      case 0xd1  => readRawShort().map(_.toShort.toLong)
      case 0xd2  => readRawInt().map(_.toLong)
      case 0xd3  => readRawLong()
      case other => Left(f"expected integer, found 0x$other%02x")
    }

    def readInt(): Either[String, Int] = readLong().flatMap { value =>
      if (value < Int.MinValue || value > Int.MaxValue) Left(s"integer out of Int range: $value")
      else Right(value.toInt)
    }

    def readString(strict: Boolean = false): Either[String, String] = readRawByte().flatMap {
      case marker if (marker & 0xe0) == 0xa0 => readStringBody(marker & 0x1f, strict)
      case 0xd9                              => readRawByte().flatMap(size => readStringBody(size, strict))
      case 0xda                              => readRawShort().flatMap(size => readStringBody(size, strict))
      case 0xdb                              => readRawInt().flatMap(size => readStringBody(size, strict))
      case other                             => Left(f"expected string, found 0x$other%02x")
    }

    private def readStringBody(size: Int, strict: Boolean): Either[String, String] =
      if (size < 0 || size > limits.maximumTextBytes.raw)
        Left(s"UTF-8 value exceeds the ${limits.maximumTextBytes}-byte text limit: $size")
      else take(size).flatMap(offset =>
        if (strict) decodeUtf8(bytes, offset, size).left.map(_ => "MessagePack string is not valid UTF-8")
        else Right(String(bytes, offset, size, StandardCharsets.UTF_8))
      )

    def readBytes(): Either[String, Array[Byte]] = readRawByte().flatMap {
      case 0xc4  => readRawByte().flatMap(size => readBytesBody(size))
      case 0xc5  => readRawShort().flatMap(size => readBytesBody(size))
      case 0xc6  => readRawInt().flatMap(size => readBytesBody(size))
      case other => Left(f"expected binary, found 0x$other%02x")
    }

    private def readBytesBody(size: Int): Either[String, Array[Byte]] =
      if (size < 0 || size > limits.maximumBytes.raw)
        Left(s"binary value exceeds the ${limits.maximumBytes}-byte limit: $size")
      else take(size).map(offset => java.util.Arrays.copyOfRange(bytes, offset, offset + size))

    def readArrayHeader(): Either[String, Int >= 0] = containerHeader(array = true)
    def readMapHeader(): Either[String, Int >= 0]   = containerHeader(array = false)

    private def containerHeader(array: Boolean): Either[String, Int >= 0] = readRawByte().flatMap {
      case tag if (tag & 0xf0) == (if (array) 0x90 else 0x80) => Right(tag & 0x0f)
      case tag if tag == (if (array) 0xdc else 0xde)          => readRawShort()
      case tag if tag == (if (array) 0xdd else 0xdf)          => readRawInt()
      case other => Left(f"expected ${if (array) "array" else "map"}, found 0x$other%02x")
    }.flatMap { count =>
      if (count > limits.maximumElements.raw)
        Left(s"collection exceeds the ${limits.maximumElements}-element limit: $count")
      else count.refine[Int >= 0].left.map(_ => s"MessagePack container size is out of range: $count")
    }

    /** Read a nonnegative Int using unsigned MessagePack encodings. */
    def readUnsignedInt(): Either[String, Int >= 0] = readRawByte().flatMap {
      case tag if tag <= 0x7f => Right(tag.toLong)
      case 0xcc               => readRawByte().map(_.toLong)
      case 0xcd               => readRawShort().map(_.toLong)
      case 0xce               => readRawInt().map(Integer.toUnsignedLong(_))
      case 0xcf               => readRawLong()
      case other              => Left(f"expected unsigned integer, found 0x$other%02x")
    }.flatMap(value =>
      value.refine[Long in 0L ~ 2147483647L].map(_.toIntExact.widen[Int >= 0]).left.map(_ =>
        "MessagePack integer exceeds supported range"
      )
    )

    def skip(maximumDepth: Int): Either[String, Unit] = {
      def children(count: Int, depth: Int): Either[String, Unit] =
        if (count > remaining) Left("truncated MessagePack container")
        else (0 until count).foldLeft[Either[String, Unit]](Right(()))((result, _) => result.flatMap(_ => value(depth)))
      def value(depth: Int): Either[String, Unit] =
        if (depth > maximumDepth) Left("MessagePack nesting is too deep")
        else if ((peek() & 0xf0) == 0x90 || peek() == 0xdc || peek() == 0xdd)
          readArrayHeader().flatMap(count => children(count.raw, depth + 1))
        else if ((peek() & 0xf0) == 0x80 || peek() == 0xde || peek() == 0xdf)
          readMapHeader().flatMap { count =>
            if (count.raw > Int.MaxValue / 2) Left("MessagePack map is too large")
            else children(count.raw * 2, depth + 1)
          }
        else readRawByte().flatMap { tag =>
          val length = tag match {
            case tag if tag <= 0x7f || tag >= 0xe0 || tag == 0xc0 || tag == 0xc2 || tag == 0xc3 => Right(0)
            case tag if (tag & 0xe0) == 0xa0                                                    => Right(tag & 0x1f)
            case 0xcc | 0xd0                                                                    => Right(1)
            case 0xcd | 0xd1                                                                    => Right(2)
            case 0xca | 0xce | 0xd2                                                             => Right(4)
            case 0xcb | 0xcf | 0xd3                                                             => Right(8)
            case 0xd9 | 0xc4                                                                    => readRawByte()
            case 0xda | 0xc5                                                                    => readRawShort()
            case 0xdb | 0xc6                                                                    => readRawInt()
            // Extension payloads have an additional one-byte type identifier.
            case 0xd4  => Right(2)
            case 0xd5  => Right(3)
            case 0xd6  => Right(5)
            case 0xd7  => Right(9)
            case 0xd8  => Right(17)
            case 0xc7  => readRawByte().flatMap(size => take(1).map(_ => size))
            case 0xc8  => readRawShort().flatMap(size => take(1).map(_ => size))
            case 0xc9  => readRawInt().flatMap(size => take(1).map(_ => size))
            case other => Left(f"invalid MessagePack tag 0x$other%02x")
          }
          length.flatMap(size => take(size).map(_ => ()))
        }
      value(0)
    }
  }
}
