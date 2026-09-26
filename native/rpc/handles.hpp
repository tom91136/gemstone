#ifndef GEMSTONE_EXECUTION_HANDLES_HPP
#define GEMSTONE_EXECUTION_HANDLES_HPP

#include <mutex>
#include <unordered_set>

#include <stdint.h>

namespace gemstone::rpc {

/** Magic carried by every opaque facade handle ("GEMSTONE" in ASCII). */
inline constexpr uint64_t HandleMagic = 0x47454d53544f4e45ULL;

/** Registry of live opaque handles. Facades consult it to reject stale, forged, or freed pointers before any field is
 * read; the vendor-specific magic and kind checks stay at the call site.
 */
class LiveHandles {
public:
  void insert(const void *value) {
    std::lock_guard lock(mutex_);
    values_.insert(value);
  }
  void erase(const void *value) {
    std::lock_guard lock(mutex_);
    values_.erase(value);
  }
  [[nodiscard]] bool contains(const void *value) const {
    std::lock_guard lock(mutex_);
    return values_.find(value) != values_.end();
  }

private:
  mutable std::mutex mutex_;
  std::unordered_set<const void *> values_;
};

} // namespace gemstone::rpc

#endif
