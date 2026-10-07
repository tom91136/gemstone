#pragma once

#include <algorithm>
#include <cstddef>
#include <cstdint>
#include <limits>
#include <vector>

namespace gemstone {

// Half-open byte ranges. Never round a borrowed range out to allocator/page boundaries.
struct MemoryRange {
  uintptr_t base = 0;
  size_t size = 0;
  bool valid() const { return base != 0 && size != 0 && size <= std::numeric_limits<uintptr_t>::max() - base; }
  uintptr_t end() const { return base + size; }
  bool contains(uintptr_t address, size_t bytes = 0) const {
    return address >= base && address - base <= size && bytes <= size - (address - base);
  }
  bool overlaps(MemoryRange other) const { return base < other.end() && other.base < end(); }
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
  std::vector<MemoryRange> ranges() const {
    std::vector<MemoryRange> result;
    result.reserve(views.size());
    for (const auto &view : views)
      result.push_back(view.range);
    return result;
  }
  const View *exact(uintptr_t base, size_t size = 0) const {
    for (auto it = views.rbegin(); it != views.rend(); ++it)
      if (it->range.base == base && (size == 0 || it->range.size == size)) return &*it;
    return nullptr;
  }
  const View *find(uintptr_t address, size_t bytes = 1) const {
    const View *found = nullptr;
    for (const auto &view : views)
      if (view.range.contains(address, bytes) && (!found || view.range.size < found->range.size)) found = &view;
    return found;
  }
  bool overlaps(MemoryRange range) const {
    for (const auto &view : views)
      if (view.range.overlaps(range)) return true;
    return false;
  }
  bool add(MemoryRange range, MemoryOwnership ownership, uint64_t flags, bool allowOverlap = false) {
    if (!range.valid() || (!allowOverlap && overlaps(range))) return false;
    views.push_back({range, ownership, flags});
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
  std::vector<MemoryRange> result;
  if (!requested.valid()) return result;
  std::sort(covered.begin(), covered.end(), [](auto a, auto b) { return a.base < b.base; });
  for (const auto range : covered) {
    if (!range.valid() || !requested.overlaps(range)) continue;
    const auto begin = std::max(requested.base, range.base), end = std::min(requested.end(), range.end());
    if (!result.empty() && begin <= result.back().end()) result.back().size = std::max(result.back().end(), end) - result.back().base;
    else result.push_back({begin, end - begin});
  }
  return result;
}

// Return only uncovered bytes, preserving holes instead of guessing a larger extent.
inline std::vector<MemoryRange> missingMemoryRanges(MemoryRange requested, std::vector<MemoryRange> covered) {
  std::vector<MemoryRange> result;
  if (!requested.valid()) return result;
  std::sort(covered.begin(), covered.end(), [](auto a, auto b) { return a.base < b.base; });
  auto cursor = requested.base;
  for (const auto range : covered) {
    if (range.end() <= cursor || range.base >= requested.end()) continue;
    if (range.base > cursor) result.push_back({cursor, range.base - cursor});
    cursor = std::max(cursor, std::min(range.end(), requested.end()));
  }
  if (cursor < requested.end()) result.push_back({cursor, requested.end() - cursor});
  return result;
}
} // namespace gemstone
