#include <algorithm>
#include <cstdio>

#include "gemstone_vulkan_adapter.hpp"

#include "state.hpp"

namespace gemstone::vulkan {
// All scheduling and synchronisation state uses stateMutex; jobs release it before running commands or calling RPC.
namespace {
void run(QueueState &queue, DeviceState &device, std::vector<Submission> &submissions) {
  auto &execution = queue.execution;
  for (auto &submission : submissions) {
    {
      std::unique_lock lock(stateMutex);
      // Every blocking wait may start a covering worker, so only waits that may block enter one.
      if (!submission.waits.empty())
        commandQueue().blocking([&] {
          stateChanged.wait(lock, [&] {
            return device.failed() || std::ranges::all_of(submission.waits, [](const auto &point) {
                     return point.semaphore->type == VK_SEMAPHORE_TYPE_TIMELINE ? point.semaphore->value >= point.value
                                                                                : point.semaphore->value != 0;
                   });
          });
        });
      if (device.failed()) return;
      // A binary wait consumes the signal exactly once.
      for (const auto &point : submission.waits)
        if (point.semaphore->type == VK_SEMAPHORE_TYPE_BINARY) point.semaphore->value = 0;
    }
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
    {
      std::lock_guard lock(stateMutex);
      if (!okay) device.lost = true;
      if (!device.failed()) {
        for (const auto &point : submission.signals) {
          if (point.semaphore->type == VK_SEMAPHORE_TYPE_TIMELINE && point.value <= point.semaphore->value) {
            device.lost = true;
            break;
          }
          point.semaphore->value = point.semaphore->type == VK_SEMAPHORE_TYPE_TIMELINE ? point.value : 1;
        }
        if (submission.fence && !device.lost) submission.fence->signalled = true;
      }
    }
    stateChanged.notify_all();
  }
}

// Callers must not hold stateMutex.
void waitIdle(const std::vector<std::shared_ptr<QueueState>> &targets) {
  std::vector<CommandQueue::Ticket> pending;
  {
    host::Scope scope(nullptr);
    for (const auto &queue : targets)
      pending.push_back(commandQueue().snapshot(queue->handle));
  }
  for (const auto &ticket : pending)
    commandQueue().wait(ticket);
}
} // namespace

// Teardown of layered runtimes may call the driver after the workers stop, so the queue is never destroyed.
CommandQueue &commandQueue() {
  host::Scope scope(nullptr);
  static auto *const queue = new CommandQueue(VK_ERROR_DEVICE_LOST);
  return *queue;
}
void shutdownQueues() {
  // Abandoned devices must not leave jobs waiting for signals that never arrive.
  {
    std::lock_guard lock(stateMutex);
    for (const auto &[_, device] : devices)
      device->stopping = true;
  }
  stateChanged.notify_all();
  host::Scope scope(nullptr);
  commandQueue().shutdown();
}
void stopQueues(const std::shared_ptr<DeviceState> &device) {
  {
    std::lock_guard lock(stateMutex);
    device->stopping = true;
  }
  stateChanged.notify_all();
  waitIdle(device->queues);
  host::Scope scope(nullptr);
  for (const auto &queue : device->queues)
    commandQueue().forget(queue->handle);
}
VkResult createQueues(uint32_t count, VkDevice *handle) {
  auto device = std::make_shared<DeviceState>();
  VkDevice created = VK_NULL_HANDLE;
  try {
    for (uint32_t i = 0; i < count; ++i) {
      auto queue = std::make_shared<QueueState>();
      device->queues.push_back(queue);
      queue->handle = makeHandle<VkQueue>();
      queue->device = device;
      queue->execution.device = device.get();
      host::Scope scope(nullptr);
      commandQueue().configure(queue->handle, true);
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
bool deviceFailed(const std::weak_ptr<DeviceState> &reference) {
  const auto device = reference.lock();
  return device && device->failed();
}
VkResult implVkCreateSemaphore(VkDevice device, const VkSemaphoreCreateInfo *info, const VkAllocationCallbacks *, VkSemaphore *semaphore) {
  if (!info || !semaphore) return VK_ERROR_INITIALIZATION_FAILED;
  auto state = std::make_shared<SemaphoreState>();
  if (const auto *type = findChained<VkSemaphoreTypeCreateInfo>(info->pNext, VK_STRUCTURE_TYPE_SEMAPHORE_TYPE_CREATE_INFO)) {
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
void implVkDestroySemaphore(VkDevice, VkSemaphore semaphore, const VkAllocationCallbacks *) {
  std::lock_guard lock(stateMutex);
  semaphores.erase(semaphore);
  deleteHandle(semaphore);
}
VkResult implVkCreateFence(VkDevice device, const VkFenceCreateInfo *info, const VkAllocationCallbacks *, VkFence *fence) {
  if (!info || !fence) return VK_ERROR_INITIALIZATION_FAILED;
  auto state = std::make_shared<FenceState>();
  state->signalled = (info->flags & VK_FENCE_CREATE_SIGNALED_BIT) != 0;
  std::lock_guard lock(stateMutex);
  if (devices.contains(device)) state->device = devices.at(device);
  *fence = makeHandle<VkFence>();
  fences.emplace(*fence, std::move(state));
  return VK_SUCCESS;
}
void implVkDestroyFence(VkDevice, VkFence fence, const VkAllocationCallbacks *) {
  std::lock_guard lock(stateMutex);
  fences.erase(fence);
  deleteHandle(fence);
}
VkResult implVkGetFenceStatus(VkDevice, VkFence fence) {
  std::lock_guard lock(stateMutex);
  const auto found = fences.find(fence);
  if (found == fences.end() || deviceFailed(found->second->device)) return VK_ERROR_DEVICE_LOST;
  return found->second->signalled ? VK_SUCCESS : VK_NOT_READY;
}
VkResult implVkResetFences(VkDevice, uint32_t count, const VkFence *values) {
  std::lock_guard lock(stateMutex);
  for (uint32_t i = 0; i < count; ++i) {
    const auto found = fences.find(values[i]);
    if (found == fences.end()) return VK_ERROR_INITIALIZATION_FAILED;
    found->second->signalled = false;
  }
  return VK_SUCCESS;
}
VkResult implVkWaitForFences(VkDevice, uint32_t count, const VkFence *values, VkBool32 all, uint64_t timeout) {
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
           || (all ? std::ranges::all_of(waiting, [](const auto &fence) { return fence->signalled; })
                   : std::ranges::any_of(waiting, [](const auto &fence) { return fence->signalled; }));
  };
  if (!stateChanged.wait_until(lock, deadlineAfter(timeout), ready)) return VK_TIMEOUT;
  return failed() ? VK_ERROR_DEVICE_LOST : VK_SUCCESS;
}
VkResult implVkQueueSubmit(VkQueue handle, uint32_t count, const VkSubmitInfo *infos, VkFence fence) {
  std::lock_guard lock(stateMutex);
  const auto found = queues.find(handle);
  if (found == queues.end() || (count && !infos)) return VK_ERROR_INITIALIZATION_FAILED;
  const auto queue = found->second;
  const auto device = queue->device.lock();
  if (!device || device->failed()) return VK_ERROR_DEVICE_LOST;
  std::vector<Submission> submissions(std::max(1u, count));
  for (uint32_t i = 0; i < count; ++i) {
    auto &submission = submissions[i];
    const auto *timeline = findChained<VkTimelineSemaphoreSubmitInfo>(infos[i].pNext, VK_STRUCTURE_TYPE_TIMELINE_SEMAPHORE_SUBMIT_INFO);
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
  host::Scope scope(nullptr);
  commandQueue().submit(handle, [queue, device, submissions = std::move(submissions)]() mutable {
    try {
      run(*queue, *device, submissions);
    } catch (...) {
      // Fence and semaphore waits only observe the device, not the ticket status.
      {
        std::lock_guard lock(stateMutex);
        device->lost = true;
      }
      stateChanged.notify_all();
      return VK_ERROR_DEVICE_LOST;
    }
    return VK_SUCCESS;
  });
  return VK_SUCCESS;
}
VkResult implVkQueueWaitIdle(VkQueue handle) {
  std::shared_ptr<QueueState> queue;
  std::shared_ptr<DeviceState> device;
  {
    std::lock_guard lock(stateMutex);
    const auto found = queues.find(handle);
    if (found == queues.end()) return VK_ERROR_INITIALIZATION_FAILED;
    queue = found->second;
    device = queue->device.lock();
    if (!device) return VK_ERROR_DEVICE_LOST;
  }
  waitIdle({queue});
  std::lock_guard lock(stateMutex);
  return device->failed() ? VK_ERROR_DEVICE_LOST : VK_SUCCESS;
}
VkResult implVkDeviceWaitIdle(VkDevice handle) {
  std::shared_ptr<DeviceState> device;
  {
    std::lock_guard lock(stateMutex);
    const auto found = devices.find(handle);
    if (found == devices.end()) return VK_ERROR_INITIALIZATION_FAILED;
    device = found->second;
  }
  waitIdle(device->queues);
  std::lock_guard lock(stateMutex);
  return device->failed() ? VK_ERROR_DEVICE_LOST : VK_SUCCESS;
}

VkResult implVkGetSemaphoreCounterValue(VkDevice, VkSemaphore semaphore, uint64_t *value) {
  if (!value) return VK_ERROR_INITIALIZATION_FAILED;
  std::lock_guard lock(stateMutex);
  const auto found = semaphores.find(semaphore);
  if (found == semaphores.end() || found->second->type != VK_SEMAPHORE_TYPE_TIMELINE) return VK_ERROR_INITIALIZATION_FAILED;
  if (deviceFailed(found->second->device)) return VK_ERROR_DEVICE_LOST;
  *value = found->second->value;
  return VK_SUCCESS;
}
VkResult implVkSignalSemaphore(VkDevice, const VkSemaphoreSignalInfo *info) {
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
VkResult implVkWaitSemaphores(VkDevice, const VkSemaphoreWaitInfo *info, uint64_t timeout) {
  if (!info) return VK_ERROR_INITIALIZATION_FAILED;
  std::unique_lock lock(stateMutex);
  std::vector<SemaphorePoint> waiting;
  for (uint32_t i = 0; i < info->semaphoreCount; ++i) {
    const auto found = semaphores.find(info->pSemaphores[i]);
    if (found == semaphores.end() || found->second->type != VK_SEMAPHORE_TYPE_TIMELINE) return VK_ERROR_INITIALIZATION_FAILED;
    waiting.push_back({found->second, info->pValues[i]});
  }
  const auto failed = [&] { return std::ranges::any_of(waiting, [](const auto &point) { return deviceFailed(point.semaphore->device); }); };
  const auto signalled = [](const auto &point) { return point.semaphore->value >= point.value; };
  const auto ready = [&] {
    return failed()
           || ((info->flags & VK_SEMAPHORE_WAIT_ANY_BIT) ? std::ranges::any_of(waiting, signalled)
                                                         : std::ranges::all_of(waiting, signalled));
  };
  if (!stateChanged.wait_until(lock, deadlineAfter(timeout), ready)) return VK_TIMEOUT;
  return failed() ? VK_ERROR_DEVICE_LOST : VK_SUCCESS;
}
} // namespace gemstone::vulkan
