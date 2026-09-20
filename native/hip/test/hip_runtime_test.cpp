#include <array>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <limits>
#include <string>
#include <vector>

#include <catch2/catch_test_macros.hpp>
#include <hip/hip_ext.h>
#include <hip/hip_runtime_api.h>

#include "gemstone/hip/dispatch.hpp"
#include <gemstone/bytes.hpp>

#include "../../hsa/test/code_object_fixture.hpp"
#include "../../rpc/test/rpc_fixture.hpp"

namespace {
struct LaunchShape {
  dim3 global, requested, local, groups;
  bool extended;
};

std::vector<uint8_t> implicitArguments(const dim3 &global, const dim3 &local) {
  std::vector<uint8_t> bytes(76, 0);
  gemstone::writeNative(bytes.data(), 4, global.x / local.x);
  gemstone::writeNative(bytes.data(), 8, global.y / local.y);
  gemstone::writeNative(bytes.data(), 12, global.z / local.z);
  gemstone::writeNative(bytes.data(), 16, static_cast<uint16_t>(local.x));
  gemstone::writeNative(bytes.data(), 18, static_cast<uint16_t>(local.y));
  gemstone::writeNative(bytes.data(), 20, static_cast<uint16_t>(local.z));
  gemstone::writeNative(bytes.data(), 22, static_cast<uint16_t>(global.x % local.x));
  gemstone::writeNative(bytes.data(), 24, static_cast<uint16_t>(global.y % local.y));
  gemstone::writeNative(bytes.data(), 26, static_cast<uint16_t>(global.z % local.z));
  gemstone::writeNative(bytes.data(), 68, static_cast<uint16_t>(global.z > 1 ? 3 : (global.y > 1 ? 2 : 1)));
  return bytes;
}

gemstone_test::RpcFixture::Expected expectedShape(const LaunchShape &shape, uint32_t seed, bool geometryOnly = false) {
  gemstone_test::RpcFixture::Expected expected{shape.local.x, shape.groups.x, shape.global.x, 0, seed};
  expected.local_yz = {shape.local.y, shape.local.z};
  expected.groups_yz = {shape.groups.y, shape.groups.z};
  expected.global_yz = {shape.global.y, shape.global.z};
  expected.geometry_only = geometryOnly;
  return expected;
}

hipError_t launchShape(hipFunction_t function, const LaunchShape &shape, void **arguments) {
  if (shape.extended)
    return hipExtModuleLaunchKernel(function, shape.global.x, shape.global.y, shape.global.z, shape.requested.x, shape.requested.y,
                                    shape.requested.z, 0, nullptr, arguments, nullptr, nullptr, nullptr, 0);
  return hipModuleLaunchKernel(function, shape.groups.x, shape.groups.y, shape.groups.z, shape.requested.x, shape.requested.y,
                               shape.requested.z, 0, nullptr, arguments, nullptr);
}

void asymmetricAndBoundaryLaunches(gemstone_test::RpcFixture &fixture, hipFunction_t function) {
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
      REQUIRE((hipMalloc(&allocation, (count + 2) * 4) == hipSuccess));
      auto *output = static_cast<uint32_t *>(allocation) + 1;
      std::vector<uint32_t> data(count + 2, 0xa5a5a5a5U);
      REQUIRE((hipMemcpy(allocation, data.data(), data.size() * 4, hipMemcpyHostToDevice) == hipSuccess));
      void *arguments[] = {&output, &seed};
      fixture.expect(expectedShape(shape, seed));
      fixture.expectTrailingArguments(implicitArguments(shape.global, shape.local));
      REQUIRE((launchShape(function, shape, arguments) == hipSuccess));
      REQUIRE((hipMemcpy(data.data(), allocation, data.size() * 4, hipMemcpyDeviceToHost) == hipSuccess));
      REQUIRE((data.front() == 0xa5a5a5a5U && data.back() == 0xa5a5a5a5U));
      for (size_t i = 0; i < count; ++i)
        REQUIRE((data[i + 1] == seed + i));
      fixture.check();
      REQUIRE((hipFree(allocation) == hipSuccess));
    }

