package net.kurobako.gemstone.codegen

/** Hand-written inputs to the AMDGPU operand domain merge. Every row names the source section it follows. */
object AMDGPUDomainRules {

  /** An edit to every domain of `isa` holding `anchor` (or matching `remove` when there is no anchor). */
  case class Override(
      isa: String,
      remove: Option[String],
      add: Vector[(String, Int)],
      anchor: Option[String],
      resolves: Set[String],
      reason: String,
      source: String
  ) {
    def isKeep: Boolean = remove.isEmpty && add.isEmpty
  }

  /** Re-types or renames an operand field whose record disagrees with the format it sits in. */
  case class FieldRemap(
      isas: Set[String],
      format: String,
      field: String,
      kind: String,
      toField: String,
      toKind: String,
      reason: String,
      source: String
  )

  /** Replaces an operand kind the ISA's own XML lacks with the nearest type its document has. */
  case class KindAlias(isas: Set[String], from: String, to: String, reason: String, source: String)

  case class AlignmentRule(isa: String, file: String, fromBits: Int, alignment: Int, source: String)

  /** Replaces the immediate of operand type `operand` on `isas`, or adds one when the type lists none. */
  case class ImmediateRule(
      isas: Set[String],
      operand: String,
      immediate: OperandDomain.Immediate,
      reason: String,
      source: String
  )

  /** An XML operand type paired with the LLVM operand class that selects the same register or constant forms. */
  case class ClassPair(xmlType: String, llvmClass: String, source: String)

  val ISAs: Vector[String] = Vector(
    "GFX6",
    "GFX7",
    "GFX8",
    "GFX9",
    "CDNA1",
    "CDNA2",
    "CDNA3",
    "CDNA4",
    "CDNA5",
    "RDNA1",
    "RDNA2",
    "RDNA3",
    "RDNA3_5",
    "GFX11_7",
    "RDNA4"
  )

  /** ISAs without an XML document take the document of the nearest ISA, then the overrides. */
  val Derived: Map[String, String] =
    Map("GFX6" -> "CDNA1", "GFX7" -> "CDNA1", "GFX8" -> "CDNA1", "GFX9" -> "CDNA1", "GFX11_7" -> "RDNA3_5")

  private val Gfx11Plus = Set("RDNA3", "RDNA3_5", "GFX11_7", "RDNA4", "CDNA5")
  private val Gfx10Plus = Gfx11Plus ++ Set("RDNA1", "RDNA2")
  private val ViTemps   = Set("GFX6", "GFX7", "GFX8")

  /** The LLVM `SGPR_MAX_*` constant bounding the scalar registers of `isa`; SIDefines.h `EncValues`. */
  def sgprBound(isa: String): String = if (Gfx10Plus(isa)) "SGPR_MAX_GFX10" else "SGPR_MAX_SI"

  /** The `SIRegisterInfo.td` suffix of the `m0` and `null` encodings of `isa`. */
  def scalarVariant(isa: String): String = if (Gfx11Plus(isa)) "gfx11plus" else "gfxpre11"

  /** The `SIRegisterInfo.td` suffix of the `ttmp` encodings of `isa`. */
  def trapVariant(isa: String): String = if (ViTemps(isa)) "vi" else "gfx9plus"

  /** The `SIRegisterInfo.td` suffix of the `flat_scratch` encodings of `isa`. */
  def scratchVariant(isa: String): String = if (isa == "GFX7") "ci" else "vi"

