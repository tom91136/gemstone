// SPDX-License-Identifier: Apache-2.0
#include <array>
#include <cassert>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <limits>
#include <string>
#include <vector>

#include "../../../native/execution/test/rpc_fixture.hpp"
#include "../../hsa/tests/code_object_fixture.hpp"
#include "gemstone/hip/dispatch.hpp"
#include <hip/hip_ext.h>
#include <hip/hip_runtime_api.h>

namespace {
struct LaunchShape {
  dim3 global, requested, local, groups;
  bool extended;
};

std::vector<uint8_t> implicit_arguments(const dim3 &global, const dim3 &local) {
  std::vector<uint8_t> bytes(76, 0);
  const auto put32 = [&](size_t offset, uint32_t value) { std::memcpy(bytes.data() + offset, &value, 4); };
  const auto put16 = [&](size_t offset, uint16_t value) { std::memcpy(bytes.data() + offset, &value, 2); };
  put32(4, global.x / local.x);
  put32(8, global.y / local.y);
  put32(12, global.z / local.z);
  put16(16, local.x);
  put16(18, local.y);
  put16(20, local.z);
  put16(22, global.x % local.x);
  put16(24, global.y % local.y);
  put16(26, global.z % local.z);
  put16(68, global.z > 1 ? 3 : (global.y > 1 ? 2 : 1));
  return bytes;
}

gemstone_test::RpcFixture::Expected expected_shape(const LaunchShape &shape, uint32_t seed, bool geometry_only = false) {
  gemstone_test::RpcFixture::Expected expected{shape.local.x, shape.groups.x, shape.global.x, 0, seed};
  expected.local_yz = {shape.local.y, shape.local.z};
  expected.groups_yz = {shape.groups.y, shape.groups.z};
  expected.global_yz = {shape.global.y, shape.global.z};
  expected.geometry_only = geometry_only;
  return expected;
}

hipError_t launch_shape(hipFunction_t function, const LaunchShape &shape, void **arguments) {
  if (shape.extended)
    return hipExtModuleLaunchKernel(function, shape.global.x, shape.global.y, shape.global.z, shape.requested.x, shape.requested.y,
                                    shape.requested.z, 0, nullptr, arguments, nullptr, nullptr, nullptr, 0);
  return hipModuleLaunchKernel(function, shape.groups.x, shape.groups.y, shape.groups.z, shape.requested.x, shape.requested.y,
                               shape.requested.z, 0, nullptr, arguments, nullptr);
}

void asymmetric_and_boundary_launches(gemstone_test::RpcFixture &fixture, hipFunction_t function) {
  const std::array<LaunchShape, 9> shapes{{{{8, 15, 1}, {4, 5, 1}, {4, 5, 1}, {2, 3, 1}, false},
                                           {{8, 9, 10}, {4, 3, 2}, {4, 3, 2}, {2, 3, 5}, false},
                                           {{32, 32, 1}, {32, 32, 1}, {32, 32, 1}, {1, 1, 1}, false},
                                           {{16, 8, 8}, {16, 8, 8}, {16, 8, 8}, {1, 1, 1}, false},
                                           {{9, 13, 1}, {4, 5, 1}, {4, 5, 1}, {3, 3, 1}, true},
                                           {{10, 11, 1}, {3, 4, 1}, {3, 4, 1}, {4, 3, 1}, true},
                                           {{9, 7, 5}, {4, 3, 2}, {4, 3, 2}, {3, 3, 3}, true},
                                           {{2, 8, 3}, {4, 3, 5}, {2, 3, 3}, {1, 3, 1}, true},
                                           {{7, 2, 9}, {3, 5, 4}, {3, 2, 4}, {3, 1, 3}, true}}};
  for (const auto &shape : shapes)
    for (uint32_t seed : {11U, 37U}) {
      const size_t count = size_t(shape.global.x) * shape.global.y * shape.global.z;
      void *allocation = nullptr;
      assert(hipMalloc(&allocation, (count + 2) * 4) == hipSuccess);
      auto *output = static_cast<uint32_t *>(allocation) + 1;
      std::vector<uint32_t> data(count + 2, 0xa5a5a5a5U);
      assert(hipMemcpy(allocation, data.data(), data.size() * 4, hipMemcpyHostToDevice) == hipSuccess);
      void *arguments[] = {&output, &seed};
      fixture.expect(expected_shape(shape, seed));
      fixture.expectTrailingArguments(implicit_arguments(shape.global, shape.local));
      assert(launch_shape(function, shape, arguments) == hipSuccess);
      assert(hipMemcpy(data.data(), allocation, data.size() * 4, hipMemcpyDeviceToHost) == hipSuccess);
      assert(data.front() == 0xa5a5a5a5U && data.back() == 0xa5a5a5a5U);
      for (size_t i = 0; i < count; ++i)
        assert(data[i + 1] == seed + i);
      fixture.check();
      assert(hipFree(allocation) == hipSuccess);
    }

  // Only wire geometry is inspected for these limits: allocate sixteen bytes, not a giant guest grid.
  void *output = nullptr;
  assert(hipMalloc(&output, 16) == hipSuccess);
  uint32_t seed = 19;
  void *arguments[] = {&output, &seed};
  constexpr uint32_t maximum = std::numeric_limits<uint32_t>::max();
  constexpr uint32_t signed_max = std::numeric_limits<int32_t>::max();
  for (const auto &shape : std::array<LaunchShape, 4>{{{{maximum, 1, 1}, {4, 1, 1}, {4, 1, 1}, {1073741824U, 1, 1}, true},
                                                       {{1, maximum, 1}, {1, 4, 1}, {1, 4, 1}, {1, 1073741824U, 1}, true},
                                                       {{1, 1, maximum}, {1, 1, 4}, {1, 1, 4}, {1, 1, 1073741824U}, true},
                                                       {{signed_max, 1, 1}, {1, 1, 1}, {1, 1, 1}, {signed_max, 1, 1}, false}}}) {
    fixture.expect(expected_shape(shape, seed, true));
    fixture.expectTrailingArguments(implicit_arguments(shape.global, shape.local));
    assert(launch_shape(function, shape, arguments) == hipSuccess);
    fixture.check();
  }

  // Advertised maxThreadsPerBlock/maxThreadsDim are 1024. Invalid requests must stop before RPC launch.
  hipDeviceProp_tR0600 caps{};
  assert(hipGetDevicePropertiesR0600(&caps, 0) == hipSuccess);
  assert(caps.maxThreadsPerBlock == 1024 && caps.maxThreadsDim[0] == 1024 && caps.maxThreadsDim[1] == 1024
         && caps.maxThreadsDim[2] == 1024);
  const auto before = fixture.launches();
  for (bool extended : {false, true}) {
    for (const dim3 local :
         {dim3{1025, 1, 1}, dim3{1, 1025, 1}, dim3{1, 1, 1025}, dim3{33, 33, 1}, dim3{65536, 65536, 1}, dim3{maximum, 1, 1}}) {
      LaunchShape invalid{local, local, local, {1, 1, 1}, extended};
      fixture.expect(expected_shape(invalid, seed, true));
      const auto status = launch_shape(function, invalid, arguments);
      if (status != hipErrorInvalidConfiguration)
        std::fprintf(stderr, "invalid block %ux%ux%u extended=%d returned=%d\n", local.x, local.y, local.z, extended, status);
      assert(status == hipErrorInvalidConfiguration);
      assert(fixture.launches() == before);
    }
  }
  for (unsigned axis = 0; axis < 3; ++axis) {
    const auto axis_value = [axis](uint32_t value) {
      dim3 result{1, 1, 1};
      if (axis == 0) result.x = value;
      else if (axis == 1) result.y = value;
      else result.z = value;
      return result;
    };
    dim3 zero = axis_value(0);
    LaunchShape local_zero{{1, 1, 1}, zero, zero, {1, 1, 1}, false};
    LaunchShape grid_zero{zero, {1, 1, 1}, {1, 1, 1}, zero, false};
    assert(launch_shape(function, local_zero, arguments) == hipErrorInvalidConfiguration);
    assert(launch_shape(function, grid_zero, arguments) == hipErrorInvalidConfiguration);
    local_zero.extended = true;
    grid_zero.extended = true;
    assert(launch_shape(function, local_zero, arguments) == hipErrorInvalidConfiguration);
    assert(launch_shape(function, grid_zero, arguments) == hipErrorInvalidConfiguration);
    dim3 grid = axis_value(signed_max);
    dim3 block = axis_value(4);
    LaunchShape overflow{{1, 1, 1}, block, block, grid, false};
    assert(launch_shape(function, overflow, arguments) == hipErrorInvalidConfiguration);
    LaunchShape excessive_grid{axis_value(maximum), {1, 1, 1}, {1, 1, 1}, axis_value(maximum), false};
    fixture.expect(expected_shape(excessive_grid, seed, true));
    assert(launch_shape(function, excessive_grid, arguments) == hipErrorInvalidConfiguration);
    excessive_grid.extended = true;
    assert(launch_shape(function, excessive_grid, arguments) == hipErrorInvalidConfiguration);
  }
  for (uint32_t width : {1025U, 2048U}) {
    LaunchShape oversized_nominal{{1, 1, 1}, {width, 1, 1}, {1, 1, 1}, {1, 1, 1}, true};
    fixture.expect(expected_shape(oversized_nominal, seed, true));
    assert(launch_shape(function, oversized_nominal, arguments) == hipErrorInvalidConfiguration);
  }
  assert(fixture.launches() == before);
  assert(hipFree(output) == hipSuccess);
}
} // namespace

