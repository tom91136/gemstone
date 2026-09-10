#include "../provider.hpp"

#include "unified_suite/adapters/cublas.hpp"
#include "unified_suite/adapters/thrust_edge_cases.hpp"
#include "unified_suite/edge_cases.hpp"

#include "unified_suite/device_function_cases.hpp"
#include <cmath>
#include <cuda_runtime_api.h>
#include <numeric>
#include <stdexcept>
#include <string>
#include <vector>

namespace {

// CloverLeaf's CUDA field summary uses this exact reduction shape. Keep the
// probe independent of CloverLeaf so it remains a small unified-suite
// regression even when the upstream source is refreshed.
template <typename T, int offset> struct CloverStyleReduce {
  __device__ static void run(T *values, T *output, T (*combine)(T, T)) {
    if (offset > 16)
      __syncthreads();
    if (threadIdx.x < offset) {
      values[threadIdx.x] = combine(values[threadIdx.x], values[threadIdx.x + offset]);
      CloverStyleReduce<T, offset / 2>::run(values, output, combine);
    }
  }
};

template <typename T> struct CloverStyleReduce<T, 0> {
  __device__ static void run(T *values, T *output, T (*)(T, T)) { output[blockIdx.x] = values[0]; }
};

__device__ double clover_style_add(double left, double right) { return left + right; }

template <unsigned BlockSize> __global__ void clover_divergent_barrier(double *output) {
  __shared__ double values[BlockSize];
  __shared__ double weights[BlockSize];
  const unsigned thread = threadIdx.x;
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
  if (thread == 0)
    output[0] = values[0] + weights[0];
}

unified_suite::Case clover_divergent_barrier_case() {
  constexpr unsigned block_size = 256;
  return {"regressions/miniapp/cloverleaf/cuda-divergent-barrier-id",
          [] {
            return unified_suite::capture([&] {
              double *output = nullptr;
              if (const auto error = cudaMalloc(reinterpret_cast<void **>(&output), sizeof(double));
                  error != cudaSuccess)
                throw std::runtime_error(std::string("Clover divergent barrier allocation: ") +
                                         cudaGetErrorString(error));
              clover_divergent_barrier<block_size><<<1, block_size>>>(output);
              if (const auto error = cudaGetLastError(); error != cudaSuccess) {
                cudaFree(output);
                throw std::runtime_error(std::string("Clover divergent barrier launch: ") +
                                         cudaGetErrorString(error));
              }
              if (const auto error = cudaDeviceSynchronize(); error != cudaSuccess) {
                cudaFree(output);
                throw std::runtime_error(std::string("Clover divergent barrier synchronize: ") +
                                         cudaGetErrorString(error));
              }
              double actual = 0.0;
              if (const auto error = cudaMemcpy(&actual, output, sizeof(actual), cudaMemcpyDeviceToHost);
                  error != cudaSuccess) {
                cudaFree(output);
                throw std::runtime_error(std::string("Clover divergent barrier download: ") +
                                         cudaGetErrorString(error));
              }
              cudaFree(output);
              if (actual != 6.0)
                throw std::runtime_error("Clover divergent barrier mismatch: expected 6, got " +
                                         std::to_string(actual));
            });
          },
          true};
}

template <unsigned BlockSize>
__global__ void clover_style_reduction(const double *input, double *output, std::size_t size) {
  __shared__ double values[BlockSize];
  values[threadIdx.x] = 0.0;
  for (std::size_t index = static_cast<std::size_t>(blockIdx.x) * blockDim.x + threadIdx.x; index < size;
       index += static_cast<std::size_t>(gridDim.x) * blockDim.x)
    values[threadIdx.x] += input[index];
  CloverStyleReduce<double, BlockSize / 2>::run(values, output, clover_style_add);
}

unified_suite::Case clover_style_reduction_case() {
  constexpr unsigned block_size = 256;
  constexpr unsigned block_count = 256;
  constexpr std::size_t size = 1089;
  std::vector<double> host_input(size);
  std::iota(host_input.begin(), host_input.end(), 1.0);
  const double expected = std::accumulate(host_input.begin(), host_input.end(), 0.0);

  return {
      "regressions/miniapp/cloverleaf/cuda-shared-reduction",
      [host_input, expected] {
        return unified_suite::capture([&] {
          double *input = nullptr;
          double *output = nullptr;
          if (const auto error =
                  cudaMalloc(reinterpret_cast<void **>(&input), host_input.size() * sizeof(double));
              error != cudaSuccess)
            throw std::runtime_error(std::string("Clover-style reduction input allocation: ") +
                                     cudaGetErrorString(error));
          if (const auto error = cudaMalloc(reinterpret_cast<void **>(&output), block_count * sizeof(double));
              error != cudaSuccess) {
            cudaFree(input);
            throw std::runtime_error(std::string("Clover-style reduction output allocation: ") +
                                     cudaGetErrorString(error));
          }
          if (const auto error = cudaMemcpy(input, host_input.data(), host_input.size() * sizeof(double),
                                            cudaMemcpyHostToDevice);
              error != cudaSuccess) {
            cudaFree(output);
            cudaFree(input);
            throw std::runtime_error(std::string("Clover-style reduction input upload: ") +
                                     cudaGetErrorString(error));
          }
          clover_style_reduction<block_size><<<block_count, block_size>>>(input, output, host_input.size());
          if (const auto error = cudaGetLastError(); error != cudaSuccess) {
            cudaFree(output);
            cudaFree(input);
            throw std::runtime_error(std::string("Clover-style reduction launch: ") +
                                     cudaGetErrorString(error));
          }
          if (const auto error = cudaDeviceSynchronize(); error != cudaSuccess) {
            cudaFree(output);
            cudaFree(input);
            throw std::runtime_error(std::string("Clover-style reduction synchronize: ") +
                                     cudaGetErrorString(error));
          }

          std::vector<double> partials(block_count);
          if (const auto error = cudaMemcpy(partials.data(), output, partials.size() * sizeof(double),
                                            cudaMemcpyDeviceToHost);
              error != cudaSuccess) {
            cudaFree(output);
            cudaFree(input);
            throw std::runtime_error(std::string("Clover-style reduction download: ") +
                                     cudaGetErrorString(error));
          }
          cudaFree(output);
          cudaFree(input);
          const double actual = std::accumulate(partials.begin(), partials.end(), 0.0);
          if (!std::isfinite(actual) || std::abs(actual - expected) > 1.0e-10 * expected)
            throw std::runtime_error("Clover-style reduction mismatch: expected " + std::to_string(expected) +
                                     ", got " + std::to_string(actual));
        });
      },
      true};
}

} // namespace

namespace unified_suite::program {

std::vector<Case> edge_case_cases(const Configuration &configuration) {
  auto result = edge_cases::cases<adapters::ThrustEdgeCases, adapters::Cublas>(configuration);
  result.push_back(gpu_calls::device_function_case<false>());
  result.push_back(gpu_calls::device_function_case<true>());
  if (configuration.include_regressions) {
    result.push_back(clover_divergent_barrier_case());
    result.push_back(clover_style_reduction_case());
    for (const unsigned magnitude : {30U, 31U, 32U, 33U}) {
      result.push_back(
          {"regressions/cuda-thrust/for-each-with-big-indexes/m" + std::to_string(magnitude), [magnitude] {
             return capture([&] {
               adapters::ThrustEdgeCases backend;
               backend.for_each_big_indexes(magnitude);
             });
           }});
      result.push_back(
          {"regressions/cuda-thrust/adjacent-difference-with-big-indexes/m" + std::to_string(magnitude),
           [magnitude] {
             return capture([&] {
               adapters::ThrustEdgeCases backend;
               backend.adjacent_difference_big_indexes(magnitude);
             });
           }});
    }
  }
  return result;
}

} // namespace unified_suite::program
