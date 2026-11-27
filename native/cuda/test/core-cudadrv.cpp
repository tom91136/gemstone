#include <climits>
#include <cstddef>
#include <string>
#include <vector>

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
const gemstone_test::Environment Target(gemstone::environment::TargetArch, "sm_89");

constexpr char InvalidPtx[] = ".version 3.1\n.target sm_999\n.address_size 64";
constexpr char SupportedPtx[] = ".version 8.0\n.target sm_89\n.address_size 64\n.visible .entry probe() { ret; }\n";
constexpr char HigherPtx[] = ".version 8.0\n.target sm_90\n.address_size 64";
constexpr char NewestPtx[] = ".version 8.0\n.target sm_121\n.address_size 64";

void initialise() {
  REQUIRE(cuInit(0) == CUDA_SUCCESS);
  CUcontext context = nullptr;
  REQUIRE(cuCtxCreate_v2(&context, 0, 0) == CUDA_SUCCESS);
}

} // namespace

TEST_CASE("cuda core: an invalid device query appears in the driver log", "[cuda][core-cudadrv]") {
  initialise();
  CUdevice device = -1;
  CHECK(cuDeviceGet(&device, INT_MAX) == CUDA_ERROR_INVALID_DEVICE);
  std::vector<char> buffer(25600);
  size_t size = buffer.size();
  REQUIRE(cuLogsDumpToMemory(nullptr, buffer.data(), &size, 0) == CUDA_SUCCESS);
  const std::string log(buffer.data(), size);
  CHECK(log.contains("cuDeviceGet"));
  CHECK(log.contains("Returning 101 (CUDA_ERROR_INVALID_DEVICE) from cuDeviceGet"));
}

TEST_CASE("cuda core: a primary context keeps its flags", "[cuda][core-cudadrv]") {
  initialise();
  unsigned flags = 99;
  int active = -1;
  REQUIRE(cuDevicePrimaryCtxGetState(0, &flags, &active) == CUDA_SUCCESS);
  CHECK(flags == 0);
  REQUIRE(cuDevicePrimaryCtxSetFlags_v2(0, CU_CTX_SCHED_BLOCKING_SYNC) == CUDA_SUCCESS);
  REQUIRE(cuDevicePrimaryCtxGetState(0, &flags, &active) == CUDA_SUCCESS);
  CHECK(flags == CU_CTX_SCHED_BLOCKING_SYNC);
}

TEST_CASE("cuda core: resetting a primary context invalidates its contexts", "[cuda][core-cudadrv]") {
  initialise();
  CUcontext context = nullptr;
  REQUIRE(cuDevicePrimaryCtxRetain(&context, 0) == CUDA_SUCCESS);
  unsigned flags = 0;
  int active = 0;
  REQUIRE(cuDevicePrimaryCtxGetState(0, &flags, &active) == CUDA_SUCCESS);
  CHECK(active == 1);
  REQUIRE(cuDevicePrimaryCtxReset_v2(0) == CUDA_SUCCESS);
  REQUIRE(cuDevicePrimaryCtxGetState(0, &flags, &active) == CUDA_SUCCESS);
  CHECK(active == 0);
  unsigned long long id = 0;
  CHECK(cuCtxGetId(context, &id) != CUDA_SUCCESS);
}

TEST_CASE("cuda core: a device reports the pool it was set to", "[cuda][core-cudadrv]") {
  initialise();
  CUmemoryPool current = nullptr;
  CUmemPoolProps props{};
  props.allocType = CU_MEM_ALLOCATION_TYPE_PINNED;
  props.location.type = CU_MEM_LOCATION_TYPE_DEVICE;
  props.location.id = 0;
  CUmemoryPool created = nullptr;
  REQUIRE(cuMemPoolCreate(&created, &props) == CUDA_SUCCESS);
  REQUIRE(cuDeviceSetMemPool(0, created) == CUDA_SUCCESS);
  REQUIRE(cuDeviceGetMemPool(&current, 0) == CUDA_SUCCESS);
  CHECK(current == created);
}

TEST_CASE("cuda core: a target at the device capability loads", "[cuda][core-cudadrv]") {
  initialise();
  CUmodule module = nullptr;
  REQUIRE(cuModuleLoadData(&module, SupportedPtx) == CUDA_SUCCESS);
  CHECK(cuModuleUnload(module) == CUDA_SUCCESS);
}

