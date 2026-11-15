#include <cstddef>
#include <cstdint>

#include <vulkan/vulkan.h>

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateDebugReportCallbackEXT(VkInstance instance,
                                                                         const VkDebugReportCallbackCreateInfoEXT *info,
                                                                         const VkAllocationCallbacks *,
                                                                         VkDebugReportCallbackEXT *callback) {
  if (!instance || !info || !callback) return VK_ERROR_INITIALIZATION_FAILED;
  *callback = reinterpret_cast<VkDebugReportCallbackEXT>(1);
  return VK_SUCCESS;
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkDebugReportMessageEXT(VkInstance, VkDebugReportFlagsEXT, VkDebugReportObjectTypeEXT, uint64_t,
                                                              size_t, int32_t, const char *, const char *) {}

extern "C" VKAPI_ATTR void VKAPI_CALL vkDestroyDebugReportCallbackEXT(VkInstance, VkDebugReportCallbackEXT, const VkAllocationCallbacks *) {
}
