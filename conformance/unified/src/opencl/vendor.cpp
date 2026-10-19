#include "test_support.hpp"
#include <fmt/format.h>

#define CL_TARGET_OPENCL_VERSION 300
#include <bit>
#include <cmath>
#include <cstddef>
#include <cstdint>
#include <cstring>
#include <string>
#include <vector>

#include <CL/cl.h>
#include <CL/cl_ext.h>
#include <aspartame/all.hpp>
#include <catch2/catch_test_macros.hpp>

#include "opencl/session.hpp"

using namespace aspartame;

// cl_khr_kernel_clock, which older OpenCL headers lack
#ifndef CL_DEVICE_KERNEL_CLOCK_CAPABILITIES_KHR
  #define CL_DEVICE_KERNEL_CLOCK_CAPABILITIES_KHR 0x1076
  #define CL_DEVICE_KERNEL_CLOCK_SCOPE_DEVICE_KHR (1 << 0)
  #define CL_DEVICE_KERNEL_CLOCK_SCOPE_WORK_GROUP_KHR (1 << 1)
  #define CL_DEVICE_KERNEL_CLOCK_SCOPE_SUB_GROUP_KHR (1 << 2)
#endif

namespace {
using namespace unified::opencl;

void requireExtension(Session &session, const char *name) {
  if (!session.extension(name)) SKIP(fmt::format("{} unavailable", name));
}

uint32_t pack(const std::vector<int64_t> &values, int bits) {
  uint32_t word = 0;
  for (size_t index = 0; index < values.size(); ++index)
    word |= static_cast<uint32_t>(values[index] & ((int64_t{1} << bits) - 1)) << (index * bits);
  return word;
}

uint16_t half(float value) {
  // the test values are small multiples of 1/4, exact in binary16
  if (value == 0.0F) return std::signbit(value) ? 0x8000 : 0;
  const uint32_t bits = std::bit_cast<uint32_t>(value);
  const uint32_t sign = (bits >> 16) & 0x8000;
  const int exponent = static_cast<int>((bits >> 23) & 0xff) - 127 + 15;
  return static_cast<uint16_t>(sign | (exponent << 10) | ((bits >> 13) & 0x3ff));
}

using unified::support::bfloat16Bits;

// intel_sub_group_*_matrix_mad_k*: each of 8 lanes passes its A rows packed across K, its B column packed in 32-bit
// words and its C column, then receives its result column; IGC lowers it to the matrix engines and Gemstone's driver
// to OpSubgroupMatrixMultiplyAccumulateINTEL
constexpr const char *MatrixSource = R"cl(
  __attribute__((intel_reqd_sub_group_size(8)))
  __kernel void integers(__global const int8 *a, __global const int8 *b, __global const int8 *c, __global int8 *out) {
    size_t lane = get_sub_group_local_id();
    out[lane] = intel_sub_group_i8_i8_matrix_mad_k32(a[lane], b[lane], c[lane]);
  }
  __attribute__((intel_reqd_sub_group_size(8)))
  __kernel void halves(__global const int8 *a, __global const int8 *b, __global const float8 *c, __global float8 *out) {
    size_t lane = get_sub_group_local_id();
    out[lane] = intel_sub_group_f16_f16_matrix_mad_k16(a[lane], b[lane], c[lane]);
  }
  __attribute__((intel_reqd_sub_group_size(8)))
  __kernel void bfloats(__global const int8 *a, __global const int8 *b, __global const float8 *c, __global float8 *out) {
    size_t lane = get_sub_group_local_id();
    out[lane] = intel_sub_group_bf16_bf16_matrix_mad_k16(a[lane], b[lane], c[lane]);
  })cl";

enum class Element { Int8, Half, BFloat16 };

const char *matrixKernelName(Element element) {
  switch (element) {
    case Element::Int8: return "integers";
    case Element::Half: return "halves";
    case Element::BFloat16: return "bfloats";
  }
  return "";
}

