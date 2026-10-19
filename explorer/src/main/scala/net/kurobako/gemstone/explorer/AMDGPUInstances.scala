package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.*
import net.kurobako.gemstone.string.*
import scala.collection.mutable

/** Assembly instances of an AMDGPU catalogue encoding on one target. Operands take distinct aligned registers of the
  * file their kind names, sized by their bit width, a lane mask in the target's wave size, a non-inline literal, or the
  * implicit state their kind stands for. Memory forms take each addressing mode their encoding has, atomics both with
  * and without the returned value, and a form takes every value of each modifier group its encoding has: the cross
  * product of the groups an instance cannot do without, such as a DPP control, then each value of the others one at a
  * time.
  */
private object AMDGPUInstances {

  /** An operand as written, and the operand names its register and literal spellings bind. */
  case class Spelt(text: String, binds: Map[String, String] = Map.empty)

  /** An instance being written: the operands in catalogue order, absent where the syntax leaves one out, the trailing
    * modifiers, and a dual-issue partner written before or after it.
    */
  case class Draft(
      mnemonic: String,
      operands: Vec[Option[Spelt]],
      modifiers: Vec[String] = Vec.empty,
      partner: Option[(text: String, first: Boolean)] = None,
      unwritten: Map[String, String] = Map.empty
  ) {
    def text: String = {
      val own = (mnemonic +: Vec(operands.flatten.map(_.text).mkString(", ")).filter(_.nonEmpty)) ++ modifiers
      partner.fold(own.mkString(" ")) { other =>
        if (other.first) s"${other.text} :: ${own.mkString(" ")}" else s"${own.mkString(" ")} :: ${other.text}"
      }
    }

    def binds: Map[String, String] = operands.flatten.foldLeft(unwritten)(_ ++ _.binds)
  }

  /** Values of one modifier group, each a change to a draft; a required group's values multiply instances. */
  case class Group(required: Boolean, values: Vec[Draft => Draft])

  val LiteralSpelling = "0x1234"

  private val FloatTypes = Set("f16", "f32", "f64", "bf16")

  private val OpSelectingPromoted = Set(ISA.RDNA3, ISA.RDNA3_5, ISA.GFX11_7, ISA.RDNA4, ISA.CDNA5)
  private val PairedOffsets       = "(read|write|wrxchg)2|2addr".r
  private val MatrixRange         = raw"([va])\[(\d+):\d+\]".r
  private val MatrixFormat        = raw"(cbsz|blgp):(\d)".r
  private val IntegerSecond       = "ldexp|trig_preop|class".r
  private val Promotable          = "VOP[12].*".r
  private val SourceFields        = Vec(EncodingField.SRC0, EncodingField.SRC1, EncodingField.SRC2)
  private val CacheFlags          = Vec(
    EncodingField.GLC -> "glc",
    EncodingField.SLC -> "slc",
    EncodingField.DLC -> "dlc",
    EncodingField.SC0 -> "sc0",
    EncodingField.SC1 -> "sc1",
    EncodingField.NT  -> "nt"
  )
  private val BufferFlags        = Vec(EncodingField.LDS -> "lds", EncodingField.TFE -> "tfe")
  private val DppModifiers       = Vec("row_mask:0x5", "bank_mask:0x5", "bound_ctrl:1")
  private val MatrixRegisterSets = Vec(8, 8, 6, 6, 4)

  def operandName(info: OperandInfo): String = info.field.fold(info.kind.toString)(_.toString)

  /** The implicit state an operand of `kind` stands for, by the state its kind ends with. */
  def state(kind: OperandKind): Option[String] =
    ImplicitState.names.find(kind.toString.toLowerCaseRoot.endsWith(_))

  private def literal(info: OperandInfo): Boolean =
    info.field.exists(field => field == EncodingField.LITERAL || field == EncodingField.LITERAL64)

  private def accumulator(kind: OperandKind): Boolean =
    kind == OperandKind.ACCVGPR || kind == OperandKind.SRC_ACCVGPR || kind == OperandKind.SRC_ACCVGPR_OR_CONST

  private def file(kind: OperandKind): Option[String] =
    if (accumulator(kind)) Some("a")
    else if (kind.isScalarSource || kind.isScalarDestination || kind.isScalarRegister || kind == OperandKind.SGPR)
      Some("s")
    else if (kind.isVector || kind.isSource) Some("v")
    else None

