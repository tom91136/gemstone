#include <algorithm>
#include <atomic>
#include <cstdio>

#include <unistd.h>

#include "gemstone_vulkan_adapter.hpp"
#include <gemstone/trace.hpp>

#include "environment_keys.hpp"
#include "resources.hpp"

namespace gemstone::vulkan::adapter {
VkResult impl_vkAllocateMemory(VkDevice, const VkMemoryAllocateInfo *info, const VkAllocationCallbacks *, VkDeviceMemory *memory) {
  if (!info || !memory || info->memoryTypeIndex != 0 || info->allocationSize > maxAllocationSize) return VK_ERROR_OUT_OF_DEVICE_MEMORY;
  std::unique_ptr<Memory> object;
  try {
    object = std::make_unique<Memory>(static_cast<size_t>(std::max<VkDeviceSize>(1, info->allocationSize)));
  } catch (const std::bad_alloc &) {
    return VK_ERROR_OUT_OF_HOST_MEMORY;
  }
  *memory = makeHandle<VkDeviceMemory>();
  std::lock_guard lock(stateMutex);
  memories[*memory] = std::move(object);
  return VK_SUCCESS;
}

void impl_vkFreeMemory(VkDevice, VkDeviceMemory memory, const VkAllocationCallbacks *) {
  {
    std::lock_guard lock(stateMutex);
    memories.erase(memory);
  }
  deleteHandle(memory);
}

void impl_vkGetDeviceMemoryCommitment(VkDevice, VkDeviceMemory memory, VkDeviceSize *committed) {
  if (!committed) return;
  std::lock_guard lock(stateMutex);
  const auto found = memories.find(memory);
  *committed = found == memories.end() ? 0 : found->second->bytes.size();
}

VkResult impl_vkMapMemory(VkDevice, VkDeviceMemory memory, VkDeviceSize offset, VkDeviceSize size, VkMemoryMapFlags, void **data) {
  if (!data) return VK_ERROR_MEMORY_MAP_FAILED;
  std::lock_guard lock(stateMutex);
  const auto it = memories.find(memory);
  if (it == memories.end() || offset > it->second->bytes.size()) return VK_ERROR_MEMORY_MAP_FAILED;
  const VkDeviceSize length = size == VK_WHOLE_SIZE ? it->second->bytes.size() - offset : size;
  if (length > it->second->bytes.size() - offset) return VK_ERROR_MEMORY_MAP_FAILED;
  *data = it->second->bytes.data() + offset;
  return VK_SUCCESS;
}

void impl_vkUnmapMemory(VkDevice, VkDeviceMemory) {}

VkResult impl_vkFlushMappedMemoryRanges(VkDevice, uint32_t, const VkMappedMemoryRange *) { return VK_SUCCESS; }
VkResult impl_vkInvalidateMappedMemoryRanges(VkDevice, uint32_t, const VkMappedMemoryRange *) { return VK_SUCCESS; }

VkResult impl_vkCreateBuffer(VkDevice, const VkBufferCreateInfo *info, const VkAllocationCallbacks *, VkBuffer *buffer) {
  if (!info || !buffer || info->size == 0) return VK_ERROR_INITIALIZATION_FAILED;
  *buffer = makeHandle<VkBuffer>();
  std::lock_guard lock(stateMutex);
  buffers.emplace(*buffer, BoundBuffer{VK_NULL_HANDLE, 0, info->size});
  return VK_SUCCESS;
}

VkResult impl_vkCreateBufferView(VkDevice, const VkBufferViewCreateInfo *info, const VkAllocationCallbacks *, VkBufferView *view) {
  if (!info || !view) return VK_ERROR_INITIALIZATION_FAILED;
  if (!storageColorFormat(info->format) && info->format != VK_FORMAT_B8G8R8A8_UNORM) return VK_ERROR_FORMAT_NOT_SUPPORTED;
  std::lock_guard lock(stateMutex);
  const auto buffer = buffers.find(info->buffer);
  if (buffer == buffers.end() || info->offset >= buffer->second.size) return VK_ERROR_INITIALIZATION_FAILED;
  const auto remaining = buffer->second.size - info->offset;
  const auto size = texelSize(info->format);
  const auto range = info->range == VK_WHOLE_SIZE ? remaining / size * size : info->range;
  if (!range || range > remaining || range % size || range / size > UINT32_MAX) return VK_ERROR_INITIALIZATION_FAILED;
  *view = makeHandle<VkBufferView>();
  bufferViews.emplace(*view, BufferView{info->buffer, info->format, info->offset, range});
  return VK_SUCCESS;
}
void impl_vkDestroyBufferView(VkDevice, VkBufferView view, const VkAllocationCallbacks *) {
  std::lock_guard lock(stateMutex);
  bufferViews.erase(view);
  deleteHandle(view);
}
void impl_vkDestroyBuffer(VkDevice, VkBuffer buffer, const VkAllocationCallbacks *) {
  {
    std::lock_guard lock(stateMutex);
    buffers.erase(buffer);
  }
  deleteHandle(buffer);
}

void impl_vkGetBufferMemoryRequirements(VkDevice, VkBuffer buffer, VkMemoryRequirements *requirements) {
  if (!requirements) return;
  std::lock_guard lock(stateMutex);
  const auto it = buffers.find(buffer);
  *requirements = {};
  if (it != buffers.end()) requirements->size = it->second.size;
  requirements->alignment = 64;
  requirements->memoryTypeBits = 1;
}

void impl_vkGetBufferMemoryRequirements2(VkDevice device, const VkBufferMemoryRequirementsInfo2 *info,
                                         VkMemoryRequirements2 *requirements) {
  if (info && requirements) {
    vkGetBufferMemoryRequirements(device, info->buffer, &requirements->memoryRequirements);
    for (auto next = static_cast<VkBaseOutStructure *>(requirements->pNext); next; next = next->pNext)
      if (next->sType == VK_STRUCTURE_TYPE_MEMORY_DEDICATED_REQUIREMENTS) {
        auto *dedicated = reinterpret_cast<VkMemoryDedicatedRequirements *>(next);
        dedicated->prefersDedicatedAllocation = VK_FALSE;
        dedicated->requiresDedicatedAllocation = VK_FALSE;
      }
  }
}

VkResult impl_vkBindBufferMemory(VkDevice, VkBuffer buffer, VkDeviceMemory memory, VkDeviceSize offset) {
  std::lock_guard lock(stateMutex);
  const auto size = buffers.find(buffer);
  const auto allocation = memories.find(memory);
  if (size == buffers.end() || allocation == memories.end() || offset > allocation->second->bytes.size()
      || size->second.size > allocation->second->bytes.size() - offset)
    return VK_ERROR_OUT_OF_DEVICE_MEMORY;
  buffers[buffer] = {memory, offset, size->second.size};
  return VK_SUCCESS;
}

VkResult impl_vkBindBufferMemory2(VkDevice device, uint32_t count, const VkBindBufferMemoryInfo *infos) {
  if (count && !infos) return VK_ERROR_INITIALIZATION_FAILED;
  for (uint32_t i = 0; i < count; ++i) {
    const auto result = vkBindBufferMemory(device, infos[i].buffer, infos[i].memory, infos[i].memoryOffset);
    if (result != VK_SUCCESS) return result;
  }
  return VK_SUCCESS;
}

VkResult impl_vkCreateDescriptorSetLayout(VkDevice, const VkDescriptorSetLayoutCreateInfo *info, const VkAllocationCallbacks *,
                                          VkDescriptorSetLayout *layout) {
  if (!info || !layout || (info->bindingCount && !info->pBindings)) return VK_ERROR_INITIALIZATION_FAILED;
  DescriptorLayout bindings;
  if (info->bindingCount) bindings.assign(info->pBindings, info->pBindings + info->bindingCount);
  std::lock_guard lock(stateMutex);
  for (auto &binding : bindings) {
    if (binding.pImmutableSamplers)
      for (uint32_t i = 0; i < binding.descriptorCount; ++i) {
        const auto sampler = samplers.find(binding.pImmutableSamplers[i]);
        if (sampler == samplers.end()) return VK_ERROR_INITIALIZATION_FAILED;
        bindings.immutableSamplers[{binding.binding, i}] = sampler->second;
      }
    binding.pImmutableSamplers = nullptr;
  }
  *layout = makeHandle<VkDescriptorSetLayout>();
  descriptorLayouts.emplace(*layout, std::move(bindings));
  return VK_SUCCESS;
}
void impl_vkDestroyDescriptorSetLayout(VkDevice, VkDescriptorSetLayout layout, const VkAllocationCallbacks *) {
  std::lock_guard lock(stateMutex);
  descriptorLayouts.erase(layout);
  deleteHandle(layout);
}

VkResult impl_vkCreateDescriptorPool(VkDevice, const VkDescriptorPoolCreateInfo *info, const VkAllocationCallbacks *,
                                     VkDescriptorPool *pool) {
  if (!info || !pool || info->maxSets == 0) return VK_ERROR_INITIALIZATION_FAILED;
  *pool = makeHandle<VkDescriptorPool>();
  std::lock_guard lock(stateMutex);
  descriptorPools.emplace(*pool, DescriptorPoolState{info->maxSets, {}});
  return VK_SUCCESS;
}
void impl_vkDestroyDescriptorPool(VkDevice, VkDescriptorPool pool, const VkAllocationCallbacks *) {
  {
    std::lock_guard lock(stateMutex);
    const auto found = descriptorPools.find(pool);
    if (found != descriptorPools.end()) {
      for (const auto set : found->second.sets) {
        descriptorSets.sets.erase(set);
        deleteHandle(set);
      }
      descriptorPools.erase(found);
    }
  }
  deleteHandle(pool);
}
VkResult impl_vkResetDescriptorPool(VkDevice, VkDescriptorPool pool, VkDescriptorPoolResetFlags) {
  std::lock_guard lock(stateMutex);
  const auto found = descriptorPools.find(pool);
  if (found == descriptorPools.end()) return VK_ERROR_INITIALIZATION_FAILED;
  for (const auto set : found->second.sets) {
    descriptorSets.sets.erase(set);
    deleteHandle(set);
  }
  found->second.sets.clear();
  return VK_SUCCESS;
}

VkResult impl_vkAllocateDescriptorSets(VkDevice, const VkDescriptorSetAllocateInfo *info, VkDescriptorSet *sets) {
  if (!info || (info->descriptorSetCount && !sets)) return VK_ERROR_INITIALIZATION_FAILED;
  std::lock_guard lock(stateMutex);
  const auto pool = descriptorPools.find(info->descriptorPool);
  if (pool == descriptorPools.end()) return VK_ERROR_INITIALIZATION_FAILED;
  if (info->descriptorSetCount > pool->second.maxSets - pool->second.sets.size()) return VK_ERROR_OUT_OF_POOL_MEMORY;
  for (uint32_t i = 0; i < info->descriptorSetCount; ++i) {
    sets[i] = makeHandle<VkDescriptorSet>();
    pool->second.sets.insert(sets[i]);
    if (info->pSetLayouts && descriptorLayouts.contains(info->pSetLayouts[i])) {
      const auto &layout = descriptorLayouts.at(info->pSetLayouts[i]);
      auto &record = descriptorSets.sets.try_emplace(sets[i], layout).first->second;
      record.immutableSamplers = layout.immutableSamplers;
    }
  }
  return VK_SUCCESS;
}
VkResult impl_vkFreeDescriptorSets(VkDevice, VkDescriptorPool pool, uint32_t count, const VkDescriptorSet *sets) {
  if (count && !sets) return VK_ERROR_INITIALIZATION_FAILED;
  std::lock_guard lock(stateMutex);
  const auto owner = descriptorPools.find(pool);
  if (owner == descriptorPools.end()) return VK_ERROR_INITIALIZATION_FAILED;
  for (uint32_t i = 0; i < count; ++i) {
    if (!sets[i]) continue;
    owner->second.sets.erase(sets[i]);
    descriptorSets.sets.erase(sets[i]);
    deleteHandle(sets[i]);
  }
  return VK_SUCCESS;
}
void impl_vkUpdateDescriptorSets(VkDevice, uint32_t writeCount, const VkWriteDescriptorSet *writes, uint32_t copyCount,
                                 const VkCopyDescriptorSet *copies) {
  std::lock_guard lock(stateMutex);
  descriptorSets.update(writeCount, writes, copyCount, copies);
}

VkResult impl_vkCreatePipelineLayout(VkDevice, const VkPipelineLayoutCreateInfo *, const VkAllocationCallbacks *,
                                     VkPipelineLayout *layout) {
  if (!layout) return VK_ERROR_INITIALIZATION_FAILED;
  *layout = makeHandle<VkPipelineLayout>();
  return VK_SUCCESS;
}
void impl_vkDestroyPipelineLayout(VkDevice, VkPipelineLayout layout, const VkAllocationCallbacks *) { deleteHandle(layout); }

VkResult impl_vkCreatePipelineCache(VkDevice, const VkPipelineCacheCreateInfo *, const VkAllocationCallbacks *, VkPipelineCache *cache) {
  if (!cache) return VK_ERROR_INITIALIZATION_FAILED;
  *cache = makeHandle<VkPipelineCache>();
  return VK_SUCCESS;
}
void impl_vkDestroyPipelineCache(VkDevice, VkPipelineCache cache, const VkAllocationCallbacks *) { deleteHandle(cache); }
VkResult impl_vkGetPipelineCacheData(VkDevice, VkPipelineCache, size_t *size, void *data) {
  if (!size) return VK_ERROR_INITIALIZATION_FAILED;
  // There is no serialized compiled payload yet. Even an empty cache carries
  // the standard little-endian header so applications can validate compatibility.
  std::array<uint8_t, 16 + VK_UUID_SIZE> header{};
  VkPhysicalDeviceProperties properties{};
  fillProperties(&properties);
  const std::array<uint32_t, 4> words{static_cast<uint32_t>(header.size()), VK_PIPELINE_CACHE_HEADER_VERSION_ONE, properties.vendorID,
                                      properties.deviceID};
  for (size_t word = 0; word < words.size(); ++word)
    for (size_t byte = 0; byte < 4; ++byte)
      header[word * 4 + byte] = static_cast<uint8_t>(words[word] >> (byte * 8));
  std::memcpy(header.data() + 16, properties.pipelineCacheUUID, VK_UUID_SIZE);
  if (!data) {
    *size = header.size();
    return VK_SUCCESS;
  }
  if (*size < header.size()) {
    *size = 0;
    return VK_INCOMPLETE;
  }
  std::memcpy(data, header.data(), header.size());
  *size = header.size();
  return VK_SUCCESS;
}
VkResult impl_vkMergePipelineCaches(VkDevice, VkPipelineCache, uint32_t, const VkPipelineCache *) {
  // Empty caches have no compiled entries to merge.
  return VK_SUCCESS;
}

VkResult impl_vkCreateShaderModule(VkDevice, const VkShaderModuleCreateInfo *info, const VkAllocationCallbacks *, VkShaderModule *module) {
  if (!info || !module || !info->pCode || info->codeSize < 20 || info->codeSize % sizeof(uint32_t) != 0) return VK_ERROR_INVALID_SHADER_NV;
  *module = makeHandle<VkShaderModule>();
  const auto *bytes = reinterpret_cast<const uint8_t *>(info->pCode);
  std::lock_guard lock(stateMutex);
  modules[*module] = {bytes, bytes + info->codeSize};
  return VK_SUCCESS;
}
void impl_vkDestroyShaderModule(VkDevice, VkShaderModule module, const VkAllocationCallbacks *) {
  {
    std::lock_guard lock(stateMutex);
    modules.erase(module);
  }
  deleteHandle(module);
}

VkResult impl_vkCreateComputePipelines(VkDevice, VkPipelineCache, uint32_t count, const VkComputePipelineCreateInfo *infos,
                                       const VkAllocationCallbacks *, VkPipeline *output) {
  if ((count && (!infos || !output))) return VK_ERROR_INITIALIZATION_FAILED;
  std::lock_guard lock(stateMutex);
  for (uint32_t i = 0; i < count; ++i) {
    const auto module = modules.find(infos[i].stage.module);
    if (module == modules.end() || !infos[i].stage.pName) {
      if (gemstone::traceEnabled("vulkan")) std::fprintf(stderr, "Gemstone Vulkan: compute pipeline has no shader module or entry\n");
      return VK_ERROR_INVALID_SHADER_NV;
    }
    auto prepared =
        gemstone::vulkan::prepareShader(module->second, infos[i].stage.pName, ShaderStageKind::Compute, infos[i].stage.pSpecializationInfo);
    if (!prepared) return VK_ERROR_INVALID_SHADER_NV;
    if (const char *directory = gemstone::environment::value(gemstone::environment::VulkanDump)) {
      static std::atomic<unsigned> serial{0};
      char filename[1024];
      std::snprintf(filename, sizeof(filename), "%s/gemstone-%ld-%u.spv", directory, static_cast<long>(getpid()), serial++);
      if (auto *file = std::fopen(filename, "wb")) {
        (void)std::fwrite(prepared->image.data(), 1, prepared->image.size(), file);
        std::fclose(file);
      }
    }
    output[i] = makeHandle<VkPipeline>();
    pipelines[output[i]] = std::move(*prepared);
  }
  return VK_SUCCESS;
}

void impl_vkDestroyPipeline(VkDevice, VkPipeline pipeline, const VkAllocationCallbacks *) {
  {
    std::lock_guard lock(stateMutex);
    pipelines.erase(pipeline);
    graphicsPipelines.erase(pipeline);
  }
  deleteHandle(pipeline);
}

VkResult impl_vkCreateDescriptorUpdateTemplate(VkDevice, const VkDescriptorUpdateTemplateCreateInfo *info, const VkAllocationCallbacks *,
                                               VkDescriptorUpdateTemplate *handle) {
  if (!info || !handle || (info->descriptorUpdateEntryCount && !info->pDescriptorUpdateEntries)) return VK_ERROR_INITIALIZATION_FAILED;
  if (info->templateType != VK_DESCRIPTOR_UPDATE_TEMPLATE_TYPE_DESCRIPTOR_SET) return VK_ERROR_FEATURE_NOT_PRESENT;
  std::vector<VkDescriptorUpdateTemplateEntry> entries;
  if (info->descriptorUpdateEntryCount)
    entries.assign(info->pDescriptorUpdateEntries, info->pDescriptorUpdateEntries + info->descriptorUpdateEntryCount);
  for (const auto &entry : entries)
    if (entry.descriptorType != VK_DESCRIPTOR_TYPE_INPUT_ATTACHMENT && entry.descriptorType != VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER
        && entry.descriptorType != VK_DESCRIPTOR_TYPE_STORAGE_BUFFER && entry.descriptorType != VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER_DYNAMIC
        && entry.descriptorType != VK_DESCRIPTOR_TYPE_STORAGE_BUFFER_DYNAMIC && entry.descriptorType != VK_DESCRIPTOR_TYPE_STORAGE_IMAGE
        && entry.descriptorType != VK_DESCRIPTOR_TYPE_SAMPLED_IMAGE && entry.descriptorType != VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER
        && entry.descriptorType != VK_DESCRIPTOR_TYPE_SAMPLER && entry.descriptorType != VK_DESCRIPTOR_TYPE_UNIFORM_TEXEL_BUFFER
        && entry.descriptorType != VK_DESCRIPTOR_TYPE_STORAGE_TEXEL_BUFFER)
      return VK_ERROR_FEATURE_NOT_PRESENT;
  std::lock_guard lock(stateMutex);
  *handle = makeHandle<VkDescriptorUpdateTemplate>();
  descriptorTemplates.emplace(*handle, std::move(entries));
  return VK_SUCCESS;
}
void impl_vkDestroyDescriptorUpdateTemplate(VkDevice, VkDescriptorUpdateTemplate handle, const VkAllocationCallbacks *) {
  std::lock_guard lock(stateMutex);
  descriptorTemplates.erase(handle);
  deleteHandle(handle);
}
void impl_vkUpdateDescriptorSetWithTemplate(VkDevice, VkDescriptorSet set, VkDescriptorUpdateTemplate handle, const void *data) {
  std::lock_guard lock(stateMutex);
  descriptorSets.updateTemplate(set, descriptorTemplates.at(handle), data);
}
} // namespace gemstone::vulkan::adapter
