#include "state.hpp"

namespace gemstone::vulkan::adapter {
std::mutex stateMutex;
std::mutex rpcExecutionMutex;
std::map<VkDescriptorSetLayout, DescriptorLayout> descriptorLayouts;
std::map<VkBuffer, BoundBuffer> buffers;
std::map<VkDeviceMemory, std::unique_ptr<Memory>> memories;
GemstoneVulkanDescriptorState descriptorSets;
std::map<VkDescriptorPool, DescriptorPoolState> descriptorPools;
std::map<VkShaderModule, std::vector<uint8_t>> modules;
std::map<VkPipeline, gemstone::execution::PreparedShader> pipelines;
std::map<VkCommandBuffer, Command> commands;
std::map<VkCommandPool, CommandPoolState> commandPools;
std::map<VkFence, std::shared_ptr<FenceState>> fences;
std::map<VkDevice, std::shared_ptr<DeviceState>> devices;
std::map<VkQueue, std::shared_ptr<QueueState>> queues;
std::map<VkSemaphore, std::shared_ptr<SemaphoreState>> semaphores;
std::map<VkEvent, bool> events;
std::condition_variable stateChanged;
std::map<VkSampler, gemstone::execution::GraphicsSampler> samplers;
std::map<VkInstance, bool> instanceProperties2;
std::map<VkImage, gemstone::vulkan::Image> images;
std::map<VkFramebuffer, Framebuffer> framebuffers;
std::map<VkRenderPass, RenderPass> renderPasses;
std::map<VkPipeline, GraphicsPipeline> graphicsPipelines;
std::map<VkQueryPool, std::shared_ptr<QueryPool>> queryPools;
std::map<VkDescriptorUpdateTemplate, std::vector<VkDescriptorUpdateTemplateEntry>> descriptorTemplates;
std::map<VkImageView, ImageSlice> imageViews;
std::map<VkBufferView, BufferView> bufferViews;
namespace {
struct QueueCleanup {
  ~QueueCleanup() {
    for (auto &[handle, device] : devices) {
      (void)handle;
      stopQueues(device);
    }
  }
} queueCleanup;
} // namespace
} // namespace gemstone::vulkan::adapter