// Exercise the real HIP -> embedded HSA -> framed RPC path; only device computation is mocked.
void cross_workgroup_launch_and_copyback() {
  gemstone_test::RpcFixture fixture;
  const auto port = std::to_string(fixture.port());
  assert(setenv("GEMSTONE_RPC_PORT", port.c_str(), 1) == 0);
  assert(setenv("GEMSTONE_RPC_TOKEN", gemstone_test::RpcFixture::token, 1) == 0);
  void *managed = nullptr;
  assert(hipMallocManaged(&managed, 256) == hipSuccess);
  hipPointerAttribute_t attributes{};
  for (size_t offset : {size_t(0), size_t(128), size_t(255)}) {
    assert(hipPointerGetAttributes(&attributes, static_cast<uint8_t *>(managed) + offset) == hipSuccess);
    assert(attributes.isManaged == 1 && attributes.type == hipMemoryTypeManaged);
  }
  assert(hipFree(managed) == hipSuccess);
  void *device = nullptr;
  assert(hipMalloc(&device, 256) == hipSuccess);
  assert(hipPointerGetAttributes(&attributes, device) == hipSuccess);
  assert(attributes.isManaged == 0 && attributes.type == hipMemoryTypeDevice);
  assert(hipFree(device) == hipSuccess);
  const auto image = gemstone::hsa::test::code_object_fixture(1, true, true);
  hipModule_t module{};
  hipFunction_t function{};
  assert(hipModuleLoadData(&module, image.data()) == hipSuccess);
  unsigned int function_count = 0;
  assert(hipModuleGetFunctionCount(&function_count, module) == hipSuccess);
  assert(function_count == 1);
  assert(hipModuleGetFunction(&function, module, "add") == hipSuccess);
  for (bool packed : {false, true})
    for (uint32_t groups : {1U, 2U, 3U, 5U, 9U})
      for (uint32_t width : {31U, 32U, 33U})
        for (uint32_t tail : {0U, 1U}) {
          if (tail && !packed) continue;
          const uint32_t count = width * groups - tail;
          const uint32_t effective_width = std::min(width, count);
          void *allocation = nullptr;
          assert(hipMalloc(&allocation, (count + 2) * sizeof(uint32_t)) == hipSuccess);
          auto *output = static_cast<uint32_t *>(allocation) + 1;
          for (uint32_t seed : {7U, 29U}) {
            std::vector<uint32_t> observed(count + 2, 0xa5a5a5a5U);
            assert(hipMemcpy(allocation, observed.data(), observed.size() * sizeof(uint32_t), hipMemcpyHostToDevice) == hipSuccess);
            fixture.expect({effective_width, groups, count, 0, seed});
            // Bytes after output+seed include alignment, V5 implicit fields, and untouched reserved bytes.
            std::vector<uint8_t> implicit(76, 0);
            const auto put32 = [&](size_t offset, uint32_t value) { std::memcpy(implicit.data() + offset, &value, 4); };
            const auto put16 = [&](size_t offset, uint16_t value) { std::memcpy(implicit.data() + offset, &value, 2); };
            put32(4, count / effective_width);
            put32(8, 1);
            put32(12, 1);
            put16(16, static_cast<uint16_t>(effective_width));
            put16(18, 1);
            put16(20, 1);
            put16(22, static_cast<uint16_t>(count % effective_width));
            put16(68, 1);
            fixture.expectTrailingArguments(implicit);
            if (packed) {
              // The packed extra-buffer path also carries harmless trailing host padding.
              std::array<uint8_t, 16> arguments{};
              std::memcpy(arguments.data(), &output, 8);
              std::memcpy(arguments.data() + 8, &seed, 4);
              size_t size = arguments.size();
              void *extra[] = {HIP_LAUNCH_PARAM_BUFFER_POINTER, arguments.data(), HIP_LAUNCH_PARAM_BUFFER_SIZE, &size,
                               HIP_LAUNCH_PARAM_END};
              assert(hipExtModuleLaunchKernel(function, count, 1, 1, width, 1, 1, 0, nullptr, nullptr, extra, nullptr, nullptr, 0)
                     == hipSuccess);
            } else {
              void *arguments[] = {&output, &seed};
              assert(hipModuleLaunchKernel(function, groups, 1, 1, width, 1, 1, 0, nullptr, arguments, nullptr) == hipSuccess);
            }
            assert(hipDeviceSynchronize() == hipSuccess);
            assert(hipMemcpy(observed.data(), allocation, observed.size() * sizeof(uint32_t), hipMemcpyDeviceToHost) == hipSuccess);
            assert(observed.front() == 0xa5a5a5a5U && observed.back() == 0xa5a5a5a5U);
            for (uint32_t index = 0; index < count; ++index)
              assert(observed[index + 1] == seed + index);
            fixture.check();
          }
          assert(hipFree(allocation) == hipSuccess);
        }
  assert(fixture.launches() == 90);
  asymmetric_and_boundary_launches(fixture, function);
  assert(fixture.launches() == 112);
  assert(hipModuleUnload(module) == hipSuccess);
  fixture.check();
}

