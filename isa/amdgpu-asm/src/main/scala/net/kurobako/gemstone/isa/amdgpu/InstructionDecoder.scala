package net.kurobako.gemstone.isa.amdgpu

import EncodingConstants.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import scala.collection.immutable.IntMap

/** Field-level machine decoding shared by the VM compiler and the disassembler. */
// the instruction cursor is passed unpacked so decoding allocates no cursor per instruction
@SuppressWarnings(Array("baumkuchen:data-clump"))
private object InstructionDecoder {

  /** One machine instruction: its layout, catalogue encoding, fixed bits and literal. */
  case class Decoded(
      layout: InstructionLayout,
      definition: Opcode,
      encoding: Metadata.Encoding,
      word: InstructionWord,
      literal: Option[Long],
      literalBytes: Int,
      size: Int > 0
  ) {
    def format: EncodingFormat                  = layout.format
    def apply(field: EncodingField): Long       = layout.get(word, field)
    def signed(field: EncodingField): Long      = layout.signed(word, field)
    def contains(field: EncodingField): Boolean = layout.byName.contains(field)
  }

  /** A dual-issue pair; each half lists the catalogue entries sharing its opcode, preferred spelling first. */
  case class Dual(
      layout: InstructionLayout,
      x: Vec[(Opcode, Metadata.Encoding)],
      y: Vec[(Opcode, Metadata.Encoding)],
      word: InstructionWord,
      literal: Option[Long],
      size: Int > 0
  ) {
    def apply(field: EncodingField): Long = layout.get(word, field)

    /** VOPD stores VDSTY without its low bit, which is the complement of the low bit of VDSTX. */
    def destinationY: Long =
      if (layout.format == EncodingFormat.VOPD3XY) apply(EncodingField.VDSTY)
      else apply(EncodingField.VDSTY) | ((apply(EncodingField.VDSTX) & 1) ^ 1)
  }

  private case class Entry(
      layout: InstructionLayout,
      lowMask: Long,
      lowPrefix: Long,
      opcodes: IntMap[Vec[(Opcode, Metadata.Encoding)]]
  )

  private val NoEncodings = Vec.empty[(Opcode, Metadata.Encoding)]

  private val DualFormats = Set(EncodingFormat.VOPDXY, EncodingFormat.VOPD3XY)

  private val SourceFields = Set(
    EncodingField.SRC0,
    EncodingField.SRC1,
    EncodingField.SRC2,
    EncodingField.SSRC0,
    EncodingField.SSRC1,
    EncodingField.SCALE_SRC0,
    EncodingField.SCALE_SRC1,
    EncodingField.SCALE_SRC2
  )

  private val Variants =
    Vec("_INST_LITERAL64", "_INST_LITERAL", "_VOP_DPP16", "_VOP_DPP8", "_VOP_DPP", "_VOP_SDWA_SDST_ENC", "_VOP_SDWA")

  private val DualSides = Vec(EncodingField.VDSTX, EncodingField.VDSTY)

  private def entry(layout: InstructionLayout): Entry =
    Entry(
      layout,
      layout.wordMask.low & 0xffffffffL,
      layout.wordPrefix.low & 0xffffffffL,
      encodings(layout, layout.format)
    )

  private def encodings(layout: InstructionLayout, format: EncodingFormat) = OpcodeIndex.encodings(layout.isa, format)

  /** Layouts of each ISA, most specific fixed bits first. */
  private lazy val tables: Map[ISA, Vec[Entry]] = InstructionLayout.Catalogue.all.groupBy(_.isa).view.mapValues {
    layouts => layouts.sortBy(layout => -layout.fixedMask.bitCount).map(entry(_))
  }.toMap

  private lazy val duals: Map[(ISA, EncodingFormat, EncodingField, Int), Vec[(Opcode, Metadata.Encoding)]] =
    InstructionDefs.definitions.flatMap { definition =>
      definition.meta.encodings.flatMap { encoding =>
        val family =
          if (encoding.format == EncodingFormat.VOPD3XY) Some(EncodingFormat.VOPD3XY)
          else Option.when(encoding.format.base == EncodingFormat.VOPDXY)(EncodingFormat.VOPDXY)
        val source = (definition, encoding)
        family.toVec.flatMap(family =>
          DualSides.collect {
            case side if encoding.operands.exists(_.field.contains(side)) =>
              (encoding.isa, family, side, encoding.opcode) -> source
          }
        )
      }
    }.groupMap(_._1)(_._2).view.mapValues(_.distinct).toMap

