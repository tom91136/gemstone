#pragma once

#include <algorithm>
#include <chrono>
#include <cmath>
#include <cstdlib>
#include <exception>
#include <limits>
#include <memory>
#include <optional>
#include <stdexcept>
#include <string>
#include <thread>

#include <dirent.h>
#include <stddef.h>

#ifdef __linux__
  #include <sched.h>
#elifdef _WIN32
  #include <bit>

  #ifndef NOMINMAX
    #define NOMINMAX
  #endif
  #include <windows.h>
#endif

#include <gemstone/environment.hpp>

#include <environment_keys.hpp>

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

/** CPUs this process may run on, honouring affinity where the platform has it. */
inline unsigned availableThreadCount() {
#ifdef __linux__
  if (cpu_set_t set; sched_getaffinity(0, sizeof(set), &set) == 0) return static_cast<unsigned>(std::max(1, CPU_COUNT(&set)));
#elifdef _WIN32
  // Both masks are zero once a process spans processor groups.
  if (DWORD_PTR process = 0, system = 0; GetProcessAffinityMask(GetCurrentProcess(), &process, &system) && process != 0)
    return static_cast<unsigned>(std::popcount(process));
#endif
  return std::max(1U, std::thread::hardware_concurrency());
}

inline uint64_t positiveSetting(const char *name, uint64_t fallback) {
  const char *raw = gemstone::environment::value(name);
  if (!raw) return fallback;
  const auto parsed = gemstone::environment::positiveInteger(raw);
  if (!parsed || *parsed > std::numeric_limits<uint32_t>::max())
    throw std::invalid_argument(gemstone::environment::rejected(name, raw, "a positive integer"));
  return *parsed;
}

/** Deadlines suit a 32-thread host; fewer threads stretch them up to fourfold, and the scale multiplies the result. */
inline double timeoutFactor() {
  const auto threads = positiveSetting(gemstone::environment::ThreadCount, availableThreadCount());
  const char *raw = gemstone::environment::value(gemstone::environment::TimeoutScale);
  const auto thousandths =
      raw ? gemstone::environment::positiveThousandths(raw) : std::optional<uint64_t>(gemstone::environment::TimeoutScaleDefault * 1000);
  if (!thousandths)
    throw std::invalid_argument(
        gemstone::environment::rejected(gemstone::environment::TimeoutScale, raw, gemstone::environment::PositiveThousandthsExpected));
  return static_cast<double>(*thousandths) / 1000.0 * std::clamp(32.0 / static_cast<double>(threads), 1.0, 4.0);
}

/** Only for waits expected to succeed; a window that must elapse keeps its length. */
template <typename Rep, typename Period> std::chrono::milliseconds scaled(std::chrono::duration<Rep, Period> timeout) {
  static const auto factor = timeoutFactor();
  return std::chrono::milliseconds(std::llround(std::chrono::duration<double, std::milli>(timeout).count() * factor));
}

template <typename Predicate, typename Rep, typename Period>
[[nodiscard]] bool waitUntil(Predicate predicate, std::chrono::duration<Rep, Period> timeout) {
  const auto deadline = std::chrono::steady_clock::now() + scaled(timeout);
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
