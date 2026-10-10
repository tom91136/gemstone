package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.rpc.Wire.*
import net.kurobako.gemstone.vm.*

/** The image formats native facades name on the wire. */
enum ProgramEncoding derives Codec {
  case PTXText
  case CUDACubin
  case HSACodeObject
  case SPIRVOpenCLCompute
  case SPIRVVulkanCompute
  case SPIRVVulkanGraphics

  def format: ProgramFormat = this match {
    case PTXText             => ProgramFormat.PTXText
    case CUDACubin           => ProgramFormat.CUDACubin
    case HSACodeObject       => ProgramFormat.HSACodeObject
    case SPIRVOpenCLCompute  => ProgramFormat.SPIRVOpenCLCompute
    case SPIRVVulkanCompute  => ProgramFormat.SPIRVVulkanCompute
    case SPIRVVulkanGraphics => ProgramFormat.SPIRVVulkanGraphics
  }
}
