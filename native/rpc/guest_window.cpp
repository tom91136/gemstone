#include "guest_window.hpp"

#include <algorithm>
#include <cerrno>
#include <charconv>
#include <cstdint>
#include <cstdio>
#include <cstring>
#include <fstream>
#include <string>
#include <string_view>
#include <system_error>
#include <utility>

#include <unistd.h>

#include <address_layout.hpp>
#include <sys/mman.h>
#include <sys/resource.h>

namespace gemstone::guest_window {
namespace {

constexpr int ReserveFlags = MAP_PRIVATE | MAP_ANONYMOUS | MAP_NORESERVE | MAP_FIXED_NOREPLACE;

std::string hex(uint64_t value) {
  char buffer[24];
  std::snprintf(buffer, sizeof(buffer), "0x%llx", static_cast<unsigned long long>(value));
  return buffer;
}

std::string describe(uint64_t base, uint64_t size) { return "guest address range [" + hex(base) + ", " + hex(base + size) + ")"; }

struct Mapping {
  uint64_t begin = 0;
  uint64_t end = 0;
  std::string line;
  bool none = false;
};

bool parse(const std::string &line, Mapping &mapping) {
  const std::string_view text = line;
  const auto dash = text.find('-');
  const auto space = text.find(' ');
  if (dash == std::string_view::npos || space == std::string_view::npos || dash > space) return false;
  const char *const start = line.data();
  const auto first = std::from_chars(start, start + dash, mapping.begin, 16);
  const auto second = std::from_chars(start + dash + 1, start + space, mapping.end, 16);
  if (first.ec != std::errc{} || second.ec != std::errc{}) return false;
  mapping.none = text.substr(space + 1, 4) == "---p";
  mapping.line = line;
  return true;
}

// The first mapping in the range that is not PROT_NONE private memory, or a gap, as a description; empty when the
// range is covered end to end by PROT_NONE mappings.
std::string coverageFault(uint64_t base, uint64_t size) {
  std::ifstream maps("/proc/self/maps");
  if (!maps) return "cannot read /proc/self/maps";
  const auto end = base + size;
  uint64_t covered = base;
  std::string line;
  while (std::getline(maps, line)) {
    Mapping mapping;
    if (!parse(line, mapping) || mapping.end <= base) continue;
    if (mapping.begin >= end) break;
    if (!mapping.none) return line;
    if (mapping.begin > covered) return "a gap before " + line;
    covered = std::max(covered, mapping.end);
  }
  return covered >= end ? "" : "a gap at " + hex(covered);
}

std::string addressSpaceLimit() {
  rlimit limit{};
  if (getrlimit(RLIMIT_AS, &limit) != 0 || limit.rlim_cur == RLIM_INFINITY) return "RLIMIT_AS is unlimited";
  return "RLIMIT_AS is " + std::to_string(limit.rlim_cur) + " bytes";
}

// True when a limit on the address space, rather than the task's address limit, is what refuses one more page.
bool limitedByAddressSpace(uint64_t page) {
  rlimit limit{};
  if (getrlimit(RLIMIT_AS, &limit) != 0 || limit.rlim_cur == RLIM_INFINITY) return false;
  std::ifstream statm("/proc/self/statm");
  uint64_t pages = 0;
  statm >> pages;
  return (pages * page) + page > limit.rlim_cur;
}

Reservation failure(std::string message) { return {.outcome = Outcome::Reserved, .error = std::move(message)}; }

} // namespace

Reservation reserve(uint64_t base, uint64_t size) {
  const auto page = static_cast<uint64_t>(sysconf(_SC_PAGESIZE));
  void *const found = mmap(reinterpret_cast<void *>(base), size, PROT_NONE, ReserveFlags, -1, 0);
  if (found != MAP_FAILED) {
    if (reinterpret_cast<uint64_t>(found) == base) return {.outcome = Outcome::Reserved, .error = {}};
    munmap(found, size);
    return failure(describe(base, size) + " cannot be reserved: the kernel ignores MAP_FIXED_NOREPLACE");
  }
  const int code = errno;
  if (code == EEXIST) {
    const auto fault = coverageFault(base, size);
    if (fault.empty()) return {.outcome = Outcome::AlreadyReserved, .error = {}};
    return failure(describe(base, size) + " is already mapped in this process: " + fault);
  }
  if (code == ENOMEM) {
    void *const probe = mmap(reinterpret_cast<void *>(base), page, PROT_NONE, ReserveFlags, -1, 0);
    if (probe != MAP_FAILED) {
      munmap(probe, page);
      return failure(describe(base, size) + " cannot be reserved: " + std::strerror(code) + " (" + addressSpaceLimit() + ")");
    }
    if (errno == ENOMEM && !limitedByAddressSpace(page)) return {.outcome = Outcome::Unreachable, .error = {}};
    return failure(describe(base, size) + " cannot be probed: " + std::strerror(errno) + " (" + addressSpaceLimit() + ")");
  }
  return failure(describe(base, size) + " cannot be reserved: " + std::strerror(code));
}

const Reservation &reserveLayout() {
  static const Reservation result = reserve(address::ReservedBase, address::ReservedLimit - address::ReservedBase);
  return result;
}

} // namespace gemstone::guest_window