void matrix(Element element) {
  Session session;
  requireExtension(session, "cl_intel_subgroup_matrix_multiply_accumulate");
  auto *kernel = session.kernel(MatrixSource, "", matrixKernelName(element));
  const int inner = element == Element::Int8 ? 32 : 16;
  const int perWord = element == Element::Int8 ? 4 : 2;
  const auto table = [](int rows, int columns, auto cell) {
    return iota(0, rows)
           | map([&](int row) { return iota(0, columns) | map([&](int column) -> double { return cell(row, column); }) | to_vector(); })
           | to_vector();
  };
  const auto a = table(8, inner, [&](int row, int k) {
    return element == Element::Int8 ? (((row * 7) + (k * 3)) % 23) - 11 : (((row + (2 * k)) % 7) - 3) * 0.5;
  });
  const auto b = table(inner, 8, [&](int k, int column) {
    return element == Element::Int8 ? (((k * 5) + column) % 19) - 9 : (((k + column) % 5) - 2) * 0.25;
  });
  const auto c = table(8, 8, [&](int row, int column) { return element == Element::Int8 ? (row * 100) + column : row - (column * 0.5); });
  const auto bits = [&](double value) -> int64_t {
    if (element == Element::Int8) return static_cast<int64_t>(value);
    return element == Element::Half ? half(static_cast<float>(value)) : bfloat16Bits(static_cast<float>(value));
  };
  const auto packed = [&](auto at) { return pack(iota(0, perWord) | map([&](int t) { return bits(at(t)); }) | to_vector(), 32 / perWord); };
  const auto perLane = [](auto cell) {
    return iota(0, 8) | flat_map([&](int lane) { return iota(0, 8) | map([&](int index) { return cell(lane, index); }) | to_vector(); })
           | to_vector();
  };
  const auto passedA = perLane([&](int lane, int row) { return packed([&](int t) { return a[row][(perWord * lane) + t]; }); });
  const auto passedB = perLane([&](int lane, int word) { return packed([&](int t) { return b[(perWord * word) + t][lane]; }); });
  const auto passedC = perLane([&](int lane, int row) {
    return element == Element::Int8 ? static_cast<uint32_t>(static_cast<int32_t>(c[row][lane]))
                                    : std::bit_cast<uint32_t>(static_cast<float>(c[row][lane]));
  });
  auto *aBuffer = session.buffer(passedA);
  auto *bBuffer = session.buffer(passedB);
  auto *cBuffer = session.buffer(passedC);
  auto *out = session.buffer(std::vector<uint32_t>(64, 0xdeadbeefU));
  args(kernel, {aBuffer, bBuffer, cBuffer, out});
  const Shape local{8, 1, 1};
  session.run(kernel, 1, {8, 1, 1}, &local);
  const auto results = session.read<uint32_t>(out, 64);
  for (int lane = 0; lane < 8; ++lane)
    for (int row = 0; row < 8; ++row) {
      const double sum = iota(0, inner) | fold_left(c[row][lane], [&](double acc, int k) { return acc + (a[row][k] * b[k][lane]); });
      INFO(fmt::format("lane {} row {}", lane, row));
      if (element == Element::Int8) REQUIRE(static_cast<int32_t>(results[(lane * 8) + row]) == static_cast<int32_t>(sum));
      else REQUIRE(std::bit_cast<float>(results[(lane * 8) + row]) == static_cast<float>(sum));
    }
}

// intel_sub_group_media_block_read_ui2 of a 4x4 block of 32-bit texels from texel column 1 runs the block's rows through
// the 16 values of the 8 lanes, a lane's second value 8 values after its first; the write of a 2x4 block takes one value
// from each lane, row-major
constexpr const char *MediaSource = R"cl(
  __attribute__((intel_reqd_sub_group_size(8)))
  __kernel void media(read_only image2d_t source, write_only image2d_t target, __global uint2 *out) {
    size_t lane = get_sub_group_local_id();
    out[lane] = intel_sub_group_media_block_read_ui2((int2)(4, 0), 4, 4, source);
    intel_sub_group_media_block_write_ui((int2)(0, 1), 2, 4, (uint)lane * 1000u + 7u, target);
  })cl";

