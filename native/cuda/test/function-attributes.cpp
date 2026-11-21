#include <cstddef>
#include <filesystem>
#include <fstream>
#include <optional>
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

constexpr char SharedReferenced[] = R"PTX(.version 8.4
.target sm_89
.address_size 64

.visible .entry k_shared400(.param .u64 p)
{
  .reg .b32 %r<3>;
  .reg .b64 %rd<2>;
  .shared .align 4 .b32 s[100];

  ld.param.u64 %rd1, [p];
  mov.u32 %r1, 7;
  st.shared.u32 [s+4], %r1;
  ld.shared.u32 %r2, [s+4];
  st.global.u32 [%rd1], %r2;
  ret;
}
)PTX";

constexpr char SharedUnreferenced[] = R"PTX(.version 8.4
.target sm_89
.address_size 64

.shared .align 4 .b32 s[100];

.visible .entry v_shared_unused(.param .u64 p)
{
  ret;
}
)PTX";

constexpr char ConstReferenced80[] = R"PTX(.version 8.4
.target sm_89
.address_size 64

.const .align 8 .b8 c[80];

.visible .entry k_const80(.param .u64 p)
{
  .reg .b64 %rd<3>;

  ld.param.u64 %rd1, [p];
  ld.const.u64 %rd2, [c+8];
  st.global.u64 [%rd1], %rd2;
  ret;
}
)PTX";

constexpr char ConstReferenced256[] = R"PTX(.version 8.4
.target sm_89
.address_size 64

.const .align 4 .b8 c[256];

.visible .entry k_const256(.param .u64 p)
{
  .reg .b32 %r<3>;
  .reg .b64 %rd<2>;

  ld.param.u64 %rd1, [p];
  ld.const.u32 %r1, [c+4];
  st.global.u32 [%rd1], %r1;
  ret;
}
)PTX";

constexpr char ConstUnreferenced[] = R"PTX(.version 8.4
.target sm_89
.address_size 64

.const .align 8 .b8 c[80];

.visible .entry k_const_unused(.param .u64 p)
{
  ret;
}
)PTX";

CUcontext createContext() {
  REQUIRE(cuInit(0) == CUDA_SUCCESS);
  CUcontext context = nullptr;
  REQUIRE(cuCtxCreate_v2(&context, 0, 0) == CUDA_SUCCESS);
  return context;
}

CUfunction loadKernel(const char *image, const char *name) {
  CUmodule module = nullptr;
  REQUIRE(cuModuleLoadData(&module, image) == CUDA_SUCCESS);
  CUfunction function = nullptr;
  REQUIRE(cuModuleGetFunction(&function, module, name) == CUDA_SUCCESS);
  return function;
}

int functionAttribute(CUfunction function, CUfunction_attribute attribute) {
  int value = -1;
  REQUIRE(cuFuncGetAttribute(&value, attribute, function) == CUDA_SUCCESS);
  return value;
}

} // namespace

