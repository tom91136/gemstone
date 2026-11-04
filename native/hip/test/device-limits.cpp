#include <cstddef>

#include <catch2/catch_test_macros.hpp>
#include <hip/hip_runtime_api.h>

#include "../../rpc/test/rpc_fixture.hpp"

namespace {

constexpr size_t DefaultHeap = size_t{8} << 20;

size_t heapLimit() {
  size_t value = 0;
  REQUIRE((hipDeviceGetLimit(&value, hipLimitMallocHeapSize) == hipSuccess));
  return value;
}

} // namespace

TEST_CASE("HIP malloc heap limit defaults to 8 MiB and follows hipDeviceSetLimit", "[hip][limits]") {
  gemstone_test::RpcFixture const fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  CHECK((heapLimit() == DefaultHeap));
  REQUIRE((hipDeviceSetLimit(hipLimitMallocHeapSize, size_t{64} << 20) == hipSuccess));
  CHECK((heapLimit() == (size_t{64} << 20)));
  REQUIRE((hipDeviceSetLimit(hipLimitMallocHeapSize, 0) == hipSuccess));
  CHECK((heapLimit() == 0));
  REQUIRE((hipDeviceSetLimit(hipLimitMallocHeapSize, DefaultHeap) == hipSuccess));
}

TEST_CASE("HIP malloc heap limit rejects sizes the device cannot hold", "[hip][limits]") {
  gemstone_test::RpcFixture const fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  CHECK((hipDeviceSetLimit(hipLimitMallocHeapSize, size_t{1} << 40) == hipErrorInvalidValue));
  CHECK((heapLimit() == DefaultHeap));
}

TEST_CASE("HIP device limits validate their arguments", "[hip][limits]") {
  gemstone_test::RpcFixture const fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  size_t value = 0;
  CHECK((hipDeviceGetLimit(nullptr, hipLimitMallocHeapSize) == hipErrorInvalidValue));
  CHECK((hipDeviceGetLimit(&value, hipLimitRange) == hipErrorInvalidValue));
  CHECK((hipDeviceSetLimit(hipLimitRange, 0) == hipErrorInvalidValue));
  CHECK((hipDeviceGetLimit(&value, hipLimitStackSize) == hipErrorUnsupportedLimit));
  CHECK((hipDeviceSetLimit(hipLimitStackSize, 1024) == hipErrorUnsupportedLimit));
}
