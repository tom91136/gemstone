package net.kurobako.gemstone.codegen

/** Gemstone's pipe object layout, shared by the native OpenCL facade and the SPIR-V VM. A pipe is a header followed by
  * a ring of `Capacity` packets of `PacketBytes` each; the read and write indices count packets and wrap at 32 bits.
  */
object PipeABI {
  private val layout = Vector(
    "ReadIndex"   -> 0L,
    "WriteIndex"  -> 4L,
    "Capacity"    -> 8L,
    "PacketBytes" -> 12L,
    "Packets"     -> 16L
  )

  def scalaSource: String = {
    val fields = layout.map { case (name, value) => s"  inline val $name = $value" }.mkString("\n")
    GeneratedHeader.scala("from Gemstone PipeABI") +
      s"package net.kurobako.gemstone.isa.spirv\n\n${GeneratedHeader.annotation()}object PipeLayout {\n$fields\n}\n"
  }

  def nativeSource: String = {
    val fields = layout.map { case (name, value) => s"inline constexpr uint32_t $name = ${value}u;" }.mkString("\n")
    GeneratedHeader.cpp("from Gemstone PipeABI") +
      s"#pragma once\n\n#include <cstdint>\n\nnamespace gemstone::spirv::PipeLayout {\n$fields\n} // namespace gemstone::spirv::PipeLayout\n"
  }
}
