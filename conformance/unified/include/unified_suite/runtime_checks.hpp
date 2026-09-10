#pragma once
#include "unified_suite/result.hpp"
#include <stdexcept>
#include <vector>

namespace unified_suite::runtime_checks {
// Release acquired API objects in reverse order, including on assertion failure.
class Cleanup {
  std::vector<std::function<void()>> actions_;

public:
  Cleanup() = default;
  Cleanup(const Cleanup &) = delete;
  Cleanup &operator=(const Cleanup &) = delete;
  template <class F> void add(F action) { actions_.emplace_back(std::move(action)); }
  ~Cleanup() {
    for (auto it = actions_.rbegin(); it != actions_.rend(); ++it)
      (*it)();
  }
};
inline void require(bool condition, const std::string &message) {
  if (!condition)
    throw std::runtime_error(message);
}
inline std::vector<int> input(std::size_t size) {
  std::vector<int> values(size);
  for (std::size_t i = 0; i < size; ++i)
    values[i] = static_cast<int>(i * 37) - 511;
  return values;
}
} // namespace unified_suite::runtime_checks
