#include "test_support.hpp"
#include <fmt/format.h>

#define UNIFIED_API_THRUST_DEVICE_FUNCTIONS 1
#include "backend_thrust.hpp"
#undef UNIFIED_API_THRUST_DEVICE_FUNCTIONS

#include <cmath>
#include <cstddef>
#include <cstdint>
#include <numeric>
#include <stdexcept>
#include <string>
#include <string_view>
#include <vector>

#include <catch2/catch_template_test_macros.hpp>
#include <catch2/catch_test_macros.hpp>
#include <catch2/generators/catch_generators_adapters.hpp>
#include <catch2/generators/catch_generators_range.hpp>
#include <cuda_runtime_api.h>

namespace cuda_regressions {

void check(cudaError_t error, std::string_view operation) {
  if (error != cudaSuccess) throw std::runtime_error(std::string(operation) + ": " + cudaGetErrorString(error));
}

// CloverLeaf's CUDA field summary uses this exact reduction shape. Keep the
// probe independent of CloverLeaf so it remains a small regression when the
// upstream source is refreshed.
template <typename T, uint32_t Offset> struct CloverStyleReduce {
  __device__ static void run(T *values, T *output, T (*combine)(T, T)) {
    if (Offset > 16) __syncthreads();
    if (threadIdx.x < Offset) {
      values[threadIdx.x] = combine(values[threadIdx.x], values[threadIdx.x + Offset]);
      CloverStyleReduce<T, Offset / 2>::run(values, output, combine);
    }
  }
};

template <typename T> struct CloverStyleReduce<T, 0> {
  __device__ static void run(T *values, T *output, T (*)(T, T)) { output[blockIdx.x] = values[0]; }
};

__device__ double cloverStyleAdd(double left, double right) { return left + right; }

template <uint32_t BlockSize> __global__ void cloverDivergentBarrier(double *output) {
  __shared__ double values[BlockSize];
  __shared__ double weights[BlockSize];
  const uint32_t thread = threadIdx.x;
  values[thread] = 1.0;
  weights[thread] = 2.0;
  if (thread < BlockSize / 2) {
    __syncthreads();
    values[thread] += values[thread + BlockSize / 2];
    weights[thread] += weights[thread + BlockSize / 2];
  } else {
    __syncthreads();
  }
  __syncthreads();
  if (thread == 0) output[0] = values[0] + weights[0];
}

TEST_CASE("regressions/miniapp/cloverleaf/cuda-divergent-barrier-id", "[unified][regression]") {
  constexpr uint32_t blockSize = 256;

  unified::test_runtime::Cleanup cleanup;
  double *output = nullptr;
  check(cudaMalloc(reinterpret_cast<void **>(&output), sizeof(double)), "Clover divergent barrier allocation");
  cleanup.add([=] { cudaFree(output); });
  cloverDivergentBarrier<blockSize><<<1, blockSize>>>(output);
  check(cudaGetLastError(), "Clover divergent barrier launch");
  check(cudaDeviceSynchronize(), "Clover divergent barrier synchronize");
  double actual = 0.0;
  check(cudaMemcpy(&actual, output, sizeof(actual), cudaMemcpyDeviceToHost), "Clover divergent barrier download");
  if (actual != 6.0) throw std::runtime_error(fmt::format("Clover divergent barrier mismatch: expected 6, got {}", actual));
}

template <uint32_t BlockSize> __global__ void cloverStyleReduction(const double *input, double *output, size_t size) {
  __shared__ double values[BlockSize];
  values[threadIdx.x] = 0.0;
  for (size_t index = static_cast<size_t>(blockIdx.x) * blockDim.x + threadIdx.x; index < size;
       index += static_cast<size_t>(gridDim.x) * blockDim.x)
    values[threadIdx.x] += input[index];
  CloverStyleReduce<double, BlockSize / 2>::run(values, output, cloverStyleAdd);
}

TEST_CASE("regressions/miniapp/cloverleaf/cuda-shared-reduction", "[unified][regression]") {
  constexpr uint32_t blockSize = 256;
  constexpr uint32_t blockCount = 256;
  constexpr size_t size = 1089;
  std::vector<double> hostInput(size);
  std::iota(hostInput.begin(), hostInput.end(), 1.0);
  const double expected = std::accumulate(hostInput.begin(), hostInput.end(), 0.0);

  unified::test_runtime::Cleanup cleanup;
  double *input = nullptr;
  double *output = nullptr;
  check(cudaMalloc(reinterpret_cast<void **>(&input), hostInput.size() * sizeof(double)), "Clover-style reduction input allocation");
  cleanup.add([=] { cudaFree(input); });
  check(cudaMalloc(reinterpret_cast<void **>(&output), blockCount * sizeof(double)), "Clover-style reduction output allocation");
  cleanup.add([=] { cudaFree(output); });
  check(cudaMemcpy(input, hostInput.data(), hostInput.size() * sizeof(double), cudaMemcpyHostToDevice),
        "Clover-style reduction input upload");
  cloverStyleReduction<blockSize><<<blockCount, blockSize>>>(input, output, hostInput.size());
  check(cudaGetLastError(), "Clover-style reduction launch");
  check(cudaDeviceSynchronize(), "Clover-style reduction synchronize");

  std::vector<double> partials(blockCount);
  check(cudaMemcpy(partials.data(), output, partials.size() * sizeof(double), cudaMemcpyDeviceToHost), "Clover-style reduction download");
  const double actual = std::accumulate(partials.begin(), partials.end(), 0.0);
  if (!std::isfinite(actual) || std::abs(actual - expected) > 1.0e-10 * expected)
    throw std::runtime_error(fmt::format("Clover-style reduction mismatch: expected {}, got {}", expected, actual));
}

} // namespace cuda_regressions

TEMPLATE_TEST_CASE_SIG("runtime/cuda/device-function", "[unified][runtime][compact]", ((bool Indirect), Indirect), false, true) {
  unified::gpu_calls::deviceFunction<Indirect>();
}

// 2^31/2^32 still exercise the 32-bit index boundary; 2^33 would cost the
// emulator hours of wall time for no extra coverage.

TEST_CASE("regressions/cuda-thrust/for-each-with-big-indexes", "[unified][regression]") {
  const auto magnitude = GENERATE(30U, 31U, 32U);
  CAPTURE(magnitude);
  unified::backend::ThrustEdgeCases backend;
  backend.forEachBigIndexes(magnitude);
}

TEST_CASE("regressions/cuda-thrust/adjacent-difference-with-big-indexes", "[unified][regression]") {
  const auto magnitude = GENERATE(30U, 31U, 32U);
  CAPTURE(magnitude);
  unified::backend::ThrustEdgeCases backend;
  backend.adjacentDifferenceBigIndexes(magnitude);
}
