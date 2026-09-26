#include <algorithm>
#include <array>
#include <atomic>
#include <chrono>
#include <condition_variable>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <limits>
#include <map>
#include <memory>
#include <mutex>
#include <optional>
#include <string>
#include <thread>
#include <type_traits>
#include <unordered_map>
#include <unordered_set>
#include <variant>
#include <vector>

#include <unistd.h>

#include "gemstone_execution_rpc.hpp"
#include <gemstone/trace.hpp>

#include "client.hpp"
#include "completion.hpp"
#include "runtime.hpp"

void GemstoneVulkanDescriptorState::update(uint32_t writeCount, const VkWriteDescriptorSet *writes, uint32_t copyCount,
                                           const VkCopyDescriptorSet *copies) {
  for (uint32_t i = 0; i < writeCount; ++i) {
    const auto &write = writes[i];
    if ((write.descriptorType != VK_DESCRIPTOR_TYPE_STORAGE_BUFFER && write.descriptorType != VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER)
        || !write.pBufferInfo)
      continue;
    for (uint32_t j = 0; j < write.descriptorCount; ++j)
      buffers[write.dstSet][{write.dstBinding, write.dstArrayElement + j}] = write.pBufferInfo[j];
  }
  for (uint32_t i = 0; i < copyCount; ++i) {
    const auto &copy = copies[i];
    std::vector<std::optional<VkDescriptorBufferInfo>> source;
    source.reserve(copy.descriptorCount);
    for (uint32_t j = 0; j < copy.descriptorCount; ++j) {
      const auto *info = get(copy.srcSet, copy.srcBinding, copy.srcArrayElement + j);
      source.emplace_back(info ? std::optional(*info) : std::nullopt);
    }
    for (uint32_t j = 0; j < copy.descriptorCount; ++j)
      if (source[j]) buffers[copy.dstSet][{copy.dstBinding, copy.dstArrayElement + j}] = *source[j];
      else buffers[copy.dstSet].erase({copy.dstBinding, copy.dstArrayElement + j});
  }
}

const VkDescriptorBufferInfo *GemstoneVulkanDescriptorState::get(VkDescriptorSet set, uint32_t binding, uint32_t element) const {
  const auto group = buffers.find(set);
  if (group == buffers.end()) return nullptr;
  const auto value = group->second.find({binding, element});
  return value == group->second.end() ? nullptr : &value->second;
}

bool GemstoneVulkanDescriptorState::hasArrayElements(VkDescriptorSet set, uint32_t binding) const {
  const auto group = buffers.find(set);
  if (group == buffers.end()) return false;
  const auto value = group->second.lower_bound({binding, 1});
  return value != group->second.end() && value->first.first == binding;
}

std::optional<size_t> gemstoneVulkanDescriptorLength(VkDeviceSize bufferSize, VkDeviceSize offset, VkDeviceSize range) {
  if (offset > bufferSize) return std::nullopt;
  const VkDeviceSize length = range == VK_WHOLE_SIZE ? bufferSize - offset : range;
  if (length > bufferSize - offset || length > std::numeric_limits<size_t>::max()) return std::nullopt;
  return static_cast<size_t>(length);
}

GemstoneVulkanDescriptorAllocationPlan gemstoneVulkanDescriptorAllocationPlan(const std::vector<GemstoneVulkanDescriptorRange> &ranges) {
  GemstoneVulkanDescriptorAllocationPlan plan;
  plan.arguments.resize(ranges.size());
  std::vector<bool> assigned(ranges.size());
  for (size_t seed = 0; seed < ranges.size(); ++seed) {
    if (assigned[seed]) continue;
    const auto memory = ranges[seed].memory;
    VkDeviceSize begin = ranges[seed].offset;
    VkDeviceSize end = begin + ranges[seed].size;
    std::vector<size_t> members{seed};
    assigned[seed] = true;
    for (bool changed = true; changed;) {
      changed = false;
      for (size_t index = 0; index < ranges.size(); ++index) {
        if (assigned[index] || ranges[index].memory != memory) continue;
        const VkDeviceSize candidateBegin = ranges[index].offset;
        const VkDeviceSize candidateEnd = candidateBegin + ranges[index].size;
        if (candidateBegin >= end || begin >= candidateEnd) continue;
        begin = std::min(begin, candidateBegin);
        end = std::max(end, candidateEnd);
        assigned[index] = true;
        members.push_back(index);
        changed = true;
      }
    }
    const size_t allocation = plan.allocations.size();
    plan.allocations.push_back({memory, begin, static_cast<size_t>(end - begin)});
    for (const auto index : members)
      plan.arguments[index] = {allocation, ranges[index].offset - begin};
  }
  return plan;
}

