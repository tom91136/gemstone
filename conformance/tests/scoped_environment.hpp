#pragma once

#include <cstdlib>
#include <exception>
#include <optional>
#include <stdexcept>
#include <string>

namespace conformance_test {

// Process-wide overrides belong on the test thread, outside concurrent work.
class Environment {
  std::string name;
  std::optional<std::string> previous;

  static int assign(const char *name, const char *value) {
    // NOLINTNEXTLINE(concurrency-mt-unsafe)
    if (value) return ::setenv(name, value, 1);
    // NOLINTNEXTLINE(concurrency-mt-unsafe)
    return ::unsetenv(name);
  }

public:
  Environment(const char *name, const char *value) : name(name) {
    if (const auto *existing = std::getenv(name)) previous = existing;
    if (assign(name, value) != 0) throw std::runtime_error("cannot configure test environment: " + this->name);
  }
  Environment(const Environment &) = delete;
  Environment &operator=(const Environment &) = delete;
  ~Environment() {
    if (assign(name.c_str(), previous ? previous->c_str() : nullptr) != 0) std::terminate();
  }
};

} // namespace conformance_test
