#pragma once
#include "unified_suite/result.hpp"
#include <stdexcept>
#include <thrust/copy.h>
#include <thrust/device_vector.h>
#include <vector>
#if defined(__HIPCC__)
#include <hip/hip_runtime.h>
#else
#include <cuda_runtime.h>
#endif
namespace unified_suite::gpu_calls {
#if defined(__HIPCC__)
inline constexpr auto backend = "hip";
inline constexpr auto success = hipSuccess;
inline auto last_error() { return hipGetLastError(); }
inline auto error_string(hipError_t status) { return hipGetErrorString(status); }
#else
inline constexpr auto backend = "cuda";
inline constexpr auto success = cudaSuccess;
inline auto last_error() { return cudaGetLastError(); }
inline auto error_string(cudaError_t status) { return cudaGetErrorString(status); }
#endif
// Focused ports of GPUOcelot's direct/indirect function-call checks.
// Copyright (c) 2015, Computer Architecture and Systems Laboratory at Georgia Tech.
// SPDX-License-Identifier: BSD-3-Clause; see programs/cuda/LICENSE.gpuocelot.
__device__ __noinline__ float square(float value) { return value * value; }
__device__ __noinline__ float twice(float value) { return value * 2; }
__device__ __noinline__ float thrice(float value) { return value * 3; }
__device__ __noinline__ float four_times(float value) { return value * 4; }
__device__ __noinline__ float five_times(float value) { return value * 5; }

template <bool Indirect> __global__ void apply_device_function(float *values, std::size_t size) {
  const std::size_t index = threadIdx.x + static_cast<std::size_t>(blockIdx.x) * blockDim.x;
  if (index >= size)
    return;
  const float input = values[index + 1];
  if constexpr (Indirect) {
    float (*function)(float) = nullptr;
    switch (index & 3) {
    case 0:
      function = &twice;
      break;
    case 1:
      function = &thrice;
      break;
    case 2:
      function = &four_times;
      break;
    default:
      function = &five_times;
      break;
    }
    values[index + 1] = function(input);
  } else {
    values[index + 1] = square(input);
  }
}

template <bool Indirect> unified_suite::Case device_function_case() {
  return {std::string("edge-cases/runtime/") + backend +
              (Indirect ? "/indirect-function/f32/n257" : "/device-function/f32/n257"),
          [] {
            return unified_suite::capture([] {
              constexpr std::size_t size = 257;
              constexpr float canary = -12345.0f;
              std::vector<float> values(size + 2, canary);
              for (std::size_t index = 0; index < size; ++index)
                values[index + 1] = (static_cast<float>(index) - 128.0f) * 0.25f;
              thrust::device_vector<float> device(values);
              apply_device_function<Indirect><<<3, 128>>>(thrust::raw_pointer_cast(device.data()), size);
              if (const auto error = last_error(); error != success)
                throw std::runtime_error(std::string("device function launch: ") + error_string(error));
              thrust::copy(device.begin(), device.end(), values.begin());
              if (values.front() != canary || values.back() != canary)
                throw std::runtime_error("device function overwrote a boundary canary");
              for (std::size_t index = 0; index < size; ++index) {
                const float input = (static_cast<float>(index) - 128.0f) * 0.25f;
                const float expected = Indirect ? input * static_cast<float>(2 + (index & 3)) : input * input;
                if (values[index + 1] != expected)
                  throw std::runtime_error("device function result mismatch at lane " +
                                           std::to_string(index));
              }
            });
          }};
}

} // namespace unified_suite::gpu_calls
