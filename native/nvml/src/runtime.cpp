#include <charconv>
#include <cstdint>
#include <cstdio>
#include <cstring>
#include <string>
#include <string_view>
#include <system_error>
#include <utility>

#include <nvml.h>

#include "gemstone_nvml_api.h"
#include <gemstone/devices.hpp>
#include <gemstone/environment.hpp>
#include <gemstone/export.h>
#include <gemstone_device_presets.hpp>

namespace {

bool validOrdinal(uint32_t ordinal) noexcept { return ordinal < gemstone::deviceCount(); }

bool validDevice(nvmlDevice_t device) noexcept {
  if (!device) return false;
  const auto value = reinterpret_cast<uintptr_t>(device);
  return value > 0 && validOrdinal(static_cast<uint32_t>(value - 1));
}

uint32_t deviceOrdinal(nvmlDevice_t device) noexcept { return static_cast<uint32_t>(reinterpret_cast<uintptr_t>(device) - 1); }

nvmlDevice_t toDevice(uint32_t ordinal) noexcept { return reinterpret_cast<nvmlDevice_t>(static_cast<uintptr_t>(ordinal + 1)); }

gemstone::DevicePreset activePreset() noexcept {
  if (const auto preset = gemstone::environment::selectedDevicePreset()) return *preset;
  return gemstone::devicePresets[0];
}

nvmlReturn_t copyString(char *destination, unsigned int length, std::string_view source) noexcept {
  if (!destination || length == 0) return NVML_ERROR_INVALID_ARGUMENT;
  if (length <= source.size()) return NVML_ERROR_INSUFFICIENT_SIZE;
  std::memcpy(destination, source.data(), source.size());
  destination[source.size()] = '\0';
  return NVML_SUCCESS;
}

std::string formatUuid(uint32_t ordinal) {
  const auto bytes = gemstone::deviceUuid(ordinal);
  char buffer[48];
  std::snprintf(buffer, sizeof(buffer), "GPU-%02x%02x%02x%02x-%02x%02x-%02x%02x-%02x%02x-%02x%02x%02x%02x%02x%02x", bytes[0], bytes[1],
                bytes[2], bytes[3], bytes[4], bytes[5], bytes[6], bytes[7], bytes[8], bytes[9], bytes[10], bytes[11], bytes[12], bytes[13],
                bytes[14], bytes[15]);
  return buffer;
}

std::pair<int, int> parseCapability(std::string_view target) noexcept {
  if (target.starts_with("sm_") && target.size() > 3) {
    int value = 0;
    const std::string_view sub(target.data() + 3, target.size() - 3);
    const auto [ptr, ec] = std::from_chars(sub.data(), sub.data() + sub.size(), value);
    if (ec == std::errc{} && value > 0) return {value / 10, value % 10};
  }
  return {8, 0};
}

} // namespace

