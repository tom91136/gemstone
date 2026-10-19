#pragma once

#include <cstddef>
#include <cstdint>
#include <vector>

#include "hip/support.hpp"
#include <catch2/catch_test_macros.hpp>
#include <hip/hip_runtime.h>

#include "struct_alignment.hpp"

#ifndef UNIFIED_STRUCT_ALIGNMENT_KERNEL_ATTRIBUTE
  #define UNIFIED_STRUCT_ALIGNMENT_KERNEL_ATTRIBUTE
#endif

// Records of every packed, under-aligned and aligned layout, read field by field and stored back. The includer
// registers the cases, and each translation unit instantiates the kernels under its own target features.
namespace {
using namespace unified::struct_alignment;

template <class T>
__global__ UNIFIED_STRUCT_ALIGNMENT_KERNEL_ATTRIBUTE void poseTransform(uint8_t *bytes, uint32_t offset, uint32_t *out, uint32_t count) {
  const uint32_t i = (blockIdx.x * blockDim.x) + threadIdx.x;
  if (i >= count) return;
  T *records = reinterpret_cast<T *>(bytes + offset);
  const float x = records[i].x;
  const float y = records[i].y;
  const float z = records[i].z;
  const int type = records[i].type;
  uint32_t tag = 0;
  if constexpr (Tagged<T>) tag = static_cast<uint8_t>(records[i].tag);
  const float tx = fmaf(R00, x, fmaf(R01, y, fmaf(R02, z, T0)));
  const float ty = fmaf(R10, x, fmaf(R11, y, fmaf(R12, z, T1)));
  const float tz = fmaf(R20, x, fmaf(R21, y, fmaf(R22, z, T2)));
  uint32_t *row = out + (static_cast<size_t>(i) * Words);
  row[0] = __float_as_uint(tx);
  row[1] = __float_as_uint(ty);
  row[2] = __float_as_uint(tz);
  row[3] = __float_as_uint(x);
  row[4] = __float_as_uint(y);
  row[5] = __float_as_uint(z);
  row[6] = static_cast<uint32_t>(type);
  row[7] = tag;
  if (i % 2 == 0) {
    records[i].x = tx;
    records[i].y = ty;
    records[i].z = tz;
    records[i].type = ~type;
    if constexpr (Tagged<T>) records[i].tag = static_cast<char>(tag ^ TagMask);
  }
}

template <class T> void run() {
  constexpr uint32_t threads = 128;
  const auto expected = fixture<T>();
  hip_support::DeviceBuffer<uint8_t> image(expected.input);
  hip_support::DeviceBuffer<uint32_t> out(std::vector<uint32_t>(expected.words.size(), 0xdeadbeefU));
  REQUIRE(reinterpret_cast<uintptr_t>(image.data) % BaseAlignment == 0);
  poseTransform<T><<<(Count + threads - 1) / threads, threads>>>(image.data, static_cast<uint32_t>(Fixture<T>::Offset), out.data, Count);
  hip_support::finish("struct alignment");
  verify(expected, image.download(), out.download());
}

} // namespace