void registered_host_copy() {
  gemstone_test::RpcFixture fixture;
  const auto port = std::to_string(fixture.port());
  setenv("GEMSTONE_RPC_PORT", port.c_str(), 1);
  setenv("GEMSTONE_RPC_TOKEN", gemstone_test::RpcFixture::token, 1);
  for (bool pinned : {false, true}) {
    std::array<uint32_t, 4> ordinary{};
    uint32_t *host = ordinary.data();
    if (pinned) assert(hipHostMalloc(reinterpret_cast<void **>(&host), sizeof(ordinary), 0) == hipSuccess);
    else assert(hipHostRegister(host, sizeof(ordinary), 0) == hipSuccess);
    void *device = nullptr, *copy = nullptr;
    assert(hipMalloc(&device, sizeof(ordinary)) == hipSuccess);
    assert(hipMalloc(&copy, sizeof(ordinary)) == hipSuccess);
    for (uint32_t seed : {0x11223344U, 0x8899aabbU}) {
      for (unsigned i=0;i<4;++i) host[i]=seed+i;
      assert(hipMemcpy(device, host, sizeof(ordinary), hipMemcpyHostToDevice) == hipSuccess);
      for (unsigned i=0;i<4;++i) assert(host[i] == seed+i);
      std::array<uint32_t,4> output{};
      assert(hipMemcpy(copy,device,sizeof(ordinary),hipMemcpyDeviceToDevice)==hipSuccess);
      assert(hipMemcpy(output.data(),copy,sizeof(output),hipMemcpyDeviceToHost)==hipSuccess);
      for(unsigned i=0;i<4;++i) assert(output[i]==seed+i);
    }
    assert(hipFree(copy)==hipSuccess);assert(hipFree(device)==hipSuccess);
    if(pinned) assert(hipHostFree(host)==hipSuccess);
    else assert(hipHostUnregister(host)==hipSuccess);
  }
  assert(fixture.launches()==0);
}

