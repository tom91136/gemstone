#include <array>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <future>
#include <limits>
#include <string>
#include <utility>
#include <vector>

#include <dlfcn.h>

#include <catch2/catch_test_macros.hpp>
#include <hip/hip_ext.h>
#include <hip/hip_runtime_api.h>

#include <gemstone/bytes.hpp>
#include <gemstone/test_support.hpp>

#include "../../hsa/test/code_object_fixture.hpp"
#include "../../rpc/test/queued_lifetime.hpp"
#include "../../rpc/test/rpc_fixture.hpp"
#include <environment_keys.hpp>

namespace {

TEST_CASE("HIP legacy device properties symbol", "[hip]") {
  using LegacyProperties = hipError_t (*)(void *, int);
  const auto query = reinterpret_cast<LegacyProperties>(dlsym(RTLD_DEFAULT, "hipGetDeviceProperties"));
  REQUIRE((query != nullptr));
  REQUIRE((setenv(gemstone::environment::TargetArch, "gfx1036", 1) == 0));
  std::array<unsigned char, 792> properties{};
  REQUIRE((query(properties.data(), 0) == hipSuccess));
  auto word = [&](size_t offset) {
    int value = 0;
    std::memcpy(&value, properties.data() + offset, sizeof(value));
    return value;
  };
  REQUIRE((std::strcmp(reinterpret_cast<const char *>(properties.data()), "Gemstone gfx1036") == 0));
  REQUIRE((word(276) == 32));      // warpSize in the unversioned HIP ABI
  REQUIRE((word(336) == 1));       // multiProcessorCount
  REQUIRE((word(392) == 1036));    // gcnArch
  REQUIRE((word(356) & (1 << 7))); // arch.hasDoubles
  REQUIRE((word(356) & (1 << 5))); // arch.hasGlobalInt64Atomics
  REQUIRE((setenv(gemstone::environment::SystemPaging, "1", 1) == 0));
  REQUIRE((query(properties.data(), 0) == hipSuccess));
  REQUIRE((word(784) == 1)); // pageableMemoryAccess in Polyregion's 792-byte ABI
  hipDeviceProp_tR0600 current{};
  REQUIRE((hipGetDevicePropertiesR0600(&current, 0) == hipSuccess));
  REQUIRE((current.warpSize == 32));
  int warpSize = 0;
  REQUIRE((hipDeviceGetAttribute(&warpSize, hipDeviceAttributeWarpSize, 0) == hipSuccess));
  REQUIRE((warpSize == 32));
  REQUIRE((current.pageableMemoryAccess == 1));
  REQUIRE((current.managedMemory == 1));
  REQUIRE((current.concurrentManagedAccess == 1));
  REQUIRE((current.hostNativeAtomicSupported == 1));
  REQUIRE((word(772) == current.managedMemory));
  REQUIRE((word(780) == current.concurrentManagedAccess));
  REQUIRE((unsetenv(gemstone::environment::SystemPaging) == 0));
  REQUIRE((unsetenv(gemstone::environment::TargetArch) == 0));
}

TEST_CASE("HIP wave size follows the selected processor", "[hip]") {
  for (const auto &[target, expected] : std::array<std::pair<const char *, int>, 6>{
           {{"gfx900", 64}, {"gfx90a", 64}, {"gfx1036", 32}, {"gfx1200", 32}, {"gfx1250", 32}, {"gfx1251", 32}}}) {
    const gemstone_test::Environment selected(gemstone::environment::TargetArch, target);
    INFO(target);
    int width = 0;
    REQUIRE((hipDeviceGetAttribute(&width, hipDeviceAttributeWarpSize, 0) == hipSuccess));
    CHECK((width == expected));
    hipDeviceProp_tR0600 properties{};
    REQUIRE((hipGetDevicePropertiesR0600(&properties, 0) == hipSuccess));
    CHECK((properties.warpSize == expected));
  }
}

TEST_CASE("HIP primary context can be pushed", "[hip]") {
  gemstone_test::RpcFixture fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  REQUIRE((hipInit(0) == hipSuccess));
  hipCtx_t context = nullptr;
  REQUIRE((hipDevicePrimaryCtxRetain(&context, 0) == hipSuccess));
  REQUIRE((context != nullptr));
  REQUIRE((hipCtxPushCurrent(context) == hipSuccess));
  hipCtx_t popped = nullptr;
  REQUIRE((hipCtxPopCurrent(&popped) == hipSuccess));
  REQUIRE((popped == context));
  REQUIRE((hipDevicePrimaryCtxRelease(0) == hipSuccess));
}

TEST_CASE("HIP stream callback reports completed work", "[hip]") {
  hipStream_t stream = nullptr;
  REQUIRE((hipStreamCreate(&stream) == hipSuccess));
  int calls = 0;
  REQUIRE(
      (hipStreamAddCallback(
           stream, [](hipStream_t, hipError_t status, void *data) { *static_cast<int *>(data) = status == hipSuccess ? 1 : -1; }, &calls, 0)
       == hipSuccess));
  REQUIRE((hipStreamSynchronize(stream) == hipSuccess));
  REQUIRE((calls == 1));
  REQUIRE((hipStreamDestroy(stream) == hipSuccess));
}

TEST_CASE("HIP driver-style asynchronous copies use the stream", "[hip]") {
  gemstone_test::RpcFixture fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  void *first = nullptr;
  void *second = nullptr;
  REQUIRE((hipMalloc(&first, sizeof(uint32_t)) == hipSuccess));
  REQUIRE((hipMalloc(&second, sizeof(uint32_t)) == hipSuccess));
  hipStream_t stream = nullptr;
  REQUIRE((hipStreamCreate(&stream) == hipSuccess));
  uint32_t input = 42;
  uint32_t output = 0;
  REQUIRE((hipMemcpyHtoDAsync(reinterpret_cast<hipDeviceptr_t>(first), &input, sizeof(input), stream) == hipSuccess));
  REQUIRE((hipMemcpyDtoDAsync(reinterpret_cast<hipDeviceptr_t>(second), reinterpret_cast<hipDeviceptr_t>(first), sizeof(input), stream)
           == hipSuccess));
  REQUIRE((hipMemcpyDtoHAsync(&output, reinterpret_cast<hipDeviceptr_t>(second), sizeof(output), stream) == hipSuccess));
  REQUIRE((output == input));
  REQUIRE((hipStreamDestroy(stream) == hipSuccess));
  REQUIRE((hipFree(second) == hipSuccess));
  REQUIRE((hipFree(first) == hipSuccess));
}

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
  expected.localYz = {shape.local.y, shape.local.z};
  expected.groupsYz = {shape.groups.y, shape.groups.z};
  expected.globalYz = {shape.global.y, shape.global.z};
  expected.geometryOnly = geometryOnly;
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
  // Block-only y/z extents still select a two- or three-dimensional dispatch.
  const std::array<LaunchShape, 11> shapes{{{{8, 15, 1}, {4, 5, 1}, {4, 5, 1}, {2, 3, 1}, false},
                                            {{56, 4, 1}, {8, 4, 1}, {8, 4, 1}, {7, 1, 1}, false},
                                            {{56, 4, 2}, {8, 4, 2}, {8, 4, 2}, {7, 1, 1}, false},
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
  constexpr uint32_t signedMax = std::numeric_limits<int32_t>::max();
  for (const auto &shape : std::array<LaunchShape, 4>{{{{signedMax, 1, 1}, {4, 1, 1}, {4, 1, 1}, {536870912U, 1, 1}, true},
                                                       {{1, signedMax, 1}, {1, 4, 1}, {1, 4, 1}, {1, 536870912U, 1}, true},
                                                       {{1, 1, signedMax}, {1, 1, 4}, {1, 1, 4}, {1, 1, 536870912U}, true},
                                                       {{signedMax, 1, 1}, {1, 1, 1}, {1, 1, 1}, {signedMax, 1, 1}, false}}}) {
    fixture.expect(expectedShape(shape, seed, true));
    fixture.expectTrailingArguments(implicitArguments(shape.global, shape.local));
    REQUIRE((launchShape(function, shape, arguments) == hipSuccess));
    REQUIRE((hipDeviceSynchronize() == hipSuccess));
    fixture.check();
  }

  // Advertised maxThreadsPerBlock/maxThreadsDim are 1024. Invalid requests must stop before RPC launch.
  hipDeviceProp_tR0600 caps{};
  REQUIRE((hipGetDevicePropertiesR0600(&caps, 0) == hipSuccess));
  REQUIRE(
      (caps.maxThreadsPerBlock == 1024 && caps.maxThreadsDim[0] == 1024 && caps.maxThreadsDim[1] == 1024 && caps.maxThreadsDim[2] == 1024));
  const auto before = fixture.launches.load();
  for (bool extended : {false, true}) {
    for (const dim3 local :
         {dim3{1025, 1, 1}, dim3{1, 1025, 1}, dim3{1, 1, 1025}, dim3{33, 33, 1}, dim3{65536, 65536, 1}, dim3{maximum, 1, 1}}) {
      LaunchShape invalid{local, local, local, {1, 1, 1}, extended};
      fixture.expect(expectedShape(invalid, seed, true));
      const auto status = launchShape(function, invalid, arguments);
      if (status != hipErrorInvalidConfiguration)
        std::fprintf(stderr, "invalid block %ux%ux%u extended=%d returned=%d\n", local.x, local.y, local.z, extended, status);
      REQUIRE((status == hipErrorInvalidConfiguration));
      REQUIRE((fixture.launches.load() == before));
    }
  }
  for (unsigned axis = 0; axis < 3; ++axis) {
    const auto axisValue = [axis](uint32_t value) {
      dim3 result{1, 1, 1};
      if (axis == 0) result.x = value;
      else if (axis == 1) result.y = value;
      else result.z = value;
      return result;
    };
    dim3 zero = axisValue(0);
    LaunchShape localZero{{1, 1, 1}, zero, zero, {1, 1, 1}, false};
    LaunchShape gridZero{zero, {1, 1, 1}, {1, 1, 1}, zero, false};
    REQUIRE((launchShape(function, localZero, arguments) == hipErrorInvalidConfiguration));
    REQUIRE((launchShape(function, gridZero, arguments) == hipErrorInvalidConfiguration));
    localZero.extended = true;
    gridZero.extended = true;
    REQUIRE((launchShape(function, localZero, arguments) == hipErrorInvalidConfiguration));
    REQUIRE((launchShape(function, gridZero, arguments) == hipErrorInvalidConfiguration));
    dim3 grid = axisValue(signedMax);
    dim3 block = axisValue(4);
    LaunchShape overflow{{1, 1, 1}, block, block, grid, false};
    REQUIRE((launchShape(function, overflow, arguments) == hipErrorInvalidConfiguration));
    LaunchShape excessiveGrid{axisValue(maximum), {1, 1, 1}, {1, 1, 1}, axisValue(maximum), false};
    fixture.expect(expectedShape(excessiveGrid, seed, true));
    REQUIRE((launchShape(function, excessiveGrid, arguments) == hipErrorInvalidConfiguration));
    excessiveGrid.extended = true;
    REQUIRE((launchShape(function, excessiveGrid, arguments) == hipErrorInvalidConfiguration));
  }
  for (uint32_t width : {1025U, 2048U}) {
    LaunchShape oversizedNominal{{1, 1, 1}, {width, 1, 1}, {1, 1, 1}, {1, 1, 1}, true};
    fixture.expect(expectedShape(oversizedNominal, seed, true));
    REQUIRE((launchShape(function, oversizedNominal, arguments) == hipErrorInvalidConfiguration));
  }
  REQUIRE((fixture.launches.load() == before));
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
  const gemstone_test::RpcEnvironment environment(fixture);
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
  unsigned int functionCount = 0;
  REQUIRE((hipModuleGetFunctionCount(&functionCount, module) == hipSuccess));
  REQUIRE((functionCount == 1));
  REQUIRE((hipModuleGetFunction(&function, module, "add") == hipSuccess));
  for (bool packed : {false, true})
    for (uint32_t groups : {1U, 2U, 3U, 5U, 9U})
      for (uint32_t width : {31U, 32U, 33U})
        for (uint32_t tail : {0U, 1U}) {
          if (tail && !packed) continue;
          const uint32_t count = width * groups - tail;
          const uint32_t effectiveWidth = std::min(width, count);
          void *allocation = nullptr;
          REQUIRE((hipMalloc(&allocation, (count + 2) * sizeof(uint32_t)) == hipSuccess));
          auto *output = static_cast<uint32_t *>(allocation) + 1;
          for (uint32_t seed : {7U, 29U}) {
            std::vector<uint32_t> observed(count + 2, 0xa5a5a5a5U);
            REQUIRE((hipMemcpy(allocation, observed.data(), observed.size() * sizeof(uint32_t), hipMemcpyHostToDevice) == hipSuccess));
            fixture.expect({effectiveWidth, groups, count, 0, seed});
            // Bytes after output+seed include alignment, V5 implicit fields, and untouched reserved bytes.
            std::vector<uint8_t> implicit(76, 0);
            gemstone::writeNative(implicit.data(), 4, count / effectiveWidth);
            gemstone::writeNative<uint32_t>(implicit.data(), 8, 1);
            gemstone::writeNative<uint32_t>(implicit.data(), 12, 1);
            gemstone::writeNative(implicit.data(), 16, static_cast<uint16_t>(effectiveWidth));
            gemstone::writeNative<uint16_t>(implicit.data(), 18, 1);
            gemstone::writeNative<uint16_t>(implicit.data(), 20, 1);
            gemstone::writeNative(implicit.data(), 22, static_cast<uint16_t>(count % effectiveWidth));
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
  REQUIRE((fixture.launches.load() == 90));
  {
    // Packed buffers must name both pointer and size, and cover the declared explicit arguments.
    std::array<uint8_t, 12> arguments{};
    size_t size = arguments.size() - 1;
    void *truncated[] = {HIP_LAUNCH_PARAM_BUFFER_POINTER, arguments.data(), HIP_LAUNCH_PARAM_BUFFER_SIZE, &size, HIP_LAUNCH_PARAM_END};
    void *malformed[] = {HIP_LAUNCH_PARAM_BUFFER_POINTER, arguments.data(), HIP_LAUNCH_PARAM_END};
    size_t complete = arguments.size();
    void *absent[] = {HIP_LAUNCH_PARAM_BUFFER_POINTER, nullptr, HIP_LAUNCH_PARAM_BUFFER_SIZE, &complete, HIP_LAUNCH_PARAM_END};
    for (void **extra : {truncated, malformed, absent})
      REQUIRE((hipModuleLaunchKernel(function, 1, 1, 1, 1, 1, 1, 0, nullptr, nullptr, extra) == hipErrorInvalidValue));
  }
  REQUIRE((fixture.launches.load() == 90));
  asymmetricAndBoundaryLaunches(fixture, function);
  REQUIRE((fixture.launches.load() == 116));
  // HIP advertises pageable host access to Polyregion. A stream wait must
  // expose writes to an ordinary host allocation before it returns.
  REQUIRE((setenv(gemstone::environment::SystemPaging, "1", 1) == 0));
  for (uint32_t seed = 1; seed <= 32; ++seed) {
    std::array<uint32_t, 4> host{0, 0, 0, 0};
    auto *output = host.data();
    void *arguments[] = {&output, &seed};
    fixture.expect({4, 1, 4, 0, seed});
    fixture.expectTrailingArguments(implicitArguments(dim3{4, 1, 1}, dim3{4, 1, 1}));
    REQUIRE((hipModuleLaunchKernel(function, 1, 1, 1, 4, 1, 1, 0, nullptr, arguments, nullptr) == hipSuccess));
    REQUIRE((hipStreamSynchronize(nullptr) == hipSuccess));
    for (uint32_t index = 0; index < host.size(); ++index)
      REQUIRE((host[index] == seed + index));
    fixture.check();
  }
  REQUIRE((unsetenv(gemstone::environment::SystemPaging) == 0));
  REQUIRE((fixture.launches.load() == 148));
  {
    // Packed records may omit hidden arguments, or extend past the kernarg segment like Tensile's four-byte trailer.
    // Only the explicit arguments are consumed either way.
    void *allocation = nullptr;
    REQUIRE((hipMalloc(&allocation, 4 * sizeof(uint32_t)) == hipSuccess));
    for (size_t size : {size_t(12), size_t(92)}) {
      std::vector<uint8_t> arguments(size, 0xa5);
      const auto seed = static_cast<uint32_t>(size);
      std::memcpy(arguments.data(), &allocation, 8);
      std::memcpy(arguments.data() + 8, &seed, 4);
      void *extra[] = {HIP_LAUNCH_PARAM_BUFFER_POINTER, arguments.data(), HIP_LAUNCH_PARAM_BUFFER_SIZE, &size, HIP_LAUNCH_PARAM_END};
      fixture.expect({4, 1, 4, 0, seed});
      fixture.expectTrailingArguments(implicitArguments(dim3{4, 1, 1}, dim3{4, 1, 1}));
      REQUIRE((hipModuleLaunchKernel(function, 1, 1, 1, 4, 1, 1, 0, nullptr, nullptr, extra) == hipSuccess));
      REQUIRE((hipStreamSynchronize(nullptr) == hipSuccess));
      fixture.check();
    }
    REQUIRE((hipFree(allocation) == hipSuccess));
  }
  REQUIRE((fixture.launches.load() == 150));
  REQUIRE((hipModuleUnload(module) == hipSuccess));
  fixture.check();
}

void registeredHostCopy() {
  gemstone_test::RpcFixture fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
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
  REQUIRE((fixture.launches.load() == 0));
}

void pointerAttributes() {
  gemstone_test::RpcFixture fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
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
  REQUIRE((fixture.launches.load() == 0));
}

void deviceResetLifetime() {
  gemstone_test::RpcFixture fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
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
  hipFunction_t staleFunction = nullptr;
  REQUIRE((hipModuleGetFunction(&staleFunction, module, "add") == hipErrorInvalidResourceHandle));
  REQUIRE((hipModuleUnload(module) == hipSuccess));
  void *replacement = nullptr;
  REQUIRE((hipMalloc(&replacement, 64) == hipSuccess));
  REQUIRE((replacement));
  REQUIRE((hipMemsetAsync(replacement, 0, 64, stream) == hipErrorInvalidResourceHandle));
  REQUIRE((hipFree(replacement) == hipSuccess));
}

} // namespace

TEST_CASE("HIP launch-buffer and runtime behaviour", "[hip]") {
  REQUIRE((setenv(gemstone::environment::TargetArch, "gfx1010", 1) == 0));
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
  REQUIRE((properties.unifiedAddressing == 1));
  REQUIRE((properties.canMapHostMemory == 1));
  int unified{}, mapped{};
  REQUIRE((hipDeviceGetAttribute(&unified, hipDeviceAttributeUnifiedAddressing, 0) == hipSuccess));
  REQUIRE((hipDeviceGetAttribute(&mapped, hipDeviceAttributeCanMapHostMemory, 0) == hipSuccess));
  REQUIRE((unified == properties.unifiedAddressing));
  REQUIRE((mapped == properties.canMapHostMemory));
  REQUIRE((properties.maxThreadsPerMultiProcessor == 1024));
  REQUIRE((properties.maxSharedMemoryPerMultiProcessor == 64U * 1024U));

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

TEST_CASE("HIP queued failures are reported to the synchronising thread", "[hip]") {
  gemstone_test::RpcFixture fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  const auto image = gemstone::hsa::test::codeObjectFixture(1, true, true);
  hipModule_t module{};
  hipFunction_t function{};
  REQUIRE((hipModuleLoadData(&module, image.data()) == hipSuccess));
  REQUIRE((hipModuleGetFunction(&function, module, "add") == hipSuccess));
  void *output = nullptr;
  REQUIRE((hipMalloc(&output, sizeof(uint32_t)) == hipSuccess));
  // Queue workers are pooled, so repeat until a callback is likely to share the failed launch's worker.
  for (uint32_t seed = 1; seed <= 16; ++seed) {
    fixture.expect({1, 1, 1, 0, seed});
    fixture.expectTrailingArguments(implicitArguments(dim3{1, 1, 1}, dim3{1, 1, 1}));
    fixture.failNextCompletion();
    void *arguments[] = {&output, &seed};
    REQUIRE((hipModuleLaunchKernel(function, 1, 1, 1, 1, 1, 1, 0, nullptr, arguments, nullptr) == hipSuccess));
    hipError_t observed = hipErrorUnknown;
    REQUIRE(
        (hipStreamAddCallback(
             nullptr, [](hipStream_t, hipError_t, void *data) { *static_cast<hipError_t *>(data) = hipPeekAtLastError(); }, &observed, 0)
         == hipSuccess));
    REQUIRE((hipDeviceSynchronize() == hipErrorLaunchFailure));
    REQUIRE((observed == hipSuccess));
    REQUIRE((hipGetLastError() == hipErrorLaunchFailure));
    REQUIRE((hipGetLastError() == hipSuccess));
  }
  REQUIRE((hipFree(output) == hipSuccess));
  REQUIRE((hipModuleUnload(module) == hipSuccess));
}

TEST_CASE("HIP pointer attributes", "[hip]") { pointerAttributes(); }

TEST_CASE("HIP device-reset lifetime", "[hip]") { deviceResetLifetime(); }

TEST_CASE("HIP registered host memory exposes its device pointer", "[hip][unified-memory]") {
  gemstone_test::RpcFixture fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  REQUIRE((unsetenv(gemstone::environment::SystemPaging) == 0));
  std::array<uint32_t, 8> host{11, 22, 33, 44, 55, 66, 77, 88};
  REQUIRE((hipHostRegister(host.data(), sizeof(host), hipHostRegisterMapped) == hipSuccess));
  void *device = nullptr;
  const auto status = hipHostGetDevicePointer(&device, host.data(), 0);
  CHECK((status == hipSuccess));
  if (status == hipSuccess) {
    uint32_t observed = 0;
    REQUIRE((hipMemcpy(&observed, static_cast<uint32_t *>(device) + 2, sizeof(observed), hipMemcpyDeviceToHost) == hipSuccess));
    CHECK((observed == host[2]));
  }
  REQUIRE((hipHostUnregister(host.data()) == hipSuccess));
}

TEST_CASE("HIP allocated mapped host memory exposes its device pointer", "[hip][unified-memory]") {
  gemstone_test::RpcFixture fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  void *host = nullptr;
  REQUIRE((hipHostMalloc(&host, 64, hipHostMallocMapped) == hipSuccess));
  void *device = nullptr;
  CHECK((hipHostGetDevicePointer(&device, host, 0) == hipSuccess));
  CHECK((device != nullptr));
  REQUIRE((hipHostFree(host) == hipSuccess));
}

TEST_CASE("HIP registered host flags round trip", "[hip][unified-memory]") {
  gemstone_test::RpcFixture fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  std::array<uint32_t, 8> host{};
  const unsigned requested = hipHostRegisterMapped | hipHostRegisterPortable;
  REQUIRE((hipHostRegister(host.data(), sizeof(host), requested) == hipSuccess));
  unsigned flags = 0;
  CHECK((hipHostGetFlags(&flags, host.data()) == hipSuccess));
  CHECK((flags == requested));
  REQUIRE((hipHostUnregister(host.data()) == hipSuccess));
}

TEST_CASE("HIP registered subranges retain ownership and permit address reuse", "[hip][unified-memory]") {
  gemstone_test::RpcFixture fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  REQUIRE((unsetenv(gemstone::environment::SystemPaging) == 0));
  std::array<uint32_t, 16> host{};
  for (unsigned iteration = 0; iteration < 3; ++iteration) {
    REQUIRE((hipHostRegister(host.data() + 1, 4 * sizeof(uint32_t), 0) == hipSuccess));
    REQUIRE((hipHostRegister(host.data() + 9, 4 * sizeof(uint32_t), 0) == hipSuccess));
    REQUIRE((hipHostUnregister(host.data() + 1) == hipSuccess));
    hipPointerAttribute_t info{};
    REQUIRE((hipPointerGetAttributes(&info, host.data() + 10) == hipSuccess));
    CHECK((info.type == hipMemoryTypeHost));
    REQUIRE((hipHostUnregister(host.data() + 9) == hipSuccess));
    host[1] = iteration;
    CHECK((hipPointerGetAttributes(&info, host.data() + 1) == hipErrorInvalidValue));
  }
}

namespace {
void unifiedMemoryRoundTrip(unsigned kind) {
  gemstone_test::RpcFixture fixture;
  fixture.kernelMetadata(12, 2, {{8, 0, 2, 0}, {4, 8, 1, 0}});
  const gemstone_test::RpcEnvironment environment(fixture);
  REQUIRE((unsetenv(gemstone::environment::SystemPaging) == 0));
  std::array<uint32_t, 16> storage{};
  void *memory = storage.data();
  if (kind == 0) REQUIRE((hipMallocManaged(&memory, sizeof(storage)) == hipSuccess));
  else if (kind == 1) REQUIRE((hipHostMalloc(&memory, sizeof(storage), 0) == hipSuccess));
  else REQUIRE((hipHostRegister(memory, sizeof(storage), 0) == hipSuccess));
  fixture.checkHostBacked(memory);
  const auto image = gemstone::hsa::test::codeObjectFixture(1, true, true);
  hipModule_t module = nullptr;
  hipFunction_t kernel = nullptr;
  REQUIRE((hipModuleLoadData(&module, image.data()) == hipSuccess));
  REQUIRE((hipModuleGetFunction(&kernel, module, "add") == hipSuccess));
  auto *host = static_cast<uint32_t *>(memory);
  for (uint32_t seed : {11U, 71U, 93U}) {
    std::fill_n(host, 16, 0xa5a5a5a5U);
    const uint64_t input = uint64_t(seed) * 0x100000001ULL;
    std::memcpy(host + 8, &input, sizeof(input));
    auto *interior = host + 1;
    void *arguments[] = {&interior, &seed};
    fixture.expect({4, 1, 4, 0, seed});
    REQUIRE((hipModuleLaunchKernel(kernel, 1, 1, 1, 4, 1, 1, 0, nullptr, arguments, nullptr) == hipSuccess));
    REQUIRE((hipDeviceSynchronize() == hipSuccess));
    for (unsigned index = 0; index < 4; ++index)
      CHECK((host[index + 1] == seed + index));
    CHECK((host[0] == 0xa5a5a5a5U && host[5] == 0xa5a5a5a5U));
    fixture.checkAllocationWord(memory, 8 * sizeof(uint32_t), input);
    fixture.check();
  }
  REQUIRE((hipModuleUnload(module) == hipSuccess));
  if (kind == 0) REQUIRE((hipFree(memory) == hipSuccess));
  else if (kind == 1) REQUIRE((hipHostFree(memory) == hipSuccess));
  else REQUIRE((hipHostUnregister(memory) == hipSuccess));
}
} // namespace

TEST_CASE("HIP managed interior kernel writes and host edits round trip", "[hip][unified-memory]") { unifiedMemoryRoundTrip(0); }

TEST_CASE("HIP pinned interior kernel writes and host edits round trip", "[hip][unified-memory]") { unifiedMemoryRoundTrip(1); }

TEST_CASE("HIP registered interior kernel writes and host edits round trip", "[hip][unified-memory]") { unifiedMemoryRoundTrip(2); }

TEST_CASE("HIP free waits for queued memory uses", "[hip]") {
  gemstone_test::RpcFixture fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  gemstone_test::queuedLifetime(
      [](size_t size) {
        void *p{};
        REQUIRE(hipMalloc(&p, size) == hipSuccess);
        return p;
      },
      [](void *dst, const void *src, size_t size, bool upload) {
        REQUIRE(hipMemcpyAsync(dst, src, size, upload ? hipMemcpyHostToDevice : hipMemcpyDeviceToHost, nullptr) == hipSuccess);
      },
      [](void (*callback)(void *), void *data) {
        struct Callback {
          void (*function)(void *);
          void *data;
        };
        auto *owned = new Callback{callback, data};
        REQUIRE(hipStreamAddCallback(
                    nullptr,
                    [](hipStream_t, hipError_t, void *p) {
                      std::unique_ptr<Callback> callback(static_cast<Callback *>(p));
                      callback->function(callback->data);
                    },
                    owned, 0)
                == hipSuccess);
      },
      [](void *p) { return hipFree(p); });
}
