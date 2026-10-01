#pragma once

#include <cstddef>
#include <cstdint>
#include <mutex>
#include <new>

#include <vulkan/vulkan.h>

// A private heap for implementation host storage. Only Vulkan implementation
// translation units use it; the application's operator new is unchanged.
// Freed blocks can be reused, but callback storage belongs to the entire arena
// lifetime. In particular, a queue worker can recycle a block without invoking an
// application callback. Destroy the arena on the provoking API thread, after its
// objects (including workers) have finished using it.
namespace gemstone::vulkan::host {
struct Block;
struct Arena {
  VkAllocationCallbacks callbacks{};
  VkSystemAllocationScope scope;
  Arena *device = nullptr;
  Block *blocks = nullptr;
  Block *freeBlocks = nullptr;
  std::mutex mutex;
  static Arena *create(const VkAllocationCallbacks *, Arena *, VkSystemAllocationScope);
  void trim() noexcept;
  void destroy(const VkAllocationCallbacks * = nullptr) noexcept;
};
inline thread_local Arena *current = nullptr;
struct Scope {
  Arena *previous;
  explicit Scope(Arena *arena) noexcept : previous(current) { current = arena; }
  ~Scope() { current = previous; }
};
struct Creation {
  Arena *arena;
  Scope scope;
  Creation(const VkAllocationCallbacks *callbacks, Arena *parent, VkSystemAllocationScope lifetime)
      : arena(Arena::create(callbacks, parent, lifetime)), scope(arena) {}
  ~Creation() {
    if (arena) arena->destroy();
  }
  void commit() noexcept { arena = nullptr; }
};
} // namespace gemstone::vulkan::host
