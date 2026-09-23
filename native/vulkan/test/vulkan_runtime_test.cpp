#include <cstring>
#include <string>
#include <vector>

#include <link.h>

#include <catch2/catch_test_macros.hpp>

#include "../../rpc/test/rpc_fixture.hpp"
#include "../src/runtime.hpp"
#include <vulkan/vulkan.h>

namespace {
std::vector<uint8_t> spirvBytes(const std::vector<uint32_t> &words) {
  std::vector<uint8_t> bytes(words.size() * sizeof(uint32_t));
  std::memcpy(bytes.data(), words.data(), bytes.size());
  return bytes;
}

int collectVulkanLibraries(dl_phdr_info *info, size_t, void *data) {
  if (info->dlpi_name && std::strstr(info->dlpi_name, "libvulkan.so"))
    static_cast<std::vector<std::string> *>(data)->emplace_back(info->dlpi_name);
  return 0;
}
} // namespace

TEST_CASE("Vulkan API facade keeps explicit unsupported and lookup paths") {
  REQUIRE(vkCreateSampler(VK_NULL_HANDLE, nullptr, nullptr, nullptr) == VK_ERROR_FEATURE_NOT_PRESENT);
  REQUIRE(vkGetInstanceProcAddr(nullptr, "vkGetInstanceProcAddr") != nullptr);
  REQUIRE(vkGetInstanceProcAddr(nullptr, "vkCreateInstance") != nullptr);
  REQUIRE(vkGetInstanceProcAddr(nullptr, "vkGemstoneUnknown") == nullptr);
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

TEST_CASE("Vulkan instance lookup preserves unavailable functions") {
  REQUIRE(vkGetInstanceProcAddr(VK_NULL_HANDLE, "vkCreateBuffer") == nullptr);
  REQUIRE(vkGetInstanceProcAddr(VK_NULL_HANDLE, "vkGetDeviceProcAddr") == nullptr);
}

TEST_CASE("Vulkan llvmpipe profile remains selectable without an ICD") {
  REQUIRE(setenv("GEMSTONE_VULKAN_PROFILE", "llvmpipe", 1) == 0);
  VkPhysicalDeviceProperties properties{};
  vkGetPhysicalDeviceProperties(VK_NULL_HANDLE, &properties);
  REQUIRE(std::string(properties.deviceName).find("llvmpipe") != std::string::npos);
  REQUIRE(unsetenv("GEMSTONE_VULKAN_PROFILE") == 0);
}

TEST_CASE("Vulkan exposes the allocator buffer plumbing") {
  const auto device = reinterpret_cast<VkDevice>(1);
  for (const char *name : {
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
           "vkBindImageMemory2",
       })
    REQUIRE(vkGetDeviceProcAddr(device, name) != nullptr);
}

TEST_CASE("Vulkan owns buffer memory and executes copies") {
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
  REQUIRE(vkBindBufferMemory(VK_NULL_HANDLE, source, memory, 0) == VK_SUCCESS);
  REQUIRE(vkBindBufferMemory(VK_NULL_HANDLE, destination, memory, 16) == VK_SUCCESS);

  VkCommandBufferAllocateInfo allocateCommand{};
  allocateCommand.sType = VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO;
  VkCommandPoolCreateInfo commandPoolInfo{};
  commandPoolInfo.sType = VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO;
  VkCommandPool commandPool = VK_NULL_HANDLE;
  REQUIRE(vkCreateCommandPool(VK_NULL_HANDLE, &commandPoolInfo, nullptr, &commandPool) == VK_SUCCESS);
  allocateCommand.commandPool = commandPool;
  allocateCommand.commandBufferCount = 1;
  VkCommandBuffer command = VK_NULL_HANDLE;
  REQUIRE(vkAllocateCommandBuffers(VK_NULL_HANDLE, &allocateCommand, &command) == VK_SUCCESS);
  VkCommandBufferBeginInfo begin{};
  begin.sType = VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO;
  REQUIRE(vkBeginCommandBuffer(command, &begin) == VK_SUCCESS);
  const VkBufferCopy region{0, 0, 9};
  vkCmdCopyBuffer(command, source, destination, 1, &region);
  REQUIRE(vkEndCommandBuffer(command) == VK_SUCCESS);
  VkFenceCreateInfo fenceInfo{};
  fenceInfo.sType = VK_STRUCTURE_TYPE_FENCE_CREATE_INFO;
  VkFence fence = VK_NULL_HANDLE;
  REQUIRE(vkCreateFence(VK_NULL_HANDLE, &fenceInfo, nullptr, &fence) == VK_SUCCESS);
  VkSubmitInfo info{};
  info.sType = VK_STRUCTURE_TYPE_SUBMIT_INFO;
  info.commandBufferCount = 1;
  info.pCommandBuffers = &command;
  REQUIRE(vkQueueSubmit(VK_NULL_HANDLE, 1, &info, fence) == VK_SUCCESS);
  REQUIRE(vkWaitForFences(VK_NULL_HANDLE, 1, &fence, VK_TRUE, 0) == VK_SUCCESS);
  REQUIRE(std::memcmp(static_cast<char *>(mapped) + 16, "gemstone", 9) == 0);
  vkDestroyFence(VK_NULL_HANDLE, fence, nullptr);
  vkFreeCommandBuffers(VK_NULL_HANDLE, commandPool, 1, &command);
  vkDestroyCommandPool(VK_NULL_HANDLE, commandPool, nullptr);
  vkDestroyBuffer(VK_NULL_HANDLE, destination, nullptr);
  vkDestroyBuffer(VK_NULL_HANDLE, source, nullptr);
  vkFreeMemory(VK_NULL_HANDLE, memory, nullptr);
}

TEST_CASE("Vulkan rejects signal semaphores it cannot delay until emulation completes") {
  const auto command = reinterpret_cast<VkCommandBuffer>(0x7890);
  VkCommandBufferBeginInfo begin{};
  begin.sType = VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO;
  REQUIRE(vkBeginCommandBuffer(command, &begin) == VK_SUCCESS);
  vkCmdDispatch(command, 1, 1, 1);
  const auto semaphore = reinterpret_cast<VkSemaphore>(0x1000);
  VkSubmitInfo info{};
  info.sType = VK_STRUCTURE_TYPE_SUBMIT_INFO;
  info.commandBufferCount = 1;
  info.pCommandBuffers = &command;
  info.signalSemaphoreCount = 1;
  info.pSignalSemaphores = &semaphore;
  REQUIRE(vkQueueSubmit(VK_NULL_HANDLE, 1, &info, VK_NULL_HANDLE) == VK_ERROR_FEATURE_NOT_PRESENT);
}

TEST_CASE("Vulkan rejects asynchronous semaphore waits for emulated dispatches") {
  const auto command = reinterpret_cast<VkCommandBuffer>(0x8910);
  VkCommandBufferBeginInfo begin{};
  begin.sType = VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO;
  REQUIRE(vkBeginCommandBuffer(command, &begin) == VK_SUCCESS);
  vkCmdDispatch(command, 1, 1, 1);
  const auto semaphore = reinterpret_cast<VkSemaphore>(0x1000);
  const VkPipelineStageFlags stage = VK_PIPELINE_STAGE_COMPUTE_SHADER_BIT;
  VkSubmitInfo info{};
  info.sType = VK_STRUCTURE_TYPE_SUBMIT_INFO;
  info.commandBufferCount = 1;
  info.pCommandBuffers = &command;
  info.waitSemaphoreCount = 1;
  info.pWaitSemaphores = &semaphore;
  info.pWaitDstStageMask = &stage;
  REQUIRE(vkQueueSubmit(VK_NULL_HANDLE, 1, &info, VK_NULL_HANDLE) == VK_ERROR_FEATURE_NOT_PRESENT);
}

TEST_CASE("Vulkan compute workgroup size comes from the selected SPIR-V entry point") {
  const auto fixed = spirvBytes({
      0x07230203,
      0x00010000,
      0,
      64,
      0,
      (5u << 16) | 15u,
      5,
      7,
      0x6e69616d,
      0,
      (6u << 16) | 16u,
      7,
      17,
      8,
      2,
      1,
  });
  REQUIRE(gemstoneVulkanLocalSize(fixed, "main", {}) == std::array<uint32_t, 3>{8, 2, 1});

  const auto specialized = spirvBytes({
      0x07230203,
      0x00010000,
      0,
      64,
      0,
      (5u << 16) | 15u,
      5,
      7,
      0x6e69616d,
      0,
      (4u << 16) | 71u,
      20,
      1,
      9,
      (4u << 16) | 71u,
      21,
      1,
      10,
      (4u << 16) | 71u,
      22,
      1,
      11,
      (4u << 16) | 50u,
      3,
      20,
      1,
      (4u << 16) | 50u,
      3,
      21,
      1,
      (4u << 16) | 50u,
      3,
      22,
      1,
      (6u << 16) | 331u,
      7,
      38,
      20,
      21,
      22,
  });
  REQUIRE(gemstoneVulkanLocalSize(specialized, "main", {{9, 4}, {10, 3}, {11, 2}}) == std::array<uint32_t, 3>{4, 3, 2});

  const auto workgroupBuiltin = spirvBytes({
      0x07230203,
      0x00010000,
      0,
      64,
      0,
      (5u << 16) | 15u,
      5,
      7,
      0x6e69616d,
      0,
      (6u << 16) | 16u,
      7,
      17,
      1,
      1,
      1,
      (4u << 16) | 71u,
      20,
      1,
      0,
      (4u << 16) | 71u,
      21,
      1,
      1,
      (4u << 16) | 71u,
      22,
      1,
      2,
      (4u << 16) | 71u,
      30,
      11,
      25,
      (4u << 16) | 50u,
      3,
      20,
      1,
      (4u << 16) | 50u,
      3,
      21,
      1,
      (4u << 16) | 50u,
      3,
      22,
      1,
      (6u << 16) | 51u,
      4,
      30,
      20,
      21,
      22,
  });
  REQUIRE(gemstoneVulkanLocalSize(workgroupBuiltin, "main", {{0, 4}, {1, 3}, {2, 2}}) == std::array<uint32_t, 3>{4, 3, 2});
}

TEST_CASE("Vulkan descriptor mirror applies array writes and copies") {
  GemstoneVulkanDescriptorState state;
  const auto source = reinterpret_cast<VkDescriptorSet>(0x1000);
  const auto destination = reinterpret_cast<VkDescriptorSet>(0x2000);
  const VkDescriptorBufferInfo buffers[] = {
      {reinterpret_cast<VkBuffer>(0x3000), 0, 16},
      {reinterpret_cast<VkBuffer>(0x4000), 0, 16},
  };
  VkWriteDescriptorSet write{};
  write.sType = VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET;
  write.dstSet = source;
  write.dstBinding = 3;
  write.dstArrayElement = 1;
  write.descriptorCount = 2;
  write.descriptorType = VK_DESCRIPTOR_TYPE_STORAGE_BUFFER;
  write.pBufferInfo = buffers;
  state.update(1, &write, 0, nullptr);
  REQUIRE(state.get(source, 3, 1)->buffer == buffers[0].buffer);
  REQUIRE(state.get(source, 3, 2)->buffer == buffers[1].buffer);
  REQUIRE(state.get(source, 4, 0) == nullptr);
  REQUIRE(state.hasArrayElements(source, 3));
  REQUIRE_FALSE(state.hasArrayElements(source, 4));

  VkCopyDescriptorSet copy{};
  copy.sType = VK_STRUCTURE_TYPE_COPY_DESCRIPTOR_SET;
  copy.srcSet = source;
  copy.srcBinding = 3;
  copy.srcArrayElement = 1;
  copy.dstSet = destination;
  copy.dstBinding = 5;
  copy.dstArrayElement = 0;
  copy.descriptorCount = 2;
  state.update(0, nullptr, 1, &copy);
  REQUIRE(state.get(destination, 5, 0)->buffer == buffers[0].buffer);
  REQUIRE(state.get(destination, 5, 1)->buffer == buffers[1].buffer);
}

TEST_CASE("Vulkan descriptor views enforce their declared range") {
  REQUIRE(gemstoneVulkanDescriptorLength(64, 16, 12) == std::optional<size_t>(12));
  REQUIRE(gemstoneVulkanDescriptorLength(64, 16, VK_WHOLE_SIZE) == std::optional<size_t>(48));
  REQUIRE_FALSE(gemstoneVulkanDescriptorLength(64, 65, VK_WHOLE_SIZE));
  REQUIRE_FALSE(gemstoneVulkanDescriptorLength(64, 60, 8));
}

TEST_CASE("Vulkan descriptor allocation planning preserves overlapping aliases") {
  const auto first = reinterpret_cast<VkDeviceMemory>(0x1000);
  const auto second = reinterpret_cast<VkDeviceMemory>(0x2000);
  const auto plan = gemstoneVulkanDescriptorAllocationPlan({
      {first, 16, 16},
      {first, 24, 16},
      {first, 64, 8},
      {second, 0, 4},
  });
  REQUIRE(plan.allocations.size() == 3);
  REQUIRE(plan.allocations[0].memory == first);
  REQUIRE(plan.allocations[0].offset == 16);
  REQUIRE(plan.allocations[0].size == 24);
  REQUIRE(plan.arguments[0].allocation == plan.arguments[1].allocation);
  REQUIRE(plan.arguments[0].offset == 0);
  REQUIRE(plan.arguments[1].offset == 8);
  REQUIRE(plan.arguments[2].allocation != plan.arguments[0].allocation);
  REQUIRE(plan.arguments[3].allocation != plan.arguments[0].allocation);
}

TEST_CASE("Vulkan dispatch uses persistent memory and preserves descriptor aliases") {
  gemstone_test::RpcFixture fixture;
  const auto port = std::to_string(fixture.port());
  REQUIRE(setenv("GEMSTONE_RPC_PORT", port.c_str(), 1) == 0);
  REQUIRE(setenv("GEMSTONE_RPC_CLIENT", "42", 1) == 0);
  fixture.expect({1, 1, 1, 0, 0});
  fixture.expectAllocationArguments({{0, 0}, {0, 4}});
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
  const VkDescriptorBufferInfo descriptorBuffers[] = {{buffers[0], 0, 16}, {buffers[1], 4, 16}};
  VkWriteDescriptorSet writes[2]{};
  for (uint32_t index = 0; index < 2; ++index) {
    writes[index].sType = VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET;
    writes[index].dstSet = reinterpret_cast<VkDescriptorSet>(0x3000);
    writes[index].dstBinding = index;
    writes[index].descriptorCount = 1;
    writes[index].descriptorType = VK_DESCRIPTOR_TYPE_STORAGE_BUFFER;
    writes[index].pBufferInfo = &descriptorBuffers[index];
  }
  vkUpdateDescriptorSets(VK_NULL_HANDLE, 2, writes, 0, nullptr);

  const auto command = reinterpret_cast<VkCommandBuffer>(0x4000);
  VkCommandBufferBeginInfo begin{};
  begin.sType = VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO;
  REQUIRE(vkBeginCommandBuffer(command, &begin) == VK_SUCCESS);
  vkCmdBindPipeline(command, VK_PIPELINE_BIND_POINT_COMPUTE, pipeline);
  const auto descriptors = writes[0].dstSet;
  vkCmdBindDescriptorSets(command, VK_PIPELINE_BIND_POINT_COMPUTE, VK_NULL_HANDLE, 0, 1, &descriptors, 0, nullptr);
  vkCmdDispatch(command, 1, 1, 1);
  REQUIRE(vkEndCommandBuffer(command) == VK_SUCCESS);
  VkSubmitInfo submit{};
  submit.sType = VK_STRUCTURE_TYPE_SUBMIT_INFO;
  submit.commandBufferCount = 1;
  submit.pCommandBuffers = &command;
  REQUIRE(vkQueueSubmit(VK_NULL_HANDLE, 1, &submit, VK_NULL_HANDLE) == VK_SUCCESS);
  fixture.check();

  REQUIRE(vkMapMemory(VK_NULL_HANDLE, memory, 0, VK_WHOLE_SIZE, 0, &mapped) == VK_SUCCESS);
  REQUIRE(static_cast<uint32_t *>(mapped)[1] == deviceWrite);
  vkUnmapMemory(VK_NULL_HANDLE, memory);
  vkDestroyBuffer(VK_NULL_HANDLE, buffers[1], nullptr);
  vkDestroyBuffer(VK_NULL_HANDLE, buffers[0], nullptr);
  vkFreeMemory(VK_NULL_HANDLE, memory, nullptr);
  vkDestroyPipeline(VK_NULL_HANDLE, pipeline, nullptr);
  vkDestroyShaderModule(VK_NULL_HANDLE, module, nullptr);
  REQUIRE(unsetenv("GEMSTONE_RPC_CLIENT") == 0);
  REQUIRE(unsetenv("GEMSTONE_RPC_PORT") == 0);
}

TEST_CASE("Vulkan descriptor reflection is scoped to the selected entry point") {
  const auto module = spirvBytes({
      0x07230203, 0x00010400,
      0,          64,
      0,          (6u << 16) | 15u,
      5,          7,
      0x73726966, 0x00000074,
      20,         (6u << 16) | 15u,
      5,          8,
      0x6f636573, 0x0000646e,
      21,         (4u << 16) | 71u,
      20,         33,
      3,          (4u << 16) | 71u,
      20,         34,
      0,          (4u << 16) | 71u,
      21,         33,
      5,          (4u << 16) | 71u,
      21,         34,
      0,
  });
  REQUIRE(gemstoneVulkanDescriptorBindings(module, "first") == std::optional(std::vector<uint32_t>{3}));
  REQUIRE(gemstoneVulkanDescriptorBindings(module, "second") == std::optional(std::vector<uint32_t>{5}));
  REQUIRE_FALSE(gemstoneVulkanDescriptorBindings(module, "missing"));
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