extern "C" {

nvmlReturn_t nvmlInit(void) { return NVML_SUCCESS; }

nvmlReturn_t nvmlInit_v2(void) { return NVML_SUCCESS; }

nvmlReturn_t nvmlInitWithFlags(unsigned int) { return NVML_SUCCESS; }

nvmlReturn_t nvmlShutdown(void) { return NVML_SUCCESS; }

nvmlReturn_t nvmlSystemGetDriverVersion(char *version, unsigned int length) { return copyString(version, length, "580.65.06"); }

nvmlReturn_t nvmlSystemGetNVMLVersion(char *version, unsigned int length) { return copyString(version, length, "13.580.65.06"); }

nvmlReturn_t nvmlSystemGetCudaDriverVersion(int *cudaDriverVersion) {
  if (!cudaDriverVersion) return NVML_ERROR_INVALID_ARGUMENT;
  *cudaDriverVersion = 13030;
  return NVML_SUCCESS;
}

nvmlReturn_t nvmlSystemGetCudaDriverVersion_v2(int *cudaDriverVersion) { return nvmlSystemGetCudaDriverVersion(cudaDriverVersion); }

nvmlReturn_t nvmlSystemGetProcessName(unsigned int, char *name, unsigned int length) { return copyString(name, length, "unknown"); }

nvmlReturn_t nvmlDeviceGetCount(unsigned int *deviceCount) {
  if (!deviceCount) return NVML_ERROR_INVALID_ARGUMENT;
  *deviceCount = gemstone::deviceCount();
  return NVML_SUCCESS;
}

nvmlReturn_t nvmlDeviceGetCount_v2(unsigned int *deviceCount) { return nvmlDeviceGetCount(deviceCount); }

nvmlReturn_t nvmlDeviceGetHandleByIndex(unsigned int index, nvmlDevice_t *device) {
  if (!device) return NVML_ERROR_INVALID_ARGUMENT;
  if (!validOrdinal(index)) return NVML_ERROR_NOT_FOUND;
  *device = toDevice(index);
  return NVML_SUCCESS;
}

nvmlReturn_t nvmlDeviceGetHandleByIndex_v2(unsigned int index, nvmlDevice_t *device) { return nvmlDeviceGetHandleByIndex(index, device); }

nvmlReturn_t nvmlDeviceGetHandleByUUID(const char *uuid, nvmlDevice_t *device) {
  if (!uuid || !device) return NVML_ERROR_INVALID_ARGUMENT;
  const auto count = gemstone::deviceCount();
  for (uint32_t i = 0; i < count; ++i) {
    const auto expected = formatUuid(i);
    if (expected == uuid || std::string_view(expected).substr(4) == uuid) {
      *device = toDevice(i);
      return NVML_SUCCESS;
    }
  }
  return NVML_ERROR_NOT_FOUND;
}

nvmlReturn_t nvmlDeviceGetHandleByPciBusId_v2(const char *pciBusId, nvmlDevice_t *device) {
  if (!pciBusId || !device) return NVML_ERROR_INVALID_ARGUMENT;
  const auto count = gemstone::deviceCount();
  for (uint32_t i = 0; i < count; ++i) {
    nvmlPciInfo_t info{};
    if (nvmlDeviceGetPciInfo_v3(toDevice(i), &info) == NVML_SUCCESS
        && (std::string_view(info.busId) == pciBusId || std::string_view(info.busIdLegacy) == pciBusId
            || gemstone::devicePciBusId(i) == pciBusId)) {
      *device = toDevice(i);
      return NVML_SUCCESS;
    }
  }
  return NVML_ERROR_NOT_FOUND;
}

nvmlReturn_t nvmlDeviceGetHandleByPciBusId(const char *pciBusId, nvmlDevice_t *device) {
  return nvmlDeviceGetHandleByPciBusId_v2(pciBusId, device);
}

nvmlReturn_t nvmlDeviceGetIndex(nvmlDevice_t device, unsigned int *index) {
  if (!index || !validDevice(device)) return NVML_ERROR_INVALID_ARGUMENT;
  *index = deviceOrdinal(device);
  return NVML_SUCCESS;
}

nvmlReturn_t nvmlDeviceGetName(nvmlDevice_t device, char *name, unsigned int length) {
  if (!validDevice(device)) return NVML_ERROR_INVALID_ARGUMENT;
  return copyString(name, length, activePreset().name);
}

nvmlReturn_t nvmlDeviceGetUUID(nvmlDevice_t device, char *uuid, unsigned int length) {
  if (!validDevice(device)) return NVML_ERROR_INVALID_ARGUMENT;
  return copyString(uuid, length, formatUuid(deviceOrdinal(device)));
}

nvmlReturn_t nvmlDeviceGetPciInfo_v3(nvmlDevice_t device, nvmlPciInfo_t *pci) {
  if (!pci || !validDevice(device)) return NVML_ERROR_INVALID_ARGUMENT;
  const auto ordinal = deviceOrdinal(device);
  const auto address = gemstone::devicePciAddress(ordinal);
  std::memset(pci, 0, sizeof(*pci));
  pci->domain = address.domain;
  pci->bus = address.bus;
  pci->device = address.device;
  pci->pciDeviceId = (0x1000U << 16U) | 0x10deU;
  std::snprintf(pci->busIdLegacy, sizeof(pci->busIdLegacy), "%04x:%02x:%02x.%u", address.domain, address.bus, address.device,
                address.function);
  std::snprintf(pci->busId, sizeof(pci->busId), "%08x:%02x:%02x.%u", address.domain, address.bus, address.device, address.function);
  return NVML_SUCCESS;
}

nvmlReturn_t nvmlDeviceGetPciInfoExt(nvmlDevice_t device, nvmlPciInfoExt_t *pci) {
  if (!pci || !validDevice(device)) return NVML_ERROR_INVALID_ARGUMENT;
  const auto ordinal = deviceOrdinal(device);
  const auto address = gemstone::devicePciAddress(ordinal);
  pci->version = nvmlPciInfoExt_v1;
  pci->domain = address.domain;
  pci->bus = address.bus;
  pci->device = address.device;
  pci->pciDeviceId = (0x1000U << 16U) | 0x10deU;
  pci->pciSubSystemId = 0;
  pci->baseClass = 0x03;
  pci->subClass = 0x00;
  std::snprintf(pci->busId, sizeof(pci->busId), "%08x:%02x:%02x.%u", address.domain, address.bus, address.device, address.function);
  return NVML_SUCCESS;
}

nvmlReturn_t nvmlDeviceGetMaxMigDeviceCount(nvmlDevice_t device, unsigned int *count) {
  if (!validDevice(device) || !count) return NVML_ERROR_INVALID_ARGUMENT;
  *count = 0;
  return NVML_SUCCESS;
}

nvmlReturn_t nvmlDeviceGetPciInfo_v2(nvmlDevice_t device, nvmlPciInfo_t *pci) { return nvmlDeviceGetPciInfo_v3(device, pci); }

nvmlReturn_t nvmlDeviceGetPciInfo(nvmlDevice_t device, nvmlPciInfo_t *pci) { return nvmlDeviceGetPciInfo_v3(device, pci); }

extern "C" GEMSTONE_PUBLIC_API nvmlReturn_t nvmlInternalGetExportTable(const void **table, const void *id) {
  if (!table || !id) return NVML_ERROR_INVALID_ARGUMENT;
  static constexpr uint64_t exportTable[1] = {sizeof(exportTable)};
  *table = exportTable;
  return NVML_SUCCESS;
}

nvmlReturn_t nvmlDeviceGetMemoryInfo(nvmlDevice_t device, nvmlMemory_t *memory) {
  if (!memory || !validDevice(device)) return NVML_ERROR_INVALID_ARGUMENT;
  const auto preset = activePreset();
  memory->total = static_cast<unsigned long long>(preset.memoryBytes);
  memory->used = 0;
  memory->free = memory->total;
  return NVML_SUCCESS;
}

nvmlReturn_t nvmlDeviceGetMemoryInfo_v2(nvmlDevice_t device, nvmlMemory_v2_t *memory) {
  if (!memory || !validDevice(device)) return NVML_ERROR_INVALID_ARGUMENT;
  const auto preset = activePreset();
  memory->version = 2;
  memory->total = static_cast<unsigned long long>(preset.memoryBytes);
  memory->reserved = 0;
  memory->used = 0;
  memory->free = memory->total;
  return NVML_SUCCESS;
}

nvmlReturn_t nvmlDeviceGetNumGpuCores(nvmlDevice_t device, unsigned int *numCores) {
  if (!numCores || !validDevice(device)) return NVML_ERROR_INVALID_ARGUMENT;
  *numCores = static_cast<unsigned int>(activePreset().multiprocessors);
  return NVML_SUCCESS;
}

nvmlReturn_t nvmlDeviceGetClockInfo(nvmlDevice_t device, nvmlClockType_t type, unsigned int *clock) {
  if (!clock || !validDevice(device)) return NVML_ERROR_INVALID_ARGUMENT;
  const auto preset = activePreset();
  switch (type) {
    case NVML_CLOCK_GRAPHICS:
    case NVML_CLOCK_SM:
    case NVML_CLOCK_VIDEO: *clock = static_cast<unsigned int>(preset.boostMhz); return NVML_SUCCESS;
    case NVML_CLOCK_MEM: *clock = static_cast<unsigned int>(preset.memoryMTps / 2); return NVML_SUCCESS;
    default: return NVML_ERROR_INVALID_ARGUMENT;
  }
}

nvmlReturn_t nvmlDeviceGetMaxClockInfo(nvmlDevice_t device, nvmlClockType_t type, unsigned int *clock) {
  return nvmlDeviceGetClockInfo(device, type, clock);
}

nvmlReturn_t nvmlDeviceGetCudaComputeCapability(nvmlDevice_t device, int *major, int *minor) {
  if (!major || !minor || !validDevice(device)) return NVML_ERROR_INVALID_ARGUMENT;
  const auto [maj, min] = parseCapability(activePreset().target);
  *major = maj;
  *minor = min;
  return NVML_SUCCESS;
}

nvmlReturn_t nvmlDeviceGetUtilizationRates(nvmlDevice_t device, nvmlUtilization_t *utilization) {
  if (!utilization || !validDevice(device)) return NVML_ERROR_INVALID_ARGUMENT;
  utilization->gpu = 0;
  utilization->memory = 0;
  return NVML_SUCCESS;
}

nvmlReturn_t nvmlDeviceGetComputeMode(nvmlDevice_t device, nvmlComputeMode_t *mode) {
  if (!mode || !validDevice(device)) return NVML_ERROR_INVALID_ARGUMENT;
  *mode = NVML_COMPUTEMODE_DEFAULT;
  return NVML_SUCCESS;
}

nvmlReturn_t nvmlDeviceGetPerformanceState(nvmlDevice_t device, nvmlPstates_t *pState) {
  if (!pState || !validDevice(device)) return NVML_ERROR_INVALID_ARGUMENT;
  *pState = NVML_PSTATE_0;
  return NVML_SUCCESS;
}

nvmlReturn_t nvmlDeviceGetPowerState(nvmlDevice_t device, nvmlPstates_t *pState) { return nvmlDeviceGetPerformanceState(device, pState); }

nvmlReturn_t nvmlDeviceGetComputeRunningProcesses(nvmlDevice_t device, unsigned int *infoCount, nvmlProcessInfo_v1_t *) {
  if (!infoCount || !validDevice(device)) return NVML_ERROR_INVALID_ARGUMENT;
  *infoCount = 0;
  return NVML_SUCCESS;
}

nvmlReturn_t nvmlDeviceGetComputeRunningProcesses_v2(nvmlDevice_t device, unsigned int *infoCount, nvmlProcessInfo_v2_t *) {
  if (!infoCount || !validDevice(device)) return NVML_ERROR_INVALID_ARGUMENT;
  *infoCount = 0;
  return NVML_SUCCESS;
}

nvmlReturn_t nvmlDeviceGetComputeRunningProcesses_v3(nvmlDevice_t device, unsigned int *infoCount, nvmlProcessInfo_t *) {
  if (!infoCount || !validDevice(device)) return NVML_ERROR_INVALID_ARGUMENT;
  *infoCount = 0;
  return NVML_SUCCESS;
}

nvmlReturn_t nvmlDeviceGetGraphicsRunningProcesses(nvmlDevice_t device, unsigned int *infoCount, nvmlProcessInfo_v1_t *) {
  if (!infoCount || !validDevice(device)) return NVML_ERROR_INVALID_ARGUMENT;
  *infoCount = 0;
  return NVML_SUCCESS;
}

nvmlReturn_t nvmlDeviceGetGraphicsRunningProcesses_v2(nvmlDevice_t device, unsigned int *infoCount, nvmlProcessInfo_v2_t *) {
  if (!infoCount || !validDevice(device)) return NVML_ERROR_INVALID_ARGUMENT;
  *infoCount = 0;
  return NVML_SUCCESS;
}

nvmlReturn_t nvmlDeviceGetGraphicsRunningProcesses_v3(nvmlDevice_t device, unsigned int *infoCount, nvmlProcessInfo_t *) {
  if (!infoCount || !validDevice(device)) return NVML_ERROR_INVALID_ARGUMENT;
  *infoCount = 0;
  return NVML_SUCCESS;
}

nvmlReturn_t nvmlDeviceGetMPSComputeRunningProcesses(nvmlDevice_t device, unsigned int *infoCount, nvmlProcessInfo_v1_t *) {
  if (!infoCount || !validDevice(device)) return NVML_ERROR_INVALID_ARGUMENT;
  *infoCount = 0;
  return NVML_SUCCESS;
}

nvmlReturn_t nvmlDeviceGetMPSComputeRunningProcesses_v2(nvmlDevice_t device, unsigned int *infoCount, nvmlProcessInfo_v2_t *) {
  if (!infoCount || !validDevice(device)) return NVML_ERROR_INVALID_ARGUMENT;
  *infoCount = 0;
  return NVML_SUCCESS;
}

nvmlReturn_t nvmlDeviceGetMPSComputeRunningProcesses_v3(nvmlDevice_t device, unsigned int *infoCount, nvmlProcessInfo_t *) {
  if (!infoCount || !validDevice(device)) return NVML_ERROR_INVALID_ARGUMENT;
  *infoCount = 0;
  return NVML_SUCCESS;
}

nvmlReturn_t nvmlDeviceGetPersistenceMode(nvmlDevice_t device, nvmlEnableState_t *mode) {
  if (!mode || !validDevice(device)) return NVML_ERROR_INVALID_ARGUMENT;
  *mode = NVML_FEATURE_ENABLED;
  return NVML_SUCCESS;
}

nvmlReturn_t nvmlDeviceGetDisplayMode(nvmlDevice_t device, nvmlEnableState_t *mode) {
  if (!mode || !validDevice(device)) return NVML_ERROR_INVALID_ARGUMENT;
  *mode = NVML_FEATURE_DISABLED;
  return NVML_SUCCESS;
}

nvmlReturn_t nvmlDeviceGetBrand(nvmlDevice_t device, nvmlBrandType_t *type) {
  if (!type || !validDevice(device)) return NVML_ERROR_INVALID_ARGUMENT;
  *type = NVML_BRAND_GEFORCE;
  return NVML_SUCCESS;
}

nvmlReturn_t nvmlDeviceGetSerial(nvmlDevice_t device, char *serial, unsigned int length) {
  if (!validDevice(device)) return NVML_ERROR_INVALID_ARGUMENT;
  return copyString(serial, length, "0000000000000");
}

nvmlReturn_t nvmlDeviceGetBoardPartNumber(nvmlDevice_t device, char *partNumber, unsigned int length) {
  if (!validDevice(device)) return NVML_ERROR_INVALID_ARGUMENT;
  return copyString(partNumber, length, "900-1G180-0000-000");
}

nvmlReturn_t nvmlDeviceGetInforomVersion(nvmlDevice_t device, nvmlInforomObject_t, char *version, unsigned int length) {
  if (!validDevice(device)) return NVML_ERROR_INVALID_ARGUMENT;
  return copyString(version, length, "N/A");
}

nvmlReturn_t nvmlDeviceGetInforomImageVersion(nvmlDevice_t device, char *version, unsigned int length) {
  if (!validDevice(device)) return NVML_ERROR_INVALID_ARGUMENT;
  return copyString(version, length, "N/A");
}

nvmlReturn_t nvmlDeviceGetVbiosVersion(nvmlDevice_t device, char *version, unsigned int length) {
  if (!validDevice(device)) return NVML_ERROR_INVALID_ARGUMENT;
  return copyString(version, length, "90.04.28.00.01");
}

nvmlReturn_t nvmlDeviceGetPowerManagementMode(nvmlDevice_t device, nvmlEnableState_t *mode) {
  if (!mode || !validDevice(device)) return NVML_ERROR_INVALID_ARGUMENT;
  *mode = NVML_FEATURE_DISABLED;
  return NVML_SUCCESS;
}

nvmlReturn_t nvmlDeviceGetPowerManagementLimit(nvmlDevice_t device, unsigned int *limit) {
  if (!limit || !validDevice(device)) return NVML_ERROR_INVALID_ARGUMENT;
  *limit = 250000;
  return NVML_SUCCESS;
}

nvmlReturn_t nvmlDeviceGetPowerManagementDefaultLimit(nvmlDevice_t device, unsigned int *limit) {
  return nvmlDeviceGetPowerManagementLimit(device, limit);
}

nvmlReturn_t nvmlDeviceGetSupportedEventTypes(nvmlDevice_t device, unsigned long long *eventTypes) {
  if (!validDevice(device) || !eventTypes) return NVML_ERROR_INVALID_ARGUMENT;
  *eventTypes = 0;
  return NVML_SUCCESS;
}

nvmlReturn_t nvmlDeviceRegisterEvents(nvmlDevice_t device, unsigned long long, nvmlEventSet_t) {
  if (!validDevice(device)) return NVML_ERROR_INVALID_ARGUMENT;
  return NVML_SUCCESS;
}

nvmlReturn_t nvmlEventSetCreate(nvmlEventSet_t *set) {
  if (!set) return NVML_ERROR_INVALID_ARGUMENT;
  *set = reinterpret_cast<nvmlEventSet_t>(uintptr_t{1});
  return NVML_SUCCESS;
}

nvmlReturn_t nvmlEventSetFree(nvmlEventSet_t set) {
  if (!set) return NVML_ERROR_INVALID_ARGUMENT;
  return NVML_SUCCESS;
}

nvmlReturn_t nvmlEventSetWait(nvmlEventSet_t set, nvmlEventData_t *, unsigned int) {
  if (!set) return NVML_ERROR_INVALID_ARGUMENT;
  return NVML_ERROR_TIMEOUT;
}

nvmlReturn_t nvmlEventSetWait_v2(nvmlEventSet_t set, nvmlEventData_t *, unsigned int) {
  if (!set) return NVML_ERROR_INVALID_ARGUMENT;
  return NVML_ERROR_TIMEOUT;
}

nvmlReturn_t nvmlDeviceGetEnforcedPowerLimit(nvmlDevice_t device, unsigned int *limit) {
  return nvmlDeviceGetPowerManagementLimit(device, limit);
}

const char *nvmlErrorString(nvmlReturn_t result) {
  switch (result) {
    case NVML_SUCCESS: return "Success";
    case NVML_ERROR_UNINITIALIZED: return "Uninitialized";
    case NVML_ERROR_INVALID_ARGUMENT: return "Invalid Argument";
    case NVML_ERROR_NOT_SUPPORTED: return "Not Supported";
    case NVML_ERROR_NO_PERMISSION: return "No Permission";
    case NVML_ERROR_ALREADY_INITIALIZED: return "Already Initialized";
    case NVML_ERROR_NOT_FOUND: return "Not Found";
    case NVML_ERROR_INSUFFICIENT_SIZE: return "Insufficient Size";
    case NVML_ERROR_INSUFFICIENT_POWER: return "Insufficient Power";
    case NVML_ERROR_DRIVER_NOT_LOADED: return "Driver Not Loaded";
    case NVML_ERROR_TIMEOUT: return "Timeout";
    case NVML_ERROR_IRQ_ISSUE: return "Interrupt Request Issue";
    case NVML_ERROR_LIBRARY_NOT_FOUND: return "NVML Shared Library Not Found";
    case NVML_ERROR_FUNCTION_NOT_FOUND: return "Function Not Found";
    case NVML_ERROR_CORRUPTED_INFOROM: return "Corrupted infoROM";
    case NVML_ERROR_GPU_IS_LOST: return "GPU is lost";
    case NVML_ERROR_RESET_REQUIRED: return "GPU requires reset";
    case NVML_ERROR_OPERATING_SYSTEM: return "The operating system has blocked the GPU";
    case NVML_ERROR_LIB_RM_VERSION_MISMATCH: return "RM has detected an NVML/RM version mismatch";
    case NVML_ERROR_IN_USE: return "GPU is currently in use";
    case NVML_ERROR_MEMORY: return "Insufficient Memory";
    case NVML_ERROR_NO_DATA: return "No Data";
    case NVML_ERROR_VGPU_ECC_NOT_SUPPORTED: return "VGPU ECC Not Supported";
    default: return "Unknown Error";
  }
}

} // extern "C"
