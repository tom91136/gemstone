package net.kurobako.gemstone.isa.amdgpu

import CodeObject.*

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.binary.ByteRange
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.isa.KernelMetadata
import net.kurobako.gemstone.isa.KernelName
import net.kurobako.gemstone.isa.LinkageName

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets

/** AMDGPU MessagePack metadata from the ELF note already owned by the Scala code-object decoder. */
object CodeObjectMetadata {
  final case class HiddenArgument(valueKind: String, offset: Int, width: Int)
  final case class KernelManifest(
      name: KernelName,
      symbol: LinkageName,
      metadata: KernelMetadata,
      hiddenArguments: Vec[HiddenArgument]
  )

  private val MetadataNoteType        = 32
  private val MaximumDepth            = 64
  private val MaximumContainerEntries = 1 << 20

  def decode(image: Vec[Byte]): Either[CodeObjectError, Vec[KernelManifest]] =
    decodeOptional(image).flatMap(_.toRight(CodeObjectError.Invalid("AMDGPU metadata note is absent")))

  /** Decode kernel metadata when present. Metadata-free ELF code objects still have usable symbols, globals, and code;
    * callers that only need those facilities must not reject the entire object.
    */
  def decodeOptional(image: Vec[Byte]): Either[CodeObjectError, Option[Vec[KernelManifest]]] = {
    val bytes = Vec.toArray(image)
    CodeObject.validateHeader(image).flatMap { _ =>
      val buffer      = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
      val tableOffset = buffer.getLong(40)
      val entrySize   = buffer.getShort(58) & 0xffff
      val count       = buffer.getShort(60) & 0xffff
      Either.cond(
        entrySize >= 64 && count > 0 && ByteRange.contains(bytes.length, tableOffset, entrySize.toLong * count),
        (),
        CodeObjectError.Invalid("invalid ELF section table")
      ).flatMap { _ =>
        (0 until count).foldLeft[Either[CodeObjectError, Option[Vec[KernelManifest]]]](Right(None)) {
          case (result, index) => result.flatMap {
              case found @ Some(_) => Right(found)
              case None            =>
                val start       = Math.toIntExact(tableOffset + index.toLong * entrySize)
                val sectionType = buffer.getInt(start + 4)
                val offset      = buffer.getLong(start + 24)
                val size        = buffer.getLong(start + 32)
                if (sectionType != 7) Right(None)
                else
                  Either.cond(
                    offset >= 0L && size >= 0L && size <= Int.MaxValue &&
                      ByteRange.contains(bytes.length, offset, size),
                    (),
                    CodeObjectError.Invalid("invalid ELF note section range")
                  ).flatMap(_ => decodeNotes(bytes, offset.toInt, size.toInt))
            }
        }
      }
    }
  }

  private def decodeNotes(
      bytes: Array[Byte],
      start: Int,
      size: Int
  ): Either[CodeObjectError, Option[Vec[KernelManifest]]] = {
    val limit = start + size
    def loop(
        offset: Int,
        found: Vec[KernelManifest]
    ): Either[CodeObjectError, Vec[KernelManifest]] =
      if (offset > limit - 12) Right(found)
      else {
        val nameSize          = Integer.toUnsignedLong(littleInt(bytes, offset))
        val descriptionSize   = Integer.toUnsignedLong(littleInt(bytes, offset + 4))
        val noteType          = littleInt(bytes, offset + 8)
        val payload           = offset + 12
        val paddedName        = alignFour(nameSize)
        val paddedDescription = alignFour(descriptionSize)
        Either.cond(
          paddedName <= Int.MaxValue && paddedDescription <= Int.MaxValue &&
            ByteRange.contains(limit, payload, paddedName) &&
            ByteRange.contains(limit, payload.toLong + paddedName, paddedDescription),
          (),
          CodeObjectError.Invalid("truncated ELF note")
        ).flatMap { _ =>
          val description = payload + paddedName.toInt
          val next        = description + paddedDescription.toInt
          val owner       = nameSize == 7L &&
            java.util.Arrays.equals(
              bytes,
              payload,
              payload + 7,
              "AMDGPU\u0000".getBytes(StandardCharsets.US_ASCII),
              0,
              7
            )
          if (!owner || noteType != MetadataNoteType) loop(next, found)
          else
            decodeMetadata(Reader(bytes, description, Math.toIntExact(descriptionSize))).flatMap(decoded =>
              loop(next, found ++ decoded)
            )
        }
      }
    loop(start, Vec.empty).map(result => Option.when(result.nonEmpty)(result))
  }

  private final case class ParameterFields(offset: Option[Int], width: Option[Int], valueKind: Option[String])

