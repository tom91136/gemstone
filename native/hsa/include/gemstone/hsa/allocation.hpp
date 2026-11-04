#pragma once

#include <stdint.h>

namespace gemstone::hsa {

// The emulated GPU exposes allocation id n at AllocationBase + (n - 1) * AllocationSpan.
// Every translation unit that translates between allocation ids and client
// pointers must use this encoding.
inline constexpr uint64_t AllocationBase = uint64_t{0x400000000000};
inline constexpr uint64_t AllocationSpan = uint64_t{0x100000000};

// A loaded program occupies the window ProgramBase + id * ProgramSpan. Its function pointers and globals are device
// addresses that name no allocation.
inline constexpr uint64_t ProgramBase = uint64_t{0x1000000000};
inline constexpr uint64_t ProgramSpan = uint64_t{0x40000000};
inline constexpr uint64_t ProgramLimit = uint64_t{0x100000000000};

constexpr bool isProgramAddress(uint64_t pointer) { return pointer >= ProgramBase && pointer < ProgramLimit; }

} // namespace gemstone::hsa