  // Only wire geometry is inspected for these limits: allocate sixteen bytes, not a giant guest grid.
  void *output = nullptr;
  REQUIRE((hipMalloc(&output, 16) == hipSuccess));
  uint32_t seed = 19;
  void *arguments[] = {&output, &seed};
  constexpr uint32_t maximum = std::numeric_limits<uint32_t>::max();
  constexpr uint32_t signed_max = std::numeric_limits<int32_t>::max();
  for (const auto &shape : std::array<LaunchShape, 4>{{{{signed_max, 1, 1}, {4, 1, 1}, {4, 1, 1}, {536870912U, 1, 1}, true},
                                                       {{1, signed_max, 1}, {1, 4, 1}, {1, 4, 1}, {1, 536870912U, 1}, true},
                                                       {{1, 1, signed_max}, {1, 1, 4}, {1, 1, 4}, {1, 1, 536870912U}, true},
                                                       {{signed_max, 1, 1}, {1, 1, 1}, {1, 1, 1}, {signed_max, 1, 1}, false}}}) {
    fixture.expect(expectedShape(shape, seed, true));
    fixture.expectTrailingArguments(implicitArguments(shape.global, shape.local));
    REQUIRE((launchShape(function, shape, arguments) == hipSuccess));
    fixture.check();
  }

