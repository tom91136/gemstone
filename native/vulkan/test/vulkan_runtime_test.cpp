#include <algorithm>
#include <cstdlib>
#include <cstring>
#include <filesystem>
#include <iterator>
#include <map>
#include <optional>
#include <string>
#include <thread>
#include <vector>

#include <link.h>

#include <catch2/catch_test_macros.hpp>
#include <catch2/generators/catch_generators.hpp>

#include "gemstone_image_formats.hpp"
#include <gemstone/command_queue.hpp>

#include "../../rpc/test/rpc_fixture.hpp"
#include "../src/descriptor.hpp"
#include "../src/shader.hpp"
#include <environment_keys.hpp>
#include <vulkan/vulkan.h>

using gemstone::vulkan::descriptorAllocationLayout;
using gemstone::vulkan::DescriptorRange;
using gemstone::vulkan::DescriptorState;
using gemstone::vulkan::rangeLength;

namespace {
struct TestDevice {
  VkInstance instance = VK_NULL_HANDLE;
  VkDevice device = VK_NULL_HANDLE;
  VkQueue queue = VK_NULL_HANDLE, other = VK_NULL_HANDLE;
  TestDevice() {
    VkInstanceCreateInfo instanceInfo{};
    instanceInfo.sType = VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO;
    REQUIRE(vkCreateInstance(&instanceInfo, nullptr, &instance) == VK_SUCCESS);
    uint32_t count = 1;
    VkPhysicalDevice physical;
    REQUIRE(vkEnumeratePhysicalDevices(instance, &count, &physical) == VK_SUCCESS);
    const float priorities[]{0, 1};
    VkDeviceQueueCreateInfo queueInfo{};
    queueInfo.sType = VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO;
    queueInfo.queueCount = 2;
    queueInfo.pQueuePriorities = priorities;
    VkDeviceCreateInfo info{};
    info.sType = VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO;
    info.queueCreateInfoCount = 1;
    info.pQueueCreateInfos = &queueInfo;
    REQUIRE(vkCreateDevice(physical, &info, nullptr, &device) == VK_SUCCESS);
    vkGetDeviceQueue(device, 0, 0, &queue);
    vkGetDeviceQueue(device, 0, 1, &other);
    REQUIRE(queue != VK_NULL_HANDLE);
    REQUIRE(other != VK_NULL_HANDLE);
    REQUIRE(queue != other);
  }
  ~TestDevice() {
    vkDestroyDevice(device, nullptr);
    vkDestroyInstance(instance, nullptr);
  }
};
// A primary command recording for execution tests. Tests retain their own image,
// memory and render-pass descriptions so the layout under test stays explicit.
struct CommandRecording {
  VkDevice device;
  VkCommandPool pool{};
  VkCommandBuffer command{};
  explicit CommandRecording(VkDevice device) : device(device) {
    VkCommandPoolCreateInfo poolInfo{};
    poolInfo.sType = VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO;
    REQUIRE(vkCreateCommandPool(device, &poolInfo, nullptr, &pool) == VK_SUCCESS);
    VkCommandBufferAllocateInfo info{};
    info.sType = VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO;
    info.commandPool = pool;
    info.commandBufferCount = 1;
    REQUIRE(vkAllocateCommandBuffers(device, &info, &command) == VK_SUCCESS);
    VkCommandBufferBeginInfo begin{};
    begin.sType = VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO;
    REQUIRE(vkBeginCommandBuffer(command, &begin) == VK_SUCCESS);
  }
  CommandRecording(const CommandRecording &) = delete;
  CommandRecording &operator=(const CommandRecording &) = delete;
  ~CommandRecording() { vkDestroyCommandPool(device, pool, nullptr); }
  VkCommandBuffer secondary() const {
    VkCommandBufferAllocateInfo info{};
    info.sType = VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO;
    info.commandPool = pool;
    info.level = VK_COMMAND_BUFFER_LEVEL_SECONDARY;
    info.commandBufferCount = 1;
    VkCommandBuffer result{};
    REQUIRE(vkAllocateCommandBuffers(device, &info, &result) == VK_SUCCESS);
    return result;
  }
};
VkSubmitInfo submitInfo(const VkCommandBuffer &command) {
  VkSubmitInfo submit{};
  submit.sType = VK_STRUCTURE_TYPE_SUBMIT_INFO;
  submit.commandBufferCount = 1;
  submit.pCommandBuffers = &command;
  return submit;
}
VkResult submitAndWait(VkQueue queue, VkCommandBuffer command, VkFence fence = VK_NULL_HANDLE) {
  const auto submit = submitInfo(command);
  REQUIRE(vkQueueSubmit(queue, 1, &submit, fence) == VK_SUCCESS);
  return vkQueueWaitIdle(queue);
}
void allocateDescriptors(DescriptorState &state, VkDescriptorSet set, VkDescriptorType type,
                         std::initializer_list<std::pair<uint32_t, uint32_t>> counts) {
  std::vector<VkDescriptorSetLayoutBinding> bindings;
  for (const auto &[binding, count] : counts)
    bindings.push_back({binding, type, count, VK_SHADER_STAGE_COMPUTE_BIT, nullptr});
  state.sets.try_emplace(set, bindings);
}
int collectVulkanLibraries(dl_phdr_info *info, size_t, void *data) {
  if (info->dlpi_name && std::strstr(info->dlpi_name, "libvulkan.so"))
    static_cast<std::vector<std::string> *>(data)->emplace_back(info->dlpi_name);
  return 0;
}
} // namespace

struct DiscardPipeline {
  TestDevice &runtime;
  VkShaderModule module{};
  VkRenderPass pass{};
  VkFramebuffer framebuffer{};
  VkPipelineLayout layout{};
  VkPipeline pipeline{};
  explicit DiscardPipeline(TestDevice &device) : runtime(device) {
    // Valid empty vertex entry point; gl_Position is deliberately absent.
    const uint32_t shader[]{0x07230203,
                            0x00010000,
                            0,
                            5,
                            0,
                            (2u << 16) | 17u,
                            1,
                            (3u << 16) | 14u,
                            0,
                            1,
                            (5u << 16) | 15u,
                            0,
                            3,
                            0x6e69616d,
                            0,
                            (2u << 16) | 19u,
                            1,
                            (3u << 16) | 33u,
                            2,
                            1,
                            (5u << 16) | 54u,
                            1,
                            3,
                            0,
                            2,
                            (2u << 16) | 248u,
                            4,
                            (1u << 16) | 253u,
                            (1u << 16) | 56u};
    VkShaderModuleCreateInfo moduleInfo{};
    moduleInfo.sType = VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO;
    moduleInfo.codeSize = sizeof(shader);
    moduleInfo.pCode = shader;
    REQUIRE(vkCreateShaderModule(runtime.device, &moduleInfo, nullptr, &module) == VK_SUCCESS);
    VkSubpassDescription subpass{};
    subpass.pipelineBindPoint = VK_PIPELINE_BIND_POINT_GRAPHICS;
    VkRenderPassCreateInfo passInfo{};
    passInfo.sType = VK_STRUCTURE_TYPE_RENDER_PASS_CREATE_INFO;
    passInfo.subpassCount = 1;
    passInfo.pSubpasses = &subpass;
    REQUIRE(vkCreateRenderPass(runtime.device, &passInfo, nullptr, &pass) == VK_SUCCESS);
    VkFramebufferCreateInfo fbInfo{};
    fbInfo.sType = VK_STRUCTURE_TYPE_FRAMEBUFFER_CREATE_INFO;
    fbInfo.renderPass = pass;
    fbInfo.width = fbInfo.height = fbInfo.layers = 1;
    REQUIRE(vkCreateFramebuffer(runtime.device, &fbInfo, nullptr, &framebuffer) == VK_SUCCESS);
    VkPipelineLayoutCreateInfo layoutInfo{};
    layoutInfo.sType = VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO;
    REQUIRE(vkCreatePipelineLayout(runtime.device, &layoutInfo, nullptr, &layout) == VK_SUCCESS);
    VkPipelineShaderStageCreateInfo stage{};
    stage.sType = VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO;
    stage.stage = VK_SHADER_STAGE_VERTEX_BIT;
    stage.module = module;
    stage.pName = "main";
    VkPipelineInputAssemblyStateCreateInfo assembly{};
    assembly.sType = VK_STRUCTURE_TYPE_PIPELINE_INPUT_ASSEMBLY_STATE_CREATE_INFO;
    assembly.topology = VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST;
    VkPipelineRasterizationStateCreateInfo raster{};
    raster.sType = VK_STRUCTURE_TYPE_PIPELINE_RASTERIZATION_STATE_CREATE_INFO;
    raster.rasterizerDiscardEnable = VK_TRUE;
    VkPipelineVertexInputStateCreateInfo input{};
    input.sType = VK_STRUCTURE_TYPE_PIPELINE_VERTEX_INPUT_STATE_CREATE_INFO;
    // Unused bindings require no buffer, even when described in the pipeline.
    const VkVertexInputBindingDescription unusedBinding{7, 16, VK_VERTEX_INPUT_RATE_VERTEX};
    const VkVertexInputAttributeDescription unusedAttribute{7, 7, VK_FORMAT_R32G32B32A32_SFLOAT, 0};
    input.vertexBindingDescriptionCount = 1;
    input.pVertexBindingDescriptions = &unusedBinding;
    input.vertexAttributeDescriptionCount = 1;
    input.pVertexAttributeDescriptions = &unusedAttribute;
    const VkDynamicState dynamicStates[]{VK_DYNAMIC_STATE_VIEWPORT, VK_DYNAMIC_STATE_SCISSOR};
    VkPipelineDynamicStateCreateInfo dynamic{};
    dynamic.sType = VK_STRUCTURE_TYPE_PIPELINE_DYNAMIC_STATE_CREATE_INFO;
    dynamic.dynamicStateCount = 2;
    dynamic.pDynamicStates = dynamicStates;
    VkGraphicsPipelineCreateInfo pipelineInfo{};
    pipelineInfo.sType = VK_STRUCTURE_TYPE_GRAPHICS_PIPELINE_CREATE_INFO;
    pipelineInfo.stageCount = 1;
    pipelineInfo.pStages = &stage;
    pipelineInfo.pVertexInputState = &input;
    pipelineInfo.pInputAssemblyState = &assembly;
    pipelineInfo.pRasterizationState = &raster;
    pipelineInfo.pDynamicState = &dynamic;
    pipelineInfo.layout = layout;
    pipelineInfo.renderPass = pass;
    // Viewport, multisample, depth/stencil, blend and fragment shader state are absent.
    REQUIRE(vkCreateGraphicsPipelines(runtime.device, VK_NULL_HANDLE, 1, &pipelineInfo, nullptr, &pipeline) == VK_SUCCESS);
  }
  ~DiscardPipeline() {
    vkDestroyPipeline(runtime.device, pipeline, nullptr);
    vkDestroyPipelineLayout(runtime.device, layout, nullptr);
    vkDestroyFramebuffer(runtime.device, framebuffer, nullptr);
    vkDestroyRenderPass(runtime.device, pass, nullptr);
    vkDestroyShaderModule(runtime.device, module, nullptr);
  }
  void begin(VkCommandBuffer command, VkSubpassContents contents = VK_SUBPASS_CONTENTS_INLINE) {
    VkRenderPassBeginInfo render{};
    render.sType = VK_STRUCTURE_TYPE_RENDER_PASS_BEGIN_INFO;
    render.renderPass = pass;
    render.framebuffer = framebuffer;
    render.renderArea.extent = {1, 1};
    vkCmdBeginRenderPass(command, &render, contents);
    if (contents == VK_SUBPASS_CONTENTS_INLINE) vkCmdBindPipeline(command, VK_PIPELINE_BIND_POINT_GRAPHICS, pipeline);
  }
};

TEST_CASE("Vulkan shader bindings preserve image subviews and reject escaping arguments") {
  using namespace gemstone::vulkan;
  using namespace gemstone::execution;
  std::array<uint8_t, 32> source{}, target{};
  StageResources stage;
  stage.allocations.push_back({source.data(), source.size(), {}});
  stage.allocations.push_back({target.data(), target.size(), {}});
  stage.arguments = {StageResources::Argument{0, 4, 12}, StageResources::Argument{0, 8, 8}, StageResources::Argument{1, 0, 32}};
  DescriptorBinding image{};
  image.argument.value = 1;
  image.descriptor = GraphicsDescriptorImage{};
  stage.descriptorBindings.push_back(image);
  // Registration may return subviews in one shared backing allocation.
  const auto registerMemory = [&](const StageResources::Allocation &allocation) {
    return GraphicsRange{AllocationId{9}, allocation.data == source.data() ? 64 : 128, static_cast<int64_t>(allocation.size)};
  };
  const auto bound = bindStageResources(stage, registerMemory);
  REQUIRE(bound.has_value());
  REQUIRE(bound->arguments.size() == 3);
  REQUIRE(bound->arguments[0]->allocation.value == bound->arguments[1]->allocation.value);
  REQUIRE(bound->arguments[0]->offset.value == 68);
  REQUIRE(bound->arguments[0]->size.value == 12);
  REQUIRE(bound->arguments[1]->offset.value == 72);
  REQUIRE(bound->arguments[2]->offset.value == 128);
  REQUIRE(bound->descriptorBindings.size() == 1);
  REQUIRE(std::get<GraphicsDescriptorImage>(bound->descriptorBindings[0].descriptor).source.memory.allocation.value == 9);
  REQUIRE(std::get<GraphicsDescriptorImage>(bound->descriptorBindings[0].descriptor).source.memory.offset.value == 72);
  REQUIRE(std::get<GraphicsDescriptorImage>(bound->descriptorBindings[0].descriptor).source.memory.size.value == 8);
  stage.arguments[0]->size = source.size();
  REQUIRE_FALSE(bindStageResources(stage, registerMemory));
  stage.arguments[0]->size = 12;
  stage.descriptorBindings[0].argument.value = -1;
  REQUIRE_FALSE(bindStageResources(stage, registerMemory));
  stage.descriptorBindings[0].argument.value = stage.arguments.size();
  REQUIRE_FALSE(bindStageResources(stage, registerMemory));
}

TEST_CASE("Vulkan standalone samplers require no native storage registration") {
  using namespace gemstone::vulkan;
  using namespace gemstone::execution;
  StageResources stage;
  stage.arguments.emplace_back(std::nullopt);
  DescriptorBinding sampler{};
  sampler.descriptor = GraphicsDescriptorSampler{};
  std::get<GraphicsDescriptorSampler>(sampler.descriptor).value.mipLodBias = -0.75f;
  stage.descriptorBindings.push_back(sampler);
  const auto registerMemory = [](const StageResources::Allocation &) -> GraphicsRange {
    FAIL("sampler preparation must not register native storage");
    return {};
  };
  const auto bound = bindStageResources(stage, registerMemory);
  REQUIRE(bound.has_value());
  REQUIRE(bound->arguments.size() == 1);
  REQUIRE_FALSE(bound->arguments.front());
  REQUIRE(std::get<GraphicsDescriptorSampler>(bound->descriptorBindings.front().descriptor).value.mipLodBias == -0.75f);
  stage.descriptorBindings.clear();
  REQUIRE_FALSE(bindStageResources(stage, registerMemory));
}

TEST_CASE("Vulkan feature queries and device requests agree across a chained payload", "[vulkan][device]") {
  TestDevice runtime;
  uint32_t count = 1;
  VkPhysicalDevice physical{};
  REQUIRE(vkEnumeratePhysicalDevices(runtime.instance, &count, &physical) == VK_SUCCESS);
  VkPhysicalDeviceProtectedMemoryFeatures protection{};
  protection.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PROTECTED_MEMORY_FEATURES;
  VkPhysicalDeviceShaderDrawParametersFeatures draw{};
  draw.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_DRAW_PARAMETERS_FEATURES;
  VkPhysicalDeviceTimelineSemaphoreFeatures timeline{};
  timeline.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TIMELINE_SEMAPHORE_FEATURES;
  protection.pNext = &draw;
  draw.pNext = &timeline;
  VkPhysicalDeviceFeatures2 features{};
  features.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2;
  features.pNext = &protection;
  vkGetPhysicalDeviceFeatures2(physical, &features);
  REQUIRE(protection.pNext == &draw);
  REQUIRE(draw.pNext == &timeline);
  REQUIRE(timeline.pNext == nullptr);
  REQUIRE(protection.protectedMemory == VK_FALSE);
  REQUIRE(draw.shaderDrawParameters == VK_FALSE);
  REQUIRE(timeline.timelineSemaphore == VK_TRUE);
  const float priority = 1;
  VkDeviceQueueCreateInfo queue{};
  queue.sType = VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO;
  queue.queueCount = 1;
  queue.pQueuePriorities = &priority;
  VkDeviceCreateInfo info{};
  info.sType = VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO;
  info.pNext = &features;
  info.queueCreateInfoCount = 1;
  info.pQueueCreateInfos = &queue;
  VkDevice device{};
  REQUIRE(vkCreateDevice(physical, &info, nullptr, &device) == VK_SUCCESS);
  vkDestroyDevice(device, nullptr);
  for (auto *unsupported : {&protection.protectedMemory, &draw.shaderDrawParameters}) {
    *unsupported = VK_TRUE;
    REQUIRE(vkCreateDevice(physical, &info, nullptr, &device) == VK_ERROR_FEATURE_NOT_PRESENT);
    *unsupported = VK_FALSE;
  }
}

TEST_CASE("Vulkan narrow storage and 1.2 feature aliases agree at device creation", "[vulkan][device]") {
  TestDevice runtime;
  uint32_t count = 1;
  VkPhysicalDevice physical{};
  REQUIRE(vkEnumeratePhysicalDevices(runtime.instance, &count, &physical) == VK_SUCCESS);
  VkPhysicalDevice16BitStorageFeatures storage{};
  storage.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_16BIT_STORAGE_FEATURES;
  VkPhysicalDeviceUniformBufferStandardLayoutFeatures layout{};
  layout.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_UNIFORM_BUFFER_STANDARD_LAYOUT_FEATURES;
  VkPhysicalDeviceImagelessFramebufferFeatures imageless{};
  imageless.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_IMAGELESS_FRAMEBUFFER_FEATURES;
  storage.pNext = &layout;
  layout.pNext = &imageless;
  VkPhysicalDeviceFeatures2 root{};
  root.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2;
  root.pNext = &storage;
  vkGetPhysicalDeviceFeatures2(physical, &root);
  VkPhysicalDeviceVulkan11Features v11{};
  v11.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_1_FEATURES;
  VkPhysicalDeviceVulkan12Features v12{};
  v12.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_2_FEATURES;
  v11.pNext = &v12;
  root.pNext = &v11;
  vkGetPhysicalDeviceFeatures2(physical, &root);
  REQUIRE(storage.storageBuffer16BitAccess == VK_TRUE);
  REQUIRE(storage.uniformAndStorageBuffer16BitAccess == VK_TRUE);
  REQUIRE(v11.storageBuffer16BitAccess == storage.storageBuffer16BitAccess);
  REQUIRE(v11.uniformAndStorageBuffer16BitAccess == storage.uniformAndStorageBuffer16BitAccess);
  REQUIRE(v12.uniformBufferStandardLayout == layout.uniformBufferStandardLayout);
  REQUIRE(layout.uniformBufferStandardLayout == VK_TRUE);
  REQUIRE(v12.imagelessFramebuffer == imageless.imagelessFramebuffer);
  REQUIRE(imageless.imagelessFramebuffer == VK_TRUE);

  REQUIRE(vkEnumerateDeviceExtensionProperties(physical, nullptr, &count, nullptr) == VK_SUCCESS);
  std::vector<VkExtensionProperties> extensions(count);
  REQUIRE(vkEnumerateDeviceExtensionProperties(physical, nullptr, &count, extensions.data()) == VK_SUCCESS);
  for (const auto *name : {VK_KHR_16BIT_STORAGE_EXTENSION_NAME, VK_KHR_STORAGE_BUFFER_STORAGE_CLASS_EXTENSION_NAME})
    REQUIRE(std::any_of(extensions.begin(), extensions.end(), [=](const auto &ext) { return std::strcmp(name, ext.extensionName) == 0; }));
  const float priority = 1;
  VkDeviceQueueCreateInfo queue{};
  queue.sType = VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO;
  queue.queueCount = 1;
  queue.pQueuePriorities = &priority;
  const char *names[]{VK_KHR_16BIT_STORAGE_EXTENSION_NAME, VK_KHR_STORAGE_BUFFER_STORAGE_CLASS_EXTENSION_NAME};
  VkDeviceCreateInfo info{};
  info.sType = VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO;
  info.queueCreateInfoCount = 1;
  info.pQueueCreateInfos = &queue;
  info.enabledExtensionCount = 2;
  info.ppEnabledExtensionNames = names;
  for (const void *features : {static_cast<void *>(&storage), static_cast<void *>(&v11)}) {
    info.pNext = features;
    VkDevice device{};
    REQUIRE(vkCreateDevice(physical, &info, nullptr, &device) == VK_SUCCESS);
    // Enabling an unrelated extension must not enable host-query-reset lookup.
    REQUIRE(vkGetDeviceProcAddr(device, "vkResetQueryPoolEXT") == nullptr);
    vkDestroyDevice(device, nullptr);
  }
  storage.storageInputOutput16 = VK_TRUE;
  info.pNext = &storage;
  VkDevice rejected{};
  REQUIRE(vkCreateDevice(physical, &info, nullptr, &rejected) == VK_ERROR_FEATURE_NOT_PRESENT);
}

TEST_CASE("Vulkan procedure lookup respects command scope and enabled extensions", "[vulkan][lookup]") {
  TestDevice runtime;
  for (const auto *name : {"vkGetPhysicalDeviceFeatures", "vkCreateDevice", "vkDestroyInstance", "vkEnumerateDeviceExtensionProperties",
                           "vkBindBufferMemory2KHR", "vkResetQueryPoolEXT", "vkGetPhysicalDeviceFeatures2KHR"})
    REQUIRE(vkGetDeviceProcAddr(runtime.device, name) == nullptr);
  for (const auto *name : {"vkQueueBindSparse",
                           "vkGetDeviceMemoryCommitment",
                           "vkAllocateMemory",
                           "vkFreeMemory",
                           "vkMapMemory",
                           "vkUnmapMemory",
                           "vkFlushMappedMemoryRanges",
                           "vkInvalidateMappedMemoryRanges",
                           "vkBindBufferMemory",
                           "vkBindImageMemory",
                           "vkGetBufferMemoryRequirements",
                           "vkGetImageMemoryRequirements",
                           "vkCreateBuffer",
                           "vkDestroyBuffer",
                           "vkCreateImage",
                           "vkDestroyImage",
                           "vkCmdCopyBuffer",
                           "vkGetBufferMemoryRequirements2",
                           "vkGetImageMemoryRequirements2",
                           "vkBindBufferMemory2",
                           "vkBindImageMemory2"}) {
    REQUIRE(vkGetDeviceProcAddr(runtime.device, name) != nullptr);
    REQUIRE(vkGetInstanceProcAddr(runtime.instance, name) != nullptr);
  }
  REQUIRE(vkGetInstanceProcAddr(runtime.instance, "vkGetPhysicalDeviceFeatures2KHR") == nullptr);
  const char *extension = VK_KHR_GET_PHYSICAL_DEVICE_PROPERTIES_2_EXTENSION_NAME;
  VkInstanceCreateInfo info{};
  info.sType = VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO;
  info.enabledExtensionCount = 1;
  info.ppEnabledExtensionNames = &extension;
  VkInstance extended{};
  REQUIRE(vkCreateInstance(&info, nullptr, &extended) == VK_SUCCESS);
  REQUIRE(vkGetInstanceProcAddr(extended, "vkGetPhysicalDeviceFeatures2KHR") != nullptr);
  REQUIRE(vkGetInstanceProcAddr(extended, "vkBindBufferMemory2KHR") == nullptr);
  vkDestroyInstance(extended, nullptr);
}

TEST_CASE("Vulkan host query reset extension is enumerated and scoped to enabled devices", "[vulkan][query][lookup]") {
  TestDevice runtime;
  uint32_t count = 1;
  VkPhysicalDevice physical{};
  REQUIRE(vkEnumeratePhysicalDevices(runtime.instance, &count, &physical) == VK_SUCCESS);
  count = 0;
  REQUIRE(vkEnumerateDeviceExtensionProperties(physical, nullptr, &count, nullptr) == VK_SUCCESS);
  REQUIRE(count >= 1);
  count = 1;
  VkExtensionProperties extension{};
  REQUIRE(vkEnumerateDeviceExtensionProperties(physical, nullptr, &count, &extension) == VK_INCOMPLETE);
  REQUIRE(std::string(extension.extensionName) == VK_EXT_HOST_QUERY_RESET_EXTENSION_NAME);
  REQUIRE(extension.specVersion == VK_EXT_HOST_QUERY_RESET_SPEC_VERSION);
  REQUIRE(vkGetInstanceProcAddr(runtime.instance, "vkResetQueryPoolEXT") != nullptr);
  REQUIRE(vkGetDeviceProcAddr(runtime.device, "vkResetQueryPoolEXT") == nullptr);

  const float priority = 1;
  VkDeviceQueueCreateInfo queue{};
  queue.sType = VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO;
  queue.queueCount = 1;
  queue.pQueuePriorities = &priority;
  VkPhysicalDeviceHostQueryResetFeatures feature{};
  feature.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_HOST_QUERY_RESET_FEATURES;
  feature.hostQueryReset = VK_TRUE;
  const char *extensions[]{VK_EXT_HOST_QUERY_RESET_EXTENSION_NAME, VK_EXT_HOST_QUERY_RESET_EXTENSION_NAME};
  VkDeviceCreateInfo info{};
  info.sType = VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO;
  info.pNext = &feature;
  info.queueCreateInfoCount = 1;
  info.pQueueCreateInfos = &queue;
  info.enabledExtensionCount = 2; // Duplicate names still enable the extension once.
  info.ppEnabledExtensionNames = extensions;
  VkDevice enabled{};
  REQUIRE(vkCreateDevice(physical, &info, nullptr, &enabled) == VK_SUCCESS);
  auto reset = reinterpret_cast<PFN_vkResetQueryPoolEXT>(vkGetDeviceProcAddr(enabled, "vkResetQueryPoolEXT"));
  REQUIRE(reset != nullptr);
  REQUIRE(vkGetDeviceProcAddr(runtime.device, "vkResetQueryPoolEXT") == nullptr);
  VkQueryPoolCreateInfo queryInfo{};
  queryInfo.sType = VK_STRUCTURE_TYPE_QUERY_POOL_CREATE_INFO;
  queryInfo.queryType = VK_QUERY_TYPE_OCCLUSION;
  queryInfo.queryCount = 1;
  VkQueryPool pool{};
  REQUIRE(vkCreateQueryPool(enabled, &queryInfo, nullptr, &pool) == VK_SUCCESS);
  reset(enabled, pool, 0, 1);
  uint64_t result[]{0xfeedface, 123};
  REQUIRE(vkGetQueryPoolResults(enabled, pool, 0, 1, sizeof(result), result, sizeof(result),
                                VK_QUERY_RESULT_64_BIT | VK_QUERY_RESULT_WITH_AVAILABILITY_BIT)
          == VK_NOT_READY);
  REQUIRE(result[0] == 0xfeedface);
  REQUIRE(result[1] == 0);
  vkDestroyQueryPool(enabled, pool, nullptr);
  vkDestroyDevice(enabled, nullptr);
  REQUIRE(vkGetDeviceProcAddr(runtime.device, "vkResetQueryPoolEXT") == nullptr);
  extensions[0] = "VK_EXT_gemstone_unknown";
  info.enabledExtensionCount = 1;
  REQUIRE(vkCreateDevice(physical, &info, nullptr, &enabled) == VK_ERROR_EXTENSION_NOT_PRESENT);
}

TEST_CASE("Vulkan API facade keeps explicit unsupported and lookup paths") {
  REQUIRE(vkCreateSampler(VK_NULL_HANDLE, nullptr, nullptr, nullptr) == VK_ERROR_INITIALIZATION_FAILED);
  REQUIRE(vkGetInstanceProcAddr(nullptr, "vkGetInstanceProcAddr") != nullptr);
  REQUIRE(vkGetInstanceProcAddr(nullptr, "vkCreateInstance") != nullptr);
  REQUIRE(vkGetInstanceProcAddr(nullptr, "vkGemstoneUnknown") == nullptr);
  REQUIRE(vkGetInstanceProcAddr(nullptr, "vkCreateBuffer") == nullptr);
  REQUIRE(vkGetInstanceProcAddr(nullptr, "vkGetDeviceProcAddr") == nullptr);
}

TEST_CASE("Vulkan explicit weighted-average sampler reduction needs no optional feature") {
  TestDevice runtime;
  VkSamplerReductionModeCreateInfo reduction{};
  reduction.sType = VK_STRUCTURE_TYPE_SAMPLER_REDUCTION_MODE_CREATE_INFO;
  reduction.reductionMode = VK_SAMPLER_REDUCTION_MODE_WEIGHTED_AVERAGE;
  VkSamplerCreateInfo info{};
  info.sType = VK_STRUCTURE_TYPE_SAMPLER_CREATE_INFO;
  info.pNext = &reduction;
  VkSampler sampler = VK_NULL_HANDLE;
  REQUIRE(vkCreateSampler(runtime.device, &info, nullptr, &sampler) == VK_SUCCESS);
  REQUIRE(sampler != VK_NULL_HANDLE);
  vkDestroySampler(runtime.device, sampler, nullptr);
  for (auto mode : {VK_SAMPLER_REDUCTION_MODE_MIN, VK_SAMPLER_REDUCTION_MODE_MAX}) {
    reduction.reductionMode = mode;
    REQUIRE(vkCreateSampler(runtime.device, &info, nullptr, &sampler) == VK_ERROR_FEATURE_NOT_PRESENT);
  }
}

TEST_CASE("Vulkan instance creation is self contained") {
  VkInstanceCreateInfo info{};
  info.sType = VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO;
  VkInstance instance = VK_NULL_HANDLE;
  REQUIRE(vkCreateInstance(&info, nullptr, &instance) == VK_SUCCESS);
  REQUIRE(instance != VK_NULL_HANDLE);
  uint32_t count = 0;
  REQUIRE(vkEnumeratePhysicalDevices(instance, &count, nullptr) == VK_SUCCESS);
  if (count > 0) {
    std::vector<VkPhysicalDevice> devices(count);
    REQUIRE(vkEnumeratePhysicalDevices(instance, &count, devices.data()) == VK_SUCCESS);
    uint32_t queueCount = 0;
    vkGetPhysicalDeviceQueueFamilyProperties(devices[0], &queueCount, nullptr);
    REQUIRE(queueCount > 0);
    const float priority = 1;
    VkDeviceQueueCreateInfo queue{};
    queue.sType = VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO;
    queue.queueFamilyIndex = 0;
    queue.queueCount = 1;
    queue.pQueuePriorities = &priority;
    VkDeviceCreateInfo deviceInfo{};
    deviceInfo.sType = VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO;
    deviceInfo.queueCreateInfoCount = 1;
    deviceInfo.pQueueCreateInfos = &queue;
    VkDevice device = VK_NULL_HANDLE;
    REQUIRE(vkCreateDevice(devices[0], &deviceInfo, nullptr, &device) == VK_SUCCESS);
    vkDestroyDevice(device, nullptr);
  }

  std::vector<std::string> vulkanLibraries;
  dl_iterate_phdr(collectVulkanLibraries, &vulkanLibraries);
  for (const auto &library : vulkanLibraries)
    INFO(library);
  REQUIRE(vulkanLibraries.size() == 1);
  vkDestroyInstance(instance, nullptr);
}

TEST_CASE("Vulkan API target keeps instance and device versions consistent", "[graphics-execution]") {
  struct RestoreVersion {
    std::optional<std::string> previous;
    RestoreVersion() {
      if (const char *value = std::getenv(gemstone::environment::VulkanApiVersion)) previous = value;
    }
    ~RestoreVersion() {
      if (previous) setenv(gemstone::environment::VulkanApiVersion, previous->c_str(), 1);
      else unsetenv(gemstone::environment::VulkanApiVersion);
    }
  } restore;
  for (const auto *target : {static_cast<const char *>(nullptr), "1.0", "1.2"}) {
    if (target) REQUIRE(setenv(gemstone::environment::VulkanApiVersion, target, 1) == 0);
    else REQUIRE(unsetenv(gemstone::environment::VulkanApiVersion) == 0);
    const uint32_t expected = target && std::strcmp(target, "1.0") == 0 ? VK_API_VERSION_1_0 : VK_API_VERSION_1_2;
    uint32_t version = 0;
    REQUIRE(vkEnumerateInstanceVersion(&version) == VK_SUCCESS);
    REQUIRE(version == expected);
    VkPhysicalDeviceProperties properties{};
    vkGetPhysicalDeviceProperties(VK_NULL_HANDLE, &properties);
    REQUIRE(properties.apiVersion == expected);
    TestDevice runtime;
    for (const auto *name : {"vkGetDeviceGroupPeerMemoryFeatures", "vkCmdSetDeviceMask", "vkGetDescriptorSetLayoutSupport",
                             "vkCreateSamplerYcbcrConversion", "vkDestroySamplerYcbcrConversion", "vkGetBufferOpaqueCaptureAddress",
                             "vkGetDeviceMemoryOpaqueCaptureAddress", "vkCmdDrawIndirectCount", "vkCmdDrawIndexedIndirectCount"}) {
      REQUIRE((vkGetDeviceProcAddr(runtime.device, name) != nullptr) == (expected >= VK_API_VERSION_1_1));
      REQUIRE((vkGetInstanceProcAddr(runtime.instance, name) != nullptr) == (expected >= VK_API_VERSION_1_1));
    }
    for (const auto *name : {"vkGetPhysicalDeviceExternalBufferProperties", "vkGetPhysicalDeviceExternalFenceProperties",
                             "vkGetPhysicalDeviceExternalSemaphoreProperties"}) {
      REQUIRE(vkGetDeviceProcAddr(runtime.device, name) == nullptr);
      REQUIRE((vkGetInstanceProcAddr(runtime.instance, name) != nullptr) == (expected >= VK_API_VERSION_1_1));
    }
    for (uint32_t requested : {0u, VK_MAKE_API_VERSION(0, 1, 0, 4095), VK_API_VERSION_1_1, VK_MAKE_API_VERSION(0, 1, 2, 4095),
                               VK_API_VERSION_1_3, VK_MAKE_API_VERSION(7, 127, 2, 0), VK_MAKE_API_VERSION(0, 127, 2, 0),
                               VK_MAKE_API_VERSION(0, 1, 1023, 0), VK_MAKE_API_VERSION(1, 1, 0, 0)}) {
      VkApplicationInfo application{};
      application.sType = VK_STRUCTURE_TYPE_APPLICATION_INFO;
      application.apiVersion = requested;
      VkInstanceCreateInfo info{};
      info.sType = VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO;
      info.pApplicationInfo = &application;
      VkInstance instance = VK_NULL_HANDLE;
      const bool supported = expected >= VK_API_VERSION_1_1 || (requested & ~0xfffu) <= expected;
      REQUIRE(vkCreateInstance(&info, nullptr, &instance) == (supported ? VK_SUCCESS : VK_ERROR_INCOMPATIBLE_DRIVER));
      if (supported) vkDestroyInstance(instance, nullptr);
      else REQUIRE(instance == VK_NULL_HANDLE);
    }
  }
  REQUIRE(setenv(gemstone::environment::VulkanApiVersion, "invalid", 1) == 0);
  uint32_t version = 0;
  REQUIRE(vkEnumerateInstanceVersion(&version) == VK_ERROR_INITIALIZATION_FAILED);
  VkInstanceCreateInfo info{};
  info.sType = VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO;
  VkInstance instance = VK_NULL_HANDLE;
  REQUIRE(vkCreateInstance(&info, nullptr, &instance) == VK_ERROR_INITIALIZATION_FAILED);
}

TEST_CASE("Vulkan core capability queries return initialised unsupported external properties", "[vulkan][lookup]") {
  VkExternalBufferProperties buffer{};
  buffer.sType = VK_STRUCTURE_TYPE_EXTERNAL_BUFFER_PROPERTIES;
  int tail = 7;
  buffer.pNext = &tail;
  std::memset(&buffer.externalMemoryProperties, 0xff, sizeof(buffer.externalMemoryProperties));
  vkGetPhysicalDeviceExternalBufferProperties(VK_NULL_HANDLE, nullptr, &buffer);
  REQUIRE(buffer.sType == VK_STRUCTURE_TYPE_EXTERNAL_BUFFER_PROPERTIES);
  REQUIRE(buffer.pNext == &tail);
  REQUIRE(buffer.externalMemoryProperties.externalMemoryFeatures == 0);
  REQUIRE(buffer.externalMemoryProperties.exportFromImportedHandleTypes == 0);
  REQUIRE(buffer.externalMemoryProperties.compatibleHandleTypes == 0);

  VkExternalFenceProperties fence{};
  fence.sType = VK_STRUCTURE_TYPE_EXTERNAL_FENCE_PROPERTIES;
  fence.pNext = &tail;
  fence.exportFromImportedHandleTypes = fence.compatibleHandleTypes = fence.externalFenceFeatures = ~0u;
  vkGetPhysicalDeviceExternalFenceProperties(VK_NULL_HANDLE, nullptr, &fence);
  REQUIRE(fence.sType == VK_STRUCTURE_TYPE_EXTERNAL_FENCE_PROPERTIES);
  REQUIRE(fence.pNext == &tail);
  REQUIRE(fence.exportFromImportedHandleTypes == 0);
  REQUIRE(fence.compatibleHandleTypes == 0);
  REQUIRE(fence.externalFenceFeatures == 0);

  VkExternalSemaphoreProperties semaphore{};
  semaphore.sType = VK_STRUCTURE_TYPE_EXTERNAL_SEMAPHORE_PROPERTIES;
  semaphore.pNext = &tail;
  semaphore.exportFromImportedHandleTypes = semaphore.compatibleHandleTypes = semaphore.externalSemaphoreFeatures = ~0u;
  vkGetPhysicalDeviceExternalSemaphoreProperties(VK_NULL_HANDLE, nullptr, &semaphore);
  REQUIRE(semaphore.sType == VK_STRUCTURE_TYPE_EXTERNAL_SEMAPHORE_PROPERTIES);
  REQUIRE(semaphore.pNext == &tail);
  REQUIRE(semaphore.exportFromImportedHandleTypes == 0);
  REQUIRE(semaphore.compatibleHandleTypes == 0);
  REQUIRE(semaphore.externalSemaphoreFeatures == 0);
  REQUIRE(tail == 7);
  vkDestroySamplerYcbcrConversion(VK_NULL_HANDLE, VK_NULL_HANDLE, nullptr);
}