  /** Every layout and exact catalogue entry consistent with the bytes, in layout priority order. */
  def candidates(bytes: Array[Byte], offset: Int, target: Target): Vec[Decoded] =
    matching(bytes, offset, target).flatMap { case (entry, word) =>
      entry.opcodes.getOrElse(entry.layout.get(word, EncodingField.OP).toInt, NoEncodings)
        .flatMap(decoded(entry.layout, word, bytes, offset, target))
    }

  /** The single instruction the VM executes: the first matching layout's canonical entry, falling back to the base
    * format, the literal variant, VOPC for VOP3-encoded compares and VOP3 for VOP3P-encoded opcodes.
    */
  def decode(bytes: Array[Byte], offset: Int, target: Target): Either[NonEmpty[String], Decoded] = {
    val exact = candidates(bytes, offset, target)
    val found = if (exact.nonEmpty) exact
    else matching(bytes, offset, target).flatMap { case (entry, word) =>
      fallbacks(entry.layout.format).view.flatMap(format =>
        encodings(entry.layout, format).getOrElse(entry.layout.get(word, EncodingField.OP).toInt, NoEncodings)
      ).flatMap(decoded(entry.layout, word, bytes, offset, target))
    }
    found.headOption.toRight[NonEmpty[String]](
      if (truncated(bytes, offset, target)) s"truncated instruction at byte $offset"
      else s"unknown instruction encoding for ${target.name} at byte $offset"
    )
      .flatMap { first =>
        val group = OpcodeIndex.canonical(found.filter(_.layout eq first.layout), target)(_.definition)
        group.map(_.definition).distinct match {
          case Vec(_) => Right(group.head)
          case values =>
            Left(
              s"ambiguous ${first.format} opcode ${first(EncodingField.OP)} for ${target.name}: ${values.mkString(", ")}"
            )
        }
      }
  }

  /** Decodes a dual-issue pair when the bytes carry a VOPD prefix. */
  def dual(bytes: Array[Byte], offset: Int, target: Target): Option[Either[NonEmpty[String], Dual]] =
    Option.when(bytes.length - offset >= 4)(LittleEndian.decode(bytes, offset, 4)).flatMap { low =>
      DualFormats.toVec.flatMap(InstructionLayout.Catalogue.forFormat(target, _))
        .find(layout => (low & layout.wordMask.low & 0xffffffffL) == (layout.wordPrefix.low & 0xffffffffL))
        .map { layout =>
          val width = layout.bits / 8
          for {
            _ <- Either.cond[NonEmpty[String], Unit](
              bytes.length - offset >= width,
              (),
              s"truncated dual-issue instruction at byte $offset"
            )
            word = InstructionWord.read(bytes, offset, width)
            half = (opcode: EncodingField, destination: EncodingField) =>
              OpcodeIndex.canonical(
                duals.getOrElse((layout.isa, layout.format, destination, layout.get(word, opcode).toInt), NoEncodings)
                  .filter(_._2.supports(target)),
                target
              )(_._1)
            x = half(EncodingField.OPX, EncodingField.VDSTX)
            y = half(EncodingField.OPY, EncodingField.VDSTY)
            _ <- Either.cond[NonEmpty[String], Unit](
              x.nonEmpty && y.nonEmpty,
              (),
              s"unknown dual-issue opcode at byte $offset for ${target.name}"
            )
            // FMAAK/FMAMK halves carry an instruction literal; other halves share one through a source selector.
            literal = Vec(x, y).flatMap(_.headOption).exists(_._2.operands.exists(info =>
              info.field.contains(EncodingField.LITERAL) && info.kind != OperandKind.SRC
            )) ||
              Vec(EncodingField.SRCX0, EncodingField.SRCY0).exists(layout.get(word, _) == Source.LITERAL_CONST)
            size <- (width + (if (literal) 4 else 0)).refine[Int > 0]
            _    <- Either.cond[NonEmpty[String], Unit](
              bytes.length - offset >= size,
              (),
              s"truncated dual-issue literal at byte $offset"
            )
          } yield Dual(layout, x, y, word, Option.when(literal)(LittleEndian.decode(bytes, offset + width, 4)), size)
        }
    }

