package net.kurobako.gemstone.codegen

import java.nio.file.Files
import java.nio.file.Path

/** The guest address layout shared by the session, the machines and the native shims. A host user-space pointer is
  * below `HostUserLimit` (2^56 covers x86-64 with 5-level paging and aarch64 with 52-bit addresses). Windows an AMDGPU
  * buffer descriptor must address sit in `[ReservedBase, ReservedLimit)`, inside its 48-bit base. That range lies
  * beyond x86-64 4-level user space, and the native shims reserve it where a host process could map it. Every other
  * window starts at or above `HostUserLimit`. Windows are disjoint by construction.
  */
object GenerateAddressLayout {
  case class Constant(name: String, value: Long, doc: String)

  /** A mapped range `[base, limit)`; `limit` names a constant. */
  case class Window(name: String, base: String, limit: String)

  val HostUserLimit: Long    = 0x0100000000000000L
  val TextureTableBase: Long = 0x3800000000000000L

  val constants: Vector[Constant] = Vector(
    Constant("HostUserLimit", HostUserLimit, "exclusive upper bound of any host user-space pointer"),
    Constant("ReservedBase", 0x800000000000L, "start of the range the native shims reserve in the host process"),
    Constant("ReservedLimit", 0x1000000000000L, "end of the reserved range, the 48-bit AMDGPU descriptor base limit"),
    Constant(
      "AllocationBase",
      0xc00000000000L,
      "allocation id n starts at AllocationBase + (n - 1) * AllocationStride"
    ),
    Constant("AllocationStride", 0x80000000L, "address span reserved per allocation id"),
    Constant("AllocationLimit", 0x1000000000000L, "end of the allocation windows"),
    Constant("ProgramBase", 0x800000000000L, "program id n loads at ProgramBase + n * ProgramStride"),
    Constant("ProgramStride", 0x40000000L, "address span of one program's code window"),
    Constant("ProgramLimit", 0x900000000000L, "end of the program code windows"),
    Constant("AmdgpuKernarg", 0x900000000000L, "AMDGPU dispatch kernarg window"),
    Constant("AmdgpuDispatch", 0xa00000000000L, "AMDGPU dispatch packet window"),
    Constant("AmdgpuQueue", 0xb00000000000L, "AMDGPU queue window"),
    Constant("AmdgpuDispatchLimit", 0xc00000000000L, "end of the AMDGPU dispatch windows"),
    Constant("TextureTableBase", TextureTableBase, "device texture and surface descriptor table"),
    Constant("TextureTableLimit", 0x3800000000800000L, "end of the descriptor table"),
    Constant("NvidiaSharedWindowBase", 0x4000000000000000L, "SASS generic shared window, selected by SR_SWINHI"),
    Constant("NvidiaSharedWindowLimit", 0x4000000100000000L, "end of the shared window"),
    Constant("NvidiaParameterBase", 0x5000000000000000L, "kernel parameter buffers"),
    Constant("NvidiaParameterLimit", 0x5000000100000000L, "end of the parameter window"),
    Constant("SassConstantBase", 0x5100000000000000L, "SASS constant bank 2"),
    Constant("SassConstantLimit", 0x5100000000010000L, "end of the constant bank window"),
    Constant("SpirvApertureShift", 56L, "bit position of a SPIR-V aperture tag"),
    Constant("SpirvGlobalAperture", 0x81L, "tag of the global aperture"),
    Constant("SpirvWorkgroupAperture", 0x82L, "tag of the workgroup aperture"),
    Constant("SpirvPrivateAperture", 0x83L, "tag of the private aperture"),
    Constant("GraphicsScratchBase", 0xc000000000000000L, "graphics shader scratch, 4 GiB per invocation"),
    Constant("GraphicsScratchLimit", 0xfffe000000000000L, "end of the graphics scratch windows"),
    Constant("AmdgpuLocalBase", 0xffff000000000000L, "AMDGPU flat local aperture"),
    Constant("AmdgpuLocalLimit", 0xffff000100000000L, "end of the local aperture"),
    Constant("AmdgpuPrivateBase", 0xfffe000000000000L, "AMDGPU flat private aperture"),
    Constant("AmdgpuPrivateLimit", 0xfffe000100000000L, "end of the private aperture")
  )

