/*
 * Adapted by the Gemstone project from NVIDIA Thrust's saxpy.cu and sum.cu
 * examples at tag 1.15.0. This file has been modified into a deterministic,
 * self-checking conformance test.
 * SPDX-License-Identifier: Apache-2.0
 */
#pragma region case: thrust-transform-reduce
#pragma region vendored: thrust,cub
#pragma region do: {nvcc} --allow-unsupported-compiler -std=c++17 -O2 -include {cuda_host_compat} {cuda_vendor_includes} -MD -MF {dependencies} -o {output} {input}
#pragma region do: {output}
#pragma region requires: pass

#include <cub/version.cuh>
#include <thrust/device_vector.h>
#include <thrust/functional.h>
#include <thrust/host_vector.h>
#include <thrust/reduce.h>
#include <thrust/transform.h>
#include <thrust/version.h>

#include <cstddef>
#include <cstdio>

static_assert(THRUST_VERSION == 101500, "the conformance corpus pins Thrust 1.15.0");
static_assert(CUB_VERSION == 101500, "the conformance corpus pins CUB 1.15.0");

struct saxpy {
  int multiplier;

  __host__ __device__ int operator()(int x, int y) const { return multiplier * x + y; }
};

int main() {
  constexpr std::size_t size = 4097;
  thrust::host_vector<int> x(size);
  thrust::host_vector<int> y(size);
  long long expected_sum = 0;

  for (std::size_t i = 0; i < size; ++i) {
    x[i] = static_cast<int>(i % 31) - 15;
    y[i] = static_cast<int>(i % 17) - 8;
    expected_sum += 7LL * x[i] + y[i];
  }

  thrust::device_vector<int> device_x = x;
  thrust::device_vector<int> device_y = y;
  thrust::transform(device_x.begin(), device_x.end(), device_y.begin(), device_y.begin(), saxpy{7});

  const long long actual_sum =
      thrust::reduce(device_y.begin(), device_y.end(), 0LL, thrust::plus<long long>());
  thrust::host_vector<int> actual = device_y;

  if (actual_sum != expected_sum) {
    std::fprintf(stderr, "sum: expected %lld, got %lld\n", expected_sum, actual_sum);
    return 1;
  }
  for (std::size_t i = 0; i < size; ++i) {
    const int expected = 7 * x[i] + y[i];
    if (actual[i] != expected) {
      std::fprintf(stderr, "element %zu: expected %d, got %d\n", i, expected, actual[i]);
      return 1;
    }
  }

  std::puts("pass");
  return 0;
}
