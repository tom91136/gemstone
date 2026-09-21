package net.kurobako.gemstone.isa.amdgpu

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

enum Family(val number: String :| MinLength[1]) {
  case GFX6  extends Family("gfx6")
  case GFX7  extends Family("gfx7")
  case GFX8  extends Family("gfx8")
  case GFX9  extends Family("gfx9")
  case GFX10 extends Family("gfx10")
  case GFX11 extends Family("gfx11")
  case GFX12 extends Family("gfx12")
  case CDNA1 extends Family("cdna1")
  case CDNA2 extends Family("cdna2")
  case CDNA3 extends Family("cdna3")
  case CDNA4 extends Family("cdna4")
  case CDNA5 extends Family("cdna5")
}
