package net.kurobako.gemstone.isa.amdgpu.asm

import cats.syntax.all.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.*
import net.kurobako.gemstone.isa.amdgpu.EncodingConstants.*
import net.kurobako.gemstone.isa.amdgpu.PackedVectorInstructionDefs.*
import net.kurobako.gemstone.string.*

/** Maps instruction operands and modifiers to the generated machine layouts. */
private object InstructionCodec {
  private val Supported = Set(
    EncodingFormat.SOP1,
    EncodingFormat.SOP2,
    EncodingFormat.SOPC,
    EncodingFormat.SOPK,
    EncodingFormat.SOPP,
    EncodingFormat.ILLEGAL,
    EncodingFormat.VOP3_BITOP3,
    EncodingFormat.VOP3_BYTESEL,
    EncodingFormat.VOP3P_MFMA,
    EncodingFormat.VOP3PX2,
    EncodingFormat.VOP3PX3,
    EncodingFormat.VOP1,
    EncodingFormat.VOP2,
    EncodingFormat.VOPC,
    EncodingFormat.VOP3,
    EncodingFormat.VOP3_SDST_ENC,
    EncodingFormat.VOP3P,
    EncodingFormat.DS,
    EncodingFormat.SMEM,
    EncodingFormat.MUBUF,
    EncodingFormat.MTBUF,
    EncodingFormat.FLAT,
    EncodingFormat.FLAT_GLBL,
    EncodingFormat.FLAT_SCRATCH,
    EncodingFormat.EXP,
    EncodingFormat.VEXPORT,
    EncodingFormat.MIMG,
    EncodingFormat.MIMG_NSA1,
    EncodingFormat.MIMG_NSA2,
    EncodingFormat.MIMG_NSA3,
    EncodingFormat.VIMAGE,
    EncodingFormat.VSAMPLE,
    EncodingFormat.VBUFFER,
    EncodingFormat.VGLOBAL,
    EncodingFormat.VFLAT,
    EncodingFormat.VSCRATCH,
    EncodingFormat.VDS,
    EncodingFormat.VDSDIR,
    EncodingFormat.LDSDIR,
    EncodingFormat.VINTERP,
    EncodingFormat.VINTRP,
    EncodingFormat.SOPK_INST_LITERAL,
    EncodingFormat.VOP2_INST_LITERAL,
    EncodingFormat.VOP2_INST_LITERAL64,
    EncodingFormat.SOP2_INST_LITERAL,
    EncodingFormat.FLAT_GLOBAL,
    EncodingFormat.VOPDXY,
    EncodingFormat.VOPD3XY
  )
  private val SourceFields = Set(
    EncodingField.SSRC0,
    EncodingField.SSRC1,
    EncodingField.SRC0,
    EncodingField.SRC1,
    EncodingField.SRC2,
    EncodingField.SCALE_SRC0,
    EncodingField.SCALE_SRC1,
    EncodingField.SCALE_SRC2
  )
  private val Flags = Set(
    EncodingField.GLC,
    EncodingField.SLC,
    EncodingField.DLC,
    EncodingField.SC0,
    EncodingField.SC1,
    EncodingField.GDS,
    EncodingField.OFFEN,
    EncodingField.IDXEN,
    EncodingField.LDS,
    EncodingField.TFE,
    EncodingField.CLAMP,
    EncodingField.COMPR,
    EncodingField.DONE,
    EncodingField.VM,
    EncodingField.ROW_EN,
    EncodingField.A16,
    EncodingField.D16,
    EncodingField.DA,
    EncodingField.LWE,
    EncodingField.UNORM,
    EncodingField.R128,
    EncodingField.NV,
    EncodingField.NT,
    EncodingField.ADDR64,
    EncodingField.SCALE_OFFSET
  )
  private val MixedPrecision: Set[Opcode] = Set(
    VFmaMixF32,
    VFmaMixF32Bf16,
    VFmaMixhiBf16,
    VFmaMixhiF16,
    VFmaMixloBf16,
    VFmaMixloF16,
    VMadMixF32,
    VMadMixhiF16,
    VMadMixloF16
  )
  private val NumericModifiers = Set(
    EncodingField.OFFSET,
    EncodingField.OFFSET0,
    EncodingField.OFFSET1,
    EncodingField.FORMAT,
    EncodingField.DFMT,
    EncodingField.NFMT,
    EncodingField.DMASK,
    EncodingField.WAIT_EXP,
    EncodingField.WAIT_VDST,
    EncodingField.WAIT_VA_VDST,
    EncodingField.WAIT_VM_VSRC,
    EncodingField.ABID,
    EncodingField.BYTE_SEL,
    EncodingField.CBSZ,
    EncodingField.BLGP,
    EncodingField.BITOP3,
    EncodingField.SCALE_OP_SEL,
    EncodingField.SCALE_OP_SEL_HI,
    EncodingField.SCALE_NEG,
    EncodingField.SCALE_NEG_HI
  )

  private def supportsLayout(layout: InstructionLayout): Boolean =
    Supported(layout.format) || LaneModifiers.supports(layout.format)

  private def schema(
      value: Metadata.Encoding,
      opcode: String,
      fields: Map[EncodingField, BigInt],
      target: Target,
      waveSize: Int in (32, 64),
      count: Option[Int] = None
  ): Vec[OperandInfo] = {
    val original = value.operands.filterNot(info => info.implicitOperand || info.kind == OperandKind.EXEC).map { info =>
      val matrix = value.groups.exists(group => group == InstructionGroup.WMMA || group == InstructionGroup.SWMMAC)
      val halve  = waveSize.raw == 64 && matrix && (Set(ISA.GFX11_7, ISA.RDNA4)(target.isa) ||
        (Set(ISA.RDNA3, ISA.RDNA3_5)(target.isa) && info.field.exists(field =>
          field == EncodingField.VDST || field == EncodingField.SRC2
        )))
      val sized      = if (halve) info.copy(bits = (info.bits.raw / 2).max(32).unsafe[Int >= 0]) else info
      val normalised =
        if (info.field.contains(EncodingField.IOFFSET)) sized.copy(field = Some(EncodingField.OFFSET)) else sized
      val components = opcode.split('_').last
      if (
        target.isa == ISA.GFX8 && target.name != "gfx810" && opcode.contains("d16") && info.field.contains(
          EncodingField.VDATA
        ) && Set("x", "xy", "xyz", "xyzw")(components)
      )
        info.copy(bits = (32 * components.length).unsafe[Int >= 0])
      else if (opcode.startsWith("ds_gws_") && info.field.contains(EncodingField.ADDR))
        normalised.copy(kind = OperandKind.VGPR_OR_ACCVGPR)
      else normalised
    }
    val returning = fields.getOrElse(EncodingField.GLC, BigInt(0)) != 0 || fields.getOrElse(
      EncodingField.SC0,
      BigInt(0)
    ) != 0 || fields.getOrElse(EncodingField.TH, BigInt(0)).testBit(0)
    val memory =
      value.format.isFlat || Set(EncodingFormat.VFLAT, EncodingFormat.VGLOBAL, EncodingFormat.VSCRATCH)(value.format)
    val data =
      if (
        memory && ((opcode.contains("atomic") && !returning) || fields.getOrElse(
          EncodingField.LDS,
          BigInt(0)
        ) != 0 || opcode.contains("_load_lds_"))
      )
        original.filterNot(_.field.contains(EncodingField.VDST))
      else if (value.format == EncodingFormat.MUBUF && fields.getOrElse(EncodingField.LDS, BigInt(0)) != 0)
        original.filterNot(_.field.contains(EncodingField.VDATA))
      else if (opcode.startsWith("ds_") && opcode.contains("_src2_"))
        original.filter(info => info.field.contains(EncodingField.ADDR))
      else original
    if (
      value.format == EncodingFormat.VFLAT && (count.contains(
        data.size - 1
      ) || fields.get(EncodingField.SADDR).contains(BigInt(Fixed.M0)))
    )
      data.filterNot(_.field.contains(EncodingField.SADDR))
    else data
  }
  private def layout(value: Metadata.Encoding): Option[InstructionLayout] =
    if (value.format == EncodingFormat.VOPDXY || value.format == EncodingFormat.VOPD3XY) None
    else if (
      value.format.isLiteral32 && !value.operands.exists(info =>
        info.field.contains(EncodingField.LITERAL) && info.kind.isImmediate
      )
    ) None
    else InstructionLayout.Catalogue.forFormat(value.isa, value.format).filter(supportsLayout(_))

