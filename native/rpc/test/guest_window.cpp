#include "../guest_window.hpp"

#include <cerrno>
#include <cstdint>
#include <cstdlib>
#include <cstring>
#include <fstream>
#include <string>
#include <vector>

#include <unistd.h>

#include <catch2/catch_test_macros.hpp>

#include <address_layout.hpp>
#include <sys/mman.h>
#include <sys/resource.h>
#include <sys/types.h>
#include <sys/wait.h>

using gemstone::guest_window::Outcome;
using gemstone::guest_window::reserve;

namespace {

constexpr uint64_t Size = uint64_t{1} << 30;

uint64_t freeRange(uint64_t size) {
  void *found = mmap(nullptr, size, PROT_NONE, MAP_PRIVATE | MAP_ANONYMOUS | MAP_NORESERVE, -1, 0);
  REQUIRE((found != MAP_FAILED));
  REQUIRE((munmap(found, size) == 0));
  return reinterpret_cast<uint64_t>(found);
}

bool mapped(uint64_t address) {
  void *probe =
      mmap(reinterpret_cast<void *>(address), 4096, PROT_NONE, MAP_PRIVATE | MAP_ANONYMOUS | MAP_NORESERVE | MAP_FIXED_NOREPLACE, -1, 0);
  if (probe != MAP_FAILED) {
    munmap(probe, 4096);
    return false;
  }
  return errno == EEXIST;
}

} // namespace

TEST_CASE("a free range is reserved so the host cannot map it, and reserving again is idempotent", "[rpc][guest_window]") {
  const auto base = freeRange(Size);
  const auto first = reserve(base, Size);
  REQUIRE(first);
  CHECK((first.outcome == Outcome::Reserved));
  CHECK(mapped(base));
  CHECK(mapped(base + Size - 4096));
  void *hinted = mmap(reinterpret_cast<void *>(base + 4096), 4096, PROT_READ, MAP_PRIVATE | MAP_ANONYMOUS | MAP_FIXED_NOREPLACE, -1, 0);
  CHECK((hinted == MAP_FAILED));
  const auto second = reserve(base, Size);
  REQUIRE(second);
  CHECK((second.outcome == Outcome::AlreadyReserved));
  munmap(reinterpret_cast<void *>(base), Size);
}

TEST_CASE("a range with a foreign mapping inside is refused and the message names the conflict", "[rpc][guest_window]") {
  const auto base = freeRange(Size);
  void *page = mmap(reinterpret_cast<void *>(base + (Size / 2)), 4096, PROT_READ | PROT_WRITE,
                    MAP_PRIVATE | MAP_ANONYMOUS | MAP_FIXED_NOREPLACE, -1, 0);
  REQUIRE((page != MAP_FAILED));
  const auto result = reserve(base, Size);
  CHECK(!result);
  CHECK(result.error.contains("already mapped"));
  CHECK(!mapped(base));
  munmap(page, 4096);
}

TEST_CASE("a readable mapping covering the range is not mistaken for a reservation", "[rpc][guest_window]") {
  const auto base = freeRange(Size);
  void *region =
      mmap(reinterpret_cast<void *>(base), Size, PROT_READ, MAP_PRIVATE | MAP_ANONYMOUS | MAP_NORESERVE | MAP_FIXED_NOREPLACE, -1, 0);
  REQUIRE((region != MAP_FAILED));
  CHECK(!reserve(base, Size));
  munmap(region, Size);
}

TEST_CASE("an address-space limit that blocks the reservation is a failure, not an unreachable range", "[rpc][guest_window]") {
  const auto base = freeRange(uint64_t{1} << 34);
  const pid_t child = fork();
  REQUIRE((child >= 0));
  if (child == 0) {
    std::ifstream statm("/proc/self/statm");
    uint64_t pages = 0;
    statm >> pages;
    const rlimit limit{.rlim_cur = (pages * static_cast<uint64_t>(sysconf(_SC_PAGESIZE))) + (uint64_t{64} << 20),
                       .rlim_max = RLIM_INFINITY};
    if (setrlimit(RLIMIT_AS, &limit) != 0) _exit(10);
    const auto result = reserve(base, uint64_t{1} << 34);
    if (result || result.outcome == Outcome::Unreachable) _exit(11);
    if (!result.error.contains("RLIMIT_AS")) _exit(12);
    _exit(0);
  }
  int status = 0;
  REQUIRE((waitpid(child, &status, 0) == child));
  CHECK((WIFEXITED(status) && WEXITSTATUS(status) == 0));
}

TEST_CASE("a range beyond the task's address limit is unreachable", "[rpc][guest_window]") {
  const auto result = reserve(uint64_t{1} << 63, Size);
  REQUIRE(result);
  CHECK((result.outcome == Outcome::Unreachable));
}

TEST_CASE("the layout range is reserved or unreachable and forked children keep that", "[rpc][guest_window]") {
  const auto &layout = gemstone::guest_window::reserveLayout();
  REQUIRE(layout);
  const auto base = gemstone::address::ReservedBase;
  const auto limit = gemstone::address::ReservedLimit;
  void *hinted = mmap(reinterpret_cast<void *>(base + 4096), 4096, PROT_READ, MAP_PRIVATE | MAP_ANONYMOUS | MAP_FIXED_NOREPLACE, -1, 0);
  CHECK((hinted == MAP_FAILED));
  void *top = mmap(reinterpret_cast<void *>(limit - 4096), 4096, PROT_READ, MAP_PRIVATE | MAP_ANONYMOUS | MAP_FIXED_NOREPLACE, -1, 0);
  CHECK((top == MAP_FAILED));
  const pid_t child = fork();
  REQUIRE((child >= 0));
  if (child == 0) {
    const auto &inherited = gemstone::guest_window::reserveLayout();
    const auto again = reserve(base, limit - base);
    void *probe = mmap(reinterpret_cast<void *>(base + 4096), 4096, PROT_READ, MAP_PRIVATE | MAP_ANONYMOUS | MAP_FIXED_NOREPLACE, -1, 0);
    _exit(inherited && again && probe == MAP_FAILED ? 0 : 1);
  }
  int status = 0;
  REQUIRE((waitpid(child, &status, 0) == child));
  CHECK((WIFEXITED(status) && WEXITSTATUS(status) == 0));
}

TEST_CASE("neither malloc nor an unhinted mmap hands out the layout range", "[rpc][guest_window]") {
  REQUIRE(gemstone::guest_window::reserveLayout());
  const auto inside = [](const void *pointer) {
    const auto address = reinterpret_cast<uint64_t>(pointer);
    return address >= gemstone::address::ReservedBase && address < gemstone::address::ReservedLimit;
  };
  std::vector<void *> blocks;
  for (int index = 0; index < 256; ++index) {
    void *block = malloc(uint64_t{1} << 22);
    REQUIRE((block != nullptr));
    CHECK(!inside(block));
    blocks.push_back(block);
    void *mapping = mmap(nullptr, uint64_t{1} << 24, PROT_READ, MAP_PRIVATE | MAP_ANONYMOUS, -1, 0);
    REQUIRE((mapping != MAP_FAILED));
    CHECK(!inside(mapping));
    munmap(mapping, uint64_t{1} << 24);
  }
  for (void *block : blocks)
    free(block);
}
