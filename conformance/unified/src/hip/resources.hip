#include <array>
#include <cmath>
#include <cstdint>
#include <cstring>
#include <stdexcept>
#include <string>
#include <string_view>
#include <vector>

#include <aspartame/all.hpp>
#include <catch2/catch_test_macros.hpp>
#include <hip/hip_runtime.h>

#include "support.hpp"
#include <fmt/format.h>

using namespace aspartame;
using namespace hip_support;

// Buffer and image resources as HIP kernels reach them: the raw buffer builtins, LLVM's buffer, typed buffer, scalar
// buffer and image intrinsics bound by assembler label as Composable Kernel binds them, texture fetches from linear
// memory, and surface objects over HIP arrays.
namespace {

template <typename T> void expectEqual(const std::vector<T> &actual, const std::vector<T> &expected, std::string_view name) {
  if (actual.size() < expected.size())
    throw std::runtime_error(fmt::format("{}: {} results, expected {}", name, actual.size(), expected.size()));
  const auto bits = [](const T &value) {
    uint64_t result = 0;
    std::memcpy(&result, &value, sizeof(T));
    return result;
  };
  if (const auto at = expected ^ zip(actual) ^ index_where_maybe([&](const T &want, const T &got) { return bits(got) != bits(want); }))
    throw std::runtime_error(
        fmt::format("{} mismatch at {}: expected {:#x}, got {:#x}", name, *at, bits(expected[*at]), bits(actual[*at])));
}

} // namespace

using Int32x4 = int __attribute__((ext_vector_type(4)));
using Int32x8 = int __attribute__((ext_vector_type(8)));
using Float32x4 = float __attribute__((ext_vector_type(4)));

__device__ int structLoad(Int32x4 resource, int index, int offset, int scalarOffset, int aux) __asm("llvm.amdgcn.struct.buffer.load.i32");
__device__ void structStore(int value, Int32x4 resource, int index, int offset, int scalarOffset,
                            int aux) __asm("llvm.amdgcn.struct.buffer.store.i32");
__device__ int structAtomicAdd(int value, Int32x4 resource, int index, int offset, int scalarOffset,
                               int aux) __asm("llvm.amdgcn.struct.buffer.atomic.add.i32");
__device__ Float32x4 structLoadFormat(Int32x4 resource, int index, int offset, int scalarOffset,
                                      int aux) __asm("llvm.amdgcn.struct.buffer.load.format.v4f32");
__device__ void structStoreFormat(Float32x4 value, Int32x4 resource, int index, int offset, int scalarOffset,
                                  int aux) __asm("llvm.amdgcn.struct.buffer.store.format.v4f32");
__device__ Int32x4 rawTypedLoad(Int32x4 resource, int offset, int scalarOffset, int format,
                                int aux) __asm("llvm.amdgcn.raw.tbuffer.load.v4i32");
__device__ int scalarBufferLoad(Int32x4 resource, int offset, int aux) __asm("llvm.amdgcn.s.buffer.load.i32");
__device__ int imageAtomicAdd(int value, int x, int y, Int32x8 resource, int fail,
                              int policy) __asm("llvm.amdgcn.image.atomic.add.2d.i32.i32");
__device__ Int32x4 imageResourceInfo(int mask, int level, Int32x8 resource, int fail,
                                     int policy) __asm("llvm.amdgcn.image.getresinfo.2d.v4i32.i32");

