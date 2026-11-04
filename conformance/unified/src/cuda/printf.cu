#include <algorithm>
#include <cstdint>
#include <cstdio>
#include <string>
#include <utility>
#include <vector>

#include "cuda/support.cuh"
#include <catch2/catch_test_macros.hpp>
#include <cuda_runtime.h>

#include "capture.hpp"

namespace {

#define PRINTF_LINES(X)                                                                                                                    \
  X("int %d %i %5d|%-5d|%05d|%+d\n", -42, 7, 42, 42, 42, 42)                                                                               \
  X("unsigned %u %x %X %o %#x %#o\n", 4000000000U, 255U, 255U, 8U, 255U, 8U)                                                               \
  X("long %ld %lld %lu %llu %llx %lX\n", -5000000000L, -9000000000000LL, 5000000000UL, 18000000000000000000ULL, 0xdeadbeefcafeULL,         \
    0xabcdefUL)                                                                                                                            \
  X("float %f %.2f %8.3f|%-8.1f|%+.0f\n", 3.14159F, 2.71828, 1.0, 2.5, 7.5)                                                                \
  X("exponent %e %E %.3e %g %G %g\n", 12345.678, 0.000123, 9.99995, 100000.0, 1e-10, 1234567.0)                                            \
  X("char %c%c%c|%3c|%-3c|\n", 'a', 'b', 'c', 'd', 'e')                                                                                    \
  X("string %s|%10s|%-10s|%.2s|%s\n", "hello", "right", "left", "truncate", "")                                                            \
  X("mixed %d %lld %d %f %s %c %u\n", 1, 2LL, 3, 4.5, "five", '6', 7U)                                                                     \
  X("short %hd %hu\n", -2, 65535)                                                                                                          \
  X("percent 100%% %d%%\n", 50)                                                                                                            \
  X("signs %d %d %x\n", -2147483647 - 1, 2147483647, -1)                                                                                   \
  X("pointer %p %p|%12p|\n", reinterpret_cast<void *>(0x1234), static_cast<void *>(nullptr), reinterpret_cast<void *>(0xabcdef0))          \
  X("none\n")

__global__ void formats() {
  if (threadIdx.x != 0 || blockIdx.x != 0) return;
#define DEVICE_LINE(...) printf(__VA_ARGS__);
  PRINTF_LINES(DEVICE_LINE)
#undef DEVICE_LINE
}

__global__ void lanes() { printf("lane %d square %u\n", threadIdx.x, threadIdx.x * threadIdx.x); }

__global__ void sequence(int count) {
  if (threadIdx.x != 0) return;
  for (int i = 0; i < count; ++i)
    printf("step %d of %d: %s %f\n", i, count, i % 2 ? "odd" : "even", i * 0.5);
}

__global__ void grid() {
  const unsigned tid = (blockIdx.x * blockDim.x) + threadIdx.x;
  printf("block %d thread %d global %u\n", blockIdx.x, threadIdx.x, tid);
}

__global__ void conditional() {
  const unsigned tid = (blockIdx.x * blockDim.x) + threadIdx.x;
  if (blockIdx.x % 2 == 1 && tid % 5 == 0) printf("selected %u in block %d\n", tid, blockIdx.x);
  if (tid % 7 == 3) printf("%s %u\n", tid % 2 ? "odd" : "even", tid);
}

__global__ void divergent() {
  if (threadIdx.x % 3 == 0) printf("zero %d\n", threadIdx.x);
  else if (threadIdx.x % 3 == 1) printf("one %d %f\n", threadIdx.x, threadIdx.x * 0.25);
  else printf("two %d %s\n", threadIdx.x, "x");
}

__global__ void repeated(int launch) {
  if (threadIdx.x == 0) printf("launch %d block %d\n", launch, blockIdx.x);
}

__global__ void alignment() {
  if (threadIdx.x != 0) return;
  const char local[] = "from local memory";
  printf("%c %lld %c %d %f %c %s %s\n", 'a', 1234567890123LL, 'b', 5, 1.5, 'c', local, "global");
}

__global__ void returns(int *results) {
  if (threadIdx.x != 0) return;
  results[0] = printf("no arguments\n");
  results[1] = printf("%d %d %d\n", 1, 2, 3);
  results[2] = printf("%d\n", 1);
}

template <typename F> std::string capture(F &&body) {
  StreamCapture output;
  std::forward<F>(body)();
  return output.finish();
}

std::vector<std::string> sorted(std::vector<std::string> values) {
  std::sort(values.begin(), values.end());
  return values;
}

template <typename F> std::string formatted(F &&write) {
  const int size = write(nullptr, 0);
  std::string text(static_cast<size_t>(size), '\0');
  std::forward<F>(write)(text.data(), text.size() + 1);
  return text;
}

#define RENDER(...) formatted([&](char *out, size_t capacity) { return std::snprintf(out, capacity, __VA_ARGS__); })

std::string expectedFormats() {
  std::string text;
#define HOST_LINE(...) text += RENDER(__VA_ARGS__);
  PRINTF_LINES(HOST_LINE)
#undef HOST_LINE
  return text;
}

} // namespace

