#include <array>
#include <cstdint>
#include <cstring>
#include <mutex>
#include <utility>
#include <vector>

#include "gemstone_vulkan_adapter.hpp"

#include "state.hpp"

namespace gemstone::vulkan {
namespace {
recorded::Dispatch recordedDispatch(const Command &state, std::array<uint32_t, 3> base, uint32_t x, uint32_t y, uint32_t z) {
  return {.pipeline = state.pipeline,
          .descriptors = state.descriptors,
          .x = x,
          .y = y,
          .z = z,
          .base = base,
          .pushConstants = state.pushConstants,
          .dynamicOffsets = state.dynamicOffsets};
}
} // namespace

VkResult implVkCreateCommandPool(VkDevice, const VkCommandPoolCreateInfo *info, const VkAllocationCallbacks *, VkCommandPool *pool) {
  if (!info || !pool) return VK_ERROR_INITIALIZATION_FAILED;
  *pool = makeHandle<VkCommandPool>();
  std::scoped_lock const lock(stateMutex);
  commandPools.emplace(*pool, CommandPoolState{});
  return VK_SUCCESS;
}

void implVkDestroyCommandPool(VkDevice, VkCommandPool pool, const VkAllocationCallbacks *) {
  {
    std::scoped_lock const lock(stateMutex);
    const auto found = commandPools.find(pool);
    if (found != commandPools.end()) {
      for (auto *const command : found->second.buffers) {
        commands.erase(command);
        deleteHandle(command);
      }
      commandPools.erase(found);
    }
  }
  deleteHandle(pool);
}

VkResult implVkResetCommandPool(VkDevice, VkCommandPool pool, VkCommandPoolResetFlags) {
  std::scoped_lock const lock(stateMutex);
  const auto found = commandPools.find(pool);
  if (found == commandPools.end()) return VK_ERROR_INITIALIZATION_FAILED;
  for (auto *const command : found->second.buffers)
    commands[command] = {};
  return VK_SUCCESS;
}

VkResult implVkAllocateCommandBuffers(VkDevice, const VkCommandBufferAllocateInfo *info, VkCommandBuffer *output) {
  if (!info || (info->commandBufferCount && !output)) return VK_ERROR_INITIALIZATION_FAILED;
  std::scoped_lock const lock(stateMutex);
  const auto pool = commandPools.find(info->commandPool);
  if (pool == commandPools.end()) return VK_ERROR_INITIALIZATION_FAILED;
  for (uint32_t i = 0; i < info->commandBufferCount; ++i) {
    output[i] = makeHandle<VkCommandBuffer>();
    commands[output[i]] = {};
    pool->second.buffers.insert(output[i]);
  }
  return VK_SUCCESS;
}

void implVkFreeCommandBuffers(VkDevice, VkCommandPool pool, uint32_t count, const VkCommandBuffer *buffersToFree) {
  if (!buffersToFree) return;
  std::scoped_lock const lock(stateMutex);
  const auto owner = commandPools.find(pool);
  for (uint32_t i = 0; i < count; ++i) {
    if (owner != commandPools.end()) owner->second.buffers.erase(buffersToFree[i]);
    commands.erase(buffersToFree[i]);
    deleteHandle(buffersToFree[i]);
  }
}

VkResult implVkBeginCommandBuffer(VkCommandBuffer command, const VkCommandBufferBeginInfo *) {
  std::scoped_lock const lock(stateMutex);
  auto &state = commands[command];
  state = {};
  state.recording = true;
  return VK_SUCCESS;
}

VkResult implVkEndCommandBuffer(VkCommandBuffer command) {
  std::scoped_lock const lock(stateMutex);
  const auto it = commands.find(command);
  if (it == commands.end() || !it->second.recording) return VK_ERROR_INITIALIZATION_FAILED;
  it->second.recording = false;
  return it->second.allocationFailed ? VK_ERROR_OUT_OF_HOST_MEMORY : VK_SUCCESS;
}

VkResult implVkResetCommandBuffer(VkCommandBuffer command, VkCommandBufferResetFlags) {
  std::scoped_lock const lock(stateMutex);
  commands[command] = {};
  return VK_SUCCESS;
}

void implVkCmdBindPipeline(VkCommandBuffer command, VkPipelineBindPoint point, VkPipeline pipeline) {
  std::scoped_lock const lock(stateMutex);
  if (point == VK_PIPELINE_BIND_POINT_GRAPHICS) commands[command].graphics.pipeline = pipeline;
  else if (point == VK_PIPELINE_BIND_POINT_COMPUTE) commands[command].pipeline = pipeline;
}

void implVkCmdBindDescriptorSets(VkCommandBuffer command, VkPipelineBindPoint point, VkPipelineLayout, uint32_t first, uint32_t count,
                                 const VkDescriptorSet *sets, uint32_t dynamicCount, const uint32_t *dynamic) {
  std::scoped_lock const lock(stateMutex);
  auto &state = commands.at(command);
  auto &boundSets = point == VK_PIPELINE_BIND_POINT_GRAPHICS ? state.graphics.descriptors : state.descriptors;
  auto &boundOffsets = point == VK_PIPELINE_BIND_POINT_GRAPHICS ? state.graphics.dynamicOffsets : state.dynamicOffsets;
  if ((point != VK_PIPELINE_BIND_POINT_COMPUTE && point != VK_PIPELINE_BIND_POINT_GRAPHICS) || first > boundSets.size()
      || count > boundSets.size() - first || (dynamicCount && !dynamic) || (count && !sets)) {
    state.operations.emplace_back(recorded::Invalid{});
    return;
  }
  uint32_t cursor = 0;
  for (uint32_t i = 0; i < count; ++i) {
    boundSets[first + i] = sets[i];
    std::erase_if(boundOffsets, [&](const auto &item) { return item.first.set == first + i; });
    const auto layout = descriptorSets.sets.find(sets[i]);
    if (layout == descriptorSets.sets.end()) continue;
    for (const auto &[bindingIndex, binding] : layout->second.layout) {
      if (binding.descriptorType != VK_DESCRIPTOR_TYPE_STORAGE_BUFFER_DYNAMIC
          && binding.descriptorType != VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER_DYNAMIC)
        continue;
      for (uint32_t element = 0; element < binding.descriptorCount; ++element) {
        if (cursor >= dynamicCount) {
          state.operations.emplace_back(recorded::Invalid{});
          return;
        }
        boundOffsets[{.set = first + i, .binding = bindingIndex, .element = element}] = dynamic[cursor++];
      }
    }
  }
  if (cursor != dynamicCount) state.operations.emplace_back(recorded::Invalid{});
}

void implVkCmdPushConstants(VkCommandBuffer command, VkPipelineLayout, VkShaderStageFlags stages, uint32_t offset, uint32_t size,
                            const void *data) {
  std::scoped_lock const lock(stateMutex);
  auto &state = commands.at(command);
  if (offset > state.pushConstants.size() || size > state.pushConstants.size() - offset || (size && !data)) {
    state.operations.emplace_back(recorded::Invalid{});
    return;
  }
  if (!size) return;
  if (stages & VK_SHADER_STAGE_COMPUTE_BIT) std::memcpy(state.pushConstants.data() + offset, data, size);
  if (stages & VK_SHADER_STAGE_VERTEX_BIT) std::memcpy(state.graphics.vertexPush.data() + offset, data, size);
  if (stages & VK_SHADER_STAGE_FRAGMENT_BIT) std::memcpy(state.graphics.fragmentPush.data() + offset, data, size);
}

void implVkCmdDispatch(VkCommandBuffer command, uint32_t x, uint32_t y, uint32_t z) {
  std::scoped_lock const lock(stateMutex);
  auto &state = commands[command];
  state.operations.emplace_back(recordedDispatch(state, {}, x, y, z));
}

void implVkCmdDispatchBase(VkCommandBuffer command, uint32_t baseX, uint32_t baseY, uint32_t baseZ, uint32_t x, uint32_t y, uint32_t z) {
  std::scoped_lock const lock(stateMutex);
  auto &state = commands.at(command);
  state.operations.emplace_back(recordedDispatch(state, {baseX, baseY, baseZ}, x, y, z));
}

void implVkCmdCopyBuffer(VkCommandBuffer command, VkBuffer source, VkBuffer destination, uint32_t count, const VkBufferCopy *regions) {
  recorded::Copy copy{.source = source, .destination = destination, .regions = {}};
  if (count && regions) copy.regions.assign(regions, regions + count);
  std::scoped_lock const lock(stateMutex);
  commands[command].operations.emplace_back(std::move(copy));
}

void implVkCmdSetEvent(VkCommandBuffer command, VkEvent event, VkPipelineStageFlags) {
  std::scoped_lock const lock(stateMutex);
  commands[command].operations.emplace_back(recorded::SetEvent{event});
}

void implVkCmdWaitEvents(VkCommandBuffer command, uint32_t count, const VkEvent *values, VkPipelineStageFlags, VkPipelineStageFlags,
                         uint32_t, const VkMemoryBarrier *, uint32_t, const VkBufferMemoryBarrier *, uint32_t,
                         const VkImageMemoryBarrier *) {
  std::vector<VkEvent> wait(values, values + count);
  std::scoped_lock const lock(stateMutex);
  commands[command].operations.emplace_back(recorded::WaitEvents{std::move(wait)});
}

void implVkCmdExecuteCommands(VkCommandBuffer command, uint32_t count, const VkCommandBuffer *secondary) {
  std::scoped_lock const lock(stateMutex);
  auto &destination = commands.at(command).operations;
  for (uint32_t i = 0; i < count; ++i) {
    const auto &source = commands.at(secondary[i]).operations;
    destination.insert(destination.end(), source.begin(), source.end());
  }
}

void implVkTrimCommandPool(VkDevice, VkCommandPool pool, VkCommandPoolTrimFlags) {
  std::scoped_lock const lock(stateMutex);
  const auto found = commandPools.find(pool);
  if (found != commandPools.end())
    for (auto *const buffer : found->second.buffers)
      commands.at(buffer).operations.shrink_to_fit();
}

void implVkCmdUpdateBuffer(VkCommandBuffer command, VkBuffer buffer, VkDeviceSize offset, VkDeviceSize size, const void *data) {
  const auto *begin = static_cast<const uint8_t *>(data);
  std::vector<uint8_t> snapshot(begin, begin + size);
  std::scoped_lock const lock(stateMutex);
  commands.at(command).operations.emplace_back(recorded::UpdateBuffer{.buffer = buffer, .offset = offset, .snapshot = std::move(snapshot)});
}

void implVkCmdDispatchIndirect(VkCommandBuffer command, VkBuffer buffer, VkDeviceSize offset) {
  std::scoped_lock const lock(stateMutex);
  auto &state = commands.at(command);
  state.operations.emplace_back(
      recorded::DispatchIndirect{.dispatch = recordedDispatch(state, {}, 1, 1, 1), .buffer = buffer, .offset = offset});
}

void implVkCmdResetEvent(VkCommandBuffer command, VkEvent event, VkPipelineStageFlags) {
  std::scoped_lock const lock(stateMutex);
  commands[command].operations.emplace_back(recorded::ResetEvent{event});
}

void implVkCmdPipelineBarrier(VkCommandBuffer, VkPipelineStageFlags, VkPipelineStageFlags, VkDependencyFlags, uint32_t,
                              const VkMemoryBarrier *, uint32_t, const VkBufferMemoryBarrier *, uint32_t, const VkImageMemoryBarrier *) {
  // Images have the same linear representation in every layout.
  // Submission executes in order; RPC dispatches import and export coherent
  // host memory before returning. Buffer dependencies are therefore satisfied
  // at every operation boundary, including host-to-device and device-to-host.
}

void implVkCmdFillBuffer(VkCommandBuffer command, VkBuffer buffer, VkDeviceSize offset, VkDeviceSize size, uint32_t value) {
  std::scoped_lock const lock(stateMutex);
  commands[command].operations.emplace_back(recorded::FillBuffer{.buffer = buffer, .offset = offset, .size = size, .value = value});
}
} // namespace gemstone::vulkan
