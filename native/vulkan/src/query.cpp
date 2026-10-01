#include <chrono>

#include "gemstone_vulkan_adapter.hpp"

#include "state.hpp"

namespace gemstone::vulkan::adapter {
// Queries use the same queue ordering and wait notifications as fences.

VkResult impl_vkCreateQueryPool(VkDevice device, const VkQueryPoolCreateInfo *info, const VkAllocationCallbacks *, VkQueryPool *pool) {
  if (!info || !pool || !info->queryCount) return VK_ERROR_INITIALIZATION_FAILED;
  if (info->queryType != VK_QUERY_TYPE_TIMESTAMP && info->queryType != VK_QUERY_TYPE_OCCLUSION) return VK_ERROR_FEATURE_NOT_PRESENT;
  auto state = std::make_shared<QueryPool>();
  state->type = info->queryType;
  state->slots.resize(info->queryCount);
  std::lock_guard lock(stateMutex);
  if (devices.contains(device)) state->device = devices.at(device);
  *pool = makeHandle<VkQueryPool>();
  queryPools.emplace(*pool, std::move(state));
  return VK_SUCCESS;
}
void impl_vkDestroyQueryPool(VkDevice, VkQueryPool pool, const VkAllocationCallbacks *) {
  std::lock_guard lock(stateMutex);
  queryPools.erase(pool);
  deleteHandle(pool);
}
bool resetQueryPool(VkQueryPool pool, uint32_t first, uint32_t count) {
  std::lock_guard lock(stateMutex);
  const auto state = queryPools.find(pool);
  if (state == queryPools.end() || first > state->second->slots.size() || count > state->second->slots.size() - first) return false;
  std::fill_n(state->second->slots.begin() + first, count, QueryPool::Slot{});
  stateChanged.notify_all();
  return true;
}
void impl_vkResetQueryPool(VkDevice, VkQueryPool pool, uint32_t first, uint32_t count) { (void)resetQueryPool(pool, first, count); }
void impl_vkCmdResetQueryPool(VkCommandBuffer command, VkQueryPool pool, uint32_t first, uint32_t count) {
  std::lock_guard lock(stateMutex);
  commands.at(command).operations.emplace_back(recorded::ResetQuery{pool, first, count});
}
void impl_vkCmdBeginQuery(VkCommandBuffer command, VkQueryPool pool, uint32_t query, VkQueryControlFlags) {
  std::lock_guard lock(stateMutex);
  commands.at(command).operations.emplace_back(recorded::BeginQuery{pool, query});
}
void impl_vkCmdEndQuery(VkCommandBuffer command, VkQueryPool pool, uint32_t query) {
  std::lock_guard lock(stateMutex);
  commands.at(command).operations.emplace_back(recorded::EndQuery{pool, query});
}
void impl_vkCmdWriteTimestamp(VkCommandBuffer command, VkPipelineStageFlagBits, VkQueryPool pool, uint32_t query) {
  std::lock_guard lock(stateMutex);
  commands.at(command).operations.emplace_back(recorded::WriteTimestamp{pool, query});
}
VkResult impl_vkGetQueryPoolResults(VkDevice, VkQueryPool pool, uint32_t first, uint32_t count, size_t size, void *data,
                                    VkDeviceSize stride, VkQueryResultFlags flags) {
  std::unique_lock lock(stateMutex);
  const auto found = queryPools.find(pool);
  if (found == queryPools.end() || (count && !data)) return VK_ERROR_INITIALIZATION_FAILED;
  const auto state = found->second;
  if (first > state->slots.size() || count > state->slots.size() - first) return VK_ERROR_INITIALIZATION_FAILED;
  const size_t width = (flags & VK_QUERY_RESULT_64_BIT) ? 8 : 4;
  const size_t record = width * ((flags & VK_QUERY_RESULT_WITH_AVAILABILITY_BIT) ? 2 : 1);
  if (count && (record > size || (count > 1 && (stride < record || stride > (size - record) / (count - 1)))))
    return VK_ERROR_INITIALIZATION_FAILED;
  VkResult result = VK_SUCCESS;
  for (uint32_t i = 0; i < count; ++i) {
    auto &slot = state->slots[first + i];
    if (flags & VK_QUERY_RESULT_WAIT_BIT) stateChanged.wait(lock, [&] { return slot.available || deviceFailed(state->device); });
    if (deviceFailed(state->device)) return VK_ERROR_DEVICE_LOST;
    auto *destination = static_cast<uint8_t *>(data) + i * stride;
    const auto store = [&](uint8_t *target, uint64_t value) {
      if (width == 8) std::memcpy(target, &value, 8);
      else {
        const auto truncated = static_cast<uint32_t>(value);
        std::memcpy(target, &truncated, 4);
      }
    };
    if (slot.available || (flags & VK_QUERY_RESULT_PARTIAL_BIT)) store(destination, slot.value);
    else result = VK_NOT_READY;
    if (flags & VK_QUERY_RESULT_WITH_AVAILABILITY_BIT) store(destination + width, slot.available ? 1 : 0);
  }
  return result;
}

void impl_vkCmdCopyQueryPoolResults(VkCommandBuffer command, VkQueryPool pool, uint32_t first, uint32_t count, VkBuffer destination,
                                    VkDeviceSize offset, VkDeviceSize stride, VkQueryResultFlags flags) {
  std::lock_guard lock(stateMutex);
  commands.at(command).operations.emplace_back(recorded::CopyQueryResults{pool, first, count, destination, offset, stride, flags});
}

} // namespace gemstone::vulkan::adapter