void pointer_attributes() {
  gemstone_test::RpcFixture fixture;
  const auto port=std::to_string(fixture.port());
  setenv("GEMSTONE_RPC_PORT",port.c_str(),1);setenv("GEMSTONE_RPC_TOKEN",gemstone_test::RpcFixture::token,1);
  uint8_t ordinary[32]{};
  hipPointerAttribute_t info{};
  assert(hipPointerGetAttributes(&info,ordinary)==hipErrorInvalidValue);
  assert(hipHostRegister(ordinary,sizeof(ordinary),0)==hipSuccess);
  for(size_t offset:{size_t(0),size_t(7),sizeof(ordinary)-1}) {
    assert(hipPointerGetAttributes(&info,ordinary+offset)==hipSuccess);
    assert(info.type==hipMemoryTypeHost && info.isManaged==0 && info.hostPointer==ordinary+offset);
  }
  assert(hipHostUnregister(ordinary)==hipSuccess);
  assert(hipPointerGetAttributes(&info,ordinary)==hipErrorInvalidValue);
  for(auto kind:{hipMemoryTypeHost,hipMemoryTypeDevice,hipMemoryTypeManaged}) {
    void *pointer=nullptr;
    if(kind==hipMemoryTypeHost) assert(hipHostMalloc(&pointer,32,0)==hipSuccess);
    else if(kind==hipMemoryTypeManaged) assert(hipMallocManaged(&pointer,32)==hipSuccess);
    else assert(hipMalloc(&pointer,32)==hipSuccess);
    for(size_t offset:{size_t(0),size_t(7),size_t(31)}) {
      assert(hipPointerGetAttributes(&info,static_cast<uint8_t *>(pointer)+offset)==hipSuccess);
      assert(info.type==kind && info.isManaged==(kind==hipMemoryTypeManaged));
    }
    if(kind==hipMemoryTypeHost) assert(hipHostFree(pointer)==hipSuccess);
    else assert(hipFree(pointer)==hipSuccess);
    assert(hipPointerGetAttributes(&info,pointer)==hipErrorInvalidValue);
  }
  assert(fixture.launches()==0);
}