TEST_CASE("cuda core: known targets above the device are refused", "[cuda][core-cudadrv]") {
  initialise();
  for (const char *source : {HigherPtx, NewestPtx}) {
    std::vector<char> buffer(4096);
    buffer[0] = 0;
    CUjit_option options[] = {CU_JIT_ERROR_LOG_BUFFER, CU_JIT_ERROR_LOG_BUFFER_SIZE_BYTES};
    void *values[] = {buffer.data(), reinterpret_cast<void *>(buffer.size())};
    CUmodule module = nullptr;
    CHECK(cuModuleLoadDataEx(&module, source, 2, options, values) == CUDA_ERROR_INVALID_PTX);
    const std::string log(buffer.data());
    CHECK(log.contains("higher than default SM version"));
    CHECK(log.contains("line 2"));
  }
}

TEST_CASE("cuda core: the ptxas log names the line of the target directive", "[cuda][core-cudadrv]") {
  initialise();
  const std::string ptx = "\n.version 3.1\n.target sm_999\n.address_size 64";
  std::vector<char> buffer(4096);
  buffer[0] = 0;
  CUjit_option options[] = {CU_JIT_ERROR_LOG_BUFFER, CU_JIT_ERROR_LOG_BUFFER_SIZE_BYTES};
  void *values[] = {buffer.data(), reinterpret_cast<void *>(buffer.size())};
  CUmodule module = nullptr;
  REQUIRE(cuModuleLoadDataEx(&module, ptx.c_str(), 2, options, values) == CUDA_ERROR_INVALID_PTX);
  CHECK(std::string(buffer.data()).contains("line 3"));
}

TEST_CASE("cuda core: an invalid PTX target is rejected with a ptxas log", "[cuda][core-cudadrv]") {
  initialise();
  std::vector<char> buffer(4096);
  buffer[0] = 0;
  CUjit_option options[] = {CU_JIT_ERROR_LOG_BUFFER, CU_JIT_ERROR_LOG_BUFFER_SIZE_BYTES};
  void *values[] = {buffer.data(), reinterpret_cast<void *>(buffer.size())};
  CUmodule module = nullptr;
  CHECK(cuModuleLoadDataEx(&module, InvalidPtx, 2, options, values) == CUDA_ERROR_INVALID_PTX);
  const std::string log(buffer.data());
  CHECK(log.contains("Unsupported .target 'sm_999'"));
  CHECK(log.contains("ptxas fatal"));
}

TEST_CASE("cuda core: the linker rejects an invalid PTX target with a ptxas log", "[cuda][core-cudadrv]") {
  initialise();
  std::vector<char> buffer(4096);
  CUjit_option options[] = {CU_JIT_ERROR_LOG_BUFFER, CU_JIT_ERROR_LOG_BUFFER_SIZE_BYTES};
  void *values[] = {buffer.data(), reinterpret_cast<void *>(buffer.size())};
  buffer[0] = 0;
  std::string ptx(InvalidPtx);
  CUlinkState link = nullptr;
  REQUIRE(cuLinkCreate_v2(2, options, values, &link) == CUDA_SUCCESS);
  CHECK(cuLinkAddData_v2(link, CU_JIT_INPUT_PTX, ptx.data(), ptx.size(), "dummy", 0, nullptr, nullptr) == CUDA_ERROR_INVALID_PTX);
  CHECK(std::string(buffer.data()).contains("ptxas fatal"));
}

TEST_CASE("cuda core: the linker refuses a target above the device", "[cuda][core-cudadrv]") {
  initialise();
  std::vector<char> buffer(4096);
  CUjit_option options[] = {CU_JIT_ERROR_LOG_BUFFER, CU_JIT_ERROR_LOG_BUFFER_SIZE_BYTES};
  void *values[] = {buffer.data(), reinterpret_cast<void *>(buffer.size())};
  buffer[0] = 0;
  std::string ptx(NewestPtx);
  CUlinkState link = nullptr;
  REQUIRE(cuLinkCreate_v2(2, options, values, &link) == CUDA_SUCCESS);
  CHECK(cuLinkAddData_v2(link, CU_JIT_INPUT_PTX, ptx.data(), ptx.size(), "dummy", 0, nullptr, nullptr) == CUDA_ERROR_INVALID_PTX);
  CHECK(std::string(buffer.data()).contains("higher than default SM version"));
}
