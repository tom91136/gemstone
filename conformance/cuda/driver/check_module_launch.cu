/*
 * Adapted from GPUOcelot's ocelot/src/cuda/test/driver/sequence.cpp and sequence.ptx.
 * Copyright (c) 2015, Computer Architecture and Systems Laboratory at Georgia Tech.
 * SPDX-License-Identifier: BSD-3-Clause
 */
#pragma region case: driver-module-launch
#pragma region targets: cuda
#pragma region do: {nvcc} --allow-unsupported-compiler -std=c++17 -O2 {cuda_driver_defines} -x cu -o {output} {input} {cuda_driver_link}
#pragma region do: {output}
#pragma region requires: pass

#include <array>
#include <cstddef>
#include <cstdio>

#include <cuda.h>

#if defined(GEMSTONE_CUDA_ABI) && defined(__GNUC__)
extern "C" CUresult CUDAAPI gemstone_cuCtxCreate_v2(CUcontext *, unsigned int, CUdevice) __asm__("cuCtxCreate_v2");
#endif

namespace {
constexpr const char *module_ptx = R"ptx(
.version 6.0
.target sm_50
.address_size 64

.visible .entry sequence(
  .param .u64 sequence_values,
  .param .u32 sequence_size
) {
  .reg .pred %p<2>;
  .reg .b32 %r<7>;
  .reg .b64 %rd<4>;

  ld.param.u64 %rd1, [sequence_values];
  ld.param.u32 %r1, [sequence_size];
  mov.u32 %r2, %tid.x;
  mov.u32 %r3, %ctaid.x;
  mov.u32 %r4, %ntid.x;
  mad.lo.s32 %r5, %r3, %r4, %r2;
  setp.ge.u32 %p1, %r5, %r1;
  @%p1 bra done;
  mul.wide.u32 %rd2, %r5, 4;
  add.s64 %rd3, %rd1, %rd2;
  shl.b32 %r6, %r5, 1;
  st.global.u32 [%rd3], %r6;
done:
  ret;
}
)ptx";
}

#define CUDA_REQUIRE(expression)                                                                                                           \
  do {                                                                                                                                     \
    const CUresult status = (expression);                                                                                                  \
    if (status != CUDA_SUCCESS) {                                                                                                          \
      const char *description = nullptr;                                                                                                   \
      cuGetErrorString(status, &description);                                                                                              \
      std::fprintf(stderr, "%s failed with CUDA error %d (%s) at line %d\n", #expression, static_cast<int>(status),                        \
                   description == nullptr ? "unknown" : description, __LINE__);                                                            \
      return 1;                                                                                                                            \
    }                                                                                                                                      \
  } while (false)

int main() {
  CUDA_REQUIRE(cuInit(0));
  CUdevice device{};
  CUcontext context{};
  CUDA_REQUIRE(cuDeviceGet(&device, 0));
#if defined(GEMSTONE_CUDA_ABI) && defined(__GNUC__)
  CUDA_REQUIRE(gemstone_cuCtxCreate_v2(&context, 0, device));
#elif CUDA_VERSION >= 13000
  CUDA_REQUIRE(cuCtxCreate(&context, nullptr, 0, device));
#else
  CUDA_REQUIRE(cuCtxCreate(&context, 0, device));
#endif

  CUmodule module{};
  CUfunction function{};
  CUDA_REQUIRE(cuModuleLoadData(&module, module_ptx));
  CUDA_REQUIRE(cuModuleGetFunction(&function, module, "sequence"));

  constexpr std::size_t size = 257;
  std::array<int, size> result{};
  CUdeviceptr values{};
  CUDA_REQUIRE(cuMemAlloc_v2(&values, result.size() * sizeof(int)));
  unsigned int element_count = static_cast<unsigned int>(size);
  void *parameters[] = {&values, &element_count};
  CUDA_REQUIRE(cuLaunchKernel(function, 3, 1, 1, 128, 1, 1, 0, nullptr, parameters, nullptr));
  CUDA_REQUIRE(cuCtxSynchronize());
  CUDA_REQUIRE(cuMemcpyDtoH_v2(result.data(), values, result.size() * sizeof(int)));

  CUDA_REQUIRE(cuMemFree_v2(values));
  CUDA_REQUIRE(cuModuleUnload(module));
  CUDA_REQUIRE(cuCtxDestroy_v2(context));
  for (std::size_t i = 0; i < size; ++i) {
    const int expected = static_cast<int>(i * 2);
    if (result[i] != expected) {
      std::fprintf(stderr, "element %zu: expected %d, got %d\n", i, expected, result[i]);
      return 1;
    }
  }
  std::puts("pass");
  return 0;
}
