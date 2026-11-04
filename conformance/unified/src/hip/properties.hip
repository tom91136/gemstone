#include <climits>
#include <cstdint>
#include <string>

#include <catch2/catch_test_macros.hpp>
#include <hip/hip_runtime.h>

#include "support.hpp"
#include <fmt/format.h>

using namespace hip_support;

// The device properties and attributes agree with each other and with what the device does, and occupancy follows from them.
namespace {

int attribute(hipDeviceAttribute_t name) {
  int value = -1;
  check(hipDeviceGetAttribute(&value, name, 0), fmt::format("attribute {}", static_cast<int>(name)));
  return value;
}

hipDeviceProp_t properties() {
  hipDeviceProp_t value{};
  check(hipGetDeviceProperties(&value, 0), "device properties");
  return value;
}

void expect(bool holds, const std::string &what) {
  if (!holds) throw std::runtime_error(what);
}

__global__ void reportWave(int *out) { *out = warpSize; }

__global__ void plain(float *data) { data[(blockIdx.x * blockDim.x) + threadIdx.x] += 1.0F; }

__global__ void staged(float *data) {
  extern __shared__ float tile[];
  tile[threadIdx.x] = data[(blockIdx.x * blockDim.x) + threadIdx.x];
  __syncthreads();
  data[(blockIdx.x * blockDim.x) + threadIdx.x] = tile[threadIdx.x] + 1.0F;
}

} // namespace

TEST_CASE("runtime/hip/device-properties", "[unified][runtime]") {
  const auto prop = properties();
  const std::string_view arch(prop.gcnArchName);
  const auto same = [](int fromProperties, hipDeviceAttribute_t name, const char *label) {
    expect(fromProperties == attribute(name), fmt::format("{}: property {} attribute {}", label, fromProperties, attribute(name)));
  };
  same(prop.warpSize, hipDeviceAttributeWarpSize, "warpSize");
  same(prop.maxThreadsPerBlock, hipDeviceAttributeMaxThreadsPerBlock, "maxThreadsPerBlock");
  same(prop.maxThreadsPerMultiProcessor, hipDeviceAttributeMaxThreadsPerMultiProcessor, "maxThreadsPerMultiProcessor");
  same(prop.multiProcessorCount, hipDeviceAttributeMultiprocessorCount, "multiProcessorCount");
  same(prop.clockRate, hipDeviceAttributeClockRate, "clockRate");
  same(prop.regsPerBlock, hipDeviceAttributeMaxRegistersPerBlock, "regsPerBlock");
  same(prop.regsPerMultiprocessor, hipDeviceAttributeMaxRegistersPerMultiprocessor, "regsPerMultiprocessor");
  same(prop.maxGridSize[0], hipDeviceAttributeMaxGridDimX, "maxGridSize[0]");
  same(prop.maxGridSize[1], hipDeviceAttributeMaxGridDimY, "maxGridSize[1]");
  same(prop.maxGridSize[2], hipDeviceAttributeMaxGridDimZ, "maxGridSize[2]");
  same(static_cast<int>(prop.maxSharedMemoryPerMultiProcessor), hipDeviceAttributeMaxSharedMemoryPerMultiprocessor,
       "maxSharedMemoryPerMultiProcessor");

  expect(prop.warpSize == 32 || prop.warpSize == 64, "warp size is 32 or 64");
  expect(prop.maxThreadsPerBlock == 1024, "1024 threads a block");
  expect(prop.maxThreadsPerMultiProcessor >= prop.maxThreadsPerBlock, "a multiprocessor holds a block");
  expect(prop.multiProcessorCount > 0, "multiprocessors");
  expect(prop.clockRate > 0, "clock rate");
  expect(attribute(hipDeviceAttributeWallClockRate) > 0, "wall clock rate");
  expect(prop.regsPerBlock > 0 && prop.regsPerMultiprocessor >= prop.regsPerBlock, "registers");
  expect(prop.sharedMemPerBlock >= size_t{32} * 1024 && prop.maxSharedMemoryPerMultiProcessor >= prop.sharedMemPerBlock, "LDS");
  expect(prop.maxGridSize[0] == INT_MAX && prop.maxGridSize[1] == 65536 && prop.maxGridSize[2] == 65536, "grid limits");
  expect(prop.maxThreadsDim[0] == 1024 && prop.maxThreadsDim[1] == 1024 && prop.maxThreadsDim[2] == 1024, "block limits");
  const bool images = !arch.starts_with("gfx94") && !arch.starts_with("gfx95");
  expect(attribute(hipDeviceAttributeImageSupport) == (images ? 1 : 0), "image support follows the architecture");

  int *wave = nullptr;
  check(hipMalloc(reinterpret_cast<void **>(&wave), sizeof(int)), "allocation");
  reportWave<<<1, 1>>>(wave);
  finish("wave size");
  int observed = 0;
  check(hipMemcpy(&observed, wave, sizeof(int), hipMemcpyDeviceToHost), "download");
  check(hipFree(wave), "free");
  expect(observed == prop.warpSize, fmt::format("kernel warpSize {} property {}", observed, prop.warpSize));
}

TEST_CASE("runtime/hip/occupancy", "[unified][runtime]") {
  const auto prop = properties();
  const auto resident = [&](const void *kernel, int block, size_t shared) {
    int blocks = -1;
    check(hipOccupancyMaxActiveBlocksPerMultiprocessor(&blocks, kernel, block, shared), "active blocks");
    return blocks;
  };
  const auto kernel = [](auto function) { return reinterpret_cast<const void *>(function); };
  for (const size_t shared : {size_t{0}, static_cast<size_t>(prop.sharedMemPerBlock) / 4}) {
    const void *function = shared == 0 ? kernel(plain) : kernel(staged);
    int minGrid = 0;
    int block = 0;
    check(hipOccupancyMaxPotentialBlockSize(&minGrid, &block, function, shared, 0), "potential block size");
    const std::string label = fmt::format("shared {}: block {} minGrid {}", shared, block, minGrid);
    expect(block >= 1 && block <= prop.maxThreadsPerBlock, label + ": block size within the limit");
    const int blocks = resident(function, block, shared);
    expect(blocks >= 1, label + ": a block fits");
    expect(minGrid == blocks * prop.multiProcessorCount, label + ": grid is blocks per multiprocessor times multiprocessors");
    expect(blocks * block <= prop.maxThreadsPerMultiProcessor, label + ": threads fit the multiprocessor");
    expect(static_cast<size_t>(blocks) * shared <= prop.maxSharedMemoryPerMultiProcessor, label + ": LDS fits the multiprocessor");
    expect(resident(function, 1, shared) >= blocks, label + ": smaller blocks never hold fewer blocks");
    const int whole = resident(function, prop.maxThreadsPerBlock, shared);
    expect(whole >= 1 && whole * prop.maxThreadsPerBlock <= prop.maxThreadsPerMultiProcessor, label + ": largest block");
  }
  int minGrid = 0;
  int block = 0;
  check(hipOccupancyMaxPotentialBlockSize(&minGrid, &block, kernel(plain), 0, prop.warpSize), "limited block size");
  expect(block >= 1 && block <= prop.warpSize, "the block size limit applies");
}