TEST_CASE("Vulkan descriptor layout support includes the advertised per-set capacity", "[vulkan][descriptor]") {
  VkDescriptorSetLayoutBinding bindings[]{{0, VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER, 2048, VK_SHADER_STAGE_COMPUTE_BIT, nullptr},
                                          {1, VK_DESCRIPTOR_TYPE_STORAGE_BUFFER, 2048, VK_SHADER_STAGE_COMPUTE_BIT, nullptr}};
  VkDescriptorSetLayoutCreateInfo info{};
  info.sType = VK_STRUCTURE_TYPE_DESCRIPTOR_SET_LAYOUT_CREATE_INFO;
  info.bindingCount = 2;
  info.pBindings = bindings;
  VkDescriptorSetLayoutSupport support{};
  support.sType = VK_STRUCTURE_TYPE_DESCRIPTOR_SET_LAYOUT_SUPPORT;
  int tail = 9;
  support.pNext = &tail;
  vkGetDescriptorSetLayoutSupport(VK_NULL_HANDLE, &info, &support);
  REQUIRE(support.supported == VK_TRUE);
  REQUIRE(support.sType == VK_STRUCTURE_TYPE_DESCRIPTOR_SET_LAYOUT_SUPPORT);
  REQUIRE(support.pNext == &tail);
  bindings[1].descriptorCount = 2049;
  vkGetDescriptorSetLayoutSupport(VK_NULL_HANDLE, &info, &support);
  REQUIRE(support.supported == VK_FALSE);
  info.bindingCount = 0;
  vkGetDescriptorSetLayoutSupport(VK_NULL_HANDLE, &info, &support);
  REQUIRE(support.supported == VK_TRUE);
  REQUIRE(tail == 9);
}

TEST_CASE("Vulkan profiles select the reported device identity", "[vulkan][device]") {
  const auto [profile, name, vendor] = GENERATE(table<const char *, std::string, uint32_t>({
      {nullptr, "Gemstone Vulkan Compute Device", 0},
      {"amd", "AMD Gemstone Vulkan GPU", 0x1002},
      {"nvidia", "NVIDIA Gemstone Vulkan GPU", 0x10de},
      {"intel", "Intel Gemstone Vulkan GPU", 0x8086},
      {"llvmpipe", "llvmpipe (Gemstone Vulkan)", 0x10005},
  }));
  struct RestoreProfile {
    std::optional<std::string> value;
    RestoreProfile() {
      if (const char *current = std::getenv(gemstone::environment::Profile)) value = current;
    }
    ~RestoreProfile() {
      if (value) setenv(gemstone::environment::Profile, value->c_str(), 1);
      else unsetenv(gemstone::environment::Profile);
    }
  } restore;
  if (profile) REQUIRE(setenv(gemstone::environment::Profile, profile, 1) == 0);
  else REQUIRE(unsetenv(gemstone::environment::Profile) == 0);
  VkPhysicalDeviceProperties properties{};
  vkGetPhysicalDeviceProperties(VK_NULL_HANDLE, &properties);
  REQUIRE(properties.deviceName == name);
  REQUIRE(properties.vendorID == vendor);
}

TEST_CASE("Vulkan core device layer enumeration is callable and preserves empty output") {
  TestDevice runtime;
  const auto enumerate =
      reinterpret_cast<PFN_vkEnumerateDeviceLayerProperties>(vkGetInstanceProcAddr(runtime.instance, "vkEnumerateDeviceLayerProperties"));
  REQUIRE(enumerate != nullptr);
  VkLayerProperties output;
  std::memset(&output, 0xa5, sizeof(output));
  const auto before = output;
  uint32_t count = 1;
  REQUIRE(enumerate(VK_NULL_HANDLE, &count, &output) == VK_SUCCESS);
  REQUIRE(count == 0);
  REQUIRE(std::memcmp(&before, &output, sizeof(output)) == 0);
  count = 5;
  REQUIRE(enumerate(VK_NULL_HANDLE, &count, nullptr) == VK_SUCCESS);
  REQUIRE(count == 0);
}

TEST_CASE("Vulkan owns buffer memory and executes copies") {
  TestDevice runtime;
  VkMemoryAllocateInfo allocation{};
  allocation.sType = VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO;
  allocation.allocationSize = 32;
  allocation.memoryTypeIndex = 0;
  VkDeviceMemory memory = VK_NULL_HANDLE;
  REQUIRE(vkAllocateMemory(VK_NULL_HANDLE, &allocation, nullptr, &memory) == VK_SUCCESS);
  void *mapped = nullptr;
  REQUIRE(vkMapMemory(VK_NULL_HANDLE, memory, 0, VK_WHOLE_SIZE, 0, &mapped) == VK_SUCCESS);
  std::memcpy(mapped, "gemstone", 9);

  VkBufferCreateInfo bufferInfo{};
  bufferInfo.sType = VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO;
  bufferInfo.size = 16;
  VkBuffer source = VK_NULL_HANDLE;
  VkBuffer destination = VK_NULL_HANDLE;
  REQUIRE(vkCreateBuffer(VK_NULL_HANDLE, &bufferInfo, nullptr, &source) == VK_SUCCESS);
  REQUIRE(vkCreateBuffer(VK_NULL_HANDLE, &bufferInfo, nullptr, &destination) == VK_SUCCESS);
  VkMemoryDedicatedRequirements dedicated{};
  dedicated.sType = VK_STRUCTURE_TYPE_MEMORY_DEDICATED_REQUIREMENTS;
  dedicated.prefersDedicatedAllocation = dedicated.requiresDedicatedAllocation = 0xcccccccc;
  VkMemoryRequirements2 requirements{};
  requirements.sType = VK_STRUCTURE_TYPE_MEMORY_REQUIREMENTS_2;
  requirements.pNext = &dedicated;
  VkBufferMemoryRequirementsInfo2 query{};
  query.sType = VK_STRUCTURE_TYPE_BUFFER_MEMORY_REQUIREMENTS_INFO_2;
  query.buffer = source;
  vkGetBufferMemoryRequirements2(runtime.device, &query, &requirements);
  REQUIRE(requirements.memoryRequirements.size == bufferInfo.size);
  REQUIRE(dedicated.prefersDedicatedAllocation == VK_FALSE);
  REQUIRE(dedicated.requiresDedicatedAllocation == VK_FALSE);
  REQUIRE(vkBindBufferMemory(VK_NULL_HANDLE, source, memory, 0) == VK_SUCCESS);
  REQUIRE(vkBindBufferMemory(VK_NULL_HANDLE, destination, memory, 16) == VK_SUCCESS);

  CommandRecording recording(runtime.device);
  const auto command = recording.command;
  const VkBufferCopy region{0, 0, 9};
  vkCmdCopyBuffer(command, source, destination, 1, &region);
  REQUIRE(vkEndCommandBuffer(command) == VK_SUCCESS);
  VkFenceCreateInfo fenceInfo{};
  fenceInfo.sType = VK_STRUCTURE_TYPE_FENCE_CREATE_INFO;
  VkFence fence = VK_NULL_HANDLE;
  REQUIRE(vkCreateFence(VK_NULL_HANDLE, &fenceInfo, nullptr, &fence) == VK_SUCCESS);
  REQUIRE(submitAndWait(runtime.queue, command, fence) == VK_SUCCESS);
  REQUIRE(vkWaitForFences(VK_NULL_HANDLE, 1, &fence, VK_TRUE, 0) == VK_SUCCESS);
  REQUIRE(std::memcmp(static_cast<char *>(mapped) + 16, "gemstone", 9) == 0);
  vkDestroyFence(VK_NULL_HANDLE, fence, nullptr);
  vkDestroyBuffer(VK_NULL_HANDLE, destination, nullptr);
  vkDestroyBuffer(VK_NULL_HANDLE, source, nullptr);
  vkFreeMemory(VK_NULL_HANDLE, memory, nullptr);
}

TEST_CASE("Vulkan buffer fills, updates and copies stay within the bound range") {
  const auto operation = GENERATE(0, 1, 2, 3);
  TestDevice runtime;
  VkMemoryAllocateInfo allocation{};
  allocation.sType = VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO;
  allocation.allocationSize = 48;
  VkDeviceMemory memory{};
  REQUIRE(vkAllocateMemory(runtime.device, &allocation, nullptr, &memory) == VK_SUCCESS);
  uint8_t *bytes = nullptr;
  REQUIRE(vkMapMemory(runtime.device, memory, 0, VK_WHOLE_SIZE, 0, reinterpret_cast<void **>(&bytes)) == VK_SUCCESS);
  std::memset(bytes, 0xa5, 48);
  VkBufferCreateInfo bufferInfo{};
  bufferInfo.sType = VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO;
  bufferInfo.size = 16;
  VkBuffer source{}, target{};
  REQUIRE(vkCreateBuffer(runtime.device, &bufferInfo, nullptr, &source) == VK_SUCCESS);
  REQUIRE(vkCreateBuffer(runtime.device, &bufferInfo, nullptr, &target) == VK_SUCCESS);
  REQUIRE(vkBindBufferMemory(runtime.device, source, memory, 0) == VK_SUCCESS);
  REQUIRE(vkBindBufferMemory(runtime.device, target, memory, 16) == VK_SUCCESS);
  CommandRecording recording(runtime.device);
  const std::array<uint8_t, 12> payload{};
  const VkBufferCopy region{0, 8, 16};
  switch (operation) {
    case 0: vkCmdFillBuffer(recording.command, target, 0, VK_WHOLE_SIZE, 0); break;
    case 1: vkCmdFillBuffer(recording.command, target, 8, 16, 0); break;
    case 2: vkCmdUpdateBuffer(recording.command, target, 8, payload.size(), payload.data()); break;
    default: vkCmdCopyBuffer(recording.command, source, target, 1, &region); break;
  }
  REQUIRE(vkEndCommandBuffer(recording.command) == VK_SUCCESS);
  const bool valid = operation == 0;
  REQUIRE(submitAndWait(runtime.queue, recording.command) == (valid ? VK_SUCCESS : VK_ERROR_DEVICE_LOST));
  for (size_t i = 0; i < 48; ++i)
    REQUIRE(bytes[i] == (valid && i >= 16 && i < 32 ? 0 : 0xa5));
  vkDestroyBuffer(runtime.device, target, nullptr);
  vkDestroyBuffer(runtime.device, source, nullptr);
  vkFreeMemory(runtime.device, memory, nullptr);
}

TEST_CASE("Vulkan rejects unknown semaphore handles before enqueueing") {
  TestDevice runtime;
  CommandRecording recording(runtime.device);
  vkCmdDispatch(recording.command, 1, 1, 1);
  REQUIRE(vkEndCommandBuffer(recording.command) == VK_SUCCESS);
  const auto semaphore = reinterpret_cast<VkSemaphore>(0x1000);
  const VkPipelineStageFlags stage = VK_PIPELINE_STAGE_COMPUTE_SHADER_BIT;
  auto info = submitInfo(recording.command);
  if (GENERATE(false, true)) {
    info.waitSemaphoreCount = 1;
    info.pWaitSemaphores = &semaphore;
    info.pWaitDstStageMask = &stage;
  } else {
    info.signalSemaphoreCount = 1;
    info.pSignalSemaphores = &semaphore;
  }
  REQUIRE(vkQueueSubmit(runtime.queue, 1, &info, VK_NULL_HANDLE) == VK_ERROR_INITIALIZATION_FAILED);
  REQUIRE(vkQueueWaitIdle(runtime.queue) == VK_SUCCESS);
}

TEST_CASE("Vulkan descriptor writes and copies cross binding boundaries", "[vulkan][descriptor]") {
  const auto type = GENERATE(VK_DESCRIPTOR_TYPE_STORAGE_BUFFER, VK_DESCRIPTOR_TYPE_SAMPLED_IMAGE, VK_DESCRIPTOR_TYPE_STORAGE_TEXEL_BUFFER);
  const bool useTemplate = GENERATE(false, true);
  DescriptorState state;
  const auto source = reinterpret_cast<VkDescriptorSet>(0x1000);
  const auto destination = reinterpret_cast<VkDescriptorSet>(0x2000);
  // Explicit zero-sized bindings and omitted bindings both consume no descriptors.
  allocateDescriptors(state, source, type, {{5, 2}, {2, 0}, {4, 1}, {1, 2}});
  allocateDescriptors(state, destination, type, {{3, 1}, {4, 4}, {5, 1}});
  VkDescriptorBufferInfo buffers[4]{};
  VkDescriptorImageInfo images[4]{};
  VkBufferView texels[4]{};
  for (size_t i = 0; i < 4; ++i) {
    buffers[i] = {reinterpret_cast<VkBuffer>(0x3000 + i), i * 16, 16};
    images[i] = {VK_NULL_HANDLE, reinterpret_cast<VkImageView>(0x4000 + i), VK_IMAGE_LAYOUT_GENERAL};
    texels[i] = reinterpret_cast<VkBufferView>(0x5000 + i);
  }
  VkWriteDescriptorSet write{};
  write.sType = VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET;
  write.dstSet = source;
  write.dstBinding = 1;
  write.dstArrayElement = 1;
  write.descriptorCount = 4;
  write.descriptorType = type;
  write.pBufferInfo = buffers;
  write.pImageInfo = images;
  write.pTexelBufferView = texels;
  if (useTemplate) {
    const auto apply = [&](const auto &values) {
      // Deliberately unaligned, padded elements exercise both offset and stride.
      const size_t offset = 1, stride = sizeof(values[0]) + 3;
      std::vector<uint8_t> bytes(offset + std::size(values) * stride, 0xcd);
      for (size_t i = 0; i < std::size(values); ++i)
        std::memcpy(bytes.data() + offset + i * stride, &values[i], sizeof(values[i]));
      const VkDescriptorUpdateTemplateEntry entry{write.dstBinding, write.dstArrayElement, write.descriptorCount, type, offset, stride};
      state.updateTemplate(source, {&entry, 1}, bytes.data());
    };
    if (type == VK_DESCRIPTOR_TYPE_STORAGE_BUFFER) apply(buffers);
    else if (type == VK_DESCRIPTOR_TYPE_SAMPLED_IMAGE) apply(images);
    else apply(texels);
  } else state.update(1, &write, 0, nullptr);
  const auto check = [&](VkDescriptorSet set, std::pair<uint32_t, uint32_t> slot, size_t i) {
    if (type == VK_DESCRIPTOR_TYPE_STORAGE_BUFFER) {
      const auto *value = state.get(set, slot.first, slot.second);
      REQUIRE(value);
      REQUIRE(value->buffer == buffers[i].buffer);
      REQUIRE(value->offset == buffers[i].offset);
    } else if (type == VK_DESCRIPTOR_TYPE_SAMPLED_IMAGE) {
      REQUIRE(state.get<VkDescriptorImageInfo>(set, slot.first, slot.second)->imageView == images[i].imageView);
    } else REQUIRE(*state.get<VkBufferView>(set, slot.first, slot.second) == texels[i]);
  };
  REQUIRE(state.get(source, 3, 0) == nullptr);
  check(source, {1, 1}, 0);
  check(source, {4, 0}, 1);
  check(source, {5, 0}, 2);
  check(source, {5, 1}, 3);
  VkCopyDescriptorSet copy{};
  copy.sType = VK_STRUCTURE_TYPE_COPY_DESCRIPTOR_SET;
  copy.srcSet = source;
  copy.srcBinding = 1;
  copy.srcArrayElement = 1;
  copy.dstSet = destination;
  copy.dstBinding = 3;
  copy.descriptorCount = 4;
  state.update(0, nullptr, 1, &copy);
  check(destination, {3, 0}, 0);
  check(destination, {4, 0}, 1);
  check(destination, {4, 1}, 2);
  check(destination, {4, 2}, 3);
}

TEST_CASE("Vulkan descriptor views enforce their declared range") {
  REQUIRE(rangeLength(64, 16, 12) == std::optional<size_t>(12));
  REQUIRE(rangeLength(64, 16, VK_WHOLE_SIZE) == std::optional<size_t>(48));
  REQUIRE_FALSE(rangeLength(64, 65, VK_WHOLE_SIZE));
  REQUIRE_FALSE(rangeLength(64, 60, 8));
}

TEST_CASE("Vulkan descriptor allocation layout preserves overlapping aliases") {
  const auto first = reinterpret_cast<VkDeviceMemory>(0x1000);
  const auto second = reinterpret_cast<VkDeviceMemory>(0x2000);
  const auto layout = descriptorAllocationLayout({
      {first, 16, 16},
      {first, 24, 16},
      {first, 64, 8},
      {second, 0, 4},
  });
  REQUIRE(layout.allocations.size() == 3);
  REQUIRE(layout.allocations[0].memory == first);
  REQUIRE(layout.allocations[0].offset == 16);
  REQUIRE(layout.allocations[0].size == 24);
  REQUIRE(layout.arguments[0].allocation == layout.arguments[1].allocation);
  REQUIRE(layout.arguments[0].offset == 0);
  REQUIRE(layout.arguments[1].offset == 8);
  REQUIRE(layout.arguments[2].allocation != layout.arguments[0].allocation);
  REQUIRE(layout.arguments[3].allocation != layout.arguments[0].allocation);
}

TEST_CASE("Vulkan descriptor allocation layout merges chains independent of input order") {
  const auto first = reinterpret_cast<VkDeviceMemory>(0x1000);
  const auto second = reinterpret_cast<VkDeviceMemory>(0x2000);
  // The bridge appears last, after nested, disjoint, touching and other-memory views.
  const std::vector<DescriptorRange> ranges{{first, 32, 16}, {first, 0, 16},  {first, 4, 4}, {first, 64, 8},
                                            {first, 48, 8},  {second, 0, 16}, {first, 8, 32}};
  auto order = std::vector<size_t>{0, 1, 2, 3, 4, 5, 6};
  do {
    std::vector<DescriptorRange> shuffled;
    for (const auto i : order)
      shuffled.push_back(ranges[i]);
    const auto layout = descriptorAllocationLayout(shuffled);
    REQUIRE(layout.allocations.size() == 4);
    for (size_t i = 0; i < order.size(); ++i) {
      const auto &input = shuffled[i];
      const auto &argument = layout.arguments[i];
      const auto &allocation = layout.allocations.at(argument.allocation);
      const bool merged = order[i] == 0 || order[i] == 1 || order[i] == 2 || order[i] == 6;
      REQUIRE(allocation.memory == input.memory);
      REQUIRE(allocation.offset == (merged ? 0 : input.offset));
      REQUIRE(allocation.size == (merged ? 48 : input.size));
      REQUIRE(allocation.offset + argument.offset == input.offset);
    }
  } while (std::next_permutation(order.begin(), order.end()));
  const auto empty = descriptorAllocationLayout({});
  REQUIRE(empty.allocations.empty());
  REQUIRE(empty.arguments.empty());
}

TEST_CASE("Vulkan dispatch uses persistent memory and preserves descriptor aliases") {
  TestDevice runtime;
  gemstone_test::RpcFixture fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  fixture.expect({1, 1, 1, 0, 0});
  fixture.kernelWorkGroup({1, 1, 1});
  fixture.shaderDescriptors(
      {{{0}, {0}, {0}, gemstone::execution::ShaderResourceKind::Buffer}, {{0}, {1}, {0}, gemstone::execution::ShaderResourceKind::Buffer}});
  fixture.expectAllocationArguments({{0, 0, 16}, {0, 4, 16}});
  const uint32_t deviceWrite = 0x12345678;
  std::vector<uint8_t> deviceBytes(sizeof(deviceWrite));
  std::memcpy(deviceBytes.data(), &deviceWrite, sizeof(deviceWrite));
  fixture.writeAllocationArgument(1, 0, std::move(deviceBytes));

  const std::vector<uint32_t> shader = {
      0x07230203,
      0x00010300,
      0,
      32,
      0,
      (2u << 16) | 17u,
      1,
      (3u << 16) | 14u,
      0,
      1,
      (7u << 16) | 15u,
      5,
      7,
      0x6e69616d,
      0,
      20,
      21,
      (6u << 16) | 16u,
      7,
      17,
      1,
      1,
      1,
      (4u << 16) | 71u,
      20,
      33,
      0,
      (4u << 16) | 71u,
      20,
      34,
      0,
      (4u << 16) | 71u,
      21,
      33,
      1,
      (4u << 16) | 71u,
      21,
      34,
      0,
  };
  VkShaderModuleCreateInfo shaderInfo{};
  shaderInfo.sType = VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO;
  shaderInfo.codeSize = shader.size() * sizeof(uint32_t);
  shaderInfo.pCode = shader.data();
  VkShaderModule module = VK_NULL_HANDLE;
  REQUIRE(vkCreateShaderModule(VK_NULL_HANDLE, &shaderInfo, nullptr, &module) == VK_SUCCESS);
  VkComputePipelineCreateInfo pipelineInfo{};
  pipelineInfo.sType = VK_STRUCTURE_TYPE_COMPUTE_PIPELINE_CREATE_INFO;
  pipelineInfo.stage.sType = VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO;
  pipelineInfo.stage.stage = VK_SHADER_STAGE_COMPUTE_BIT;
  pipelineInfo.stage.module = module;
  pipelineInfo.stage.pName = "main";
  const VkSpecializationMapEntry specializationEntries[] = {{0, 0, 4}, {1, 4, 4}, {2, 8, 4}};
  const uint32_t specializationData[] = {1, 1, 1};
  const VkSpecializationInfo specializationInfo{3, specializationEntries, sizeof(specializationData), specializationData};
  pipelineInfo.stage.pSpecializationInfo = &specializationInfo;
  VkPipeline pipeline = VK_NULL_HANDLE;
  REQUIRE(vkCreateComputePipelines(VK_NULL_HANDLE, VK_NULL_HANDLE, 1, &pipelineInfo, nullptr, &pipeline) == VK_SUCCESS);

  VkMemoryAllocateInfo allocationInfo{};
  allocationInfo.sType = VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO;
  allocationInfo.allocationSize = 32;
  VkDeviceMemory memory = VK_NULL_HANDLE;
  REQUIRE(vkAllocateMemory(VK_NULL_HANDLE, &allocationInfo, nullptr, &memory) == VK_SUCCESS);
  void *mapped = nullptr;
  REQUIRE(vkMapMemory(VK_NULL_HANDLE, memory, 0, VK_WHOLE_SIZE, 0, &mapped) == VK_SUCCESS);
  std::memset(mapped, 0, 32);
  vkUnmapMemory(VK_NULL_HANDLE, memory);

  VkBufferCreateInfo bufferInfo{};
  bufferInfo.sType = VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO;
  bufferInfo.size = 20;
  VkBuffer buffers[2]{};
  REQUIRE(vkCreateBuffer(VK_NULL_HANDLE, &bufferInfo, nullptr, &buffers[0]) == VK_SUCCESS);
  REQUIRE(vkCreateBuffer(VK_NULL_HANDLE, &bufferInfo, nullptr, &buffers[1]) == VK_SUCCESS);
  REQUIRE(vkBindBufferMemory(VK_NULL_HANDLE, buffers[0], memory, 0) == VK_SUCCESS);
  REQUIRE(vkBindBufferMemory(VK_NULL_HANDLE, buffers[1], memory, 0) == VK_SUCCESS);
  const VkDescriptorSetLayoutBinding bindings[] = {
      {0, VK_DESCRIPTOR_TYPE_STORAGE_BUFFER, 1, VK_SHADER_STAGE_COMPUTE_BIT, nullptr},
      {1, VK_DESCRIPTOR_TYPE_STORAGE_BUFFER, 1, VK_SHADER_STAGE_COMPUTE_BIT, nullptr},
  };
  VkDescriptorSetLayoutCreateInfo layoutInfo{};
  layoutInfo.sType = VK_STRUCTURE_TYPE_DESCRIPTOR_SET_LAYOUT_CREATE_INFO;
  layoutInfo.bindingCount = 2;
  layoutInfo.pBindings = bindings;
  VkDescriptorSetLayout layout{};
  REQUIRE(vkCreateDescriptorSetLayout(runtime.device, &layoutInfo, nullptr, &layout) == VK_SUCCESS);
  const VkDescriptorPoolSize poolSize{VK_DESCRIPTOR_TYPE_STORAGE_BUFFER, 2};
  VkDescriptorPoolCreateInfo poolInfo{};
  poolInfo.sType = VK_STRUCTURE_TYPE_DESCRIPTOR_POOL_CREATE_INFO;
  poolInfo.maxSets = 1;
  poolInfo.poolSizeCount = 1;
  poolInfo.pPoolSizes = &poolSize;
  VkDescriptorPool pool{};
  REQUIRE(vkCreateDescriptorPool(runtime.device, &poolInfo, nullptr, &pool) == VK_SUCCESS);
  VkDescriptorSetAllocateInfo setInfo{};
  setInfo.sType = VK_STRUCTURE_TYPE_DESCRIPTOR_SET_ALLOCATE_INFO;
  setInfo.descriptorPool = pool;
  setInfo.descriptorSetCount = 1;
  setInfo.pSetLayouts = &layout;
  VkDescriptorSet descriptors{};
  REQUIRE(vkAllocateDescriptorSets(runtime.device, &setInfo, &descriptors) == VK_SUCCESS);
  const VkDescriptorBufferInfo descriptorBuffers[] = {{buffers[0], 0, 16}, {buffers[1], 4, 16}};
  VkWriteDescriptorSet writes[2]{};
  for (uint32_t index = 0; index < 2; ++index) {
    writes[index].sType = VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET;
    writes[index].dstSet = descriptors;
    writes[index].dstBinding = index;
    writes[index].descriptorCount = 1;
    writes[index].descriptorType = VK_DESCRIPTOR_TYPE_STORAGE_BUFFER;
    writes[index].pBufferInfo = &descriptorBuffers[index];
  }
  vkUpdateDescriptorSets(VK_NULL_HANDLE, 2, writes, 0, nullptr);

  CommandRecording recording(runtime.device);
  const auto command = recording.command;
  vkCmdBindPipeline(command, VK_PIPELINE_BIND_POINT_COMPUTE, pipeline);
  vkCmdBindDescriptorSets(command, VK_PIPELINE_BIND_POINT_COMPUTE, VK_NULL_HANDLE, 0, 1, &descriptors, 0, nullptr);
  VkBuffer indirect = VK_NULL_HANDLE;
  SECTION("direct dispatch") { vkCmdDispatch(command, 1, 1, 1); }
  SECTION("indirect dispatch reads parameters after the preceding update") {
    bufferInfo.size = 32;
    REQUIRE(vkCreateBuffer(VK_NULL_HANDLE, &bufferInfo, nullptr, &indirect) == VK_SUCCESS);
    REQUIRE(vkBindBufferMemory(VK_NULL_HANDLE, indirect, memory, 0) == VK_SUCCESS);
    VkDispatchIndirectCommand dimensions{1, 1, 1};
    vkCmdUpdateBuffer(command, indirect, 20, sizeof(dimensions), &dimensions);
    dimensions = {0, 0, 0}; // UpdateBuffer must own the original host data.
    vkCmdDispatchIndirect(command, indirect, 20);
  }
  REQUIRE(vkEndCommandBuffer(command) == VK_SUCCESS);
  REQUIRE(submitAndWait(runtime.queue, command) == VK_SUCCESS);
  fixture.check();

  REQUIRE(vkMapMemory(VK_NULL_HANDLE, memory, 0, VK_WHOLE_SIZE, 0, &mapped) == VK_SUCCESS);
  REQUIRE(static_cast<uint32_t *>(mapped)[1] == deviceWrite);
  vkUnmapMemory(VK_NULL_HANDLE, memory);
  if (indirect) vkDestroyBuffer(VK_NULL_HANDLE, indirect, nullptr);
  vkDestroyBuffer(VK_NULL_HANDLE, buffers[1], nullptr);
  vkDestroyBuffer(VK_NULL_HANDLE, buffers[0], nullptr);
  vkFreeMemory(VK_NULL_HANDLE, memory, nullptr);
  vkDestroyPipeline(VK_NULL_HANDLE, pipeline, nullptr);
  vkDestroyShaderModule(VK_NULL_HANDLE, module, nullptr);
  vkDestroyDescriptorPool(runtime.device, pool, nullptr);
  vkDestroyDescriptorSetLayout(runtime.device, layout, nullptr);
}

TEST_CASE("Vulkan descriptor pool capacity is restored by reset") {
  VkDescriptorPoolCreateInfo poolInfo{};
  poolInfo.sType = VK_STRUCTURE_TYPE_DESCRIPTOR_POOL_CREATE_INFO;
  poolInfo.maxSets = 1;
  VkDescriptorPool pool = VK_NULL_HANDLE;
  REQUIRE(vkCreateDescriptorPool(VK_NULL_HANDLE, &poolInfo, nullptr, &pool) == VK_SUCCESS);

  const auto layout = reinterpret_cast<VkDescriptorSetLayout>(0x1000);
  VkDescriptorSetAllocateInfo allocation{};
  allocation.sType = VK_STRUCTURE_TYPE_DESCRIPTOR_SET_ALLOCATE_INFO;
  allocation.descriptorPool = pool;
  allocation.descriptorSetCount = 1;
  allocation.pSetLayouts = &layout;
  VkDescriptorSet first = VK_NULL_HANDLE;
  VkDescriptorSet second = VK_NULL_HANDLE;
  REQUIRE(vkAllocateDescriptorSets(VK_NULL_HANDLE, &allocation, &first) == VK_SUCCESS);
  REQUIRE(vkAllocateDescriptorSets(VK_NULL_HANDLE, &allocation, &second) == VK_ERROR_OUT_OF_POOL_MEMORY);
  REQUIRE(vkResetDescriptorPool(VK_NULL_HANDLE, pool, 0) == VK_SUCCESS);
  REQUIRE(vkAllocateDescriptorSets(VK_NULL_HANDLE, &allocation, &second) == VK_SUCCESS);
  vkDestroyDescriptorPool(VK_NULL_HANDLE, pool, nullptr);
}

TEST_CASE("Vulkan device groups preserve caller structure headers") {
  VkInstanceCreateInfo info{};
  info.sType = VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO;
  VkInstance instance = VK_NULL_HANDLE;
  REQUIRE(vkCreateInstance(&info, nullptr, &instance) == VK_SUCCESS);
  uint32_t count = 0;
  REQUIRE(vkEnumeratePhysicalDeviceGroups(instance, &count, nullptr) == VK_SUCCESS);
  REQUIRE(count == 1);
  VkPhysicalDeviceGroupProperties group{};
  group.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_GROUP_PROPERTIES;
  REQUIRE(vkEnumeratePhysicalDeviceGroups(instance, &count, &group) == VK_SUCCESS);
  REQUIRE(group.sType == VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_GROUP_PROPERTIES);
  REQUIRE(group.physicalDeviceCount == 1);
  REQUIRE(group.physicalDevices[0] != VK_NULL_HANDLE);
  count = 0;
  REQUIRE(vkEnumeratePhysicalDeviceGroups(instance, &count, &group) == VK_INCOMPLETE);
  vkDestroyInstance(instance, nullptr);
}

TEST_CASE("Vulkan events expose reset set and reset state") {
  VkEventCreateInfo info{};
  info.sType = VK_STRUCTURE_TYPE_EVENT_CREATE_INFO;
  VkEvent event = VK_NULL_HANDLE;
  REQUIRE(vkCreateEvent(VK_NULL_HANDLE, &info, nullptr, &event) == VK_SUCCESS);
  REQUIRE(vkGetEventStatus(VK_NULL_HANDLE, event) == VK_EVENT_RESET);
  REQUIRE(vkSetEvent(VK_NULL_HANDLE, event) == VK_SUCCESS);
  REQUIRE(vkGetEventStatus(VK_NULL_HANDLE, event) == VK_EVENT_SET);
  REQUIRE(vkResetEvent(VK_NULL_HANDLE, event) == VK_SUCCESS);
  REQUIRE(vkGetEventStatus(VK_NULL_HANDLE, event) == VK_EVENT_RESET);
  vkDestroyEvent(VK_NULL_HANDLE, event, nullptr);
}

TEST_CASE("Vulkan colour images validate memory bounds and unsupported formats") {
  VkImageCreateInfo info{};
  info.sType = VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO;
  info.imageType = VK_IMAGE_TYPE_2D;
  info.format = VK_FORMAT_R8G8B8A8_UNORM;
  info.extent = {8, 4, 1};
  info.mipLevels = info.arrayLayers = 1;
  info.samples = VK_SAMPLE_COUNT_1_BIT;
  info.tiling = VK_IMAGE_TILING_OPTIMAL;
  info.usage = VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT | VK_IMAGE_USAGE_TRANSFER_SRC_BIT;
  VkImage image = VK_NULL_HANDLE;
  REQUIRE(vkCreateImage(VK_NULL_HANDLE, &info, nullptr, &image) == VK_SUCCESS);
  VkMemoryRequirements requirements{};
  vkGetImageMemoryRequirements(VK_NULL_HANDLE, image, &requirements);
  REQUIRE(requirements.size == 128);
  VkMemoryDedicatedRequirements dedicated{};
  dedicated.sType = VK_STRUCTURE_TYPE_MEMORY_DEDICATED_REQUIREMENTS;
  dedicated.prefersDedicatedAllocation = dedicated.requiresDedicatedAllocation = 0xcccccccc;
  VkMemoryRequirements2 extended{};
  extended.sType = VK_STRUCTURE_TYPE_MEMORY_REQUIREMENTS_2;
  extended.pNext = &dedicated;
  VkImageMemoryRequirementsInfo2 query{};
  query.sType = VK_STRUCTURE_TYPE_IMAGE_MEMORY_REQUIREMENTS_INFO_2;
  query.image = image;
  vkGetImageMemoryRequirements2(VK_NULL_HANDLE, &query, &extended);
  REQUIRE(extended.memoryRequirements.size == requirements.size);
  REQUIRE(dedicated.prefersDedicatedAllocation == VK_FALSE);
  REQUIRE(dedicated.requiresDedicatedAllocation == VK_FALSE);
  VkMemoryAllocateInfo allocation{};
  allocation.sType = VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO;
  allocation.allocationSize = requirements.size;
  VkDeviceMemory memory = VK_NULL_HANDLE;
  REQUIRE(vkAllocateMemory(VK_NULL_HANDLE, &allocation, nullptr, &memory) == VK_SUCCESS);
  REQUIRE(vkBindImageMemory(VK_NULL_HANDLE, image, memory, 64) == VK_ERROR_OUT_OF_DEVICE_MEMORY);
  REQUIRE(vkBindImageMemory(VK_NULL_HANDLE, image, memory, 0) == VK_SUCCESS);
  vkDestroyImage(VK_NULL_HANDLE, image, nullptr);
  vkFreeMemory(VK_NULL_HANDLE, memory, nullptr);
  info.format = VK_FORMAT_D32_SFLOAT;
  REQUIRE(vkCreateImage(VK_NULL_HANDLE, &info, nullptr, &image) == VK_ERROR_FORMAT_NOT_SUPPORTED);
}

TEST_CASE("Vulkan zero work dispatches perform no RPC or descriptor accesses") {
  TestDevice runtime;
  CommandRecording recording(runtime.device);
  const auto command = recording.command;
  vkCmdDispatch(command, 0, 1, 1);
  vkCmdDispatch(command, 1, 0, 1);
  vkCmdDispatch(command, 1, 1, 0);
  REQUIRE(vkEndCommandBuffer(command) == VK_SUCCESS);
  REQUIRE(submitAndWait(runtime.queue, command) == VK_SUCCESS);
  REQUIRE(vkResetCommandBuffer(command, 0) == VK_SUCCESS);
}

TEST_CASE("Vulkan secondary commands retain payload snapshots and replay in order") {
  TestDevice runtime;
  CommandRecording recording(runtime.device);
  const auto primary = recording.command, secondary = recording.secondary();
  VkEventCreateInfo eventInfo{};
  eventInfo.sType = VK_STRUCTURE_TYPE_EVENT_CREATE_INFO;
  VkEvent event;
  REQUIRE(vkCreateEvent(VK_NULL_HANDLE, &eventInfo, nullptr, &event) == VK_SUCCESS);
  // Guard words surround a two-word update recorded into the secondary buffer.
  std::array<uint32_t, 4> expected{0xaabbccddu, 17, 29, 0xaabbccddu};
  std::array<uint32_t, 2> payload{expected[1], expected[2]};
  VkMemoryAllocateInfo memoryInfo{};
  memoryInfo.sType = VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO;
  memoryInfo.allocationSize = sizeof(expected);
  VkDeviceMemory memory;
  REQUIRE(vkAllocateMemory(runtime.device, &memoryInfo, nullptr, &memory) == VK_SUCCESS);
  VkBufferCreateInfo bufferInfo{};
  bufferInfo.sType = VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO;
  bufferInfo.size = sizeof(expected);
  bufferInfo.usage = VK_BUFFER_USAGE_TRANSFER_DST_BIT;
  VkBuffer buffer;
  REQUIRE(vkCreateBuffer(runtime.device, &bufferInfo, nullptr, &buffer) == VK_SUCCESS);
  REQUIRE(vkBindBufferMemory(runtime.device, buffer, memory, 0) == VK_SUCCESS);
  void *mapped = nullptr;
  REQUIRE(vkMapMemory(runtime.device, memory, 0, VK_WHOLE_SIZE, 0, &mapped) == VK_SUCCESS);
  VkCommandBufferBeginInfo begin{};
  begin.sType = VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO;
  REQUIRE(vkBeginCommandBuffer(secondary, &begin) == VK_SUCCESS);
  vkCmdUpdateBuffer(secondary, buffer, sizeof(uint32_t), sizeof(payload), payload.data());
  payload.fill(0); // Caller storage can change immediately after recording.
  vkCmdSetEvent(secondary, event, VK_PIPELINE_STAGE_ALL_COMMANDS_BIT);
  REQUIRE(vkEndCommandBuffer(secondary) == VK_SUCCESS);
  vkCmdFillBuffer(primary, buffer, 0, VK_WHOLE_SIZE, expected[0]);
  vkCmdExecuteCommands(primary, 1, &secondary);
  vkCmdWaitEvents(primary, 1, &event, VK_PIPELINE_STAGE_ALL_COMMANDS_BIT, VK_PIPELINE_STAGE_ALL_COMMANDS_BIT, 0, nullptr, 0, nullptr, 0,
                  nullptr);
  REQUIRE(vkEndCommandBuffer(primary) == VK_SUCCESS);
  REQUIRE(vkGetEventStatus(VK_NULL_HANDLE, event) == VK_EVENT_RESET);
  for (int repeat = 0; repeat < 2; ++repeat) {
    std::memset(mapped, 0, sizeof(expected));
    REQUIRE(vkResetEvent(VK_NULL_HANDLE, event) == VK_SUCCESS);
    REQUIRE(submitAndWait(runtime.queue, primary) == VK_SUCCESS);
    REQUIRE(vkGetEventStatus(VK_NULL_HANDLE, event) == VK_EVENT_SET);
    REQUIRE(std::memcmp(mapped, expected.data(), sizeof(expected)) == 0);
  }
  vkDestroyEvent(VK_NULL_HANDLE, event, nullptr);
  vkUnmapMemory(runtime.device, memory);
  vkDestroyBuffer(runtime.device, buffer, nullptr);
  vkFreeMemory(runtime.device, memory, nullptr);
}

