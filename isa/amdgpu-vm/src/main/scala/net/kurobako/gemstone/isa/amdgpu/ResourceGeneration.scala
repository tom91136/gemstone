package net.kurobako.gemstone.isa.amdgpu

private[isa] object ResourceGeneration {
  def of(isa: ISA): ResourceGeneration = isa match {
    case ISA.GFX6 | ISA.GFX7 | ISA.GFX8                           => GFX8
    case ISA.GFX9 | ISA.CDNA1 | ISA.CDNA2 | ISA.CDNA3 | ISA.CDNA4 => GFX9
    case ISA.RDNA1 | ISA.RDNA2                                    => GFX10
    case ISA.RDNA3 | ISA.RDNA3_5 | ISA.GFX11_7                    => GFX11
    case ISA.RDNA4 | ISA.CDNA5                                    => GFX12
  }
}

/** The layout of resource descriptors and the addressing rules buffer and image instructions follow. */
private[isa] enum ResourceGeneration derives CanEqual {
  case GFX8, GFX9, GFX10, GFX11, GFX12
}
