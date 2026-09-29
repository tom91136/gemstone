package net.kurobako.gemstone.server.backend

import net.kurobako.gemstone.isa.spirv.v1_6.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.vm.*

object Backends {
  def default(workgroups: Workgroups = Workgroups.Sequential): Backends = Backends(
    List(
      PTX(workgroups),
      AMDGPU(workgroups),
      SPIRV(Validation.Environment.OpenCLCompute, workgroups = workgroups),
      SPIRV(Validation.Environment.VulkanCompute, "spirv-vulkan-binary", workgroups)
    ),
    Some(SPIRV)
  )
}

case class Backends(programs: List[RuntimeBackend], specialization: Option[RuntimeBackend.Specialization] = None)