TEST_CASE("CUDA function attributes forward the shared size from kernel metadata", "[cuda][function-attributes]") {
  const gemstone_test::Environment target(gemstone::environment::TargetArch, "sm_89");
  auto *const context = createContext();
  fixture.kernelResources({
      .registerCount = std::nullopt,
      .sharedSizeBytes = 400,
      .constSizeBytes = std::nullopt,
      .localSizeBytes = std::nullopt,
  });
  CHECK(functionAttribute(loadKernel(SharedReferenced, "k_shared400"), CU_FUNC_ATTRIBUTE_SHARED_SIZE_BYTES) == 400);
  fixture.kernelResources({
      .registerCount = std::nullopt,
      .sharedSizeBytes = 0,
      .constSizeBytes = std::nullopt,
      .localSizeBytes = std::nullopt,
  });
  CHECK(functionAttribute(loadKernel(SharedUnreferenced, "v_shared_unused"), CU_FUNC_ATTRIBUTE_SHARED_SIZE_BYTES) == 0);
  CHECK(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("CUDA function attributes forward the const size from kernel metadata", "[cuda][function-attributes]") {
  const gemstone_test::Environment target(gemstone::environment::TargetArch, "sm_89");
  auto *const context = createContext();
  fixture.kernelResources({
      .registerCount = std::nullopt,
      .sharedSizeBytes = std::nullopt,
      .constSizeBytes = 80,
      .localSizeBytes = std::nullopt,
  });
  CHECK(functionAttribute(loadKernel(ConstReferenced80, "k_const80"), CU_FUNC_ATTRIBUTE_CONST_SIZE_BYTES) == 80);
  fixture.kernelResources({
      .registerCount = std::nullopt,
      .sharedSizeBytes = std::nullopt,
      .constSizeBytes = 256,
      .localSizeBytes = std::nullopt,
  });
  CHECK(functionAttribute(loadKernel(ConstReferenced256, "k_const256"), CU_FUNC_ATTRIBUTE_CONST_SIZE_BYTES) == 256);
  fixture.kernelResources({
      .registerCount = std::nullopt,
      .sharedSizeBytes = std::nullopt,
      .constSizeBytes = 80,
      .localSizeBytes = std::nullopt,
  });
  CHECK(functionAttribute(loadKernel(ConstUnreferenced, "k_const_unused"), CU_FUNC_ATTRIBUTE_CONST_SIZE_BYTES) == 80);
  CHECK(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("CUDA function attributes forward the register count and local size from kernel metadata", "[cuda][function-attributes]") {
  const gemstone_test::Environment target(gemstone::environment::TargetArch, "sm_89");
  auto *const context = createContext();
  fixture.kernelResources({.registerCount = 38, .sharedSizeBytes = std::nullopt, .constSizeBytes = std::nullopt, .localSizeBytes = 8000});
  auto *const function = loadKernel(ConstReferenced80, "k_const80");
  CHECK(functionAttribute(function, CU_FUNC_ATTRIBUTE_NUM_REGS) == 38);
  CHECK(functionAttribute(function, CU_FUNC_ATTRIBUTE_LOCAL_SIZE_BYTES) == 8000);
  CHECK(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("CUDA kernel launch bounds limit the reported block size and the launch", "[cuda][function-attributes]") {
  const gemstone_test::Environment target(gemstone::environment::TargetArch, "sm_89");
  auto *const context = createContext();
  auto *const unbounded = loadKernel(ConstReferenced80, "k_const80");
  CHECK(functionAttribute(unbounded, CU_FUNC_ATTRIBUTE_MAX_THREADS_PER_BLOCK) == 1024);
  fixture.kernelLaunchBound(256);
  auto *const bounded = loadKernel(SharedReferenced, "k_shared400");
  CHECK(functionAttribute(bounded, CU_FUNC_ATTRIBUTE_MAX_THREADS_PER_BLOCK) == 256);
  const auto launches = fixture.launches.load();
  CHECK(cuLaunchKernel(bounded, 1, 1, 1, 512, 1, 1, 0, nullptr, nullptr, nullptr) == CUDA_ERROR_INVALID_VALUE);
  CHECK(cuLaunchKernel(bounded, 1, 1, 1, 16, 32, 1, 0, nullptr, nullptr, nullptr) == CUDA_ERROR_INVALID_VALUE);
  CHECK(fixture.launches.load() == launches);
  fixture.kernelLaunchBound(std::nullopt);
  CHECK(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("CUDA cuLinkAddFile adds a PTX file to the link", "[cuda][function-attributes]") {
  const gemstone_test::Environment target(gemstone::environment::TargetArch, "sm_89");
  auto *const context = createContext();
  const auto path = std::filesystem::temp_directory_path() / "gemstone-function-attributes-link.ptx";
  {
    std::ofstream out(path);
    out << ConstReferenced80;
  }
  CUlinkState state = nullptr;
  REQUIRE(cuLinkCreate(0, nullptr, nullptr, &state) == CUDA_SUCCESS);
  CHECK(cuLinkAddFile(state, CU_JIT_INPUT_PTX, path.c_str(), 0, nullptr, nullptr) == CUDA_SUCCESS);
  void *image = nullptr;
  size_t size = 0;
  CHECK(cuLinkComplete(state, &image, &size) == CUDA_SUCCESS);
  CHECK(size > 0);
  CHECK(cuLinkDestroy(state) == CUDA_SUCCESS);
  std::filesystem::remove(path);
  CHECK(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("CUDA cuLinkAddFile reports a missing input file as file not found", "[cuda][function-attributes]") {
  const gemstone_test::Environment target(gemstone::environment::TargetArch, "sm_89");
  auto *const context = createContext();
  const auto missing = std::filesystem::temp_directory_path() / "gemstone-function-attributes-missing.ptx";
  CUlinkState state = nullptr;
  REQUIRE(cuLinkCreate(0, nullptr, nullptr, &state) == CUDA_SUCCESS);
  CHECK(cuLinkAddFile(state, CU_JIT_INPUT_PTX, missing.c_str(), 0, nullptr, nullptr) == CUDA_ERROR_FILE_NOT_FOUND);
  CHECK(cuLinkAddFile(state, CU_JIT_INPUT_LIBRARY, "nonexistent.a", 0, nullptr, nullptr) == CUDA_ERROR_FILE_NOT_FOUND);
  CHECK(cuLinkDestroy(state) == CUDA_SUCCESS);
  CHECK(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}