TEST_CASE("Vulkan binary waits consume one signal and permit cross queue reuse") {
  TestDevice runtime;
  VkSemaphoreCreateInfo create{};
  create.sType = VK_STRUCTURE_TYPE_SEMAPHORE_CREATE_INFO;
  VkSemaphore semaphore;
  REQUIRE(vkCreateSemaphore(runtime.device, &create, nullptr, &semaphore) == VK_SUCCESS);
  VkFenceCreateInfo fenceInfo{};
  fenceInfo.sType = VK_STRUCTURE_TYPE_FENCE_CREATE_INFO;
  VkFence fence;
  REQUIRE(vkCreateFence(runtime.device, &fenceInfo, nullptr, &fence) == VK_SUCCESS);
  const VkPipelineStageFlags stage = VK_PIPELINE_STAGE_ALL_COMMANDS_BIT;
  VkSubmitInfo signal{};
  signal.sType = VK_STRUCTURE_TYPE_SUBMIT_INFO;
  signal.signalSemaphoreCount = 1;
  signal.pSignalSemaphores = &semaphore;
  VkSubmitInfo wait{};
  wait.sType = VK_STRUCTURE_TYPE_SUBMIT_INFO;
  wait.waitSemaphoreCount = 1;
  wait.pWaitSemaphores = &semaphore;
  wait.pWaitDstStageMask = &stage;
  for (int repeat = 0; repeat < 2; ++repeat) {
    REQUIRE(vkResetFences(runtime.device, 1, &fence) == VK_SUCCESS);
    REQUIRE(vkQueueSubmit(runtime.other, 1, &wait, fence) == VK_SUCCESS);
    REQUIRE(vkWaitForFences(runtime.device, 1, &fence, VK_TRUE, 1000000) == VK_TIMEOUT);
    REQUIRE(vkQueueSubmit(runtime.queue, 1, &signal, VK_NULL_HANDLE) == VK_SUCCESS);
    REQUIRE(vkWaitForFences(runtime.device, 1, &fence, VK_TRUE, 1000000000) == VK_SUCCESS);
  }
  REQUIRE(vkDeviceWaitIdle(runtime.device) == VK_SUCCESS);
  vkDestroyFence(runtime.device, fence, nullptr);
  vkDestroySemaphore(runtime.device, semaphore, nullptr);
}

TEST_CASE("Vulkan host event releases asynchronous work before semaphore and fence signalling") {
  TestDevice runtime;
  VkEventCreateInfo eventInfo{};
  eventInfo.sType = VK_STRUCTURE_TYPE_EVENT_CREATE_INFO;
  VkEvent event;
  REQUIRE(vkCreateEvent(runtime.device, &eventInfo, nullptr, &event) == VK_SUCCESS);
  VkFenceCreateInfo fenceInfo{};
  fenceInfo.sType = VK_STRUCTURE_TYPE_FENCE_CREATE_INFO;
  VkFence fence;
  REQUIRE(vkCreateFence(runtime.device, &fenceInfo, nullptr, &fence) == VK_SUCCESS);
  VkSemaphoreCreateInfo semaphoreInfo{};
  semaphoreInfo.sType = VK_STRUCTURE_TYPE_SEMAPHORE_CREATE_INFO;
  VkSemaphore semaphore;
  REQUIRE(vkCreateSemaphore(runtime.device, &semaphoreInfo, nullptr, &semaphore) == VK_SUCCESS);
  VkMemoryAllocateInfo allocation{};
  allocation.sType = VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO;
  allocation.allocationSize = 4;
  VkDeviceMemory memory;
  REQUIRE(vkAllocateMemory(runtime.device, &allocation, nullptr, &memory) == VK_SUCCESS);
  void *mapped = nullptr;
  REQUIRE(vkMapMemory(runtime.device, memory, 0, 4, 0, &mapped) == VK_SUCCESS);
  VkBufferCreateInfo bufferInfo{};
  bufferInfo.sType = VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO;
  bufferInfo.size = 4;
  VkBuffer buffer;
  REQUIRE(vkCreateBuffer(runtime.device, &bufferInfo, nullptr, &buffer) == VK_SUCCESS);
  REQUIRE(vkBindBufferMemory(runtime.device, buffer, memory, 0) == VK_SUCCESS);
  CommandRecording recording(runtime.device);
  const auto command = recording.command;
  vkCmdWaitEvents(command, 1, &event, VK_PIPELINE_STAGE_HOST_BIT, VK_PIPELINE_STAGE_TRANSFER_BIT, 0, nullptr, 0, nullptr, 0, nullptr);
  vkCmdFillBuffer(command, buffer, 0, 4, 0x12345678);
  REQUIRE(vkEndCommandBuffer(command) == VK_SUCCESS);
  auto submit = submitInfo(command);
  submit.signalSemaphoreCount = 1;
  submit.pSignalSemaphores = &semaphore;
  REQUIRE(vkQueueSubmit(runtime.queue, 1, &submit, VK_NULL_HANDLE) == VK_SUCCESS);
  const VkPipelineStageFlags stage = VK_PIPELINE_STAGE_ALL_COMMANDS_BIT;
  VkSubmitInfo wait{};
  wait.sType = VK_STRUCTURE_TYPE_SUBMIT_INFO;
  wait.waitSemaphoreCount = 1;
  wait.pWaitSemaphores = &semaphore;
  wait.pWaitDstStageMask = &stage;
  REQUIRE(vkQueueSubmit(runtime.other, 1, &wait, fence) == VK_SUCCESS);
  REQUIRE(vkGetFenceStatus(runtime.device, fence) == VK_NOT_READY);
  REQUIRE(vkWaitForFences(runtime.device, 1, &fence, VK_TRUE, 1000000) == VK_TIMEOUT);
  REQUIRE(*static_cast<uint32_t *>(mapped) == 0);
  REQUIRE(vkSetEvent(runtime.device, event) == VK_SUCCESS);
  REQUIRE(vkWaitForFences(runtime.device, 1, &fence, VK_TRUE, 1000000000) == VK_SUCCESS);
  REQUIRE(*static_cast<uint32_t *>(mapped) == 0x12345678);
  REQUIRE(vkDeviceWaitIdle(runtime.device) == VK_SUCCESS);
  vkDestroyBuffer(runtime.device, buffer, nullptr);
  vkUnmapMemory(runtime.device, memory);
  vkFreeMemory(runtime.device, memory, nullptr);
  vkDestroyEvent(runtime.device, event, nullptr);
  vkDestroySemaphore(runtime.device, semaphore, nullptr);
  vkDestroyFence(runtime.device, fence, nullptr);
}

TEST_CASE("Vulkan asynchronous execution failure wakes fence and idle waits") {
  TestDevice runtime;
  VkFenceCreateInfo info{};
  info.sType = VK_STRUCTURE_TYPE_FENCE_CREATE_INFO;
  VkFence fence;
  REQUIRE(vkCreateFence(runtime.device, &info, nullptr, &fence) == VK_SUCCESS);
  const auto command = reinterpret_cast<VkCommandBuffer>(0xa200);
  VkCommandBufferBeginInfo begin{};
  begin.sType = VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO;
  REQUIRE(vkBeginCommandBuffer(command, &begin) == VK_SUCCESS);
  VkEventCreateInfo eventInfo{};
  eventInfo.sType = VK_STRUCTURE_TYPE_EVENT_CREATE_INFO;
  VkEvent event;
  REQUIRE(vkCreateEvent(runtime.device, &eventInfo, nullptr, &event) == VK_SUCCESS);
  const auto waitingCommand = reinterpret_cast<VkCommandBuffer>(0xa210);
  REQUIRE(vkBeginCommandBuffer(waitingCommand, &begin) == VK_SUCCESS);
  vkCmdWaitEvents(waitingCommand, 1, &event, VK_PIPELINE_STAGE_HOST_BIT, VK_PIPELINE_STAGE_ALL_COMMANDS_BIT, 0, nullptr, 0, nullptr, 0,
                  nullptr);
  REQUIRE(vkEndCommandBuffer(waitingCommand) == VK_SUCCESS);
  const auto waiting = submitInfo(waitingCommand);
  REQUIRE(vkQueueSubmit(runtime.other, 1, &waiting, VK_NULL_HANDLE) == VK_SUCCESS);
  // Deliberately force an executor failure, without invoking RPC.
  vkCmdDispatch(command, 1, 1, 1);
  REQUIRE(vkEndCommandBuffer(command) == VK_SUCCESS);
  const auto submit = submitInfo(command);
  REQUIRE(vkQueueSubmit(runtime.queue, 1, &submit, fence) == VK_SUCCESS);
  REQUIRE(vkWaitForFences(runtime.device, 1, &fence, VK_TRUE, 1000000000) == VK_ERROR_DEVICE_LOST);
  REQUIRE(vkGetFenceStatus(runtime.device, fence) == VK_ERROR_DEVICE_LOST);
  REQUIRE(vkQueueWaitIdle(runtime.queue) == VK_ERROR_DEVICE_LOST);
  REQUIRE(vkDeviceWaitIdle(runtime.device) == VK_ERROR_DEVICE_LOST);
  REQUIRE(vkQueueSubmit(runtime.other, 0, nullptr, VK_NULL_HANDLE) == VK_ERROR_DEVICE_LOST);
  vkDestroyFence(runtime.device, fence, nullptr);
  vkDestroyEvent(runtime.device, event, nullptr);
}

TEST_CASE("Vulkan submissions without waits start no covering workers") {
  TestDevice runtime;
  const auto threads = [] { return std::distance(std::filesystem::directory_iterator("/proc/self/task"), {}); };
  const auto before = threads();
  VkSubmitInfo submit{};
  submit.sType = VK_STRUCTURE_TYPE_SUBMIT_INFO;
  for (int i = 0; i < 4; ++i) {
    REQUIRE(vkQueueSubmit(runtime.queue, 1, &submit, VK_NULL_HANDLE) == VK_SUCCESS);
    REQUIRE(vkQueueWaitIdle(runtime.queue) == VK_SUCCESS);
  }
  CHECK(threads() == before + gemstone::CommandQueue::Concurrency);
}

TEST_CASE("Vulkan timeline waits retain values and resume queued work after a host signal") {
  TestDevice runtime;
  VkSemaphoreTypeCreateInfo type{};
  type.sType = VK_STRUCTURE_TYPE_SEMAPHORE_TYPE_CREATE_INFO;
  type.semaphoreType = VK_SEMAPHORE_TYPE_TIMELINE;
  type.initialValue = 2;
  VkSemaphoreCreateInfo create{};
  create.sType = VK_STRUCTURE_TYPE_SEMAPHORE_CREATE_INFO;
  create.pNext = &type;
  VkSemaphore semaphores[2];
  REQUIRE(vkCreateSemaphore(runtime.device, &create, nullptr, &semaphores[0]) == VK_SUCCESS);
  type.initialValue = 4;
  REQUIRE(vkCreateSemaphore(runtime.device, &create, nullptr, &semaphores[1]) == VK_SUCCESS);
  const uint64_t thresholds[]{3, 4};
  VkSemaphoreWaitInfo wait{};
  wait.sType = VK_STRUCTURE_TYPE_SEMAPHORE_WAIT_INFO;
  wait.semaphoreCount = 2;
  wait.pSemaphores = semaphores;
  wait.pValues = thresholds;
  wait.flags = VK_SEMAPHORE_WAIT_ANY_BIT;
  REQUIRE(vkWaitSemaphores(runtime.device, &wait, 0) == VK_SUCCESS);
  wait.flags = 0;
  REQUIRE(vkWaitSemaphores(runtime.device, &wait, 1000000) == VK_TIMEOUT);
  VkSemaphoreSignalInfo signal{};
  signal.sType = VK_STRUCTURE_TYPE_SEMAPHORE_SIGNAL_INFO;
  signal.semaphore = semaphores[0];
  signal.value = 3;
  REQUIRE(vkSignalSemaphore(runtime.device, &signal) == VK_SUCCESS);
  REQUIRE(vkWaitSemaphores(runtime.device, &wait, 0) == VK_SUCCESS);
  REQUIRE(vkWaitSemaphores(runtime.device, &wait, 0) == VK_SUCCESS); // Timeline waits do not consume values.
  uint64_t value = 0;
  REQUIRE(vkGetSemaphoreCounterValue(runtime.device, semaphores[0], &value) == VK_SUCCESS);
  REQUIRE(value == 3);

  const uint64_t waitedValue = 5, signalledValue = 7;
  VkTimelineSemaphoreSubmitInfo timeline{};
  timeline.sType = VK_STRUCTURE_TYPE_TIMELINE_SEMAPHORE_SUBMIT_INFO;
  timeline.waitSemaphoreValueCount = timeline.signalSemaphoreValueCount = 1;
  timeline.pWaitSemaphoreValues = &waitedValue;
  timeline.pSignalSemaphoreValues = &signalledValue;
  const VkPipelineStageFlags stage = VK_PIPELINE_STAGE_ALL_COMMANDS_BIT;
  VkSubmitInfo submit{};
  submit.sType = VK_STRUCTURE_TYPE_SUBMIT_INFO;
  submit.pNext = &timeline;
  submit.waitSemaphoreCount = submit.signalSemaphoreCount = 1;
  submit.pWaitSemaphores = semaphores;
  submit.pSignalSemaphores = semaphores + 1;
  submit.pWaitDstStageMask = &stage;
  VkFenceCreateInfo fenceInfo{};
  fenceInfo.sType = VK_STRUCTURE_TYPE_FENCE_CREATE_INFO;
  VkFence fence;
  REQUIRE(vkCreateFence(runtime.device, &fenceInfo, nullptr, &fence) == VK_SUCCESS);
  REQUIRE(vkQueueSubmit(runtime.queue, 1, &submit, fence) == VK_SUCCESS);
  REQUIRE(vkWaitForFences(runtime.device, 1, &fence, VK_TRUE, 1000000) == VK_TIMEOUT);
  signal.value = waitedValue;
  REQUIRE(vkSignalSemaphoreKHR(runtime.device, &signal) == VK_SUCCESS);
  REQUIRE(vkWaitForFences(runtime.device, 1, &fence, VK_TRUE, 1000000000) == VK_SUCCESS);
  REQUIRE(vkGetSemaphoreCounterValueKHR(runtime.device, semaphores[1], &value) == VK_SUCCESS);
  REQUIRE(value == signalledValue);
  REQUIRE(vkDeviceWaitIdle(runtime.device) == VK_SUCCESS);
  vkDestroyFence(runtime.device, fence, nullptr);
  for (auto semaphore : semaphores)
    vkDestroySemaphore(runtime.device, semaphore, nullptr);
}

TEST_CASE("Vulkan timeline queries initialise aggregate features and preserve query chains") {
  VkPhysicalDeviceHostQueryResetFeatures hostReset{};
  hostReset.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_HOST_QUERY_RESET_FEATURES;
  VkPhysicalDeviceTimelineSemaphoreFeatures timeline{};
  timeline.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TIMELINE_SEMAPHORE_FEATURES;
  timeline.pNext = &hostReset;
  VkPhysicalDeviceVulkan12Features aggregate;
  std::memset(&aggregate, 0xff, sizeof(aggregate));
  aggregate.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_2_FEATURES;
  aggregate.pNext = &timeline;
  VkPhysicalDeviceFeatures2 features{};
  features.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2;
  features.pNext = &aggregate;
  vkGetPhysicalDeviceFeatures2(VK_NULL_HANDLE, &features);
  REQUIRE(aggregate.pNext == &timeline);
  REQUIRE(aggregate.timelineSemaphore == VK_TRUE);
  REQUIRE(timeline.timelineSemaphore == VK_TRUE);
  REQUIRE(aggregate.hostQueryReset == VK_TRUE);
  REQUIRE(hostReset.hostQueryReset == VK_TRUE);
  REQUIRE(features.features.depthBounds == VK_TRUE);
  REQUIRE(aggregate.shaderFloat16 == VK_TRUE);
  REQUIRE(aggregate.shaderInt8 == VK_TRUE);
  REQUIRE(aggregate.bufferDeviceAddress == VK_FALSE);
  REQUIRE(aggregate.descriptorIndexing == VK_FALSE);

  VkPhysicalDeviceTimelineSemaphoreProperties limit{};
  limit.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TIMELINE_SEMAPHORE_PROPERTIES;
  VkPhysicalDeviceVulkan12Properties aggregateLimit;
  std::memset(&aggregateLimit, 0xff, sizeof(aggregateLimit));
  aggregateLimit.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_2_PROPERTIES;
  aggregateLimit.pNext = &limit;
  VkPhysicalDeviceProperties2 properties{};
  properties.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PROPERTIES_2;
  properties.pNext = &aggregateLimit;
  vkGetPhysicalDeviceProperties2(VK_NULL_HANDLE, &properties);
  REQUIRE(aggregateLimit.pNext == &limit);
  REQUIRE(aggregateLimit.maxTimelineSemaphoreValueDifference == UINT64_MAX);
  REQUIRE(limit.maxTimelineSemaphoreValueDifference == UINT64_MAX);
  REQUIRE(aggregateLimit.maxDescriptorSetUpdateAfterBindSamplers == 0);
}

TEST_CASE("Vulkan mapped memory preserves advertised base alignment at arbitrary offsets") {
  TestDevice runtime;
  uint32_t count = 1;
  VkPhysicalDevice physical{};
  REQUIRE(vkEnumeratePhysicalDevices(runtime.instance, &count, &physical) == VK_SUCCESS);
  VkPhysicalDeviceProperties properties{};
  vkGetPhysicalDeviceProperties(physical, &properties);
  const auto alignment = properties.limits.minMemoryMapAlignment;
  REQUIRE(alignment >= 64);
  std::vector<VkDeviceMemory> allocations;
  for (VkDeviceSize size : {65, 127, 128, 129, 255, 256, 257, 1024, 4096}) {
    VkMemoryAllocateInfo info{};
    info.sType = VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO;
    info.allocationSize = size;
    VkDeviceMemory memory{};
    REQUIRE(vkAllocateMemory(runtime.device, &info, nullptr, &memory) == VK_SUCCESS);
    allocations.push_back(memory);
    void *mapped{};
    REQUIRE(vkMapMemory(runtime.device, memory, 0, VK_WHOLE_SIZE, 0, &mapped) == VK_SUCCESS);
    std::memset(mapped, 0x5a, size);
    vkUnmapMemory(runtime.device, memory);
    for (VkDeviceSize offset : {0, 1, 17, 63}) {
      CAPTURE(size, offset);
      REQUIRE(vkMapMemory(runtime.device, memory, offset, VK_WHOLE_SIZE, 0, &mapped) == VK_SUCCESS);
      CHECK((reinterpret_cast<uintptr_t>(mapped) - offset) % alignment == 0);
      REQUIRE(static_cast<const uint8_t *>(mapped)[0] == 0x5a);
      REQUIRE(static_cast<const uint8_t *>(mapped)[size - offset - 1] == 0x5a);
      vkUnmapMemory(runtime.device, memory);
    }
  }
  for (auto memory : allocations)
    vkFreeMemory(runtime.device, memory, nullptr);
}

TEST_CASE("Vulkan driver identity queries agree and overwrite poisoned metadata") {
  const auto poison = GENERATE(0xaa, 0xff);
  VkPhysicalDeviceDriverProperties driver;
  std::memset(&driver, poison, sizeof(driver));
  driver.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DRIVER_PROPERTIES;
  driver.pNext = nullptr;
  VkPhysicalDeviceVulkan12Properties aggregate;
  std::memset(&aggregate, poison, sizeof(aggregate));
  aggregate.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_2_PROPERTIES;
  aggregate.pNext = &driver;
  VkPhysicalDeviceProperties2 properties{};
  properties.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PROPERTIES_2;
  properties.pNext = &aggregate;
  vkGetPhysicalDeviceProperties2(VK_NULL_HANDLE, &properties);
  REQUIRE(aggregate.pNext == &driver);
  REQUIRE(driver.pNext == nullptr);
  REQUIRE(driver.driverID == static_cast<VkDriverId>(0x47454d53));
  REQUIRE(driver.conformanceVersion.major == 1);
  REQUIRE(driver.conformanceVersion.minor == 4);
  REQUIRE(driver.conformanceVersion.subminor == 6);
  REQUIRE(driver.conformanceVersion.patch == 2);
  REQUIRE(std::string(driver.driverName) == "Gemstone");
  REQUIRE(std::string(driver.driverInfo) == "Portable SPIR-V interpreter");
  REQUIRE(aggregate.driverID == driver.driverID);
  REQUIRE(aggregate.conformanceVersion.major == driver.conformanceVersion.major);
  REQUIRE(aggregate.conformanceVersion.minor == driver.conformanceVersion.minor);
  REQUIRE(aggregate.conformanceVersion.subminor == driver.conformanceVersion.subminor);
  REQUIRE(aggregate.conformanceVersion.patch == driver.conformanceVersion.patch);
  REQUIRE(std::memcmp(aggregate.driverName, driver.driverName, VK_MAX_DRIVER_NAME_SIZE) == 0);
  REQUIRE(std::memcmp(aggregate.driverInfo, driver.driverInfo, VK_MAX_DRIVER_INFO_SIZE) == 0);
}

TEST_CASE("Vulkan overlapping descriptor copies within one binding retain array elements", "[vulkan][descriptor]") {
  const auto type = GENERATE(VK_DESCRIPTOR_TYPE_STORAGE_BUFFER, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE, VK_DESCRIPTOR_TYPE_STORAGE_TEXEL_BUFFER);
  DescriptorState state;
  const auto set = reinterpret_cast<VkDescriptorSet>(0x400);
  allocateDescriptors(state, set, type, {{2, 3}});
  VkDescriptorBufferInfo buffers[3]{};
  VkDescriptorImageInfo images[3]{};
  VkBufferView texels[3]{};
  for (uintptr_t i = 0; i < 3; ++i) {
    buffers[i] = {reinterpret_cast<VkBuffer>(0x100 + i), 0, 16};
    images[i] = {VK_NULL_HANDLE, reinterpret_cast<VkImageView>(0x100 + i), VK_IMAGE_LAYOUT_GENERAL};
    texels[i] = reinterpret_cast<VkBufferView>(0x100 + i);
  }
  VkWriteDescriptorSet write{};
  write.sType = VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET;
  write.dstSet = set;
  write.dstBinding = 2;
  write.descriptorType = type;
  write.descriptorCount = 3;
  write.pBufferInfo = buffers;
  write.pImageInfo = images;
  write.pTexelBufferView = texels;
  state.update(1, &write, 0, nullptr);
  VkCopyDescriptorSet copy{};
  copy.sType = VK_STRUCTURE_TYPE_COPY_DESCRIPTOR_SET;
  copy.srcSet = copy.dstSet = set;
  copy.srcBinding = copy.dstBinding = 2;
  copy.dstArrayElement = 1;
  copy.descriptorCount = 2;
  state.update(0, nullptr, 1, &copy);
  const auto identity = [&](uint32_t element) {
    if (type == VK_DESCRIPTOR_TYPE_STORAGE_BUFFER) return reinterpret_cast<uintptr_t>(state.get(set, 2, element)->buffer);
    if (type == VK_DESCRIPTOR_TYPE_STORAGE_IMAGE)
      return reinterpret_cast<uintptr_t>(state.get<VkDescriptorImageInfo>(set, 2, element)->imageView);
    return reinterpret_cast<uintptr_t>(*state.get<VkBufferView>(set, 2, element));
  };
  REQUIRE(identity(0) == 0x100);
  REQUIRE(identity(1) == 0x100);
  REQUIRE(identity(2) == 0x101);
}

TEST_CASE("Vulkan R32_UINT clears and padded rectangular transfers preserve surrounding data", "[graphics-execution]") {
  TestDevice runtime;
  const auto layerCount = GENERATE(1u, 3u);
  const auto baseLayer = layerCount - 1;
  const auto imageWord = 48 + baseLayer * 12;
  VkFormatProperties format{};
  vkGetPhysicalDeviceFormatProperties(VK_NULL_HANDLE, VK_FORMAT_R32_UINT, &format);
  REQUIRE((format.optimalTilingFeatures & VK_FORMAT_FEATURE_STORAGE_IMAGE_ATOMIC_BIT) != 0);
  VkImageCreateInfo imageInfo{};
  imageInfo.sType = VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO;
  imageInfo.imageType = VK_IMAGE_TYPE_2D;
  imageInfo.format = VK_FORMAT_R32_UINT;
  imageInfo.tiling = VK_IMAGE_TILING_OPTIMAL;
  imageInfo.extent = {4, 3, 1};
  imageInfo.mipLevels = 1;
  imageInfo.arrayLayers = layerCount;
  imageInfo.samples = VK_SAMPLE_COUNT_1_BIT;
  imageInfo.usage = VK_IMAGE_USAGE_STORAGE_BIT | VK_IMAGE_USAGE_TRANSFER_SRC_BIT | VK_IMAGE_USAGE_TRANSFER_DST_BIT;
  VkImage image;
  REQUIRE(vkCreateImage(runtime.device, &imageInfo, nullptr, &image) == VK_SUCCESS);
  VkMemoryAllocateInfo allocate{};
  allocate.sType = VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO;
  allocate.allocationSize = 384;
  VkDeviceMemory memory;
  REQUIRE(vkAllocateMemory(runtime.device, &allocate, nullptr, &memory) == VK_SUCCESS);
  REQUIRE(vkBindImageMemory(runtime.device, image, memory, 192) == VK_SUCCESS);
  VkBufferCreateInfo bufferInfo{};
  bufferInfo.sType = VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO;
  bufferInfo.size = 128;
  bufferInfo.usage = VK_BUFFER_USAGE_TRANSFER_SRC_BIT | VK_BUFFER_USAGE_TRANSFER_DST_BIT;
  VkBuffer buffer;
  REQUIRE(vkCreateBuffer(runtime.device, &bufferInfo, nullptr, &buffer) == VK_SUCCESS);
  REQUIRE(vkBindBufferMemory(runtime.device, buffer, memory, 16) == VK_SUCCESS);
  void *mapped;
  REQUIRE(vkMapMemory(runtime.device, memory, 0, VK_WHOLE_SIZE, 0, &mapped) == VK_SUCCESS);
  auto *words = static_cast<uint32_t *>(mapped);
  std::fill(words, words + 96, 0xdeadbeefu);
  words[5] = 11;
  words[6] = 12;
  words[9] = 21;
  words[10] = 22;
  CommandRecording recording(runtime.device);
  const auto command = recording.command;
  VkClearColorValue clear{};
  clear.uint32[0] = 0x12345678;
  const VkImageSubresourceRange range{VK_IMAGE_ASPECT_COLOR_BIT, 0, 1, baseLayer, VK_REMAINING_ARRAY_LAYERS};
  vkCmdClearColorImage(command, image, VK_IMAGE_LAYOUT_GENERAL, &clear, 1, &range);
  clear.uint32[0] = 0; // Recording must snapshot the clear value.
  VkBufferImageCopy copy{};
  copy.bufferOffset = 4;
  copy.bufferRowLength = 4;
  copy.bufferImageHeight = 3;
  copy.imageSubresource = {VK_IMAGE_ASPECT_COLOR_BIT, 0, baseLayer, 1};
  copy.imageOffset = {1, 1, 0};
  copy.imageExtent = {2, 2, 1};
  bool invalid = false;
  SECTION("round trip") {}
  SECTION("overflowing buffer offset fails without accessing memory") {
    copy.bufferOffset = UINT64_MAX - 3;
    invalid = true;
  }
  vkCmdCopyBufferToImage(command, buffer, image, VK_IMAGE_LAYOUT_GENERAL, 1, &copy);
  copy.bufferOffset = 64;
  vkCmdCopyImageToBuffer(command, image, VK_IMAGE_LAYOUT_GENERAL, buffer, 1, &copy);
  REQUIRE(vkEndCommandBuffer(command) == VK_SUCCESS);
  REQUIRE(submitAndWait(runtime.queue, command) == (invalid ? VK_ERROR_DEVICE_LOST : VK_SUCCESS));
  if (baseLayer) REQUIRE(words[48] == 0xdeadbeef);
  REQUIRE(words[imageWord] == 0x12345678);
  REQUIRE(words[imageWord + 3] == 0x12345678);
  REQUIRE(words[imageWord + 12] == 0xdeadbeef);
  REQUIRE(words[19] == 0xdeadbeef);
  REQUIRE(words[22] == 0xdeadbeef);
  if (!invalid) {
    REQUIRE(words[20] == 11);
    REQUIRE(words[21] == 12);
    REQUIRE(words[24] == 21);
    REQUIRE(words[25] == 22);
    REQUIRE(words[imageWord + 5] == 11);
    REQUIRE(words[imageWord + 6] == 12);
    REQUIRE(words[imageWord + 9] == 21);
    REQUIRE(words[imageWord + 10] == 22);
  }
  vkDestroyBuffer(runtime.device, buffer, nullptr);
  vkDestroyImage(runtime.device, image, nullptr);
  vkUnmapMemory(runtime.device, memory);
  vkFreeMemory(runtime.device, memory, nullptr);
}

TEST_CASE("Vulkan timestamp queries preserve unavailable values and respect result stride") {
  TestDevice runtime;
  VkBufferCreateInfo bufferInfo{};
  bufferInfo.sType = VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO;
  bufferInfo.size = 80;
  bufferInfo.usage = VK_BUFFER_USAGE_TRANSFER_DST_BIT;
  VkBuffer buffer;
  REQUIRE(vkCreateBuffer(runtime.device, &bufferInfo, nullptr, &buffer) == VK_SUCCESS);
  VkMemoryAllocateInfo memoryInfo{};
  memoryInfo.sType = VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO;
  memoryInfo.allocationSize = 96;
  VkDeviceMemory memory;
  REQUIRE(vkAllocateMemory(runtime.device, &memoryInfo, nullptr, &memory) == VK_SUCCESS);
  REQUIRE(vkBindBufferMemory(runtime.device, buffer, memory, 16) == VK_SUCCESS);
  void *mapped = nullptr;
  REQUIRE(vkMapMemory(runtime.device, memory, 0, VK_WHOLE_SIZE, 0, &mapped) == VK_SUCCESS);
  auto *copied = static_cast<uint64_t *>(mapped);
  std::fill_n(copied, 12, 0xfeed);
  VkQueryPoolCreateInfo poolInfo{};
  poolInfo.sType = VK_STRUCTURE_TYPE_QUERY_POOL_CREATE_INFO;
  poolInfo.queryType = VK_QUERY_TYPE_TIMESTAMP;
  poolInfo.queryCount = 2;
  VkQueryPool queries;
  REQUIRE(vkCreateQueryPool(runtime.device, &poolInfo, nullptr, &queries) == VK_SUCCESS);
  uint64_t result[6];
  std::fill(std::begin(result), std::end(result), 0xfeed);
  const auto flags = VK_QUERY_RESULT_64_BIT | VK_QUERY_RESULT_WITH_AVAILABILITY_BIT;
  REQUIRE(vkGetQueryPoolResults(runtime.device, queries, 0, 2, sizeof(result), result, 24, flags) == VK_NOT_READY);
  REQUIRE(result[0] == 0xfeed);
  REQUIRE(result[1] == 0);
  REQUIRE(result[2] == 0xfeed);
  REQUIRE(result[3] == 0xfeed);
  REQUIRE(result[4] == 0);
  REQUIRE(result[5] == 0xfeed);
  REQUIRE(vkGetQueryPoolResults(runtime.device, queries, 0, 2, 15, result, 24, flags) == VK_ERROR_INITIALIZATION_FAILED);
  CommandRecording recording(runtime.device);
  const auto command = recording.command;
  vkCmdResetQueryPool(command, queries, 0, 2);
  vkCmdWriteTimestamp(command, VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT, queries, 0);
  vkCmdWriteTimestamp(command, VK_PIPELINE_STAGE_BOTTOM_OF_PIPE_BIT, queries, 1);
  vkCmdCopyQueryPoolResults(command, queries, 0, 2, buffer, 8, 24, flags | VK_QUERY_RESULT_WAIT_BIT);
  REQUIRE(vkEndCommandBuffer(command) == VK_SUCCESS);
  const auto submit = submitInfo(command);
  REQUIRE(vkQueueSubmit(runtime.queue, 1, &submit, VK_NULL_HANDLE) == VK_SUCCESS);
  REQUIRE(vkGetQueryPoolResults(runtime.device, queries, 0, 2, sizeof(result), result, 24, flags | VK_QUERY_RESULT_WAIT_BIT) == VK_SUCCESS);
  REQUIRE(result[0] > 0);
  REQUIRE(result[3] >= result[0]);
  REQUIRE(result[1] == 1);
  REQUIRE(result[4] == 1);
  REQUIRE(result[2] == 0xfeed);
  REQUIRE(result[5] == 0xfeed);
  uint32_t narrow[2]{};
  REQUIRE(vkGetQueryPoolResults(runtime.device, queries, 0, 1, sizeof(narrow), narrow, 8, VK_QUERY_RESULT_WITH_AVAILABILITY_BIT)
          == VK_SUCCESS);
  REQUIRE(narrow[0] == static_cast<uint32_t>(result[0]));
  REQUIRE(narrow[1] == 1);
  REQUIRE(vkQueueWaitIdle(runtime.queue) == VK_SUCCESS);
  REQUIRE(copied[3] == result[0]);
  REQUIRE(copied[4] == 1);
  REQUIRE(copied[6] == result[3]);
  REQUIRE(copied[7] == 1);
  for (auto index : {0, 1, 2, 5, 8, 9, 10, 11})
    REQUIRE(copied[index] == 0xfeed);
  // Reset a strict subset without changing its neighbour or unavailable result bytes.
  const auto neighbour = result[3];
  vkResetQueryPool(runtime.device, queries, 0, 1);
  std::fill(std::begin(result), std::end(result), 0xfeed);
  REQUIRE(vkGetQueryPoolResults(runtime.device, queries, 0, 2, sizeof(result), result, 24, flags) == VK_NOT_READY);
  REQUIRE(result[0] == 0xfeed);
  REQUIRE(result[1] == 0);
  REQUIRE(result[3] == neighbour);
  REQUIRE(result[4] == 1);
  vkResetQueryPoolEXT(runtime.device, queries, 1, 1);
  REQUIRE(vkGetQueryPoolResults(runtime.device, queries, 1, 1, sizeof(result), result, 24, flags) == VK_NOT_READY);
  REQUIRE(result[1] == 0);
  REQUIRE(vkQueueSubmit(runtime.queue, 1, &submit, VK_NULL_HANDLE) == VK_SUCCESS);
  REQUIRE(vkQueueWaitIdle(runtime.queue) == VK_SUCCESS);
  REQUIRE(vkGetQueryPoolResults(runtime.device, queries, 0, 2, sizeof(result), result, 24, flags) == VK_SUCCESS);
  REQUIRE(result[1] == 1);
  REQUIRE(result[4] == 1);
  vkUnmapMemory(runtime.device, memory);
  vkDestroyBuffer(runtime.device, buffer, nullptr);
  vkFreeMemory(runtime.device, memory, nullptr);
  vkDestroyQueryPool(runtime.device, queries, nullptr);
}