  def supports(value: Metadata.Encoding): Boolean = layout(value).nonEmpty

  def encode(instruction: Instruction, target: Target, waveSize: Int in (32, 64)): Either[CodecError, Vec[Byte]] = {
    val candidates =
      Metadata.encodingsFor(instruction.definition, target).flatMap(value => layout(value).map(value -> _))
        .sortBy(_._2.bits)
    candidates.view.flatMap { case (value, layout) => encode(instruction, target, value, layout, waveSize) }.headOption
      .toRight(
        if (
          candidates.nonEmpty && candidates.forall(_._2.format == EncodingFormat.SOPP) && instruction.mods.isEmpty &&
          instruction.operands.forall {
            case Operand.IntegerLiteral(_, _) => true
            case _                            => false
          }
        ) CodecError.InvalidOperand(s"${instruction.opcode} requires a 16-bit integer operand")
        else CodecError.UnsupportedOperandSyntax(instruction.opcode, target, instruction.operands)
      )
  }

  private def modifiers(
      instruction: Instruction,
      layout: InstructionLayout,
      operands: Vec[OperandInfo]
  ): Option[Map[EncodingField, BigInt]] =
    instruction.mods.traverse { modifier =>
      val name = modifier.name.toUpperCaseRoot
      if (name == "E32" || name == "E64")
        Option.when(modifier.value.isEmpty && (name == "E64") == layout.format.isVOP3)(Map.empty[EncodingField, BigInt])
      else if (name.startsWith("MATRIX_") && layout.isa == ISA.CDNA5)
        modifier.value.flatMap(SymbolicOperandEncoder.matrix(name, _)).map { case (field, bits) =>
          Map((if (layout.format == EncodingFormat.VOP3P) field match {
                 case EncodingField.SCALE_NEG       => EncodingField.NEG;
                 case EncodingField.SCALE_NEG_HI    => EncodingField.NEG_HI;
                 case EncodingField.SCALE_OP_SEL    => EncodingField.OP_SEL;
                 case EncodingField.SCALE_OP_SEL_HI => EncodingField.OP_SEL_HI; case other => other
               }
               else field) -> bits)
        }
      else if ((name == "MUL" || name == "DIV") && layout.byName.contains(EncodingField.OMOD))
        modifier.value.flatMap(value =>
          (name, value.raw) match {
            case ("MUL", "2") => Some(Map(EncodingField.OMOD -> BigInt(EncodingConstants.OutputModifiers.MUL2)))
            case ("MUL", "4") => Some(Map(EncodingField.OMOD -> BigInt(EncodingConstants.OutputModifiers.MUL4)))
            case ("DIV", "2") => Some(Map(EncodingField.OMOD -> BigInt(EncodingConstants.OutputModifiers.DIV2)))
            case _            => None
          }
        )
      else EncodingField.fromName(if (name == "NEG_LO") "NEG" else name).flatMap { name =>
        if (Set(EncodingField.OP_SEL, EncodingField.OP_SEL_HI, EncodingField.NEG, EncodingField.NEG_HI)(name)) {
          val field = name
          modifier.value.flatMap(bitList(_)).flatMap { raw =>
            val sources = operands.count(info =>
              info.field.exists(field => (SourceFields(field) && !field.isScale) || field == EncodingField.VSRC0)
            )
            val bits = if (name == EncodingField.OP_SEL && !packedFormat(layout))
              (raw & ((BigInt(1) << sources) - 1)) | ((raw >> sources) << 3)
            else raw
            if (name == EncodingField.OP_SEL_HI && layout.byName.contains(EncodingField.OP_SEL_HI_2))
              Some(Map(EncodingField.OP_SEL_HI -> (bits & 3)) ++
                Option.when(modifier.value.exists(_.count(_ == ',') >= 2))(EncodingField.OP_SEL_HI_2 -> (bits >> 2)))
            else Option.when(layout.byName.contains(field))(Map(field ->
              (if (
                 name == EncodingField.OP_SEL_HI && sources == 2 && !instruction.opcode.raw.startsWith(
                   "v_wmma_ld_scale"
                 ) && !MixedPrecision(instruction.definition)
               ) bits | 4
               else bits)))
          }
        } else if (Flags(name) && layout.byName.contains(name))
          Option.when(modifier.value.isEmpty)(Map(name -> BigInt(1)))
        else if (name == EncodingField.FORMAT && modifier.value.exists(_.startsWith("[")))
          modifier.value.flatMap(SymbolicOperandEncoder.bufferFormat(_, layout.isa))
        else if (
          NumericModifiers(name) && (layout.byName.contains(name) || (name == EncodingField.OFFSET && Set(
            EncodingFormat.DS,
            EncodingFormat.VDS
          )(layout.format)))
        )
          modifier.value.flatMap(value =>
            parseBigIntLiteral(value).orElse(Option.when(name == EncodingField.OFFSET)(SymbolicOperandEncoder.swizzle(
              value,
              layout.isa
            )).flatten)
          ).flatMap { raw =>
            val field  = layout.byName.get(name)
            val signed =
              name == EncodingField.OFFSET && Set(
                EncodingFormat.FLAT_GLBL,
                EncodingFormat.FLAT_GLOBAL,
                EncodingFormat.FLAT_SCRATCH,
                EncodingFormat.VGLOBAL,
                EncodingFormat.VFLAT,
                EncodingFormat.VSCRATCH
              )(layout.format)
            val value = if (signed && raw < 0) field.map(f => raw + (BigInt(1) << f.width)).getOrElse(raw) else raw
            Option.when(raw >= 0 || (signed && field.exists(f => raw >= -(BigInt(1) << (f.width - 1))))) {
              if (name == EncodingField.OFFSET && Set(EncodingFormat.DS, EncodingFormat.VDS)(layout.format))
                Map(EncodingField.OFFSET0 -> (value & 255), EncodingField.OFFSET1 -> (value >> 8))
              else Map(name               -> value)
            }
          }
        else None
      }.orElse(ImageOperands.modifier(modifier, layout, instruction.definition))
        .orElse(LaneModifiers.modifier(modifier, layout))
    }.filter(values => values.flatMap(_.keys.toVec).distinct.size == values.map(_.size).sum).map(_.foldLeft(Map.empty[
      EncodingField,
      BigInt
    ])(_ ++ _))

  private def encode(
      instruction: Instruction,
      target: Target,
      value: Metadata.Encoding,
      layout: InstructionLayout,
      waveSize: Int in (32, 64)
  ): Option[Vec[Byte]] =
    if (layout.format == EncodingFormat.SOPP) ProgramOperands.encode(instruction, target, value, layout)
    else encodeFields(instruction, target, value, layout, waveSize)

