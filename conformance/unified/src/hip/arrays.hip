#include <algorithm>
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

// HIP arrays as the runtime copies them and kernels read them: 3D copies between arrays, pitched device memory and
// the host, copies of whole arrays, and mipmapped arrays whose levels are arrays of their own and whose texture objects
// load any level. ROCm copies only whole arrays with hipMemcpyToArray and hipMemcpyFromArray as CUDA documents them,
// and supports mipmapped arrays only on Windows.
namespace {

template <typename T> void expectEqual(const std::vector<T> &actual, const std::vector<T> &expected, std::string_view name) {
  if (actual.size() < expected.size())
    throw std::runtime_error(fmt::format("{}: {} results, expected {}", name, actual.size(), expected.size()));
  if (const auto at = expected ^ zip(actual) ^ index_where_maybe([](const T &want, const T &got) { return got != want; }))
    throw std::runtime_error(fmt::format("{} mismatch at {}: expected {:#x}, got {:#x}", name, *at, expected[*at], actual[*at]));
}

/** Whether the device has image instructions; CDNA3 and CDNA4 have none. */
bool imageSupport() {
  hipDeviceProp_t properties{};
  check(hipGetDeviceProperties(&properties, 0), "device properties");
  const std::string_view name(properties.gcnArchName);
  return !name.starts_with("gfx94") && !name.starts_with("gfx95");
}

} // namespace

using Int32x4 = int __attribute__((ext_vector_type(4)));
using Int32x8 = int __attribute__((ext_vector_type(8)));

__device__ Int32x4 imageLoad3D(int mask, int x, int y, int z, Int32x8 resource, int fail,
                               int policy) __asm("llvm.amdgcn.image.load.3d.v4i32.i32");
__device__ Int32x4 imageLoad2D(int mask, int x, int y, Int32x8 resource, int fail, int policy) __asm("llvm.amdgcn.image.load.2d.v4i32.i32");
__device__ Int32x4 imageLoadMip2D(int mask, int x, int y, int mip, Int32x8 resource, int fail,
                                  int policy) __asm("llvm.amdgcn.image.load.mip.2d.v4i32.i32");

namespace {

/** The descriptor a texture or surface object holds first. */
__device__ Int32x8 descriptor(const void *object) { return *reinterpret_cast<const Int32x8 *>(object); }

// image.load.3d of each texel of a volume through its surface object; image-less targets compile it empty
__global__ void readVolume(hipSurfaceObject_t surface, uint32_t *out, int width, int height, int depth) {
#if !__HIP_NO_IMAGE_SUPPORT
  const int i = static_cast<int>((blockIdx.x * blockDim.x) + threadIdx.x);
  if (i >= width * height * depth) return;
  out[i] = static_cast<uint32_t>(imageLoad3D(0x1, i % width, i / width % height, i / (width * height), descriptor(surface), 0, 0).x);
#endif
}

// image.load.2d of each texel of a plane through its surface object
__global__ void readPlane(hipSurfaceObject_t surface, uint32_t *out, int width, int height) {
#if !__HIP_NO_IMAGE_SUPPORT
  const int i = static_cast<int>((blockIdx.x * blockDim.x) + threadIdx.x);
  if (i >= width * height) return;
  out[i] = static_cast<uint32_t>(imageLoad2D(0x1, i % width, i / width, descriptor(surface), 0, 0).x);
#endif
}

// image.load.mip.2d of texel (x, y) of `level` through a texture object over a mipmapped array
__global__ void readLevels(hipTextureObject_t texture, const int *coordinates, uint32_t *out, int count) {
#if !__HIP_NO_IMAGE_SUPPORT
  const int i = static_cast<int>((blockIdx.x * blockDim.x) + threadIdx.x);
  if (i >= count) return;
  const int *at = coordinates + (static_cast<size_t>(i) * 3);
  out[i] = static_cast<uint32_t>(imageLoadMip2D(0x1, at[0], at[1], at[2], descriptor(texture), 0, 0).x);
#endif
}

hipSurfaceObject_t surfaceOf(hipArray_t array) {
  hipResourceDesc resource{};
  resource.resType = hipResourceTypeArray;
  resource.res.array.array = array;
  hipSurfaceObject_t surface{};
  check(hipCreateSurfaceObject(&surface, &resource), "surface object");
  return surface;
}

template <typename T> T *deviceCopy(const std::vector<T> &host) {
  T *data = nullptr;
  check(hipMalloc(reinterpret_cast<void **>(&data), host.size() * sizeof(T)), "allocation");
  check(hipMemcpy(data, host.data(), host.size() * sizeof(T), hipMemcpyHostToDevice), "upload");
  return data;
}

template <typename T> std::vector<T> hostCopy(const T *device, size_t count) {
  std::vector<T> host(count);
  check(hipMemcpy(host.data(), device, count * sizeof(T), hipMemcpyDeviceToHost), "download");
  return host;
}

} // namespace

