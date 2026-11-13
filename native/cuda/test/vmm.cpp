#include <cstddef>
#include <string>

#include <catch2/catch_test_macros.hpp>
#include <cuda.h>

#include <gemstone/test_support.hpp>

#include "../../rpc/test/rpc_fixture.hpp"
#include <environment_keys.hpp>

extern "C" CUresult cuCtxCreate_v2(CUcontext *context, unsigned flags, CUdevice device);

namespace {

gemstone_test::RpcFixture fixture;
const std::string Endpoint = fixture.endpoint();
const gemstone_test::Environment RpcEndpoint(gemstone::environment::RpcEndpoint, Endpoint.c_str());
const gemstone_test::Environment RpcClient(gemstone::environment::RpcClientId, "42");

constexpr size_t AllocationGranularity = size_t{2} << 20;

CUmemAllocationProp deviceAllocation() {
  CUmemAllocationProp prop{};
  prop.type = CU_MEM_ALLOCATION_TYPE_PINNED;
  prop.location.type = CU_MEM_LOCATION_TYPE_DEVICE;
  prop.location.id = 0;
  return prop;
}

} // namespace

TEST_CASE("CUDA reports allocation granularity for device memory", "[cuda][memory]") {
  REQUIRE(cuInit(0) == CUDA_SUCCESS);
  CUcontext context = nullptr;
  REQUIRE(cuCtxCreate_v2(&context, 0, 0) == CUDA_SUCCESS);

  const auto prop = deviceAllocation();
  size_t minimum = 0;
  size_t recommended = 0;
  REQUIRE(cuMemGetAllocationGranularity(&minimum, &prop, CU_MEM_ALLOC_GRANULARITY_MINIMUM) == CUDA_SUCCESS);
  REQUIRE(cuMemGetAllocationGranularity(&recommended, &prop, CU_MEM_ALLOC_GRANULARITY_RECOMMENDED) == CUDA_SUCCESS);
  CHECK(minimum == AllocationGranularity);
  CHECK(recommended == AllocationGranularity);

  CHECK(cuMemGetAllocationGranularity(nullptr, &prop, CU_MEM_ALLOC_GRANULARITY_MINIMUM) == CUDA_ERROR_INVALID_VALUE);
  CHECK(cuMemGetAllocationGranularity(&minimum, nullptr, CU_MEM_ALLOC_GRANULARITY_MINIMUM) == CUDA_ERROR_INVALID_VALUE);
  CHECK(cuMemGetAllocationGranularity(&minimum, &prop, static_cast<CUmemAllocationGranularity_flags>(2)) == CUDA_ERROR_INVALID_VALUE);

  auto invalid = prop;
  invalid.type = CU_MEM_ALLOCATION_TYPE_INVALID;
  CHECK(cuMemGetAllocationGranularity(&minimum, &invalid, CU_MEM_ALLOC_GRANULARITY_MINIMUM) == CUDA_ERROR_INVALID_VALUE);
  invalid = prop;
  invalid.location.type = CU_MEM_LOCATION_TYPE_INVALID;
  CHECK(cuMemGetAllocationGranularity(&minimum, &invalid, CU_MEM_ALLOC_GRANULARITY_MINIMUM) == CUDA_ERROR_INVALID_VALUE);
  invalid = prop;
  invalid.location.id = 1;
  CHECK(cuMemGetAllocationGranularity(&minimum, &invalid, CU_MEM_ALLOC_GRANULARITY_MINIMUM) == CUDA_ERROR_INVALID_VALUE);

  CHECK(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}
