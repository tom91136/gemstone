#include <cstdint>
#include <mutex>
#include <vector>

#include "cuda/support.cuh"
#include <aspartame/all.hpp>
#include <catch2/catch_test_macros.hpp>
#include <cuda_runtime.h>

using namespace aspartame;

namespace {

struct Probe {
  std::mutex mutex;
  std::vector<int> order;
  const uint32_t *staged = nullptr;
  size_t count = 0;
  std::vector<uint32_t> snapshot;

  void record(int id) {
    std::lock_guard lock(mutex);
    order.push_back(id);
  }

  void capture(int id) {
    std::lock_guard lock(mutex);
    order.push_back(id);
    snapshot.assign(staged, staged + count);
  }
};

struct Step {
  Probe *probe;
  int id;
};

void CUDART_CB recordHost(void *userData) {
  const auto *step = static_cast<const Step *>(userData);
  step->probe->record(step->id);
}

void CUDART_CB captureHost(void *userData) {
  const auto *step = static_cast<const Step *>(userData);
  step->probe->capture(step->id);
}

void CUDART_CB recordStream(cudaStream_t, cudaError_t, void *userData) {
  const auto *step = static_cast<const Step *>(userData);
  step->probe->record(step->id);
}

__global__ void increment(uint32_t *data, size_t count) {
  const size_t i = static_cast<size_t>(blockIdx.x) * blockDim.x + threadIdx.x;
  if (i < count) data[i] += 1;
}

} // namespace

TEST_CASE("runtime/cuda/host-callbacks", "[unified][runtime][async]") {
  constexpr size_t count = 256;
  constexpr size_t bytes = count * sizeof(uint32_t);
  const auto values = iota(size_t{0}, count) | map([](size_t i) { return static_cast<uint32_t>(0x7000 + i); }) | to_vector();

  Probe probe;
  uint32_t *staging = nullptr;
  check(cudaMallocHost(reinterpret_cast<void **>(&staging), bytes), "pinned staging");
  probe.staged = staging;
  probe.count = count;
  Step steps[] = {Step{.probe = &probe, .id = 0}, Step{.probe = &probe, .id = 1}, Step{.probe = &probe, .id = 2},
                  Step{.probe = &probe, .id = 3}};

  uint32_t *device = nullptr;
  check(cudaMalloc(reinterpret_cast<void **>(&device), bytes), "allocation");
  cudaStream_t stream = nullptr;
  check(cudaStreamCreate(&stream), "stream");

  check(cudaMemcpyAsync(device, values.data(), bytes, cudaMemcpyHostToDevice, stream), "upload");
  check(cudaLaunchHostFunc(stream, recordHost, &steps[0]), "host function after upload");
  increment<<<static_cast<unsigned int>((count + 63) / 64), 64, 0, stream>>>(device, count);
  check(cudaGetLastError(), "increment launch");
  check(cudaStreamAddCallback(stream, recordStream, &steps[1], 0), "callback after kernel");
  check(cudaMemcpyAsync(staging, device, bytes, cudaMemcpyDeviceToHost, stream), "download");
  check(cudaLaunchHostFunc(stream, captureHost, &steps[2]), "host function after download");
  check(cudaStreamAddCallback(stream, recordStream, &steps[3], 0), "callback after capture");
  check(cudaStreamSynchronize(stream), "stream synchronize");

  REQUIRE(probe.order == std::vector<int>{0, 1, 2, 3});
  const auto expected = values | map([](uint32_t value) { return value + 1; }) | to_vector();
  expectEqual(probe.snapshot, expected, "host callback snapshot");

  check(cudaStreamDestroy(stream), "stream destroy");
  check(cudaFree(device), "free");
  check(cudaFreeHost(staging), "free pinned staging");
}
