#include <algorithm>
#include <array>
#include <bit>
#include <cstdint>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <limits>
#include <memory>
#include <string>
#include <utility>
#include <vector>

#include <dlfcn.h>

#include <catch2/catch_test_macros.hpp>
#include <hip/hip_ext.h>
#include <hip/hip_runtime_api.h>

#include <gemstone/bytes.hpp>
#include <gemstone/test_support.hpp>

#include "../../generated/rpc/gemstone_execution_rpc.hpp"
#include "../../hsa/test/code_object_fixture.hpp"
#include "../../rpc/test/queued_lifetime.hpp"
#include "../../rpc/test/rpc_fixture.hpp"
#include <environment_keys.hpp>

namespace {

// hip_runtime_api.h spells these sentinels as C-style casts
void *const LaunchParamBufferPointer = std::bit_cast<void *>(uintptr_t{0x01});
void *const LaunchParamBufferSize = std::bit_cast<void *>(uintptr_t{0x02});
void *const LaunchParamEnd = std::bit_cast<void *>(uintptr_t{0x03});

TEST_CASE("HIP legacy device properties symbol", "[hip]") {
  using LegacyProperties = hipError_t (*)(void *, int);
  const auto query = reinterpret_cast<LegacyProperties>(dlsym(RTLD_DEFAULT, "hipGetDeviceProperties"));
  REQUIRE((query != nullptr));
  const gemstone_test::Environment target(gemstone::environment::TargetArch, "gfx1036");
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
  REQUIRE((word(656) == 1));       // cooperativeLaunch
  REQUIRE((word(356) & (1 << 7))); // arch.hasDoubles
  REQUIRE((word(356) & (1 << 5))); // arch.hasGlobalInt64Atomics
  const gemstone_test::Environment paging(gemstone::environment::SystemPaging, "1");
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

TEST_CASE("HIP module function lookups of a missing symbol report it not found", "[hip]") {
  // gfx1036 (ROCm 7.1.1): hipModuleGetFunction of a name the loaded code object lacks returns hipErrorNotFound, which
  // hipGetLastError then reports too.
  gemstone_test::RpcFixture const fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  const auto image = gemstone::hsa::test::codeObjectFixture(1, true, true);
  hipModule_t module = nullptr;
  REQUIRE((hipModuleLoadData(&module, image.data()) == hipSuccess));
  hipFunction_t function = nullptr;
  CHECK((hipModuleGetFunction(&function, module, "absent") == hipErrorNotFound));
  CHECK((function == nullptr));
  CHECK((hipGetLastError() == hipErrorNotFound));
  REQUIRE((hipModuleGetFunction(&function, module, "add") == hipSuccess));
  REQUIRE((hipModuleUnload(module) == hipSuccess));
}

TEST_CASE("HIP primary context can be pushed", "[hip]") {
  gemstone_test::RpcFixture const fixture;
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
  gemstone_test::RpcFixture const fixture;
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
  bool extended{};
};

uint16_t dimensions(const dim3 &global) {
  if (global.z > 1) return 3;
  if (global.y > 1) return 2;
  return 1;
}

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
  gemstone::writeNative(bytes.data(), 68, dimensions(global));
  return bytes;
}

gemstone_test::RpcFixture::Expected expectedShape(const LaunchShape &shape, uint32_t seed, bool geometryOnly = false) {
  gemstone_test::RpcFixture::Expected expected{
      .local = shape.local.x, .groups = shape.groups.x, .global = shape.global.x, .offset = 0, .seed = seed};
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
  const std::array<LaunchShape, 11> shapes{
      {{.global = {8, 15, 1}, .requested = {4, 5, 1}, .local = {4, 5, 1}, .groups = {2, 3, 1}, .extended = false},
       {.global = {56, 4, 1}, .requested = {8, 4, 1}, .local = {8, 4, 1}, .groups = {7, 1, 1}, .extended = false},
       {.global = {56, 4, 2}, .requested = {8, 4, 2}, .local = {8, 4, 2}, .groups = {7, 1, 1}, .extended = false},
       {.global = {8, 9, 10}, .requested = {4, 3, 2}, .local = {4, 3, 2}, .groups = {2, 3, 5}, .extended = false},
       {.global = {32, 32, 1}, .requested = {32, 32, 1}, .local = {32, 32, 1}, .groups = {1, 1, 1}, .extended = false},
       {.global = {16, 8, 8}, .requested = {16, 8, 8}, .local = {16, 8, 8}, .groups = {1, 1, 1}, .extended = false},
       {.global = {9, 13, 1}, .requested = {4, 5, 1}, .local = {4, 5, 1}, .groups = {3, 3, 1}, .extended = true},
       {.global = {10, 11, 1}, .requested = {3, 4, 1}, .local = {3, 4, 1}, .groups = {4, 3, 1}, .extended = true},
       {.global = {9, 7, 5}, .requested = {4, 3, 2}, .local = {4, 3, 2}, .groups = {3, 3, 3}, .extended = true},
       {.global = {2, 8, 3}, .requested = {4, 3, 5}, .local = {2, 3, 3}, .groups = {1, 3, 1}, .extended = true},
       {.global = {7, 2, 9}, .requested = {3, 5, 4}, .local = {3, 2, 4}, .groups = {3, 1, 3}, .extended = true}}};
  for (const auto &shape : shapes)
    for (uint32_t seed : {11U, 37U}) {
      const size_t count = static_cast<size_t>(shape.global.x) * shape.global.y * shape.global.z;
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
  for (const auto &shape : std::array<LaunchShape, 4>{
           {{.global = {signedMax, 1, 1}, .requested = {4, 1, 1}, .local = {4, 1, 1}, .groups = {536870912U, 1, 1}, .extended = true},
            {.global = {1, signedMax, 1}, .requested = {1, 4, 1}, .local = {1, 4, 1}, .groups = {1, 536870912U, 1}, .extended = true},
            {.global = {1, 1, signedMax}, .requested = {1, 1, 4}, .local = {1, 1, 4}, .groups = {1, 1, 536870912U}, .extended = true},
            {.global = {signedMax, 1, 1}, .requested = {1, 1, 1}, .local = {1, 1, 1}, .groups = {signedMax, 1, 1}, .extended = false}}}) {
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
  for (bool const extended : {false, true}) {
    for (const dim3 local :
         {dim3{1025, 1, 1}, dim3{1, 1025, 1}, dim3{1, 1, 1025}, dim3{33, 33, 1}, dim3{65536, 65536, 1}, dim3{maximum, 1, 1}}) {
      LaunchShape const invalid{.global = local, .requested = local, .local = local, .groups = {1, 1, 1}, .extended = extended};
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
    dim3 const zero = axisValue(0);
    LaunchShape localZero{.global = {1, 1, 1}, .requested = zero, .local = zero, .groups = {1, 1, 1}, .extended = false};
    LaunchShape gridZero{.global = zero, .requested = {1, 1, 1}, .local = {1, 1, 1}, .groups = zero, .extended = false};
    REQUIRE((launchShape(function, localZero, arguments) == hipErrorInvalidConfiguration));
    REQUIRE((launchShape(function, gridZero, arguments) == hipErrorInvalidConfiguration));
    localZero.extended = true;
    gridZero.extended = true;
    REQUIRE((launchShape(function, localZero, arguments) == hipErrorInvalidConfiguration));
    REQUIRE((launchShape(function, gridZero, arguments) == hipErrorInvalidConfiguration));
    dim3 const grid = axisValue(signedMax);
    dim3 const block = axisValue(4);
    LaunchShape const overflow{.global = {1, 1, 1}, .requested = block, .local = block, .groups = grid, .extended = false};
    REQUIRE((launchShape(function, overflow, arguments) == hipErrorInvalidConfiguration));
    LaunchShape excessiveGrid{
        .global = axisValue(maximum), .requested = {1, 1, 1}, .local = {1, 1, 1}, .groups = axisValue(maximum), .extended = false};
    fixture.expect(expectedShape(excessiveGrid, seed, true));
    REQUIRE((launchShape(function, excessiveGrid, arguments) == hipErrorInvalidConfiguration));
    excessiveGrid.extended = true;
    REQUIRE((launchShape(function, excessiveGrid, arguments) == hipErrorInvalidConfiguration));
  }
  for (uint32_t width : {1025U, 2048U}) {
    LaunchShape const oversizedNominal{
        .global = {1, 1, 1}, .requested = {width, 1, 1}, .local = {1, 1, 1}, .groups = {1, 1, 1}, .extended = true};
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
                         {{.width = 8, .offset = 0, .kind = 2, .containsPointers = 0},
                          {.width = 4, .offset = 8, .kind = 1, .containsPointers = 1},
                          {.width = 4, .offset = 16, .kind = 1, .containsPointers = 1},
                          {.width = 4, .offset = 20, .kind = 1, .containsPointers = 1},
                          {.width = 4, .offset = 24, .kind = 1, .containsPointers = 1},
                          {.width = 2, .offset = 28, .kind = 1, .containsPointers = 1},
                          {.width = 2, .offset = 30, .kind = 1, .containsPointers = 1},
                          {.width = 2, .offset = 32, .kind = 1, .containsPointers = 1},
                          {.width = 2, .offset = 34, .kind = 1, .containsPointers = 1},
                          {.width = 2, .offset = 36, .kind = 1, .containsPointers = 1},
                          {.width = 2, .offset = 38, .kind = 1, .containsPointers = 1},
                          {.width = 2, .offset = 80, .kind = 1, .containsPointers = 1}});
  const gemstone_test::RpcEnvironment environment(fixture);
  void *managed = nullptr;
  REQUIRE((hipMallocManaged(&managed, 256) == hipSuccess));
  hipPointerAttribute_t attributes{};
  for (size_t const offset : {size_t{0}, size_t{128}, size_t{255}}) {
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
  for (bool const packed : {false, true})
    for (uint32_t const groups : {1U, 2U, 3U, 5U, 9U})
      for (uint32_t const width : {31U, 32U, 33U})
        for (uint32_t const tail : {0U, 1U}) {
          if (tail && !packed) continue;
          const uint32_t count = (width * groups) - tail;
          const uint32_t effectiveWidth = std::min(width, count);
          void *allocation = nullptr;
          REQUIRE((hipMalloc(&allocation, (count + 2) * sizeof(uint32_t)) == hipSuccess));
          auto *output = static_cast<uint32_t *>(allocation) + 1;
          for (uint32_t seed : {7U, 29U}) {
            std::vector<uint32_t> observed(count + 2, 0xa5a5a5a5U);
            REQUIRE((hipMemcpy(allocation, observed.data(), observed.size() * sizeof(uint32_t), hipMemcpyHostToDevice) == hipSuccess));
            fixture.expect({.local = effectiveWidth, .groups = groups, .global = count, .offset = 0, .seed = seed});
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
              void *extra[] = {LaunchParamBufferPointer, arguments.data(), LaunchParamBufferSize, &size, LaunchParamEnd};
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
    void *truncated[] = {LaunchParamBufferPointer, arguments.data(), LaunchParamBufferSize, &size, LaunchParamEnd};
    void *malformed[] = {LaunchParamBufferPointer, arguments.data(), LaunchParamEnd};
    size_t complete = arguments.size();
    void *absent[] = {LaunchParamBufferPointer, nullptr, LaunchParamBufferSize, &complete, LaunchParamEnd};
    for (void **extra : {truncated, malformed, absent})
      REQUIRE((hipModuleLaunchKernel(function, 1, 1, 1, 1, 1, 1, 0, nullptr, nullptr, extra) == hipErrorInvalidValue));
  }
  REQUIRE((fixture.launches.load() == 90));
  asymmetricAndBoundaryLaunches(fixture, function);
  REQUIRE((fixture.launches.load() == 116));
  // HIP advertises pageable host access to Polyregion. A stream wait must
  // expose writes to an ordinary host allocation before it returns.
  {
    const gemstone_test::Environment paging(gemstone::environment::SystemPaging, "1");
    for (uint32_t seed = 1; seed <= 32; ++seed) {
      std::array<uint32_t, 4> host{0, 0, 0, 0};
      auto *output = host.data();
      void *arguments[] = {&output, &seed};
      fixture.expect({.local = 4, .groups = 1, .global = 4, .offset = 0, .seed = seed});
      fixture.expectTrailingArguments(implicitArguments(dim3{4, 1, 1}, dim3{4, 1, 1}));
      REQUIRE((hipModuleLaunchKernel(function, 1, 1, 1, 4, 1, 1, 0, nullptr, arguments, nullptr) == hipSuccess));
      REQUIRE((hipStreamSynchronize(nullptr) == hipSuccess));
      for (uint32_t index = 0; index < host.size(); ++index)
        REQUIRE((host[index] == seed + index));
      fixture.check();
    }
  }
  REQUIRE((fixture.launches.load() == 148));
  {
    // Packed records may omit hidden arguments, or extend past the kernarg segment like Tensile's four-byte trailer.
    // Only the explicit arguments are consumed either way.
    void *allocation = nullptr;
    REQUIRE((hipMalloc(&allocation, 4 * sizeof(uint32_t)) == hipSuccess));
    for (size_t size : {size_t{12}, size_t{92}}) {
      std::vector<uint8_t> arguments(size, 0xa5);
      const auto seed = static_cast<uint32_t>(size);
      std::memcpy(arguments.data(), &allocation, 8);
      std::memcpy(arguments.data() + 8, &seed, 4);
      void *extra[] = {LaunchParamBufferPointer, arguments.data(), LaunchParamBufferSize, &size, LaunchParamEnd};
      fixture.expect({.local = 4, .groups = 1, .global = 4, .offset = 0, .seed = seed});
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
  gemstone_test::RpcFixture const fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  for (bool const pinned : {false, true}) {
    std::array<uint32_t, 4> ordinary{};
    uint32_t *host = ordinary.data();
    if (pinned) REQUIRE((hipHostMalloc(reinterpret_cast<void **>(&host), sizeof(ordinary), 0) == hipSuccess));
    else REQUIRE((hipHostRegister(host, sizeof(ordinary), 0) == hipSuccess));
    void *device = nullptr;
    void *copy = nullptr;
    REQUIRE((hipMalloc(&device, sizeof(ordinary)) == hipSuccess));
    REQUIRE((hipMalloc(&copy, sizeof(ordinary)) == hipSuccess));
    for (uint32_t const seed : {0x11223344U, 0x8899aabbU}) {
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
  gemstone_test::RpcFixture const fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  uint8_t ordinary[32]{};
  hipPointerAttribute_t info{};
  REQUIRE((hipPointerGetAttributes(&info, ordinary) == hipErrorInvalidValue));
  REQUIRE((hipHostRegister(ordinary, sizeof(ordinary), 0) == hipSuccess));
  for (size_t const offset : {size_t{0}, size_t{7}, sizeof(ordinary) - 1}) {
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
    for (size_t const offset : {size_t{0}, size_t{7}, size_t{31}}) {
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
  gemstone_test::RpcFixture const fixture;
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
  const gemstone_test::Environment target(gemstone::environment::TargetArch, "gfx1010");
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
  int unified{};
  int mapped{};
  REQUIRE((hipDeviceGetAttribute(&unified, hipDeviceAttributeUnifiedAddressing, 0) == hipSuccess));
  REQUIRE((hipDeviceGetAttribute(&mapped, hipDeviceAttributeCanMapHostMemory, 0) == hipSuccess));
  REQUIRE((unified == properties.unifiedAddressing));
  REQUIRE((mapped == properties.canMapHostMemory));
  REQUIRE((properties.maxThreadsPerMultiProcessor == 1024));
  REQUIRE((properties.maxSharedMemoryPerMultiProcessor == size_t{64} * 1024));

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

TEST_CASE("HIP cooperative launches fit every block on the device at once", "[hip]") {
  gemstone_test::RpcFixture fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  const gemstone_test::Environment target(gemstone::environment::TargetArch, "gfx1030");
  int cooperative = 0;
  int units = 0;
  REQUIRE((hipDeviceGetAttribute(&cooperative, hipDeviceAttributeCooperativeLaunch, 0) == hipSuccess));
  REQUIRE((hipDeviceGetAttribute(&units, hipDeviceAttributeMultiprocessorCount, 0) == hipSuccess));
  hipDeviceProp_tR0600 properties{};
  REQUIRE((hipGetDevicePropertiesR0600(&properties, 0) == hipSuccess));
  REQUIRE((cooperative == 1 && properties.cooperativeLaunch == 1));
  REQUIRE((units == 1 && properties.multiProcessorCount == units));
  const auto image = gemstone::hsa::test::codeObjectFixture(1, true, true);
  hipModule_t module{};
  hipFunction_t function{};
  REQUIRE((hipModuleLoadData(&module, image.data()) == hipSuccess));
  REQUIRE((hipModuleGetFunction(&function, module, "add") == hipSuccess));
  // 1024 work-items a multiprocessor, in whole wave32 waves, and 64 KiB of LDS
  int blocks = 0;
  REQUIRE((hipModuleOccupancyMaxActiveBlocksPerMultiprocessor(&blocks, function, 256, 0) == hipSuccess));
  REQUIRE((blocks == 4));
  REQUIRE((hipModuleOccupancyMaxActiveBlocksPerMultiprocessor(&blocks, function, 33, 0) == hipSuccess));
  REQUIRE((blocks == 16));
  REQUIRE((hipModuleOccupancyMaxActiveBlocksPerMultiprocessor(&blocks, function, 64, size_t{32} * 1024) == hipSuccess));
  REQUIRE((blocks == 2));
  REQUIRE((hipModuleOccupancyMaxActiveBlocksPerMultiprocessor(&blocks, function, 2048, 0) == hipSuccess));
  REQUIRE((blocks == 0));
  void *output = nullptr;
  REQUIRE((hipMalloc(&output, 1024 * sizeof(uint32_t)) == hipSuccess));
  uint32_t seed = 3;
  void *arguments[] = {&output, &seed};
  REQUIRE((hipModuleLaunchCooperativeKernel(function, 5, 1, 1, 256, 1, 1, 0, nullptr, arguments) == hipErrorCooperativeLaunchTooLarge));
  REQUIRE((std::strcmp(hipGetErrorName(hipGetLastError()), "hipErrorCooperativeLaunchTooLarge") == 0));
  fixture.opaqueLaunches();
  for (const bool resident : {true, false}) {
    REQUIRE(((resident ? hipModuleLaunchCooperativeKernel(function, 2, 2, 1, 256, 1, 1, 0, nullptr, arguments)
                       : hipModuleLaunchKernel(function, 2, 2, 1, 256, 1, 1, 0, nullptr, arguments, nullptr))
             == hipSuccess));
    REQUIRE((hipDeviceSynchronize() == hipSuccess));
    const auto launch = fixture.lastLaunch();
    REQUIRE((launch.cooperative == resident));
    REQUIRE((launch.groupCount.x.value == 2 && launch.groupCount.y.value == 2 && launch.groupSize.x.value == 256));
  }
  REQUIRE((hipFree(output) == hipSuccess));
  REQUIRE((hipModuleUnload(module) == hipSuccess));
}

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
    fixture.expect({.local = 1, .groups = 1, .global = 1, .offset = 0, .seed = seed});
    fixture.failNextCompletion(gemstone::execution::FailureKind::IllegalAddress);
    void *arguments[] = {&output, &seed};
    REQUIRE((hipModuleLaunchKernel(function, 1, 1, 1, 1, 1, 1, 0, nullptr, arguments, nullptr) == hipSuccess));
    hipError_t observed = hipErrorUnknown;
    REQUIRE(
        (hipStreamAddCallback(
             nullptr, [](hipStream_t, hipError_t, void *data) { *static_cast<hipError_t *>(data) = hipPeekAtLastError(); }, &observed, 0)
         == hipSuccess));
    REQUIRE((hipDeviceSynchronize() == hipErrorIllegalAddress));
    REQUIRE((observed == hipSuccess));
    REQUIRE((hipGetLastError() == hipErrorIllegalAddress));
    REQUIRE((hipGetLastError() == hipSuccess));
    fixture.check();
  }
  REQUIRE((hipFree(output) == hipSuccess));
  REQUIRE((hipModuleUnload(module) == hipSuccess));
}

TEST_CASE("HIP queued failures report the error the failure kind names", "[hip]") {
  using gemstone::execution::FailureKind;
  gemstone_test::RpcFixture fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  const auto image = gemstone::hsa::test::codeObjectFixture(1, true, true);
  hipModule_t module{};
  hipFunction_t function{};
  REQUIRE((hipModuleLoadData(&module, image.data()) == hipSuccess));
  REQUIRE((hipModuleGetFunction(&function, module, "add") == hipSuccess));
  void *output = nullptr;
  REQUIRE((hipMalloc(&output, sizeof(uint32_t)) == hipSuccess));
  const struct {
    FailureKind kind;
    hipError_t expected;
  } cases[]{
      {.kind = FailureKind::IllegalAddress, .expected = hipErrorIllegalAddress},
      {.kind = FailureKind::MisalignedAddress, .expected = hipErrorIllegalAddress},
      {.kind = FailureKind::OutOfResources, .expected = hipErrorLaunchOutOfResources},
      {.kind = FailureKind::InvalidProgram, .expected = hipErrorInvalidImage},
      {.kind = FailureKind::InvalidConfiguration, .expected = hipErrorInvalidConfiguration},
      {.kind = FailureKind::IllegalInstruction, .expected = hipErrorLaunchFailure},
      {.kind = FailureKind::Trapped, .expected = hipErrorLaunchFailure},
      {.kind = FailureKind::Failed, .expected = hipErrorLaunchFailure},
  };
  uint32_t seed = 1;
  for (const auto &[kind, expected] : cases) {
    CAPTURE(static_cast<int>(kind));
    fixture.expect({.local = 1, .groups = 1, .global = 1, .offset = 0, .seed = seed});
    fixture.failNextCompletion(kind);
    void *arguments[] = {&output, &seed};
    REQUIRE((hipModuleLaunchKernel(function, 1, 1, 1, 1, 1, 1, 0, nullptr, arguments, nullptr) == hipSuccess));
    CHECK((hipDeviceSynchronize() == expected));
    CHECK((hipGetLastError() == expected));
    fixture.check();
    ++seed;
  }
  REQUIRE((hipFree(output) == hipSuccess));
  REQUIRE((hipModuleUnload(module) == hipSuccess));
}

namespace {

struct BuildSession {
  gemstone_test::RpcFixture fixture;
  const gemstone_test::RpcEnvironment environment{fixture};
  hipModule_t module{};
  hipFunction_t function{};
  void *output = nullptr;
  uint32_t seed = 1;

  BuildSession() {
    const auto image = gemstone::hsa::test::codeObjectFixture(1, true, true);
    REQUIRE((hipModuleLoadData(&module, image.data()) == hipSuccess));
    REQUIRE((hipModuleGetFunction(&function, module, "add") == hipSuccess));
    REQUIRE((hipMalloc(&output, sizeof(uint32_t)) == hipSuccess));
  }
  ~BuildSession() {
    (void)hipFree(output);
    (void)hipModuleUnload(module);
  }
  hipError_t launch() {
    fixture.expect({.local = 1, .groups = 1, .global = 1, .offset = 0, .seed = seed});
    void *arguments[] = {&output, &seed};
    return hipModuleLaunchKernel(function, 1, 1, 1, 1, 1, 1, 0, nullptr, arguments, nullptr);
  }
};

} // namespace

TEST_CASE("HIP kernels that fail to build fail their launch, leaving the following synchronisation clean", "[hip]") {
  using gemstone::execution::FailureKind;
  const struct {
    FailureKind kind;
    hipError_t expected;
  } cases[]{
      {.kind = FailureKind::InvalidProgram, .expected = hipErrorInvalidImage},
      {.kind = FailureKind::Unsupported, .expected = hipErrorNotSupported},
      {.kind = FailureKind::InvalidRequest, .expected = hipErrorInvalidValue},
      {.kind = FailureKind::NotFound, .expected = hipErrorNotFound},
      {.kind = FailureKind::OutOfResources, .expected = hipErrorLaunchOutOfResources},
      {.kind = FailureKind::Failed, .expected = hipErrorLaunchFailure},
  };
  BuildSession session;
  for (const auto &[kind, expected] : cases) {
    CAPTURE(static_cast<int>(kind));
    session.fixture.rejectNext(gemstone::execution::operation::BuildKernel, kind);
    CHECK((session.launch() == expected));
    CHECK((hipGetLastError() == expected));
    CHECK((hipDeviceSynchronize() == hipSuccess));
    CHECK((hipGetLastError() == hipSuccess));
    CHECK((session.fixture.kernelBuilds == 0));
  }
}

TEST_CASE("HIP kernels build again after a rejected build, then stop", "[hip]") {
  BuildSession session;
  CHECK((session.fixture.kernelBuilds == 0));
  session.fixture.rejectNext(gemstone::execution::operation::BuildKernel, gemstone::execution::FailureKind::InvalidProgram);
  CHECK((session.launch() == hipErrorInvalidImage));
  CHECK((session.fixture.kernelBuilds == 0));
  for (uint32_t launch = 0; launch < 3; ++launch) {
    REQUIRE((session.launch() == hipSuccess));
    CHECK((hipDeviceSynchronize() == hipSuccess));
    CHECK((session.fixture.kernelBuilds == 1));
    ++session.seed;
  }
}

TEST_CASE("HIP module loads report a rejected code object synchronously", "[hip]") {
  using gemstone::execution::FailureKind;
  gemstone_test::RpcFixture fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  const auto image = gemstone::hsa::test::codeObjectFixture(1, true, true);
  for (const auto operation : {gemstone::execution::operation::LoadProgram, gemstone::execution::operation::DescribeProgram,
                               gemstone::execution::operation::DescribeKernel}) {
    CAPTURE(std::string(operation));
    hipModule_t module{};
    fixture.rejectNext(operation, FailureKind::InvalidProgram);
    CHECK((hipModuleLoadData(&module, image.data()) == hipErrorInvalidImage));
    CHECK((module == nullptr));
    CHECK((hipGetLastError() == hipErrorInvalidImage));
    CHECK((hipDeviceSynchronize() == hipSuccess));
    CHECK((hipGetLastError() == hipSuccess));
  }
}

TEST_CASE("HIP pointer attributes", "[hip]") { pointerAttributes(); }

TEST_CASE("HIP device-reset lifetime", "[hip]") { deviceResetLifetime(); }

TEST_CASE("HIP registered host memory exposes its device pointer", "[hip][unified-memory]") {
  gemstone_test::RpcFixture const fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  const gemstone_test::Environment paging(gemstone::environment::SystemPaging, nullptr);
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
  gemstone_test::RpcFixture const fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  void *host = nullptr;
  REQUIRE((hipHostMalloc(&host, 64, hipHostMallocMapped) == hipSuccess));
  void *device = nullptr;
  CHECK((hipHostGetDevicePointer(&device, host, 0) == hipSuccess));
  CHECK((device != nullptr));
  REQUIRE((hipHostFree(host) == hipSuccess));
}

TEST_CASE("HIP registered host flags round trip", "[hip][unified-memory]") {
  gemstone_test::RpcFixture const fixture;
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
  gemstone_test::RpcFixture const fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  const gemstone_test::Environment paging(gemstone::environment::SystemPaging, nullptr);
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
  fixture.kernelMetadata(
      12, 2, {{.width = 8, .offset = 0, .kind = 2, .containsPointers = 0}, {.width = 4, .offset = 8, .kind = 1, .containsPointers = 0}});
  const gemstone_test::RpcEnvironment environment(fixture);
  const gemstone_test::Environment paging(gemstone::environment::SystemPaging, nullptr);
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
    const uint64_t input = static_cast<uint64_t>(seed) * 0x100000001ULL;
    std::memcpy(host + 8, &input, sizeof(input));
    auto *interior = host + 1;
    void *arguments[] = {&interior, &seed};
    fixture.expect({.local = 4, .groups = 1, .global = 4, .offset = 0, .seed = seed});
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
  gemstone_test::RpcFixture const fixture;
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
        auto *owned = new Callback{.function = callback, .data = data};
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
