package net.kurobako.gemstone.isa.amdgpu

import EncodingConstants.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import scala.collection.immutable.IntMap

/** Field-level machine decoding shared by the VM compiler and the disassembler. */
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
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def candidates(stream: CodeStream, offset: Int): Vec[Decoded] =
    if (stream.bytes.length - offset < 4) Vec.empty
    else {
      val bytes   = stream.bytes
      val target  = stream.target
      val low     = LittleEndian.decode(bytes, offset, 4)
      val entries = tables.getOrElse(target.isa, Vec.empty[Entry])
      val result  = Vec.newBuilder[Decoded]
      var i       = 0
      while (i < entries.length) {
        val entry = entries(i)
        val width = entry.layout.bits / 8
        if (
          !DualFormats(entry.layout.format) && (low & entry.lowMask) == entry.lowPrefix &&
          bytes.length - offset >= width
        ) {
          val word = InstructionWord.read(bytes, offset, width)
          if (entry.layout.matches(word)) {
            val op    = entry.layout.get(word, EncodingField.OP).toInt
            val cands = entry.opcodes.getOrElse(op, NoEncodings)
            var c     = 0
            while (c < cands.length) {
              decoded(entry.layout, word, stream, offset)(cands(c)) match {
                case Some(dec) => result += dec
                case None      =>
              }
              c += 1
            }
          }
        }
        i += 1
      }
      result.result()
    }

  /** The single instruction the VM executes: the first matching layout's canonical entry, falling back to the base
    * format, the literal variant, VOPC for VOP3-encoded compares and VOP3 for VOP3P-encoded opcodes.
    */
  def decode(stream: CodeStream, offset: Int): Either[NonEmpty[String], Decoded] = {
    val target = stream.target
    val exact  = candidates(stream, offset)
    val found  = if (exact.nonEmpty) exact
    else matching(stream, offset).flatMap { case (entry, word) =>
      fallbacks(entry.layout.format).view.flatMap(format =>
        encodings(entry.layout, format).getOrElse(entry.layout.get(word, EncodingField.OP).toInt, NoEncodings)
      ).flatMap(decoded(entry.layout, word, stream, offset))
    }
    found.headOption.toRight[NonEmpty[String]](
      if (truncated(stream, offset)) s"truncated instruction at byte $offset"
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
  def dual(stream: CodeStream, offset: Int): Option[Either[NonEmpty[String], Dual]] = {
    val bytes  = stream.bytes
    val target = stream.target
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
  }

  /** Whether a layout's fixed bits match the first dword but the instruction or its literal runs past the bytes. */
  private def truncated(stream: CodeStream, offset: Int): Boolean = {
    val bytes = stream.bytes
    bytes.length - offset >= 4 && {
      val low = LittleEndian.decode(bytes, offset, 4)
      tables.getOrElse(stream.target.isa, Vec.empty[Entry]).exists(entry =>
        (low & entry.lowMask) == entry.lowPrefix && !DualFormats(entry.layout.format) &&
          (bytes.length - offset < entry.layout.bits / 8 || {
            val word = InstructionWord.read(bytes, offset, entry.layout.bits / 8)
            entry.layout.matches(word) && entry.opcodes.contains(entry.layout.get(word, EncodingField.OP).toInt)
          })
      )
    }
  }

  private def matching(stream: CodeStream, offset: Int): Vec[(Entry, InstructionWord)] =
    if (stream.bytes.length - offset < 4) Vec.empty
    else {
      val bytes = stream.bytes
      val low   = LittleEndian.decode(bytes, offset, 4)
      tables.getOrElse(stream.target.isa, Vec.empty[Entry]).flatMap { entry =>
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

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def decoded(layout: InstructionLayout, word: InstructionWord, stream: CodeStream, offset: Int)(
      candidate: (Opcode, Metadata.Encoding)
  ): Option[Decoded] = {
    val (definition, encoding) = candidate
    val bytes                  = stream.bytes
    if (!encoding.supports(stream.target)) None
    else {
      val embedded     = layout.embedded
      var source       = 0
      var hasLiteral64 = false
      var hasLiteral   = false
      val ops          = encoding.operands
      var i            = 0
      while (i < ops.length) {
        val info = ops(i)
        if (!info.implicitOperand && info.kind != OperandKind.EXEC) {
          info.field match {
            case Some(field) =>
              if (!embedded && SourceFields.contains(field)) {
                val code = layout.get(word, field).toInt
                if (code == Source.LITERAL_CONST || code == Source.LITERAL64_CONST) {
                  val lit = SourceOperand.literalSize(code, layout.isa).fold(0)(_.raw)
                  if (lit > source) source = lit
                }
              }
              if (field == EncodingField.LITERAL64) hasLiteral64 = true
              else if (field == EncodingField.LITERAL) hasLiteral = true
            case None =>
          }
        }
        i += 1
      }
      val instruction =
        if (embedded || source > 0) 0
        else if (layout.memoryLiteral(word)) 4
        else if (hasLiteral64) 8
        else if (hasLiteral) 4
        else 0
      val trailing = math.max(source, instruction)
      val rawSize  = layout.bits / 8 + trailing
      if (rawSize > 0 && bytes.length - offset >= rawSize && layout.accepts(word, source)) {
        val size            = rawSize.unsafe[Int > 0]
        val embeddedLiteral = layout.literalField.map(_.get(word))
        val literal         = embeddedLiteral match {
          case Some(value) => Some(value)
          case None        =>
            if (trailing == 8) Some(LittleEndian.decode(bytes, offset + layout.bits / 8, 8))
            else if (trailing > 0) Some(LittleEndian.decode(bytes, offset + layout.bits / 8, 4))
            else None
        }
        Some(Decoded(layout, definition, encoding, word, literal, if (embeddedLiteral.nonEmpty) 4 else trailing, size))
      } else None
    }
  }
}
