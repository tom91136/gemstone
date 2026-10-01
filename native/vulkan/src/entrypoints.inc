namespace {
template <typename Info, typename Create>
VkResult createPipelines(VkDevice device, VkPipelineCache cache, uint32_t count, const Info *infos, const VkAllocationCallbacks *allocator,
                         VkPipeline *output, Create create) {
  if (count && (!output || !infos)) return VK_ERROR_INITIALIZATION_FAILED;
  if (output) std::fill_n(output, count, VK_NULL_HANDLE);
  VkResult result = VK_SUCCESS;
  for (uint32_t i = 0; i < count; ++i) {
    try {
      host::Creation creation(allocator, handleArena(device), VK_SYSTEM_ALLOCATION_SCOPE_OBJECT);
      try {
        const auto status = create(device, cache, 1, infos + i, allocator, output + i);
        if (status == VK_SUCCESS) creation.commit();
        else {
          impl_vkDestroyPipeline(device, output[i], allocator);
          output[i] = VK_NULL_HANDLE;
          result = status;
        }
      } catch (...) {
        impl_vkDestroyPipeline(device, output[i], allocator);
        output[i] = VK_NULL_HANDLE;
        throw;
      }
    } catch (const std::bad_alloc &) {
      result = VK_ERROR_OUT_OF_HOST_MEMORY;
    }
  }
  return result;
}
} // namespace

// Ownership boundaries with specialized allocation or rollback behavior.

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkResetDescriptorPool(VkDevice device, VkDescriptorPool pool, VkDescriptorPoolResetFlags arg2) {
  host::Scope scope(handleArena(pool));
  try {
    return impl_vkResetDescriptorPool(device, pool, arg2);
  } catch (const std::bad_alloc &) {
    return VK_ERROR_OUT_OF_HOST_MEMORY;
  }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkAllocateDescriptorSets(VkDevice device, const VkDescriptorSetAllocateInfo *info,
                                                                   VkDescriptorSet *sets) {
  if (info && sets) std::fill_n(sets, info->descriptorSetCount, VK_NULL_HANDLE);
  host::Scope scope(info ? handleArena(info->descriptorPool) : nullptr);
  try {
    return impl_vkAllocateDescriptorSets(device, info, sets);
  } catch (const std::bad_alloc &) {
    if (info && sets) {
      impl_vkFreeDescriptorSets(device, info->descriptorPool, info->descriptorSetCount, sets);
      std::fill_n(sets, info->descriptorSetCount, VK_NULL_HANDLE);
    }
    return VK_ERROR_OUT_OF_HOST_MEMORY;
  }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateComputePipelines(VkDevice device, VkPipelineCache arg1, uint32_t count,
                                                                   const VkComputePipelineCreateInfo *infos,
                                                                   const VkAllocationCallbacks *allocator, VkPipeline *output) {
  return createPipelines(device, arg1, count, infos, allocator, output, impl_vkCreateComputePipelines);
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkResetCommandPool(VkDevice device, VkCommandPool pool, VkCommandPoolResetFlags arg2) {
  host::Scope scope(handleArena(pool));
  try {
    const auto result = impl_vkResetCommandPool(device, pool, arg2);
    if (result == VK_SUCCESS && (arg2 & VK_COMMAND_POOL_RESET_RELEASE_RESOURCES_BIT))
      if (auto *arena = handleArena(pool)) arena->trim();
    return result;
  } catch (const std::bad_alloc &) {
    return VK_ERROR_OUT_OF_HOST_MEMORY;
  }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkAllocateCommandBuffers(VkDevice device, const VkCommandBufferAllocateInfo *info,
                                                                   VkCommandBuffer *output) {
  if (info && output) std::fill_n(output, info->commandBufferCount, VK_NULL_HANDLE);
  host::Scope scope(info ? handleArena(info->commandPool) : nullptr);
  try {
    return impl_vkAllocateCommandBuffers(device, info, output);
  } catch (const std::bad_alloc &) {
    if (info && output) {
      impl_vkFreeCommandBuffers(device, info->commandPool, info->commandBufferCount, output);
      std::fill_n(output, info->commandBufferCount, VK_NULL_HANDLE);
    }
    return VK_ERROR_OUT_OF_HOST_MEMORY;
  }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkResetCommandBuffer(VkCommandBuffer command, VkCommandBufferResetFlags arg1) {
  host::Scope scope(handleArena(command));
  try {
    const auto result = impl_vkResetCommandBuffer(command, arg1);
    if (result == VK_SUCCESS && (arg1 & VK_COMMAND_BUFFER_RESET_RELEASE_RESOURCES_BIT))
      if (auto *arena = handleArena(command)) arena->trim();
    return result;
  } catch (const std::bad_alloc &) {
    return VK_ERROR_OUT_OF_HOST_MEMORY;
  }
}

extern "C" VKAPI_ATTR void VKAPI_CALL vkTrimCommandPool(VkDevice device, VkCommandPool pool, VkCommandPoolTrimFlags arg2) {
  host::Scope scope(handleArena(pool));
  try {
    impl_vkTrimCommandPool(device, pool, arg2);
    if (auto *arena = handleArena(pool)) arena->trim();
  } catch (const std::bad_alloc &) {
    std::lock_guard lock(stateMutex);
    const auto found = devices.find(device);
    if (found != devices.end()) found->second->lost = true;
    stateChanged.notify_all();
  }
}

extern "C" VKAPI_ATTR VkResult VKAPI_CALL vkCreateGraphicsPipelines(VkDevice device, VkPipelineCache arg1, uint32_t count,
                                                                    const VkGraphicsPipelineCreateInfo *infos,
                                                                    const VkAllocationCallbacks *allocator, VkPipeline *output) {
  return createPipelines(device, arg1, count, infos, allocator, output, impl_vkCreateGraphicsPipelines);
}