namespace {

constexpr uint32_t Identity = 4U | (5U << 3) | (6U << 6) | (7U << 9);

/** dword 3 of a buffer descriptor of 32-bit unsigned elements: raw bounds, or by index when `indexed`. */
__device__ uint32_t elementFlags(bool indexed) {
#if defined(__GFX8__) || defined(__GFX9__)
  (void)indexed;
  return Identity | (4U << 12) | (4U << 15);
#elifdef __GFX10__
  return Identity | (20U << 12) | (1U << 24) | ((indexed ? 1U : 3U) << 28);
#else
  return Identity | (20U << 12) | ((indexed ? 1U : 3U) << 28);
#endif
}

/** dword 3 of a buffer descriptor of 8_8_8_8_UNORM texels checked by index. */
__device__ uint32_t unormFlags() {
#if defined(__GFX8__) || defined(__GFX9__)
  return Identity | (0U << 12) | (10U << 15);
#elifdef __GFX10__
  return Identity | (56U << 12) | (1U << 24) | (1U << 28);
#else
  return Identity | (42U << 12) | (1U << 28);
#endif
}

/** The typed-instruction format of 32_32_32_32_UINT: DFMT and NFMT before GFX10, the unified format after. */
constexpr int VectorFormat =
#if defined(__GFX8__) || defined(__GFX9__)
    14 | (4 << 4);
#elifdef __GFX10__
    75;
#else
    61;
#endif

/** A descriptor of `records` records of `stride` bytes from `base`; GFX8 counts unswizzled records in bytes. */
__device__ Int32x4 descriptor(const void *base, uint32_t stride, uint32_t records, uint32_t flags) {
  const auto address = reinterpret_cast<uint64_t>(base);
#ifdef __GFX8__
  if (stride != 0) records *= stride;
#endif
  return Int32x4{static_cast<int>(address), static_cast<int>((address >> 32) | (stride << 16)), static_cast<int>(records),
                 static_cast<int>(flags)};
}

// raw.buffer.load.b32 and store.b32 through a raw descriptor of `bytes` bytes; past it, loads read zero and stores
// are dropped
__global__ void rawBuffer(uint32_t *in, uint32_t *out, uint32_t *written, uint32_t bytes) {
  const uint32_t i = threadIdx.x;
#if defined(__GFX8__) || defined(__GFX9__)
  const int flags = static_cast<int>(Identity | (4U << 12) | (4U << 15));
#elifdef __GFX10__
  const int flags = static_cast<int>(Identity | (20U << 12) | (1U << 24) | (3U << 28));
#else
  const int flags = static_cast<int>(Identity | (20U << 12) | (3U << 28));
#endif
  const auto source = __builtin_amdgcn_make_buffer_rsrc(in, 0, static_cast<int>(bytes), flags);
  const auto target = __builtin_amdgcn_make_buffer_rsrc(written, 0, static_cast<int>(bytes), flags);
  out[i] = __builtin_amdgcn_raw_buffer_load_b32(source, static_cast<int>(i * 4), 0, 0);
  __builtin_amdgcn_raw_buffer_store_b32(i + 1000, target, static_cast<int>(i * 4), 0, 0);
}

} // namespace

TEST_CASE("resources/hip/raw-buffer", "[unified][resources]") {
  constexpr uint32_t lanes = 64;
  constexpr uint32_t bytes = 37 * 4;
  const auto input = iota(0U, lanes) | map([](uint32_t i) { return 0x10000 + (i * 3); }) | to_vector();
  DeviceBuffer<uint32_t> in(input);
  DeviceBuffer<uint32_t> out(std::vector<uint32_t>(lanes, 0xdeadbeef));
  DeviceBuffer<uint32_t> written(std::vector<uint32_t>(lanes, 7));
  rawBuffer<<<1, lanes>>>(in.data, out.data, written.data, bytes);
  finish("raw buffer");
  const auto loaded = iota(0U, lanes) | map([&](uint32_t i) { return (i * 4) + 4 <= bytes ? input[i] : 0; }) | to_vector();
  const auto stored = iota(0U, lanes) | map([&](uint32_t i) { return (i * 4) + 4 <= bytes ? i + 1000 : 7; }) | to_vector();
  expectEqual(out.download(), loaded, "raw buffer loads");
  expectEqual(written.download(), stored, "raw buffer stores");
}

