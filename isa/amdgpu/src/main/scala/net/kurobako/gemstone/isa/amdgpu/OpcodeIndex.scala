package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.collection.*
import scala.collection.immutable.IntMap

/** Catalogue encodings by machine opcode, and the preferred spelling among aliases sharing one. */
private object OpcodeIndex {
  private val aliases = Map(
    "v_dot4_i32_i8"          -> "v_dot4_i32_iu8",
    "v_dot8_i32_i4"          -> "v_dot8_i32_iu4",
    "v_accvgpr_read"         -> "v_accvgpr_read_b32",
    "v_mad_legacy_f16"       -> "v_mad_f16",
    "v_div_fixup_legacy_f16" -> "v_div_fixup_f16",
    "v_interp_p2_legacy_f16" -> "v_interp_p2_f16",
    "buffer_wbinvl1_vol"     -> "buffer_wbinvl1_sc",
    "v_accvgpr_write"        -> "v_accvgpr_write_b32",
    "v_addc_co_u32"          -> "v_add_co_ci_u32",
    "v_subb_co_u32"          -> "v_sub_co_ci_u32",
    "v_subbrev_co_u32"       -> "v_subrev_co_ci_u32",
    "v_mul_lo_i32"           -> "v_mul_lo_u32",
    "v_add_i32"              -> "v_add_co_u32",
    "v_sub_i32"              -> "v_sub_co_u32",
    "v_subrev_i32"           -> "v_subrev_co_u32",
    "v_addc_u32"             -> "v_add_co_ci_u32",
    "v_subb_u32"             -> "v_sub_co_ci_u32",
    "v_subbrev_u32"          -> "v_subrev_co_ci_u32"
  )

  private val gfx8Aliases = Map(
    "v_add_u32"    -> "v_add_co_u32",
    "v_sub_u32"    -> "v_sub_co_u32",
    "v_subrev_u32" -> "v_subrev_co_u32",
    "v_mad_u16"    -> "v_mad_legacy_u16"
  )
  private val gfx900Aliases = Vector("f32", "lo_f16", "hi_f16").map { suffix =>
    val fma = s"v_fma_mix${if (suffix == "f32") "_" else ""}$suffix"
    fma -> fma.replace("v_fma_", "v_mad_")
  }.toMap
  private val gfx906Aliases = gfx900Aliases.map(_.swap)

  private def preferredName(opcode: String, target: Target): Option[String] = {
    val targetAliases = target.isa match {
      case ISA.GFX8 => gfx8Aliases
      case ISA.GFX9 => if (Set("gfx9", "gfx900")(target.name)) gfx900Aliases else gfx906Aliases
      case _        => Map.empty[String, String]
    }
    targetAliases.get(opcode).orElse(aliases.get(opcode))
  }

  private lazy val entries: Map[(ISA, EncodingFormat), IntMap[Vec[(Opcode, Metadata.Encoding)]]] =
    Opcode.definitions.flatMap(definition => definition.meta.encodings.map(definition -> _))
      .groupBy { case (_, encoding) => (encoding.isa, encoding.format) }
      .view.mapValues(values => IntMap.from(values.groupBy(_._2.opcode))).toMap

  /** Catalogue encodings of one machine format, by opcode; callers filter by processor availability. */
  def encodings(isa: ISA, format: EncodingFormat): IntMap[Vec[(Opcode, Metadata.Encoding)]] =
    entries.getOrElse((isa, format), IntMap.empty)

  def canonical[A](values: Vec[A], target: Target)(definition: A => Opcode): Vec[A] =
    values.filterNot(value =>
      preferredName(definition(value).opcode, target).exists(name =>
        values.exists(value => definition(value).opcode == name)
      )
    )
}