  /** `count` registers of the file `prefix` from `base`, spelt as one register or a range, binding the spellings that
    * name the operand `name`: the register or range itself and its first register, and the later registers as
    * `name+offset`.
    */
  def registers(prefix: String, base: Int, count: Int, name: String): Spelt = {
    val spelling = if (count == 1) s"$prefix$base" else s"$prefix[$base:${base + count - 1}]"
    Spelt(
      spelling,
      (1 until count).map(offset => s"$prefix${base + offset}" -> s"$name+$offset").toMap +
        (s"$prefix$base" -> name) + (spelling -> name)
    )
  }

  /** VGPRs `bases` as an address list, binding the first to `name` and the others as `name+offset`. */
  private def listed(bases: Vec[Int], name: String): Spelt =
    Spelt(
      bases.map(base => s"v$base").mkString("[", ", ", "]"),
      bases.zipWithIndex.map((base, offset) => s"v$base" -> (if (offset == 0) name else s"$name+$offset")).toMap
    )

  /** The first `count` registers of a register or range spelling. */
  private def resized(spelling: Spelt, count: Int): Option[Spelt] = {
    val prefix = spelling.text.takeWhile(_.isLetter)
    val start  = spelling.text.drop(prefix.length).stripPrefix("[").takeWhile(_.isDigit)
    start.toIntOption.map(base =>
      registers(prefix, base, count, spelling.binds.getOrElse(s"$prefix$base", spelling.text))
    )
  }

  /** How many addresses an image encoding holds: any number in one register range, up to its address fields in a gfx12
    * list, or two or more spread over the non-sequential address dwords, four to a dword after the first address.
    */
  private def holds(value: Metadata.Encoding, words: Int): Boolean = {
    val layout = EncodingCatalogue.layout(value.isa, value.format)
    val fields = layout.fold(0)(_.fields.count(_.name.isImageAddress))
    if (value.format == EncodingFormat.VIMAGE || value.format == EncodingFormat.VSAMPLE) words <= fields
    else if (value.format.isNonSequentialImage)
      words >= 2 && layout.exists(found => (words - 2) / 4 + 1 == (found.bits - 64) / 32)
    else true
  }

  /** The dimension an instance of an image encoding with a dimension field addresses and how many address registers it
    * takes there: the first dimension whose address count the encoding holds.
    */
  private def imageAddressing(value: Metadata.Encoding, mnemonic: String): Option[(ImageDimension, Int)] = for {
    layout <- EncodingCatalogue.layout(value.isa, value.format)
    if layout.fields.exists(_.name == EncodingField.DIM)
    shape <- InstructionDefs.forOpcode(mnemonic).headOption.flatMap(ImageMetadata.forOpcode)
    found <- ImageDimension.values.toVec.map(dimension =>
      dimension -> shape.addressWords(dimension, a16 = false, packedGradients = false)
    ).find((_, words) => holds(value, words))
  } yield found

  /** Why no instance of an encoding can be written, where none can: an image instruction that LLVM does not define or
    * whose address counts the encoding cannot hold, a catalogue record without operands, a literal in place of an
    * operand that takes none, or a sub-dword selection of a 64-bit move.
    */
  def unwritable(mnemonic: String, value: Metadata.Encoding): Option[String] =
    unshaped(mnemonic, value).orElse(operandless(mnemonic, value)).orElse(unliteral(value)).orElse(
      unsubdword(mnemonic, value)
    )

  /** Why the SDWA encoding of a 64-bit move cannot be written: SDWA selects bytes and words within a dword, and LLVM
    * defines no such variant of v_mov_b64.
    */
  private def unsubdword(mnemonic: String, value: Metadata.Encoding): Option[String] =
    Option.when(mnemonic == "v_mov_b64" && value.format.isSdwa)(
      "LLVM defines no SDWA variant of v_mov_b64, as SDWA selects bytes and words within a dword and the move takes " +
        "a register pair, so no toolchain writes it."
    )

  /** Why the VM leaves out an encoding whose instances the assembler cannot write either, where it does: gfx1250's
    * tensor data mover, which copies tiles between global memory and LDS by descriptors in SGPRs.
    */
  def unsupported(mnemonic: String, value: Metadata.Encoding): Option[String] =
    Option.when(value.format == EncodingFormat.VIMAGE && mnemonic.startsWith("tensor_"))(
      s"$mnemonic belongs to the tensor data mover, which copies tiles between global memory and LDS by descriptors " +
        "in SGPRs; neither the assembler nor the VM implements it."
    )

  /** Operand kinds a literal cannot stand for, with what they take instead. */
  private val Unliteral = Map(
    OperandKind.SSRC_NOLIT  -> "no literal",
    OperandKind.SRC_NOLIT   -> "no literal",
    OperandKind.SIMM4       -> "a 4-bit immediate",
    OperandKind.SRC_ACCVGPR -> "only AGPRs"
  )

