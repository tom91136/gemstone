#pragma once

#include <chrono>
#include <memory>
#include <stdexcept>
#include <thread>
#include <utility>

#include <dirent.h>
#include <stddef.h>

namespace gemstone_test {

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
