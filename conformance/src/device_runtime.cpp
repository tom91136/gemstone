#include <cstdint>
#include <cstdlib>
#include <stdexcept>
#include <string>
#include <vector>

#include "cuda_architecture.hpp"
#include <aspartame/all.hpp>

#include "conformance/device.hpp"
#include "conformance/environment_keys.hpp"

#include "device_selector.hpp"
#include <fmt/format.h>
#include <magic_enum/magic_enum.hpp>

#ifdef CONFORMANCE_DEVICE_CUDA
  #include <cstddef>
  #include <string_view>
  #ifdef CONFORMANCE_DEVICE_CUDA_DRIVER
    #include <charconv>
    #include <optional>
    #include <system_error>

    #include <cuda.h>
  #else
    #include <cuda_runtime_api.h>
  #endif
#elif defined(CONFORMANCE_DEVICE_HIP) || defined(CONFORMANCE_DEVICE_HSA)
  #include <array>
  #include <string_view>
  #ifdef CONFORMANCE_DEVICE_HIP
    #include <cstddef>

    #include <hip/hip_runtime_api.h>
    #include <hip/hip_version.h>
  #endif
  #include <hsa/hsa.h>
  #include <hsa/hsa_ext_amd.h>
#elif defined(CONFORMANCE_DEVICE_OPENCL)
  #include <array>
  #include <cstddef>
  #include <map>
  #include <utility>

  #ifndef CL_TARGET_OPENCL_VERSION
    #define CL_TARGET_OPENCL_VERSION 120
  #endif
  #include <CL/cl.h>
  #include <CL/cl_ext.h>
#elif defined(CONFORMANCE_DEVICE_SYCL)
  #include <charconv>
  #include <cstddef>
  #include <map>
  #include <string_view>
  #include <system_error>

  #include <sycl/sycl.hpp>
#elif defined(CONFORMANCE_DEVICE_VULKAN)
  #include <algorithm>
  #include <array>

  #include <vulkan/vulkan.h>
#elif defined(CONFORMANCE_DEVICE_LEVELZERO)
  #include <level_zero/ze_api.h>
#endif

