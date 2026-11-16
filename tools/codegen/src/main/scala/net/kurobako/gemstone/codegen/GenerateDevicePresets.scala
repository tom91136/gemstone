package net.kurobako.gemstone.codegen

import java.nio.file.Path
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.profile.DevicePreset

/** Renders the device presets for the native runtimes to report. */
object GenerateDevicePresets {

  def render(presets: Vec[DevicePreset]): String = {
    val rows = presets.sortBy(_.name).map { preset =>
      s"""    {"${preset.name}", "${preset.target}", ${preset.multiprocessors}, ${preset.boostMhz}, ${preset.memoryBytes}, ${preset.memoryMTps}, ${preset.busWidthBits}, ${preset.l2Bytes}},"""
    }
    GeneratedHeader.cpp("from the device presets") +
      s"""#pragma once
         |#include <array>
         |#include <cstdint>
         |#include <cstdio>
         |#include <optional>
         |#include <string>
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
         |struct PciAddress {
         |  uint32_t domain;
         |  uint32_t bus;
         |  uint32_t device;
         |  uint32_t function;
         |  constexpr uint32_t bdfid() const noexcept { return ((bus & 0xffU) << 8U) | ((device & 0x1fU) << 3U) | (function & 0x7U); }
         |};
         |
         |constexpr PciAddress devicePciAddress(uint32_t ordinal) noexcept { return {.domain = 0, .bus = ordinal, .device = 1, .function = 0}; }
         |
         |inline std::string devicePciBusId(uint32_t ordinal) {
         |  const auto address = devicePciAddress(ordinal);
         |  char buffer[32];
         |  std::snprintf(buffer, sizeof(buffer), "%04x:%02x:%02x.%u", address.domain, address.bus, address.device, address.function);
         |  return buffer;
         |}
         |
         |constexpr std::array<uint8_t, 16> deviceUuid(uint32_t ordinal) noexcept {
         |  std::array<uint8_t, 16> uuid{};
         |  for (size_t index = 0; index < uuid.size(); ++index)
         |    uuid[index] = static_cast<uint8_t>(0x47U + index);
         |  uuid.back() = static_cast<uint8_t>(uuid.back() + ordinal);
         |  return uuid;
         |}
         |
         |constexpr uint64_t deviceUniqueId(uint32_t ordinal) noexcept {
         |  const auto bytes = deviceUuid(ordinal);
         |  uint64_t unique = 0;
         |  for (size_t index = 8; index < 16; ++index)
         |    unique = (unique << 8U) | bytes[index];
         |  return unique;
         |}
         |
         |inline std::string deviceHsaUuid(uint32_t ordinal) {
         |  char buffer[24];
         |  std::snprintf(buffer, sizeof(buffer), "GPU-%016llx", static_cast<unsigned long long>(deviceUniqueId(ordinal)));
         |  return buffer;
         |}
         |
         |constexpr uint32_t graphicsVersion(std::string_view target) noexcept {
         |  uint32_t version = 0;
         |  bool started = false;
         |  for (const char character : target) {
         |    if (!started) {
         |      if (character < '0' || character > '9') continue;
         |      started = true;
         |    }
         |    if (character >= '0' && character <= '9') version = (version << 4U) | static_cast<uint32_t>(character - '0');
         |    else if (character >= 'a' && character <= 'f') version = (version << 4U) | static_cast<uint32_t>(character - 'a' + 10);
         |    else break;
         |  }
         |  return version;
         |}
         |
         |inline constexpr DevicePreset devicePresets[] = {
         |${rows.mkString("\n")}
         |};
         |
         |inline std::optional<DevicePreset> devicePreset(std::string_view name) noexcept {
         |  for (const auto &preset : devicePresets) {
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