  // Advertised maxThreadsPerBlock/maxThreadsDim are 1024. Invalid requests must stop before RPC launch.
  hipDeviceProp_tR0600 caps{};
  REQUIRE((hipGetDevicePropertiesR0600(&caps, 0) == hipSuccess));
  REQUIRE(
      (caps.maxThreadsPerBlock == 1024 && caps.maxThreadsDim[0] == 1024 && caps.maxThreadsDim[1] == 1024 && caps.maxThreadsDim[2] == 1024));
  const auto before = fixture.launches();
  for (bool extended : {false, true}) {
    for (const dim3 local :
         {dim3{1025, 1, 1}, dim3{1, 1025, 1}, dim3{1, 1, 1025}, dim3{33, 33, 1}, dim3{65536, 65536, 1}, dim3{maximum, 1, 1}}) {
      LaunchShape invalid{local, local, local, {1, 1, 1}, extended};
      fixture.expect(expectedShape(invalid, seed, true));
      const auto status = launchShape(function, invalid, arguments);
      if (status != hipErrorInvalidConfiguration)
        std::fprintf(stderr, "invalid block %ux%ux%u extended=%d returned=%d\n", local.x, local.y, local.z, extended, status);
      REQUIRE((status == hipErrorInvalidConfiguration));
      REQUIRE((fixture.launches() == before));
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
    REQUIRE((launchShape(function, local_zero, arguments) == hipErrorInvalidConfiguration));
    REQUIRE((launchShape(function, grid_zero, arguments) == hipErrorInvalidConfiguration));
    local_zero.extended = true;
    grid_zero.extended = true;
    REQUIRE((launchShape(function, local_zero, arguments) == hipErrorInvalidConfiguration));
    REQUIRE((launchShape(function, grid_zero, arguments) == hipErrorInvalidConfiguration));
    dim3 grid = axis_value(signed_max);
    dim3 block = axis_value(4);
    LaunchShape overflow{{1, 1, 1}, block, block, grid, false};
    REQUIRE((launchShape(function, overflow, arguments) == hipErrorInvalidConfiguration));
    LaunchShape excessive_grid{axis_value(maximum), {1, 1, 1}, {1, 1, 1}, axis_value(maximum), false};
    fixture.expect(expectedShape(excessive_grid, seed, true));
    REQUIRE((launchShape(function, excessive_grid, arguments) == hipErrorInvalidConfiguration));
    excessive_grid.extended = true;
    REQUIRE((launchShape(function, excessive_grid, arguments) == hipErrorInvalidConfiguration));
  }
  for (uint32_t width : {1025U, 2048U}) {
    LaunchShape oversized_nominal{{1, 1, 1}, {width, 1, 1}, {1, 1, 1}, {1, 1, 1}, true};
    fixture.expect(expectedShape(oversized_nominal, seed, true));
    REQUIRE((launchShape(function, oversized_nominal, arguments) == hipErrorInvalidConfiguration));
  }
  REQUIRE((fixture.launches() == before));
  REQUIRE((hipFree(output) == hipSuccess));
}

// Exercise the real HIP -> embedded HSA -> framed RPC path; only device computation is mocked.
void crossWorkgroupLaunchAndCopyback() {
  gemstone_test::RpcFixture fixture;
  fixture.kernelMetadata(88, 2,
                         {{8, 0, 2, 0},
                          {4, 8, 1, 1},
                          {4, 16, 1, 1},
                          {4, 20, 1, 1},
                          {4, 24, 1, 1},
                          {2, 28, 1, 1},
                          {2, 30, 1, 1},
                          {2, 32, 1, 1},
                          {2, 34, 1, 1},
                          {2, 36, 1, 1},
                          {2, 38, 1, 1},
                          {2, 80, 1, 1}});
  const auto port = std::to_string(fixture.port());
  REQUIRE((setenv("GEMSTONE_RPC_PORT", port.c_str(), 1) == 0));
  REQUIRE((setenv("GEMSTONE_RPC_CLIENT", "42", 1) == 0));
  void *managed = nullptr;
  REQUIRE((hipMallocManaged(&managed, 256) == hipSuccess));
  hipPointerAttribute_t attributes{};
  for (size_t offset : {size_t(0), size_t(128), size_t(255)}) {
    REQUIRE((hipPointerGetAttributes(&attributes, static_cast<uint8_t *>(managed) + offset) == hipSuccess));
    REQUIRE((attributes.isManaged == 1 && attributes.type == hipMemoryTypeManaged));
  }
  REQUIRE((hipFree(managed) == hipSuccess));
  void *device = nullptr;
  REQUIRE((hipMalloc(&device, 256) == hipSuccess));
  REQUIRE((hipPointerGetAttributes(&attributes, device) == hipSuccess));
  REQUIRE((attributes.isManaged == 0 && attributes.type == hipMemoryTypeDevice));
  REQUIRE((hipFree(device) == hipSuccess));
  const auto image = gemstone::hsa::test::codeObjectFixture(1, true, true);
  hipModule_t module{};
  hipFunction_t function{};
  REQUIRE((hipModuleLoadData(&module, image.data()) == hipSuccess));
  unsigned int function_count = 0;
  REQUIRE((hipModuleGetFunctionCount(&function_count, module) == hipSuccess));
  REQUIRE((function_count == 1));
  REQUIRE((hipModuleGetFunction(&function, module, "add") == hipSuccess));
  for (bool packed : {false, true})
    for (uint32_t groups : {1U, 2U, 3U, 5U, 9U})
      for (uint32_t width : {31U, 32U, 33U})
        for (uint32_t tail : {0U, 1U}) {
          if (tail && !packed) continue;
          const uint32_t count = width * groups - tail;
          const uint32_t effective_width = std::min(width, count);
          void *allocation = nullptr;
          REQUIRE((hipMalloc(&allocation, (count + 2) * sizeof(uint32_t)) == hipSuccess));
          auto *output = static_cast<uint32_t *>(allocation) + 1;
          for (uint32_t seed : {7U, 29U}) {
            std::vector<uint32_t> observed(count + 2, 0xa5a5a5a5U);
            REQUIRE((hipMemcpy(allocation, observed.data(), observed.size() * sizeof(uint32_t), hipMemcpyHostToDevice) == hipSuccess));
            fixture.expect({effective_width, groups, count, 0, seed});
            // Bytes after output+seed include alignment, V5 implicit fields, and untouched reserved bytes.
            std::vector<uint8_t> implicit(76, 0);
            gemstone::writeNative(implicit.data(), 4, count / effective_width);
            gemstone::writeNative<uint32_t>(implicit.data(), 8, 1);
            gemstone::writeNative<uint32_t>(implicit.data(), 12, 1);
            gemstone::writeNative(implicit.data(), 16, static_cast<uint16_t>(effective_width));
            gemstone::writeNative<uint16_t>(implicit.data(), 18, 1);
            gemstone::writeNative<uint16_t>(implicit.data(), 20, 1);
            gemstone::writeNative(implicit.data(), 22, static_cast<uint16_t>(count % effective_width));
            gemstone::writeNative<uint16_t>(implicit.data(), 68, 1);
            fixture.expectTrailingArguments(implicit);
            if (packed) {
              // The packed extra-buffer path also carries harmless trailing host padding.
              std::array<uint8_t, 16> arguments{};
              std::memcpy(arguments.data(), &output, 8);
              std::memcpy(arguments.data() + 8, &seed, 4);
              size_t size = arguments.size();
              void *extra[] = {HIP_LAUNCH_PARAM_BUFFER_POINTER, arguments.data(), HIP_LAUNCH_PARAM_BUFFER_SIZE, &size,
                               HIP_LAUNCH_PARAM_END};
              REQUIRE((hipExtModuleLaunchKernel(function, count, 1, 1, width, 1, 1, 0, nullptr, nullptr, extra, nullptr, nullptr, 0)
                       == hipSuccess));
            } else {
              void *arguments[] = {&output, &seed};
              REQUIRE((hipModuleLaunchKernel(function, groups, 1, 1, width, 1, 1, 0, nullptr, arguments, nullptr) == hipSuccess));
            }
            REQUIRE((hipDeviceSynchronize() == hipSuccess));
            REQUIRE((hipMemcpy(observed.data(), allocation, observed.size() * sizeof(uint32_t), hipMemcpyDeviceToHost) == hipSuccess));
            REQUIRE((observed.front() == 0xa5a5a5a5U && observed.back() == 0xa5a5a5a5U));
            for (uint32_t index = 0; index < count; ++index)
              REQUIRE((observed[index + 1] == seed + index));
            fixture.check();
          }
          REQUIRE((hipFree(allocation) == hipSuccess));
        }
  REQUIRE((fixture.launches() == 90));
  asymmetricAndBoundaryLaunches(fixture, function);
  REQUIRE((fixture.launches() == 112));
  REQUIRE((hipModuleUnload(module) == hipSuccess));
  fixture.check();
}

void registeredHostCopy() {
  gemstone_test::RpcFixture fixture;
  const auto port = std::to_string(fixture.port());
  setenv("GEMSTONE_RPC_PORT", port.c_str(), 1);
  setenv("GEMSTONE_RPC_CLIENT", "42", 1);
  for (bool pinned : {false, true}) {
    std::array<uint32_t, 4> ordinary{};
    uint32_t *host = ordinary.data();
    if (pinned) REQUIRE((hipHostMalloc(reinterpret_cast<void **>(&host), sizeof(ordinary), 0) == hipSuccess));
    else REQUIRE((hipHostRegister(host, sizeof(ordinary), 0) == hipSuccess));
    void *device = nullptr, *copy = nullptr;
    REQUIRE((hipMalloc(&device, sizeof(ordinary)) == hipSuccess));
    REQUIRE((hipMalloc(&copy, sizeof(ordinary)) == hipSuccess));
    for (uint32_t seed : {0x11223344U, 0x8899aabbU}) {
      for (unsigned i = 0; i < 4; ++i)
        host[i] = seed + i;
      REQUIRE((hipMemcpy(device, host, sizeof(ordinary), hipMemcpyHostToDevice) == hipSuccess));
      for (unsigned i = 0; i < 4; ++i)
        REQUIRE((host[i] == seed + i));
      std::array<uint32_t, 4> output{};
      REQUIRE((hipMemcpy(copy, device, sizeof(ordinary), hipMemcpyDeviceToDevice) == hipSuccess));
      REQUIRE((hipMemcpy(output.data(), copy, sizeof(output), hipMemcpyDeviceToHost) == hipSuccess));
      for (unsigned i = 0; i < 4; ++i)
        REQUIRE((output[i] == seed + i));
    }
    REQUIRE((hipFree(copy) == hipSuccess));
    REQUIRE((hipFree(device) == hipSuccess));
    if (pinned) REQUIRE((hipHostFree(host) == hipSuccess));
    else REQUIRE((hipHostUnregister(host) == hipSuccess));
  }
  REQUIRE((fixture.launches() == 0));
}

void pointerAttributes() {
  gemstone_test::RpcFixture fixture;
  const auto port = std::to_string(fixture.port());
  setenv("GEMSTONE_RPC_PORT", port.c_str(), 1);
  setenv("GEMSTONE_RPC_CLIENT", "42", 1);
  uint8_t ordinary[32]{};
  hipPointerAttribute_t info{};
  REQUIRE((hipPointerGetAttributes(&info, ordinary) == hipErrorInvalidValue));
  REQUIRE((hipHostRegister(ordinary, sizeof(ordinary), 0) == hipSuccess));
  for (size_t offset : {size_t(0), size_t(7), sizeof(ordinary) - 1}) {
    REQUIRE((hipPointerGetAttributes(&info, ordinary + offset) == hipSuccess));
    REQUIRE((info.type == hipMemoryTypeHost && info.isManaged == 0 && info.hostPointer == ordinary + offset));
  }
  REQUIRE((hipHostUnregister(ordinary) == hipSuccess));
  REQUIRE((hipPointerGetAttributes(&info, ordinary) == hipErrorInvalidValue));
  for (auto kind : {hipMemoryTypeHost, hipMemoryTypeDevice, hipMemoryTypeManaged}) {
    void *pointer = nullptr;
    if (kind == hipMemoryTypeHost) REQUIRE((hipHostMalloc(&pointer, 32, 0) == hipSuccess));
    else if (kind == hipMemoryTypeManaged) REQUIRE((hipMallocManaged(&pointer, 32) == hipSuccess));
    else REQUIRE((hipMalloc(&pointer, 32) == hipSuccess));
    for (size_t offset : {size_t(0), size_t(7), size_t(31)}) {
      REQUIRE((hipPointerGetAttributes(&info, static_cast<uint8_t *>(pointer) + offset) == hipSuccess));
      REQUIRE((info.type == kind && info.isManaged == (kind == hipMemoryTypeManaged)));
    }
    if (kind == hipMemoryTypeHost) REQUIRE((hipHostFree(pointer) == hipSuccess));
    else REQUIRE((hipFree(pointer) == hipSuccess));
    REQUIRE((hipPointerGetAttributes(&info, pointer) == hipErrorInvalidValue));
  }
  REQUIRE((fixture.launches() == 0));
}

void deviceResetLifetime() {
  gemstone_test::RpcFixture fixture;
  const auto port = std::to_string(fixture.port());
  REQUIRE((setenv("GEMSTONE_RPC_PORT", port.c_str(), 1) == 0));
  REQUIRE((setenv("GEMSTONE_RPC_CLIENT", "42", 1) == 0));
  void *memory = nullptr;
  hipStream_t stream = nullptr;
  hipEvent_t event = nullptr;
  REQUIRE((hipMalloc(&memory, 64) == hipSuccess));
  REQUIRE((hipStreamCreate(&stream) == hipSuccess));
  REQUIRE((hipEventCreate(&event) == hipSuccess));
  const auto image = gemstone::hsa::test::codeObjectFixture(1, true, true);
  hipModule_t module = nullptr;
  REQUIRE((hipModuleLoadData(&module, image.data()) == hipSuccess));
  REQUIRE((hipDeviceReset() == hipSuccess));
  hipPointerAttribute_t attributes{};
  REQUIRE((hipPointerGetAttributes(&attributes, memory) == hipErrorInvalidValue));
  REQUIRE((hipFree(memory) == hipErrorInvalidDevicePointer));
  REQUIRE((hipStreamQuery(stream) == hipErrorInvalidResourceHandle));
  REQUIRE((hipEventQuery(event) == hipErrorInvalidResourceHandle));
  hipFunction_t stale_function = nullptr;
  REQUIRE((hipModuleGetFunction(&stale_function, module, "add") == hipErrorInvalidResourceHandle));
  REQUIRE((hipModuleUnload(module) == hipSuccess));
  void *replacement = nullptr;
  REQUIRE((hipMalloc(&replacement, 64) == hipSuccess));
  REQUIRE((replacement));
  REQUIRE((hipMemsetAsync(replacement, 0, 64, stream) == hipErrorInvalidResourceHandle));
  REQUIRE((hipFree(replacement) == hipSuccess));
}

} // namespace