namespace conformance {
using namespace aspartame;
namespace {

#if defined(CONFORMANCE_DEVICE_CUDA) || defined(CONFORMANCE_DEVICE_HIP) || defined(CONFORMANCE_DEVICE_HSA)
bool targetsArchitecture(std::string_view architecture) {
  #ifdef CONFORMANCE_DEVICE_HIP
  architecture = architecture.substr(0, architecture.find(':'));
  #elif defined(CONFORMANCE_DEVICE_HSA)
  if (const auto gfx = architecture.find("gfx"); gfx != std::string_view::npos) architecture.remove_prefix(gfx);
  architecture = architecture.substr(0, architecture.find(':'));
  #endif
  std::string_view targets(CONFORMANCE_DEVICE_TARGETS);
  while (!targets.empty()) {
    const auto separator = targets.find(',');
    auto target = targets.substr(0, separator);
    target = target.substr(0, target.find(':'));
    if (target == architecture) return true;
  #ifdef CONFORMANCE_DEVICE_CUDA
    const auto device = CudaArchitecture::parse(architecture);
    const auto code = CudaArchitecture::parse(target);
    if (device && code && code->runsOn(device->capability)) return true;
    #ifdef CONFORMANCE_DEVICE_CUDA_PTX_ONLY
    // PTX targets a minimum compute capability; the driver JITs it for newer GPUs.
    if (device && code && device->capability >= code->capability) return true;
    #endif
  #endif
    if (separator == std::string_view::npos) break;
    targets.remove_prefix(separator + 1);
  }
  return false;
}
#endif

#ifdef CONFORMANCE_DEVICE_HIP
void validateHipRuntimeVersion() {
  static_assert(HIP_VERSION_MAJOR == CONFORMANCE_ROCM_MAJOR && HIP_VERSION_MINOR == CONFORMANCE_ROCM_MINOR,
                "ROCm headers do not match configured build release");
  int runtimeVersion = 0;
  const auto status = hipRuntimeGetVersion(&runtimeVersion);
  if (status != hipSuccess)
    throw std::runtime_error(fmt::format("cannot query the loaded HIP runtime version: {}", hipGetErrorString(status)));
  constexpr int versionComponentScale = 100000;
  constexpr int majorComponentScale = 100;
  const auto runtimeMajor = runtimeVersion / (versionComponentScale * majorComponentScale);
  const auto runtimeMinor = runtimeVersion / versionComponentScale % majorComponentScale;
  if (runtimeMajor != HIP_VERSION_MAJOR || runtimeMinor != HIP_VERSION_MINOR)
    throw std::runtime_error(fmt::format("ROCm/HIP runtime version mismatch: artifact was built against {}, but loaded runtime is {}.{}",
                                         CONFORMANCE_ROCM_VERSION, runtimeMajor, runtimeMinor));
}
#endif

#if defined(CONFORMANCE_DEVICE_HIP) || defined(CONFORMANCE_DEVICE_HSA)
struct HsaRuntime {
  HsaRuntime() {
    if (hsa_init() != HSA_STATUS_SUCCESS) throw std::runtime_error("cannot initialise HSA device discovery");
  }
};

struct HsaDiscovery {
  std::vector<hsa_agent_t> agents;
  std::vector<std::string> descriptions;
  std::vector<PhysicalDevice> devices;
  std::vector<std::string> selectors;
};

struct HsaIsaNames {
  std::string joined;
  bool compatible = false;
};

hsa_status_t collectHsaIsa(hsa_isa_t isa, void *data) {
  std::array<char, 128> name{};
  if (hsa_isa_get_info_alt(isa, HSA_ISA_INFO_NAME, name.data()) != HSA_STATUS_SUCCESS) return HSA_STATUS_SUCCESS;
  auto &names = *static_cast<HsaIsaNames *>(data);
  if (!names.joined.empty()) names.joined += ',';
  names.joined += name.data();
  names.compatible |= targetsArchitecture(name.data());
  return HSA_STATUS_SUCCESS;
}

hsa_status_t collectHsaAgent(hsa_agent_t agent, void *data) {
  hsa_device_type_t type{};
  if (hsa_agent_get_info(agent, HSA_AGENT_INFO_DEVICE, &type) != HSA_STATUS_SUCCESS || type != HSA_DEVICE_TYPE_GPU)
    return HSA_STATUS_SUCCESS;
  HsaIsaNames isas;
  if (hsa_agent_iterate_isas(agent, collectHsaIsa, &isas) != HSA_STATUS_SUCCESS) return HSA_STATUS_SUCCESS;
  std::array<char, 64> name{};
  uint32_t bdf = 0;
  uint32_t domain = 0;
  hsa_agent_get_info(agent, HSA_AGENT_INFO_NAME, name.data());
  if (hsa_agent_get_info(agent, static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_BDFID), &bdf) != HSA_STATUS_SUCCESS
      || hsa_agent_get_info(agent, static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_DOMAIN), &domain) != HSA_STATUS_SUCCESS)
    return HSA_STATUS_ERROR;
  std::array<char, 64> uuid{};
  hsa_agent_get_info(agent, static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_UUID), uuid.data());
  auto &discovery = *static_cast<HsaDiscovery *>(data);
  const std::string identity(uuid.data());
  discovery.selectors.push_back(identity.starts_with("GPU-") && identity.size() == 20 ? identity : fmt::to_string(discovery.agents.size()));
  discovery.devices.push_back(makePciDevice(domain, static_cast<uint8_t>((bdf >> 8) & 0xff), static_cast<uint8_t>((bdf >> 3) & 0x1f),
                                            static_cast<uint8_t>(bdf & 7)));
  discovery.agents.push_back(agent);
  discovery.descriptions.push_back(
      fmt::format("hsa:{}:{}:{}", name.data(), isas.joined, pciDescription(domain, (bdf >> 8) & 0xff, (bdf >> 3) & 0x1f, bdf & 0x7)));
  return HSA_STATUS_SUCCESS;
}
#endif

#if defined(CONFORMANCE_DEVICE_CUDA_DRIVER) || defined(CONFORMANCE_DEVICE_SYCL)
struct PciAddress {
  uint32_t domain = 0;
  uint32_t bus = 0;
  uint32_t slot = 0;
  uint32_t function = 0;
};

