#include <algorithm>
#include <cstdint>
#include <stdexcept>
#include <string>
#include <string_view>
#include <vector>

#include <aspartame/all.hpp>
#include <catch2/catch_test_macros.hpp>
#include <hip/hip_cooperative_groups.h>
#include <hip/hip_runtime.h>

#include "support.hpp"
#include <fmt/format.h>

using namespace aspartame;
using namespace hip_support;

// Cooperative launches: grid.sync() is ds_gws_barrier on gfx9 and gfx10 and a spin on the runtime's multigrid sync
// record on gfx11 and gfx12, so every block must be resident at once.
namespace {

namespace cg = cooperative_groups;

constexpr uint32_t Rounds = 4;
constexpr uint32_t BlockSize = 128;

// Each round every work-item counts its arrival, waits for the grid and then mixes in an element another block wrote
// in the previous round; `seen` records the arrival count each work-item observes after every grid.sync().
__global__ void rounds(uint32_t *values, uint32_t *scratch, uint32_t *arrivals, uint32_t *seen, uint32_t *shape) {
  cg::grid_group grid = cg::this_grid();
  const uint32_t rank = static_cast<uint32_t>(grid.thread_rank());
  const uint32_t size = static_cast<uint32_t>(grid.size());
  if (rank == 0) {
    shape[0] = size;
    shape[1] = grid.is_valid() ? 1 : 0;
  }
  if (rank != (blockIdx.x * blockDim.x) + threadIdx.x) shape[2] = 1;
  uint32_t *from = values;
  uint32_t *to = scratch;
  for (uint32_t round = 0; round < Rounds; ++round) {
    atomicAdd(arrivals, 1);
    to[rank] = (from[rank] * 3) + from[(rank + BlockSize + round) % size] + round;
    grid.sync();
    seen[(round * size) + rank] = __hip_atomic_load(arrivals, __ATOMIC_RELAXED, __HIP_MEMORY_SCOPE_AGENT);
    grid.sync();
    uint32_t *const written = to;
    to = from;
    from = written;
  }
}

} // namespace

TEST_CASE("cooperative/hip/grid-sync", "[unified][cooperative]") {
  int device = 0;
  int supported = 0;
  int units = 0;
  int resident = 0;
  check(hipGetDevice(&device), "device");
  check(hipDeviceGetAttribute(&supported, hipDeviceAttributeCooperativeLaunch, device), "cooperative launch attribute");
  if (!supported) SKIP("the device has no cooperative launches");
  check(hipDeviceGetAttribute(&units, hipDeviceAttributeMultiprocessorCount, device), "multiprocessor count");
  check(hipOccupancyMaxActiveBlocksPerMultiprocessor(&resident, rounds, BlockSize, 0), "occupancy");
  const uint32_t capacity = static_cast<uint32_t>(resident) * static_cast<uint32_t>(units);
  REQUIRE(capacity >= 1);

  for (const uint32_t blocks : {1U, std::min(capacity, 3U), std::min(capacity, 8U)}) {
    const uint32_t size = blocks * BlockSize;
    const auto input = iota(0U, size) | map([](uint32_t i) { return (i * 2654435761U) + 7; }) | to_vector();
    DeviceBuffer<uint32_t> values(input);
    DeviceBuffer<uint32_t> scratch(std::vector<uint32_t>(size, 0));
    DeviceBuffer<uint32_t> arrivals(std::vector<uint32_t>(1, 0));
    DeviceBuffer<uint32_t> seen(std::vector<uint32_t>(static_cast<size_t>(Rounds) * size, 0));
    DeviceBuffer<uint32_t> shape(std::vector<uint32_t>(3, 0));
    void *arguments[] = {&values.data, &scratch.data, &arrivals.data, &seen.data, &shape.data};
    check(hipLaunchCooperativeKernel(reinterpret_cast<const void *>(rounds), dim3(blocks), dim3(BlockSize), arguments, 0, nullptr),
          "cooperative launch");
    check(hipDeviceSynchronize(), "synchronize");

    std::vector<uint32_t> from = input;
    std::vector<uint32_t> to(size);
    for (uint32_t round = 0; round < Rounds; ++round) {
      for (uint32_t rank = 0; rank < size; ++rank)
        to[rank] = (from[rank] * 3) + from[(rank + BlockSize + round) % size] + round;
      std::swap(from, to);
    }
    // Rounds is even, so the last round wrote values
    const auto result = values.download();
    if (const auto at = result ^ zip(from) ^ index_where_maybe([](uint32_t got, uint32_t want) { return got != want; }))
      FAIL(fmt::format("{} blocks: element {} is {:#x}, expected {:#x}", blocks, *at, result[*at], from[*at]));
    const auto observed = seen.download();
    for (uint32_t round = 0; round < Rounds; ++round)
      for (uint32_t rank = 0; rank < size; ++rank)
        if (observed[(round * size) + rank] != (round + 1) * size)
          FAIL(fmt::format("{} blocks: work-item {} saw {} arrivals after round {}, expected {}", blocks, rank,
                           observed[(round * size) + rank], round, (round + 1) * size));
    REQUIRE(arrivals.download()[0] == Rounds * size);
    REQUIRE(shape.download() == std::vector<uint32_t>{size, 1, 0});
  }
}

TEST_CASE("cooperative/hip/launch-too-large", "[unified][cooperative]") {
  int device = 0;
  int supported = 0;
  int units = 0;
  int resident = 0;
  check(hipGetDevice(&device), "device");
  check(hipDeviceGetAttribute(&supported, hipDeviceAttributeCooperativeLaunch, device), "cooperative launch attribute");
  if (!supported) SKIP("the device has no cooperative launches");
  check(hipDeviceGetAttribute(&units, hipDeviceAttributeMultiprocessorCount, device), "multiprocessor count");
  check(hipOccupancyMaxActiveBlocksPerMultiprocessor(&resident, rounds, BlockSize, 0), "occupancy");
  const uint32_t blocks = (static_cast<uint32_t>(resident) * static_cast<uint32_t>(units)) + 1;
  void *arguments[5]{};
  REQUIRE(hipLaunchCooperativeKernel(reinterpret_cast<const void *>(rounds), dim3(blocks), dim3(BlockSize), arguments, 0, nullptr)
          == hipErrorCooperativeLaunchTooLarge);
  (void)hipGetLastError();
}