namespace {

using namespace gemstone::execution;

constexpr size_t robustBufferGuard = 4096;
constexpr VkDeviceSize maxAllocationSize = static_cast<VkDeviceSize>(std::numeric_limits<int32_t>::max()) - robustBufferGuard;

template <typename Handle> Handle makeHandle() {
  auto *token = new uint8_t(0);
  if constexpr (std::is_pointer_v<Handle>) return reinterpret_cast<Handle>(token);
  else return static_cast<Handle>(reinterpret_cast<uintptr_t>(token));
}

template <typename Handle> void deleteHandle(Handle handle) {
  if (!handle) return;
  if constexpr (std::is_pointer_v<Handle>) delete reinterpret_cast<uint8_t *>(handle);
  else delete reinterpret_cast<uint8_t *>(static_cast<uintptr_t>(handle));
}

struct BoundBuffer {
  VkDeviceMemory memory = VK_NULL_HANDLE;
  VkDeviceSize offset = 0;
  VkDeviceSize size = 0;
};

struct Memory {
  std::vector<uint8_t> bytes;
};

struct Pipeline {
  std::vector<uint8_t> image;
  std::string entry;
  std::array<uint32_t, 3> local{1, 1, 1};
  std::vector<uint32_t> bindings;
};

struct Dispatch {
  VkPipeline pipeline = VK_NULL_HANDLE;
  VkDescriptorSet descriptors = VK_NULL_HANDLE;
  uint32_t x = 1, y = 1, z = 1;
};

struct Copy {
  VkBuffer source = VK_NULL_HANDLE;
  VkBuffer destination = VK_NULL_HANDLE;
  std::vector<VkBufferCopy> regions;
};

struct Command {
  VkPipeline pipeline = VK_NULL_HANDLE;
  VkDescriptorSet descriptors = VK_NULL_HANDLE;
  std::vector<std::variant<Dispatch, Copy>> operations;
  bool recording = false;
};

struct FenceState {
  std::mutex mutex;
  std::condition_variable changed;
  bool signaled = false;
};

struct DescriptorPoolState {
  uint32_t maxSets = 0;
  std::unordered_set<VkDescriptorSet> sets;
};

struct CommandPoolState {
  std::unordered_set<VkCommandBuffer> buffers;
};

std::mutex stateMutex;
std::unordered_map<VkBuffer, VkDeviceSize> bufferSizes;
std::unordered_map<VkBuffer, BoundBuffer> buffers;
std::unordered_map<VkDeviceMemory, std::shared_ptr<Memory>> memories;
GemstoneVulkanDescriptorState descriptorSets;
std::unordered_map<VkDescriptorPool, DescriptorPoolState> descriptorPools;
std::unordered_map<VkShaderModule, std::vector<uint8_t>> modules;
std::unordered_map<VkPipeline, Pipeline> pipelines;
std::unordered_map<VkCommandBuffer, Command> commands;
std::unordered_map<VkCommandPool, CommandPoolState> commandPools;
std::unordered_map<VkFence, std::shared_ptr<FenceState>> fences;
std::unordered_map<VkDevice, VkQueue> deviceQueues;
uint8_t physicalDeviceToken = 0;

VkPhysicalDevice physicalDeviceHandle() { return reinterpret_cast<VkPhysicalDevice>(&physicalDeviceToken); }

void setFence(VkFence fence, bool signaled) {
  if (!fence) return;
  std::shared_ptr<FenceState> state;
  {
    std::lock_guard lock(stateMutex);
    const auto it = fences.find(fence);
    if (it == fences.end()) return;
    state = it->second;
  }
  {
    std::lock_guard lock(state->mutex);
    state->signaled = signaled;
  }
  state->changed.notify_all();
}
std::mutex &executionMutex() {
  static std::mutex mutex;
  return mutex;
}

Client *executionClient() {
  static ClientManager manager;
  return manager.client();
}

bool run(const Dispatch &dispatch) {
  Pipeline pipeline;
  std::map<uint32_t, VkDescriptorBufferInfo> bindings;
  std::unordered_map<VkBuffer, BoundBuffer> bound;
  std::unordered_map<VkDeviceMemory, std::shared_ptr<Memory>> storage;
  {
    std::lock_guard lock(stateMutex);
    const auto pipe = pipelines.find(dispatch.pipeline);
    if (pipe == pipelines.end()) {
      std::fprintf(stderr, "Gemstone Vulkan: missing compute pipeline\n");
      return false;
    }
    pipeline = pipe->second;
    for (const auto binding : pipeline.bindings)
      if (descriptorSets.hasArrayElements(dispatch.descriptors, binding)) {
        std::fprintf(stderr, "Gemstone Vulkan: descriptor arrays are not supported by emulated dispatch\n");
        return false;
      }
    for (const auto binding : pipeline.bindings)
      if (const auto *info = descriptorSets.get(dispatch.descriptors, binding, 0)) bindings[binding] = *info;
    bound = buffers;
    for (const auto &[handle, memory] : memories)
      storage.emplace(handle, memory);
  }
  const bool trace = gemstone::traceEnabled("vulkan");
  if (trace)
    std::fprintf(stderr, "Gemstone Vulkan dispatch: %s bindings=%zu groups=%u,%u,%u local=%u,%u,%u\n", pipeline.entry.c_str(),
                 pipeline.bindings.size(), dispatch.x, dispatch.y, dispatch.z, pipeline.local[0], pipeline.local[1], pipeline.local[2]);
  std::lock_guard rpcLock(executionMutex());
  Client *client = executionClient();
  if (!client) {
    std::fprintf(stderr, "Gemstone Vulkan: GEMSTONE_RPC_CLIENT is absent\n");
    return false;
  }
  auto loaded = client->call(LoadProgram{"spirv-vulkan-binary", pipeline.image});
  if (!loaded) {
    std::fprintf(stderr, "Gemstone Vulkan: program load failed: %s\n", client->error().c_str());
    return false;
  }
  const ProgramId program = loaded.value();
  struct RemoteBuffer {
    AllocationId id;
    uint8_t *data;
    size_t size;
  };
  std::vector<RemoteBuffer> allocations;
  std::vector<KernelArgument> arguments;
  // Polyregion kernels can contain inactive SIMD lanes whose accesses fall just
  // beyond the descriptor range. Vulkan robustBufferAccess discards those
  // writes and returns zero for reads; the RPC allocation needs guard storage
  // so its stricter bounds checker can model that behavior.
  bool okay = true;
  std::vector<GemstoneVulkanDescriptorRange> ranges;
  ranges.reserve(pipeline.bindings.size());
  for (const auto binding : pipeline.bindings) {
    const auto descriptor = bindings.find(binding);
    if (descriptor == bindings.end()) {
      std::fprintf(stderr, "Gemstone Vulkan: descriptor binding %u is missing\n", binding);
      okay = false;
      break;
    }
    const auto &info = descriptor->second;
    const auto buffer = bound.find(info.buffer);
    if (buffer == bound.end()) {
      std::fprintf(stderr, "Gemstone Vulkan: descriptor buffer is unbound\n");
      okay = false;
      break;
    }
    const auto memory = storage.find(buffer->second.memory);
    if (memory == storage.end()) {
      std::fprintf(stderr, "Gemstone Vulkan: descriptor memory is unavailable\n");
      okay = false;
      break;
    }
    const auto length = gemstoneVulkanDescriptorLength(buffer->second.size, info.offset, info.range);
    if (!length || buffer->second.offset > std::numeric_limits<VkDeviceSize>::max() - info.offset) {
      std::fprintf(stderr, "Gemstone Vulkan: descriptor range is outside buffer\n");
      okay = false;
      break;
    }
    const auto begin = buffer->second.offset + info.offset;
    if (begin > memory->second->bytes.size() || *length > memory->second->bytes.size() - begin || *length > maxAllocationSize) {
      std::fprintf(stderr, "Gemstone Vulkan: descriptor range is outside memory\n");
      okay = false;
      break;
    }
    if (trace) {
      uint64_t sample = 0;
      auto *data = memory->second->bytes.data() + begin;
      std::memcpy(&sample, data, std::min(sizeof(sample), *length));
      std::fprintf(stderr,
                   "Gemstone Vulkan binding: binding=%u buffer=%zu memory=%zu bufferOffset=%llu descriptorOffset=%llu host=%p size=%zu "
                   "sample=%016llx\n",
                   binding, std::hash<VkBuffer>{}(info.buffer), std::hash<VkDeviceMemory>{}(buffer->second.memory),
                   static_cast<unsigned long long>(buffer->second.offset), static_cast<unsigned long long>(info.offset),
                   static_cast<void *>(data), *length, static_cast<unsigned long long>(sample));
    }
    ranges.push_back({buffer->second.memory, begin, *length});
  }
  const auto plan = gemstoneVulkanDescriptorAllocationPlan(ranges);
  if (okay)
    for (const auto &allocation : plan.allocations) {
      const auto memory = storage.find(allocation.memory);
      if (memory == storage.end()) {
        okay = false;
        break;
      }
      auto *data = memory->second->bytes.data() + allocation.offset;
      auto registered = client->call(RegisterAllocation{AllocationKind::Device, static_cast<int64_t>(allocation.size + robustBufferGuard)});
      if (!registered) {
        okay = false;
        break;
      }
      const AllocationId id = registered.value();
      allocations.push_back({id, data, allocation.size});
      if (!client->call(RegisterAllocationAddress{id, GuestAddress{static_cast<int64_t>(reinterpret_cast<uintptr_t>(data))}, true})) {
        okay = false;
        break;
      }
    }
  if (okay)
    for (const auto &argument : plan.arguments) {
      if (argument.allocation >= allocations.size() || argument.offset > std::numeric_limits<int64_t>::max()) {
        okay = false;
        break;
      }
      arguments.emplace_back(KernelArgumentAllocation{allocations[argument.allocation].id, static_cast<int64_t>(argument.offset)});
    }
  if (okay) {
    const auto local = pipeline.local;
    const Dimensions groupSize{static_cast<int32_t>(local[0]), static_cast<int32_t>(local[1]), static_cast<int32_t>(local[2])};
    const Dimensions groupCount{static_cast<int32_t>(dispatch.x), static_cast<int32_t>(dispatch.y), static_cast<int32_t>(dispatch.z)};
    const GlobalDimensions globalSize{static_cast<int64_t>(local[0]) * dispatch.x, static_cast<int64_t>(local[1]) * dispatch.y,
                                      static_cast<int64_t>(local[2]) * dispatch.z};
    auto launched = client->call(
        Launch{program, pipeline.entry, groupSize, groupCount, globalSize, 3, Coordinates{0, 0, 0}, 0, 0, std::move(arguments), {}});
    if (!launched) okay = false;
    else {
      CompletionResult result;
      std::string error;
      okay = collect_completion(*client, launched.value(), result, error, true);
      if (!okay) std::fprintf(stderr, "Gemstone Vulkan: launch failed: %s\n", error.c_str());
    }
  }
  if (okay && trace)
    for (const auto &allocation : allocations) {
      uint64_t sample = 0;
      std::memcpy(&sample, allocation.data, std::min(sizeof(sample), allocation.size));
      std::fprintf(stderr, "Gemstone Vulkan download: size=%zu sample=%016llx\n", allocation.size, static_cast<unsigned long long>(sample));
    }
  for (const auto &allocation : allocations)
    (void)client->call(ReleaseAllocation{allocation.id});
  (void)client->call(UnloadProgram{program});
  if (!okay) std::fprintf(stderr, "Gemstone Vulkan: dispatch failed: %s\n", client->error().c_str());
  return okay;
}

} // namespace

