#include <algorithm>
#include <cstddef>
#include <cstdint>
#include <cstring>
#include <optional>
#include <string>
#include <tuple>
#include <vector>

#include <catch2/catch_test_macros.hpp>
#include <catch2/generators/catch_generators.hpp>
#include <cuda.h>
#include <cuda_runtime_api.h>

#include <gemstone/test_support.hpp>

#include "../../generated/rpc/gemstone_execution_rpc.hpp"
#include "../../rpc/test/rpc_fixture.hpp"
#include <environment_keys.hpp>

extern "C" CUresult cuCtxCreate_v2(CUcontext *context, unsigned flags, CUdevice device);
extern "C" cudaError_t __cudaLaunchKernel(cudaKernel_t kernel, dim3 grid, dim3 block, void **arguments, size_t sharedMemory,
                                          cudaStream_t stream);
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

// A fixture-backed context with one loaded module whose kernel takes a single allocation parameter.
class FailureSession {
public:
  FailureSession()
      : endpoint(fixture.endpoint()), rpcEndpoint(gemstone::environment::RpcEndpoint, endpoint.c_str()),
        rpcClient(gemstone::environment::RpcClient, "42"), targetArch(gemstone::environment::TargetArch, "compute_89") {
    REQUIRE(cuInit(0) == CUDA_SUCCESS);
    REQUIRE(cuCtxCreate_v2(&context, 0, 0) == CUDA_SUCCESS);
    REQUIRE(cuMemAlloc_v2(&allocation, 16) == CUDA_SUCCESS);
    fixture.kernelMetadata(8, 1, {{.width = 8, .offset = 0, .kind = 2, .containsPointers = 0}});
    fixture.expectAllocationArguments({{.alias = 0, .offset = 0}});
  }

  CUresult load(const void *image) { return cuModuleLoadData(&module, image); }

  void loadKernel(const void *image) {
    REQUIRE(load(image) == CUDA_SUCCESS);
    REQUIRE(cuModuleGetFunction(&function, module, "probe") == CUDA_SUCCESS);
  }

  CUresult launch() {
    fixture.expect({.local = 1, .groups = 1, .global = 1, .offset = 0, .seed = 0});
    void *arguments[]{&allocation};
    return cuLaunchKernel(function, 1, 1, 1, 1, 1, 1, 0, nullptr, arguments, nullptr);
  }

  gemstone_test::RpcFixture fixture;
  std::string endpoint;
  gemstone_test::Environment rpcEndpoint;
  gemstone_test::Environment rpcClient;
  gemstone_test::Environment targetArch;
  CUcontext context = nullptr;
  CUdeviceptr allocation = 0;
  CUmodule module = nullptr;
  CUfunction function = nullptr;
};

// cuda.h spells these as C-style casts of integers
void *const LaunchParamBufferPointer = reinterpret_cast<void *>(uintptr_t{0x1});
void *const LaunchParamBufferSize = reinterpret_cast<void *>(uintptr_t{0x2});

} // namespace