TEST_CASE("resources/hip/memcpy-3d", "[unified][resources]") {
  if (!imageSupport()) SKIP("the device has no image instructions");
  constexpr int width = 5;
  constexpr int height = 4;
  constexpr int depth = 3;
  auto volume = iota(size_t{0}, static_cast<size_t>(width) * height * depth)
                | map([](size_t i) { return static_cast<uint32_t>(0x10000 + (i * 0x101)); }) | to_vector();
  const hipChannelFormatDesc desc = hipCreateChannelDesc<unsigned int>();
  hipArray_t array = nullptr;
  check(hipMalloc3DArray(&array, &desc, make_hipExtent(width, height, depth), 0), "3D array");

  // the whole volume from the host, in elements of the array
  hipMemcpy3DParms up{};
  up.srcPtr = make_hipPitchedPtr(volume.data(), width * sizeof(uint32_t), width, height);
  up.dstArray = array;
  up.extent = make_hipExtent(width, height, depth);
  up.kind = hipMemcpyHostToDevice;
  check(hipMemcpy3D(&up), "host to array");

  // a 3 by 2 by 2 box from (1, 1, 1) into pitched device memory of 64-byte rows
  constexpr size_t pitch = 64;
  void *pitched = nullptr;
  check(hipMalloc(&pitched, pitch * 2 * 2), "pitched allocation");
  hipMemcpy3DParms box{};
  box.srcArray = array;
  box.srcPos = make_hipPos(1, 1, 1);
  box.dstPtr = make_hipPitchedPtr(pitched, pitch, 3, 2);
  box.extent = make_hipExtent(3, 2, 2);
  box.kind = hipMemcpyDeviceToDevice;
  check(hipMemcpy3D(&box), "array to pitched memory");

  // the box to the host in bytes, on a stream
  std::vector<uint32_t> boxed(size_t{3} * 2 * 2, 0xdeadbeef);
  hipMemcpy3DParms down{};
  down.srcPtr = make_hipPitchedPtr(pitched, pitch, 3, 2);
  down.dstPtr = make_hipPitchedPtr(boxed.data(), 3 * sizeof(uint32_t), 3, 2);
  down.extent = make_hipExtent(3 * sizeof(uint32_t), 2, 2);
  down.kind = hipMemcpyDeviceToHost;
  hipStream_t stream = nullptr;
  check(hipStreamCreate(&stream), "stream");
  check(hipMemcpy3DAsync(&down, stream), "pitched memory to host");
  check(hipStreamSynchronize(stream), "stream synchronize");
  check(hipStreamDestroy(stream), "stream destroy");
  std::vector<uint32_t> expected(size_t{3} * 2 * 2);
  for (int z = 0; z < 2; ++z)
    for (int y = 0; y < 2; ++y)
      for (int x = 0; x < 3; ++x)
        expected[(((z * 2) + y) * 3) + x] = volume[((((1 + z) * height) + 1 + y) * width) + 1 + x];
  expectEqual(boxed, expected, "3D box");

  // kernels see the volume the copy wrote
  auto *const surface = surfaceOf(array);
  uint32_t *out = nullptr;
  check(hipMalloc(reinterpret_cast<void **>(&out), volume.size() * sizeof(uint32_t)), "output");
  readVolume<<<1, 64>>>(surface, out, width, height, depth);
  finish("volume read");
  expectEqual(hostCopy(out, volume.size()), volume, "volume texels");

  // and the whole volume back to the host
  std::vector<uint32_t> back(volume.size(), 0);
  hipMemcpy3DParms whole{};
  whole.srcArray = array;
  whole.dstPtr = make_hipPitchedPtr(back.data(), width * sizeof(uint32_t), width, height);
  whole.extent = make_hipExtent(width, height, depth);
  whole.kind = hipMemcpyDeviceToHost;
  check(hipMemcpy3D(&whole), "array to host");
  expectEqual(back, volume, "3D round trip");
  check(hipFree(out), "free");
  check(hipFree(pitched), "free");
  check(hipDestroySurfaceObject(surface), "destroy surface");
  check(hipFreeArray(array), "free array");
}

TEST_CASE("resources/hip/array-copies", "[unified][resources]") {
  if (!imageSupport()) SKIP("the device has no image instructions");
  constexpr int width = 6;
  constexpr int height = 3;
  auto texels = iota(size_t{0}, static_cast<size_t>(width) * height) | map([](size_t i) { return static_cast<uint32_t>(0x2000 + (i * 3)); })
                | to_vector();
  const hipChannelFormatDesc desc = hipCreateChannelDesc<unsigned int>();
  hipArray_t array = nullptr;
  check(hipMallocArray(&array, &desc, width, height, hipArraySurfaceLoadStore), "array");
  const std::vector<uint32_t> &expected = texels;
  std::vector<uint32_t> whole(texels.size(), 0);
  // the deprecated whole-array copies are the APIs under test
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
  check(hipMemcpyToArray(array, 0, 0, texels.data(), texels.size() * sizeof(uint32_t), hipMemcpyHostToDevice), "array in");
  check(hipMemcpyFromArray(whole.data(), array, 0, 0, whole.size() * sizeof(uint32_t), hipMemcpyDeviceToHost), "array out");
#pragma clang diagnostic pop
  expectEqual(whole, expected, "array rows");
  auto *const surface = surfaceOf(array);
  uint32_t *out = nullptr;
  check(hipMalloc(reinterpret_cast<void **>(&out), texels.size() * sizeof(uint32_t)), "output");
  readPlane<<<1, 32>>>(surface, out, width, height);
  finish("plane read");
  expectEqual(hostCopy(out, texels.size()), expected, "plane texels");
  check(hipFree(out), "free");
  check(hipDestroySurfaceObject(surface), "destroy surface");
  check(hipFreeArray(array), "free array");
}