  /** Why a literal encoding the catalogue lists for an operand that takes no literal cannot be written. */
  private def unliteral(value: Metadata.Encoding): Option[String] = value.operands.collectFirst {
    case info if literal(info) && Unliteral.contains(info.kind) =>
      s"The catalogue writes a literal for its ${info.kind} operand, which takes ${Unliteral(info.kind)}; LLVM " +
        "rejects one there, so no toolchain writes it."
  }

  private def written(value: Metadata.Encoding): Boolean = value.operands.exists(!_.implicitOperand)

  /** Why an encoding the catalogue lists without the operands its other encodings take cannot be written: the encoding
    * of the same opcode that names them, as the same instruction or another name for it, is classified instead.
    */
  private def operandless(mnemonic: String, value: Metadata.Encoding): Option[String] =
    Option.when(!written(value) && InstructionDefs.forOpcode(mnemonic).exists(_.meta.encodings.exists(written(_)))) {
      val named = InstructionDefs.definitions.view.flatMap(definition =>
        definition.meta.encodings.view.filter(other =>
          other.isa == value.isa && other.opcode == value.opcode && written(other) &&
            (definition.mnemonic.raw == mnemonic ||
              value.format == EncodingFormat.FLAT_GLBL && other.format == EncodingFormat.FLAT_GLOBAL)
        ).map(definition.mnemonic.raw -> _.format)
      ).headOption
      val listed = s"The ${value.isa} catalogue lists $mnemonic in ${value.format} without operands"
      named match {
        case Some((`mnemonic`, format)) => s"$listed; its $format encoding names them and is classified there."
        case Some((other, format))      => s"$listed, as another name for $other in $format, which is classified there."
        case None => s"$listed, and no other ${value.isa} encoding of its opcode names them, so none can be written."
      }
    }

  /** Why no instance of an image encoding can be written, where none can: LLVM, whose image table shapes the
    * assembler's operands, defines no such instruction, or no dimension takes an address count the encoding holds.
    */
  private def unshaped(mnemonic: String, value: Metadata.Encoding): Option[String] =
    Option.when(value.format.isImage && mnemonic.startsWith("image_"))(
      InstructionDefs.forOpcode(mnemonic).headOption.flatMap(ImageMetadata.forOpcode)
    ).flatMap {
      case None =>
        Some(s"No LLVM target defines $mnemonic, so no toolchain emits it and the assembler cannot shape it.")
      case Some(shape) if imageAddressing(value, mnemonic).isEmpty =>
        val counts = ImageDimension.values.toVec.map(shape.addressWords(_, a16 = false, packedGradients = false))
        EncodingCatalogue.layout(value.isa, value.format).filter(_ => value.format.isNonSequentialImage).map {
          layout =>
            val dwords = (layout.bits - 64) / 32
            val takes  = if (counts.min == counts.max) s"${counts.min}" else s"${counts.min} to ${counts.max}"
            s"${value.format} lists ${4 * dwords - 2} to ${4 * dwords + 1} addresses in $dwords extra " +
              s"dword${if (dwords == 1) "" else "s"}, and $mnemonic takes $takes, so no toolchain writes it."
        }
      case Some(_) => None
    }

  /** Distinct registers of each file, aligned to their size. */
  private class Allocator {
    private val next = mutable.Map.empty[String, Int]

    def take(prefix: String, count: Int, name: String): Spelt = {
      val alignment = if (count <= 2) count else 4
      val base      = next.getOrElse(prefix, 0).alignUp(alignment)
      next(prefix) = base + count
      registers(prefix, base, count, name)
    }
  }

  /** Whether the mnemonic's sources are floating point: the source type of a conversion, or any type it names. */
  private def floating(mnemonic: String): Boolean = {
    val parts = mnemonic.split('_').toVec
    if (parts.lift(1).exists(part => part == "cvt" || part == "frexp")) parts.lastOption.exists(FloatTypes)
    else parts.exists(FloatTypes)
  }

  private def halfWidth(mnemonic: String): Boolean = mnemonic.split('_').exists(_.endsWith("16"))