  private def encodeFields(
      instruction: Instruction,
      target: Target,
      value: Metadata.Encoding,
      layout: InstructionLayout,
      waveSize: Int in (32, 64)
  ): Option[Vec[Byte]] = {
    val allOperands = value.operands.filterNot(_.implicitOperand)
    val parsedMods  = modifiers(instruction, layout, allOperands)
    val operands    = schema(
      value,
      instruction.opcode.raw,
      parsedMods.getOrElse(Map.empty),
      target,
      waveSize,
      Some(instruction.operands.size)
    )
    if (
      (target.isa == ISA.CDNA5 && waveSize.raw == 64 && value.groups.exists(group =>
        group == InstructionGroup.WMMA || group == InstructionGroup.SWMMAC
      )) ||
      (layout.format.isLiteral32 && !operands.exists(info =>
        info.field.contains(EncodingField.LITERAL) && info.kind.isImmediate
      )) || operands.size != instruction.operands.size || !LaneModifiers.selected(instruction, layout)
    ) None
    else if (
      layout.byName.contains(
        EncodingField.ACC
      ) && !layout.format.isVector && operands.zip(instruction.operands).collect {
        case (info, Operand.Register(file, _, _))
            if info.field.exists(Set(
              EncodingField.VDST,
              EncodingField.VDATA,
              EncodingField.DATA,
              EncodingField.DATA0,
              EncodingField.DATA1
            )) => file
      }.distinct.size > 1
    ) None
    else for {
      mods <- parsedMods
      address = operands.zip(instruction.operands).find(_._1.field.contains(EncodingField.SADDR)).flatMap {
        case (info, operand) =>
          encodeOperand(operand, info, target, layout, mods).map(_._1)
      }.getOrElse(Map.empty[EncodingField, BigInt])
      fields <- if (ExportOperands.supports(layout))
        ExportOperands.encode(instruction, mods.get(EncodingField.COMPR).contains(BigInt(1))).map(value =>
          Vec((value, Option.empty[OperandCodec.Literal]))
        )
      else if (ImageOperands.supports(layout))
        ImageOperands.encode(instruction, layout, mods).map(value => Vec((value, Option.empty[OperandCodec.Literal])))
      else operands.zip(instruction.operands).traverse { case (info, operand) =>
        encodeOperand(operand, info, target, layout, mods ++ address, MixedPrecision(instruction.definition))
      }
      literals = fields.flatMap(_._2).distinct
      if !layout.byName.contains(EncodingField.LITERAL) || literals.forall(literal =>
        fields.exists(_._1.get(EncodingField.LITERAL).contains(BigInt(literal.value)))
      )
      if literals.size <= 1 && !(literals.nonEmpty && Set(
        ISA.GFX6,
        ISA.GFX7,
        ISA.GFX8,
        ISA.GFX9
      )(layout.isa) && layout.format.isVOP3)
      merged <-
        fields.foldLeft(Option(
          mods ++ Option.when(layout.byName.contains(EncodingField.OP))(EncodingField.OP -> BigInt(value.opcode))
        )) {
          case (result, (fields, _)) =>
            fields.foldLeft(result) { case (values, (key, value)) =>
              values.flatMap { previous =>
                Option.when(!previous.contains(key) || previous(
                  key
                ) == value || key == EncodingField.NEG || key == EncodingField.NEG_HI || key == EncodingField.ABS || key == EncodingField.OP_SEL || key == EncodingField.ACC)(
                  previous.updated(key, previous.getOrElse(key, BigInt(0)) | value)
                )
              }
            }
        }
      noAddresses = layout.format == EncodingFormat.FLAT_SCRATCH && Set(ISA.RDNA1, ISA.RDNA2)(layout.isa) &&
        operands.zip(instruction.operands).filter { case (info, _) =>
          info.field.contains(EncodingField.ADDR) || info.field.contains(EncodingField.SADDR)
        }
          .forall { case (_, operand) =>
            operand == Operand.Identifier("off") || operand == Operand.SpecialRegister("null")
          }
      addressFields = if (noAddresses) merged.updated(EncodingField.SADDR, BigInt(127)) else merged
      scratch       =
        if (
          Set(
            EncodingFormat.FLAT_SCRATCH,
            EncodingFormat.VSCRATCH
          )(layout.format) && layout.byName.contains(EncodingField.SVE) && !operands.zip(instruction.operands).exists {
            case (info, operand) =>
              info.field.exists(field =>
                field == EncodingField.ADDR || field == EncodingField.VADDR
              ) && operand == Operand.Identifier("off")
          }
        ) Map(EncodingField.SVE -> BigInt(1))
        else Map.empty[EncodingField, BigInt]
      buffer = if (Set(EncodingFormat.MTBUF, EncodingFormat.VBUFFER)(layout.format))
        Map(
          (if (layout.byName.contains(EncodingField.FORMAT)) EncodingField.FORMAT else EncodingField.DFMT) -> BigInt(1)
        )
      else Map.empty[EncodingField, BigInt]
      defaults = LaneModifiers.defaults(layout) ++
        (if (
           Set(ISA.RDNA3, ISA.RDNA3_5, ISA.RDNA4, ISA.GFX11_7)(layout.isa) && Set[Opcode](
             VDot4I32I8,
             VDot8I32I4
           )(instruction.definition)
         )
           Map(EncodingField.NEG -> BigInt(3))
         else Map.empty[
           EncodingField,
           BigInt
         ]) ++ buffer ++ scratch ++ value.operands.filter(_.kind == OperandKind.EXEC).flatMap(
          _.field
        ).map(_ -> BigInt(Fixed.ExecLo)).toMap ++ (if (
                                                     layout.format == EncodingFormat.VOP3PX2 && layout.isa == ISA.CDNA4
                                                   )
                                                     Map(EncodingField.ABID -> BigInt(1))
                                                   else Map.empty[EncodingField, BigInt]) ++
        (if (layout.format == EncodingFormat.VINTERP) Map(EncodingField.WAIT_EXP -> BigInt(7))
         else Map.empty[EncodingField, BigInt]) ++
        (if (Set(EncodingFormat.LDSDIR, EncodingFormat.VDSDIR)(layout.format))
           Map((if (layout.format == EncodingFormat.VDSDIR) EncodingField.WAIT_VA_VDST
                else EncodingField.WAIT_VDST) -> BigInt(15))
         else Map.empty[EncodingField, BigInt]) ++
        (if (
           layout.format.isVOP3 && (layout.isa.isRDNA || Set(
             ISA.CDNA5,
             ISA.GFX11_7
           )(layout.isa)) && (!LaneModifiers.supports(
             layout.format
           ) || value.operands.exists(_.kind == OperandKind.EXEC))
         )
           SourceFields.filter(layout.byName.contains(_)).filterNot(field =>
             merged.contains(field) || (field == EncodingField.SRC0 && LaneModifiers.supports(layout.format))
           ).map(_ -> BigInt(128)).toMap
         else Map.empty[EncodingField, BigInt]) ++
        (if (
           (Set(
             EncodingFormat.FLAT,
             EncodingFormat.VFLAT,
             EncodingFormat.VGLOBAL,
             EncodingFormat.VSCRATCH
           )(layout.format)) && (layout.isa.isRDNA || Set(
             ISA.CDNA5,
             ISA.GFX11_7
           )(layout.isa))
         )
           Map(EncodingField.SADDR -> BigInt(nullRegister(layout)))
         else Map.empty[EncodingField, BigInt])
      packed =
        if (
          packedFormat(layout) && layout.byName.contains(EncodingField.OP_SEL_HI) && !MixedPrecision(
            instruction.definition
          ) && !instruction.opcode.raw.endsWith("_f8f6f4") && !instruction.opcode.raw.startsWith("v_wmma_ld_scale")
        )
          if (layout.byName.contains(EncodingField.OP_SEL_HI_2))
            Map(EncodingField.OP_SEL_HI    -> BigInt(3), EncodingField.OP_SEL_HI_2 -> BigInt(1))
          else Map(EncodingField.OP_SEL_HI -> BigInt(if (
            layout.isa == ISA.CDNA5 && !instruction.opcode.raw.endsWith(
              "_f4"
            ) && (instruction.opcode.raw.startsWith("v_wmma_") || instruction.opcode.raw.startsWith("v_swmmac_"))
          ) 3
          else 7))
        else Map.empty[EncodingField, BigInt]
      parts <- (packed ++ defaults ++ addressFields).toVector.traverse { case (field, number) =>
        layout.byName.get(field).flatMap(_.put(number))
      }
      word = parts.foldLeft(layout.fixedPrefix)(_ | _)
      if (word & layout.fixedMask) == layout.fixedPrefix
      if layout.accepts(
        InstructionWord.from(word),
        if (layout.byName.contains(EncodingField.LITERAL) || layout.byName.contains(EncodingField.LITERAL64)) 0
        else literals.headOption.fold(0)(_.bytes.size)
      )
    } yield Vec.from((0 until layout.bits / 8).map(index => ((word >> (8 * index)) & 255).toByte)) ++
      (if (layout.byName.contains(EncodingField.LITERAL) || layout.byName.contains(EncodingField.LITERAL64))
         Vec.empty[Byte]
       else literals.flatMap(_.bytes))
  }