namespace {
// struct.buffer.load, store and atomic.add by index over 16-byte records, offset 4 into each; indices past the
// records read zero, drop stores and skip atomics
__global__ void structuredBuffer(uint32_t *records, uint32_t *out, uint32_t count) {
  const uint32_t i = threadIdx.x;
  const Int32x4 resource = descriptor(records, 16, count, elementFlags(true));
  const int index = static_cast<int>(i % (count + 4));
  uint32_t *o = out + (static_cast<size_t>(i) * 3);
  o[0] = static_cast<uint32_t>(structLoad(resource, index, 4, 0, 0));
  o[1] = static_cast<uint32_t>(structAtomicAdd(1, resource, index, 8, 0, 0));
  if (i < count + 4) structStore(static_cast<int>(i + 500), resource, index, 12, 0, 0);
  o[2] = 0;
}

} // namespace

TEST_CASE("resources/hip/structured-buffer", "[unified][resources]") {
  constexpr uint32_t lanes = 64;
  constexpr uint32_t count = 9;
  constexpr uint32_t stride = 4;
  const auto input = iota(uint32_t{0}, (count + 4) * stride) | map([](uint32_t i) { return 0x2000 + i; }) | to_vector();
  DeviceBuffer<uint32_t> records(input);
  DeviceBuffer<uint32_t> out(std::vector<uint32_t>(static_cast<size_t>(lanes) * 3, 0xdeadbeef));
  structuredBuffer<<<1, lanes>>>(records.data, out.data, count);
  finish("structured buffer");
  std::vector<uint32_t> expected = input;
  std::vector<uint32_t> loaded(static_cast<size_t>(lanes) * 3);
  for (uint32_t i = 0; i < lanes; ++i) {
    const uint32_t index = i % (count + 4);
    const size_t at = static_cast<size_t>(i) * 3;
    loaded[at] = index < count ? input[(index * stride) + 1] : 0;
    loaded[at + 2] = 0;
    if (index < count) {
      expected[(index * stride) + 2] += 1;
      if (i < count + 4) expected[(index * stride) + 3] = i + 500;
    }
  }
  const auto actual = out.download();
  const auto memory = records.download();
  expectEqual(memory, expected, "structured buffer stores and atomics");
  // each in-range atomic returned a distinct earlier value of its counter
  for (uint32_t i = 0; i < lanes; ++i) {
    const uint32_t index = i % (count + 4);
    const size_t at = static_cast<size_t>(i) * 3;
    if (actual[at] != loaded[at])
      throw std::runtime_error(fmt::format("structured load {}: expected {:#x}, got {:#x}", i, loaded[at], actual[at]));
    const uint32_t returned = actual[at + 1];
    const bool valid = index < count ? returned >= input[(index * stride) + 2] && returned < expected[(index * stride) + 2] : returned == 0;
    if (!valid) throw std::runtime_error(fmt::format("structured atomic {} returned {:#x}", i, returned));
  }
}

namespace {
// struct.buffer.load.format and store.format over 8_8_8_8_UNORM texels, and raw.tbuffer.load of
// 32_32_32_32_UINT named by the instruction
__global__ void formattedBuffer(const uint32_t *texels, uint32_t *copies, float *out, uint32_t *typed, uint32_t count) {
  const uint32_t i = threadIdx.x;
  const Float32x4 value = structLoadFormat(descriptor(texels, 4, count, unormFlags()), static_cast<int>(i), 0, 0, 0);
  float *o = out + (static_cast<size_t>(i) * 4);
  o[0] = value.x;
  o[1] = value.y;
  o[2] = value.z;
  o[3] = value.w;
  structStoreFormat(Float32x4{value.w, value.z, value.y, value.x}, descriptor(copies, 4, count, unormFlags()), static_cast<int>(i), 0, 0,
                    0);
  const Int32x4 words =
      rawTypedLoad(descriptor(texels, 0, (blockDim.x + 3) * 4, elementFlags(false)), static_cast<int>(i * 4), 0, VectorFormat, 0);
  uint32_t *t = typed + (static_cast<size_t>(i) * 4);
  t[0] = static_cast<uint32_t>(words.x);
  t[1] = static_cast<uint32_t>(words.y);
  t[2] = static_cast<uint32_t>(words.z);
  t[3] = static_cast<uint32_t>(words.w);
}

} // namespace

