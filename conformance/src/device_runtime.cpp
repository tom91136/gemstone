#include <algorithm>
#include <array>
#include <cinttypes>
#include <cstddef>
#include <cstdint>
#include <cstdio>
#include <stdexcept>
#include <string_view>
#include <utility>
#include <vector>

#include "conformance/device.hpp"

#include "device_selector.hpp"
#include <fmt/format.h>
#include <magic_enum/magic_enum.hpp>

#if defined(CONFORMANCE_DEVICE_CUDA)
  #if defined(CONFORMANCE_DEVICE_CUDA_DRIVER)
    #include <cuda.h>
  #else
    #include <cuda_runtime_api.h>
  #endif
#elif defined(CONFORMANCE_DEVICE_HIP) || defined(CONFORMANCE_DEVICE_HSA)
  #include <hip/hip_runtime_api.h>
  #include <hip/hip_version.h>
  #include <hsa/hsa.h>
  #include <hsa/hsa_ext_amd.h>
#elif defined(CONFORMANCE_DEVICE_OPENCL)
  #ifndef CL_TARGET_OPENCL_VERSION
    #define CL_TARGET_OPENCL_VERSION 120
  #endif
  #include <CL/cl.h>
#elif defined(CONFORMANCE_DEVICE_SYCL)
  #include <sycl/sycl.hpp>
#elif defined(CONFORMANCE_DEVICE_LEVELZERO)
  #include <level_zero/ze_api.h>
#endif

namespace conformance {
namespace {

#if defined(CONFORMANCE_DEVICE_CUDA) || defined(CONFORMANCE_DEVICE_HIP) || defined(CONFORMANCE_DEVICE_HSA)
bool targets_architecture(std::string_view architecture) {
  #if defined(CONFORMANCE_DEVICE_CUDA)
  if (architecture.starts_with("sm_")) architecture.remove_prefix(3);
  #elif defined(CONFORMANCE_DEVICE_HIP)
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
    if (separator == std::string_view::npos) break;
    targets.remove_prefix(separator + 1);
  }
  return false;
}
#endif

#if defined(CONFORMANCE_DEVICE_HIP) || defined(CONFORMANCE_DEVICE_HSA)
void validate_hip_runtime_version() {
  static_assert(HIP_VERSION_MAJOR == CONFORMANCE_ROCM_MAJOR && HIP_VERSION_MINOR == CONFORMANCE_ROCM_MINOR,
                "ROCm headers do not match configured build release");
  int runtime_version = 0;
  const auto status = hipRuntimeGetVersion(&runtime_version);
  if (status != hipSuccess)
    throw std::runtime_error(fmt::format("cannot query the loaded HIP runtime version: {}", hipGetErrorString(status)));
  constexpr int version_component_scale = 100000;
  constexpr int major_component_scale = 100;
  const auto runtime_major = runtime_version / (version_component_scale * major_component_scale);
  const auto runtime_minor = runtime_version / version_component_scale % major_component_scale;
  if (runtime_major != HIP_VERSION_MAJOR || runtime_minor != HIP_VERSION_MINOR)
    throw std::runtime_error(fmt::format("ROCm/HIP runtime version mismatch: artifact was built against {}, but loaded runtime is {}.{}",
                                         CONFORMANCE_ROCM_VERSION, runtime_major, runtime_minor));
}
#endif

#if defined(CONFORMANCE_DEVICE_HIP) || defined(CONFORMANCE_DEVICE_HSA)
struct HsaRuntime {
  HsaRuntime() {
    validate_hip_runtime_version();
    if (hsa_init() != HSA_STATUS_SUCCESS) throw std::runtime_error("cannot initialize HSA device discovery");
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

hsa_status_t collect_hsa_isa(hsa_isa_t isa, void *data) {
  std::array<char, 128> name{};
  if (hsa_isa_get_info_alt(isa, HSA_ISA_INFO_NAME, name.data()) != HSA_STATUS_SUCCESS) return HSA_STATUS_SUCCESS;
  auto &names = *static_cast<HsaIsaNames *>(data);
  if (!names.joined.empty()) names.joined += ',';
  names.joined += name.data();
  names.compatible |= targets_architecture(name.data());
  return HSA_STATUS_SUCCESS;
}

hsa_status_t collect_hsa_agent(hsa_agent_t agent, void *data) {
  hsa_device_type_t type{};
  if (hsa_agent_get_info(agent, HSA_AGENT_INFO_DEVICE, &type) != HSA_STATUS_SUCCESS || type != HSA_DEVICE_TYPE_GPU)
    return HSA_STATUS_SUCCESS;
  HsaIsaNames isas;
  if (hsa_agent_iterate_isas(agent, collect_hsa_isa, &isas) != HSA_STATUS_SUCCESS) return HSA_STATUS_SUCCESS;
  std::array<char, 64> name{};
  uint32_t bdf = 0, domain = 0;
  hsa_agent_get_info(agent, HSA_AGENT_INFO_NAME, name.data());
  if (hsa_agent_get_info(agent, static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_BDFID), &bdf) != HSA_STATUS_SUCCESS
      || hsa_agent_get_info(agent, static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_DOMAIN), &domain) != HSA_STATUS_SUCCESS)
    return HSA_STATUS_ERROR;
  std::array<char, 64> uuid{};
  hsa_agent_get_info(agent, static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_UUID), uuid.data());
  auto &discovery = *static_cast<HsaDiscovery *>(data);
  const std::string identity(uuid.data());
  discovery.selectors.push_back(identity.starts_with("GPU-") && identity.size() == 20 ? identity : fmt::to_string(discovery.agents.size()));
  discovery.devices.push_back(make_pci_device(domain, static_cast<uint8_t>((bdf >> 8) & 0xff), static_cast<uint8_t>((bdf >> 3) & 0x1f),
                                              static_cast<uint8_t>(bdf & 7)));
  discovery.agents.push_back(agent);
  discovery.descriptions.push_back(
      fmt::format("hsa:{}:{}:{}", name.data(), isas.joined, pci_description(domain, (bdf >> 8) & 0xff, (bdf >> 3) & 0x1f, bdf & 0x7)));
  return HSA_STATUS_SUCCESS;
}
#endif

} // namespace

