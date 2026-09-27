package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Constraint.NonEmptyString

enum Family(val number: NonEmptyString) {
  case GFX6  extends Family(NonEmptyString("gfx6"))
  case GFX7  extends Family(NonEmptyString("gfx7"))
  case GFX8  extends Family(NonEmptyString("gfx8"))
  case GFX9  extends Family(NonEmptyString("gfx9"))
  case GFX10 extends Family(NonEmptyString("gfx10"))
  case GFX11 extends Family(NonEmptyString("gfx11"))
  case GFX12 extends Family(NonEmptyString("gfx12"))
  case CDNA1 extends Family(NonEmptyString("cdna1"))
  case CDNA2 extends Family(NonEmptyString("cdna2"))
  case CDNA3 extends Family(NonEmptyString("cdna3"))
  case CDNA4 extends Family(NonEmptyString("cdna4"))
  case CDNA5 extends Family(NonEmptyString("cdna5"))
}
