#include <algorithm>
#include <cstdint>
#include <vector>

#include "cuda/support.cuh"
#include <aspartame/all.hpp>
#include <catch2/catch_test_macros.hpp>
#include <cuda_runtime.h>

using namespace aspartame;

TEST_CASE("resources/cuda/memcpy-3d", "[unified][resources]") {
  constexpr size_t width = 5;
  constexpr size_t height = 4;
  constexpr size_t depth = 3;
  auto volume =
      iota(size_t{0}, width * height * depth) | map([](size_t i) { return static_cast<uint32_t>(0x10000 + (i * 0x101)); }) | to_vector();
  const cudaChannelFormatDesc desc = cudaCreateChannelDesc<unsigned int>();
  cudaArray_t array = nullptr;
  check(cudaMalloc3DArray(&array, &desc, make_cudaExtent(width, height, depth), 0), "3D array");

  cudaMemcpy3DParms up{};
  up.srcPtr = make_cudaPitchedPtr(volume.data(), width * sizeof(uint32_t), width, height);
  up.dstArray = array;
  up.extent = make_cudaExtent(width, height, depth);
  up.kind = cudaMemcpyHostToDevice;
  check(cudaMemcpy3D(&up), "host to array");

  constexpr size_t pitch = 64;
  void *pitched = nullptr;
  check(cudaMalloc(&pitched, pitch * 2 * 2), "pitched allocation");
  cudaMemcpy3DParms box{};
  box.srcArray = array;
  box.srcPos = make_cudaPos(1, 1, 1);
  box.dstPtr = make_cudaPitchedPtr(pitched, pitch, 3 * sizeof(uint32_t), 2);
  box.extent = make_cudaExtent(3, 2, 2);
  box.kind = cudaMemcpyDeviceToDevice;
  check(cudaMemcpy3D(&box), "array to pitched memory");

  std::vector<uint32_t> boxed(size_t{3} * 2 * 2, 0xdeadbeef);
  cudaMemcpy3DParms down{};
  down.srcPtr = make_cudaPitchedPtr(pitched, pitch, 3 * sizeof(uint32_t), 2);
  down.dstPtr = make_cudaPitchedPtr(boxed.data(), 3 * sizeof(uint32_t), 3 * sizeof(uint32_t), 2);
  down.extent = make_cudaExtent(3 * sizeof(uint32_t), 2, 2);
  down.kind = cudaMemcpyDeviceToHost;
  cudaStream_t stream = nullptr;
  check(cudaStreamCreate(&stream), "stream");
  check(cudaMemcpy3DAsync(&down, stream), "pitched memory to host");
  check(cudaStreamSynchronize(stream), "stream synchronize");
  check(cudaStreamDestroy(stream), "stream destroy");
  std::vector<uint32_t> expected(size_t{3} * 2 * 2);
  for (size_t z = 0; z < 2; ++z)
    for (size_t y = 0; y < 2; ++y)
      for (size_t x = 0; x < 3; ++x)
        expected[(((z * 2) + y) * 3) + x] = volume[((((1 + z) * height) + 1 + y) * width) + 1 + x];
  expectEqual(boxed, expected, "3D box");

  std::vector<uint32_t> back(volume.size(), 0);
  cudaMemcpy3DParms whole{};
  whole.srcArray = array;
  whole.dstPtr = make_cudaPitchedPtr(back.data(), width * sizeof(uint32_t), width, height);
  whole.extent = make_cudaExtent(width, height, depth);
  whole.kind = cudaMemcpyDeviceToHost;
  check(cudaMemcpy3D(&whole), "array to host");
  expectEqual(back, volume, "3D round trip");
  check(cudaFree(pitched), "free");
  check(cudaFreeArray(array), "free array");
}

TEST_CASE("resources/cuda/array-copies", "[unified][resources]") {
  constexpr size_t width = 6;
  constexpr size_t height = 3;
  constexpr size_t row = width * sizeof(uint32_t);
  const auto texels = iota(size_t{0}, width * height) | map([](size_t i) { return static_cast<uint32_t>(0x2000 + (i * 3)); }) | to_vector();
  const cudaChannelFormatDesc desc = cudaCreateChannelDesc<unsigned int>();
  cudaArray_t array = nullptr;
  check(cudaMallocArray(&array, &desc, width, height), "array");

  std::vector<uint32_t> whole(texels.size(), 0);
#ifdef __GNUC__
  #pragma GCC diagnostic push
  #pragma GCC diagnostic ignored "-Wdeprecated-declarations"
#endif
  check(cudaMemcpyToArray(array, 0, 0, texels.data(), row * height, cudaMemcpyHostToDevice), "array in");
  check(cudaMemcpyFromArray(whole.data(), array, 0, 0, row * height, cudaMemcpyDeviceToHost), "array out");
#ifdef __GNUC__
  #pragma GCC diagnostic pop
#endif
  expectEqual(whole, texels, "array rows");

  constexpr size_t patchColumns = 3;
  constexpr size_t patchRows = 2;
  constexpr size_t patchX = 2;
  constexpr size_t patchY = 1;
  const auto patch =
      iota(size_t{0}, patchColumns * patchRows) | map([](size_t i) { return static_cast<uint32_t>(0x9000 + i); }) | to_vector();
  check(cudaMemcpy2DToArray(array, patchX * sizeof(uint32_t), patchY, patch.data(), patchColumns * sizeof(uint32_t),
                            patchColumns * sizeof(uint32_t), patchRows, cudaMemcpyHostToDevice),
        "2D array in");
  auto expected = texels;
  for (size_t y = 0; y < patchRows; ++y)
    for (size_t x = 0; x < patchColumns; ++x)
      expected[((patchY + y) * width) + patchX + x] = patch[(y * patchColumns) + x];

  std::vector<uint32_t> readBack(texels.size(), 0);
  check(cudaMemcpy2DFromArray(readBack.data(), row, array, 0, 0, row, height, cudaMemcpyDeviceToHost), "2D array out");
  expectEqual(readBack, expected, "2D array rows");
  check(cudaFreeArray(array), "free array");
}