void media() {
  Session session;
  session.require(Capability::Images);
  requireExtension(session, "cl_intel_media_block_io");
  auto *kernel = session.kernel(MediaSource, "", "media");
  constexpr size_t width = 8;
  constexpr size_t height = 6;
  auto texels = iota<size_t>(0, width * height) | map([&](size_t at) { return static_cast<uint32_t>(((at / width) * 100) + (at % width)); })
                | to_vector();
  cl_image_desc desc{};
  desc.image_type = CL_MEM_OBJECT_IMAGE2D;
  desc.image_width = width;
  desc.image_height = height;
  const cl_image_format format{.image_channel_order = CL_R, .image_channel_data_type = CL_UNSIGNED_INT32};
  auto *source = session.image(CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, format, desc, texels.data());
  std::vector<uint32_t> blank(width * height, 0);
  auto *target = session.image(CL_MEM_WRITE_ONLY | CL_MEM_COPY_HOST_PTR, format, desc, blank.data());
  auto *out = session.buffer(std::vector<uint32_t>(16, 0xdeadbeefU));
  arg(kernel, 0, source, "source");
  arg(kernel, 1, target, "target");
  arg(kernel, 2, out, "out");
  const Shape local{8, 1, 1};
  session.run(kernel, 1, {8, 1, 1}, &local);
  const auto values = session.read<uint32_t>(out, 16);
  for (size_t lane = 0; lane < 8; ++lane)
    for (size_t component = 0; component < 2; ++component) {
      const size_t at = (component * 8) + lane;
      const size_t row = at / 4;
      const size_t column = 1 + (at % 4);
      INFO(fmt::format("lane {} component {}", lane, component));
      REQUIRE(values[(lane * 2) + component] == (row * 100) + column);
    }
  std::vector<uint32_t> written(width * height);
  const size_t origin[] = {0, 0, 0};
  const size_t region[] = {width, height, 1};
  session.readImage(target, origin, region, written.data(), "read image");
  for (size_t row = 0; row < height; ++row)
    for (size_t column = 0; column < width; ++column) {
      const bool inside = row >= 1 && row < 5 && column < 2;
      INFO(fmt::format("texel {}, {}", column, row));
      REQUIRE(written[(row * width) + column] == (inside ? (((((row - 1) * 2) + column) * 1000) + 7) : 0));
    }
}

// cl_intel_bfloat16_conversions round to nearest even and widen exactly, through OpConvertFToBF16INTEL and
// OpConvertBF16ToFINTEL; cl_intel_split_work_group_barrier's arrival and wait order the local exchange between them
constexpr const char *ConversionSource = R"cl(
  __kernel void convert(__global const float *in, __global ushort *narrow, __global float *wide, __local float *shared) {
    size_t i = get_global_id(0), lid = get_local_id(0), size = get_local_size(0);
    narrow[i] = intel_convert_bfloat16_as_ushort(in[i]);
    shared[lid] = intel_convert_as_bfloat16_float(narrow[i]);
    intel_work_group_barrier_arrive(CLK_LOCAL_MEM_FENCE);
    intel_work_group_barrier_wait(CLK_LOCAL_MEM_FENCE);
    wide[i] = shared[(lid + 1) % size];
  })cl";

uint16_t nearestEven(float value) {
  const uint32_t bits = std::bit_cast<uint32_t>(value);
  const uint32_t rounded = bits + 0x7fff + ((bits >> 16) & 1);
  return static_cast<uint16_t>(rounded >> 16);
}

void conversions(size_t items) {
  Session session;
  requireExtension(session, "cl_intel_bfloat16_conversions");
  requireExtension(session, "cl_intel_split_work_group_barrier");
  auto *kernel = session.kernel(ConversionSource, "", "convert");
  const float edges[] = {1.0F, 3.3F, -2.5F, 1.00390625F, 1.01171875F, 65504.0F, 3.0e38F, -0.0F, 1.0e-39F, 0.0F};
  const auto in = iota<size_t>(0, items) | map([&](size_t i) {
                    return i < std::size(edges)
                               ? edges[i]
                               : std::ldexp(static_cast<float>((i * 2654435761U) % 4096) - 2048.0F, static_cast<int>(i % 40) - 20);
                  })
                  | to_vector();
  auto *input = session.buffer(in);
  auto *narrow = session.buffer(std::vector<uint16_t>(items, 0));
  auto *wide = session.buffer(std::vector<float>(items, -1.0F));
  arg(kernel, 0, input, "in");
  arg(kernel, 1, narrow, "narrow");
  arg(kernel, 2, wide, "wide");
  check(clSetKernelArg(kernel, 3, 32 * sizeof(float), nullptr), "shared");
  const Shape local{32, 1, 1};
  session.requireGroup(local);
  session.run(kernel, 1, {items, 1, 1}, &local);
  const auto narrowed = session.read<uint16_t>(narrow, items);
  const auto widened = session.read<float>(wide, items);
  for (size_t i = 0; i < items; ++i) {
    INFO(fmt::format("item {}: {}", i, in[i]));
    // denormal inputs may flush; the conversion of everything else rounds to nearest even
    if (std::fpclassify(in[i]) != FP_SUBNORMAL) REQUIRE(narrowed[i] == nearestEven(in[i]));
    const size_t neighbour = i - (i % 32) + (((i % 32) + 1) % 32);
    REQUIRE(std::bit_cast<uint32_t>(widened[i]) == static_cast<uint32_t>(narrowed[neighbour]) << 16);
  }
}

