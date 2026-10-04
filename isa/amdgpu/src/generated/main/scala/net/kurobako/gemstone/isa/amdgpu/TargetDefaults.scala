// Generated from LLVM processor wavefront defaults. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.constraint.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
object TargetDefaults {
  def waveSize(target: Target): Int in (32, 64) = target.name.raw match {
    case "gfx10-1-generic" | "gfx10-3-generic" | "gfx1001" | "gfx1010" | "gfx1011" | "gfx1012" | "gfx1013" | "gfx1030" | "gfx1031" | "gfx1032" | "gfx1033" | "gfx1034" | "gfx1035" | "gfx1036" | "gfx11-7-generic" | "gfx11-generic" | "gfx1100" | "gfx1101" | "gfx1102" | "gfx1103" | "gfx1150" | "gfx1151" | "gfx1152" | "gfx1153" | "gfx1154" | "gfx1170" | "gfx1171" | "gfx1172" | "gfx12" | "gfx12-5-generic" | "gfx12-generic" | "gfx1200" | "gfx1201" | "gfx1250" | "gfx1251" | "gfx13-generic" | "gfx1310" =>
      32
    case _ => 64
  }
}