TEST_CASE("CUDA legacy linker symbols preserve every PTX input", "[cuda][code-mode]") {
  const gemstone_test::Environment codeMode(gemstone::environment::CudaCode, "ptx");
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
  const gemstone_test::Environment rpcEndpoint(gemstone::environment::RpcEndpoint, endpoint.c_str());
  const gemstone_test::Environment rpcClient(gemstone::environment::RpcClient, "42");
  const gemstone_test::Environment targetArch(gemstone::environment::TargetArch, "compute_89");
  std::optional<gemstone_test::Environment> codeMode;
  const auto setCodeMode = [&](const char *mode) { codeMode.emplace(gemstone::environment::CudaCode, mode); };

  setCodeMode("cubin");
  CHECK((cuInit(0) == CUDA_ERROR_NOT_INITIALIZED));
  setCodeMode(nullptr);
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
  std::string const ptx(Ptx);
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
  CHECK((cuFuncSetAttribute(clustered, CU_FUNC_ATTRIBUTE_REQUIRED_CLUSTER_WIDTH, 17) == CUDA_ERROR_INVALID_CLUSTER_SIZE));
  CHECK((cuFuncSetAttribute(clustered, CU_FUNC_ATTRIBUTE_REQUIRED_CLUSTER_WIDTH, 2) == CUDA_SUCCESS));
  REQUIRE((cuFuncSetAttribute(clustered, CU_FUNC_ATTRIBUTE_REQUIRED_CLUSTER_WIDTH, 1) == CUDA_SUCCESS));
  int clusterWidth = -1;
  REQUIRE((cuFuncGetAttribute(&clusterWidth, CU_FUNC_ATTRIBUTE_REQUIRED_CLUSTER_WIDTH, clustered) == CUDA_SUCCESS));
  CHECK(clusterWidth == 1);
  CUlaunchAttribute attribute{};
  attribute.id = CU_LAUNCH_ATTRIBUTE_CLUSTER_DIMENSION;
  attribute.value.clusterDim = {.x = 2, .y = 1, .z = 1};
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
  config.gridDimX = 3;
  CHECK((cuLaunchKernelEx(&config, clustered, nullptr, nullptr) == CUDA_ERROR_INVALID_VALUE));
  attribute.value.clusterDim = {.x = 17, .y = 1, .z = 1};
  config.gridDimX = 17;
  CHECK((cuLaunchKernelEx(&config, clustered, nullptr, nullptr) == CUDA_ERROR_INVALID_CLUSTER_SIZE));
  REQUIRE((cuFuncSetAttribute(clustered, CU_FUNC_ATTRIBUTE_REQUIRED_CLUSTER_WIDTH, 0) == CUDA_SUCCESS));
  attribute.value.clusterDim = {.x = 3, .y = 1, .z = 1};
  config.gridDimX = 3;
  config.gridDimY = 1024;
  config.blockDimX = 128;
  config.sharedMemBytes = 98304; // Exact cuBLAS Hopper catalogue capacity probe.
  int activeClusters = -1;
  REQUIRE((cuOccupancyMaxActiveClusters(&activeClusters, clustered, &config) == CUDA_SUCCESS));
  CHECK(activeClusters > 0);
  attribute.value.clusterDim = {.x = 1, .y = 1, .z = 1};
  REQUIRE((cuOccupancyMaxActiveClusters(&activeClusters, clustered, &config) == CUDA_SUCCESS));
  CHECK(activeClusters > 0);
  attribute.value.clusterDim = {.x = 2, .y = 1, .z = 1};
  CHECK((cuOccupancyMaxActiveClusters(&activeClusters, clustered, &config) == CUDA_ERROR_INVALID_CLUSTER_SIZE));
  attribute.value.clusterDim = {.x = 0, .y = 1, .z = 1};
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
  fixture.kernelMetadata(
      24, 2, {{.width = 8, .offset = 0, .kind = 2, .containsPointers = 0}, {.width = 8, .offset = 16, .kind = 2, .containsPointers = 0}});
  fixture.expect({.local = 1, .groups = 1, .global = 1, .offset = 0, .seed = 0});
  fixture.expectAllocationArguments({{.alias = 0, .offset = 0}, {.alias = 0, .offset = 4}});
  REQUIRE(cuModuleLoadData(&module, ptx.c_str()) == CUDA_SUCCESS);
  REQUIRE(cuModuleGetFunction(&function, module, "probe") == CUDA_SUCCESS);
  REQUIRE(cuMemAlloc_v2(&allocation, bytes) == CUDA_SUCCESS);
  std::vector<uint8_t> packed(24, 0xff);
  store64(packed, 0, allocation);
  store64(packed, 16, allocation + 4);
  size_t packedSize = packed.size();
  void *extra[]{LaunchParamBufferSize, &packedSize, LaunchParamBufferPointer, packed.data(), CU_LAUNCH_PARAM_END};
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
}

TEST_CASE("CUDA architecture- and family-specific targets report their compute capability", "[cuda][code-mode]") {
  gemstone_test::RpcFixture const fixture;
  const auto endpoint = fixture.endpoint();
  const gemstone_test::Environment rpcEndpoint(gemstone::environment::RpcEndpoint, endpoint.c_str());
  const gemstone_test::Environment rpcClient(gemstone::environment::RpcClient, "42");
  for (const auto &[target, major, minor] :
       {std::tuple{"sm_90a", 9, 0}, std::tuple{"sm_100f", 10, 0}, std::tuple{"sm_103a", 10, 3}, std::tuple{"compute_120f", 12, 0}}) {
    const gemstone_test::Environment targetArch(gemstone::environment::TargetArch, target);
    REQUIRE((cuInit(0) == CUDA_SUCCESS));
    int value = 0;
    REQUIRE((cuDeviceGetAttribute(&value, CU_DEVICE_ATTRIBUTE_COMPUTE_CAPABILITY_MAJOR, 0) == CUDA_SUCCESS));
    CHECK(value == major);
    REQUIRE((cuDeviceGetAttribute(&value, CU_DEVICE_ATTRIBUTE_COMPUTE_CAPABILITY_MINOR, 0) == CUDA_SUCCESS));
    CHECK(value == minor);
  }
}