// cl_khr_kernel_clock: each scope's clock never runs backwards within the invocation, and the sub-group clock advances
constexpr const char *ClockSource = R"cl(
  __kernel void clocks(__global ulong *out) {
    size_t i = get_global_id(0);
  #if defined(__opencl_c_kernel_clock_scope_device)
    ulong device = clock_read_device();
    out[i * 6 + 0] = device;
    out[i * 6 + 1] = clock_read_device();
  #endif
  #if defined(__opencl_c_kernel_clock_scope_work_group)
    out[i * 6 + 2] = clock_read_work_group();
    out[i * 6 + 3] = clock_read_work_group();
  #endif
  #if defined(__opencl_c_kernel_clock_scope_sub_group)
    out[i * 6 + 4] = clock_read_sub_group();
    uint2 parts = clock_read_hilo_sub_group();
    out[i * 6 + 5] = ((ulong)parts.y << 32) | parts.x;
  #endif
  })cl";

void clocks(size_t items) {
  Session session;
  requireExtension(session, "cl_khr_kernel_clock");
  cl_bitfield capabilities = 0;
  check(clGetDeviceInfo(session.device, CL_DEVICE_KERNEL_CLOCK_CAPABILITIES_KHR, sizeof(capabilities), &capabilities, nullptr),
        "clock capabilities");
  auto *kernel = session.kernel(ClockSource, "", "clocks");
  auto *out = session.buffer(std::vector<uint64_t>(items * 6, 0));
  arg(kernel, 0, out, "out");
  const Shape local{32, 1, 1};
  session.requireGroup(local);
  session.run(kernel, 1, {items, 1, 1}, &local);
  const auto values = session.read<uint64_t>(out, items * 6);
  for (size_t i = 0; i < items; ++i) {
    const auto *row = values.data() + (i * 6);
    INFO(fmt::format("work-item {}: {} {} {} {} {} {}", i, row[0], row[1], row[2], row[3], row[4], row[5]));
    if (capabilities & CL_DEVICE_KERNEL_CLOCK_SCOPE_DEVICE_KHR) REQUIRE(row[1] >= row[0]);
    if (capabilities & CL_DEVICE_KERNEL_CLOCK_SCOPE_WORK_GROUP_KHR) REQUIRE(row[3] >= row[2]);
    if (capabilities & CL_DEVICE_KERNEL_CLOCK_SCOPE_SUB_GROUP_KHR) REQUIRE(row[5] > row[4]);
  }
}
} // namespace

TEST_CASE("edge-cases/runtime/opencl/intel/subgroup-matrix-multiply-accumulate/i8-k32", "[unified][runtime][compact]") {
  matrix(Element::Int8);
}

TEST_CASE("edge-cases/runtime/opencl/intel/subgroup-matrix-multiply-accumulate/f16-k16", "[unified][runtime][compact]") {
  matrix(Element::Half);
}

TEST_CASE("edge-cases/runtime/opencl/intel/subgroup-matrix-multiply-accumulate/bf16-k16", "[unified][runtime][compact]") {
  matrix(Element::BFloat16);
}

TEST_CASE("edge-cases/runtime/opencl/intel/media-block-io/r32ui", "[unified][runtime][compact]") { media(); }

TEST_CASE("edge-cases/runtime/opencl/intel/bfloat16-conversions/split-barrier/n256", "[unified][runtime][compact]") { conversions(256); }

TEST_CASE("edge-cases/runtime/opencl/kernel-clock/n64", "[unified][runtime][compact]") { clocks(64); }
