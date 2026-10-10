package net.kurobako.gemstone.vm

import net.kurobako.gemstone.constraint.*

type ProgramFormat = ProgramFormat.Type

/** The image format a backend loads. Formats are open: a backend outside Gemstone names its own. */
object ProgramFormat extends Newtype[NonEmpty[String]] {
  val PTXText: ProgramFormat             = ProgramFormat("ptx")
  val CUDACubin: ProgramFormat           = ProgramFormat("cubin")
  val HSACodeObject: ProgramFormat       = ProgramFormat("hsaco")
  val SPIRVOpenCLCompute: ProgramFormat  = ProgramFormat("spirv-opencl")
  val SPIRVVulkanCompute: ProgramFormat  = ProgramFormat("spirv-vulkan-compute")
  val SPIRVVulkanGraphics: ProgramFormat = ProgramFormat("spirv-vulkan-graphics")
}