  private def decodeParameter(reader: Reader): Either[CodeObjectError, (KernelMetadata.Parameter, Option[String])] =
    reader.mapSize.flatMap { count =>
      (0 until count).foldLeft[Either[CodeObjectError, ParameterFields]](
        Right(ParameterFields(None, None, None))
      ) { case (result, _) =>
        result.flatMap { fields =>
          reader.string.flatMap {
            case ".offset"     => reader.uint32.map(value => fields.copy(offset = Some(value)))
            case ".size"       => reader.uint32.map(value => fields.copy(width = Some(value)))
            case ".value_kind" => reader.string.map(value => fields.copy(valueKind = Some(value)))
            case _             => reader.skip().map(_ => fields)
          }
        }
      }.flatMap { fields =>
        (fields.offset, fields.width, fields.valueKind) match {
          case (Some(offset), Some(width), Some(valueKind)) if width > 0 =>
            val allocation = Set("global_buffer", "by_reference", "queue").contains(valueKind)
            for {
              refinedWidth <- width.refineEither[Positive].left.map(_ =>
                CodeObjectError.Invalid("AMDGPU kernel argument width must be positive")
              )
              refinedOffset <- offset.refineEither[Positive0].left.map(_ =>
                CodeObjectError.Invalid("AMDGPU kernel argument offset must be non-negative")
              )
              _ <- Either.cond(
                !allocation || width == 8,
                (),
                CodeObjectError.Invalid("AMDGPU pointer kernarg is not 64 bits")
              )
            } yield KernelMetadata.Parameter(
              refinedWidth,
              refinedOffset,
              if (allocation) KernelMetadata.ParameterKind.Memory else KernelMetadata.ParameterKind.Scalar,
              containsPointers = !allocation
            ) -> Option.when(valueKind.startsWith("hidden_"))(valueKind)
          case _ => Left(CodeObjectError.Invalid("invalid AMDGPU kernel argument metadata"))
        }
      }
    }

  private final case class KernelFields(
      name: Option[String],
      symbol: Option[String],
      argumentBlockSize: Option[Int],
      parameters: Option[Vec[(KernelMetadata.Parameter, Option[String])]]
  )

  private def decodeKernel(reader: Reader): Either[CodeObjectError, KernelManifest] =
    reader.mapSize.flatMap { count =>
      (0 until count).foldLeft[Either[CodeObjectError, KernelFields]](
        Right(KernelFields(None, None, None, None))
      ) { case (result, _) =>
        result.flatMap { fields =>
          reader.string.flatMap {
            case ".name"                 => reader.string.map(value => fields.copy(name = Some(value)))
            case ".symbol"               => reader.string.map(value => fields.copy(symbol = Some(value)))
            case ".kernarg_segment_size" => reader.uint32.map(value => fields.copy(argumentBlockSize = Some(value)))
            case ".args"                 =>
              reader.arraySize.flatMap { parameterCount =>
                (0 until parameterCount).foldLeft[Either[CodeObjectError, Vec[(
                    KernelMetadata.Parameter,
                    Option[String]
                )]]](
                  Right(Vec.empty)
                )((parameters, _) => parameters.flatMap(values => decodeParameter(reader).map(values.appended)))
                  .map(value => fields.copy(parameters = Some(value)))
              }
            case _ => reader.skip().map(_ => fields)
          }
        }
      }.flatMap { fields =>
        (fields.name, fields.symbol, fields.argumentBlockSize, fields.parameters) match {
          case (Some(name), Some(symbol), Some(blockSize), Some(parameters)) =>
            val sorted = parameters.sortBy(_._1.offset)
            for {
              refinedName <- KernelName.either(name).left.map(_ =>
                CodeObjectError.Invalid("AMDGPU kernel name must be non-empty and contain no NUL")
              )
              refinedSymbol <- LinkageName.either(symbol).left.map(_ =>
                CodeObjectError.Invalid("AMDGPU kernel symbol must be non-empty and contain no NUL")
              )
              refinedBlockSize <- blockSize.refineEither[Positive0].left.map(_ =>
                CodeObjectError.Invalid("AMDGPU kernarg block size must be non-negative")
              )
              refinedParameterCount <- sorted.count(_._2.isEmpty).refineEither[Positive0].left.map(_ =>
                CodeObjectError.Invalid("AMDGPU explicit parameter count must be non-negative")
              )
              metadata <- KernelMetadata.validated(refinedBlockSize, refinedParameterCount, sorted.map(_._1))
                .left.map { detail =>
                  val raw: String = detail
                  CodeObjectError.Invalid(raw)
                }
            } yield KernelManifest(
              refinedName,
              refinedSymbol,
              metadata,
              sorted.collect { case (parameter, Some(kind)) =>
                HiddenArgument(kind, parameter.offset, parameter.width)
              }
            )
          case (Some(name), Some(symbol), Some(0), None) =>
            for {
              refinedName <- KernelName.either(name).left.map(_ =>
                CodeObjectError.Invalid("AMDGPU kernel name must be non-empty and contain no NUL")
              )
              refinedSymbol <- LinkageName.either(symbol).left.map(_ =>
                CodeObjectError.Invalid("AMDGPU kernel symbol must be non-empty and contain no NUL")
              )
              metadata <- KernelMetadata.validated(0, 0, Vec.empty[KernelMetadata.Parameter])
                .left.map { detail =>
                  val raw: String = detail
                  CodeObjectError.Invalid(raw)
                }
            } yield KernelManifest(refinedName, refinedSymbol, metadata, Vec.empty)
          case _ => Left(CodeObjectError.Invalid(
              "AMDGPU kernel metadata is missing name, symbol, kernarg size, or arguments"
            ))
        }
      }
    }