  private def bitList(value: String): Option[BigInt] =
    Option.when(
      value.startsWith("[") && value.endsWith("]")
    )(value.drop(1).dropRight(1).split(',').toVector.map(_.trim))
      .filter(bits => bits.nonEmpty && bits.size <= 4 && bits.forall(bit => bit == "0" || bit == "1"))
      .map(_.zipWithIndex.foldLeft(BigInt(0)) { case (value, (bit, index)) => value | (BigInt(bit.toInt) << index) })

  private def packedFormat(layout: InstructionLayout): Boolean =
    LaneModifiers.base(layout) == EncodingFormat.VOP3P || (layout.isa == ISA.CDNA5 && Set(
      EncodingFormat.VOP3PX2,
      EncodingFormat.VOP3PX3
    )(layout.format))

  private def registerWidth(info: OperandInfo, layout: InstructionLayout, fields: Map[EncodingField, BigInt]): Int =
    if (
      layout.format == EncodingFormat.VOP3PX2 && layout.isa == ISA.CDNA4 && info.field.exists(field =>
        field == EncodingField.SRC0 || field == EncodingField.SRC1
      )
    ) {
      val format = fields.getOrElse(
        if (info.field.contains(EncodingField.SRC0)) EncodingField.CBSZ else EncodingField.BLGP,
        BigInt(0)
      ).toInt
      (if (format <= 1) 8 else if (format <= 3) 6 else 4) * 32
    } else if (
      layout.isa == ISA.CDNA5 && Set(EncodingFormat.VOP3PX2, EncodingFormat.VOP3PX3)(layout.format) && fields.getOrElse(
        EncodingField.OP_SEL_HI,
        BigInt(0)
      ) < 5 && info.field.exists(field => field == EncodingField.SRC0 || field == EncodingField.SRC1)
    ) {
      val format = fields.getOrElse(
        if (info.field.contains(EncodingField.SRC0)) EncodingField.OP_SEL else EncodingField.OP_SEL_HI,
        BigInt(0)
      ).toInt
      val bits = if (format <= 1) 8 else if (format <= 3) 6 else 4
      info.bits.raw * bits / 8
    } else if (
      info.field.contains(EncodingField.VDATA) && Set(
        EncodingFormat.MUBUF,
        EncodingFormat.MTBUF,
        EncodingFormat.VBUFFER
      )(layout.format)
    ) info.bits.raw.max(32) +
      (if (fields.get(EncodingField.TFE).contains(BigInt(1))) 32 else 0)
    else if (
      info.field.contains(EncodingField.VADDR) && Set(
        EncodingFormat.MUBUF,
        EncodingFormat.MTBUF,
        EncodingFormat.VBUFFER
      )(layout.format)
    )
      if (fields.get(EncodingField.ADDR64).contains(BigInt(1))) 64
      else 32 * (fields.getOrElse(
        EncodingField.OFFEN,
        BigInt(0)
      ).toInt + fields.getOrElse(EncodingField.IDXEN, BigInt(0)).toInt).max(1)
    else if (
      info.field.exists(field => field == EncodingField.ADDR || field == EncodingField.VADDR) && Set(
        EncodingFormat.FLAT_SCRATCH,
        EncodingFormat.VSCRATCH
      )(layout.format)
    ) 32
    else if (
      info.field.exists(field => field == EncodingField.ADDR || field == EncodingField.VADDR) && Set(
        EncodingFormat.FLAT_GLBL,
        EncodingFormat.FLAT_GLOBAL,
        EncodingFormat.VGLOBAL,
        EncodingFormat.VFLAT
      )(layout.format) &&
      fields.get(EncodingField.SADDR).exists(_ != nullRegister(layout))
    ) 32
    else info.bits.raw.max(32)

  private def nullRegister(layout: InstructionLayout): Int =
    if ((layout.isa.isCDNA && layout.isa != ISA.CDNA5) || Set(ISA.GFX6, ISA.GFX7, ISA.GFX8, ISA.GFX9)(layout.isa)) 127
    else if (Set(ISA.RDNA3, ISA.RDNA3_5, ISA.RDNA4, ISA.CDNA5, ISA.GFX11_7)(layout.isa)) Fixed.M0 else Fixed.NullSgpr

  private def scalarRegister(kind: OperandKind): Boolean =
    kind.isScalarRegister || kind.isScalarSource || kind.isScalarDestination ||
      Set(
        OperandKind.SRC,
        OperandKind.SRC_SIMPLE,
        OperandKind.SRC_NOLIT,
        OperandKind.SRC_NOLDS,
        OperandKind.SRC_NOINLINE,
        OperandKind.SMEM_OFFSET_NOK
      )(kind)

  private def vectorRegister(kind: OperandKind): Boolean =
    kind.isVector ||
      Set(
        OperandKind.SRC,
        OperandKind.SRC_SIMPLE,
        OperandKind.SRC_NOLIT,
        OperandKind.SRC_NOLDS,
        OperandKind.SRC_NOINLINE,
        OperandKind.SRC_VGPR,
        OperandKind.SRC_VGPR_OR_INLINE
      )(kind)