TEST_CASE("Vulkan image copies preserve bits, row pitches, and recorded regions", "[graphics-execution]") {
  TestDevice runtime;
  VkImageCreateInfo info{};
  info.sType = VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO;
  info.imageType = VK_IMAGE_TYPE_2D;
  info.format = VK_FORMAT_R8G8B8A8_UNORM;
  info.extent = {8, 4, 1};
  info.mipLevels = info.arrayLayers = 1;
  info.samples = VK_SAMPLE_COUNT_1_BIT;
  info.tiling = VK_IMAGE_TILING_LINEAR;
  info.usage = VK_IMAGE_USAGE_TRANSFER_SRC_BIT | VK_IMAGE_USAGE_TRANSFER_DST_BIT;
  VkImage src, dst;
  REQUIRE(vkCreateImage(runtime.device, &info, nullptr, &src) == VK_SUCCESS);
  REQUIRE(vkCreateImage(runtime.device, &info, nullptr, &dst) == VK_SUCCESS);
  VkMemoryAllocateInfo allocation{};
  allocation.sType = VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO;
  allocation.allocationSize = 384;
  VkDeviceMemory memory;
  REQUIRE(vkAllocateMemory(runtime.device, &allocation, nullptr, &memory) == VK_SUCCESS);
  REQUIRE(vkBindImageMemory(runtime.device, src, memory, 64) == VK_SUCCESS);
  REQUIRE(vkBindImageMemory(runtime.device, dst, memory, 256) == VK_SUCCESS);
  void *mapped = nullptr;
  REQUIRE(vkMapMemory(runtime.device, memory, 0, VK_WHOLE_SIZE, 0, &mapped) == VK_SUCCESS);
  auto *bytes = static_cast<uint8_t *>(mapped);
  std::fill_n(bytes, 384, 0xa5);
  for (unsigned i = 0; i < 128; ++i)
    bytes[64 + i] = static_cast<uint8_t>(i);
  VkImageSubresource subresource{VK_IMAGE_ASPECT_COLOR_BIT, 0, 0};
  VkSubresourceLayout layout{};
  vkGetImageSubresourceLayout(runtime.device, dst, &subresource, &layout);
  REQUIRE(layout.offset == 0); // Image-relative, independent of the memory binding offset.
  REQUIRE(layout.size == 128);
  REQUIRE(layout.rowPitch == 32);
  CommandRecording recording(runtime.device);
  const auto command = recording.command;
  VkImageCopy region{{VK_IMAGE_ASPECT_COLOR_BIT, 0, 0, 1}, {2, 1, 0}, {VK_IMAGE_ASPECT_COLOR_BIT, 0, 0, 1}, {1, 0, 0}, {3, 2, 1}};
  vkCmdCopyImage(command, src, VK_IMAGE_LAYOUT_GENERAL, dst, VK_IMAGE_LAYOUT_GENERAL, 1, &region);
  region.extent = {1, 1, 1}; // Host changes after recording must not alter the command.
  REQUIRE(vkEndCommandBuffer(command) == VK_SUCCESS);
  REQUIRE(submitAndWait(runtime.queue, command) == VK_SUCCESS);
  for (unsigned i = 0; i < 128; ++i) {
    const auto y = i / 32, x = i % 32;
    const auto expected = y < 2 && x >= 4 && x < 16 ? (y + 1) * 32 + x + 4 : 0xa5;
    REQUIRE(bytes[256 + i] == expected);
  }
  REQUIRE(bytes[255] == 0xa5);
  vkDestroyImage(runtime.device, src, nullptr);
  vkDestroyImage(runtime.device, dst, nullptr);
  vkUnmapMemory(runtime.device, memory);
  vkFreeMemory(runtime.device, memory, nullptr);
}

TEST_CASE("Vulkan 2D array views address volume mip slices and bound framebuffer clears", "[vulkan][image][graphics-execution]") {
  const uint32_t mip = GENERATE(0u, 1u);
  const uint32_t framebufferLayers = GENERATE(1u, 2u);
  const bool imageless = GENERATE(false, true);
  const bool extendedUsage = GENERATE(false, true);
  TestDevice runtime;
  VkImageCreateInfo info{};
  info.sType = VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO;
  info.flags = VK_IMAGE_CREATE_2D_ARRAY_COMPATIBLE_BIT;
  if (extendedUsage) info.flags |= VK_IMAGE_CREATE_MUTABLE_FORMAT_BIT | VK_IMAGE_CREATE_EXTENDED_USAGE_BIT;
  info.imageType = VK_IMAGE_TYPE_3D;
  info.format = extendedUsage ? VK_FORMAT_E5B9G9R9_UFLOAT_PACK32 : VK_FORMAT_R8G8B8A8_UNORM;
  info.extent = {8, 8, 8};
  info.mipLevels = 2;
  info.arrayLayers = 1;
  info.samples = VK_SAMPLE_COUNT_1_BIT;
  info.tiling = VK_IMAGE_TILING_OPTIMAL;
  info.usage = VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT;
  VkImage image{};
  REQUIRE(vkCreateImage(runtime.device, &info, nullptr, &image) == VK_SUCCESS);
  VkMemoryRequirements requirements{};
  vkGetImageMemoryRequirements(runtime.device, image, &requirements);
  VkMemoryAllocateInfo allocation{};
  allocation.sType = VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO;
  allocation.allocationSize = requirements.size + 128;
  VkDeviceMemory memory{};
  REQUIRE(vkAllocateMemory(runtime.device, &allocation, nullptr, &memory) == VK_SUCCESS);
  REQUIRE(vkBindImageMemory(runtime.device, image, memory, 64) == VK_SUCCESS);
  void *mapped{};
  REQUIRE(vkMapMemory(runtime.device, memory, 0, VK_WHOLE_SIZE, 0, &mapped) == VK_SUCCESS);
  auto *bytes = static_cast<uint8_t *>(mapped);
  std::fill_n(bytes, allocation.allocationSize, 0xa5);
  VkImageViewCreateInfo viewInfo{};
  viewInfo.sType = VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO;
  viewInfo.image = image;
  viewInfo.viewType = VK_IMAGE_VIEW_TYPE_2D_ARRAY;
  viewInfo.format = VK_FORMAT_R8G8B8A8_UNORM;
  viewInfo.subresourceRange = {VK_IMAGE_ASPECT_COLOR_BIT, mip, 1, 1, VK_REMAINING_ARRAY_LAYERS};
  VkImageView view{};
  REQUIRE(vkCreateImageView(runtime.device, &viewInfo, nullptr, &view) == VK_SUCCESS);
  VkImageView rejected{};
  viewInfo.subresourceRange.baseArrayLayer = 8u >> mip;
  REQUIRE(vkCreateImageView(runtime.device, &viewInfo, nullptr, &rejected) == VK_ERROR_FORMAT_NOT_SUPPORTED);
  viewInfo.subresourceRange = {VK_IMAGE_ASPECT_COLOR_BIT, 0, 2, 0, 1};
  REQUIRE(vkCreateImageView(runtime.device, &viewInfo, nullptr, &rejected) == VK_ERROR_FORMAT_NOT_SUPPORTED);
  VkAttachmentDescription attachment{};
  attachment.format = viewInfo.format;
  attachment.samples = VK_SAMPLE_COUNT_1_BIT;
  attachment.loadOp = VK_ATTACHMENT_LOAD_OP_CLEAR;
  attachment.storeOp = VK_ATTACHMENT_STORE_OP_STORE;
  attachment.initialLayout = attachment.finalLayout = VK_IMAGE_LAYOUT_GENERAL;
  const VkAttachmentReference colour{0, VK_IMAGE_LAYOUT_GENERAL};
  VkSubpassDescription subpass{};
  subpass.pipelineBindPoint = VK_PIPELINE_BIND_POINT_GRAPHICS;
  subpass.colorAttachmentCount = 1;
  subpass.pColorAttachments = &colour;
  VkRenderPassCreateInfo passInfo{};
  passInfo.sType = VK_STRUCTURE_TYPE_RENDER_PASS_CREATE_INFO;
  passInfo.attachmentCount = 1;
  passInfo.pAttachments = &attachment;
  passInfo.subpassCount = 1;
  passInfo.pSubpasses = &subpass;
  VkRenderPass pass{};
  REQUIRE(vkCreateRenderPass(runtime.device, &passInfo, nullptr, &pass) == VK_SUCCESS);
  VkFramebufferCreateInfo fb{};
  fb.sType = VK_STRUCTURE_TYPE_FRAMEBUFFER_CREATE_INFO;
  fb.renderPass = pass;
  fb.attachmentCount = 1;
  fb.pAttachments = &view;
  fb.width = fb.height = 8u >> mip;
  fb.layers = framebufferLayers;
  VkFramebufferAttachmentImageInfo attachmentInfo{};
  attachmentInfo.sType = VK_STRUCTURE_TYPE_FRAMEBUFFER_ATTACHMENT_IMAGE_INFO;
  attachmentInfo.flags = info.flags;
  attachmentInfo.usage = info.usage;
  attachmentInfo.width = fb.width;
  attachmentInfo.height = fb.height;
  attachmentInfo.layerCount = (8u >> mip) - 1;
  attachmentInfo.viewFormatCount = 1;
  attachmentInfo.pViewFormats = &viewInfo.format;
  VkFramebufferAttachmentsCreateInfo framebufferAttachments{};
  framebufferAttachments.sType = VK_STRUCTURE_TYPE_FRAMEBUFFER_ATTACHMENTS_CREATE_INFO;
  framebufferAttachments.attachmentImageInfoCount = 1;
  framebufferAttachments.pAttachmentImageInfos = &attachmentInfo;
  if (imageless) {
    fb.flags = VK_FRAMEBUFFER_CREATE_IMAGELESS_BIT;
    fb.pNext = &framebufferAttachments;
    // pAttachments must be ignored for imageless framebuffers.
    fb.pAttachments = reinterpret_cast<const VkImageView *>(1);
  }
  VkFramebuffer framebuffer{};
  REQUIRE(vkCreateFramebuffer(runtime.device, &fb, nullptr, &framebuffer) == VK_SUCCESS);
  CommandRecording recording(runtime.device);
  const auto command = recording.command;
  VkClearValue clear{};
  std::fill_n(clear.color.float32, 4, 1.0f);
  VkRenderPassBeginInfo render{};
  render.sType = VK_STRUCTURE_TYPE_RENDER_PASS_BEGIN_INFO;
  render.renderPass = pass;
  render.framebuffer = framebuffer;
  render.renderArea = {{0, 0}, {fb.width, fb.height}};
  render.clearValueCount = 1;
  render.pClearValues = &clear;
  VkImageView boundView = view;
  VkRenderPassAttachmentBeginInfo bound{};
  bound.sType = VK_STRUCTURE_TYPE_RENDER_PASS_ATTACHMENT_BEGIN_INFO;
  bound.attachmentCount = 1;
  bound.pAttachments = &boundView;
  if (imageless) render.pNext = &bound;
  vkCmdBeginRenderPass(command, &render, VK_SUBPASS_CONTENTS_INLINE);
  boundView = VK_NULL_HANDLE; // Recording must own a copy of the attachment list.
  vkCmdEndRenderPass(command);
  REQUIRE(vkEndCommandBuffer(command) == VK_SUCCESS);
  REQUIRE(submitAndWait(runtime.queue, command) == VK_SUCCESS);
  const uint64_t sliceBytes = fb.width * fb.height * 4;
  const uint64_t start = 64 + (mip ? 8 * 8 * 8 * 4 : 0) + sliceBytes;
  for (uint64_t i = 0; i < allocation.allocationSize; ++i)
    REQUIRE(bytes[i] == (i >= start && i < start + framebufferLayers * sliceBytes ? 0xff : 0xa5));
  vkDestroyFramebuffer(runtime.device, framebuffer, nullptr);
  vkDestroyRenderPass(runtime.device, pass, nullptr);
  vkDestroyImageView(runtime.device, view, nullptr);
  vkDestroyImage(runtime.device, image, nullptr);
  vkUnmapMemory(runtime.device, memory);
  vkFreeMemory(runtime.device, memory, nullptr);
}

TEST_CASE("Vulkan image array slices preserve layer offsets and neighbouring layers", "[graphics-execution]") {
  TestDevice runtime;
  VkImageCreateInfo info{};
  info.sType = VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO;
  info.imageType = VK_IMAGE_TYPE_2D;
  info.format = VK_FORMAT_R8G8B8A8_UNORM;
  info.extent = {8, 4, 1};
  info.mipLevels = 1;
  info.arrayLayers = 3;
  info.samples = VK_SAMPLE_COUNT_1_BIT;
  info.tiling = VK_IMAGE_TILING_LINEAR;
  info.usage = VK_IMAGE_USAGE_TRANSFER_SRC_BIT | VK_IMAGE_USAGE_TRANSFER_DST_BIT;
  VkImage src, dst;
  REQUIRE(vkCreateImage(runtime.device, &info, nullptr, &src) == VK_SUCCESS);
  REQUIRE(vkCreateImage(runtime.device, &info, nullptr, &dst) == VK_SUCCESS);
  VkMemoryAllocateInfo allocation{};
  allocation.sType = VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO;
  allocation.allocationSize = 960;
  VkDeviceMemory memory;
  REQUIRE(vkAllocateMemory(runtime.device, &allocation, nullptr, &memory) == VK_SUCCESS);
  REQUIRE(vkBindImageMemory(runtime.device, src, memory, 64) == VK_SUCCESS);
  REQUIRE(vkBindImageMemory(runtime.device, dst, memory, 512) == VK_SUCCESS);
  void *mapped = nullptr;
  REQUIRE(vkMapMemory(runtime.device, memory, 0, VK_WHOLE_SIZE, 0, &mapped) == VK_SUCCESS);
  auto *bytes = static_cast<uint8_t *>(mapped);
  std::fill_n(bytes, 960, 0xa5);
  for (unsigned i = 0; i < 384; ++i)
    bytes[64 + i] = static_cast<uint8_t>(i);
  VkImageSubresource subresource{VK_IMAGE_ASPECT_COLOR_BIT, 0, 2};
  VkSubresourceLayout layout{};
  vkGetImageSubresourceLayout(runtime.device, dst, &subresource, &layout);
  REQUIRE(layout.offset == 256); // Image-relative, independent of the memory binding offset.
  REQUIRE(layout.size == 128);
  REQUIRE(layout.rowPitch == 32);
  VkImageViewCreateInfo viewInfo{};
  viewInfo.sType = VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO;
  viewInfo.image = dst;
  viewInfo.viewType = VK_IMAGE_VIEW_TYPE_2D;
  viewInfo.format = info.format;
  viewInfo.subresourceRange = {VK_IMAGE_ASPECT_COLOR_BIT, 0, 1, 2, 1};
  VkImageView view;
  REQUIRE(vkCreateImageView(runtime.device, &viewInfo, nullptr, &view) == VK_SUCCESS);
  vkDestroyImageView(runtime.device, view, nullptr);
  viewInfo.subresourceRange.baseArrayLayer = 3;
  REQUIRE(vkCreateImageView(runtime.device, &viewInfo, nullptr, &view) == VK_ERROR_FORMAT_NOT_SUPPORTED);
  CommandRecording recording(runtime.device);
  const auto command = recording.command;
  VkImageCopy region{{VK_IMAGE_ASPECT_COLOR_BIT, 0, 1, 1}, {2, 1, 0}, {VK_IMAGE_ASPECT_COLOR_BIT, 0, 2, 1}, {1, 0, 0}, {3, 2, 1}};
  vkCmdCopyImage(command, src, VK_IMAGE_LAYOUT_GENERAL, dst, VK_IMAGE_LAYOUT_GENERAL, 1, &region);
  region.extent = {1, 1, 1}; // Host changes after recording must not alter the command.
  REQUIRE(vkEndCommandBuffer(command) == VK_SUCCESS);
  REQUIRE(submitAndWait(runtime.queue, command) == VK_SUCCESS);
  for (unsigned i = 0; i < 384; ++i) {
    const auto layer = i / 128, y = (i % 128) / 32, x = i % 32;
    const auto expected = layer == 2 && y < 2 && x >= 4 && x < 16 ? 128 + (y + 1) * 32 + x + 4 : 0xa5;
    REQUIRE(bytes[512 + i] == expected);
  }
  REQUIRE(bytes[511] == 0xa5);
  vkDestroyImage(runtime.device, src, nullptr);
  vkDestroyImage(runtime.device, dst, nullptr);
  vkUnmapMemory(runtime.device, memory);
  vkFreeMemory(runtime.device, memory, nullptr);
}

TEST_CASE("Vulkan depth clears and padded copies preserve layer and texel boundaries", "[graphics-execution]") {
  TestDevice runtime;
  const auto format = GENERATE(VK_FORMAT_D16_UNORM, VK_FORMAT_D32_SFLOAT, VK_FORMAT_D32_SFLOAT_S8_UINT);
  const uint32_t width = format == VK_FORMAT_D16_UNORM ? 2 : 4;
  const uint32_t imageStride = format == VK_FORMAT_D32_SFLOAT_S8_UINT ? 8 : width;
  VkImageCreateInfo info{};
  info.sType = VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO;
  info.imageType = VK_IMAGE_TYPE_2D;
  info.format = format;
  info.extent = {4, 3, 1};
  info.mipLevels = 1;
  info.arrayLayers = 2;
  info.samples = VK_SAMPLE_COUNT_1_BIT;
  info.usage = VK_IMAGE_USAGE_TRANSFER_SRC_BIT | VK_IMAGE_USAGE_TRANSFER_DST_BIT | VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT;
  VkImage image;
  REQUIRE(vkCreateImage(runtime.device, &info, nullptr, &image) == VK_SUCCESS);
  VkMemoryRequirements requirements{};
  vkGetImageMemoryRequirements(runtime.device, image, &requirements);
  REQUIRE(requirements.size == 24 * imageStride);
  VkMemoryAllocateInfo allocation{};
  allocation.sType = VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO;
  allocation.allocationSize = 320;
  VkDeviceMemory memory;
  REQUIRE(vkAllocateMemory(runtime.device, &allocation, nullptr, &memory) == VK_SUCCESS);
  REQUIRE(vkBindImageMemory(runtime.device, image, memory, 64) == VK_SUCCESS);
  VkBufferCreateInfo bufferInfo{};
  bufferInfo.sType = VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO;
  bufferInfo.size = 64;
  bufferInfo.usage = VK_BUFFER_USAGE_TRANSFER_DST_BIT;
  VkBuffer buffer;
  REQUIRE(vkCreateBuffer(runtime.device, &bufferInfo, nullptr, &buffer) == VK_SUCCESS);
  REQUIRE(vkBindBufferMemory(runtime.device, buffer, memory, 0) == VK_SUCCESS);
  void *mapped;
  REQUIRE(vkMapMemory(runtime.device, memory, 0, VK_WHOLE_SIZE, 0, &mapped) == VK_SUCCESS);
  auto *bytes = static_cast<uint8_t *>(mapped);
  std::fill_n(bytes, 320, 0xa5);
  CommandRecording recording(runtime.device);
  const auto command = recording.command;
  VkClearDepthStencilValue clear{0.5f, 0};
  VkImageSubresourceRange range{VK_IMAGE_ASPECT_DEPTH_BIT, 0, 1, 1, VK_REMAINING_ARRAY_LAYERS};
  vkCmdClearDepthStencilImage(command, image, VK_IMAGE_LAYOUT_GENERAL, &clear, 1, &range);
  clear.depth = 0.0f;
  VkBufferImageCopy copy{};
  copy.bufferOffset = 4;
  copy.bufferRowLength = 4;
  copy.bufferImageHeight = 3;
  copy.imageSubresource = {VK_IMAGE_ASPECT_DEPTH_BIT, 0, 1, 1};
  copy.imageOffset = {1, 1, 0};
  copy.imageExtent = {2, 2, 1};
  vkCmdCopyImageToBuffer(command, image, VK_IMAGE_LAYOUT_GENERAL, buffer, 1, &copy);
  REQUIRE(vkEndCommandBuffer(command) == VK_SUCCESS);
  REQUIRE(submitAndWait(runtime.queue, command) == VK_SUCCESS);
  const uint32_t expected = width == 2 ? 32768u : 0x3f000000u;
  for (uint32_t row = 0; row < 2; ++row)
    for (uint32_t col = 0; col < 2; ++col) {
      uint32_t word = 0;
      std::memcpy(&word, bytes + 4 + (row * 4 + col) * width, width);
      REQUIRE(word == expected);
    }
  REQUIRE(bytes[3] == 0xa5);
  REQUIRE(bytes[4 + 2 * width] == 0xa5);
  REQUIRE(bytes[63] == 0xa5);
  REQUIRE(bytes[64] == 0xa5);
  REQUIRE(bytes[64 + 12 * imageStride - 1] == 0xa5);
  REQUIRE(bytes[64 + 24 * imageStride] == 0xa5);
  if (format == VK_FORMAT_D32_SFLOAT_S8_UINT)
    for (uint32_t pixel = 0; pixel < 24; ++pixel)
      for (uint32_t byte = 4; byte < 8; ++byte)
        REQUIRE(bytes[64 + pixel * 8 + byte] == 0xa5);
  vkDestroyBuffer(runtime.device, buffer, nullptr);
  vkDestroyImage(runtime.device, image, nullptr);
  vkUnmapMemory(runtime.device, memory);
  vkFreeMemory(runtime.device, memory, nullptr);
}

TEST_CASE("Vulkan multisample clears and resolves preserve sample and layer boundaries", "[graphics-execution]") {
  TestDevice runtime;
  const auto format = GENERATE(VK_FORMAT_R8G8B8A8_UNORM, VK_FORMAT_R8G8B8A8_UINT, VK_FORMAT_R32_UINT, VK_FORMAT_R32_SFLOAT,
                               VK_FORMAT_R32G32_SFLOAT, VK_FORMAT_R32G32B32A32_SFLOAT);
  const unsigned components = format == VK_FORMAT_R32_SFLOAT            ? 1
                              : format == VK_FORMAT_R32G32_SFLOAT       ? 2
                              : format == VK_FORMAT_R32G32B32A32_SFLOAT ? 4
                                                                        : 0;
  const unsigned texelBytes = components ? components * 4 : 4;
  VkImageCreateInfo info{};
  info.sType = VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO;
  info.imageType = VK_IMAGE_TYPE_2D;
  info.format = format;
  info.extent = {2, 1, 1};
  info.mipLevels = 1;
  info.arrayLayers = 2;
  info.samples = VK_SAMPLE_COUNT_4_BIT;
  info.usage = VK_IMAGE_USAGE_TRANSFER_SRC_BIT | VK_IMAGE_USAGE_TRANSFER_DST_BIT;
  VkImage src, dst;
  REQUIRE(vkCreateImage(runtime.device, &info, nullptr, &src) == VK_SUCCESS);
  VkMemoryRequirements requirements{};
  vkGetImageMemoryRequirements(runtime.device, src, &requirements);
  REQUIRE(requirements.size == 16 * texelBytes);
  info.samples = VK_SAMPLE_COUNT_1_BIT;
  info.extent.width = 4;
  REQUIRE(vkCreateImage(runtime.device, &info, nullptr, &dst) == VK_SUCCESS);
  VkMemoryAllocateInfo allocation{};
  allocation.sType = VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO;
  allocation.allocationSize = 768;
  VkDeviceMemory memory;
  REQUIRE(vkAllocateMemory(runtime.device, &allocation, nullptr, &memory) == VK_SUCCESS);
  REQUIRE(vkBindImageMemory(runtime.device, src, memory, 0) == VK_SUCCESS);
  REQUIRE(vkBindImageMemory(runtime.device, dst, memory, 512) == VK_SUCCESS);
  void *mapped;
  REQUIRE(vkMapMemory(runtime.device, memory, 0, VK_WHOLE_SIZE, 0, &mapped) == VK_SUCCESS);
  auto *bytes = static_cast<uint8_t *>(mapped);
  std::fill_n(bytes, 768, 0xa5);
  // Seed distinct internal samples so an incorrect first-sample resolve is observable.
  for (unsigned sample = 0; sample < 4; ++sample)
    for (unsigned lane = 0; lane < (components ? components : 4); ++lane) {
      if (components) {
        const float value = static_cast<float>(sample * 2 + lane) - 4.0f;
        std::memcpy(bytes + 8 * texelBytes + sample * texelBytes + lane * 4, &value, 4);
      } else bytes[32 + sample * 4 + lane] = sample * 64 + lane;
    }
  CommandRecording recording(runtime.device);
  const auto command = recording.command;
  VkClearColorValue clear{};
  if (format == VK_FORMAT_R8G8B8A8_UINT || format == VK_FORMAT_R32_UINT)
    std::fill_n(clear.uint32, 4, format == VK_FORMAT_R32_UINT ? UINT32_MAX : 255u);
  else std::fill_n(clear.float32, 4, components ? -2.5f : 1.0f);
  const VkImageSubresourceRange range{VK_IMAGE_ASPECT_COLOR_BIT, 0, 1, 0, 1};
  vkCmdClearColorImage(command, src, VK_IMAGE_LAYOUT_GENERAL, &clear, 1, &range);
  VkImageResolve region{{VK_IMAGE_ASPECT_COLOR_BIT, 0, 1, 1}, {0, 0, 0}, {VK_IMAGE_ASPECT_COLOR_BIT, 0, 1, 1}, {1, 0, 0}, {1, 1, 1}};
  vkCmdResolveImage(command, src, VK_IMAGE_LAYOUT_GENERAL, dst, VK_IMAGE_LAYOUT_GENERAL, 1, &region);
  region.extent.width = 2;
  REQUIRE(vkEndCommandBuffer(command) == VK_SUCCESS);
  REQUIRE(submitAndWait(runtime.queue, command) == VK_SUCCESS);
  if (components) {
    for (unsigned word = 0; word < 8 * components; ++word) {
      float value;
      std::memcpy(&value, bytes + word * 4, 4);
      REQUIRE(value == -2.5f);
    }
    for (unsigned lane = 0; lane < components; ++lane) {
      float value;
      std::memcpy(&value, bytes + 512 + 5 * texelBytes + lane * 4, 4);
      REQUIRE(value == static_cast<float>(lane) - 1.0f);
    }
  } else {
    for (unsigned i = 0; i < 32; ++i)
      REQUIRE(bytes[i] == 255);
    for (unsigned lane = 0; lane < 4; ++lane)
      REQUIRE(bytes[532 + lane] == ((format == VK_FORMAT_R8G8B8A8_UINT || format == VK_FORMAT_R32_UINT) ? lane : 96 + lane));
  }
  REQUIRE(bytes[512 + 5 * texelBytes - 1] == 0xa5);
  REQUIRE(bytes[512 + 6 * texelBytes] == 0xa5);
  REQUIRE(bytes[12 * texelBytes] == 0xa5);
  REQUIRE(bytes[16 * texelBytes] == 0xa5);
  vkDestroyImage(runtime.device, src, nullptr);
  vkDestroyImage(runtime.device, dst, nullptr);
  vkUnmapMemory(runtime.device, memory);
  vkFreeMemory(runtime.device, memory, nullptr);
}

TEST_CASE("Vulkan mip clears and copies preserve other levels and layers", "[graphics-execution]") {
  TestDevice runtime;
  const auto format = GENERATE(VK_FORMAT_R32_SFLOAT, VK_FORMAT_D16_UNORM, VK_FORMAT_D32_SFLOAT);
  const bool depth = format != VK_FORMAT_R32_SFLOAT;
  const uint32_t bytesPerTexel = format == VK_FORMAT_D16_UNORM ? 2 : 4;
  const auto aspect = depth ? VK_IMAGE_ASPECT_DEPTH_BIT : VK_IMAGE_ASPECT_COLOR_BIT;
  VkImageCreateInfo info{};
  info.sType = VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO;
  info.imageType = VK_IMAGE_TYPE_2D;
  info.format = format;
  info.extent = {8, 4, 1};
  info.tiling = VK_IMAGE_TILING_LINEAR;
  info.mipLevels = 5;
  info.arrayLayers = 2;
  info.samples = VK_SAMPLE_COUNT_1_BIT;
  info.usage = VK_IMAGE_USAGE_TRANSFER_SRC_BIT | VK_IMAGE_USAGE_TRANSFER_DST_BIT | VK_IMAGE_USAGE_SAMPLED_BIT;
  VkImage image;
  REQUIRE(vkCreateImage(runtime.device, &info, nullptr, &image) == VK_ERROR_FORMAT_NOT_SUPPORTED);
  info.mipLevels = 4;
  REQUIRE(vkCreateImage(runtime.device, &info, nullptr, &image) == VK_SUCCESS);
  VkMemoryRequirements requirements{};
  vkGetImageMemoryRequirements(runtime.device, image, &requirements);
  REQUIRE(requirements.size == 86 * bytesPerTexel);
  VkImageSubresource subresource{aspect, 2, 1};
  VkSubresourceLayout layout{};
  vkGetImageSubresourceLayout(runtime.device, image, &subresource, &layout);
  REQUIRE(layout.offset == 82 * bytesPerTexel);
  REQUIRE(layout.size == 2 * bytesPerTexel);
  REQUIRE(layout.rowPitch == 2 * bytesPerTexel);
  VkImageViewCreateInfo viewInfo{};
  viewInfo.sType = VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO;
  viewInfo.image = image;
  viewInfo.viewType = VK_IMAGE_VIEW_TYPE_2D;
  viewInfo.format = format;
  viewInfo.subresourceRange = {aspect, 2, VK_REMAINING_MIP_LEVELS, 1, 1};
  VkImageView view;
  REQUIRE(vkCreateImageView(runtime.device, &viewInfo, nullptr, &view) == VK_SUCCESS);
  VkMemoryAllocateInfo allocation{};
  allocation.sType = VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO;
  allocation.allocationSize = 1024;
  VkDeviceMemory memory;
  REQUIRE(vkAllocateMemory(runtime.device, &allocation, nullptr, &memory) == VK_SUCCESS);
  REQUIRE(vkBindImageMemory(runtime.device, image, memory, 256) == VK_SUCCESS);
  VkBufferCreateInfo bufferInfo{};
  bufferInfo.sType = VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO;
  bufferInfo.size = 128;
  bufferInfo.usage = VK_BUFFER_USAGE_TRANSFER_DST_BIT;
  VkBuffer buffer;
  REQUIRE(vkCreateBuffer(runtime.device, &bufferInfo, nullptr, &buffer) == VK_SUCCESS);
  REQUIRE(vkBindBufferMemory(runtime.device, buffer, memory, 0) == VK_SUCCESS);
  void *mapped;
  REQUIRE(vkMapMemory(runtime.device, memory, 0, VK_WHOLE_SIZE, 0, &mapped) == VK_SUCCESS);
  auto *bytes = static_cast<uint8_t *>(mapped);
  std::fill_n(bytes, 1024, 0xa5);
  CommandRecording recording(runtime.device);
  const auto command = recording.command;
  // The overlapping ranges share storage and must survive one batched submission.
  VkImageSubresourceRange ranges[]{{aspect, 1, VK_REMAINING_MIP_LEVELS, 1, 1}, {aspect, 2, 1, 1, 1}};
  VkClearColorValue colour{};
  colour.float32[0] = .5f;
  const VkClearDepthStencilValue clearDepth{.5f, 0};
  if (depth) vkCmdClearDepthStencilImage(command, image, VK_IMAGE_LAYOUT_GENERAL, &clearDepth, 2, ranges);
  else vkCmdClearColorImage(command, image, VK_IMAGE_LAYOUT_GENERAL, &colour, 2, ranges);
  ranges[0] = ranges[1] = {}; // Recording owns the caller-provided ranges.
  VkImageCopy copy{{aspect, 1, 1, 1}, {1, 0, 0}, {aspect, 0, 0, 1}, {1, 1, 0}, {2, 2, 1}};
  vkCmdCopyImage(command, image, VK_IMAGE_LAYOUT_GENERAL, image, VK_IMAGE_LAYOUT_GENERAL, 1, &copy);
  copy = {}; // Copy regions are also snapshots, not borrowed caller storage.
  VkBufferImageCopy readback{};
  readback.bufferOffset = 8;
  readback.bufferRowLength = 3;
  readback.imageSubresource = {aspect, 1, 1, 1};
  readback.imageOffset = {1, 0, 0};
  readback.imageExtent = {2, 2, 1};
  vkCmdCopyImageToBuffer(command, image, VK_IMAGE_LAYOUT_GENERAL, buffer, 1, &readback);
  REQUIRE(vkEndCommandBuffer(command) == VK_SUCCESS);
  REQUIRE(submitAndWait(runtime.queue, command) == VK_SUCCESS);
  const uint32_t expected = bytesPerTexel == 2 ? 32768 : 0x3f000000;
  for (unsigned texel = 0; texel < 86; ++texel) {
    const bool changed =
        texel == 9 || texel == 10 || texel == 17 || texel == 18 || (texel >= 72 && texel < 80) || texel == 82 || texel == 83 || texel == 85;
    uint32_t word = 0;
    std::memcpy(&word, bytes + 256 + texel * bytesPerTexel, bytesPerTexel);
    REQUIRE(word == (changed ? expected : bytesPerTexel == 2 ? 0xa5a5u : 0xa5a5a5a5u));
  }
  for (unsigned row = 0; row < 2; ++row)
    for (unsigned col = 0; col < 3; ++col) {
      uint32_t word = 0;
      std::memcpy(&word, bytes + 8 + (row * 3 + col) * bytesPerTexel, bytesPerTexel);
      REQUIRE(word == (col < 2 ? expected : bytesPerTexel == 2 ? 0xa5a5u : 0xa5a5a5a5u));
    }
  REQUIRE(bytes[255] == 0xa5);
  REQUIRE(bytes[256 + 86 * bytesPerTexel] == 0xa5);
  vkDestroyImageView(runtime.device, view, nullptr);
  vkDestroyBuffer(runtime.device, buffer, nullptr);
  vkDestroyImage(runtime.device, image, nullptr);
  vkUnmapMemory(runtime.device, memory);
  vkFreeMemory(runtime.device, memory, nullptr);
}

TEST_CASE("Vulkan subpass resolves and multisample depth clears preserve the render area and other layers", "[graphics-execution]") {
  TestDevice runtime;
  const auto format = GENERATE(VK_FORMAT_R8G8B8A8_UNORM, VK_FORMAT_R8G8B8A8_UINT, VK_FORMAT_R32_UINT, VK_FORMAT_R32_SFLOAT);
  const auto depthFormat = GENERATE(VK_FORMAT_D16_UNORM, VK_FORMAT_D32_SFLOAT);
  const uint32_t depthBytes = depthFormat == VK_FORMAT_D16_UNORM ? 2 : 4;
  VkMemoryAllocateInfo allocation{};
  allocation.sType = VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO;
  allocation.allocationSize = 4096;
  VkDeviceMemory memory;
  REQUIRE(vkAllocateMemory(runtime.device, &allocation, nullptr, &memory) == VK_SUCCESS);
  std::array<VkImage, 3> images{};
  std::array<VkImageView, 3> views{};
  std::array<VkAttachmentDescription, 3> attachments{};
  const std::array<uint32_t, 3> offsets{256, 1536, 2048};
  for (size_t i = 0; i < 3; ++i) {
    VkImageCreateInfo info{};
    info.sType = VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO;
    info.imageType = VK_IMAGE_TYPE_2D;
    info.format = i == 2 ? depthFormat : format;
    info.extent = {8, 4, 1};
    info.mipLevels = 1;
    info.arrayLayers = 2;
    info.samples = i == 1 ? VK_SAMPLE_COUNT_1_BIT : VK_SAMPLE_COUNT_4_BIT;
    info.usage = i == 2 ? VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT : VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT;
    REQUIRE(vkCreateImage(runtime.device, &info, nullptr, &images[i]) == VK_SUCCESS);
    REQUIRE(vkBindImageMemory(runtime.device, images[i], memory, offsets[i]) == VK_SUCCESS);
    VkImageViewCreateInfo view{};
    view.sType = VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO;
    view.image = images[i];
    view.viewType = VK_IMAGE_VIEW_TYPE_2D;
    view.format = info.format;
    view.subresourceRange = {i == 2 ? VK_IMAGE_ASPECT_DEPTH_BIT : VK_IMAGE_ASPECT_COLOR_BIT, 0, 1, 1, 1};
    REQUIRE(vkCreateImageView(runtime.device, &view, nullptr, &views[i]) == VK_SUCCESS);
    attachments[i].format = info.format;
    attachments[i].samples = info.samples;
    attachments[i].loadOp = i == 2 ? VK_ATTACHMENT_LOAD_OP_CLEAR : VK_ATTACHMENT_LOAD_OP_LOAD;
    attachments[i].storeOp = VK_ATTACHMENT_STORE_OP_STORE;
    attachments[i].initialLayout = attachments[i].finalLayout = VK_IMAGE_LAYOUT_GENERAL;
  }
  void *mapped;
  REQUIRE(vkMapMemory(runtime.device, memory, 0, VK_WHOLE_SIZE, 0, &mapped) == VK_SUCCESS);
  auto *bytes = static_cast<uint8_t *>(mapped);
  std::fill_n(bytes, 4096, 0xa5);
  for (unsigned pixel = 0; pixel < 64; ++pixel)
    for (unsigned sample = 0; sample < 4; ++sample) {
      auto *at = bytes + 256 + (pixel * 4 + sample) * 4;
      if (format == VK_FORMAT_R32_SFLOAT) {
        const float value = sample * .25f;
        std::memcpy(at, &value, 4);
      } else std::fill_n(at, 4, (format == VK_FORMAT_R8G8B8A8_UINT || format == VK_FORMAT_R32_UINT) ? 17 + sample * 10 : sample * 64);
    }
  const VkAttachmentReference colour{0, VK_IMAGE_LAYOUT_GENERAL}, resolve{1, VK_IMAGE_LAYOUT_GENERAL}, depth{2, VK_IMAGE_LAYOUT_GENERAL};
  VkSubpassDescription subpass{};
  subpass.pipelineBindPoint = VK_PIPELINE_BIND_POINT_GRAPHICS;
  subpass.colorAttachmentCount = 1;
  subpass.pColorAttachments = &colour;
  subpass.pResolveAttachments = &resolve;
  subpass.pDepthStencilAttachment = &depth;
  VkRenderPassCreateInfo passInfo{};
  passInfo.sType = VK_STRUCTURE_TYPE_RENDER_PASS_CREATE_INFO;
  passInfo.attachmentCount = 3;
  passInfo.pAttachments = attachments.data();
  passInfo.subpassCount = 1;
  passInfo.pSubpasses = &subpass;
  VkRenderPass pass;
  REQUIRE(vkCreateRenderPass(runtime.device, &passInfo, nullptr, &pass) == VK_SUCCESS);
  VkFramebufferCreateInfo framebufferInfo{};
  framebufferInfo.sType = VK_STRUCTURE_TYPE_FRAMEBUFFER_CREATE_INFO;
  framebufferInfo.renderPass = pass;
  framebufferInfo.attachmentCount = 3;
  framebufferInfo.pAttachments = views.data();
  framebufferInfo.width = 8;
  framebufferInfo.height = 4;
  framebufferInfo.layers = 1;
  VkFramebuffer framebuffer;
  REQUIRE(vkCreateFramebuffer(runtime.device, &framebufferInfo, nullptr, &framebuffer) == VK_SUCCESS);
  CommandRecording recording(runtime.device);
  const auto command = recording.command;
  std::array<VkClearValue, 3> clear{};
  clear[2].depthStencil.depth = .5f;
  VkRenderPassBeginInfo render{};
  render.sType = VK_STRUCTURE_TYPE_RENDER_PASS_BEGIN_INFO;
  render.renderPass = pass;
  render.framebuffer = framebuffer;
  render.renderArea = {{2, 1}, {3, 2}};
  render.clearValueCount = 3;
  render.pClearValues = clear.data();
  vkCmdBeginRenderPass(command, &render, VK_SUBPASS_CONTENTS_INLINE);
  clear[2].depthStencil.depth = 1;
  vkCmdEndRenderPass(command);
  REQUIRE(vkEndCommandBuffer(command) == VK_SUCCESS);
  REQUIRE(submitAndWait(runtime.queue, command) == VK_SUCCESS);
  for (unsigned pixel = 0; pixel < 64; ++pixel) {
    const auto x = pixel % 8, y = pixel / 8 % 4;
    const bool changed = pixel >= 32 && x >= 2 && x < 5 && y >= 1 && y < 3;
    uint32_t actual;
    std::memcpy(&actual, bytes + 1536 + pixel * 4, 4);
    const uint32_t expected = format == VK_FORMAT_R32_SFLOAT                                        ? 0x3ec00000
                              : (format == VK_FORMAT_R8G8B8A8_UINT || format == VK_FORMAT_R32_UINT) ? 0x11111111
                                                                                                    : 0x60606060;
    REQUIRE(actual == (changed ? expected : 0xa5a5a5a5));
    for (unsigned sample = 0; sample < 4; ++sample) {
      actual = 0;
      std::memcpy(&actual, bytes + 2048 + (pixel * 4 + sample) * depthBytes, depthBytes);
      REQUIRE(actual == (changed ? (depthBytes == 2 ? 0x8000u : 0x3f000000u) : (depthBytes == 2 ? 0xa5a5u : 0xa5a5a5a5u)));
    }
  }
  for (const auto guard : {255, 1280, 1535, 1792, 2047, 3072, 4095})
    REQUIRE(bytes[guard] == 0xa5);
  vkDestroyFramebuffer(runtime.device, framebuffer, nullptr);
  vkDestroyRenderPass(runtime.device, pass, nullptr);
  for (size_t i = 0; i < 3; ++i) {
    vkDestroyImageView(runtime.device, views[i], nullptr);
    vkDestroyImage(runtime.device, images[i], nullptr);
  }
  vkUnmapMemory(runtime.device, memory);
  vkFreeMemory(runtime.device, memory, nullptr);
}

