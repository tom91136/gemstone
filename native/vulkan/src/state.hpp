#pragma once

#include <array>
#include <condition_variable>
#include <cstring>
#include <deque>
#include <map>
#include <memory>
#include <mutex>
#include <optional>
#include <span>
#include <thread>
#include <unordered_set>
#include <vector>

#include "commands.hpp"
#include "execution.hpp"
#include "handles.hpp"
#include "physical_device.hpp"

namespace gemstone::vulkan::adapter {

using namespace gemstone::execution;
using namespace gemstone::vulkan;

namespace host = gemstone::vulkan::host;

struct BoundBuffer {
  VkDeviceMemory memory = VK_NULL_HANDLE;
  VkDeviceSize offset = 0;
  VkDeviceSize size = 0;
};

struct Memory {
  struct Deleter {
    void operator()(uint8_t *data) const { ::operator delete(data, std::align_val_t{memoryMapAlignment}); }
  };
  std::unique_ptr<uint8_t, Deleter> allocation;
  std::span<uint8_t> bytes;

  static uint8_t *allocateBacking(size_t size) {
    host::Scope deviceStorage(nullptr);
    return static_cast<uint8_t *>(::operator new(size, std::align_val_t{memoryMapAlignment}));
  }
  explicit Memory(size_t size) : allocation(allocateBacking(size)), bytes(allocation.get(), size) {
    std::memset(bytes.data(), 0, bytes.size());
  }
};

namespace recorded = gemstone::vulkan::recorded;
using recorded::Copy;
using recorded::Dispatch;
using recorded::Operation;
struct Execution;
bool execute(Execution &execution, const Operation &operation);

struct Command {
  gemstone::vulkan::GraphicsCommandState graphics;
  VkPipeline pipeline = VK_NULL_HANDLE;
  std::array<VkDescriptorSet, MaxBoundDescriptorSets> descriptors{};
  std::vector<Operation> operations;
  std::array<uint8_t, execution::GraphicsPushConstantBytes> pushConstants{};
  std::map<GemstoneVulkanDescriptorSlot, uint32_t> dynamicOffsets{};
  bool recording = false;
  bool allocationFailed = false;
};

struct DeviceState;
struct FenceState {
  bool signaled = false;
  std::weak_ptr<DeviceState> device;
};
struct SemaphoreState {
  VkSemaphoreType type = VK_SEMAPHORE_TYPE_BINARY;
  uint64_t value = 0;
  std::weak_ptr<DeviceState> device;
};
struct SemaphorePoint {
  std::shared_ptr<SemaphoreState> semaphore;
  uint64_t value;
};
struct Submission {
  std::vector<Operation> operations;
  std::vector<SemaphorePoint> waits, signals;
  std::shared_ptr<FenceState> fence;
};
struct QueueState {
  VkQueue handle = VK_NULL_HANDLE;
  std::weak_ptr<DeviceState> device;
  std::deque<Submission> submissions;
  bool active = false;
  std::thread worker;
};
struct DeviceState {
  bool stopping = false, lost = false;
  bool hostQueryResetExtension = false;
  std::vector<std::shared_ptr<QueueState>> queues;
};
struct DescriptorPoolState {
  uint32_t maxSets = 0;
  std::unordered_set<VkDescriptorSet> sets;
};

struct CommandPoolState {
  std::unordered_set<VkCommandBuffer> buffers;
};

extern std::mutex stateMutex;
// Address registrations are temporary to one execution. Share the existing compute
// serialization domain with graphics so overlapping native allocations cannot be
// registered twice by different queues. No execution waits while holding this lock
// may acquire stateMutex.
extern std::mutex rpcExecutionMutex;
struct DescriptorLayout : std::vector<VkDescriptorSetLayoutBinding> {
  std::map<std::pair<uint32_t, uint32_t>, gemstone::execution::GraphicsSampler> immutableSamplers;
};
extern std::map<VkDescriptorSetLayout, DescriptorLayout> descriptorLayouts;
extern std::map<VkBuffer, BoundBuffer> buffers;
extern std::map<VkDeviceMemory, std::unique_ptr<Memory>> memories;
extern GemstoneVulkanDescriptorState descriptorSets;
extern std::map<VkDescriptorPool, DescriptorPoolState> descriptorPools;
extern std::map<VkShaderModule, std::vector<uint8_t>> modules;
extern std::map<VkPipeline, gemstone::execution::PreparedShader> pipelines;
extern std::map<VkCommandBuffer, Command> commands;
extern std::map<VkCommandPool, CommandPoolState> commandPools;
extern std::map<VkFence, std::shared_ptr<FenceState>> fences;
extern std::map<VkDevice, std::shared_ptr<DeviceState>> devices;
extern std::map<VkQueue, std::shared_ptr<QueueState>> queues;
extern std::map<VkSemaphore, std::shared_ptr<SemaphoreState>> semaphores;
extern std::map<VkEvent, bool> events;
extern std::condition_variable stateChanged;
extern std::map<VkSampler, gemstone::execution::GraphicsSampler> samplers;
extern std::map<VkInstance, bool> instanceProperties2;
inline VkPhysicalDevice physicalDeviceHandle(VkInstance instance) { return reinterpret_cast<VkPhysicalDevice>(instance); }

extern std::map<VkImage, gemstone::vulkan::Image> images;

struct ImageSlice {
  VkImage image;
  uint32_t baseLayer, level = 0, levels = 1;
  VkFormat format = VK_FORMAT_UNDEFINED;
  uint32_t layers = 1;
  VkImageAspectFlags aspects = 0;
  std::array<VkComponentSwizzle, 4> components{};
  bool depthSlices = false;
};

struct BufferView {
  VkBuffer buffer;
  VkFormat format;
  VkDeviceSize offset, range;
};
extern std::map<VkFramebuffer, Framebuffer> framebuffers;
extern std::map<VkRenderPass, RenderPass> renderPasses;
extern std::map<VkPipeline, GraphicsPipeline> graphicsPipelines;

struct QueryPool {
  struct Slot {
    uint64_t value = 0;
    bool available = false;
  };
  VkQueryType type;
  std::vector<Slot> slots;
  std::weak_ptr<DeviceState> device;
};
extern std::map<VkQueryPool, std::shared_ptr<QueryPool>> queryPools;
extern std::map<VkDescriptorUpdateTemplate, std::vector<VkDescriptorUpdateTemplateEntry>> descriptorTemplates;
extern std::map<VkImageView, ImageSlice> imageViews;
extern std::map<VkBufferView, BufferView> bufferViews;

VkResult createQueues(uint32_t count, VkDevice *handle);
void stopQueues(const std::shared_ptr<DeviceState> &device);
bool resetQueryPool(VkQueryPool pool, uint32_t first, uint32_t count);
bool deviceFailed(const std::weak_ptr<DeviceState> &reference);
} // namespace gemstone::vulkan::adapter