  /** The instances of `value`, an encoding of `mnemonic`, on `target`. */
  def written(
      target: Target,
      mnemonic: String,
      value: Metadata.Encoding
  ): Vec[(text: String, binds: Map[String, String])] = {
    val format = value.format
    val fields = EncodingCatalogue.layout(value.isa, format).fold(Set.empty[EncodingField])(_.fields.map(_.name).toSet)
    val wave32 = TargetDefaults.waveSize(target) == 32
    // the loads into LDS name no destination register
    val explicit = value.operands.filter(info =>
      !info.implicitOperand && info.kind != OperandKind.EXEC &&
        !(mnemonic.contains("_load_lds_") && info.field.contains(EncodingField.VDST))
    )
    val vector = format.isVector || format.isVOP3
    val alloc  = Allocator()
    val second = Allocator()
    // GFX8 before gfx810 loads and stores 16-bit typed buffer components unpacked, a register each
    val unpacked = target.isa == ISA.GFX8 && target.name != "gfx810" && mnemonic.contains("format_d16_") &&
      (format == EncodingFormat.MUBUF || format == EncodingFormat.MTBUF)
    // a scalar lane mask takes one register in a wave32
    def count(info: OperandInfo): Int = {
      val words = ((info.bits.raw + 31) / 32).max(1)
      if (vector && words == 2 && wave32 && file(info.kind).contains("s")) 1
      else if (unpacked && info.field.contains(EncodingField.VDATA)) mnemonic.reverse.takeWhile(_ != '_').length
      else words
    }
    val spelt = explicit.map { info =>
      val name = operandName(info)
      (literal(info), file(info.kind), state(info.kind)) match {
        case (true, _, _) => Spelt(LiteralSpelling, Map(LiteralSpelling -> name))
        // a named barrier's id, which LLVM takes from M0 or an inline constant
        case (false, Some(_), _) if !vector && info.kind == OperandKind.SRC => Spelt("m0", Map("m0" -> name))
        case (false, Some(prefix), _)                                       => alloc.take(prefix, count(info), name)
        case (false, None, Some(state))                                     => Spelt(state, Map(state -> name))
        // gfx12 renames the mode register
        case (false, None, None) if info.kind == OperandKind.HWREG =>
          Spelt(s"hwreg(${
              if (PartialFunction.cond(target.isa) { case ISA.RDNA4 | ISA.CDNA5 => true }) "HW_REG_WAVE_MODE"
              else "HW_REG_MODE"
            })")
        // MSG_RTN_GET_REALTIME, the one reply the VM has
        case (false, None, None) if mnemonic.startsWith("s_sendmsg_rtn_") => Spelt("131")
        case (false, None, None) if info.kind == OperandKind.ATTR         => Spelt("attr0.x")
        case (false, None, None) if info.kind == OperandKind.PARAM        => Spelt("p10")
        case (false, None, None) if info.kind.isImmediate                 => Spelt("1")
        // MSG_INTERRUPT, the one message every target defines
        case (false, None, None) if info.kind == OperandKind.SENDMSG => Spelt("1")
        case (false, None, None)                                     => Spelt("0")
      }
    }
    val suffix =
      if (format.isDpp && format.isVOP3) "_e64_dpp"
      else if (format.isDpp) "_dpp"
      else if (format.isSdwa) "_sdwa"
      else if (format.base == EncodingFormat.VOPD || format.base == EncodingFormat.VOPDXY) ""
      else if (format == EncodingFormat.VOPD3XY || format.base == EncodingFormat.VOP3P) ""
      else if (format.isVOP3) "_e64"
      else if (format.isVector) "_e32"
      else ""
    val dual = format.base == EncodingFormat.VOPD || format.base == EncodingFormat.VOPDXY ||
      format == EncodingFormat.VOPD3XY
    // dual-issue syntax leaves out the VCC a dual-issue select reads
    val base = Draft(
      mnemonic + suffix,
      explicit.zip(spelt).map((info, spelling) => Option.unless(dual && info.kind == OperandKind.VCC)(spelling)),
      unwritten = explicit.zip(spelt).collect {
        case (info, spelling) if dual && info.kind == OperandKind.VCC => spelling.binds
      }.foldLeft(Map.empty[String, String])(_ ++ _)
    )

    def index(field: EncodingField): Option[Int] = explicit.indexWhere(_.field.contains(field)) match {
      case -1    => None
      case found => Some(found)
    }
    def respell(field: EncodingField)(change: Spelt => Option[Spelt])(draft: Draft): Draft =
      index(field).fold(draft)(at =>
        draft.copy(operands = draft.operands.updated(at, draft.operands(at).flatMap(change)))
      )
    def first(spelling: Spelt): Spelt = {
      val register = spelling.text.takeWhile(_ != '[') + spelling.text.dropWhile(_ != '[').drop(1).takeWhile(_ != ':')
      Spelt(register, spelling.binds.filter((key, _) => key == register))
    }
    def modifier(text: String)(draft: Draft): Draft = draft.copy(modifiers = draft.modifiers :+ text)
    val off                                         = (_: Spelt) => Some(Spelt("off"))