  val Processors: Map[String, Set[String]] = Map(
    "GFX6" -> Set("tahiti", "verde", "gfx600", "gfx601", "pitcairn", "oland", "hainan", "gfx602"),
    "GFX7" -> Set(
      "bonaire",
      "hawaii",
      "kabini",
      "kaveri",
      "gfx700",
      "gfx701",
      "mullins",
      "gfx702",
      "gfx703",
      "gfx704",
      "gfx705"
    ),
    "GFX8"    -> Set("carrizo", "fiji", "iceland", "stoney", "tonga", "gfx800", "gfx801", "gfx802", "gfx803", "gfx810"),
    "GFX9"    -> Set("gfx9", "gfx900", "gfx904", "gfx906", "gfx90c"),
    "CDNA1"   -> Set("gfx908"),
    "CDNA2"   -> Set("gfx90a"),
    "CDNA3"   -> Set("gfx940", "gfx941", "gfx942"),
    "CDNA4"   -> Set("gfx950"),
    "CDNA5"   -> Set("gfx1250", "gfx1251"),
    "RDNA1"   -> Set("gfx1001", "gfx1010", "gfx1011", "gfx1012", "gfx1013"),
    "RDNA2"   -> Set("gfx1030", "gfx1031", "gfx1032", "gfx1033", "gfx1034", "gfx1035", "gfx1036"),
    "RDNA3"   -> Set("gfx1100", "gfx1101", "gfx1102", "gfx1103"),
    "RDNA3_5" -> Set("gfx1150", "gfx1151", "gfx1152", "gfx1153"),
    "GFX11_7" -> Set("gfx1170"),
    "RDNA4"   -> Set("gfx12", "gfx1200", "gfx1201")
  )

  val ClassPairs: Vector[ClassPair] = Vector(
    ClassPair("SSRC", "SSrc_b32", "SIRegisterInfo.td SSrc_b32: scalar source with literal"),
    ClassPair("SSRC_NOLIT", "SCSrc_b32", "SIRegisterInfo.td SCSrc_b32: scalar source, inline constants only"),
    ClassPair("SRC", "VSrc_b32", "SIRegisterInfo.td VSrc_b32: vector source with literal"),
    ClassPair("SRC_NOLIT", "VCSrc_b32", "SIRegisterInfo.td VCSrc_b32: vector source, inline constants only")
  )

  /** Operand names with no LLVM register or constant: memory and program counter pseudo-operands. */
  val PseudoOperands: Set[String] = Set("dsmem", "gpumem", "pc", "pc_all", "flat_scratch_all")

  /** Prose rules: 64-bit scalar values start on even registers and wider ones on multiples of four. An operand of
    * `bits` bits in `file` starts on a multiple of the largest alignment whose `fromBits` it reaches.
    */
  val Alignment: Vector[AlignmentRule] = {
    val scalar = "amdgpu_isa_*.xml, PredefinedValue s0 of OPR_SSRC: alignment restriction"
    val vector = "amdgpu_isa_*.xml, PredefinedValue v0 of OPR_VGPR: alignment restriction"
    val accum  = "amdgpu_isa_*.xml, PredefinedValue acc0 of OPR_ACCVGPR: alignment restriction"
    val rules  = ISAs.flatMap { isa =>
      Vector(
        AlignmentRule(isa, "sgpr", fromBits = 64, alignment = 2, source = scalar),
        AlignmentRule(isa, "sgpr", fromBits = 128, alignment = 4, source = scalar),
        AlignmentRule(isa, "ttmp", fromBits = 64, alignment = 2, source = scalar),
        AlignmentRule(isa, "ttmp", fromBits = 128, alignment = 4, source = scalar)
      )
    }
    val oracle = "llvm-oracle/gfx1251.s: vgpr tuples must be 64 bit aligned"
    rules ++ Vector("CDNA2", "CDNA3", "CDNA4").flatMap { isa =>
      Vector(
        AlignmentRule(isa, "vgpr", fromBits = 64, alignment = 2, source = vector),
        AlignmentRule(isa, "accvgpr", fromBits = 64, alignment = 2, source = accum)
      )
    } :+ AlignmentRule("CDNA5", "vgpr", fromBits = 64, alignment = 2, source = oracle)
  }

  /** Immediates the XML describes in prose as signed. */
  val SignedImmediates: Set[String] = Set("LABEL")

  private val OffsetSource = "SMInstructions.td SMEMOffset, smrd_offset_8, smrd_literal_offset"