TEST_CASE("CUDA module and kernel lookup failures follow the failure kind and image kind", "[cuda][code-mode]") {
  using gemstone::execution::FailureKind;
  FailureSession session;
  const auto cubin = minimalCubin();
  const std::string ptx(Ptx);

  const auto rejects = [&](const void *image, FailureKind kind) {
    session.fixture.rejectNext(gemstone::execution::operation::LoadProgram, kind);
    session.module = nullptr;
    return session.load(image);
  };
  CHECK(rejects(ptx.c_str(), FailureKind::InvalidProgram) == CUDA_ERROR_INVALID_PTX);
  CHECK(rejects(cubin.data(), FailureKind::InvalidProgram) == CUDA_ERROR_INVALID_IMAGE);
  CHECK(rejects(ptx.c_str(), FailureKind::Unsupported) == CUDA_ERROR_NOT_SUPPORTED);
  CHECK(rejects(ptx.c_str(), FailureKind::InvalidRequest) == CUDA_ERROR_INVALID_VALUE);
  CHECK(rejects(ptx.c_str(), FailureKind::Failed) == CUDA_ERROR_UNKNOWN);
  CHECK(session.module == nullptr);

  REQUIRE(session.load(ptx.c_str()) == CUDA_SUCCESS);
  const auto lookup = [&](FailureKind kind) {
    session.fixture.rejectNext(gemstone::execution::operation::DescribeKernel, kind);
    return cuModuleGetFunction(&session.function, session.module, "probe");
  };
  CHECK(lookup(FailureKind::InvalidProgram) == CUDA_ERROR_INVALID_PTX);
  CHECK(lookup(FailureKind::NotFound) == CUDA_ERROR_NOT_FOUND);
  CHECK(lookup(FailureKind::Failed) == CUDA_ERROR_NOT_FOUND);
  CHECK(cuModuleUnload(session.module) == CUDA_SUCCESS);
}

TEST_CASE("CUDA dispatch failures that leave the context usable are reported once", "[cuda][code-mode]") {
  using gemstone::execution::FailureKind;
  FailureSession session;
  const auto cubin = minimalCubin();
  const std::string ptx(Ptx);
  const struct {
    FailureKind kind;
    bool cubinModule;
    CUresult expected;
  } cases[]{
      {.kind = FailureKind::OutOfResources, .cubinModule = false, .expected = CUDA_ERROR_LAUNCH_OUT_OF_RESOURCES},
      {.kind = FailureKind::InvalidProgram, .cubinModule = false, .expected = CUDA_ERROR_INVALID_PTX},
      {.kind = FailureKind::InvalidProgram, .cubinModule = true, .expected = CUDA_ERROR_INVALID_IMAGE},
      {.kind = FailureKind::Unsupported, .cubinModule = false, .expected = CUDA_ERROR_NOT_SUPPORTED},
      {.kind = FailureKind::InvalidRequest, .cubinModule = false, .expected = CUDA_ERROR_INVALID_VALUE},
      {.kind = FailureKind::NotFound, .cubinModule = false, .expected = CUDA_ERROR_NOT_FOUND},
  };
  for (const auto &[kind, cubinModule, expected] : cases) {
    CAPTURE(static_cast<int>(kind), cubinModule);
    session.loadKernel(cubinModule ? static_cast<const void *>(cubin.data()) : ptx.c_str());
    session.fixture.failNextCompletion(kind);
    REQUIRE(session.launch() == CUDA_SUCCESS);
    CHECK(cuCtxSynchronize() == expected);
    CHECK(cuCtxSynchronize() == CUDA_SUCCESS);
    CHECK(cuStreamQuery(nullptr) == CUDA_SUCCESS);
    CHECK(cudaGetLastError() == cudaSuccess);
    REQUIRE(session.launch() == CUDA_SUCCESS);
    CHECK(cuCtxSynchronize() == CUDA_SUCCESS);
    REQUIRE(cuModuleUnload(session.module) == CUDA_SUCCESS);
  }
}

