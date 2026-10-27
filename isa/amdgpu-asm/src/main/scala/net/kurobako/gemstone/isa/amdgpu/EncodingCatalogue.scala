package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.isa.catalogue.*

/** Read-only views of the encoding catalogues for documentation and tooling. */
object EncodingCatalogue {

  case class Field(name: EncodingField, ranges: Vec[BitRange])

  /** An instruction word layout; bits set in `fixedMask` always hold the matching bits of `fixedPrefix`. */
  case class Layout(
      isa: ISA,
      format: EncodingFormat,
      bits: Int,
      fixedMask: BigInt,
      fixedPrefix: BigInt,
      fields: Vec[Field]
  )

  def layout(isa: ISA, format: EncodingFormat): Option[Layout] =
    InstructionLayout.Catalogue.forFormat(isa, format).map(layout =>
      Layout(
        layout.isa,
        layout.format,
        layout.bits,
        layout.fixedMask,
        layout.fixedPrefix,
        layout.fields.toVec.map(field => Field(field.name, field.ranges.toVec))
      )
    )

  /** One DPP16 control of each kind `isa` routes lanes with, spelt as the assembler takes it. */
  def dppControls(isa: ISA): Vec[String] = {
    import EncodingConstants.DPP.*
    Vec(
      "quad_perm:[1,0,3,2]"                                                      -> 0xb1,
      "row_shl:1"                                                                -> (ROW_SHL0 + 1),
      "row_shr:1"                                                                -> (ROW_SHR0 + 1),
      "row_ror:1"                                                                -> (ROW_ROR0 + 1),
      "wave_shl:1"                                                               -> WAVE_SHL1,
      "wave_rol:1"                                                               -> WAVE_ROL1,
      "wave_shr:1"                                                               -> WAVE_SHR1,
      "wave_ror:1"                                                               -> WAVE_ROR1,
      "row_mirror"                                                               -> ROW_MIRROR,
      "row_half_mirror"                                                          -> ROW_HALF_MIRROR,
      "row_bcast:15"                                                             -> BCAST15,
      "row_bcast:31"                                                             -> BCAST31,
      (if (DppControl.rowNewBroadcast(isa)) "row_newbcast:1" else "row_share:1") -> (ROW_SHARE0 + 1),
      "row_xmask:1"                                                              -> (ROW_XMASK0 + 1)
    ).collect { case (spelling, control) if DppControl.valid(control, isa) => spelling }
  }

  /** Source operand spellings, such as `s0`, `vcc_lo` or `src_scc`, by their encoded selector value. */
  def sourceSelectors(isa: ISA): Map[String, Int] =
    domain.DomainCatalogue.table(isa, "src").fold(Map.empty[String, Int])(_.selectors)
}