bool parsePciAddress(std::string_view text, PciAddress &address) {
  const auto field = [&text](uint32_t &value, char separator) {
    const auto result = std::from_chars(text.data(), text.data() + text.size(), value, 16);
    if (result.ec != std::errc{}) return false;
    text.remove_prefix(static_cast<size_t>(result.ptr - text.data()));
    if (separator == '\0') return true;
    if (text.empty() || text.front() != separator) return false;
    text.remove_prefix(1);
    return true;
  };
  return field(address.domain, ':') && field(address.bus, ':') && field(address.slot, '.') && field(address.function, '\0');
}
#endif

} // namespace

static DeviceSelection discoverPhysicalDevice() {
#ifdef CONFORMANCE_DEVICE_CUDA_DRIVER
  if (cuInit(0) != CUDA_SUCCESS) throw std::runtime_error("cannot initialise CUDA");
  int count = 0;
  if (cuDeviceGetCount(&count) != CUDA_SUCCESS || count <= 0) throw std::runtime_error("cannot enumerate CUDA devices");
  struct Device {
    CUdevice handle;
    int major;
    int minor;
    PciAddress address;
  };
  std::vector<Device> devices;
  std::vector<std::string> descriptions;
  const auto deviceCount = static_cast<size_t>(count);
  for (size_t index = 0; index < deviceCount; ++index) {
    const auto deviceIndex = static_cast<int>(index);
    Device candidate{};
    char name[256]{};
    char pci[32]{};
    if (cuDeviceGet(&candidate.handle, deviceIndex) != CUDA_SUCCESS || cuDeviceGetName(name, sizeof(name), candidate.handle) != CUDA_SUCCESS
        || cuDeviceGetAttribute(&candidate.major, CU_DEVICE_ATTRIBUTE_COMPUTE_CAPABILITY_MAJOR, candidate.handle) != CUDA_SUCCESS
        || cuDeviceGetAttribute(&candidate.minor, CU_DEVICE_ATTRIBUTE_COMPUTE_CAPABILITY_MINOR, candidate.handle) != CUDA_SUCCESS
        || cuDeviceGetPCIBusId(pci, sizeof(pci), candidate.handle) != CUDA_SUCCESS || !parsePciAddress(pci, candidate.address))
      throw std::runtime_error(fmt::format("cannot identify CUDA device {}", index));
    descriptions.push_back(
        fmt::format("cuda:{}:sm_{}:{}", name, (candidate.major * 10) + candidate.minor,
                    pciDescription(candidate.address.domain, candidate.address.bus, candidate.address.slot, candidate.address.function)));
    devices.push_back(candidate);
  }
  const auto index = selectDevice(descriptions);
  const auto &selected = devices[index];
  const auto architecture = fmt::to_string((selected.major * 10) + selected.minor);
  if (!targetsArchitecture(architecture))
    throw std::runtime_error(fmt::format("selected CUDA device {} is incompatible with targeted architecture(s) {}", descriptions[index],
                                         CONFORMANCE_DEVICE_TARGETS));
  CUcontext context = nullptr;
  if (cuDevicePrimaryCtxRetain(&context, selected.handle) != CUDA_SUCCESS || cuCtxSetCurrent(context) != CUDA_SUCCESS)
    throw std::runtime_error(fmt::format("cannot select {}", descriptions[index]));
  CUuuid uuid{};
  if (cuDeviceGetUuid(&uuid, selected.handle) != CUDA_SUCCESS) throw std::runtime_error("cannot query CUDA UUID");
  auto visible = std::string("GPU-");
  for (size_t byte = 0; byte < 16; ++byte) {
    if (byte == 4 || byte == 6 || byte == 8 || byte == 10) visible += '-';
    visible += fmt::format("{:02x}", static_cast<uint8_t>(uuid.bytes[byte]));
  }
  return {
      .device = makePciDevice(selected.address.domain, static_cast<uint8_t>(selected.address.bus),
                              static_cast<uint8_t>(selected.address.slot), static_cast<uint8_t>(selected.address.function)),
      .environment = {
          {"CUDA_VISIBLE_DEVICES", visible}, {"CUDA_DEVICE_ORDER", "PCI_BUS_ID"}, {"ACC_DEVICE_NUM", "0"}, {"OMP_DEFAULT_DEVICE", "0"}}};
#elif defined(CONFORMANCE_DEVICE_CUDA)
  int count = 0;
  if (cudaGetDeviceCount(&count) != cudaSuccess || count <= 0) throw std::runtime_error("cannot enumerate CUDA devices");
  std::vector<cudaDeviceProp> devices(static_cast<size_t>(count));
  std::vector<std::string> descriptions;
  for (size_t index = 0; index < devices.size(); ++index) {
    auto &candidate = devices[index];
    if (cudaGetDeviceProperties(&candidate, static_cast<int>(index)) != cudaSuccess)
      throw std::runtime_error(fmt::format("cannot identify CUDA device {}", index));
    descriptions.push_back(fmt::format("cuda:{}:sm_{}:{}", candidate.name, (candidate.major * 10) + candidate.minor,
                                       pciDescription(candidate.pciDomainID, candidate.pciBusID, candidate.pciDeviceID, 0)));
  }
  const auto index = selectDevice(descriptions);
  const auto architecture = fmt::to_string((devices[index].major * 10) + devices[index].minor);
  if (!targetsArchitecture(architecture))
    throw std::runtime_error(fmt::format("selected CUDA device {} is incompatible with targeted architecture(s) {}", descriptions[index],
                                         CONFORMANCE_DEVICE_TARGETS));
  if (cudaSetDevice(static_cast<int>(index)) != cudaSuccess) throw std::runtime_error(fmt::format("cannot select {}", descriptions[index]));
  const auto &properties = devices[index];
  return {.device = makePciDevice(static_cast<uint32_t>(properties.pciDomainID), static_cast<uint8_t>(properties.pciBusID),
                                  static_cast<uint8_t>(properties.pciDeviceID), 0),
          .environment = {{"CUDA_VISIBLE_DEVICES", fmt::to_string(index)},
                          {"CUDA_DEVICE_ORDER", "PCI_BUS_ID"},
                          {"ACC_DEVICE_NUM", "0"},
                          {"OMP_DEFAULT_DEVICE", "0"}}};
#elif defined(CONFORMANCE_DEVICE_HIP)
  validateHipRuntimeVersion();
  int count = 0;
  if (hipGetDeviceCount(&count) != hipSuccess || count <= 0) throw std::runtime_error("cannot enumerate HIP devices");
  std::vector<hipDeviceProp_t> devices(static_cast<size_t>(count));
  std::vector<std::string> descriptions;
  for (size_t index = 0; index < devices.size(); ++index) {
    auto &candidate = devices[index];
    if (hipGetDeviceProperties(&candidate, static_cast<int>(index)) != hipSuccess)
      throw std::runtime_error(fmt::format("cannot identify HIP device {}", index));
    descriptions.push_back(fmt::format("hip:{}:{}:{}", candidate.name, candidate.gcnArchName,
                                       pciDescription(candidate.pciDomainID, candidate.pciBusID, candidate.pciDeviceID, 0)));
  }
  const auto index = selectDevice(descriptions);
  if (!targetsArchitecture(devices[index].gcnArchName))
    throw std::runtime_error(fmt::format("selected HIP device {} is incompatible with targeted architecture(s) {}", descriptions[index],
                                         CONFORMANCE_DEVICE_TARGETS));
  if (hipSetDevice(static_cast<int>(index)) != hipSuccess) throw std::runtime_error(fmt::format("cannot select {}", descriptions[index]));
  const auto &properties = devices[index];
  const auto physical = makePciDevice(static_cast<uint32_t>(properties.pciDomainID), static_cast<uint8_t>(properties.pciBusID),
                                      static_cast<uint8_t>(properties.pciDeviceID), 0);
  static const HsaRuntime runtime;
  HsaDiscovery hsa;
  if (hsa_iterate_agents(collectHsaAgent, &hsa) != HSA_STATUS_SUCCESS) throw std::runtime_error("cannot map HIP device to ROCr");
  for (size_t i = 0; i < hsa.devices.size(); ++i)
    if (physicalDeviceKey(hsa.devices[i]) == physicalDeviceKey(physical))
      return {.device = physical,
              .environment = {{"ROCR_VISIBLE_DEVICES", hsa.selectors[i]}, {"HIP_VISIBLE_DEVICES", "0"}, {"OMP_DEFAULT_DEVICE", "0"}}};
  throw std::runtime_error("selected HIP device has no matching ROCr device for OpenMP/miniapp execution");
#elif defined(CONFORMANCE_DEVICE_HSA)
  static const HsaRuntime runtime;
  HsaDiscovery discovery;
  if (hsa_iterate_agents(collectHsaAgent, &discovery) != HSA_STATUS_SUCCESS) throw std::runtime_error("cannot enumerate HSA devices");
  const auto selected = selectDevice(discovery.descriptions);
  HsaIsaNames isas;
  if (hsa_agent_iterate_isas(discovery.agents[selected], collectHsaIsa, &isas) != HSA_STATUS_SUCCESS || !isas.compatible)
    throw std::runtime_error("selected HSA device is incompatible with artifact architecture(s): " CONFORMANCE_DEVICE_TARGETS);
  return {.device = discovery.devices[selected],
          .environment = {{"ROCR_VISIBLE_DEVICES", discovery.selectors[selected]}, {"HIP_VISIBLE_DEVICES", "0"}},
          .nativeHandle = discovery.agents[selected].handle};
#elif defined(CONFORMANCE_DEVICE_OPENCL)
  cl_uint platformCount = 0;
  if (clGetPlatformIDs(0, nullptr, &platformCount) != CL_SUCCESS || platformCount == 0)
    throw std::runtime_error("cannot enumerate OpenCL platforms");
  std::vector<cl_platform_id> platforms(platformCount);
  if (clGetPlatformIDs(platformCount, platforms.data(), nullptr) != CL_SUCCESS)
    throw std::runtime_error("cannot enumerate OpenCL platforms");
  struct Candidate {
    cl_device_id device;
    cl_uint platformIndex;
    cl_uint deviceIndex;
    size_t flatDeviceIndex;
    std::string typeName;
  };
  std::vector<Candidate> candidates;
  std::vector<std::string> descriptions;
  const auto info = [](auto object, cl_uint field, auto query) {
    size_t size = 0;
    if (query(object, field, 0, nullptr, &size) != CL_SUCCESS || size == 0) return std::string{};
    std::string value(size, '\0');
    if (query(object, field, size, value.data(), nullptr) != CL_SUCCESS) return std::string{};
    while (!value.empty() && value.back() == '\0')
      value.pop_back();
    return value;
  };
  size_t flatDeviceOffset = 0;
  for (cl_uint platformIndex = 0; platformIndex < platformCount; ++platformIndex) {
    cl_uint allDeviceCount = 0;
    std::vector<cl_device_id> allDevices;
    if (clGetDeviceIDs(platforms[platformIndex], CL_DEVICE_TYPE_ALL, 0, nullptr, &allDeviceCount) == CL_SUCCESS && allDeviceCount != 0) {
      allDevices.resize(allDeviceCount);
      if (clGetDeviceIDs(platforms[platformIndex], CL_DEVICE_TYPE_ALL, allDeviceCount, allDevices.data(), nullptr) != CL_SUCCESS)
        allDevices.clear();
    }
    for (cl_uint deviceIndex = 0; deviceIndex < allDevices.size(); ++deviceIndex) {
      cl_device_type type = 0;
      if (clGetDeviceInfo(allDevices[deviceIndex], CL_DEVICE_TYPE, sizeof(type), &type, nullptr) != CL_SUCCESS)
        throw std::runtime_error("cannot identify OpenCL device type");
      cl_device_type selectionType = CL_DEVICE_TYPE_DEFAULT;
      std::string typeName = "default";
      if ((type & CL_DEVICE_TYPE_GPU) != 0) {
        selectionType = CL_DEVICE_TYPE_GPU;
        typeName = "gpu";
      } else if ((type & CL_DEVICE_TYPE_CPU) != 0) {
        selectionType = CL_DEVICE_TYPE_CPU;
        typeName = "cpu";
      } else if ((type & CL_DEVICE_TYPE_ACCELERATOR) != 0) {
        selectionType = CL_DEVICE_TYPE_ACCELERATOR;
        typeName = "accelerator";
  #ifdef CL_DEVICE_TYPE_CUSTOM
      } else if ((type & CL_DEVICE_TYPE_CUSTOM) != 0) {
        selectionType = CL_DEVICE_TYPE_CUSTOM;
        typeName = "custom";
  #endif
      }
      cl_uint typeIndex = 0;
      for (cl_uint prior = 0; prior < deviceIndex; ++prior) {
        cl_device_type priorType = 0;
        if (clGetDeviceInfo(allDevices[prior], CL_DEVICE_TYPE, sizeof(priorType), &priorType, nullptr) == CL_SUCCESS
            && (priorType & selectionType) != 0)
          ++typeIndex;
      }
      auto description = "opencl:" + info(platforms[platformIndex], CL_PLATFORM_VENDOR, clGetPlatformInfo) + ':'
                         + info(platforms[platformIndex], CL_PLATFORM_NAME, clGetPlatformInfo) + ':'
                         + info(allDevices[deviceIndex], CL_DEVICE_VENDOR, clGetDeviceInfo) + ':'
                         + info(allDevices[deviceIndex], CL_DEVICE_NAME, clGetDeviceInfo);
      descriptions.push_back(std::move(description));
      candidates.push_back({.device = allDevices[deviceIndex],
                            .platformIndex = platformIndex,
                            .deviceIndex = typeIndex,
                            .flatDeviceIndex = flatDeviceOffset + deviceIndex,
                            .typeName = std::move(typeName)});
    }
    flatDeviceOffset += allDevices.size();
  }
  const auto candidate = candidates[selectDevice(descriptions)];
  const std::map<std::string, std::string> execution{{"CL_PLATFORM_INDEX", fmt::to_string(candidate.platformIndex)},
                                                     {"CL_DEVICE_INDEX", fmt::to_string(candidate.deviceIndex)},
                                                     {"CL_DEVICE_TYPE", candidate.typeName},
                                                     {environment::OpenclDeviceIndex, fmt::to_string(candidate.flatDeviceIndex)}};
  const auto selection = [&](PhysicalDevice device) {
    return DeviceSelection{
        .device = device, .environment = execution, .nativeHandle = static_cast<uint64_t>(reinterpret_cast<uintptr_t>(candidate.device))};
  };
  struct PciBusInfo {
    cl_uint domain, bus, device, function;
  } pci{};
  if (clGetDeviceInfo(candidate.device, CL_DEVICE_PCI_BUS_INFO_KHR, sizeof(pci), &pci, nullptr) == CL_SUCCESS
      && (pci.domain != 0 || pci.bus != 0 || pci.device != 0 || pci.function != 0))
    return selection(
        makePciDevice(pci.domain, static_cast<uint8_t>(pci.bus), static_cast<uint8_t>(pci.device), static_cast<uint8_t>(pci.function)));
  // The pinned OpenCL headers do not ship cl_amd_device_topology.h.
  constexpr cl_uint topologyTypePcieAmd = 1;
  struct AmdTopology {
    cl_uint type;
    cl_char unused[17];
    cl_char bus, device, function;
  } topology{};
  static_assert(sizeof(AmdTopology) == 24);
  if (clGetDeviceInfo(candidate.device, CL_DEVICE_TOPOLOGY_AMD, sizeof(topology), &topology, nullptr) == CL_SUCCESS
      && topology.type == topologyTypePcieAmd)
    return selection(makePciDevice(0, static_cast<uint8_t>(topology.bus), static_cast<uint8_t>(topology.device),
                                   static_cast<uint8_t>(topology.function)));
  uint8_t uuid[16]{};
  if (clGetDeviceInfo(candidate.device, CL_DEVICE_UUID_KHR, sizeof(uuid), uuid, nullptr) == CL_SUCCESS
      && std::to_array(uuid) ^ exists([](uint8_t byte) { return byte != 0; }))
    return selection(makeUuidDevice(uuid));
  return selection(PhysicalDevice{.scheme = PhysicalDeviceScheme::Unknown, .value = {}});
#elif defined(CONFORMANCE_DEVICE_VULKAN)
  VkApplicationInfo application{};
  application.sType = VK_STRUCTURE_TYPE_APPLICATION_INFO;
  uint32_t instanceVersion = VK_API_VERSION_1_0;
  if (const auto enumerateVersion =
          reinterpret_cast<PFN_vkEnumerateInstanceVersion>(vkGetInstanceProcAddr(nullptr, "vkEnumerateInstanceVersion")))
    if (enumerateVersion(&instanceVersion) != VK_SUCCESS) throw std::runtime_error("cannot query Vulkan instance version");
  application.apiVersion = std::min(instanceVersion, VK_API_VERSION_1_1);
  VkInstanceCreateInfo info{};
  info.sType = VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO;
  info.pApplicationInfo = &application;
  VkInstance instance = VK_NULL_HANDLE;
  if (vkCreateInstance(&info, nullptr, &instance) != VK_SUCCESS) throw std::runtime_error("cannot initialise Vulkan");
  struct InstanceGuard {
    VkInstance value;
    ~InstanceGuard() { vkDestroyInstance(value, nullptr); }
  } guard{.value = instance};
  uint32_t count = 0;
  if (vkEnumeratePhysicalDevices(instance, &count, nullptr) != VK_SUCCESS) throw std::runtime_error("cannot count Vulkan devices");
  std::vector<VkPhysicalDevice> devices(count);
  if (vkEnumeratePhysicalDevices(instance, &count, devices.data()) != VK_SUCCESS) throw std::runtime_error("cannot list Vulkan devices");
  std::vector<PhysicalDevice> identities;
  std::vector<std::string> descriptions;
  for (const auto device : devices) {
    VkPhysicalDeviceProperties properties{};
    vkGetPhysicalDeviceProperties(device, &properties);
    VkPhysicalDeviceIDProperties identity{};
    identity.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ID_PROPERTIES;
    if (application.apiVersion >= VK_API_VERSION_1_1 && properties.apiVersion >= VK_API_VERSION_1_1) {
      VkPhysicalDeviceProperties2 extended{};
      extended.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PROPERTIES_2;
      extended.pNext = &identity;
      vkGetPhysicalDeviceProperties2(device, &extended);
    }
    const auto description = fmt::format("vulkan:{}:{}:{}", properties.vendorID, properties.deviceID, properties.deviceName);
    descriptions.push_back(description);
    identities.push_back(std::to_array(identity.deviceUUID) ^ exists([](uint8_t b) { return b != 0; })
                             ? makeUuidDevice(identity.deviceUUID)
                             : PhysicalDevice{.scheme = PhysicalDeviceScheme::Unknown, .value = {}});
  }
  const auto index = selectDevice(descriptions);
  return {.device = identities[index], .environment = {{environment::VulkanDeviceId, fmt::to_string(index + 1)}}};
#elif defined(CONFORMANCE_DEVICE_LEVELZERO)
  if (zeInit(0) != ZE_RESULT_SUCCESS) throw std::runtime_error("cannot initialise Level Zero");
  uint32_t driverCount = 0;
  if (zeDriverGet(&driverCount, nullptr) != ZE_RESULT_SUCCESS) throw std::runtime_error("cannot list Level Zero drivers");
  std::vector<ze_driver_handle_t> drivers(driverCount);
  if (zeDriverGet(&driverCount, drivers.data()) != ZE_RESULT_SUCCESS) throw std::runtime_error("cannot get Level Zero drivers");
  struct Candidate {
    PhysicalDevice physical;
    uint32_t driver, device;
    ze_device_handle_t handle;
  };
  std::vector<Candidate> candidates;
  std::vector<std::string> descriptions;
  for (uint32_t driverIndex = 0; driverIndex < driverCount; ++driverIndex) {
    uint32_t deviceCount = 0;
    if (zeDeviceGet(drivers[driverIndex], &deviceCount, nullptr) != ZE_RESULT_SUCCESS)
      throw std::runtime_error("cannot list Level Zero devices");
    std::vector<ze_device_handle_t> devices(deviceCount);
    if (zeDeviceGet(drivers[driverIndex], &deviceCount, devices.data()) != ZE_RESULT_SUCCESS)
      throw std::runtime_error("cannot get Level Zero devices");
    for (uint32_t deviceIndex = 0; deviceIndex < deviceCount; ++deviceIndex) {
      ze_device_properties_t properties{};
      properties.stype = ZE_STRUCTURE_TYPE_DEVICE_PROPERTIES;
      if (zeDeviceGetProperties(devices[deviceIndex], &properties) != ZE_RESULT_SUCCESS)
        throw std::runtime_error("cannot query Level Zero device");
      if (properties.type != ZE_DEVICE_TYPE_GPU) continue;
      ze_pci_ext_properties_t pci{};
      pci.stype = ZE_STRUCTURE_TYPE_PCI_EXT_PROPERTIES;
      PhysicalDevice physical = makeUuidDevice(properties.uuid.id);
      if (zeDevicePciGetPropertiesExt(devices[deviceIndex], &pci) == ZE_RESULT_SUCCESS)
        physical = makePciDevice(pci.address.domain, static_cast<uint8_t>(pci.address.bus), static_cast<uint8_t>(pci.address.device),
                                 static_cast<uint8_t>(pci.address.function));
      descriptions.push_back(fmt::format("levelzero:{}:{}:{}", driverIndex, deviceIndex, properties.name));
      candidates.push_back({.physical = physical, .driver = driverIndex, .device = deviceIndex, .handle = devices[deviceIndex]});
    }
  }
  const auto &candidate = candidates[selectDevice(descriptions)];
  return {.device = candidate.physical,
          .environment = {{"LZT_DEFAULT_DRIVER_IDX", fmt::to_string(candidate.driver)},
                          {"LZT_DEFAULT_DEVICE_IDX", fmt::to_string(candidate.device)}},
          .nativeHandle = static_cast<uint64_t>(reinterpret_cast<uintptr_t>(candidate.handle))};
#elif defined(CONFORMANCE_DEVICE_SYCL)
  const auto all = sycl::device::get_devices();
  std::map<sycl::backend, size_t> ordinals;
  std::vector<sycl::device> devices;
  std::vector<std::string> descriptions;
  std::vector<std::string> filters;
  for (const auto &candidate : all) {
    const auto backend = candidate.get_backend();
    const auto ordinal = ordinals[backend]++;
    if (!candidate.is_gpu()) continue;
    std::string backendName(magic_enum::enum_name(backend));
    constexpr std::string_view extOneapiPrefix = "ext_oneapi_";
    if (backendName.starts_with(extOneapiPrefix)) backendName.erase(0, extOneapiPrefix.size());
    if (backendName != "level_zero" && backendName != "opencl" && backendName != "cuda" && backendName != "hip")
      throw std::runtime_error("SYCL backend cannot honour a propagated device selector");
    std::string address = "no-pci";
    if (candidate.has(sycl::aspect::ext_intel_pci_address)) address = candidate.get_info<sycl::ext::intel::info::device::pci_address>();
    descriptions.push_back(fmt::format("sycl:{}:{}:{}:{}:{}", backendName, candidate.get_platform().get_info<sycl::info::platform::name>(),
                                       candidate.get_info<sycl::info::device::vendor>(), candidate.get_info<sycl::info::device::name>(),
                                       address));
    filters.push_back(fmt::format("{}:{}", backendName, ordinal));
    devices.push_back(candidate);
  }
  const auto selected = selectDevice(descriptions);
  const auto &device = devices[selected];
  if (!device.has(sycl::aspect::ext_intel_pci_address)) throw std::runtime_error("selected SYCL GPU exposes no physical PCI identity");
  const auto address = device.get_info<sycl::ext::intel::info::device::pci_address>();
  PciAddress pci;
  if (!parsePciAddress(address, pci)) throw std::runtime_error("invalid SYCL PCI identity: " + address);
  if (const auto *expected = std::getenv(environment::PhysicalDeviceName); expected && *expected && devices.size() != 1)
    throw std::runtime_error("propagated SYCL selector did not isolate exactly one GPU");
  return {.device =
              makePciDevice(pci.domain, static_cast<uint8_t>(pci.bus), static_cast<uint8_t>(pci.slot), static_cast<uint8_t>(pci.function)),
          .environment = {{"ONEAPI_DEVICE_SELECTOR", filters[selected]}}};
#else
  #error "A conformance device backend is required"
#endif
}

const DeviceSelection &deviceSelection() {
  static const auto selected = [] {
    auto result = discoverPhysicalDevice();
    if (const auto *expected = std::getenv(environment::PhysicalDeviceName);
        expected && *expected && physicalDeviceKey(result.device) != expected)
      throw std::runtime_error("execution device " + physicalDeviceKey(result.device) + " differs from locked/probed device " + expected);
    return result;
  }();
  return selected;
}
} // namespace conformance