TEST_CASE("resources/hip/format-buffer", "[unified][resources]") {
  constexpr uint32_t lanes = 64;
  constexpr uint32_t count = 50;
  const auto input = iota(0U, lanes + 3) | map([](uint32_t i) { return (i * 0x01030507U) + 0x00ff7f80U; }) | to_vector();
  DeviceBuffer<uint32_t> texels(input);
  DeviceBuffer<uint32_t> copies(std::vector<uint32_t>(lanes, 0x55555555));
  DeviceBuffer<float> out(std::vector<float>(static_cast<size_t>(lanes) * 4, -1.0F));
  DeviceBuffer<uint32_t> typed(std::vector<uint32_t>(static_cast<size_t>(lanes) * 4, 0xdeadbeef));
  formattedBuffer<<<1, lanes>>>(texels.data, copies.data, out.data, typed.data, count);
  finish("format buffer");
  const auto floats =
      iota(0U, lanes * 4)
      | map([&](uint32_t j) { return j / 4 < count ? static_cast<float>((input[j / 4] >> (8 * (j % 4))) & 0xffU) / 255.0F : 0.0F; })
      | to_vector();
  const auto words = iota(0U, lanes * 4) | map([&](uint32_t j) { return input[(j / 4) + (j % 4)]; }) | to_vector();
  // the store reverses the channels it read
  const auto copied =
      iota(0U, lanes) | map([&](uint32_t i) {
        const uint32_t texel = input[i];
        return i < count ? (texel >> 24) | ((texel >> 8) & 0xff00U) | ((texel << 8) & 0xff0000U) | (texel << 24) : 0x55555555U;
      })
      | to_vector();
  expectEqual(out.download(), floats, "format loads");
  expectEqual(copies.download(), copied, "format stores");
  expectEqual(typed.download(), words, "typed loads");
}

namespace {
// s.buffer.load at uniform offsets of a 20-byte buffer; dwords past it read zero
__global__ void scalarBuffer(const uint32_t *in, uint32_t *out) {
  const Int32x4 resource = descriptor(in, 0, 20, elementFlags(false));
  for (int i = 0; i < 8; ++i)
    out[i] = static_cast<uint32_t>(scalarBufferLoad(resource, i * 4, 0));
}

} // namespace

TEST_CASE("resources/hip/scalar-buffer", "[unified][resources]") {
  const auto input = iota(0U, 8U) | map([](uint32_t i) { return 0x3000 + i; }) | to_vector();
  DeviceBuffer<uint32_t> in(input);
  DeviceBuffer<uint32_t> out(std::vector<uint32_t>(8, 0xdeadbeef));
  scalarBuffer<<<1, 1>>>(in.data, out.data);
  finish("scalar buffer");
  expectEqual(out.download(), std::vector<uint32_t>{0x3000, 0x3001, 0x3002, 0x3003, 0x3004, 0, 0, 0}, "scalar buffer loads");
}

