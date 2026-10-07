#include <cstdlib>

#include <vulkan/vulkan.h>

namespace {

VkInstance instance = VK_NULL_HANDLE;
VkDevice device = VK_NULL_HANDLE;
VkQueue queue = VK_NULL_HANDLE;

// Layered runtimes register teardown after loading the driver but before its first submission, and release their
// objects from it.
void teardown() {
  VkFenceCreateInfo fenceInfo{};
  fenceInfo.sType = VK_STRUCTURE_TYPE_FENCE_CREATE_INFO;
  VkFence fence = VK_NULL_HANDLE;
  const bool released = vkCreateFence(device, &fenceInfo, nullptr, &fence) == VK_SUCCESS
                        && vkQueueSubmit(queue, 0, nullptr, fence) == VK_SUCCESS
                        && vkWaitForFences(device, 1, &fence, VK_TRUE, 1'000'000'000ULL) == VK_SUCCESS;
  if (!released) std::_Exit(EXIT_FAILURE);
  vkDestroyFence(device, fence, nullptr);
  vkDestroyDevice(device, nullptr);
  vkDestroyInstance(instance, nullptr);
}

} // namespace

int main() {
  if (std::atexit(teardown) != 0) return EXIT_FAILURE;
  VkInstanceCreateInfo instanceInfo{};
  instanceInfo.sType = VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO;
  if (vkCreateInstance(&instanceInfo, nullptr, &instance) != VK_SUCCESS) return EXIT_FAILURE;
  uint32_t count = 1;
  VkPhysicalDevice physical = VK_NULL_HANDLE;
  if (vkEnumeratePhysicalDevices(instance, &count, &physical) != VK_SUCCESS) return EXIT_FAILURE;
  const float priority = 1;
  VkDeviceQueueCreateInfo queueInfo{};
  queueInfo.sType = VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO;
  queueInfo.queueCount = 1;
  queueInfo.pQueuePriorities = &priority;
  VkDeviceCreateInfo deviceInfo{};
  deviceInfo.sType = VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO;
  deviceInfo.queueCreateInfoCount = 1;
  deviceInfo.pQueueCreateInfos = &queueInfo;
  if (vkCreateDevice(physical, &deviceInfo, nullptr, &device) != VK_SUCCESS) return EXIT_FAILURE;
  vkGetDeviceQueue(device, 0, 0, &queue);
  return queue ? EXIT_SUCCESS : EXIT_FAILURE;
}