  /** Immediate ranges the XML omits or states differently from what LLVM and its MC oracle accept. */
  val ImmediateRules: Vector[ImmediateRule] = {
    val all    = ISAs.toSet
    val signed = Set("GFX9", "CDNA1", "CDNA2", "CDNA3", "CDNA4", "RDNA1", "RDNA2", "RDNA3", "RDNA3_5", "GFX11_7")
    val wide   = Set("RDNA4", "CDNA5")
    Vector(
      ImmediateRule(
        Set("GFX6", "GFX7"),
        "SMEM_OFFSET",
        OperandDomain.Immediate(8, signed = false),
        "SMRD offsets are 8-bit dword counts; GFX7 takes a trailing 32-bit literal beyond them",
        s"$OffsetSource; llvm-oracle/tahiti.s, bonaire.s"
      ),
      ImmediateRule(
        Set("GFX8"),
        "SMEM_OFFSET",
        OperandDomain.Immediate(20, signed = false),
        "VI scalar memory offsets are 20-bit unsigned; the oracle diagnoses larger and negative values",
        s"$OffsetSource; llvm-oracle/fiji.s"
      ),
      ImmediateRule(
        signed,
        "SMEM_OFFSET",
        OperandDomain.Immediate(21, signed = true),
        "21-bit signed from GFX9; the XML types it unsigned or lists none but describes the field as signed, and llvm-mc encodes negative offsets",
        s"$OffsetSource; amdgpu_isa_rdna3.xml ENC_SMEM IOFFSET: 21-bit SIGNED BYTE offset"
      ),
      ImmediateRule(
        wide,
        "SMEM_OFFSET",
        OperandDomain.Immediate(24, signed = true),
        "24-bit signed from GFX12; the XML types it unsigned but describes the field as signed, and llvm-mc encodes negative offsets",
        s"$OffsetSource; amdgpu_isa_cdna5.xml ENC_SMEM IOFFSET: 24-bit SIGNED BYTE offset"
      ),
      ImmediateRule(
        all,
        "SIMM16",
        OperandDomain.Immediate(16, signed = true, max = Some(65535L)),
        "the signed and the unsigned 16-bit spelling of the same bits both assemble",
        "SIInstrInfo.td SIMM16bit; llvm-mc 22.1.8 on gfx1100 (s_movk_i32 s0, -1)"
      ),
      ImmediateRule(
        all,
        "SIMM32",
        OperandDomain.Immediate(32, signed = false, min = Some(Int.MinValue.toLong)),
        "the signed and the unsigned 32-bit spelling of the same bits both assemble",
        "SIInstrInfo.td KImmFP32; llvm-mc 22.1.8 on gfx1100 (v_fmaak_f32 v0, v1, v2, -1)"
      )
    )
  }

  /** The XML documents whose types an ISA's records draw on, in precedence order. */
  def documents(isa: String): Vector[String] = {
    val own = Derived.getOrElse(isa, isa)
    own +:
      (isa match {
        case "RDNA3"   => Vector("RDNA3_5")
        case "GFX11_7" => Vector("RDNA4")
        case _         => Vector.empty
      })
  }

  val KindAliases: Vector[KindAlias] = Vector(
    KindAlias(
      isas = Set("GFX6", "GFX7"),
      from = "SREG_NONULL",
      to = "SREG",
      reason = "null does not exist before GFX10; the records borrowed the RDNA type of the same scalar registers",
      source = "SIRegisterInfo.td SGPR_NULL_gfxpre11"
    ),
    KindAlias(
      isas = Set("GFX6", "GFX7"),
      from = "SREG_M0_INL",
      to = "SSRC_NOLIT",
      reason =
        "the records borrowed an RDNA scalar source; the nearest CDNA1 type is the scalar source without literal",
      source = "SIRegisterInfo.td SCSrc_b32"
    ),
    KindAlias(
      isas = Set("RDNA1"),
      from = "SRC_NOLIT",
      to = "SRC",
      reason = "the records borrowed a CDNA1 VOP3 source; GFX10 VOP3 accepts one literal",
      source = "VOPInstructions.td VOP3 literal on gfx10"
    )
  )