static DeviceSelection discover_physical_device() {
#if defined(CONFORMANCE_DEVICE_CUDA_DRIVER)
  if (cuInit(0) != CUDA_SUCCESS) throw std::runtime_error("cannot initialize CUDA");
  int count = 0;
  if (cuDeviceGetCount(&count) != CUDA_SUCCESS || count <= 0) throw std::runtime_error("cannot enumerate CUDA devices");
  struct Device {
    CUdevice handle;
    int major;
    int minor;
    uint32_t domain;
    uint32_t bus;
    uint32_t slot;
    uint32_t function;
  };
  std::vector<Device> devices;
  std::vector<std::string> descriptions;
  for (size_t index = 0; index < static_cast<size_t>(count); ++index) {
    const auto device_index = static_cast<int>(index);
    Device candidate{};
    char name[256]{};
    char pci[32]{};
    if (cuDeviceGet(&candidate.handle, device_index) != CUDA_SUCCESS
        || cuDeviceGetName(name, sizeof(name), candidate.handle) != CUDA_SUCCESS
        || cuDeviceGetAttribute(&candidate.major, CU_DEVICE_ATTRIBUTE_COMPUTE_CAPABILITY_MAJOR, candidate.handle) != CUDA_SUCCESS
        || cuDeviceGetAttribute(&candidate.minor, CU_DEVICE_ATTRIBUTE_COMPUTE_CAPABILITY_MINOR, candidate.handle) != CUDA_SUCCESS
        || cuDeviceGetPCIBusId(pci, sizeof(pci), candidate.handle) != CUDA_SUCCESS
        || std::sscanf(pci, "%" SCNx32 ":%" SCNx32 ":%" SCNx32 ".%" SCNx32, &candidate.domain, &candidate.bus, &candidate.slot,
                       &candidate.function)
               != 4)
      throw std::runtime_error(fmt::format("cannot identify CUDA device {}", index));
    descriptions.push_back(fmt::format("cuda:{}:sm_{}:{}", name, candidate.major * 10 + candidate.minor,
                                       pci_description(candidate.domain, candidate.bus, candidate.slot, candidate.function)));
    devices.push_back(candidate);
  }
  const auto index = select_device(descriptions);
  const auto &selected = devices[index];
  const auto architecture = fmt::to_string(selected.major * 10 + selected.minor);
  if (!targets_architecture(architecture))
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
    visible += fmt::format("{:02x}", static_cast<unsigned>(uuid.bytes[byte]));
  }
  return {make_pci_device(selected.domain, static_cast<uint8_t>(selected.bus), static_cast<uint8_t>(selected.slot),
                          static_cast<uint8_t>(selected.function)),
          {{"CUDA_VISIBLE_DEVICES", visible}, {"CUDA_DEVICE_ORDER", "PCI_BUS_ID"}, {"ACC_DEVICE_NUM", "0"}, {"OMP_DEFAULT_DEVICE", "0"}}};
