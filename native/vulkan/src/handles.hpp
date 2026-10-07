#pragma once

#include <cstdint>
#include <type_traits>

#include "host_allocator.hpp"

namespace gemstone::vulkan {
uintptr_t createHandle();
void destroyHandle(uintptr_t address);
host::Arena *findHandleArena(uintptr_t address);
inline host::Arena *deviceArena(host::Arena *arena) { return arena && arena->device ? arena->device : arena; }
template <typename Handle> uintptr_t handleAddress(Handle handle) {
  if constexpr (std::is_pointer_v<Handle>) return reinterpret_cast<uintptr_t>(handle);
  else return static_cast<uintptr_t>(handle);
}
template <typename Handle> host::Arena *handleArena(Handle handle) { return findHandleArena(handleAddress(handle)); }
template <typename Handle> Handle makeHandle() {
  const auto address = createHandle();
  if constexpr (std::is_pointer_v<Handle>) return reinterpret_cast<Handle>(address);
  else return static_cast<Handle>(address);
}
template <typename Handle> void deleteHandle(Handle handle) { destroyHandle(handleAddress(handle)); }
} // namespace gemstone::vulkan