TEST_CASE("Vulkan promoted feature queries initialise poisoned payloads and preserve unknown chains") {
  const auto check = []<typename T>(VkStructureType type, VkBool32 T::*first, VkBool32 T::*last, VkBool32 firstValue = VK_FALSE,
                                    VkBool32 lastValue = VK_FALSE) {
    for (auto query : {vkGetPhysicalDeviceFeatures2, vkGetPhysicalDeviceFeatures2KHR}) {
      struct Unknown {
        VkStructureType sType;
        void *pNext;
        uint32_t sentinel;
      } unknown{static_cast<VkStructureType>(0x7ffffffe), nullptr, 0x12345678};
      T payload;
      std::memset(&payload, 0xa5, sizeof(payload));
      payload.sType = type;
      payload.pNext = &unknown;
      VkPhysicalDeviceFeatures2 root{};
      root.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2;
      root.pNext = &payload;
      query(VK_NULL_HANDLE, &root);
      REQUIRE(root.pNext == &payload);
      REQUIRE(payload.sType == type);
      REQUIRE(payload.pNext == &unknown);
      REQUIRE(payload.*first == firstValue);
      REQUIRE(payload.*last == lastValue);
      REQUIRE(unknown.sentinel == 0x12345678);
      REQUIRE(unknown.pNext == nullptr);
    }
  };
  check(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_1_FEATURES, &VkPhysicalDeviceVulkan11Features::storageBuffer16BitAccess,
        &VkPhysicalDeviceVulkan11Features::shaderDrawParameters, VK_TRUE);
  check(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_16BIT_STORAGE_FEATURES, &VkPhysicalDevice16BitStorageFeatures::storageBuffer16BitAccess,
        &VkPhysicalDevice16BitStorageFeatures::storageInputOutput16, VK_TRUE);
  check(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MULTIVIEW_FEATURES, &VkPhysicalDeviceMultiviewFeatures::multiview,
        &VkPhysicalDeviceMultiviewFeatures::multiviewTessellationShader, VK_TRUE);
  check(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VARIABLE_POINTERS_FEATURES,
        &VkPhysicalDeviceVariablePointersFeatures::variablePointersStorageBuffer,
        &VkPhysicalDeviceVariablePointersFeatures::variablePointers);
  check(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PROTECTED_MEMORY_FEATURES, &VkPhysicalDeviceProtectedMemoryFeatures::protectedMemory,
        &VkPhysicalDeviceProtectedMemoryFeatures::protectedMemory);
  check(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SAMPLER_YCBCR_CONVERSION_FEATURES,
        &VkPhysicalDeviceSamplerYcbcrConversionFeatures::samplerYcbcrConversion,
        &VkPhysicalDeviceSamplerYcbcrConversionFeatures::samplerYcbcrConversion);
  check(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_DRAW_PARAMETERS_FEATURES,
        &VkPhysicalDeviceShaderDrawParametersFeatures::shaderDrawParameters,
        &VkPhysicalDeviceShaderDrawParametersFeatures::shaderDrawParameters);
  check(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_8BIT_STORAGE_FEATURES, &VkPhysicalDevice8BitStorageFeatures::storageBuffer8BitAccess,
        &VkPhysicalDevice8BitStorageFeatures::storagePushConstant8);
  check(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_ATOMIC_INT64_FEATURES,
        &VkPhysicalDeviceShaderAtomicInt64Features::shaderBufferInt64Atomics,
        &VkPhysicalDeviceShaderAtomicInt64Features::shaderSharedInt64Atomics);
  check(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DESCRIPTOR_INDEXING_FEATURES,
        &VkPhysicalDeviceDescriptorIndexingFeatures::shaderInputAttachmentArrayDynamicIndexing,
        &VkPhysicalDeviceDescriptorIndexingFeatures::runtimeDescriptorArray);
  check(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SCALAR_BLOCK_LAYOUT_FEATURES, &VkPhysicalDeviceScalarBlockLayoutFeatures::scalarBlockLayout,
        &VkPhysicalDeviceScalarBlockLayoutFeatures::scalarBlockLayout);
  check(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_IMAGELESS_FRAMEBUFFER_FEATURES,
        &VkPhysicalDeviceImagelessFramebufferFeatures::imagelessFramebuffer,
        &VkPhysicalDeviceImagelessFramebufferFeatures::imagelessFramebuffer, VK_TRUE, VK_TRUE);
  check(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_UNIFORM_BUFFER_STANDARD_LAYOUT_FEATURES,
        &VkPhysicalDeviceUniformBufferStandardLayoutFeatures::uniformBufferStandardLayout,
        &VkPhysicalDeviceUniformBufferStandardLayoutFeatures::uniformBufferStandardLayout, VK_TRUE, VK_TRUE);
  check(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_SUBGROUP_EXTENDED_TYPES_FEATURES,
        &VkPhysicalDeviceShaderSubgroupExtendedTypesFeatures::shaderSubgroupExtendedTypes,
        &VkPhysicalDeviceShaderSubgroupExtendedTypesFeatures::shaderSubgroupExtendedTypes, VK_TRUE, VK_TRUE);
  check(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SEPARATE_DEPTH_STENCIL_LAYOUTS_FEATURES,
        &VkPhysicalDeviceSeparateDepthStencilLayoutsFeatures::separateDepthStencilLayouts,
        &VkPhysicalDeviceSeparateDepthStencilLayoutsFeatures::separateDepthStencilLayouts, VK_TRUE, VK_TRUE);
  check(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_BUFFER_DEVICE_ADDRESS_FEATURES, &VkPhysicalDeviceBufferDeviceAddressFeatures::bufferDeviceAddress,
        &VkPhysicalDeviceBufferDeviceAddressFeatures::bufferDeviceAddressMultiDevice);
  check(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_MEMORY_MODEL_FEATURES, &VkPhysicalDeviceVulkanMemoryModelFeatures::vulkanMemoryModel,
        &VkPhysicalDeviceVulkanMemoryModelFeatures::vulkanMemoryModelAvailabilityVisibilityChains);
}

TEST_CASE("Vulkan sparse capability queries enumerate no records without overwriting caller storage") {
  TestDevice runtime;
  for (const char *name : {"vkGetPhysicalDeviceSparseImageFormatProperties", "vkGetPhysicalDeviceSparseImageFormatProperties2",
                           "vkGetImageSparseMemoryRequirements", "vkGetImageSparseMemoryRequirements2"})
    REQUIRE(vkGetInstanceProcAddr(runtime.instance, name) != nullptr);
  VkSparseImageFormatProperties oldProperties;
  std::memset(&oldProperties, 0xa5, sizeof(oldProperties));
  const auto expectedOld = oldProperties;
  uint32_t count = 71;
  vkGetPhysicalDeviceSparseImageFormatProperties(VK_NULL_HANDLE, VK_FORMAT_R8G8B8A8_UNORM, VK_IMAGE_TYPE_2D, VK_SAMPLE_COUNT_1_BIT,
                                                 VK_IMAGE_USAGE_SAMPLED_BIT, VK_IMAGE_TILING_OPTIMAL, &count, nullptr);
  REQUIRE(count == 0);
  count = 1;
  vkGetPhysicalDeviceSparseImageFormatProperties(VK_NULL_HANDLE, VK_FORMAT_R8G8B8A8_UNORM, VK_IMAGE_TYPE_2D, VK_SAMPLE_COUNT_1_BIT,
                                                 VK_IMAGE_USAGE_SAMPLED_BIT, VK_IMAGE_TILING_OPTIMAL, &count, &oldProperties);
  REQUIRE(count == 0);
  REQUIRE(std::memcmp(&oldProperties, &expectedOld, sizeof(oldProperties)) == 0);
  VkPhysicalDeviceSparseImageFormatInfo2 info{};
  info.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SPARSE_IMAGE_FORMAT_INFO_2;
  info.format = VK_FORMAT_R8G8B8A8_UNORM;
  info.type = VK_IMAGE_TYPE_2D;
  info.samples = VK_SAMPLE_COUNT_1_BIT;
  info.usage = VK_IMAGE_USAGE_SAMPLED_BIT;
  info.tiling = VK_IMAGE_TILING_OPTIMAL;
  for (auto query : {vkGetPhysicalDeviceSparseImageFormatProperties2, vkGetPhysicalDeviceSparseImageFormatProperties2KHR}) {
    VkSparseImageFormatProperties2 properties;
    std::memset(&properties, 0xa5, sizeof(properties));
    properties.sType = VK_STRUCTURE_TYPE_SPARSE_IMAGE_FORMAT_PROPERTIES_2;
    properties.pNext = nullptr;
    const auto expected = properties;
    count = 71;
    query(VK_NULL_HANDLE, &info, &count, nullptr);
    REQUIRE(count == 0);
    count = 1;
    query(VK_NULL_HANDLE, &info, &count, &properties);
    REQUIRE(count == 0);
    REQUIRE(std::memcmp(&properties, &expected, sizeof(properties)) == 0);
  }
  VkImageCreateInfo create{};
  create.sType = VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO;
  create.imageType = VK_IMAGE_TYPE_2D;
  create.format = VK_FORMAT_R8G8B8A8_UNORM;
  create.extent = {1, 1, 1};
  create.mipLevels = create.arrayLayers = 1;
  create.samples = VK_SAMPLE_COUNT_1_BIT;
  create.tiling = VK_IMAGE_TILING_OPTIMAL;
  create.usage = VK_IMAGE_USAGE_SAMPLED_BIT;
  VkImage image;
  REQUIRE(vkCreateImage(VK_NULL_HANDLE, &create, nullptr, &image) == VK_SUCCESS);
  VkSparseImageMemoryRequirements memory;
  std::memset(&memory, 0xa5, sizeof(memory));
  const auto expectedMemory = memory;
  count = 1;
  vkGetImageSparseMemoryRequirements(VK_NULL_HANDLE, image, &count, &memory);
  REQUIRE(count == 0);
  REQUIRE(std::memcmp(&memory, &expectedMemory, sizeof(memory)) == 0);
  VkImageSparseMemoryRequirementsInfo2 imageInfo{};
  imageInfo.sType = VK_STRUCTURE_TYPE_IMAGE_SPARSE_MEMORY_REQUIREMENTS_INFO_2;
  imageInfo.image = image;
  for (auto query : {vkGetImageSparseMemoryRequirements2, vkGetImageSparseMemoryRequirements2KHR}) {
    VkSparseImageMemoryRequirements2 requirements;
    std::memset(&requirements, 0xa5, sizeof(requirements));
    requirements.sType = VK_STRUCTURE_TYPE_SPARSE_IMAGE_MEMORY_REQUIREMENTS_2;
    requirements.pNext = nullptr;
    const auto expected = requirements;
    count = 71;
    query(VK_NULL_HANDLE, &imageInfo, &count, nullptr);
    REQUIRE(count == 0);
    count = 1;
    query(VK_NULL_HANDLE, &imageInfo, &count, &requirements);
    REQUIRE(count == 0);
    REQUIRE(std::memcmp(&requirements, &expected, sizeof(requirements)) == 0);
  }
  vkDestroyImage(VK_NULL_HANDLE, image, nullptr);
}

TEST_CASE("Vulkan formats preserve storage identity across decoding and depth stencil views", "[vulkan]") {
  using gemstone::execution::GraphicsFormat;
  using gemstone::vulkan::graphicsFormat;
  REQUIRE(graphicsFormat(VK_FORMAT_ETC2_R8G8B8_UNORM_BLOCK) == GraphicsFormat::Etc2R8g8b8UnormBlock);
  REQUIRE(graphicsFormat(VK_FORMAT_ETC2_R8G8B8_SRGB_BLOCK) == GraphicsFormat::Etc2R8g8b8SrgbBlock);
  REQUIRE(graphicsFormat(VK_FORMAT_EAC_R11_UNORM_BLOCK) == GraphicsFormat::EacR11UnormBlock);
  REQUIRE(graphicsFormat(VK_FORMAT_R32_SFLOAT) == GraphicsFormat::R32f);
  REQUIRE(graphicsFormat(VK_FORMAT_D32_SFLOAT) == GraphicsFormat::D32Sfloat);
  REQUIRE(graphicsFormat(VK_FORMAT_D32_SFLOAT_S8_UINT) == GraphicsFormat::D32SfloatS8Depth);
  REQUIRE(graphicsFormat(VK_FORMAT_D32_SFLOAT_S8_UINT, VK_IMAGE_ASPECT_DEPTH_BIT) == GraphicsFormat::D32SfloatS8Depth);
  REQUIRE(graphicsFormat(VK_FORMAT_D32_SFLOAT_S8_UINT, VK_IMAGE_ASPECT_STENCIL_BIT) == GraphicsFormat::D32SfloatS8Stencil);
  REQUIRE(graphicsFormat(VK_FORMAT_S8_UINT) == GraphicsFormat::S8Uint);
  REQUIRE(graphicsFormat(VK_FORMAT_R8G8B8A8_UNORM) == graphicsFormat(VK_FORMAT_A8B8G8R8_UNORM_PACK32));
  REQUIRE(gemstone::vulkan::texelSize(VK_FORMAT_ETC2_R8G8B8_UNORM_BLOCK) == 8);
  REQUIRE(gemstone::vulkan::texelSize(VK_FORMAT_EAC_R11G11_SNORM_BLOCK) == 16);
  REQUIRE(gemstone::vulkan::texelSize(VK_FORMAT_D32_SFLOAT_S8_UINT) == 8);
  REQUIRE(gemstone::vulkan::texelSize(VK_FORMAT_D16_UNORM) == 2);
  REQUIRE(gemstone::vulkan::depthFormat(VK_FORMAT_D32_SFLOAT_S8_UINT));
  REQUIRE(gemstone::vulkan::stencilFormat(VK_FORMAT_D32_SFLOAT_S8_UINT));
  REQUIRE_FALSE(gemstone::vulkan::depthFormat(VK_FORMAT_R32_SFLOAT));
  REQUIRE_FALSE(gemstone::vulkan::stencilFormat(VK_FORMAT_D32_SFLOAT));
  REQUIRE(gemstone::vulkan::texelSize(VK_FORMAT_UNDEFINED) == 0);
  REQUIRE_FALSE(graphicsFormat(VK_FORMAT_UNDEFINED));
  REQUIRE_FALSE(graphicsFormat(static_cast<VkFormat>(-1)));
}

TEST_CASE("Vulkan format capabilities preserve storage and blending exclusions", "[vulkan][format]") {
  struct Case {
    VkFormat format;
    VkFormatFeatureFlags required, forbidden;
  };
  constexpr VkFormatFeatureFlags linear = VK_FORMAT_FEATURE_SAMPLED_IMAGE_FILTER_LINEAR_BIT;
  constexpr VkFormatFeatureFlags blend = VK_FORMAT_FEATURE_COLOR_ATTACHMENT_BLEND_BIT;
  constexpr VkFormatFeatureFlags atomic = VK_FORMAT_FEATURE_STORAGE_IMAGE_ATOMIC_BIT;
  std::vector<Case> cases{{VK_FORMAT_R8G8B8A8_UNORM, blend | VK_FORMAT_FEATURE_STORAGE_IMAGE_BIT, 0},
                          {VK_FORMAT_R8G8B8A8_SNORM, VK_FORMAT_FEATURE_COLOR_ATTACHMENT_BIT | VK_FORMAT_FEATURE_STORAGE_IMAGE_BIT, blend},
                          {VK_FORMAT_R8G8B8A8_SRGB, blend, VK_FORMAT_FEATURE_STORAGE_IMAGE_BIT},
                          {VK_FORMAT_B8G8R8A8_UNORM, blend, VK_FORMAT_FEATURE_STORAGE_IMAGE_BIT},
                          {VK_FORMAT_R32_UINT, VK_FORMAT_FEATURE_COLOR_ATTACHMENT_BIT | VK_FORMAT_FEATURE_STORAGE_IMAGE_BIT, blend},
                          {VK_FORMAT_R16G16_SFLOAT, blend | VK_FORMAT_FEATURE_STORAGE_IMAGE_BIT, 0},
                          {VK_FORMAT_E5B9G9R9_UFLOAT_PACK32, VK_FORMAT_FEATURE_SAMPLED_IMAGE_BIT | linear,
                           VK_FORMAT_FEATURE_COLOR_ATTACHMENT_BIT | VK_FORMAT_FEATURE_STORAGE_IMAGE_BIT},
                          {VK_FORMAT_ETC2_R8G8B8_UNORM_BLOCK, VK_FORMAT_FEATURE_SAMPLED_IMAGE_BIT,
                           VK_FORMAT_FEATURE_COLOR_ATTACHMENT_BIT | VK_FORMAT_FEATURE_STORAGE_IMAGE_BIT},
                          {VK_FORMAT_D32_SFLOAT, VK_FORMAT_FEATURE_DEPTH_STENCIL_ATTACHMENT_BIT,
                           VK_FORMAT_FEATURE_COLOR_ATTACHMENT_BIT | VK_FORMAT_FEATURE_STORAGE_IMAGE_BIT}};
  for (const auto format : {VK_FORMAT_R8_UINT, VK_FORMAT_R8_SINT, VK_FORMAT_R8G8_UINT, VK_FORMAT_R8G8_SINT, VK_FORMAT_R8G8B8A8_UINT,
                            VK_FORMAT_R8G8B8A8_SINT, VK_FORMAT_R16_UINT, VK_FORMAT_R16_SINT, VK_FORMAT_R16G16_UINT, VK_FORMAT_R16G16_SINT,
                            VK_FORMAT_R16G16B16A16_UINT, VK_FORMAT_R16G16B16A16_SINT, VK_FORMAT_R32_UINT, VK_FORMAT_R32_SINT,
                            VK_FORMAT_R32G32_UINT, VK_FORMAT_R32G32_SINT, VK_FORMAT_R32G32B32A32_UINT, VK_FORMAT_R32G32B32A32_SINT}) {
    const bool atomics = format == VK_FORMAT_R32_UINT || format == VK_FORMAT_R32_SINT;
    cases.push_back({format, VK_FORMAT_FEATURE_STORAGE_IMAGE_BIT | (atomics ? atomic : 0), linear | blend | (atomics ? 0 : atomic)});
  }
  for (const auto format :
       {VK_FORMAT_R8_UNORM, VK_FORMAT_R8G8_UNORM, VK_FORMAT_R8G8B8A8_UNORM, VK_FORMAT_R8G8B8A8_SRGB, VK_FORMAT_B8G8R8A8_UNORM,
        VK_FORMAT_B8G8R8A8_SRGB, VK_FORMAT_A8B8G8R8_UNORM_PACK32, VK_FORMAT_A8B8G8R8_SRGB_PACK32})
    cases.push_back({format, linear | blend, 0});
  for (const auto format : {VK_FORMAT_R8_SNORM, VK_FORMAT_R8G8_SNORM, VK_FORMAT_R8G8B8A8_SNORM, VK_FORMAT_A8B8G8R8_SNORM_PACK32})
    cases.push_back({format, linear, blend});
  for (const auto &value : cases) {
    INFO("format " << value.format);
    VkFormatProperties properties{};
    vkGetPhysicalDeviceFormatProperties(VK_NULL_HANDLE, value.format, &properties);
    REQUIRE((properties.optimalTilingFeatures & value.required) == value.required);
    REQUIRE((properties.optimalTilingFeatures & value.forbidden) == 0);
  }
  VkImageFormatProperties image{};
  REQUIRE(vkGetPhysicalDeviceImageFormatProperties(VK_NULL_HANDLE, VK_FORMAT_E5B9G9R9_UFLOAT_PACK32, VK_IMAGE_TYPE_2D,
                                                   VK_IMAGE_TILING_OPTIMAL, VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT, 0, &image)
          == VK_ERROR_FORMAT_NOT_SUPPORTED);
}

TEST_CASE("Vulkan rejected image format queries zero the core result payload") {
  for (auto format : {VK_FORMAT_R8G8B8A8_UNORM, VK_FORMAT_UNDEFINED}) {
    VkImageFormatProperties properties;
    std::memset(&properties, 0xa5, sizeof(properties));
    REQUIRE(vkGetPhysicalDeviceImageFormatProperties(VK_NULL_HANDLE, format, VK_IMAGE_TYPE_2D, VK_IMAGE_TILING_OPTIMAL,
                                                     VK_IMAGE_USAGE_SAMPLED_BIT, VK_IMAGE_CREATE_SPARSE_RESIDENCY_BIT, &properties)
            == VK_ERROR_FORMAT_NOT_SUPPORTED);
    REQUIRE(properties.maxExtent.width == 0);
    REQUIRE(properties.maxExtent.height == 0);
    REQUIRE(properties.maxExtent.depth == 0);
    REQUIRE(properties.maxMipLevels == 0);
    REQUIRE(properties.maxArrayLayers == 0);
    REQUIRE(properties.sampleCounts == 0);
    REQUIRE(properties.maxResourceSize == 0);
  }
}

TEST_CASE("Vulkan aggregate property queries agree with individual promoted properties") {
  VkPhysicalDeviceMultiviewProperties multiview;
  std::memset(&multiview, 0xa5, sizeof(multiview));
  multiview.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MULTIVIEW_PROPERTIES;
  multiview.pNext = nullptr;
  VkPhysicalDeviceIDProperties identity;
  std::memset(&identity, 0xa5, sizeof(identity));
  identity.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ID_PROPERTIES;
  identity.pNext = &multiview;
  VkPhysicalDeviceSubgroupProperties subgroup{};
  subgroup.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SUBGROUP_PROPERTIES;
  subgroup.pNext = &identity;
  VkPhysicalDeviceMaintenance3Properties maintenance{};
  maintenance.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_3_PROPERTIES;
  maintenance.pNext = &subgroup;
  VkPhysicalDeviceVulkan11Properties aggregate;
  std::memset(&aggregate, 0xa5, sizeof(aggregate));
  aggregate.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_1_PROPERTIES;
  aggregate.pNext = &maintenance;
  VkPhysicalDeviceProperties2 root{};
  root.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PROPERTIES_2;
  root.pNext = &aggregate;
  vkGetPhysicalDeviceProperties2(VK_NULL_HANDLE, &root);
  REQUIRE(aggregate.pNext == &maintenance);
  REQUIRE(aggregate.sType == VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_1_PROPERTIES);
  REQUIRE(std::memcmp(aggregate.deviceUUID, identity.deviceUUID, VK_UUID_SIZE) == 0);
  REQUIRE(std::memcmp(aggregate.driverUUID, identity.driverUUID, VK_UUID_SIZE) == 0);
  REQUIRE(identity.deviceNodeMask == 0);
  REQUIRE(identity.deviceLUIDValid == VK_FALSE);
  REQUIRE(aggregate.subgroupSize == subgroup.subgroupSize);
  REQUIRE(aggregate.subgroupSupportedStages == subgroup.supportedStages);
  REQUIRE(aggregate.subgroupSupportedOperations == subgroup.supportedOperations);
  REQUIRE(aggregate.maxPerSetDescriptors == maintenance.maxPerSetDescriptors);
  REQUIRE(aggregate.maxMemoryAllocationSize == maintenance.maxMemoryAllocationSize);
  REQUIRE(aggregate.maxMultiviewViewCount == multiview.maxMultiviewViewCount);
  REQUIRE(multiview.maxMultiviewViewCount == 6);
  REQUIRE(multiview.maxMultiviewInstanceIndex == 134217727);
  REQUIRE(aggregate.protectedNoFault == VK_FALSE);
}

TEST_CASE("Vulkan multiple colour resolves preserve slot permutations and unused attachments", "[graphics-execution]") {
  TestDevice runtime;
  VkMemoryAllocateInfo allocation{};
  allocation.sType = VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO;
  allocation.allocationSize = 1024;
  VkDeviceMemory memory;
  REQUIRE(vkAllocateMemory(runtime.device, &allocation, nullptr, &memory) == VK_SUCCESS);
  std::array<VkImage, 7> images{};
  std::array<VkImageView, 7> views{};
  std::array<VkAttachmentDescription, 7> attachments{};
  const auto multisampled = [](unsigned i) { return i == 0 || i == 3 || i == 5; };
  for (unsigned i = 0; i < images.size(); ++i) {
    VkImageCreateInfo info{};
    info.sType = VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO;
    info.imageType = VK_IMAGE_TYPE_2D;
    info.format = VK_FORMAT_R32_SFLOAT;
    info.extent = {2, 2, 1};
    info.mipLevels = info.arrayLayers = 1;
    info.samples = multisampled(i) ? VK_SAMPLE_COUNT_4_BIT : VK_SAMPLE_COUNT_1_BIT;
    info.usage = VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT;
    REQUIRE(vkCreateImage(runtime.device, &info, nullptr, &images[i]) == VK_SUCCESS);
    REQUIRE(vkBindImageMemory(runtime.device, images[i], memory, i * 128) == VK_SUCCESS);
    VkImageViewCreateInfo view{};
    view.sType = VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO;
    view.image = images[i];
    view.viewType = VK_IMAGE_VIEW_TYPE_2D;
    view.format = info.format;
    view.subresourceRange = {VK_IMAGE_ASPECT_COLOR_BIT, 0, 1, 0, 1};
    REQUIRE(vkCreateImageView(runtime.device, &view, nullptr, &views[i]) == VK_SUCCESS);
    attachments[i].format = info.format;
    attachments[i].samples = info.samples;
    attachments[i].loadOp = VK_ATTACHMENT_LOAD_OP_CLEAR;
    attachments[i].storeOp = VK_ATTACHMENT_STORE_OP_STORE;
    attachments[i].initialLayout = attachments[i].finalLayout = VK_IMAGE_LAYOUT_GENERAL;
  }
  void *mapped;
  REQUIRE(vkMapMemory(runtime.device, memory, 0, VK_WHOLE_SIZE, 0, &mapped) == VK_SUCCESS);
  std::memset(mapped, 0xa5, 1024);
  const std::array<VkAttachmentReference, 4> colours{{{3, VK_IMAGE_LAYOUT_GENERAL},
                                                      {VK_ATTACHMENT_UNUSED, VK_IMAGE_LAYOUT_UNDEFINED},
                                                      {0, VK_IMAGE_LAYOUT_GENERAL},
                                                      {5, VK_IMAGE_LAYOUT_GENERAL}}};
  const std::array<VkAttachmentReference, 4> resolves{{{6, VK_IMAGE_LAYOUT_GENERAL},
                                                       {VK_ATTACHMENT_UNUSED, VK_IMAGE_LAYOUT_UNDEFINED},
                                                       {1, VK_IMAGE_LAYOUT_GENERAL},
                                                       {4, VK_IMAGE_LAYOUT_GENERAL}}};
  VkSubpassDescription subpass{};
  subpass.pipelineBindPoint = VK_PIPELINE_BIND_POINT_GRAPHICS;
  subpass.colorAttachmentCount = colours.size();
  subpass.pColorAttachments = colours.data();
  subpass.pResolveAttachments = resolves.data();
  VkRenderPassCreateInfo passInfo{};
  passInfo.sType = VK_STRUCTURE_TYPE_RENDER_PASS_CREATE_INFO;
  passInfo.attachmentCount = attachments.size();
  passInfo.pAttachments = attachments.data();
  passInfo.subpassCount = 1;
  passInfo.pSubpasses = &subpass;
  VkRenderPass pass;
  REQUIRE(vkCreateRenderPass(runtime.device, &passInfo, nullptr, &pass) == VK_SUCCESS);
  VkFramebufferCreateInfo framebufferInfo{};
  framebufferInfo.sType = VK_STRUCTURE_TYPE_FRAMEBUFFER_CREATE_INFO;
  framebufferInfo.renderPass = pass;
  framebufferInfo.attachmentCount = views.size();
  framebufferInfo.pAttachments = views.data();
  framebufferInfo.width = framebufferInfo.height = 2;
  framebufferInfo.layers = 1;
  VkFramebuffer framebuffer;
  REQUIRE(vkCreateFramebuffer(runtime.device, &framebufferInfo, nullptr, &framebuffer) == VK_SUCCESS);
  CommandRecording recording(runtime.device);
  const auto command = recording.command;
  std::array<VkClearValue, 7> clear{};
  for (unsigned i = 0; i < clear.size(); ++i)
    clear[i].color.float32[0] = i + .5f;
  VkRenderPassBeginInfo render{};
  render.sType = VK_STRUCTURE_TYPE_RENDER_PASS_BEGIN_INFO;
  render.renderPass = pass;
  render.framebuffer = framebuffer;
  render.renderArea = {{1, 0}, {1, 1}};
  render.clearValueCount = clear.size();
  render.pClearValues = clear.data();
  vkCmdBeginRenderPass(command, &render, VK_SUBPASS_CONTENTS_INLINE);
  vkCmdEndRenderPass(command);
  REQUIRE(vkEndCommandBuffer(command) == VK_SUCCESS);
  REQUIRE(submitAndWait(runtime.queue, command) == VK_SUCCESS);
  const std::array<int, 7> source{{0, 0, -1, 3, 5, 5, 3}};
  const auto *bytes = static_cast<const uint8_t *>(mapped);
  for (unsigned image = 0; image < images.size(); ++image) {
    const unsigned samples = multisampled(image) ? 4 : 1;
    for (unsigned pixel = 0; pixel < 4; ++pixel)
      for (unsigned sample = 0; sample < samples; ++sample) {
        uint32_t actual, expected = 0xa5a5a5a5;
        if (pixel == 1 && source[image] >= 0) {
          const float value = source[image] + .5f;
          std::memcpy(&expected, &value, 4);
        }
        std::memcpy(&actual, bytes + image * 128 + (pixel * samples + sample) * 4, 4);
        REQUIRE(actual == expected);
      }
    REQUIRE(bytes[image * 128 + 127] == 0xa5);
  }
  vkDestroyFramebuffer(runtime.device, framebuffer, nullptr);
  vkDestroyRenderPass(runtime.device, pass, nullptr);
  for (unsigned i = 0; i < images.size(); ++i) {
    vkDestroyImageView(runtime.device, views[i], nullptr);
    vkDestroyImage(runtime.device, images[i], nullptr);
  }
  vkUnmapMemory(runtime.device, memory);
  vkFreeMemory(runtime.device, memory, nullptr);
}

TEST_CASE("Vulkan texel buffer view creation validates ranges and reports executable formats") {
  TestDevice runtime;
  VkBufferCreateInfo bufferInfo{};
  bufferInfo.sType = VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO;
  bufferInfo.size = 67;
  bufferInfo.usage = VK_BUFFER_USAGE_UNIFORM_TEXEL_BUFFER_BIT | VK_BUFFER_USAGE_STORAGE_TEXEL_BUFFER_BIT;
  VkBuffer buffer;
  REQUIRE(vkCreateBuffer(runtime.device, &bufferInfo, nullptr, &buffer) == VK_SUCCESS);
  for (const auto format : {VK_FORMAT_R8G8B8A8_UNORM, VK_FORMAT_R8G8B8A8_UINT, VK_FORMAT_R32_UINT, VK_FORMAT_R32_SFLOAT,
                            VK_FORMAT_R32G32_SFLOAT, VK_FORMAT_R32G32B32A32_SFLOAT, VK_FORMAT_B8G8R8A8_UNORM}) {
    VkFormatProperties properties{};
    vkGetPhysicalDeviceFormatProperties(VK_NULL_HANDLE, format, &properties);
    REQUIRE((properties.bufferFeatures & VK_FORMAT_FEATURE_UNIFORM_TEXEL_BUFFER_BIT) != 0);
    REQUIRE(((properties.bufferFeatures & VK_FORMAT_FEATURE_STORAGE_TEXEL_BUFFER_BIT) != 0) == (format != VK_FORMAT_B8G8R8A8_UNORM));
    REQUIRE(((properties.bufferFeatures & VK_FORMAT_FEATURE_STORAGE_TEXEL_BUFFER_ATOMIC_BIT) != 0) == (format == VK_FORMAT_R32_UINT));
    VkBufferViewCreateInfo info{};
    info.sType = VK_STRUCTURE_TYPE_BUFFER_VIEW_CREATE_INFO;
    info.buffer = buffer;
    info.format = format;
    info.offset = 1;
    info.range = VK_WHOLE_SIZE;
    VkBufferView view;
    REQUIRE(vkCreateBufferView(runtime.device, &info, nullptr, &view) == VK_SUCCESS);
    vkDestroyBufferView(runtime.device, view, nullptr);
    info.range = 64;
    REQUIRE(vkCreateBufferView(runtime.device, &info, nullptr, &view) == VK_SUCCESS);
    vkDestroyBufferView(runtime.device, view, nullptr);
    info.range = 0;
    REQUIRE(vkCreateBufferView(runtime.device, &info, nullptr, &view) == VK_ERROR_INITIALIZATION_FAILED);
    info.range = 3;
    REQUIRE(vkCreateBufferView(runtime.device, &info, nullptr, &view) == VK_ERROR_INITIALIZATION_FAILED);
    info.range = 68;
    REQUIRE(vkCreateBufferView(runtime.device, &info, nullptr, &view) == VK_ERROR_INITIALIZATION_FAILED);
    info.offset = 67;
    info.range = VK_WHOLE_SIZE;
    REQUIRE(vkCreateBufferView(runtime.device, &info, nullptr, &view) == VK_ERROR_INITIALIZATION_FAILED);
  }
  vkDestroyBuffer(runtime.device, buffer, nullptr);
}

TEST_CASE("Vulkan 1D images enforce view dimensions and preserve mip and layer boundaries", "[graphics-execution]") {
  TestDevice runtime;
  VkImageFormatProperties properties{};
  const auto usage = VK_IMAGE_USAGE_SAMPLED_BIT | VK_IMAGE_USAGE_STORAGE_BIT | VK_IMAGE_USAGE_TRANSFER_DST_BIT;
  REQUIRE(vkGetPhysicalDeviceImageFormatProperties(VK_NULL_HANDLE, VK_FORMAT_R32_UINT, VK_IMAGE_TYPE_1D, VK_IMAGE_TILING_OPTIMAL, usage, 0,
                                                   &properties)
          == VK_SUCCESS);
  REQUIRE(properties.maxExtent.height == 1);
  REQUIRE(properties.maxExtent.depth == 1);
  REQUIRE(properties.sampleCounts == VK_SAMPLE_COUNT_1_BIT);
  VkImageCreateInfo info{};
  info.sType = VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO;
  info.imageType = VK_IMAGE_TYPE_1D;
  info.format = VK_FORMAT_R32_UINT;
  info.extent = {17, 2, 1};
  info.mipLevels = 5;
  info.arrayLayers = 2;
  info.samples = VK_SAMPLE_COUNT_1_BIT;
  info.usage = usage;
  VkImage image;
  REQUIRE(vkCreateImage(runtime.device, &info, nullptr, &image) == VK_ERROR_FORMAT_NOT_SUPPORTED);
  info.extent.height = 1;
  REQUIRE(vkCreateImage(runtime.device, &info, nullptr, &image) == VK_SUCCESS);
  VkMemoryRequirements requirements{};
  vkGetImageMemoryRequirements(runtime.device, image, &requirements);
  REQUIRE(requirements.size == 256);
  VkMemoryAllocateInfo allocation{};
  allocation.sType = VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO;
  allocation.allocationSize = 320;
  VkDeviceMemory memory;
  REQUIRE(vkAllocateMemory(runtime.device, &allocation, nullptr, &memory) == VK_SUCCESS);
  REQUIRE(vkBindImageMemory(runtime.device, image, memory, 0) == VK_SUCCESS);
  void *mapped;
  REQUIRE(vkMapMemory(runtime.device, memory, 0, VK_WHOLE_SIZE, 0, &mapped) == VK_SUCCESS);
  auto *bytes = static_cast<uint8_t *>(mapped);
  std::fill_n(bytes, 320, 0xa5);
  VkImageViewCreateInfo viewInfo{};
  viewInfo.sType = VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO;
  viewInfo.image = image;
  viewInfo.viewType = VK_IMAGE_VIEW_TYPE_2D;
  viewInfo.format = info.format;
  viewInfo.subresourceRange = {VK_IMAGE_ASPECT_COLOR_BIT, 1, 2, 1, 1};
  VkImageView view;
  REQUIRE(vkCreateImageView(runtime.device, &viewInfo, nullptr, &view) == VK_ERROR_FORMAT_NOT_SUPPORTED);
  viewInfo.viewType = VK_IMAGE_VIEW_TYPE_1D;
  REQUIRE(vkCreateImageView(runtime.device, &viewInfo, nullptr, &view) == VK_SUCCESS);
  CommandRecording recording(runtime.device);
  const auto command = recording.command;
  VkClearColorValue clear{};
  clear.uint32[0] = 0x12345678u;
  vkCmdClearColorImage(command, image, VK_IMAGE_LAYOUT_GENERAL, &clear, 1, &viewInfo.subresourceRange);
  REQUIRE(vkEndCommandBuffer(command) == VK_SUCCESS);
  REQUIRE(submitAndWait(runtime.queue, command) == VK_SUCCESS);
  for (size_t offset = 0; offset < 320; offset += 4) {
    uint32_t value;
    std::memcpy(&value, bytes + offset, 4);
    const bool cleared = (offset >= 168 && offset < 200) || (offset >= 216 && offset < 232);
    REQUIRE(value == (cleared ? 0x12345678u : 0xa5a5a5a5u));
  }
  vkDestroyImageView(runtime.device, view, nullptr);
  vkDestroyImage(runtime.device, image, nullptr);
  vkFreeMemory(runtime.device, memory, nullptr);
}

