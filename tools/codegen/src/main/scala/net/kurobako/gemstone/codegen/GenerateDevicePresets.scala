package net.kurobako.gemstone.codegen

import java.nio.file.Path
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.profile.Architecture
import net.kurobako.gemstone.profile.DevicePreset

/** Renders the device presets for the native runtimes to report, with the card values of each preset's target. */
object GenerateDevicePresets {

  def render(presets: Vec[DevicePreset]): String = {
    val rows = presets.sortBy(_.name).map { preset =>
      val card = Architecture.named(preset.target).flatMap(_.card)
        .getOrElse(throw IllegalArgumentException(s"${preset.name} has no card on ${preset.target}"))
      val multiprocessors = card.multiprocessors
        .getOrElse(throw IllegalArgumentException(s"${preset.name} has no multiprocessor count"))
      val boostMhz = card.boostMhz.getOrElse(throw IllegalArgumentException(s"${preset.name} has no boost clock"))
      val l2Bytes  = card.l2KiB * 1024
      s"""  {"${preset.name}", "${preset.target}", $multiprocessors, $boostMhz, ${preset.memoryBytes}, ${preset.memoryMTps}, ${card.busWidthBits}, $l2Bytes},"""
    }
    GeneratedHeader.cpp("from the device presets and the Architecture table") +
      s"""#pragma once
         |#include <cstdint>
         |#include <optional>
         |#include <string_view>
         |
         |namespace gemstone {
         |struct DevicePreset {
         |  std::string_view name;
         |  std::string_view target;
         |  int32_t multiprocessors;
         |  int32_t boostMhz;
         |  int64_t memoryBytes;
         |  int32_t memoryMTps;
         |  int32_t busWidthBits;
         |  int32_t l2Bytes;
         |};
         |
         |inline constexpr DevicePreset devicePresets[] = {
         |${rows.mkString("\n")}
         |};
         |
         |inline std::optional<DevicePreset> devicePreset(std::string_view name) noexcept {
         |  for (const auto& preset : devicePresets) {
         |    if (preset.name == name) return preset;
         |  }
         |  return std::nullopt;
         |}
         |} // namespace gemstone
         |""".stripMargin
  }

  def main(arguments: Array[String]): Unit =
    GeneratedFiles.write(Path.of(arguments(0)).resolve("gemstone_device_presets.hpp"), render(DevicePreset.all))
}
