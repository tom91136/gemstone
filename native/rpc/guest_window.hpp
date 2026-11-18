#pragma once

#include <cstdint>
#include <string>

namespace gemstone::guest_window {

enum class Outcome { Reserved, AlreadyReserved, Unreachable };

struct Reservation {
  Outcome outcome = Outcome::Reserved;
  std::string error;
  explicit operator bool() const { return error.empty(); }
};

// Keeps the host from mapping [base, base + size). Unreachable means the range lies beyond the task's address limit,
// which is equally safe; any other failure carries a message.
Reservation reserve(uint64_t base, uint64_t size);

// Reserves the generated layout's reserved range once per process, whatever number of facades load.
const Reservation &reserveLayout();

} // namespace gemstone::guest_window
