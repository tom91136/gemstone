#include <algorithm>
#include <cstddef>
#include <cstdint>
#include <cstring>
#include <iterator>
#include <stdexcept>
#include <string>
#include <vector>

#include <catch2/catch_test_macros.hpp>
#include <cuda.h>

#include "../../rpc/test/rpc_fixture.hpp"
#include <environment_keys.hpp>

extern "C" CUresult cuCtxCreate_v2(CUcontext *context, unsigned flags, CUdevice device);
#undef cuLinkCreate
#undef cuLinkAddData
extern "C" CUresult cuLinkCreate(unsigned, CUjit_option *, void **, CUlinkState *);
extern "C" CUresult cuLinkAddData(CUlinkState, CUjitInputType, void *, size_t, const char *, unsigned, CUjit_option *, void **);

namespace {

constexpr char Ptx[] = ".version 8.0\n.target sm_89\n.address_size 64\n.visible .entry probe() { ret; }\n";

void store16(std::vector<uint8_t> &bytes, size_t offset, uint16_t value) { std::memcpy(bytes.data() + offset, &value, sizeof(value)); }

void store32(std::vector<uint8_t> &bytes, size_t offset, uint32_t value) { std::memcpy(bytes.data() + offset, &value, sizeof(value)); }

void store64(std::vector<uint8_t> &bytes, size_t offset, uint64_t value) { std::memcpy(bytes.data() + offset, &value, sizeof(value)); }

std::vector<uint8_t> minimalCubin() {
  std::vector<uint8_t> image(128);
  constexpr uint8_t magic[]{0x7f, 'E', 'L', 'F', 2, 1};
  std::copy(std::begin(magic), std::end(magic), image.begin());
  store16(image, 18, 190);
  store16(image, 52, 64);
  store64(image, 40, 64);
  store16(image, 58, 64);
  store16(image, 60, 1);
  return image;
}

void appendFatbinEntry(std::vector<uint8_t> &fatbin, uint16_t kind, int32_t target, const uint8_t *data, size_t size) {
  const auto entry = fatbin.size();
  fatbin.resize(entry + 64 + size);
  store16(fatbin, entry, kind);
  store32(fatbin, entry + 4, 64);
  store32(fatbin, entry + 8, static_cast<uint32_t>(size));
  store32(fatbin, entry + 16, static_cast<uint32_t>(size));
  store32(fatbin, entry + 28, static_cast<uint32_t>(target));
  std::copy(data, data + size, fatbin.data() + entry + 64);
}

std::vector<uint8_t> mixedFatbin(const std::vector<uint8_t> &cubin) {
  std::vector<uint8_t> fatbin(16);
  store32(fatbin, 0, 0xba55ed50);
  store16(fatbin, 6, 16);
  appendFatbinEntry(fatbin, 1, 0, reinterpret_cast<const uint8_t *>(Ptx), sizeof(Ptx) - 1);
  appendFatbinEntry(fatbin, 2, 89, cubin.data(), cubin.size());
  store64(fatbin, 8, fatbin.size() - 16);
  return fatbin;
}

std::vector<uint8_t> metadataFatbin(const std::vector<uint8_t> &cubin) {
  std::vector<uint8_t> fatbin(16);
  store32(fatbin, 0, 0xba55ed50);
  store16(fatbin, 6, 16);
  appendFatbinEntry(fatbin, 2, 89, cubin.data(), cubin.size());
  store64(fatbin, 8, fatbin.size() - 16);
  return fatbin;
}

void setCodeMode(const char *mode) {
  if (setenv(gemstone::environment::CudaCode, mode, 1) != 0) throw std::runtime_error("cannot set CUDA code mode");
}

} // namespace

