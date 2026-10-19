#pragma once

#include <stdint.h>

namespace gemstone::hsa {

// The emulated GPU exposes allocation id n at AllocationBase + (n - 1) * AllocationSpan.
// Every translation unit that translates between allocation ids and client
// pointers must use this encoding.
inline constexpr uint64_t AllocationBase = uint64_t{0x400000000000};
inline constexpr uint64_t AllocationSpan = uint64_t{0x100000000};

} // namespace gemstone::hsa