void device_reset_lifetime() {
  gemstone_test::RpcFixture fixture;
  const auto port = std::to_string(fixture.port());
  assert(setenv("GEMSTONE_RPC_PORT", port.c_str(), 1) == 0);
  assert(setenv("GEMSTONE_RPC_TOKEN", gemstone_test::RpcFixture::token, 1) == 0);
  void *memory = nullptr;
  hipStream_t stream = nullptr;
  hipEvent_t event = nullptr;
  assert(hipMalloc(&memory, 64) == hipSuccess);
  assert(hipStreamCreate(&stream) == hipSuccess);
  assert(hipEventCreate(&event) == hipSuccess);
  const auto image = gemstone::hsa::test::code_object_fixture(1, true, true);
  hipModule_t module = nullptr;
  assert(hipModuleLoadData(&module, image.data()) == hipSuccess);
  assert(hipDeviceReset() == hipSuccess);
  hipPointerAttribute_t attributes{};
  assert(hipPointerGetAttributes(&attributes, memory) == hipErrorInvalidValue);
  assert(hipFree(memory) == hipErrorInvalidDevicePointer);
  assert(hipStreamQuery(stream) == hipErrorInvalidResourceHandle);
  assert(hipEventQuery(event) == hipErrorInvalidResourceHandle);
  hipFunction_t stale_function = nullptr;
  assert(hipModuleGetFunction(&stale_function, module, "add") == hipErrorInvalidResourceHandle);
  assert(hipModuleUnload(module) == hipSuccess);
  void *replacement = nullptr;
  assert(hipMalloc(&replacement, 64) == hipSuccess);
  assert(replacement != nullptr);
  assert(hipMemsetAsync(replacement, 0, 64, stream) == hipErrorInvalidResourceHandle);
  assert(hipFree(replacement) == hipSuccess);
}

