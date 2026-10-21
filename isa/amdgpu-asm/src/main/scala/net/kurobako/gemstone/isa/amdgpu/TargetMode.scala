package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.constraint.*

object TargetMode {
  def apply(target: Target): TargetMode = TargetMode(target, TargetDefaults.waveSize(target))
}

/** A target and the wavefront width it runs at, which decide how its wave-dependent encodings lay out. */
case class TargetMode(target: Target, waveSize: Int in (32, 64))
