package net.kurobako.gemstone.isa.amdgpu

import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*

/** AMDGPU MessagePack metadata from the ELF note already owned by the Scala code-object decoder. */
object CodeObjectMetadata {
  case class HiddenArgument(valueKind: String, offset: Int >= 0, width: Int > 0)
  case class KernelManifest(
      name: NonEmpty[String],
      symbol: NonEmpty[String],
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
  def decodeOptional(image: Vec[Byte]): Either[CodeObjectError, Option[Vec[KernelManifest]]] =
    ELF.notes(image).flatMap(decodeNotes(_))

  def decodeNotes(notes: Vec[Vec[Byte]]): Either[CodeObjectError, Option[Vec[KernelManifest]]] =
    notes.foldLeft[Either[CodeObjectError, Option[Vec[KernelManifest]]]](Right(None)) { (result, note) =>
      result.flatMap {
        case found @ Some(_) => Right(found)
        case None            => decodeNotes(Vec.toArray(note), 0, note.size)
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
        val nameSize          = Integer.toUnsignedLong(LittleEndian.decodeInt32(bytes, offset))
        val descriptionSize   = Integer.toUnsignedLong(LittleEndian.decodeInt32(bytes, offset + 4))
        val noteType          = LittleEndian.decodeInt32(bytes, offset + 8)
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
          case (Some(offset), Some(width), Some(valueKind)) if width.raw > 0 =>
            val allocation = Set("global_buffer", "by_reference", "queue").contains(valueKind)
            for {
              refinedWidth <- width.raw.refine[Int > 0].left.map(_ =>
                CodeObjectError.Invalid("AMDGPU kernel argument width must be positive")
              )
              _ <- Either.cond(
                !allocation || width.raw == 8,
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
        val parameters = fields.parameters.orElse(Option.when(fields.argumentBlockSize.contains(0))(Vec.empty[(
            KernelMetadata.Parameter,
            Option[String]
        )]))
        (fields.name, fields.symbol, fields.argumentBlockSize, parameters) match {
          case (Some(name), Some(symbol), Some(blockSize), Some(parameters)) =>
            val sorted = parameters.sortBy(_._1.offset)
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

  private class Reader(bytes: Array[Byte], start: Int, size: Int) {
    private val input = msgpack.Reader(
      bytes = bytes,
      start = start,
      size = size,
      maximumElements = MaximumContainerEntries
    )
    private def checked[A](result: Either[String, A]): Either[CodeObjectError, A] =
      result.left.map(message => CodeObjectError.Invalid(message.unsafe[NonEmpty[String]]))

    def finished: Boolean                         = input.remaining == 0
    def string: Either[CodeObjectError, String]   = checked(input.readString(strict = true))
    def uint32: Either[CodeObjectError, Int >= 0] = checked(input.readUnsignedInt()).map(_.unsafe[Int >= 0])
    def mapSize: Either[CodeObjectError, Int]     = checked(input.readMapHeader())
    def arraySize: Either[CodeObjectError, Int]   = checked(input.readArrayHeader())
    def skip(): Either[CodeObjectError, Unit]     = checked(input.skip(MaximumDepth))
  }
}