namespace {
/** Whether the device has image instructions; CDNA3 and CDNA4 have none. */
bool imageSupport() {
  hipDeviceProp_t properties{};
  check(hipGetDeviceProperties(&properties, 0), "device properties");
  const std::string_view name(properties.gcnArchName);
  return !name.starts_with("gfx94") && !name.starts_with("gfx95");
}

// tex1Dfetch from linear memory: a typed load indexed by element. Image-less targets compile the image kernels
// empty.
__global__ void fetchFloats([[maybe_unused]] hipTextureObject_t texture, [[maybe_unused]] float *out) {
#if !__HIP_NO_IMAGE_SUPPORT
  out[threadIdx.x] = tex1Dfetch<float>(texture, static_cast<int>(threadIdx.x));
#endif
}
__global__ void fetchBytes([[maybe_unused]] hipTextureObject_t texture, [[maybe_unused]] float4 *out) {
#if !__HIP_NO_IMAGE_SUPPORT
  out[threadIdx.x] = tex1Dfetch<float4>(texture, static_cast<int>(threadIdx.x));
#endif
}
__global__ void fetchInts([[maybe_unused]] hipTextureObject_t texture, [[maybe_unused]] int2 *out) {
#if !__HIP_NO_IMAGE_SUPPORT
  out[threadIdx.x] = tex1Dfetch<int2>(texture, static_cast<int>(threadIdx.x));
#endif
}

template <typename T> hipTextureObject_t linearTexture(const DeviceBuffer<T> &buffer, hipChannelFormatDesc desc, hipTextureReadMode mode) {
  hipResourceDesc resource{};
  resource.resType = hipResourceTypeLinear;
  resource.res.linear.devPtr = buffer.data;
  resource.res.linear.desc = desc;
  resource.res.linear.sizeInBytes = buffer.size * sizeof(T);
  hipTextureDesc texture{};
  texture.readMode = mode;
  hipTextureObject_t object{};
  check(hipCreateTextureObject(&object, &resource, &texture, nullptr), "texture object");
  return object;
}

} // namespace

TEST_CASE("resources/hip/texture-fetch", "[unified][resources]") {
  if (!imageSupport()) SKIP("the device has no image instructions");
  constexpr uint32_t lanes = 48;
  const auto floats = iota(0U, lanes) | map([](uint32_t i) { return (static_cast<float>(i) * 0.75F) - 9.0F; }) | to_vector();
  const auto bytes = iota(0U, lanes) | map([](uint32_t i) { return (i * 0x04030201U) + 0x10203040U; }) | to_vector();
  const auto ints = iota(0U, lanes)
                    | flat_map([](uint32_t i) { return std::array<int, 2>{static_cast<int>(i) * -7, static_cast<int>(i * i)}; })
                    | to_vector();
  DeviceBuffer<float> floatData(floats);
  DeviceBuffer<float> floatOut{std::vector<float>(lanes)};
  DeviceBuffer<uint32_t> byteData(bytes);
  DeviceBuffer<float4> byteOut{std::vector<float4>(lanes)};
  DeviceBuffer<int> intData(ints);
  DeviceBuffer<int2> intOut{std::vector<int2>(lanes)};
  auto *const floatTexture = linearTexture(floatData, hipCreateChannelDesc<float>(), hipReadModeElementType);
  auto *const byteTexture = linearTexture(byteData, hipCreateChannelDesc<uchar4>(), hipReadModeNormalizedFloat);
  auto *const intTexture = linearTexture(intData, hipCreateChannelDesc<int2>(), hipReadModeElementType);
  fetchFloats<<<1, lanes>>>(floatTexture, floatOut.data);
  fetchBytes<<<1, lanes>>>(byteTexture, byteOut.data);
  fetchInts<<<1, lanes>>>(intTexture, intOut.data);
  finish("texture fetch");
  expectEqual(floatOut.download(), floats, "tex1Dfetch float");
  const auto normalised = iota(0U, lanes * 4)
                          | map([&](uint32_t j) { return static_cast<float>((bytes[j / 4] >> (8 * (j % 4))) & 0xffU) / 255.0F; })
                          | to_vector();
  const auto fetched = byteOut.download();
  std::vector<float> flattened(static_cast<size_t>(lanes) * 4);
  std::memcpy(flattened.data(), fetched.data(), flattened.size() * sizeof(float));
  expectEqual(flattened, normalised, "tex1Dfetch normalised uchar4");
  const auto pairs = intOut.download();
  std::vector<int> flatPairs(static_cast<size_t>(lanes) * 2);
  std::memcpy(flatPairs.data(), pairs.data(), flatPairs.size() * sizeof(int));
  expectEqual(flatPairs, ints, "tex1Dfetch int2");
  check(hipDestroyTextureObject(floatTexture), "destroy texture");
  check(hipDestroyTextureObject(byteTexture), "destroy texture");
  check(hipDestroyTextureObject(intTexture), "destroy texture");
}