int main(int argc, char **argv) {
  if(argc==2 && std::strcmp(argv[1],"host-copy")==0){registered_host_copy();return 0;}
  if(argc==2 && std::strcmp(argv[1],"pointer-attributes")==0){pointer_attributes();return 0;}
  if(argc==2 && std::strcmp(argv[1],"device-reset")==0){device_reset_lifetime();return 0;}

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
  // Tensile's gfx1030 GEMM declares 112 bytes but supplies a 116-byte packed record.
  // Accept its unused trailer without writing beyond the destination or accepting truncated fields.
  unsigned char packed[116];
  for (size_t index = 0; index < sizeof(packed); ++index)
    packed[index] = static_cast<unsigned char>(index);
  unsigned char destination[120];
  std::memset(destination, 0xa5, sizeof(destination));
  gemstone::hip::LaunchBuffer tensile{packed, sizeof(packed)};
  assert(tensile.copy_to(destination + 4, 112, 112));
  assert(std::memcmp(destination + 4, packed, 112) == 0);
  for (size_t index = 0; index < 4; ++index) {
    assert(destination[index] == 0xa5);
    assert(destination[116 + index] == 0xa5);
  }
  std::memset(destination, 0xa5, sizeof(destination));
  tensile.size = 111;
  assert(!tensile.copy_to(destination + 4, 112, 112));
  for (unsigned char value : destination)
    assert(value == 0xa5);
  tensile.size = 112;
  assert(tensile.copy_to(destination + 4, 112, 112));
  // A caller need not supply hidden arguments, which the dispatcher initializes separately.
  std::memset(destination, 0, sizeof(destination));
  tensile.size = 80;
  assert(tensile.copy_to(destination + 4, 112, 80));
  assert(std::memcmp(destination + 4, packed, 80) == 0);
  for (size_t index = 84; index < sizeof(destination); ++index)
    assert(destination[index] == 0);
  std::memset(destination, 0, sizeof(destination));
  tensile.size = 116;
  assert(tensile.copy_to(destination + 4, 112, 80));
  assert(std::memcmp(destination + 4, packed, 80) == 0);
  for (size_t index = 84; index < sizeof(destination); ++index)
    assert(destination[index] == 0);
  assert(!tensile.copy_to(destination, 79, 80));
  tensile.data = nullptr;
  assert(!tensile.copy_to(destination, 112, 80));
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
  assert(properties.maxThreadsPerMultiProcessor == 1024);
  assert(properties.maxSharedMemoryPerMultiProcessor == 64U * 1024U);

  unsetenv("GEMSTONE_RPC_PORT");
  unsetenv("GEMSTONE_RPC_TOKEN");
  int count = -1;
  assert(hipGetDeviceCount(&count) == hipErrorNoDevice);
  assert(count == 0);
  assert(hipGetLastError() == hipErrorNoDevice);
  assert(hipMemAdvise(nullptr, 0, hipMemAdviseSetReadMostly, 0) == hipErrorNoDevice);
  assert(hipMemAdvise(nullptr, 0, hipMemAdviseSetAccessedBy, 0) == hipErrorNoDevice);
  assert(hipMemAdvise(nullptr, 0, hipMemAdviseUnsetAccessedBy, 0) == hipErrorNoDevice);
  cross_workgroup_launch_and_copyback();
  return 0;
}
