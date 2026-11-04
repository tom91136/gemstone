#include <algorithm>
#include <charconv>
#include <cstdint>
#include <cstdio>
#include <map>
#include <numbers>
#include <optional>
#include <string>
#include <string_view>
#include <utility>
#include <vector>

#include <catch2/catch_test_macros.hpp>
#include <hip/hip_runtime.h>

#include "../capture.hpp"
#include "support.hpp"

using namespace hip_support;

namespace {

std::optional<unsigned> numberAfter(const std::string &line, std::string_view prefix) {
  if (!line.starts_with(prefix)) return std::nullopt;
  unsigned value = 0;
  const char *end = line.data() + line.size();
  const auto [ptr, error] = std::from_chars(line.data() + prefix.size(), end, value);
  if (error != std::errc{}) return std::nullopt;
  return value;
}

template <typename Launch> std::string captured(Launch &&launch, std::FILE *stream = stdout) {
  StreamCapture capture(stream);
  std::forward<Launch>(launch)();
  finish("printf");
  return capture.finish();
}

std::string formatted(const char *format, auto... arguments) {
  char buffer[512];
  std::snprintf(buffer, sizeof(buffer), format, arguments...);
  return buffer;
}

__global__ void formats() {
  if (threadIdx.x != 0) return;
  printf("%d %u %ld %lu %lld %llx|%x %c|%s|\n", -42, 4000000000U, -5000000000L, 18000000000UL, -9000000000000LL, 0xdeadbeefcafeULL, 255,
         'z', "text");
  printf("%5.2f|%-8d|%08.3f|%+d|% d|%e|%g|%g|%%\n", std::numbers::pi, 42, -std::numbers::pi, 5, 5, 12345.678, 0.0001, 1e20);
  printf("%hhd %hd %*d|%-*d|%.*f|%.3s|\n", 300, 70000, 6, 42, 4, 7, 2, 2.5, "abcdef");
  printf("%p|\n", static_cast<void *>(nullptr));
  printf("%10s|\n", "right");
  printf("%-10s|\n", "left");
}

__global__ void toStderr() {
  if (threadIdx.x != 0) return;
  const char format[] = "to stderr %d\n";
  const auto message = __ockl_fprintf_append_string_n(__ockl_fprintf_stderr_begin(), format, sizeof(format), 0);
  __ockl_fprintf_append_args(message, 1, 7, 0, 0, 0, 0, 0, 0, 1);
  printf("to stdout %d\n", 8);
}

__global__ void everyThread() { printf("thread %u %u\n", blockIdx.x, threadIdx.x); }

__global__ void twoLines() {
  const unsigned tid = (blockIdx.x * blockDim.x) + threadIdx.x;
  printf("first %u\n", tid);
  printf("second %u\n", tid);
}

__global__ void divergent() {
  const unsigned tid = threadIdx.x;
  if (tid % 3 == 0) printf("fizz %u\n", tid);
  else if (tid % 3 == 1) printf("buzz %u %f\n", tid, tid * 0.5);
}

__global__ void returnValue(int *counts) { counts[threadIdx.x] = printf("value %u\n", threadIdx.x); }

__global__ void wide() {
  if (threadIdx.x != 0) return;
  printf("%d %d %d %d %d %d %d %d %d %d %d %d %d %d %d %d %d %d %d %d\n", 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
         20);
  printf("%s\n", "0123456789abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz0123456789");
}

} // namespace

