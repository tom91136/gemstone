// SPDX-License-Identifier: Apache-2.0
#include <cassert>
#include <cstdlib>
#include <cstring>

#include "gemstone/hip/dispatch.hpp"
#include <hip/hip_runtime_api.h>

int main() {
  assert(gemstone::hip::dispatch_dimensions(dim3{7, 1, 1}, dim3{64, 1, 1}) == 1);
  assert(gemstone::hip::dispatch_dimensions(dim3{7, 1, 1}, dim3{8, 4, 1}) == 2);
  assert(gemstone::hip::dispatch_dimensions(dim3{7, 1, 1}, dim3{8, 4, 2}) == 3);
  assert(gemstone::hip::dispatch_dimensions(dim3{7, 3, 1}, dim3{8, 1, 1}) == 2);
  assert(gemstone::hip::dispatch_dimensions(dim3{7, 3, 2}, dim3{8, 1, 1}) == 3);
  std::uint32_t packed_value = 0x12345678;
  size_t packed_size = sizeof(packed_value);
  void *extra[] = {
      HIP_LAUNCH_PARAM_BUFFER_POINTER, &packed_value, HIP_LAUNCH_PARAM_BUFFER_SIZE, &packed_size, HIP_LAUNCH_PARAM_END,
  };
  gemstone::hip::LaunchBuffer launch_buffer;
  assert(gemstone::hip::parse_launch_buffer(extra, launch_buffer));
  assert(launch_buffer.data == &packed_value && launch_buffer.size == sizeof(packed_value));
  void *malformed[] = {HIP_LAUNCH_PARAM_BUFFER_POINTER, &packed_value, HIP_LAUNCH_PARAM_END};
  assert(!gemstone::hip::parse_launch_buffer(malformed, launch_buffer));
  assert(setenv("GEMSTONE_AMDGPU_TARGET", "gfx1010", 1) == 0);
  int major = 0;
  int minor = 0;
  assert(hipDeviceComputeCapability(&major, &minor, 0) == hipSuccess);
  assert(major == 10 && minor == 1);
  char name[64]{};
  assert(hipDeviceGetName(name, sizeof(name), 0) == hipSuccess);
  assert(std::strcmp(name, "Gemstone gfx1010") == 0);
  hipDeviceProp_tR0600 properties{};
  assert(hipGetDevicePropertiesR0600(&properties, 0) == hipSuccess);
  assert(std::strcmp(properties.gcnArchName, "gfx1010") == 0);
  assert(properties.major == 10 && properties.minor == 1);

  unsetenv("GEMSTONE_RPC_PORT");
  unsetenv("GEMSTONE_RPC_TOKEN");
  int count = -1;
  assert(hipGetDeviceCount(&count) == hipErrorNoDevice);
  assert(count == 0);
  assert(hipGetLastError() == hipErrorNoDevice);
  return 0;
}
