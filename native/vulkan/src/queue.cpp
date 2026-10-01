#include <algorithm>
#include <chrono>
#include <cstdio>

#include "gemstone_vulkan_adapter.hpp"

#include "state.hpp"

namespace gemstone::vulkan::adapter {
// All scheduling and synchronization state uses
// stateMutex; execution releases it before running commands or calling RPC.

void queueWorker(DeviceState *device, QueueState *queue) {
  Execution execution{device, {}, 0, {}};
  std::unique_lock lock(stateMutex);
  for (;;) {
    stateChanged.wait(lock, [&] { return device->stopping || device->lost || !queue->submissions.empty(); });
    if (device->stopping || device->lost) break;
    auto submission = std::move(queue->submissions.front());
    queue->submissions.pop_front();
    queue->active = true;
    stateChanged.wait(lock, [&] {
      return device->stopping || device->lost || std::ranges::all_of(submission.waits, [](const auto &point) {
               return point.semaphore->type == VK_SEMAPHORE_TYPE_TIMELINE ? point.semaphore->value >= point.value
                                                                          : point.semaphore->value != 0;
             });
    });
    if (device->stopping || device->lost) break;
    // A binary wait consumes the signal exactly once.
    for (const auto &point : submission.waits)
      if (point.semaphore->type == VK_SEMAPHORE_TYPE_BINARY) point.semaphore->value = 0;
    lock.unlock();
    bool okay = true;
    try {
      execution.renderScope = {};
      for (const auto &operation : submission.operations) {
        okay = execute(execution, operation);
        if (!okay) break;
      }
      execution.renderScope = {};
    } catch (const std::exception &error) {
      std::fprintf(stderr, "Gemstone Vulkan queue execution: %s\n", error.what());
      okay = false;
    }
    lock.lock();
    if (!okay) device->lost = true;
    if (okay && !device->lost && !device->stopping) {
      for (const auto &point : submission.signals) {
        if (point.semaphore->type == VK_SEMAPHORE_TYPE_TIMELINE && point.value <= point.semaphore->value) {
          device->lost = true;
          break;
        }
        point.semaphore->value = point.semaphore->type == VK_SEMAPHORE_TYPE_TIMELINE ? point.value : 1;
      }
      if (submission.fence && !device->lost) submission.fence->signaled = true;
    }
    queue->active = false;
    stateChanged.notify_all();
  }
  queue->active = false;
  queue->submissions.clear();
  stateChanged.notify_all();
}
void stopQueues(const std::shared_ptr<DeviceState> &device) {
  {
    std::lock_guard lock(stateMutex);
    device->stopping = true;
    stateChanged.notify_all();
  }
  for (const auto &queue : device->queues)
    if (queue->worker.joinable()) queue->worker.join();
}
// Ensure abandoned devices cannot leave joinable workers at library shutdown.
VkResult createQueues(uint32_t count, VkDevice *handle) {
  auto device = std::make_shared<DeviceState>();
  VkDevice created = VK_NULL_HANDLE;
  try {
    for (uint32_t i = 0; i < count; ++i) {
      auto queue = std::make_shared<QueueState>();
      device->queues.push_back(queue);
      queue->handle = makeHandle<VkQueue>();
      queue->device = device;
      queue->worker = std::thread(queueWorker, device.get(), queue.get());
    }
    created = makeHandle<VkDevice>();
    std::lock_guard lock(stateMutex);
    devices.emplace(created, device);
    for (const auto &queue : device->queues)
      queues.emplace(queue->handle, queue);
    *handle = created;
    return VK_SUCCESS;
  } catch (const std::exception &) {
    stopQueues(device);
    std::lock_guard lock(stateMutex);
    devices.erase(created);
    for (const auto &queue : device->queues) {
      queues.erase(queue->handle);
      deleteHandle(queue->handle);
    }
    deleteHandle(created);
    return VK_ERROR_OUT_OF_HOST_MEMORY;
  }
}
std::chrono::steady_clock::time_point waitDeadline(uint64_t timeout) {
  const auto now = std::chrono::steady_clock::now();
  const auto room = std::chrono::duration_cast<std::chrono::nanoseconds>(std::chrono::steady_clock::time_point::max() - now).count();
  return timeout >= static_cast<uint64_t>(room) ? std::chrono::steady_clock::time_point::max() : now + std::chrono::nanoseconds(timeout);
}
bool deviceFailed(const std::weak_ptr<DeviceState> &reference) {
  const auto device = reference.lock();
  return device && (device->lost || device->stopping);
}
VkResult impl_vkCreateSemaphore(VkDevice device, const VkSemaphoreCreateInfo *info, const VkAllocationCallbacks *, VkSemaphore *semaphore) {
  if (!info || !semaphore) return VK_ERROR_INITIALIZATION_FAILED;
  auto state = std::make_shared<SemaphoreState>();
  for (auto *next = static_cast<const VkBaseInStructure *>(info->pNext); next; next = next->pNext)
    if (next->sType == VK_STRUCTURE_TYPE_SEMAPHORE_TYPE_CREATE_INFO) {
      const auto *type = reinterpret_cast<const VkSemaphoreTypeCreateInfo *>(next);
      if (type->semaphoreType != VK_SEMAPHORE_TYPE_BINARY && type->semaphoreType != VK_SEMAPHORE_TYPE_TIMELINE)
        return VK_ERROR_FEATURE_NOT_PRESENT;
      state->type = type->semaphoreType;
      state->value = state->type == VK_SEMAPHORE_TYPE_TIMELINE ? type->initialValue : 0;
    }
  std::lock_guard lock(stateMutex);
  if (devices.contains(device)) state->device = devices.at(device);
  *semaphore = makeHandle<VkSemaphore>();
  semaphores.emplace(*semaphore, std::move(state));
  return VK_SUCCESS;
}
void impl_vkDestroySemaphore(VkDevice, VkSemaphore semaphore, const VkAllocationCallbacks *) {
  std::lock_guard lock(stateMutex);
  semaphores.erase(semaphore);
  deleteHandle(semaphore);
}
VkResult impl_vkCreateFence(VkDevice device, const VkFenceCreateInfo *info, const VkAllocationCallbacks *, VkFence *fence) {
  if (!info || !fence) return VK_ERROR_INITIALIZATION_FAILED;
  auto state = std::make_shared<FenceState>();
  state->signaled = (info->flags & VK_FENCE_CREATE_SIGNALED_BIT) != 0;
  std::lock_guard lock(stateMutex);
  if (devices.contains(device)) state->device = devices.at(device);
  *fence = makeHandle<VkFence>();
  fences.emplace(*fence, std::move(state));
  return VK_SUCCESS;
}
void impl_vkDestroyFence(VkDevice, VkFence fence, const VkAllocationCallbacks *) {
  std::lock_guard lock(stateMutex);
  fences.erase(fence);
  deleteHandle(fence);
}
VkResult impl_vkGetFenceStatus(VkDevice, VkFence fence) {
  std::lock_guard lock(stateMutex);
  const auto found = fences.find(fence);
  if (found == fences.end() || deviceFailed(found->second->device)) return VK_ERROR_DEVICE_LOST;
  return found->second->signaled ? VK_SUCCESS : VK_NOT_READY;
}
VkResult impl_vkResetFences(VkDevice, uint32_t count, const VkFence *values) {
  std::lock_guard lock(stateMutex);
  for (uint32_t i = 0; i < count; ++i) {
    const auto found = fences.find(values[i]);
    if (found == fences.end()) return VK_ERROR_INITIALIZATION_FAILED;
    found->second->signaled = false;
  }
  return VK_SUCCESS;
}
VkResult impl_vkWaitForFences(VkDevice, uint32_t count, const VkFence *values, VkBool32 all, uint64_t timeout) {
  std::unique_lock lock(stateMutex);
  std::vector<std::shared_ptr<FenceState>> waiting;
  for (uint32_t i = 0; i < count; ++i) {
    const auto found = fences.find(values[i]);
    if (found == fences.end()) return VK_ERROR_INITIALIZATION_FAILED;
    waiting.push_back(found->second);
  }
  const auto failed = [&] { return std::ranges::any_of(waiting, [](const auto &fence) { return deviceFailed(fence->device); }); };
  const auto ready = [&] {
    return failed()
           || (all ? std::ranges::all_of(waiting, [](const auto &fence) { return fence->signaled; })
                   : std::ranges::any_of(waiting, [](const auto &fence) { return fence->signaled; }));
  };
  if (!stateChanged.wait_until(lock, waitDeadline(timeout), ready)) return VK_TIMEOUT;
  return failed() ? VK_ERROR_DEVICE_LOST : VK_SUCCESS;
}
VkResult impl_vkQueueSubmit(VkQueue handle, uint32_t count, const VkSubmitInfo *infos, VkFence fence) {
  std::lock_guard lock(stateMutex);
  const auto found = queues.find(handle);
  if (found == queues.end() || (count && !infos)) return VK_ERROR_INITIALIZATION_FAILED;
  const auto queue = found->second;
  const auto device = queue->device.lock();
  if (!device || device->lost || device->stopping) return VK_ERROR_DEVICE_LOST;
  std::vector<Submission> submissions(std::max(1u, count));
  for (uint32_t i = 0; i < count; ++i) {
    auto &submission = submissions[i];
    const VkTimelineSemaphoreSubmitInfo *timeline = nullptr;
    for (auto *next = static_cast<const VkBaseInStructure *>(infos[i].pNext); next; next = next->pNext)
      if (next->sType == VK_STRUCTURE_TYPE_TIMELINE_SEMAPHORE_SUBMIT_INFO)
        timeline = reinterpret_cast<const VkTimelineSemaphoreSubmitInfo *>(next);
    for (uint32_t j = 0; j < infos[i].waitSemaphoreCount; ++j) {
      const auto semaphore = semaphores.find(infos[i].pWaitSemaphores[j]);
      if (semaphore == semaphores.end()) return VK_ERROR_INITIALIZATION_FAILED;
      uint64_t value = 0;
      if (semaphore->second->type == VK_SEMAPHORE_TYPE_TIMELINE) {
        if (!timeline || timeline->waitSemaphoreValueCount != infos[i].waitSemaphoreCount) return VK_ERROR_INITIALIZATION_FAILED;
        value = timeline->pWaitSemaphoreValues[j];
      }
      submission.waits.push_back({semaphore->second, value});
    }
    for (uint32_t j = 0; j < infos[i].signalSemaphoreCount; ++j) {
      const auto semaphore = semaphores.find(infos[i].pSignalSemaphores[j]);
      if (semaphore == semaphores.end()) return VK_ERROR_INITIALIZATION_FAILED;
      uint64_t value = 0;
      if (semaphore->second->type == VK_SEMAPHORE_TYPE_TIMELINE) {
        if (!timeline || timeline->signalSemaphoreValueCount != infos[i].signalSemaphoreCount) return VK_ERROR_INITIALIZATION_FAILED;
        value = timeline->pSignalSemaphoreValues[j];
      }
      submission.signals.push_back({semaphore->second, value});
    }
    for (uint32_t j = 0; j < infos[i].commandBufferCount; ++j) {
      const auto command = commands.find(infos[i].pCommandBuffers[j]);
      if (command == commands.end() || command->second.recording) return VK_ERROR_INITIALIZATION_FAILED;
      submission.operations.insert(submission.operations.end(), command->second.operations.begin(), command->second.operations.end());
    }
  }
  if (fence) {
    const auto state = fences.find(fence);
    if (state == fences.end()) return VK_ERROR_INITIALIZATION_FAILED;
    state->second->device = device;
    submissions.back().fence = state->second;
  }
  const auto previous = queue->submissions.size();
  try {
    for (auto &submission : submissions)
      queue->submissions.push_back(std::move(submission));
  } catch (...) {
    while (queue->submissions.size() > previous)
      queue->submissions.pop_back();
    throw;
  }
  stateChanged.notify_all();
  return VK_SUCCESS;
}
VkResult impl_vkQueueWaitIdle(VkQueue handle) {
  std::unique_lock lock(stateMutex);
  const auto found = queues.find(handle);
  if (found == queues.end()) return VK_ERROR_INITIALIZATION_FAILED;
  const auto queue = found->second;
  const auto device = queue->device.lock();
  if (!device) return VK_ERROR_DEVICE_LOST;
  stateChanged.wait(lock, [&] { return device->lost || device->stopping || (!queue->active && queue->submissions.empty()); });
  return device->lost || device->stopping ? VK_ERROR_DEVICE_LOST : VK_SUCCESS;
}
VkResult impl_vkDeviceWaitIdle(VkDevice handle) {
  std::unique_lock lock(stateMutex);
  const auto found = devices.find(handle);
  if (found == devices.end()) return VK_ERROR_INITIALIZATION_FAILED;
  const auto device = found->second;
  stateChanged.wait(lock, [&] {
    return device->lost || device->stopping
           || std::ranges::all_of(device->queues, [](const auto &queue) { return !queue->active && queue->submissions.empty(); });
  });
  return device->lost || device->stopping ? VK_ERROR_DEVICE_LOST : VK_SUCCESS;
}

VkResult impl_vkGetSemaphoreCounterValue(VkDevice, VkSemaphore semaphore, uint64_t *value) {
  if (!value) return VK_ERROR_INITIALIZATION_FAILED;
  std::lock_guard lock(stateMutex);
  const auto found = semaphores.find(semaphore);
  if (found == semaphores.end() || found->second->type != VK_SEMAPHORE_TYPE_TIMELINE) return VK_ERROR_INITIALIZATION_FAILED;
  if (deviceFailed(found->second->device)) return VK_ERROR_DEVICE_LOST;
  *value = found->second->value;
  return VK_SUCCESS;
}
VkResult impl_vkSignalSemaphore(VkDevice, const VkSemaphoreSignalInfo *info) {
  if (!info) return VK_ERROR_INITIALIZATION_FAILED;
  std::lock_guard lock(stateMutex);
  const auto found = semaphores.find(info->semaphore);
  if (found == semaphores.end() || found->second->type != VK_SEMAPHORE_TYPE_TIMELINE || info->value <= found->second->value)
    return VK_ERROR_INITIALIZATION_FAILED;
  if (deviceFailed(found->second->device)) return VK_ERROR_DEVICE_LOST;
  found->second->value = info->value;
  stateChanged.notify_all();
  return VK_SUCCESS;
}
VkResult impl_vkWaitSemaphores(VkDevice, const VkSemaphoreWaitInfo *info, uint64_t timeout) {
  if (!info) return VK_ERROR_INITIALIZATION_FAILED;
  std::unique_lock lock(stateMutex);
  std::vector<SemaphorePoint> waiting;
  for (uint32_t i = 0; i < info->semaphoreCount; ++i) {
    const auto found = semaphores.find(info->pSemaphores[i]);
    if (found == semaphores.end() || found->second->type != VK_SEMAPHORE_TYPE_TIMELINE) return VK_ERROR_INITIALIZATION_FAILED;
    waiting.push_back({found->second, info->pValues[i]});
  }
  const auto failed = [&] { return std::ranges::any_of(waiting, [](const auto &point) { return deviceFailed(point.semaphore->device); }); };
  const auto signaled = [](const auto &point) { return point.semaphore->value >= point.value; };
  const auto ready = [&] {
    return failed()
           || ((info->flags & VK_SEMAPHORE_WAIT_ANY_BIT) ? std::ranges::any_of(waiting, signaled) : std::ranges::all_of(waiting, signaled));
  };
  if (!stateChanged.wait_until(lock, waitDeadline(timeout), ready)) return VK_TIMEOUT;
  return failed() ? VK_ERROR_DEVICE_LOST : VK_SUCCESS;
}
} // namespace gemstone::vulkan::adapter
