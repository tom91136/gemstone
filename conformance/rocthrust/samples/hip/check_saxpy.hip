#pragma region case: hip-saxpy
#pragma region do: {hipcc} -std=c++17 -O2 --offload-arch=gfx900 -o {output} {input} -lamdhip64
#pragma region do: {output}
#pragma region requires: pass

// Adapted from ROCm/rocm-examples HIP-Basic/saxpy/main.hip.
// MIT License
// Copyright (c) 2022-2023 Advanced Micro Devices, Inc. All rights reserved.
// See ../LICENSE-ROCM-EXAMPLES-MIT.

#include <hip/hip_runtime.h>

#include <cassert>
#include <cmath>
#include <cstddef>
#include <cstdio>
#include <vector>

__global__ void saxpy_kernel(const float a, const float* x, float* y, const unsigned int size)
{
    const unsigned int index = blockIdx.x * blockDim.x + threadIdx.x;
    if(index < size)
        y[index] = a * x[index] + y[index];
}

int main()
{
    constexpr unsigned int size = 257;
    constexpr float a = 2.0F;
    std::vector<float> x(size);
    std::vector<float> y(size, 1.0F);
    for(unsigned int index = 0; index < size; ++index)
        x[index] = static_cast<float>(index + 1);

    float* device_x = nullptr;
    float* device_y = nullptr;
    assert(hipMalloc(&device_x, size * sizeof(float)) == hipSuccess);
    assert(hipMalloc(&device_y, size * sizeof(float)) == hipSuccess);
    assert(hipMemcpy(device_x, x.data(), size * sizeof(float), hipMemcpyHostToDevice) == hipSuccess);
    assert(hipMemcpy(device_y, y.data(), size * sizeof(float), hipMemcpyHostToDevice) == hipSuccess);
    saxpy_kernel<<<2, 256>>>(a, device_x, device_y, size);
    assert(hipGetLastError() == hipSuccess);
    assert(hipMemcpy(y.data(), device_y, size * sizeof(float), hipMemcpyDeviceToHost) == hipSuccess);
    assert(hipFree(device_y) == hipSuccess);
    assert(hipFree(device_x) == hipSuccess);

    for(unsigned int index = 0; index < size; ++index)
        assert(std::fabs(y[index] - (a * x[index] + 1.0F)) < 0.0001F);
    std::puts("pass");
    return 0;
}