  private def decodeMetadata(reader: Reader): Either[CodeObjectError, Vec[KernelManifest]] =
    reader.mapSize.flatMap { count =>
      (0 until count).foldLeft[Either[CodeObjectError, Option[Vec[KernelManifest]]]](Right(None)) {
        case (result, _) => result.flatMap { found =>
            reader.string.flatMap {
              case "amdhsa.kernels" => reader.arraySize.flatMap { kernelCount =>
                  (0 until kernelCount).foldLeft[Either[CodeObjectError, Vec[KernelManifest]]](
                    Right(Vec.empty)
                  )((kernels, _) => kernels.flatMap(values => decodeKernel(reader).map(values.appended)))
                    .map(Some(_))
                }
              case _ => reader.skip().map(_ => found)
            }
          }
      }.flatMap(_.toRight(CodeObjectError.Invalid("AMDGPU metadata has no amdhsa.kernels array")))
        .flatMap(result =>
          Either.cond(reader.finished, result, CodeObjectError.Invalid("trailing AMDGPU MessagePack metadata"))
        )
    }

  private def alignFour(value: Long): Long = Math.addExact(value, 3L) & ~3L

  private def littleInt(bytes: Array[Byte], offset: Int): Int =
    LittleEndian.decodeInt32(bytes, offset)