TEST_CASE("CUDA kernels that fail to load fail their launch, leaving the following synchronisation clean", "[cuda][code-mode]") {
  using gemstone::execution::FailureKind;
  FailureSession session;
  const auto cubin = minimalCubin();
  const std::string ptx(Ptx);
  const struct {
    bool cubinModule;
    CUresult driver;
    cudaError_t runtime;
  } cases[]{
      {.cubinModule = false, .driver = CUDA_ERROR_INVALID_PTX, .runtime = cudaErrorInvalidPtx},
      {.cubinModule = true, .driver = CUDA_ERROR_INVALID_IMAGE, .runtime = cudaErrorInvalidKernelImage},
  };
  for (const auto &[cubinModule, driver, runtime] : cases) {
    CAPTURE(cubinModule);
    session.loadKernel(cubinModule ? static_cast<const void *>(cubin.data()) : ptx.c_str());
    session.fixture.failNextCompletion(FailureKind::InvalidProgram);
    session.fixture.expect({.local = 1, .groups = 1, .global = 1, .offset = 0, .seed = 0});
    void *arguments[]{&session.allocation};
    CHECK(__cudaLaunchKernel(reinterpret_cast<cudaKernel_t>(session.function), dim3(1), dim3(1), arguments, 0, nullptr) == runtime);
    CHECK(cudaGetLastError() == runtime);
    CHECK(cudaDeviceSynchronize() == cudaSuccess);
    CHECK(cudaGetLastError() == cudaSuccess);

    session.fixture.failNextCompletion(FailureKind::InvalidProgram);
    CHECK(session.launch() == driver);
    CHECK(cuCtxSynchronize() == CUDA_SUCCESS);
    REQUIRE(cuModuleUnload(session.module) == CUDA_SUCCESS);
  }
}

namespace {

// A context-corrupting failure outlives its launch, so each kind runs in a process of its own.
void checkStickyFailure(gemstone::execution::FailureKind kind, CUresult expected) {
  FailureSession session;
  const std::string ptx(Ptx);
  session.loadKernel(ptx.c_str());

  session.fixture.failNextCompletion(kind);
  REQUIRE(session.launch() == CUDA_SUCCESS);
  CHECK(cuCtxSynchronize() == expected);
  CHECK(cuCtxSynchronize() == expected);
  CHECK(cuStreamQuery(nullptr) == expected);
  CHECK(session.launch() == expected);
  CHECK(cudaPeekAtLastError() == static_cast<cudaError_t>(expected));
  CHECK(cudaGetLastError() == static_cast<cudaError_t>(expected));
  CHECK(cudaGetLastError() == static_cast<cudaError_t>(expected));
  CHECK(cuDevicePrimaryCtxReset(0) == CUDA_SUCCESS);
  CHECK(cuCtxSynchronize() == CUDA_SUCCESS);
}

} // namespace

TEST_CASE("CUDA illegal addresses persist", "[cuda][code-mode]") {
  checkStickyFailure(gemstone::execution::FailureKind::IllegalAddress, CUDA_ERROR_ILLEGAL_ADDRESS);
}

TEST_CASE("CUDA misaligned addresses persist", "[cuda][code-mode]") {
  checkStickyFailure(gemstone::execution::FailureKind::MisalignedAddress, CUDA_ERROR_MISALIGNED_ADDRESS);
}

TEST_CASE("CUDA illegal instructions persist", "[cuda][code-mode]") {
  checkStickyFailure(gemstone::execution::FailureKind::IllegalInstruction, CUDA_ERROR_ILLEGAL_INSTRUCTION);
}

TEST_CASE("CUDA traps persist as launch failures", "[cuda][code-mode]") {
  checkStickyFailure(gemstone::execution::FailureKind::Trapped, CUDA_ERROR_LAUNCH_FAILED);
}

TEST_CASE("CUDA failed assertions persist", "[cuda][code-mode]") {
  checkStickyFailure(gemstone::execution::FailureKind::AssertionFailed, CUDA_ERROR_ASSERT);
}

TEST_CASE("CUDA launch timeouts persist", "[cuda][code-mode]") {
  checkStickyFailure(gemstone::execution::FailureKind::TimedOut, CUDA_ERROR_LAUNCH_TIMEOUT);
}

TEST_CASE("CUDA unclassified dispatch failures persist as launch failures", "[cuda][code-mode]") {
  checkStickyFailure(gemstone::execution::FailureKind::Failed, CUDA_ERROR_LAUNCH_FAILED);
}

