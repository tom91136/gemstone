/*
 * Adapted from GPUOcelot's ocelot/src/cuda/test/driver/sequence.cpp.
 * Copyright (c) 2015, Computer Architecture and Systems Laboratory at Georgia Tech.
 * SPDX-License-Identifier: BSD-3-Clause
 */
#pragma region case: driver-memory
#pragma region do: {nvcc} --allow-unsupported-compiler -std=c++17 -O2 {cuda_driver_defines} -x cu -o {output} {input} {cuda_driver_link}
#pragma region do: {output}
#pragma region requires: pass

#include <array>
#include <cstddef>
#include <cstdint>
#include <cstdio>

#include <cuda.h>

#define CUDA_REQUIRE(expression)                                                                                                           \
  do {                                                                                                                                     \
    const CUresult status = (expression);                                                                                                  \
    if (status != CUDA_SUCCESS) {                                                                                                          \
      std::fprintf(stderr, "%s failed with CUDA error %d at line %d\n", #expression, static_cast<int>(status), __LINE__);                  \
      return 1;                                                                                                                            \
    }                                                                                                                                      \
  } while (false)

int main() {
  CUDA_REQUIRE(cuInit(0));

  int count = 0;
  CUDA_REQUIRE(cuDeviceGetCount(&count));
  if (count < 1) {
    std::fprintf(stderr, "CUDA reference reports no devices\n");
    return 1;
  }

  CUdevice device{};
  CUcontext context{};
  CUDA_REQUIRE(cuDeviceGet(&device, 0));
#if CUDA_VERSION >= 13000
  CUDA_REQUIRE(cuCtxCreate(&context, nullptr, 0, device));
#else
  CUDA_REQUIRE(cuCtxCreate(&context, 0, device));
#endif

  constexpr std::size_t size = 1024;
  std::array<std::uint8_t, size> source{};
  std::array<std::uint8_t, size> result{};
  for (std::size_t i = 0; i < size; ++i)
    source[i] = static_cast<std::uint8_t>((i * 37 + 11) & 0xff);

  CUdeviceptr first{};
  CUdeviceptr second{};
  CUDA_REQUIRE(cuMemAlloc_v2(&first, size));
  CUDA_REQUIRE(cuMemAlloc_v2(&second, size));
  CUDA_REQUIRE(cuMemcpyHtoD_v2(first, source.data(), size));
  CUDA_REQUIRE(cuMemsetD8(first + 257, 0xa5, 193));
  // CUDA runtimes may dispatch directional copies through the generic entry
  // points after resolving them with cuGetProcAddress. Exercise both spellings
  // here so a provider cannot silently leave the generic ABI as a weak stub.
  CUDA_REQUIRE(cuMemcpy(second, first, size));
  CUDA_REQUIRE(cuMemcpyAsync(second, first, size, nullptr));
  CUDA_REQUIRE(cuMemcpyDtoH_v2(result.data(), second, size));

  for (std::size_t i = 0; i < size; ++i) {
    const auto expected = i >= 257 && i < 450 ? std::uint8_t{0xa5} : source[i];
    if (result[i] != expected) {
      std::fprintf(stderr, "byte %zu: expected %u, got %u\n", i, static_cast<unsigned>(expected), static_cast<unsigned>(result[i]));
      return 1;
    }
  }

  CUDA_REQUIRE(cuMemFree_v2(second));
  CUDA_REQUIRE(cuMemFree_v2(first));
  CUDA_REQUIRE(cuCtxDestroy_v2(context));
  std::puts("pass");
  return 0;
}
