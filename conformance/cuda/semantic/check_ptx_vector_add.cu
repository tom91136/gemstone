#pragma region case: semantic-ptx-vector-add
#pragma region targets: cuda
#pragma region ptx-kernel: vector_add
#pragma region ptx-shape: 1,4
#pragma region ptx-input: input_a=1,2,3,4
#pragma region ptx-input: input_b=10,20,30,40
#pragma region ptx-output: output=11,22,33,44
#pragma region do: {nvcc} --allow-unsupported-compiler -std=c++17 -O2 {cuda_driver_defines} -x cu -o {output} {input} {cuda_driver_link}
#pragma region do: {output}
#pragma region requires: pass

#include <array>
#include <cstdio>

#include <cuda.h>

namespace {
constexpr const char *module_ptx = R"ptx(
.version 8.0
.target sm_80
.address_size 64

.visible .entry vector_add(
  .param .u64 input_a,
  .param .u64 input_b,
  .param .u64 output
) {
  .reg .b32 %r<4>;
  .reg .b64 %rd<4>;
  ld.param.u64 %rd1, [input_a];
  ld.param.u64 %rd2, [input_b];
  ld.param.u64 %rd3, [output];
  mov.u32 %r0, %tid.x;
  mul.wide.u32 %rd0, %r0, 4;
  add.s64 %rd1, %rd1, %rd0;
  add.s64 %rd2, %rd2, %rd0;
  add.s64 %rd3, %rd3, %rd0;
  ld.global.u32 %r1, [%rd1];
  ld.global.u32 %r2, [%rd2];
  add.u32 %r3, %r1, %r2;
  st.global.u32 [%rd3], %r3;
  ret;
}
)ptx";

#define CUDA_REQUIRE(expression)                                                                                                           \
  do {                                                                                                                                     \
    const CUresult status = (expression);                                                                                                  \
    if (status != CUDA_SUCCESS) {                                                                                                          \
      const char *description = nullptr;                                                                                                   \
      cuGetErrorString(status, &description);                                                                                              \
      std::fprintf(stderr, "%s failed with CUDA error %d (%s) at line %d\n", #expression, static_cast<int>(status),                        \
                   description == nullptr ? "unknown" : description, __LINE__);                                                           \
      return 1;                                                                                                                            \
    }                                                                                                                                      \
  } while (false)
} // namespace

int main() {
  CUDA_REQUIRE(cuInit(0));
  CUdevice device{};
  CUcontext context{};
  CUDA_REQUIRE(cuDeviceGet(&device, 0));
#if CUDA_VERSION >= 13000
  CUDA_REQUIRE(cuCtxCreate(&context, nullptr, 0, device));
#else
  CUDA_REQUIRE(cuCtxCreate(&context, 0, device));
#endif

  CUmodule module{};
  CUfunction function{};
  CUDA_REQUIRE(cuModuleLoadData(&module, module_ptx));
  CUDA_REQUIRE(cuModuleGetFunction(&function, module, "vector_add"));

  constexpr std::array<int, 4> input_a{1, 2, 3, 4};
  constexpr std::array<int, 4> input_b{10, 20, 30, 40};
  constexpr std::array<int, 4> expected{11, 22, 33, 44};
  std::array<int, 4> output{};
  CUdeviceptr a{};
  CUdeviceptr b{};
  CUdeviceptr out{};
  CUDA_REQUIRE(cuMemAlloc(&a, sizeof(input_a)));
  CUDA_REQUIRE(cuMemAlloc(&b, sizeof(input_b)));
  CUDA_REQUIRE(cuMemAlloc(&out, sizeof(output)));
  CUDA_REQUIRE(cuMemcpyHtoD(a, input_a.data(), sizeof(input_a)));
  CUDA_REQUIRE(cuMemcpyHtoD(b, input_b.data(), sizeof(input_b)));
  void *parameters[] = {&a, &b, &out};
  CUDA_REQUIRE(cuLaunchKernel(function, 1, 1, 1, 4, 1, 1, 0, nullptr, parameters, nullptr));
  CUDA_REQUIRE(cuCtxSynchronize());
  CUDA_REQUIRE(cuMemcpyDtoH(output.data(), out, sizeof(output)));

  CUDA_REQUIRE(cuMemFree(out));
  CUDA_REQUIRE(cuMemFree(b));
  CUDA_REQUIRE(cuMemFree(a));
  CUDA_REQUIRE(cuModuleUnload(module));
  CUDA_REQUIRE(cuCtxDestroy(context));
  if (output != expected) {
    std::fprintf(stderr, "unexpected vector: %d,%d,%d,%d\n", output[0], output[1], output[2], output[3]);
    return 1;
  }
  std::puts("pass");
  return 0;
}
