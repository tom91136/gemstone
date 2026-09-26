#ifndef GEMSTONE_EXECUTION_RELOCATIONS_HPP
#define GEMSTONE_EXECUTION_RELOCATIONS_HPP

#include <cstdint>
#include <cstring>
#include <string>
#include <variant>
#include <vector>

#include "gemstone_execution_rpc.hpp"

namespace gemstone::execution {

struct RelocationTarget {
  void *base = nullptr;
  uint64_t size = 0;
};

/** Writes each relocation's target native address into its source allocation.
 * `resolve` maps an allocation id to its native base and size; `onApplied`
 * observes every successful translation so facades can retain typed caches.
 */
template <class Resolve, class OnApplied>
bool applyPointerRelocations(const std::vector<PointerRelocation> &relocations, Resolve resolve, OnApplied onApplied, std::string &error) {
  for (const auto &relocation : relocations) {
    const auto *sourceId = std::get_if<RelocationSourceAllocation>(&relocation.source);
    if (!sourceId) {
      error = "execution returned an invalid pointer relocation";
      return false;
    }
    const auto sourceIdValue = static_cast<uint64_t>(sourceId->id.value);
    const auto targetIdValue = static_cast<uint64_t>(relocation.target.value);
    const auto source = resolve(sourceIdValue);
    const auto target = resolve(targetIdValue);
    const auto sourceOffset = static_cast<uint64_t>(relocation.sourceOffset.value);
    const auto targetOffset = static_cast<uint64_t>(relocation.targetOffset.value);
    if (!source.base || !target.base || sourceOffset > source.size || sizeof(uintptr_t) > source.size - sourceOffset
        || targetOffset > target.size) {
      error = "execution returned an invalid pointer relocation";
      return false;
    }
    const auto address = reinterpret_cast<uintptr_t>(target.base) + targetOffset;
    std::memcpy(static_cast<uint8_t *>(source.base) + sourceOffset, &address, sizeof(address));
    onApplied(sourceIdValue, targetIdValue, sourceOffset, targetOffset);
  }
  return true;
}

} // namespace gemstone::execution

#endif