TEST_CASE("resources/cuda/mipmapped-array", "[unified][resources]") {
  constexpr size_t width = 13;
  constexpr size_t height = 6;
  constexpr unsigned levels = 3;
  const cudaChannelFormatDesc desc = cudaCreateChannelDesc<unsigned int>();
  cudaMipmappedArray_t mipmapped = nullptr;
  const cudaError_t allocated = cudaMallocMipmappedArray(&mipmapped, &desc, make_cudaExtent(width, height, 0), levels, 0);
  if (allocated == cudaErrorNotSupported) {
    (void)cudaGetLastError();
    SKIP("the runtime has no mipmapped arrays");
  }
  check(allocated, "mipmapped array");

  const auto value = [](size_t level, size_t x, size_t y) { return static_cast<uint32_t>((level << 16) | (y << 8) | x); };
  for (unsigned level = 0; level < levels; ++level) {
    CAPTURE(level);
    const size_t levelWidth = std::max<size_t>(1, width >> level);
    const size_t levelHeight = std::max<size_t>(1, height >> level);
    cudaArray_t array = nullptr;
    check(cudaGetMipmappedArrayLevel(&array, mipmapped, level), "level");
    cudaChannelFormatDesc levelDesc{};
    cudaExtent extent{};
    unsigned flags = 0;
    check(cudaArrayGetInfo(&levelDesc, &extent, &flags, array), "level info");
    REQUIRE(extent.width == levelWidth);
    REQUIRE(extent.height == levelHeight);

    std::vector<uint32_t> texels(levelWidth * levelHeight);
    for (size_t y = 0; y < levelHeight; ++y)
      for (size_t x = 0; x < levelWidth; ++x)
        texels[(y * levelWidth) + x] = value(level, x, y);
    const size_t row = levelWidth * sizeof(uint32_t);
    check(cudaMemcpy2DToArray(array, 0, 0, texels.data(), row, row, levelHeight, cudaMemcpyHostToDevice), "level upload");
    std::vector<uint32_t> back(texels.size(), 0);
    check(cudaMemcpy2DFromArray(back.data(), row, array, 0, 0, row, levelHeight, cudaMemcpyDeviceToHost), "level download");
    expectEqual(back, texels, fmt::format("level {} round trip", level));
  }
  check(cudaFreeMipmappedArray(mipmapped), "free mipmapped array");
}

TEST_CASE("resources/cuda/pitched-copies", "[unified][resources]") {
  constexpr size_t columns = 7;
  constexpr size_t rows = 5;
  constexpr size_t width = columns * sizeof(uint32_t);
  const auto values =
      iota(size_t{0}, columns * rows) | map([](size_t i) { return static_cast<uint32_t>(0x3000 + (i * 0x11)); }) | to_vector();

  void *device = nullptr;
  size_t pitch = 0;
  check(cudaMallocPitch(&device, &pitch, width, rows), "pitched allocation");
  REQUIRE(pitch >= width);

  check(cudaMemset(device, 0, pitch * rows), "clear");
  check(cudaMemset2D(device, pitch, 0x5a, width, rows), "memset 2D");
  std::vector<uint8_t> bytes(pitch * rows);
  check(cudaMemcpy(bytes.data(), device, bytes.size(), cudaMemcpyDeviceToHost), "memset download");
  std::vector<uint8_t> expectedBytes(pitch * rows, 0);
  for (size_t r = 0; r < rows; ++r)
    for (size_t c = 0; c < width; ++c)
      expectedBytes[(r * pitch) + c] = 0x5a;
  expectEqual(bytes, expectedBytes, "memset 2D");

  check(cudaMemcpy2D(device, pitch, values.data(), width, width, rows, cudaMemcpyHostToDevice), "2D upload");
  std::vector<uint32_t> compact(values.size(), 0);
  check(cudaMemcpy2D(compact.data(), width, device, pitch, width, rows, cudaMemcpyDeviceToHost), "2D download");
  expectEqual(compact, values, "2D round trip");

  constexpr size_t hostPitch = 32;
  std::vector<uint8_t> padded(hostPitch * rows, 0xcc);
  cudaStream_t stream = nullptr;
  check(cudaStreamCreate(&stream), "stream");
  check(cudaMemcpy2DAsync(padded.data(), hostPitch, device, pitch, width, rows, cudaMemcpyDeviceToHost, stream), "2D async download");
  check(cudaStreamSynchronize(stream), "stream synchronize");
  check(cudaStreamDestroy(stream), "stream destroy");
  const auto *source = reinterpret_cast<const uint8_t *>(values.data());
  std::vector<uint8_t> expectedPadded(hostPitch * rows, 0xcc);
  for (size_t r = 0; r < rows; ++r)
    for (size_t c = 0; c < width; ++c)
      expectedPadded[(r * hostPitch) + c] = source[(r * width) + c];
  expectEqual(padded, expectedPadded, "2D async rows");
  check(cudaFree(device), "free");
}
