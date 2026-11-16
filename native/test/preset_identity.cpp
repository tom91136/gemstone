#include <array>
#include <cstdint>
#include <cstring>
#include <string>
#include <string_view>
#include <utility>
#include <vector>

#include <hsa.h>
#include <hsa_ext_amd.h>
#include <nvml.h>

#include <CL/cl.h>
#include <CL/cl_ext.h>
#include <catch2/catch_test_macros.hpp>
#include <cuda.h>
#include <hip/hip_runtime_api.h>
#include <hsakmt/hsakmt.h>
#include <ze_api.h>

#include <gemstone/test_support.hpp>
#include <gemstone_device_presets.hpp>

#include "../rpc/test/rpc_fixture.hpp"
#include <amd_smi/amdsmi.h>
#include <environment_keys.hpp>
#include <rocm_smi/rocm_smi.h>
#include <vulkan/vulkan.h>

namespace {

gemstone_test::RpcFixture fixture;
const std::string Endpoint = fixture.endpoint();
const gemstone_test::Environment RpcEndpoint(gemstone::environment::RpcEndpoint, Endpoint.c_str());
const gemstone_test::Environment RpcClient(gemstone::environment::RpcClientId, "42");
const gemstone_test::Environment DeviceCount(gemstone::environment::DeviceCount, "2");

struct RuntimeReport {
  std::string name;
  uint64_t memoryBytes = 0;
  int32_t multiprocessors = 0;
  int32_t boostMhz = 0;
  int32_t memoryClockMhz = 0;
  int32_t busWidthBits = 0;
  int32_t l2Bytes = 0;
  uint64_t uniqueId = 0;
  std::array<uint8_t, 16> uuid{};
  gemstone::PciAddress pci{};
  std::string pciBusId;
};

RuntimeReport queryCuda(int ordinal) {
  RuntimeReport report{};
  char name[256]{};
  REQUIRE(cuDeviceGetName(name, sizeof(name), ordinal) == CUDA_SUCCESS);
  report.name = name;
  size_t totalMemory = 0;
  REQUIRE(cuDeviceTotalMem_v2(&totalMemory, ordinal) == CUDA_SUCCESS);
  report.memoryBytes = totalMemory;
  int clockKhz = 0;
  int memoryClockKhz = 0;
  REQUIRE(cuDeviceGetAttribute(&report.multiprocessors, CU_DEVICE_ATTRIBUTE_MULTIPROCESSOR_COUNT, ordinal) == CUDA_SUCCESS);
  REQUIRE(cuDeviceGetAttribute(&clockKhz, CU_DEVICE_ATTRIBUTE_CLOCK_RATE, ordinal) == CUDA_SUCCESS);
  REQUIRE(cuDeviceGetAttribute(&memoryClockKhz, CU_DEVICE_ATTRIBUTE_MEMORY_CLOCK_RATE, ordinal) == CUDA_SUCCESS);
  REQUIRE(cuDeviceGetAttribute(&report.busWidthBits, CU_DEVICE_ATTRIBUTE_GLOBAL_MEMORY_BUS_WIDTH, ordinal) == CUDA_SUCCESS);
  REQUIRE(cuDeviceGetAttribute(&report.l2Bytes, CU_DEVICE_ATTRIBUTE_L2_CACHE_SIZE, ordinal) == CUDA_SUCCESS);
  report.boostMhz = clockKhz / 1000;
  report.memoryClockMhz = memoryClockKhz / 1000;
  CUuuid uuid{};
  REQUIRE(cuDeviceGetUuid_v2(&uuid, ordinal) == CUDA_SUCCESS);
  std::memcpy(report.uuid.data(), uuid.bytes, report.uuid.size());
  int domain = -1;
  int bus = -1;
  int device = -1;
  REQUIRE(cuDeviceGetAttribute(&domain, CU_DEVICE_ATTRIBUTE_PCI_DOMAIN_ID, ordinal) == CUDA_SUCCESS);
  REQUIRE(cuDeviceGetAttribute(&bus, CU_DEVICE_ATTRIBUTE_PCI_BUS_ID, ordinal) == CUDA_SUCCESS);
  REQUIRE(cuDeviceGetAttribute(&device, CU_DEVICE_ATTRIBUTE_PCI_DEVICE_ID, ordinal) == CUDA_SUCCESS);
  report.pci = {
      .domain = static_cast<uint32_t>(domain), .bus = static_cast<uint32_t>(bus), .device = static_cast<uint32_t>(device), .function = 0};
  char busId[32]{};
  REQUIRE(cuDeviceGetPCIBusId(busId, sizeof(busId), ordinal) == CUDA_SUCCESS);
  report.pciBusId = busId;
  return report;
}

RuntimeReport queryHip(int ordinal) {
  RuntimeReport report{};
  hipDeviceProp_tR0600 props{};
  REQUIRE(hipGetDevicePropertiesR0600(&props, ordinal) == hipSuccess);
  report.name = props.name;
  report.memoryBytes = props.totalGlobalMem;
  report.multiprocessors = props.multiProcessorCount;
  report.boostMhz = props.clockRate / 1000;
  report.memoryClockMhz = props.memoryClockRate / 1000;
  report.busWidthBits = props.memoryBusWidth;
  report.l2Bytes = props.l2CacheSize;
  hipUUID uuid{};
  REQUIRE(hipDeviceGetUuid(&uuid, ordinal) == hipSuccess);
  std::memcpy(report.uuid.data(), uuid.bytes, report.uuid.size());
  report.pci = {.domain = static_cast<uint32_t>(props.pciDomainID),
                .bus = static_cast<uint32_t>(props.pciBusID),
                .device = static_cast<uint32_t>(props.pciDeviceID),
                .function = 0};
  char busId[32]{};
  REQUIRE(hipDeviceGetPCIBusId(busId, sizeof(busId), ordinal) == hipSuccess);
  report.pciBusId = busId;
  return report;
}

RuntimeReport queryOpenCl(cl_device_id device) {
  RuntimeReport report{};
  char name[128]{};
  REQUIRE(clGetDeviceInfo(device, CL_DEVICE_NAME, sizeof(name), name, nullptr) == CL_SUCCESS);
  report.name = name;
  cl_ulong memoryBytes = 0;
  cl_uint computeUnits = 0;
  cl_uint clockMhz = 0;
  cl_ulong l2Bytes = 0;
  REQUIRE(clGetDeviceInfo(device, CL_DEVICE_GLOBAL_MEM_SIZE, sizeof(memoryBytes), &memoryBytes, nullptr) == CL_SUCCESS);
  REQUIRE(clGetDeviceInfo(device, CL_DEVICE_MAX_COMPUTE_UNITS, sizeof(computeUnits), &computeUnits, nullptr) == CL_SUCCESS);
  REQUIRE(clGetDeviceInfo(device, CL_DEVICE_MAX_CLOCK_FREQUENCY, sizeof(clockMhz), &clockMhz, nullptr) == CL_SUCCESS);
  REQUIRE(clGetDeviceInfo(device, CL_DEVICE_GLOBAL_MEM_CACHE_SIZE, sizeof(l2Bytes), &l2Bytes, nullptr) == CL_SUCCESS);
  report.memoryBytes = memoryBytes;
  report.multiprocessors = static_cast<int32_t>(computeUnits);
  report.boostMhz = static_cast<int32_t>(clockMhz);
  report.l2Bytes = static_cast<int32_t>(l2Bytes);
  REQUIRE(clGetDeviceInfo(device, CL_DEVICE_UUID_KHR, report.uuid.size(), report.uuid.data(), nullptr) == CL_SUCCESS);
  cl_device_pci_bus_info_khr pci{};
  REQUIRE(clGetDeviceInfo(device, CL_DEVICE_PCI_BUS_INFO_KHR, sizeof(pci), &pci, nullptr) == CL_SUCCESS);
  report.pci = {.domain = pci.pci_domain, .bus = pci.pci_bus, .device = pci.pci_device, .function = pci.pci_function};
  return report;
}

RuntimeReport queryLevelZero(ze_device_handle_t device) {
  RuntimeReport report{};
  ze_device_properties_t props{};
  props.stype = ZE_STRUCTURE_TYPE_DEVICE_PROPERTIES;
  REQUIRE(zeDeviceGetProperties(device, &props) == ZE_RESULT_SUCCESS);
  report.name = props.name;
  std::memcpy(report.uuid.data(), props.uuid.id, report.uuid.size());
  uint32_t memCount = 1;
  ze_device_memory_properties_t memProps{};
  memProps.stype = ZE_STRUCTURE_TYPE_DEVICE_MEMORY_PROPERTIES;
  REQUIRE(zeDeviceGetMemoryProperties(device, &memCount, &memProps) == ZE_RESULT_SUCCESS);
  report.memoryBytes = memProps.totalSize;
  ze_pci_ext_properties_t pciProps{};
  pciProps.stype = ZE_STRUCTURE_TYPE_PCI_EXT_PROPERTIES;
  REQUIRE(zeDevicePciGetPropertiesExt(device, &pciProps) == ZE_RESULT_SUCCESS);
  report.pci = {.domain = pciProps.address.domain,
                .bus = pciProps.address.bus,
                .device = pciProps.address.device,
                .function = pciProps.address.function};
  return report;
}

RuntimeReport queryVulkan(VkPhysicalDevice device) {
  RuntimeReport report{};
  VkPhysicalDevicePCIBusInfoPropertiesEXT pci{};
  pci.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PCI_BUS_INFO_PROPERTIES_EXT;
  VkPhysicalDeviceIDProperties id{};
  id.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ID_PROPERTIES;
  id.pNext = &pci;
  VkPhysicalDeviceProperties2 props{};
  props.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PROPERTIES_2;
  props.pNext = &id;
  vkGetPhysicalDeviceProperties2(device, &props);
  report.name = props.properties.deviceName;
  std::memcpy(report.uuid.data(), id.deviceUUID, report.uuid.size());
  report.pci = {.domain = pci.pciDomain, .bus = pci.pciBus, .device = pci.pciDevice, .function = pci.pciFunction};
  VkPhysicalDeviceMemoryProperties memProps{};
  vkGetPhysicalDeviceMemoryProperties(device, &memProps);
  report.memoryBytes = memProps.memoryHeaps[0].size;
  return report;
}

RuntimeReport queryNvml(unsigned int ordinal) {
  RuntimeReport report{};
  nvmlDevice_t handle = nullptr;
  REQUIRE(nvmlDeviceGetHandleByIndex_v2(ordinal, &handle) == NVML_SUCCESS);
  char name[256]{};
  REQUIRE(nvmlDeviceGetName(handle, name, sizeof(name)) == NVML_SUCCESS);
  report.name = name;
  nvmlMemory_t mem{};
  REQUIRE(nvmlDeviceGetMemoryInfo(handle, &mem) == NVML_SUCCESS);
  report.memoryBytes = mem.total;
  unsigned int clockMhz = 0;
  REQUIRE(nvmlDeviceGetClockInfo(handle, NVML_CLOCK_SM, &clockMhz) == NVML_SUCCESS);
  report.boostMhz = static_cast<int32_t>(clockMhz);
  unsigned int memClockMhz = 0;
  REQUIRE(nvmlDeviceGetClockInfo(handle, NVML_CLOCK_MEM, &memClockMhz) == NVML_SUCCESS);
  report.memoryClockMhz = static_cast<int32_t>(memClockMhz);
  unsigned int cores = 0;
  REQUIRE(nvmlDeviceGetNumGpuCores(handle, &cores) == NVML_SUCCESS);
  report.multiprocessors = static_cast<int32_t>(cores);
  char uuidStr[64]{};
  REQUIRE(nvmlDeviceGetUUID(handle, uuidStr, sizeof(uuidStr)) == NVML_SUCCESS);
  const char *hex = std::strncmp(uuidStr, "GPU-", 4) == 0 ? uuidStr + 4 : uuidStr;
  const auto hexByte = [](char c) -> uint8_t {
    if (c >= '0' && c <= '9') return static_cast<uint8_t>(c - '0');
    if (c >= 'a' && c <= 'f') return static_cast<uint8_t>(c - 'a' + 10);
    if (c >= 'A' && c <= 'F') return static_cast<uint8_t>(c - 'A' + 10);
    return 0;
  };
  size_t uidx = 0;
  for (size_t i = 0; hex[i] && hex[i + 1] && uidx < report.uuid.size();) {
    if (hex[i] == '-') {
      ++i;
      continue;
    }
    report.uuid[uidx++] = static_cast<uint8_t>((hexByte(hex[i]) << 4) | hexByte(hex[i + 1]));
    i += 2;
  }
  nvmlPciInfo_t pci{};
  REQUIRE(nvmlDeviceGetPciInfo_v3(handle, &pci) == NVML_SUCCESS);
  report.pci = {.domain = pci.domain, .bus = pci.bus, .device = pci.device, .function = 0};
  report.pciBusId = pci.busIdLegacy;
  return report;
}

uint64_t uuidPrefix(const std::array<uint8_t, 16> &uuid) {
  uint64_t prefix = 0;
  for (size_t index = 0; index < 8; ++index)
    prefix = (prefix << 8U) | uuid[index];
  return prefix;
}

RuntimeReport queryRocmSmi(uint32_t ordinal) {
  RuntimeReport report{};
  char name[256]{};
  REQUIRE(rsmi_dev_name_get(ordinal, name, sizeof(name)) == RSMI_STATUS_SUCCESS);
  report.name = name;
  uint64_t totalMemory = 0;
  REQUIRE(rsmi_dev_memory_total_get(ordinal, RSMI_MEM_TYPE_VRAM, &totalMemory) == RSMI_STATUS_SUCCESS);
  report.memoryBytes = totalMemory;
  rsmi_frequencies_t sclk{};
  REQUIRE(rsmi_dev_gpu_clk_freq_get(ordinal, RSMI_CLK_TYPE_SYS, &sclk) == RSMI_STATUS_SUCCESS);
  report.boostMhz = static_cast<int32_t>(sclk.frequency[0] / 1000000ULL);
  rsmi_frequencies_t mclk{};
  REQUIRE(rsmi_dev_gpu_clk_freq_get(ordinal, RSMI_CLK_TYPE_MEM, &mclk) == RSMI_STATUS_SUCCESS);
  report.memoryClockMhz = static_cast<int32_t>(mclk.frequency[0] / 1000000ULL);
  uint64_t bdfid = 0;
  REQUIRE(rsmi_dev_pci_id_get(ordinal, &bdfid) == RSMI_STATUS_SUCCESS);
  report.pci = {.domain = static_cast<uint32_t>(bdfid >> 32),
                .bus = static_cast<uint32_t>((bdfid >> 8U) & 0xffU),
                .device = static_cast<uint32_t>((bdfid >> 3U) & 0x1fU),
                .function = static_cast<uint32_t>(bdfid & 0x7U)};
  report.pciBusId = gemstone::devicePciBusId(ordinal);
  REQUIRE(rsmi_dev_unique_id_get(ordinal, &report.uniqueId) == RSMI_STATUS_SUCCESS);
  return report;
}

RuntimeReport queryAmdSmi(uint32_t ordinal) {
  RuntimeReport report{};
  auto *const handle = reinterpret_cast<amdsmi_processor_handle>(static_cast<uintptr_t>(ordinal + 1));
  amdsmi_asic_info_t asic{};
  REQUIRE(amdsmi_get_gpu_asic_info(handle, &asic) == AMDSMI_STATUS_SUCCESS);
  report.name = asic.market_name;
  report.multiprocessors = static_cast<int32_t>(asic.num_of_compute_units);
  amdsmi_vram_info_t vram{};
  REQUIRE(amdsmi_get_gpu_vram_info(handle, &vram) == AMDSMI_STATUS_SUCCESS);
  report.memoryBytes = vram.vram_size * (1024ULL * 1024ULL);
  report.busWidthBits = static_cast<int32_t>(vram.vram_bit_width);
  amdsmi_clk_info_t sysClk{};
  REQUIRE(amdsmi_get_clock_info(handle, AMDSMI_CLK_TYPE_SYS, &sysClk) == AMDSMI_STATUS_SUCCESS);
  report.boostMhz = static_cast<int32_t>(sysClk.clk);
  amdsmi_clk_info_t memClk{};
  REQUIRE(amdsmi_get_clock_info(handle, AMDSMI_CLK_TYPE_MEM, &memClk) == AMDSMI_STATUS_SUCCESS);
  report.memoryClockMhz = static_cast<int32_t>(memClk.clk);
  amdsmi_bdf_t bdf{};
  REQUIRE(amdsmi_get_gpu_device_bdf(handle, &bdf) == AMDSMI_STATUS_SUCCESS);
  report.pci = {.domain = static_cast<uint32_t>(bdf.bdf.domain_number),
                .bus = static_cast<uint32_t>(bdf.bdf.bus_number),
                .device = static_cast<uint32_t>(bdf.bdf.device_number),
                .function = static_cast<uint32_t>(bdf.bdf.function_number)};
  report.pciBusId = gemstone::devicePciBusId(ordinal);
  char uuidBuf[64]{};
  unsigned int uuidLen = sizeof(uuidBuf);
  REQUIRE(amdsmi_get_gpu_device_uuid(handle, &uuidLen, uuidBuf) == AMDSMI_STATUS_SUCCESS);
  const auto hexByte = [](char c) -> uint8_t {
    if (c >= '0' && c <= '9') return static_cast<uint8_t>(c - '0');
    if (c >= 'a' && c <= 'f') return static_cast<uint8_t>(c - 'a' + 10);
    if (c >= 'A' && c <= 'F') return static_cast<uint8_t>(c - 'A' + 10);
    return 0;
  };
  size_t uidx = 0;
  for (size_t i = 0; uuidBuf[i] && uuidBuf[i + 1] && uidx < report.uuid.size();) {
    if (uuidBuf[i] == '-') {
      ++i;
      continue;
    }
    report.uuid[uidx++] = static_cast<uint8_t>((hexByte(uuidBuf[i]) << 4) | hexByte(uuidBuf[i + 1]));
    i += 2;
  }
  return report;
}

} // namespace

