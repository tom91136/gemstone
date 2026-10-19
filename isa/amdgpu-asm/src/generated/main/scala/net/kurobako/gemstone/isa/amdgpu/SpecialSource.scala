// Generated from the pinned AMDGPU machine metadata. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu

import upickle.default.*

object SpecialSource {

  given ReadWriter[SpecialSource] = readwriter[String].bimap(_.toString, valueOf(_))
  private lazy val byName: Map[String, SpecialSource] = values.view.map(value => value.toString -> value).toMap
  def fromName(name: String): Option[SpecialSource] = byName.get(name)
}

@javax.annotation.processing.Generated(Array("sbt codegen"))
enum SpecialSource derives CanEqual {
  case `src_execz`
  case `src_flat_scratch_base`
  case `src_flat_scratch_base_hi`
  case `src_flat_scratch_base_lo`
  case `src_lds_direct`
  case `src_pops_exiting_wave_id`
  case `src_private_base`
  case `src_private_limit`
  case `src_scc`
  case `src_shared_base`
  case `src_shared_limit`
  case `src_simm21`
  case `src_simm8`
  case `src_vccz`

}
