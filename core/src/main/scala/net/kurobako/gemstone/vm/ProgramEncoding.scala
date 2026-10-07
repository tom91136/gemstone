package net.kurobako.gemstone.vm

/** The image formats a backend loads; native facades name them on the wire. */
enum ProgramEncoding {
  case PTXText
  case CUDACubin
  case HSACodeObject
  case SPIRVOpenCLCompute
  case SPIRVVulkanCompute
  case SPIRVVulkanGraphics
}