TEST_CASE("resources/hip/mipmapped-array", "[unified][resources]") {
  if (!imageSupport()) SKIP("the device has no image instructions");
  constexpr int width = 13;
  constexpr int height = 6;
  constexpr int levels = 3;
  const hipChannelFormatDesc desc = hipCreateChannelDesc<unsigned int>();
  hipMipmappedArray_t mipmapped = nullptr;
  const hipError_t allocated = hipMallocMipmappedArray(&mipmapped, &desc, make_hipExtent(width, height, 0), levels, 0);
  if (allocated == hipErrorNotSupported) {
    (void)hipGetLastError();
    SKIP("the runtime has no mipmapped arrays");
  }
  check(allocated, "mipmapped array");
  const auto value = [](int level, int x, int y) { return static_cast<uint32_t>((level << 16) | (y << 8) | x); };
  std::vector<int> coordinates;
  std::vector<uint32_t> expected;
  for (int level = 0; level < levels; ++level) {
    const int levelWidth = std::max(1, width >> level);
    const int levelHeight = std::max(1, height >> level);
    hipArray_t array = nullptr;
    check(hipGetMipmappedArrayLevel(&array, mipmapped, static_cast<unsigned>(level)), "level");
    hipChannelFormatDesc levelDesc{};
    hipExtent extent{};
    unsigned flags = 0;
    check(hipArrayGetInfo(&levelDesc, &extent, &flags, array), "level info");
    REQUIRE((std::cmp_equal(extent.width, levelWidth) && std::cmp_equal(extent.height, levelHeight)));
    std::vector<uint32_t> texels(static_cast<size_t>(levelWidth) * levelHeight);
    for (int y = 0; y < levelHeight; ++y)
      for (int x = 0; x < levelWidth; ++x) {
        texels[(y * levelWidth) + x] = value(level, x, y);
        coordinates.insert(coordinates.end(), {x, y, level});
        expected.push_back(value(level, x, y));
      }
    const size_t row = levelWidth * sizeof(uint32_t);
    check(hipMemcpy2DToArray(array, 0, 0, texels.data(), row, row, levelHeight, hipMemcpyHostToDevice), "level upload");
    std::vector<uint32_t> back(texels.size(), 0);
    check(hipMemcpy2DFromArray(back.data(), row, array, 0, 0, row, levelHeight, hipMemcpyDeviceToHost), "level download");
    expectEqual(back, texels, fmt::format("level {} round trip", level));
    if (level == 1) {
      auto *const surface = surfaceOf(array);
      uint32_t *out = nullptr;
      check(hipMalloc(reinterpret_cast<void **>(&out), texels.size() * sizeof(uint32_t)), "output");
      readPlane<<<1, 32>>>(surface, out, levelWidth, levelHeight);
      finish("level read");
      expectEqual(hostCopy(out, texels.size()), texels, "level surface texels");
      check(hipFree(out), "free");
      check(hipDestroySurfaceObject(surface), "destroy surface");
    }
  }
  hipResourceDesc resource{};
  resource.resType = hipResourceTypeMipmappedArray;
  resource.res.mipmap.mipmap = mipmapped;
  hipTextureDesc textureDesc{};
  textureDesc.readMode = hipReadModeElementType;
  textureDesc.filterMode = hipFilterModePoint;
  textureDesc.maxMipmapLevelClamp = static_cast<float>(levels - 1);
  hipTextureObject_t texture{};
  check(hipCreateTextureObject(&texture, &resource, &textureDesc, nullptr), "texture object");
  const int count = static_cast<int>(expected.size());
  int *where = deviceCopy(coordinates);
  uint32_t *out = nullptr;
  check(hipMalloc(reinterpret_cast<void **>(&out), expected.size() * sizeof(uint32_t)), "output");
  readLevels<<<(count + 63) / 64, 64>>>(texture, where, out, count);
  finish("mip level reads");
  expectEqual(hostCopy(out, expected.size()), expected, "mip level texels");
  check(hipFree(out), "free");
  check(hipFree(where), "free");
  check(hipDestroyTextureObject(texture), "destroy texture");
  check(hipFreeMipmappedArray(mipmapped), "free mipmapped array");
}