  private def encodeOperand(
      operand: Operand,
      info: OperandInfo,
      target: Target,
      layout: InstructionLayout,
      mods: Map[EncodingField, BigInt],
      mixed: Boolean = false
  ): Option[(Map[EncodingField, BigInt], Option[OperandCodec.Literal])] = {
    info.field match {
      case None => Option.when(info.kind == OperandKind.VCC && (operand == Operand.SpecialRegister(
          "vcc"
        ) || operand == Operand.SpecialRegister("vcc_lo")))((Map.empty[EncodingField, BigInt], None))
      case Some(field) =>
        def result(value: BigInt, literal: Option[OperandCodec.Literal] = None) = Some((Map(field -> value), literal))
        if (
          layout.byName.contains(EncodingField.ACC) && !layout.format.isVector && (Set(
            EncodingField.VDST,
            EncodingField.VDATA,
            EncodingField.DATA,
            EncodingField.DATA0,
            EncodingField.DATA1
          )(field) || info.kind == OperandKind.VGPR_OR_ACCVGPR)
        ) operand match {
          case Operand.Register(RegisterFile.Accumulator, first, last) =>
            encodeOperand(
              Operand.Register(RegisterFile.VectorRegister, first, last),
              info,
              target,
              layout.copy(fields = layout.fields.filterNot(_.name == EncodingField.ACC)),
              mods
            )
              .map { case (values, literal) => (values.updated(EncodingField.ACC, BigInt(1)), literal) }
          case Operand.Register(RegisterFile.VectorRegister, _, _) =>
            encodeOperand(
              operand,
              info,
              target,
              layout.copy(fields = layout.fields.filterNot(_.name == EncodingField.ACC)),
              mods
            )
          case _ => None
        }
        else if (
          (layout.format == EncodingFormat.VOP3P_MFMA || (layout.format == EncodingFormat.VOP3PX2 && layout.isa == ISA.CDNA4)) && !field.isScale
        ) operand match {
          case Operand.Register(file, first, last)
              if (file == RegisterFile.VectorRegister || file == RegisterFile.Accumulator) && first >= 0 && first < 256 && last - first + 1 == (registerWidth(
                info,
                layout,
                mods
              ) + 31) / 32 =>
            val accumulator = file == RegisterFile.Accumulator
            val cd          = field == EncodingField.VDST || field == EncodingField.SRC2
            if ((accumulator && !info.kind.acceptsAccumulator) || (!accumulator && info.kind.isAccumulator)) None
            else if (cd && !layout.byName.contains(EncodingField.ACC_CD) && !accumulator) None
            else Some((
              Map(field -> BigInt(first + (if (SourceFields(field)) 256 else 0))) ++
                (if (cd && layout.byName.contains(EncodingField.ACC_CD))
                   Map(EncodingField.ACC_CD -> BigInt(if (accumulator) 1 else 0))
                 else if (!cd && accumulator) Map(EncodingField.ACC -> (BigInt(1) << field.index))
                 else Map.empty[EncodingField, BigInt]),
              None
            ))
          case _ if info.kind.acceptsConstant =>
            OperandCodec.encode(operand, info, target).filter(_._2.isEmpty).map { case (value, literal) =>
              (Map(field -> BigInt(value)), literal)
            }
          case _ => None
        }
        else if (info.kind == OperandKind.ATTR) operand match {
          case Operand.Identifier(s"attr$index.$channel") => for {
              index   <- index.toIntOption.filter(value => value >= 0 && value < 64)
              channel <- Option.when(channel.length == 1)("xyzw".indexOf(channel)).filter(_ >= 0)
            } yield (
              if (field == EncodingField.ATTR)
                Map(EncodingField.ATTR -> BigInt(index), EncodingField.ATTR_CHAN -> BigInt(channel))
              else Map(field           -> BigInt(index | (channel << 6))),
              None
            )
          case _ => None
        }
        else if (info.kind == OperandKind.PARAM) operand match {
          case Operand.Identifier(name) => Vector("p10", "p20", "p0").indexOf(name) match {
              case index if index >= 0 => result(index)
              case _                   => None
            }
          case _ => None
        }
        else if (field == EncodingField.SDST && LaneModifiers.sdwa(layout)) {
          OperandCodec.encode(operand, info, target).map { case (value, literal) =>
            (
              if (value == Fixed.VccLo) Map(EncodingField.SD -> BigInt(0))
              else Map(EncodingField.SDST                    -> BigInt(value), EncodingField.SD -> BigInt(1)),
              literal
            )
          }
        } else if (
          field == EncodingField.SOFFSET && layout.format == EncodingFormat.VBUFFER && operand == Operand.SpecialRegister(
            "src_scc"
          )
        )
          result(Fixed.SccSource & 0x7f)
        else if (
          field == EncodingField.SOFFSET && layout.format == EncodingFormat.VBUFFER && operand == Operand.IntegerLiteral(
            BigInt(0),
            10
          )
        )
          result(nullRegister(layout))
        else if (field == EncodingField.LITERAL64) operand match {
          case Operand.IntegerLiteral(value, _) if value >= Int.MinValue && value <= BigInt("ffffffff", 16) =>
            result((value & BigInt("ffffffff", 16)) << 32)
          case Operand.Expression(value) if value.startsWith("lit64(") && value.endsWith(")") =>
            OperandCodec.encode(operand, info.copy(field = Some(EncodingField.SRC0)), target).flatMap(_._2).flatMap(
              literal =>
                result(BigInt(literal.value) & ((BigInt(1) << 64) - 1))
            )
          case _ => None
        }
        else if (field == EncodingField.LITERAL) operand match {
          case Operand.IntegerLiteral(value, _) if value >= Int.MinValue && value <= BigInt("ffffffff", 16) =>
            result(value & BigInt("ffffffff", 16))
          case Operand.Expression(value) if value.startsWith("lit(") && value.endsWith(")") =>
            OperandCodec.encode(operand, info.copy(field = Some(EncodingField.SRC0)), target).flatMap(_._2).flatMap(
              literal =>
                result(BigInt(literal.value))
            )
          case _ => None
        }
        else if (info.kind.isMessage || info.kind == OperandKind.SIMM4)
          OperandCodec.encode(operand, info, target).map { case (value, literal) =>
            (Map(field -> BigInt(value)), literal)
          }
        else if (info.kind.isImmediate && field != EncodingField.SIMM16 && field != EncodingField.LITERAL)
          operand match {
            case Operand.IntegerLiteral(value, _) => result(value)
            case _                                => None
          }
        else if (info.kind == OperandKind.HWREG)
          SymbolicOperandEncoder.encode(operand, info.kind, target).flatMap(result(_))
        else if (field == EncodingField.SIMM16) operand match {
          case Operand.IntegerLiteral(value, _) if value >= Short.MinValue && value <= 65535 => result(value & 65535)
          case _                                                                             => None
        }
        else if (field == EncodingField.SOFFSET && info.kind == OperandKind.SMEM_OFFSET) operand match {
          case Operand.IntegerLiteral(value, _)
              if value >= 256 && layout.bits == 32 && layout.isa == ISA.GFX7 && value <= BigInt("ffffffff", 16) =>
            Some((Map(EncodingField.OFFSET -> BigInt(Source.LITERAL_CONST)), Some(OperandCodec.Literal(value.toLong))))
          case Operand.IntegerLiteral(value, _) if value >= 0 =>
            Some((
              if (layout.byName.contains(EncodingField.IMM))
                Map(EncodingField.IMM        -> BigInt(1), EncodingField.OFFSET                    -> value)
              else Map(EncodingField.SOFFSET -> BigInt(nullRegister(layout)), EncodingField.OFFSET -> value),
              None
            ))
          case _ => OperandCodec.encode(operand, info.copy(field = Some(EncodingField.SDST)), target).map {
              case (value, literal) =>
                (
                  Map(
                    (if (layout.byName.contains(EncodingField.IMM)) EncodingField.OFFSET else field) -> BigInt(value)
                  ),
                  literal
                )
            }
        }
        else if (
          (operand == Operand.Identifier("off") || operand == Operand.SpecialRegister(
            "null"
          )) && (field == EncodingField.VADDR || field == EncodingField.ADDR || field == EncodingField.SADDR)
        )
          if (field == EncodingField.SADDR) result(nullRegister(layout))
          else Option.when(mods.getOrElse(
            EncodingField.OFFEN,
            BigInt(0)
          ) == 0 && mods.getOrElse(EncodingField.IDXEN, BigInt(0)) == 0)((Map(field -> BigInt(0)), None))
        else operand match {
          case Operand.Register(RegisterFile.Accumulator, first, last)
              if Set(
                OperandKind.ACCVGPR,
                OperandKind.SRC_ACCVGPR
              )(info.kind) && first == last && first >= 0 && first < 256 =>
            result(first + (if (SourceFields(field)) 256 else 0))
          case Operand.Register(RegisterFile.Scalar, first, last)
              if info.bits.raw == 64 && first == last && first >= 0 && first < 128 && (field == EncodingField.SDST || (field == EncodingField.VDST && scalarRegister(
                info.kind
              )) || (field == EncodingField.SRC2 && info.kind == OperandKind.SREG)) => result(first)
          case Operand.SpecialRegister("vcc_lo" | "vcc_hi" | "exec_lo" | "exec_hi")
              if info.bits.raw == 64 && (field == EncodingField.SDST || field == EncodingField.VDST || (field == EncodingField.SRC2 && info.kind == OperandKind.SREG)) =>
            OperandCodec.encode(operand, info.copy(bits = 32), target).flatMap { case (value, literal) =>
              result(value, literal)
            }
          case Operand.Half(Operand.Register(RegisterFile.VectorRegister, first, last), high)
              if !layout.byName.contains(
                EncodingField.OP_SEL
              ) && first == last && first >= 0 && first < 128 && info.bits.raw == 16 =>
            result(first | (if (high) 128 else 0) | (if (SourceFields(field)) 256 else 0))
          case Operand.Half(inner, high) if layout.byName.contains(EncodingField.OP_SEL) =>
            val index =
              if (field == EncodingField.VDST) 3
              else if (field == EncodingField.VSRC0) 0 else if (SourceFields(field)) field.index else -1
            Option.when(index >= 0)(index).flatMap(index =>
              encodeOperand(inner, info, target, layout, mods, mixed).map { case (values, literal) =>
                (values ++ Option.when(high)(EncodingField.OP_SEL -> (BigInt(1) << index)), literal)
              }
            )
          case Operand.Negated(inner)
              if field == EncodingField.VSRC0 && LaneModifiers.base(
                layout
              ) == EncodingFormat.VOP3 && layout.byName.contains(EncodingField.NEG) =>
            encodeOperand(inner, info, target, layout, mods, mixed).map { case (values, literal) =>
              (values + (EncodingField.NEG -> BigInt(1)), literal)
            }
          case Operand.Absolute(inner)
              if field == EncodingField.VSRC0 && LaneModifiers.base(
                layout
              ) == EncodingFormat.VOP3 && layout.byName.contains(EncodingField.ABS) =>
            encodeOperand(inner, info, target, layout, mods, mixed).map { case (values, literal) =>
              (values + (EncodingField.ABS -> BigInt(1)), literal)
            }
          case Operand.Negated(inner) if field.negativeModifier.exists(layout.byName.contains) =>
            encodeOperand(inner, info, target, layout, mods, mixed).map { case (values, literal) =>
              (values ++ field.negativeModifier.map(_ -> BigInt(1)), literal)
            }
          case Operand.Absolute(inner) if field.absoluteModifier.exists(layout.byName.contains) =>
            encodeOperand(inner, info, target, layout, mods, mixed).map { case (values, literal) =>
              (values ++ field.absoluteModifier.map(_ -> BigInt(1)), literal)
            }
          case Operand.SignExtended(inner)
              if field.extensionModifier.exists(layout.byName.contains) =>
            encodeOperand(inner, info, target, layout, mods, mixed).map { case (values, literal) =>
              (values ++ field.extensionModifier.map(_ -> BigInt(1)), literal)
            }
          case Operand.Negated(inner) if SourceFields(field) && layout.byName.contains(EncodingField.NEG) =>
            encodeOperand(inner, info, target, layout, mods, mixed).map { case (values, literal) =>
              (values + (EncodingField.NEG -> (BigInt(1) << field.index)), literal)
            }
          case Operand.Absolute(inner)
              if mixed && SourceFields(field) && layout.byName.contains(EncodingField.NEG_HI) =>
            encodeOperand(inner, info, target, layout, mods, mixed).map { case (values, literal) =>
              (values + (EncodingField.NEG_HI -> (BigInt(1) << field.index)), literal)
            }
          case Operand.Absolute(inner) if SourceFields(field) && layout.byName.contains(EncodingField.ABS) =>
            encodeOperand(inner, info, target, layout, mods, mixed).map { case (values, literal) =>
              (values + (EncodingField.ABS -> (BigInt(1) << field.index)), literal)
            }
          case Operand.Register(RegisterFile.VectorRegister, first, last)
              if vectorRegister(info.kind) && first >= 0 && first < 256 &&
                last - first + 1 ==
                (registerWidth(info, layout, mods) + 31) / 32 =>
            result(first + (if (SourceFields(field)) 256 else 0))
          case Operand.Register(RegisterFile.Scalar, first, last)
              if scalarRegister(info.kind) && first >= 0 && last < 128 &&
                last - first + 1 ==
                (info.bits.raw + 31) / 32 &&
                (info.bits.raw <= 32 || first % 2 == 0) =>
            if (LaneModifiers.sdwa(layout) && field.isVectorSource)
              Some((Map(field -> BigInt(first), EncodingField.ScalarSelectors(field.index) -> BigInt(1)), None))
            else result(first)
          case Operand.Register(RegisterFile.TrapTemporary, _, _) if scalarRegister(info.kind) =>
            OperandCodec.encode(operand, info, target).map { case (value, literal) =>
              (Map(field -> BigInt(value)), literal)
            }
          case Operand.SpecialRegister("src_lds_direct") if info.kind == OperandKind.VGPR_OR_LDS =>
            result(Fixed.LdsDirectSource)
          case Operand.Register(_, _, _)                                                                   => None
          case Operand.SpecialRegister("null") if info.kind == OperandKind.SREG_NONULL                     => None
          case Operand.SpecialRegister("vcc" | "vcc_lo" | "vcc_hi") if info.kind == OperandKind.SREG_NOVCC => None
          case _ if scalarRegister(info.kind) || info.kind == OperandKind.SRC_VGPR_OR_INLINE               =>
            val source = SourceFields(field) || field == EncodingField.SOFFSET || info.kind.isSource
            OperandCodec.encode(
              operand,
              info.copy(field = Some(if (source) EncodingField.SSRC0 else EncodingField.SDST)),
              target
            )
              .filter { case (value, literal) =>
                (literal.isEmpty || (SourceFields(field) && !info.kind.excludesLiteral)) &&
                (info.kind != OperandKind.SRC_VGPR_OR_INLINE || (value >= 128 && value < Source.LITERAL_CONST)) &&
                (info.kind != OperandKind.SRC_NOINLINE || value < 128 || value == Source.LITERAL_CONST)
              }
              .map { case (value, literal) =>
                (
                  Map(field -> BigInt(value)) ++ (if (LaneModifiers.sdwa(layout) && field.isVectorSource)
                                                    Map(EncodingField.ScalarSelectors(field.index) -> BigInt(1))
                                                  else Map.empty[EncodingField, BigInt]),
                  literal
                )
              }
          case _ => None
        }
    }
  }

