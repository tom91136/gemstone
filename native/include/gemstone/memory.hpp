#pragma once

#include <algorithm>
#include <cstddef>
#include <cstdint>
#include <limits>
#include <vector>

#include <aspartame/optional.hpp>
#include <aspartame/vector.hpp>

namespace gemstone {

// Half-open byte ranges. Never round a borrowed range out to allocator/page boundaries.
struct MemoryRange {
  uintptr_t base = 0;
  size_t size = 0;
  [[nodiscard]] bool valid() const { return base != 0 && size != 0 && size <= std::numeric_limits<uintptr_t>::max() - base; }
  [[nodiscard]] uintptr_t end() const { return base + size; }
  [[nodiscard]] bool contains(uintptr_t address, size_t bytes = 0) const {
    return address >= base && address - base <= size && bytes <= size - (address - base);
  }
  [[nodiscard]] bool overlaps(MemoryRange other) const { return base < other.end() && other.base < end(); }
};

enum class MemoryOwnership { Borrowed, Owned };

// API registrations are independent leases over backing bytes. Backing storage is
// owned by each adapter; overlapping leases must never create duplicate mirrors.
// Callers serialise transactions with their runtime lock.
class MemoryRegistrations {
public:
  struct View {
    MemoryRange range;
    MemoryOwnership ownership;
    uint64_t flags;
  };

private:
  std::vector<View> views;

public:
  [[nodiscard]] std::vector<MemoryRange> ranges() const {
    using namespace aspartame;
    return views ^ map([](const View &view) { return view.range; });
  }
  [[nodiscard]] const View *exact(uintptr_t base, size_t size = 0) const {
    using namespace aspartame;
    return views ^ last_index_where_maybe([&](const View &view) { return view.range.base == base && (size == 0 || view.range.size == size); }) ^
           map([&](size_t index) { return &views[index]; }) ^ get_or_else<const View *>(nullptr);
  }
  [[nodiscard]] const View *find(uintptr_t address, size_t bytes = 1) const {
    const View *found = nullptr;
    for (const auto &view : views)
      if (view.range.contains(address, bytes) && (!found || view.range.size < found->range.size)) found = &view;
    return found;
  }
  [[nodiscard]] bool overlaps(MemoryRange range) const {
    using namespace aspartame;
    return views ^ exists([&](const View &view) { return view.range.overlaps(range); });
  }
  bool add(MemoryRange range, MemoryOwnership ownership, uint64_t flags, bool allowOverlap = false) {
    if (!range.valid() || (!allowOverlap && overlaps(range))) return false;
    views.push_back({.range = range, .ownership = ownership, .flags = flags});
    return true;
  }
  bool remove(uintptr_t base, MemoryOwnership ownership, size_t size = 0) {
    // Repeated locks are independent leases; release one, never all of them.
    for (auto it = views.end(); it != views.begin();) {
      --it;
      if (it->range.base == base && it->ownership == ownership && (size == 0 || it->range.size == size)) {
        views.erase(it);
        return true;
      }
    }
    return false;
  }
};

// Union of the live view intersections, without including gaps or retired bytes.
inline std::vector<MemoryRange> coveredMemoryRanges(MemoryRange requested, std::vector<MemoryRange> covered) {
  using namespace aspartame;
  std::vector<MemoryRange> result;
  if (!requested.valid()) return result;
  covered ^= sort_by([](const MemoryRange &range) { return range.base; });
  for (const auto range : covered) {
    if (!range.valid() || !requested.overlaps(range)) continue;
    const auto begin = std::max(requested.base, range.base);
    const auto end = std::min(requested.end(), range.end());
    if (!result.empty() && begin <= result.back().end()) result.back().size = std::max(result.back().end(), end) - result.back().base;
    else result.push_back({.base = begin, .size = end - begin});
  }
  return result;
}

// Return only uncovered bytes, preserving holes instead of guessing a larger extent.
inline std::vector<MemoryRange> missingMemoryRanges(MemoryRange requested, std::vector<MemoryRange> covered) {
  using namespace aspartame;
  std::vector<MemoryRange> result;
  if (!requested.valid()) return result;
  covered ^= sort_by([](const MemoryRange &range) { return range.base; });
  auto cursor = requested.base;
  for (const auto range : covered) {
    if (range.end() <= cursor || range.base >= requested.end()) continue;
    if (range.base > cursor) result.push_back({.base = cursor, .size = range.base - cursor});
    cursor = std::max(cursor, std::min(range.end(), requested.end()));
  }
  if (cursor < requested.end()) result.push_back({.base = cursor, .size = requested.end() - cursor});
  return result;
}

// C APIs hand back, or pass on, as mutable an address their caller supplied as const
template <typename T> [[nodiscard]] constexpr T *apiAddress(const T *address) noexcept {
  return const_cast<T *>(address); // NOLINT(cppcoreguidelines-pro-type-const-cast)
}
} // namespace gemstone