TEST_CASE("HIP launch-buffer and runtime behavior", "[hip]") {
  REQUIRE((gemstone::hip::dispatchDimensions(dim3{7, 1, 1}, dim3{64, 1, 1}) == 1));
  REQUIRE((gemstone::hip::dispatchDimensions(dim3{7, 1, 1}, dim3{8, 4, 1}) == 2));
  REQUIRE((gemstone::hip::dispatchDimensions(dim3{7, 1, 1}, dim3{8, 4, 2}) == 3));
  REQUIRE((gemstone::hip::dispatchDimensions(dim3{7, 3, 1}, dim3{8, 1, 1}) == 2));
  REQUIRE((gemstone::hip::dispatchDimensions(dim3{7, 3, 2}, dim3{8, 1, 1}) == 3));
  uint32_t packed_value = 0x12345678;
  size_t packed_size = sizeof(packed_value);
  void *extra[] = {
      HIP_LAUNCH_PARAM_BUFFER_POINTER, &packed_value, HIP_LAUNCH_PARAM_BUFFER_SIZE, &packed_size, HIP_LAUNCH_PARAM_END,
  };
  gemstone::hip::LaunchBuffer launch_buffer;
  REQUIRE((gemstone::hip::parseLaunchBuffer(extra, launch_buffer)));
  REQUIRE((launch_buffer.data == &packed_value && launch_buffer.size == sizeof(packed_value)));
  void *malformed[] = {HIP_LAUNCH_PARAM_BUFFER_POINTER, &packed_value, HIP_LAUNCH_PARAM_END};
  REQUIRE((!gemstone::hip::parseLaunchBuffer(malformed, launch_buffer)));
  // Tensile's gfx1030 GEMM declares 112 bytes but supplies a 116-byte packed record.
  // Accept its unused trailer without writing beyond the destination or accepting truncated fields.
  unsigned char packed[116];
  for (size_t index = 0; index < sizeof(packed); ++index)
    packed[index] = static_cast<unsigned char>(index);
  unsigned char destination[120];
  std::memset(destination, 0xa5, sizeof(destination));
  gemstone::hip::LaunchBuffer tensile{packed, sizeof(packed)};
  REQUIRE((tensile.copyTo(destination + 4, 112, 112)));
  REQUIRE((std::memcmp(destination + 4, packed, 112) == 0));
  for (size_t index = 0; index < 4; ++index) {
    REQUIRE((destination[index] == 0xa5));
    REQUIRE((destination[116 + index] == 0xa5));
  }
  std::memset(destination, 0xa5, sizeof(destination));
  tensile.size = 111;
  REQUIRE((!tensile.copyTo(destination + 4, 112, 112)));
  for (unsigned char value : destination)
    REQUIRE((value == 0xa5));
  tensile.size = 112;
  REQUIRE((tensile.copyTo(destination + 4, 112, 112)));
  // A caller need not supply hidden arguments, which the dispatcher initializes separately.
  std::memset(destination, 0, sizeof(destination));
  tensile.size = 80;
  REQUIRE((tensile.copyTo(destination + 4, 112, 80)));
  REQUIRE((std::memcmp(destination + 4, packed, 80) == 0));
  for (size_t index = 84; index < sizeof(destination); ++index)
    REQUIRE((destination[index] == 0));
  std::memset(destination, 0, sizeof(destination));
  tensile.size = 116;
  REQUIRE((tensile.copyTo(destination + 4, 112, 80)));
  REQUIRE((std::memcmp(destination + 4, packed, 80) == 0));
  for (size_t index = 84; index < sizeof(destination); ++index)
    REQUIRE((destination[index] == 0));
  REQUIRE((!tensile.copyTo(destination, 79, 80)));
  tensile.data = nullptr;
  REQUIRE((!tensile.copyTo(destination, 112, 80)));
  REQUIRE((setenv("GEMSTONE_AMDGPU_TARGET", "gfx1010", 1) == 0));
  int major = 0;
  int minor = 0;
  REQUIRE((hipDeviceComputeCapability(&major, &minor, 0) == hipSuccess));
  REQUIRE((major == 10 && minor == 1));
  char name[64]{};
  REQUIRE((hipDeviceGetName(name, sizeof(name), 0) == hipSuccess));
  REQUIRE((std::strcmp(name, "Gemstone gfx1010") == 0));
  hipDeviceProp_tR0600 properties{};
  REQUIRE((hipGetDevicePropertiesR0600(&properties, 0) == hipSuccess));
  REQUIRE((std::strcmp(properties.gcnArchName, "gfx1010") == 0));
  REQUIRE((properties.major == 10 && properties.minor == 1));
  REQUIRE((properties.maxThreadsPerMultiProcessor == 1024));
  REQUIRE((properties.maxSharedMemoryPerMultiProcessor == 64U * 1024U));

  unsetenv("GEMSTONE_RPC_PORT");
  unsetenv("GEMSTONE_RPC_CLIENT");
  int count = -1;
  REQUIRE((hipGetDeviceCount(&count) == hipErrorNoDevice));
  REQUIRE((count == 0));
  REQUIRE((hipGetLastError() == hipErrorNoDevice));
  REQUIRE((hipMemAdvise(nullptr, 0, hipMemAdviseSetReadMostly, 0) == hipErrorNoDevice));
  REQUIRE((hipMemAdvise(nullptr, 0, hipMemAdviseSetAccessedBy, 0) == hipErrorNoDevice));
  REQUIRE((hipMemAdvise(nullptr, 0, hipMemAdviseUnsetAccessedBy, 0) == hipErrorNoDevice));
  crossWorkgroupLaunchAndCopyback();
}

TEST_CASE("HIP registered host copies", "[hip]") { registeredHostCopy(); }

TEST_CASE("HIP pointer attributes", "[hip]") { pointerAttributes(); }

TEST_CASE("HIP device-reset lifetime", "[hip]") { deviceResetLifetime(); }
