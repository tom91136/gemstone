#pragma once

#include <cstddef>
#include <cstdint>
#include <string_view>

#include "gemstone/hsa/target.hpp"

namespace gemstone::hsa {

inline constexpr size_t GroupMemoryBytes = size_t{64} * 1024;
inline constexpr size_t GlobalMemoryBytes = size_t{1} << 30U;
inline constexpr uint64_t TimestampHz = 1'000'000'000;
inline constexpr uint32_t EmulatedProcessors = 4;

struct DeviceModel {
  uint32_t waveSize;
  uint32_t simdsPerComputeUnit;
  uint32_t wavesPerSimd;
  uint32_t registersPerSimd;
  uint32_t computeUnitsPerProcessor;
  uint32_t computeUnits;
  uint32_t clockMHz;
  size_t globalMemoryBytes;
  bool imageInstructions;
  bool imageGatherLod;

  [[nodiscard]] uint32_t wavesPerComputeUnit() const { return simdsPerComputeUnit * wavesPerSimd; }
  [[nodiscard]] uint32_t processors() const { return computeUnits / computeUnitsPerProcessor; }
  [[nodiscard]] uint32_t threadsPerProcessor() const { return computeUnitsPerProcessor * wavesPerComputeUnit() * waveSize; }
  [[nodiscard]] uint32_t registersPerProcessor() const { return computeUnitsPerProcessor * simdsPerComputeUnit * registersPerSimd; }
  [[nodiscard]] size_t groupMemoryPerProcessor() const { return computeUnitsPerProcessor * GroupMemoryBytes; }
};

// RDNA workgroups run on a pair of compute units (WGP mode).
inline DeviceModel deviceModel(std::string_view target) {
  const unsigned wave = gemstone::amdgpu::defaultWaveSize(target);
  const bool rdna = wave == 32;
  const bool unifiedRegisters = target.starts_with("gfx90a") || target.starts_with("gfx94") || target.starts_with("gfx95");
  const bool rdna1 = target.starts_with("gfx101") || target.starts_with("gfx10-1");
  const uint32_t simds = rdna ? 2U : 4U;
  const uint32_t waves = [&] {
    if (unifiedRegisters) return 8U;
    if (!rdna) return 10U;
    if (rdna1) return 20U;
    return 16U;
  }();
  const uint32_t registers = [&] {
    if (rdna) return 1024U;
    if (unifiedRegisters) return 512U;
    return 256U;
  }();
  const auto device = targetPreset(target);
  // a preset counts compute units, so each one is a processor
  const uint32_t perProcessor = rdna && !device ? 2U : 1U;
  return DeviceModel{
      .waveSize = wave,
      .simdsPerComputeUnit = simds,
      .wavesPerSimd = waves,
      .registersPerSimd = registers * wave,
      .computeUnitsPerProcessor = perProcessor,
      .computeUnits = device ? static_cast<uint32_t>(device->multiprocessors) : EmulatedProcessors * perProcessor,
      .clockMHz = device ? static_cast<uint32_t>(device->boostMhz) : static_cast<uint32_t>(TimestampHz / 1'000'000),
      .globalMemoryBytes = device ? static_cast<size_t>(device->memoryBytes) : GlobalMemoryBytes,
      .imageInstructions = !target.starts_with("gfx94") && !target.starts_with("gfx95"),
      .imageGatherLod = !target.starts_with("gfx90a") && !target.starts_with("gfx94") && !target.starts_with("gfx95"),
  };
}

inline DeviceModel deviceModel() { return deviceModel(targetArch()); }

} // namespace gemstone::hsa
