#include <array>
#include <cstdint>
#include <cstring>
#include <string>
#include <string_view>
#include <utility>
#include <vector>

#include <hsa.h>
#include <hsa_ext_amd.h>

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
#include <environment_keys.hpp>
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

} // namespace

TEST_CASE("Every runtime reports the same identity and capacity for the same preset and device index", "[preset][identity]") {
  const gemstone_test::Environment presetEnv(gemstone::environment::Device, "GeForce RTX 5080");
  REQUIRE(cuInit(0) == CUDA_SUCCESS);

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
  }

  REQUIRE(hsa_shut_down() == HSA_STATUS_SUCCESS);
  REQUIRE(hsaKmtCloseKFD() == HSAKMT_STATUS_SUCCESS);
  vkDestroyInstance(vkInstance, nullptr);
}
