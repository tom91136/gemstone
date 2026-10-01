package net.kurobako.gemstone.server.backend

import net.kurobako.gemstone.graphics.*
import net.kurobako.gemstone.isa.spirv.v1_6.{SPIRV as _, *}
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.vm.*

object Backends {
  def default(workgroups: Workgroups = Workgroups.Sequential): Backends = Backends(
    List(
      PTX(workgroups),
      AMDGPU(workgroups),
      SPIRV(environment = Validation.Environment.OpenCLCompute, workgroups = workgroups),
      SPIRV(Validation.Environment.VulkanCompute, "spirv-vulkan-binary", workgroups),
      SPIRV(Validation.Environment.VulkanGraphics, "spirv-vulkan-graphics-binary", workgroups)
    ),
    Some(SPIRV),
    Some(Graphics(workgroups))
  )
}

case class Backends(
    programs: List[RuntimeBackend],
    specialization: Option[RuntimeBackend.Specialization] = None,
    graphics: Option[GraphicsBackend] = None
)