#elif defined(CONFORMANCE_DEVICE_CUDA)
  int count = 0;
  if (cudaGetDeviceCount(&count) != cudaSuccess || count <= 0) throw std::runtime_error("cannot enumerate CUDA devices");
  std::vector<cudaDeviceProp> devices(static_cast<size_t>(count));
  std::vector<std::string> descriptions;
  for (size_t index = 0; index < devices.size(); ++index) {
    auto &candidate = devices[index];
    if (cudaGetDeviceProperties(&candidate, static_cast<int>(index)) != cudaSuccess)
      throw std::runtime_error(fmt::format("cannot identify CUDA device {}", index));
    descriptions.push_back(fmt::format("cuda:{}:sm_{}:{}", candidate.name, candidate.major * 10 + candidate.minor,
                                       pci_description(candidate.pciDomainID, candidate.pciBusID, candidate.pciDeviceID, 0)));
  }
  const auto index = select_device(descriptions);
  const auto architecture = fmt::to_string(devices[index].major * 10 + devices[index].minor);
  if (!targets_architecture(architecture))
    throw std::runtime_error(fmt::format("selected CUDA device {} is incompatible with targeted architecture(s) {}", descriptions[index],
                                         CONFORMANCE_DEVICE_TARGETS));
  if (cudaSetDevice(static_cast<int>(index)) != cudaSuccess) throw std::runtime_error(fmt::format("cannot select {}", descriptions[index]));
  const auto &properties = devices[index];
  return {make_pci_device(static_cast<uint32_t>(properties.pciDomainID), static_cast<uint8_t>(properties.pciBusID),
                          static_cast<uint8_t>(properties.pciDeviceID), 0),
          {{"CUDA_VISIBLE_DEVICES", fmt::to_string(index)},
           {"CUDA_DEVICE_ORDER", "PCI_BUS_ID"},
           {"ACC_DEVICE_NUM", "0"},
           {"OMP_DEFAULT_DEVICE", "0"}}};
#elif defined(CONFORMANCE_DEVICE_HIP)
  validate_hip_runtime_version();
  int count = 0;
  if (hipGetDeviceCount(&count) != hipSuccess || count <= 0) throw std::runtime_error("cannot enumerate HIP devices");
  std::vector<hipDeviceProp_t> devices(static_cast<size_t>(count));
  std::vector<std::string> descriptions;
  for (size_t index = 0; index < devices.size(); ++index) {
    auto &candidate = devices[index];
    if (hipGetDeviceProperties(&candidate, static_cast<int>(index)) != hipSuccess)
      throw std::runtime_error(fmt::format("cannot identify HIP device {}", index));
    descriptions.push_back(fmt::format("hip:{}:{}:{}", candidate.name, candidate.gcnArchName,
                                       pci_description(candidate.pciDomainID, candidate.pciBusID, candidate.pciDeviceID, 0)));
  }
  const auto index = select_device(descriptions);
  if (!targets_architecture(devices[index].gcnArchName))
    throw std::runtime_error(fmt::format("selected HIP device {} is incompatible with targeted architecture(s) {}", descriptions[index],
                                         CONFORMANCE_DEVICE_TARGETS));
  if (hipSetDevice(static_cast<int>(index)) != hipSuccess) throw std::runtime_error(fmt::format("cannot select {}", descriptions[index]));
  const auto &properties = devices[index];
  const auto physical = make_pci_device(static_cast<uint32_t>(properties.pciDomainID), static_cast<uint8_t>(properties.pciBusID),
                                        static_cast<uint8_t>(properties.pciDeviceID), 0);
  static HsaRuntime runtime;
  HsaDiscovery hsa;
  if (hsa_iterate_agents(collect_hsa_agent, &hsa) != HSA_STATUS_SUCCESS) throw std::runtime_error("cannot map HIP device to ROCr");
  for (size_t i = 0; i < hsa.devices.size(); ++i)
    if (physical_device_key(hsa.devices[i]) == physical_device_key(physical))
      return {physical, {{"ROCR_VISIBLE_DEVICES", hsa.selectors[i]}, {"HIP_VISIBLE_DEVICES", "0"}, {"OMP_DEFAULT_DEVICE", "0"}}};
  throw std::runtime_error("selected HIP device has no matching ROCr device for OpenMP/miniapp execution");