TEST_CASE("Vulkan image resource bounds include subresources and differ from allocation limits") {
  VkImageFormatProperties properties{};
  REQUIRE(vkGetPhysicalDeviceImageFormatProperties(VK_NULL_HANDLE, VK_FORMAT_R8G8B8A8_UNORM, VK_IMAGE_TYPE_2D, VK_IMAGE_TILING_OPTIMAL,
                                                   VK_IMAGE_USAGE_TRANSFER_DST_BIT, 0, &properties)
          == VK_SUCCESS);
  REQUIRE(properties.maxResourceSize >= (VkDeviceSize{1} << 31));
  VkImageCreateInfo info{};
  info.sType = VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO;
  info.imageType = VK_IMAGE_TYPE_2D;
  info.format = VK_FORMAT_R8G8B8A8_UNORM;
  info.extent = {16384, 16384, 1};
  info.arrayLayers = 2;
  info.mipLevels = 1;
  info.samples = VK_SAMPLE_COUNT_1_BIT;
  info.tiling = VK_IMAGE_TILING_OPTIMAL;
  info.usage = VK_IMAGE_USAGE_TRANSFER_DST_BIT;
  VkImage image = VK_NULL_HANDLE;
  REQUIRE(vkCreateImage(VK_NULL_HANDLE, &info, nullptr, &image) == VK_SUCCESS);
  VkMemoryRequirements requirements{};
  vkGetImageMemoryRequirements(VK_NULL_HANDLE, image, &requirements);
  REQUIRE(requirements.size == (VkDeviceSize{1} << 31));
  vkDestroyImage(VK_NULL_HANDLE, image, nullptr);
  image = VK_NULL_HANDLE;
  SECTION("array layers") { info.arrayLayers = 3; }
  SECTION("mip levels") { info.mipLevels = 2; }
  SECTION("multisampling") { info.samples = VK_SAMPLE_COUNT_4_BIT; }
  REQUIRE(vkCreateImage(VK_NULL_HANDLE, &info, nullptr, &image) == VK_ERROR_OUT_OF_DEVICE_MEMORY);
  REQUIRE(image == VK_NULL_HANDLE);
}

TEST_CASE("Vulkan subpass first-use clears preserve aliased contents and remapped slots", "[graphics-execution]") {
  TestDevice runtime;
  const bool clearAgain = GENERATE(false, true);
  for (auto name : {"vkCmdNextSubpass", "vkCmdNextSubpass2", "vkCreateRenderPass2", "vkCmdBeginRenderPass2", "vkCmdEndRenderPass2"})
    REQUIRE(vkGetDeviceProcAddr(runtime.device, name) != nullptr);
  VkImageCreateInfo imageInfo{};
  imageInfo.sType = VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO;
  imageInfo.imageType = VK_IMAGE_TYPE_2D;
  imageInfo.format = VK_FORMAT_R32_UINT;
  imageInfo.extent = {4, 3, 1};
  imageInfo.mipLevels = imageInfo.arrayLayers = 1;
  imageInfo.samples = VK_SAMPLE_COUNT_1_BIT;
  imageInfo.usage = VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT | VK_IMAGE_USAGE_INPUT_ATTACHMENT_BIT;
  VkImage image;
  REQUIRE(vkCreateImage(runtime.device, &imageInfo, nullptr, &image) == VK_SUCCESS);
  VkMemoryAllocateInfo allocate{};
  allocate.sType = VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO;
  allocate.allocationSize = 256;
  VkDeviceMemory memory;
  REQUIRE(vkAllocateMemory(runtime.device, &allocate, nullptr, &memory) == VK_SUCCESS);
  REQUIRE(vkBindImageMemory(runtime.device, image, memory, 64) == VK_SUCCESS);
  void *mapped;
  REQUIRE(vkMapMemory(runtime.device, memory, 0, VK_WHOLE_SIZE, 0, &mapped) == VK_SUCCESS);
  auto *words = static_cast<uint32_t *>(mapped);
  std::fill(words, words + 64, 0xdeadbeefu);
  VkImageViewCreateInfo viewInfo{};
  viewInfo.sType = VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO;
  viewInfo.image = image;
  viewInfo.viewType = VK_IMAGE_VIEW_TYPE_2D;
  viewInfo.format = imageInfo.format;
  viewInfo.subresourceRange = {VK_IMAGE_ASPECT_COLOR_BIT, 0, 1, 0, 1};
  VkImageView view;
  REQUIRE(vkCreateImageView(runtime.device, &viewInfo, nullptr, &view) == VK_SUCCESS);
  std::array<VkAttachmentDescription, 2> attachments{};
  for (auto &a : attachments) {
    a.flags = VK_ATTACHMENT_DESCRIPTION_MAY_ALIAS_BIT;
    a.format = imageInfo.format;
    a.samples = VK_SAMPLE_COUNT_1_BIT;
    a.loadOp = VK_ATTACHMENT_LOAD_OP_CLEAR;
    a.storeOp = VK_ATTACHMENT_STORE_OP_STORE;
    a.initialLayout = a.finalLayout = VK_IMAGE_LAYOUT_GENERAL;
  }
  const VkAttachmentReference first{0, VK_IMAGE_LAYOUT_GENERAL};
  const VkAttachmentReference second[]{{VK_ATTACHMENT_UNUSED, VK_IMAGE_LAYOUT_UNDEFINED}, {1, VK_IMAGE_LAYOUT_GENERAL}};
  VkSubpassDescription subpasses[3]{};
  for (auto &subpass : subpasses)
    subpass.pipelineBindPoint = VK_PIPELINE_BIND_POINT_GRAPHICS;
  subpasses[0].colorAttachmentCount = 1;
  subpasses[0].pColorAttachments = &first;
  for (int i = 1; i < 3; ++i) {
    subpasses[i].colorAttachmentCount = 2;
    subpasses[i].pColorAttachments = second;
  }
  VkSubpassDependency dependencies[2]{};
  for (uint32_t i = 0; i < 2; ++i) {
    dependencies[i] = {i,
                       i + 1,
                       VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT,
                       VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT,
                       VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT,
                       VK_ACCESS_COLOR_ATTACHMENT_READ_BIT | VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT,
                       0};
  }
  VkRenderPassCreateInfo passInfo{};
  passInfo.sType = VK_STRUCTURE_TYPE_RENDER_PASS_CREATE_INFO;
  passInfo.attachmentCount = 2;
  passInfo.pAttachments = attachments.data();
  passInfo.subpassCount = 3;
  passInfo.pSubpasses = subpasses;
  passInfo.dependencyCount = 2;
  passInfo.pDependencies = dependencies;
  VkRenderPass pass;
  REQUIRE(vkCreateRenderPass(runtime.device, &passInfo, nullptr, &pass) == VK_SUCCESS);
  const VkImageView views[]{view, view};
  VkFramebufferCreateInfo fb{};
  fb.sType = VK_STRUCTURE_TYPE_FRAMEBUFFER_CREATE_INFO;
  fb.renderPass = pass;
  fb.attachmentCount = 2;
  fb.pAttachments = views;
  fb.width = 4;
  fb.height = 3;
  fb.layers = 1;
  VkFramebuffer framebuffer;
  REQUIRE(vkCreateFramebuffer(runtime.device, &fb, nullptr, &framebuffer) == VK_SUCCESS);
  CommandRecording recording(runtime.device);
  const auto command = recording.command;
  VkClearValue clears[2]{};
  clears[0].color.uint32[0] = 11;
  clears[1].color.uint32[0] = 37;
  VkRenderPassBeginInfo render{};
  render.sType = VK_STRUCTURE_TYPE_RENDER_PASS_BEGIN_INFO;
  render.renderPass = pass;
  render.framebuffer = framebuffer;
  render.renderArea = {{1, 1}, {2, 1}};
  render.clearValueCount = 2;
  render.pClearValues = clears;
  vkCmdBeginRenderPass(command, &render, VK_SUBPASS_CONTENTS_INLINE);
  VkClearAttachment clear{};
  clear.aspectMask = VK_IMAGE_ASPECT_COLOR_BIT;
  clear.clearValue.color.uint32[0] = 23;
  VkClearRect rect{render.renderArea, 0, 1};
  vkCmdClearAttachments(command, 1, &clear, 1, &rect);
  vkCmdNextSubpass(command, VK_SUBPASS_CONTENTS_INLINE);
  if (clearAgain) {
    clear.colorAttachment = 1;
    clear.clearValue.color.uint32[0] = 59;
    vkCmdClearAttachments(command, 1, &clear, 1, &rect);
  }
  vkCmdNextSubpass(command, VK_SUBPASS_CONTENTS_INLINE);
  vkCmdEndRenderPass(command);
  REQUIRE(vkEndCommandBuffer(command) == VK_SUCCESS);
  REQUIRE(submitAndWait(runtime.queue, command) == VK_SUCCESS);
  for (size_t i = 0; i < 64; ++i)
    REQUIRE(words[i] == ((i == 21 || i == 22) ? (clearAgain ? 59u : 37u) : 0xdeadbeefu));
  vkDestroyFramebuffer(runtime.device, framebuffer, nullptr);
  vkDestroyRenderPass(runtime.device, pass, nullptr);
  vkDestroyImageView(runtime.device, view, nullptr);
  vkDestroyImage(runtime.device, image, nullptr);
  vkFreeMemory(runtime.device, memory, nullptr);
}

TEST_CASE("Vulkan mutable framebuffer views reinterpret bits at a selected mip and layer", "[graphics-execution]") {
  TestDevice runtime;
  const bool clearAgain = false;
  const auto viewFormat = GENERATE(VK_FORMAT_R32_SFLOAT, VK_FORMAT_R8G8B8A8_UNORM, VK_FORMAT_R8G8B8A8_UINT, VK_FORMAT_B8G8R8A8_UNORM);
  uint32_t expected = viewFormat == VK_FORMAT_R32_SFLOAT       ? 0x3fc00000u
                      : viewFormat == VK_FORMAT_R8G8B8A8_UNORM ? 0xff804020u
                      : viewFormat == VK_FORMAT_R8G8B8A8_UINT  ? 0x44332211u
                                                               : 0xff204080u;
  for (auto name : {"vkCmdNextSubpass", "vkCmdNextSubpass2", "vkCreateRenderPass2", "vkCmdBeginRenderPass2", "vkCmdEndRenderPass2"})
    REQUIRE(vkGetDeviceProcAddr(runtime.device, name) != nullptr);
  VkImageCreateInfo imageInfo{};
  imageInfo.sType = VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO;
  imageInfo.imageType = VK_IMAGE_TYPE_2D;
  imageInfo.format = VK_FORMAT_R32_UINT;
  imageInfo.extent = {8, 6, 1};
  imageInfo.flags = VK_IMAGE_CREATE_MUTABLE_FORMAT_BIT;
  imageInfo.mipLevels = imageInfo.arrayLayers = 2;
  imageInfo.samples = VK_SAMPLE_COUNT_1_BIT;
  imageInfo.usage = VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT | VK_IMAGE_USAGE_INPUT_ATTACHMENT_BIT;
  VkImage image;
  REQUIRE(vkCreateImage(runtime.device, &imageInfo, nullptr, &image) == VK_SUCCESS);
  VkMemoryAllocateInfo allocate{};
  allocate.sType = VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO;
  allocate.allocationSize = 768;
  VkDeviceMemory memory;
  REQUIRE(vkAllocateMemory(runtime.device, &allocate, nullptr, &memory) == VK_SUCCESS);
  REQUIRE(vkBindImageMemory(runtime.device, image, memory, 64) == VK_SUCCESS);
  void *mapped;
  REQUIRE(vkMapMemory(runtime.device, memory, 0, VK_WHOLE_SIZE, 0, &mapped) == VK_SUCCESS);
  auto *words = static_cast<uint32_t *>(mapped);
  std::fill(words, words + 192, 0xdeadbeefu);
  VkImageViewCreateInfo viewInfo{};
  viewInfo.sType = VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO;
  viewInfo.image = image;
  viewInfo.viewType = VK_IMAGE_VIEW_TYPE_2D;
  viewInfo.format = imageInfo.format;
  viewInfo.subresourceRange = {VK_IMAGE_ASPECT_COLOR_BIT, 1, 1, 1, 1};
  VkImageView view;
  REQUIRE(vkCreateImageView(runtime.device, &viewInfo, nullptr, &view) == VK_SUCCESS);
  viewInfo.format = viewFormat;
  VkImageView reinterpreted;
  REQUIRE(vkCreateImageView(runtime.device, &viewInfo, nullptr, &reinterpreted) == VK_SUCCESS);
  std::array<VkAttachmentDescription, 2> attachments{};
  for (auto &a : attachments) {
    a.flags = VK_ATTACHMENT_DESCRIPTION_MAY_ALIAS_BIT;
    a.format = imageInfo.format;
    a.samples = VK_SAMPLE_COUNT_1_BIT;
    a.loadOp = VK_ATTACHMENT_LOAD_OP_CLEAR;
    a.storeOp = VK_ATTACHMENT_STORE_OP_STORE;
    a.initialLayout = a.finalLayout = VK_IMAGE_LAYOUT_GENERAL;
  }
  attachments[1].format = viewFormat;
  const VkAttachmentReference first{0, VK_IMAGE_LAYOUT_GENERAL};
  const VkAttachmentReference second[]{{VK_ATTACHMENT_UNUSED, VK_IMAGE_LAYOUT_UNDEFINED}, {1, VK_IMAGE_LAYOUT_GENERAL}};
  VkSubpassDescription subpasses[3]{};
  for (auto &subpass : subpasses)
    subpass.pipelineBindPoint = VK_PIPELINE_BIND_POINT_GRAPHICS;
  subpasses[0].colorAttachmentCount = 1;
  subpasses[0].pColorAttachments = &first;
  for (int i = 1; i < 3; ++i) {
    subpasses[i].colorAttachmentCount = 2;
    subpasses[i].pColorAttachments = second;
  }
  VkSubpassDependency dependencies[2]{};
  for (uint32_t i = 0; i < 2; ++i) {
    dependencies[i] = {i,
                       i + 1,
                       VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT,
                       VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT,
                       VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT,
                       VK_ACCESS_COLOR_ATTACHMENT_READ_BIT | VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT,
                       0};
  }
  VkRenderPassCreateInfo passInfo{};
  passInfo.sType = VK_STRUCTURE_TYPE_RENDER_PASS_CREATE_INFO;
  passInfo.attachmentCount = 2;
  passInfo.pAttachments = attachments.data();
  passInfo.subpassCount = 3;
  passInfo.pSubpasses = subpasses;
  passInfo.dependencyCount = 2;
  passInfo.pDependencies = dependencies;
  VkRenderPass pass;
  REQUIRE(vkCreateRenderPass(runtime.device, &passInfo, nullptr, &pass) == VK_SUCCESS);
  const VkImageView views[]{view, reinterpreted};
  VkFramebufferCreateInfo fb{};
  fb.sType = VK_STRUCTURE_TYPE_FRAMEBUFFER_CREATE_INFO;
  fb.renderPass = pass;
  fb.attachmentCount = 2;
  fb.pAttachments = views;
  fb.width = 4;
  fb.height = 3;
  fb.layers = 1;
  VkFramebuffer framebuffer;
  REQUIRE(vkCreateFramebuffer(runtime.device, &fb, nullptr, &framebuffer) == VK_SUCCESS);
  CommandRecording recording(runtime.device);
  const auto command = recording.command;
  VkClearValue clears[2]{};
  clears[0].color.uint32[0] = 11;
  if (viewFormat == VK_FORMAT_R8G8B8A8_UINT) {
    for (unsigned i = 0; i < 4; ++i)
      clears[1].color.uint32[i] = (i + 1) * 17;
  } else {
    clears[1].color.float32[0] = viewFormat == VK_FORMAT_R32_SFLOAT ? 1.5f : .125f;
    clears[1].color.float32[1] = .25f;
    clears[1].color.float32[2] = .5f;
    clears[1].color.float32[3] = 1.f;
  }
  VkRenderPassBeginInfo render{};
  render.sType = VK_STRUCTURE_TYPE_RENDER_PASS_BEGIN_INFO;
  render.renderPass = pass;
  render.framebuffer = framebuffer;
  render.renderArea = {{1, 1}, {2, 1}};
  render.clearValueCount = 2;
  render.pClearValues = clears;
  vkCmdBeginRenderPass(command, &render, VK_SUBPASS_CONTENTS_INLINE);
  VkClearAttachment clear{};
  clear.aspectMask = VK_IMAGE_ASPECT_COLOR_BIT;
  clear.clearValue.color.uint32[0] = 23;
  VkClearRect rect{render.renderArea, 0, 1};
  vkCmdClearAttachments(command, 1, &clear, 1, &rect);
  vkCmdNextSubpass(command, VK_SUBPASS_CONTENTS_INLINE);
  if (clearAgain) {
    clear.colorAttachment = 1;
    clear.clearValue.color.uint32[0] = 59;
    vkCmdClearAttachments(command, 1, &clear, 1, &rect);
  }
  vkCmdNextSubpass(command, VK_SUBPASS_CONTENTS_INLINE);
  vkCmdEndRenderPass(command);
  REQUIRE(vkEndCommandBuffer(command) == VK_SUCCESS);
  REQUIRE(submitAndWait(runtime.queue, command) == VK_SUCCESS);
  for (size_t i = 0; i < 192; ++i)
    REQUIRE(words[i] == ((i == 129 || i == 130) ? expected : 0xdeadbeefu));
  vkDestroyFramebuffer(runtime.device, framebuffer, nullptr);
  vkDestroyRenderPass(runtime.device, pass, nullptr);
  vkDestroyImageView(runtime.device, reinterpreted, nullptr);
  vkDestroyImageView(runtime.device, view, nullptr);
  vkDestroyImage(runtime.device, image, nullptr);
  vkFreeMemory(runtime.device, memory, nullptr);
}

TEST_CASE("Vulkan mutable views enforce compatibility and own their format list") {
  const auto imageFormat = GENERATE(VK_FORMAT_R32_UINT, VK_FORMAT_D32_SFLOAT);
  const bool mutableFormat = GENERATE(false, true);
  const bool formatList = GENERATE(false, true);
  VkImageCreateInfo imageInfo{};
  imageInfo.sType = VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO;
  imageInfo.flags = mutableFormat ? VK_IMAGE_CREATE_MUTABLE_FORMAT_BIT : 0;
  imageInfo.imageType = VK_IMAGE_TYPE_2D;
  imageInfo.format = imageFormat;
  imageInfo.extent = {4, 4, 1};
  imageInfo.mipLevels = imageInfo.arrayLayers = 1;
  imageInfo.samples = VK_SAMPLE_COUNT_1_BIT;
  imageInfo.usage = VK_IMAGE_USAGE_SAMPLED_BIT;
  VkFormat permitted = imageFormat;
  VkImageFormatListCreateInfo list{};
  list.sType = VK_STRUCTURE_TYPE_IMAGE_FORMAT_LIST_CREATE_INFO;
  list.viewFormatCount = 1;
  list.pViewFormats = &permitted;
  imageInfo.pNext = formatList ? &list : nullptr;
  VkImage image;
  REQUIRE(vkCreateImage(VK_NULL_HANDLE, &imageInfo, nullptr, &image) == VK_SUCCESS);
  permitted = VK_FORMAT_R32_SFLOAT; // The runtime owns the original list.
  for (auto format : {imageFormat, VK_FORMAT_R32_SFLOAT, VK_FORMAT_R32G32_SFLOAT, VK_FORMAT_R8G8B8A8_UINT}) {
    VkImageViewCreateInfo info{};
    info.sType = VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO;
    info.image = image;
    info.viewType = VK_IMAGE_VIEW_TYPE_2D;
    info.format = format;
    info.subresourceRange = {format == VK_FORMAT_D32_SFLOAT ? VK_IMAGE_ASPECT_DEPTH_BIT : VK_IMAGE_ASPECT_COLOR_BIT, 0, 1, 0, 1};
    VkImageView view = VK_NULL_HANDLE;
    const bool valid =
        format == imageFormat || (mutableFormat && !formatList && imageFormat == VK_FORMAT_R32_UINT && format != VK_FORMAT_R32G32_SFLOAT);
    REQUIRE(vkCreateImageView(VK_NULL_HANDLE, &info, nullptr, &view) == (valid ? VK_SUCCESS : VK_ERROR_FORMAT_NOT_SUPPORTED));
    if (valid) vkDestroyImageView(VK_NULL_HANDLE, view, nullptr);
    else REQUIRE(view == VK_NULL_HANDLE);
  }
  vkDestroyImage(VK_NULL_HANDLE, image, nullptr);
}

TEST_CASE("Vulkan image queries distinguish cube sample counts and extended view usage") {
  for (auto format : {VK_FORMAT_R32_UINT, VK_FORMAT_R8G8B8A8_UNORM, VK_FORMAT_D32_SFLOAT}) {
    for (auto flags : {VkImageCreateFlags{0}, VkImageCreateFlags{VK_IMAGE_CREATE_MUTABLE_FORMAT_BIT}}) {
      VkImageFormatProperties properties{};
      REQUIRE(vkGetPhysicalDeviceImageFormatProperties(VK_NULL_HANDLE, format, VK_IMAGE_TYPE_2D, VK_IMAGE_TILING_OPTIMAL,
                                                       VK_IMAGE_USAGE_SAMPLED_BIT, flags, &properties)
              == VK_SUCCESS);
      REQUIRE(properties.sampleCounts == (VK_SAMPLE_COUNT_1_BIT | VK_SAMPLE_COUNT_4_BIT));
      REQUIRE(vkGetPhysicalDeviceImageFormatProperties(VK_NULL_HANDLE, format, VK_IMAGE_TYPE_2D, VK_IMAGE_TILING_OPTIMAL,
                                                       VK_IMAGE_USAGE_SAMPLED_BIT, flags | VK_IMAGE_CREATE_CUBE_COMPATIBLE_BIT, &properties)
              == VK_SUCCESS);
      REQUIRE(properties.sampleCounts == VK_SAMPLE_COUNT_1_BIT);
    }
  }
  VkImageFormatProperties properties{};
  REQUIRE(vkGetPhysicalDeviceImageFormatProperties(VK_NULL_HANDLE, VK_FORMAT_B8G8R8A8_UNORM, VK_IMAGE_TYPE_2D, VK_IMAGE_TILING_OPTIMAL,
                                                   VK_IMAGE_USAGE_STORAGE_BIT, VK_IMAGE_CREATE_MUTABLE_FORMAT_BIT, &properties)
          == VK_ERROR_FORMAT_NOT_SUPPORTED);
  REQUIRE(properties.maxResourceSize == 0);
  REQUIRE(vkGetPhysicalDeviceImageFormatProperties(VK_NULL_HANDLE, VK_FORMAT_B8G8R8A8_UNORM, VK_IMAGE_TYPE_2D, VK_IMAGE_TILING_OPTIMAL,
                                                   VK_IMAGE_USAGE_STORAGE_BIT,
                                                   VK_IMAGE_CREATE_MUTABLE_FORMAT_BIT | VK_IMAGE_CREATE_EXTENDED_USAGE_BIT, &properties)
          == VK_SUCCESS);
  REQUIRE(properties.sampleCounts == VK_SAMPLE_COUNT_1_BIT);
}

TEST_CASE("Vulkan depth blits preserve bits across scaled reversed mip and layer regions", "[graphics-execution]") {
  TestDevice runtime;
  const auto format = GENERATE(VK_FORMAT_D16_UNORM, VK_FORMAT_D32_SFLOAT);
  const size_t stride = format == VK_FORMAT_D16_UNORM ? 2 : 4;
  VkImageCreateInfo info{};
  info.sType = VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO;
  info.imageType = VK_IMAGE_TYPE_2D;
  info.format = format;
  info.extent = {4, 3, 1};
  info.mipLevels = info.arrayLayers = 2;
  info.samples = VK_SAMPLE_COUNT_1_BIT;
  info.usage = VK_IMAGE_USAGE_TRANSFER_SRC_BIT | VK_IMAGE_USAGE_TRANSFER_DST_BIT;
  VkImage images[2]{};
  for (auto &image : images)
    REQUIRE(vkCreateImage(runtime.device, &info, nullptr, &image) == VK_SUCCESS);
  VkMemoryAllocateInfo allocate{};
  allocate.sType = VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO;
  allocate.allocationSize = 384;
  VkDeviceMemory memory;
  REQUIRE(vkAllocateMemory(runtime.device, &allocate, nullptr, &memory) == VK_SUCCESS);
  REQUIRE(vkBindImageMemory(runtime.device, images[0], memory, 64) == VK_SUCCESS);
  REQUIRE(vkBindImageMemory(runtime.device, images[1], memory, 192) == VK_SUCCESS);
  void *mapped;
  REQUIRE(vkMapMemory(runtime.device, memory, 0, VK_WHOLE_SIZE, 0, &mapped) == VK_SUCCESS);
  auto *bytes = static_cast<uint8_t *>(mapped);
  std::fill_n(bytes, 384, 0xa5);
  const uint32_t values[]{format == VK_FORMAT_D16_UNORM ? 0x1234u : 0x3e800000u, format == VK_FORMAT_D16_UNORM ? 0xfedcu : 0x3f400000u};
  const size_t sourceOffset = 64 + 26 * stride;
  for (size_t x = 0; x < 2; ++x)
    std::memcpy(bytes + sourceOffset + x * stride, values + x, stride);
  std::vector<uint8_t> expected(bytes, bytes + 384);
  for (size_t y = 0; y < 3; ++y)
    for (size_t x = 0; x < 4; ++x)
      std::memcpy(expected.data() + 192 + (12 + y * 4 + x) * stride, values + (x < 2 ? 1 : 0), stride);
  CommandRecording recording(runtime.device);
  const auto command = recording.command;
  VkImageBlit region{};
  region.srcSubresource = {VK_IMAGE_ASPECT_DEPTH_BIT, 1, 1, 1};
  region.srcOffsets[1] = {2, 1, 1};
  region.dstSubresource = {VK_IMAGE_ASPECT_DEPTH_BIT, 0, 1, 1};
  region.dstOffsets[0] = {4, 3, 0};
  region.dstOffsets[1] = {0, 0, 1};
  vkCmdBlitImage(command, images[0], VK_IMAGE_LAYOUT_GENERAL, images[1], VK_IMAGE_LAYOUT_GENERAL, 1, &region, VK_FILTER_NEAREST);
  REQUIRE(vkEndCommandBuffer(command) == VK_SUCCESS);
  REQUIRE(submitAndWait(runtime.queue, command) == VK_SUCCESS);
  REQUIRE(std::equal(expected.begin(), expected.end(), bytes));
  for (auto image : images)
    vkDestroyImage(runtime.device, image, nullptr);
  vkFreeMemory(runtime.device, memory, nullptr);
}

TEST_CASE("Vulkan empty pipeline caches serialise a bounded compatible header and merge") {
  TestDevice device;
  VkPipelineCacheCreateInfo info{};
  info.sType = VK_STRUCTURE_TYPE_PIPELINE_CACHE_CREATE_INFO;
  VkPipelineCache cache{}, restored{};
  REQUIRE(vkCreatePipelineCache(device.device, &info, nullptr, &cache) == VK_SUCCESS);
  REQUIRE(vkGetDeviceProcAddr(device.device, "vkMergePipelineCaches") != nullptr);
  size_t size = 0;
  REQUIRE(vkGetPipelineCacheData(device.device, cache, &size, nullptr) == VK_SUCCESS);
  REQUIRE(size == 32);
  std::array<uint8_t, 40> bytes{};
  for (size_t capacity = 0; capacity <= bytes.size(); ++capacity) {
    bytes.fill(0xa5);
    size = capacity;
    REQUIRE(vkGetPipelineCacheData(device.device, cache, &size, bytes.data()) == (capacity < 32 ? VK_INCOMPLETE : VK_SUCCESS));
    REQUIRE(size == (capacity < 32 ? 0 : 32));
    for (size_t i = size; i < bytes.size(); ++i)
      REQUIRE(bytes[i] == 0xa5);
  }
  const auto word = [&](size_t offset) {
    return uint32_t(bytes[offset]) | (uint32_t(bytes[offset + 1]) << 8) | (uint32_t(bytes[offset + 2]) << 16)
           | (uint32_t(bytes[offset + 3]) << 24);
  };
  uint32_t count = 1;
  VkPhysicalDevice physical{};
  REQUIRE(vkEnumeratePhysicalDevices(device.instance, &count, &physical) == VK_SUCCESS);
  VkPhysicalDeviceProperties properties{};
  vkGetPhysicalDeviceProperties(physical, &properties);
  REQUIRE(word(0) == 32);
  REQUIRE(word(4) == VK_PIPELINE_CACHE_HEADER_VERSION_ONE);
  REQUIRE(word(8) == properties.vendorID);
  REQUIRE(word(12) == properties.deviceID);
  REQUIRE(std::memcmp(bytes.data() + 16, properties.pipelineCacheUUID, VK_UUID_SIZE) == 0);
  info.initialDataSize = size;
  info.pInitialData = bytes.data();
  REQUIRE(vkCreatePipelineCache(device.device, &info, nullptr, &restored) == VK_SUCCESS);
  REQUIRE(vkMergePipelineCaches(device.device, restored, 1, &cache) == VK_SUCCESS);
  std::array<uint8_t, 40> merged{};
  size = merged.size();
  REQUIRE(vkGetPipelineCacheData(device.device, restored, &size, merged.data()) == VK_SUCCESS);
  REQUIRE(std::memcmp(bytes.data(), merged.data(), size) == 0);
  vkDestroyPipelineCache(device.device, restored, nullptr);
  vkDestroyPipelineCache(device.device, cache, nullptr);
}

TEST_CASE("Vulkan rasteriser discard invokes vertices without Position or ignored fragment state", "[graphics-execution]") {
  TestDevice runtime;
  DiscardPipeline pipeline(runtime);
  const bool secondary = GENERATE(false, true);
  CommandRecording recording(runtime.device);
  const auto command = recording.command, child = recording.secondary();
  VkQueryPoolCreateInfo queryInfo{};
  queryInfo.sType = VK_STRUCTURE_TYPE_QUERY_POOL_CREATE_INFO;
  queryInfo.queryType = VK_QUERY_TYPE_OCCLUSION;
  queryInfo.queryCount = 1;
  VkQueryPool query{};
  REQUIRE(vkCreateQueryPool(runtime.device, &queryInfo, nullptr, &query) == VK_SUCCESS);
  vkCmdResetQueryPool(command, query, 0, 1);
  pipeline.begin(command, secondary ? VK_SUBPASS_CONTENTS_SECONDARY_COMMAND_BUFFERS : VK_SUBPASS_CONTENTS_INLINE);
  VkCommandBufferInheritanceInfo inheritance{};
  inheritance.sType = VK_STRUCTURE_TYPE_COMMAND_BUFFER_INHERITANCE_INFO;
  inheritance.renderPass = pipeline.pass;
  // No framebuffer hint: execution must inherit the active primary render scope.
  if (secondary) {
    VkCommandBufferBeginInfo childBegin{};
    childBegin.sType = VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO;
    childBegin.flags = VK_COMMAND_BUFFER_USAGE_RENDER_PASS_CONTINUE_BIT;
    childBegin.pInheritanceInfo = &inheritance;
    REQUIRE(vkBeginCommandBuffer(child, &childBegin) == VK_SUCCESS);
    vkCmdBindPipeline(child, VK_PIPELINE_BIND_POINT_GRAPHICS, pipeline.pipeline);
  }
  const auto draw = secondary ? child : command;
  vkCmdBeginQuery(draw, query, 0, 0);
  vkCmdDraw(draw, 6, 2, 0, 0);
  vkCmdEndQuery(draw, query, 0);
  if (secondary) {
    REQUIRE(vkEndCommandBuffer(child) == VK_SUCCESS);
    vkCmdExecuteCommands(command, 1, &child);
  }
  vkCmdEndRenderPass(command);
  REQUIRE(vkEndCommandBuffer(command) == VK_SUCCESS);
  REQUIRE(submitAndWait(runtime.queue, command) == VK_SUCCESS);
  uint64_t samples = 0xfeed;
  REQUIRE(vkGetQueryPoolResults(runtime.device, query, 0, 1, sizeof(samples), &samples, sizeof(samples), VK_QUERY_RESULT_64_BIT)
          == VK_SUCCESS);
  REQUIRE(samples == 0);
  vkDestroyQueryPool(runtime.device, query, nullptr);
}

TEST_CASE("Vulkan occlusion query availability and partial results remain independent across queues") {
  TestDevice runtime;
  VkQueryPoolCreateInfo queryInfo{};
  queryInfo.sType = VK_STRUCTURE_TYPE_QUERY_POOL_CREATE_INFO;
  queryInfo.queryType = VK_QUERY_TYPE_OCCLUSION;
  queryInfo.queryCount = 2;
  VkQueryPool query{};
  REQUIRE(vkCreateQueryPool(runtime.device, &queryInfo, nullptr, &query) == VK_SUCCESS);
  REQUIRE(vkGetDeviceProcAddr(runtime.device, "vkCmdBeginQuery") != nullptr);
  REQUIRE(vkGetDeviceProcAddr(runtime.device, "vkCmdEndQuery") != nullptr);
  VkEventCreateInfo eventInfo{};
  eventInfo.sType = VK_STRUCTURE_TYPE_EVENT_CREATE_INFO;
  VkEvent entered{}, release{};
  REQUIRE(vkCreateEvent(runtime.device, &eventInfo, nullptr, &entered) == VK_SUCCESS);
  REQUIRE(vkCreateEvent(runtime.device, &eventInfo, nullptr, &release) == VK_SUCCESS);
  const VkCommandBuffer commands[]{reinterpret_cast<VkCommandBuffer>(0x5200), reinterpret_cast<VkCommandBuffer>(0x5201)};
  VkCommandBufferBeginInfo begin{};
  begin.sType = VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO;
  for (uint32_t i = 0; i < 2; ++i) {
    REQUIRE(vkBeginCommandBuffer(commands[i], &begin) == VK_SUCCESS);
    vkCmdResetQueryPool(commands[i], query, i, 1);
    vkCmdBeginQuery(commands[i], query, i, 0);
    if (i == 0) {
      vkCmdSetEvent(commands[i], entered, VK_PIPELINE_STAGE_ALL_COMMANDS_BIT);
      vkCmdWaitEvents(commands[i], 1, &release, VK_PIPELINE_STAGE_HOST_BIT, VK_PIPELINE_STAGE_ALL_COMMANDS_BIT, 0, nullptr, 0, nullptr, 0,
                      nullptr);
    }
    vkCmdEndQuery(commands[i], query, i);
    REQUIRE(vkEndCommandBuffer(commands[i]) == VK_SUCCESS);
  }
  auto submit = submitInfo(commands[0]);
  REQUIRE(vkQueueSubmit(runtime.queue, 1, &submit, VK_NULL_HANDLE) == VK_SUCCESS);
  const auto deadline = std::chrono::steady_clock::now() + std::chrono::seconds(5);
  while (vkGetEventStatus(runtime.device, entered) != VK_EVENT_SET && std::chrono::steady_clock::now() < deadline)
    std::this_thread::yield();
  REQUIRE(vkGetEventStatus(runtime.device, entered) == VK_EVENT_SET);
  uint64_t value[]{0xfeed, 0xfeed, 0xfeed};
  const auto flags = VK_QUERY_RESULT_64_BIT | VK_QUERY_RESULT_WITH_AVAILABILITY_BIT;
  REQUIRE(vkGetQueryPoolResults(runtime.device, query, 0, 1, sizeof(value), value, 24, flags) == VK_NOT_READY);
  REQUIRE(value[0] == 0xfeed);
  REQUIRE(value[1] == 0);
  REQUIRE(value[2] == 0xfeed);
  REQUIRE(vkGetQueryPoolResults(runtime.device, query, 0, 1, sizeof(value), value, 24, flags | VK_QUERY_RESULT_PARTIAL_BIT) == VK_SUCCESS);
  REQUIRE(value[0] == 0);
  REQUIRE(value[1] == 0);
  REQUIRE(value[2] == 0xfeed);
  submit.pCommandBuffers = commands + 1;
  REQUIRE(vkQueueSubmit(runtime.other, 1, &submit, VK_NULL_HANDLE) == VK_SUCCESS);
  REQUIRE(vkQueueWaitIdle(runtime.other) == VK_SUCCESS);
  REQUIRE(vkGetQueryPoolResults(runtime.device, query, 1, 1, sizeof(value), value, 24, flags) == VK_SUCCESS);
  REQUIRE(value[0] == 0);
  REQUIRE(value[1] == 1);
  REQUIRE(value[2] == 0xfeed);
  REQUIRE(vkGetQueryPoolResults(runtime.device, query, 0, 1, sizeof(value), value, 24, flags) == VK_NOT_READY);
  REQUIRE(value[1] == 0);
  REQUIRE(vkSetEvent(runtime.device, release) == VK_SUCCESS);
  REQUIRE(vkQueueWaitIdle(runtime.queue) == VK_SUCCESS);
  REQUIRE(vkGetQueryPoolResults(runtime.device, query, 0, 1, sizeof(value), value, 24, flags | VK_QUERY_RESULT_WAIT_BIT) == VK_SUCCESS);
  REQUIRE(value[0] == 0);
  REQUIRE(value[1] == 1);
  REQUIRE(value[2] == 0xfeed);
  // Replaying a reset/begin/end command starts a fresh query on the same slot.
  REQUIRE(vkQueueSubmit(runtime.other, 1, &submit, VK_NULL_HANDLE) == VK_SUCCESS);
  REQUIRE(vkQueueWaitIdle(runtime.other) == VK_SUCCESS);
  uint32_t narrow[]{0xfeed, 0xfeed, 0xfeed};
  REQUIRE(vkGetQueryPoolResults(runtime.device, query, 1, 1, sizeof(narrow), narrow, 12, VK_QUERY_RESULT_WITH_AVAILABILITY_BIT)
          == VK_SUCCESS);
  REQUIRE(narrow[0] == 0);
  REQUIRE(narrow[1] == 1);
  REQUIRE(narrow[2] == 0xfeed);
  vkDestroyEvent(runtime.device, entered, nullptr);
  vkDestroyEvent(runtime.device, release, nullptr);
  vkDestroyQueryPool(runtime.device, query, nullptr);
}

