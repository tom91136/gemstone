#include <algorithm>
#include <cstdint>
#include <cstdio>
#include <cstring>
#include <string_view>

#include "gemstone_amd_smi_api.h"
#include <gemstone/devices.hpp>
#include <gemstone/environment.hpp>
#include <gemstone/export.h>
#include <gemstone_device_presets.hpp>

#include <amd_smi/amdsmi.h>

namespace {

constexpr uintptr_t SocketHandleValue = 1;

bool validOrdinal(uint32_t ordinal) noexcept { return ordinal < gemstone::deviceCount(); }

bool validHandle(amdsmi_processor_handle handle) noexcept {
  if (!handle) return false;
  const auto value = reinterpret_cast<uintptr_t>(handle);
  return value > 0 && validOrdinal(static_cast<uint32_t>(value - 1));
}

uint32_t toOrdinal(amdsmi_processor_handle handle) noexcept { return static_cast<uint32_t>(reinterpret_cast<uintptr_t>(handle) - 1); }

gemstone::DevicePreset activePreset() noexcept {
  if (const auto preset = gemstone::environment::selectedDevicePreset()) return *preset;
  return gemstone::devicePresets[0];
}

} // namespace

extern "C" {

amdsmi_status_t amdsmi_init(uint64_t /*init_flags*/) { return AMDSMI_STATUS_SUCCESS; }
amdsmi_status_t amdsmi_shut_down() { return AMDSMI_STATUS_SUCCESS; }

amdsmi_status_t amdsmi_get_socket_handles(uint32_t *socketCount, amdsmi_socket_handle *socketHandles) {
  if (!socketCount) return AMDSMI_STATUS_INVAL;
  if (!socketHandles) {
    *socketCount = 1;
    return AMDSMI_STATUS_SUCCESS;
  }
  if (*socketCount < 1) return AMDSMI_STATUS_INSUFFICIENT_SIZE;
  socketHandles[0] = reinterpret_cast<amdsmi_socket_handle>(SocketHandleValue);
  *socketCount = 1;
  return AMDSMI_STATUS_SUCCESS;
}

amdsmi_status_t amdsmi_get_socket_info(amdsmi_socket_handle socketHandle, size_t len, char *name) {
  if (!name || len == 0 || reinterpret_cast<uintptr_t>(socketHandle) != SocketHandleValue) return AMDSMI_STATUS_INVAL;
  std::snprintf(name, len, "Socket 0");
  return AMDSMI_STATUS_SUCCESS;
}

amdsmi_status_t amdsmi_get_processor_handles(amdsmi_socket_handle socketHandle, uint32_t *processorCount,
                                             amdsmi_processor_handle *processorHandles) {
  if (!processorCount || reinterpret_cast<uintptr_t>(socketHandle) != SocketHandleValue) return AMDSMI_STATUS_INVAL;
  const uint32_t count = gemstone::deviceCount();
  if (!processorHandles) {
    *processorCount = count;
    return AMDSMI_STATUS_SUCCESS;
  }
  if (*processorCount < count) return AMDSMI_STATUS_INSUFFICIENT_SIZE;
  for (uint32_t i = 0; i < count; ++i) {
    processorHandles[i] = reinterpret_cast<amdsmi_processor_handle>(static_cast<uintptr_t>(i + 1));
  }
  *processorCount = count;
  return AMDSMI_STATUS_SUCCESS;
}

amdsmi_status_t amdsmi_get_processor_handle_from_bdf(amdsmi_bdf_t bdf, amdsmi_processor_handle *processorHandle) {
  if (!processorHandle) return AMDSMI_STATUS_INVAL;
  for (uint32_t i = 0; i < gemstone::deviceCount(); ++i) {
    const auto addr = gemstone::devicePciAddress(i);
    if (addr.domain == bdf.bdf.domain_number && addr.bus == bdf.bdf.bus_number && addr.device == bdf.bdf.device_number
        && addr.function == bdf.bdf.function_number) {
      *processorHandle = reinterpret_cast<amdsmi_processor_handle>(static_cast<uintptr_t>(i + 1));
      return AMDSMI_STATUS_SUCCESS;
    }
  }
  return AMDSMI_STATUS_NOT_FOUND;
}

amdsmi_status_t amdsmi_get_processor_type(amdsmi_processor_handle processorHandle, processor_type_t *processorType) {
  if (!processorType || !validHandle(processorHandle)) return AMDSMI_STATUS_INVAL;
  *processorType = AMDSMI_PROCESSOR_TYPE_AMD_GPU;
  return AMDSMI_STATUS_SUCCESS;
}

amdsmi_status_t amdsmi_get_gpu_device_bdf(amdsmi_processor_handle processorHandle, amdsmi_bdf_t *bdf) {
  if (!bdf || !validHandle(processorHandle)) return AMDSMI_STATUS_INVAL;
  const uint32_t ordinal = toOrdinal(processorHandle);
  const auto addr = gemstone::devicePciAddress(ordinal);
  std::memset(bdf, 0, sizeof(*bdf));
  bdf->bdf.function_number = addr.function;
  bdf->bdf.device_number = addr.device;
  bdf->bdf.bus_number = addr.bus;
  bdf->bdf.domain_number = addr.domain;
  return AMDSMI_STATUS_SUCCESS;
}

amdsmi_status_t amdsmi_get_gpu_bdf_id(amdsmi_processor_handle processorHandle, uint64_t *bdfid) {
  if (!bdfid || !validHandle(processorHandle)) return AMDSMI_STATUS_INVAL;
  const uint32_t ordinal = toOrdinal(processorHandle);
  *bdfid = gemstone::devicePciAddress(ordinal).bdfid();
  return AMDSMI_STATUS_SUCCESS;
}

amdsmi_status_t amdsmi_get_gpu_device_uuid(amdsmi_processor_handle processorHandle, unsigned int *uuidLength, char *uuid) {
  if (!uuidLength || !validHandle(processorHandle)) return AMDSMI_STATUS_INVAL;
  const uint32_t ordinal = toOrdinal(processorHandle);
  const auto bytes = gemstone::deviceUuid(ordinal);
  char buf[40];
  std::snprintf(buf, sizeof(buf), "%02x%02x%02x%02x-%02x%02x-%02x%02x-%02x%02x-%02x%02x%02x%02x%02x%02x", bytes[0], bytes[1], bytes[2],
                bytes[3], bytes[4], bytes[5], bytes[6], bytes[7], bytes[8], bytes[9], bytes[10], bytes[11], bytes[12], bytes[13], bytes[14],
                bytes[15]);
  const auto len = static_cast<unsigned int>(std::strlen(buf));
  if (!uuid) {
    *uuidLength = len + 1U;
    return AMDSMI_STATUS_SUCCESS;
  }
  if (*uuidLength < len + 1U) return AMDSMI_STATUS_INSUFFICIENT_SIZE;
  std::memcpy(uuid, buf, len + 1U);
  *uuidLength = len + 1U;
  return AMDSMI_STATUS_SUCCESS;
}

amdsmi_status_t amdsmi_get_gpu_kfd_info(amdsmi_processor_handle processorHandle, amdsmi_kfd_info_t *info) {
  if (!info || !validHandle(processorHandle)) return AMDSMI_STATUS_INVAL;
  const uint32_t ordinal = toOrdinal(processorHandle);
  std::memset(info, 0, sizeof(*info));
  info->kfd_id = 1000ULL + ordinal;
  info->node_id = 1U + ordinal;
  info->current_partition_id = 0;
  return AMDSMI_STATUS_SUCCESS;
}

amdsmi_status_t amdsmi_get_gpu_asic_info(amdsmi_processor_handle processorHandle, amdsmi_asic_info_t *info) {
  if (!info || !validHandle(processorHandle)) return AMDSMI_STATUS_INVAL;
  const auto preset = activePreset();
  std::memset(info, 0, sizeof(*info));
  std::snprintf(info->market_name, sizeof(info->market_name), "%.*s", static_cast<int>(preset.name.size()), preset.name.data());
  info->vendor_id = 0x1002U;
  std::snprintf(info->vendor_name, sizeof(info->vendor_name), "Advanced Micro Devices Inc. [AMD/ATI]");
  info->subvendor_id = 0x1002U;
  info->device_id = 0x744cU;
  info->rev_id = 0;
  std::snprintf(info->asic_serial, sizeof(info->asic_serial), "0x0000000000000000");
  info->oam_id = 0xFFFFFFFFU;
  info->num_of_compute_units = static_cast<uint32_t>(preset.multiprocessors);
  info->target_graphics_version = gemstone::graphicsVersion(preset.target);
  info->subsystem_id = 0x0b35U;
  return AMDSMI_STATUS_SUCCESS;
}

amdsmi_status_t amdsmi_get_gpu_board_info(amdsmi_processor_handle processorHandle, amdsmi_board_info_t *info) {
  if (!info || !validHandle(processorHandle)) return AMDSMI_STATUS_INVAL;
  const auto preset = activePreset();
  std::memset(info, 0, sizeof(*info));
  std::snprintf(info->product_name, sizeof(info->product_name), "%.*s", static_cast<int>(preset.name.size()), preset.name.data());
  std::snprintf(info->manufacturer_name, sizeof(info->manufacturer_name), "Advanced Micro Devices, Inc. [AMD/ATI]");
  return AMDSMI_STATUS_SUCCESS;
}

amdsmi_status_t amdsmi_get_gpu_vram_info(amdsmi_processor_handle processorHandle, amdsmi_vram_info_t *info) {
  if (!info || !validHandle(processorHandle)) return AMDSMI_STATUS_INVAL;
  const auto preset = activePreset();
  std::memset(info, 0, sizeof(*info));
  info->vram_type = AMDSMI_VRAM_TYPE_GDDR6;
  std::snprintf(info->vram_vendor, sizeof(info->vram_vendor), "UNKNOWN");
  info->vram_size = static_cast<uint64_t>(preset.memoryBytes / (1024ULL * 1024ULL));
  info->vram_bit_width = static_cast<uint32_t>(preset.busWidthBits);
  info->vram_max_bandwidth = 0;
  return AMDSMI_STATUS_SUCCESS;
}

amdsmi_status_t amdsmi_get_gpu_vram_usage(amdsmi_processor_handle processorHandle, amdsmi_vram_usage_t *vramUsage) {
  if (!vramUsage || !validHandle(processorHandle)) return AMDSMI_STATUS_INVAL;
  const auto preset = activePreset();
  std::memset(vramUsage, 0, sizeof(*vramUsage));
  vramUsage->vram_total = static_cast<uint32_t>(preset.memoryBytes / (1024ULL * 1024ULL));
  vramUsage->vram_used = 0;
  return AMDSMI_STATUS_SUCCESS;
}

amdsmi_status_t amdsmi_get_gpu_memory_total(amdsmi_processor_handle processorHandle, amdsmi_memory_type_t memType, uint64_t *total) {
  if (!total || !validHandle(processorHandle)) return AMDSMI_STATUS_INVAL;
  const auto preset = activePreset();
  if (memType == AMDSMI_MEM_TYPE_VRAM) {
    *total = static_cast<uint64_t>(preset.memoryBytes);
    return AMDSMI_STATUS_SUCCESS;
  }
  if (memType == AMDSMI_MEM_TYPE_VIS_VRAM) {
    *total = std::min<uint64_t>(preset.memoryBytes, 256ULL * 1024ULL * 1024ULL);
    return AMDSMI_STATUS_SUCCESS;
  }
  if (memType == AMDSMI_MEM_TYPE_GTT) {
    *total = static_cast<uint64_t>(preset.memoryBytes);
    return AMDSMI_STATUS_SUCCESS;
  }
  return AMDSMI_STATUS_NOT_SUPPORTED;
}

amdsmi_status_t amdsmi_get_gpu_memory_usage(amdsmi_processor_handle processorHandle, amdsmi_memory_type_t memType, uint64_t *used) {
  if (!used || !validHandle(processorHandle)) return AMDSMI_STATUS_INVAL;
  if (memType == AMDSMI_MEM_TYPE_VRAM || memType == AMDSMI_MEM_TYPE_VIS_VRAM || memType == AMDSMI_MEM_TYPE_GTT) {
    *used = 0;
    return AMDSMI_STATUS_SUCCESS;
  }
  return AMDSMI_STATUS_NOT_SUPPORTED;
}

amdsmi_status_t amdsmi_get_gpu_busy_percent(amdsmi_processor_handle processorHandle, uint32_t *busyPercent) {
  if (!busyPercent || !validHandle(processorHandle)) return AMDSMI_STATUS_INVAL;
  *busyPercent = 0;
  return AMDSMI_STATUS_SUCCESS;
}

amdsmi_status_t amdsmi_get_clock_info(amdsmi_processor_handle processorHandle, amdsmi_clk_type_t clkType, amdsmi_clk_info_t *info) {
  if (!info || !validHandle(processorHandle)) return AMDSMI_STATUS_INVAL;
  const auto preset = activePreset();
  std::memset(info, 0, sizeof(*info));
  if (clkType == AMDSMI_CLK_TYPE_SYS) {
    info->clk = static_cast<uint32_t>(preset.boostMhz);
    info->min_clk = 600;
    info->max_clk = static_cast<uint32_t>(preset.boostMhz);
    return AMDSMI_STATUS_SUCCESS;
  }
  if (clkType == AMDSMI_CLK_TYPE_MEM) {
    info->clk = static_cast<uint32_t>(preset.memoryMTps / 2);
    info->min_clk = 800;
    info->max_clk = static_cast<uint32_t>(preset.memoryMTps / 2);
    return AMDSMI_STATUS_SUCCESS;
  }
  return AMDSMI_STATUS_NOT_SUPPORTED;
}

amdsmi_status_t amdsmi_get_lib_version(amdsmi_version_t *version) {
  if (!version) return AMDSMI_STATUS_INVAL;
  version->major = 26;
  version->minor = 2;
  version->release = 0;
  version->build = "gemstone";
  return AMDSMI_STATUS_SUCCESS;
}

amdsmi_status_t amdsmi_status_code_to_string(amdsmi_status_t status, const char **statusString) {
  if (!statusString) return AMDSMI_STATUS_INVAL;
  switch (status) {
    case AMDSMI_STATUS_SUCCESS: *statusString = "AMDSMI_STATUS_SUCCESS"; return AMDSMI_STATUS_SUCCESS;
    case AMDSMI_STATUS_INVAL: *statusString = "AMDSMI_STATUS_INVAL"; return AMDSMI_STATUS_SUCCESS;
    case AMDSMI_STATUS_NOT_SUPPORTED: *statusString = "AMDSMI_STATUS_NOT_SUPPORTED"; return AMDSMI_STATUS_SUCCESS;
    case AMDSMI_STATUS_NOT_FOUND: *statusString = "AMDSMI_STATUS_NOT_FOUND"; return AMDSMI_STATUS_SUCCESS;
    case AMDSMI_STATUS_INSUFFICIENT_SIZE: *statusString = "AMDSMI_STATUS_INSUFFICIENT_SIZE"; return AMDSMI_STATUS_SUCCESS;
    default: *statusString = "AMDSMI_STATUS_UNKNOWN_ERROR"; return AMDSMI_STATUS_SUCCESS;
  }
}

amdsmi_status_t amdsmi_get_gpu_compute_process_info(amdsmi_process_info_t * /*procs*/, uint32_t *numAllocated) {
  if (!numAllocated) return AMDSMI_STATUS_INVAL;
  *numAllocated = 0;
  return AMDSMI_STATUS_SUCCESS;
}

amdsmi_status_t amdsmi_get_gpu_process_list(amdsmi_processor_handle processorHandle, uint32_t *numProcess,
                                            amdsmi_proc_info_t * /*proc_list*/) {
  if (!numProcess || !validHandle(processorHandle)) return AMDSMI_STATUS_INVAL;
  *numProcess = 0;
  return AMDSMI_STATUS_SUCCESS;
}

amdsmi_status_t amdsmi_get_gpu_driver_info(amdsmi_processor_handle processorHandle, amdsmi_driver_info_t *info) {
  if (!info || !validHandle(processorHandle)) return AMDSMI_STATUS_INVAL;
  std::memset(info, 0, sizeof(*info));
  std::snprintf(info->driver_name, sizeof(info->driver_name), "amdgpu");
  std::snprintf(info->driver_version, sizeof(info->driver_version), "gemstone");
  return AMDSMI_STATUS_SUCCESS;
}

amdsmi_status_t amdsmi_get_gpu_vbios_info(amdsmi_processor_handle processorHandle, amdsmi_vbios_info_t *info) {
  if (!info || !validHandle(processorHandle)) return AMDSMI_STATUS_INVAL;
  std::memset(info, 0, sizeof(*info));
  std::snprintf(info->name, sizeof(info->name), "Gemstone Generic VBIOS");
  std::snprintf(info->version, sizeof(info->version), "1.0");
  return AMDSMI_STATUS_SUCCESS;
}

amdsmi_status_t amdsmi_get_power_info(amdsmi_processor_handle processorHandle, amdsmi_power_info_t * /*info*/) {
  if (!validHandle(processorHandle)) return AMDSMI_STATUS_INVAL;
  return AMDSMI_STATUS_NOT_SUPPORTED;
}

amdsmi_status_t amdsmi_get_temp_metric(amdsmi_processor_handle processorHandle, amdsmi_temperature_type_t /*type*/,
                                       amdsmi_temperature_metric_t /*metric*/, int64_t * /*temperature*/) {
  if (!validHandle(processorHandle)) return AMDSMI_STATUS_INVAL;
  return AMDSMI_STATUS_NOT_SUPPORTED;
}

GEMSTONE_PUBLIC_API void amdsmi_free_name_value_pairs(void * /*ptr*/) noexcept {}

} // extern "C"