#elif defined(CONFORMANCE_DEVICE_HSA)
  static HsaRuntime runtime;
  HsaDiscovery discovery;
  if (hsa_iterate_agents(collect_hsa_agent, &discovery) != HSA_STATUS_SUCCESS) throw std::runtime_error("cannot enumerate HSA devices");
  const auto selected = select_device(discovery.descriptions);
  HsaIsaNames isas;
  if (hsa_agent_iterate_isas(discovery.agents[selected], collect_hsa_isa, &isas) != HSA_STATUS_SUCCESS || !isas.compatible)
    throw std::runtime_error("selected HSA device is incompatible with artifact architecture(s): " CONFORMANCE_DEVICE_TARGETS);
  return {discovery.devices[selected],
          {{"ROCR_VISIBLE_DEVICES", discovery.selectors[selected]}, {"HIP_VISIBLE_DEVICES", "0"}},
          discovery.agents[selected].handle};
#elif defined(CONFORMANCE_DEVICE_OPENCL)
  cl_uint platform_count = 0;
  if (clGetPlatformIDs(0, nullptr, &platform_count) != CL_SUCCESS || platform_count == 0)
    throw std::runtime_error("cannot enumerate OpenCL platforms");
  std::vector<cl_platform_id> platforms(platform_count);
  if (clGetPlatformIDs(platform_count, platforms.data(), nullptr) != CL_SUCCESS)
    throw std::runtime_error("cannot enumerate OpenCL platforms");
  struct Candidate {
    cl_device_id device;
    cl_uint platform_index;
    cl_uint device_index;
    size_t flat_device_index;
    cl_device_type type;
    std::string type_name;
    std::string description;
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
  size_t flat_device_offset = 0;
  for (cl_uint platform_index = 0; platform_index < platform_count; ++platform_index) {
    cl_uint all_device_count = 0;
    std::vector<cl_device_id> all_devices;
    if (clGetDeviceIDs(platforms[platform_index], CL_DEVICE_TYPE_ALL, 0, nullptr, &all_device_count) == CL_SUCCESS
        && all_device_count != 0) {
      all_devices.resize(all_device_count);
      if (clGetDeviceIDs(platforms[platform_index], CL_DEVICE_TYPE_ALL, all_device_count, all_devices.data(), nullptr) != CL_SUCCESS)
        all_devices.clear();
    }
    for (cl_uint device_index = 0; device_index < all_devices.size(); ++device_index) {
      cl_device_type type = 0;
      if (clGetDeviceInfo(all_devices[device_index], CL_DEVICE_TYPE, sizeof(type), &type, nullptr) != CL_SUCCESS)
        throw std::runtime_error("cannot identify OpenCL device type");
      cl_device_type selection_type = CL_DEVICE_TYPE_DEFAULT;
      std::string type_name = "default";
      if ((type & CL_DEVICE_TYPE_GPU) != 0) {
        selection_type = CL_DEVICE_TYPE_GPU;
        type_name = "gpu";
      } else if ((type & CL_DEVICE_TYPE_CPU) != 0) {
        selection_type = CL_DEVICE_TYPE_CPU;
        type_name = "cpu";
      } else if ((type & CL_DEVICE_TYPE_ACCELERATOR) != 0) {
        selection_type = CL_DEVICE_TYPE_ACCELERATOR;
        type_name = "accelerator";
  #if defined(CL_DEVICE_TYPE_CUSTOM)
      } else if ((type & CL_DEVICE_TYPE_CUSTOM) != 0) {
        selection_type = CL_DEVICE_TYPE_CUSTOM;
        type_name = "custom";
  #endif
      }
      cl_uint type_index = 0;
      for (cl_uint prior = 0; prior < device_index; ++prior) {
        cl_device_type prior_type = 0;
        if (clGetDeviceInfo(all_devices[prior], CL_DEVICE_TYPE, sizeof(prior_type), &prior_type, nullptr) == CL_SUCCESS
            && (prior_type & selection_type) != 0)
          ++type_index;
      }
      auto description = "opencl:" + info(platforms[platform_index], CL_PLATFORM_VENDOR, clGetPlatformInfo) + ':'
                         + info(platforms[platform_index], CL_PLATFORM_NAME, clGetPlatformInfo) + ':'
                         + info(all_devices[device_index], CL_DEVICE_VENDOR, clGetDeviceInfo) + ':'
                         + info(all_devices[device_index], CL_DEVICE_NAME, clGetDeviceInfo);
      descriptions.push_back(description);
      candidates.push_back({all_devices[device_index], platform_index, type_index, flat_device_offset + device_index, type,
                            std::move(type_name), std::move(description)});
    }
    flat_device_offset += all_devices.size();
  }
  const auto candidate = candidates[select_device(descriptions)];
  const std::map<std::string, std::string> execution{{"CL_PLATFORM_INDEX", fmt::to_string(candidate.platform_index)},
                                                     {"CL_DEVICE_INDEX", fmt::to_string(candidate.device_index)},
                                                     {"CL_DEVICE_TYPE", candidate.type_name},
                                                     {"CONFORMANCE_OPENCL_DEVICE_INDEX", fmt::to_string(candidate.flat_device_index)}};
  const auto selection = [&](PhysicalDevice device) {
    return DeviceSelection{device, execution, static_cast<uint64_t>(reinterpret_cast<uintptr_t>(candidate.device))};
  };
  constexpr cl_device_info pci_bus_info_khr = 0x410f;
  struct PciBusInfo {
    cl_uint domain, bus, device, function;
  } pci{};
  if (clGetDeviceInfo(candidate.device, pci_bus_info_khr, sizeof(pci), &pci, nullptr) == CL_SUCCESS
      && (pci.domain != 0 || pci.bus != 0 || pci.device != 0 || pci.function != 0))
    return selection(
        make_pci_device(pci.domain, static_cast<uint8_t>(pci.bus), static_cast<uint8_t>(pci.device), static_cast<uint8_t>(pci.function)));
  constexpr cl_device_info topology_amd = 0x4037;
  constexpr cl_uint topology_type_pcie_amd = 1;
  struct AmdTopology {
    cl_uint type;
    cl_char unused[17];
    cl_char bus, device, function;
  } topology{};
  static_assert(sizeof(AmdTopology) == 24);
  if (clGetDeviceInfo(candidate.device, topology_amd, sizeof(topology), &topology, nullptr) == CL_SUCCESS
      && topology.type == topology_type_pcie_amd)
    return selection(make_pci_device(0, static_cast<uint8_t>(topology.bus), static_cast<uint8_t>(topology.device),
                                     static_cast<uint8_t>(topology.function)));
  constexpr cl_device_info uuid_khr = 0x106a;
  uint8_t uuid[16]{};
  if (clGetDeviceInfo(candidate.device, uuid_khr, sizeof(uuid), uuid, nullptr) == CL_SUCCESS
      && std::ranges::any_of(uuid, [](uint8_t byte) { return byte != 0; }))
    return selection(make_uuid_device(uuid));
  return selection(make_registry_device(fmt::format("{}:{}:{}", candidate.description, candidate.platform_index, candidate.device_index)));
#elif defined(CONFORMANCE_DEVICE_LEVELZERO)
  if (zeInit(0) != ZE_RESULT_SUCCESS) throw std::runtime_error("cannot initialize Level Zero");
  uint32_t driver_count = 0;
  if (zeDriverGet(&driver_count, nullptr) != ZE_RESULT_SUCCESS) throw std::runtime_error("cannot list Level Zero drivers");
  std::vector<ze_driver_handle_t> drivers(driver_count);
  if (zeDriverGet(&driver_count, drivers.data()) != ZE_RESULT_SUCCESS) throw std::runtime_error("cannot get Level Zero drivers");
  struct Candidate { PhysicalDevice physical; uint32_t driver, device; ze_device_handle_t handle; };
  std::vector<Candidate> candidates;
  std::vector<std::string> descriptions;
  for (uint32_t driver_index = 0; driver_index < driver_count; ++driver_index) {
    uint32_t device_count = 0;
    if (zeDeviceGet(drivers[driver_index], &device_count, nullptr) != ZE_RESULT_SUCCESS)
      throw std::runtime_error("cannot list Level Zero devices");
    std::vector<ze_device_handle_t> devices(device_count);
    if (zeDeviceGet(drivers[driver_index], &device_count, devices.data()) != ZE_RESULT_SUCCESS)
      throw std::runtime_error("cannot get Level Zero devices");
    for (uint32_t device_index = 0; device_index < device_count; ++device_index) {
      ze_device_properties_t properties{ZE_STRUCTURE_TYPE_DEVICE_PROPERTIES};
      if (zeDeviceGetProperties(devices[device_index], &properties) != ZE_RESULT_SUCCESS)
        throw std::runtime_error("cannot query Level Zero device");
      if (properties.type != ZE_DEVICE_TYPE_GPU) continue;
      ze_pci_ext_properties_t pci{ZE_STRUCTURE_TYPE_PCI_EXT_PROPERTIES};
      PhysicalDevice physical = make_uuid_device(properties.uuid.id);
      if (zeDevicePciGetPropertiesExt(devices[device_index], &pci) == ZE_RESULT_SUCCESS)
        physical = make_pci_device(pci.address.domain, static_cast<uint8_t>(pci.address.bus),
                                   static_cast<uint8_t>(pci.address.device), static_cast<uint8_t>(pci.address.function));
      descriptions.push_back(fmt::format("levelzero:{}:{}:{}", driver_index, device_index, properties.name));
      candidates.push_back({physical, driver_index, device_index, devices[device_index]});
    }
  }
  const auto &candidate = candidates[select_device(descriptions)];
  return {candidate.physical,
          {{"LZT_DEFAULT_DRIVER_IDX", fmt::to_string(candidate.driver)},
           {"LZT_DEFAULT_DEVICE_IDX", fmt::to_string(candidate.device)}},
          static_cast<uint64_t>(reinterpret_cast<uintptr_t>(candidate.handle))};
#elif defined(CONFORMANCE_DEVICE_SYCL)
  const auto all = sycl::device::get_devices();
  std::map<sycl::backend, size_t> ordinals;
  std::vector<sycl::device> devices;
  std::vector<std::string> descriptions, filters;
  for (const auto &candidate : all) {
    const auto backend = candidate.get_backend();
    const auto ordinal = ordinals[backend]++;
    if (!candidate.is_gpu()) continue;
    std::string backend_name(magic_enum::enum_name(backend));
    constexpr std::string_view ext_oneapi_prefix = "ext_oneapi_";
    if (backend_name.starts_with(ext_oneapi_prefix)) backend_name.erase(0, ext_oneapi_prefix.size());
    if (backend_name != "level_zero" && backend_name != "opencl" && backend_name != "cuda" && backend_name != "hip")
      throw std::runtime_error("SYCL backend cannot honor a propagated device selector");
    std::string address = "no-pci";
    if (candidate.has(sycl::aspect::ext_intel_pci_address)) address = candidate.get_info<sycl::ext::intel::info::device::pci_address>();
    descriptions.push_back(fmt::format("sycl:{}:{}:{}:{}:{}", backend_name, candidate.get_platform().get_info<sycl::info::platform::name>(),
                                       candidate.get_info<sycl::info::device::vendor>(), candidate.get_info<sycl::info::device::name>(),
                                       address));
    filters.push_back(fmt::format("{}:{}", backend_name, ordinal));
    devices.push_back(candidate);
  }
  const auto selected = select_device(descriptions);
  const auto &device = devices[selected];
  if (!device.has(sycl::aspect::ext_intel_pci_address)) throw std::runtime_error("selected SYCL GPU exposes no physical PCI identity");
  const auto address = device.get_info<sycl::ext::intel::info::device::pci_address>();
  uint32_t domain = 0, bus = 0, slot = 0, function = 0;
  if (std::sscanf(address.c_str(), "%" SCNx32 ":%" SCNx32 ":%" SCNx32 ".%" SCNx32, &domain, &bus, &slot, &function) != 4)
    throw std::runtime_error("invalid SYCL PCI identity: " + address);
  if (const auto *expected = std::getenv("CONFORMANCE_PHYSICAL_DEVICE"); expected && *expected && devices.size() != 1)
    throw std::runtime_error("propagated SYCL selector did not isolate exactly one GPU");
  return {make_pci_device(domain, static_cast<uint8_t>(bus), static_cast<uint8_t>(slot), static_cast<uint8_t>(function)),
          {{"ONEAPI_DEVICE_SELECTOR", filters[selected]}}};
#else
  #error "A conformance device backend is required"
#endif
}

const DeviceSelection &device_selection() {
  static const auto selected = [] {
    auto result = discover_physical_device();
    if (const auto *expected = std::getenv("CONFORMANCE_PHYSICAL_DEVICE");
        expected && *expected && physical_device_key(result.device) != expected)
      throw std::runtime_error("execution device " + physical_device_key(result.device) + " differs from locked/probed device " + expected);
    return result;
  }();
  return selected;
}
PhysicalDevice physical_device() { return device_selection().device; }
} // namespace conformance