    // GFX9's catalogue leaves its atomics ungrouped
    val atomic   = value.groups.contains(InstructionGroup.ATOMIC) || mnemonic.contains("_atomic_")
    val returned = target.isa match {
      case ISA.CDNA3 | ISA.CDNA4 => "sc0"
      case ISA.RDNA4 | ISA.CDNA5 => "th:TH_ATOMIC_RETURN"
      case _                     => "glc"
    }
    val flags = CacheFlags.collect {
      case (field, spelling)
          if fields(field) &&
            !(atomic && (spelling == returned || format == EncodingFormat.SMEM && field == EncodingField.GLC)) =>
        spelling
    }

    val accumulators = explicit.zipWithIndex.collect {
      case (info, at)
          if info.kind.acceptsAccumulator && !accumulator(info.kind) && !info.field.exists(field =>
            field == EncodingField.SRC0 || field == EncodingField.SRC1 || field == EncodingField.VADDR ||
              field == EncodingField.ADDR
          ) => (info, at)
    }
    val agpr = Option.when(fields(EncodingField.ACC) && accumulators.nonEmpty)(Group(
      required = false,
      Vec((draft: Draft) =>
        accumulators.foldLeft(draft) { case (changed, (info, at)) =>
          changed.copy(operands =
            changed.operands.updated(
              at,
              changed.operands(at).map(_ => second.take("a", count(info), operandName(info)))
            )
          )
        }
      )
    ))
    val returning = Option.when(atomic && index(EncodingField.VDST).nonEmpty)(Group(
      required = true,
      Vec(
        respell(EncodingField.VDST)(_ => None),
        (draft: Draft) => modifier(returned)(draft)
      )
    ))
    val returningBuffer = Option.when(atomic && format.base == EncodingFormat.MUBUF)(Group(
      required = true,
      Vec(identity[Draft], modifier(returned))
    ))
    val flagGroup = Option.when(flags.nonEmpty)(Group(required = false, flags.map(modifier(_))))