namespace {
// surf2Dread and surf2Dwrite: each texel is read, transformed and written to a second surface
__global__ void transformBytes([[maybe_unused]] hipSurfaceObject_t source, [[maybe_unused]] hipSurfaceObject_t target, int width,
                               int height) {
  const int x = static_cast<int>((blockIdx.x * blockDim.x) + threadIdx.x);
  const int y = static_cast<int>(blockIdx.y);
  if (x >= width || y >= height) return;
#if !__HIP_NO_IMAGE_SUPPORT
  uchar4 value;
  surf2Dread(&value, source, x * static_cast<int>(sizeof(uchar4)), y);
  surf2Dwrite(make_uchar4(value.w, static_cast<unsigned char>(value.z + x), static_cast<unsigned char>(value.y ^ y), value.x), target,
              x * static_cast<int>(sizeof(uchar4)), y);
#endif
}

__global__ void transformFloats([[maybe_unused]] hipSurfaceObject_t source, [[maybe_unused]] hipSurfaceObject_t target, int width) {
  const int x = static_cast<int>(threadIdx.x);
  if (x >= width) return;
#if !__HIP_NO_IMAGE_SUPPORT
  float value = NAN;
  surf1Dread(&value, source, x * static_cast<int>(sizeof(float)));
  surf1Dwrite((value * 2.0F) + 1.0F, target, x * static_cast<int>(sizeof(float)));
#endif
}

struct Array {
  hipArray_t array = nullptr;
  hipSurfaceObject_t surface = nullptr;
  Array(hipChannelFormatDesc desc, size_t width, size_t height) {
    check(hipMallocArray(&array, &desc, width, height, hipArraySurfaceLoadStore), "array");
    hipResourceDesc resource{};
    resource.resType = hipResourceTypeArray;
    resource.res.array.array = array;
    check(hipCreateSurfaceObject(&surface, &resource), "surface object");
  }
  Array(const Array &) = delete;
  Array &operator=(const Array &) = delete;
  ~Array() {
    (void)hipDestroySurfaceObject(surface);
    (void)hipFreeArray(array);
  }
};

} // namespace

TEST_CASE("resources/hip/surface", "[unified][resources]") {
  if (!imageSupport()) SKIP("the device has no image instructions");
  constexpr int width = 37;
  constexpr int height = 5;
  const auto texels = iota(size_t{0}, static_cast<size_t>(width) * height)
                      | map([](size_t i) { return static_cast<uint32_t>((i * 0x01020305U) + 0x80402010U); }) | to_vector();
  Array source(hipCreateChannelDesc<uchar4>(), width, height);
  Array target(hipCreateChannelDesc<uchar4>(), width, height);
  check(hipMemcpy2DToArray(source.array, 0, 0, texels.data(), width * sizeof(uint32_t), width * sizeof(uint32_t), height,
                           hipMemcpyHostToDevice),
        "array upload");
  transformBytes<<<dim3(2, height), 32>>>(source.surface, target.surface, width, height);
  finish("surface bytes");
  std::vector<uint32_t> result(static_cast<size_t>(width) * height);
  check(hipMemcpy2DFromArray(result.data(), width * sizeof(uint32_t), target.array, 0, 0, width * sizeof(uint32_t), height,
                             hipMemcpyDeviceToHost),
        "array download");
  const auto expected = iota(0, width * height) | map([&](int i) {
                          const int x = i % width;
                          const int y = i / width;
                          const uint32_t v = texels[i];
                          const uint32_t r = v & 0xff;
                          const uint32_t g = (v >> 8) & 0xff;
                          const uint32_t b = (v >> 16) & 0xff;
                          const uint32_t a = v >> 24;
                          return a | (((b + x) & 0xffU) << 8) | ((g ^ static_cast<uint32_t>(y)) << 16) | (r << 24);
                        })
                        | to_vector();
  expectEqual(result, expected, "surf2D read and write");

  constexpr int length = 50;
  const auto values = iota(0, length) | map([](int i) { return (static_cast<float>(i) * 0.5F) - 3.0F; }) | to_vector();
  Array line(hipCreateChannelDesc<float>(), length, 0);
  Array doubled(hipCreateChannelDesc<float>(), length, 0);
  check(hipMemcpy2DToArray(line.array, 0, 0, values.data(), length * sizeof(float), length * sizeof(float), 1, hipMemcpyHostToDevice),
        "line upload");
  transformFloats<<<1, 64>>>(line.surface, doubled.surface, length);
  finish("surface floats");
  std::vector<float> lineResult(length);
  check(hipMemcpy2DFromArray(lineResult.data(), length * sizeof(float), doubled.array, 0, 0, length * sizeof(float), 1,
                             hipMemcpyDeviceToHost),
        "line download");
  const auto lineExpected = values ^ map([](float value) { return (value * 2.0F) + 1.0F; });
  expectEqual(lineResult, lineExpected, "surf1D read and write");
}