TEST_CASE("CUDA launch configurations outside the device limits are invalid configurations", "[cuda][code-mode]") {
  FailureSession session;
  const std::string ptx(Ptx);
  session.loadKernel(ptx.c_str());
  void *arguments[]{&session.allocation};
  const struct {
    unsigned gridY, blockX, blockY, blockZ;
  } limits[]{
      {.gridY = 1, .blockX = 1025, .blockY = 1, .blockZ = 1},  {.gridY = 1, .blockX = 1, .blockY = 1025, .blockZ = 1},
      {.gridY = 1, .blockX = 1, .blockY = 1, .blockZ = 65},    {.gridY = 1, .blockX = 33, .blockY = 32, .blockZ = 1},
      {.gridY = 65536, .blockX = 1, .blockY = 1, .blockZ = 1}, {.gridY = 0, .blockX = 1, .blockY = 1, .blockZ = 1},
      {.gridY = 1, .blockX = 0, .blockY = 1, .blockZ = 1},
  };
  for (const auto &[gridY, blockX, blockY, blockZ] : limits) {
    CAPTURE(gridY, blockX, blockY, blockZ);
    CHECK(cuLaunchKernel(session.function, 1, gridY, 1, blockX, blockY, blockZ, 0, nullptr, arguments, nullptr)
          == CUDA_ERROR_INVALID_VALUE);
    CHECK(__cudaLaunchKernel(reinterpret_cast<cudaKernel_t>(session.function), dim3(1, gridY, 1), dim3(blockX, blockY, blockZ), arguments,
                             0, nullptr)
          == cudaErrorInvalidConfiguration);
    CHECK(cudaGetLastError() == cudaErrorInvalidConfiguration);
    CHECK(cuCtxSynchronize() == CUDA_SUCCESS);
  }
}

TEST_CASE("CUDA names every error the facade reports", "[cuda][code-mode]") {
  const auto [driver, runtime, driverName, runtimeName] = GENERATE(table<CUresult, cudaError_t, const char *, const char *>({
      {CUDA_ERROR_INVALID_IMAGE, cudaErrorInvalidKernelImage, "CUDA_ERROR_INVALID_IMAGE", "cudaErrorInvalidKernelImage"},
      {CUDA_ERROR_NO_BINARY_FOR_GPU, cudaErrorNoKernelImageForDevice, "CUDA_ERROR_NO_BINARY_FOR_GPU", "cudaErrorNoKernelImageForDevice"},
      {CUDA_ERROR_INVALID_PTX, cudaErrorInvalidPtx, "CUDA_ERROR_INVALID_PTX", "cudaErrorInvalidPtx"},
      {CUDA_ERROR_ILLEGAL_ADDRESS, cudaErrorIllegalAddress, "CUDA_ERROR_ILLEGAL_ADDRESS", "cudaErrorIllegalAddress"},
      {CUDA_ERROR_LAUNCH_OUT_OF_RESOURCES, cudaErrorLaunchOutOfResources, "CUDA_ERROR_LAUNCH_OUT_OF_RESOURCES",
       "cudaErrorLaunchOutOfResources"},
      {CUDA_ERROR_LAUNCH_TIMEOUT, cudaErrorLaunchTimeout, "CUDA_ERROR_LAUNCH_TIMEOUT", "cudaErrorLaunchTimeout"},
      {CUDA_ERROR_ASSERT, cudaErrorAssert, "CUDA_ERROR_ASSERT", "cudaErrorAssert"},
      {CUDA_ERROR_ILLEGAL_INSTRUCTION, cudaErrorIllegalInstruction, "CUDA_ERROR_ILLEGAL_INSTRUCTION", "cudaErrorIllegalInstruction"},
      {CUDA_ERROR_MISALIGNED_ADDRESS, cudaErrorMisalignedAddress, "CUDA_ERROR_MISALIGNED_ADDRESS", "cudaErrorMisalignedAddress"},
      {CUDA_ERROR_LAUNCH_FAILED, cudaErrorLaunchFailure, "CUDA_ERROR_LAUNCH_FAILED", "cudaErrorLaunchFailure"},
  }));
  CHECK(static_cast<int>(driver) == static_cast<int>(runtime));
  const char *name = nullptr;
  REQUIRE(cuGetErrorName(driver, &name) == CUDA_SUCCESS);
  CHECK(std::string(name) == driverName);
  CHECK(std::string(cudaGetErrorName(runtime)) == runtimeName);
  CHECK(std::string(cudaGetErrorName(cudaErrorInvalidConfiguration)) == "cudaErrorInvalidConfiguration");
  CHECK(cuGetErrorName(static_cast<CUresult>(cudaErrorInvalidConfiguration), &name) == CUDA_ERROR_INVALID_VALUE);
}