  def decode(
      bytes: Array[Byte],
      offset: Int,
      target: Target,
      waveSize: Int in (32, 64)
  ): Either[CodecError, DecodedInstruction] = {
    val candidates = InstructionDecoder.candidates(bytes, offset, target)
      .filter(candidate => supportsLayout(candidate.layout))
      .flatMap(decodeOperands(bytes, offset, target, _, waveSize).toVec).distinct
    val canonical = OpcodeIndex.canonical(candidates, target)(_.instruction.definition).filterNot(value =>
      value.instruction.opcode.raw.startsWith("v_wmma_ld_scale") && candidates.exists(_.size.raw == 16)
    )
    canonical match {
      case Vec(value) => Right(value)
      case Vec()      =>
        Left(CodecError.InvalidEncoding(s"unknown or unsupported instruction at byte $offset for ${target.name}"))
      case _ =>
        Left(CodecError.InvalidEncoding(s"ambiguous instruction at byte $offset for ${target.name}: ${canonical.map(
            value => s"${value.instruction.opcode} ${value.instruction.operands}"
          ).mkString("; ")}"))
    }
  }

  private def decodeOperands(
      bytes: Array[Byte],
      offset: Int,
      target: Target,
      decoded: InstructionDecoder.Decoded,
      waveSize: Int in (32, 64)
  ): Option[DecodedInstruction] = {
    val layout     = decoded.layout
    val word       = decoded.word
    val definition = decoded.definition
    val value      = decoded.encoding
    val fields     = layout.fields.map(field => field.name -> BigInt(field.get(word))).toMap
    val operands   = schema(value, definition.opcode.raw, fields, target, waveSize)
    val literal    = decoded.literal.flatMap(bits =>
      decoded.literalBytes.refine[Int in (4, 8)].toOption.map(OperandCodec.Literal(bits, _))
    )
    val memoryLiteral = layout.format == EncodingFormat.SMEM && layout.bits == 32 && layout.isa == ISA.GFX7 &&
      layout.get(word, EncodingField.IMM) == 0 && layout.get(word, EncodingField.OFFSET) == Source.LITERAL_CONST
    def bit(value: Long, index: Int): Boolean = ((value >>> index) & 1) != 0
    val mods                                  = layout.fields.toVec.flatMap { field =>
      val value = field.get(word)
      if (
        value == 0 && field.name != EncodingField.OP_SEL_HI && field.name != EncodingField.DFMT &&
        field.name != EncodingField.FORMAT && !field.name.isWait
      ) Vec.empty[Modifier]
      else if (Flags(field.name)) Vec(Modifier(field.name.assemblyName))
      else if (NumericModifiers(field.name))
        Vec(Modifier(field.name.assemblyName, Some(value.toString.unsafe[NonEmpty[String]])))
      else if (
        field.name == EncodingField.OP_SEL || field.name == EncodingField.NEG_HI ||
        (field.name == EncodingField.NEG && packedFormat(layout)) || field.name == EncodingField.OP_SEL_HI
      ) {
        val name    = if (field.name == EncodingField.NEG) "neg_lo" else field.name.toString.toLowerCaseRoot
        val sources = operands.count(info =>
          info.field.exists(field => (SourceFields(field) && !field.isScale) || field == EncodingField.VSRC0)
        )
        val width =
          if (field.name == EncodingField.OP_SEL_HI)
            field.width + (if (layout.byName.contains(EncodingField.OP_SEL_HI_2)) 1 else 0)
          else if (packedFormat(layout)) sources
          else if (field.name == EncodingField.OP_SEL) sources + 1
          else field.width
        val bits =
          if (field.name == EncodingField.OP_SEL_HI) value | (layout.get(word, EncodingField.OP_SEL_HI_2) << 2)
          else if (field.name == EncodingField.OP_SEL && !packedFormat(layout))
            (value & ((1L << sources) - 1)) | ((value >>> 3) << sources)
          else value
        Vec(Modifier(
          name.unsafe[NonEmpty[String]],
          Some((0 until width).map(index => if (bit(bits, index)) "1" else "0").mkString("[", ",", "]")
            .unsafe[NonEmpty[String]])
        ))
      } else if (field.name == EncodingField.OMOD)
        Vec(if (value == EncodingConstants.OutputModifiers.DIV2) Modifier("div", Some("2"))
        else Modifier("mul", Some((1 << value.toInt).toString.unsafe[NonEmpty[String]])))
      else Vec.empty[Modifier]
    } ++
      (if (LaneModifiers.base(layout) == EncodingFormat.VOP3) Vec(Modifier("e64")) else Vec.empty[Modifier]) ++
      LaneModifiers.modifiers(word, layout) ++ ImageOperands.modifiers(word, layout)
    val decodedOperands = if (layout.format == EncodingFormat.SOPP)
      ProgramOperands.decode(definition, word, layout, target)
    else if (ExportOperands.supports(layout)) ExportOperands.decode(word, layout)
    else if (ImageOperands.supports(layout)) ImageOperands.decode(definition, word, layout)
    else operands.traverse { info =>
      info.field match {
        case None        => Option.when(info.kind == OperandKind.VCC)(Operand.SpecialRegister("vcc"))
        case Some(field) =>
          val number = layout.get(word, field).toInt
          val bits   = registerWidth(info, layout, fields)
          val base   = if (Set(OperandKind.ACCVGPR, OperandKind.SRC_ACCVGPR)(info.kind)) Some(Operand.Register(
            RegisterFile.Accumulator,
            if (SourceFields(field)) number - 256 else number,
            (if (SourceFields(field)) number - 256 else number) + (bits + 31) / 32 - 1
          ))
          else if (
            (layout.format == EncodingFormat.VOP3P_MFMA ||
              (layout.format == EncodingFormat.VOP3PX2 && layout.isa == ISA.CDNA4)) && !field.isScale &&
            (field == EncodingField.VDST || number >= 256)
          ) {
            val cd          = field == EncodingField.VDST || field == EncodingField.SRC2
            val accumulator =
              if (cd) !layout.byName.contains(EncodingField.ACC_CD) || layout.get(word, EncodingField.ACC_CD) != 0
              else bit(layout.get(word, EncodingField.ACC), field.index)
            val first = if (field == EncodingField.VDST) number else number - 256
            Some(Operand.Register(
              if (accumulator) RegisterFile.Accumulator else RegisterFile.VectorRegister,
              first,
              first + (bits + 31) / 32 - 1
            ))
          } else if (info.kind == OperandKind.ATTR) {
            val index   = if (field == EncodingField.ATTR) number else number & 63
            val channel =
              if (field == EncodingField.ATTR) layout.get(word, EncodingField.ATTR_CHAN).toInt else number >> 6
            Some(Operand.Identifier(s"attr$index.${"xyzw" (channel)}"))
          } else if (info.kind == OperandKind.PARAM)
            Vector("p10", "p20", "p0").lift(number).map(Operand.Identifier(_))
          else if (field == EncodingField.SDST && LaneModifiers.sdwa(layout) && layout.get(word, EncodingField.SD) == 0)
            Some(Operand.SpecialRegister("vcc"))
          else if (LaneModifiers.sdwa(layout) && field.isVectorSource) {
            if (layout.get(word, EncodingField.ScalarSelectors(field.index)) == 0)
              Some(Operand.Register(RegisterFile.VectorRegister, number, number + (bits + 31) / 32 - 1))
            else OperandCodec.decode(number, info.copy(field = Some(EncodingField.SSRC0)), literal, target)
          } else if (
            field == EncodingField.SOFFSET && layout.format == EncodingFormat.VBUFFER &&
            number == (Fixed.SccSource & 0x7f)
          )
            Some(Operand.SpecialRegister("src_scc"))
          else if (
            field == EncodingField.SOFFSET && layout.format == EncodingFormat.VBUFFER && number == nullRegister(layout)
          )
            Some(Operand.IntegerLiteral(BigInt(0), 10))
          else if (field == EncodingField.LITERAL64) {
            val literal = layout.get(word, field)
            Some(
              if ((literal & 0xffffffffL) == 0) Operand.IntegerLiteral(BigInt(literal >>> 32), 16)
              else Operand.Expression(s"lit64(0x${java.lang.Long.toHexString(literal)})")
            )
          } else if (field == EncodingField.LITERAL) Some(Operand.IntegerLiteral(BigInt(layout.get(word, field)), 16))
          else if (info.kind.isImmediate && field != EncodingField.SIMM16)
            Some(Operand.IntegerLiteral(BigInt(number), 10))
          else if (info.kind == OperandKind.HWREG)
            Some(Operand.Expression(s"hwreg(${number & 63}, ${(number >> 6) & 31}, ${(number >> 11) + 1})"))
          else if (field == EncodingField.SIMM16) Some(Operand.IntegerLiteral(BigInt(number.toShort.toInt), 10))
          else if (
            field == EncodingField.SOFFSET && info.kind == OperandKind.SMEM_OFFSET &&
            layout.byName.contains(EncodingField.IMM)
          )
            if (memoryLiteral) literal.map(value => Operand.IntegerLiteral(BigInt(value.value), 16))
            else if (layout.get(word, EncodingField.IMM) == 1)
              Some(Operand.IntegerLiteral(BigInt(layout.get(word, EncodingField.OFFSET)), 10))
            else OperandCodec.decode(
              layout.get(word, EncodingField.OFFSET).toInt,
              info.copy(field = Some(EncodingField.SDST)),
              None,
              target
            )
          else if (
            field == EncodingField.SOFFSET && info.kind == OperandKind.SMEM_OFFSET && number == nullRegister(layout)
          )
            Some(Operand.IntegerLiteral(BigInt(layout.get(word, EncodingField.OFFSET)), 10))
          else if (
            (field == EncodingField.ADDR || field == EncodingField.VADDR) &&
            Set(EncodingFormat.VSCRATCH, EncodingFormat.FLAT_SCRATCH)(layout.format) &&
            layout.byName.contains(EncodingField.SVE) && layout.get(word, EncodingField.SVE) == 0
          ) Some(Operand.Identifier("off"))
          else if (
            layout.format == EncodingFormat.FLAT_SCRATCH && Set(ISA.RDNA1, ISA.RDNA2)(layout.isa) &&
            layout.get(word, EncodingField.SADDR) == 127 &&
            (field == EncodingField.SADDR || field == EncodingField.ADDR)
          ) Some(Operand.Identifier("off"))
          else if (field == EncodingField.SADDR && number == nullRegister(layout)) Some(Operand.Identifier("off"))
          else if (
            field == EncodingField.VADDR &&
            Set(EncodingFormat.MUBUF, EncodingFormat.MTBUF, EncodingFormat.VBUFFER)(layout.format) &&
            layout.get(word, EncodingField.OFFEN) == 0 && layout.get(word, EncodingField.IDXEN) == 0 &&
            layout.get(word, EncodingField.ADDR64) == 0
          ) Some(Operand.Identifier("off"))
          else if (
            info.bits.raw == 16 && !layout.byName.contains(EncodingField.OP_SEL) &&
            (info.kind.isVector || (SourceFields(field) && number >= 256))
          )
            Some(Operand.Half(
              Operand.Register(RegisterFile.VectorRegister, number & 127, number & 127),
              (number & 128) != 0
            ))
          else if (SourceFields(field) && number >= 256)
            Some(Operand.Register(RegisterFile.VectorRegister, number - 256, number - 256 + (bits + 31) / 32 - 1))
          else if (info.kind.isVector && !SourceFields(field))
            Some(Operand.Register(RegisterFile.VectorRegister, number, number + (bits + 31) / 32 - 1))
          else if (info.kind == OperandKind.VGPR_OR_LDS && number == Fixed.LdsDirectSource)
            Some(Operand.SpecialRegister("src_lds_direct"))
          else if (info.kind.isVector) None
          else if (bits > 64 && number < 128)
            Some(Operand.Register(RegisterFile.Scalar, number, number + (bits + 31) / 32 - 1))
          else OperandCodec.decode(
            number,
            info.copy(
              bits =
                if (
                  info.bits.raw == 64 && number % 2 != 0 &&
                  (field == EncodingField.SDST || field == EncodingField.VDST ||
                    (field == EncodingField.SRC2 && info.kind == OperandKind.SREG))
                ) 32
                else info.bits,
              field = Some(
                if (SourceFields(field) || field == EncodingField.SOFFSET || info.kind.isSource) EncodingField.SSRC0
                else EncodingField.SDST
              )
            ),
            literal,
            target
          )
          base.map { rawOperand =>
            val operand = rawOperand match {
              case Operand.Register(RegisterFile.VectorRegister, first, last)
                  if !layout.format.isVector && layout.get(word, EncodingField.ACC) != 0 &&
                    (Set(
                      EncodingField.VDST,
                      EncodingField.VDATA,
                      EncodingField.DATA,
                      EncodingField.DATA0,
                      EncodingField.DATA1
                    )(field) || info.kind == OperandKind.VGPR_OR_ACCVGPR) =>
                Operand.Register(RegisterFile.Accumulator, first, last)
              case _ => rawOperand
            }
            val selected = if (field.isVectorSource) {
              val absolute =
                if (field.absoluteModifier.exists(layout.get(word, _) != 0)) Operand.Absolute(operand) else operand
              val negative =
                if (field.negativeModifier.exists(layout.get(word, _) != 0)) Operand.Negated(absolute) else absolute
              if (field.extensionModifier.exists(layout.get(word, _) != 0)) Operand.SignExtended(negative)
              else negative
            } else operand
            val index = if (SourceFields(field)) field.index
            else if (field == EncodingField.VSRC0 && LaneModifiers.base(layout) == EncodingFormat.VOP3) 0
            else -1
            val abs =
              if (index >= 0 && bit(layout.get(word, EncodingField.ABS), index)) Operand.Absolute(selected)
              else selected
            if (index >= 0 && !packedFormat(layout) && bit(layout.get(word, EncodingField.NEG), index))
              Operand.Negated(abs)
            else abs
          }
      }
    }
    decodedOperands.flatMap { args =>
      val filtered =
        if (
          operands.exists(_.kind == OperandKind.SMEM_OFFSET) &&
          (layout.byName.contains(EncodingField.IMM) ||
            layout.get(word, EncodingField.SOFFSET) == nullRegister(layout))
        ) mods.filterNot(_.name == "offset")
        else mods
      Instruction.decode(definition, args, filtered).toOption.flatMap { instruction =>
        encode(instruction, target, value, layout, waveSize)
          .filter(_ == Vec.unsafeWrapArray(bytes.slice(offset, offset + decoded.size.raw)))
          .map(_ => DecodedInstruction(instruction, decoded.size))
      }
    }
  }