TEST_CASE("runtime/hip/device_printf/formats", "[unified][runtime][printf]") {
  const auto text = captured([] { formats<<<1, 64>>>(); });
  std::string expected;
  expected += formatted("%d %u %ld %lu %lld %llx|%x %c|%s|\n", -42, 4000000000U, -5000000000L, 18000000000UL, -9000000000000LL,
                        0xdeadbeefcafeULL, 255, 'z', "text");
  expected += formatted("%5.2f|%-8d|%08.3f|%+d|% d|%e|%g|%g|%%\n", std::numbers::pi, 42, -std::numbers::pi, 5, 5, 12345.678, 0.0001, 1e20);
  expected += formatted("%hhd %hd %*d|%-*d|%.*f|%.3s|\n", 300, 70000, 6, 42, 4, 7, 2, 2.5, "abcdef");
  expected += formatted("%p|\n", static_cast<void *>(nullptr));
  expected += formatted("%10s|\n", "right");
  expected += formatted("%-10s|\n", "left");
  REQUIRE(text == expected);
}

TEST_CASE("runtime/hip/device_printf/stderr", "[unified][runtime][printf]") {
  std::string out;
  const auto err = captured([&] { out = captured([] { toStderr<<<1, 32>>>(); }); }, stderr);
  REQUIRE(err == "to stderr 7\n");
  REQUIRE(out == "to stdout 8\n");
}

TEST_CASE("runtime/hip/device_printf/every-thread", "[unified][runtime][printf]") {
  constexpr unsigned blocks = 4;
  constexpr unsigned threads = 100;
  auto got = lines(captured([] { everyThread<<<blocks, threads>>>(); }));
  std::vector<std::string> expected;
  for (unsigned block = 0; block < blocks; ++block)
    for (unsigned thread = 0; thread < threads; ++thread)
      expected.push_back(formatted("thread %u %u", block, thread));
  std::sort(got.begin(), got.end());
  std::sort(expected.begin(), expected.end());
  REQUIRE(got == expected);
}

TEST_CASE("runtime/hip/device_printf/per-thread-order", "[unified][runtime][printf]") {
  constexpr unsigned blocks = 2;
  constexpr unsigned threads = 96;
  const auto got = lines(captured([] { twoLines<<<blocks, threads>>>(); }));
  REQUIRE(got.size() == size_t{2} * blocks * threads);
  std::map<unsigned, size_t> firsts;
  for (size_t index = 0; index < got.size(); ++index) {
    if (const auto tid = numberAfter(got[index], "first ")) firsts[*tid] = index;
  }
  REQUIRE(firsts.size() == size_t{blocks} * threads);
  for (size_t index = 0; index < got.size(); ++index) {
    if (const auto tid = numberAfter(got[index], "second ")) REQUIRE(firsts.at(*tid) < index);
  }
}

TEST_CASE("runtime/hip/device_printf/divergent-lanes", "[unified][runtime][printf]") {
  constexpr unsigned threads = 128;
  auto got = lines(captured([] { divergent<<<1, threads>>>(); }));
  std::vector<std::string> expected;
  for (unsigned tid = 0; tid < threads; ++tid) {
    if (tid % 3 == 0) expected.push_back(formatted("fizz %u", tid));
    else if (tid % 3 == 1) expected.push_back(formatted("buzz %u %f", tid, tid * 0.5));
  }
  std::sort(got.begin(), got.end());
  std::sort(expected.begin(), expected.end());
  REQUIRE(got == expected);
}

TEST_CASE("runtime/hip/device_printf/return-value", "[unified][runtime][printf]") {
  constexpr unsigned threads = 64;
  DeviceBuffer<int> counts(std::vector<int>(threads, -1));
  const auto text = captured([&] { returnValue<<<1, threads>>>(counts.data); });
  REQUIRE(lines(text).size() == threads);
  const auto result = counts.download();
  for (unsigned tid = 0; tid < threads; ++tid) {
    const int printed = static_cast<int>(formatted("value %u\n", tid).size());
    REQUIRE(result[tid] == printed);
  }
}

TEST_CASE("runtime/hip/device_printf/messages-spanning-packets", "[unified][runtime][printf]") {
  const auto text = captured([] { wide<<<1, 64>>>(); });
  std::string expected = "1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20\n";
  expected += "0123456789abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz0123456789\n";
  REQUIRE(text == expected);
}