  /** Vector-file flags of the SDWA formats: the flag clear selects the vector file for the source field. */
  val SdwaFlags: Map[String, String] = Map("VSRC0" -> "S0", "VSRC1" -> "S1")

  val FieldRemaps: Vector[FieldRemap] = Vector(
    FieldRemap(
      isas = Set("CDNA5", "RDNA4"),
      format = "SMEM",
      field = "IOFFSET",
      kind = "SIMM24",
      toField = "OFFSET",
      toKind = "SIMM24",
      reason = "the XML names the 24-bit immediate IOFFSET, the encoding names its field OFFSET",
      source = "amdgpu_isa_rdna4.xml ENC_SMEM BitMap"
    ),
    FieldRemap(
      isas = Set("GFX6", "GFX7"),
      format = "SMEM",
      field = "SOFFSET",
      kind = "SMEM_OFFSET",
      toField = "OFFSET",
      toKind = "SMEM_OFFSET",
      reason = "the SI and CI scalar memory format has one OFFSET field selected by IMM",
      source = "SMInstructions.td SMRD_Real_si"
    ),
    FieldRemap(
      isas = Set("GFX6", "GFX7"),
      format = "VOP2",
      field = "VSRC1",
      kind = "SRC_SIMPLE",
      toField = "VSRC1",
      toKind = "VGPR",
      reason = "VOP2 holds a vector register in src1; the record took the VOP3 operand type",
      source = "VOP2Instructions.td VOP2_Real_e32 vsrc1"
    ),
    FieldRemap(
      isas = Set("RDNA4"),
      format = "SOP1",
      field = "SSRC0",
      kind = "SRC",
      toField = "SSRC0",
      toKind = "SSRC",
      reason = "SOP1 sources are scalar; the LLVM-derived record took the vector source type",
      source = "SOPInstructions.td SOP1 ssrc0"
    )
  )

  private val OlderISAs = Vector("GFX6", "GFX7", "GFX8")

  private def removal(isa: String, pattern: String, resolves: Set[String], reason: String, source: String) =
    Override(
      isa = isa,
      remove = Some(pattern),
      add = Vector.empty,
      anchor = None,
      resolves = resolves,
      reason = reason,
      source = source
    )

  private val TrapTemporaries = Vector.tabulate(12)(index => s"ttmp$index" -> (112 + index)) ++
    Vector("tba_lo" -> 108, "tba_hi" -> 109, "tma_lo" -> 110, "tma_hi" -> 111)

  private val TrapSubjects = Set("ttmp", "ttmp12", "ttmp13", "tba_lo", "tba_hi", "tma_lo", "tma_hi")

  /** exec_lo and exec_hi are scalar operands from RDNA1 on, which the SGPR-class domains omit. */
  private val ExecOperands: Vector[Override] =
    Vector("RDNA1", "RDNA2", "RDNA3", "RDNA3_5", "RDNA4", "CDNA5").map { isa =>
      Override(
        isa = isa,
        remove = Some("exec_(lo|hi)"),
        add = Vector("exec_lo" -> 126, "exec_hi" -> 127),
        anchor = Some("vcc_hi"),
        resolves = Set.empty,
        reason = "v_cmp, s_load and buffer_load take exec_lo and exec_hi as scalar operands, which the domains omit",
        source =
          "llvm-mc -mcpu=gfx1100: v_cmp_eq_u32_e64 exec_hi, v0, v1 (SDST 0x7f); s_load_dword s0, s[2:3], exec_hi (SOFFSET 0xfe)"
      )
    }