  /** SOPP carries one 16-bit operand, which may be a symbolic counter, message or relative branch. */
  private object ProgramOperands {
    def encode(
        instruction: Instruction,
        target: Target,
        value: Metadata.Encoding,
        layout: InstructionLayout
    ): Option[Vec[Byte]] = for {
      immediate <- immediate(instruction, target, value, layout)
      opcode    <- layout.byName.get(EncodingField.OP).flatMap(_.put(BigInt(value.opcode)))
      field     <- layout.byName.get(EncodingField.SIMM16).flatMap(_.put(BigInt(immediate)))
    } yield LittleEndian.toVec((layout.fixedPrefix | opcode | field).toLong, 4)

    def decode(
        definition: Opcode,
        word: InstructionWord,
        layout: InstructionLayout,
        target: Target
    ): Option[Vec[Operand]] =
      layout.byName.get(EncodingField.SIMM16).map { field =>
        val raw   = field.get(word)
        val value =
          if (Instruction.branchesRelative(definition, target) && ((raw >>> (field.width - 1)) & 1) != 0)
            raw - (1L << field.width)
          else raw
        if (!Instruction.operandCounts(definition).contains(0) || value != 0)
          Vec(Operand.IntegerLiteral(BigInt(value), 10))
        else Vec.empty[Operand]
      }

