#include <array>
#include <chrono>
#include <cstdint>
#include <cstdio>
#include <future>
#include <string>
#include <string_view>
#include <utility>

#include <dlfcn.h>

#include <catch2/catch_test_macros.hpp>
#include <cuda.h>
#include <cuda_runtime_api.h>

#include <gemstone/test_support.hpp>

#include <environment_keys.hpp>

namespace {

struct TargetCase {
  const char *value;
  int major;
  int minor;
};

bool targetSelectionWorks() {
  constexpr std::array cases{
      TargetCase{.value = "sm_75", .major = 7, .minor = 5},
      TargetCase{.value = "compute_89", .major = 8, .minor = 9},
      TargetCase{.value = "sm89", .major = 8, .minor = 9},
      TargetCase{.value = "compute90", .major = 9, .minor = 0},
  };
  bool valid = true;
  for (const auto &target : cases) {
    const gemstone_test::Environment targetArch(gemstone::environment::TargetArch, target.value);
    int major = -1;
    int minor = -1;
    const CUresult status = cuDeviceComputeCapability(&major, &minor, 0);
    if (status != CUDA_SUCCESS || major != target.major || minor != target.minor) {
      std::fprintf(stderr, "target case %s returned status=%d capability=%d.%d, expected %d.%d\n", target.value, static_cast<int>(status),
                   major, minor, target.major, target.minor);
      valid = false;
    }
  }
  return valid;
}

bool driverErrorQueryWorks() {
  const char *name = nullptr;
  const char *message = nullptr;
  const CUresult nameStatus = cuGetErrorName(CUDA_ERROR_INVALID_VALUE, &name);
  const CUresult messageStatus = cuGetErrorString(CUDA_ERROR_INVALID_VALUE, &message);
  if (nameStatus == CUDA_SUCCESS && messageStatus == CUDA_SUCCESS && name && message && name == std::string_view{"CUDA_ERROR_INVALID_VALUE"}
      && message[0] != '\0')
    return true;
  std::fprintf(stderr, "driver error query returned name=%d message=%d name-value=%p message-value=%p\n", static_cast<int>(nameStatus),
               static_cast<int>(messageStatus), static_cast<const void *>(name), static_cast<const void *>(message));
  return false;
}

struct Lookup {
  const char *symbol;
  int version;
  CUdriverProcAddressQueryResult status;
  const char *expected;
};

// Exported only as the not-supported stub, which stands in for a revision whose ABI no export provides.
constexpr const char *Unsupported = "cuCtxAttach";

// Each row names the exported symbol cuGetProcAddress must return; the CUDA 3.2 split selects the legacy ABI.
constexpr std::array Lookups{
    Lookup{.symbol = "cuMemAlloc", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuMemAlloc_v2"},
    Lookup{.symbol = "cuMemAlloc", .version = 3010, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = Unsupported},
    Lookup{.symbol = "cuMemAlloc_v2", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuMemAlloc_v2"},
    Lookup{.symbol = "cuArrayCreate", .version = 3010, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuArrayCreate"},
    Lookup{.symbol = "cuArrayCreate", .version = 3020, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuArrayCreate_v2"},
    Lookup{.symbol = "cuArrayCreate", .version = 0, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuArrayCreate_v2"},
    Lookup{.symbol = "cuModuleGetGlobal", .version = 2000, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuModuleGetGlobal"},
    Lookup{.symbol = "cuModuleGetGlobal", .version = 12000, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuModuleGetGlobal_v2"},
    Lookup{.symbol = "cuMemcpy2D", .version = 3000, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuMemcpy2D"},
    Lookup{.symbol = "cuMemcpy2D", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuMemcpy2D_v2"},
    Lookup{.symbol = "cuDeviceTotalMem", .version = 3010, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuDeviceTotalMem"},
    Lookup{.symbol = "cuDeviceTotalMem", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuDeviceTotalMem_v2"},
    Lookup{.symbol = "cuMemcpyHtoD", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuMemcpyHtoD_v2"},
    Lookup{.symbol = "cuMemcpyHtoDAsync", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuMemcpyHtoDAsync_v2"},
    Lookup{.symbol = "cuMemsetD8", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuMemsetD8_v2"},
    Lookup{.symbol = "cuMemAllocPitch", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuMemAllocPitch_v2"},
    Lookup{.symbol = "cuMemHostGetDevicePointer",
           .version = 13030,
           .status = CU_GET_PROC_ADDRESS_SUCCESS,
           .expected = "cuMemHostGetDevicePointer_v2"},
    Lookup{
        .symbol = "cuMemGetAddressRange", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuMemGetAddressRange_v2"},
    Lookup{.symbol = "cuCtxDestroy", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuCtxDestroy_v2"},
    Lookup{.symbol = "cuEventDestroy", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuEventDestroy_v2"},
    Lookup{.symbol = "cuEventElapsedTime", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuEventElapsedTime_v2"},
    Lookup{.symbol = "cuLinkCreate", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuLinkCreate_v2"},
    Lookup{.symbol = "cuLinkAddData", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuLinkAddData_v2"},
    Lookup{.symbol = "cuCtxCreate_v2", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuCtxCreate_v2"},
    Lookup{.symbol = "cuCtxCreate", .version = 11030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuCtxCreate"},
    Lookup{.symbol = "cuCtxCreate", .version = 11040, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuCtxCreate_v3"},
    Lookup{.symbol = "cuCtxCreate", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuCtxCreate_v4"},
    Lookup{.symbol = "cuGetProcAddress", .version = 11030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuGetProcAddress"},
    Lookup{.symbol = "cuGetProcAddress", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuGetProcAddress_v2"},
    Lookup{.symbol = "cuCtxSynchronize", .version = 12080, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuCtxSynchronize"},
    Lookup{.symbol = "cuCtxSynchronize", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuCtxSynchronize_v2"},
    Lookup{.symbol = "cuMemAllocHost", .version = 3010, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuMemAllocHost"},
    Lookup{.symbol = "cuMemAllocHost", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuMemAllocHost_v2"},
    Lookup{.symbol = "cuLaunchHostFunc", .version = 13000, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuLaunchHostFunc"},
    Lookup{.symbol = "cuLaunchHostFunc", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuLaunchHostFunc_v2"},
    Lookup{.symbol = "cuCtxDestroy", .version = 3020, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = Unsupported},
    Lookup{.symbol = "cuLaunchKernelEx", .version = 11000, .status = CU_GET_PROC_ADDRESS_VERSION_NOT_SUFFICIENT, .expected = nullptr},
    Lookup{.symbol = "cuStreamDestroy", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuStreamDestroy_v2"},
    Lookup{.symbol = "cuMemcpyHtoD_ptds", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuMemcpyHtoD_v2"},
    Lookup{.symbol = "cuStreamQuery_ptsz", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuStreamQuery"},
    Lookup{.symbol = "cuGetProcAddress_v2", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuGetProcAddress_v2"},
    Lookup{.symbol = "cuLaunchKernel", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuLaunchKernel"},
    Lookup{.symbol = "cuLaunchKernel_ptsz", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuLaunchKernel_ptsz"},
    Lookup{.symbol = "cuLaunchKernelEx", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuLaunchKernelEx"},
    Lookup{.symbol = "cuStreamGetCaptureInfo", .version = 10000, .status = CU_GET_PROC_ADDRESS_VERSION_NOT_SUFFICIENT, .expected = nullptr},
    Lookup{
        .symbol = "cuStreamGetCaptureInfo", .version = 10010, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuStreamGetCaptureInfo"},
    Lookup{.symbol = "cuStreamGetCaptureInfo",
           .version = 11030,
           .status = CU_GET_PROC_ADDRESS_SUCCESS,
           .expected = "cuStreamGetCaptureInfo_v2"},
    Lookup{.symbol = "cuStreamGetCaptureInfo",
           .version = 12030,
           .status = CU_GET_PROC_ADDRESS_SUCCESS,
           .expected = "cuStreamGetCaptureInfo_v3"},
    Lookup{.symbol = "cuStreamGetCaptureInfo_ptsz",
           .version = 13030,
           .status = CU_GET_PROC_ADDRESS_SUCCESS,
           .expected = "cuStreamGetCaptureInfo_v3"},
    Lookup{.symbol = "cuStreamGetCaptureInfo_v2_ptsz",
           .version = 13030,
           .status = CU_GET_PROC_ADDRESS_SUCCESS,
           .expected = "cuStreamGetCaptureInfo_v2"},
    Lookup{
        .symbol = "cuStreamIsCapturing_ptsz", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuStreamIsCapturing"},
    Lookup{.symbol = "cuMemAllocAsync", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuMemAllocAsync"},
    Lookup{.symbol = "cuMemsetD8Async", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuMemsetD8Async"},
    Lookup{
        .symbol = "cuEventRecordWithFlags", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuEventRecordWithFlags"},
    Lookup{.symbol = "cuLibraryLoadData", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuLibraryLoadData"},
    Lookup{.symbol = "cuGetExportTable", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuGetExportTable"},
    Lookup{.symbol = "cuGraphCreate", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuGraphCreate"},
    Lookup{.symbol = "cuMemAdvise", .version = 12000, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuMemAdvise"},
    Lookup{.symbol = "cuMemAdvise", .version = 12020, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuMemAdvise_v2"},
    Lookup{.symbol = "cuMemAdvise", .version = 13030, .status = CU_GET_PROC_ADDRESS_SUCCESS, .expected = "cuMemAdvise_v2"},
    Lookup{.symbol = "cuDefinitelyMissing", .version = 13030, .status = CU_GET_PROC_ADDRESS_SYMBOL_NOT_FOUND, .expected = nullptr},
    Lookup{.symbol = "cudaMalloc", .version = 13030, .status = CU_GET_PROC_ADDRESS_SYMBOL_NOT_FOUND, .expected = nullptr},
    Lookup{.symbol = "", .version = 13030, .status = CU_GET_PROC_ADDRESS_SYMBOL_NOT_FOUND, .expected = nullptr},
};

bool driverEntryPointQueryWorks() {
  bool valid = true;
  for (const auto flags : {CU_GET_PROC_ADDRESS_DEFAULT, CU_GET_PROC_ADDRESS_PER_THREAD_DEFAULT_STREAM}) {
    for (const auto &lookup : Lookups) {
      void *resolved = reinterpret_cast<void *>(uintptr_t{1});
      auto symbolStatus = static_cast<CUdriverProcAddressQueryResult>(-1);
      const CUresult status = cuGetProcAddress(lookup.symbol, &resolved, lookup.version, flags, &symbolStatus);
      void *expected = lookup.expected ? dlsym(RTLD_DEFAULT, lookup.expected) : nullptr;
      if (status != CUDA_SUCCESS || symbolStatus != lookup.status || resolved != expected || (lookup.expected && !expected)) {
        std::fprintf(stderr, "lookup %s@%d returned status=%d symbol-status=%d resolved=%p, expected %s=%p\n", lookup.symbol,
                     lookup.version, static_cast<int>(status), static_cast<int>(symbolStatus), resolved,
                     lookup.expected ? lookup.expected : "null", expected);
        valid = false;
      }
    }
  }
  const auto legacy = reinterpret_cast<CUresult (*)(const char *, void **, int, cuuint64_t)>(dlsym(RTLD_DEFAULT, "cuGetProcAddress"));
  void *resolved = nullptr;
  if (!legacy || legacy("cuMemAlloc", &resolved, 13030, 0) != CUDA_SUCCESS || resolved != dlsym(RTLD_DEFAULT, "cuMemAlloc_v2")
      || legacy("cuDefinitelyMissing", &resolved, 13030, 0) != CUDA_ERROR_NOT_FOUND) {
    std::fprintf(stderr, "legacy cuGetProcAddress lookup failed\n");
    valid = false;
  }
  return valid;
}

template <class Function> Function entryPoint(const char *symbol) {
  void *resolved = nullptr;
  CUdriverProcAddressQueryResult symbolStatus{};
  return cuGetProcAddress(symbol, &resolved, CUDA_VERSION, CU_GET_PROC_ADDRESS_DEFAULT, &symbolStatus) == CUDA_SUCCESS
                 && symbolStatus == CU_GET_PROC_ADDRESS_SUCCESS
             ? reinterpret_cast<Function>(resolved)
             : nullptr;
}

bool resolvedEntryPointsBehave() {
  const auto attribute = entryPoint<CUresult (*)(int *, CUdevice_attribute, CUdevice)>("cuDeviceGetAttribute");
  const auto memoryInfo = entryPoint<CUresult (*)(size_t *, size_t *)>("cuMemGetInfo");
  const auto errorName = entryPoint<CUresult (*)(CUresult, const char **)>("cuGetErrorName");
  int warp = 0;
  size_t free = 0;
  size_t total = 0;
  const char *name = nullptr;
  return attribute && memoryInfo && errorName && attribute(&warp, CU_DEVICE_ATTRIBUTE_WARP_SIZE, 0) == CUDA_SUCCESS && warp == 32
         && memoryInfo(&free, &total) == CUDA_SUCCESS && total != 0 && errorName(CUDA_ERROR_NOT_FOUND, &name) == CUDA_SUCCESS
         && name == std::string_view{"CUDA_ERROR_NOT_FOUND"};
}

using ExportTableQuery = CUresult (*)(const void **, const void *);
using DriverStateQuery = CUresult (*)(int *);
using CallbackSlotQuery = CUresult (*)(void **, void **, unsigned, unsigned);
using DriverVersionQuery = CUresult (*)(int *);

struct ToolsContextInfo {
  uint64_t size;
  CUcontext context;
  uint64_t reserved;
};

struct ToolsRunRequest {
  uint64_t first;
  int (*function)(void *, const ToolsContextInfo *);
  void *argument;
  uint64_t second;
  uint64_t third;
  uint64_t status;
};

using ToolsRun = CUresult (*)(ToolsRunRequest *);

struct ToolsRunObservation {
  int calls = 0;
  ToolsContextInfo info{};
};

int observeToolsRun(void *argument, const ToolsContextInfo *info) {
  auto &observation = *static_cast<ToolsRunObservation *>(argument);
  observation.calls++;
  observation.info = *info;
  return 7;
}

using ToolsInterfaceQuery = void *(*)(int);
using ToolsWordsQuery = CUresult (*)(uint64_t *, uint64_t *, unsigned);
using ToolsCountQuery = CUresult (*)(uint64_t *, void **, unsigned);
using ToolsLimitQuery = CUresult (*)(uint32_t *);
using ToolsSubscribe = CUresult (*)(uint32_t *, void *, void *);
using ToolsRelease = CUresult (*)(uint32_t);
using ToolsEnableAll = CUresult (*)(int, uint32_t);
using ToolsEnableDomain = CUresult (*)(int, uint32_t, uint32_t);
using ToolsEnableCallback = CUresult (*)(int, uint32_t, uint32_t, uint32_t);

constexpr std::array<unsigned char, 16> ToolsDriverStateId{0xd7, 0xbd, 0x8a, 0xf8, 0xe7, 0xac, 0xca, 0x4a,
                                                           0xbd, 0xbc, 0xfb, 0x1f, 0x1b, 0x3f, 0xcf, 0x33};
constexpr std::array<unsigned char, 16> ToolsSubscriberId{0x2c, 0x8e, 0x0a, 0xd8, 0x07, 0x10, 0xab, 0x4e,
                                                          0x90, 0xdd, 0x54, 0x71, 0x9f, 0xe5, 0xf7, 0x4b};
constexpr std::array<unsigned char, 16> ToolsInterfaceId{0x41, 0xae, 0x71, 0x7d, 0xa9, 0xeb, 0xa6, 0x4f,
                                                         0x99, 0x9b, 0xe3, 0xc2, 0x7f, 0x3c, 0x14, 0x0f};
constexpr std::array<unsigned char, 16> ToolsCallbackRegistryId{0x7c, 0xb6, 0x3f, 0x9b, 0x9c, 0xa4, 0x8e, 0x42,
                                                                0xa4, 0x84, 0xbe, 0xb6, 0x6e, 0x6a, 0xed, 0x7f};

const void *const *toolsTable(const std::array<unsigned char, 16> &id) {
  const auto query = reinterpret_cast<ExportTableQuery>(dlsym(RTLD_DEFAULT, "cuGetExportTable"));
  const void *table = nullptr;
  return query && query(&table, id.data()) == CUDA_SUCCESS ? static_cast<const void *const *>(table) : nullptr;
}

template <class Function> Function tableEntry(const void *const *table, size_t index) {
  return reinterpret_cast<Function>(reinterpret_cast<uintptr_t>(table[index]));
}

} // namespace

TEST_CASE("CUDA facade fails closed without execution service", "[cuda]") {
  const gemstone_test::Environment noClient(gemstone::environment::RpcClientId, nullptr);
  const gemstone_test::Environment noEndpoint(gemstone::environment::RpcEndpoint, nullptr);
  const CUresult status = cuInit(0);
  REQUIRE((status == CUDA_ERROR_NOT_INITIALIZED));
  REQUIRE((targetSelectionWorks()));
  REQUIRE((driverErrorQueryWorks()));
  REQUIRE((driverEntryPointQueryWorks()));
  REQUIRE((resolvedEntryPointsBehave()));
}

TEST_CASE("CUDA runtime stream synchronisation waits only for the requested stream", "[cuda]") {
  CUcontext context = nullptr;
  REQUIRE((cuCtxCreate(&context, nullptr, 0, 0) == CUDA_SUCCESS));
  CUstream blocked = nullptr;
  CUstream idle = nullptr;
  REQUIRE((cuStreamCreate(&blocked, CU_STREAM_NON_BLOCKING) == CUDA_SUCCESS));
  REQUIRE((cuStreamCreate(&idle, CU_STREAM_NON_BLOCKING) == CUDA_SUCCESS));
  std::promise<void> release;
  auto released = release.get_future().share();
  REQUIRE(
      (cuLaunchHostFunc(blocked, [](void *gate) { static_cast<std::shared_future<void> *>(gate)->wait(); }, &released) == CUDA_SUCCESS));
  auto synchronised = std::async(std::launch::async, [context, idle] {
    return cuCtxSetCurrent(context) == CUDA_SUCCESS ? cudaStreamSynchronize(idle) : cudaErrorInvalidValue;
  });
  const bool prompt = synchronised.wait_for(gemstone_test::scaled(std::chrono::seconds(5))) == std::future_status::ready;
  release.set_value();
  CHECK(prompt);
  CHECK((synchronised.get() == cudaSuccess));
  CHECK((cuStreamSynchronize(blocked) == CUDA_SUCCESS));
  CHECK((cuStreamDestroy(blocked) == CUDA_SUCCESS));
  CHECK((cuStreamDestroy(idle) == CUDA_SUCCESS));
  CHECK((cuCtxDestroy(context) == CUDA_SUCCESS));
}

TEST_CASE("CUDA runtime channel descriptors carry their components", "[cuda]") {
  const auto descriptor = cudaCreateChannelDesc(8, 16, 0, 32, cudaChannelFormatKindFloat);
  CHECK((descriptor.x == 8 && descriptor.y == 16 && descriptor.z == 0 && descriptor.w == 32));
  CHECK((descriptor.f == cudaChannelFormatKindFloat));
}

TEST_CASE("CUDA tools driver-state table validates its output", "[cuda]") {
  const auto *const table = toolsTable(ToolsDriverStateId);
  REQUIRE((table != nullptr));
  CHECK((reinterpret_cast<uintptr_t>(table[0]) == 112));
  const auto query = tableEntry<DriverStateQuery>(table, 3);
  REQUIRE((query != nullptr));
  CHECK((query(nullptr) == CUDA_ERROR_INVALID_VALUE));
}

TEST_CASE("CUDA tools callback registry keeps the first table and bounds-checks slots", "[cuda]") {
  const auto *const table = toolsTable(ToolsCallbackRegistryId);
  REQUIRE((table != nullptr));
  CHECK((reinterpret_cast<uintptr_t>(table[0]) == 560));
  const auto slot = tableEntry<CallbackSlotQuery>(table, 67);
  REQUIRE((slot != nullptr));
  void *first[4] = {reinterpret_cast<void *>(0xa0), reinterpret_cast<void *>(0xa1), reinterpret_cast<void *>(0xa2),
                    reinterpret_cast<void *>(0xa3)};
  void *second[4] = {reinterpret_cast<void *>(0xb0), reinterpret_cast<void *>(0xb1), reinterpret_cast<void *>(0xb2),
                     reinterpret_cast<void *>(0xb3)};
  void *out = reinterpret_cast<void *>(0x77);
  CHECK((slot(nullptr, first, 1, 4) == CUDA_ERROR_INVALID_HANDLE));
  CHECK((slot(&out, nullptr, 1, 4) == CUDA_ERROR_INVALID_HANDLE));
  CHECK((out == reinterpret_cast<void *>(0x77)));
  CHECK((slot(&out, first, 4, 4) == CUDA_ERROR_INVALID_VALUE));
  CHECK((out == nullptr));
  CHECK((slot(&out, first, 2, 4) == CUDA_SUCCESS));
  CHECK((out == reinterpret_cast<void *>(0xa2)));
  CHECK((slot(&out, second, 1, 2) == CUDA_SUCCESS));
  CHECK((out == reinterpret_cast<void *>(0xa1)));
  CHECK((slot(&out, second, 3, 2) == CUDA_SUCCESS));
  CHECK((out == reinterpret_cast<void *>(0xa3)));
  CHECK((slot(&out, second, 4, 2) == CUDA_ERROR_INVALID_VALUE));
  CHECK((out == nullptr));
}

TEST_CASE("CUDA tools subscriber table reports interface constants and rejects null outputs", "[cuda]") {
  const auto *const table = toolsTable(ToolsSubscriberId);
  REQUIRE((table != nullptr));
  CHECK((reinterpret_cast<uintptr_t>(table[0]) == 96));
  const auto words = tableEntry<ToolsWordsQuery>(table, 1);
  const auto counts = tableEntry<ToolsCountQuery>(table, 2);
  const auto limit = tableEntry<ToolsLimitQuery>(table, 10);
  REQUIRE((words != nullptr && counts != nullptr && limit != nullptr));
  uint64_t first = 0xdeadbeef;
  uint64_t second = 0xdeadbeef;
  CHECK((words(&first, &second, 0xf) == CUDA_SUCCESS));
  CHECK((first == 0x21 && second == 0));
  CHECK((words(nullptr, &second, 0xf) == CUDA_ERROR_INVALID_VALUE));
  CHECK((words(&first, nullptr, 0xf) == CUDA_ERROR_INVALID_VALUE));
  uint32_t features = 0;
  CHECK((limit(&features) == CUDA_SUCCESS));
  CHECK((features == 23));
  CHECK((limit(nullptr) == CUDA_ERROR_INVALID_VALUE));
  uint64_t count = 0xdeadbeef;
  void *list = nullptr;
  CHECK((counts(nullptr, &list, 2) == CUDA_ERROR_INVALID_VALUE));
  CHECK((counts(&count, nullptr, 2) == CUDA_ERROR_INVALID_VALUE));
  CHECK((counts(&count, &list, 2) == CUDA_SUCCESS));
  CHECK((count == 105));
  CHECK((counts(&count, &list, 6) == CUDA_SUCCESS));
  CHECK((count == 877));
  CHECK((counts(&count, &list, 32) == CUDA_SUCCESS));
  CHECK((count == 2));
  CHECK((counts(&count, &list, 0) == CUDA_SUCCESS));
  CHECK((count == 0));
  CHECK((counts(&count, &list, 33) == CUDA_SUCCESS));
  CHECK((count == 0));
}

TEST_CASE("CUDA tools subscriber table admits one subscriber and validates its enables", "[cuda]") {
  const auto *const table = toolsTable(ToolsSubscriberId);
  REQUIRE((table != nullptr));
  CHECK((reinterpret_cast<uintptr_t>(table[0]) == 96));
  const auto subscribe = tableEntry<ToolsSubscribe>(table, 11);
  const auto release = tableEntry<ToolsRelease>(table, 4);
  const auto enableAll = tableEntry<ToolsEnableAll>(table, 8);
  const auto enableDomain = tableEntry<ToolsEnableDomain>(table, 7);
  const auto enableCallback = tableEntry<ToolsEnableCallback>(table, 6);
  const auto enableFeature = tableEntry<ToolsEnableDomain>(table, 9);
  REQUIRE((subscribe && release && enableAll && enableDomain && enableCallback && enableFeature));
  auto *const callback = reinterpret_cast<void *>(0x1000);

  CHECK((release(1) == CUDA_ERROR_UNKNOWN));

  uint32_t id = 0;
  CHECK((subscribe(nullptr, callback, nullptr) == CUDA_ERROR_INVALID_VALUE));
  CHECK((subscribe(&id, nullptr, nullptr) == CUDA_ERROR_INVALID_VALUE));
  REQUIRE((subscribe(&id, callback, nullptr) == CUDA_SUCCESS));
  CHECK((id == 1));
  uint32_t other = 0;
  CHECK((subscribe(&other, callback, nullptr) == 210));
  CHECK((other == 0));

  CHECK((enableAll(1, id) == CUDA_SUCCESS));
  CHECK((enableAll(0, id) == CUDA_SUCCESS));
  CHECK((enableAll(1, 0) == CUDA_ERROR_INVALID_VALUE));
  CHECK((enableAll(1, 2) == CUDA_ERROR_INVALID_VALUE));
  CHECK((enableDomain(1, id, 0) == CUDA_SUCCESS));
  CHECK((enableDomain(1, id, 32) == CUDA_SUCCESS));
  CHECK((enableDomain(1, id, 33) == CUDA_ERROR_INVALID_VALUE));
  CHECK((enableDomain(1, 2, 2) == CUDA_ERROR_INVALID_VALUE));
  CHECK((enableCallback(1, id, 2, 0) == CUDA_SUCCESS));
  CHECK((enableCallback(1, id, 2, 104) == CUDA_SUCCESS));
  CHECK((enableCallback(1, id, 2, 105) == CUDA_ERROR_INVALID_VALUE));
  CHECK((enableCallback(1, id, 7, 1023) == CUDA_SUCCESS));
  CHECK((enableCallback(1, id, 7, 1024) == CUDA_ERROR_INVALID_VALUE));
  CHECK((enableCallback(1, id, 0, 1) == CUDA_ERROR_INVALID_VALUE));
  CHECK((enableCallback(1, id, 33, 1) == CUDA_ERROR_INVALID_VALUE));
  CHECK((enableCallback(1, 2, 2, 1) == CUDA_ERROR_INVALID_VALUE));
  CHECK((enableFeature(1, id, 22) == CUDA_SUCCESS));
  CHECK((enableFeature(1, id, 23) == CUDA_ERROR_INVALID_VALUE));
  CHECK((enableFeature(1, 2, 0) == CUDA_ERROR_INVALID_HANDLE));

  CHECK((release(2) == CUDA_ERROR_UNKNOWN));
  CHECK((release(id) == CUDA_SUCCESS));
  CHECK((release(id) == CUDA_ERROR_UNKNOWN));
  CHECK((subscribe(&other, callback, nullptr) == CUDA_SUCCESS));
  CHECK((other == 1));
  CHECK((release(other) == CUDA_SUCCESS));
}

TEST_CASE("CUDA tools driver-state table reports the driver version", "[cuda]") {
  const auto *const table = toolsTable(ToolsDriverStateId);
  REQUIRE((table != nullptr));
  CHECK((reinterpret_cast<uintptr_t>(table[0]) == 112));
  const auto version = tableEntry<DriverVersionQuery>(table, 2);
  REQUIRE((version != nullptr));
  int expected = 0;
  REQUIRE((cuDriverGetVersion(&expected) == CUDA_SUCCESS));
  int reported = 0;
  CHECK((version(&reported) == CUDA_SUCCESS));
  CHECK((reported == expected));
  CHECK((version(nullptr) == CUDA_ERROR_INVALID_VALUE));
}

TEST_CASE("CUDA tools driver-state run slot calls back with the current context", "[cuda]") {
  const auto *const table = toolsTable(ToolsDriverStateId);
  REQUIRE((table != nullptr));
  CHECK((reinterpret_cast<uintptr_t>(table[0]) == 112));
  const auto run = tableEntry<ToolsRun>(table, 4);
  REQUIRE((run != nullptr));
  ToolsRunObservation observation;
  ToolsRunRequest request{};
  request.function = observeToolsRun;
  request.argument = &observation;
  request.status = 0x55;
  CHECK((run(nullptr) == CUDA_ERROR_INVALID_VALUE));
  ToolsRunRequest missing{};
  CHECK((run(&missing) == CUDA_ERROR_INVALID_VALUE));

  CUcontext context = nullptr;
  REQUIRE((cuCtxCreate(&context, nullptr, 0, 0) == CUDA_SUCCESS));
  CHECK((run(&request) == CUDA_SUCCESS));
  CHECK((observation.calls == 1));
  CHECK((observation.info.size == 0x18));
  CHECK((observation.info.context == context));
  CHECK((observation.info.reserved == 0));
  CHECK((request.status == 0x55));
  REQUIRE((cuCtxDestroy(context) == CUDA_SUCCESS));
}

TEST_CASE("CUDA tools tables CUPTI looks up are served at the size the driver reports", "[cuda]") {
  using ToolsTableSize = std::pair<std::string_view, size_t>;
  constexpr std::array<ToolsTableSize, 36> expected{{
      {"2c8e0ad80710ab4e90dd54719fe5f74b", 96},  {"d7bd8af8e7acca4abdbcfb1f1b3fcf33", 112}, {"7cb63f9b9ca48e42a484beb66e6aed7f", 560},
      {"21318c60971432488ca641ff7324c8f2", 768}, {"fc58ac1a057c444fa97772ee9aefdb89", 56},  {"b10541e1f7c7c74a9f64f223be99f1e2", 272},
      {"c98d1afaf2275b42b12dcd81b5f21ec1", 40},  {"c2db37e3c4754247b3a8a079f37202ec", 744}, {"b127870b49d29b44a1c1a118e26f1c14", 48},
      {"b9fd53270628c24f87a9988cddf73089", 88},  {"35770f1b092e0348a48e056fc423968d", 80},  {"ff9ab8ee28d40e4cb3b7fd724a6277a8", 16},
      {"bfdb432dbf3c5a4a945eb34029e81e75", 488}, {"a6b1ff99ecc4c94f92f91928663d5585", 256}, {"127d4831ee3357449742b8447b7a2364", 88},
      {"c83963f617f7e241a5749143314dff9f", 96},  {"6fbd4b79aa8c8e46baf3c103b6d18e3c", 48},  {"1406180d72d6cb47ab6eb963e51bf4c6", 360},
      {"06ac4921c9a9e61180f576304dec7eb7", 288}, {"a259cffb1846e8488957bfc72ceb58b8", 160}, {"fe605bbf8542924ca91268bee10b29e7", 56},
      {"cc529e945c4e9d46837c9625868334e4", 184}, {"12458358a7c21e592177ef3556188709", 32},  {"a5987389dd8f6b4bbddbc24bf650aa18", 7016},

      {"41ae717da9eba64f999be3c27f3c140f", 16},  {"36b889606158f543bbd9ac633878b6b1", 40},  {"bd91fa4d185f964a8b17f3805601413f", 272},
      {"7a7125c2f716ea118d71362b9e155667", 296}, {"32683f42be9652459950838b1c3de155", 184}, {"dccdd90889660045bb1f77598aca1133", 152},
      {"5b618f5a1202684781e4a7765ac9f359", 144}, {"f6d3f4d92de380409f0c058cf1b2906b", 24},  {"0e7af9b33460794470edf020433b7ef9", 48},
      {"e9ff28a90c61b4499c09e567201b05f0", 24},  {"1ad5eede0290c64eba8100f425b38aa5", 40},  {"e01b33cec3b7ce4dbe3caba233b1a9cc", 32},
  }};
  for (const auto &[hex, bytes] : expected) {
    std::array<unsigned char, 16> id{};
    for (size_t i = 0; i < id.size(); ++i)
      id[i] = static_cast<unsigned char>(std::stoi(std::string(hex.substr(2 * i, 2)), nullptr, 16));
    INFO(hex);
    const auto *const table = toolsTable(id);
    REQUIRE((table != nullptr));
    CHECK((reinterpret_cast<uintptr_t>(table[0]) == bytes));
    for (size_t slot = 1; slot < bytes / sizeof(void *); ++slot)
      CHECK((table[slot] != nullptr));
  }
}

TEST_CASE("CUDA tools interface table offers the version 4 interface only", "[cuda]") {
  const auto *const table = toolsTable(ToolsInterfaceId);
  REQUIRE((table != nullptr));
  CHECK((reinterpret_cast<uintptr_t>(table[0]) == 16));
  const auto interfaceOfKind = tableEntry<ToolsInterfaceQuery>(table, 1);
  REQUIRE((interfaceOfKind != nullptr));
  for (int kind = 0; kind <= 8; ++kind)
    CHECK(((interfaceOfKind(kind) != nullptr) == (kind == 4)));
}

TEST_CASE("CUDA tools tables refuse the slots they do not implement", "[cuda]") {
  using Refused = CUresult (*)();
  constexpr std::array<unsigned char, 16> placeholder{0xfc, 0x58, 0xac, 0x1a, 0x05, 0x7c, 0x44, 0x4f,
                                                      0xa9, 0x77, 0x72, 0xee, 0x9a, 0xef, 0xdb, 0x89};
  const auto *const unserved = toolsTable(placeholder);
  REQUIRE((unserved != nullptr));
  CHECK((reinterpret_cast<uintptr_t>(unserved[0]) == 56));
  for (size_t slot = 1; slot < 7; ++slot)
    CHECK((tableEntry<Refused>(unserved, slot)() == CUDA_ERROR_NOT_SUPPORTED));
  const auto *const partial = toolsTable(ToolsSubscriberId);
  REQUIRE((partial != nullptr));
  CHECK((reinterpret_cast<uintptr_t>(partial[0]) == 96));
  CHECK((tableEntry<Refused>(partial, 3)() == CUDA_ERROR_NOT_SUPPORTED));
  CHECK((tableEntry<Refused>(partial, 5)() == CUDA_ERROR_NOT_SUPPORTED));
}
