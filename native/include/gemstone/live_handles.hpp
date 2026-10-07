#pragma once

#include <mutex>
#include <unordered_map>

namespace gemstone {

/** Registry of live opaque handles and their kinds. Facades consult it before dereferencing a caller's handle, so stale,
 * forged, freed, and mistyped handles are rejected without reading their memory.
 */
template <class Kind> class LiveHandles {
public:
  void insert(const void *value, Kind kind) {
    std::lock_guard lock(mutex);
    values.insert_or_assign(value, kind);
  }
  void erase(const void *value) {
    std::lock_guard lock(mutex);
    values.erase(value);
  }
  [[nodiscard]] bool contains(const void *value, Kind kind) const {
    std::lock_guard lock(mutex);
    const auto found = values.find(value);
    return found != values.end() && found->second == kind;
  }

private:
  mutable std::mutex mutex;
  std::unordered_map<const void *, Kind> values;
};

} // namespace gemstone