  /** Cursor mutation is confined to this single-pass MessagePack reader. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private final class Reader(bytes: Array[Byte], start: Int, size: Int) {
    private val limit  = start + size
    private var cursor = start

    def finished: Boolean = cursor == limit

    def string: Either[CodeObjectError, String] = readByte("truncated MessagePack string").flatMap { tag =>
      val size =
        if ((tag & 0xe0) == 0xa0) Right(tag & 0x1f)
        else tag match {
          case 0xd9 => readUnsignedInt(1, "truncated str8 size")
          case 0xda => readUnsignedInt(2, "truncated str16 size")
          case 0xdb => readUnsigned(4, "truncated str32 size").flatMap(value =>
              Either.cond(
                value <= Int.MaxValue,
                value.toInt,
                CodeObjectError.Invalid("MessagePack string is too large")
              )
            )
          case _ => Left(CodeObjectError.Invalid("AMDGPU metadata key is not a string"))
        }
      size match {
        case Left(error)                         => Left(error)
        case Right(length) if length > remaining =>
          Left(CodeObjectError.Invalid("truncated MessagePack string"))
        case Right(length) =>
          val value = String(bytes, cursor, length, StandardCharsets.UTF_8)
          cursor += length
          Right(value)
      }
    }

    def uint32: Either[CodeObjectError, Int] = readByte("truncated MessagePack integer").flatMap {
      case tag if tag <= 0x7f => Right(tag)
      case 0xcc               => readUnsignedInt(1, "truncated uint8")
      case 0xcd               => readUnsignedInt(2, "truncated uint16")
      case 0xce               => readUnsigned(4, "truncated uint32").flatMap(unsignedInt(_))
      case 0xcf               => readUnsigned(8, "truncated uint64").flatMap(unsignedInt(_))
      case _                  => Left(CodeObjectError.Invalid("AMDGPU metadata integer is negative or malformed"))
    }

    def mapSize: Either[CodeObjectError, Int]   = containerSize(array = false)
    def arraySize: Either[CodeObjectError, Int] = containerSize(array = true)

    def skip(depth: Int = 0): Either[CodeObjectError, Unit] =
      Either.cond(depth <= MaximumDepth, (), CodeObjectError.Invalid("MessagePack nesting is too deep")).flatMap { _ =>
        readByte("truncated MessagePack value").flatMap { tag =>
          if (tag <= 0x7f || tag >= 0xe0 || tag == 0xc0 || tag == 0xc2 || tag == 0xc3) Right(())
          else if ((tag & 0xe0) == 0xa0) skipBytes(tag & 0x1f)
          else {
            val count =
              if ((tag & 0xf0) == 0x90) Right(tag & 0x0f)
              else if ((tag & 0xf0) == 0x80) Right(2 * (tag & 0x0f))
              else tag match {
                case 0xcc | 0xd0 => skipBytes(1).map(_ => 0)
                case 0xcd | 0xd1 => skipBytes(2).map(_ => 0)
                case 0xce | 0xd2 => skipBytes(4).map(_ => 0)
                case 0xcf | 0xd3 => skipBytes(8).map(_ => 0)
                case 0xd9        => readUnsigned(1, "truncated str8 size").flatMap(skipLength(_))
                case 0xda        => readUnsigned(2, "truncated str16 size").flatMap(skipLength(_))
                case 0xdb        => readUnsigned(4, "truncated str32 size").flatMap(skipLength(_))
                case 0xdc        => readUnsignedInt(2, "truncated array16 size")
                case 0xdd        => readUnsigned(4, "truncated array32 size").flatMap(containerCount(_, 1))
                case 0xde        => readUnsignedInt(2, "truncated map16 size").map(_ * 2)
                case 0xdf        => readUnsigned(4, "truncated map32 size").flatMap(containerCount(_, 2))
                case _           => Left(CodeObjectError.Invalid("unsupported MessagePack type in AMDGPU metadata"))
              }
            count.flatMap { entries =>
              Either.cond(
                entries <= 2 * MaximumContainerEntries,
                (),
                CodeObjectError.Invalid("MessagePack container is unreasonably large")
              ).flatMap(_ =>
                (0 until entries).foldLeft[Either[CodeObjectError, Unit]](Right(()))((result, _) =>
                  result.flatMap(_ => skip(depth + 1))
                )
              )
            }
          }
        }
      }

    private def remaining: Int = limit - cursor

    private def readByte(error: String): Either[CodeObjectError, Int] =
      if (remaining < 1) Left(CodeObjectError.Invalid(error))
      else {
        val result = bytes(cursor) & 0xff
        cursor += 1
        Right(result)
      }

    private def readUnsigned(
        length: Int,
        error: String
    ): Either[CodeObjectError, Long] =
      if (remaining < length) Left(CodeObjectError.Invalid(error))
      else {
        val result = (0 until length).foldLeft(0L)((value, index) => (value << 8) | (bytes(cursor + index) & 0xffL))
        cursor += length
        Right(result)
      }

    private def readUnsignedInt(length: Int, error: String): Either[CodeObjectError, Int] =
      readUnsigned(length, error).flatMap(unsignedInt(_))

    private def skipBytes(length: Int): Either[CodeObjectError, Unit] =
      if (length > remaining) Left(CodeObjectError.Invalid("truncated MessagePack value"))
      else { cursor += length; Right(()) }

    private def skipLength(length: Long): Either[CodeObjectError, Int] =
      Either.cond(length <= Int.MaxValue, length.toInt, CodeObjectError.Invalid("MessagePack value is too large"))
        .flatMap(skipBytes(_)).map(_ => 0)

    private def unsignedInt(value: Long): Either[CodeObjectError, Int] =
      Either.cond(
        value <= Int.MaxValue,
        value.toInt,
        CodeObjectError.Invalid("AMDGPU metadata integer exceeds supported range")
      )

    private def containerCount(value: Long, multiplier: Int): Either[CodeObjectError, Int] =
      Either.cond(
        value <= MaximumContainerEntries,
        value.toInt * multiplier,
        CodeObjectError.Invalid("MessagePack container is unreasonably large")
      )

    private def containerSize(array: Boolean): Either[CodeObjectError, Int] =
      readByte("truncated MessagePack container").flatMap { tag =>
        val count =
          if (array && (tag & 0xf0) == 0x90) Right(tag & 0x0f)
          else if (!array && (tag & 0xf0) == 0x80) Right(tag & 0x0f)
          else if (array && tag == 0xdc) readUnsignedInt(2, "truncated array16 size")
          else if (array && tag == 0xdd) readUnsigned(4, "truncated array32 size").flatMap(containerCount(_, 1))
          else if (!array && tag == 0xde) readUnsignedInt(2, "truncated map16 size")
          else if (!array && tag == 0xdf) readUnsigned(4, "truncated map32 size").flatMap(containerCount(_, 1))
          else Left(CodeObjectError.Invalid(
            if (array) "AMDGPU metadata value is not an array" else "AMDGPU metadata value is not a map"
          ))
        count.flatMap(value =>
          Either.cond(
            value <= MaximumContainerEntries,
            value,
            CodeObjectError.Invalid("MessagePack container is unreasonably large")
          )
        )
      }
  }

}