TEST_CASE("Vulkan stencil clears and padded copies preserve depth and neighbouring layers", "[graphics-execution]") {
  TestDevice runtime;
  const auto format = GENERATE(VK_FORMAT_S8_UINT, VK_FORMAT_D32_SFLOAT_S8_UINT);
  const uint32_t width = 1;
  const uint32_t imageStride = format == VK_FORMAT_S8_UINT ? 1 : 8;
  VkImageCreateInfo info{};
  info.sType = VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO;
  info.imageType = VK_IMAGE_TYPE_2D;
  info.format = format;
  info.extent = {4, 3, 1};
  info.mipLevels = 1;
  info.arrayLayers = 2;
  info.samples = VK_SAMPLE_COUNT_1_BIT;
  info.usage = VK_IMAGE_USAGE_TRANSFER_SRC_BIT | VK_IMAGE_USAGE_TRANSFER_DST_BIT | VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT;
  VkImage image;
  REQUIRE(vkCreateImage(runtime.device, &info, nullptr, &image) == VK_SUCCESS);
  VkMemoryRequirements requirements{};
  vkGetImageMemoryRequirements(runtime.device, image, &requirements);
  REQUIRE(requirements.size == 24 * imageStride);
  VkMemoryAllocateInfo allocation{};
  allocation.sType = VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO;
  allocation.allocationSize = 320;
  VkDeviceMemory memory;
  REQUIRE(vkAllocateMemory(runtime.device, &allocation, nullptr, &memory) == VK_SUCCESS);
  REQUIRE(vkBindImageMemory(runtime.device, image, memory, 64) == VK_SUCCESS);
  VkBufferCreateInfo bufferInfo{};
  bufferInfo.sType = VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO;
  bufferInfo.size = 64;
  bufferInfo.usage = VK_BUFFER_USAGE_TRANSFER_DST_BIT;
  VkBuffer buffer;
  REQUIRE(vkCreateBuffer(runtime.device, &bufferInfo, nullptr, &buffer) == VK_SUCCESS);
  REQUIRE(vkBindBufferMemory(runtime.device, buffer, memory, 0) == VK_SUCCESS);
  void *mapped;
  REQUIRE(vkMapMemory(runtime.device, memory, 0, VK_WHOLE_SIZE, 0, &mapped) == VK_SUCCESS);
  auto *bytes = static_cast<uint8_t *>(mapped);
  std::fill_n(bytes, 320, 0xa5);
  CommandRecording recording(runtime.device);
  const auto command = recording.command;
  VkClearDepthStencilValue clear{0.5f, 0x12345678};
  VkImageSubresourceRange range{VK_IMAGE_ASPECT_STENCIL_BIT, 0, 1, 1, VK_REMAINING_ARRAY_LAYERS};
  vkCmdClearDepthStencilImage(command, image, VK_IMAGE_LAYOUT_GENERAL, &clear, 1, &range);
  clear.stencil = 0;
  VkBufferImageCopy copy{};
  copy.bufferOffset = 4;
  copy.bufferRowLength = 4;
  copy.bufferImageHeight = 3;
  copy.imageSubresource = {VK_IMAGE_ASPECT_STENCIL_BIT, 0, 1, 1};
  copy.imageOffset = {1, 1, 0};
  copy.imageExtent = {2, 2, 1};
  vkCmdCopyImageToBuffer(command, image, VK_IMAGE_LAYOUT_GENERAL, buffer, 1, &copy);
  REQUIRE(vkEndCommandBuffer(command) == VK_SUCCESS);
  REQUIRE(submitAndWait(runtime.queue, command) == VK_SUCCESS);
  const uint32_t expected = 0x78u;
  for (uint32_t row = 0; row < 2; ++row)
    for (uint32_t col = 0; col < 2; ++col) {
      uint32_t word = 0;
      std::memcpy(&word, bytes + 4 + (row * 4 + col) * width, width);
      REQUIRE(word == expected);
    }
  REQUIRE(bytes[3] == 0xa5);
  REQUIRE(bytes[4 + 2 * width] == 0xa5);
  REQUIRE(bytes[63] == 0xa5);
  REQUIRE(bytes[64] == 0xa5);
  REQUIRE(bytes[64 + 12 * imageStride - 1] == 0xa5);
  REQUIRE(bytes[64 + 24 * imageStride] == 0xa5);
  if (format == VK_FORMAT_D32_SFLOAT_S8_UINT)
    for (uint32_t pixel = 0; pixel < 24; ++pixel)
      for (uint32_t byte = 0; byte < 8; ++byte)
        if (byte != 4) REQUIRE(bytes[64 + pixel * 8 + byte] == 0xa5);
  vkDestroyBuffer(runtime.device, buffer, nullptr);
  vkDestroyImage(runtime.device, image, nullptr);
  vkUnmapMemory(runtime.device, memory);
  vkFreeMemory(runtime.device, memory, nullptr);
}

TEST_CASE("Vulkan cube views validate six-face groups and preserve mip and layer ranges") {
  VkImageCreateInfo info{};
  info.sType = VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO;
  info.imageType = VK_IMAGE_TYPE_2D;
  info.format = VK_FORMAT_R8G8B8A8_UNORM;
  info.extent = {8, 8, 1};
  info.mipLevels = 4;
  info.arrayLayers = 18;
  info.samples = VK_SAMPLE_COUNT_1_BIT;
  info.usage = VK_IMAGE_USAGE_SAMPLED_BIT;
  const bool compatible = GENERATE(false, true);
  info.flags = compatible ? VK_IMAGE_CREATE_CUBE_COMPATIBLE_BIT : 0;
  VkImage image;
  REQUIRE(vkCreateImage(VK_NULL_HANDLE, &info, nullptr, &image) == VK_SUCCESS);
  VkImageViewCreateInfo view{};
  view.sType = VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO;
  view.image = image;
  view.format = info.format;
  view.subresourceRange = {VK_IMAGE_ASPECT_COLOR_BIT, 1, 2, 0, 6};
  for (const auto type : {VK_IMAGE_VIEW_TYPE_CUBE, VK_IMAGE_VIEW_TYPE_CUBE_ARRAY})
    for (uint32_t base : {0u, 1u, 6u, 13u})
      for (uint32_t count : {1u, 5u, 6u, 7u, 12u, VK_REMAINING_ARRAY_LAYERS}) {
        view.viewType = type;
        view.subresourceRange.baseArrayLayer = base;
        view.subresourceRange.layerCount = count;
        const auto layers = count == VK_REMAINING_ARRAY_LAYERS ? 18 - base : count;
        const bool valid = compatible && layers <= 18 - base && layers % 6 == 0 && (type == VK_IMAGE_VIEW_TYPE_CUBE_ARRAY || layers == 6);
        VkImageView result = VK_NULL_HANDLE;
        REQUIRE(vkCreateImageView(VK_NULL_HANDLE, &view, nullptr, &result) == (valid ? VK_SUCCESS : VK_ERROR_FORMAT_NOT_SUPPORTED));
        if (valid) vkDestroyImageView(VK_NULL_HANDLE, result, nullptr);
      }
  vkDestroyImage(VK_NULL_HANDLE, image, nullptr);
}

TEST_CASE("Vulkan indirect draws accept the mandatory draw count with padded command strides", "[graphics-execution]") {
  TestDevice runtime;
  DiscardPipeline pipeline(runtime);
  const bool indexed = GENERATE(false, true);
  constexpr uint32_t count = 65535, stride = 32;
  constexpr VkDeviceSize prefix = 16, bytes = prefix + uint64_t{count} * stride;
  VkMemoryAllocateInfo allocation{};
  allocation.sType = VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO;
  allocation.allocationSize = bytes;
  VkDeviceMemory memory = VK_NULL_HANDLE;
  REQUIRE(vkAllocateMemory(runtime.device, &allocation, nullptr, &memory) == VK_SUCCESS);
  void *mapped = nullptr;
  REQUIRE(vkMapMemory(runtime.device, memory, 0, bytes, 0, &mapped) == VK_SUCCESS);
  std::memset(mapped, 0, bytes);
  // Every command has zero vertices but a nonzero instance count. Prefix and
  // stride padding must not be interpreted as commands.
  for (uint32_t i = 0; i < count; ++i) {
    const uint32_t one = 1;
    std::memcpy(static_cast<uint8_t *>(mapped) + prefix + uint64_t{i} * stride + 4, &one, 4);
  }
  VkBufferCreateInfo info{};
  info.sType = VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO;
  info.size = bytes;
  info.usage = VK_BUFFER_USAGE_INDIRECT_BUFFER_BIT | VK_BUFFER_USAGE_TRANSFER_DST_BIT | VK_BUFFER_USAGE_INDEX_BUFFER_BIT;
  VkBuffer buffer = VK_NULL_HANDLE;
  REQUIRE(vkCreateBuffer(runtime.device, &info, nullptr, &buffer) == VK_SUCCESS);
  REQUIRE(vkBindBufferMemory(runtime.device, buffer, memory, 0) == VK_SUCCESS);
  CommandRecording recording(runtime.device);
  const auto command = recording.command;
  pipeline.begin(command);
  if (indexed) vkCmdBindIndexBuffer(command, buffer, 0, VK_INDEX_TYPE_UINT16);
  if (indexed) vkCmdDrawIndexedIndirect(command, buffer, prefix, count, stride);
  else vkCmdDrawIndirect(command, buffer, prefix, count, stride);
  vkCmdEndRenderPass(command);
  vkCmdFillBuffer(command, buffer, 0, 4, 0x12345678);
  REQUIRE(vkEndCommandBuffer(command) == VK_SUCCESS);
  REQUIRE(submitAndWait(runtime.queue, command) == VK_SUCCESS);
  REQUIRE(*static_cast<uint32_t *>(mapped) == 0x12345678);
  vkUnmapMemory(runtime.device, memory);
  vkDestroyBuffer(runtime.device, buffer, nullptr);
  vkFreeMemory(runtime.device, memory, nullptr);
}

TEST_CASE("Vulkan volume mip layouts, padded transfers, partial copies and flipped blits preserve neighbouring slices",
          "[graphics-execution]") {
  TestDevice runtime;
  VkImageCreateInfo info{};
  info.sType = VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO;
  info.imageType = VK_IMAGE_TYPE_3D;
  info.format = VK_FORMAT_R32_SFLOAT;
  info.extent = {5, 3, 4};
  info.mipLevels = 3;
  info.arrayLayers = 1;
  info.samples = VK_SAMPLE_COUNT_1_BIT;
  info.tiling = VK_IMAGE_TILING_LINEAR;
  info.usage = VK_IMAGE_USAGE_TRANSFER_SRC_BIT | VK_IMAGE_USAGE_TRANSFER_DST_BIT | VK_IMAGE_USAGE_SAMPLED_BIT;
  VkImage images[2]{};
  for (auto &image : images)
    REQUIRE(vkCreateImage(runtime.device, &info, nullptr, &image) == VK_SUCCESS);
  for (unsigned invalid = 0; invalid < 4; ++invalid) {
    auto candidate = info;
    if (invalid == 0) candidate.arrayLayers = 2;
    if (invalid == 1) candidate.samples = VK_SAMPLE_COUNT_4_BIT;
    if (invalid == 2) candidate.mipLevels = 4;
    if (invalid == 3) candidate.extent.depth = 2049;
    VkImage rejected{};
    REQUIRE(vkCreateImage(runtime.device, &candidate, nullptr, &rejected) == VK_ERROR_FORMAT_NOT_SUPPORTED);
  }
  VkMemoryRequirements requirements{};
  vkGetImageMemoryRequirements(runtime.device, images[0], &requirements);
  REQUIRE(requirements.size == (60 + 4 + 1) * sizeof(float));
  for (uint32_t mip = 0; mip < 3; ++mip) {
    VkImageSubresource subresource{VK_IMAGE_ASPECT_COLOR_BIT, mip, 0};
    VkSubresourceLayout layout{};
    vkGetImageSubresourceLayout(runtime.device, images[0], &subresource, &layout);
    const uint32_t width = std::max(1u, 5u >> mip), height = std::max(1u, 3u >> mip), depth = std::max(1u, 4u >> mip);
    REQUIRE(layout.rowPitch == width * 4);
    REQUIRE(layout.depthPitch == width * height * 4);
    REQUIRE(layout.size == width * height * depth * 4);
    REQUIRE(layout.offset == (mip == 0 ? 0 : mip == 1 ? 240 : 256));
  }
  VkImageViewCreateInfo view{};
  view.sType = VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO;
  view.image = images[0];
  view.format = info.format;
  view.viewType = VK_IMAGE_VIEW_TYPE_3D;
  view.subresourceRange = {VK_IMAGE_ASPECT_COLOR_BIT, 1, VK_REMAINING_MIP_LEVELS, 0, 1};
  VkImageView handle{};
  REQUIRE(vkCreateImageView(runtime.device, &view, nullptr, &handle) == VK_SUCCESS);
  vkDestroyImageView(runtime.device, handle, nullptr);
  view.viewType = VK_IMAGE_VIEW_TYPE_2D;
  REQUIRE(vkCreateImageView(runtime.device, &view, nullptr, &handle) == VK_ERROR_FORMAT_NOT_SUPPORTED);
  VkMemoryAllocateInfo allocate{};
  allocate.sType = VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO;
  allocate.allocationSize = 4096;
  VkDeviceMemory memory{};
  REQUIRE(vkAllocateMemory(runtime.device, &allocate, nullptr, &memory) == VK_SUCCESS);
  REQUIRE(vkBindImageMemory(runtime.device, images[0], memory, 64) == VK_SUCCESS);
  REQUIRE(vkBindImageMemory(runtime.device, images[1], memory, 512) == VK_SUCCESS);
  VkBufferCreateInfo bufferInfo{};
  bufferInfo.sType = VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO;
  bufferInfo.size = 2048;
  bufferInfo.usage = VK_BUFFER_USAGE_TRANSFER_SRC_BIT | VK_BUFFER_USAGE_TRANSFER_DST_BIT;
  VkBuffer buffer{};
  REQUIRE(vkCreateBuffer(runtime.device, &bufferInfo, nullptr, &buffer) == VK_SUCCESS);
  REQUIRE(vkBindBufferMemory(runtime.device, buffer, memory, 1024) == VK_SUCCESS);
  void *mapped{};
  REQUIRE(vkMapMemory(runtime.device, memory, 0, VK_WHOLE_SIZE, 0, &mapped) == VK_SUCCESS);
  auto *bytes = static_cast<uint8_t *>(mapped);
  std::fill_n(bytes, 4096, 0xa5);
  for (uint32_t i = 0; i < 64; ++i) {
    const float value = static_cast<float>(i);
    std::memcpy(bytes + 1024 + i * 4, &value, 4);
  }
  std::vector<uint8_t> expected(bytes, bytes + 4096);
  const auto set = [&](size_t offset, float value) { std::memcpy(expected.data() + offset, &value, 4); };
  for (auto offset : {64u, 512u})
    for (uint32_t i = 0; i < 65; ++i)
      set(offset + i * 4, -1.f);
  // Two planes of a subvolume, with four-texel rows and three-row planes in the buffer.
  for (uint32_t z = 0; z < 2; ++z)
    for (uint32_t y = 0; y < 2; ++y)
      for (uint32_t x = 0; x < 2; ++x)
        set(64 + (((z + 1) * 3 + y + 1) * 5 + x + 1) * 4, static_cast<float>(z * 12 + y * 4 + x));
  // Copy to the other image at a different Z offset.
  for (uint32_t z = 0; z < 2; ++z)
    for (uint32_t y = 0; y < 2; ++y)
      for (uint32_t x = 0; x < 2; ++x)
        set(512 + ((z * 3 + y) * 5 + x) * 4, static_cast<float>(z * 12 + y * 4 + x));
  // Flip Z while reducing XY to the two-texel-wide, two-slice mip.
  for (uint32_t z = 0; z < 2; ++z)
    for (uint32_t x = 0; x < 2; ++x)
      set(512 + 240 + (z * 2 + x) * 4, static_cast<float>((1 - z) * 12 + 4 + x));
  for (uint32_t z = 0; z < 2; ++z)
    for (uint32_t y = 0; y < 2; ++y)
      for (uint32_t x = 0; x < 2; ++x)
        set(1024 + 512 + (z * 15 + y * 5 + x) * 4, static_cast<float>(z * 12 + y * 4 + x));
  CommandRecording recording(runtime.device);
  const auto command = recording.command;
  VkClearColorValue clear{};
  clear.float32[0] = -1.f;
  VkImageSubresourceRange range{VK_IMAGE_ASPECT_COLOR_BIT, 0, VK_REMAINING_MIP_LEVELS, 0, 1};
  for (auto image : images)
    vkCmdClearColorImage(command, image, VK_IMAGE_LAYOUT_GENERAL, &clear, 1, &range);
  VkBufferImageCopy upload{};
  upload.bufferRowLength = 4;
  upload.bufferImageHeight = 3;
  upload.imageSubresource = {VK_IMAGE_ASPECT_COLOR_BIT, 0, 0, 1};
  upload.imageOffset = {1, 1, 1};
  upload.imageExtent = {2, 2, 2};
  vkCmdCopyBufferToImage(command, buffer, images[0], VK_IMAGE_LAYOUT_GENERAL, 1, &upload);
  VkImageCopy copy{};
  copy.srcSubresource = copy.dstSubresource = {VK_IMAGE_ASPECT_COLOR_BIT, 0, 0, 1};
  copy.srcOffset = {1, 1, 1};
  copy.extent = {2, 2, 2};
  vkCmdCopyImage(command, images[0], VK_IMAGE_LAYOUT_GENERAL, images[1], VK_IMAGE_LAYOUT_GENERAL, 1, &copy);
  VkImageBlit blit{};
  blit.srcSubresource = {VK_IMAGE_ASPECT_COLOR_BIT, 0, 0, 1};
  blit.srcOffsets[0] = {1, 1, 3};
  blit.srcOffsets[1] = {3, 3, 1};
  blit.dstSubresource = {VK_IMAGE_ASPECT_COLOR_BIT, 1, 0, 1};
  blit.dstOffsets[1] = {2, 1, 2};
  vkCmdBlitImage(command, images[0], VK_IMAGE_LAYOUT_GENERAL, images[1], VK_IMAGE_LAYOUT_GENERAL, 1, &blit, VK_FILTER_NEAREST);
  VkBufferImageCopy download = upload;
  download.bufferOffset = 512;
  download.bufferRowLength = 5;
  download.imageOffset = {0, 0, 0};
  vkCmdCopyImageToBuffer(command, images[1], VK_IMAGE_LAYOUT_GENERAL, buffer, 1, &download);
  REQUIRE(vkEndCommandBuffer(command) == VK_SUCCESS);
  REQUIRE(submitAndWait(runtime.queue, command) == VK_SUCCESS);
  REQUIRE(std::equal(expected.begin(), expected.end(), bytes));
  vkDestroyBuffer(runtime.device, buffer, nullptr);
  for (auto image : images)
    vkDestroyImage(runtime.device, image, nullptr);
  vkFreeMemory(runtime.device, memory, nullptr);
}

TEST_CASE("Vulkan compressed mip storage and padded block transfers preserve edge blocks, arrays and guards", "[graphics-execution]") {
  TestDevice runtime;
  VkImageCreateInfo info{};
  info.sType = VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO;
  info.imageType = VK_IMAGE_TYPE_2D;
  info.format = VK_FORMAT_ETC2_R8G8B8_UNORM_BLOCK;
  info.extent = {7, 5, 1};
  info.mipLevels = 3;
  info.arrayLayers = 2;
  info.samples = VK_SAMPLE_COUNT_1_BIT;
  info.tiling = VK_IMAGE_TILING_OPTIMAL;
  info.usage = VK_IMAGE_USAGE_TRANSFER_SRC_BIT | VK_IMAGE_USAGE_TRANSFER_DST_BIT | VK_IMAGE_USAGE_SAMPLED_BIT;
  VkImage images[2]{};
  for (auto &image : images)
    REQUIRE(vkCreateImage(runtime.device, &info, nullptr, &image) == VK_SUCCESS);
  VkMemoryRequirements requirement{};
  vkGetImageMemoryRequirements(runtime.device, images[0], &requirement);
  REQUIRE(requirement.size == 96);
  VkImageSubresource subresource{VK_IMAGE_ASPECT_COLOR_BIT, 1, 1};
  VkSubresourceLayout layout{};
  vkGetImageSubresourceLayout(runtime.device, images[0], &subresource, &layout);
  REQUIRE(layout.offset == 72);
  REQUIRE(layout.rowPitch == 8);
  REQUIRE(layout.arrayPitch == 8);
  for (auto usage : {VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT, VK_IMAGE_USAGE_STORAGE_BIT}) {
    VkImageFormatProperties properties{};
    REQUIRE(vkGetPhysicalDeviceImageFormatProperties(VK_NULL_HANDLE, info.format, info.imageType, info.tiling, usage, 0, &properties)
            == VK_ERROR_FORMAT_NOT_SUPPORTED);
  }
  VkMemoryAllocateInfo allocate{};
  allocate.sType = VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO;
  allocate.allocationSize = 2048;
  VkDeviceMemory memory{};
  REQUIRE(vkAllocateMemory(runtime.device, &allocate, nullptr, &memory) == VK_SUCCESS);
  void *mapped{};
  REQUIRE(vkMapMemory(runtime.device, memory, 0, VK_WHOLE_SIZE, 0, &mapped) == VK_SUCCESS);
  auto *bytes = static_cast<uint8_t *>(mapped);
  std::fill(bytes, bytes + 2048, 0xa5);
  for (unsigned i = 0; i < 256; ++i)
    bytes[i] = static_cast<uint8_t>(i * 19 + 7);
  VkBufferCreateInfo bufferInfo{};
  bufferInfo.sType = VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO;
  bufferInfo.size = 1024;
  bufferInfo.usage = VK_BUFFER_USAGE_TRANSFER_SRC_BIT | VK_BUFFER_USAGE_TRANSFER_DST_BIT;
  VkBuffer buffer{};
  REQUIRE(vkCreateBuffer(runtime.device, &bufferInfo, nullptr, &buffer) == VK_SUCCESS);
  REQUIRE(vkBindBufferMemory(runtime.device, buffer, memory, 0) == VK_SUCCESS);
  for (unsigned i = 0; i < 2; ++i)
    REQUIRE(vkBindImageMemory(runtime.device, images[i], memory, 1024 + i * 128) == VK_SUCCESS);
  std::vector<uint8_t> expected(bytes, bytes + 2048);
  for (unsigned layer = 0; layer < 2; ++layer) {
    for (unsigned row = 0; row < 2; ++row) {
      std::memcpy(expected.data() + 1024 + layer * 32 + row * 16, bytes + layer * 48 + row * 24, 16);
      std::memcpy(expected.data() + 1152 + layer * 32 + row * 16, bytes + layer * 48 + row * 24, 16);
      std::memcpy(expected.data() + 1152 + layer * 32 + row * 16, bytes + layer * 48 + row * 24 + 8, 8);
      std::memcpy(expected.data() + 512 + layer * 48 + row * 24, expected.data() + 1152 + layer * 32 + row * 16, 16);
    }
    std::memcpy(expected.data() + 1024 + 64 + layer * 8, bytes + 192 + layer * 8, 8);
    std::memcpy(expected.data() + 1152 + 64 + layer * 8, bytes + 192 + layer * 8, 8);
  }
  CommandRecording recording(runtime.device);
  const auto command = recording.command;
  VkBufferImageCopy upload{};
  upload.bufferRowLength = 12;
  upload.bufferImageHeight = 8;
  upload.imageSubresource = {VK_IMAGE_ASPECT_COLOR_BIT, 0, 0, 2};
  upload.imageExtent = {7, 5, 1};
  vkCmdCopyBufferToImage(command, buffer, images[0], VK_IMAGE_LAYOUT_GENERAL, 1, &upload);
  auto mip = upload;
  mip.bufferRowLength = mip.bufferImageHeight = 0;
  mip.bufferOffset = 192;
  mip.imageSubresource.mipLevel = 1;
  mip.imageExtent = {3, 2, 1};
  vkCmdCopyBufferToImage(command, buffer, images[0], VK_IMAGE_LAYOUT_GENERAL, 1, &mip);
  VkImageCopy copies[3]{};
  copies[0].srcSubresource = copies[0].dstSubresource = upload.imageSubresource;
  copies[0].extent = upload.imageExtent;
  copies[1].srcSubresource = copies[1].dstSubresource = mip.imageSubresource;
  copies[1].extent = mip.imageExtent;
  copies[2] = copies[0];
  copies[2].srcOffset.x = 4;
  copies[2].extent.width = 3;
  vkCmdCopyImage(command, images[0], VK_IMAGE_LAYOUT_GENERAL, images[1], VK_IMAGE_LAYOUT_GENERAL, 3, copies);
  upload.bufferOffset = 512;
  vkCmdCopyImageToBuffer(command, images[1], VK_IMAGE_LAYOUT_GENERAL, buffer, 1, &upload);
  REQUIRE(vkEndCommandBuffer(command) == VK_SUCCESS);
  REQUIRE(submitAndWait(runtime.queue, command) == VK_SUCCESS);
  REQUIRE(std::equal(expected.begin(), expected.end(), bytes));
  vkDestroyBuffer(runtime.device, buffer, nullptr);
  for (auto image : images)
    vkDestroyImage(runtime.device, image, nullptr);
  vkFreeMemory(runtime.device, memory, nullptr);
}

TEST_CASE("Vulkan multiview load and explicit clears preserve inactive layers and previous subpass writes", "[graphics-execution]") {
  const bool imageless = GENERATE(false, true);
  TestDevice runtime;
  VkImageCreateInfo info{};
  info.sType = VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO;
  info.imageType = VK_IMAGE_TYPE_2D;
  info.format = VK_FORMAT_R32_UINT;
  info.extent = {2, 2, 1};
  info.mipLevels = 1;
  info.arrayLayers = 6;
  info.samples = VK_SAMPLE_COUNT_1_BIT;
  info.usage = VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT;
  VkImage image;
  REQUIRE(vkCreateImage(runtime.device, &info, nullptr, &image) == VK_SUCCESS);
  VkMemoryAllocateInfo allocation{};
  allocation.sType = VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO;
  allocation.allocationSize = 256;
  VkDeviceMemory memory;
  REQUIRE(vkAllocateMemory(runtime.device, &allocation, nullptr, &memory) == VK_SUCCESS);
  REQUIRE(vkBindImageMemory(runtime.device, image, memory, 64) == VK_SUCCESS);
  void *mapped;
  REQUIRE(vkMapMemory(runtime.device, memory, 0, VK_WHOLE_SIZE, 0, &mapped) == VK_SUCCESS);
  auto *words = static_cast<uint32_t *>(mapped);
  std::fill_n(words, 64, 0xdeadbeefu);
  VkImageViewCreateInfo viewInfo{};
  viewInfo.sType = VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO;
  viewInfo.image = image;
  viewInfo.viewType = VK_IMAGE_VIEW_TYPE_2D_ARRAY;
  viewInfo.format = info.format;
  viewInfo.subresourceRange = {VK_IMAGE_ASPECT_COLOR_BIT, 0, 1, 1, 4};
  VkImageView view;
  REQUIRE(vkCreateImageView(runtime.device, &viewInfo, nullptr, &view) == VK_SUCCESS);
  VkAttachmentDescription attachment{};
  attachment.format = info.format;
  attachment.samples = info.samples;
  attachment.loadOp = VK_ATTACHMENT_LOAD_OP_CLEAR;
  attachment.storeOp = VK_ATTACHMENT_STORE_OP_STORE;
  attachment.initialLayout = attachment.finalLayout = VK_IMAGE_LAYOUT_GENERAL;
  const VkAttachmentReference reference{0, VK_IMAGE_LAYOUT_GENERAL};
  std::array<VkSubpassDescription, 3> subpasses{};
  for (auto &subpass : subpasses) {
    subpass.pipelineBindPoint = VK_PIPELINE_BIND_POINT_GRAPHICS;
    subpass.colorAttachmentCount = 1;
    subpass.pColorAttachments = &reference;
  }
  const uint32_t masks[]{5, 10, 5};
  VkRenderPassMultiviewCreateInfo multiview{};
  multiview.sType = VK_STRUCTURE_TYPE_RENDER_PASS_MULTIVIEW_CREATE_INFO;
  multiview.subpassCount = 3;
  multiview.pViewMasks = masks;
  VkRenderPassCreateInfo passInfo{};
  passInfo.sType = VK_STRUCTURE_TYPE_RENDER_PASS_CREATE_INFO;
  passInfo.pNext = &multiview;
  passInfo.attachmentCount = 1;
  passInfo.pAttachments = &attachment;
  passInfo.subpassCount = 3;
  passInfo.pSubpasses = subpasses.data();
  VkRenderPass pass;
  REQUIRE(vkCreateRenderPass(runtime.device, &passInfo, nullptr, &pass) == VK_SUCCESS);
  VkFramebufferCreateInfo fb{};
  fb.sType = VK_STRUCTURE_TYPE_FRAMEBUFFER_CREATE_INFO;
  fb.renderPass = pass;
  fb.attachmentCount = 1;
  fb.pAttachments = &view;
  fb.width = fb.height = 2;
  fb.layers = 1;
  VkFramebufferAttachmentImageInfo attachmentInfo{};
  attachmentInfo.sType = VK_STRUCTURE_TYPE_FRAMEBUFFER_ATTACHMENT_IMAGE_INFO;
  attachmentInfo.usage = info.usage;
  attachmentInfo.width = attachmentInfo.height = 2;
  attachmentInfo.layerCount = 4;
  attachmentInfo.viewFormatCount = 1;
  attachmentInfo.pViewFormats = &info.format;
  VkFramebufferAttachmentsCreateInfo attachmentInfos{};
  attachmentInfos.sType = VK_STRUCTURE_TYPE_FRAMEBUFFER_ATTACHMENTS_CREATE_INFO;
  attachmentInfos.attachmentImageInfoCount = 1;
  attachmentInfos.pAttachmentImageInfos = &attachmentInfo;
  if (imageless) {
    fb.flags = VK_FRAMEBUFFER_CREATE_IMAGELESS_BIT;
    fb.pNext = &attachmentInfos;
    fb.pAttachments = nullptr;
  }
  VkFramebuffer framebuffer;
  REQUIRE(vkCreateFramebuffer(runtime.device, &fb, nullptr, &framebuffer) == VK_SUCCESS);
  VkQueryPoolCreateInfo queryInfo{};
  queryInfo.sType = VK_STRUCTURE_TYPE_QUERY_POOL_CREATE_INFO;
  queryInfo.queryType = VK_QUERY_TYPE_TIMESTAMP;
  queryInfo.queryCount = 4;
  VkQueryPool query;
  REQUIRE(vkCreateQueryPool(runtime.device, &queryInfo, nullptr, &query) == VK_SUCCESS);
  CommandRecording recording(runtime.device);
  const auto command = recording.command;
  vkCmdResetQueryPool(command, query, 0, 4);
  VkClearValue clear{};
  clear.color.uint32[0] = 7;
  VkRenderPassBeginInfo render{};
  render.sType = VK_STRUCTURE_TYPE_RENDER_PASS_BEGIN_INFO;
  render.renderPass = pass;
  render.framebuffer = framebuffer;
  render.renderArea = {{0, 0}, {2, 2}};
  render.clearValueCount = 1;
  render.pClearValues = &clear;
  VkRenderPassAttachmentBeginInfo boundAttachments{};
  boundAttachments.sType = VK_STRUCTURE_TYPE_RENDER_PASS_ATTACHMENT_BEGIN_INFO;
  boundAttachments.attachmentCount = 1;
  boundAttachments.pAttachments = &view;
  if (imageless) render.pNext = &boundAttachments;
  vkCmdBeginRenderPass(command, &render, VK_SUBPASS_CONTENTS_INLINE);
  VkClearAttachment explicitClear{};
  explicitClear.aspectMask = VK_IMAGE_ASPECT_COLOR_BIT;
  explicitClear.clearValue.color.uint32[0] = 19;
  VkClearRect rect{{{1, 0}, {1, 1}}, 0, 1};
  vkCmdClearAttachments(command, 1, &explicitClear, 1, &rect);
  vkCmdWriteTimestamp(command, VK_PIPELINE_STAGE_BOTTOM_OF_PIPE_BIT, query, 1);
  vkCmdNextSubpass(command, VK_SUBPASS_CONTENTS_INLINE);
  vkCmdNextSubpass(command, VK_SUBPASS_CONTENTS_INLINE);
  vkCmdEndRenderPass(command);
  REQUIRE(vkEndCommandBuffer(command) == VK_SUCCESS);
  REQUIRE(submitAndWait(runtime.queue, command) == VK_SUCCESS);
  for (uint32_t layer = 0; layer < 6; ++layer)
    for (uint32_t pixel = 0; pixel < 4; ++pixel) {
      const auto expected = layer == 0 || layer == 5 ? 0xdeadbeefu : ((layer == 1 || layer == 3) && pixel == 1 ? 19u : 7u);
      REQUIRE(words[16 + layer * 4 + pixel] == expected);
    }
  REQUIRE(words[15] == 0xdeadbeefu);
  REQUIRE(words[40] == 0xdeadbeefu);
  uint64_t results[4][2]{};
  REQUIRE(vkGetQueryPoolResults(runtime.device, query, 0, 4, sizeof(results), results, sizeof(results[0]),
                                VK_QUERY_RESULT_64_BIT | VK_QUERY_RESULT_WITH_AVAILABILITY_BIT)
          == VK_NOT_READY);
  REQUIRE(results[0][1] == 0);
  REQUIRE(results[1][1] == 1);
  REQUIRE(results[2][1] == 1);
  REQUIRE(results[3][1] == 0);
  REQUIRE(results[1][0] == results[2][0]);
  vkDestroyQueryPool(runtime.device, query, nullptr);
  vkDestroyFramebuffer(runtime.device, framebuffer, nullptr);
  vkDestroyRenderPass(runtime.device, pass, nullptr);
  vkDestroyImageView(runtime.device, view, nullptr);
  vkDestroyImage(runtime.device, image, nullptr);
  vkFreeMemory(runtime.device, memory, nullptr);
}

