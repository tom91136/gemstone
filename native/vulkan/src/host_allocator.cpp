#include "host_allocator.hpp"

#include <algorithm>
#include <array>
#include <cstdio>
#include <cstdlib>
#include <limits>

#include <gemstone/environment.hpp>

namespace gemstone::vulkan::host {
struct Block {
  Block *next, *hashNext, *freeNext;
  Arena *arena;
  void *payload;
  size_t size, alignment;
  bool used;
};
namespace {
std::mutex registryMutex;
std::array<Block *, 4096> registry{};
size_t bucket(void *p) { return (reinterpret_cast<uintptr_t>(p) >> 4) % registry.size(); }
void registerBlock(Block *block) {
  std::lock_guard lock(registryMutex);
  auto &head = registry[bucket(block->payload)];
  block->hashNext = head;
  head = block;
}
bool recycle(void *p) noexcept {
  std::lock_guard lock(registryMutex);
  for (auto *block = registry[bucket(p)]; block; block = block->hashNext) {
    if (block->payload != p) continue;
    std::lock_guard arenaLock(block->arena->mutex);
    block->used = false;
    block->freeNext = block->arena->freeBlocks;
    block->arena->freeBlocks = block;
    return true;
  }
  return false;
}
} // namespace
Arena *Arena::create(const VkAllocationCallbacks *provided, Arena *parent, VkSystemAllocationScope lifetime) {
  const auto callbacks = provided ? *provided : parent ? parent->callbacks : VkAllocationCallbacks{};
  void *storage = callbacks.pfnAllocation ? callbacks.pfnAllocation(callbacks.pUserData, sizeof(Arena), alignof(Arena), lifetime)
                                          : ::operator new(sizeof(Arena));
  if (!storage) throw std::bad_alloc();
  auto *arena = new (storage) Arena;
  arena->callbacks = callbacks;
  arena->scope = lifetime;
  arena->device = lifetime == VK_SYSTEM_ALLOCATION_SCOPE_DEVICE ? arena : parent ? parent->device : nullptr;
  return arena;
}
void Arena::trim() noexcept {
  Block *released = nullptr;
  {
    std::lock_guard lock(mutex);
    freeBlocks = nullptr;
    for (auto **at = &blocks; *at;) {
      auto *block = *at;
      if (block->used) {
        at = &block->next;
        continue;
      }
      *at = block->next;
      block->next = released;
      released = block;
    }
  }
  while (released) {
    auto *next = released->next;
    {
      std::lock_guard lock(registryMutex);
      auto **at = &registry[bucket(released->payload)];
      while (*at != released)
        at = &(*at)->hashNext;
      *at = released->hashNext;
    }
    callbacks.pfnFree(callbacks.pUserData, released);
    released = next;
  }
}
void Arena::destroy(const VkAllocationCallbacks *provided) noexcept {
  const auto allocator = provided ? *provided : callbacks;
  auto *block = blocks;
  while (block) {
    auto *next = block->next;
    if (block->used && gemstone::environment::value(gemstone::environment::VulkanValidateAllocators)) {
      std::fprintf(stderr, "Gemstone Vulkan: host storage outlived its allocator\n");
      std::abort();
    }
    {
      std::lock_guard lock(registryMutex);
      auto **at = &registry[bucket(block->payload)];
      while (*at != block)
        at = &(*at)->hashNext;
      *at = block->hashNext;
    }
    allocator.pfnFree(allocator.pUserData, block);
    block = next;
  }
  this->~Arena();
  if (allocator.pfnFree) allocator.pfnFree(allocator.pUserData, this);
  else ::operator delete(this);
}
void *allocate(size_t size, size_t alignment, bool aligned = false) {
  auto *arena = current;
  if (!arena || !arena->callbacks.pfnAllocation) {
    return aligned ? ::operator new(size, std::align_val_t{alignment}) : ::operator new(size);
  }
  alignment = std::max(alignment, alignof(Block));
  {
    std::lock_guard lock(arena->mutex);
    for (auto **at = &arena->freeBlocks; *at; at = &(*at)->freeNext) {
      auto *block = *at;
      if (block->size >= size && block->alignment >= alignment) {
        *at = block->freeNext;
        block->used = true;
        return block->payload;
      }
    }
  }
  if (size > std::numeric_limits<size_t>::max() - sizeof(Block) - alignment) throw std::bad_alloc();
  const size_t offset = (sizeof(Block) + alignment - 1) & ~(alignment - 1);
  // Callbacks are deliberately outside our locks.
  auto *storage = arena->callbacks.pfnAllocation(arena->callbacks.pUserData, offset + std::max(size, size_t{1}), alignment, arena->scope);
  if (!storage) throw std::bad_alloc();
  auto *block = new (storage) Block{nullptr, nullptr, nullptr, arena, static_cast<char *>(storage) + offset, size, alignment, true};
  {
    std::lock_guard lock(arena->mutex);
    block->next = arena->blocks;
    arena->blocks = block;
  }
  registerBlock(block);
  return block->payload;
}
void deallocate(void *p, size_t alignment = 0) noexcept {
  if (!p || recycle(p)) return;
  if (alignment) ::operator delete(p, std::align_val_t{alignment});
  else ::operator delete(p);
}
} // namespace gemstone::vulkan::host

// These private entry points are selected by host_allocator_redirect.hpp only
// for this runtime. This translation unit retains the system allocation ABI.
using gemstone::vulkan::host::allocate;
using gemstone::vulkan::host::deallocate;
extern "C" {
void *gemstone_vulkan_host_new(size_t n) { return allocate(n, __STDCPP_DEFAULT_NEW_ALIGNMENT__); }
void *gemstone_vulkan_host_new_aligned(size_t n, std::align_val_t a) { return allocate(n, static_cast<size_t>(a), true); }
void *gemstone_vulkan_host_new_nothrow(size_t n, const std::nothrow_t &) noexcept {
  try {
    return gemstone_vulkan_host_new(n);
  } catch (...) {
    return nullptr;
  }
}
void *gemstone_vulkan_host_new_aligned_nothrow(size_t n, std::align_val_t a, const std::nothrow_t &) noexcept {
  try {
    return gemstone_vulkan_host_new_aligned(n, a);
  } catch (...) {
    return nullptr;
  }
}
void gemstone_vulkan_host_delete(void *p) noexcept { deallocate(p); }
void gemstone_vulkan_host_delete_sized(void *p, size_t) noexcept { deallocate(p); }
void gemstone_vulkan_host_delete_aligned(void *p, std::align_val_t a) noexcept { deallocate(p, static_cast<size_t>(a)); }
void gemstone_vulkan_host_delete_sized_aligned(void *p, size_t, std::align_val_t a) noexcept { deallocate(p, static_cast<size_t>(a)); }
void gemstone_vulkan_host_delete_nothrow(void *p, const std::nothrow_t &) noexcept { deallocate(p); }
void gemstone_vulkan_host_delete_aligned_nothrow(void *p, std::align_val_t a, const std::nothrow_t &) noexcept {
  deallocate(p, static_cast<size_t>(a));
}
}
