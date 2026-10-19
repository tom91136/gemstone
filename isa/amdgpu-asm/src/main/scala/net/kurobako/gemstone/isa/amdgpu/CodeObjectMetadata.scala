package net.kurobako.gemstone.isa.amdgpu

import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import scala.reflect.ClassTag

/** AMDGPU MessagePack metadata from the ELF note already owned by the Scala code-object decoder. */
object CodeObjectMetadata {
  case class HiddenArgument(valueKind: String, offset: Int >= 0, width: Int > 0)
  case class KernelManifest(
      name: NonEmpty[String],
      symbol: NonEmpty[String],
      metadata: KernelMetadata,
      hiddenArguments: Vec[HiddenArgument]
  )

  private val MetadataNoteType                 = 32
  private val Owner                            = "AMDGPU\u0000".getBytes(StandardCharsets.US_ASCII)
  private val MaximumDepth                     = 64
  private val MaximumContainerEntries: Int > 0 = 1048576

  def decode(image: Vec[Byte]): Either[CodeObjectError, Vec[KernelManifest]] =
    ELF.notes(
      image
    ).flatMap(decodeNotes(_)).flatMap(_.toRight(CodeObjectError.Invalid("AMDGPU metadata note is absent")))

  def decodeNotes(notes: Vec[Vec[Byte]]): Either[CodeObjectError, Option[Vec[KernelManifest]]] =
    notes.foldLeft[Either[CodeObjectError, Option[Vec[KernelManifest]]]](Right(None)) { (result, note) =>
      result.flatMap {
        case found @ Some(_) => Right(found)
        case None            => decodeNotes(Vec.toArray(note), 0, note.size)
      }
    }

  private def named(bytes: Array[Byte], at: Int): Boolean =
    Owner.indices.forall(index => bytes(at + index) == Owner(index))

  private def decodeNotes(
      bytes: Array[Byte],
      start: Int,
      size: Int
  ): Either[CodeObjectError, Option[Vec[KernelManifest]]] = {
    val limit = start + size
    @scala.annotation.tailrec
    def loop(
        offset: Int,
        found: Vec[KernelManifest]
    ): Either[CodeObjectError, Vec[KernelManifest]] =
      if (offset > limit - 12) Right(found)
      else {
        val nameSize          = Integer.toUnsignedLong(LittleEndian.decodeInt32(bytes, offset))
        val descriptionSize   = Integer.toUnsignedLong(LittleEndian.decodeInt32(bytes, offset + 4))
        val noteType          = LittleEndian.decodeInt32(bytes, offset + 8)
        val payload           = offset + 12
        val paddedName        = alignFour(nameSize)
        val paddedDescription = alignFour(descriptionSize)
        if (
          paddedName > Int.MaxValue || paddedDescription > Int.MaxValue ||
          !ByteRange.contains(limit, payload, paddedName) ||
          !ByteRange.contains(limit, payload.toLong + paddedName, paddedDescription)
        )
          Left(CodeObjectError.Invalid("truncated ELF note"))
        else {
          val description = payload + paddedName.toInt
          val next        = description + paddedDescription.toInt
          val owner       = nameSize == Owner.length.toLong && named(bytes, payload)
          if (!owner || noteType != MetadataNoteType) loop(next, found)
          else
            decodeMetadata(Reader(bytes, description, Math.toIntExact(descriptionSize))) match {
              case Left(error)    => Left(error)
              case Right(decoded) => loop(next, found ++ decoded)
            }
        }
      }
    loop(start, Vec.empty).map(result => Option.when(result.nonEmpty)(result))
  }

  private def decodeAll[A: ClassTag](count: Int >= 0)(
      decode: => Either[CodeObjectError, A]
  ): Either[CodeObjectError, Vec[A]] = {
    val decoded = Vec.newBuilder[A]
    @scala.annotation.tailrec
    def loop(remaining: Int): Either[CodeObjectError, Vec[A]] =
      if (remaining == 0) Right(decoded.result())
      else
        decode match {
          case Left(error)  => Left(error)
          case Right(value) =>
            decoded += value
            loop(remaining - 1)
        }
    loop(count)
  }

