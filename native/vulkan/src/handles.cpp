#include "handles.hpp"

#include <array>
#include <cstddef>
#include <cstdint>
#include <mutex>

namespace gemstone::vulkan {
namespace {
struct Handle {
  host::Arena *arena{};
  Handle *next = nullptr;
};
std::mutex mutex;
std::array<Handle *, 4096> handles{};
size_t bucket(uintptr_t address) { return (address >> 4) % handles.size(); }
} // namespace
host::Arena *findHandleArena(uintptr_t address) {
  if (!address) return nullptr;
  std::scoped_lock const lock(mutex);
  for (auto *handle = handles[bucket(address)]; handle; handle = handle->next)
    if (reinterpret_cast<uintptr_t>(handle) == address) return handle->arena;
  return nullptr;
}
uintptr_t createHandle() {
  auto *handle = new Handle{.arena = host::current};
  const auto address = reinterpret_cast<uintptr_t>(handle);
  std::scoped_lock const lock(mutex);
  auto &head = handles[bucket(address)];
  handle->next = head;
  head = handle;
  return address;
}
void destroyHandle(uintptr_t address) {
  if (!address) return;
  auto *handle = reinterpret_cast<Handle *>(address);
  {
    std::scoped_lock const lock(mutex);
    auto **at = &handles[bucket(address)];
    while (*at && *at != handle)
      at = &(*at)->next;
    if (!*at) return;
    *at = handle->next;
  }
  delete handle;
}
} // namespace gemstone::vulkan