namespace {
// image.atomic.add and image.getresinfo on the descriptor a surface object holds
__global__ void imageAtomics([[maybe_unused]] hipSurfaceObject_t surface, [[maybe_unused]] int *returned, [[maybe_unused]] int *info,
                             [[maybe_unused]] int width, [[maybe_unused]] int height) {
#if !__HIP_NO_IMAGE_SUPPORT
  const Int32x8 resource = *reinterpret_cast<const Int32x8 *>(surface);
  const int x = static_cast<int>(threadIdx.x) % width;
  const int y = static_cast<int>(threadIdx.x) / width % height;
  returned[threadIdx.x] = imageAtomicAdd(static_cast<int>(threadIdx.x) + 1, x, y, resource, 0, 0);
  if (threadIdx.x == 0) {
    const Int32x4 dimensions = imageResourceInfo(0x3, 0, resource, 0, 0);
    info[0] = dimensions.x;
    info[1] = dimensions.y;
  }
#endif
}

} // namespace

TEST_CASE("resources/hip/image-atomic", "[unified][resources]") {
  if (!imageSupport()) SKIP("the device has no image instructions");
  constexpr int width = 6;
  constexpr int height = 3;
  constexpr int lanes = 64;
  const auto initial = iota(0, width * height) | map([](int i) { return static_cast<uint32_t>(i * 100); }) | to_vector();
  Array image(hipCreateChannelDesc<unsigned int>(), width, height);
  check(hipMemcpy2DToArray(image.array, 0, 0, initial.data(), width * sizeof(uint32_t), width * sizeof(uint32_t), height,
                           hipMemcpyHostToDevice),
        "image upload");
  DeviceBuffer<int> returned(std::vector<int>(lanes, -1));
  DeviceBuffer<int> info(std::vector<int>(2, -1));
  imageAtomics<<<1, lanes>>>(image.surface, returned.data, info.data, width, height);
  finish("image atomics");
  std::vector<uint32_t> result(static_cast<size_t>(width) * height);
  std::vector<uint32_t> expected = initial;
  for (int lane = 0; lane < lanes; ++lane)
    expected[lane % (width * height)] += static_cast<uint32_t>(lane + 1);
  check(hipMemcpy2DFromArray(result.data(), width * sizeof(uint32_t), image.array, 0, 0, width * sizeof(uint32_t), height,
                             hipMemcpyDeviceToHost),
        "image download");
  expectEqual(result, expected, "image atomic sums");
  expectEqual(info.download(), std::vector<int>{width, height}, "image resource info");
  const auto old = returned.download();
  for (int lane = 0; lane < lanes; ++lane) {
    const uint32_t value = static_cast<uint32_t>(old[lane]);
    const uint32_t texel = static_cast<uint32_t>(lane % (width * height));
    if (value < initial[texel] || value >= expected[texel] || (value - initial[texel]) > static_cast<uint32_t>(lanes * lanes))
      throw std::runtime_error(fmt::format("image atomic {} returned {:#x}", lane, value));
  }
}