    private def immediate(
        instruction: Instruction,
        target: Target,
        definition: Metadata.Encoding,
        layout: InstructionLayout
    ): Option[Long] = layout.byName.get(EncodingField.SIMM16).flatMap { field =>
      val maximum = (BigInt(1) << field.width) - 1
      val minimum = -(BigInt(1) << (field.width - 1))
      val named   = definition.operands.find(!_.implicitOperand).flatMap { info =>
        if (Set(OperandKind.WAITCNT, OperandKind.WAITCNT_DEPCTR, OperandKind.WAIT_ALU)(info.kind))
          SymbolicOperandEncoder.counters(instruction, info.kind, target)
        else if (instruction.mods.isEmpty && instruction.operands.size == 1)
          SymbolicOperandEncoder.encode(instruction.operands.head, info.kind, target)
        else None
      }
      named.filter(value => value >= 0 && value <= maximum).map(_.toLong).orElse {
        instruction.operands match {
          case _ if instruction.mods.nonEmpty                                                => None
          case Vec()                                                                         => Some(0L)
          case Vec(Operand.IntegerLiteral(value, _)) if value >= minimum && value <= maximum =>
            Some((value & maximum).toLong)
          case _ => None
        }
      }
    }
  }

  private object ExportOperands {
    private val Targets =
      Map("mrtz" -> 8, "null" -> 9, "prim" -> 20, "dual_src_blend0" -> 21, "dual_src_blend1" -> 22) ++
        (0 until 8).map(index => s"mrt$index" -> index) ++
        (0 until 4).map(index => s"pos$index" -> (12 + index)) ++
        (0 until 32).map(index => s"param$index" -> (32 + index))
    private val Names = Targets.map(_.swap)

    def supports(layout: InstructionLayout): Boolean =
      layout.format == EncodingFormat.EXP || layout.format == EncodingFormat.VEXPORT

    def encode(instruction: Instruction, compressed: Boolean): Option[Map[EncodingField, BigInt]] =
      instruction.operands match {
        case Vec(target, x, y, z, w) =>
          val name = target match {
            case Operand.Identifier(value)      => Some(value)
            case Operand.SpecialRegister(value) => Some(value)
            case _                              => None
          }
          for {
            target <- name.flatMap(Targets.get)
            values <- Vec(x, y, z, w).traverse {
              case Operand.Identifier("off") => Some(None)
              case Operand.Register(RegisterFile.VectorRegister, first, last)
                  if first == last && first >= 0 && first < 256 => Some(Some(first))
              case _ => None
            }
          } yield {
            val used   = if (compressed) Vec(values(0), values(2)) else values
            val enable = used.zipWithIndex.foldLeft(0) { case (mask, (value, index)) =>
              if (value.isEmpty) mask else mask | ((if (compressed) 3 else 1) << (index * (if (compressed) 2 else 1)))
            }
            Map(EncodingField.TGT -> BigInt(target), EncodingField.EN -> BigInt(enable)) ++ used.zipWithIndex.map {
              case (value, index) =>
                EncodingField.VectorSources(index) -> BigInt(value.getOrElse(0))
            }
          }
        case _ => None
      }

    def decode(word: InstructionWord, layout: InstructionLayout): Option[Vec[Operand]] =
      Names.get(layout.get(word, EncodingField.TGT).toInt).map { name =>
        val compressed = layout.get(word, EncodingField.COMPR) != 0
        val enabled    = layout.get(word, EncodingField.EN).toInt
        val target     = if (name == "null") Operand.SpecialRegister(name) else Operand.Identifier(name)
        target +: Vec.tabulate(4) { index =>
          val field = if (compressed) index / 2 else index
          val mask  = if (compressed) 3 << (field * 2) else 1 << field
          if ((enabled & mask) == 0) Operand.Identifier("off")
          else {
            val register = layout.get(word, EncodingField.VectorSources(field)).toInt
            Operand.Register(RegisterFile.VectorRegister, register, register)
          }
        }
      }
  }
}