  /** Whether a layout's fixed bits match the first dword but the instruction or its literal runs past the bytes. */
  private def truncated(bytes: Array[Byte], offset: Int, target: Target): Boolean =
    bytes.length - offset >= 4 && {
      val low = LittleEndian.decode(bytes, offset, 4)
      tables.getOrElse(target.isa, Vec.empty[Entry]).exists(entry =>
        (low & entry.lowMask) == entry.lowPrefix && !DualFormats(entry.layout.format) &&
          (bytes.length - offset < entry.layout.bits / 8 || {
            val word = InstructionWord.read(bytes, offset, entry.layout.bits / 8)
            entry.layout.matches(word) && entry.opcodes.contains(entry.layout.get(word, EncodingField.OP).toInt)
          })
      )
    }

  private def matching(bytes: Array[Byte], offset: Int, target: Target): Vec[(Entry, InstructionWord)] =
    if (bytes.length - offset < 4) Vec.empty
    else {
      val low = LittleEndian.decode(bytes, offset, 4)
      tables.getOrElse(target.isa, Vec.empty[Entry]).flatMap { entry =>
        val width = entry.layout.bits / 8
        if (
          DualFormats(entry.layout.format) || (low & entry.lowMask) != entry.lowPrefix || bytes.length - offset < width
        ) Vec.empty
        else {
          val word = InstructionWord.read(bytes, offset, width)
          Option.when(entry.layout.matches(word))(entry -> word).toVec
        }
      }
    }

  private def fallbacks(format: EncodingFormat): Vec[EncodingFormat] = {
    val variant = Variants.collectFirst {
      case suffix if format.toString.endsWith(suffix) => format.toString.dropRight(suffix.length)
    }.flatMap(EncodingFormat.fromName)
    val alias = format match {
      case EncodingFormat.VOP3  => Some(EncodingFormat.VOPC)
      case EncodingFormat.VOP3P => Some(EncodingFormat.VOP3)
      case _                    => None
    }
    (variant.toVec ++ Vec(format.base) ++ format.withLiteral(4).toVec ++ alias.toVec).distinct.filterNot(_ == format)
  }

  private def decoded(
      layout: InstructionLayout,
      word: InstructionWord,
      bytes: Array[Byte],
      offset: Int,
      target: Target
  )(candidate: (Opcode, Metadata.Encoding)): Option[Decoded] = {
    val (definition, encoding) = candidate
    val operands = encoding.operands.filterNot(info => info.implicitOperand || info.kind == OperandKind.EXEC)
    val embedded = layout.byName.contains(EncodingField.LITERAL) || layout.byName.contains(EncodingField.LITERAL64)
    val source   =
      if (embedded) 0
      else operands.flatMap(_.field).filter(SourceFields).flatMap(field =>
        SourceOperand.literalSize(layout.get(word, field).toInt, layout.isa).rawElements
      ).maxOption.getOrElse(0)
    // Instruction literals such as the FMAAK constant follow the word whatever the source selectors say.
    val instruction =
      if (embedded || source > 0) 0
      else if (layout.memoryLiteral(word)) 4
      else if (operands.exists(_.field.contains(EncodingField.LITERAL64))) 8
      else if (operands.exists(_.field.contains(EncodingField.LITERAL))) 4
      else 0
    val trailing = source.max(instruction)
    for {
      size <- (layout.bits / 8 + trailing).refine[Int > 0].toOption
      if encoding.supports(target) && bytes.length - offset >= size && layout.accepts(word, source)
    } yield {
      val embeddedLiteral = layout.byName.get(EncodingField.LITERAL).map(_.get(word))
      val literal         = embeddedLiteral.orElse(Option.when(trailing > 0)(
        if (trailing == 8) LittleEndian.decode(bytes, offset + layout.bits / 8, 8)
        else LittleEndian.decode(bytes, offset + layout.bits / 8, 4)
      ))
      Decoded(layout, definition, encoding, word, literal, if (embeddedLiteral.nonEmpty) 4 else trailing, size)
    }
  }
}
