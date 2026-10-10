package net.kurobako.gemstone.codegen

/** Gemstone's private image descriptor wire values, shared by the native facade and SPIR-V VM. These extend SPIR-V
  * image formats for sampled/transfer views; they are not Khronos enumerants. Keep values stable: existing clients and
  * binary fixtures use this ABI.
  */
object ImageDescriptorABI {
  private val groups = Vector(
    (
      "Format",
      "Int",
      Vector(
        "D16Unorm"           -> 1000L,
        "Bgra8Unorm"         -> 1001L,
        "D32SfloatS8Depth"   -> 1002L,
        "S8Uint"             -> 1003L,
        "D32SfloatS8Stencil" -> 1004L,
        "Rgba8Srgb"          -> 1005L,
        "Bgra8Srgb"          -> 1006L,
        "B4G4R4A4Unorm"      -> 1007L,
        "R5G6B5Unorm"        -> 1008L,
        "A1R5G5B5Unorm"      -> 1009L,
        "E5B9G9R9Ufloat"     -> 1010L,
        "DecodedRgb32f"      -> 1011L,
        "D32Sfloat"          -> 1012L
      )
    ),
    (
      "Flags",
      "Long",
      Vector(
        "LevelTable" -> 0x80000000L,
        "Volume"     -> 0x40000000L,
        "Swizzle"    -> 0x20000000L,
        "Pitched"    -> 0x10000000L,
        "FormatMask" -> 0x0fffffffL
      )
    ),
    (
      "SamplerWord",
      "Int",
      Vector(
        "MinFilter"     -> 0L,
        "MagFilter"     -> 1L,
        "AddressU"      -> 2L,
        "AddressV"      -> 3L,
        "Unnormalized"  -> 4L,
        "CompareEnable" -> 5L,
        "CompareOp"     -> 6L,
        "BorderColour"  -> 7L,
        "MipLodBias"    -> 8L,
        "MinLod"        -> 9L,
        "MaxLod"        -> 10L,
        "MipmapMode"    -> 11L,
        "AddressW"      -> 12L,
        "Count"         -> 13L
      )
    ),
    (
      "Layout",
      "Int",
      Vector(
        "Width"             -> 0L,
        "Height"            -> 4L,
        "Slices"            -> 8L,
        "Format"            -> 12L,
        "Texels"            -> 16L,
        "TexelBytes"        -> 24L,
        "Levels"            -> 32L,
        "Samples"           -> 36L,
        "LevelTable"        -> 40L,
        "LevelBytes"        -> 16L,
        "LevelWidth"        -> 0L,
        "LevelHeight"       -> 4L,
        "LevelOffset"       -> 8L,
        "SwizzleBytes"      -> 16L,
        "PitchBytes"        -> 8L,
        "InlineHeaderBytes" -> 16L
      )
    )
  ) ++ GraphicsEnums.descriptorGroups

  def scalaSource: String = {
    val body = groups.map { case (group, tpe, values) =>
      val fields = values.map { case (name, value) =>
        val suffix = if (tpe == "Long") "L" else ""
        s"    inline val $name = $value$suffix"
      }.mkString("\n")
      s"  object $group {\n$fields\n  }"
    }.mkString("\n\n")
    GeneratedHeader.scala("from Gemstone ImageDescriptorABI") +
      s"package net.kurobako.gemstone.isa.spirv\n\n${GeneratedHeader.annotation()}object ImageDescriptor {\n$body\n}\n"
  }

  def nativeSource: String = {
    val body = groups.map((group, _, values) => Emission.cppConstants(group, values)).mkString("\n\n")
    GeneratedHeader.cpp("from Gemstone ImageDescriptorABI") +
      s"#pragma once\n\n#include <cstdint>\n\nnamespace gemstone::spirv::ImageDescriptor {\n$body\n} // namespace gemstone::spirv::ImageDescriptor\n"
  }
}
