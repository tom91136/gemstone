package net.kurobako.gemstone.server.backend

import net.kurobako.gemstone.graphics.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.spirv.*
import net.kurobako.gemstone.isa.spirv.graphics.*
import net.kurobako.gemstone.vm.*

object Backends {
  def default(workgroups: Workgroups = Workgroups.Sequential): Backends = Backends(
    List(
      ptx.PTXBackend(workgroups),
      sass.CubinBackend(workgroups),
      amdgpu.AMDGPUBackend(workgroups),
      SPIRVBackend(Validation.Environment.OpenCLCompute, ProgramFormat.SPIRVOpenCLCompute, workgroups),
      SPIRVBackend(Validation.Environment.VulkanCompute, ProgramFormat.SPIRVVulkanCompute, workgroups),
      SPIRVBackend(Validation.Environment.VulkanGraphics, ProgramFormat.SPIRVVulkanGraphics, workgroups)
    ),
    Some(Specialization),
    Some(GraphicsRuntime(workgroups))
  )
}

case class Backends(
    programs: List[Backend],
    specialization: Option[Backend.Specialization] = None,
    graphics: Option[GraphicsBackend] = None
)
