package net.kurobako.gemstone.codegen

import cats.syntax.all.*
import com.monovore.decline.Command

/** The device-visible texture and surface descriptor table: its fixed address, entry size, and field layout. A CUDA
  * texture or surface handle is a nonzero 32-bit index into this table, which the shim registers with the server and
  * the VMs read back. On Kepler a surface handle is instead the address of a `Kepler` record, held below 2^40 in the
  * `KeplerTable` as the 40-bit surface address encoding requires: the record holds the surface base in units of 256
  * bytes, a format word, the inclusive byte or row limit of each axis, the row pitch in bytes, and the height in rows.
  * Kepler addressing is pitch-linear, so the base, the pitch, and the slice pitch are multiples of `Alignment`.
  *
  * A mipmapped texture entry holds level zero in its base fields and, in the second half of the entry, the device
  * address of a table of `Level` records, one per level, with the level count, the mipmap filter, and the level of
  * detail bounds and bias.
  */
object TextureDescriptorABI {
  private val CorePackage = "net.kurobako.gemstone.texture"

  private val groups = Vector(
    (
      "Table",
      "Long",
      Vector(
        "BaseAddress" -> 0x3f0000000000L,
        "EntryBytes"  -> 128L,
        "Entries"     -> 65536L
      )
    ),
    (
      "Field",
      "Int",
      Vector(
        "Base"        -> 0L,
        "Pitch"       -> 8L,
        "SlicePitch"  -> 16L,
        "Width"       -> 24L,
        "Height"      -> 28L,
        "Depth"       -> 32L,
        "Object"      -> 36L,
        "Dimensions"  -> 37L,
        "ChannelKind" -> 38L,
        "Flags"       -> 39L,
        "BitsX"       -> 40L,
        "BitsY"       -> 41L,
        "BitsZ"       -> 42L,
        "BitsW"       -> 43L,
        "AddressX"    -> 44L,
        "AddressY"    -> 45L,
        "AddressZ"    -> 46L,
        "Filter"      -> 47L,
        "BorderX"     -> 48L,
        "BorderY"     -> 52L,
        "BorderZ"     -> 56L,
        "BorderW"     -> 60L,
        "LevelTable"  -> 64L,
        "Levels"      -> 72L,
        "MipFilter"   -> 76L,
        "MinLod"      -> 80L,
        "MaxLod"      -> 84L,
        "LodBias"     -> 88L
      )
    ),
    (
      "Flag",
      "Int",
      Vector(
        "NormalisedCoordinates" -> 1L,
        "Srgb"                  -> 2L,
        "NormalisedRead"        -> 4L,
        "ResourceShift"         -> 3L,
        "ResourceMask"          -> 3L
      )
    ),
    ("KeplerTable", "Long", Vector("BaseAddress" -> 0x7e00000000L)),
    ("Object", "Int", Vector("Free" -> 0L, "Texture" -> 1L, "Surface" -> 2L)),
    (
      "Channel",
      "Int",
      Vector("Signed" -> 0L, "Unsigned" -> 1L, "Float" -> 2L, "UnsignedNormalised" -> 3L, "SignedNormalised" -> 4L)
    ),
    ("Address", "Int", Vector("Wrap" -> 0L, "Clamp" -> 1L, "Mirror" -> 2L, "Border" -> 3L)),
    ("Filter", "Int", Vector("Point" -> 0L, "Linear" -> 1L)),
    ("Resource", "Int", Vector("Array" -> 0L, "MipmappedArray" -> 1L, "Linear" -> 2L, "Pitch2D" -> 3L)),
    (
      "Kepler",
      "Int",
      Vector(
        "Base"      -> 0L,
        "Format"    -> 4L,
        "LimitX"    -> 8L,
        "Pitch"     -> 12L,
        "LimitY"    -> 16L,
        "LimitZ"    -> 24L,
        "Height"    -> 28L,
        "Bytes"     -> 32L,
        "Alignment" -> 256L,
        "MaxPitch"  -> 65280L,
        "MaxRows"   -> 65535L,
        "MaxBytes"  -> 65536L
      )
    ),
    (
      "Level",
      "Int",
      Vector(
        "Base"       -> 0L,
        "Pitch"      -> 8L,
        "SlicePitch" -> 16L,
        "Width"      -> 24L,
        "Height"     -> 28L,
        "Depth"      -> 32L,
        "Bytes"      -> 40L
      )
    )
  )

  def scalaSource: String = {
    val body = groups.map { case (group, tpe, values) =>
      val fields = values.map { case (name, value) =>
        if (tpe == "Long") s"    inline val $name = 0x${value.toHexString}L" else s"    inline val $name = $value"
      }.mkString("\n")
      s"  object $group {\n$fields\n  }"
    }.mkString("\n\n")
    GeneratedHeader.scala("from Gemstone TextureDescriptorABI") +
      s"package $CorePackage\n\n${GeneratedHeader.annotation()}object TextureLayout {\n$body\n}\n"
  }

  def nativeSource: String = {
    val body = groups.map { case (group, tpe, values) =>
      val (cpp, suffix) = if (tpe == "Long") ("uint64_t", "ull") else ("uint32_t", "u")
      val fields        = values.map { case (name, value) =>
        val text = if (tpe == "Long") s"0x${value.toHexString}" else s"$value"
        s"inline constexpr $cpp $name = $text$suffix;"
      }.mkString("\n")
      s"namespace $group {\n$fields\n} // namespace $group"
    }.mkString("\n\n")
    GeneratedHeader.cpp("from Gemstone TextureDescriptorABI") +
      s"#pragma once\n\n#include <cstdint>\n\nnamespace gemstone::texture::Layout {\n$body\n} // namespace gemstone::texture::Layout\n"
  }

  def main(arguments: Array[String]): Unit = CodegenCommand.run(
    Command("TextureDescriptorABI", "Generate the texture descriptor table layout")((
      CodegenCommand.path("core-source-root"),
      CodegenCommand.path("native-root")
    ).tupled),
    arguments
  ) { case (coreRoot, nativeRoot) =>
    GeneratedFiles.writeScala(coreRoot.resolve("net/kurobako/gemstone/texture/TextureLayout.scala"), scalaSource)
    GeneratedFiles.write(nativeRoot.resolve("gemstone_texture_layout.hpp"), nativeSource)
  }
}