TEST_CASE("Vulkan sample-zero depth and stencil resolves select the first sample across views", "[graphics-execution]") {
  const auto format = GENERATE(VK_FORMAT_D16_UNORM, VK_FORMAT_D32_SFLOAT, VK_FORMAT_D32_SFLOAT_S8_UINT, VK_FORMAT_S8_UINT);
  const bool depth = format != VK_FORMAT_S8_UINT;
  const bool stencil = format == VK_FORMAT_S8_UINT || format == VK_FORMAT_D32_SFLOAT_S8_UINT;
  const uint32_t stride = format == VK_FORMAT_D16_UNORM ? 2 : format == VK_FORMAT_S8_UINT ? 1 : stencil ? 8 : 4;
  const uint32_t depthBytes = format == VK_FORMAT_D16_UNORM ? 2 : 4;
  const uint32_t stencilByte = depth ? 4 : 0;
  const VkImageAspectFlags aspects = (depth ? VK_IMAGE_ASPECT_DEPTH_BIT : 0) | (stencil ? VK_IMAGE_ASPECT_STENCIL_BIT : 0);
  TestDevice runtime;
  VkPhysicalDeviceDepthStencilResolveProperties resolveProperties{};
  resolveProperties.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEPTH_STENCIL_RESOLVE_PROPERTIES;
  VkPhysicalDeviceVulkan12Properties aggregate{};
  aggregate.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_2_PROPERTIES;
  aggregate.pNext = &resolveProperties;
  VkPhysicalDeviceProperties2 properties{};
  properties.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PROPERTIES_2;
  properties.pNext = &aggregate;
  vkGetPhysicalDeviceProperties2(VK_NULL_HANDLE, &properties);
  REQUIRE(resolveProperties.supportedDepthResolveModes == VK_RESOLVE_MODE_SAMPLE_ZERO_BIT);
  REQUIRE(resolveProperties.supportedStencilResolveModes == VK_RESOLVE_MODE_SAMPLE_ZERO_BIT);
  REQUIRE(aggregate.supportedDepthResolveModes == resolveProperties.supportedDepthResolveModes);
  REQUIRE(aggregate.supportedStencilResolveModes == resolveProperties.supportedStencilResolveModes);
  VkMemoryAllocateInfo allocation{};
  allocation.sType = VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO;
  allocation.allocationSize = 1024;
  VkDeviceMemory memory;
  REQUIRE(vkAllocateMemory(runtime.device, &allocation, nullptr, &memory) == VK_SUCCESS);
  void *mapped;
  REQUIRE(vkMapMemory(runtime.device, memory, 0, VK_WHOLE_SIZE, 0, &mapped) == VK_SUCCESS);
  auto *bytes = static_cast<uint8_t *>(mapped);
  std::memset(bytes, 0xcc, 1024);
  std::array<VkImage, 2> images{};
  std::array<VkImageView, 2> views{};
  std::array<VkAttachmentDescription2, 2> attachments{};
  VkAttachmentDescriptionStencilLayout stencilDescription{};
  stencilDescription.sType = VK_STRUCTURE_TYPE_ATTACHMENT_DESCRIPTION_STENCIL_LAYOUT;
  stencilDescription.stencilInitialLayout = VK_IMAGE_LAYOUT_GENERAL;
  stencilDescription.stencilFinalLayout = VK_IMAGE_LAYOUT_STENCIL_READ_ONLY_OPTIMAL;
  for (uint32_t i = 0; i < 2; ++i) {
    VkImageCreateInfo image{};
    image.sType = VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO;
    image.imageType = VK_IMAGE_TYPE_2D;
    image.format = format;
    image.extent = {2, 2, 1};
    image.mipLevels = 1;
    image.arrayLayers = 2;
    image.samples = i == 0 ? VK_SAMPLE_COUNT_4_BIT : VK_SAMPLE_COUNT_1_BIT;
    image.usage = VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT;
    REQUIRE(vkCreateImage(runtime.device, &image, nullptr, &images[i]) == VK_SUCCESS);
    REQUIRE(vkBindImageMemory(runtime.device, images[i], memory, i == 0 ? 64 : 512) == VK_SUCCESS);
    VkImageViewCreateInfo view{};
    view.sType = VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO;
    view.image = images[i];
    view.viewType = VK_IMAGE_VIEW_TYPE_2D_ARRAY;
    view.format = format;
    view.subresourceRange = {aspects, 0, 1, 0, 2};
    REQUIRE(vkCreateImageView(runtime.device, &view, nullptr, &views[i]) == VK_SUCCESS);
    auto &attachment = attachments[i];
    attachment.sType = VK_STRUCTURE_TYPE_ATTACHMENT_DESCRIPTION_2;
    attachment.pNext = depth && stencil ? &stencilDescription : nullptr;
    attachment.format = format;
    attachment.samples = image.samples;
    attachment.loadOp = attachment.stencilLoadOp = VK_ATTACHMENT_LOAD_OP_LOAD;
    attachment.storeOp = attachment.stencilStoreOp = VK_ATTACHMENT_STORE_OP_STORE;
    attachment.initialLayout = VK_IMAGE_LAYOUT_GENERAL;
    attachment.finalLayout = depth ? VK_IMAGE_LAYOUT_DEPTH_READ_ONLY_OPTIMAL : VK_IMAGE_LAYOUT_STENCIL_READ_ONLY_OPTIMAL;
  }
  for (uint32_t pixel = 0; pixel < 8; ++pixel)
    for (uint32_t sample = 0; sample < 4; ++sample) {
      auto *target = bytes + 64 + (pixel * 4 + sample) * stride;
      if (depth) {
        if (depthBytes == 2) {
          const uint16_t value = 8000 * (sample + 1) + pixel;
          std::memcpy(target, &value, 2);
        } else {
          const float value = (sample + 1) * .125f + pixel * .01f;
          std::memcpy(target, &value, 4);
        }
      }
      if (stencil) target[stencilByte] = 20 + sample + pixel;
    }
  VkAttachmentReferenceStencilLayout stencilReference{};
  stencilReference.sType = VK_STRUCTURE_TYPE_ATTACHMENT_REFERENCE_STENCIL_LAYOUT;
  stencilReference.stencilLayout = VK_IMAGE_LAYOUT_STENCIL_ATTACHMENT_OPTIMAL;
  VkAttachmentReference2 source{}, destination{};
  source.sType = destination.sType = VK_STRUCTURE_TYPE_ATTACHMENT_REFERENCE_2;
  source.pNext = destination.pNext = depth && stencil ? &stencilReference : nullptr;
  source.attachment = 0;
  destination.attachment = 1;
  source.layout = destination.layout = depth ? VK_IMAGE_LAYOUT_DEPTH_ATTACHMENT_OPTIMAL : VK_IMAGE_LAYOUT_STENCIL_ATTACHMENT_OPTIMAL;
  VkSubpassDescriptionDepthStencilResolve resolve{};
  resolve.sType = VK_STRUCTURE_TYPE_SUBPASS_DESCRIPTION_DEPTH_STENCIL_RESOLVE;
  resolve.depthResolveMode = depth ? VK_RESOLVE_MODE_SAMPLE_ZERO_BIT : VK_RESOLVE_MODE_NONE;
  resolve.stencilResolveMode = stencil ? VK_RESOLVE_MODE_SAMPLE_ZERO_BIT : VK_RESOLVE_MODE_NONE;
  resolve.pDepthStencilResolveAttachment = &destination;
  VkSubpassDescription2 subpass{};
  subpass.sType = VK_STRUCTURE_TYPE_SUBPASS_DESCRIPTION_2;
  subpass.pNext = &resolve;
  subpass.pipelineBindPoint = VK_PIPELINE_BIND_POINT_GRAPHICS;
  subpass.viewMask = 3;
  subpass.pDepthStencilAttachment = &source;
  VkRenderPassCreateInfo2 passInfo{};
  passInfo.sType = VK_STRUCTURE_TYPE_RENDER_PASS_CREATE_INFO_2;
  passInfo.attachmentCount = 2;
  passInfo.pAttachments = attachments.data();
  passInfo.subpassCount = 1;
  passInfo.pSubpasses = &subpass;
  VkRenderPass pass;
  REQUIRE(vkCreateRenderPass2(runtime.device, &passInfo, nullptr, &pass) == VK_SUCCESS);
  VkFramebufferCreateInfo framebufferInfo{};
  framebufferInfo.sType = VK_STRUCTURE_TYPE_FRAMEBUFFER_CREATE_INFO;
  framebufferInfo.renderPass = pass;
  framebufferInfo.attachmentCount = 2;
  framebufferInfo.pAttachments = views.data();
  framebufferInfo.width = framebufferInfo.height = 2;
  framebufferInfo.layers = 1;
  VkFramebuffer framebuffer;
  REQUIRE(vkCreateFramebuffer(runtime.device, &framebufferInfo, nullptr, &framebuffer) == VK_SUCCESS);
  CommandRecording recording(runtime.device);
  const auto command = recording.command;
  VkRenderPassBeginInfo render{};
  render.sType = VK_STRUCTURE_TYPE_RENDER_PASS_BEGIN_INFO;
  render.renderPass = pass;
  render.framebuffer = framebuffer;
  render.renderArea = {{0, 0}, {2, 2}};
  vkCmdBeginRenderPass(command, &render, VK_SUBPASS_CONTENTS_INLINE);
  vkCmdEndRenderPass(command);
  REQUIRE(vkEndCommandBuffer(command) == VK_SUCCESS);
  REQUIRE(submitAndWait(runtime.queue, command) == VK_SUCCESS);
  for (uint32_t pixel = 0; pixel < 8; ++pixel)
    for (uint32_t byte = 0; byte < stride; ++byte) {
      const bool copied = (depth && byte < depthBytes) || (stencil && byte == stencilByte);
      REQUIRE(bytes[512 + pixel * stride + byte] == (copied ? bytes[64 + pixel * 4 * stride + byte] : 0xcc));
    }
  REQUIRE(bytes[511] == 0xcc);
  REQUIRE(bytes[512 + 8 * stride] == 0xcc);
  vkDestroyFramebuffer(runtime.device, framebuffer, nullptr);
  vkDestroyRenderPass(runtime.device, pass, nullptr);
  for (uint32_t i = 0; i < 2; ++i) {
    vkDestroyImageView(runtime.device, views[i], nullptr);
    vkDestroyImage(runtime.device, images[i], nullptr);
  }
  vkFreeMemory(runtime.device, memory, nullptr);
}

namespace {
struct AllocationAudit {
  struct Entry {
    size_t size;
    VkSystemAllocationScope scope;
  };
  std::map<void *, Entry> live;
  size_t calls = 0, frees = 0, failAt = SIZE_MAX;
  bool wrongThread = false, wrongFree = false, wrongAlignment = false;
  std::thread::id thread = std::this_thread::get_id();
  static void *VKAPI_CALL allocate(void *data, size_t size, size_t alignment, VkSystemAllocationScope scope) {
    auto &audit = *static_cast<AllocationAudit *>(data);
    audit.wrongThread |= std::this_thread::get_id() != audit.thread;
    if (audit.calls++ >= audit.failAt) return nullptr;
    void *p = nullptr;
    if (posix_memalign(&p, std::max(alignment, sizeof(void *)), size) != 0) return nullptr;
    audit.wrongAlignment |= (reinterpret_cast<uintptr_t>(p) % alignment) != 0;
    audit.live.emplace(p, Entry{size, scope});
    return p;
  }
  static void VKAPI_CALL free(void *data, void *p) {
    if (!p) return;
    auto &audit = *static_cast<AllocationAudit *>(data);
    audit.wrongThread |= std::this_thread::get_id() != audit.thread;
    if (audit.live.erase(p) != 1) audit.wrongFree = true;
    ++audit.frees;
    std::free(p);
  }
  static void *VKAPI_CALL reallocate(void *data, void *p, size_t size, size_t alignment, VkSystemAllocationScope scope) {
    auto &audit = *static_cast<AllocationAudit *>(data);
    if (!size) {
      free(data, p);
      return nullptr;
    }
    void *result = allocate(data, size, alignment, scope);
    if (result && p) {
      std::memcpy(result, p, std::min(size, audit.live.at(p).size));
      free(data, p);
    }
    return result;
  }
  VkAllocationCallbacks callbacks() { return {this, allocate, reallocate, free, nullptr, nullptr}; }
  void requireClean() {
    REQUIRE(live.empty());
    REQUIRE_FALSE(wrongThread);
    REQUIRE_FALSE(wrongFree);
    REQUIRE_FALSE(wrongAlignment);
  }
};
VkInstance allocatorInstance(const VkAllocationCallbacks *callbacks) {
  VkInstanceCreateInfo info{};
  info.sType = VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO;
  VkInstance instance{};
  REQUIRE(vkCreateInstance(&info, callbacks, &instance) == VK_SUCCESS);
  return instance;
}
VkDevice allocatorDevice(VkInstance instance, const VkAllocationCallbacks *callbacks) {
  uint32_t count = 1;
  VkPhysicalDevice physical{};
  REQUIRE(vkEnumeratePhysicalDevices(instance, &count, &physical) == VK_SUCCESS);
  const float priority = 1;
  VkDeviceQueueCreateInfo queue{};
  queue.sType = VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO;
  queue.queueCount = 1;
  queue.pQueuePriorities = &priority;
  VkDeviceCreateInfo info{};
  info.sType = VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO;
  info.queueCreateInfoCount = 1;
  info.pQueueCreateInfos = &queue;
  VkDevice device{};
  REQUIRE(vkCreateDevice(physical, &info, callbacks, &device) == VK_SUCCESS);
  return device;
}
} // namespace

TEST_CASE("Vulkan pipeline batches preserve partial success and roll back allocation failures", "[vulkan][allocation]") {
  TestDevice runtime;
  gemstone_test::RpcFixture fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  fixture.shaderDescriptors({});
  // The RPC fixture supplies prepared metadata; these header words are only a transport payload.
  const uint32_t shader[]{0x07230203, 0x00010000, 0, 1, 0};
  VkShaderModuleCreateInfo moduleInfo{};
  moduleInfo.sType = VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO;
  moduleInfo.codeSize = sizeof(shader);
  moduleInfo.pCode = shader;
  VkShaderModule module{};
  REQUIRE(vkCreateShaderModule(runtime.device, &moduleInfo, nullptr, &module) == VK_SUCCESS);
  VkComputePipelineCreateInfo info{};
  info.sType = VK_STRUCTURE_TYPE_COMPUTE_PIPELINE_CREATE_INFO;
  info.stage.sType = VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO;
  info.stage.stage = VK_SHADER_STAGE_COMPUTE_BIT;
  info.stage.module = module;
  info.stage.pName = "main";
  std::array infos{info, info, info};
  SECTION("an invalid middle shader does not discard earlier or later successes") {
    infos[1].stage.module = VK_NULL_HANDLE;
    AllocationAudit audit;
    auto callbacks = audit.callbacks();
    std::array<VkPipeline, 3> output{};
    output.fill(reinterpret_cast<VkPipeline>(uintptr_t{1}));
    REQUIRE(vkCreateComputePipelines(runtime.device, VK_NULL_HANDLE, infos.size(), infos.data(), &callbacks, output.data())
            == VK_ERROR_INVALID_SHADER_NV);
    REQUIRE(output[0] != VK_NULL_HANDLE);
    REQUIRE(output[1] == VK_NULL_HANDLE);
    REQUIRE(output[2] != VK_NULL_HANDLE);
    for (auto pipeline : output)
      vkDestroyPipeline(runtime.device, pipeline, &callbacks);
    audit.requireClean();
  }
  SECTION("every callback allocation failure leaves only successful pipelines alive") {
    size_t allocationCount = 0;
    const auto create = [&](size_t failAt) {
      AllocationAudit audit;
      audit.failAt = failAt;
      auto callbacks = audit.callbacks();
      std::array<VkPipeline, 3> output{};
      output.fill(reinterpret_cast<VkPipeline>(uintptr_t{1}));
      const auto result = vkCreateComputePipelines(runtime.device, VK_NULL_HANDLE, infos.size(), infos.data(), &callbacks, output.data());
      if (failAt == SIZE_MAX) {
        REQUIRE(result == VK_SUCCESS);
        allocationCount = audit.calls;
      } else {
        REQUIRE(result == VK_ERROR_OUT_OF_HOST_MEMORY);
        REQUIRE(std::ranges::find(output, VK_NULL_HANDLE) != output.end());
      }
      for (auto pipeline : output)
        vkDestroyPipeline(runtime.device, pipeline, &callbacks);
      audit.requireClean();
    };
    create(SIZE_MAX);
    REQUIRE(allocationCount > 0);
    for (size_t failAt = 0; failAt < allocationCount; ++failAt)
      create(failAt);
  }
  vkDestroyShaderModule(runtime.device, module, nullptr);
}

TEST_CASE("Vulkan host callbacks cover storage and clean every creation failure", "[vulkan][allocation]") {
  TestDevice runtime;
  VkBufferCreateInfo info{};
  info.sType = VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO;
  info.size = 256;
  info.usage = VK_BUFFER_USAGE_STORAGE_BUFFER_BIT;
  size_t failures = 0;
  for (size_t failAt = 0; failAt < 32; ++failAt) {
    AllocationAudit audit;
    audit.failAt = failAt;
    auto callbacks = audit.callbacks();
    VkBuffer buffer = reinterpret_cast<VkBuffer>(uintptr_t{1});
    VkResult result{};
    REQUIRE_NOTHROW(result = vkCreateBuffer(runtime.device, &info, &callbacks, &buffer));
    if (result == VK_SUCCESS) {
      REQUIRE(audit.live.size() >= 3); // arena, handle, registry state
      for (const auto &[p, entry] : audit.live) {
        (void)p;
        REQUIRE(entry.scope == VK_SYSTEM_ALLOCATION_SCOPE_OBJECT);
      }
      vkDestroyBuffer(runtime.device, buffer, &callbacks);
      audit.requireClean();
      break;
    }
    REQUIRE(result == VK_ERROR_OUT_OF_HOST_MEMORY);
    REQUIRE(buffer == VK_NULL_HANDLE);
    audit.requireClean();
    ++failures;
  }
  REQUIRE(failures >= 3);
  REQUIRE(failures < 32);
}

TEST_CASE("Vulkan render pass creation retains callbacks and rolls back every allocation failure", "[vulkan][allocation]") {
  TestDevice runtime;
  const auto create = GENERATE(vkCreateRenderPass2, vkCreateRenderPass2KHR);
  VkAttachmentDescription2 attachment{};
  attachment.sType = VK_STRUCTURE_TYPE_ATTACHMENT_DESCRIPTION_2;
  attachment.format = VK_FORMAT_R8G8B8A8_UNORM;
  attachment.samples = VK_SAMPLE_COUNT_1_BIT;
  VkAttachmentReference2 reference{};
  reference.sType = VK_STRUCTURE_TYPE_ATTACHMENT_REFERENCE_2;
  reference.layout = VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL;
  VkSubpassDescription2 subpass{};
  subpass.sType = VK_STRUCTURE_TYPE_SUBPASS_DESCRIPTION_2;
  subpass.pipelineBindPoint = VK_PIPELINE_BIND_POINT_GRAPHICS;
  subpass.colorAttachmentCount = 1;
  subpass.pColorAttachments = &reference;
  VkRenderPassCreateInfo2 info{};
  info.sType = VK_STRUCTURE_TYPE_RENDER_PASS_CREATE_INFO_2;
  info.attachmentCount = 1;
  info.pAttachments = &attachment;
  info.subpassCount = 1;
  info.pSubpasses = &subpass;
  size_t failures = 0;
  // Bound the failure sweep so a permanently failing creation cannot hang the suite.
  for (size_t failAt = 0; failAt < 64; ++failAt) {
    AllocationAudit audit;
    audit.failAt = failAt;
    auto callbacks = audit.callbacks();
    VkRenderPass pass = reinterpret_cast<VkRenderPass>(uintptr_t{1});
    VkResult result{};
    REQUIRE_NOTHROW(result = create(runtime.device, &info, &callbacks, &pass));
    if (result == VK_SUCCESS) {
      REQUIRE_FALSE(audit.live.empty());
      for (const auto &[pointer, entry] : audit.live) {
        (void)pointer;
        REQUIRE(entry.scope == VK_SYSTEM_ALLOCATION_SCOPE_OBJECT);
      }
      vkDestroyRenderPass(runtime.device, pass, &callbacks);
      audit.requireClean();
      break;
    }
    REQUIRE(result == VK_ERROR_OUT_OF_HOST_MEMORY);
    REQUIRE(pass == VK_NULL_HANDLE);
    audit.requireClean();
    ++failures;
  }
  REQUIRE(failures > 0);
  REQUIRE(failures < 64);
}

TEST_CASE("Vulkan allocators are copied and inherited independently by each instance", "[vulkan][allocation]") {
  AllocationAudit first, second, object;
  auto firstCallbacks = first.callbacks(), secondCallbacks = second.callbacks(), objectCallbacks = object.callbacks();
  const auto firstInstance = allocatorInstance(&firstCallbacks), secondInstance = allocatorInstance(&secondCallbacks);
  const auto firstDevice = allocatorDevice(firstInstance, nullptr), secondDevice = allocatorDevice(secondInstance, nullptr);
  const auto beforeFirst = first.calls, beforeSecond = second.calls;
  // The runtime must retain the callback contents, not this structure's address.
  firstCallbacks = {};
  VkBufferCreateInfo info{};
  info.sType = VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO;
  info.size = 64;
  info.usage = VK_BUFFER_USAGE_STORAGE_BUFFER_BIT;
  VkBuffer inherited{}, explicitBuffer{};
  REQUIRE(vkCreateBuffer(firstDevice, &info, nullptr, &inherited) == VK_SUCCESS);
  REQUIRE(first.calls > beforeFirst);
  REQUIRE(second.calls == beforeSecond);
  const auto afterInherited = first.calls;
  REQUIRE(vkCreateBuffer(firstDevice, &info, &objectCallbacks, &explicitBuffer) == VK_SUCCESS);
  REQUIRE(first.calls == afterInherited);
  REQUIRE(object.calls >= 3);
  vkDestroyBuffer(firstDevice, explicitBuffer, &objectCallbacks);
  object.requireClean();
  vkDestroyBuffer(firstDevice, inherited, nullptr);
  vkDestroyDevice(firstDevice, nullptr);
  vkDestroyDevice(secondDevice, nullptr);
  firstCallbacks = first.callbacks();
  vkDestroyInstance(firstInstance, &firstCallbacks);
  vkDestroyInstance(secondInstance, &secondCallbacks);
  first.requireClean();
  second.requireClean();
}

TEST_CASE("Vulkan destroy accepts a compatible allocator with different user data", "[vulkan][allocation]") {
  TestDevice runtime;
  AllocationAudit audit;
  auto callbacks = audit.callbacks();
  VkEventCreateInfo info{};
  info.sType = VK_STRUCTURE_TYPE_EVENT_CREATE_INFO;
  VkEvent event{};
  REQUIRE(vkCreateEvent(runtime.device, &info, &callbacks, &event) == VK_SUCCESS);
  struct Compatible {
    AllocationAudit *audit;
    size_t calls = 0;
  } compatible{&audit};
  callbacks.pUserData = &compatible;
  callbacks.pfnFree = [](void *data, void *p) {
    auto &state = *static_cast<Compatible *>(data);
    ++state.calls;
    AllocationAudit::free(state.audit, p);
  };
  vkDestroyEvent(runtime.device, event, &callbacks);
  REQUIRE(compatible.calls >= 3);
  audit.requireClean();
}

TEST_CASE("Vulkan command pools retain allocator ownership and recover from recording OOM", "[vulkan][allocation]") {
  TestDevice runtime;
  AllocationAudit audit;
  auto callbacks = audit.callbacks();
  VkCommandPoolCreateInfo poolInfo{};
  poolInfo.sType = VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO;
  poolInfo.flags = VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT;
  VkCommandPool pool{};
  REQUIRE(vkCreateCommandPool(runtime.device, &poolInfo, &callbacks, &pool) == VK_SUCCESS);
  VkCommandBufferAllocateInfo allocate{};
  allocate.sType = VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO;
  allocate.commandPool = pool;
  allocate.commandBufferCount = 2;
  std::array<VkCommandBuffer, 2> commands{};
  const auto before = audit.calls;
  REQUIRE(vkAllocateCommandBuffers(runtime.device, &allocate, commands.data()) == VK_SUCCESS);
  REQUIRE(audit.calls > before);
  VkCommandBufferBeginInfo begin{};
  begin.sType = VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO;
  REQUIRE(vkBeginCommandBuffer(commands[0], &begin) == VK_SUCCESS);
  audit.failAt = audit.calls;
  std::array<VkBufferCopy, 4096> regions{};
  REQUIRE_NOTHROW(vkCmdCopyBuffer(commands[0], VK_NULL_HANDLE, VK_NULL_HANDLE, regions.size(), regions.data()));
  REQUIRE(vkEndCommandBuffer(commands[0]) == VK_ERROR_OUT_OF_HOST_MEMORY);
  audit.failAt = SIZE_MAX;
  REQUIRE(vkResetCommandBuffer(commands[0], 0) == VK_SUCCESS);
  REQUIRE(vkBeginCommandBuffer(commands[0], &begin) == VK_SUCCESS);
  REQUIRE(vkEndCommandBuffer(commands[0]) == VK_SUCCESS);
  vkFreeCommandBuffers(runtime.device, pool, commands.size(), commands.data());
  vkDestroyCommandPool(runtime.device, pool, &callbacks);
  audit.requireClean();
}

TEST_CASE("Vulkan command buffer batch allocation rolls back partial outputs", "[vulkan][allocation]") {
  TestDevice runtime;
  bool passed = false;
  size_t failures = 0;
  for (size_t step = 0; step < 32; ++step) {
    AllocationAudit audit;
    auto callbacks = audit.callbacks();
    VkCommandPoolCreateInfo poolInfo{};
    poolInfo.sType = VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO;
    VkCommandPool pool{};
    REQUIRE(vkCreateCommandPool(runtime.device, &poolInfo, &callbacks, &pool) == VK_SUCCESS);
    audit.failAt = audit.calls + step;
    VkCommandBufferAllocateInfo info{};
    info.sType = VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO;
    info.commandPool = pool;
    info.commandBufferCount = 3;
    std::array<VkCommandBuffer, 3> commands{};
    const auto result = vkAllocateCommandBuffers(runtime.device, &info, commands.data());
    if (result == VK_SUCCESS) {
      vkFreeCommandBuffers(runtime.device, pool, commands.size(), commands.data());
      passed = true;
    } else {
      REQUIRE(result == VK_ERROR_OUT_OF_HOST_MEMORY);
      for (auto command : commands)
        REQUIRE(command == VK_NULL_HANDLE);
      ++failures;
    }
    vkDestroyCommandPool(runtime.device, pool, &callbacks);
    audit.requireClean();
    if (passed) break;
  }
  REQUIRE(passed);
  REQUIRE(failures > 3);
}

TEST_CASE("Vulkan queue workers never call application allocators", "[vulkan][allocation]") {
  AllocationAudit audit;
  auto callbacks = audit.callbacks();
  const auto instance = allocatorInstance(&callbacks);
  const auto device = allocatorDevice(instance, nullptr);
  VkQueue queue{};
  vkGetDeviceQueue(device, 0, 0, &queue);
  VkEventCreateInfo eventInfo{};
  eventInfo.sType = VK_STRUCTURE_TYPE_EVENT_CREATE_INFO;
  VkEvent event{};
  REQUIRE(vkCreateEvent(device, &eventInfo, nullptr, &event) == VK_SUCCESS);
  {
    CommandRecording recording(device);
    vkCmdSetEvent(recording.command, event, VK_PIPELINE_STAGE_ALL_COMMANDS_BIT);
    REQUIRE(vkEndCommandBuffer(recording.command) == VK_SUCCESS);
    REQUIRE(submitAndWait(queue, recording.command) == VK_SUCCESS);
  }
  REQUIRE(vkGetEventStatus(device, event) == VK_EVENT_SET);
  vkDestroyEvent(device, event, nullptr);
  vkDestroyDevice(device, nullptr);
  vkDestroyInstance(instance, &callbacks);
  audit.requireClean();
}

TEST_CASE("Vulkan descriptor updates need no allocation after set allocation", "[vulkan][allocation]") {
  AllocationAudit audit;
  auto callbacks = audit.callbacks();
  const auto instance = allocatorInstance(&callbacks);
  const auto device = allocatorDevice(instance, nullptr);
  VkBufferCreateInfo bufferInfo{};
  bufferInfo.sType = VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO;
  bufferInfo.size = 64;
  bufferInfo.usage = VK_BUFFER_USAGE_STORAGE_BUFFER_BIT;
  VkBuffer buffer{};
  REQUIRE(vkCreateBuffer(device, &bufferInfo, nullptr, &buffer) == VK_SUCCESS);
  VkDescriptorSetLayoutBinding binding{0, VK_DESCRIPTOR_TYPE_STORAGE_BUFFER, 4, VK_SHADER_STAGE_COMPUTE_BIT, nullptr};
  VkDescriptorSetLayoutCreateInfo layoutInfo{};
  layoutInfo.sType = VK_STRUCTURE_TYPE_DESCRIPTOR_SET_LAYOUT_CREATE_INFO;
  layoutInfo.bindingCount = 1;
  layoutInfo.pBindings = &binding;
  VkDescriptorSetLayout layout{};
  REQUIRE(vkCreateDescriptorSetLayout(device, &layoutInfo, nullptr, &layout) == VK_SUCCESS);
  VkDescriptorPoolSize size{VK_DESCRIPTOR_TYPE_STORAGE_BUFFER, 8};
  VkDescriptorPoolCreateInfo poolInfo{};
  poolInfo.sType = VK_STRUCTURE_TYPE_DESCRIPTOR_POOL_CREATE_INFO;
  poolInfo.maxSets = 2;
  poolInfo.poolSizeCount = 1;
  poolInfo.pPoolSizes = &size;
  VkDescriptorPool pool{};
  REQUIRE(vkCreateDescriptorPool(device, &poolInfo, nullptr, &pool) == VK_SUCCESS);
  const VkDescriptorSetLayout layouts[]{layout, layout};
  VkDescriptorSetAllocateInfo allocate{};
  allocate.sType = VK_STRUCTURE_TYPE_DESCRIPTOR_SET_ALLOCATE_INFO;
  allocate.descriptorPool = pool;
  allocate.descriptorSetCount = 2;
  allocate.pSetLayouts = layouts;
  VkDescriptorSet sets[2]{};
  REQUIRE(vkAllocateDescriptorSets(device, &allocate, sets) == VK_SUCCESS);
  const VkDescriptorBufferInfo buffers[]{{buffer, 0, 16}, {buffer, 16, 16}, {buffer, 32, 16}, {buffer, 48, 16}};
  VkWriteDescriptorSet write{};
  write.sType = VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET;
  write.dstSet = sets[0];
  write.descriptorCount = 4;
  write.descriptorType = VK_DESCRIPTOR_TYPE_STORAGE_BUFFER;
  write.pBufferInfo = buffers;
  VkCopyDescriptorSet copy{};
  copy.sType = VK_STRUCTURE_TYPE_COPY_DESCRIPTOR_SET;
  copy.srcSet = sets[0];
  copy.dstSet = sets[1];
  copy.descriptorCount = 4;
  const VkDescriptorUpdateTemplateEntry entry{0, 0, 4, VK_DESCRIPTOR_TYPE_STORAGE_BUFFER, 0, sizeof(VkDescriptorBufferInfo)};
  VkDescriptorUpdateTemplateCreateInfo templateInfo{};
  templateInfo.sType = VK_STRUCTURE_TYPE_DESCRIPTOR_UPDATE_TEMPLATE_CREATE_INFO;
  templateInfo.templateType = VK_DESCRIPTOR_UPDATE_TEMPLATE_TYPE_DESCRIPTOR_SET;
  templateInfo.descriptorSetLayout = layout;
  templateInfo.descriptorUpdateEntryCount = 1;
  templateInfo.pDescriptorUpdateEntries = &entry;
  VkDescriptorUpdateTemplate updateTemplate{};
  REQUIRE(vkCreateDescriptorUpdateTemplate(device, &templateInfo, nullptr, &updateTemplate) == VK_SUCCESS);
  const auto before = audit.calls;
  audit.failAt = before;
  REQUIRE_NOTHROW(vkUpdateDescriptorSets(device, 1, &write, 1, &copy));
  REQUIRE_NOTHROW(vkUpdateDescriptorSetWithTemplate(device, sets[0], updateTemplate, buffers));
  REQUIRE(audit.calls == before);
  REQUIRE(vkDeviceWaitIdle(device) == VK_SUCCESS);
  vkDestroyDescriptorUpdateTemplate(device, updateTemplate, nullptr);
  vkDestroyDescriptorPool(device, pool, nullptr);
  vkDestroyDescriptorSetLayout(device, layout, nullptr);
  vkDestroyBuffer(device, buffer, nullptr);
  vkDestroyDevice(device, nullptr);
  vkDestroyInstance(instance, &callbacks);
  audit.requireClean();
}

TEST_CASE("Vulkan recorded queries do not extend the query pool allocator lifetime", "[vulkan][allocation]") {
  TestDevice runtime;
  AllocationAudit audit;
  auto callbacks = audit.callbacks();
  VkQueryPoolCreateInfo queryInfo{};
  queryInfo.sType = VK_STRUCTURE_TYPE_QUERY_POOL_CREATE_INFO;
  queryInfo.queryType = VK_QUERY_TYPE_TIMESTAMP;
  queryInfo.queryCount = 1;
  VkQueryPool query{};
  REQUIRE(vkCreateQueryPool(runtime.device, &queryInfo, &callbacks, &query) == VK_SUCCESS);
  CommandRecording recording(runtime.device);
  const auto command = recording.command;
  vkCmdResetQueryPool(command, query, 0, 1);
  vkCmdWriteTimestamp(command, VK_PIPELINE_STAGE_BOTTOM_OF_PIPE_BIT, query, 0);
  REQUIRE(vkEndCommandBuffer(command) == VK_SUCCESS);
  // This invalidates the recorded command; it is legal because it is not pending.
  vkDestroyQueryPool(runtime.device, query, &callbacks);
  audit.requireClean();
}

TEST_CASE("Vulkan concurrent queues publish disjoint image views of one allocation", "[graphics-execution]") {
  TestDevice runtime;
  DiscardPipeline pipeline(runtime);
  const bool indirect = GENERATE(false, true);
  std::array<VkImage, 2> images{};
  std::array<VkCommandBuffer, 2> commands{reinterpret_cast<VkCommandBuffer>(0x7310), reinterpret_cast<VkCommandBuffer>(0x7320)};
  VkImageCreateInfo info{};
  info.sType = VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO;
  info.imageType = VK_IMAGE_TYPE_2D;
  info.format = VK_FORMAT_R32_UINT;
  info.extent = {8, 8, 1};
  info.mipLevels = info.arrayLayers = 1;
  info.samples = VK_SAMPLE_COUNT_1_BIT;
  info.tiling = VK_IMAGE_TILING_OPTIMAL;
  info.usage = VK_IMAGE_USAGE_TRANSFER_DST_BIT;
  for (auto &image : images)
    REQUIRE(vkCreateImage(runtime.device, &info, nullptr, &image) == VK_SUCCESS);
  VkMemoryRequirements requirements{};
  vkGetImageMemoryRequirements(runtime.device, images[0], &requirements);
  const auto stride = (requirements.size + requirements.alignment - 1) / requirements.alignment * requirements.alignment;
  VkMemoryAllocateInfo allocation{};
  allocation.sType = VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO;
  allocation.allocationSize = stride * 2;
  VkDeviceMemory memory{};
  REQUIRE(vkAllocateMemory(runtime.device, &allocation, nullptr, &memory) == VK_SUCCESS);
  void *mapped{};
  REQUIRE(vkMapMemory(runtime.device, memory, 0, stride * 2, 0, &mapped) == VK_SUCCESS);
  std::memset(mapped, 0, stride * 2);
  VkBufferCreateInfo bufferInfo{};
  bufferInfo.sType = VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO;
  bufferInfo.size = stride * 2;
  bufferInfo.usage = VK_BUFFER_USAGE_INDEX_BUFFER_BIT | VK_BUFFER_USAGE_INDIRECT_BUFFER_BIT | VK_BUFFER_USAGE_TRANSFER_DST_BIT;
  VkBuffer indexBuffer{};
  REQUIRE(vkCreateBuffer(runtime.device, &bufferInfo, nullptr, &indexBuffer) == VK_SUCCESS);
  REQUIRE(vkBindBufferMemory(runtime.device, indexBuffer, memory, 0) == VK_SUCCESS);

  for (size_t i = 0; i < images.size(); ++i) {
    REQUIRE(vkBindImageMemory(runtime.device, images[i], memory, stride * i) == VK_SUCCESS);
    VkCommandBufferBeginInfo begin{};
    begin.sType = VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO;
    REQUIRE(vkBeginCommandBuffer(commands[i], &begin) == VK_SUCCESS);
    // Reused command buffers must restore arguments overwritten by the previous clear.
    const VkDrawIndexedIndirectCommand arguments{3, 1, 0, 0, 0};
    if (indirect) vkCmdUpdateBuffer(commands[i], indexBuffer, stride * i, sizeof(arguments), &arguments);
    // Draw waits release the state mutex; both queues register this same owner.
    pipeline.begin(commands[i]);
    vkCmdBindIndexBuffer(commands[i], indexBuffer, stride * i + sizeof(arguments), VK_INDEX_TYPE_UINT32);
    if (indirect) vkCmdDrawIndexedIndirect(commands[i], indexBuffer, stride * i, 1, sizeof(arguments));
    else vkCmdDrawIndexed(commands[i], 3, 1, 0, 0, 0);
    vkCmdEndRenderPass(commands[i]);

    VkClearColorValue value{};
    value.uint32[0] = 0x12340000u + static_cast<uint32_t>(i);
    VkImageSubresourceRange range{VK_IMAGE_ASPECT_COLOR_BIT, 0, 1, 0, 1};
    vkCmdClearColorImage(commands[i], images[i], VK_IMAGE_LAYOUT_GENERAL, &value, 1, &range);
    REQUIRE(vkEndCommandBuffer(commands[i]) == VK_SUCCESS);
  }
  // Mutate the shared render-pass map while workers look up the active pass.
  // Keep assertions on the test thread, after joining the creator.
  VkResult creationResult = VK_SUCCESS;
  size_t created = 0;
  std::jthread creator([&](std::stop_token stop) {
    VkSubpassDescription subpass{};
    subpass.pipelineBindPoint = VK_PIPELINE_BIND_POINT_GRAPHICS;
    VkRenderPassCreateInfo passInfo{};
    passInfo.sType = VK_STRUCTURE_TYPE_RENDER_PASS_CREATE_INFO;
    passInfo.subpassCount = 1;
    passInfo.pSubpasses = &subpass;
    do {
      VkRenderPass pass{};
      creationResult = vkCreateRenderPass(runtime.device, &passInfo, nullptr, &pass);
      if (creationResult != VK_SUCCESS) break;
      ++created;
      vkDestroyRenderPass(runtime.device, pass, nullptr);
      std::this_thread::yield();
    } while (!stop.stop_requested());
  });
  const std::array<VkQueue, 2> queues{runtime.queue, runtime.other};
  for (unsigned repeat = 0; repeat < 8; ++repeat) {
    for (size_t i = 0; i < queues.size(); ++i) {
      const auto submit = submitInfo(commands[i]);
      REQUIRE(vkQueueSubmit(queues[i], 1, &submit, VK_NULL_HANDLE) == VK_SUCCESS);
    }
    for (const auto queue : queues)
      REQUIRE(vkQueueWaitIdle(queue) == VK_SUCCESS);
    for (size_t i = 0; i < images.size(); ++i)
      for (size_t pixel = 0; pixel < 64; ++pixel) {
        uint32_t value;
        std::memcpy(&value, static_cast<uint8_t *>(mapped) + stride * i + pixel * 4, 4);
        REQUIRE(value == 0x12340000u + i);
      }
  }
  creator.request_stop();
  creator.join();
  REQUIRE(creationResult == VK_SUCCESS);
  REQUIRE(created > 0);
  vkDestroyBuffer(runtime.device, indexBuffer, nullptr);
  vkUnmapMemory(runtime.device, memory);
  for (const auto image : images)
    vkDestroyImage(runtime.device, image, nullptr);
  vkFreeMemory(runtime.device, memory, nullptr);
}
