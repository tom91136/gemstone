#pragma once

#include <chrono>
#include <cstdlib>
#include <exception>
#include <memory>
#include <optional>
#include <stdexcept>
#include <string>
#include <thread>

#include <dirent.h>
#include <stddef.h>

namespace gemstone_test {

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

template <typename Predicate, typename Rep, typename Period>
[[nodiscard]] bool waitUntil(Predicate predicate, std::chrono::duration<Rep, Period> timeout) {
  const auto deadline = std::chrono::steady_clock::now() + timeout;
  while (!predicate()) {
    if (std::chrono::steady_clock::now() >= deadline) return false;
    std::this_thread::yield();
  }
  return true;
}

inline size_t processThreadCount() {
  struct CloseDirectory {
    void operator()(DIR *directory) const noexcept { closedir(directory); }
  };
  const std::unique_ptr<DIR, CloseDirectory> directory(opendir("/proc/self/task"));
  if (!directory) throw std::runtime_error("cannot inspect /proc/self/task");
  size_t count = 0;
  while (const auto *entry = readdir(directory.get()))
    if (entry->d_name[0] != '.') ++count;
  return count;
}

} // namespace gemstone_test