    val matrix = Option.when(format.base == EncodingFormat.VOP3P || format == EncodingFormat.VOP3PX2)(
      InstructionSemantics.matrixModifiers(target, mnemonic)
    ).flatten
    val groups: Vec[Option[Group]] = format match {
      case EncodingFormat.MUBUF | EncodingFormat.MTBUF | EncodingFormat.VBUFFER =>
        Vec(
          Some(Group(
            required = true,
            Vec(
              respell(EncodingField.VADDR)(off),
              respell(EncodingField.VADDR)(spelling => Some(first(spelling))).andThen(modifier("offen")),
              respell(EncodingField.VADDR)(spelling => Some(first(spelling))).andThen(modifier("idxen")),
              modifier("idxen").andThen(modifier("offen"))
            ) ++ Option.when(fields(EncodingField.ADDR64) && !mnemonic.contains("format"))(
              // GFX6 and GFX7 address a whole VGPR pair from the descriptor's base
              respell(EncodingField.VADDR) { spelling =>
                val register = first(spelling)
                register.text.drop(1).toIntOption.map(base =>
                  registers("v", base, 2, register.binds.getOrElse(register.text, EncodingField.VADDR.toString))
                )
              }.andThen(modifier("addr64"))
            ).toVec
          )),
          returningBuffer,
          Some(Group(
            required = false,
            Vec(modifier("offset:16")) ++ BufferFlags.collect {
              case (field, spelling) if fields(field) => modifier(spelling)
            }
          )),
          flagGroup,
          agpr
        )
      case _ if format.isImage =>
        // DMASK enables one component for each data register, and an atomic returns one, or a compare-and-swap's
        // pair; a format with a dimension field takes the first dimension whose address count it holds, at a range,
        // a gfx12 address list or registers apart for the non-sequential forms
        val shape      = InstructionDefs.forOpcode(mnemonic).headOption.flatMap(ImageMetadata.forOpcode)
        val atomicData = shape.filter(_.atomic).map(_ => if (mnemonic.contains("cmpswap")) 2 else 1)
        val components =
          atomicData.orElse(index(EncodingField.VDATA).map(at => count(explicit(at)).min(4))).getOrElse(1)
        val masked = modifier(f"dmask:0x${(1 << components) - 1}%x").andThen(atomicData.fold(identity[Draft])(n =>
          respell(EncodingField.VDATA)(spelling => resized(spelling, n))
        ))
        val address = index(EncodingField.VADDR).orElse(
          Some(explicit.indexWhere(info => info.field.isEmpty && !info.output && file(info.kind).contains("v")))
            .filter(_ >= 0)
        )
        val addressed = imageAddressing(value, mnemonic).fold(masked) { (dimension, words) =>
          val modern = format == EncodingFormat.VIMAGE || format == EncodingFormat.VSAMPLE
          def spell(spelling: Spelt): Option[Spelt] = {
            val start = first(spelling)
            val name  = start.binds.getOrElse(start.text, spelling.text)
            start.text.drop(1).toIntOption.map(base =>
              if (words == 1) start
              else if (modern) listed(Vec.tabulate(words)(base + _), name)
              else if (format.isNonSequentialImage) listed(base +: Vec.tabulate(words - 1)(20 + _), name)
              else registers("v", base, words, name)
            )
          }
          masked.andThen(modifier(s"dim:${dimension.assemblyName}")).andThen((draft: Draft) =>
            address.fold(draft)(at =>
              draft.copy(operands = draft.operands.updated(at, draft.operands(at).flatMap(spell(_))))
            )
          )
        }
        Vec(
          Some(Group(required = true, Vec(addressed))),
          Option.when(atomic)(Group(required = true, Vec(identity[Draft], modifier(returned)))),
          flagGroup
        )
      // gfx1250's flat takes a 64-bit VGPR address, or a scalar base and a 32-bit VGPR offset
      case EncodingFormat.FLAT | EncodingFormat.VFLAT =>
        Vec(
          index(EncodingField.SADDR).map(_ =>
            Group(
              required = true,
              Vec(
                respell(EncodingField.SADDR)(_ => None),
                respell(EncodingField.VADDR)(spelling => Some(first(spelling)))
              )
            )
          ),
          returning,
          Some(Group(required = false, Vec(modifier("offset:16")))),
          flagGroup,
          agpr
        )
      case EncodingFormat.FLAT_SCRATCH | EncodingFormat.VSCRATCH =>
        val both =
          PartialFunction.cond(target.isa) { case ISA.RDNA3 | ISA.RDNA3_5 | ISA.RDNA4 | ISA.CDNA3 | ISA.CDNA4 => true }
        Vec(
          Some(Group(
            required = true,
            Vec(
              respell(EncodingField.SADDR)(off),
              respell(EncodingField.ADDR)(off)
            ) ++
              (if (both)
                 Vec(respell(EncodingField.ADDR)(off).andThen(respell(EncodingField.SADDR)(off)), identity[Draft])
               else Vec.empty[Draft => Draft])
          )),
          returning,
          Some(Group(required = false, Vec(modifier("offset:16"), modifier("offset:-16")))),
          flagGroup,
          agpr
        )
      case EncodingFormat.FLAT_GLBL | EncodingFormat.FLAT_GLOBAL | EncodingFormat.VGLOBAL =>
        Vec(
          Some(Group(
            required = true,
            Vec(respell(EncodingField.SADDR)(off), respell(EncodingField.ADDR)(spelling => Some(first(spelling))))
          )),
          returning,
          Some(Group(required = false, Vec(modifier("offset:16"), modifier("offset:-16")))),
          flagGroup,
          agpr
        )
      case EncodingFormat.DS | EncodingFormat.VDS =>
        val two = PairedOffsets.findFirstIn(mnemonic).nonEmpty
        Vec(
          Some(Group(
            required = false,
            Vec(if (two) modifier("offset0:1").andThen(modifier("offset1:2")) else modifier("offset:16")) ++
              Option.when(fields(EncodingField.GDS))(modifier("gds")).toVec
          )),
          agpr
        )
      case EncodingFormat.SMEM =>
        Vec(
          Some(Group(
            required = true,
            Vec(identity[Draft]) ++ index(EncodingField.SOFFSET).map(_ =>
              respell(EncodingField.SOFFSET)(_ => Some(Spelt("s20")))
            ).toVec
          )),
          // a scalar atomic returns the old value under GLC, whatever the target calls the bit elsewhere
          Option.when(atomic && fields(EncodingField.GLC))(Group(
            required = true,
            Vec(identity[Draft], modifier("glc"))
          )),
          flagGroup
        )
      case _ if format.isSdwa =>
        val selections = SubDwordSelection.values.toVec.filter(_ != SubDwordSelection.DWord).map(_.assemblyName.raw)
        def selected(field: EncodingField, name: String): Vec[Draft => Draft] =
          if (fields(field)) selections.map(selection => modifier(s"$name:$selection")) else Vec.empty[Draft => Draft]
        Vec(
          Some(Group(
            required = false,
            selected(EncodingField.DST_SEL, "dst_sel") ++
              (if (fields(EncodingField.DST_UNUSED)) Vec("UNUSED_SEXT", "UNUSED_PRESERVE").map(unused =>
                 modifier("dst_sel:WORD_0").andThen(modifier(s"dst_unused:$unused"))
               )
               else Vec.empty[Draft => Draft]) ++
              selected(EncodingField.SRC0_SEL, "src0_sel") ++ selected(EncodingField.SRC1_SEL, "src1_sel") ++
              sourceModifiers(
                fields(EncodingField.SRC0_ABS),
                fields(EncodingField.SRC0_SEXT),
                Vec(
                  EncodingField.VSRC0,
                  EncodingField.VSRC1
                ).filter(field => index(field).nonEmpty),
                mnemonic,
                respell(_)
              ) ++
              outputModifiers(fields, mnemonic, compareClamp = false).map(modifier(_))
          ))
        )
      case _ if format.isDpp8 =>
        Vec(
          Some(Group(required = true, Vec("dpp8:[0,1,2,3,4,5,6,7]", "dpp8:[7,6,5,4,3,2,1,0]").map(modifier(_)))),
          Some(Group(required = false, Vec(modifier("fi:1")) ++ vop3(target, fields, mnemonic, explicit, respell(_))))
        )
      case _ if format.isDpp =>
        Vec(
          Some(Group(required = true, EncodingCatalogue.dppControls(value.isa).map(modifier(_)))),
          Some(Group(
            required = false,
            DppModifiers.map(modifier(_)) ++
              Option.when(fields(EncodingField.FI))(modifier("fi:1")).toVec ++
              sourceModifiers(
                fields(EncodingField.SRC0_ABS),
                extending = false,
                Vec(EncodingField.VSRC0, EncodingField.VSRC1)
                  .filter(field => index(field).nonEmpty),
                mnemonic,
                respell(_)
              ) ++
              vop3(target, fields, mnemonic, explicit, respell(_))
          ))
        )
      case _ if matrix.nonEmpty =>
        // the f8f6f4 forms take 8, 6 or 4 registers of A (CBSZ) and B (BLGP) by their formats
        def resized(field: EncodingField, code: Int): Draft => Draft = respell(field)(spelling =>
          spelling.text match {
            case MatrixRange(prefix, first) if mnemonic.endsWith("_f8f6f4") =>
              Some(registers(prefix, first.toInt, MatrixRegisterSets(code), spelling.binds(spelling.text)))
            case _ => Some(spelling)
          }
        )
        Vec(
          Some(Group(
            required = false,
            matrix.toVec.flatten.map {
              case text @ MatrixFormat("cbsz", code) => modifier(text).andThen(resized(EncodingField.SRC0, code.toInt))
              case text @ MatrixFormat(_, code)      => modifier(text).andThen(resized(EncodingField.SRC1, code.toInt))
              case text                              => modifier(text)
            }
          )),
          agpr
        )
      case _ if format.base == EncodingFormat.VOP3P =>
        val sources =
          SourceFields.count(field => index(field).nonEmpty)
        def bits(name: String, ones: (Int, Int) => Boolean): Vec[Draft => Draft] = Vec.tabulate(sources)(bit =>
          modifier(Vec.tabulate(sources)(at => if (ones(at, bit)) 1 else 0).mkString(s"$name:[", ",", "]"))
        )
        def lone(name: String): Vec[Draft => Draft]   = bits(name, _ == _)
        def allBut(name: String): Vec[Draft => Draft] = bits(name, _ != _)
        Vec(
          Some(Group(
            required = false,
            (if (fields(EncodingField.OP_SEL)) lone("op_sel") else Vec.empty[Draft => Draft]) ++
              (if (fields(EncodingField.OP_SEL_HI)) allBut("op_sel_hi") else Vec.empty[Draft => Draft]) ++
              (if (fields(EncodingField.NEG) && floating(mnemonic)) lone("neg_lo")
               else Vec.empty[Draft => Draft]) ++
              (if (fields(EncodingField.NEG_HI) && floating(mnemonic)) lone("neg_hi")
               else Vec.empty[Draft => Draft]) ++
              Option.when(fields(EncodingField.CLAMP))(modifier("clamp")).toVec
          )),
          agpr
        )
      // the partner takes the other half: after an X form, before a Y form; a VOPD3 form's partner has three
      // sources, which only VOPD3 encodes
      case _ if dual =>
        val second  = explicit.exists(_.field.exists(_.toString.endsWith("Y")))
        val partner =
          if (format == EncodingFormat.VOPD3XY) "v_dual_fma_f32 v7, v14, v15, v16" else "v_dual_mov_b32 v7, v14"
        Vec(Some(Group(
          required = true,
          Vec((draft: Draft) => draft.copy(partner = Some((text = partner, first = second))))
        )))
      case _ if format.isVOP3 =>
        Vec(Some(Group(required = false, vop3(target, fields, mnemonic, explicit, respell(_)))), agpr)
      case _ => Vec.empty
    }

