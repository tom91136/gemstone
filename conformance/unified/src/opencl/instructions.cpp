#include "test_support.hpp"
#include <fmt/format.h>

#define CL_TARGET_OPENCL_VERSION 300
#include <algorithm>
#include <cstddef>
#include <cstdint>
#include <limits>
#include <string>
#include <vector>

#include <CL/cl.h>
#include <catch2/catch_test_macros.hpp>

#include "opencl/session.hpp"

namespace {
using namespace unified::opencl;

// clang and llvm-spirv lower these to OpSatConvertSToU and OpSatConvertUToS, at equal, narrower and wider widths
constexpr const char *SaturateSource = R"cl(
  __kernel void saturate(__global const int *s, __global const uint *u, __global const long *sl, __global const ulong *ul,
                         __global uint *out) {
    size_t i = get_global_id(0);
    __global uint *row = out + i * 8;
    row[0] = convert_uint_sat(s[i]);
    row[1] = (uint)convert_int_sat(u[i]);
    row[2] = convert_uchar_sat(s[i]);
    row[3] = (uint)(int)convert_char_sat(u[i]);
    row[4] = convert_ushort_sat(sl[i]);
    long l = convert_long_sat(ul[i]);
    row[5] = (uint)l;
    row[6] = (uint)(l >> 32);
    uint4 v = convert_uint4_sat((int4)(s[i], (int)(0u - (uint)s[i]), s[i] >> 1, -1));
    row[7] = v.x ^ (v.y << 1) ^ (v.z << 2) ^ (v.w << 3);
  })cl";

template <class R, class T> R clamped(T value) {
  const __int128 wide = value;
  if (wide < std::numeric_limits<R>::min()) return std::numeric_limits<R>::min();
  if (wide > std::numeric_limits<R>::max()) return std::numeric_limits<R>::max();
  return static_cast<R>(value);
}

// `options` is how the kernel is built; -g adds OpenCL.DebugInfo.100 instructions, which carry no semantics
void saturate(size_t items, const std::string &options) {
  Session session;
  auto *kernel = session.kernel(SaturateSource, options, "saturate");
  std::vector<int32_t> s(items);
  std::vector<uint32_t> u(items);
  std::vector<int64_t> sl(items);
  std::vector<uint64_t> ul(items);
  const int32_t edges[] = {
      0, 1, -1, 127, 128, 255, 256, -128, -129, std::numeric_limits<int32_t>::max(), std::numeric_limits<int32_t>::min()};
  for (size_t i = 0; i < items; ++i) {
    const auto mixed = static_cast<uint64_t>(i) * 0x9e3779b97f4a7c15ULL;
    s[i] = i < std::size(edges) ? edges[i] : static_cast<int32_t>(mixed >> 32);
    u[i] = i < std::size(edges) ? static_cast<uint32_t>(edges[i]) : static_cast<uint32_t>(mixed);
    sl[i] = i % 3 == 0 ? static_cast<int64_t>(s[i]) : static_cast<int64_t>(mixed ^ (mixed >> 17));
    ul[i] = i % 2 == 0 ? static_cast<uint64_t>(u[i]) : mixed;
  }
  auto *sBuffer = session.buffer(s);
  auto *uBuffer = session.buffer(u);
  auto *slBuffer = session.buffer(sl);
  auto *ulBuffer = session.buffer(ul);
  auto *out = session.buffer(std::vector<uint32_t>(items * 8, 0xdeadbeefU));
  args(kernel, {sBuffer, uBuffer, slBuffer, ulBuffer, out});
  session.run(kernel, 1, {items, 1, 1}, nullptr);
  const auto rows = session.read<uint32_t>(out, items * 8);
  for (size_t i = 0; i < items; ++i) {
    const auto *row = rows.data() + (i * 8);
    INFO(fmt::format("item {}: s {} u {} sl {} ul {}", i, s[i], u[i], sl[i], ul[i]));
    CHECK(row[0] == clamped<uint32_t>(s[i]));
    CHECK(std::cmp_equal(row[1], clamped<int32_t>(u[i])));
    CHECK(row[2] == clamped<uint8_t>(s[i]));
    CHECK(std::cmp_equal(row[3], static_cast<int32_t>(clamped<int8_t>(u[i]))));
    CHECK(row[4] == clamped<uint16_t>(sl[i]));
    const auto l = static_cast<uint64_t>(clamped<int64_t>(ul[i]));
    CHECK(row[5] == static_cast<uint32_t>(l));
    CHECK(row[6] == static_cast<uint32_t>(l >> 32));
    const int32_t lanes[] = {s[i], static_cast<int32_t>(0U - static_cast<uint32_t>(s[i])), s[i] >> 1, -1};
    uint32_t mixed = 0;
    for (int lane = 0; lane < 4; ++lane)
      mixed ^= clamped<uint32_t>(lanes[lane]) << lane;
    REQUIRE(row[7] == mixed);
  }
}
} // namespace

TEST_CASE("edge-cases/runtime/opencl/saturating-conversions/integers/n256", "[unified][runtime][compact]") { saturate(256, ""); }

TEST_CASE("edge-cases/runtime/opencl/debug-information/saturating-conversions/n256", "[unified][runtime][compact]") { saturate(256, "-g"); }