  val Overrides: Vector[Override] =
    OlderISAs.map { isa =>
      Override(
        isa = isa,
        remove = Some("ttmp[0-9]+"),
        add = TrapTemporaries,
        anchor = None,
        resolves = TrapSubjects,
        reason = "before GFX9 the trap temporaries are ttmp0..11 at 112..123 beside tba and tma at 108..111",
        source = "SIRegisterInfo.td TTMP#_vi, TBA_LO, TMA_LO; SIDefines.h TTMP_VI_MIN"
      )
    } ++ OlderISAs.map { isa =>
      removal(
        isa = isa,
        pattern = "src_(shared|private)_(base|limit)",
        resolves = Set("src_shared_base"),
        reason = "the aperture registers arrive with GFX9; the oracle rejects src_shared_base on tonga",
        source = "llvm-oracle/tonga.s"
      )
    } ++ OlderISAs.map { isa =>
      removal(
        isa = isa,
        pattern = "src_pops_exiting_wave_id",
        resolves = Set("src_pops_exiting_wave_id"),
        reason = "LLVM rejects the primitive-ordered shader wave id before GFX9",
        source = "llvm-oracle/tahiti.s"
      )
    } ++ Vector("GFX6", "GFX7").map { isa =>
      removal(
        isa = isa,
        pattern = "xnack_mask_(lo|hi)",
        resolves = Set("xnack_mask_hi"),
        reason = "XNACK does not exist before GFX8; the oracle rejects xnack_mask_hi on tahiti and hawaii",
        source = "llvm-oracle/tahiti.s, hawaii.s"
      )
    } ++ Vector("GFX6", "GFX7").map { isa =>
      removal(
        isa,
        "0\\.15915494",
        Set.empty,
        "the 1/(2*pi) inline constant exists from VI",
        "SIDefines.h INLINE_FLOATING_C_MAX"
      )
    } ++ Vector(
      Override(
        isa = "GFX8",
        remove = None,
        add = Vector.empty,
        anchor = None,
        resolves = Set("xnack_mask_hi"),
        reason = "gfx801 and gfx810 enable XNACK, so the GFX8 family keeps xnack_mask beside tonga, which rejects it",
        source = "AMDGPU.td FeatureISAVersion8_1_0; llvm-oracle/tonga.s"
      ),
      Override(
        isa = "GFX7",
        remove = Some("flat_scratch_(lo|hi)"),
        add = Vector("flat_scratch_lo" -> 104, "flat_scratch_hi" -> 105),
        anchor = None,
        resolves = Set("flat_scratch_lo", "flat_scratch_hi"),
        reason = "CI places flat_scratch above the 104 scalar registers, VI below them",
        source = "SIRegisterInfo.td FLAT_SCR_LO_ci, FLAT_SCR_HI_ci"
      ),
      Override(
        isa = "GFX6",
        remove = Some("flat_scratch_(lo|hi)"),
        add = Vector("s102" -> 102, "s103" -> 103),
        anchor = Some("s101"),
        resolves = Set("s102", "s103"),
        reason =
          "SI has 104 scalar registers and no flat_scratch; llvm-mc takes s102 and s103 and rejects flat_scratch_lo",
        source = "llvm-oracle/tahiti.s; llvm-mc 22.1.8 on tahiti (s_mov_b32 flat_scratch_lo, 0)"
      ),
      Override(
        isa = "GFX7",
        remove = None,
        add = Vector("s102" -> 102, "s103" -> 103),
        anchor = Some("s101"),
        resolves = Set("s103"),
        reason = "CI has 104 scalar registers; the oracle accepts s103 on bonaire",
        source = "llvm-oracle/bonaire.s"
      ),
      Override(
        isa = "GFX11_7",
        remove = None,
        add = Vector("src_vccz" -> 251, "src_execz" -> 252),
        anchor = Some("src_scc"),
        resolves = Set("src_vccz", "src_execz"),
        reason = "gfx1170 has no XML document and LLVM accepts vccz and execz there but not pops",
        source = "SIRegisterInfo.td SRC_VCCZ, SRC_EXECZ; BinarySuite implicit sources"
      )
    ) ++ ExecOperands
}
