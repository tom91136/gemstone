#pragma once

#include <chrono>
#include <cstdlib>
#include <exception>
#include <memory>
#include <optional>
#include <stdexcept>
#include <string>
#include <thread>
#include <utility>

#include <dirent.h>
#include <stddef.h>

namespace gemstone_test {

// Process-wide overrides belong on the test thread, outside concurrent work.
class Environment {
  std::string name;
  std::optional<std::string> previous;

public:
  Environment(const char *name, const char *value) : name(name) {
    if (const auto *existing = std::getenv(name)) previous = existing;
    if ((value ? ::setenv(name, value, 1) : ::unsetenv(name)) != 0)
      throw std::runtime_error("cannot configure test environment: " + this->name);
  }
  Environment(const Environment &) = delete;
  Environment &operator=(const Environment &) = delete;
  ~Environment() {
    if ((previous ? ::setenv(name.c_str(), previous->c_str(), 1) : ::unsetenv(name.c_str())) != 0) std::terminate();
  }
};

template <typename Predicate, typename Rep, typename Period>
[[nodiscard]] bool waitUntil(Predicate &&predicate, std::chrono::duration<Rep, Period> timeout) {
  const auto deadline = std::chrono::steady_clock::now() + timeout;
  while (!std::forward<Predicate>(predicate)()) {
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