  private case class ParameterFields(offset: Option[Int >= 0], width: Option[Int >= 0], valueKind: Option[String])

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
          case (Some(offset), Some(width), Some(valueKind)) =>
            val allocation = Set("global_buffer", "by_reference", "queue").contains(valueKind)
            for {
              refinedWidth <- width.refine[Int > 0].left.map(_ =>
                CodeObjectError.Invalid("AMDGPU kernel argument width must be positive")
              )
              _ <- Either.cond(
                !allocation || width == 8,
                (),
                CodeObjectError.Invalid("AMDGPU pointer kernarg is not 64 bits")
              )
            } yield KernelMetadata.Parameter(
              refinedWidth,
              offset,
              if (allocation) KernelMetadata.ParameterKind.Allocation else KernelMetadata.ParameterKind.Scalar,
              containsPointers = !allocation
            ) -> Option.when(valueKind.startsWith("hidden_"))(valueKind)
          case _ => Left(CodeObjectError.Invalid("invalid AMDGPU kernel argument metadata"))
        }
      }
    }

  private case class KernelFields(
      name: Option[String],
      symbol: Option[String],
      argumentBlockSize: Option[Int >= 0],
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
                decodeAll(parameterCount)(decodeParameter(reader)).map(value => fields.copy(parameters = Some(value)))
              }
            case _ => reader.skip().map(_ => fields)
          }
        }
      }.flatMap { fields =>
        (fields.name, fields.symbol, fields.argumentBlockSize) match {
          case (Some(name), Some(symbol), Some(blockSize)) =>
            // Argument descriptions are optional; retain the raw block without inventing parameter boundaries.
            val parameters = fields.parameters.getOrElse(Vec.empty[(KernelMetadata.Parameter, Option[String])])
            val sorted     = parameters.sortBy(_._1.offset)
            for {
              refinedName <- name.refine[NonEmpty[String]].left.map(_ =>
                CodeObjectError.Invalid("AMDGPU kernel name must be non-empty")
              )
              refinedSymbol <- symbol.refine[NonEmpty[String]].left.map(_ =>
                CodeObjectError.Invalid("AMDGPU kernel symbol must be non-empty")
              )
              metadata <-
                KernelMetadata.validated(blockSize, sorted.count(_._2.isEmpty).unsafe[Int >= 0], sorted.map(_._1))
                  .left.map(detail => CodeObjectError.Invalid(detail))
            } yield KernelManifest(
              refinedName,
              refinedSymbol,
              metadata,
              sorted.collect { case (parameter, Some(kind)) =>
                HiddenArgument(kind, parameter.offset, parameter.width)
              }
            )
          case _ => Left(CodeObjectError.Invalid(
              "AMDGPU kernel metadata is missing name, symbol, or kernarg size"
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
                  decodeAll(kernelCount)(decodeKernel(reader)).map(Some(_))
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

  private class Reader(bytes: Array[Byte], start: Int, size: Int) {
    private val input = msgpack.Reader(
      bytes,
      start,
      size,
      msgpack.Limits(MaximumContainerEntries, Int.MaxValue, Int.MaxValue)
    )
    private def checked[A](result: Either[String, A]): Either[CodeObjectError, A] =
      result.left.map(message => CodeObjectError.Invalid(message.unsafe[NonEmpty[String]]))

    def finished: Boolean                            = input.remaining == 0
    def string: Either[CodeObjectError, String]      = checked(input.readString(strict = true))
    def uint32: Either[CodeObjectError, Int >= 0]    = checked(input.readUnsignedInt())
    def mapSize: Either[CodeObjectError, Int >= 0]   = checked(input.readMapHeader())
    def arraySize: Either[CodeObjectError, Int >= 0] = checked(input.readArrayHeader())
    def skip(): Either[CodeObjectError, Unit]        = checked(input.skip(MaximumDepth))
  }
}