TEST_CASE("runtime/cuda/printf/formats", "[unified][runtime][printf]") {
  const auto text = capture([] {
    formats<<<1, 1>>>();
    finish("formats");
  });
  REQUIRE(text == expectedFormats());
}

TEST_CASE("runtime/cuda/printf/warp-lanes-in-order", "[unified][runtime][printf]") {
  const auto text = capture([] {
    lanes<<<1, 32>>>();
    finish("lanes");
  });
  std::string expected;
  for (unsigned lane = 0; lane < 32; ++lane)
    expected += RENDER("lane %d square %u\n", lane, lane * lane);
  REQUIRE(text == expected);
}

TEST_CASE("runtime/cuda/printf/thread-sequence", "[unified][runtime][printf]") {
  constexpr int count = 40;
  const auto text = capture([] {
    sequence<<<1, 64>>>(count);
    finish("sequence");
  });
  std::string expected;
  for (int i = 0; i < count; ++i)
    expected += RENDER("step %d of %d: %s %f\n", i, count, i % 2 ? "odd" : "even", i * 0.5);
  REQUIRE(text == expected);
}

TEST_CASE("runtime/cuda/printf/many-blocks", "[unified][runtime][printf]") {
  constexpr unsigned blocks = 9;
  constexpr unsigned threads = 96;
  const auto text = capture([] {
    grid<<<blocks, threads>>>();
    finish("grid");
  });
  std::vector<std::string> expected;
  for (unsigned block = 0; block < blocks; ++block)
    for (unsigned thread = 0; thread < threads; ++thread)
      expected.push_back(RENDER("block %d thread %d global %u", block, thread, (block * threads) + thread));
  REQUIRE(sorted(lines(text)) == sorted(expected));
}

TEST_CASE("runtime/cuda/printf/conditional", "[unified][runtime][printf]") {
  constexpr unsigned blocks = 6;
  constexpr unsigned threads = 80;
  const auto text = capture([] {
    conditional<<<blocks, threads>>>();
    finish("conditional");
  });
  std::vector<std::string> expected;
  for (unsigned block = 0; block < blocks; ++block)
    for (unsigned thread = 0; thread < threads; ++thread) {
      const unsigned tid = (block * threads) + thread;
      if (block % 2 == 1 && tid % 5 == 0) expected.push_back(RENDER("selected %u in block %d", tid, block));
      if (tid % 7 == 3) expected.push_back(RENDER("%s %u", tid % 2 ? "odd" : "even", tid));
    }
  REQUIRE(sorted(lines(text)) == sorted(expected));
}

TEST_CASE("runtime/cuda/printf/divergent-warp", "[unified][runtime][printf]") {
  const auto text = capture([] {
    divergent<<<1, 64>>>();
    finish("divergent");
  });
  std::vector<std::string> expected;
  for (unsigned thread = 0; thread < 64; ++thread) {
    if (thread % 3 == 0) expected.push_back(RENDER("zero %d", thread));
    else if (thread % 3 == 1) expected.push_back(RENDER("one %d %f", thread, thread * 0.25));
    else expected.push_back(RENDER("two %d %s", thread, "x"));
  }
  REQUIRE(sorted(lines(text)) == sorted(expected));
}

TEST_CASE("runtime/cuda/printf/launch-order", "[unified][runtime][printf]") {
  cudaStream_t stream = nullptr;
  check(cudaStreamCreate(&stream), "stream");
  const auto text = capture([&] {
    for (int launch = 0; launch < 3; ++launch)
      repeated<<<1, 32, 0, stream>>>(launch);
    check(cudaGetLastError(), "launch");
    check(cudaStreamSynchronize(stream), "stream synchronize");
  });
  check(cudaStreamDestroy(stream), "stream destroy");
  REQUIRE(text == "launch 0 block 0\nlaunch 1 block 0\nlaunch 2 block 0\n");
}

TEST_CASE("runtime/cuda/printf/flush-on-memcpy", "[unified][runtime][printf]") {
  DeviceBuffer<int> results(std::vector<int>(3, 0));
  std::vector<int> host;
  const auto text = capture([&] {
    returns<<<1, 32>>>(results.data);
    check(cudaGetLastError(), "launch");
    host = results.download();
  });
  REQUIRE(text == "no arguments\n1 2 3\n1\n");
  REQUIRE(host == std::vector<int>{0, 3, 1});
}

TEST_CASE("runtime/cuda/printf/mixed-argument-alignment", "[unified][runtime][printf]") {
  const auto text = capture([] {
    alignment<<<1, 32>>>();
    finish("alignment");
  });
  REQUIRE(text == "a 1234567890123 b 5 1.500000 c from local memory global\n");
}
