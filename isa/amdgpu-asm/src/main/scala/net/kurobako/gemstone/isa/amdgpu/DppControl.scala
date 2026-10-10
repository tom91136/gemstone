package net.kurobako.gemstone.isa.amdgpu

import EncodingConstants.DPP.*

private object DppControl {
  def within(control: Int, first: Int, last: Int): Boolean = control >= first && control <= last

  /** GCN keeps wave shifts and row broadcasts, which RDNA replaces with row share and XOR mask; CDNA2 onwards adds
    * row_newbcast, which shares row share's encoding and routing.
    */
  def valid(control: Int, isa: ISA): Boolean = {
    val common = within(control, QUAD_PERM_FIRST, QUAD_PERM_LAST) ||
      within(control, ROW_SHL_FIRST, ROW_SHL_LAST) ||
      within(control, ROW_SHR_FIRST, ROW_SHR_LAST) || within(control, ROW_ROR_FIRST, ROW_ROR_LAST) ||
      control == ROW_MIRROR || control == ROW_HALF_MIRROR
    val gcn = common || control == WAVE_SHL1 || control == WAVE_ROL1 || control == WAVE_SHR1 ||
      control == WAVE_ROR1 || control == BCAST15 || control == BCAST31
    isa match {
      case ISA.RDNA1 | ISA.RDNA2 | ISA.RDNA3 | ISA.RDNA3_5 | ISA.RDNA4 | ISA.GFX11_7 | ISA.CDNA5 =>
        common || within(control, ROW_SHARE_FIRST, ROW_SHARE_LAST) ||
        within(control, ROW_XMASK_FIRST, ROW_XMASK_LAST)
      case _ if rowNewBroadcast(isa) => gcn || within(control, ROW_NEWBCAST_FIRST, ROW_NEWBCAST_LAST)
      case _                         => gcn
    }
  }

  def rowNewBroadcast(isa: ISA): Boolean = isa match {
    case ISA.CDNA2 | ISA.CDNA3 | ISA.CDNA4 => true
    case _                                 => false
  }
}