TEST_CASE("CUDA legacy linker symbols preserve every PTX input", "[cuda][code-mode]") {
  setCodeMode("ptx");
  CUlinkState state = nullptr;
  REQUIRE(cuLinkCreate(0, nullptr, nullptr, &state) == CUDA_SUCCESS);
  std::string first(Ptx);
  std::string second = ".visible .entry second() { ret; }\n";
  REQUIRE(cuLinkAddData(state, CU_JIT_INPUT_PTX, first.data(), first.size() + 1, nullptr, 0, nullptr, nullptr) == CUDA_SUCCESS);
  REQUIRE(cuLinkAddData_v2(state, CU_JIT_INPUT_PTX, second.data(), second.size() + 1, nullptr, 0, nullptr, nullptr) == CUDA_SUCCESS);
  void *image = nullptr;
  size_t size = 0;
  REQUIRE(cuLinkComplete(state, &image, &size) == CUDA_SUCCESS);
  const auto expected = first + '\n' + second;
  CHECK(std::string(static_cast<const char *>(image)) == expected);
  CHECK(size == expected.size());
  CHECK(cuLinkDestroy(state) == CUDA_SUCCESS);
}

TEST_CASE("CUDA strict code modes reject wrong kinds and select matching fatbin images", "[cuda][code-mode]") {
  gemstone_test::RpcFixture fixture;
  const auto endpoint = fixture.endpoint();
  REQUIRE(setenv(gemstone::environment::RpcEndpoint, endpoint.c_str(), 1) == 0);
  REQUIRE(setenv(gemstone::environment::RpcClient, "42", 1) == 0);
  REQUIRE(setenv(gemstone::environment::TargetArch, "compute_89", 1) == 0);

  setCodeMode("cubin");
  CHECK((cuInit(0) == CUDA_ERROR_NOT_INITIALIZED));
  REQUIRE(unsetenv(gemstone::environment::CudaCode) == 0);
  REQUIRE((cuInit(0) == CUDA_SUCCESS));
  CUcontext context = nullptr;
  REQUIRE((cuCtxCreate_v2(&context, 0, 0) == CUDA_SUCCESS));

  CUdeviceptr allocation = 0;
  constexpr size_t bytes = 37;
  REQUIRE(cuMemAlloc_v2(&allocation, bytes) == CUDA_SUCCESS);
  CUdeviceptr base = 0;
  size_t size = 0;
  REQUIRE(cuMemGetAddressRange_v2(&base, &size, allocation + bytes - 1) == CUDA_SUCCESS);
  CHECK(base == allocation);
  CHECK(size == bytes);
  CHECK(cuMemGetAddressRange_v2(nullptr, &size, allocation) == CUDA_SUCCESS);
  CHECK(cuMemGetAddressRange_v2(&base, nullptr, allocation) == CUDA_SUCCESS);
  CHECK(cuMemGetAddressRange_v2(nullptr, nullptr, allocation) == CUDA_SUCCESS);
  CHECK(cuMemGetAddressRange_v2(&base, &size, allocation + bytes) == CUDA_ERROR_INVALID_VALUE);
  REQUIRE(cuMemFree_v2(allocation) == CUDA_SUCCESS);
  CHECK(cuMemGetAddressRange_v2(&base, &size, allocation) == CUDA_ERROR_INVALID_VALUE);

  const auto cubin = minimalCubin();
  const auto fatbin = mixedFatbin(cubin);
  const auto metadataOnlyFatbin = metadataFatbin(cubin);
  std::string ptx(Ptx);
  const auto loadsBefore = fixture.programLoads.load();

  setCodeMode("sass");
  CUmodule module = nullptr;
  CHECK((cuModuleLoadData(&module, ptx.c_str()) == CUDA_ERROR_NOT_SUPPORTED));
  CHECK((module == nullptr));
  CHECK(fixture.programLoads.load() == loadsBefore);

  setCodeMode("ptx");
  module = nullptr;
  CHECK((cuModuleLoadData(&module, cubin.data()) == CUDA_ERROR_NOT_SUPPORTED));
  CHECK((module == nullptr));
  CHECK(fixture.programLoads.load() == loadsBefore);

  module = nullptr;
  REQUIRE((cuModuleLoadData(&module, ptx.c_str()) == CUDA_SUCCESS));
  CHECK((module != nullptr));
  CHECK(fixture.lastProgramImage().front() == static_cast<uint8_t>('.'));
  REQUIRE((cuModuleUnload(module) == CUDA_SUCCESS));

  setCodeMode("sass");
  module = nullptr;
  REQUIRE((cuModuleLoadData(&module, cubin.data()) == CUDA_SUCCESS));
  CHECK((module != nullptr));
  CHECK(fixture.lastProgramImage().front() == 0x7f);
  REQUIRE((cuModuleUnload(module) == CUDA_SUCCESS));

  module = nullptr;
  CHECK((cuModuleLoadData(&module, ptx.c_str()) == CUDA_ERROR_NOT_SUPPORTED));
  CHECK((module == nullptr));

  setCodeMode("ptx");
  module = nullptr;
  REQUIRE((cuModuleLoadFatBinary(&module, fatbin.data()) == CUDA_SUCCESS));
  CHECK((module != nullptr));
  CHECK(fixture.lastProgramImage().front() == static_cast<uint8_t>('.'));
  REQUIRE((cuModuleUnload(module) == CUDA_SUCCESS));

  setCodeMode("sass");
  module = nullptr;
  REQUIRE((cuModuleLoadFatBinary(&module, fatbin.data()) == CUDA_SUCCESS));
  CHECK((module != nullptr));
  CHECK(fixture.lastProgramImage().front() == 0x7f);
  REQUIRE((cuModuleUnload(module) == CUDA_SUCCESS));

  const auto loadsBeforeMetadata = fixture.programLoads.load();
  module = nullptr;
  REQUIRE((cuModuleLoadFatBinary(&module, metadataOnlyFatbin.data()) == CUDA_SUCCESS));
  CHECK((module != nullptr));
  const auto metadataImage = fixture.lastProgramImage();
  CHECK(std::string(metadataImage.begin(), metadataImage.end()) == ".version 9.3\n.target sm_89\n.address_size 64\n");
  REQUIRE((cuModuleUnload(module) == CUDA_SUCCESS));

  module = nullptr;
  CHECK((cuModuleLoadData(&module, ptx.c_str()) == CUDA_ERROR_NOT_SUPPORTED));
  CHECK((module == nullptr));
  CHECK(fixture.programLoads.load() == loadsBeforeMetadata + 1);

  CUlibrary metadataLibrary = nullptr;
  REQUIRE((cuLibraryLoadData(&metadataLibrary, metadataOnlyFatbin.data(), nullptr, nullptr, 0, nullptr, nullptr, 0) == CUDA_SUCCESS));
  const auto metadataLibraryImage = fixture.lastProgramImage();
  CHECK(std::string(metadataLibraryImage.begin(), metadataLibraryImage.end()) == ".version 9.3\n.target sm_89\n.address_size 64\n");
  REQUIRE((cuLibraryUnload(metadataLibrary) == CUDA_SUCCESS));

  setCodeMode("ptx");
  module = nullptr;
  REQUIRE((cuModuleLoadDataEx(&module, fatbin.data(), 0, nullptr, nullptr) == CUDA_SUCCESS));
  CHECK(fixture.lastProgramImage().front() == static_cast<uint8_t>('.'));
  REQUIRE((cuModuleUnload(module) == CUDA_SUCCESS));

  setCodeMode("sass");
  module = nullptr;
  REQUIRE((cuModuleLoadDataEx(&module, fatbin.data(), 0, nullptr, nullptr) == CUDA_SUCCESS));
  CHECK(fixture.lastProgramImage().front() == 0x7f);
  REQUIRE((cuModuleUnload(module) == CUDA_SUCCESS));

  CUlibrary library = nullptr;
  CHECK((cuLibraryLoadData(&library, nullptr, nullptr, nullptr, 0, nullptr, nullptr, 0) == CUDA_ERROR_INVALID_VALUE));
  CHECK((library == nullptr));

  setCodeMode("ptx");
  library = nullptr;
  REQUIRE((cuLibraryLoadData(&library, fatbin.data(), nullptr, nullptr, 0, nullptr, nullptr, 0) == CUDA_SUCCESS));
  CHECK(fixture.lastProgramImage().front() == static_cast<uint8_t>('.'));
  REQUIRE((cuLibraryUnload(library) == CUDA_SUCCESS));

  setCodeMode("sass");
  library = nullptr;
  REQUIRE((cuLibraryLoadData(&library, fatbin.data(), nullptr, nullptr, 0, nullptr, nullptr, 0) == CUDA_SUCCESS));
  CHECK(fixture.lastProgramImage().front() == 0x7f);
  REQUIRE((cuLibraryUnload(library) == CUDA_SUCCESS));

  library = nullptr;
  CHECK((cuLibraryLoadData(&library, ptx.c_str(), nullptr, nullptr, 0, nullptr, nullptr, 0) == CUDA_ERROR_NOT_SUPPORTED));
  CHECK((library == nullptr));

  setCodeMode("ptx");
  REQUIRE((cuModuleLoadData(&module, ptx.c_str()) == CUDA_SUCCESS));
  CUfunction clustered = nullptr;
  REQUIRE((cuModuleGetFunction(&clustered, module, "probe") == CUDA_SUCCESS));
  CHECK((cuFuncSetAttribute(clustered, CU_FUNC_ATTRIBUTE_REQUIRED_CLUSTER_WIDTH, 2) == CUDA_ERROR_NOT_SUPPORTED));
  REQUIRE((cuFuncSetAttribute(clustered, CU_FUNC_ATTRIBUTE_REQUIRED_CLUSTER_WIDTH, 1) == CUDA_SUCCESS));
  int clusterWidth = -1;
  REQUIRE((cuFuncGetAttribute(&clusterWidth, CU_FUNC_ATTRIBUTE_REQUIRED_CLUSTER_WIDTH, clustered) == CUDA_SUCCESS));
  CHECK(clusterWidth == 1);
  CUlaunchAttribute attribute{};
  attribute.id = CU_LAUNCH_ATTRIBUTE_CLUSTER_DIMENSION;
  attribute.value.clusterDim = {2, 1, 1};
  CUlaunchConfig config{};
  config.gridDimX = 2;
  config.gridDimY = config.gridDimZ = config.blockDimX = config.blockDimY = config.blockDimZ = 1;
  config.attrs = &attribute;
  config.numAttrs = 1;
  size_t sharedForOne = 0;
  size_t sharedForTwo = 0;
  REQUIRE((cuOccupancyAvailableDynamicSMemPerBlock(&sharedForOne, clustered, 1, 256) == CUDA_SUCCESS));
  REQUIRE((cuOccupancyAvailableDynamicSMemPerBlock(&sharedForTwo, clustered, 2, 256) == CUDA_SUCCESS));
  CHECK(sharedForOne == 2 * sharedForTwo);
  CHECK((cuOccupancyAvailableDynamicSMemPerBlock(&sharedForOne, clustered, 0, 256) == CUDA_ERROR_INVALID_VALUE));
  CHECK((cuOccupancyAvailableDynamicSMemPerBlock(&sharedForOne, clustered, 2, 1024) == CUDA_ERROR_INVALID_VALUE));
  CHECK((cuLaunchKernelEx(&config, clustered, nullptr, nullptr) == CUDA_ERROR_NOT_SUPPORTED));
  REQUIRE((cuFuncSetAttribute(clustered, CU_FUNC_ATTRIBUTE_REQUIRED_CLUSTER_WIDTH, 0) == CUDA_SUCCESS));
  attribute.value.clusterDim = {3, 1, 1};
  config.gridDimX = 3;
  config.gridDimY = 1024;
  config.blockDimX = 128;
  config.sharedMemBytes = 98304; // Exact cuBLAS Hopper catalogue capacity probe.
  int activeClusters = -1;
  REQUIRE((cuOccupancyMaxActiveClusters(&activeClusters, clustered, &config) == CUDA_SUCCESS));
  CHECK(activeClusters == 0);
  CHECK((cuLaunchKernelEx(&config, clustered, nullptr, nullptr) == CUDA_ERROR_NOT_SUPPORTED));
  attribute.value.clusterDim = {1, 1, 1};
  REQUIRE((cuOccupancyMaxActiveClusters(&activeClusters, clustered, &config) == CUDA_SUCCESS));
  CHECK(activeClusters > 0);
  attribute.value.clusterDim = {2, 1, 1};
  CHECK((cuOccupancyMaxActiveClusters(&activeClusters, clustered, &config) == CUDA_ERROR_INVALID_CLUSTER_SIZE));
  attribute.value.clusterDim = {0, 1, 1};
  CHECK((cuLaunchKernelEx(&config, clustered, nullptr, nullptr) == CUDA_ERROR_INVALID_VALUE));
  config.attrs = nullptr;
  CHECK((cuLaunchKernelEx(&config, clustered, nullptr, nullptr) == CUDA_ERROR_INVALID_VALUE));
  REQUIRE((cuModuleUnload(module) == CUDA_SUCCESS));

  // Library catalogues may contain unused cubin-only kernels in a PTX run.
  // Metadata handles are usable for inspection; every launch still rejects SASS.
  setCodeMode("ptx");
  library = nullptr;
  REQUIRE((cuLibraryLoadData(&library, cubin.data(), nullptr, nullptr, 0, nullptr, nullptr, 0) == CUDA_SUCCESS));
  CUkernel kernel = nullptr;
  REQUIRE((cuLibraryGetKernel(&kernel, library, "probe") == CUDA_SUCCESS));
  CUfunction function = nullptr;
  REQUIRE((cuKernelGetFunction(&function, kernel) == CUDA_SUCCESS));
  CHECK((function != nullptr));
  REQUIRE((cuLibraryGetModule(&module, library) == CUDA_SUCCESS));
  REQUIRE((cuModuleGetFunction(&function, module, "probe") == CUDA_SUCCESS));
  CHECK((function != nullptr));
  void *arguments[]{nullptr};
  CHECK(
      (cuLaunchKernel(reinterpret_cast<CUfunction>(kernel), 1, 1, 1, 1, 1, 1, 0, nullptr, arguments, nullptr) == CUDA_ERROR_NOT_SUPPORTED));
  config.attrs = nullptr;
  config.numAttrs = 0;
  CHECK((cuLaunchKernelEx(&config, function, arguments, nullptr) == CUDA_ERROR_NOT_SUPPORTED));
  REQUIRE((cuLibraryUnload(library) == CUDA_SUCCESS));

  // The fixture uses kind 2 for allocation parameters; the gap checks ABI offsets.
  fixture.kernelMetadata(24, 2, {{8, 0, 2, 0}, {8, 16, 2, 0}});
  fixture.expect({1, 1, 1, 0, 0});
  fixture.expectAllocationArguments({{0, 0}, {0, 4}});
  REQUIRE(cuModuleLoadData(&module, ptx.c_str()) == CUDA_SUCCESS);
  REQUIRE(cuModuleGetFunction(&function, module, "probe") == CUDA_SUCCESS);
  REQUIRE(cuMemAlloc_v2(&allocation, bytes) == CUDA_SUCCESS);
  std::vector<uint8_t> packed(24, 0xff);
  store64(packed, 0, allocation);
  store64(packed, 16, allocation + 4);
  size_t packedSize = packed.size();
  void *extra[]{CU_LAUNCH_PARAM_BUFFER_SIZE, &packedSize, CU_LAUNCH_PARAM_BUFFER_POINTER, packed.data(), CU_LAUNCH_PARAM_END};
  const auto launches = fixture.launches.load();
  CHECK(cuLaunchKernel(function, 1, 1, 1, 1, 1, 1, 0, nullptr, arguments, extra) == CUDA_ERROR_INVALID_VALUE);
  --packedSize;
  CHECK(cuLaunchKernel(function, 1, 1, 1, 1, 1, 1, 0, nullptr, nullptr, extra) == CUDA_ERROR_INVALID_VALUE);
  CHECK(fixture.launches.load() == launches);
  ++packedSize;
  REQUIRE(cuLaunchKernel(function, 1, 1, 1, 1, 1, 1, 0, nullptr, nullptr, extra) == CUDA_SUCCESS);
  REQUIRE(cuCtxSynchronize() == CUDA_SUCCESS);
  CHECK(fixture.launches.load() == launches + 1);
  REQUIRE(cuMemFree_v2(allocation) == CUDA_SUCCESS);
  REQUIRE(cuModuleUnload(module) == CUDA_SUCCESS);

  CHECK((cuCtxDestroy_v2(context) == CUDA_SUCCESS));
  unsetenv(gemstone::environment::RpcEndpoint);
  unsetenv(gemstone::environment::RpcClient);
  unsetenv(gemstone::environment::TargetArch);
  unsetenv(gemstone::environment::CudaCode);
}