    val present  = groups.flatten.filter(_.values.nonEmpty)
    val required = present.filter(_.required).foldLeft(Vec(base))((drafts, group) =>
      drafts.flatMap(draft => group.values.map(_(draft)))
    )
    val optional = for {
      group <- present.filterNot(_.required)
      value <- group.values
      draft <- required.headOption.toVec
    } yield value(draft)
    (required ++ optional).map(draft => (text = draft.text, binds = draft.binds)).distinctBy(_.text)
  }

  /** OMOD and clamp on a floating-point result; a compare has no result to scale, and its clamp, where it has one,
    * signals on NaN.
    */
  private def outputModifiers(fields: Set[EncodingField], mnemonic: String, compareClamp: Boolean): Vec[String] =
    if (mnemonic.startsWith("v_cmp")) Option.when(compareClamp && fields(EncodingField.CLAMP))("clamp").toVec
    else
      Option.when(fields(EncodingField.CLAMP))("clamp").toVec ++
        (if (fields(EncodingField.OMOD) && floating(mnemonic)) Vec("mul:2", "mul:4", "div:2") else Vec.empty[String])

  /** Negation and absolute value of each floating-point source, or sign extension of each integer source. */
  private def sourceModifiers(
      present: Boolean,
      extending: Boolean,
      sources: Vec[EncodingField],
      mnemonic: String,
      respell: EncodingField => (Spelt => Option[Spelt]) => Draft => Draft
  ): Vec[Draft => Draft] =
    if (present && floating(mnemonic)) sources.flatMap { field =>
      // the second source of these is an integer exponent, segment or class mask
      if (
        (field == EncodingField.SRC1 || field == EncodingField.VSRC1) &&
        IntegerSecond.findFirstIn(mnemonic).nonEmpty
      ) Vec.empty[Draft => Draft]
      else
        Vec(
          respell(field)(spelling => Some(spelling.copy(text = s"-${spelling.text}"))),
          respell(field)(spelling => Some(spelling.copy(text = s"|${spelling.text}|")))
        )
    }
    else if (extending && !floating(mnemonic))
      sources.map(field => respell(field)(spelling => Some(spelling.copy(text = s"sext(${spelling.text})"))))
    else Vec.empty[Draft => Draft]

  /** VOP3 clamp, OMOD, source modifiers and the 16-bit operand halves OP_SEL picks. */
  private def vop3(
      target: Target,
      fields: Set[EncodingField],
      mnemonic: String,
      explicit: Vec[OperandInfo],
      respell: EncodingField => (Spelt => Option[Spelt]) => Draft => Draft
  ): Vec[Draft => Draft] = {
    val sources = SourceFields
      .filter(field => explicit.exists(_.field.contains(field)))
    // before GFX11, OP_SEL is undefined on the VOP3 forms of opcodes VOP1 and VOP2 also encode
    val promoted = InstructionDefs.forOpcode(mnemonic).exists(_.meta.encodings.exists(encoding =>
      encoding.isa == target.isa && Promotable.matches(encoding.format.toString)
    ))
    val selected =
      if (fields(EncodingField.OP_SEL) && halfWidth(mnemonic) && (!promoted || OpSelectingPromoted(target.isa)))
        Vec.tabulate(sources.size + 1)(bit =>
          Vec.tabulate(sources.size + 1)(at => if (at == bit) 1 else 0).mkString("op_sel:[", ",", "]")
        )
      else Vec.empty[String]
    val compareClamp = !target.isa.isRDNA && floating(mnemonic) && !mnemonic.contains("class")
    (outputModifiers(fields, mnemonic, compareClamp) ++ selected).map(text =>
      (draft: Draft) =>
        draft.copy(modifiers = draft.modifiers :+ text)
    ) ++ sourceModifiers(
      fields(EncodingField.ABS) || fields(EncodingField.NEG),
      extending = false,
      sources,
      mnemonic,
      respell
    )
  }
}