TEST_CASE("Every runtime reports the same identity and capacity for the same preset and device index", "[preset][identity]") {
  const gemstone_test::Environment presetEnv(gemstone::environment::Device, "GeForce RTX 5080");
  REQUIRE(cuInit(0) == CUDA_SUCCESS);
  REQUIRE(nvmlInit_v2() == NVML_SUCCESS);

  cl_platform_id clPlatform = nullptr;
  REQUIRE(clGetPlatformIDs(1, &clPlatform, nullptr) == CL_SUCCESS);
  std::array<cl_device_id, 2> clDevices{};
  REQUIRE(clGetDeviceIDs(clPlatform, CL_DEVICE_TYPE_GPU, 2, clDevices.data(), nullptr) == CL_SUCCESS);

  REQUIRE(zeInit(0) == ZE_RESULT_SUCCESS);
  uint32_t zeDriverCount = 1;
  ze_driver_handle_t zeDriver = nullptr;
  REQUIRE(zeDriverGet(&zeDriverCount, &zeDriver) == ZE_RESULT_SUCCESS);
  uint32_t zeDeviceCount = 2;
  std::array<ze_device_handle_t, 2> zeDevices{};
  REQUIRE(zeDeviceGet(zeDriver, &zeDeviceCount, zeDevices.data()) == ZE_RESULT_SUCCESS);

  const char *vkExtensions[] = {VK_KHR_GET_PHYSICAL_DEVICE_PROPERTIES_2_EXTENSION_NAME};
  VkInstanceCreateInfo vkCreate{};
  vkCreate.sType = VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO;
  vkCreate.enabledExtensionCount = 1;
  vkCreate.ppEnabledExtensionNames = vkExtensions;
  VkInstance vkInstance = VK_NULL_HANDLE;
  REQUIRE(vkCreateInstance(&vkCreate, nullptr, &vkInstance) == VK_SUCCESS);
  uint32_t vkDeviceCount = 2;
  std::array<VkPhysicalDevice, 2> vkDevices{};
  REQUIRE(vkEnumeratePhysicalDevices(vkInstance, &vkDeviceCount, vkDevices.data()) == VK_SUCCESS);

  const gemstone_test::Environment amdPresetEnv(gemstone::environment::Device, "Radeon RX 9070 XT");
  REQUIRE(hsaKmtOpenKFD() == HSAKMT_STATUS_SUCCESS);
  REQUIRE(hsa_init() == HSA_STATUS_SUCCESS);
  REQUIRE(rsmi_init(0) == RSMI_STATUS_SUCCESS);
  REQUIRE(amdsmi_init(0) == AMDSMI_STATUS_SUCCESS);
  std::vector<hsa_agent_t> hsaGpus;
  REQUIRE(hsa_iterate_agents(
              [](hsa_agent_t agent, void *data) {
                hsa_device_type_t type{};
                if (hsa_agent_get_info(agent, HSA_AGENT_INFO_DEVICE, &type) == HSA_STATUS_SUCCESS && type == HSA_DEVICE_TYPE_GPU)
                  static_cast<std::vector<hsa_agent_t> *>(data)->push_back(agent);
                return HSA_STATUS_SUCCESS;
              },
              &hsaGpus)
          == HSA_STATUS_SUCCESS);
  REQUIRE(hsaGpus.size() == 2);

  const auto preset = gemstone::devicePreset("Radeon RX 9070 XT");
  REQUIRE(preset.has_value());
  if (!preset.has_value()) return;
  const gemstone::DevicePreset expectedPreset = *preset;

  for (uint32_t ordinal = 0; ordinal < 2; ++ordinal) {
    INFO("ordinal " << ordinal);
    const auto cuda = queryCuda(static_cast<int>(ordinal));
    const auto hip = queryHip(static_cast<int>(ordinal));
    const auto opencl = queryOpenCl(clDevices[ordinal]);
    const auto levelZero = queryLevelZero(zeDevices[ordinal]);
    const auto vulkan = queryVulkan(vkDevices[ordinal]);

    char hsaProductName[64]{};
    char hsaUuid[64]{};
    uint32_t hsaComputeUnits = 0;
    uint32_t hsaClockMhz = 0;
    uint32_t hsaMemoryClockMhz = 0;
    uint32_t hsaBusWidth = 0;
    std::array<uint32_t, 4> hsaCacheSizes{};
    uint32_t hsaDomain = UINT32_MAX;
    uint32_t hsaBdfid = UINT32_MAX;
    uint64_t hsaMemoryAvail = 0;
    REQUIRE(hsa_agent_get_info(hsaGpus[ordinal], static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_PRODUCT_NAME), hsaProductName)
            == HSA_STATUS_SUCCESS);
    REQUIRE(hsa_agent_get_info(hsaGpus[ordinal], static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_UUID), hsaUuid) == HSA_STATUS_SUCCESS);
    REQUIRE(hsa_agent_get_info(hsaGpus[ordinal], static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_COMPUTE_UNIT_COUNT), &hsaComputeUnits)
            == HSA_STATUS_SUCCESS);
    REQUIRE(hsa_agent_get_info(hsaGpus[ordinal], static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_MAX_CLOCK_FREQUENCY), &hsaClockMhz)
            == HSA_STATUS_SUCCESS);
    REQUIRE(hsa_agent_get_info(hsaGpus[ordinal], static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_MEMORY_MAX_FREQUENCY), &hsaMemoryClockMhz)
            == HSA_STATUS_SUCCESS);
    REQUIRE(hsa_agent_get_info(hsaGpus[ordinal], static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_MEMORY_WIDTH), &hsaBusWidth)
            == HSA_STATUS_SUCCESS);
    REQUIRE(hsa_agent_get_info(hsaGpus[ordinal], HSA_AGENT_INFO_CACHE_SIZE, hsaCacheSizes.data()) == HSA_STATUS_SUCCESS);
    REQUIRE(hsa_agent_get_info(hsaGpus[ordinal], static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_DOMAIN), &hsaDomain)
            == HSA_STATUS_SUCCESS);
    REQUIRE(hsa_agent_get_info(hsaGpus[ordinal], static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_BDFID), &hsaBdfid) == HSA_STATUS_SUCCESS);
    REQUIRE(hsa_agent_get_info(hsaGpus[ordinal], static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_MEMORY_AVAIL), &hsaMemoryAvail)
            == HSA_STATUS_SUCCESS);

    const auto expectedUuid = gemstone::deviceUuid(ordinal);
    const auto expectedPci = gemstone::devicePciAddress(ordinal);

    CHECK(cuda.name == expectedPreset.name);
    CHECK(hip.name == expectedPreset.name);
    CHECK(std::string_view(hsaProductName) == expectedPreset.name);
    CHECK(opencl.name == expectedPreset.name);
    CHECK(levelZero.name == expectedPreset.name);
    CHECK(vulkan.name == expectedPreset.name);

    CHECK(std::cmp_equal(cuda.memoryBytes, expectedPreset.memoryBytes));
    CHECK(hip.memoryBytes == cuda.memoryBytes);
    CHECK(hsaMemoryAvail == cuda.memoryBytes);
    CHECK(opencl.memoryBytes == cuda.memoryBytes);
    CHECK(levelZero.memoryBytes == cuda.memoryBytes);
    CHECK(vulkan.memoryBytes == cuda.memoryBytes);

    CHECK(cuda.multiprocessors == expectedPreset.multiprocessors);
    CHECK(hip.multiprocessors == cuda.multiprocessors);
    CHECK(std::cmp_equal(hsaComputeUnits, cuda.multiprocessors));
    CHECK(opencl.multiprocessors == cuda.multiprocessors);

    CHECK(cuda.boostMhz == expectedPreset.boostMhz);
    CHECK(hip.boostMhz == cuda.boostMhz);
    CHECK(std::cmp_equal(hsaClockMhz, cuda.boostMhz));
    CHECK(opencl.boostMhz == cuda.boostMhz);

    CHECK(cuda.memoryClockMhz == expectedPreset.memoryMTps / 2);
    CHECK(hip.memoryClockMhz == cuda.memoryClockMhz);
    CHECK(std::cmp_equal(hsaMemoryClockMhz, cuda.memoryClockMhz));

    CHECK(cuda.busWidthBits == expectedPreset.busWidthBits);
    CHECK(hip.busWidthBits == cuda.busWidthBits);
    CHECK(std::cmp_equal(hsaBusWidth, cuda.busWidthBits));

    CHECK(cuda.l2Bytes == expectedPreset.l2Bytes);
    CHECK(hip.l2Bytes == cuda.l2Bytes);
    CHECK(std::cmp_equal(hsaCacheSizes[1], cuda.l2Bytes));
    CHECK(opencl.l2Bytes == cuda.l2Bytes);

    CHECK(cuda.uuid == expectedUuid);
    CHECK(hip.uuid == cuda.uuid);
    CHECK(opencl.uuid == cuda.uuid);
    CHECK(levelZero.uuid == cuda.uuid);
    CHECK(vulkan.uuid == cuda.uuid);
    CHECK(std::string(hsaUuid) == gemstone::deviceHsaUuid(ordinal));

    CHECK(cuda.pci.domain == expectedPci.domain);
    CHECK(cuda.pci.bus == expectedPci.bus);
    CHECK(cuda.pci.device == expectedPci.device);
    CHECK(cuda.pci.function == expectedPci.function);
    CHECK(hip.pci.domain == cuda.pci.domain);
    CHECK(hip.pci.bus == cuda.pci.bus);
    CHECK(hip.pci.device == cuda.pci.device);
    CHECK(opencl.pci.domain == cuda.pci.domain);
    CHECK(opencl.pci.bus == cuda.pci.bus);
    CHECK(opencl.pci.device == cuda.pci.device);
    CHECK(opencl.pci.function == cuda.pci.function);
    CHECK(levelZero.pci.domain == cuda.pci.domain);
    CHECK(levelZero.pci.bus == cuda.pci.bus);
    CHECK(levelZero.pci.device == cuda.pci.device);
    CHECK(levelZero.pci.function == cuda.pci.function);
    CHECK(vulkan.pci.domain == cuda.pci.domain);
    CHECK(vulkan.pci.bus == cuda.pci.bus);
    CHECK(vulkan.pci.device == cuda.pci.device);
    CHECK(vulkan.pci.function == cuda.pci.function);
    CHECK(hsaDomain == cuda.pci.domain);
    CHECK(hsaBdfid == cuda.pci.bdfid());
    HsaNodeProperties kmtProps{};
    REQUIRE(hsaKmtGetNodeProperties(1 + ordinal, &kmtProps) == HSAKMT_STATUS_SUCCESS);
    CHECK(kmtProps.LocationId == cuda.pci.bdfid());
    CHECK(cuda.pciBusId == gemstone::devicePciBusId(ordinal));
    CHECK(hip.pciBusId == cuda.pciBusId);

    const auto nvml = queryNvml(ordinal);
    CHECK(nvml.name == expectedPreset.name);
    CHECK(nvml.memoryBytes == cuda.memoryBytes);
    CHECK(nvml.multiprocessors == cuda.multiprocessors);
    CHECK(nvml.boostMhz == cuda.boostMhz);
    CHECK(nvml.memoryClockMhz == cuda.memoryClockMhz);
    CHECK(nvml.uuid == cuda.uuid);
    CHECK(nvml.pci.domain == cuda.pci.domain);
    CHECK(nvml.pci.bus == cuda.pci.bus);
    CHECK(nvml.pci.device == cuda.pci.device);
    CHECK(nvml.pciBusId == cuda.pciBusId);

    const auto rocmSmi = queryRocmSmi(ordinal);
    CHECK(rocmSmi.name == expectedPreset.name);
    CHECK(rocmSmi.memoryBytes == cuda.memoryBytes);
    CHECK(rocmSmi.boostMhz == cuda.boostMhz);
    CHECK(rocmSmi.memoryClockMhz == cuda.memoryClockMhz);
    CHECK(rocmSmi.uniqueId == uuidPrefix(cuda.uuid));
    CHECK(rocmSmi.pci.domain == cuda.pci.domain);
    CHECK(rocmSmi.pci.bus == cuda.pci.bus);
    CHECK(rocmSmi.pci.device == cuda.pci.device);
    CHECK(rocmSmi.pciBusId == cuda.pciBusId);

    const auto amdSmi = queryAmdSmi(ordinal);
    CHECK(amdSmi.name == expectedPreset.name);
    CHECK(amdSmi.memoryBytes == cuda.memoryBytes);
    CHECK(amdSmi.multiprocessors == cuda.multiprocessors);
    CHECK(amdSmi.boostMhz == cuda.boostMhz);
    CHECK(amdSmi.memoryClockMhz == cuda.memoryClockMhz);
    CHECK(amdSmi.busWidthBits == cuda.busWidthBits);
    CHECK(amdSmi.uuid == cuda.uuid);
    CHECK(amdSmi.pci.domain == cuda.pci.domain);
    CHECK(amdSmi.pci.bus == cuda.pci.bus);
    CHECK(amdSmi.pci.device == cuda.pci.device);
    CHECK(amdSmi.pciBusId == cuda.pciBusId);
  }

  REQUIRE(amdsmi_shut_down() == AMDSMI_STATUS_SUCCESS);
  REQUIRE(rsmi_shut_down() == RSMI_STATUS_SUCCESS);
  REQUIRE(nvmlShutdown() == NVML_SUCCESS);
  REQUIRE(hsa_shut_down() == HSA_STATUS_SUCCESS);
  REQUIRE(hsaKmtCloseKFD() == HSAKMT_STATUS_SUCCESS);
  vkDestroyInstance(vkInstance, nullptr);
}

TEST_CASE("AMD device control is not supported", "[preset][control]") {
  const gemstone_test::Environment presetEnv(gemstone::environment::Device, "Radeon RX 9070 XT");
  REQUIRE(rsmi_init(0) == RSMI_STATUS_SUCCESS);
  REQUIRE(amdsmi_init(0) == AMDSMI_STATUS_SUCCESS);
  auto *const handle = reinterpret_cast<amdsmi_processor_handle>(uintptr_t{1});
  CHECK(rsmi_dev_fan_reset(0, 0) == RSMI_STATUS_NOT_SUPPORTED);
  CHECK(rsmi_dev_power_cap_set(0, 0, 0) == RSMI_STATUS_NOT_SUPPORTED);
  CHECK(amdsmi_reset_gpu(handle) == AMDSMI_STATUS_NOT_SUPPORTED);
  CHECK(amdsmi_set_power_cap(handle, 0, 0) == AMDSMI_STATUS_NOT_SUPPORTED);
  REQUIRE(amdsmi_shut_down() == AMDSMI_STATUS_SUCCESS);
  REQUIRE(rsmi_shut_down() == RSMI_STATUS_SUCCESS);
}