  val windows: Vector[Window] = Vector(
    Window("Allocation", "AllocationBase", "AllocationLimit"),
    Window("Program", "ProgramBase", "ProgramLimit"),
    Window("AmdgpuDispatch", "AmdgpuKernarg", "AmdgpuDispatchLimit"),
    Window("TextureTable", "TextureTableBase", "TextureTableLimit"),
    Window("NvidiaSharedWindow", "NvidiaSharedWindowBase", "NvidiaSharedWindowLimit"),
    Window("NvidiaParameter", "NvidiaParameterBase", "NvidiaParameterLimit"),
    Window("SassConstant", "SassConstantBase", "SassConstantLimit"),
    Window("GraphicsScratch", "GraphicsScratchBase", "GraphicsScratchLimit"),
    Window("AmdgpuLocal", "AmdgpuLocalBase", "AmdgpuLocalLimit"),
    Window("AmdgpuPrivate", "AmdgpuPrivateBase", "AmdgpuPrivateLimit")
  )

  private def hex(value: Long): String = s"0x${java.lang.Long.toHexString(value)}"

  private val known = constants.map(_.name).toSet

  require(constants.map(_.name).distinct.size == constants.size, "duplicate layout constant")
  private val byName = constants.map(c => c.name -> c.value).toMap
  windows.foreach { window =>
    val (base, limit) = (byName(window.base), byName(window.limit))
    val reserved      = java.lang.Long.compareUnsigned(base, byName("ReservedBase")) >= 0 &&
      java.lang.Long.compareUnsigned(limit, byName("ReservedLimit")) <= 0
    val high = java.lang.Long.compareUnsigned(base, HostUserLimit) >= 0
    require(reserved || high, s"window ${window.name} is neither reserved nor above the host user limit")
    require(java.lang.Long.compareUnsigned(base, limit) < 0, s"window ${window.name} is empty")
  }
  windows.sortWith((a, b) => java.lang.Long.compareUnsigned(byName(a.base), byName(b.base)) < 0).sliding(2).foreach {
    case Vector(a, b) => require(
        java.lang.Long.compareUnsigned(byName(a.limit), byName(b.base)) <= 0,
        s"windows ${a.name} and ${b.name} overlap"
      )
    case _ => ()
  }
  windows.foreach(window => require(known(window.base) && known(window.limit), s"unknown bound of ${window.name}"))

  def scalaSource: String = {
    val fields =
      constants.map(c => s"  /** ${c.doc.capitalize}. */\n  inline val ${c.name} = ${hex(c.value)}L").mkString("\n")
    val ranges = windows.map(w => s"""    ("${w.name}", ${w.base}, ${w.limit})""").mkString(",\n")
    GeneratedHeader.scala("by GenerateAddressLayout") +
      s"package net.kurobako.gemstone.memory\n\n${GeneratedHeader.annotation()}object AddressLayout {\n$fields\n\n" +
      s"  /** Every mapped window as name, base and exclusive limit; compare unsigned. */\n  val Windows: List[(String, Long, Long)] = List(\n$ranges\n  )\n}\n"
  }

  def nativeSource: String = {
    val fields = constants.map(c =>
      s"// ${c.doc.capitalize}\ninline constexpr uint64_t ${c.name} = ${hex(c.value)}ull;"
    ).mkString("\n")
    val checks = windows.map(w =>
      s"static_assert((${w.base} >= ReservedBase && ${w.limit} <= ReservedLimit) || ${w.base} >= HostUserLimit);"
    ).mkString("\n")
    GeneratedHeader.cpp("by GenerateAddressLayout") +
      s"#pragma once\n\n#include <cstdint>\n\nnamespace gemstone::address {\n$fields\n\n$checks\n} // namespace gemstone::address\n"
  }

  val ScalaPath: String  = "core/src/generated/main/scala/net/kurobako/gemstone/memory/AddressLayout.scala"
  val NativePath: String = "native/generated/address_layout.hpp"

  def generate(root: Path, check: Boolean): Unit = {
    val scala  = root.resolve(ScalaPath)
    val native = root.resolve(NativePath)
    val files  = Vector(scala -> GeneratedScalaFormatter(scala, scalaSource), native -> nativeSource)
    if (check) files.foreach { (path, expected) =>
      require(
        Files.isRegularFile(path) && Files.readString(path) == expected,
        s"stale address layout output: $path; run codegen"
      )
    }
    else files.foreach(GeneratedFiles.write(_, _))
  }

  def main(arguments: Array[String]): Unit =
    generate(Path.of(arguments(0)).toAbsolutePath.normalize, arguments.contains("--check"))
}