namespace {

template <typename T> VkResult enumerateOne(uint32_t *count, T *values, const T &value) {
  if (!count) return VK_ERROR_INITIALIZATION_FAILED;
  if (!values) {
    *count = 1;
    return VK_SUCCESS;
  }
  if (*count == 0) return VK_INCOMPLETE;
  values[0] = value;
  const bool complete = *count >= 1;
  *count = 1;
  return complete ? VK_SUCCESS : VK_INCOMPLETE;
}

void fillProperties(VkPhysicalDeviceProperties *properties) {
  if (!properties) return;
  *properties = {};
  properties->apiVersion = VK_API_VERSION_1_2;
  properties->driverVersion = VK_MAKE_VERSION(1, 0, 0);
  properties->vendorID = 0x1ae0;
  properties->deviceID = 1;
  properties->deviceType = VK_PHYSICAL_DEVICE_TYPE_CPU;
  const char *name = "Gemstone Vulkan Compute Device";
  if (const char *profile = gemstone::environment::value(gemstone::environment::Profile)) {
    if (std::strcmp(profile, "amd") == 0) {
      name = "AMD Gemstone Vulkan GPU";
      properties->vendorID = 0x1002;
    } else if (std::strcmp(profile, "nvidia") == 0) {
      name = "NVIDIA Gemstone Vulkan GPU";
      properties->vendorID = 0x10de;
    } else if (std::strcmp(profile, "intel") == 0) {
      name = "Intel Gemstone Vulkan GPU";
      properties->vendorID = 0x8086;
    } else if (std::strcmp(profile, "llvmpipe") == 0) {
      name = "llvmpipe (Gemstone Vulkan)";
      properties->vendorID = 0x10005;
    }
  }
  std::snprintf(properties->deviceName, sizeof(properties->deviceName), "%s", name);
  for (size_t i = 0; i < VK_UUID_SIZE; ++i)
    properties->pipelineCacheUUID[i] = static_cast<uint8_t>(0x47 + i);
  auto &limits = properties->limits;
  limits.maxImageDimension1D = limits.maxImageDimension2D = 16384;
  limits.maxImageDimension3D = 2048;
  limits.maxImageDimensionCube = 16384;
  limits.maxImageArrayLayers = 2048;
  limits.maxTexelBufferElements = std::numeric_limits<uint32_t>::max();
  limits.maxUniformBufferRange = 65536;
  limits.maxStorageBufferRange = std::numeric_limits<uint32_t>::max();
  limits.maxMemoryAllocationCount = 4096;
  limits.maxBoundDescriptorSets = 8;
  limits.maxPerStageDescriptorStorageBuffers = 1024;
  limits.maxPerStageDescriptorUniformBuffers = 1024;
  limits.maxPerStageResources = 2048;
  limits.maxDescriptorSetStorageBuffers = 1024;
  limits.maxDescriptorSetUniformBuffers = 1024;
  limits.maxComputeSharedMemorySize = 65536;
  limits.maxComputeWorkGroupCount[0] = limits.maxComputeWorkGroupCount[1] = limits.maxComputeWorkGroupCount[2] = 65535;
  limits.maxComputeWorkGroupInvocations = 1024;
  limits.maxComputeWorkGroupSize[0] = 1024;
  limits.maxComputeWorkGroupSize[1] = 1024;
  limits.maxComputeWorkGroupSize[2] = 64;
  limits.minMemoryMapAlignment = 64;
  limits.minUniformBufferOffsetAlignment = 16;
  limits.minStorageBufferOffsetAlignment = 16;
  limits.nonCoherentAtomSize = 1;
  limits.bufferImageGranularity = 1;
}

void fillFeatures(VkPhysicalDeviceFeatures *features) {
  if (!features) return;
  *features = {};
  features->robustBufferAccess = VK_TRUE;
  features->shaderFloat64 = VK_TRUE;
  features->shaderInt64 = VK_TRUE;
  features->shaderInt16 = VK_TRUE;
}

void fillMemoryProperties(VkPhysicalDeviceMemoryProperties *properties) {
  if (!properties) return;
  *properties = {};
  properties->memoryTypeCount = 1;
  properties->memoryTypes[0].propertyFlags = VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT | VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT
                                             | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT | VK_MEMORY_PROPERTY_HOST_CACHED_BIT;
  properties->memoryTypes[0].heapIndex = 0;
  properties->memoryHeapCount = 1;
  properties->memoryHeaps[0].size = VkDeviceSize{64} << 30;
  properties->memoryHeaps[0].flags = VK_MEMORY_HEAP_DEVICE_LOCAL_BIT;
}

void fillPropertyChain(void *next) {
  for (auto *base = static_cast<VkBaseOutStructure *>(next); base; base = base->pNext) {
    switch (base->sType) {
      case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ID_PROPERTIES: {
        auto *value = reinterpret_cast<VkPhysicalDeviceIDProperties *>(base);
        for (size_t i = 0; i < VK_UUID_SIZE; ++i) {
          value->deviceUUID[i] = static_cast<uint8_t>(0x47 + i);
          value->driverUUID[i] = static_cast<uint8_t>(0x67 + i);
        }
        value->deviceLUIDValid = VK_FALSE;
        break;
      }
      case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_3_PROPERTIES: {
        auto *value = reinterpret_cast<VkPhysicalDeviceMaintenance3Properties *>(base);
        value->maxPerSetDescriptors = 4096;
        value->maxMemoryAllocationSize = maxAllocationSize;
        break;
      }
      case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SUBGROUP_PROPERTIES: {
        auto *value = reinterpret_cast<VkPhysicalDeviceSubgroupProperties *>(base);
        value->subgroupSize = 32;
        value->supportedStages = VK_SHADER_STAGE_COMPUTE_BIT;
        value->supportedOperations = VK_SUBGROUP_FEATURE_BASIC_BIT | VK_SUBGROUP_FEATURE_VOTE_BIT | VK_SUBGROUP_FEATURE_ARITHMETIC_BIT
                                     | VK_SUBGROUP_FEATURE_BALLOT_BIT | VK_SUBGROUP_FEATURE_SHUFFLE_BIT
                                     | VK_SUBGROUP_FEATURE_SHUFFLE_RELATIVE_BIT;
        value->quadOperationsInAllStages = VK_FALSE;
        break;
      }
      default: break;
    }
  }
}

void fillFeatureChain(void *next) {
  for (auto *base = static_cast<VkBaseOutStructure *>(next); base; base = base->pNext) {
    if (base->sType == VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_FLOAT16_INT8_FEATURES) {
      auto *value = reinterpret_cast<VkPhysicalDeviceShaderFloat16Int8Features *>(base);
      value->shaderFloat16 = VK_TRUE;
      value->shaderInt8 = VK_TRUE;
    }
  }
}

bool copyBuffers(const Copy &copy) {
  std::lock_guard lock(stateMutex);
  const auto src = buffers.find(copy.source);
  const auto dst = buffers.find(copy.destination);
  if (src == buffers.end() || dst == buffers.end()) return false;
  const auto srcMem = memories.find(src->second.memory);
  const auto dstMem = memories.find(dst->second.memory);
  if (srcMem == memories.end() || dstMem == memories.end()) return false;
  for (const auto &region : copy.regions) {
    const auto srcOffset = src->second.offset + region.srcOffset;
    const auto dstOffset = dst->second.offset + region.dstOffset;
    if (region.srcOffset > src->second.size || region.size > src->second.size - region.srcOffset || region.dstOffset > dst->second.size
        || region.size > dst->second.size - region.dstOffset || srcOffset > srcMem->second->bytes.size()
        || region.size > srcMem->second->bytes.size() - srcOffset || dstOffset > dstMem->second->bytes.size()
        || region.size > dstMem->second->bytes.size() - dstOffset)
      return false;
    std::memmove(dstMem->second->bytes.data() + dstOffset, srcMem->second->bytes.data() + srcOffset, static_cast<size_t>(region.size));
  }
  return true;
}

} // namespace

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkEnumerateInstanceVersion(uint32_t *version) {
  if (!version) return VK_ERROR_INITIALIZATION_FAILED;
  *version = VK_API_VERSION_1_2;
  return VK_SUCCESS;
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkEnumerateInstanceExtensionProperties(const char *, uint32_t *count,
                                                                                 VkExtensionProperties *properties) {
  VkExtensionProperties property{};
  std::snprintf(property.extensionName, sizeof(property.extensionName), "%s", VK_KHR_GET_PHYSICAL_DEVICE_PROPERTIES_2_EXTENSION_NAME);
  property.specVersion = VK_KHR_GET_PHYSICAL_DEVICE_PROPERTIES_2_SPEC_VERSION;
  return enumerateOne(count, properties, property);
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkEnumerateInstanceLayerProperties(uint32_t *count, VkLayerProperties *) {
  if (!count) return VK_ERROR_INITIALIZATION_FAILED;
  *count = 0;
  return VK_SUCCESS;
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateInstance(const VkInstanceCreateInfo *info, const VkAllocationCallbacks *,
                                                           VkInstance *instance) {
  if (!info || info->sType != VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO || !instance) return VK_ERROR_INITIALIZATION_FAILED;
  if (info->pApplicationInfo && info->pApplicationInfo->apiVersion > VK_API_VERSION_1_2) return VK_ERROR_INCOMPATIBLE_DRIVER;
  *instance = makeHandle<VkInstance>();
  return VK_SUCCESS;
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyInstance(VkInstance instance, const VkAllocationCallbacks *) { deleteHandle(instance); }

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkEnumeratePhysicalDevices(VkInstance instance, uint32_t *count, VkPhysicalDevice *devices) {
  if (!instance) return VK_ERROR_INITIALIZATION_FAILED;
  return enumerateOne(count, devices, physicalDeviceHandle());
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPhysicalDeviceProperties(VkPhysicalDevice, VkPhysicalDeviceProperties *properties) {
  fillProperties(properties);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPhysicalDeviceProperties2(VkPhysicalDevice, VkPhysicalDeviceProperties2 *properties) {
  if (!properties) return;
  fillProperties(&properties->properties);
  fillPropertyChain(properties->pNext);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPhysicalDeviceProperties2KHR(VkPhysicalDevice device, VkPhysicalDeviceProperties2 *properties) {
  vkGetPhysicalDeviceProperties2(device, properties);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPhysicalDeviceFeatures(VkPhysicalDevice, VkPhysicalDeviceFeatures *features) {
  fillFeatures(features);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPhysicalDeviceFeatures2(VkPhysicalDevice, VkPhysicalDeviceFeatures2 *features) {
  if (!features) return;
  fillFeatures(&features->features);
  fillFeatureChain(features->pNext);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPhysicalDeviceFeatures2KHR(VkPhysicalDevice device, VkPhysicalDeviceFeatures2 *features) {
  vkGetPhysicalDeviceFeatures2(device, features);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPhysicalDeviceMemoryProperties(VkPhysicalDevice, VkPhysicalDeviceMemoryProperties *properties) {
  fillMemoryProperties(properties);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPhysicalDeviceMemoryProperties2(VkPhysicalDevice,
                                                                           VkPhysicalDeviceMemoryProperties2 *properties) {
  if (properties) fillMemoryProperties(&properties->memoryProperties);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPhysicalDeviceMemoryProperties2KHR(VkPhysicalDevice device,
                                                                              VkPhysicalDeviceMemoryProperties2 *properties) {
  vkGetPhysicalDeviceMemoryProperties2(device, properties);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetPhysicalDeviceQueueFamilyProperties(VkPhysicalDevice, uint32_t *count,
                                                                               VkQueueFamilyProperties *properties) {
  if (!count) return;
  if (!properties) {
    *count = 1;
    return;
  }
  if (*count > 0) {
    properties[0] = {};
    properties[0].queueFlags = VK_QUEUE_COMPUTE_BIT | VK_QUEUE_TRANSFER_BIT;
    properties[0].queueCount = 1;
    properties[0].timestampValidBits = 0;
    properties[0].minImageTransferGranularity = {1, 1, 1};
    *count = 1;
  }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkEnumerateDeviceExtensionProperties(VkPhysicalDevice, const char *, uint32_t *count,
                                                                               VkExtensionProperties *) {
  if (!count) return VK_ERROR_INITIALIZATION_FAILED;
  *count = 0;
  return VK_SUCCESS;
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateDevice(VkPhysicalDevice physicalDevice, const VkDeviceCreateInfo *info,
                                                         const VkAllocationCallbacks *, VkDevice *device) {
  if (physicalDevice != physicalDeviceHandle() || !info || !device || info->queueCreateInfoCount == 0)
    return VK_ERROR_INITIALIZATION_FAILED;
  *device = makeHandle<VkDevice>();
  std::lock_guard lock(stateMutex);
  deviceQueues[*device] = makeHandle<VkQueue>();
  return VK_SUCCESS;
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyDevice(VkDevice device, const VkAllocationCallbacks *) {
  VkQueue queue = VK_NULL_HANDLE;
  {
    std::lock_guard lock(stateMutex);
    const auto it = deviceQueues.find(device);
    if (it != deviceQueues.end()) {
      queue = it->second;
      deviceQueues.erase(it);
    }
  }
  deleteHandle(queue);
  deleteHandle(device);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetDeviceQueue(VkDevice device, uint32_t, uint32_t, VkQueue *queue) {
  if (!queue) return;
  std::lock_guard lock(stateMutex);
  const auto it = deviceQueues.find(device);
  *queue = it == deviceQueues.end() ? VK_NULL_HANDLE : it->second;
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetDeviceQueue2(VkDevice device, const VkDeviceQueueInfo2 *, VkQueue *queue) {
  vkGetDeviceQueue(device, 0, 0, queue);
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkAllocateMemory(VkDevice, const VkMemoryAllocateInfo *info, const VkAllocationCallbacks *,
                                                           VkDeviceMemory *memory) {
  if (!info || !memory || info->memoryTypeIndex != 0 || info->allocationSize > maxAllocationSize) return VK_ERROR_OUT_OF_DEVICE_MEMORY;
  auto object = std::make_shared<Memory>();
  try {
    object->bytes.resize(static_cast<size_t>(std::max<VkDeviceSize>(1, info->allocationSize)));
  } catch (const std::bad_alloc &) {
    return VK_ERROR_OUT_OF_HOST_MEMORY;
  }
  *memory = makeHandle<VkDeviceMemory>();
  std::lock_guard lock(stateMutex);
  memories[*memory] = std::move(object);
  return VK_SUCCESS;
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkFreeMemory(VkDevice, VkDeviceMemory memory, const VkAllocationCallbacks *) {
  {
    std::lock_guard lock(stateMutex);
    memories.erase(memory);
  }
  deleteHandle(memory);
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkMapMemory(VkDevice, VkDeviceMemory memory, VkDeviceSize offset, VkDeviceSize size,
                                                      VkMemoryMapFlags, void **data) {
  if (!data) return VK_ERROR_MEMORY_MAP_FAILED;
  std::lock_guard lock(stateMutex);
  const auto it = memories.find(memory);
  if (it == memories.end() || offset > it->second->bytes.size()) return VK_ERROR_MEMORY_MAP_FAILED;
  const VkDeviceSize length = size == VK_WHOLE_SIZE ? it->second->bytes.size() - offset : size;
  if (length > it->second->bytes.size() - offset) return VK_ERROR_MEMORY_MAP_FAILED;
  *data = it->second->bytes.data() + offset;
  return VK_SUCCESS;
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkUnmapMemory(VkDevice, VkDeviceMemory) {}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkFlushMappedMemoryRanges(VkDevice, uint32_t, const VkMappedMemoryRange *) { return VK_SUCCESS; }
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkInvalidateMappedMemoryRanges(VkDevice, uint32_t, const VkMappedMemoryRange *) {
  return VK_SUCCESS;
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateBuffer(VkDevice, const VkBufferCreateInfo *info, const VkAllocationCallbacks *,
                                                         VkBuffer *buffer) {
  if (!info || !buffer || info->size == 0) return VK_ERROR_INITIALIZATION_FAILED;
  *buffer = makeHandle<VkBuffer>();
  std::lock_guard lock(stateMutex);
  bufferSizes[*buffer] = info->size;
  return VK_SUCCESS;
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyBuffer(VkDevice, VkBuffer buffer, const VkAllocationCallbacks *) {
  {
    std::lock_guard lock(stateMutex);
    bufferSizes.erase(buffer);
    buffers.erase(buffer);
  }
  deleteHandle(buffer);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetBufferMemoryRequirements(VkDevice, VkBuffer buffer, VkMemoryRequirements *requirements) {
  if (!requirements) return;
  std::lock_guard lock(stateMutex);
  const auto it = bufferSizes.find(buffer);
  *requirements = {};
  if (it != bufferSizes.end()) requirements->size = it->second;
  requirements->alignment = 64;
  requirements->memoryTypeBits = 1;
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetBufferMemoryRequirements2(VkDevice device, const VkBufferMemoryRequirementsInfo2 *info,
                                                                     VkMemoryRequirements2 *requirements) {
  if (info && requirements) vkGetBufferMemoryRequirements(device, info->buffer, &requirements->memoryRequirements);
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkGetBufferMemoryRequirements2KHR(VkDevice device, const VkBufferMemoryRequirementsInfo2 *info,
                                                                        VkMemoryRequirements2 *requirements) {
  vkGetBufferMemoryRequirements2(device, info, requirements);
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkBindBufferMemory(VkDevice, VkBuffer buffer, VkDeviceMemory memory, VkDeviceSize offset) {
  std::lock_guard lock(stateMutex);
  const auto size = bufferSizes.find(buffer);
  const auto allocation = memories.find(memory);
  if (size == bufferSizes.end() || allocation == memories.end() || offset > allocation->second->bytes.size()
      || size->second > allocation->second->bytes.size() - offset)
    return VK_ERROR_OUT_OF_DEVICE_MEMORY;
  buffers[buffer] = {memory, offset, size->second};
  return VK_SUCCESS;
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkBindBufferMemory2(VkDevice device, uint32_t count, const VkBindBufferMemoryInfo *infos) {
  if (count && !infos) return VK_ERROR_INITIALIZATION_FAILED;
  for (uint32_t i = 0; i < count; ++i) {
    const auto result = vkBindBufferMemory(device, infos[i].buffer, infos[i].memory, infos[i].memoryOffset);
    if (result != VK_SUCCESS) return result;
  }
  return VK_SUCCESS;
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkBindBufferMemory2KHR(VkDevice device, uint32_t count, const VkBindBufferMemoryInfo *infos) {
  return vkBindBufferMemory2(device, count, infos);
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateDescriptorSetLayout(VkDevice, const VkDescriptorSetLayoutCreateInfo *,
                                                                      const VkAllocationCallbacks *, VkDescriptorSetLayout *layout) {
  if (!layout) return VK_ERROR_INITIALIZATION_FAILED;
  *layout = makeHandle<VkDescriptorSetLayout>();
  return VK_SUCCESS;
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyDescriptorSetLayout(VkDevice, VkDescriptorSetLayout layout, const VkAllocationCallbacks *) {
  deleteHandle(layout);
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateDescriptorPool(VkDevice, const VkDescriptorPoolCreateInfo *info,
                                                                 const VkAllocationCallbacks *, VkDescriptorPool *pool) {
  if (!info || !pool || info->maxSets == 0) return VK_ERROR_INITIALIZATION_FAILED;
  *pool = makeHandle<VkDescriptorPool>();
  std::lock_guard lock(stateMutex);
  descriptorPools.emplace(*pool, DescriptorPoolState{info->maxSets, {}});
  return VK_SUCCESS;
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyDescriptorPool(VkDevice, VkDescriptorPool pool, const VkAllocationCallbacks *) {
  {
    std::lock_guard lock(stateMutex);
    const auto found = descriptorPools.find(pool);
    if (found != descriptorPools.end()) {
      for (const auto set : found->second.sets) {
        descriptorSets.buffers.erase(set);
        deleteHandle(set);
      }
      descriptorPools.erase(found);
    }
  }
  deleteHandle(pool);
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkResetDescriptorPool(VkDevice, VkDescriptorPool pool, VkDescriptorPoolResetFlags) {
  std::lock_guard lock(stateMutex);
  const auto found = descriptorPools.find(pool);
  if (found == descriptorPools.end()) return VK_ERROR_INITIALIZATION_FAILED;
  for (const auto set : found->second.sets) {
    descriptorSets.buffers.erase(set);
    deleteHandle(set);
  }
  found->second.sets.clear();
  return VK_SUCCESS;
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkAllocateDescriptorSets(VkDevice, const VkDescriptorSetAllocateInfo *info,
                                                                   VkDescriptorSet *sets) {
  if (!info || (info->descriptorSetCount && !sets)) return VK_ERROR_INITIALIZATION_FAILED;
  std::lock_guard lock(stateMutex);
  const auto pool = descriptorPools.find(info->descriptorPool);
  if (pool == descriptorPools.end()) return VK_ERROR_INITIALIZATION_FAILED;
  if (info->descriptorSetCount > pool->second.maxSets - pool->second.sets.size()) return VK_ERROR_OUT_OF_POOL_MEMORY;
  for (uint32_t i = 0; i < info->descriptorSetCount; ++i) {
    sets[i] = makeHandle<VkDescriptorSet>();
    pool->second.sets.insert(sets[i]);
  }
  return VK_SUCCESS;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkFreeDescriptorSets(VkDevice, VkDescriptorPool pool, uint32_t count,
                                                               const VkDescriptorSet *sets) {
  if (count && !sets) return VK_ERROR_INITIALIZATION_FAILED;
  std::lock_guard lock(stateMutex);
  const auto owner = descriptorPools.find(pool);
  if (owner == descriptorPools.end()) return VK_ERROR_INITIALIZATION_FAILED;
  for (uint32_t i = 0; i < count; ++i) {
    if (owner->second.sets.erase(sets[i]) == 0) return VK_ERROR_INITIALIZATION_FAILED;
    descriptorSets.buffers.erase(sets[i]);
    deleteHandle(sets[i]);
  }
  return VK_SUCCESS;
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkUpdateDescriptorSets(VkDevice, uint32_t writeCount, const VkWriteDescriptorSet *writes,
                                                             uint32_t copyCount, const VkCopyDescriptorSet *copies) {
  std::lock_guard lock(stateMutex);
  descriptorSets.update(writeCount, writes, copyCount, copies);
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreatePipelineLayout(VkDevice, const VkPipelineLayoutCreateInfo *,
                                                                 const VkAllocationCallbacks *, VkPipelineLayout *layout) {
  if (!layout) return VK_ERROR_INITIALIZATION_FAILED;
  *layout = makeHandle<VkPipelineLayout>();
  return VK_SUCCESS;
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyPipelineLayout(VkDevice, VkPipelineLayout layout, const VkAllocationCallbacks *) {
  deleteHandle(layout);
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreatePipelineCache(VkDevice, const VkPipelineCacheCreateInfo *, const VkAllocationCallbacks *,
                                                                VkPipelineCache *cache) {
  if (!cache) return VK_ERROR_INITIALIZATION_FAILED;
  *cache = makeHandle<VkPipelineCache>();
  return VK_SUCCESS;
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyPipelineCache(VkDevice, VkPipelineCache cache, const VkAllocationCallbacks *) {
  deleteHandle(cache);
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetPipelineCacheData(VkDevice, VkPipelineCache, size_t *size, void *data) {
  if (!size) return VK_ERROR_INITIALIZATION_FAILED;
  if (!data) {
    *size = 0;
    return VK_SUCCESS;
  }
  *size = 0;
  return VK_SUCCESS;
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateShaderModule(VkDevice, const VkShaderModuleCreateInfo *info,
                                                               const VkAllocationCallbacks *, VkShaderModule *module) {
  if (!info || !module || !info->pCode || info->codeSize < 20 || info->codeSize % sizeof(uint32_t) != 0) return VK_ERROR_INVALID_SHADER_NV;
  *module = makeHandle<VkShaderModule>();
  const auto *bytes = reinterpret_cast<const uint8_t *>(info->pCode);
  std::lock_guard lock(stateMutex);
  modules[*module] = {bytes, bytes + info->codeSize};
  return VK_SUCCESS;
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyShaderModule(VkDevice, VkShaderModule module, const VkAllocationCallbacks *) {
  {
    std::lock_guard lock(stateMutex);
    modules.erase(module);
  }
  deleteHandle(module);
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateComputePipelines(VkDevice, VkPipelineCache, uint32_t count,
                                                                   const VkComputePipelineCreateInfo *infos, const VkAllocationCallbacks *,
                                                                   VkPipeline *output) {
  if ((count && (!infos || !output))) return VK_ERROR_INITIALIZATION_FAILED;
  for (uint32_t i = 0; i < count; ++i) {
    Pipeline pipe;
    {
      std::lock_guard lock(stateMutex);
      const auto module = modules.find(infos[i].stage.module);
      if (module == modules.end() || !infos[i].stage.pName) {
        if (gemstone::traceEnabled("vulkan")) std::fprintf(stderr, "Gemstone Vulkan: compute pipeline has no shader module or entry\n");
        return VK_ERROR_INVALID_SHADER_NV;
      }
      pipe.image = module->second;
      pipe.entry = infos[i].stage.pName;
    }
    std::map<uint32_t, std::vector<uint8_t>> specializationValues;
    if (const auto *spec = infos[i].stage.pSpecializationInfo) {
      if ((spec->mapEntryCount && !spec->pMapEntries) || (spec->dataSize && !spec->pData)) return VK_ERROR_INVALID_SHADER_NV;
      for (uint32_t entry = 0; entry < spec->mapEntryCount; ++entry) {
        const auto &map = spec->pMapEntries[entry];
        if (map.offset > spec->dataSize || map.size > spec->dataSize - map.offset) return VK_ERROR_INVALID_SHADER_NV;
        const auto *begin = static_cast<const uint8_t *>(spec->pData) + map.offset;
        specializationValues[map.constantID] = {begin, begin + map.size};
      }
    }
    {
      std::lock_guard rpcLock(executionMutex());
      Client *client = executionClient();
      if (!client) return VK_ERROR_INVALID_SHADER_NV;
      if (!specializationValues.empty()) {
        std::vector<SpecializationConstant> constants;
        constants.reserve(specializationValues.size());
        for (const auto &[id, value] : specializationValues)
          constants.push_back(SpecializationConstant{IntPositive0{static_cast<int32_t>(id)}, value});
        auto specialized = client->call(SpecializeImage{pipe.image, constants});
        if (!specialized) {
          if (gemstone::traceEnabled("vulkan")) {
            std::fprintf(stderr, "Gemstone Vulkan: cannot specialize %zu-byte SPIR-V module\n", pipe.image.size());
            for (const auto &[id, value] : specializationValues)
              std::fprintf(stderr, "Gemstone Vulkan: specialization id=%u size=%zu\n", id, value.size());
          }
          return VK_ERROR_INVALID_SHADER_NV;
        }
        pipe.image = std::move(specialized.value());
      }
      auto described = client->call(DescribeImage{"spirv-vulkan-binary", pipe.image, KernelName{pipe.entry}});
      if (!described) {
        if (gemstone::traceEnabled("vulkan")) std::fprintf(stderr, "Gemstone Vulkan: cannot reflect entry %s\n", pipe.entry.c_str());
        return VK_ERROR_INVALID_SHADER_NV;
      }
      const auto &metadata = described.value();
      if (metadata.requiredWorkGroupSize)
        pipe.local = {static_cast<uint32_t>(metadata.requiredWorkGroupSize->x.value),
                      static_cast<uint32_t>(metadata.requiredWorkGroupSize->y.value),
                      static_cast<uint32_t>(metadata.requiredWorkGroupSize->z.value)};
      pipe.bindings.clear();
      pipe.bindings.reserve(metadata.bindingIndices.size());
      for (const auto binding : metadata.bindingIndices)
        pipe.bindings.push_back(static_cast<uint32_t>(binding.value));
    }
    if (const char *directory = gemstone::environment::value(gemstone::environment::VulkanDump)) {
      static std::atomic<unsigned> serial{0};
      char filename[1024];
      std::snprintf(filename, sizeof(filename), "%s/gemstone-%ld-%u.spv", directory, static_cast<long>(getpid()), serial++);
      if (auto *file = std::fopen(filename, "wb")) {
        (void)std::fwrite(pipe.image.data(), 1, pipe.image.size(), file);
        std::fclose(file);
      }
    }
    {
      std::lock_guard lock(stateMutex);
      output[i] = makeHandle<VkPipeline>();
      pipelines[output[i]] = std::move(pipe);
    }
  }
  return VK_SUCCESS;
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyPipeline(VkDevice, VkPipeline pipeline, const VkAllocationCallbacks *) {
  {
    std::lock_guard lock(stateMutex);
    pipelines.erase(pipeline);
  }
  deleteHandle(pipeline);
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateCommandPool(VkDevice, const VkCommandPoolCreateInfo *info, const VkAllocationCallbacks *,
                                                              VkCommandPool *pool) {
  if (!info || !pool) return VK_ERROR_INITIALIZATION_FAILED;
  *pool = makeHandle<VkCommandPool>();
  std::lock_guard lock(stateMutex);
  commandPools.emplace(*pool, CommandPoolState{});
  return VK_SUCCESS;
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyCommandPool(VkDevice, VkCommandPool pool, const VkAllocationCallbacks *) {
  {
    std::lock_guard lock(stateMutex);
    const auto found = commandPools.find(pool);
    if (found != commandPools.end()) {
      for (const auto command : found->second.buffers) {
        commands.erase(command);
        deleteHandle(command);
      }
      commandPools.erase(found);
    }
  }
  deleteHandle(pool);
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkResetCommandPool(VkDevice, VkCommandPool pool, VkCommandPoolResetFlags) {
  std::lock_guard lock(stateMutex);
  const auto found = commandPools.find(pool);
  if (found == commandPools.end()) return VK_ERROR_INITIALIZATION_FAILED;
  for (const auto command : found->second.buffers)
    commands[command] = {};
  return VK_SUCCESS;
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkAllocateCommandBuffers(VkDevice, const VkCommandBufferAllocateInfo *info,
                                                                   VkCommandBuffer *output) {
  if (!info || (info->commandBufferCount && !output)) return VK_ERROR_INITIALIZATION_FAILED;
  std::lock_guard lock(stateMutex);
  const auto pool = commandPools.find(info->commandPool);
  if (pool == commandPools.end()) return VK_ERROR_INITIALIZATION_FAILED;
  for (uint32_t i = 0; i < info->commandBufferCount; ++i) {
    output[i] = makeHandle<VkCommandBuffer>();
    commands[output[i]] = {};
    pool->second.buffers.insert(output[i]);
  }
  return VK_SUCCESS;
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkFreeCommandBuffers(VkDevice, VkCommandPool pool, uint32_t count,
                                                           const VkCommandBuffer *buffersToFree) {
  if (!buffersToFree) return;
  std::lock_guard lock(stateMutex);
  const auto owner = commandPools.find(pool);
  for (uint32_t i = 0; i < count; ++i) {
    if (owner != commandPools.end()) owner->second.buffers.erase(buffersToFree[i]);
    commands.erase(buffersToFree[i]);
    deleteHandle(buffersToFree[i]);
  }
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkBeginCommandBuffer(VkCommandBuffer command, const VkCommandBufferBeginInfo *) {
  std::lock_guard lock(stateMutex);
  auto &state = commands[command];
  state = {};
  state.recording = true;
  return VK_SUCCESS;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkEndCommandBuffer(VkCommandBuffer command) {
  std::lock_guard lock(stateMutex);
  const auto it = commands.find(command);
  if (it == commands.end() || !it->second.recording) return VK_ERROR_INITIALIZATION_FAILED;
  it->second.recording = false;
  return VK_SUCCESS;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkResetCommandBuffer(VkCommandBuffer command, VkCommandBufferResetFlags) {
  std::lock_guard lock(stateMutex);
  commands[command] = {};
  return VK_SUCCESS;
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBindPipeline(VkCommandBuffer command, VkPipelineBindPoint point, VkPipeline pipeline) {
  if (point != VK_PIPELINE_BIND_POINT_COMPUTE) return;
  std::lock_guard lock(stateMutex);
  commands[command].pipeline = pipeline;
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdBindDescriptorSets(VkCommandBuffer command, VkPipelineBindPoint point, VkPipelineLayout,
                                                              uint32_t first, uint32_t count, const VkDescriptorSet *sets, uint32_t,
                                                              const uint32_t *) {
  if (point != VK_PIPELINE_BIND_POINT_COMPUTE || first != 0 || count == 0 || !sets) return;
  std::lock_guard lock(stateMutex);
  commands[command].descriptors = sets[0];
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdDispatch(VkCommandBuffer command, uint32_t x, uint32_t y, uint32_t z) {
  std::lock_guard lock(stateMutex);
  auto &state = commands[command];
  state.operations.emplace_back(Dispatch{state.pipeline, state.descriptors, x, y, z});
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkCmdCopyBuffer(VkCommandBuffer command, VkBuffer source, VkBuffer destination, uint32_t count,
                                                      const VkBufferCopy *regions) {
  Copy copy{source, destination, {}};
  if (count && regions) copy.regions.assign(regions, regions + count);
  std::lock_guard lock(stateMutex);
  commands[command].operations.emplace_back(std::move(copy));
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateFence(VkDevice, const VkFenceCreateInfo *info, const VkAllocationCallbacks *,
                                                        VkFence *fence) {
  if (!info || !fence) return VK_ERROR_INITIALIZATION_FAILED;
  *fence = makeHandle<VkFence>();
  auto state = std::make_shared<FenceState>();
  state->signaled = (info->flags & VK_FENCE_CREATE_SIGNALED_BIT) != 0;
  std::lock_guard lock(stateMutex);
  fences[*fence] = std::move(state);
  return VK_SUCCESS;
}
extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyFence(VkDevice, VkFence fence, const VkAllocationCallbacks *) {
  {
    std::lock_guard lock(stateMutex);
    fences.erase(fence);
  }
  deleteHandle(fence);
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkGetFenceStatus(VkDevice, VkFence fence) {
  std::shared_ptr<FenceState> state;
  {
    std::lock_guard lock(stateMutex);
    const auto it = fences.find(fence);
    if (it == fences.end()) return VK_ERROR_DEVICE_LOST;
    state = it->second;
  }
  std::lock_guard lock(state->mutex);
  return state->signaled ? VK_SUCCESS : VK_NOT_READY;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkResetFences(VkDevice, uint32_t count, const VkFence *values) {
  if (count && !values) return VK_ERROR_INITIALIZATION_FAILED;
  for (uint32_t i = 0; i < count; ++i)
    setFence(values[i], false);
  return VK_SUCCESS;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkWaitForFences(VkDevice, uint32_t count, const VkFence *values, VkBool32 waitAll,
                                                          uint64_t timeout) {
  if (count == 0) return VK_SUCCESS;
  if (!values) return VK_ERROR_INITIALIZATION_FAILED;
  const auto deadline = timeout == std::numeric_limits<uint64_t>::max()
                            ? std::chrono::steady_clock::time_point::max()
                            : std::chrono::steady_clock::now() + std::chrono::nanoseconds(timeout);
  for (;;) {
    uint32_t signaled = 0;
    for (uint32_t i = 0; i < count; ++i)
      if (vkGetFenceStatus(VK_NULL_HANDLE, values[i]) == VK_SUCCESS) ++signaled;
    if ((waitAll && signaled == count) || (!waitAll && signaled != 0)) return VK_SUCCESS;
    if (timeout == 0 || std::chrono::steady_clock::now() >= deadline) return VK_TIMEOUT;
    std::this_thread::yield();
  }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkQueueSubmit(VkQueue, uint32_t count, const VkSubmitInfo *infos, VkFence fence) {
  if (count && !infos) return VK_ERROR_INITIALIZATION_FAILED;
  std::vector<std::variant<Dispatch, Copy>> operations;
  {
    std::lock_guard lock(stateMutex);
    for (uint32_t i = 0; i < count; ++i) {
      if (infos[i].waitSemaphoreCount || infos[i].signalSemaphoreCount) return VK_ERROR_FEATURE_NOT_PRESENT;
      for (uint32_t j = 0; j < infos[i].commandBufferCount; ++j) {
        const auto command = commands.find(infos[i].pCommandBuffers[j]);
        if (command == commands.end()) return VK_ERROR_INITIALIZATION_FAILED;
        operations.insert(operations.end(), command->second.operations.begin(), command->second.operations.end());
      }
    }
  }
  for (const auto &operation : operations) {
    const bool okay =
        std::holds_alternative<Dispatch>(operation) ? run(std::get<Dispatch>(operation)) : copyBuffers(std::get<Copy>(operation));
    if (!okay) return VK_ERROR_DEVICE_LOST;
  }
  setFence(fence, true);
  return VK_SUCCESS;
}
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkQueueWaitIdle(VkQueue) { return VK_SUCCESS; }
extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkDeviceWaitIdle(VkDevice) { return VK_SUCCESS; }

namespace {
PFN_vkVoidFunction supportedFunction(const char *name) {
#define GEMSTONE_VK_FUNCTION(function)                                                                                                     \
  if (std::strcmp(name, #function) == 0) return reinterpret_cast<PFN_vkVoidFunction>(function)
  GEMSTONE_VK_FUNCTION(vkGetInstanceProcAddr);
  GEMSTONE_VK_FUNCTION(vkGetDeviceProcAddr);
  GEMSTONE_VK_FUNCTION(vkEnumerateInstanceVersion);
  GEMSTONE_VK_FUNCTION(vkEnumerateInstanceExtensionProperties);
  GEMSTONE_VK_FUNCTION(vkEnumerateInstanceLayerProperties);
  GEMSTONE_VK_FUNCTION(vkCreateInstance);
  GEMSTONE_VK_FUNCTION(vkDestroyInstance);
  GEMSTONE_VK_FUNCTION(vkEnumeratePhysicalDevices);
  GEMSTONE_VK_FUNCTION(vkGetPhysicalDeviceProperties);
  GEMSTONE_VK_FUNCTION(vkGetPhysicalDeviceProperties2);
  GEMSTONE_VK_FUNCTION(vkGetPhysicalDeviceProperties2KHR);
  GEMSTONE_VK_FUNCTION(vkGetPhysicalDeviceFeatures);
  GEMSTONE_VK_FUNCTION(vkGetPhysicalDeviceFeatures2);
  GEMSTONE_VK_FUNCTION(vkGetPhysicalDeviceFeatures2KHR);
  GEMSTONE_VK_FUNCTION(vkGetPhysicalDeviceMemoryProperties);
  GEMSTONE_VK_FUNCTION(vkGetPhysicalDeviceMemoryProperties2);
  GEMSTONE_VK_FUNCTION(vkGetPhysicalDeviceMemoryProperties2KHR);
  GEMSTONE_VK_FUNCTION(vkGetPhysicalDeviceQueueFamilyProperties);
  GEMSTONE_VK_FUNCTION(vkEnumerateDeviceExtensionProperties);
  GEMSTONE_VK_FUNCTION(vkCreateDevice);
  GEMSTONE_VK_FUNCTION(vkDestroyDevice);
  GEMSTONE_VK_FUNCTION(vkGetDeviceQueue);
  GEMSTONE_VK_FUNCTION(vkGetDeviceQueue2);
  GEMSTONE_VK_FUNCTION(vkAllocateMemory);
  GEMSTONE_VK_FUNCTION(vkFreeMemory);
  GEMSTONE_VK_FUNCTION(vkMapMemory);
  GEMSTONE_VK_FUNCTION(vkUnmapMemory);
  GEMSTONE_VK_FUNCTION(vkFlushMappedMemoryRanges);
  GEMSTONE_VK_FUNCTION(vkInvalidateMappedMemoryRanges);
  GEMSTONE_VK_FUNCTION(vkCreateBuffer);
  GEMSTONE_VK_FUNCTION(vkDestroyBuffer);
  GEMSTONE_VK_FUNCTION(vkGetBufferMemoryRequirements);
  GEMSTONE_VK_FUNCTION(vkGetBufferMemoryRequirements2);
  GEMSTONE_VK_FUNCTION(vkGetBufferMemoryRequirements2KHR);
  GEMSTONE_VK_FUNCTION(vkBindBufferMemory);
  GEMSTONE_VK_FUNCTION(vkBindBufferMemory2);
  GEMSTONE_VK_FUNCTION(vkBindBufferMemory2KHR);
  GEMSTONE_VK_FUNCTION(vkBindImageMemory);
  GEMSTONE_VK_FUNCTION(vkBindImageMemory2);
  GEMSTONE_VK_FUNCTION(vkBindImageMemory2KHR);
  GEMSTONE_VK_FUNCTION(vkGetImageMemoryRequirements);
  GEMSTONE_VK_FUNCTION(vkGetImageMemoryRequirements2);
  GEMSTONE_VK_FUNCTION(vkGetImageMemoryRequirements2KHR);
  GEMSTONE_VK_FUNCTION(vkCreateImage);
  GEMSTONE_VK_FUNCTION(vkDestroyImage);
  GEMSTONE_VK_FUNCTION(vkCmdCopyBuffer);
  GEMSTONE_VK_FUNCTION(vkCreateDescriptorSetLayout);
  GEMSTONE_VK_FUNCTION(vkDestroyDescriptorSetLayout);
  GEMSTONE_VK_FUNCTION(vkCreateDescriptorPool);
  GEMSTONE_VK_FUNCTION(vkDestroyDescriptorPool);
  GEMSTONE_VK_FUNCTION(vkResetDescriptorPool);
  GEMSTONE_VK_FUNCTION(vkAllocateDescriptorSets);
  GEMSTONE_VK_FUNCTION(vkFreeDescriptorSets);
  GEMSTONE_VK_FUNCTION(vkUpdateDescriptorSets);
  GEMSTONE_VK_FUNCTION(vkCreatePipelineLayout);
  GEMSTONE_VK_FUNCTION(vkDestroyPipelineLayout);
  GEMSTONE_VK_FUNCTION(vkCreatePipelineCache);
  GEMSTONE_VK_FUNCTION(vkDestroyPipelineCache);
  GEMSTONE_VK_FUNCTION(vkGetPipelineCacheData);
  GEMSTONE_VK_FUNCTION(vkCreateShaderModule);
  GEMSTONE_VK_FUNCTION(vkDestroyShaderModule);
  GEMSTONE_VK_FUNCTION(vkCreateComputePipelines);
  GEMSTONE_VK_FUNCTION(vkDestroyPipeline);
  GEMSTONE_VK_FUNCTION(vkCreateCommandPool);
  GEMSTONE_VK_FUNCTION(vkDestroyCommandPool);
  GEMSTONE_VK_FUNCTION(vkResetCommandPool);
  GEMSTONE_VK_FUNCTION(vkAllocateCommandBuffers);
  GEMSTONE_VK_FUNCTION(vkFreeCommandBuffers);
  GEMSTONE_VK_FUNCTION(vkBeginCommandBuffer);
  GEMSTONE_VK_FUNCTION(vkEndCommandBuffer);
  GEMSTONE_VK_FUNCTION(vkResetCommandBuffer);
  GEMSTONE_VK_FUNCTION(vkCmdBindPipeline);
  GEMSTONE_VK_FUNCTION(vkCmdBindDescriptorSets);
  GEMSTONE_VK_FUNCTION(vkCmdDispatch);
  GEMSTONE_VK_FUNCTION(vkCreateFence);
  GEMSTONE_VK_FUNCTION(vkDestroyFence);
  GEMSTONE_VK_FUNCTION(vkGetFenceStatus);
  GEMSTONE_VK_FUNCTION(vkResetFences);
  GEMSTONE_VK_FUNCTION(vkWaitForFences);
  GEMSTONE_VK_FUNCTION(vkQueueSubmit);
  GEMSTONE_VK_FUNCTION(vkQueueWaitIdle);
  GEMSTONE_VK_FUNCTION(vkDeviceWaitIdle);
#undef GEMSTONE_VK_FUNCTION
  return nullptr;
}
} // namespace

extern "C" VKAPI_ATTR PFN_vkVoidFunction VKAPI_CALL vkGetDeviceProcAddr(VkDevice device, const char *name) {
  if (!device || !name) return nullptr;
  return supportedFunction(name);
}

extern "C" VKAPI_ATTR PFN_vkVoidFunction VKAPI_CALL vkGetInstanceProcAddr(VkInstance instance, const char *name) {
  if (!name) return nullptr;
  if (!instance) {
    if (std::strcmp(name, "vkGetInstanceProcAddr") == 0) return reinterpret_cast<PFN_vkVoidFunction>(vkGetInstanceProcAddr);
    if (std::strcmp(name, "vkCreateInstance") == 0) return reinterpret_cast<PFN_vkVoidFunction>(vkCreateInstance);
    if (std::strcmp(name, "vkEnumerateInstanceVersion") == 0) return reinterpret_cast<PFN_vkVoidFunction>(vkEnumerateInstanceVersion);
    if (std::strcmp(name, "vkEnumerateInstanceExtensionProperties") == 0)
      return reinterpret_cast<PFN_vkVoidFunction>(vkEnumerateInstanceExtensionProperties);
    if (std::strcmp(name, "vkEnumerateInstanceLayerProperties") == 0)
      return reinterpret_cast<PFN_vkVoidFunction>(vkEnumerateInstanceLayerProperties);
    return nullptr;
  }
  return supportedFunction(name);
}

PFN_vkVoidFunction gemstoneVulkanDeviceFunction(const char *name, PFN_vkVoidFunction) { return name ? supportedFunction(name) : nullptr; }
PFN_vkVoidFunction gemstoneVulkanInstanceFunction(const char *name, PFN_vkVoidFunction) { return name ? supportedFunction(name) : nullptr; }
PFN_vkQueueWaitIdle gemstoneVulkanSystemQueueWaitIdle() { return vkQueueWaitIdle; }
