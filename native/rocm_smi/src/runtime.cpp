#include <algorithm>
#include <cstdint>
#include <cstdio>
#include <cstring>
#include <string_view>

#include "gemstone_rocm_smi_api.h"
#include <gemstone/devices.hpp>
#include <gemstone/environment.hpp>
#include <gemstone_device_presets.hpp>

#include <rocm_smi/rocm_smi.h>

namespace {

bool validOrdinal(uint32_t ordinal) noexcept { return ordinal < gemstone::deviceCount(); }

gemstone::DevicePreset activePreset() noexcept {
  if (const auto preset = gemstone::environment::selectedDevicePreset()) return *preset;
  return gemstone::devicePresets[0];
}

rsmi_status_t copyString(char *destination, size_t length, std::string_view source) noexcept {
  if (!destination || length == 0) return RSMI_STATUS_INVALID_ARGS;
  if (length <= source.size()) return RSMI_STATUS_INSUFFICIENT_SIZE;
  std::memcpy(destination, source.data(), source.size());
  destination[source.size()] = '\0';
  return RSMI_STATUS_SUCCESS;
}

} // namespace

extern "C" {

rsmi_status_t rsmi_init(uint64_t /*flags*/) { return RSMI_STATUS_SUCCESS; }

rsmi_status_t rsmi_shut_down() { return RSMI_STATUS_SUCCESS; }

rsmi_status_t rsmi_num_monitor_devices(uint32_t *numDevices) {
  if (!numDevices) return RSMI_STATUS_INVALID_ARGS;
  *numDevices = gemstone::deviceCount();
  return RSMI_STATUS_SUCCESS;
}

rsmi_status_t rsmi_dev_id_get(uint32_t dvInd, uint16_t *id) {
  if (!id) return RSMI_STATUS_INVALID_ARGS;
  if (!validOrdinal(dvInd)) return RSMI_STATUS_INPUT_OUT_OF_BOUNDS;
  *id = 0x744cU;
  return RSMI_STATUS_SUCCESS;
}

rsmi_status_t rsmi_dev_sku_get(uint32_t dvInd, uint16_t *sku) {
  if (!sku) return RSMI_STATUS_INVALID_ARGS;
  if (!validOrdinal(dvInd)) return RSMI_STATUS_INPUT_OUT_OF_BOUNDS;
  *sku = 0;
  return RSMI_STATUS_SUCCESS;
}

rsmi_status_t rsmi_dev_vendor_id_get(uint32_t dvInd, uint16_t *id) {
  if (!id) return RSMI_STATUS_INVALID_ARGS;
  if (!validOrdinal(dvInd)) return RSMI_STATUS_INPUT_OUT_OF_BOUNDS;
  *id = 0x1002U;
  return RSMI_STATUS_SUCCESS;
}

rsmi_status_t rsmi_dev_subsystem_id_get(uint32_t dvInd, uint16_t *id) {
  if (!id) return RSMI_STATUS_INVALID_ARGS;
  if (!validOrdinal(dvInd)) return RSMI_STATUS_INPUT_OUT_OF_BOUNDS;
  *id = 0x0b35U;
  return RSMI_STATUS_SUCCESS;
}

rsmi_status_t rsmi_dev_subsystem_vendor_id_get(uint32_t dvInd, uint16_t *id) {
  if (!id) return RSMI_STATUS_INVALID_ARGS;
  if (!validOrdinal(dvInd)) return RSMI_STATUS_INPUT_OUT_OF_BOUNDS;
  *id = 0x1002U;
  return RSMI_STATUS_SUCCESS;
}

rsmi_status_t rsmi_dev_vendor_name_get(uint32_t dvInd, char *name, size_t len) {
  if (!validOrdinal(dvInd)) return RSMI_STATUS_INPUT_OUT_OF_BOUNDS;
  return copyString(name, len, "Advanced Micro Devices, Inc. [AMD/ATI]");
}

rsmi_status_t rsmi_dev_subsystem_name_get(uint32_t dvInd, char *name, size_t len) {
  if (!validOrdinal(dvInd)) return RSMI_STATUS_INPUT_OUT_OF_BOUNDS;
  return copyString(name, len, "Advanced Micro Devices, Inc. [AMD/ATI]");
}

rsmi_status_t rsmi_dev_name_get(uint32_t dvInd, char *name, size_t len) {
  if (!validOrdinal(dvInd)) return RSMI_STATUS_INPUT_OUT_OF_BOUNDS;
  return copyString(name, len, activePreset().name);
}

rsmi_status_t rsmi_dev_brand_get(uint32_t dvInd, char *brand, uint32_t len) {
  if (!validOrdinal(dvInd)) return RSMI_STATUS_INPUT_OUT_OF_BOUNDS;
  return copyString(brand, len, "AMD");
}

rsmi_status_t rsmi_dev_market_name_get(uint32_t dvInd, char *name, uint32_t len) {
  if (!validOrdinal(dvInd)) return RSMI_STATUS_INPUT_OUT_OF_BOUNDS;
  return copyString(name, len, activePreset().name);
}

rsmi_status_t rsmi_dev_pci_id_get(uint32_t dvInd, uint64_t *bdfid) {
  if (!bdfid) return RSMI_STATUS_INVALID_ARGS;
  if (!validOrdinal(dvInd)) return RSMI_STATUS_INPUT_OUT_OF_BOUNDS;
  *bdfid = gemstone::devicePciAddress(dvInd).bdfid();
  return RSMI_STATUS_SUCCESS;
}

rsmi_status_t rsmi_dev_pci_bandwidth_get(uint32_t dvInd, rsmi_pcie_bandwidth_t *bandwidth) {
  if (!bandwidth) return RSMI_STATUS_INVALID_ARGS;
  if (!validOrdinal(dvInd)) return RSMI_STATUS_INPUT_OUT_OF_BOUNDS;
  bandwidth->transfer_rate.num_supported = 1;
  bandwidth->transfer_rate.current = 0;
  bandwidth->transfer_rate.frequency[0] = 16000000000ULL;
  bandwidth->lanes[0] = 16;
  return RSMI_STATUS_SUCCESS;
}

rsmi_status_t rsmi_dev_unique_id_get(uint32_t dvInd, uint64_t *id) {
  if (!id) return RSMI_STATUS_INVALID_ARGS;
  if (!validOrdinal(dvInd)) return RSMI_STATUS_INPUT_OUT_OF_BOUNDS;
  *id = gemstone::deviceUniqueId(dvInd);
  return RSMI_STATUS_SUCCESS;
}

rsmi_status_t rsmi_dev_guid_get(uint32_t dvInd, uint64_t *guid) {
  if (!guid) return RSMI_STATUS_INVALID_ARGS;
  if (!validOrdinal(dvInd)) return RSMI_STATUS_INPUT_OUT_OF_BOUNDS;
  *guid = 1000ULL + dvInd;
  return RSMI_STATUS_SUCCESS;
}

rsmi_status_t rsmi_dev_node_id_get(uint32_t dvInd, uint32_t *nodeId) {
  if (!nodeId) return RSMI_STATUS_INVALID_ARGS;
  if (!validOrdinal(dvInd)) return RSMI_STATUS_INPUT_OUT_OF_BOUNDS;
  *nodeId = 1U + dvInd;
  return RSMI_STATUS_SUCCESS;
}

rsmi_status_t rsmi_dev_partition_id_get(uint32_t dvInd, uint32_t *partitionId) {
  if (!partitionId) return RSMI_STATUS_INVALID_ARGS;
  if (!validOrdinal(dvInd)) return RSMI_STATUS_INPUT_OUT_OF_BOUNDS;
  *partitionId = 0;
  return RSMI_STATUS_SUCCESS;
}

rsmi_status_t rsmi_dev_target_graphics_version_get(uint32_t dvInd, uint64_t *targetVersion) {
  if (!targetVersion) return RSMI_STATUS_INVALID_ARGS;
  if (!validOrdinal(dvInd)) return RSMI_STATUS_INPUT_OUT_OF_BOUNDS;
  *targetVersion = gemstone::graphicsVersion(activePreset().target);
  return RSMI_STATUS_SUCCESS;
}

rsmi_status_t rsmi_dev_drm_render_minor_get(uint32_t dvInd, uint32_t *minor) {
  if (!minor) return RSMI_STATUS_INVALID_ARGS;
  if (!validOrdinal(dvInd)) return RSMI_STATUS_INPUT_OUT_OF_BOUNDS;
  *minor = 128U + dvInd;
  return RSMI_STATUS_SUCCESS;
}

rsmi_status_t rsmi_dev_revision_get(uint32_t dvInd, uint16_t *revision) {
  if (!revision) return RSMI_STATUS_INVALID_ARGS;
  if (!validOrdinal(dvInd)) return RSMI_STATUS_INPUT_OUT_OF_BOUNDS;
  *revision = 0;
  return RSMI_STATUS_SUCCESS;
}

rsmi_status_t rsmi_dev_memory_total_get(uint32_t dvInd, rsmi_memory_type_t memType, uint64_t *total) {
  if (!total) return RSMI_STATUS_INVALID_ARGS;
  if (!validOrdinal(dvInd)) return RSMI_STATUS_INPUT_OUT_OF_BOUNDS;
  const auto preset = activePreset();
  if (memType == RSMI_MEM_TYPE_VRAM) {
    *total = static_cast<uint64_t>(preset.memoryBytes);
    return RSMI_STATUS_SUCCESS;
  }
  if (memType == RSMI_MEM_TYPE_VIS_VRAM) {
    *total = std::min<uint64_t>(preset.memoryBytes, 256ULL * 1024ULL * 1024ULL);
    return RSMI_STATUS_SUCCESS;
  }
  if (memType == RSMI_MEM_TYPE_GTT) {
    *total = static_cast<uint64_t>(preset.memoryBytes);
    return RSMI_STATUS_SUCCESS;
  }
  return RSMI_STATUS_NOT_SUPPORTED;
}

rsmi_status_t rsmi_dev_memory_usage_get(uint32_t dvInd, rsmi_memory_type_t memType, uint64_t *used) {
  if (!used) return RSMI_STATUS_INVALID_ARGS;
  if (!validOrdinal(dvInd)) return RSMI_STATUS_INPUT_OUT_OF_BOUNDS;
  if (memType == RSMI_MEM_TYPE_VRAM || memType == RSMI_MEM_TYPE_VIS_VRAM || memType == RSMI_MEM_TYPE_GTT) {
    *used = 0;
    return RSMI_STATUS_SUCCESS;
  }
  return RSMI_STATUS_NOT_SUPPORTED;
}

rsmi_status_t rsmi_dev_memory_busy_percent_get(uint32_t dvInd, uint32_t *busyPercent) {
  if (!busyPercent) return RSMI_STATUS_INVALID_ARGS;
  if (!validOrdinal(dvInd)) return RSMI_STATUS_INPUT_OUT_OF_BOUNDS;
  *busyPercent = 0;
  return RSMI_STATUS_SUCCESS;
}

rsmi_status_t rsmi_dev_busy_percent_get(uint32_t dvInd, uint32_t *busyPercent) {
  if (!busyPercent) return RSMI_STATUS_INVALID_ARGS;
  if (!validOrdinal(dvInd)) return RSMI_STATUS_INPUT_OUT_OF_BOUNDS;
  *busyPercent = 0;
  return RSMI_STATUS_SUCCESS;
}

rsmi_status_t rsmi_dev_gpu_clk_freq_get(uint32_t dvInd, rsmi_clk_type_t clkType, rsmi_frequencies_t *f) {
  if (!f) return RSMI_STATUS_INVALID_ARGS;
  if (!validOrdinal(dvInd)) return RSMI_STATUS_INPUT_OUT_OF_BOUNDS;
  const auto preset = activePreset();
  f->num_supported = 1;
  f->current = 0;
  if (clkType == RSMI_CLK_TYPE_SYS) {
    f->frequency[0] = static_cast<uint64_t>(preset.boostMhz) * 1000000ULL;
    return RSMI_STATUS_SUCCESS;
  }
  if (clkType == RSMI_CLK_TYPE_MEM) {
    f->frequency[0] = static_cast<uint64_t>(preset.memoryMTps / 2) * 1000000ULL;
    return RSMI_STATUS_SUCCESS;
  }
  return RSMI_STATUS_NOT_SUPPORTED;
}

rsmi_status_t rsmi_compute_process_info_get(rsmi_process_info_t * /*procs*/, uint32_t *numAllocated) {
  if (!numAllocated) return RSMI_STATUS_INVALID_ARGS;
  *numAllocated = 0;
  return RSMI_STATUS_SUCCESS;
}

rsmi_status_t rsmi_compute_process_gpus_get(uint32_t /*pid*/, uint32_t * /*dv_indices*/, uint32_t *numDevices) {
  if (!numDevices) return RSMI_STATUS_INVALID_ARGS;
  *numDevices = 0;
  return RSMI_STATUS_SUCCESS;
}

rsmi_status_t rsmi_compute_process_info_by_device_get(uint32_t dvInd, uint32_t /*numAllocated*/, rsmi_process_info_t * /*procs*/) {
  if (!validOrdinal(dvInd)) return RSMI_STATUS_INPUT_OUT_OF_BOUNDS;
  return RSMI_STATUS_SUCCESS;
}

rsmi_status_t rsmi_compute_process_info_by_pid_get(uint32_t /*pid*/, rsmi_process_info_t * /*procs*/) { return RSMI_STATUS_NOT_FOUND; }

rsmi_status_t rsmi_dev_power_get(uint32_t dvInd, uint64_t * /*power*/, RSMI_POWER_TYPE * /*type*/) {
  if (!validOrdinal(dvInd)) return RSMI_STATUS_INPUT_OUT_OF_BOUNDS;
  return RSMI_STATUS_NOT_SUPPORTED;
}

rsmi_status_t rsmi_dev_temp_metric_get(uint32_t dvInd, uint32_t /*sensor_type*/, rsmi_temperature_metric_t /*metric*/,
                                       int64_t * /*temperature*/) {
  if (!validOrdinal(dvInd)) return RSMI_STATUS_INPUT_OUT_OF_BOUNDS;
  return RSMI_STATUS_NOT_SUPPORTED;
}

rsmi_status_t rsmi_dev_volt_metric_get(uint32_t dvInd, rsmi_voltage_type_t /*sensor_type*/, rsmi_voltage_metric_t /*metric*/,
                                       int64_t * /*voltage*/) {
  if (!validOrdinal(dvInd)) return RSMI_STATUS_INPUT_OUT_OF_BOUNDS;
  return RSMI_STATUS_NOT_SUPPORTED;
}

rsmi_status_t rsmi_dev_fan_speed_get(uint32_t dvInd, uint32_t /*sensor_idx*/, int64_t * /*speed*/) {
  if (!validOrdinal(dvInd)) return RSMI_STATUS_INPUT_OUT_OF_BOUNDS;
  return RSMI_STATUS_NOT_SUPPORTED;
}

rsmi_status_t rsmi_dev_xgmi_error_status(uint32_t dvInd, rsmi_xgmi_status_t * /*status*/) {
  if (!validOrdinal(dvInd)) return RSMI_STATUS_INPUT_OUT_OF_BOUNDS;
  return RSMI_STATUS_NOT_SUPPORTED;
}

rsmi_status_t rsmi_version_get(rsmi_version_t *version) {
  if (!version) return RSMI_STATUS_INVALID_ARGS;
  version->major = 7;
  version->minor = 8;
  version->patch = 0;
  version->build = "gemstone";
  return RSMI_STATUS_SUCCESS;
}

rsmi_status_t rsmi_version_str_get(rsmi_sw_component_t /*component*/, char *versionStr, uint32_t len) {
  return copyString(versionStr, len, "7.1.1");
}

rsmi_status_t rsmi_status_string(rsmi_status_t status, const char **statusString) {
  if (!statusString) return RSMI_STATUS_INVALID_ARGS;
  switch (status) {
    case RSMI_STATUS_SUCCESS: *statusString = "RSMI_STATUS_SUCCESS"; return RSMI_STATUS_SUCCESS;
    case RSMI_STATUS_INVALID_ARGS: *statusString = "RSMI_STATUS_INVALID_ARGS"; return RSMI_STATUS_SUCCESS;
    case RSMI_STATUS_NOT_SUPPORTED: *statusString = "RSMI_STATUS_NOT_SUPPORTED"; return RSMI_STATUS_SUCCESS;
    case RSMI_STATUS_INPUT_OUT_OF_BOUNDS: *statusString = "RSMI_STATUS_INPUT_OUT_OF_BOUNDS"; return RSMI_STATUS_SUCCESS;
    case RSMI_STATUS_NOT_FOUND: *statusString = "RSMI_STATUS_NOT_FOUND"; return RSMI_STATUS_SUCCESS;
    case RSMI_STATUS_INSUFFICIENT_SIZE: *statusString = "RSMI_STATUS_INSUFFICIENT_SIZE"; return RSMI_STATUS_SUCCESS;
    default: *statusString = "RSMI_STATUS_UNKNOWN_ERROR"; return RSMI_STATUS_SUCCESS;
  }
}

} // extern "C"
